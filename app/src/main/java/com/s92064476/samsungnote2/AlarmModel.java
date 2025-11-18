package com.s92064476.samsungnote2;

public class AlarmModel {
    private long id;
    private String description;
    private long targetTimeInMillis;
    private long startTimeInMillis; // NEW: Needed for progress bar
    private String formattedTime;

    public AlarmModel(long id, String description, long targetTimeInMillis, long startTimeInMillis, String formattedTime) {
        this.id = id;
        this.description = description;
        this.targetTimeInMillis = targetTimeInMillis;
        this.startTimeInMillis = startTimeInMillis;
        this.formattedTime = formattedTime;
    }

    public long getId() { return id; }
    public String getDescription() { return description; }
    public long getTargetTimeInMillis() { return targetTimeInMillis; }
    public long getStartTimeInMillis() { return startTimeInMillis; }
    public String getFormattedTime() { return formattedTime; }
}