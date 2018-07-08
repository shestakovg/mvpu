package Entitys;

public class Task {
    private int id;
    private String reference;
    private String OutletId;
    private String Description;
    private String ResultDescription;
    private String Number;
    private int Status;
    public  int send = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getOutletId() {
        return OutletId;
    }

    public void setOutletId(String outletId) {
        OutletId = outletId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getResultDescription() {
        return ResultDescription;
    }

    public void setResultDescription(String resultDescription) {
        ResultDescription = resultDescription;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public boolean isResolved() {return Status > 0;}
}
