package zipconcurrent;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static boolean isStopped = false;

    public static void main(String[] args) throws IOException {

        ExecutorService executor = Executors.newFixedThreadPool(5);
        try (Scanner sc = new Scanner(System.in)) {
            while (!isStopped) {

                String command = Helper.enterCommand(sc);

                String pathSrc = Helper.enterPathSrc(sc);

                String nameRezultFile = Helper.enterRezultFileName(sc);

                Runnable worker = new ZipOperations(command, pathSrc, nameRezultFile);
                executor.execute(worker);

                isStopped = Helper.isStop(sc);
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all files zip or unzip");
    }

}



