package com.redhat.amq.standalone.producer;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Producer {

    public static void main(String[] args) throws JMSException {
        Producer producer = new Producer();
        ActiveMQConnectionFactory connectionFactory = null;
        Connection connection = null;

        try {
            Properties properties = producer.loadProperties("producer.properties");
            String trustStorePassword = properties.getProperty("trustStorePassword");

            if (trustStorePassword != null && !trustStorePassword.isEmpty()) {
                // SSL

                connectionFactory = new ActiveMQSslConnectionFactory(properties.getProperty("brokerUrl"));

                ((ActiveMQSslConnectionFactory)connectionFactory).setTrustStore(properties.getProperty("trustStore"));
                ((ActiveMQSslConnectionFactory)connectionFactory).setTrustStorePassword((properties.getProperty("trustStorePassword"))) ;
            } else {
                connectionFactory = new ActiveMQConnectionFactory(properties.getProperty("brokerUrl"));
            }

            connection = connectionFactory.createConnection(properties.getProperty("user"), properties.getProperty("password"));
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = new ActiveMQQueue(properties.getProperty("queue"));

            MessageProducer messageProducer = session.createProducer(destination);
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            Integer messageCount = Integer.parseInt(properties.getProperty("messageCount"));
            
            for (int i = 0; i < messageCount; i++) {
                TextMessage msg = session.createTextMessage("Test!");
                msg.setIntProperty("id", i);
                messageProducer.send(msg);

                if( (i % 1000) == 0) {
                    System.out.println(String.format("Sent %d messages", i));
                }
            }

            messageProducer.send(session.createTextMessage("END"));
            connection.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.close();
        }
    }

    public Properties loadProperties(String filename) throws IOException {
        InputStream is = null;

        try {
            is = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            is = this.getClass().getClassLoader().getResourceAsStream("producer.properties");
        }

        Properties properties = new Properties();
        properties.load(is);

        return properties;
    }
}
