import boto3
from botocore import UNSIGNED
from botocore.config import Config


def create_table():
    table_name = 'ThePit'
    attribute_definitions = [
        {
            'AttributeName': 'playerId',
            'AttributeType': 'S'
        }
    ]
    key_schema = [
        {
            'AttributeName': 'playerId',
            'KeyType': 'HASH'
        }
    ]

    billing_mode = 'PAY_PER_REQUEST'

    dynamodb = boto3.client(
        'dynamodb',
        region_name='us-west-2',
        endpoint_url='http://localhost:8147',
        config=Config(UNSIGNED)
    )

    try:
        dynamodb.delete_table(TableName=table_name)
        waiter = dynamodb.get_waiter('table_not_exists')
        waiter.wait(TableName=table_name)
        print('Table deleted successfully.')

        dynamodb.create_table(
            TableName=table_name,
            AttributeDefinitions=attribute_definitions,
            KeySchema=key_schema,
            BillingMode=billing_mode
        )
        waiter = dynamodb.get_waiter('table_exists')
        waiter.wait(TableName=table_name)
        print('Table created successfully.')
    except dynamodb.exceptions.ResourceNotFoundException:
        print('Table does not exist.')
    except Exception as e:
        print(f'Error creating/deleting table: {str(e)}')


create_table()
