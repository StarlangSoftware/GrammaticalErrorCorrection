package Formatter;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

import Corpus.Sentence;
import Dictionary.Word;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import Corpus.EnglishSplitter;

/**
 * The class is responsible for splitting the essays into sentences and saving each sentence in a file.
 */

public class AnnotationsSentenceFormatter {

    private static String sourceRootFolderName = "corpus"; // a folder containing folders (like yadyok below)
    private  static String targetRootFolderName = "data";
    private  static String fileSourceName = "yadyok"; // coprpus/yadyok
    static EnglishSplitter splitter;
    public static void main(String[] args) throws IOException, InvalidFormatException {

        File folder = new File( sourceRootFolderName + "/" + fileSourceName);

        File dir = new File(targetRootFolderName + "/" + fileSourceName);
        if (!dir.exists()){
            dir.mkdirs();
        }

        File[] listOfFiles = folder.listFiles();
        int counter = 0;
        for (File file : listOfFiles) {
            String extension = FilenameUtils.getExtension(String.valueOf(file));
            String text = "";

            if (extension.equals("pdf"))
                text = pdfReader(file);
            else if (extension.equals("docx"))
                text = docxReader(file);

            splitter = new EnglishSplitter();
            ArrayList<Sentence> sentences = splitter.split(text);
            for (Sentence sent: sentences){
                ArrayList<Word> tokens = sent.getWords();
                String annotationFormat = "";
                int i = 0;
                for (Word token : tokens){
                    annotationFormat = annotationFormat +  "{english=" + token +  "}{grammaticalError=---}";
                    if(!(i++ == sentences.size() - 1)){
                        annotationFormat = annotationFormat + " ";
                    }
                }

                String fileName = String.format("%04d", counter);
                new File(targetRootFolderName + "/" + fileSourceName+ "/" + fileName + ".train");
                FileWriter myWriter = new FileWriter(targetRootFolderName + "/" + fileSourceName + "/" + fileName + ".train");
                myWriter.write(annotationFormat);
                myWriter.close();
                counter++;
            }
        }

        combineSentences();

    }

    /**
     * Combines all the annotated sentences in the subfolders in one folder.
     * @throws IOException
     */
    public static void combineSentences() throws IOException {

        File combined_folder = new File(targetRootFolderName + "/combined" );
        if (!combined_folder.exists()){
            combined_folder.mkdirs();
        }

        File folder = new File(targetRootFolderName);
        File[] listOfFiles = folder.listFiles();
        String fileName = "";
        int counter = 0;
        for (File dir : listOfFiles) {
            if (dir.isDirectory()){
                File subfolder = new File(String.valueOf(dir));
                File[] sentencFiles = subfolder.listFiles();
                for (File file : sentencFiles) {
                    if (file.isFile()) {
                        fileName = String.format("%04d", counter);
                        Files.copy(file.toPath(), new File(targetRootFolderName + "/" + "combined"+"/"+ fileName + ".train").toPath());
                        counter++;
                    }
                }
            }
        }

    }

    /**
     * Reads a docx file and returns all the text in it.
     * @param file_in
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     */
    public static String docxReader(File file_in) throws IOException, InvalidFormatException {
        FileInputStream fis = new FileInputStream(file_in);
        XWPFDocument file = new XWPFDocument(OPCPackage.open(fis));
        XWPFWordExtractor ext = new XWPFWordExtractor(file);
        return ext.getText();

    }

    /**
     * Reads a pdf file and returns the text in it.
     * @param file
     * @return
     * @throws IOException
     */
    public static String pdfReader(File file) throws IOException {
        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        PDFParser parser = new PDFParser(new RandomAccessBufferedFileInputStream(file));
        parser.parse();
        try (COSDocument cosDoc = parser.getDocument()) {
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(5);
            String parsedText = pdfStripper.getText(pdDoc);
            return parsedText;
        }
    }
}
