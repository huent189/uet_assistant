package vnu.uet.mobilecourse.assistant.ui.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.viewmodel.ChatViewModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        final TextView textView = root.findViewById(R.id.text_chat);

        chatViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        return root;
    }

}
