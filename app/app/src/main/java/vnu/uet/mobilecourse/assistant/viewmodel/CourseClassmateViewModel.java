package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class CourseClassmateViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<List<String>> mClassMates;

    public CourseClassmateViewModel() {
        mClassMates = new MutableLiveData<>();

        List<String> mates = new ArrayList<>();

        for (int i = 1; i < 20; i++) {
            mates.add("Bạn số " + i);
        }

        mClassMates.setValue(mates);
    }

    public LiveData<List<String>> getClassMates() {
        return mClassMates;
    }
}
