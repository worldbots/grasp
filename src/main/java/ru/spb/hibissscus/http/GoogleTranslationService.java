package ru.spb.hibissscus.http;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spb.hibissscus.common.LanguageType;
import ru.spb.hibissscus.common.LastTranslation;
import ru.spb.hibissscus.common.RegexSplitter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Master for Google Translate
 */
public class GoogleTranslationService {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleTranslationService.class);

    /**
     * Max chars which Google be able to translate
     * without API by one request
     */
    private static final int MAXCHARS = 100;

    /**
     * Settings to HttpMaster
     */
    private String scheme;
    private String host;

    /**
     * Master to make the http conaction
     */
    private HttpMaster httpMaster;

    /**
     * Last translation maide by GoogleSrvice
     */
    private LastTranslation lastTranslation;

    /**
     * Proxy
     */
    private HttpHost proxy;

    /**
     * Input inputText not formated
     */
    private String inputText;

    /**
     * Formated input inputText
     */
    private List<String> separatedText;

    /**
     * All translations
     */
    List<String> translations;

    /**
     * All voices
     */
    List<byte[]> voices;



    public GoogleTranslationService() {
        this.scheme = "http";
        this.host = "translate.google.com";
        this.proxy = null;
        this.inputText = null;
        this.separatedText = new ArrayList<String>();
        this.translations = new ArrayList<String>();
        this.voices = new ArrayList<byte[]>();
        this.httpMaster = new HttpMaster();
    }


    public HttpMaster getHttpMaster() {
        return httpMaster;
    }

    public void setHttpMaster(HttpMaster httpMaster) {
        this.httpMaster = httpMaster;
    }

    public void setProxy(HttpHost proxy) {
        this.proxy = proxy;
        setupProxy();
    }

    public void setProxy(String host, int port) {
        this.proxy = new HttpHost(host, port);
        setupProxy();
    }

    public HttpHost getProxy() {
        return proxy;
    }

    /**
     * Set Proxy to the Google Service
     */
    private void setupProxy() {
        getHttpMaster().setupProxy(proxy);
    }

    public List<String> getTranslations() {
        return translations;
    }

    public List<byte[]> getVoices() {
        return voices;
    }


    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
        separeteText();

    }

    /**
     * Editing input inputText to appropriate format for Google Translation
     */
    private void separeteText() {
        this.separatedText = RegexSplitter.splitText(inputText);
    }


    public List<String> getSeparatedText() {
        return separatedText;
    }


    /**
     * Try to make translation and voice
     *
     * @param from language from
     * @param to   target  language
     */
    public void makeTranslationsAndVoices(LanguageType from, LanguageType to) {
        getTranslations().clear();
        getVoices().clear();
        for (String str : getSeparatedText()) {
            getTranslations().add(makeTranslation(str, from, to));
            getVoices().add(makeVoice(str, from));
        }
    }

    /**
     * Try to make translation
     *
     * @param from language from
     * @param to   target  language
     */
    public void makeTranslations(LanguageType from, LanguageType to) {
        getTranslations().clear();
        for (String str : getSeparatedText()) {
            getTranslations().add(makeTranslation(str, from, to));
        }
    }


    /**
     * Try to make only voice to insert inputText
     *
     * @param from language from
     */
    public void makeVoices(LanguageType from) {
        getVoices().clear();
        for (String str : getSeparatedText()) {
            getVoices().add(makeVoice(str, from));
        }
    }

    /**
     * Try to make only voice to insert inputText
     *
     * @param strings strings
     * @param to      target  language
     */
    public void makeTextVoices(List<String> strings, LanguageType to) {
//        List<String> strings =  RegexSplitter.splitText(inputText);
        getVoices().clear();
        for (String str : strings) {
            getVoices().add(makeVoice(str, to));
        }
    }


    /**
     * Get words translation
     */
    public String makeTranslation(String str, LanguageType from, LanguageType to) {

        try {
            String path = "/translate_a/t";
            String languageFrom = from.getShortName();
            String languageTo = to.getShortName();

            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("client", "t"));
            qparams.add(new BasicNameValuePair("text", str));
            qparams.add(new BasicNameValuePair("sl", languageFrom));
            qparams.add(new BasicNameValuePair("tl", languageTo));

            URI uri = URIUtils.createURI(scheme, host, -1, path, URLEncodedUtils.format(qparams, "UTF-8"), null);

            LOG.info("makeTranslation: {}", uri);

            // Response from Google Translation
            BufferedHttpEntity entity = getHttpMaster().getResponseEntity(uri.toString());
            if (entity != null) {
                String s = EntityUtils.toString(entity);
                lastTranslation = buildTranslation(s);
                lastTranslation.setToLanguage(to);

                return lastTranslation.getToWord();
            } else
                return null;


        } catch (URISyntaxException e) {
            e.printStackTrace();
            LOG.error("Exception type: {}. message: {}", e.getClass(),e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LOG.error("Exception type: {}. message: {}", e.getClass(),e);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Exception type: {}. message: {}", e.getClass(),e);
        }

        return null;
    }

    /**
     * Get Voice for word
     * <p/>
     * example:
     * sound("http://translate.google.ru/translate_tts?ie=UTF-8&q=%D1%81%D0%BE%D0%B1%D0%B0%D0%BA%D0%B0&tl=ru&prev=input");
     * byte array "собака"
     */
    public byte[] makeVoice(String str, LanguageType languageType) {
        try {
            String path = "/translate_tts";
            String language = "auto";
            if (languageType != null)
                language = languageType.getShortName();

            List<NameValuePair> qparams = new ArrayList<NameValuePair>();
            qparams.add(new BasicNameValuePair("client", "t"));
            qparams.add(new BasicNameValuePair("q", str));
            qparams.add(new BasicNameValuePair("prev", "input"));
            qparams.add(new BasicNameValuePair("tl", language));

            URI uri = URIUtils.createURI(scheme, host, -1, path, URLEncodedUtils.format(qparams, "UTF-8"), null);

            LOG.info("makeVoice: {}", uri);

            // Response from Google Translation
            BufferedHttpEntity entity = getHttpMaster().getResponseEntity(uri.toString());
            if (entity != null) {
                return EntityUtils.toByteArray(entity);
            } else
                return null;

        } catch (URISyntaxException e) {
            e.printStackTrace();
            LOG.error("Exception type: {}. message: {}", e.getClass(),e);
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Exception type: {}. message: {}", e.getClass(),e);
        }
        return null;
    }


    /**
     * Build translation
     *
     * @param str input text
     * @return LastTranslation with "from" language,
     *         because if from - AUTODETECTED we must understand "from" language
     *         //todo regExp
     */
    private static LastTranslation buildTranslation(String str) {
        //ex. [[["Освобождение связи обратно в связи",
        StringBuilder builder = new StringBuilder("");
        str = str.trim();

        // language definition
        String[] bounds = str.split("\\[\\[\"");
        String language = bounds[bounds.length - 1].substring(0, 2);

        // try to define language
        LanguageType from = LanguageType.byShortName(language);
       if(from != null) {
           LOG.debug("Language [{}] str: {}",from.getShortName(),str);
       } else {  // if we did not find set up AUTODETECTED
           LOG.debug("Language had not been determined [{}] str: {}",language,str);
           from = LanguageType.AUTODETECTED;
           from.setSname(language);
       }

        // finde translation
        String[] wordArray = str.split("\"");
        if (builder.toString().length() < 1) builder.append(wordArray[1]);

        return new LastTranslation(str, from, null, builder.toString());
    }




    /**
     * Получить последний перевод
     */
    public LastTranslation getLastTranslation() {
        return lastTranslation;
    }


}
