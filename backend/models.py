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
    _message_fields_schema = ("entityKey", "id", "title", "time_added")
    id = ndb.IntegerProperty(indexed=False)
    title = ndb.StringProperty(indexed=False)
    time_added = ndb.DateTimeProperty()

    def to_html(self):
        return "<td>" + self.title + "</td>"

class Post(EndpointsModel):
    """
    Generated from: http://shilohranch.com/api/get_posts/
    OR
    http://shilohranch.com/api/get_category_posts/?id=21 (where id is the id from Category)
    """
    _message_fields_schema = ("entityKey", "title", "date", "content", "category", "time_added")
    title = ndb.StringProperty(indexed=False)
    date = ndb.DateTimeProperty(indexed=False)
    content = ndb.TextProperty(indexed=False)
    category = ndb.KeyProperty(kind=Category)
    time_added = ndb.DateTimeProperty()

    def to_html(self):
        return "<td>" + self.title + "</td><td>" + str(self.date) + "</td>"

class Sermon(EndpointsModel):
    """
    Generated from: http://shilohranch.com/api/get_posts/?post_type=sermons
    """
    _message_fields_schema = ("entityKey", "title", "date", "audio_link", "time_added")
    title = ndb.StringProperty(indexed=False)
    date = ndb.DateProperty(indexed=False)
    audio_link = ndb.StringProperty(indexed=False)
    time_added = ndb.DateTimeProperty()

    def to_html(self):
        return "<td>" + self.title + "</td><td>" + str(self.date) + "</td>"

class Event(EndpointsModel):
    """
    Generated from: http://shilohranch.com/api/get_posts/?post_type=events
    """
    _message_fields_schema = ("entityKey", "title", "date_published", "content", "excerpt", "location", "time", "time_added")
    title = ndb.StringProperty(indexed=False)
    date_published = ndb.DateTimeProperty(indexed=False)
    content = ndb.TextProperty(indexed=False)
    excerpt = ndb.TextProperty(indexed=False)
    attachment = ndb.StringProperty(indexed=False)
    location = ndb.StringProperty(indexed=False)
    time = ndb.StringProperty(indexed=False)
    time_added = ndb.DateTimeProperty()

    def to_html(self):
        return "<td>" + self.title + "</td><td>" + self.location + "</td><td>" + self.time + "</td>"

class Deletion(EndpointsModel):
    """
    This entity should be created from the front end of this app.
    """
    _message_fields_schema = ("entityKey", "deletion_key", "kind", "time_added")
    deletion_key = ndb.KeyProperty(indexed=False)
    kind = ndb.StringProperty(indexed=False, choices=['Event', 'Category', 'Post', 'Sermon'])
    time_added = ndb.DateTimeProperty()

class LastUpdate(ndb.Model):
    """
    There will be 4 entities of this model (Post, Sermon, Event, Category). And will update the last_touch time whenever one is updated,
    So all an app has todo, is check to see if it's local copy of the last time something was updated matches this
    value. If it's value is less than this one, it needs to query the other API methods to get updates on it's information.
    NOTE:
    These values should be memcached so that they do not kill our datastore limits.
    """
    last_touch = ndb.DateTimeProperty(indexed=False)

class Update(EndpointsModel):
    """
    This is what the LastUpdate will parcel to the apps to tell them if they need to update anything.
    """
    _message_fields_schema = ("update_posts", "update_sermons", "update_events", "update_categories", "update_deletions")
    update_posts = ndb.BooleanProperty()
    update_sermons = ndb.BooleanProperty()
    update_events = ndb.BooleanProperty()
    update_categories = ndb.BooleanProperty()
    update_deletions = ndb.BooleanProperty()
