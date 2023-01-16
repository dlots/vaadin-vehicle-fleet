import argparse

from generators import generate_vehicles


def enterprises(arg_value):
    str_args = arg_value.split(',')
    result = []
    for str_arg in str_args:
        try:
            result.append(int(str_arg))
        except ValueError:
            raise argparse.ArgumentTypeError('"{}" is not a valid enterprise id'.format(str_arg))
    return result


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-e', '--enterprise', type=enterprises,
                        help='integer enterprise id or comma separated list of integer enterprise ids')
    parser.add_argument('-n', '--numvehicles', type=int,
                        help='integer number of vehicles to be generated')
    args = parser.parse_args()
    generate_vehicles(args.enterprise, args.numvehicles)
