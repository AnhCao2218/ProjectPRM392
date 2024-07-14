package com.appmanager.myproject.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appmanager.myproject.R;
import com.appmanager.myproject.databinding.ActivityThemspBinding;
import com.appmanager.myproject.model.MessageModel;
import com.appmanager.myproject.model.SanPhamMoi;
import com.appmanager.myproject.retrofit.ApiBanHang;
import com.appmanager.myproject.retrofit.RetrofitCilient;
import com.appmanager.myproject.utlis.Utlis;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemSPActivity extends AppCompatActivity {

    Spinner spinner;
    int loai=0;
    ActivityThemspBinding binding;
    ApiBanHang apiBanHang;
    String mediaPath;
    SanPhamMoi spSua;
    boolean flag= false;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_themsp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiBanHang= RetrofitCilient.getInstance(Utlis.BASE_URL).create(ApiBanHang.class);
        binding=ActivityThemspBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        spinner=findViewById(R.id.spiner_loai);
        initData();
        Intent intent= getIntent();
        spSua= (SanPhamMoi) intent.getSerializableExtra("Sửa");

        if(spSua==null){
            flag=false;
        }else {
            flag=true;
            binding.btnthem.setText("Sửa sản phẩm");
            //show data
            binding.mota.setText(spSua.getMota());
            binding.giasp.setText(spSua.getGiasp());
            binding.tensp.setText(spSua.getTensp());
            binding.hinhanh.setText(spSua.getHinhanh());
            binding.spinerLoai.setSelection(spSua.getLoaisp());

        }


    }

    private void initData() {
        List<String> stringList= new ArrayList<>();
        stringList.add("Vui lòng chọn loại: ");
        stringList.add("Điện Thoại");
        stringList.add("Laptop");
        ArrayAdapter<String> adapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,stringList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loai = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag==false){
                    themsanpham();
                }else {
                    suaSanPham();
                }

            }
        });
        binding.imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ThemSPActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
    }

    private void suaSanPham() {
        String str_tensp= binding.tensp.getText().toString().trim();
        String str_giasp=binding.giasp.getText().toString().trim();
        String str_mota=binding.mota.getText().toString().trim();
        String str_hinhanh=binding.hinhanh.getText().toString().trim();
        if(TextUtils.isEmpty(str_tensp)|| TextUtils.isEmpty(str_giasp)||TextUtils.isEmpty(str_mota)||TextUtils.isEmpty(str_hinhanh)||loai==0){
            Toast.makeText(getApplicationContext(),"Vui lòng nhạp đủ thông tin",Toast.LENGTH_LONG).show();
        }else {
            compositeDisposable.add(apiBanHang.updatesp(str_tensp,str_giasp,str_hinhanh,str_mota,loai,spSua.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if(messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                            }
                    ));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mediaPath=data.getDataString();
        uploadFile();
        Log.d("log", "onActivityResult: "+mediaPath);
    }

    private void themsanpham() {
        String str_tensp= binding.tensp.getText().toString().trim();
        String str_giasp=binding.giasp.getText().toString().trim();
        String str_mota=binding.mota.getText().toString().trim();
        String str_hinhanh=binding.hinhanh.getText().toString().trim();
        if(TextUtils.isEmpty(str_tensp)|| TextUtils.isEmpty(str_giasp)||TextUtils.isEmpty(str_mota)||TextUtils.isEmpty(str_hinhanh)||loai==0){
            Toast.makeText(getApplicationContext(),"Vui lòng nhạp ủ thông tin",Toast.LENGTH_LONG).show();
        }else {
            compositeDisposable.add(apiBanHang.insertsp(str_tensp,str_giasp,str_hinhanh,str_mota,(loai-1))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if(messageModel.isSuccess()){
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                                }

                            },
                            throwable -> {
                                Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                            }
                    ));
        }
    }
    private  String getMediaPath(Uri uri){
        String result;
        Cursor cursor= getContentResolver().query(uri,null,null,null,null);
        if (cursor==null){
            result=uri.getPath();
        }else {
            cursor.moveToFirst();
            int index=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }
    private void uploadFile() {
        Uri uri=Uri.parse(mediaPath);
        File file = new File(getMediaPath(uri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());


        Call<MessageModel> call = apiBanHang.uploadFile(fileToUpload);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                MessageModel serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isSuccess()) {
                        binding.hinhanh.setText(serverResponse.getName());
//                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(),Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), serverResponse.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    assert serverResponse != null;
                    Log.v("Response", serverResponse.toString());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("log",t.getMessage());
            }
        });
    }
    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}