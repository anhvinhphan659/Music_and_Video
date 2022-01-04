package com.example.music_and_video.custom;

public class VJItem {
    private String name;
    private String size;
    private String duration;
    private String path;

    public VJItem(String name, String size, String duration, String path) {
        this.name = name;
        this.size = size;
        this.duration = duration;
        this.path = path;
    }

    public String getName() {
        return  name;
    }
    public String getSize() {
        return  size;
    }
    public String getDuration() {
        return  duration;
    }
    public String getPath() {
        return  path;
    }

    @Override
    public String toString() {
        return this.path;
    }
}
