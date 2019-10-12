package com;

import java.util.Set;

public class Product {

    private String articleId;
    private Set<String> colors;
    private Set<String> sizes;
    private String name;
    private String brandName;
    private Price minPrice;
    private Price maxPrice;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public Set<String> getColors() {
        return colors;
    }

    public void setColors(Set<String> colors) {
        this.colors = colors;
    }

    public Set<String> getSizes() {
        return sizes;
    }

    public void setSizes(Set<String> sizes) {
        this.sizes = sizes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Price getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Price minPrice) {
        this.minPrice = minPrice;
    }

    public Price getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Price maxPrice) {
        this.maxPrice = maxPrice;
    }


    public static class Builder {
        private Product product;

        public Builder() {
            product = new Product();
        }

        public Builder withName(String name){
            product.name = name;
            return this;
        }

        public Builder withBrandName(String brandName){
            product.brandName = brandName;
            return this;
        }

        public Builder withColors(Set<String> colors){
            product.colors = colors;
            return this;
        }

        public Builder withSizes(Set<String> sizes){
            product.sizes = sizes;
            return this;
        }

        public Builder withArticleId(String articleId){
            product.articleId = articleId;
            return this;
        }

        public Builder withMinPrice(Price minPrice){
            product.minPrice = minPrice;
            return this;
        }

        public Builder withMaxPrice(Price maxPrice){
            product.maxPrice = maxPrice;
            return this;
        }

        public Product build(){
            return product;
        }
    }
}
