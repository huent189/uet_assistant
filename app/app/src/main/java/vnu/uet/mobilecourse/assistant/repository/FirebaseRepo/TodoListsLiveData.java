package vnu.uet.mobilecourse.assistant.repository.FirebaseRepo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoListDocument;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class TodoListsLiveData extends StateLiveData<List<TodoListDocument>> {

    private List<TodoListDocument> shallowTodoLists;

    private List<TodoDocument> todos;

    private boolean listSuccess;

    private boolean todoSuccess;

    public TodoListsLiveData(@NonNull StateLiveData<List<TodoListDocument>> shallowTodoLists,
                             @NonNull StateLiveData<List<TodoDocument>> todos) {
        super(new StateModel<>(StateStatus.LOADING));

        this.shallowTodoLists = new ArrayList<>();
        StateModel<List<TodoListDocument>> listStateModel = shallowTodoLists.getValue();
        if (listStateModel != null) {
            if (listStateModel.getStatus() == StateStatus.SUCCESS) {
                listSuccess = true;
                updateShallowTodoLists(listStateModel.getData());
            }
        }

        this.todos = new ArrayList<>();
        StateModel<List<TodoDocument>> todoStateModel = todos.getValue();
        if (todoStateModel != null) {
            if (todoStateModel.getStatus() == StateStatus.SUCCESS) {
                todoSuccess = true;
                updateTodos(todoStateModel.getData());
            }
        }

        if (listSuccess && todoSuccess) {
            List<TodoListDocument> value = combineData();
            postSuccess(value);
        }

        MediatorLiveData<List<TodoListDocument>> mergedLiveData = new MediatorLiveData<>();

        mergedLiveData.addSource(shallowTodoLists, new Observer<StateModel<List<TodoListDocument>>>() {
            @Override
            public void onChanged(StateModel<List<TodoListDocument>> stateModel) {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        listSuccess = false;
                        postLoading();
                        break;

                    case ERROR:
                        listSuccess = false;
                        postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        listSuccess = true;

                        List<TodoListDocument> todoLists = stateModel.getData();
                        updateShallowTodoLists(todoLists);

                        if (listSuccess && todoSuccess) {
                            List<TodoListDocument> value = combineData();
                            postSuccess(value);
                        }

                        break;
                }
            }
        });

        mergedLiveData.addSource(todos, new Observer<StateModel<List<TodoDocument>>>() {
            @Override
            public void onChanged(StateModel<List<TodoDocument>> stateModel) {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        todoSuccess = false;
                        postLoading();
                        break;

                    case ERROR:
                        todoSuccess = false;
                        postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        todoSuccess = true;

                        List<TodoDocument> todos = stateModel.getData();
                        updateTodos(todos);

                        if (todoSuccess && listSuccess) {
                            List<TodoListDocument> value = combineData();
                            postSuccess(value);
                        }

                        break;
                }
            }
        });
    }

    private void updateShallowTodoLists(List<TodoListDocument> newList) {
        if (newList == null)
            return;

        shallowTodoLists.clear();
        shallowTodoLists.addAll(newList);
    }

    private void updateTodos(List<TodoDocument> newList) {
        if (newList == null)
            return;

        todos.clear();
        todos.addAll(newList);
    }

    private List<TodoListDocument> combineData() {
        List<TodoListDocument> result = new ArrayList<>(shallowTodoLists);

        todos.forEach(todo -> {
            String todoListId = todo.getTodoListId();

            result.forEach(todoList -> {
                if (todoList.getTodoListId().equals(todoListId)) {
                    todoList.add(todo);
                }
            });
        });

        return result;
    }
}
