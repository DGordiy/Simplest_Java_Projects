package search;

public class Main {
    public static void main(String[] args) {
        SearchEngine searchEngine = new SearchEngine();

        int i = 0;
        String fileName = null;
        while (i < args.length) {
            if ("--data".equals(args[i])) {
                fileName = args[i + 1];
                break;
            }

            i++;
        }

        searchEngine.setData(fileName);
        searchEngine.mainMenu();
    }
}
