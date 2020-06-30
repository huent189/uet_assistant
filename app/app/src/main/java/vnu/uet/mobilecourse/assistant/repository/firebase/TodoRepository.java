package vnu.uet.mobilecourse.assistant.repository.firebase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.database.DAO.TodoDAO;
import vnu.uet.mobilecourse.assistant.database.DAO.TodoListDAO;
import vnu.uet.mobilecourse.assistant.exception.OverrideDocumentException;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateMediatorLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class TodoRepository implements ITodoRepository {

    private static TodoRepository instance;

    private StateLiveData<List<TodoList>> mListLiveData;
    private StateLiveData<List<Todo>> mTodoLiveData;

    private TodoDAO mTodoDao;
    private TodoListDAO mListDao;

    private TodoRepository() {
        mTodoDao = new TodoDAO();
        mListDao = new TodoListDAO();

        mTodoLiveData = mTodoDao.readAll();
        mListLiveData = mListDao.readAll();
    }

    public static TodoRepository getInstance() {
        if (instance == null) {
            instance = new TodoRepository();
        }

        return instance;
    }

    @Override
    public StateLiveData<List<Todo>> getAllTodos() {
        return mTodoLiveData;
    }

    public IStateLiveData<List<TodoList>> getShallowTodoLists() {
        return mListLiveData;
    }

    @Override
    public IStateLiveData<List<TodoList>> getAllTodoLists() {
        return new DeepTodoListsStateLiveData(mListLiveData, mTodoLiveData);
    }

    public IStateLiveData<Todo> getTodoById(String todoId) {
        return mTodoDao.read(todoId);
    }

    @Override
    public IStateLiveData<Todo> addTodo(Todo todo) {

        return new ValidationTodoLiveData(todo) {
            @Override
            protected StateLiveData<Todo> onAccess() {
                return mTodoDao.add(todo.getId(), todo);
            }
        };
    }

    @Override
    public IStateLiveData<TodoList> addTodoList(TodoList todoList) {
        return mListDao.add(todoList.getId(), todoList);
    }

    @Override
    public IStateLiveData<String> deleteTodo(String id) {
        return mTodoDao.delete(id);
    }

    @Override
    public IStateLiveData<String> deleteTodoList(String id, List<Todo> todos) {
        String[] todoIds = todos.stream().map(Todo::getId).toArray(String[]::new);

        return mListDao.deleteDeep(id, todoIds);
    }

    @Override
    public IStateLiveData<String> modifyTodo(String id, Map<String, Object> changes) {
        return mTodoDao.update(id, changes);
    }

    @Override
    public IStateLiveData<String> modifyTodoList(String id, Map<String, Object> changes) {
        return mListDao.update(id, changes);
    }

    public abstract class ValidationTodoLiveData extends StateMediatorLiveData<Todo> {

        ValidationTodoLiveData(Todo todo) {
            postLoading();

            addSource(mTodoLiveData, new Observer<StateModel<List<Todo>>>() {
                @Override
                public void onChanged(StateModel<List<Todo>> stateModel) {
                    switch (stateModel.getStatus()) {
                        case ERROR:
                            postError(stateModel.getError());
                            break;

                        case LOADING:
                            postLoading();
                            break;

                        case SUCCESS:
                            removeSource(mTodoLiveData);

                            String title = todo.getTitle();
                            String todoListId = todo.getTodoListId();
                            String todoId = todo.getId();

                            boolean isExist = stateModel.getData()
                                    .stream()
                                    .filter(item -> item.getTodoListId().equals(todoListId)
                                            && !item.getId().equals(todoId))
                                    .anyMatch(item -> item.getTitle().equals(title));

                            if (!isExist) {
                                StateLiveData<Todo> addLiveData = onAccess();

                                addSource(addLiveData, new Observer<StateModel<Todo>>() {
                                    @Override
                                    public void onChanged(StateModel<Todo> stateModel) {
                                        switch (stateModel.getStatus()) {
                                            case LOADING:
                                                postLoading();
                                                break;

                                            case ERROR:
                                                postError(stateModel.getError());
                                                break;

                                            case SUCCESS:
                                                postSuccess(stateModel.getData());
                                                break;
                                        }
                                    }
                                });
                            } else {
                                postError(new OverrideDocumentException("Đã tồn tại công việc " + title + " trong danh sách"));
                            }

                    }
                }
            });
        }

        protected abstract StateLiveData<Todo> onAccess();
    }

    static class DeepTodoListsStateLiveData extends StateMediatorLiveData<List<TodoList>> {

        private List<TodoList> mShallowLists;
        private List<Todo> mItems;

        private boolean mListSuccess;
        private boolean mItemSuccess;

        DeepTodoListsStateLiveData(@NonNull StateLiveData<List<TodoList>> shallowTodoLists,
                                          @NonNull StateLiveData<List<Todo>> todos) {
//            super(new StateModel<>(StateStatus.LOADING));

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
                        todo.setCategory(todoList.getTitle());
                        todoList.add(todo);
                    }
                });
            });

            return result;
        }
    }
}
