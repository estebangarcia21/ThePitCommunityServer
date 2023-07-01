import boto3
import json

dynamodb = boto3.client('dynamodb', endpoint_url='http://localhost:8147', region_name='us-west-2')
table_name = 'ThePit'

response = dynamodb.scan(TableName=table_name)
print(json.dumps(response['Items'], indent=4))
