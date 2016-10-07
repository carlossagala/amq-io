package com.redhat.amq.standalone;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import javax.jms.*;

public class MessageReceiver {

    private Session session;
    MessageConsumer messageConsumer;
    Connection connection;

    public MessageReceiver(String brokerUrl,
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

        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = new ActiveMQQueue(queue);

        messageConsumer = session.createConsumer(destination);
    }

    public MessageReceiver(String brokerUrl,
                           String queue,
                           String user,
                           String password) throws Exception {
        this(brokerUrl, queue, user, password, "", "");
    }

    public TextMessage receiveMessage() throws JMSException {
        Message message = messageConsumer.receive();

        if (message instanceof TextMessage) {
            return (TextMessage) message;
        }

        return null;
    }

    public void close() throws JMSException {
        if (connection != null)
            connection.close();;
    }
}
