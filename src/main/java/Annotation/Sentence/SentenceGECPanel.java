package Annotation.Sentence;

import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import AnnotatedSentence.ViewLayerType;
import DataCollector.Sentence.SentenceAnnotatorPanel;
import org.apache.batik.dom.AbstractChildNode;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SentenceGECPanel extends SentenceAnnotatorPanel {
    final private ArrayList<String> errorsList;
    final private ArrayList<String> GecOplist;
    protected DefaultListModel GecListmodel;
    protected JList GecOpList; // operation e.g. R, U, M, etc.
    protected JScrollPane paneGecOpList;
    protected int isAltDown;

    public SentenceGECPanel(String currentPath, String fileName, ArrayList<String> errorsList, ArrayList<String> GecOplist){
        super(currentPath, fileName, ViewLayerType.GRAMMATICAL_ERROR);
        this.errorsList = errorsList;
        this.GecOplist = GecOplist;
        setGecOpList();
        setLayout(new BorderLayout());

        // remove the ActionListener of editText
        for (ActionListener al : super.editText.getActionListeners())
            super.editText.removeActionListener(al);

        editText.addActionListener(actionEvent -> {
            if (clickedWord!=null) {
                String newText = editText.getText();
                if (!newText.contains(" ")) {
                    if (isAltDown == 1) {
                        clickedWord.getGrammaticalError().setWordIndex(newText);
                    } else if (clickedWord.getGrammaticalError().getOperation().equals("M")) {
                        clickedWord.setName(newText);
//                    clickedWord.setGrammaticalErrorEdit(newText);
                    } else {
                        clickedWord.getGrammaticalError().setEditedWord(newText);
                    }
                } else {
                    String[] words = newText.split(" ");
                    for (int i = words.length - 1; i >= 1; i--) {
                        switch (clickedWord.getLanguage()) {
                            case ENGLISH:
                                sentence.insertWord(selectedWordIndex + 1, new AnnotatedWord("{english=" + words[i] + "}"));
                                break;
                            case TURKISH:
                                sentence.insertWord(selectedWordIndex + 1, new AnnotatedWord("{turkish=" + words[i] + "}"));
                                break;
                            case PERSIAN:
                                sentence.insertWord(selectedWordIndex + 1, new AnnotatedWord("{persian=" + words[i] + "}"));
                                break;
                        }
                    }
                    clickedWord.setName(words[0]);
                }
                sentence.writeToFile(new File(fileDescription.getFileName()));
                editText.setVisible(false);
                list.setVisible(false);
                pane.setVisible(false);
                repaint();
            }
        });

    }

    @Override
    public int populateLeaf(AnnotatedSentence sentence, int wordIndex){
        int selectedIndex = -1;
        AnnotatedWord word = (AnnotatedWord) sentence.getWord(wordIndex);
        listModel.clear();
        GecListmodel.clear();
        for (int i = 0; i < errorsList.size(); i++){
            if (word.getGrammaticalError() != null && word.getGrammaticalError().toString().equals(errorsList.get(i))){
                selectedIndex = i;
            }
            listModel.addElement(errorsList.get(i));
        }

        for (int i = 0; i < GecOplist.size(); i++){
            if (word.getGrammaticalError().getOperation() != null && word.getGrammaticalError().getOperation().equals(GecOplist.get(i))){
                selectedIndex = i;
            }
            GecListmodel.addElement(this.GecOplist.get(i));
        }
        return selectedIndex;

    }

    private void setGecOpList(){
        GecListmodel = new DefaultListModel();
        GecOpList = new JList(GecListmodel);
        GecOpList.setVisible(false);
        GecOpList.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting()) {
                if (GecOpList.getSelectedIndex() != -1 && clickedWord != null) {
                    clickedWord.setSelected(false);

                    if (GecOpList.getSelectedValue().toString().equals("M:Before")){
//                        String newSentence = "";
//                        ArrayList<String> wordArray = new ArrayList<String>(Arrays.asList(sentence.toString().split(" ")));
                        String missing = "{english=-}{grammaticalError=-M--}";
                        AnnotatedWord word = new AnnotatedWord(missing);
                        sentence.insertWord(selectedWordIndex, word);

//                        wordArray.add(selectedWordIndex, missing);
//                        for (int i = 0; i < wordArray.size(); i++){
//                            newSentence = newSentence + " " + wordArray.get(i);
//                        }
//                        sentence = new AnnotatedSentence(newSentence);
//                        AnnotatedWord missing_word = ((AnnotatedWord) sentence.getWord(selectedWordIndex));
//                        missing_word.getGrammaticalError().setOperation("M");
                    } else if (GecOpList.getSelectedValue().toString().equals("M:After")) {
//                        String newSentence = "";
//                        ArrayList<String> wordArray = new ArrayList<String>(Arrays.asList(sentence.toString().split(" ")));
//                        String missing = "{english=-}{grammaticalError=-M--}";
//                        wordArray.add(selectedWordIndex+1, missing);
//                        for (int i = 0; i < wordArray.size(); i++){
//                            newSentence = newSentence + " " + wordArray.get(i);
//                        }
//                        sentence = new AnnotatedSentence(newSentence);
//                        AnnotatedWord missing_word = ((AnnotatedWord) sentence.getWord(selectedWordIndex+1));
//                        missing_word.getGrammaticalError().setOperation("M");
                        String missing = "{english=-}{grammaticalError=-M--}";
                        AnnotatedWord word = new AnnotatedWord(missing);
                        sentence.insertWord(selectedWordIndex+1, word);
                    } else if (!clickedWord.getGrammaticalError().getOperation().equals("M")) {
                        clickedWord.getGrammaticalError().setOperation(GecOpList.getSelectedValue().toString());
                    }

                    sentence.writeToFile(new File(fileDescription.getFileName()));
                    GecOpList.setVisible(false);
                    paneGecOpList.setVisible(false);
                    clickedWord = null;
                    GecOpList.setSelectedIndex(-1);
                    repaint();
                }
            }
        });
        GecOpList.setFocusTraversalKeysEnabled(false);
        paneGecOpList = new JScrollPane(GecOpList);
        add(paneGecOpList);
        paneGecOpList.setFocusTraversalKeysEnabled(false);
        setFocusable(false);
    }

    @Override
    protected void setWordLayer() {
        clickedWord.getGrammaticalError().setError(list.getSelectedValue().toString());

    }

    @Override
    protected void setBounds() {
        pane.setBounds(((AnnotatedWord) sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord) sentence.getWord(selectedWordIndex)).getArea().y + 20, 240, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4));

    }

    @Override
    protected void drawLayer(AnnotatedWord word, Graphics g, int currentLeft, int lineIndex, int wordIndex, int maxSize, ArrayList<Integer> wordSize, ArrayList<Integer> wordTotal) {

        if (word.getGrammaticalError()==null) return;

        if (word.getGrammaticalError().getOperation()!= null && !word.getGrammaticalError().getOperation().equals("M")){
            g.setColor(Color.blue);
            g.drawString(wordIndex + "", currentLeft, (lineIndex + 1) * lineSpace - 30); // drawing the index of each word
            g.setColor(Color.BLACK);
            wordIndex++;
        }

        if (word.getGrammaticalError().getError() != null) {
            String opToWrite = "";
            if (!word.getGrammaticalError().equals("None")) {
                opToWrite = word.getGrammaticalError().getError();
            }
            g.setColor(Color.RED);
            g.drawString(opToWrite, currentLeft, (lineIndex + 1) * lineSpace + 50);
        }
        if (word.getGrammaticalError().getOperation() != null) {
            String opToWrite = "";
            if (!word.getGrammaticalError().getOperation().equals("None")) {
                opToWrite = word.getGrammaticalError().getOperation();
            }
            g.setColor(Color.MAGENTA);
            g.drawString(opToWrite, currentLeft, (lineIndex + 1) * lineSpace + 30);
        }
        if (word.getGrammaticalError().getEditedWord() != null) {
            if (!(word.getGrammaticalError().getOperation().equals("M") || (word.getGrammaticalError().getOperation().equals("U")) ||
                        (word.getGrammaticalError().getOperation().equals("UNK")))) {
                g.setColor(Color.GRAY);
                g.drawString(word.getGrammaticalError().getEditedWord(), currentLeft, (lineIndex + 1) * lineSpace + 70);
            }
        }
        if (word.getGrammaticalError().getWordIndex() != null) {
            g.setColor(Color.blue);
            g.drawString(word.getGrammaticalError().getWordIndex(), currentLeft, (lineIndex + 1) * lineSpace + 90);
        }
        g.setColor(Color.RED);

    }

    @Override
    protected int getMaxLayerLength(AnnotatedWord word, Graphics g) {
        int size, maxSize = g.getFontMetrics().stringWidth(word.getName());
        if (word.getGrammaticalError() != null) {
            size = g.getFontMetrics().stringWidth(word.getGrammaticalError().toString());
            if (size > maxSize){
                maxSize = size;
            }
        }
        return maxSize;
    }

    public void mouseDoubleClicked(){
        list.setVisible(false);
        pane.setVisible(false);
        int selectedIndex;
        if (selectedWordIndex != -1){
            selectedIndex = populateLeaf(sentence, selectedWordIndex);
            if (selectedIndex != -1){
                GecOpList.setValueIsAdjusting(true);
                GecOpList.setSelectedIndex(selectedIndex);
            }
            editText.setVisible(false);
            GecOpList.setVisible(true);
            clickedWord = ((AnnotatedWord)sentence.getWord(selectedWordIndex));
            lastClickedWord = clickedWord;
            paneGecOpList.setVisible(true);
            paneGecOpList.getVerticalScrollBar().setValue(0);
            paneGecOpList.setBounds(((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().x, ((AnnotatedWord)sentence.getWord(selectedWordIndex)).getArea().y + 20, 240, (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.4));
            this.repaint();
        }
    }

    @Override
    public void previous(int count) {
        super.previous(count);
        paneGecOpList.setVisible(false);
        repaint();
    }

    @Override
    public void next(int count) {
        super.next(count);
        paneGecOpList.setVisible(false);
        repaint();
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        for (int i = 0; i < sentence.wordCount(); i++){
            AnnotatedWord word = (AnnotatedWord) sentence.getWord(i);
            if (word.getArea().contains(e.getX(), e.getY())){
                word.setSelected(true);
                if (i != selectedWordIndex){
                    if (selectedWordIndex != -1){
                        ((AnnotatedWord)sentence.getWord(i)).setSelected(false);
                    }
                }
                selectedWordIndex = i;
                repaint();
                return;
            }
        }
        if (selectedWordIndex != -1){
            ((AnnotatedWord)sentence.getWord(selectedWordIndex)).setSelected(false);
            selectedWordIndex = -1;
            if (!editText.isVisible()){
                clickedWord = null;
            }
            pane.setVisible(false);
            paneGecOpList.setVisible(false);
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if (mouseEvent.getClickCount() == 2 && mouseEvent.getButton() == MouseEvent.BUTTON1){
            if (layerType == ViewLayerType.GRAMMATICAL_ERROR){
                mouseDoubleClicked();
            }
        } else {
            int selectedIndex;
            if (selectedWordIndex != -1) {
                if (mouseEvent.isControlDown()) {
                    clickedWord = ((AnnotatedWord) sentence.getWord(selectedWordIndex));
                    if (!(clickedWord.getGrammaticalError().getOperation().equals("U") || clickedWord.getGrammaticalError().getOperation().equals("UNK") )){
                        lastClickedWord = clickedWord;
                        isAltDown = 0;
                        editText.setText(clickedWord.getName());
                        editText.setBounds(clickedWord.getArea().x - 5, clickedWord.getArea().y + 30, 100, 30);
                        editText.setVisible(true);
                        pane.setVisible(false);
                        editText.requestFocus();
                    }

                } else if (mouseEvent.isAltDown()) {
                    clickedWord = ((AnnotatedWord) sentence.getWord(selectedWordIndex));
                    lastClickedWord = clickedWord;
                    if ((layerType == ViewLayerType.GRAMMATICAL_ERROR) && (!clickedWord.getGrammaticalError().getOperation().equals("M")))  {
                        isAltDown = 1;
                        editText.setText(""); // maybe set the given index here
                        editText.setBounds(clickedWord.getArea().x - 5, clickedWord.getArea().y + 30, 100, 30);
                        editText.setText(clickedWord.getGrammaticalError().getWordIndex());
                        editText.setVisible(true);
                        pane.setVisible(false);
                        editText.requestFocus();
                    }

                } else if (mouseEvent.isShiftDown() && (clickedWord !=null ) && (clickedWord.getGrammaticalError().getOperation().equals("M"))){
                    String sentAfterRemoval = "";
                    ArrayList<String> wordArray = new ArrayList<String>(Arrays.asList(sentence.toString().split(" ")));
                    wordArray.remove(selectedWordIndex);
                    for (int i = 0; i < wordArray.size(); i++){
                        sentAfterRemoval = sentAfterRemoval + " " + wordArray.get(i);
                    }
                    this.sentence = new AnnotatedSentence(sentAfterRemoval); selectedWordIndex = -1;
                    sentence.writeToFile(new File(fileDescription.getFileName()));


                } else {
                    selectedIndex = populateLeaf(sentence, selectedWordIndex);
                    if (selectedIndex != -1) {
                        list.setValueIsAdjusting(true);
                        list.setSelectedIndex(selectedIndex);
                    }
                    editText.setVisible(false);
                    list.setVisible(true);
                    clickedWord = ((AnnotatedWord) sentence.getWord(selectedWordIndex));
                    lastClickedWord = clickedWord;
                    pane.setVisible(true);
                    pane.getVerticalScrollBar().setValue(0);
                    setBounds();
                    repaint();
                }
            }


        }
        this.repaint();
    }

    @Override
    protected void setLineSpace() {
        lineSpace = 140;

    }


}