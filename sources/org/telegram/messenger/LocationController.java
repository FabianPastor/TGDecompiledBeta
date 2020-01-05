package org.telegram.messenger;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseIntArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_channels_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputGeoPointEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messages_affectedMessages;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentLocations;
import org.telegram.tgnet.TLRPC.TL_messages_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_peerLocated;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class LocationController extends BaseController implements NotificationCenterDelegate, ConnectionCallbacks, OnConnectionFailedListener {
    private static final int BACKGROUD_UPDATE_TIME = 30000;
    private static final long FASTEST_INTERVAL = 1000;
    private static final int FOREGROUND_UPDATE_TIME = 20000;
    private static volatile LocationController[] Instance = new LocationController[3];
    private static final int LOCATION_ACQUIRE_TIME = 10000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int SEND_NEW_LOCATION_TIME = 2000;
    private static final long UPDATE_INTERVAL = 1000;
    private static final int WATCH_LOCATION_TIMEOUT = 65000;
    private static HashMap<LocationFetchCallback, Runnable> callbacks = new HashMap();
    private LongSparseArray<Boolean> cacheRequests = new LongSparseArray();
    private ArrayList<TL_peerLocated> cachedNearbyChats = new ArrayList();
    private ArrayList<TL_peerLocated> cachedNearbyUsers = new ArrayList();
    private FusedLocationListener fusedLocationListener = new FusedLocationListener();
    private GoogleApiClient googleApiClient;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener();
    private Location lastKnownLocation;
    private boolean lastLocationByGoogleMaps;
    private long lastLocationSendTime;
    private long lastLocationStartTime;
    private LongSparseArray<Integer> lastReadLocationTime = new LongSparseArray();
    private long locationEndWatchTime;
    private LocationManager locationManager = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location"));
    private LocationRequest locationRequest;
    private boolean locationSentSinceLastGoogleMapUpdate = true;
    public LongSparseArray<ArrayList<Message>> locationsCache = new LongSparseArray();
    private boolean lookingForPeopleNearby;
    private GpsLocationListener networkLocationListener = new GpsLocationListener();
    private GpsLocationListener passiveLocationListener = new GpsLocationListener();
    private Boolean playServicesAvailable;
    private SparseIntArray requests = new SparseIntArray();
    private ArrayList<SharingLocationInfo> sharingLocations = new ArrayList();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMap = new LongSparseArray();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMapUI = new LongSparseArray();
    public ArrayList<SharingLocationInfo> sharingLocationsUI = new ArrayList();
    private boolean started;
    private boolean wasConnectedToPlayServices;

    private class GpsLocationListener implements LocationListener {
        public void onProviderDisabled(String str) {
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
        }

        private GpsLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                if (LocationController.this.lastKnownLocation == null || !(this == LocationController.this.networkLocationListener || this == LocationController.this.passiveLocationListener)) {
                    LocationController.this.setLastKnownLocation(location);
                } else if (!LocationController.this.started && location.distanceTo(LocationController.this.lastKnownLocation) > 20.0f) {
                    LocationController.this.setLastKnownLocation(location);
                    LocationController.this.lastLocationSendTime = (SystemClock.uptimeMillis() - 30000) + 5000;
                }
            }
        }
    }

    public interface LocationFetchCallback {
        void onLocationAddressAvailable(String str, String str2, Location location);
    }

    public static class SharingLocationInfo {
        public int account;
        public long did;
        public MessageObject messageObject;
        public int mid;
        public int period;
        public int stopTime;
    }

    private class FusedLocationListener implements com.google.android.gms.location.LocationListener {
        private FusedLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                LocationController.this.setLastKnownLocation(location);
            }
        }
    }

    public void onConnectionSuspended(int i) {
    }

    public static LocationController getInstance(int i) {
        LocationController locationController = Instance[i];
        if (locationController == null) {
            synchronized (LocationController.class) {
                locationController = Instance[i];
                if (locationController == null) {
                    LocationController[] locationControllerArr = Instance;
                    LocationController locationController2 = new LocationController(i);
                    locationControllerArr[i] = locationController2;
                    locationController = locationController2;
                }
            }
        }
        return locationController;
    }

    public LocationController(int i) {
        super(i);
        Builder builder = new Builder(ApplicationLoader.applicationContext);
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        this.googleApiClient = builder.build();
        this.locationRequest = new LocationRequest();
        this.locationRequest.setPriority(100);
        this.locationRequest.setInterval(1000);
        this.locationRequest.setFastestInterval(1000);
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$jwDhs2Wxth9unque4gfUrSG9YJ8(this));
        loadSharingLocations();
    }

    public /* synthetic */ void lambda$new$0$LocationController() {
        LocationController locationController = getAccountInstance().getLocationController();
        getNotificationCenter().addObserver(locationController, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(locationController, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(locationController, NotificationCenter.replaceMessagesObjects);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        long longValue;
        ArrayList arrayList;
        ArrayList arrayList2;
        Object obj;
        int i4;
        MessageObject messageObject;
        int i5;
        if (i == NotificationCenter.didReceiveNewMessages) {
            if (!((Boolean) objArr[2]).booleanValue()) {
                longValue = ((Long) objArr[0]).longValue();
                if (isSharingLocation(longValue)) {
                    arrayList = (ArrayList) this.locationsCache.get(longValue);
                    if (arrayList != null) {
                        arrayList2 = (ArrayList) objArr[1];
                        obj = null;
                        for (i4 = 0; i4 < arrayList2.size(); i4++) {
                            messageObject = (MessageObject) arrayList2.get(i4);
                            if (messageObject.isLiveLocation()) {
                                for (int i6 = 0; i6 < arrayList.size(); i6++) {
                                    i5 = ((Message) arrayList.get(i6)).from_id;
                                    Message message = messageObject.messageOwner;
                                    if (i5 == message.from_id) {
                                        arrayList.set(i6, message);
                                        obj = 1;
                                        break;
                                    }
                                }
                                obj = null;
                                if (obj == null) {
                                    arrayList.add(messageObject.messageOwner);
                                }
                                obj = 1;
                            }
                        }
                        if (obj != null) {
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(longValue), Integer.valueOf(this.currentAccount));
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!(((Boolean) objArr[2]).booleanValue() || this.sharingLocationsUI.isEmpty())) {
                ArrayList arrayList3 = (ArrayList) objArr[0];
                i2 = ((Integer) objArr[1]).intValue();
                ArrayList arrayList4 = null;
                for (int i7 = 0; i7 < this.sharingLocationsUI.size(); i7++) {
                    SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocationsUI.get(i7);
                    MessageObject messageObject2 = sharingLocationInfo.messageObject;
                    if (i2 == (messageObject2 != null ? messageObject2.getChannelId() : 0) && arrayList3.contains(Integer.valueOf(sharingLocationInfo.mid))) {
                        if (arrayList4 == null) {
                            arrayList4 = new ArrayList();
                        }
                        arrayList4.add(Long.valueOf(sharingLocationInfo.did));
                    }
                }
                if (arrayList4 != null) {
                    while (i3 < arrayList4.size()) {
                        removeSharingLocation(((Long) arrayList4.get(i3)).longValue());
                        i3++;
                    }
                }
            }
        } else if (i == NotificationCenter.replaceMessagesObjects) {
            longValue = ((Long) objArr[0]).longValue();
            if (isSharingLocation(longValue)) {
                arrayList = (ArrayList) this.locationsCache.get(longValue);
                if (arrayList != null) {
                    arrayList2 = (ArrayList) objArr[1];
                    obj = null;
                    for (i4 = 0; i4 < arrayList2.size(); i4++) {
                        messageObject = (MessageObject) arrayList2.get(i4);
                        i5 = 0;
                        while (i5 < arrayList.size()) {
                            if (((Message) arrayList.get(i5)).from_id == messageObject.messageOwner.from_id) {
                                if (messageObject.isLiveLocation()) {
                                    arrayList.set(i5, messageObject.messageOwner);
                                } else {
                                    arrayList.remove(i5);
                                }
                                obj = 1;
                            } else {
                                i5++;
                            }
                        }
                    }
                    if (obj != null) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(longValue), Integer.valueOf(this.currentAccount));
                    }
                }
            }
        }
    }

    public void onConnected(Bundle bundle) {
        this.wasConnectedToPlayServices = true;
        try {
            if (VERSION.SDK_INT >= 21) {
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(this.locationRequest);
                LocationServices.SettingsApi.checkLocationSettings(this.googleApiClient, builder.build()).setResultCallback(new -$$Lambda$LocationController$HjK4JENsms0-KVbFHgtgUvgpN7c(this));
                return;
            }
            startFusedLocationRequest(true);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$onConnected$3$LocationController(LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        int statusCode = status.getStatusCode();
        if (statusCode == 0) {
            startFusedLocationRequest(true);
        } else if (statusCode == 6) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$SErDtRtUtr-SYpBAY9GDKQLN_qM(this, status));
        } else if (statusCode == 8502) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$whNm-MbzTi2lj2CwArRMu0lTbhc(this));
        }
    }

    public /* synthetic */ void lambda$null$1$LocationController(Status status) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowPlayServicesAlert, status);
    }

    public /* synthetic */ void lambda$null$2$LocationController() {
        this.playServicesAvailable = Boolean.valueOf(false);
        try {
            this.googleApiClient.disconnect();
            start();
        } catch (Throwable unused) {
        }
    }

    public void startFusedLocationRequest(boolean z) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$var_XYhGo5ZYMcvzEsVevHLyTlHo(this, z));
    }

    public /* synthetic */ void lambda$startFusedLocationRequest$4$LocationController(boolean z) {
        if (!z) {
            this.playServicesAvailable = Boolean.valueOf(false);
        }
        if (!this.lookingForPeopleNearby && this.sharingLocations.isEmpty()) {
            return;
        }
        if (z) {
            try {
                setLastKnownLocation(LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient));
                LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient, this.locationRequest, this.fusedLocationListener);
                return;
            } catch (Throwable th) {
                FileLog.e(th);
                return;
            }
        }
        start();
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!this.wasConnectedToPlayServices) {
            this.playServicesAvailable = Boolean.valueOf(false);
            if (this.started) {
                this.started = false;
                start();
            }
        }
    }

    private boolean checkPlayServices() {
        if (this.playServicesAvailable == null) {
            this.playServicesAvailable = Boolean.valueOf(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ApplicationLoader.applicationContext) == 0);
        }
        return this.playServicesAvailable.booleanValue();
    }

    private void broadcastLastKnownLocation(boolean z) {
        if (this.lastKnownLocation != null) {
            int i;
            if (this.requests.size() != 0) {
                if (z) {
                    for (i = 0; i < this.requests.size(); i++) {
                        getConnectionsManager().cancelRequest(this.requests.keyAt(i), false);
                    }
                }
                this.requests.clear();
            }
            i = getConnectionsManager().getCurrentTime();
            float[] fArr = new float[1];
            for (int i2 = 0; i2 < this.sharingLocations.size(); i2++) {
                SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocations.get(i2);
                Message message = sharingLocationInfo.messageObject.messageOwner;
                MessageMedia messageMedia = message.media;
                if (!(messageMedia == null || messageMedia.geo == null)) {
                    int i3 = message.edit_date;
                    if (i3 == 0) {
                        i3 = message.date;
                    }
                    GeoPoint geoPoint = sharingLocationInfo.messageObject.messageOwner.media.geo;
                    if (Math.abs(i - i3) < 10) {
                        Location.distanceBetween(geoPoint.lat, geoPoint._long, this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude(), fArr);
                        if (fArr[0] < 1.0f) {
                        }
                    }
                }
                TL_messages_editMessage tL_messages_editMessage = new TL_messages_editMessage();
                tL_messages_editMessage.peer = getMessagesController().getInputPeer((int) sharingLocationInfo.did);
                tL_messages_editMessage.id = sharingLocationInfo.mid;
                tL_messages_editMessage.flags |= 16384;
                tL_messages_editMessage.media = new TL_inputMediaGeoLive();
                InputMedia inputMedia = tL_messages_editMessage.media;
                inputMedia.stopped = false;
                inputMedia.geo_point = new TL_inputGeoPoint();
                tL_messages_editMessage.media.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                tL_messages_editMessage.media.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
                this.requests.put(new int[]{getConnectionsManager().sendRequest(tL_messages_editMessage, new -$$Lambda$LocationController$nSJH9qZNTlSrmnG1la3CG2Fuy4c(this, sharingLocationInfo, r3))}[0], 0);
            }
            getConnectionsManager().resumeNetworkMaybe();
            if (shouldStopGps()) {
                stop(false);
            }
        }
    }

    public /* synthetic */ void lambda$broadcastLastKnownLocation$6$LocationController(SharingLocationInfo sharingLocationInfo, int[] iArr, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            if (tL_error.text.equals("MESSAGE_ID_INVALID")) {
                this.sharingLocations.remove(sharingLocationInfo);
                this.sharingLocationsMap.remove(sharingLocationInfo.did);
                saveSharingLocation(sharingLocationInfo, 1);
                this.requests.delete(iArr[0]);
                AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$uZY486pz9eQ2-w-VIu3xaKyapsU(this, sharingLocationInfo));
            }
            return;
        }
        Updates updates = (Updates) tLObject;
        Object obj = null;
        for (int i = 0; i < updates.updates.size(); i++) {
            Update update = (Update) updates.updates.get(i);
            if (update instanceof TL_updateEditMessage) {
                sharingLocationInfo.messageObject.messageOwner = ((TL_updateEditMessage) update).message;
            } else if (update instanceof TL_updateEditChannelMessage) {
                sharingLocationInfo.messageObject.messageOwner = ((TL_updateEditChannelMessage) update).message;
            } else {
            }
            obj = 1;
        }
        if (obj != null) {
            saveSharingLocation(sharingLocationInfo, 0);
        }
        getMessagesController().processUpdates(updates, false);
    }

    public /* synthetic */ void lambda$null$5$LocationController(SharingLocationInfo sharingLocationInfo) {
        this.sharingLocationsUI.remove(sharingLocationInfo);
        this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private boolean shouldStopGps() {
        return SystemClock.uptimeMillis() > this.locationEndWatchTime;
    }

    /* Access modifiers changed, original: protected */
    public void setNewLocationEndWatchTime() {
        if (!this.sharingLocations.isEmpty()) {
            this.locationEndWatchTime = SystemClock.uptimeMillis() + 65000;
            start();
        }
    }

    /* Access modifiers changed, original: protected */
    public void update() {
        if (!this.sharingLocations.isEmpty()) {
            boolean z = false;
            int i = 0;
            while (i < this.sharingLocations.size()) {
                SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocations.get(i);
                if (sharingLocationInfo.stopTime <= getConnectionsManager().getCurrentTime()) {
                    this.sharingLocations.remove(i);
                    this.sharingLocationsMap.remove(sharingLocationInfo.did);
                    saveSharingLocation(sharingLocationInfo, 1);
                    AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$o6WL-kB1GZyY8AhL0HNz3oaedxk(this, sharingLocationInfo));
                    i--;
                }
                i++;
            }
            if (this.started) {
                long uptimeMillis = SystemClock.uptimeMillis();
                if (this.lastLocationByGoogleMaps || Math.abs(this.lastLocationStartTime - uptimeMillis) > 10000 || shouldSendLocationNow()) {
                    this.lastLocationByGoogleMaps = false;
                    this.locationSentSinceLastGoogleMapUpdate = true;
                    if (SystemClock.uptimeMillis() - this.lastLocationSendTime > 2000) {
                        z = true;
                    }
                    this.lastLocationStartTime = uptimeMillis;
                    this.lastLocationSendTime = SystemClock.uptimeMillis();
                    broadcastLastKnownLocation(z);
                }
            } else if (Math.abs(this.lastLocationSendTime - SystemClock.uptimeMillis()) > 30000) {
                this.lastLocationStartTime = SystemClock.uptimeMillis();
                start();
            }
        }
    }

    public /* synthetic */ void lambda$update$7$LocationController(SharingLocationInfo sharingLocationInfo) {
        this.sharingLocationsUI.remove(sharingLocationInfo);
        this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private boolean shouldSendLocationNow() {
        if (shouldStopGps() && Math.abs(this.lastLocationSendTime - SystemClock.uptimeMillis()) >= 2000) {
            return true;
        }
        return false;
    }

    public void cleanup() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        this.locationsCache.clear();
        this.cacheRequests.clear();
        this.cachedNearbyUsers.clear();
        this.cachedNearbyChats.clear();
        this.lastReadLocationTime.clear();
        stopService();
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$XPRFqZemQqulaF1_J0ennNAoj_8(this));
    }

    public /* synthetic */ void lambda$cleanup$8$LocationController() {
        this.locationEndWatchTime = 0;
        this.requests.clear();
        this.sharingLocationsMap.clear();
        this.sharingLocations.clear();
        setLastKnownLocation(null);
        stop(true);
    }

    private void setLastKnownLocation(Location location) {
        this.lastKnownLocation = location;
        if (this.lastKnownLocation != null) {
            AndroidUtilities.runOnUIThread(-$$Lambda$LocationController$-l1kKN3fxUe0FY0l1iAxTz7FYBE.INSTANCE);
        }
    }

    public void setCachedNearbyUsersAndChats(ArrayList<TL_peerLocated> arrayList, ArrayList<TL_peerLocated> arrayList2) {
        this.cachedNearbyUsers = new ArrayList(arrayList);
        this.cachedNearbyChats = new ArrayList(arrayList2);
    }

    public ArrayList<TL_peerLocated> getCachedNearbyUsers() {
        return this.cachedNearbyUsers;
    }

    public ArrayList<TL_peerLocated> getCachedNearbyChats() {
        return this.cachedNearbyChats;
    }

    /* Access modifiers changed, original: protected */
    public void addSharingLocation(long j, int i, int i2, Message message) {
        SharingLocationInfo sharingLocationInfo = new SharingLocationInfo();
        sharingLocationInfo.did = j;
        sharingLocationInfo.mid = i;
        sharingLocationInfo.period = i2;
        i = this.currentAccount;
        sharingLocationInfo.account = i;
        sharingLocationInfo.messageObject = new MessageObject(i, message, false);
        sharingLocationInfo.stopTime = getConnectionsManager().getCurrentTime() + i2;
        SharingLocationInfo sharingLocationInfo2 = (SharingLocationInfo) this.sharingLocationsMap.get(j);
        this.sharingLocationsMap.put(j, sharingLocationInfo);
        if (sharingLocationInfo2 != null) {
            this.sharingLocations.remove(sharingLocationInfo2);
        }
        this.sharingLocations.add(sharingLocationInfo);
        saveSharingLocation(sharingLocationInfo, 0);
        this.lastLocationSendTime = (SystemClock.uptimeMillis() - 30000) + 5000;
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$Pu-U11hvPEurl1Ab6Bm2sYLQ1rg(this, sharingLocationInfo2, sharingLocationInfo));
    }

    public /* synthetic */ void lambda$addSharingLocation$10$LocationController(SharingLocationInfo sharingLocationInfo, SharingLocationInfo sharingLocationInfo2) {
        if (sharingLocationInfo != null) {
            this.sharingLocationsUI.remove(sharingLocationInfo);
        }
        this.sharingLocationsUI.add(sharingLocationInfo2);
        this.sharingLocationsMapUI.put(sharingLocationInfo2.did, sharingLocationInfo2);
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public boolean isSharingLocation(long j) {
        return this.sharingLocationsMapUI.indexOfKey(j) >= 0;
    }

    public SharingLocationInfo getSharingLocationInfo(long j) {
        return (SharingLocationInfo) this.sharingLocationsMapUI.get(j);
    }

    private void loadSharingLocations() {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$LocationController$8916hgvxJMGvWbZ-RkJNejb8f5s(this));
    }

    public /* synthetic */ void lambda$loadSharingLocations$14$LocationController() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        try {
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT uid, mid, date, period, message FROM sharing_locations WHERE 1", new Object[0]);
            while (queryFinalized.next()) {
                SharingLocationInfo sharingLocationInfo = new SharingLocationInfo();
                sharingLocationInfo.did = queryFinalized.longValue(0);
                sharingLocationInfo.mid = queryFinalized.intValue(1);
                sharingLocationInfo.stopTime = queryFinalized.intValue(2);
                sharingLocationInfo.period = queryFinalized.intValue(3);
                sharingLocationInfo.account = this.currentAccount;
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(4);
                if (byteBufferValue != null) {
                    sharingLocationInfo.messageObject = new MessageObject(this.currentAccount, Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), false);
                    MessagesStorage.addUsersAndChatsFromMessage(sharingLocationInfo.messageObject.messageOwner, arrayList4, arrayList5);
                    byteBufferValue.reuse();
                }
                arrayList.add(sharingLocationInfo);
                int i = (int) sharingLocationInfo.did;
                if (i != 0) {
                    if (i < 0) {
                        i = -i;
                        if (!arrayList5.contains(Integer.valueOf(i))) {
                            arrayList5.add(Integer.valueOf(i));
                        }
                    } else if (!arrayList4.contains(Integer.valueOf(i))) {
                        arrayList4.add(Integer.valueOf(i));
                    }
                }
            }
            queryFinalized.dispose();
            String str = ",";
            if (!arrayList5.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(str, arrayList5), arrayList3);
            }
            if (!arrayList4.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(str, arrayList4), arrayList2);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$HXSVxtcz_AJ2418UERNJBYw6aVs(this, arrayList2, arrayList3, arrayList));
        }
    }

    public /* synthetic */ void lambda$null$13$LocationController(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$w8s6gGWqhdkUzJCPdPHADZ3FjCo(this, arrayList3));
    }

    public /* synthetic */ void lambda$null$12$LocationController(ArrayList arrayList) {
        this.sharingLocations.addAll(arrayList);
        for (int i = 0; i < this.sharingLocations.size(); i++) {
            SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocations.get(i);
            this.sharingLocationsMap.put(sharingLocationInfo.did, sharingLocationInfo);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$lrxXfBfYqSiqR55QdtpWzhs_Ku0(this, arrayList));
    }

    public /* synthetic */ void lambda$null$11$LocationController(ArrayList arrayList) {
        this.sharingLocationsUI.addAll(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) arrayList.get(i);
            this.sharingLocationsMapUI.put(sharingLocationInfo.did, sharingLocationInfo);
        }
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void saveSharingLocation(SharingLocationInfo sharingLocationInfo, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$LocationController$J9E_uuS3DUnAFkelpP5g5PQKIYU(this, i, sharingLocationInfo));
    }

    public /* synthetic */ void lambda$saveSharingLocation$15$LocationController(int i, SharingLocationInfo sharingLocationInfo) {
        if (i == 2) {
            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM sharing_locations WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (i == 1) {
            if (sharingLocationInfo != null) {
                SQLiteDatabase database = getMessagesStorage().getDatabase();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("DELETE FROM sharing_locations WHERE uid = ");
                stringBuilder.append(sharingLocationInfo.did);
                database.executeFast(stringBuilder.toString()).stepThis().dispose();
            }
        } else if (sharingLocationInfo != null) {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO sharing_locations VALUES(?, ?, ?, ?, ?)");
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(sharingLocationInfo.messageObject.messageOwner.getObjectSize());
            sharingLocationInfo.messageObject.messageOwner.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, sharingLocationInfo.did);
            executeFast.bindInteger(2, sharingLocationInfo.mid);
            executeFast.bindInteger(3, sharingLocationInfo.stopTime);
            executeFast.bindInteger(4, sharingLocationInfo.period);
            executeFast.bindByteBuffer(5, nativeByteBuffer);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        }
    }

    public void removeSharingLocation(long j) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$bhlITlepFqu_ZDxAY0yJUBOfHlU(this, j));
    }

    public /* synthetic */ void lambda$removeSharingLocation$18$LocationController(long j) {
        SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocationsMap.get(j);
        this.sharingLocationsMap.remove(j);
        if (sharingLocationInfo != null) {
            TL_messages_editMessage tL_messages_editMessage = new TL_messages_editMessage();
            tL_messages_editMessage.peer = getMessagesController().getInputPeer((int) sharingLocationInfo.did);
            tL_messages_editMessage.id = sharingLocationInfo.mid;
            tL_messages_editMessage.flags |= 16384;
            tL_messages_editMessage.media = new TL_inputMediaGeoLive();
            InputMedia inputMedia = tL_messages_editMessage.media;
            inputMedia.stopped = true;
            inputMedia.geo_point = new TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(tL_messages_editMessage, new -$$Lambda$LocationController$PbHSWDn1agIXFAt6Sc4hQ_Ofzmw(this));
            this.sharingLocations.remove(sharingLocationInfo);
            saveSharingLocation(sharingLocationInfo, 1);
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$828RiNyjxaW5VCew1MqVmJiLHu4(this, sharingLocationInfo));
            if (this.sharingLocations.isEmpty()) {
                stop(true);
            }
        }
    }

    public /* synthetic */ void lambda$null$16$LocationController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$null$17$LocationController(SharingLocationInfo sharingLocationInfo) {
        this.sharingLocationsUI.remove(sharingLocationInfo);
        this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void startService() {
        try {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    private void stopService() {
        ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
    }

    public void removeAllLocationSharings() {
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$8LW2tMmCLASSNAMETPiH1zBI2T_FieO0(this));
    }

    public /* synthetic */ void lambda$removeAllLocationSharings$21$LocationController() {
        for (int i = 0; i < this.sharingLocations.size(); i++) {
            SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocations.get(i);
            TL_messages_editMessage tL_messages_editMessage = new TL_messages_editMessage();
            tL_messages_editMessage.peer = getMessagesController().getInputPeer((int) sharingLocationInfo.did);
            tL_messages_editMessage.id = sharingLocationInfo.mid;
            tL_messages_editMessage.flags |= 16384;
            tL_messages_editMessage.media = new TL_inputMediaGeoLive();
            InputMedia inputMedia = tL_messages_editMessage.media;
            inputMedia.stopped = true;
            inputMedia.geo_point = new TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(tL_messages_editMessage, new -$$Lambda$LocationController$WOQJw50pBRuqU0IK0JutTDDOJUs(this));
        }
        this.sharingLocations.clear();
        this.sharingLocationsMap.clear();
        saveSharingLocation(null, 2);
        stop(true);
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$e4lcjBidOjnMwxvJvlKpgftURHI(this));
    }

    public /* synthetic */ void lambda$null$19$LocationController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$null$20$LocationController() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        stopService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public void setGoogleMapLocation(Location location, boolean z) {
        if (location != null) {
            this.lastLocationByGoogleMaps = true;
            if (!z) {
                Location location2 = this.lastKnownLocation;
                if (location2 == null || location2.distanceTo(location) < 20.0f) {
                    if (this.locationSentSinceLastGoogleMapUpdate) {
                        this.lastLocationSendTime = (SystemClock.uptimeMillis() - 30000) + 20000;
                        this.locationSentSinceLastGoogleMapUpdate = false;
                    }
                    setLastKnownLocation(location);
                }
            }
            this.lastLocationSendTime = SystemClock.uptimeMillis() - 30000;
            this.locationSentSinceLastGoogleMapUpdate = false;
            setLastKnownLocation(location);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0022 A:{SYNTHETIC, Splitter:B:11:0x0022} */
    private void start() {
        /*
        r7 = this;
        r0 = r7.started;
        if (r0 == 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = android.os.SystemClock.uptimeMillis();
        r7.lastLocationStartTime = r0;
        r0 = 1;
        r7.started = r0;
        r1 = 0;
        r2 = r7.checkPlayServices();
        if (r2 == 0) goto L_0x001f;
    L_0x0015:
        r2 = r7.googleApiClient;	 Catch:{ all -> 0x001b }
        r2.connect();	 Catch:{ all -> 0x001b }
        goto L_0x0020;
    L_0x001b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x001f:
        r0 = 0;
    L_0x0020:
        if (r0 != 0) goto L_0x0078;
    L_0x0022:
        r1 = r7.locationManager;	 Catch:{ Exception -> 0x002f }
        r2 = "gps";
        r3 = 1;
        r5 = 0;
        r6 = r7.gpsLocationListener;	 Catch:{ Exception -> 0x002f }
        r1.requestLocationUpdates(r2, r3, r5, r6);	 Catch:{ Exception -> 0x002f }
        goto L_0x0033;
    L_0x002f:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0033:
        r1 = r7.locationManager;	 Catch:{ Exception -> 0x0040 }
        r2 = "network";
        r3 = 1;
        r5 = 0;
        r6 = r7.networkLocationListener;	 Catch:{ Exception -> 0x0040 }
        r1.requestLocationUpdates(r2, r3, r5, r6);	 Catch:{ Exception -> 0x0040 }
        goto L_0x0044;
    L_0x0040:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0044:
        r1 = r7.locationManager;	 Catch:{ Exception -> 0x0051 }
        r2 = "passive";
        r3 = 1;
        r5 = 0;
        r6 = r7.passiveLocationListener;	 Catch:{ Exception -> 0x0051 }
        r1.requestLocationUpdates(r2, r3, r5, r6);	 Catch:{ Exception -> 0x0051 }
        goto L_0x0055;
    L_0x0051:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0055:
        r0 = r7.lastKnownLocation;
        if (r0 != 0) goto L_0x0078;
    L_0x0059:
        r0 = r7.locationManager;	 Catch:{ Exception -> 0x0074 }
        r1 = "gps";
        r0 = r0.getLastKnownLocation(r1);	 Catch:{ Exception -> 0x0074 }
        r7.setLastKnownLocation(r0);	 Catch:{ Exception -> 0x0074 }
        r0 = r7.lastKnownLocation;	 Catch:{ Exception -> 0x0074 }
        if (r0 != 0) goto L_0x0078;
    L_0x0068:
        r0 = r7.locationManager;	 Catch:{ Exception -> 0x0074 }
        r1 = "network";
        r0 = r0.getLastKnownLocation(r1);	 Catch:{ Exception -> 0x0074 }
        r7.setLastKnownLocation(r0);	 Catch:{ Exception -> 0x0074 }
        goto L_0x0078;
    L_0x0074:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0078:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocationController.start():void");
    }

    private void stop(boolean z) {
        if (!this.lookingForPeopleNearby) {
            this.started = false;
            if (checkPlayServices()) {
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, this.fusedLocationListener);
                    this.googleApiClient.disconnect();
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            this.locationManager.removeUpdates(this.gpsLocationListener);
            if (z) {
                this.locationManager.removeUpdates(this.networkLocationListener);
                this.locationManager.removeUpdates(this.passiveLocationListener);
            }
        }
    }

    public void startLocationLookupForPeopleNearby(boolean z) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$mrKYHoVlPhfx4JxfvZd73TcMe-A(this, z));
    }

    public /* synthetic */ void lambda$startLocationLookupForPeopleNearby$22$LocationController(boolean z) {
        this.lookingForPeopleNearby = z ^ 1;
        if (this.lookingForPeopleNearby) {
            start();
        } else if (this.sharingLocations.isEmpty()) {
            stop(true);
        }
    }

    public Location getLastKnownLocation() {
        return this.lastKnownLocation;
    }

    public void loadLiveLocations(long j) {
        if (this.cacheRequests.indexOfKey(j) < 0) {
            this.cacheRequests.put(j, Boolean.valueOf(true));
            TL_messages_getRecentLocations tL_messages_getRecentLocations = new TL_messages_getRecentLocations();
            tL_messages_getRecentLocations.peer = getMessagesController().getInputPeer((int) j);
            tL_messages_getRecentLocations.limit = 100;
            getConnectionsManager().sendRequest(tL_messages_getRecentLocations, new -$$Lambda$LocationController$KJ6mbZv5SMV5OdCf_3AIRNgyiw4(this, j));
        }
    }

    public /* synthetic */ void lambda$loadLiveLocations$24$LocationController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$VFCl2YLno1sfdxoNp2r4jL7b1LY(this, j, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$23$LocationController(long j, TLObject tLObject) {
        this.cacheRequests.delete(j);
        messages_Messages messages_messages = (messages_Messages) tLObject;
        int i = 0;
        while (i < messages_messages.messages.size()) {
            if (!(((Message) messages_messages.messages.get(i)).media instanceof TL_messageMediaGeoLive)) {
                messages_messages.messages.remove(i);
                i--;
            }
            i++;
        }
        getMessagesStorage().putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
        getMessagesController().putUsers(messages_messages.users, false);
        getMessagesController().putChats(messages_messages.chats, false);
        this.locationsCache.put(j, messages_messages.messages);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(j), Integer.valueOf(this.currentAccount));
    }

    public void markLiveLoactionsAsRead(long j) {
        int i = (int) j;
        if (i != 0) {
            ArrayList arrayList = (ArrayList) this.locationsCache.get(j);
            if (!(arrayList.isEmpty() || arrayList == null)) {
                Integer num = (Integer) this.lastReadLocationTime.get(j);
                int uptimeMillis = (int) (SystemClock.uptimeMillis() / 1000);
                if (num == null || num.intValue() + 60 <= uptimeMillis) {
                    int i2;
                    TLObject tL_channels_readMessageContents;
                    this.lastReadLocationTime.put(j, Integer.valueOf(uptimeMillis));
                    int i3 = 0;
                    if (i < 0) {
                        i2 = -i;
                        if (ChatObject.isChannel(i2, this.currentAccount)) {
                            tL_channels_readMessageContents = new TL_channels_readMessageContents();
                            int size = arrayList.size();
                            while (i3 < size) {
                                tL_channels_readMessageContents.id.add(Integer.valueOf(((Message) arrayList.get(i3)).id));
                                i3++;
                            }
                            tL_channels_readMessageContents.channel = getMessagesController().getInputChannel(i2);
                            getConnectionsManager().sendRequest(tL_channels_readMessageContents, new -$$Lambda$LocationController$22_VP_PsyLV189MEr2nl7-p4dOw(this));
                        }
                    }
                    tL_channels_readMessageContents = new TL_messages_readMessageContents();
                    i2 = arrayList.size();
                    while (i3 < i2) {
                        tL_channels_readMessageContents.id.add(Integer.valueOf(((Message) arrayList.get(i3)).id));
                        i3++;
                    }
                    getConnectionsManager().sendRequest(tL_channels_readMessageContents, new -$$Lambda$LocationController$22_VP_PsyLV189MEr2nl7-p4dOw(this));
                }
            }
        }
    }

    public /* synthetic */ void lambda$markLiveLoactionsAsRead$25$LocationController(TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_messages_affectedMessages) {
            TL_messages_affectedMessages tL_messages_affectedMessages = (TL_messages_affectedMessages) tLObject;
            getMessagesController().processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    public static int getLocationsCount() {
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            i += getInstance(i2).sharingLocationsUI.size();
        }
        return i;
    }

    public static void fetchLocationAddress(Location location, LocationFetchCallback locationFetchCallback) {
        if (locationFetchCallback != null) {
            Runnable runnable = (Runnable) callbacks.get(locationFetchCallback);
            if (runnable != null) {
                Utilities.globalQueue.cancelRunnable(runnable);
                callbacks.remove(locationFetchCallback);
            }
            if (location == null) {
                if (locationFetchCallback != null) {
                    locationFetchCallback.onLocationAddressAvailable(null, null, null);
                }
                return;
            }
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            -$$Lambda$LocationController$MlYv6UWfkd6XTrF1ctqH1Ck5v5E -__lambda_locationcontroller_mlyv6uwfkd6xtrf1ctqh1ck5v5e = new -$$Lambda$LocationController$MlYv6UWfkd6XTrF1ctqH1Ck5v5E(location, locationFetchCallback);
            dispatchQueue.postRunnable(-__lambda_locationcontroller_mlyv6uwfkd6xtrf1ctqh1ck5v5e, 300);
            callbacks.put(locationFetchCallback, -__lambda_locationcontroller_mlyv6uwfkd6xtrf1ctqh1ck5v5e);
        }
    }

    static /* synthetic */ void lambda$fetchLocationAddress$27(Location location, LocationFetchCallback locationFetchCallback) {
        String str = "Unknown address (%f,%f)";
        String subAdminArea;
        try {
            List fromLocation = new Geocoder(ApplicationLoader.applicationContext, LocaleController.getInstance().getSystemDefaultLocale()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (fromLocation.size() > 0) {
                Object obj;
                Address address = (Address) fromLocation.get(0);
                StringBuilder stringBuilder = new StringBuilder();
                StringBuilder stringBuilder2 = new StringBuilder();
                String subThoroughfare = address.getSubThoroughfare();
                if (TextUtils.isEmpty(subThoroughfare)) {
                    obj = null;
                } else {
                    stringBuilder.append(subThoroughfare);
                    obj = 1;
                }
                String thoroughfare = address.getThoroughfare();
                String str2 = ", ";
                if (!TextUtils.isEmpty(thoroughfare)) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(str2);
                    }
                    stringBuilder.append(thoroughfare);
                    obj = 1;
                }
                if (obj == null) {
                    thoroughfare = address.getAdminArea();
                    if (!TextUtils.isEmpty(thoroughfare)) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(str2);
                        }
                        stringBuilder.append(thoroughfare);
                    }
                    thoroughfare = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(thoroughfare)) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(str2);
                        }
                        stringBuilder.append(thoroughfare);
                    }
                }
                thoroughfare = address.getLocality();
                if (!TextUtils.isEmpty(thoroughfare)) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(str2);
                    }
                    stringBuilder.append(thoroughfare);
                }
                thoroughfare = address.getCountryName();
                if (!TextUtils.isEmpty(thoroughfare)) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(str2);
                    }
                    stringBuilder.append(thoroughfare);
                }
                thoroughfare = address.getCountryName();
                if (!TextUtils.isEmpty(thoroughfare)) {
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2.append(str2);
                    }
                    stringBuilder2.append(thoroughfare);
                }
                thoroughfare = address.getLocality();
                if (!TextUtils.isEmpty(thoroughfare)) {
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2.append(str2);
                    }
                    stringBuilder2.append(thoroughfare);
                }
                if (obj == null) {
                    subThoroughfare = address.getAdminArea();
                    if (!TextUtils.isEmpty(subThoroughfare)) {
                        if (stringBuilder2.length() > 0) {
                            stringBuilder2.append(str2);
                        }
                        stringBuilder2.append(subThoroughfare);
                    }
                    subAdminArea = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(subAdminArea)) {
                        if (stringBuilder2.length() > 0) {
                            stringBuilder2.append(str2);
                        }
                        stringBuilder2.append(subAdminArea);
                    }
                }
                subAdminArea = stringBuilder.toString();
                str = stringBuilder2.toString();
                AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$f_wQSf0Yb70kecs4vFip2jNqUfk(locationFetchCallback, subAdminArea, str, location));
            }
            subAdminArea = String.format(Locale.US, str, new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
            str = subAdminArea;
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$f_wQSf0Yb70kecs4vFip2jNqUfk(locationFetchCallback, subAdminArea, str, location));
        } catch (Exception unused) {
            subAdminArea = String.format(Locale.US, str, new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
        }
    }

    static /* synthetic */ void lambda$null$26(LocationFetchCallback locationFetchCallback, String str, String str2, Location location) {
        callbacks.remove(locationFetchCallback);
        if (locationFetchCallback != null) {
            locationFetchCallback.onLocationAddressAvailable(str, str2, location);
        }
    }
}
