package vnu.uet.mobilecourse.assistant.repository.course;

import android.util.Log;
import androidx.lifecycle.LiveData;
import com.google.gson.JsonElement;
import retrofit2.Call;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.MaterialDAO;
import vnu.uet.mobilecourse.assistant.database.querymodel.Submission;
import vnu.uet.mobilecourse.assistant.model.event.CourseSubmissionEvent;
import vnu.uet.mobilecourse.assistant.model.material.*;
import vnu.uet.mobilecourse.assistant.network.CourseClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MaterialRepository {
    private static final int DAY_DURATION_SECOND = 24 * 60 *60;

    private static MaterialRepository instance;

    private CourseRequest sender;
    private MaterialDAO materialDAO;

    private MaterialRepository() {
        sender = CourseClient.getInstance().request(CourseRequest.class);
        materialDAO = CoursesDatabase.getDatabase().materialDAO();
    }

    public static MaterialRepository getInstance() {
        if (instance == null) {
            instance = new MaterialRepository();
        }

        return instance;
    }

    public LiveData<? extends MaterialContent> getDetails(int materialId, String type){
        switch (type){
            case CourseConstant.MaterialType.ASSIGN:
                new Thread(() -> {
                    try {
                        updateAssignments();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                return materialDAO.getAssignment(materialId);
            case CourseConstant.MaterialType.PAGE:
                new Thread(() -> {
                    try {
                        updatePageContents();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                return materialDAO.getPageContent(materialId);
            case CourseConstant.MaterialType.QUIZ:
                new Thread(() -> {
                    try {
                        updateQuizzes();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                return materialDAO.getQuiz(materialId);
            case CourseConstant.MaterialType.RESOURCE:
                new Thread(() -> {
                    try {
                        updateInternalResources();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                return materialDAO.getInternalResource(materialId);
            case CourseConstant.MaterialType.URL:
                new Thread(() -> {
                    try {
                        updateExternalResources();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
                return materialDAO.getExternalResource(materialId);
            case CourseConstant.MaterialType.LABEL:
                new Thread(() -> {
                    try {
                        updateLabels();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            default:
                return materialDAO.getMaterialContent(materialId);
        }
    }
    public List<MaterialContent> selectiveUpdate(Set<String> types) throws IOException {
        ArrayList<MaterialContent> updateList = new ArrayList<>();
        if(types.contains(CourseConstant.MaterialType.ASSIGN)){
            updateList.addAll(updateAssignments());
        }
        if(types.contains(CourseConstant.MaterialType.URL)){
            updateList.addAll(updateExternalResources());
        }
        if(types.contains(CourseConstant.MaterialType.RESOURCE)){
            updateList.addAll(updateInternalResources());
        }
        if(types.contains(CourseConstant.MaterialType.LABEL)){
            updateList.addAll(updateLabels());
        }
        if(types.contains(CourseConstant.MaterialType.PAGE)){
            updateList.addAll(updatePageContents());
        }
        if(types.contains(CourseConstant.MaterialType.QUIZ)){
            updateList.addAll(updateQuizzes());
        }
        return updateList;
    }
    public List<MaterialContent> updateAll() throws IOException {
        ArrayList<MaterialContent> updateList = new ArrayList<>();
        updateList.addAll(updateAssignments());
        updateList.addAll(updateExternalResources());
        updateList.addAll(updateInternalResources());
        updateList.addAll(updateLabels());
        updateList.addAll(updatePageContents());
        updateList.addAll(updateQuizzes());
        return updateList;
    }

    private List<PageContent> updatePageContents() throws IOException {
        Call<JsonElement> call = sender.getPagesByCourses(null);
        final ArrayList<PageContent> updateList = new ArrayList<>();
        CoursesResponseCallback handler = new CoursesResponseCallback<PageContent[]>(PageContent[].class, "pages") {
            @Override
            public void onSuccess(PageContent[] response) {
                long lastTime = materialDAO.getLastUpdateTime(PageContent.class);
                Log.d("REPO_UPDATE", "onSuccess: " + lastTime);
                if(lastTime == -1){
                    materialDAO.insertPageContent(Arrays.asList(response));
                } else {
                    updateList.addAll(Arrays.stream(response)
                            .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                    materialDAO.insertPageContent(updateList);
                }
            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }

    private List<ExternalResourceContent> updateExternalResources() throws IOException {
        Call<JsonElement> call = sender.getURLsByCourses(null);
        final ArrayList<ExternalResourceContent> updateList = new ArrayList<>();
        CoursesResponseCallback<ExternalResourceContent[]> handler
                = new CoursesResponseCallback<ExternalResourceContent[]>(ExternalResourceContent[].class, "urls") {
            @Override
            public void onSuccess(ExternalResourceContent[] response) {
                long lastTime = materialDAO.getLastUpdateTime(ExternalResourceContent.class);
                Log.d("REPO_UPDATE", "onSuccess: " + lastTime);
                if(lastTime == -1){
                    materialDAO.insertExternalResourceContent(Arrays.asList(response));
                }
                else {
                    updateList.addAll(Arrays.stream(response)
                            .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                    materialDAO.insertExternalResourceContent(updateList);
                }
            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }

    private List<MaterialContent> updateLabels() throws IOException {
        Call<JsonElement> call = sender.getLabelByCourses(null);
        final ArrayList<MaterialContent> updateList = new ArrayList<>();
        CoursesResponseCallback<MaterialContent[]> handler
                = new CoursesResponseCallback<MaterialContent[]>(MaterialContent[].class, "labels") {
            @Override
            public void onSuccess(MaterialContent[] response) {
                long lastTime = materialDAO.getLastUpdateTime(MaterialContent.class);
                Log.d("REPO_UPDATE", "onSuccess: " + lastTime);
                if(lastTime == -1){
                    materialDAO.insertMaterialContent(Arrays.asList(response));
                } else {
                    updateList.addAll(Arrays.stream(response)
                            .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                    materialDAO.insertMaterialContent(updateList);
                }

            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }

    private List<InternalResourceContent> updateInternalResources() throws IOException {
        Call<JsonElement> call = sender.getInternalResourceByCourses(null);
        final ArrayList<InternalResourceContent> updateList = new ArrayList<>();
        CoursesResponseCallback<InternalResourceContent[]> handler
                = new CoursesResponseCallback<InternalResourceContent[]>(InternalResourceContent[].class, "resources") {
            @Override
            public void onSuccess(InternalResourceContent[] response) {
                long lastTime = materialDAO.getLastUpdateTime(InternalResourceContent.class);
                Log.d("REPO_UPDATE", "onSuccess: " + lastTime);
                if(lastTime == -1){
                    materialDAO.insertInternalResource(Arrays.asList(response));
                }
                else {
                    updateList.addAll(Arrays.stream(response)
                            .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                    materialDAO.insertInternalResource(updateList);
                }

            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }
    private List<AssignmentContent> updateAssignments() throws IOException {
        Call<JsonElement> call = sender.getAssignmentByCourses(null);
        final ArrayList<AssignmentContent> updateList = new ArrayList<>();
        CoursesResponseCallback<AssignmentContent[]> handler
                = new CoursesResponseCallback<AssignmentContent[]>(AssignmentContent[].class) {
            @Override
            public void onSuccess(AssignmentContent[] response) {
                long lastTime = materialDAO.getLastUpdateTime(AssignmentContent.class);
                Log.d("REPO_UPDATE", "onSuccess: " + lastTime);
                if(lastTime == -1){
                    materialDAO.insertAssignments(Arrays.asList(response));
                }else {
                    updateList.addAll(Arrays.stream(response)
                            .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                    materialDAO.insertAssignments(updateList);
                }

            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }
    private List<QuizNoGrade> updateQuizzes() throws IOException {
        Call<JsonElement> call = sender.getQuizzesByCourses(null);
        final ArrayList<QuizNoGrade> updateList = new ArrayList<>();
        final List<Integer> existId = materialDAO.getAllQuizId();
        CoursesResponseCallback<QuizNoGrade[]> handler
                = new CoursesResponseCallback<QuizNoGrade[]>(QuizNoGrade[].class, "quizzes") {
            @Override
            public void onSuccess(QuizNoGrade[] response) {
                List<QuizNoGrade> tmp = Arrays.stream(response).filter(quiz -> !existId.contains(quiz.getId()))
                        .collect(Collectors.toList());
                if(tmp.size() != response.length){
                    updateList.addAll(tmp);
                }else {
                    updateList.addAll(tmp.stream().filter(q -> q.getTimeOpen() >= System.currentTimeMillis() / 1000).collect(Collectors.toList()));
                }
                materialDAO.insertQuizzes(tmp);
            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }

    public StateLiveData<List<CourseSubmissionEvent>> getDailyCourseSubmissionEvent(Date date){
        StateLiveData<List<CourseSubmissionEvent>> eventsLiveData = new StateLiveData<>();
        eventsLiveData.postLoading();
        new Thread(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DATE);
            calendar.clear();
            calendar.set(year, month, day);
            long startTime = calendar.getTimeInMillis() / 1000;
            long endTime = calendar.getTimeInMillis() / 1000 + DAY_DURATION_SECOND;
            List<Submission> materials = queryCourseSubmission(startTime, endTime);
            ArrayList<CourseSubmissionEvent> events = new ArrayList<>();
            materials.forEach(materialWithCourse -> {
                if(materialWithCourse.getStartTime() >= startTime){
                    events.add(materialWithCourse.toStartEvent());
                }
                if(materialWithCourse.getEndTime() < endTime){
                    events.add(materialWithCourse.toEndEvent());
                }
            });
            eventsLiveData.postSuccess(events);
        }).start();
        return eventsLiveData;
    }
    private List<Submission> queryCourseSubmission(long startTime, long endTime){
        List<Submission> assignments = materialDAO.getAssignmentInRange(startTime, endTime);
        List<Submission> quizzes = materialDAO.getQuizInRange(startTime, endTime);
        ArrayList<Submission> merge = new ArrayList<Submission>();
        merge.addAll(assignments);
        merge.addAll(quizzes);
        return merge;
    }

    public List<CourseSubmissionEvent> getSubmissionEventFromNow(List<Integer> quizIds, List<Integer> assignIds){
        List<Submission> submissions = new ArrayList<>();
        if(!quizIds.isEmpty()){
            submissions.addAll(materialDAO.getQuizSubmissions(quizIds));
        }
        if(!assignIds.isEmpty()){
            submissions.addAll(materialDAO.getAssignmentSubmissions(assignIds));
        }
        long now = System.currentTimeMillis() / 1000 - 3339663;
        return submissions.parallelStream().map(submission -> {
            ArrayList<CourseSubmissionEvent> events = new ArrayList<>();
            if(submission.getStartTime() >= now){
                events.add(submission.toStartEvent());
            }
            if(submission.getEndTime() >= now){
                events.add(submission.toEndEvent());
            }
            return events;
        }).flatMap(List::stream).collect(Collectors.toList());
    }
}
