import argparse

import generators


def enterprises(arg_value):
    str_args = arg_value.split(',')
    result = []
    for str_arg in str_args:
        try:
            result.append(int(str_arg))
        except ValueError:
            raise argparse.ArgumentTypeError('"{}" is not an int'.format(str_arg))
    return result


def point(arg_value):
    str_args = arg_value.split(',')
    result = []
    for str_arg in str_args:
        try:
            result.append(float(str_arg))
        except ValueError:
            raise argparse.ArgumentTypeError('"{}" is not a float'.format(str_arg))
    if len(result) != 2:
        raise argparse.ArgumentTypeError('"{}" is not a gps point'.format(arg_value))
    return arg_value


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-m', '--mode', type=str, help='utility work mode')
    parser.add_argument('-e', '--enterprise', type=enterprises,
                        help='integer enterprise id or comma separated list of integer enterprise ids')
    parser.add_argument('-n', '--numvehicles', type=int, help='integer number of vehicles to be generated')
    parser.add_argument('-v', '--vehicle', type=int, help='integer vehicle id')
    parser.add_argument('-s', '--start', type=point, help='start point coordinates; format: <lat>,<long>')
    parser.add_argument('-f', '--finish', type=point, help='finish point coordinates; format: <lat>,<long>')
    args = parser.parse_args()
    mode = args.mode
    if mode == 'vehicles':
        generators.generate_track(args.enterprise, args.numvehicles)
    elif mode == 'track':
        generators.generate_track(args.vehicle, args.start, args.finish)
