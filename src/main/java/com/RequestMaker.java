package com;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class RequestMaker {

    private static int page = 1;
    private static final int numberOfProductsByRequest = 500;

    private Logger logger = Logger.getLogger(RequestMaker.class);

    public static int getNumberOfProductsByRequest() {
        return numberOfProductsByRequest;
    }

    public static int getPage() {
        return page;
    }

    public JSONObject doRequest() throws Exception {
        logger.info("doRequest() method was called");

        String ssp = "//api-cloud.aboutyou.de/v1/products?with=attributes:key(brand|name|colorDetail),variants," +
                "variants.attributes:key(shopSize),priceRange&filters[category]=20290&sortDir=desc&sortScore=" +
                "category_scores&sortChannel=etkp&page=" + page + "&perPage=" + numberOfProductsByRequest +
                "&campaignKey=px&shopId=139";

        HttpGet request = new HttpGet(new URI("https", ssp, null));

        JSONObject responseJSON = new JSONObject();

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)) {

            int responseStatusCode = response.getStatusLine().getStatusCode();
            if (responseStatusCode != 200) {
                logger.error("Request was unsuccessful. Status code " + responseStatusCode);
                throw new Exception("Request was unsuccessful. Status code " + responseStatusCode);
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                page++;

                try {
                    responseJSON = new JSONObject(EntityUtils.toString(entity));
                } catch (JSONException e) {
                    logger.error("Response has wrong format");
                    throw new Exception("Response has wrong format");
                }
            }

        }

        return responseJSON;
    }
}
