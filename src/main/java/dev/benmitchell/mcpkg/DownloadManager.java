package dev.benmitchell.mcpkg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class DownloadManager {
    private static HttpClient httpClient = HttpClients.createDefault();

    public static String postRequest(URL url, Map<String, String> requestContent)
            throws IOException, URISyntaxException {
        HttpPost httpPost = new HttpPost(url.toURI());

        List<NameValuePair> nvpList = new ArrayList<NameValuePair>(requestContent.size());
        for (var entryPair : requestContent.entrySet()) {
            nvpList.add(new BasicNameValuePair(entryPair.getKey(), entryPair.getValue()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvpList, "UTF-8"));

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        if (entity != null)
            try (InputStream iStream = entity.getContent()) {
                return IOUtils.toString(iStream, StandardCharsets.UTF_8.name());
            }

        throw new RuntimeException("entity was null");
    }

    public static void downloadToFile(URL source, File destination, boolean append)
            throws IOException, FileNotFoundException {
        try (InputStream iStream = source.openStream()) {
            try (OutputStream oStream = new FileOutputStream(destination, append)) {
                iStream.transferTo(oStream);
            } // oStream
        } // iStream
    }
}
