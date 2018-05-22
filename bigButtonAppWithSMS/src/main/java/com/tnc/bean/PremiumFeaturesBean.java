package com.tnc.bean;

/**
 * Created by a3logics on 23/10/17.
 */

public class PremiumFeaturesBean {
    String id;
    String caption;
    String image;

    public PremiumFeaturesBean() {
    }

    public PremiumFeaturesBean(String id, String caption, String image) {
        this.id = id;
        this.caption = caption;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
