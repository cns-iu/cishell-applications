from django.core.urlresolvers import reverse
from datetime import datetime
import functools
import logging

# user should be an actual User object.
def user_is_item_creator(user, item):
    if user == item.creator or user.id == item.creator.id:
        return True
    
    return False

def get_item_url(item, item_view_function_name):
    item_url_reverse_data = {'item_id': item.id, 'slug': item.slug,}
    item_url = reverse(item_view_function_name, kwargs=item_url_reverse_data)
    
    return item_url

def get_specifics_from_item_ids(model, item_ids):
    specifics = model.objects.filter(is_active=True).\
                              filter(id__in=item_ids)
    
    return specifics



def convert_quotes(message_item):
    return message_item.replace('"', "'")

def as_log_message(params):
    return ' '.join(['%s="%s"' % (k, convert_quotes(str(v))) for k, v in params.items()])

UNKNOWN_LOG_VALUE = 'unknown'
def set_if_defined(key, dict1, dict2):
	if key in dict2:
		dict1[key] = dict2[key]
	else:
		dict1[key] = UNKNOWN_LOG_VALUE

def logged_view(view):
    module_logger = logging.getLogger(view.__module__)
	
    def decorated_view(request, *args, **kwargs):
        params = dict()
        
        # Store request and argument information
        params['path'] = request.path
        params['method'] = request.method
        params['user'] = request.user
        params['view_name'] = view.__name__
        
        set_if_defined('REMOTE_ADDR', params, request.META)
        set_if_defined('HTTP_USER_AGENT', params, request.META)
        set_if_defined('HTTP_REFERER', params, request.META)

        for index, arg in enumerate(args):
            params['arg:' + str(index)] = arg
        
        for kwarg, kwval in kwargs.items():
            params['kwarg:' + kwarg] = kwval

        # Process view with timing
        params['request_time'] = datetime.now()
        response = view(request, *args, **kwargs)
        params['response_time'] = datetime.now()
        
        view_timedelta = params['response_time'] - params['request_time']
        params['view_timedelta_microseconds'] = view_timedelta.microseconds

        # Log
        module_logger.info(as_log_message(params))

        return response
    
    return functools.update_wrapper(decorated_view, view)