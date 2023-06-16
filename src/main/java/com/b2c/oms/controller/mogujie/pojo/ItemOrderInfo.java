/**
 * Copyright 2020 bejson.com
 */
package com.b2c.oms.controller.mogujie.pojo;
import java.util.List;

/**
 * Auto-generated: 2020-05-11 9:57:46
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ItemOrderInfo {

    private String img;
    private String extraMap;
    private String itemCode;//商品编码
    private String orderStatus;
    private String refundStatus;
    private long itemOrderId;
    private String title;
    private String imgUrl;
    private String itemId;
    private Integer nowPrice;
    private int number;
    private String skuAttributes;
    private Integer price;
    private String skuCode;//sku编码
    private String skuId;
    public void setImg(String img) {
        this.img = img;
    }
    public String getImg() {
        return img;
    }

    public void setExtraMap(String extraMap) {
        this.extraMap = extraMap;
    }
    public String getExtraMap() {
        return extraMap;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public String getItemCode() {
        return itemCode;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }
    public String getRefundStatus() {
        return refundStatus;
    }

    public void setItemOrderId(long itemOrderId) {
        this.itemOrderId = itemOrderId;
    }
    public long getItemOrderId() {
        return itemOrderId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public String getImgUrl() {
        return imgUrl;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getItemId() {
        return itemId;
    }



    public void setNumber(int number) {
        this.number = number;
    }
    public int getNumber() {
        return number;
    }

    public void setSkuAttributes(String skuAttributes) {
        this.skuAttributes = skuAttributes;
    }
    public String getSkuAttributes() {
        return skuAttributes;
    }


    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
    public String getSkuId() {
        return skuId;
    }

    public Integer getNowPrice() {
        return nowPrice;
    }

    public void setNowPrice(Integer nowPrice) {
        this.nowPrice = nowPrice;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}