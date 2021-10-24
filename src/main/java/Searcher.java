import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

    private static String RESULTS_PATH="results.txt";
    private static String INDEX="index";
    private static String QUERY_PATH ="cran_dataset/cran.qry";

    public void main() throws Exception {

        String queryString = "";

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX)));
        IndexSearcher searcher = new IndexSearcher(reader);

        //Analyzer analyzer = new SimpleAnalyzer();
        //Analyzer analyzer = new WhitespaceAnalyzer();;
        Analyzer analyzer = new EnglishAnalyzer();

        PrintWriter writer = new PrintWriter(RESULTS_PATH, "UTF-8");

        //BM25 Similarity
        searcher.setSimilarity(new BM25Similarity());

        //Classic Similarity
        //searcher.setSimilarity(new ClassicSimilarity());

        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(QUERY_PATH), StandardCharsets.UTF_8);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "author", "bibliography", "words"}, analyzer);

        String line = bufferedReader.readLine();

        System.out.println("Reading the queries and creating results");

        String id = "";
        int index=0;

        while (line != null) {
            index++;
            if (line.startsWith(".I")) {
                id = Integer.toString(index);
                line = bufferedReader.readLine();
            }
            if (line.startsWith(".W")) {
                line = bufferedReader.readLine();
                while (line != null && !line.startsWith(".I")) {
                    queryString += line + " ";
                    line = bufferedReader.readLine();
                }
            }
            queryString = queryString.trim();
            Query query = parser.parse(QueryParser.escape(queryString));
            queryString = "";
            search(searcher, writer, Integer.parseInt(id), query);
        }

        System.out.println("Results have been written to the file.");
        writer.close();
        reader.close();
    }

    public static void search(IndexSearcher searcher, PrintWriter writer, Integer queryNumber, Query query) throws IOException {

        TopDocs results = searcher.search(query, 1400);
        ScoreDoc[] hits = results.scoreDocs;

        for (int i = 0; i < hits.length; i++) {
            Document doc = searcher.doc(hits[i].doc);
            writer.println(queryNumber + " 0 " + doc.get("id") + " " + i + " " + hits[i].score + " ....");
        }
    }
}
