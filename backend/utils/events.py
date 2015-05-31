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
time_pattern = re.compile("DTSTART:(.*)\r\n")
repeat_pattern = re.compile("RRULE:FREQ=(.*?);")

def find_pattern(pattern, string):
    result = pattern.search(string)
    if result:
        result = result.group(1).strip()
    return result

def convert_to_entity(vevent):
#     print vevent
    event_key = get_key(find_pattern(uid_pattern, vevent))
    event = Event(key=event_key)
    event.title = find_pattern(summary_pattern, vevent)
    event.content = find_pattern(content_pattern, vevent).replace('\r\n ', '')
    date = find_pattern(time_added_pattern, vevent) # 20150527T131225Z
    event.location = find_pattern(location_pattern, vevent)
    time = find_pattern(time_pattern, vevent) # 20150528T190000Z
    event.time_added = datetime.strptime(date, '%Y%m%dT%H%M%SZ')
    event.time = datetime.strptime(time, '%Y%m%dT%H%M%SZ')
    event.repeat = find_pattern(repeat_pattern, vevent)
    return event


