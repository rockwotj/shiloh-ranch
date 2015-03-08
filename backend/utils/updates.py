import logging

from google.appengine.api import memcache
from google.appengine.ext import ndb

from models import Update, LastUpdate
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
            logging.debug("Creating LastUpdate for: " + key_name)
            value = LastUpdate(key=__update_key(key_name))
            value.last_touch = datetime.utcfromtimestamp(0)
            value.put()
        client.add(key_name, value)
    return value

def get_last_post_time():
    return __get_from_memcache('last_post').last_touch

def get_last_sermon_time():
    return __get_from_memcache('last_sermon').last_touch

def get_last_event_time():
    return __get_from_memcache('last_event').last_touch

def get_last_category_time():
    return __get_from_memcache('last_category').last_touch

def get_last_delete_time():
    return __get_from_memcache('last_delete').last_touch

def __update(key_name, dt):
    value = __get_from_memcache(key_name)
    value.last_touch = dt if dt > value.last_touch else value.last_touch
    value.put()
    memcache.Client().set(key_name, value)

def set_last_post_time(dt):
    return __update('last_post', dt)

def set_last_sermon_time(dt):
    return __update('last_sermon', dt)

def set_last_event_time(dt):
    return __update('last_event', dt)

def set_last_category_time(dt):
    return __update('last_category', dt)

def set_last_delete_time(dt):
    return __update('last_delete', dt)

