package vnu.uet.mobilecourse.assistant.ui.courses;

import android.animation.ArgbEvaluator;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.ViewPager;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;

public class CoursesViewModel extends ViewModel {



    private MutableLiveData<String> mText;

    public CoursesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");




    }

    public LiveData<String> getText() {
        return mText;
    }
}