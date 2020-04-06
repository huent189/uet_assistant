package vnu.uet.mobilecourse.assistant.ui.model;

public class Course {

    private int thumbnail;

    private String title;

    private String id = "INT3100 2";

    public Course(int thumbnail, String title) {
        this.thumbnail = thumbnail;
        this.title = title;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
