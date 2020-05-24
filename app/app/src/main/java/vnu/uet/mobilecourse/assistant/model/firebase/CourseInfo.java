package vnu.uet.mobilecourse.assistant.model.firebase;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseInfo implements IFirebaseModel {

    private String name;
    private String id;

    private Map<String, CourseSession> sessions = new HashMap<>();

    @Exclude
    private List<Participant_CourseSubCol> participants = new ArrayList<>();

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

    public void setSessions(Map<String, CourseSession> sessions) {
        this.sessions = sessions;
    }

    public Map<String, CourseSession> getSessions() {
        return sessions;
    }

    public void setParticipants(List<Participant_CourseSubCol> participants) {
        this.participants = participants;
    }

    @Exclude
    public List<Participant_CourseSubCol> getParticipants() {
        return participants;
    }
}
