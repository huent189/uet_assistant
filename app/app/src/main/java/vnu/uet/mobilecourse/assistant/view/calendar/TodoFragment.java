package vnu.uet.mobilecourse.assistant.view.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
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
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.CalendarSharedViewModel;

public abstract class TodoFragment extends Fragment {

    /**
     * Shared view model between add _todo and add _todo list
     */
    protected CalendarSharedViewModel mViewModel;

    /**
     * Current activity allocate this fragment
     */
    protected FragmentActivity mActivity;

    /**
     * Nav controller
     */
    protected NavController mNavController;

    protected RadioGroup mRgTodoList;
    protected EditText mEtTodoTitle;
    protected EditText mEtDescription;
    protected TextView mTvTodoListTitle;
    protected Button mBtnDate;
    protected Button mBtnTime;
    protected CardView mCvTodoListPicker;
    protected Button mBtnSave;

    private TextView mTvErrorTitle;
    private TextView mTvErrorList;
    private TextView mTvErrorDate;

    protected abstract int getLayoutResource();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();

        if (mActivity != null) {
            mViewModel = new ViewModelProvider(mActivity).get(CalendarSharedViewModel.class);
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(getLayoutResource(), container, false);

        initializeToolbar(root);

        mCvTodoListPicker = root.findViewById(R.id.cvTodoListPicker);

        mRgTodoList = root.findViewById(R.id.rgTodoList);

        mTvTodoListTitle = root.findViewById(R.id.tvTodoListTitle);

        mViewModel.getShallowTodoLists().observe(getViewLifecycleOwner(), stateModel -> {
                    switch (stateModel.getStatus()) {
                        case LOADING:
                        case ERROR:
                            break;

                        case SUCCESS:
                            generateRadioGroup(inflater, stateModel.getData());
                            break;
                    }
                }
        );

        View.OnClickListener onExpandListener = generateOnExpandAnimation();

        mCvTodoListPicker.setOnClickListener(onExpandListener);

        ImageButton btnExpand = root.findViewById(R.id.btnExpand);
        btnExpand.setOnClickListener(onExpandListener);

        mEtDescription = root.findViewById(R.id.etDescription);
        mEtTodoTitle = root.findViewById(R.id.etTodoTitle);

        mBtnDate = root.findViewById(R.id.btnDate);
        Bundle args = getArguments();

        Calendar calendar = Calendar.getInstance();

        if (args != null) {
            String currentDate = args.getString("currentDate");
            mViewModel.setDate(currentDate);

            if (currentDate != null) {
                try {
                    Date date = DateTimeUtils.SHORT_DATE_FORMAT.parse(currentDate);

                    if (date != null) {
                        Calendar prev = Calendar.getInstance();
                        prev.setTime(date);

                        calendar.set(Calendar.YEAR, prev.get(Calendar.YEAR));
                        calendar.set(Calendar.MONTH, prev.get(Calendar.MONTH));
                        calendar.set(Calendar.DAY_OF_MONTH, prev.get(Calendar.DAY_OF_MONTH));
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        Locale locale = new Locale("vi", "VN");
        Locale.setDefault(locale);

        mBtnDate.setOnClickListener(v -> {
            if (mBtnDate.getText().length() == 0) {
                Calendar current = Calendar.getInstance();

                calendar.set(Calendar.YEAR, current.get(Calendar.YEAR));
                calendar.set(Calendar.MONTH, current.get(Calendar.MONTH));
                calendar.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
            }

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    (view, year1, month1, dayOfMonth1) -> {
                        calendar.set(Calendar.YEAR, year1);
                        calendar.set(Calendar.MONTH, month1);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth1);

                        String selectedTime = DateTimeUtils.SHORT_DATE_FORMAT.format(calendar.getTime());
                        mBtnDate.setText(selectedTime);
                    }, year, month, dayOfMonth
            );

            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CANCEL_BUTTON_TITLE, dialog);

            dialog.show();

        });

        mBtnTime = root.findViewById(R.id.btnTime);
        mBtnTime.setOnClickListener(v -> {
            String currentText = mBtnTime.getText().toString();

            if (currentText.isEmpty()) {
                Calendar current = Calendar.getInstance();

                calendar.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, current.get(Calendar.MINUTE));
            } else {
                try {
                    Date date = DateTimeUtils.TIME_12H_FORMAT.parse(currentText);

                    Calendar current = Calendar.getInstance();
                    assert date != null;
                    current.setTime(date);

                    calendar.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, current.get(Calendar.MINUTE));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
            int MINUTE = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(
                    getContext(),
                    (view, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        String selectedTime = DateTimeUtils.TIME_12H_FORMAT.format(calendar.getTime());
                        mBtnTime.setText(selectedTime);
                    },
                    HOUR, MINUTE, false
            );

            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CANCEL_BUTTON_TITLE, dialog);
            dialog.show();
        });

        mViewModel.isCardExpand().observe(getViewLifecycleOwner(), expand -> {
            if (expand)
                mRgTodoList.setVisibility(View.VISIBLE);
            else
                mRgTodoList.setVisibility(View.GONE);
        });

        mViewModel.getTodoDate().observe(getViewLifecycleOwner(), s -> mBtnDate.setText(s));

        mViewModel.getTodoDesc().observe(getViewLifecycleOwner(), s -> mEtDescription.setText(s));

        mViewModel.getTodoTitle().observe(getViewLifecycleOwner(), s -> mEtTodoTitle.setText(s));

        mViewModel.getTodoTime().observe(getViewLifecycleOwner(), s -> mBtnTime.setText(s));

        mBtnSave = root.findViewById(R.id.btnSave);

        mTvErrorTitle = root.findViewById(R.id.tvErrorTitle);
        mTvErrorList = root.findViewById(R.id.tvErrorList);
        mTvErrorDate = root.findViewById(R.id.tvErrorDate);

        return root;
    }

    protected boolean validate() {
        boolean titleError = false;
        String title = mEtTodoTitle.getText().toString();

        if (title.length() < 6) {
            mTvErrorTitle.setVisibility(View.VISIBLE);
            titleError = true;
        } else {
            mTvErrorTitle.setVisibility(View.GONE);
        }

        boolean dateError = false;
        String date = mBtnDate.getText().toString();
        String time = mBtnTime.getText().toString();

        if (date.isEmpty() || time.isEmpty()) {
            mTvErrorDate.setVisibility(View.VISIBLE);
            dateError = true;
        } else {
            mTvErrorDate.setVisibility(View.GONE);
        }

        boolean listError = false;
        String list = mTvTodoListTitle.getText().toString();

        if (list.isEmpty()) {
            mTvErrorList.setVisibility(View.VISIBLE);
            listError = true;
        } else {
            mTvErrorList.setVisibility(View.GONE);
        }
        
        return !listError && !titleError && !dateError;
    }

    private void generateRadioGroup(LayoutInflater inflater, List<TodoList> todoLists) {
        // delete all exist radio button
        mRgTodoList.removeAllViews();

        // map todoList to corresponding radio button
        todoLists.forEach(todoList -> {
            RadioButton radioButton = (RadioButton)
                    inflater.inflate(R.layout.layout_radio_button, mRgTodoList, false);

            String title = todoList.getTitle();
            String listId = todoList.getId();

            radioButton.setText(title);

            int id = todoLists.indexOf(todoList);

            radioButton.setId(id);

            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked)
                    mTvTodoListTitle.setText(title);
            });

            if (title.equals(mViewModel.getTodoListTitle().getValue())
                    || listId.equals(mViewModel.getTodoListId())) {
                radioButton.setChecked(true);
                mTvTodoListTitle.setText(title);
            }

            mRgTodoList.addView(radioButton);
        });

        // addition radio button to create new todoList
        RadioButton radioButton = (RadioButton)
                inflater.inflate(R.layout.layout_radio_button, mRgTodoList, false);

        radioButton.setText(ADD_NEW_TODO_LIST_TITLE);
        radioButton.setId(-1);

        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                mNavController.navigate(getCreateTodoListActionId());
        });

        mRgTodoList.addView(radioButton);
    }

    protected abstract int getCreateTodoListActionId();

    private void initializeToolbar(View root) {
        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
    }

    private View.OnClickListener generateOnExpandAnimation() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current visibility
                int currentVisibility = mRgTodoList.getVisibility();

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
                            // change expand state in shared view model to false
                            mViewModel.isCardExpand().postValue(false);
                            mRgTodoList.setVisibility(View.GONE);
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
                    TransitionManager.beginDelayedTransition(mCvTodoListPicker, transition);
                    mRgTodoList.setVisibility(View.INVISIBLE);

                } else if (currentVisibility == View.GONE) {
                    // apply expand animation
                    TransitionManager.beginDelayedTransition(mCvTodoListPicker, new AutoTransition());

                    // show radio group when animate finish
                    mRgTodoList.setVisibility(View.VISIBLE);

                    // change expand state in shared view model to true
                    mViewModel.isCardExpand().postValue(true);
                }
            }
        };
    }

    protected void showFailureToast(Exception e) {
        Toast.makeText(getContext(), "Tạo thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * save user input into shared view model before navigate
     */
    @Override
    public void onPause() {
        String currentTodoTitle = mEtTodoTitle.getText().toString();
        mViewModel.getTodoTitle().postValue(currentTodoTitle);

        String currentTodoDesc = mEtDescription.getText().toString();
        mViewModel.getTodoDesc().postValue(currentTodoDesc);

        String currentTodoDate = mBtnDate.getText().toString();
        mViewModel.getTodoDate().postValue(currentTodoDate);

        String currentTodoTime = mBtnTime.getText().toString();
        mViewModel.getTodoTime().postValue(currentTodoTime);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.clearHistory();
    }

    private static final String ADD_NEW_TODO_LIST_TITLE = "Thêm danh sách mới";
    private static final String CANCEL_BUTTON_TITLE = "Thoát";
}
