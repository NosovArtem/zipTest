package zipconcurrent;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipOperations implements Runnable {

    private static final Logger log = Logger.getLogger(ZipOperations.class);

    private static final int FILE_BUFFER_SIZE = 1024;

    final private String command;
    final private String pathSrc;
    final private String basePackage;
    final private String pathDest;


    public ZipOperations(String command, String pathSrc, String nameRezultFile) throws FileNotFoundException {
        this.command = command;
        this.pathSrc = pathSrc;
        this.basePackage = getBasePackage(pathSrc);
        this.pathDest = basePackage + File.separator + nameRezultFile;
    }

    @Override
    public void run() {
        try {
            log.info(Thread.currentThread().getName() + " Start. Is archiving : " + command);
            Commands.valueOf(command.toUpperCase().trim()).command(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getBasePackage(String fullPathToFile) throws FileNotFoundException {
        fileIsExists();
        int index = fullPathToFile.lastIndexOf("\\");
        return fullPathToFile.substring(0, index);
    }

    public void fileIsExists() throws FileNotFoundException {
        File file = new File(pathSrc);

        if (!file.exists()) {
            throw new FileNotFoundException("Invalid path to the file. pathSrc:" + pathSrc);
        }
    }

    public boolean zipFile() throws IOException {
        fileIsExists();
        boolean zipResult;
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(pathDest + ".zip"))) {
            File file = new File(pathSrc);

            if (file.isFile()) {
                zipResult = singleFileToZip(basePackage, file, zout);
            } else {
                zipResult = dirToZip(basePackage, file, zout);
            }
        }
        log.info("Archiving of file: " + pathSrc + "is completed. Archive: " + pathDest + ".zip");
        return zipResult;
    }

    /**
     * This method compresses the single file to zip format
     */
    private boolean singleFileToZip(String basePackage, File file, ZipOutputStream out) throws IOException {
        ZipEntry entry;

        byte[] buffer = new byte[FILE_BUFFER_SIZE];
        int bytes_read;
        try (FileInputStream in = new FileInputStream(file)) {

            entry = new ZipEntry(file.getName());
            out.putNextEntry(entry);

            while ((bytes_read = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes_read);
            }
            out.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                out.closeEntry();
            }
        }
        return true;
    }

    private boolean dirToZip(String baseDirPath, File dir, ZipOutputStream out) throws IOException {
        if (!dir.isDirectory()) {
            return false;
        }

        File[] files = dir.listFiles();
        if (files.length == 0) {
            ZipEntry entry = new ZipEntry(dir.getName());

            try {
                out.putNextEntry(entry);
                out.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                singleFileToZip(baseDirPath, files[i], out);
            } else {
                dirToZip(baseDirPath, files[i], out);
            }
        }
        return true;
    }

    public boolean unZipFile() throws Exception {
        fileIsExists();
        File f = new File(pathDest);

        if (!f.exists()) {
            f.mkdirs();
        }

        ZipEntry entry;
        ZipFile zipfile = new ZipFile(pathSrc);
        Enumeration<?> enumeration = zipfile.entries();
        byte data[] = new byte[FILE_BUFFER_SIZE];
        log.info("pathDest: " + pathDest);

        while (enumeration.hasMoreElements()) {
            entry = (ZipEntry) enumeration.nextElement();

            if (entry.isDirectory()) {
                File f1 = new File(pathDest + "/" + entry.getName());

                if (!f1.exists()) {
                    f1.mkdirs();
                }
            } else {
                try (BufferedInputStream is = new BufferedInputStream(zipfile.getInputStream(entry))) {
                    int count;
                    String name = pathDest + "/" + entry.getName();

                    File file = new File(name);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();

                    int begin = 0;

                    while ((count = is.read(data, 0, FILE_BUFFER_SIZE)) != -1) {
                        try (RandomAccessFile m_randFile = new RandomAccessFile(file, "rw")) {
                            m_randFile.seek(begin);
                            m_randFile.write(data, 0, count);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        begin = begin + count;
                    }
                    file.delete();
                }
            }
        }
        log.info("Archive successfully decompressed. Folder: " + pathDest);
        return true;
    }


}

