package com.caodnhe150776.myproject.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caodnhe150776.myproject.R;
import com.caodnhe150776.myproject.adapter.DienThoaiAdapter;
import com.caodnhe150776.myproject.model.SanPhamMoi;
import com.caodnhe150776.myproject.retrofit.ApiBanHang;
import com.caodnhe150776.myproject.retrofit.RetrofitCilient;
import com.caodnhe150776.myproject.utlis.Utlis;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DienThoaiActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ApiBanHang apiBanHang;
    int page=1;
    int loai;
    DienThoaiAdapter adapterDt;
    List<SanPhamMoi> sanPhamMoiList;
    CompositeDisposable compositeDisposable= new CompositeDisposable();

    Handler handler= new Handler();
    boolean isLoading=false;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dien_thoai);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar=findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.recycleview_dt);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        sanPhamMoiList= new ArrayList<>();
        apiBanHang= RetrofitCilient.getInstance(Utlis.BASE_URL).create(ApiBanHang.class);
        loai=getIntent().getIntExtra("loai",1);
        getData(page);
        ActionToolBar();
        addEventLoad();
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if( isLoading == false){
                    if(linearLayoutManager.findLastCompletelyVisibleItemPosition()==sanPhamMoiList.size()-1){
                        isLoading=true;
                        loadMore();
                    }
                }

            }
        });
    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                sanPhamMoiList.add(null);
                adapterDt.notifyItemInserted(sanPhamMoiList.size()-1);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //remove null
                sanPhamMoiList.remove(sanPhamMoiList.size()-1);
                adapterDt.notifyItemRemoved(sanPhamMoiList.size());
                page=page+1;
                getData(page);
                adapterDt.notifyDataSetChanged();
                isLoading = false;
            }
        },2000);
    }

    private void getData(int page) {
        compositeDisposable.add(apiBanHang.getSanPham(page,loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                        if(sanPhamMoiModel.isSuccess()){
                            if (adapterDt == null) {
                                sanPhamMoiList = sanPhamMoiModel.getResult();
                                adapterDt = new DienThoaiAdapter(getApplicationContext(), sanPhamMoiList);
                                recyclerView.setAdapter(adapterDt);
                            } else {
                                int vitri = sanPhamMoiList.size() - 1;
                                int soluongadd = sanPhamMoiModel.getResult().size();
                                for (int i = 0; i < soluongadd; i++) {
                                    sanPhamMoiList.add(sanPhamMoiModel.getResult().get(i));
                                }
                                adapterDt.notifyItemRangeInserted(vitri, soluongadd);
                            }

                        }else {
                            Toast.makeText(getApplicationContext(),"Het du lieu roi",Toast.LENGTH_LONG).show();
                            isLoading=true;
                        }
                        },
                        throwable -> {

                            Toast.makeText(getApplicationContext(),"Khong ket noi Sever",Toast.LENGTH_LONG).show();

                        }
                ));
    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}