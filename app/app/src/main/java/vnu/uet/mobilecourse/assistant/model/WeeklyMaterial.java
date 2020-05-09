package vnu.uet.mobilecourse.assistant.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import vnu.uet.mobilecourse.assistant.util.TimestampConverter;

import java.util.Date;

@Entity
public class WeeklyMaterial {
    @PrimaryKey
    private int id;
    private String title;
    @TypeConverters({TimestampConverter.class})
    private Date from;
    @TypeConverters({TimestampConverter.class})
    private Date to;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }
}
