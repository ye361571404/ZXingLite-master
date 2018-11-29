/*
 * Copyright (C) 2018 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.king.zxing.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.king.zxing.Intents;
import com.king.zxing.app.bean.IMEIBean;
import com.king.zxing.app.bean.IMEIBeanDao;
import com.king.zxing.app.bean.OrderBean;
import com.king.zxing.app.helper.DBHelper;
import com.king.zxing.app.util.ExcelUtil;
import com.king.zxing.app.util.UriUtils;
import com.king.zxing.util.CodeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.king.zxing.app.QuickMultiAdapter.TYPE_LEVEL_0;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    private final String TAG = "MainActivity";

    public static final String KEY_TITLE = "key_title";
    public static final String KEY_IS_QR_CODE = "key_code";

    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int REQUEST_CODE_PHOTO = 0X02;

    public static final int RC_CAMERA = 0X01;

    public static final int RC_READ_PHOTO = 0X02;

    private Class<?> cls;
    private String title;
    // 1: 订单号扫描     2: IMEI码扫描
    private int scanType;
    // 当前订单号
    private String currentOrderNum;
    private RecyclerView recyclerView;
    private QuickMultiAdapter mAdapter;
    private Button scanIMEI;
    private List<MultiItemEntity> multiItems;
    private LinkedHashMap<String, List<MultiItemEntity>> imeiBeansByOrderNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }


    /**
     * 创建菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    /**
     * 菜单点击事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_recording:
                // 导出扫描记录到excel
                List<IMEIBean> list = DBHelper.getInstance().getIMEIBeanDao().queryBuilder().orderDesc(IMEIBeanDao.Properties.ScanTime).list();
                exportRecording(list);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        // ToolBarT添加菜单需要先操作这一步
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        scanIMEI = findViewById(R.id.btn4);
    }

    private void initData() {
        imeiBeansByOrderNum = new LinkedHashMap<>();
        multiItems = new ArrayList<>();
        mAdapter = new QuickMultiAdapter(multiItems);
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        // important! setLayoutManager should be called after setAdapter
        recyclerView.setLayoutManager(manager);
        showData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    processData(result);
                    break;
                case REQUEST_CODE_PHOTO:
                    parsePhoto(data);
                    break;
            }
        }
    }




    private void processData(String result) {
        Log.e(TAG, "onActivityResult: 条形码 " + result);
        if (scanType == 1) {
            // 订单号
            currentOrderNum = result;
            scanIMEI.setText("IMEI码扫描(当前订单号:" + currentOrderNum + ")");
        } else if (scanType == 2) {
            // IMEI号
            if (currentOrderNum == null) {
                Toast.makeText(this, "请先扫订单号", Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleDateFormat matter = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            IMEIBean imeiBean = new IMEIBean(currentOrderNum, result, matter.format(new Date()));
            try {
                DBHelper.getInstance().getIMEIBeanDao().insert(imeiBean);
            } catch (SQLiteConstraintException e) {
                e.printStackTrace();
                Toast.makeText(this, "该号码已存在", Toast.LENGTH_SHORT).show();
            }
        }
        showData();
    }




    private void showData() {
        if (!DBHelper.getInstance().isOpenDb()) {
            DBHelper.getInstance().openDB();
        }
        multiItems.clear();
        imeiBeansByOrderNum.clear();

        List<IMEIBean> imeiBeans = DBHelper.getInstance().getIMEIBeanDao().queryBuilder().orderDesc(IMEIBeanDao.Properties.ScanTime).list();
        // 数据转换为适用于列表层级显示
        IMEIBean imeiBean;
        for (int i = 0; i < imeiBeans.size(); i++) {
            imeiBean = imeiBeans.get(i);
            String orderNum = imeiBean.getOrderNum();
            List<MultiItemEntity> imeiList = imeiBeansByOrderNum.get(orderNum);
            if (imeiList == null) {
                imeiList = new ArrayList<>();
                imeiBeansByOrderNum.put(orderNum, imeiList);
            }
            imeiList.add(imeiBean);
        }

        int no = imeiBeans.size();
        Iterator<Map.Entry<String, List<MultiItemEntity>>> iterator = imeiBeansByOrderNum.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MultiItemEntity>> next = iterator.next();
            String orderNum = next.getKey();
            List<MultiItemEntity> imeiBeanList = next.getValue();
            OrderBean orderBean = new OrderBean(orderNum);

            Collections.reverse(imeiBeanList);
            for (int i = imeiBeanList.size() - 1; i >= 0; i--) {
                IMEIBean subItem = (IMEIBean) imeiBeanList.get(i);
                subItem.setNo(no--);
                orderBean.addSubItem(subItem);
            }
            orderBean.setScanTime(orderBean.getSubItems().get(0).getScanTime());
            multiItems.add(orderBean);
        }

        mAdapter.replaceData(multiItems);
        mAdapter.notifyDataSetChanged();
        mAdapter.expandAll();
    }

    private void parsePhoto(Intent data) {
        final String path = UriUtils.INSTANCE.getImagePath(this, data);
        Log.d("Jenly", "path:" + path);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        //异步解析
        asyncThread(new Runnable() {
            @Override
            public void run() {
                final String result = CodeUtils.parseCode(path);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Jenly", "result:" + result);
                        Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private Context getContext() {
        return this;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Some permissions have been granted
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Some permissions have been denied
        // ...
    }

    /**
     * 检测拍摄权限
     */
    @AfterPermissionGranted(RC_CAMERA)
    private void checkCameraPermissions() {
        String[] perms = {Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {//有权限
            startScan(cls, title);
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.permission_camera), RC_CAMERA, perms);
        }
    }

    private void asyncThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * 扫码
     *
     * @param cls
     * @param title
     */
    private void startScan(Class<?> cls, String title) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.in, R.anim.out);
        Intent intent = new Intent(this, cls);
        intent.putExtra(KEY_TITLE, title);
        ActivityCompat.startActivityForResult(this, intent, REQUEST_CODE_SCAN, optionsCompat.toBundle());
    }


    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btn3:
                // 订单号扫描
                scanType = 1;
                this.cls = CustomCaptureActivity.class;
                this.title = ((Button) v).getText().toString();
                checkCameraPermissions();
                break;
            case R.id.btn4:
                // IMEI码扫描
                scanType = 2;
                this.cls = CustomCaptureActivity.class;
                this.title = ((Button) v).getText().toString();
                checkCameraPermissions();
                break;
        }
    }


    /**
     * 导出扫描记录到excel
     * @param recordingList
     */
    public void exportRecording(List<IMEIBean> recordingList) {
        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmssS");
            String dateStr = simpleDateFormat.format(date);
            String fileName = "excel_" + dateStr;
            ExcelUtil.writeExcel(recordingList, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
