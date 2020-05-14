package vnu.uet.mobilecourse.assistant.view.calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarSharedViewModel;

public class AddTodoFragment extends Fragment {

    private CalendarSharedViewModel mViewModel;

    private RadioGroup rgTodoList;

    private FragmentActivity activity;

    private EditText etTodoTitle;

    private TextView tvTodoListTitle;

    private EditText etDescription;

    private Button btnDate;

    private Button btnTime;

    private CardView cvTodoListPicker;

    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        activity = getActivity();

        if (activity != null) {
            mViewModel = new ViewModelProvider(activity).get(CalendarSharedViewModel.class);
            navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_add_todo, container, false);

        initializeToolbar(root);

        cvTodoListPicker = root.findViewById(R.id.cvTodoListPicker);

        rgTodoList = root.findViewById(R.id.rgTodoList);

        tvTodoListTitle = root.findViewById(R.id.tvTodoListTitle);

        mViewModel.getAllTodoLists().observe(
                getViewLifecycleOwner(),
                todoLists -> generateRadioGroup(inflater, todoLists)
        );

        rgTodoList.setOnCheckedChangeListener((group, checkedId) -> {
            TodoList selected = mViewModel.findTodoListById(checkedId);

            if (selected != null)
                tvTodoListTitle.setText(selected.getTitle());
        });

        View.OnClickListener onExpandListener = generateOnExpandAnimation();

        cvTodoListPicker.setOnClickListener(onExpandListener);

        ImageButton btnExpand = root.findViewById(R.id.btnExpand);
        btnExpand.setOnClickListener(onExpandListener);

        etDescription = root.findViewById(R.id.etDescription);
        etTodoTitle = root.findViewById(R.id.etTodoTitle);

        btnDate = root.findViewById(R.id.btnDate);
        Bundle args = getArguments();

        Calendar calendar = Calendar.getInstance();

        if (args != null) {
            String currentDate = args.getString("currentDate");
            btnDate.setText(currentDate);

            if (currentDate != null) {
                try {
                    Date date = DateTimeUtils.SHORT_DATE_FORMAT.parse(currentDate);

                    if (date != null)
                        calendar.setTime(date);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        Locale.setDefault(new Locale("vi", "VN"));

        btnDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                }
            }, year, month, dayOfMonth);

//            DatePickerDialog dialog = new DatePickerDialog(
//                    getContext(),
//                    new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//
//                        }
//                    }, year, month, dayOfMonth);

            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CANCEL_BUTTON_TITLE, dialog);
            dialog.show();
        });

        btnTime = root.findViewById(R.id.btnTime);
        btnTime.setOnClickListener(v -> {
//            Calendar temp = Calendar.getInstance();

            int HOUR = calendar.get(Calendar.HOUR);
            int MINUTE = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    getContext(),
                    (view, hourOfDay, minute) -> {
                        String selectedTime = String.format(Locale.ROOT, "%02d:%02d", hourOfDay, minute);
                        btnTime.setText(selectedTime);
                    },
                    HOUR, MINUTE, true
            );

            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CANCEL_BUTTON_TITLE, dialog);
            dialog.show();
        });

        mViewModel.isCardExpand().observe(getViewLifecycleOwner(), expand -> {
            if (expand)
                rgTodoList.setVisibility(View.VISIBLE);
            else
                rgTodoList.setVisibility(View.GONE);
        });

        mViewModel.getTodoDate().observe(getViewLifecycleOwner(), s -> btnDate.setText(s));

        mViewModel.getTodoDesc().observe(getViewLifecycleOwner(), s -> etDescription.setText(s));

        mViewModel.getTodoTitle().observe(getViewLifecycleOwner(), s -> etTodoTitle.setText(s));

        mViewModel.getTodoTime().observe(getViewLifecycleOwner(), s -> btnTime.setText(s));

        Button btnSave = root.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();

                try {
                    Todo todo = generateTodo();
                    mViewModel.addTodo(todo);

                    Toast.makeText(context,"Tạo thành công", Toast.LENGTH_SHORT).show();

                    navController.navigateUp();

                } catch (ParseException e) {
                    Toast.makeText(context,"Tạo thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void generateRadioGroup(LayoutInflater inflater, List<TodoList> todoLists) {
        // delete all exist radio button
        rgTodoList.removeAllViews();

        // map todoList to corresponding radio button
        todoLists.forEach(todoList -> {
            RadioButton radioButton = (RadioButton)
                    inflater.inflate(R.layout.layout_radio_button, rgTodoList, false);

            String title = todoList.getTitle();

            radioButton.setText(title);

            radioButton.setId(Integer.parseInt(todoList.getId()));

            if (title.equals(mViewModel.getTodoListTitle().getValue()))
                radioButton.setChecked(true);

            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked)
                    tvTodoListTitle.setText(title);
            });

            rgTodoList.addView(radioButton);
        });

        // addition radio button to create new todoList
        RadioButton radioButton = (RadioButton)
                inflater.inflate(R.layout.layout_radio_button, rgTodoList, false);

        radioButton.setText(ADD_NEW_TODO_LIST_TITLE);
        radioButton.setId(-1);

        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                navController.navigate(R.id.action_navigation_add_todo_to_navigation_add_todo_list);
        });

        rgTodoList.addView(radioButton);
    }

    private Todo generateTodo() throws ParseException {
        Todo todo = new Todo();

        String title = etTodoTitle.getText().toString();
        todo.setTitle(title);

        String desc = etDescription.getText().toString();
        todo.setDescription(desc);

        String todoListId = String.valueOf(rgTodoList.getCheckedRadioButtonId());
        todo.setTodoListId(todoListId);

        String date = btnDate.getText().toString();
        String time = btnTime.getText().toString();
        Date deadline = DateTimeUtils.DATE_TIME_FORMAT.parse(date + " " + time);
        todo.setDeadline(deadline);

        return todo;
    }

    private View.OnClickListener generateOnExpandAnimation() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current visibility
                int currentVisibility = rgTodoList.getVisibility();

                // on expand case
                if (currentVisibility == View.VISIBLE) {
                    // create expand animation
                    Transition transition = new AutoTransition();
                    transition.addListener(new Transition.TransitionListener() {

                        @Override
                        public void onTransitionStart(@NonNull Transition transition) {

                        }

                        /**
                         * Hide radio group when animate ends
                         */
                        @Override
                        public void onTransitionEnd(@NonNull Transition transition) {
                            rgTodoList.setVisibility(View.GONE);
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

                    // apply created animation
                    TransitionManager.beginDelayedTransition(cvTodoListPicker, transition);
                    rgTodoList.setVisibility(View.INVISIBLE);

                    // change expand state in shared view model to false
                    mViewModel.isCardExpand().postValue(false);

                } else if (currentVisibility == View.GONE) {
                    // apply expand animation
                    TransitionManager.beginDelayedTransition(cvTodoListPicker, new AutoTransition());

                    // show radio group when animate finish
                    rgTodoList.setVisibility(View.VISIBLE);

                    // change expand state in shared view model to true
                    mViewModel.isCardExpand().postValue(true);
                }
            }
        };
    }

    /**
     * save user input into shared view model before navigate
     */
    @Override
    public void onPause() {
        String currentTodoTitle = etTodoTitle.getText().toString();
        mViewModel.getTodoTitle().postValue(currentTodoTitle);

        String currentTodoDesc = etDescription.getText().toString();
        mViewModel.getTodoDesc().postValue(currentTodoDesc);

        String currentTodoDate = btnDate.getText().toString();
        mViewModel.getTodoDate().postValue(currentTodoDate);

        String currentTodoTime = btnTime.getText().toString();
        mViewModel.getTodoTime().postValue(currentTodoTime);

        super.onPause();
    }

    private void initializeToolbar(View root) {
        if (activity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
    }

    private static final String ADD_NEW_TODO_LIST_TITLE = "Thêm danh sách mới";

    private static final String CANCEL_BUTTON_TITLE = "Thoát";
}