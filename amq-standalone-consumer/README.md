# amq-standalone-consumer

This example contains an A-MQ Standalone Java Consumer to be used with any broker running at a given uri.

It allows several configuration flavors:

* SSL connection
* Basic authentication with the broker
* List of custom JMS properties in the message received.

## Properties

### consumer.properties

    # Credentials
    user=admin
    password=admin

    # SSL credentials
    trustStore=../etc/client.ts
    trustStorePassword=client

    # Broker configuration
    brokerUrl=failover:(ssl://192.168.100.149:61617,ssl://192.168.100.191:61617)

    # Queue name
    queue=test

    # Message content (from a file or a simple text)
    messageFilePath=../etc/message
    message=Test!

    # Number of messages to be sent
    messageCount=20

    # File with all custom properties
    jmsCustomProperties=../etc/jms.properties

### jms.properties

    # These properties will be shipped inside the JMS message as JMS custom properties (can be more properties)
    customProperty1 = CustomPropertyValue1
    customProperty2 = CustomPropertyValue2

## Usage

First of all the example must be compilated and built:

    mvn clean install

This example can be used in two different ways:

### Executing the jar file

One of the tasks when the example is built is generate a jar file located in the bin folder in the root directory.

    java -jar ../bin/amq-standalone-consumer-x.x.x.jar ../etc/consumer.properties

### Executing with Maven Exec plugin

    mvn exec:java

Note: this way of executing will use the consumer.properties located in the resources folder.

