import json

import boto3

dynamodb = boto3.client(
    'dynamodb',
    endpoint_url='http://localhost:8147',
    region_name='us-west-2',
    aws_access_key_id='DUMMYIDEXAMPLE',
    aws_secret_access_key='DUMMYIDEXAMPLE'
)
table_name = 'ThePit'

response = dynamodb.scan(TableName=table_name)
print(json.dumps(response['Items'], indent=4))
