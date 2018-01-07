package net.hockeyapp.android.tasks;

import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import net.hockeyapp.android.utils.Util;

public abstract class ConnectionTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected static String getStringFromConnection(HttpURLConnection connection) throws IOException {
        InputStream inputStream = new BufferedInputStream(connection.getInputStream());
        String jsonString = Util.convertStreamToString(inputStream);
        inputStream.close();
        return jsonString;
    }
}
