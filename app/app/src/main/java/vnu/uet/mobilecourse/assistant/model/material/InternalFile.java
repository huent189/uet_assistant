package vnu.uet.mobilecourse.assistant.model.material;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
@Entity(indices = {@Index("parentId")})
public class InternalFile {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int parentId;
    @SerializedName("filename")
    private String fileName;
    @SerializedName("fileurl")
    private String fileUrl;
    @SerializedName("mimetype")
    private String mimeType;
    @SerializedName("filesize")
    private long fileSize;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
