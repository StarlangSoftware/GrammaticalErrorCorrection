package Formatter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TestM2SentenceFormatter {

    /**
     * The method reads the files in a folder given the foldername and formats the annotated sentences in M2formats.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        String fileName = "data/yadyok";
        File folder = new File(fileName);
        File[] listOfFiles = folder.listFiles();
        int counter = 0;
        for (File file : listOfFiles) {
            counter++;
            if (file.isFile()) {
                String sentence = readFile(String.valueOf(file));
                System.out.println(sentence);
                M2SentenceFormatter m2Formatter  = new M2SentenceFormatter(sentence);
                ArrayList<M2Format> m2Formats = m2Formatter.formatSentence();
               System.out.println("S " + m2Formatter.getOriginalSentence());
                for (M2Format m2: m2Formats){
                    System.out.println(m2);
                }
                System.out.println("\n");
            }
        }
        System.out.println(counter);
    }

    public static String readFile(String fileName) throws IOException {
        File file = new File(fileName);
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
}


