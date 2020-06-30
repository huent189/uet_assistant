package vnu.uet.mobilecourse.assistant.view.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.adapter.ExternalResourceAdapter;
import vnu.uet.mobilecourse.assistant.adapter.InternalResourceAdapter;
import vnu.uet.mobilecourse.assistant.model.material.AssignmentContent;
import vnu.uet.mobilecourse.assistant.model.material.CourseConstant;
import vnu.uet.mobilecourse.assistant.model.material.ExternalResourceContent;
import vnu.uet.mobilecourse.assistant.model.material.InternalFile;
import vnu.uet.mobilecourse.assistant.model.material.InternalResourceContent;
import vnu.uet.mobilecourse.assistant.model.material.MaterialContent;
import vnu.uet.mobilecourse.assistant.model.material.QuizNoGrade;
import vnu.uet.mobilecourse.assistant.repository.course.CourseActionRepository;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.StringUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.MaterialViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Material;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MaterialFragment extends Fragment {

    private MaterialViewModel mViewModel;
    private FragmentActivity mActivity;

    private TextView mTvStartTime, mTvStartTimeTitle;
    private TextView mTvDeadline, mTvDeadlineTitle;
    private TextView mTvLimitTime, mTvLimitTimeTitle;
    private LinearLayout mLayoutGradeAndAttempt;
    private TextView mTvAttachmentTitle;
    private ShimmerFrameLayout mSflAttachments;
    private TextView mTvModifyTime, mTvModifyTimeTitle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_material, container, false);

        mViewModel = new ViewModelProvider(this).get(MaterialViewModel.class);

        mActivity = getActivity();

        Toolbar toolbar = initializeToolbar(root);

        TextView tvHtml = root.findViewById(R.id.tvHtml);

        TextView tvStatus = root.findViewById(R.id.tvStatus);

        mTvModifyTime = root.findViewById(R.id.tvModifyTime);
        mTvModifyTimeTitle = root.findViewById(R.id.tvModifyTimeTitle);

        mTvStartTime = root.findViewById(R.id.tvStartTime);
        mTvStartTimeTitle = root.findViewById(R.id.tvStartTimeTitle);

        mTvDeadline = root.findViewById(R.id.tvDeadline);
        mTvDeadlineTitle = root.findViewById(R.id.tvDeadlineTitle);

        mTvLimitTime = root.findViewById(R.id.tvLimitTime);
        mTvLimitTimeTitle = root.findViewById(R.id.tvLimitTimeTitle);

        mLayoutGradeAndAttempt = root.findViewById(R.id.layoutGradeAndAttempt);

        CheckBox cbDone = root.findViewById(R.id.cbDone);

        TextView tvMaxGrade = root.findViewById(R.id.tvMaxGrade);
        TextView tvMaxAttempt = root.findViewById(R.id.tvMaxAttempt);

        RecyclerView rvAttachments = root.findViewById(R.id.rvAttachments);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        rvAttachments.setLayoutManager(layoutManager);

        mSflAttachments = root.findViewById(R.id.sflAttachments);
        mSflAttachments.startShimmerAnimation();

        mTvAttachmentTitle = root.findViewById(R.id.tvAttachmentTitle);

        Bundle args = getArguments();
        if (args != null && toolbar != null) {
            // get material from bundle arguments
            Material material = args.getParcelable("material");

            if (material != null) {
                String title = material.getTitle();

                int materialId = material.getId();
                String type = material.getType();
                preprocessor(type);

                String desc = material.getDescription();
                if (desc != null) {
                    setDescriptionView(tvHtml, desc);
                }

                mViewModel.getDetailContent(materialId, type).observe(getViewLifecycleOwner(), new Observer<MaterialContent>() {
                    @Override
                    public void onChanged(MaterialContent content) {
                        if (content != null) {
                            Date modifyTime = DateTimeUtils.fromSecond(content.getTimeModified());
                            mTvModifyTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(modifyTime));

                            String intro = content.getIntro();
                            if (intro != null) {
                                setDescriptionView(tvHtml, intro);
                            }

                            if (content instanceof AssignmentContent) {
                                AssignmentContent assignment = (AssignmentContent) content;

                                Date startTime = DateTimeUtils.fromSecond(assignment.getStartDate());
                                mTvStartTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(startTime));

                                Date deadline = DateTimeUtils.fromSecond(assignment.getDeadline());
                                mTvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));

                                String maxGrade = String.valueOf(assignment.getMaximumGrade());
                                tvMaxGrade.setText(maxGrade);

                                int maxAttempt = assignment.getMaxAttemptAllowed();
                                if (maxAttempt <= 0) {
                                    tvMaxAttempt.setText(R.string.material_assignment_no_attempt_title);
                                } else {
                                    tvMaxAttempt.setText(String.valueOf(maxAttempt));
                                }

                            } else if (content instanceof QuizNoGrade) {
                                QuizNoGrade quiz = (QuizNoGrade) content;

                                Date startTime = DateTimeUtils.fromSecond(quiz.getTimeOpen());
                                mTvStartTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(startTime));

                                Date deadline = DateTimeUtils.fromSecond(quiz.getTimeClose());
                                mTvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));

                                String limitTime = String.format(Locale.ROOT, "%d phút", quiz.getTimeLimit() / 60);
                                mTvLimitTime.setText(limitTime);

                                String maxGrade = String.valueOf(quiz.getMaximumGrade());
                                tvMaxGrade.setText(maxGrade);

                                int maxAttempt = quiz.getMaximumAttempt();
                                if (maxAttempt <= 0) {
                                    tvMaxAttempt.setText(R.string.material_assignment_no_attempt_title);
                                } else {
                                    tvMaxAttempt.setText(String.valueOf(maxAttempt));
                                }

                            } else if (content instanceof InternalResourceContent) {
                                InternalResourceContent internalResource = (InternalResourceContent) content;

                                List<InternalFile> files = internalResource.getFiles();

                                if (files != null) {
                                    InternalResourceAdapter adapter = new InternalResourceAdapter(files, mActivity);
                                    rvAttachments.setAdapter(adapter);

                                    rvAttachments.setVisibility(View.VISIBLE);
                                    mSflAttachments.setVisibility(View.GONE);
                                }

                            } else if (content instanceof ExternalResourceContent) {
                                ExternalResourceAdapter adapter = new ExternalResourceAdapter(material, mActivity);
                                rvAttachments.setAdapter(adapter);

                                rvAttachments.setVisibility(View.VISIBLE);
                                mSflAttachments.setVisibility(View.GONE);
                            }
                        }
                    }
                });

                // setup toolbar title
                toolbar.setTitle(title);

                // change status text view if task hasn't complete
                if (material.getCompletion() != 1) {
                    tvStatus.setText(R.string.material_status_uncomplete);
                    cbDone.setChecked(false);
                } else {
                    cbDone.setChecked(true);
                }

                cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CourseActionRepository repository = new CourseActionRepository();
                        if (isChecked) {
                            repository.triggerMaterialCompletion(material);
                        } else {
                            repository.triggerMaterialUnCompletion(material);
                        }
                    }
                });
            }
        }

        return root;
    }

    private void setDescriptionView(TextView tvDesc, @NonNull String rawContent) {
        SpannableStringBuilder strBuilder = StringUtils.convertHtml(rawContent);

        if (strBuilder.length() > 0) {
            tvDesc.setText(strBuilder);
            tvDesc.setLinksClickable(true);
            tvDesc.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void preprocessor(String materialType) {
        switch (materialType) {
            case CourseConstant.MaterialType.ASSIGN:
                mTvStartTimeTitle.setVisibility(View.VISIBLE);
                mTvStartTime.setVisibility(View.VISIBLE);

                mTvDeadlineTitle.setVisibility(View.VISIBLE);
                mTvDeadline.setVisibility(View.VISIBLE);

                mLayoutGradeAndAttempt.setVisibility(View.VISIBLE);
                break;

            case CourseConstant.MaterialType.QUIZ:
                mTvStartTimeTitle.setVisibility(View.VISIBLE);
                mTvStartTime.setVisibility(View.VISIBLE);

                mTvDeadlineTitle.setVisibility(View.VISIBLE);
                mTvDeadline.setVisibility(View.VISIBLE);

                mTvLimitTime.setVisibility(View.VISIBLE);
                mTvLimitTimeTitle.setVisibility(View.VISIBLE);

                mLayoutGradeAndAttempt.setVisibility(View.VISIBLE);
                break;

            case CourseConstant.MaterialType.RESOURCE:
            case CourseConstant.MaterialType.URL:
                mTvAttachmentTitle.setVisibility(View.VISIBLE);
                mSflAttachments.setVisibility(View.VISIBLE);
                break;

            case CourseConstant.MaterialType.QUESTIONNAIRE:
                mTvModifyTime.setVisibility(View.GONE);
                mTvModifyTimeTitle.setVisibility(View.GONE);
        }

    }

    private Toolbar initializeToolbar(View root) {
        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);

            return toolbar;
        }

        return null;
    }
}
