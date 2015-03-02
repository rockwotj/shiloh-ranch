from google.appengine.ext import ndb

from models import Post


def get_key(slug):
    return ndb.Key("Post", slug)

def get_data():
    """ 
    Gets all posts from the website's REST service. 
    Should throw out old or preexsiting posts
    Returns entities of new posts
    """
    return []



def convert_to_entity(json):
    post = Post()
    return post