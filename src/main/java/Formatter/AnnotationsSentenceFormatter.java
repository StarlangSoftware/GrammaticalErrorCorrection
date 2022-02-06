package Formatter;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

import AnnotatedSentence.AnnotatedSentence;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenizerModel;
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

public class AnnotationsSentenceFormatter {

    private static String sourceRootFolderName = "corpus";
    private  static String targetRootFolderName = "data";
    private  static String fileSourceName = "yadyok";

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
            // refer to model file "en-sent,bin", available at link http://opennlp.sourceforge.net/models-1.5/
            InputStream en_sent= new FileInputStream("en-sent.bin");
            SentenceModel sent_model = new SentenceModel(en_sent);
            // feed the model to SentenceDetectorME class
            SentenceDetectorME sdetector = new SentenceDetectorME(sent_model);
            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

            if (extension.equals("pdf"))
                text = pdfReader(file);
            else if (extension.equals("docx"))
                text = docxReader(file);

            String sentences[] = sdetector.sentDetect(text);
            sentences = Arrays.copyOfRange(sentences, 1, sentences.length);
            for (String sent: sentences){
                String tokens[] = tokenizer.tokenize(sent);
                String annotationFormat = "";
                int i = 0;
                for (String token : tokens){
                    annotationFormat = annotationFormat +  "{english=" + token +  "}{grammaticalError=---}";
                    if(!(i++ == sentences.length - 1)){
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

//                        private static void copyFileUsingJava7Files(File source, File dest) throws IOException {
                        fileName = String.format("%04d", counter);
                        Files.copy(file.toPath(), new File(targetRootFolderName + "/" + "combined"+"/"+ fileName + ".train").toPath());
//                        }

//                        file.renameTo(new File(fileName));
                        counter++;
                    }
                }
            }
        }

    }

    public static String docxReader(File file_in) throws IOException, InvalidFormatException {
        FileInputStream fis = new FileInputStream(file_in);
        XWPFDocument file = new XWPFDocument(OPCPackage.open(fis));
        XWPFWordExtractor ext = new XWPFWordExtractor(file);
        return ext.getText();

    }
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
