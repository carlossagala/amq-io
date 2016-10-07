package com.redhat.amq.standalone;

import com.redhat.amq.standalone.util.PropertiesUtil;

import javax.jms.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class ProducerMain {

    public static void main(String[] args) throws JMSException {
        Properties properties;
        MessageSender sender = null;

        try {
            if (args.length > 0)
                properties = PropertiesUtil.loadPropertiesFromFile(args[0]);
            else
                properties = PropertiesUtil.loadPropertiesFromResources("producer.properties");

            String trustStore = properties.getProperty("trustStore", "");
            String trustStorePassword = properties.getProperty("trustStorePassword", "");
            String user = properties.getProperty("user", "");
            String password = properties.getProperty("password", "");
            String brokerUrl = properties.getProperty("brokerUrl", "");
            String queue = properties.getProperty("queue", "");
            Integer messageCount = Integer.parseInt(properties.getProperty("messageCount", String.valueOf(0)));

            sender = new MessageSender(brokerUrl, queue, user, password, trustStore, trustStorePassword);

            for (int i = 1; i <= messageCount; i++) {
                Date now = new Date();
                sender.sendMessage(now.toString());

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
