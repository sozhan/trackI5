package tmm.tracki5.model;

/**
 * Created by Arun on 02/04/16.
 */
public class AttendanceData {
    String studentName;
    String regNo;
    Boolean isPresent;
    String mark;
    //etc.,

    public AttendanceData(){

    }

    public AttendanceData(String studentName, String regNo, Boolean isPresent){
        this.studentName = studentName;
        this.regNo = regNo;
        this.isPresent = isPresent;
    }

    public AttendanceData(String studentName, String regNo,  String mark){
        this.studentName = studentName;
        this.regNo = regNo;
        this.mark = mark;
    }

    public AttendanceData(String regNo,  int mark){
        this.regNo = regNo;
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(mark);
        this.mark = sb.toString();
    }


    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getAttendanceInfo() {
        return "Roll No: "+this.regNo +", " + this.studentName;
    }


    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public Boolean getIsPresent() {
        return isPresent;
    }

    public void setIsPresent(Boolean isPresent) {
        this.isPresent = isPresent;
    }

    public void setMark(String mark) { this.mark = mark; }

    public String getMark() { return mark; }

    @Override
    public String toString() {
        return "Roll No: "+this.regNo +", " + this.studentName;
    }
}

