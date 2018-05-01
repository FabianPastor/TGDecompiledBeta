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
import org.telegram.tgnet.ConnectionsManager;
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

    public void setDelegate(BaseLocationAdapterDelegate delegate) {
        this.delegate = delegate;
    }

    public void searchDelayed(final String query, final Location coordinate) {
        if (query == null || query.length() == 0) {
            this.places.clear();
            notifyDataSetChanged();
            return;
        }
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
                    BaseLocationAdapter.this.searchGooglePlacesWithQuery(query, coordinate);
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
    }

    public void searchGooglePlacesWithQuery(String query, Location coordinate) {
        if (this.lastSearchLocation == null || coordinate.distanceTo(this.lastSearchLocation) >= 200.0f) {
            this.lastSearchLocation = coordinate;
            if (this.searching) {
                this.searching = false;
                if (this.currentTask != null) {
                    this.currentTask.cancel(true);
                    this.currentTask = null;
                }
            }
            try {
                String str;
                this.searching = true;
                Locale locale = Locale.US;
                String str2 = "https://api.foursquare.com/v2/venues/search/?v=%s&locale=en&limit=25&client_id=%s&client_secret=%s&ll=%s%s";
                r5 = new Object[5];
                r5[3] = String.format(Locale.US, "%f,%f", new Object[]{Double.valueOf(coordinate.getLatitude()), Double.valueOf(coordinate.getLongitude())});
                if (query == null || query.length() <= 0) {
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                } else {
                    str = "&query=" + URLEncoder.encode(query, C0542C.UTF8_NAME);
                }
                r5[4] = str;
                final String url = String.format(locale, str2, r5);
                this.currentTask = new AsyncTask<Void, Void, JSONObject>() {
                    private boolean canRetry = true;

                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    private String downloadUrlContent(String url) {
                        Throwable e;
                        boolean canRetry = true;
                        InputStream httpConnectionStream = null;
                        boolean done = false;
                        StringBuilder result = null;
                        URLConnection httpConnection = null;
                        try {
                            httpConnection = new URL(url).openConnection();
                            httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                            httpConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                            httpConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                            httpConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                            httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                            httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                            if (httpConnection instanceof HttpURLConnection) {
                                HttpURLConnection httpURLConnection = (HttpURLConnection) httpConnection;
                                httpURLConnection.setInstanceFollowRedirects(true);
                                int status = httpURLConnection.getResponseCode();
                                if (status == 302 || status == 301 || status == 303) {
                                    String newUrl = httpURLConnection.getHeaderField("Location");
                                    String cookies = httpURLConnection.getHeaderField("Set-Cookie");
                                    httpConnection = new URL(newUrl).openConnection();
                                    httpConnection.setRequestProperty("Cookie", cookies);
                                    httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)");
                                    httpConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
                                    httpConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                                    httpConnection.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
                                }
                            }
                            httpConnection.connect();
                            httpConnectionStream = httpConnection.getInputStream();
                        } catch (Throwable e2) {
                            if (e2 instanceof SocketTimeoutException) {
                                if (ConnectionsManager.isNetworkOnline()) {
                                    canRetry = false;
                                }
                            } else if (e2 instanceof UnknownHostException) {
                                canRetry = false;
                            } else if (e2 instanceof SocketException) {
                                if (e2.getMessage() != null && e2.getMessage().contains("ECONNRESET")) {
                                    canRetry = false;
                                }
                            } else if (e2 instanceof FileNotFoundException) {
                                canRetry = false;
                            }
                            FileLog.m3e(e2);
                        }
                        if (canRetry) {
                            if (httpConnection != null) {
                                try {
                                    if (httpConnection instanceof HttpURLConnection) {
                                        int code = ((HttpURLConnection) httpConnection).getResponseCode();
                                        if (code != 200) {
                                            if (code != 202) {
                                            }
                                        }
                                    }
                                } catch (Throwable e22) {
                                    FileLog.m3e(e22);
                                }
                            }
                            if (httpConnectionStream != null) {
                                try {
                                    byte[] data = new byte[32768];
                                    StringBuilder result2 = null;
                                    while (!isCancelled()) {
                                        try {
                                            try {
                                                int read = httpConnectionStream.read(data);
                                                if (read > 0) {
                                                    if (result2 == null) {
                                                        result = new StringBuilder();
                                                    } else {
                                                        result = result2;
                                                    }
                                                    try {
                                                        result.append(new String(data, 0, read, C0542C.UTF8_NAME));
                                                        result2 = result;
                                                    } catch (Exception e3) {
                                                        e22 = e3;
                                                    }
                                                } else if (read == -1) {
                                                    done = true;
                                                    result = result2;
                                                } else {
                                                    result = result2;
                                                }
                                            } catch (Exception e4) {
                                                e22 = e4;
                                                result = result2;
                                            }
                                        } catch (Throwable th) {
                                            e22 = th;
                                            result = result2;
                                        }
                                    }
                                    result = result2;
                                } catch (Throwable th2) {
                                    e22 = th2;
                                    FileLog.m3e(e22);
                                    if (httpConnectionStream != null) {
                                        try {
                                            httpConnectionStream.close();
                                        } catch (Throwable e222) {
                                            FileLog.m3e(e222);
                                        }
                                    }
                                    if (done) {
                                        return null;
                                    }
                                    return result.toString();
                                }
                            }
                            if (httpConnectionStream != null) {
                                httpConnectionStream.close();
                            }
                        }
                        if (done) {
                            return result.toString();
                        }
                        return null;
                        FileLog.m3e(e222);
                        if (httpConnectionStream != null) {
                            httpConnectionStream.close();
                        }
                        if (done) {
                            return result.toString();
                        }
                        return null;
                    }

                    protected JSONObject doInBackground(Void... voids) {
                        String code = downloadUrlContent(url);
                        if (isCancelled()) {
                            return null;
                        }
                        try {
                            return new JSONObject(code);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                            return null;
                        }
                    }

                    protected void onPostExecute(JSONObject response) {
                        if (response != null) {
                            try {
                                BaseLocationAdapter.this.places.clear();
                                BaseLocationAdapter.this.iconUrls.clear();
                                JSONArray result = response.getJSONObject("response").getJSONArray("venues");
                                for (int a = 0; a < result.length(); a++) {
                                    try {
                                        JSONObject object = result.getJSONObject(a);
                                        String iconUrl = null;
                                        if (object.has("categories")) {
                                            JSONArray categories = object.getJSONArray("categories");
                                            if (categories.length() > 0) {
                                                JSONObject category = categories.getJSONObject(0);
                                                if (category.has("icon")) {
                                                    JSONObject icon = category.getJSONObject("icon");
                                                    iconUrl = String.format(Locale.US, "%s64%s", new Object[]{icon.getString("prefix"), icon.getString("suffix")});
                                                }
                                            }
                                        }
                                        BaseLocationAdapter.this.iconUrls.add(iconUrl);
                                        JSONObject location = object.getJSONObject("location");
                                        TL_messageMediaVenue venue = new TL_messageMediaVenue();
                                        venue.geo = new TL_geoPoint();
                                        venue.geo.lat = location.getDouble("lat");
                                        venue.geo._long = location.getDouble("lng");
                                        if (location.has("address")) {
                                            venue.address = location.getString("address");
                                        } else if (location.has("city")) {
                                            venue.address = location.getString("city");
                                        } else if (location.has("state")) {
                                            venue.address = location.getString("state");
                                        } else if (location.has("country")) {
                                            venue.address = location.getString("country");
                                        } else {
                                            venue.address = String.format(Locale.US, "%f,%f", new Object[]{Double.valueOf(venue.geo.lat), Double.valueOf(venue.geo._long)});
                                        }
                                        if (object.has("name")) {
                                            venue.title = object.getString("name");
                                        }
                                        venue.venue_type = TtmlNode.ANONYMOUS_REGION_ID;
                                        venue.venue_id = object.getString(TtmlNode.ATTR_ID);
                                        venue.provider = "foursquare";
                                        BaseLocationAdapter.this.places.add(venue);
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
