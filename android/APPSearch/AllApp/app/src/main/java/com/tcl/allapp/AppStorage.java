package com.tcl.allapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.usage.StorageStats;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.Process;
import android.text.format.Formatter;
import android.app.usage.StorageStatsManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @brief 获取应用存储信息
 *
 */
public class AppStorage {

    /**
     * @brief 应用存储信息结构
     *
     */
    public class StorageInfo implements Parcelable {
        String apkPath;         /**< 安装包路径 */
        String volumeUuid;      /**< 目标存储设备Uuid */
        String volumeDesr;      /**< 目标设备设备描述 */
        long totalSize;         /**< 总存储空间，bytes */
        long freeSize;          /**< 空余存储空间，bytes */
        long appSize;           /**< 应用存储大小，bytes */
        long apkSize;           /**< Apk大小，bytes */
        long cacheSize;         /**< 应用Cache大小，bytes */
        long dataSize;          /**< 应用数据大小，bytes */

        /**
         * @brief 构造函数
         *
         */
        public StorageInfo() {
            this.apkPath = "";
            this.volumeUuid = "";
            this.volumeDesr = "";
            this.totalSize = 0;
            this.freeSize = 0;
            this.appSize = 0;
            this.apkSize = 0;
            this.cacheSize = 0;
            this.dataSize = 0;
        }

        /**
         * @brief Pracel构造函数
         *
         * @param in Pracel对象
         */
        public StorageInfo(Parcel in) {
            this.apkPath = in.readString();
            this.volumeUuid = in.readString();
            this.volumeDesr = in.readString();
            this.totalSize = in.readLong();
            this.freeSize = in.readLong();
            this.appSize = in.readLong();
            this.cacheSize = in.readLong();
            this.dataSize = in.readLong();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * @brief 写入Parcel，以便Bundle传递数据
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.apkPath);
            dest.writeString(this.volumeUuid);
            dest.writeString(this.volumeDesr);
            dest.writeLong(this.totalSize);
            dest.writeLong(this.freeSize);
            dest.writeLong(this.appSize);
            dest.writeLong(this.apkSize);
            dest.writeLong(this.cacheSize);
            dest.writeLong(this.dataSize);
        }

        public final Parcelable.Creator<StorageInfo> CREATOR = new Parcelable.Creator<StorageInfo>() {
            public StorageInfo createFromParcel(Parcel in) {
                return new StorageInfo(in);
            }

            public StorageInfo[] newArray(int size) {
                return new StorageInfo[size];
            }
        };

        /**
         * @brief  生成应用存储信息
         *
         * @return 返回存储信息
         */
        public String toString(Context context) {
            return    "  App存储：" + Formatter.formatShortFileSize(context, this.appSize) + "\n"
                    + "  Apk大小：" + Formatter.formatShortFileSize(context, this.apkSize) + "\n"
                    + "  App缓存：" + Formatter.formatShortFileSize(context, this.cacheSize) + "\n"
                    + "  App数据：" + Formatter.formatShortFileSize(context, this.dataSize) + "\n"
                    + "  Apk路径：\n" + this.apkPath;
        }
    }

    /**
     * @brief 获取应用存储的线程
     *
     * 获取存储信息是耗时操作，需要在线程中实现
     *
     */
    private class ThreadStorage extends Thread {
        public Context context;
        public PackageInfo pkgInfo;
        public AllApp.StorageHandler handler;

        @TargetApi(Build.VERSION_CODES.O)
        @Override
        public void run() {
            super.run();
            @SuppressLint("WrongConstant")final StorageStatsManager storageStatsManager
                    = (StorageStatsManager) this.context.getSystemService(Context.STORAGE_STATS_SERVICE);
            final StorageManager storageManager
                    = (StorageManager) this.context.getSystemService(Context.STORAGE_SERVICE);
            final List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();
            final UserHandle user = Process.myUserHandle();
            final UUID uuid = StorageManager.UUID_DEFAULT;
            try {
                final StorageStats ss = storageStatsManager.queryStatsForPackage(uuid, this.pkgInfo.packageName, user);
                StorageInfo si = new StorageInfo();
                si.apkPath = this.context.getPackageManager().getApplicationInfo(this.pkgInfo.packageName, 0).sourceDir;
                si.volumeUuid = uuid.toString();
                si.totalSize = storageStatsManager.getTotalBytes(uuid);
                si.freeSize = storageStatsManager.getFreeBytes(uuid);
                si.appSize = ss.getAppBytes();
                si.apkSize = Integer.valueOf((int)new File(si.apkPath).length());
                si.cacheSize = ss.getCacheBytes();
                si.dataSize = ss.getDataBytes();

                // 传递存储数据
                Bundle bundle = new Bundle();
                bundle.putParcelable("StorageInfo", si);
                Message msg = new Message();
                msg.what = 0;
                msg.setData(bundle);
                handler.sendMessage(msg);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
            List<StorageInfo> storageInfoList = new ArrayList<StorageInfo>();
            for (StorageVolume sv : storageVolumeList) {
                // 设备需要处理挂载状态
                if (!sv.getState().equals(Environment.MEDIA_MOUNTED))
                    continue;
                final String uuidStr = sv.getUuid();
                if (uuidStr == null)
                    continue;;
                final UUID uuid = UUID.fromString(uuidStr);
            }
            */
        }
    }

    /**
     * @brief 获取应用存储信息
     *
     * @param context 上下文环境
     * @param pkgInfo 应用包信息
     * @param handler 用于子线程传递消息的Handler
     * @return
     * @retval None
     */
    public boolean updateStorage(Context context, PackageInfo pkgInfo, AllApp.StorageHandler handler) {
        ThreadStorage ts = new ThreadStorage();
        ts.context = context;
        ts.pkgInfo = pkgInfo;
        ts.handler = handler;
        ts.start();
        return true;
    }
}
