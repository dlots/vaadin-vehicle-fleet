import datetime
import random
import string
import names

import curl_requests

CURRENT_YEAR = datetime.date.today().year


def generate_drivers_for_vehicle(vehicle, curl_handler):
    vehicle['driverIds'] = []
    vehicle['activeDriverId'] = None
    if random.randint(0, 9) != 0:
        return
    num_drivers = random.randint(1, 5)
    for _ in range(num_drivers):
        new_driver_id = curl_handler.post_new_driver({
            'name': names.get_full_name(),
            'salaryUsd': random.randint(1000, 9000),
            'enterpriseId': vehicle['enterpriseId'],
            'vehicleId': None
        })
        vehicle['driverIds'].append(new_driver_id)
    vehicle['activeDriverId'] = random.choice(vehicle['driverIds'])


def generate_vehicle(models, enterprise_id, curl_handler):
    vehicle = {
        'modelId': random.choice(models),
        'enterpriseId': enterprise_id,
        'vin': ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(17)),
        'priceUsd': random.randint(1000, 10000),
        'manufactureYear': random.randint(2010, CURRENT_YEAR),
        'kmDistanceTravelled': random.randint(0, 300000)
    }
    generate_drivers_for_vehicle(vehicle, curl_handler)
    return vehicle


def generate_vehicles(enterprise_ids, num_vehicles):
    curl_handler = curl_requests.CurlHandler()
    models = curl_handler.get_vehicle_model_ids()
    for enterprise_id in enterprise_ids:
        for _ in range(num_vehicles):
            vehicle = generate_vehicle(models, enterprise_id, curl_handler)
            curl_handler.post_new_vehicle(vehicle)
