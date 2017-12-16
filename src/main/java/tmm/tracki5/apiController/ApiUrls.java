package tmm.tracki5.apiController;

/**
 * Created by Arun on 19/02/16.
 */
public class ApiUrls {

    public static final String PRIMARY_ENDPOINT = "http://104.43.16.155/BackToSchoolApi/Api/";

    public static final String LOGIN = PRIMARY_ENDPOINT+"Authentication/Login";

    public static final String AUTHORIZE = PRIMARY_ENDPOINT+"Authentication/Authorize";

    public static final String GET_SUBJECT_LIST = PRIMARY_ENDPOINT+"School/AllSubjects";

    public static final String GET_SUBJECT_LIST_CLASS_FILTER = PRIMARY_ENDPOINT+"ClassData/SubjectsList";

    public static final String GET_CLASS_SECTION_LIST = PRIMARY_ENDPOINT+"School/GetGradesSectionList";

    public static final String GET_EXAM_LIST = PRIMARY_ENDPOINT+"ClassData/GetPlannedExamsList";

    public static final String GET_MARKS = PRIMARY_ENDPOINT+"Marks/GetMarks";

    public static final String GET_CLASS_EXAMS = PRIMARY_ENDPOINT+"School/ApplicableClassesAndCompletedExams";

    public static final String GET_STUDENTS_LIST_CLASS_FILTER = PRIMARY_ENDPOINT+"ClassData/StudentsList";

    public static final String UPLOAD_ATTENDANCE = PRIMARY_ENDPOINT+"Attendance/UploadAttendance";

    public static final String ATTENDANCE_REPORT = PRIMARY_ENDPOINT+"Attendance/ClassAttendanceReport";

    public static final String UPLOAD_MARKS = PRIMARY_ENDPOINT+"Marks/UploadMarks";

}
