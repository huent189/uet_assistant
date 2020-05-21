package vnu.uet.mobilecourse.assistant.viewmodel.state;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.model.todo.PriorityTodoList;

public class DeepTodoListsStateLiveData extends StateMediatorLiveData<List<TodoList>> {

    private List<TodoList> mShallowLists;
    private List<Todo> mItems;

    private boolean mListSuccess;
    private boolean mItemSuccess;

    public DeepTodoListsStateLiveData(@NonNull StateLiveData<List<TodoList>> shallowTodoLists,
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
                .map(TodoList::clone)
                .collect(Collectors.toList());

        mItems.forEach(todo -> {
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
