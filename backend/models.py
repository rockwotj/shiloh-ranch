"""
This module contains all of the information and entities for the data in this server.
"""

from google.appengine.ext import ndb
from endpoints_proto_datastore.ndb.model import EndpointsModel, \
    EndpointsAliasProperty
import endpoints
from datetime import datetime
from endpoints_proto_datastore import utils

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
EVENT_URL = 'http://shilohranch.com/feed/eo-events'



class Category(EndpointsModel):
    """
    Generated from: http://shilohranch.com/api/get_category_index/?parent=8
    """
    _message_fields_schema = ("entityKey", "id", "title", "time_added")
    id = ndb.IntegerProperty(indexed=False)
    title = ndb.StringProperty(indexed=False)
    time_added = ndb.DateTimeProperty()

    def last_sync_set(self, value):
        try:
            last_sync = utils.DatetimeValueFromString(value)
            if not isinstance(last_sync, datetime):
                raise TypeError('Not a datetime stamp.')
        except TypeError:
            raise endpoints.BadRequestException('Invalid timestamp for lastSync.')
        self._endpoints_query_info._filters.add(Category.time_added > last_sync)

    @EndpointsAliasProperty(name='lastSync', setter=last_sync_set)
    def last_sync(self):
        raise endpoints.BadRequestException('lastSync value should never be accessed.')

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

    def last_sync_set(self, value):
        try:
            last_sync = utils.DatetimeValueFromString(value)
            if not isinstance(last_sync, datetime):
                raise TypeError('Not a datetime stamp.')
        except TypeError:
            raise endpoints.BadRequestException('Invalid timestamp for lastSync.')
        self._endpoints_query_info._filters.add(Post.time_added > last_sync)

    @EndpointsAliasProperty(name='lastSync', setter=last_sync_set)
    def last_sync(self):
        raise endpoints.BadRequestException('lastSync value should never be accessed.')

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

    def last_sync_set(self, value):
        try:
            last_sync = utils.DatetimeValueFromString(value)
            if not isinstance(last_sync, datetime):
                raise TypeError('Not a datetime stamp.')
        except TypeError:
            raise endpoints.BadRequestException('Invalid timestamp for lastSync.')
        self._endpoints_query_info._filters.add(Sermon.time_added > last_sync)

    @EndpointsAliasProperty(name='lastSync', setter=last_sync_set)
    def last_sync(self):
        raise endpoints.BadRequestException('lastSync value should never be accessed.')

class Event(EndpointsModel):
    """
    Generated from: 'http://shilohranch.com/feed/eo-events'
    """
    _message_fields_schema = ("entityKey", "title", "content", "location", "end_time", "start_time", "repeat", "time_added")
    title = ndb.StringProperty(indexed=False)
    content = ndb.TextProperty(indexed=False)
    location = ndb.StringProperty(indexed=False, default="")
    start_time = ndb.DateTimeProperty(indexed=False)
    end_time = ndb.DateTimeProperty(indexed=False)
    repeat = ndb.StringProperty(indexed=False)
    time_added = ndb.DateTimeProperty()

    def to_html(self):
        return "<td>" + self.title + "</td><td>" + str(self.location) + "</td><td>" + str(self.start_time) + "</td>"

    def last_sync_set(self, value):
        try:
            last_sync = utils.DatetimeValueFromString(value)
            if not isinstance(last_sync, datetime):
                raise TypeError('Not a datetime stamp.')
        except TypeError:
            raise endpoints.BadRequestException('Invalid timestamp for lastSync.')
        self._endpoints_query_info._filters.add(Event.time_added > last_sync)

    @EndpointsAliasProperty(name='lastSync', setter=last_sync_set)
    def last_sync(self):
        raise endpoints.BadRequestException('lastSync value should never be accessed.')

class Deletion(EndpointsModel):
    """
    This entity should be created from the front end of this app.
    """
    _message_fields_schema = ("entityKey", "deletion_key", "kind", "time_added")
    deletion_key = ndb.KeyProperty(indexed=False)
    kind = ndb.StringProperty(indexed=False, choices=['Event', 'Category', 'Post', 'Sermon'])
    time_added = ndb.DateTimeProperty()

    def last_sync_set(self, value):
        try:
            last_sync = utils.DatetimeValueFromString(value)
            if not isinstance(last_sync, datetime):
                raise TypeError('Not a datetime stamp.')
        except TypeError:
            raise endpoints.BadRequestException('Invalid timestamp for lastSync.')
        self._endpoints_query_info._filters.add(Deletion.time_added > last_sync)

    @EndpointsAliasProperty(name='lastSync', setter=last_sync_set)
    def last_sync(self):
        raise endpoints.BadRequestException('lastSync value should never be accessed.')

class LastUpdate(ndb.Model):
    """
    There will be 5 entities of this model (Post, Sermon, Event, Category, Deletion). And will update the last_touch time whenever one is updated,
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
    _message_fields_schema = ("needs_update",)
    needs_update = ndb.BooleanProperty()
