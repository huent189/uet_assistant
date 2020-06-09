package vnu.uet.mobilecourse.assistant.repository.course;

import androidx.lifecycle.LiveData;
import com.google.gson.JsonElement;
import retrofit2.Call;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.MaterialDAO;
import vnu.uet.mobilecourse.assistant.model.material.*;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MaterialRepository {
    private CourseRequest sender = HTTPClient.getInstance().request(CourseRequest.class);
    private MaterialDAO materialDAO = CoursesDatabase.getDatabase().materialDAO();
    public LiveData getDetails(int materialId, String type){
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
            case CourseConstant.MaterialType.RESOURSE:
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
                updateList.addAll(Arrays.stream(response)
                        .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                materialDAO.insertPageContent(Arrays.asList(response));
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
                updateList.addAll(Arrays.stream(response)
                        .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                materialDAO.insertExternalResourceContent(Arrays.asList(response));
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
                updateList.addAll(Arrays.stream(response)
                        .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                materialDAO.insertMaterialContent(Arrays.asList(response));
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
                updateList.addAll(Arrays.stream(response)
                        .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                materialDAO.insertInternalResource(Arrays.asList(response));
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
                updateList.addAll(Arrays.stream(response)
                        .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                materialDAO.insertAssignments(Arrays.asList(response));
            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }
    private List<QuizNoGrade> updateQuizzes() throws IOException {
        Call<JsonElement> call = sender.getQuizzesByCourses(null);
        final ArrayList<QuizNoGrade> updateList = new ArrayList<>();
        CoursesResponseCallback<QuizNoGrade[]> handler
                = new CoursesResponseCallback<QuizNoGrade[]>(QuizNoGrade[].class, "quizzes") {
            @Override
            public void onSuccess(QuizNoGrade[] response) {
                long lastTime = materialDAO.getLastUpdateTime(QuizNoGrade.class);
                updateList.addAll(Arrays.stream(response)
                        .filter(p -> p.getTimeModified() > lastTime).collect(Collectors.toList()));
                materialDAO.insertQuizzes(Arrays.asList(response));
            }
        };
        handler.onResponse(call, call.execute());
        return updateList;
    }
}
