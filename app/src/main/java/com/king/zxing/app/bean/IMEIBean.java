package com.king.zxing.app.bean;

import android.support.annotation.NonNull;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.king.zxing.app.QuickMultiAdapter;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * IMEI号
 * on 2018/11/27
 */
@Entity
public class IMEIBean implements MultiItemEntity{

    @Id(autoincrement = true)
    private Long id;
    // 序号
    @Transient
    private int no;
    // 订单号
    private String orderNum;
    // IMEI号
    @Index(unique = true)
    private String imeiNum;
    // 扫描时间
    private String scanTime;
    @Transient
    private SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    public IMEIBean(String orderNum, String imeiNum, String scanTime) {
        this.orderNum = orderNum;
        this.imeiNum = imeiNum;
        this.scanTime = scanTime;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getScanTime() {
        return scanTime;
    }

    public void setScanTime(String scanTime) {
        this.scanTime = scanTime;
    }

    public IMEIBean(String imeiNum) {
        this.imeiNum = imeiNum;
    }

    @Generated(hash = 1905938047)
    public IMEIBean(Long id, String orderNum, String imeiNum, String scanTime) {
        this.id = id;
        this.orderNum = orderNum;
        this.imeiNum = imeiNum;
        this.scanTime = scanTime;
    }

    @Generated(hash = 1492904075)
    public IMEIBean() {
    }

    public String getImeiNum() {
        return imeiNum;
    }

    public void setImeiNum(String imeiNum) {
        this.imeiNum = imeiNum;
    }

    @Override
    public int getItemType() {
        return QuickMultiAdapter.TYPE_LEVEL_1;
    }

   /* @Override
    public int compareTo(@NonNull IMEIBean o) {
        Long i = 0l;
        try {
            // i =  matter.parse(o.getScanTime()).getTime() - matter.parse(this.scanTime).getTime();
            i = matter.parse(this.scanTime).getTime() - matter.parse(o.getScanTime()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return i.intValue();
    }*/
}
