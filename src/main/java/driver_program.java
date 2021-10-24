// main function to run the code
public class driver_program {
    public static void main (String[] args) throws Exception {
        CreateIndex createIndex = new CreateIndex();
        Searcher searcher = new Searcher();

        createIndex.main();
        searcher.main();
    }
}
