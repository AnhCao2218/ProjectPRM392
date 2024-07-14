package com.caodnhe150776.myproject.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.caodnhe150776.myproject.R;
import com.caodnhe150776.myproject.adapter.LoaiSPAdapter;
import com.caodnhe150776.myproject.adapter.SanPhamMoiAdapter;
import com.caodnhe150776.myproject.model.LoaiSp;
import com.caodnhe150776.myproject.model.LoaiSpModel;
import com.caodnhe150776.myproject.model.SanPhamMoi;
import com.caodnhe150776.myproject.model.SanPhamMoiModel;
import com.caodnhe150776.myproject.model.User;
import com.caodnhe150776.myproject.retrofit.ApiBanHang;
import com.caodnhe150776.myproject.retrofit.RetrofitCilient;
import com.caodnhe150776.myproject.utlis.Utlis;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nex3z.notificationbadge.NotificationBadge;

import org.jetbrains.annotations.Async;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity2 extends AppCompatActivity {
    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerView;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSPAdapter adapter;
    List<LoaiSp> mangloaiSp;
    CompositeDisposable compositeDisposable= new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangspMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Paper.init(this);
        if(Paper.book().read("User")!=null){
            User user=Paper.book().read("User");
            Utlis.user_current=user;
        }
        imgsearch=findViewById(R.id.imgsearch);
        toolbar= findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper= findViewById(R.id.viewlipper);
        recyclerView= findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager= new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        listViewManHinhChinh= findViewById(R.id.listviewmanhinhchinh);
        badge=findViewById(R.id.menu_sl);
        frameLayout=findViewById(R.id.framelayout);
        navigationView= findViewById(R.id.navigationview);
        drawerLayout=findViewById(R.id.drawerlayout);
        apiBanHang= RetrofitCilient.getInstance(Utlis.BASE_URL).create(ApiBanHang.class);
        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent= new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
            }
        });
        getToken();
        ActionBar();

        mangloaiSp= new ArrayList<>();
        mangspMoi= new ArrayList<>();
        if(Utlis.manggiohang==null){
            Utlis.manggiohang= new ArrayList<>();
        }else {
            int totalItem=0;
            for (int i=0;i<Utlis.manggiohang.size();i++){
                totalItem=totalItem+Utlis.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang=new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });


        if(isConnected(this)){
            ActionViewFliper();
            getLoaiSanPham();
            getSpMoi();
            getEventClick();
        }else {
            Toast.makeText(getApplicationContext(),"ko co internet, ",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem=0;
        for (int i=0;i<Utlis.manggiohang.size();i++){
            totalItem=totalItem+Utlis.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(!TextUtils.isEmpty(s)){
                            compositeDisposable.add(apiBanHang.updatetoken(Utlis.user_current.getId(),s)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            messageModel -> {

                                            },
                                            throwable -> {
                                                Log.d("log",throwable.getMessage());
                                            }
                                    ));
                        }
                    }
                });
    }

    private void getEventClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent trangchu= new Intent(getApplicationContext(), MainActivity2.class);
                        startActivity(trangchu);
                        break;
                    case 1:
                        Intent dienthoai= new Intent(getApplicationContext(), DienThoaiActivity.class);
                        dienthoai.putExtra("loai",1);
                        startActivity(dienthoai);
                        break;
                    case 2:
                        Intent laptop= new Intent(getApplicationContext(), LaptopActivity.class);
                        laptop.putExtra("loai",2);
                        startActivity(laptop);
                        break;
                    case 5:
                        Intent donhang= new Intent(getApplicationContext(), XemDonActivity.class);

                        startActivity(donhang);
                        break;
                    case 6:
                        Paper.book().delete("User");
                        Intent dangnhap= new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(dangnhap);
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        break;
                }
            }
        });


    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel-> {
                            if(sanPhamMoiModel.isSuccess()){
                                mangspMoi=sanPhamMoiModel.getResult();
                                spAdapter= new SanPhamMoiAdapter(getApplicationContext(),mangspMoi);
                                recyclerView.setAdapter(spAdapter);
                            }
                        },
                        throwable->{
                            Toast.makeText(getApplicationContext(),"Khong ket noi duoc sever"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    loaiSpModel -> {
                        if(loaiSpModel.isSuccess()){
                        mangloaiSp= loaiSpModel.getResult();
                        mangloaiSp.add(new LoaiSp("Đăng xuất",""));
                            adapter = new LoaiSPAdapter(getApplicationContext(),mangloaiSp);
                            listViewManHinhChinh.setAdapter(adapter);
                        }
                    }
                ));

    }

    private void ActionViewFliper() {
        List<String> mangquangcao= new ArrayList<>();
        mangquangcao.add("https://smarthomekit.vn/wp-content/uploads/2024/06/custom-access-ios18-banner-1024x576.jpg");
        mangquangcao.add("https://s3.cloud.cmctelecom.vn/tinhte1/2018/09/4429992_GIAM1TR.png");
        mangquangcao.add("https://tragop.online/uploads/fbimgs/mua-ban-tra-gop-dien-thoai-iphone-lai-suat-0.jpg");
        mangquangcao.add("https://cdn11.dienmaycholon.vn/filewebdmclnew/DMCL21/Picture//Tm/Tm_picture_2347/samsung-a35a55-_mobi_261_1200.png.webp");
        mangquangcao.add("https://inkythuatso.com/uploads/thumbnails/800/2022/01/banner-may-tinh-inkythuatso-21-11-49-53.jpg");

        for (int i=0; i<mangquangcao.size();i++){
            ImageView imageView= new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
        Animation slide_out= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slider_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setInAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi!=null&&wifi.isConnected())||(mobile!=null&&mobile.isConnected())){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}