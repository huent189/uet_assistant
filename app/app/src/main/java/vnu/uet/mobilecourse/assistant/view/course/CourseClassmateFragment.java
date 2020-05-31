package vnu.uet.mobilecourse.assistant.view.course;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import vnu.uet.mobilecourse.assistant.adapter.ClassMateAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.Participant_CourseSubCol;
import vnu.uet.mobilecourse.assistant.util.CONST;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseClassmateViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class CourseClassmateFragment extends Fragment {

    private CourseClassmateViewModel mViewModel;

    private ClassMateAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_course_classmate, container, false);

        mViewModel = new ViewModelProvider(this).get(CourseClassmateViewModel.class);

        initializeClassMateListView(root);

        return root;
    }

    private void initializeClassMateListView(View root) {
//        mAdapter = new ClassMateAdapter(mViewModel.getClassMates().getValue(), this);

        RecyclerView rvClassMate = root.findViewById(R.id.rvClassMate);
        rvClassMate.setLayoutManager(new LinearLayoutManager(this.getContext()));

        Bundle args = getArguments();
        if (args != null) {
            String id = args.getString("courseCode");

            if (id != null && !id.isEmpty()) {
                id = id.replace(CONST.COURSE_PREFIX + CONST.UNDERSCORE, "")
                        .replace(CONST.UNDERSCORE, CONST.SPACE);

                mViewModel.getClassMates(id).observe(getViewLifecycleOwner(), new Observer<StateModel<List<Participant_CourseSubCol>>>() {
                    @Override
                    public void onChanged(StateModel<List<Participant_CourseSubCol>> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                mAdapter = new ClassMateAdapter(stateModel.getData(), CourseClassmateFragment.this);
                                rvClassMate.setAdapter(mAdapter);
                                break;
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.course_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

//        Drawable background = ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_background);
//        searchView.setBackground(background);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
