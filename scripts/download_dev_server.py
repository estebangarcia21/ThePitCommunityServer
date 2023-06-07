import os
import shutil

import boto3
from botocore import UNSIGNED
from botocore.config import Config

PROJECT_ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..'))


def download_s3_folder(bucket_name, s3_folder, local_dir=None):
    s3 = boto3.client("s3", config=Config(signature_version=UNSIGNED))

    """
    Download the contents of a folder directory
    Args:
        bucket_name: the name of the s3 bucket
        s3_folder: the folder path in the s3 bucket
        local_dir: a relative or absolute directory path in the local file system
    """
    response = s3.list_objects_v2(Bucket=bucket_name, Prefix=s3_folder)
    for obj in response['Contents']:
        key = obj['Key']
        target = key if local_dir is None \
            else os.path.join(local_dir, os.path.relpath(key, s3_folder))
        if not os.path.exists(os.path.dirname(target)):
            os.makedirs(os.path.dirname(target))
        if key[-1] == '/':
            continue
        s3.download_file(bucket_name, key, target)


bucket_name = 'the-pit-community'
folder_prefix = '.local-server/'
destination_path = os.path.join(PROJECT_ROOT_DIR, '.local-server')

if os.path.exists(destination_path):
    shutil.rmtree(destination_path)
    os.mkdir(destination_path)
else:
    os.mkdir(destination_path)

print('Cloning development server...')
download_s3_folder(bucket_name, folder_prefix, destination_path)
print('Done!')
