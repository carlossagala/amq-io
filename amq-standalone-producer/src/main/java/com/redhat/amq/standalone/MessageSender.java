package com.redhat.amq.standalone;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;
import java.util.Enumeration;
import java.util.Properties;

public class MessageSender {

    private Session session;
    MessageProducer messageProducer;
    Connection connection;

    public MessageSender(String brokerUrl,
                         String queue,
                         String user,
                         String password,
                         String trustStore,
                         String trustStorePassword) throws Exception {

        ConnectionFactory connectionFactory;

        if (!trustStorePassword.isEmpty() && !trustStorePassword.isEmpty()) {
            // SSL

            connectionFactory = new ActiveMQSslConnectionFactory(brokerUrl);

            ((ActiveMQSslConnectionFactory)connectionFactory).setTrustStore(trustStore);
            ((ActiveMQSslConnectionFactory)connectionFactory).setTrustStorePassword(trustStorePassword);
        } else {
            connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        }

        connection = connectionFactory.createConnection(user, password);
        connection.start();

        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Destination destination = new ActiveMQQueue(queue);

        messageProducer = session.createProducer(destination);
        messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    public MessageSender(String brokerUrl,
                         String queue,
                         String user,
                         String password) throws Exception {
        this(brokerUrl, queue, user, password, "", "");
    }

    public void sendMessage(String message) throws JMSException {
        TextMessage msg = session.createTextMessage(message);
        messageProducer.send(msg);
    }

    public void sendMessage(String message, Properties properties) throws JMSException {
        TextMessage msg = session.createTextMessage(message);
        Enumeration e = properties.keys();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            msg.setObjectProperty(key, properties.getProperty(key));
        }

        messageProducer.send(msg);
    }

    public void close() throws JMSException {
        if (connection != null)
            connection.close();;
    }
}
