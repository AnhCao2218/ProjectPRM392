package com.appmanager.myproject.model;

import java.io.Serializable;

public class SanPhamMoi implements Serializable {
    int id;
    String tensp;
    String hinhanh;
    String giasp;
    int loaisp;
    String mota;

    public SanPhamMoi(int id, String tensp, String hinhanh, String giasp, int loaisp, String mota) {
        this.id = id;
        this.tensp = tensp;
        this.hinhanh = hinhanh;
        this.giasp = giasp;
        this.loaisp = loaisp;
        this.mota = mota;
    }

    public SanPhamMoi() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public String getGiasp() {
        return giasp;
    }

    public void setGiasp(String giasp) {
        this.giasp = giasp;
    }

    public int getLoaisp() {
        return loaisp;
    }

    public void setLoaisp(int loaisp) {
        this.loaisp = loaisp;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }
}
