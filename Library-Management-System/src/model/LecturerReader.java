
package model;

public class LecturerReader extends Reader {
    private String department;

    public LecturerReader(String id, String fullName,
                          String phone, String department) {
        super(id, fullName, phone);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public String getReaderType() {
        return "Giảng viên";
    }
}
