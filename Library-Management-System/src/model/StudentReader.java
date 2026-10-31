
package model;

public class StudentReader extends Reader {
    private String studentId;

    public StudentReader(String id, String fullName,
                         String phone, String studentId) {
        super(id, fullName, phone);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public String getReaderType() {
        return "Sinh viên";
    }
}
