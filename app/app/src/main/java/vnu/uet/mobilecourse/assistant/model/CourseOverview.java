package vnu.uet.mobilecourse.assistant.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CourseOverview {
    @Embedded
    private WeeklyMaterial weekInfo;
    @Relation(parentColumn = "id", entityColumn = "weekId")
    private List<Material> materials;

    public CourseOverview(WeeklyMaterial weekInfo, List<Material> materials) {
        this.weekInfo = weekInfo;
        this.materials = materials;
    }

    public WeeklyMaterial getWeekInfo() {
        return weekInfo;
    }

    public void setWeekInfo(WeeklyMaterial weekInfo) {
        this.weekInfo = weekInfo;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    @Override
    public String toString() {
        return "CourseOverview{" +
                "weekInfo=" + weekInfo +
                ", materials=" + materials +
                '}';
    }
}
