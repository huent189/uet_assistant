package vnu.uet.mobilecourse.assistant.view.chat;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.ClassMateAdapter;
import vnu.uet.mobilecourse.assistant.adapter.MessageAdapter;
import vnu.uet.mobilecourse.assistant.view.MyCoursesActivity;
import vnu.uet.mobilecourse.assistant.viewmodel.ChatViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private MessageAdapter adapter;

    private ChatViewModel chatViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

//        FragmentActivity activity = getActivity();
//
//        if (activity instanceof MyCoursesActivity) {
//            ((MyCoursesActivity) activity).getNavView().setVisibility(View.GONE);
//        }

        initializeListView(root);

        return root;
    }

    private void initializeListView(View root) {
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            messages.add("Tin nhắn số " + i);

        adapter = new MessageAdapter(messages, this);

        RecyclerView rvChat = root.findViewById(R.id.rvChat);

        rvChat.setAdapter(adapter);
        rvChat.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }
}
