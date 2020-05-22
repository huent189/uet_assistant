package vnu.uet.mobilecourse.assistant.view.chat;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.MessageAdapter;
import vnu.uet.mobilecourse.assistant.viewmodel.ChatRoomViewModel;

public class ChatRoomFragment extends Fragment {

    private MessageAdapter mMessageAdapter;

    private ChatRoomViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat_room, container, false);

        EditText etMessage= root.findViewById(R.id.etMessage);
        etMessage.setMovementMethod(new ScrollingMovementMethod());

        initializeListView(root);

        return root;
    }

    private void initializeListView(View root) {
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            messages.add("Tin nhắn số " + i);

        mMessageAdapter = new MessageAdapter(messages, this);

        RecyclerView rvChat = root.findViewById(R.id.rvChat);
        rvChat.setHasFixedSize(true);

        rvChat.setAdapter(mMessageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setStackFromEnd(true);

        rvChat.setLayoutManager(linearLayoutManager);
    }
}
