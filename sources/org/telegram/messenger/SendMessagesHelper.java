package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaCodecInfo;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.Toast;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.Point;

public class SendMessagesHelper extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    private static volatile SendMessagesHelper[] Instance = new SendMessagesHelper[3];
    private static DispatchQueue mediaSendQueue = new DispatchQueue("mediaSendQueue");
    private static ThreadPoolExecutor mediaSendThreadPool;
    /* access modifiers changed from: private */
    public HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap<>();
    private SparseArray<TLRPC.Message> editingMessages = new SparseArray<>();
    private LocationProvider locationProvider = new LocationProvider(new LocationProvider.LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            SendMessagesHelper.this.sendLocation(location);
            SendMessagesHelper.this.waitingForLocation.clear();
        }

        public void onUnableLocationAcquire() {
            HashMap hashMap = new HashMap(SendMessagesHelper.this.waitingForLocation);
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, hashMap);
            SendMessagesHelper.this.waitingForLocation.clear();
        }
    });
    private SparseArray<TLRPC.Message> sendingMessages = new SparseArray<>();
    private LongSparseArray<Integer> sendingMessagesIdDialogs = new LongSparseArray<>();
    private SparseArray<MessageObject> unsentMessages = new SparseArray<>();
    private SparseArray<TLRPC.Message> uploadMessages = new SparseArray<>();
    private LongSparseArray<Integer> uploadingMessagesIdDialogs = new LongSparseArray<>();
    private LongSparseArray<Long> voteSendTime = new LongSparseArray<>();
    private HashMap<String, Boolean> waitingForCallback = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<String, MessageObject> waitingForLocation = new HashMap<>();
    private HashMap<String, byte[]> waitingForVote = new HashMap<>();

    public static class SendingMediaInfo {
        public boolean canDeleteAfter;
        public String caption;
        public ArrayList<TLRPC.MessageEntity> entities;
        public TLRPC.BotInlineResult inlineResult;
        public boolean isVideo;
        public ArrayList<TLRPC.InputDocument> masks;
        public HashMap<String, String> params;
        public String path;
        public MediaController.SearchImage searchImage;
        public int ttl;
        public Uri uri;
        public VideoEditedInfo videoEditedInfo;
    }

    static {
        int availableProcessors = Build.VERSION.SDK_INT >= 17 ? Runtime.getRuntime().availableProcessors() : 2;
        mediaSendThreadPool = new ThreadPoolExecutor(availableProcessors, availableProcessors, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }

    private static class MediaSendPrepareWorker {
        public volatile String parentObject;
        public volatile TLRPC.TL_photo photo;
        public CountDownLatch sync;

        private MediaSendPrepareWorker() {
        }
    }

    public static class LocationProvider {
        /* access modifiers changed from: private */
        public LocationProviderDelegate delegate;
        private GpsLocationListener gpsLocationListener = new GpsLocationListener();
        /* access modifiers changed from: private */
        public Location lastKnownLocation;
        private LocationManager locationManager;
        /* access modifiers changed from: private */
        public Runnable locationQueryCancelRunnable;
        private GpsLocationListener networkLocationListener = new GpsLocationListener();

        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
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
                if (location != null && LocationProvider.this.locationQueryCancelRunnable != null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("found location " + location);
                    }
                    Location unused = LocationProvider.this.lastKnownLocation = location;
                    if (location.getAccuracy() < 100.0f) {
                        if (LocationProvider.this.delegate != null) {
                            LocationProvider.this.delegate.onLocationAcquired(location);
                        }
                        if (LocationProvider.this.locationQueryCancelRunnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(LocationProvider.this.locationQueryCancelRunnable);
                        }
                        LocationProvider.this.cleanup();
                    }
                }
            }
        }

        public LocationProvider() {
        }

        public LocationProvider(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        public void setDelegate(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        /* access modifiers changed from: private */
        public void cleanup() {
            this.locationManager.removeUpdates(this.gpsLocationListener);
            this.locationManager.removeUpdates(this.networkLocationListener);
            this.lastKnownLocation = null;
            this.locationQueryCancelRunnable = null;
        }

        public void start() {
            if (this.locationManager == null) {
                this.locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            }
            try {
                this.locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            try {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                if (this.lastKnownLocation == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
            }
            Runnable runnable = this.locationQueryCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            this.locationQueryCancelRunnable = new Runnable() {
                public final void run() {
                    SendMessagesHelper.LocationProvider.this.lambda$start$0$SendMessagesHelper$LocationProvider();
                }
            };
            AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, 5000);
        }

        public /* synthetic */ void lambda$start$0$SendMessagesHelper$LocationProvider() {
            if (this.locationQueryCancelRunnable == this) {
                LocationProviderDelegate locationProviderDelegate = this.delegate;
                if (locationProviderDelegate != null) {
                    Location location = this.lastKnownLocation;
                    if (location != null) {
                        locationProviderDelegate.onLocationAcquired(location);
                    } else {
                        locationProviderDelegate.onUnableLocationAcquire();
                    }
                }
                cleanup();
            }
        }

        public void stop() {
            if (this.locationManager != null) {
                Runnable runnable = this.locationQueryCancelRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                cleanup();
            }
        }
    }

    protected class DelayedMessageSendAfterRequest {
        public DelayedMessage delayedMessage;
        public MessageObject msgObj;
        public ArrayList<MessageObject> msgObjs;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public TLObject request;
        public boolean scheduled;

        protected DelayedMessageSendAfterRequest() {
        }
    }

    protected class DelayedMessage {
        public TLRPC.EncryptedChat encryptedChat;
        public HashMap<Object, Object> extraHashMap;
        public int finalGroupMessage;
        public long groupId;
        public String httpLocation;
        public ArrayList<String> httpLocations;
        public ArrayList<TLRPC.InputMedia> inputMedias;
        public TLRPC.InputMedia inputUploadMedia;
        public TLObject locationParent;
        public ArrayList<TLRPC.PhotoSize> locations;
        public ArrayList<MessageObject> messageObjects;
        public ArrayList<TLRPC.Message> messages;
        public MessageObject obj;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public long peer;
        public boolean performMediaUpload;
        public TLRPC.PhotoSize photoSize;
        ArrayList<DelayedMessageSendAfterRequest> requests;
        public boolean scheduled;
        public TLObject sendEncryptedRequest;
        public TLObject sendRequest;
        public int type;
        public VideoEditedInfo videoEditedInfo;
        public ArrayList<VideoEditedInfo> videoEditedInfos;

        public DelayedMessage(long j) {
            this.peer = j;
        }

        public void initForGroup(long j) {
            this.type = 4;
            this.groupId = j;
            this.messageObjects = new ArrayList<>();
            this.messages = new ArrayList<>();
            this.inputMedias = new ArrayList<>();
            this.originalPaths = new ArrayList<>();
            this.parentObjects = new ArrayList<>();
            this.extraHashMap = new HashMap<>();
            this.locations = new ArrayList<>();
            this.httpLocations = new ArrayList<>();
            this.videoEditedInfos = new ArrayList<>();
        }

        public void addDelayedRequest(TLObject tLObject, MessageObject messageObject, String str, Object obj2, DelayedMessage delayedMessage, boolean z) {
            DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = new DelayedMessageSendAfterRequest();
            delayedMessageSendAfterRequest.request = tLObject;
            delayedMessageSendAfterRequest.msgObj = messageObject;
            delayedMessageSendAfterRequest.originalPath = str;
            delayedMessageSendAfterRequest.delayedMessage = delayedMessage;
            delayedMessageSendAfterRequest.parentObject = obj2;
            delayedMessageSendAfterRequest.scheduled = z;
            if (this.requests == null) {
                this.requests = new ArrayList<>();
            }
            this.requests.add(delayedMessageSendAfterRequest);
        }

        public void addDelayedRequest(TLObject tLObject, ArrayList<MessageObject> arrayList, ArrayList<String> arrayList2, ArrayList<Object> arrayList3, DelayedMessage delayedMessage, boolean z) {
            DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = new DelayedMessageSendAfterRequest();
            delayedMessageSendAfterRequest.request = tLObject;
            delayedMessageSendAfterRequest.msgObjs = arrayList;
            delayedMessageSendAfterRequest.originalPaths = arrayList2;
            delayedMessageSendAfterRequest.delayedMessage = delayedMessage;
            delayedMessageSendAfterRequest.parentObjects = arrayList3;
            delayedMessageSendAfterRequest.scheduled = z;
            if (this.requests == null) {
                this.requests = new ArrayList<>();
            }
            this.requests.add(delayedMessageSendAfterRequest);
        }

        public void sendDelayedRequests() {
            if (this.requests != null) {
                int i = this.type;
                if (i == 4 || i == 0) {
                    int size = this.requests.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = this.requests.get(i2);
                        TLObject tLObject = delayedMessageSendAfterRequest.request;
                        if (tLObject instanceof TLRPC.TL_messages_sendEncryptedMultiMedia) {
                            SendMessagesHelper.this.getSecretChatHelper().performSendEncryptedRequest((TLRPC.TL_messages_sendEncryptedMultiMedia) delayedMessageSendAfterRequest.request, this);
                        } else if (tLObject instanceof TLRPC.TL_messages_sendMultiMedia) {
                            SendMessagesHelper.this.performSendMessageRequestMulti((TLRPC.TL_messages_sendMultiMedia) tLObject, delayedMessageSendAfterRequest.msgObjs, delayedMessageSendAfterRequest.originalPaths, delayedMessageSendAfterRequest.parentObjects, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.scheduled);
                        } else {
                            SendMessagesHelper.this.performSendMessageRequest(tLObject, delayedMessageSendAfterRequest.msgObj, delayedMessageSendAfterRequest.originalPath, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.parentObject, delayedMessageSendAfterRequest.scheduled);
                        }
                    }
                    this.requests = null;
                }
            }
        }

        public void markAsError() {
            if (this.type == 4) {
                for (int i = 0; i < this.messageObjects.size(); i++) {
                    MessageObject messageObject = this.messageObjects.get(i);
                    SendMessagesHelper.this.getMessagesStorage().markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                    messageObject.messageOwner.send_state = 2;
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                    SendMessagesHelper.this.processSentMessage(messageObject.getId());
                    SendMessagesHelper.this.removeFromUploadingMessages(messageObject.getId(), this.scheduled);
                }
                HashMap access$800 = SendMessagesHelper.this.delayedMessages;
                access$800.remove("group_" + this.groupId);
            } else {
                MessagesStorage messagesStorage = SendMessagesHelper.this.getMessagesStorage();
                MessageObject messageObject2 = this.obj;
                messagesStorage.markMessageAsSendError(messageObject2.messageOwner, messageObject2.scheduled);
                this.obj.messageOwner.send_state = 2;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(this.obj.getId()));
                SendMessagesHelper.this.processSentMessage(this.obj.getId());
                SendMessagesHelper.this.removeFromUploadingMessages(this.obj.getId(), this.scheduled);
            }
            sendDelayedRequests();
        }
    }

    public static SendMessagesHelper getInstance(int i) {
        SendMessagesHelper sendMessagesHelper = Instance[i];
        if (sendMessagesHelper == null) {
            synchronized (SendMessagesHelper.class) {
                sendMessagesHelper = Instance[i];
                if (sendMessagesHelper == null) {
                    SendMessagesHelper[] sendMessagesHelperArr = Instance;
                    SendMessagesHelper sendMessagesHelper2 = new SendMessagesHelper(i);
                    sendMessagesHelperArr[i] = sendMessagesHelper2;
                    sendMessagesHelper = sendMessagesHelper2;
                }
            }
        }
        return sendMessagesHelper;
    }

    public SendMessagesHelper(int i) {
        super(i);
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                SendMessagesHelper.this.lambda$new$0$SendMessagesHelper();
            }
        });
    }

    public /* synthetic */ void lambda$new$0$SendMessagesHelper() {
        getNotificationCenter().addObserver(this, NotificationCenter.FileDidUpload);
        getNotificationCenter().addObserver(this, NotificationCenter.FileDidFailUpload);
        getNotificationCenter().addObserver(this, NotificationCenter.filePreparingStarted);
        getNotificationCenter().addObserver(this, NotificationCenter.fileNewChunkAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.filePreparingFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.fileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    public void cleanup() {
        this.delayedMessages.clear();
        this.unsentMessages.clear();
        this.sendingMessages.clear();
        this.editingMessages.clear();
        this.sendingMessagesIdDialogs.clear();
        this.uploadMessages.clear();
        this.uploadingMessagesIdDialogs.clear();
        this.waitingForLocation.clear();
        this.waitingForCallback.clear();
        this.waitingForVote.clear();
        this.locationProvider.stop();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        ArrayList arrayList;
        MessageObject messageObject;
        char c;
        MessageObject messageObject2;
        TLRPC.InputMedia inputMedia;
        String str2;
        TLRPC.InputFile inputFile;
        TLRPC.InputEncryptedFile inputEncryptedFile;
        ArrayList arrayList2;
        TLObject tLObject;
        TLRPC.TL_decryptedMessage tL_decryptedMessage;
        ArrayList arrayList3;
        TLRPC.InputEncryptedFile inputEncryptedFile2;
        int i3;
        TLRPC.PhotoSize photoSize;
        TLRPC.PhotoSize photoSize2;
        TLRPC.PhotoSize photoSize3;
        int i4 = i;
        int i5 = 4;
        int i6 = 0;
        boolean z = true;
        if (i4 == NotificationCenter.FileDidUpload) {
            String str3 = objArr[0];
            TLRPC.InputFile inputFile2 = objArr[1];
            TLRPC.InputEncryptedFile inputEncryptedFile3 = objArr[2];
            ArrayList arrayList4 = this.delayedMessages.get(str3);
            if (arrayList4 != null) {
                while (i6 < arrayList4.size()) {
                    DelayedMessage delayedMessage = (DelayedMessage) arrayList4.get(i6);
                    TLObject tLObject2 = delayedMessage.sendRequest;
                    if (tLObject2 instanceof TLRPC.TL_messages_sendMedia) {
                        inputMedia = ((TLRPC.TL_messages_sendMedia) tLObject2).media;
                    } else if (tLObject2 instanceof TLRPC.TL_messages_editMessage) {
                        inputMedia = ((TLRPC.TL_messages_editMessage) tLObject2).media;
                    } else {
                        inputMedia = tLObject2 instanceof TLRPC.TL_messages_sendMultiMedia ? (TLRPC.InputMedia) delayedMessage.extraHashMap.get(str3) : null;
                    }
                    if (inputFile2 == null || inputMedia == null) {
                        arrayList2 = arrayList4;
                        inputFile = inputFile2;
                        str2 = str3;
                        inputEncryptedFile = inputEncryptedFile3;
                        if (!(inputEncryptedFile == null || (tLObject = delayedMessage.sendEncryptedRequest) == null)) {
                            if (delayedMessage.type == i5) {
                                TLRPC.TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TLRPC.TL_messages_sendEncryptedMultiMedia) tLObject;
                                TLRPC.InputEncryptedFile inputEncryptedFile4 = (TLRPC.InputEncryptedFile) delayedMessage.extraHashMap.get(str2);
                                int indexOf = tL_messages_sendEncryptedMultiMedia.files.indexOf(inputEncryptedFile4);
                                if (indexOf >= 0) {
                                    tL_messages_sendEncryptedMultiMedia.files.set(indexOf, inputEncryptedFile);
                                    if (inputEncryptedFile4.id == 1) {
                                        MessageObject messageObject3 = (MessageObject) delayedMessage.extraHashMap.get(str2 + "_i");
                                        delayedMessage.photoSize = (TLRPC.PhotoSize) delayedMessage.extraHashMap.get(str2 + "_t");
                                        stopVideoService(delayedMessage.messageObjects.get(indexOf).messageOwner.attachPath);
                                    }
                                    tL_decryptedMessage = tL_messages_sendEncryptedMultiMedia.messages.get(indexOf);
                                } else {
                                    tL_decryptedMessage = null;
                                }
                            } else {
                                tL_decryptedMessage = (TLRPC.TL_decryptedMessage) tLObject;
                            }
                            if (tL_decryptedMessage != null) {
                                TLRPC.DecryptedMessageMedia decryptedMessageMedia = tL_decryptedMessage.media;
                                if ((decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaVideo) || (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaPhoto) || (decryptedMessageMedia instanceof TLRPC.TL_decryptedMessageMediaDocument)) {
                                    tL_decryptedMessage.media.size = (int) objArr[5].longValue();
                                }
                                TLRPC.DecryptedMessageMedia decryptedMessageMedia2 = tL_decryptedMessage.media;
                                decryptedMessageMedia2.key = objArr[3];
                                decryptedMessageMedia2.iv = objArr[4];
                                if (delayedMessage.type == 4) {
                                    uploadMultiMedia(delayedMessage, (TLRPC.InputMedia) null, inputEncryptedFile, str2);
                                } else {
                                    SecretChatHelper secretChatHelper = getSecretChatHelper();
                                    MessageObject messageObject4 = delayedMessage.obj;
                                    secretChatHelper.performSendEncryptedRequest(tL_decryptedMessage, messageObject4.messageOwner, delayedMessage.encryptedChat, inputEncryptedFile, delayedMessage.originalPath, messageObject4);
                                }
                            }
                            arrayList2.remove(i6);
                            i6--;
                        }
                    } else {
                        int i7 = delayedMessage.type;
                        if (i7 == 0) {
                            inputMedia.file = inputFile2;
                            arrayList3 = arrayList4;
                            inputEncryptedFile2 = inputEncryptedFile3;
                            i3 = i6;
                            inputFile = inputFile2;
                            str2 = str3;
                            performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, delayedMessage, true, (DelayedMessage) null, delayedMessage.parentObject, delayedMessage.scheduled);
                        } else {
                            DelayedMessage delayedMessage2 = delayedMessage;
                            arrayList3 = arrayList4;
                            inputEncryptedFile2 = inputEncryptedFile3;
                            i3 = i6;
                            inputFile = inputFile2;
                            str2 = str3;
                            if (i7 != z) {
                                DelayedMessage delayedMessage3 = delayedMessage2;
                                if (i7 == 2) {
                                    if (inputMedia.file == null) {
                                        inputMedia.file = inputFile;
                                        if (inputMedia.thumb != null || (photoSize2 = delayedMessage3.photoSize) == null || photoSize2.location == null) {
                                            performSendMessageRequest(delayedMessage3.sendRequest, delayedMessage3.obj, delayedMessage3.originalPath, (DelayedMessage) null, delayedMessage3.parentObject, delayedMessage3.scheduled);
                                        } else {
                                            performSendDelayedMessage(delayedMessage3);
                                        }
                                    } else {
                                        inputMedia.thumb = inputFile;
                                        inputMedia.flags |= i5;
                                        performSendMessageRequest(delayedMessage3.sendRequest, delayedMessage3.obj, delayedMessage3.originalPath, (DelayedMessage) null, delayedMessage3.parentObject, delayedMessage3.scheduled);
                                    }
                                } else if (i7 == 3) {
                                    inputMedia.file = inputFile;
                                    performSendMessageRequest(delayedMessage3.sendRequest, delayedMessage3.obj, delayedMessage3.originalPath, (DelayedMessage) null, delayedMessage3.parentObject, delayedMessage3.scheduled);
                                } else if (i7 == i5) {
                                    if (!(inputMedia instanceof TLRPC.TL_inputMediaUploadedDocument)) {
                                        inputMedia.file = inputFile;
                                        uploadMultiMedia(delayedMessage3, inputMedia, (TLRPC.InputEncryptedFile) null, str2);
                                    } else if (inputMedia.file == null) {
                                        inputMedia.file = inputFile;
                                        int indexOf2 = delayedMessage3.messageObjects.indexOf((MessageObject) delayedMessage3.extraHashMap.get(str2 + "_i"));
                                        if (indexOf2 >= 0) {
                                            stopVideoService(delayedMessage3.messageObjects.get(indexOf2).messageOwner.attachPath);
                                        }
                                        delayedMessage3.photoSize = (TLRPC.PhotoSize) delayedMessage3.extraHashMap.get(str2 + "_t");
                                        if (inputMedia.thumb != null || (photoSize = delayedMessage3.photoSize) == null || photoSize.location == null) {
                                            uploadMultiMedia(delayedMessage3, inputMedia, (TLRPC.InputEncryptedFile) null, str2);
                                        } else {
                                            delayedMessage3.performMediaUpload = z;
                                            performSendDelayedMessage(delayedMessage3, indexOf2);
                                        }
                                    } else {
                                        inputMedia.thumb = inputFile;
                                        inputMedia.flags |= i5;
                                        uploadMultiMedia(delayedMessage3, inputMedia, (TLRPC.InputEncryptedFile) null, (String) delayedMessage3.extraHashMap.get(str2 + "_o"));
                                    }
                                }
                            } else if (inputMedia.file == null) {
                                inputMedia.file = inputFile;
                                DelayedMessage delayedMessage4 = delayedMessage2;
                                if (inputMedia.thumb != null || (photoSize3 = delayedMessage4.photoSize) == null || photoSize3.location == null) {
                                    performSendMessageRequest(delayedMessage4.sendRequest, delayedMessage4.obj, delayedMessage4.originalPath, (DelayedMessage) null, delayedMessage4.parentObject, delayedMessage4.scheduled);
                                } else {
                                    performSendDelayedMessage(delayedMessage4);
                                }
                            } else {
                                DelayedMessage delayedMessage5 = delayedMessage2;
                                inputMedia.thumb = inputFile;
                                inputMedia.flags |= i5;
                                performSendMessageRequest(delayedMessage5.sendRequest, delayedMessage5.obj, delayedMessage5.originalPath, (DelayedMessage) null, delayedMessage5.parentObject, delayedMessage5.scheduled);
                            }
                        }
                        arrayList2 = arrayList3;
                        int i8 = i3;
                        arrayList2.remove(i8);
                        i6 = i8 - 1;
                        inputEncryptedFile = inputEncryptedFile2;
                    }
                    i6++;
                    arrayList4 = arrayList2;
                    inputEncryptedFile3 = inputEncryptedFile;
                    inputFile2 = inputFile;
                    str3 = str2;
                    i5 = 4;
                    z = true;
                }
                String str4 = str3;
                if (arrayList4.isEmpty()) {
                    this.delayedMessages.remove(str4);
                }
            }
        } else if (i4 == NotificationCenter.FileDidFailUpload) {
            String str5 = objArr[0];
            boolean booleanValue = objArr[1].booleanValue();
            ArrayList arrayList5 = this.delayedMessages.get(str5);
            if (arrayList5 != null) {
                while (i6 < arrayList5.size()) {
                    DelayedMessage delayedMessage6 = (DelayedMessage) arrayList5.get(i6);
                    if ((booleanValue && delayedMessage6.sendEncryptedRequest != null) || (!booleanValue && delayedMessage6.sendRequest != null)) {
                        delayedMessage6.markAsError();
                        arrayList5.remove(i6);
                        i6--;
                    }
                    i6++;
                }
                if (arrayList5.isEmpty()) {
                    this.delayedMessages.remove(str5);
                }
            }
        } else if (i4 == NotificationCenter.filePreparingStarted) {
            MessageObject messageObject5 = objArr[0];
            if (messageObject5.getId() != 0) {
                String str6 = objArr[1];
                ArrayList arrayList6 = this.delayedMessages.get(messageObject5.messageOwner.attachPath);
                if (arrayList6 != null) {
                    while (true) {
                        if (i6 >= arrayList6.size()) {
                            break;
                        }
                        DelayedMessage delayedMessage7 = (DelayedMessage) arrayList6.get(i6);
                        if (delayedMessage7.type == 4) {
                            int indexOf3 = delayedMessage7.messageObjects.indexOf(messageObject5);
                            delayedMessage7.photoSize = (TLRPC.PhotoSize) delayedMessage7.extraHashMap.get(messageObject5.messageOwner.attachPath + "_t");
                            delayedMessage7.performMediaUpload = true;
                            performSendDelayedMessage(delayedMessage7, indexOf3);
                            arrayList6.remove(i6);
                            break;
                        } else if (delayedMessage7.obj == messageObject5) {
                            delayedMessage7.videoEditedInfo = null;
                            performSendDelayedMessage(delayedMessage7);
                            arrayList6.remove(i6);
                            break;
                        } else {
                            i6++;
                        }
                    }
                    if (arrayList6.isEmpty()) {
                        this.delayedMessages.remove(messageObject5.messageOwner.attachPath);
                    }
                }
            }
        } else if (i4 == NotificationCenter.fileNewChunkAvailable) {
            MessageObject messageObject6 = objArr[0];
            if (messageObject6.getId() != 0) {
                String str7 = objArr[1];
                long longValue = objArr[2].longValue();
                long longValue2 = objArr[3].longValue();
                getFileLoader().checkUploadNewDataAvailable(str7, ((int) messageObject6.getDialogId()) == 0, longValue, longValue2);
                if (longValue2 != 0) {
                    stopVideoService(messageObject6.messageOwner.attachPath);
                    ArrayList arrayList7 = this.delayedMessages.get(messageObject6.messageOwner.attachPath);
                    if (arrayList7 != null) {
                        for (int i9 = 0; i9 < arrayList7.size(); i9++) {
                            DelayedMessage delayedMessage8 = (DelayedMessage) arrayList7.get(i9);
                            if (delayedMessage8.type == 4) {
                                int i10 = 0;
                                while (true) {
                                    if (i10 >= delayedMessage8.messageObjects.size()) {
                                        break;
                                    }
                                    MessageObject messageObject7 = delayedMessage8.messageObjects.get(i10);
                                    if (messageObject7 == messageObject6) {
                                        messageObject7.videoEditedInfo = null;
                                        messageObject7.messageOwner.params.remove("ve");
                                        messageObject7.messageOwner.media.document.size = (int) longValue2;
                                        ArrayList arrayList8 = new ArrayList();
                                        arrayList8.add(messageObject7.messageOwner);
                                        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList8, false, true, false, 0, messageObject7.scheduled);
                                        break;
                                    }
                                    i10++;
                                }
                            } else {
                                MessageObject messageObject8 = delayedMessage8.obj;
                                if (messageObject8 == messageObject6) {
                                    messageObject8.videoEditedInfo = null;
                                    messageObject8.messageOwner.params.remove("ve");
                                    delayedMessage8.obj.messageOwner.media.document.size = (int) longValue2;
                                    ArrayList arrayList9 = new ArrayList();
                                    arrayList9.add(delayedMessage8.obj.messageOwner);
                                    getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList9, false, true, false, 0, delayedMessage8.obj.scheduled);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        } else if (i4 == NotificationCenter.filePreparingFailed) {
            MessageObject messageObject9 = objArr[0];
            if (messageObject9.getId() != 0) {
                String str8 = objArr[1];
                stopVideoService(messageObject9.messageOwner.attachPath);
                ArrayList arrayList10 = this.delayedMessages.get(str8);
                if (arrayList10 != null) {
                    int i11 = 0;
                    while (i11 < arrayList10.size()) {
                        DelayedMessage delayedMessage9 = (DelayedMessage) arrayList10.get(i11);
                        if (delayedMessage9.type == 4) {
                            int i12 = 0;
                            while (true) {
                                if (i12 >= delayedMessage9.messages.size()) {
                                    break;
                                } else if (delayedMessage9.messageObjects.get(i12) == messageObject9) {
                                    delayedMessage9.markAsError();
                                    arrayList10.remove(i11);
                                    break;
                                } else {
                                    i12++;
                                }
                            }
                            i11++;
                        } else if (delayedMessage9.obj == messageObject9) {
                            delayedMessage9.markAsError();
                            arrayList10.remove(i11);
                        } else {
                            i11++;
                        }
                        i11--;
                        i11++;
                    }
                    if (arrayList10.isEmpty()) {
                        this.delayedMessages.remove(str8);
                    }
                }
            }
        } else if (i4 == NotificationCenter.httpFileDidLoad) {
            String str9 = objArr[0];
            ArrayList arrayList11 = this.delayedMessages.get(str9);
            if (arrayList11 != null) {
                for (int i13 = 0; i13 < arrayList11.size(); i13++) {
                    DelayedMessage delayedMessage10 = (DelayedMessage) arrayList11.get(i13);
                    int i14 = delayedMessage10.type;
                    if (i14 == 0) {
                        messageObject = delayedMessage10.obj;
                        c = 0;
                    } else {
                        if (i14 == 2) {
                            messageObject2 = delayedMessage10.obj;
                        } else if (i14 == 4) {
                            messageObject2 = (MessageObject) delayedMessage10.extraHashMap.get(str9);
                            if (messageObject2.getDocument() == null) {
                                messageObject = messageObject2;
                                c = 0;
                            }
                        } else {
                            c = 65535;
                            messageObject = null;
                        }
                        messageObject = messageObject2;
                        c = 1;
                    }
                    if (c == 0) {
                        File file = new File(FileLoader.getDirectory(4), Utilities.MD5(str9) + "." + ImageLoader.getHttpUrlExtension(str9, "file"));
                        DispatchQueue dispatchQueue = Utilities.globalQueue;
                        $$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv97eY r8 = r0;
                        $$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv97eY r0 = new Runnable(file, messageObject, delayedMessage10, str9) {
                            private final /* synthetic */ File f$1;
                            private final /* synthetic */ MessageObject f$2;
                            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$3;
                            private final /* synthetic */ String f$4;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                            }

                            public final void run() {
                                SendMessagesHelper.this.lambda$didReceivedNotification$2$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
                            }
                        };
                        dispatchQueue.postRunnable(r8);
                    } else if (c == 1) {
                        Utilities.globalQueue.postRunnable(new Runnable(delayedMessage10, new File(FileLoader.getDirectory(4), Utilities.MD5(str9) + ".gif"), messageObject) {
                            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$1;
                            private final /* synthetic */ File f$2;
                            private final /* synthetic */ MessageObject f$3;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void run() {
                                SendMessagesHelper.this.lambda$didReceivedNotification$4$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
                            }
                        });
                    }
                }
                this.delayedMessages.remove(str9);
            }
        } else if (i4 == NotificationCenter.fileDidLoad) {
            String str10 = objArr[0];
            ArrayList arrayList12 = this.delayedMessages.get(str10);
            if (arrayList12 != null) {
                while (i6 < arrayList12.size()) {
                    performSendDelayedMessage((DelayedMessage) arrayList12.get(i6));
                    i6++;
                }
                this.delayedMessages.remove(str10);
            }
        } else if ((i4 == NotificationCenter.httpFileDidFailedLoad || i4 == NotificationCenter.fileDidFailToLoad) && (arrayList = this.delayedMessages.get(str)) != null) {
            while (i6 < arrayList.size()) {
                ((DelayedMessage) arrayList.get(i6)).markAsError();
                i6++;
            }
            this.delayedMessages.remove((str = objArr[0]));
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$2$SendMessagesHelper(File file, MessageObject messageObject, DelayedMessage delayedMessage, String str) {
        AndroidUtilities.runOnUIThread(new Runnable(generatePhotoSizes(file.toString(), (Uri) null), messageObject, file, delayedMessage, str) {
            private final /* synthetic */ TLRPC.TL_photo f$1;
            private final /* synthetic */ MessageObject f$2;
            private final /* synthetic */ File f$3;
            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$4;
            private final /* synthetic */ String f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$1$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$SendMessagesHelper(TLRPC.TL_photo tL_photo, MessageObject messageObject, File file, DelayedMessage delayedMessage, String str) {
        if (tL_photo != null) {
            TLRPC.Message message = messageObject.messageOwner;
            message.media.photo = tL_photo;
            message.attachPath = file.toString();
            ArrayList arrayList = new ArrayList();
            arrayList.add(messageObject.messageOwner);
            getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, false, true, false, 0, messageObject.scheduled);
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, messageObject.messageOwner);
            ArrayList<TLRPC.PhotoSize> arrayList2 = tL_photo.sizes;
            delayedMessage.photoSize = arrayList2.get(arrayList2.size() - 1);
            delayedMessage.locationParent = tL_photo;
            delayedMessage.httpLocation = null;
            if (delayedMessage.type == 4) {
                delayedMessage.performMediaUpload = true;
                performSendDelayedMessage(delayedMessage, delayedMessage.messageObjects.indexOf(messageObject));
                return;
            }
            performSendDelayedMessage(delayedMessage);
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("can't load image " + str + " to file " + file.toString());
        }
        delayedMessage.markAsError();
    }

    public /* synthetic */ void lambda$didReceivedNotification$4$SendMessagesHelper(DelayedMessage delayedMessage, File file, MessageObject messageObject) {
        TLRPC.Document document = delayedMessage.obj.getDocument();
        boolean z = false;
        if (document.thumbs.isEmpty() || (document.thumbs.get(0).location instanceof TLRPC.TL_fileLocationUnavailable)) {
            try {
                Bitmap loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), (Uri) null, 90.0f, 90.0f, true);
                if (loadBitmap != null) {
                    document.thumbs.clear();
                    ArrayList<TLRPC.PhotoSize> arrayList = document.thumbs;
                    if (delayedMessage.sendEncryptedRequest != null) {
                        z = true;
                    }
                    arrayList.add(ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, z));
                    loadBitmap.recycle();
                }
            } catch (Exception e) {
                document.thumbs.clear();
                FileLog.e((Throwable) e);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(delayedMessage, file, document, messageObject) {
            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$1;
            private final /* synthetic */ File f$2;
            private final /* synthetic */ TLRPC.Document f$3;
            private final /* synthetic */ MessageObject f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$3$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$SendMessagesHelper(DelayedMessage delayedMessage, File file, TLRPC.Document document, MessageObject messageObject) {
        delayedMessage.httpLocation = null;
        delayedMessage.obj.messageOwner.attachPath = file.toString();
        if (!document.thumbs.isEmpty()) {
            delayedMessage.photoSize = document.thumbs.get(0);
            delayedMessage.locationParent = document;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject.messageOwner);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, false, true, false, 0, messageObject.scheduled);
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
        getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, delayedMessage.obj.messageOwner);
    }

    private void revertEditingMessageObject(MessageObject messageObject) {
        messageObject.cancelEditing = true;
        TLRPC.Message message = messageObject.messageOwner;
        message.media = messageObject.previousMedia;
        message.message = messageObject.previousCaption;
        message.entities = messageObject.previousCaptionEntities;
        message.attachPath = messageObject.previousAttachPath;
        message.send_state = 0;
        messageObject.previousMedia = null;
        messageObject.previousCaption = null;
        messageObject.previousCaptionEntities = null;
        messageObject.previousAttachPath = null;
        messageObject.videoEditedInfo = null;
        messageObject.type = -1;
        messageObject.setType();
        messageObject.caption = null;
        messageObject.generateCaption();
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject.messageOwner);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, false, true, false, 0, messageObject.scheduled);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(messageObject);
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList2);
    }

    public void cancelSendingMessage(MessageObject messageObject) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject);
        cancelSendingMessage((ArrayList<MessageObject>) arrayList);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0193, code lost:
        r7 = r23;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancelSendingMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r27) {
        /*
            r26 = this;
            r0 = r26
            r1 = r27
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r6 = 0
            r8 = r6
            r6 = 0
            r7 = 0
            r10 = 0
            r12 = 0
        L_0x001a:
            int r11 = r27.size()
            r13 = 1
            if (r6 >= r11) goto L_0x01a3
            java.lang.Object r10 = r1.get(r6)
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            boolean r11 = r10.scheduled
            if (r11 == 0) goto L_0x0030
            long r8 = r10.getDialogId()
            r12 = 1
        L_0x0030:
            int r11 = r10.getId()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            r5.add(r11)
            org.telegram.tgnet.TLRPC$Message r11 = r10.messageOwner
            org.telegram.tgnet.TLRPC$Peer r11 = r11.to_id
            int r11 = r11.channel_id
            int r14 = r10.getId()
            boolean r15 = r10.scheduled
            org.telegram.tgnet.TLRPC$Message r14 = r0.removeFromSendingMessages(r14, r15)
            if (r14 == 0) goto L_0x0056
            org.telegram.tgnet.ConnectionsManager r15 = r26.getConnectionsManager()
            int r14 = r14.reqId
            r15.cancelRequest(r14, r13)
        L_0x0056:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r14 = r0.delayedMessages
            java.util.Set r14 = r14.entrySet()
            java.util.Iterator r22 = r14.iterator()
        L_0x0060:
            boolean r14 = r22.hasNext()
            if (r14 == 0) goto L_0x019a
            java.lang.Object r14 = r22.next()
            java.util.Map$Entry r14 = (java.util.Map.Entry) r14
            java.lang.Object r15 = r14.getValue()
            java.util.ArrayList r15 = (java.util.ArrayList) r15
            r4 = 0
        L_0x0073:
            int r13 = r15.size()
            if (r4 >= r13) goto L_0x018f
            java.lang.Object r13 = r15.get(r4)
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r13 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r13
            r23 = r7
            int r7 = r13.type
            r24 = r8
            r8 = 4
            if (r7 != r8) goto L_0x0150
            r4 = -1
            r7 = 0
            r8 = r7
            r7 = 0
        L_0x008c:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r9 = r13.messageObjects
            int r9 = r9.size()
            if (r7 >= r9) goto L_0x00b4
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r13.messageObjects
            java.lang.Object r8 = r8.get(r7)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            int r9 = r8.getId()
            int r14 = r10.getId()
            if (r9 != r14) goto L_0x00b1
            int r4 = r10.getId()
            boolean r9 = r10.scheduled
            r0.removeFromUploadingMessages(r4, r9)
            r4 = r7
            goto L_0x00b4
        L_0x00b1:
            int r7 = r7 + 1
            goto L_0x008c
        L_0x00b4:
            if (r4 < 0) goto L_0x0193
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r13.messageObjects
            r7.remove(r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r7 = r13.messages
            r7.remove(r4)
            java.util.ArrayList<java.lang.String> r7 = r13.originalPaths
            r7.remove(r4)
            org.telegram.tgnet.TLObject r7 = r13.sendRequest
            if (r7 == 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r7 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r7 = r7.multi_media
            r7.remove(r4)
            goto L_0x00df
        L_0x00d1:
            org.telegram.tgnet.TLObject r7 = r13.sendEncryptedRequest
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r7 = (org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r9 = r7.messages
            r9.remove(r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r7 = r7.files
            r7.remove(r4)
        L_0x00df:
            org.telegram.messenger.MediaController r4 = org.telegram.messenger.MediaController.getInstance()
            r4.cancelVideoConvert(r10)
            java.util.HashMap<java.lang.Object, java.lang.Object> r4 = r13.extraHashMap
            java.lang.Object r4 = r4.get(r8)
            java.lang.String r4 = (java.lang.String) r4
            if (r4 == 0) goto L_0x00f3
            r2.add(r4)
        L_0x00f3:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r13.messageObjects
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x0100
            r13.sendDelayedRequests()
            goto L_0x0193
        L_0x0100:
            int r4 = r13.finalGroupMessage
            int r7 = r10.getId()
            if (r4 != r7) goto L_0x0146
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r13.messageObjects
            int r7 = r4.size()
            r8 = 1
            int r7 = r7 - r8
            java.lang.Object r4 = r4.get(r7)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            int r7 = r4.getId()
            r13.finalGroupMessage = r7
            org.telegram.tgnet.TLRPC$Message r7 = r4.messageOwner
            java.util.HashMap<java.lang.String, java.lang.String> r7 = r7.params
            java.lang.String r8 = "final"
            java.lang.String r9 = "1"
            r7.put(r8, r9)
            org.telegram.tgnet.TLRPC$TL_messages_messages r15 = new org.telegram.tgnet.TLRPC$TL_messages_messages
            r15.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r7 = r15.messages
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            r7.add(r4)
            org.telegram.messenger.MessagesStorage r14 = r26.getMessagesStorage()
            long r7 = r13.peer
            r18 = -2
            r19 = 0
            r20 = 0
            r16 = r7
            r21 = r12
            r14.putMessages((org.telegram.tgnet.TLRPC.messages_Messages) r15, (long) r16, (int) r18, (int) r19, (boolean) r20, (boolean) r21)
        L_0x0146:
            boolean r4 = r3.contains(r13)
            if (r4 != 0) goto L_0x0193
            r3.add(r13)
            goto L_0x0193
        L_0x0150:
            org.telegram.messenger.MessageObject r7 = r13.obj
            int r7 = r7.getId()
            int r8 = r10.getId()
            if (r7 != r8) goto L_0x0187
            int r7 = r10.getId()
            boolean r8 = r10.scheduled
            r0.removeFromUploadingMessages(r7, r8)
            r15.remove(r4)
            r13.sendDelayedRequests()
            org.telegram.messenger.MediaController r4 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r7 = r13.obj
            r4.cancelVideoConvert(r7)
            int r4 = r15.size()
            if (r4 != 0) goto L_0x0193
            java.lang.Object r4 = r14.getKey()
            r2.add(r4)
            org.telegram.tgnet.TLObject r4 = r13.sendEncryptedRequest
            if (r4 == 0) goto L_0x0193
            r7 = 1
            goto L_0x0195
        L_0x0187:
            int r4 = r4 + 1
            r7 = r23
            r8 = r24
            goto L_0x0073
        L_0x018f:
            r23 = r7
            r24 = r8
        L_0x0193:
            r7 = r23
        L_0x0195:
            r8 = r24
            r13 = 1
            goto L_0x0060
        L_0x019a:
            r23 = r7
            r24 = r8
            int r6 = r6 + 1
            r10 = r11
            goto L_0x001a
        L_0x01a3:
            r4 = 0
        L_0x01a4:
            int r6 = r2.size()
            if (r4 >= r6) goto L_0x01d2
            java.lang.Object r6 = r2.get(r4)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.String r11 = "http"
            boolean r11 = r6.startsWith(r11)
            if (r11 == 0) goto L_0x01c0
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            r11.cancelLoadHttpFile(r6)
            goto L_0x01c7
        L_0x01c0:
            org.telegram.messenger.FileLoader r11 = r26.getFileLoader()
            r11.cancelUploadFile(r6, r7)
        L_0x01c7:
            r0.stopVideoService(r6)
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r11 = r0.delayedMessages
            r11.remove(r6)
            int r4 = r4 + 1
            goto L_0x01a4
        L_0x01d2:
            int r2 = r3.size()
            r4 = 0
        L_0x01d7:
            if (r4 >= r2) goto L_0x01e7
            java.lang.Object r6 = r3.get(r4)
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r6
            r7 = 1
            r11 = 0
            r0.sendReadyToSendGroup(r6, r11, r7)
            int r4 = r4 + 1
            goto L_0x01d7
        L_0x01e7:
            r7 = 1
            r11 = 0
            int r2 = r27.size()
            if (r2 != r7) goto L_0x020f
            java.lang.Object r2 = r1.get(r11)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            boolean r2 = r2.isEditing()
            if (r2 == 0) goto L_0x020f
            java.lang.Object r2 = r1.get(r11)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.previousMedia
            if (r2 == 0) goto L_0x020f
            java.lang.Object r1 = r1.get(r11)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r0.revertEditingMessageObject(r1)
            goto L_0x0219
        L_0x020f:
            org.telegram.messenger.MessagesController r4 = r26.getMessagesController()
            r6 = 0
            r7 = 0
            r11 = 0
            r4.deleteMessages(r5, r6, r7, r8, r10, r11, r12)
        L_0x0219:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.cancelSendingMessage(java.util.ArrayList):void");
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean z) {
        if (messageObject.getId() >= 0) {
            if (messageObject.isEditing()) {
                editMessageMedia(messageObject, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.TL_document) null, (String) null, (HashMap<String, String>) null, true, messageObject);
            }
            return false;
        }
        TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
        if (messageAction instanceof TLRPC.TL_messageEncryptedAction) {
            TLRPC.EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                getMessagesStorage().markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                messageObject.messageOwner.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            TLRPC.Message message = messageObject.messageOwner;
            if (message.random_id == 0) {
                message.random_id = getNextRandomId();
            }
            TLRPC.DecryptedMessageAction decryptedMessageAction = messageObject.messageOwner.action.encryptedAction;
            if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL) {
                getSecretChatHelper().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionDeleteMessages) {
                getSecretChatHelper().sendMessagesDeleteMessage(encryptedChat, (ArrayList<Long>) null, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionFlushHistory) {
                getSecretChatHelper().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionNotifyLayer) {
                getSecretChatHelper().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionReadMessages) {
                getSecretChatHelper().sendMessagesReadMessage(encryptedChat, (ArrayList<Long>) null, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages) {
                getSecretChatHelper().sendScreenshotMessage(encryptedChat, (ArrayList<Long>) null, messageObject.messageOwner);
            } else if (!(decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionTyping)) {
                if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionResend) {
                    getSecretChatHelper().sendResendMessage(encryptedChat, 0, 0, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionCommitKey) {
                    getSecretChatHelper().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionAbortKey) {
                    getSecretChatHelper().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionRequestKey) {
                    getSecretChatHelper().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionAcceptKey) {
                    getSecretChatHelper().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TLRPC.TL_decryptedMessageActionNoop) {
                    getSecretChatHelper().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        }
        if (messageAction instanceof TLRPC.TL_messageActionScreenshotTaken) {
            TLRPC.User user = getMessagesController().getUser(Integer.valueOf((int) messageObject.getDialogId()));
            TLRPC.Message message2 = messageObject.messageOwner;
            sendScreenshotMessage(user, message2.reply_to_msg_id, message2);
        }
        if (z) {
            this.unsentMessages.put(messageObject.getId(), messageObject);
        }
        sendMessage(messageObject);
        return true;
    }

    /* access modifiers changed from: protected */
    public void processSentMessage(int i) {
        int size = this.unsentMessages.size();
        this.unsentMessages.remove(i);
        if (size != 0 && this.unsentMessages.size() == 0) {
            checkUnsentMessages();
        }
    }

    public void processForwardFromMyName(MessageObject messageObject, long j) {
        ArrayList arrayList;
        HashMap hashMap;
        MessageObject messageObject2 = messageObject;
        long j2 = j;
        if (messageObject2 != null) {
            TLRPC.Message message = messageObject2.messageOwner;
            TLRPC.MessageMedia messageMedia = message.media;
            if (messageMedia == null || (messageMedia instanceof TLRPC.TL_messageMediaEmpty) || (messageMedia instanceof TLRPC.TL_messageMediaWebPage) || (messageMedia instanceof TLRPC.TL_messageMediaGame) || (messageMedia instanceof TLRPC.TL_messageMediaInvoice)) {
                TLRPC.Message message2 = messageObject2.messageOwner;
                if (message2.message != null) {
                    TLRPC.MessageMedia messageMedia2 = message2.media;
                    TLRPC.WebPage webPage = messageMedia2 instanceof TLRPC.TL_messageMediaWebPage ? messageMedia2.webpage : null;
                    ArrayList<TLRPC.MessageEntity> arrayList2 = messageObject2.messageOwner.entities;
                    if (arrayList2 == null || arrayList2.isEmpty()) {
                        arrayList = null;
                    } else {
                        ArrayList arrayList3 = new ArrayList();
                        for (int i = 0; i < messageObject2.messageOwner.entities.size(); i++) {
                            TLRPC.MessageEntity messageEntity = messageObject2.messageOwner.entities.get(i);
                            if ((messageEntity instanceof TLRPC.TL_messageEntityBold) || (messageEntity instanceof TLRPC.TL_messageEntityItalic) || (messageEntity instanceof TLRPC.TL_messageEntityPre) || (messageEntity instanceof TLRPC.TL_messageEntityCode) || (messageEntity instanceof TLRPC.TL_messageEntityTextUrl)) {
                                arrayList3.add(messageEntity);
                            }
                        }
                        arrayList = arrayList3;
                    }
                    sendMessage(messageObject2.messageOwner.message, j, messageObject2.replyMessageObject, webPage, true, arrayList, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (((int) j2) != 0) {
                    ArrayList arrayList4 = new ArrayList();
                    arrayList4.add(messageObject2);
                    sendMessage(arrayList4, j, true, 0);
                }
            } else {
                int i2 = (int) j2;
                if (i2 != 0 || message.to_id == null || (!(messageMedia.photo instanceof TLRPC.TL_photo) && !(messageMedia.document instanceof TLRPC.TL_document))) {
                    hashMap = null;
                } else {
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("parentObject", "sent_" + messageObject2.messageOwner.to_id.channel_id + "_" + messageObject.getId());
                    hashMap = hashMap2;
                }
                TLRPC.Message message3 = messageObject2.messageOwner;
                TLRPC.MessageMedia messageMedia3 = message3.media;
                TLRPC.Photo photo = messageMedia3.photo;
                if (photo instanceof TLRPC.TL_photo) {
                    sendMessage((TLRPC.TL_photo) photo, (String) null, j, messageObject2.replyMessageObject, message3.message, message3.entities, (TLRPC.ReplyMarkup) null, hashMap, true, 0, messageMedia3.ttl_seconds, messageObject);
                    return;
                }
                TLRPC.Document document = messageMedia3.document;
                if (document instanceof TLRPC.TL_document) {
                    sendMessage((TLRPC.TL_document) document, (VideoEditedInfo) null, message3.attachPath, j, messageObject2.replyMessageObject, message3.message, message3.entities, (TLRPC.ReplyMarkup) null, hashMap, true, 0, messageMedia3.ttl_seconds, messageObject);
                } else if ((messageMedia3 instanceof TLRPC.TL_messageMediaVenue) || (messageMedia3 instanceof TLRPC.TL_messageMediaGeo)) {
                    sendMessage(messageObject2.messageOwner.media, j, messageObject2.replyMessageObject, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (messageMedia3.phone_number != null) {
                    TLRPC.TL_userContact_old2 tL_userContact_old2 = new TLRPC.TL_userContact_old2();
                    TLRPC.MessageMedia messageMedia4 = messageObject2.messageOwner.media;
                    tL_userContact_old2.phone = messageMedia4.phone_number;
                    tL_userContact_old2.first_name = messageMedia4.first_name;
                    tL_userContact_old2.last_name = messageMedia4.last_name;
                    tL_userContact_old2.id = messageMedia4.user_id;
                    sendMessage((TLRPC.User) tL_userContact_old2, j, messageObject2.replyMessageObject, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (i2 != 0) {
                    ArrayList arrayList5 = new ArrayList();
                    arrayList5.add(messageObject2);
                    sendMessage(arrayList5, j, true, 0);
                }
            }
        }
    }

    public void sendScreenshotMessage(TLRPC.User user, int i, TLRPC.Message message) {
        if (user != null && i != 0 && user.id != getUserConfig().getClientUserId()) {
            TLRPC.TL_messages_sendScreenshotNotification tL_messages_sendScreenshotNotification = new TLRPC.TL_messages_sendScreenshotNotification();
            tL_messages_sendScreenshotNotification.peer = new TLRPC.TL_inputPeerUser();
            TLRPC.InputPeer inputPeer = tL_messages_sendScreenshotNotification.peer;
            inputPeer.access_hash = user.access_hash;
            inputPeer.user_id = user.id;
            if (message != null) {
                tL_messages_sendScreenshotNotification.reply_to_msg_id = i;
                tL_messages_sendScreenshotNotification.random_id = message.random_id;
            } else {
                message = new TLRPC.TL_messageService();
                message.random_id = getNextRandomId();
                message.dialog_id = (long) user.id;
                message.unread = true;
                message.out = true;
                int newMessageId = getUserConfig().getNewMessageId();
                message.id = newMessageId;
                message.local_id = newMessageId;
                message.from_id = getUserConfig().getClientUserId();
                message.flags |= 256;
                message.flags |= 8;
                message.reply_to_msg_id = i;
                message.to_id = new TLRPC.TL_peerUser();
                message.to_id.user_id = user.id;
                message.date = getConnectionsManager().getCurrentTime();
                message.action = new TLRPC.TL_messageActionScreenshotTaken();
                getUserConfig().saveConfig(false);
            }
            tL_messages_sendScreenshotNotification.random_id = message.random_id;
            MessageObject messageObject = new MessageObject(this.currentAccount, message, false);
            messageObject.messageOwner.send_state = 1;
            ArrayList arrayList = new ArrayList();
            arrayList.add(messageObject);
            getMessagesController().updateInterfaceWithMessages(message.dialog_id, arrayList, false);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(message);
            getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList2, false, true, false, 0, false);
            performSendMessageRequest(tL_messages_sendScreenshotNotification, messageObject, (String) null, (DelayedMessage) null, (Object) null, false);
        }
    }

    public void sendSticker(TLRPC.Document document, long j, MessageObject messageObject, Object obj, boolean z, int i) {
        TLRPC.TL_document_layer82 tL_document_layer82;
        TLRPC.Document document2 = document;
        long j2 = j;
        if (document2 != null) {
            if (((int) j2) == 0) {
                if (getMessagesController().getEncryptedChat(Integer.valueOf((int) (j2 >> 32))) != null) {
                    TLRPC.TL_document_layer82 tL_document_layer822 = new TLRPC.TL_document_layer82();
                    tL_document_layer822.id = document2.id;
                    tL_document_layer822.access_hash = document2.access_hash;
                    tL_document_layer822.date = document2.date;
                    tL_document_layer822.mime_type = document2.mime_type;
                    tL_document_layer822.file_reference = document2.file_reference;
                    if (tL_document_layer822.file_reference == null) {
                        tL_document_layer822.file_reference = new byte[0];
                    }
                    tL_document_layer822.size = document2.size;
                    tL_document_layer822.dc_id = document2.dc_id;
                    tL_document_layer822.attributes = new ArrayList<>(document2.attributes);
                    if (tL_document_layer822.mime_type == null) {
                        tL_document_layer822.mime_type = "";
                    }
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
                    if (closestPhotoSizeWithSize instanceof TLRPC.TL_photoSize) {
                        File pathToAttach = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                        if (pathToAttach.exists()) {
                            try {
                                pathToAttach.length();
                                byte[] bArr = new byte[((int) pathToAttach.length())];
                                new RandomAccessFile(pathToAttach, "r").readFully(bArr);
                                TLRPC.TL_photoCachedSize tL_photoCachedSize = new TLRPC.TL_photoCachedSize();
                                TLRPC.TL_fileLocation_layer82 tL_fileLocation_layer82 = new TLRPC.TL_fileLocation_layer82();
                                tL_fileLocation_layer82.dc_id = closestPhotoSizeWithSize.location.dc_id;
                                tL_fileLocation_layer82.volume_id = closestPhotoSizeWithSize.location.volume_id;
                                tL_fileLocation_layer82.local_id = closestPhotoSizeWithSize.location.local_id;
                                tL_fileLocation_layer82.secret = closestPhotoSizeWithSize.location.secret;
                                tL_photoCachedSize.location = tL_fileLocation_layer82;
                                tL_photoCachedSize.size = closestPhotoSizeWithSize.size;
                                tL_photoCachedSize.w = closestPhotoSizeWithSize.w;
                                tL_photoCachedSize.h = closestPhotoSizeWithSize.h;
                                tL_photoCachedSize.type = closestPhotoSizeWithSize.type;
                                tL_photoCachedSize.bytes = bArr;
                                tL_document_layer822.thumbs.add(tL_photoCachedSize);
                                tL_document_layer822.flags |= 1;
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        }
                    }
                    if (tL_document_layer822.thumbs.isEmpty()) {
                        TLRPC.TL_photoSizeEmpty tL_photoSizeEmpty = new TLRPC.TL_photoSizeEmpty();
                        tL_photoSizeEmpty.type = "s";
                        tL_document_layer822.thumbs.add(tL_photoSizeEmpty);
                    }
                    tL_document_layer82 = tL_document_layer822;
                } else {
                    return;
                }
            } else {
                tL_document_layer82 = document2;
            }
            if (MessageObject.isGifDocument(tL_document_layer82)) {
                mediaSendQueue.postRunnable(new Runnable(tL_document_layer82, j, messageObject, z, i, obj) {
                    private final /* synthetic */ TLRPC.Document f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ MessageObject f$3;
                    private final /* synthetic */ boolean f$4;
                    private final /* synthetic */ int f$5;
                    private final /* synthetic */ Object f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                        this.f$4 = r6;
                        this.f$5 = r7;
                        this.f$6 = r8;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$sendSticker$6$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                    }
                });
                return;
            }
            sendMessage((TLRPC.TL_document) tL_document_layer82, (VideoEditedInfo) null, (String) null, j, messageObject, (String) null, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj);
        }
    }

    public /* synthetic */ void lambda$sendSticker$6$SendMessagesHelper(TLRPC.Document document, long j, MessageObject messageObject, boolean z, int i, Object obj) {
        String str;
        TLRPC.Document document2 = document;
        Bitmap[] bitmapArr = new Bitmap[1];
        String[] strArr = new String[1];
        String key = ImageLocation.getForDocument(document).getKey((Object) null, (Object) null, false);
        if ("video/mp4".equals(document2.mime_type)) {
            str = ".mp4";
        } else {
            str = "video/x-matroska".equals(document2.mime_type) ? ".mkv" : "";
        }
        File directory = FileLoader.getDirectory(3);
        File file = new File(directory, key + str);
        if (!file.exists()) {
            File directory2 = FileLoader.getDirectory(2);
            file = new File(directory2, key + str);
        }
        ensureMediaThumbExists(false, document, file.getAbsolutePath(), (Uri) null, 0);
        strArr[0] = getKeyForPhotoSize(FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 320), bitmapArr, true, true);
        AndroidUtilities.runOnUIThread(new Runnable(bitmapArr, strArr, document, j, messageObject, z, i, obj) {
            private final /* synthetic */ Bitmap[] f$1;
            private final /* synthetic */ String[] f$2;
            private final /* synthetic */ TLRPC.Document f$3;
            private final /* synthetic */ long f$4;
            private final /* synthetic */ MessageObject f$5;
            private final /* synthetic */ boolean f$6;
            private final /* synthetic */ int f$7;
            private final /* synthetic */ Object f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r7;
                this.f$6 = r8;
                this.f$7 = r9;
                this.f$8 = r10;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$5$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$SendMessagesHelper(Bitmap[] bitmapArr, String[] strArr, TLRPC.Document document, long j, MessageObject messageObject, boolean z, int i, Object obj) {
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        sendMessage((TLRPC.TL_document) document, (VideoEditedInfo) null, (String) null, j, messageObject, (String) null, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj);
    }

    /* JADX WARNING: Removed duplicated region for block: B:105:0x02bb  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x02d1  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02d9  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02eb  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x031c  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0427  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0431  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0452 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x049d  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x049f  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x04af A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x04f0  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x04f7  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0517  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x052a  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0534  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x056b  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x05a1  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x05b6  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0667  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0689  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0693  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0696  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x06e2  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0191  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x01a3  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01f9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r49, long r50, boolean r52, int r53) {
        /*
            r48 = this;
            r14 = r48
            r15 = r49
            r12 = r50
            r11 = r53
            r10 = 0
            if (r15 == 0) goto L_0x0797
            boolean r0 = r49.isEmpty()
            if (r0 == 0) goto L_0x0013
            goto L_0x0797
        L_0x0013:
            int r0 = (int) r12
            if (r0 == 0) goto L_0x0779
            org.telegram.messenger.MessagesController r1 = r48.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r9 = r1.getPeer(r0)
            if (r0 <= 0) goto L_0x003d
            org.telegram.messenger.MessagesController r1 = r48.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r1 != 0) goto L_0x002f
            return r10
        L_0x002f:
            r6 = 0
            r16 = 1
            r17 = 1
            r18 = 1
            r19 = 0
            r20 = 1
            r21 = 0
            goto L_0x0074
        L_0x003d:
            org.telegram.messenger.MessagesController r1 = r48.getMessagesController()
            int r2 = -r0
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0055
            boolean r2 = r1.megagroup
            boolean r3 = r1.signatures
            goto L_0x0057
        L_0x0055:
            r2 = 0
            r3 = 0
        L_0x0057:
            boolean r4 = org.telegram.messenger.ChatObject.canSendStickers(r1)
            boolean r5 = org.telegram.messenger.ChatObject.canSendMedia(r1)
            boolean r6 = org.telegram.messenger.ChatObject.canSendEmbed(r1)
            boolean r16 = org.telegram.messenger.ChatObject.canSendPolls(r1)
            r19 = r2
            r21 = r3
            r17 = r5
            r20 = r6
            r18 = r16
            r6 = r1
            r16 = r4
        L_0x0074:
            android.util.LongSparseArray r5 = new android.util.LongSparseArray
            r5.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            android.util.LongSparseArray r22 = new android.util.LongSparseArray
            r22.<init>()
            org.telegram.messenger.MessagesController r10 = r48.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r10 = r10.getInputPeer(r0)
            org.telegram.messenger.UserConfig r0 = r48.getUserConfig()
            int r0 = r0.getClientUserId()
            r24 = r9
            long r8 = (long) r0
            int r26 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r26 != 0) goto L_0x00ac
            r26 = 1
            goto L_0x00ae
        L_0x00ac:
            r26 = 0
        L_0x00ae:
            r27 = r1
            r28 = r2
            r2 = r3
            r3 = r4
            r1 = r22
            r4 = 0
            r22 = 0
        L_0x00b9:
            int r7 = r49.size()
            if (r4 >= r7) goto L_0x0774
            java.lang.Object r7 = r15.get(r4)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            int r30 = r7.getId()
            if (r30 <= 0) goto L_0x0702
            boolean r30 = r7.needDrawBluredPreview()
            if (r30 == 0) goto L_0x00d3
            goto L_0x0702
        L_0x00d3:
            if (r16 != 0) goto L_0x0130
            boolean r31 = r7.isSticker()
            if (r31 != 0) goto L_0x00ed
            boolean r31 = r7.isAnimatedSticker()
            if (r31 != 0) goto L_0x00ed
            boolean r31 = r7.isGif()
            if (r31 != 0) goto L_0x00ed
            boolean r31 = r7.isGame()
            if (r31 == 0) goto L_0x0130
        L_0x00ed:
            if (r22 != 0) goto L_0x0113
            r7 = 8
            boolean r7 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r7)
            if (r7 == 0) goto L_0x00fa
            r22 = 4
            goto L_0x00fc
        L_0x00fa:
            r22 = 1
        L_0x00fc:
            r29 = r0
            r15 = r4
            r35 = r6
            r30 = r8
            r25 = r10
            r33 = r24
            r0 = r27
            r37 = 0
            r38 = 1
            r39 = 0
            r27 = r5
            goto L_0x075a
        L_0x0113:
            r29 = r0
            r34 = r2
            r36 = r3
            r15 = r4
            r35 = r6
            r30 = r8
            r25 = r10
            r33 = r24
            r40 = r27
            r37 = 0
            r38 = 1
            r39 = 0
            r24 = r1
            r27 = r5
            goto L_0x0752
        L_0x0130:
            r31 = 2
            if (r17 != 0) goto L_0x0185
            org.telegram.tgnet.TLRPC$Message r14 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r14 = r14.media
            r33 = r10
            boolean r10 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r10 != 0) goto L_0x0142
            boolean r10 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r10 == 0) goto L_0x0187
        L_0x0142:
            if (r22 != 0) goto L_0x0168
            r7 = 7
            boolean r7 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r7)
            if (r7 == 0) goto L_0x014f
            r7 = 5
            r22 = 5
            goto L_0x0151
        L_0x014f:
            r22 = 2
        L_0x0151:
            r29 = r0
            r15 = r4
            r35 = r6
            r30 = r8
            r0 = r27
            r25 = r33
            r37 = 0
            r38 = 1
            r39 = 0
            r27 = r5
            r33 = r24
            goto L_0x075a
        L_0x0168:
            r29 = r0
            r34 = r2
            r36 = r3
            r15 = r4
            r35 = r6
            r30 = r8
            r40 = r27
            r25 = r33
            r37 = 0
            r38 = 1
            r39 = 0
            r27 = r5
            r33 = r24
            r24 = r1
            goto L_0x0752
        L_0x0185:
            r33 = r10
        L_0x0187:
            if (r18 != 0) goto L_0x01a3
            org.telegram.tgnet.TLRPC$Message r10 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r10 == 0) goto L_0x01a3
            if (r22 != 0) goto L_0x0168
            r7 = 10
            boolean r7 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r7)
            if (r7 == 0) goto L_0x019f
            r7 = 6
            r22 = 6
            goto L_0x0151
        L_0x019f:
            r7 = 3
            r22 = 3
            goto L_0x0151
        L_0x01a3:
            org.telegram.tgnet.TLRPC$TL_message r10 = new org.telegram.tgnet.TLRPC$TL_message
            r10.<init>()
            long r34 = r7.getDialogId()
            int r14 = (r34 > r8 ? 1 : (r34 == r8 ? 0 : -1))
            if (r14 != 0) goto L_0x01c2
            org.telegram.tgnet.TLRPC$Message r14 = r7.messageOwner
            int r14 = r14.from_id
            org.telegram.messenger.UserConfig r34 = r48.getUserConfig()
            r35 = r6
            int r6 = r34.getClientUserId()
            if (r14 != r6) goto L_0x01c4
            r6 = 1
            goto L_0x01c5
        L_0x01c2:
            r35 = r6
        L_0x01c4:
            r6 = 0
        L_0x01c5:
            boolean r14 = r7.isForwarded()
            if (r14 == 0) goto L_0x01f9
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r6 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r6.<init>()
            r10.fwd_from = r6
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            org.telegram.tgnet.TLRPC$Message r14 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            int r11 = r14.flags
            r6.flags = r11
            int r11 = r14.from_id
            r6.from_id = r11
            int r11 = r14.date
            r6.date = r11
            int r11 = r14.channel_id
            r6.channel_id = r11
            int r11 = r14.channel_post
            r6.channel_post = r11
            java.lang.String r11 = r14.post_author
            r6.post_author = r11
            java.lang.String r11 = r14.from_name
            r6.from_name = r11
            r11 = 4
            r10.flags = r11
            goto L_0x0299
        L_0x01f9:
            r11 = 4
            if (r6 != 0) goto L_0x0299
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r6 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r6.<init>()
            r10.fwd_from = r6
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r14 = r7.getId()
            r6.channel_post = r14
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r14 = r6.flags
            r14 = r14 | r11
            r6.flags = r14
            boolean r6 = r7.isFromUser()
            if (r6 == 0) goto L_0x0227
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            org.telegram.tgnet.TLRPC$Message r11 = r7.messageOwner
            int r11 = r11.from_id
            r6.from_id = r11
            int r11 = r6.flags
            r14 = 1
            r11 = r11 | r14
            r6.flags = r11
            goto L_0x0247
        L_0x0227:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            org.telegram.tgnet.TLRPC$Message r11 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r14 = r11.to_id
            int r14 = r14.channel_id
            r6.channel_id = r14
            int r14 = r6.flags
            r14 = r14 | 2
            r6.flags = r14
            boolean r14 = r11.post
            if (r14 == 0) goto L_0x0247
            int r11 = r11.from_id
            if (r11 <= 0) goto L_0x0247
            r6.from_id = r11
            int r11 = r6.flags
            r14 = 1
            r11 = r11 | r14
            r6.flags = r11
        L_0x0247:
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            java.lang.String r6 = r6.post_author
            if (r6 == 0) goto L_0x0259
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r10.fwd_from
            r11.post_author = r6
            int r6 = r11.flags
            r14 = 8
            r6 = r6 | r14
            r11.flags = r6
            goto L_0x0290
        L_0x0259:
            boolean r6 = r7.isOutOwner()
            if (r6 != 0) goto L_0x0290
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            int r11 = r6.from_id
            if (r11 <= 0) goto L_0x0290
            boolean r6 = r6.post
            if (r6 == 0) goto L_0x0290
            org.telegram.messenger.MessagesController r6 = r48.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r11 = r7.messageOwner
            int r11 = r11.from_id
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r11)
            if (r6 == 0) goto L_0x0290
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r10.fwd_from
            java.lang.String r14 = r6.first_name
            java.lang.String r6 = r6.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r14, r6)
            r11.post_author = r6
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            int r11 = r6.flags
            r14 = 8
            r11 = r11 | r14
            r6.flags = r11
        L_0x0290:
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            int r6 = r6.date
            r10.date = r6
            r6 = 4
            r10.flags = r6
        L_0x0299:
            int r6 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r6 != 0) goto L_0x02c5
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            if (r6 == 0) goto L_0x02c5
            int r11 = r6.flags
            r11 = r11 | 16
            r6.flags = r11
            int r11 = r7.getId()
            r6.saved_from_msg_id = r11
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r10.fwd_from
            org.telegram.tgnet.TLRPC$Message r11 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r11 = r11.to_id
            r6.saved_from_peer = r11
            org.telegram.tgnet.TLRPC$Peer r6 = r6.saved_from_peer
            int r11 = r6.user_id
            if (r11 != r0) goto L_0x02c5
            r30 = r8
            long r8 = r7.getDialogId()
            int r9 = (int) r8
            r6.user_id = r9
            goto L_0x02c7
        L_0x02c5:
            r30 = r8
        L_0x02c7:
            if (r20 != 0) goto L_0x02d9
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r6 == 0) goto L_0x02d9
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r6.<init>()
            r10.media = r6
            goto L_0x02df
        L_0x02d9:
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            r10.media = r6
        L_0x02df:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r10.media
            if (r6 == 0) goto L_0x02e9
            int r6 = r10.flags
            r6 = r6 | 512(0x200, float:7.175E-43)
            r10.flags = r6
        L_0x02e9:
            if (r19 == 0) goto L_0x02f2
            int r6 = r10.flags
            r8 = -2147483648(0xfffffffvar_, float:-0.0)
            r6 = r6 | r8
            r10.flags = r6
        L_0x02f2:
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            int r6 = r6.via_bot_id
            if (r6 == 0) goto L_0x0300
            r10.via_bot_id = r6
            int r6 = r10.flags
            r6 = r6 | 2048(0x800, float:2.87E-42)
            r10.flags = r6
        L_0x0300:
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            java.lang.String r6 = r6.message
            r10.message = r6
            int r6 = r7.getId()
            r10.fwd_msg_id = r6
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            java.lang.String r8 = r6.attachPath
            r10.attachPath = r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r6.entities
            r10.entities = r8
            org.telegram.tgnet.TLRPC$ReplyMarkup r6 = r6.reply_markup
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup
            if (r6 == 0) goto L_0x03c9
            org.telegram.tgnet.TLRPC$TL_replyInlineMarkup r6 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            r6.<init>()
            r10.reply_markup = r6
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r6 = r6.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r6 = r6.rows
            int r6 = r6.size()
            r8 = 0
            r9 = 0
        L_0x032f:
            if (r8 >= r6) goto L_0x03b6
            org.telegram.tgnet.TLRPC$Message r11 = r7.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r11 = r11.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r11 = r11.rows
            java.lang.Object r11 = r11.get(r8)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r11 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r14 = r11.buttons
            int r14 = r14.size()
            r32 = r0
            r0 = 0
            r34 = 0
        L_0x0348:
            r36 = r6
            if (r0 >= r14) goto L_0x03a9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r6 = r11.buttons
            java.lang.Object r6 = r6.get(r0)
            org.telegram.tgnet.TLRPC$KeyboardButton r6 = (org.telegram.tgnet.TLRPC.KeyboardButton) r6
            r37 = r9
            boolean r9 = r6 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth
            r38 = r11
            if (r9 != 0) goto L_0x0367
            boolean r11 = r6 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl
            if (r11 != 0) goto L_0x0367
            boolean r11 = r6 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline
            if (r11 == 0) goto L_0x0365
            goto L_0x0367
        L_0x0365:
            r9 = 1
            goto L_0x03ab
        L_0x0367:
            if (r9 == 0) goto L_0x0388
            org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r9.<init>()
            int r11 = r6.flags
            r9.flags = r11
            java.lang.String r11 = r6.fwd_text
            if (r11 == 0) goto L_0x037b
            r9.fwd_text = r11
            r9.text = r11
            goto L_0x037f
        L_0x037b:
            java.lang.String r11 = r6.text
            r9.text = r11
        L_0x037f:
            java.lang.String r11 = r6.url
            r9.url = r11
            int r6 = r6.button_id
            r9.button_id = r6
            r6 = r9
        L_0x0388:
            if (r34 != 0) goto L_0x0397
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow
            r9.<init>()
            org.telegram.tgnet.TLRPC$ReplyMarkup r11 = r10.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r11 = r11.rows
            r11.add(r9)
            goto L_0x0399
        L_0x0397:
            r9 = r34
        L_0x0399:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r11 = r9.buttons
            r11.add(r6)
            int r0 = r0 + 1
            r34 = r9
            r6 = r36
            r9 = r37
            r11 = r38
            goto L_0x0348
        L_0x03a9:
            r37 = r9
        L_0x03ab:
            if (r9 == 0) goto L_0x03ae
            goto L_0x03b8
        L_0x03ae:
            int r8 = r8 + 1
            r0 = r32
            r6 = r36
            goto L_0x032f
        L_0x03b6:
            r32 = r0
        L_0x03b8:
            r37 = r9
            if (r37 != 0) goto L_0x03c3
            int r0 = r10.flags
            r0 = r0 | 64
            r10.flags = r0
            goto L_0x03cb
        L_0x03c3:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            r8 = 0
            r0.reply_markup = r8
            goto L_0x03cc
        L_0x03c9:
            r32 = r0
        L_0x03cb:
            r8 = 0
        L_0x03cc:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r10.entities
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x03da
            int r0 = r10.flags
            r0 = r0 | 128(0x80, float:1.794E-43)
            r10.flags = r0
        L_0x03da:
            java.lang.String r0 = r10.attachPath
            if (r0 != 0) goto L_0x03e2
            java.lang.String r0 = ""
            r10.attachPath = r0
        L_0x03e2:
            org.telegram.messenger.UserConfig r0 = r48.getUserConfig()
            int r0 = r0.getNewMessageId()
            r10.id = r0
            r10.local_id = r0
            r0 = 1
            r10.out = r0
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            long r8 = r0.grouped_id
            r36 = 0
            int r0 = (r8 > r36 ? 1 : (r8 == r36 ? 0 : -1))
            if (r0 == 0) goto L_0x0427
            java.lang.Object r0 = r5.get(r8)
            java.lang.Long r0 = (java.lang.Long) r0
            if (r0 != 0) goto L_0x0417
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r38 = r0.nextLong()
            java.lang.Long r0 = java.lang.Long.valueOf(r38)
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            r38 = r8
            long r8 = r6.grouped_id
            r5.put(r8, r0)
            goto L_0x0419
        L_0x0417:
            r38 = r8
        L_0x0419:
            long r8 = r0.longValue()
            r10.grouped_id = r8
            int r0 = r10.flags
            r6 = 131072(0x20000, float:1.83671E-40)
            r0 = r0 | r6
            r10.flags = r0
            goto L_0x0429
        L_0x0427:
            r38 = r8
        L_0x0429:
            int r0 = r49.size()
            r6 = 1
            int r0 = r0 - r6
            if (r4 == r0) goto L_0x044a
            int r0 = r4 + 1
            java.lang.Object r0 = r15.get(r0)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            long r8 = r0.grouped_id
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            r11 = r5
            long r5 = r0.grouped_id
            int r0 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x044b
            r9 = r24
            r0 = 1
            goto L_0x044e
        L_0x044a:
            r11 = r5
        L_0x044b:
            r9 = r24
            r0 = 0
        L_0x044e:
            int r5 = r9.channel_id
            if (r5 == 0) goto L_0x0466
            if (r19 != 0) goto L_0x0466
            if (r21 == 0) goto L_0x045f
            org.telegram.messenger.UserConfig r5 = r48.getUserConfig()
            int r5 = r5.getClientUserId()
            goto L_0x0460
        L_0x045f:
            int r5 = -r5
        L_0x0460:
            r10.from_id = r5
            r5 = 1
            r10.post = r5
            goto L_0x0476
        L_0x0466:
            org.telegram.messenger.UserConfig r5 = r48.getUserConfig()
            int r5 = r5.getClientUserId()
            r10.from_id = r5
            int r5 = r10.flags
            r5 = r5 | 256(0x100, float:3.59E-43)
            r10.flags = r5
        L_0x0476:
            long r5 = r10.random_id
            int r8 = (r5 > r36 ? 1 : (r5 == r36 ? 0 : -1))
            if (r8 != 0) goto L_0x0482
            long r5 = r48.getNextRandomId()
            r10.random_id = r5
        L_0x0482:
            long r5 = r10.random_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            r2.add(r5)
            long r5 = r10.random_id
            r1.put(r5, r10)
            int r5 = r10.fwd_msg_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r3.add(r5)
            r14 = r53
            if (r14 == 0) goto L_0x049f
            r5 = r14
            goto L_0x04a7
        L_0x049f:
            org.telegram.tgnet.ConnectionsManager r5 = r48.getConnectionsManager()
            int r5 = r5.getCurrentTime()
        L_0x04a7:
            r10.date = r5
            r8 = r33
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel
            if (r5 == 0) goto L_0x04bf
            if (r19 != 0) goto L_0x04bf
            if (r14 != 0) goto L_0x04bc
            r6 = 1
            r10.views = r6
            int r6 = r10.flags
            r6 = r6 | 1024(0x400, float:1.435E-42)
            r10.flags = r6
        L_0x04bc:
            r24 = r1
            goto L_0x04d8
        L_0x04bf:
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            r24 = r1
            int r1 = r6.flags
            r1 = r1 & 1024(0x400, float:1.435E-42)
            if (r1 == 0) goto L_0x04d5
            if (r14 != 0) goto L_0x04d5
            int r1 = r6.views
            r10.views = r1
            int r1 = r10.flags
            r1 = r1 | 1024(0x400, float:1.435E-42)
            r10.flags = r1
        L_0x04d5:
            r1 = 1
            r10.unread = r1
        L_0x04d8:
            r10.dialog_id = r12
            r10.to_id = r9
            boolean r1 = org.telegram.messenger.MessageObject.isVoiceMessage(r10)
            if (r1 != 0) goto L_0x04e8
            boolean r1 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r10)
            if (r1 == 0) goto L_0x04fa
        L_0x04e8:
            if (r5 == 0) goto L_0x04f7
            int r1 = r7.getChannelId()
            if (r1 == 0) goto L_0x04f7
            boolean r1 = r7.isContentUnread()
            r10.media_unread = r1
            goto L_0x04fa
        L_0x04f7:
            r1 = 1
            r10.media_unread = r1
        L_0x04fa:
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel
            if (r5 == 0) goto L_0x0507
            int r1 = r1.channel_id
            int r1 = -r1
            r10.ttl = r1
        L_0x0507:
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject
            r6 = r48
            int r5 = r6.currentAccount
            r33 = r9
            r9 = 1
            r1.<init>(r5, r10, r9)
            if (r14 == 0) goto L_0x0517
            r5 = 1
            goto L_0x0518
        L_0x0517:
            r5 = 0
        L_0x0518:
            r1.scheduled = r5
            org.telegram.tgnet.TLRPC$Message r5 = r1.messageOwner
            r5.send_state = r9
            r9 = r27
            r9.add(r1)
            r5 = r28
            r5.add(r10)
            if (r14 == 0) goto L_0x052c
            r1 = 1
            goto L_0x052d
        L_0x052c:
            r1 = 0
        L_0x052d:
            r6.putToSendingMessages(r10, r1)
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x056b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r10 = "forward message user_id = "
            r1.append(r10)
            int r10 = r8.user_id
            r1.append(r10)
            java.lang.String r10 = " chat_id = "
            r1.append(r10)
            int r10 = r8.chat_id
            r1.append(r10)
            java.lang.String r10 = " channel_id = "
            r1.append(r10)
            int r10 = r8.channel_id
            r1.append(r10)
            java.lang.String r10 = " access_hash = "
            r1.append(r10)
            r27 = r11
            long r10 = r8.access_hash
            r1.append(r10)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.FileLog.d(r1)
            goto L_0x056d
        L_0x056b:
            r27 = r11
        L_0x056d:
            if (r0 == 0) goto L_0x0575
            int r0 = r5.size()
            if (r0 > 0) goto L_0x05b6
        L_0x0575:
            int r0 = r5.size()
            r1 = 100
            if (r0 == r1) goto L_0x05b6
            int r0 = r49.size()
            r1 = 1
            int r0 = r0 - r1
            if (r4 == r0) goto L_0x05b6
            int r0 = r49.size()
            int r0 = r0 - r1
            if (r4 == r0) goto L_0x05a1
            int r0 = r4 + 1
            java.lang.Object r0 = r15.get(r0)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r0 = r0.getDialogId()
            long r10 = r7.getDialogId()
            int r28 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r28 == 0) goto L_0x05a1
            goto L_0x05b6
        L_0x05a1:
            r34 = r2
            r36 = r3
            r15 = r4
            r28 = r5
            r25 = r8
            r40 = r9
            r29 = r32
            r37 = 0
            r38 = 1
            r39 = 0
            goto L_0x0752
        L_0x05b6:
            org.telegram.messenger.MessagesStorage r40 = r48.getMessagesStorage()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r5)
            r42 = 0
            r43 = 1
            r44 = 0
            r45 = 0
            if (r14 == 0) goto L_0x05cc
            r46 = 1
            goto L_0x05ce
        L_0x05cc:
            r46 = 0
        L_0x05ce:
            r41 = r0
            r40.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC.Message>) r41, (boolean) r42, (boolean) r43, (boolean) r44, (int) r45, (boolean) r46)
            org.telegram.messenger.MessagesController r0 = r48.getMessagesController()
            if (r14 == 0) goto L_0x05db
            r1 = 1
            goto L_0x05dc
        L_0x05db:
            r1 = 0
        L_0x05dc:
            r0.updateInterfaceWithMessages(r12, r9, r1)
            org.telegram.messenger.NotificationCenter r0 = r48.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r0.postNotificationName(r1, r11)
            org.telegram.messenger.UserConfig r0 = r48.getUserConfig()
            r0.saveConfig(r10)
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r11 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages
            r11.<init>()
            r11.to_peer = r8
            int r0 = (r38 > r36 ? 1 : (r38 == r36 ? 0 : -1))
            if (r0 == 0) goto L_0x05ff
            r0 = 1
            goto L_0x0600
        L_0x05ff:
            r0 = 0
        L_0x0600:
            r11.grouped = r0
            if (r52 == 0) goto L_0x0625
            int r0 = r6.currentAccount
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r10 = "silent_"
            r1.append(r10)
            r1.append(r12)
            java.lang.String r1 = r1.toString()
            r10 = 0
            boolean r0 = r0.getBoolean(r1, r10)
            if (r0 == 0) goto L_0x0623
            goto L_0x0625
        L_0x0623:
            r0 = 0
            goto L_0x0626
        L_0x0625:
            r0 = 1
        L_0x0626:
            r11.silent = r0
            if (r14 == 0) goto L_0x0632
            r11.schedule_date = r14
            int r0 = r11.flags
            r0 = r0 | 1024(0x400, float:1.435E-42)
            r11.flags = r0
        L_0x0632:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel
            if (r0 == 0) goto L_0x0667
            org.telegram.messenger.MessagesController r0 = r48.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.to_id
            int r1 = r1.channel_id
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            r1.<init>()
            r11.from_peer = r1
            org.telegram.tgnet.TLRPC$InputPeer r1 = r11.from_peer
            org.telegram.tgnet.TLRPC$Message r10 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r10 = r10.to_id
            int r10 = r10.channel_id
            r1.channel_id = r10
            r10 = r4
            r28 = r5
            if (r0 == 0) goto L_0x0671
            long r4 = r0.access_hash
            r1.access_hash = r4
            goto L_0x0671
        L_0x0667:
            r10 = r4
            r28 = r5
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r0.<init>()
            r11.from_peer = r0
        L_0x0671:
            r11.random_id = r2
            r11.id = r3
            int r0 = r49.size()
            r5 = 1
            r4 = 0
            if (r0 != r5) goto L_0x068b
            java.lang.Object r0 = r15.get(r4)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0.with_my_score
            if (r0 == 0) goto L_0x068b
            r0 = 1
            goto L_0x068c
        L_0x068b:
            r0 = 0
        L_0x068c:
            r11.with_my_score = r0
            r0 = 2147483646(0x7ffffffe, float:NaN)
            if (r14 != r0) goto L_0x0696
            r23 = 1
            goto L_0x0698
        L_0x0696:
            r23 = 0
        L_0x0698:
            org.telegram.tgnet.ConnectionsManager r1 = r48.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$flpU3A3e0tPwUqp389LcbHlF_dc r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$flpU3A3e0tPwUqp389LcbHlF_dc
            r29 = r32
            r32 = r0
            r14 = r1
            r1 = r48
            r34 = r2
            r36 = r3
            r2 = r50
            r37 = 0
            r4 = r53
            r38 = 1
            r5 = r19
            r6 = r23
            r23 = r7
            r7 = r26
            r25 = r8
            r39 = 0
            r8 = r24
            r40 = r9
            r9 = r28
            r15 = r10
            r10 = r40
            r41 = r11
            r11 = r23
            r12 = r33
            r13 = r41
            r0.<init>(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r0 = 68
            r2 = r32
            r1 = r41
            r14.sendRequest(r1, r2, r0)
            int r0 = r49.size()
            int r0 = r0 + -1
            if (r15 == r0) goto L_0x0752
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r28 = new java.util.ArrayList
            r28.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            android.util.LongSparseArray r3 = new android.util.LongSparseArray
            r3.<init>()
            r47 = r2
            r2 = r1
            r1 = r3
            r3 = r47
            goto L_0x075a
        L_0x0702:
            r29 = r0
            r34 = r2
            r36 = r3
            r15 = r4
            r35 = r6
            r30 = r8
            r25 = r10
            r33 = r24
            r40 = r27
            r37 = 0
            r38 = 1
            r39 = 0
            r24 = r1
            r27 = r5
            int r0 = r7.type
            if (r0 != 0) goto L_0x0752
            java.lang.CharSequence r0 = r7.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0752
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0733
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            r5 = r0
            goto L_0x0735
        L_0x0733:
            r5 = r39
        L_0x0735:
            java.lang.CharSequence r0 = r7.messageText
            java.lang.String r1 = r0.toString()
            r4 = 0
            if (r5 == 0) goto L_0x0740
            r6 = 1
            goto L_0x0741
        L_0x0740:
            r6 = 0
        L_0x0741:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r0.entities
            r8 = 0
            r9 = 0
            r0 = r48
            r2 = r50
            r10 = r52
            r11 = r53
            r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11)
        L_0x0752:
            r1 = r24
            r2 = r34
            r3 = r36
            r0 = r40
        L_0x075a:
            int r4 = r15 + 1
            r14 = r48
            r15 = r49
            r12 = r50
            r11 = r53
            r10 = r25
            r5 = r27
            r8 = r30
            r24 = r33
            r6 = r35
            r27 = r0
            r0 = r29
            goto L_0x00b9
        L_0x0774:
            r3 = r48
            r37 = r22
            goto L_0x0796
        L_0x0779:
            r37 = 0
            r0 = 0
        L_0x077c:
            int r1 = r49.size()
            if (r0 >= r1) goto L_0x0794
            r1 = r49
            java.lang.Object r2 = r1.get(r0)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            r3 = r48
            r4 = r50
            r3.processForwardFromMyName(r2, r4)
            int r0 = r0 + 1
            goto L_0x077c
        L_0x0794:
            r3 = r48
        L_0x0796:
            return r37
        L_0x0797:
            r3 = r14
            r37 = 0
            return r37
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, int):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x00e7  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ea  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x01ae  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendMessage$14$SendMessagesHelper(long r26, int r28, boolean r29, boolean r30, boolean r31, android.util.LongSparseArray r32, java.util.ArrayList r33, java.util.ArrayList r34, org.telegram.messenger.MessageObject r35, org.telegram.tgnet.TLRPC.Peer r36, org.telegram.tgnet.TLRPC.TL_messages_forwardMessages r37, org.telegram.tgnet.TLObject r38, org.telegram.tgnet.TLRPC.TL_error r39) {
        /*
            r25 = this;
            r11 = r25
            r12 = r28
            r13 = r33
            r14 = r34
            r0 = r39
            r15 = 1
            if (r0 != 0) goto L_0x01e1
            org.telegram.messenger.support.SparseLongArray r9 = new org.telegram.messenger.support.SparseLongArray
            r9.<init>()
            r7 = r38
            org.telegram.tgnet.TLRPC$Updates r7 = (org.telegram.tgnet.TLRPC.Updates) r7
            r0 = 0
        L_0x0017:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x003d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$Update r1 = (org.telegram.tgnet.TLRPC.Update) r1
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateMessageID
            if (r2 == 0) goto L_0x003b
            org.telegram.tgnet.TLRPC$TL_updateMessageID r1 = (org.telegram.tgnet.TLRPC.TL_updateMessageID) r1
            int r2 = r1.id
            long r3 = r1.random_id
            r9.put(r2, r3)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            r1.remove(r0)
            int r0 = r0 + -1
        L_0x003b:
            int r0 = r0 + r15
            goto L_0x0017
        L_0x003d:
            org.telegram.messenger.MessagesController r0 = r25.getMessagesController()
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r0.dialogs_read_outbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r26)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x006b
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            r5 = r26
            int r0 = r0.getDialogReadMax(r15, r5)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.messenger.MessagesController r1 = r25.getMessagesController()
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            java.lang.Long r2 = java.lang.Long.valueOf(r26)
            r1.put(r2, r0)
            goto L_0x006d
        L_0x006b:
            r5 = r26
        L_0x006d:
            r16 = r0
            r0 = 0
            r8 = 0
        L_0x0071:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x01c3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$Update r1 = (org.telegram.tgnet.TLRPC.Update) r1
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewMessage
            if (r2 != 0) goto L_0x0097
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage
            if (r3 != 0) goto L_0x0097
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage
            if (r3 == 0) goto L_0x008e
            goto L_0x0097
        L_0x008e:
            r17 = r0
            r14 = r7
            r19 = r9
            r1 = 1
            r13 = 0
            goto L_0x01b3
        L_0x0097:
            if (r12 == 0) goto L_0x009b
            r3 = 1
            goto L_0x009c
        L_0x009b:
            r3 = 0
        L_0x009c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r7.updates
            r4.remove(r0)
            int r17 = r0 + -1
            r0 = -1
            if (r2 == 0) goto L_0x00b7
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r1 = (org.telegram.tgnet.TLRPC.TL_updateNewMessage) r1
            org.telegram.tgnet.TLRPC$Message r2 = r1.message
            org.telegram.messenger.MessagesController r4 = r25.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            r4.processNewDifferenceParams(r0, r10, r0, r1)
        L_0x00b5:
            r10 = r2
            goto L_0x00de
        L_0x00b7:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x00c1
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r1 = (org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage) r1
            org.telegram.tgnet.TLRPC$Message r1 = r1.message
            r10 = r1
            goto L_0x00de
        L_0x00c1:
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r1 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r1
            org.telegram.tgnet.TLRPC$Message r2 = r1.message
            org.telegram.messenger.MessagesController r4 = r25.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            org.telegram.tgnet.TLRPC$Peer r0 = r2.to_id
            int r0 = r0.channel_id
            r4.processNewChannelDifferenceParams(r10, r1, r0)
            if (r29 == 0) goto L_0x00b5
            int r0 = r2.flags
            r1 = -2147483648(0xfffffffvar_, float:-0.0)
            r0 = r0 | r1
            r2.flags = r0
            goto L_0x00b5
        L_0x00de:
            if (r30 == 0) goto L_0x00ea
            int r0 = r10.date
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 == r1) goto L_0x00ea
            r19 = 0
            goto L_0x00ec
        L_0x00ea:
            r19 = r3
        L_0x00ec:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r10)
            if (r19 != 0) goto L_0x00fe
            int r0 = r16.intValue()
            int r1 = r10.id
            if (r0 >= r1) goto L_0x00fb
            r0 = 1
            goto L_0x00fc
        L_0x00fb:
            r0 = 0
        L_0x00fc:
            r10.unread = r0
        L_0x00fe:
            if (r31 == 0) goto L_0x0108
            r10.out = r15
            r4 = 0
            r10.unread = r4
            r10.media_unread = r4
            goto L_0x0109
        L_0x0108:
            r4 = 0
        L_0x0109:
            int r0 = r10.id
            long r0 = r9.get(r0)
            r2 = 0
            int r18 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r18 == 0) goto L_0x01ae
            r3 = r32
            java.lang.Object r0 = r3.get(r0)
            r2 = r0
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC.Message) r2
            if (r2 != 0) goto L_0x0122
            goto L_0x01ae
        L_0x0122:
            int r0 = r13.indexOf(r2)
            r1 = -1
            if (r0 != r1) goto L_0x012b
            goto L_0x01ae
        L_0x012b:
            java.lang.Object r1 = r14.get(r0)
            r18 = r1
            org.telegram.messenger.MessageObject r18 = (org.telegram.messenger.MessageObject) r18
            r13.remove(r0)
            r14.remove(r0)
            int r1 = r2.id
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r0.add(r10)
            int r4 = r10.id
            r20 = 0
            r21 = 1
            r22 = r0
            r0 = r25
            r23 = r1
            r1 = r18
            r15 = r2
            r2 = r10
            r3 = r4
            r24 = 0
            r4 = r20
            r5 = r21
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r18 = r18.getMediaExistanceFlags()
            int r0 = r10.id
            r15.id = r0
            int r20 = r8 + 1
            if (r12 == 0) goto L_0x0184
            if (r19 != 0) goto L_0x0184
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$_gHTxqQKXqvVN-u6Eg96RvX1uR8 r8 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$_gHTxqQKXqvVN-u6Eg96RvX1uR8
            r0 = r8
            r1 = r25
            r2 = r23
            r3 = r15
            r4 = r22
            r5 = r35
            r6 = r28
            r0.<init>(r2, r3, r4, r5, r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8)
            r14 = r7
            r19 = r9
            r13 = 0
            goto L_0x01ab
        L_0x0184:
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$ZCbfkTtQsDOKM8NiFiE3mHK3dIo r6 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$ZCbfkTtQsDOKM8NiFiE3mHK3dIo
            r0 = r6
            r1 = r25
            r2 = r15
            r3 = r23
            r4 = r36
            r5 = r28
            r15 = r6
            r6 = r22
            r14 = r7
            r12 = r8
            r7 = r26
            r19 = r9
            r9 = r10
            r13 = 0
            r10 = r18
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10)
            r12.postRunnable(r15)
        L_0x01ab:
            r8 = r20
            goto L_0x01b2
        L_0x01ae:
            r14 = r7
            r19 = r9
            r13 = 0
        L_0x01b2:
            r1 = 1
        L_0x01b3:
            int r0 = r17 + 1
            r5 = r26
            r12 = r28
            r13 = r33
            r7 = r14
            r9 = r19
            r15 = 1
            r14 = r34
            goto L_0x0071
        L_0x01c3:
            r14 = r7
            r1 = 1
            r13 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r0 = r14.updates
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01d5
            org.telegram.messenger.MessagesController r0 = r25.getMessagesController()
            r0.processUpdates(r14, r13)
        L_0x01d5:
            org.telegram.messenger.StatsController r0 = r25.getStatsController()
            int r2 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r0.incrementSentItemsCount(r2, r1, r8)
            goto L_0x01ed
        L_0x01e1:
            r1 = 1
            r13 = 0
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$Np5Q7xHh1zw91IDVJqdT9IWuXcI r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Np5Q7xHh1zw91IDVJqdT9IWuXcI
            r3 = r37
            r2.<init>(r0, r3)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x01ed:
            r0 = 0
        L_0x01ee:
            int r2 = r33.size()
            if (r0 >= r2) goto L_0x0215
            r2 = r33
            java.lang.Object r3 = r2.get(r0)
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC.Message) r3
            org.telegram.messenger.MessagesStorage r4 = r25.getMessagesStorage()
            r5 = r28
            if (r5 == 0) goto L_0x0206
            r6 = 1
            goto L_0x0207
        L_0x0206:
            r6 = 0
        L_0x0207:
            r4.markMessageAsSendError(r3, r6)
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$9-4ORe2MVG9HpTDZt06-uF5tbLE r4 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$9-4ORe2MVG9HpTDZt06-uF5tbLE
            r4.<init>(r3, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            int r0 = r0 + 1
            goto L_0x01ee
        L_0x0215:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendMessage$14$SendMessagesHelper(long, int, boolean, boolean, boolean, android.util.LongSparseArray, java.util.ArrayList, java.util.ArrayList, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Peer, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$null$9$SendMessagesHelper(int i, TLRPC.Message message, ArrayList arrayList, MessageObject messageObject, int i2) {
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(Integer.valueOf(i));
        getMessagesController().deleteMessages(arrayList2, (ArrayList<Long>) null, (TLRPC.EncryptedChat) null, message.dialog_id, message.to_id.channel_id, false, true);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList, messageObject, message, i, i2) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ MessageObject f$2;
            private final /* synthetic */ TLRPC.Message f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$8$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$8$SendMessagesHelper(ArrayList arrayList, MessageObject messageObject, TLRPC.Message message, int i, int i2) {
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable(messageObject, message, i, i2) {
            private final /* synthetic */ MessageObject f$1;
            private final /* synthetic */ TLRPC.Message f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$7$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
    }

    public /* synthetic */ void lambda$null$7$SendMessagesHelper(MessageObject messageObject, TLRPC.Message message, int i, int i2) {
        ArrayList arrayList = new ArrayList();
        boolean z = true;
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true));
        getMessagesController().updateInterfaceWithMessages(message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        processSentMessage(i);
        if (i2 == 0) {
            z = false;
        }
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$11$SendMessagesHelper(TLRPC.Message message, int i, TLRPC.Peer peer, int i2, ArrayList arrayList, long j, TLRPC.Message message2, int i3) {
        TLRPC.Message message3 = message;
        getMessagesStorage().updateMessageStateAndId(message3.random_id, Integer.valueOf(i), message3.id, 0, false, peer.channel_id, i2 != 0 ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, true, false, false, 0, i2 != 0);
        AndroidUtilities.runOnUIThread(new Runnable(message, j, i, message2, i3, i2) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ TLRPC.Message f$4;
            private final /* synthetic */ int f$5;
            private final /* synthetic */ int f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$10$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$10$SendMessagesHelper(TLRPC.Message message, long j, int i, TLRPC.Message message2, int i2, int i3) {
        boolean z = false;
        message.send_state = 0;
        getMediaDataController().increasePeerRaiting(j);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i4 = NotificationCenter.messageReceivedByServer;
        Object[] objArr = new Object[7];
        objArr[0] = Integer.valueOf(i);
        objArr[1] = Integer.valueOf(message2.id);
        objArr[2] = message2;
        objArr[3] = Long.valueOf(j);
        objArr[4] = 0L;
        objArr[5] = Integer.valueOf(i2);
        objArr[6] = Boolean.valueOf(i3 != 0);
        notificationCenter.postNotificationName(i4, objArr);
        processSentMessage(i);
        if (i3 != 0) {
            z = true;
        }
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$12$SendMessagesHelper(TLRPC.TL_error tL_error, TLRPC.TL_messages_forwardMessages tL_messages_forwardMessages) {
        AlertsCreator.processError(this.currentAccount, tL_error, (BaseFragment) null, tL_messages_forwardMessages, new Object[0]);
    }

    public /* synthetic */ void lambda$null$13$SendMessagesHelper(TLRPC.Message message, int i) {
        message.send_state = 2;
        boolean z = true;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
        processSentMessage(message.id);
        int i2 = message.id;
        if (i == 0) {
            z = false;
        }
        removeFromSendingMessages(i2, z);
    }

    private void writePreviousMessageData(TLRPC.Message message, SerializedData serializedData) {
        message.media.serializeToStream(serializedData);
        String str = message.message;
        if (str == null) {
            str = "";
        }
        serializedData.writeString(str);
        String str2 = message.attachPath;
        if (str2 == null) {
            str2 = "";
        }
        serializedData.writeString(str2);
        int size = message.entities.size();
        serializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            message.entities.get(i).serializeToStream(serializedData);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v15, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v16, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v22, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v23, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v29, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v47, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v36, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v51, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v53, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v54, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0226 A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x02db A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0041 A[SYNTHETIC, Splitter:B:16:0x0041] */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0403 A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0415 A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x045e A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0463 A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0479 A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008e A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x0137 A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0154 A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x017e A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01ff A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x020b A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0213 A[Catch:{ Exception -> 0x0508 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void editMessageMedia(org.telegram.messenger.MessageObject r28, org.telegram.tgnet.TLRPC.TL_photo r29, org.telegram.messenger.VideoEditedInfo r30, org.telegram.tgnet.TLRPC.TL_document r31, java.lang.String r32, java.util.HashMap<java.lang.String, java.lang.String> r33, boolean r34, java.lang.Object r35) {
        /*
            r27 = this;
            r10 = r27
            r11 = r28
            r0 = r29
            r1 = r31
            r2 = r32
            java.lang.String r3 = "originalPath"
            java.lang.String r4 = "parentObject"
            if (r11 != 0) goto L_0x0011
            return
        L_0x0011:
            org.telegram.tgnet.TLRPC$Message r5 = r11.messageOwner
            r6 = 0
            r11.cancelEditing = r6
            long r7 = r28.getDialogId()     // Catch:{ Exception -> 0x0508 }
            int r9 = (int) r7     // Catch:{ Exception -> 0x0508 }
            if (r9 != 0) goto L_0x003c
            r13 = 32
            long r13 = r7 >> r13
            int r14 = (int) r13     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.MessagesController r13 = r27.getMessagesController()     // Catch:{ Exception -> 0x0508 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$EncryptedChat r13 = r13.getEncryptedChat(r14)     // Catch:{ Exception -> 0x0508 }
            if (r13 == 0) goto L_0x003a
            int r13 = r13.layer     // Catch:{ Exception -> 0x0508 }
            int r13 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r13)     // Catch:{ Exception -> 0x0508 }
            r14 = 101(0x65, float:1.42E-43)
            if (r13 >= r14) goto L_0x003c
        L_0x003a:
            r13 = 0
            goto L_0x003d
        L_0x003c:
            r13 = 1
        L_0x003d:
            java.lang.String r14 = "http"
            if (r34 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media     // Catch:{ Exception -> 0x0508 }
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto     // Catch:{ Exception -> 0x0508 }
            if (r2 == 0) goto L_0x0055
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0     // Catch:{ Exception -> 0x0508 }
            r15 = r30
            r2 = 2
            goto L_0x006b
        L_0x0055:
            org.telegram.tgnet.TLRPC$Message r1 = r11.messageOwner     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$Document r1 = r1.document     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC.TL_document) r1     // Catch:{ Exception -> 0x0508 }
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1)     // Catch:{ Exception -> 0x0508 }
            if (r2 != 0) goto L_0x0068
            if (r30 == 0) goto L_0x0066
            goto L_0x0068
        L_0x0066:
            r2 = 7
            goto L_0x0069
        L_0x0068:
            r2 = 3
        L_0x0069:
            org.telegram.messenger.VideoEditedInfo r15 = r11.videoEditedInfo     // Catch:{ Exception -> 0x0508 }
        L_0x006b:
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r5.params     // Catch:{ Exception -> 0x0508 }
            if (r35 != 0) goto L_0x007c
            if (r6 == 0) goto L_0x007c
            boolean r18 = r6.containsKey(r4)     // Catch:{ Exception -> 0x0508 }
            if (r18 == 0) goto L_0x007c
            java.lang.Object r4 = r6.get(r4)     // Catch:{ Exception -> 0x0508 }
            goto L_0x007e
        L_0x007c:
            r4 = r35
        L_0x007e:
            java.lang.String r12 = r5.message     // Catch:{ Exception -> 0x0508 }
            r11.editingMessage = r12     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r12 = r5.entities     // Catch:{ Exception -> 0x0508 }
            r11.editingMessageEntities = r12     // Catch:{ Exception -> 0x0508 }
            java.lang.String r12 = r5.attachPath     // Catch:{ Exception -> 0x0508 }
            r19 = r9
            r9 = r4
            r4 = r6
            goto L_0x0150
        L_0x008e:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0508 }
            r11.previousMedia = r4     // Catch:{ Exception -> 0x0508 }
            java.lang.String r4 = r5.message     // Catch:{ Exception -> 0x0508 }
            r11.previousCaption = r4     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r5.entities     // Catch:{ Exception -> 0x0508 }
            r11.previousCaptionEntities = r4     // Catch:{ Exception -> 0x0508 }
            java.lang.String r4 = r5.attachPath     // Catch:{ Exception -> 0x0508 }
            r11.previousAttachPath = r4     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0508 }
            r6 = 1
            r4.<init>((boolean) r6)     // Catch:{ Exception -> 0x0508 }
            r10.writePreviousMessageData(r5, r4)     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.SerializedData r6 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0508 }
            int r4 = r4.length()     // Catch:{ Exception -> 0x0508 }
            r6.<init>((int) r4)     // Catch:{ Exception -> 0x0508 }
            r10.writePreviousMessageData(r5, r6)     // Catch:{ Exception -> 0x0508 }
            if (r33 != 0) goto L_0x00bb
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ Exception -> 0x0508 }
            r4.<init>()     // Catch:{ Exception -> 0x0508 }
            goto L_0x00bd
        L_0x00bb:
            r4 = r33
        L_0x00bd:
            java.lang.String r12 = "prevMedia"
            byte[] r15 = r6.toByteArray()     // Catch:{ Exception -> 0x0508 }
            r19 = r9
            r9 = 0
            java.lang.String r15 = android.util.Base64.encodeToString(r15, r9)     // Catch:{ Exception -> 0x0508 }
            r4.put(r12, r15)     // Catch:{ Exception -> 0x0508 }
            r6.cleanup()     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x0114
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0508 }
            r6.<init>()     // Catch:{ Exception -> 0x0508 }
            r5.media = r6     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media     // Catch:{ Exception -> 0x0508 }
            int r9 = r6.flags     // Catch:{ Exception -> 0x0508 }
            r12 = 3
            r9 = r9 | r12
            r6.flags = r9     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media     // Catch:{ Exception -> 0x0508 }
            r6.photo = r0     // Catch:{ Exception -> 0x0508 }
            if (r2 == 0) goto L_0x00f6
            int r6 = r32.length()     // Catch:{ Exception -> 0x0508 }
            if (r6 <= 0) goto L_0x00f6
            boolean r6 = r2.startsWith(r14)     // Catch:{ Exception -> 0x0508 }
            if (r6 == 0) goto L_0x00f6
            r5.attachPath = r2     // Catch:{ Exception -> 0x0508 }
            goto L_0x0112
        L_0x00f6:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r0.sizes     // Catch:{ Exception -> 0x0508 }
            int r9 = r9.size()     // Catch:{ Exception -> 0x0508 }
            r12 = 1
            int r9 = r9 - r12
            java.lang.Object r6 = r6.get(r9)     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC.PhotoSize) r6     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.location     // Catch:{ Exception -> 0x0508 }
            java.io.File r6 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r12)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0508 }
            r5.attachPath = r6     // Catch:{ Exception -> 0x0508 }
        L_0x0112:
            r15 = 2
            goto L_0x0145
        L_0x0114:
            if (r1 == 0) goto L_0x0144
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0508 }
            r6.<init>()     // Catch:{ Exception -> 0x0508 }
            r5.media = r6     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media     // Catch:{ Exception -> 0x0508 }
            int r9 = r6.flags     // Catch:{ Exception -> 0x0508 }
            r12 = 3
            r9 = r9 | r12
            r6.flags = r9     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media     // Catch:{ Exception -> 0x0508 }
            r6.document = r1     // Catch:{ Exception -> 0x0508 }
            boolean r6 = org.telegram.messenger.MessageObject.isVideoDocument(r31)     // Catch:{ Exception -> 0x0508 }
            if (r6 != 0) goto L_0x0134
            if (r30 == 0) goto L_0x0132
            goto L_0x0134
        L_0x0132:
            r15 = 7
            goto L_0x0135
        L_0x0134:
            r15 = 3
        L_0x0135:
            if (r30 == 0) goto L_0x0141
            java.lang.String r6 = r30.getString()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = "ve"
            r4.put(r9, r6)     // Catch:{ Exception -> 0x0508 }
        L_0x0141:
            r5.attachPath = r2     // Catch:{ Exception -> 0x0508 }
            goto L_0x0145
        L_0x0144:
            r15 = -1
        L_0x0145:
            r5.params = r4     // Catch:{ Exception -> 0x0508 }
            r6 = 3
            r5.send_state = r6     // Catch:{ Exception -> 0x0508 }
            r9 = r35
            r12 = r2
            r2 = r15
            r15 = r30
        L_0x0150:
            java.lang.String r6 = r5.attachPath     // Catch:{ Exception -> 0x0508 }
            if (r6 != 0) goto L_0x0158
            java.lang.String r6 = ""
            r5.attachPath = r6     // Catch:{ Exception -> 0x0508 }
        L_0x0158:
            r6 = 0
            r5.local_id = r6     // Catch:{ Exception -> 0x0508 }
            int r6 = r11.type     // Catch:{ Exception -> 0x0508 }
            r29 = r1
            r1 = 3
            if (r6 == r1) goto L_0x0169
            if (r15 != 0) goto L_0x0169
            int r1 = r11.type     // Catch:{ Exception -> 0x0508 }
            r6 = 2
            if (r1 != r6) goto L_0x0174
        L_0x0169:
            java.lang.String r1 = r5.attachPath     // Catch:{ Exception -> 0x0508 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0508 }
            if (r1 != 0) goto L_0x0174
            r1 = 1
            r11.attachPathExists = r1     // Catch:{ Exception -> 0x0508 }
        L_0x0174:
            org.telegram.messenger.VideoEditedInfo r1 = r11.videoEditedInfo     // Catch:{ Exception -> 0x0508 }
            if (r1 == 0) goto L_0x017c
            if (r15 != 0) goto L_0x017c
            org.telegram.messenger.VideoEditedInfo r15 = r11.videoEditedInfo     // Catch:{ Exception -> 0x0508 }
        L_0x017c:
            if (r34 != 0) goto L_0x01ff
            java.lang.CharSequence r6 = r11.editingMessage     // Catch:{ Exception -> 0x0508 }
            if (r6 == 0) goto L_0x01b5
            java.lang.CharSequence r6 = r11.editingMessage     // Catch:{ Exception -> 0x0508 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0508 }
            r5.message = r6     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r11.editingMessageEntities     // Catch:{ Exception -> 0x0508 }
            if (r6 == 0) goto L_0x0194
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r11.editingMessageEntities     // Catch:{ Exception -> 0x0508 }
            r5.entities = r6     // Catch:{ Exception -> 0x0508 }
        L_0x0192:
            r1 = 0
            goto L_0x01b0
        L_0x0194:
            r6 = 1
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r6]     // Catch:{ Exception -> 0x0508 }
            java.lang.CharSequence r6 = r11.editingMessage     // Catch:{ Exception -> 0x0508 }
            r17 = 0
            r1[r17] = r6     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.MediaDataController r6 = r27.getMediaDataController()     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList r1 = r6.getEntities(r1, r13)     // Catch:{ Exception -> 0x0508 }
            if (r1 == 0) goto L_0x0192
            boolean r6 = r1.isEmpty()     // Catch:{ Exception -> 0x0508 }
            if (r6 != 0) goto L_0x0192
            r5.entities = r1     // Catch:{ Exception -> 0x0508 }
            goto L_0x0192
        L_0x01b0:
            r11.caption = r1     // Catch:{ Exception -> 0x0508 }
            r28.generateCaption()     // Catch:{ Exception -> 0x0508 }
        L_0x01b5:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0508 }
            r1.<init>()     // Catch:{ Exception -> 0x0508 }
            r1.add(r5)     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.MessagesStorage r20 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x0508 }
            r22 = 0
            r23 = 1
            r24 = 0
            r25 = 0
            boolean r5 = r11.scheduled     // Catch:{ Exception -> 0x0508 }
            r21 = r1
            r26 = r5
            r20.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC.Message>) r21, (boolean) r22, (boolean) r23, (boolean) r24, (int) r25, (boolean) r26)     // Catch:{ Exception -> 0x0508 }
            r1 = -1
            r11.type = r1     // Catch:{ Exception -> 0x0508 }
            r28.setType()     // Catch:{ Exception -> 0x0508 }
            r28.createMessageSendInfo()     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0508 }
            r1.<init>()     // Catch:{ Exception -> 0x0508 }
            r1.add(r11)     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.NotificationCenter r5 = r27.getNotificationCenter()     // Catch:{ Exception -> 0x0508 }
            int r6 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects     // Catch:{ Exception -> 0x0508 }
            r16 = r13
            r31 = r15
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x0508 }
            java.lang.Long r13 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x0508 }
            r17 = 0
            r15[r17] = r13     // Catch:{ Exception -> 0x0508 }
            r13 = 1
            r15[r13] = r1     // Catch:{ Exception -> 0x0508 }
            r5.postNotificationName(r6, r15)     // Catch:{ Exception -> 0x0508 }
            goto L_0x0203
        L_0x01ff:
            r16 = r13
            r31 = r15
        L_0x0203:
            if (r4 == 0) goto L_0x0213
            boolean r1 = r4.containsKey(r3)     // Catch:{ Exception -> 0x0508 }
            if (r1 == 0) goto L_0x0213
            java.lang.Object r1 = r4.get(r3)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0508 }
            r5 = r1
            goto L_0x0214
        L_0x0213:
            r5 = 0
        L_0x0214:
            r1 = 8
            r3 = 1
            if (r2 < r3) goto L_0x021c
            r3 = 3
            if (r2 <= r3) goto L_0x0221
        L_0x021c:
            r3 = 5
            if (r2 < r3) goto L_0x050f
            if (r2 > r1) goto L_0x050f
        L_0x0221:
            r20 = 0
            r3 = 2
            if (r2 != r3) goto L_0x02db
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x0508 }
            r3.<init>()     // Catch:{ Exception -> 0x0508 }
            if (r4 == 0) goto L_0x0268
            java.lang.String r6 = "masks"
            java.lang.Object r4 = r4.get(r6)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0508 }
            if (r4 == 0) goto L_0x0268
            org.telegram.tgnet.SerializedData r6 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0508 }
            byte[] r4 = org.telegram.messenger.Utilities.hexToBytes(r4)     // Catch:{ Exception -> 0x0508 }
            r6.<init>((byte[]) r4)     // Catch:{ Exception -> 0x0508 }
            r4 = 0
            int r13 = r6.readInt32(r4)     // Catch:{ Exception -> 0x0508 }
            r15 = 0
        L_0x0246:
            if (r15 >= r13) goto L_0x025f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r3.stickers     // Catch:{ Exception -> 0x0508 }
            r29 = r13
            int r13 = r6.readInt32(r4)     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputDocument r13 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r6, r13, r4)     // Catch:{ Exception -> 0x0508 }
            r1.add(r13)     // Catch:{ Exception -> 0x0508 }
            int r15 = r15 + 1
            r13 = r29
            r1 = 8
            r4 = 0
            goto L_0x0246
        L_0x025f:
            int r1 = r3.flags     // Catch:{ Exception -> 0x0508 }
            r4 = 1
            r1 = r1 | r4
            r3.flags = r1     // Catch:{ Exception -> 0x0508 }
            r6.cleanup()     // Catch:{ Exception -> 0x0508 }
        L_0x0268:
            r4 = r2
            long r1 = r0.access_hash     // Catch:{ Exception -> 0x0508 }
            int r6 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1))
            if (r6 != 0) goto L_0x0273
            r2 = r3
            r6 = r14
            r1 = 1
            goto L_0x02a1
        L_0x0273:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x0508 }
            r1.<init>()     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r2 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x0508 }
            r2.<init>()     // Catch:{ Exception -> 0x0508 }
            r1.id = r2     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0508 }
            r6 = r14
            long r13 = r0.id     // Catch:{ Exception -> 0x0508 }
            r2.id = r13     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0508 }
            long r13 = r0.access_hash     // Catch:{ Exception -> 0x0508 }
            r2.access_hash = r13     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0508 }
            byte[] r13 = r0.file_reference     // Catch:{ Exception -> 0x0508 }
            r2.file_reference = r13     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = r2.file_reference     // Catch:{ Exception -> 0x0508 }
            if (r2 != 0) goto L_0x029f
            org.telegram.tgnet.TLRPC$InputPhoto r2 = r1.id     // Catch:{ Exception -> 0x0508 }
            r13 = 0
            byte[] r14 = new byte[r13]     // Catch:{ Exception -> 0x0508 }
            r2.file_reference = r14     // Catch:{ Exception -> 0x0508 }
        L_0x029f:
            r2 = r1
            r1 = 0
        L_0x02a1:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r13 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0508 }
            r13.<init>(r7)     // Catch:{ Exception -> 0x0508 }
            r7 = 0
            r13.type = r7     // Catch:{ Exception -> 0x0508 }
            r13.obj = r11     // Catch:{ Exception -> 0x0508 }
            r13.originalPath = r5     // Catch:{ Exception -> 0x0508 }
            r13.parentObject = r9     // Catch:{ Exception -> 0x0508 }
            r13.inputUploadMedia = r3     // Catch:{ Exception -> 0x0508 }
            r13.performMediaUpload = r1     // Catch:{ Exception -> 0x0508 }
            if (r12 == 0) goto L_0x02c4
            int r3 = r12.length()     // Catch:{ Exception -> 0x0508 }
            if (r3 <= 0) goto L_0x02c4
            boolean r3 = r12.startsWith(r6)     // Catch:{ Exception -> 0x0508 }
            if (r3 == 0) goto L_0x02c4
            r13.httpLocation = r12     // Catch:{ Exception -> 0x0508 }
            goto L_0x02d8
        L_0x02c4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r0.sizes     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes     // Catch:{ Exception -> 0x0508 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0508 }
            r7 = 1
            int r6 = r6 - r7
            java.lang.Object r3 = r3.get(r6)     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3     // Catch:{ Exception -> 0x0508 }
            r13.photoSize = r3     // Catch:{ Exception -> 0x0508 }
            r13.locationParent = r0     // Catch:{ Exception -> 0x0508 }
        L_0x02d8:
            r6 = r1
            goto L_0x03e0
        L_0x02db:
            r4 = r2
            r0 = 3
            if (r4 != r0) goto L_0x036d
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0508 }
            r0.<init>()     // Catch:{ Exception -> 0x0508 }
            r1 = r29
            java.lang.String r2 = r1.mime_type     // Catch:{ Exception -> 0x0508 }
            r0.mime_type = r2     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r1.attributes     // Catch:{ Exception -> 0x0508 }
            r0.attributes = r2     // Catch:{ Exception -> 0x0508 }
            boolean r2 = r28.isGif()     // Catch:{ Exception -> 0x0508 }
            if (r2 != 0) goto L_0x030c
            if (r31 == 0) goto L_0x02fd
            r15 = r31
            boolean r2 = r15.muted     // Catch:{ Exception -> 0x0508 }
            if (r2 != 0) goto L_0x030e
            goto L_0x02ff
        L_0x02fd:
            r15 = r31
        L_0x02ff:
            r2 = 1
            r0.nosound_video = r2     // Catch:{ Exception -> 0x0508 }
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0508 }
            if (r2 == 0) goto L_0x030e
            java.lang.String r2 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0508 }
            goto L_0x030e
        L_0x030c:
            r15 = r31
        L_0x030e:
            long r2 = r1.access_hash     // Catch:{ Exception -> 0x0508 }
            int r6 = (r2 > r20 ? 1 : (r2 == r20 ? 0 : -1))
            if (r6 != 0) goto L_0x0317
            r2 = r0
            r6 = 1
            goto L_0x0343
        L_0x0317:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0508 }
            r2.<init>()     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0508 }
            r3.<init>()     // Catch:{ Exception -> 0x0508 }
            r2.id = r3     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            long r12 = r1.id     // Catch:{ Exception -> 0x0508 }
            r3.id = r12     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            long r12 = r1.access_hash     // Catch:{ Exception -> 0x0508 }
            r3.access_hash = r12     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            byte[] r6 = r1.file_reference     // Catch:{ Exception -> 0x0508 }
            r3.file_reference = r6     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            byte[] r3 = r3.file_reference     // Catch:{ Exception -> 0x0508 }
            if (r3 != 0) goto L_0x0342
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            r6 = 0
            byte[] r12 = new byte[r6]     // Catch:{ Exception -> 0x0508 }
            r3.file_reference = r12     // Catch:{ Exception -> 0x0508 }
        L_0x0342:
            r6 = 0
        L_0x0343:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0508 }
            r3.<init>(r7)     // Catch:{ Exception -> 0x0508 }
            r7 = 1
            r3.type = r7     // Catch:{ Exception -> 0x0508 }
            r3.obj = r11     // Catch:{ Exception -> 0x0508 }
            r3.originalPath = r5     // Catch:{ Exception -> 0x0508 }
            r3.parentObject = r9     // Catch:{ Exception -> 0x0508 }
            r3.inputUploadMedia = r0     // Catch:{ Exception -> 0x0508 }
            r3.performMediaUpload = r6     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0508 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0508 }
            if (r0 != 0) goto L_0x036a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0508 }
            r7 = 0
            java.lang.Object r0 = r0.get(r7)     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC.PhotoSize) r0     // Catch:{ Exception -> 0x0508 }
            r3.photoSize = r0     // Catch:{ Exception -> 0x0508 }
            r3.locationParent = r1     // Catch:{ Exception -> 0x0508 }
        L_0x036a:
            r3.videoEditedInfo = r15     // Catch:{ Exception -> 0x0508 }
            goto L_0x03db
        L_0x036d:
            r1 = r29
            r0 = 7
            if (r4 != r0) goto L_0x03dd
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0508 }
            r0.<init>()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r2 = r1.mime_type     // Catch:{ Exception -> 0x0508 }
            r0.mime_type = r2     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r1.attributes     // Catch:{ Exception -> 0x0508 }
            r0.attributes = r2     // Catch:{ Exception -> 0x0508 }
            long r2 = r1.access_hash     // Catch:{ Exception -> 0x0508 }
            int r6 = (r2 > r20 ? 1 : (r2 == r20 ? 0 : -1))
            if (r6 != 0) goto L_0x0388
            r2 = r0
            r6 = 1
            goto L_0x03b4
        L_0x0388:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0508 }
            r2.<init>()     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0508 }
            r3.<init>()     // Catch:{ Exception -> 0x0508 }
            r2.id = r3     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            long r12 = r1.id     // Catch:{ Exception -> 0x0508 }
            r3.id = r12     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            long r12 = r1.access_hash     // Catch:{ Exception -> 0x0508 }
            r3.access_hash = r12     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            byte[] r6 = r1.file_reference     // Catch:{ Exception -> 0x0508 }
            r3.file_reference = r6     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            byte[] r3 = r3.file_reference     // Catch:{ Exception -> 0x0508 }
            if (r3 != 0) goto L_0x03b3
            org.telegram.tgnet.TLRPC$InputDocument r3 = r2.id     // Catch:{ Exception -> 0x0508 }
            r6 = 0
            byte[] r12 = new byte[r6]     // Catch:{ Exception -> 0x0508 }
            r3.file_reference = r12     // Catch:{ Exception -> 0x0508 }
        L_0x03b3:
            r6 = 0
        L_0x03b4:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0508 }
            r3.<init>(r7)     // Catch:{ Exception -> 0x0508 }
            r3.originalPath = r5     // Catch:{ Exception -> 0x0508 }
            r7 = 2
            r3.type = r7     // Catch:{ Exception -> 0x0508 }
            r3.obj = r11     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x0508 }
            boolean r7 = r7.isEmpty()     // Catch:{ Exception -> 0x0508 }
            if (r7 != 0) goto L_0x03d5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x0508 }
            r8 = 0
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0508 }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC.PhotoSize) r7     // Catch:{ Exception -> 0x0508 }
            r3.photoSize = r7     // Catch:{ Exception -> 0x0508 }
            r3.locationParent = r1     // Catch:{ Exception -> 0x0508 }
        L_0x03d5:
            r3.parentObject = r9     // Catch:{ Exception -> 0x0508 }
            r3.inputUploadMedia = r0     // Catch:{ Exception -> 0x0508 }
            r3.performMediaUpload = r6     // Catch:{ Exception -> 0x0508 }
        L_0x03db:
            r13 = r3
            goto L_0x03e0
        L_0x03dd:
            r2 = 0
            r6 = 0
            r13 = 0
        L_0x03e0:
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r0 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage     // Catch:{ Exception -> 0x0508 }
            r0.<init>()     // Catch:{ Exception -> 0x0508 }
            int r1 = r28.getId()     // Catch:{ Exception -> 0x0508 }
            r0.id = r1     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.MessagesController r1 = r27.getMessagesController()     // Catch:{ Exception -> 0x0508 }
            r3 = r19
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer(r3)     // Catch:{ Exception -> 0x0508 }
            r0.peer = r1     // Catch:{ Exception -> 0x0508 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0508 }
            r1 = r1 | 16384(0x4000, float:2.2959E-41)
            r0.flags = r1     // Catch:{ Exception -> 0x0508 }
            r0.media = r2     // Catch:{ Exception -> 0x0508 }
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0508 }
            if (r1 == 0) goto L_0x0411
            org.telegram.tgnet.TLRPC$Message r1 = r11.messageOwner     // Catch:{ Exception -> 0x0508 }
            int r1 = r1.date     // Catch:{ Exception -> 0x0508 }
            r0.schedule_date = r1     // Catch:{ Exception -> 0x0508 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0508 }
            r2 = 32768(0x8000, float:4.5918E-41)
            r1 = r1 | r2
            r0.flags = r1     // Catch:{ Exception -> 0x0508 }
        L_0x0411:
            java.lang.CharSequence r1 = r11.editingMessage     // Catch:{ Exception -> 0x0508 }
            if (r1 == 0) goto L_0x045c
            java.lang.CharSequence r1 = r11.editingMessage     // Catch:{ Exception -> 0x0508 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0508 }
            r0.message = r1     // Catch:{ Exception -> 0x0508 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0508 }
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r0.flags = r1     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r11.editingMessageEntities     // Catch:{ Exception -> 0x0508 }
            if (r1 == 0) goto L_0x0434
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r11.editingMessageEntities     // Catch:{ Exception -> 0x0508 }
            r0.entities = r1     // Catch:{ Exception -> 0x0508 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0508 }
            r2 = 8
            r1 = r1 | r2
            r0.flags = r1     // Catch:{ Exception -> 0x0508 }
        L_0x0432:
            r1 = 0
            goto L_0x0458
        L_0x0434:
            r1 = 1
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r1]     // Catch:{ Exception -> 0x0508 }
            java.lang.CharSequence r1 = r11.editingMessage     // Catch:{ Exception -> 0x0508 }
            r3 = 0
            r2[r3] = r1     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.MediaDataController r1 = r27.getMediaDataController()     // Catch:{ Exception -> 0x0508 }
            r12 = r16
            java.util.ArrayList r1 = r1.getEntities(r2, r12)     // Catch:{ Exception -> 0x0508 }
            if (r1 == 0) goto L_0x0432
            boolean r2 = r1.isEmpty()     // Catch:{ Exception -> 0x0508 }
            if (r2 != 0) goto L_0x0432
            r0.entities = r1     // Catch:{ Exception -> 0x0508 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0508 }
            r2 = 8
            r1 = r1 | r2
            r0.flags = r1     // Catch:{ Exception -> 0x0508 }
            goto L_0x0432
        L_0x0458:
            r11.editingMessage = r1     // Catch:{ Exception -> 0x0508 }
            r11.editingMessageEntities = r1     // Catch:{ Exception -> 0x0508 }
        L_0x045c:
            if (r13 == 0) goto L_0x0460
            r13.sendRequest = r0     // Catch:{ Exception -> 0x0508 }
        L_0x0460:
            r1 = 1
            if (r4 != r1) goto L_0x0479
            r1 = 0
            boolean r2 = r11.scheduled     // Catch:{ Exception -> 0x0508 }
            r29 = r27
            r30 = r0
            r31 = r28
            r32 = r1
            r33 = r13
            r34 = r9
            r35 = r2
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x0479:
            r1 = 2
            if (r4 != r1) goto L_0x0497
            if (r6 == 0) goto L_0x0483
            r10.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x0483:
            r6 = 0
            r7 = 1
            boolean r12 = r11.scheduled     // Catch:{ Exception -> 0x0508 }
            r1 = r27
            r2 = r0
            r3 = r28
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r13
            r8 = r9
            r9 = r12
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x0497:
            r1 = 3
            if (r4 != r1) goto L_0x04b6
            if (r6 == 0) goto L_0x04a1
            r10.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x04a1:
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0508 }
            r29 = r27
            r30 = r0
            r31 = r28
            r32 = r5
            r33 = r13
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x04b6:
            r1 = 6
            if (r4 != r1) goto L_0x04cd
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0508 }
            r29 = r27
            r30 = r0
            r31 = r28
            r32 = r5
            r33 = r13
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x04cd:
            r1 = 7
            if (r4 != r1) goto L_0x04ea
            if (r6 == 0) goto L_0x04d6
            r10.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x04d6:
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0508 }
            r29 = r27
            r30 = r0
            r31 = r28
            r32 = r5
            r33 = r13
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x04ea:
            r1 = 8
            if (r4 != r1) goto L_0x050f
            if (r6 == 0) goto L_0x04f4
            r10.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x04f4:
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0508 }
            r29 = r27
            r30 = r0
            r31 = r28
            r32 = r5
            r33 = r13
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0508 }
            goto L_0x050f
        L_0x0508:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r27.revertEditingMessageObject(r28)
        L_0x050f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessageMedia(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, BaseFragment baseFragment, ArrayList<TLRPC.MessageEntity> arrayList, int i, Runnable runnable) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return 0;
        }
        TLRPC.TL_messages_editMessage tL_messages_editMessage = new TLRPC.TL_messages_editMessage();
        tL_messages_editMessage.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        if (str != null) {
            tL_messages_editMessage.message = str;
            tL_messages_editMessage.flags |= 2048;
            tL_messages_editMessage.no_webpage = !z;
        }
        tL_messages_editMessage.id = messageObject.getId();
        if (arrayList != null) {
            tL_messages_editMessage.entities = arrayList;
            tL_messages_editMessage.flags |= 8;
        }
        if (i != 0) {
            tL_messages_editMessage.schedule_date = i;
            tL_messages_editMessage.flags |= 32768;
        }
        return getConnectionsManager().sendRequest(tL_messages_editMessage, new RequestDelegate(baseFragment, tL_messages_editMessage, runnable) {
            private final /* synthetic */ BaseFragment f$1;
            private final /* synthetic */ TLRPC.TL_messages_editMessage f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.lambda$editMessage$16$SendMessagesHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$editMessage$16$SendMessagesHelper(BaseFragment baseFragment, TLRPC.TL_messages_editMessage tL_messages_editMessage, Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, baseFragment, tL_messages_editMessage) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC.TL_messages_editMessage f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$15$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
                }
            });
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public /* synthetic */ void lambda$null$15$SendMessagesHelper(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_editMessage tL_messages_editMessage) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editMessage, new Object[0]);
    }

    /* access modifiers changed from: private */
    public void sendLocation(Location location) {
        TLRPC.TL_messageMediaGeo tL_messageMediaGeo = new TLRPC.TL_messageMediaGeo();
        tL_messageMediaGeo.geo = new TLRPC.TL_geoPoint();
        tL_messageMediaGeo.geo.lat = AndroidUtilities.fixLocationCoord(location.getLatitude());
        tL_messageMediaGeo.geo._long = AndroidUtilities.fixLocationCoord(location.getLongitude());
        for (Map.Entry<String, MessageObject> value : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            sendMessage((TLRPC.MessageMedia) tL_messageMediaGeo, messageObject.getDialogId(), messageObject, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton) {
        if (messageObject != null && keyboardButton != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(messageObject.getDialogId());
            sb.append("_");
            sb.append(messageObject.getId());
            sb.append("_");
            sb.append(Utilities.bytesToHex(keyboardButton.data));
            sb.append("_");
            sb.append(keyboardButton instanceof TLRPC.TL_keyboardButtonGame ? "1" : "0");
            this.waitingForLocation.put(sb.toString(), messageObject);
            this.locationProvider.start();
        }
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton) {
        if (messageObject == null || keyboardButton == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(messageObject.getDialogId());
        sb.append("_");
        sb.append(messageObject.getId());
        sb.append("_");
        sb.append(Utilities.bytesToHex(keyboardButton.data));
        sb.append("_");
        sb.append(keyboardButton instanceof TLRPC.TL_keyboardButtonGame ? "1" : "0");
        return this.waitingForLocation.containsKey(sb.toString());
    }

    public void sendNotificationCallback(long j, int i, byte[] bArr) {
        AndroidUtilities.runOnUIThread(new Runnable(j, i, bArr) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ byte[] f$3;

            {
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$sendNotificationCallback$19$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$sendNotificationCallback$19$SendMessagesHelper(long j, int i, byte[] bArr) {
        TLRPC.Chat chatSync;
        TLRPC.User userSync;
        int i2 = i;
        byte[] bArr2 = bArr;
        int i3 = (int) j;
        String str = j + "_" + i + "_" + Utilities.bytesToHex(bArr) + "_" + 0;
        this.waitingForCallback.put(str, true);
        if (i3 <= 0) {
            int i4 = -i3;
            if (getMessagesController().getChat(Integer.valueOf(i4)) == null && (chatSync = getMessagesStorage().getChatSync(i4)) != null) {
                getMessagesController().putChat(chatSync, true);
            }
        } else if (getMessagesController().getUser(Integer.valueOf(i3)) == null && (userSync = getMessagesStorage().getUserSync(i3)) != null) {
            getMessagesController().putUser(userSync, true);
        }
        TLRPC.TL_messages_getBotCallbackAnswer tL_messages_getBotCallbackAnswer = new TLRPC.TL_messages_getBotCallbackAnswer();
        tL_messages_getBotCallbackAnswer.peer = getMessagesController().getInputPeer(i3);
        tL_messages_getBotCallbackAnswer.msg_id = i2;
        tL_messages_getBotCallbackAnswer.game = false;
        if (bArr2 != null) {
            tL_messages_getBotCallbackAnswer.flags |= 1;
            tL_messages_getBotCallbackAnswer.data = bArr2;
        }
        getConnectionsManager().sendRequest(tL_messages_getBotCallbackAnswer, new RequestDelegate(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.lambda$null$18$SendMessagesHelper(this.f$1, tLObject, tL_error);
            }
        }, 2);
        getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, true, 0);
    }

    public /* synthetic */ void lambda$null$17$SendMessagesHelper(String str) {
        Boolean remove = this.waitingForCallback.remove(str);
    }

    public /* synthetic */ void lambda$null$18$SendMessagesHelper(String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$17$SendMessagesHelper(this.f$1);
            }
        });
    }

    public byte[] isSendingVote(MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        return this.waitingForVote.get("poll_" + messageObject.getPollId());
    }

    public int sendVote(MessageObject messageObject, ArrayList<TLRPC.TL_pollAnswer> arrayList, Runnable runnable) {
        byte[] bArr;
        if (messageObject == null) {
            return 0;
        }
        String str = "poll_" + messageObject.getPollId();
        if (this.waitingForCallback.containsKey(str)) {
            return 0;
        }
        TLRPC.TL_messages_sendVote tL_messages_sendVote = new TLRPC.TL_messages_sendVote();
        tL_messages_sendVote.msg_id = messageObject.getId();
        tL_messages_sendVote.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        if (arrayList != null) {
            bArr = new byte[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.TL_pollAnswer tL_pollAnswer = arrayList.get(i);
                tL_messages_sendVote.options.add(tL_pollAnswer.option);
                bArr[i] = tL_pollAnswer.option[0];
            }
        } else {
            bArr = new byte[0];
        }
        this.waitingForVote.put(str, bArr);
        return getConnectionsManager().sendRequest(tL_messages_sendVote, new RequestDelegate(messageObject, str, runnable) {
            private final /* synthetic */ MessageObject f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.lambda$sendVote$21$SendMessagesHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$sendVote$21$SendMessagesHelper(MessageObject messageObject, String str, Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.voteSendTime.put(messageObject.getPollId(), 0L);
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(SystemClock.elapsedRealtime()));
        }
        AndroidUtilities.runOnUIThread(new Runnable(str, runnable) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ Runnable f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$20$SendMessagesHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$20$SendMessagesHelper(String str, Runnable runnable) {
        this.waitingForVote.remove(str);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: protected */
    public long getVoteSendTime(long j) {
        return this.voteSendTime.get(j, 0L).longValue();
    }

    public void sendReaction(MessageObject messageObject, CharSequence charSequence, ChatActivity chatActivity) {
        if (messageObject != null && chatActivity != null) {
            TLRPC.TL_messages_sendReaction tL_messages_sendReaction = new TLRPC.TL_messages_sendReaction();
            tL_messages_sendReaction.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
            tL_messages_sendReaction.msg_id = messageObject.getId();
            if (charSequence != null) {
                tL_messages_sendReaction.reaction = charSequence.toString();
                tL_messages_sendReaction.flags |= 1;
            }
            getConnectionsManager().sendRequest(tL_messages_sendReaction, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$sendReaction$22$SendMessagesHelper(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendReaction$22$SendMessagesHelper(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0082  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendCallback(boolean r18, org.telegram.messenger.MessageObject r19, org.telegram.tgnet.TLRPC.KeyboardButton r20, org.telegram.ui.ChatActivity r21) {
        /*
            r17 = this;
            r8 = r19
            r9 = r20
            if (r8 == 0) goto L_0x0113
            if (r9 == 0) goto L_0x0113
            if (r21 != 0) goto L_0x000c
            goto L_0x0113
        L_0x000c:
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth
            r11 = 1
            r12 = 0
            r13 = 2
            if (r10 == 0) goto L_0x0016
            r0 = 3
        L_0x0014:
            r14 = 0
            goto L_0x0027
        L_0x0016:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame
            if (r0 == 0) goto L_0x001c
            r0 = 1
            goto L_0x0014
        L_0x001c:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy
            if (r0 == 0) goto L_0x0024
            r14 = r18
            r0 = 2
            goto L_0x0027
        L_0x0024:
            r14 = r18
            r0 = 0
        L_0x0027:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            long r2 = r19.getDialogId()
            r1.append(r2)
            java.lang.String r2 = "_"
            r1.append(r2)
            int r3 = r19.getId()
            r1.append(r3)
            r1.append(r2)
            byte[] r3 = r9.data
            java.lang.String r3 = org.telegram.messenger.Utilities.bytesToHex(r3)
            r1.append(r3)
            r1.append(r2)
            r1.append(r0)
            java.lang.String r15 = r1.toString()
            r7 = r17
            java.util.HashMap<java.lang.String, java.lang.Boolean> r0 = r7.waitingForCallback
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r11)
            r0.put(r15, r1)
            org.telegram.tgnet.TLObject[] r6 = new org.telegram.tgnet.TLObject[r11]
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$jARrrSNLwypH2Y4M6FY3LifNwFc r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$jARrrSNLwypH2Y4M6FY3LifNwFc
            r0 = r5
            r1 = r17
            r2 = r15
            r3 = r14
            r4 = r19
            r11 = r5
            r5 = r20
            r16 = r6
            r6 = r21
            r7 = r16
            r0.<init>(r2, r3, r4, r5, r6, r7)
            if (r14 == 0) goto L_0x0082
            org.telegram.messenger.MessagesStorage r0 = r17.getMessagesStorage()
            r0.getBotCache(r15, r11)
            goto L_0x0113
        L_0x0082:
            if (r10 == 0) goto L_0x00ac
            org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth r0 = new org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth
            r0.<init>()
            org.telegram.messenger.MessagesController r1 = r17.getMessagesController()
            long r2 = r19.getDialogId()
            int r3 = (int) r2
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer(r3)
            r0.peer = r1
            int r1 = r19.getId()
            r0.msg_id = r1
            int r1 = r9.button_id
            r0.button_id = r1
            r16[r12] = r0
            org.telegram.tgnet.ConnectionsManager r1 = r17.getConnectionsManager()
            r1.sendRequest(r0, r11, r13)
            goto L_0x0113
        L_0x00ac:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy
            if (r0 == 0) goto L_0x00e2
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.flags
            r0 = r0 & 4
            if (r0 != 0) goto L_0x00cd
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm r0 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm
            r0.<init>()
            int r1 = r19.getId()
            r0.msg_id = r1
            org.telegram.tgnet.ConnectionsManager r1 = r17.getConnectionsManager()
            r1.sendRequest(r0, r11, r13)
            goto L_0x0113
        L_0x00cd:
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentReceipt r0 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentReceipt
            r0.<init>()
            org.telegram.tgnet.TLRPC$Message r1 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            int r1 = r1.receipt_msg_id
            r0.msg_id = r1
            org.telegram.tgnet.ConnectionsManager r1 = r17.getConnectionsManager()
            r1.sendRequest(r0, r11, r13)
            goto L_0x0113
        L_0x00e2:
            org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer r0 = new org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer
            r0.<init>()
            org.telegram.messenger.MessagesController r1 = r17.getMessagesController()
            long r2 = r19.getDialogId()
            int r3 = (int) r2
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer(r3)
            r0.peer = r1
            int r1 = r19.getId()
            r0.msg_id = r1
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame
            r0.game = r1
            byte[] r1 = r9.data
            if (r1 == 0) goto L_0x010c
            int r2 = r0.flags
            r3 = 1
            r2 = r2 | r3
            r0.flags = r2
            r0.data = r1
        L_0x010c:
            org.telegram.tgnet.ConnectionsManager r1 = r17.getConnectionsManager()
            r1.sendRequest(r0, r11, r13)
        L_0x0113:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendCallback(boolean, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity):void");
    }

    public /* synthetic */ void lambda$sendCallback$24$SendMessagesHelper(String str, boolean z, MessageObject messageObject, TLRPC.KeyboardButton keyboardButton, ChatActivity chatActivity, TLObject[] tLObjectArr, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, z, tLObject, messageObject, keyboardButton, chatActivity, tLObjectArr) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ MessageObject f$4;
            private final /* synthetic */ TLRPC.KeyboardButton f$5;
            private final /* synthetic */ ChatActivity f$6;
            private final /* synthetic */ TLObject[] f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$23$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:76:0x015b, code lost:
        if (org.telegram.messenger.MessagesController.getNotificationsSettings(r0.currentAccount).getBoolean("askgame_" + r9, true) != false) goto L_0x015f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$23$SendMessagesHelper(java.lang.String r12, boolean r13, org.telegram.tgnet.TLObject r14, org.telegram.messenger.MessageObject r15, org.telegram.tgnet.TLRPC.KeyboardButton r16, org.telegram.ui.ChatActivity r17, org.telegram.tgnet.TLObject[] r18) {
        /*
            r11 = this;
            r0 = r11
            r1 = r12
            r2 = r14
            r3 = r15
            r4 = r16
            r5 = r17
            java.util.HashMap<java.lang.String, java.lang.Boolean> r6 = r0.waitingForCallback
            r6.remove(r12)
            r6 = 0
            if (r13 == 0) goto L_0x0017
            if (r2 != 0) goto L_0x0017
            r11.sendCallback(r6, r15, r4, r5)
            goto L_0x016f
        L_0x0017:
            if (r2 == 0) goto L_0x016f
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth
            r8 = 1
            if (r7 == 0) goto L_0x004c
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_urlAuthResultRequest
            if (r1 == 0) goto L_0x0030
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest r1 = (org.telegram.tgnet.TLRPC.TL_urlAuthResultRequest) r1
            r2 = r18[r6]
            org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth r2 = (org.telegram.tgnet.TLRPC.TL_messages_requestUrlAuth) r2
            java.lang.String r3 = r4.url
            r5.showRequestUrlAlert(r1, r2, r3)
            goto L_0x016f
        L_0x0030:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_urlAuthResultAccepted
            if (r1 == 0) goto L_0x003e
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted r1 = (org.telegram.tgnet.TLRPC.TL_urlAuthResultAccepted) r1
            java.lang.String r1 = r1.url
            r5.showOpenUrlAlert(r1, r6)
            goto L_0x016f
        L_0x003e:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_urlAuthResultDefault
            if (r1 == 0) goto L_0x016f
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault r1 = (org.telegram.tgnet.TLRPC.TL_urlAuthResultDefault) r1
            java.lang.String r1 = r4.url
            r5.showOpenUrlAlert(r1, r8)
            goto L_0x016f
        L_0x004c:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy
            if (r7 == 0) goto L_0x007a
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_paymentForm
            if (r1 == 0) goto L_0x006a
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = (org.telegram.tgnet.TLRPC.TL_payments_paymentForm) r1
            org.telegram.messenger.MessagesController r2 = r11.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r1.users
            r2.putUsers(r4, r6)
            org.telegram.ui.PaymentFormActivity r2 = new org.telegram.ui.PaymentFormActivity
            r2.<init>((org.telegram.tgnet.TLRPC.TL_payments_paymentForm) r1, (org.telegram.messenger.MessageObject) r15)
            r5.presentFragment(r2)
            goto L_0x016f
        L_0x006a:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt
            if (r1 == 0) goto L_0x016f
            org.telegram.ui.PaymentFormActivity r1 = new org.telegram.ui.PaymentFormActivity
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r2 = (org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt) r2
            r1.<init>((org.telegram.messenger.MessageObject) r15, (org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt) r2)
            r5.presentFragment(r1)
            goto L_0x016f
        L_0x007a:
            org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer r2 = (org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer) r2
            if (r13 != 0) goto L_0x0089
            int r7 = r2.cache_time
            if (r7 == 0) goto L_0x0089
            org.telegram.messenger.MessagesStorage r7 = r11.getMessagesStorage()
            r7.saveBotCache(r12, r2)
        L_0x0089:
            java.lang.String r1 = r2.message
            r7 = 0
            if (r1 == 0) goto L_0x0100
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            int r3 = r1.from_id
            int r1 = r1.via_bot_id
            if (r1 == 0) goto L_0x0097
            goto L_0x0098
        L_0x0097:
            r1 = r3
        L_0x0098:
            if (r1 <= 0) goto L_0x00b1
            org.telegram.messenger.MessagesController r3 = r11.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r3.getUser(r1)
            if (r1 == 0) goto L_0x00c3
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            goto L_0x00c4
        L_0x00b1:
            org.telegram.messenger.MessagesController r3 = r11.getMessagesController()
            int r1 = -r1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            if (r1 == 0) goto L_0x00c3
            java.lang.String r1 = r1.title
            goto L_0x00c4
        L_0x00c3:
            r1 = r7
        L_0x00c4:
            if (r1 != 0) goto L_0x00c8
            java.lang.String r1 = "bot"
        L_0x00c8:
            boolean r3 = r2.alert
            if (r3 == 0) goto L_0x00f9
            android.app.Activity r3 = r17.getParentActivity()
            if (r3 != 0) goto L_0x00d3
            return
        L_0x00d3:
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r17.getParentActivity()
            r3.<init>((android.content.Context) r4)
            r3.setTitle(r1)
            r1 = 2131625882(0x7f0e079a, float:1.8878984E38)
            java.lang.String r4 = "OK"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r3.setPositiveButton(r1, r7)
            java.lang.String r1 = r2.message
            r3.setMessage(r1)
            org.telegram.ui.ActionBar.AlertDialog r1 = r3.create()
            r5.showDialog(r1)
            goto L_0x016f
        L_0x00f9:
            java.lang.String r2 = r2.message
            r5.showAlert(r1, r2)
            goto L_0x016f
        L_0x0100:
            java.lang.String r1 = r2.url
            if (r1 == 0) goto L_0x016f
            android.app.Activity r1 = r17.getParentActivity()
            if (r1 != 0) goto L_0x010b
            return
        L_0x010b:
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            int r9 = r1.from_id
            int r1 = r1.via_bot_id
            if (r1 == 0) goto L_0x0114
            r9 = r1
        L_0x0114:
            org.telegram.messenger.MessagesController r1 = r11.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r10)
            if (r1 == 0) goto L_0x0128
            boolean r1 = r1.verified
            if (r1 == 0) goto L_0x0128
            r1 = 1
            goto L_0x0129
        L_0x0128:
            r1 = 0
        L_0x0129:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame
            if (r4 == 0) goto L_0x016a
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r10 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r10 == 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            goto L_0x0139
        L_0x0138:
            r4 = r7
        L_0x0139:
            if (r4 != 0) goto L_0x013c
            return
        L_0x013c:
            java.lang.String r7 = r2.url
            if (r1 != 0) goto L_0x015e
            int r1 = r0.currentAccount
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getNotificationsSettings(r1)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "askgame_"
            r2.append(r10)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            boolean r1 = r1.getBoolean(r2, r8)
            if (r1 == 0) goto L_0x015e
            goto L_0x015f
        L_0x015e:
            r8 = 0
        L_0x015f:
            r1 = r17
            r2 = r4
            r3 = r15
            r4 = r7
            r5 = r8
            r6 = r9
            r1.showOpenGameAlert(r2, r3, r4, r5, r6)
            goto L_0x016f
        L_0x016a:
            java.lang.String r1 = r2.url
            r5.showOpenUrlAlert(r1, r6)
        L_0x016f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$23$SendMessagesHelper(java.lang.String, boolean, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity, org.telegram.tgnet.TLObject[]):void");
    }

    public boolean isSendingCallback(MessageObject messageObject, TLRPC.KeyboardButton keyboardButton) {
        int i = 0;
        if (messageObject == null || keyboardButton == null) {
            return false;
        }
        if (keyboardButton instanceof TLRPC.TL_keyboardButtonUrlAuth) {
            i = 3;
        } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonGame) {
            i = 1;
        } else if (keyboardButton instanceof TLRPC.TL_keyboardButtonBuy) {
            i = 2;
        }
        return this.waitingForCallback.containsKey(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(keyboardButton.data) + "_" + i);
    }

    public void sendGame(TLRPC.InputPeer inputPeer, TLRPC.TL_inputMediaGame tL_inputMediaGame, long j, long j2) {
        long j3;
        NativeByteBuffer nativeByteBuffer;
        if (inputPeer != null && tL_inputMediaGame != null) {
            TLRPC.TL_messages_sendMedia tL_messages_sendMedia = new TLRPC.TL_messages_sendMedia();
            tL_messages_sendMedia.peer = inputPeer;
            TLRPC.InputPeer inputPeer2 = tL_messages_sendMedia.peer;
            if (inputPeer2 instanceof TLRPC.TL_inputPeerChannel) {
                tL_messages_sendMedia.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + (-inputPeer.channel_id), false);
            } else if (inputPeer2 instanceof TLRPC.TL_inputPeerChat) {
                tL_messages_sendMedia.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + (-inputPeer.chat_id), false);
            } else {
                tL_messages_sendMedia.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + inputPeer.user_id, false);
            }
            if (j != 0) {
                j3 = j;
            } else {
                j3 = getNextRandomId();
            }
            tL_messages_sendMedia.random_id = j3;
            tL_messages_sendMedia.message = "";
            tL_messages_sendMedia.media = tL_inputMediaGame;
            if (j2 == 0) {
                try {
                    nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + tL_inputMediaGame.getObjectSize() + 4 + 8);
                    try {
                        nativeByteBuffer.writeInt32(3);
                        nativeByteBuffer.writeInt64(j);
                        inputPeer.serializeToStream(nativeByteBuffer);
                        tL_inputMediaGame.serializeToStream(nativeByteBuffer);
                    } catch (Exception e) {
                        e = e;
                    }
                } catch (Exception e2) {
                    e = e2;
                    nativeByteBuffer = null;
                    FileLog.e((Throwable) e);
                    j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_messages_sendMedia, new RequestDelegate(j2) {
                        private final /* synthetic */ long f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            SendMessagesHelper.this.lambda$sendGame$25$SendMessagesHelper(this.f$1, tLObject, tL_error);
                        }
                    });
                }
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tL_messages_sendMedia, new RequestDelegate(j2) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$sendGame$25$SendMessagesHelper(this.f$1, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendGame$25$SendMessagesHelper(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void sendMessage(MessageObject messageObject) {
        MessageObject messageObject2 = messageObject;
        long dialogId = messageObject.getDialogId();
        TLRPC.Message message = messageObject2.messageOwner;
        sendMessage((String) null, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, dialogId, message.attachPath, (MessageObject) null, (TLRPC.WebPage) null, true, messageObject, (ArrayList<TLRPC.MessageEntity>) null, message.reply_markup, message.params, !message.silent, messageObject2.scheduled ? message.date : 0, 0, (Object) null);
    }

    public void sendMessage(TLRPC.User user, long j, MessageObject messageObject, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, user, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, j, (String) null, messageObject, (TLRPC.WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC.MessageEntity>) null, replyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC.TL_document tL_document, VideoEditedInfo videoEditedInfo, String str, long j, MessageObject messageObject, String str2, ArrayList<TLRPC.MessageEntity> arrayList, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        String str3 = str2;
        sendMessage((String) null, str3, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, videoEditedInfo, (TLRPC.User) null, tL_document, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, j, str, messageObject, (TLRPC.WebPage) null, true, (MessageObject) null, arrayList, replyMarkup, hashMap, z, i, i2, obj);
    }

    public void sendMessage(String str, long j, MessageObject messageObject, TLRPC.WebPage webPage, boolean z, ArrayList<TLRPC.MessageEntity> arrayList, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z2, int i) {
        sendMessage(str, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, j, (String) null, messageObject, webPage, z, (MessageObject) null, arrayList, replyMarkup, hashMap, z2, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC.MessageMedia messageMedia, long j, MessageObject messageObject, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, messageMedia, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, j, (String) null, messageObject, (TLRPC.WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC.MessageEntity>) null, replyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, long j, MessageObject messageObject, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, tL_messageMediaPoll, j, (String) null, messageObject, (TLRPC.WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC.MessageEntity>) null, replyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC.TL_game tL_game, long j, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC.MessageMedia) null, (TLRPC.TL_photo) null, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, tL_game, (TLRPC.TL_messageMediaPoll) null, j, (String) null, (MessageObject) null, (TLRPC.WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC.MessageEntity>) null, replyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC.TL_photo tL_photo, String str, long j, MessageObject messageObject, String str2, ArrayList<TLRPC.MessageEntity> arrayList, TLRPC.ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        String str3 = str2;
        sendMessage((String) null, str3, (TLRPC.MessageMedia) null, tL_photo, (VideoEditedInfo) null, (TLRPC.User) null, (TLRPC.TL_document) null, (TLRPC.TL_game) null, (TLRPC.TL_messageMediaPoll) null, j, str, messageObject, (TLRPC.WebPage) null, true, (MessageObject) null, arrayList, replyMarkup, hashMap, z, i, i2, obj);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v2, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v6, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v7, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v8, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v9, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v32, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v10, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v11, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v80, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v94, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v12, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v96, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v13, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v14, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v60, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v121, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v123, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v25, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v26, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v73, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v27, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v75, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v90, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v92, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v29, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v30, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r44v31, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v55, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v167, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v169, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v78, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v170, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v80, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v35, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v82, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v83, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v235, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v86, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v87, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v35, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v88, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v89, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v3, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v90, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v4, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v184, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v91, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v102, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v38, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v39, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v103, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v116, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v222, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v208, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v121, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v210, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v211, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v212, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v123, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v222, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v223, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v225, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v40, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v129, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v8, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v56, resolved type: org.telegram.tgnet.TLRPC$TL_userRequest_old2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v138, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v141, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v142, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v143, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v148, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v149, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v150, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v153, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v156, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v157, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v158, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v159, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v160, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v270, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v165, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v167, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v169, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v170, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v171, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v172, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v173, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v174, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v175, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v176, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v177, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v178, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v179, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v180, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v181, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v183, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v185, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v186, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v187, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v189, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v190, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v191, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v193, resolved type: org.telegram.tgnet.TLRPC$TL_message_secret} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v194, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v195, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v196, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v198, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v199, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v200, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v201, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v202, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v204, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v206, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v209, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x02f2, code lost:
        if (r12.containsKey("query_id") != false) goto L_0x02f4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x0397, code lost:
        if (r12.containsKey("query_id") != false) goto L_0x02f4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x04d6, code lost:
        if (org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, false) != false) goto L_0x04d8;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0241 A[Catch:{ Exception -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1092:0x154f A[SYNTHETIC, Splitter:B:1092:0x154f] */
    /* JADX WARNING: Removed duplicated region for block: B:1096:0x1556 A[SYNTHETIC, Splitter:B:1096:0x1556] */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x159b A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1114:0x15a1 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1137:0x1633 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1140:0x164c A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1141:0x165a A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1149:0x1687 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1150:0x1689 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1169:0x16ed A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1172:0x1713 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1175:0x1722 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1176:0x1724 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x026c  */
    /* JADX WARNING: Removed duplicated region for block: B:1180:0x1748 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1193:0x1786 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1195:0x1792 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1201:0x17a8 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1204:0x17b4 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1205:0x17b6 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1208:0x17ca A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17d4 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1265:0x116a A[EDGE_INSN: B:1265:0x116a->B:952:0x116a ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x049e A[Catch:{ Exception -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x04bc A[Catch:{ Exception -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x04c7 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x04cb A[Catch:{ Exception -> 0x0561 }] */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x056b A[SYNTHETIC, Splitter:B:310:0x056b] */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x057d A[Catch:{ Exception -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0580 A[SYNTHETIC, Splitter:B:317:0x0580] */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x058a A[SYNTHETIC, Splitter:B:325:0x058a] */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x059d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x05df A[Catch:{ Exception -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x05f1 A[Catch:{ Exception -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0602 A[Catch:{ Exception -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0623  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x0626  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0691  */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x06c3 A[Catch:{ Exception -> 0x0678 }] */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x06d9 A[SYNTHETIC, Splitter:B:403:0x06d9] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00d2 A[SYNTHETIC, Splitter:B:41:0x00d2] */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x071b  */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x07f4 A[Catch:{ Exception -> 0x0678 }] */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x07f8  */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x0804  */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0806  */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x080f A[SYNTHETIC, Splitter:B:485:0x080f] */
    /* JADX WARNING: Removed duplicated region for block: B:500:0x0831 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x083a A[Catch:{ Exception -> 0x0827 }] */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x086a  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0875 A[SYNTHETIC, Splitter:B:516:0x0875] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00eb A[Catch:{ Exception -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x08cf  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0104 A[Catch:{ Exception -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0922 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x0991 A[Catch:{ Exception -> 0x08f6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:722:0x0CLASSNAME A[SYNTHETIC, Splitter:B:722:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:725:0x0CLASSNAME A[SYNTHETIC, Splitter:B:725:0x0CLASSNAME] */
    /* JADX WARNING: Removed duplicated region for block: B:734:0x0c9c A[Catch:{ Exception -> 0x0ccb }] */
    /* JADX WARNING: Removed duplicated region for block: B:745:0x0cd2  */
    /* JADX WARNING: Removed duplicated region for block: B:790:0x0dc3 A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:803:0x0e05 A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:805:0x0e11 A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:813:0x0e7c A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:823:0x0ebc A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x0ee1 A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x0eeb A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:836:0x0ef2 A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:837:0x0ef7 A[Catch:{ Exception -> 0x0b38 }] */
    /* JADX WARNING: Removed duplicated region for block: B:948:0x1148 A[Catch:{ Exception -> 0x15b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:954:0x117a A[Catch:{ Exception -> 0x15b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:959:0x11bb A[Catch:{ Exception -> 0x15b4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:968:0x11eb  */
    /* JADX WARNING: Removed duplicated region for block: B:969:0x11ed  */
    /* JADX WARNING: Removed duplicated region for block: B:972:0x11f1 A[Catch:{ Exception -> 0x1879 }] */
    /* JADX WARNING: Removed duplicated region for block: B:975:0x1201 A[Catch:{ Exception -> 0x1879 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendMessage(java.lang.String r44, java.lang.String r45, org.telegram.tgnet.TLRPC.MessageMedia r46, org.telegram.tgnet.TLRPC.TL_photo r47, org.telegram.messenger.VideoEditedInfo r48, org.telegram.tgnet.TLRPC.User r49, org.telegram.tgnet.TLRPC.TL_document r50, org.telegram.tgnet.TLRPC.TL_game r51, org.telegram.tgnet.TLRPC.TL_messageMediaPoll r52, long r53, java.lang.String r55, org.telegram.messenger.MessageObject r56, org.telegram.tgnet.TLRPC.WebPage r57, boolean r58, org.telegram.messenger.MessageObject r59, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r60, org.telegram.tgnet.TLRPC.ReplyMarkup r61, java.util.HashMap<java.lang.String, java.lang.String> r62, boolean r63, int r64, int r65, java.lang.Object r66) {
        /*
            r43 = this;
            r1 = r43
            r2 = r44
            r3 = r46
            r4 = r47
            r5 = r49
            r6 = r50
            r7 = r51
            r8 = r52
            r9 = r53
            r11 = r55
            r12 = r56
            r13 = r57
            r14 = r59
            r15 = r60
            r12 = r62
            r6 = r65
            if (r5 == 0) goto L_0x0027
            java.lang.String r7 = r5.phone
            if (r7 != 0) goto L_0x0027
            return
        L_0x0027:
            r16 = 0
            int r7 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r7 != 0) goto L_0x002e
            return
        L_0x002e:
            java.lang.String r7 = ""
            if (r2 != 0) goto L_0x0037
            if (r45 != 0) goto L_0x0037
            r18 = r7
            goto L_0x0039
        L_0x0037:
            r18 = r45
        L_0x0039:
            if (r12 == 0) goto L_0x004c
            java.lang.String r5 = "originalPath"
            boolean r5 = r12.containsKey(r5)
            if (r5 == 0) goto L_0x004c
            java.lang.String r5 = "originalPath"
            java.lang.Object r5 = r12.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            goto L_0x004d
        L_0x004c:
            r5 = 0
        L_0x004d:
            r19 = -1
            r20 = r5
            int r5 = (int) r9
            r21 = 32
            long r3 = r9 >> r21
            int r4 = (int) r3
            if (r5 == 0) goto L_0x0062
            org.telegram.messenger.MessagesController r3 = r43.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer(r5)
            goto L_0x0063
        L_0x0062:
            r3 = 0
        L_0x0063:
            if (r5 != 0) goto L_0x00a7
            org.telegram.messenger.MessagesController r10 = r43.getMessagesController()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r10.getEncryptedChat(r9)
            if (r9 != 0) goto L_0x00a4
            if (r14 == 0) goto L_0x00a3
            org.telegram.messenger.MessagesStorage r2 = r43.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            boolean r4 = r14.scheduled
            r2.markMessageAsSendError(r3, r4)
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            r3 = 2
            r2.send_state = r3
            org.telegram.messenger.NotificationCenter r2 = r43.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.messageSendError
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r5 = r59.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 0
            r4[r6] = r5
            r2.postNotificationName(r3, r4)
            int r2 = r59.getId()
            r1.processSentMessage(r2)
        L_0x00a3:
            return
        L_0x00a4:
            r23 = r4
            goto L_0x00c9
        L_0x00a7:
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel
            if (r9 == 0) goto L_0x00c6
            org.telegram.messenger.MessagesController r9 = r43.getMessagesController()
            int r10 = r3.channel_id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
            if (r9 == 0) goto L_0x00c1
            boolean r9 = r9.megagroup
            if (r9 != 0) goto L_0x00c1
            r10 = 1
            goto L_0x00c2
        L_0x00c1:
            r10 = 0
        L_0x00c2:
            r23 = r4
            r9 = 0
            goto L_0x00ca
        L_0x00c6:
            r23 = r4
            r9 = 0
        L_0x00c9:
            r10 = 0
        L_0x00ca:
            java.lang.String r4 = "query_id"
            java.lang.String r1 = "parentObject"
            r25 = r5
            if (r14 == 0) goto L_0x026c
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0264 }
            if (r66 != 0) goto L_0x00e3
            if (r12 == 0) goto L_0x00e3
            boolean r26 = r12.containsKey(r1)     // Catch:{ Exception -> 0x025f }
            if (r26 == 0) goto L_0x00e3
            java.lang.Object r26 = r12.get(r1)     // Catch:{ Exception -> 0x025f }
            goto L_0x00e5
        L_0x00e3:
            r26 = r66
        L_0x00e5:
            boolean r27 = r59.isForwarded()     // Catch:{ Exception -> 0x025f }
            if (r27 == 0) goto L_0x0104
            r44 = r47
            r27 = r1
            r1 = r2
            r19 = r4
            r2 = r8
            r29 = r10
            r32 = r13
            r51 = r18
            r18 = r26
            r15 = 4
            r8 = r46
            r13 = r49
        L_0x0100:
            r4 = r50
            goto L_0x05d9
        L_0x0104:
            r27 = r1
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            if (r1 == 0) goto L_0x0213
            boolean r1 = r59.isAnimatedEmoji()     // Catch:{ Exception -> 0x025f }
            if (r1 == 0) goto L_0x0112
            goto L_0x0213
        L_0x0112:
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r28 = r3
            r3 = 4
            if (r1 != r3) goto L_0x012a
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            r19 = r50
            r30 = r8
            r29 = r10
            r3 = 2
            r22 = 1
            r8 = r47
            r10 = r49
            goto L_0x0231
        L_0x012a:
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 1
            if (r1 != r3) goto L_0x0145
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$TL_photo r1 = (org.telegram.tgnet.TLRPC.TL_photo) r1     // Catch:{ Exception -> 0x025f }
            r19 = r50
            r30 = r8
            r29 = r10
            r3 = 2
            r22 = 2
            r10 = r49
            r8 = r1
        L_0x0141:
            r1 = r46
            goto L_0x0231
        L_0x0145:
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 3
            if (r1 == r3) goto L_0x0201
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 5
            if (r1 == r3) goto L_0x0201
            if (r48 == 0) goto L_0x0153
            goto L_0x0201
        L_0x0153:
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 12
            if (r1 != r3) goto L_0x0198
            org.telegram.tgnet.TLRPC$TL_userRequest_old2 r1 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2     // Catch:{ Exception -> 0x025f }
            r1.<init>()     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r5.media     // Catch:{ Exception -> 0x025f }
            java.lang.String r3 = r3.phone_number     // Catch:{ Exception -> 0x025f }
            r1.phone = r3     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r5.media     // Catch:{ Exception -> 0x025f }
            java.lang.String r3 = r3.first_name     // Catch:{ Exception -> 0x025f }
            r1.first_name = r3     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r5.media     // Catch:{ Exception -> 0x025f }
            java.lang.String r3 = r3.last_name     // Catch:{ Exception -> 0x025f }
            r1.last_name = r3     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r3 = new org.telegram.tgnet.TLRPC$TL_restrictionReason     // Catch:{ Exception -> 0x025f }
            r3.<init>()     // Catch:{ Exception -> 0x025f }
            r3.platform = r7     // Catch:{ Exception -> 0x025f }
            r3.reason = r7     // Catch:{ Exception -> 0x025f }
            r29 = r10
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r5.media     // Catch:{ Exception -> 0x025f }
            java.lang.String r10 = r10.vcard     // Catch:{ Exception -> 0x025f }
            r3.text = r10     // Catch:{ Exception -> 0x025f }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r10 = r1.restriction_reason     // Catch:{ Exception -> 0x025f }
            r10.add(r3)     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r5.media     // Catch:{ Exception -> 0x025f }
            int r3 = r3.user_id     // Catch:{ Exception -> 0x025f }
            r1.id = r3     // Catch:{ Exception -> 0x025f }
            r19 = 6
            r19 = r50
            r10 = r1
            r30 = r8
            r3 = 2
            r22 = 6
            goto L_0x022d
        L_0x0198:
            r29 = r10
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 8
            if (r1 == r3) goto L_0x01ef
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 9
            if (r1 == r3) goto L_0x01ef
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 13
            if (r1 == r3) goto L_0x01ef
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 14
            if (r1 == r3) goto L_0x01ef
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 15
            if (r1 != r3) goto L_0x01b9
            goto L_0x01ef
        L_0x01b9:
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r3 = 2
            if (r1 != r3) goto L_0x01ce
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$Document r1 = r1.document     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC.TL_document) r1     // Catch:{ Exception -> 0x025f }
            r10 = r49
            r19 = r1
            r30 = r8
            r22 = 8
            goto L_0x022d
        L_0x01ce:
            int r1 = r14.type     // Catch:{ Exception -> 0x025f }
            r10 = 17
            if (r1 != r10) goto L_0x01e4
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r1     // Catch:{ Exception -> 0x025f }
            r8 = r47
            r10 = r49
            r19 = r50
            r30 = r1
            r22 = 10
            goto L_0x0141
        L_0x01e4:
            r1 = r46
            r10 = r49
            r19 = r50
            r30 = r8
            r22 = -1
            goto L_0x022f
        L_0x01ef:
            r3 = 2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$Document r1 = r1.document     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC.TL_document) r1     // Catch:{ Exception -> 0x025f }
            r19 = 7
            r10 = r49
            r19 = r1
            r30 = r8
            r22 = 7
            goto L_0x022d
        L_0x0201:
            r29 = r10
            r3 = 2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$Document r1 = r1.document     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC.TL_document) r1     // Catch:{ Exception -> 0x025f }
            r10 = r49
            r19 = r1
            r30 = r8
            r22 = 3
            goto L_0x022d
        L_0x0213:
            r28 = r3
            r29 = r10
            r3 = 2
            org.telegram.tgnet.TLRPC$Message r1 = r14.messageOwner     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media     // Catch:{ Exception -> 0x025f }
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame     // Catch:{ Exception -> 0x025f }
            if (r1 == 0) goto L_0x0222
            r1 = r2
            goto L_0x0224
        L_0x0222:
            java.lang.String r1 = r5.message     // Catch:{ Exception -> 0x025f }
        L_0x0224:
            r10 = r49
            r19 = r50
            r2 = r1
            r30 = r8
            r22 = 0
        L_0x022d:
            r1 = r46
        L_0x022f:
            r8 = r47
        L_0x0231:
            if (r12 == 0) goto L_0x023b
            boolean r31 = r12.containsKey(r4)     // Catch:{ Exception -> 0x025f }
            if (r31 == 0) goto L_0x023b
            r22 = 9
        L_0x023b:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r5.media     // Catch:{ Exception -> 0x025f }
            int r3 = r3.ttl_seconds     // Catch:{ Exception -> 0x025f }
            if (r3 <= 0) goto L_0x0246
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r5.media     // Catch:{ Exception -> 0x025f }
            int r3 = r3.ttl_seconds     // Catch:{ Exception -> 0x025f }
            r6 = r3
        L_0x0246:
            r44 = r8
            r32 = r13
            r51 = r18
            r15 = r22
            r18 = r26
            r3 = r28
            r8 = r1
            r1 = r2
            r13 = r10
            r2 = r30
            r42 = r19
            r19 = r4
            r4 = r42
            goto L_0x05d9
        L_0x025f:
            r0 = move-exception
        L_0x0260:
            r1 = r64
        L_0x0262:
            r2 = r0
            goto L_0x0269
        L_0x0264:
            r0 = move-exception
            r1 = r64
            r2 = r0
            r5 = 0
        L_0x0269:
            r11 = 0
            goto L_0x1897
        L_0x026c:
            r27 = r1
            r28 = r3
            r29 = r10
            if (r2 == 0) goto L_0x02be
            if (r9 == 0) goto L_0x027c
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0264 }
            r1.<init>()     // Catch:{ Exception -> 0x0264 }
            goto L_0x0281
        L_0x027c:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0264 }
            r1.<init>()     // Catch:{ Exception -> 0x0264 }
        L_0x0281:
            r5 = r1
            if (r9 == 0) goto L_0x0298
            boolean r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending     // Catch:{ Exception -> 0x025f }
            if (r1 == 0) goto L_0x0298
            java.lang.String r1 = r13.url     // Catch:{ Exception -> 0x025f }
            if (r1 == 0) goto L_0x0297
            org.telegram.tgnet.TLRPC$TL_webPageUrlPending r1 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending     // Catch:{ Exception -> 0x025f }
            r1.<init>()     // Catch:{ Exception -> 0x025f }
            java.lang.String r3 = r13.url     // Catch:{ Exception -> 0x025f }
            r1.url = r3     // Catch:{ Exception -> 0x025f }
            r13 = r1
            goto L_0x0298
        L_0x0297:
            r13 = 0
        L_0x0298:
            if (r13 != 0) goto L_0x02a2
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x025f }
            r1.<init>()     // Catch:{ Exception -> 0x025f }
            r5.media = r1     // Catch:{ Exception -> 0x025f }
            goto L_0x02ad
        L_0x02a2:
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x025f }
            r1.<init>()     // Catch:{ Exception -> 0x025f }
            r5.media = r1     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            r1.webpage = r13     // Catch:{ Exception -> 0x025f }
        L_0x02ad:
            if (r12 == 0) goto L_0x02b8
            boolean r1 = r12.containsKey(r4)     // Catch:{ Exception -> 0x025f }
            if (r1 == 0) goto L_0x02b8
            r19 = 9
            goto L_0x02ba
        L_0x02b8:
            r19 = 0
        L_0x02ba:
            r5.message = r2     // Catch:{ Exception -> 0x025f }
            goto L_0x039b
        L_0x02be:
            if (r8 == 0) goto L_0x02d8
            if (r9 == 0) goto L_0x02c8
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0264 }
            r1.<init>()     // Catch:{ Exception -> 0x0264 }
            goto L_0x02cd
        L_0x02c8:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0264 }
            r1.<init>()     // Catch:{ Exception -> 0x0264 }
        L_0x02cd:
            r5 = r1
            r5.media = r8     // Catch:{ Exception -> 0x025f }
            r10 = r49
            r1 = r50
            r19 = 10
            goto L_0x0569
        L_0x02d8:
            r1 = r46
            if (r1 == 0) goto L_0x0304
            if (r9 == 0) goto L_0x02e4
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0264 }
            r3.<init>()     // Catch:{ Exception -> 0x0264 }
            goto L_0x02e9
        L_0x02e4:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0264 }
            r3.<init>()     // Catch:{ Exception -> 0x0264 }
        L_0x02e9:
            r5 = r3
            r5.media = r1     // Catch:{ Exception -> 0x025f }
            if (r12 == 0) goto L_0x02fc
            boolean r3 = r12.containsKey(r4)     // Catch:{ Exception -> 0x025f }
            if (r3 == 0) goto L_0x02fc
        L_0x02f4:
            r10 = r49
            r1 = r50
            r19 = 9
            goto L_0x0569
        L_0x02fc:
            r10 = r49
            r1 = r50
            r19 = 1
            goto L_0x0569
        L_0x0304:
            r3 = r47
            if (r3 == 0) goto L_0x037d
            if (r9 == 0) goto L_0x0310
            org.telegram.tgnet.TLRPC$TL_message_secret r5 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0264 }
            r5.<init>()     // Catch:{ Exception -> 0x0264 }
            goto L_0x0315
        L_0x0310:
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0264 }
            r5.<init>()     // Catch:{ Exception -> 0x0264 }
        L_0x0315:
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r10 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x025f }
            r10.<init>()     // Catch:{ Exception -> 0x025f }
            r5.media = r10     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r5.media     // Catch:{ Exception -> 0x025f }
            int r1 = r10.flags     // Catch:{ Exception -> 0x025f }
            r19 = 3
            r1 = r1 | 3
            r10.flags = r1     // Catch:{ Exception -> 0x025f }
            if (r15 == 0) goto L_0x032a
            r5.entities = r15     // Catch:{ Exception -> 0x025f }
        L_0x032a:
            if (r6 == 0) goto L_0x033c
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            r1.ttl_seconds = r6     // Catch:{ Exception -> 0x025f }
            r5.ttl = r6     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            int r10 = r1.flags     // Catch:{ Exception -> 0x025f }
            r19 = 4
            r10 = r10 | 4
            r1.flags = r10     // Catch:{ Exception -> 0x025f }
        L_0x033c:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r5.media     // Catch:{ Exception -> 0x025f }
            r1.photo = r3     // Catch:{ Exception -> 0x025f }
            if (r12 == 0) goto L_0x034b
            boolean r1 = r12.containsKey(r4)     // Catch:{ Exception -> 0x025f }
            if (r1 == 0) goto L_0x034b
            r19 = 9
            goto L_0x034d
        L_0x034b:
            r19 = 2
        L_0x034d:
            if (r11 == 0) goto L_0x0360
            int r1 = r55.length()     // Catch:{ Exception -> 0x025f }
            if (r1 <= 0) goto L_0x0360
            java.lang.String r1 = "http"
            boolean r1 = r11.startsWith(r1)     // Catch:{ Exception -> 0x025f }
            if (r1 == 0) goto L_0x0360
            r5.attachPath = r11     // Catch:{ Exception -> 0x025f }
            goto L_0x039b
        L_0x0360:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.sizes     // Catch:{ Exception -> 0x025f }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r3.sizes     // Catch:{ Exception -> 0x025f }
            int r10 = r10.size()     // Catch:{ Exception -> 0x025f }
            r2 = 1
            int r10 = r10 - r2
            java.lang.Object r1 = r1.get(r10)     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.location     // Catch:{ Exception -> 0x025f }
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r2)     // Catch:{ Exception -> 0x025f }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x025f }
            r5.attachPath = r1     // Catch:{ Exception -> 0x025f }
            goto L_0x039b
        L_0x037d:
            r1 = r51
            if (r1 == 0) goto L_0x03a1
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0264 }
            r5.<init>()     // Catch:{ Exception -> 0x0264 }
            org.telegram.tgnet.TLRPC$TL_messageMediaGame r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x025f }
            r2.<init>()     // Catch:{ Exception -> 0x025f }
            r5.media = r2     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r5.media     // Catch:{ Exception -> 0x025f }
            r2.game = r1     // Catch:{ Exception -> 0x025f }
            if (r12 == 0) goto L_0x039b
            boolean r1 = r12.containsKey(r4)     // Catch:{ Exception -> 0x025f }
            if (r1 == 0) goto L_0x039b
            goto L_0x02f4
        L_0x039b:
            r10 = r49
        L_0x039d:
            r1 = r50
            goto L_0x0569
        L_0x03a1:
            r10 = r49
            r5 = 0
            if (r10 == 0) goto L_0x043a
            if (r9 == 0) goto L_0x03b5
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x03ae }
            r1.<init>()     // Catch:{ Exception -> 0x03ae }
            goto L_0x03ba
        L_0x03ae:
            r0 = move-exception
            r1 = r64
            r2 = r0
            r11 = r5
            goto L_0x1897
        L_0x03b5:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0264 }
            r1.<init>()     // Catch:{ Exception -> 0x0264 }
        L_0x03ba:
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact     // Catch:{ Exception -> 0x0432 }
            r2.<init>()     // Catch:{ Exception -> 0x0432 }
            r1.media = r2     // Catch:{ Exception -> 0x0432 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            java.lang.String r5 = r10.phone     // Catch:{ Exception -> 0x0432 }
            r2.phone_number = r5     // Catch:{ Exception -> 0x0432 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            java.lang.String r5 = r10.first_name     // Catch:{ Exception -> 0x0432 }
            r2.first_name = r5     // Catch:{ Exception -> 0x0432 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            java.lang.String r5 = r10.last_name     // Catch:{ Exception -> 0x0432 }
            r2.last_name = r5     // Catch:{ Exception -> 0x0432 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            int r5 = r10.id     // Catch:{ Exception -> 0x0432 }
            r2.user_id = r5     // Catch:{ Exception -> 0x0432 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r10.restriction_reason     // Catch:{ Exception -> 0x0432 }
            boolean r2 = r2.isEmpty()     // Catch:{ Exception -> 0x0432 }
            if (r2 != 0) goto L_0x0404
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r10.restriction_reason     // Catch:{ Exception -> 0x0432 }
            r5 = 0
            java.lang.Object r2 = r2.get(r5)     // Catch:{ Exception -> 0x0432 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r2 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r2     // Catch:{ Exception -> 0x0432 }
            java.lang.String r2 = r2.text     // Catch:{ Exception -> 0x0432 }
            java.lang.String r5 = "BEGIN:VCARD"
            boolean r2 = r2.startsWith(r5)     // Catch:{ Exception -> 0x0432 }
            if (r2 == 0) goto L_0x0404
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r5 = r10.restriction_reason     // Catch:{ Exception -> 0x0432 }
            r3 = 0
            java.lang.Object r5 = r5.get(r3)     // Catch:{ Exception -> 0x0432 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r5 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r5     // Catch:{ Exception -> 0x0432 }
            java.lang.String r3 = r5.text     // Catch:{ Exception -> 0x0432 }
            r2.vcard = r3     // Catch:{ Exception -> 0x0432 }
            goto L_0x0408
        L_0x0404:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            r2.vcard = r7     // Catch:{ Exception -> 0x0432 }
        L_0x0408:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            java.lang.String r2 = r2.first_name     // Catch:{ Exception -> 0x0432 }
            if (r2 != 0) goto L_0x0414
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            r2.first_name = r7     // Catch:{ Exception -> 0x0432 }
            r10.first_name = r7     // Catch:{ Exception -> 0x0432 }
        L_0x0414:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            java.lang.String r2 = r2.last_name     // Catch:{ Exception -> 0x0432 }
            if (r2 != 0) goto L_0x0420
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0432 }
            r2.last_name = r7     // Catch:{ Exception -> 0x0432 }
            r10.last_name = r7     // Catch:{ Exception -> 0x0432 }
        L_0x0420:
            if (r12 == 0) goto L_0x042d
            boolean r2 = r12.containsKey(r4)     // Catch:{ Exception -> 0x0432 }
            if (r2 == 0) goto L_0x042d
            r5 = r1
            r19 = 9
            goto L_0x039d
        L_0x042d:
            r19 = 6
            r5 = r1
            goto L_0x039d
        L_0x0432:
            r0 = move-exception
            r2 = r0
            r5 = r1
            r11 = 0
            r1 = r64
            goto L_0x1897
        L_0x043a:
            r1 = r50
            if (r1 == 0) goto L_0x0566
            if (r9 == 0) goto L_0x0446
            org.telegram.tgnet.TLRPC$TL_message_secret r2 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0264 }
            r2.<init>()     // Catch:{ Exception -> 0x0264 }
            goto L_0x044b
        L_0x0446:
            org.telegram.tgnet.TLRPC$TL_message r2 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0264 }
            r2.<init>()     // Catch:{ Exception -> 0x0264 }
        L_0x044b:
            r5 = r2
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0561 }
            r2.<init>()     // Catch:{ Exception -> 0x0561 }
            r5.media = r2     // Catch:{ Exception -> 0x0561 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r5.media     // Catch:{ Exception -> 0x0561 }
            int r3 = r2.flags     // Catch:{ Exception -> 0x0561 }
            r19 = 3
            r3 = r3 | 3
            r2.flags = r3     // Catch:{ Exception -> 0x0561 }
            if (r6 == 0) goto L_0x046f
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r5.media     // Catch:{ Exception -> 0x025f }
            r2.ttl_seconds = r6     // Catch:{ Exception -> 0x025f }
            r5.ttl = r6     // Catch:{ Exception -> 0x025f }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r5.media     // Catch:{ Exception -> 0x025f }
            int r3 = r2.flags     // Catch:{ Exception -> 0x025f }
            r19 = 4
            r3 = r3 | 4
            r2.flags = r3     // Catch:{ Exception -> 0x025f }
        L_0x046f:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r5.media     // Catch:{ Exception -> 0x0561 }
            r2.document = r1     // Catch:{ Exception -> 0x0561 }
            if (r12 == 0) goto L_0x047e
            boolean r2 = r12.containsKey(r4)     // Catch:{ Exception -> 0x025f }
            if (r2 == 0) goto L_0x047e
            r19 = 9
            goto L_0x049c
        L_0x047e:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r50)     // Catch:{ Exception -> 0x0561 }
            if (r2 != 0) goto L_0x049a
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r50)     // Catch:{ Exception -> 0x025f }
            if (r2 != 0) goto L_0x049a
            if (r48 == 0) goto L_0x048d
            goto L_0x049a
        L_0x048d:
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceDocument(r50)     // Catch:{ Exception -> 0x025f }
            if (r2 == 0) goto L_0x0496
            r19 = 8
            goto L_0x049c
        L_0x0496:
            r2 = 7
            r19 = 7
            goto L_0x049c
        L_0x049a:
            r19 = 3
        L_0x049c:
            if (r48 == 0) goto L_0x04b0
            java.lang.String r2 = r48.getString()     // Catch:{ Exception -> 0x025f }
            if (r12 != 0) goto L_0x04aa
            java.util.HashMap r3 = new java.util.HashMap     // Catch:{ Exception -> 0x025f }
            r3.<init>()     // Catch:{ Exception -> 0x025f }
            r12 = r3
        L_0x04aa:
            java.lang.String r3 = "ve"
            r12.put(r3, r2)     // Catch:{ Exception -> 0x025f }
        L_0x04b0:
            if (r9 == 0) goto L_0x04c7
            int r2 = r1.dc_id     // Catch:{ Exception -> 0x025f }
            if (r2 <= 0) goto L_0x04c7
            boolean r2 = org.telegram.messenger.MessageObject.isStickerDocument(r50)     // Catch:{ Exception -> 0x025f }
            if (r2 != 0) goto L_0x04c7
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r50)     // Catch:{ Exception -> 0x025f }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x025f }
            r5.attachPath = r2     // Catch:{ Exception -> 0x025f }
            goto L_0x04c9
        L_0x04c7:
            r5.attachPath = r11     // Catch:{ Exception -> 0x0561 }
        L_0x04c9:
            if (r9 == 0) goto L_0x0556
            boolean r2 = org.telegram.messenger.MessageObject.isStickerDocument(r50)     // Catch:{ Exception -> 0x0561 }
            if (r2 != 0) goto L_0x04d8
            r2 = 0
            boolean r3 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r2)     // Catch:{ Exception -> 0x025f }
            if (r3 == 0) goto L_0x0556
        L_0x04d8:
            r2 = 0
        L_0x04d9:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0561 }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0561 }
            if (r2 >= r3) goto L_0x0556
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0561 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0561 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3     // Catch:{ Exception -> 0x0561 }
            r22 = r5
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker     // Catch:{ Exception -> 0x054e }
            if (r5 == 0) goto L_0x0545
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r1.attributes     // Catch:{ Exception -> 0x054e }
            r5.remove(r2)     // Catch:{ Exception -> 0x054e }
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55 r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55     // Catch:{ Exception -> 0x054e }
            r2.<init>()     // Catch:{ Exception -> 0x054e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r1.attributes     // Catch:{ Exception -> 0x054e }
            r5.add(r2)     // Catch:{ Exception -> 0x054e }
            java.lang.String r5 = r3.alt     // Catch:{ Exception -> 0x054e }
            r2.alt = r5     // Catch:{ Exception -> 0x054e }
            org.telegram.tgnet.TLRPC$InputStickerSet r5 = r3.stickerset     // Catch:{ Exception -> 0x054e }
            if (r5 == 0) goto L_0x053b
            org.telegram.tgnet.TLRPC$InputStickerSet r5 = r3.stickerset     // Catch:{ Exception -> 0x054e }
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName     // Catch:{ Exception -> 0x054e }
            if (r5 == 0) goto L_0x0513
            org.telegram.tgnet.TLRPC$InputStickerSet r3 = r3.stickerset     // Catch:{ Exception -> 0x054e }
            java.lang.String r3 = r3.short_name     // Catch:{ Exception -> 0x054e }
            r51 = r12
            goto L_0x0521
        L_0x0513:
            org.telegram.messenger.MediaDataController r5 = r43.getMediaDataController()     // Catch:{ Exception -> 0x054e }
            org.telegram.tgnet.TLRPC$InputStickerSet r3 = r3.stickerset     // Catch:{ Exception -> 0x054e }
            r51 = r12
            long r12 = r3.id     // Catch:{ Exception -> 0x054e }
            java.lang.String r3 = r5.getStickerSetName(r12)     // Catch:{ Exception -> 0x054e }
        L_0x0521:
            boolean r5 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x054e }
            if (r5 != 0) goto L_0x0533
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r5 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x054e }
            r5.<init>()     // Catch:{ Exception -> 0x054e }
            r2.stickerset = r5     // Catch:{ Exception -> 0x054e }
            org.telegram.tgnet.TLRPC$InputStickerSet r2 = r2.stickerset     // Catch:{ Exception -> 0x054e }
            r2.short_name = r3     // Catch:{ Exception -> 0x054e }
            goto L_0x055a
        L_0x0533:
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x054e }
            r3.<init>()     // Catch:{ Exception -> 0x054e }
            r2.stickerset = r3     // Catch:{ Exception -> 0x054e }
            goto L_0x055a
        L_0x053b:
            r51 = r12
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x054e }
            r3.<init>()     // Catch:{ Exception -> 0x054e }
            r2.stickerset = r3     // Catch:{ Exception -> 0x054e }
            goto L_0x055a
        L_0x0545:
            r51 = r12
            int r2 = r2 + 1
            r13 = r57
            r5 = r22
            goto L_0x04d9
        L_0x054e:
            r0 = move-exception
            r1 = r64
            r2 = r0
            r5 = r22
            goto L_0x0269
        L_0x0556:
            r22 = r5
            r51 = r12
        L_0x055a:
            r12 = r51
            r13 = r57
            r5 = r22
            goto L_0x0569
        L_0x0561:
            r0 = move-exception
            r22 = r5
            goto L_0x0260
        L_0x0566:
            r13 = r57
            r5 = 0
        L_0x0569:
            if (r15 == 0) goto L_0x0579
            boolean r2 = r60.isEmpty()     // Catch:{ Exception -> 0x025f }
            if (r2 != 0) goto L_0x0579
            r5.entities = r15     // Catch:{ Exception -> 0x025f }
            int r2 = r5.flags     // Catch:{ Exception -> 0x025f }
            r2 = r2 | 128(0x80, float:1.794E-43)
            r5.flags = r2     // Catch:{ Exception -> 0x025f }
        L_0x0579:
            r2 = r18
            if (r2 == 0) goto L_0x0580
            r5.message = r2     // Catch:{ Exception -> 0x025f }
            goto L_0x0586
        L_0x0580:
            java.lang.String r3 = r5.message     // Catch:{ Exception -> 0x1890 }
            if (r3 != 0) goto L_0x0586
            r5.message = r7     // Catch:{ Exception -> 0x025f }
        L_0x0586:
            java.lang.String r3 = r5.attachPath     // Catch:{ Exception -> 0x1890 }
            if (r3 != 0) goto L_0x058c
            r5.attachPath = r7     // Catch:{ Exception -> 0x025f }
        L_0x058c:
            org.telegram.messenger.UserConfig r3 = r43.getUserConfig()     // Catch:{ Exception -> 0x1890 }
            int r3 = r3.getNewMessageId()     // Catch:{ Exception -> 0x1890 }
            r5.id = r3     // Catch:{ Exception -> 0x1890 }
            r5.local_id = r3     // Catch:{ Exception -> 0x1890 }
            r3 = 1
            r5.out = r3     // Catch:{ Exception -> 0x1890 }
            if (r29 == 0) goto L_0x05a7
            if (r28 == 0) goto L_0x05a7
            r3 = r28
            int r1 = r3.channel_id     // Catch:{ Exception -> 0x025f }
            int r1 = -r1
            r5.from_id = r1     // Catch:{ Exception -> 0x025f }
            goto L_0x05b9
        L_0x05a7:
            r3 = r28
            org.telegram.messenger.UserConfig r1 = r43.getUserConfig()     // Catch:{ Exception -> 0x1890 }
            int r1 = r1.getClientUserId()     // Catch:{ Exception -> 0x1890 }
            r5.from_id = r1     // Catch:{ Exception -> 0x1890 }
            int r1 = r5.flags     // Catch:{ Exception -> 0x1890 }
            r1 = r1 | 256(0x100, float:3.59E-43)
            r5.flags = r1     // Catch:{ Exception -> 0x1890 }
        L_0x05b9:
            org.telegram.messenger.UserConfig r1 = r43.getUserConfig()     // Catch:{ Exception -> 0x1890 }
            r18 = r5
            r5 = 0
            r1.saveConfig(r5)     // Catch:{ Exception -> 0x1888 }
            r1 = r44
            r44 = r47
            r51 = r2
            r2 = r8
            r32 = r13
            r5 = r18
            r15 = r19
            r8 = r46
            r18 = r66
            r19 = r4
            r13 = r10
            goto L_0x0100
        L_0x05d9:
            long r10 = r5.random_id     // Catch:{ Exception -> 0x025f }
            int r22 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r22 != 0) goto L_0x05e5
            long r10 = r43.getNextRandomId()     // Catch:{ Exception -> 0x025f }
            r5.random_id = r10     // Catch:{ Exception -> 0x025f }
        L_0x05e5:
            if (r12 == 0) goto L_0x061a
            java.lang.String r10 = "bot"
            boolean r10 = r12.containsKey(r10)     // Catch:{ Exception -> 0x025f }
            if (r10 == 0) goto L_0x061a
            if (r9 == 0) goto L_0x0602
            java.lang.String r10 = "bot_name"
            java.lang.Object r10 = r12.get(r10)     // Catch:{ Exception -> 0x025f }
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ Exception -> 0x025f }
            r5.via_bot_name = r10     // Catch:{ Exception -> 0x025f }
            java.lang.String r10 = r5.via_bot_name     // Catch:{ Exception -> 0x025f }
            if (r10 != 0) goto L_0x0614
            r5.via_bot_name = r7     // Catch:{ Exception -> 0x025f }
            goto L_0x0614
        L_0x0602:
            java.lang.String r10 = "bot"
            java.lang.Object r10 = r12.get(r10)     // Catch:{ Exception -> 0x025f }
            java.lang.CharSequence r10 = (java.lang.CharSequence) r10     // Catch:{ Exception -> 0x025f }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt(r10)     // Catch:{ Exception -> 0x025f }
            int r10 = r10.intValue()     // Catch:{ Exception -> 0x025f }
            r5.via_bot_id = r10     // Catch:{ Exception -> 0x025f }
        L_0x0614:
            int r10 = r5.flags     // Catch:{ Exception -> 0x025f }
            r10 = r10 | 2048(0x800, float:2.87E-42)
            r5.flags = r10     // Catch:{ Exception -> 0x025f }
        L_0x061a:
            r5.params = r12     // Catch:{ Exception -> 0x025f }
            if (r14 == 0) goto L_0x0626
            boolean r10 = r14.resendAsIs     // Catch:{ Exception -> 0x025f }
            if (r10 != 0) goto L_0x0623
            goto L_0x0626
        L_0x0623:
            r10 = r64
            goto L_0x0680
        L_0x0626:
            r10 = r64
            if (r10 == 0) goto L_0x062c
            r11 = r10
            goto L_0x0634
        L_0x062c:
            org.telegram.tgnet.ConnectionsManager r11 = r43.getConnectionsManager()     // Catch:{ Exception -> 0x1884 }
            int r11 = r11.getCurrentTime()     // Catch:{ Exception -> 0x1884 }
        L_0x0634:
            r5.date = r11     // Catch:{ Exception -> 0x1884 }
            boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel     // Catch:{ Exception -> 0x1884 }
            if (r11 == 0) goto L_0x067d
            if (r10 != 0) goto L_0x0647
            if (r29 == 0) goto L_0x0647
            r11 = 1
            r5.views = r11     // Catch:{ Exception -> 0x0678 }
            int r11 = r5.flags     // Catch:{ Exception -> 0x0678 }
            r11 = r11 | 1024(0x400, float:1.435E-42)
            r5.flags = r11     // Catch:{ Exception -> 0x0678 }
        L_0x0647:
            org.telegram.messenger.MessagesController r11 = r43.getMessagesController()     // Catch:{ Exception -> 0x0678 }
            int r14 = r3.channel_id     // Catch:{ Exception -> 0x0678 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0678 }
            org.telegram.tgnet.TLRPC$Chat r11 = r11.getChat(r14)     // Catch:{ Exception -> 0x0678 }
            if (r11 == 0) goto L_0x0680
            boolean r14 = r11.megagroup     // Catch:{ Exception -> 0x0678 }
            if (r14 == 0) goto L_0x0666
            int r11 = r5.flags     // Catch:{ Exception -> 0x0678 }
            r14 = -2147483648(0xfffffffvar_, float:-0.0)
            r11 = r11 | r14
            r5.flags = r11     // Catch:{ Exception -> 0x0678 }
            r14 = 1
            r5.unread = r14     // Catch:{ Exception -> 0x0678 }
            goto L_0x0680
        L_0x0666:
            r14 = 1
            r5.post = r14     // Catch:{ Exception -> 0x0678 }
            boolean r11 = r11.signatures     // Catch:{ Exception -> 0x0678 }
            if (r11 == 0) goto L_0x0680
            org.telegram.messenger.UserConfig r11 = r43.getUserConfig()     // Catch:{ Exception -> 0x0678 }
            int r11 = r11.getClientUserId()     // Catch:{ Exception -> 0x0678 }
            r5.from_id = r11     // Catch:{ Exception -> 0x0678 }
            goto L_0x0680
        L_0x0678:
            r0 = move-exception
        L_0x0679:
            r2 = r0
            r1 = r10
            goto L_0x0269
        L_0x067d:
            r11 = 1
            r5.unread = r11     // Catch:{ Exception -> 0x1884 }
        L_0x0680:
            int r11 = r5.flags     // Catch:{ Exception -> 0x1884 }
            r11 = r11 | 512(0x200, float:7.175E-43)
            r5.flags = r11     // Catch:{ Exception -> 0x1884 }
            r46 = r13
            r11 = 2
            r13 = r53
            r5.dialog_id = r13     // Catch:{ Exception -> 0x1884 }
            r11 = r56
            if (r11 == 0) goto L_0x06c3
            if (r9 == 0) goto L_0x06af
            r26 = r2
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner     // Catch:{ Exception -> 0x0678 }
            r28 = r7
            r29 = r8
            long r7 = r2.random_id     // Catch:{ Exception -> 0x0678 }
            int r2 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x06b5
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner     // Catch:{ Exception -> 0x0678 }
            long r7 = r2.random_id     // Catch:{ Exception -> 0x0678 }
            r5.reply_to_random_id = r7     // Catch:{ Exception -> 0x0678 }
            int r2 = r5.flags     // Catch:{ Exception -> 0x0678 }
            r7 = 8
            r2 = r2 | r7
            r5.flags = r2     // Catch:{ Exception -> 0x0678 }
            goto L_0x06bc
        L_0x06af:
            r26 = r2
            r28 = r7
            r29 = r8
        L_0x06b5:
            int r2 = r5.flags     // Catch:{ Exception -> 0x0678 }
            r7 = 8
            r2 = r2 | r7
            r5.flags = r2     // Catch:{ Exception -> 0x0678 }
        L_0x06bc:
            int r2 = r56.getId()     // Catch:{ Exception -> 0x0678 }
            r5.reply_to_msg_id = r2     // Catch:{ Exception -> 0x0678 }
            goto L_0x06c9
        L_0x06c3:
            r26 = r2
            r28 = r7
            r29 = r8
        L_0x06c9:
            r2 = r61
            if (r2 == 0) goto L_0x06d7
            if (r9 != 0) goto L_0x06d7
            int r7 = r5.flags     // Catch:{ Exception -> 0x0678 }
            r7 = r7 | 64
            r5.flags = r7     // Catch:{ Exception -> 0x0678 }
            r5.reply_markup = r2     // Catch:{ Exception -> 0x0678 }
        L_0x06d7:
            if (r25 == 0) goto L_0x071b
            org.telegram.messenger.MessagesController r2 = r43.getMessagesController()     // Catch:{ Exception -> 0x0716 }
            r7 = r25
            org.telegram.tgnet.TLRPC$Peer r2 = r2.getPeer(r7)     // Catch:{ Exception -> 0x0716 }
            r5.to_id = r2     // Catch:{ Exception -> 0x0716 }
            if (r7 <= 0) goto L_0x070d
            org.telegram.messenger.MessagesController r2 = r43.getMessagesController()     // Catch:{ Exception -> 0x0716 }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ Exception -> 0x0716 }
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r7)     // Catch:{ Exception -> 0x0716 }
            if (r2 != 0) goto L_0x06fd
            int r1 = r5.id     // Catch:{ Exception -> 0x0716 }
            r7 = r43
            r7.processSentMessage(r1)     // Catch:{ Exception -> 0x0678 }
            return
        L_0x06fd:
            r7 = r43
            r8 = r27
            boolean r2 = r2.bot     // Catch:{ Exception -> 0x0678 }
            if (r2 == 0) goto L_0x0708
            r2 = 0
            r5.unread = r2     // Catch:{ Exception -> 0x0678 }
        L_0x0708:
            r47 = r6
            r27 = r8
            goto L_0x0711
        L_0x070d:
            r7 = r43
        L_0x070f:
            r47 = r6
        L_0x0711:
            r2 = r23
            r6 = 1
            goto L_0x07e6
        L_0x0716:
            r0 = move-exception
            r7 = r43
            goto L_0x0679
        L_0x071b:
            r7 = r43
            r8 = r27
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x1884 }
            r2.<init>()     // Catch:{ Exception -> 0x1884 }
            r5.to_id = r2     // Catch:{ Exception -> 0x1884 }
            int r2 = r9.participant_id     // Catch:{ Exception -> 0x1884 }
            org.telegram.messenger.UserConfig r25 = r43.getUserConfig()     // Catch:{ Exception -> 0x1884 }
            r27 = r8
            int r8 = r25.getClientUserId()     // Catch:{ Exception -> 0x1884 }
            if (r2 != r8) goto L_0x073b
            org.telegram.tgnet.TLRPC$Peer r2 = r5.to_id     // Catch:{ Exception -> 0x0678 }
            int r8 = r9.admin_id     // Catch:{ Exception -> 0x0678 }
            r2.user_id = r8     // Catch:{ Exception -> 0x0678 }
            goto L_0x0741
        L_0x073b:
            org.telegram.tgnet.TLRPC$Peer r2 = r5.to_id     // Catch:{ Exception -> 0x1884 }
            int r8 = r9.participant_id     // Catch:{ Exception -> 0x1884 }
            r2.user_id = r8     // Catch:{ Exception -> 0x1884 }
        L_0x0741:
            if (r6 == 0) goto L_0x0746
            r5.ttl = r6     // Catch:{ Exception -> 0x0678 }
            goto L_0x0762
        L_0x0746:
            int r2 = r9.ttl     // Catch:{ Exception -> 0x1884 }
            r5.ttl = r2     // Catch:{ Exception -> 0x1884 }
            int r2 = r5.ttl     // Catch:{ Exception -> 0x1884 }
            if (r2 == 0) goto L_0x0762
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r5.media     // Catch:{ Exception -> 0x0678 }
            if (r2 == 0) goto L_0x0762
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r5.media     // Catch:{ Exception -> 0x0678 }
            int r8 = r5.ttl     // Catch:{ Exception -> 0x0678 }
            r2.ttl_seconds = r8     // Catch:{ Exception -> 0x0678 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r5.media     // Catch:{ Exception -> 0x0678 }
            int r8 = r2.flags     // Catch:{ Exception -> 0x0678 }
            r24 = 4
            r8 = r8 | 4
            r2.flags = r8     // Catch:{ Exception -> 0x0678 }
        L_0x0762:
            int r2 = r5.ttl     // Catch:{ Exception -> 0x1884 }
            if (r2 == 0) goto L_0x070f
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r5.media     // Catch:{ Exception -> 0x0678 }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x0678 }
            if (r2 == 0) goto L_0x070f
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r5)     // Catch:{ Exception -> 0x0678 }
            if (r2 == 0) goto L_0x07a8
            r2 = 0
        L_0x0773:
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r5.media     // Catch:{ Exception -> 0x0678 }
            org.telegram.tgnet.TLRPC$Document r8 = r8.document     // Catch:{ Exception -> 0x0678 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r8.attributes     // Catch:{ Exception -> 0x0678 }
            int r8 = r8.size()     // Catch:{ Exception -> 0x0678 }
            if (r2 >= r8) goto L_0x0799
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r5.media     // Catch:{ Exception -> 0x0678 }
            org.telegram.tgnet.TLRPC$Document r8 = r8.document     // Catch:{ Exception -> 0x0678 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r8.attributes     // Catch:{ Exception -> 0x0678 }
            java.lang.Object r8 = r8.get(r2)     // Catch:{ Exception -> 0x0678 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8     // Catch:{ Exception -> 0x0678 }
            r47 = r6
            boolean r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio     // Catch:{ Exception -> 0x0678 }
            if (r6 == 0) goto L_0x0794
            int r2 = r8.duration     // Catch:{ Exception -> 0x0678 }
            goto L_0x079c
        L_0x0794:
            int r2 = r2 + 1
            r6 = r47
            goto L_0x0773
        L_0x0799:
            r47 = r6
            r2 = 0
        L_0x079c:
            int r6 = r5.ttl     // Catch:{ Exception -> 0x0678 }
            r8 = 1
            int r2 = r2 + r8
            int r2 = java.lang.Math.max(r6, r2)     // Catch:{ Exception -> 0x0678 }
            r5.ttl = r2     // Catch:{ Exception -> 0x0678 }
            goto L_0x0711
        L_0x07a8:
            r47 = r6
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r5)     // Catch:{ Exception -> 0x0678 }
            if (r2 != 0) goto L_0x07b6
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r5)     // Catch:{ Exception -> 0x0678 }
            if (r2 == 0) goto L_0x0711
        L_0x07b6:
            r2 = 0
        L_0x07b7:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media     // Catch:{ Exception -> 0x0678 }
            org.telegram.tgnet.TLRPC$Document r6 = r6.document     // Catch:{ Exception -> 0x0678 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r6.attributes     // Catch:{ Exception -> 0x0678 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0678 }
            if (r2 >= r6) goto L_0x07d9
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media     // Catch:{ Exception -> 0x0678 }
            org.telegram.tgnet.TLRPC$Document r6 = r6.document     // Catch:{ Exception -> 0x0678 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r6.attributes     // Catch:{ Exception -> 0x0678 }
            java.lang.Object r6 = r6.get(r2)     // Catch:{ Exception -> 0x0678 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r6 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r6     // Catch:{ Exception -> 0x0678 }
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo     // Catch:{ Exception -> 0x0678 }
            if (r8 == 0) goto L_0x07d6
            int r2 = r6.duration     // Catch:{ Exception -> 0x0678 }
            goto L_0x07da
        L_0x07d6:
            int r2 = r2 + 1
            goto L_0x07b7
        L_0x07d9:
            r2 = 0
        L_0x07da:
            int r6 = r5.ttl     // Catch:{ Exception -> 0x0678 }
            r8 = 1
            int r2 = r2 + r8
            int r2 = java.lang.Math.max(r6, r2)     // Catch:{ Exception -> 0x0678 }
            r5.ttl = r2     // Catch:{ Exception -> 0x0678 }
            goto L_0x0711
        L_0x07e6:
            if (r2 == r6) goto L_0x07f8
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r5)     // Catch:{ Exception -> 0x0678 }
            if (r2 != 0) goto L_0x07f4
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r5)     // Catch:{ Exception -> 0x0678 }
            if (r2 == 0) goto L_0x07f8
        L_0x07f4:
            r2 = 1
            r5.media_unread = r2     // Catch:{ Exception -> 0x0678 }
            goto L_0x07f9
        L_0x07f8:
            r2 = 1
        L_0x07f9:
            r5.send_state = r2     // Catch:{ Exception -> 0x1884 }
            org.telegram.messenger.MessageObject r6 = new org.telegram.messenger.MessageObject     // Catch:{ Exception -> 0x1884 }
            int r8 = r7.currentAccount     // Catch:{ Exception -> 0x1884 }
            r6.<init>((int) r8, (org.telegram.tgnet.TLRPC.Message) r5, (org.telegram.messenger.MessageObject) r11, (boolean) r2)     // Catch:{ Exception -> 0x1884 }
            if (r10 == 0) goto L_0x0806
            r2 = 1
            goto L_0x0807
        L_0x0806:
            r2 = 0
        L_0x0807:
            r6.scheduled = r2     // Catch:{ Exception -> 0x187f }
            boolean r2 = r6.isForwarded()     // Catch:{ Exception -> 0x187f }
            if (r2 != 0) goto L_0x082d
            int r2 = r6.type     // Catch:{ Exception -> 0x0827 }
            r8 = 3
            if (r2 == r8) goto L_0x081b
            if (r48 != 0) goto L_0x081b
            int r2 = r6.type     // Catch:{ Exception -> 0x0827 }
            r8 = 2
            if (r2 != r8) goto L_0x082d
        L_0x081b:
            java.lang.String r2 = r5.attachPath     // Catch:{ Exception -> 0x0827 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0827 }
            if (r2 != 0) goto L_0x082d
            r2 = 1
            r6.attachPathExists = r2     // Catch:{ Exception -> 0x0827 }
            goto L_0x082d
        L_0x0827:
            r0 = move-exception
            r2 = r0
            r11 = r6
        L_0x082a:
            r1 = r10
            goto L_0x1897
        L_0x082d:
            org.telegram.messenger.VideoEditedInfo r2 = r6.videoEditedInfo     // Catch:{ Exception -> 0x187f }
            if (r2 == 0) goto L_0x0836
            if (r48 != 0) goto L_0x0836
            org.telegram.messenger.VideoEditedInfo r2 = r6.videoEditedInfo     // Catch:{ Exception -> 0x0827 }
            goto L_0x0838
        L_0x0836:
            r2 = r48
        L_0x0838:
            if (r12 == 0) goto L_0x086a
            java.lang.String r8 = "groupId"
            java.lang.Object r8 = r12.get(r8)     // Catch:{ Exception -> 0x0827 }
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ Exception -> 0x0827 }
            if (r8 == 0) goto L_0x085a
            java.lang.Long r8 = org.telegram.messenger.Utilities.parseLong(r8)     // Catch:{ Exception -> 0x0827 }
            r49 = r1
            r48 = r2
            long r1 = r8.longValue()     // Catch:{ Exception -> 0x0827 }
            r5.grouped_id = r1     // Catch:{ Exception -> 0x0827 }
            int r8 = r5.flags     // Catch:{ Exception -> 0x0827 }
            r11 = 131072(0x20000, float:1.83671E-40)
            r8 = r8 | r11
            r5.flags = r8     // Catch:{ Exception -> 0x0827 }
            goto L_0x0860
        L_0x085a:
            r49 = r1
            r48 = r2
            r1 = r16
        L_0x0860:
            java.lang.String r8 = "final"
            java.lang.Object r8 = r12.get(r8)     // Catch:{ Exception -> 0x0827 }
            if (r8 == 0) goto L_0x0870
            r8 = 1
            goto L_0x0871
        L_0x086a:
            r49 = r1
            r48 = r2
            r1 = r16
        L_0x0870:
            r8 = 0
        L_0x0871:
            int r11 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r11 != 0) goto L_0x08cf
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x08c7 }
            r8.<init>()     // Catch:{ Exception -> 0x08c7 }
            r8.add(r6)     // Catch:{ Exception -> 0x08c7 }
            java.util.ArrayList r11 = new java.util.ArrayList     // Catch:{ Exception -> 0x08c7 }
            r11.<init>()     // Catch:{ Exception -> 0x08c7 }
            r11.add(r5)     // Catch:{ Exception -> 0x08c7 }
            r50 = r12
            int r12 = r7.currentAccount     // Catch:{ Exception -> 0x08c7 }
            org.telegram.messenger.MessagesStorage r33 = org.telegram.messenger.MessagesStorage.getInstance(r12)     // Catch:{ Exception -> 0x08c7 }
            r35 = 0
            r36 = 1
            r37 = 0
            r38 = 0
            if (r10 == 0) goto L_0x089a
            r39 = 1
            goto L_0x089c
        L_0x089a:
            r39 = 0
        L_0x089c:
            r34 = r11
            r33.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC.Message>) r34, (boolean) r35, (boolean) r36, (boolean) r37, (int) r38, (boolean) r39)     // Catch:{ Exception -> 0x08c7 }
            int r11 = r7.currentAccount     // Catch:{ Exception -> 0x08c7 }
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)     // Catch:{ Exception -> 0x08c7 }
            if (r10 == 0) goto L_0x08ab
            r12 = 1
            goto L_0x08ac
        L_0x08ab:
            r12 = 0
        L_0x08ac:
            r11.updateInterfaceWithMessages(r13, r8, r12)     // Catch:{ Exception -> 0x08c7 }
            if (r10 != 0) goto L_0x08c2
            int r8 = r7.currentAccount     // Catch:{ Exception -> 0x08c7 }
            org.telegram.messenger.NotificationCenter r8 = org.telegram.messenger.NotificationCenter.getInstance(r8)     // Catch:{ Exception -> 0x08c7 }
            int r11 = org.telegram.messenger.NotificationCenter.dialogsNeedReload     // Catch:{ Exception -> 0x08c7 }
            r57 = r6
            r12 = 0
            java.lang.Object[] r6 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x08f6 }
            r8.postNotificationName(r11, r6)     // Catch:{ Exception -> 0x08f6 }
            goto L_0x08c4
        L_0x08c2:
            r57 = r6
        L_0x08c4:
            r6 = 0
            r11 = 0
            goto L_0x091c
        L_0x08c7:
            r0 = move-exception
            r57 = r6
        L_0x08ca:
            r11 = r57
        L_0x08cc:
            r2 = r0
            goto L_0x082a
        L_0x08cf:
            r57 = r6
            r50 = r12
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x187b }
            r6.<init>()     // Catch:{ Exception -> 0x187b }
            java.lang.String r11 = "group_"
            r6.append(r11)     // Catch:{ Exception -> 0x187b }
            r6.append(r1)     // Catch:{ Exception -> 0x187b }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x187b }
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r11 = r7.delayedMessages     // Catch:{ Exception -> 0x187b }
            java.lang.Object r6 = r11.get(r6)     // Catch:{ Exception -> 0x187b }
            java.util.ArrayList r6 = (java.util.ArrayList) r6     // Catch:{ Exception -> 0x187b }
            if (r6 == 0) goto L_0x08f8
            r11 = 0
            java.lang.Object r6 = r6.get(r11)     // Catch:{ Exception -> 0x08f6 }
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r6     // Catch:{ Exception -> 0x08f6 }
            goto L_0x08f9
        L_0x08f6:
            r0 = move-exception
            goto L_0x08ca
        L_0x08f8:
            r6 = 0
        L_0x08f9:
            if (r6 != 0) goto L_0x090c
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x08f6 }
            r6.<init>(r13)     // Catch:{ Exception -> 0x08f6 }
            r6.initForGroup(r1)     // Catch:{ Exception -> 0x08f6 }
            r6.encryptedChat = r9     // Catch:{ Exception -> 0x08f6 }
            if (r10 == 0) goto L_0x0909
            r11 = 1
            goto L_0x090a
        L_0x0909:
            r11 = 0
        L_0x090a:
            r6.scheduled = r11     // Catch:{ Exception -> 0x08f6 }
        L_0x090c:
            r11 = 0
            r6.performMediaUpload = r11     // Catch:{ Exception -> 0x187b }
            r11 = 0
            r6.photoSize = r11     // Catch:{ Exception -> 0x187b }
            r6.videoEditedInfo = r11     // Catch:{ Exception -> 0x187b }
            r6.httpLocation = r11     // Catch:{ Exception -> 0x187b }
            if (r8 == 0) goto L_0x091c
            int r8 = r5.id     // Catch:{ Exception -> 0x08f6 }
            r6.finalGroupMessage = r8     // Catch:{ Exception -> 0x08f6 }
        L_0x091c:
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x187b }
            java.lang.String r12 = "silent_"
            if (r8 == 0) goto L_0x0989
            if (r3 == 0) goto L_0x0989
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x08f6 }
            r8.<init>()     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r11 = "send message user_id = "
            r8.append(r11)     // Catch:{ Exception -> 0x08f6 }
            int r11 = r3.user_id     // Catch:{ Exception -> 0x08f6 }
            r8.append(r11)     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r11 = " chat_id = "
            r8.append(r11)     // Catch:{ Exception -> 0x08f6 }
            int r11 = r3.chat_id     // Catch:{ Exception -> 0x08f6 }
            r8.append(r11)     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r11 = " channel_id = "
            r8.append(r11)     // Catch:{ Exception -> 0x08f6 }
            int r11 = r3.channel_id     // Catch:{ Exception -> 0x08f6 }
            r8.append(r11)     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r11 = " access_hash = "
            r8.append(r11)     // Catch:{ Exception -> 0x08f6 }
            r61 = r1
            long r1 = r3.access_hash     // Catch:{ Exception -> 0x08f6 }
            r8.append(r1)     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r1 = " notify = "
            r8.append(r1)     // Catch:{ Exception -> 0x08f6 }
            r1 = r63
            r8.append(r1)     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r2 = " silent = "
            r8.append(r2)     // Catch:{ Exception -> 0x08f6 }
            int r2 = r7.currentAccount     // Catch:{ Exception -> 0x08f6 }
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)     // Catch:{ Exception -> 0x08f6 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x08f6 }
            r11.<init>()     // Catch:{ Exception -> 0x08f6 }
            r11.append(r12)     // Catch:{ Exception -> 0x08f6 }
            r11.append(r13)     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x08f6 }
            r52 = r12
            r12 = 0
            boolean r2 = r2.getBoolean(r11, r12)     // Catch:{ Exception -> 0x08f6 }
            r8.append(r2)     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r2 = r8.toString()     // Catch:{ Exception -> 0x08f6 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x08f6 }
            goto L_0x098f
        L_0x0989:
            r61 = r1
            r52 = r12
            r1 = r63
        L_0x098f:
            if (r15 == 0) goto L_0x1738
            r2 = 9
            if (r15 != r2) goto L_0x099b
            if (r49 == 0) goto L_0x099b
            if (r9 == 0) goto L_0x099b
            goto L_0x1738
        L_0x099b:
            r2 = 1
            if (r15 < r2) goto L_0x09a1
            r2 = 3
            if (r15 <= r2) goto L_0x09b2
        L_0x09a1:
            r2 = 5
            if (r15 < r2) goto L_0x09a8
            r2 = 8
            if (r15 <= r2) goto L_0x09b2
        L_0x09a8:
            r2 = 9
            if (r15 != r2) goto L_0x09ae
            if (r9 != 0) goto L_0x09b2
        L_0x09ae:
            r2 = 10
            if (r15 != r2) goto L_0x15b9
        L_0x09b2:
            if (r9 != 0) goto L_0x0fe0
            r2 = 1
            if (r15 != r2) goto L_0x0a11
            r2 = r29
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue     // Catch:{ Exception -> 0x08f6 }
            if (r4 == 0) goto L_0x09d7
            org.telegram.tgnet.TLRPC$TL_inputMediaVenue r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue     // Catch:{ Exception -> 0x08f6 }
            r4.<init>()     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r8 = r2.address     // Catch:{ Exception -> 0x08f6 }
            r4.address = r8     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r8 = r2.title     // Catch:{ Exception -> 0x08f6 }
            r4.title = r8     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r8 = r2.provider     // Catch:{ Exception -> 0x08f6 }
            r4.provider = r8     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r8 = r2.venue_id     // Catch:{ Exception -> 0x08f6 }
            r4.venue_id = r8     // Catch:{ Exception -> 0x08f6 }
            r8 = r28
            r4.venue_type = r8     // Catch:{ Exception -> 0x08f6 }
            goto L_0x09f0
        L_0x09d7:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive     // Catch:{ Exception -> 0x08f6 }
            if (r4 == 0) goto L_0x09eb
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive     // Catch:{ Exception -> 0x08f6 }
            r4.<init>()     // Catch:{ Exception -> 0x08f6 }
            int r8 = r2.period     // Catch:{ Exception -> 0x08f6 }
            r4.period = r8     // Catch:{ Exception -> 0x08f6 }
            int r8 = r4.flags     // Catch:{ Exception -> 0x08f6 }
            r9 = 2
            r8 = r8 | r9
            r4.flags = r8     // Catch:{ Exception -> 0x08f6 }
            goto L_0x09f0
        L_0x09eb:
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint     // Catch:{ Exception -> 0x08f6 }
            r4.<init>()     // Catch:{ Exception -> 0x08f6 }
        L_0x09f0:
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r8 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint     // Catch:{ Exception -> 0x08f6 }
            r8.<init>()     // Catch:{ Exception -> 0x08f6 }
            r4.geo_point = r8     // Catch:{ Exception -> 0x08f6 }
            org.telegram.tgnet.TLRPC$InputGeoPoint r8 = r4.geo_point     // Catch:{ Exception -> 0x08f6 }
            org.telegram.tgnet.TLRPC$GeoPoint r9 = r2.geo     // Catch:{ Exception -> 0x08f6 }
            double r11 = r9.lat     // Catch:{ Exception -> 0x08f6 }
            r8.lat = r11     // Catch:{ Exception -> 0x08f6 }
            org.telegram.tgnet.TLRPC$InputGeoPoint r8 = r4.geo_point     // Catch:{ Exception -> 0x08f6 }
            org.telegram.tgnet.TLRPC$GeoPoint r2 = r2.geo     // Catch:{ Exception -> 0x08f6 }
            double r11 = r2._long     // Catch:{ Exception -> 0x08f6 }
            r8._long = r11     // Catch:{ Exception -> 0x08f6 }
            r11 = r57
            r28 = r3
            r3 = r18
            r12 = r20
            goto L_0x0CLASSNAME
        L_0x0a11:
            r8 = r28
            r2 = 2
            if (r15 == r2) goto L_0x0cde
            r2 = 9
            if (r15 != r2) goto L_0x0a1e
            if (r44 == 0) goto L_0x0a1e
            goto L_0x0cde
        L_0x0a1e:
            r2 = 3
            if (r15 != r2) goto L_0x0ae7
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x08f6 }
            r2.<init>()     // Catch:{ Exception -> 0x08f6 }
            java.lang.String r8 = r4.mime_type     // Catch:{ Exception -> 0x08f6 }
            r2.mime_type = r8     // Catch:{ Exception -> 0x08f6 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r4.attributes     // Catch:{ Exception -> 0x08f6 }
            r2.attributes = r8     // Catch:{ Exception -> 0x08f6 }
            boolean r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r4)     // Catch:{ Exception -> 0x08f6 }
            if (r8 != 0) goto L_0x0a50
            if (r48 == 0) goto L_0x0a41
            r11 = r48
            boolean r8 = r11.muted     // Catch:{ Exception -> 0x08f6 }
            if (r8 != 0) goto L_0x0a52
            boolean r8 = r11.roundVideo     // Catch:{ Exception -> 0x08f6 }
            if (r8 != 0) goto L_0x0a52
            goto L_0x0a43
        L_0x0a41:
            r11 = r48
        L_0x0a43:
            r8 = 1
            r2.nosound_video = r8     // Catch:{ Exception -> 0x08f6 }
            boolean r8 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x08f6 }
            if (r8 == 0) goto L_0x0a52
            java.lang.String r8 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r8)     // Catch:{ Exception -> 0x08f6 }
            goto L_0x0a52
        L_0x0a50:
            r11 = r48
        L_0x0a52:
            if (r47 == 0) goto L_0x0a60
            r9 = r47
            r2.ttl_seconds = r9     // Catch:{ Exception -> 0x08f6 }
            r5.ttl = r9     // Catch:{ Exception -> 0x08f6 }
            int r8 = r2.flags     // Catch:{ Exception -> 0x08f6 }
            r9 = 2
            r8 = r8 | r9
            r2.flags = r8     // Catch:{ Exception -> 0x08f6 }
        L_0x0a60:
            long r8 = r4.access_hash     // Catch:{ Exception -> 0x08f6 }
            int r12 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r12 != 0) goto L_0x0a6b
            r9 = r2
            r48 = r11
            r8 = 1
            goto L_0x0a9a
        L_0x0a6b:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x08f6 }
            r8.<init>()     // Catch:{ Exception -> 0x08f6 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r9 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x08f6 }
            r9.<init>()     // Catch:{ Exception -> 0x08f6 }
            r8.id = r9     // Catch:{ Exception -> 0x08f6 }
            org.telegram.tgnet.TLRPC$InputDocument r9 = r8.id     // Catch:{ Exception -> 0x08f6 }
            r48 = r11
            long r11 = r4.id     // Catch:{ Exception -> 0x08f6 }
            r9.id = r11     // Catch:{ Exception -> 0x08f6 }
            org.telegram.tgnet.TLRPC$InputDocument r9 = r8.id     // Catch:{ Exception -> 0x08f6 }
            long r11 = r4.access_hash     // Catch:{ Exception -> 0x08f6 }
            r9.access_hash = r11     // Catch:{ Exception -> 0x08f6 }
            org.telegram.tgnet.TLRPC$InputDocument r9 = r8.id     // Catch:{ Exception -> 0x08f6 }
            byte[] r11 = r4.file_reference     // Catch:{ Exception -> 0x08f6 }
            r9.file_reference = r11     // Catch:{ Exception -> 0x08f6 }
            org.telegram.tgnet.TLRPC$InputDocument r9 = r8.id     // Catch:{ Exception -> 0x08f6 }
            byte[] r9 = r9.file_reference     // Catch:{ Exception -> 0x08f6 }
            if (r9 != 0) goto L_0x0a98
            org.telegram.tgnet.TLRPC$InputDocument r9 = r8.id     // Catch:{ Exception -> 0x08f6 }
            r11 = 0
            byte[] r12 = new byte[r11]     // Catch:{ Exception -> 0x08f6 }
            r9.file_reference = r12     // Catch:{ Exception -> 0x08f6 }
        L_0x0a98:
            r9 = r8
            r8 = 0
        L_0x0a9a:
            if (r6 != 0) goto L_0x0aba
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x08f6 }
            r6.<init>(r13)     // Catch:{ Exception -> 0x08f6 }
            r11 = 1
            r6.type = r11     // Catch:{ Exception -> 0x08f6 }
            r11 = r57
            r6.obj = r11     // Catch:{ Exception -> 0x0b38 }
            r12 = r20
            r6.originalPath = r12     // Catch:{ Exception -> 0x0b38 }
            r1 = r18
            r6.parentObject = r1     // Catch:{ Exception -> 0x0b38 }
            r44 = r9
            if (r10 == 0) goto L_0x0ab6
            r9 = 1
            goto L_0x0ab7
        L_0x0ab6:
            r9 = 0
        L_0x0ab7:
            r6.scheduled = r9     // Catch:{ Exception -> 0x0b38 }
            goto L_0x0ac2
        L_0x0aba:
            r11 = r57
            r44 = r9
            r1 = r18
            r12 = r20
        L_0x0ac2:
            r6.inputUploadMedia = r2     // Catch:{ Exception -> 0x0b38 }
            r6.performMediaUpload = r8     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r4.thumbs     // Catch:{ Exception -> 0x0b38 }
            boolean r2 = r2.isEmpty()     // Catch:{ Exception -> 0x0b38 }
            if (r2 != 0) goto L_0x0adb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r4.thumbs     // Catch:{ Exception -> 0x0b38 }
            r9 = 0
            java.lang.Object r2 = r2.get(r9)     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2     // Catch:{ Exception -> 0x0b38 }
            r6.photoSize = r2     // Catch:{ Exception -> 0x0b38 }
            r6.locationParent = r4     // Catch:{ Exception -> 0x0b38 }
        L_0x0adb:
            r2 = r48
            r6.videoEditedInfo = r2     // Catch:{ Exception -> 0x0b38 }
            r4 = r44
            r28 = r3
            r2 = r8
        L_0x0ae4:
            r3 = r1
            goto L_0x0dbf
        L_0x0ae7:
            r9 = r47
            r11 = r57
            r1 = r18
            r12 = r20
            r2 = 6
            if (r15 != r2) goto L_0x0b3b
            org.telegram.tgnet.TLRPC$TL_inputMediaContact r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact     // Catch:{ Exception -> 0x0b38 }
            r2.<init>()     // Catch:{ Exception -> 0x0b38 }
            r4 = r46
            java.lang.String r9 = r4.phone     // Catch:{ Exception -> 0x0b38 }
            r2.phone_number = r9     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r9 = r4.first_name     // Catch:{ Exception -> 0x0b38 }
            r2.first_name = r9     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r9 = r4.last_name     // Catch:{ Exception -> 0x0b38 }
            r2.last_name = r9     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r9 = r4.restriction_reason     // Catch:{ Exception -> 0x0b38 }
            boolean r9 = r9.isEmpty()     // Catch:{ Exception -> 0x0b38 }
            if (r9 != 0) goto L_0x0b30
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r9 = r4.restriction_reason     // Catch:{ Exception -> 0x0b38 }
            r28 = r3
            r3 = 0
            java.lang.Object r9 = r9.get(r3)     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r9 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r9     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r3 = r9.text     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r9 = "BEGIN:VCARD"
            boolean r3 = r3.startsWith(r9)     // Catch:{ Exception -> 0x0b38 }
            if (r3 == 0) goto L_0x0b32
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r3 = r4.restriction_reason     // Catch:{ Exception -> 0x0b38 }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r3 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r3     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r3 = r3.text     // Catch:{ Exception -> 0x0b38 }
            r2.vcard = r3     // Catch:{ Exception -> 0x0b38 }
            goto L_0x0b34
        L_0x0b30:
            r28 = r3
        L_0x0b32:
            r2.vcard = r8     // Catch:{ Exception -> 0x0b38 }
        L_0x0b34:
            r3 = r1
            r4 = r2
            goto L_0x0CLASSNAME
        L_0x0b38:
            r0 = move-exception
            goto L_0x08cc
        L_0x0b3b:
            r28 = r3
            r2 = 7
            if (r15 == r2) goto L_0x0c0c
            r2 = 9
            if (r15 != r2) goto L_0x0b46
            goto L_0x0c0c
        L_0x0b46:
            r2 = 8
            if (r15 != r2) goto L_0x0bb3
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0b38 }
            r2.<init>()     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r3 = r4.mime_type     // Catch:{ Exception -> 0x0b38 }
            r2.mime_type = r3     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r4.attributes     // Catch:{ Exception -> 0x0b38 }
            r2.attributes = r3     // Catch:{ Exception -> 0x0b38 }
            if (r9 == 0) goto L_0x0b63
            r2.ttl_seconds = r9     // Catch:{ Exception -> 0x0b38 }
            r5.ttl = r9     // Catch:{ Exception -> 0x0b38 }
            int r3 = r2.flags     // Catch:{ Exception -> 0x0b38 }
            r6 = 2
            r3 = r3 | r6
            r2.flags = r3     // Catch:{ Exception -> 0x0b38 }
        L_0x0b63:
            long r8 = r4.access_hash     // Catch:{ Exception -> 0x0b38 }
            int r3 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x0b6c
            r4 = r2
            r3 = 1
            goto L_0x0b99
        L_0x0b6c:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0b38 }
            r3.<init>()     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0b38 }
            r6.<init>()     // Catch:{ Exception -> 0x0b38 }
            r3.id = r6     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputDocument r6 = r3.id     // Catch:{ Exception -> 0x0b38 }
            long r8 = r4.id     // Catch:{ Exception -> 0x0b38 }
            r6.id = r8     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputDocument r6 = r3.id     // Catch:{ Exception -> 0x0b38 }
            long r8 = r4.access_hash     // Catch:{ Exception -> 0x0b38 }
            r6.access_hash = r8     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputDocument r6 = r3.id     // Catch:{ Exception -> 0x0b38 }
            byte[] r4 = r4.file_reference     // Catch:{ Exception -> 0x0b38 }
            r6.file_reference = r4     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputDocument r4 = r3.id     // Catch:{ Exception -> 0x0b38 }
            byte[] r4 = r4.file_reference     // Catch:{ Exception -> 0x0b38 }
            if (r4 != 0) goto L_0x0b97
            org.telegram.tgnet.TLRPC$InputDocument r4 = r3.id     // Catch:{ Exception -> 0x0b38 }
            r6 = 0
            byte[] r8 = new byte[r6]     // Catch:{ Exception -> 0x0b38 }
            r4.file_reference = r8     // Catch:{ Exception -> 0x0b38 }
        L_0x0b97:
            r4 = r3
            r3 = 0
        L_0x0b99:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0b38 }
            r6.<init>(r13)     // Catch:{ Exception -> 0x0b38 }
            r8 = 3
            r6.type = r8     // Catch:{ Exception -> 0x0b38 }
            r6.obj = r11     // Catch:{ Exception -> 0x0b38 }
            r6.parentObject = r1     // Catch:{ Exception -> 0x0b38 }
            r6.inputUploadMedia = r2     // Catch:{ Exception -> 0x0b38 }
            r6.performMediaUpload = r3     // Catch:{ Exception -> 0x0b38 }
            if (r10 == 0) goto L_0x0bad
            r2 = 1
            goto L_0x0bae
        L_0x0bad:
            r2 = 0
        L_0x0bae:
            r6.scheduled = r2     // Catch:{ Exception -> 0x0b38 }
            r2 = r3
            goto L_0x0ae4
        L_0x0bb3:
            r2 = 10
            if (r15 != r2) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_inputMediaPoll r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll     // Catch:{ Exception -> 0x0b38 }
            r2.<init>()     // Catch:{ Exception -> 0x0b38 }
            r8 = r26
            org.telegram.tgnet.TLRPC$TL_poll r3 = r8.poll     // Catch:{ Exception -> 0x0b38 }
            r2.poll = r3     // Catch:{ Exception -> 0x0b38 }
            if (r50 == 0) goto L_0x0bff
            java.lang.String r3 = "answers"
            r8 = r50
            boolean r3 = r8.containsKey(r3)     // Catch:{ Exception -> 0x0b38 }
            if (r3 == 0) goto L_0x0bff
            java.lang.String r3 = "answers"
            java.lang.Object r3 = r8.get(r3)     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0b38 }
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x0b38 }
            int r4 = r3.length     // Catch:{ Exception -> 0x0b38 }
            if (r4 <= 0) goto L_0x0bff
            r4 = 0
        L_0x0bde:
            int r8 = r3.length     // Catch:{ Exception -> 0x0b38 }
            if (r4 >= r8) goto L_0x0bf6
            java.util.ArrayList<byte[]> r8 = r2.correct_answers     // Catch:{ Exception -> 0x0b38 }
            r18 = r1
            r9 = 1
            byte[] r1 = new byte[r9]     // Catch:{ Exception -> 0x0b38 }
            byte r9 = r3[r4]     // Catch:{ Exception -> 0x0b38 }
            r19 = 0
            r1[r19] = r9     // Catch:{ Exception -> 0x0b38 }
            r8.add(r1)     // Catch:{ Exception -> 0x0b38 }
            int r4 = r4 + 1
            r1 = r18
            goto L_0x0bde
        L_0x0bf6:
            r18 = r1
            int r1 = r2.flags     // Catch:{ Exception -> 0x0b38 }
            r3 = 1
            r1 = r1 | r3
            r2.flags = r1     // Catch:{ Exception -> 0x0b38 }
            goto L_0x0CLASSNAME
        L_0x0bff:
            r18 = r1
        L_0x0CLASSNAME:
            r4 = r2
            r3 = r18
        L_0x0CLASSNAME:
            r2 = 0
            goto L_0x0dbf
        L_0x0CLASSNAME:
            r3 = r1
            r2 = 0
            r4 = 0
            goto L_0x0dbf
        L_0x0c0c:
            r8 = r50
            r18 = r1
            if (r12 != 0) goto L_0x0CLASSNAME
            r1 = r55
            r3 = r18
            if (r1 != 0) goto L_0x0CLASSNAME
            long r1 = r4.access_hash     // Catch:{ Exception -> 0x0b38 }
            int r18 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x0c1f
            goto L_0x0CLASSNAME
        L_0x0c1f:
            r1 = 0
            goto L_0x0c5b
        L_0x0CLASSNAME:
            r3 = r18
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0cd9 }
            r1.<init>()     // Catch:{ Exception -> 0x0cd9 }
            if (r9 == 0) goto L_0x0CLASSNAME
            r1.ttl_seconds = r9     // Catch:{ Exception -> 0x0b38 }
            r5.ttl = r9     // Catch:{ Exception -> 0x0b38 }
            int r2 = r1.flags     // Catch:{ Exception -> 0x0b38 }
            r9 = 2
            r2 = r2 | r9
            r1.flags = r2     // Catch:{ Exception -> 0x0b38 }
        L_0x0CLASSNAME:
            boolean r2 = android.text.TextUtils.isEmpty(r55)     // Catch:{ Exception -> 0x0cd9 }
            if (r2 != 0) goto L_0x0CLASSNAME
            java.lang.String r2 = r55.toLowerCase()     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r9 = "mp4"
            boolean r2 = r2.endsWith(r9)     // Catch:{ Exception -> 0x0b38 }
            if (r2 == 0) goto L_0x0CLASSNAME
            if (r8 == 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "forceDocument"
            boolean r2 = r8.containsKey(r2)     // Catch:{ Exception -> 0x0b38 }
            if (r2 == 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r2 = 1
            r1.nosound_video = r2     // Catch:{ Exception -> 0x0b38 }
        L_0x0CLASSNAME:
            java.lang.String r2 = r4.mime_type     // Catch:{ Exception -> 0x0cd9 }
            r1.mime_type = r2     // Catch:{ Exception -> 0x0cd9 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r4.attributes     // Catch:{ Exception -> 0x0cd9 }
            r1.attributes = r2     // Catch:{ Exception -> 0x0cd9 }
        L_0x0c5b:
            long r8 = r4.access_hash     // Catch:{ Exception -> 0x0cd9 }
            int r2 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r2 != 0) goto L_0x0CLASSNAME
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0b38 }
            r18 = r5
            r56 = r6
            r5 = r1
            goto L_0x0c9a
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0cd9 }
            r2.<init>()     // Catch:{ Exception -> 0x0cd9 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r8 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0cd9 }
            r8.<init>()     // Catch:{ Exception -> 0x0cd9 }
            r2.id = r8     // Catch:{ Exception -> 0x0cd9 }
            org.telegram.tgnet.TLRPC$InputDocument r8 = r2.id     // Catch:{ Exception -> 0x0cd9 }
            r18 = r5
            r56 = r6
            long r5 = r4.id     // Catch:{ Exception -> 0x0ccb }
            r8.id = r5     // Catch:{ Exception -> 0x0ccb }
            org.telegram.tgnet.TLRPC$InputDocument r5 = r2.id     // Catch:{ Exception -> 0x0ccb }
            long r8 = r4.access_hash     // Catch:{ Exception -> 0x0ccb }
            r5.access_hash = r8     // Catch:{ Exception -> 0x0ccb }
            org.telegram.tgnet.TLRPC$InputDocument r5 = r2.id     // Catch:{ Exception -> 0x0ccb }
            byte[] r6 = r4.file_reference     // Catch:{ Exception -> 0x0ccb }
            r5.file_reference = r6     // Catch:{ Exception -> 0x0ccb }
            org.telegram.tgnet.TLRPC$InputDocument r5 = r2.id     // Catch:{ Exception -> 0x0ccb }
            byte[] r5 = r5.file_reference     // Catch:{ Exception -> 0x0ccb }
            if (r5 != 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$InputDocument r5 = r2.id     // Catch:{ Exception -> 0x0ccb }
            r6 = 0
            byte[] r8 = new byte[r6]     // Catch:{ Exception -> 0x0ccb }
            r5.file_reference = r8     // Catch:{ Exception -> 0x0ccb }
        L_0x0CLASSNAME:
            r5 = r2
            r2 = 0
        L_0x0c9a:
            if (r1 == 0) goto L_0x0cd2
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0ccb }
            r6.<init>(r13)     // Catch:{ Exception -> 0x0ccb }
            r6.originalPath = r12     // Catch:{ Exception -> 0x0ccb }
            r8 = 2
            r6.type = r8     // Catch:{ Exception -> 0x0ccb }
            r6.obj = r11     // Catch:{ Exception -> 0x0ccb }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r4.thumbs     // Catch:{ Exception -> 0x0ccb }
            boolean r8 = r8.isEmpty()     // Catch:{ Exception -> 0x0ccb }
            if (r8 != 0) goto L_0x0cbd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r4.thumbs     // Catch:{ Exception -> 0x0ccb }
            r9 = 0
            java.lang.Object r8 = r8.get(r9)     // Catch:{ Exception -> 0x0ccb }
            org.telegram.tgnet.TLRPC$PhotoSize r8 = (org.telegram.tgnet.TLRPC.PhotoSize) r8     // Catch:{ Exception -> 0x0ccb }
            r6.photoSize = r8     // Catch:{ Exception -> 0x0ccb }
            r6.locationParent = r4     // Catch:{ Exception -> 0x0ccb }
        L_0x0cbd:
            r6.parentObject = r3     // Catch:{ Exception -> 0x0ccb }
            r6.inputUploadMedia = r1     // Catch:{ Exception -> 0x0ccb }
            r6.performMediaUpload = r2     // Catch:{ Exception -> 0x0ccb }
            if (r10 == 0) goto L_0x0cc7
            r1 = 1
            goto L_0x0cc8
        L_0x0cc7:
            r1 = 0
        L_0x0cc8:
            r6.scheduled = r1     // Catch:{ Exception -> 0x0ccb }
            goto L_0x0cd4
        L_0x0ccb:
            r0 = move-exception
            r2 = r0
            r1 = r10
        L_0x0cce:
            r5 = r18
            goto L_0x1897
        L_0x0cd2:
            r6 = r56
        L_0x0cd4:
            r4 = r5
            r5 = r18
            goto L_0x0dbf
        L_0x0cd9:
            r0 = move-exception
            r18 = r5
            goto L_0x08cc
        L_0x0cde:
            r9 = r47
            r8 = r50
            r11 = r57
            r28 = r3
            r56 = r6
            r3 = r18
            r12 = r20
            r18 = r5
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x0fdb }
            r1.<init>()     // Catch:{ Exception -> 0x0fdb }
            if (r9 == 0) goto L_0x0d02
            r1.ttl_seconds = r9     // Catch:{ Exception -> 0x0fdb }
            r5 = r18
            r5.ttl = r9     // Catch:{ Exception -> 0x0b38 }
            int r2 = r1.flags     // Catch:{ Exception -> 0x0b38 }
            r4 = 2
            r2 = r2 | r4
            r1.flags = r2     // Catch:{ Exception -> 0x0b38 }
            goto L_0x0d04
        L_0x0d02:
            r5 = r18
        L_0x0d04:
            if (r8 == 0) goto L_0x0d3f
            java.lang.String r2 = "masks"
            java.lang.Object r2 = r8.get(r2)     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0b38 }
            if (r2 == 0) goto L_0x0d3f
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0b38 }
            byte[] r2 = org.telegram.messenger.Utilities.hexToBytes(r2)     // Catch:{ Exception -> 0x0b38 }
            r4.<init>((byte[]) r2)     // Catch:{ Exception -> 0x0b38 }
            r2 = 0
            int r6 = r4.readInt32(r2)     // Catch:{ Exception -> 0x0b38 }
            r8 = 0
        L_0x0d1f:
            if (r8 >= r6) goto L_0x0d36
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r9 = r1.stickers     // Catch:{ Exception -> 0x0b38 }
            r45 = r6
            int r6 = r4.readInt32(r2)     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputDocument r6 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r4, r6, r2)     // Catch:{ Exception -> 0x0b38 }
            r9.add(r6)     // Catch:{ Exception -> 0x0b38 }
            int r8 = r8 + 1
            r6 = r45
            r2 = 0
            goto L_0x0d1f
        L_0x0d36:
            int r2 = r1.flags     // Catch:{ Exception -> 0x0b38 }
            r6 = 1
            r2 = r2 | r6
            r1.flags = r2     // Catch:{ Exception -> 0x0b38 }
            r4.cleanup()     // Catch:{ Exception -> 0x0b38 }
        L_0x0d3f:
            r6 = r44
            long r8 = r6.access_hash     // Catch:{ Exception -> 0x0b38 }
            int r2 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r2 != 0) goto L_0x0d4a
            r4 = r1
            r2 = 1
            goto L_0x0d77
        L_0x0d4a:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x0b38 }
            r2.<init>()     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r4 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x0b38 }
            r4.<init>()     // Catch:{ Exception -> 0x0b38 }
            r2.id = r4     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r2.id     // Catch:{ Exception -> 0x0b38 }
            long r8 = r6.id     // Catch:{ Exception -> 0x0b38 }
            r4.id = r8     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r2.id     // Catch:{ Exception -> 0x0b38 }
            long r8 = r6.access_hash     // Catch:{ Exception -> 0x0b38 }
            r4.access_hash = r8     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r2.id     // Catch:{ Exception -> 0x0b38 }
            byte[] r8 = r6.file_reference     // Catch:{ Exception -> 0x0b38 }
            r4.file_reference = r8     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r2.id     // Catch:{ Exception -> 0x0b38 }
            byte[] r4 = r4.file_reference     // Catch:{ Exception -> 0x0b38 }
            if (r4 != 0) goto L_0x0d75
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r2.id     // Catch:{ Exception -> 0x0b38 }
            r8 = 0
            byte[] r9 = new byte[r8]     // Catch:{ Exception -> 0x0b38 }
            r4.file_reference = r9     // Catch:{ Exception -> 0x0b38 }
        L_0x0d75:
            r4 = r2
            r2 = 0
        L_0x0d77:
            if (r56 != 0) goto L_0x0d8d
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r8 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0b38 }
            r8.<init>(r13)     // Catch:{ Exception -> 0x0b38 }
            r9 = 0
            r8.type = r9     // Catch:{ Exception -> 0x0b38 }
            r8.obj = r11     // Catch:{ Exception -> 0x0b38 }
            r8.originalPath = r12     // Catch:{ Exception -> 0x0b38 }
            if (r10 == 0) goto L_0x0d89
            r9 = 1
            goto L_0x0d8a
        L_0x0d89:
            r9 = 0
        L_0x0d8a:
            r8.scheduled = r9     // Catch:{ Exception -> 0x0b38 }
            goto L_0x0d8f
        L_0x0d8d:
            r8 = r56
        L_0x0d8f:
            r8.inputUploadMedia = r1     // Catch:{ Exception -> 0x0b38 }
            r8.performMediaUpload = r2     // Catch:{ Exception -> 0x0b38 }
            r1 = r55
            if (r1 == 0) goto L_0x0da8
            int r9 = r55.length()     // Catch:{ Exception -> 0x0b38 }
            if (r9 <= 0) goto L_0x0da8
            java.lang.String r9 = "http"
            boolean r9 = r1.startsWith(r9)     // Catch:{ Exception -> 0x0b38 }
            if (r9 == 0) goto L_0x0da8
            r8.httpLocation = r1     // Catch:{ Exception -> 0x0b38 }
            goto L_0x0dbe
        L_0x0da8:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r6.sizes     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r6.sizes     // Catch:{ Exception -> 0x0b38 }
            int r9 = r9.size()     // Catch:{ Exception -> 0x0b38 }
            r18 = 1
            int r9 = r9 + -1
            java.lang.Object r1 = r1.get(r9)     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1     // Catch:{ Exception -> 0x0b38 }
            r8.photoSize = r1     // Catch:{ Exception -> 0x0b38 }
            r8.locationParent = r6     // Catch:{ Exception -> 0x0b38 }
        L_0x0dbe:
            r6 = r8
        L_0x0dbf:
            int r1 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x0e7c
            org.telegram.tgnet.TLObject r1 = r6.sendRequest     // Catch:{ Exception -> 0x0b38 }
            if (r1 == 0) goto L_0x0dce
            org.telegram.tgnet.TLObject r1 = r6.sendRequest     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r1     // Catch:{ Exception -> 0x0b38 }
            r44 = r15
            goto L_0x0e1b
        L_0x0dce:
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia     // Catch:{ Exception -> 0x0b38 }
            r1.<init>()     // Catch:{ Exception -> 0x0b38 }
            r8 = r28
            r1.peer = r8     // Catch:{ Exception -> 0x0b38 }
            if (r63 == 0) goto L_0x0dfc
            int r8 = r7.currentAccount     // Catch:{ Exception -> 0x0b38 }
            android.content.SharedPreferences r8 = org.telegram.messenger.MessagesController.getNotificationsSettings(r8)     // Catch:{ Exception -> 0x0b38 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b38 }
            r9.<init>()     // Catch:{ Exception -> 0x0b38 }
            r44 = r15
            r15 = r52
            r9.append(r15)     // Catch:{ Exception -> 0x0b38 }
            r9.append(r13)     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0b38 }
            r13 = 0
            boolean r8 = r8.getBoolean(r9, r13)     // Catch:{ Exception -> 0x0b38 }
            if (r8 == 0) goto L_0x0dfa
            goto L_0x0dfe
        L_0x0dfa:
            r8 = 0
            goto L_0x0dff
        L_0x0dfc:
            r44 = r15
        L_0x0dfe:
            r8 = 1
        L_0x0dff:
            r1.silent = r8     // Catch:{ Exception -> 0x0b38 }
            int r8 = r5.reply_to_msg_id     // Catch:{ Exception -> 0x0b38 }
            if (r8 == 0) goto L_0x0e0f
            int r8 = r1.flags     // Catch:{ Exception -> 0x0b38 }
            r9 = 1
            r8 = r8 | r9
            r1.flags = r8     // Catch:{ Exception -> 0x0b38 }
            int r8 = r5.reply_to_msg_id     // Catch:{ Exception -> 0x0b38 }
            r1.reply_to_msg_id = r8     // Catch:{ Exception -> 0x0b38 }
        L_0x0e0f:
            if (r10 == 0) goto L_0x0e19
            r1.schedule_date = r10     // Catch:{ Exception -> 0x0b38 }
            int r8 = r1.flags     // Catch:{ Exception -> 0x0b38 }
            r8 = r8 | 1024(0x400, float:1.435E-42)
            r1.flags = r8     // Catch:{ Exception -> 0x0b38 }
        L_0x0e19:
            r6.sendRequest = r1     // Catch:{ Exception -> 0x0b38 }
        L_0x0e1b:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r6.messageObjects     // Catch:{ Exception -> 0x0b38 }
            r8.add(r11)     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<java.lang.Object> r8 = r6.parentObjects     // Catch:{ Exception -> 0x0b38 }
            r8.add(r3)     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r6.locations     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$PhotoSize r9 = r6.photoSize     // Catch:{ Exception -> 0x0b38 }
            r8.add(r9)     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo> r8 = r6.videoEditedInfos     // Catch:{ Exception -> 0x0b38 }
            org.telegram.messenger.VideoEditedInfo r9 = r6.videoEditedInfo     // Catch:{ Exception -> 0x0b38 }
            r8.add(r9)     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<java.lang.String> r8 = r6.httpLocations     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r9 = r6.httpLocation     // Catch:{ Exception -> 0x0b38 }
            r8.add(r9)     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputMedia> r8 = r6.inputMedias     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$InputMedia r9 = r6.inputUploadMedia     // Catch:{ Exception -> 0x0b38 }
            r8.add(r9)     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r8 = r6.messages     // Catch:{ Exception -> 0x0b38 }
            r8.add(r5)     // Catch:{ Exception -> 0x0b38 }
            java.util.ArrayList<java.lang.String> r8 = r6.originalPaths     // Catch:{ Exception -> 0x0b38 }
            r8.add(r12)     // Catch:{ Exception -> 0x0b38 }
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r8 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia     // Catch:{ Exception -> 0x0b38 }
            r8.<init>()     // Catch:{ Exception -> 0x0b38 }
            long r13 = r5.random_id     // Catch:{ Exception -> 0x0b38 }
            r8.random_id = r13     // Catch:{ Exception -> 0x0b38 }
            r8.media = r4     // Catch:{ Exception -> 0x0b38 }
            r9 = r51
            r8.message = r9     // Catch:{ Exception -> 0x0b38 }
            r9 = r44
            r4 = r60
            if (r4 == 0) goto L_0x0e6e
            boolean r13 = r60.isEmpty()     // Catch:{ Exception -> 0x0b38 }
            if (r13 != 0) goto L_0x0e6e
            r8.entities = r4     // Catch:{ Exception -> 0x0b38 }
            int r4 = r8.flags     // Catch:{ Exception -> 0x0b38 }
            r13 = 1
            r4 = r4 | r13
            r8.flags = r4     // Catch:{ Exception -> 0x0b38 }
        L_0x0e6e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r1.multi_media     // Catch:{ Exception -> 0x0b38 }
            r4.add(r8)     // Catch:{ Exception -> 0x0b38 }
            r44 = r2
            r18 = r3
            r20 = r12
            r12 = r9
            goto L_0x0eee
        L_0x0e7c:
            r9 = r51
            r1 = r60
            r44 = r2
            r20 = r12
            r12 = r15
            r8 = r28
            r15 = r52
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia     // Catch:{ Exception -> 0x0b38 }
            r2.<init>()     // Catch:{ Exception -> 0x0b38 }
            r2.peer = r8     // Catch:{ Exception -> 0x0b38 }
            if (r63 == 0) goto L_0x0eb3
            int r8 = r7.currentAccount     // Catch:{ Exception -> 0x0b38 }
            android.content.SharedPreferences r8 = org.telegram.messenger.MessagesController.getNotificationsSettings(r8)     // Catch:{ Exception -> 0x0b38 }
            r18 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b38 }
            r3.<init>()     // Catch:{ Exception -> 0x0b38 }
            r3.append(r15)     // Catch:{ Exception -> 0x0b38 }
            r3.append(r13)     // Catch:{ Exception -> 0x0b38 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0b38 }
            r13 = 0
            boolean r3 = r8.getBoolean(r3, r13)     // Catch:{ Exception -> 0x0b38 }
            if (r3 == 0) goto L_0x0eb1
            goto L_0x0eb5
        L_0x0eb1:
            r3 = 0
            goto L_0x0eb6
        L_0x0eb3:
            r18 = r3
        L_0x0eb5:
            r3 = 1
        L_0x0eb6:
            r2.silent = r3     // Catch:{ Exception -> 0x0b38 }
            int r3 = r5.reply_to_msg_id     // Catch:{ Exception -> 0x0b38 }
            if (r3 == 0) goto L_0x0ec6
            int r3 = r2.flags     // Catch:{ Exception -> 0x0b38 }
            r8 = 1
            r3 = r3 | r8
            r2.flags = r3     // Catch:{ Exception -> 0x0b38 }
            int r3 = r5.reply_to_msg_id     // Catch:{ Exception -> 0x0b38 }
            r2.reply_to_msg_id = r3     // Catch:{ Exception -> 0x0b38 }
        L_0x0ec6:
            long r13 = r5.random_id     // Catch:{ Exception -> 0x0b38 }
            r2.random_id = r13     // Catch:{ Exception -> 0x0b38 }
            r2.media = r4     // Catch:{ Exception -> 0x0b38 }
            r2.message = r9     // Catch:{ Exception -> 0x0b38 }
            if (r1 == 0) goto L_0x0edf
            boolean r3 = r60.isEmpty()     // Catch:{ Exception -> 0x0b38 }
            if (r3 != 0) goto L_0x0edf
            r2.entities = r1     // Catch:{ Exception -> 0x0b38 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x0b38 }
            r3 = 8
            r1 = r1 | r3
            r2.flags = r1     // Catch:{ Exception -> 0x0b38 }
        L_0x0edf:
            if (r10 == 0) goto L_0x0ee9
            r2.schedule_date = r10     // Catch:{ Exception -> 0x0b38 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x0b38 }
            r1 = r1 | 1024(0x400, float:1.435E-42)
            r2.flags = r1     // Catch:{ Exception -> 0x0b38 }
        L_0x0ee9:
            if (r6 == 0) goto L_0x0eed
            r6.sendRequest = r2     // Catch:{ Exception -> 0x0b38 }
        L_0x0eed:
            r1 = r2
        L_0x0eee:
            int r2 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x0ef7
            r7.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0ef7:
            r2 = 1
            if (r12 != r2) goto L_0x0var_
            r2 = 0
            if (r10 == 0) goto L_0x0eff
            r3 = 1
            goto L_0x0var_
        L_0x0eff:
            r3 = 0
        L_0x0var_:
            r44 = r43
            r45 = r1
            r46 = r11
            r47 = r2
            r48 = r6
            r49 = r18
            r50 = r3
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0var_:
            r2 = 2
            if (r12 != r2) goto L_0x0f3b
            if (r44 == 0) goto L_0x0f1d
            r7.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0f1d:
            r2 = 0
            r3 = 1
            if (r10 == 0) goto L_0x0var_
            r4 = 1
            goto L_0x0var_
        L_0x0var_:
            r4 = 0
        L_0x0var_:
            r44 = r43
            r45 = r1
            r46 = r11
            r47 = r20
            r48 = r2
            r49 = r3
            r50 = r6
            r51 = r18
            r52 = r4
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0f3b:
            r2 = 3
            if (r12 != r2) goto L_0x0f5d
            if (r44 == 0) goto L_0x0var_
            r7.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0var_:
            if (r10 == 0) goto L_0x0var_
            r2 = 1
            goto L_0x0f4a
        L_0x0var_:
            r2 = 0
        L_0x0f4a:
            r44 = r43
            r45 = r1
            r46 = r11
            r47 = r20
            r48 = r6
            r49 = r18
            r50 = r2
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0f5d:
            r2 = 6
            if (r12 != r2) goto L_0x0var_
            if (r10 == 0) goto L_0x0var_
            r2 = 1
            goto L_0x0var_
        L_0x0var_:
            r2 = 0
        L_0x0var_:
            r44 = r43
            r45 = r1
            r46 = r11
            r47 = r20
            r48 = r6
            r49 = r18
            r50 = r2
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0var_:
            r2 = 7
            if (r12 != r2) goto L_0x0f9c
            if (r44 == 0) goto L_0x0var_
            if (r6 == 0) goto L_0x0var_
            r7.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0var_:
            if (r10 == 0) goto L_0x0var_
            r2 = 1
            goto L_0x0var_
        L_0x0var_:
            r2 = 0
        L_0x0var_:
            r44 = r43
            r45 = r1
            r46 = r11
            r47 = r20
            r48 = r6
            r49 = r18
            r50 = r2
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0f9c:
            r2 = 8
            if (r12 != r2) goto L_0x0fbf
            if (r44 == 0) goto L_0x0fa7
            r7.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0fa7:
            if (r10 == 0) goto L_0x0fab
            r2 = 1
            goto L_0x0fac
        L_0x0fab:
            r2 = 0
        L_0x0fac:
            r44 = r43
            r45 = r1
            r46 = r11
            r47 = r20
            r48 = r6
            r49 = r18
            r50 = r2
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0fbf:
            r2 = 10
            if (r12 != r2) goto L_0x1876
            if (r10 == 0) goto L_0x0fc7
            r2 = 1
            goto L_0x0fc8
        L_0x0fc7:
            r2 = 0
        L_0x0fc8:
            r44 = r43
            r45 = r1
            r46 = r11
            r47 = r20
            r48 = r6
            r49 = r18
            r50 = r2
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50)     // Catch:{ Exception -> 0x0b38 }
            goto L_0x1876
        L_0x0fdb:
            r0 = move-exception
            r5 = r18
            goto L_0x08cc
        L_0x0fe0:
            r41 = r46
            r40 = r48
            r1 = r51
            r11 = r57
            r3 = r60
            r56 = r6
            r12 = r15
            r8 = r28
            r2 = r29
            r6 = r44
            r15 = r50
            int r10 = r9.layer     // Catch:{ Exception -> 0x15b4 }
            int r10 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r10)     // Catch:{ Exception -> 0x15b4 }
            r13 = 73
            if (r10 < r13) goto L_0x1014
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x15b4 }
            r10.<init>()     // Catch:{ Exception -> 0x15b4 }
            int r13 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1))
            if (r13 == 0) goto L_0x1019
            r13 = r61
            r10.grouped_id = r13     // Catch:{ Exception -> 0x15b4 }
            int r13 = r10.flags     // Catch:{ Exception -> 0x15b4 }
            r14 = 131072(0x20000, float:1.83671E-40)
            r13 = r13 | r14
            r10.flags = r13     // Catch:{ Exception -> 0x15b4 }
            goto L_0x1019
        L_0x1014:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x15b4 }
            r10.<init>()     // Catch:{ Exception -> 0x15b4 }
        L_0x1019:
            int r13 = r5.ttl     // Catch:{ Exception -> 0x15b4 }
            r10.ttl = r13     // Catch:{ Exception -> 0x15b4 }
            if (r3 == 0) goto L_0x102d
            boolean r13 = r60.isEmpty()     // Catch:{ Exception -> 0x15b4 }
            if (r13 != 0) goto L_0x102d
            r10.entities = r3     // Catch:{ Exception -> 0x15b4 }
            int r3 = r10.flags     // Catch:{ Exception -> 0x15b4 }
            r3 = r3 | 128(0x80, float:1.794E-43)
            r10.flags = r3     // Catch:{ Exception -> 0x15b4 }
        L_0x102d:
            long r13 = r5.reply_to_random_id     // Catch:{ Exception -> 0x15b4 }
            int r3 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x103e
            long r13 = r5.reply_to_random_id     // Catch:{ Exception -> 0x15b4 }
            r10.reply_to_random_id = r13     // Catch:{ Exception -> 0x15b4 }
            int r3 = r10.flags     // Catch:{ Exception -> 0x15b4 }
            r13 = 8
            r3 = r3 | r13
            r10.flags = r3     // Catch:{ Exception -> 0x15b4 }
        L_0x103e:
            int r3 = r10.flags     // Catch:{ Exception -> 0x15b4 }
            r3 = r3 | 512(0x200, float:7.175E-43)
            r10.flags = r3     // Catch:{ Exception -> 0x15b4 }
            if (r15 == 0) goto L_0x105e
            java.lang.String r3 = "bot_name"
            java.lang.Object r3 = r15.get(r3)     // Catch:{ Exception -> 0x15b4 }
            if (r3 == 0) goto L_0x105e
            java.lang.String r3 = "bot_name"
            java.lang.Object r3 = r15.get(r3)     // Catch:{ Exception -> 0x15b4 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x15b4 }
            r10.via_bot_name = r3     // Catch:{ Exception -> 0x15b4 }
            int r3 = r10.flags     // Catch:{ Exception -> 0x15b4 }
            r3 = r3 | 2048(0x800, float:2.87E-42)
            r10.flags = r3     // Catch:{ Exception -> 0x15b4 }
        L_0x105e:
            long r13 = r5.random_id     // Catch:{ Exception -> 0x15b4 }
            r10.random_id = r13     // Catch:{ Exception -> 0x15b4 }
            r10.message = r8     // Catch:{ Exception -> 0x15b4 }
            r3 = 1
            if (r12 != r3) goto L_0x10c5
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue     // Catch:{ Exception -> 0x15b4 }
            if (r1 == 0) goto L_0x108b
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue     // Catch:{ Exception -> 0x15b4 }
            r1.<init>()     // Catch:{ Exception -> 0x15b4 }
            r10.media = r1     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            java.lang.String r3 = r2.address     // Catch:{ Exception -> 0x15b4 }
            r1.address = r3     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            java.lang.String r3 = r2.title     // Catch:{ Exception -> 0x15b4 }
            r1.title = r3     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            java.lang.String r3 = r2.provider     // Catch:{ Exception -> 0x15b4 }
            r1.provider = r3     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            java.lang.String r3 = r2.venue_id     // Catch:{ Exception -> 0x15b4 }
            r1.venue_id = r3     // Catch:{ Exception -> 0x15b4 }
            goto L_0x1092
        L_0x108b:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint     // Catch:{ Exception -> 0x15b4 }
            r1.<init>()     // Catch:{ Exception -> 0x15b4 }
            r10.media = r1     // Catch:{ Exception -> 0x15b4 }
        L_0x1092:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$GeoPoint r3 = r2.geo     // Catch:{ Exception -> 0x15b4 }
            double r3 = r3.lat     // Catch:{ Exception -> 0x15b4 }
            r1.lat = r3     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$GeoPoint r2 = r2.geo     // Catch:{ Exception -> 0x15b4 }
            double r2 = r2._long     // Catch:{ Exception -> 0x15b4 }
            r1._long = r2     // Catch:{ Exception -> 0x15b4 }
            org.telegram.messenger.SecretChatHelper r1 = r43.getSecretChatHelper()     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner     // Catch:{ Exception -> 0x15b4 }
            r3 = 0
            r4 = 0
            r46 = r1
            r47 = r10
            r48 = r2
            r49 = r9
            r50 = r3
            r51 = r4
            r52 = r11
            r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x15b4 }
            r13 = r53
            r18 = r5
            r44 = r12
            r2 = r20
            goto L_0x142a
        L_0x10c5:
            r2 = 2
            if (r12 == r2) goto L_0x1436
            r2 = 9
            if (r12 != r2) goto L_0x10d0
            if (r6 == 0) goto L_0x10d0
            goto L_0x1436
        L_0x10d0:
            r2 = 3
            if (r12 != r2) goto L_0x120a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r4.thumbs     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r7.getThumbForSecretChat(r2)     // Catch:{ Exception -> 0x15b4 }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r2)     // Catch:{ Exception -> 0x15b4 }
            boolean r3 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC.Document) r4)     // Catch:{ Exception -> 0x15b4 }
            if (r3 != 0) goto L_0x1109
            boolean r3 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r4)     // Catch:{ Exception -> 0x15b4 }
            if (r3 == 0) goto L_0x10e9
            goto L_0x1109
        L_0x10e9:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo     // Catch:{ Exception -> 0x15b4 }
            r3.<init>()     // Catch:{ Exception -> 0x15b4 }
            r10.media = r3     // Catch:{ Exception -> 0x15b4 }
            if (r2 == 0) goto L_0x10ff
            byte[] r3 = r2.bytes     // Catch:{ Exception -> 0x15b4 }
            if (r3 == 0) goto L_0x10ff
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r3     // Catch:{ Exception -> 0x15b4 }
            byte[] r6 = r2.bytes     // Catch:{ Exception -> 0x15b4 }
            r3.thumb = r6     // Catch:{ Exception -> 0x15b4 }
            goto L_0x112e
        L_0x10ff:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r3     // Catch:{ Exception -> 0x15b4 }
            r6 = 0
            byte[] r8 = new byte[r6]     // Catch:{ Exception -> 0x15b4 }
            r3.thumb = r8     // Catch:{ Exception -> 0x15b4 }
            goto L_0x112e
        L_0x1109:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x15b4 }
            r3.<init>()     // Catch:{ Exception -> 0x15b4 }
            r10.media = r3     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x15b4 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes     // Catch:{ Exception -> 0x15b4 }
            r3.attributes = r6     // Catch:{ Exception -> 0x15b4 }
            if (r2 == 0) goto L_0x1125
            byte[] r3 = r2.bytes     // Catch:{ Exception -> 0x15b4 }
            if (r3 == 0) goto L_0x1125
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r3     // Catch:{ Exception -> 0x15b4 }
            byte[] r6 = r2.bytes     // Catch:{ Exception -> 0x15b4 }
            r3.thumb = r6     // Catch:{ Exception -> 0x15b4 }
            goto L_0x112e
        L_0x1125:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r3     // Catch:{ Exception -> 0x15b4 }
            r6 = 0
            byte[] r8 = new byte[r6]     // Catch:{ Exception -> 0x15b4 }
            r3.thumb = r8     // Catch:{ Exception -> 0x15b4 }
        L_0x112e:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x15b4 }
            r3.caption = r1     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            java.lang.String r3 = "video/mp4"
            r1.mime_type = r3     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            int r3 = r4.size     // Catch:{ Exception -> 0x15b4 }
            r1.size = r3     // Catch:{ Exception -> 0x15b4 }
            r1 = 0
        L_0x1140:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r4.attributes     // Catch:{ Exception -> 0x15b4 }
            int r3 = r3.size()     // Catch:{ Exception -> 0x15b4 }
            if (r1 >= r3) goto L_0x116a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r4.attributes     // Catch:{ Exception -> 0x15b4 }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3     // Catch:{ Exception -> 0x15b4 }
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo     // Catch:{ Exception -> 0x15b4 }
            if (r6 == 0) goto L_0x1167
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            int r6 = r3.w     // Catch:{ Exception -> 0x15b4 }
            r1.w = r6     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            int r6 = r3.h     // Catch:{ Exception -> 0x15b4 }
            r1.h = r6     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            int r3 = r3.duration     // Catch:{ Exception -> 0x15b4 }
            r1.duration = r3     // Catch:{ Exception -> 0x15b4 }
            goto L_0x116a
        L_0x1167:
            int r1 = r1 + 1
            goto L_0x1140
        L_0x116a:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            int r3 = r2.h     // Catch:{ Exception -> 0x15b4 }
            r1.thumb_h = r3     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x15b4 }
            int r2 = r2.w     // Catch:{ Exception -> 0x15b4 }
            r1.thumb_w = r2     // Catch:{ Exception -> 0x15b4 }
            byte[] r1 = r4.key     // Catch:{ Exception -> 0x15b4 }
            if (r1 == 0) goto L_0x11b9
            int r1 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x117f
            goto L_0x11b9
        L_0x117f:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x15b4 }
            r1.<init>()     // Catch:{ Exception -> 0x15b4 }
            long r2 = r4.id     // Catch:{ Exception -> 0x15b4 }
            r1.id = r2     // Catch:{ Exception -> 0x15b4 }
            long r2 = r4.access_hash     // Catch:{ Exception -> 0x15b4 }
            r1.access_hash = r2     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r10.media     // Catch:{ Exception -> 0x15b4 }
            byte[] r3 = r4.key     // Catch:{ Exception -> 0x15b4 }
            r2.key = r3     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r10.media     // Catch:{ Exception -> 0x15b4 }
            byte[] r3 = r4.iv     // Catch:{ Exception -> 0x15b4 }
            r2.iv = r3     // Catch:{ Exception -> 0x15b4 }
            org.telegram.messenger.SecretChatHelper r2 = r43.getSecretChatHelper()     // Catch:{ Exception -> 0x15b4 }
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner     // Catch:{ Exception -> 0x15b4 }
            r4 = 0
            r46 = r2
            r47 = r10
            r48 = r3
            r49 = r9
            r50 = r1
            r51 = r4
            r52 = r11
            r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x15b4 }
            r13 = r53
            r6 = r56
            r1 = r64
            r2 = r20
            goto L_0x1204
        L_0x11b9:
            if (r56 != 0) goto L_0x11f1
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x15b4 }
            r13 = r53
            r6.<init>(r13)     // Catch:{ Exception -> 0x15b4 }
            r6.encryptedChat = r9     // Catch:{ Exception -> 0x15b4 }
            r1 = 1
            r6.type = r1     // Catch:{ Exception -> 0x15b4 }
            r6.sendEncryptedRequest = r10     // Catch:{ Exception -> 0x15b4 }
            r2 = r20
            r6.originalPath = r2     // Catch:{ Exception -> 0x15b4 }
            r6.obj = r11     // Catch:{ Exception -> 0x15b4 }
            if (r15 == 0) goto L_0x11e0
            r3 = r27
            boolean r1 = r15.containsKey(r3)     // Catch:{ Exception -> 0x15b4 }
            if (r1 == 0) goto L_0x11e0
            java.lang.Object r1 = r15.get(r3)     // Catch:{ Exception -> 0x15b4 }
            r6.parentObject = r1     // Catch:{ Exception -> 0x15b4 }
            goto L_0x11e4
        L_0x11e0:
            r8 = r18
            r6.parentObject = r8     // Catch:{ Exception -> 0x15b4 }
        L_0x11e4:
            r1 = 1
            r6.performMediaUpload = r1     // Catch:{ Exception -> 0x15b4 }
            r1 = r64
            if (r1 == 0) goto L_0x11ed
            r3 = 1
            goto L_0x11ee
        L_0x11ed:
            r3 = 0
        L_0x11ee:
            r6.scheduled = r3     // Catch:{ Exception -> 0x1879 }
            goto L_0x11f9
        L_0x11f1:
            r13 = r53
            r1 = r64
            r2 = r20
            r6 = r56
        L_0x11f9:
            r3 = r40
            r6.videoEditedInfo = r3     // Catch:{ Exception -> 0x1879 }
            int r3 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x1204
            r7.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x1879 }
        L_0x1204:
            r18 = r5
            r44 = r12
            goto L_0x1552
        L_0x120a:
            r13 = r53
            r6 = r64
            r8 = r18
            r2 = r20
            r3 = r27
            r18 = r5
            r5 = 6
            if (r12 != r5) goto L_0x125a
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact     // Catch:{ Exception -> 0x1255 }
            r1.<init>()     // Catch:{ Exception -> 0x1255 }
            r10.media = r1     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1255 }
            r3 = r41
            java.lang.String r4 = r3.phone     // Catch:{ Exception -> 0x1255 }
            r1.phone_number = r4     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1255 }
            java.lang.String r4 = r3.first_name     // Catch:{ Exception -> 0x1255 }
            r1.first_name = r4     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1255 }
            java.lang.String r4 = r3.last_name     // Catch:{ Exception -> 0x1255 }
            r1.last_name = r4     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1255 }
            int r3 = r3.id     // Catch:{ Exception -> 0x1255 }
            r1.user_id = r3     // Catch:{ Exception -> 0x1255 }
            org.telegram.messenger.SecretChatHelper r1 = r43.getSecretChatHelper()     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner     // Catch:{ Exception -> 0x1255 }
            r4 = 0
            r5 = 0
            r46 = r1
            r47 = r10
            r48 = r3
            r49 = r9
            r50 = r4
            r51 = r5
            r52 = r11
            r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1255 }
            goto L_0x12dc
        L_0x1255:
            r0 = move-exception
            r2 = r0
            r1 = r6
            goto L_0x0cce
        L_0x125a:
            r5 = 7
            if (r12 == r5) goto L_0x12e0
            r5 = 9
            if (r12 != r5) goto L_0x1265
            if (r4 == 0) goto L_0x1265
            goto L_0x12e0
        L_0x1265:
            r3 = 8
            if (r12 != r3) goto L_0x12dc
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1255 }
            r3.<init>(r13)     // Catch:{ Exception -> 0x1255 }
            r3.encryptedChat = r9     // Catch:{ Exception -> 0x1255 }
            r3.sendEncryptedRequest = r10     // Catch:{ Exception -> 0x1255 }
            r3.obj = r11     // Catch:{ Exception -> 0x1255 }
            r5 = 3
            r3.type = r5     // Catch:{ Exception -> 0x1255 }
            r3.parentObject = r8     // Catch:{ Exception -> 0x1255 }
            r5 = 1
            r3.performMediaUpload = r5     // Catch:{ Exception -> 0x1255 }
            if (r6 == 0) goto L_0x1280
            r5 = 1
            goto L_0x1281
        L_0x1280:
            r5 = 0
        L_0x1281:
            r3.scheduled = r5     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1255 }
            r5.<init>()     // Catch:{ Exception -> 0x1255 }
            r10.media = r5     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1255 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r4.attributes     // Catch:{ Exception -> 0x1255 }
            r5.attributes = r8     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1255 }
            r5.caption = r1     // Catch:{ Exception -> 0x1255 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r7.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x1255 }
            if (r1 == 0) goto L_0x12b4
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1)     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1255 }
            byte[] r8 = r1.bytes     // Catch:{ Exception -> 0x1255 }
            r5.thumb = r8     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1255 }
            int r8 = r1.h     // Catch:{ Exception -> 0x1255 }
            r5.thumb_h = r8     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1255 }
            int r1 = r1.w     // Catch:{ Exception -> 0x1255 }
            r5.thumb_w = r1     // Catch:{ Exception -> 0x1255 }
            goto L_0x12c5
        L_0x12b4:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r1     // Catch:{ Exception -> 0x1255 }
            r5 = 0
            byte[] r8 = new byte[r5]     // Catch:{ Exception -> 0x1255 }
            r1.thumb = r8     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1255 }
            r1.thumb_h = r5     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1255 }
            r1.thumb_w = r5     // Catch:{ Exception -> 0x1255 }
        L_0x12c5:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1255 }
            java.lang.String r5 = r4.mime_type     // Catch:{ Exception -> 0x1255 }
            r1.mime_type = r5     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1255 }
            int r4 = r4.size     // Catch:{ Exception -> 0x1255 }
            r1.size = r4     // Catch:{ Exception -> 0x1255 }
            r3.originalPath = r2     // Catch:{ Exception -> 0x1255 }
            r7.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x1255 }
            r1 = r6
            r44 = r12
            r6 = r3
            goto L_0x1552
        L_0x12dc:
            r44 = r12
            goto L_0x142a
        L_0x12e0:
            boolean r5 = org.telegram.messenger.MessageObject.isStickerDocument(r4)     // Catch:{ Exception -> 0x1430 }
            if (r5 != 0) goto L_0x13ba
            r5 = 0
            boolean r19 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r5)     // Catch:{ Exception -> 0x1430 }
            if (r19 == 0) goto L_0x12ef
            goto L_0x13ba
        L_0x12ef:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1430 }
            r5.<init>()     // Catch:{ Exception -> 0x1430 }
            r10.media = r5     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1430 }
            r44 = r12
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r4.attributes     // Catch:{ Exception -> 0x1430 }
            r5.attributes = r12     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1430 }
            r5.caption = r1     // Catch:{ Exception -> 0x1430 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r7.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x1430 }
            if (r1 == 0) goto L_0x1322
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1)     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1255 }
            byte[] r12 = r1.bytes     // Catch:{ Exception -> 0x1255 }
            r5.thumb = r12     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1255 }
            int r12 = r1.h     // Catch:{ Exception -> 0x1255 }
            r5.thumb_h = r12     // Catch:{ Exception -> 0x1255 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1255 }
            int r1 = r1.w     // Catch:{ Exception -> 0x1255 }
            r5.thumb_w = r1     // Catch:{ Exception -> 0x1255 }
            goto L_0x1333
        L_0x1322:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r1     // Catch:{ Exception -> 0x1430 }
            r5 = 0
            byte[] r12 = new byte[r5]     // Catch:{ Exception -> 0x1430 }
            r1.thumb = r12     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            r1.thumb_h = r5     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            r1.thumb_w = r5     // Catch:{ Exception -> 0x1430 }
        L_0x1333:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            int r5 = r4.size     // Catch:{ Exception -> 0x1430 }
            r1.size = r5     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            java.lang.String r5 = r4.mime_type     // Catch:{ Exception -> 0x1430 }
            r1.mime_type = r5     // Catch:{ Exception -> 0x1430 }
            byte[] r1 = r4.key     // Catch:{ Exception -> 0x1430 }
            if (r1 != 0) goto L_0x1388
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1255 }
            r1.<init>(r13)     // Catch:{ Exception -> 0x1255 }
            r1.originalPath = r2     // Catch:{ Exception -> 0x1255 }
            r1.sendEncryptedRequest = r10     // Catch:{ Exception -> 0x1255 }
            r4 = 2
            r1.type = r4     // Catch:{ Exception -> 0x1255 }
            r1.obj = r11     // Catch:{ Exception -> 0x1255 }
            if (r15 == 0) goto L_0x1360
            boolean r4 = r15.containsKey(r3)     // Catch:{ Exception -> 0x1255 }
            if (r4 == 0) goto L_0x1360
            java.lang.Object r3 = r15.get(r3)     // Catch:{ Exception -> 0x1255 }
            r1.parentObject = r3     // Catch:{ Exception -> 0x1255 }
            goto L_0x1362
        L_0x1360:
            r1.parentObject = r8     // Catch:{ Exception -> 0x1255 }
        L_0x1362:
            r1.encryptedChat = r9     // Catch:{ Exception -> 0x1255 }
            r3 = 1
            r1.performMediaUpload = r3     // Catch:{ Exception -> 0x1255 }
            r4 = r55
            if (r4 == 0) goto L_0x137b
            int r3 = r55.length()     // Catch:{ Exception -> 0x1255 }
            if (r3 <= 0) goto L_0x137b
            java.lang.String r3 = "http"
            boolean r3 = r4.startsWith(r3)     // Catch:{ Exception -> 0x1255 }
            if (r3 == 0) goto L_0x137b
            r1.httpLocation = r4     // Catch:{ Exception -> 0x1255 }
        L_0x137b:
            if (r6 == 0) goto L_0x137f
            r3 = 1
            goto L_0x1380
        L_0x137f:
            r3 = 0
        L_0x1380:
            r1.scheduled = r3     // Catch:{ Exception -> 0x1255 }
            r7.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1255 }
            r6 = r1
            goto L_0x142c
        L_0x1388:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1430 }
            r1.<init>()     // Catch:{ Exception -> 0x1430 }
            long r5 = r4.id     // Catch:{ Exception -> 0x1430 }
            r1.id = r5     // Catch:{ Exception -> 0x1430 }
            long r5 = r4.access_hash     // Catch:{ Exception -> 0x1430 }
            r1.access_hash = r5     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x1430 }
            byte[] r5 = r4.key     // Catch:{ Exception -> 0x1430 }
            r3.key = r5     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x1430 }
            byte[] r4 = r4.iv     // Catch:{ Exception -> 0x1430 }
            r3.iv = r4     // Catch:{ Exception -> 0x1430 }
            org.telegram.messenger.SecretChatHelper r3 = r43.getSecretChatHelper()     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner     // Catch:{ Exception -> 0x1430 }
            r5 = 0
            r46 = r3
            r47 = r10
            r48 = r4
            r49 = r9
            r50 = r1
            r51 = r5
            r52 = r11
            r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1430 }
            goto L_0x142a
        L_0x13ba:
            r44 = r12
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument     // Catch:{ Exception -> 0x1430 }
            r1.<init>()     // Catch:{ Exception -> 0x1430 }
            r10.media = r1     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            long r5 = r4.id     // Catch:{ Exception -> 0x1430 }
            r1.id = r5     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            int r3 = r4.date     // Catch:{ Exception -> 0x1430 }
            r1.date = r3     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            long r5 = r4.access_hash     // Catch:{ Exception -> 0x1430 }
            r1.access_hash = r5     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            java.lang.String r3 = r4.mime_type     // Catch:{ Exception -> 0x1430 }
            r1.mime_type = r3     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            int r3 = r4.size     // Catch:{ Exception -> 0x1430 }
            r1.size = r3     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            int r3 = r4.dc_id     // Catch:{ Exception -> 0x1430 }
            r1.dc_id = r3     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r4.attributes     // Catch:{ Exception -> 0x1430 }
            r1.attributes = r3     // Catch:{ Exception -> 0x1430 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r7.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x1430 }
            if (r1 == 0) goto L_0x13fc
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r3     // Catch:{ Exception -> 0x1430 }
            r3.thumb = r1     // Catch:{ Exception -> 0x1430 }
            goto L_0x1411
        L_0x13fc:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r3 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty     // Catch:{ Exception -> 0x1430 }
            r3.<init>()     // Catch:{ Exception -> 0x1430 }
            r1.thumb = r3     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r1.thumb     // Catch:{ Exception -> 0x1430 }
            java.lang.String r3 = "s"
            r1.type = r3     // Catch:{ Exception -> 0x1430 }
        L_0x1411:
            org.telegram.messenger.SecretChatHelper r1 = r43.getSecretChatHelper()     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner     // Catch:{ Exception -> 0x1430 }
            r4 = 0
            r5 = 0
            r46 = r1
            r47 = r10
            r48 = r3
            r49 = r9
            r50 = r4
            r51 = r5
            r52 = r11
            r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1430 }
        L_0x142a:
            r6 = r56
        L_0x142c:
            r1 = r64
            goto L_0x1552
        L_0x1430:
            r0 = move-exception
            r1 = r64
        L_0x1433:
            r2 = r0
            goto L_0x0cce
        L_0x1436:
            r13 = r53
            r4 = r55
            r44 = r12
            r8 = r18
            r2 = r20
            r3 = r27
            r18 = r5
            r5 = r1
            r1 = r64
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r6.sizes     // Catch:{ Exception -> 0x15af }
            r4 = 0
            java.lang.Object r12 = r12.get(r4)     // Catch:{ Exception -> 0x15af }
            org.telegram.tgnet.TLRPC$PhotoSize r12 = (org.telegram.tgnet.TLRPC.PhotoSize) r12     // Catch:{ Exception -> 0x15af }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r6.sizes     // Catch:{ Exception -> 0x15af }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r6.sizes     // Catch:{ Exception -> 0x15ab }
            int r1 = r1.size()     // Catch:{ Exception -> 0x15ab }
            r19 = 1
            int r1 = r1 + -1
            java.lang.Object r1 = r4.get(r1)     // Catch:{ Exception -> 0x15ab }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1     // Catch:{ Exception -> 0x15ab }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r12)     // Catch:{ Exception -> 0x15ab }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto     // Catch:{ Exception -> 0x15ab }
            r4.<init>()     // Catch:{ Exception -> 0x15ab }
            r10.media = r4     // Catch:{ Exception -> 0x15ab }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x15ab }
            r4.caption = r5     // Catch:{ Exception -> 0x15ab }
            byte[] r4 = r12.bytes     // Catch:{ Exception -> 0x15ab }
            if (r4 == 0) goto L_0x147f
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4     // Catch:{ Exception -> 0x1430 }
            byte[] r5 = r12.bytes     // Catch:{ Exception -> 0x1430 }
            r4.thumb = r5     // Catch:{ Exception -> 0x1430 }
            r19 = r6
            goto L_0x148a
        L_0x147f:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x15ab }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4     // Catch:{ Exception -> 0x15ab }
            r19 = r6
            r5 = 0
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x15ab }
            r4.thumb = r6     // Catch:{ Exception -> 0x15ab }
        L_0x148a:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x15ab }
            int r5 = r12.h     // Catch:{ Exception -> 0x15ab }
            r4.thumb_h = r5     // Catch:{ Exception -> 0x15ab }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x15ab }
            int r5 = r12.w     // Catch:{ Exception -> 0x15ab }
            r4.thumb_w = r5     // Catch:{ Exception -> 0x15ab }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x15ab }
            int r5 = r1.w     // Catch:{ Exception -> 0x15ab }
            r4.w = r5     // Catch:{ Exception -> 0x15ab }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x15ab }
            int r5 = r1.h     // Catch:{ Exception -> 0x15ab }
            r4.h = r5     // Catch:{ Exception -> 0x15ab }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x15ab }
            int r5 = r1.size     // Catch:{ Exception -> 0x15ab }
            r4.size = r5     // Catch:{ Exception -> 0x15ab }
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location     // Catch:{ Exception -> 0x15ab }
            byte[] r4 = r4.key     // Catch:{ Exception -> 0x15ab }
            if (r4 == 0) goto L_0x14ee
            int r4 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x14b3
            goto L_0x14ee
        L_0x14b3:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1430 }
            r3.<init>()     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location     // Catch:{ Exception -> 0x1430 }
            long r4 = r4.volume_id     // Catch:{ Exception -> 0x1430 }
            r3.id = r4     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location     // Catch:{ Exception -> 0x1430 }
            long r4 = r4.secret     // Catch:{ Exception -> 0x1430 }
            r3.access_hash = r4     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r1.location     // Catch:{ Exception -> 0x1430 }
            byte[] r5 = r5.key     // Catch:{ Exception -> 0x1430 }
            r4.key = r5     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.location     // Catch:{ Exception -> 0x1430 }
            byte[] r1 = r1.iv     // Catch:{ Exception -> 0x1430 }
            r4.iv = r1     // Catch:{ Exception -> 0x1430 }
            org.telegram.messenger.SecretChatHelper r1 = r43.getSecretChatHelper()     // Catch:{ Exception -> 0x1430 }
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner     // Catch:{ Exception -> 0x1430 }
            r5 = 0
            r46 = r1
            r47 = r10
            r48 = r4
            r49 = r9
            r50 = r3
            r51 = r5
            r52 = r11
            r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1430 }
            goto L_0x142a
        L_0x14ee:
            if (r56 != 0) goto L_0x151e
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1430 }
            r6.<init>(r13)     // Catch:{ Exception -> 0x1430 }
            r6.encryptedChat = r9     // Catch:{ Exception -> 0x1430 }
            r1 = 0
            r6.type = r1     // Catch:{ Exception -> 0x1430 }
            r6.originalPath = r2     // Catch:{ Exception -> 0x1430 }
            r6.sendEncryptedRequest = r10     // Catch:{ Exception -> 0x1430 }
            r6.obj = r11     // Catch:{ Exception -> 0x1430 }
            if (r15 == 0) goto L_0x150f
            boolean r1 = r15.containsKey(r3)     // Catch:{ Exception -> 0x1430 }
            if (r1 == 0) goto L_0x150f
            java.lang.Object r1 = r15.get(r3)     // Catch:{ Exception -> 0x1430 }
            r6.parentObject = r1     // Catch:{ Exception -> 0x1430 }
            goto L_0x1511
        L_0x150f:
            r6.parentObject = r8     // Catch:{ Exception -> 0x1430 }
        L_0x1511:
            r1 = 1
            r6.performMediaUpload = r1     // Catch:{ Exception -> 0x1430 }
            r1 = r64
            if (r1 == 0) goto L_0x151a
            r3 = 1
            goto L_0x151b
        L_0x151a:
            r3 = 0
        L_0x151b:
            r6.scheduled = r3     // Catch:{ Exception -> 0x155f }
            goto L_0x1522
        L_0x151e:
            r1 = r64
            r6 = r56
        L_0x1522:
            boolean r3 = android.text.TextUtils.isEmpty(r55)     // Catch:{ Exception -> 0x15af }
            if (r3 != 0) goto L_0x1535
            java.lang.String r3 = "http"
            r4 = r55
            boolean r3 = r4.startsWith(r3)     // Catch:{ Exception -> 0x155f }
            if (r3 == 0) goto L_0x1535
            r6.httpLocation = r4     // Catch:{ Exception -> 0x155f }
            goto L_0x154b
        L_0x1535:
            r8 = r19
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r8.sizes     // Catch:{ Exception -> 0x15af }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r8.sizes     // Catch:{ Exception -> 0x15af }
            int r4 = r4.size()     // Catch:{ Exception -> 0x15af }
            r5 = 1
            int r4 = r4 - r5
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x15af }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3     // Catch:{ Exception -> 0x15af }
            r6.photoSize = r3     // Catch:{ Exception -> 0x15af }
            r6.locationParent = r8     // Catch:{ Exception -> 0x15af }
        L_0x154b:
            int r3 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x1552
            r7.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x155f }
        L_0x1552:
            int r3 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x159b
            org.telegram.tgnet.TLObject r3 = r6.sendEncryptedRequest     // Catch:{ Exception -> 0x15af }
            if (r3 == 0) goto L_0x1562
            org.telegram.tgnet.TLObject r3 = r6.sendEncryptedRequest     // Catch:{ Exception -> 0x155f }
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r3 = (org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r3     // Catch:{ Exception -> 0x155f }
            goto L_0x1569
        L_0x155f:
            r0 = move-exception
            goto L_0x1433
        L_0x1562:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia     // Catch:{ Exception -> 0x15af }
            r3.<init>()     // Catch:{ Exception -> 0x15af }
            r6.sendEncryptedRequest = r3     // Catch:{ Exception -> 0x15af }
        L_0x1569:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r6.messageObjects     // Catch:{ Exception -> 0x15af }
            r4.add(r11)     // Catch:{ Exception -> 0x15af }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r6.messages     // Catch:{ Exception -> 0x15af }
            r5 = r18
            r4.add(r5)     // Catch:{ Exception -> 0x1879 }
            java.util.ArrayList<java.lang.String> r4 = r6.originalPaths     // Catch:{ Exception -> 0x1879 }
            r4.add(r2)     // Catch:{ Exception -> 0x1879 }
            r2 = 1
            r6.performMediaUpload = r2     // Catch:{ Exception -> 0x1879 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r2 = r3.messages     // Catch:{ Exception -> 0x1879 }
            r2.add(r10)     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1879 }
            r2.<init>()     // Catch:{ Exception -> 0x1879 }
            r4 = r44
            r8 = 3
            if (r4 != r8) goto L_0x158e
            r16 = 1
        L_0x158e:
            r8 = r16
            r2.id = r8     // Catch:{ Exception -> 0x1879 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r3 = r3.files     // Catch:{ Exception -> 0x1879 }
            r3.add(r2)     // Catch:{ Exception -> 0x1879 }
            r7.performSendDelayedMessage(r6)     // Catch:{ Exception -> 0x1879 }
            goto L_0x159d
        L_0x159b:
            r5 = r18
        L_0x159d:
            r2 = r59
            if (r2 != 0) goto L_0x1876
            org.telegram.messenger.MediaDataController r2 = r43.getMediaDataController()     // Catch:{ Exception -> 0x1879 }
            r3 = 0
            r2.cleanDraft(r13, r3)     // Catch:{ Exception -> 0x1879 }
            goto L_0x1876
        L_0x15ab:
            r0 = move-exception
            r1 = r64
            goto L_0x15b0
        L_0x15af:
            r0 = move-exception
        L_0x15b0:
            r5 = r18
            goto L_0x1882
        L_0x15b4:
            r0 = move-exception
            r1 = r64
            goto L_0x1882
        L_0x15b9:
            r12 = r50
            r11 = r57
            r2 = r59
            r8 = r3
            r1 = r10
            r4 = r15
            r26 = r18
            r3 = 4
            r15 = r52
            if (r4 != r3) goto L_0x169d
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r3 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages     // Catch:{ Exception -> 0x1879 }
            r3.<init>()     // Catch:{ Exception -> 0x1879 }
            r3.to_peer = r8     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner     // Catch:{ Exception -> 0x1879 }
            boolean r4 = r4.with_my_score     // Catch:{ Exception -> 0x1879 }
            r3.with_my_score = r4     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner     // Catch:{ Exception -> 0x1879 }
            int r4 = r4.ttl     // Catch:{ Exception -> 0x1879 }
            if (r4 == 0) goto L_0x1606
            org.telegram.messenger.MessagesController r4 = r43.getMessagesController()     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner     // Catch:{ Exception -> 0x1879 }
            int r6 = r6.ttl     // Catch:{ Exception -> 0x1879 }
            int r6 = -r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r6)     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r6 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x1879 }
            r6.<init>()     // Catch:{ Exception -> 0x1879 }
            r3.from_peer = r6     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$InputPeer r6 = r3.from_peer     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$Message r8 = r2.messageOwner     // Catch:{ Exception -> 0x1879 }
            int r8 = r8.ttl     // Catch:{ Exception -> 0x1879 }
            int r8 = -r8
            r6.channel_id = r8     // Catch:{ Exception -> 0x1879 }
            if (r4 == 0) goto L_0x160d
            org.telegram.tgnet.TLRPC$InputPeer r6 = r3.from_peer     // Catch:{ Exception -> 0x1879 }
            long r8 = r4.access_hash     // Catch:{ Exception -> 0x1879 }
            r6.access_hash = r8     // Catch:{ Exception -> 0x1879 }
            goto L_0x160d
        L_0x1606:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x1879 }
            r4.<init>()     // Catch:{ Exception -> 0x1879 }
            r3.from_peer = r4     // Catch:{ Exception -> 0x1879 }
        L_0x160d:
            if (r63 == 0) goto L_0x162e
            int r4 = r7.currentAccount     // Catch:{ Exception -> 0x1879 }
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4)     // Catch:{ Exception -> 0x1879 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1879 }
            r6.<init>()     // Catch:{ Exception -> 0x1879 }
            r6.append(r15)     // Catch:{ Exception -> 0x1879 }
            r6.append(r13)     // Catch:{ Exception -> 0x1879 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x1879 }
            r8 = 0
            boolean r4 = r4.getBoolean(r6, r8)     // Catch:{ Exception -> 0x1879 }
            if (r4 == 0) goto L_0x162c
            goto L_0x162e
        L_0x162c:
            r4 = 0
            goto L_0x162f
        L_0x162e:
            r4 = 1
        L_0x162f:
            r3.silent = r4     // Catch:{ Exception -> 0x1879 }
            if (r1 == 0) goto L_0x163b
            r3.schedule_date = r1     // Catch:{ Exception -> 0x1879 }
            int r4 = r3.flags     // Catch:{ Exception -> 0x1879 }
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r3.flags = r4     // Catch:{ Exception -> 0x1879 }
        L_0x163b:
            java.util.ArrayList<java.lang.Long> r4 = r3.random_id     // Catch:{ Exception -> 0x1879 }
            long r8 = r5.random_id     // Catch:{ Exception -> 0x1879 }
            java.lang.Long r6 = java.lang.Long.valueOf(r8)     // Catch:{ Exception -> 0x1879 }
            r4.add(r6)     // Catch:{ Exception -> 0x1879 }
            int r4 = r59.getId()     // Catch:{ Exception -> 0x1879 }
            if (r4 < 0) goto L_0x165a
            java.util.ArrayList<java.lang.Integer> r4 = r3.id     // Catch:{ Exception -> 0x1879 }
            int r2 = r59.getId()     // Catch:{ Exception -> 0x1879 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x1879 }
            r4.add(r2)     // Catch:{ Exception -> 0x1879 }
            goto L_0x1683
        L_0x165a:
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner     // Catch:{ Exception -> 0x1879 }
            int r4 = r4.fwd_msg_id     // Catch:{ Exception -> 0x1879 }
            if (r4 == 0) goto L_0x166e
            java.util.ArrayList<java.lang.Integer> r4 = r3.id     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner     // Catch:{ Exception -> 0x1879 }
            int r2 = r2.fwd_msg_id     // Catch:{ Exception -> 0x1879 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x1879 }
            r4.add(r2)     // Catch:{ Exception -> 0x1879 }
            goto L_0x1683
        L_0x166e:
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r4.fwd_from     // Catch:{ Exception -> 0x1879 }
            if (r4 == 0) goto L_0x1683
            java.util.ArrayList<java.lang.Integer> r4 = r3.id     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from     // Catch:{ Exception -> 0x1879 }
            int r2 = r2.channel_post     // Catch:{ Exception -> 0x1879 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x1879 }
            r4.add(r2)     // Catch:{ Exception -> 0x1879 }
        L_0x1683:
            r2 = 0
            r4 = 0
            if (r1 == 0) goto L_0x1689
            r6 = 1
            goto L_0x168a
        L_0x1689:
            r6 = 0
        L_0x168a:
            r44 = r43
            r45 = r3
            r46 = r11
            r47 = r2
            r48 = r4
            r49 = r26
            r50 = r6
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50)     // Catch:{ Exception -> 0x1879 }
            goto L_0x1876
        L_0x169d:
            r3 = 9
            if (r4 != r3) goto L_0x1876
            org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult     // Catch:{ Exception -> 0x1879 }
            r3.<init>()     // Catch:{ Exception -> 0x1879 }
            r3.peer = r8     // Catch:{ Exception -> 0x1879 }
            long r8 = r5.random_id     // Catch:{ Exception -> 0x1879 }
            r3.random_id = r8     // Catch:{ Exception -> 0x1879 }
            java.lang.String r4 = "bot"
            boolean r4 = r12.containsKey(r4)     // Catch:{ Exception -> 0x1879 }
            if (r4 != 0) goto L_0x16b6
            r4 = 1
            goto L_0x16b7
        L_0x16b6:
            r4 = 0
        L_0x16b7:
            r3.hide_via = r4     // Catch:{ Exception -> 0x1879 }
            int r4 = r5.reply_to_msg_id     // Catch:{ Exception -> 0x1879 }
            if (r4 == 0) goto L_0x16c7
            int r4 = r3.flags     // Catch:{ Exception -> 0x1879 }
            r6 = 1
            r4 = r4 | r6
            r3.flags = r4     // Catch:{ Exception -> 0x1879 }
            int r4 = r5.reply_to_msg_id     // Catch:{ Exception -> 0x1879 }
            r3.reply_to_msg_id = r4     // Catch:{ Exception -> 0x1879 }
        L_0x16c7:
            if (r63 == 0) goto L_0x16e8
            int r4 = r7.currentAccount     // Catch:{ Exception -> 0x1879 }
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4)     // Catch:{ Exception -> 0x1879 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1879 }
            r6.<init>()     // Catch:{ Exception -> 0x1879 }
            r6.append(r15)     // Catch:{ Exception -> 0x1879 }
            r6.append(r13)     // Catch:{ Exception -> 0x1879 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x1879 }
            r8 = 0
            boolean r4 = r4.getBoolean(r6, r8)     // Catch:{ Exception -> 0x1879 }
            if (r4 == 0) goto L_0x16e6
            goto L_0x16e8
        L_0x16e6:
            r4 = 0
            goto L_0x16e9
        L_0x16e8:
            r4 = 1
        L_0x16e9:
            r3.silent = r4     // Catch:{ Exception -> 0x1879 }
            if (r1 == 0) goto L_0x16f5
            r3.schedule_date = r1     // Catch:{ Exception -> 0x1879 }
            int r4 = r3.flags     // Catch:{ Exception -> 0x1879 }
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r3.flags = r4     // Catch:{ Exception -> 0x1879 }
        L_0x16f5:
            r4 = r19
            java.lang.Object r4 = r12.get(r4)     // Catch:{ Exception -> 0x1879 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x1879 }
            java.lang.Long r4 = org.telegram.messenger.Utilities.parseLong(r4)     // Catch:{ Exception -> 0x1879 }
            long r8 = r4.longValue()     // Catch:{ Exception -> 0x1879 }
            r3.query_id = r8     // Catch:{ Exception -> 0x1879 }
            java.lang.String r4 = "id"
            java.lang.Object r4 = r12.get(r4)     // Catch:{ Exception -> 0x1879 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x1879 }
            r3.id = r4     // Catch:{ Exception -> 0x1879 }
            if (r2 != 0) goto L_0x171e
            r2 = 1
            r3.clear_draft = r2     // Catch:{ Exception -> 0x1879 }
            org.telegram.messenger.MediaDataController r2 = r43.getMediaDataController()     // Catch:{ Exception -> 0x1879 }
            r4 = 0
            r2.cleanDraft(r13, r4)     // Catch:{ Exception -> 0x1879 }
        L_0x171e:
            r2 = 0
            r4 = 0
            if (r1 == 0) goto L_0x1724
            r6 = 1
            goto L_0x1725
        L_0x1724:
            r6 = 0
        L_0x1725:
            r44 = r43
            r45 = r3
            r46 = r11
            r47 = r2
            r48 = r4
            r49 = r26
            r50 = r6
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50)     // Catch:{ Exception -> 0x1879 }
            goto L_0x1876
        L_0x1738:
            r12 = r50
            r15 = r52
            r11 = r57
            r2 = r59
            r8 = r3
            r1 = r10
            r26 = r18
            r3 = r60
            if (r9 != 0) goto L_0x17d4
            org.telegram.tgnet.TLRPC$TL_messages_sendMessage r4 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage     // Catch:{ Exception -> 0x1879 }
            r4.<init>()     // Catch:{ Exception -> 0x1879 }
            r6 = r49
            r4.message = r6     // Catch:{ Exception -> 0x1879 }
            if (r2 != 0) goto L_0x1755
            r6 = 1
            goto L_0x1756
        L_0x1755:
            r6 = 0
        L_0x1756:
            r4.clear_draft = r6     // Catch:{ Exception -> 0x1879 }
            if (r63 == 0) goto L_0x1779
            int r6 = r7.currentAccount     // Catch:{ Exception -> 0x1879 }
            android.content.SharedPreferences r6 = org.telegram.messenger.MessagesController.getNotificationsSettings(r6)     // Catch:{ Exception -> 0x1879 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1879 }
            r9.<init>()     // Catch:{ Exception -> 0x1879 }
            r9.append(r15)     // Catch:{ Exception -> 0x1879 }
            r9.append(r13)     // Catch:{ Exception -> 0x1879 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x1879 }
            r10 = 0
            boolean r6 = r6.getBoolean(r9, r10)     // Catch:{ Exception -> 0x1879 }
            if (r6 == 0) goto L_0x1777
            goto L_0x1779
        L_0x1777:
            r6 = 0
            goto L_0x177a
        L_0x1779:
            r6 = 1
        L_0x177a:
            r4.silent = r6     // Catch:{ Exception -> 0x1879 }
            r4.peer = r8     // Catch:{ Exception -> 0x1879 }
            long r8 = r5.random_id     // Catch:{ Exception -> 0x1879 }
            r4.random_id = r8     // Catch:{ Exception -> 0x1879 }
            int r6 = r5.reply_to_msg_id     // Catch:{ Exception -> 0x1879 }
            if (r6 == 0) goto L_0x1790
            int r6 = r4.flags     // Catch:{ Exception -> 0x1879 }
            r8 = 1
            r6 = r6 | r8
            r4.flags = r6     // Catch:{ Exception -> 0x1879 }
            int r6 = r5.reply_to_msg_id     // Catch:{ Exception -> 0x1879 }
            r4.reply_to_msg_id = r6     // Catch:{ Exception -> 0x1879 }
        L_0x1790:
            if (r58 != 0) goto L_0x1795
            r6 = 1
            r4.no_webpage = r6     // Catch:{ Exception -> 0x1879 }
        L_0x1795:
            if (r3 == 0) goto L_0x17a6
            boolean r6 = r60.isEmpty()     // Catch:{ Exception -> 0x1879 }
            if (r6 != 0) goto L_0x17a6
            r4.entities = r3     // Catch:{ Exception -> 0x1879 }
            int r3 = r4.flags     // Catch:{ Exception -> 0x1879 }
            r6 = 8
            r3 = r3 | r6
            r4.flags = r3     // Catch:{ Exception -> 0x1879 }
        L_0x17a6:
            if (r1 == 0) goto L_0x17b0
            r4.schedule_date = r1     // Catch:{ Exception -> 0x1879 }
            int r3 = r4.flags     // Catch:{ Exception -> 0x1879 }
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r4.flags = r3     // Catch:{ Exception -> 0x1879 }
        L_0x17b0:
            r3 = 0
            r6 = 0
            if (r1 == 0) goto L_0x17b6
            r8 = 1
            goto L_0x17b7
        L_0x17b6:
            r8 = 0
        L_0x17b7:
            r44 = r43
            r45 = r4
            r46 = r11
            r47 = r3
            r48 = r6
            r49 = r26
            r50 = r8
            r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50)     // Catch:{ Exception -> 0x1879 }
            if (r2 != 0) goto L_0x1876
            org.telegram.messenger.MediaDataController r2 = r43.getMediaDataController()     // Catch:{ Exception -> 0x1879 }
            r3 = 0
            r2.cleanDraft(r13, r3)     // Catch:{ Exception -> 0x1879 }
            goto L_0x1876
        L_0x17d4:
            r6 = r49
            int r4 = r9.layer     // Catch:{ Exception -> 0x1879 }
            int r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4)     // Catch:{ Exception -> 0x1879 }
            r8 = 73
            if (r4 < r8) goto L_0x17e6
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1879 }
            r4.<init>()     // Catch:{ Exception -> 0x1879 }
            goto L_0x17eb
        L_0x17e6:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x1879 }
            r4.<init>()     // Catch:{ Exception -> 0x1879 }
        L_0x17eb:
            int r8 = r5.ttl     // Catch:{ Exception -> 0x1879 }
            r4.ttl = r8     // Catch:{ Exception -> 0x1879 }
            if (r3 == 0) goto L_0x17ff
            boolean r8 = r60.isEmpty()     // Catch:{ Exception -> 0x1879 }
            if (r8 != 0) goto L_0x17ff
            r4.entities = r3     // Catch:{ Exception -> 0x1879 }
            int r3 = r4.flags     // Catch:{ Exception -> 0x1879 }
            r3 = r3 | 128(0x80, float:1.794E-43)
            r4.flags = r3     // Catch:{ Exception -> 0x1879 }
        L_0x17ff:
            long r7 = r5.reply_to_random_id     // Catch:{ Exception -> 0x1879 }
            int r3 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x1810
            long r7 = r5.reply_to_random_id     // Catch:{ Exception -> 0x1879 }
            r4.reply_to_random_id = r7     // Catch:{ Exception -> 0x1879 }
            int r3 = r4.flags     // Catch:{ Exception -> 0x1879 }
            r7 = 8
            r3 = r3 | r7
            r4.flags = r3     // Catch:{ Exception -> 0x1879 }
        L_0x1810:
            if (r12 == 0) goto L_0x182a
            java.lang.String r3 = "bot_name"
            java.lang.Object r3 = r12.get(r3)     // Catch:{ Exception -> 0x1879 }
            if (r3 == 0) goto L_0x182a
            java.lang.String r3 = "bot_name"
            java.lang.Object r3 = r12.get(r3)     // Catch:{ Exception -> 0x1879 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x1879 }
            r4.via_bot_name = r3     // Catch:{ Exception -> 0x1879 }
            int r3 = r4.flags     // Catch:{ Exception -> 0x1879 }
            r3 = r3 | 2048(0x800, float:2.87E-42)
            r4.flags = r3     // Catch:{ Exception -> 0x1879 }
        L_0x182a:
            long r7 = r5.random_id     // Catch:{ Exception -> 0x1879 }
            r4.random_id = r7     // Catch:{ Exception -> 0x1879 }
            r4.message = r6     // Catch:{ Exception -> 0x1879 }
            r3 = r32
            if (r3 == 0) goto L_0x184c
            java.lang.String r6 = r3.url     // Catch:{ Exception -> 0x1879 }
            if (r6 == 0) goto L_0x184c
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage     // Catch:{ Exception -> 0x1879 }
            r6.<init>()     // Catch:{ Exception -> 0x1879 }
            r4.media = r6     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r4.media     // Catch:{ Exception -> 0x1879 }
            java.lang.String r3 = r3.url     // Catch:{ Exception -> 0x1879 }
            r6.url = r3     // Catch:{ Exception -> 0x1879 }
            int r3 = r4.flags     // Catch:{ Exception -> 0x1879 }
            r3 = r3 | 512(0x200, float:7.175E-43)
            r4.flags = r3     // Catch:{ Exception -> 0x1879 }
            goto L_0x1853
        L_0x184c:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty     // Catch:{ Exception -> 0x1879 }
            r3.<init>()     // Catch:{ Exception -> 0x1879 }
            r4.media = r3     // Catch:{ Exception -> 0x1879 }
        L_0x1853:
            org.telegram.messenger.SecretChatHelper r3 = r43.getSecretChatHelper()     // Catch:{ Exception -> 0x1879 }
            org.telegram.tgnet.TLRPC$Message r6 = r11.messageOwner     // Catch:{ Exception -> 0x1879 }
            r7 = 0
            r8 = 0
            r46 = r3
            r47 = r4
            r48 = r6
            r49 = r9
            r50 = r7
            r51 = r8
            r52 = r11
            r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1879 }
            if (r2 != 0) goto L_0x1876
            org.telegram.messenger.MediaDataController r2 = r43.getMediaDataController()     // Catch:{ Exception -> 0x1879 }
            r3 = 0
            r2.cleanDraft(r13, r3)     // Catch:{ Exception -> 0x1879 }
        L_0x1876:
            r2 = r43
            goto L_0x18c9
        L_0x1879:
            r0 = move-exception
            goto L_0x1882
        L_0x187b:
            r0 = move-exception
            r11 = r57
            goto L_0x1881
        L_0x187f:
            r0 = move-exception
            r11 = r6
        L_0x1881:
            r1 = r10
        L_0x1882:
            r2 = r0
            goto L_0x1897
        L_0x1884:
            r0 = move-exception
            r1 = r10
            goto L_0x0262
        L_0x1888:
            r0 = move-exception
            r1 = r64
            r2 = r0
            r5 = r18
            goto L_0x0269
        L_0x1890:
            r0 = move-exception
            r1 = r64
            r18 = r5
            goto L_0x0262
        L_0x1897:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
            org.telegram.messenger.MessagesStorage r2 = r43.getMessagesStorage()
            if (r1 == 0) goto L_0x18a2
            r1 = 1
            goto L_0x18a3
        L_0x18a2:
            r1 = 0
        L_0x18a3:
            r2.markMessageAsSendError(r5, r1)
            if (r11 == 0) goto L_0x18ad
            org.telegram.tgnet.TLRPC$Message r1 = r11.messageOwner
            r2 = 2
            r1.send_state = r2
        L_0x18ad:
            org.telegram.messenger.NotificationCenter r1 = r43.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messageSendError
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r4 = r5.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r6 = 0
            r3[r6] = r4
            r1.postNotificationName(r2, r3)
            int r1 = r5.id
            r2 = r43
            r2.processSentMessage(r1)
        L_0x18c9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, org.telegram.tgnet.TLRPC$TL_messageMediaPoll, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, boolean, int, int, java.lang.Object):void");
    }

    private void performSendDelayedMessage(DelayedMessage delayedMessage) {
        performSendDelayedMessage(delayedMessage, -1);
    }

    private TLRPC.PhotoSize getThumbForSecretChat(ArrayList<TLRPC.PhotoSize> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                TLRPC.PhotoSize photoSize = arrayList.get(i);
                if (photoSize == null || (photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoSizeEmpty) || photoSize.location == null) {
                    i++;
                } else {
                    TLRPC.TL_photoSize tL_photoSize = new TLRPC.TL_photoSize();
                    tL_photoSize.type = photoSize.type;
                    tL_photoSize.w = photoSize.w;
                    tL_photoSize.h = photoSize.h;
                    tL_photoSize.size = photoSize.size;
                    tL_photoSize.bytes = photoSize.bytes;
                    if (tL_photoSize.bytes == null) {
                        tL_photoSize.bytes = new byte[0];
                    }
                    tL_photoSize.location = new TLRPC.TL_fileLocation_layer82();
                    TLRPC.FileLocation fileLocation = tL_photoSize.location;
                    TLRPC.FileLocation fileLocation2 = photoSize.location;
                    fileLocation.dc_id = fileLocation2.dc_id;
                    fileLocation.volume_id = fileLocation2.volume_id;
                    fileLocation.local_id = fileLocation2.local_id;
                    fileLocation.secret = fileLocation2.secret;
                    return tL_photoSize;
                }
            }
        }
        return null;
    }

    private void performSendDelayedMessage(DelayedMessage delayedMessage, int i) {
        boolean z;
        boolean z2;
        Object obj;
        MessageObject messageObject;
        TLRPC.InputMedia inputMedia;
        TLRPC.InputMedia inputMedia2;
        TLRPC.InputMedia inputMedia3;
        DelayedMessage delayedMessage2 = delayedMessage;
        int i2 = delayedMessage2.type;
        boolean z3 = false;
        boolean z4 = true;
        if (i2 == 0) {
            String str = delayedMessage2.httpLocation;
            if (str != null) {
                putToDelayedMessages(str, delayedMessage2);
                ImageLoader.getInstance().loadHttpFile(delayedMessage2.httpLocation, "file", this.currentAccount);
            } else if (delayedMessage2.sendRequest != null) {
                String file = FileLoader.getPathToAttach(delayedMessage2.photoSize).toString();
                putToDelayedMessages(file, delayedMessage2);
                getFileLoader().uploadFile(file, false, true, 16777216);
                putToUploadingMessages(delayedMessage2.obj);
            } else {
                String file2 = FileLoader.getPathToAttach(delayedMessage2.photoSize).toString();
                if (!(delayedMessage2.sendEncryptedRequest == null || delayedMessage2.photoSize.location.dc_id == 0)) {
                    File file3 = new File(file2);
                    if (!file3.exists()) {
                        file2 = FileLoader.getPathToAttach(delayedMessage2.photoSize, true).toString();
                        file3 = new File(file2);
                    }
                    if (!file3.exists()) {
                        putToDelayedMessages(FileLoader.getAttachFileName(delayedMessage2.photoSize), delayedMessage2);
                        getFileLoader().loadFile(ImageLocation.getForObject(delayedMessage2.photoSize, delayedMessage2.locationParent), delayedMessage2.parentObject, "jpg", 2, 0);
                        return;
                    }
                }
                putToDelayedMessages(file2, delayedMessage2);
                getFileLoader().uploadFile(file2, true, true, 16777216);
                putToUploadingMessages(delayedMessage2.obj);
            }
        } else if (i2 == 1) {
            VideoEditedInfo videoEditedInfo = delayedMessage2.videoEditedInfo;
            if (videoEditedInfo == null || !videoEditedInfo.needConvert()) {
                VideoEditedInfo videoEditedInfo2 = delayedMessage2.videoEditedInfo;
                if (videoEditedInfo2 != null) {
                    if (videoEditedInfo2.file != null) {
                        TLObject tLObject = delayedMessage2.sendRequest;
                        if (tLObject instanceof TLRPC.TL_messages_sendMedia) {
                            inputMedia3 = ((TLRPC.TL_messages_sendMedia) tLObject).media;
                        } else {
                            inputMedia3 = ((TLRPC.TL_messages_editMessage) tLObject).media;
                        }
                        VideoEditedInfo videoEditedInfo3 = delayedMessage2.videoEditedInfo;
                        inputMedia3.file = videoEditedInfo3.file;
                        videoEditedInfo3.file = null;
                    } else if (videoEditedInfo2.encryptedFile != null) {
                        TLRPC.TL_decryptedMessage tL_decryptedMessage = (TLRPC.TL_decryptedMessage) delayedMessage2.sendEncryptedRequest;
                        TLRPC.DecryptedMessageMedia decryptedMessageMedia = tL_decryptedMessage.media;
                        decryptedMessageMedia.size = (int) videoEditedInfo2.estimatedSize;
                        decryptedMessageMedia.key = videoEditedInfo2.key;
                        decryptedMessageMedia.iv = videoEditedInfo2.iv;
                        SecretChatHelper secretChatHelper = getSecretChatHelper();
                        MessageObject messageObject2 = delayedMessage2.obj;
                        secretChatHelper.performSendEncryptedRequest(tL_decryptedMessage, messageObject2.messageOwner, delayedMessage2.encryptedChat, delayedMessage2.videoEditedInfo.encryptedFile, delayedMessage2.originalPath, messageObject2);
                        delayedMessage2.videoEditedInfo.encryptedFile = null;
                        return;
                    }
                }
                TLObject tLObject2 = delayedMessage2.sendRequest;
                if (tLObject2 != null) {
                    if (tLObject2 instanceof TLRPC.TL_messages_sendMedia) {
                        inputMedia2 = ((TLRPC.TL_messages_sendMedia) tLObject2).media;
                    } else {
                        inputMedia2 = ((TLRPC.TL_messages_editMessage) tLObject2).media;
                    }
                    if (inputMedia2.file == null) {
                        MessageObject messageObject3 = delayedMessage2.obj;
                        String str2 = messageObject3.messageOwner.attachPath;
                        TLRPC.Document document = messageObject3.getDocument();
                        if (str2 == null) {
                            str2 = FileLoader.getDirectory(4) + "/" + document.id + ".mp4";
                        }
                        String str3 = str2;
                        putToDelayedMessages(str3, delayedMessage2);
                        VideoEditedInfo videoEditedInfo4 = delayedMessage2.obj.videoEditedInfo;
                        if (videoEditedInfo4 == null || !videoEditedInfo4.needConvert()) {
                            getFileLoader().uploadFile(str3, false, false, 33554432);
                        } else {
                            getFileLoader().uploadFile(str3, false, false, document.size, 33554432);
                        }
                        putToUploadingMessages(delayedMessage2.obj);
                        return;
                    }
                    String str4 = FileLoader.getDirectory(4) + "/" + delayedMessage2.photoSize.location.volume_id + "_" + delayedMessage2.photoSize.location.local_id + ".jpg";
                    putToDelayedMessages(str4, delayedMessage2);
                    getFileLoader().uploadFile(str4, false, true, 16777216);
                    putToUploadingMessages(delayedMessage2.obj);
                    return;
                }
                MessageObject messageObject4 = delayedMessage2.obj;
                String str5 = messageObject4.messageOwner.attachPath;
                TLRPC.Document document2 = messageObject4.getDocument();
                if (str5 == null) {
                    str5 = FileLoader.getDirectory(4) + "/" + document2.id + ".mp4";
                }
                String str6 = str5;
                if (delayedMessage2.sendEncryptedRequest == null || document2.dc_id == 0 || new File(str6).exists()) {
                    putToDelayedMessages(str6, delayedMessage2);
                    VideoEditedInfo videoEditedInfo5 = delayedMessage2.obj.videoEditedInfo;
                    if (videoEditedInfo5 == null || !videoEditedInfo5.needConvert()) {
                        getFileLoader().uploadFile(str6, true, false, 33554432);
                    } else {
                        getFileLoader().uploadFile(str6, true, false, document2.size, 33554432);
                    }
                    putToUploadingMessages(delayedMessage2.obj);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(document2), delayedMessage2);
                getFileLoader().loadFile(document2, delayedMessage2.parentObject, 2, 0);
                return;
            }
            MessageObject messageObject5 = delayedMessage2.obj;
            String str7 = messageObject5.messageOwner.attachPath;
            TLRPC.Document document3 = messageObject5.getDocument();
            if (str7 == null) {
                str7 = FileLoader.getDirectory(4) + "/" + document3.id + ".mp4";
            }
            putToDelayedMessages(str7, delayedMessage2);
            MediaController.getInstance().scheduleVideoConvert(delayedMessage2.obj);
            putToUploadingMessages(delayedMessage2.obj);
        } else if (i2 == 2) {
            String str8 = delayedMessage2.httpLocation;
            if (str8 != null) {
                putToDelayedMessages(str8, delayedMessage2);
                ImageLoader.getInstance().loadHttpFile(delayedMessage2.httpLocation, "gif", this.currentAccount);
                return;
            }
            TLObject tLObject3 = delayedMessage2.sendRequest;
            if (tLObject3 != null) {
                if (tLObject3 instanceof TLRPC.TL_messages_sendMedia) {
                    inputMedia = ((TLRPC.TL_messages_sendMedia) tLObject3).media;
                } else {
                    inputMedia = ((TLRPC.TL_messages_editMessage) tLObject3).media;
                }
                if (inputMedia.file == null) {
                    String str9 = delayedMessage2.obj.messageOwner.attachPath;
                    putToDelayedMessages(str9, delayedMessage2);
                    FileLoader fileLoader = getFileLoader();
                    if (delayedMessage2.sendRequest != null) {
                        z4 = false;
                    }
                    fileLoader.uploadFile(str9, z4, false, 67108864);
                    putToUploadingMessages(delayedMessage2.obj);
                } else if (inputMedia.thumb == null && delayedMessage2.photoSize != null) {
                    String str10 = FileLoader.getDirectory(4) + "/" + delayedMessage2.photoSize.location.volume_id + "_" + delayedMessage2.photoSize.location.local_id + ".jpg";
                    putToDelayedMessages(str10, delayedMessage2);
                    getFileLoader().uploadFile(str10, false, true, 16777216);
                    putToUploadingMessages(delayedMessage2.obj);
                }
            } else {
                MessageObject messageObject6 = delayedMessage2.obj;
                String str11 = messageObject6.messageOwner.attachPath;
                TLRPC.Document document4 = messageObject6.getDocument();
                if (delayedMessage2.sendEncryptedRequest == null || document4.dc_id == 0 || new File(str11).exists()) {
                    putToDelayedMessages(str11, delayedMessage2);
                    getFileLoader().uploadFile(str11, true, false, 67108864);
                    putToUploadingMessages(delayedMessage2.obj);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(document4), delayedMessage2);
                getFileLoader().loadFile(document4, delayedMessage2.parentObject, 2, 0);
            }
        } else if (i2 == 3) {
            String str12 = delayedMessage2.obj.messageOwner.attachPath;
            putToDelayedMessages(str12, delayedMessage2);
            FileLoader fileLoader2 = getFileLoader();
            if (delayedMessage2.sendRequest == null) {
                z3 = true;
            }
            fileLoader2.uploadFile(str12, z3, true, 50331648);
            putToUploadingMessages(delayedMessage2.obj);
        } else if (i2 == 4) {
            boolean z5 = i < 0;
            if (delayedMessage2.performMediaUpload) {
                int size = i < 0 ? delayedMessage2.messageObjects.size() - 1 : i;
                MessageObject messageObject7 = delayedMessage2.messageObjects.get(size);
                if (messageObject7.getDocument() != null) {
                    if (delayedMessage2.videoEditedInfo != null) {
                        String str13 = messageObject7.messageOwner.attachPath;
                        TLRPC.Document document5 = messageObject7.getDocument();
                        if (str13 == null) {
                            str13 = FileLoader.getDirectory(4) + "/" + document5.id + ".mp4";
                        }
                        putToDelayedMessages(str13, delayedMessage2);
                        delayedMessage2.extraHashMap.put(messageObject7, str13);
                        delayedMessage2.extraHashMap.put(str13 + "_i", messageObject7);
                        TLRPC.PhotoSize photoSize = delayedMessage2.photoSize;
                        if (!(photoSize == null || photoSize.location == null)) {
                            delayedMessage2.extraHashMap.put(str13 + "_t", delayedMessage2.photoSize);
                        }
                        MediaController.getInstance().scheduleVideoConvert(messageObject7);
                        delayedMessage2.obj = messageObject7;
                        putToUploadingMessages(messageObject7);
                    } else {
                        TLRPC.Document document6 = messageObject7.getDocument();
                        String str14 = messageObject7.messageOwner.attachPath;
                        if (str14 == null) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(FileLoader.getDirectory(4));
                            sb.append("/");
                            messageObject = messageObject7;
                            sb.append(document6.id);
                            sb.append(".mp4");
                            str14 = sb.toString();
                        } else {
                            messageObject = messageObject7;
                        }
                        TLObject tLObject4 = delayedMessage2.sendRequest;
                        if (tLObject4 != null) {
                            TLRPC.InputMedia inputMedia4 = ((TLRPC.TL_messages_sendMultiMedia) tLObject4).multi_media.get(size).media;
                            if (inputMedia4.file == null) {
                                putToDelayedMessages(str14, delayedMessage2);
                                MessageObject messageObject8 = messageObject;
                                delayedMessage2.extraHashMap.put(messageObject8, str14);
                                delayedMessage2.extraHashMap.put(str14, inputMedia4);
                                delayedMessage2.extraHashMap.put(str14 + "_i", messageObject8);
                                TLRPC.PhotoSize photoSize2 = delayedMessage2.photoSize;
                                if (!(photoSize2 == null || photoSize2.location == null)) {
                                    delayedMessage2.extraHashMap.put(str14 + "_t", delayedMessage2.photoSize);
                                }
                                VideoEditedInfo videoEditedInfo6 = messageObject8.videoEditedInfo;
                                if (videoEditedInfo6 == null || !videoEditedInfo6.needConvert()) {
                                    getFileLoader().uploadFile(str14, false, false, 33554432);
                                } else {
                                    getFileLoader().uploadFile(str14, false, false, document6.size, 33554432);
                                }
                                putToUploadingMessages(messageObject8);
                            } else {
                                MessageObject messageObject9 = messageObject;
                                String str15 = FileLoader.getDirectory(4) + "/" + delayedMessage2.photoSize.location.volume_id + "_" + delayedMessage2.photoSize.location.local_id + ".jpg";
                                putToDelayedMessages(str15, delayedMessage2);
                                delayedMessage2.extraHashMap.put(str15 + "_o", str14);
                                delayedMessage2.extraHashMap.put(messageObject9, str15);
                                delayedMessage2.extraHashMap.put(str15, inputMedia4);
                                getFileLoader().uploadFile(str15, false, true, 16777216);
                                putToUploadingMessages(messageObject9);
                            }
                        } else {
                            MessageObject messageObject10 = messageObject;
                            putToDelayedMessages(str14, delayedMessage2);
                            delayedMessage2.extraHashMap.put(messageObject10, str14);
                            delayedMessage2.extraHashMap.put(str14, ((TLRPC.TL_messages_sendEncryptedMultiMedia) delayedMessage2.sendEncryptedRequest).files.get(size));
                            delayedMessage2.extraHashMap.put(str14 + "_i", messageObject10);
                            TLRPC.PhotoSize photoSize3 = delayedMessage2.photoSize;
                            if (!(photoSize3 == null || photoSize3.location == null)) {
                                delayedMessage2.extraHashMap.put(str14 + "_t", delayedMessage2.photoSize);
                            }
                            VideoEditedInfo videoEditedInfo7 = messageObject10.videoEditedInfo;
                            if (videoEditedInfo7 == null || !videoEditedInfo7.needConvert()) {
                                getFileLoader().uploadFile(str14, true, false, 33554432);
                            } else {
                                getFileLoader().uploadFile(str14, true, false, document6.size, 33554432);
                            }
                            putToUploadingMessages(messageObject10);
                        }
                    }
                    delayedMessage2.videoEditedInfo = null;
                    delayedMessage2.photoSize = null;
                } else {
                    String str16 = delayedMessage2.httpLocation;
                    if (str16 != null) {
                        putToDelayedMessages(str16, delayedMessage2);
                        delayedMessage2.extraHashMap.put(messageObject7, delayedMessage2.httpLocation);
                        delayedMessage2.extraHashMap.put(delayedMessage2.httpLocation, messageObject7);
                        ImageLoader.getInstance().loadHttpFile(delayedMessage2.httpLocation, "file", this.currentAccount);
                        delayedMessage2.httpLocation = null;
                    } else {
                        TLObject tLObject5 = delayedMessage2.sendRequest;
                        if (tLObject5 != null) {
                            obj = ((TLRPC.TL_messages_sendMultiMedia) tLObject5).multi_media.get(size).media;
                        } else {
                            obj = ((TLRPC.TL_messages_sendEncryptedMultiMedia) delayedMessage2.sendEncryptedRequest).files.get(size);
                        }
                        String file4 = FileLoader.getPathToAttach(delayedMessage2.photoSize).toString();
                        putToDelayedMessages(file4, delayedMessage2);
                        delayedMessage2.extraHashMap.put(file4, obj);
                        delayedMessage2.extraHashMap.put(messageObject7, file4);
                        z = true;
                        getFileLoader().uploadFile(file4, delayedMessage2.sendEncryptedRequest != null, true, 16777216);
                        putToUploadingMessages(messageObject7);
                        delayedMessage2.photoSize = null;
                        z2 = false;
                        delayedMessage2.performMediaUpload = z2;
                    }
                }
                z2 = false;
                z = true;
                delayedMessage2.performMediaUpload = z2;
            } else {
                boolean z6 = false;
                z = true;
                if (!delayedMessage2.messageObjects.isEmpty()) {
                    ArrayList<MessageObject> arrayList = delayedMessage2.messageObjects;
                    TLRPC.Message message = arrayList.get(arrayList.size() - 1).messageOwner;
                    if (delayedMessage2.finalGroupMessage != 0) {
                        z6 = true;
                    }
                    putToSendingMessages(message, z6);
                }
            }
            sendReadyToSendGroup(delayedMessage2, z5, z);
        }
    }

    private void uploadMultiMedia(DelayedMessage delayedMessage, TLRPC.InputMedia inputMedia, TLRPC.InputEncryptedFile inputEncryptedFile, String str) {
        if (inputMedia != null) {
            TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TLRPC.TL_messages_sendMultiMedia) delayedMessage.sendRequest;
            int i = 0;
            while (true) {
                if (i >= tL_messages_sendMultiMedia.multi_media.size()) {
                    break;
                } else if (tL_messages_sendMultiMedia.multi_media.get(i).media == inputMedia) {
                    putToSendingMessages(delayedMessage.messages.get(i), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, -1L, -1L, false);
                    break;
                } else {
                    i++;
                }
            }
            TLRPC.TL_messages_uploadMedia tL_messages_uploadMedia = new TLRPC.TL_messages_uploadMedia();
            tL_messages_uploadMedia.media = inputMedia;
            tL_messages_uploadMedia.peer = ((TLRPC.TL_messages_sendMultiMedia) delayedMessage.sendRequest).peer;
            getConnectionsManager().sendRequest(tL_messages_uploadMedia, new RequestDelegate(inputMedia, delayedMessage) {
                private final /* synthetic */ TLRPC.InputMedia f$1;
                private final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$uploadMultiMedia$27$SendMessagesHelper(this.f$1, this.f$2, tLObject, tL_error);
                }
            });
        } else if (inputEncryptedFile != null) {
            TLRPC.TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TLRPC.TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
            int i2 = 0;
            while (true) {
                if (i2 >= tL_messages_sendEncryptedMultiMedia.files.size()) {
                    break;
                } else if (tL_messages_sendEncryptedMultiMedia.files.get(i2) == inputEncryptedFile) {
                    putToSendingMessages(delayedMessage.messages.get(i2), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, -1L, -1L, false);
                    break;
                } else {
                    i2++;
                }
            }
            sendReadyToSendGroup(delayedMessage, false, true);
        }
    }

    public /* synthetic */ void lambda$uploadMultiMedia$27$SendMessagesHelper(TLRPC.InputMedia inputMedia, DelayedMessage delayedMessage, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, inputMedia, delayedMessage) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC.InputMedia f$2;
            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$26$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$26$SendMessagesHelper(org.telegram.tgnet.TLObject r6, org.telegram.tgnet.TLRPC.InputMedia r7, org.telegram.messenger.SendMessagesHelper.DelayedMessage r8) {
        /*
            r5 = this;
            if (r6 == 0) goto L_0x004e
            org.telegram.tgnet.TLRPC$MessageMedia r6 = (org.telegram.tgnet.TLRPC.MessageMedia) r6
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedPhoto
            if (r0 == 0) goto L_0x0029
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0029
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputPhoto
            r1.<init>()
            r0.id = r1
            org.telegram.tgnet.TLRPC$InputPhoto r1 = r0.id
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            long r2 = r6.id
            r1.id = r2
            long r2 = r6.access_hash
            r1.access_hash = r2
            byte[] r6 = r6.file_reference
            r1.file_reference = r6
            goto L_0x004f
        L_0x0029:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument
            if (r0 == 0) goto L_0x004e
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r0 == 0) goto L_0x004e
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputDocument
            r1.<init>()
            r0.id = r1
            org.telegram.tgnet.TLRPC$InputDocument r1 = r0.id
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            long r2 = r6.id
            r1.id = r2
            long r2 = r6.access_hash
            r1.access_hash = r2
            byte[] r6 = r6.file_reference
            r1.file_reference = r6
            goto L_0x004f
        L_0x004e:
            r0 = 0
        L_0x004f:
            if (r0 == 0) goto L_0x0089
            int r6 = r7.ttl_seconds
            r1 = 1
            if (r6 == 0) goto L_0x005d
            r0.ttl_seconds = r6
            int r6 = r0.flags
            r6 = r6 | r1
            r0.flags = r6
        L_0x005d:
            org.telegram.tgnet.TLObject r6 = r8.sendRequest
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r6 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r6
            r2 = 0
            r3 = 0
        L_0x0063:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r6.multi_media
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0085
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r6.multi_media
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r4 = (org.telegram.tgnet.TLRPC.TL_inputSingleMedia) r4
            org.telegram.tgnet.TLRPC$InputMedia r4 = r4.media
            if (r4 != r7) goto L_0x0082
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r6 = r6.multi_media
            java.lang.Object r6 = r6.get(r3)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r6 = (org.telegram.tgnet.TLRPC.TL_inputSingleMedia) r6
            r6.media = r0
            goto L_0x0085
        L_0x0082:
            int r3 = r3 + 1
            goto L_0x0063
        L_0x0085:
            r5.sendReadyToSendGroup(r8, r2, r1)
            goto L_0x008c
        L_0x0089:
            r8.markAsError()
        L_0x008c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$26$SendMessagesHelper(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$InputMedia, org.telegram.messenger.SendMessagesHelper$DelayedMessage):void");
    }

    private void sendReadyToSendGroup(DelayedMessage delayedMessage, boolean z, boolean z2) {
        DelayedMessage findMaxDelayedMessageForMessageId;
        if (delayedMessage.messageObjects.isEmpty()) {
            delayedMessage.markAsError();
            return;
        }
        String str = "group_" + delayedMessage.groupId;
        int i = delayedMessage.finalGroupMessage;
        ArrayList<MessageObject> arrayList = delayedMessage.messageObjects;
        if (i == arrayList.get(arrayList.size() - 1).getId()) {
            int i2 = 0;
            if (z) {
                this.delayedMessages.remove(str);
                getMessagesStorage().putMessages(delayedMessage.messages, false, true, false, 0, delayedMessage.scheduled);
                getMessagesController().updateInterfaceWithMessages(delayedMessage.peer, delayedMessage.messageObjects, delayedMessage.scheduled);
                if (!delayedMessage.scheduled) {
                    getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
            TLObject tLObject = delayedMessage.sendRequest;
            if (tLObject instanceof TLRPC.TL_messages_sendMultiMedia) {
                TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TLRPC.TL_messages_sendMultiMedia) tLObject;
                while (i2 < tL_messages_sendMultiMedia.multi_media.size()) {
                    TLRPC.InputMedia inputMedia = tL_messages_sendMultiMedia.multi_media.get(i2).media;
                    if (!(inputMedia instanceof TLRPC.TL_inputMediaUploadedPhoto) && !(inputMedia instanceof TLRPC.TL_inputMediaUploadedDocument)) {
                        i2++;
                    } else {
                        return;
                    }
                }
                if (z2 && (findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(delayedMessage.finalGroupMessage, delayedMessage.peer)) != null) {
                    findMaxDelayedMessageForMessageId.addDelayedRequest(delayedMessage.sendRequest, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage, delayedMessage.scheduled);
                    ArrayList<DelayedMessageSendAfterRequest> arrayList2 = delayedMessage.requests;
                    if (arrayList2 != null) {
                        findMaxDelayedMessageForMessageId.requests.addAll(arrayList2);
                        return;
                    }
                    return;
                }
            } else {
                TLRPC.TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TLRPC.TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
                while (i2 < tL_messages_sendEncryptedMultiMedia.files.size()) {
                    if (!(tL_messages_sendEncryptedMultiMedia.files.get(i2) instanceof TLRPC.TL_inputEncryptedFile)) {
                        i2++;
                    } else {
                        return;
                    }
                }
            }
            TLObject tLObject2 = delayedMessage.sendRequest;
            if (tLObject2 instanceof TLRPC.TL_messages_sendMultiMedia) {
                performSendMessageRequestMulti((TLRPC.TL_messages_sendMultiMedia) tLObject2, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage, delayedMessage.scheduled);
            } else {
                getSecretChatHelper().performSendEncryptedRequest((TLRPC.TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest, delayedMessage);
            }
            delayedMessage.sendDelayedRequests();
        } else if (z) {
            putToDelayedMessages(str, delayedMessage);
        }
    }

    public /* synthetic */ void lambda$null$28$SendMessagesHelper(String str) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, str, Integer.valueOf(this.currentAccount));
    }

    public /* synthetic */ void lambda$stopVideoService$29$SendMessagesHelper(String str) {
        AndroidUtilities.runOnUIThread(new Runnable(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$28$SendMessagesHelper(this.f$1);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void stopVideoService(String str) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$stopVideoService$29$SendMessagesHelper(this.f$1);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void putToSendingMessages(TLRPC.Message message, boolean z) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new Runnable(message, z) {
                private final /* synthetic */ TLRPC.Message f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$putToSendingMessages$30$SendMessagesHelper(this.f$1, this.f$2);
                }
            });
        } else {
            putToSendingMessages(message, z, true);
        }
    }

    public /* synthetic */ void lambda$putToSendingMessages$30$SendMessagesHelper(TLRPC.Message message, boolean z) {
        putToSendingMessages(message, z, true);
    }

    /* access modifiers changed from: protected */
    public void putToSendingMessages(TLRPC.Message message, boolean z, boolean z2) {
        if (message != null) {
            int i = message.id;
            if (i > 0) {
                this.editingMessages.put(i, message);
                return;
            }
            boolean z3 = this.sendingMessages.indexOfKey(i) >= 0;
            removeFromUploadingMessages(message.id, z);
            this.sendingMessages.put(message.id, message);
            if (!z && !z3) {
                long dialogId = MessageObject.getDialogId(message);
                LongSparseArray<Integer> longSparseArray = this.sendingMessagesIdDialogs;
                longSparseArray.put(dialogId, Integer.valueOf(longSparseArray.get(dialogId, 0).intValue() + 1));
                if (z2) {
                    getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public TLRPC.Message removeFromSendingMessages(int i, boolean z) {
        long dialogId;
        Integer num;
        if (i > 0) {
            TLRPC.Message message = this.editingMessages.get(i);
            if (message == null) {
                return message;
            }
            this.editingMessages.remove(i);
            return message;
        }
        TLRPC.Message message2 = this.sendingMessages.get(i);
        if (message2 != null) {
            this.sendingMessages.remove(i);
            if (!z && (num = this.sendingMessagesIdDialogs.get(dialogId)) != null) {
                int intValue = num.intValue() - 1;
                if (intValue <= 0) {
                    this.sendingMessagesIdDialogs.remove(dialogId);
                } else {
                    this.sendingMessagesIdDialogs.put((dialogId = MessageObject.getDialogId(message2)), Integer.valueOf(intValue));
                }
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
        return message2;
    }

    public int getSendingMessageId(long j) {
        for (int i = 0; i < this.sendingMessages.size(); i++) {
            TLRPC.Message valueAt = this.sendingMessages.valueAt(i);
            if (valueAt.dialog_id == j) {
                return valueAt.id;
            }
        }
        for (int i2 = 0; i2 < this.uploadMessages.size(); i2++) {
            TLRPC.Message valueAt2 = this.uploadMessages.valueAt(i2);
            if (valueAt2.dialog_id == j) {
                return valueAt2.id;
            }
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void putToUploadingMessages(MessageObject messageObject) {
        if (messageObject != null && messageObject.getId() <= 0 && !messageObject.scheduled) {
            TLRPC.Message message = messageObject.messageOwner;
            boolean z = this.uploadMessages.indexOfKey(message.id) >= 0;
            this.uploadMessages.put(message.id, message);
            if (!z) {
                long dialogId = MessageObject.getDialogId(message);
                LongSparseArray<Integer> longSparseArray = this.uploadingMessagesIdDialogs;
                longSparseArray.put(dialogId, Integer.valueOf(longSparseArray.get(dialogId, 0).intValue() + 1));
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void removeFromUploadingMessages(int i, boolean z) {
        TLRPC.Message message;
        if (i <= 0 && !z && (message = this.uploadMessages.get(i)) != null) {
            this.uploadMessages.remove(i);
            long dialogId = MessageObject.getDialogId(message);
            Integer num = this.uploadingMessagesIdDialogs.get(dialogId);
            if (num != null) {
                int intValue = num.intValue() - 1;
                if (intValue <= 0) {
                    this.uploadingMessagesIdDialogs.remove(dialogId);
                } else {
                    this.uploadingMessagesIdDialogs.put(dialogId, Integer.valueOf(intValue));
                }
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
    }

    public boolean isSendingMessage(int i) {
        return this.sendingMessages.indexOfKey(i) >= 0 || this.editingMessages.indexOfKey(i) >= 0;
    }

    public boolean isSendingMessageIdDialog(long j) {
        return this.sendingMessagesIdDialogs.get(j, 0).intValue() > 0;
    }

    public boolean isUploadingMessageIdDialog(long j) {
        return this.uploadingMessagesIdDialogs.get(j, 0).intValue() > 0;
    }

    /* access modifiers changed from: protected */
    public void performSendMessageRequestMulti(TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList<MessageObject> arrayList, ArrayList<String> arrayList2, ArrayList<Object> arrayList3, DelayedMessage delayedMessage, boolean z) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ArrayList<MessageObject> arrayList4 = arrayList;
            putToSendingMessages(arrayList.get(i).messageOwner, z);
        }
        ConnectionsManager connectionsManager = getConnectionsManager();
        $$Lambda$SendMessagesHelper$ZYsiGPlj9hRHiRl2LlxJG53Fm0s r2 = new RequestDelegate(arrayList3, tL_messages_sendMultiMedia, arrayList, arrayList2, delayedMessage, z) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ SendMessagesHelper.DelayedMessage f$5;
            private final /* synthetic */ boolean f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                SendMessagesHelper.this.lambda$performSendMessageRequestMulti$38$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
            }
        };
        TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia2 = tL_messages_sendMultiMedia;
        connectionsManager.sendRequest((TLObject) tL_messages_sendMultiMedia, (RequestDelegate) r2, (QuickAckDelegate) null, 68);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$38$SendMessagesHelper(ArrayList arrayList, TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        ArrayList arrayList4 = arrayList;
        TLRPC.TL_error tL_error2 = tL_error;
        if (tL_error2 != null && FileRefController.isFileRefError(tL_error2.text)) {
            if (arrayList4 != null) {
                ArrayList arrayList5 = new ArrayList(arrayList);
                getFileRefController().requestReference(arrayList5, tL_messages_sendMultiMedia, arrayList2, arrayList3, arrayList5, delayedMessage, Boolean.valueOf(z));
                return;
            } else if (delayedMessage != null) {
                AndroidUtilities.runOnUIThread(new Runnable(tL_messages_sendMultiMedia, delayedMessage, arrayList2, z) {
                    private final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$1;
                    private final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;
                    private final /* synthetic */ ArrayList f$3;
                    private final /* synthetic */ boolean f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$null$31$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject, arrayList2, arrayList3, z, tL_messages_sendMultiMedia) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$37$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$31$SendMessagesHelper(TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, DelayedMessage delayedMessage, ArrayList arrayList, boolean z) {
        int size = tL_messages_sendMultiMedia.multi_media.size();
        for (int i = 0; i < size; i++) {
            if (delayedMessage.parentObjects.get(i) != null) {
                removeFromSendingMessages(((MessageObject) arrayList.get(i)).getId(), z);
                TLRPC.TL_inputSingleMedia tL_inputSingleMedia = tL_messages_sendMultiMedia.multi_media.get(i);
                TLRPC.InputMedia inputMedia = tL_inputSingleMedia.media;
                if (inputMedia instanceof TLRPC.TL_inputMediaPhoto) {
                    tL_inputSingleMedia.media = delayedMessage.inputMedias.get(i);
                } else if (inputMedia instanceof TLRPC.TL_inputMediaDocument) {
                    tL_inputSingleMedia.media = delayedMessage.inputMedias.get(i);
                }
                delayedMessage.videoEditedInfo = delayedMessage.videoEditedInfos.get(i);
                delayedMessage.httpLocation = delayedMessage.httpLocations.get(i);
                delayedMessage.photoSize = delayedMessage.locations.get(i);
                delayedMessage.performMediaUpload = true;
                performSendDelayedMessage(delayedMessage, i);
            }
        }
    }

    public /* synthetic */ void lambda$null$37$SendMessagesHelper(TLRPC.TL_error tL_error, TLObject tLObject, ArrayList arrayList, ArrayList arrayList2, boolean z, TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia) {
        boolean z2;
        TLRPC.Updates updates;
        TLRPC.Message message;
        TLRPC.TL_error tL_error2 = tL_error;
        ArrayList arrayList3 = arrayList;
        boolean z3 = z;
        if (tL_error2 == null) {
            SparseArray sparseArray = new SparseArray();
            LongSparseArray longSparseArray = new LongSparseArray();
            TLRPC.Updates updates2 = (TLRPC.Updates) tLObject;
            ArrayList<TLRPC.Update> arrayList4 = updates2.updates;
            int i = 0;
            while (i < arrayList4.size()) {
                TLRPC.Update update = arrayList4.get(i);
                if (update instanceof TLRPC.TL_updateMessageID) {
                    TLRPC.TL_updateMessageID tL_updateMessageID = (TLRPC.TL_updateMessageID) update;
                    longSparseArray.put(tL_updateMessageID.random_id, Integer.valueOf(tL_updateMessageID.id));
                    arrayList4.remove(i);
                } else if (update instanceof TLRPC.TL_updateNewMessage) {
                    TLRPC.TL_updateNewMessage tL_updateNewMessage = (TLRPC.TL_updateNewMessage) update;
                    TLRPC.Message message2 = tL_updateNewMessage.message;
                    sparseArray.put(message2.id, message2);
                    Utilities.stageQueue.postRunnable(new Runnable(tL_updateNewMessage) {
                        private final /* synthetic */ TLRPC.TL_updateNewMessage f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$32$SendMessagesHelper(this.f$1);
                        }
                    });
                    arrayList4.remove(i);
                } else if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                    TLRPC.TL_updateNewChannelMessage tL_updateNewChannelMessage = (TLRPC.TL_updateNewChannelMessage) update;
                    TLRPC.Message message3 = tL_updateNewChannelMessage.message;
                    sparseArray.put(message3.id, message3);
                    Utilities.stageQueue.postRunnable(new Runnable(tL_updateNewChannelMessage) {
                        private final /* synthetic */ TLRPC.TL_updateNewChannelMessage f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$33$SendMessagesHelper(this.f$1);
                        }
                    });
                    arrayList4.remove(i);
                } else if (update instanceof TLRPC.TL_updateNewScheduledMessage) {
                    TLRPC.Message message4 = ((TLRPC.TL_updateNewScheduledMessage) update).message;
                    sparseArray.put(message4.id, message4);
                    arrayList4.remove(i);
                } else {
                    i++;
                }
                i--;
                i++;
            }
            int i2 = 0;
            while (true) {
                if (i2 >= arrayList.size()) {
                    updates = updates2;
                    z2 = false;
                    break;
                }
                MessageObject messageObject = (MessageObject) arrayList3.get(i2);
                String str = (String) arrayList2.get(i2);
                TLRPC.Message message5 = messageObject.messageOwner;
                int i3 = message5.id;
                ArrayList arrayList5 = new ArrayList();
                String str2 = message5.attachPath;
                Integer num = (Integer) longSparseArray.get(message5.random_id);
                if (num == null || (message = (TLRPC.Message) sparseArray.get(num.intValue())) == null) {
                    updates = updates2;
                    z2 = true;
                } else {
                    arrayList5.add(message);
                    ArrayList arrayList6 = arrayList5;
                    int i4 = i3;
                    TLRPC.Message message6 = message5;
                    updateMediaPaths(messageObject, message, message.id, str, false);
                    int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                    message6.id = message.id;
                    if ((message6.flags & Integer.MIN_VALUE) != 0) {
                        message.flags |= Integer.MIN_VALUE;
                    }
                    long j = message.grouped_id;
                    if (!z3) {
                        Integer num2 = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message.dialog_id));
                        if (num2 == null) {
                            num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(message.out, message.dialog_id));
                            getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message.dialog_id), num2);
                        }
                        message.unread = num2.intValue() < message.id;
                    }
                    getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                    message6.send_state = 0;
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i4), Integer.valueOf(message6.id), message6, Long.valueOf(message6.dialog_id), Long.valueOf(j), Integer.valueOf(mediaExistanceFlags), Boolean.valueOf(z));
                    DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                    SparseArray sparseArray2 = sparseArray;
                    $$Lambda$SendMessagesHelper$IfuL9XcMh89YHY1N1peNifOal8 r15 = r0;
                    $$Lambda$SendMessagesHelper$IfuL9XcMh89YHY1N1peNifOal8 r0 = new Runnable(message6, i4, z, arrayList6, j, mediaExistanceFlags) {
                        private final /* synthetic */ TLRPC.Message f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ boolean f$3;
                        private final /* synthetic */ ArrayList f$4;
                        private final /* synthetic */ long f$5;
                        private final /* synthetic */ int f$6;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r8;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$35$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                        }
                    };
                    storageQueue.postRunnable(r15);
                    i2++;
                    sparseArray = sparseArray2;
                    updates2 = updates2;
                    longSparseArray = longSparseArray;
                }
            }
            updates = updates2;
            z2 = true;
            Utilities.stageQueue.postRunnable(new Runnable(updates) {
                private final /* synthetic */ TLRPC.Updates f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$36$SendMessagesHelper(this.f$1);
                }
            });
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error2, (BaseFragment) null, tL_messages_sendMultiMedia, new Object[0]);
            z2 = true;
        }
        if (z2) {
            for (int i5 = 0; i5 < arrayList.size(); i5++) {
                TLRPC.Message message7 = ((MessageObject) arrayList3.get(i5)).messageOwner;
                getMessagesStorage().markMessageAsSendError(message7, z3);
                message7.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message7.id));
                processSentMessage(message7.id);
                removeFromSendingMessages(message7.id, z3);
            }
        }
    }

    public /* synthetic */ void lambda$null$32$SendMessagesHelper(TLRPC.TL_updateNewMessage tL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$33$SendMessagesHelper(TLRPC.TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$35$SendMessagesHelper(TLRPC.Message message, int i, boolean z, ArrayList arrayList, long j, int i2) {
        TLRPC.Message message2 = message;
        getMessagesStorage().updateMessageStateAndId(message2.random_id, Integer.valueOf(i), message2.id, 0, false, message2.to_id.channel_id, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable(message, i, j, i2, z) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ long f$3;
            private final /* synthetic */ int f$4;
            private final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$34$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$34$SendMessagesHelper(TLRPC.Message message, int i, long j, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), Long.valueOf(j), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$36$SendMessagesHelper(TLRPC.Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    /* access modifiers changed from: private */
    public void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, Object obj, boolean z) {
        performSendMessageRequest(tLObject, messageObject, str, (DelayedMessage) null, false, delayedMessage, obj, z);
    }

    private DelayedMessage findMaxDelayedMessageForMessageId(int i, long j) {
        int i2;
        DelayedMessage delayedMessage = null;
        int i3 = Integer.MIN_VALUE;
        for (Map.Entry<String, ArrayList<DelayedMessage>> value : this.delayedMessages.entrySet()) {
            ArrayList arrayList = (ArrayList) value.getValue();
            int size = arrayList.size();
            int i4 = i3;
            DelayedMessage delayedMessage2 = delayedMessage;
            for (int i5 = 0; i5 < size; i5++) {
                DelayedMessage delayedMessage3 = (DelayedMessage) arrayList.get(i5);
                int i6 = delayedMessage3.type;
                if ((i6 == 4 || i6 == 0) && delayedMessage3.peer == j) {
                    MessageObject messageObject = delayedMessage3.obj;
                    if (messageObject != null) {
                        i2 = messageObject.getId();
                    } else {
                        ArrayList<MessageObject> arrayList2 = delayedMessage3.messageObjects;
                        if (arrayList2 == null || arrayList2.isEmpty()) {
                            i2 = 0;
                        } else {
                            ArrayList<MessageObject> arrayList3 = delayedMessage3.messageObjects;
                            i2 = arrayList3.get(arrayList3.size() - 1).getId();
                        }
                    }
                    if (i2 != 0 && i2 > i && delayedMessage2 == null && i4 < i2) {
                        delayedMessage2 = delayedMessage3;
                        i4 = i2;
                    }
                }
            }
            delayedMessage = delayedMessage2;
            i3 = i4;
        }
        return delayedMessage;
    }

    /* access modifiers changed from: protected */
    public void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, Object obj, boolean z2) {
        DelayedMessage findMaxDelayedMessageForMessageId;
        ArrayList<DelayedMessageSendAfterRequest> arrayList;
        TLObject tLObject2 = tLObject;
        DelayedMessage delayedMessage3 = delayedMessage;
        if ((tLObject2 instanceof TLRPC.TL_messages_editMessage) || !z || (findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(messageObject.getId(), messageObject.getDialogId())) == null) {
            TLRPC.Message message = messageObject.messageOwner;
            boolean z3 = z2;
            putToSendingMessages(message, z3);
            $$Lambda$SendMessagesHelper$anaw0NqrAe1M6q_iXvar_cgrWPg r14 = r0;
            ConnectionsManager connectionsManager = getConnectionsManager();
            $$Lambda$SendMessagesHelper$anaw0NqrAe1M6q_iXvar_cgrWPg r0 = new RequestDelegate(tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, z3, message) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ Object f$2;
                private final /* synthetic */ MessageObject f$3;
                private final /* synthetic */ String f$4;
                private final /* synthetic */ SendMessagesHelper.DelayedMessage f$5;
                private final /* synthetic */ boolean f$6;
                private final /* synthetic */ SendMessagesHelper.DelayedMessage f$7;
                private final /* synthetic */ boolean f$8;
                private final /* synthetic */ TLRPC.Message f$9;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                    this.f$9 = r10;
                }

                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$52$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tL_error);
                }
            };
            message.reqId = connectionsManager.sendRequest(tLObject2, (RequestDelegate) r14, (QuickAckDelegate) new QuickAckDelegate(message) {
                private final /* synthetic */ TLRPC.Message f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$54$SendMessagesHelper(this.f$1);
                }
            }, (tLObject2 instanceof TLRPC.TL_messages_sendMessage ? 128 : 0) | 68);
            if (delayedMessage != null) {
                delayedMessage.sendDelayedRequests();
                return;
            }
            return;
        }
        findMaxDelayedMessageForMessageId.addDelayedRequest(tLObject, messageObject, str, obj, delayedMessage2, delayedMessage3 != null ? delayedMessage3.scheduled : false);
        if (delayedMessage3 != null && (arrayList = delayedMessage3.requests) != null) {
            findMaxDelayedMessageForMessageId.requests.addAll(arrayList);
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequest$52$SendMessagesHelper(TLObject tLObject, Object obj, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, boolean z2, TLRPC.Message message, TLObject tLObject2, TLRPC.TL_error tL_error) {
        TLObject tLObject3 = tLObject;
        Object obj2 = obj;
        TLRPC.TL_error tL_error2 = tL_error;
        if (tL_error2 != null && (((tLObject3 instanceof TLRPC.TL_messages_sendMedia) || (tLObject3 instanceof TLRPC.TL_messages_editMessage)) && FileRefController.isFileRefError(tL_error2.text))) {
            if (obj2 != null) {
                getFileRefController().requestReference(obj, tLObject3, messageObject, str, delayedMessage, Boolean.valueOf(z), delayedMessage2, Boolean.valueOf(z2));
                return;
            } else if (delayedMessage2 != null) {
                AndroidUtilities.runOnUIThread(new Runnable(message, z2, tLObject, delayedMessage2) {
                    private final /* synthetic */ TLRPC.Message f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ TLObject f$3;
                    private final /* synthetic */ SendMessagesHelper.DelayedMessage f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$null$39$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
                return;
            }
        }
        if (tLObject3 instanceof TLRPC.TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, message, tLObject2, messageObject, str, z2, tLObject) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLRPC.Message f$2;
                private final /* synthetic */ TLObject f$3;
                private final /* synthetic */ MessageObject f$4;
                private final /* synthetic */ String f$5;
                private final /* synthetic */ boolean f$6;
                private final /* synthetic */ TLObject f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$42$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(z2, tL_error, message, tLObject2, messageObject, str, tLObject) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ TLRPC.TL_error f$2;
                private final /* synthetic */ TLRPC.Message f$3;
                private final /* synthetic */ TLObject f$4;
                private final /* synthetic */ MessageObject f$5;
                private final /* synthetic */ String f$6;
                private final /* synthetic */ TLObject f$7;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$51$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$39$SendMessagesHelper(TLRPC.Message message, boolean z, TLObject tLObject, DelayedMessage delayedMessage) {
        removeFromSendingMessages(message.id, z);
        if (tLObject instanceof TLRPC.TL_messages_sendMedia) {
            TLRPC.TL_messages_sendMedia tL_messages_sendMedia = (TLRPC.TL_messages_sendMedia) tLObject;
            TLRPC.InputMedia inputMedia = tL_messages_sendMedia.media;
            if (inputMedia instanceof TLRPC.TL_inputMediaPhoto) {
                tL_messages_sendMedia.media = delayedMessage.inputUploadMedia;
            } else if (inputMedia instanceof TLRPC.TL_inputMediaDocument) {
                tL_messages_sendMedia.media = delayedMessage.inputUploadMedia;
            }
        } else if (tLObject instanceof TLRPC.TL_messages_editMessage) {
            TLRPC.TL_messages_editMessage tL_messages_editMessage = (TLRPC.TL_messages_editMessage) tLObject;
            TLRPC.InputMedia inputMedia2 = tL_messages_editMessage.media;
            if (inputMedia2 instanceof TLRPC.TL_inputMediaPhoto) {
                tL_messages_editMessage.media = delayedMessage.inputUploadMedia;
            } else if (inputMedia2 instanceof TLRPC.TL_inputMediaDocument) {
                tL_messages_editMessage.media = delayedMessage.inputUploadMedia;
            }
        }
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
    }

    public /* synthetic */ void lambda$null$42$SendMessagesHelper(TLRPC.TL_error tL_error, TLRPC.Message message, TLObject tLObject, MessageObject messageObject, String str, boolean z, TLObject tLObject2) {
        int i = 0;
        TLRPC.Message message2 = null;
        if (tL_error == null) {
            String str2 = message.attachPath;
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            ArrayList<TLRPC.Update> arrayList = updates.updates;
            while (true) {
                if (i >= arrayList.size()) {
                    break;
                }
                TLRPC.Update update = arrayList.get(i);
                if (update instanceof TLRPC.TL_updateEditMessage) {
                    message2 = ((TLRPC.TL_updateEditMessage) update).message;
                    break;
                } else if (update instanceof TLRPC.TL_updateEditChannelMessage) {
                    message2 = ((TLRPC.TL_updateEditChannelMessage) update).message;
                    break;
                } else if (update instanceof TLRPC.TL_updateNewScheduledMessage) {
                    message2 = ((TLRPC.TL_updateNewScheduledMessage) update).message;
                    break;
                } else {
                    i++;
                }
            }
            TLRPC.Message message3 = message2;
            if (message3 != null) {
                ImageLoader.saveMessageThumbs(message3);
                updateMediaPaths(messageObject, message3, message3.id, str, false);
            }
            Utilities.stageQueue.postRunnable(new Runnable(updates, message, z) {
                private final /* synthetic */ TLRPC.Updates f$1;
                private final /* synthetic */ TLRPC.Message f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$41$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
                }
            });
            if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
                stopVideoService(str2);
                return;
            }
            return;
        }
        AlertsCreator.processError(this.currentAccount, tL_error, (BaseFragment) null, tLObject2, new Object[0]);
        if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
            stopVideoService(message.attachPath);
        }
        removeFromSendingMessages(message.id, z);
        revertEditingMessageObject(messageObject);
    }

    public /* synthetic */ void lambda$null$41$SendMessagesHelper(TLRPC.Updates updates, TLRPC.Message message, boolean z) {
        getMessagesController().processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable(message, z) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$40$SendMessagesHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$40$SendMessagesHelper(TLRPC.Message message, boolean z) {
        processSentMessage(message.id);
        removeFromSendingMessages(message.id, z);
    }

    public /* synthetic */ void lambda$null$51$SendMessagesHelper(boolean z, TLRPC.TL_error tL_error, TLRPC.Message message, TLObject tLObject, MessageObject messageObject, String str, TLObject tLObject2) {
        boolean z2;
        boolean z3;
        int i;
        TLRPC.Message message2;
        int i2;
        boolean z4;
        boolean z5;
        TLRPC.Message message3;
        boolean z6 = z;
        TLRPC.TL_error tL_error2 = tL_error;
        TLRPC.Message message4 = message;
        TLObject tLObject3 = tLObject;
        if (tL_error2 == null) {
            int i3 = message4.id;
            ArrayList arrayList = new ArrayList();
            String str2 = message4.attachPath;
            boolean z7 = message4.date == NUM;
            if (tLObject3 instanceof TLRPC.TL_updateShortSentMessage) {
                TLRPC.TL_updateShortSentMessage tL_updateShortSentMessage = (TLRPC.TL_updateShortSentMessage) tLObject3;
                updateMediaPaths(messageObject, (TLRPC.Message) null, tL_updateShortSentMessage.id, (String) null, false);
                int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                int i4 = tL_updateShortSentMessage.id;
                message4.id = i4;
                message4.local_id = i4;
                message4.date = tL_updateShortSentMessage.date;
                message4.entities = tL_updateShortSentMessage.entities;
                message4.out = tL_updateShortSentMessage.out;
                TLRPC.MessageMedia messageMedia = tL_updateShortSentMessage.media;
                if (messageMedia != null) {
                    message4.media = messageMedia;
                    message4.flags |= 512;
                    ImageLoader.saveMessageThumbs(message);
                }
                if ((tL_updateShortSentMessage.media instanceof TLRPC.TL_messageMediaGame) && !TextUtils.isEmpty(tL_updateShortSentMessage.message)) {
                    message4.message = tL_updateShortSentMessage.message;
                }
                if (!message4.entities.isEmpty()) {
                    message4.flags |= 128;
                }
                Integer num = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message4.dialog_id));
                if (num == null) {
                    num = Integer.valueOf(getMessagesStorage().getDialogReadMax(message4.out, message4.dialog_id));
                    getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message4.dialog_id), num);
                }
                message4.unread = num.intValue() < message4.id;
                Utilities.stageQueue.postRunnable(new Runnable(tL_updateShortSentMessage) {
                    private final /* synthetic */ TLRPC.TL_updateShortSentMessage f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$null$43$SendMessagesHelper(this.f$1);
                    }
                });
                arrayList.add(message4);
                i = mediaExistanceFlags;
                z2 = false;
                z3 = false;
            } else if (tLObject3 instanceof TLRPC.Updates) {
                TLRPC.Updates updates = (TLRPC.Updates) tLObject3;
                ArrayList<TLRPC.Update> arrayList2 = updates.updates;
                int i5 = 0;
                while (i5 < arrayList2.size()) {
                    TLRPC.Update update = arrayList2.get(i5);
                    if (update instanceof TLRPC.TL_updateNewMessage) {
                        TLRPC.TL_updateNewMessage tL_updateNewMessage = (TLRPC.TL_updateNewMessage) update;
                        message3 = tL_updateNewMessage.message;
                        arrayList.add(message3);
                        Utilities.stageQueue.postRunnable(new Runnable(tL_updateNewMessage) {
                            private final /* synthetic */ TLRPC.TL_updateNewMessage f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                SendMessagesHelper.this.lambda$null$44$SendMessagesHelper(this.f$1);
                            }
                        });
                        arrayList2.remove(i5);
                    } else if (update instanceof TLRPC.TL_updateNewChannelMessage) {
                        TLRPC.TL_updateNewChannelMessage tL_updateNewChannelMessage = (TLRPC.TL_updateNewChannelMessage) update;
                        message3 = tL_updateNewChannelMessage.message;
                        arrayList.add(message3);
                        if ((message4.flags & Integer.MIN_VALUE) != 0) {
                            TLRPC.Message message5 = tL_updateNewChannelMessage.message;
                            message5.flags = Integer.MIN_VALUE | message5.flags;
                        }
                        Utilities.stageQueue.postRunnable(new Runnable(tL_updateNewChannelMessage) {
                            private final /* synthetic */ TLRPC.TL_updateNewChannelMessage f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                SendMessagesHelper.this.lambda$null$45$SendMessagesHelper(this.f$1);
                            }
                        });
                        arrayList2.remove(i5);
                    } else if (update instanceof TLRPC.TL_updateNewScheduledMessage) {
                        TLRPC.TL_updateNewScheduledMessage tL_updateNewScheduledMessage = (TLRPC.TL_updateNewScheduledMessage) update;
                        message3 = tL_updateNewScheduledMessage.message;
                        arrayList.add(message3);
                        if ((message4.flags & Integer.MIN_VALUE) != 0) {
                            TLRPC.Message message6 = tL_updateNewScheduledMessage.message;
                            message6.flags = Integer.MIN_VALUE | message6.flags;
                        }
                        arrayList2.remove(i5);
                    } else {
                        i5++;
                    }
                    message2 = message3;
                }
                message2 = null;
                if (message2 != null) {
                    z4 = (!z7 || message2.date == NUM) ? z6 : false;
                    ImageLoader.saveMessageThumbs(message2);
                    if (!z4) {
                        Integer num2 = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message2.dialog_id));
                        if (num2 == null) {
                            num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(message2.out, message2.dialog_id));
                            getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message2.dialog_id), num2);
                        }
                        message2.unread = num2.intValue() < message2.id;
                    }
                    updateMediaPaths(messageObject, message2, message2.id, str, false);
                    i2 = messageObject.getMediaExistanceFlags();
                    message4.id = message2.id;
                    z5 = false;
                } else {
                    z4 = z6;
                    z5 = true;
                    i2 = 0;
                }
                Utilities.stageQueue.postRunnable(new Runnable(updates) {
                    private final /* synthetic */ TLRPC.Updates f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$null$46$SendMessagesHelper(this.f$1);
                    }
                });
                i = i2;
                z3 = z4;
                z2 = z5;
            } else {
                z3 = z6;
                i = 0;
                z2 = false;
            }
            if (MessageObject.isLiveLocationMessage(message)) {
                getLocationController().addSharingLocation(message4.dialog_id, message4.id, message4.media.period, message);
            }
            if (!z2) {
                getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                message4.send_state = 0;
                if (!z6 || z3) {
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i3), Integer.valueOf(message4.id), message4, Long.valueOf(message4.dialog_id), 0L, Integer.valueOf(i), Boolean.valueOf(z));
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable(message, i3, z, arrayList, i, str2) {
                        private final /* synthetic */ TLRPC.Message f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ boolean f$3;
                        private final /* synthetic */ ArrayList f$4;
                        private final /* synthetic */ int f$5;
                        private final /* synthetic */ String f$6;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$50$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                        }
                    });
                } else {
                    ArrayList arrayList3 = new ArrayList();
                    arrayList3.add(Integer.valueOf(i3));
                    getMessagesController().deleteMessages(arrayList3, (ArrayList<Long>) null, (TLRPC.EncryptedChat) null, message4.dialog_id, message4.to_id.channel_id, false, true);
                    getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList, messageObject, message, i3, z, str2) {
                        private final /* synthetic */ ArrayList f$1;
                        private final /* synthetic */ MessageObject f$2;
                        private final /* synthetic */ TLRPC.Message f$3;
                        private final /* synthetic */ int f$4;
                        private final /* synthetic */ boolean f$5;
                        private final /* synthetic */ String f$6;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$48$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                        }
                    });
                }
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error2, (BaseFragment) null, tLObject2, new Object[0]);
            z2 = true;
        }
        if (z2) {
            getMessagesStorage().markMessageAsSendError(message4, z6);
            message4.send_state = 2;
            getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message4.id));
            processSentMessage(message4.id);
            if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
                stopVideoService(message4.attachPath);
            }
            removeFromSendingMessages(message4.id, z6);
        }
    }

    public /* synthetic */ void lambda$null$43$SendMessagesHelper(TLRPC.TL_updateShortSentMessage tL_updateShortSentMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateShortSentMessage.pts, tL_updateShortSentMessage.date, tL_updateShortSentMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$44$SendMessagesHelper(TLRPC.TL_updateNewMessage tL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$45$SendMessagesHelper(TLRPC.TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$46$SendMessagesHelper(TLRPC.Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    public /* synthetic */ void lambda$null$48$SendMessagesHelper(ArrayList arrayList, MessageObject messageObject, TLRPC.Message message, int i, boolean z, String str) {
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable(messageObject, message, i, z) {
            private final /* synthetic */ MessageObject f$1;
            private final /* synthetic */ TLRPC.Message f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$47$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
        if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
            stopVideoService(str);
            return;
        }
    }

    public /* synthetic */ void lambda$null$47$SendMessagesHelper(MessageObject messageObject, TLRPC.Message message, int i, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true));
        getMessagesController().updateInterfaceWithMessages(message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$50$SendMessagesHelper(TLRPC.Message message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        TLRPC.Message message2 = message;
        getMessagesStorage().updateMessageStateAndId(message2.random_id, Integer.valueOf(i), message2.id, 0, false, message2.to_id.channel_id, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC.Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable(message, i, i2, z) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$49$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
        if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
            stopVideoService(str);
        }
    }

    public /* synthetic */ void lambda$null$49$SendMessagesHelper(TLRPC.Message message, int i, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), 0L, Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$54$SendMessagesHelper(TLRPC.Message message) {
        AndroidUtilities.runOnUIThread(new Runnable(message, message.id) {
            private final /* synthetic */ TLRPC.Message f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$53$SendMessagesHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$53$SendMessagesHelper(TLRPC.Message message, int i) {
        message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(i));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0022, code lost:
        r5 = (r5 = r7.media).photo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMediaPaths(org.telegram.messenger.MessageObject r17, org.telegram.tgnet.TLRPC.Message r18, int r19, java.lang.String r20, boolean r21) {
        /*
            r16 = this;
            r0 = r17
            r7 = r18
            r8 = r20
            r1 = r21
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            java.lang.String r4 = "_"
            if (r2 == 0) goto L_0x0105
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            r6 = 40
            if (r5 == 0) goto L_0x0034
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x002d
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x002d
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            if (r5 == 0) goto L_0x002d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x002e
        L_0x002d:
            r5 = r2
        L_0x002e:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            goto L_0x00aa
        L_0x0034:
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            if (r5 == 0) goto L_0x0055
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x004f
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x004f
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            if (r5 == 0) goto L_0x004f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x0050
        L_0x004f:
            r5 = r2
        L_0x0050:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            goto L_0x00aa
        L_0x0055:
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            if (r2 == 0) goto L_0x00a7
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            if (r5 == 0) goto L_0x0080
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x0078
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x0078
            org.telegram.tgnet.TLRPC$WebPage r5 = r5.webpage
            if (r5 == 0) goto L_0x0078
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            if (r5 == 0) goto L_0x0078
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x0079
        L_0x0078:
            r5 = r2
        L_0x0079:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            goto L_0x00aa
        L_0x0080:
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            if (r2 == 0) goto L_0x00a7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x009f
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x009f
            org.telegram.tgnet.TLRPC$WebPage r5 = r5.webpage
            if (r5 == 0) goto L_0x009f
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            if (r5 == 0) goto L_0x009f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x00a0
        L_0x009f:
            r5 = r2
        L_0x00a0:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            goto L_0x00aa
        L_0x00a7:
            r2 = 0
            r5 = 0
            r6 = 0
        L_0x00aa:
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r10 == 0) goto L_0x0105
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize
            if (r2 == 0) goto L_0x0105
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "stripped"
            r2.append(r10)
            java.lang.String r11 = org.telegram.messenger.FileRefController.getKeyForParentObject(r17)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            if (r7 == 0) goto L_0x00dd
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            java.lang.String r10 = org.telegram.messenger.FileRefController.getKeyForParentObject(r18)
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            goto L_0x00fa
        L_0x00dd:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "strippedmessage"
            r10.append(r11)
            r11 = r19
            r10.append(r11)
            r10.append(r4)
            int r11 = r17.getChannelId()
            r10.append(r11)
            java.lang.String r10 = r10.toString()
        L_0x00fa:
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            r11.replaceImageInCache(r2, r10, r5, r1)
        L_0x0105:
            if (r7 != 0) goto L_0x0108
            return
        L_0x0108:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            java.lang.String r6 = "sent_"
            java.lang.String r12 = ".jpg"
            r13 = 4
            r14 = 0
            r15 = 1
            if (r5 == 0) goto L_0x02b6
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            if (r5 == 0) goto L_0x02b6
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x02b6
            org.telegram.tgnet.TLRPC$Photo r3 = r5.photo
            if (r3 == 0) goto L_0x02b6
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x0154
            boolean r0 = r0.scheduled
            if (r0 != 0) goto L_0x0154
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            org.telegram.tgnet.TLRPC$Peer r5 = r7.to_id
            int r5 = r5.channel_id
            r3.append(r5)
            r3.append(r4)
            int r5 = r7.id
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            r0.putSentFile(r8, r2, r14, r3)
        L_0x0154:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            int r0 = r0.size()
            if (r0 != r15) goto L_0x0180
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            java.lang.Object r0 = r0.get(r14)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC.PhotoSize) r0
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.location
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable
            if (r0 == 0) goto L_0x0180
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            r0.sizes = r1
            goto L_0x029c
        L_0x0180:
            r0 = 0
        L_0x0181:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x029c
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2
            if (r2 == 0) goto L_0x0295
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            if (r3 == 0) goto L_0x0295
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty
            if (r3 != 0) goto L_0x0295
            java.lang.String r3 = r2.type
            if (r3 != 0) goto L_0x01a9
            goto L_0x0295
        L_0x01a9:
            r3 = 0
        L_0x01aa:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x0295
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            java.lang.Object r5 = r5.get(r3)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC.PhotoSize) r5
            if (r5 == 0) goto L_0x028e
            org.telegram.tgnet.TLRPC$FileLocation r6 = r5.location
            if (r6 == 0) goto L_0x028e
            java.lang.String r8 = r5.type
            if (r8 != 0) goto L_0x01ce
            goto L_0x028e
        L_0x01ce:
            long r14 = r6.volume_id
            int r6 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x01dc
            java.lang.String r6 = r2.type
            boolean r6 = r6.equals(r8)
            if (r6 != 0) goto L_0x01e8
        L_0x01dc:
            int r6 = r2.w
            int r8 = r5.w
            if (r6 != r8) goto L_0x028e
            int r6 = r2.h
            int r8 = r5.h
            if (r6 != r8) goto L_0x028e
        L_0x01e8:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r6 = r5.location
            long r14 = r6.volume_id
            r3.append(r14)
            r3.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r6 = r5.location
            int r6 = r6.local_id
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r8 = r2.location
            long r14 = r8.volume_id
            r6.append(r14)
            r6.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r8 = r2.location
            int r8 = r8.local_id
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            boolean r8 = r3.equals(r6)
            if (r8 == 0) goto L_0x0223
            goto L_0x0295
        L_0x0223:
            java.io.File r8 = new java.io.File
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r13)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r3)
            r15.append(r12)
            java.lang.String r15 = r15.toString()
            r8.<init>(r14, r15)
            org.telegram.tgnet.TLRPC$MessageMedia r14 = r7.media
            int r15 = r14.ttl_seconds
            if (r15 != 0) goto L_0x025b
            org.telegram.tgnet.TLRPC$Photo r14 = r14.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r14.sizes
            int r14 = r14.size()
            r15 = 1
            if (r14 == r15) goto L_0x0256
            int r14 = r2.w
            r15 = 90
            if (r14 > r15) goto L_0x0256
            int r14 = r2.h
            if (r14 <= r15) goto L_0x025b
        L_0x0256:
            java.io.File r14 = org.telegram.messenger.FileLoader.getPathToAttach(r2)
            goto L_0x0273
        L_0x025b:
            java.io.File r14 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r6)
            r13.append(r12)
            java.lang.String r13 = r13.toString()
            r14.<init>(r15, r13)
        L_0x0273:
            r8.renameTo(r14)
            org.telegram.messenger.ImageLoader r8 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$MessageMedia r13 = r7.media
            org.telegram.tgnet.TLRPC$Photo r13 = r13.photo
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r13)
            r8.replaceImageInCache(r3, r6, r13, r1)
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            r5.location = r3
            int r2 = r2.size
            r5.size = r2
            goto L_0x0295
        L_0x028e:
            int r3 = r3 + 1
            r13 = 4
            r14 = 0
            r15 = 1
            goto L_0x01aa
        L_0x0295:
            int r0 = r0 + 1
            r13 = 4
            r14 = 0
            r15 = 1
            goto L_0x0181
        L_0x029c:
            java.lang.String r0 = r9.message
            r7.message = r0
            java.lang.String r0 = r9.attachPath
            r7.attachPath = r0
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            long r2 = r1.id
            r0.id = r2
            long r1 = r1.access_hash
            r0.access_hash = r1
            goto L_0x05cc
        L_0x02b6:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r3 == 0) goto L_0x057e
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            if (r3 == 0) goto L_0x057e
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r9.media
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r5 == 0) goto L_0x057e
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            if (r3 == 0) goto L_0x057e
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x0355
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r18)
            if (r2 != 0) goto L_0x02da
            boolean r3 = org.telegram.messenger.MessageObject.isGifMessage(r18)
            if (r3 == 0) goto L_0x031e
        L_0x02da:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r5)
            if (r3 != r5) goto L_0x031e
            boolean r3 = r0.scheduled
            if (r3 != 0) goto L_0x0317
            org.telegram.messenger.MessagesStorage r3 = r16.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            r13 = 2
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r6)
            org.telegram.tgnet.TLRPC$Peer r6 = r7.to_id
            int r6 = r6.channel_id
            r14.append(r6)
            r14.append(r4)
            int r6 = r7.id
            r14.append(r6)
            java.lang.String r6 = r14.toString()
            r3.putSentFile(r8, r5, r13, r6)
        L_0x0317:
            if (r2 == 0) goto L_0x0355
            java.lang.String r2 = r9.attachPath
            r7.attachPath = r2
            goto L_0x0355
        L_0x031e:
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r18)
            if (r2 != 0) goto L_0x0355
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r18)
            if (r2 != 0) goto L_0x0355
            boolean r2 = r0.scheduled
            if (r2 != 0) goto L_0x0355
            org.telegram.messenger.MessagesStorage r2 = r16.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r6)
            org.telegram.tgnet.TLRPC$Peer r6 = r7.to_id
            int r6 = r6.channel_id
            r5.append(r6)
            r5.append(r4)
            int r6 = r7.id
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 1
            r2.putSentFile(r8, r3, r6, r5)
        L_0x0355:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r3 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r3)
            if (r2 == 0) goto L_0x040b
            org.telegram.tgnet.TLRPC$FileLocation r5 = r2.location
            if (r5 == 0) goto L_0x040b
            long r5 = r5.volume_id
            int r13 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r13 != 0) goto L_0x040b
            if (r3 == 0) goto L_0x040b
            org.telegram.tgnet.TLRPC$FileLocation r5 = r3.location
            if (r5 == 0) goto L_0x040b
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty
            if (r5 != 0) goto L_0x040b
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty
            if (r5 != 0) goto L_0x040b
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r6 = r2.location
            long r10 = r6.volume_id
            r5.append(r10)
            r5.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r6 = r2.location
            int r6 = r6.local_id
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r10 = r3.location
            long r10 = r10.volume_id
            r6.append(r10)
            r6.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r4 = r3.location
            int r4 = r4.local_id
            r6.append(r4)
            java.lang.String r4 = r6.toString()
            boolean r6 = r5.equals(r4)
            if (r6 != 0) goto L_0x0434
            java.io.File r6 = new java.io.File
            r10 = 4
            java.io.File r11 = org.telegram.messenger.FileLoader.getDirectory(r10)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r5)
            r13.append(r12)
            java.lang.String r13 = r13.toString()
            r6.<init>(r11, r13)
            java.io.File r11 = new java.io.File
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r10)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r4)
            r10.append(r12)
            java.lang.String r10 = r10.toString()
            r11.<init>(r13, r10)
            r6.renameTo(r11)
            org.telegram.messenger.ImageLoader r6 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r7.media
            org.telegram.tgnet.TLRPC$Document r10 = r10.document
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument(r3, r10)
            r6.replaceImageInCache(r5, r4, r10, r1)
            org.telegram.tgnet.TLRPC$FileLocation r1 = r3.location
            r2.location = r1
            int r1 = r3.size
            r2.size = r1
            goto L_0x0434
        L_0x040b:
            if (r2 == 0) goto L_0x041a
            boolean r1 = org.telegram.messenger.MessageObject.isStickerMessage(r18)
            if (r1 == 0) goto L_0x041a
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            if (r1 == 0) goto L_0x041a
            r3.location = r1
            goto L_0x0434
        L_0x041a:
            if (r2 == 0) goto L_0x0428
            if (r2 == 0) goto L_0x0424
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable
            if (r1 != 0) goto L_0x0428
        L_0x0424:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty
            if (r1 == 0) goto L_0x0434
        L_0x0428:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r1.thumbs = r2
        L_0x0434:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            int r3 = r2.dc_id
            r1.dc_id = r3
            long r3 = r2.id
            r1.id = r3
            long r2 = r2.access_hash
            r1.access_hash = r2
            r1 = 0
        L_0x0449:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x046b
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r2
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio
            if (r3 == 0) goto L_0x0468
            byte[] r3 = r2.waveform
            goto L_0x046c
        L_0x0468:
            int r1 = r1 + 1
            goto L_0x0449
        L_0x046b:
            r3 = 0
        L_0x046c:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            r1.attributes = r2
            if (r3 == 0) goto L_0x04a2
            r1 = 0
        L_0x047b:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x04a2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r2
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio
            if (r4 == 0) goto L_0x049f
            r2.waveform = r3
            int r4 = r2.flags
            r5 = 4
            r4 = r4 | r5
            r2.flags = r4
        L_0x049f:
            int r1 = r1 + 1
            goto L_0x047b
        L_0x04a2:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            int r3 = r2.size
            r1.size = r3
            java.lang.String r2 = r2.mime_type
            r1.mime_type = r2
            int r1 = r7.flags
            r2 = 4
            r1 = r1 & r2
            if (r1 != 0) goto L_0x04fc
            boolean r1 = org.telegram.messenger.MessageObject.isOut(r18)
            if (r1 == 0) goto L_0x04fc
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC.Document) r1)
            if (r1 == 0) goto L_0x04d6
            org.telegram.messenger.MediaDataController r1 = r16.getMediaDataController()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            int r3 = r7.date
            r1.addRecentGif(r2, r3)
            goto L_0x04fc
        L_0x04d6:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r1)
            if (r1 != 0) goto L_0x04eb
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            r2 = 1
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r2)
            if (r1 == 0) goto L_0x04fc
        L_0x04eb:
            org.telegram.messenger.MediaDataController r1 = r16.getMediaDataController()
            r2 = 0
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            int r5 = r7.date
            r6 = 0
            r3 = r18
            r1.addRecentSticker(r2, r3, r4, r5, r6)
        L_0x04fc:
            java.lang.String r1 = r9.attachPath
            if (r1 == 0) goto L_0x0575
            r2 = 4
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r2)
            java.lang.String r2 = r2.getAbsolutePath()
            boolean r1 = r1.startsWith(r2)
            if (r1 == 0) goto L_0x0575
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r9.attachPath
            r1.<init>(r2)
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            int r2 = r2.ttl_seconds
            if (r2 == 0) goto L_0x0520
            r2 = 1
            goto L_0x0521
        L_0x0520:
            r2 = 0
        L_0x0521:
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r2)
            boolean r3 = r1.renameTo(r2)
            if (r3 != 0) goto L_0x0545
            boolean r1 = r1.exists()
            if (r1 == 0) goto L_0x0536
            java.lang.String r1 = r9.attachPath
            r7.attachPath = r1
            goto L_0x0539
        L_0x0536:
            r1 = 0
            r0.attachPathExists = r1
        L_0x0539:
            boolean r1 = r2.exists()
            r0.mediaExists = r1
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x05cc
        L_0x0545:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoMessage(r18)
            if (r1 == 0) goto L_0x0550
            r1 = 1
            r0.attachPathExists = r1
            goto L_0x05cc
        L_0x0550:
            boolean r1 = r0.attachPathExists
            r0.mediaExists = r1
            r1 = 0
            r0.attachPathExists = r1
            java.lang.String r0 = ""
            r9.attachPath = r0
            if (r8 == 0) goto L_0x05cc
            java.lang.String r0 = "http"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x05cc
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            java.lang.String r1 = r2.toString()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            r0.addRecentLocalFile(r8, r1, r2)
            goto L_0x05cc
        L_0x0575:
            java.lang.String r0 = r9.attachPath
            r7.attachPath = r0
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x05cc
        L_0x057e:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r7.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r1 == 0) goto L_0x058d
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r1 == 0) goto L_0x058d
            r9.media = r0
            goto L_0x05cc
        L_0x058d:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r7.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0596
            r9.media = r0
            goto L_0x05cc
        L_0x0596:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r1 == 0) goto L_0x05a9
            org.telegram.tgnet.TLRPC$GeoPoint r0 = r0.geo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$GeoPoint r1 = r1.geo
            double r2 = r1.lat
            r0.lat = r2
            double r1 = r1._long
            r0._long = r1
            goto L_0x05cc
        L_0x05a9:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r1 == 0) goto L_0x05c6
            r9.media = r0
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x05cc
            java.lang.String r0 = r7.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x05cc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r7.entities
            r9.entities = r0
            java.lang.String r0 = r7.message
            r9.message = r0
            goto L_0x05cc
        L_0x05c6:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r1 == 0) goto L_0x05cc
            r9.media = r0
        L_0x05cc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.updateMediaPaths(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Message, int, java.lang.String, boolean):void");
    }

    private void putToDelayedMessages(String str, DelayedMessage delayedMessage) {
        ArrayList arrayList = this.delayedMessages.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.delayedMessages.put(str, arrayList);
        }
        arrayList.add(delayedMessage);
    }

    /* access modifiers changed from: protected */
    public ArrayList<DelayedMessage> getDelayedMessages(String str) {
        return this.delayedMessages.get(str);
    }

    /* access modifiers changed from: protected */
    public long getNextRandomId() {
        long j = 0;
        while (j == 0) {
            j = Utilities.random.nextLong();
        }
        return j;
    }

    public void checkUnsentMessages() {
        getMessagesStorage().getUnsentMessages(1000);
    }

    /* access modifiers changed from: protected */
    public void processUnsentMessages(ArrayList<TLRPC.Message> arrayList, ArrayList<TLRPC.Message> arrayList2, ArrayList<TLRPC.User> arrayList3, ArrayList<TLRPC.Chat> arrayList4, ArrayList<TLRPC.EncryptedChat> arrayList5) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList3, arrayList4, arrayList5, arrayList, arrayList2) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ ArrayList f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$processUnsentMessages$55$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$processUnsentMessages$55$SendMessagesHelper(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (int i = 0; i < arrayList4.size(); i++) {
            retrySendMessage(new MessageObject(this.currentAccount, (TLRPC.Message) arrayList4.get(i), false), true);
        }
        if (arrayList5 != null) {
            for (int i2 = 0; i2 < arrayList5.size(); i2++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC.Message) arrayList5.get(i2), false);
                messageObject.scheduled = true;
                retrySendMessage(messageObject, true);
            }
        }
    }

    public TLRPC.TL_photo generatePhotoSizes(String str, Uri uri) {
        return generatePhotoSizes((TLRPC.TL_photo) null, str, uri);
    }

    public TLRPC.TL_photo generatePhotoSizes(TLRPC.TL_photo tL_photo, String str, Uri uri) {
        Bitmap loadBitmap = ImageLoader.loadBitmap(str, uri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
        if (loadBitmap == null) {
            loadBitmap = ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true);
        }
        ArrayList<TLRPC.PhotoSize> arrayList = new ArrayList<>();
        TLRPC.PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, true);
        if (scaleAndSaveImage != null) {
            arrayList.add(scaleAndSaveImage);
        }
        TLRPC.PhotoSize scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(loadBitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
        if (scaleAndSaveImage2 != null) {
            arrayList.add(scaleAndSaveImage2);
        }
        if (loadBitmap != null) {
            loadBitmap.recycle();
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        getUserConfig().saveConfig(false);
        if (tL_photo == null) {
            tL_photo = new TLRPC.TL_photo();
        }
        tL_photo.date = getConnectionsManager().getCurrentTime();
        tL_photo.sizes = arrayList;
        tL_photo.file_reference = new byte[0];
        return tL_photo;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0334, code lost:
        r4 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x0335, code lost:
        if (r4 == 0) goto L_0x036d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0338, code lost:
        if (r4 == 1) goto L_0x0368;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x033b, code lost:
        if (r4 == 2) goto L_0x0363;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x033e, code lost:
        if (r4 == 3) goto L_0x035e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0341, code lost:
        if (r4 == 4) goto L_0x0359;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x0344, code lost:
        if (r4 == 5) goto L_0x0354;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0346, code lost:
        r0 = r19.getMimeTypeFromExtension(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x034c, code lost:
        if (r0 == null) goto L_0x0351;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x034e, code lost:
        r7.mime_type = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0351, code lost:
        r7.mime_type = "application/octet-stream";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0354, code lost:
        r7.mime_type = "audio/flac";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0359, code lost:
        r7.mime_type = "audio/ogg";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x035e, code lost:
        r7.mime_type = "audio/m4a";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x0363, code lost:
        r7.mime_type = "audio/mpeg";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x0368, code lost:
        r7.mime_type = "audio/opus";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x036d, code lost:
        r7.mime_type = "image/webp";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004e, code lost:
        if (r3 == false) goto L_0x0052;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0154 A[SYNTHETIC, Splitter:B:102:0x0154] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0166 A[SYNTHETIC, Splitter:B:110:0x0166] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x019f  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01c8  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x020b A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ad  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043d  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0443  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044b  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0454  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045d  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00af  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r28, java.lang.String r29, java.lang.String r30, android.net.Uri r31, java.lang.String r32, long r33, org.telegram.messenger.MessageObject r35, java.lang.CharSequence r36, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r37, org.telegram.messenger.MessageObject r38, boolean r39, boolean r40, int r41) {
        /*
            r0 = r29
            r1 = r30
            r2 = r31
            r3 = r32
            r4 = 0
            if (r0 == 0) goto L_0x0011
            int r5 = r29.length()
            if (r5 != 0) goto L_0x0014
        L_0x0011:
            if (r2 != 0) goto L_0x0014
            return r4
        L_0x0014:
            if (r2 == 0) goto L_0x001d
            boolean r5 = org.telegram.messenger.AndroidUtilities.isInternalUri(r31)
            if (r5 == 0) goto L_0x001d
            return r4
        L_0x001d:
            if (r0 == 0) goto L_0x002f
            java.io.File r5 = new java.io.File
            r5.<init>(r0)
            android.net.Uri r5 = android.net.Uri.fromFile(r5)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isInternalUri(r5)
            if (r5 == 0) goto L_0x002f
            return r4
        L_0x002f:
            android.webkit.MimeTypeMap r5 = android.webkit.MimeTypeMap.getSingleton()
            r14 = 1
            if (r2 == 0) goto L_0x0051
            if (r3 == 0) goto L_0x003d
            java.lang.String r0 = r5.getExtensionFromMimeType(r3)
            goto L_0x003e
        L_0x003d:
            r0 = 0
        L_0x003e:
            if (r0 != 0) goto L_0x0045
            java.lang.String r0 = "txt"
            r3 = 0
            goto L_0x0046
        L_0x0045:
            r3 = 1
        L_0x0046:
            java.lang.String r2 = org.telegram.messenger.MediaController.copyFileToCache(r2, r0)
            if (r2 != 0) goto L_0x004d
            return r4
        L_0x004d:
            r13 = r2
            if (r3 != 0) goto L_0x0053
            goto L_0x0052
        L_0x0051:
            r13 = r0
        L_0x0052:
            r0 = 0
        L_0x0053:
            java.io.File r2 = new java.io.File
            r2.<init>(r13)
            boolean r3 = r2.exists()
            if (r3 == 0) goto L_0x047d
            long r7 = r2.length()
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x006a
            goto L_0x047d
        L_0x006a:
            r9 = r33
            int r3 = (int) r9
            if (r3 != 0) goto L_0x0071
            r3 = 1
            goto L_0x0072
        L_0x0071:
            r3 = 0
        L_0x0072:
            r15 = r3 ^ 1
            java.lang.String r8 = r2.getName()
            r7 = -1
            java.lang.String r6 = ""
            if (r0 == 0) goto L_0x0080
        L_0x007d:
            r16 = r0
            goto L_0x0090
        L_0x0080:
            r0 = 46
            int r0 = r13.lastIndexOf(r0)
            if (r0 == r7) goto L_0x008e
            int r0 = r0 + r14
            java.lang.String r0 = r13.substring(r0)
            goto L_0x007d
        L_0x008e:
            r16 = r6
        L_0x0090:
            java.lang.String r10 = r16.toLowerCase()
            java.lang.String r9 = "mp3"
            boolean r0 = r10.equals(r9)
            java.lang.String r4 = "flac"
            java.lang.String r11 = "opus"
            java.lang.String r12 = "m4a"
            java.lang.String r14 = "ogg"
            r29 = r15
            r15 = 2
            if (r0 != 0) goto L_0x0170
            boolean r0 = r10.equals(r12)
            if (r0 == 0) goto L_0x00af
            goto L_0x0170
        L_0x00af:
            boolean r0 = r10.equals(r11)
            if (r0 != 0) goto L_0x00ce
            boolean r0 = r10.equals(r14)
            if (r0 != 0) goto L_0x00ce
            boolean r0 = r10.equals(r4)
            if (r0 == 0) goto L_0x00c2
            goto L_0x00ce
        L_0x00c2:
            r18 = r8
            r19 = r9
            r0 = 0
            r8 = 0
            r9 = 0
        L_0x00c9:
            r15 = 0
        L_0x00ca:
            r22 = 0
            goto L_0x019d
        L_0x00ce:
            android.media.MediaMetadataRetriever r7 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0145, all -> 0x0141 }
            r7.<init>()     // Catch:{ Exception -> 0x0145, all -> 0x0141 }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x013b }
            r7.setDataSource(r0)     // Catch:{ Exception -> 0x013b }
            r0 = 9
            java.lang.String r0 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x013b }
            if (r0 == 0) goto L_0x010e
            r18 = r8
            r19 = r9
            long r8 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x010c }
            float r0 = (float) r8     // Catch:{ Exception -> 0x010c }
            r8 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r8
            double r8 = (double) r0     // Catch:{ Exception -> 0x010c }
            double r8 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x010c }
            int r8 = (int) r8
            r0 = 7
            java.lang.String r9 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x0106 }
            java.lang.String r0 = r7.extractMetadata(r15)     // Catch:{ Exception -> 0x0101 }
            r20 = r8
            r8 = r0
            goto L_0x0116
        L_0x0101:
            r0 = move-exception
            r20 = r8
            r8 = 0
            goto L_0x014f
        L_0x0106:
            r0 = move-exception
            r20 = r8
            r8 = 0
            r9 = 0
            goto L_0x014f
        L_0x010c:
            r0 = move-exception
            goto L_0x014b
        L_0x010e:
            r18 = r8
            r19 = r9
            r8 = 0
            r9 = 0
            r20 = 0
        L_0x0116:
            if (r38 != 0) goto L_0x012d
            boolean r0 = r10.equals(r14)     // Catch:{ Exception -> 0x012b }
            if (r0 == 0) goto L_0x012d
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x012b }
            int r0 = org.telegram.messenger.MediaController.isOpusFile(r0)     // Catch:{ Exception -> 0x012b }
            r15 = 1
            if (r0 != r15) goto L_0x012d
            r15 = 1
            goto L_0x012e
        L_0x012b:
            r0 = move-exception
            goto L_0x014f
        L_0x012d:
            r15 = 0
        L_0x012e:
            r7.release()     // Catch:{ Exception -> 0x0132 }
            goto L_0x0137
        L_0x0132:
            r0 = move-exception
            r7 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x0137:
            r0 = r8
            r8 = r20
            goto L_0x00ca
        L_0x013b:
            r0 = move-exception
            r18 = r8
            r19 = r9
            goto L_0x014b
        L_0x0141:
            r0 = move-exception
            r1 = r0
            r7 = 0
            goto L_0x0164
        L_0x0145:
            r0 = move-exception
            r18 = r8
            r19 = r9
            r7 = 0
        L_0x014b:
            r8 = 0
            r9 = 0
            r20 = 0
        L_0x014f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0162 }
            if (r7 == 0) goto L_0x015d
            r7.release()     // Catch:{ Exception -> 0x0158 }
            goto L_0x015d
        L_0x0158:
            r0 = move-exception
            r7 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x015d:
            r0 = r8
            r8 = r20
            goto L_0x00c9
        L_0x0162:
            r0 = move-exception
            r1 = r0
        L_0x0164:
            if (r7 == 0) goto L_0x016f
            r7.release()     // Catch:{ Exception -> 0x016a }
            goto L_0x016f
        L_0x016a:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x016f:
            throw r1
        L_0x0170:
            r18 = r8
            r19 = r9
            org.telegram.messenger.audioinfo.AudioInfo r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r2)
            if (r0 == 0) goto L_0x0192
            long r7 = r0.getDuration()
            r22 = 0
            int r9 = (r7 > r22 ? 1 : (r7 == r22 ? 0 : -1))
            if (r9 == 0) goto L_0x0194
            java.lang.String r9 = r0.getArtist()
            java.lang.String r0 = r0.getTitle()
            r24 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r24
            int r8 = (int) r7
            goto L_0x0197
        L_0x0192:
            r22 = 0
        L_0x0194:
            r0 = 0
            r8 = 0
            r9 = 0
        L_0x0197:
            r15 = 0
            r27 = r9
            r9 = r0
            r0 = r27
        L_0x019d:
            if (r8 == 0) goto L_0x01c8
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r7.<init>()
            r7.duration = r8
            r7.title = r9
            r7.performer = r0
            java.lang.String r0 = r7.title
            if (r0 != 0) goto L_0x01b0
            r7.title = r6
        L_0x01b0:
            int r0 = r7.flags
            r8 = 1
            r0 = r0 | r8
            r7.flags = r0
            java.lang.String r0 = r7.performer
            if (r0 != 0) goto L_0x01bc
            r7.performer = r6
        L_0x01bc:
            int r0 = r7.flags
            r9 = 2
            r0 = r0 | r9
            r7.flags = r0
            if (r15 == 0) goto L_0x01c6
            r7.voice = r8
        L_0x01c6:
            r0 = r7
            goto L_0x01c9
        L_0x01c8:
            r0 = 0
        L_0x01c9:
            if (r1 == 0) goto L_0x0207
            java.lang.String r7 = "attheme"
            boolean r7 = r1.endsWith(r7)
            if (r7 == 0) goto L_0x01d6
            r15 = r1
            r1 = 1
            goto L_0x0209
        L_0x01d6:
            if (r0 == 0) goto L_0x01f1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r1)
            java.lang.String r1 = "audio"
            r7.append(r1)
            long r8 = r2.length()
            r7.append(r8)
            java.lang.String r1 = r7.toString()
            goto L_0x0207
        L_0x01f1:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r1)
            r7.append(r6)
            long r8 = r2.length()
            r7.append(r8)
            java.lang.String r1 = r7.toString()
        L_0x0207:
            r15 = r1
            r1 = 0
        L_0x0209:
            if (r1 != 0) goto L_0x0296
            if (r3 != 0) goto L_0x0296
            org.telegram.messenger.MessagesStorage r1 = r28.getMessagesStorage()
            if (r3 != 0) goto L_0x0215
            r7 = 1
            goto L_0x0216
        L_0x0215:
            r7 = 4
        L_0x0216:
            java.lang.Object[] r1 = r1.getSentFile(r15, r7)
            if (r1 == 0) goto L_0x0228
            r7 = 0
            r8 = r1[r7]
            r7 = r8
            org.telegram.tgnet.TLRPC$TL_document r7 = (org.telegram.tgnet.TLRPC.TL_document) r7
            r8 = 1
            r1 = r1[r8]
            java.lang.String r1 = (java.lang.String) r1
            goto L_0x022a
        L_0x0228:
            r1 = 0
            r7 = 0
        L_0x022a:
            if (r7 != 0) goto L_0x0268
            boolean r8 = r13.equals(r15)
            if (r8 != 0) goto L_0x0268
            if (r3 != 0) goto L_0x0268
            org.telegram.messenger.MessagesStorage r8 = r28.getMessagesStorage()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r13)
            r31 = r10
            r32 = r11
            long r10 = r2.length()
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            if (r3 != 0) goto L_0x0253
            r10 = 1
            goto L_0x0254
        L_0x0253:
            r10 = 4
        L_0x0254:
            java.lang.Object[] r8 = r8.getSentFile(r9, r10)
            if (r8 == 0) goto L_0x026c
            r9 = 0
            r1 = r8[r9]
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC.TL_document) r1
            r7 = 1
            r8 = r8[r7]
            r7 = r8
            java.lang.String r7 = (java.lang.String) r7
            r20 = r7
            goto L_0x026f
        L_0x0268:
            r31 = r10
            r32 = r11
        L_0x026c:
            r20 = r1
            r1 = r7
        L_0x026f:
            r10 = 0
            r24 = 0
            r17 = -1
            r7 = r3
            r11 = r18
            r8 = r1
            r26 = r19
            r9 = r13
            r30 = r31
            r31 = r1
            r19 = r5
            r1 = r11
            r18 = r15
            r15 = r32
            r32 = r6
            r5 = r22
            r22 = r12
            r11 = r24
            ensureMediaThumbExists(r7, r8, r9, r10, r11)
            r7 = r31
            r8 = r20
            goto L_0x02ab
        L_0x0296:
            r32 = r6
            r30 = r10
            r1 = r18
            r26 = r19
            r17 = -1
            r19 = r5
            r18 = r15
            r5 = r22
            r15 = r11
            r22 = r12
            r7 = 0
            r8 = 0
        L_0x02ab:
            if (r7 != 0) goto L_0x0438
            org.telegram.tgnet.TLRPC$TL_document r7 = new org.telegram.tgnet.TLRPC$TL_document
            r7.<init>()
            r7.id = r5
            org.telegram.tgnet.ConnectionsManager r9 = r28.getConnectionsManager()
            int r9 = r9.getCurrentTime()
            r7.date = r9
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r9 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r9.<init>()
            r9.file_name = r1
            r1 = 0
            byte[] r10 = new byte[r1]
            r7.file_reference = r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r10 = r7.attributes
            r10.add(r9)
            long r10 = r2.length()
            int r11 = (int) r10
            r7.size = r11
            r7.dc_id = r1
            if (r0 == 0) goto L_0x02df
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r7.attributes
            r1.add(r0)
        L_0x02df:
            int r0 = r16.length()
            java.lang.String r1 = "application/octet-stream"
            if (r0 == 0) goto L_0x0372
            int r0 = r30.hashCode()
            switch(r0) {
                case 106458: goto L_0x0328;
                case 108272: goto L_0x031c;
                case 109967: goto L_0x0312;
                case 3145576: goto L_0x0308;
                case 3418175: goto L_0x02fe;
                case 3645340: goto L_0x02f1;
                default: goto L_0x02ee;
            }
        L_0x02ee:
            r10 = r30
            goto L_0x0334
        L_0x02f1:
            java.lang.String r0 = "webp"
            r10 = r30
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x0334
            r4 = 0
            goto L_0x0335
        L_0x02fe:
            r10 = r30
            boolean r0 = r10.equals(r15)
            if (r0 == 0) goto L_0x0334
            r4 = 1
            goto L_0x0335
        L_0x0308:
            r10 = r30
            boolean r0 = r10.equals(r4)
            if (r0 == 0) goto L_0x0334
            r4 = 5
            goto L_0x0335
        L_0x0312:
            r10 = r30
            boolean r0 = r10.equals(r14)
            if (r0 == 0) goto L_0x0334
            r4 = 4
            goto L_0x0335
        L_0x031c:
            r10 = r30
            r4 = r26
            boolean r0 = r10.equals(r4)
            if (r0 == 0) goto L_0x0334
            r4 = 2
            goto L_0x0335
        L_0x0328:
            r10 = r30
            r4 = r22
            boolean r0 = r10.equals(r4)
            if (r0 == 0) goto L_0x0334
            r4 = 3
            goto L_0x0335
        L_0x0334:
            r4 = -1
        L_0x0335:
            if (r4 == 0) goto L_0x036d
            r11 = 1
            if (r4 == r11) goto L_0x0368
            r11 = 2
            if (r4 == r11) goto L_0x0363
            r0 = 3
            if (r4 == r0) goto L_0x035e
            r0 = 4
            if (r4 == r0) goto L_0x0359
            r0 = 5
            if (r4 == r0) goto L_0x0354
            r4 = r19
            java.lang.String r0 = r4.getMimeTypeFromExtension(r10)
            if (r0 == 0) goto L_0x0351
            r7.mime_type = r0
            goto L_0x0374
        L_0x0351:
            r7.mime_type = r1
            goto L_0x0374
        L_0x0354:
            java.lang.String r0 = "audio/flac"
            r7.mime_type = r0
            goto L_0x0374
        L_0x0359:
            java.lang.String r0 = "audio/ogg"
            r7.mime_type = r0
            goto L_0x0374
        L_0x035e:
            java.lang.String r0 = "audio/m4a"
            r7.mime_type = r0
            goto L_0x0374
        L_0x0363:
            java.lang.String r0 = "audio/mpeg"
            r7.mime_type = r0
            goto L_0x0374
        L_0x0368:
            java.lang.String r0 = "audio/opus"
            r7.mime_type = r0
            goto L_0x0374
        L_0x036d:
            java.lang.String r0 = "image/webp"
            r7.mime_type = r0
            goto L_0x0374
        L_0x0372:
            r7.mime_type = r1
        L_0x0374:
            java.lang.String r0 = r7.mime_type
            java.lang.String r1 = "image/gif"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x03bf
            if (r38 == 0) goto L_0x0388
            long r0 = r38.getGroupIdForUse()
            int r4 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x03bf
        L_0x0388:
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x03bb }
            r1 = 1119092736(0x42b40000, float:90.0)
            r2 = 0
            r4 = 1
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r2, r1, r1, r4)     // Catch:{ Exception -> 0x03bb }
            if (r0 == 0) goto L_0x03bf
            java.lang.String r2 = "animation.gif"
            r9.file_name = r2     // Catch:{ Exception -> 0x03bb }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r7.attributes     // Catch:{ Exception -> 0x03bb }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x03bb }
            r4.<init>()     // Catch:{ Exception -> 0x03bb }
            r2.add(r4)     // Catch:{ Exception -> 0x03bb }
            r2 = 55
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r1, r1, r2, r3)     // Catch:{ Exception -> 0x03bb }
            if (r1 == 0) goto L_0x03b7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r7.thumbs     // Catch:{ Exception -> 0x03bb }
            r2.add(r1)     // Catch:{ Exception -> 0x03bb }
            int r1 = r7.flags     // Catch:{ Exception -> 0x03bb }
            r2 = 1
            r1 = r1 | r2
            r7.flags = r1     // Catch:{ Exception -> 0x03bb }
        L_0x03b7:
            r0.recycle()     // Catch:{ Exception -> 0x03bb }
            goto L_0x03bf
        L_0x03bb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03bf:
            java.lang.String r0 = r7.mime_type
            java.lang.String r1 = "image/webp"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0438
            if (r29 == 0) goto L_0x0438
            if (r38 != 0) goto L_0x0438
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options
            r1.<init>()
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ Exception -> 0x03fc }
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x03fc }
            java.lang.String r2 = "r"
            r0.<init>(r13, r2)     // Catch:{ Exception -> 0x03fc }
            java.nio.channels.FileChannel r19 = r0.getChannel()     // Catch:{ Exception -> 0x03fc }
            java.nio.channels.FileChannel$MapMode r20 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Exception -> 0x03fc }
            r21 = 0
            int r2 = r13.length()     // Catch:{ Exception -> 0x03fc }
            long r2 = (long) r2     // Catch:{ Exception -> 0x03fc }
            r23 = r2
            java.nio.MappedByteBuffer r2 = r19.map(r20, r21, r23)     // Catch:{ Exception -> 0x03fc }
            int r3 = r2.limit()     // Catch:{ Exception -> 0x03fc }
            r4 = 0
            r5 = 1
            org.telegram.messenger.Utilities.loadWebpImage(r4, r2, r3, r1, r5)     // Catch:{ Exception -> 0x03fc }
            r0.close()     // Catch:{ Exception -> 0x03fc }
            goto L_0x0400
        L_0x03fc:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0400:
            int r0 = r1.outWidth
            if (r0 == 0) goto L_0x0438
            int r2 = r1.outHeight
            if (r2 == 0) goto L_0x0438
            r3 = 800(0x320, float:1.121E-42)
            if (r0 > r3) goto L_0x0438
            r0 = 800(0x320, float:1.121E-42)
            if (r2 > r0) goto L_0x0438
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            r2 = r32
            r0.alt = r2
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r3.<init>()
            r0.stickerset = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r7.attributes
            r3.add(r0)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r0.<init>()
            int r3 = r1.outWidth
            r0.w = r3
            int r1 = r1.outHeight
            r0.h = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r7.attributes
            r1.add(r0)
            goto L_0x043a
        L_0x0438:
            r2 = r32
        L_0x043a:
            r3 = r7
            if (r36 == 0) goto L_0x0443
            java.lang.String r0 = r36.toString()
            r10 = r0
            goto L_0x0444
        L_0x0443:
            r10 = r2
        L_0x0444:
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            if (r18 == 0) goto L_0x0452
            java.lang.String r0 = "originalPath"
            r1 = r18
            r5.put(r0, r1)
        L_0x0452:
            if (r39 == 0) goto L_0x045b
            java.lang.String r0 = "forceDocument"
            java.lang.String r1 = "1"
            r5.put(r0, r1)
        L_0x045b:
            if (r8 == 0) goto L_0x0462
            java.lang.String r0 = "parentObject"
            r5.put(r0, r8)
        L_0x0462:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$MPg0uLkoYJmnXNFqAe0X_D_nmR0 r14 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$MPg0uLkoYJmnXNFqAe0X_D_nmR0
            r0 = r14
            r1 = r38
            r2 = r28
            r4 = r13
            r6 = r8
            r7 = r33
            r9 = r35
            r11 = r37
            r12 = r40
            r13 = r41
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r14)
            r1 = 1
            return r1
        L_0x047d:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, boolean, boolean, int):boolean");
    }

    static /* synthetic */ void lambda$prepareSendingDocumentInternal$56(MessageObject messageObject, AccountInstance accountInstance, TLRPC.TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, String str3, ArrayList arrayList, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC.TL_photo) null, (VideoEditedInfo) null, tL_document, str, hashMap, false, str2);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tL_document, (VideoEditedInfo) null, str, j, messageObject2, str3, arrayList, (TLRPC.ReplyMarkup) null, hashMap, z, i, 0, str2);
        }
    }

    public static void prepareSendingDocument(AccountInstance accountInstance, String str, String str2, Uri uri, String str3, String str4, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject2, boolean z, int i) {
        String str5 = str;
        String str6 = str2;
        Uri uri2 = uri;
        if ((str5 != null && str6 != null) || uri2 != null) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = null;
            if (uri2 != null) {
                arrayList3 = new ArrayList();
                arrayList3.add(uri2);
            }
            if (str5 != null) {
                arrayList.add(str);
                arrayList2.add(str2);
            }
            prepareSendingDocuments(accountInstance, arrayList, arrayList2, arrayList3, str3, str4, j, messageObject, inputContentInfoCompat, messageObject2, z, i);
        }
    }

    public static void prepareSendingAudioDocuments(AccountInstance accountInstance, ArrayList<MessageObject> arrayList, String str, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i) {
        new Thread(new Runnable(arrayList, j, accountInstance, str, messageObject2, messageObject, z, i) {
            private final /* synthetic */ ArrayList f$0;
            private final /* synthetic */ long f$1;
            private final /* synthetic */ AccountInstance f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ MessageObject f$4;
            private final /* synthetic */ MessageObject f$5;
            private final /* synthetic */ boolean f$6;
            private final /* synthetic */ int f$7;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r4;
                this.f$3 = r5;
                this.f$4 = r6;
                this.f$5 = r7;
                this.f$6 = r8;
                this.f$7 = r9;
            }

            public final void run() {
                SendMessagesHelper.lambda$prepareSendingAudioDocuments$58(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        }).start();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v3, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x008c  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008b A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingAudioDocuments$58(java.util.ArrayList r19, long r20, org.telegram.messenger.AccountInstance r22, java.lang.String r23, org.telegram.messenger.MessageObject r24, org.telegram.messenger.MessageObject r25, boolean r26, int r27) {
        /*
            r13 = r20
            int r15 = r19.size()
            r16 = 0
            r12 = 0
        L_0x0009:
            if (r12 >= r15) goto L_0x00c1
            r11 = r19
            java.lang.Object r0 = r11.get(r12)
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            java.lang.String r0 = r0.attachPath
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            int r2 = (int) r13
            r3 = 1
            if (r2 != 0) goto L_0x0023
            r2 = 1
            goto L_0x0024
        L_0x0023:
            r2 = 0
        L_0x0024:
            if (r0 == 0) goto L_0x003e
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r0)
            java.lang.String r0 = "audio"
            r5.append(r0)
            long r0 = r1.length()
            r5.append(r0)
            java.lang.String r0 = r5.toString()
        L_0x003e:
            r1 = 0
            if (r2 != 0) goto L_0x0066
            org.telegram.messenger.MessagesStorage r5 = r22.getMessagesStorage()
            if (r2 != 0) goto L_0x0049
            r6 = 1
            goto L_0x004a
        L_0x0049:
            r6 = 4
        L_0x004a:
            java.lang.Object[] r5 = r5.getSentFile(r0, r6)
            if (r5 == 0) goto L_0x0066
            r6 = r5[r16]
            r17 = r6
            org.telegram.tgnet.TLRPC$TL_document r17 = (org.telegram.tgnet.TLRPC.TL_document) r17
            r3 = r5[r3]
            java.lang.String r3 = (java.lang.String) r3
            r8 = 0
            r9 = 0
            r5 = r2
            r6 = r17
            r7 = r0
            ensureMediaThumbExists(r5, r6, r7, r8, r9)
            r6 = r3
            goto L_0x0069
        L_0x0066:
            r6 = r1
            r17 = r6
        L_0x0069:
            if (r17 != 0) goto L_0x0074
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC.TL_document) r3
            goto L_0x0076
        L_0x0074:
            r3 = r17
        L_0x0076:
            if (r2 == 0) goto L_0x008c
            r2 = 32
            long r7 = r13 >> r2
            int r2 = (int) r7
            org.telegram.messenger.MessagesController r5 = r22.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r5.getEncryptedChat(r2)
            if (r2 != 0) goto L_0x008c
            return
        L_0x008c:
            if (r12 != 0) goto L_0x0091
            r10 = r23
            goto L_0x0092
        L_0x0091:
            r10 = r1
        L_0x0092:
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            if (r0 == 0) goto L_0x009e
            java.lang.String r1 = "originalPath"
            r5.put(r1, r0)
        L_0x009e:
            if (r6 == 0) goto L_0x00a5
            java.lang.String r0 = "parentObject"
            r5.put(r0, r6)
        L_0x00a5:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$ncpM1KI8cZ87_6ujC8P1vKoYuis r17 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$ncpM1KI8cZ87_6ujC8P1vKoYuis
            r0 = r17
            r1 = r24
            r2 = r22
            r7 = r20
            r9 = r25
            r11 = r26
            r18 = r12
            r12 = r27
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r17)
            int r12 = r18 + 1
            goto L_0x0009
        L_0x00c1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$58(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$57(MessageObject messageObject, AccountInstance accountInstance, TLRPC.TL_document tL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, String str2, boolean z, int i) {
        MessageObject messageObject4 = messageObject2;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC.TL_photo) null, (VideoEditedInfo) null, tL_document, messageObject4.messageOwner.attachPath, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, (VideoEditedInfo) null, messageObject4.messageOwner.attachPath, j, messageObject3, str2, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, hashMap, z, i, 0, str);
    }

    public static void prepareSendingDocuments(AccountInstance accountInstance, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<Uri> arrayList3, String str, String str2, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject2, boolean z, int i) {
        if (arrayList != null || arrayList2 != null || arrayList3 != null) {
            if (arrayList == null || arrayList2 == null || arrayList.size() == arrayList2.size()) {
                new Thread(new Runnable(arrayList, str, accountInstance, arrayList2, str2, j, messageObject, messageObject2, z, i, arrayList3, inputContentInfoCompat) {
                    private final /* synthetic */ ArrayList f$0;
                    private final /* synthetic */ String f$1;
                    private final /* synthetic */ ArrayList f$10;
                    private final /* synthetic */ InputContentInfoCompat f$11;
                    private final /* synthetic */ AccountInstance f$2;
                    private final /* synthetic */ ArrayList f$3;
                    private final /* synthetic */ String f$4;
                    private final /* synthetic */ long f$5;
                    private final /* synthetic */ MessageObject f$6;
                    private final /* synthetic */ MessageObject f$7;
                    private final /* synthetic */ boolean f$8;
                    private final /* synthetic */ int f$9;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r8;
                        this.f$7 = r9;
                        this.f$8 = r10;
                        this.f$9 = r11;
                        this.f$10 = r12;
                        this.f$11 = r13;
                    }

                    public final void run() {
                        SendMessagesHelper.lambda$prepareSendingDocuments$60(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
                    }
                }).start();
            }
        }
    }

    static /* synthetic */ void lambda$prepareSendingDocuments$60(ArrayList arrayList, String str, AccountInstance accountInstance, ArrayList arrayList2, String str2, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, ArrayList arrayList3, InputContentInfoCompat inputContentInfoCompat) {
        boolean z2;
        ArrayList arrayList4 = arrayList;
        ArrayList arrayList5 = arrayList3;
        int i2 = 0;
        if (arrayList4 != null) {
            int i3 = 0;
            z2 = false;
            while (i3 < arrayList.size()) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList4.get(i3), (String) arrayList2.get(i3), (Uri) null, str2, j, messageObject, i3 == 0 ? str : null, (ArrayList<TLRPC.MessageEntity>) null, messageObject2, false, z, i)) {
                    z2 = true;
                }
                i3++;
            }
        } else {
            z2 = false;
        }
        if (arrayList5 != null) {
            while (i2 < arrayList3.size()) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) null, (String) null, (Uri) arrayList5.get(i2), str2, j, messageObject, (i2 == 0 && (arrayList4 == null || arrayList.size() == 0)) ? str : null, (ArrayList<TLRPC.MessageEntity>) null, messageObject2, false, z, i)) {
                    z2 = true;
                }
                i2++;
            }
        }
        if (inputContentInfoCompat != null) {
            inputContentInfoCompat.releasePermission();
        }
        if (z2) {
            AndroidUtilities.runOnUIThread($$Lambda$SendMessagesHelper$9FWiBOGXPtJb0IsUnmHjYiHVSA.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$null$59() {
        try {
            Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", NUM), 0).show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, Uri uri, long j, MessageObject messageObject, CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, ArrayList<TLRPC.InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject2, boolean z, int i2) {
        ArrayList<TLRPC.InputDocument> arrayList3 = arrayList2;
        SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
        sendingMediaInfo.path = str;
        sendingMediaInfo.uri = uri;
        if (charSequence != null) {
            sendingMediaInfo.caption = charSequence.toString();
        }
        sendingMediaInfo.entities = arrayList;
        sendingMediaInfo.ttl = i;
        if (arrayList3 != null && !arrayList2.isEmpty()) {
            sendingMediaInfo.masks = new ArrayList<>(arrayList3);
        }
        ArrayList arrayList4 = new ArrayList();
        arrayList4.add(sendingMediaInfo);
        prepareSendingMedia(accountInstance, arrayList4, j, messageObject, inputContentInfoCompat, false, false, messageObject2, z, i2);
    }

    public static void prepareSendingBotContextResult(AccountInstance accountInstance, TLRPC.BotInlineResult botInlineResult, HashMap<String, String> hashMap, long j, MessageObject messageObject, boolean z, int i) {
        TLRPC.BotInlineResult botInlineResult2 = botInlineResult;
        if (botInlineResult2 != null) {
            TLRPC.BotInlineMessage botInlineMessage = botInlineResult2.send_message;
            if (botInlineMessage instanceof TLRPC.TL_botInlineMessageMediaAuto) {
                new Thread(new Runnable(j, botInlineResult, accountInstance, hashMap, messageObject, z, i) {
                    private final /* synthetic */ long f$0;
                    private final /* synthetic */ TLRPC.BotInlineResult f$1;
                    private final /* synthetic */ AccountInstance f$2;
                    private final /* synthetic */ HashMap f$3;
                    private final /* synthetic */ MessageObject f$4;
                    private final /* synthetic */ boolean f$5;
                    private final /* synthetic */ int f$6;

                    {
                        this.f$0 = r1;
                        this.f$1 = r3;
                        this.f$2 = r4;
                        this.f$3 = r5;
                        this.f$4 = r6;
                        this.f$5 = r7;
                        this.f$6 = r8;
                    }

                    public final void run() {
                        SendMessagesHelper.lambda$prepareSendingBotContextResult$62(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                    }
                }).run();
            } else if (botInlineMessage instanceof TLRPC.TL_botInlineMessageText) {
                TLRPC.TL_webPagePending tL_webPagePending = null;
                if (((int) j) == 0) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= botInlineResult2.send_message.entities.size()) {
                            break;
                        }
                        TLRPC.MessageEntity messageEntity = botInlineResult2.send_message.entities.get(i2);
                        if (messageEntity instanceof TLRPC.TL_messageEntityUrl) {
                            tL_webPagePending = new TLRPC.TL_webPagePending();
                            String str = botInlineResult2.send_message.message;
                            int i3 = messageEntity.offset;
                            tL_webPagePending.url = str.substring(i3, messageEntity.length + i3);
                            break;
                        }
                        i2++;
                    }
                }
                TLRPC.TL_webPagePending tL_webPagePending2 = tL_webPagePending;
                SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
                TLRPC.BotInlineMessage botInlineMessage2 = botInlineResult2.send_message;
                sendMessagesHelper.sendMessage(botInlineMessage2.message, j, messageObject, tL_webPagePending2, !botInlineMessage2.no_webpage, botInlineMessage2.entities, botInlineMessage2.reply_markup, hashMap, z, i);
            } else {
                long j2 = j;
                if (botInlineMessage instanceof TLRPC.TL_botInlineMessageMediaVenue) {
                    TLRPC.TL_messageMediaVenue tL_messageMediaVenue = new TLRPC.TL_messageMediaVenue();
                    TLRPC.BotInlineMessage botInlineMessage3 = botInlineResult2.send_message;
                    tL_messageMediaVenue.geo = botInlineMessage3.geo;
                    tL_messageMediaVenue.address = botInlineMessage3.address;
                    tL_messageMediaVenue.title = botInlineMessage3.title;
                    tL_messageMediaVenue.provider = botInlineMessage3.provider;
                    tL_messageMediaVenue.venue_id = botInlineMessage3.venue_id;
                    String str2 = botInlineMessage3.venue_type;
                    tL_messageMediaVenue.venue_id = str2;
                    tL_messageMediaVenue.venue_type = str2;
                    if (tL_messageMediaVenue.venue_type == null) {
                        tL_messageMediaVenue.venue_type = "";
                    }
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC.MessageMedia) tL_messageMediaVenue, j, messageObject, botInlineResult2.send_message.reply_markup, hashMap, z, i);
                } else if (botInlineMessage instanceof TLRPC.TL_botInlineMessageMediaGeo) {
                    if (botInlineMessage.period != 0) {
                        TLRPC.TL_messageMediaGeoLive tL_messageMediaGeoLive = new TLRPC.TL_messageMediaGeoLive();
                        TLRPC.BotInlineMessage botInlineMessage4 = botInlineResult2.send_message;
                        tL_messageMediaGeoLive.period = botInlineMessage4.period;
                        tL_messageMediaGeoLive.geo = botInlineMessage4.geo;
                        accountInstance.getSendMessagesHelper().sendMessage((TLRPC.MessageMedia) tL_messageMediaGeoLive, j, messageObject, botInlineResult2.send_message.reply_markup, hashMap, z, i);
                        return;
                    }
                    TLRPC.TL_messageMediaGeo tL_messageMediaGeo = new TLRPC.TL_messageMediaGeo();
                    tL_messageMediaGeo.geo = botInlineResult2.send_message.geo;
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC.MessageMedia) tL_messageMediaGeo, j, messageObject, botInlineResult2.send_message.reply_markup, hashMap, z, i);
                } else if (botInlineMessage instanceof TLRPC.TL_botInlineMessageMediaContact) {
                    TLRPC.TL_user tL_user = new TLRPC.TL_user();
                    TLRPC.BotInlineMessage botInlineMessage5 = botInlineResult2.send_message;
                    tL_user.phone = botInlineMessage5.phone_number;
                    tL_user.first_name = botInlineMessage5.first_name;
                    tL_user.last_name = botInlineMessage5.last_name;
                    TLRPC.TL_restrictionReason tL_restrictionReason = new TLRPC.TL_restrictionReason();
                    tL_restrictionReason.text = botInlineResult2.send_message.vcard;
                    tL_restrictionReason.platform = "";
                    tL_restrictionReason.reason = "";
                    tL_user.restriction_reason.add(tL_restrictionReason);
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC.User) tL_user, j, messageObject, botInlineResult2.send_message.reply_markup, hashMap, z, i);
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v19, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v2, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v76, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v77, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v78, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v80, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v81, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v82, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v85, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v88, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v27, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r0v87 */
    /* JADX WARNING: type inference failed for: r0v89 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x03cf  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0428  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingBotContextResult$62(long r20, org.telegram.tgnet.TLRPC.BotInlineResult r22, org.telegram.messenger.AccountInstance r23, java.util.HashMap r24, org.telegram.messenger.MessageObject r25, boolean r26, int r27) {
        /*
            r10 = r22
            r11 = r24
            r7 = r20
            int r0 = (int) r7
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x000d
            r12 = 1
            goto L_0x000e
        L_0x000d:
            r12 = 0
        L_0x000e:
            java.lang.String r3 = r10.type
            java.lang.String r4 = "game"
            boolean r3 = r4.equals(r3)
            r5 = 0
            if (r3 == 0) goto L_0x004f
            if (r0 != 0) goto L_0x001c
            return
        L_0x001c:
            org.telegram.tgnet.TLRPC$TL_game r0 = new org.telegram.tgnet.TLRPC$TL_game
            r0.<init>()
            java.lang.String r3 = r10.title
            r0.title = r3
            java.lang.String r3 = r10.description
            r0.description = r3
            java.lang.String r3 = r10.id
            r0.short_name = r3
            org.telegram.tgnet.TLRPC$Photo r3 = r10.photo
            r0.photo = r3
            org.telegram.tgnet.TLRPC$Photo r3 = r0.photo
            if (r3 != 0) goto L_0x003c
            org.telegram.tgnet.TLRPC$TL_photoEmpty r3 = new org.telegram.tgnet.TLRPC$TL_photoEmpty
            r3.<init>()
            r0.photo = r3
        L_0x003c:
            org.telegram.tgnet.TLRPC$Document r3 = r10.document
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_document
            if (r6 == 0) goto L_0x0049
            r0.document = r3
            int r3 = r0.flags
            r3 = r3 | r2
            r0.flags = r3
        L_0x0049:
            r18 = r0
            r0 = r5
            r6 = r0
            goto L_0x0410
        L_0x004f:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMediaResult
            if (r0 == 0) goto L_0x0071
            org.telegram.tgnet.TLRPC$Document r0 = r10.document
            if (r0 == 0) goto L_0x0065
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document
            if (r3 == 0) goto L_0x040a
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC.TL_document) r0
            r6 = r5
            r18 = r6
            r5 = r0
            r0 = r18
            goto L_0x0410
        L_0x0065:
            org.telegram.tgnet.TLRPC$Photo r0 = r10.photo
            if (r0 == 0) goto L_0x040a
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photo
            if (r3 == 0) goto L_0x040a
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0
            goto L_0x040d
        L_0x0071:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r10.content
            if (r0 == 0) goto L_0x040a
            java.io.File r0 = new java.io.File
            r3 = 4
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r3)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            org.telegram.tgnet.TLRPC$WebDocument r13 = r10.content
            java.lang.String r13 = r13.url
            java.lang.String r13 = org.telegram.messenger.Utilities.MD5(r13)
            r9.append(r13)
            java.lang.String r13 = "."
            r9.append(r13)
            org.telegram.tgnet.TLRPC$WebDocument r14 = r10.content
            java.lang.String r14 = r14.url
            java.lang.String r15 = "file"
            java.lang.String r14 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r14, r15)
            r9.append(r14)
            java.lang.String r9 = r9.toString()
            r0.<init>(r6, r9)
            boolean r6 = r0.exists()
            if (r6 == 0) goto L_0x00b0
            java.lang.String r6 = r0.getAbsolutePath()
            goto L_0x00b4
        L_0x00b0:
            org.telegram.tgnet.TLRPC$WebDocument r6 = r10.content
            java.lang.String r6 = r6.url
        L_0x00b4:
            java.lang.String r9 = r10.type
            int r14 = r9.hashCode()
            java.lang.String r4 = "gif"
            java.lang.String r3 = "sticker"
            r2 = 2
            switch(r14) {
                case -1890252483: goto L_0x00fd;
                case 102340: goto L_0x00f5;
                case 3143036: goto L_0x00ed;
                case 93166550: goto L_0x00e3;
                case 106642994: goto L_0x00d9;
                case 112202875: goto L_0x00ce;
                case 112386354: goto L_0x00c3;
                default: goto L_0x00c2;
            }
        L_0x00c2:
            goto L_0x0105
        L_0x00c3:
            java.lang.String r14 = "voice"
            boolean r9 = r9.equals(r14)
            if (r9 == 0) goto L_0x0105
            r9 = 1
            goto L_0x0106
        L_0x00ce:
            java.lang.String r14 = "video"
            boolean r9 = r9.equals(r14)
            if (r9 == 0) goto L_0x0105
            r9 = 3
            goto L_0x0106
        L_0x00d9:
            java.lang.String r14 = "photo"
            boolean r9 = r9.equals(r14)
            if (r9 == 0) goto L_0x0105
            r9 = 6
            goto L_0x0106
        L_0x00e3:
            java.lang.String r14 = "audio"
            boolean r9 = r9.equals(r14)
            if (r9 == 0) goto L_0x0105
            r9 = 0
            goto L_0x0106
        L_0x00ed:
            boolean r9 = r9.equals(r15)
            if (r9 == 0) goto L_0x0105
            r9 = 2
            goto L_0x0106
        L_0x00f5:
            boolean r9 = r9.equals(r4)
            if (r9 == 0) goto L_0x0105
            r9 = 5
            goto L_0x0106
        L_0x00fd:
            boolean r9 = r9.equals(r3)
            if (r9 == 0) goto L_0x0105
            r9 = 4
            goto L_0x0106
        L_0x0105:
            r9 = -1
        L_0x0106:
            switch(r9) {
                case 0: goto L_0x015b;
                case 1: goto L_0x015b;
                case 2: goto L_0x015b;
                case 3: goto L_0x015b;
                case 4: goto L_0x015b;
                case 5: goto L_0x015b;
                case 6: goto L_0x0110;
                default: goto L_0x0109;
            }
        L_0x0109:
            r13 = r5
            r0 = r13
            r5 = r0
        L_0x010c:
            r18 = r5
            goto L_0x0410
        L_0x0110:
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x011f
            org.telegram.messenger.SendMessagesHelper r0 = r23.getSendMessagesHelper()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r6, r5)
            goto L_0x0120
        L_0x011f:
            r0 = r5
        L_0x0120:
            if (r0 != 0) goto L_0x010c
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r2 = r23.getConnectionsManager()
            int r2 = r2.getCurrentTime()
            r0.date = r2
            byte[] r2 = new byte[r1]
            r0.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize r2 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r2.<init>()
            int[] r3 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r4 = r3[r1]
            r2.w = r4
            r4 = 1
            r3 = r3[r4]
            r2.h = r3
            r2.size = r4
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r3 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r3.<init>()
            r2.location = r3
            java.lang.String r3 = "x"
            r2.type = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r0.sizes
            r3.add(r2)
            goto L_0x010c
        L_0x015b:
            org.telegram.tgnet.TLRPC$TL_document r9 = new org.telegram.tgnet.TLRPC$TL_document
            r9.<init>()
            r19 = r6
            r5 = 0
            r9.id = r5
            r9.size = r1
            r9.dc_id = r1
            org.telegram.tgnet.TLRPC$WebDocument r0 = r10.content
            java.lang.String r0 = r0.mime_type
            r9.mime_type = r0
            byte[] r0 = new byte[r1]
            r9.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r23.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r9.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r5.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r9.attributes
            r0.add(r5)
            java.lang.String r0 = r10.type
            int r6 = r0.hashCode()
            switch(r6) {
                case -1890252483: goto L_0x01c2;
                case 102340: goto L_0x01ba;
                case 3143036: goto L_0x01b2;
                case 93166550: goto L_0x01a8;
                case 112202875: goto L_0x019d;
                case 112386354: goto L_0x0192;
                default: goto L_0x0191;
            }
        L_0x0191:
            goto L_0x01ca
        L_0x0192:
            java.lang.String r3 = "voice"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x01ca
            r0 = 1
            goto L_0x01cb
        L_0x019d:
            java.lang.String r3 = "video"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x01ca
            r0 = 4
            goto L_0x01cb
        L_0x01a8:
            java.lang.String r3 = "audio"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x01ca
            r0 = 2
            goto L_0x01cb
        L_0x01b2:
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x01ca
            r0 = 3
            goto L_0x01cb
        L_0x01ba:
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x01ca
            r0 = 0
            goto L_0x01cb
        L_0x01c2:
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x01ca
            r0 = 5
            goto L_0x01cb
        L_0x01ca:
            r0 = -1
        L_0x01cb:
            r3 = 55
            if (r0 == 0) goto L_0x0361
            r4 = 1
            if (r0 == r4) goto L_0x0348
            if (r0 == r2) goto L_0x031d
            r4 = 3
            if (r0 == r4) goto L_0x02ed
            r2 = 1119092736(0x42b40000, float:90.0)
            r4 = 4
            if (r0 == r4) goto L_0x026f
            r4 = 5
            if (r0 == r4) goto L_0x01e4
        L_0x01df:
            r6 = r19
            r13 = 0
            goto L_0x03c5
        L_0x01e4:
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            java.lang.String r4 = ""
            r0.alt = r4
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r4.<init>()
            r0.stickerset = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r9.attributes
            r4.add(r0)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r0.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r6 = r4[r1]
            r0.w = r6
            r6 = 1
            r4 = r4[r6]
            r0.h = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r9.attributes
            r4.add(r0)
            java.lang.String r0 = "sticker.webp"
            r5.file_name = r0
            org.telegram.tgnet.TLRPC$WebDocument r0 = r10.thumb     // Catch:{ all -> 0x0269 }
            if (r0 == 0) goto L_0x01df
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0269 }
            r4 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ all -> 0x0269 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0269 }
            r6.<init>()     // Catch:{ all -> 0x0269 }
            org.telegram.tgnet.TLRPC$WebDocument r14 = r10.thumb     // Catch:{ all -> 0x0269 }
            java.lang.String r14 = r14.url     // Catch:{ all -> 0x0269 }
            java.lang.String r14 = org.telegram.messenger.Utilities.MD5(r14)     // Catch:{ all -> 0x0269 }
            r6.append(r14)     // Catch:{ all -> 0x0269 }
            r6.append(r13)     // Catch:{ all -> 0x0269 }
            org.telegram.tgnet.TLRPC$WebDocument r13 = r10.thumb     // Catch:{ all -> 0x0269 }
            java.lang.String r13 = r13.url     // Catch:{ all -> 0x0269 }
            java.lang.String r14 = "webp"
            java.lang.String r13 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r13, r14)     // Catch:{ all -> 0x0269 }
            r6.append(r13)     // Catch:{ all -> 0x0269 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0269 }
            r0.<init>(r4, r6)     // Catch:{ all -> 0x0269 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x0269 }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r4, r2, r2, r6)     // Catch:{ all -> 0x0269 }
            if (r0 == 0) goto L_0x01df
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r3, r1)     // Catch:{ all -> 0x0269 }
            if (r2 == 0) goto L_0x0264
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r9.thumbs     // Catch:{ all -> 0x0269 }
            r3.add(r2)     // Catch:{ all -> 0x0269 }
            int r2 = r9.flags     // Catch:{ all -> 0x0269 }
            r3 = 1
            r2 = r2 | r3
            r9.flags = r2     // Catch:{ all -> 0x0269 }
        L_0x0264:
            r0.recycle()     // Catch:{ all -> 0x0269 }
            goto L_0x01df
        L_0x0269:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01df
        L_0x026f:
            java.lang.String r0 = "video.mp4"
            r5.file_name = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r0.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r6 = r4[r1]
            r0.w = r6
            r6 = 1
            r4 = r4[r6]
            r0.h = r4
            int r4 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r4
            r0.supports_streaming = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r9.attributes
            r4.add(r0)
            org.telegram.tgnet.TLRPC$WebDocument r0 = r10.thumb     // Catch:{ all -> 0x02e7 }
            if (r0 == 0) goto L_0x01df
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x02e7 }
            r4 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ all -> 0x02e7 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x02e7 }
            r6.<init>()     // Catch:{ all -> 0x02e7 }
            org.telegram.tgnet.TLRPC$WebDocument r14 = r10.thumb     // Catch:{ all -> 0x02e7 }
            java.lang.String r14 = r14.url     // Catch:{ all -> 0x02e7 }
            java.lang.String r14 = org.telegram.messenger.Utilities.MD5(r14)     // Catch:{ all -> 0x02e7 }
            r6.append(r14)     // Catch:{ all -> 0x02e7 }
            r6.append(r13)     // Catch:{ all -> 0x02e7 }
            org.telegram.tgnet.TLRPC$WebDocument r13 = r10.thumb     // Catch:{ all -> 0x02e7 }
            java.lang.String r13 = r13.url     // Catch:{ all -> 0x02e7 }
            java.lang.String r14 = "jpg"
            java.lang.String r13 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r13, r14)     // Catch:{ all -> 0x02e7 }
            r6.append(r13)     // Catch:{ all -> 0x02e7 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x02e7 }
            r0.<init>(r4, r6)     // Catch:{ all -> 0x02e7 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x02e7 }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r4, r2, r2, r6)     // Catch:{ all -> 0x02e7 }
            if (r0 == 0) goto L_0x01df
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r3, r1)     // Catch:{ all -> 0x02e7 }
            if (r2 == 0) goto L_0x02e2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r9.thumbs     // Catch:{ all -> 0x02e7 }
            r3.add(r2)     // Catch:{ all -> 0x02e7 }
            int r2 = r9.flags     // Catch:{ all -> 0x02e7 }
            r3 = 1
            r2 = r2 | r3
            r9.flags = r2     // Catch:{ all -> 0x02e7 }
        L_0x02e2:
            r0.recycle()     // Catch:{ all -> 0x02e7 }
            goto L_0x01df
        L_0x02e7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01df
        L_0x02ed:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r10.content
            java.lang.String r0 = r0.mime_type
            r2 = 47
            int r0 = r0.lastIndexOf(r2)
            r2 = -1
            if (r0 == r2) goto L_0x0319
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "file."
            r2.append(r3)
            org.telegram.tgnet.TLRPC$WebDocument r3 = r10.content
            java.lang.String r3 = r3.mime_type
            r4 = 1
            int r0 = r0 + r4
            java.lang.String r0 = r3.substring(r0)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r5.file_name = r0
            goto L_0x01df
        L_0x0319:
            r5.file_name = r15
            goto L_0x01df
        L_0x031d:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r3 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r3
            java.lang.String r3 = r10.title
            r0.title = r3
            int r3 = r0.flags
            r4 = 1
            r3 = r3 | r4
            r0.flags = r3
            java.lang.String r3 = r10.description
            if (r3 == 0) goto L_0x033d
            r0.performer = r3
            int r3 = r0.flags
            r2 = r2 | r3
            r0.flags = r2
        L_0x033d:
            java.lang.String r2 = "audio.mp3"
            r5.file_name = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r9.attributes
            r2.add(r0)
            goto L_0x01df
        L_0x0348:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r2 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r2
            r2 = 1
            r0.voice = r2
            java.lang.String r2 = "audio.ogg"
            r5.file_name = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r9.attributes
            r2.add(r0)
            goto L_0x01df
        L_0x0361:
            java.lang.String r0 = "animation.gif"
            r5.file_name = r0
            java.lang.String r0 = "mp4"
            r6 = r19
            boolean r0 = r6.endsWith(r0)
            if (r0 == 0) goto L_0x037f
            java.lang.String r0 = "video/mp4"
            r9.mime_type = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r9.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r2.<init>()
            r0.add(r2)
            goto L_0x0383
        L_0x037f:
            java.lang.String r0 = "image/gif"
            r9.mime_type = r0
        L_0x0383:
            if (r12 == 0) goto L_0x0388
            r4 = 90
            goto L_0x038a
        L_0x0388:
            r4 = 320(0x140, float:4.48E-43)
        L_0x038a:
            java.lang.String r0 = "mp4"
            boolean r0 = r6.endsWith(r0)     // Catch:{ all -> 0x03c0 }
            if (r0 == 0) goto L_0x0399
            r2 = 1
            android.graphics.Bitmap r0 = android.media.ThumbnailUtils.createVideoThumbnail(r6, r2)     // Catch:{ all -> 0x03c0 }
            r13 = 0
            goto L_0x03a0
        L_0x0399:
            r2 = 1
            float r0 = (float) r4
            r13 = 0
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r6, r13, r0, r0, r2)     // Catch:{ all -> 0x03be }
        L_0x03a0:
            if (r0 == 0) goto L_0x03c5
            float r2 = (float) r4     // Catch:{ all -> 0x03be }
            r14 = 90
            if (r4 <= r14) goto L_0x03a9
            r3 = 80
        L_0x03a9:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r3, r1)     // Catch:{ all -> 0x03be }
            if (r2 == 0) goto L_0x03ba
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r9.thumbs     // Catch:{ all -> 0x03be }
            r3.add(r2)     // Catch:{ all -> 0x03be }
            int r2 = r9.flags     // Catch:{ all -> 0x03be }
            r3 = 1
            r2 = r2 | r3
            r9.flags = r2     // Catch:{ all -> 0x03be }
        L_0x03ba:
            r0.recycle()     // Catch:{ all -> 0x03be }
            goto L_0x03c5
        L_0x03be:
            r0 = move-exception
            goto L_0x03c2
        L_0x03c0:
            r0 = move-exception
            r13 = 0
        L_0x03c2:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03c5:
            java.lang.String r0 = r5.file_name
            if (r0 != 0) goto L_0x03cb
            r5.file_name = r15
        L_0x03cb:
            java.lang.String r0 = r9.mime_type
            if (r0 != 0) goto L_0x03d3
            java.lang.String r0 = "application/octet-stream"
            r9.mime_type = r0
        L_0x03d3:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r9.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0405
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            int[] r2 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r3 = r2[r1]
            r0.w = r3
            r3 = 1
            r2 = r2[r3]
            r0.h = r2
            r0.size = r1
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r2 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r2.<init>()
            r0.location = r2
            java.lang.String r2 = "x"
            r0.type = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r9.thumbs
            r2.add(r0)
            int r0 = r9.flags
            r0 = r0 | r3
            r9.flags = r0
        L_0x0405:
            r5 = r9
            r0 = r13
            r18 = r0
            goto L_0x0410
        L_0x040a:
            r13 = r5
            r0 = r13
            r5 = r0
        L_0x040d:
            r6 = r5
            r18 = r6
        L_0x0410:
            if (r11 == 0) goto L_0x041d
            org.telegram.tgnet.TLRPC$WebDocument r2 = r10.content
            if (r2 == 0) goto L_0x041d
            java.lang.String r2 = r2.url
            java.lang.String r3 = "originalPath"
            r11.put(r3, r2)
        L_0x041d:
            r2 = 1
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r2]
            java.lang.String[] r4 = new java.lang.String[r2]
            boolean r9 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC.Document) r5)
            if (r9 == 0) goto L_0x0450
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r5.thumbs
            r13 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r13)
            java.io.File r13 = org.telegram.messenger.FileLoader.getPathToAttach(r5)
            boolean r14 = r13.exists()
            if (r14 != 0) goto L_0x043e
            java.io.File r13 = org.telegram.messenger.FileLoader.getPathToAttach(r5, r2)
        L_0x043e:
            java.lang.String r14 = r13.getAbsolutePath()
            r15 = 0
            r16 = 0
            r13 = r5
            ensureMediaThumbExists(r12, r13, r14, r15, r16)
            r2 = 1
            java.lang.String r2 = getKeyForPhotoSize(r9, r3, r2, r2)
            r4[r1] = r2
        L_0x0450:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$MIZbjfh6Bb5p9kEGsqUHYA3MKhI r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$MIZbjfh6Bb5p9kEGsqUHYA3MKhI
            r1 = r16
            r2 = r5
            r5 = r23
            r7 = r20
            r9 = r25
            r10 = r22
            r11 = r24
            r12 = r26
            r13 = r27
            r14 = r0
            r15 = r18
            r1.<init>(r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r16)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$62(long, org.telegram.tgnet.TLRPC$BotInlineResult, org.telegram.messenger.AccountInstance, java.util.HashMap, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$61(TLRPC.TL_document tL_document, Bitmap[] bitmapArr, String[] strArr, AccountInstance accountInstance, String str, long j, MessageObject messageObject, TLRPC.BotInlineResult botInlineResult, HashMap hashMap, boolean z, int i, TLRPC.TL_photo tL_photo, TLRPC.TL_game tL_game) {
        TLRPC.BotInlineResult botInlineResult2 = botInlineResult;
        if (tL_document != null) {
            if (!(bitmapArr[0] == null || strArr[0] == null)) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
            }
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            TLRPC.BotInlineMessage botInlineMessage = botInlineResult2.send_message;
            sendMessagesHelper.sendMessage(tL_document, (VideoEditedInfo) null, str, j, messageObject, botInlineMessage.message, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap, z, i, 0, botInlineResult);
        } else if (tL_photo != null) {
            SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
            TLRPC.WebDocument webDocument = botInlineResult2.content;
            String str2 = webDocument != null ? webDocument.url : null;
            TLRPC.BotInlineMessage botInlineMessage2 = botInlineResult2.send_message;
            sendMessagesHelper2.sendMessage(tL_photo, str2, j, messageObject, botInlineMessage2.message, botInlineMessage2.entities, botInlineMessage2.reply_markup, hashMap, z, i, 0, botInlineResult);
        } else if (tL_game != null) {
            accountInstance.getSendMessagesHelper().sendMessage(tL_game, j, botInlineResult2.send_message.reply_markup, hashMap, z, i);
        }
    }

    private static String getTrimmedString(String str) {
        String trim = str.trim();
        if (trim.length() == 0) {
            return trim;
        }
        while (str.startsWith("\n")) {
            str = str.substring(1);
        }
        while (str.endsWith("\n")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static void prepareSendingText(AccountInstance accountInstance, String str, long j, boolean z, int i) {
        accountInstance.getMessagesStorage().getStorageQueue().postRunnable(new Runnable(str, accountInstance, j, z, i) {
            private final /* synthetic */ String f$0;
            private final /* synthetic */ AccountInstance f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ int f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
            }

            public final void run() {
                Utilities.stageQueue.postRunnable(new Runnable(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4) {
                    private final /* synthetic */ String f$0;
                    private final /* synthetic */ AccountInstance f$1;
                    private final /* synthetic */ long f$2;
                    private final /* synthetic */ boolean f$3;
                    private final /* synthetic */ int f$4;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                        this.f$4 = r6;
                    }

                    public final void run() {
                        AndroidUtilities.runOnUIThread(new Runnable(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4) {
                            private final /* synthetic */ String f$0;
                            private final /* synthetic */ AccountInstance f$1;
                            private final /* synthetic */ long f$2;
                            private final /* synthetic */ boolean f$3;
                            private final /* synthetic */ int f$4;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r5;
                                this.f$4 = r6;
                            }

                            public final void run() {
                                SendMessagesHelper.lambda$null$63(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                            }
                        });
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$null$63(String str, AccountInstance accountInstance, long j, boolean z, int i) {
        String trimmedString = getTrimmedString(str);
        if (trimmedString.length() != 0) {
            int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / 4096.0f));
            int i2 = 0;
            while (i2 < ceil) {
                int i3 = i2 * 4096;
                i2++;
                accountInstance.getSendMessagesHelper().sendMessage(trimmedString.substring(i3, Math.min(i2 * 4096, trimmedString.length())), j, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i);
            }
        }
    }

    public static void ensureMediaThumbExists(boolean z, TLObject tLObject, String str, Uri uri, long j) {
        boolean z2;
        TLRPC.PhotoSize scaleAndSaveImage;
        TLRPC.PhotoSize scaleAndSaveImage2;
        TLObject tLObject2 = tLObject;
        String str2 = str;
        Uri uri2 = uri;
        if (tLObject2 instanceof TLRPC.TL_photo) {
            TLRPC.TL_photo tL_photo = (TLRPC.TL_photo) tLObject2;
            TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tL_photo.sizes, 90);
            if (closestPhotoSizeWithSize instanceof TLRPC.TL_photoStrippedSize) {
                z2 = true;
            } else {
                z2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists();
            }
            TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tL_photo.sizes, AndroidUtilities.getPhotoSize());
            boolean exists = FileLoader.getPathToAttach(closestPhotoSizeWithSize2, false).exists();
            if (!z2 || !exists) {
                Bitmap loadBitmap = ImageLoader.loadBitmap(str2, uri2, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
                Bitmap loadBitmap2 = loadBitmap == null ? ImageLoader.loadBitmap(str2, uri2, 800.0f, 800.0f, true) : loadBitmap;
                if (!exists && (scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize2, loadBitmap2, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101, false)) != closestPhotoSizeWithSize2) {
                    tL_photo.sizes.add(0, scaleAndSaveImage2);
                }
                if (!z2 && (scaleAndSaveImage = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize, loadBitmap2, 90.0f, 90.0f, 55, true, false)) != closestPhotoSizeWithSize) {
                    tL_photo.sizes.add(0, scaleAndSaveImage);
                }
                if (loadBitmap2 != null) {
                    loadBitmap2.recycle();
                }
            }
        } else if (tLObject2 instanceof TLRPC.TL_document) {
            TLRPC.TL_document tL_document = (TLRPC.TL_document) tLObject2;
            if ((MessageObject.isVideoDocument(tL_document) || MessageObject.isNewGifDocument((TLRPC.Document) tL_document)) && MessageObject.isDocumentHasThumb(tL_document)) {
                int i = 320;
                TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(tL_document.thumbs, 320);
                if (!(closestPhotoSizeWithSize3 instanceof TLRPC.TL_photoStrippedSize) && !FileLoader.getPathToAttach(closestPhotoSizeWithSize3, true).exists()) {
                    Bitmap createVideoThumbnail = createVideoThumbnail(str2, j);
                    Bitmap createVideoThumbnail2 = createVideoThumbnail == null ? ThumbnailUtils.createVideoThumbnail(str2, 1) : createVideoThumbnail;
                    if (z) {
                        i = 90;
                    }
                    float f = (float) i;
                    tL_document.thumbs.set(0, ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize3, createVideoThumbnail2, f, f, i > 90 ? 80 : 55, false, true));
                }
            }
        }
    }

    public static String getKeyForPhotoSize(TLRPC.PhotoSize photoSize, Bitmap[] bitmapArr, boolean z, boolean z2) {
        if (photoSize == null || photoSize.location == null) {
            return null;
        }
        Point messageSize = ChatMessageCell.getMessageSize(photoSize.w, photoSize.h);
        if (bitmapArr != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                File pathToAttach = FileLoader.getPathToAttach(photoSize, z2);
                FileInputStream fileInputStream = new FileInputStream(pathToAttach);
                BitmapFactory.decodeStream(fileInputStream, (Rect) null, options);
                fileInputStream.close();
                float max = Math.max(((float) options.outWidth) / messageSize.x, ((float) options.outHeight) / messageSize.y);
                if (max < 1.0f) {
                    max = 1.0f;
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int) max;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                if (Build.VERSION.SDK_INT >= 21) {
                    FileInputStream fileInputStream2 = new FileInputStream(pathToAttach);
                    bitmapArr[0] = BitmapFactory.decodeStream(fileInputStream2, (Rect) null, options);
                    fileInputStream2.close();
                }
            } catch (Throwable unused) {
            }
        }
        return String.format(Locale.US, z ? "%d_%d@%d_%d_b" : "%d_%d@%d_%d", new Object[]{Long.valueOf(photoSize.location.volume_id), Integer.valueOf(photoSize.location.local_id), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density))});
    }

    public static void prepareSendingMedia(AccountInstance accountInstance, ArrayList<SendingMediaInfo> arrayList, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, boolean z, boolean z2, MessageObject messageObject2, boolean z3, int i) {
        boolean z4;
        if (!arrayList.isEmpty()) {
            int size = arrayList.size();
            int i2 = 0;
            while (true) {
                ArrayList<SendingMediaInfo> arrayList2 = arrayList;
                if (i2 >= size) {
                    z4 = z2;
                    break;
                } else if (arrayList2.get(i2).ttl > 0) {
                    z4 = false;
                    break;
                } else {
                    i2++;
                }
            }
            mediaSendQueue.postRunnable(new Runnable(arrayList, j, accountInstance, z, z4, messageObject2, messageObject, z3, i, inputContentInfoCompat) {
                private final /* synthetic */ ArrayList f$0;
                private final /* synthetic */ long f$1;
                private final /* synthetic */ AccountInstance f$2;
                private final /* synthetic */ boolean f$3;
                private final /* synthetic */ boolean f$4;
                private final /* synthetic */ MessageObject f$5;
                private final /* synthetic */ MessageObject f$6;
                private final /* synthetic */ boolean f$7;
                private final /* synthetic */ int f$8;
                private final /* synthetic */ InputContentInfoCompat f$9;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                    this.f$8 = r10;
                    this.f$9 = r11;
                }

                public final void run() {
                    SendMessagesHelper.lambda$prepareSendingMedia$72(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:430:0x0a81, code lost:
        if (r5 == (r31 - 1)) goto L_0x0a86;
     */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02b1 A[Catch:{ Exception -> 0x02a3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02bb A[Catch:{ Exception -> 0x02a3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02c8 A[Catch:{ Exception -> 0x02a3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x060b  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x06f0  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0706  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0718  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x072a  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0756  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x075e  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0765  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x08b3  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x08f7  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0a61  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0a90  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0b2b  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x0b3e  */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x0b50 A[LOOP:4: B:451:0x0b4a->B:453:0x0b50, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0b9a  */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x0715 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:471:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0180  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingMedia$72(java.util.ArrayList r56, long r57, org.telegram.messenger.AccountInstance r59, boolean r60, boolean r61, org.telegram.messenger.MessageObject r62, org.telegram.messenger.MessageObject r63, boolean r64, int r65, androidx.core.view.inputmethod.InputContentInfoCompat r66) {
        /*
            r1 = r56
            r14 = r57
            r13 = r59
            long r18 = java.lang.System.currentTimeMillis()
            int r12 = r56.size()
            int r0 = (int) r14
            if (r0 != 0) goto L_0x0013
            r10 = 1
            goto L_0x0014
        L_0x0013:
            r10 = 0
        L_0x0014:
            if (r10 == 0) goto L_0x0031
            r0 = 32
            long r2 = r14 >> r0
            int r0 = (int) r2
            org.telegram.messenger.MessagesController r2 = r59.getMessagesController()
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r2.getEncryptedChat(r0)
            if (r0 == 0) goto L_0x0031
            int r0 = r0.layer
            int r0 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r0)
            r8 = r0
            goto L_0x0032
        L_0x0031:
            r8 = 0
        L_0x0032:
            java.lang.String r6 = ".gif"
            r20 = 3
            java.lang.String r7 = "_"
            if (r10 == 0) goto L_0x003e
            r0 = 73
            if (r8 < r0) goto L_0x016b
        L_0x003e:
            if (r60 != 0) goto L_0x016b
            if (r61 == 0) goto L_0x016b
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r4 = 0
        L_0x0048:
            if (r4 >= r12) goto L_0x0165
            java.lang.Object r2 = r1.get(r4)
            r3 = r2
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r3
            org.telegram.messenger.MediaController$SearchImage r2 = r3.searchImage
            if (r2 != 0) goto L_0x0157
            boolean r2 = r3.isVideo
            if (r2 != 0) goto L_0x0157
            java.lang.String r2 = r3.path
            if (r2 != 0) goto L_0x006c
            android.net.Uri r5 = r3.uri
            if (r5 == 0) goto L_0x006c
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.getPath(r5)
            android.net.Uri r5 = r3.uri
            java.lang.String r5 = r5.toString()
            goto L_0x006d
        L_0x006c:
            r5 = r2
        L_0x006d:
            if (r2 == 0) goto L_0x007f
            boolean r17 = r2.endsWith(r6)
            if (r17 != 0) goto L_0x0157
            java.lang.String r9 = ".webp"
            boolean r9 = r2.endsWith(r9)
            if (r9 == 0) goto L_0x007f
            goto L_0x0157
        L_0x007f:
            java.lang.String r9 = r3.path
            android.net.Uri r11 = r3.uri
            boolean r9 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r9, r11)
            if (r9 == 0) goto L_0x008b
            goto L_0x0157
        L_0x008b:
            if (r2 != 0) goto L_0x00a1
            android.net.Uri r9 = r3.uri
            if (r9 == 0) goto L_0x00a1
            boolean r9 = org.telegram.messenger.MediaController.isGif(r9)
            if (r9 != 0) goto L_0x0157
            android.net.Uri r9 = r3.uri
            boolean r9 = org.telegram.messenger.MediaController.isWebp(r9)
            if (r9 == 0) goto L_0x00a1
            goto L_0x0157
        L_0x00a1:
            if (r2 == 0) goto L_0x00c7
            java.io.File r9 = new java.io.File
            r9.<init>(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r5)
            r11 = r4
            long r4 = r9.length()
            r2.append(r4)
            r2.append(r7)
            long r4 = r9.lastModified()
            r2.append(r4)
            java.lang.String r5 = r2.toString()
            goto L_0x00c9
        L_0x00c7:
            r11 = r4
            r5 = 0
        L_0x00c9:
            if (r10 != 0) goto L_0x012b
            int r2 = r3.ttl
            if (r2 != 0) goto L_0x012b
            org.telegram.messenger.MessagesStorage r2 = r59.getMessagesStorage()
            if (r10 != 0) goto L_0x00d7
            r4 = 0
            goto L_0x00d8
        L_0x00d7:
            r4 = 3
        L_0x00d8:
            java.lang.Object[] r2 = r2.getSentFile(r5, r4)
            if (r2 == 0) goto L_0x00e9
            r4 = 0
            r5 = r2[r4]
            org.telegram.tgnet.TLRPC$TL_photo r5 = (org.telegram.tgnet.TLRPC.TL_photo) r5
            r4 = 1
            r2 = r2[r4]
            java.lang.String r2 = (java.lang.String) r2
            goto L_0x00eb
        L_0x00e9:
            r2 = 0
            r5 = 0
        L_0x00eb:
            if (r5 != 0) goto L_0x0112
            android.net.Uri r4 = r3.uri
            if (r4 == 0) goto L_0x0112
            org.telegram.messenger.MessagesStorage r4 = r59.getMessagesStorage()
            android.net.Uri r9 = r3.uri
            java.lang.String r9 = org.telegram.messenger.AndroidUtilities.getPath(r9)
            r22 = r2
            if (r10 != 0) goto L_0x0101
            r2 = 0
            goto L_0x0102
        L_0x0101:
            r2 = 3
        L_0x0102:
            java.lang.Object[] r2 = r4.getSentFile(r9, r2)
            if (r2 == 0) goto L_0x0114
            r4 = 0
            r5 = r2[r4]
            org.telegram.tgnet.TLRPC$TL_photo r5 = (org.telegram.tgnet.TLRPC.TL_photo) r5
            r4 = 1
            r2 = r2[r4]
            java.lang.String r2 = (java.lang.String) r2
        L_0x0112:
            r22 = r2
        L_0x0114:
            r9 = r5
            java.lang.String r4 = r3.path
            android.net.Uri r5 = r3.uri
            r23 = 0
            r2 = r10
            r25 = r3
            r3 = r9
            r15 = 0
            r14 = r6
            r26 = r7
            r6 = r23
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r2 = r22
            goto L_0x0133
        L_0x012b:
            r25 = r3
            r14 = r6
            r26 = r7
            r15 = 0
            r2 = r15
            r9 = r2
        L_0x0133:
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r3 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker
            r3.<init>()
            r4 = r25
            r0.put(r4, r3)
            if (r9 == 0) goto L_0x0144
            r3.parentObject = r2
            r3.photo = r9
            goto L_0x015c
        L_0x0144:
            java.util.concurrent.CountDownLatch r2 = new java.util.concurrent.CountDownLatch
            r5 = 1
            r2.<init>(r5)
            r3.sync = r2
            java.util.concurrent.ThreadPoolExecutor r2 = mediaSendThreadPool
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$la6jF8kETgbN8qhomDix2KNyxgg r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$la6jF8kETgbN8qhomDix2KNyxgg
            r5.<init>(r13, r4, r10)
            r2.execute(r5)
            goto L_0x015c
        L_0x0157:
            r11 = r4
            r14 = r6
            r26 = r7
            r15 = 0
        L_0x015c:
            int r4 = r11 + 1
            r6 = r14
            r7 = r26
            r14 = r57
            goto L_0x0048
        L_0x0165:
            r14 = r6
            r26 = r7
            r15 = 0
            r11 = r0
            goto L_0x0170
        L_0x016b:
            r14 = r6
            r26 = r7
            r15 = 0
            r11 = r15
        L_0x0170:
            r4 = r15
            r5 = r4
            r22 = r5
            r27 = r22
            r28 = r27
            r0 = 0
            r2 = 0
            r9 = 0
            r23 = 0
        L_0x017e:
            if (r9 >= r12) goto L_0x0b20
            java.lang.Object r16 = r1.get(r9)
            r15 = r16
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r15 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r15
            if (r61 == 0) goto L_0x01a3
            if (r10 == 0) goto L_0x0190
            r6 = 73
            if (r8 < r6) goto L_0x01a3
        L_0x0190:
            r6 = 1
            if (r12 <= r6) goto L_0x01a3
            int r6 = r0 % 10
            if (r6 != 0) goto L_0x01a3
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r2 = r0.nextLong()
            r6 = r2
            r23 = r6
            r16 = 0
            goto L_0x01a9
        L_0x01a3:
            r16 = r0
            r6 = r23
            r23 = r2
        L_0x01a9:
            org.telegram.messenger.MediaController$SearchImage r0 = r15.searchImage
            java.lang.String r2 = "mp4"
            java.lang.String r3 = "originalPath"
            r31 = r5
            java.lang.String r5 = ""
            r34 = 4
            if (r0 == 0) goto L_0x0505
            int r1 = r0.type
            r35 = r4
            java.lang.String r4 = "jpg"
            r36 = r6
            java.lang.String r6 = "."
            r7 = 1
            if (r1 != r7) goto L_0x0386
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            org.telegram.messenger.MediaController$SearchImage r0 = r15.searchImage
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document
            if (r5 == 0) goto L_0x01d9
            r5 = r0
            org.telegram.tgnet.TLRPC$TL_document r5 = (org.telegram.tgnet.TLRPC.TL_document) r5
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r5, r7)
            goto L_0x0206
        L_0x01d9:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r5 = r15.searchImage
            java.lang.String r5 = r5.imageUrl
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)
            r0.append(r5)
            r0.append(r6)
            org.telegram.messenger.MediaController$SearchImage r5 = r15.searchImage
            java.lang.String r5 = r5.imageUrl
            java.lang.String r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r4)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.io.File r5 = new java.io.File
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r34)
            r5.<init>(r7, r0)
            r0 = r5
            r5 = 0
        L_0x0206:
            if (r5 != 0) goto L_0x0322
            org.telegram.tgnet.TLRPC$TL_document r5 = new org.telegram.tgnet.TLRPC$TL_document
            r5.<init>()
            r38 = r8
            r7 = 0
            r5.id = r7
            r7 = 0
            byte[] r8 = new byte[r7]
            r5.file_reference = r8
            org.telegram.tgnet.ConnectionsManager r7 = r59.getConnectionsManager()
            int r7 = r7.getCurrentTime()
            r5.date = r7
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r7.<init>()
            java.lang.String r8 = "animation.gif"
            r7.file_name = r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r5.attributes
            r8.add(r7)
            org.telegram.messenger.MediaController$SearchImage r7 = r15.searchImage
            int r7 = r7.size
            r5.size = r7
            r7 = 0
            r5.dc_id = r7
            java.lang.String r7 = r0.toString()
            boolean r7 = r7.endsWith(r2)
            if (r7 == 0) goto L_0x0253
            java.lang.String r7 = "video/mp4"
            r5.mime_type = r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r5.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r8.<init>()
            r7.add(r8)
            goto L_0x0257
        L_0x0253:
            java.lang.String r7 = "image/gif"
            r5.mime_type = r7
        L_0x0257:
            boolean r7 = r0.exists()
            if (r7 == 0) goto L_0x025f
            r7 = r0
            goto L_0x0261
        L_0x025f:
            r0 = 0
            r7 = 0
        L_0x0261:
            if (r0 != 0) goto L_0x0297
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r8 = r15.searchImage
            java.lang.String r8 = r8.thumbUrl
            java.lang.String r8 = org.telegram.messenger.Utilities.MD5(r8)
            r0.append(r8)
            r0.append(r6)
            org.telegram.messenger.MediaController$SearchImage r6 = r15.searchImage
            java.lang.String r6 = r6.thumbUrl
            java.lang.String r4 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r4)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.io.File r4 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r34)
            r4.<init>(r6, r0)
            boolean r0 = r4.exists()
            if (r0 != 0) goto L_0x0296
            r0 = 0
            goto L_0x0297
        L_0x0296:
            r0 = r4
        L_0x0297:
            if (r0 == 0) goto L_0x02ea
            if (r10 != 0) goto L_0x02a5
            int r4 = r15.ttl     // Catch:{ Exception -> 0x02a3 }
            if (r4 == 0) goto L_0x02a0
            goto L_0x02a5
        L_0x02a0:
            r4 = 320(0x140, float:4.48E-43)
            goto L_0x02a7
        L_0x02a3:
            r0 = move-exception
            goto L_0x02e7
        L_0x02a5:
            r4 = 90
        L_0x02a7:
            java.lang.String r6 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x02a3 }
            boolean r2 = r6.endsWith(r2)     // Catch:{ Exception -> 0x02a3 }
            if (r2 == 0) goto L_0x02bb
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x02a3 }
            r2 = 1
            android.graphics.Bitmap r0 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r2)     // Catch:{ Exception -> 0x02a3 }
            goto L_0x02c6
        L_0x02bb:
            r2 = 1
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x02a3 }
            float r6 = (float) r4     // Catch:{ Exception -> 0x02a3 }
            r8 = 0
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r8, r6, r6, r2)     // Catch:{ Exception -> 0x02a3 }
        L_0x02c6:
            if (r0 == 0) goto L_0x02ea
            float r2 = (float) r4     // Catch:{ Exception -> 0x02a3 }
            r6 = 90
            if (r4 <= r6) goto L_0x02d0
            r4 = 80
            goto L_0x02d2
        L_0x02d0:
            r4 = 55
        L_0x02d2:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r4, r10)     // Catch:{ Exception -> 0x02a3 }
            if (r2 == 0) goto L_0x02e3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r5.thumbs     // Catch:{ Exception -> 0x02a3 }
            r4.add(r2)     // Catch:{ Exception -> 0x02a3 }
            int r2 = r5.flags     // Catch:{ Exception -> 0x02a3 }
            r4 = 1
            r2 = r2 | r4
            r5.flags = r2     // Catch:{ Exception -> 0x02a3 }
        L_0x02e3:
            r0.recycle()     // Catch:{ Exception -> 0x02a3 }
            goto L_0x02ea
        L_0x02e7:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02ea:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r5.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x031e
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r15.searchImage
            int r4 = r2.width
            r0.w = r4
            int r2 = r2.height
            r0.h = r2
            r8 = 0
            r0.size = r8
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r2 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r2.<init>()
            r0.location = r2
            java.lang.String r2 = "x"
            r0.type = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.thumbs
            r2.add(r0)
            int r0 = r5.flags
            r17 = 1
            r0 = r0 | 1
            r5.flags = r0
            goto L_0x0328
        L_0x031e:
            r8 = 0
            r17 = 1
            goto L_0x0328
        L_0x0322:
            r38 = r8
            r8 = 0
            r17 = 1
            r7 = r0
        L_0x0328:
            org.telegram.messenger.MediaController$SearchImage r0 = r15.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r7 != 0) goto L_0x032f
            goto L_0x0333
        L_0x032f:
            java.lang.String r0 = r7.toString()
        L_0x0333:
            r6 = r0
            org.telegram.messenger.MediaController$SearchImage r0 = r15.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r0 == 0) goto L_0x033d
            r1.put(r3, r0)
        L_0x033d:
            r0 = 0
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$nqq2i9mk4k2dAnH27bdICh2H6Hg r21 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$nqq2i9mk4k2dAnH27bdICh2H6Hg
            r2 = r21
            r3 = r62
            r7 = r35
            r4 = r59
            r39 = r31
            r40 = r36
            r29 = 0
            r42 = r7
            r7 = r1
            r1 = r38
            r31 = 0
            r8 = r0
            r44 = r9
            r43 = r10
            r9 = r57
            r45 = r11
            r11 = r63
            r31 = r12
            r12 = r15
            r15 = r13
            r13 = r64
            r46 = r14
            r14 = r65
            r2.<init>(r4, r5, r6, r7, r8, r9, r11, r12, r13, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r21)
            r0 = r16
            r2 = r23
            r37 = r26
            r5 = r39
            r33 = r40
            r4 = r42
            r26 = r43
            r21 = r44
            r25 = r45
            r35 = r46
            goto L_0x04ff
        L_0x0386:
            r1 = r8
            r44 = r9
            r43 = r10
            r45 = r11
            r46 = r14
            r39 = r31
            r42 = r35
            r40 = r36
            r29 = 0
            r31 = r12
            r14 = r13
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photo
            if (r2 == 0) goto L_0x03a5
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0
            r13 = r43
            goto L_0x03ac
        L_0x03a5:
            r13 = r43
            if (r13 != 0) goto L_0x03ab
            int r0 = r15.ttl
        L_0x03ab:
            r0 = 0
        L_0x03ac:
            if (r0 != 0) goto L_0x0475
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.messenger.MediaController$SearchImage r7 = r15.searchImage
            java.lang.String r7 = r7.imageUrl
            java.lang.String r7 = org.telegram.messenger.Utilities.MD5(r7)
            r2.append(r7)
            r2.append(r6)
            org.telegram.messenger.MediaController$SearchImage r7 = r15.searchImage
            java.lang.String r7 = r7.imageUrl
            java.lang.String r7 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r7, r4)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            java.io.File r7 = new java.io.File
            java.io.File r8 = org.telegram.messenger.FileLoader.getDirectory(r34)
            r7.<init>(r8, r2)
            boolean r2 = r7.exists()
            if (r2 == 0) goto L_0x03f8
            long r8 = r7.length()
            int r2 = (r8 > r29 ? 1 : (r8 == r29 ? 0 : -1))
            if (r2 == 0) goto L_0x03f8
            org.telegram.messenger.SendMessagesHelper r0 = r59.getSendMessagesHelper()
            java.lang.String r2 = r7.toString()
            r7 = 0
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r7)
            if (r0 == 0) goto L_0x03f8
            r2 = 0
            goto L_0x03f9
        L_0x03f8:
            r2 = 1
        L_0x03f9:
            if (r0 != 0) goto L_0x0472
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            org.telegram.messenger.MediaController$SearchImage r8 = r15.searchImage
            java.lang.String r8 = r8.thumbUrl
            java.lang.String r8 = org.telegram.messenger.Utilities.MD5(r8)
            r7.append(r8)
            r7.append(r6)
            org.telegram.messenger.MediaController$SearchImage r6 = r15.searchImage
            java.lang.String r6 = r6.thumbUrl
            java.lang.String r4 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r4)
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            java.io.File r6 = new java.io.File
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r34)
            r6.<init>(r7, r4)
            boolean r4 = r6.exists()
            if (r4 == 0) goto L_0x0439
            org.telegram.messenger.SendMessagesHelper r0 = r59.getSendMessagesHelper()
            java.lang.String r4 = r6.toString()
            r6 = 0
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r4, r6)
        L_0x0439:
            if (r0 != 0) goto L_0x0472
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r4 = r59.getConnectionsManager()
            int r4 = r4.getCurrentTime()
            r0.date = r4
            r12 = 0
            byte[] r4 = new byte[r12]
            r0.file_reference = r4
            org.telegram.tgnet.TLRPC$TL_photoSize r4 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r4.<init>()
            org.telegram.messenger.MediaController$SearchImage r6 = r15.searchImage
            int r7 = r6.width
            r4.w = r7
            int r6 = r6.height
            r4.h = r6
            r4.size = r12
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r6 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r6.<init>()
            r4.location = r6
            java.lang.String r6 = "x"
            r4.type = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes
            r6.add(r4)
            goto L_0x0473
        L_0x0472:
            r12 = 0
        L_0x0473:
            r6 = r2
            goto L_0x0477
        L_0x0475:
            r12 = 0
            r6 = 1
        L_0x0477:
            if (r0 == 0) goto L_0x04e3
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r15.searchImage
            java.lang.String r2 = r2.imageUrl
            if (r2 == 0) goto L_0x0487
            r8.put(r3, r2)
        L_0x0487:
            if (r61 == 0) goto L_0x04bd
            int r2 = r16 + 1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r5)
            r10 = r40
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "groupId"
            r8.put(r4, r3)
            r7 = 10
            if (r2 == r7) goto L_0x04af
            int r3 = r31 + -1
            r9 = r44
            if (r9 != r3) goto L_0x04ac
            goto L_0x04b1
        L_0x04ac:
            r16 = r2
            goto L_0x04c1
        L_0x04af:
            r9 = r44
        L_0x04b1:
            java.lang.String r3 = "final"
            java.lang.String r4 = "1"
            r8.put(r3, r4)
            r16 = r2
            r23 = r29
            goto L_0x04c1
        L_0x04bd:
            r10 = r40
            r9 = r44
        L_0x04c1:
            r17 = 0
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$NzRFdrMj_c6d6hXgqa8qx2Mof-g r21 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$NzRFdrMj_c6d6hXgqa8qx2Mof-g
            r2 = r21
            r3 = r62
            r4 = r59
            r5 = r0
            r7 = r15
            r15 = r9
            r9 = r17
            r47 = r10
            r10 = r57
            r12 = r63
            r49 = r13
            r13 = r64
            r14 = r65
            r2.<init>(r4, r5, r6, r7, r8, r9, r10, r12, r13, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r21)
            goto L_0x04e9
        L_0x04e3:
            r49 = r13
            r47 = r40
            r15 = r44
        L_0x04e9:
            r38 = r1
            r21 = r15
            r0 = r16
            r2 = r23
            r37 = r26
            r5 = r39
            r4 = r42
            r25 = r45
            r35 = r46
            r33 = r47
            r26 = r49
        L_0x04ff:
            r17 = 0
            r32 = 0
            goto L_0x0b08
        L_0x0505:
            r42 = r4
            r47 = r6
            r1 = r8
            r49 = r10
            r45 = r11
            r46 = r14
            r39 = r31
            r6 = 90
            r7 = 10
            r29 = 0
            r14 = r9
            r31 = r12
            boolean r0 = r15.isVideo
            if (r0 == 0) goto L_0x0812
            if (r60 == 0) goto L_0x0523
            r0 = 0
            goto L_0x052e
        L_0x0523:
            org.telegram.messenger.VideoEditedInfo r0 = r15.videoEditedInfo
            if (r0 == 0) goto L_0x0528
            goto L_0x052e
        L_0x0528:
            java.lang.String r0 = r15.path
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r0)
        L_0x052e:
            if (r60 != 0) goto L_0x07d4
            if (r0 != 0) goto L_0x053a
            java.lang.String r4 = r15.path
            boolean r2 = r4.endsWith(r2)
            if (r2 == 0) goto L_0x07d4
        L_0x053a:
            java.lang.String r8 = r15.path
            java.io.File r9 = new java.io.File
            r9.<init>(r8)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r8)
            long r10 = r9.length()
            r2.append(r10)
            r12 = r26
            r2.append(r12)
            long r10 = r9.lastModified()
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            if (r0 == 0) goto L_0x05b7
            boolean r11 = r0.muted
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            long r6 = r0.estimatedDuration
            r4.append(r6)
            r4.append(r12)
            long r6 = r0.startTime
            r4.append(r6)
            r4.append(r12)
            long r6 = r0.endTime
            r4.append(r6)
            boolean r2 = r0.muted
            if (r2 == 0) goto L_0x0588
            java.lang.String r2 = "_m"
            goto L_0x0589
        L_0x0588:
            r2 = r5
        L_0x0589:
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            int r4 = r0.resultWidth
            int r6 = r0.originalWidth
            if (r4 == r6) goto L_0x05aa
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r4.append(r12)
            int r2 = r0.resultWidth
            r4.append(r2)
            java.lang.String r2 = r4.toString()
        L_0x05aa:
            long r6 = r0.startTime
            int r4 = (r6 > r29 ? 1 : (r6 == r29 ? 0 : -1))
            if (r4 < 0) goto L_0x05b1
            goto L_0x05b3
        L_0x05b1:
            r6 = r29
        L_0x05b3:
            r10 = r2
            r13 = r49
            goto L_0x05bd
        L_0x05b7:
            r10 = r2
            r6 = r29
            r13 = r49
            r11 = 0
        L_0x05bd:
            if (r13 != 0) goto L_0x05fd
            int r2 = r15.ttl
            if (r2 != 0) goto L_0x05fd
            org.telegram.messenger.MessagesStorage r2 = r59.getMessagesStorage()
            if (r13 != 0) goto L_0x05cb
            r4 = 2
            goto L_0x05cc
        L_0x05cb:
            r4 = 5
        L_0x05cc:
            java.lang.Object[] r2 = r2.getSentFile(r10, r4)
            if (r2 == 0) goto L_0x05fd
            r4 = 0
            r17 = r2[r4]
            org.telegram.tgnet.TLRPC$TL_document r17 = (org.telegram.tgnet.TLRPC.TL_document) r17
            r21 = r5
            r5 = 1
            r2 = r2[r5]
            r26 = r2
            java.lang.String r26 = (java.lang.String) r26
            java.lang.String r2 = r15.path
            r35 = 0
            r36 = r2
            r2 = r13
            r50 = r3
            r3 = r17
            r37 = r12
            r12 = 0
            r4 = r36
            r51 = r21
            r12 = 1
            r5 = r35
            r35 = r6
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r7 = r26
            goto L_0x0609
        L_0x05fd:
            r50 = r3
            r51 = r5
            r35 = r6
            r37 = r12
            r12 = 1
            r7 = 0
            r17 = 0
        L_0x0609:
            if (r17 != 0) goto L_0x06f0
            java.lang.String r2 = r15.path
            r3 = r35
            android.graphics.Bitmap r2 = createVideoThumbnail(r2, r3)
            if (r2 != 0) goto L_0x061b
            java.lang.String r2 = r15.path
            android.graphics.Bitmap r2 = android.media.ThumbnailUtils.createVideoThumbnail(r2, r12)
        L_0x061b:
            if (r2 == 0) goto L_0x0648
            if (r13 != 0) goto L_0x0631
            int r3 = r15.ttl
            if (r3 == 0) goto L_0x0624
            goto L_0x0631
        L_0x0624:
            int r3 = r2.getWidth()
            int r4 = r2.getHeight()
            int r5 = java.lang.Math.max(r3, r4)
            goto L_0x0633
        L_0x0631:
            r5 = 90
        L_0x0633:
            float r3 = (float) r5
            r4 = 90
            if (r5 <= r4) goto L_0x063b
            r4 = 80
            goto L_0x063d
        L_0x063b:
            r4 = 55
        L_0x063d:
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r4, r13)
            r3 = 0
            r4 = 0
            java.lang.String r6 = getKeyForPhotoSize(r5, r3, r12, r4)
            goto L_0x064b
        L_0x0648:
            r4 = 0
            r5 = 0
            r6 = 0
        L_0x064b:
            org.telegram.tgnet.TLRPC$TL_document r3 = new org.telegram.tgnet.TLRPC$TL_document
            r3.<init>()
            byte[] r12 = new byte[r4]
            r3.file_reference = r12
            if (r5 == 0) goto L_0x0661
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r3.thumbs
            r4.add(r5)
            int r4 = r3.flags
            r5 = 1
            r4 = r4 | r5
            r3.flags = r4
        L_0x0661:
            java.lang.String r4 = "video/mp4"
            r3.mime_type = r4
            org.telegram.messenger.UserConfig r4 = r59.getUserConfig()
            r12 = 0
            r4.saveConfig(r12)
            if (r13 == 0) goto L_0x0680
            r4 = 66
            if (r1 < r4) goto L_0x067a
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r4.<init>()
            goto L_0x0688
        L_0x067a:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r4.<init>()
            goto L_0x0688
        L_0x0680:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r4.<init>()
            r5 = 1
            r4.supports_streaming = r5
        L_0x0688:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r3.attributes
            r5.add(r4)
            if (r0 == 0) goto L_0x06d7
            boolean r5 = r0.needConvert()
            if (r5 == 0) goto L_0x06d7
            boolean r5 = r0.muted
            if (r5 == 0) goto L_0x06a9
            java.lang.String r5 = r15.path
            fillVideoAttribute(r5, r4, r0)
            int r5 = r4.w
            r0.originalWidth = r5
            int r5 = r4.h
            r0.originalHeight = r5
            r43 = r13
            goto L_0x06b4
        L_0x06a9:
            r43 = r13
            long r12 = r0.estimatedDuration
            r35 = 1000(0x3e8, double:4.94E-321)
            long r12 = r12 / r35
            int r5 = (int) r12
            r4.duration = r5
        L_0x06b4:
            int r5 = r0.rotationValue
            r9 = 90
            if (r5 == r9) goto L_0x06c8
            r9 = 270(0x10e, float:3.78E-43)
            if (r5 != r9) goto L_0x06bf
            goto L_0x06c8
        L_0x06bf:
            int r5 = r0.resultWidth
            r4.w = r5
            int r5 = r0.resultHeight
            r4.h = r5
            goto L_0x06d0
        L_0x06c8:
            int r5 = r0.resultHeight
            r4.w = r5
            int r5 = r0.resultWidth
            r4.h = r5
        L_0x06d0:
            long r4 = r0.estimatedSize
            int r5 = (int) r4
            r3.size = r5
            r12 = 0
            goto L_0x06ec
        L_0x06d7:
            r43 = r13
            boolean r5 = r9.exists()
            if (r5 == 0) goto L_0x06e6
            long r12 = r9.length()
            int r5 = (int) r12
            r3.size = r5
        L_0x06e6:
            java.lang.String r5 = r15.path
            r12 = 0
            fillVideoAttribute(r5, r4, r12)
        L_0x06ec:
            r9 = r3
            r4 = r6
            r3 = r2
            goto L_0x06f7
        L_0x06f0:
            r43 = r13
            r12 = 0
            r3 = r12
            r4 = r3
            r9 = r17
        L_0x06f7:
            if (r0 == 0) goto L_0x0722
            boolean r2 = r0.muted
            if (r2 == 0) goto L_0x0722
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r9.attributes
            int r2 = r2.size()
            r5 = 0
        L_0x0704:
            if (r5 >= r2) goto L_0x0715
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r9.attributes
            java.lang.Object r6 = r6.get(r5)
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated
            if (r6 == 0) goto L_0x0712
            r2 = 1
            goto L_0x0716
        L_0x0712:
            int r5 = r5 + 1
            goto L_0x0704
        L_0x0715:
            r2 = 0
        L_0x0716:
            if (r2 != 0) goto L_0x0722
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r9.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r5.<init>()
            r2.add(r5)
        L_0x0722:
            if (r0 == 0) goto L_0x0756
            boolean r2 = r0.needConvert()
            if (r2 == 0) goto L_0x0756
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "-2147483648_"
            r2.append(r5)
            int r5 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r2.append(r5)
            java.lang.String r5 = ".mp4"
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r34)
            r5.<init>(r6, r2)
            org.telegram.messenger.SharedConfig.saveConfig()
            java.lang.String r2 = r5.getAbsolutePath()
            r13 = r2
            goto L_0x0757
        L_0x0756:
            r13 = r8
        L_0x0757:
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            if (r10 == 0) goto L_0x0763
            r6 = r50
            r8.put(r6, r10)
        L_0x0763:
            if (r7 == 0) goto L_0x076a
            java.lang.String r2 = "parentObject"
            r8.put(r2, r7)
        L_0x076a:
            if (r11 != 0) goto L_0x07a0
            if (r61 == 0) goto L_0x07a0
            int r2 = r16 + 1
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r10 = r51
            r5.append(r10)
            r10 = r47
            r5.append(r10)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "groupId"
            r8.put(r6, r5)
            r5 = 10
            if (r2 == r5) goto L_0x0794
            int r5 = r31 + -1
            if (r14 != r5) goto L_0x0791
            goto L_0x0794
        L_0x0791:
            r21 = r2
            goto L_0x07a4
        L_0x0794:
            java.lang.String r5 = "final"
            java.lang.String r6 = "1"
            r8.put(r5, r6)
            r21 = r2
            r23 = r29
            goto L_0x07a4
        L_0x07a0:
            r10 = r47
            r21 = r16
        L_0x07a4:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$HV6ayuYlgpOaqnUKa4BtW-YtCVc r25 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$HV6ayuYlgpOaqnUKa4BtW-YtCVc
            r2 = r25
            r16 = 1
            r5 = r62
            r6 = r59
            r26 = r7
            r7 = r0
            r0 = r8
            r8 = r9
            r9 = r13
            r52 = r10
            r10 = r0
            r11 = r26
            r38 = r1
            r16 = r12
            r1 = r37
            r26 = r43
            r12 = r57
            r54 = r14
            r14 = r63
            r32 = r16
            r16 = r64
            r17 = r65
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r25)
            goto L_0x07fc
        L_0x07d4:
            r38 = r1
            r54 = r14
            r1 = r26
            r52 = r47
            r26 = r49
            r32 = 0
            java.lang.String r4 = r15.path
            r5 = 0
            r6 = 0
            java.lang.String r10 = r15.caption
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r11 = r15.entities
            r2 = r59
            r3 = r4
            r7 = r57
            r9 = r63
            r12 = r62
            r13 = r60
            r14 = r64
            r15 = r65
            prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15)
            r21 = r16
        L_0x07fc:
            r37 = r1
            r0 = r21
            r2 = r23
            r5 = r39
            r4 = r42
            r25 = r45
            r35 = r46
        L_0x080a:
            r33 = r52
            r21 = r54
            r17 = 0
            goto L_0x0b08
        L_0x0812:
            r38 = r1
            r6 = r3
            r10 = r5
            r54 = r14
            r1 = r26
            r52 = r47
            r26 = r49
            r5 = 10
            r32 = 0
            java.lang.String r0 = r15.path
            if (r0 != 0) goto L_0x0835
            android.net.Uri r2 = r15.uri
            if (r2 == 0) goto L_0x0835
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r2)
            android.net.Uri r2 = r15.uri
            java.lang.String r2 = r2.toString()
            goto L_0x0836
        L_0x0835:
            r2 = r0
        L_0x0836:
            if (r60 != 0) goto L_0x089f
            java.lang.String r3 = r15.path
            android.net.Uri r4 = r15.uri
            boolean r3 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r3, r4)
            if (r3 == 0) goto L_0x0843
            goto L_0x089f
        L_0x0843:
            r14 = r46
            if (r0 == 0) goto L_0x0864
            boolean r3 = r0.endsWith(r14)
            if (r3 != 0) goto L_0x0855
            java.lang.String r3 = ".webp"
            boolean r3 = r0.endsWith(r3)
            if (r3 == 0) goto L_0x0864
        L_0x0855:
            boolean r3 = r0.endsWith(r14)
            if (r3 == 0) goto L_0x085e
            java.lang.String r3 = "gif"
            goto L_0x0861
        L_0x085e:
            java.lang.String r3 = "webp"
        L_0x0861:
            r22 = r3
            goto L_0x08af
        L_0x0864:
            if (r0 != 0) goto L_0x089c
            android.net.Uri r3 = r15.uri
            if (r3 == 0) goto L_0x089c
            boolean r3 = org.telegram.messenger.MediaController.isGif(r3)
            if (r3 == 0) goto L_0x0881
            android.net.Uri r0 = r15.uri
            java.lang.String r2 = r0.toString()
            android.net.Uri r0 = r15.uri
            java.lang.String r3 = "gif"
            java.lang.String r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3)
            java.lang.String r22 = "gif"
            goto L_0x08af
        L_0x0881:
            android.net.Uri r3 = r15.uri
            boolean r3 = org.telegram.messenger.MediaController.isWebp(r3)
            if (r3 == 0) goto L_0x089c
            android.net.Uri r0 = r15.uri
            java.lang.String r2 = r0.toString()
            android.net.Uri r0 = r15.uri
            java.lang.String r3 = "webp"
            java.lang.String r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3)
            java.lang.String r22 = "webp"
            goto L_0x08af
        L_0x089c:
            r8 = r0
            r0 = 0
            goto L_0x08b1
        L_0x089f:
            r14 = r46
            if (r0 == 0) goto L_0x08ad
            java.io.File r3 = new java.io.File
            r3.<init>(r0)
            java.lang.String r3 = org.telegram.messenger.FileLoader.getFileExtension(r3)
            goto L_0x0861
        L_0x08ad:
            r22 = r10
        L_0x08af:
            r8 = r0
            r0 = 1
        L_0x08b1:
            if (r0 == 0) goto L_0x08f7
            r13 = r39
            if (r13 != 0) goto L_0x08d0
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r27 = new java.util.ArrayList
            r27.<init>()
            java.util.ArrayList r28 = new java.util.ArrayList
            r28.<init>()
            r0 = r27
            r3 = r28
            goto L_0x08d7
        L_0x08d0:
            r5 = r13
            r0 = r27
            r3 = r28
            r4 = r42
        L_0x08d7:
            r5.add(r8)
            r4.add(r2)
            java.lang.String r2 = r15.caption
            r0.add(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r15.entities
            r3.add(r2)
            r27 = r0
            r37 = r1
            r28 = r3
            r35 = r14
            r0 = r16
            r2 = r23
            r25 = r45
            goto L_0x080a
        L_0x08f7:
            r13 = r39
            if (r8 == 0) goto L_0x091f
            java.io.File r0 = new java.io.File
            r0.<init>(r8)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            long r11 = r0.length()
            r3.append(r11)
            r3.append(r1)
            long r11 = r0.lastModified()
            r3.append(r11)
            java.lang.String r0 = r3.toString()
            r9 = r0
            goto L_0x0921
        L_0x091f:
            r9 = r32
        L_0x0921:
            r12 = r45
            if (r12 == 0) goto L_0x0948
            java.lang.Object r0 = r12.get(r15)
            r2 = r0
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
            if (r0 != 0) goto L_0x0940
            java.util.concurrent.CountDownLatch r0 = r2.sync     // Catch:{ Exception -> 0x0938 }
            r0.await()     // Catch:{ Exception -> 0x0938 }
            goto L_0x093c
        L_0x0938:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x093c:
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
        L_0x0940:
            r7 = r0
            r37 = r1
            r11 = r6
            r1 = 1
            r6 = r3
            goto L_0x09d2
        L_0x0948:
            if (r26 != 0) goto L_0x09a9
            int r0 = r15.ttl
            if (r0 != 0) goto L_0x09a9
            org.telegram.messenger.MessagesStorage r0 = r59.getMessagesStorage()
            if (r26 != 0) goto L_0x0956
            r2 = 0
            goto L_0x0957
        L_0x0956:
            r2 = 3
        L_0x0957:
            java.lang.Object[] r0 = r0.getSentFile(r9, r2)
            if (r0 == 0) goto L_0x0968
            r11 = 0
            r2 = r0[r11]
            org.telegram.tgnet.TLRPC$TL_photo r2 = (org.telegram.tgnet.TLRPC.TL_photo) r2
            r7 = 1
            r0 = r0[r7]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x096d
        L_0x0968:
            r7 = 1
            r11 = 0
            r0 = r32
            r2 = r0
        L_0x096d:
            if (r2 != 0) goto L_0x0993
            android.net.Uri r3 = r15.uri
            if (r3 == 0) goto L_0x0993
            org.telegram.messenger.MessagesStorage r3 = r59.getMessagesStorage()
            android.net.Uri r4 = r15.uri
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.getPath(r4)
            if (r26 != 0) goto L_0x0981
            r5 = 0
            goto L_0x0982
        L_0x0981:
            r5 = 3
        L_0x0982:
            java.lang.Object[] r3 = r3.getSentFile(r4, r5)
            if (r3 == 0) goto L_0x0993
            r0 = r3[r11]
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0
            r2 = r3[r7]
            java.lang.String r2 = (java.lang.String) r2
            r17 = r2
            goto L_0x0996
        L_0x0993:
            r17 = r0
            r0 = r2
        L_0x0996:
            java.lang.String r4 = r15.path
            android.net.Uri r5 = r15.uri
            r35 = 0
            r2 = r26
            r3 = r0
            r37 = r1
            r11 = r6
            r1 = 1
            r6 = r35
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            goto L_0x09b1
        L_0x09a9:
            r37 = r1
            r11 = r6
            r1 = 1
            r0 = r32
            r17 = r0
        L_0x09b1:
            if (r0 != 0) goto L_0x09cf
            org.telegram.messenger.SendMessagesHelper r0 = r59.getSendMessagesHelper()
            java.lang.String r2 = r15.path
            android.net.Uri r3 = r15.uri
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r3)
            if (r26 == 0) goto L_0x09cf
            boolean r2 = r15.canDeleteAfter
            if (r2 == 0) goto L_0x09cf
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r15.path
            r2.<init>(r3)
            r2.delete()
        L_0x09cf:
            r7 = r0
            r6 = r17
        L_0x09d2:
            if (r7 == 0) goto L_0x0ac3
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r1]
            java.lang.String[] r4 = new java.lang.String[r1]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r0 = r15.masks
            if (r0 == 0) goto L_0x09e9
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x09e9
            r0 = 1
            goto L_0x09ea
        L_0x09e9:
            r0 = 0
        L_0x09ea:
            r7.has_stickers = r0
            if (r0 == 0) goto L_0x0a2d
            org.telegram.tgnet.SerializedData r0 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r15.masks
            int r2 = r2.size()
            int r2 = r2 * 20
            int r2 = r2 + 4
            r0.<init>((int) r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r15.masks
            int r2 = r2.size()
            r0.writeInt32(r2)
            r2 = 0
        L_0x0a07:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r15.masks
            int r5 = r5.size()
            if (r2 >= r5) goto L_0x0a1d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r15.masks
            java.lang.Object r5 = r5.get(r2)
            org.telegram.tgnet.TLRPC$InputDocument r5 = (org.telegram.tgnet.TLRPC.InputDocument) r5
            r5.serializeToStream(r0)
            int r2 = r2 + 1
            goto L_0x0a07
        L_0x0a1d:
            byte[] r2 = r0.toByteArray()
            java.lang.String r2 = org.telegram.messenger.Utilities.bytesToHex(r2)
            java.lang.String r5 = "masks"
            r8.put(r5, r2)
            r0.cleanup()
        L_0x0a2d:
            if (r9 == 0) goto L_0x0a32
            r8.put(r11, r9)
        L_0x0a32:
            if (r6 == 0) goto L_0x0a39
            java.lang.String r0 = "parentObject"
            r8.put(r0, r6)
        L_0x0a39:
            if (r61 == 0) goto L_0x0a44
            int r0 = r56.size()     // Catch:{ Exception -> 0x0a5a }
            if (r0 != r1) goto L_0x0a42
            goto L_0x0a44
        L_0x0a42:
            r11 = 0
            goto L_0x0a5f
        L_0x0a44:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r7.sizes     // Catch:{ Exception -> 0x0a5a }
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()     // Catch:{ Exception -> 0x0a5a }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2)     // Catch:{ Exception -> 0x0a5a }
            if (r0 == 0) goto L_0x0a42
            r11 = 0
            java.lang.String r0 = getKeyForPhotoSize(r0, r3, r11, r11)     // Catch:{ Exception -> 0x0a58 }
            r4[r11] = r0     // Catch:{ Exception -> 0x0a58 }
            goto L_0x0a5f
        L_0x0a58:
            r0 = move-exception
            goto L_0x0a5c
        L_0x0a5a:
            r0 = move-exception
            r11 = 0
        L_0x0a5c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0a5f:
            if (r61 == 0) goto L_0x0a90
            int r0 = r16 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r10)
            r9 = r52
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            java.lang.String r5 = "groupId"
            r8.put(r5, r2)
            r2 = 10
            if (r0 == r2) goto L_0x0a84
            int r2 = r31 + -1
            r5 = r54
            if (r5 != r2) goto L_0x0a96
            goto L_0x0a86
        L_0x0a84:
            r5 = r54
        L_0x0a86:
            java.lang.String r2 = "final"
            java.lang.String r1 = "1"
            r8.put(r2, r1)
            r23 = r29
            goto L_0x0a96
        L_0x0a90:
            r9 = r52
            r5 = r54
            r0 = r16
        L_0x0a96:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$UdYxeFJJPkyzGq2o6oz82-zZGls r1 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$UdYxeFJJPkyzGq2o6oz82-zZGls
            r2 = r1
            r21 = r5
            r5 = r62
            r17 = r6
            r6 = r59
            r33 = r9
            r9 = r17
            r17 = 0
            r10 = r57
            r25 = r12
            r12 = r63
            r55 = r13
            r13 = r15
            r35 = r14
            r14 = r64
            r15 = r65
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            r2 = r23
            r4 = r42
            r5 = r55
            goto L_0x0b08
        L_0x0ac3:
            r25 = r12
            r35 = r14
            r33 = r52
            r21 = r54
            r17 = 0
            r14 = r13
            if (r14 != 0) goto L_0x0ae9
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r27 = new java.util.ArrayList
            r27.<init>()
            java.util.ArrayList r28 = new java.util.ArrayList
            r28.<init>()
            r0 = r27
            r1 = r28
            goto L_0x0af0
        L_0x0ae9:
            r5 = r14
            r0 = r27
            r1 = r28
            r4 = r42
        L_0x0af0:
            r5.add(r8)
            r4.add(r9)
            java.lang.String r2 = r15.caption
            r0.add(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r15.entities
            r1.add(r2)
            r27 = r0
            r28 = r1
            r0 = r16
            r2 = r23
        L_0x0b08:
            int r9 = r21 + 1
            r1 = r56
            r13 = r59
            r11 = r25
            r10 = r26
            r12 = r31
            r15 = r32
            r23 = r33
            r14 = r35
            r26 = r37
            r8 = r38
            goto L_0x017e
        L_0x0b20:
            r42 = r4
            r14 = r5
            r17 = 0
            r29 = 0
            int r0 = (r2 > r29 ? 1 : (r2 == r29 ? 0 : -1))
            if (r0 == 0) goto L_0x0b38
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$pKfT1w1I30bZ85vW1f-pzxx_nIk r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$pKfT1w1I30bZ85vW1f-pzxx_nIk
            r15 = r59
            r13 = r65
            r0.<init>(r2, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0b3c
        L_0x0b38:
            r15 = r59
            r13 = r65
        L_0x0b3c:
            if (r66 == 0) goto L_0x0b41
            r66.releasePermission()
        L_0x0b41:
            if (r14 == 0) goto L_0x0b96
            boolean r0 = r14.isEmpty()
            if (r0 != 0) goto L_0x0b96
            r0 = 0
        L_0x0b4a:
            int r1 = r14.size()
            if (r0 >= r1) goto L_0x0b96
            java.lang.Object r1 = r14.get(r0)
            r2 = r1
            java.lang.String r2 = (java.lang.String) r2
            r12 = r42
            java.lang.Object r1 = r12.get(r0)
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3
            r4 = 0
            r11 = r27
            java.lang.Object r1 = r11.get(r0)
            r9 = r1
            java.lang.CharSequence r9 = (java.lang.CharSequence) r9
            r10 = r28
            java.lang.Object r1 = r10.get(r0)
            r16 = r1
            java.util.ArrayList r16 = (java.util.ArrayList) r16
            r1 = r59
            r5 = r22
            r6 = r57
            r8 = r63
            r10 = r16
            r11 = r62
            r16 = r12
            r12 = r60
            r13 = r64
            r17 = r14
            r14 = r65
            prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14)
            int r0 = r0 + 1
            r13 = r65
            r42 = r16
            r14 = r17
            goto L_0x0b4a
        L_0x0b96:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0bb4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "total send time = "
            r0.append(r1)
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r18
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0bb4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$72(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, boolean, boolean, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    static /* synthetic */ void lambda$null$66(MediaSendPrepareWorker mediaSendPrepareWorker, AccountInstance accountInstance, SendingMediaInfo sendingMediaInfo, boolean z) {
        mediaSendPrepareWorker.photo = accountInstance.getSendMessagesHelper().generatePhotoSizes(sendingMediaInfo.path, sendingMediaInfo.uri);
        if (z && sendingMediaInfo.canDeleteAfter) {
            new File(sendingMediaInfo.path).delete();
        }
        mediaSendPrepareWorker.sync.countDown();
    }

    static /* synthetic */ void lambda$null$67(MessageObject messageObject, AccountInstance accountInstance, TLRPC.TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC.TL_photo) null, (VideoEditedInfo) null, tL_document, str, hashMap, false, str2);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, (VideoEditedInfo) null, str, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC.ReplyMarkup) null, hashMap, z, i, 0, str2);
    }

    static /* synthetic */ void lambda$null$68(MessageObject messageObject, AccountInstance accountInstance, TLRPC.TL_photo tL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2, boolean z2, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        String str2 = null;
        if (messageObject != null) {
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            if (z) {
                str2 = sendingMediaInfo2.searchImage.imageUrl;
            }
            sendMessagesHelper.editMessageMedia(messageObject, tL_photo, (VideoEditedInfo) null, (TLRPC.TL_document) null, str2, hashMap, false, str);
            return;
        }
        SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
        if (z) {
            str2 = sendingMediaInfo2.searchImage.imageUrl;
        }
        sendMessagesHelper2.sendMessage(tL_photo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC.ReplyMarkup) null, hashMap, z2, i, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$69(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC.TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        Bitmap bitmap2 = bitmap;
        String str4 = str;
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmap2 == null || str4 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str4);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC.TL_photo) null, videoEditedInfo, tL_document, str2, hashMap, false, str3);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, videoEditedInfo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC.ReplyMarkup) null, hashMap, z, i, sendingMediaInfo2.ttl, str3);
    }

    static /* synthetic */ void lambda$null$70(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TLRPC.TL_photo tL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, tL_photo, (VideoEditedInfo) null, (TLRPC.TL_document) null, (String) null, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_photo, (String) null, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC.ReplyMarkup) null, hashMap, z, i, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$71(AccountInstance accountInstance, long j, int i) {
        SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
        HashMap<String, ArrayList<DelayedMessage>> hashMap = sendMessagesHelper.delayedMessages;
        ArrayList arrayList = hashMap.get("group_" + j);
        if (arrayList != null && !arrayList.isEmpty()) {
            DelayedMessage delayedMessage = (DelayedMessage) arrayList.get(0);
            ArrayList<MessageObject> arrayList2 = delayedMessage.messageObjects;
            MessageObject messageObject = arrayList2.get(arrayList2.size() - 1);
            delayedMessage.finalGroupMessage = messageObject.getId();
            messageObject.messageOwner.params.put("final", "1");
            TLRPC.TL_messages_messages tL_messages_messages = new TLRPC.TL_messages_messages();
            tL_messages_messages.messages.add(messageObject.messageOwner);
            accountInstance.getMessagesStorage().putMessages((TLRPC.messages_Messages) tL_messages_messages, delayedMessage.peer, -2, 0, false, i != 0);
            sendMessagesHelper.sendReadyToSendGroup(delayedMessage, true, true);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x0080 A[SYNTHETIC, Splitter:B:39:0x0080] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x008b A[SYNTHETIC, Splitter:B:45:0x008b] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00c1 A[SYNTHETIC, Splitter:B:52:0x00c1] */
    /* JADX WARNING: Removed duplicated region for block: B:58:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void fillVideoAttribute(java.lang.String r5, org.telegram.tgnet.TLRPC.TL_documentAttributeVideo r6, org.telegram.messenger.VideoEditedInfo r7) {
        /*
            r0 = 1148846080(0x447a0000, float:1000.0)
            r1 = 0
            android.media.MediaMetadataRetriever r2 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x007a }
            r2.<init>()     // Catch:{ Exception -> 0x007a }
            r2.setDataSource(r5)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            r1 = 18
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            if (r1 == 0) goto L_0x0019
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            r6.w = r1     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
        L_0x0019:
            r1 = 19
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            if (r1 == 0) goto L_0x0027
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            r6.h = r1     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
        L_0x0027:
            r1 = 9
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            if (r1 == 0) goto L_0x003d
            long r3 = java.lang.Long.parseLong(r1)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            float r1 = (float) r3     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            float r1 = r1 / r0
            double r3 = (double) r1     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            double r3 = java.lang.Math.ceil(r3)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            int r1 = (int) r3     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            r6.duration = r1     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
        L_0x003d:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            r3 = 17
            if (r1 < r3) goto L_0x0068
            r1 = 24
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            if (r1 == 0) goto L_0x0068
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            int r1 = r1.intValue()     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            if (r7 == 0) goto L_0x0058
            r7.rotationValue = r1     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            goto L_0x0068
        L_0x0058:
            r7 = 90
            if (r1 == r7) goto L_0x0060
            r7 = 270(0x10e, float:3.78E-43)
            if (r1 != r7) goto L_0x0068
        L_0x0060:
            int r7 = r6.w     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            int r1 = r6.h     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            r6.w = r1     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
            r6.h = r7     // Catch:{ Exception -> 0x0074, all -> 0x0072 }
        L_0x0068:
            r7 = 1
            r2.release()     // Catch:{ Exception -> 0x006d }
            goto L_0x0089
        L_0x006d:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            goto L_0x0089
        L_0x0072:
            r5 = move-exception
            goto L_0x00bf
        L_0x0074:
            r7 = move-exception
            r1 = r2
            goto L_0x007b
        L_0x0077:
            r5 = move-exception
            r2 = r1
            goto L_0x00bf
        L_0x007a:
            r7 = move-exception
        L_0x007b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ all -> 0x0077 }
            if (r1 == 0) goto L_0x0088
            r1.release()     // Catch:{ Exception -> 0x0084 }
            goto L_0x0088
        L_0x0084:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x0088:
            r7 = 0
        L_0x0089:
            if (r7 != 0) goto L_0x00be
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00ba }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x00ba }
            r1.<init>(r5)     // Catch:{ Exception -> 0x00ba }
            android.net.Uri r5 = android.net.Uri.fromFile(r1)     // Catch:{ Exception -> 0x00ba }
            android.media.MediaPlayer r5 = android.media.MediaPlayer.create(r7, r5)     // Catch:{ Exception -> 0x00ba }
            if (r5 == 0) goto L_0x00be
            int r7 = r5.getDuration()     // Catch:{ Exception -> 0x00ba }
            float r7 = (float) r7     // Catch:{ Exception -> 0x00ba }
            float r7 = r7 / r0
            double r0 = (double) r7     // Catch:{ Exception -> 0x00ba }
            double r0 = java.lang.Math.ceil(r0)     // Catch:{ Exception -> 0x00ba }
            int r7 = (int) r0     // Catch:{ Exception -> 0x00ba }
            r6.duration = r7     // Catch:{ Exception -> 0x00ba }
            int r7 = r5.getVideoWidth()     // Catch:{ Exception -> 0x00ba }
            r6.w = r7     // Catch:{ Exception -> 0x00ba }
            int r7 = r5.getVideoHeight()     // Catch:{ Exception -> 0x00ba }
            r6.h = r7     // Catch:{ Exception -> 0x00ba }
            r5.release()     // Catch:{ Exception -> 0x00ba }
            goto L_0x00be
        L_0x00ba:
            r5 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x00be:
            return
        L_0x00bf:
            if (r2 == 0) goto L_0x00c9
            r2.release()     // Catch:{ Exception -> 0x00c5 }
            goto L_0x00c9
        L_0x00c5:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x00c9:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.fillVideoAttribute(java.lang.String, org.telegram.tgnet.TLRPC$TL_documentAttributeVideo, org.telegram.messenger.VideoEditedInfo):void");
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x0015 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.graphics.Bitmap createVideoThumbnail(java.lang.String r2, long r3) {
        /*
            android.media.MediaMetadataRetriever r0 = new android.media.MediaMetadataRetriever
            r0.<init>()
            r1 = 0
            r0.setDataSource(r2)     // Catch:{ Exception -> 0x0015, all -> 0x0019 }
            r2 = 1
            android.graphics.Bitmap r1 = r0.getFrameAtTime(r3, r2)     // Catch:{ Exception -> 0x0015, all -> 0x0019 }
            if (r1 != 0) goto L_0x0015
            r2 = 3
            android.graphics.Bitmap r1 = r0.getFrameAtTime(r3, r2)     // Catch:{ Exception -> 0x0015, all -> 0x0019 }
        L_0x0015:
            r0.release()     // Catch:{ RuntimeException -> 0x001e }
            goto L_0x001e
        L_0x0019:
            r2 = move-exception
            r0.release()     // Catch:{ RuntimeException -> 0x001d }
        L_0x001d:
            throw r2
        L_0x001e:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.createVideoThumbnail(java.lang.String, long):android.graphics.Bitmap");
    }

    private static VideoEditedInfo createCompressionSettings(String str) {
        int i;
        int i2;
        int[] iArr = new int[11];
        AnimatedFileDrawable.getVideoInfo(str, iArr);
        if (iArr[0] == 0) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("video hasn't avc1 atom");
            }
            return null;
        }
        int videoBitrate = MediaController.getVideoBitrate(str);
        int i3 = 4;
        float f = (float) iArr[4];
        long j = (long) iArr[6];
        long j2 = (long) iArr[5];
        int i4 = iArr[7];
        if (Build.VERSION.SDK_INT < 18) {
            try {
                MediaCodecInfo selectCodec = MediaController.selectCodec("video/avc");
                if (selectCodec == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("no codec info for video/avc");
                    }
                    return null;
                }
                String name = selectCodec.getName();
                if (!name.equals("OMX.google.h264.encoder") && !name.equals("OMX.ST.VFM.H264Enc") && !name.equals("OMX.Exynos.avc.enc") && !name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") && !name.equals("OMX.MARVELL.VIDEO.H264ENCODER") && !name.equals("OMX.k3.video.encoder.avc")) {
                    if (!name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                        if (MediaController.selectColorFormat(selectCodec, "video/avc") == 0) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("no color format for video/avc");
                            }
                            return null;
                        }
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("unsupported encoder = " + name);
                }
                return null;
            } catch (Exception unused) {
            }
        }
        VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
        videoEditedInfo.startTime = -1;
        videoEditedInfo.endTime = -1;
        videoEditedInfo.bitrate = videoBitrate;
        videoEditedInfo.originalPath = str;
        videoEditedInfo.framerate = i4;
        videoEditedInfo.estimatedDuration = (long) Math.ceil((double) f);
        int i5 = iArr[1];
        videoEditedInfo.originalWidth = i5;
        videoEditedInfo.resultWidth = i5;
        int i6 = iArr[2];
        videoEditedInfo.originalHeight = i6;
        videoEditedInfo.resultHeight = i6;
        videoEditedInfo.rotationValue = iArr[8];
        videoEditedInfo.originalDuration = (long) (f * 1000.0f);
        int i7 = MessagesController.getGlobalMainSettings().getInt("compress_video2", 1);
        int i8 = videoEditedInfo.originalWidth;
        if (i8 > 1280 || (i2 = videoEditedInfo.originalHeight) > 1280) {
            i3 = 5;
        } else if (i8 <= 848 && i2 <= 848) {
            i3 = (i8 > 640 || i2 > 640) ? 3 : (i8 > 480 || i2 > 480) ? 2 : 1;
        }
        if (i7 >= i3) {
            i7 = i3 - 1;
        }
        int i9 = i3 - 1;
        if (i7 != i9) {
            float f2 = i7 != 0 ? i7 != 1 ? i7 != 2 ? 1280.0f : 848.0f : 640.0f : 432.0f;
            int i10 = videoEditedInfo.originalWidth;
            int i11 = videoEditedInfo.originalHeight;
            float f3 = f2 / (i10 > i11 ? (float) i10 : (float) i11);
            videoEditedInfo.resultWidth = Math.round((((float) videoEditedInfo.originalWidth) * f3) / 2.0f) * 2;
            videoEditedInfo.resultHeight = Math.round((((float) videoEditedInfo.originalHeight) * f3) / 2.0f) * 2;
            i = MediaController.makeVideoBitrate(videoEditedInfo.originalHeight, videoEditedInfo.originalWidth, videoBitrate, videoEditedInfo.resultHeight, videoEditedInfo.resultWidth);
        } else {
            i = videoBitrate;
        }
        if (i7 == i9) {
            videoEditedInfo.resultWidth = videoEditedInfo.originalWidth;
            videoEditedInfo.resultHeight = videoEditedInfo.originalHeight;
            videoEditedInfo.bitrate = videoBitrate;
            videoEditedInfo.estimatedSize = (long) ((int) new File(str).length());
        } else {
            videoEditedInfo.bitrate = i;
            videoEditedInfo.estimatedSize = (long) ((int) (j2 + j));
            long j3 = videoEditedInfo.estimatedSize;
            videoEditedInfo.estimatedSize = j3 + ((j3 / 32768) * 16);
        }
        if (videoEditedInfo.estimatedSize == 0) {
            videoEditedInfo.estimatedSize = 1;
        }
        return videoEditedInfo;
    }

    public static void prepareSendingVideo(AccountInstance accountInstance, String str, long j, long j2, int i, int i2, VideoEditedInfo videoEditedInfo, long j3, MessageObject messageObject, CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, int i3, MessageObject messageObject2, boolean z, int i4) {
        if (str != null && str.length() != 0) {
            $$Lambda$SendMessagesHelper$VHKs7qA0gndu87lOdAqsXp0B48 r19 = r0;
            $$Lambda$SendMessagesHelper$VHKs7qA0gndu87lOdAqsXp0B48 r0 = new Runnable(videoEditedInfo, str, j3, j2, i3, accountInstance, i2, i, j, charSequence, messageObject2, messageObject, arrayList, z, i4) {
                private final /* synthetic */ VideoEditedInfo f$0;
                private final /* synthetic */ String f$1;
                private final /* synthetic */ MessageObject f$10;
                private final /* synthetic */ MessageObject f$11;
                private final /* synthetic */ ArrayList f$12;
                private final /* synthetic */ boolean f$13;
                private final /* synthetic */ int f$14;
                private final /* synthetic */ long f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ AccountInstance f$5;
                private final /* synthetic */ int f$6;
                private final /* synthetic */ int f$7;
                private final /* synthetic */ long f$8;
                private final /* synthetic */ CharSequence f$9;

                {
                    this.f$0 = r4;
                    this.f$1 = r5;
                    this.f$2 = r6;
                    this.f$3 = r8;
                    this.f$4 = r10;
                    this.f$5 = r11;
                    this.f$6 = r12;
                    this.f$7 = r13;
                    this.f$8 = r14;
                    this.f$9 = r16;
                    this.f$10 = r17;
                    this.f$11 = r18;
                    this.f$12 = r19;
                    this.f$13 = r20;
                    this.f$14 = r21;
                }

                public final void run() {
                    VideoEditedInfo videoEditedInfo = this.f$0;
                    VideoEditedInfo videoEditedInfo2 = videoEditedInfo;
                    SendMessagesHelper.lambda$prepareSendingVideo$74(videoEditedInfo2, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14);
                }
            };
            new Thread(r19).start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:103:0x02c8  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0307  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x031d  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0328  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0355  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x0365  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x036e  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0269  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0287  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x02b1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingVideo$74(org.telegram.messenger.VideoEditedInfo r32, java.lang.String r33, long r34, long r36, int r38, org.telegram.messenger.AccountInstance r39, int r40, int r41, long r42, java.lang.CharSequence r44, org.telegram.messenger.MessageObject r45, org.telegram.messenger.MessageObject r46, java.util.ArrayList r47, boolean r48, int r49) {
        /*
            r6 = r33
            r10 = r34
            r7 = r36
            r9 = r40
            r12 = r41
            if (r32 == 0) goto L_0x000f
            r13 = r32
            goto L_0x0014
        L_0x000f:
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r33)
            r13 = r0
        L_0x0014:
            int r0 = (int) r10
            if (r0 != 0) goto L_0x0019
            r4 = 1
            goto L_0x001a
        L_0x0019:
            r4 = 0
        L_0x001a:
            if (r13 == 0) goto L_0x0022
            boolean r0 = r13.roundVideo
            if (r0 == 0) goto L_0x0022
            r5 = 1
            goto L_0x0023
        L_0x0022:
            r5 = 0
        L_0x0023:
            if (r13 != 0) goto L_0x004d
            java.lang.String r0 = "mp4"
            boolean r0 = r6.endsWith(r0)
            if (r0 != 0) goto L_0x004d
            if (r5 == 0) goto L_0x0030
            goto L_0x004d
        L_0x0030:
            r3 = 0
            r4 = 0
            r12 = 0
            r0 = r39
            r1 = r33
            r2 = r33
            r5 = r34
            r7 = r46
            r8 = r44
            r9 = r47
            r10 = r45
            r11 = r12
            r12 = r48
            r13 = r49
            prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x0396
        L_0x004d:
            java.io.File r3 = new java.io.File
            r3.<init>(r6)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r6)
            long r1 = r3.length()
            r0.append(r1)
            java.lang.String r2 = "_"
            r0.append(r2)
            long r14 = r3.lastModified()
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            java.lang.String r14 = ""
            r17 = 0
            if (r13 == 0) goto L_0x00d2
            if (r5 != 0) goto L_0x00c1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r7)
            r1.append(r2)
            r19 = r14
            long r14 = r13.startTime
            r1.append(r14)
            r1.append(r2)
            long r14 = r13.endTime
            r1.append(r14)
            boolean r0 = r13.muted
            if (r0 == 0) goto L_0x009d
            java.lang.String r14 = "_m"
            goto L_0x009f
        L_0x009d:
            r14 = r19
        L_0x009f:
            r1.append(r14)
            java.lang.String r0 = r1.toString()
            int r1 = r13.resultWidth
            int r14 = r13.originalWidth
            if (r1 == r14) goto L_0x00c3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r2)
            int r0 = r13.resultWidth
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x00c3
        L_0x00c1:
            r19 = r14
        L_0x00c3:
            long r14 = r13.startTime
            int r1 = (r14 > r17 ? 1 : (r14 == r17 ? 0 : -1))
            if (r1 < 0) goto L_0x00ca
            goto L_0x00cc
        L_0x00ca:
            r14 = r17
        L_0x00cc:
            r30 = r14
            r14 = r0
            r0 = r30
            goto L_0x00d7
        L_0x00d2:
            r19 = r14
            r14 = r0
            r0 = r17
        L_0x00d7:
            if (r4 != 0) goto L_0x0120
            if (r38 != 0) goto L_0x0120
            org.telegram.messenger.MessagesStorage r15 = r39.getMessagesStorage()
            if (r4 != 0) goto L_0x00e5
            r21 = r0
            r0 = 2
            goto L_0x00ea
        L_0x00e5:
            r20 = 5
            r21 = r0
            r0 = 5
        L_0x00ea:
            java.lang.Object[] r0 = r15.getSentFile(r14, r0)
            if (r0 == 0) goto L_0x0117
            r1 = 0
            r15 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_document r15 = (org.telegram.tgnet.TLRPC.TL_document) r15
            r1 = 1
            r0 = r0[r1]
            r20 = r0
            java.lang.String r20 = (java.lang.String) r20
            r23 = 0
            r24 = r21
            r0 = r4
            r1 = r15
            r21 = r15
            r15 = r2
            r2 = r33
            r22 = r3
            r3 = r23
            r9 = r5
            r23 = r14
            r14 = r4
            r4 = r24
            ensureMediaThumbExists(r0, r1, r2, r3, r4)
            r5 = r20
            goto L_0x012c
        L_0x0117:
            r15 = r2
            r9 = r5
            r23 = r14
            r24 = r21
            r22 = r3
            goto L_0x0128
        L_0x0120:
            r24 = r0
            r15 = r2
            r22 = r3
            r9 = r5
            r23 = r14
        L_0x0128:
            r14 = r4
            r5 = 0
            r21 = 0
        L_0x012c:
            if (r21 != 0) goto L_0x031d
            r0 = r24
            android.graphics.Bitmap r0 = createVideoThumbnail(r6, r0)
            if (r0 != 0) goto L_0x013b
            r1 = 1
            android.graphics.Bitmap r0 = android.media.ThumbnailUtils.createVideoThumbnail(r6, r1)
        L_0x013b:
            r1 = 90
            if (r14 != 0) goto L_0x0145
            if (r38 == 0) goto L_0x0142
            goto L_0x0145
        L_0x0142:
            r2 = 320(0x140, float:4.48E-43)
            goto L_0x0147
        L_0x0145:
            r2 = 90
        L_0x0147:
            float r3 = (float) r2
            if (r2 <= r1) goto L_0x014d
            r2 = 80
            goto L_0x014f
        L_0x014d:
            r2 = 55
        L_0x014f:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r14)
            if (r0 == 0) goto L_0x0260
            if (r2 == 0) goto L_0x0260
            if (r9 == 0) goto L_0x025d
            r3 = 21
            if (r14 == 0) goto L_0x01fd
            r4 = 1
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r1, r1, r4)
            r25 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x016b
            r26 = 0
            goto L_0x016d
        L_0x016b:
            r26 = 1
        L_0x016d:
            int r27 = r0.getWidth()
            int r28 = r0.getHeight()
            int r29 = r0.getRowBytes()
            r24 = r0
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            r25 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x0187
            r26 = 0
            goto L_0x0189
        L_0x0187:
            r26 = 1
        L_0x0189:
            int r27 = r0.getWidth()
            int r28 = r0.getHeight()
            int r29 = r0.getRowBytes()
            r24 = r0
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            r25 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x01a3
            r26 = 0
            goto L_0x01a5
        L_0x01a3:
            r26 = 1
        L_0x01a5:
            int r27 = r0.getWidth()
            int r28 = r0.getHeight()
            int r29 = r0.getRowBytes()
            r24 = r0
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r4 = r2.location
            r21 = r2
            long r1 = r4.volume_id
            r3.append(r1)
            r3.append(r15)
            r1 = r21
            org.telegram.tgnet.TLRPC$FileLocation r2 = r1.location
            int r2 = r2.local_id
            r3.append(r2)
            java.lang.String r2 = "@%d_%d_b2"
            r3.append(r2)
            java.lang.String r2 = r3.toString()
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r4 = (float) r4
            float r15 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r15
            int r4 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r15 = 0
            r3[r15] = r4
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r4 = (float) r4
            float r15 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r15
            int r4 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r15 = 1
            r3[r15] = r4
            java.lang.String r15 = java.lang.String.format(r2, r3)
            goto L_0x0262
        L_0x01fd:
            r1 = r2
            r25 = 3
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 >= r3) goto L_0x0207
            r26 = 0
            goto L_0x0209
        L_0x0207:
            r26 = 1
        L_0x0209:
            int r27 = r0.getWidth()
            int r28 = r0.getHeight()
            int r29 = r0.getRowBytes()
            r24 = r0
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r3 = r1.location
            long r3 = r3.volume_id
            r2.append(r3)
            r2.append(r15)
            org.telegram.tgnet.TLRPC$FileLocation r3 = r1.location
            int r3 = r3.local_id
            r2.append(r3)
            java.lang.String r3 = "@%d_%d_b"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r4 = (float) r4
            float r15 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r15
            int r4 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r15 = 0
            r3[r15] = r4
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r4 = (float) r4
            float r15 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r15
            int r4 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r15 = 1
            r3[r15] = r4
            java.lang.String r15 = java.lang.String.format(r2, r3)
            goto L_0x0262
        L_0x025d:
            r1 = r2
            r0 = 0
            goto L_0x0261
        L_0x0260:
            r1 = r2
        L_0x0261:
            r15 = 0
        L_0x0262:
            org.telegram.tgnet.TLRPC$TL_document r2 = new org.telegram.tgnet.TLRPC$TL_document
            r2.<init>()
            if (r1 == 0) goto L_0x0274
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r2.thumbs
            r3.add(r1)
            int r1 = r2.flags
            r3 = 1
            r1 = r1 | r3
            r2.flags = r1
        L_0x0274:
            r1 = 0
            byte[] r3 = new byte[r1]
            r2.file_reference = r3
            java.lang.String r3 = "video/mp4"
            r2.mime_type = r3
            org.telegram.messenger.UserConfig r3 = r39.getUserConfig()
            r3.saveConfig(r1)
            if (r14 == 0) goto L_0x02b1
            r1 = 32
            long r3 = r10 >> r1
            int r1 = (int) r3
            org.telegram.messenger.MessagesController r3 = r39.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r3.getEncryptedChat(r1)
            if (r1 != 0) goto L_0x029b
            return
        L_0x029b:
            int r1 = r1.layer
            int r1 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r1)
            r3 = 66
            if (r1 < r3) goto L_0x02ab
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            goto L_0x02b9
        L_0x02ab:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r1.<init>()
            goto L_0x02b9
        L_0x02b1:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            r3 = 1
            r1.supports_streaming = r3
        L_0x02b9:
            r1.round_message = r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r2.attributes
            r3.add(r1)
            if (r13 == 0) goto L_0x0307
            boolean r3 = r13.needConvert()
            if (r3 == 0) goto L_0x0307
            boolean r3 = r13.muted
            if (r3 == 0) goto L_0x02e2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r2.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r4.<init>()
            r3.add(r4)
            fillVideoAttribute(r6, r1, r13)
            int r3 = r1.w
            r13.originalWidth = r3
            int r3 = r1.h
            r13.originalHeight = r3
            goto L_0x02e9
        L_0x02e2:
            r3 = 1000(0x3e8, double:4.94E-321)
            long r3 = r7 / r3
            int r4 = (int) r3
            r1.duration = r4
        L_0x02e9:
            int r3 = r13.rotationValue
            r4 = 90
            if (r3 == r4) goto L_0x02fb
            r4 = 270(0x10e, float:3.78E-43)
            if (r3 != r4) goto L_0x02f4
            goto L_0x02fb
        L_0x02f4:
            r1.w = r12
            r3 = r40
            r1.h = r3
            goto L_0x0301
        L_0x02fb:
            r3 = r40
            r1.w = r3
            r1.h = r12
        L_0x0301:
            r3 = r42
            int r1 = (int) r3
            r2.size = r1
            goto L_0x0318
        L_0x0307:
            boolean r3 = r22.exists()
            if (r3 == 0) goto L_0x0314
            long r3 = r22.length()
            int r4 = (int) r3
            r2.size = r4
        L_0x0314:
            r3 = 0
            fillVideoAttribute(r6, r1, r3)
        L_0x0318:
            r1 = r0
            r21 = r2
            r2 = r15
            goto L_0x0320
        L_0x031d:
            r3 = 0
            r1 = r3
            r2 = r1
        L_0x0320:
            if (r13 == 0) goto L_0x0355
            boolean r0 = r13.needConvert()
            if (r0 == 0) goto L_0x0355
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "-2147483648_"
            r0.append(r3)
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r0.append(r3)
            java.lang.String r3 = ".mp4"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.io.File r3 = new java.io.File
            r4 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)
            r3.<init>(r4, r0)
            org.telegram.messenger.SharedConfig.saveConfig()
            java.lang.String r0 = r3.getAbsolutePath()
            r7 = r0
            goto L_0x0356
        L_0x0355:
            r7 = r6
        L_0x0356:
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            if (r44 == 0) goto L_0x0363
            java.lang.String r0 = r44.toString()
            r19 = r0
        L_0x0363:
            if (r23 == 0) goto L_0x036c
            java.lang.String r0 = "originalPath"
            r3 = r23
            r8.put(r0, r3)
        L_0x036c:
            if (r5 == 0) goto L_0x0373
            java.lang.String r0 = "parentObject"
            r8.put(r0, r5)
        L_0x0373:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$sEG1r9eCF9TKK2nk0jbM2n33M88 r18 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$sEG1r9eCF9TKK2nk0jbM2n33M88
            r0 = r18
            r3 = r45
            r4 = r39
            r20 = r5
            r5 = r13
            r6 = r21
            r9 = r20
            r10 = r34
            r12 = r46
            r13 = r19
            r14 = r47
            r15 = r48
            r16 = r49
            r17 = r38
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16, r17)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r18)
        L_0x0396:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$74(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, long, int, org.telegram.messenger.AccountInstance, int, int, long, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$73(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC.TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, String str4, ArrayList arrayList, boolean z, int i, int i2) {
        Bitmap bitmap2 = bitmap;
        String str5 = str;
        if (!(bitmap2 == null || str5 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str5);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC.TL_photo) null, videoEditedInfo, tL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tL_document, videoEditedInfo, str2, j, messageObject2, str4, arrayList, (TLRPC.ReplyMarkup) null, hashMap, z, i, i2, str3);
        }
    }
}
