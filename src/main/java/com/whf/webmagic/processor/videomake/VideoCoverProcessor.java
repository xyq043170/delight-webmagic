package com.whf.webmagic.processor.videomake;

import com.whf.webmagic.dao.DelightArticleDao;
import com.whf.webmagic.dao.DelightVideoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VideoCoverProcessor {
    private final static Logger LOG = LoggerFactory.getLogger(VideoCoverProcessor.class);
    final static String FFMPEG_PATH = "E:\\private\\ffmpeg-20190512-1cc7e26-win64-static\\ffmpeg-20190512-1cc7e26-win64-static\\bin\\ffmpeg.exe";

    public void processor()
    {

    }

    public static void mkvToMp4(String inputPath,String outPath) throws IOException {
        //ffmpeg -i input.mkv -c:v copy -c:a copy output.mp4
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inputPath);
        command.add("-c:v");
        command.add("copy");
        command.add("-c:a");
        command.add("copy");
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
            LOG.info("mkvToMp4 line="+line);
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

    public static void cutVideoToPic(String inputPath,String outPath) throws IOException {
        //ffmpeg.exe -i aa.mp4 -y -f image2 -ss 8 -t 0.01 -q:v 2 -s 1280x720 test.jpg
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inputPath);
        command.add("-y");
        command.add("-f");
        command.add("image2");
        command.add("-ss");
        command.add("8");
        command.add("-t");
        command.add("0.01");
        command.add("-q:v");
        command.add("2");
        command.add("-s");
        command.add("640x360");
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
            LOG.info("cutVideoToPic line="+line);
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

    public static void videoAddPic(String inputPath,String inputPicPath,String outPath) throws IOException {
        //ffmpeg -i input.mp4 -i iQIYI_logo.png -filter_complex overlay output.mp4
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inputPath);
        command.add("-i");
        command.add(inputPicPath);
        command.add("-filter_complex");
        command.add("overlay");
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
            LOG.info("videoAddPic line="+line);
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

    public static void clearLightLogo(String inputPath,String outPath) throws IOException {
        //ffmpeg -i input.mp4 -vf delogo=0:0:220:90:100:1 output.mp4
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inputPath);
        command.add("-vf");
        command.add("delogo=x=710:y=10:w=120:h=80:show=0");
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
            LOG.info("clearLightLogo line="+line);
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

    public static void clearRightLogo(String inputPath,String outPath) throws IOException {
        //ffmpeg -i input.mp4 -vf delogo=0:0:220:90:100:1 output.mp4
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inputPath);
        command.add("-vf");
        command.add("delogo=300:0:220:90:100:1");
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
            LOG.info("clearRightLogo line="+line);
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

    public static void main(String[] args) throws IOException {
        VideoCoverProcessor.clearLightLogo("e:\\tempVideo\\test.mp4","e:\\tempVideo\\out1.mp4");
    }
}
