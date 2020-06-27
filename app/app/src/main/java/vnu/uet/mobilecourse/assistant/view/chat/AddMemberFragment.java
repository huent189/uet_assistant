package vnu.uet.mobilecourse.assistant.view.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.adapter.HorizontalMemberAdapter;
import vnu.uet.mobilecourse.assistant.adapter.SuggestionMemberAdapter;
import vnu.uet.mobilecourse.assistant.model.IStudent;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.viewmodel.AddGroupChatViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class AddMemberFragment extends Fragment {

    private AddGroupChatViewModel mViewModel;
    private FragmentActivity mActivity;
    private NavController mNavController;
    private IStudent mSearchResult;
    private SuggestionMemberAdapter suggestionAdapter = null;
    private HorizontalMemberAdapter mMemberAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mActivity = getActivity();
        if (mActivity != null) {
            mViewModel = new ViewModelProvider(mActivity).get(AddGroupChatViewModel.class);
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_add_member, container, false);

        initializeToolbar(root);

        SearchView searchView = root.findViewById(R.id.searchView);
        searchView.onActionViewExpanded();
        searchView.setIconified(false);

        View layoutSearchResult = root.findViewById(R.id.layoutSearchResult);

        TextView tvName = layoutSearchResult.findViewById(R.id.tvName);
        TextView tvId = layoutSearchResult.findViewById(R.id.tvId);
        CheckBox checkBox = layoutSearchResult.findViewById(R.id.checkbox);
        ImageView ivWarning = layoutSearchResult.findViewById(R.id.ivWarning);

        ShimmerFrameLayout shimmerSearchResult = root.findViewById(R.id.shimmerSearchResult);
        shimmerSearchResult.startShimmerAnimation();

        layoutSearchResult.setVisibility(View.GONE);
        shimmerSearchResult.setVisibility(View.GONE);

        RecyclerView rvMembers = root.findViewById(R.id.rvMembers);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                mActivity,
                LinearLayoutManager.HORIZONTAL,
                false);

        rvMembers.setLayoutManager(layoutManager);

        mViewModel.getSelectedList().observe(getViewLifecycleOwner(), new Observer<List<IStudent>>() {
            @Override
            public void onChanged(List<IStudent> students) {
                mMemberAdapter = new HorizontalMemberAdapter(students, AddMemberFragment.this,
                        new HorizontalMemberAdapter.OnClearListener() {
                            @Override
                            public void onClear(IStudent student) {
                                mViewModel.removeMember(student);
                            }
                        });
                rvMembers.setAdapter(mMemberAdapter);
            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                mAdapter.getFilter().filter(newText);

                if (!newText.matches("[0-9]+")) {
                    layoutSearchResult.setVisibility(View.GONE);
                    shimmerSearchResult.setVisibility(View.GONE);
                    return false;
                }

                if (newText.length() != 8) {
                    layoutSearchResult.setVisibility(View.GONE);
                    shimmerSearchResult.setVisibility(View.VISIBLE);
                    return false;
                }

//                long count = mAdapter.getVisibleRooms().stream()
//                        .filter(room -> room.getType().equals(GroupChat.DIRECT)
//                                && room.getId().contains(newText)
//                        ).count();
//
//                if (count > 0) {
//                    tvSearchResultTitle.setVisibility(View.GONE);
//                    layoutSearchResult.setVisibility(View.GONE);
//                    shimmerSearchResult.setVisibility(View.GONE);
//                    return false;
//                }

                mViewModel.searchStudent(newText).observe(getViewLifecycleOwner(), new Observer<StateModel<UserInfo>>() {
                    @Override
                    public void onChanged(StateModel<UserInfo> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                layoutSearchResult.setVisibility(View.VISIBLE);
                                shimmerSearchResult.setVisibility(View.GONE);

                                mSearchResult = stateModel.getData();
                                tvName.setText(mSearchResult.getName());
                                tvId.setText(mSearchResult.getCode());

                                mViewModel.isSelected(mSearchResult).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                                    @Override
                                    public void onChanged(Boolean aBoolean) {
                                        checkBox.setChecked(aBoolean);
                                    }
                                });

                                if (mSearchResult.isActive()) {
                                    ivWarning.setVisibility(View.GONE);
                                    checkBox.setVisibility(View.VISIBLE);
                                } else {
                                    ivWarning.setVisibility(View.VISIBLE);
                                    checkBox.setVisibility(View.GONE);
                                }

                                break;

                            case LOADING:
                                layoutSearchResult.setVisibility(View.GONE);
                                shimmerSearchResult.setVisibility(View.VISIBLE);
                                break;

                            case ERROR:
                                layoutSearchResult.setVisibility(View.GONE);
                                shimmerSearchResult.setVisibility(View.GONE);
                                break;
                        }
                    }
                });

                return false;
            }
        });

        RecyclerView rvSuggestions = root.findViewById(R.id.rvSuggestions);


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mViewModel.addMember(mSearchResult);
                    layoutSearchResult.setVisibility(View.GONE);
                } else {
                    mViewModel.removeMember(mSearchResult);
                }
            }
        });


        SuggestionMemberAdapter.OnCheckChangeListener onCheckChangeListener = new SuggestionMemberAdapter.OnCheckChangeListener() {
            @Override
            public void onCheckedChanged(IStudent student, boolean isChecked) {
                if (isChecked) {
                    mViewModel.addMember(student);
                } else {
                    mViewModel.removeMember(student);
                }
            }
        };

        mViewModel.getSuggestions().observe(getViewLifecycleOwner(), new Observer<StateModel<List<IStudent>>>() {
            @Override
            public void onChanged(StateModel<List<IStudent>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        List<IStudent> suggestions = stateModel.getData();
                        suggestionAdapter = new SuggestionMemberAdapter(suggestions, AddMemberFragment.this, onCheckChangeListener);
                        rvSuggestions.setAdapter(suggestionAdapter);

                        break;
                }
            }
        });


        return root;
    }

    private Toolbar initializeToolbar(View root) {
        Toolbar toolbar = null;

        if (mActivity instanceof AppCompatActivity) {
            toolbar = root.findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.title_chat_add_member);
            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }

        return toolbar;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.create_group_chat_toolbar_menu, menu);
        Log.d(getTag(), "onCreateOptionsMenu: ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_next) {
            List<IStudent> students = mViewModel.getSelectedList().getValue();
            assert students != null;
            int counter = students.size();
            if (counter == 1) {
                IStudent selected = students.get(0);

                Bundle bundle = new Bundle();

                String title = selected.getName();
                bundle.putString("title", title);

                String code = selected.getCode();
                bundle.putString("code", code);

                bundle.putString("type", GroupChat.DIRECT);

                mNavController.navigate(R.id.action_navigation_add_member_to_navigation_chat_room, bundle);

            } else if (counter > 1) {
                mNavController.navigate(R.id.action_navigation_add_member_to_navigation_set_room_title);
            } else {
                Toast.makeText(mActivity, "Bạn chưa chọn thành viên", Toast.LENGTH_SHORT).show();
            }

            Log.d(getTag(), "onOptionsItemSelected: ");
        }

        return super.onOptionsItemSelected(item);
    }

    public AddGroupChatViewModel getViewModel() {
        return mViewModel;
    }
}