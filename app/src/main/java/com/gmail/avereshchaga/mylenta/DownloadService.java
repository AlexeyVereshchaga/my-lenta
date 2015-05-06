package com.gmail.avereshchaga.mylenta;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.gmail.avereshchaga.mylenta.model.News;
import com.gmail.avereshchaga.mylenta.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class DownloadService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_DOWNLOAD = "com.gmail.avereshchaga.mylenta.action.DOWNLOAD";
    private static final String EXTRA_PARAM_URL = "com.gmail.avereshchaga.mylenta.extra.URL";
    public static final String CHANNEL = DownloadService.class.getSimpleName()+".broadcast";

    /**
     * Starts this service to perform action Download with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionDownload(Context context, String url) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_PARAM_URL, url);
        context.startService(intent);
    }

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                String url = intent.getStringExtra(EXTRA_PARAM_URL);
                handleActionDownload(url);
            }
        }
    }

    /**
     * Handle action Download in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownload(String url) {
        ArrayList<News> listRssNews = (ArrayList<News>) Utils.parseRssData(Utils.getXmlFromUrl(url));
        sendResult(listRssNews);
    }

    private void sendResult(ArrayList<News> listRssNews) {
        Intent intent = new Intent(CHANNEL);
        intent.putParcelableArrayListExtra(Utils.KEY_LIST, listRssNews);
        sendBroadcast(intent);
    }
}
