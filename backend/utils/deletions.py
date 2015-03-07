from datetime import datetime

from google.appengine.ext import ndb

from models import Deletion
from utils import updates


def get_key(slug):
    return ndb.Key("Deletion", slug)

def delete_entity(key):
    slug = key.string_id()
    kind = key.kind()
    key.delete()
    deletion_key = get_key(slug)
    deletion = Deletion(key=deletion_key)
    deletion.time_added = datetime.utcnow()
    deletion.deletion_key = key
    deletion.kind = kind
    deletion.put()
    updates.set_last_delete_time(deletion.time_added)
