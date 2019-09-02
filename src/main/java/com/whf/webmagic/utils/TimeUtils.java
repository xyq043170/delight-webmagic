package com.whf.webmagic.utils;

public class TimeUtils {
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
        {
            return "00:00";
        }
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if(i >= 0 && i < 10)
        {
            retStr = "0" + Integer.toString(i);
        }
        else{
            retStr = "" + i;
        }

        return retStr;
    }

    public static long getSecond(String time){
        long s = 0;
        if(time.length()==8){ //时分秒格式00:00:00
            int index1=time.indexOf(":");
            int index2=time.indexOf(":",index1+1);
            s = Integer.parseInt(time.substring(0,index1))*3600;//小时
            s+=Integer.parseInt(time.substring(index1+1,index2))*60;//分钟
            s+=Integer.parseInt(time.substring(index2+1));//秒
        }
        if(time.length()==5){//分秒格式00:00
            s = Integer.parseInt(time.substring(time.length()-2)); //秒  后两位肯定是秒
            s+=Integer.parseInt(time.substring(0,2))*60;    //分钟
        }
        return s;
    }
    public static void main(String[] args) {
        int i=(int)(Math.random()*300)+600;
        System.out.println("i="+i);
        System.out.println(secToTime(i));
        int a=(int)(Math.random()*5100)+300;
        System.out.println("a="+a);
        System.out.println(secToTime(a));
        int s=a+i;
        System.out.println("s="+s);
        System.out.println(secToTime(s));

        System.out.println(getSecond("00:"+secToTime(s)));
    }
}
