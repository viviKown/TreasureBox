package com.tcl.allapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

/**
 * @brief 监听系统应用的安装与卸载
 *
 */
public class AppListening extends BroadcastReceiver {
    private final AllApp aa;

    public AppListening(AllApp aa) {
        this.aa = aa;
    }


    /**
     * @brief 应用安装和卸载的监听函数
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String pkgName = intent.getData().getSchemeSpecificPart();
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            this.aa.stopTimer();
            this.aa.updateAppList();
            Toast.makeText(context, "应用\""+ pkgName + "\"被安装！", Toast.LENGTH_LONG).show();
        }
        else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            this.aa.stopTimer();
            this.aa.updateAppList();
            Toast.makeText(context, "应用\""+ pkgName + "\"被卸载！", Toast.LENGTH_LONG).show();
        }
    }
}
