package com.caodnhe150776.myproject.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caodnhe150776.myproject.R;
import com.caodnhe150776.myproject.retrofit.ApiBanHang;
import com.caodnhe150776.myproject.retrofit.RetrofitCilient;
import com.caodnhe150776.myproject.utlis.Utlis;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;



public class DangNhapActivity extends AppCompatActivity {
    TextView txtdangki,txtresetpassword;
    EditText email,password;
    AppCompatButton btndangnhap;
    ApiBanHang apiBanHang;
    boolean isLogin=false;
    CompositeDisposable compositeDisposable;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Paper.init(this);
        txtresetpassword=findViewById(R.id.txtresetpassword);
        txtdangki=findViewById(R.id.txtdangki);
        email=findViewById(R.id.email_dn);
        password=findViewById(R.id.password_dn);
        btndangnhap=findViewById(R.id.btndangnhap);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        apiBanHang= RetrofitCilient.getInstance(Utlis.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        if(Paper.book().read("email")!=null&&Paper.book().read("password")!=null){
            email.setText(Paper.book().read("email"));
            password.setText(Paper.book().read("password"));
            if(Paper.book().read("isLogin")!=null){
                boolean flag=Paper.book().read("isLogin");
                if(flag){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //dangNhap(Paper.book().read("email"),Paper.book().read("password"));
                        }
                    },3000);
                }
            }
        }
        initControl();
    }

    private void dangNhap(String email, String pass) {
        compositeDisposable.add(apiBanHang.dangNhap(email,pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                isLogin=true;
                                Paper.book().write("isLogin",isLogin);
                                Utlis.user_current= userModel.getResult().get(0);
                                //luu lai thong tin ng dung
                                Paper.book().write("User",userModel.getResult().get(0));
                                Intent intent= new Intent(getApplicationContext(), MainActivity2.class);
                                startActivity(intent);
                                finish();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                ));
    }


    private void initControl() {
        txtdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), DangKiActivity.class);
                startActivity(intent);
            }
        });
        txtresetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String str_email= email.getText().toString().trim();
            String str_password= password.getText().toString().trim();
                if(TextUtils.isEmpty(str_email)){
                    Toast.makeText(getApplicationContext(),"Bạn chưa nhập Email",Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(str_password)){
                    Toast.makeText(getApplicationContext(),"Bạn chưa nhập password",Toast.LENGTH_LONG).show();
                }else {
                    Paper.book().write("email",str_email);
                    Paper.book().write("password",str_password);
                    if(user!=null){
                        dangNhap(str_email,str_password);
                    }else {
                        firebaseAuth.signInWithEmailAndPassword(str_email,str_password)
                                .addOnCompleteListener(DangNhapActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            dangNhap(str_email,str_password);
                                        }
                                    }
                                });
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utlis.user_current.getEmail()!=null &&Utlis.user_current.getPassword()!=null ){
            email.setText(Utlis.user_current.getEmail());
            password.setText(Utlis.user_current.getPassword());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}