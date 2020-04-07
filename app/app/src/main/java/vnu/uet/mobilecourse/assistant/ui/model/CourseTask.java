package vnu.uet.mobilecourse.assistant.ui.model;

public class CourseTask {
    private boolean isDone;

    private String title;

    private String desc;

    private int thumbnail;

    public CourseTask(String title, String desc, int thumbnail) {
        this.title = title;
        this.desc = desc;
        this.thumbnail = thumbnail;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}
