package com.redhat.amq.standalone;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;
import java.io.*;
import java.util.Properties;

public class Consumer {

    public static void main(String[] args) throws JMSException {
        Consumer consumer = new Consumer();
        ActiveMQConnectionFactory connectionFactory = null;
        Connection connection = null;

        try {
            Properties properties = consumer.loadProperties(args[0]);
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

            MessageConsumer messageConsumer = session.createConsumer(destination);

            while (true) {
                Message message = messageConsumer.receive();

                if (message instanceof TextMessage) {
                    String body = ((TextMessage) message).getText();

                    if ("END".equals(body))
                        break;

                    Integer id = message.getIntProperty("id");
                    System.out.println(String.format("ID: %d - Datetime: %s", id, body));
                }
            }
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
            is = this.getClass().getClassLoader().getResourceAsStream("consumer.properties");
        }

        Properties properties = new Properties();
        properties.load(is);

        return properties;
    }
}
