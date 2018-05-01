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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public abstract class BaseLocationAdapter extends SelectionAdapter {
    private AsyncTask<Void, Void, JSONObject> currentTask;
    private BaseLocationAdapterDelegate delegate;
    protected ArrayList<String> iconUrls = new ArrayList();
    private Location lastSearchLocation;
    protected ArrayList<TL_messageMediaVenue> places = new ArrayList();
    private Timer searchTimer;
    protected boolean searching;

    public interface BaseLocationAdapterDelegate {
        void didLoadedSearchResult(ArrayList<TL_messageMediaVenue> arrayList);
    }

    public void destroy() {
        if (this.currentTask != null) {
            this.currentTask.cancel(true);
            this.currentTask = null;
        }
    }

    public void setDelegate(BaseLocationAdapterDelegate baseLocationAdapterDelegate) {
        this.delegate = baseLocationAdapterDelegate;
    }

    public void searchDelayed(final String str, final Location location) {
        if (str != null) {
            if (str.length() != 0) {
                try {
                    if (this.searchTimer != null) {
                        this.searchTimer.cancel();
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                this.searchTimer = new Timer();
                this.searchTimer.schedule(new TimerTask() {

                    /* renamed from: org.telegram.ui.Adapters.BaseLocationAdapter$1$1 */
                    class C07681 implements Runnable {
                        C07681() {
                        }

                        public void run() {
                            BaseLocationAdapter.this.lastSearchLocation = null;
                            BaseLocationAdapter.this.searchGooglePlacesWithQuery(str, location);
                        }
                    }

                    public void run() {
                        try {
                            BaseLocationAdapter.this.searchTimer.cancel();
                            BaseLocationAdapter.this.searchTimer = null;
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        AndroidUtilities.runOnUIThread(new C07681());
                    }
                }, 200, 500);
                return;
            }
        }
        this.places.clear();
        notifyDataSetChanged();
    }

    public void searchGooglePlacesWithQuery(String str, Location location) {
        if (this.lastSearchLocation == null || location.distanceTo(this.lastSearchLocation) >= 200.0f) {
            this.lastSearchLocation = location;
            if (this.searching) {
                this.searching = false;
                if (this.currentTask != null) {
                    this.currentTask.cancel(true);
                    this.currentTask = null;
                }
            }
            try {
                this.searching = true;
                Locale locale = Locale.US;
                String str2 = "https://api.foursquare.com/v2/venues/search/?v=%s&locale=en&limit=25&client_id=%s&client_secret=%s&ll=%s%s";
                r5 = new Object[5];
                r5[3] = String.format(Locale.US, "%f,%f", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
                if (str == null || str.length() <= 0) {
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("&query=");
                    stringBuilder.append(URLEncoder.encode(str, C0542C.UTF8_NAME));
                    str = stringBuilder.toString();
                }
                r5[4] = str;
                str = String.format(locale, str2, r5);
                this.currentTask = new AsyncTask<Void, Void, JSONObject>() {
                    private boolean canRetry = true;

                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    private String downloadUrlContent(String str) {
                        Throwable th;
                        boolean z;
                        InputStream inputStream;
                        StringBuilder stringBuilder;
                        Throwable e;
                        int read;
                        int i = 0;
                        try {
                            str = new URL(str).openConnection();
                            try {
                                str.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                                str.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                                str.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                                str.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                                str.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                                str.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                                if (str instanceof HttpURLConnection) {
                                    HttpURLConnection httpURLConnection = (HttpURLConnection) str;
                                    httpURLConnection.setInstanceFollowRedirects(true);
                                    int responseCode = httpURLConnection.getResponseCode();
                                    if (responseCode == 302 || responseCode == 301 || responseCode == 303) {
                                        String headerField = httpURLConnection.getHeaderField("Location");
                                        String headerField2 = httpURLConnection.getHeaderField("Set-Cookie");
                                        URLConnection openConnection = new URL(headerField).openConnection();
                                        try {
                                            openConnection.setRequestProperty("Cookie", headerField2);
                                            openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                                            openConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                                            openConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                                            openConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                                            str = openConnection;
                                        } catch (String str2) {
                                            th = str2;
                                            str2 = openConnection;
                                            if (th instanceof SocketTimeoutException) {
                                                if (th instanceof UnknownHostException) {
                                                    if (!(th instanceof SocketException)) {
                                                        if (th instanceof FileNotFoundException) {
                                                        }
                                                        z = true;
                                                        FileLog.m3e(th);
                                                        inputStream = null;
                                                        if (z) {
                                                            stringBuilder = null;
                                                        } else {
                                                            if (str2 != null) {
                                                                try {
                                                                } catch (Throwable e2) {
                                                                    FileLog.m3e(e2);
                                                                }
                                                            }
                                                            if (inputStream == null) {
                                                                try {
                                                                    str2 = new byte[32768];
                                                                    stringBuilder = null;
                                                                    while (!isCancelled()) {
                                                                        try {
                                                                            read = inputStream.read(str2);
                                                                            if (read <= 0) {
                                                                                if (stringBuilder == null) {
                                                                                    stringBuilder = new StringBuilder();
                                                                                }
                                                                                stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                                                            } else if (read == -1) {
                                                                                i = 1;
                                                                            }
                                                                        } catch (Throwable e22) {
                                                                            FileLog.m3e(e22);
                                                                        } catch (Throwable th2) {
                                                                            e22 = th2;
                                                                        }
                                                                    }
                                                                } catch (Throwable th3) {
                                                                    e22 = th3;
                                                                    stringBuilder = null;
                                                                    FileLog.m3e(e22);
                                                                    if (inputStream != null) {
                                                                        try {
                                                                            inputStream.close();
                                                                        } catch (Throwable e222) {
                                                                            FileLog.m3e(e222);
                                                                        }
                                                                    }
                                                                    if (i != 0) {
                                                                        return stringBuilder.toString();
                                                                    }
                                                                    return null;
                                                                }
                                                            }
                                                            stringBuilder = null;
                                                            if (inputStream != null) {
                                                                inputStream.close();
                                                            }
                                                        }
                                                        if (i != 0) {
                                                            return stringBuilder.toString();
                                                        }
                                                        return null;
                                                    }
                                                }
                                            }
                                            z = false;
                                            FileLog.m3e(th);
                                            inputStream = null;
                                            if (z) {
                                                stringBuilder = null;
                                            } else {
                                                if (str2 != null) {
                                                }
                                                if (inputStream == null) {
                                                    stringBuilder = null;
                                                } else {
                                                    str2 = new byte[32768];
                                                    stringBuilder = null;
                                                    while (!isCancelled()) {
                                                        read = inputStream.read(str2);
                                                        if (read <= 0) {
                                                            if (stringBuilder == null) {
                                                                stringBuilder = new StringBuilder();
                                                            }
                                                            stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                                        } else if (read == -1) {
                                                            i = 1;
                                                        }
                                                    }
                                                }
                                                if (inputStream != null) {
                                                    inputStream.close();
                                                }
                                            }
                                            if (i != 0) {
                                                return null;
                                            }
                                            return stringBuilder.toString();
                                        }
                                    }
                                }
                                str2.connect();
                                inputStream = str2.getInputStream();
                                z = true;
                            } catch (Throwable th4) {
                                th = th4;
                                if (th instanceof SocketTimeoutException) {
                                    if (th instanceof UnknownHostException) {
                                        if (!(th instanceof SocketException)) {
                                            if (th.getMessage() != null && th.getMessage().contains("ECONNRESET")) {
                                            }
                                            z = true;
                                            FileLog.m3e(th);
                                            inputStream = null;
                                            if (z) {
                                                stringBuilder = null;
                                            } else {
                                                if (str2 != null) {
                                                }
                                                if (inputStream == null) {
                                                    stringBuilder = null;
                                                } else {
                                                    str2 = new byte[32768];
                                                    stringBuilder = null;
                                                    while (!isCancelled()) {
                                                        read = inputStream.read(str2);
                                                        if (read <= 0) {
                                                            if (stringBuilder == null) {
                                                                stringBuilder = new StringBuilder();
                                                            }
                                                            stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                                        } else if (read == -1) {
                                                            i = 1;
                                                        }
                                                    }
                                                }
                                                if (inputStream != null) {
                                                    inputStream.close();
                                                }
                                            }
                                            if (i != 0) {
                                                return null;
                                            }
                                            return stringBuilder.toString();
                                        }
                                        if (th instanceof FileNotFoundException) {
                                        }
                                        z = true;
                                        FileLog.m3e(th);
                                        inputStream = null;
                                        if (z) {
                                            if (str2 != null) {
                                            }
                                            if (inputStream == null) {
                                                str2 = new byte[32768];
                                                stringBuilder = null;
                                                while (!isCancelled()) {
                                                    read = inputStream.read(str2);
                                                    if (read <= 0) {
                                                        if (stringBuilder == null) {
                                                            stringBuilder = new StringBuilder();
                                                        }
                                                        stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                                    } else if (read == -1) {
                                                        i = 1;
                                                    }
                                                }
                                            } else {
                                                stringBuilder = null;
                                            }
                                            if (inputStream != null) {
                                                inputStream.close();
                                            }
                                        } else {
                                            stringBuilder = null;
                                        }
                                        if (i != 0) {
                                            return stringBuilder.toString();
                                        }
                                        return null;
                                    }
                                }
                                z = false;
                                FileLog.m3e(th);
                                inputStream = null;
                                if (z) {
                                    stringBuilder = null;
                                } else {
                                    if (str2 != null) {
                                    }
                                    if (inputStream == null) {
                                        stringBuilder = null;
                                    } else {
                                        str2 = new byte[32768];
                                        stringBuilder = null;
                                        while (!isCancelled()) {
                                            read = inputStream.read(str2);
                                            if (read <= 0) {
                                                if (stringBuilder == null) {
                                                    stringBuilder = new StringBuilder();
                                                }
                                                stringBuilder.append(new String(str2, 0, read, C0542C.UTF8_NAME));
                                            } else if (read == -1) {
                                                i = 1;
                                            }
                                        }
                                    }
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                }
                                if (i != 0) {
                                    return null;
                                }
                                return stringBuilder.toString();
                            }
                        } catch (String str22) {
                            th = str22;
                            str22 = null;
                            if (th instanceof SocketTimeoutException) {
                                if (th instanceof UnknownHostException) {
                                    if (!(th instanceof SocketException)) {
                                        if (th instanceof FileNotFoundException) {
                                        }
                                        z = true;
                                        FileLog.m3e(th);
                                        inputStream = null;
                                        if (z) {
                                            if (str22 != null) {
                                            }
                                            if (inputStream == null) {
                                                str22 = new byte[32768];
                                                stringBuilder = null;
                                                while (!isCancelled()) {
                                                    read = inputStream.read(str22);
                                                    if (read <= 0) {
                                                        if (stringBuilder == null) {
                                                            stringBuilder = new StringBuilder();
                                                        }
                                                        stringBuilder.append(new String(str22, 0, read, C0542C.UTF8_NAME));
                                                    } else if (read == -1) {
                                                        i = 1;
                                                    }
                                                }
                                            } else {
                                                stringBuilder = null;
                                            }
                                            if (inputStream != null) {
                                                inputStream.close();
                                            }
                                        } else {
                                            stringBuilder = null;
                                        }
                                        if (i != 0) {
                                            return stringBuilder.toString();
                                        }
                                        return null;
                                    }
                                }
                            }
                            z = false;
                            FileLog.m3e(th);
                            inputStream = null;
                            if (z) {
                                stringBuilder = null;
                            } else {
                                if (str22 != null) {
                                }
                                if (inputStream == null) {
                                    stringBuilder = null;
                                } else {
                                    str22 = new byte[32768];
                                    stringBuilder = null;
                                    while (!isCancelled()) {
                                        read = inputStream.read(str22);
                                        if (read <= 0) {
                                            if (stringBuilder == null) {
                                                stringBuilder = new StringBuilder();
                                            }
                                            stringBuilder.append(new String(str22, 0, read, C0542C.UTF8_NAME));
                                        } else if (read == -1) {
                                            i = 1;
                                        }
                                    }
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                            }
                            if (i != 0) {
                                return null;
                            }
                            return stringBuilder.toString();
                        }
                        if (z) {
                            if (str22 != null) {
                                if ((str22 instanceof HttpURLConnection) && ((HttpURLConnection) str22).getResponseCode() != 200) {
                                }
                            }
                            if (inputStream == null) {
                                str22 = new byte[32768];
                                stringBuilder = null;
                                while (!isCancelled()) {
                                    read = inputStream.read(str22);
                                    if (read <= 0) {
                                        if (stringBuilder == null) {
                                            stringBuilder = new StringBuilder();
                                        }
                                        stringBuilder.append(new String(str22, 0, read, C0542C.UTF8_NAME));
                                    } else if (read == -1) {
                                        i = 1;
                                    }
                                }
                            } else {
                                stringBuilder = null;
                            }
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } else {
                            stringBuilder = null;
                        }
                        if (i != 0) {
                            return stringBuilder.toString();
                        }
                        return null;
                    }

                    protected JSONObject doInBackground(Void... voidArr) {
                        voidArr = downloadUrlContent(str);
                        if (isCancelled()) {
                            return null;
                        }
                        try {
                            return new JSONObject(voidArr);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                            return null;
                        }
                    }

                    protected void onPostExecute(JSONObject jSONObject) {
                        if (jSONObject != null) {
                            try {
                                BaseLocationAdapter.this.places.clear();
                                BaseLocationAdapter.this.iconUrls.clear();
                                jSONObject = jSONObject.getJSONObject("response").getJSONArray("venues");
                                for (int i = 0; i < jSONObject.length(); i++) {
                                    try {
                                        JSONObject jSONObject2;
                                        JSONObject jSONObject3 = jSONObject.getJSONObject(i);
                                        Object obj = null;
                                        if (jSONObject3.has("categories")) {
                                            JSONArray jSONArray = jSONObject3.getJSONArray("categories");
                                            if (jSONArray.length() > 0) {
                                                JSONObject jSONObject4 = jSONArray.getJSONObject(0);
                                                if (jSONObject4.has("icon")) {
                                                    jSONObject2 = jSONObject4.getJSONObject("icon");
                                                    obj = String.format(Locale.US, "%s64%s", new Object[]{jSONObject2.getString("prefix"), jSONObject2.getString("suffix")});
                                                }
                                            }
                                        }
                                        BaseLocationAdapter.this.iconUrls.add(obj);
                                        jSONObject2 = jSONObject3.getJSONObject("location");
                                        TL_messageMediaVenue tL_messageMediaVenue = new TL_messageMediaVenue();
                                        tL_messageMediaVenue.geo = new TL_geoPoint();
                                        tL_messageMediaVenue.geo.lat = jSONObject2.getDouble("lat");
                                        tL_messageMediaVenue.geo._long = jSONObject2.getDouble("lng");
                                        if (jSONObject2.has("address")) {
                                            tL_messageMediaVenue.address = jSONObject2.getString("address");
                                        } else if (jSONObject2.has("city")) {
                                            tL_messageMediaVenue.address = jSONObject2.getString("city");
                                        } else if (jSONObject2.has("state")) {
                                            tL_messageMediaVenue.address = jSONObject2.getString("state");
                                        } else if (jSONObject2.has("country")) {
                                            tL_messageMediaVenue.address = jSONObject2.getString("country");
                                        } else {
                                            tL_messageMediaVenue.address = String.format(Locale.US, "%f,%f", new Object[]{Double.valueOf(tL_messageMediaVenue.geo.lat), Double.valueOf(tL_messageMediaVenue.geo._long)});
                                        }
                                        if (jSONObject3.has("name")) {
                                            tL_messageMediaVenue.title = jSONObject3.getString("name");
                                        }
                                        tL_messageMediaVenue.venue_type = TtmlNode.ANONYMOUS_REGION_ID;
                                        tL_messageMediaVenue.venue_id = jSONObject3.getString(TtmlNode.ATTR_ID);
                                        tL_messageMediaVenue.provider = "foursquare";
                                        BaseLocationAdapter.this.places.add(tL_messageMediaVenue);
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                }
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                            BaseLocationAdapter.this.searching = false;
                            BaseLocationAdapter.this.notifyDataSetChanged();
                            if (BaseLocationAdapter.this.delegate != null) {
                                BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
                                return;
                            }
                            return;
                        }
                        BaseLocationAdapter.this.searching = false;
                        BaseLocationAdapter.this.notifyDataSetChanged();
                        if (BaseLocationAdapter.this.delegate != null) {
                            BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
                        }
                    }
                };
                this.currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            } catch (Throwable e) {
                FileLog.m3e(e);
                this.searching = false;
                if (this.delegate != null) {
                    this.delegate.didLoadedSearchResult(this.places);
                }
            }
            notifyDataSetChanged();
        }
    }
}
