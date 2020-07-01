package vnu.uet.mobilecourse.assistant.view.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.ChatGroupAdapter;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.StringConst;
import vnu.uet.mobilecourse.assistant.view.component.AvatarView;
import vnu.uet.mobilecourse.assistant.viewmodel.AddGroupChatViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.ChatViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class ChatFragment extends Fragment {

    private ChatViewModel mViewModel;
    private FragmentActivity mActivity;
    private NavController mNavController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        mActivity = getActivity();

        if (mActivity != null) {
            AddGroupChatViewModel prevViewModel = new ViewModelProvider(mActivity).get(AddGroupChatViewModel.class);
            prevViewModel.clearData();

            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        initializeToolbar(root);

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

        Button btnSearch = root.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNavController.navigate(R.id.action_navigation_chat_to_navigation_search_student);
            }
        });

        AvatarView avatarView = root.findViewById(R.id.avatarView);
        avatarView.setLifecycleOwner(getViewLifecycleOwner());
        avatarView.loadUser(User.getInstance().getStudentId());

        return root;
    }

    private Toolbar initializeToolbar(View root) {
        Toolbar toolbar = null;

        if (mActivity instanceof AppCompatActivity) {
            toolbar = root.findViewById(R.id.toolbar);
            toolbar.setTitle(StringConst.EMPTY);
            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }

        return toolbar;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_toolbar_menu, menu);
        Log.d(getTag(), "onCreateOptionsMenu: ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_create) {
            mNavController.navigate(R.id.action_navigation_chat_to_navigation_add_member);
            Log.d(getTag(), "onOptionsItemSelected: ");
        }

        return super.onOptionsItemSelected(item);
    }
}
