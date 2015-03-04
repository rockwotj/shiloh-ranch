"""
This module contains all of the information and entities for the data in this server.
"""

from google.appengine.ext import ndb
from endpoints_proto_datastore.ndb.model import EndpointsModel

# add 'count' to change the limit
""" URL to get the category updates under app notifications """
CATEGORY_URL = "http://shilohranch.com/api/get_category_index/?parent=8"
""" URL to get the posts """
POST_URL = "http://shilohranch.com/api/get_posts/"
""" URL to get the posts for a certain category """
POST_CATEGORY_URL = "http://shilohranch.com/api/get_category_posts/?id="
""" URL to get the sermons """
SERMON_URL = "http://shilohranch.com/api/get_posts/?post_type=sermons"
""" URL to get the events """
EVENT_URL = "http://shilohranch.com/api/get_posts/?post_type=events"

class Category(EndpointsModel):
    """
    Generated from: http://shilohranch.com/api/get_category_index/?parent=8
    """
    _message_fields_schema = ("entity_key", "id", "title")
    id = ndb.IntegerProperty('i')
    title = ndb.StringProperty('t', indexed=False)
    time_added = ndb.DateTimeProperty('z', auto_now_add=True)

class Post(EndpointsModel):
    """
    Generated from: http://shilohranch.com/api/get_posts/
    OR
    http://shilohranch.com/api/get_category_posts/?id=21 (where id is the id from Category)
    """
    _message_fields_schema = ("entity_key", "title", "date", "content", "category")
    title = ndb.StringProperty('n')
    date_published = ndb.DateTimeProperty('d')
    content = ndb.TextProperty('c')
    category = ndb.KeyProperty('g', kind=Category)
    time_added = ndb.DateTimeProperty('z', auto_now_add=True)

class Sermon(EndpointsModel):
    """
    Generated from: http://shilohranch.com/api/get_posts/?post_type=sermons
    """
    _message_fields_schema = ("entity_key", "title", "date", "audio_link")
    title = ndb.StringProperty('t')
    date = ndb.DateProperty('d')
    audio_link = ndb.StringProperty('a')
    time_added = ndb.DateTimeProperty('z', auto_now_add=True)

class Event(EndpointsModel):
    """
    Generated from: http://shilohranch.com/api/get_posts/?post_type=events
    """
    _message_fields_schema = ("entity_key", "title", "date_published", "content", "excerpt", "location", "time")
    title = ndb.StringProperty('n')
    date_published = ndb.DateTimeProperty('d')
    content = ndb.TextProperty('c')
    excerpt = ndb.TextProperty('e')
    attachment = ndb.StringProperty('a')
    location = ndb.StringProperty('l')
    time = ndb.StringProperty('t')
    time_added = ndb.DateTimeProperty('z', auto_now_add=True)

class LastUpdate(ndb.Model):
    """
    There will be 4 entities of this model (Post, Sermon, Event, Category). And will update the last_touch time whenever one is updated,
    So all an app has todo, is check to see if it's local copy of the last time something was updated matches this
    value. If it's value is less than this one, it needs to query the other API methods to get updates on it's information.
    NOTE:
    These values should be memcached so that they do not kill our datastore limits.
    """
    # TODO: check auto_now vs auto_now_add
    last_touch = ndb.DateTimeProperty('l', auto_now_add=True)

class Update(EndpointsModel):
    """
    This is what the LastUpdate will parcel to the apps to tell them if they need to update anything.
    """
    _message_fields_schema = ("update_posts", "update_sermons", "update_events", "update_categories")
    update_posts = ndb.BooleanProperty('p')
    update_sermons = ndb.BooleanProperty('s')
    update_events = ndb.BooleanProperty('e')
    update_categories = ndb.BooleanProperty('c')
