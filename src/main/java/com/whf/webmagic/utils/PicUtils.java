package com.whf.webmagic.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PicUtils {
    public static BufferedImage drawTranslucentStringPic(int width, int height, Integer fontHeight,String drawStr)
    {
        try
        {
            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D gd = buffImg.createGraphics();
            //设置透明  start
            buffImg = gd.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            gd=buffImg.createGraphics();
            //设置透明  end
            gd.setFont(new Font("微软雅黑", Font.BOLD, fontHeight)); //设置字体
            gd.setColor(Color.white); //设置颜色
//            gd.drawRect(0, 0, width - 1, height - 1); //画边框
            gd.drawString(drawStr, width/2-fontHeight*drawStr.length()/2,fontHeight); //输出文字（中文横向居中）
            return buffImg;
        } catch (Exception e) {
            return null;
        }
    }

    public static void createImage(String str,String outPath){
        BufferedImage imgMap = drawTranslucentStringPic(str.length()*26, 30, 26,str);
        try
        {
            ImageIO.write(imgMap, "PNG", new File(outPath));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("生成完成");
    }
    public static void main(String[] args) throws Exception {
//        String str ="《计中计之密钥奇缘撒是》";
//        System.out.println(str.length());
//        createImage("《计中计之密钥奇缘撒是》", new Font("微软雅黑", Font.BOLD, 16), new File("e:\\tempVideo\\a.png"), str.length()*16, 24);
////        createImage(str, new Font("宋体", Font.PLAIN, 100), new File("e:\\tempVideo\\a.png"));
//        Font font = new Font("微软雅黑", Font.PLAIN, 130);
//        createImage("https://www.sojson.com", font, new File("e:\\tempVideo\\a.png"));
        String str ="《计中计之密钥奇缘撒是》";
        createImage(str,"e:\\tempVideo\\a.png");
    }
}
