package com.caodnhe150776.myproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caodnhe150776.myproject.R;
import com.caodnhe150776.myproject.model.LoaiSp;

import java.util.List;

public class LoaiSPAdapter extends BaseAdapter {
    List<LoaiSp> array;
    Context context;

    public LoaiSPAdapter( Context context,List<LoaiSp> array) {
        this.array = array;
        this.context = context;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    public class ViewHolder{
        TextView textsp;
        ImageView imghinhanh;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            viewHolder= new ViewHolder();
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =layoutInflater.inflate(R.layout.item_sanpham,null);
            viewHolder.textsp= convertView.findViewById(R.id.item_tensp);
            viewHolder.imghinhanh= convertView.findViewById(R.id.item_image);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) convertView.getTag();

        }
        viewHolder.textsp.setText(array.get(position).getTensanpham());
        Glide.with(context).load(array.get(position).getImg()).into(viewHolder.imghinhanh);
        return convertView;
    }
}
