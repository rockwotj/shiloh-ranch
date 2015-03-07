from google.appengine.ext import ndb

from models import Post, POST_URL
from google.appengine.api import urlfetch
from datetime import datetime
from utils import categories, updates
import json
import logging


def get_key(slug):
    return ndb.Key("Post", slug)

def get_data():
    """ 
    Gets all posts from the website's REST service. 
    Should throw out old or preexsiting posts
    Returns entities of new posts
    """
    result = urlfetch.fetch(POST_URL)
    if result.status_code != 200:
        logging.error("Could not pull data from wordpress")
        return []
    posts = json.loads(result.content)['posts']
    entities = []
    for post in posts:
        entity = convert_to_entity(post)
        if entity:
            entities.append(entity)
    last_post_time = updates.get_last_post_time()
    new_entity_filter = lambda p: p != None and p.time_added > last_post_time
    entities = filter(new_entity_filter, entities)
    if len(entities) > 0:
        updates.set_last_post_time(entities[0].time_added)
    ndb.put_multi(entities)
    return entities


def convert_to_entity(json):
    if json['status'] != "publish" or json['slug'] == 'placeholder':
        return None
    post = Post(key=get_key(json['slug']))
    post.title = json['title']
    post.content = json['content'].replace("\n", "")
    date = json['modified']
    post.time_added = datetime.strptime(date, '%Y-%m-%d %H:%M:%S')
    if len(json['categories']) > 0:
        category = json['categories'][0]
        if category['parent'] != 8:
            return None
        key = categories.get_key(category['slug'])
        post.category = key
    else:
        return None
    date = json['date']
    post.date = datetime.strptime(date, '%Y-%m-%d %H:%M:%S')
    return post
