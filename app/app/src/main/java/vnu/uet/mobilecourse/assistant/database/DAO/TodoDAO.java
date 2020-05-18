package vnu.uet.mobilecourse.assistant.database.DAO;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.FirebaseCollectionName;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class TodoDAO {
    private static final String TAG = TodoDAO.class.getSimpleName();

    private static final String OWNER_ID = User.getInstance().getStudentId();

    private StateLiveData<List<TodoDocument>> todos;

    public StateLiveData<List<TodoDocument>> readAll() {
        if (todos == null) {
            // initialize with loading state
            todos = new StateLiveData<>(new StateModel<>(StateStatus.LOADING));

            // listen data from firebase
            FirebaseFirestore.getInstance()
                    // reference to _TODO collection
                    .collection(FirebaseCollectionName.TODO)
                    // query all document owned by current user
                    .whereEqualTo("ownerId", OWNER_ID)
                    // listen for data change
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.e(TAG, "Listen to todo list failed.");
                                todos.postError(e);

                            } else if (snapshots == null) {
                                Log.d(TAG, "Listening to todo list.");
                                todos.postLoading();

                            } else {
                                List<TodoDocument> allTodos = snapshots.getDocuments().stream()
                                        .map(snapshot -> snapshot.toObject(TodoDocument.class))
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());

                                todos.postSuccess(allTodos);
                            }
                        }
                    });
        }

        return todos;
    }

    public IStateLiveData<TodoDocument> read(String id) {
        readAll();

        StateModel<TodoDocument> loadingState = new StateModel<>(StateStatus.LOADING);
        StateMediatorLiveData<TodoDocument> todo = new StateMediatorLiveData<>(loadingState);

        todo.addSource(todos, new Observer<StateModel<List<TodoDocument>>>() {
            @Override
            public void onChanged(StateModel<List<TodoDocument>> state) {
                switch (state.getStatus()) {
                    case LOADING:
                        todo.postLoading();
                        break;

                    case ERROR:
                        todo.postError(state.getError());
                        break;

                    case SUCCESS:
                        TodoDocument doc = state.getData().stream()
                                .filter(todo -> todo.getTodoId().equals(id))
                                .findFirst()
                                .orElse(null);

                        if (doc == null)
                            todo.postLoading();
                        else
                            todo.postSuccess(doc);

                        break;
                }
            }
        });

        return todo;
    }

    public IStateLiveData<TodoDocument> add(TodoDocument todo) {
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

    public IStateLiveData<String> delete(String id) {
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.TODO)
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        response.postSuccess(id);
                        Log.d(TAG, "Add a new todo list: " + id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.postError(e);
                        Log.e(TAG, "Failed to add todo list: " + id);
                    }
                });

        return response;
    }

    public IStateLiveData<String> update(String id, Map<String, Object> changes) {
        StateModel<String> loadingState = new StateModel<>(StateStatus.LOADING);
        StateLiveData<String> response = new StateLiveData<>(loadingState);

        FirebaseFirestore.getInstance()
                .collection(FirebaseCollectionName.TODO)
                .document(id)
                .update(changes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        response.postSuccess(id);
                        Log.d(TAG, "Add a new todo list: " + id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.postError(e);
                        Log.e(TAG, "Failed to add todo list: " + id);
                    }
                });

        return response;
    }
}
