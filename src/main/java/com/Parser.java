package com;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Parser {

    private Logger logger = Logger.getLogger(Parser.class);
    private RequestMaker requestMaker = new RequestMaker();

    private static final String CURRENCY_CODE = "currencyCode";
    private static final String REFERENCE_KEY = "referenceKey";
    private static final String COLOR_DETAIL = "colorDetail";
    private static final String PRICE_RANGE = "priceRange";
    private static final String WITHOUT_TAX = "withoutTax";
    private static final String ATTRIBUTES = "attributes";
    private static final String SHOP_SIZE = "shopSize";
    private static final String VARIANTS = "variants";
    private static final String ENTITIES = "entities";
    private static final String WITH_TAX = "withTax";
    private static final String VALUES = "values";
    private static final String LABEL = "label";
    private static final String VALUE = "value";
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final String MIN = "min";
    private static final String MAX = "max";

    public void parse() throws Exception {
        logger.info("parse() method was called");

        boolean endFlag = true;

        while (endFlag) {
            JSONObject responseJSON = requestMaker.doRequest();

            if (!responseJSON.has(ENTITIES)) {
                break;
            }

            JSONArray products = responseJSON.getJSONArray(ENTITIES);

            if (products.length() < RequestMaker.getNumberOfProductsByRequest()) {
                endFlag = false;
            }

            List<Product> result = new ArrayList<>();

            for (int i = 0; i < products.length(); i++) {
                try {
                    JSONObject productJSON = products.getJSONObject(i);

                    JSONObject attributes = productJSON.getJSONObject(ATTRIBUTES);
                    JSONObject priceRange = productJSON.getJSONObject(PRICE_RANGE);

                    result.add(new Product.Builder()
                            .withBrandName(attributes.getJSONObject(BRAND).getJSONObject(VALUES).getString(LABEL))
                            .withName(attributes.getJSONObject(NAME).getJSONObject(VALUES).getString(LABEL))
                            .withMaxPrice(parsePrice(priceRange.getJSONObject(MAX)))
                            .withMinPrice(parsePrice(priceRange.getJSONObject(MIN)))
                            .withArticleId(productJSON.getString(REFERENCE_KEY))
                            .withColors(parseColor(attributes))
                            .withSizes(parseSizes(productJSON))
                            .build());
                } catch (RuntimeException e) {
                    logger.error("Product " + (WriterToFile.getAmountOfProducts() + i) + " was not extracted. "
                            + e.getMessage());
                }
            }

            WriterToFile.writeToFile(result);
        }
    }

    private Set<String> parseSizes(JSONObject productJSON) {
        logger.info("parseSizes() method was called");

        JSONArray variants = productJSON.getJSONArray(VARIANTS);
        Set<String> sizes = new HashSet<>();

        for (int j = 0; j < variants.length(); j++) {
            try {
                String size = variants.getJSONObject(j)
                        .getJSONObject(ATTRIBUTES)
                        .getJSONObject(SHOP_SIZE)
                        .getJSONObject(VALUES)
                        .getString(VALUE);

                sizes.add(size);
            } catch (JSONException e) {
                //no action needed
            }
        }

        return sizes;
    }

    private Price parsePrice(JSONObject price) {
        logger.info("parsePrice() method was called");

        Price priceResult = new Price();
        priceResult.setWithTax(price.getInt(WITH_TAX));
        priceResult.setWithoutTax(price.getInt(WITHOUT_TAX));
        priceResult.setCurrency(price.getString(CURRENCY_CODE));

        return priceResult;
    }

    private Set<String> parseColor(JSONObject attributes) {
        logger.info("parseColor() method was called");

        JSONArray colorsJSON = attributes.getJSONObject(COLOR_DETAIL).getJSONArray(VALUES);
        Set<String> colors = new HashSet<>();

        for (int k = 0; k < colorsJSON.length(); k++) {
            try {
                colors.add(colorsJSON.getJSONObject(k).getString(LABEL));
            } catch (JSONException e) {
                //no action needed
            }
        }

        return colors;
    }
}
