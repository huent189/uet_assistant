package vnu.uet.mobilecourse.assistant.model.material;

public class QuizContent extends QuizNoGrade {
    private int userGrade;

    public int getUserGrade() {
        return userGrade;
    }

    public void setUserGrade(int userGrade) {
        this.userGrade = userGrade;
    }

    @Override
    public String toString() {
        return "QuizContent{" +
                "userGrade=" + userGrade +
                '}';
    }
}
