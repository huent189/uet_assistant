package vnu.uet.mobilecourse.assistant.view.calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarSharedViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.util.Util;

public class AddTodoListFragment extends Fragment {

    private CalendarSharedViewModel mViewModel;
    private NavController mNavController;
    private FragmentActivity mActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FragmentActivity activity = getActivity();
        if (activity != null) {
            mViewModel = new ViewModelProvider(activity).get(CalendarSharedViewModel.class);
            mNavController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_add_todo_list, container, false);

        EditText etTitle = root.findViewById(R.id.etTitle);
        EditText etDescription = root.findViewById(R.id.etDescription);

        Button btnSave = root.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoList todoList = new TodoList();

                String title = etTitle.getText().toString();
                title = title.trim();
                String desc = etDescription.getText().toString();
                todoList.setTitle(title);
                todoList.setDescription(desc);

                @SuppressLint("RestrictedApi")
                String id = Util.autoId();
                todoList.setId(id);

                String ownerId = User.getInstance().getStudentId();
                todoList.setOwnerId(ownerId);

                mViewModel.addTodoList(todoList).observe(getViewLifecycleOwner(), new Observer<StateModel<TodoList>>() {
                    @Override
                    public void onChanged(StateModel<TodoList> stateModel) {
                        switch (stateModel.getStatus()) {
                            case ERROR:
                                Toast.makeText(getContext(), FAILURE_MESSAGE, Toast.LENGTH_LONG).show();
                                break;

                            default:
                                mViewModel.getTodoListTitle().postValue(todoList.getTitle());
                                mNavController.navigateUp();
                                break;
                        }
                    }
                });

            }
        });

        initializeToolbar(root);

        return root;
    }

    private void initializeToolbar(View root) {
        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
        }
    }

    private static final String FAILURE_MESSAGE = "Tạo danh sách không thành công";
}
