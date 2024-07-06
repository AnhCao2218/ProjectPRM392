package com.appmanager.myproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appmanager.myproject.R;
import com.appmanager.myproject.adapter.SanPhamMoiAdapter;
import com.appmanager.myproject.model.EventBus.SuaXoaEvent;
import com.appmanager.myproject.model.SanPhamMoi;
import com.appmanager.myproject.retrofit.ApiBanHang;
import com.appmanager.myproject.retrofit.RetrofitCilient;
import com.appmanager.myproject.utlis.Utlis;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import soup.neumorphism.NeumorphCardView;

public class QuanliActivity extends AppCompatActivity {

    ImageView themsp;
    RecyclerView recyclerView;
    ApiBanHang apiBanHang;

    List<SanPhamMoi>list;
    SanPhamMoiAdapter adapter;

    SanPhamMoi spSuaXoa;

    CompositeDisposable compositeDisposable =new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quanli);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiBanHang= RetrofitCilient.getInstance(Utlis.BASE_URL).create(ApiBanHang.class);
        themsp=findViewById(R.id.img_them);
        recyclerView=findViewById(R.id.recycleview_ql);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        initControl();
        getSpMoi();
    }
    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel-> {
                            if(sanPhamMoiModel.isSuccess()){
                                list=sanPhamMoiModel.getResult();
                                adapter= new SanPhamMoiAdapter(getApplicationContext(),list);
                                recyclerView.setAdapter(adapter);
                            }
                        },
                        throwable->{
                            Toast.makeText(getApplicationContext(),"Khong ket noi duoc sever"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }
    private void initControl() {
    themsp.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent= new Intent(getApplicationContext(), ThemSPActivity.class);
            startActivity(intent);
        }
    });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Sửa")){
            suaSanPham();
        }else if(item.getTitle().equals("Xoá")){
            xoaSanPham();
        }


        return super.onContextItemSelected(item);
    }

    private void suaSanPham() {
        Intent intent= new Intent(getApplicationContext(),ThemSPActivity.class);
        intent.putExtra("sửa",spSuaXoa);
        startActivity(intent);
    }

    private void xoaSanPham() {
        compositeDisposable.add(apiBanHang.xoaSanPham(spSuaXoa.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    messageModel -> {
                            if(messageModel.isSuccess()){
                                Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                                getSpMoi();
                            }else {
                                Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                            }
                    },
                        throwable -> {
                            Log.d("log",throwable.getMessage());
                        }

                ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();

    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void evenSuaXoa(SuaXoaEvent event){
        if(event !=null){
            spSuaXoa=event.getSanPhamMoi();

        }
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