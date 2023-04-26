package com.example.comicall.comment;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Comment implements Serializable {
    private String username;
    private String comment;
    private boolean justAdded;
    @ServerTimestamp
    private Date timestamp;

    private static final int SECOND_MILLIS = 1;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public Comment(String username, String comment, Date timestamp,boolean justAdded){
        this.username = username;
        this.comment = comment;
        this.timestamp = timestamp;
        this.justAdded = justAdded;
    }

    public String getComment(){ return comment; }
    public String getUsername(){
        return username;
    }
    public Date getTimestamp(){ return timestamp; }
    public boolean getJustAdded(){return justAdded;}
    public void setJustAdded(boolean justAddedNew){
        this.justAdded = justAddedNew;
    }
    public String getTimeAgo(long time) {
        long now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        if(time > now || time <= 0){
            return "";
        }
        long diff = now - time;
        if(diff < MINUTE_MILLIS){
            return "Justo ahora";
        }
        else if(diff < 2 * MINUTE_MILLIS){
            return "Hace un minuto";
        }
        else if(diff < 60*MINUTE_MILLIS){
            long time_dif = diff/MINUTE_MILLIS;
            return "Hace "+time_dif+ " minutos";
        }
        else if(diff < 2*HOUR_MILLIS){
            return "Hace una hora";
        }
        else if (diff < 24*HOUR_MILLIS) {
            long time_dif = diff/HOUR_MILLIS;
            return "Hace "+time_dif + " horas";
        }
        else if(diff < 48*HOUR_MILLIS){
            return "Ayer";
        }
        else{
            long time_diff = diff/DAY_MILLIS;
            return "Hace " + time_diff + " días";
        }
    }
}
