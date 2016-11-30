package zipconcurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Helper {

    public static void fileIsExists(String pathSrc) throws FileNotFoundException {
        File file = new File(pathSrc);
        if (!file.exists()) {
            throw new FileNotFoundException("Invalid path to the file. pathSrc:" + pathSrc);
        }
    }

    public static boolean isStop(Scanner sc) {
        System.out.println("Do you want to repeate operation Zip or Unzip?  \n" +
                "After completion of the program, working with files continues if the work was not completed.\n" +
                "(E)Exit or (C)Continue");
        String exit = sc.nextLine();
        return exit.toUpperCase().trim().equals("E") || exit.toUpperCase().trim().equals("EXIT");
    }

    public static String enterRezultFileName(Scanner sc) {
        boolean b = false;
        String name = "";
        while (!b) {
            System.out.println("Enter the name of the archive/folder. \n" +
                    "The program will place the files/archive in the same directory where the file is located");
            name = sc.nextLine();
            if (!"".equals(name)) {
                b = true;
            }
        }
        return name;
    }

    public static String enterPathSrc(Scanner sc) {
        boolean b = false;
        String pathSrc = "";
        while (!b) {
            System.out.println("Enter the path to the file. \nExample: C:\\test\\MB.mdx");
            pathSrc = sc.nextLine();
            try {
                fileIsExists(pathSrc);
                b = true;
            } catch (FileNotFoundException e) {
                System.err.println("Invalid path : " + pathSrc);
            }
        }
        return pathSrc;
    }

    public static String enterCommand(Scanner sc) {
        boolean b = false;
        String command = "";
        while (!b) {
            System.out.println("Select operations. \nEnter (Z)Zip or (U)UnZip:");
            command = sc.nextLine();
            try {
                Commands.valueOf(command.toUpperCase().trim());
                b = true;
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid command : " + command + ".\n " +
                        "Enter command from the list :" + new ArrayList(Arrays.asList(Commands.values())));
            }
        }
        return command;
    }

}
