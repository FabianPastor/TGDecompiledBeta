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
        public int lastSentProximityMeters;
        public MessageObject messageObject;
        public int mid;
        public int period;
        public int proximityMeters;
        public int stopTime;
    }

    public static LocationController getInstance(int num) {
        LocationController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (LocationController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    LocationController[] locationControllerArr = Instance;
                    LocationController locationController = new LocationController(num);
                    localInstance = locationController;
                    locationControllerArr[num] = locationController;
                }
            }
        }
        return localInstance;
    }

    private class GpsLocationListener implements LocationListener {
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

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
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

    public LocationController(int instance) {
        super(instance);
        LocationRequest locationRequest2 = new LocationRequest();
        this.locationRequest = locationRequest2;
        locationRequest2.setPriority(100);
        this.locationRequest.setInterval(1000);
        this.locationRequest.setFastestInterval(1000);
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda27(this));
        loadSharingLocations();
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m658lambda$new$0$orgtelegrammessengerLocationController() {
        LocationController locationController = getAccountInstance().getLocationController();
        getNotificationCenter().addObserver(locationController, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(locationController, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(locationController, NotificationCenter.replaceMessagesObjects);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ArrayList<TLRPC.Message> messages;
        ArrayList<TLRPC.Message> messages2;
        int i = id;
        if (i == NotificationCenter.didReceiveNewMessages) {
            if (!args[2].booleanValue()) {
                long did = args[0].longValue();
                if (isSharingLocation(did) && (messages2 = this.locationsCache.get(did)) != null) {
                    ArrayList<MessageObject> arr = args[1];
                    boolean added = false;
                    for (int a = 0; a < arr.size(); a++) {
                        MessageObject messageObject = arr.get(a);
                        if (messageObject.isLiveLocation()) {
                            added = true;
                            boolean replaced = false;
                            int b = 0;
                            while (true) {
                                if (b >= messages2.size()) {
                                    break;
                                } else if (MessageObject.getFromChatId(messages2.get(b)) == messageObject.getFromChatId()) {
                                    replaced = true;
                                    messages2.set(b, messageObject.messageOwner);
                                    break;
                                } else {
                                    b++;
                                }
                            }
                            if (!replaced) {
                                messages2.add(messageObject.messageOwner);
                            }
                        } else if (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionGeoProximityReached) {
                            long dialogId = messageObject.getDialogId();
                            if (DialogObject.isUserDialog(dialogId)) {
                                setProximityLocation(dialogId, 0, false);
                            }
                        }
                    }
                    if (added) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did), Integer.valueOf(this.currentAccount));
                    }
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!args[2].booleanValue() && !this.sharingLocationsUI.isEmpty()) {
                ArrayList<Integer> markAsDeletedMessages = args[0];
                long channelId = args[1].longValue();
                ArrayList<Long> toRemove = null;
                for (int a2 = 0; a2 < this.sharingLocationsUI.size(); a2++) {
                    SharingLocationInfo info = this.sharingLocationsUI.get(a2);
                    if (channelId == (info.messageObject != null ? info.messageObject.getChannelId() : 0) && markAsDeletedMessages.contains(Integer.valueOf(info.mid))) {
                        if (toRemove == null) {
                            toRemove = new ArrayList<>();
                        }
                        toRemove.add(Long.valueOf(info.did));
                    }
                }
                if (toRemove != null) {
                    for (int a3 = 0; a3 < toRemove.size(); a3++) {
                        removeSharingLocation(toRemove.get(a3).longValue());
                    }
                }
            }
        } else if (i == NotificationCenter.replaceMessagesObjects) {
            long did2 = args[0].longValue();
            if (isSharingLocation(did2) && (messages = this.locationsCache.get(did2)) != null) {
                boolean updated = false;
                ArrayList<MessageObject> messageObjects = args[1];
                for (int a4 = 0; a4 < messageObjects.size(); a4++) {
                    MessageObject messageObject2 = messageObjects.get(a4);
                    int b2 = 0;
                    while (true) {
                        if (b2 >= messages.size()) {
                            break;
                        } else if (MessageObject.getFromChatId(messages.get(b2)) == messageObject2.getFromChatId()) {
                            if (!messageObject2.isLiveLocation()) {
                                messages.remove(b2);
                            } else {
                                messages.set(b2, messageObject2.messageOwner);
                            }
                            updated = true;
                        } else {
                            b2++;
                        }
                    }
                }
                if (updated) {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did2), Integer.valueOf(this.currentAccount));
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
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$onConnected$4$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m662lambda$onConnected$4$orgtelegrammessengerLocationController(LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case 0:
                startFusedLocationRequest(true);
                return;
            case 6:
                Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda6(this, status));
                return;
            case 8502:
                Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda28(this));
                return;
            default:
                return;
        }
    }

    /* renamed from: lambda$onConnected$2$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m660lambda$onConnected$2$orgtelegrammessengerLocationController(Status status) {
        if (this.lookingForPeopleNearby || !this.sharingLocations.isEmpty()) {
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda5(this, status));
        }
    }

    /* renamed from: lambda$onConnected$1$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m659lambda$onConnected$1$orgtelegrammessengerLocationController(Status status) {
        getNotificationCenter().postNotificationName(NotificationCenter.needShowPlayServicesAlert, status);
    }

    /* renamed from: lambda$onConnected$3$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m661lambda$onConnected$3$orgtelegrammessengerLocationController() {
        this.playServicesAvailable = false;
        try {
            this.googleApiClient.disconnect();
            start();
        } catch (Throwable th) {
        }
    }

    public void startFusedLocationRequest(boolean permissionsGranted) {
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda15(this, permissionsGranted));
    }

    /* renamed from: lambda$startFusedLocationRequest$5$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m672x23advar_(boolean permissionsGranted) {
        if (!permissionsGranted) {
            this.playServicesAvailable = false;
        }
        if (!this.shareMyCurrentLocation && !this.lookingForPeopleNearby && this.sharingLocations.isEmpty()) {
            return;
        }
        if (permissionsGranted) {
            try {
                setLastKnownLocation(LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient));
                LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient, this.locationRequest, (com.google.android.gms.location.LocationListener) this.fusedLocationListener);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        } else {
            start();
        }
    }

    public void onConnectionSuspended(int i) {
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

    private void broadcastLastKnownLocation(boolean cancelCurrent) {
        if (this.lastKnownLocation != null) {
            if (this.requests.size() != 0) {
                if (cancelCurrent) {
                    for (int a = 0; a < this.requests.size(); a++) {
                        getConnectionsManager().cancelRequest(this.requests.keyAt(a), false);
                    }
                }
                this.requests.clear();
            }
            if (!this.sharingLocations.isEmpty()) {
                int date = getConnectionsManager().getCurrentTime();
                float[] result = new float[1];
                for (int a2 = 0; a2 < this.sharingLocations.size(); a2++) {
                    SharingLocationInfo info = this.sharingLocations.get(a2);
                    if (!(info.messageObject.messageOwner.media == null || info.messageObject.messageOwner.media.geo == null || info.lastSentProximityMeters != info.proximityMeters)) {
                        int messageDate = info.messageObject.messageOwner.edit_date != 0 ? info.messageObject.messageOwner.edit_date : info.messageObject.messageOwner.date;
                        TLRPC.GeoPoint point = info.messageObject.messageOwner.media.geo;
                        if (Math.abs(date - messageDate) < 10) {
                            TLRPC.GeoPoint geoPoint = point;
                            Location.distanceBetween(point.lat, point._long, this.lastKnownLocation.getLatitude(), this.lastKnownLocation.getLongitude(), result);
                            if (result[0] < 1.0f) {
                            }
                        }
                    }
                    TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
                    req.peer = getMessagesController().getInputPeer(info.did);
                    req.id = info.mid;
                    req.flags |= 16384;
                    req.media = new TLRPC.TL_inputMediaGeoLive();
                    req.media.stopped = false;
                    req.media.geo_point = new TLRPC.TL_inputGeoPoint();
                    req.media.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                    req.media.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
                    req.media.geo_point.accuracy_radius = (int) this.lastKnownLocation.getAccuracy();
                    if (req.media.geo_point.accuracy_radius != 0) {
                        req.media.geo_point.flags |= 1;
                    }
                    if (info.lastSentProximityMeters != info.proximityMeters) {
                        req.media.proximity_notification_radius = info.proximityMeters;
                        req.media.flags |= 8;
                    }
                    req.media.heading = getHeading(this.lastKnownLocation);
                    req.media.flags |= 4;
                    int[] reqId = {getConnectionsManager().sendRequest(req, new LocationController$$ExternalSyntheticLambda23(this, info, reqId, req))};
                    this.requests.put(reqId[0], 0);
                }
            }
            if (this.shareMyCurrentLocation != 0) {
                UserConfig userConfig = getUserConfig();
                userConfig.lastMyLocationShareTime = (int) (System.currentTimeMillis() / 1000);
                userConfig.saveConfig(false);
                TLRPC.TL_contacts_getLocated req2 = new TLRPC.TL_contacts_getLocated();
                req2.geo_point = new TLRPC.TL_inputGeoPoint();
                req2.geo_point.lat = this.lastKnownLocation.getLatitude();
                req2.geo_point._long = this.lastKnownLocation.getLongitude();
                req2.background = true;
                getConnectionsManager().sendRequest(req2, LocationController$$ExternalSyntheticLambda24.INSTANCE);
            }
            getConnectionsManager().resumeNetworkMaybe();
            if (shouldStopGps() || this.shareMyCurrentLocation) {
                this.shareMyCurrentLocation = false;
                stop(false);
            }
        }
    }

    /* renamed from: lambda$broadcastLastKnownLocation$7$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m649xd8307cfb(SharingLocationInfo info, int[] reqId, TLRPC.TL_messages_editMessage req, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            if ((req.flags & 8) != 0) {
                info.lastSentProximityMeters = req.media.proximity_notification_radius;
            }
            TLRPC.Updates updates = (TLRPC.Updates) response;
            boolean updated = false;
            for (int a1 = 0; a1 < updates.updates.size(); a1++) {
                TLRPC.Update update = updates.updates.get(a1);
                if (update instanceof TLRPC.TL_updateEditMessage) {
                    updated = true;
                    info.messageObject.messageOwner = ((TLRPC.TL_updateEditMessage) update).message;
                } else if (update instanceof TLRPC.TL_updateEditChannelMessage) {
                    updated = true;
                    info.messageObject.messageOwner = ((TLRPC.TL_updateEditChannelMessage) update).message;
                }
            }
            if (updated) {
                saveSharingLocation(info, 0);
            }
            getMessagesController().processUpdates(updates, false);
        } else if (error.text.equals("MESSAGE_ID_INVALID")) {
            this.sharingLocations.remove(info);
            this.sharingLocationsMap.remove(info.did);
            saveSharingLocation(info, 1);
            this.requests.delete(reqId[0]);
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda10(this, info));
        }
    }

    /* renamed from: lambda$broadcastLastKnownLocation$6$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m648xf2ef0e3a(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    static /* synthetic */ void lambda$broadcastLastKnownLocation$8(TLObject response, TLRPC.TL_error error) {
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
        boolean cancelAll = true;
        if (ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePaused && !this.shareMyCurrentLocation && userConfig.isClientActivated() && userConfig.isConfigLoaded() && userConfig.sharingMyLocationUntil != 0 && Math.abs((System.currentTimeMillis() / 1000) - ((long) userConfig.lastMyLocationShareTime)) >= 3600) {
            this.shareMyCurrentLocation = true;
        }
        if (!this.sharingLocations.isEmpty()) {
            int a = 0;
            while (a < this.sharingLocations.size()) {
                SharingLocationInfo info = this.sharingLocations.get(a);
                if (info.stopTime <= getConnectionsManager().getCurrentTime()) {
                    this.sharingLocations.remove(a);
                    this.sharingLocationsMap.remove(info.did);
                    saveSharingLocation(info, 1);
                    AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda13(this, info));
                    a--;
                }
                a++;
            }
        }
        if (this.started != 0) {
            long newTime = SystemClock.elapsedRealtime();
            if (this.lastLocationByGoogleMaps || Math.abs(this.lastLocationStartTime - newTime) > 10000 || shouldSendLocationNow()) {
                this.lastLocationByGoogleMaps = false;
                this.locationSentSinceLastGoogleMapUpdate = true;
                if (SystemClock.elapsedRealtime() - this.lastLocationSendTime <= 2000) {
                    cancelAll = false;
                }
                this.lastLocationStartTime = newTime;
                this.lastLocationSendTime = SystemClock.elapsedRealtime();
                broadcastLastKnownLocation(cancelAll);
            }
        } else if (this.sharingLocations.isEmpty() && !this.shareMyCurrentLocation) {
        } else {
            if (this.shareMyCurrentLocation || Math.abs(this.lastLocationSendTime - SystemClock.elapsedRealtime()) > 30000) {
                this.lastLocationStartTime = SystemClock.elapsedRealtime();
                start();
            }
        }
    }

    /* renamed from: lambda$update$9$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m674lambda$update$9$orgtelegrammessengerLocationController(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
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
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda25(this));
    }

    /* renamed from: lambda$cleanup$10$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m650lambda$cleanup$10$orgtelegrammessengerLocationController() {
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
                AndroidUtilities.runOnUIThread(LocationController$$ExternalSyntheticLambda17.INSTANCE);
            }
        }
    }

    public void setCachedNearbyUsersAndChats(ArrayList<TLRPC.TL_peerLocated> u, ArrayList<TLRPC.TL_peerLocated> c) {
        this.cachedNearbyUsers = new ArrayList<>(u);
        this.cachedNearbyChats = new ArrayList<>(c);
    }

    public ArrayList<TLRPC.TL_peerLocated> getCachedNearbyUsers() {
        return this.cachedNearbyUsers;
    }

    public ArrayList<TLRPC.TL_peerLocated> getCachedNearbyChats() {
        return this.cachedNearbyChats;
    }

    /* access modifiers changed from: protected */
    public void addSharingLocation(TLRPC.Message message) {
        SharingLocationInfo info = new SharingLocationInfo();
        info.did = message.dialog_id;
        info.mid = message.id;
        info.period = message.media.period;
        int i = message.media.proximity_notification_radius;
        info.proximityMeters = i;
        info.lastSentProximityMeters = i;
        info.account = this.currentAccount;
        info.messageObject = new MessageObject(this.currentAccount, message, false, false);
        info.stopTime = getConnectionsManager().getCurrentTime() + info.period;
        SharingLocationInfo old = this.sharingLocationsMap.get(info.did);
        this.sharingLocationsMap.put(info.did, info);
        if (old != null) {
            this.sharingLocations.remove(old);
        }
        this.sharingLocations.add(info);
        saveSharingLocation(info, 0);
        this.lastLocationSendTime = (SystemClock.elapsedRealtime() - 30000) + 5000;
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda14(this, old, info));
    }

    /* renamed from: lambda$addSharingLocation$12$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m647x90698a22(SharingLocationInfo old, SharingLocationInfo info) {
        if (old != null) {
            this.sharingLocationsUI.remove(old);
        }
        this.sharingLocationsUI.add(info);
        this.sharingLocationsMapUI.put(info.did, info);
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public boolean isSharingLocation(long did) {
        return this.sharingLocationsMapUI.indexOfKey(did) >= 0;
    }

    public SharingLocationInfo getSharingLocationInfo(long did) {
        return this.sharingLocationsMapUI.get(did);
    }

    public boolean setProximityLocation(long did, int meters, boolean broadcast) {
        SharingLocationInfo info = this.sharingLocationsMapUI.get(did);
        if (info != null) {
            info.proximityMeters = meters;
        }
        getMessagesStorage().getStorageQueue().postRunnable(new LocationController$$ExternalSyntheticLambda1(this, meters, did));
        if (broadcast) {
            Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda31(this));
        }
        return info != null;
    }

    /* renamed from: lambda$setProximityLocation$13$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m670x2fvar_(int meters, long did) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("UPDATE sharing_locations SET proximity = ? WHERE uid = ?");
            state.requery();
            state.bindInteger(1, meters);
            state.bindLong(2, did);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$setProximityLocation$14$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m671x1531f6e2() {
        broadcastLastKnownLocation(true);
    }

    public static int getHeading(Location location) {
        float val = location.getBearing();
        if (val <= 0.0f || val >= 1.0f) {
            return (int) val;
        }
        if (val < 0.5f) {
            return 360;
        }
        return 1;
    }

    private void loadSharingLocations() {
        getMessagesStorage().getStorageQueue().postRunnable(new LocationController$$ExternalSyntheticLambda26(this));
    }

    /* renamed from: lambda$loadSharingLocations$18$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m656x8b5ab3d0() {
        ArrayList<SharingLocationInfo> result = new ArrayList<>();
        ArrayList<TLRPC.User> users = new ArrayList<>();
        ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        try {
            ArrayList<Long> usersToLoad = new ArrayList<>();
            ArrayList<Long> chatsToLoad = new ArrayList<>();
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT uid, mid, date, period, message, proximity FROM sharing_locations WHERE 1", new Object[0]);
            while (cursor.next()) {
                SharingLocationInfo info = new SharingLocationInfo();
                info.did = cursor.longValue(0);
                info.mid = cursor.intValue(1);
                info.stopTime = cursor.intValue(2);
                info.period = cursor.intValue(3);
                info.proximityMeters = cursor.intValue(5);
                info.account = this.currentAccount;
                NativeByteBuffer data = cursor.byteBufferValue(4);
                if (data != null) {
                    info.messageObject = new MessageObject(this.currentAccount, TLRPC.Message.TLdeserialize(data, data.readInt32(false), false), false, false);
                    MessagesStorage.addUsersAndChatsFromMessage(info.messageObject.messageOwner, usersToLoad, chatsToLoad);
                    data.reuse();
                }
                result.add(info);
                if (DialogObject.isChatDialog(info.did)) {
                    if (!chatsToLoad.contains(Long.valueOf(-info.did))) {
                        chatsToLoad.add(Long.valueOf(-info.did));
                    }
                } else if (DialogObject.isUserDialog(info.did) && !usersToLoad.contains(Long.valueOf(info.did))) {
                    usersToLoad.add(Long.valueOf(info.did));
                }
            }
            cursor.dispose();
            if (!chatsToLoad.isEmpty()) {
                getMessagesStorage().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            if (!usersToLoad.isEmpty()) {
                getMessagesStorage().getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (!result.isEmpty()) {
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda9(this, users, chats, result));
        }
    }

    /* renamed from: lambda$loadSharingLocations$17$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m655xa619450f(ArrayList users, ArrayList chats, ArrayList result) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda8(this, result));
    }

    /* renamed from: lambda$loadSharingLocations$16$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m654xc0d7d64e(ArrayList result) {
        this.sharingLocations.addAll(result);
        for (int a = 0; a < this.sharingLocations.size(); a++) {
            SharingLocationInfo info = this.sharingLocations.get(a);
            this.sharingLocationsMap.put(info.did, info);
        }
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda7(this, result));
    }

    /* renamed from: lambda$loadSharingLocations$15$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m653xdb96678d(ArrayList result) {
        this.sharingLocationsUI.addAll(result);
        for (int a = 0; a < result.size(); a++) {
            SharingLocationInfo info = (SharingLocationInfo) result.get(a);
            this.sharingLocationsMapUI.put(info.did, info);
        }
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void saveSharingLocation(SharingLocationInfo info, int remove) {
        getMessagesStorage().getStorageQueue().postRunnable(new LocationController$$ExternalSyntheticLambda2(this, remove, info));
    }

    /* renamed from: lambda$saveSharingLocation$19$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m669xea365e5f(int remove, SharingLocationInfo info) {
        if (remove == 2) {
            try {
                getMessagesStorage().getDatabase().executeFast("DELETE FROM sharing_locations WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (remove == 1) {
            if (info != null) {
                SQLiteDatabase database = getMessagesStorage().getDatabase();
                database.executeFast("DELETE FROM sharing_locations WHERE uid = " + info.did).stepThis().dispose();
            }
        } else if (info != null) {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO sharing_locations VALUES(?, ?, ?, ?, ?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(info.messageObject.messageOwner.getObjectSize());
            info.messageObject.messageOwner.serializeToStream(data);
            state.bindLong(1, info.did);
            state.bindInteger(2, info.mid);
            state.bindInteger(3, info.stopTime);
            state.bindInteger(4, info.period);
            state.bindByteBuffer(5, data);
            state.bindInteger(6, info.proximityMeters);
            state.step();
            state.dispose();
            data.reuse();
        }
    }

    public void removeSharingLocation(long did) {
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda3(this, did));
    }

    /* renamed from: lambda$removeSharingLocation$22$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m668xvar_fbe(long did) {
        SharingLocationInfo info = this.sharingLocationsMap.get(did);
        this.sharingLocationsMap.remove(did);
        if (info != null) {
            TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
            req.peer = getMessagesController().getInputPeer(info.did);
            req.id = info.mid;
            req.flags |= 16384;
            req.media = new TLRPC.TL_inputMediaGeoLive();
            req.media.stopped = true;
            req.media.geo_point = new TLRPC.TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(req, new LocationController$$ExternalSyntheticLambda20(this));
            this.sharingLocations.remove(info);
            saveSharingLocation(info, 1);
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda12(this, info));
            if (this.sharingLocations.isEmpty()) {
                stop(true);
            }
        }
    }

    /* renamed from: lambda$removeSharingLocation$20$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m666x2ee3a23c(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    /* renamed from: lambda$removeSharingLocation$21$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m667x142510fd(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void startService() {
        try {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void stopService() {
        ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
    }

    public void removeAllLocationSharings() {
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda30(this));
    }

    /* renamed from: lambda$removeAllLocationSharings$25$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m665x31cdd005() {
        for (int a = 0; a < this.sharingLocations.size(); a++) {
            SharingLocationInfo info = this.sharingLocations.get(a);
            TLRPC.TL_messages_editMessage req = new TLRPC.TL_messages_editMessage();
            req.peer = getMessagesController().getInputPeer(info.did);
            req.id = info.mid;
            req.flags |= 16384;
            req.media = new TLRPC.TL_inputMediaGeoLive();
            req.media.stopped = true;
            req.media.geo_point = new TLRPC.TL_inputGeoPointEmpty();
            getConnectionsManager().sendRequest(req, new LocationController$$ExternalSyntheticLambda19(this));
        }
        this.sharingLocations.clear();
        this.sharingLocationsMap.clear();
        saveSharingLocation((SharingLocationInfo) null, 2);
        stop(true);
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda29(this));
    }

    /* renamed from: lambda$removeAllLocationSharings$23$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m663x674avar_(TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    /* renamed from: lambda$removeAllLocationSharings$24$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m664x4c8CLASSNAME() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        stopService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public void setGoogleMapLocation(Location location, boolean first) {
        Location location2;
        if (location != null) {
            this.lastLocationByGoogleMaps = true;
            if (first || ((location2 = this.lastKnownLocation) != null && location2.distanceTo(location) >= 20.0f)) {
                this.lastLocationSendTime = SystemClock.elapsedRealtime() - 30000;
                this.locationSentSinceLastGoogleMapUpdate = false;
            } else if (this.locationSentSinceLastGoogleMapUpdate) {
                this.lastLocationSendTime = (SystemClock.elapsedRealtime() - 30000) + 20000;
                this.locationSentSinceLastGoogleMapUpdate = false;
            }
            setLastKnownLocation(location);
        }
    }

    private void start() {
        if (!this.started) {
            this.lastLocationStartTime = SystemClock.elapsedRealtime();
            this.started = true;
            boolean ok = false;
            if (checkPlayServices()) {
                try {
                    this.googleApiClient.connect();
                    ok = true;
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (!ok) {
                try {
                    this.locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
                try {
                    this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
                try {
                    this.locationManager.requestLocationUpdates("passive", 1, 0.0f, this.passiveLocationListener);
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
                if (this.lastKnownLocation == null) {
                    try {
                        setLastKnownLocation(this.locationManager.getLastKnownLocation("gps"));
                        if (this.lastKnownLocation == null) {
                            setLastKnownLocation(this.locationManager.getLastKnownLocation("network"));
                        }
                    } catch (Exception e5) {
                        FileLog.e((Throwable) e5);
                    }
                }
            }
        }
    }

    private void stop(boolean empty) {
        if (!this.lookingForPeopleNearby && !this.shareMyCurrentLocation) {
            this.started = false;
            if (checkPlayServices()) {
                try {
                    LocationServices.FusedLocationApi.removeLocationUpdates(this.googleApiClient, (com.google.android.gms.location.LocationListener) this.fusedLocationListener);
                    this.googleApiClient.disconnect();
                } catch (Throwable e) {
                    FileLog.e(e, false);
                }
            }
            this.locationManager.removeUpdates(this.gpsLocationListener);
            if (empty) {
                this.locationManager.removeUpdates(this.networkLocationListener);
                this.locationManager.removeUpdates(this.passiveLocationListener);
            }
        }
    }

    public void startLocationLookupForPeopleNearby(boolean stop) {
        Utilities.stageQueue.postRunnable(new LocationController$$ExternalSyntheticLambda16(this, stop));
    }

    /* renamed from: lambda$startLocationLookupForPeopleNearby$26$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m673x93ad6f8f(boolean stop) {
        boolean z = !stop;
        this.lookingForPeopleNearby = z;
        if (z) {
            start();
        } else if (this.sharingLocations.isEmpty()) {
            stop(true);
        }
    }

    public Location getLastKnownLocation() {
        return this.lastKnownLocation;
    }

    public void loadLiveLocations(long did) {
        if (this.cacheRequests.indexOfKey(did) < 0) {
            this.cacheRequests.put(did, true);
            TLRPC.TL_messages_getRecentLocations req = new TLRPC.TL_messages_getRecentLocations();
            req.peer = getMessagesController().getInputPeer(did);
            req.limit = 100;
            getConnectionsManager().sendRequest(req, new LocationController$$ExternalSyntheticLambda21(this, did));
        }
    }

    /* renamed from: lambda$loadLiveLocations$28$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m652x22591025(long did, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda4(this, did, response));
        }
    }

    /* renamed from: lambda$loadLiveLocations$27$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m651x3d17a164(long did, TLObject response) {
        this.cacheRequests.delete(did);
        TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
        int a = 0;
        while (a < res.messages.size()) {
            if (!(res.messages.get(a).media instanceof TLRPC.TL_messageMediaGeoLive)) {
                res.messages.remove(a);
                a--;
            }
            a++;
        }
        getMessagesStorage().putUsersAndChats(res.users, res.chats, true, true);
        getMessagesController().putUsers(res.users, false);
        getMessagesController().putChats(res.chats, false);
        this.locationsCache.put(did, res.messages);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did), Integer.valueOf(this.currentAccount));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readMessageContents} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: org.telegram.tgnet.TLRPC$TL_channels_readMessageContents} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readMessageContents} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: org.telegram.tgnet.TLRPC$TL_messages_readMessageContents} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void markLiveLoactionsAsRead(long r9) {
        /*
            r8 = this;
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r9)
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            androidx.collection.LongSparseArray<java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>> r0 = r8.locationsCache
            java.lang.Object r0 = r0.get(r9)
            java.util.ArrayList r0 = (java.util.ArrayList) r0
            if (r0 == 0) goto L_0x00a7
            boolean r1 = r0.isEmpty()
            if (r1 == 0) goto L_0x0019
            goto L_0x00a7
        L_0x0019:
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r8.lastReadLocationTime
            java.lang.Object r1 = r1.get(r9)
            java.lang.Integer r1 = (java.lang.Integer) r1
            long r2 = android.os.SystemClock.elapsedRealtime()
            r4 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 / r4
            int r3 = (int) r2
            if (r1 == 0) goto L_0x0034
            int r2 = r1.intValue()
            int r2 = r2 + 60
            if (r2 <= r3) goto L_0x0034
            return
        L_0x0034:
            androidx.collection.LongSparseArray<java.lang.Integer> r2 = r8.lastReadLocationTime
            java.lang.Integer r4 = java.lang.Integer.valueOf(r3)
            r2.put(r9, r4)
            boolean r2 = org.telegram.messenger.DialogObject.isChatDialog(r9)
            if (r2 == 0) goto L_0x0079
            long r4 = -r9
            int r2 = r8.currentAccount
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r4, r2)
            if (r2 == 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$TL_channels_readMessageContents r2 = new org.telegram.tgnet.TLRPC$TL_channels_readMessageContents
            r2.<init>()
            r4 = 0
            int r5 = r0.size()
        L_0x0056:
            if (r4 >= r5) goto L_0x006c
            java.util.ArrayList<java.lang.Integer> r6 = r2.id
            java.lang.Object r7 = r0.get(r4)
            org.telegram.tgnet.TLRPC$Message r7 = (org.telegram.tgnet.TLRPC.Message) r7
            int r7 = r7.id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r6.add(r7)
            int r4 = r4 + 1
            goto L_0x0056
        L_0x006c:
            org.telegram.messenger.MessagesController r4 = r8.getMessagesController()
            long r5 = -r9
            org.telegram.tgnet.TLRPC$InputChannel r4 = r4.getInputChannel((long) r5)
            r2.channel = r4
            goto L_0x009a
        L_0x0079:
            org.telegram.tgnet.TLRPC$TL_messages_readMessageContents r2 = new org.telegram.tgnet.TLRPC$TL_messages_readMessageContents
            r2.<init>()
            r4 = 0
            int r5 = r0.size()
        L_0x0083:
            if (r4 >= r5) goto L_0x0099
            java.util.ArrayList<java.lang.Integer> r6 = r2.id
            java.lang.Object r7 = r0.get(r4)
            org.telegram.tgnet.TLRPC$Message r7 = (org.telegram.tgnet.TLRPC.Message) r7
            int r7 = r7.id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r6.add(r7)
            int r4 = r4 + 1
            goto L_0x0083
        L_0x0099:
            r4 = r2
        L_0x009a:
            org.telegram.tgnet.ConnectionsManager r4 = r8.getConnectionsManager()
            org.telegram.messenger.LocationController$$ExternalSyntheticLambda18 r5 = new org.telegram.messenger.LocationController$$ExternalSyntheticLambda18
            r5.<init>(r8)
            r4.sendRequest(r2, r5)
            return
        L_0x00a7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.LocationController.markLiveLoactionsAsRead(long):void");
    }

    /* renamed from: lambda$markLiveLoactionsAsRead$29$org-telegram-messenger-LocationController  reason: not valid java name */
    public /* synthetic */ void m657x777703c9(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_messages_affectedMessages) {
            TLRPC.TL_messages_affectedMessages res = (TLRPC.TL_messages_affectedMessages) response;
            getMessagesController().processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
    }

    public static int getLocationsCount() {
        int count = 0;
        for (int a = 0; a < 3; a++) {
            count += getInstance(a).sharingLocationsUI.size();
        }
        return count;
    }

    public static void fetchLocationAddress(Location location, LocationFetchCallback callback) {
        if (callback != null) {
            Runnable fetchLocationRunnable = callbacks.get(callback);
            if (fetchLocationRunnable != null) {
                Utilities.globalQueue.cancelRunnable(fetchLocationRunnable);
                callbacks.remove(callback);
            }
            if (location == null) {
                callback.onLocationAddressAvailable((String) null, (String) null, (Location) null);
                return;
            }
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            Runnable fetchLocationRunnable2 = new LocationController$$ExternalSyntheticLambda11(location, callback);
            dispatchQueue.postRunnable(fetchLocationRunnable2, 300);
            callbacks.put(callback, fetchLocationRunnable2);
        }
    }

    static /* synthetic */ void lambda$fetchLocationAddress$31(Location location, LocationFetchCallback callback) {
        String name;
        String displayName;
        try {
            List<Address> addresses = new Geocoder(ApplicationLoader.applicationContext, LocaleController.getInstance().getSystemDefaultLocale()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                boolean hasAny = false;
                StringBuilder nameBuilder = new StringBuilder();
                StringBuilder displayNameBuilder = new StringBuilder();
                String arg = address.getSubThoroughfare();
                if (!TextUtils.isEmpty(arg)) {
                    nameBuilder.append(arg);
                    hasAny = true;
                }
                String arg2 = address.getThoroughfare();
                if (!TextUtils.isEmpty(arg2)) {
                    if (nameBuilder.length() > 0) {
                        nameBuilder.append(" ");
                    }
                    nameBuilder.append(arg2);
                    hasAny = true;
                }
                if (!hasAny) {
                    String arg3 = address.getAdminArea();
                    if (!TextUtils.isEmpty(arg3)) {
                        if (nameBuilder.length() > 0) {
                            nameBuilder.append(", ");
                        }
                        nameBuilder.append(arg3);
                    }
                    String arg4 = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(arg4)) {
                        if (nameBuilder.length() > 0) {
                            nameBuilder.append(", ");
                        }
                        nameBuilder.append(arg4);
                    }
                }
                String arg5 = address.getLocality();
                if (!TextUtils.isEmpty(arg5)) {
                    if (nameBuilder.length() > 0) {
                        nameBuilder.append(", ");
                    }
                    nameBuilder.append(arg5);
                }
                String arg6 = address.getCountryName();
                if (!TextUtils.isEmpty(arg6)) {
                    if (nameBuilder.length() > 0) {
                        nameBuilder.append(", ");
                    }
                    nameBuilder.append(arg6);
                }
                String arg7 = address.getCountryName();
                if (!TextUtils.isEmpty(arg7)) {
                    if (displayNameBuilder.length() > 0) {
                        displayNameBuilder.append(", ");
                    }
                    displayNameBuilder.append(arg7);
                }
                String arg8 = address.getLocality();
                if (!TextUtils.isEmpty(arg8)) {
                    if (displayNameBuilder.length() > 0) {
                        displayNameBuilder.append(", ");
                    }
                    displayNameBuilder.append(arg8);
                }
                if (!hasAny) {
                    String arg9 = address.getAdminArea();
                    if (!TextUtils.isEmpty(arg9)) {
                        if (displayNameBuilder.length() > 0) {
                            displayNameBuilder.append(", ");
                        }
                        displayNameBuilder.append(arg9);
                    }
                    String arg10 = address.getSubAdminArea();
                    if (!TextUtils.isEmpty(arg10)) {
                        if (displayNameBuilder.length() > 0) {
                            displayNameBuilder.append(", ");
                        }
                        displayNameBuilder.append(arg10);
                    }
                }
                name = nameBuilder.toString();
                displayName = displayNameBuilder.toString();
            } else {
                displayName = String.format(Locale.US, "Unknown address (%f,%f)", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
                String str = displayName;
                name = displayName;
            }
        } catch (Exception e) {
            displayName = String.format(Locale.US, "Unknown address (%f,%f)", new Object[]{Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude())});
            String str2 = displayName;
            name = displayName;
        }
        AndroidUtilities.runOnUIThread(new LocationController$$ExternalSyntheticLambda22(callback, name, displayName, location));
    }

    static /* synthetic */ void lambda$fetchLocationAddress$30(LocationFetchCallback callback, String nameFinal, String displayNameFinal, Location location) {
        callbacks.remove(callback);
        callback.onLocationAddressAvailable(nameFinal, displayNameFinal, location);
    }
}
