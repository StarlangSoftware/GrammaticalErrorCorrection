package Formatter;

public class M2Format {
    private int startIdx;
    private int endIdx;
    private String op;
    private String error;
    private String edit;
    private int annotatorId;

    public M2Format(int startIdx, int endIdx, String op, String error, String edit, int annotatorId){
        this.startIdx = startIdx;
        this.endIdx = endIdx;
        this.op = op;
        this.error = error;
        this.edit = edit;
        this.annotatorId = annotatorId;
    }

    public M2Format(){

    }

    public int getStartIdx() {
        return startIdx;
    }

    public void setStartIdx(int startIdx) {
        this.startIdx = startIdx;
    }

    public int getEndIdx() {
        return endIdx;
    }

    public void setEndIdx(int endIdx) {
        this.endIdx = endIdx;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public int getAnnotatorId() {
        return annotatorId;
    }

    public void setAnnotatorId(int annotatorId) {
        this.annotatorId = annotatorId;
    }

    public String getNoErrorFromat(){
        return "A -1 -1|||noop|||-NONE-|||REQUIRED|||-NONE-|||0";
    }
    public String toString(){
        String colon = ":";
        if (op.equals("UNK"))
            colon = "";
        return "A " + startIdx + " " + endIdx  + "|||" + op + colon + error + "|||" + edit +  "|||REQUIRED|||-NONE|||"+annotatorId;
    }

}
