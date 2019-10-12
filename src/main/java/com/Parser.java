package com;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private Logger logger = Logger.getLogger(Parser.class);
    private RequestMaker requestMaker = new RequestMaker();

    public void parse() throws Exception {
        logger.info("parse() method was called");

        boolean endFlag = true;

        while (endFlag) {
            JSONObject responseJSON = requestMaker.doRequest();

            if (!responseJSON.has("entities")) {
                break;
            }

            JSONArray products = responseJSON.getJSONArray("entities");

            if (products.length() < RequestMaker.getNumberOfProductsByRequest()) {
                endFlag = false;
            }

            List<Product> result = new ArrayList<>();

            for (int i = 0; i < products.length(); i++) {
                try {
                    result.add(parseProduct(products.getJSONObject(i)));
                } catch (RuntimeException e) {
                    logger.error("Product " + (WriterToFile.getAmountOfProducts() + i) + " was not extracted. "
                            + e.getMessage());
                }
            }

            WriterToFile.writeToFile(result);
        }
    }

    private Product parseProduct(JSONObject productJSON) {
        logger.info("parseProduct() method was called");

        String articleId = productJSON.getString("referenceKey");
        String[] sizes = parseSizes(productJSON);

        JSONObject attributes = productJSON.getJSONObject("attributes");
        String[] colors = parseColor(attributes);
        String brandName = attributes.getJSONObject("brand").getJSONObject("values").getString("label");
        String name = attributes.getJSONObject("name").getJSONObject("values").getString("label");

        JSONObject priceRange = productJSON.getJSONObject("priceRange");
        Price minPrice = parsePrice(priceRange.getJSONObject("min"));
        Price maxPrice = parsePrice(priceRange.getJSONObject("max"));

        return new Product.Builder()
                .withArticleId(articleId)
                .withName(name)
                .withBrandName(brandName)
                .withColors(colors)
                .withMaxPrice(maxPrice)
                .withMinPrice(minPrice)
                .withSizes(sizes)
                .build();
    }

    private String[] parseSizes(JSONObject productJSON) {
        logger.info("parseSizes() method was called");

        List<String> sizes = new ArrayList<>();

        JSONArray variants = productJSON.getJSONArray("variants");

        for (int j = 0; j < variants.length(); j++) {
            JSONObject variant = variants.getJSONObject(j);

            try {
                String size = variant.getJSONObject("attributes")
                        .getJSONObject("shopSize")
                        .getJSONObject("values")
                        .getString("value");

                if (!sizes.contains(size)) {
                    sizes.add(size);
                }
            } catch (JSONException e) {
                //no action needed
            }
        }

        return sizes.toArray(new String[sizes.size()]);
    }

    private Price parsePrice(JSONObject price) {
        logger.info("parsePrice() method was called");

        Price priceResult = new Price();
        priceResult.setWithTax(price.getInt("withTax"));
        priceResult.setWithoutTax(price.getInt("withoutTax"));
        priceResult.setCurrency(price.getString("currencyCode"));

        return priceResult;
    }

    private String[] parseColor(JSONObject attributes) {
        logger.info("parseColor() method was called");

        List<String> colors = new ArrayList<>();

        try {
            JSONArray colorsJSON = attributes.getJSONObject("colorDetail").getJSONArray("values");

            for (int k = 0; k < colorsJSON.length(); k++) {
                colors.add(colorsJSON.getJSONObject(k).getString("label"));
            }

        } catch (JSONException e) {
            //no action needed
        }

        return colors.toArray(new String[colors.size()]);
    }
}
