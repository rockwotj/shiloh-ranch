from google.appengine.ext import ndb

from models import Event, EVENT_URL
from datetime import datetime
from google.appengine.api import urlfetch


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
        return []
    json = result.content
    return []



def convert_to_entity(json):
    if json['status'] != "publish":
        return None
    event_key = get_key(json['slug'])
    event = Event(event_key)
    event.title = json['title']
    date = json['date']
    event.date_published = datetime.strptime(date, '%Y-%m-%d %H:%M:%S')
    event.content = json['content']
    event.excerpt = json['excerpt']
    custom_fields = json['custom_fields']
    if len(custom_fields['thumbimg']) > 0:
        event.attachment = custom_fields['thumbimg'][0]
    if len(custom_fields['location']) > 0:
        event.location = custom_fields['location'][0]
    if len(custom_fields['time']) > 0:
        event.time = custom_fields['time'][0]
    date = json['modified']
    event.time_added = datetime.strptime(date, '%Y-%m-%d %H:%M:%S')
    return event
