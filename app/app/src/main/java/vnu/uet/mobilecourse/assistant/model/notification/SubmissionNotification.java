package vnu.uet.mobilecourse.assistant.model.notification;

import com.google.firebase.firestore.Exclude;

import vnu.uet.mobilecourse.assistant.model.Material;

public class SubmissionNotification extends Notification_UserSubCol {

    private String courseCode;
    private int courseId;
    private int materialId;
    private String materialType;

    @Exclude
    private Material material;

    public SubmissionNotification() {
        super(NotificationType.ATTENDANCE);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    @Exclude
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String type) {
        this.materialType = type;
    }
}
