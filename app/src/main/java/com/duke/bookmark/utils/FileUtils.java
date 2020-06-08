package com.duke.bookmark.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    private static final String PACKAGE_PHOTO = "bookmark/photos/";

    public static boolean copyFile(File srcFile, File destFile) {
        boolean flag = false;
        if (srcFile != null && !srcFile.exists()) {
            LogUtils.i("源文件不存在");
            return false;
        }
        if (destFile == null) {
            LogUtils.i( "目标文件为空");
            return false;
        }
        // 获取待复制文件的文件名
        if (srcFile.getAbsolutePath().equals(destFile.getAbsolutePath())) {
            LogUtils.i( "源文件路径和目标文件路径重复");
            return false;
        }
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        try {
            FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int c;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }
            fis.close();
            fos.close();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;

    }

    // 创建一个临时目录，用于复制临时文件，如assets目录下的离线资源文件
    public static String createTmpDir(Context context) {
        String sampleDir = "baiduTTS";
        String tmpDir = Environment.getExternalStorageDirectory().toString() + "/" + sampleDir;
        if (!makeDir(tmpDir)) {
            tmpDir = context.getExternalFilesDir(sampleDir).getAbsolutePath();
            if (!makeDir(sampleDir)) {
                throw new RuntimeException("create voice_room IMModel resources dir failed :" + tmpDir);
            }
        }
        return tmpDir;
    }

    public static boolean fileCanRead(String filename) {
        File f = new File(filename);
        return f.canRead();
    }

    public static boolean makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    public static void copyFromAssets(AssetManager assets, String source, String dest, boolean isCover)
            throws IOException {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = assets.open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            LogUtils.e(e.toString()+"");
        } catch (IOException e) {
            LogUtils.e(e.toString()+"");
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            LogUtils.e(e.toString()+"");
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
//        ToastUtils.showTop(MyApplication.getContext(), MyApplication.getContext().getResources().getString(R.string.saved_to_album));
    }

    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    public static String getRootFilePath(Context context) {
        if (hasSDCard()) {
            return Environment.getDataDirectory().getAbsolutePath() + "/";// filePath:/sdcard/
        } else {
            return context.getFilesDir().toString() + File.separator;
        }
    }

    public static File getPhotoDirectory(Context context) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PACKAGE_PHOTO);
    }
    public static String getPhotoDirectoryString(Context context) {
        return new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PACKAGE_PHOTO).getAbsolutePath();
    }

    public static String getFileTypeName(String url) {
        String type = null;
        if (!TextUtils.isEmpty(url) && url.contains(".")) {
            type = url.substring(url.lastIndexOf("."));
        }
        if (TextUtils.isEmpty(type) || type.length() > 5) {
            type = ".jpg";
        }
        if (!type.startsWith(".")) {
            type = "." + type;
        }
        return type;
    }
}
