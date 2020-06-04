package vnu.uet.mobilecourse.assistant.viewmodel;

import java.util.List;

import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.firebase.CourseInfo;
import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.ParticipantRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public class CourseGeneralViewModel extends ViewModel {

    private CourseRepository mCourseRepo = CourseRepository.getInstance();

    private ParticipantRepository mParticipantRepo = ParticipantRepository.getInstance();

    public IStateLiveData<CourseInfo> getCourseInfo(String courseId) {
        return mCourseRepo.getCourseInfo(courseId);
    }

    public IStateLiveData<List<Participant_CourseSubCol>> getParticipants(String courseId) {
        return mParticipantRepo.getAllParticipants(courseId);
    }
}
