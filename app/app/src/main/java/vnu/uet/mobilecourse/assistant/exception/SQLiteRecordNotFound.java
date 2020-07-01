package vnu.uet.mobilecourse.assistant.exception;

public class SQLiteRecordNotFound extends Exception{
    public SQLiteRecordNotFound(String info) {
        super("Không tìm thấy bản ghi: " + info);
    }
}
