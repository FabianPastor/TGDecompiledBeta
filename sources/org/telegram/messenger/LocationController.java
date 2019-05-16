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
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
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
                    LocationController.this.lastKnownLocation = location;
                } else if (!LocationController.this.started && location.distanceTo(LocationController.this.lastKnownLocation) > 20.0f) {
                    LocationController.this.lastKnownLocation = location;
                    LocationController.this.lastLocationSendTime = (System.currentTimeMillis() - 90000) + 5000;
                }
            }
        }
    }

    public static class SharingLocationInfo {
        public long did;
        public MessageObject messageObject;
        public int mid;
        public int period;
        public int stopTime;
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
        this.currentAccount = i;
        this.locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$jwDhs2Wxth9unque4gfUrSG9YJ8(this));
        loadSharingLocations();
    }

    public /* synthetic */ void lambda$new$0$LocationController() {
        LocationController instance = getInstance(this.currentAccount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(instance, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(instance, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(instance, NotificationCenter.replaceMessagesObjects);
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
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!this.sharingLocationsUI.isEmpty()) {
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

    private void broadcastLastKnownLocation() {
        if (this.lastKnownLocation != null) {
            int i;
            if (this.requests.size() != 0) {
                for (i = 0; i < this.requests.size(); i++) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requests.keyAt(i), false);
                }
                this.requests.clear();
            }
            i = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
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
                    if (Math.abs(i - i3) < 30 && Math.abs(geoPoint.lat - this.lastKnownLocation.getLatitude()) <= 1.0E-4d && Math.abs(geoPoint._long - this.lastKnownLocation.getLongitude()) <= 1.0E-4d) {
                    }
                }
                TL_messages_editMessage tL_messages_editMessage = new TL_messages_editMessage();
                tL_messages_editMessage.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) sharingLocationInfo.did);
                tL_messages_editMessage.id = sharingLocationInfo.mid;
                tL_messages_editMessage.flags |= 16384;
                tL_messages_editMessage.media = new TL_inputMediaGeoLive();
                InputMedia inputMedia = tL_messages_editMessage.media;
                inputMedia.stopped = false;
                inputMedia.geo_point = new TL_inputGeoPoint();
                tL_messages_editMessage.media.geo_point.lat = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLatitude());
                tL_messages_editMessage.media.geo_point._long = AndroidUtilities.fixLocationCoord(this.lastKnownLocation.getLongitude());
                this.requests.put(new int[]{ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editMessage, new -$$Lambda$LocationController$GgGvUosGIle_dcnPqBDWFGDVM7A(this, sharingLocationInfo, r5))}[0], 0);
            }
            ConnectionsManager.getInstance(this.currentAccount).resumeNetworkMaybe();
            stop(false);
        }
    }

    public /* synthetic */ void lambda$broadcastLastKnownLocation$2$LocationController(SharingLocationInfo sharingLocationInfo, int[] iArr, TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            if (tL_error.text.equals("MESSAGE_ID_INVALID")) {
                this.sharingLocations.remove(sharingLocationInfo);
                this.sharingLocationsMap.remove(sharingLocationInfo.did);
                saveSharingLocation(sharingLocationInfo, 1);
                this.requests.delete(iArr[0]);
                AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$xabGRpaKBh9IgfIoS-aqNvjHcyM(this, sharingLocationInfo));
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
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
    }

    public /* synthetic */ void lambda$null$1$LocationController(SharingLocationInfo sharingLocationInfo) {
        this.sharingLocationsUI.remove(sharingLocationInfo);
        this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
        if (this.sharingLocationsUI.isEmpty()) {
            stopService();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    /* Access modifiers changed, original: protected */
    public void update() {
        if (!this.sharingLocations.isEmpty()) {
            int i = 0;
            while (i < this.sharingLocations.size()) {
                SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocations.get(i);
                if (sharingLocationInfo.stopTime <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                    this.sharingLocations.remove(i);
                    this.sharingLocationsMap.remove(sharingLocationInfo.did);
                    saveSharingLocation(sharingLocationInfo, 1);
                    AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$PRv-6ObbwXmRgZBGVbIVGOjYQD8(this, sharingLocationInfo));
                    i--;
                }
                i++;
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

    public /* synthetic */ void lambda$update$3$LocationController(SharingLocationInfo sharingLocationInfo) {
        this.sharingLocationsUI.remove(sharingLocationInfo);
        this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$CqwMtWkVqaOCz-mvGUr_tzsuYCI(this));
    }

    public /* synthetic */ void lambda$cleanup$4$LocationController() {
        this.requests.clear();
        this.sharingLocationsMap.clear();
        this.sharingLocations.clear();
        this.lastKnownLocation = null;
        stop(true);
    }

    /* Access modifiers changed, original: protected */
    public void addSharingLocation(long j, int i, int i2, Message message) {
        SharingLocationInfo sharingLocationInfo = new SharingLocationInfo();
        sharingLocationInfo.did = j;
        sharingLocationInfo.mid = i;
        sharingLocationInfo.period = i2;
        sharingLocationInfo.messageObject = new MessageObject(this.currentAccount, message, false);
        sharingLocationInfo.stopTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + i2;
        SharingLocationInfo sharingLocationInfo2 = (SharingLocationInfo) this.sharingLocationsMap.get(j);
        this.sharingLocationsMap.put(j, sharingLocationInfo);
        if (sharingLocationInfo2 != null) {
            this.sharingLocations.remove(sharingLocationInfo2);
        }
        this.sharingLocations.add(sharingLocationInfo);
        saveSharingLocation(sharingLocationInfo, 0);
        this.lastLocationSendTime = (System.currentTimeMillis() - 90000) + 5000;
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$zAJ9cmnQja1jAmGuw23-E99HA_0(this, sharingLocationInfo2, sharingLocationInfo));
    }

    public /* synthetic */ void lambda$addSharingLocation$5$LocationController(SharingLocationInfo sharingLocationInfo, SharingLocationInfo sharingLocationInfo2) {
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$LocationController$CedxaXVEsRh3MfbiwLY0cCEiBNY(this));
    }

    public /* synthetic */ void lambda$loadSharingLocations$9$LocationController() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        try {
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            SQLiteCursor queryFinalized = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT uid, mid, date, period, message FROM sharing_locations WHERE 1", new Object[0]);
            while (queryFinalized.next()) {
                SharingLocationInfo sharingLocationInfo = new SharingLocationInfo();
                sharingLocationInfo.did = queryFinalized.longValue(0);
                sharingLocationInfo.mid = queryFinalized.intValue(1);
                sharingLocationInfo.stopTime = queryFinalized.intValue(2);
                sharingLocationInfo.period = queryFinalized.intValue(3);
                NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(4);
                if (byteBufferValue != null) {
                    sharingLocationInfo.messageObject = new MessageObject(this.currentAccount, Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), false);
                    MessagesStorage.addUsersAndChatsFromMessage(sharingLocationInfo.messageObject.messageOwner, arrayList4, arrayList5);
                    byteBufferValue.reuse();
                }
                arrayList.add(sharingLocationInfo);
                int i = (int) sharingLocationInfo.did;
                long j = sharingLocationInfo.did;
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
            String str = ",";
            if (!arrayList5.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getChatsInternal(TextUtils.join(str, arrayList5), arrayList3);
            }
            if (!arrayList4.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).getUsersInternal(TextUtils.join(str, arrayList4), arrayList2);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$0L0Myfw-8A7gbZ9tpB4LMVz7Qgw(this, arrayList2, arrayList3, arrayList));
        }
    }

    public /* synthetic */ void lambda$null$8$LocationController(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, true);
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$KzQNHV5exKVTMUhNM3DuUpb5ALk(this, arrayList3));
    }

    public /* synthetic */ void lambda$null$7$LocationController(ArrayList arrayList) {
        this.sharingLocations.addAll(arrayList);
        for (int i = 0; i < this.sharingLocations.size(); i++) {
            SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocations.get(i);
            this.sharingLocationsMap.put(sharingLocationInfo.did, sharingLocationInfo);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$n806eE82hDy_EKjzj-moV7XdY8A(this, arrayList));
    }

    public /* synthetic */ void lambda$null$6$LocationController(ArrayList arrayList) {
        this.sharingLocationsUI.addAll(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) arrayList.get(i);
            this.sharingLocationsMapUI.put(sharingLocationInfo.did, sharingLocationInfo);
        }
        startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
    }

    private void saveSharingLocation(SharingLocationInfo sharingLocationInfo, int i) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$LocationController$yQY9F3qag_AziWeeanO81WIpkbw(this, i, sharingLocationInfo));
    }

    public /* synthetic */ void lambda$saveSharingLocation$10$LocationController(int i, SharingLocationInfo sharingLocationInfo) {
        if (i == 2) {
            try {
                MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("DELETE FROM sharing_locations WHERE 1").stepThis().dispose();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (i == 1) {
            if (sharingLocationInfo != null) {
                SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("DELETE FROM sharing_locations WHERE uid = ");
                stringBuilder.append(sharingLocationInfo.did);
                database.executeFast(stringBuilder.toString()).stepThis().dispose();
            }
        } else if (sharingLocationInfo != null) {
            SQLitePreparedStatement executeFast = MessagesStorage.getInstance(this.currentAccount).getDatabase().executeFast("REPLACE INTO sharing_locations VALUES(?, ?, ?, ?, ?)");
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$--2b05V3CLASSNAMEL8B6e6NaqY7nkE0(this, j));
    }

    public /* synthetic */ void lambda$removeSharingLocation$13$LocationController(long j) {
        SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocationsMap.get(j);
        this.sharingLocationsMap.remove(j);
        if (sharingLocationInfo != null) {
            TL_messages_editMessage tL_messages_editMessage = new TL_messages_editMessage();
            tL_messages_editMessage.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) sharingLocationInfo.did);
            tL_messages_editMessage.id = sharingLocationInfo.mid;
            tL_messages_editMessage.flags |= 16384;
            tL_messages_editMessage.media = new TL_inputMediaGeoLive();
            InputMedia inputMedia = tL_messages_editMessage.media;
            inputMedia.stopped = true;
            inputMedia.geo_point = new TL_inputGeoPointEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editMessage, new -$$Lambda$LocationController$8lTryql87YvYEN2OCsICR_gOP3g(this));
            this.sharingLocations.remove(sharingLocationInfo);
            saveSharingLocation(sharingLocationInfo, 1);
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$N9sfOku6NSx_Uin6Q7qXme__lGo(this, sharingLocationInfo));
            if (this.sharingLocations.isEmpty()) {
                stop(true);
            }
        }
    }

    public /* synthetic */ void lambda$null$11$LocationController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$null$12$LocationController(SharingLocationInfo sharingLocationInfo) {
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$LocationController$unkueEB9icgZn5pQTky3flbGZuo(this));
    }

    public /* synthetic */ void lambda$removeAllLocationSharings$16$LocationController() {
        for (int i = 0; i < this.sharingLocations.size(); i++) {
            SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocations.get(i);
            TL_messages_editMessage tL_messages_editMessage = new TL_messages_editMessage();
            tL_messages_editMessage.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) sharingLocationInfo.did);
            tL_messages_editMessage.id = sharingLocationInfo.mid;
            tL_messages_editMessage.flags |= 16384;
            tL_messages_editMessage.media = new TL_inputMediaGeoLive();
            InputMedia inputMedia = tL_messages_editMessage.media;
            inputMedia.stopped = true;
            inputMedia.geo_point = new TL_inputGeoPointEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editMessage, new -$$Lambda$LocationController$o4AyynaxXNLOklLrLCjAeV7nwjM(this));
        }
        this.sharingLocations.clear();
        this.sharingLocationsMap.clear();
        saveSharingLocation(null, 2);
        stop(true);
        AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$Kp3u6_UIa9KXJzaDwAYVrLB6ovM(this));
    }

    public /* synthetic */ void lambda$null$14$LocationController(TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) tLObject, false);
        }
    }

    public /* synthetic */ void lambda$null$15$LocationController() {
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
                        this.lastLocationSendTime = (System.currentTimeMillis() - 90000) + 20000;
                        this.locationSentSinceLastGoogleMapUpdate = false;
                    }
                    this.lastKnownLocation = location;
                }
            }
            this.lastLocationSendTime = System.currentTimeMillis() - 90000;
            this.locationSentSinceLastGoogleMapUpdate = false;
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

    private void stop(boolean z) {
        this.started = false;
        this.locationManager.removeUpdates(this.gpsLocationListener);
        if (z) {
            this.locationManager.removeUpdates(this.networkLocationListener);
            this.locationManager.removeUpdates(this.passiveLocationListener);
        }
    }

    public void loadLiveLocations(long j) {
        if (this.cacheRequests.indexOfKey(j) < 0) {
            this.cacheRequests.put(j, Boolean.valueOf(true));
            TL_messages_getRecentLocations tL_messages_getRecentLocations = new TL_messages_getRecentLocations();
            tL_messages_getRecentLocations.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) j);
            tL_messages_getRecentLocations.limit = 100;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getRecentLocations, new -$$Lambda$LocationController$9e6Jr8NCwhd7x0jAE3itGEDvCZ8(this, j));
        }
    }

    public /* synthetic */ void lambda$loadLiveLocations$18$LocationController(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationController$w0C2adFKR_FSdQ3YkLyieoe840U(this, j, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$17$LocationController(long j, TLObject tLObject) {
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
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
        MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(messages_messages.chats, false);
        this.locationsCache.put(j, messages_messages.messages);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(j), Integer.valueOf(this.currentAccount));
    }

    public static int getLocationsCount() {
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            i += getInstance(i2).sharingLocationsUI.size();
        }
        return i;
    }
}
