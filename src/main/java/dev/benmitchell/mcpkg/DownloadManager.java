package dev.benmitchell.mcpkg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class DownloadManager {
    public static void downloadToFile(URL source, File destination, boolean append)
            throws IOException, FileNotFoundException {
        try (InputStream iStream = source.openStream()) {
            try (OutputStream oStream = new FileOutputStream(destination, append)) {
                iStream.transferTo(oStream);
            } // oStream
        } // iStream
    }
}
