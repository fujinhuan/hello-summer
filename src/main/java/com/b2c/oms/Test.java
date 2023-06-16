package com.b2c.oms;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Test {

    public static void main(String[] args) throws Exception {
        String imageUrl = "https://rescdn.crashclub.app/uploads/images/background/20220811/85d989f76cc481707f738e5409bc3bc2.png";
        String destinationFile = "image.jpg";
        URL url = new URL(imageUrl);
        URLConnection myconn = url.openConnection();

        myconn.setRequestProperty("User-Agent","User-Agent: Mozilla/5.0 (Windows NT 6.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");

//        OutputStream os = new FileOutputStream(destinationFile, true);
        byte[] dataBuffer = new byte[1024];
        try (BufferedInputStream in = new BufferedInputStream(new URL(imageUrl).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
             e.printStackTrace();
        }
        System.out.println();
    }

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

}