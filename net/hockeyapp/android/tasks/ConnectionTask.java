package net.hockeyapp.android.tasks;

import android.os.AsyncTask;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import net.hockeyapp.android.utils.Util;

public abstract class ConnectionTask<Params, Progress, Result>
  extends AsyncTask<Params, Progress, Result>
{
  protected static String getStringFromConnection(HttpURLConnection paramHttpURLConnection)
    throws IOException
  {
    paramHttpURLConnection = new BufferedInputStream(paramHttpURLConnection.getInputStream());
    String str = Util.convertStreamToString(paramHttpURLConnection);
    paramHttpURLConnection.close();
    return str;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/tasks/ConnectionTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */