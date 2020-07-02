package vnu.uet.mobilecourse.assistant.model.notification;

import com.google.firebase.firestore.Exclude;

import vnu.uet.mobilecourse.assistant.model.Material;

public class NewMaterialNotification extends Notification_UserSubCol {

    private int courseId;

    private int materialId;

    private String materialType;

    @Exclude
    private Material material;

    public NewMaterialNotification() {
        super(NotificationType.MATERIAL);
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

    @Exclude
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }
}
