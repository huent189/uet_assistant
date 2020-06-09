package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;
import vnu.uet.mobilecourse.assistant.repository.firebase.ParticipantRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

import java.util.ArrayList;
import java.util.List;

public class CourseClassmateViewModel extends ViewModel {

    private ParticipantRepository participantRepo = ParticipantRepository.getInstance();

    public IStateLiveData<List<Participant_CourseSubCol>> getClassMates(String courseId) {
        return participantRepo.getAllParticipants(courseId);
    }
}
