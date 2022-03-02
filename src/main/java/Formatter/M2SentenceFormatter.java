package Formatter;


import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import org.apache.bcel.generic.ACONST_NULL;

import javax.swing.plaf.IconUIResource;
import java.lang.reflect.Array;
import java.util.*;

public class M2SentenceFormatter {

    private AnnotatedSentence inputSentence;
    private String word;
    private int startIdx;
    private int endIdx;
    private String op;
    private String error;
    private String edit;
    private int annotatorId;
    private ArrayList<String> originalSentence = new ArrayList<String>();
    private int missingCount = 0;
    private ArrayList<M2Format> m2Formats = new ArrayList<M2Format>();
    private String woIdx = "";
    private String unks;

    public  M2SentenceFormatter(String sentence){
        inputSentence = new AnnotatedSentence(sentence);
    }


    public ArrayList<M2Format> getM2Formats() {
        return m2Formats;
    }

    public String getOriginalSentence() {
        return String.join(" ", originalSentence);
    }

    public ArrayList<M2Format> formatSentence() {
        unks = "";
        String currentIdx;
        for (int i = 0; i < inputSentence.getWords().size(); i++) {
            AnnotatedWord annotatedWord = (AnnotatedWord) inputSentence.getWord(i);
            word = annotatedWord.getName();
            if (annotatedWord.getGrammaticalError() != null) {
                op = annotatedWord.getGrammaticalError().getOperation();
                error = annotatedWord.getGrammaticalError().getError();
                edit = annotatedWord.getGrammaticalError().getEditedWord();
                 currentIdx = annotatedWord.getGrammaticalError().getWordIndex();
            }
            else {
                op = "";
                error = "";
                edit = "";
                currentIdx = "";
            }

            if (currentIdx.isEmpty()) {
                //recording the positions where a there is a change of index(Word order errors)
                woIdx = woIdx + '#';
            } else {
                woIdx = woIdx + currentIdx +"%";
            }

            if (!op.equals("M"))
                originalSentence.add(word);

            if (op.equals("R") || op.equals("U") || op.equals("UNK")) {
                startIdx = i - missingCount;
                endIdx = (i-missingCount) + 1;

            }else if (op.equals("M")){
                startIdx = i - missingCount;
                endIdx = i - missingCount;
                missingCount++;
            }

            if (op.equals("UNK")){
                edit = word;
                // recording their positions to combine the consecutive ones later as a single m1 format.
                unks = unks + startIdx + "%";
            } else if (!op.equals("M")) {
                unks = unks + "#";
            }

            if (!op.isEmpty() && !op.equals("UNK")) {
                M2Format m2Format = new M2Format(startIdx, endIdx, op, error, edit, annotatorId);
                m2Formats.add(m2Format);
            }
        }

        try{
            processWoGrammaticalErrors();
        } catch (Exception exception){} //TODO change the separator - to something that rarely happens in text. Current problem e.g. (ADJ-R-break-resistant-)

        processUnkGrammaticalErrors();
        sortM2Formats();

        return m2Formats;
    }

    private void processUnkGrammaticalErrors(){
        List<String>  filteredWoIdx = new ArrayList<String>(Arrays.asList(unks.split("#")));
        filteredWoIdx.removeAll(Collections.singleton(""));
        for (String s : filteredWoIdx) {
            List<String> filteredSubWoIdx = new ArrayList<String>(Arrays.asList(s.split("%")));
            startIdx = Integer.parseInt(filteredSubWoIdx.get(0));
            endIdx = Integer.parseInt(filteredSubWoIdx.get(filteredSubWoIdx.size()-1)) + 1;
            edit = String.join(" ", originalSentence.subList(startIdx, endIdx));
            M2Format m2Format = new M2Format(startIdx, endIdx, "UNK", "", edit, annotatorId);
            m2Formats.add(m2Format);
        }
    }

    /**
     * The method processes the @woIdx string that contains either a hash, a number, or a percent.
     * The method splits the string by #, to end up with a list of strings containing the modified indices
     * separated by %. The string is splitted again by % returning a list of numbers which gets sorted.
     * Finally, it sets the number at index 0 as the start index and the number at index -1, plus 1, as the end index.
     */
    private void processWoGrammaticalErrors(){
        List<String>  filteredWoIdx = new ArrayList<String>(Arrays.asList(woIdx.split("#")));
        filteredWoIdx.removeAll(Collections.singleton(""));
        for (String s : filteredWoIdx){
            List<String> filteredSubWoIdx = new ArrayList<String>(Arrays.asList(s.split("%")));
            Collections.sort(filteredSubWoIdx);
            startIdx = Integer.parseInt(filteredSubWoIdx.get(0));
            endIdx = Integer.parseInt(filteredSubWoIdx.get(filteredSubWoIdx.size()-1)) + 1;
            M2Format m2Format = new M2Format(startIdx, endIdx, "R", "WO", edit, annotatorId);
            m2Formats.add(m2Format);

        }
    }


    private void sortM2Formats(){
        Comparator<M2Format> comparator = Comparator.comparing(M2Format::getStartIndex).thenComparing(M2Format::getEndIndex);
        m2Formats.sort(comparator);
    }
}