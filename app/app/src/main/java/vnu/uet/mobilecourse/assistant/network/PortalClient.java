package vnu.uet.mobilecourse.assistant.network;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import vnu.uet.mobilecourse.assistant.model.FinalExam;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PortalClient {
    private static final String URL = "http://112.137.129.87/congdaotao/module/dsthi/index.php";
    public static List<FinalExam> getFinalExamSchedule(String studentId) throws IOException, ParseException {
        ArrayList<FinalExam> finalExams = new ArrayList<>();
        Document doc = Jsoup.connect(URL).data("keysearch", studentId).post();
        Log.d("COURSE_DEBUG", "getExamSchedule: " + doc);
        Elements table = doc.select(".items > tbody > tr");
        if(table.size() == 0){
            return finalExams;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy HH:mm");
        for (int i = 0; i < table.size() ; i++) {
            Element row = table.get(i);
            Elements cols = row.select("td");
            FinalExam e = new FinalExam();
            e.setClassCode(cols.get(6).text());
            e.setClassName(cols.get(7).text());
            e.setForm(cols.get(13).text());
            e.setIdNumber(cols.get(5).text());
            e.setExamTime(dateFormat.parse(cols.get(8).text() + " " + cols.get(9).text()));
            e.setPlace(cols.get(12).text() + ", " + cols.get(11).text());
            finalExams.add(e);
        }
        return finalExams;
    }
}
