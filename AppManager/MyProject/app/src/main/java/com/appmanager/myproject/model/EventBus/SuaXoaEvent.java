package com.appmanager.myproject.model.EventBus;

import com.appmanager.myproject.model.SanPhamMoi;

public class SuaXoaEvent {
    SanPhamMoi sanPhamMoi;

    public SanPhamMoi getSanPhamMoi() {
        return sanPhamMoi;
    }

    public void setSanPhamMoi(SanPhamMoi sanPhamMoi) {
        this.sanPhamMoi = sanPhamMoi;
    }

    public SuaXoaEvent(SanPhamMoi sanPhamMoi) {
        this.sanPhamMoi = sanPhamMoi;
    }
}
