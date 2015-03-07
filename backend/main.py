#!/usr/bin/env python

import os

import jinja2
import webapp2
from utils import sermons, posts, categories, events, deletions
from google.appengine.ext import ndb
import models

jinja_env = jinja2.Environment(
  loader=jinja2.FileSystemLoader(os.path.dirname(__file__)))

class MainHandler(webapp2.RequestHandler):
    def get(self):
        template = jinja_env.get_template("templates/index.html")
        self.response.out.write(template.render())

class DeletionHandler(webapp2.RequestHandler):
    def get(self):
        entity_kind = self.request.get('kind')
        kind = None
        if entity_kind == 'Event':
            kind = models.Event
        elif entity_kind == 'Sermon':
            kind = models.Sermon
        elif entity_kind == 'Category':
            kind = models.Category
        elif entity_kind == 'Post':
            kind = models.Post
        else:
            self.response.set_status(404)
            self.response.write('<h1>ERROR 404</h1> We could not find that resource. Contact the developer if this is a problem!')
            return
        q = kind.query().order(-kind.time_added)
        template = jinja_env.get_template("templates/delete.html")
        values = {"kind":entity_kind, "entities":q}
        self.response.out.write(template.render(values))

    def post(self):
        urlsafe = self.request.get('urlsafe')
        key = ndb.Key(urlsafe=urlsafe)
        deletions.delete_entity(key)
        self.redirect(self.request.referer)

class PullHandler(webapp2.RequestHandler):

    def post(self):
        entity_kind = self.request.get('kind')
        if entity_kind == 'Event':
            events.get_data()
        elif entity_kind == 'Sermon':
            sermons.get_data()
        elif entity_kind == 'Category':
            categories.get_data()
        elif entity_kind == 'Post':
            posts.get_data()
        else:
            self.response.set_status(404)
            self.response.write('<h1>ERROR 404</h1> We could not find that resource. Contact the developer if this is a problem!')
            return
        self.redirect(self.request.referer)

app = webapp2.WSGIApplication([
    ('/', MainHandler), ('/delete', DeletionHandler), ('/pull', PullHandler)
], debug=True)
