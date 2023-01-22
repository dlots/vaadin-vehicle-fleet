import pycurl
from io import BytesIO
import json


class CurlHandler:
    def __init__(self):
        self.__curl = pycurl.Curl()
        self.__application_url = '172.21.224.1:8080'
        self.username = 'admin'
        self.password = 'password'
        self.__curl.setopt(pycurl.USERPWD, '{}:{}'.format(self.username, self.password))
        self.__curl.setopt(pycurl.VERBOSE, 0)

    def __del__(self):
        self.__curl.close()

    def __send_curl_request(self, method, post_data=None):
        if post_data is None:
            self.__curl.setopt(pycurl.HTTPGET, True)
            self.__curl.unsetopt(pycurl.HTTPHEADER)
        else:
            self.__curl.setopt(pycurl.POST, 1)
            self.__curl.setopt(pycurl.POSTFIELDS, json.dumps(post_data))
            self.__curl.setopt(pycurl.HTTPHEADER, ['Content-type: application/json'])
        self.__curl.setopt(pycurl.URL, '{}/api/{}'.format(self.__application_url, method))
        buffer = BytesIO()
        self.__curl.setopt(pycurl.WRITEDATA, buffer)
        self.__curl.perform()
        response_body = buffer.getvalue().decode('utf8')
        return json.loads(response_body)

    def get_vehicle_model_ids(self):
        return [model['id'] for model in self.__send_curl_request('vehicle_models')]

    def post_new_vehicle(self, vehicle):
        return self.__send_curl_request('vehicles', vehicle)['id']

    def post_new_driver(self, driver):
        return self.__send_curl_request('drivers', driver)['id']
