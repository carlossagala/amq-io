package com.redhat.amq.standalone;

import javax.jms.*;
import java.io.*;
import java.util.Properties;

public class ConsumerMain {

    public static void main(String[] args) throws JMSException {
        ConsumerMain consumerMain = new ConsumerMain();

        Properties properties = null;
        MessageReceiver receiver = null;

        try {
            if (args.length > 0)
                properties = consumerMain.loadPropertiesFromFile(args[0]);
            else
                properties = consumerMain.loadPropertiesFromResources();

            String trustStore = properties.getProperty("trustStore", "");
            String trustStorePassword = properties.getProperty("trustStorePassword", "");
            String user = properties.getProperty("user", "");
            String password = properties.getProperty("password", "");
            String brokerUrl = properties.getProperty("brokerUrl", "");
            String queue = properties.getProperty("queue", "");

            receiver = new MessageReceiver(brokerUrl, queue, user, password, trustStore, trustStorePassword);

            while (true) {
                String message = receiver.receiveMessage();

                if (message != null) {
                    if (message.equals("END"))
                        break;
                    else
                        System.out.println(message);
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

    public Properties loadPropertiesFromFile(String filename) throws IOException {
        InputStream is = new FileInputStream(filename);

        Properties properties = new Properties();
        properties.load(is);

        return properties;
    }

    public Properties loadPropertiesFromResources() throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("consumer.properties");

        Properties properties = new Properties();
        properties.load(is);

        return properties;
    }
}
