package vnu.uet.mobilecourse.assistant.repository.FirebaseRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.Todo;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoList;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class TodoListsLiveData extends StateMediatorLiveData<List<TodoList>> {

    private List<TodoList> shallowTodoLists;

    private List<Todo> todos;

    private boolean listSuccess;

    private boolean todoSuccess;

    public TodoListsLiveData(@NonNull StateLiveData<List<TodoList>> shallowTodoLists,
                             @NonNull StateLiveData<List<Todo>> todos) {
        super(new StateModel<>(StateStatus.LOADING));

        this.shallowTodoLists = new ArrayList<>();
//        StateModel<List<TodoListDocument>> listStateModel = shallowTodoLists.getValue();
//        if (listStateModel != null) {
//            if (listStateModel.getStatus() == StateStatus.SUCCESS) {
//                listSuccess = true;
//                updateShallowTodoLists(listStateModel.getData());
//            }
//        }

        this.todos = new ArrayList<>();
//        StateModel<List<TodoDocument>> todoStateModel = todos.getValue();
//        if (todoStateModel != null) {
//            if (todoStateModel.getStatus() == StateStatus.SUCCESS) {
//                todoSuccess = true;
//                updateTodos(todoStateModel.getData());
//            }
//        }
//
//        if (listSuccess && todoSuccess) {
//            List<TodoListDocument> value = combineData();
//            postSuccess(value);
//        }
//
//        MediatorLiveData<List<TodoListDocument>> mergedLiveData = new MediatorLiveData<>();

        addSource(shallowTodoLists, new Observer<StateModel<List<TodoList>>>() {
            @Override
            public void onChanged(StateModel<List<TodoList>> stateModel) {
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

                        List<TodoList> todoLists = stateModel.getData();
                        updateShallowTodoLists(todoLists);

                        if (listSuccess && todoSuccess) {
                            List<TodoList> value = combineData();
                            postSuccess(value);
                        }

                        break;
                }
            }
        });

        addSource(todos, new Observer<StateModel<List<Todo>>>() {
            @Override
            public void onChanged(StateModel<List<Todo>> stateModel) {
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

                        List<Todo> todos = stateModel.getData();
                        updateTodos(todos);

                        if (todoSuccess && listSuccess) {
                            List<TodoList> value = combineData();
                            postSuccess(value);
                        }

                        break;
                }
            }
        });
    }

    public void updateShallowTodoLists(List<TodoList> newList) {
        if (newList == null)
            return;

        shallowTodoLists.clear();
        shallowTodoLists.addAll(newList);
    }

    public void updateTodos(List<Todo> newList) {
        if (newList == null)
            return;

        todos.clear();
        todos.addAll(newList);
    }

    public List<TodoList> combineData() {
        List<TodoList> result = shallowTodoLists.stream()
                .map(TodoList::clone)
                .collect(Collectors.toList());

        todos.forEach(todo -> {
            String todoListId = todo.getTodoListId();

            result.forEach(todoList -> {
                if (todoList.getId().equals(todoListId)) {
                    if (!todoList.contains(todo))
                        todoList.add(todo);
                }
            });
        });

        return result;
    }
}
