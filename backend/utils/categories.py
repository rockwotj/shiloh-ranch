from google.appengine.ext import ndb

from models import Category, CATEGORY_URL
from datetime import datetime
from google.appengine.api import urlfetch
import json
from utils import updates


def get_key(slug):
    return ndb.Key("Category", slug)

def get_data():
    """ 
    Gets all categories from the website's REST service. 
    Should throw out old or preexsiting categories
    Returns entities of new categories
    """
    result = urlfetch.fetch(CATEGORY_URL)
    if result.status_code != 200:
        return []
    categories = json.loads(result.content)['categories']
    entities = []
    for category in categories:
        entity = convert_to_entity(category)
        if entity:
            entities.append(entity)
    new_entity_filter = lambda c: c != None
    entities = filter(new_entity_filter, entities)
    if len(entities) > 0:
        updates.set_last_category_time(datetime.utcnow())
    ndb.put_multi(entities)
    return entities

def sync(last_sync_time):
    pass

def convert_to_entity(json):
    category_key = get_key(json['slug'])
    if category_key.get() != None:
        return None
    category = Category(key=category_key)
    category.title = json['title']
    category.id = json['id']
    category.time_added = datetime.utcnow()
    return category
