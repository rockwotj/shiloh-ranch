from google.appengine.ext import ndb

from models import Sermon, SERMON_URL
from datetime import datetime
from google.appengine.api import urlfetch


def get_key(slug):
    return ndb.Key("Sermon", slug)

def get_data():
    """
    Gets all sermons from the website's REST service. 
    Should throw out old or preexsiting sermons
    Returns entities of new sermons
    """
    result = urlfetch.fetch(SERMON_URL)
    if result.status_code != 200:
        return []
    json = result.content
    return []

def convert_to_entity(json):
    if json['status'] != "publish":
        return None
    sermon_key = get_key(json['slug'])
    sermon = Sermon(sermon_key)
    sermon.title = json['title']
    sermon.audio_link = json['custom_fields']['sermonmp3'][0]
    # Wordpress works in UTC time?
    date = json['date']
    sermon.date = datetime.strptime(date, '%Y-%m-%d %H:%M:%S')
    date = json['modified']
    sermon.time_added = datetime.strptime(date, '%Y-%m-%d %H:%M:%S')
    return sermon
