package com.isflee;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;

@Slf4j

public class MyFileUtils {

    public static void saveFile(byte[] img, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        File dir = new File(filePath);
        try {
            //判断文件目录是否存在
            if(dir.exists() && !dir.isDirectory()){
                FileUtils.deleteQuietly(dir);
            }
            dir.mkdir();
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(img);
            log.info("文件已经下载到:"+filePath+"， 文件名：" + fileName);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public static String inputByteReader(InputStream in){

        ByteArrayOutputStream res = null;
        try(ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream()){
            byte buffer [] = new byte[1024];
            int count = -1;
            while (((count = in.read(buffer)) != -1)) {
                byteArrayInputStream.write(buffer, 0, count);
                buffer = new byte[1024];
            }
            res = byteArrayInputStream;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return res.toString();
    }
}
