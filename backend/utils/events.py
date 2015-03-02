from google.appengine.ext import ndb

from models import Event


def get_key(slug):
    return ndb.Key("Event", slug)

def get_data():
    """ 
    Gets all events from the website's REST service. 
    Should throw out old or preexsiting events
    Returns entities of new events
    """
    return []



def convert_to_entity(json):
    event = Event()
    return event