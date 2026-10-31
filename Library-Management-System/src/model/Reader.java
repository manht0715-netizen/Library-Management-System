
package model;

public abstract class Reader {
    protected String id;
    protected String fullName;
    protected String phone;

    public Reader(String id, String fullName, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public abstract String getReaderType();
}
