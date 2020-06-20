package vnu.uet.mobilecourse.assistant.view.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.MessageAdapter;
import vnu.uet.mobilecourse.assistant.exception.DocumentNotFoundException;
import vnu.uet.mobilecourse.assistant.model.firebase.Message_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.ChatRoomViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class ChatRoomFragment extends Fragment {

    private MessageAdapter mMessageAdapter;

    private ChatRoomViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat_room, container, false);

        EditText etMessage= root.findViewById(R.id.etMessage);
        etMessage.setMovementMethod(new ScrollingMovementMethod());

        Bundle args = getArguments();
        if (args != null) {
            String title = args.getString("title");
            TextView tvChatGroupTitle = root.findViewById(R.id.tvRoomTitle);
            tvChatGroupTitle.setText(title);

            RecyclerView rvChat = initializeListView(root);

            String code = args.getString("code");
            mViewModel.getAllMessages(code).observe(getViewLifecycleOwner(), new Observer<StateModel<List<Message_GroupChatSubCol>>>() {
                @Override
                public void onChanged(StateModel<List<Message_GroupChatSubCol>> stateModel) {
                    Log.d(ChatRoomFragment.class.getSimpleName(), "onChanged: " + stateModel.getStatus());

                    switch (stateModel.getStatus()) {
                        case SUCCESS:
                            List<Message_GroupChatSubCol> messages = stateModel.getData();

                            for (int i = 0; i < 20; i++) {
                                Message_GroupChatSubCol message = new Message_GroupChatSubCol();
                                message.setFromName("safsfs");
                                message.setContent("Tin nhắn " + i);
                                message.setTimestamp(System.currentTimeMillis() / 1000);
                                messages.add(message);
                                if (i % 3 == 0) message.setFromId("17020845");
                            }

                            mMessageAdapter = new MessageAdapter(messages, ChatRoomFragment.this);
                            rvChat.setAdapter(mMessageAdapter);

                            break;

                        case ERROR:
                            Exception error = stateModel.getError();
                            if (error instanceof DocumentNotFoundException) {
                                Toast.makeText(getContext(), "Chat room not created yet.", Toast.LENGTH_SHORT).show();
                                Log.d(ChatRoomFragment.class.getSimpleName(), "Chat room not created yet.");
                            } else {
                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d(ChatRoomFragment.class.getSimpleName(), "Error: " + error.getMessage());
                            }

                            break;
                    }
                }
            });
        }

        return root;
    }

    private RecyclerView initializeListView(View root) {
        RecyclerView rvChat = root.findViewById(R.id.rvChat);
        rvChat.setHasFixedSize(true);

        rvChat.setAdapter(mMessageAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        linearLayoutManager.setStackFromEnd(true);

        rvChat.setLayoutManager(linearLayoutManager);

        return rvChat;
    }
}
