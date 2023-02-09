import os
import requests


def get_gps_route(start, finish):
    url = "https://graphhopper.com/api/1/route"
    query = {
        "point": [start, finish],
        "key": os.environ.get('GRAPHHOPPER_API_KEY'),
        "points_encoded": False,
    }
    round_trip = True
    if round_trip:
        query["algorithm"] = "round_trip"
        query["round_trip.distance"] = "3000"
        query["ch.disable"] = True
        query["point"].pop()
    response = requests.get(url, params=query)
    return response.json()['paths'][0]['points']['coordinates']
