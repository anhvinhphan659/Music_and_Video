package com.example.music_and_video.util;

public class TimeConverter {
    public static double convertStringToSeconds(String inputTime)
    {
        //inputtime must have type: hh:mm:ss
        String[] timeArrays=inputTime.split(":");
        double ret=0;
        if(timeArrays.length!=3)
            return -1;
        ret=Double.parseDouble(timeArrays[0])*3600+Double.parseDouble(timeArrays[1])*60+Double.parseDouble(timeArrays[2]);
        return ret;
    }
    public static String convertSecondToStr(int seconds)
    {
        int second=seconds;
        int hh=second/3600;
        int mm=(second-hh*3600)/60;
        int ss=second-hh*3600-mm*60;
        String ret="";
        if(hh<10)
            ret+="0";
        ret+=String.valueOf(hh)+":";
        if(mm<10)
            ret+="0";
        ret+=String.valueOf(mm)+":";
        if(ss<10)
            ret+="0";
        ret+=String.valueOf(ss);
        return  ret;
    }

}
