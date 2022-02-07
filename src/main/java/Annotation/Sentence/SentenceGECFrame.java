package Annotation.Sentence;

import AnnotatedSentence.AnnotatedCorpus;
import DataCollector.ParseTree.TreeEditorPanel;
import DataCollector.Sentence.SentenceAnnotatorFrame;
import DataCollector.Sentence.SentenceAnnotatorPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * The class extends the SentenceAnnotatorFrame class. It is the frame of the annotation tool.
 */
public class SentenceGECFrame extends SentenceAnnotatorFrame {

    private ArrayList<String> errorsList = new ArrayList<String>();
    private ArrayList<String> GecOplist = new ArrayList<String>(){{
        /**
         * The list contains the available grammatical error correction operations.
         */
        add("None");
        add("R");
        add("U");
        add("UNK");
        add("M:Before");
        add("M:After");
    }};

    /**
     * The method call's the super constructor, reads the grammatical errors' list.
     * It adds a JMenuItem "View Annotations" and appends an action listener that creates an object
     * of "ViewSentenceGECAnnotationFrame".
     */

    public SentenceGECFrame() {

        super();
        try {
            Scanner input = new Scanner(new File("errorsList.txt"));
            while (input.hasNext()) {
                errorsList.add(input.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        AnnotatedCorpus annotatedCorpus = new AnnotatedCorpus(new File(TreeEditorPanel.phrasePath + "/combined"));

        JMenuItem itemViewAnnotated = addMenuItem(projectMenu, "View Annotations", KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        itemViewAnnotated.addActionListener(e -> {
            new ViewSentenceGECAnnotationFrame(annotatedCorpus, this);
        });

        JOptionPane.showMessageDialog(this, "Annotated corpus is loaded!", "GEC Annotation", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    protected SentenceAnnotatorPanel generatePanel(String currentPath, String rawFileName) {
        return new SentenceGECPanel(currentPath, rawFileName, errorsList, GecOplist);
    }

}


