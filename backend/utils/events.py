from google.appengine.ext import ndb

from models import Event, EVENT_URL
from datetime import datetime
from google.appengine.api import urlfetch
from utils import updates
import logging
import re

def get_key(slug):
    return ndb.Key("Event", slug)


def get_data():
    """ 
    Gets all events from the website's REST service. 
    Should throw out old or preexsiting events
    Returns entities of new events
    """
    result = urlfetch.fetch(EVENT_URL)
    if result.status_code != 200:
        logging.error("Could not pull data from wordpress")
        return []
    events = ics_loads(result.content)
    entities = []
    for event in reversed(events):
        entity = convert_to_entity(event)
        if entity:
            entities.append(entity)
    last_event_time = updates.get_last_event_time()
    new_entity_filter = lambda e: e != None and e.time_added > last_event_time
    entities = filter(new_entity_filter, entities)
    if len(entities) > 0:
        updates.set_last_event_time(entities[0].time_added)
    ndb.put_multi(entities)
    return entities


def ics_loads(content):
    return content.split('BEGIN:VEVENT')[1:]

uid_pattern = re.compile('UID:(.*)\r\n')
summary_pattern = re.compile("SUMMARY: (.*)\r\n")
content_pattern = re.compile("X-ALT-DESC;FMTTYPE=text/html:(.*?)\r\n[A-Z]", re.S | re.M)
time_added_pattern = re.compile("LAST-MODIFIED:(.*)\r\n")
location_pattern = re.compile("LOCATION:(.*)\r\n")
start_time_pattern = re.compile("DTSTART:(.*)\r\n")
end_time_pattern = re.compile("DTEND:(.*)\r\n")
repeat_pattern = re.compile("RRULE:FREQ=(.*?);")
until_pattern = re.compile("RRULE:(.*?)UNTIL=(.*?)\r\n")

def find_pattern(pattern, string, group_number=1):
    result = pattern.search(string)
    if result:
        result = result.group(group_number).strip()
    return result

def to_string(obj):
    if obj is not None:
        return str(obj)
    else:
        return ""

def convert_to_entity(vevent):
#     print vevent
    event_key = get_key(find_pattern(uid_pattern, vevent))
    event = Event(key=event_key)
    event.title = find_pattern(summary_pattern, vevent)
    event.content = find_pattern(content_pattern, vevent).replace('\r\n ', '')
    date = find_pattern(time_added_pattern, vevent) # 20150527T131225Z
    event.location = find_pattern(location_pattern, vevent)
    event.time_added = datetime.strptime(date, '%Y%m%dT%H%M%SZ')
    time = find_pattern(start_time_pattern, vevent) # 20150528T190000Z
    event.start_time = datetime.strptime(time, '%Y%m%dT%H%M%SZ')
    time = find_pattern(end_time_pattern, vevent) # 20150528T190000Z
    event.end_time = datetime.strptime(time, '%Y%m%dT%H%M%SZ')
    repeat = find_pattern(repeat_pattern, vevent)
    until = find_pattern(until_pattern, vevent, 2)
    seperator = ""
    if until is not None:
        seperator = ":"
    event.repeat = to_string(repeat) + seperator + to_string(until)
    return event


