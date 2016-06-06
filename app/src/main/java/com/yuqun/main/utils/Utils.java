package com.yuqun.main.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.yuqun.main.app.YuQunApplication;
import com.yuqun.main.comm.CommonData;

import junit.framework.Assert;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Utils {
    private static final String EMPTY_IP_ADDRESS = "0.0.0.0";


    /****
     *
     * 移动: 2G号段(GSM网络)有139,138,137,136,135,134(0-8),159,158,152,151,150
     * 3G号段(TD-SCDMA网络)有157,188,187 147是移动TD上网卡专用号段. 联通:
     * 2G号段(GSM网络)有130,131,132,155,156 3G号段(WCDMA网络)有186,185 电信:
     * 2G号段(CDMA网络)有133,153 3G号段(CDMA网络)有189,180
     *
     */
    /**
     * 根据jid获取用户�?
     */
    /*
     * public static String getJidToUsername(String jid) { return
	 * jid.split("@")[0]; }
	 *
	 * public static String getUserNameToJid(String username) { return username
	 * + "@";// + XmppConnection.SERVER_NAME; }
	 */

    private static final int INT_LENGTH = 9;
    private static final String TAG = "SDK_Sample.Util";
    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    /***
     * 手机号码验证（严格）
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * md5加密 返回32位加密的结果
     *
     * @param src
     * @return
     * @see [类�?�类#方法、类#成员]
     */
    public static String md5Encode(String src) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            md.update(src.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte b[] = md.digest();
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

    /**
     * �?查网络连�?
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        if (null == context) {
            return null;
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info;
    }

    public static WifiInfo getWifiInfo(Context context) {
        if (null == context) {
            return null;
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo;
    }

    /**
     * 获得模拟器的IP地址
     *
     * @return String 本机ip地址
     */
    public static String getLocalIpAddress(Context context) {
        String address = "";

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        address = Formatter.formatIpAddress(ipAddress);

        if (!StringUtils.isEmpty(address) && address.length() >= EMPTY_IP_ADDRESS.length()
                && !EMPTY_IP_ADDRESS.equals(address)) {

            return address;
        }

        NetworkInterface intf = null;
        InetAddress inetAddress = null;
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            Enumeration<InetAddress> enumIpAddr = null;

            for (; en.hasMoreElements(); ) {
                intf = en.nextElement();
                enumIpAddr = intf.getInetAddresses();

                for (; enumIpAddr.hasMoreElements(); ) {
                    inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        address = inetAddress.getHostAddress();

                        return address;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.i("", ex.toString());
        }

        return address;
    }

    /**
     * 字符串转整型
     *
     * @param str
     * @return
     */
    public static int parseINT(String str, int defaultInt) {
        int rInt = defaultInt;

        if (StringUtils.isEmpty(str)) {
            return rInt;
        }

        if (INT_LENGTH < str.length()) {
            str = str.substring(str.length() - INT_LENGTH);
        }

        try {
            rInt = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            rInt = defaultInt;

        }

        return rInt;
    }

    /**
     * 获取文件大小
     *
     * @param url
     * @return
     */
    public static long getFileSize(String url) {
        if (StringUtils.isEmpty(url)) {
            return 0;
        }

        File file = new File(url);

        if (file.isDirectory()) {
            return 0;
        }

        return file.length();
    }

    /**
     * 获取List的长�?
     *
     * @param list
     * @return list为空指针或�?�空集合，返�?0；否则返回具体长�?
     */
    public static int sizeOf(List<?> list) {
        if (null == list) {
            return 0;
        }

        return list.size();
    }

    public static String appendStr(String... strings) {
        StringBuffer sb = new StringBuffer();

        if (null != strings && strings.length > 0) {
            for (String str : strings) {
                sb.append(str);
            }
        }

        return sb.toString();
    }

    /**
     * 把字符串中的特殊字符用xml指定的字符代�?
     *
     * @param str �?要转换的字符�?
     * @return 转换后的字符�?
     */
    public static String changeStrToXml(Object str) {
        String tempStr = null == str ? "" : str.toString();
        // 转换�?�?&�?&amp;
        tempStr = tempStr.replaceAll("[&]", "&amp;");
        // 转换�?�?<�?&lt;
        tempStr = tempStr.replaceAll("[<]", "&lt;");
        // 转换�?�?>�?&gt;
        tempStr = tempStr.replaceAll("[>]", "&gt;");
        // 转换�?有�?�为&apos;
        tempStr = tempStr.replaceAll("[']", "&apos;");
        // 转换�?�?"�?&quot;
        tempStr = tempStr.replaceAll("[\"]", "&quot;");
        // 转换�?�?(�?&#40;
        tempStr = tempStr.replaceAll("[(]", "&#40;");
        // 转换�?�?)�?&#41;
        tempStr = tempStr.replaceAll("[)]", "&#41;");
        // 转换�?�?%�?&#37;
        tempStr = tempStr.replaceAll("[%]", "&#37;");
        // 转换�?�?+�?&#43;
        tempStr = tempStr.replaceAll("[+]", "&#43;");
        // 转换�?�?-�?&#45;
        tempStr = tempStr.replaceAll("[-]", "&#45;");

        return tempStr;
    }

    /**
     * 把xml中的的特殊字符用转换回原始数�?
     *
     * @param xmlStr �?要转换的字符�?
     * @return 转换后的字符�?
     */
    public static String changeXmlToStr(String xmlStr) {
        if (StringUtils.isEmpty(xmlStr)) {
            return "";
        }

        // 转换�?�?&amp;�?&
        xmlStr = xmlStr.replaceAll("&amp;", "&");
        // 转换�?�?&lt;�?<
        xmlStr = xmlStr.replaceAll("&lt;", "<");
        // 转换�?�?&gt;�?>
        xmlStr = xmlStr.replaceAll("&gt;", ">");
        // 转换�?�?&apos;为�??
        xmlStr = xmlStr.replaceAll("&apos;", "'");
        // 转换�?�?&quot;�?"
        xmlStr = xmlStr.replaceAll("&quot;", "\"");
        // 转换�?�?&#40;�?(
        xmlStr = xmlStr.replaceAll("&#40;", "(");
        // 转换�?�?&#41;�?)
        xmlStr = xmlStr.replaceAll("&#41;", ")");
        // 转换�?�?&#37;�?%
        xmlStr = xmlStr.replaceAll("&#37;", "%");
        // 转换�?�?&#43;�?+
        xmlStr = xmlStr.replaceAll("&#43;", "+");
        // 转换�?�?&#45;�?-
        xmlStr = xmlStr.replaceAll("&#45;", "-");

        return xmlStr;
    }

    /**
     * autolistview
     *
     * @return
     */
    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }

    /**
     * autolistview
     *
     * @param format
     * @return
     */
    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    /**
     * 统一处理IO的关闭操�?
     *
     * @param c
     */
    public static void closeIO(Closeable c) {
        if (null == c) {
            return;
        }

        try {
            c.close();
        } catch (IOException e) {

        }
    }

    public static <T> T getCastObject(Class<T> c, Object obj) {
        if (null == obj || null == c) {

            return null;
        }

        T body = null;

        if (c.equals(obj.getClass())) {
            try {
                body = c.cast(obj);
            } catch (ClassCastException e) {

            }
        }

        return body;
    }

    /**
     * 汉字转换位汉语拼音，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i],
                            defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    // 根据经纬度获取所在城市
    public static String getCityByLocation(double latitude, double longitude) {
        String uriAPI = "http://api.map.baidu.com/geocoder?output=json&location=" + latitude + "," +
                "" + longitude
                + "&key=igAVHKvNuCaVYlpawCKIEq90";
        String strResult = "";
        /* 建立HTTP Get对象 */
        HttpGet httpRequest = new HttpGet(uriAPI);
        try {
			/* 发送请求并等待响应 */
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			/* 若状态码为200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读 */
                strResult = EntityUtils.toString(httpResponse.getEntity());
				/* 去没有用的字符 */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResult;
    }

    /**
     * obj转map
     *
     * @param map 转出的map
     * @param obj 需要转换的对象
     */
    public static void javaBeanToMap(Map<String, String> map, Object obj) {
        // 获得对象所有属性
        Field fields[] = obj.getClass().getDeclaredFields();
        Field field = null;
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            field.setAccessible(true);// 修改访问权限
            try {
                String key = field.getName();
                String value = (String) field.get(obj);
                map.put(key, (String) value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } // 读取属性值
        }
    }

    /**
     * 获取时间差
     * <P>数据格式:2016-12-12 00:00:00</P>
     */
    public static String getTimeDiff(String deadLine) {
        String timeDiff = null;
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = format.format(new Date());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date now = df.parse(time);
            java.util.Date date = df.parse(deadLine);
            long l = date.getTime() - now.getTime();
            if (l < 0) {
                timeDiff = "该订单不可接";
            } else {
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                timeDiff = "" + day + "天" + hour + "小时" + min + "分" + s + "秒";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeDiff;
    }

    public static String getCityName(String json) {
        if (json == "" || null == json) {
            return "";
        }
        try {
            JSONObject jsonObj = new JSONObject(json);
            String status = jsonObj.getString("status");
            if (status.equalsIgnoreCase("OK")) {
                String city = jsonObj.getJSONObject("result").getJSONObject("addressComponent")
                        .getString("city");
                System.out.println("currentCity ===" + city);
                YuQunApplication.currentCity = city;
                return city;
            } else {
                System.out.println("获取城市失败：" + status);
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取外网IP
     *
     * @param params
     * @return
     */
    private static String GetNetIp2(String params) {
        URL infoUrl = null;
        InputStream inStream = null;
        try {
            infoUrl = new URL(params);
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,
                        "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                //从反馈的结果中提取出IP地址
                int start = strber.indexOf("{");
                int end = strber.indexOf("}", start);
                line = strber.substring(start, end + 1);
                return line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取外网IP
     *
     * @param url
     * @return
     */
    public static void GetNetCurrentIP(final String url) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String response = GetNetIp2(url);
                return response;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObject = new JSONObject(s.toString());
                    String cname = (String) jsonObject.get("cname");
                    String city = cname.substring(2, cname.length());
                    YuQunApplication.currentCity = city;
                    LogN.d("currentCity =", city);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    /***
     * 微信分享 （这里仅提供一个分享网页的示例，其它请参看官网示例代码）
     *
     * @param flag        (0:分享到微信好友，1：分享到微信朋友圈)
     * @param url
     * @param title
     * @param description 文字
     * @param thumb       图片格式需要压缩
     */
    public static void wechatShare(int flag, String url, String title, String description, Bitmap
            thumb) {
        CommonData.isShare = true;
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        msg.setThumbImage(thumb);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req
                .WXSceneTimeline;
        YuQunApplication.api.sendReq(req);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] httpGet(final String url) {
        if (url == null || url.length() == 0) {
            Log.e(TAG, "httpGet, url is null");
            return null;
        }

        HttpClient httpClient = getNewHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse resp = httpClient.execute(httpGet);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.e(TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
                return null;
            }

            return EntityUtils.toByteArray(resp.getEntity());

        } catch (Exception e) {
            Log.e(TAG, "httpGet exception, e = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] httpPost(String url, String entity) {
        if (url == null || url.length() == 0) {
            Log.e(TAG, "httpPost, url is null");
            return null;
        }

        HttpClient httpClient = getNewHttpClient();

        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new StringEntity(entity));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse resp = httpClient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.e(TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
                return null;
            }

            return EntityUtils.toByteArray(resp.getEntity());
        } catch (Exception e) {
            Log.e(TAG, "httpPost exception, e = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static byte[] readFromFile(String fileName, int offset, int len) {
        if (fileName == null) {
            return null;
        }

        File file = new File(fileName);
        if (!file.exists()) {
            Log.i(TAG, "readFromFile: file not found");
            return null;
        }

        if (len == -1) {
            len = (int) file.length();
        }

        Log.d(TAG, "readFromFile : offset = " + offset + " len = " + len + " offset + len = " +
                (offset + len));

        if (offset < 0) {
            Log.e(TAG, "readFromFile invalid offset:" + offset);
            return null;
        }
        if (len <= 0) {
            Log.e(TAG, "readFromFile invalid len:" + len);
            return null;
        }
        if (offset + len > (int) file.length()) {
            Log.e(TAG, "readFromFile invalid file len:" + file.length());
            return null;
        }

        byte[] b = null;
        try {
            RandomAccessFile in = new RandomAccessFile(fileName, "r");
            b = new byte[len]; // 创建合适文件大小的数组
            in.seek(offset);
            in.readFully(b);
            in.close();

        } catch (Exception e) {
            Log.e(TAG, "readFromFile : errMsg = " + e.getMessage());
            e.printStackTrace();
        }
        return b;
    }

    public static Bitmap extractThumbNail(final String path, final int height, final int width,
                                          final boolean crop) {
        Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize >
                    MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;

            Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options
                    .outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed");
                return null;
            }

            Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm
                        .getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            Log.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    public static String sha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes());

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<String> stringsToList(final String[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        final List<String> result = new ArrayList<String>();
        for (int i = 0; i < src.length; i++) {
            result.add(src[i]);
        }
        return result;
    }

    private static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException,
                KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws
                        java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws
                        java.security.cert.CertificateException {
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
    public static int getDpi(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        int height = 0;
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            height = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }
    public static int[] getScreenWH(Context poCotext) {
        WindowManager wm = (WindowManager) poCotext
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        return new int[] { width, height };
    }
    public static int getVrtualBtnHeight(Context poCotext) {
        int location[] = getScreenWH(poCotext);
        int realHeiht = getDpi((Activity) poCotext);
        int virvalHeight = realHeiht - location[1];
        return virvalHeight;
    }

}
