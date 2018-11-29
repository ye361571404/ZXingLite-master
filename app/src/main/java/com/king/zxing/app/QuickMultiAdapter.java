package com.king.zxing.app;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.king.zxing.app.bean.IMEIBean;
import com.king.zxing.app.bean.OrderBean;
import com.king.zxing.app.helper.DBHelper;

import java.util.List;

/**
 * on 2018/11/27
 */
public class QuickMultiAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity,BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public QuickMultiAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_expandable_lv0);
        addItemType(TYPE_LEVEL_1, R.layout.item_expandable_lv1);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {

        switch (helper.getItemViewType()){
            case TYPE_LEVEL_0:
                // 订单号
                final OrderBean orderBean = (OrderBean) item;
                helper.setText(R.id.title, "订单号:" + orderBean.getOrderNum());
                // helper.setText(R.id.sub_title, "扫描时间:" + orderBean.getScanTime());

                helper.setImageResource(R.id.iv, orderBean.isExpanded() ? R.mipmap.arrow_b : R.mipmap.arrow_r);
                final int pos = helper.getAdapterPosition();
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (orderBean.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = helper.getAdapterPosition();
                        remove(pos);
                        return true;
                    }
                });
                break;
            case TYPE_LEVEL_1:
                // IMEI号
                final IMEIBean imeiBean = (IMEIBean) item;
                helper.setText(R.id.title," IMEI号:" + imeiBean.getImeiNum() + "  序号:" + imeiBean.getNo());
                helper.setText(R.id.sub_title, "扫描时间:" + imeiBean.getScanTime());
                helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = helper.getAdapterPosition();
                        remove(pos);
                        DBHelper.getInstance().getIMEIBeanDao().delete(imeiBean);
                        return true;
                    }
                });
                break;
        }
    }

























}
