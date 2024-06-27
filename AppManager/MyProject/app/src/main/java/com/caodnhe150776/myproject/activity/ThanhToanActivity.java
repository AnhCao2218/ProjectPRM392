package com.caodnhe150776.myproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caodnhe150776.myproject.R;
import com.caodnhe150776.myproject.retrofit.ApiBanHang;
import com.caodnhe150776.myproject.retrofit.RetrofitCilient;
import com.caodnhe150776.myproject.utlis.Utlis;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThanhToanActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txttongtien,txtphone,txtemail;
    EditText editdiachi;
    AppCompatButton btndathang;

    CompositeDisposable compositeDisposable= new CompositeDisposable();
    ApiBanHang apiBanHang;
    long tongtien;
    int totalItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_thanh_toan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiBanHang= RetrofitCilient.getInstance(Utlis.BASE_URL).create(ApiBanHang.class);
        toolbar=findViewById(R.id.toolbar);
        txttongtien=findViewById(R.id.txttongtien);
        txtphone=findViewById(R.id.txtphone);
        txtemail=findViewById(R.id.txtemail);
        editdiachi=findViewById(R.id.editdiachi);
        btndathang=findViewById(R.id.btndathang);
        initCOntrol();
        countItem();
    }

    private void countItem() {
        totalItem=0;
        for (int i=0;i<Utlis.mangmuahang.size();i++){
            totalItem=totalItem+Utlis.mangmuahang.get(i).getSoluong();
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void initCOntrol() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DecimalFormat decimalFormat= new DecimalFormat("###,###,###");
        tongtien=getIntent().getLongExtra("tongtien",0);
        txttongtien.setText(decimalFormat.format(tongtien));
        txtemail.setText(Utlis.user_current.getEmail());
        txtphone.setText(Utlis.user_current.getMobile());


        btndathang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_diachi= editdiachi.getText().toString().trim();
                if(TextUtils.isEmpty(str_diachi)){
                    Toast.makeText(getApplicationContext(),"Bạn chưa nhập địa chỉ",Toast.LENGTH_LONG).show();
                }else {
                    String str_email= Utlis.user_current.getEmail();
                    String str_sdt=Utlis.user_current.getMobile();
                    int id=Utlis.user_current.getId();

                    Log.d("test",new Gson().toJson(Utlis.mangmuahang));
                    compositeDisposable.add(apiBanHang.createOder(str_email,str_sdt,String.valueOf(tongtien),id,str_diachi, totalItem,new Gson().toJson(Utlis.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    userModel -> {
                                        Toast.makeText(getApplicationContext(),"Thành Công",Toast.LENGTH_LONG).show();
                                        Utlis.mangmuahang.clear();
                                        Intent intent= new Intent(getApplicationContext(), MainActivity2.class);
                                        startActivity(intent);
                                        finish();
                                        },
                                    throwable -> {
                                        Log.d("logg",throwable.getMessage());
                                        Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                            ));
                }
            }
        });
    }
}