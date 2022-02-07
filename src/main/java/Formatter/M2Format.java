package Formatter;


/**
 * The class holds the format of the M2ErrorFormat.
 */

public class M2Format {
    private int startIndex;
    private int endIndex;
    private String operation;
    private String grammaticalError;
    private String editOfWord;
    private int annotatorId;

    public M2Format(int startIndex, int endIndex, String operation, String grammaticalError, String editOfWord, int annotatorId){
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.operation = operation;
        this.grammaticalError = grammaticalError;
        this.editOfWord = editOfWord;
        this.annotatorId = annotatorId;
    }


    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = M2Format.this.endIndex;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getGrammaticalError() {
        return grammaticalError;
    }

    public void setGrammaticalError(String grammaticalError) {
        this.grammaticalError = grammaticalError;
    }

    public String getEditOfWord() {
        return editOfWord;
    }

    public void setEditOfWord(String editOfWord) {
        this.editOfWord = editOfWord;
    }

    public int getAnnotatorId() {
        return annotatorId;
    }

    public void setAnnotatorId(int annotatorId) {
        this.annotatorId = annotatorId;
    }

    public String getNoGrammaticalErrorFromat(){
        return "A -1 -1|||nooperation|||-NONE-|||REQUIRED|||-NONE-|||0";
    }
    public String toString(){
        String colon = ":";
        if (operation.equals("UNK"))
            colon = "";
        return "A " + startIndex + " " + endIndex  + "|||" + operation + colon + grammaticalError + "|||" + editOfWord +  "|||REQUIRED|||-NONE|||"+annotatorId;
    }

}
