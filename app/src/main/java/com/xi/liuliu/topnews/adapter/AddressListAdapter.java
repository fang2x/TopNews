package com.xi.liuliu.topnews.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xi.liuliu.topnews.R;
import com.xi.liuliu.topnews.bean.Address;

import java.util.ArrayList;

/**
 * Created by zhangxb171 on 2017/9/6.
 */

public class AddressListAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private static final String TAG = "AddressListAdapter";
    private static final int TYPE_NOT_SHOW_ADDRESS = 0;
    private static final int TYPE_SHOW_ADDRESS = 1;
    private ArrayList<Address> mAddressList;
    private OnItemClickListener mOnItemClickListener;

    public AddressListAdapter(ArrayList<Address> list) {
        this.mAddressList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SHOW_ADDRESS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_list, parent, false);
            view.setOnClickListener(this);
            AddressListHolder holder = new AddressListHolder(view);
            holder.addressName = (TextView) view.findViewById(R.id.address_name_item_address_list);
            holder.addressNumber = (TextView) view.findViewById(R.id.address_number_item_address_list);
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_not_show, parent, false);
            view.setOnClickListener(this);
            AddressNoHolder holder = new AddressNoHolder(view);
            holder.textView = (TextView) view.findViewById(R.id.address_not_show);
            return holder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        if (getItemViewType(position) == TYPE_SHOW_ADDRESS) {
            AddressListHolder addressListHolder = (AddressListHolder) holder;
            if (mAddressList != null && mAddressList.size() > 0) {
                String addressName = mAddressList.get(position - 1).getName();
                String addressNumber = mAddressList.get(position - 1).getNumber();
                addressListHolder.addressName.setText(addressName);
                addressListHolder.addressNumber.setText(addressNumber);

            }
        }
    }

    @Override
    public int getItemCount() {
        return mAddressList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_NOT_SHOW_ADDRESS;
        } else {
            return TYPE_SHOW_ADDRESS;
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    private static class AddressListHolder extends RecyclerView.ViewHolder {
        TextView addressName;
        TextView addressNumber;
        View addressItemView;

        public AddressListHolder(View itemView) {
            super(itemView);
            addressItemView = itemView;
        }
    }

    private static class AddressNoHolder extends RecyclerView.ViewHolder {
        TextView textView;
        View view;

        public AddressNoHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}