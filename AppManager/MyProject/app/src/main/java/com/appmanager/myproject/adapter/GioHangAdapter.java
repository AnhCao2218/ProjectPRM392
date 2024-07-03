package com.appmanager.myproject.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.appmanager.myproject.Interface.ImageClickListenner;
import com.appmanager.myproject.R;
import com.appmanager.myproject.model.EventBus.TinhTongEvent;
import com.appmanager.myproject.model.GioHang;
import com.appmanager.myproject.utlis.Utlis;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder> {

    Context context;
    List<GioHang> gioHangList;

    public GioHangAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    GioHang gioHang= gioHangList.get(position);
    holder.item_giohang_tensp.setText(gioHang.getTensp());
        holder.item_giohang_soluong.setText(gioHang.getSoluong()+ "");
        holder.item_giohang_giasp.setText(String.valueOf(gioHang.getGiasp()));

        Glide.with(context).load(gioHang.getHinhsp()).into(holder.item_giohang_img);
        long gia= gioHang.getSoluong()* gioHang.getGiasp();
        holder.item_giohang_thanhtien.setText(String.valueOf(gia));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Utlis.mangmuahang.add(gioHang);
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }else {
                    for (int i=0;i<Utlis.mangmuahang.size();i++){
                        if(Utlis.mangmuahang.get(i).getIdsp()==gioHang.getIdsp()){
                            Utlis.mangmuahang.remove(i);
                            EventBus.getDefault().postSticky(new TinhTongEvent());
                        }
                    }
                }
            }
        });
        holder.setListenner(new ImageClickListenner() {
            @Override
            public void onImageClick(View view, int pos, int giatri) {
                if(giatri==1){
                    if(gioHangList.get(pos).getSoluong()>1){
                        int soluongmoi=gioHangList.get(pos).getSoluong()-1;
                        gioHangList.get(pos).setSoluong(soluongmoi);
                        holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluong()+ "");
                        long gia= gioHangList.get(pos).getSoluong()* gioHangList.get(pos).getGiasp();
                        holder.item_giohang_thanhtien.setText(String.valueOf(gia));
                        EventBus.getDefault().postSticky(new TinhTongEvent());
                    }else if(gioHangList.get(pos).getSoluong()==1){
                        AlertDialog.Builder builder= new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("Thông báo");
                        builder.setMessage("Bạn có muốn xoá sản phẩm này khỏi giỏ hàng ");
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utlis.manggiohang.remove(pos);
                                notifyDataSetChanged();
                                EventBus.getDefault().postSticky(new TinhTongEvent());
                            }
                        });
                        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();

                    }
                }else if(giatri==2){
                    if(gioHangList.get(pos).getSoluong()<11){
                        int soluongmoi=gioHangList.get(pos).getSoluong()+1;
                        gioHangList.get(pos).setSoluong(soluongmoi);
                    }
                    holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluong()+ "");
                    long gia= gioHangList.get(pos).getSoluong()* gioHangList.get(pos).getGiasp();
                    holder.item_giohang_thanhtien.setText(String.valueOf(gia));
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_giohang_img,item_giohang_tru,item_giohang_cong;
        TextView item_giohang_tensp,item_giohang_giasp,item_giohang_soluong,item_giohang_thanhtien;
        ImageClickListenner listenner;
        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_giohang_img=itemView.findViewById(R.id.item_giohang_img);
            item_giohang_tensp=itemView.findViewById(R.id.item_giohang_tensp);
            item_giohang_giasp=itemView.findViewById(R.id.item_giohang_giasp);
            item_giohang_soluong=itemView.findViewById(R.id.item_giohang_soluong);
            item_giohang_thanhtien=itemView.findViewById(R.id.item_giohang_thanhtien);
            item_giohang_tru=itemView.findViewById(R.id.item_giohang_tru);
            item_giohang_cong=itemView.findViewById(R.id.item_giohang_cong);

            item_giohang_cong.setOnClickListener(this);
            item_giohang_tru.setOnClickListener(this);
            checkBox=itemView.findViewById(R.id.item_giohang_checkbox);
        }

        public void setListenner(ImageClickListenner listenner) {
            this.listenner = listenner;
        }

        @Override
        public void onClick(View v) {
            if(v ==item_giohang_tru){
                listenner.onImageClick(v,getAdapterPosition(),1);
            }else if(v==item_giohang_cong){
                listenner.onImageClick(v,getAdapterPosition(),2);
            }
        }
    }
}
