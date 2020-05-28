package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.database.DAO.TodoDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.TodoListDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.TodoComparator;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class TodoRepository implements ITodoRepository {
    private static TodoRepository instance;

    private StateLiveData<List<TodoList>> listLiveData;

    private StateLiveData<List<Todo>> todoLiveData;

    private TodoDAO todoDAO = new TodoDAO();

    private TodoListDAO todoListDAO = new TodoListDAO();

    public TodoRepository() {
        todoLiveData = todoDAO.readAll();
        listLiveData = todoListDAO.readAll();
    }

    public static TodoRepository getInstance() {
        if (instance == null) {
            instance = new TodoRepository();
        }

        return instance;
    }

    @Override
    public StateLiveData<List<Todo>> getAllTodos() {
        return todoLiveData;
    }

    @Override
    public StateMediatorLiveData<DailyTodoList> getDailyTodoList(Date date) {
        // initialize state live data with loading state
        StateModel<DailyTodoList> loadingState = new StateModel<>(StateStatus.LOADING);
        StateMediatorLiveData<DailyTodoList> response = new StateMediatorLiveData<>(loadingState);

        response.addSource(todoLiveData, new Observer<StateModel<List<Todo>>>() {
            @Override
            public void onChanged(StateModel<List<Todo>> stateModel) {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        response.postLoading();
                        break;

                    case ERROR:
                        response.postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        List<Todo> todos = stateModel.getData();

                        DailyTodoList dailyTodoList = new DailyTodoList(date);

                        List<Todo> todoByDay = todos.stream()
                                .filter(todo -> {
                                    Date deadline = DateTimeUtils.fromSecond(todo.getDeadline());
                                    return DateTimeUtils.isSameDate(date, deadline);
                                })
                                .sorted(new TodoComparator())
                                .collect(Collectors.toList());

                        dailyTodoList.addAll(todoByDay);

                        response.postSuccess(dailyTodoList);
                }
            }
        });

        return response;
    }

    public IStateLiveData<List<TodoList>> getShallowTodoLists() {
        return listLiveData;
    }

    @Override
    public IStateLiveData<List<TodoList>> getAllTodoLists() {
        return new DeepTodoListsStateLiveData(listLiveData, todoLiveData);
    }

    @Override
    public IStateLiveData<Todo> addTodo(Todo todo) {
        return todoDAO.add(todo.getId(), todo);
    }

    @Override
    public IStateLiveData<TodoList> addTodoList(TodoList todoList) {
        return todoListDAO.add(todoList.getId(), todoList);
    }

    @Override
    public IStateLiveData<String> deleteTodo(String id) {
        return todoDAO.delete(id);
    }

    @Override
    public IStateLiveData<String> deleteTodoList(String id) {
        return todoListDAO.delete(id);
    }

    @Override
    public IStateLiveData<String> modifyTodo(String id, Map<String, Object> changes) {
        return todoDAO.update(id, changes);
    }

    @Override
    public IStateLiveData<String> modifyTodoList(String id, Map<String, Object> changes) {
        return todoListDAO.update(id, changes);
    }

    static class DeepTodoListsStateLiveData extends StateMediatorLiveData<List<TodoList>> {

        private List<TodoList> mShallowLists;
        private List<Todo> mItems;

        private boolean mListSuccess;
        private boolean mItemSuccess;

        DeepTodoListsStateLiveData(@NonNull StateLiveData<List<TodoList>> shallowTodoLists,
                                          @NonNull StateLiveData<List<Todo>> todos) {
            super(new StateModel<>(StateStatus.LOADING));

            this.mShallowLists = new ArrayList<>();
            this.mItems = new ArrayList<>();

            addSource(shallowTodoLists, stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        mListSuccess = false;
                        postLoading();
                        break;

                    case ERROR:
                        mListSuccess = false;
                        postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        mListSuccess = true;

                        List<TodoList> todoLists = stateModel.getData();
                        updateShallowTodoLists(todoLists);

                        if (mListSuccess && mItemSuccess) {
                            List<TodoList> value = combineData();
                            postSuccess(value);
                        }

                        break;
                }
            });

            addSource(todos, stateModel -> {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        mItemSuccess = false;
                        postLoading();
                        break;

                    case ERROR:
                        mItemSuccess = false;
                        postError(stateModel.getError());
                        break;

                    case SUCCESS:
                        mItemSuccess = true;

                        List<Todo> items = stateModel.getData();
                        updateTodos(items);

                        if (mItemSuccess && mListSuccess) {
                            List<TodoList> value = combineData();
                            postSuccess(value);
                        }

                        break;
                }
            });
        }

        private void updateShallowTodoLists(List<TodoList> newList) {
            if (newList == null)
                return;

            mShallowLists.clear();
            mShallowLists.addAll(newList);
        }

        private void updateTodos(List<Todo> newList) {
            if (newList == null)
                return;

            mItems.clear();
            mItems.addAll(newList);
        }

        private List<TodoList> combineData() {
            List<TodoList> result = mShallowLists.stream()
                    .map(list -> {
                        TodoList output = list.clone();
                        output.clear();
                        return output;
                    })
                    .collect(Collectors.toList());

            mItems.forEach(todo -> {
                String todoListId = todo.getTodoListId();

                result.forEach(todoList -> {
                    if (todoList.getId().equals(todoListId)) {
                        todoList.add(todo);
                    }
                });
            });

            return result;
        }
    }
}
