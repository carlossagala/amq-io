package com.redhat.amq.standalone.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileMessageUtil {
    public static String getMessageFromFile(String fileName) throws IOException {
        File file = new File(fileName);

        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuffer fileContents = new StringBuffer();
        String line = br.readLine();
        while (line != null) {
            fileContents.append(line);
            line = br.readLine();
        }

        br.close();

        return fileContents.toString();
    }
}
