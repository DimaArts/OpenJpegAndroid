package com.dimaarts.ru.openjpegtest.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by gorshunovdv on 10/25/2017.
 */

public class FileUtils {

    public static boolean copyAsset(AssetManager assetManager,
                                    String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            boolean created = new File(toPath).createNewFile();
            if(created) {
                out = new FileOutputStream(toPath);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static String getDataDirectory(Context context) throws PackageManager.NameNotFoundException {
        PackageManager m = context.getPackageManager();
        String s = context.getPackageName();
        PackageInfo p = m.getPackageInfo(s, 0);
        return p.applicationInfo.dataDir;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static int getFileSize(String fullPath) {
        File file = new File(fullPath);
        return Integer.parseInt(String.valueOf(file.length()/1024));
    }
}
