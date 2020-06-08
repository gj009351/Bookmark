package com.duke.bookmark.utils;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.duke.bookmark.BuildConfig;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map;

/**
 * Log工具，类似android.util.Log。 tag自动产生，格式:
 * tagPrefix:className.methodName(Line:lineNumber),
 * tagPrefix为空时只输出：className.methodName(Line:lineNumber)。
 */
public class LogUtils {

    // 自定义Tag的前缀
    public static String TAG_PREFIX = "duke";
    //是否允许打印日志，默认是true，设置为false则不打印
    public static boolean ALLOW_LOG = BuildConfig.DEBUG;
    // 是否把保存日志到SD卡中
    private static final boolean IS_SAVE_LOG = true;


    //SD卡中的根目录
    public static final String ROOT = Environment.getExternalStorageDirectory().getPath() + "/com.duke.bookmark/";
    private static final String PATH_LOG_INFO = ROOT + "log/";

    private LogUtils() {
    }

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s(Line:%d)"; // 占位符
        String callerClazzName = caller.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber()); // 替换
        tag = TextUtils.isEmpty(TAG_PREFIX) ? tag : TAG_PREFIX + ":" + tag;
        return tag;
    }

    public static void http(String content) {
        if (!ALLOW_LOG) {
            return;
        }

        Log.d("http", content);
        if (IS_SAVE_LOG) {
            point(PATH_LOG_INFO, "http", content);
        }
    }


    public static String makeUrl(String url, JSONObject params) {
        StringBuilder stringBuilder = new StringBuilder(url);
        if (stringBuilder.indexOf("?") < 0) {
            stringBuilder.append('?');
        }
        if (params != null) {
            stringBuilder.append(params.toString());
        } else {
            stringBuilder.append("params=null");
        }
//        if (params.size() > 0) {
//            for (String name : params.keySet()) {
//                stringBuilder.append('&');
//                stringBuilder.append(name);
//                stringBuilder.append('=');
//                stringBuilder.append(String.valueOf(params.get(name)));
//                // 不做URLEncoder处理n
//                // url.append(URLEncoder.encode(String.valueOf(params.get(name)),
//                // UTF_8));
//            }
//        }
        return stringBuilder.toString().replace("?&", "?");
    }

    public static String makeUrl(String url, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder(url);
        if (stringBuilder.indexOf("?") < 0) {
            stringBuilder.append('?');
        }
        if (params.size() > 0) {
            for (String name : params.keySet()) {
                stringBuilder.append('&');
                stringBuilder.append(name);
                stringBuilder.append('=');
                stringBuilder.append(String.valueOf(params.get(name)));
                // 不做URLEncoder处理n
                // url.append(URLEncoder.encode(String.valueOf(params.get(name)),
                // UTF_8));
            }
        }
        return stringBuilder.toString().replace("?&", "?");
    }

    public static void d(String url, JSONObject params) {
        d(makeUrl(url, params));
    }

    public static void d(String url, Map<String, String> params) {
        d(makeUrl(url, params));
    }
    public static void d(Map<String, String> params) {
        if (params.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String name : params.keySet()) {
                stringBuilder.append('&');
                stringBuilder.append(name);
                stringBuilder.append('=');
                stringBuilder.append(params.get(name));
                d(stringBuilder.toString());
                stringBuilder.setLength(0);
            }
        }
    }

    public static void d(String content) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.d(tag, content);
    }

    public static void d(String content, Throwable tr) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.d(tag, content, tr);
    }

    public static void e(String content) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.e(tag, content);
        if (IS_SAVE_LOG) {
            point(PATH_LOG_INFO, tag, content);
        }
    }

    public static void e(String content, Throwable tr) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.e(tag, content, tr);
        if (IS_SAVE_LOG) {
            point(PATH_LOG_INFO, tag, tr.getMessage());
        }
    }

    public static void i(String content) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.i(tag, content);
    }

    public static void i(String content, Throwable tr) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.i(tag, content, tr);
    }

    public static void v(String content) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.v(tag, content);
    }

    public static void v(String content, Throwable tr) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.v(tag, content, tr);
    }

    public static void w(String content) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.w(tag, content, tr);
    }

    public static void w(Throwable tr) {
        if (!ALLOW_LOG) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);

        Log.w(tag, tr);
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static void point(String path, String tag, String msg) {
        if (isSDAva()) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("",
                    Locale.SIMPLIFIED_CHINESE);
            dateFormat.applyPattern("yyyy");
            path = path + dateFormat.format(date) + "/";
            dateFormat.applyPattern("MM");
            path += dateFormat.format(date) + "/";
            dateFormat.applyPattern("dd");
            path += dateFormat.format(date) + ".log";
            dateFormat.applyPattern("[yyyy-MM-dd HH:mm:ss]");
            String time = dateFormat.format(date);
            File file = new File(path);
            if (!file.exists()) {
                createDipPath(path);
            }

            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(file, true)));
                out.write(time + " " + tag + " " + msg + "\r\n");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据文件路径 递归创建文件
     *
     * @param file
     */
    public static void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A little trick to reuse a formatter in the same thread
     */
    private static class ReusableFormatter {

        private Formatter formatter;
        private StringBuilder builder;

        public ReusableFormatter() {
            builder = new StringBuilder();
            formatter = new Formatter(builder);
        }

        public String format(String msg, Object... args) {
            formatter.format(msg, args);
            String s = builder.toString();
            builder.setLength(0);
            return s;
        }
    }

    private static final ThreadLocal<ReusableFormatter> thread_local_formatter = new ThreadLocal<ReusableFormatter>() {
        protected ReusableFormatter initialValue() {
            return new ReusableFormatter();
        }
    };

    public static String format(String msg, Object... args) {
        ReusableFormatter formatter = thread_local_formatter.get();
        return formatter.format(msg, args);
    }

    public static boolean isSDAva() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)
                || Environment.getExternalStorageDirectory().exists()) {
            return true;
        } else {
            return false;
        }
    }

}