package com.tcl.allapp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief 应用权限信息类
 *
 */
public class AppPermission {

    /**
     * List集合返回某APP拥有的权限信息
     * @param context Context 实例
     * @param packageName APP包名
     * @return 返回该APP的权限list
     */
    private List getPermission(Context context, String packageName) {
        List<String> permissionInfoList = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String[] permissions = pi.requestedPermissions;
            if(permissions != null){
                for(String str : permissions){
                    PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(str, 0);
                    permissionInfoList.add(permissionInfo.toString());
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return permissionInfoList;
    }

    /**
     * @brief 更新显示应用权限信息
     * @param context Context实例
     * @param pkgInfo package信息
     * @param listView listview组件
     * @return 成功更新则返回是，否则返回否
     */
    public boolean updatePermission(Context context, PackageInfo pkgInfo, ListView listView) {
        //获取permissionList
        List<String> list0 = new ArrayList<String>();
        list0 = getPermission(context, pkgInfo.packageName);
        int len = list0.size();

        //目标List
        List<String> newList = new ArrayList<String>();

        if(len == 0){
            newList.clear();
            newList.add(" (空)");
        }
        else{
            for(int i = 0; i < len; i++){
                String temp = "";
                String[] str = list0.get(i).split("\\.");
                temp = str[str.length - 1];
                newList.add(i, " " + temp.replace("}", ""));
            }
        }

        //将处理好的permission传到arrayAdapter处
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.listview_text, newList);
        listView.setAdapter(adapter);
        return true;
    }

    /**
     * @brief 返回一个list
     * @param context context实例
     * @param pkgInfo package信息
     * @return list
     */
    public List<String> updatePermission(Context context, PackageInfo pkgInfo) {
        List<String> list0 = new ArrayList<String>();
        list0 = getPermission(context, pkgInfo.packageName);
        return list0;
    }
}
