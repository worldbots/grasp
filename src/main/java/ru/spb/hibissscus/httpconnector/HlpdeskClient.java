package ru.spb.hibissscus.httpconnector;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HlpdeskClient {
    static final String SITE = "http://hlpdesk.corbina.ru";
    static final String LOGON_SITE = "hibiscus.appspot.com";
    static final int LOGON_PORT = 80;
    static String PROTOCOL = "http";
    static boolean VALID = false;

    public static void main(String[] args) throws UnsupportedEncodingException, HldError {
        new HlpdeskClient();
        System.out.println("HlpdeskClient");
    }

    private static HttpClient instance;

    public static HttpClient getInstance() {
        if (instance == null) {
            instance = new HttpClient();
        }

        return instance;
    }


    public static String getMethodExec(String url) {
        String result = null;
        GetMethod getMethod = new GetMethod(SITE + url);
        try {
            int resultFlag = HlpdeskClient.getInstance().executeMethod(getMethod);
            result = getMethod.getResponseBodyAsString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Закрыть в любом случае
            getMethod.releaseConnection();
        }

        return result;
    }

    /**
     * Проверяем прошли ли мы регистрацию
     *
     * @param body тело html докуменнта
     * @return прошли ли проверку
     */
    public static boolean checkLogin(String body) {
        // Проверяем прошли ли проверку (авторизацаю)
        Document doc = Jsoup.parse(body);
        Elements log = doc.getElementsMatchingOwnText("Забыли пароль?");
        //System.out.println(authpost.getResponseBodyAsString());
        return log.size() == 0;
    }


    /** Класс ошибок для сеанса с хелпдеском */
    public static class HldError extends Exception {
        public HldError(String message) {
            super(message);
        }
    }


    public static boolean login(String login, String password) throws HldError {

        PostMethod authpost = new PostMethod("http://hlpdesk.corbina.ru/login.pl") {
            @Override
            public boolean getFollowRedirects() {
                return true;
            }
        };

        try {
            HlpdeskClient.getInstance().getHostConfiguration().setHost(LOGON_SITE, LOGON_PORT, PROTOCOL);
            HlpdeskClient.getInstance().getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            HlpdeskClient.getInstance().getHttpConnectionManager().getParams().setConnectionTimeout(4000);

            authpost.setParameter("login", login);
            authpost.setParameter("password", password);

            int result = HlpdeskClient.getInstance().executeMethod(authpost);
            return result == HttpStatus.SC_OK && checkLogin(authpost.getResponseBodyAsString());

        } catch (Exception e) {
            throw new HldError("Проблемы со связью, возможно не установлено VPN соединение.");
        } finally {
            // закрыть в любом случае
            authpost.releaseConnection();
        }
    }


    public static boolean checkPermission() throws HldError {

        GetMethod getPermission = new GetMethod("http://hibiscus.appspot.com/robot.html") {
            @Override
            public boolean getFollowRedirects() {
                return true;
            }
        };

        try {
            HlpdeskClient.getInstance().getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            HlpdeskClient.getInstance().getHttpConnectionManager().getParams().setConnectionTimeout(4000);

            int result = HlpdeskClient.getInstance().executeMethod(getPermission);
            return result == HttpStatus.SC_OK && checkPermission(getPermission.getResponseBodyAsString());

        } catch (Exception e) {
            throw new HldError("Проблемы со связью"+e.getMessage());
        } finally {
            // закрыть в любом случае
            getPermission.releaseConnection();
        }
    }


    /**
     * Проверяем дозволино ли нам заходить
     *
     * @param body тело html докуменнта
     * @return прошли ли проверку
     */
    public static boolean checkPermission(String body) throws HldError {
        boolean result = false;
        // Проверяем прошли ли проверку
        Document doc = Jsoup.parse(body);
        Elements inputs = doc.getElementsByTag("input");

        if (inputs.size() == 2) {
            String text = inputs.get(0).val();
            String key = inputs.get(1).val();
            if (key.equals("1")) result = true;
            else throw new HldError(text);
        }
        return result;
    }


    public HlpdeskClient() throws HldError {
//        try {
//            if (login("", ")) VALID = true ;
//            else System.out.println("Не прошла аутентификация");
//        } catch (Exception e) {
//            e.printStackTrace();
//            VALID = false;
//
//        }
//        String result = getMethodExec("/comments.pl?ticket_id=17275091");
//         FileUtils.saveResponseBodyToFile(result);
//        if (!checkLogin(result)) System.out.println("Не прошла аутентификация");


//        GetMethod getMethod = new GetMethod("http://hlpdesk.corbina.ru/shedule_nconnect.pl?area=348378&day=2011-05-07&step=3&ods=0");
//        //GetMethod getMethod = new GetMethod("http://hlpdesk.corbina.ru/shedule_nconnect.pl?area=37919");
//        //  GetMethod getMethod = new GetMethod("http://hlpdesk.corbina.ru/tickets2.pl?queue_id=1");
//        try {
//            HlpdeskClient.getInstance().executeMethod(getMethod);
//            System.out.println(getMethod.getResponseBodyAsString());
//            FileUtils.saveResponseBodyToFile(getMethod.getResponseBodyAsString());
////
////           BidsTableParser.parsing21(getMethod.getResponseBodyAsStream());
////           parse(file, "windows-1251", "http://hlpdesk.corbina.ru");
//
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

    }

}