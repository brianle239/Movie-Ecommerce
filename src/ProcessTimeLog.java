import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ProcessTimeLog {
    public static void main(String[] args) throws Exception {
        // Path for AWS (ubuntu)

        try {
            String path = "C:\\Program Files\\Apache Software Foundation\\Tomcat 10.1\\webapps\\fabflix_war\\timeLog.txt"; // Windows use .replaceALl("/", "\\\\")
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            long stSum = 0;
            long dbSum = 0;
            int count = 0;
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(":");
                count += 1;
                stSum += Long.parseLong(data[0]);
                dbSum += Long.parseLong(data[1]);

            }
            System.out.println("Samples: " + count);
            System.out.println("Servlet Time AVG: " + (stSum/count));
            System.out.println("JDBC Time AVG: " + (dbSum/count));
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}

