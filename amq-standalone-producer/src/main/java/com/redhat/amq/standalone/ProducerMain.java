package com.redhat.amq.standalone;

import com.redhat.amq.standalone.util.FileMessageUtil;
import com.redhat.amq.standalone.util.PropertiesUtil;

import javax.jms.*;
import java.io.IOException;
import java.util.Properties;

public class ProducerMain {

    public static void main(String[] args) throws JMSException {
        Properties properties;
        Properties jmsHeaders;

        MessageSender sender = null;

        try {
            if (args.length > 0)
                properties = PropertiesUtil.loadPropertiesFromFile(args[0]);
            else
                properties = PropertiesUtil.loadPropertiesFromResources("producer.properties");

            // Get the properties
            String trustStore = properties.getProperty("trustStore", "");
            String trustStorePassword = properties.getProperty("trustStorePassword", "");
            String user = properties.getProperty("user", "");
            String password = properties.getProperty("password", "");
            String brokerUrl = properties.getProperty("brokerUrl", "");
            String queue = properties.getProperty("queue", "");
            Integer messageCount = Integer.parseInt(properties.getProperty("messageCount", String.valueOf(0)));
            String message = properties.getProperty("message", "");
            String messageFilePath = properties.getProperty("messageFilePath", "");
            String jmsCustomProperties = properties.getProperty("jmsCustomProperties", "");

            if (jmsCustomProperties.isEmpty())
                jmsHeaders = PropertiesUtil.loadPropertiesFromResources("jms.properties");
            else
                jmsHeaders = PropertiesUtil.loadPropertiesFromFile(jmsCustomProperties);

            if (!messageFilePath.isEmpty())
                message = FileMessageUtil.getMessageFromFile(messageFilePath);

            sender = new MessageSender(brokerUrl, queue, user, password, trustStore, trustStorePassword);

            for (int i = 1; i <= messageCount; i++) {
                sender.sendMessage(message, jmsHeaders);

                Thread.sleep(1000);
            }

            sender.sendMessage("END");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sender != null)
                sender.close();
        }
    }
}
