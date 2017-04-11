package com.feicuiedu.treasure_20170327.treasure.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.feicuiedu.treasure_20170327.custom.TreasureView;
import com.feicuiedu.treasure_20170327.treasure.Treasure;
import com.feicuiedu.treasure_20170327.treasure.detail.TreasureDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gqq on 2017/4/6.
 */
// RecyclerView的适配器
public class TreasureListAdapter extends RecyclerView.Adapter<TreasureListAdapter.MyViewHolder> {

    private List<Treasure> data = new ArrayList<>();

    // 添加数据的方法
    public void addItemData(List<Treasure> list){
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    // 创建ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TreasureView treasureView = new TreasureView(parent.getContext());
        return new MyViewHolder(treasureView);
    }

    // 数据的绑定
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // 数据的绑定
        final Treasure treasure = data.get(position);
        holder.mTreasureView.bindTreasure(treasure);

        // 点击事件
        holder.mTreasureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 点击卡片的时候直接跳转到宝藏详情页
                TreasureDetailActivity.open(v.getContext(), treasure);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TreasureView mTreasureView;

        public MyViewHolder(TreasureView itemView) {
            super(itemView);
            this.mTreasureView = itemView;
        }
    }

    public interface OnItemClickListener {

        void onItemClick();

        void onItemLongClick();
    }

}
