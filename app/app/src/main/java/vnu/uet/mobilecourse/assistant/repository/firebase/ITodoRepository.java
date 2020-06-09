package vnu.uet.mobilecourse.assistant.repository.firebase;

import java.util.Date;
import java.util.List;
import java.util.Map;

import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;
import vnu.uet.mobilecourse.assistant.model.event.DailyEventList;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;

public interface ITodoRepository {
    /**
     * Retrieve all student's todoLists in FireStore database
     * @return all student's todoLists
     */
    IStateLiveData<List<TodoList>> getAllTodoLists();

    /**
     * Retrieve all student's todos in FireStore database
     * @return all student's todos
     */
    IStateLiveData<List<Todo>> getAllTodos();

    /**
     * Add a new todo_
     */
    IStateLiveData<Todo> addTodo(Todo todo);

    /**
     * Add a new todoList
     */
    IStateLiveData<TodoList> addTodoList(TodoList todoList);

    /**
     * Delete an exist todo_
     * @param id of todo_
     */
    IStateLiveData<String> deleteTodo(String id);

    /**
     * Delete an exist TodoList and its todo_s
     * @param id of todoList
     */
    IStateLiveData<String> deleteTodoList(String id, List<Todo> todos);

    /**
     * Modify an exist todo_
     * @param id of todo_
     * @param changes contains modified data
     */
    IStateLiveData<String> modifyTodo(String id, Map<String, Object> changes);

    /**
     * Modify an exist todoList
     * @param id of todoList
     * @param changes contains modified data (not its todo_s)
     */
    IStateLiveData<String> modifyTodoList(String id, Map<String, Object> changes);
}
