package zipconcurrent;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipOperations implements Runnable {

    private static final int FILE_BUFFER_SIZE = 1024;

    final private Boolean isArchiving;
    final private String pathSrc;
    final private String nameResultFile;

    public ZipOperations(Boolean isArchiving, String pathSrc, String nameRezultFile) {
        this.isArchiving = isArchiving;
        this.pathSrc = pathSrc;
        this.nameResultFile = nameRezultFile;

    }

    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " Start. Is archiving : " + isArchiving);
            doAction(isArchiving, pathSrc, nameResultFile);
        } catch (Exception e) {
             e.printStackTrace();
        }
    }

    private void doAction(Boolean isArchiving, String pathSrc, String nameRezultFile) throws Exception {
        String basePackage = getBasePackage(pathSrc);
        String pathDest = basePackage + File.separator + nameRezultFile;

        if (isArchiving){
            zipFile(basePackage, pathSrc, pathDest);
        }else if(!isArchiving) {
            unZipFile(pathSrc, pathDest);
        }
    }


    public String getBasePackage(String fullPathToFile) {
        int index = fullPathToFile.lastIndexOf("\\");
        return fullPathToFile.substring(0, index);
    }

    private void zipFile(String basePackage, String sourceDir, String zipFile) throws IOException {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile + ".zip"))) {
            File file = new File(sourceDir);
            if (file.isFile()) {
                singleFileToZip(basePackage, file, zout);
            } else {
                dirToZip(basePackage, file, zout);
            }
        }
    }

    /**
     * This method compresses the single file to zip format
     */
    private boolean singleFileToZip(String basePackage, File file, ZipOutputStream out) throws IOException {
        ZipEntry entry;

        byte[] buffer = new byte[FILE_BUFFER_SIZE];
        int bytes_read;
        try(FileInputStream in = new FileInputStream(file)) {

            entry = new ZipEntry(file.getName());
            out.putNextEntry(entry);

            while ((bytes_read = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytes_read);
            }
            out.closeEntry();
        } catch (IOException e) {
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
//  27.11.2016 добавить лог
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

    public void unZipFile(String fileName, String unZipDir) throws Exception {
        File f = new File(unZipDir);

        if (!f.exists()) {
            f.mkdirs();
        }

        BufferedInputStream is = null;
        ZipEntry entry;
        ZipFile zipfile = new ZipFile(fileName);
        Enumeration<?> enumeration = zipfile.entries();
        byte data[] = new byte[FILE_BUFFER_SIZE];


        while (enumeration.hasMoreElements()) {
            entry = (ZipEntry) enumeration.nextElement();

            if (entry.isDirectory()) {
                File f1 = new File(unZipDir + "/" + entry.getName());

                if (!f1.exists()) {
                    f1.mkdirs();
                }
            } else {
                is = new BufferedInputStream(zipfile.getInputStream(entry));
                int count;
                String name = unZipDir + "/" + entry.getName();
                RandomAccessFile m_randFile = null;
                File file = new File(name);
                if (file.exists()) {
                    file.delete();
                }

                file.createNewFile();
                m_randFile = new RandomAccessFile(file, "rw");
                int begin = 0;

                while ((count = is.read(data, 0, FILE_BUFFER_SIZE)) != -1) {
                    try {
                        m_randFile.seek(begin);
                    } catch (Exception ex) {

                    }

                    m_randFile.write(data, 0, count);
                    begin = begin + count;
                }

                file.delete();
                m_randFile.close();
                is.close();
            }
        }
    }


}

