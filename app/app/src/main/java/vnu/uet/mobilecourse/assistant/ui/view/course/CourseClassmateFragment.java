package vnu.uet.mobilecourse.assistant.ui.view.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.graphics.drawable.Drawable;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vnu.uet.mobilecourse.assistant.ui.adapter.ClassMateAdapter;
import vnu.uet.mobilecourse.assistant.ui.adapter.GradeAdapter;
import vnu.uet.mobilecourse.assistant.ui.model.Course;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.CourseClassmateViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.CourseGradeViewModel;

public class CourseClassmateFragment extends Fragment {

    private CourseClassmateViewModel viewModel;

    private ClassMateAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_course_classmate, container, false);

        viewModel = new ViewModelProvider(this).get(CourseClassmateViewModel.class);

        initializeClassMateListView(root);

        return root;
    }

    private void initializeClassMateListView(View root) {
        adapter = new ClassMateAdapter(viewModel.getClassMates().getValue(), this);

        RecyclerView rvClassMate = root.findViewById(R.id.rvClassMate);

        rvClassMate.setAdapter(adapter);
        rvClassMate.setLayoutManager(new LinearLayoutManager(this.getContext()));

        viewModel.getClassMates().observe(getViewLifecycleOwner(), mates -> adapter.notifyDataSetChanged());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        inflater.inflate(R.menu.course_toolbar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        Drawable background = ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_background);
        searchView.setBackground(background);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
