package vnu.uet.mobilecourse.assistant.model.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.util.CONST;

public class CourseInfo implements IFirebaseModel, ICourse {

    private String name;
    private String id;

    private int credits;

    private List<CourseSession> sessions = new ArrayList<>();

    @Exclude
    private List<Participant_CourseSubCol> participants = new ArrayList<>();

    public CourseInfo() {}

    protected CourseInfo(Parcel in) {
        name = in.readString();
        id = in.readString();
        credits = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeInt(credits);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CourseInfo> CREATOR = new Creator<CourseInfo>() {
        @Override
        public CourseInfo createFromParcel(Parcel in) {
            return new CourseInfo(in);
        }

        @Override
        public CourseInfo[] newArray(int size) {
            return new CourseInfo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CourseSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<CourseSession> sessions) {
        this.sessions = sessions;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setParticipants(List<Participant_CourseSubCol> participants) {
        this.participants = participants;
    }

    @Exclude
    public List<Participant_CourseSubCol> getParticipants() {
        return participants;
    }

    @Override
    public String getCode() {
        return CONST.COURSE_PREFIX + CONST.UNDERSCORE + id.replace(CONST.SPACE, CONST.UNDERSCORE);
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof CourseInfo) {
            return ((CourseInfo) obj).id.equals(id);
        }

        return false;
    }
}
