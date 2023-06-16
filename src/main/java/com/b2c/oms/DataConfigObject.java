package com.b2c.oms;

import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

/**
 * 描述：
 *
 * @author qlp
 * @date 2019-11-08 13:58
 */
public class DataConfigObject {
    //创建 SingleObject 的一个对象
    private static DataConfigObject instance = new DataConfigObject();

    private Integer pageSize = 10;
    private String pddClientId = "";
    private String pddClientSecret = "";
    private Integer autoUpdateTaoOrder = 0;//是否开启淘系订单自动更新 1开启 0关闭
    private String jdbcURL = null;

    public String getJdbcURL() {
        return jdbcURL;
    }

    public String getJdbcUSER() {
        return jdbcUSER;
    }

    public String getJdbcPASSWORD() {
        return jdbcPASSWORD;
    }

    private String jdbcUSER = null;
    private String jdbcPASSWORD = null;

    //让构造函数为 private，这样该类就不会被实例化
    private DataConfigObject() {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties("config.properties");
            pageSize = Integer.parseInt(properties.getProperty("pageSize"));
            pddClientId = "5d1127f5da664d6893d152bf56cbd6a6";//properties.getProperty("pdd_client_id");
            pddClientSecret = "e78898519256c5c7601a5f729834242eeb396e1d";//properties.getProperty("pdd_client_secret");
//            autoUpdateTaoOrder = Integer.parseInt(properties.getProperty("auto_update_order"));

            Properties properties1 = PropertiesLoaderUtils.loadAllProperties("application.properties");
            jdbcURL =  properties1.getProperty("spring.datasource.url");
            jdbcUSER =  properties1.getProperty("spring.datasource.username");
            jdbcPASSWORD =  properties1.getProperty("spring.datasource.password");

        } catch (Exception e) {
            pageSize = 10;
        }
    }

    //获取唯一可用的对象
    public static DataConfigObject getInstance() {
        return instance;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public String getPddClientId(){
        return pddClientId;
    }

    public String getPddClientSecret(){
        return pddClientSecret;
    }

    public Integer getAutoUpdateTaoOrder() {
        return autoUpdateTaoOrder;
    }
}
