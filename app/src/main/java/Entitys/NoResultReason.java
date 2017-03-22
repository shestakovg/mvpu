package Entitys;

/**
 * Created by shest on 3/19/2017.
 */

public class NoResultReason {
    private int Id;
    private String Description;

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }

    private boolean isTrue;

    public NoResultReason(int id, String description,boolean aTrue) {
        Id = id;
        Description = description;
        isTrue = aTrue;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
