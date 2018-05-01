package org.telegram.ui.Adapters;

import android.location.Location;
import android.os.AsyncTask;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public abstract class BaseLocationAdapter
  extends RecyclerListView.SelectionAdapter
{
  private AsyncTask<Void, Void, JSONObject> currentTask;
  private BaseLocationAdapterDelegate delegate;
  protected ArrayList<String> iconUrls = new ArrayList();
  private Location lastSearchLocation;
  protected ArrayList<TLRPC.TL_messageMediaVenue> places = new ArrayList();
  private Timer searchTimer;
  protected boolean searching;
  
  public void destroy()
  {
    if (this.currentTask != null)
    {
      this.currentTask.cancel(true);
      this.currentTask = null;
    }
  }
  
  public void searchDelayed(final String paramString, final Location paramLocation)
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      this.places.clear();
      notifyDataSetChanged();
    }
    for (;;)
    {
      return;
      try
      {
        if (this.searchTimer != null) {
          this.searchTimer.cancel();
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask()
        {
          public void run()
          {
            try
            {
              BaseLocationAdapter.this.searchTimer.cancel();
              BaseLocationAdapter.access$002(BaseLocationAdapter.this, null);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  BaseLocationAdapter.access$102(BaseLocationAdapter.this, null);
                  BaseLocationAdapter.this.searchGooglePlacesWithQuery(BaseLocationAdapter.1.this.val$query, BaseLocationAdapter.1.this.val$coordinate);
                }
              });
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e(localException);
              }
            }
          }
        }, 200L, 500L);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
  }
  
  public void searchGooglePlacesWithQuery(String paramString, Location paramLocation)
  {
    if ((this.lastSearchLocation != null) && (paramLocation.distanceTo(this.lastSearchLocation) < 200.0F)) {
      return;
    }
    this.lastSearchLocation = paramLocation;
    if (this.searching)
    {
      this.searching = false;
      if (this.currentTask != null)
      {
        this.currentTask.cancel(true);
        this.currentTask = null;
      }
    }
    for (;;)
    {
      try
      {
        this.searching = true;
        Locale localLocale = Locale.US;
        String str1 = BuildVars.FOURSQUARE_API_VERSION;
        String str2 = BuildVars.FOURSQUARE_API_ID;
        String str3 = BuildVars.FOURSQUARE_API_KEY;
        paramLocation = String.format(Locale.US, "%f,%f", new Object[] { Double.valueOf(paramLocation.getLatitude()), Double.valueOf(paramLocation.getLongitude()) });
        if ((paramString == null) || (paramString.length() <= 0)) {
          continue;
        }
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        paramString = "&query=" + URLEncoder.encode(paramString, "UTF-8");
        paramLocation = String.format(localLocale, "https://api.foursquare.com/v2/venues/search/?v=%s&locale=en&limit=25&client_id=%s&client_secret=%s&ll=%s%s", new Object[] { str1, str2, str3, paramLocation, paramString });
        paramString = new org/telegram/ui/Adapters/BaseLocationAdapter$2;
        paramString.<init>(this, paramLocation);
        this.currentTask = paramString;
        this.currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
      }
      catch (Exception paramString)
      {
        FileLog.e(paramString);
        this.searching = false;
        if (this.delegate == null) {
          continue;
        }
        this.delegate.didLoadedSearchResult(this.places);
        continue;
      }
      notifyDataSetChanged();
      break;
      paramString = "";
    }
  }
  
  public void setDelegate(BaseLocationAdapterDelegate paramBaseLocationAdapterDelegate)
  {
    this.delegate = paramBaseLocationAdapterDelegate;
  }
  
  public static abstract interface BaseLocationAdapterDelegate
  {
    public abstract void didLoadedSearchResult(ArrayList<TLRPC.TL_messageMediaVenue> paramArrayList);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/BaseLocationAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */