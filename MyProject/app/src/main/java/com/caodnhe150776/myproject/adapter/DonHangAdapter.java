package com.caodnhe150776.myproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.caodnhe150776.myproject.R;
import com.caodnhe150776.myproject.model.DonHang;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {

    Context context;
    List<DonHang>listdonhang;
    private RecyclerView.RecycledViewPool viewPool= new RecyclerView.RecycledViewPool();

    public DonHangAdapter(Context context, List<DonHang> listdonhang) {
        this.context = context;
        this.listdonhang = listdonhang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang=listdonhang.get(position);
        holder.txtdonhang.setText("Đơn Hàng: "+donHang.getId());
        LinearLayoutManager layoutManager= new LinearLayoutManager(
                holder.reChitiet.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        ChiTietAdapter chiTietAdapter= new ChiTietAdapter(context,donHang.getItem());
        holder.reChitiet.setLayoutManager(layoutManager);
        holder.reChitiet.setAdapter(chiTietAdapter);
        holder.reChitiet.setRecycledViewPool(viewPool);


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txtdonhang;
        RecyclerView reChitiet;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtdonhang=itemView.findViewById(R.id.iddonhang);
            reChitiet=itemView.findViewById(R.id.recyclerview_chitiet);
        }
    }
}
