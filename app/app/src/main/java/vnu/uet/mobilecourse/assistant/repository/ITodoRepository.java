package vnu.uet.mobilecourse.assistant.repository;

import java.util.Date;
import java.util.List;

import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoListDocument;
import vnu.uet.mobilecourse.assistant.model.todo.DailyTodoList;
import vnu.uet.mobilecourse.assistant.repository.FirebaseRepo.TodoListsLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateLiveData;

public interface ITodoRepository {
    /**
     * Use in Calendar Fragment, when touch on a specific date
     * UI will display all todo_s had deadline on selected date
     *
     * @param date selected date on calendar
     * @return list of todo_ on specific date
     */
    StateLiveData<DailyTodoList> getDailyTodoList(Date date);

    StateLiveData<List<TodoDocument>> getAllTodos();

//    /**
//     * Use in Calendar Fragment, when touch on two specific date
//     * UI will display all todo_s had deadline between selected dates
//     *
//     * @param from date lower boundary
//     * @param to date higher boundary
//     * @return list of todo_ between selected dates
//     */
//    StateLiveData<AbstractTodoList> getTodoListByRange(Date from, Date to);

    /**
     * Retrieve all student's todoLists in FireStore database
     * @return all student's todoLists
     */
    StateLiveData<List<TodoListDocument>> getAllTodoLists();
//    LiveData<List<TodoList>> getAllTodoLists_old();

    /**
     * Add a new todo_
     */
    StateLiveData<TodoDocument> addTodo(TodoDocument todo);
//    void addTodo(Todo todo);

    /**
     * Add a new todoList
     */
    StateLiveData<TodoListDocument> addTodoList(TodoListDocument todoList);
//    void addTodoList(TodoList todoList);

    /**
     * Delete an exist todo_
     * @param id of todo_
     */
    StateLiveData<TodoDocument> deleteTodo(String id);
//    Todo deleteTodo(String id);

    /**
     * Delete an exist TodoList and its todo_s
     * @param id of todoList
     */
    StateLiveData<TodoListDocument> deleteTodoList(String id);
//    TodoList deleteTodoList(String id);

    /**
     * Modify an exist todo_
     * @param id of todo_
     * @param newTodo contains modified data
     */
    StateLiveData<TodoDocument> modifyTodo(String id, TodoDocument newTodo);
//    void modifyTodo(String id, Todo newTodo);

    /**
     * Modify an exist todoList
     * @param id of todoList
     * @param newList contains modified data (not its todo_s)
     */
    StateLiveData<TodoListDocument> modifyTodo(String id, TodoListDocument newList);
//    void modifyTodoList(String id, TodoList newList);
}
