package vnu.uet.mobilecourse.assistant.repository;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import vnu.uet.mobilecourse.assistant.model.todo.AbstractTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.model.todo.Todo;
import vnu.uet.mobilecourse.assistant.model.todo.TodoList;

public interface ITodoRepository {
    /**
     * Use in Calendar Fragment, when touch on a specific date
     * UI will display all todo_s had deadline on selected date
     *
     * @param date selected date on calendar
     * @return list of todo_ on specific date
     */
    LiveData<DailyTodoList> getTodoListByDate(Date date);

    /**
     * Use in Calendar Fragment, when touch on two specific date
     * UI will display all todo_s had deadline between selected dates
     *
     * @param from date lower boundary
     * @param to date higher boundary
     * @return list of todo_ between selected dates
     */
    LiveData<AbstractTodoList> getTodoListByRange(Date from, Date to);

    /**
     * Retrieve all student's todoLists in FireStore database
     * @return all student's todoLists
     */
    LiveData<List<TodoList>> getAllTodoLists();

    /**
     * Add a new todo_
     */
    void addTodo(Todo todo);

    /**
     * Add a new todoList
     */
    void addTodoList(TodoList todoList);

    /**
     * Delete an exist todo_
     * @param id of todo_
     */
    Todo deleteTodo(String id);

    /**
     * Delete an exist TodoList and its todo_s
     * @param id of todoList
     */
    TodoList deleteTodoList(String id);

    /**
     * Modify an exist todo_
     * @param id of todo_
     * @param newTodo contains modified data
     */
    void modifyTodo(String id, Todo newTodo);

    /**
     * Modify an exist todoList
     * @param id of todoList
     * @param newList contains modified data (not its todo_s)
     */
    void modifyTodoList(String id, TodoList newList);
}
