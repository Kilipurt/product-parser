package com;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriterToFile {

    private static final String path = "products.txt";
    private static int amountOfProducts = 0;

    private static Logger logger = Logger.getLogger(WriterToFile.class);

    public static int getAmountOfProducts() {
        return amountOfProducts;
    }

    public static void writeToFile(List<Product> products) throws IOException {
        logger.info("writeToFile() was called");

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, true))) {

            if (amountOfProducts == 0) {
                bufferedWriter.append("[");
            }

            for (Product product : products) {
                if (amountOfProducts != 0) {
                    bufferedWriter.append(",");
                }

                bufferedWriter.append(new JSONObject(product).toString());
                amountOfProducts++;
            }

            if (products.size() < RequestMaker.getNumberOfProductsByRequest()) {
                bufferedWriter.append("]");
            }
        } catch (IOException e) {
            logger.error(e.getMessage() + ". Cant write to file " + path);
            throw new IOException(e.getMessage() + ". Cant write to file " + path);
        }
    }
}
