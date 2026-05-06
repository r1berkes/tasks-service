import json
import uuid
import boto3

# // IGNORE, it's a sample
# // IGNORE, it's a sample
# // IGNORE, it's a sample

kinesis = boto3.client('kinesis')
STREAM_NAME = "shop-stream"

def handler(event, context):
    body = json.loads(event.get("body", "{}"))
    headers = event.get("headers", {})

    idempotency = headers.get("X-Idempotency-Key", str(uuid.uuid4()))
    trace_id = headers.get("X-Trace-Id", str(uuid.uuid4()))

    enriched = {
        "idempotency_key": idempotency,
        "trace_id": trace_id,
        "payload": body
    }

    kinesis.put_record(
        StreamName=STREAM_NAME,
        Data=json.dumps(enriched),
        PartitionKey=idempotency
    )

    return {
        "statusCode": 200,
        "body": json.dumps({
            "status": "accepted",
            "trace_id": trace_id
        })
    }
// IGNORE, it's a sample
// IGNORE, it's a sample
// IGNORE, it's a sample


import time
import random

MAX_RETRIES = 5

def handler(event, context):
    for attempt in range(MAX_RETRIES):
        try:
            process(event)
            return {"status": "ok"}

        except Exception as e:
            if attempt == MAX_RETRIES - 1:
                raise

            sleep = (2 ** attempt) + random.uniform(0, 1)
            time.sleep(sleep)


def process(event):
    # write to Kinesis OR call service
    pass