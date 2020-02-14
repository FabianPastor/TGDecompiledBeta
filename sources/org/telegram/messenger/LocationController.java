package org.telegram.messenger;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseIntArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
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
import org.telegram.messenger.LocationController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class LocationController extends BaseController implements NotificationCenter.NotificationCenterDelegate, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int BACKGROUD_UPDATE_TIME = 30000;
    private static final long FASTEST_INTERVAL = 1000;
    private static final int FOREGROUND_UPDATE_TIME = 20000;
    private static volatile LocationController[] Instance = new LocationController[3];
    private static final int LOCATION_ACQUIRE_TIME = 10000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int SEND_NEW_LOCATION_TIME = 2000;
    private static final long UPDATE_INTERVAL = 1000;
    private static final int WATCH_LOCATION_TIMEOUT = 65000;
    private static HashMap<LocationFetchCallback, Runnable> callbacks = new HashMap<>();
    private LongSparseArray<Boolean> cacheRequests = new LongSparseArray<>();
    private ArrayList<TLRPC.TL_peerLocated> cachedNearbyChats = new ArrayList<>();
    private ArrayList<TLRPC.TL_peerLocated> cachedNearbyUsers = new ArrayList<>();
    private FusedLocationListener fusedLocationListener = new FusedLocationListener();
    private GoogleApiClient googleApiClient;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener();
    /* access modifiers changed from: private */
    public Location lastKnownLocation;
    private boolean lastLocationByGoogleMaps;
    /* access modifiers changed from: private */
    public long lastLocationSendTime;
    private long lastLocationStartTime;
    private LongSparseArray<Integer> lastReadLocationTime = new LongSparseArray<>();
    private long locationEndWatchTime;
    private LocationManager locationManager = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location"));
    private LocationRequest locationRequest;
    private boolean locationSentSinceLastGoogleMapUpdate = true;
    public LongSparseArray<ArrayList<TLRPC.Message>> locationsCache = new LongSparseArray<>();
    private boolean lookingForPeopleNearby;
    /* access modifiers changed from: private */
    public GpsLocationListener networkLocationListener = new GpsLocationListener();
    /* access modifiers changed from: private */
    public GpsLocationListener passiveLocationListener = new GpsLocationListener();
    private Boolean playServicesAvailable;
    private SparseIntArray requests = new SparseIntArray();
    private boolean shareMyCurrentLocation;
    private ArrayList<SharingLocationInfo> sharingLocations = new ArrayList<>();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMap = new LongSparseArray<>();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMapUI = new LongSparseArray<>();
    public ArrayList<SharingLocationInfo> sharingLocationsUI = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean started;
    private boolean wasConnectedToPlayServices;

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

    static /* synthetic */ void lambda$broadcastLastKnownLocation$8(TLObject tLObject, TLRPC.TL_error tL_error) {
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
                    long unused = LocationController.this.lastLocationSendTime = (SystemClock.elapsedRealtime() - 30000) + 5000;
                }
            }
        }
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

    public LocationController(int i) {
        super(i);
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(ApplicationLoader.applicationContext);
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        this.googleApiClient = builder.build();
        this.locationRequest = new LocationRequest();
        this.locationRequest.setPriority(100);
        this.locationRequest.setInterval(1000);
        this.locationRequest.setFastestInterval(1000);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LocationController.this.lambda$new$0$LocationController();
            }
        });
        loadSharingLocations();
    }

    public /* synthetic */ void lambda$new$0$LocationController() {
        LocationController locationController = getAccountInstance().getLocationController();
        getNotificationCenter().addObserver(locationController, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(locationController, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(locationController, NotificationCenter.replaceMessagesObjects);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ArrayList arrayList;
        ArrayList arrayList2;
        boolean z;
        if (i == NotificationCenter.didReceiveNewMessages) {
            if (!objArr[2].booleanValue()) {
                long longValue = objArr[0].longValue();
                if (isSharingLocation(longValue) && (arrayList2 = this.locationsCache.get(longValue)) != null) {
                    ArrayList arrayList3 = objArr[1];
                    boolean z2 = false;
                    for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                        MessageObject messageObject = (MessageObject) arrayList3.get(i3);
                        if (messageObject.isLiveLocation()) {
                            int i4 = 0;
                            while (true) {
                                if (i4 >= arrayList2.size()) {
                                    z = false;
                                    break;
                                }
                                int i5 = ((TLRPC.Message) arrayList2.get(i4)).from_id;
                                TLRPC.Message message = messageObject.messageOwner;
                                if (i5 == message.from_id) {
                                    arrayList2.set(i4, message);
                                    z = true;
                                    break;
                                }
                                i4++;
                            }
                            if (!z) {
                                arrayList2.add(messageObject.messageOwner);
                            }
                            z2 = true;
                        }
                    }
                    if (z2) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(longValue), Integer.valueOf(this.currentAccount));
                    }
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!objArr[2].booleanValue() && !this.sharingLocationsUI.isEmpty()) {
                ArrayList arrayList4 = objArr[0];
                int intValue = objArr[1].intValue();
                ArrayList arrayList5 = null;
                for (int i6 = 0; i6 < this.sharingLocationsUI.size(); i6++) {
                    SharingLocationInfo sharingLocationInfo = this.sharingLocationsUI.get(i6);
                    MessageObject messageObject2 = sharingLocationInfo.messageObject;
                    if (intValue == (messageObject2 != null ? messageObject2.getChannelId() : 0) && arrayList4.contains(Integer.valueOf(sharingLocationInfo.mid))) {
                        if (arrayList5 == null) {
                            arrayList5 = new ArrayList();
                        }
                        arrayList5.add(Long.valueOf(sharingLocationInfo.did));
                    }
                }
                if (arrayList5 != null) {
                    for (int i7 = 0; i7 < arrayList5.size(); i7++) {
                        removeSharingLocation(((Long) arrayList5.get(i7)).longValue());
                    }
                }
            }
        } else if (i == NotificationCenter.replaceMessagesObjects) {
            long longValue2 = objArr[0].longValue();
            if (isSharingLocation(longValue2) && (arrayList = this.locationsCache.get(longValue2)) != null) {
                ArrayList arrayList6 = objArr[1];
                boolean z3 = false;
                for (int i8 = 0; i8 < arrayList6.size(); i8++) {
                    MessageObject messageObject3 = (MessageObject) arrayList6.get(i8);
                    int i9 = 0;
                    while (true) {
                        if (i9 >= arrayList.size()) {
                            break;
                        } else if (((TLRPC.Message) arrayList.get(i9)).from_id == messageObject3.messageOwner.from_id) {
                            if (!messageObject3.isLiveLocation()) {
                                arrayList.remove(i9);
                            } else {
                                arrayList.set(i9, messageObject3.messageOwner);
                            }
                            z3 = true;
                        } else {
                            i9++;
                        }
                    }
                }
                if (z3) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(longValue2), Integer.valueOf(this.currentAccount));
                }
            }
        }
    }

    public void onConnected(Bundle bundle) {
        this.wasConnectedToPlayServices = true;
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                builder.addLocationRequest(this.locationRequest);
                LocationServices.SettingsApi.checkLocationSettings(this.googleApiClient, builder.build()).setResultCallback(new ResultCallback() {
                    public final void onResult(Result result) {
                        LocationController.this.lambda$onConnected$4$LocationController((LocationSettingsResult) result);
                    }
                });
                return;
            }
            startFusedLocationRequest(true);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public /* synthetic */ void lambda$onConnected$4$LocationController(LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        int statusCode = status.getStatusCode();
        if (statusCode == 0) {
            startFusedLocationRequest(true);
        } else if (statusCode == 6) {
            Utilities.stageQueue.postRunnable(new Runnable(status) {
                private final /* synthetic */ Status f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationController.this.lambda$null$2$LocationController(this.f$1);
                }
            });
        } else if (statusCode == 8502) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public final void run() {
                    LocationController.this.lambda$null$3$LocationController();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$2$LocationController(Status status) {
        if (this.lookingForPeopleNearby || !this.sharingLocations.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(status) {
                private final /* synthetic */ Status f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationController.this.lambda$null$1$LocationController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$1$LocationController(Status status) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowPlayServicesAlert, status);
    }

    public /* synthetic */ void lambda$null$3$LocationController() {
        this.playServicesAvailable = false;
        try {
            this.googleApiClient.disconnect();
            start();
        } catch (Throwable unused) {
        }
    }

    public void startFusedLocationRequest(boolean z) {
        Utilities.stageQueue.postRunnable(new Runnable(z) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$startFusedLocationRequest$5$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$startFusedLocationRequest$5$LocationController(boolean z) {
        if (!z) {
            this.playServicesAvailable = false;
        }
        if (!this.shareMyCurrentLocation && !this.lookingForPeopleNearby && this.sharingLocations.isEmpty()) {
            return;
        }
        if (z) {
            try {
                setLastKnownLocation(LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient));
                LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient, this.locationRequest, this.fusedLocationListener);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else {
            start();
        }
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!this.wasConnectedToPlayServices) {
            this.playServicesAvailable = false;
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
            if (this.requests.size() != 0) {
                if (z) {
                    for (int i = 0; i < this.requests.size(); i++) {
                        getConnectionsManager().cancelRequest(this.requests.keyAt(i), false);
                    }
                }
                this.requests.clear();
            }
            if (!this.sharingLocations.isEmpty()) {
                int currentTime = getConnectionsManager().getCurrentTime();
                float[] fArr = new float[1];
                for (int i2 = 0; i2 < this.sharingLocations.size(); i2++) {
                    SharingLocationInfo sharingLocationInfo = this.sharingLocations.get(i2);
                    TLRPC.Message message = sharingLocationInfo.messageObject.messageOwner;
                    TLRPC.MessageMedia messageMedia = message.media;
                    if (!(messageMedia == null || messageMedia.geo == null)) {
                        int i3 = message.edit_date;
                        if (i3 == 0) {
                            i3 = message.date;
                        }
                        TLRPC.GeoPoint geoPoint = sharingLocationInfo.messageObject.messageOwner.media.geo;
                        if (Math.abs(currentTime - i3) < 10) {
                            Location.distanceBetween(geoPoint.lat, geoPoint._long, this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude(), fArr);
                            if (fArr[0] < 1.0f) {
                            }
                        }
                    }
                    TLRPC.TL_messages_editMessage tL_messages_editMessage = new TLRPC.TL_messages_editMessage();
                    tL_messages_editMessage.peer = getMessagesController().getInputPeer((int) sharingLocationInfo.did);
                    tL_messages_editMessage.id = sharingLocationInfo.mid;
                    tL_messages_editMessage.flags |= 16384;
                    tL_messages_editMessage.media = new TLRPC.TL_inputMediaGeoLive();
                    TLRPC.InputMedia inputMedia = tL_messages_editMessage.media;
                    inputMedia.stopped = false;
                    inputMedia.geo_point = new TLRPC.TL_inputGeoPoint();
                    tL_messages_editMessage.media.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                    tL_messages_editMessage.media.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
                    int[] iArr = {getConnectionsManager().sendRequest(tL_messages_editMessage, new RequestDelegate(sharingLocationInfo, iArr) {
                        private final /* synthetic */ LocationController.SharingLocationInfo f$1;
                        private final /* synthetic */ int[] f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LocationController.this.lambda$broadcastLastKnownLocation$7$LocationController(this.f$1, this.f$2, tLObject, tL_error);
                        }
                    })};
                    this.requests.put(iArr[0], 0);
                }
            }
            if (this.shareMyCurrentLocation) {
                UserConfig userConfig = getUserConfig();
                userConfig.lastMyLocationShareTime = (int) (System.currentTimeMillis() / 1000);
                userConfig.saveConfig(false);
                TLRPC.TL_contacts_getLocated tL_contacts_getLocated = new TLRPC.TL_contacts_getLocated();
                tL_contacts_getLocated.geo_point = new TLRPC.TL_inputGeoPoint();
                tL_contacts_getLocated.geo_point.lat = this.lastKnownLocation.getLatitude();
                tL_contacts_getLocated.geo_point._long = this.lastKnownLocation.getLongitude();
                tL_contacts_getLocated.background = true;
                getConnectionsManager().sendRequest(tL_contacts_getLocated, $$Lambda$LocationController$gC7iaeGr_Tt3Akk8QzxZYRjn6Jk.INSTANCE);
            }
            getConnectionsManager().resumeNetworkMaybe();
            if (shouldStopGps() || this.shareMyCurrentLocation) {
                this.shareMyCurrentLocation = false;
                stop(false);
            }
        }
    }

    public /* synthetic */ void lambda$broadcastLastKnownLocation$7$LocationController(SharingLocationInfo sharingLocationInfo, int[] iArr, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            boolean z = false;
            for (int i = 0; i < updates.updates.size(); i++) {
                TLRPC.Update update = updates.updates.get(i);
                if (update instanceof TLRPC.TL_updateEditMessage) {
                    sharingLocationInfo.messageObject.messageOwner = ((TLRPC.TL_updateEditMessage) update).message;
                } else if (update instanceof TLRPC.TL_updateEditChannelMessage) {
                    sharingLocationInfo.messageObject.messageOwner = ((TLRPC.TL_updateEditChannelMessage) update).message;
                }
                z = true;
            }
            if (z) {
                saveSharingLocation(sharingLocationInfo, 0);
            }
            getMessagesController().processUpdates(updates, false);
        } else if (tL_error.text.equals("MESSAGE_ID_INVALID")) {
            this.sharingLocations.remove(sharingLocationInfo);
            this.sharingLocationsMap.remove(sharingLocationInfo.did);
            saveSharingLocation(sharingLocationInfo, 1);
            this.requests.delete(iArr[0]);
            AndroidUtilities.runOnUIThread(new Runnable(sharingLocationInfo) {
                private final /* synthetic */ LocationController.SharingLocationInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationController.this.lambda$null$6$LocationController(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$6$LocationController(SharingLocationInfo sharingLocationInfo) {
        this.sharingLocationsUI.remove(sharingLocationInfo);
        this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private boolean shouldStopGps() {
        return SystemClock.elapsedRealtime() > this.locationEndWatchTime;
    }

    /* access modifiers changed from: protected */
    public void setNewLocationEndWatchTime() {
        if (!this.sharingLocations.isEmpty()) {
            this.locationEndWatchTime = SystemClock.elapsedRealtime() + 65000;
            start();
        }
    }

    /* access modifiers changed from: protected */
    public void update() {
        UserConfig userConfig = getUserConfig();
        if (ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePaused && !this.shareMyCurrentLocation && userConfig.isClientActivated() && userConfig.isConfigLoaded() && userConfig.sharingMyLocationUntil != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) userConfig.lastMyLocationShareTime)) >= 3600) {
            this.shareMyCurrentLocation = true;
        }
        boolean z = false;
        if (!this.sharingLocations.isEmpty()) {
            int i = 0;
            while (i < this.sharingLocations.size()) {
                SharingLocationInfo sharingLocationInfo = this.sharingLocations.get(i);
                if (sharingLocationInfo.stopTime <= getConnectionsManager().getCurrentTime()) {
                    this.sharingLocations.remove(i);
                    this.sharingLocationsMap.remove(sharingLocationInfo.did);
                    saveSharingLocation(sharingLocationInfo, 1);
                    AndroidUtilities.runOnUIThread(new Runnable(sharingLocationInfo) {
                        private final /* synthetic */ LocationController.SharingLocationInfo f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            LocationController.this.lambda$update$9$LocationController(this.f$1);
                        }
                    });
                    i--;
                }
                i++;
            }
        }
        if (this.started) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (this.lastLocationByGoogleMaps || Math.abs(this.lastLocationStartTime - elapsedRealtime) > 10000 || shouldSendLocationNow()) {
                this.lastLocationByGoogleMaps = false;
                this.locationSentSinceLastGoogleMapUpdate = true;
                if (SystemClock.elapsedRealtime() - this.lastLocationSendTime > 2000) {
                    z = true;
                }
                this.lastLocationStartTime = elapsedRealtime;
                this.lastLocationSendTime = SystemClock.elapsedRealtime();
                broadcastLastKnownLocation(z);
            }
        } else if (this.sharingLocations.isEmpty() && !this.shareMyCurrentLocation) {
        } else {
            if (this.shareMyCurrentLocation || Math.abs(this.lastLocationSendTime - SystemClock.elapsedRealtime()) > 30000) {
                this.lastLocationStartTime = SystemClock.elapsedRealtime();
                start();
            }
        }
    }

    public /* synthetic */ void lambda$update$9$LocationController(SharingLocationInfo sharingLocationInfo) {
        this.sharingLocationsUI.remove(sharingLocationInfo);
        this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private boolean shouldSendLocationNow() {
        if (shouldStopGps() && Math.abs(this.lastLocationSendTime - SystemClock.elapsedRealtime()) >= 2000) {
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
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                LocationController.this.lambda$cleanup$10$LocationController();
            }
        });
    }

    public /* synthetic */ void lambda$cleanup$10$LocationController() {
        this.locationEndWatchTime = 0;
        this.requests.clear();
        this.sharingLocationsMap.clear();
        this.sharingLocations.clear();
        setLastKnownLocation((Location) null);
        stop(true);
    }

    /* access modifiers changed from: private */
    public void setLastKnownLocation(Location location) {
        this.lastKnownLocation = location;
        if (this.lastKnownLocation != null) {
            AndroidUtilities.runOnUIThread($$Lambda$LocationController$UFu6QmZ4dYgGAgZ8gnaudair6ZY.INSTANCE);
        }
    }

    public void setCachedNearbyUsersAndChats(ArrayList<TLRPC.TL_peerLocated> arrayList, ArrayList<TLRPC.TL_peerLocated> arrayList2) {
        this.cachedNearbyUsers = new ArrayList<>(arrayList);
        this.cachedNearbyChats = new ArrayList<>(arrayList2);
    }

    public ArrayList<TLRPC.TL_peerLocated> getCachedNearbyUsers() {
        return this.cachedNearbyUsers;
    }

    public ArrayList<TLRPC.TL_peerLocated> getCachedNearbyChats() {
        return this.cachedNearbyChats;
    }

    /* access modifiers changed from: protected */
    public void addSharingLocation(long j, int i, int i2, TLRPC.Message message) {
        SharingLocationInfo sharingLocationInfo = new SharingLocationInfo();
        sharingLocationInfo.did = j;
        sharingLocationInfo.mid = i;
        sharingLocationInfo.period = i2;
        int i3 = this.currentAccount;
        sharingLocationInfo.account = i3;
        sharingLocationInfo.messageObject = new MessageObject(i3, message, false);
        sharingLocationInfo.stopTime = getConnectionsManager().getCurrentTime() + i2;
        SharingLocationInfo sharingLocationInfo2 = this.sharingLocationsMap.get(j);
        this.sharingLocationsMap.put(j, sharingLocationInfo);
        if (sharingLocationInfo2 != null) {
            this.sharingLocations.remove(sharingLocationInfo2);
        }
        this.sharingLocations.add(sharingLocationInfo);
        saveSharingLocation(sharingLocationInfo, 0);
        this.lastLocationSendTime = (SystemClock.elapsedRealtime() - 30000) + 5000;
        AndroidUtilities.runOnUIThread(new Runnable(sharingLocationInfo2, sharingLocationInfo) {
            private final /* synthetic */ LocationController.SharingLocationInfo f$1;
            private final /* synthetic */ LocationController.SharingLocationInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LocationController.this.lambda$addSharingLocation$12$LocationController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$addSharingLocation$12$LocationController(SharingLocationInfo sharingLocationInfo, SharingLocationInfo sharingLocationInfo2) {
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
        return this.sharingLocationsMapUI.get(j);
    }

    private void loadSharingLocations() {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                LocationController.this.lambda$loadSharingLocations$16$LocationController();
            }
        });
    }

    public /* synthetic */ void lambda$loadSharingLocations$16$LocationController() {
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
                    sharingLocationInfo.messageObject = new MessageObject(this.currentAccount, TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), false);
                    MessagesStorage.addUsersAndChatsFromMessage(sharingLocationInfo.messageObject.messageOwner, arrayList4, arrayList5);
                    byteBufferValue.reuse();
                }
                arrayList.add(sharingLocationInfo);
                int i = (int) sharingLocationInfo.did;
                if (i != 0) {
                    if (i < 0) {
                        int i2 = -i;
                        if (!arrayList5.contains(Integer.valueOf(i2))) {
                            arrayList5.add(Integer.valueOf(i2));
                        }
                    } else if (!arrayList4.contains(Integer.valueOf(i))) {
                        arrayList4.add(Integer.valueOf(i));
                    }
                }
            }
            queryFinalized.dispose();
            if (!arrayList5.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", arrayList5), arrayList3);
            }
            if (!arrayList4.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", arrayList4), arrayList2);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList2, arrayList3, arrayList) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;
                private final /* synthetic */ ArrayList f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocationController.this.lambda$null$15$LocationController(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$15$LocationController(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        Utilities.stageQueue.postRunnable(new Runnable(arrayList3) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$null$14$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$14$LocationController(ArrayList arrayList) {
        this.sharingLocations.addAll(arrayList);
        for (int i = 0; i < this.sharingLocations.size(); i++) {
            SharingLocationInfo sharingLocationInfo = this.sharingLocations.get(i);
            this.sharingLocationsMap.put(sharingLocationInfo.did, sharingLocationInfo);
        }
        AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$null$13$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$13$LocationController(ArrayList arrayList) {
        this.sharingLocationsUI.addAll(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) arrayList.get(i);
            this.sharingLocationsMapUI.put(sharingLocationInfo.did, sharingLocationInfo);
        }
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void saveSharingLocation(SharingLocationInfo sharingLocationInfo, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(i, sharingLocationInfo) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ LocationController.SharingLocationInfo f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                LocationController.this.lambda$saveSharingLocation$17$LocationController(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$saveSharingLocation$17$LocationController(int i, SharingLocationInfo sharingLocationInfo) {
        if (i == 2) {
            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM sharing_locations WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (i == 1) {
            if (sharingLocationInfo != null) {
                SQLiteDatabase database = getMessagesStorage().getDatabase();
                database.executeFast("DELETE FROM sharing_locations WHERE uid = " + sharingLocationInfo.did).stepThis().dispose();
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
        Utilities.stageQueue.postRunnable(new Runnable(j) {
            private final /* synthetic */ long f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$removeSharingLocation$20$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$removeSharingLocation$20$LocationController(long j) {
        SharingLocationInfo sharingLocationInfo = this.sharingLocationsMap.get(j);
        this.sharingLocationsMap.remove(j);
        if (sharingLocationInfo != null) {
            TLRPC.TL_messages_editMessage tL_messages_editMessage = new TLRPC.TL_messages_editMessage();
            tL_messages_editMessage.peer = getMessagesController().getInputPeer((int) sharingLocationInfo.did);
            tL_messages_editMessage.id = sharingLocationInfo.mid;
            tL_messages_editMessage.flags |= 16384;
            tL_messages_editMessage.media = new TLRPC.TL_inputMediaGeoLive();
            TLRPC.InputMedia inputMedia = tL_messages_editMessage.media;
            inputMedia.stopped = true;
            inputMedia.geo_point = new TLRPC.TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(tL_messages_editMessage, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocationController.this.lambda$null$18$LocationController(tLObject, tL_error);
                }
            });
            this.sharingLocations.remove(sharingLocationInfo);
            saveSharingLocation(sharingLocationInfo, 1);
            AndroidUtilities.runOnUIThread(new Runnable(sharingLocationInfo) {
                private final /* synthetic */ LocationController.SharingLocationInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    LocationController.this.lambda$null$19$LocationController(this.f$1);
                }
            });
            if (this.sharingLocations.isEmpty()) {
                stop(true);
            }
        }
    }

    public /* synthetic */ void lambda$null$18$LocationController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$null$19$LocationController(SharingLocationInfo sharingLocationInfo) {
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
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                LocationController.this.lambda$removeAllLocationSharings$23$LocationController();
            }
        });
    }

    public /* synthetic */ void lambda$removeAllLocationSharings$23$LocationController() {
        for (int i = 0; i < this.sharingLocations.size(); i++) {
            SharingLocationInfo sharingLocationInfo = this.sharingLocations.get(i);
            TLRPC.TL_messages_editMessage tL_messages_editMessage = new TLRPC.TL_messages_editMessage();
            tL_messages_editMessage.peer = getMessagesController().getInputPeer((int) sharingLocationInfo.did);
            tL_messages_editMessage.id = sharingLocationInfo.mid;
            tL_messages_editMessage.flags |= 16384;
            tL_messages_editMessage.media = new TLRPC.TL_inputMediaGeoLive();
            TLRPC.InputMedia inputMedia = tL_messages_editMessage.media;
            inputMedia.stopped = true;
            inputMedia.geo_point = new TLRPC.TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(tL_messages_editMessage, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocationController.this.lambda$null$21$LocationController(tLObject, tL_error);
                }
            });
        }
        this.sharingLocations.clear();
        this.sharingLocationsMap.clear();
        saveSharingLocation((SharingLocationInfo) null, 2);
        stop(true);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                LocationController.this.lambda$null$22$LocationController();
            }
        });
    }

    public /* synthetic */ void lambda$null$21$LocationController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$null$22$LocationController() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        stopService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public void setGoogleMapLocation(Location location, boolean z) {
        Location location2;
        if (location != null) {
            this.lastLocationByGoogleMaps = true;
            if (z || ((location2 = this.lastKnownLocation) != null && location2.distanceTo(location) >= 20.0f)) {
                this.lastLocationSendTime = SystemClock.elapsedRealtime() - 30000;
                this.locationSentSinceLastGoogleMapUpdate = false;
            } else if (this.locationSentSinceLastGoogleMapUpdate) {
                this.lastLocationSendTime = (SystemClock.elapsedRealtime() - 30000) + 20000;
                this.locationSentSinceLastGoogleMapUpdate = false;
            }
            setLastKnownLocation(location);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0022 A[SYNTHETIC, Splitter:B:11:0x0022] */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void start() {
        /*
            r7 = this;
            boolean r0 = r7.started
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            long r0 = android.os.SystemClock.elapsedRealtime()
            r7.lastLocationStartTime = r0
            r0 = 1
            r7.started = r0
            r1 = 0
            boolean r2 = r7.checkPlayServices()
            if (r2 == 0) goto L_0x001f
            com.google.android.gms.common.api.GoogleApiClient r2 = r7.googleApiClient     // Catch:{ all -> 0x001b }
            r2.connect()     // Catch:{ all -> 0x001b }
            goto L_0x0020
        L_0x001b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x001f:
            r0 = 0
        L_0x0020:
            if (r0 != 0) goto L_0x0078
            android.location.LocationManager r1 = r7.locationManager     // Catch:{ Exception -> 0x002f }
            java.lang.String r2 = "gps"
            r3 = 1
            r5 = 0
            org.telegram.messenger.LocationController$GpsLocationListener r6 = r7.gpsLocationListener     // Catch:{ Exception -> 0x002f }
            r1.requestLocationUpdates(r2, r3, r5, r6)     // Catch:{ Exception -> 0x002f }
            goto L_0x0033
        L_0x002f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0033:
            android.location.LocationManager r1 = r7.locationManager     // Catch:{ Exception -> 0x0040 }
            java.lang.String r2 = "network"
            r3 = 1
            r5 = 0
            org.telegram.messenger.LocationController$GpsLocationListener r6 = r7.networkLocationListener     // Catch:{ Exception -> 0x0040 }
            r1.requestLocationUpdates(r2, r3, r5, r6)     // Catch:{ Exception -> 0x0040 }
            goto L_0x0044
        L_0x0040:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0044:
            android.location.LocationManager r1 = r7.locationManager     // Catch:{ Exception -> 0x0051 }
            java.lang.String r2 = "passive"
            r3 = 1
            r5 = 0
            org.telegram.messenger.LocationController$GpsLocationListener r6 = r7.passiveLocationListener     // Catch:{ Exception -> 0x0051 }
            r1.requestLocationUpdates(r2, r3, r5, r6)     // Catch:{ Exception -> 0x0051 }
            goto L_0x0055
        L_0x0051:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0055:
            android.location.Location r0 = r7.lastKnownLocation
            if (r0 != 0) goto L_0x0078
            android.location.LocationManager r0 = r7.locationManager     // Catch:{ Exception -> 0x0074 }
            java.lang.String r1 = "gps"
            android.location.Location r0 = r0.getLastKnownLocation(r1)     // Catch:{ Exception -> 0x0074 }
            r7.setLastKnownLocation(r0)     // Catch:{ Exception -> 0x0074 }
            android.location.Location r0 = r7.lastKnownLocation     // Catch:{ Exception -> 0x0074 }
            if (r0 != 0) goto L_0x0078
            android.location.LocationManager r0 = r7.locationManager     // Catch:{ Exception -> 0x0074 }
            java.lang.String r1 = "network"
            android.location.Location r0 = r0.getLastKnownLocation(r1)     // Catch:{ Exception -> 0x0074 }
            r7.setLastKnownLocation(r0)     // Catch:{ Exception -> 0x0074 }
            goto L_0x0078
        L_0x0074:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0078:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocationController.start():void");
    }

    private void stop(boolean z) {
        if (!this.lookingForPeopleNearby && !this.shareMyCurrentLocation) {
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
        Utilities.stageQueue.postRunnable(new Runnable(z) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                LocationController.this.lambda$startLocationLookupForPeopleNearby$24$LocationController(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$startLocationLookupForPeopleNearby$24$LocationController(boolean z) {
        this.lookingForPeopleNearby = !z;
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
            this.cacheRequests.put(j, true);
            TLRPC.TL_messages_getRecentLocations tL_messages_getRecentLocations = new TLRPC.TL_messages_getRecentLocations();
            tL_messages_getRecentLocations.peer = getMessagesController().getInputPeer((int) j);
            tL_messages_getRecentLocations.limit = 100;
            getConnectionsManager().sendRequest(tL_messages_getRecentLocations, new RequestDelegate(j) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LocationController.this.lambda$loadLiveLocations$26$LocationController(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadLiveLocations$26$LocationController(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(j, tLObject) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ TLObject f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    LocationController.this.lambda$null$25$LocationController(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$25$LocationController(long j, TLObject tLObject) {
        this.cacheRequests.delete(j);
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        int i = 0;
        while (i < messages_messages.messages.size()) {
            if (!(messages_messages.messages.get(i).media instanceof TLRPC.TL_messageMediaGeoLive)) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readMessageContents} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_channels_readMessageContents} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readMessageContents} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readMessageContents} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void markLiveLoactionsAsRead(long r8) {
        /*
            r7 = this;
            int r0 = (int) r8
            if (r0 != 0) goto L_0x0004
            return
        L_0x0004:
            android.util.LongSparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>> r1 = r7.locationsCache
            java.lang.Object r1 = r1.get(r8)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            boolean r2 = r1.isEmpty()
            if (r2 != 0) goto L_0x009b
            if (r1 != 0) goto L_0x0016
            goto L_0x009b
        L_0x0016:
            android.util.LongSparseArray<java.lang.Integer> r2 = r7.lastReadLocationTime
            java.lang.Object r2 = r2.get(r8)
            java.lang.Integer r2 = (java.lang.Integer) r2
            long r3 = android.os.SystemClock.elapsedRealtime()
            r5 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 / r5
            int r4 = (int) r3
            if (r2 == 0) goto L_0x0031
            int r2 = r2.intValue()
            int r2 = r2 + 60
            if (r2 <= r4) goto L_0x0031
            return
        L_0x0031:
            android.util.LongSparseArray<java.lang.Integer> r2 = r7.lastReadLocationTime
            java.lang.Integer r3 = java.lang.Integer.valueOf(r4)
            r2.put(r8, r3)
            r8 = 0
            if (r0 >= 0) goto L_0x0070
            int r9 = -r0
            int r0 = r7.currentAccount
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r9, r0)
            if (r0 == 0) goto L_0x0070
            org.telegram.tgnet.TLRPC$TL_channels_readMessageContents r0 = new org.telegram.tgnet.TLRPC$TL_channels_readMessageContents
            r0.<init>()
            int r2 = r1.size()
        L_0x004f:
            if (r8 >= r2) goto L_0x0065
            java.util.ArrayList<java.lang.Integer> r3 = r0.id
            java.lang.Object r4 = r1.get(r8)
            org.telegram.tgnet.TLRPC$Message r4 = (org.telegram.tgnet.TLRPC.Message) r4
            int r4 = r4.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3.add(r4)
            int r8 = r8 + 1
            goto L_0x004f
        L_0x0065:
            org.telegram.messenger.MessagesController r8 = r7.getMessagesController()
            org.telegram.tgnet.TLRPC$InputChannel r8 = r8.getInputChannel((int) r9)
            r0.channel = r8
            goto L_0x008f
        L_0x0070:
            org.telegram.tgnet.TLRPC$TL_messages_readMessageContents r0 = new org.telegram.tgnet.TLRPC$TL_messages_readMessageContents
            r0.<init>()
            int r9 = r1.size()
        L_0x0079:
            if (r8 >= r9) goto L_0x008f
            java.util.ArrayList<java.lang.Integer> r2 = r0.id
            java.lang.Object r3 = r1.get(r8)
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC.Message) r3
            int r3 = r3.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2.add(r3)
            int r8 = r8 + 1
            goto L_0x0079
        L_0x008f:
            org.telegram.tgnet.ConnectionsManager r8 = r7.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$LocationController$3BwcW61KhsSJOPKE5mTQj4lYMJY r9 = new org.telegram.messenger.-$$Lambda$LocationController$3BwcW61KhsSJOPKE5mTQj4lYMJY
            r9.<init>()
            r8.sendRequest(r0, r9)
        L_0x009b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocationController.markLiveLoactionsAsRead(long):void");
    }

    public /* synthetic */ void lambda$markLiveLoactionsAsRead$27$LocationController(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_messages_affectedMessages) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
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
            Runnable runnable = callbacks.get(locationFetchCallback);
            if (runnable != null) {
                Utilities.globalQueue.cancelRunnable(runnable);
                callbacks.remove(locationFetchCallback);
            }
            if (location != null) {
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                $$Lambda$LocationController$tP3U4RW8B0Z7Wy3pdyZG8OOw918 r1 = new Runnable(location, locationFetchCallback) {
                    private final /* synthetic */ Location f$0;
                    private final /* synthetic */ LocationController.LocationFetchCallback f$1;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                    }

                    public final void run() {
                        LocationController.lambda$fetchLocationAddress$29(this.f$0, this.f$1);
                    }
                };
                dispatchQueue.postRunnable(r1, 300);
                callbacks.put(locationFetchCallback, r1);
            } else if (locationFetchCallback != null) {
                locationFetchCallback.onLocationAddressAvailable((String) null, (String) null, (Location) null);
            }
        }
    }

    static /* synthetic */ void lambda$fetchLocationAddress$29(Location location, LocationFetchCallback locationFetchCallback) {
        String str;
        String str2;
        boolean z;
        try {
            List<Address> fromLocation = new Geocoder(ApplicationLoader.applicationContext, LocaleController.getInstance().getSystemDefaultLocale()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (fromLocation.size() > 0) {
                Address address = fromLocation.get(0);
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                String subThoroughfare = address.getSubThoroughfare();
                if (!TextUtils.isEmpty(subThoroughfare)) {
                    sb.append(subThoroughfare);
                    z = true;
                } else {
                    z = false;
                }
                String thoroughfare = address.getThoroughfare();
                if (!TextUtils.isEmpty(thoroughfare)) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(thoroughfare);
                    z = true;
                }
                if (!z) {
                    String adminArea = address.getAdminArea();
                    if (!TextUtils.isEmpty(adminArea)) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(adminArea);
                    }
                    String subAdminArea = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(subAdminArea)) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(subAdminArea);
                    }
                }
                String locality = address.getLocality();
                if (!TextUtils.isEmpty(locality)) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(locality);
                }
                String countryName = address.getCountryName();
                if (!TextUtils.isEmpty(countryName)) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(countryName);
                }
                String countryName2 = address.getCountryName();
                if (!TextUtils.isEmpty(countryName2)) {
                    if (sb2.length() > 0) {
                        sb2.append(", ");
                    }
                    sb2.append(countryName2);
                }
                String locality2 = address.getLocality();
                if (!TextUtils.isEmpty(locality2)) {
                    if (sb2.length() > 0) {
                        sb2.append(", ");
                    }
                    sb2.append(locality2);
                }
                if (!z) {
                    String adminArea2 = address.getAdminArea();
                    if (!TextUtils.isEmpty(adminArea2)) {
                        if (sb2.length() > 0) {
                            sb2.append(", ");
                        }
                        sb2.append(adminArea2);
                    }
                    String subAdminArea2 = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(subAdminArea2)) {
                        if (sb2.length() > 0) {
                            sb2.append(", ");
                        }
                        sb2.append(subAdminArea2);
                    }
                }
                str = sb.toString();
                str2 = sb2.toString();
                AndroidUtilities.runOnUIThread(new Runnable(str, str2, location) {
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ String f$2;
                    private final /* synthetic */ Location f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        LocationController.lambda$null$28(LocationController.LocationFetchCallback.this, this.f$1, this.f$2, this.f$3);
                    }
                });
            }
            str = String.format(Locale.US, "Unknown address (%f,%f)", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
            str2 = str;
            AndroidUtilities.runOnUIThread(new Runnable(str, str2, location) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ Location f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    LocationController.lambda$null$28(LocationController.LocationFetchCallback.this, this.f$1, this.f$2, this.f$3);
                }
            });
        } catch (Exception unused) {
            str = String.format(Locale.US, "Unknown address (%f,%f)", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
        }
    }

    static /* synthetic */ void lambda$null$28(LocationFetchCallback locationFetchCallback, String str, String str2, Location location) {
        callbacks.remove(locationFetchCallback);
        if (locationFetchCallback != null) {
            locationFetchCallback.onLocationAddressAvailable(str, str2, location);
        }
    }
}
