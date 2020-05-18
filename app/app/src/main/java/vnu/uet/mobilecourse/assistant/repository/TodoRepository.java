package vnu.uet.mobilecourse.assistant.repository;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoListDocument;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.repository.FirebaseRepo.TodoListsLiveData;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class TodoRepository implements ITodoRepository {
    private static final String TAG = TodoRepository.class.getSimpleName();

    private static TodoRepository instance;

    private String ownerId;

    private StateLiveData<List<TodoListDocument>> todoListLiveData;

    private StateLiveData<List<TodoDocument>> todoLiveData;

    public TodoRepository() {
        StateModel<List<TodoListDocument>> listLoadingState = new StateModel<>(StateStatus.LOADING);
        todoListLiveData = new StateLiveData<>(listLoadingState);

        StateModel<List<TodoDocument>> todoLoadingState = new StateModel<>(StateStatus.LOADING);
        todoLiveData = new StateLiveData<>(todoLoadingState);

        ownerId = User.getInstance().getUserId();

        listenToTodoLists();
        listenToTodos();
    }

    private void listenToTodos() {
        FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.TODO)
                .whereEqualTo("ownerId", ownerId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen to todo list failed.");
                            todoLiveData.postError(e);

                        } else if (snapshots == null) {
                            Log.d(TAG, "Listening to todo list.");
                            todoLiveData.postLoading();

                        } else {
                            List<TodoDocument> todos = snapshots.getDocuments().stream()
                                    .map(snapshot -> snapshot.toObject(TodoDocument.class))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            todoLiveData.postSuccess(todos);
                        }
                    }
                });
    }

    private void listenToTodoLists() {
        FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.TODO_LIST)
                .whereEqualTo("ownerId", ownerId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e(TAG, "Listen to todo list failed.");
                            todoListLiveData.postError(e);

                        } else if (snapshots == null) {
                            Log.d(TAG, "Listening to todo list.");
                            todoListLiveData.postLoading();

                        } else {
                            List<TodoListDocument> todoLists = snapshots.getDocuments().stream()
                                    .map(snapshot -> snapshot.toObject(TodoListDocument.class))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList());

                            todoListLiveData.postSuccess(todoLists);
                        }
                    }
                });
    }

    public static TodoRepository getInstance() {
        if (instance == null) {
            instance = new TodoRepository();
        }

        return instance;
    }

    @Override
    public StateLiveData<List<TodoDocument>> getAllTodos() {
        return todoLiveData;
    }

    @Override
    public StateLiveData<DailyTodoList> getDailyTodoList(Date date) {
        // initialize state live data with loading state
        StateModel<DailyTodoList> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<DailyTodoList> response = new StateLiveData<>(loadingState);

        MediatorLiveData<DailyTodoList> filterLiveData = new MediatorLiveData<>();

        filterLiveData.addSource(todoLiveData, new Observer<StateModel<List<TodoDocument>>>() {
            @Override
            public void onChanged(StateModel<List<TodoDocument>> stateModel) {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        response.postLoading();
                        break;

                    case ERROR:
                        response.postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        List<TodoDocument> todos = stateModel.getData();

                        DailyTodoList dailyTodoList = new DailyTodoList(date);

                        List<TodoDocument> todoByDay = todos.stream()
                                .filter(todo -> {
                                    Date deadline = new Date(todo.getDeadline() * 1000);
                                    return DateTimeUtils.isSameDate(date, deadline);
                                })
                                .collect(Collectors.toList());

                        dailyTodoList.addAll(todoByDay);

                        response.postSuccess(dailyTodoList);
                }
            }
        });

        return response;
    }

    public StateLiveData<List<TodoListDocument>> getShallowTodoLists() {
        return todoListLiveData;
    }

    @Override
    public StateLiveData<List<TodoListDocument>> getAllTodoLists() {
        return new TodoListsLiveData(todoListLiveData, todoLiveData);
    }

    @Override
    public StateLiveData<TodoDocument> addTodo(TodoDocument todo) {
        StateModel<TodoDocument> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<TodoDocument> response = new StateLiveData<>(loadingState);

        String todoId = todo.getTodoId();

        FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.TODO)
                .document(todoId)
                .set(todo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        response.postSuccess(todo);
                        Log.d(TAG, "Add a new todo list: " + todo.getTitle());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.postError(e);
                        Log.e(TAG, "Failed to add todo list: " + todo.getTitle());
                    }
                });

        return response;
    }

    @Override
    public StateLiveData<TodoListDocument> addTodoList(TodoListDocument todoList) {
        StateModel<TodoListDocument> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<TodoListDocument> response = new StateLiveData<>(loadingState);

        String todoListId = todoList.getTodoListId();

        FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.TODO_LIST)
                .document(todoListId)
                .set(todoList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        response.postSuccess(todoList);
                        Log.d(TAG, "Add a new todo list: " + todoList.getTitle());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.postError(e);
                        Log.e(TAG, "Failed to add todo list: " + todoList.getTitle());
                    }
                });

        return response;
    }

    @Override
    public StateLiveData<TodoDocument> deleteTodo(String id) {
        return null;
    }

    @Override
    public StateLiveData<TodoListDocument> deleteTodoList(String id) {
        return null;
    }

    @Override
    public StateLiveData<TodoDocument> modifyTodo(String id, TodoDocument newTodo) {
        return null;
    }

    @Override
    public StateLiveData<TodoListDocument> modifyTodo(String id, TodoListDocument newList) {
        return null;
    }
}
