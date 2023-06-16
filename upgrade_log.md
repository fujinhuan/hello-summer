# 更新日志
##### 2020-04-01
+ 1、新增erp_sales_order_item表字段：modifySkuRemark
    ```
     ALTER TABLE `erp_sales_order_item` ADD COLUMN modifySkuRemark VARCHAR(100) NULL COMMENT '修改sku备注' ;
    ```
##### 2020-03-31
+ 1、删除erp_sales_order表字段：billMethod、billStatus
+ 2、新增erp_sales_order表字段：payMethod、payStatus、payTime、payVoucher、payAmount、orderDate
    ```
    ALTER TABLE `erp_sales_order` ADD COLUMN payMethod INT(11) NOT NULL COMMENT '支付方式（1:微信，2:支付宝，3:线下支付，4:第三方平台，5:0元购）' ;
    ALTER TABLE `erp_sales_order` ADD COLUMN payStatus INT(11) NOT NULL COMMENT '付款状态（0未付款1部分付款2完全付款）' ;
    ALTER TABLE `erp_sales_order` ADD COLUMN payTime BIGINT(20) NOT NULL COMMENT '支付时间（取第一次支付时间）' ;
    ALTER TABLE `erp_sales_order` ADD COLUMN payVoucher VARCHAR(50) NULL COMMENT '支付凭据' ;
    ALTER TABLE `erp_sales_order` ADD COLUMN payAmount DECIMAL(10,2) NOT NULL COMMENT '支付金额' ;
    ALTER TABLE `erp_sales_order` ADD COLUMN shopId INT(11) NOT NULL COMMENT '店铺id（dc_shop）' ;
    ALTER TABLE `erp_sales_order` ADD COLUMN orderDate DATE NOT NULL COMMENT '订单日期' ;
    ```

