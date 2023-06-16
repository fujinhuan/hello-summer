package com.b2c.oms.reponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：省市区AJAX
 *
 * @author qlp
 * @date 2019-01-30 11:27
 */
public class AreaApiResult {
    private int status;
    private List<Area> data;

    public AreaApiResult() {
        this.data = new ArrayList<Area>();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Area> getData() {
        return data;
    }

    public void setData(List<Area> data) {
        this.data = data;
    }

    public void addData(String code, String name) {
        data.add(new Area(code, name));
    }

    public class Area {
        private String code;
        private String name;

        public Area(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
