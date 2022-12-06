package org.apache.commons.compress.archivers.zip;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.util.Collection;
import static org.junit.Assert.*;

public class ZipArchiveOutputStreamTest {
    private ZipArchiveOutputStream zipOutput;
    private ZipArchiveEntry entry;
    @Before
    public void setUp() throws Exception {
        File dir = new File("C:\\data\\JunitTest");
        //压缩后zip包文件路径
        File dest = new File("C:\\data\\test.zip");
        OutputStream outputStream = new FileOutputStream(dest);
        zipOutput = null;
        try {
            zipOutput = (ZipArchiveOutputStream) new ArchiveStreamFactory()
                    .createArchiveOutputStream(ArchiveStreamFactory.ZIP, outputStream);
            zipOutput.setEncoding("utf-8");
            zipOutput.setUseZip64(Zip64Mode.AsNeeded);
            Collection<File> files = FileUtils.listFilesAndDirs(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

            for (File file : files) {
                InputStream in = null;
                try {
                    if (file.getPath().equals(dir.getPath())) {
                        continue;
                    }
                    String relativePath = StringUtils.replace(file.getPath(), dir.getPath() + File.separator, "");
                    entry = new ZipArchiveEntry(file, relativePath);
                    zipOutput.putArchiveEntry(entry);
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
        } finally {
            IOUtils.closeQuietly(zipOutput);
        }
    }
    @Test
    public void setEncoding() {
        assertEquals(zipOutput.getEncoding(),"utf-8");
    }
    @Test
    public void finish() {
        assertEquals(zipOutput.finished,true);
    }
    @Test
    public void putArchiveEntry() {
        assertEquals(entry.getSize(),6);
    }


}


