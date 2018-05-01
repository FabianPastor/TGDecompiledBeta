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
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
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

    /* renamed from: org.telegram.messenger.LocationController$1 */
    class C02311 implements Runnable {
        C02311() {
        }

        public void run() {
            LocationController instance = LocationController.getInstance(LocationController.this.currentAccount);
            NotificationCenter.getInstance(LocationController.this.currentAccount).addObserver(instance, NotificationCenter.didReceivedNewMessages);
            NotificationCenter.getInstance(LocationController.this.currentAccount).addObserver(instance, NotificationCenter.messagesDeleted);
            NotificationCenter.getInstance(LocationController.this.currentAccount).addObserver(instance, NotificationCenter.replaceMessagesObjects);
        }
    }

    /* renamed from: org.telegram.messenger.LocationController$4 */
    class C02344 implements Runnable {
        C02344() {
        }

        public void run() {
            LocationController.this.requests.clear();
            LocationController.this.sharingLocationsMap.clear();
            LocationController.this.sharingLocations.clear();
            LocationController.this.lastKnownLocation = null;
            LocationController.this.stop(true);
        }
    }

    /* renamed from: org.telegram.messenger.LocationController$6 */
    class C02396 implements Runnable {
        C02396() {
        }

        public void run() {
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            final ArrayList arrayList3 = new ArrayList();
            try {
                Iterable arrayList4 = new ArrayList();
                Iterable arrayList5 = new ArrayList();
                SQLiteCursor queryFinalized = MessagesStorage.getInstance(LocationController.this.currentAccount).getDatabase().queryFinalized("SELECT uid, mid, date, period, message FROM sharing_locations WHERE 1", new Object[0]);
                while (queryFinalized.next()) {
                    SharingLocationInfo sharingLocationInfo = new SharingLocationInfo();
                    sharingLocationInfo.did = queryFinalized.longValue(0);
                    sharingLocationInfo.mid = queryFinalized.intValue(1);
                    sharingLocationInfo.stopTime = queryFinalized.intValue(2);
                    sharingLocationInfo.period = queryFinalized.intValue(3);
                    AbstractSerializedData byteBufferValue = queryFinalized.byteBufferValue(4);
                    if (byteBufferValue != null) {
                        sharingLocationInfo.messageObject = new MessageObject(LocationController.this.currentAccount, Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false), false);
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
                if (!arrayList5.isEmpty()) {
                    MessagesStorage.getInstance(LocationController.this.currentAccount).getChatsInternal(TextUtils.join(",", arrayList5), arrayList3);
                }
                if (!arrayList4.isEmpty()) {
                    MessagesStorage.getInstance(LocationController.this.currentAccount).getUsersInternal(TextUtils.join(",", arrayList4), arrayList2);
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (!arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() {

                    /* renamed from: org.telegram.messenger.LocationController$6$1$1 */
                    class C02371 implements Runnable {

                        /* renamed from: org.telegram.messenger.LocationController$6$1$1$1 */
                        class C02361 implements Runnable {
                            C02361() {
                            }

                            public void run() {
                                LocationController.this.sharingLocationsUI.addAll(arrayList);
                                for (int i = 0; i < arrayList.size(); i++) {
                                    SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) arrayList.get(i);
                                    LocationController.this.sharingLocationsMapUI.put(sharingLocationInfo.did, sharingLocationInfo);
                                }
                                LocationController.this.startService();
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
                            }
                        }

                        C02371() {
                        }

                        public void run() {
                            LocationController.this.sharingLocations.addAll(arrayList);
                            for (int i = 0; i < LocationController.this.sharingLocations.size(); i++) {
                                SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) LocationController.this.sharingLocations.get(i);
                                LocationController.this.sharingLocationsMap.put(sharingLocationInfo.did, sharingLocationInfo);
                            }
                            AndroidUtilities.runOnUIThread(new C02361());
                        }
                    }

                    public void run() {
                        MessagesController.getInstance(LocationController.this.currentAccount).putUsers(arrayList2, true);
                        MessagesController.getInstance(LocationController.this.currentAccount).putChats(arrayList3, true);
                        Utilities.stageQueue.postRunnable(new C02371());
                    }
                });
            }
        }
    }

    /* renamed from: org.telegram.messenger.LocationController$9 */
    class C02449 implements Runnable {

        /* renamed from: org.telegram.messenger.LocationController$9$2 */
        class C02432 implements Runnable {
            C02432() {
            }

            public void run() {
                LocationController.this.sharingLocationsUI.clear();
                LocationController.this.sharingLocationsMapUI.clear();
                LocationController.this.stopService();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
            }
        }

        /* renamed from: org.telegram.messenger.LocationController$9$1 */
        class C18121 implements RequestDelegate {
            C18121() {
            }

            public void run(TLObject tLObject, TL_error tL_error) {
                if (tL_error == null) {
                    MessagesController.getInstance(LocationController.this.currentAccount).processUpdates((Updates) tLObject, false);
                }
            }
        }

        C02449() {
        }

        public void run() {
            for (int i = 0; i < LocationController.this.sharingLocations.size(); i++) {
                SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) LocationController.this.sharingLocations.get(i);
                TLObject tL_messages_editMessage = new TL_messages_editMessage();
                tL_messages_editMessage.peer = MessagesController.getInstance(LocationController.this.currentAccount).getInputPeer((int) sharingLocationInfo.did);
                tL_messages_editMessage.id = sharingLocationInfo.mid;
                tL_messages_editMessage.stop_geo_live = true;
                ConnectionsManager.getInstance(LocationController.this.currentAccount).sendRequest(tL_messages_editMessage, new C18121());
            }
            LocationController.this.sharingLocations.clear();
            LocationController.this.sharingLocationsMap.clear();
            LocationController.this.saveSharingLocation(null, 2);
            LocationController.this.stop(true);
            AndroidUtilities.runOnUIThread(new C02432());
        }
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
                    LocationController.this.lastKnownLocation = location;
                } else if (!LocationController.this.started && location.distanceTo(LocationController.this.lastKnownLocation) > 20.0f) {
                    LocationController.this.lastKnownLocation = location;
                    LocationController.this.lastLocationSendTime = (System.currentTimeMillis() - 90000) + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
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
        AndroidUtilities.runOnUIThread(new C02311());
        loadSharingLocations();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        ArrayList arrayList;
        ArrayList arrayList2;
        int i4;
        int i5;
        MessageObject messageObject;
        if (i == NotificationCenter.didReceivedNewMessages) {
            i = ((Long) objArr[0]).longValue();
            if (isSharingLocation(i)) {
                arrayList = (ArrayList) this.locationsCache.get(i);
                if (arrayList != null) {
                    arrayList2 = (ArrayList) objArr[1];
                    i4 = 0;
                    i5 = i4;
                    while (i4 < arrayList2.size()) {
                        messageObject = (MessageObject) arrayList2.get(i4);
                        if (messageObject.isLiveLocation()) {
                            for (i5 = 0; i5 < arrayList.size(); i5++) {
                                if (((Message) arrayList.get(i5)).from_id == messageObject.messageOwner.from_id) {
                                    arrayList.set(i5, messageObject.messageOwner);
                                    i5 = 1;
                                    break;
                                }
                            }
                            i5 = 0;
                            if (i5 == 0) {
                                arrayList.add(messageObject.messageOwner);
                            }
                            i5 = 1;
                        }
                        i4++;
                    }
                    if (i5 != 0) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(i), Integer.valueOf(this.currentAccount));
                    }
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (this.sharingLocationsUI.isEmpty() == 0) {
                ArrayList arrayList3 = (ArrayList) objArr[0];
                i2 = ((Integer) objArr[1]).intValue();
                ArrayList arrayList4 = null;
                for (objArr = null; objArr < this.sharingLocationsUI.size(); objArr++) {
                    SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocationsUI.get(objArr);
                    if (i2 == (sharingLocationInfo.messageObject != null ? sharingLocationInfo.messageObject.getChannelId() : 0)) {
                        if (arrayList3.contains(Integer.valueOf(sharingLocationInfo.mid))) {
                            if (arrayList4 == null) {
                                arrayList4 = new ArrayList();
                            }
                            arrayList4.add(Long.valueOf(sharingLocationInfo.did));
                        }
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
            i = ((Long) objArr[0]).longValue();
            if (isSharingLocation(i)) {
                arrayList = (ArrayList) this.locationsCache.get(i);
                if (arrayList != null) {
                    arrayList2 = (ArrayList) objArr[1];
                    i4 = 0;
                    i5 = i4;
                    while (i4 < arrayList2.size()) {
                        messageObject = (MessageObject) arrayList2.get(i4);
                        int i6 = 0;
                        while (i6 < arrayList.size()) {
                            if (((Message) arrayList.get(i6)).from_id == messageObject.messageOwner.from_id) {
                                if (messageObject.isLiveLocation()) {
                                    arrayList.set(i6, messageObject.messageOwner);
                                } else {
                                    arrayList.remove(i6);
                                }
                                i5 = 1;
                                i4++;
                            } else {
                                i6++;
                            }
                        }
                        i4++;
                    }
                    if (i5 != 0) {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(i), Integer.valueOf(this.currentAccount));
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
                final SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocations.get(i2);
                if (!(sharingLocationInfo.messageObject.messageOwner.media == null || sharingLocationInfo.messageObject.messageOwner.media.geo == null)) {
                    int i3 = sharingLocationInfo.messageObject.messageOwner.edit_date != 0 ? sharingLocationInfo.messageObject.messageOwner.edit_date : sharingLocationInfo.messageObject.messageOwner.date;
                    GeoPoint geoPoint = sharingLocationInfo.messageObject.messageOwner.media.geo;
                    if (Math.abs(i - i3) < 30 && Math.abs(geoPoint.lat - this.lastKnownLocation.getLatitude()) <= eps && Math.abs(geoPoint._long - this.lastKnownLocation.getLongitude()) <= eps) {
                    }
                }
                TLObject tL_messages_editMessage = new TL_messages_editMessage();
                tL_messages_editMessage.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) sharingLocationInfo.did);
                tL_messages_editMessage.id = sharingLocationInfo.mid;
                tL_messages_editMessage.stop_geo_live = false;
                tL_messages_editMessage.flags |= MessagesController.UPDATE_MASK_CHANNEL;
                tL_messages_editMessage.geo_point = new TL_inputGeoPoint();
                tL_messages_editMessage.geo_point.lat = this.lastKnownLocation.getLatitude();
                tL_messages_editMessage.geo_point._long = this.lastKnownLocation.getLongitude();
                final int[] iArr = new int[]{ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editMessage, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.LocationController$2$1 */
                    class C02321 implements Runnable {
                        C02321() {
                        }

                        public void run() {
                            LocationController.this.sharingLocationsUI.remove(sharingLocationInfo);
                            LocationController.this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
                            if (LocationController.this.sharingLocationsUI.isEmpty()) {
                                LocationController.this.stopService();
                            }
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
                        }
                    }

                    public void run(TLObject tLObject, TL_error tL_error) {
                        if (tL_error != null) {
                            if (tL_error.text.equals("MESSAGE_ID_INVALID") != null) {
                                LocationController.this.sharingLocations.remove(sharingLocationInfo);
                                LocationController.this.sharingLocationsMap.remove(sharingLocationInfo.did);
                                LocationController.this.saveSharingLocation(sharingLocationInfo, 1);
                                LocationController.this.requests.delete(iArr[0]);
                                AndroidUtilities.runOnUIThread(new C02321());
                            }
                            return;
                        }
                        Updates updates = (Updates) tLObject;
                        tL_error = null;
                        TL_error tL_error2 = tL_error;
                        while (tL_error < updates.updates.size()) {
                            Update update = (Update) updates.updates.get(tL_error);
                            if (update instanceof TL_updateEditMessage) {
                                sharingLocationInfo.messageObject.messageOwner = ((TL_updateEditMessage) update).message;
                            } else if (update instanceof TL_updateEditChannelMessage) {
                                sharingLocationInfo.messageObject.messageOwner = ((TL_updateEditChannelMessage) update).message;
                            } else {
                                tL_error++;
                            }
                            tL_error2 = 1;
                            tL_error++;
                        }
                        if (tL_error2 != null) {
                            LocationController.this.saveSharingLocation(sharingLocationInfo, 0);
                        }
                        MessagesController.getInstance(LocationController.this.currentAccount).processUpdates(updates, false);
                    }
                })};
                this.requests.put(iArr[0], 0);
            }
            ConnectionsManager.getInstance(this.currentAccount).resumeNetworkMaybe();
            stop(false);
        }
    }

    protected void update() {
        if (!this.sharingLocations.isEmpty()) {
            int i = 0;
            while (i < this.sharingLocations.size()) {
                final SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) this.sharingLocations.get(i);
                if (sharingLocationInfo.stopTime <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                    this.sharingLocations.remove(i);
                    this.sharingLocationsMap.remove(sharingLocationInfo.did);
                    saveSharingLocation(sharingLocationInfo, 1);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LocationController.this.sharingLocationsUI.remove(sharingLocationInfo);
                            LocationController.this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
                            if (LocationController.this.sharingLocationsUI.isEmpty()) {
                                LocationController.this.stopService();
                            }
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
                        }
                    });
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

    public void cleanup() {
        this.sharingLocationsUI.clear();
        this.sharingLocationsMapUI.clear();
        this.locationsCache.clear();
        this.cacheRequests.clear();
        stopService();
        Utilities.stageQueue.postRunnable(new C02344());
    }

    protected void addSharingLocation(long j, int i, int i2, Message message) {
        final SharingLocationInfo sharingLocationInfo = new SharingLocationInfo();
        sharingLocationInfo.did = j;
        sharingLocationInfo.mid = i;
        sharingLocationInfo.period = i2;
        sharingLocationInfo.messageObject = new MessageObject(this.currentAccount, message, false);
        sharingLocationInfo.stopTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + i2;
        final SharingLocationInfo sharingLocationInfo2 = (SharingLocationInfo) this.sharingLocationsMap.get(j);
        this.sharingLocationsMap.put(j, sharingLocationInfo);
        if (sharingLocationInfo2 != null) {
            this.sharingLocations.remove(sharingLocationInfo2);
        }
        this.sharingLocations.add(sharingLocationInfo);
        saveSharingLocation(sharingLocationInfo, 0);
        this.lastLocationSendTime = (System.currentTimeMillis() - BACKGROUD_UPDATE_TIME) + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (sharingLocationInfo2 != null) {
                    LocationController.this.sharingLocationsUI.remove(sharingLocationInfo2);
                }
                LocationController.this.sharingLocationsUI.add(sharingLocationInfo);
                LocationController.this.sharingLocationsMapUI.put(sharingLocationInfo.did, sharingLocationInfo);
                LocationController.this.startService();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
            }
        });
    }

    public boolean isSharingLocation(long j) {
        return this.sharingLocationsMapUI.indexOfKey(j) >= null ? 1 : 0;
    }

    public SharingLocationInfo getSharingLocationInfo(long j) {
        return (SharingLocationInfo) this.sharingLocationsMapUI.get(j);
    }

    private void loadSharingLocations() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new C02396());
    }

    private void saveSharingLocation(final SharingLocationInfo sharingLocationInfo, final int i) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                try {
                    if (i == 2) {
                        MessagesStorage.getInstance(LocationController.this.currentAccount).getDatabase().executeFast("DELETE FROM sharing_locations WHERE 1").stepThis().dispose();
                    } else if (i == 1) {
                        if (sharingLocationInfo != null) {
                            SQLiteDatabase database = MessagesStorage.getInstance(LocationController.this.currentAccount).getDatabase();
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("DELETE FROM sharing_locations WHERE uid = ");
                            stringBuilder.append(sharingLocationInfo.did);
                            database.executeFast(stringBuilder.toString()).stepThis().dispose();
                        }
                    } else if (sharingLocationInfo != null) {
                        SQLitePreparedStatement executeFast = MessagesStorage.getInstance(LocationController.this.currentAccount).getDatabase().executeFast("REPLACE INTO sharing_locations VALUES(?, ?, ?, ?, ?)");
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
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
    }

    public void removeSharingLocation(final long j) {
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.LocationController$8$1 */
            class C18111 implements RequestDelegate {
                C18111() {
                }

                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        MessagesController.getInstance(LocationController.this.currentAccount).processUpdates((Updates) tLObject, false);
                    }
                }
            }

            public void run() {
                final SharingLocationInfo sharingLocationInfo = (SharingLocationInfo) LocationController.this.sharingLocationsMap.get(j);
                LocationController.this.sharingLocationsMap.remove(j);
                if (sharingLocationInfo != null) {
                    TLObject tL_messages_editMessage = new TL_messages_editMessage();
                    tL_messages_editMessage.peer = MessagesController.getInstance(LocationController.this.currentAccount).getInputPeer((int) sharingLocationInfo.did);
                    tL_messages_editMessage.id = sharingLocationInfo.mid;
                    tL_messages_editMessage.stop_geo_live = true;
                    ConnectionsManager.getInstance(LocationController.this.currentAccount).sendRequest(tL_messages_editMessage, new C18111());
                    LocationController.this.sharingLocations.remove(sharingLocationInfo);
                    LocationController.this.saveSharingLocation(sharingLocationInfo, 1);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            LocationController.this.sharingLocationsUI.remove(sharingLocationInfo);
                            LocationController.this.sharingLocationsMapUI.remove(sharingLocationInfo.did);
                            if (LocationController.this.sharingLocationsUI.isEmpty()) {
                                LocationController.this.stopService();
                            }
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
                        }
                    });
                    if (LocationController.this.sharingLocations.isEmpty()) {
                        LocationController.this.stop(true);
                    }
                }
            }
        });
    }

    private void startService() {
        try {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
        } catch (Throwable th) {
            FileLog.m3e(th);
        }
    }

    private void stopService() {
        ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
    }

    public void removeAllLocationSharings() {
        Utilities.stageQueue.postRunnable(new C02449());
    }

    public void setGoogleMapLocation(Location location, boolean z) {
        if (location != null) {
            this.lastLocationByGoogleMaps = true;
            if (!z) {
                if (!this.lastKnownLocation || this.lastKnownLocation.distanceTo(location) < true) {
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
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
            try {
                this.locationManager.requestLocationUpdates("passive", 1, 0.0f, this.passiveLocationListener);
            } catch (Throwable e22) {
                FileLog.m3e(e22);
            }
            if (this.lastKnownLocation == null) {
                try {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                    if (this.lastKnownLocation == null) {
                        this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                    }
                } catch (Throwable e222) {
                    FileLog.m3e(e222);
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

    public void loadLiveLocations(final long j) {
        if (this.cacheRequests.indexOfKey(j) < 0) {
            this.cacheRequests.put(j, Boolean.valueOf(true));
            TLObject tL_messages_getRecentLocations = new TL_messages_getRecentLocations();
            tL_messages_getRecentLocations.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) j);
            tL_messages_getRecentLocations.limit = 100;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getRecentLocations, new RequestDelegate() {
                public void run(final TLObject tLObject, TL_error tL_error) {
                    if (tL_error == null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                LocationController.this.cacheRequests.delete(j);
                                messages_Messages messages_messages = (messages_Messages) tLObject;
                                int i = 0;
                                while (i < messages_messages.messages.size()) {
                                    if (!(((Message) messages_messages.messages.get(i)).media instanceof TL_messageMediaGeoLive)) {
                                        messages_messages.messages.remove(i);
                                        i--;
                                    }
                                    i++;
                                }
                                MessagesStorage.getInstance(LocationController.this.currentAccount).putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
                                MessagesController.getInstance(LocationController.this.currentAccount).putUsers(messages_messages.users, false);
                                MessagesController.getInstance(LocationController.this.currentAccount).putChats(messages_messages.chats, false);
                                LocationController.this.locationsCache.put(j, messages_messages.messages);
                                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, Long.valueOf(j), Integer.valueOf(LocationController.this.currentAccount));
                            }
                        });
                    }
                }
            });
        }
    }

    public static int getLocationsCount() {
        int i = 0;
        int i2 = 0;
        while (i < 3) {
            i2 += getInstance(i).sharingLocationsUI.size();
            i++;
        }
        return i2;
    }
}
