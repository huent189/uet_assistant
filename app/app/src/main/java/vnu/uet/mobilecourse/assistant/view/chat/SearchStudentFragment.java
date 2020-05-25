package vnu.uet.mobilecourse.assistant.view.chat;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.viewmodel.SearchStudentViewModel;

public class SearchStudentFragment extends Fragment {

    private SearchStudentViewModel mViewModel;

    public static SearchStudentFragment newInstance() {
        return new SearchStudentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_student, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SearchStudentViewModel.class);
        // TODO: Use the ViewModel
    }

}
