package Entitys;

/**
 * Created by g.shestakov on 11.06.2015.
 */
public class sendResult {
    public Boolean getResult() {
        return result;
    }

    private Boolean result = true;

    public void setFail()
    {
        this.result = false;
    }

    private  int recordTransfer = 0;

    public void incRecordTransfer()
    {
        this.recordTransfer++;
    }
}
