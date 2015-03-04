import logging

from google.appengine.api import memcache
from google.appengine.ext import ndb

from models import Update
import models
from datetime import datetime


def __update_key(key_name):
    return ndb.Key("LastUpdate", key_name)

def __get_from_memcache(key_name):
    client = memcache.Client()
    value = client.get(key_name)
    if value is None:
        logging.debug("Cache Miss for " + key_name)
        value = __update_key(key_name).get()
        if value is None:
            value = models.LastUpdate()
            value.put()
        client.add(key_name, value)
    return value

def __get_last_post_time():
    return __get_from_memcache('last_post')

def __get_last_sermon_time():
    return __get_from_memcache('last_sermon')

def __get_last_event_time():
    return __get_from_memcache('last_event')

def __get_last_category_time():
    return __get_from_memcache('last_category')

def __update(key_name):
    value = __get_from_memcache(key_name)
    # TODO: check compatable data types
    value.last_touch = datetime.utcnow()
    value.put()
    memcache.Client().set(key_name, value)

def set_last_post_time():
    return __update('last_post')

def set_last_sermon_time():
    return __update('last_sermon')

def set_last_event_time():
    return __update('last_event')

def set_last_category_time():
    return __update('last_category')

def get_needs_update(last_sync_time):
    update = Update()
    update.update_posts = True
    update.update_events = True
    update.update_posts = True
    update.update_sermons = True
    return update
