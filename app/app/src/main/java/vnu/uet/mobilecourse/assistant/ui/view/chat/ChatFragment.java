package vnu.uet.mobilecourse.assistant.ui.view.chat;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.adapter.CourseTaskAdapter;
import vnu.uet.mobilecourse.assistant.ui.model.CourseTask;
import vnu.uet.mobilecourse.assistant.ui.model.WeeklyTasks;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.ChatViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        return root;
    }

}
