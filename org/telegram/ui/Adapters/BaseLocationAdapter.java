package org.telegram.ui.Adapters;

import android.location.Location;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.volley.RequestQueue;
import org.telegram.messenger.volley.Response.ErrorListener;
import org.telegram.messenger.volley.Response.Listener;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.messenger.volley.toolbox.JsonObjectRequest;
import org.telegram.messenger.volley.toolbox.Volley;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;

public class BaseLocationAdapter
  extends BaseFragmentAdapter
{
  private BaseLocationAdapterDelegate delegate;
  protected ArrayList<String> iconUrls = new ArrayList();
  private Location lastSearchLocation;
  protected ArrayList<TLRPC.TL_messageMediaVenue> places = new ArrayList();
  private RequestQueue requestQueue = Volley.newRequestQueue(ApplicationLoader.applicationContext);
  private Timer searchTimer;
  protected boolean searching;
  
  public void destroy()
  {
    if (this.requestQueue != null)
    {
      this.requestQueue.cancelAll("search");
      this.requestQueue.stop();
    }
  }
  
  public void searchDelayed(final String paramString, final Location paramLocation)
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      this.places.clear();
      notifyDataSetChanged();
      return;
    }
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
              FileLog.e("tmessages", localException);
            }
          }
        }
      }, 200L, 500L);
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
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
      this.requestQueue.cancelAll("search");
    }
    try
    {
      this.searching = true;
      String str = String.format(Locale.US, "https://api.foursquare.com/v2/venues/search/?v=%s&locale=en&limit=25&client_id=%s&client_secret=%s&ll=%s", new Object[] { BuildVars.FOURSQUARE_API_VERSION, BuildVars.FOURSQUARE_API_ID, BuildVars.FOURSQUARE_API_KEY, String.format(Locale.US, "%f,%f", new Object[] { Double.valueOf(paramLocation.getLatitude()), Double.valueOf(paramLocation.getLongitude()) }) });
      paramLocation = str;
      if (paramString != null)
      {
        paramLocation = str;
        if (paramString.length() > 0) {
          paramLocation = str + "&query=" + URLEncoder.encode(paramString, "UTF-8");
        }
      }
      paramString = new JsonObjectRequest(0, paramLocation, null, new Response.Listener()new Response.ErrorListener
      {
        /* Error */
        public void onResponse(JSONObject paramAnonymousJSONObject)
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 18	org/telegram/ui/Adapters/BaseLocationAdapter$2:this$0	Lorg/telegram/ui/Adapters/BaseLocationAdapter;
          //   4: getfield 35	org/telegram/ui/Adapters/BaseLocationAdapter:places	Ljava/util/ArrayList;
          //   7: invokevirtual 40	java/util/ArrayList:clear	()V
          //   10: aload_0
          //   11: getfield 18	org/telegram/ui/Adapters/BaseLocationAdapter$2:this$0	Lorg/telegram/ui/Adapters/BaseLocationAdapter;
          //   14: getfield 43	org/telegram/ui/Adapters/BaseLocationAdapter:iconUrls	Ljava/util/ArrayList;
          //   17: invokevirtual 40	java/util/ArrayList:clear	()V
          //   20: aload_1
          //   21: ldc 45
          //   23: invokevirtual 49	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
          //   26: ldc 51
          //   28: invokevirtual 55	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
          //   31: astore 5
          //   33: iconst_0
          //   34: istore_2
          //   35: aload 5
          //   37: invokevirtual 61	org/json/JSONArray:length	()I
          //   40: istore_3
          //   41: iload_2
          //   42: iload_3
          //   43: if_icmpge +293 -> 336
          //   46: aload 5
          //   48: iload_2
          //   49: invokevirtual 64	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
          //   52: astore 6
          //   54: aconst_null
          //   55: astore 4
          //   57: aload 4
          //   59: astore_1
          //   60: aload 6
          //   62: ldc 66
          //   64: invokevirtual 70	org/json/JSONObject:has	(Ljava/lang/String;)Z
          //   67: ifeq +83 -> 150
          //   70: aload 6
          //   72: ldc 66
          //   74: invokevirtual 55	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
          //   77: astore 7
          //   79: aload 4
          //   81: astore_1
          //   82: aload 7
          //   84: invokevirtual 61	org/json/JSONArray:length	()I
          //   87: ifle +63 -> 150
          //   90: aload 7
          //   92: iconst_0
          //   93: invokevirtual 64	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
          //   96: astore 7
          //   98: aload 4
          //   100: astore_1
          //   101: aload 7
          //   103: ldc 72
          //   105: invokevirtual 70	org/json/JSONObject:has	(Ljava/lang/String;)Z
          //   108: ifeq +42 -> 150
          //   111: aload 7
          //   113: ldc 72
          //   115: invokevirtual 49	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
          //   118: astore_1
          //   119: getstatic 78	java/util/Locale:US	Ljava/util/Locale;
          //   122: ldc 80
          //   124: iconst_2
          //   125: anewarray 5	java/lang/Object
          //   128: dup
          //   129: iconst_0
          //   130: aload_1
          //   131: ldc 82
          //   133: invokevirtual 86	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
          //   136: aastore
          //   137: dup
          //   138: iconst_1
          //   139: aload_1
          //   140: ldc 88
          //   142: invokevirtual 86	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
          //   145: aastore
          //   146: invokestatic 94	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
          //   149: astore_1
          //   150: aload_0
          //   151: getfield 18	org/telegram/ui/Adapters/BaseLocationAdapter$2:this$0	Lorg/telegram/ui/Adapters/BaseLocationAdapter;
          //   154: getfield 43	org/telegram/ui/Adapters/BaseLocationAdapter:iconUrls	Ljava/util/ArrayList;
          //   157: aload_1
          //   158: invokevirtual 98	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   161: pop
          //   162: aload 6
          //   164: ldc 100
          //   166: invokevirtual 49	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
          //   169: astore_1
          //   170: new 102	org/telegram/tgnet/TLRPC$TL_messageMediaVenue
          //   173: dup
          //   174: invokespecial 103	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:<init>	()V
          //   177: astore 4
          //   179: aload 4
          //   181: new 105	org/telegram/tgnet/TLRPC$TL_geoPoint
          //   184: dup
          //   185: invokespecial 106	org/telegram/tgnet/TLRPC$TL_geoPoint:<init>	()V
          //   188: putfield 110	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
          //   191: aload 4
          //   193: getfield 110	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
          //   196: aload_1
          //   197: ldc 112
          //   199: invokevirtual 116	org/json/JSONObject:getDouble	(Ljava/lang/String;)D
          //   202: putfield 121	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
          //   205: aload 4
          //   207: getfield 110	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
          //   210: aload_1
          //   211: ldc 123
          //   213: invokevirtual 116	org/json/JSONObject:getDouble	(Ljava/lang/String;)D
          //   216: putfield 126	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
          //   219: aload_1
          //   220: ldc -128
          //   222: invokevirtual 70	org/json/JSONObject:has	(Ljava/lang/String;)Z
          //   225: ifeq +71 -> 296
          //   228: aload 4
          //   230: aload_1
          //   231: ldc -128
          //   233: invokevirtual 86	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
          //   236: putfield 131	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:address	Ljava/lang/String;
          //   239: aload 6
          //   241: ldc -123
          //   243: invokevirtual 70	org/json/JSONObject:has	(Ljava/lang/String;)Z
          //   246: ifeq +15 -> 261
          //   249: aload 4
          //   251: aload 6
          //   253: ldc -123
          //   255: invokevirtual 86	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
          //   258: putfield 136	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:title	Ljava/lang/String;
          //   261: aload 4
          //   263: aload 6
          //   265: ldc -118
          //   267: invokevirtual 86	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
          //   270: putfield 141	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:venue_id	Ljava/lang/String;
          //   273: aload 4
          //   275: ldc -113
          //   277: putfield 146	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:provider	Ljava/lang/String;
          //   280: aload_0
          //   281: getfield 18	org/telegram/ui/Adapters/BaseLocationAdapter$2:this$0	Lorg/telegram/ui/Adapters/BaseLocationAdapter;
          //   284: getfield 35	org/telegram/ui/Adapters/BaseLocationAdapter:places	Ljava/util/ArrayList;
          //   287: aload 4
          //   289: invokevirtual 98	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   292: pop
          //   293: goto +182 -> 475
          //   296: aload_1
          //   297: ldc -108
          //   299: invokevirtual 70	org/json/JSONObject:has	(Ljava/lang/String;)Z
          //   302: ifeq +79 -> 381
          //   305: aload 4
          //   307: aload_1
          //   308: ldc -108
          //   310: invokevirtual 86	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
          //   313: putfield 131	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:address	Ljava/lang/String;
          //   316: goto -77 -> 239
          //   319: astore_1
          //   320: ldc -106
          //   322: aload_1
          //   323: invokestatic 156	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
          //   326: goto +149 -> 475
          //   329: astore_1
          //   330: ldc -106
          //   332: aload_1
          //   333: invokestatic 156	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
          //   336: aload_0
          //   337: getfield 18	org/telegram/ui/Adapters/BaseLocationAdapter$2:this$0	Lorg/telegram/ui/Adapters/BaseLocationAdapter;
          //   340: iconst_0
          //   341: putfield 160	org/telegram/ui/Adapters/BaseLocationAdapter:searching	Z
          //   344: aload_0
          //   345: getfield 18	org/telegram/ui/Adapters/BaseLocationAdapter$2:this$0	Lorg/telegram/ui/Adapters/BaseLocationAdapter;
          //   348: invokevirtual 163	org/telegram/ui/Adapters/BaseLocationAdapter:notifyDataSetChanged	()V
          //   351: aload_0
          //   352: getfield 18	org/telegram/ui/Adapters/BaseLocationAdapter$2:this$0	Lorg/telegram/ui/Adapters/BaseLocationAdapter;
          //   355: invokestatic 167	org/telegram/ui/Adapters/BaseLocationAdapter:access$200	(Lorg/telegram/ui/Adapters/BaseLocationAdapter;)Lorg/telegram/ui/Adapters/BaseLocationAdapter$BaseLocationAdapterDelegate;
          //   358: ifnull +22 -> 380
          //   361: aload_0
          //   362: getfield 18	org/telegram/ui/Adapters/BaseLocationAdapter$2:this$0	Lorg/telegram/ui/Adapters/BaseLocationAdapter;
          //   365: invokestatic 167	org/telegram/ui/Adapters/BaseLocationAdapter:access$200	(Lorg/telegram/ui/Adapters/BaseLocationAdapter;)Lorg/telegram/ui/Adapters/BaseLocationAdapter$BaseLocationAdapterDelegate;
          //   368: aload_0
          //   369: getfield 18	org/telegram/ui/Adapters/BaseLocationAdapter$2:this$0	Lorg/telegram/ui/Adapters/BaseLocationAdapter;
          //   372: getfield 35	org/telegram/ui/Adapters/BaseLocationAdapter:places	Ljava/util/ArrayList;
          //   375: invokeinterface 173 2 0
          //   380: return
          //   381: aload_1
          //   382: ldc -81
          //   384: invokevirtual 70	org/json/JSONObject:has	(Ljava/lang/String;)Z
          //   387: ifeq +17 -> 404
          //   390: aload 4
          //   392: aload_1
          //   393: ldc -81
          //   395: invokevirtual 86	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
          //   398: putfield 131	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:address	Ljava/lang/String;
          //   401: goto -162 -> 239
          //   404: aload_1
          //   405: ldc -79
          //   407: invokevirtual 70	org/json/JSONObject:has	(Ljava/lang/String;)Z
          //   410: ifeq +17 -> 427
          //   413: aload 4
          //   415: aload_1
          //   416: ldc -79
          //   418: invokevirtual 86	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
          //   421: putfield 131	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:address	Ljava/lang/String;
          //   424: goto -185 -> 239
          //   427: aload 4
          //   429: getstatic 78	java/util/Locale:US	Ljava/util/Locale;
          //   432: ldc -77
          //   434: iconst_2
          //   435: anewarray 5	java/lang/Object
          //   438: dup
          //   439: iconst_0
          //   440: aload 4
          //   442: getfield 110	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
          //   445: getfield 121	org/telegram/tgnet/TLRPC$GeoPoint:lat	D
          //   448: invokestatic 185	java/lang/Double:valueOf	(D)Ljava/lang/Double;
          //   451: aastore
          //   452: dup
          //   453: iconst_1
          //   454: aload 4
          //   456: getfield 110	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:geo	Lorg/telegram/tgnet/TLRPC$GeoPoint;
          //   459: getfield 126	org/telegram/tgnet/TLRPC$GeoPoint:_long	D
          //   462: invokestatic 185	java/lang/Double:valueOf	(D)Ljava/lang/Double;
          //   465: aastore
          //   466: invokestatic 94	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
          //   469: putfield 131	org/telegram/tgnet/TLRPC$TL_messageMediaVenue:address	Ljava/lang/String;
          //   472: goto -233 -> 239
          //   475: iload_2
          //   476: iconst_1
          //   477: iadd
          //   478: istore_2
          //   479: goto -444 -> 35
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	482	0	this	2
          //   0	482	1	paramAnonymousJSONObject	JSONObject
          //   34	445	2	i	int
          //   40	4	3	j	int
          //   55	400	4	localTL_messageMediaVenue	TLRPC.TL_messageMediaVenue
          //   31	16	5	localJSONArray	org.json.JSONArray
          //   52	212	6	localJSONObject	JSONObject
          //   77	35	7	localObject	Object
          // Exception table:
          //   from	to	target	type
          //   46	54	319	java/lang/Exception
          //   60	79	319	java/lang/Exception
          //   82	98	319	java/lang/Exception
          //   101	150	319	java/lang/Exception
          //   150	239	319	java/lang/Exception
          //   239	261	319	java/lang/Exception
          //   261	293	319	java/lang/Exception
          //   296	316	319	java/lang/Exception
          //   381	401	319	java/lang/Exception
          //   404	424	319	java/lang/Exception
          //   427	472	319	java/lang/Exception
          //   0	33	329	java/lang/Exception
          //   35	41	329	java/lang/Exception
          //   320	326	329	java/lang/Exception
        }
      }, new Response.ErrorListener()
      {
        public void onErrorResponse(VolleyError paramAnonymousVolleyError)
        {
          FileLog.e("tmessages", "Error: " + paramAnonymousVolleyError.getMessage());
          BaseLocationAdapter.this.searching = false;
          BaseLocationAdapter.this.notifyDataSetChanged();
          if (BaseLocationAdapter.this.delegate != null) {
            BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
          }
        }
      });
      paramString.setShouldCache(false);
      paramString.setTag("search");
      this.requestQueue.add(paramString);
    }
    catch (Exception paramString)
    {
      for (;;)
      {
        FileLog.e("tmessages", paramString);
        this.searching = false;
        if (this.delegate != null) {
          this.delegate.didLoadedSearchResult(this.places);
        }
      }
    }
    notifyDataSetChanged();
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