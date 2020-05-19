package vnu.uet.mobilecourse.assistant.model.firebase;

public class User {
    private String studentId;
    private String name;
    private String uetClass;
    private String DOB;
    private String avatar;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUetClass() {
        return uetClass;
    }

    public void setUetClass(String uetClass) {
        this.uetClass = uetClass;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
