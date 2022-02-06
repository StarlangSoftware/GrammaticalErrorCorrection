package Annotation.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import AnnotatedSentence.AnnotatedSentence;
import AnnotatedSentence.AnnotatedWord;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.Sentence.ViewSentenceAnnotationFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ViewSentenceGECAnnotationFrame extends ViewSentenceAnnotationFrame implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        dataTable.invalidate();
    }

    public class GrammaticalErrorTableDataModel extends TableDataModel {

        public String getColumnName(int col) {
            switch (col) {
                case FILENAME_INDEX:
                    return "FileName";
                case WORD_POS_INDEX:
                    return "Index";
                case WORD_INDEX:
                    return "Word";
                case 3:
                    return "Grammatical Error";
                case 4:
                    return "Sentence";
                default:
                    return "";
            }
        }
    }



    protected void prepareData(AnnotatedCorpus corpus){
        data = new ArrayList<>();
        for (int i = 0; i < corpus.sentenceCount(); i++){
            AnnotatedSentence sentence = (AnnotatedSentence) corpus.getSentence(i);
            for (int j = 0; j < corpus.getSentence(i).wordCount(); j++){
                AnnotatedWord word = (AnnotatedWord) sentence.getWord(j);
                ArrayList<String> row = new ArrayList<>();
                row.add(sentence.getFileName());
                row.add("" + (j + 1));
                row.add(word.getName());
                if (word.getGrammaticalError() != null){
                    row.add(word.getGrammaticalError().toString());
                    row.add("<html>" + sentence.toNamedEntityString(j) + "</html>");
                } else {
                    row.add("NONE");
                    row.add(sentence.toWords());
                }
                row.add("" + i);
                row.add("0");
                data.add(row);
            }
        }
    }

    public ViewSentenceGECAnnotationFrame(AnnotatedCorpus corpus, SentenceGECFrame sentenceGECFrame){
        super(corpus);
        COLOR_COLUMN_INDEX = 6;
        TAG_INDEX = 3;
        prepareData(corpus);
        dataTable = new JTable(new GrammaticalErrorTableDataModel());
        dataTable.getColumnModel().getColumn(FILENAME_INDEX).setMinWidth(150);
        dataTable.getColumnModel().getColumn(FILENAME_INDEX).setMaxWidth(150);
        dataTable.getColumnModel().getColumn(WORD_POS_INDEX).setMinWidth(60);
        dataTable.getColumnModel().getColumn(WORD_POS_INDEX).setMaxWidth(60);
        dataTable.getColumnModel().getColumn(WORD_INDEX).setWidth(200);
        dataTable.getColumnModel().getColumn(TAG_INDEX).setWidth(200);
        dataTable.setDefaultRenderer(Object.class, new CellRenderer());
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2){
                    int row = dataTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        String fileName = data.get(row).get(0);
                        sentenceGECFrame.addPanelToFrame(sentenceGECFrame.generatePanel(TreeEditorPanel.phrasePath, fileName), fileName);
                    }
                }
            }
        });
        JScrollPane tablePane = new JScrollPane(dataTable);
        add(tablePane, BorderLayout.CENTER);
    }

}
