package com.ibring_driver.provider.Models;

import java.io.Serializable;

public class BrandList implements Serializable {

    String id,brandName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
