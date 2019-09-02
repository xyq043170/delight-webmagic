package com.whf.webmagic.task;

import com.whf.webmagic.dao.DelightWebmagicDao;
import com.whf.webmagic.entity.DelightWebmagic;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;

@Component
public class ScheduleDownLodaPicTask {
    private final static Logger LOG = LoggerFactory.getLogger(ScheduleDownLodaPicTask.class);
    @Autowired
    private DelightWebmagicDao delightWebmagicDao;

    @Scheduled(cron="0 0/30 * * * ?")
//    @Scheduled(cron="0/5 * * * * ?")
    private void process(){
        LOG.info("ScheduleDownLodaPicTask task start!");
        List<DelightWebmagic> delightWebmagics=delightWebmagicDao.queryUnSavePic();
        LOG.info("ScheduleDownLodaPicTask size="+delightWebmagics.size());
        for (int i = 0; i < delightWebmagics.size(); i++) {
            String jpgUrl =delightWebmagics.get(i).getJpgUrl();
            LOG.info("ScheduleDownLodaPicTask jpgUrl="+jpgUrl);
            if(jpgUrl.contains("jpg"))
            {
                downloadHDPicture(jpgUrl);
            }
            else
            {
                LOG.info("ScheduleDownLodaPicTask gif jpgUrl="+jpgUrl);
                downloadPicture(jpgUrl);
            }

        }
        LOG.info("ScheduleDownLodaPicTask task end!");
    }

    //链接url下载图片
    private void downloadPicture(String urlList) {
        URL url = null;
        int imageNumber = 0;

        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            String imageName =  "E:\\pic\\"+System.currentTimeMillis()+".jpg";

            FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            byte[] context=output.toByteArray();
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            DelightWebmagic delightWebmagic = new DelightWebmagic();
            delightWebmagic.setJpgUrl(urlList);
            delightWebmagic.setLocalUrl(imageName);
            delightWebmagic.setIsSave("1");
            delightWebmagicDao.updateSaveStatus(delightWebmagic);
        } catch (Exception e) {
            updateSaveFail(urlList);
            e.printStackTrace();
        }
    }

    private void downloadHDPicture(String urlList) {
        URL url = null;
        try {
//            File imageFile=new File(urlList);
            url = new URL(urlList);
            String newPath =  "E:\\pic\\"+System.currentTimeMillis()+".jpg";
//            if(!imageFile.canRead())
//                return;
            BufferedImage bufferedImage = ImageIO.read(url.openStream());
            if (null == bufferedImage) {
                updateSaveFail(urlList);
                return;
            }

            int originalWidth = bufferedImage.getWidth();
            int originalHeight = bufferedImage.getHeight();
            double scale = (double)originalWidth/1;    // 缩放的比例

            int newHeight =  (int)(originalHeight / 1);

            zoomImageUtils(urlList, newPath, bufferedImage, originalWidth, originalHeight);
        } catch (Exception e) {
            updateSaveFail(urlList);
            e.printStackTrace();
        }
    }

    private void zoomImageUtils(String urlList, String newPath, BufferedImage bufferedImage, int width, int height)
            throws IOException{

        String suffix = StringUtils.substringAfterLast(urlList, ".");

        // 处理 png 背景变黑的问题
//        if(suffix != null && (suffix.trim().toLowerCase().endsWith("png") || suffix.trim().toLowerCase().endsWith("gif"))){
//            BufferedImage to= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2d = to.createGraphics();
//            to = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
//            g2d.dispose();
//
//            g2d = to.createGraphics();
//            Image from = bufferedImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
//            g2d.drawImage(from, 0, 0, null);
//            g2d.dispose();
//
//            ImageIO.write(to, suffix, new File(newPath));
//
//        }else{
            // 高质量压缩，其实对清晰度而言没有太多的帮助
//            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            tag.getGraphics().drawImage(bufferedImage, 0, 0, width, height, null);
//
//            FileOutputStream out = new FileOutputStream(newPath);    // 将图片写入 newPath
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
//            jep.setQuality(1f, true);    //压缩质量, 1 是最高值
//            encoder.encode(tag, jep);
//            out.close();

            BufferedImage newImage = new BufferedImage(width, height, bufferedImage.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(bufferedImage, 0, 0, width, height, null);
            g.dispose();
            ImageIO.write(newImage, suffix, new File(newPath));
//        }
        DelightWebmagic delightWebmagic = new DelightWebmagic();
        delightWebmagic.setJpgUrl(urlList);
        delightWebmagic.setLocalUrl(newPath);
        delightWebmagic.setIsSave("1");
        delightWebmagicDao.updateSaveStatus(delightWebmagic);
    }

    private void updateSaveFail(String urlList)
    {
        DelightWebmagic delightWebmagic = new DelightWebmagic();
        delightWebmagic.setJpgUrl(urlList);
        delightWebmagic.setIsSave("2");
        delightWebmagicDao.updateSaveStatus(delightWebmagic);
    }

}
