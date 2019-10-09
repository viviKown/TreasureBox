package com.tcl.allapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Debug;
import android.support.v4.media.session.ParcelableVolumeInfo;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


/**
 * @brief 应用Cpu和Pid信息类
 *
 */
public class AppCpu {

    /**
     * @brief Cpu信息结构
     *
     */
    public class CpuInfo {
        boolean isRunning;  /**< 是否正在运行 */
        int pid;            /**< 进程号 */
        int totalMem;       /**< 总内存 */
        int swapMem;        /**< 虚拟内存 */
        int javaMem;        /**< Java对象 */
        int nativeMem;      /**< C/C++层 */
        int statckMem;      /**< 线程/方法栈 */
        int graphicsMem;    /**< 为图像显示准备内存(非GPU内存) */
        int codeMem;        /**< so库内存 */
        int othersMem;      /**< 未归类内存 */
        String cpu;         /**< Cpu占用百分比 */
    }

    /**
     * @brief 更新CpuInfo实例对象信息
     *
     * @param context 上下文环境
     * @param pkgInfo 应用信息实例对象
     * @param cpuView  CPU和PID信息显示界面
     * @param memView   内存信息显示页面
     * @return
     * @retval None
     */
    public boolean updateCpu(Context context, PackageInfo pkgInfo, TextView cpuView, TextView memView) {
        // 获取Pid
        CpuInfo cpuInfo = new CpuInfo();
        cpuInfo.isRunning = getPid(context, pkgInfo, cpuInfo);
        if (!cpuInfo.isRunning)
        {
            // 显示未运行
            this.updateView(context, cpuView, memView, cpuInfo);
            return false;
        }
        // 获取内存和CPU
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{cpuInfo.pid})[0];
        // 获取进程占内存
        cpuInfo.javaMem = AppCpu.convertToInt(memoryInfo.getMemoryStat("summary.java-heap"), 0);
        cpuInfo.nativeMem = AppCpu.convertToInt(memoryInfo.getMemoryStat("summary.native-heap"), 0);
        cpuInfo.statckMem = AppCpu.convertToInt(memoryInfo.getMemoryStat("summary.stack"), 0);
        cpuInfo.swapMem = AppCpu.convertToInt(memoryInfo.getMemoryStat("summary.total-swap"), 0);
        cpuInfo.graphicsMem = AppCpu.convertToInt(memoryInfo.getMemoryStat("summary.graphics"), 0);
        cpuInfo.codeMem = AppCpu.convertToInt(memoryInfo.getMemoryStat("summary.code"), 0);
        cpuInfo.othersMem = AppCpu.convertToInt(memoryInfo.getMemoryStat("summary.system"), 0);
        cpuInfo.totalMem = cpuInfo.javaMem + cpuInfo.nativeMem + cpuInfo.statckMem
                + cpuInfo.graphicsMem + cpuInfo.codeMem + cpuInfo.othersMem;
        // 获取Pid
        cpuInfo.cpu = getCpuByCmd(String.valueOf(cpuInfo.pid));
        if (cpuInfo.cpu == null)
            cpuInfo.isRunning = false;
        this.updateView(context, cpuView, memView, cpuInfo);
        return true;
    }

    /**
     * @brief 获取应用是否运行的状态，如果正在运行将将pid赋给CpuInfo实例
     *
     * @param context 上下文环境
     * @param  pkgInfo 应用信息实例对象
     * @param cpuInfo 应用Cpu和Pid信息类实例
     * @return
     * @retval None
     */
    private boolean getPid(Context context, PackageInfo pkgInfo, CpuInfo cpuInfo) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessList = am.getRunningAppProcesses();
        // 查找Pid
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            String processName = appProcessInfo.processName;
            if (processName.equals(pkgInfo.packageName)) {
                cpuInfo.pid = appProcessInfo.pid;
                return true;
            }
        }
        return false;
    }

    /**
     * @brief  将CPU 内存对象中的信息展示到界面上
     *
     * @param  context 上下文环境
     * @param cpuView CPU和PID信息显示界面
     * @param memView 内存信息显示界面
     * @param cpuInfo CPU内存类对象
     * @return
     * @retval None
     */
    private void updateView(Context context, TextView cpuView, TextView memView, CpuInfo cpuInfo) {
        if (cpuInfo.isRunning) {
            cpuView.setText(
                    ("  PID进程号：" + cpuInfo.pid + "\n"
                    + "  CPU占用率：" + cpuInfo.cpu + "%"));
            memView.setText(
                    ("  总内存：" + AppCpu.formatByte(cpuInfo.totalMem) + "\n"
                    + "  Java内存：" + AppCpu.formatByte(cpuInfo.javaMem) + "\n"
                    + "  Native内存：" + AppCpu.formatByte(cpuInfo.nativeMem) + "\n"
                    + "  Stack内存：" + AppCpu.formatByte(cpuInfo.statckMem) + "\n"
                    + "  Graphics内存：" + AppCpu.formatByte(cpuInfo.graphicsMem) + "\n"
                    + "  Code内存：" + AppCpu.formatByte(cpuInfo.codeMem) + "\n"
                    + "  Others内存：" + AppCpu.formatByte(cpuInfo.othersMem) + "\n"
                    + "  Swap内存：" + AppCpu.formatByte(cpuInfo.swapMem)));
        } else {
            cpuView.setText("  应用未在运行！");
            memView.setText("  应用未在运行！");
        }
    }

    /**
     * @brief 获取CPU占用率
     *
     * @param  pid 需要获取应用的进程号

     * @return
     * @retval 返回当前PID进程的CPU占用率
     */
    private static String getCpuByCmd(String pid){
        Process process;
//        String cmd = "top -p " + pid +" -n 2";
        String cmd = "ps -o PID,%CPU -p " + pid;
        try{
            process = Runtime.getRuntime().exec(new String[]{"sh","-c",cmd});
            BufferedReader br = new BufferedReader(new InputStreamReader((process).getInputStream()));
            String line = "";
            while ((line = br.readLine()) != null){
                String[] resp = line.trim().split("\\s+");
                if(resp.length == 2 && pid.equals(resp[0])){
                    return resp[1];
                }
            }
            try {
                process.waitFor();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @brief  格式转化工具，将Object对象转化为整数
     *
     * @param  value  任意对象
     * @param  defaultValue 转化失败时返回默认值
     * @return int
     * @retval  NONE
     */
    private static int convertToInt(Object value, int defaultValue){
        if (value == null || "".equals(value.toString().trim())){
            return defaultValue;
        }
        try {
            return Integer.valueOf(value.toString());
        }catch (Exception e){
            try {
                return Integer.valueOf(String.valueOf(value));
            }catch (Exception e1) {
                try {
                    return Double.valueOf(value.toString()).intValue();
                }catch (Exception e2){
                    return defaultValue;
                }
            }
        }
    }

    /**
     * @brief  格式转化工具，将整数自动转化为对应内存单位大小的字符串
     *
     * @param  data  整数
     * @return int
     * @retval  NONE
     */
    private static String formatByte(int data){
        DecimalFormat format = new DecimalFormat("##.##");
        if(data < 1024){
            return data+"KB";
        }else if(data < 1024 * 1024){
            return format.format(data/1024f) +"MB";
        }else if(data < 1024 * 1024 * 1024){
            return format.format(data/1024f/1024f) +"GB";
        }else{
            return "超出统计范围";
        }
    }
}

