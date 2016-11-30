package zipconcurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static boolean isStopped = false;

    public static void main(String[] args) throws ZipCommandNotFoundException, FileNotFoundException {



        ExecutorService executor = Executors.newFixedThreadPool(5);
        try (Scanner sc = new Scanner(System.in)) {
          while (!isStopped) {
                System.out.println("Select operations. \nEnter (Z)Zip or (U)UnZip:");
                String command = sc.nextLine();

                System.out.println("Enter the path to the file. \nExample: C:\\test\\MB.mdx");
                String pathSrc = sc.nextLine();

                System.out.println("Enter the name of the archive/folder. \n" +
                        "The program will place the files/archive in the same directory where the file is located");
                String nameRezultZipFile = sc.nextLine();

                Runnable worker = new ZipOperations(command, pathSrc, nameRezultZipFile);
                executor.execute(worker);
                System.out.println("You want to continue? \n" +
                        "After completion of the program, working with files continues if the work was not completed.\n" +
                        "(E)Exit or (C)Continue");
                String exit = sc.nextLine();
                if (exit.toUpperCase().trim().equals("E") || exit.toUpperCase().trim().equals("EXIT")) {
                    isStopped = true;
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");
    }

}



