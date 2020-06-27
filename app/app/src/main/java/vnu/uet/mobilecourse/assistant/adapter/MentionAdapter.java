package vnu.uet.mobilecourse.assistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.Member_GroupChatSubCol;
import vnu.uet.mobilecourse.assistant.util.StringUtils;

public class MentionAdapter extends ArrayAdapter<Member_GroupChatSubCol> {

    private List<Member_GroupChatSubCol> mMembersFull;
    private List<Member_GroupChatSubCol> mMembers;
    private MemberFilter mFilter;

    public MentionAdapter(@NonNull Context context, @NonNull List<Member_GroupChatSubCol> members) {
        super(context, 0, members);
        mMembersFull = members;
        mMembers = new ArrayList<>(members);
        mFilter = new MemberFilter();
    }

    @Override
    public int getCount() {
        return mMembers.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.layout_member_mention_item, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvId = convertView.findViewById(R.id.tvId);

        Member_GroupChatSubCol member = mMembers.get(position);
        if (member != null) {
            String simpleName = StringUtils.getLastSegment(member.getName(), 2);
            tvName.setText(simpleName);
            tvId.setText(member.getCode());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public class MemberFilter extends MyFilter<Member_GroupChatSubCol> {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Member_GroupChatSubCol> filteredList;

            if (constraint == null || constraint.length() == 0) {
                filteredList = new ArrayList<>(mMembersFull);
            } else {
                final String filterPattern = constraint.toString().trim();

                filteredList = mMembersFull.stream()
                        .filter(i -> i.getCode().contains(filterPattern)
                                || i.getName().contains(filterPattern))
                        .collect(Collectors.toList());
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mMembers = getListFromResults(results);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Member_GroupChatSubCol member = (Member_GroupChatSubCol) resultValue;
            return member.getCode();
        }
    }
}
