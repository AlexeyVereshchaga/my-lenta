package com.gmail.avereshchaga.mylenta.utils;

import android.util.Log;

import com.gmail.avereshchaga.mylenta.model.News;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Alex on 15.03.2015.
 */
public class Utils {
    public static final String URL_LENTA_RU = "http://81.19.76.10:80/rss";
    public static final String URL_GAZETA_RU = "http://www.gazeta.ru/export/rss/lenta.xml";
    public static final String TAG = "MyLenta";
    public static final int NUMBER_OF_NEWS = 200;
    public static final String KEY_SAVE_LIST = "com.gmail.avereshchaga.mylenta_save";
    public static final String KEY_LIST = "com.gmail.avereshchaga.LIST";

    private final static String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";

    private enum RssXmlTag {
        SOURCE, TITLE, DATE, DESCRIPTION, IMAGE, AUTHOR, IGNORE_TAG;
    }

    public static List<News> parseRssData(String rssData) {
        List<News> listRssNews = new ArrayList<>();
        RssXmlTag currentTag = null;
        try {
            InputStream is = new ByteArrayInputStream(rssData.getBytes());
            // parse xml
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(is, "UTF-8");

            int eventType = xpp.getEventType();
            News postNews = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {

                } else if (eventType == XmlPullParser.START_TAG) {
                    Log.w("warming", xpp.getName());
                    if (xpp.getName().equals("item")) {
                        postNews = new News();
                        currentTag = RssXmlTag.IGNORE_TAG;
                    } else if (xpp.getName().equals("title")) {
                        currentTag = RssXmlTag.TITLE;
                    } else if (xpp.getName().equals("pubDate")) {
                        currentTag = RssXmlTag.DATE;
                    } else if (xpp.getName().equals("author")) {
                        currentTag = RssXmlTag.AUTHOR;
                    } else if (xpp.getName().equals("description")) {
                        currentTag = RssXmlTag.DESCRIPTION;
                    } else if (xpp.getName().equals("enclosure")) {
                        String imageUrl = xpp.getAttributeValue(0);
                        postNews.setEnclosure(imageUrl);
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equals("item")) {

                        listRssNews.add(postNews);
                    } else {
                        currentTag = RssXmlTag.IGNORE_TAG;
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    String content = xpp.getText();
                    content = content.trim();
                    Log.d("debug", content);
                    if (postNews != null) {
                        switch (currentTag) {
                            case TITLE:
                                if (content.length() != 0) {
                                    postNews.setTitle(content);
                                }
                                break;
                            case DESCRIPTION:
                                if (content.length() != 0) {
                                    postNews.setDescription(content);
                                }
                                break;
                            case DATE:
                                postNews.setPubDate(content);
                                break;
                            case AUTHOR:
                                if (content.length() != 0) {
                                    postNews.setAuthor(content);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "", e);
        } catch (IOException e) {
            Log.e(TAG, "", e);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "", e);
        }
        return listRssNews;
    }

    public static Date parseDate(String dateStr) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, "", e);
        }
        return date;
    }

    public static String getXmlFromUrl(String url) {
        String xml = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String encoding = url.contains("gazeta.ru") ? "windows-1251" : "UTF-8";
            xml = EntityUtils.toString(httpEntity, encoding);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xml;
    }

    public static <T extends Comparable<T>> void sortAndTrimList(List<T> originalList, List<T> addedList) {
        originalList.addAll(addedList);
        Set<T> set = new HashSet<>(originalList);
        originalList.clear();
        originalList.addAll(set);
        Collections.sort(originalList);
        if (NUMBER_OF_NEWS < originalList.size()) {
            originalList.subList(NUMBER_OF_NEWS, originalList.size()).clear();
        }
    }

}
