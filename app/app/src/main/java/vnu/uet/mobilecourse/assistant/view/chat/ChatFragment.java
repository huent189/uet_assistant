package vnu.uet.mobilecourse.assistant.view.chat;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.viewmodel.ChatViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        return root;
    }

}
