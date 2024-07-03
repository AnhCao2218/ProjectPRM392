package com.appmanager.myproject.model;

public class LoaiSp {
    int id;
    String tensanpham;
    String img;

    public LoaiSp(String tensanpham, String img) {
        this.tensanpham = tensanpham;
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTensanpham() {
        return tensanpham;
    }

    public void setTensanpham(String tensanpham) {
        this.tensanpham = tensanpham;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
