import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 * Created by Szymon on 26.02.2018.
 */
public class MatrixFileReader {

    private BufferedReader bufferedReader;
    private int matrixSize;

    private int[][] distanceMatrix;
    private int[][] flowMatrix;

    public void readMatrixFromFile(String path){
        try
        {
            bufferedReader = new BufferedReader(new FileReader(path));

            matrixSize = Integer.parseInt(bufferedReader.readLine().replaceAll("\\s",""));

            distanceMatrix = new int[matrixSize][matrixSize];
            flowMatrix = new int[matrixSize][matrixSize];


            bufferedReader.readLine(); //read blank line

            for(int i=0; i<matrixSize; i++){
                String line = bufferedReader.readLine().replaceAll("\\s","");
                for(int j=0; j<matrixSize; j++){
                    distanceMatrix[i][j] = Integer.parseInt(String.valueOf(line.charAt(j)));
                }
            }

            bufferedReader.readLine(); //read blank line

            for(int i=0; i<matrixSize; i++){
                String line = bufferedReader.readLine().replaceAll("\\s","");
                for(int j=0; j<matrixSize; j++){
                    flowMatrix[i][j] = Integer.parseInt(String.valueOf(line.charAt(j)));
                }
            }
        }
        catch(IOException ioe){
            System.out.println("File reading failed.");
            ioe.printStackTrace();
        }
    }

    public int[][] getDistanceMatrix(){
        return this.distanceMatrix;
    }

    public int[][] getFlowMatrix(){
        return this.flowMatrix;
    }

    public int getMatrixSize(){
        return this.matrixSize;
    }
}
