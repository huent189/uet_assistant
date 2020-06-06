package vnu.uet.mobilecourse.assistant.network.response;

import com.google.gson.annotations.SerializedName;
import vnu.uet.mobilecourse.assistant.util.StringUtils;

public class PageContentResponse {
    @SerializedName("coursemodule")
    private int id;
    @SerializedName("course")
    private int courseId;
    @SerializedName("name")
    private String name;
    @SerializedName("intro")
    private String intro;
    @SerializedName("content")
    private String content;
    @SerializedName("timemodified")
    private long timeModified;

    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getContent() {
        return StringUtils.mergePageContent(intro, content);
    }

    public long getTimeModified() {
        return timeModified;
    }

    @Override
    public String toString() {
        return "PageContentResponse{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", name='" + name + '\'' +
                ", intro='" + intro + '\'' +
                ", content='" + content + '\'' +
                ", timeModified=" + timeModified +
                '}';
    }
}
