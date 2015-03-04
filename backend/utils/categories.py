from google.appengine.ext import ndb

from models import Category, CATEGORY_URL
from datetime import datetime
from google.appengine.api import urlfetch


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
    json = result.content
    return []

def convert_to_entity(json):
    category_key = get_key(json['slug'])
    if category_key.get() != None:
        return None
    category = Category(category_key)
    category.title = json['title']
    category.id = json['id']
    category.time_added = datetime.utcnow()
    return category
