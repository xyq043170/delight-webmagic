package com.whf.webmagic.utils;

import java.io.File;

public class FileUtils {
    // 判断文件是否存在
     public static boolean judeFileExists(File file) {
         if (file.exists()) {
             return true;
         } else {
             return false;
         }
     }
}
