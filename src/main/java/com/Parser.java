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
                JSONObject productJSON = products.getJSONObject(i);
                Product productResult = new Product();
                parseProduct(productResult, productJSON);
                result.add(productResult);
            }

            WriterToFile.writeToFile(result);
        }
    }

    private void parseProduct(Product productResult, JSONObject productJSON) {
        logger.info("parseProduct() method was called");

        parseArticleId(productResult, productJSON);

        if (productJSON.has("attributes")) {
            JSONObject attributes = productJSON.getJSONObject("attributes");

            parseColor(productResult, attributes);
            parseBrandName(productResult, attributes);
            parseName(productResult, attributes);
        }

        if (productJSON.has("variants")) {
            JSONArray variants = productJSON.getJSONArray("variants");
            parseSizes(productResult, variants);
        }

        parsePriceRange(productResult, productJSON);
    }

    private void parseSizes(Product productResult, JSONArray variants) {
        logger.info("parseSizes() method was called");

        List<String> sizes = new ArrayList<>();

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

        productResult.setSizes(sizes.toArray(new String[sizes.size()]));
    }

    private void parsePriceRange(Product productResult, JSONObject productJSON) {
        logger.info("parsePriceRange() method was called");

        if (productJSON.has("priceRange")) {
            JSONObject priceRange = productJSON.getJSONObject("priceRange");

            if (priceRange.has("min")) {
                JSONObject minPrice = priceRange.getJSONObject("min");
                productResult.setMinPrice(parsePrice(minPrice));
            }

            if (priceRange.has("max")) {
                JSONObject maxPrice = priceRange.getJSONObject("max");
                productResult.setMaxPrice(parsePrice(maxPrice));
            }
        }
    }

    private Price parsePrice(JSONObject price) {
        logger.info("parsePrice() method was called");

        int withTax = 0;
        int withoutTax = 0;
        String currencyCode = "";

        Price priceResult = new Price();

        if (price.has("withTax")) {
            withTax = price.getInt("withTax");
        }

        priceResult.setWithTax(withTax);

        if (price.has("withoutTax")) {
            withoutTax = price.getInt("withoutTax");
        }

        priceResult.setWithoutTax(withoutTax);

        if (price.has("currencyCode")) {
            currencyCode = price.getString("currencyCode");
        }

        priceResult.setCurrency(currencyCode);

        return priceResult;
    }

    private void parseName(Product productResult, JSONObject attributes) {
        logger.info("parseName() method was called");

        String name = "";

        try {
            name = attributes.getJSONObject("name").getJSONObject("values").getString("label");
        } catch (JSONException e) {
            //no action needed
        }

        productResult.setName(name);
    }

    private void parseBrandName(Product productResult, JSONObject attributes) {
        logger.info("parseBrandName() method was called");

        String brandName = "";

        try {
            brandName = attributes.getJSONObject("brand").getJSONObject("values").getString("label");
        } catch (JSONException e) {
            //no action needed
        }

        productResult.setBrandName(brandName);
    }

    private void parseColor(Product productResult, JSONObject attributes) {
        logger.info("parseColor() method was called");

        try {
            JSONArray colorsJSON = attributes.getJSONObject("colorDetail").getJSONArray("values");
            String[] colors = new String[colorsJSON.length()];

            for (int k = 0; k < colorsJSON.length(); k++) {
                if (colorsJSON.getJSONObject(k).has("label")) {
                    colors[k] = colorsJSON.getJSONObject(k).getString("label");
                }
            }

            productResult.setColors(colors);
        } catch (JSONException e) {
            //no action needed
        }
    }

    private void parseArticleId(Product productResult, JSONObject productJSON) {
        logger.info("parseArticleId() method was called");

        String articleId = "";

        if (productJSON.has("referenceKey")) {
            articleId = productJSON.getString("referenceKey");
        }

        productResult.setArticleId(articleId);
    }
}
