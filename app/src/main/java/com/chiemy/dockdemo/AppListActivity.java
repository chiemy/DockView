package com.chiemy.dockdemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chiemy.dock.DockMenuActivity;
import com.chiemy.dock.DockMenuView;
import com.chiemy.dock.DockView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: chiemy
 * Date: 9/15/17
 * Description:
 */

public class AppListActivity extends DockMenuActivity {
    private static final String EXTRA_ORIENTATION = "orientation";

    private List<PackageInfo> appList;
    private ImageView logoIv;
    private TextView appInfoTv;
    private int orientation;
    private int itemLayoutResId;

    public static void start(Context context, int orientation) {
        Intent intent = new Intent(context, AppListActivity.class);
        intent.putExtra(EXTRA_ORIENTATION, orientation);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        orientation = getIntent().getIntExtra(EXTRA_ORIENTATION, DockMenuView.BOTTOM_TO_TOP);
        itemLayoutResId = getItemLayoutResId();
        setOrientation(orientation);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano_menu);
        logoIv = (ImageView) findViewById(R.id.iv_logo);
        appInfoTv = (TextView) findViewById(R.id.tv_app_info);

        appList = initAppList();
        setMenuAdapter(new DockView.DockAdapter() {
            @Override
            public View onCreateItemView(DockView parent) {
                return LayoutInflater.from(parent.getContext()).inflate(itemLayoutResId, parent, false);
            }

            @Override
            public void onBindItemView(View itemView, int position) {
                ImageView iv = (ImageView) itemView.findViewById(R.id.iv_icon);
                PackageInfo packageInfo = appList.get(position);
                iv.setImageDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));
            }

            @Override
            public int getItemCount() {
                return appList.size();
            }
        });
    }

    private int getItemLayoutResId() {
        int layoutResId = R.layout.item_bottom;
        switch (orientation) {
            case DockMenuView.BOTTOM_TO_TOP:
                layoutResId = R.layout.item_bottom;
                break;
            case DockMenuView.TOP_TO_BOTTOM:
                layoutResId = R.layout.item_top;
                break;
            case DockMenuView.LEFT_TO_RIGHT:
                layoutResId = R.layout.item_left;
                break;
            case DockMenuView.RIGHT_TO_LEFT:
                layoutResId = R.layout.item_right;
                break;
        }
        return layoutResId;
    }

    @Override
    public void onItemSelected(DockView view, View itemView, int position) {
        super.onItemSelected(view, itemView, position);
        Log.d("chiemy", "onItemSelected: " + position);
        PackageInfo packageInfo = appList.get(position);
        logoIv.setImageDrawable(packageInfo.applicationInfo.loadIcon(getPackageManager()));
        appInfoTv.setText(
                String.format(
                        "%s %s",
                        packageInfo.applicationInfo.loadLabel(getPackageManager()).toString(),
                        packageInfo.versionName
                )
        );
    }

    /**
     * 获取非系统应用信息列表
     */
    private List<PackageInfo> initAppList() {
        PackageManager pm = getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<PackageInfo> appList = new ArrayList<>(packages.size());
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appList.add(packageInfo);
            } else {
                // 系统应用　　　　　　　　
            }
        }
        return appList;
    }

}
