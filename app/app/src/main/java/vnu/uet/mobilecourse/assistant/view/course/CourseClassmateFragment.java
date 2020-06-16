package vnu.uet.mobilecourse.assistant.view.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.ClassMateAdapter;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.util.StringConst;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseClassmateViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

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
        RecyclerView rvClassMate = root.findViewById(R.id.rvClassMate);
        rvClassMate.setLayoutManager(new LinearLayoutManager(this.getContext()));

        Bundle args = getArguments();

        if (args != null) {
            ICourse course = args.getParcelable("course");
            assert course != null;
            String courseCode = course.getCode();

            if (courseCode != null && !courseCode.isEmpty()) {
                courseCode = courseCode
                        .replace(StringConst.COURSE_PREFIX + StringConst.UNDERSCORE_CHAR, StringConst.EMPTY)
                        .replace(StringConst.UNDERSCORE_CHAR, StringConst.SPACE_CHAR);

                mViewModel.getClassMates(courseCode).observe(getViewLifecycleOwner(), stateModel -> {
                    if (stateModel.getStatus() == StateStatus.SUCCESS) {
                        mAdapter = new ClassMateAdapter(stateModel.getData(), CourseClassmateFragment.this);
                        rvClassMate.setAdapter(mAdapter);
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
