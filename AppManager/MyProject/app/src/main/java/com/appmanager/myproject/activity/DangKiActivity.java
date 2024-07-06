package com.appmanager.myproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appmanager.myproject.R;
import com.appmanager.myproject.retrofit.ApiBanHang;
import com.appmanager.myproject.retrofit.RetrofitCilient;
import com.appmanager.myproject.utlis.Utlis;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {

    EditText email,password,repassword,mobile,username;
    AppCompatButton button;
    ApiBanHang apiBanHang;
    FirebaseAuth firebaseAuth;
    CompositeDisposable compositeDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ki);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email=findViewById(R.id.textEmail);
        password=findViewById(R.id.textPass);
        repassword=findViewById(R.id.textRePass);
        button=findViewById(R.id.btndangki);
        mobile=findViewById(R.id.textMobile);
        username=findViewById(R.id.textUserName);
        apiBanHang= RetrofitCilient.getInstance(Utlis.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        initControl();

    }

    private void initControl() {
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dangKi();
        }
    });
    }

    private void dangKi() {
        String str_email= email.getText().toString().trim();
        String str_password= password.getText().toString().trim();
        String str_repassword= repassword.getText().toString().trim();
        String str_mobile= mobile.getText().toString().trim();
        String str_username= username.getText().toString().trim();
        if(TextUtils.isEmpty(str_email)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập Email",Toast.LENGTH_LONG).show();
        } else if(TextUtils.isEmpty(str_password)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập password",Toast.LENGTH_LONG).show();
        } else if(TextUtils.isEmpty(str_repassword)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập Repassword",Toast.LENGTH_LONG).show();
        } else if(TextUtils.isEmpty(str_mobile)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập Mobile",Toast.LENGTH_LONG).show();
        }else if(TextUtils.isEmpty(str_username)){
            Toast.makeText(getApplicationContext(),"Bạn chưa nhập UserName",Toast.LENGTH_LONG).show();
        } else {
            if(str_password.equals(str_repassword)){
                firebaseAuth=FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(str_email,str_password)
                        .addOnCompleteListener(DangKiActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser user=firebaseAuth.getCurrentUser();
                                    if(user!=null){
                                        postData(str_email,str_password,str_username,str_mobile,user.getUid());
                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(),"Email đã tồn tại",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }else {
                Toast.makeText(getApplicationContext(),"Pass chưa khớp",Toast.LENGTH_LONG).show();;
            }
        }

    }
    private void  postData(String str_email, String str_password, String str_username,String str_mobile,String uid){
        compositeDisposable.add(apiBanHang.dangKi(str_email,str_password,str_username,str_mobile,uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if(userModel.isSuccess()){
                                Utlis.user_current.setEmail(str_email);
                                Utlis.user_current.setPassword(str_password);
                                Intent intent= new Intent(getApplicationContext(), DangNhapActivity.class);
                                startActivity(intent);
                                finish();
                                //Toast.makeText(getApplicationContext(),"Thành Công",Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(getApplicationContext(),userModel.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}