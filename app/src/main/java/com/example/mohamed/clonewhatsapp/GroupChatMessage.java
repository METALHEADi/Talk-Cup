package com.example.mohamed.clonewhatsapp;

public class GroupChatMessage {
    private String Date,Message,Name,Time,from;

    public GroupChatMessage() {
    }

    public GroupChatMessage(String date, String message, String name, String time, String from) {
        Date = date;
        Message = message;
        Name = name;
        Time = time;
        this.from = from;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
