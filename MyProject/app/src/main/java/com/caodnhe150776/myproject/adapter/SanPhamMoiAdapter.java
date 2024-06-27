package com.caodnhe150776.myproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.caodnhe150776.myproject.Interface.ItemClickListener;
import com.caodnhe150776.myproject.R;
import com.caodnhe150776.myproject.activity.ChiTietActivity;
import com.caodnhe150776.myproject.model.SanPhamMoi;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class SanPhamMoiAdapter extends RecyclerView.Adapter<SanPhamMoiAdapter.MyViewHolder> {
    Context context;
    List<SanPhamMoi> array;

    public SanPhamMoiAdapter(Context context, List<SanPhamMoi> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi,parent,false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPhamMoi sanPhamMoi=array.get(position);
        holder.txtten.setText(sanPhamMoi.getTensp());
        // Xử lý nhiều dấu phẩy thập phân tiềm ẩn trong giá
        String giaString = sanPhamMoi.getGiasp() + "Đ";  // Thêm ký hiệu tiền tệ
        try {
            // Thử phân tích thành double (giả sử giá là một số)
            double gia = Double.parseDouble(giaString.replaceAll("[.]", ".?"));  // Thay thế dấu phẩy đơn bằng bộ khớp nhóm chụp (".?") cho dấu thập phân
            holder.txtgia.setText("Giá: " + new DecimalFormat("#,###.##").format(gia) + "Đ");
        } catch (NumberFormatException e) {
            // Xử lý trường hợp phân tích thất bại (ví dụ: nếu giá không phải là số)
            holder.txtgia.setText("Giá: " + giaString);
        }
        Glide.with(context).load(sanPhamMoi.getHinhanh()).into(holder.imghinhanh);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick){
                    //click
                    Intent intent= new Intent(context, ChiTietActivity.class);
                    intent.putExtra("chitiet", (Serializable) sanPhamMoi);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtgia,txtten;
        ImageView imghinhanh;
        private ItemClickListener itemClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtgia=itemView.findViewById(R.id.itemsp_gia);
            txtten=itemView.findViewById(R.id.itemsp_ten);
            imghinhanh=itemView.findViewById(R.id.itemsp_image);
            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
    }
}
