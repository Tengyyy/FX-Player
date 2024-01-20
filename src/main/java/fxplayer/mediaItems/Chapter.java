package fxplayer.mediaItems;

import javafx.util.Duration;

public class Chapter {
    String title;
    Duration startTime;

    public Chapter(String title, Duration startTime){
        this.title = title;
        this.startTime = startTime;
    }

    public String getTitle(){
        return title;
    }

    public Duration getStartTime(){
        return startTime;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setStartTime(Duration startTime){
        this.startTime = startTime;
    }
}
