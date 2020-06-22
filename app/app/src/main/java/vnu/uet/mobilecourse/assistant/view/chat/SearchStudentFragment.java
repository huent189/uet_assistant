package vnu.uet.mobilecourse.assistant.view.chat;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.adapter.ChatGroupAdapter;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.viewmodel.SearchStudentViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class SearchStudentFragment extends Fragment {

    private static final String TAG = SearchStudentFragment.class.getSimpleName();

    private SearchStudentViewModel mViewModel;
    private FragmentActivity mActivity;
    private NavController mNavController;
    private ChatGroupAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(SearchStudentViewModel.class);

        mActivity = getActivity();
        if (mActivity != null) {
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_search_student, container, false);

        SearchView searchView = root.findViewById(R.id.searchView);
        searchView.onActionViewExpanded();
        searchView.setIconified(false);

        TextView tvSearchResultTitle = root.findViewById(R.id.tvSearchResultTitle);
        View layoutSearchResult = root.findViewById(R.id.layoutSearchResult);

        TextView tvName = layoutSearchResult.findViewById(R.id.tvName);
        TextView tvId = layoutSearchResult.findViewById(R.id.tvId);
        ImageButton btnChat = layoutSearchResult.findViewById(R.id.btnChat);

        ShimmerFrameLayout shimmerSearchResult = root.findViewById(R.id.shimmerSearchResult);
        shimmerSearchResult.startShimmerAnimation();

        tvSearchResultTitle.setVisibility(View.GONE);
        layoutSearchResult.setVisibility(View.GONE);
        shimmerSearchResult.setVisibility(View.GONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);

                if (!newText.matches("[0-9]+")) {
                    tvSearchResultTitle.setVisibility(View.GONE);
                    layoutSearchResult.setVisibility(View.GONE);
                    shimmerSearchResult.setVisibility(View.GONE);
                    return false;
                }

                if (newText.length() != 8) {
                    tvSearchResultTitle.setVisibility(View.VISIBLE);
                    layoutSearchResult.setVisibility(View.GONE);
                    shimmerSearchResult.setVisibility(View.VISIBLE);
                    return false;
                }

                long count = mAdapter.getVisibleRooms().stream()
                        .filter(room -> room.getType().equals(GroupChat.DIRECT)
                                && room.getId().contains(newText)
                        ).count();

                if (count > 0) {
                    tvSearchResultTitle.setVisibility(View.GONE);
                    layoutSearchResult.setVisibility(View.GONE);
                    shimmerSearchResult.setVisibility(View.GONE);
                    return false;
                }

                mViewModel.searchStudent(newText).observe(getViewLifecycleOwner(), new Observer<StateModel<UserInfo>>() {
                    @Override
                    public void onChanged(StateModel<UserInfo> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                tvSearchResultTitle.setVisibility(View.VISIBLE);
                                layoutSearchResult.setVisibility(View.VISIBLE);
                                shimmerSearchResult.setVisibility(View.GONE);

                                UserInfo userInfo = stateModel.getData();
                                tvName.setText(userInfo.getName());
                                tvId.setText(userInfo.getId());

                                if (!userInfo.isActive()) {
                                    btnChat.setImageResource(R.drawable.ic_warning_24dp);
                                    btnChat.setBackgroundResource(R.color.background);
                                    btnChat.setClickable(false);
                                    btnChat.setEnabled(false);
                                } else {
                                    btnChat.setImageResource(R.drawable.ic_chat_24dp);
                                    btnChat.setBackgroundResource(R.drawable.primary_button_background);
                                    btnChat.setClickable(true);
                                    btnChat.setEnabled(true);
                                }

                                break;

                            case LOADING:
                                tvSearchResultTitle.setVisibility(View.VISIBLE);
                                layoutSearchResult.setVisibility(View.GONE);
                                shimmerSearchResult.setVisibility(View.VISIBLE);
                                Log.i(TAG, "Loading");
                                break;

                            case ERROR:
                                tvSearchResultTitle.setVisibility(View.GONE);
                                layoutSearchResult.setVisibility(View.GONE);
                                shimmerSearchResult.setVisibility(View.GONE);
                                Log.e(TAG, "Error");
                                break;
                        }
                    }
                });

                return false;
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                String title = tvName.getText().toString();
                bundle.putString("title", title);

                String code = tvId.getText().toString();
                bundle.putString("code", code);

                bundle.putString("type", GroupChat.DIRECT);

                mNavController.navigate(R.id.action_navigation_search_student_to_navigation_chat_room, bundle);
            }
        });

        RecyclerView rvChatGroups = root.findViewById(R.id.rvChatGroups);
        mViewModel.getGroupChats().observe(getViewLifecycleOwner(), new Observer<StateModel<List<GroupChat_UserSubCol>>>() {
            @Override
            public void onChanged(StateModel<List<GroupChat_UserSubCol>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        mAdapter = new ChatGroupAdapter(stateModel.getData(), SearchStudentFragment.this);
                        rvChatGroups.setAdapter(mAdapter);
                        break;
                }
            }
        });

        return root;
    }
}
