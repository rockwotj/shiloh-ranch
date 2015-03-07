'''
Created on Mar 4, 2015

The endpoints API for shiloh ranch

@author: rockwotj
'''
import endpoints
from protorpc import remote
from models import Category, Deletion, Post, Event, Sermon, Update
from utils import updates
from protorpc.message_types import DateTimeField, DateTimeMessage



@endpoints.api(name="shilohranch", version="v1", description="Shiloh Ranch Mobile App API")
class ShilohRanchApi(remote.Service):

    # Update
    @Update.method(path="update", http_method="GET", name="update", request_message=DateTimeMessage)
    def needs_update(self, request):
        # The 1 is the number it is within the request message. Not sure why it is one indexed?
        return updates.get_needs_update(DateTimeField(1).value_from_message(request))

    # Queries
    @Deletion.query_method(path="deletions", http_method="GET", name="deletions", query_fields=("limit", "pageToken", "lastSync"))
    def sync_deletions(self, query):
        return query.order(-Deletion.time_added)

    @Category.query_method(path="categories", http_method="GET", name="categories", query_fields=("limit", "pageToken", "lastSync"))
    def sync_categories(self, query):
        return query.order(-Category.time_added)

    @Post.query_method(path="posts", http_method="GET", name="posts", query_fields=("limit", "pageToken", "lastSync"))
    def sync_posts(self, query):
        return query.order(-Post.time_added)

    @Event.query_method(path="events", http_method="GET", name="events", query_fields=("limit", "pageToken", "lastSync"))
    def sync_events(self, query):
        return query.order(-Event.time_added)

    @Sermon.query_method(path="sermons", http_method="GET", name="sermons", query_fields=("limit", "pageToken", "lastSync"))
    def sync_sermons(self, query):
        return query.order(-Sermon.time_added)

api = endpoints.api_server([ShilohRanchApi], restricted=False)
