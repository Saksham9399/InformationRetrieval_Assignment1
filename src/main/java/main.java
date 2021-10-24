// main function to run the code
public class main {
    public static void main (String[] args) throws Exception {
        CreateIndex createIndex = new CreateIndex();
        Searcher searcher = new Searcher();

        createIndex.main();
        searcher.main();
    }
}
