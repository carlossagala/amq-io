package com.redhat.amq.standalone;

import com.redhat.amq.standalone.util.PropertiesUtil;

import javax.jms.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

public class ConsumerMain {

    public static void main(String[] args) throws JMSException {
        Properties properties;
        Properties jmsProperties;

        MessageReceiver receiver = null;

        try {
            if (args.length > 0)
                properties = PropertiesUtil.loadPropertiesFromFile(args[0]);
            else
                properties = PropertiesUtil.loadPropertiesFromResources("consumer.properties");

            String trustStore = properties.getProperty("trustStore", "");
            String trustStorePassword = properties.getProperty("trustStorePassword", "");
            String user = properties.getProperty("user", "");
            String password = properties.getProperty("password", "");
            String brokerUrl = properties.getProperty("brokerUrl", "");
            String queue = properties.getProperty("queue", "");
            String jmsCustomPropertiesPath = properties.getProperty("jmsCustomProperties", "");

            if (jmsCustomPropertiesPath.isEmpty())
                jmsProperties = PropertiesUtil.loadPropertiesFromResources("jms.properties");
            else
                jmsProperties = PropertiesUtil.loadPropertiesFromFile(jmsCustomPropertiesPath);

            receiver = new MessageReceiver(brokerUrl, queue, user, password, trustStore, trustStorePassword);

            while (true) {
                TextMessage message = receiver.receiveMessage();

                if (message != null) {
                    if (message.getText().equals("END"))
                        break;
                    else {
                        System.out.println("--- Message received");
                        System.out.println("Message content: " + message.getText());

                        // Print all the custom properties
                        Enumeration e = jmsProperties.keys();

                        while (e.hasMoreElements()) {
                            String key = (String) e.nextElement();
                            System.out.println("Message property: " + key + ": " + message.getObjectProperty(key));
                        }

                        System.out.println("--- End");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (receiver != null)
                receiver.close();
        }
    }
}
