# Locator

A user locator service built on top of DynamoDB storage.

Hosting location: http://locator-188295432.us-east-2.elb.amazonaws.com/

Example interactions:

User `user1` visits location `McDonalds`
The response payload will contain the visit identifier:

```
curl -X POST -H "Content-Type: application/json" -d '{"userId": "user1","name": "McDonalds"}' http://locator-188295432.us-east-2.elb.amazonaws.com/
{"visitId":"b4250607-d5a9-4722-96ee-8d21c2b64ff1"}
```


The visit identifier can be utilized for retrieving the visit details:

```
curl "http://locator-188295432.us-east-2.elb.amazonaws.com/visit?visitId=b4250607-d5a9-4722-96ee-8d21c2b64ff1"
[{"visitId":"b4250607-d5a9-4722-96ee-8d21c2b64ff1","timestamp":1579661280136,"userId":"user1","name":"McDonalds"}]
```

We can also search for a user's recent activity (last five locations):

```
curl "http://locator-188295432.us-east-2.elb.amazonaws.com/visit?userId=user1&searchString=MCDONALD%E2%80%99S%20LAS%20VEGAS"
[{"visitId":"b4250607-d5a9-4722-96ee-8d21c2b64ff1","timestamp":1579661280136,"userId":"user1","name":"McDonalds"},{"visitId":"367b9a56-4877-4c75-b899-de67f200ee0e","timestamp":1579653340874,"userId":"user1","name":"McDonalds"}]
```  

## Building locally

Required:

- Java 8
- maven 3
- Docker

For running tests and building he docker image:

```
mvn clean install
```

### Notes

An open-api descriptor is included in the client package and it's used to generate a Java client which is leveraged in testing.
The descriptor can also be used to auto-generate documentation and server stubs. 

The included integration test starts the service in-process locally and runs against a dynamo db emulator that's running in docker.
This ensures a high degree of accuracy prior to service deploy.

The fuzzy matching can be done better. In the interest of time an off-the-shelf library was chosen that covers the main bases of text analysis, tokenization and matching.

      