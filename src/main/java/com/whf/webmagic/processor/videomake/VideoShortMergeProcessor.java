package com.whf.webmagic.processor.videomake;

import com.alibaba.fastjson.JSON;
import com.whf.webmagic.constant.ConstantKey;
import com.whf.webmagic.dao.DelightArticleDao;
import com.whf.webmagic.dao.DelightVideoDao;
import com.whf.webmagic.dao.DelightVideoResultDao;
import com.whf.webmagic.entity.DelightArticle;
import com.whf.webmagic.entity.DelightVideo;
import com.whf.webmagic.entity.DelightVideoResult;
import com.whf.webmagic.utils.FileUtils;
import com.whf.webmagic.utils.PicUtils;
import com.whf.webmagic.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoShortMergeProcessor {
    private final static Logger LOG = LoggerFactory.getLogger(VideoShortMergeProcessor.class);
    final static String FFMPEG_PATH = "E:\\private\\ffmpeg-20190512-1cc7e26-win64-static\\ffmpeg-20190512-1cc7e26-win64-static\\bin\\ffmpeg.exe";
    final static String FFPROBE_PATH = "E:\\private\\ffmpeg-20190512-1cc7e26-win64-static\\ffmpeg-20190512-1cc7e26-win64-static\\bin\\ffprobe.exe";
    private DelightVideoDao delightVideoDao;
    private DelightArticleDao delightArticleDao;
    private DelightVideoResultDao delightVideoResultDao;
    public VideoShortMergeProcessor(DelightVideoDao delightVideoDao, DelightArticleDao delightArticleDao, DelightVideoResultDao delightVideoResultDao)
    {
        this.delightVideoDao = delightVideoDao;
        this.delightArticleDao = delightArticleDao;
        this.delightVideoResultDao =delightVideoResultDao;
    }

    public void processor()
    {
        try{
            List<Map<String,Object>> processList = new ArrayList<>();
            List<Map<String,Object>> processFilterList = new ArrayList<>();
            List<String> processCutList = new ArrayList<>();
            List<String> processRatioList = new ArrayList<>();
            int playTimes = 0;
            //清除临时文件夹文件
            delFolder(ConstantKey.TS_SAVE_PATH1);
            //查询数据库数据随机1条
            List<DelightVideo> lists =delightVideoDao.queryRandShortVideo();
            LOG.info("VideoCutMergeProcessor processor lists size="+lists.size());
//            processList.add(ConstantKey.VIDEO_START_PATH);
            for (int i = 0; i < lists.size(); i++) {
                Map<String,Object> map= new HashMap<String,Object>();
                playTimes+=lists.get(i).getPalyTime();
                if(playTimes >= 300)
                {
                    map.put("url",lists.get(i).getLocalUrl());
                    map.put("playTime",lists.get(i).getPalyTime());
                    processList.add(map);
                    break;
                }
                else
                {
                    map.put("url",lists.get(i).getLocalUrl());
                    map.put("playTime",lists.get(i).getPalyTime());
                    processList.add(map);
                }
            }
//            processList.add(ConstantKey.VIDEO_END_PATH);
            LOG.info("VideoCutMergeProcessor processor processList="+JSON.toJSONString(processList));

            //视频加片头过滤效果
            for (int i = 0; i < processList.size(); i++) {
                String filterPath = ConstantKey.TS_SAVE_PATH1+i+"f.mp4";
                Map<String,Object> dataMap= processList.get(i);
                filterVideo(dataMap.get("url").toString(),filterPath);
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("url",filterPath);
                map.put("playTime",dataMap.get("playTime").toString());
                processFilterList.add(map);
            }
            //视频裁剪去除LOGO
            processCutList.add(ConstantKey.VIDEO_START_PATH);
            for (int i = 1; i < processFilterList.size()-1; i++) {
                String cutPath = ConstantKey.TS_SAVE_PATH1+i+"c.mp4";
                Map<String,Object> dataMap= processFilterList.get(i);
                int edTime = Integer.valueOf(dataMap.get("playTime").toString()) - 17;
                String startTime=TimeUtils.secToTime(0);
                String endTime=TimeUtils.secToTime(edTime);
                cutVideo(dataMap.get("url").toString(), startTime, endTime, cutPath);
                processCutList.add(cutPath);
            }
            processCutList.add(ConstantKey.VIDEO_END_PATH);
            //获取当前视频帧速率和播放时间
            Double maxfps = 0.00;
            Double minfps = 100.00;
            Double avgfps = 0.00;
            String currentRatio ="1280x720";

            for (int i = 1; i < processCutList.size()-1; i++) {
                LOG.info("VideoCutMergeProcessor processor i="+i);
                Map<String,String> map=getRatioInfo(processCutList.get(i));
                Double fps = Double.valueOf(map.get("fps"));
                String ratio =map.get("ratio");
                String videoLen = map.get("videoLen");
                long videoPlayTime=TimeUtils.getSecond(videoLen);
                LOG.info("VideoCutMergeProcessor processor fps="+fps);
                LOG.info("VideoCutMergeProcessor processor ratio="+ratio);
                LOG.info("VideoCutMergeProcessor processor videoLen="+videoLen);
                if(fps > maxfps)
                {
                    maxfps = fps;
                }
                if(fps < minfps)
                {
                    minfps = fps;
                }
            }
            LOG.info("VideoCutMergeProcessor processor maxfps="+maxfps);
            LOG.info("VideoCutMergeProcessor processor minfps="+minfps);
            LOG.info("VideoCutMergeProcessor processor avgfps="+avgfps);
            LOG.info("VideoCutMergeProcessor processor currentRatio="+currentRatio);

            for (int i = 0; i < processCutList.size(); i++) {
                //视频转分辨率和帧速率
                String ratioPath = ConstantKey.TS_SAVE_PATH1+i+"r.mp4";
                changeRatio(minfps+"","900x540",processCutList.get(i),ratioPath);
                Thread.sleep(2000);
                processRatioList.add(ratioPath);
            }
            LOG.info("VideoCutMergeProcessor processor processRatioList="+JSON.toJSONString(processRatioList));
            if(processRatioList != null && processRatioList.size() >0)
            {
                String resultPath = merge(processRatioList);
                if(resultPath != null && !"".equals(resultPath))
                {
                    //截图图片保存数据库
                    cutVideoPic(resultPath);
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        LOG.info("VideoCutMergeProcessor processor success!");
    }

    public void filterVideo(String inPath,String outPath) throws IOException {
        //ffmpeg -i 1.mp4 -vf fade=in:0:50 out3.mp4 -y
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inPath);
        command.add("-vf");
        command.add("fade=in:0:50");
        command.add(outPath);
        command.add("-y");
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 使用这种方式会在瞬间大量消耗CPU和内存等系统资源，所以这里我们需要对流进行处理
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
            LOG.info("filterVideo==>"+line);
        }
        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }

    public void cutVideoPic(String resultPath) throws IOException, InterruptedException {
        DelightArticle param = new DelightArticle();
        param.setChannel(ConstantKey.VIDEO_MAKE_CHANNEL);
        DelightArticle delightArticle=delightArticleDao.queryArticleNum(param);
        LOG.info("delightArticle ="+ JSON.toJSONString(delightArticle));
        int makeVideoNum =delightArticle.getArticleNum()+1;
        String title = ConstantKey.VIDEO_TITLE+makeVideoNum+"期.mp4";
        LOG.info("merge makevideo title="+title);

        String picPath = ConstantKey.VIDEO_MAKE_RESULT_PATH + title.replace(".mp4",".jpg");
        Thread.sleep(5000);
        if(FileUtils.judeFileExists(new File(resultPath)))
        {
                    VideoCoverProcessor.cutVideoToPic(resultPath,picPath);
                    LOG.info("cutVideoPic resultPath="+resultPath);
                    LOG.info("cutVideoPic makeVideoNum="+makeVideoNum);
                    LOG.info("cutVideoPic picPath="+picPath);
                    LOG.info("cutVideoPic title="+title);
                    Thread.sleep(5000);
                    if(FileUtils.judeFileExists(new File(picPath))) {
//                        VideoCoverProcessor.clearLightLogo(resultPath,ConstantKey.VIDEO_MAKE_RESULT_PATH+title);
//                        if(FileUtils.judeFileExists(new File(ConstantKey.VIDEO_MAKE_RESULT_PATH+title))) {
//                            VideoCoverProcessor.clearRightLogo(ConstantKey.TS_SAVE_PATH1+"result2.mp4",ConstantKey.VIDEO_MAKE_RESULT_PATH+title);
//                            if(FileUtils.judeFileExists(new File(ConstantKey.VIDEO_MAKE_RESULT_PATH+title))) {
                                param = new DelightArticle();
                                param.setChannel(ConstantKey.VIDEO_MAKE_CHANNEL);
                                param.setArticleNum(makeVideoNum);
                                delightArticleDao.updateArticleNum(param);

                                String videoName = title.substring(0, title.lastIndexOf("."));
                                LOG.info("cutVideoPic videoName=" + videoName);
                                DelightVideoResult delightVideoResult = new DelightVideoResult();
                                delightVideoResult.setVideoName(videoName);
                                delightVideoResult.setLocalPath(ConstantKey.VIDEO_MAKE_RESULT_PATH+title);
                                delightVideoResult.setPicPath(picPath);
                                delightVideoResult.setVideoNum(makeVideoNum);
                                LOG.info("cutVideoPic delightVideoResult=" + JSON.toJSONString(delightVideoResult));
                                delightVideoResultDao.insertVideoResultInfo(delightVideoResult);
//                            }
//                            else
//                            {
//                                LOG.info("cutVideoPic resultVideo not find jpg!");
//                            }
                        }
                        else
                        {
                            LOG.info("cutVideoPic picPath not find jpg!");
                        }
                    }
                    else
                    {
                        LOG.info("cutVideoPic videoResult not find file!");
                    }
//                }
//                else
//                {
//                        LOG.info("cutVideoPic videoResult not find file!");
//                }
    }

    public void cutVideo(String inputPath,String startTime,String endTime,String outPath) throws IOException {
        //ffmpeg  -i ./plutopr.mp4 -vcodec copy -acodec copy -ss 00:00:10 -to 00:00:15 ./cutout1.mp4 -y
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-ss");
        command.add(startTime);
        command.add("-t");
        command.add(endTime);
        command.add("-accurate_seek");
        command.add("-i");
        command.add(inputPath);
        command.add("-vcodec");
        command.add("copy");
        command.add("-acodec");
        command.add("copy");
//        command.add("-avoid_negative_ts 1");
        command.add(outPath);
        command.add("-y");
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 使用这种方式会在瞬间大量消耗CPU和内存等系统资源，所以这里我们需要对流进行处理
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
            LOG.info("cutVideo==>"+line);
        }
        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }

    public void changeRatio(String fps,String ratio,String inputPath,String outPath) throws IOException {
        //ffmpeg -i input.mp4 -vf scale=960:540 output.mp4
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-r");
        command.add(fps);
        command.add("-i");
        command.add(inputPath);
        command.add("-max_muxing_queue_size");
        command.add("1024");
        command.add("-vf");
        command.add("scale="+ratio.replace("x",":"));//-1:720
        command.add(outPath);
        command.add("-y");
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 使用这种方式会在瞬间大量消耗CPU和内存等系统资源，所以这里我们需要对流进行处理
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
            LOG.info("changeRatio==>"+line);
        }
        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }

    public Map<String,String> getRatioInfo(String inputPath) throws IOException {
        //ffmpeg -i input.mp4 -vf scale=960:540 output.mp4
        HashMap<String,String> map = new HashMap<String,String>();
        List<String> command = new ArrayList<String>();
        command.add(FFPROBE_PATH);
        command.add("-show_streams");
        command.add(inputPath);
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 使用这种方式会在瞬间大量消耗CPU和内存等系统资源，所以这里我们需要对流进行处理
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
            if(line.contains("fps"))
            {
                LOG.info("getRatioInfo line="+line);
                String fps="";
                String ratio ="";
                String[] datas=line.substring(line.indexOf("Video:")+6).split(",");
                for (int i = 0; i < datas.length; i++) {
                    if(datas[i].contains("fps"))
                    {
                        LOG.info("getRatioInfo fps="+datas[i]);
                        fps = datas[i].split(" ")[1];
                        map.put("fps",fps);
                    }
                    else if(datas[i].contains("x") && !datas[i].contains(")"))
                    {
                        LOG.info("getRatioInfo ratio="+datas[i]);
                        ratio =datas[i].split(" ")[1];
                        map.put("ratio",ratio);
                    }
                }
                LOG.info("getRatioInfo fps="+fps);
                LOG.info("getRatioInfo ratio="+ratio);

            }
            else if(line.contains("Duration:")){
                LOG.info("getRatioInfo line="+line);
                String videoLen=line.substring(line.indexOf("Duration:")+10,line.indexOf("Duration:")+18);
                map.put("videoLen",videoLen);
                LOG.info("getRatioInfo videoLen="+videoLen);

            }
//            LOG.info("getRatioInfo line="+line);
        }
        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
        return map;
    }

    public String merge(List<String> processLists) throws IOException, InterruptedException {
        LOG.info("merge size="+processLists.size());
        //mp4转成ts
        for (int i = 0; i < processLists.size(); i++) {
            LOG.info("merge localUrl="+processLists.get(i));
            coverMP4ToTs(processLists.get(i), ConstantKey.TS_SAVE_PATH1+i+ConstantKey.TS_SAVE_PATH2);
            if(!FileUtils.judeFileExists(new File(ConstantKey.TS_SAVE_PATH1+i+ConstantKey.TS_SAVE_PATH2)))
            {
                LOG.info("merge coverMP4ToTs fail!");
                return null;
            }
        }
        //ts写入txt文件中
        String fielPath =writeTsFile(processLists.size());
        LOG.info("merge fielPath="+fielPath);

        DelightArticle param = new DelightArticle();
        param.setChannel(ConstantKey.VIDEO_MAKE_CHANNEL);
        DelightArticle delightArticle=delightArticleDao.queryArticleNum(param);
        LOG.info("delightArticle ="+ JSON.toJSONString(delightArticle));
        int makeVideoNum =delightArticle.getArticleNum()+1;
        String title = ConstantKey.VIDEO_TITLE+makeVideoNum+"期.mp4";
        LOG.info("merge makevideo title="+title);

        //txt文件中的ts文件合并转成mp4
        coverTsToMP4(fielPath,ConstantKey.VIDEO_MAKE_RESULT_PATH+title);    //ConstantKey.VIDEO_MAKE_RESULT_PATH
        Thread.sleep(15000);
        if(FileUtils.judeFileExists(new File(ConstantKey.VIDEO_MAKE_RESULT_PATH+title))) {
            String resultPath = ConstantKey.VIDEO_MAKE_RESULT_PATH+title;
            LOG.info("merge resultPath="+resultPath);
            return resultPath;
        }
        else
        {
            LOG.info("merge coverTsToMP4 fail");
        }

        return "";
    }

    public void coverMP4ToTs(String inputPath,String outPath) throws IOException
    {
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-y");
        command.add("-i");
        command.add(inputPath);
        command.add("-vcodec");
        command.add("copy");
        command.add("-acodec");
        command.add("copy");
        command.add("-vbsf");
        command.add("h264_mp4toannexb");
        command.add(outPath);
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 使用这种方式会在瞬间大量消耗CPU和内存等系统资源，所以这里我们需要对流进行处理
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
            LOG.info("coverMP4ToTs line="+line);
        }
        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
    }

    public void coverTsToMP4(String filePath,String outputPath) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("cmd.exe /c ");
        sb.append(FFMPEG_PATH);
        sb.append(" -f concat -safe 0 -i ");
        sb.append(filePath);
        sb.append(" -acodec copy -vcodec copy -absf aac_adtstoasc ");
        sb.append(outputPath);
        sb.append(" -y");
        Runtime.getRuntime().exec(sb.toString());
//        Runtime.getRuntime().exec("cmd.exe /c "+FFMPEG_PATH+" -f concat -safe 0 -i e:\\testffmpg\\filelist2.txt -acodec copy -vcodec copy -absf aac_adtstoasc e:\\testffmpg\\output.mp4");
    }

    public static String writeTsFile(int len)
    {
        String fielPath = ConstantKey.TS_FILE_PATH;
        File file = new File(fielPath);
        // 2.创建一个FileOutputStream的对象，将file的对象作为形参传递给FileOutputStream的构造器中
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // 3.写入的操作
            for (int i = 0; i < len; i++) {
                fos.write(new String("file E:\\\\tempVideo\\\\"+i+".ts"+System.getProperty("line.separator")).getBytes());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 4.关闭输出流
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return fielPath;

    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
//            String filePath = folderPath;
//            filePath = filePath.toString();
//            java.io.File myFilePath = new java.io.File(filePath);
//            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除指定文件夹下所有文件
//param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static void task(DelightVideoDao delightVideoDao,DelightArticleDao delightArticleDao,DelightVideoResultDao delightVideoResultDao)
    {
        new VideoShortMergeProcessor(delightVideoDao,delightArticleDao,delightVideoResultDao).processor();
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        VideoCutMergeProcessor aa=new VideoCutMergeProcessor(null,null,null);
        //剪切视频
//        aa.cutViedo("E:\\videoResult\\456.mkv","00:07:30","00:08:00","E:\\videoResult\\temp.mkv");
//        Map<String,String> map=aa.getRatioInfo("E:\\tempVideo\\temp.mp4");
//        VideoCoverProcessor.cutVideoToPic("E:\\videoResult\\奇闻轶事乐园搞笑视频第23期.mp4","E:\\videoResult\\奇闻轶事乐园搞笑视频第23期.jpg");
////        //视频转分辨率和帧速率
////        aa.changeRatio("E:\\videoResult\\temp.mkv","E:\\videoResult\\output.mkv");
//        VideoCoverProcessor.videoAddPic("E:\\tempVideo\\奇闻轶事乐园搞笑视频第1期.mp4",ConstantKey.TS_SAVE_PATH1+"filmNamePic.png",ConstantKey.VIDEO_MAKE_RESULT_PATH+"奇闻轶事乐园搞笑视频第1期.mp4");
//        Thread.sleep(2000);
//        System.out.println(FileUtils.judeFileExists(new File(ConstantKey.VIDEO_MAKE_RESULT_PATH+"奇闻轶事乐园搞笑视频第1期.mp4")));

        String cutPath = "E:\\1\\out4.mp4";
        String inputPath = "E:\\1\\4.mp4";
        int edTime = 73 - 17;
        String startTime=TimeUtils.secToTime(0);
        String endTime=TimeUtils.secToTime(edTime);
        aa.cutVideo(inputPath, startTime, endTime, cutPath);

    }
}
