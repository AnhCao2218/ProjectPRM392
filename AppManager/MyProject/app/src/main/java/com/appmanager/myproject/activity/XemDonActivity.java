package com.appmanager.myproject.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appmanager.myproject.R;
import com.appmanager.myproject.adapter.DonHangAdapter;
import com.appmanager.myproject.retrofit.ApiBanHang;
import com.appmanager.myproject.retrofit.RetrofitCilient;
import com.appmanager.myproject.utlis.Utlis;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable= new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView redonhang;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_xem_don);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        initToolbar();
        apiBanHang= RetrofitCilient.getInstance(Utlis.BASE_URL).create(ApiBanHang.class);

        getOrder();
    }

    private void getOrder() {
        compositeDisposable.add(apiBanHang.xemDonHang(Utlis.user_current.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            DonHangAdapter adapter= new DonHangAdapter(getApplicationContext(),donHangModel.getResult());
                            redonhang.setAdapter(adapter);


                        },
                        throwable -> {

                        }
                ));
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        redonhang= findViewById(R.id.recycleview_donhang);
        apiBanHang=RetrofitCilient.getInstance(Utlis.BASE_URL).create(ApiBanHang.class);
        toolbar=findViewById(R.id.toolbar);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        redonhang.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}