package org.apache.commons.compress.archivers.zip;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public class TestZipArchiveOutputStream {


    public static void zip(File dir, OutputStream outputStream) throws IOException, ArchiveException {
        ZipArchiveOutputStream zipOutput = null;
        try {
            zipOutput = (ZipArchiveOutputStream) new ArchiveStreamFactory()
                    .createArchiveOutputStream(ArchiveStreamFactory.ZIP, outputStream);
            zipOutput.setEncoding("utf-8");
            if(zipOutput.getEncoding()!="utf-8")
            {
                System.err.println("Error Encoding");
            }
            zipOutput.setUseZip64(Zip64Mode.AsNeeded);
            Collection<File> files = FileUtils.listFilesAndDirs(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

            for (File file : files) {
                InputStream in = null;
                try {
                    if (file.getPath().equals(dir.getPath())) {
                        continue;
                    }
                    String relativePath = StringUtils.replace(file.getPath(), dir.getPath() + File.separator, "");
                    ZipArchiveEntry entry = new ZipArchiveEntry(file, relativePath);
                    zipOutput.putArchiveEntry(entry);
                    if (entry.getSize()!=5) {
                        System.err.println("The putArchiveEntry Size is wrong");
                    }
                    if (file.isDirectory()) {
                        continue;
                    }

                    in = new FileInputStream(file);
                    IOUtils.copy(in, zipOutput);
                    zipOutput.closeArchiveEntry();
                } finally {
                    if (in != null) {
                        IOUtils.closeQuietly(in);
                    }
                }
            }

            zipOutput.finish();
            if(zipOutput.finished!=true)
            {
                System.err.println("This archive has not been finished");
            }
        } finally {
            IOUtils.closeQuietly(zipOutput);
        }
    }


    public static void main(String args[]) throws IOException, ArchiveException {
        //要压缩的文件路径
        File dir = new File("C:\\data\\JunitTest");
        //压缩后zip包文件路径
        File dest = new File("C:\\data\\test.zip");
        OutputStream outputStream = new FileOutputStream(dest);
        zip(dir, outputStream);
    }
}