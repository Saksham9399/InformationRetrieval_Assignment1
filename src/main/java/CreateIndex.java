import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CreateIndex {

    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "index";
    // Document path for the cran dataset
    private static String DOCUMENT_PATH = "cran_dataset/cran.all.1400";

    public void main() {

        final Path doc = Paths.get(DOCUMENT_PATH);
        if (!Files.isReadable(doc)) {
            System.out.println("Document'" + doc.toAbsolutePath() + "is invalid");
            System.exit(1);
        }
        try {
            System.out.println("Indexing to directory" + INDEX_DIRECTORY );

            Directory dir= FSDirectory.open(Paths.get(INDEX_DIRECTORY));

            //Analyzer analyzer = new SimpleAnalyzer();
            //Analyzer analyzer = new WhitespaceAnalyzer();
            Analyzer analyzer = new EnglishAnalyzer();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            //BM25 Similarity
            iwc.setSimilarity(new BM25Similarity());

            //Classic Similarity
            //iwc.setSimilarity(new ClassicSimilarity());

            iwc.setOpenMode(OpenMode.CREATE);

            IndexWriter writer = new IndexWriter(dir, iwc);
            createIndex(writer, doc);

            writer.close();

        } catch (IOException error) {
            System.out.println(" caught error:" + error.getClass() + "\n with message: " + error.getMessage());
        }
    }

    static Document createDocument(String id, String title, String author, String bib, String words){
        Document doc = new Document();
        doc.add(new StringField("id", id, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("author", author, Field.Store.YES));
        doc.add(new TextField("bibliography", bib, Field.Store.YES));
        doc.add(new TextField("words", words, Field.Store.YES));
        return doc;
    }

    static void createIndex(IndexWriter writer, Path file) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {

            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

            String id = "";
            String title = "";
            String author = "";
            String bibliography = "";
            String words= "";
            String value = "";
            String line_text;
            Boolean first = true;

            System.out.println("Indexing documents...");

            while ((line_text = buffer.readLine()) != null){
                switch(line_text.substring(0,2)){
                    case ".I":
                        if(!first){
                            Document d = createDocument(id,title,author,bibliography,words);
                            writer.addDocument(d);
                        }
                        else{ first=false; }
                        title = ""; author = ""; bibliography = ""; words = "";
                        id = line_text.substring(3); break;
                    case ".T":
                    case ".A":
                    case ".B":
                    case ".W":
                        value = line_text;
                        break;
                    default:
                        switch(value){
                            case ".T": title += line_text + " ";
                            break;
                            case ".A": author += line_text + " ";
                            break;
                            case ".B": bibliography += line_text + " ";
                            break;
                            case ".W": words += line_text + " ";
                            break;
                        }
                }
            }
            Document d = createDocument(id,title,author,bibliography,words);
            writer.addDocument(d);
        }
    }
}
