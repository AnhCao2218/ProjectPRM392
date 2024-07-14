package com.appmanager.myproject.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appmanager.myproject.R;
import com.appmanager.myproject.adapter.DonHangAdapter;
import com.appmanager.myproject.model.DonHang;
import com.appmanager.myproject.model.EventBus.DonHangEvent;
import com.appmanager.myproject.model.NotiSendData;
import com.appmanager.myproject.retrofit.ApiBanHang;
import com.appmanager.myproject.retrofit.ApiPushNotification;
import com.appmanager.myproject.retrofit.RetrofitCilient;
import com.appmanager.myproject.retrofit.RetrofitCilientNoti;
import com.appmanager.myproject.utlis.Utlis;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class XemDonActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable= new CompositeDisposable();
    ApiBanHang apiBanHang;
    RecyclerView redonhang;
    Toolbar toolbar;
    DonHang donHang;
    int tinhtrang;
    AlertDialog dialog;
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
        compositeDisposable.add(apiBanHang.xemDonHang(0)
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
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void eventDonHang(DonHangEvent event){
        if(event!=null){
            donHang=event.getDonHang();
            showDialog();

        }
    }

    private void showDialog() {
        LayoutInflater inflater= getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_donhang,null);
        Spinner spinner= view.findViewById(R.id.spiner_dialog);
        AppCompatButton btndongy= view.findViewById(R.id.dongy_dialog);
        List<String> list= new ArrayList<>();
        list.add("Đơn hàng dang được xử lí");
        list.add("Đơn hàng đã đặt thành công");
        list.add("Đơn hàng đã giao cho đơn vị vân chuyển");
        list.add("Đơn hàng đã giao thành công");
        list.add("Đơn hàng đã huỷ");

        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(adapter);
        spinner.setSelection(donHang.getTrangthai());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tinhtrang=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btndongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                capNhatDonhang();
            }
        });
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setView(view);
        dialog=builder.create();
        dialog.show();
    }

    private void capNhatDonhang() {
        compositeDisposable.add(apiBanHang.updatedonhang(donHang.getId(),tinhtrang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            getOrder();
                            dialog.dismiss();
                            pushNotiToUser();
                        },
                        throwable -> {

                        }
                ));
    }
    private void pushNotiToUser() {
        compositeDisposable.add(apiBanHang.gettoken(0,donHang.getUserid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            for (int i=0;i<userModel.getResult().size();i++){
                                if(userModel.isSuccess()){
                                    Map<String, String> data= new HashMap<>();
                                    data.put("title","Thong Bao");
                                    data.put("body",trangThaiDon(tinhtrang));

                                    NotiSendData notiSendData= new NotiSendData(userModel.getResult().get(i).getToken(),data);
                                    ApiPushNotification apiPushNotification= RetrofitCilientNoti.getInstance().create(ApiPushNotification.class);
                                    compositeDisposable.add(apiPushNotification.sendNotification(notiSendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiResponse -> {

                                                    },
                                                    throwable -> {
                                                        Log.d("log",throwable.getMessage());
                                                    }
                                            ));
                                }
                            }

                        },
                        throwable -> {
                            Log.d("log",throwable.getMessage());
                        }
                ));


    }

    private String trangThaiDon(int status){
        String result="";
        switch (status){
            case 0: result="Đơn hàng dang được xử lí";
                break;
            case 1: result="Đơn hàng đã đặt thành công";
                break;
            case 2: result="Đơn hàng đã giao cho đơn vị vân chuyển";
                break;
            case 3: result="Đơn hàng đã giao thành công";
                break;
            case 4: result="Đơn hàng đã huỷ";
                break;

        }

        return  result;

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}