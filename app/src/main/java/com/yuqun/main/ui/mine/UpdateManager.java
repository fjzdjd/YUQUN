package com.yuqun.main.ui.mine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.yuqun.main.R;
import com.yuqun.main.comm.CommonData;
import com.yuqun.main.net.HttpRequestUtil;
import com.yuqun.main.net.request.IRequestAction;
import com.yuqun.main.ui.model.VersionModel;
import com.yuqun.main.utils.JsonUtil;


/**
 * 版本更新管理类
 *
 * @author gqy
 */
public class UpdateManager {
    /**
     * 下载
     */
    private static final int DOWNLOAD = 1;
    /**
     * 下载完成
     */
    private static final int DOWNLOAD_FINISH = 2;

    /**
     * 保存路径
     */
    private String mSavePath;
    /**
     * 下载进度
     */
    private int progress;
    /**
     * 是否点击取消下载
     */
    private boolean cancelUpdate = false;

    private Context mContext;
    /**
     * 下载进度条
     */
    private ProgressBar mProgress;
    /**
     * 是否下载提示框
     */
    private Dialog mDownloadDialog;
    /**
     * 当前版本 号
     */
    private int currentCode;
    /**
     * 项目包名
     */
    private String packName;

    /**
     * 新版本包下载地址
     */
    private String url_downLoadPack = "";
    private String url_downLoadPack_real = "";

    private List<VersionModel> mVersionList = new ArrayList<VersionModel>();
    private String versionNum;
    private String isNeed = "1";
    private String downloadUrl;

    public UpdateManager(Context mContext) {
        this.mContext = mContext;
    }

    private Handler Versionhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonData.HTTP_HANDLE_FAILE:
                    Toast.makeText(mContext, "获取版本信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case CommonData.HTTP_HANDLE_SUCCESS:
                    String json = msg.obj.toString();
                    mVersionList = JsonUtil.parseFromJsonToList(json, VersionModel.class);
                    System.out.println("bijiaList=" + mVersionList.toString());
                    isNeed = mVersionList.get(0).getAndroidIsNeed();
                    versionNum = mVersionList.get(0).getAndroidVersonNum();
                    downloadUrl = mVersionList.get(0).getAndroidPath();
                    // 获取当前项目的版本号
                    try {
                        packName = CommonData.PACKNAME;
                        currentCode = mContext.getPackageManager().getPackageInfo(packName, 0).versionCode;
                        if (Integer.parseInt(versionNum) > currentCode) {
                            showNoticeDialog();
                        } else {
                            if (CommonData.isUpdate == false) {
                            /*从检测按钮进来的才显示，首页判断不显示*/
                                Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_SHORT).show();
                            } else {

                            }
                        }

                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    break;
            }
        }

        ;
    };



    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD:
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    installApk();
                    break;
                default:
                    break;
            }
        }

        ;
    };



    /**
     * 判断是否更新
     */
    public void isupdate() throws NameNotFoundException {
        HttpRequestUtil.sendHttpPostCommonRequest(IRequestAction.getVersionForAndroid, null, Versionhandler);
    }

    /**
     * 显示提示更新的dialog
     */
    private void showNoticeDialog() {
        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        if (isNeed.equals("1"))
            builder.setCancelable(false);
        else
            builder.setCancelable(true);
        builder.setPositiveButton(R.string.soft_update_updatebtn,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        showDownloadDialog();
                    }
                });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * 显示下载进度条
     */
    private void showDownloadDialog() {
        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_updating);
        if (isNeed.equals("1"))
            builder.setCancelable(false);
        else
            builder.setCancelable(true);
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        builder.setView(v);
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        new downloadApkThread().start();
    }

    /**
     * 从服务器上下载新包
     *
     * @author gqy
     */

    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    String sdpath = Environment.getExternalStorageDirectory()
                            + "/";
                    mSavePath = sdpath + "download";
                    // url需要访问接口获取
                    URL url = new URL(downloadUrl);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "yuqun.apk");
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    byte buf[] = new byte[1024];
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mDownloadDialog.dismiss();
        }
    }

    ;

    /**
     * 安装APK包
     */
    private void installApk() {
        File apkfile = new File(mSavePath, "yuqun.apk");
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}
