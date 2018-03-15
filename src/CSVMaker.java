import java.io.*;
import java.util.LinkedList;

/**
 * Created by Szymon on 03.03.2018.
 */
public class CSVMaker {
    private PrintWriter pw;

    public void saveCSV(String path, LinkedList<String> rows){

        try {
            pw = new PrintWriter(new File(path));
            StringBuilder sb = new StringBuilder();
            sb.append("nr");
            sb.append(';');
            sb.append("fittest");
            sb.append(';');
            sb.append("weakest");
            sb.append(';');
            sb.append("avg");
            sb.append('\n');

            for(String row : rows) {
                sb.append(row);
                sb.append('\n');
            }

            pw.write(sb.toString());
            pw.close();
            System.out.println("CSV Saved.");
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

}
