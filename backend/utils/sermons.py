from google.appengine.ext import ndb

from models import Sermon


def get_key(slug):
    return ndb.Key("Sermon", slug)

def get_data():
    """ 
    Gets all sermons from the website's REST service. 
    Should throw out old or preexsiting sermons
    Returns entities of new sermons
    """
    return []

def convert_to_entity(json):
    sermon = Sermon()
    return sermon