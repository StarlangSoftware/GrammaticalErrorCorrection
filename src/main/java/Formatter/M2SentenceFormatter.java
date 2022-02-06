package Formatter;


import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;

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
        for (int i = 0; i < inputSentence.getWords().size(); i++) {
            AnnotatedWord annotatedWord = (AnnotatedWord) inputSentence.getWord(i);
            word = annotatedWord.getName();
            op = annotatedWord.getGrammaticalError().getOperation();
            error = annotatedWord.getGrammaticalError().getError();
            edit = annotatedWord.getGrammaticalError().getEditedWord();;
            String currentIdx = annotatedWord.getGrammaticalError().getWordIndex();

            if (currentIdx.isEmpty()) {
                //recording the positions where a there is a change of index(Word order errors)
                woIdx = woIdx + '#';
            } else {
                woIdx = woIdx + currentIdx;
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
                unks = unks + startIdx;
            } else if (!op.equals("M")) {
                unks = unks + "#";
            }

            if (!op.isEmpty() && !op.equals("UNK")) {
                M2Format m2Format = new M2Format(startIdx, endIdx, op, error, edit, annotatorId);
                m2Formats.add(m2Format);
            }
        }

        processWoGrammaticalErrors();
        processUnkGrammaticalErrors();
        sortM2Formats();

        return m2Formats;
    }

    private void processUnkGrammaticalErrors(){
        for (String item : unks.split("#")){
            if (item.isEmpty())
                continue;
           char[] arr = item.toCharArray();
           startIdx = arr[0] - '0';
           endIdx = ((arr[arr.length-1]) - '0') + 1;
            edit = String.join(" ", originalSentence.subList(startIdx, endIdx));
            M2Format m2Format = new M2Format(startIdx, endIdx, "UNK", "", edit, annotatorId);
            m2Formats.add(m2Format);
        }

    }
    private void processWoGrammaticalErrors(){
        System.out.println(woIdx);
        List<String>  filteredWoIdx = new ArrayList<String>(Arrays.asList(woIdx.split("#")));
        filteredWoIdx.removeAll(Collections.singleton(""));
        System.out.println(filteredWoIdx);
        for (String s : filteredWoIdx){
            char[] arr = s.toCharArray();
            edit = "";
            for (int i=0; i < arr.length; i++){
                edit = edit + originalSentence.get(arr[i] - '0');
                if (!( i == arr.length-1))
                    edit = edit + " ";
            }
            Arrays.sort(arr);
            startIdx = arr[0] - '0';
            endIdx = (arr[arr.length-1] - '0') + 1;
            M2Format m2Format = new M2Format(startIdx, endIdx, "R", "WO", edit, annotatorId);
            m2Formats.add(m2Format);

        }
    }

    private void sortM2Formats(){
        Comparator<M2Format> comparator = Comparator.comparing(M2Format::getStartIdx).thenComparing(M2Format::getEndIdx);
        m2Formats.sort(comparator);
    }
}