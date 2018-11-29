package com.king.zxing.app.bean;

import android.support.annotation.NonNull;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.king.zxing.app.QuickMultiAdapter.TYPE_LEVEL_0;

/**
 * 订单
 * on 2018/11/27
 */
public class OrderBean extends AbstractExpandableItem<IMEIBean> implements MultiItemEntity,Comparable<OrderBean>{


    // 订单号
    private String orderNum;
    // 扫描时间
    private String scanTime;

    private SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public OrderBean(String orderNum) {
        this.orderNum = orderNum;
    }

    public OrderBean(String orderNum, String date) {
        this.orderNum = orderNum;
        this.scanTime = date;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public int getItemType() {
        return TYPE_LEVEL_0;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int compareTo(@NonNull OrderBean o) {
        Long i = 0l;
        try {
            i = matter.parse(this.scanTime).getTime() - matter.parse(o.getScanTime()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return i.intValue();
    }
}
