package com.yuqun.main.comm;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;


public class ExtractorThread extends HandlerThread {
    private static final String LOADER_THREAD_NAME = "NVSExtractorThread";

    private static volatile ExtractorThread extractorThread;

    private Handler extractorHandler;

    private volatile Handler mainHandler;

    private ExtractorThread() {
        super(LOADER_THREAD_NAME);
    }

    public static ExtractorThread getInstance() {
        if (null == extractorThread) {

            startup();
        }
        return extractorThread;
    }

    /**
     * 应用程序初始化时调用
     */
    public static void startup() {
        shutdown();

        extractorThread = new ExtractorThread();
        extractorThread.start();
        extractorThread.createHandler();
    }

    /**
     * 应用程序退出时调用
     */
    public static void shutdown() {
        if (extractorThread != null) {
            extractorThread.destroyHandler();
            extractorThread.quit();
            extractorThread = null;
        }
    }

    private void createHandler() {
        extractorHandler = new Handler(this.getLooper());
    }

    private void destroyHandler() {
        mainHandler = null;
        extractorHandler = null;
    }

    public Handler getMainHandler() {
        if (null == mainHandler) {
            mainHandler = new Handler(Looper.getMainLooper());
        }

        return mainHandler;
    }

    public void runOnUiThread(Runnable runnable) {
        getMainHandler().post(runnable);
    }

    public Handler getAsyncHandler() {
        return extractorHandler;
    }

    public void runOnBkThread(Runnable runnable) {
        if (null != extractorHandler) {
            extractorHandler.post(runnable);
        }
    }
}
