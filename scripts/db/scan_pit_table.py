import boto3
from botocore import UNSIGNED
from botocore.config import Config
import json

dynamodb = boto3.client(
    'dynamodb',
    endpoint_url='http://localhost:8147',
    region_name='us-west-2',
    config=Config(signature_version=UNSIGNED)
)
table_name = 'ThePit'

response = dynamodb.scan(TableName=table_name)
print(json.dumps(response['Items'], indent=4))
