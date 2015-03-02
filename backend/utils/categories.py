from google.appengine.ext import ndb

from models import Category


def get_key(slug):
    return ndb.Key("Category", slug)

def get_data():
    """ 
    Gets all categories from the website's REST service. 
    Should throw out old or preexsiting categories
    Returns entities of new categories
    """
    return []



def convert_to_entity(json):
    category = Category()
    return category