'''
Created on Mar 4, 2015

The endpoints API for shiloh ranch

@author: rockwotj
'''
import endpoints
from protorpc import remote


@endpoints.api(name="shilohranch", version="v1", description="Shiloh Ranch Mobile App API")
class ShilohRanchApi(remote.Service):
    pass

api = endpoints.api_server([ShilohRanchApi], restricted=False)