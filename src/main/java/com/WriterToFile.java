package com;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriterToFile {

    private static final String path = "products.txt";
    private static int amountOfProducts = 0;

    private static Logger logger = Logger.getLogger(WriterToFile.class);

    public static int getAmountOfProducts() {
        return amountOfProducts;
    }

    public static void writeToFile(JSONArray content) throws IOException {
        logger.info("writeToFile() was called");

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, true))) {

            if (amountOfProducts == 0) {
                bufferedWriter.append("[");
            }

            for (int i = 0; i < content.length(); i++) {
                if (amountOfProducts != 0) {
                    bufferedWriter.append(",");
                }

                bufferedWriter.append(content.getJSONObject(i).toString());
                amountOfProducts++;
            }

            if (content.length() < RequestMaker.getNumberOfProductsByRequest()) {
                bufferedWriter.append("]");
            }
        } catch (IOException e) {
            logger.error(e.getMessage() + ". Cant write to file " + path);
            throw new IOException(e.getMessage() + ". Cant write to file " + path);
        }
    }
}
