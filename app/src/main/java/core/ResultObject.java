package core;

/**
 * Created by shestakov.g on 11.11.2015.
 */
public class ResultObject {
    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Object getSender() {
        return sender;
    }

    public void setSender(Object sender) {
        this.sender = sender;
    }

    private boolean result  = false;
    private String resultMessage = "";

    public ResultObject(Object sender) {
        this.sender = sender;
    }

    private Object sender = null;

    public ResultObject(boolean result, String resultMessage, Object sender) {
        this.result = result;
        this.resultMessage = resultMessage;
        this.sender = sender;
    }
}
