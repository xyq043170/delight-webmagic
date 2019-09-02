package com.whf.webmagic.processor.videomake;

import com.whf.webmagic.constant.ConstantKey;
import com.whf.webmagic.dao.DelightVideoDao;
import com.whf.webmagic.entity.DelightVideo;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class VideoRenameProcessor {
    private final static Logger LOG = LoggerFactory.getLogger(VideoRenameProcessor.class);

    public static ArrayList<File> getFiles(String path) throws Exception {
        //目标集合fileList
        ArrayList<File> fileList = new ArrayList<File>();
        File file = new File(path);
        if(file.isDirectory()){
            File []files = file.listFiles();
            for(File fileIndex:files){
                //如果这个文件是目录，则进行递归搜索
                if(fileIndex.isDirectory()){
                    getFiles(fileIndex.getPath());
                }else {
                //如果文件是普通文件，则将文件句柄放入集合中
                fileList.add(fileIndex);
                }
            }
        }
        return fileList;
    }

    public void processor()
    {
        try{
            ArrayList<File> files = getFiles(ConstantKey.VIDEO_PATH);   //ConstantKey.VIDEO_PATH
            for (int i = 0; i < files.size(); i++) {
                File source = files.get(i);
                String newFilePath = source.getPath().substring(0, source.getPath().lastIndexOf("\\")) + "\\" + System.currentTimeMillis()
                        + source.getPath().substring(source.getPath().lastIndexOf("."));
                LOG.info("newFilePath="+newFilePath);
                File nf = new File(newFilePath);

                if (source.exists() && !nf.exists()) {
                    boolean result = source.renameTo(nf);
                    LOG.info("name="+source.getName());
                    LOG.info("path="+source.getPath());
                    LOG.info("result="+result);
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//    public static List<DelightVideo> getStartAndEndVideo()
//    {
//        List<DelightVideo> lists =new ArrayList<>();
//        try{
//            ArrayList<File> files = getFiles(ConstantKey.VIDEO_START_AND_END_PATH);
//            for (int i = 0; i < files.size(); i++) {
//                File source = files.get(i);
//                Encoder encoder = new Encoder();
//                MultimediaInfo m = encoder.getInfo(source);
//                long ls = m.getDuration()/1000;
//                LOG.info("name="+source.getName());
//                LOG.info("path="+source.getPath());
//                LOG.info("ls="+ls);
//                LOG.info("此视频时长为:"+ls/60000+"分"+(ls)/1000+"秒！");
//                DelightVideo delightVideo = new DelightVideo();
//                delightVideo.setLocalUrl(source.getPath());
//                delightVideo.setPalyTime((int)ls);
//                delightVideo.setVideoName(source.getName());
//                lists.add(delightVideo);
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return lists;
//    }

    public static void main(String[] args) {
        try{
            ArrayList<File> files = getFiles(ConstantKey.VIDEO_PATH);
            for (int i = 0; i < files.size(); i++) {
                File source = files.get(i);
                Encoder encoder = new Encoder();
                MultimediaInfo m = encoder.getInfo(source);
                long ls = m.getDuration();
                LOG.info("name="+source.getName());
                LOG.info("path="+source.getPath());
                LOG.info("ls="+ls);
                LOG.info("此视频时长为:"+ls/60000+"分"+(ls)/1000+"秒！");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void task()
    {
        new VideoRenameProcessor().processor();
    }
}