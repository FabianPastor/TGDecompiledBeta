package org.telegram.messenger;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseIntArray;
import java.util.ArrayList;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputGeoPointEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentLocations;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class LocationController implements NotificationCenterDelegate {
    private static final int BACKGROUD_UPDATE_TIME = 90000;
    private static final int FOREGROUND_UPDATE_TIME = 20000;
    private static volatile LocationController[] Instance = new LocationController[3];
    private static final int LOCATION_ACQUIRE_TIME = 10000;
    private static final double eps = 1.0E-4d;
    private LongSparseArray<Boolean> cacheRequests = new LongSparseArray();
    private int currentAccount;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener();
    private Location lastKnownLocation;
    private boolean lastLocationByGoogleMaps;
    private long lastLocationSendTime;
    private long lastLocationStartTime;
    private LocationManager locationManager;
    private boolean locationSentSinceLastGoogleMapUpdate = true;
    public LongSparseArray<ArrayList<Message>> locationsCache = new LongSparseArray();
    private GpsLocationListener networkLocationListener = new GpsLocationListener();
    private GpsLocationListener passiveLocationListener = new GpsLocationListener();
    private SparseIntArray requests = new SparseIntArray();
    private ArrayList<SharingLocationInfo> sharingLocations = new ArrayList();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMap = new LongSparseArray();
    private LongSparseArray<SharingLocationInfo> sharingLocationsMapUI = new LongSparseArray();
    public ArrayList<SharingLocationInfo> sharingLocationsUI = new ArrayList();
    private boolean started;

    private class GpsLocationListener implements LocationListener {
        private GpsLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                if (LocationController.this.lastKnownLocation == null || !(this == LocationController.this.networkLocationListener || this == LocationController.this.passiveLocationListener)) {
                    LocationController.this.lastKnownLocation = location;
                } else if (!LocationController.this.started && location.distanceTo(LocationController.this.lastKnownLocation) > 20.0f) {
                    LocationController.this.lastKnownLocation = location;
                    LocationController.this.lastLocationSendTime = (System.currentTimeMillis() - 90000) + 5000;
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

    public static class SharingLocationInfo {
        public long did;
        public MessageObject messageObject;
        public int mid;
        public int period;
        public int stopTime;
    }

    public static LocationController getInstance(int num) {
        Throwable th;
        LocationController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (LocationController.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        LocationController[] locationControllerArr = Instance;
                        LocationController localInstance2 = new LocationController(num);
                        try {
                            locationControllerArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public LocationController(int instance) {
        this.currentAccount = instance;
        this.locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        AndroidUtilities.runOnUIThread(new LocationController$$Lambda$0(this));
        loadSharingLocations();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$0$LocationController() {
        LocationController locationController = getInstance(this.currentAccount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(locationController, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(locationController, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(locationController, NotificationCenter.replaceMessagesObjects);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        long did;
        ArrayList<Message> messages;
        int a;
        MessageObject messageObject;
        int b;
        if (id == NotificationCenter.didReceiveNewMessages) {
            did = ((Long) args[0]).longValue();
            if (isSharingLocation(did)) {
                messages = (ArrayList) this.locationsCache.get(did);
                if (messages != null) {
                    ArrayList<MessageObject> arr = args[1];
                    boolean added = false;
                    for (a = 0; a < arr.size(); a++) {
                        messageObject = (MessageObject) arr.get(a);
                        if (messageObject.isLiveLocation()) {
                            added = true;
                            boolean replaced = false;
                            for (b = 0; b < messages.size(); b++) {
                                if (((Message) messages.get(b)).from_id == messageObject.messageOwner.from_id) {
                                    replaced = true;
                                    messages.set(b, messageObject.messageOwner);
                                    break;
                                }
                            }
                            if (!replaced) {
                                messages.add(messageObject.messageOwner);
                            }
                        }
                    }
                    if (added) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did), Integer.valueOf(this.currentAccount));
                    }
                }
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            if (!this.sharingLocationsUI.isEmpty()) {
                ArrayList<Integer> markAsDeletedMessages = args[0];
                int channelId = ((Integer) args[1]).intValue();
                ArrayList<Long> toRemove = null;
                for (a = 0; a < this.sharingLocationsUI.size(); a++) {
                    SharingLocationInfo info = (SharingLocationInfo) this.sharingLocationsUI.get(a);
                    if (channelId == (info.messageObject != null ? info.messageObject.getChannelId() : 0) && markAsDeletedMessages.contains(Integer.valueOf(info.mid))) {
                        if (toRemove == null) {
                            toRemove = new ArrayList();
                        }
                        toRemove.add(Long.valueOf(info.did));
                    }
                }
                if (toRemove != null) {
                    for (a = 0; a < toRemove.size(); a++) {
                        removeSharingLocation(((Long) toRemove.get(a)).longValue());
                    }
                }
            }
        } else if (id == NotificationCenter.replaceMessagesObjects) {
            did = ((Long) args[0]).longValue();
            if (isSharingLocation(did)) {
                messages = (ArrayList) this.locationsCache.get(did);
                if (messages != null) {
                    boolean updated = false;
                    ArrayList<MessageObject> messageObjects = args[1];
                    for (a = 0; a < messageObjects.size(); a++) {
                        messageObject = (MessageObject) messageObjects.get(a);
                        b = 0;
                        while (b < messages.size()) {
                            if (((Message) messages.get(b)).from_id == messageObject.messageOwner.from_id) {
                                if (messageObject.isLiveLocation()) {
                                    messages.set(b, messageObject.messageOwner);
                                } else {
                                    messages.remove(b);
                                }
                                updated = true;
                            } else {
                                b++;
                            }
                        }
                    }
                    if (updated) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did), Integer.valueOf(this.currentAccount));
                    }
                }
            }
        }
    }

    private void broadcastLastKnownLocation() {
        if (this.lastKnownLocation != null) {
            int a;
            if (this.requests.size() != 0) {
                for (a = 0; a < this.requests.size(); a++) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requests.keyAt(a), false);
                }
                this.requests.clear();
            }
            int date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            for (a = 0; a < this.sharingLocations.size(); a++) {
                SharingLocationInfo info = (SharingLocationInfo) this.sharingLocations.get(a);
                if (!(info.messageObject.messageOwner.media == null || info.messageObject.messageOwner.media.geo == null)) {
                    int messageDate = info.messageObject.messageOwner.edit_date != 0 ? info.messageObject.messageOwner.edit_date : info.messageObject.messageOwner.date;
                    GeoPoint point = info.messageObject.messageOwner.media.geo;
                    if (Math.abs(date - messageDate) < 30 && Math.abs(point.lat - this.lastKnownLocation.getLatitude()) <= 1.0E-4d && Math.abs(point._long - this.lastKnownLocation.getLongitude()) <= 1.0E-4d) {
                    }
                }
                TL_messages_editMessage req = new TL_messages_editMessage();
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) info.did);
                req.id = info.mid;
                req.flags |= 16384;
                req.media = new TL_inputMediaGeoLive();
                req.media.stopped = false;
                req.media.geo_point = new TL_inputGeoPoint();
                req.media.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                req.media.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
                this.requests.put(new int[]{ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LocationController$$Lambda$1(this, info, reqId))}[0], 0);
            }
            ConnectionsManager.getInstance(this.currentAccount).resumeNetworkMaybe();
            stop(false);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$broadcastLastKnownLocation$2$LocationController(SharingLocationInfo info, int[] reqId, TLObject response, TL_error error) {
        if (error == null) {
            Updates updates = (Updates) response;
            boolean updated = false;
            for (int a1 = 0; a1 < updates.updates.size(); a1++) {
                Update update = (Update) updates.updates.get(a1);
                if (update instanceof TL_updateEditMessage) {
                    updated = true;
                    info.messageObject.messageOwner = ((TL_updateEditMessage) update).message;
                } else if (update instanceof TL_updateEditChannelMessage) {
                    updated = true;
                    info.messageObject.messageOwner = ((TL_updateEditChannelMessage) update).message;
                }
            }
            if (updated) {
                saveSharingLocation(info, 0);
            }
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
        } else if (error.text.equals("MESSAGE_ID_INVALID")) {
            this.sharingLocations.remove(info);
            this.sharingLocationsMap.remove(info.did);
            saveSharingLocation(info, 1);
            this.requests.delete(reqId[0]);
            AndroidUtilities.runOnUIThread(new LocationController$$Lambda$18(this, info));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$1$LocationController(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    /* Access modifiers changed, original: protected */
    public void update() {
        if (!this.sharingLocations.isEmpty()) {
            int a = 0;
            while (a < this.sharingLocations.size()) {
                SharingLocationInfo info = (SharingLocationInfo) this.sharingLocations.get(a);
                if (info.stopTime <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                    this.sharingLocations.remove(a);
                    this.sharingLocationsMap.remove(info.did);
                    saveSharingLocation(info, 1);
                    AndroidUtilities.runOnUIThread(new LocationController$$Lambda$2(this, info));
                    a--;
                }
                a++;
            }
            if (this.started) {
                if (this.lastLocationByGoogleMaps || Math.abs(this.lastLocationStartTime - System.currentTimeMillis()) > 10000) {
                    this.lastLocationByGoogleMaps = false;
                    this.locationSentSinceLastGoogleMapUpdate = true;
                    this.lastLocationSendTime = System.currentTimeMillis();
                    broadcastLastKnownLocation();
                }
            } else if (Math.abs(this.lastLocationSendTime - System.currentTimeMillis()) > 90000) {
                this.lastLocationStartTime = System.currentTimeMillis();
                start();
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$update$3$LocationController(SharingLocationInfo info) {
        this.sharingLocationsUI.remove(info);
        this.sharingLocationsMapUI.remove(info.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public void cleanup() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        this.locationsCache.clear();
        this.cacheRequests.clear();
        stopService();
        Utilities.stageQueue.postRunnable(new LocationController$$Lambda$3(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$cleanup$4$LocationController() {
        this.requests.clear();
        this.sharingLocationsMap.clear();
        this.sharingLocations.clear();
        this.lastKnownLocation = null;
        stop(true);
    }

    /* Access modifiers changed, original: protected */
    public void addSharingLocation(long did, int mid, int period, Message message) {
        SharingLocationInfo info = new SharingLocationInfo();
        info.did = did;
        info.mid = mid;
        info.period = period;
        info.messageObject = new MessageObject(this.currentAccount, message, false);
        info.stopTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + period;
        SharingLocationInfo old = (SharingLocationInfo) this.sharingLocationsMap.get(did);
        this.sharingLocationsMap.put(did, info);
        if (old != null) {
            this.sharingLocations.remove(old);
        }
        this.sharingLocations.add(info);
        saveSharingLocation(info, 0);
        this.lastLocationSendTime = (System.currentTimeMillis() - 90000) + 5000;
        AndroidUtilities.runOnUIThread(new LocationController$$Lambda$4(this, old, info));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addSharingLocation$5$LocationController(SharingLocationInfo old, SharingLocationInfo info) {
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
        return (SharingLocationInfo) this.sharingLocationsMapUI.get(did);
    }

    private void loadSharingLocations() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new LocationController$$Lambda$5(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadSharingLocations$9$LocationController() {
        ArrayList<SharingLocationInfo> result = new ArrayList();
        ArrayList<User> users = new ArrayList();
        ArrayList<Chat> chats = new ArrayList();
        try {
            ArrayList<Integer> usersToLoad = new ArrayList();
            ArrayList<Integer> chatsToLoad = new ArrayList();
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT uid, mid, date, period, message FROM sharing_locations WHERE 1", new Object[0]);
            while (cursor.next()) {
                SharingLocationInfo info = new SharingLocationInfo();
                info.did = cursor.longValue(0);
                info.mid = cursor.intValue(1);
                info.stopTime = cursor.intValue(2);
                info.period = cursor.intValue(3);
                NativeByteBuffer data = cursor.byteBufferValue(4);
                if (data != null) {
                    info.messageObject = new MessageObject(this.currentAccount, Message.TLdeserialize(data, data.readInt32(false), false), false);
                    MessagesStorage.addUsersAndChatsFromMessage(info.messageObject.messageOwner, usersToLoad, chatsToLoad);
                    data.reuse();
                }
                result.add(info);
                int lower_id = (int) info.did;
                int high_id = (int) (info.did >> 32);
                if (lower_id != 0) {
                    if (lower_id < 0) {
                        if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                            chatsToLoad.add(Integer.valueOf(-lower_id));
                        }
                    } else if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                        usersToLoad.add(Integer.valueOf(lower_id));
                    }
                }
            }
            cursor.dispose();
            if (!chatsToLoad.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
            }
            if (!usersToLoad.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(",", usersToLoad), users);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!result.isEmpty()) {
            AndroidUtilities.runOnUIThread(new LocationController$$Lambda$15(this, users, chats, result));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$8$LocationController(ArrayList users, ArrayList chats, ArrayList result) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        Utilities.stageQueue.postRunnable(new LocationController$$Lambda$16(this, result));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$7$LocationController(ArrayList result) {
        this.sharingLocations.addAll(result);
        for (int a = 0; a < this.sharingLocations.size(); a++) {
            SharingLocationInfo info = (SharingLocationInfo) this.sharingLocations.get(a);
            this.sharingLocationsMap.put(info.did, info);
        }
        AndroidUtilities.runOnUIThread(new LocationController$$Lambda$17(this, result));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$6$LocationController(ArrayList result) {
        this.sharingLocationsUI.addAll(result);
        for (int a = 0; a < result.size(); a++) {
            SharingLocationInfo info = (SharingLocationInfo) result.get(a);
            this.sharingLocationsMapUI.put(info.did, info);
        }
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void saveSharingLocation(SharingLocationInfo info, int remove) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new LocationController$$Lambda$6(this, remove, info));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$saveSharingLocation$10$LocationController(int remove, SharingLocationInfo info) {
        if (remove == 2) {
            try {
                MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM sharing_locations WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (remove == 1) {
            if (info != null) {
                MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM sharing_locations WHERE uid = " + info.did).stepThis().dispose();
            }
        } else if (info != null) {
            SQLitePreparedStatement state = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO sharing_locations VALUES(?, ?, ?, ?, ?)");
            state.requery();
            NativeByteBuffer data = new NativeByteBuffer(info.messageObject.messageOwner.getObjectSize());
            info.messageObject.messageOwner.serializeToStream(data);
            state.bindLong(1, info.did);
            state.bindInteger(2, info.mid);
            state.bindInteger(3, info.stopTime);
            state.bindInteger(4, info.period);
            state.bindByteBuffer(5, data);
            state.step();
            state.dispose();
            data.reuse();
        }
    }

    public void removeSharingLocation(long did) {
        Utilities.stageQueue.postRunnable(new LocationController$$Lambda$7(this, did));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$removeSharingLocation$13$LocationController(long did) {
        SharingLocationInfo info = (SharingLocationInfo) this.sharingLocationsMap.get(did);
        this.sharingLocationsMap.remove(did);
        if (info != null) {
            TL_messages_editMessage req = new TL_messages_editMessage();
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) info.did);
            req.id = info.mid;
            req.flags |= 16384;
            req.media = new TL_inputMediaGeoLive();
            req.media.stopped = true;
            req.media.geo_point = new TL_inputGeoPointEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LocationController$$Lambda$13(this));
            this.sharingLocations.remove(info);
            saveSharingLocation(info, 1);
            AndroidUtilities.runOnUIThread(new LocationController$$Lambda$14(this, info));
            if (this.sharingLocations.isEmpty()) {
                stop(true);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$11$LocationController(TLObject response, TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) response, false);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$12$LocationController(SharingLocationInfo info) {
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
        Utilities.stageQueue.postRunnable(new LocationController$$Lambda$8(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$removeAllLocationSharings$16$LocationController() {
        for (int a = 0; a < this.sharingLocations.size(); a++) {
            SharingLocationInfo info = (SharingLocationInfo) this.sharingLocations.get(a);
            TL_messages_editMessage req = new TL_messages_editMessage();
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) info.did);
            req.id = info.mid;
            req.flags |= 16384;
            req.media = new TL_inputMediaGeoLive();
            req.media.stopped = true;
            req.media.geo_point = new TL_inputGeoPointEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LocationController$$Lambda$11(this));
        }
        this.sharingLocations.clear();
        this.sharingLocationsMap.clear();
        saveSharingLocation(null, 2);
        stop(true);
        AndroidUtilities.runOnUIThread(new LocationController$$Lambda$12(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$14$LocationController(TLObject response, TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) response, false);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$15$LocationController() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        stopService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    public void setGoogleMapLocation(Location location, boolean first) {
        if (location != null) {
            this.lastLocationByGoogleMaps = true;
            if (first || (this.lastKnownLocation != null && this.lastKnownLocation.distanceTo(location) >= 20.0f)) {
                this.lastLocationSendTime = System.currentTimeMillis() - 90000;
                this.locationSentSinceLastGoogleMapUpdate = false;
            } else if (this.locationSentSinceLastGoogleMapUpdate) {
                this.lastLocationSendTime = (System.currentTimeMillis() - 90000) + 20000;
                this.locationSentSinceLastGoogleMapUpdate = false;
            }
            this.lastKnownLocation = location;
        }
    }

    private void start() {
        if (!this.started) {
            this.lastLocationStartTime = System.currentTimeMillis();
            this.started = true;
            try {
                this.locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            try {
                this.locationManager.requestLocationUpdates("passive", 1, 0.0f, this.passiveLocationListener);
            } catch (Exception e22) {
                FileLog.e(e22);
            }
            if (this.lastKnownLocation == null) {
                try {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                    if (this.lastKnownLocation == null) {
                        this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                    }
                } catch (Exception e222) {
                    FileLog.e(e222);
                }
            }
        }
    }

    private void stop(boolean empty) {
        this.started = false;
        this.locationManager.removeUpdates(this.gpsLocationListener);
        if (empty) {
            this.locationManager.removeUpdates(this.networkLocationListener);
            this.locationManager.removeUpdates(this.passiveLocationListener);
        }
    }

    public void loadLiveLocations(long did) {
        if (this.cacheRequests.indexOfKey(did) < 0) {
            this.cacheRequests.put(did, Boolean.valueOf(true));
            TL_messages_getRecentLocations req = new TL_messages_getRecentLocations();
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) did);
            req.limit = 100;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new LocationController$$Lambda$9(this, did));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadLiveLocations$18$LocationController(long did, TLObject response, TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new LocationController$$Lambda$10(this, did, response));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$17$LocationController(long did, TLObject response) {
        this.cacheRequests.delete(did);
        messages_Messages res = (messages_Messages) response;
        int a = 0;
        while (a < res.messages.size()) {
            if (!(((Message) res.messages.get(a)).media instanceof TL_messageMediaGeoLive)) {
                res.messages.remove(a);
                a--;
            }
            a++;
        }
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        this.locationsCache.put(did, res.messages);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(did), Integer.valueOf(this.currentAccount));
    }

    public static int getLocationsCount() {
        int count = 0;
        for (int a = 0; a < 3; a++) {
            count += getInstance(a).sharingLocationsUI.size();
        }
        return count;
    }
}
