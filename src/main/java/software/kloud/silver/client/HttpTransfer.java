package software.kloud.silver.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class HttpTransfer {
    private final CloseableHttpClient httpClient;
    private final String url;
    private final String apiKey;

    public HttpTransfer(String serviceUrl, String apiKey) {
        this.apiKey = apiKey;
        httpClient = HttpClients.createDefault();
        this.url = serviceUrl;
    }

    private String getString(InputStream in) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, Charset.defaultCharset());
        return writer.toString();
    }

    public String post(String content) throws IOException {
        var httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(content, ContentType.APPLICATION_JSON));
        //TODO Handle this?

        httpPost.addHeader("Authorization", "Bearer " + apiKey);
        var resp = httpClient.execute(httpPost);
        return getString(resp.getEntity().getContent());

    }

    public String get(String key, String clazz) throws IOException {
        var get = new HttpGet(url + "?key=" + key + "&clazz=" + clazz);
        get.addHeader("Authorization", "Bearer " + apiKey);
        var resp = httpClient.execute(get);
        return getString(resp.getEntity().getContent());
    }
}
