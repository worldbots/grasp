package ru.spb.hibissscus.http;


import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Master to make the http connection
 */
public class HttpMaster {

    private static final Logger LOG = LoggerFactory.getLogger(HttpMaster.class);

    /**
     * Default apache http client
     */
    private DefaultHttpClient httpClient;

    /**
     * Proxy to HttpClients
     */
    private HttpHost proxy;


    public HttpMaster() {
        this.httpClient = new ContentEncodingHttpClient(createHttpParams());
        this.httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);

        HttpMethodRetryHandler myretryhandler = new HttpMethodRetryHandler() {
            public boolean retryMethod(
                    final HttpMethod method,
                    final IOException exception,
                    int executionCount) {
                if (executionCount >= 5) {
                    LOG.error("{}", "Do not retry if over max retry count");
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    LOG.error("{}", "Retry if the server dropped connection on us");
                    // Retry if the server dropped connection on us
                    return true;
                }
                if (!method.isRequestSent()) {
                    // Retry if the request has not been sent fully or
                    // if it's OK to retry methods that have been sent
                    LOG.error("{}", "Retry if the request has not been sent fully");
                    return true;
                }
                // otherwise do not retry
                LOG.error("{}", "otherwise do not retry");
                return false;
            }
        };
    }

    private HttpParams createHttpParams() {
        HttpParams params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        // This is realy important to Google translation service
//                HttpProtocolParams.setUserAgent(params,
//                        "Chrome/19.0.1084.46 Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.5 (KHTML, like Gecko) Safari/536.5");
        HttpProtocolParams.setUserAgent(params,
                "Chrome/20.0.1132.47 Opera/9.80 (Windows NT 6.1; U; es-ES) Presto/2.9.181 Version/12.00 Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.5 (KHTML, like Gecko) Safari/536.5");

        return params;
    }

    public void setupProxy(HttpHost httpHost) {
        proxy = httpHost;
        httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
    }


    /**
     * Метод получающий массив байт
     *
     * @param url урл для запроса массива байт (например звуковой дорожки)
     * @return массив байт
     */
    public BufferedHttpEntity getResponseEntity(String url) {
        HttpGet httpget = new HttpGet(url);

        try {
            HttpResponse response = httpClient.execute(httpget);

            HttpEntity entity = response.getEntity();

            return new BufferedHttpEntity(entity);
        } catch (IOException e) {
            LOG.error("{}", e);
            LOG.error("URL: {} request PROBLEM", url);
            return null;
        }
    }


    /**
     * Запрос RESTсервиса
     */
    public void askRestService() {

        //http://localhost:8080/jsfdict/rest/service/count
        URI uri = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            uri = URIUtils.createURI("http", "localhost", 8080, "/jsfdict/rest/service/count", null, null);
            HttpGet httpget = new HttpGet(uri);

            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();

            LOG.info(EntityUtils.toString(entity));

        } catch (URISyntaxException e) {
            e.printStackTrace();
            LOG.error("Exception type: {}. message: {}", e.getClass(), e);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            LOG.error("Exception type: {}. message: {}", e.getClass(), e);
        } catch (IOException e) {
            LOG.error("url: \n" + uri + "\nrequest PROBLEM");
        }
    }


//    /**
//     * Посылаем перевод через RESTсервис
//     * import org.codehaus.jettison.json.JSONObject;
//     */
//    public String restAddTranslation(final LastTranslation lastTranslation) {
//
//        if (lastTranslation != null) {
//            //http://localhost:8080/jsfdict/rest/service/count
//            URI uri = null;
//            DefaultHttpClient httpClient = new DefaultHttpClient();
//            try {
//                uri = URIUtils.createURI("http", "localhost", 8080, "/jsfdict/rest/translation/add", null, null);
//
//
//                HttpPost request = new HttpPost(uri);
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("leftWord", lastTranslation.getFromWord().toLowerCase());
//                params.put("leftLanguage", lastTranslation.getFromLanguage().toString());
//                params.put("rightWord", lastTranslation.getToWord().toLowerCase());
//                params.put("rightLanguage", lastTranslation.getToLanguage().toString());
//
//                JSONObject object = new JSONObject(params);
//                request.setEntity(new StringEntity(object.toString(), "application/json", "UTF-8"));
//                HttpResponse response = httpClient.execute(request);
//
//                LOG.info("/jsfdict/rest/translation/add ->" + response.getStatusLine().toString());
//
//                return "sending new translation: " + response.getStatusLine().toString();
//
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//                LOG.error("Exception type: {}. message: {}", e.getClass(), e);
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//                LOG.error("Exception type: {}. message: {}", e.getClass(), e);
//            } catch (IOException e) {
//                String result = "url: \n" + uri + "\nrequest PROBLEM";
//                LOG.error(result);
//                return result;
//            }
//        }
//
//        return "empty translation! nothing to send";
//    }

}
