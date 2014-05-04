package ru.spb.hibissscus.httpconnector;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.ContentEncodingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.params.*;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class HttpConnector {

    private static final Logger LOG = LoggerFactory.getLogger(HttpConnector.class);



    private static HttpMethod httpMethod;

    private static int result;
    private static DefaultHttpClient httpClient;
    private static final String DOMAIN = "hlpdesk.corbina.ru";
    private static final String SITE_ROOT = "http://" + DOMAIN + "/";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String SITE_CHARSET = "CP1251";

    public static final String URL_WEB = "https://rosreestr.ru/wps/portal/p/cc_ib_ros_reestr/cc_ib_contact/cc_ib_office/!ut/p/c5/lc_LCsIwFATQb_EL7kSNcZv6SBO1rWjQZiNVpCh9uBDBvzd1J1gf9y4PAzPkyH-V3U55dj3VVVbQltxgx_lipGeKQU0soJOhEGtuoALuPW33qfgrHVseeF_G0hgwAL-ktTJJ-PTl3PtovGKJFEDyLW3I5UW99xs3zerPK178Tc_GPzVpHC0nQVFYl0dKyYkdGJcs7EPHfNCDVBMWBlHcBRilxTHPDne6lNZu7yt97tvOA9mxlFs!/dl3/d3/L0lJSklna21DU1EhIS9JRGpBQUN5QUJFUkNKRXFnLzRGR2dzbzBWdnphOUlBOW9JQSEhLzdfMDE1QTFINDBJTzU2MzBBR0UxSEJOTzIwMDEvb0N2RUc0NzY3MDE1Ni9zYS5ydS5mY2NsYW5kLmlibXBvcnRhbC5zcHJpbmcucG9ydGxldC5kaXNwYXRjaGVyLkRpc3BhdGNoZXJBY3Rpb25FdmVudC1NVUxUSVBBUlQ!/?PC_7_015A1H40IO5630AGE1HBNO2001000000_ru.fccland.ibmportal.spring.portlet.handler.BeanNameParameterHandlerMapping-PATH=%2fOfficeTimetableFormController&PC_7_015A1H40IO5630AGE1HBNO2001000000_office_id=14129";
    public static final String POST = "https://rosreestr.ru/wps/portal/p/cc_ib_ros_reestr/cc_ib_contact/cc_ib_office/!ut/p/c5/04_SB8K8xLLM9MSSzPy8xBz9CP0os3hTU19nT293QwN311ADA88AC3PzEFMvA3cnU6B8JG55N3OSdPuHmjoB5QP9Hb28DAwNDAyI0e3p7hXgAZYP9AHKO7sEGwY4mhsYBBDS7aUflZ6TnwT0YzjI1_h9gSKPxZ0geXwuAckb4ACOBvp-Hvm5qfoFuaGhEZXBnlkmjooAhPACIg!!/dl3/d3/L0lDU0lKSWdrbUNTUS9JUFJBQUlpQ2dBek15cXpHWUEhIS80QkVqOG8wRmxHaXQtYlhwQUh0Qi83XzAxNUExSDQwSU81NjMwQUdFMUhCTk8yMDAxLzB4SXNXNDAzODAwNTMvc2EucnUuZmNjbGFuZC5pYm1wb3J0YWwuc3ByaW5nLnBvcnRsZXQuZGlzcGF0Y2hlci5EaXNwYXRjaGVyQWN0aW9uRXZlbnQtTVVMVElQQVJU/?PC_7_015A1H40IO5630AGE1HBNO2001000000_ru.fccland.ibmportal.spring.portlet.handler.BeanNameParameterHandlerMapping-PATH=%2fOfficeTimetableFormController";

    private static HttpParams createHttpParams() {
        HttpParams params = new SyncBasicHttpParams();
        HttpProtocolParams.setVersion(params, org.apache.http.HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpProtocolParams.setUserAgent(params,
                "Chrome/20.0.1132.47 Opera/9.80 (Windows NT 6.1; U; es-ES) Presto/2.9.181 Version/12.00 Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.5 (KHTML, like Gecko) Safari/536.5");

        return params;
    }

    public static BufferedHttpEntity getResponseEntity(String url) {
        HttpGet httpget = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();

            return new BufferedHttpEntity(entity);
        } catch (IOException e) {
            LOG.error("URL_WEB: {} request PROBLEM", e, url);
            return null;
        }
    }


    public static BufferedHttpEntity postResponseEntity(String url) {
        HttpPost httppost = new HttpPost(url);

        try {
            HttpResponse response = httpClient.execute(httppost);
            HttpEntity entity = response.getEntity();

            return new BufferedHttpEntity(entity);
        } catch (IOException e) {
            LOG.error("URL_WEB: {} request PROBLEM", e, url);
            return null;
        }
    }

    public static final String U = "http://worldoftanks.ru/community/accounts/";
    public static final String U1 = "http://worldoftanks.ru/community/accounts/#wot&at_search=tinturion";


    public static void main(String[] args) throws IOException {
        json();
    }

    private static void json() throws IOException {
        httpClient = new DefaultHttpClient();
        BufferedHttpEntity entityGet = getResponseEntity("http://api.worldoftanks.ru/2.0/account/list/?application_id=171745d21f7f98fd8878771da1000a31&search=123");

        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(EntityUtils.toString(entityGet));
            String status = (String) jsonObject.get("status");
            LOG.info("status: " + status);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void async() throws IOException, InterruptedException, ExecutionException {
        httpClient = new ContentEncodingHttpClient(createHttpParams());
        httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
        httpClient.getParams().setParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE,10000);


        BufferedHttpEntity entityGet = getResponseEntity(U);
        BufferedHttpEntity entityGet1 = getResponseEntity(U1);
        String s = EntityUtils.toString(entityGet1);
        //LOG.info(s);


        Document doc = Jsoup.parse(s);
        Element tableContainer = doc.getElementById("account_table_container");
        Elements links = tableContainer.select("a[href~=/community/accounts/*]"); // a with href
        LOG.info(links.toString());


        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        try {
            // Start the client
            httpclient.start();

            // Execute request
            final HttpGet request1 = new HttpGet(U);
            Future<HttpResponse> future = httpclient.execute(request1, null);
            // and wait until a response is received
            HttpResponse response1 = future.get();
            System.out.println(request1.getRequestLine() + "->" + response1.getStatusLine());

            // One most likely would want to use a callback for operation result
            final CountDownLatch latch1 = new CountDownLatch(1);
            final HttpGet request2 = new HttpGet(U1);
            httpclient.execute(request2, new FutureCallback<HttpResponse>() {

                public void completed(final HttpResponse response2) {
                    latch1.countDown();
                    try {
                        String s = EntityUtils.toString(response2.getEntity());
                        Document doc = Jsoup.parse(s);
                        Element tableContainer = doc.getElementById("account_table_container");
                        Elements links = tableContainer.select("a[href~=/community/accounts/*]"); // a with href
                        System.out.println("links:"+links);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    System.out.println(request2.getRequestLine() + "->" + response2.getStatusLine());
                }

                public void failed(final Exception ex) {
                    latch1.countDown();
                    System.out.println(request2.getRequestLine() + "->" + ex);
                }

                public void cancelled() {
                    latch1.countDown();
                    System.out.println(request2.getRequestLine() + " cancelled");
                }

            });
            latch1.await();

            // In real world one most likely would also want to stream
            // request and response body content
            final CountDownLatch latch2 = new CountDownLatch(1);
            final HttpGet request3 = new HttpGet(U1);
            HttpAsyncRequestProducer producer3 = HttpAsyncMethods.create(request3);
            AsyncCharConsumer<HttpResponse> consumer3 = new AsyncCharConsumer<HttpResponse>() {

                HttpResponse response;

                @Override
                protected void onResponseReceived(final HttpResponse response) {
                    this.response = response;
                }

                @Override
                protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
                    // Do something useful
                }

                @Override
                protected void releaseResources() {
                }

                @Override
                protected HttpResponse buildResult(final HttpContext context) {
                    return this.response;
                }

            };
            httpclient.execute(producer3, consumer3, new FutureCallback<HttpResponse>() {

                public void completed(final HttpResponse response3) {
                    latch2.countDown();
                    System.out.println(request2.getRequestLine() + "->" + response3.getStatusLine());
                }

                public void failed(final Exception ex) {
                    latch2.countDown();
                    System.out.println(request2.getRequestLine() + "->" + ex);
                }

                public void cancelled() {
                    latch2.countDown();
                    System.out.println(request2.getRequestLine() + " cancelled");
                }

            });
            latch2.await();

        } finally {
            httpclient.close();
        }
    }



    private static void silenium() throws IOException {
        httpClient = new ContentEncodingHttpClient(createHttpParams());
        httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8);
        httpClient.getParams().setParameter(CoreProtocolPNames.WAIT_FOR_CONTINUE,10000);


        BufferedHttpEntity entityGet = getResponseEntity(U);
        String s = EntityUtils.toString(entityGet);

//        WebDriver driver = new FirefoxDriver();
        HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("http://worldoftanks.ru/community/accounts/#wot&at_search=tinturion");


        Document doc = Jsoup.parse(driver.getPageSource());
        Element tableContainer = doc.getElementById("account_table_container");
        Elements links = tableContainer.select("a[href~=/community/accounts/*]"); // a with href
        LOG.info(links.toString());


//        HtmlUnitDriver driver = new HtmlUnitDriver(true);
//        WebDriver driver = new RemoteWebDriver(
//                new URL("http://localhost:4444/wd/hub"),
//                DesiredCapabilities.firefox());
//
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        driver.get("http://worldoftanks.ru/community/accounts/#wot&at_search=tinturion");

        // RemoteWebDriver does not implement the TakesScreenshot class
        // if the driver does have the Capabilities to take a screenshot
        // then Augmenter will add the TakesScreenshot methods to the instance
//        WebDriver augmentedDriver = new Augmenter().augment(driver);
//        File screenshot = ((TakesScreenshot)driver).
//                getScreenshotAs(OutputType.FILE);
        driver.close();
    }


//    public static void main(String[] args) throws UnsupportedEncodingException {
//        httpClientDefault();
//
//        // Загрузка страницы с запросом методом POST
//        PostMethod postMethod = new PostMethod(SITE_ROOT + "/login.pl");
//        postMethod.addParameter("login", "balakin_a");
//        postMethod.addParameter("name", "1q2w3e4r5t");
//        postMethod.getParams().setContentCharset(SITE_CHARSET);
//        try {
//            int result = httpClient.executeMethod(postMethod);
//            if (result == HttpStatus.SC_OK) {
//                // Выводим страницу на экран
//                printStream(postMethod.getResponseBodyAsStream());
//                // postMethod.getParams();
//                // getMethod(SITE_ROOT + "/shedule_nconnect.pl");
//                Cookie[] cookies = httpClient.getState().getCookies();
//                // Display the cookies
//                System.out.println("Present cookies: ");
//                for (int i = 0; i < cookies.length; i++) {
//                    System.out.println(" - " + cookies[i].toExternalForm());
//                }
//
//                // Загрузка страницы с запросом методом GET
//                GetMethod getMethod = new GetMethod(SITE_ROOT + "/shedule_nconnect.pl");//+ "index.html?cat=1&amp;search=" + URLEncoder.encode("Доктор Хаус", SITE_CHARSET));
//                getMethod.getParams().setContentCharset(SITE_CHARSET);
//                try {
//                    result = httpClient.executeMethod(getMethod);
//                    if (result == HttpStatus.SC_OK) {
//                        // Выводим страницу на экран
//                        System.out.println(getMethod.getResponseBodyAsString());
//                    } else {
//                        System.out.println("А страничка-то и не загрузилась!!!");
//                        return;
//                    }
//                } catch (IOException e) {
//                    System.out.println("Проблемы со связью");
//                    return;
//                } finally {
//                    getMethod.releaseConnection();
//                }
//
//
//            } else {
//                System.out.println("А страничка-то и не загрузилась!!!");
//                return;
//            }
//        } catch (IOException e) {
//            System.out.println("Проблемы со связью");
//            return;
//        } finally {
//
//            postMethod.releaseConnection();
//        }
//    }


    /**
     * Метод выполняющий запрос POST на заданный url
     *
     * @param url - заданный URL_WEB адрес
     */
    private static boolean login(String url, String name, String password) {
        PostMethod postMethod = new PostMethod(url);
        postMethod.addParameter("login", "balakin_a");
        postMethod.addParameter("password", "1q2w3e4r5t");
        postMethod.getParams().setContentCharset(SITE_CHARSET);
        postMethod.setFollowRedirects(true);
        try {

//            result = httpClient.executeMethod(postMethod);

            if (result == HttpStatus.SC_OK) {
                // Выводим страницу на экран
                System.out.println("А страничка-то загрузилась!!!");
                printStream(postMethod.getResponseBodyAsStream());
                return true;
            } else {
                System.out.println("А страничка-то и НЕ загрузилась!!!");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Проблемы со связью");
            return false;
        } finally {
            postMethod.releaseConnection();
        }
    }


    /**
     * Метод выполняющий запрос GET на заданный url
     *
     * @param url - заданный URL_WEB адрес
     */
    private static boolean getMethod(String url) {
        httpMethod = new GetMethod(url);
//        httpMethod.getParams().setContentCharset(SITE_CHARSET);
        httpMethod.setFollowRedirects(true);
        try {

//            result = httpClient.executeMethod(httpMethod);

            if (result == HttpStatus.SC_OK) {
                // Выводим страницу на экран
                System.out.println("А страничка-то загрузилась!!!");
                printStream(httpMethod.getResponseBodyAsStream());
                return true;
            } else {
                System.out.println("А страничка-то и НЕ загрузилась!!!");
                return false;
            }
        } catch (IOException e) {
            System.out.println("Проблемы со связью");
            return false;
        } finally {
            httpMethod.releaseConnection();
        }
    }

    private static void printStream(InputStream res) throws IOException {
        InputStreamReader reader = new InputStreamReader(res, SITE_CHARSET);
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        int am;
        char[] buffer = new char[4096];
        while ((am = reader.read(buffer)) != -1)
            writer.write(buffer, 0, am);
    }
}



