package com.king.zxing.app.view;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.king.zxing.app.MainActivity;
import com.king.zxing.app.R;
import com.king.zxing.app.permission.DefaultRationale;
import com.king.zxing.app.permission.PermissionSetting;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import java.util.List;

public class StartActivity extends AppCompatActivity implements Action{

    private Rationale mRationale;
    private PermissionSetting mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initData();
    }

    private void initData() {
        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(this);
        requestPermission(Permission.Group.STORAGE);
    }


    private void requestPermission(String... permissions) {
        AndPermission.with(this)
                .permission(permissions)
                .rationale(mRationale)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        // toast(R.string.successfully);
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .onDenied(this)
                .start();
    }

    protected void toast(@StringRes int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAction(List<String> permissions) {
        toast(R.string.failure);
        if (AndPermission.hasAlwaysDeniedPermission(StartActivity.this, permissions)) {
            mSetting.showSetting(permissions);
        }
    }
}
