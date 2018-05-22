package com.tnc.SocialNetwork.Facebook;

/**
 * Created by a3logics on 27/11/17.
 */

public class FaceBookApiResponse  {


    /**
     * id : 1463808563705266
     * name : Devender Singh Parihar
     * picture : {"data":{"height":120,"is_silhouette":true,"url":"https://scontent.xx.fbcdn.net/v/t1.0-1/c35.0.120.120/p120x120/10354686_10150004552801856_220367501106153455_n.jpg?oh=2014135e1c0b57f9fd91295ac9ba8e45&oe=5AA5F98E","width":120}}
     */

    private String id;
    private String name;
    private PictureBean picture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PictureBean getPicture() {
        return picture;
    }

    public void setPicture(PictureBean picture) {
        this.picture = picture;
    }

    public static class PictureBean {
        /**
         * data : {"height":120,"is_silhouette":true,"url":"https://scontent.xx.fbcdn.net/v/t1.0-1/c35.0.120.120/p120x120/10354686_10150004552801856_220367501106153455_n.jpg?oh=2014135e1c0b57f9fd91295ac9ba8e45&oe=5AA5F98E","width":120}
         */

        private DataBean data;

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * height : 120
             * is_silhouette : true
             * url : https://scontent.xx.fbcdn.net/v/t1.0-1/c35.0.120.120/p120x120/10354686_10150004552801856_220367501106153455_n.jpg?oh=2014135e1c0b57f9fd91295ac9ba8e45&oe=5AA5F98E
             * width : 120
             */

            private int height;
            private boolean is_silhouette;
            private String url;
            private int width;

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public boolean isIs_silhouette() {
                return is_silhouette;
            }

            public void setIs_silhouette(boolean is_silhouette) {
                this.is_silhouette = is_silhouette;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }
        }
    }
}
