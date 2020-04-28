package vnu.uet.mobilecourse.assistant.view.course;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

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
import android.view.View;
import android.view.ViewGroup;

import vnu.uet.mobilecourse.assistant.adapter.ClassMateAdapter;
import vnu.uet.mobilecourse.assistant.viewmodel.CourseClassmateViewModel;
import vnu.uet.mobilecourse.assistant.R;

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
