package com;

import org.apache.log4j.Logger;

public class Demo {
    private static Logger logger = Logger.getLogger(Demo.class);

    public static void main(String[] args) {
        logger.info("program start");

        Parser parser = new Parser();

        try {
            parser.parse();
        } catch (Exception e) {
            logger.error("Catch error in main. " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Amount of extracted products: " + WriterToFile.getAmountOfProducts());
        System.out.println("Amount of triggered http requests: " + RequestMaker.getPage());
    }
}
