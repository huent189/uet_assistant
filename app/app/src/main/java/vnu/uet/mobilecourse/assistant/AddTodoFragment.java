package vnu.uet.mobilecourse.assistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import vnu.uet.mobilecourse.assistant.adapter.TodoListPickerAdapter;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;
import vnu.uet.mobilecourse.assistant.repository.TodoRepository;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTodoFragment extends Fragment {

    private AddTodoViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Fragment fragment = this;

        View root = inflater.inflate(R.layout.fragment_add_todo, container, false);

        initializeToolbar(root);

        Spinner spinner = root.findViewById(R.id.spTodoListTitle);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, Arrays.asList("Hee", "sdf"));

        spinner.setAdapter(arrayAdapter);

        RecyclerView rvAllTodoLists = root.findViewById(R.id.rvAllTodoLists);
        rvAllTodoLists.setLayoutManager(new LinearLayoutManager(getContext()));

        TodoRepository.getInstance().getAllTodoLists().observe(getViewLifecycleOwner(), new Observer<List<TodoList>>() {
            @Override
            public void onChanged(List<TodoList> todoLists) {
                TodoListPickerAdapter adapter = new TodoListPickerAdapter(todoLists, fragment);
                rvAllTodoLists.setAdapter(adapter);
            }
        });

        CardView cvTodoListPicker = root.findViewById(R.id.cvTodoListPicker);

        ImageButton btnExpand = root.findViewById(R.id.btnExpand);
        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentVisibility = rvAllTodoLists.getVisibility();

                if (currentVisibility == View.VISIBLE) {
                    Transition transition = new AutoTransition();
                    transition.addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(@NonNull Transition transition) {

                        }

                        @Override
                        public void onTransitionEnd(@NonNull Transition transition) {
                            rvAllTodoLists.setVisibility(View.GONE);
                        }

                        @Override
                        public void onTransitionCancel(@NonNull Transition transition) {

                        }

                        @Override
                        public void onTransitionPause(@NonNull Transition transition) {

                        }

                        @Override
                        public void onTransitionResume(@NonNull Transition transition) {

                        }
                    });
                    TransitionManager.beginDelayedTransition(cvTodoListPicker, transition);
                    rvAllTodoLists.setVisibility(View.INVISIBLE);

                } else if (currentVisibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(cvTodoListPicker, new AutoTransition());
                    rvAllTodoLists.setVisibility(View.VISIBLE);
                }
            }
        });

        EditText etDate = root.findViewById(R.id.etDate);

        Bundle args = getArguments();
        if (args != null) {
            String currentDate = args.getString("currentDate");
            etDate.setText(currentDate);
        }

        Button btnTime = root.findViewById(R.id.btnTime);
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int HOUR = calendar.get(Calendar.HOUR);
                int MINUTE = calendar.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = String.format(Locale.ROOT, "%02d:%02d", hourOfDay, minute);
                        btnTime.setText(selectedTime);
                    }
                }, HOUR, MINUTE, true);

                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Thoát", dialog);
                dialog.show();
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddTodoViewModel.class);
        // TODO: Use the ViewModel
    }

    private void initializeToolbar(View root) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (activity != null) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            activity.setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
    }
}
