from google.appengine.ext import ndb

from models import Post, POST_URL
from google.appengine.api import urlfetch
from datetime import datetime
from utils import categories


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
        return []
    json = result.content
    return []



def convert_to_entity(json):
    if json['status'] != "publish" or json['slug'] == 'placeholder':
        return None
    post = Post()
    post.title = json['title']
    post.content = json['content']
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
