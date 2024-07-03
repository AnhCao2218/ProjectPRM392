package com.appmanager.myproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appmanager.myproject.R;
import com.appmanager.myproject.adapter.GioHangAdapter;
import com.appmanager.myproject.model.EventBus.TinhTongEvent;
import com.appmanager.myproject.model.GioHang;
import com.appmanager.myproject.utlis.Utlis;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.List;

public class GioHangActivity extends AppCompatActivity {
    TextView giohangtrong, tongtien;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button btnmuahang;
    GioHangAdapter adapter;
    long tongtiensp;
    List<GioHang> gioHangList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gio_hang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        giohangtrong = findViewById(R.id.txtgiohangtrong);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycleviewgiohang);
        tongtien = findViewById(R.id.txttongtien);
        btnmuahang = findViewById(R.id.btnmuahang);
        initControl();
        tinhTongTien();
    }

    private void tinhTongTien() {
        tongtiensp = 0;
        if (Utlis.mangmuahang != null) {
            for (int i = 0; i < Utlis.mangmuahang.size(); i++) {
                tongtiensp += Utlis.mangmuahang.get(i).getGiasp() * Utlis.mangmuahang.get(i).getSoluong();
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        tongtien.setText(decimalFormat.format(tongtiensp));
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if (Utlis.manggiohang == null || Utlis.manggiohang.size() == 0) {
            giohangtrong.setVisibility(View.VISIBLE);
        } else {
            adapter = new GioHangAdapter(getApplicationContext(), Utlis.manggiohang);
            recyclerView.setAdapter(adapter);
        }
        btnmuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ThanhToanActivity.class);
                intent.putExtra("tongtien", tongtiensp);
                Utlis.manggiohang.clear();

                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event) {
        if (event != null) {
            tinhTongTien();
        }
    }
}