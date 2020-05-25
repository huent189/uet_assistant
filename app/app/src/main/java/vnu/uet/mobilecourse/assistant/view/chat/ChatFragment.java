package vnu.uet.mobilecourse.assistant.view.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.ChatGroupAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.viewmodel.ChatViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class ChatFragment extends Fragment {

    private ChatViewModel mViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView rvChatGroups = root.findViewById(R.id.rvChatGroups);
        rvChatGroups.setLayoutManager(new LinearLayoutManager(getContext()));

        mViewModel.getGroupChats().observe(getViewLifecycleOwner(), new Observer<StateModel<List<GroupChat_UserSubCol>>>() {
            @Override
            public void onChanged(StateModel<List<GroupChat_UserSubCol>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        ChatGroupAdapter adapter = new ChatGroupAdapter(stateModel.getData(), ChatFragment.this);
                        rvChatGroups.setAdapter(adapter);
                        break;
                }
            }
        });

        EditText etSearch= root.findViewById(R.id.etSearch);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: navigate to search chat room/student fragment
            }
        });

        return root;
    }
}
