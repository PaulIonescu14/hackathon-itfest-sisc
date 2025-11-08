package com.example.taskforce;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class Task implements Serializable {

    public String title;
    public String author;
    public String group;
    public String deadline;
    public int importance;
    public String pathToImage;
    public String notes;

    public Task(String title, String author, String group, String deadline, int importance, String pathToImage, String notes) {
        this.title = title;
        this.author = author;
        this.deadline = deadline;
        this.importance = importance;
        this.pathToImage = pathToImage;
        this.notes = notes;
    }


}
