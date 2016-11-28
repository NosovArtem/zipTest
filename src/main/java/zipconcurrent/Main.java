package zipconcurrent;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static boolean isStopped = false;

    public static void main(String[] args) throws ZipCommandNotFoundException {
//  27.11.2016 первые пробелы в командах....добавить уведомление при завершении операции
//  27.11.2016 импользовать try с ресурсами . проверить везде.
        ExecutorService executor = Executors.newFixedThreadPool(3);
        try (Scanner sc = new Scanner(System.in)) {
            while (!isStopped) {
                System.out.println("Select operations. \nEnter (Z)Zip or (U)UnZip:");
                String command = sc.nextLine();
                Boolean isArchiving = null;
                if (command.toUpperCase().trim().equals("Z") || command.toUpperCase().trim().equals("ZIP")) {
                    isArchiving = true;
                } else if(command.toUpperCase().trim().equals("U") || command.toUpperCase().trim().equals("UNZIP")){
                    isArchiving = false;
                }else{
                    System.out.println("Invalid command. Try again.");
                   throw new ZipCommandNotFoundException("Invalid command. Try again.");
                }

                System.out.println("Enter the path to the file. \nExample: C:\\test\\MB.mdx");
                String pathSrc = sc.nextLine();
                File file = new File(pathSrc);
                if (!file.exists()) {
                    System.out.println("Invalid path to the file. Try enter path to the file again..");
                    throw new ZipCommandNotFoundException("");
                }

                System.out.println("Enter the name of the archive/folder. \n" +
                        "The program will place the files/archive in the same directory where the file is located");
                String nameRezultZipFile = sc.nextLine();
                if ("".equals(nameRezultZipFile)) {
                    System.out.println("You did not specify the name of the output file.");
                    throw new ZipCommandNotFoundException("");
                }

                Runnable worker = new ZipOperations(isArchiving, pathSrc, nameRezultZipFile);
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



