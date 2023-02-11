import random
import string
import names
import tqdm
from dateutil.relativedelta import relativedelta

import graphhopper_api_handler
import vehicle_api_handler

from datetime import datetime, date, timezone

CURRENT_YEAR = date.today().year


def __generate_drivers_for_vehicle(vehicle, api_handler):
    vehicle['driverIds'] = []
    vehicle['activeDriverId'] = None
    # if random.randint(0, 2) != 0:
    #     return
    num_drivers = random.randint(1, 3)
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
    manufacture_year = random.randint(2010, CURRENT_YEAR - 4)
    now = datetime.now(timezone.utc)
    purchase_date_from = now - relativedelta(years=(CURRENT_YEAR - manufacture_year))
    purchase_date_to = now - relativedelta(years=3)
    random_timestamp = random.uniform(purchase_date_from.timestamp(), purchase_date_to.timestamp())
    purchase_date_time = datetime.fromtimestamp(random_timestamp, timezone.utc)
    vehicle = {
        'modelId': random.choice(models),
        'enterpriseId': enterprise_id,
        'vin': ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(17)),
        'priceUsd': random.randint(1000, 10000),
        'manufactureYear': manufacture_year,
        'kmDistanceTravelled': random.randint(0, 300000),
        "purchaseDateTimeUtc": purchase_date_time.isoformat()
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


def generate_ride(vehicle_id, start_time, end_time, api_handler):
    start_time_iso = (start_time - relativedelta(seconds=1)).isoformat()
    end_time_iso = (end_time + relativedelta(seconds=1)).isoformat()
    ride_payload = {
        "vehicleId": vehicle_id,
        "startTime": start_time_iso,
        "endTime": end_time_iso
    }
    api_handler.post_new_ride(ride_payload)


def generate_tracks(vehicle_ids, start_point, finish_point, num_tracks):
    api_handler = vehicle_api_handler.CurlHandler()
    origin_time = datetime.now(timezone.utc) - relativedelta(years=2)
    for vehicle_id in tqdm.tqdm(vehicle_ids):
        day = origin_time
        errors = -1
        for track_i in range(num_tracks):
            point_time = day
            points = []
            track = None
            while track is None:
                track = graphhopper_api_handler.get_gps_route(start_point, finish_point, str(vehicle_id) + str(track_i))
                if track is None:
                    errors += 1
                    print('errors for vehicle: {}', errors)
            for point in track:
                points.append({
                    "vehicleId": vehicle_id,
                    "point": "{},{}".format(point[1], point[0]),
                    "timestamp": point_time.isoformat()
                })
                point_time = point_time + relativedelta(seconds=1)
            api_handler.post_new_gps_point(points)
            generate_ride(vehicle_id, day, point_time, api_handler)
            day = day + relativedelta(days=1)
