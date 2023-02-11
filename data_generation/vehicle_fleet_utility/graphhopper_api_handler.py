import os
import requests


def get_gps_route(start, finish, seed):
    url = "https://graphhopper.com/api/1/route"
    query = {
        "point": [start, finish],
        "key": os.environ.get('GRAPHHOPPER_API_KEY'),
        "points_encoded": False,
    }
    round_trip = True
    if round_trip:
        query["algorithm"] = "round_trip"
        query["round_trip.distance"] = "5000"
        query["round_trip.seed"] = seed
        query["ch.disable"] = True
        query["point"].pop()
    response = requests.get(url, params=query)
    if response.status_code == 200:
        return response.json()['paths'][0]['points']['coordinates']
    return None
