package vnu.uet.mobilecourse.assistant.util;

import vnu.uet.mobilecourse.assistant.model.ICourse;

public class FbAndCourseMap {

    public static boolean equals(ICourse c1, ICourse c2) {
        if (c1 == c2) {
            return true;
        }

        String code1 = c1.getCode();
        String code2 = c2.getCode();

        if (code1.equals(code2)) {
            return true;
        } else {
            code1 = cleanCode(code1);
            code2 = cleanCode(code2);

            return code1.equals(code2);
        }
    }

    public static String cleanCode(String code) {
        if (code.length() < DELIMITER_POS)
            return code;

        // E.g: 1920II_INT2206_21 (lớp tự nguyên)
        String cleanCode = code
                // convert "1920II_INT2206_21 (lớp tự nguyên)" --> "_INT2206_21 (lớp tự nguyên)"
                .replace(StringConst.COURSE_PREFIX, StringConst.EMPTY)
                // convert "_INT2206_21 (lớp tự nguyên)" --> "_INT2206_21 "
                .replaceAll("\\(.*?\\)", StringConst.EMPTY)
                // convert "_INT2206_21 " --> " INT2206 21 "
                .replace(StringConst.UNDERSCORE_CHAR, StringConst.SPACE_CHAR)
                // convert " INT2206 21 " --> "INT2206 21"
                .trim();


        // remove space between alphabet and number
        // Eg: "INT 2206 21" --> "INT2206 21"
        StringBuilder builder = new StringBuilder(cleanCode);
        if (builder.charAt(DELIMITER_POS) == StringConst.SPACE_CHAR) {
            builder.deleteCharAt(DELIMITER_POS);
        }

        return builder.toString();
    }

    public static final int DELIMITER_POS = 3;
}
