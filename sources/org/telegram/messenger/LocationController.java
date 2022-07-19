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
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
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
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$GeoPoint;
import org.telegram.tgnet.TLRPC$InputGeoPoint;
import org.telegram.tgnet.TLRPC$InputMedia;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$TL_contacts_getLocated;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC$TL_inputGeoPointEmpty;
import org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messages_affectedMessages;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.tgnet.TLRPC$TL_messages_getRecentLocations;
import org.telegram.tgnet.TLRPC$TL_peerLocated;
import org.telegram.tgnet.TLRPC$TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateEditMessage;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$messages_Messages;

public class LocationController extends BaseController implements NotificationCenter.NotificationCenterDelegate, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int BACKGROUD_UPDATE_TIME = 30000;
    private static final long FASTEST_INTERVAL = 1000;
    private static final int FOREGROUND_UPDATE_TIME = 20000;
    private static volatile LocationController[] Instance = new LocationController[4];
    private static final int LOCATION_ACQUIRE_TIME = 10000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int SEND_NEW_LOCATION_TIME = 2000;
    private static final long UPDATE_INTERVAL = 1000;
    private static final int WATCH_LOCATION_TIMEOUT = 65000;
    private static HashMap<LocationFetchCallback, Runnable> callbacks = new HashMap<>();
    private LongSparseArray<Boolean> cacheRequests = new LongSparseArray<>();
    private ArrayList<TLRPC$TL_peerLocated> cachedNearbyChats = new ArrayList<>();
    private ArrayList<TLRPC$TL_peerLocated> cachedNearbyUsers = new ArrayList<>();
    private FusedLocationListener fusedLocationListener = new FusedLocationListener();
    private GoogleApiClient googleApiClient = new GoogleApiClient.Builder(ApplicationLoader.applicationContext).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
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
    public LongSparseArray<ArrayList<TLRPC$Message>> locationsCache = new LongSparseArray<>();
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
        public int lastSentProximityMeters;
        public MessageObject messageObject;
        public int mid;
        public int period;
        public int proximityMeters;
        public int stopTime;
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$broadcastLastKnownLocation$8(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        LocationRequest locationRequest2 = new LocationRequest();
        this.locationRequest = locationRequest2;
        locationRequest2.setPriority(100);
        this.locationRequest.setInterval(1000);
        this.locationRequest.setFastestInterval(1000);
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda4(this));
        loadSharingLocations();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        LocationController locationController = getAccountInstance().getLocationController();
        getNotificationCenter().addObserver(locationController, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(locationController, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(locationController, NotificationCenter.replaceMessagesObjects);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ArrayList arrayList;
        ArrayList arrayList2;
        boolean z;
        int i3 = i;
        if (i3 == NotificationCenter.didReceiveNewMessages) {
            if (!objArr[2].booleanValue()) {
                long longValue = objArr[0].longValue();
                if (isSharingLocation(longValue) && (arrayList2 = this.locationsCache.get(longValue)) != null) {
                    ArrayList arrayList3 = objArr[1];
                    boolean z2 = false;
                    for (int i4 = 0; i4 < arrayList3.size(); i4++) {
                        MessageObject messageObject = (MessageObject) arrayList3.get(i4);
                        if (messageObject.isLiveLocation()) {
                            int i5 = 0;
                            while (true) {
                                if (i5 >= arrayList2.size()) {
                                    z = false;
                                    break;
                                } else if (MessageObject.getFromChatId((TLRPC$Message) arrayList2.get(i5)) == messageObject.getFromChatId()) {
                                    arrayList2.set(i5, messageObject.messageOwner);
                                    z = true;
                                    break;
                                } else {
                                    i5++;
                                }
                            }
                            if (!z) {
                                arrayList2.add(messageObject.messageOwner);
                            }
                            z2 = true;
                        } else if (messageObject.messageOwner.action instanceof TLRPC$TL_messageActionGeoProximityReached) {
                            long dialogId = messageObject.getDialogId();
                            if (DialogObject.isUserDialog(dialogId)) {
                                setProximityLocation(dialogId, 0, false);
                            }
                        }
                    }
                    if (z2) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(longValue), Integer.valueOf(this.currentAccount));
                    }
                }
            }
        } else if (i3 == NotificationCenter.messagesDeleted) {
            if (!objArr[2].booleanValue() && !this.sharingLocationsUI.isEmpty()) {
                ArrayList arrayList4 = objArr[0];
                long longValue2 = objArr[1].longValue();
                ArrayList arrayList5 = null;
                for (int i6 = 0; i6 < this.sharingLocationsUI.size(); i6++) {
                    SharingLocationInfo sharingLocationInfo = this.sharingLocationsUI.get(i6);
                    MessageObject messageObject2 = sharingLocationInfo.messageObject;
                    if (longValue2 == (messageObject2 != null ? messageObject2.getChannelId() : 0) && arrayList4.contains(Integer.valueOf(sharingLocationInfo.mid))) {
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
        } else if (i3 == NotificationCenter.replaceMessagesObjects) {
            long longValue3 = objArr[0].longValue();
            if (isSharingLocation(longValue3) && (arrayList = this.locationsCache.get(longValue3)) != null) {
                ArrayList arrayList6 = objArr[1];
                boolean z3 = false;
                for (int i8 = 0; i8 < arrayList6.size(); i8++) {
                    MessageObject messageObject3 = (MessageObject) arrayList6.get(i8);
                    int i9 = 0;
                    while (true) {
                        if (i9 >= arrayList.size()) {
                            break;
                        } else if (MessageObject.getFromChatId((TLRPC$Message) arrayList.get(i9)) == messageObject3.getFromChatId()) {
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
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(longValue3), Integer.valueOf(this.currentAccount));
                }
            }
        }
    }

    public void onConnected(Bundle bundle) {
        this.wasConnectedToPlayServices = true;
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                LocationServices.SettingsApi.checkLocationSettings(this.googleApiClient, new LocationSettingsRequest.Builder().addLocationRequest(this.locationRequest).build()).setResultCallback(new LocationController$$ExternalSyntheticLambda0(this));
                return;
            }
            startFusedLocationRequest(true);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onConnected$4(LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        int statusCode = status.getStatusCode();
        if (statusCode == 0) {
            startFusedLocationRequest(true);
        } else if (statusCode == 6) {
            Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda15(this, status));
        } else if (statusCode == 8502) {
            Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda7(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onConnected$2(Status status) {
        if (this.lookingForPeopleNearby || !this.sharingLocations.isEmpty()) {
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda14(this, status));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onConnected$1(Status status) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowPlayServicesAlert, status);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onConnected$3() {
        this.playServicesAvailable = Boolean.FALSE;
        try {
            this.googleApiClient.disconnect();
            start();
        } catch (Throwable unused) {
        }
    }

    public void startFusedLocationRequest(boolean z) {
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda23(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startFusedLocationRequest$5(boolean z) {
        if (!z) {
            this.playServicesAvailable = Boolean.FALSE;
        }
        if (!this.shareMyCurrentLocation && !this.lookingForPeopleNearby && this.sharingLocations.isEmpty()) {
            return;
        }
        if (z) {
            try {
                FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
                setLastKnownLocation(fusedLocationProviderApi.getLastLocation(this.googleApiClient));
                fusedLocationProviderApi.requestLocationUpdates(this.googleApiClient, this.locationRequest, this.fusedLocationListener);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        } else {
            start();
        }
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!this.wasConnectedToPlayServices) {
            this.playServicesAvailable = Boolean.FALSE;
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
        TLRPC$GeoPoint tLRPC$GeoPoint;
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
                    TLRPC$Message tLRPC$Message = sharingLocationInfo.messageObject.messageOwner;
                    TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
                    if (!(tLRPC$MessageMedia == null || (tLRPC$GeoPoint = tLRPC$MessageMedia.geo) == null || sharingLocationInfo.lastSentProximityMeters != sharingLocationInfo.proximityMeters)) {
                        int i3 = tLRPC$Message.edit_date;
                        if (i3 == 0) {
                            i3 = tLRPC$Message.date;
                        }
                        if (Math.abs(currentTime - i3) < 10) {
                            Location.distanceBetween(tLRPC$GeoPoint.lat, tLRPC$GeoPoint._long, this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude(), fArr);
                            if (fArr[0] < 1.0f) {
                            }
                        }
                    }
                    TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage = new TLRPC$TL_messages_editMessage();
                    tLRPC$TL_messages_editMessage.peer = getMessagesController().getInputPeer(sharingLocationInfo.did);
                    tLRPC$TL_messages_editMessage.id = sharingLocationInfo.mid;
                    tLRPC$TL_messages_editMessage.flags |= 16384;
                    TLRPC$TL_inputMediaGeoLive tLRPC$TL_inputMediaGeoLive = new TLRPC$TL_inputMediaGeoLive();
                    tLRPC$TL_messages_editMessage.media = tLRPC$TL_inputMediaGeoLive;
                    tLRPC$TL_inputMediaGeoLive.stopped = false;
                    tLRPC$TL_inputMediaGeoLive.geo_point = new TLRPC$TL_inputGeoPoint();
                    tLRPC$TL_messages_editMessage.media.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                    tLRPC$TL_messages_editMessage.media.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
                    tLRPC$TL_messages_editMessage.media.geo_point.accuracy_radius = (int) this.lastKnownLocation.getAccuracy();
                    TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_messages_editMessage.media;
                    TLRPC$InputGeoPoint tLRPC$InputGeoPoint = tLRPC$InputMedia.geo_point;
                    if (tLRPC$InputGeoPoint.accuracy_radius != 0) {
                        tLRPC$InputGeoPoint.flags |= 1;
                    }
                    int i4 = sharingLocationInfo.lastSentProximityMeters;
                    int i5 = sharingLocationInfo.proximityMeters;
                    if (i4 != i5) {
                        tLRPC$InputMedia.proximity_notification_radius = i5;
                        tLRPC$InputMedia.flags |= 8;
                    }
                    tLRPC$InputMedia.heading = getHeading(this.lastKnownLocation);
                    tLRPC$TL_messages_editMessage.media.flags |= 4;
                    int[] iArr = {getConnectionsManager().sendRequest(tLRPC$TL_messages_editMessage, new LocationController$$ExternalSyntheticLambda30(this, sharingLocationInfo, iArr, tLRPC$TL_messages_editMessage))};
                    this.requests.put(iArr[0], 0);
                }
            }
            if (this.shareMyCurrentLocation) {
                UserConfig userConfig = getUserConfig();
                userConfig.lastMyLocationShareTime = (int) (System.currentTimeMillis() / 1000);
                userConfig.saveConfig(false);
                TLRPC$TL_contacts_getLocated tLRPC$TL_contacts_getLocated = new TLRPC$TL_contacts_getLocated();
                TLRPC$TL_inputGeoPoint tLRPC$TL_inputGeoPoint = new TLRPC$TL_inputGeoPoint();
                tLRPC$TL_contacts_getLocated.geo_point = tLRPC$TL_inputGeoPoint;
                tLRPC$TL_inputGeoPoint.lat = this.lastKnownLocation.getLatitude();
                tLRPC$TL_contacts_getLocated.geo_point._long = this.lastKnownLocation.getLongitude();
                tLRPC$TL_contacts_getLocated.background = true;
                getConnectionsManager().sendRequest(tLRPC$TL_contacts_getLocated, LocationController$$ExternalSyntheticLambda31.INSTANCE);
            }
            getConnectionsManager().resumeNetworkMaybe();
            if (shouldStopGps() || this.shareMyCurrentLocation) {
                this.shareMyCurrentLocation = false;
                stop(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastLastKnownLocation$7(SharingLocationInfo sharingLocationInfo, int[] iArr, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            if ((tLRPC$TL_messages_editMessage.flags & 8) != 0) {
                sharingLocationInfo.lastSentProximityMeters = tLRPC$TL_messages_editMessage.media.proximity_notification_radius;
            }
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            boolean z = false;
            for (int i = 0; i < tLRPC$Updates.updates.size(); i++) {
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateEditMessage) {
                    sharingLocationInfo.messageObject.messageOwner = ((TLRPC$TL_updateEditMessage) tLRPC$Update).message;
                } else if (tLRPC$Update instanceof TLRPC$TL_updateEditChannelMessage) {
                    sharingLocationInfo.messageObject.messageOwner = ((TLRPC$TL_updateEditChannelMessage) tLRPC$Update).message;
                }
                z = true;
            }
            if (z) {
                saveSharingLocation(sharingLocationInfo, 0);
            }
            getMessagesController().processUpdates(tLRPC$Updates, false);
        } else if (tLRPC$TL_error.text.equals("MESSAGE_ID_INVALID")) {
            this.sharingLocations.remove(sharingLocationInfo);
            this.sharingLocationsMap.remove(sharingLocationInfo.did);
            saveSharingLocation(sharingLocationInfo, 1);
            this.requests.delete(iArr[0]);
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda19(this, sharingLocationInfo));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$broadcastLastKnownLocation$6(SharingLocationInfo sharingLocationInfo) {
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
        boolean z = true;
        if (ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePaused && !this.shareMyCurrentLocation && userConfig.isClientActivated() && userConfig.isConfigLoaded() && userConfig.sharingMyLocationUntil != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) userConfig.lastMyLocationShareTime)) >= 3600) {
            this.shareMyCurrentLocation = true;
        }
        if (!this.sharingLocations.isEmpty()) {
            int i = 0;
            while (i < this.sharingLocations.size()) {
                SharingLocationInfo sharingLocationInfo = this.sharingLocations.get(i);
                if (sharingLocationInfo.stopTime <= getConnectionsManager().getCurrentTime()) {
                    this.sharingLocations.remove(i);
                    this.sharingLocationsMap.remove(sharingLocationInfo.did);
                    saveSharingLocation(sharingLocationInfo, 1);
                    AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda21(this, sharingLocationInfo));
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
                if (SystemClock.elapsedRealtime() - this.lastLocationSendTime <= 2000) {
                    z = false;
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$update$9(SharingLocationInfo sharingLocationInfo) {
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
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda9(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanup$10() {
        this.locationEndWatchTime = 0;
        this.requests.clear();
        this.sharingLocationsMap.clear();
        this.sharingLocations.clear();
        setLastKnownLocation((Location) null);
        stop(true);
    }

    /* access modifiers changed from: private */
    public void setLastKnownLocation(Location location) {
        if (location == null || Build.VERSION.SDK_INT < 17 || (SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos()) / NUM <= 300) {
            this.lastKnownLocation = location;
            if (location != null) {
                AndroidUtilities.runOnUIThread(LocationController$$ExternalSyntheticLambda25.INSTANCE);
            }
        }
    }

    public void setCachedNearbyUsersAndChats(ArrayList<TLRPC$TL_peerLocated> arrayList, ArrayList<TLRPC$TL_peerLocated> arrayList2) {
        this.cachedNearbyUsers = new ArrayList<>(arrayList);
        this.cachedNearbyChats = new ArrayList<>(arrayList2);
    }

    public ArrayList<TLRPC$TL_peerLocated> getCachedNearbyUsers() {
        return this.cachedNearbyUsers;
    }

    public ArrayList<TLRPC$TL_peerLocated> getCachedNearbyChats() {
        return this.cachedNearbyChats;
    }

    /* access modifiers changed from: protected */
    public void addSharingLocation(TLRPC$Message tLRPC$Message) {
        SharingLocationInfo sharingLocationInfo = new SharingLocationInfo();
        sharingLocationInfo.did = tLRPC$Message.dialog_id;
        sharingLocationInfo.mid = tLRPC$Message.id;
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        sharingLocationInfo.period = tLRPC$MessageMedia.period;
        int i = tLRPC$MessageMedia.proximity_notification_radius;
        sharingLocationInfo.proximityMeters = i;
        sharingLocationInfo.lastSentProximityMeters = i;
        sharingLocationInfo.account = this.currentAccount;
        sharingLocationInfo.messageObject = new MessageObject(this.currentAccount, tLRPC$Message, false, false);
        sharingLocationInfo.stopTime = getConnectionsManager().getCurrentTime() + sharingLocationInfo.period;
        SharingLocationInfo sharingLocationInfo2 = this.sharingLocationsMap.get(sharingLocationInfo.did);
        this.sharingLocationsMap.put(sharingLocationInfo.did, sharingLocationInfo);
        if (sharingLocationInfo2 != null) {
            this.sharingLocations.remove(sharingLocationInfo2);
        }
        this.sharingLocations.add(sharingLocationInfo);
        saveSharingLocation(sharingLocationInfo, 0);
        this.lastLocationSendTime = (SystemClock.elapsedRealtime() - 30000) + 5000;
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda22(this, sharingLocationInfo2, sharingLocationInfo));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addSharingLocation$12(SharingLocationInfo sharingLocationInfo, SharingLocationInfo sharingLocationInfo2) {
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

    public boolean setProximityLocation(long j, int i, boolean z) {
        SharingLocationInfo sharingLocationInfo = this.sharingLocationsMapUI.get(j);
        if (sharingLocationInfo != null) {
            sharingLocationInfo.proximityMeters = i;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new LocationController$$ExternalSyntheticLambda10(this, i, j));
        if (z) {
            Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda5(this));
        }
        return sharingLocationInfo != null;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setProximityLocation$13(int i, long j) {
        try {
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("UPDATE sharing_locations SET proximity = ? WHERE uid = ?");
            executeFast.requery();
            executeFast.bindInteger(1, i);
            executeFast.bindLong(2, j);
            executeFast.step();
            executeFast.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setProximityLocation$14() {
        broadcastLastKnownLocation(true);
    }

    public static int getHeading(Location location) {
        float bearing = location.getBearing();
        if (bearing <= 0.0f || bearing >= 1.0f) {
            return (int) bearing;
        }
        return bearing < 0.5f ? 360 : 1;
    }

    private void loadSharingLocations() {
        getMessagesStorage().getStorageQueue().postRunnable(new LocationController$$ExternalSyntheticLambda3(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadSharingLocations$18() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        try {
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            SQLiteCursor queryFinalized = getMessagesStorage().getDatabase().queryFinalized("SELECT uid, mid, date, period, message, proximity FROM sharing_locations WHERE 1", new Object[0]);
            while (queryFinalized.next()) {
                SharingLocationInfo sharingLocationInfo = new SharingLocationInfo();
                sharingLocationInfo.did = queryFinalized.longValue(0);
                sharingLocationInfo.mid = queryFinalized.intValue(1);
                sharingLocationInfo.stopTime = queryFinalized.intValue(2);
                sharingLocationInfo.period = queryFinalized.intValue(3);
                sharingLocationInfo.proximityMeters = queryFinalized.intValue(5);
                sharingLocationInfo.account = this.currentAccount;
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(4);
                if (byteBufferValue != null) {
                    MessageObject messageObject = new MessageObject(this.currentAccount, TLRPC$Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), false, false);
                    sharingLocationInfo.messageObject = messageObject;
                    MessagesStorage.addUsersAndChatsFromMessage(messageObject.messageOwner, arrayList4, arrayList5, (ArrayList<Long>) null);
                    byteBufferValue.reuse();
                }
                arrayList.add(sharingLocationInfo);
                if (DialogObject.isChatDialog(sharingLocationInfo.did)) {
                    if (!arrayList5.contains(Long.valueOf(-sharingLocationInfo.did))) {
                        arrayList5.add(Long.valueOf(-sharingLocationInfo.did));
                    }
                } else if (DialogObject.isUserDialog(sharingLocationInfo.did) && !arrayList4.contains(Long.valueOf(sharingLocationInfo.did))) {
                    arrayList4.add(Long.valueOf(sharingLocationInfo.did));
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
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda18(this, arrayList2, arrayList3, arrayList));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadSharingLocations$17(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda17(this, arrayList3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadSharingLocations$16(ArrayList arrayList) {
        this.sharingLocations.addAll(arrayList);
        for (int i = 0; i < this.sharingLocations.size(); i++) {
            SharingLocationInfo sharingLocationInfo = this.sharingLocations.get(i);
            this.sharingLocationsMap.put(sharingLocationInfo.did, sharingLocationInfo);
        }
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda16(this, arrayList));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadSharingLocations$15(ArrayList arrayList) {
        this.sharingLocationsUI.addAll(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) arrayList.get(i);
            this.sharingLocationsMapUI.put(sharingLocationInfo.did, sharingLocationInfo);
        }
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void saveSharingLocation(SharingLocationInfo sharingLocationInfo, int i) {
        getMessagesStorage().getStorageQueue().postRunnable(new LocationController$$ExternalSyntheticLambda11(this, i, sharingLocationInfo));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveSharingLocation$19(int i, SharingLocationInfo sharingLocationInfo) {
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
            SQLitePreparedStatement executeFast = getMessagesStorage().getDatabase().executeFast("REPLACE INTO sharing_locations VALUES(?, ?, ?, ?, ?, ?)");
            executeFast.requery();
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(sharingLocationInfo.messageObject.messageOwner.getObjectSize());
            sharingLocationInfo.messageObject.messageOwner.serializeToStream(nativeByteBuffer);
            executeFast.bindLong(1, sharingLocationInfo.did);
            executeFast.bindInteger(2, sharingLocationInfo.mid);
            executeFast.bindInteger(3, sharingLocationInfo.stopTime);
            executeFast.bindInteger(4, sharingLocationInfo.period);
            executeFast.bindByteBuffer(5, nativeByteBuffer);
            executeFast.bindInteger(6, sharingLocationInfo.proximityMeters);
            executeFast.step();
            executeFast.dispose();
            nativeByteBuffer.reuse();
        }
    }

    public void removeSharingLocation(long j) {
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda12(this, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeSharingLocation$22(long j) {
        SharingLocationInfo sharingLocationInfo = this.sharingLocationsMap.get(j);
        this.sharingLocationsMap.remove(j);
        if (sharingLocationInfo != null) {
            TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage = new TLRPC$TL_messages_editMessage();
            tLRPC$TL_messages_editMessage.peer = getMessagesController().getInputPeer(sharingLocationInfo.did);
            tLRPC$TL_messages_editMessage.id = sharingLocationInfo.mid;
            tLRPC$TL_messages_editMessage.flags |= 16384;
            TLRPC$TL_inputMediaGeoLive tLRPC$TL_inputMediaGeoLive = new TLRPC$TL_inputMediaGeoLive();
            tLRPC$TL_messages_editMessage.media = tLRPC$TL_inputMediaGeoLive;
            tLRPC$TL_inputMediaGeoLive.stopped = true;
            tLRPC$TL_inputMediaGeoLive.geo_point = new TLRPC$TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(tLRPC$TL_messages_editMessage, new LocationController$$ExternalSyntheticLambda27(this));
            this.sharingLocations.remove(sharingLocationInfo);
            saveSharingLocation(sharingLocationInfo, 1);
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda20(this, sharingLocationInfo));
            if (this.sharingLocations.isEmpty()) {
                stop(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeSharingLocation$20(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeSharingLocation$21(SharingLocationInfo sharingLocationInfo) {
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
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda8(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeAllLocationSharings$25() {
        for (int i = 0; i < this.sharingLocations.size(); i++) {
            SharingLocationInfo sharingLocationInfo = this.sharingLocations.get(i);
            TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage = new TLRPC$TL_messages_editMessage();
            tLRPC$TL_messages_editMessage.peer = getMessagesController().getInputPeer(sharingLocationInfo.did);
            tLRPC$TL_messages_editMessage.id = sharingLocationInfo.mid;
            tLRPC$TL_messages_editMessage.flags |= 16384;
            TLRPC$TL_inputMediaGeoLive tLRPC$TL_inputMediaGeoLive = new TLRPC$TL_inputMediaGeoLive();
            tLRPC$TL_messages_editMessage.media = tLRPC$TL_inputMediaGeoLive;
            tLRPC$TL_inputMediaGeoLive.stopped = true;
            tLRPC$TL_inputMediaGeoLive.geo_point = new TLRPC$TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(tLRPC$TL_messages_editMessage, new LocationController$$ExternalSyntheticLambda26(this));
        }
        this.sharingLocations.clear();
        this.sharingLocationsMap.clear();
        saveSharingLocation((SharingLocationInfo) null, 2);
        stop(true);
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeAllLocationSharings$23(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeAllLocationSharings$24() {
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
                    FileLog.e(th, false);
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
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda24(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startLocationLookupForPeopleNearby$26(boolean z) {
        boolean z2 = !z;
        this.lookingForPeopleNearby = z2;
        if (z2) {
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
            this.cacheRequests.put(j, Boolean.TRUE);
            TLRPC$TL_messages_getRecentLocations tLRPC$TL_messages_getRecentLocations = new TLRPC$TL_messages_getRecentLocations();
            tLRPC$TL_messages_getRecentLocations.peer = getMessagesController().getInputPeer(j);
            tLRPC$TL_messages_getRecentLocations.limit = 100;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getRecentLocations, new LocationController$$ExternalSyntheticLambda29(this, j));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadLiveLocations$28(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda13(this, j, tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadLiveLocations$27(long j, TLObject tLObject) {
        this.cacheRequests.delete(j);
        TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
        int i = 0;
        while (i < tLRPC$messages_Messages.messages.size()) {
            if (!(tLRPC$messages_Messages.messages.get(i).media instanceof TLRPC$TL_messageMediaGeoLive)) {
                tLRPC$messages_Messages.messages.remove(i);
                i--;
            }
            i++;
        }
        getMessagesStorage().putUsersAndChats(tLRPC$messages_Messages.users, tLRPC$messages_Messages.chats, true, true);
        getMessagesController().putUsers(tLRPC$messages_Messages.users, false);
        getMessagesController().putChats(tLRPC$messages_Messages.chats, false);
        this.locationsCache.put(j, tLRPC$messages_Messages.messages);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(j), Integer.valueOf(this.currentAccount));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readMessageContents} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: org.telegram.tgnet.TLRPC$TL_channels_readMessageContents} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readMessageContents} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readMessageContents} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void markLiveLoactionsAsRead(long r7) {
        /*
            r6 = this;
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            androidx.collection.LongSparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>> r0 = r6.locationsCache
            java.lang.Object r0 = r0.get(r7)
            java.util.ArrayList r0 = (java.util.ArrayList) r0
            if (r0 == 0) goto L_0x00a2
            boolean r1 = r0.isEmpty()
            if (r1 == 0) goto L_0x0019
            goto L_0x00a2
        L_0x0019:
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r6.lastReadLocationTime
            java.lang.Object r1 = r1.get(r7)
            java.lang.Integer r1 = (java.lang.Integer) r1
            long r2 = android.os.SystemClock.elapsedRealtime()
            r4 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 / r4
            int r3 = (int) r2
            if (r1 == 0) goto L_0x0034
            int r1 = r1.intValue()
            int r1 = r1 + 60
            if (r1 <= r3) goto L_0x0034
            return
        L_0x0034:
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r6.lastReadLocationTime
            java.lang.Integer r2 = java.lang.Integer.valueOf(r3)
            r1.put(r7, r2)
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r7)
            r2 = 0
            if (r1 == 0) goto L_0x0077
            long r7 = -r7
            int r1 = r6.currentAccount
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r7, r1)
            if (r1 == 0) goto L_0x0077
            org.telegram.tgnet.TLRPC$TL_channels_readMessageContents r1 = new org.telegram.tgnet.TLRPC$TL_channels_readMessageContents
            r1.<init>()
            int r3 = r0.size()
        L_0x0056:
            if (r2 >= r3) goto L_0x006c
            java.util.ArrayList<java.lang.Integer> r4 = r1.id
            java.lang.Object r5 = r0.get(r2)
            org.telegram.tgnet.TLRPC$Message r5 = (org.telegram.tgnet.TLRPC$Message) r5
            int r5 = r5.id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4.add(r5)
            int r2 = r2 + 1
            goto L_0x0056
        L_0x006c:
            org.telegram.messenger.MessagesController r0 = r6.getMessagesController()
            org.telegram.tgnet.TLRPC$InputChannel r7 = r0.getInputChannel((long) r7)
            r1.channel = r7
            goto L_0x0096
        L_0x0077:
            org.telegram.tgnet.TLRPC$TL_messages_readMessageContents r1 = new org.telegram.tgnet.TLRPC$TL_messages_readMessageContents
            r1.<init>()
            int r7 = r0.size()
        L_0x0080:
            if (r2 >= r7) goto L_0x0096
            java.util.ArrayList<java.lang.Integer> r8 = r1.id
            java.lang.Object r3 = r0.get(r2)
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            int r3 = r3.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r8.add(r3)
            int r2 = r2 + 1
            goto L_0x0080
        L_0x0096:
            org.telegram.tgnet.ConnectionsManager r7 = r6.getConnectionsManager()
            org.telegram.messenger.LocationController$$ExternalSyntheticLambda28 r8 = new org.telegram.messenger.LocationController$$ExternalSyntheticLambda28
            r8.<init>(r6)
            r7.sendRequest(r1, r8)
        L_0x00a2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocationController.markLiveLoactionsAsRead(long):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$markLiveLoactionsAsRead$29(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_messages_affectedMessages) {
            TLRPC$TL_messages_affectedMessages tLRPC$TL_messages_affectedMessages = (TLRPC$TL_messages_affectedMessages) tLObject;
            getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_messages_affectedMessages.pts, -1, tLRPC$TL_messages_affectedMessages.pts_count);
        }
    }

    public static int getLocationsCount() {
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
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
            if (location == null) {
                locationFetchCallback.onLocationAddressAvailable((String) null, (String) null, (Location) null);
                return;
            }
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            LocationController$$ExternalSyntheticLambda1 locationController$$ExternalSyntheticLambda1 = new LocationController$$ExternalSyntheticLambda1(location, locationFetchCallback);
            dispatchQueue.postRunnable(locationController$$ExternalSyntheticLambda1, 300);
            callbacks.put(locationFetchCallback, locationController$$ExternalSyntheticLambda1);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$fetchLocationAddress$31(Location location, LocationFetchCallback locationFetchCallback) {
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
                        sb.append(" ");
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
            } else {
                str = String.format(Locale.US, "Unknown address (%f,%f)", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
                str2 = str;
            }
        } catch (Exception unused) {
            str2 = String.format(Locale.US, "Unknown address (%f,%f)", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
            str = str2;
        }
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda2(locationFetchCallback, str, str2, location));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$fetchLocationAddress$30(LocationFetchCallback locationFetchCallback, String str, String str2, Location location) {
        callbacks.remove(locationFetchCallback);
        locationFetchCallback.onLocationAddressAvailable(str, str2, location);
    }
}
