import random
import string
import names
import tqdm

import graphhopper_api_handler
import vehicle_api_handler

from datetime import datetime, date, timezone
from time import sleep

CURRENT_YEAR = date.today().year


def __generate_drivers_for_vehicle(vehicle, api_handler):
    vehicle['driverIds'] = []
    vehicle['activeDriverId'] = None
    if random.randint(0, 9) != 0:
        return
    num_drivers = random.randint(1, 5)
    for _ in range(num_drivers):
        new_driver_id = api_handler.post_new_driver({
            'name': names.get_full_name(),
            'salaryUsd': random.randint(1000, 9000),
            'enterpriseId': vehicle['enterpriseId'],
            'vehicleId': None
        })
        vehicle['driverIds'].append(new_driver_id)
    vehicle['activeDriverId'] = random.choice(vehicle['driverIds'])


def __generate_vehicle(models, enterprise_id, api_handler):
    vehicle = {
        'modelId': random.choice(models),
        'enterpriseId': enterprise_id,
        'vin': ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(17)),
        'priceUsd': random.randint(1000, 10000),
        'manufactureYear': random.randint(2010, CURRENT_YEAR),
        'kmDistanceTravelled': random.randint(0, 300000)
    }
    __generate_drivers_for_vehicle(vehicle, api_handler)
    return vehicle


def generate_vehicles(enterprise_ids, num_vehicles):
    api_handler = vehicle_api_handler.CurlHandler()
    models = api_handler.get_vehicle_model_ids()
    for enterprise_id in tqdm.tqdm(enterprise_ids):
        for _ in tqdm.tqdm(range(num_vehicles)):
            vehicle = __generate_vehicle(models, enterprise_id, api_handler)
            api_handler.post_new_vehicle(vehicle)


def generate_track(vehicle_id, start_point, finish_point):
    api_handler = vehicle_api_handler.CurlHandler()
    track = graphhopper_api_handler.get_gps_route(start_point, finish_point)
    for point in tqdm.tqdm(track):

        point_payload = {
            "vehicleId": vehicle_id,
            "point": "{},{}".format(point[1], point[0]),
            "timestamp": datetime.now(timezone.utc).isoformat()
        }
        api_handler.post_new_gps_point(point_payload)
        # sleep(1)
