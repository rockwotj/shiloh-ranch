from google.appengine.ext import ndb

from models import Sermon
from datetime import datetime
from google.appengine.api import urlfetch
import json
from utils import updates
import models

def get_key(slug):
    return ndb.Key("Sermon", slug)

def get_data():
    """
    Gets all sermons from the website's REST service. 
    Should throw out old or pre-exsiting sermons
    Returns entities of new sermons
    """
    result = urlfetch.fetch(models.SERMON_URL)
    if result.status_code != 200:
        return []
    posts = json.loads(result.content)['posts']
    entities = []
    for post in posts:
        entity = convert_to_entity(post)
        if entity:
            entities.append(entity)
    last_sermon_time = updates.get_last_sermon_time()
    new_entity_filter = lambda s: s != None and s.time_added > last_sermon_time
    entities = filter(new_entity_filter, entities)
    if len(entities) > 0:
        updates.set_last_sermon_time(entities[0].time_added)
    ndb.put_multi(entities)
    return entities

def sync(last_sync_time):
    pass

def convert_to_entity(json):
    if json['status'] != "publish":
        return None
    sermon_key = get_key(json['slug'])
    sermon = Sermon(key=sermon_key)
    sermon.title = json['title']
    sermon.audio_link = json['custom_fields']['sermonmp3'][0]
    # Wordpress works in UTC time?
    date = json['date']
    sermon.date = datetime.strptime(date, '%Y-%m-%d %H:%M:%S')
    date = json['modified']
    sermon.time_added = datetime.strptime(date, '%Y-%m-%d %H:%M:%S')
    return sermon
