package org.telegram.messenger;

import android.content.SharedPreferences;
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
import org.telegram.tgnet.TLRPC$BotInlineMessage;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$DecryptedMessageAction;
import org.telegram.tgnet.TLRPC$DecryptedMessageMedia;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputDocument;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$InputMedia;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaContact;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageText;
import org.telegram.tgnet.TLRPC$TL_decryptedMessage;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionAbortKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionAcceptKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionCommitKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionDeleteMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionFlushHistory;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionNoop;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionNotifyLayer;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionReadMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionRequestKey;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionResend;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionTyping;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_document_layer82;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_fileLocation_layer82;
import org.telegram.tgnet.TLRPC$TL_game;
import org.telegram.tgnet.TLRPC$TL_geoPoint;
import org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;
import org.telegram.tgnet.TLRPC$TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC$TL_inputMediaGame;
import org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;
import org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
import org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC$TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC$TL_messageEntityBold;
import org.telegram.tgnet.TLRPC$TL_messageEntityCode;
import org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC$TL_messageEntityPre;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaGame;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendReaction;
import org.telegram.tgnet.TLRPC$TL_messages_sendScreenshotNotification;
import org.telegram.tgnet.TLRPC$TL_messages_sendVote;
import org.telegram.tgnet.TLRPC$TL_messages_uploadMedia;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoCachedSize;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_restrictionReason;
import org.telegram.tgnet.TLRPC$TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateEditMessage;
import org.telegram.tgnet.TLRPC$TL_updateMessageID;
import org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateNewMessage;
import org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage;
import org.telegram.tgnet.TLRPC$TL_updateShortSentMessage;
import org.telegram.tgnet.TLRPC$TL_user;
import org.telegram.tgnet.TLRPC$TL_userContact_old2;
import org.telegram.tgnet.TLRPC$TL_webPagePending;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.TLRPC$messages_Messages;
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
    private SparseArray<TLRPC$Message> editingMessages = new SparseArray<>();
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
    private SparseArray<TLRPC$Message> sendingMessages = new SparseArray<>();
    private LongSparseArray<Integer> sendingMessagesIdDialogs = new LongSparseArray<>();
    private SparseArray<MessageObject> unsentMessages = new SparseArray<>();
    private SparseArray<TLRPC$Message> uploadMessages = new SparseArray<>();
    private LongSparseArray<Integer> uploadingMessagesIdDialogs = new LongSparseArray<>();
    private LongSparseArray<Long> voteSendTime = new LongSparseArray<>();
    private HashMap<String, Boolean> waitingForCallback = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<String, MessageObject> waitingForLocation = new HashMap<>();
    private HashMap<String, byte[]> waitingForVote = new HashMap<>();

    public static class SendingMediaInfo {
        public boolean canDeleteAfter;
        public String caption;
        public ArrayList<TLRPC$MessageEntity> entities;
        public TLRPC$BotInlineResult inlineResult;
        public boolean isVideo;
        public ArrayList<TLRPC$InputDocument> masks;
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
        public volatile TLRPC$TL_photo photo;
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
                Location lastKnownLocation2 = this.locationManager.getLastKnownLocation("gps");
                this.lastKnownLocation = lastKnownLocation2;
                if (lastKnownLocation2 == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
            }
            Runnable runnable = this.locationQueryCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            $$Lambda$SendMessagesHelper$LocationProvider$Kc6YgD2bSOGi90WwdXYCZI1bg r0 = new Runnable() {
                public final void run() {
                    SendMessagesHelper.LocationProvider.this.lambda$start$0$SendMessagesHelper$LocationProvider();
                }
            };
            this.locationQueryCancelRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 5000);
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
        public TLRPC$EncryptedChat encryptedChat;
        public HashMap<Object, Object> extraHashMap;
        public int finalGroupMessage;
        public long groupId;
        public String httpLocation;
        public ArrayList<String> httpLocations;
        public ArrayList<TLRPC$InputMedia> inputMedias;
        public TLRPC$InputMedia inputUploadMedia;
        public TLObject locationParent;
        public ArrayList<TLRPC$PhotoSize> locations;
        public ArrayList<MessageObject> messageObjects;
        public ArrayList<TLRPC$Message> messages;
        public MessageObject obj;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public long peer;
        public boolean performMediaUpload;
        public TLRPC$PhotoSize photoSize;
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
                        if (tLObject instanceof TLRPC$TL_messages_sendEncryptedMultiMedia) {
                            SendMessagesHelper.this.getSecretChatHelper().performSendEncryptedRequest((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessageSendAfterRequest.request, this);
                        } else if (tLObject instanceof TLRPC$TL_messages_sendMultiMedia) {
                            SendMessagesHelper.this.performSendMessageRequestMulti((TLRPC$TL_messages_sendMultiMedia) tLObject, delayedMessageSendAfterRequest.msgObjs, delayedMessageSendAfterRequest.originalPaths, delayedMessageSendAfterRequest.parentObjects, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.scheduled);
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
        TLRPC$InputMedia tLRPC$InputMedia;
        String str2;
        TLRPC$InputFile tLRPC$InputFile;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile;
        ArrayList arrayList2;
        TLObject tLObject;
        TLRPC$TL_decryptedMessage tLRPC$TL_decryptedMessage;
        ArrayList arrayList3;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile2;
        int i3;
        TLRPC$PhotoSize tLRPC$PhotoSize;
        TLRPC$PhotoSize tLRPC$PhotoSize2;
        int i4 = i;
        int i5 = 4;
        int i6 = 0;
        boolean z = true;
        if (i4 == NotificationCenter.FileDidUpload) {
            String str3 = objArr[0];
            TLRPC$InputFile tLRPC$InputFile2 = objArr[1];
            TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile3 = objArr[2];
            ArrayList arrayList4 = this.delayedMessages.get(str3);
            if (arrayList4 != null) {
                while (i6 < arrayList4.size()) {
                    DelayedMessage delayedMessage = (DelayedMessage) arrayList4.get(i6);
                    TLObject tLObject2 = delayedMessage.sendRequest;
                    if (tLObject2 instanceof TLRPC$TL_messages_sendMedia) {
                        tLRPC$InputMedia = ((TLRPC$TL_messages_sendMedia) tLObject2).media;
                    } else if (tLObject2 instanceof TLRPC$TL_messages_editMessage) {
                        tLRPC$InputMedia = ((TLRPC$TL_messages_editMessage) tLObject2).media;
                    } else {
                        tLRPC$InputMedia = tLObject2 instanceof TLRPC$TL_messages_sendMultiMedia ? (TLRPC$InputMedia) delayedMessage.extraHashMap.get(str3) : null;
                    }
                    if (tLRPC$InputFile2 == null || tLRPC$InputMedia == null) {
                        arrayList2 = arrayList4;
                        tLRPC$InputFile = tLRPC$InputFile2;
                        str2 = str3;
                        tLRPC$InputEncryptedFile = tLRPC$InputEncryptedFile3;
                        if (!(tLRPC$InputEncryptedFile == null || (tLObject = delayedMessage.sendEncryptedRequest) == null)) {
                            if (delayedMessage.type == i5) {
                                TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) tLObject;
                                TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile4 = (TLRPC$InputEncryptedFile) delayedMessage.extraHashMap.get(str2);
                                int indexOf = tLRPC$TL_messages_sendEncryptedMultiMedia.files.indexOf(tLRPC$InputEncryptedFile4);
                                if (indexOf >= 0) {
                                    tLRPC$TL_messages_sendEncryptedMultiMedia.files.set(indexOf, tLRPC$InputEncryptedFile);
                                    if (tLRPC$InputEncryptedFile4.id == 1) {
                                        MessageObject messageObject3 = (MessageObject) delayedMessage.extraHashMap.get(str2 + "_i");
                                        delayedMessage.photoSize = (TLRPC$PhotoSize) delayedMessage.extraHashMap.get(str2 + "_t");
                                        stopVideoService(delayedMessage.messageObjects.get(indexOf).messageOwner.attachPath);
                                    }
                                    tLRPC$TL_decryptedMessage = tLRPC$TL_messages_sendEncryptedMultiMedia.messages.get(indexOf);
                                } else {
                                    tLRPC$TL_decryptedMessage = null;
                                }
                            } else {
                                tLRPC$TL_decryptedMessage = (TLRPC$TL_decryptedMessage) tLObject;
                            }
                            if (tLRPC$TL_decryptedMessage != null) {
                                TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia = tLRPC$TL_decryptedMessage.media;
                                if ((tLRPC$DecryptedMessageMedia instanceof TLRPC$TL_decryptedMessageMediaVideo) || (tLRPC$DecryptedMessageMedia instanceof TLRPC$TL_decryptedMessageMediaPhoto) || (tLRPC$DecryptedMessageMedia instanceof TLRPC$TL_decryptedMessageMediaDocument)) {
                                    tLRPC$TL_decryptedMessage.media.size = (int) objArr[5].longValue();
                                }
                                TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia2 = tLRPC$TL_decryptedMessage.media;
                                tLRPC$DecryptedMessageMedia2.key = objArr[3];
                                tLRPC$DecryptedMessageMedia2.iv = objArr[4];
                                if (delayedMessage.type == 4) {
                                    uploadMultiMedia(delayedMessage, (TLRPC$InputMedia) null, tLRPC$InputEncryptedFile, str2);
                                } else {
                                    SecretChatHelper secretChatHelper = getSecretChatHelper();
                                    MessageObject messageObject4 = delayedMessage.obj;
                                    secretChatHelper.performSendEncryptedRequest(tLRPC$TL_decryptedMessage, messageObject4.messageOwner, delayedMessage.encryptedChat, tLRPC$InputEncryptedFile, delayedMessage.originalPath, messageObject4);
                                }
                            }
                            arrayList2.remove(i6);
                            i6--;
                        }
                    } else {
                        int i7 = delayedMessage.type;
                        if (i7 == 0) {
                            tLRPC$InputMedia.file = tLRPC$InputFile2;
                            arrayList3 = arrayList4;
                            tLRPC$InputEncryptedFile2 = tLRPC$InputEncryptedFile3;
                            i3 = i6;
                            tLRPC$InputFile = tLRPC$InputFile2;
                            str2 = str3;
                            performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, delayedMessage, true, (DelayedMessage) null, delayedMessage.parentObject, delayedMessage.scheduled);
                        } else {
                            DelayedMessage delayedMessage2 = delayedMessage;
                            arrayList3 = arrayList4;
                            tLRPC$InputEncryptedFile2 = tLRPC$InputEncryptedFile3;
                            i3 = i6;
                            tLRPC$InputFile = tLRPC$InputFile2;
                            str2 = str3;
                            if (i7 != z) {
                                DelayedMessage delayedMessage3 = delayedMessage2;
                                if (i7 == 2) {
                                    if (tLRPC$InputMedia.file == null) {
                                        tLRPC$InputMedia.file = tLRPC$InputFile;
                                        if (tLRPC$InputMedia.thumb != null || (tLRPC$PhotoSize = delayedMessage3.photoSize) == null || tLRPC$PhotoSize.location == null) {
                                            performSendMessageRequest(delayedMessage3.sendRequest, delayedMessage3.obj, delayedMessage3.originalPath, (DelayedMessage) null, delayedMessage3.parentObject, delayedMessage3.scheduled);
                                        } else {
                                            performSendDelayedMessage(delayedMessage3);
                                        }
                                    } else {
                                        tLRPC$InputMedia.thumb = tLRPC$InputFile;
                                        tLRPC$InputMedia.flags |= i5;
                                        performSendMessageRequest(delayedMessage3.sendRequest, delayedMessage3.obj, delayedMessage3.originalPath, (DelayedMessage) null, delayedMessage3.parentObject, delayedMessage3.scheduled);
                                    }
                                } else if (i7 == 3) {
                                    tLRPC$InputMedia.file = tLRPC$InputFile;
                                    performSendMessageRequest(delayedMessage3.sendRequest, delayedMessage3.obj, delayedMessage3.originalPath, (DelayedMessage) null, delayedMessage3.parentObject, delayedMessage3.scheduled);
                                } else if (i7 == i5) {
                                    if (!(tLRPC$InputMedia instanceof TLRPC$TL_inputMediaUploadedDocument)) {
                                        tLRPC$InputMedia.file = tLRPC$InputFile;
                                        uploadMultiMedia(delayedMessage3, tLRPC$InputMedia, (TLRPC$InputEncryptedFile) null, str2);
                                    } else if (tLRPC$InputMedia.file == null) {
                                        tLRPC$InputMedia.file = tLRPC$InputFile;
                                        int indexOf2 = delayedMessage3.messageObjects.indexOf((MessageObject) delayedMessage3.extraHashMap.get(str2 + "_i"));
                                        if (indexOf2 >= 0) {
                                            stopVideoService(delayedMessage3.messageObjects.get(indexOf2).messageOwner.attachPath);
                                        }
                                        TLRPC$PhotoSize tLRPC$PhotoSize3 = (TLRPC$PhotoSize) delayedMessage3.extraHashMap.get(str2 + "_t");
                                        delayedMessage3.photoSize = tLRPC$PhotoSize3;
                                        if (tLRPC$InputMedia.thumb != null || tLRPC$PhotoSize3 == null || tLRPC$PhotoSize3.location == null) {
                                            uploadMultiMedia(delayedMessage3, tLRPC$InputMedia, (TLRPC$InputEncryptedFile) null, str2);
                                        } else {
                                            delayedMessage3.performMediaUpload = z;
                                            performSendDelayedMessage(delayedMessage3, indexOf2);
                                        }
                                    } else {
                                        tLRPC$InputMedia.thumb = tLRPC$InputFile;
                                        tLRPC$InputMedia.flags |= i5;
                                        uploadMultiMedia(delayedMessage3, tLRPC$InputMedia, (TLRPC$InputEncryptedFile) null, (String) delayedMessage3.extraHashMap.get(str2 + "_o"));
                                    }
                                }
                            } else if (tLRPC$InputMedia.file == null) {
                                tLRPC$InputMedia.file = tLRPC$InputFile;
                                DelayedMessage delayedMessage4 = delayedMessage2;
                                if (tLRPC$InputMedia.thumb != null || (tLRPC$PhotoSize2 = delayedMessage4.photoSize) == null || tLRPC$PhotoSize2.location == null) {
                                    performSendMessageRequest(delayedMessage4.sendRequest, delayedMessage4.obj, delayedMessage4.originalPath, (DelayedMessage) null, delayedMessage4.parentObject, delayedMessage4.scheduled);
                                } else {
                                    performSendDelayedMessage(delayedMessage4);
                                }
                            } else {
                                DelayedMessage delayedMessage5 = delayedMessage2;
                                tLRPC$InputMedia.thumb = tLRPC$InputFile;
                                tLRPC$InputMedia.flags |= i5;
                                performSendMessageRequest(delayedMessage5.sendRequest, delayedMessage5.obj, delayedMessage5.originalPath, (DelayedMessage) null, delayedMessage5.parentObject, delayedMessage5.scheduled);
                            }
                        }
                        arrayList2 = arrayList3;
                        int i8 = i3;
                        arrayList2.remove(i8);
                        i6 = i8 - 1;
                        tLRPC$InputEncryptedFile = tLRPC$InputEncryptedFile2;
                    }
                    i6++;
                    arrayList4 = arrayList2;
                    tLRPC$InputEncryptedFile3 = tLRPC$InputEncryptedFile;
                    tLRPC$InputFile2 = tLRPC$InputFile;
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
                            delayedMessage7.photoSize = (TLRPC$PhotoSize) delayedMessage7.extraHashMap.get(messageObject5.messageOwner.attachPath + "_t");
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
                                        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList8, false, true, false, 0, messageObject7.scheduled);
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
                                    getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList9, false, true, false, 0, delayedMessage8.obj.scheduled);
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
            private final /* synthetic */ TLRPC$TL_photo f$1;
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

    public /* synthetic */ void lambda$null$1$SendMessagesHelper(TLRPC$TL_photo tLRPC$TL_photo, MessageObject messageObject, File file, DelayedMessage delayedMessage, String str) {
        if (tLRPC$TL_photo != null) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            tLRPC$Message.media.photo = tLRPC$TL_photo;
            tLRPC$Message.attachPath = file.toString();
            ArrayList arrayList = new ArrayList();
            arrayList.add(messageObject.messageOwner);
            getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, false, true, false, 0, messageObject.scheduled);
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, messageObject.messageOwner);
            ArrayList<TLRPC$PhotoSize> arrayList2 = tLRPC$TL_photo.sizes;
            delayedMessage.photoSize = arrayList2.get(arrayList2.size() - 1);
            delayedMessage.locationParent = tLRPC$TL_photo;
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
        TLRPC$Document document = delayedMessage.obj.getDocument();
        boolean z = false;
        if (document.thumbs.isEmpty() || (document.thumbs.get(0).location instanceof TLRPC$TL_fileLocationUnavailable)) {
            try {
                Bitmap loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), (Uri) null, 90.0f, 90.0f, true);
                if (loadBitmap != null) {
                    document.thumbs.clear();
                    ArrayList<TLRPC$PhotoSize> arrayList = document.thumbs;
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
            private final /* synthetic */ TLRPC$Document f$3;
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

    public /* synthetic */ void lambda$null$3$SendMessagesHelper(DelayedMessage delayedMessage, File file, TLRPC$Document tLRPC$Document, MessageObject messageObject) {
        delayedMessage.httpLocation = null;
        delayedMessage.obj.messageOwner.attachPath = file.toString();
        if (!tLRPC$Document.thumbs.isEmpty()) {
            delayedMessage.photoSize = tLRPC$Document.thumbs.get(0);
            delayedMessage.locationParent = tLRPC$Document;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject.messageOwner);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, false, true, false, 0, messageObject.scheduled);
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
        getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, delayedMessage.obj.messageOwner);
    }

    private void revertEditingMessageObject(MessageObject messageObject) {
        messageObject.cancelEditing = true;
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        tLRPC$Message.media = messageObject.previousMedia;
        tLRPC$Message.message = messageObject.previousCaption;
        tLRPC$Message.entities = messageObject.previousCaptionEntities;
        tLRPC$Message.attachPath = messageObject.previousAttachPath;
        tLRPC$Message.send_state = 0;
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
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, false, true, false, 0, messageObject.scheduled);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(messageObject);
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList2);
    }

    public void cancelSendingMessage(MessageObject messageObject) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject);
        cancelSendingMessage((ArrayList<MessageObject>) arrayList);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0192, code lost:
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
            if (r6 >= r11) goto L_0x01a2
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
            if (r14 == 0) goto L_0x0199
            java.lang.Object r14 = r22.next()
            java.util.Map$Entry r14 = (java.util.Map.Entry) r14
            java.lang.Object r15 = r14.getValue()
            java.util.ArrayList r15 = (java.util.ArrayList) r15
            r4 = 0
        L_0x0073:
            int r13 = r15.size()
            if (r4 >= r13) goto L_0x018e
            java.lang.Object r13 = r15.get(r4)
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r13 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r13
            r23 = r7
            int r7 = r13.type
            r24 = r8
            r8 = 4
            if (r7 != r8) goto L_0x014f
            r4 = -1
            r7 = 0
            r8 = 0
        L_0x008b:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r9 = r13.messageObjects
            int r9 = r9.size()
            if (r8 >= r9) goto L_0x00b3
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r13.messageObjects
            java.lang.Object r7 = r7.get(r8)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            int r9 = r7.getId()
            int r14 = r10.getId()
            if (r9 != r14) goto L_0x00b0
            int r4 = r10.getId()
            boolean r9 = r10.scheduled
            r0.removeFromUploadingMessages(r4, r9)
            r4 = r8
            goto L_0x00b3
        L_0x00b0:
            int r8 = r8 + 1
            goto L_0x008b
        L_0x00b3:
            if (r4 < 0) goto L_0x0192
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r13.messageObjects
            r8.remove(r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r8 = r13.messages
            r8.remove(r4)
            java.util.ArrayList<java.lang.String> r8 = r13.originalPaths
            r8.remove(r4)
            org.telegram.tgnet.TLObject r8 = r13.sendRequest
            if (r8 == 0) goto L_0x00d0
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r8 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r8 = r8.multi_media
            r8.remove(r4)
            goto L_0x00de
        L_0x00d0:
            org.telegram.tgnet.TLObject r8 = r13.sendEncryptedRequest
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r8 = (org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia) r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r9 = r8.messages
            r9.remove(r4)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r8 = r8.files
            r8.remove(r4)
        L_0x00de:
            org.telegram.messenger.MediaController r4 = org.telegram.messenger.MediaController.getInstance()
            r4.cancelVideoConvert(r10)
            java.util.HashMap<java.lang.Object, java.lang.Object> r4 = r13.extraHashMap
            java.lang.Object r4 = r4.get(r7)
            java.lang.String r4 = (java.lang.String) r4
            if (r4 == 0) goto L_0x00f2
            r2.add(r4)
        L_0x00f2:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r13.messageObjects
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x00ff
            r13.sendDelayedRequests()
            goto L_0x0192
        L_0x00ff:
            int r4 = r13.finalGroupMessage
            int r7 = r10.getId()
            if (r4 != r7) goto L_0x0145
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
            r14.putMessages((org.telegram.tgnet.TLRPC$messages_Messages) r15, (long) r16, (int) r18, (int) r19, (boolean) r20, (boolean) r21)
        L_0x0145:
            boolean r4 = r3.contains(r13)
            if (r4 != 0) goto L_0x0192
            r3.add(r13)
            goto L_0x0192
        L_0x014f:
            org.telegram.messenger.MessageObject r7 = r13.obj
            int r7 = r7.getId()
            int r8 = r10.getId()
            if (r7 != r8) goto L_0x0186
            int r7 = r10.getId()
            boolean r8 = r10.scheduled
            r0.removeFromUploadingMessages(r7, r8)
            r15.remove(r4)
            r13.sendDelayedRequests()
            org.telegram.messenger.MediaController r4 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r7 = r13.obj
            r4.cancelVideoConvert(r7)
            int r4 = r15.size()
            if (r4 != 0) goto L_0x0192
            java.lang.Object r4 = r14.getKey()
            r2.add(r4)
            org.telegram.tgnet.TLObject r4 = r13.sendEncryptedRequest
            if (r4 == 0) goto L_0x0192
            r7 = 1
            goto L_0x0194
        L_0x0186:
            int r4 = r4 + 1
            r7 = r23
            r8 = r24
            goto L_0x0073
        L_0x018e:
            r23 = r7
            r24 = r8
        L_0x0192:
            r7 = r23
        L_0x0194:
            r8 = r24
            r13 = 1
            goto L_0x0060
        L_0x0199:
            r23 = r7
            r24 = r8
            int r6 = r6 + 1
            r10 = r11
            goto L_0x001a
        L_0x01a2:
            r4 = 0
        L_0x01a3:
            int r6 = r2.size()
            if (r4 >= r6) goto L_0x01d1
            java.lang.Object r6 = r2.get(r4)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.String r11 = "http"
            boolean r11 = r6.startsWith(r11)
            if (r11 == 0) goto L_0x01bf
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            r11.cancelLoadHttpFile(r6)
            goto L_0x01c6
        L_0x01bf:
            org.telegram.messenger.FileLoader r11 = r26.getFileLoader()
            r11.cancelUploadFile(r6, r7)
        L_0x01c6:
            r0.stopVideoService(r6)
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r11 = r0.delayedMessages
            r11.remove(r6)
            int r4 = r4 + 1
            goto L_0x01a3
        L_0x01d1:
            int r2 = r3.size()
            r4 = 0
        L_0x01d6:
            if (r4 >= r2) goto L_0x01e6
            java.lang.Object r6 = r3.get(r4)
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r6
            r7 = 1
            r11 = 0
            r0.sendReadyToSendGroup(r6, r11, r7)
            int r4 = r4 + 1
            goto L_0x01d6
        L_0x01e6:
            r7 = 1
            r11 = 0
            int r2 = r27.size()
            if (r2 != r7) goto L_0x020e
            java.lang.Object r2 = r1.get(r11)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            boolean r2 = r2.isEditing()
            if (r2 == 0) goto L_0x020e
            java.lang.Object r2 = r1.get(r11)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.previousMedia
            if (r2 == 0) goto L_0x020e
            java.lang.Object r1 = r1.get(r11)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r0.revertEditingMessageObject(r1)
            goto L_0x0218
        L_0x020e:
            org.telegram.messenger.MessagesController r4 = r26.getMessagesController()
            r6 = 0
            r7 = 0
            r11 = 0
            r4.deleteMessages(r5, r6, r7, r8, r10, r11, r12)
        L_0x0218:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.cancelSendingMessage(java.util.ArrayList):void");
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean z) {
        if (messageObject.getId() >= 0) {
            if (messageObject.isEditing()) {
                editMessageMedia(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$TL_document) null, (String) null, (HashMap<String, String>) null, true, messageObject);
            }
            return false;
        }
        TLRPC$MessageAction tLRPC$MessageAction = messageObject.messageOwner.action;
        if (tLRPC$MessageAction instanceof TLRPC$TL_messageEncryptedAction) {
            TLRPC$EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                getMessagesStorage().markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                messageObject.messageOwner.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (tLRPC$Message.random_id == 0) {
                tLRPC$Message.random_id = getNextRandomId();
            }
            TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction = messageObject.messageOwner.action.encryptedAction;
            if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL) {
                getSecretChatHelper().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionDeleteMessages) {
                getSecretChatHelper().sendMessagesDeleteMessage(encryptedChat, (ArrayList<Long>) null, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionFlushHistory) {
                getSecretChatHelper().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionNotifyLayer) {
                getSecretChatHelper().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionReadMessages) {
                getSecretChatHelper().sendMessagesReadMessage(encryptedChat, (ArrayList<Long>) null, messageObject.messageOwner);
            } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionScreenshotMessages) {
                getSecretChatHelper().sendScreenshotMessage(encryptedChat, (ArrayList<Long>) null, messageObject.messageOwner);
            } else if (!(tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionTyping)) {
                if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionResend) {
                    getSecretChatHelper().sendResendMessage(encryptedChat, 0, 0, messageObject.messageOwner);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionCommitKey) {
                    getSecretChatHelper().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionAbortKey) {
                    getSecretChatHelper().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionRequestKey) {
                    getSecretChatHelper().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionAcceptKey) {
                    getSecretChatHelper().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionNoop) {
                    getSecretChatHelper().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        }
        if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionScreenshotTaken) {
            TLRPC$User user = getMessagesController().getUser(Integer.valueOf((int) messageObject.getDialogId()));
            TLRPC$Message tLRPC$Message2 = messageObject.messageOwner;
            sendScreenshotMessage(user, tLRPC$Message2.reply_to_msg_id, tLRPC$Message2);
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
            TLRPC$Message tLRPC$Message = messageObject2.messageOwner;
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (tLRPC$MessageMedia == null || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaEmpty) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaGame) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice)) {
                TLRPC$Message tLRPC$Message2 = messageObject2.messageOwner;
                if (tLRPC$Message2.message != null) {
                    TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message2.media;
                    TLRPC$WebPage tLRPC$WebPage = tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaWebPage ? tLRPC$MessageMedia2.webpage : null;
                    ArrayList<TLRPC$MessageEntity> arrayList2 = messageObject2.messageOwner.entities;
                    if (arrayList2 == null || arrayList2.isEmpty()) {
                        arrayList = null;
                    } else {
                        ArrayList arrayList3 = new ArrayList();
                        for (int i = 0; i < messageObject2.messageOwner.entities.size(); i++) {
                            TLRPC$MessageEntity tLRPC$MessageEntity = messageObject2.messageOwner.entities.get(i);
                            if ((tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityBold) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityItalic) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityPre) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCode) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityTextUrl)) {
                                arrayList3.add(tLRPC$MessageEntity);
                            }
                        }
                        arrayList = arrayList3;
                    }
                    sendMessage(messageObject2.messageOwner.message, j, messageObject2.replyMessageObject, tLRPC$WebPage, true, arrayList, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (((int) j2) != 0) {
                    ArrayList arrayList4 = new ArrayList();
                    arrayList4.add(messageObject2);
                    sendMessage(arrayList4, j, true, 0);
                }
            } else {
                int i2 = (int) j2;
                if (i2 != 0 || tLRPC$Message.to_id == null || (!(tLRPC$MessageMedia.photo instanceof TLRPC$TL_photo) && !(tLRPC$MessageMedia.document instanceof TLRPC$TL_document))) {
                    hashMap = null;
                } else {
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("parentObject", "sent_" + messageObject2.messageOwner.to_id.channel_id + "_" + messageObject.getId());
                    hashMap = hashMap2;
                }
                TLRPC$Message tLRPC$Message3 = messageObject2.messageOwner;
                TLRPC$MessageMedia tLRPC$MessageMedia3 = tLRPC$Message3.media;
                TLRPC$Photo tLRPC$Photo = tLRPC$MessageMedia3.photo;
                if (tLRPC$Photo instanceof TLRPC$TL_photo) {
                    sendMessage((TLRPC$TL_photo) tLRPC$Photo, (String) null, j, messageObject2.replyMessageObject, tLRPC$Message3.message, tLRPC$Message3.entities, (TLRPC$ReplyMarkup) null, hashMap, true, 0, tLRPC$MessageMedia3.ttl_seconds, messageObject);
                    return;
                }
                TLRPC$Document tLRPC$Document = tLRPC$MessageMedia3.document;
                if (tLRPC$Document instanceof TLRPC$TL_document) {
                    sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, tLRPC$Message3.attachPath, j, messageObject2.replyMessageObject, tLRPC$Message3.message, tLRPC$Message3.entities, (TLRPC$ReplyMarkup) null, hashMap, true, 0, tLRPC$MessageMedia3.ttl_seconds, messageObject);
                } else if ((tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaVenue) || (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaGeo)) {
                    sendMessage(messageObject2.messageOwner.media, j, messageObject2.replyMessageObject, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (tLRPC$MessageMedia3.phone_number != null) {
                    TLRPC$TL_userContact_old2 tLRPC$TL_userContact_old2 = new TLRPC$TL_userContact_old2();
                    TLRPC$MessageMedia tLRPC$MessageMedia4 = messageObject2.messageOwner.media;
                    tLRPC$TL_userContact_old2.phone = tLRPC$MessageMedia4.phone_number;
                    tLRPC$TL_userContact_old2.first_name = tLRPC$MessageMedia4.first_name;
                    tLRPC$TL_userContact_old2.last_name = tLRPC$MessageMedia4.last_name;
                    tLRPC$TL_userContact_old2.id = tLRPC$MessageMedia4.user_id;
                    sendMessage((TLRPC$User) tLRPC$TL_userContact_old2, j, messageObject2.replyMessageObject, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (i2 != 0) {
                    ArrayList arrayList5 = new ArrayList();
                    arrayList5.add(messageObject2);
                    sendMessage(arrayList5, j, true, 0);
                }
            }
        }
    }

    public void sendScreenshotMessage(TLRPC$User tLRPC$User, int i, TLRPC$Message tLRPC$Message) {
        if (tLRPC$User != null && i != 0 && tLRPC$User.id != getUserConfig().getClientUserId()) {
            TLRPC$TL_messages_sendScreenshotNotification tLRPC$TL_messages_sendScreenshotNotification = new TLRPC$TL_messages_sendScreenshotNotification();
            TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
            tLRPC$TL_messages_sendScreenshotNotification.peer = tLRPC$TL_inputPeerUser;
            tLRPC$TL_inputPeerUser.access_hash = tLRPC$User.access_hash;
            tLRPC$TL_inputPeerUser.user_id = tLRPC$User.id;
            if (tLRPC$Message != null) {
                tLRPC$TL_messages_sendScreenshotNotification.reply_to_msg_id = i;
                tLRPC$TL_messages_sendScreenshotNotification.random_id = tLRPC$Message.random_id;
            } else {
                tLRPC$Message = new TLRPC$TL_messageService();
                tLRPC$Message.random_id = getNextRandomId();
                tLRPC$Message.dialog_id = (long) tLRPC$User.id;
                tLRPC$Message.unread = true;
                tLRPC$Message.out = true;
                int newMessageId = getUserConfig().getNewMessageId();
                tLRPC$Message.id = newMessageId;
                tLRPC$Message.local_id = newMessageId;
                tLRPC$Message.from_id = getUserConfig().getClientUserId();
                int i2 = tLRPC$Message.flags | 256;
                tLRPC$Message.flags = i2;
                tLRPC$Message.flags = i2 | 8;
                tLRPC$Message.reply_to_msg_id = i;
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$Message.to_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = tLRPC$User.id;
                tLRPC$Message.date = getConnectionsManager().getCurrentTime();
                tLRPC$Message.action = new TLRPC$TL_messageActionScreenshotTaken();
                getUserConfig().saveConfig(false);
            }
            tLRPC$TL_messages_sendScreenshotNotification.random_id = tLRPC$Message.random_id;
            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message, false);
            messageObject.messageOwner.send_state = 1;
            ArrayList arrayList = new ArrayList();
            arrayList.add(messageObject);
            getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(tLRPC$Message);
            getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList2, false, true, false, 0, false);
            performSendMessageRequest(tLRPC$TL_messages_sendScreenshotNotification, messageObject, (String) null, (DelayedMessage) null, (Object) null, false);
        }
    }

    public void sendSticker(TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, Object obj, boolean z, int i) {
        TLRPC$TL_document_layer82 tLRPC$TL_document_layer82;
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        long j2 = j;
        if (tLRPC$Document2 != null) {
            if (((int) j2) == 0) {
                if (getMessagesController().getEncryptedChat(Integer.valueOf((int) (j2 >> 32))) != null) {
                    TLRPC$TL_document_layer82 tLRPC$TL_document_layer822 = new TLRPC$TL_document_layer82();
                    tLRPC$TL_document_layer822.id = tLRPC$Document2.id;
                    tLRPC$TL_document_layer822.access_hash = tLRPC$Document2.access_hash;
                    tLRPC$TL_document_layer822.date = tLRPC$Document2.date;
                    tLRPC$TL_document_layer822.mime_type = tLRPC$Document2.mime_type;
                    byte[] bArr = tLRPC$Document2.file_reference;
                    tLRPC$TL_document_layer822.file_reference = bArr;
                    if (bArr == null) {
                        tLRPC$TL_document_layer822.file_reference = new byte[0];
                    }
                    tLRPC$TL_document_layer822.size = tLRPC$Document2.size;
                    tLRPC$TL_document_layer822.dc_id = tLRPC$Document2.dc_id;
                    tLRPC$TL_document_layer822.attributes = new ArrayList<>(tLRPC$Document2.attributes);
                    if (tLRPC$TL_document_layer822.mime_type == null) {
                        tLRPC$TL_document_layer822.mime_type = "";
                    }
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90);
                    if (closestPhotoSizeWithSize instanceof TLRPC$TL_photoSize) {
                        File pathToAttach = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                        if (pathToAttach.exists()) {
                            try {
                                pathToAttach.length();
                                byte[] bArr2 = new byte[((int) pathToAttach.length())];
                                new RandomAccessFile(pathToAttach, "r").readFully(bArr2);
                                TLRPC$TL_photoCachedSize tLRPC$TL_photoCachedSize = new TLRPC$TL_photoCachedSize();
                                TLRPC$TL_fileLocation_layer82 tLRPC$TL_fileLocation_layer82 = new TLRPC$TL_fileLocation_layer82();
                                tLRPC$TL_fileLocation_layer82.dc_id = closestPhotoSizeWithSize.location.dc_id;
                                tLRPC$TL_fileLocation_layer82.volume_id = closestPhotoSizeWithSize.location.volume_id;
                                tLRPC$TL_fileLocation_layer82.local_id = closestPhotoSizeWithSize.location.local_id;
                                tLRPC$TL_fileLocation_layer82.secret = closestPhotoSizeWithSize.location.secret;
                                tLRPC$TL_photoCachedSize.location = tLRPC$TL_fileLocation_layer82;
                                tLRPC$TL_photoCachedSize.size = closestPhotoSizeWithSize.size;
                                tLRPC$TL_photoCachedSize.w = closestPhotoSizeWithSize.w;
                                tLRPC$TL_photoCachedSize.h = closestPhotoSizeWithSize.h;
                                tLRPC$TL_photoCachedSize.type = closestPhotoSizeWithSize.type;
                                tLRPC$TL_photoCachedSize.bytes = bArr2;
                                tLRPC$TL_document_layer822.thumbs.add(tLRPC$TL_photoCachedSize);
                                tLRPC$TL_document_layer822.flags |= 1;
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        }
                    }
                    if (tLRPC$TL_document_layer822.thumbs.isEmpty()) {
                        TLRPC$TL_photoSizeEmpty tLRPC$TL_photoSizeEmpty = new TLRPC$TL_photoSizeEmpty();
                        tLRPC$TL_photoSizeEmpty.type = "s";
                        tLRPC$TL_document_layer822.thumbs.add(tLRPC$TL_photoSizeEmpty);
                    }
                    tLRPC$TL_document_layer82 = tLRPC$TL_document_layer822;
                } else {
                    return;
                }
            } else {
                tLRPC$TL_document_layer82 = tLRPC$Document2;
            }
            if (MessageObject.isGifDocument(tLRPC$TL_document_layer82)) {
                mediaSendQueue.postRunnable(new Runnable(tLRPC$TL_document_layer82, j, messageObject, z, i, obj) {
                    private final /* synthetic */ TLRPC$Document f$1;
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
            sendMessage((TLRPC$TL_document) tLRPC$TL_document_layer82, (VideoEditedInfo) null, (String) null, j, messageObject, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj);
        }
    }

    public /* synthetic */ void lambda$sendSticker$6$SendMessagesHelper(TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, boolean z, int i, Object obj) {
        String str;
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        Bitmap[] bitmapArr = new Bitmap[1];
        String[] strArr = new String[1];
        String key = ImageLocation.getForDocument(tLRPC$Document).getKey((Object) null, (Object) null, false);
        if ("video/mp4".equals(tLRPC$Document2.mime_type)) {
            str = ".mp4";
        } else {
            str = "video/x-matroska".equals(tLRPC$Document2.mime_type) ? ".mkv" : "";
        }
        File directory = FileLoader.getDirectory(3);
        File file = new File(directory, key + str);
        if (!file.exists()) {
            File directory2 = FileLoader.getDirectory(2);
            file = new File(directory2, key + str);
        }
        ensureMediaThumbExists(false, tLRPC$Document, file.getAbsolutePath(), (Uri) null, 0);
        strArr[0] = getKeyForPhotoSize(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 320), bitmapArr, true, true);
        AndroidUtilities.runOnUIThread(new Runnable(bitmapArr, strArr, tLRPC$Document, j, messageObject, z, i, obj) {
            private final /* synthetic */ Bitmap[] f$1;
            private final /* synthetic */ String[] f$2;
            private final /* synthetic */ TLRPC$Document f$3;
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

    public /* synthetic */ void lambda$null$5$SendMessagesHelper(Bitmap[] bitmapArr, String[] strArr, TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, boolean z, int i, Object obj) {
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, (String) null, j, messageObject, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj);
    }

    /* JADX WARNING: Removed duplicated region for block: B:106:0x0297  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02ad  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02b5  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02bf  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02c7  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03a5  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03b0  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x03ba  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03d7  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0421  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0425  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x042c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0456  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0479  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x047b  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x048b A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x04cc  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x04d3  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x04de  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x04f1  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x04f3  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0506  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0508  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0510  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x0578  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x058d  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0607  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0617  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x0641  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0662  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0664  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x066c  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x066f  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x06bf  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01ab  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01d9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r51, long r52, boolean r54, int r55) {
        /*
            r50 = this;
            r14 = r50
            r15 = r51
            r12 = r52
            r11 = r55
            r10 = 0
            if (r15 == 0) goto L_0x0777
            boolean r0 = r51.isEmpty()
            if (r0 == 0) goto L_0x0013
            goto L_0x0777
        L_0x0013:
            int r0 = (int) r12
            if (r0 == 0) goto L_0x0758
            org.telegram.messenger.MessagesController r1 = r50.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r9 = r1.getPeer(r0)
            if (r0 <= 0) goto L_0x003d
            org.telegram.messenger.MessagesController r1 = r50.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r1 != 0) goto L_0x002f
            return r10
        L_0x002f:
            r6 = 0
            r16 = 0
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 1
            r21 = 1
            goto L_0x0074
        L_0x003d:
            org.telegram.messenger.MessagesController r1 = r50.getMessagesController()
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
            r17 = r3
            r18 = r4
            r19 = r5
            r20 = r6
            r21 = r16
            r6 = r1
            r16 = r2
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
            org.telegram.messenger.MessagesController r10 = r50.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r10 = r10.getInputPeer(r0)
            org.telegram.messenger.UserConfig r0 = r50.getUserConfig()
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
            r29 = r2
            r27 = r22
            r7 = 0
            r28 = 0
            r49 = r4
            r4 = r1
            r1 = r49
        L_0x00ba:
            int r2 = r51.size()
            if (r7 >= r2) goto L_0x0753
            java.lang.Object r2 = r15.get(r7)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r30 = r2.getId()
            if (r30 <= 0) goto L_0x06df
            boolean r30 = r2.needDrawBluredPreview()
            if (r30 == 0) goto L_0x00d4
            goto L_0x06df
        L_0x00d4:
            r30 = r4
            if (r18 != 0) goto L_0x0135
            boolean r32 = r2.isSticker()
            if (r32 != 0) goto L_0x00f0
            boolean r32 = r2.isAnimatedSticker()
            if (r32 != 0) goto L_0x00f0
            boolean r32 = r2.isGif()
            if (r32 != 0) goto L_0x00f0
            boolean r32 = r2.isGame()
            if (r32 == 0) goto L_0x0135
        L_0x00f0:
            if (r28 != 0) goto L_0x0118
            r2 = 8
            boolean r2 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r2)
            if (r2 == 0) goto L_0x00fc
            r4 = 4
            goto L_0x00fd
        L_0x00fc:
            r4 = 1
        L_0x00fd:
            r23 = r0
            r28 = r4
        L_0x0101:
            r35 = r6
            r12 = r7
            r31 = r8
            r40 = r10
            r14 = r27
            r4 = r30
            r22 = 1
            r25 = 0
            r38 = 0
        L_0x0112:
            r27 = r24
            r24 = r5
            goto L_0x0739
        L_0x0118:
            r23 = r0
            r34 = r1
            r35 = r6
            r12 = r7
            r31 = r8
            r40 = r10
            r14 = r27
            r37 = r29
            r22 = 1
            r25 = 0
            r38 = 0
            r29 = r3
            r27 = r24
            r24 = r5
            goto L_0x0731
        L_0x0135:
            r32 = 2
            if (r19 != 0) goto L_0x0166
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r14 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r14 != 0) goto L_0x0145
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r4 == 0) goto L_0x0166
        L_0x0145:
            if (r28 != 0) goto L_0x0118
            r2 = 7
            boolean r2 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r2)
            if (r2 == 0) goto L_0x0150
            r32 = 5
        L_0x0150:
            r23 = r0
            r35 = r6
            r12 = r7
            r40 = r10
            r14 = r27
            r4 = r30
            r28 = r32
            r22 = 1
            r25 = 0
            r38 = 0
            r31 = r8
            goto L_0x0112
        L_0x0166:
            if (r21 != 0) goto L_0x0183
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0183
            if (r28 != 0) goto L_0x0118
            r2 = 10
            boolean r2 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r2)
            if (r2 == 0) goto L_0x017c
            r2 = 6
            goto L_0x017d
        L_0x017c:
            r2 = 3
        L_0x017d:
            r23 = r0
            r28 = r2
            goto L_0x0101
        L_0x0183:
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message
            r4.<init>()
            long r34 = r2.getDialogId()
            int r14 = (r34 > r8 ? 1 : (r34 == r8 ? 0 : -1))
            if (r14 != 0) goto L_0x01a2
            org.telegram.tgnet.TLRPC$Message r14 = r2.messageOwner
            int r14 = r14.from_id
            org.telegram.messenger.UserConfig r34 = r50.getUserConfig()
            r35 = r6
            int r6 = r34.getClientUserId()
            if (r14 != r6) goto L_0x01a4
            r6 = 1
            goto L_0x01a5
        L_0x01a2:
            r35 = r6
        L_0x01a4:
            r6 = 0
        L_0x01a5:
            boolean r14 = r2.isForwarded()
            if (r14 == 0) goto L_0x01d9
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r6 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r6.<init>()
            r4.fwd_from = r6
            org.telegram.tgnet.TLRPC$Message r14 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            r34 = r10
            int r10 = r14.flags
            r6.flags = r10
            int r10 = r14.from_id
            r6.from_id = r10
            int r10 = r14.date
            r6.date = r10
            int r10 = r14.channel_id
            r6.channel_id = r10
            int r10 = r14.channel_post
            r6.channel_post = r10
            java.lang.String r10 = r14.post_author
            r6.post_author = r10
            java.lang.String r10 = r14.from_name
            r6.from_name = r10
            r10 = 4
            r4.flags = r10
            goto L_0x0277
        L_0x01d9:
            r34 = r10
            r10 = 4
            if (r6 != 0) goto L_0x0277
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r6 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r6.<init>()
            r4.fwd_from = r6
            int r14 = r2.getId()
            r6.channel_post = r14
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r4.fwd_from
            int r14 = r6.flags
            r14 = r14 | r10
            r6.flags = r14
            boolean r6 = r2.isFromUser()
            if (r6 == 0) goto L_0x0207
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r4.fwd_from
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner
            int r10 = r10.from_id
            r6.from_id = r10
            int r10 = r6.flags
            r14 = 1
            r10 = r10 | r14
            r6.flags = r10
            goto L_0x0225
        L_0x0207:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r4.fwd_from
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r14 = r10.to_id
            int r14 = r14.channel_id
            r6.channel_id = r14
            int r14 = r6.flags
            r14 = r14 | 2
            r6.flags = r14
            boolean r11 = r10.post
            if (r11 == 0) goto L_0x0225
            int r10 = r10.from_id
            if (r10 <= 0) goto L_0x0225
            r6.from_id = r10
            r10 = r14 | 1
            r6.flags = r10
        L_0x0225:
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            java.lang.String r6 = r6.post_author
            if (r6 == 0) goto L_0x0237
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r4.fwd_from
            r10.post_author = r6
            int r6 = r10.flags
            r11 = 8
            r6 = r6 | r11
            r10.flags = r6
            goto L_0x026e
        L_0x0237:
            boolean r6 = r2.isOutOwner()
            if (r6 != 0) goto L_0x026e
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r10 = r6.from_id
            if (r10 <= 0) goto L_0x026e
            boolean r6 = r6.post
            if (r6 == 0) goto L_0x026e
            org.telegram.messenger.MessagesController r6 = r50.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner
            int r10 = r10.from_id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r10)
            if (r6 == 0) goto L_0x026e
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r4.fwd_from
            java.lang.String r11 = r6.first_name
            java.lang.String r6 = r6.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r11, r6)
            r10.post_author = r6
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r4.fwd_from
            int r10 = r6.flags
            r11 = 8
            r10 = r10 | r11
            r6.flags = r10
        L_0x026e:
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r6 = r6.date
            r4.date = r6
            r6 = 4
            r4.flags = r6
        L_0x0277:
            int r6 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r6 != 0) goto L_0x02a1
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r4.fwd_from
            if (r6 == 0) goto L_0x02a1
            int r10 = r6.flags
            r10 = r10 | 16
            r6.flags = r10
            int r10 = r2.getId()
            r6.saved_from_msg_id = r10
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r4.fwd_from
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r10 = r10.to_id
            r6.saved_from_peer = r10
            int r6 = r10.user_id
            if (r6 != r0) goto L_0x02a1
            r31 = r8
            long r8 = r2.getDialogId()
            int r6 = (int) r8
            r10.user_id = r6
            goto L_0x02a3
        L_0x02a1:
            r31 = r8
        L_0x02a3:
            if (r20 != 0) goto L_0x02b5
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r6 == 0) goto L_0x02b5
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r6.<init>()
            r4.media = r6
            goto L_0x02bb
        L_0x02b5:
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            r4.media = r6
        L_0x02bb:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r4.media
            if (r6 == 0) goto L_0x02c5
            int r6 = r4.flags
            r6 = r6 | 512(0x200, float:7.175E-43)
            r4.flags = r6
        L_0x02c5:
            if (r16 == 0) goto L_0x02ce
            int r6 = r4.flags
            r8 = -2147483648(0xfffffffvar_, float:-0.0)
            r6 = r6 | r8
            r4.flags = r6
        L_0x02ce:
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r6 = r6.via_bot_id
            if (r6 == 0) goto L_0x02dc
            r4.via_bot_id = r6
            int r6 = r4.flags
            r6 = r6 | 2048(0x800, float:2.87E-42)
            r4.flags = r6
        L_0x02dc:
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            java.lang.String r6 = r6.message
            r4.message = r6
            int r6 = r2.getId()
            r4.fwd_msg_id = r6
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            java.lang.String r8 = r6.attachPath
            r4.attachPath = r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r6.entities
            r4.entities = r8
            org.telegram.tgnet.TLRPC$ReplyMarkup r6 = r6.reply_markup
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            if (r6 == 0) goto L_0x03a5
            org.telegram.tgnet.TLRPC$TL_replyInlineMarkup r6 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            r6.<init>()
            r4.reply_markup = r6
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r6 = r6.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r6 = r6.rows
            int r6 = r6.size()
            r8 = 0
            r9 = 0
        L_0x030b:
            if (r8 >= r6) goto L_0x0392
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r10 = r10.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r10 = r10.rows
            java.lang.Object r10 = r10.get(r8)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r10 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r11 = r10.buttons
            int r11 = r11.size()
            r14 = 0
            r33 = 0
        L_0x0322:
            r36 = r0
            if (r14 >= r11) goto L_0x0383
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r0 = r10.buttons
            java.lang.Object r0 = r0.get(r14)
            org.telegram.tgnet.TLRPC$KeyboardButton r0 = (org.telegram.tgnet.TLRPC$KeyboardButton) r0
            r37 = r6
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r38 = r9
            if (r6 != 0) goto L_0x0341
            boolean r9 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl
            if (r9 != 0) goto L_0x0341
            boolean r9 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline
            if (r9 == 0) goto L_0x033f
            goto L_0x0341
        L_0x033f:
            r9 = 1
            goto L_0x0387
        L_0x0341:
            if (r6 == 0) goto L_0x0362
            org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth r6 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r6.<init>()
            int r9 = r0.flags
            r6.flags = r9
            java.lang.String r9 = r0.fwd_text
            if (r9 == 0) goto L_0x0355
            r6.fwd_text = r9
            r6.text = r9
            goto L_0x0359
        L_0x0355:
            java.lang.String r9 = r0.text
            r6.text = r9
        L_0x0359:
            java.lang.String r9 = r0.url
            r6.url = r9
            int r0 = r0.button_id
            r6.button_id = r0
            r0 = r6
        L_0x0362:
            if (r33 != 0) goto L_0x0371
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r6 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow
            r6.<init>()
            org.telegram.tgnet.TLRPC$ReplyMarkup r9 = r4.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r9 = r9.rows
            r9.add(r6)
            goto L_0x0373
        L_0x0371:
            r6 = r33
        L_0x0373:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r9 = r6.buttons
            r9.add(r0)
            int r14 = r14 + 1
            r33 = r6
            r0 = r36
            r6 = r37
            r9 = r38
            goto L_0x0322
        L_0x0383:
            r37 = r6
            r38 = r9
        L_0x0387:
            if (r9 == 0) goto L_0x038a
            goto L_0x0396
        L_0x038a:
            int r8 = r8 + 1
            r0 = r36
            r6 = r37
            goto L_0x030b
        L_0x0392:
            r36 = r0
            r38 = r9
        L_0x0396:
            if (r9 != 0) goto L_0x039f
            int r0 = r4.flags
            r0 = r0 | 64
            r4.flags = r0
            goto L_0x03a7
        L_0x039f:
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            r8 = 0
            r0.reply_markup = r8
            goto L_0x03a8
        L_0x03a5:
            r36 = r0
        L_0x03a7:
            r8 = 0
        L_0x03a8:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r4.entities
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x03b6
            int r0 = r4.flags
            r0 = r0 | 128(0x80, float:1.794E-43)
            r4.flags = r0
        L_0x03b6:
            java.lang.String r0 = r4.attachPath
            if (r0 != 0) goto L_0x03be
            java.lang.String r0 = ""
            r4.attachPath = r0
        L_0x03be:
            org.telegram.messenger.UserConfig r0 = r50.getUserConfig()
            int r0 = r0.getNewMessageId()
            r4.id = r0
            r4.local_id = r0
            r0 = 1
            r4.out = r0
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            long r9 = r0.grouped_id
            r37 = 0
            int r0 = (r9 > r37 ? 1 : (r9 == r37 ? 0 : -1))
            if (r0 == 0) goto L_0x0403
            java.lang.Object r0 = r5.get(r9)
            java.lang.Long r0 = (java.lang.Long) r0
            if (r0 != 0) goto L_0x03f3
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r39 = r0.nextLong()
            java.lang.Long r0 = java.lang.Long.valueOf(r39)
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            r39 = r9
            long r8 = r6.grouped_id
            r5.put(r8, r0)
            goto L_0x03f5
        L_0x03f3:
            r39 = r9
        L_0x03f5:
            long r8 = r0.longValue()
            r4.grouped_id = r8
            int r0 = r4.flags
            r6 = 131072(0x20000, float:1.83671E-40)
            r0 = r0 | r6
            r4.flags = r0
            goto L_0x0405
        L_0x0403:
            r39 = r9
        L_0x0405:
            int r0 = r51.size()
            r6 = 1
            int r0 = r0 - r6
            if (r7 == r0) goto L_0x0425
            int r0 = r7 + 1
            java.lang.Object r0 = r15.get(r0)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            long r8 = r0.grouped_id
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            long r10 = r0.grouped_id
            int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0425
            r9 = r24
            r0 = 1
            goto L_0x0428
        L_0x0425:
            r9 = r24
            r0 = 0
        L_0x0428:
            int r6 = r9.channel_id
            if (r6 == 0) goto L_0x0440
            if (r16 != 0) goto L_0x0440
            if (r17 == 0) goto L_0x0439
            org.telegram.messenger.UserConfig r6 = r50.getUserConfig()
            int r6 = r6.getClientUserId()
            goto L_0x043a
        L_0x0439:
            int r6 = -r6
        L_0x043a:
            r4.from_id = r6
            r6 = 1
            r4.post = r6
            goto L_0x0450
        L_0x0440:
            org.telegram.messenger.UserConfig r6 = r50.getUserConfig()
            int r6 = r6.getClientUserId()
            r4.from_id = r6
            int r6 = r4.flags
            r6 = r6 | 256(0x100, float:3.59E-43)
            r4.flags = r6
        L_0x0450:
            long r10 = r4.random_id
            int r6 = (r10 > r37 ? 1 : (r10 == r37 ? 0 : -1))
            if (r6 != 0) goto L_0x045c
            long r10 = r50.getNextRandomId()
            r4.random_id = r10
        L_0x045c:
            long r10 = r4.random_id
            java.lang.Long r6 = java.lang.Long.valueOf(r10)
            r3.add(r6)
            long r10 = r4.random_id
            r14 = r27
            r14.put(r10, r4)
            int r6 = r4.fwd_msg_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r1.add(r6)
            r11 = r55
            if (r11 == 0) goto L_0x047b
            r6 = r11
            goto L_0x0483
        L_0x047b:
            org.telegram.tgnet.ConnectionsManager r6 = r50.getConnectionsManager()
            int r6 = r6.getCurrentTime()
        L_0x0483:
            r4.date = r6
            r10 = r34
            boolean r6 = r10 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r6 == 0) goto L_0x049b
            if (r16 != 0) goto L_0x049b
            if (r11 != 0) goto L_0x0498
            r8 = 1
            r4.views = r8
            int r8 = r4.flags
            r8 = r8 | 1024(0x400, float:1.435E-42)
            r4.flags = r8
        L_0x0498:
            r24 = r5
            goto L_0x04b4
        L_0x049b:
            org.telegram.tgnet.TLRPC$Message r8 = r2.messageOwner
            r24 = r5
            int r5 = r8.flags
            r5 = r5 & 1024(0x400, float:1.435E-42)
            if (r5 == 0) goto L_0x04b1
            if (r11 != 0) goto L_0x04b1
            int r5 = r8.views
            r4.views = r5
            int r5 = r4.flags
            r5 = r5 | 1024(0x400, float:1.435E-42)
            r4.flags = r5
        L_0x04b1:
            r5 = 1
            r4.unread = r5
        L_0x04b4:
            r4.dialog_id = r12
            r4.to_id = r9
            boolean r5 = org.telegram.messenger.MessageObject.isVoiceMessage(r4)
            if (r5 != 0) goto L_0x04c4
            boolean r5 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r4)
            if (r5 == 0) goto L_0x04d6
        L_0x04c4:
            if (r6 == 0) goto L_0x04d3
            int r5 = r2.getChannelId()
            if (r5 == 0) goto L_0x04d3
            boolean r5 = r2.isContentUnread()
            r4.media_unread = r5
            goto L_0x04d6
        L_0x04d3:
            r5 = 1
            r4.media_unread = r5
        L_0x04d6:
            org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.to_id
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r6 == 0) goto L_0x04e3
            int r5 = r5.channel_id
            int r5 = -r5
            r4.ttl = r5
        L_0x04e3:
            org.telegram.messenger.MessageObject r5 = new org.telegram.messenger.MessageObject
            r8 = r50
            int r6 = r8.currentAccount
            r27 = r9
            r9 = 1
            r5.<init>(r6, r4, r9)
            if (r11 == 0) goto L_0x04f3
            r6 = 1
            goto L_0x04f4
        L_0x04f3:
            r6 = 0
        L_0x04f4:
            r5.scheduled = r6
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            r6.send_state = r9
            r6 = r30
            r6.add(r5)
            r5 = r29
            r5.add(r4)
            if (r11 == 0) goto L_0x0508
            r9 = 1
            goto L_0x0509
        L_0x0508:
            r9 = 0
        L_0x0509:
            r8.putToSendingMessages(r4, r9)
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r4 == 0) goto L_0x0544
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r9 = "forward message user_id = "
            r4.append(r9)
            int r9 = r10.user_id
            r4.append(r9)
            java.lang.String r9 = " chat_id = "
            r4.append(r9)
            int r9 = r10.chat_id
            r4.append(r9)
            java.lang.String r9 = " channel_id = "
            r4.append(r9)
            int r9 = r10.channel_id
            r4.append(r9)
            java.lang.String r9 = " access_hash = "
            r4.append(r9)
            long r8 = r10.access_hash
            r4.append(r8)
            java.lang.String r4 = r4.toString()
            org.telegram.messenger.FileLog.d(r4)
        L_0x0544:
            if (r0 == 0) goto L_0x054c
            int r0 = r5.size()
            if (r0 > 0) goto L_0x058d
        L_0x054c:
            int r0 = r5.size()
            r4 = 100
            if (r0 == r4) goto L_0x058d
            int r0 = r51.size()
            r4 = 1
            int r0 = r0 - r4
            if (r7 == r0) goto L_0x058d
            int r0 = r51.size()
            int r0 = r0 - r4
            if (r7 == r0) goto L_0x0578
            int r0 = r7 + 1
            java.lang.Object r0 = r15.get(r0)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r8 = r0.getDialogId()
            long r29 = r2.getDialogId()
            int r0 = (r8 > r29 ? 1 : (r8 == r29 ? 0 : -1))
            if (r0 == 0) goto L_0x0578
            goto L_0x058d
        L_0x0578:
            r34 = r1
            r29 = r3
            r37 = r5
            r30 = r6
            r12 = r7
            r40 = r10
            r23 = r36
            r22 = 1
            r25 = 0
            r38 = 0
            goto L_0x0731
        L_0x058d:
            org.telegram.messenger.MessagesStorage r41 = r50.getMessagesStorage()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r5)
            r43 = 0
            r44 = 1
            r45 = 0
            r46 = 0
            if (r11 == 0) goto L_0x05a3
            r47 = 1
            goto L_0x05a5
        L_0x05a3:
            r47 = 0
        L_0x05a5:
            r42 = r0
            r41.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r42, (boolean) r43, (boolean) r44, (boolean) r45, (int) r46, (boolean) r47)
            org.telegram.messenger.MessagesController r0 = r50.getMessagesController()
            if (r11 == 0) goto L_0x05b2
            r4 = 1
            goto L_0x05b3
        L_0x05b2:
            r4 = 0
        L_0x05b3:
            r0.updateInterfaceWithMessages(r12, r6, r4)
            org.telegram.messenger.NotificationCenter r0 = r50.getNotificationCenter()
            int r4 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r0.postNotificationName(r4, r9)
            org.telegram.messenger.UserConfig r0 = r50.getUserConfig()
            r0.saveConfig(r8)
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r9 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages
            r9.<init>()
            r9.to_peer = r10
            int r0 = (r39 > r37 ? 1 : (r39 == r37 ? 0 : -1))
            if (r0 == 0) goto L_0x05d6
            r0 = 1
            goto L_0x05d7
        L_0x05d6:
            r0 = 0
        L_0x05d7:
            r9.grouped = r0
            r8 = r50
            if (r54 == 0) goto L_0x0600
            int r0 = r8.currentAccount
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r29 = r5
            java.lang.String r5 = "silent_"
            r4.append(r5)
            r4.append(r12)
            java.lang.String r4 = r4.toString()
            r5 = 0
            boolean r0 = r0.getBoolean(r4, r5)
            if (r0 == 0) goto L_0x05fe
            goto L_0x0602
        L_0x05fe:
            r0 = 0
            goto L_0x0603
        L_0x0600:
            r29 = r5
        L_0x0602:
            r0 = 1
        L_0x0603:
            r9.silent = r0
            if (r11 == 0) goto L_0x060f
            r9.schedule_date = r11
            int r0 = r9.flags
            r0 = r0 | 1024(0x400, float:1.435E-42)
            r9.flags = r0
        L_0x060f:
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r0 == 0) goto L_0x0641
            org.telegram.messenger.MessagesController r0 = r50.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.to_id
            int r4 = r4.channel_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            r4.<init>()
            r9.from_peer = r4
            org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.to_id
            int r5 = r5.channel_id
            r4.channel_id = r5
            r30 = r6
            if (r0 == 0) goto L_0x064a
            long r5 = r0.access_hash
            r4.access_hash = r5
            goto L_0x064a
        L_0x0641:
            r30 = r6
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r0.<init>()
            r9.from_peer = r0
        L_0x064a:
            r9.random_id = r3
            r9.id = r1
            int r0 = r51.size()
            r6 = 1
            r5 = 0
            if (r0 != r6) goto L_0x0664
            java.lang.Object r0 = r15.get(r5)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0.with_my_score
            if (r0 == 0) goto L_0x0664
            r0 = 1
            goto L_0x0665
        L_0x0664:
            r0 = 0
        L_0x0665:
            r9.with_my_score = r0
            r0 = 2147483646(0x7ffffffe, float:NaN)
            if (r11 != r0) goto L_0x066f
            r22 = 1
            goto L_0x0671
        L_0x066f:
            r22 = 0
        L_0x0671:
            org.telegram.tgnet.ConnectionsManager r4 = r50.getConnectionsManager()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$flpU3A3e0tPwUqp389LcbHlF_dc r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$flpU3A3e0tPwUqp389LcbHlF_dc
            r33 = r0
            r23 = r36
            r34 = r1
            r1 = r50
            r36 = r2
            r37 = r29
            r29 = r3
            r2 = r52
            r15 = r4
            r4 = r55
            r38 = 0
            r5 = r16
            r39 = 1
            r6 = r22
            r48 = r7
            r22 = 1
            r7 = r26
            r25 = 0
            r8 = r14
            r39 = r9
            r9 = r37
            r40 = r10
            r10 = r30
            r11 = r36
            r12 = r27
            r13 = r39
            r0.<init>(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r0 = 68
            r2 = r33
            r1 = r39
            r15.sendRequest(r1, r2, r0)
            int r0 = r51.size()
            int r0 = r0 + -1
            r12 = r48
            if (r12 == r0) goto L_0x0731
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            android.util.LongSparseArray r4 = new android.util.LongSparseArray
            r4.<init>()
            r29 = r1
            r1 = r3
            r14 = r4
            r4 = r0
            r3 = r2
            goto L_0x0739
        L_0x06df:
            r23 = r0
            r34 = r1
            r30 = r4
            r35 = r6
            r12 = r7
            r31 = r8
            r40 = r10
            r14 = r27
            r37 = r29
            r22 = 1
            r25 = 0
            r38 = 0
            r29 = r3
            r27 = r24
            r24 = r5
            int r0 = r2.type
            if (r0 != 0) goto L_0x0731
            java.lang.CharSequence r0 = r2.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0731
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0712
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            r5 = r0
            goto L_0x0714
        L_0x0712:
            r5 = r25
        L_0x0714:
            java.lang.CharSequence r0 = r2.messageText
            java.lang.String r1 = r0.toString()
            r4 = 0
            if (r5 == 0) goto L_0x071f
            r6 = 1
            goto L_0x0720
        L_0x071f:
            r6 = 0
        L_0x0720:
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r0.entities
            r8 = 0
            r9 = 0
            r0 = r50
            r2 = r52
            r10 = r54
            r11 = r55
            r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11)
        L_0x0731:
            r3 = r29
            r4 = r30
            r1 = r34
            r29 = r37
        L_0x0739:
            int r7 = r12 + 1
            r15 = r51
            r12 = r52
            r11 = r55
            r0 = r23
            r5 = r24
            r24 = r27
            r8 = r31
            r6 = r35
            r10 = r40
            r27 = r14
            r14 = r50
            goto L_0x00ba
        L_0x0753:
            r2 = r50
            r10 = r28
            goto L_0x0776
        L_0x0758:
            r38 = 0
            r10 = 0
        L_0x075b:
            int r0 = r51.size()
            if (r10 >= r0) goto L_0x0773
            r0 = r51
            java.lang.Object r1 = r0.get(r10)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r2 = r50
            r3 = r52
            r2.processForwardFromMyName(r1, r3)
            int r10 = r10 + 1
            goto L_0x075b
        L_0x0773:
            r2 = r50
            r10 = 0
        L_0x0776:
            return r10
        L_0x0777:
            r2 = r14
            r38 = 0
            return r38
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, int):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x00e5  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ef  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0129  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x01ae  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendMessage$14$SendMessagesHelper(long r26, int r28, boolean r29, boolean r30, boolean r31, android.util.LongSparseArray r32, java.util.ArrayList r33, java.util.ArrayList r34, org.telegram.messenger.MessageObject r35, org.telegram.tgnet.TLRPC$Peer r36, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r37, org.telegram.tgnet.TLObject r38, org.telegram.tgnet.TLRPC$TL_error r39) {
        /*
            r25 = this;
            r11 = r25
            r12 = r28
            r13 = r33
            r14 = r34
            r0 = r39
            r15 = 1
            if (r0 != 0) goto L_0x01e2
            org.telegram.messenger.support.SparseLongArray r9 = new org.telegram.messenger.support.SparseLongArray
            r9.<init>()
            r7 = r38
            org.telegram.tgnet.TLRPC$Updates r7 = (org.telegram.tgnet.TLRPC$Updates) r7
            r0 = 0
        L_0x0017:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x003d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$Update r1 = (org.telegram.tgnet.TLRPC$Update) r1
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateMessageID
            if (r2 == 0) goto L_0x003b
            org.telegram.tgnet.TLRPC$TL_updateMessageID r1 = (org.telegram.tgnet.TLRPC$TL_updateMessageID) r1
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
            if (r0 >= r1) goto L_0x01c4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$Update r1 = (org.telegram.tgnet.TLRPC$Update) r1
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateNewMessage
            if (r2 != 0) goto L_0x0095
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage
            if (r3 != 0) goto L_0x0095
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r3 == 0) goto L_0x008e
            goto L_0x0095
        L_0x008e:
            r14 = r7
            r19 = r9
            r1 = 1
            r13 = 0
            goto L_0x01b5
        L_0x0095:
            if (r12 == 0) goto L_0x0099
            r3 = 1
            goto L_0x009a
        L_0x0099:
            r3 = 0
        L_0x009a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r4 = r7.updates
            r4.remove(r0)
            int r17 = r0 + -1
            r0 = -1
            if (r2 == 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewMessage) r1
            org.telegram.tgnet.TLRPC$Message r2 = r1.message
            org.telegram.messenger.MessagesController r4 = r25.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            r4.processNewDifferenceParams(r0, r10, r0, r1)
        L_0x00b3:
            r10 = r2
            goto L_0x00dc
        L_0x00b5:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x00bf
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage) r1
            org.telegram.tgnet.TLRPC$Message r1 = r1.message
            r10 = r1
            goto L_0x00dc
        L_0x00bf:
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage) r1
            org.telegram.tgnet.TLRPC$Message r2 = r1.message
            org.telegram.messenger.MessagesController r4 = r25.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            org.telegram.tgnet.TLRPC$Peer r0 = r2.to_id
            int r0 = r0.channel_id
            r4.processNewChannelDifferenceParams(r10, r1, r0)
            if (r29 == 0) goto L_0x00b3
            int r0 = r2.flags
            r1 = -2147483648(0xfffffffvar_, float:-0.0)
            r0 = r0 | r1
            r2.flags = r0
            goto L_0x00b3
        L_0x00dc:
            if (r30 == 0) goto L_0x00e8
            int r0 = r10.date
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 == r1) goto L_0x00e8
            r19 = 0
            goto L_0x00ea
        L_0x00e8:
            r19 = r3
        L_0x00ea:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r10)
            if (r19 != 0) goto L_0x00fc
            int r0 = r16.intValue()
            int r1 = r10.id
            if (r0 >= r1) goto L_0x00f9
            r0 = 1
            goto L_0x00fa
        L_0x00f9:
            r0 = 0
        L_0x00fa:
            r10.unread = r0
        L_0x00fc:
            if (r31 == 0) goto L_0x0106
            r10.out = r15
            r4 = 0
            r10.unread = r4
            r10.media_unread = r4
            goto L_0x0107
        L_0x0106:
            r4 = 0
        L_0x0107:
            int r0 = r10.id
            long r0 = r9.get(r0)
            r2 = 0
            int r18 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r18 == 0) goto L_0x01ae
            r3 = r32
            java.lang.Object r0 = r3.get(r0)
            r2 = r0
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC$Message) r2
            if (r2 != 0) goto L_0x0120
            goto L_0x01ae
        L_0x0120:
            int r0 = r13.indexOf(r2)
            r1 = -1
            if (r0 != r1) goto L_0x0129
            goto L_0x01ae
        L_0x0129:
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
            if (r12 == 0) goto L_0x0182
            if (r19 != 0) goto L_0x0182
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
            goto L_0x01a9
        L_0x0182:
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
        L_0x01a9:
            r0 = r17
            r8 = r20
            goto L_0x01b4
        L_0x01ae:
            r14 = r7
            r19 = r9
            r13 = 0
            r0 = r17
        L_0x01b4:
            r1 = 1
        L_0x01b5:
            int r0 = r0 + r1
            r5 = r26
            r12 = r28
            r13 = r33
            r7 = r14
            r9 = r19
            r15 = 1
            r14 = r34
            goto L_0x0071
        L_0x01c4:
            r14 = r7
            r1 = 1
            r13 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r0 = r14.updates
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01d6
            org.telegram.messenger.MessagesController r0 = r25.getMessagesController()
            r0.processUpdates(r14, r13)
        L_0x01d6:
            org.telegram.messenger.StatsController r0 = r25.getStatsController()
            int r2 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r0.incrementSentItemsCount(r2, r1, r8)
            goto L_0x01ee
        L_0x01e2:
            r1 = 1
            r13 = 0
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$Np5Q7xHh1zw91IDVJqdT9IWuXcI r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Np5Q7xHh1zw91IDVJqdT9IWuXcI
            r3 = r37
            r2.<init>(r0, r3)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x01ee:
            r10 = 0
        L_0x01ef:
            int r0 = r33.size()
            if (r10 >= r0) goto L_0x0218
            r0 = r33
            r2 = 0
            java.lang.Object r3 = r0.get(r10)
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            org.telegram.messenger.MessagesStorage r4 = r25.getMessagesStorage()
            r5 = r28
            if (r5 == 0) goto L_0x0208
            r6 = 1
            goto L_0x0209
        L_0x0208:
            r6 = 0
        L_0x0209:
            r4.markMessageAsSendError(r3, r6)
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$9-4ORe2MVG9HpTDZt06-uF5tbLE r4 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$9-4ORe2MVG9HpTDZt06-uF5tbLE
            r4.<init>(r3, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            int r10 = r10 + 1
            r13 = 0
            goto L_0x01ef
        L_0x0218:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendMessage$14$SendMessagesHelper(long, int, boolean, boolean, boolean, android.util.LongSparseArray, java.util.ArrayList, java.util.ArrayList, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Peer, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$null$9$SendMessagesHelper(int i, TLRPC$Message tLRPC$Message, ArrayList arrayList, MessageObject messageObject, int i2) {
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(Integer.valueOf(i));
        getMessagesController().deleteMessages(arrayList2, (ArrayList<Long>) null, (TLRPC$EncryptedChat) null, tLRPC$Message.dialog_id, tLRPC$Message.to_id.channel_id, false, true);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList, messageObject, tLRPC$Message, i, i2) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ MessageObject f$2;
            private final /* synthetic */ TLRPC$Message f$3;
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

    public /* synthetic */ void lambda$null$8$SendMessagesHelper(ArrayList arrayList, MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, int i2) {
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable(messageObject, tLRPC$Message, i, i2) {
            private final /* synthetic */ MessageObject f$1;
            private final /* synthetic */ TLRPC$Message f$2;
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

    public /* synthetic */ void lambda$null$7$SendMessagesHelper(MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, int i2) {
        ArrayList arrayList = new ArrayList();
        boolean z = true;
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true));
        getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        processSentMessage(i);
        if (i2 == 0) {
            z = false;
        }
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$11$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, TLRPC$Peer tLRPC$Peer, int i2, ArrayList arrayList, long j, TLRPC$Message tLRPC$Message2, int i3) {
        TLRPC$Message tLRPC$Message3 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message3.random_id, Integer.valueOf(i), tLRPC$Message3.id, 0, false, tLRPC$Peer.channel_id, i2 != 0 ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, i2 != 0);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, j, i, tLRPC$Message2, i3, i2) {
            private final /* synthetic */ TLRPC$Message f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ TLRPC$Message f$4;
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

    public /* synthetic */ void lambda$null$10$SendMessagesHelper(TLRPC$Message tLRPC$Message, long j, int i, TLRPC$Message tLRPC$Message2, int i2, int i3) {
        boolean z = false;
        tLRPC$Message.send_state = 0;
        getMediaDataController().increasePeerRaiting(j);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i4 = NotificationCenter.messageReceivedByServer;
        Object[] objArr = new Object[7];
        objArr[0] = Integer.valueOf(i);
        objArr[1] = Integer.valueOf(tLRPC$Message2.id);
        objArr[2] = tLRPC$Message2;
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

    public /* synthetic */ void lambda$null$12$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_forwardMessages tLRPC$TL_messages_forwardMessages) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, (BaseFragment) null, tLRPC$TL_messages_forwardMessages, new Object[0]);
    }

    public /* synthetic */ void lambda$null$13$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i) {
        tLRPC$Message.send_state = 2;
        boolean z = true;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message.id));
        processSentMessage(tLRPC$Message.id);
        int i2 = tLRPC$Message.id;
        if (i == 0) {
            z = false;
        }
        removeFromSendingMessages(i2, z);
    }

    private void writePreviousMessageData(TLRPC$Message tLRPC$Message, SerializedData serializedData) {
        tLRPC$Message.media.serializeToStream(serializedData);
        String str = tLRPC$Message.message;
        String str2 = "";
        if (str == null) {
            str = str2;
        }
        serializedData.writeString(str);
        String str3 = tLRPC$Message.attachPath;
        if (str3 != null) {
            str2 = str3;
        }
        serializedData.writeString(str2);
        int size = tLRPC$Message.entities.size();
        serializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            tLRPC$Message.entities.get(i).serializeToStream(serializedData);
        }
    }

    /* JADX WARNING: type inference failed for: r35v0, types: [java.lang.Object] */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x021d A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02c9 A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0041 A[SYNTHETIC, Splitter:B:16:0x0041] */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x03da A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x03ea A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0431 A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0436 A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x044c A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008d A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x012e A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x014b A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0175 A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01f6 A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0202 A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x020a A[Catch:{ Exception -> 0x04d7 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void editMessageMedia(org.telegram.messenger.MessageObject r28, org.telegram.tgnet.TLRPC$TL_photo r29, org.telegram.messenger.VideoEditedInfo r30, org.telegram.tgnet.TLRPC$TL_document r31, java.lang.String r32, java.util.HashMap<java.lang.String, java.lang.String> r33, boolean r34, java.lang.Object r35) {
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
            long r7 = r28.getDialogId()     // Catch:{ Exception -> 0x04d7 }
            int r9 = (int) r7     // Catch:{ Exception -> 0x04d7 }
            if (r9 != 0) goto L_0x003c
            r13 = 32
            long r13 = r7 >> r13
            int r14 = (int) r13     // Catch:{ Exception -> 0x04d7 }
            org.telegram.messenger.MessagesController r13 = r27.getMessagesController()     // Catch:{ Exception -> 0x04d7 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$EncryptedChat r13 = r13.getEncryptedChat(r14)     // Catch:{ Exception -> 0x04d7 }
            if (r13 == 0) goto L_0x003a
            int r13 = r13.layer     // Catch:{ Exception -> 0x04d7 }
            int r13 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r13)     // Catch:{ Exception -> 0x04d7 }
            r14 = 101(0x65, float:1.42E-43)
            if (r13 >= r14) goto L_0x003c
        L_0x003a:
            r13 = 0
            goto L_0x003d
        L_0x003c:
            r13 = 1
        L_0x003d:
            java.lang.String r14 = "http"
            if (r34 == 0) goto L_0x008d
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media     // Catch:{ Exception -> 0x04d7 }
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x04d7 }
            if (r2 == 0) goto L_0x0055
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0     // Catch:{ Exception -> 0x04d7 }
            r15 = r30
            r2 = 2
            goto L_0x006b
        L_0x0055:
            org.telegram.tgnet.TLRPC$Message r1 = r11.messageOwner     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$Document r1 = r1.document     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC$TL_document) r1     // Catch:{ Exception -> 0x04d7 }
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1)     // Catch:{ Exception -> 0x04d7 }
            if (r2 != 0) goto L_0x0068
            if (r30 == 0) goto L_0x0066
            goto L_0x0068
        L_0x0066:
            r2 = 7
            goto L_0x0069
        L_0x0068:
            r2 = 3
        L_0x0069:
            org.telegram.messenger.VideoEditedInfo r15 = r11.videoEditedInfo     // Catch:{ Exception -> 0x04d7 }
        L_0x006b:
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r5.params     // Catch:{ Exception -> 0x04d7 }
            if (r35 != 0) goto L_0x007c
            if (r6 == 0) goto L_0x007c
            boolean r18 = r6.containsKey(r4)     // Catch:{ Exception -> 0x04d7 }
            if (r18 == 0) goto L_0x007c
            java.lang.Object r4 = r6.get(r4)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x007e
        L_0x007c:
            r4 = r35
        L_0x007e:
            java.lang.String r12 = r5.message     // Catch:{ Exception -> 0x04d7 }
            r11.editingMessage = r12     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r12 = r5.entities     // Catch:{ Exception -> 0x04d7 }
            r11.editingMessageEntities = r12     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r12 = r5.attachPath     // Catch:{ Exception -> 0x04d7 }
            r19 = r9
            r9 = r4
            goto L_0x0147
        L_0x008d:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x04d7 }
            r11.previousMedia = r4     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r4 = r5.message     // Catch:{ Exception -> 0x04d7 }
            r11.previousCaption = r4     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r5.entities     // Catch:{ Exception -> 0x04d7 }
            r11.previousCaptionEntities = r4     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r4 = r5.attachPath     // Catch:{ Exception -> 0x04d7 }
            r11.previousAttachPath = r4     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x04d7 }
            r6 = 1
            r4.<init>((boolean) r6)     // Catch:{ Exception -> 0x04d7 }
            r10.writePreviousMessageData(r5, r4)     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.SerializedData r6 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x04d7 }
            int r4 = r4.length()     // Catch:{ Exception -> 0x04d7 }
            r6.<init>((int) r4)     // Catch:{ Exception -> 0x04d7 }
            r10.writePreviousMessageData(r5, r6)     // Catch:{ Exception -> 0x04d7 }
            if (r33 != 0) goto L_0x00ba
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ Exception -> 0x04d7 }
            r4.<init>()     // Catch:{ Exception -> 0x04d7 }
            goto L_0x00bc
        L_0x00ba:
            r4 = r33
        L_0x00bc:
            java.lang.String r12 = "prevMedia"
            byte[] r15 = r6.toByteArray()     // Catch:{ Exception -> 0x04d7 }
            r19 = r9
            r9 = 0
            java.lang.String r15 = android.util.Base64.encodeToString(r15, r9)     // Catch:{ Exception -> 0x04d7 }
            r4.put(r12, r15)     // Catch:{ Exception -> 0x04d7 }
            r6.cleanup()     // Catch:{ Exception -> 0x04d7 }
            if (r0 == 0) goto L_0x010f
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x04d7 }
            r6.<init>()     // Catch:{ Exception -> 0x04d7 }
            r5.media = r6     // Catch:{ Exception -> 0x04d7 }
            int r9 = r6.flags     // Catch:{ Exception -> 0x04d7 }
            r12 = 3
            r9 = r9 | r12
            r6.flags = r9     // Catch:{ Exception -> 0x04d7 }
            r6.photo = r0     // Catch:{ Exception -> 0x04d7 }
            if (r2 == 0) goto L_0x00f1
            int r6 = r32.length()     // Catch:{ Exception -> 0x04d7 }
            if (r6 <= 0) goto L_0x00f1
            boolean r6 = r2.startsWith(r14)     // Catch:{ Exception -> 0x04d7 }
            if (r6 == 0) goto L_0x00f1
            r5.attachPath = r2     // Catch:{ Exception -> 0x04d7 }
            goto L_0x010d
        L_0x00f1:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r0.sizes     // Catch:{ Exception -> 0x04d7 }
            int r9 = r9.size()     // Catch:{ Exception -> 0x04d7 }
            r12 = 1
            int r9 = r9 - r12
            java.lang.Object r6 = r6.get(r9)     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC$PhotoSize) r6     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.location     // Catch:{ Exception -> 0x04d7 }
            java.io.File r6 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r12)     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x04d7 }
            r5.attachPath = r6     // Catch:{ Exception -> 0x04d7 }
        L_0x010d:
            r6 = 2
            goto L_0x013b
        L_0x010f:
            if (r1 == 0) goto L_0x013a
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x04d7 }
            r6.<init>()     // Catch:{ Exception -> 0x04d7 }
            r5.media = r6     // Catch:{ Exception -> 0x04d7 }
            int r9 = r6.flags     // Catch:{ Exception -> 0x04d7 }
            r12 = 3
            r9 = r9 | r12
            r6.flags = r9     // Catch:{ Exception -> 0x04d7 }
            r6.document = r1     // Catch:{ Exception -> 0x04d7 }
            boolean r6 = org.telegram.messenger.MessageObject.isVideoDocument(r31)     // Catch:{ Exception -> 0x04d7 }
            if (r6 != 0) goto L_0x012b
            if (r30 == 0) goto L_0x0129
            goto L_0x012b
        L_0x0129:
            r6 = 7
            goto L_0x012c
        L_0x012b:
            r6 = 3
        L_0x012c:
            if (r30 == 0) goto L_0x0137
            java.lang.String r9 = r30.getString()     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r12 = "ve"
            r4.put(r12, r9)     // Catch:{ Exception -> 0x04d7 }
        L_0x0137:
            r5.attachPath = r2     // Catch:{ Exception -> 0x04d7 }
            goto L_0x013b
        L_0x013a:
            r6 = -1
        L_0x013b:
            r5.params = r4     // Catch:{ Exception -> 0x04d7 }
            r9 = 3
            r5.send_state = r9     // Catch:{ Exception -> 0x04d7 }
            r15 = r30
            r9 = r35
            r12 = r2
            r2 = r6
            r6 = r4
        L_0x0147:
            java.lang.String r4 = r5.attachPath     // Catch:{ Exception -> 0x04d7 }
            if (r4 != 0) goto L_0x014f
            java.lang.String r4 = ""
            r5.attachPath = r4     // Catch:{ Exception -> 0x04d7 }
        L_0x014f:
            r4 = 0
            r5.local_id = r4     // Catch:{ Exception -> 0x04d7 }
            int r4 = r11.type     // Catch:{ Exception -> 0x04d7 }
            r29 = r1
            r1 = 3
            if (r4 == r1) goto L_0x0160
            if (r15 != 0) goto L_0x0160
            int r1 = r11.type     // Catch:{ Exception -> 0x04d7 }
            r4 = 2
            if (r1 != r4) goto L_0x016b
        L_0x0160:
            java.lang.String r1 = r5.attachPath     // Catch:{ Exception -> 0x04d7 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x04d7 }
            if (r1 != 0) goto L_0x016b
            r1 = 1
            r11.attachPathExists = r1     // Catch:{ Exception -> 0x04d7 }
        L_0x016b:
            org.telegram.messenger.VideoEditedInfo r1 = r11.videoEditedInfo     // Catch:{ Exception -> 0x04d7 }
            if (r1 == 0) goto L_0x0173
            if (r15 != 0) goto L_0x0173
            org.telegram.messenger.VideoEditedInfo r15 = r11.videoEditedInfo     // Catch:{ Exception -> 0x04d7 }
        L_0x0173:
            if (r34 != 0) goto L_0x01f6
            java.lang.CharSequence r4 = r11.editingMessage     // Catch:{ Exception -> 0x04d7 }
            if (r4 == 0) goto L_0x01ac
            java.lang.CharSequence r4 = r11.editingMessage     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04d7 }
            r5.message = r4     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r11.editingMessageEntities     // Catch:{ Exception -> 0x04d7 }
            if (r4 == 0) goto L_0x018b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r11.editingMessageEntities     // Catch:{ Exception -> 0x04d7 }
            r5.entities = r4     // Catch:{ Exception -> 0x04d7 }
        L_0x0189:
            r1 = 0
            goto L_0x01a7
        L_0x018b:
            r4 = 1
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r4]     // Catch:{ Exception -> 0x04d7 }
            java.lang.CharSequence r4 = r11.editingMessage     // Catch:{ Exception -> 0x04d7 }
            r17 = 0
            r1[r17] = r4     // Catch:{ Exception -> 0x04d7 }
            org.telegram.messenger.MediaDataController r4 = r27.getMediaDataController()     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList r1 = r4.getEntities(r1, r13)     // Catch:{ Exception -> 0x04d7 }
            if (r1 == 0) goto L_0x0189
            boolean r4 = r1.isEmpty()     // Catch:{ Exception -> 0x04d7 }
            if (r4 != 0) goto L_0x0189
            r5.entities = r1     // Catch:{ Exception -> 0x04d7 }
            goto L_0x0189
        L_0x01a7:
            r11.caption = r1     // Catch:{ Exception -> 0x04d7 }
            r28.generateCaption()     // Catch:{ Exception -> 0x04d7 }
        L_0x01ac:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x04d7 }
            r1.<init>()     // Catch:{ Exception -> 0x04d7 }
            r1.add(r5)     // Catch:{ Exception -> 0x04d7 }
            org.telegram.messenger.MessagesStorage r20 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x04d7 }
            r22 = 0
            r23 = 1
            r24 = 0
            r25 = 0
            boolean r4 = r11.scheduled     // Catch:{ Exception -> 0x04d7 }
            r21 = r1
            r26 = r4
            r20.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r21, (boolean) r22, (boolean) r23, (boolean) r24, (int) r25, (boolean) r26)     // Catch:{ Exception -> 0x04d7 }
            r1 = -1
            r11.type = r1     // Catch:{ Exception -> 0x04d7 }
            r28.setType()     // Catch:{ Exception -> 0x04d7 }
            r28.createMessageSendInfo()     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x04d7 }
            r1.<init>()     // Catch:{ Exception -> 0x04d7 }
            r1.add(r11)     // Catch:{ Exception -> 0x04d7 }
            org.telegram.messenger.NotificationCenter r4 = r27.getNotificationCenter()     // Catch:{ Exception -> 0x04d7 }
            int r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects     // Catch:{ Exception -> 0x04d7 }
            r16 = r13
            r31 = r15
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x04d7 }
            java.lang.Long r13 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x04d7 }
            r17 = 0
            r15[r17] = r13     // Catch:{ Exception -> 0x04d7 }
            r13 = 1
            r15[r13] = r1     // Catch:{ Exception -> 0x04d7 }
            r4.postNotificationName(r5, r15)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x01fa
        L_0x01f6:
            r16 = r13
            r31 = r15
        L_0x01fa:
            if (r6 == 0) goto L_0x020a
            boolean r1 = r6.containsKey(r3)     // Catch:{ Exception -> 0x04d7 }
            if (r1 == 0) goto L_0x020a
            java.lang.Object r1 = r6.get(r3)     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x04d7 }
            r4 = r1
            goto L_0x020b
        L_0x020a:
            r4 = 0
        L_0x020b:
            r1 = 8
            r3 = 1
            if (r2 < r3) goto L_0x0213
            r3 = 3
            if (r2 <= r3) goto L_0x0218
        L_0x0213:
            r3 = 5
            if (r2 < r3) goto L_0x04de
            if (r2 > r1) goto L_0x04de
        L_0x0218:
            r20 = 0
            r3 = 2
            if (r2 != r3) goto L_0x02c9
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x04d7 }
            r3.<init>()     // Catch:{ Exception -> 0x04d7 }
            if (r6 == 0) goto L_0x025f
            java.lang.String r5 = "masks"
            java.lang.Object r5 = r6.get(r5)     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x04d7 }
            if (r5 == 0) goto L_0x025f
            org.telegram.tgnet.SerializedData r6 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x04d7 }
            byte[] r5 = org.telegram.messenger.Utilities.hexToBytes(r5)     // Catch:{ Exception -> 0x04d7 }
            r6.<init>((byte[]) r5)     // Catch:{ Exception -> 0x04d7 }
            r5 = 0
            int r13 = r6.readInt32(r5)     // Catch:{ Exception -> 0x04d7 }
            r15 = 0
        L_0x023d:
            if (r15 >= r13) goto L_0x0256
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r3.stickers     // Catch:{ Exception -> 0x04d7 }
            r29 = r13
            int r13 = r6.readInt32(r5)     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$InputDocument r13 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r6, r13, r5)     // Catch:{ Exception -> 0x04d7 }
            r1.add(r13)     // Catch:{ Exception -> 0x04d7 }
            int r15 = r15 + 1
            r13 = r29
            r1 = 8
            r5 = 0
            goto L_0x023d
        L_0x0256:
            int r1 = r3.flags     // Catch:{ Exception -> 0x04d7 }
            r5 = 1
            r1 = r1 | r5
            r3.flags = r1     // Catch:{ Exception -> 0x04d7 }
            r6.cleanup()     // Catch:{ Exception -> 0x04d7 }
        L_0x025f:
            long r5 = r0.access_hash     // Catch:{ Exception -> 0x04d7 }
            int r1 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1))
            if (r1 != 0) goto L_0x0269
            r6 = r2
            r1 = r3
            r2 = 1
            goto L_0x028e
        L_0x0269:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x04d7 }
            r1.<init>()     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r5 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x04d7 }
            r5.<init>()     // Catch:{ Exception -> 0x04d7 }
            r1.id = r5     // Catch:{ Exception -> 0x04d7 }
            r29 = r1
            r6 = r2
            long r1 = r0.id     // Catch:{ Exception -> 0x04d7 }
            r5.id = r1     // Catch:{ Exception -> 0x04d7 }
            long r1 = r0.access_hash     // Catch:{ Exception -> 0x04d7 }
            r5.access_hash = r1     // Catch:{ Exception -> 0x04d7 }
            byte[] r1 = r0.file_reference     // Catch:{ Exception -> 0x04d7 }
            r5.file_reference = r1     // Catch:{ Exception -> 0x04d7 }
            if (r1 != 0) goto L_0x028b
            r1 = 0
            byte[] r2 = new byte[r1]     // Catch:{ Exception -> 0x04d7 }
            r5.file_reference = r2     // Catch:{ Exception -> 0x04d7 }
        L_0x028b:
            r1 = r29
            r2 = 0
        L_0x028e:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r5 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x04d7 }
            r5.<init>(r7)     // Catch:{ Exception -> 0x04d7 }
            r7 = 0
            r5.type = r7     // Catch:{ Exception -> 0x04d7 }
            r5.obj = r11     // Catch:{ Exception -> 0x04d7 }
            r5.originalPath = r4     // Catch:{ Exception -> 0x04d7 }
            r5.parentObject = r9     // Catch:{ Exception -> 0x04d7 }
            r5.inputUploadMedia = r3     // Catch:{ Exception -> 0x04d7 }
            r5.performMediaUpload = r2     // Catch:{ Exception -> 0x04d7 }
            if (r12 == 0) goto L_0x02b1
            int r3 = r12.length()     // Catch:{ Exception -> 0x04d7 }
            if (r3 <= 0) goto L_0x02b1
            boolean r3 = r12.startsWith(r14)     // Catch:{ Exception -> 0x04d7 }
            if (r3 == 0) goto L_0x02b1
            r5.httpLocation = r12     // Catch:{ Exception -> 0x04d7 }
            goto L_0x02c5
        L_0x02b1:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r0.sizes     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r0.sizes     // Catch:{ Exception -> 0x04d7 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x04d7 }
            r8 = 1
            int r7 = r7 - r8
            java.lang.Object r3 = r3.get(r7)     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x04d7 }
            r5.photoSize = r3     // Catch:{ Exception -> 0x04d7 }
            r5.locationParent = r0     // Catch:{ Exception -> 0x04d7 }
        L_0x02c5:
            r3 = r2
        L_0x02c6:
            r0 = r5
            goto L_0x03b7
        L_0x02c9:
            r6 = r2
            r0 = 3
            if (r6 != r0) goto L_0x034f
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x04d7 }
            r0.<init>()     // Catch:{ Exception -> 0x04d7 }
            r1 = r29
            java.lang.String r2 = r1.mime_type     // Catch:{ Exception -> 0x04d7 }
            r0.mime_type = r2     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r1.attributes     // Catch:{ Exception -> 0x04d7 }
            r0.attributes = r2     // Catch:{ Exception -> 0x04d7 }
            boolean r2 = r28.isGif()     // Catch:{ Exception -> 0x04d7 }
            if (r2 != 0) goto L_0x02fa
            if (r31 == 0) goto L_0x02eb
            r15 = r31
            boolean r2 = r15.muted     // Catch:{ Exception -> 0x04d7 }
            if (r2 != 0) goto L_0x02fc
            goto L_0x02ed
        L_0x02eb:
            r15 = r31
        L_0x02ed:
            r2 = 1
            r0.nosound_video = r2     // Catch:{ Exception -> 0x04d7 }
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x04d7 }
            if (r2 == 0) goto L_0x02fc
            java.lang.String r2 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x02fc
        L_0x02fa:
            r15 = r31
        L_0x02fc:
            long r2 = r1.access_hash     // Catch:{ Exception -> 0x04d7 }
            int r5 = (r2 > r20 ? 1 : (r2 == r20 ? 0 : -1))
            if (r5 != 0) goto L_0x0305
            r2 = r0
            r3 = 1
            goto L_0x0325
        L_0x0305:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x04d7 }
            r2.<init>()     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x04d7 }
            r3.<init>()     // Catch:{ Exception -> 0x04d7 }
            r2.id = r3     // Catch:{ Exception -> 0x04d7 }
            long r12 = r1.id     // Catch:{ Exception -> 0x04d7 }
            r3.id = r12     // Catch:{ Exception -> 0x04d7 }
            long r12 = r1.access_hash     // Catch:{ Exception -> 0x04d7 }
            r3.access_hash = r12     // Catch:{ Exception -> 0x04d7 }
            byte[] r5 = r1.file_reference     // Catch:{ Exception -> 0x04d7 }
            r3.file_reference = r5     // Catch:{ Exception -> 0x04d7 }
            if (r5 != 0) goto L_0x0324
            r5 = 0
            byte[] r12 = new byte[r5]     // Catch:{ Exception -> 0x04d7 }
            r3.file_reference = r12     // Catch:{ Exception -> 0x04d7 }
        L_0x0324:
            r3 = 0
        L_0x0325:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r5 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x04d7 }
            r5.<init>(r7)     // Catch:{ Exception -> 0x04d7 }
            r7 = 1
            r5.type = r7     // Catch:{ Exception -> 0x04d7 }
            r5.obj = r11     // Catch:{ Exception -> 0x04d7 }
            r5.originalPath = r4     // Catch:{ Exception -> 0x04d7 }
            r5.parentObject = r9     // Catch:{ Exception -> 0x04d7 }
            r5.inputUploadMedia = r0     // Catch:{ Exception -> 0x04d7 }
            r5.performMediaUpload = r3     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x04d7 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x04d7 }
            if (r0 != 0) goto L_0x034c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x04d7 }
            r7 = 0
            java.lang.Object r0 = r0.get(r7)     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0     // Catch:{ Exception -> 0x04d7 }
            r5.photoSize = r0     // Catch:{ Exception -> 0x04d7 }
            r5.locationParent = r1     // Catch:{ Exception -> 0x04d7 }
        L_0x034c:
            r5.videoEditedInfo = r15     // Catch:{ Exception -> 0x04d7 }
            goto L_0x03b1
        L_0x034f:
            r1 = r29
            r0 = 7
            if (r6 != r0) goto L_0x03b4
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x04d7 }
            r0.<init>()     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r2 = r1.mime_type     // Catch:{ Exception -> 0x04d7 }
            r0.mime_type = r2     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r1.attributes     // Catch:{ Exception -> 0x04d7 }
            r0.attributes = r2     // Catch:{ Exception -> 0x04d7 }
            long r2 = r1.access_hash     // Catch:{ Exception -> 0x04d7 }
            int r5 = (r2 > r20 ? 1 : (r2 == r20 ? 0 : -1))
            if (r5 != 0) goto L_0x036a
            r2 = r0
            r3 = 1
            goto L_0x038a
        L_0x036a:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x04d7 }
            r2.<init>()     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x04d7 }
            r3.<init>()     // Catch:{ Exception -> 0x04d7 }
            r2.id = r3     // Catch:{ Exception -> 0x04d7 }
            long r12 = r1.id     // Catch:{ Exception -> 0x04d7 }
            r3.id = r12     // Catch:{ Exception -> 0x04d7 }
            long r12 = r1.access_hash     // Catch:{ Exception -> 0x04d7 }
            r3.access_hash = r12     // Catch:{ Exception -> 0x04d7 }
            byte[] r5 = r1.file_reference     // Catch:{ Exception -> 0x04d7 }
            r3.file_reference = r5     // Catch:{ Exception -> 0x04d7 }
            if (r5 != 0) goto L_0x0389
            r5 = 0
            byte[] r12 = new byte[r5]     // Catch:{ Exception -> 0x04d7 }
            r3.file_reference = r12     // Catch:{ Exception -> 0x04d7 }
        L_0x0389:
            r3 = 0
        L_0x038a:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r5 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x04d7 }
            r5.<init>(r7)     // Catch:{ Exception -> 0x04d7 }
            r5.originalPath = r4     // Catch:{ Exception -> 0x04d7 }
            r7 = 2
            r5.type = r7     // Catch:{ Exception -> 0x04d7 }
            r5.obj = r11     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x04d7 }
            boolean r7 = r7.isEmpty()     // Catch:{ Exception -> 0x04d7 }
            if (r7 != 0) goto L_0x03ab
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x04d7 }
            r8 = 0
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x04d7 }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC$PhotoSize) r7     // Catch:{ Exception -> 0x04d7 }
            r5.photoSize = r7     // Catch:{ Exception -> 0x04d7 }
            r5.locationParent = r1     // Catch:{ Exception -> 0x04d7 }
        L_0x03ab:
            r5.parentObject = r9     // Catch:{ Exception -> 0x04d7 }
            r5.inputUploadMedia = r0     // Catch:{ Exception -> 0x04d7 }
            r5.performMediaUpload = r3     // Catch:{ Exception -> 0x04d7 }
        L_0x03b1:
            r1 = r2
            goto L_0x02c6
        L_0x03b4:
            r0 = 0
            r1 = 0
            r3 = 0
        L_0x03b7:
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r2 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage     // Catch:{ Exception -> 0x04d7 }
            r2.<init>()     // Catch:{ Exception -> 0x04d7 }
            int r5 = r28.getId()     // Catch:{ Exception -> 0x04d7 }
            r2.id = r5     // Catch:{ Exception -> 0x04d7 }
            org.telegram.messenger.MessagesController r5 = r27.getMessagesController()     // Catch:{ Exception -> 0x04d7 }
            r7 = r19
            org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer(r7)     // Catch:{ Exception -> 0x04d7 }
            r2.peer = r5     // Catch:{ Exception -> 0x04d7 }
            int r5 = r2.flags     // Catch:{ Exception -> 0x04d7 }
            r5 = r5 | 16384(0x4000, float:2.2959E-41)
            r2.flags = r5     // Catch:{ Exception -> 0x04d7 }
            r2.media = r1     // Catch:{ Exception -> 0x04d7 }
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x04d7 }
            if (r1 == 0) goto L_0x03e6
            org.telegram.tgnet.TLRPC$Message r1 = r11.messageOwner     // Catch:{ Exception -> 0x04d7 }
            int r1 = r1.date     // Catch:{ Exception -> 0x04d7 }
            r2.schedule_date = r1     // Catch:{ Exception -> 0x04d7 }
            r1 = 32768(0x8000, float:4.5918E-41)
            r1 = r1 | r5
            r2.flags = r1     // Catch:{ Exception -> 0x04d7 }
        L_0x03e6:
            java.lang.CharSequence r1 = r11.editingMessage     // Catch:{ Exception -> 0x04d7 }
            if (r1 == 0) goto L_0x042f
            java.lang.CharSequence r1 = r11.editingMessage     // Catch:{ Exception -> 0x04d7 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x04d7 }
            r2.message = r1     // Catch:{ Exception -> 0x04d7 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x04d7 }
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r2.flags = r1     // Catch:{ Exception -> 0x04d7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r11.editingMessageEntities     // Catch:{ Exception -> 0x04d7 }
            if (r5 == 0) goto L_0x0407
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r11.editingMessageEntities     // Catch:{ Exception -> 0x04d7 }
            r2.entities = r5     // Catch:{ Exception -> 0x04d7 }
            r5 = 8
            r1 = r1 | r5
            r2.flags = r1     // Catch:{ Exception -> 0x04d7 }
        L_0x0405:
            r1 = 0
            goto L_0x042b
        L_0x0407:
            r1 = 1
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r1]     // Catch:{ Exception -> 0x04d7 }
            java.lang.CharSequence r1 = r11.editingMessage     // Catch:{ Exception -> 0x04d7 }
            r7 = 0
            r5[r7] = r1     // Catch:{ Exception -> 0x04d7 }
            org.telegram.messenger.MediaDataController r1 = r27.getMediaDataController()     // Catch:{ Exception -> 0x04d7 }
            r12 = r16
            java.util.ArrayList r1 = r1.getEntities(r5, r12)     // Catch:{ Exception -> 0x04d7 }
            if (r1 == 0) goto L_0x0405
            boolean r5 = r1.isEmpty()     // Catch:{ Exception -> 0x04d7 }
            if (r5 != 0) goto L_0x0405
            r2.entities = r1     // Catch:{ Exception -> 0x04d7 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x04d7 }
            r5 = 8
            r1 = r1 | r5
            r2.flags = r1     // Catch:{ Exception -> 0x04d7 }
            goto L_0x0405
        L_0x042b:
            r11.editingMessage = r1     // Catch:{ Exception -> 0x04d7 }
            r11.editingMessageEntities = r1     // Catch:{ Exception -> 0x04d7 }
        L_0x042f:
            if (r0 == 0) goto L_0x0433
            r0.sendRequest = r2     // Catch:{ Exception -> 0x04d7 }
        L_0x0433:
            r1 = 1
            if (r6 != r1) goto L_0x044c
            r1 = 0
            boolean r3 = r11.scheduled     // Catch:{ Exception -> 0x04d7 }
            r29 = r27
            r30 = r2
            r31 = r28
            r32 = r1
            r33 = r0
            r34 = r9
            r35 = r3
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x044c:
            r1 = 2
            if (r6 != r1) goto L_0x0466
            if (r3 == 0) goto L_0x0456
            r10.performSendDelayedMessage(r0)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x0456:
            r5 = 0
            r6 = 1
            boolean r12 = r11.scheduled     // Catch:{ Exception -> 0x04d7 }
            r1 = r27
            r3 = r28
            r7 = r0
            r8 = r9
            r9 = r12
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x0466:
            r1 = 3
            if (r6 != r1) goto L_0x0485
            if (r3 == 0) goto L_0x0470
            r10.performSendDelayedMessage(r0)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x0470:
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x04d7 }
            r29 = r27
            r30 = r2
            r31 = r28
            r32 = r4
            r33 = r0
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x0485:
            r1 = 6
            if (r6 != r1) goto L_0x049c
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x04d7 }
            r29 = r27
            r30 = r2
            r31 = r28
            r32 = r4
            r33 = r0
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x049c:
            r1 = 7
            if (r6 != r1) goto L_0x04b9
            if (r3 == 0) goto L_0x04a5
            r10.performSendDelayedMessage(r0)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x04a5:
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x04d7 }
            r29 = r27
            r30 = r2
            r31 = r28
            r32 = r4
            r33 = r0
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x04b9:
            r1 = 8
            if (r6 != r1) goto L_0x04de
            if (r3 == 0) goto L_0x04c3
            r10.performSendDelayedMessage(r0)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x04c3:
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x04d7 }
            r29 = r27
            r30 = r2
            r31 = r28
            r32 = r4
            r33 = r0
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x04d7 }
            goto L_0x04de
        L_0x04d7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r27.revertEditingMessageObject(r28)
        L_0x04de:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessageMedia(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, BaseFragment baseFragment, ArrayList<TLRPC$MessageEntity> arrayList, int i, Runnable runnable) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return 0;
        }
        TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage = new TLRPC$TL_messages_editMessage();
        tLRPC$TL_messages_editMessage.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        if (str != null) {
            tLRPC$TL_messages_editMessage.message = str;
            tLRPC$TL_messages_editMessage.flags |= 2048;
            tLRPC$TL_messages_editMessage.no_webpage = !z;
        }
        tLRPC$TL_messages_editMessage.id = messageObject.getId();
        if (arrayList != null) {
            tLRPC$TL_messages_editMessage.entities = arrayList;
            tLRPC$TL_messages_editMessage.flags |= 8;
        }
        if (i != 0) {
            tLRPC$TL_messages_editMessage.schedule_date = i;
            tLRPC$TL_messages_editMessage.flags |= 32768;
        }
        return getConnectionsManager().sendRequest(tLRPC$TL_messages_editMessage, new RequestDelegate(baseFragment, tLRPC$TL_messages_editMessage, runnable) {
            private final /* synthetic */ BaseFragment f$1;
            private final /* synthetic */ TLRPC$TL_messages_editMessage f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$editMessage$16$SendMessagesHelper(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$editMessage$16$SendMessagesHelper(BaseFragment baseFragment, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editMessage) {
                private final /* synthetic */ TLRPC$TL_error f$1;
                private final /* synthetic */ BaseFragment f$2;
                private final /* synthetic */ TLRPC$TL_messages_editMessage f$3;

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

    public /* synthetic */ void lambda$null$15$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editMessage, new Object[0]);
    }

    /* access modifiers changed from: private */
    public void sendLocation(Location location) {
        TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo = new TLRPC$TL_messageMediaGeo();
        TLRPC$TL_geoPoint tLRPC$TL_geoPoint = new TLRPC$TL_geoPoint();
        tLRPC$TL_messageMediaGeo.geo = tLRPC$TL_geoPoint;
        tLRPC$TL_geoPoint.lat = AndroidUtilities.fixLocationCoord(location.getLatitude());
        tLRPC$TL_messageMediaGeo.geo._long = AndroidUtilities.fixLocationCoord(location.getLongitude());
        for (Map.Entry<String, MessageObject> value : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaGeo, messageObject.getDialogId(), messageObject, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        if (messageObject != null && tLRPC$KeyboardButton != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(messageObject.getDialogId());
            sb.append("_");
            sb.append(messageObject.getId());
            sb.append("_");
            sb.append(Utilities.bytesToHex(tLRPC$KeyboardButton.data));
            sb.append("_");
            sb.append(tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonGame ? "1" : "0");
            this.waitingForLocation.put(sb.toString(), messageObject);
            this.locationProvider.start();
        }
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        if (messageObject == null || tLRPC$KeyboardButton == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(messageObject.getDialogId());
        sb.append("_");
        sb.append(messageObject.getId());
        sb.append("_");
        sb.append(Utilities.bytesToHex(tLRPC$KeyboardButton.data));
        sb.append("_");
        sb.append(tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonGame ? "1" : "0");
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
        TLRPC$Chat chatSync;
        TLRPC$User userSync;
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
        TLRPC$TL_messages_getBotCallbackAnswer tLRPC$TL_messages_getBotCallbackAnswer = new TLRPC$TL_messages_getBotCallbackAnswer();
        tLRPC$TL_messages_getBotCallbackAnswer.peer = getMessagesController().getInputPeer(i3);
        tLRPC$TL_messages_getBotCallbackAnswer.msg_id = i2;
        tLRPC$TL_messages_getBotCallbackAnswer.game = false;
        if (bArr2 != null) {
            tLRPC$TL_messages_getBotCallbackAnswer.flags |= 1;
            tLRPC$TL_messages_getBotCallbackAnswer.data = bArr2;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getBotCallbackAnswer, new RequestDelegate(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$null$18$SendMessagesHelper(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 2);
        getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, true, 0);
    }

    public /* synthetic */ void lambda$null$17$SendMessagesHelper(String str) {
        Boolean remove = this.waitingForCallback.remove(str);
    }

    public /* synthetic */ void lambda$null$18$SendMessagesHelper(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

    public int sendVote(MessageObject messageObject, ArrayList<TLRPC$TL_pollAnswer> arrayList, Runnable runnable) {
        byte[] bArr;
        if (messageObject == null) {
            return 0;
        }
        String str = "poll_" + messageObject.getPollId();
        if (this.waitingForCallback.containsKey(str)) {
            return 0;
        }
        TLRPC$TL_messages_sendVote tLRPC$TL_messages_sendVote = new TLRPC$TL_messages_sendVote();
        tLRPC$TL_messages_sendVote.msg_id = messageObject.getId();
        tLRPC$TL_messages_sendVote.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        if (arrayList != null) {
            bArr = new byte[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$TL_pollAnswer tLRPC$TL_pollAnswer = arrayList.get(i);
                tLRPC$TL_messages_sendVote.options.add(tLRPC$TL_pollAnswer.option);
                bArr[i] = tLRPC$TL_pollAnswer.option[0];
            }
        } else {
            bArr = new byte[0];
        }
        this.waitingForVote.put(str, bArr);
        return getConnectionsManager().sendRequest(tLRPC$TL_messages_sendVote, new RequestDelegate(messageObject, str, runnable) {
            private final /* synthetic */ MessageObject f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$sendVote$21$SendMessagesHelper(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$sendVote$21$SendMessagesHelper(MessageObject messageObject, String str, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            this.voteSendTime.put(messageObject.getPollId(), 0L);
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
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
            TLRPC$TL_messages_sendReaction tLRPC$TL_messages_sendReaction = new TLRPC$TL_messages_sendReaction();
            tLRPC$TL_messages_sendReaction.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
            tLRPC$TL_messages_sendReaction.msg_id = messageObject.getId();
            if (charSequence != null) {
                tLRPC$TL_messages_sendReaction.reaction = charSequence.toString();
                tLRPC$TL_messages_sendReaction.flags |= 1;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_sendReaction, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SendMessagesHelper.this.lambda$sendReaction$22$SendMessagesHelper(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendReaction$22$SendMessagesHelper(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0082  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void sendCallback(boolean r18, org.telegram.messenger.MessageObject r19, org.telegram.tgnet.TLRPC$KeyboardButton r20, org.telegram.ui.ChatActivity r21) {
        /*
            r17 = this;
            r8 = r19
            r9 = r20
            if (r8 == 0) goto L_0x0113
            if (r9 == 0) goto L_0x0113
            if (r21 != 0) goto L_0x000c
            goto L_0x0113
        L_0x000c:
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r11 = 1
            r12 = 0
            r13 = 2
            if (r10 == 0) goto L_0x0016
            r0 = 3
        L_0x0014:
            r14 = 0
            goto L_0x0027
        L_0x0016:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            if (r0 == 0) goto L_0x001c
            r0 = 1
            goto L_0x0014
        L_0x001c:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
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
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
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
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
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

    public /* synthetic */ void lambda$sendCallback$24$SendMessagesHelper(String str, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, TLObject[] tLObjectArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, z, tLObject, messageObject, tLRPC$KeyboardButton, chatActivity, tLObjectArr) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ MessageObject f$4;
            private final /* synthetic */ TLRPC$KeyboardButton f$5;
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

    /* JADX WARNING: Code restructure failed: missing block: B:76:0x015a, code lost:
        if (org.telegram.messenger.MessagesController.getNotificationsSettings(r0.currentAccount).getBoolean("askgame_" + r9, true) != false) goto L_0x015e;
     */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00cb  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$23$SendMessagesHelper(java.lang.String r12, boolean r13, org.telegram.tgnet.TLObject r14, org.telegram.messenger.MessageObject r15, org.telegram.tgnet.TLRPC$KeyboardButton r16, org.telegram.ui.ChatActivity r17, org.telegram.tgnet.TLObject[] r18) {
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
            goto L_0x016e
        L_0x0017:
            if (r2 == 0) goto L_0x016e
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r8 = 1
            if (r7 == 0) goto L_0x004c
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest
            if (r1 == 0) goto L_0x0030
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest r1 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest) r1
            r2 = r18[r6]
            org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth r2 = (org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth) r2
            java.lang.String r3 = r4.url
            r5.showRequestUrlAlert(r1, r2, r3)
            goto L_0x016e
        L_0x0030:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted
            if (r1 == 0) goto L_0x003e
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted r1 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted) r1
            java.lang.String r1 = r1.url
            r5.showOpenUrlAlert(r1, r6)
            goto L_0x016e
        L_0x003e:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault
            if (r1 == 0) goto L_0x016e
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault r1 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault) r1
            java.lang.String r1 = r4.url
            r5.showOpenUrlAlert(r1, r8)
            goto L_0x016e
        L_0x004c:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r7 == 0) goto L_0x007a
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_payments_paymentForm
            if (r1 == 0) goto L_0x006a
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r1 = (org.telegram.tgnet.TLRPC$TL_payments_paymentForm) r1
            org.telegram.messenger.MessagesController r2 = r11.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r1.users
            r2.putUsers(r4, r6)
            org.telegram.ui.PaymentFormActivity r2 = new org.telegram.ui.PaymentFormActivity
            r2.<init>((org.telegram.tgnet.TLRPC$TL_payments_paymentForm) r1, (org.telegram.messenger.MessageObject) r15)
            r5.presentFragment(r2)
            goto L_0x016e
        L_0x006a:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt
            if (r1 == 0) goto L_0x016e
            org.telegram.ui.PaymentFormActivity r1 = new org.telegram.ui.PaymentFormActivity
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r2 = (org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt) r2
            r1.<init>((org.telegram.messenger.MessageObject) r15, (org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt) r2)
            r5.presentFragment(r1)
            goto L_0x016e
        L_0x007a:
            org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer r2 = (org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer) r2
            if (r13 != 0) goto L_0x0089
            int r7 = r2.cache_time
            if (r7 == 0) goto L_0x0089
            org.telegram.messenger.MessagesStorage r7 = r11.getMessagesStorage()
            r7.saveBotCache(r12, r2)
        L_0x0089:
            java.lang.String r1 = r2.message
            r7 = 0
            if (r1 == 0) goto L_0x00ff
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            int r3 = r1.from_id
            int r1 = r1.via_bot_id
            if (r1 == 0) goto L_0x0097
            r3 = r1
        L_0x0097:
            if (r3 <= 0) goto L_0x00b0
            org.telegram.messenger.MessagesController r1 = r11.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r3)
            if (r1 == 0) goto L_0x00c2
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            goto L_0x00c3
        L_0x00b0:
            org.telegram.messenger.MessagesController r1 = r11.getMessagesController()
            int r3 = -r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)
            if (r1 == 0) goto L_0x00c2
            java.lang.String r1 = r1.title
            goto L_0x00c3
        L_0x00c2:
            r1 = r7
        L_0x00c3:
            if (r1 != 0) goto L_0x00c7
            java.lang.String r1 = "bot"
        L_0x00c7:
            boolean r3 = r2.alert
            if (r3 == 0) goto L_0x00f8
            android.app.Activity r3 = r17.getParentActivity()
            if (r3 != 0) goto L_0x00d2
            return
        L_0x00d2:
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r17.getParentActivity()
            r3.<init>((android.content.Context) r4)
            r3.setTitle(r1)
            r1 = 2131625983(0x7f0e07ff, float:1.887919E38)
            java.lang.String r4 = "OK"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r3.setPositiveButton(r1, r7)
            java.lang.String r1 = r2.message
            r3.setMessage(r1)
            org.telegram.ui.ActionBar.AlertDialog r1 = r3.create()
            r5.showDialog(r1)
            goto L_0x016e
        L_0x00f8:
            java.lang.String r2 = r2.message
            r5.showAlert(r1, r2)
            goto L_0x016e
        L_0x00ff:
            java.lang.String r1 = r2.url
            if (r1 == 0) goto L_0x016e
            android.app.Activity r1 = r17.getParentActivity()
            if (r1 != 0) goto L_0x010a
            return
        L_0x010a:
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            int r9 = r1.from_id
            int r1 = r1.via_bot_id
            if (r1 == 0) goto L_0x0113
            r9 = r1
        L_0x0113:
            org.telegram.messenger.MessagesController r1 = r11.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r10)
            if (r1 == 0) goto L_0x0127
            boolean r1 = r1.verified
            if (r1 == 0) goto L_0x0127
            r1 = 1
            goto L_0x0128
        L_0x0127:
            r1 = 0
        L_0x0128:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            if (r4 == 0) goto L_0x0169
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r10 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r10 == 0) goto L_0x0137
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            goto L_0x0138
        L_0x0137:
            r4 = r7
        L_0x0138:
            if (r4 != 0) goto L_0x013b
            return
        L_0x013b:
            java.lang.String r7 = r2.url
            if (r1 != 0) goto L_0x015d
            int r1 = r0.currentAccount
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getNotificationsSettings(r1)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "askgame_"
            r2.append(r10)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            boolean r1 = r1.getBoolean(r2, r8)
            if (r1 == 0) goto L_0x015d
            goto L_0x015e
        L_0x015d:
            r8 = 0
        L_0x015e:
            r1 = r17
            r2 = r4
            r3 = r15
            r4 = r7
            r5 = r8
            r6 = r9
            r1.showOpenGameAlert(r2, r3, r4, r5, r6)
            goto L_0x016e
        L_0x0169:
            java.lang.String r1 = r2.url
            r5.showOpenUrlAlert(r1, r6)
        L_0x016e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$23$SendMessagesHelper(java.lang.String, boolean, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity, org.telegram.tgnet.TLObject[]):void");
    }

    public boolean isSendingCallback(MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        int i = 0;
        if (messageObject == null || tLRPC$KeyboardButton == null) {
            return false;
        }
        if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonUrlAuth) {
            i = 3;
        } else if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonGame) {
            i = 1;
        } else if (tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonBuy) {
            i = 2;
        }
        return this.waitingForCallback.containsKey(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(tLRPC$KeyboardButton.data) + "_" + i);
    }

    public void sendGame(TLRPC$InputPeer tLRPC$InputPeer, TLRPC$TL_inputMediaGame tLRPC$TL_inputMediaGame, long j, long j2) {
        long j3;
        NativeByteBuffer nativeByteBuffer;
        if (tLRPC$InputPeer != null && tLRPC$TL_inputMediaGame != null) {
            TLRPC$TL_messages_sendMedia tLRPC$TL_messages_sendMedia = new TLRPC$TL_messages_sendMedia();
            tLRPC$TL_messages_sendMedia.peer = tLRPC$InputPeer;
            if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChannel) {
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                tLRPC$TL_messages_sendMedia.silent = notificationsSettings.getBoolean("silent_" + (-tLRPC$InputPeer.channel_id), false);
            } else if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChat) {
                SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(this.currentAccount);
                tLRPC$TL_messages_sendMedia.silent = notificationsSettings2.getBoolean("silent_" + (-tLRPC$InputPeer.chat_id), false);
            } else {
                SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(this.currentAccount);
                tLRPC$TL_messages_sendMedia.silent = notificationsSettings3.getBoolean("silent_" + tLRPC$InputPeer.user_id, false);
            }
            if (j != 0) {
                j3 = j;
            } else {
                j3 = getNextRandomId();
            }
            tLRPC$TL_messages_sendMedia.random_id = j3;
            tLRPC$TL_messages_sendMedia.message = "";
            tLRPC$TL_messages_sendMedia.media = tLRPC$TL_inputMediaGame;
            if (j2 == 0) {
                NativeByteBuffer nativeByteBuffer2 = null;
                try {
                    nativeByteBuffer = new NativeByteBuffer(tLRPC$InputPeer.getObjectSize() + tLRPC$TL_inputMediaGame.getObjectSize() + 4 + 8);
                    try {
                        nativeByteBuffer.writeInt32(3);
                        nativeByteBuffer.writeInt64(j);
                        tLRPC$InputPeer.serializeToStream(nativeByteBuffer);
                        tLRPC$TL_inputMediaGame.serializeToStream(nativeByteBuffer);
                    } catch (Exception e) {
                        e = e;
                        nativeByteBuffer2 = nativeByteBuffer;
                    }
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e((Throwable) e);
                    nativeByteBuffer = nativeByteBuffer2;
                    j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMedia, new RequestDelegate(j2) {
                        private final /* synthetic */ long f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            SendMessagesHelper.this.lambda$sendGame$25$SendMessagesHelper(this.f$1, tLObject, tLRPC$TL_error);
                        }
                    });
                }
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMedia, new RequestDelegate(j2) {
                private final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SendMessagesHelper.this.lambda$sendGame$25$SendMessagesHelper(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendGame$25$SendMessagesHelper(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void sendMessage(MessageObject messageObject) {
        MessageObject messageObject2 = messageObject;
        long dialogId = messageObject.getDialogId();
        TLRPC$Message tLRPC$Message = messageObject2.messageOwner;
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, dialogId, tLRPC$Message.attachPath, (MessageObject) null, (TLRPC$WebPage) null, true, messageObject, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$Message.reply_markup, tLRPC$Message.params, !tLRPC$Message.silent, messageObject2.scheduled ? tLRPC$Message.date : 0, 0, (Object) null);
    }

    public void sendMessage(TLRPC$User tLRPC$User, long j, MessageObject messageObject, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$User, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, (String) null, messageObject, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$TL_document tLRPC$TL_document, VideoEditedInfo videoEditedInfo, String str, long j, MessageObject messageObject, String str2, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        String str3 = str2;
        sendMessage((String) null, str3, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, videoEditedInfo, (TLRPC$User) null, tLRPC$TL_document, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, str, messageObject, (TLRPC$WebPage) null, true, (MessageObject) null, arrayList, tLRPC$ReplyMarkup, hashMap, z, i, i2, obj);
    }

    public void sendMessage(String str, long j, MessageObject messageObject, TLRPC$WebPage tLRPC$WebPage, boolean z, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z2, int i) {
        sendMessage(str, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, (String) null, messageObject, tLRPC$WebPage, z, (MessageObject) null, arrayList, tLRPC$ReplyMarkup, hashMap, z2, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$MessageMedia tLRPC$MessageMedia, long j, MessageObject messageObject, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, tLRPC$MessageMedia, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, (String) null, messageObject, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, long j, MessageObject messageObject, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, tLRPC$TL_messageMediaPoll, j, (String) null, messageObject, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$TL_game tLRPC$TL_game, long j, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, tLRPC$TL_game, (TLRPC$TL_messageMediaPoll) null, j, (String) null, (MessageObject) null, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$TL_photo tLRPC$TL_photo, String str, long j, MessageObject messageObject, String str2, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        String str3 = str2;
        sendMessage((String) null, str3, (TLRPC$MessageMedia) null, tLRPC$TL_photo, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, str, messageObject, (TLRPC$WebPage) null, true, (MessageObject) null, arrayList, tLRPC$ReplyMarkup, hashMap, z, i, i2, obj);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v5, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v1, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v3, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v4, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v5, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v8, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v22, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v9, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v10, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v12, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v14, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v87, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v88, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v150, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v89, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v110, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v118, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v22, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v125, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v27, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v130, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v29, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v132, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v134, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v30, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v95, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v21, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v137, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v22, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v23, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r46v24, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v102, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v104, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v106, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v111, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v112, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v117, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v113, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v116, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v122, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v130, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v131, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v161, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v163, resolved type: org.telegram.tgnet.TLRPC$TL_message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v132, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v133, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v159, resolved type: org.telegram.tgnet.TLRPC$TL_userRequest_old2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r53v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v166, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v99, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v168, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v169, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v173, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v175, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v100, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v101, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v102, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v180, resolved type: org.telegram.tgnet.TLRPC$PhotoSize} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v103, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v181, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v187, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v193, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v213, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v214, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v104, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v242, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v243, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v244, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v245, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v246, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v247, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v248, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v249, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v250, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX WARNING: Code restructure failed: missing block: B:1306:0x1912, code lost:
        r0 = e;
        r13 = r13;
        r11 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1320:0x193c, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1321:0x193e, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1324:0x1944, code lost:
        r4.messageOwner.send_state = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x056d, code lost:
        if (org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r10, false) != false) goto L_0x056f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x0584, code lost:
        r10.attributes.remove(r7);
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55();
        r10.attributes.add(r5);
        r5.alt = r12.alt;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x0599, code lost:
        if (r12.stickerset == null) goto L_0x05ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x059f, code lost:
        if ((r12.stickerset instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName) == false) goto L_0x05a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:?, code lost:
        r7 = r12.stickerset.short_name;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x05a5, code lost:
        r22 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x05ae, code lost:
        r22 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:?, code lost:
        r7 = getMediaDataController().getStickerSetName(r12.stickerset.id);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x05ba, code lost:
        if (android.text.TextUtils.isEmpty(r7) != false) goto L_0x05c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x05bc, code lost:
        r10 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName();
        r5.stickerset = r10;
        r10.short_name = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x05c6, code lost:
        r5.stickerset = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x05ce, code lost:
        r22 = r11;
        r5.stickerset = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x05d8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1008:0x1210  */
    /* JADX WARNING: Removed duplicated region for block: B:1079:0x1380 A[Catch:{ Exception -> 0x1244 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1085:0x13b2 A[Catch:{ Exception -> 0x1244 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1090:0x13f3 A[Catch:{ Exception -> 0x1244 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1099:0x1423  */
    /* JADX WARNING: Removed duplicated region for block: B:1100:0x1425  */
    /* JADX WARNING: Removed duplicated region for block: B:1103:0x142b A[Catch:{ Exception -> 0x1445 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1106:0x143b A[Catch:{ Exception -> 0x1445 }] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:1218:0x176f A[SYNTHETIC, Splitter:B:1218:0x176f] */
    /* JADX WARNING: Removed duplicated region for block: B:1222:0x1776 A[SYNTHETIC, Splitter:B:1222:0x1776] */
    /* JADX WARNING: Removed duplicated region for block: B:1240:0x17be A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1242:0x17c2 A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1252:0x17ed A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1265:0x1829 A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1267:0x1835 A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1273:0x184b A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1276:0x1857 A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1277:0x1859 A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1280:0x186d A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1281:0x1877 A[Catch:{ Exception -> 0x1912 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1320:0x193c  */
    /* JADX WARNING: Removed duplicated region for block: B:1321:0x193e  */
    /* JADX WARNING: Removed duplicated region for block: B:1324:0x1944  */
    /* JADX WARNING: Removed duplicated region for block: B:1335:0x13a2 A[EDGE_INSN: B:1335:0x13a2->B:1083:0x13a2 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:1354:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:1356:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x0533 A[Catch:{ Exception -> 0x04fc }] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0546 A[Catch:{ Exception -> 0x04fc }] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0553 A[Catch:{ Exception -> 0x04fc }] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x055e A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x0562 A[Catch:{ Exception -> 0x05eb }] */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x0602 A[SYNTHETIC, Splitter:B:341:0x0602] */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x061a A[Catch:{ Exception -> 0x0611 }] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x061d A[SYNTHETIC, Splitter:B:349:0x061d] */
    /* JADX WARNING: Removed duplicated region for block: B:357:0x0627 A[SYNTHETIC, Splitter:B:357:0x0627] */
    /* JADX WARNING: Removed duplicated region for block: B:362:0x063a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x067e A[SYNTHETIC, Splitter:B:375:0x067e] */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x0693 A[SYNTHETIC, Splitter:B:382:0x0693] */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x06cb A[SYNTHETIC, Splitter:B:395:0x06cb] */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x06d5  */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x06d7 A[SYNTHETIC, Splitter:B:401:0x06d7] */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x06e5 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d4 A[SYNTHETIC, Splitter:B:40:0x00d4] */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0725  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x073d  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x076d A[Catch:{ Exception -> 0x0685 }] */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0783 A[Catch:{ Exception -> 0x0685 }] */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x07b9  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x0884 A[Catch:{ Exception -> 0x0888 }] */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x088b  */
    /* JADX WARNING: Removed duplicated region for block: B:512:0x0897  */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x0899  */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x08a2 A[SYNTHETIC, Splitter:B:517:0x08a2] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00ed A[Catch:{ Exception -> 0x028f }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x010e A[Catch:{ Exception -> 0x028f }] */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x08c4 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x08cd A[Catch:{ Exception -> 0x08ba }] */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x08fd  */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0905  */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x0965  */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x09bc A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0a2b A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:640:0x0ac1 A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:643:0x0ada A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0ae8 A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0b15 A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0b17 A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x0b7d A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:675:0x0ba3 A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0bb2 A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0bb4 A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:682:0x0bca A[Catch:{ Exception -> 0x0990 }] */
    /* JADX WARNING: Removed duplicated region for block: B:834:0x0e9d A[SYNTHETIC, Splitter:B:834:0x0e9d] */
    /* JADX WARNING: Removed duplicated region for block: B:837:0x0ea4 A[SYNTHETIC, Splitter:B:837:0x0ea4] */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x0ec8 A[Catch:{ Exception -> 0x0var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x0ef7  */
    /* JADX WARNING: Removed duplicated region for block: B:916:0x1030 A[Catch:{ Exception -> 0x1206 }] */
    /* JADX WARNING: Removed duplicated region for block: B:918:0x103c A[Catch:{ Exception -> 0x1206 }] */
    /* JADX WARNING: Removed duplicated region for block: B:936:0x10e4 A[Catch:{ Exception -> 0x1206 }] */
    /* JADX WARNING: Removed duplicated region for block: B:943:0x1109 A[Catch:{ Exception -> 0x1206 }] */
    /* JADX WARNING: Removed duplicated region for block: B:945:0x1113 A[Catch:{ Exception -> 0x1206 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendMessage(java.lang.String r46, java.lang.String r47, org.telegram.tgnet.TLRPC$MessageMedia r48, org.telegram.tgnet.TLRPC$TL_photo r49, org.telegram.messenger.VideoEditedInfo r50, org.telegram.tgnet.TLRPC$User r51, org.telegram.tgnet.TLRPC$TL_document r52, org.telegram.tgnet.TLRPC$TL_game r53, org.telegram.tgnet.TLRPC$TL_messageMediaPoll r54, long r55, java.lang.String r57, org.telegram.messenger.MessageObject r58, org.telegram.tgnet.TLRPC$WebPage r59, boolean r60, org.telegram.messenger.MessageObject r61, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r62, org.telegram.tgnet.TLRPC$ReplyMarkup r63, java.util.HashMap<java.lang.String, java.lang.String> r64, boolean r65, int r66, int r67, java.lang.Object r68) {
        /*
            r45 = this;
            r1 = r45
            r2 = r46
            r3 = r48
            r4 = r49
            r5 = r51
            r6 = r52
            r7 = r53
            r8 = r54
            r9 = r55
            r11 = r57
            r12 = r58
            r13 = r59
            r14 = r61
            r15 = r62
            r12 = r64
            r6 = r66
            r7 = r67
            if (r5 == 0) goto L_0x0029
            java.lang.String r11 = r5.phone
            if (r11 != 0) goto L_0x0029
            return
        L_0x0029:
            r16 = 0
            int r11 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r11 != 0) goto L_0x0030
            return
        L_0x0030:
            java.lang.String r11 = ""
            if (r2 != 0) goto L_0x0039
            if (r47 != 0) goto L_0x0039
            r18 = r11
            goto L_0x003b
        L_0x0039:
            r18 = r47
        L_0x003b:
            if (r12 == 0) goto L_0x004e
            java.lang.String r5 = "originalPath"
            boolean r5 = r12.containsKey(r5)
            if (r5 == 0) goto L_0x004e
            java.lang.String r5 = "originalPath"
            java.lang.Object r5 = r12.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            goto L_0x004f
        L_0x004e:
            r5 = 0
        L_0x004f:
            r19 = -1
            r20 = r5
            int r5 = (int) r9
            r21 = 32
            long r3 = r9 >> r21
            int r4 = (int) r3
            if (r5 == 0) goto L_0x0064
            org.telegram.messenger.MessagesController r3 = r45.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer(r5)
            goto L_0x0065
        L_0x0064:
            r3 = 0
        L_0x0065:
            if (r5 != 0) goto L_0x00a6
            org.telegram.messenger.MessagesController r10 = r45.getMessagesController()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r10.getEncryptedChat(r9)
            if (r9 != 0) goto L_0x00c5
            if (r14 == 0) goto L_0x00a5
            org.telegram.messenger.MessagesStorage r2 = r45.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            boolean r4 = r14.scheduled
            r2.markMessageAsSendError(r3, r4)
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            r3 = 2
            r2.send_state = r3
            org.telegram.messenger.NotificationCenter r2 = r45.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.messageSendError
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r5 = r61.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 0
            r4[r6] = r5
            r2.postNotificationName(r3, r4)
            int r2 = r61.getId()
            r1.processSentMessage(r2)
        L_0x00a5:
            return
        L_0x00a6:
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r9 == 0) goto L_0x00c4
            org.telegram.messenger.MessagesController r9 = r45.getMessagesController()
            int r10 = r3.channel_id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
            if (r9 == 0) goto L_0x00c0
            boolean r9 = r9.megagroup
            if (r9 != 0) goto L_0x00c0
            r9 = 1
            goto L_0x00c1
        L_0x00c0:
            r9 = 0
        L_0x00c1:
            r10 = r9
            r9 = 0
            goto L_0x00c6
        L_0x00c4:
            r9 = 0
        L_0x00c5:
            r10 = 0
        L_0x00c6:
            r23 = r4
            java.lang.String r4 = "http"
            java.lang.String r1 = "query_id"
            r25 = r5
            java.lang.String r5 = "parentObject"
            r26 = r3
            if (r14 == 0) goto L_0x02a1
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x0298 }
            if (r68 != 0) goto L_0x00e5
            if (r12 == 0) goto L_0x00e5
            boolean r27 = r12.containsKey(r5)     // Catch:{ Exception -> 0x028f }
            if (r27 == 0) goto L_0x00e5
            java.lang.Object r27 = r12.get(r5)     // Catch:{ Exception -> 0x028f }
            goto L_0x00e7
        L_0x00e5:
            r27 = r68
        L_0x00e7:
            boolean r28 = r61.isForwarded()     // Catch:{ Exception -> 0x028f }
            if (r28 == 0) goto L_0x010e
            r22 = r51
            r15 = r3
            r28 = r5
            r3 = r6
            r19 = r8
            r29 = r10
            r6 = r11
            r33 = r13
            r34 = r18
            r8 = r26
            r53 = r27
            r11 = 4
            r10 = r49
            r26 = r52
            r5 = r2
            r18 = r4
            r2 = r7
            r4 = r12
            r7 = r48
            goto L_0x0678
        L_0x010e:
            boolean r28 = r61.isDice()     // Catch:{ Exception -> 0x028f }
            if (r28 == 0) goto L_0x012b
            r18 = r52
            r28 = r5
            r19 = r8
            r29 = r10
            r22 = r11
            r30 = r22
            r10 = 2
            r31 = 11
            r5 = r48
            r8 = r49
            r11 = r51
            goto L_0x025d
        L_0x012b:
            r28 = r5
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            if (r5 == 0) goto L_0x023f
            boolean r5 = r61.isAnimatedEmoji()     // Catch:{ Exception -> 0x028f }
            if (r5 == 0) goto L_0x0139
            goto L_0x023f
        L_0x0139:
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r29 = r10
            r10 = 4
            if (r5 != r10) goto L_0x0151
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media     // Catch:{ Exception -> 0x028f }
            r19 = r8
            r30 = r11
            r22 = r18
            r10 = 2
            r31 = 1
            r8 = r49
            r11 = r51
            goto L_0x025b
        L_0x0151:
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 1
            if (r5 != r10) goto L_0x016e
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$TL_photo r5 = (org.telegram.tgnet.TLRPC$TL_photo) r5     // Catch:{ Exception -> 0x028f }
            r19 = r8
            r30 = r11
            r22 = r18
            r10 = 2
            r31 = 2
            r11 = r51
            r18 = r52
            r8 = r5
        L_0x016a:
            r5 = r48
            goto L_0x025d
        L_0x016e:
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 3
            if (r5 == r10) goto L_0x0228
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 5
            if (r5 == r10) goto L_0x0228
            if (r50 == 0) goto L_0x017c
            goto L_0x0228
        L_0x017c:
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 12
            if (r5 != r10) goto L_0x01c1
            org.telegram.tgnet.TLRPC$TL_userRequest_old2 r5 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2     // Catch:{ Exception -> 0x028f }
            r5.<init>()     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r3.media     // Catch:{ Exception -> 0x028f }
            java.lang.String r10 = r10.phone_number     // Catch:{ Exception -> 0x028f }
            r5.phone = r10     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r3.media     // Catch:{ Exception -> 0x028f }
            java.lang.String r10 = r10.first_name     // Catch:{ Exception -> 0x028f }
            r5.first_name = r10     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r3.media     // Catch:{ Exception -> 0x028f }
            java.lang.String r10 = r10.last_name     // Catch:{ Exception -> 0x028f }
            r5.last_name = r10     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r10 = new org.telegram.tgnet.TLRPC$TL_restrictionReason     // Catch:{ Exception -> 0x028f }
            r10.<init>()     // Catch:{ Exception -> 0x028f }
            r10.platform = r11     // Catch:{ Exception -> 0x028f }
            r10.reason = r11     // Catch:{ Exception -> 0x028f }
            r30 = r11
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r3.media     // Catch:{ Exception -> 0x028f }
            java.lang.String r11 = r11.vcard     // Catch:{ Exception -> 0x028f }
            r10.text = r11     // Catch:{ Exception -> 0x028f }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r11 = r5.restriction_reason     // Catch:{ Exception -> 0x028f }
            r11.add(r10)     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r3.media     // Catch:{ Exception -> 0x028f }
            int r10 = r10.user_id     // Catch:{ Exception -> 0x028f }
            r5.id = r10     // Catch:{ Exception -> 0x028f }
            r11 = r5
            r19 = r8
            r22 = r18
            r10 = 2
            r31 = 6
            r5 = r48
            goto L_0x0259
        L_0x01c1:
            r30 = r11
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 8
            if (r5 == r10) goto L_0x0218
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 9
            if (r5 == r10) goto L_0x0218
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 13
            if (r5 == r10) goto L_0x0218
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 14
            if (r5 == r10) goto L_0x0218
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 15
            if (r5 != r10) goto L_0x01e2
            goto L_0x0218
        L_0x01e2:
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r10 = 2
            if (r5 != r10) goto L_0x01f6
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$TL_document r5 = (org.telegram.tgnet.TLRPC$TL_document) r5     // Catch:{ Exception -> 0x028f }
            r11 = r51
            r19 = r8
            r22 = r18
            r31 = 8
            goto L_0x0239
        L_0x01f6:
            int r5 = r14.type     // Catch:{ Exception -> 0x028f }
            r11 = 17
            if (r5 != r11) goto L_0x020d
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5     // Catch:{ Exception -> 0x028f }
            r8 = r49
            r11 = r51
            r19 = r5
            r22 = r18
            r31 = 10
            r5 = r48
            goto L_0x025b
        L_0x020d:
            r5 = r48
            r11 = r51
            r19 = r8
            r22 = r18
            r31 = -1
            goto L_0x0259
        L_0x0218:
            r10 = 2
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$TL_document r5 = (org.telegram.tgnet.TLRPC$TL_document) r5     // Catch:{ Exception -> 0x028f }
            r11 = r51
            r19 = r8
            r22 = r18
            r31 = 7
            goto L_0x0239
        L_0x0228:
            r30 = r11
            r10 = 2
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$TL_document r5 = (org.telegram.tgnet.TLRPC$TL_document) r5     // Catch:{ Exception -> 0x028f }
            r11 = r51
            r19 = r8
            r22 = r18
            r31 = 3
        L_0x0239:
            r8 = r49
            r18 = r5
            goto L_0x016a
        L_0x023f:
            r29 = r10
            r30 = r11
            r10 = 2
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x028f }
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media     // Catch:{ Exception -> 0x028f }
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x028f }
            if (r5 == 0) goto L_0x024d
            goto L_0x024f
        L_0x024d:
            java.lang.String r2 = r3.message     // Catch:{ Exception -> 0x028f }
        L_0x024f:
            r5 = r48
            r11 = r51
            r19 = r8
            r22 = r18
            r31 = 0
        L_0x0259:
            r8 = r49
        L_0x025b:
            r18 = r52
        L_0x025d:
            if (r12 == 0) goto L_0x0267
            boolean r32 = r12.containsKey(r1)     // Catch:{ Exception -> 0x028f }
            if (r32 == 0) goto L_0x0267
            r31 = 9
        L_0x0267:
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r3.media     // Catch:{ Exception -> 0x028f }
            int r10 = r10.ttl_seconds     // Catch:{ Exception -> 0x028f }
            if (r10 <= 0) goto L_0x0271
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r3.media     // Catch:{ Exception -> 0x028f }
            int r7 = r7.ttl_seconds     // Catch:{ Exception -> 0x028f }
        L_0x0271:
            r15 = r3
            r3 = r6
            r10 = r8
            r33 = r13
            r34 = r22
            r8 = r26
            r53 = r27
            r6 = r30
            r22 = r11
            r26 = r18
            r11 = r31
            r18 = r4
            r4 = r12
            r44 = r5
            r5 = r2
            r2 = r7
            r7 = r44
            goto L_0x0678
        L_0x028f:
            r0 = move-exception
            r10 = r45
            r1 = r0
            r5 = r3
        L_0x0294:
            r11 = r6
        L_0x0295:
            r4 = 0
            goto L_0x1933
        L_0x0298:
            r0 = move-exception
            r10 = r45
            r1 = r0
            r11 = r6
        L_0x029d:
            r4 = 0
            r5 = 0
            goto L_0x1933
        L_0x02a1:
            r28 = r5
            r29 = r10
            r30 = r11
            if (r2 == 0) goto L_0x0320
            if (r9 == 0) goto L_0x02b1
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0298 }
            r3.<init>()     // Catch:{ Exception -> 0x0298 }
            goto L_0x02b6
        L_0x02b1:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0298 }
            r3.<init>()     // Catch:{ Exception -> 0x0298 }
        L_0x02b6:
            if (r9 == 0) goto L_0x02cc
            boolean r5 = r13 instanceof org.telegram.tgnet.TLRPC$TL_webPagePending     // Catch:{ Exception -> 0x028f }
            if (r5 == 0) goto L_0x02cc
            java.lang.String r5 = r13.url     // Catch:{ Exception -> 0x028f }
            if (r5 == 0) goto L_0x02cb
            org.telegram.tgnet.TLRPC$TL_webPageUrlPending r5 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending     // Catch:{ Exception -> 0x028f }
            r5.<init>()     // Catch:{ Exception -> 0x028f }
            java.lang.String r10 = r13.url     // Catch:{ Exception -> 0x028f }
            r5.url = r10     // Catch:{ Exception -> 0x028f }
            r13 = r5
            goto L_0x02cc
        L_0x02cb:
            r13 = 0
        L_0x02cc:
            if (r13 != 0) goto L_0x02f8
            if (r15 == 0) goto L_0x02d6
            boolean r5 = r62.isEmpty()     // Catch:{ Exception -> 0x028f }
            if (r5 == 0) goto L_0x02f8
        L_0x02d6:
            java.lang.String r5 = "🎲"
            boolean r5 = r2.equals(r5)     // Catch:{ Exception -> 0x028f }
            if (r5 == 0) goto L_0x02f8
            if (r9 != 0) goto L_0x02f8
            if (r6 != 0) goto L_0x02f8
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaDice     // Catch:{ Exception -> 0x028f }
            r5.<init>()     // Catch:{ Exception -> 0x028f }
            r10 = -1
            r5.value = r10     // Catch:{ Exception -> 0x028f }
            r3.media = r5     // Catch:{ Exception -> 0x028f }
            r2 = r57
            r5 = r3
            r3 = r6
            r6 = r30
            r7 = r6
            r19 = 11
            goto L_0x0600
        L_0x02f8:
            if (r13 != 0) goto L_0x0302
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x028f }
            r5.<init>()     // Catch:{ Exception -> 0x028f }
            r3.media = r5     // Catch:{ Exception -> 0x028f }
            goto L_0x030b
        L_0x0302:
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x028f }
            r5.<init>()     // Catch:{ Exception -> 0x028f }
            r3.media = r5     // Catch:{ Exception -> 0x028f }
            r5.webpage = r13     // Catch:{ Exception -> 0x028f }
        L_0x030b:
            if (r12 == 0) goto L_0x0316
            boolean r5 = r12.containsKey(r1)     // Catch:{ Exception -> 0x028f }
            if (r5 == 0) goto L_0x0316
            r19 = 9
            goto L_0x0318
        L_0x0316:
            r19 = 0
        L_0x0318:
            r3.message = r2     // Catch:{ Exception -> 0x028f }
            r2 = r57
            r5 = r3
            r3 = r6
            goto L_0x041b
        L_0x0320:
            if (r8 == 0) goto L_0x033d
            if (r9 == 0) goto L_0x032a
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0298 }
            r3.<init>()     // Catch:{ Exception -> 0x0298 }
            goto L_0x032f
        L_0x032a:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0298 }
            r3.<init>()     // Catch:{ Exception -> 0x0298 }
        L_0x032f:
            r3.media = r8     // Catch:{ Exception -> 0x028f }
            r2 = r57
            r5 = r3
            r3 = r6
            r7 = r18
            r6 = r30
            r19 = 10
            goto L_0x0600
        L_0x033d:
            r3 = r48
            if (r3 == 0) goto L_0x0374
            if (r9 == 0) goto L_0x0349
            org.telegram.tgnet.TLRPC$TL_message_secret r5 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0298 }
            r5.<init>()     // Catch:{ Exception -> 0x0298 }
            goto L_0x034e
        L_0x0349:
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0298 }
            r5.<init>()     // Catch:{ Exception -> 0x0298 }
        L_0x034e:
            r5.media = r3     // Catch:{ Exception -> 0x036e }
            if (r12 == 0) goto L_0x0363
            boolean r10 = r12.containsKey(r1)     // Catch:{ Exception -> 0x036e }
            if (r10 == 0) goto L_0x0363
            r2 = r57
            r3 = r6
        L_0x035b:
            r7 = r18
            r6 = r30
        L_0x035f:
            r19 = 9
            goto L_0x0600
        L_0x0363:
            r2 = r57
            r3 = r6
            r7 = r18
            r6 = r30
            r19 = 1
            goto L_0x0600
        L_0x036e:
            r0 = move-exception
            r10 = r45
            r1 = r0
            goto L_0x0294
        L_0x0374:
            r5 = r49
            if (r5 == 0) goto L_0x03f8
            if (r9 == 0) goto L_0x0380
            org.telegram.tgnet.TLRPC$TL_message_secret r10 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0298 }
            r10.<init>()     // Catch:{ Exception -> 0x0298 }
            goto L_0x0385
        L_0x0380:
            org.telegram.tgnet.TLRPC$TL_message r10 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0298 }
            r10.<init>()     // Catch:{ Exception -> 0x0298 }
        L_0x0385:
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r11 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x03ef }
            r11.<init>()     // Catch:{ Exception -> 0x03ef }
            r10.media = r11     // Catch:{ Exception -> 0x03ef }
            int r2 = r11.flags     // Catch:{ Exception -> 0x03ef }
            r19 = 3
            r2 = r2 | 3
            r11.flags = r2     // Catch:{ Exception -> 0x03ef }
            if (r15 == 0) goto L_0x0398
            r10.entities = r15     // Catch:{ Exception -> 0x03ef }
        L_0x0398:
            if (r7 == 0) goto L_0x03aa
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r10.media     // Catch:{ Exception -> 0x03ef }
            r2.ttl_seconds = r7     // Catch:{ Exception -> 0x03ef }
            r10.ttl = r7     // Catch:{ Exception -> 0x03ef }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r10.media     // Catch:{ Exception -> 0x03ef }
            int r11 = r2.flags     // Catch:{ Exception -> 0x03ef }
            r19 = 4
            r11 = r11 | 4
            r2.flags = r11     // Catch:{ Exception -> 0x03ef }
        L_0x03aa:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r10.media     // Catch:{ Exception -> 0x03ef }
            r2.photo = r5     // Catch:{ Exception -> 0x03ef }
            if (r12 == 0) goto L_0x03bb
            boolean r2 = r12.containsKey(r1)     // Catch:{ Exception -> 0x03ef }
            if (r2 == 0) goto L_0x03bb
            r2 = r57
            r19 = 9
            goto L_0x03bf
        L_0x03bb:
            r2 = r57
            r19 = 2
        L_0x03bf:
            if (r2 == 0) goto L_0x03d0
            int r11 = r57.length()     // Catch:{ Exception -> 0x03ef }
            if (r11 <= 0) goto L_0x03d0
            boolean r11 = r2.startsWith(r4)     // Catch:{ Exception -> 0x03ef }
            if (r11 == 0) goto L_0x03d0
            r10.attachPath = r2     // Catch:{ Exception -> 0x03ef }
            goto L_0x03ec
        L_0x03d0:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r5.sizes     // Catch:{ Exception -> 0x03ef }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r5.sizes     // Catch:{ Exception -> 0x03ef }
            int r3 = r3.size()     // Catch:{ Exception -> 0x03ef }
            r5 = 1
            int r3 = r3 - r5
            java.lang.Object r3 = r11.get(r3)     // Catch:{ Exception -> 0x03ef }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x03ef }
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.location     // Catch:{ Exception -> 0x03ef }
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r5)     // Catch:{ Exception -> 0x03ef }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x03ef }
            r10.attachPath = r3     // Catch:{ Exception -> 0x03ef }
        L_0x03ec:
            r3 = r6
            r5 = r10
            goto L_0x041b
        L_0x03ef:
            r0 = move-exception
            r1 = r0
            r11 = r6
        L_0x03f2:
            r5 = r10
            r4 = 0
            r10 = r45
            goto L_0x1933
        L_0x03f8:
            r3 = r53
            r2 = r57
            r5 = r7
            if (r3 == 0) goto L_0x0429
            org.telegram.tgnet.TLRPC$TL_message r7 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0298 }
            r7.<init>()     // Catch:{ Exception -> 0x0298 }
            org.telegram.tgnet.TLRPC$TL_messageMediaGame r10 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x0421 }
            r10.<init>()     // Catch:{ Exception -> 0x0421 }
            r7.media = r10     // Catch:{ Exception -> 0x0421 }
            r10.game = r3     // Catch:{ Exception -> 0x0421 }
            if (r12 == 0) goto L_0x0419
            boolean r3 = r12.containsKey(r1)     // Catch:{ Exception -> 0x0421 }
            if (r3 == 0) goto L_0x0419
            r3 = r6
            r5 = r7
            goto L_0x035b
        L_0x0419:
            r3 = r6
            r5 = r7
        L_0x041b:
            r7 = r18
            r6 = r30
            goto L_0x0600
        L_0x0421:
            r0 = move-exception
            r10 = r45
            r1 = r0
            r11 = r6
            r5 = r7
            goto L_0x0295
        L_0x0429:
            r7 = r51
            r3 = 0
            if (r7 == 0) goto L_0x04cc
            if (r9 == 0) goto L_0x043f
            org.telegram.tgnet.TLRPC$TL_message_secret r10 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0436 }
            r10.<init>()     // Catch:{ Exception -> 0x0436 }
            goto L_0x0444
        L_0x0436:
            r0 = move-exception
            r10 = r45
            r1 = r0
            r4 = r3
            r5 = r4
            r11 = r6
            goto L_0x1933
        L_0x043f:
            org.telegram.tgnet.TLRPC$TL_message r10 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x04c4 }
            r10.<init>()     // Catch:{ Exception -> 0x04c4 }
        L_0x0444:
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r11 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact     // Catch:{ Exception -> 0x04be }
            r11.<init>()     // Catch:{ Exception -> 0x04be }
            r10.media = r11     // Catch:{ Exception -> 0x04be }
            java.lang.String r3 = r7.phone     // Catch:{ Exception -> 0x04be }
            r11.phone_number = r3     // Catch:{ Exception -> 0x04be }
            java.lang.String r3 = r7.first_name     // Catch:{ Exception -> 0x04be }
            r11.first_name = r3     // Catch:{ Exception -> 0x04be }
            java.lang.String r3 = r7.last_name     // Catch:{ Exception -> 0x04be }
            r11.last_name = r3     // Catch:{ Exception -> 0x04be }
            int r3 = r7.id     // Catch:{ Exception -> 0x04be }
            r11.user_id = r3     // Catch:{ Exception -> 0x04be }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r3 = r7.restriction_reason     // Catch:{ Exception -> 0x04be }
            boolean r3 = r3.isEmpty()     // Catch:{ Exception -> 0x04be }
            if (r3 != 0) goto L_0x0488
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r3 = r7.restriction_reason     // Catch:{ Exception -> 0x04be }
            r11 = 0
            java.lang.Object r3 = r3.get(r11)     // Catch:{ Exception -> 0x04be }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r3 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r3     // Catch:{ Exception -> 0x04be }
            java.lang.String r3 = r3.text     // Catch:{ Exception -> 0x04be }
            java.lang.String r11 = "BEGIN:VCARD"
            boolean r3 = r3.startsWith(r11)     // Catch:{ Exception -> 0x04be }
            if (r3 == 0) goto L_0x0488
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media     // Catch:{ Exception -> 0x04be }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r11 = r7.restriction_reason     // Catch:{ Exception -> 0x04be }
            r6 = 0
            java.lang.Object r11 = r11.get(r6)     // Catch:{ Exception -> 0x04be }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r11 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r11     // Catch:{ Exception -> 0x04be }
            java.lang.String r6 = r11.text     // Catch:{ Exception -> 0x04be }
            r3.vcard = r6     // Catch:{ Exception -> 0x04be }
            r6 = r30
            goto L_0x048e
        L_0x0488:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media     // Catch:{ Exception -> 0x04be }
            r6 = r30
            r3.vcard = r6     // Catch:{ Exception -> 0x04be }
        L_0x048e:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media     // Catch:{ Exception -> 0x04be }
            java.lang.String r3 = r3.first_name     // Catch:{ Exception -> 0x04be }
            if (r3 != 0) goto L_0x049a
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media     // Catch:{ Exception -> 0x04be }
            r3.first_name = r6     // Catch:{ Exception -> 0x04be }
            r7.first_name = r6     // Catch:{ Exception -> 0x04be }
        L_0x049a:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media     // Catch:{ Exception -> 0x04be }
            java.lang.String r3 = r3.last_name     // Catch:{ Exception -> 0x04be }
            if (r3 != 0) goto L_0x04a6
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media     // Catch:{ Exception -> 0x04be }
            r3.last_name = r6     // Catch:{ Exception -> 0x04be }
            r7.last_name = r6     // Catch:{ Exception -> 0x04be }
        L_0x04a6:
            if (r12 == 0) goto L_0x04b5
            boolean r3 = r12.containsKey(r1)     // Catch:{ Exception -> 0x04be }
            if (r3 == 0) goto L_0x04b5
            r3 = r66
            r5 = r10
            r7 = r18
            goto L_0x035f
        L_0x04b5:
            r3 = r66
            r5 = r10
            r7 = r18
            r19 = 6
            goto L_0x0600
        L_0x04be:
            r0 = move-exception
            r11 = r66
            r1 = r0
            goto L_0x03f2
        L_0x04c4:
            r0 = move-exception
            r10 = r45
            r11 = r66
            r1 = r0
            goto L_0x029d
        L_0x04cc:
            r10 = r52
            r3 = r6
            r6 = r30
            if (r10 == 0) goto L_0x05fd
            if (r9 == 0) goto L_0x04db
            org.telegram.tgnet.TLRPC$TL_message_secret r11 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x05f6 }
            r11.<init>()     // Catch:{ Exception -> 0x05f6 }
            goto L_0x04e0
        L_0x04db:
            org.telegram.tgnet.TLRPC$TL_message r11 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x05f6 }
            r11.<init>()     // Catch:{ Exception -> 0x05f6 }
        L_0x04e0:
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x05eb }
            r7.<init>()     // Catch:{ Exception -> 0x05eb }
            r11.media = r7     // Catch:{ Exception -> 0x05eb }
            int r8 = r7.flags     // Catch:{ Exception -> 0x05eb }
            r19 = 3
            r8 = r8 | 3
            r7.flags = r8     // Catch:{ Exception -> 0x05eb }
            if (r5 == 0) goto L_0x0505
            r7.ttl_seconds = r5     // Catch:{ Exception -> 0x04fc }
            r11.ttl = r5     // Catch:{ Exception -> 0x04fc }
            r19 = 4
            r8 = r8 | 4
            r7.flags = r8     // Catch:{ Exception -> 0x04fc }
            goto L_0x0505
        L_0x04fc:
            r0 = move-exception
            r10 = r45
            r1 = r0
            r5 = r11
            r4 = 0
        L_0x0502:
            r11 = r3
            goto L_0x1933
        L_0x0505:
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r11.media     // Catch:{ Exception -> 0x05eb }
            r7.document = r10     // Catch:{ Exception -> 0x05eb }
            if (r12 == 0) goto L_0x0514
            boolean r7 = r12.containsKey(r1)     // Catch:{ Exception -> 0x04fc }
            if (r7 == 0) goto L_0x0514
            r19 = 9
            goto L_0x0531
        L_0x0514:
            boolean r7 = org.telegram.messenger.MessageObject.isVideoDocument(r52)     // Catch:{ Exception -> 0x05eb }
            if (r7 != 0) goto L_0x052f
            boolean r7 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r52)     // Catch:{ Exception -> 0x04fc }
            if (r7 != 0) goto L_0x052f
            if (r50 == 0) goto L_0x0523
            goto L_0x052f
        L_0x0523:
            boolean r7 = org.telegram.messenger.MessageObject.isVoiceDocument(r52)     // Catch:{ Exception -> 0x04fc }
            if (r7 == 0) goto L_0x052c
            r19 = 8
            goto L_0x0531
        L_0x052c:
            r19 = 7
            goto L_0x0531
        L_0x052f:
            r19 = 3
        L_0x0531:
            if (r50 == 0) goto L_0x0546
            java.lang.String r7 = r50.getString()     // Catch:{ Exception -> 0x04fc }
            if (r12 != 0) goto L_0x053f
            java.util.HashMap r8 = new java.util.HashMap     // Catch:{ Exception -> 0x04fc }
            r8.<init>()     // Catch:{ Exception -> 0x04fc }
            goto L_0x0540
        L_0x053f:
            r8 = r12
        L_0x0540:
            java.lang.String r12 = "ve"
            r8.put(r12, r7)     // Catch:{ Exception -> 0x04fc }
            goto L_0x0547
        L_0x0546:
            r8 = r12
        L_0x0547:
            if (r9 == 0) goto L_0x055e
            int r7 = r10.dc_id     // Catch:{ Exception -> 0x04fc }
            if (r7 <= 0) goto L_0x055e
            boolean r7 = org.telegram.messenger.MessageObject.isStickerDocument(r52)     // Catch:{ Exception -> 0x04fc }
            if (r7 != 0) goto L_0x055e
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r52)     // Catch:{ Exception -> 0x04fc }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x04fc }
            r11.attachPath = r7     // Catch:{ Exception -> 0x04fc }
            goto L_0x0560
        L_0x055e:
            r11.attachPath = r2     // Catch:{ Exception -> 0x05eb }
        L_0x0560:
            if (r9 == 0) goto L_0x05e3
            boolean r7 = org.telegram.messenger.MessageObject.isStickerDocument(r52)     // Catch:{ Exception -> 0x05eb }
            if (r7 != 0) goto L_0x056f
            r7 = 0
            boolean r12 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r10, r7)     // Catch:{ Exception -> 0x04fc }
            if (r12 == 0) goto L_0x05e3
        L_0x056f:
            r7 = 0
        L_0x0570:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r10.attributes     // Catch:{ Exception -> 0x05eb }
            int r12 = r12.size()     // Catch:{ Exception -> 0x05eb }
            if (r7 >= r12) goto L_0x05e3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r10.attributes     // Catch:{ Exception -> 0x05eb }
            java.lang.Object r12 = r12.get(r7)     // Catch:{ Exception -> 0x05eb }
            org.telegram.tgnet.TLRPC$DocumentAttribute r12 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r12     // Catch:{ Exception -> 0x05eb }
            boolean r5 = r12 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker     // Catch:{ Exception -> 0x05eb }
            if (r5 == 0) goto L_0x05da
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r10.attributes     // Catch:{ Exception -> 0x05eb }
            r5.remove(r7)     // Catch:{ Exception -> 0x05eb }
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55 r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55     // Catch:{ Exception -> 0x05eb }
            r5.<init>()     // Catch:{ Exception -> 0x05eb }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r10.attributes     // Catch:{ Exception -> 0x05eb }
            r7.add(r5)     // Catch:{ Exception -> 0x05eb }
            java.lang.String r7 = r12.alt     // Catch:{ Exception -> 0x05eb }
            r5.alt = r7     // Catch:{ Exception -> 0x05eb }
            org.telegram.tgnet.TLRPC$InputStickerSet r7 = r12.stickerset     // Catch:{ Exception -> 0x05eb }
            if (r7 == 0) goto L_0x05ce
            org.telegram.tgnet.TLRPC$InputStickerSet r7 = r12.stickerset     // Catch:{ Exception -> 0x05eb }
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x05eb }
            if (r7 == 0) goto L_0x05a8
            org.telegram.tgnet.TLRPC$InputStickerSet r7 = r12.stickerset     // Catch:{ Exception -> 0x04fc }
            java.lang.String r7 = r7.short_name     // Catch:{ Exception -> 0x04fc }
            r22 = r11
            goto L_0x05b6
        L_0x05a8:
            org.telegram.messenger.MediaDataController r7 = r45.getMediaDataController()     // Catch:{ Exception -> 0x05eb }
            org.telegram.tgnet.TLRPC$InputStickerSet r12 = r12.stickerset     // Catch:{ Exception -> 0x05eb }
            r22 = r11
            long r10 = r12.id     // Catch:{ Exception -> 0x05d8 }
            java.lang.String r7 = r7.getStickerSetName(r10)     // Catch:{ Exception -> 0x05d8 }
        L_0x05b6:
            boolean r10 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x05d8 }
            if (r10 != 0) goto L_0x05c6
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r10 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x05d8 }
            r10.<init>()     // Catch:{ Exception -> 0x05d8 }
            r5.stickerset = r10     // Catch:{ Exception -> 0x05d8 }
            r10.short_name = r7     // Catch:{ Exception -> 0x05d8 }
            goto L_0x05e5
        L_0x05c6:
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r7 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x05d8 }
            r7.<init>()     // Catch:{ Exception -> 0x05d8 }
            r5.stickerset = r7     // Catch:{ Exception -> 0x05d8 }
            goto L_0x05e5
        L_0x05ce:
            r22 = r11
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r7 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x05d8 }
            r7.<init>()     // Catch:{ Exception -> 0x05d8 }
            r5.stickerset = r7     // Catch:{ Exception -> 0x05d8 }
            goto L_0x05e5
        L_0x05d8:
            r0 = move-exception
            goto L_0x05ee
        L_0x05da:
            r22 = r11
            int r7 = r7 + 1
            r10 = r52
            r5 = r67
            goto L_0x0570
        L_0x05e3:
            r22 = r11
        L_0x05e5:
            r12 = r8
            r7 = r18
            r5 = r22
            goto L_0x0600
        L_0x05eb:
            r0 = move-exception
            r22 = r11
        L_0x05ee:
            r10 = r45
            r1 = r0
            r11 = r3
            r5 = r22
            goto L_0x0295
        L_0x05f6:
            r0 = move-exception
            r10 = r45
            r1 = r0
            r11 = r3
            goto L_0x029d
        L_0x05fd:
            r7 = r18
            r5 = 0
        L_0x0600:
            if (r15 == 0) goto L_0x0618
            boolean r8 = r62.isEmpty()     // Catch:{ Exception -> 0x0611 }
            if (r8 != 0) goto L_0x0618
            r5.entities = r15     // Catch:{ Exception -> 0x0611 }
            int r8 = r5.flags     // Catch:{ Exception -> 0x0611 }
            r8 = r8 | 128(0x80, float:1.794E-43)
            r5.flags = r8     // Catch:{ Exception -> 0x0611 }
            goto L_0x0618
        L_0x0611:
            r0 = move-exception
            r10 = r45
            r1 = r0
            r11 = r3
            goto L_0x0295
        L_0x0618:
            if (r7 == 0) goto L_0x061d
            r5.message = r7     // Catch:{ Exception -> 0x0611 }
            goto L_0x0623
        L_0x061d:
            java.lang.String r8 = r5.message     // Catch:{ Exception -> 0x192c }
            if (r8 != 0) goto L_0x0623
            r5.message = r6     // Catch:{ Exception -> 0x0611 }
        L_0x0623:
            java.lang.String r8 = r5.attachPath     // Catch:{ Exception -> 0x192c }
            if (r8 != 0) goto L_0x0629
            r5.attachPath = r6     // Catch:{ Exception -> 0x0611 }
        L_0x0629:
            org.telegram.messenger.UserConfig r8 = r45.getUserConfig()     // Catch:{ Exception -> 0x192c }
            int r8 = r8.getNewMessageId()     // Catch:{ Exception -> 0x192c }
            r5.id = r8     // Catch:{ Exception -> 0x192c }
            r5.local_id = r8     // Catch:{ Exception -> 0x192c }
            r8 = 1
            r5.out = r8     // Catch:{ Exception -> 0x192c }
            if (r29 == 0) goto L_0x0644
            if (r26 == 0) goto L_0x0644
            r8 = r26
            int r10 = r8.channel_id     // Catch:{ Exception -> 0x0611 }
            int r10 = -r10
            r5.from_id = r10     // Catch:{ Exception -> 0x0611 }
            goto L_0x0656
        L_0x0644:
            r8 = r26
            org.telegram.messenger.UserConfig r10 = r45.getUserConfig()     // Catch:{ Exception -> 0x192c }
            int r10 = r10.getClientUserId()     // Catch:{ Exception -> 0x192c }
            r5.from_id = r10     // Catch:{ Exception -> 0x192c }
            int r10 = r5.flags     // Catch:{ Exception -> 0x192c }
            r10 = r10 | 256(0x100, float:3.59E-43)
            r5.flags = r10     // Catch:{ Exception -> 0x192c }
        L_0x0656:
            org.telegram.messenger.UserConfig r10 = r45.getUserConfig()     // Catch:{ Exception -> 0x192c }
            r11 = 0
            r10.saveConfig(r11)     // Catch:{ Exception -> 0x192c }
            r10 = r49
            r22 = r51
            r26 = r52
            r2 = r67
            r53 = r68
            r18 = r4
            r15 = r5
            r34 = r7
            r4 = r12
            r33 = r13
            r11 = r19
            r5 = r46
            r7 = r48
            r19 = r54
        L_0x0678:
            long r12 = r15.random_id     // Catch:{ Exception -> 0x1923 }
            int r27 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r27 != 0) goto L_0x068d
            long r12 = r45.getNextRandomId()     // Catch:{ Exception -> 0x0685 }
            r15.random_id = r12     // Catch:{ Exception -> 0x0685 }
            goto L_0x068d
        L_0x0685:
            r0 = move-exception
            r10 = r45
        L_0x0688:
            r1 = r0
            r11 = r3
            r5 = r15
            goto L_0x0295
        L_0x068d:
            java.lang.String r12 = "bot"
            java.lang.String r13 = "bot_name"
            if (r4 == 0) goto L_0x06c5
            boolean r27 = r4.containsKey(r12)     // Catch:{ Exception -> 0x0685 }
            if (r27 == 0) goto L_0x06c5
            if (r9 == 0) goto L_0x06ac
            java.lang.Object r27 = r4.get(r13)     // Catch:{ Exception -> 0x0685 }
            r46 = r13
            r13 = r27
            java.lang.String r13 = (java.lang.String) r13     // Catch:{ Exception -> 0x0685 }
            r15.via_bot_name = r13     // Catch:{ Exception -> 0x0685 }
            if (r13 != 0) goto L_0x06be
            r15.via_bot_name = r6     // Catch:{ Exception -> 0x0685 }
            goto L_0x06be
        L_0x06ac:
            r46 = r13
            java.lang.Object r13 = r4.get(r12)     // Catch:{ Exception -> 0x0685 }
            java.lang.CharSequence r13 = (java.lang.CharSequence) r13     // Catch:{ Exception -> 0x0685 }
            java.lang.Integer r13 = org.telegram.messenger.Utilities.parseInt(r13)     // Catch:{ Exception -> 0x0685 }
            int r13 = r13.intValue()     // Catch:{ Exception -> 0x0685 }
            r15.via_bot_id = r13     // Catch:{ Exception -> 0x0685 }
        L_0x06be:
            int r13 = r15.flags     // Catch:{ Exception -> 0x0685 }
            r13 = r13 | 2048(0x800, float:2.87E-42)
            r15.flags = r13     // Catch:{ Exception -> 0x0685 }
            goto L_0x06c7
        L_0x06c5:
            r46 = r13
        L_0x06c7:
            r15.params = r4     // Catch:{ Exception -> 0x1923 }
            if (r14 == 0) goto L_0x06d3
            boolean r13 = r14.resendAsIs     // Catch:{ Exception -> 0x0685 }
            if (r13 != 0) goto L_0x06d0
            goto L_0x06d3
        L_0x06d0:
            r48 = r10
            goto L_0x072a
        L_0x06d3:
            if (r3 == 0) goto L_0x06d7
            r13 = r3
            goto L_0x06df
        L_0x06d7:
            org.telegram.tgnet.ConnectionsManager r13 = r45.getConnectionsManager()     // Catch:{ Exception -> 0x1923 }
            int r13 = r13.getCurrentTime()     // Catch:{ Exception -> 0x1923 }
        L_0x06df:
            r15.date = r13     // Catch:{ Exception -> 0x1923 }
            boolean r13 = r8 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x1923 }
            if (r13 == 0) goto L_0x0725
            if (r3 != 0) goto L_0x06f2
            if (r29 == 0) goto L_0x06f2
            r13 = 1
            r15.views = r13     // Catch:{ Exception -> 0x0685 }
            int r13 = r15.flags     // Catch:{ Exception -> 0x0685 }
            r13 = r13 | 1024(0x400, float:1.435E-42)
            r15.flags = r13     // Catch:{ Exception -> 0x0685 }
        L_0x06f2:
            org.telegram.messenger.MessagesController r13 = r45.getMessagesController()     // Catch:{ Exception -> 0x0685 }
            r48 = r10
            int r10 = r8.channel_id     // Catch:{ Exception -> 0x0685 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0685 }
            org.telegram.tgnet.TLRPC$Chat r10 = r13.getChat(r10)     // Catch:{ Exception -> 0x0685 }
            if (r10 == 0) goto L_0x072a
            boolean r13 = r10.megagroup     // Catch:{ Exception -> 0x0685 }
            if (r13 == 0) goto L_0x0713
            int r10 = r15.flags     // Catch:{ Exception -> 0x0685 }
            r13 = -2147483648(0xfffffffvar_, float:-0.0)
            r10 = r10 | r13
            r15.flags = r10     // Catch:{ Exception -> 0x0685 }
            r13 = 1
            r15.unread = r13     // Catch:{ Exception -> 0x0685 }
            goto L_0x072a
        L_0x0713:
            r13 = 1
            r15.post = r13     // Catch:{ Exception -> 0x0685 }
            boolean r10 = r10.signatures     // Catch:{ Exception -> 0x0685 }
            if (r10 == 0) goto L_0x072a
            org.telegram.messenger.UserConfig r10 = r45.getUserConfig()     // Catch:{ Exception -> 0x0685 }
            int r10 = r10.getClientUserId()     // Catch:{ Exception -> 0x0685 }
            r15.from_id = r10     // Catch:{ Exception -> 0x0685 }
            goto L_0x072a
        L_0x0725:
            r48 = r10
            r10 = 1
            r15.unread = r10     // Catch:{ Exception -> 0x1923 }
        L_0x072a:
            int r10 = r15.flags     // Catch:{ Exception -> 0x1923 }
            r10 = r10 | 512(0x200, float:7.175E-43)
            r15.flags = r10     // Catch:{ Exception -> 0x1923 }
            r30 = r6
            r49 = r7
            r13 = 2
            r6 = r55
            r15.dialog_id = r6     // Catch:{ Exception -> 0x1923 }
            r13 = r58
            if (r13 == 0) goto L_0x076d
            if (r9 == 0) goto L_0x0759
            r27 = r1
            org.telegram.tgnet.TLRPC$Message r1 = r13.messageOwner     // Catch:{ Exception -> 0x0685 }
            r59 = r11
            r51 = r12
            long r11 = r1.random_id     // Catch:{ Exception -> 0x0685 }
            int r1 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x075f
            org.telegram.tgnet.TLRPC$Message r1 = r13.messageOwner     // Catch:{ Exception -> 0x0685 }
            long r11 = r1.random_id     // Catch:{ Exception -> 0x0685 }
            r15.reply_to_random_id = r11     // Catch:{ Exception -> 0x0685 }
            r1 = 8
            r10 = r10 | r1
            r15.flags = r10     // Catch:{ Exception -> 0x0685 }
            goto L_0x0766
        L_0x0759:
            r27 = r1
            r59 = r11
            r51 = r12
        L_0x075f:
            int r1 = r15.flags     // Catch:{ Exception -> 0x0685 }
            r10 = 8
            r1 = r1 | r10
            r15.flags = r1     // Catch:{ Exception -> 0x0685 }
        L_0x0766:
            int r1 = r58.getId()     // Catch:{ Exception -> 0x0685 }
            r15.reply_to_msg_id = r1     // Catch:{ Exception -> 0x0685 }
            goto L_0x0773
        L_0x076d:
            r27 = r1
            r59 = r11
            r51 = r12
        L_0x0773:
            r1 = r63
            if (r1 == 0) goto L_0x0781
            if (r9 != 0) goto L_0x0781
            int r10 = r15.flags     // Catch:{ Exception -> 0x0685 }
            r10 = r10 | 64
            r15.flags = r10     // Catch:{ Exception -> 0x0685 }
            r15.reply_markup = r1     // Catch:{ Exception -> 0x0685 }
        L_0x0781:
            if (r25 == 0) goto L_0x07b9
            org.telegram.messenger.MessagesController r1 = r45.getMessagesController()     // Catch:{ Exception -> 0x0685 }
            r10 = r25
            org.telegram.tgnet.TLRPC$Peer r1 = r1.getPeer(r10)     // Catch:{ Exception -> 0x0685 }
            r15.to_id = r1     // Catch:{ Exception -> 0x0685 }
            if (r10 <= 0) goto L_0x07b2
            org.telegram.messenger.MessagesController r1 = r45.getMessagesController()     // Catch:{ Exception -> 0x0685 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0685 }
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r10)     // Catch:{ Exception -> 0x0685 }
            if (r1 != 0) goto L_0x07a7
            int r1 = r15.id     // Catch:{ Exception -> 0x0685 }
            r10 = r45
            r10.processSentMessage(r1)     // Catch:{ Exception -> 0x0888 }
            return
        L_0x07a7:
            r11 = 6
            r10 = r45
            boolean r1 = r1.bot     // Catch:{ Exception -> 0x0888 }
            if (r1 == 0) goto L_0x07b4
            r1 = 0
            r15.unread = r1     // Catch:{ Exception -> 0x0888 }
            goto L_0x07b4
        L_0x07b2:
            r10 = r45
        L_0x07b4:
            r1 = r23
            r11 = 1
            goto L_0x0876
        L_0x07b9:
            r11 = 6
            r10 = r45
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x1921 }
            r1.<init>()     // Catch:{ Exception -> 0x1921 }
            r15.to_id = r1     // Catch:{ Exception -> 0x1921 }
            int r1 = r9.participant_id     // Catch:{ Exception -> 0x1921 }
            org.telegram.messenger.UserConfig r12 = r45.getUserConfig()     // Catch:{ Exception -> 0x1921 }
            int r12 = r12.getClientUserId()     // Catch:{ Exception -> 0x1921 }
            if (r1 != r12) goto L_0x07d6
            org.telegram.tgnet.TLRPC$Peer r1 = r15.to_id     // Catch:{ Exception -> 0x0888 }
            int r12 = r9.admin_id     // Catch:{ Exception -> 0x0888 }
            r1.user_id = r12     // Catch:{ Exception -> 0x0888 }
            goto L_0x07dc
        L_0x07d6:
            org.telegram.tgnet.TLRPC$Peer r1 = r15.to_id     // Catch:{ Exception -> 0x1921 }
            int r12 = r9.participant_id     // Catch:{ Exception -> 0x1921 }
            r1.user_id = r12     // Catch:{ Exception -> 0x1921 }
        L_0x07dc:
            if (r2 == 0) goto L_0x07e1
            r15.ttl = r2     // Catch:{ Exception -> 0x0888 }
            goto L_0x07f9
        L_0x07e1:
            int r1 = r9.ttl     // Catch:{ Exception -> 0x1921 }
            r15.ttl = r1     // Catch:{ Exception -> 0x1921 }
            if (r1 == 0) goto L_0x07f9
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r15.media     // Catch:{ Exception -> 0x0888 }
            if (r12 == 0) goto L_0x07f9
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r15.media     // Catch:{ Exception -> 0x0888 }
            r12.ttl_seconds = r1     // Catch:{ Exception -> 0x0888 }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r15.media     // Catch:{ Exception -> 0x0888 }
            int r12 = r1.flags     // Catch:{ Exception -> 0x0888 }
            r24 = 4
            r12 = r12 | 4
            r1.flags = r12     // Catch:{ Exception -> 0x0888 }
        L_0x07f9:
            int r1 = r15.ttl     // Catch:{ Exception -> 0x1921 }
            if (r1 == 0) goto L_0x07b4
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r15.media     // Catch:{ Exception -> 0x0888 }
            org.telegram.tgnet.TLRPC$Document r1 = r1.document     // Catch:{ Exception -> 0x0888 }
            if (r1 == 0) goto L_0x07b4
            boolean r1 = org.telegram.messenger.MessageObject.isVoiceMessage(r15)     // Catch:{ Exception -> 0x0888 }
            if (r1 == 0) goto L_0x083a
            r1 = 0
        L_0x080a:
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r15.media     // Catch:{ Exception -> 0x0888 }
            org.telegram.tgnet.TLRPC$Document r12 = r12.document     // Catch:{ Exception -> 0x0888 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r12.attributes     // Catch:{ Exception -> 0x0888 }
            int r12 = r12.size()     // Catch:{ Exception -> 0x0888 }
            if (r1 >= r12) goto L_0x082d
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r15.media     // Catch:{ Exception -> 0x0888 }
            org.telegram.tgnet.TLRPC$Document r12 = r12.document     // Catch:{ Exception -> 0x0888 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r12.attributes     // Catch:{ Exception -> 0x0888 }
            java.lang.Object r12 = r12.get(r1)     // Catch:{ Exception -> 0x0888 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r12 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r12     // Catch:{ Exception -> 0x0888 }
            boolean r11 = r12 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ Exception -> 0x0888 }
            if (r11 == 0) goto L_0x0829
            int r1 = r12.duration     // Catch:{ Exception -> 0x0888 }
            goto L_0x082e
        L_0x0829:
            int r1 = r1 + 1
            r11 = 6
            goto L_0x080a
        L_0x082d:
            r1 = 0
        L_0x082e:
            int r11 = r15.ttl     // Catch:{ Exception -> 0x0888 }
            r12 = 1
            int r1 = r1 + r12
            int r1 = java.lang.Math.max(r11, r1)     // Catch:{ Exception -> 0x0888 }
            r15.ttl = r1     // Catch:{ Exception -> 0x0888 }
            goto L_0x07b4
        L_0x083a:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoMessage(r15)     // Catch:{ Exception -> 0x0888 }
            if (r1 != 0) goto L_0x0846
            boolean r1 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r15)     // Catch:{ Exception -> 0x0888 }
            if (r1 == 0) goto L_0x07b4
        L_0x0846:
            r1 = 0
        L_0x0847:
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r15.media     // Catch:{ Exception -> 0x0888 }
            org.telegram.tgnet.TLRPC$Document r11 = r11.document     // Catch:{ Exception -> 0x0888 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r11 = r11.attributes     // Catch:{ Exception -> 0x0888 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0888 }
            if (r1 >= r11) goto L_0x0869
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r15.media     // Catch:{ Exception -> 0x0888 }
            org.telegram.tgnet.TLRPC$Document r11 = r11.document     // Catch:{ Exception -> 0x0888 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r11 = r11.attributes     // Catch:{ Exception -> 0x0888 }
            java.lang.Object r11 = r11.get(r1)     // Catch:{ Exception -> 0x0888 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r11 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r11     // Catch:{ Exception -> 0x0888 }
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x0888 }
            if (r12 == 0) goto L_0x0866
            int r1 = r11.duration     // Catch:{ Exception -> 0x0888 }
            goto L_0x086a
        L_0x0866:
            int r1 = r1 + 1
            goto L_0x0847
        L_0x0869:
            r1 = 0
        L_0x086a:
            int r11 = r15.ttl     // Catch:{ Exception -> 0x0888 }
            r12 = 1
            int r1 = r1 + r12
            int r1 = java.lang.Math.max(r11, r1)     // Catch:{ Exception -> 0x0888 }
            r15.ttl = r1     // Catch:{ Exception -> 0x0888 }
            goto L_0x07b4
        L_0x0876:
            if (r1 == r11) goto L_0x088b
            boolean r1 = org.telegram.messenger.MessageObject.isVoiceMessage(r15)     // Catch:{ Exception -> 0x0888 }
            if (r1 != 0) goto L_0x0884
            boolean r1 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r15)     // Catch:{ Exception -> 0x0888 }
            if (r1 == 0) goto L_0x088b
        L_0x0884:
            r1 = 1
            r15.media_unread = r1     // Catch:{ Exception -> 0x0888 }
            goto L_0x088c
        L_0x0888:
            r0 = move-exception
            goto L_0x0688
        L_0x088b:
            r1 = 1
        L_0x088c:
            r15.send_state = r1     // Catch:{ Exception -> 0x1921 }
            org.telegram.messenger.MessageObject r11 = new org.telegram.messenger.MessageObject     // Catch:{ Exception -> 0x1921 }
            int r12 = r10.currentAccount     // Catch:{ Exception -> 0x1921 }
            r11.<init>((int) r12, (org.telegram.tgnet.TLRPC$Message) r15, (org.telegram.messenger.MessageObject) r13, (boolean) r1)     // Catch:{ Exception -> 0x1921 }
            if (r3 == 0) goto L_0x0899
            r1 = 1
            goto L_0x089a
        L_0x0899:
            r1 = 0
        L_0x089a:
            r11.scheduled = r1     // Catch:{ Exception -> 0x191a }
            boolean r1 = r11.isForwarded()     // Catch:{ Exception -> 0x191a }
            if (r1 != 0) goto L_0x08c0
            int r1 = r11.type     // Catch:{ Exception -> 0x08ba }
            r12 = 3
            if (r1 == r12) goto L_0x08ae
            if (r50 != 0) goto L_0x08ae
            int r1 = r11.type     // Catch:{ Exception -> 0x08ba }
            r12 = 2
            if (r1 != r12) goto L_0x08c0
        L_0x08ae:
            java.lang.String r1 = r15.attachPath     // Catch:{ Exception -> 0x08ba }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x08ba }
            if (r1 != 0) goto L_0x08c0
            r1 = 1
            r11.attachPathExists = r1     // Catch:{ Exception -> 0x08ba }
            goto L_0x08c0
        L_0x08ba:
            r0 = move-exception
            r1 = r0
            r4 = r11
            r5 = r15
            goto L_0x0502
        L_0x08c0:
            org.telegram.messenger.VideoEditedInfo r1 = r11.videoEditedInfo     // Catch:{ Exception -> 0x191a }
            if (r1 == 0) goto L_0x08c9
            if (r50 != 0) goto L_0x08c9
            org.telegram.messenger.VideoEditedInfo r1 = r11.videoEditedInfo     // Catch:{ Exception -> 0x08ba }
            goto L_0x08cb
        L_0x08c9:
            r1 = r50
        L_0x08cb:
            if (r4 == 0) goto L_0x08fd
            java.lang.String r12 = "groupId"
            java.lang.Object r12 = r4.get(r12)     // Catch:{ Exception -> 0x08ba }
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ Exception -> 0x08ba }
            if (r12 == 0) goto L_0x08eb
            java.lang.Long r12 = org.telegram.messenger.Utilities.parseLong(r12)     // Catch:{ Exception -> 0x08ba }
            long r12 = r12.longValue()     // Catch:{ Exception -> 0x08ba }
            r15.grouped_id = r12     // Catch:{ Exception -> 0x08ba }
            r63 = r12
            int r12 = r15.flags     // Catch:{ Exception -> 0x08ba }
            r13 = 131072(0x20000, float:1.83671E-40)
            r12 = r12 | r13
            r15.flags = r12     // Catch:{ Exception -> 0x08ba }
            goto L_0x08ed
        L_0x08eb:
            r63 = r16
        L_0x08ed:
            java.lang.String r12 = "final"
            java.lang.Object r12 = r4.get(r12)     // Catch:{ Exception -> 0x08ba }
            if (r12 == 0) goto L_0x08f7
            r12 = 1
            goto L_0x08f8
        L_0x08f7:
            r12 = 0
        L_0x08f8:
            r23 = r12
            r12 = r63
            goto L_0x0901
        L_0x08fd:
            r12 = r16
            r23 = 0
        L_0x0901:
            int r25 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r25 != 0) goto L_0x0965
            r52 = r2
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x095b }
            r2.<init>()     // Catch:{ Exception -> 0x095b }
            r2.add(r11)     // Catch:{ Exception -> 0x095b }
            r50 = r1
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x095b }
            r1.<init>()     // Catch:{ Exception -> 0x095b }
            r1.add(r15)     // Catch:{ Exception -> 0x095b }
            r54 = r4
            int r4 = r10.currentAccount     // Catch:{ Exception -> 0x095b }
            org.telegram.messenger.MessagesStorage r35 = org.telegram.messenger.MessagesStorage.getInstance(r4)     // Catch:{ Exception -> 0x095b }
            r37 = 0
            r38 = 1
            r39 = 0
            r40 = 0
            if (r3 == 0) goto L_0x092e
            r41 = 1
            goto L_0x0930
        L_0x092e:
            r41 = 0
        L_0x0930:
            r36 = r1
            r35.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r36, (boolean) r37, (boolean) r38, (boolean) r39, (int) r40, (boolean) r41)     // Catch:{ Exception -> 0x095b }
            int r1 = r10.currentAccount     // Catch:{ Exception -> 0x095b }
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)     // Catch:{ Exception -> 0x095b }
            if (r3 == 0) goto L_0x093f
            r4 = 1
            goto L_0x0940
        L_0x093f:
            r4 = 0
        L_0x0940:
            r1.updateInterfaceWithMessages(r6, r2, r4)     // Catch:{ Exception -> 0x095b }
            if (r3 != 0) goto L_0x0956
            int r1 = r10.currentAccount     // Catch:{ Exception -> 0x095b }
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)     // Catch:{ Exception -> 0x095b }
            int r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload     // Catch:{ Exception -> 0x095b }
            r63 = r11
            r4 = 0
            java.lang.Object[] r11 = new java.lang.Object[r4]     // Catch:{ Exception -> 0x0990 }
            r1.postNotificationName(r2, r11)     // Catch:{ Exception -> 0x0990 }
            goto L_0x0958
        L_0x0956:
            r63 = r11
        L_0x0958:
            r1 = 0
            r2 = 0
            goto L_0x09b6
        L_0x095b:
            r0 = move-exception
            r63 = r11
        L_0x095e:
            r4 = r63
        L_0x0960:
            r1 = r0
            r11 = r3
            r5 = r15
            goto L_0x1933
        L_0x0965:
            r50 = r1
            r52 = r2
            r54 = r4
            r63 = r11
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1914 }
            r1.<init>()     // Catch:{ Exception -> 0x1914 }
            java.lang.String r2 = "group_"
            r1.append(r2)     // Catch:{ Exception -> 0x1914 }
            r1.append(r12)     // Catch:{ Exception -> 0x1914 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x1914 }
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r2 = r10.delayedMessages     // Catch:{ Exception -> 0x1914 }
            java.lang.Object r1 = r2.get(r1)     // Catch:{ Exception -> 0x1914 }
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch:{ Exception -> 0x1914 }
            if (r1 == 0) goto L_0x0992
            r2 = 0
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x0990 }
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r1     // Catch:{ Exception -> 0x0990 }
            goto L_0x0993
        L_0x0990:
            r0 = move-exception
            goto L_0x095e
        L_0x0992:
            r1 = 0
        L_0x0993:
            if (r1 != 0) goto L_0x09a6
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0990 }
            r1.<init>(r6)     // Catch:{ Exception -> 0x0990 }
            r1.initForGroup(r12)     // Catch:{ Exception -> 0x0990 }
            r1.encryptedChat = r9     // Catch:{ Exception -> 0x0990 }
            if (r3 == 0) goto L_0x09a3
            r2 = 1
            goto L_0x09a4
        L_0x09a3:
            r2 = 0
        L_0x09a4:
            r1.scheduled = r2     // Catch:{ Exception -> 0x0990 }
        L_0x09a6:
            r2 = 0
            r1.performMediaUpload = r2     // Catch:{ Exception -> 0x1914 }
            r2 = 0
            r1.photoSize = r2     // Catch:{ Exception -> 0x1914 }
            r1.videoEditedInfo = r2     // Catch:{ Exception -> 0x1914 }
            r1.httpLocation = r2     // Catch:{ Exception -> 0x1914 }
            if (r23 == 0) goto L_0x09b6
            int r4 = r15.id     // Catch:{ Exception -> 0x0990 }
            r1.finalGroupMessage = r4     // Catch:{ Exception -> 0x0990 }
        L_0x09b6:
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1914 }
            java.lang.String r11 = "silent_"
            if (r4 == 0) goto L_0x0a23
            if (r8 == 0) goto L_0x0a23
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0990 }
            r4.<init>()     // Catch:{ Exception -> 0x0990 }
            java.lang.String r2 = "send message user_id = "
            r4.append(r2)     // Catch:{ Exception -> 0x0990 }
            int r2 = r8.user_id     // Catch:{ Exception -> 0x0990 }
            r4.append(r2)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r2 = " chat_id = "
            r4.append(r2)     // Catch:{ Exception -> 0x0990 }
            int r2 = r8.chat_id     // Catch:{ Exception -> 0x0990 }
            r4.append(r2)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r2 = " channel_id = "
            r4.append(r2)     // Catch:{ Exception -> 0x0990 }
            int r2 = r8.channel_id     // Catch:{ Exception -> 0x0990 }
            r4.append(r2)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r2 = " access_hash = "
            r4.append(r2)     // Catch:{ Exception -> 0x0990 }
            r67 = r12
            long r12 = r8.access_hash     // Catch:{ Exception -> 0x0990 }
            r4.append(r12)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r2 = " notify = "
            r4.append(r2)     // Catch:{ Exception -> 0x0990 }
            r2 = r65
            r4.append(r2)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r12 = " silent = "
            r4.append(r12)     // Catch:{ Exception -> 0x0990 }
            int r12 = r10.currentAccount     // Catch:{ Exception -> 0x0990 }
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)     // Catch:{ Exception -> 0x0990 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0990 }
            r13.<init>()     // Catch:{ Exception -> 0x0990 }
            r13.append(r11)     // Catch:{ Exception -> 0x0990 }
            r13.append(r6)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0990 }
            r58 = r1
            r1 = 0
            boolean r12 = r12.getBoolean(r13, r1)     // Catch:{ Exception -> 0x0990 }
            r4.append(r12)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r1 = r4.toString()     // Catch:{ Exception -> 0x0990 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0990 }
            goto L_0x0a29
        L_0x0a23:
            r2 = r65
            r58 = r1
            r67 = r12
        L_0x0a29:
            if (r59 == 0) goto L_0x17dc
            r1 = r59
            r4 = 9
            if (r1 != r4) goto L_0x0a37
            if (r5 == 0) goto L_0x0a37
            if (r9 == 0) goto L_0x0a37
            goto L_0x17dc
        L_0x0a37:
            r4 = 1
            if (r1 < r4) goto L_0x0a42
            r4 = 3
            if (r1 <= r4) goto L_0x0a3e
            goto L_0x0a42
        L_0x0a3e:
            r12 = r54
            goto L_0x0bc8
        L_0x0a42:
            r4 = 5
            if (r1 < r4) goto L_0x0a49
            r4 = 8
            if (r1 <= r4) goto L_0x0a3e
        L_0x0a49:
            r4 = 9
            if (r1 != r4) goto L_0x0a4f
            if (r9 != 0) goto L_0x0a3e
        L_0x0a4f:
            r4 = 10
            if (r1 == r4) goto L_0x0a3e
            r4 = 11
            if (r1 != r4) goto L_0x0a58
            goto L_0x0a3e
        L_0x0a58:
            r4 = 4
            if (r1 != r4) goto L_0x0b2b
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r1 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages     // Catch:{ Exception -> 0x0990 }
            r1.<init>()     // Catch:{ Exception -> 0x0990 }
            r1.to_peer = r8     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0990 }
            boolean r4 = r4.with_my_score     // Catch:{ Exception -> 0x0990 }
            r1.with_my_score = r4     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0990 }
            int r4 = r4.ttl     // Catch:{ Exception -> 0x0990 }
            if (r4 == 0) goto L_0x0a94
            org.telegram.messenger.MessagesController r4 = r45.getMessagesController()     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0990 }
            int r5 = r5.ttl     // Catch:{ Exception -> 0x0990 }
            int r5 = -r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r5 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x0990 }
            r5.<init>()     // Catch:{ Exception -> 0x0990 }
            r1.from_peer = r5     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner     // Catch:{ Exception -> 0x0990 }
            int r8 = r8.ttl     // Catch:{ Exception -> 0x0990 }
            int r8 = -r8
            r5.channel_id = r8     // Catch:{ Exception -> 0x0990 }
            if (r4 == 0) goto L_0x0a9b
            long r8 = r4.access_hash     // Catch:{ Exception -> 0x0990 }
            r5.access_hash = r8     // Catch:{ Exception -> 0x0990 }
            goto L_0x0a9b
        L_0x0a94:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0990 }
            r4.<init>()     // Catch:{ Exception -> 0x0990 }
            r1.from_peer = r4     // Catch:{ Exception -> 0x0990 }
        L_0x0a9b:
            if (r2 == 0) goto L_0x0abc
            int r2 = r10.currentAccount     // Catch:{ Exception -> 0x0990 }
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)     // Catch:{ Exception -> 0x0990 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0990 }
            r4.<init>()     // Catch:{ Exception -> 0x0990 }
            r4.append(r11)     // Catch:{ Exception -> 0x0990 }
            r4.append(r6)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0990 }
            r5 = 0
            boolean r2 = r2.getBoolean(r4, r5)     // Catch:{ Exception -> 0x0990 }
            if (r2 == 0) goto L_0x0aba
            goto L_0x0abc
        L_0x0aba:
            r2 = 0
            goto L_0x0abd
        L_0x0abc:
            r2 = 1
        L_0x0abd:
            r1.silent = r2     // Catch:{ Exception -> 0x0990 }
            if (r3 == 0) goto L_0x0ac9
            r1.schedule_date = r3     // Catch:{ Exception -> 0x0990 }
            int r2 = r1.flags     // Catch:{ Exception -> 0x0990 }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r1.flags = r2     // Catch:{ Exception -> 0x0990 }
        L_0x0ac9:
            java.util.ArrayList<java.lang.Long> r2 = r1.random_id     // Catch:{ Exception -> 0x0990 }
            long r4 = r15.random_id     // Catch:{ Exception -> 0x0990 }
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x0990 }
            r2.add(r4)     // Catch:{ Exception -> 0x0990 }
            int r2 = r61.getId()     // Catch:{ Exception -> 0x0990 }
            if (r2 < 0) goto L_0x0ae8
            java.util.ArrayList<java.lang.Integer> r2 = r1.id     // Catch:{ Exception -> 0x0990 }
            int r4 = r61.getId()     // Catch:{ Exception -> 0x0990 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0990 }
            r2.add(r4)     // Catch:{ Exception -> 0x0990 }
            goto L_0x0b11
        L_0x0ae8:
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner     // Catch:{ Exception -> 0x0990 }
            int r2 = r2.fwd_msg_id     // Catch:{ Exception -> 0x0990 }
            if (r2 == 0) goto L_0x0afc
            java.util.ArrayList<java.lang.Integer> r2 = r1.id     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0990 }
            int r4 = r4.fwd_msg_id     // Catch:{ Exception -> 0x0990 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0990 }
            r2.add(r4)     // Catch:{ Exception -> 0x0990 }
            goto L_0x0b11
        L_0x0afc:
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from     // Catch:{ Exception -> 0x0990 }
            if (r2 == 0) goto L_0x0b11
            java.util.ArrayList<java.lang.Integer> r2 = r1.id     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r4.fwd_from     // Catch:{ Exception -> 0x0990 }
            int r4 = r4.channel_post     // Catch:{ Exception -> 0x0990 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0990 }
            r2.add(r4)     // Catch:{ Exception -> 0x0990 }
        L_0x0b11:
            r2 = 0
            r4 = 0
            if (r3 == 0) goto L_0x0b17
            r5 = 1
            goto L_0x0b18
        L_0x0b17:
            r5 = 0
        L_0x0b18:
            r46 = r45
            r47 = r1
            r48 = r63
            r49 = r2
            r50 = r4
            r51 = r53
            r52 = r5
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x0990 }
            goto L_0x1963
        L_0x0b2b:
            r4 = 9
            if (r1 != r4) goto L_0x1963
            org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult     // Catch:{ Exception -> 0x0990 }
            r1.<init>()     // Catch:{ Exception -> 0x0990 }
            r1.peer = r8     // Catch:{ Exception -> 0x0990 }
            long r4 = r15.random_id     // Catch:{ Exception -> 0x0990 }
            r1.random_id = r4     // Catch:{ Exception -> 0x0990 }
            r4 = r51
            r12 = r54
            boolean r4 = r12.containsKey(r4)     // Catch:{ Exception -> 0x0990 }
            if (r4 != 0) goto L_0x0b46
            r4 = 1
            goto L_0x0b47
        L_0x0b46:
            r4 = 0
        L_0x0b47:
            r1.hide_via = r4     // Catch:{ Exception -> 0x0990 }
            int r4 = r15.reply_to_msg_id     // Catch:{ Exception -> 0x0990 }
            if (r4 == 0) goto L_0x0b57
            int r4 = r1.flags     // Catch:{ Exception -> 0x0990 }
            r5 = 1
            r4 = r4 | r5
            r1.flags = r4     // Catch:{ Exception -> 0x0990 }
            int r4 = r15.reply_to_msg_id     // Catch:{ Exception -> 0x0990 }
            r1.reply_to_msg_id = r4     // Catch:{ Exception -> 0x0990 }
        L_0x0b57:
            if (r2 == 0) goto L_0x0b78
            int r2 = r10.currentAccount     // Catch:{ Exception -> 0x0990 }
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)     // Catch:{ Exception -> 0x0990 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0990 }
            r4.<init>()     // Catch:{ Exception -> 0x0990 }
            r4.append(r11)     // Catch:{ Exception -> 0x0990 }
            r4.append(r6)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0990 }
            r5 = 0
            boolean r2 = r2.getBoolean(r4, r5)     // Catch:{ Exception -> 0x0990 }
            if (r2 == 0) goto L_0x0b76
            goto L_0x0b78
        L_0x0b76:
            r2 = 0
            goto L_0x0b79
        L_0x0b78:
            r2 = 1
        L_0x0b79:
            r1.silent = r2     // Catch:{ Exception -> 0x0990 }
            if (r3 == 0) goto L_0x0b85
            r1.schedule_date = r3     // Catch:{ Exception -> 0x0990 }
            int r2 = r1.flags     // Catch:{ Exception -> 0x0990 }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r1.flags = r2     // Catch:{ Exception -> 0x0990 }
        L_0x0b85:
            r2 = r27
            java.lang.Object r2 = r12.get(r2)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0990 }
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r2)     // Catch:{ Exception -> 0x0990 }
            long r4 = r2.longValue()     // Catch:{ Exception -> 0x0990 }
            r1.query_id = r4     // Catch:{ Exception -> 0x0990 }
            java.lang.String r2 = "id"
            java.lang.Object r2 = r12.get(r2)     // Catch:{ Exception -> 0x0990 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0990 }
            r1.id = r2     // Catch:{ Exception -> 0x0990 }
            if (r14 != 0) goto L_0x0bae
            r2 = 1
            r1.clear_draft = r2     // Catch:{ Exception -> 0x0990 }
            org.telegram.messenger.MediaDataController r2 = r45.getMediaDataController()     // Catch:{ Exception -> 0x0990 }
            r4 = 0
            r2.cleanDraft(r6, r4)     // Catch:{ Exception -> 0x0990 }
        L_0x0bae:
            r2 = 0
            r4 = 0
            if (r3 == 0) goto L_0x0bb4
            r5 = 1
            goto L_0x0bb5
        L_0x0bb4:
            r5 = 0
        L_0x0bb5:
            r46 = r45
            r47 = r1
            r48 = r63
            r49 = r2
            r50 = r4
            r51 = r53
            r52 = r5
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x0990 }
            goto L_0x1963
        L_0x0bc8:
            if (r9 != 0) goto L_0x1210
            r4 = 1
            if (r1 != r4) goto L_0x0c2a
            r5 = r49
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x0990 }
            if (r4 == 0) goto L_0x0bed
            org.telegram.tgnet.TLRPC$TL_inputMediaVenue r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue     // Catch:{ Exception -> 0x0990 }
            r4.<init>()     // Catch:{ Exception -> 0x0990 }
            java.lang.String r9 = r5.address     // Catch:{ Exception -> 0x0990 }
            r4.address = r9     // Catch:{ Exception -> 0x0990 }
            java.lang.String r9 = r5.title     // Catch:{ Exception -> 0x0990 }
            r4.title = r9     // Catch:{ Exception -> 0x0990 }
            java.lang.String r9 = r5.provider     // Catch:{ Exception -> 0x0990 }
            r4.provider = r9     // Catch:{ Exception -> 0x0990 }
            java.lang.String r9 = r5.venue_id     // Catch:{ Exception -> 0x0990 }
            r4.venue_id = r9     // Catch:{ Exception -> 0x0990 }
            r13 = r30
            r4.venue_type = r13     // Catch:{ Exception -> 0x0990 }
            goto L_0x0CLASSNAME
        L_0x0bed:
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive     // Catch:{ Exception -> 0x0990 }
            if (r4 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive     // Catch:{ Exception -> 0x0990 }
            r4.<init>()     // Catch:{ Exception -> 0x0990 }
            int r9 = r5.period     // Catch:{ Exception -> 0x0990 }
            r4.period = r9     // Catch:{ Exception -> 0x0990 }
            int r9 = r4.flags     // Catch:{ Exception -> 0x0990 }
            r12 = 2
            r9 = r9 | r12
            r4.flags = r9     // Catch:{ Exception -> 0x0990 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint     // Catch:{ Exception -> 0x0990 }
            r4.<init>()     // Catch:{ Exception -> 0x0990 }
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r9 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint     // Catch:{ Exception -> 0x0990 }
            r9.<init>()     // Catch:{ Exception -> 0x0990 }
            r4.geo_point = r9     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$GeoPoint r12 = r5.geo     // Catch:{ Exception -> 0x0990 }
            double r12 = r12.lat     // Catch:{ Exception -> 0x0990 }
            r9.lat = r12     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$GeoPoint r5 = r5.geo     // Catch:{ Exception -> 0x0990 }
            double r12 = r5._long     // Catch:{ Exception -> 0x0990 }
            r9._long = r12     // Catch:{ Exception -> 0x0990 }
            r19 = r53
            r13 = r58
            r5 = r4
            r26 = r8
            r49 = r11
            r8 = r15
            r11 = r20
            r12 = 0
            r4 = r63
            goto L_0x0fe6
        L_0x0c2a:
            r13 = r30
            r4 = 2
            if (r1 == r4) goto L_0x0f0d
            r4 = 9
            if (r1 != r4) goto L_0x0CLASSNAME
            if (r48 == 0) goto L_0x0CLASSNAME
            goto L_0x0f0d
        L_0x0CLASSNAME:
            r4 = 3
            if (r1 != r4) goto L_0x0d01
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0990 }
            r4.<init>()     // Catch:{ Exception -> 0x0990 }
            r5 = r26
            java.lang.String r9 = r5.mime_type     // Catch:{ Exception -> 0x0990 }
            r4.mime_type = r9     // Catch:{ Exception -> 0x0990 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r9 = r5.attributes     // Catch:{ Exception -> 0x0990 }
            r4.attributes = r9     // Catch:{ Exception -> 0x0990 }
            boolean r9 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r5)     // Catch:{ Exception -> 0x0990 }
            if (r9 != 0) goto L_0x0c6b
            if (r50 == 0) goto L_0x0c5c
            r9 = r50
            boolean r12 = r9.muted     // Catch:{ Exception -> 0x0990 }
            if (r12 != 0) goto L_0x0c6d
            boolean r12 = r9.roundVideo     // Catch:{ Exception -> 0x0990 }
            if (r12 != 0) goto L_0x0c6d
            goto L_0x0c5e
        L_0x0c5c:
            r9 = r50
        L_0x0c5e:
            r12 = 1
            r4.nosound_video = r12     // Catch:{ Exception -> 0x0990 }
            boolean r12 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0990 }
            if (r12 == 0) goto L_0x0c6d
            java.lang.String r12 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r12)     // Catch:{ Exception -> 0x0990 }
            goto L_0x0c6d
        L_0x0c6b:
            r9 = r50
        L_0x0c6d:
            if (r52 == 0) goto L_0x0c7b
            r14 = r52
            r4.ttl_seconds = r14     // Catch:{ Exception -> 0x0990 }
            r15.ttl = r14     // Catch:{ Exception -> 0x0990 }
            int r12 = r4.flags     // Catch:{ Exception -> 0x0990 }
            r13 = 2
            r12 = r12 | r13
            r4.flags = r12     // Catch:{ Exception -> 0x0990 }
        L_0x0c7b:
            long r12 = r5.access_hash     // Catch:{ Exception -> 0x0990 }
            int r14 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x0CLASSNAME
            r49 = r11
            r12 = 1
            r11 = r4
            goto L_0x0cac
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r12 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0990 }
            r12.<init>()     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r13 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0990 }
            r13.<init>()     // Catch:{ Exception -> 0x0990 }
            r12.id = r13     // Catch:{ Exception -> 0x0990 }
            r49 = r11
            r46 = r12
            long r11 = r5.id     // Catch:{ Exception -> 0x0990 }
            r13.id = r11     // Catch:{ Exception -> 0x0990 }
            long r11 = r5.access_hash     // Catch:{ Exception -> 0x0990 }
            r13.access_hash = r11     // Catch:{ Exception -> 0x0990 }
            byte[] r11 = r5.file_reference     // Catch:{ Exception -> 0x0990 }
            r13.file_reference = r11     // Catch:{ Exception -> 0x0990 }
            if (r11 != 0) goto L_0x0ca9
            r11 = 0
            byte[] r12 = new byte[r11]     // Catch:{ Exception -> 0x0990 }
            r13.file_reference = r12     // Catch:{ Exception -> 0x0990 }
        L_0x0ca9:
            r11 = r46
            r12 = 0
        L_0x0cac:
            if (r58 != 0) goto L_0x0cd3
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r13 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0990 }
            r13.<init>(r6)     // Catch:{ Exception -> 0x0990 }
            r14 = 1
            r13.type = r14     // Catch:{ Exception -> 0x0990 }
            r14 = r63
            r13.obj = r14     // Catch:{ Exception -> 0x0cce }
            r46 = r11
            r11 = r20
            r13.originalPath = r11     // Catch:{ Exception -> 0x0cce }
            r2 = r53
            r13.parentObject = r2     // Catch:{ Exception -> 0x0cce }
            r63 = r14
            if (r3 == 0) goto L_0x0cca
            r14 = 1
            goto L_0x0ccb
        L_0x0cca:
            r14 = 0
        L_0x0ccb:
            r13.scheduled = r14     // Catch:{ Exception -> 0x0990 }
            goto L_0x0cdb
        L_0x0cce:
            r0 = move-exception
            r63 = r14
            goto L_0x095e
        L_0x0cd3:
            r2 = r53
            r46 = r11
            r11 = r20
            r13 = r58
        L_0x0cdb:
            r13.inputUploadMedia = r4     // Catch:{ Exception -> 0x0990 }
            r13.performMediaUpload = r12     // Catch:{ Exception -> 0x0990 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r5.thumbs     // Catch:{ Exception -> 0x0990 }
            boolean r4 = r4.isEmpty()     // Catch:{ Exception -> 0x0990 }
            if (r4 != 0) goto L_0x0cf4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r5.thumbs     // Catch:{ Exception -> 0x0990 }
            r14 = 0
            java.lang.Object r4 = r4.get(r14)     // Catch:{ Exception -> 0x0990 }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = (org.telegram.tgnet.TLRPC$PhotoSize) r4     // Catch:{ Exception -> 0x0990 }
            r13.photoSize = r4     // Catch:{ Exception -> 0x0990 }
            r13.locationParent = r5     // Catch:{ Exception -> 0x0990 }
        L_0x0cf4:
            r13.videoEditedInfo = r9     // Catch:{ Exception -> 0x0990 }
            r5 = r46
            r4 = r63
            r19 = r2
            r26 = r8
            r8 = r15
            goto L_0x0fe6
        L_0x0d01:
            r14 = r52
            r2 = r53
            r4 = r63
            r49 = r11
            r11 = r20
            r5 = r26
            r9 = 6
            if (r1 != r9) goto L_0x0d5a
            org.telegram.tgnet.TLRPC$TL_inputMediaContact r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact     // Catch:{ Exception -> 0x0d57 }
            r5.<init>()     // Catch:{ Exception -> 0x0d57 }
            r9 = r22
            java.lang.String r12 = r9.phone     // Catch:{ Exception -> 0x0d57 }
            r5.phone_number = r12     // Catch:{ Exception -> 0x0d57 }
            java.lang.String r12 = r9.first_name     // Catch:{ Exception -> 0x0d57 }
            r5.first_name = r12     // Catch:{ Exception -> 0x0d57 }
            java.lang.String r12 = r9.last_name     // Catch:{ Exception -> 0x0d57 }
            r5.last_name = r12     // Catch:{ Exception -> 0x0d57 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r12 = r9.restriction_reason     // Catch:{ Exception -> 0x0d57 }
            boolean r12 = r12.isEmpty()     // Catch:{ Exception -> 0x0d57 }
            if (r12 != 0) goto L_0x0d4c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r12 = r9.restriction_reason     // Catch:{ Exception -> 0x0d57 }
            r14 = 0
            java.lang.Object r12 = r12.get(r14)     // Catch:{ Exception -> 0x0d57 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r12 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r12     // Catch:{ Exception -> 0x0d57 }
            java.lang.String r12 = r12.text     // Catch:{ Exception -> 0x0d57 }
            java.lang.String r14 = "BEGIN:VCARD"
            boolean r12 = r12.startsWith(r14)     // Catch:{ Exception -> 0x0d57 }
            if (r12 == 0) goto L_0x0d4c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r9 = r9.restriction_reason     // Catch:{ Exception -> 0x0d57 }
            r12 = 0
            java.lang.Object r9 = r9.get(r12)     // Catch:{ Exception -> 0x0d57 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r9 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r9     // Catch:{ Exception -> 0x0d57 }
            java.lang.String r9 = r9.text     // Catch:{ Exception -> 0x0d57 }
            r5.vcard = r9     // Catch:{ Exception -> 0x0d57 }
            goto L_0x0d4e
        L_0x0d4c:
            r5.vcard = r13     // Catch:{ Exception -> 0x0d57 }
        L_0x0d4e:
            r13 = r58
            r19 = r2
            r26 = r8
            r8 = r15
            goto L_0x0e33
        L_0x0d57:
            r0 = move-exception
            goto L_0x0960
        L_0x0d5a:
            r9 = 7
            if (r1 == r9) goto L_0x0e36
            r9 = 9
            if (r1 != r9) goto L_0x0d63
            goto L_0x0e36
        L_0x0d63:
            r9 = 8
            if (r1 != r9) goto L_0x0dd2
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r9 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0dcd }
            r9.<init>()     // Catch:{ Exception -> 0x0dcd }
            java.lang.String r12 = r5.mime_type     // Catch:{ Exception -> 0x0dcd }
            r9.mime_type = r12     // Catch:{ Exception -> 0x0dcd }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r5.attributes     // Catch:{ Exception -> 0x0dcd }
            r9.attributes = r12     // Catch:{ Exception -> 0x0dcd }
            if (r14 == 0) goto L_0x0d80
            r9.ttl_seconds = r14     // Catch:{ Exception -> 0x0d57 }
            r15.ttl = r14     // Catch:{ Exception -> 0x0d57 }
            int r12 = r9.flags     // Catch:{ Exception -> 0x0d57 }
            r13 = 2
            r12 = r12 | r13
            r9.flags = r12     // Catch:{ Exception -> 0x0d57 }
        L_0x0d80:
            long r12 = r5.access_hash     // Catch:{ Exception -> 0x0dcd }
            int r14 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x0d8b
            r5 = r9
            r20 = r15
            r12 = 1
            goto L_0x0dae
        L_0x0d8b:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r12 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0dcd }
            r12.<init>()     // Catch:{ Exception -> 0x0dcd }
            org.telegram.tgnet.TLRPC$TL_inputDocument r13 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0dcd }
            r13.<init>()     // Catch:{ Exception -> 0x0dcd }
            r12.id = r13     // Catch:{ Exception -> 0x0dcd }
            r20 = r15
            long r14 = r5.id     // Catch:{ Exception -> 0x0var_ }
            r13.id = r14     // Catch:{ Exception -> 0x0var_ }
            long r14 = r5.access_hash     // Catch:{ Exception -> 0x0var_ }
            r13.access_hash = r14     // Catch:{ Exception -> 0x0var_ }
            byte[] r5 = r5.file_reference     // Catch:{ Exception -> 0x0var_ }
            r13.file_reference = r5     // Catch:{ Exception -> 0x0var_ }
            if (r5 != 0) goto L_0x0dac
            r5 = 0
            byte[] r14 = new byte[r5]     // Catch:{ Exception -> 0x0var_ }
            r13.file_reference = r14     // Catch:{ Exception -> 0x0var_ }
        L_0x0dac:
            r5 = r12
            r12 = 0
        L_0x0dae:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r13 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0var_ }
            r13.<init>(r6)     // Catch:{ Exception -> 0x0var_ }
            r14 = 3
            r13.type = r14     // Catch:{ Exception -> 0x0var_ }
            r13.obj = r4     // Catch:{ Exception -> 0x0var_ }
            r13.parentObject = r2     // Catch:{ Exception -> 0x0var_ }
            r13.inputUploadMedia = r9     // Catch:{ Exception -> 0x0var_ }
            r13.performMediaUpload = r12     // Catch:{ Exception -> 0x0var_ }
            if (r3 == 0) goto L_0x0dc2
            r9 = 1
            goto L_0x0dc3
        L_0x0dc2:
            r9 = 0
        L_0x0dc3:
            r13.scheduled = r9     // Catch:{ Exception -> 0x0var_ }
            r19 = r2
            r26 = r8
            r8 = r20
            goto L_0x0fe6
        L_0x0dcd:
            r0 = move-exception
            r20 = r15
            goto L_0x0var_
        L_0x0dd2:
            r20 = r15
            r5 = 10
            if (r1 != r5) goto L_0x0e18
            org.telegram.tgnet.TLRPC$TL_inputMediaPoll r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll     // Catch:{ Exception -> 0x0var_ }
            r5.<init>()     // Catch:{ Exception -> 0x0var_ }
            r9 = r19
            org.telegram.tgnet.TLRPC$TL_poll r9 = r9.poll     // Catch:{ Exception -> 0x0var_ }
            r5.poll = r9     // Catch:{ Exception -> 0x0var_ }
            if (r12 == 0) goto L_0x0e21
            java.lang.String r9 = "answers"
            boolean r9 = r12.containsKey(r9)     // Catch:{ Exception -> 0x0var_ }
            if (r9 == 0) goto L_0x0e21
            java.lang.String r9 = "answers"
            java.lang.Object r9 = r12.get(r9)     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ Exception -> 0x0var_ }
            byte[] r9 = org.telegram.messenger.Utilities.hexToBytes(r9)     // Catch:{ Exception -> 0x0var_ }
            int r12 = r9.length     // Catch:{ Exception -> 0x0var_ }
            if (r12 <= 0) goto L_0x0e21
            r12 = 0
        L_0x0dfd:
            int r13 = r9.length     // Catch:{ Exception -> 0x0var_ }
            if (r12 >= r13) goto L_0x0e11
            java.util.ArrayList<byte[]> r13 = r5.correct_answers     // Catch:{ Exception -> 0x0var_ }
            r14 = 1
            byte[] r15 = new byte[r14]     // Catch:{ Exception -> 0x0var_ }
            byte r14 = r9[r12]     // Catch:{ Exception -> 0x0var_ }
            r18 = 0
            r15[r18] = r14     // Catch:{ Exception -> 0x0var_ }
            r13.add(r15)     // Catch:{ Exception -> 0x0var_ }
            int r12 = r12 + 1
            goto L_0x0dfd
        L_0x0e11:
            int r9 = r5.flags     // Catch:{ Exception -> 0x0var_ }
            r12 = 1
            r9 = r9 | r12
            r5.flags = r9     // Catch:{ Exception -> 0x0var_ }
            goto L_0x0e21
        L_0x0e18:
            r5 = 11
            if (r1 != r5) goto L_0x0e2a
            org.telegram.tgnet.TLRPC$TL_inputMediaDice r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDice     // Catch:{ Exception -> 0x0var_ }
            r5.<init>()     // Catch:{ Exception -> 0x0var_ }
        L_0x0e21:
            r13 = r58
            r19 = r2
            r26 = r8
            r8 = r20
            goto L_0x0e33
        L_0x0e2a:
            r13 = r58
            r19 = r2
            r26 = r8
            r8 = r20
            r5 = 0
        L_0x0e33:
            r12 = 0
            goto L_0x0fe6
        L_0x0e36:
            r20 = r15
            r15 = r57
            r9 = r14
            if (r11 != 0) goto L_0x0e4c
            if (r15 != 0) goto L_0x0e4c
            long r13 = r5.access_hash     // Catch:{ Exception -> 0x0var_ }
            int r18 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x0e46
            goto L_0x0e4c
        L_0x0e46:
            r26 = r8
            r14 = r20
            r13 = 0
            goto L_0x0e97
        L_0x0e4c:
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r13 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0var_ }
            r13.<init>()     // Catch:{ Exception -> 0x0var_ }
            if (r9 == 0) goto L_0x0e6c
            r13.ttl_seconds = r9     // Catch:{ Exception -> 0x0e64 }
            r14 = r20
            r14.ttl = r9     // Catch:{ Exception -> 0x0e62 }
            int r9 = r13.flags     // Catch:{ Exception -> 0x0e62 }
            r18 = 2
            r9 = r9 | 2
            r13.flags = r9     // Catch:{ Exception -> 0x0e62 }
            goto L_0x0e6e
        L_0x0e62:
            r0 = move-exception
            goto L_0x0e67
        L_0x0e64:
            r0 = move-exception
            r14 = r20
        L_0x0e67:
            r1 = r0
            r11 = r3
            r5 = r14
            goto L_0x1933
        L_0x0e6c:
            r14 = r20
        L_0x0e6e:
            boolean r9 = android.text.TextUtils.isEmpty(r57)     // Catch:{ Exception -> 0x0var_ }
            if (r9 != 0) goto L_0x0e8d
            java.lang.String r9 = r57.toLowerCase()     // Catch:{ Exception -> 0x0e62 }
            java.lang.String r15 = "mp4"
            boolean r9 = r9.endsWith(r15)     // Catch:{ Exception -> 0x0e62 }
            if (r9 == 0) goto L_0x0e8d
            if (r12 == 0) goto L_0x0e8a
            java.lang.String r9 = "forceDocument"
            boolean r9 = r12.containsKey(r9)     // Catch:{ Exception -> 0x0e62 }
            if (r9 == 0) goto L_0x0e8d
        L_0x0e8a:
            r9 = 1
            r13.nosound_video = r9     // Catch:{ Exception -> 0x0e62 }
        L_0x0e8d:
            java.lang.String r9 = r5.mime_type     // Catch:{ Exception -> 0x0var_ }
            r13.mime_type = r9     // Catch:{ Exception -> 0x0var_ }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r9 = r5.attributes     // Catch:{ Exception -> 0x0var_ }
            r13.attributes = r9     // Catch:{ Exception -> 0x0var_ }
            r26 = r8
        L_0x0e97:
            long r8 = r5.access_hash     // Catch:{ Exception -> 0x0var_ }
            int r12 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r12 != 0) goto L_0x0ea4
            boolean r8 = r13 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0e62 }
            r9 = r8
            r8 = r13
            r20 = r14
            goto L_0x0ec6
        L_0x0ea4:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0var_ }
            r8.<init>()     // Catch:{ Exception -> 0x0var_ }
            org.telegram.tgnet.TLRPC$TL_inputDocument r9 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0var_ }
            r9.<init>()     // Catch:{ Exception -> 0x0var_ }
            r8.id = r9     // Catch:{ Exception -> 0x0var_ }
            r20 = r14
            long r14 = r5.id     // Catch:{ Exception -> 0x0var_ }
            r9.id = r14     // Catch:{ Exception -> 0x0var_ }
            long r14 = r5.access_hash     // Catch:{ Exception -> 0x0var_ }
            r9.access_hash = r14     // Catch:{ Exception -> 0x0var_ }
            byte[] r12 = r5.file_reference     // Catch:{ Exception -> 0x0var_ }
            r9.file_reference = r12     // Catch:{ Exception -> 0x0var_ }
            if (r12 != 0) goto L_0x0ec5
            r12 = 0
            byte[] r14 = new byte[r12]     // Catch:{ Exception -> 0x0var_ }
            r9.file_reference = r14     // Catch:{ Exception -> 0x0var_ }
        L_0x0ec5:
            r9 = 0
        L_0x0ec6:
            if (r13 == 0) goto L_0x0ef7
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r12 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0var_ }
            r12.<init>(r6)     // Catch:{ Exception -> 0x0var_ }
            r12.originalPath = r11     // Catch:{ Exception -> 0x0var_ }
            r14 = 2
            r12.type = r14     // Catch:{ Exception -> 0x0var_ }
            r12.obj = r4     // Catch:{ Exception -> 0x0var_ }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r5.thumbs     // Catch:{ Exception -> 0x0var_ }
            boolean r14 = r14.isEmpty()     // Catch:{ Exception -> 0x0var_ }
            if (r14 != 0) goto L_0x0ee9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r5.thumbs     // Catch:{ Exception -> 0x0var_ }
            r15 = 0
            java.lang.Object r14 = r14.get(r15)     // Catch:{ Exception -> 0x0var_ }
            org.telegram.tgnet.TLRPC$PhotoSize r14 = (org.telegram.tgnet.TLRPC$PhotoSize) r14     // Catch:{ Exception -> 0x0var_ }
            r12.photoSize = r14     // Catch:{ Exception -> 0x0var_ }
            r12.locationParent = r5     // Catch:{ Exception -> 0x0var_ }
        L_0x0ee9:
            r12.parentObject = r2     // Catch:{ Exception -> 0x0var_ }
            r12.inputUploadMedia = r13     // Catch:{ Exception -> 0x0var_ }
            r12.performMediaUpload = r9     // Catch:{ Exception -> 0x0var_ }
            if (r3 == 0) goto L_0x0ef3
            r5 = 1
            goto L_0x0ef4
        L_0x0ef3:
            r5 = 0
        L_0x0ef4:
            r12.scheduled = r5     // Catch:{ Exception -> 0x0var_ }
            goto L_0x0ef9
        L_0x0ef7:
            r12 = r58
        L_0x0ef9:
            r19 = r2
            r5 = r8
            r13 = r12
            r8 = r20
            r12 = r9
            goto L_0x0fe6
        L_0x0var_:
            r0 = move-exception
            r20 = r14
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
        L_0x0var_:
            r1 = r0
            r11 = r3
        L_0x0var_:
            r5 = r20
            goto L_0x1933
        L_0x0f0d:
            r9 = r52
            r2 = r53
            r4 = r63
            r26 = r8
            r49 = r11
            r11 = r20
            r20 = r15
            r15 = r57
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x1208 }
            r5.<init>()     // Catch:{ Exception -> 0x1208 }
            if (r9 == 0) goto L_0x0var_
            r5.ttl_seconds = r9     // Catch:{ Exception -> 0x1208 }
            r8 = r20
            r8.ttl = r9     // Catch:{ Exception -> 0x1206 }
            int r9 = r5.flags     // Catch:{ Exception -> 0x1206 }
            r13 = 2
            r9 = r9 | r13
            r5.flags = r9     // Catch:{ Exception -> 0x1206 }
            goto L_0x0var_
        L_0x0var_:
            r8 = r20
        L_0x0var_:
            if (r12 == 0) goto L_0x0var_
            java.lang.String r9 = "masks"
            java.lang.Object r9 = r12.get(r9)     // Catch:{ Exception -> 0x1206 }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ Exception -> 0x1206 }
            if (r9 == 0) goto L_0x0var_
            org.telegram.tgnet.SerializedData r12 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x1206 }
            byte[] r9 = org.telegram.messenger.Utilities.hexToBytes(r9)     // Catch:{ Exception -> 0x1206 }
            r12.<init>((byte[]) r9)     // Catch:{ Exception -> 0x1206 }
            r9 = 0
            int r13 = r12.readInt32(r9)     // Catch:{ Exception -> 0x1206 }
            r14 = 0
        L_0x0f4e:
            if (r14 >= r13) goto L_0x0var_
            r19 = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r5.stickers     // Catch:{ Exception -> 0x1206 }
            r46 = r13
            int r13 = r12.readInt32(r9)     // Catch:{ Exception -> 0x1206 }
            org.telegram.tgnet.TLRPC$InputDocument r13 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r12, r13, r9)     // Catch:{ Exception -> 0x1206 }
            r2.add(r13)     // Catch:{ Exception -> 0x1206 }
            int r14 = r14 + 1
            r13 = r46
            r2 = r19
            r9 = 0
            goto L_0x0f4e
        L_0x0var_:
            r19 = r2
            int r2 = r5.flags     // Catch:{ Exception -> 0x1206 }
            r9 = 1
            r2 = r2 | r9
            r5.flags = r2     // Catch:{ Exception -> 0x1206 }
            r12.cleanup()     // Catch:{ Exception -> 0x1206 }
            goto L_0x0var_
        L_0x0var_:
            r19 = r2
        L_0x0var_:
            r2 = r48
            long r12 = r2.access_hash     // Catch:{ Exception -> 0x1206 }
            int r9 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r9 != 0) goto L_0x0var_
            r9 = r5
            r12 = 1
            goto L_0x0fa2
        L_0x0var_:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r9 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x1206 }
            r9.<init>()     // Catch:{ Exception -> 0x1206 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r12 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x1206 }
            r12.<init>()     // Catch:{ Exception -> 0x1206 }
            r9.id = r12     // Catch:{ Exception -> 0x1206 }
            long r13 = r2.id     // Catch:{ Exception -> 0x1206 }
            r12.id = r13     // Catch:{ Exception -> 0x1206 }
            long r13 = r2.access_hash     // Catch:{ Exception -> 0x1206 }
            r12.access_hash = r13     // Catch:{ Exception -> 0x1206 }
            byte[] r13 = r2.file_reference     // Catch:{ Exception -> 0x1206 }
            r12.file_reference = r13     // Catch:{ Exception -> 0x1206 }
            if (r13 != 0) goto L_0x0fa1
            r13 = 0
            byte[] r14 = new byte[r13]     // Catch:{ Exception -> 0x1206 }
            r12.file_reference = r14     // Catch:{ Exception -> 0x1206 }
        L_0x0fa1:
            r12 = 0
        L_0x0fa2:
            if (r58 != 0) goto L_0x0fb8
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r13 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1206 }
            r13.<init>(r6)     // Catch:{ Exception -> 0x1206 }
            r14 = 0
            r13.type = r14     // Catch:{ Exception -> 0x1206 }
            r13.obj = r4     // Catch:{ Exception -> 0x1206 }
            r13.originalPath = r11     // Catch:{ Exception -> 0x1206 }
            if (r3 == 0) goto L_0x0fb4
            r14 = 1
            goto L_0x0fb5
        L_0x0fb4:
            r14 = 0
        L_0x0fb5:
            r13.scheduled = r14     // Catch:{ Exception -> 0x1206 }
            goto L_0x0fba
        L_0x0fb8:
            r13 = r58
        L_0x0fba:
            r13.inputUploadMedia = r5     // Catch:{ Exception -> 0x1206 }
            r13.performMediaUpload = r12     // Catch:{ Exception -> 0x1206 }
            if (r15 == 0) goto L_0x0fd1
            int r5 = r57.length()     // Catch:{ Exception -> 0x1206 }
            if (r5 <= 0) goto L_0x0fd1
            r5 = r18
            boolean r5 = r15.startsWith(r5)     // Catch:{ Exception -> 0x1206 }
            if (r5 == 0) goto L_0x0fd1
            r13.httpLocation = r15     // Catch:{ Exception -> 0x1206 }
            goto L_0x0fe5
        L_0x0fd1:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r2.sizes     // Catch:{ Exception -> 0x1206 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r2.sizes     // Catch:{ Exception -> 0x1206 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x1206 }
            r15 = 1
            int r14 = r14 - r15
            java.lang.Object r5 = r5.get(r14)     // Catch:{ Exception -> 0x1206 }
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5     // Catch:{ Exception -> 0x1206 }
            r13.photoSize = r5     // Catch:{ Exception -> 0x1206 }
            r13.locationParent = r2     // Catch:{ Exception -> 0x1206 }
        L_0x0fe5:
            r5 = r9
        L_0x0fe6:
            int r2 = (r67 > r16 ? 1 : (r67 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x10a3
            org.telegram.tgnet.TLObject r2 = r13.sendRequest     // Catch:{ Exception -> 0x1206 }
            if (r2 == 0) goto L_0x0ff7
            org.telegram.tgnet.TLObject r2 = r13.sendRequest     // Catch:{ Exception -> 0x1206 }
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r2 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r2     // Catch:{ Exception -> 0x1206 }
            r46 = r12
            r9 = r19
            goto L_0x1046
        L_0x0ff7:
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia     // Catch:{ Exception -> 0x1206 }
            r2.<init>()     // Catch:{ Exception -> 0x1206 }
            r15 = r26
            r2.peer = r15     // Catch:{ Exception -> 0x1206 }
            r9 = r19
            if (r65 == 0) goto L_0x1027
            int r14 = r10.currentAccount     // Catch:{ Exception -> 0x1206 }
            android.content.SharedPreferences r14 = org.telegram.messenger.MessagesController.getNotificationsSettings(r14)     // Catch:{ Exception -> 0x1206 }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1206 }
            r15.<init>()     // Catch:{ Exception -> 0x1206 }
            r46 = r12
            r12 = r49
            r15.append(r12)     // Catch:{ Exception -> 0x1206 }
            r15.append(r6)     // Catch:{ Exception -> 0x1206 }
            java.lang.String r6 = r15.toString()     // Catch:{ Exception -> 0x1206 }
            r7 = 0
            boolean r6 = r14.getBoolean(r6, r7)     // Catch:{ Exception -> 0x1206 }
            if (r6 == 0) goto L_0x1025
            goto L_0x1029
        L_0x1025:
            r6 = 0
            goto L_0x102a
        L_0x1027:
            r46 = r12
        L_0x1029:
            r6 = 1
        L_0x102a:
            r2.silent = r6     // Catch:{ Exception -> 0x1206 }
            int r6 = r8.reply_to_msg_id     // Catch:{ Exception -> 0x1206 }
            if (r6 == 0) goto L_0x103a
            int r6 = r2.flags     // Catch:{ Exception -> 0x1206 }
            r7 = 1
            r6 = r6 | r7
            r2.flags = r6     // Catch:{ Exception -> 0x1206 }
            int r6 = r8.reply_to_msg_id     // Catch:{ Exception -> 0x1206 }
            r2.reply_to_msg_id = r6     // Catch:{ Exception -> 0x1206 }
        L_0x103a:
            if (r3 == 0) goto L_0x1044
            r2.schedule_date = r3     // Catch:{ Exception -> 0x1206 }
            int r6 = r2.flags     // Catch:{ Exception -> 0x1206 }
            r6 = r6 | 1024(0x400, float:1.435E-42)
            r2.flags = r6     // Catch:{ Exception -> 0x1206 }
        L_0x1044:
            r13.sendRequest = r2     // Catch:{ Exception -> 0x1206 }
        L_0x1046:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r13.messageObjects     // Catch:{ Exception -> 0x1206 }
            r6.add(r4)     // Catch:{ Exception -> 0x1206 }
            java.util.ArrayList<java.lang.Object> r6 = r13.parentObjects     // Catch:{ Exception -> 0x1206 }
            r6.add(r9)     // Catch:{ Exception -> 0x1206 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r13.locations     // Catch:{ Exception -> 0x1206 }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r13.photoSize     // Catch:{ Exception -> 0x1206 }
            r6.add(r7)     // Catch:{ Exception -> 0x1206 }
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo> r6 = r13.videoEditedInfos     // Catch:{ Exception -> 0x1206 }
            org.telegram.messenger.VideoEditedInfo r7 = r13.videoEditedInfo     // Catch:{ Exception -> 0x1206 }
            r6.add(r7)     // Catch:{ Exception -> 0x1206 }
            java.util.ArrayList<java.lang.String> r6 = r13.httpLocations     // Catch:{ Exception -> 0x1206 }
            java.lang.String r7 = r13.httpLocation     // Catch:{ Exception -> 0x1206 }
            r6.add(r7)     // Catch:{ Exception -> 0x1206 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputMedia> r6 = r13.inputMedias     // Catch:{ Exception -> 0x1206 }
            org.telegram.tgnet.TLRPC$InputMedia r7 = r13.inputUploadMedia     // Catch:{ Exception -> 0x1206 }
            r6.add(r7)     // Catch:{ Exception -> 0x1206 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r6 = r13.messages     // Catch:{ Exception -> 0x1206 }
            r6.add(r8)     // Catch:{ Exception -> 0x1206 }
            java.util.ArrayList<java.lang.String> r6 = r13.originalPaths     // Catch:{ Exception -> 0x1206 }
            r6.add(r11)     // Catch:{ Exception -> 0x1206 }
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r6 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia     // Catch:{ Exception -> 0x1206 }
            r6.<init>()     // Catch:{ Exception -> 0x1206 }
            long r14 = r8.random_id     // Catch:{ Exception -> 0x1206 }
            r6.random_id = r14     // Catch:{ Exception -> 0x1206 }
            r6.media = r5     // Catch:{ Exception -> 0x1206 }
            r14 = r34
            r6.message = r14     // Catch:{ Exception -> 0x1206 }
            r5 = r62
            if (r5 == 0) goto L_0x1097
            boolean r7 = r62.isEmpty()     // Catch:{ Exception -> 0x1206 }
            if (r7 != 0) goto L_0x1097
            r6.entities = r5     // Catch:{ Exception -> 0x1206 }
            int r5 = r6.flags     // Catch:{ Exception -> 0x1206 }
            r7 = 1
            r5 = r5 | r7
            r6.flags = r5     // Catch:{ Exception -> 0x1206 }
        L_0x1097:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r5 = r2.multi_media     // Catch:{ Exception -> 0x1206 }
            r5.add(r6)     // Catch:{ Exception -> 0x1206 }
            r19 = r9
            r20 = r11
            r11 = r2
            goto L_0x1115
        L_0x10a3:
            r2 = r62
            r20 = r11
            r46 = r12
            r9 = r19
            r15 = r26
            r14 = r34
            r12 = r49
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r11 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia     // Catch:{ Exception -> 0x1206 }
            r11.<init>()     // Catch:{ Exception -> 0x1206 }
            r11.peer = r15     // Catch:{ Exception -> 0x1206 }
            if (r65 == 0) goto L_0x10db
            int r15 = r10.currentAccount     // Catch:{ Exception -> 0x1206 }
            android.content.SharedPreferences r15 = org.telegram.messenger.MessagesController.getNotificationsSettings(r15)     // Catch:{ Exception -> 0x1206 }
            r19 = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1206 }
            r9.<init>()     // Catch:{ Exception -> 0x1206 }
            r9.append(r12)     // Catch:{ Exception -> 0x1206 }
            r9.append(r6)     // Catch:{ Exception -> 0x1206 }
            java.lang.String r6 = r9.toString()     // Catch:{ Exception -> 0x1206 }
            r7 = 0
            boolean r6 = r15.getBoolean(r6, r7)     // Catch:{ Exception -> 0x1206 }
            if (r6 == 0) goto L_0x10d9
            goto L_0x10dd
        L_0x10d9:
            r6 = 0
            goto L_0x10de
        L_0x10db:
            r19 = r9
        L_0x10dd:
            r6 = 1
        L_0x10de:
            r11.silent = r6     // Catch:{ Exception -> 0x1206 }
            int r6 = r8.reply_to_msg_id     // Catch:{ Exception -> 0x1206 }
            if (r6 == 0) goto L_0x10ee
            int r6 = r11.flags     // Catch:{ Exception -> 0x1206 }
            r7 = 1
            r6 = r6 | r7
            r11.flags = r6     // Catch:{ Exception -> 0x1206 }
            int r6 = r8.reply_to_msg_id     // Catch:{ Exception -> 0x1206 }
            r11.reply_to_msg_id = r6     // Catch:{ Exception -> 0x1206 }
        L_0x10ee:
            long r6 = r8.random_id     // Catch:{ Exception -> 0x1206 }
            r11.random_id = r6     // Catch:{ Exception -> 0x1206 }
            r11.media = r5     // Catch:{ Exception -> 0x1206 }
            r11.message = r14     // Catch:{ Exception -> 0x1206 }
            if (r2 == 0) goto L_0x1107
            boolean r5 = r62.isEmpty()     // Catch:{ Exception -> 0x1206 }
            if (r5 != 0) goto L_0x1107
            r11.entities = r2     // Catch:{ Exception -> 0x1206 }
            int r2 = r11.flags     // Catch:{ Exception -> 0x1206 }
            r5 = 8
            r2 = r2 | r5
            r11.flags = r2     // Catch:{ Exception -> 0x1206 }
        L_0x1107:
            if (r3 == 0) goto L_0x1111
            r11.schedule_date = r3     // Catch:{ Exception -> 0x1206 }
            int r2 = r11.flags     // Catch:{ Exception -> 0x1206 }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r11.flags = r2     // Catch:{ Exception -> 0x1206 }
        L_0x1111:
            if (r13 == 0) goto L_0x1115
            r13.sendRequest = r11     // Catch:{ Exception -> 0x1206 }
        L_0x1115:
            int r2 = (r67 > r16 ? 1 : (r67 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x111e
            r10.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x111e:
            r2 = 1
            if (r1 != r2) goto L_0x113a
            r1 = 0
            if (r3 == 0) goto L_0x1126
            r2 = 1
            goto L_0x1127
        L_0x1126:
            r2 = 0
        L_0x1127:
            r46 = r45
            r47 = r11
            r48 = r4
            r49 = r1
            r50 = r13
            r51 = r19
            r52 = r2
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x113a:
            r2 = 2
            if (r1 != r2) goto L_0x1162
            if (r46 == 0) goto L_0x1144
            r10.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x1144:
            r1 = 0
            r2 = 1
            if (r3 == 0) goto L_0x114a
            r5 = 1
            goto L_0x114b
        L_0x114a:
            r5 = 0
        L_0x114b:
            r46 = r45
            r47 = r11
            r48 = r4
            r49 = r20
            r50 = r1
            r51 = r2
            r52 = r13
            r53 = r19
            r54 = r5
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x1162:
            r2 = 3
            if (r1 != r2) goto L_0x1184
            if (r46 == 0) goto L_0x116c
            r10.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x116c:
            if (r3 == 0) goto L_0x1170
            r1 = 1
            goto L_0x1171
        L_0x1170:
            r1 = 0
        L_0x1171:
            r46 = r45
            r47 = r11
            r48 = r4
            r49 = r20
            r50 = r13
            r51 = r19
            r52 = r1
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x1184:
            r2 = 6
            if (r1 != r2) goto L_0x119f
            if (r3 == 0) goto L_0x118b
            r1 = 1
            goto L_0x118c
        L_0x118b:
            r1 = 0
        L_0x118c:
            r46 = r45
            r47 = r11
            r48 = r4
            r49 = r20
            r50 = r13
            r51 = r19
            r52 = r1
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x119f:
            r2 = 7
            if (r1 != r2) goto L_0x11c3
            if (r46 == 0) goto L_0x11ab
            if (r13 == 0) goto L_0x11ab
            r10.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x11ab:
            if (r3 == 0) goto L_0x11af
            r1 = 1
            goto L_0x11b0
        L_0x11af:
            r1 = 0
        L_0x11b0:
            r46 = r45
            r47 = r11
            r48 = r4
            r49 = r20
            r50 = r13
            r51 = r19
            r52 = r1
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x11c3:
            r2 = 8
            if (r1 != r2) goto L_0x11e6
            if (r46 == 0) goto L_0x11ce
            r10.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x11ce:
            if (r3 == 0) goto L_0x11d2
            r1 = 1
            goto L_0x11d3
        L_0x11d2:
            r1 = 0
        L_0x11d3:
            r46 = r45
            r47 = r11
            r48 = r4
            r49 = r20
            r50 = r13
            r51 = r19
            r52 = r1
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x11e6:
            r2 = 10
            if (r1 == r2) goto L_0x11ee
            r2 = 11
            if (r1 != r2) goto L_0x1963
        L_0x11ee:
            if (r3 == 0) goto L_0x11f2
            r1 = 1
            goto L_0x11f3
        L_0x11f2:
            r1 = 0
        L_0x11f3:
            r46 = r45
            r47 = r11
            r48 = r4
            r49 = r20
            r50 = r13
            r51 = r19
            r52 = r1
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1206 }
            goto L_0x1963
        L_0x1206:
            r0 = move-exception
            goto L_0x120b
        L_0x1208:
            r0 = move-exception
            r8 = r20
        L_0x120b:
            r1 = r0
            r11 = r3
            r5 = r8
            goto L_0x1933
        L_0x1210:
            r2 = r48
            r5 = r49
            r19 = r53
            r8 = r62
            r4 = r63
            r11 = r15
            r43 = r18
            r42 = r22
            r15 = r26
            r13 = r30
            r14 = r34
            int r3 = r9.layer     // Catch:{ Exception -> 0x17d6 }
            int r3 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r3)     // Catch:{ Exception -> 0x17d6 }
            r6 = 73
            if (r3 < r6) goto L_0x124b
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1244 }
            r3.<init>()     // Catch:{ Exception -> 0x1244 }
            int r6 = (r67 > r16 ? 1 : (r67 == r16 ? 0 : -1))
            if (r6 == 0) goto L_0x1250
            r6 = r67
            r3.grouped_id = r6     // Catch:{ Exception -> 0x1244 }
            int r6 = r3.flags     // Catch:{ Exception -> 0x1244 }
            r7 = 131072(0x20000, float:1.83671E-40)
            r6 = r6 | r7
            r3.flags = r6     // Catch:{ Exception -> 0x1244 }
            goto L_0x1250
        L_0x1244:
            r0 = move-exception
            r1 = r0
            r5 = r11
            r11 = r66
            goto L_0x1933
        L_0x124b:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x17d6 }
            r3.<init>()     // Catch:{ Exception -> 0x17d6 }
        L_0x1250:
            int r6 = r11.ttl     // Catch:{ Exception -> 0x17d6 }
            r3.ttl = r6     // Catch:{ Exception -> 0x17d6 }
            if (r8 == 0) goto L_0x1264
            boolean r6 = r62.isEmpty()     // Catch:{ Exception -> 0x1244 }
            if (r6 != 0) goto L_0x1264
            r3.entities = r8     // Catch:{ Exception -> 0x1244 }
            int r6 = r3.flags     // Catch:{ Exception -> 0x1244 }
            r6 = r6 | 128(0x80, float:1.794E-43)
            r3.flags = r6     // Catch:{ Exception -> 0x1244 }
        L_0x1264:
            long r6 = r11.reply_to_random_id     // Catch:{ Exception -> 0x17d6 }
            int r8 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r8 == 0) goto L_0x1275
            long r6 = r11.reply_to_random_id     // Catch:{ Exception -> 0x1244 }
            r3.reply_to_random_id = r6     // Catch:{ Exception -> 0x1244 }
            int r6 = r3.flags     // Catch:{ Exception -> 0x1244 }
            r7 = 8
            r6 = r6 | r7
            r3.flags = r6     // Catch:{ Exception -> 0x1244 }
        L_0x1275:
            int r6 = r3.flags     // Catch:{ Exception -> 0x17d6 }
            r6 = r6 | 512(0x200, float:7.175E-43)
            r3.flags = r6     // Catch:{ Exception -> 0x17d6 }
            if (r12 == 0) goto L_0x1293
            r6 = r46
            java.lang.Object r7 = r12.get(r6)     // Catch:{ Exception -> 0x1244 }
            if (r7 == 0) goto L_0x1293
            java.lang.Object r6 = r12.get(r6)     // Catch:{ Exception -> 0x1244 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x1244 }
            r3.via_bot_name = r6     // Catch:{ Exception -> 0x1244 }
            int r6 = r3.flags     // Catch:{ Exception -> 0x1244 }
            r6 = r6 | 2048(0x800, float:2.87E-42)
            r3.flags = r6     // Catch:{ Exception -> 0x1244 }
        L_0x1293:
            long r6 = r11.random_id     // Catch:{ Exception -> 0x17d6 }
            r3.random_id = r6     // Catch:{ Exception -> 0x17d6 }
            r3.message = r13     // Catch:{ Exception -> 0x17d6 }
            r6 = 1
            if (r1 != r6) goto L_0x12f4
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x1244 }
            if (r2 == 0) goto L_0x12b8
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue     // Catch:{ Exception -> 0x1244 }
            r2.<init>()     // Catch:{ Exception -> 0x1244 }
            r3.media = r2     // Catch:{ Exception -> 0x1244 }
            java.lang.String r6 = r5.address     // Catch:{ Exception -> 0x1244 }
            r2.address = r6     // Catch:{ Exception -> 0x1244 }
            java.lang.String r6 = r5.title     // Catch:{ Exception -> 0x1244 }
            r2.title = r6     // Catch:{ Exception -> 0x1244 }
            java.lang.String r6 = r5.provider     // Catch:{ Exception -> 0x1244 }
            r2.provider = r6     // Catch:{ Exception -> 0x1244 }
            java.lang.String r6 = r5.venue_id     // Catch:{ Exception -> 0x1244 }
            r2.venue_id = r6     // Catch:{ Exception -> 0x1244 }
            goto L_0x12bf
        L_0x12b8:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint     // Catch:{ Exception -> 0x1244 }
            r2.<init>()     // Catch:{ Exception -> 0x1244 }
            r3.media = r2     // Catch:{ Exception -> 0x1244 }
        L_0x12bf:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$GeoPoint r6 = r5.geo     // Catch:{ Exception -> 0x1244 }
            double r6 = r6.lat     // Catch:{ Exception -> 0x1244 }
            r2.lat = r6     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$GeoPoint r5 = r5.geo     // Catch:{ Exception -> 0x1244 }
            double r5 = r5._long     // Catch:{ Exception -> 0x1244 }
            r2._long = r5     // Catch:{ Exception -> 0x1244 }
            org.telegram.messenger.SecretChatHelper r2 = r45.getSecretChatHelper()     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner     // Catch:{ Exception -> 0x1244 }
            r6 = 0
            r7 = 0
            r48 = r2
            r49 = r3
            r50 = r5
            r51 = r9
            r52 = r6
            r53 = r7
            r54 = r4
            r48.performSendEncryptedRequest(r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x1244 }
            r5 = r55
            r13 = r66
            r59 = r1
            r7 = r20
            r20 = r11
            goto L_0x164b
        L_0x12f4:
            r5 = 2
            if (r1 == r5) goto L_0x1650
            r5 = 9
            if (r1 != r5) goto L_0x130f
            if (r2 == 0) goto L_0x130f
            r5 = r55
            r13 = r66
            r59 = r1
            r1 = r19
            r7 = r20
            r8 = r28
            r15 = r43
            r20 = r11
            goto L_0x1662
        L_0x130f:
            r2 = 3
            if (r1 != r2) goto L_0x144b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r15.thumbs     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r10.getThumbForSecretChat(r2)     // Catch:{ Exception -> 0x1244 }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r2)     // Catch:{ Exception -> 0x1244 }
            boolean r5 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r15)     // Catch:{ Exception -> 0x1244 }
            if (r5 != 0) goto L_0x1346
            boolean r5 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r15)     // Catch:{ Exception -> 0x1244 }
            if (r5 == 0) goto L_0x1328
            goto L_0x1346
        L_0x1328:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo     // Catch:{ Exception -> 0x1244 }
            r5.<init>()     // Catch:{ Exception -> 0x1244 }
            r3.media = r5     // Catch:{ Exception -> 0x1244 }
            if (r2 == 0) goto L_0x133c
            byte[] r6 = r2.bytes     // Catch:{ Exception -> 0x1244 }
            if (r6 == 0) goto L_0x133c
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r5     // Catch:{ Exception -> 0x1244 }
            byte[] r6 = r2.bytes     // Catch:{ Exception -> 0x1244 }
            r5.thumb = r6     // Catch:{ Exception -> 0x1244 }
            goto L_0x1367
        L_0x133c:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r5     // Catch:{ Exception -> 0x1244 }
            r6 = 0
            byte[] r7 = new byte[r6]     // Catch:{ Exception -> 0x1244 }
            r5.thumb = r7     // Catch:{ Exception -> 0x1244 }
            goto L_0x1367
        L_0x1346:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1244 }
            r5.<init>()     // Catch:{ Exception -> 0x1244 }
            r3.media = r5     // Catch:{ Exception -> 0x1244 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r15.attributes     // Catch:{ Exception -> 0x1244 }
            r5.attributes = r6     // Catch:{ Exception -> 0x1244 }
            if (r2 == 0) goto L_0x135e
            byte[] r6 = r2.bytes     // Catch:{ Exception -> 0x1244 }
            if (r6 == 0) goto L_0x135e
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1244 }
            byte[] r6 = r2.bytes     // Catch:{ Exception -> 0x1244 }
            r5.thumb = r6     // Catch:{ Exception -> 0x1244 }
            goto L_0x1367
        L_0x135e:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1244 }
            r6 = 0
            byte[] r7 = new byte[r6]     // Catch:{ Exception -> 0x1244 }
            r5.thumb = r7     // Catch:{ Exception -> 0x1244 }
        L_0x1367:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            r5.caption = r14     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            java.lang.String r6 = "video/mp4"
            r5.mime_type = r6     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            int r6 = r15.size     // Catch:{ Exception -> 0x1244 }
            r5.size = r6     // Catch:{ Exception -> 0x1244 }
            r5 = 0
        L_0x1378:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r15.attributes     // Catch:{ Exception -> 0x1244 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x1244 }
            if (r5 >= r6) goto L_0x13a2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r15.attributes     // Catch:{ Exception -> 0x1244 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r6 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r6     // Catch:{ Exception -> 0x1244 }
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x1244 }
            if (r7 == 0) goto L_0x139f
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            int r7 = r6.w     // Catch:{ Exception -> 0x1244 }
            r5.w = r7     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            int r7 = r6.h     // Catch:{ Exception -> 0x1244 }
            r5.h = r7     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            int r6 = r6.duration     // Catch:{ Exception -> 0x1244 }
            r5.duration = r6     // Catch:{ Exception -> 0x1244 }
            goto L_0x13a2
        L_0x139f:
            int r5 = r5 + 1
            goto L_0x1378
        L_0x13a2:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            int r6 = r2.h     // Catch:{ Exception -> 0x1244 }
            r5.thumb_h = r6     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            int r2 = r2.w     // Catch:{ Exception -> 0x1244 }
            r5.thumb_w = r2     // Catch:{ Exception -> 0x1244 }
            byte[] r2 = r15.key     // Catch:{ Exception -> 0x1244 }
            if (r2 == 0) goto L_0x13f1
            int r2 = (r67 > r16 ? 1 : (r67 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x13b7
            goto L_0x13f1
        L_0x13b7:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1244 }
            r2.<init>()     // Catch:{ Exception -> 0x1244 }
            long r5 = r15.id     // Catch:{ Exception -> 0x1244 }
            r2.id = r5     // Catch:{ Exception -> 0x1244 }
            long r5 = r15.access_hash     // Catch:{ Exception -> 0x1244 }
            r2.access_hash = r5     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            byte[] r6 = r15.key     // Catch:{ Exception -> 0x1244 }
            r5.key = r6     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r3.media     // Catch:{ Exception -> 0x1244 }
            byte[] r6 = r15.iv     // Catch:{ Exception -> 0x1244 }
            r5.iv = r6     // Catch:{ Exception -> 0x1244 }
            org.telegram.messenger.SecretChatHelper r5 = r45.getSecretChatHelper()     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner     // Catch:{ Exception -> 0x1244 }
            r7 = 0
            r48 = r5
            r49 = r3
            r50 = r6
            r51 = r9
            r52 = r2
            r53 = r7
            r54 = r4
            r48.performSendEncryptedRequest(r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x1244 }
            r5 = r55
            r2 = r58
            r13 = r66
            r7 = r20
            goto L_0x143e
        L_0x13f1:
            if (r58 != 0) goto L_0x142b
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1244 }
            r5 = r55
            r2.<init>(r5)     // Catch:{ Exception -> 0x1244 }
            r2.encryptedChat = r9     // Catch:{ Exception -> 0x1244 }
            r7 = 1
            r2.type = r7     // Catch:{ Exception -> 0x1244 }
            r2.sendEncryptedRequest = r3     // Catch:{ Exception -> 0x1244 }
            r7 = r20
            r2.originalPath = r7     // Catch:{ Exception -> 0x1244 }
            r2.obj = r4     // Catch:{ Exception -> 0x1244 }
            if (r12 == 0) goto L_0x1418
            r8 = r28
            boolean r9 = r12.containsKey(r8)     // Catch:{ Exception -> 0x1244 }
            if (r9 == 0) goto L_0x1418
            java.lang.Object r8 = r12.get(r8)     // Catch:{ Exception -> 0x1244 }
            r2.parentObject = r8     // Catch:{ Exception -> 0x1244 }
            goto L_0x141c
        L_0x1418:
            r8 = r19
            r2.parentObject = r8     // Catch:{ Exception -> 0x1244 }
        L_0x141c:
            r8 = 1
            r2.performMediaUpload = r8     // Catch:{ Exception -> 0x1244 }
            r13 = r66
            if (r13 == 0) goto L_0x1425
            r8 = 1
            goto L_0x1426
        L_0x1425:
            r8 = 0
        L_0x1426:
            r2.scheduled = r8     // Catch:{ Exception -> 0x1445 }
            r8 = r50
            goto L_0x1435
        L_0x142b:
            r5 = r55
            r13 = r66
            r7 = r20
            r8 = r50
            r2 = r58
        L_0x1435:
            r2.videoEditedInfo = r8     // Catch:{ Exception -> 0x1445 }
            int r8 = (r67 > r16 ? 1 : (r67 == r16 ? 0 : -1))
            if (r8 != 0) goto L_0x143e
            r10.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x1445 }
        L_0x143e:
            r59 = r1
            r1 = r2
            r20 = r11
            goto L_0x164d
        L_0x1445:
            r0 = move-exception
            r1 = r0
            r5 = r11
            r11 = r13
            goto L_0x1933
        L_0x144b:
            r5 = r55
            r13 = r66
            r2 = r19
            r7 = r20
            r8 = r28
            r20 = r11
            r11 = 6
            if (r1 != r11) goto L_0x1493
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact     // Catch:{ Exception -> 0x148e }
            r2.<init>()     // Catch:{ Exception -> 0x148e }
            r3.media = r2     // Catch:{ Exception -> 0x148e }
            r11 = r42
            java.lang.String r8 = r11.phone     // Catch:{ Exception -> 0x148e }
            r2.phone_number = r8     // Catch:{ Exception -> 0x148e }
            java.lang.String r8 = r11.first_name     // Catch:{ Exception -> 0x148e }
            r2.first_name = r8     // Catch:{ Exception -> 0x148e }
            java.lang.String r8 = r11.last_name     // Catch:{ Exception -> 0x148e }
            r2.last_name = r8     // Catch:{ Exception -> 0x148e }
            int r8 = r11.id     // Catch:{ Exception -> 0x148e }
            r2.user_id = r8     // Catch:{ Exception -> 0x148e }
            org.telegram.messenger.SecretChatHelper r2 = r45.getSecretChatHelper()     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner     // Catch:{ Exception -> 0x148e }
            r11 = 0
            r12 = 0
            r48 = r2
            r49 = r3
            r50 = r8
            r51 = r9
            r52 = r11
            r53 = r12
            r54 = r4
            r48.performSendEncryptedRequest(r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x148e }
            goto L_0x1510
        L_0x148e:
            r0 = move-exception
            r1 = r0
            r11 = r13
            goto L_0x0var_
        L_0x1493:
            r11 = 7
            if (r1 == r11) goto L_0x1514
            r11 = 9
            if (r1 != r11) goto L_0x149e
            if (r15 == 0) goto L_0x149e
            goto L_0x1514
        L_0x149e:
            r8 = 8
            if (r1 != r8) goto L_0x1510
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r8 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x148e }
            r8.<init>(r5)     // Catch:{ Exception -> 0x148e }
            r8.encryptedChat = r9     // Catch:{ Exception -> 0x148e }
            r8.sendEncryptedRequest = r3     // Catch:{ Exception -> 0x148e }
            r8.obj = r4     // Catch:{ Exception -> 0x148e }
            r9 = 3
            r8.type = r9     // Catch:{ Exception -> 0x148e }
            r8.parentObject = r2     // Catch:{ Exception -> 0x148e }
            r2 = 1
            r8.performMediaUpload = r2     // Catch:{ Exception -> 0x148e }
            if (r13 == 0) goto L_0x14b9
            r2 = 1
            goto L_0x14ba
        L_0x14b9:
            r2 = 0
        L_0x14ba:
            r8.scheduled = r2     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x148e }
            r2.<init>()     // Catch:{ Exception -> 0x148e }
            r3.media = r2     // Catch:{ Exception -> 0x148e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r9 = r15.attributes     // Catch:{ Exception -> 0x148e }
            r2.attributes = r9     // Catch:{ Exception -> 0x148e }
            r2.caption = r14     // Catch:{ Exception -> 0x148e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r15.thumbs     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = r10.getThumbForSecretChat(r2)     // Catch:{ Exception -> 0x148e }
            if (r2 == 0) goto L_0x14e9
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r2)     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r9 = r3.media     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r9 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r9     // Catch:{ Exception -> 0x148e }
            byte[] r11 = r2.bytes     // Catch:{ Exception -> 0x148e }
            r9.thumb = r11     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r9 = r3.media     // Catch:{ Exception -> 0x148e }
            int r11 = r2.h     // Catch:{ Exception -> 0x148e }
            r9.thumb_h = r11     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r9 = r3.media     // Catch:{ Exception -> 0x148e }
            int r2 = r2.w     // Catch:{ Exception -> 0x148e }
            r9.thumb_w = r2     // Catch:{ Exception -> 0x148e }
            goto L_0x14fa
        L_0x14e9:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r2 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r2     // Catch:{ Exception -> 0x148e }
            r9 = 0
            byte[] r11 = new byte[r9]     // Catch:{ Exception -> 0x148e }
            r2.thumb = r11     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x148e }
            r2.thumb_h = r9     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x148e }
            r2.thumb_w = r9     // Catch:{ Exception -> 0x148e }
        L_0x14fa:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x148e }
            java.lang.String r9 = r15.mime_type     // Catch:{ Exception -> 0x148e }
            r2.mime_type = r9     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x148e }
            int r9 = r15.size     // Catch:{ Exception -> 0x148e }
            r2.size = r9     // Catch:{ Exception -> 0x148e }
            r8.originalPath = r7     // Catch:{ Exception -> 0x148e }
            r10.performSendDelayedMessage(r8)     // Catch:{ Exception -> 0x148e }
            r59 = r1
            r1 = r8
            goto L_0x164d
        L_0x1510:
            r59 = r1
            goto L_0x164b
        L_0x1514:
            boolean r11 = org.telegram.messenger.MessageObject.isStickerDocument(r15)     // Catch:{ Exception -> 0x148e }
            if (r11 != 0) goto L_0x15e9
            r11 = 0
            boolean r18 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r15, r11)     // Catch:{ Exception -> 0x148e }
            if (r18 == 0) goto L_0x1523
            goto L_0x15e9
        L_0x1523:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r11 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x148e }
            r11.<init>()     // Catch:{ Exception -> 0x148e }
            r3.media = r11     // Catch:{ Exception -> 0x148e }
            r59 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r15.attributes     // Catch:{ Exception -> 0x148e }
            r11.attributes = r1     // Catch:{ Exception -> 0x148e }
            r11.caption = r14     // Catch:{ Exception -> 0x148e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r15.thumbs     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r10.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x148e }
            if (r1 == 0) goto L_0x1552
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1)     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r11 = r3.media     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r11 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r11     // Catch:{ Exception -> 0x148e }
            byte[] r14 = r1.bytes     // Catch:{ Exception -> 0x148e }
            r11.thumb = r14     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r11 = r3.media     // Catch:{ Exception -> 0x148e }
            int r14 = r1.h     // Catch:{ Exception -> 0x148e }
            r11.thumb_h = r14     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r11 = r3.media     // Catch:{ Exception -> 0x148e }
            int r1 = r1.w     // Catch:{ Exception -> 0x148e }
            r11.thumb_w = r1     // Catch:{ Exception -> 0x148e }
            goto L_0x1563
        L_0x1552:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r3.media     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r1     // Catch:{ Exception -> 0x148e }
            r11 = 0
            byte[] r14 = new byte[r11]     // Catch:{ Exception -> 0x148e }
            r1.thumb = r14     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r3.media     // Catch:{ Exception -> 0x148e }
            r1.thumb_h = r11     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r3.media     // Catch:{ Exception -> 0x148e }
            r1.thumb_w = r11     // Catch:{ Exception -> 0x148e }
        L_0x1563:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r3.media     // Catch:{ Exception -> 0x148e }
            int r11 = r15.size     // Catch:{ Exception -> 0x148e }
            r1.size = r11     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r3.media     // Catch:{ Exception -> 0x148e }
            java.lang.String r11 = r15.mime_type     // Catch:{ Exception -> 0x148e }
            r1.mime_type = r11     // Catch:{ Exception -> 0x148e }
            byte[] r1 = r15.key     // Catch:{ Exception -> 0x148e }
            if (r1 != 0) goto L_0x15b7
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x148e }
            r1.<init>(r5)     // Catch:{ Exception -> 0x148e }
            r1.originalPath = r7     // Catch:{ Exception -> 0x148e }
            r1.sendEncryptedRequest = r3     // Catch:{ Exception -> 0x148e }
            r11 = 2
            r1.type = r11     // Catch:{ Exception -> 0x148e }
            r1.obj = r4     // Catch:{ Exception -> 0x148e }
            if (r12 == 0) goto L_0x1590
            boolean r11 = r12.containsKey(r8)     // Catch:{ Exception -> 0x148e }
            if (r11 == 0) goto L_0x1590
            java.lang.Object r2 = r12.get(r8)     // Catch:{ Exception -> 0x148e }
            r1.parentObject = r2     // Catch:{ Exception -> 0x148e }
            goto L_0x1592
        L_0x1590:
            r1.parentObject = r2     // Catch:{ Exception -> 0x148e }
        L_0x1592:
            r1.encryptedChat = r9     // Catch:{ Exception -> 0x148e }
            r2 = 1
            r1.performMediaUpload = r2     // Catch:{ Exception -> 0x148e }
            r11 = r57
            if (r11 == 0) goto L_0x15ab
            int r2 = r57.length()     // Catch:{ Exception -> 0x148e }
            if (r2 <= 0) goto L_0x15ab
            r15 = r43
            boolean r2 = r11.startsWith(r15)     // Catch:{ Exception -> 0x148e }
            if (r2 == 0) goto L_0x15ab
            r1.httpLocation = r11     // Catch:{ Exception -> 0x148e }
        L_0x15ab:
            if (r13 == 0) goto L_0x15af
            r2 = 1
            goto L_0x15b0
        L_0x15af:
            r2 = 0
        L_0x15b0:
            r1.scheduled = r2     // Catch:{ Exception -> 0x148e }
            r10.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x148e }
            goto L_0x164d
        L_0x15b7:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x148e }
            r1.<init>()     // Catch:{ Exception -> 0x148e }
            long r11 = r15.id     // Catch:{ Exception -> 0x148e }
            r1.id = r11     // Catch:{ Exception -> 0x148e }
            long r11 = r15.access_hash     // Catch:{ Exception -> 0x148e }
            r1.access_hash = r11     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x148e }
            byte[] r8 = r15.key     // Catch:{ Exception -> 0x148e }
            r2.key = r8     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x148e }
            byte[] r8 = r15.iv     // Catch:{ Exception -> 0x148e }
            r2.iv = r8     // Catch:{ Exception -> 0x148e }
            org.telegram.messenger.SecretChatHelper r2 = r45.getSecretChatHelper()     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner     // Catch:{ Exception -> 0x148e }
            r11 = 0
            r48 = r2
            r49 = r3
            r50 = r8
            r51 = r9
            r52 = r1
            r53 = r11
            r54 = r4
            r48.performSendEncryptedRequest(r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x148e }
            goto L_0x164b
        L_0x15e9:
            r59 = r1
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument     // Catch:{ Exception -> 0x148e }
            r1.<init>()     // Catch:{ Exception -> 0x148e }
            r3.media = r1     // Catch:{ Exception -> 0x148e }
            long r11 = r15.id     // Catch:{ Exception -> 0x148e }
            r1.id = r11     // Catch:{ Exception -> 0x148e }
            int r2 = r15.date     // Catch:{ Exception -> 0x148e }
            r1.date = r2     // Catch:{ Exception -> 0x148e }
            long r11 = r15.access_hash     // Catch:{ Exception -> 0x148e }
            r1.access_hash = r11     // Catch:{ Exception -> 0x148e }
            java.lang.String r2 = r15.mime_type     // Catch:{ Exception -> 0x148e }
            r1.mime_type = r2     // Catch:{ Exception -> 0x148e }
            int r2 = r15.size     // Catch:{ Exception -> 0x148e }
            r1.size = r2     // Catch:{ Exception -> 0x148e }
            int r2 = r15.dc_id     // Catch:{ Exception -> 0x148e }
            r1.dc_id = r2     // Catch:{ Exception -> 0x148e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r15.attributes     // Catch:{ Exception -> 0x148e }
            r1.attributes = r2     // Catch:{ Exception -> 0x148e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r15.thumbs     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r10.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x148e }
            if (r1 == 0) goto L_0x161d
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r2 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r2     // Catch:{ Exception -> 0x148e }
            r2.thumb = r1     // Catch:{ Exception -> 0x148e }
            goto L_0x1632
        L_0x161d:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r3.media     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r2 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty     // Catch:{ Exception -> 0x148e }
            r2.<init>()     // Catch:{ Exception -> 0x148e }
            r1.thumb = r2     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r3.media     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r1.thumb     // Catch:{ Exception -> 0x148e }
            java.lang.String r2 = "s"
            r1.type = r2     // Catch:{ Exception -> 0x148e }
        L_0x1632:
            org.telegram.messenger.SecretChatHelper r1 = r45.getSecretChatHelper()     // Catch:{ Exception -> 0x148e }
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner     // Catch:{ Exception -> 0x148e }
            r8 = 0
            r11 = 0
            r48 = r1
            r49 = r3
            r50 = r2
            r51 = r9
            r52 = r8
            r53 = r11
            r54 = r4
            r48.performSendEncryptedRequest(r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x148e }
        L_0x164b:
            r1 = r58
        L_0x164d:
            r11 = r13
            goto L_0x1772
        L_0x1650:
            r5 = r55
            r13 = r66
            r59 = r1
            r1 = r19
            r7 = r20
            r8 = r28
            r15 = r43
            r20 = r11
            r11 = r57
        L_0x1662:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r2.sizes     // Catch:{ Exception -> 0x17d0 }
            r18 = r15
            r15 = 0
            java.lang.Object r11 = r11.get(r15)     // Catch:{ Exception -> 0x17d0 }
            org.telegram.tgnet.TLRPC$PhotoSize r11 = (org.telegram.tgnet.TLRPC$PhotoSize) r11     // Catch:{ Exception -> 0x17d0 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r15 = r2.sizes     // Catch:{ Exception -> 0x17d0 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r13 = r2.sizes     // Catch:{ Exception -> 0x17cc }
            int r13 = r13.size()     // Catch:{ Exception -> 0x17cc }
            r19 = 1
            int r13 = r13 + -1
            java.lang.Object r13 = r15.get(r13)     // Catch:{ Exception -> 0x17cc }
            org.telegram.tgnet.TLRPC$PhotoSize r13 = (org.telegram.tgnet.TLRPC$PhotoSize) r13     // Catch:{ Exception -> 0x17cc }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r11)     // Catch:{ Exception -> 0x17cc }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r15 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto     // Catch:{ Exception -> 0x17cc }
            r15.<init>()     // Catch:{ Exception -> 0x17cc }
            r3.media = r15     // Catch:{ Exception -> 0x17cc }
            r15.caption = r14     // Catch:{ Exception -> 0x17cc }
            byte[] r14 = r11.bytes     // Catch:{ Exception -> 0x17cc }
            if (r14 == 0) goto L_0x169d
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r15 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r15     // Catch:{ Exception -> 0x1698 }
            byte[] r14 = r11.bytes     // Catch:{ Exception -> 0x1698 }
            r15.thumb = r14     // Catch:{ Exception -> 0x1698 }
            r48 = r2
            goto L_0x16a6
        L_0x1698:
            r0 = move-exception
            r11 = r66
            goto L_0x1780
        L_0x169d:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r15 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r15     // Catch:{ Exception -> 0x17cc }
            r48 = r2
            r14 = 0
            byte[] r2 = new byte[r14]     // Catch:{ Exception -> 0x17cc }
            r15.thumb = r2     // Catch:{ Exception -> 0x17cc }
        L_0x16a6:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x17cc }
            int r14 = r11.h     // Catch:{ Exception -> 0x17cc }
            r2.thumb_h = r14     // Catch:{ Exception -> 0x17cc }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x17cc }
            int r11 = r11.w     // Catch:{ Exception -> 0x17cc }
            r2.thumb_w = r11     // Catch:{ Exception -> 0x17cc }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x17cc }
            int r11 = r13.w     // Catch:{ Exception -> 0x17cc }
            r2.w = r11     // Catch:{ Exception -> 0x17cc }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x17cc }
            int r11 = r13.h     // Catch:{ Exception -> 0x17cc }
            r2.h = r11     // Catch:{ Exception -> 0x17cc }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x17cc }
            int r11 = r13.size     // Catch:{ Exception -> 0x17cc }
            r2.size = r11     // Catch:{ Exception -> 0x17cc }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r13.location     // Catch:{ Exception -> 0x17cc }
            byte[] r2 = r2.key     // Catch:{ Exception -> 0x17cc }
            if (r2 == 0) goto L_0x170d
            int r2 = (r67 > r16 ? 1 : (r67 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x16cf
            goto L_0x170d
        L_0x16cf:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1698 }
            r1.<init>()     // Catch:{ Exception -> 0x1698 }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r13.location     // Catch:{ Exception -> 0x1698 }
            long r11 = r2.volume_id     // Catch:{ Exception -> 0x1698 }
            r1.id = r11     // Catch:{ Exception -> 0x1698 }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r13.location     // Catch:{ Exception -> 0x1698 }
            long r11 = r2.secret     // Catch:{ Exception -> 0x1698 }
            r1.access_hash = r11     // Catch:{ Exception -> 0x1698 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x1698 }
            org.telegram.tgnet.TLRPC$FileLocation r8 = r13.location     // Catch:{ Exception -> 0x1698 }
            byte[] r8 = r8.key     // Catch:{ Exception -> 0x1698 }
            r2.key = r8     // Catch:{ Exception -> 0x1698 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r3.media     // Catch:{ Exception -> 0x1698 }
            org.telegram.tgnet.TLRPC$FileLocation r8 = r13.location     // Catch:{ Exception -> 0x1698 }
            byte[] r8 = r8.iv     // Catch:{ Exception -> 0x1698 }
            r2.iv = r8     // Catch:{ Exception -> 0x1698 }
            org.telegram.messenger.SecretChatHelper r2 = r45.getSecretChatHelper()     // Catch:{ Exception -> 0x1698 }
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner     // Catch:{ Exception -> 0x1698 }
            r11 = 0
            r48 = r2
            r49 = r3
            r50 = r8
            r51 = r9
            r52 = r1
            r53 = r11
            r54 = r4
            r48.performSendEncryptedRequest(r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x1698 }
            r1 = r58
            r11 = r66
            goto L_0x1772
        L_0x170d:
            if (r58 != 0) goto L_0x173e
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1698 }
            r2.<init>(r5)     // Catch:{ Exception -> 0x1698 }
            r2.encryptedChat = r9     // Catch:{ Exception -> 0x1698 }
            r9 = 0
            r2.type = r9     // Catch:{ Exception -> 0x1698 }
            r2.originalPath = r7     // Catch:{ Exception -> 0x1698 }
            r2.sendEncryptedRequest = r3     // Catch:{ Exception -> 0x1698 }
            r2.obj = r4     // Catch:{ Exception -> 0x1698 }
            if (r12 == 0) goto L_0x172e
            boolean r9 = r12.containsKey(r8)     // Catch:{ Exception -> 0x1698 }
            if (r9 == 0) goto L_0x172e
            java.lang.Object r1 = r12.get(r8)     // Catch:{ Exception -> 0x1698 }
            r2.parentObject = r1     // Catch:{ Exception -> 0x1698 }
            goto L_0x1730
        L_0x172e:
            r2.parentObject = r1     // Catch:{ Exception -> 0x1698 }
        L_0x1730:
            r1 = 1
            r2.performMediaUpload = r1     // Catch:{ Exception -> 0x1698 }
            r11 = r66
            if (r11 == 0) goto L_0x1739
            r1 = 1
            goto L_0x173a
        L_0x1739:
            r1 = 0
        L_0x173a:
            r2.scheduled = r1     // Catch:{ Exception -> 0x177f }
            r1 = r2
            goto L_0x1742
        L_0x173e:
            r11 = r66
            r1 = r58
        L_0x1742:
            boolean r2 = android.text.TextUtils.isEmpty(r57)     // Catch:{ Exception -> 0x17bc }
            if (r2 != 0) goto L_0x1755
            r2 = r57
            r8 = r18
            boolean r8 = r2.startsWith(r8)     // Catch:{ Exception -> 0x177f }
            if (r8 == 0) goto L_0x1755
            r1.httpLocation = r2     // Catch:{ Exception -> 0x177f }
            goto L_0x176b
        L_0x1755:
            r8 = r48
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r8.sizes     // Catch:{ Exception -> 0x17bc }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r8.sizes     // Catch:{ Exception -> 0x17bc }
            int r9 = r9.size()     // Catch:{ Exception -> 0x17bc }
            r12 = 1
            int r9 = r9 - r12
            java.lang.Object r2 = r2.get(r9)     // Catch:{ Exception -> 0x17bc }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2     // Catch:{ Exception -> 0x17bc }
            r1.photoSize = r2     // Catch:{ Exception -> 0x17bc }
            r1.locationParent = r8     // Catch:{ Exception -> 0x17bc }
        L_0x176b:
            int r2 = (r67 > r16 ? 1 : (r67 == r16 ? 0 : -1))
            if (r2 != 0) goto L_0x1772
            r10.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x177f }
        L_0x1772:
            int r2 = (r67 > r16 ? 1 : (r67 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x17be
            org.telegram.tgnet.TLObject r2 = r1.sendEncryptedRequest     // Catch:{ Exception -> 0x17bc }
            if (r2 == 0) goto L_0x1783
            org.telegram.tgnet.TLObject r2 = r1.sendEncryptedRequest     // Catch:{ Exception -> 0x177f }
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r2 = (org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia) r2     // Catch:{ Exception -> 0x177f }
            goto L_0x178a
        L_0x177f:
            r0 = move-exception
        L_0x1780:
            r1 = r0
            goto L_0x0var_
        L_0x1783:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia     // Catch:{ Exception -> 0x17bc }
            r2.<init>()     // Catch:{ Exception -> 0x17bc }
            r1.sendEncryptedRequest = r2     // Catch:{ Exception -> 0x17bc }
        L_0x178a:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r1.messageObjects     // Catch:{ Exception -> 0x17bc }
            r8.add(r4)     // Catch:{ Exception -> 0x17bc }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r8 = r1.messages     // Catch:{ Exception -> 0x17bc }
            r13 = r20
            r8.add(r13)     // Catch:{ Exception -> 0x1912 }
            java.util.ArrayList<java.lang.String> r8 = r1.originalPaths     // Catch:{ Exception -> 0x1912 }
            r8.add(r7)     // Catch:{ Exception -> 0x1912 }
            r7 = 1
            r1.performMediaUpload = r7     // Catch:{ Exception -> 0x1912 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r7 = r2.messages     // Catch:{ Exception -> 0x1912 }
            r7.add(r3)     // Catch:{ Exception -> 0x1912 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1912 }
            r3.<init>()     // Catch:{ Exception -> 0x1912 }
            r7 = r59
            r8 = 3
            if (r7 != r8) goto L_0x17af
            r16 = 1
        L_0x17af:
            r7 = r16
            r3.id = r7     // Catch:{ Exception -> 0x1912 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r2 = r2.files     // Catch:{ Exception -> 0x1912 }
            r2.add(r3)     // Catch:{ Exception -> 0x1912 }
            r10.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1912 }
            goto L_0x17c0
        L_0x17bc:
            r0 = move-exception
            goto L_0x17d2
        L_0x17be:
            r13 = r20
        L_0x17c0:
            if (r61 != 0) goto L_0x1963
            org.telegram.messenger.MediaDataController r1 = r45.getMediaDataController()     // Catch:{ Exception -> 0x1912 }
            r2 = 0
            r1.cleanDraft(r5, r2)     // Catch:{ Exception -> 0x1912 }
            goto L_0x1963
        L_0x17cc:
            r0 = move-exception
            r11 = r66
            goto L_0x17d2
        L_0x17d0:
            r0 = move-exception
            r11 = r13
        L_0x17d2:
            r13 = r20
            goto L_0x191e
        L_0x17d6:
            r0 = move-exception
            r13 = r11
            r11 = r66
            goto L_0x191e
        L_0x17dc:
            r1 = r53
            r4 = r63
            r12 = r11
            r13 = r15
            r11 = r3
            r2 = r6
            r15 = r8
            r6 = r46
            r7 = r54
            r8 = r62
            if (r9 != 0) goto L_0x1877
            org.telegram.tgnet.TLRPC$TL_messages_sendMessage r6 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage     // Catch:{ Exception -> 0x1912 }
            r6.<init>()     // Catch:{ Exception -> 0x1912 }
            r6.message = r5     // Catch:{ Exception -> 0x1912 }
            if (r61 != 0) goto L_0x17f8
            r5 = 1
            goto L_0x17f9
        L_0x17f8:
            r5 = 0
        L_0x17f9:
            r6.clear_draft = r5     // Catch:{ Exception -> 0x1912 }
            if (r65 == 0) goto L_0x181c
            int r5 = r10.currentAccount     // Catch:{ Exception -> 0x1912 }
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getNotificationsSettings(r5)     // Catch:{ Exception -> 0x1912 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1912 }
            r7.<init>()     // Catch:{ Exception -> 0x1912 }
            r7.append(r12)     // Catch:{ Exception -> 0x1912 }
            r7.append(r2)     // Catch:{ Exception -> 0x1912 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x1912 }
            r9 = 0
            boolean r5 = r5.getBoolean(r7, r9)     // Catch:{ Exception -> 0x1912 }
            if (r5 == 0) goto L_0x181a
            goto L_0x181c
        L_0x181a:
            r5 = 0
            goto L_0x181d
        L_0x181c:
            r5 = 1
        L_0x181d:
            r6.silent = r5     // Catch:{ Exception -> 0x1912 }
            r6.peer = r15     // Catch:{ Exception -> 0x1912 }
            long r14 = r13.random_id     // Catch:{ Exception -> 0x1912 }
            r6.random_id = r14     // Catch:{ Exception -> 0x1912 }
            int r5 = r13.reply_to_msg_id     // Catch:{ Exception -> 0x1912 }
            if (r5 == 0) goto L_0x1833
            int r5 = r6.flags     // Catch:{ Exception -> 0x1912 }
            r7 = 1
            r5 = r5 | r7
            r6.flags = r5     // Catch:{ Exception -> 0x1912 }
            int r5 = r13.reply_to_msg_id     // Catch:{ Exception -> 0x1912 }
            r6.reply_to_msg_id = r5     // Catch:{ Exception -> 0x1912 }
        L_0x1833:
            if (r60 != 0) goto L_0x1838
            r5 = 1
            r6.no_webpage = r5     // Catch:{ Exception -> 0x1912 }
        L_0x1838:
            if (r8 == 0) goto L_0x1849
            boolean r5 = r62.isEmpty()     // Catch:{ Exception -> 0x1912 }
            if (r5 != 0) goto L_0x1849
            r6.entities = r8     // Catch:{ Exception -> 0x1912 }
            int r5 = r6.flags     // Catch:{ Exception -> 0x1912 }
            r7 = 8
            r5 = r5 | r7
            r6.flags = r5     // Catch:{ Exception -> 0x1912 }
        L_0x1849:
            if (r11 == 0) goto L_0x1853
            r6.schedule_date = r11     // Catch:{ Exception -> 0x1912 }
            int r5 = r6.flags     // Catch:{ Exception -> 0x1912 }
            r5 = r5 | 1024(0x400, float:1.435E-42)
            r6.flags = r5     // Catch:{ Exception -> 0x1912 }
        L_0x1853:
            r5 = 0
            r7 = 0
            if (r11 == 0) goto L_0x1859
            r8 = 1
            goto L_0x185a
        L_0x1859:
            r8 = 0
        L_0x185a:
            r46 = r45
            r47 = r6
            r48 = r4
            r49 = r5
            r50 = r7
            r51 = r1
            r52 = r8
            r46.performSendMessageRequest(r47, r48, r49, r50, r51, r52)     // Catch:{ Exception -> 0x1912 }
            if (r61 != 0) goto L_0x1963
            org.telegram.messenger.MediaDataController r1 = r45.getMediaDataController()     // Catch:{ Exception -> 0x1912 }
            r5 = 0
            r1.cleanDraft(r2, r5)     // Catch:{ Exception -> 0x1912 }
            goto L_0x1963
        L_0x1877:
            int r1 = r9.layer     // Catch:{ Exception -> 0x1912 }
            int r1 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r1)     // Catch:{ Exception -> 0x1912 }
            r12 = 73
            if (r1 < r12) goto L_0x1887
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1912 }
            r1.<init>()     // Catch:{ Exception -> 0x1912 }
            goto L_0x188c
        L_0x1887:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x1912 }
            r1.<init>()     // Catch:{ Exception -> 0x1912 }
        L_0x188c:
            int r12 = r13.ttl     // Catch:{ Exception -> 0x1912 }
            r1.ttl = r12     // Catch:{ Exception -> 0x1912 }
            if (r8 == 0) goto L_0x18a0
            boolean r12 = r62.isEmpty()     // Catch:{ Exception -> 0x1912 }
            if (r12 != 0) goto L_0x18a0
            r1.entities = r8     // Catch:{ Exception -> 0x1912 }
            int r8 = r1.flags     // Catch:{ Exception -> 0x1912 }
            r8 = r8 | 128(0x80, float:1.794E-43)
            r1.flags = r8     // Catch:{ Exception -> 0x1912 }
        L_0x18a0:
            long r14 = r13.reply_to_random_id     // Catch:{ Exception -> 0x1912 }
            int r8 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r8 == 0) goto L_0x18b1
            long r14 = r13.reply_to_random_id     // Catch:{ Exception -> 0x1912 }
            r1.reply_to_random_id = r14     // Catch:{ Exception -> 0x1912 }
            int r8 = r1.flags     // Catch:{ Exception -> 0x1912 }
            r12 = 8
            r8 = r8 | r12
            r1.flags = r8     // Catch:{ Exception -> 0x1912 }
        L_0x18b1:
            if (r7 == 0) goto L_0x18c7
            java.lang.Object r8 = r7.get(r6)     // Catch:{ Exception -> 0x1912 }
            if (r8 == 0) goto L_0x18c7
            java.lang.Object r6 = r7.get(r6)     // Catch:{ Exception -> 0x1912 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x1912 }
            r1.via_bot_name = r6     // Catch:{ Exception -> 0x1912 }
            int r6 = r1.flags     // Catch:{ Exception -> 0x1912 }
            r6 = r6 | 2048(0x800, float:2.87E-42)
            r1.flags = r6     // Catch:{ Exception -> 0x1912 }
        L_0x18c7:
            long r6 = r13.random_id     // Catch:{ Exception -> 0x1912 }
            r1.random_id = r6     // Catch:{ Exception -> 0x1912 }
            r1.message = r5     // Catch:{ Exception -> 0x1912 }
            r5 = r33
            if (r5 == 0) goto L_0x18e7
            java.lang.String r6 = r5.url     // Catch:{ Exception -> 0x1912 }
            if (r6 == 0) goto L_0x18e7
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage     // Catch:{ Exception -> 0x1912 }
            r6.<init>()     // Catch:{ Exception -> 0x1912 }
            r1.media = r6     // Catch:{ Exception -> 0x1912 }
            java.lang.String r5 = r5.url     // Catch:{ Exception -> 0x1912 }
            r6.url = r5     // Catch:{ Exception -> 0x1912 }
            int r5 = r1.flags     // Catch:{ Exception -> 0x1912 }
            r5 = r5 | 512(0x200, float:7.175E-43)
            r1.flags = r5     // Catch:{ Exception -> 0x1912 }
            goto L_0x18ee
        L_0x18e7:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty     // Catch:{ Exception -> 0x1912 }
            r5.<init>()     // Catch:{ Exception -> 0x1912 }
            r1.media = r5     // Catch:{ Exception -> 0x1912 }
        L_0x18ee:
            org.telegram.messenger.SecretChatHelper r5 = r45.getSecretChatHelper()     // Catch:{ Exception -> 0x1912 }
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner     // Catch:{ Exception -> 0x1912 }
            r7 = 0
            r8 = 0
            r48 = r5
            r49 = r1
            r50 = r6
            r51 = r9
            r52 = r7
            r53 = r8
            r54 = r4
            r48.performSendEncryptedRequest(r49, r50, r51, r52, r53, r54)     // Catch:{ Exception -> 0x1912 }
            if (r61 != 0) goto L_0x1963
            org.telegram.messenger.MediaDataController r1 = r45.getMediaDataController()     // Catch:{ Exception -> 0x1912 }
            r5 = 0
            r1.cleanDraft(r2, r5)     // Catch:{ Exception -> 0x1912 }
            goto L_0x1963
        L_0x1912:
            r0 = move-exception
            goto L_0x191e
        L_0x1914:
            r0 = move-exception
            r4 = r63
            r11 = r3
            r13 = r15
            goto L_0x191e
        L_0x191a:
            r0 = move-exception
            r4 = r11
            r13 = r15
            r11 = r3
        L_0x191e:
            r1 = r0
            r5 = r13
            goto L_0x1933
        L_0x1921:
            r0 = move-exception
            goto L_0x1926
        L_0x1923:
            r0 = move-exception
            r10 = r45
        L_0x1926:
            r11 = r3
            r13 = r15
            r1 = r0
            r5 = r13
            goto L_0x0295
        L_0x192c:
            r0 = move-exception
            r10 = r45
            r11 = r3
            r1 = r0
            goto L_0x0295
        L_0x1933:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            org.telegram.messenger.MessagesStorage r1 = r45.getMessagesStorage()
            if (r11 == 0) goto L_0x193e
            r2 = 1
            goto L_0x193f
        L_0x193e:
            r2 = 0
        L_0x193f:
            r1.markMessageAsSendError(r5, r2)
            if (r4 == 0) goto L_0x1949
            org.telegram.tgnet.TLRPC$Message r1 = r4.messageOwner
            r2 = 2
            r1.send_state = r2
        L_0x1949:
            org.telegram.messenger.NotificationCenter r1 = r45.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messageSendError
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r4 = r5.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r6 = 0
            r3[r6] = r4
            r1.postNotificationName(r2, r3)
            int r1 = r5.id
            r10.processSentMessage(r1)
        L_0x1963:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, org.telegram.tgnet.TLRPC$TL_messageMediaPoll, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, boolean, int, int, java.lang.Object):void");
    }

    private void performSendDelayedMessage(DelayedMessage delayedMessage) {
        performSendDelayedMessage(delayedMessage, -1);
    }

    private TLRPC$PhotoSize getThumbForSecretChat(ArrayList<TLRPC$PhotoSize> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                TLRPC$PhotoSize tLRPC$PhotoSize = arrayList.get(i);
                if (tLRPC$PhotoSize == null || (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) || (tLRPC$PhotoSize instanceof TLRPC$TL_photoSizeEmpty) || tLRPC$PhotoSize.location == null) {
                    i++;
                } else {
                    TLRPC$TL_photoSize tLRPC$TL_photoSize = new TLRPC$TL_photoSize();
                    tLRPC$TL_photoSize.type = tLRPC$PhotoSize.type;
                    tLRPC$TL_photoSize.w = tLRPC$PhotoSize.w;
                    tLRPC$TL_photoSize.h = tLRPC$PhotoSize.h;
                    tLRPC$TL_photoSize.size = tLRPC$PhotoSize.size;
                    byte[] bArr = tLRPC$PhotoSize.bytes;
                    tLRPC$TL_photoSize.bytes = bArr;
                    if (bArr == null) {
                        tLRPC$TL_photoSize.bytes = new byte[0];
                    }
                    TLRPC$TL_fileLocation_layer82 tLRPC$TL_fileLocation_layer82 = new TLRPC$TL_fileLocation_layer82();
                    tLRPC$TL_photoSize.location = tLRPC$TL_fileLocation_layer82;
                    TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
                    tLRPC$TL_fileLocation_layer82.dc_id = tLRPC$FileLocation.dc_id;
                    tLRPC$TL_fileLocation_layer82.volume_id = tLRPC$FileLocation.volume_id;
                    tLRPC$TL_fileLocation_layer82.local_id = tLRPC$FileLocation.local_id;
                    tLRPC$TL_fileLocation_layer82.secret = tLRPC$FileLocation.secret;
                    return tLRPC$TL_photoSize;
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
        TLRPC$InputMedia tLRPC$InputMedia;
        TLRPC$InputMedia tLRPC$InputMedia2;
        TLRPC$InputMedia tLRPC$InputMedia3;
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
                        if (tLObject instanceof TLRPC$TL_messages_sendMedia) {
                            tLRPC$InputMedia3 = ((TLRPC$TL_messages_sendMedia) tLObject).media;
                        } else {
                            tLRPC$InputMedia3 = ((TLRPC$TL_messages_editMessage) tLObject).media;
                        }
                        VideoEditedInfo videoEditedInfo3 = delayedMessage2.videoEditedInfo;
                        tLRPC$InputMedia3.file = videoEditedInfo3.file;
                        videoEditedInfo3.file = null;
                    } else if (videoEditedInfo2.encryptedFile != null) {
                        TLRPC$TL_decryptedMessage tLRPC$TL_decryptedMessage = (TLRPC$TL_decryptedMessage) delayedMessage2.sendEncryptedRequest;
                        TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia = tLRPC$TL_decryptedMessage.media;
                        tLRPC$DecryptedMessageMedia.size = (int) videoEditedInfo2.estimatedSize;
                        tLRPC$DecryptedMessageMedia.key = videoEditedInfo2.key;
                        tLRPC$DecryptedMessageMedia.iv = videoEditedInfo2.iv;
                        SecretChatHelper secretChatHelper = getSecretChatHelper();
                        MessageObject messageObject2 = delayedMessage2.obj;
                        secretChatHelper.performSendEncryptedRequest(tLRPC$TL_decryptedMessage, messageObject2.messageOwner, delayedMessage2.encryptedChat, delayedMessage2.videoEditedInfo.encryptedFile, delayedMessage2.originalPath, messageObject2);
                        delayedMessage2.videoEditedInfo.encryptedFile = null;
                        return;
                    }
                }
                TLObject tLObject2 = delayedMessage2.sendRequest;
                if (tLObject2 != null) {
                    if (tLObject2 instanceof TLRPC$TL_messages_sendMedia) {
                        tLRPC$InputMedia2 = ((TLRPC$TL_messages_sendMedia) tLObject2).media;
                    } else {
                        tLRPC$InputMedia2 = ((TLRPC$TL_messages_editMessage) tLObject2).media;
                    }
                    if (tLRPC$InputMedia2.file == null) {
                        MessageObject messageObject3 = delayedMessage2.obj;
                        String str2 = messageObject3.messageOwner.attachPath;
                        TLRPC$Document document = messageObject3.getDocument();
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
                TLRPC$Document document2 = messageObject4.getDocument();
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
            TLRPC$Document document3 = messageObject5.getDocument();
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
                if (tLObject3 instanceof TLRPC$TL_messages_sendMedia) {
                    tLRPC$InputMedia = ((TLRPC$TL_messages_sendMedia) tLObject3).media;
                } else {
                    tLRPC$InputMedia = ((TLRPC$TL_messages_editMessage) tLObject3).media;
                }
                if (tLRPC$InputMedia.file == null) {
                    String str9 = delayedMessage2.obj.messageOwner.attachPath;
                    putToDelayedMessages(str9, delayedMessage2);
                    FileLoader fileLoader = getFileLoader();
                    if (delayedMessage2.sendRequest != null) {
                        z4 = false;
                    }
                    fileLoader.uploadFile(str9, z4, false, 67108864);
                    putToUploadingMessages(delayedMessage2.obj);
                } else if (tLRPC$InputMedia.thumb == null && delayedMessage2.photoSize != null) {
                    String str10 = FileLoader.getDirectory(4) + "/" + delayedMessage2.photoSize.location.volume_id + "_" + delayedMessage2.photoSize.location.local_id + ".jpg";
                    putToDelayedMessages(str10, delayedMessage2);
                    getFileLoader().uploadFile(str10, false, true, 16777216);
                    putToUploadingMessages(delayedMessage2.obj);
                }
            } else {
                MessageObject messageObject6 = delayedMessage2.obj;
                String str11 = messageObject6.messageOwner.attachPath;
                TLRPC$Document document4 = messageObject6.getDocument();
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
                        TLRPC$Document document5 = messageObject7.getDocument();
                        if (str13 == null) {
                            str13 = FileLoader.getDirectory(4) + "/" + document5.id + ".mp4";
                        }
                        putToDelayedMessages(str13, delayedMessage2);
                        delayedMessage2.extraHashMap.put(messageObject7, str13);
                        delayedMessage2.extraHashMap.put(str13 + "_i", messageObject7);
                        TLRPC$PhotoSize tLRPC$PhotoSize = delayedMessage2.photoSize;
                        if (!(tLRPC$PhotoSize == null || tLRPC$PhotoSize.location == null)) {
                            delayedMessage2.extraHashMap.put(str13 + "_t", delayedMessage2.photoSize);
                        }
                        MediaController.getInstance().scheduleVideoConvert(messageObject7);
                        delayedMessage2.obj = messageObject7;
                        putToUploadingMessages(messageObject7);
                    } else {
                        TLRPC$Document document6 = messageObject7.getDocument();
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
                            TLRPC$InputMedia tLRPC$InputMedia4 = ((TLRPC$TL_messages_sendMultiMedia) tLObject4).multi_media.get(size).media;
                            if (tLRPC$InputMedia4.file == null) {
                                putToDelayedMessages(str14, delayedMessage2);
                                MessageObject messageObject8 = messageObject;
                                delayedMessage2.extraHashMap.put(messageObject8, str14);
                                delayedMessage2.extraHashMap.put(str14, tLRPC$InputMedia4);
                                delayedMessage2.extraHashMap.put(str14 + "_i", messageObject8);
                                TLRPC$PhotoSize tLRPC$PhotoSize2 = delayedMessage2.photoSize;
                                if (!(tLRPC$PhotoSize2 == null || tLRPC$PhotoSize2.location == null)) {
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
                                delayedMessage2.extraHashMap.put(str15, tLRPC$InputMedia4);
                                getFileLoader().uploadFile(str15, false, true, 16777216);
                                putToUploadingMessages(messageObject9);
                            }
                        } else {
                            MessageObject messageObject10 = messageObject;
                            putToDelayedMessages(str14, delayedMessage2);
                            delayedMessage2.extraHashMap.put(messageObject10, str14);
                            delayedMessage2.extraHashMap.put(str14, ((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage2.sendEncryptedRequest).files.get(size));
                            delayedMessage2.extraHashMap.put(str14 + "_i", messageObject10);
                            TLRPC$PhotoSize tLRPC$PhotoSize3 = delayedMessage2.photoSize;
                            if (!(tLRPC$PhotoSize3 == null || tLRPC$PhotoSize3.location == null)) {
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
                            obj = ((TLRPC$TL_messages_sendMultiMedia) tLObject5).multi_media.get(size).media;
                        } else {
                            obj = ((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage2.sendEncryptedRequest).files.get(size);
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
                z = true;
                if (!delayedMessage2.messageObjects.isEmpty()) {
                    ArrayList<MessageObject> arrayList = delayedMessage2.messageObjects;
                    putToSendingMessages(arrayList.get(arrayList.size() - 1).messageOwner, delayedMessage2.finalGroupMessage != 0);
                }
            }
            sendReadyToSendGroup(delayedMessage2, z5, z);
        }
    }

    private void uploadMultiMedia(DelayedMessage delayedMessage, TLRPC$InputMedia tLRPC$InputMedia, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, String str) {
        if (tLRPC$InputMedia != null) {
            TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = (TLRPC$TL_messages_sendMultiMedia) delayedMessage.sendRequest;
            int i = 0;
            while (true) {
                if (i >= tLRPC$TL_messages_sendMultiMedia.multi_media.size()) {
                    break;
                } else if (tLRPC$TL_messages_sendMultiMedia.multi_media.get(i).media == tLRPC$InputMedia) {
                    putToSendingMessages(delayedMessage.messages.get(i), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, -1L, -1L, false);
                    break;
                } else {
                    i++;
                }
            }
            TLRPC$TL_messages_uploadMedia tLRPC$TL_messages_uploadMedia = new TLRPC$TL_messages_uploadMedia();
            tLRPC$TL_messages_uploadMedia.media = tLRPC$InputMedia;
            tLRPC$TL_messages_uploadMedia.peer = ((TLRPC$TL_messages_sendMultiMedia) delayedMessage.sendRequest).peer;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_uploadMedia, new RequestDelegate(tLRPC$InputMedia, delayedMessage) {
                private final /* synthetic */ TLRPC$InputMedia f$1;
                private final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SendMessagesHelper.this.lambda$uploadMultiMedia$27$SendMessagesHelper(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
        } else if (tLRPC$InputEncryptedFile != null) {
            TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
            int i2 = 0;
            while (true) {
                if (i2 >= tLRPC$TL_messages_sendEncryptedMultiMedia.files.size()) {
                    break;
                } else if (tLRPC$TL_messages_sendEncryptedMultiMedia.files.get(i2) == tLRPC$InputEncryptedFile) {
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

    public /* synthetic */ void lambda$uploadMultiMedia$27$SendMessagesHelper(TLRPC$InputMedia tLRPC$InputMedia, DelayedMessage delayedMessage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$InputMedia, delayedMessage) {
            private final /* synthetic */ TLObject f$1;
            private final /* synthetic */ TLRPC$InputMedia f$2;
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
    /* JADX WARNING: Removed duplicated region for block: B:13:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0085  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$26$SendMessagesHelper(org.telegram.tgnet.TLObject r6, org.telegram.tgnet.TLRPC$InputMedia r7, org.telegram.messenger.SendMessagesHelper.DelayedMessage r8) {
        /*
            r5 = this;
            if (r6 == 0) goto L_0x004a
            org.telegram.tgnet.TLRPC$MessageMedia r6 = (org.telegram.tgnet.TLRPC$MessageMedia) r6
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto
            if (r0 == 0) goto L_0x0027
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0027
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputPhoto
            r1.<init>()
            r0.id = r1
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            long r2 = r6.id
            r1.id = r2
            long r2 = r6.access_hash
            r1.access_hash = r2
            byte[] r6 = r6.file_reference
            r1.file_reference = r6
            goto L_0x004b
        L_0x0027:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument
            if (r0 == 0) goto L_0x004a
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L_0x004a
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument
            r0.<init>()
            org.telegram.tgnet.TLRPC$TL_inputDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputDocument
            r1.<init>()
            r0.id = r1
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            long r2 = r6.id
            r1.id = r2
            long r2 = r6.access_hash
            r1.access_hash = r2
            byte[] r6 = r6.file_reference
            r1.file_reference = r6
            goto L_0x004b
        L_0x004a:
            r0 = 0
        L_0x004b:
            if (r0 == 0) goto L_0x0085
            int r6 = r7.ttl_seconds
            r1 = 1
            if (r6 == 0) goto L_0x0059
            r0.ttl_seconds = r6
            int r6 = r0.flags
            r6 = r6 | r1
            r0.flags = r6
        L_0x0059:
            org.telegram.tgnet.TLObject r6 = r8.sendRequest
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r6 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r6
            r2 = 0
            r3 = 0
        L_0x005f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r6.multi_media
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0081
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r6.multi_media
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r4 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r4
            org.telegram.tgnet.TLRPC$InputMedia r4 = r4.media
            if (r4 != r7) goto L_0x007e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r6 = r6.multi_media
            java.lang.Object r6 = r6.get(r3)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r6 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r6
            r6.media = r0
            goto L_0x0081
        L_0x007e:
            int r3 = r3 + 1
            goto L_0x005f
        L_0x0081:
            r5.sendReadyToSendGroup(r8, r2, r1)
            goto L_0x0088
        L_0x0085:
            r8.markAsError()
        L_0x0088:
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
            if (tLObject instanceof TLRPC$TL_messages_sendMultiMedia) {
                TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = (TLRPC$TL_messages_sendMultiMedia) tLObject;
                while (i2 < tLRPC$TL_messages_sendMultiMedia.multi_media.size()) {
                    TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_messages_sendMultiMedia.multi_media.get(i2).media;
                    if (!(tLRPC$InputMedia instanceof TLRPC$TL_inputMediaUploadedPhoto) && !(tLRPC$InputMedia instanceof TLRPC$TL_inputMediaUploadedDocument)) {
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
                TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
                while (i2 < tLRPC$TL_messages_sendEncryptedMultiMedia.files.size()) {
                    if (!(tLRPC$TL_messages_sendEncryptedMultiMedia.files.get(i2) instanceof TLRPC$TL_inputEncryptedFile)) {
                        i2++;
                    } else {
                        return;
                    }
                }
            }
            TLObject tLObject2 = delayedMessage.sendRequest;
            if (tLObject2 instanceof TLRPC$TL_messages_sendMultiMedia) {
                performSendMessageRequestMulti((TLRPC$TL_messages_sendMultiMedia) tLObject2, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage, delayedMessage.scheduled);
            } else {
                getSecretChatHelper().performSendEncryptedRequest((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest, delayedMessage);
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
    public void putToSendingMessages(TLRPC$Message tLRPC$Message, boolean z) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, z) {
                private final /* synthetic */ TLRPC$Message f$1;
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
            putToSendingMessages(tLRPC$Message, z, true);
        }
    }

    public /* synthetic */ void lambda$putToSendingMessages$30$SendMessagesHelper(TLRPC$Message tLRPC$Message, boolean z) {
        putToSendingMessages(tLRPC$Message, z, true);
    }

    /* access modifiers changed from: protected */
    public void putToSendingMessages(TLRPC$Message tLRPC$Message, boolean z, boolean z2) {
        if (tLRPC$Message != null) {
            int i = tLRPC$Message.id;
            if (i > 0) {
                this.editingMessages.put(i, tLRPC$Message);
                return;
            }
            boolean z3 = this.sendingMessages.indexOfKey(i) >= 0;
            removeFromUploadingMessages(tLRPC$Message.id, z);
            this.sendingMessages.put(tLRPC$Message.id, tLRPC$Message);
            if (!z && !z3) {
                long dialogId = MessageObject.getDialogId(tLRPC$Message);
                LongSparseArray<Integer> longSparseArray = this.sendingMessagesIdDialogs;
                longSparseArray.put(dialogId, Integer.valueOf(longSparseArray.get(dialogId, 0).intValue() + 1));
                if (z2) {
                    getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public TLRPC$Message removeFromSendingMessages(int i, boolean z) {
        long dialogId;
        Integer num;
        if (i > 0) {
            TLRPC$Message tLRPC$Message = this.editingMessages.get(i);
            if (tLRPC$Message == null) {
                return tLRPC$Message;
            }
            this.editingMessages.remove(i);
            return tLRPC$Message;
        }
        TLRPC$Message tLRPC$Message2 = this.sendingMessages.get(i);
        if (tLRPC$Message2 != null) {
            this.sendingMessages.remove(i);
            if (!z && (num = this.sendingMessagesIdDialogs.get(dialogId)) != null) {
                int intValue = num.intValue() - 1;
                if (intValue <= 0) {
                    this.sendingMessagesIdDialogs.remove(dialogId);
                } else {
                    this.sendingMessagesIdDialogs.put((dialogId = MessageObject.getDialogId(tLRPC$Message2)), Integer.valueOf(intValue));
                }
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
        return tLRPC$Message2;
    }

    public int getSendingMessageId(long j) {
        for (int i = 0; i < this.sendingMessages.size(); i++) {
            TLRPC$Message valueAt = this.sendingMessages.valueAt(i);
            if (valueAt.dialog_id == j) {
                return valueAt.id;
            }
        }
        for (int i2 = 0; i2 < this.uploadMessages.size(); i2++) {
            TLRPC$Message valueAt2 = this.uploadMessages.valueAt(i2);
            if (valueAt2.dialog_id == j) {
                return valueAt2.id;
            }
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void putToUploadingMessages(MessageObject messageObject) {
        if (messageObject != null && messageObject.getId() <= 0 && !messageObject.scheduled) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            boolean z = this.uploadMessages.indexOfKey(tLRPC$Message.id) >= 0;
            this.uploadMessages.put(tLRPC$Message.id, tLRPC$Message);
            if (!z) {
                long dialogId = MessageObject.getDialogId(tLRPC$Message);
                LongSparseArray<Integer> longSparseArray = this.uploadingMessagesIdDialogs;
                longSparseArray.put(dialogId, Integer.valueOf(longSparseArray.get(dialogId, 0).intValue() + 1));
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void removeFromUploadingMessages(int i, boolean z) {
        TLRPC$Message tLRPC$Message;
        if (i <= 0 && !z && (tLRPC$Message = this.uploadMessages.get(i)) != null) {
            this.uploadMessages.remove(i);
            long dialogId = MessageObject.getDialogId(tLRPC$Message);
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
    public void performSendMessageRequestMulti(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, ArrayList<MessageObject> arrayList, ArrayList<String> arrayList2, ArrayList<Object> arrayList3, DelayedMessage delayedMessage, boolean z) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ArrayList<MessageObject> arrayList4 = arrayList;
            putToSendingMessages(arrayList.get(i).messageOwner, z);
        }
        ConnectionsManager connectionsManager = getConnectionsManager();
        $$Lambda$SendMessagesHelper$ZYsiGPlj9hRHiRl2LlxJG53Fm0s r2 = new RequestDelegate(arrayList3, tLRPC$TL_messages_sendMultiMedia, arrayList, arrayList2, delayedMessage, z) {
            private final /* synthetic */ ArrayList f$1;
            private final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$2;
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

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$performSendMessageRequestMulti$38$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
            }
        };
        TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia2 = tLRPC$TL_messages_sendMultiMedia;
        connectionsManager.sendRequest((TLObject) tLRPC$TL_messages_sendMultiMedia, (RequestDelegate) r2, (QuickAckDelegate) null, 68);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$38$SendMessagesHelper(ArrayList arrayList, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList arrayList4 = arrayList;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        if (tLRPC$TL_error2 != null && FileRefController.isFileRefError(tLRPC$TL_error2.text)) {
            if (arrayList4 != null) {
                ArrayList arrayList5 = new ArrayList(arrayList);
                getFileRefController().requestReference(arrayList5, tLRPC$TL_messages_sendMultiMedia, arrayList2, arrayList3, arrayList5, delayedMessage, Boolean.valueOf(z));
                return;
            } else if (delayedMessage != null) {
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_messages_sendMultiMedia, delayedMessage, arrayList2, z) {
                    private final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$1;
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
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, arrayList2, arrayList3, z, tLRPC$TL_messages_sendMultiMedia) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ ArrayList f$3;
            private final /* synthetic */ ArrayList f$4;
            private final /* synthetic */ boolean f$5;
            private final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$6;

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

    public /* synthetic */ void lambda$null$31$SendMessagesHelper(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, DelayedMessage delayedMessage, ArrayList arrayList, boolean z) {
        int size = tLRPC$TL_messages_sendMultiMedia.multi_media.size();
        for (int i = 0; i < size; i++) {
            if (delayedMessage.parentObjects.get(i) != null) {
                removeFromSendingMessages(((MessageObject) arrayList.get(i)).getId(), z);
                TLRPC$TL_inputSingleMedia tLRPC$TL_inputSingleMedia = tLRPC$TL_messages_sendMultiMedia.multi_media.get(i);
                TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_inputSingleMedia.media;
                if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaPhoto) {
                    tLRPC$TL_inputSingleMedia.media = delayedMessage.inputMedias.get(i);
                } else if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaDocument) {
                    tLRPC$TL_inputSingleMedia.media = delayedMessage.inputMedias.get(i);
                }
                delayedMessage.videoEditedInfo = delayedMessage.videoEditedInfos.get(i);
                delayedMessage.httpLocation = delayedMessage.httpLocations.get(i);
                delayedMessage.photoSize = delayedMessage.locations.get(i);
                delayedMessage.performMediaUpload = true;
                performSendDelayedMessage(delayedMessage, i);
            }
        }
    }

    public /* synthetic */ void lambda$null$37$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, ArrayList arrayList, ArrayList arrayList2, boolean z, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia) {
        boolean z2;
        TLRPC$Updates tLRPC$Updates;
        boolean z3;
        TLRPC$Message tLRPC$Message;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        ArrayList arrayList3 = arrayList;
        boolean z4 = z;
        if (tLRPC$TL_error2 == null) {
            SparseArray sparseArray = new SparseArray();
            LongSparseArray longSparseArray = new LongSparseArray();
            TLRPC$Updates tLRPC$Updates2 = (TLRPC$Updates) tLObject;
            ArrayList<TLRPC$Update> arrayList4 = tLRPC$Updates2.updates;
            int i = 0;
            while (i < arrayList4.size()) {
                TLRPC$Update tLRPC$Update = arrayList4.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateMessageID) {
                    TLRPC$TL_updateMessageID tLRPC$TL_updateMessageID = (TLRPC$TL_updateMessageID) tLRPC$Update;
                    longSparseArray.put(tLRPC$TL_updateMessageID.random_id, Integer.valueOf(tLRPC$TL_updateMessageID.id));
                    arrayList4.remove(i);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewMessage) {
                    TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage = (TLRPC$TL_updateNewMessage) tLRPC$Update;
                    TLRPC$Message tLRPC$Message2 = tLRPC$TL_updateNewMessage.message;
                    sparseArray.put(tLRPC$Message2.id, tLRPC$Message2);
                    Utilities.stageQueue.postRunnable(new Runnable(tLRPC$TL_updateNewMessage) {
                        private final /* synthetic */ TLRPC$TL_updateNewMessage f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$32$SendMessagesHelper(this.f$1);
                        }
                    });
                    arrayList4.remove(i);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) {
                    TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage = (TLRPC$TL_updateNewChannelMessage) tLRPC$Update;
                    TLRPC$Message tLRPC$Message3 = tLRPC$TL_updateNewChannelMessage.message;
                    sparseArray.put(tLRPC$Message3.id, tLRPC$Message3);
                    Utilities.stageQueue.postRunnable(new Runnable(tLRPC$TL_updateNewChannelMessage) {
                        private final /* synthetic */ TLRPC$TL_updateNewChannelMessage f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$33$SendMessagesHelper(this.f$1);
                        }
                    });
                    arrayList4.remove(i);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewScheduledMessage) {
                    TLRPC$Message tLRPC$Message4 = ((TLRPC$TL_updateNewScheduledMessage) tLRPC$Update).message;
                    sparseArray.put(tLRPC$Message4.id, tLRPC$Message4);
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
                    tLRPC$Updates = tLRPC$Updates2;
                    z3 = false;
                    break;
                }
                MessageObject messageObject = (MessageObject) arrayList3.get(i2);
                String str = (String) arrayList2.get(i2);
                TLRPC$Message tLRPC$Message5 = messageObject.messageOwner;
                int i3 = tLRPC$Message5.id;
                ArrayList arrayList5 = new ArrayList();
                String str2 = tLRPC$Message5.attachPath;
                Integer num = (Integer) longSparseArray.get(tLRPC$Message5.random_id);
                if (num == null || (tLRPC$Message = (TLRPC$Message) sparseArray.get(num.intValue())) == null) {
                    tLRPC$Updates = tLRPC$Updates2;
                    z3 = true;
                } else {
                    MessageObject.getDialogId(tLRPC$Message);
                    arrayList5.add(tLRPC$Message);
                    ArrayList arrayList6 = arrayList5;
                    int i4 = i3;
                    TLRPC$Message tLRPC$Message6 = tLRPC$Message5;
                    updateMediaPaths(messageObject, tLRPC$Message, tLRPC$Message.id, str, false);
                    int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                    tLRPC$Message6.id = tLRPC$Message.id;
                    if ((tLRPC$Message6.flags & Integer.MIN_VALUE) != 0) {
                        tLRPC$Message.flags |= Integer.MIN_VALUE;
                    }
                    long j = tLRPC$Message.grouped_id;
                    if (!z4) {
                        Integer num2 = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(tLRPC$Message.dialog_id));
                        if (num2 == null) {
                            num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(tLRPC$Message.out, tLRPC$Message.dialog_id));
                            getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(tLRPC$Message.dialog_id), num2);
                        }
                        tLRPC$Message.unread = num2.intValue() < tLRPC$Message.id;
                    }
                    getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                    tLRPC$Message6.send_state = 0;
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i4), Integer.valueOf(tLRPC$Message6.id), tLRPC$Message6, Long.valueOf(tLRPC$Message6.dialog_id), Long.valueOf(j), Integer.valueOf(mediaExistanceFlags), Boolean.valueOf(z));
                    DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                    SparseArray sparseArray2 = sparseArray;
                    $$Lambda$SendMessagesHelper$IfuL9XcMh89YHY1N1peNifOal8 r15 = r0;
                    $$Lambda$SendMessagesHelper$IfuL9XcMh89YHY1N1peNifOal8 r0 = new Runnable(tLRPC$Message6, i4, z, arrayList6, j, mediaExistanceFlags) {
                        private final /* synthetic */ TLRPC$Message f$1;
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
                    tLRPC$Updates2 = tLRPC$Updates2;
                    longSparseArray = longSparseArray;
                }
            }
            tLRPC$Updates = tLRPC$Updates2;
            z3 = true;
            Utilities.stageQueue.postRunnable(new Runnable(tLRPC$Updates) {
                private final /* synthetic */ TLRPC$Updates f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$36$SendMessagesHelper(this.f$1);
                }
            });
            z2 = z3;
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error2, (BaseFragment) null, tLRPC$TL_messages_sendMultiMedia, new Object[0]);
            z2 = true;
        }
        if (z2) {
            for (int i5 = 0; i5 < arrayList.size(); i5++) {
                TLRPC$Message tLRPC$Message7 = ((MessageObject) arrayList3.get(i5)).messageOwner;
                getMessagesStorage().markMessageAsSendError(tLRPC$Message7, z4);
                tLRPC$Message7.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message7.id));
                processSentMessage(tLRPC$Message7.id);
                removeFromSendingMessages(tLRPC$Message7.id, z4);
            }
        }
    }

    public /* synthetic */ void lambda$null$32$SendMessagesHelper(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$33$SendMessagesHelper(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$35$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, boolean z, ArrayList arrayList, long j, int i2) {
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, Integer.valueOf(i), tLRPC$Message2.id, 0, false, tLRPC$Message2.to_id.channel_id, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, i, j, i2, z) {
            private final /* synthetic */ TLRPC$Message f$1;
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

    public /* synthetic */ void lambda$null$34$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, long j, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), Long.valueOf(j), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$36$SendMessagesHelper(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
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
            for (int i4 = 0; i4 < size; i4++) {
                DelayedMessage delayedMessage2 = (DelayedMessage) arrayList.get(i4);
                int i5 = delayedMessage2.type;
                if ((i5 == 4 || i5 == 0) && delayedMessage2.peer == j) {
                    MessageObject messageObject = delayedMessage2.obj;
                    if (messageObject != null) {
                        i2 = messageObject.getId();
                    } else {
                        ArrayList<MessageObject> arrayList2 = delayedMessage2.messageObjects;
                        if (arrayList2 == null || arrayList2.isEmpty()) {
                            i2 = 0;
                        } else {
                            ArrayList<MessageObject> arrayList3 = delayedMessage2.messageObjects;
                            i2 = arrayList3.get(arrayList3.size() - 1).getId();
                        }
                    }
                    if (i2 != 0 && i2 > i && delayedMessage == null && i3 < i2) {
                        delayedMessage = delayedMessage2;
                        i3 = i2;
                    }
                }
            }
        }
        return delayedMessage;
    }

    /* access modifiers changed from: protected */
    public void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, Object obj, boolean z2) {
        DelayedMessage findMaxDelayedMessageForMessageId;
        ArrayList<DelayedMessageSendAfterRequest> arrayList;
        TLObject tLObject2 = tLObject;
        DelayedMessage delayedMessage3 = delayedMessage;
        if ((tLObject2 instanceof TLRPC$TL_messages_editMessage) || !z || (findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(messageObject.getId(), messageObject.getDialogId())) == null) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            boolean z3 = z2;
            putToSendingMessages(tLRPC$Message, z3);
            $$Lambda$SendMessagesHelper$anaw0NqrAe1M6q_iXvar_cgrWPg r14 = r0;
            ConnectionsManager connectionsManager = getConnectionsManager();
            $$Lambda$SendMessagesHelper$anaw0NqrAe1M6q_iXvar_cgrWPg r0 = new RequestDelegate(tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, z3, tLRPC$Message) {
                private final /* synthetic */ TLObject f$1;
                private final /* synthetic */ Object f$2;
                private final /* synthetic */ MessageObject f$3;
                private final /* synthetic */ String f$4;
                private final /* synthetic */ SendMessagesHelper.DelayedMessage f$5;
                private final /* synthetic */ boolean f$6;
                private final /* synthetic */ SendMessagesHelper.DelayedMessage f$7;
                private final /* synthetic */ boolean f$8;
                private final /* synthetic */ TLRPC$Message f$9;

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

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$52$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tLRPC$TL_error);
                }
            };
            tLRPC$Message.reqId = connectionsManager.sendRequest(tLObject2, (RequestDelegate) r14, (QuickAckDelegate) new QuickAckDelegate(tLRPC$Message) {
                private final /* synthetic */ TLRPC$Message f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$54$SendMessagesHelper(this.f$1);
                }
            }, (tLObject2 instanceof TLRPC$TL_messages_sendMessage ? 128 : 0) | 68);
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

    public /* synthetic */ void lambda$performSendMessageRequest$52$SendMessagesHelper(TLObject tLObject, Object obj, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, boolean z2, TLRPC$Message tLRPC$Message, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        TLObject tLObject3 = tLObject;
        Object obj2 = obj;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        if (tLRPC$TL_error2 != null && (((tLObject3 instanceof TLRPC$TL_messages_sendMedia) || (tLObject3 instanceof TLRPC$TL_messages_editMessage)) && FileRefController.isFileRefError(tLRPC$TL_error2.text))) {
            if (obj2 != null) {
                getFileRefController().requestReference(obj, tLObject3, messageObject, str, delayedMessage, Boolean.valueOf(z), delayedMessage2, Boolean.valueOf(z2));
                return;
            } else if (delayedMessage2 != null) {
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, z2, tLObject, delayedMessage2) {
                    private final /* synthetic */ TLRPC$Message f$1;
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
        if (tLObject3 instanceof TLRPC$TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, z2, tLObject) {
                private final /* synthetic */ TLRPC$TL_error f$1;
                private final /* synthetic */ TLRPC$Message f$2;
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
            AndroidUtilities.runOnUIThread(new Runnable(z2, tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, tLObject) {
                private final /* synthetic */ boolean f$1;
                private final /* synthetic */ TLRPC$TL_error f$2;
                private final /* synthetic */ TLRPC$Message f$3;
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

    public /* synthetic */ void lambda$null$39$SendMessagesHelper(TLRPC$Message tLRPC$Message, boolean z, TLObject tLObject, DelayedMessage delayedMessage) {
        removeFromSendingMessages(tLRPC$Message.id, z);
        if (tLObject instanceof TLRPC$TL_messages_sendMedia) {
            TLRPC$TL_messages_sendMedia tLRPC$TL_messages_sendMedia = (TLRPC$TL_messages_sendMedia) tLObject;
            TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_messages_sendMedia.media;
            if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaPhoto) {
                tLRPC$TL_messages_sendMedia.media = delayedMessage.inputUploadMedia;
            } else if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaDocument) {
                tLRPC$TL_messages_sendMedia.media = delayedMessage.inputUploadMedia;
            }
        } else if (tLObject instanceof TLRPC$TL_messages_editMessage) {
            TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage = (TLRPC$TL_messages_editMessage) tLObject;
            TLRPC$InputMedia tLRPC$InputMedia2 = tLRPC$TL_messages_editMessage.media;
            if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaPhoto) {
                tLRPC$TL_messages_editMessage.media = delayedMessage.inputUploadMedia;
            } else if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaDocument) {
                tLRPC$TL_messages_editMessage.media = delayedMessage.inputUploadMedia;
            }
        }
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
    }

    public /* synthetic */ void lambda$null$42$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, TLRPC$Message tLRPC$Message, TLObject tLObject, MessageObject messageObject, String str, boolean z, TLObject tLObject2) {
        int i = 0;
        TLRPC$Message tLRPC$Message2 = null;
        if (tLRPC$TL_error == null) {
            String str2 = tLRPC$Message.attachPath;
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            ArrayList<TLRPC$Update> arrayList = tLRPC$Updates.updates;
            while (true) {
                if (i >= arrayList.size()) {
                    break;
                }
                TLRPC$Update tLRPC$Update = arrayList.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateEditMessage) {
                    tLRPC$Message2 = ((TLRPC$TL_updateEditMessage) tLRPC$Update).message;
                    break;
                } else if (tLRPC$Update instanceof TLRPC$TL_updateEditChannelMessage) {
                    tLRPC$Message2 = ((TLRPC$TL_updateEditChannelMessage) tLRPC$Update).message;
                    break;
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewScheduledMessage) {
                    tLRPC$Message2 = ((TLRPC$TL_updateNewScheduledMessage) tLRPC$Update).message;
                    break;
                } else {
                    i++;
                }
            }
            TLRPC$Message tLRPC$Message3 = tLRPC$Message2;
            if (tLRPC$Message3 != null) {
                ImageLoader.saveMessageThumbs(tLRPC$Message3);
                updateMediaPaths(messageObject, tLRPC$Message3, tLRPC$Message3.id, str, false);
            }
            Utilities.stageQueue.postRunnable(new Runnable(tLRPC$Updates, tLRPC$Message, z) {
                private final /* synthetic */ TLRPC$Updates f$1;
                private final /* synthetic */ TLRPC$Message f$2;
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
            if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
                stopVideoService(str2);
                return;
            }
            return;
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, (BaseFragment) null, tLObject2, new Object[0]);
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(tLRPC$Message.attachPath);
        }
        removeFromSendingMessages(tLRPC$Message.id, z);
        revertEditingMessageObject(messageObject);
    }

    public /* synthetic */ void lambda$null$41$SendMessagesHelper(TLRPC$Updates tLRPC$Updates, TLRPC$Message tLRPC$Message, boolean z) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, z) {
            private final /* synthetic */ TLRPC$Message f$1;
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

    public /* synthetic */ void lambda$null$40$SendMessagesHelper(TLRPC$Message tLRPC$Message, boolean z) {
        processSentMessage(tLRPC$Message.id);
        removeFromSendingMessages(tLRPC$Message.id, z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:74:0x01c2  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01d5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$51$SendMessagesHelper(boolean r26, org.telegram.tgnet.TLRPC$TL_error r27, org.telegram.tgnet.TLRPC$Message r28, org.telegram.tgnet.TLObject r29, org.telegram.messenger.MessageObject r30, java.lang.String r31, org.telegram.tgnet.TLObject r32) {
        /*
            r25 = this;
            r8 = r25
            r9 = r26
            r0 = r27
            r10 = r28
            r1 = r29
            if (r0 != 0) goto L_0x0287
            int r6 = r10.id
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.lang.String r14 = r10.attachPath
            int r0 = r10.date
            r3 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 != r3) goto L_0x001e
            r0 = 1
            goto L_0x001f
        L_0x001e:
            r0 = 0
        L_0x001f:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateShortSentMessage
            if (r4 == 0) goto L_0x00c8
            r15 = r1
            org.telegram.tgnet.TLRPC$TL_updateShortSentMessage r15 = (org.telegram.tgnet.TLRPC$TL_updateShortSentMessage) r15
            r2 = 0
            int r3 = r15.id
            r4 = 0
            r5 = 0
            r0 = r25
            r1 = r30
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r0 = r30.getMediaExistanceFlags()
            int r1 = r15.id
            r10.id = r1
            r10.local_id = r1
            int r1 = r15.date
            r10.date = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r15.entities
            r10.entities = r1
            boolean r1 = r15.out
            r10.out = r1
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r15.media
            if (r1 == 0) goto L_0x0057
            r10.media = r1
            int r1 = r10.flags
            r1 = r1 | 512(0x200, float:7.175E-43)
            r10.flags = r1
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r28)
        L_0x0057:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r15.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0069
            java.lang.String r1 = r15.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0069
            java.lang.String r1 = r15.message
            r10.message = r1
        L_0x0069:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r10.entities
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0077
            int r1 = r10.flags
            r1 = r1 | 128(0x80, float:1.794E-43)
            r10.flags = r1
        L_0x0077:
            org.telegram.messenger.MessagesController r1 = r25.getMessagesController()
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            long r2 = r10.dialog_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x00aa
            org.telegram.messenger.MessagesStorage r1 = r25.getMessagesStorage()
            boolean r2 = r10.out
            long r3 = r10.dialog_id
            int r1 = r1.getDialogReadMax(r2, r3)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.messenger.MessagesController r2 = r25.getMessagesController()
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r2.dialogs_read_outbox_max
            long r3 = r10.dialog_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r2.put(r3, r1)
        L_0x00aa:
            int r1 = r1.intValue()
            int r2 = r10.id
            if (r1 >= r2) goto L_0x00b4
            r1 = 1
            goto L_0x00b5
        L_0x00b4:
            r1 = 0
        L_0x00b5:
            r10.unread = r1
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$2cls1P7tqQZ1I3-97ssFZ8jhbps r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$2cls1P7tqQZ1I3-97ssFZ8jhbps
            r2.<init>(r15)
            r1.postRunnable(r2)
            r7.add(r10)
            r11 = r0
            r12 = 0
            goto L_0x01bb
        L_0x00c8:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$Updates
            if (r4 == 0) goto L_0x01b9
            r15 = r1
            org.telegram.tgnet.TLRPC$Updates r15 = (org.telegram.tgnet.TLRPC$Updates) r15
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r15.updates
            r4 = 0
        L_0x00d2:
            int r5 = r1.size()
            if (r4 >= r5) goto L_0x013d
            java.lang.Object r5 = r1.get(r4)
            org.telegram.tgnet.TLRPC$Update r5 = (org.telegram.tgnet.TLRPC$Update) r5
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewMessage
            if (r2 == 0) goto L_0x00f8
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewMessage) r5
            org.telegram.tgnet.TLRPC$Message r2 = r5.message
            r7.add(r2)
            org.telegram.messenger.DispatchQueue r11 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$KnZaYqmrVc-tQGTwKl5NtVyQ2iU r13 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$KnZaYqmrVc-tQGTwKl5NtVyQ2iU
            r13.<init>(r5)
            r11.postRunnable(r13)
            r1.remove(r4)
        L_0x00f6:
            r11 = r2
            goto L_0x013e
        L_0x00f8:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage
            r11 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r2 == 0) goto L_0x011f
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage) r5
            org.telegram.tgnet.TLRPC$Message r2 = r5.message
            r7.add(r2)
            int r13 = r10.flags
            r13 = r13 & r11
            if (r13 == 0) goto L_0x0111
            org.telegram.tgnet.TLRPC$Message r13 = r5.message
            int r12 = r13.flags
            r11 = r11 | r12
            r13.flags = r11
        L_0x0111:
            org.telegram.messenger.DispatchQueue r11 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$aMzUgpeWOQtaba-1QJrysWUjyos r12 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$aMzUgpeWOQtaba-1QJrysWUjyos
            r12.<init>(r5)
            r11.postRunnable(r12)
            r1.remove(r4)
            goto L_0x00f6
        L_0x011f:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x013a
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage) r5
            org.telegram.tgnet.TLRPC$Message r2 = r5.message
            r7.add(r2)
            int r12 = r10.flags
            r12 = r12 & r11
            if (r12 == 0) goto L_0x0136
            org.telegram.tgnet.TLRPC$Message r5 = r5.message
            int r12 = r5.flags
            r11 = r11 | r12
            r5.flags = r11
        L_0x0136:
            r1.remove(r4)
            goto L_0x00f6
        L_0x013a:
            int r4 = r4 + 1
            goto L_0x00d2
        L_0x013d:
            r11 = 0
        L_0x013e:
            if (r11 == 0) goto L_0x01a9
            org.telegram.messenger.MessageObject.getDialogId(r11)
            if (r0 == 0) goto L_0x014b
            int r0 = r11.date
            if (r0 == r3) goto L_0x014b
            r12 = 0
            goto L_0x014c
        L_0x014b:
            r12 = r9
        L_0x014c:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r11)
            if (r12 != 0) goto L_0x0191
            org.telegram.messenger.MessagesController r0 = r25.getMessagesController()
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r0.dialogs_read_outbox_max
            long r1 = r11.dialog_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0184
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            boolean r1 = r11.out
            long r2 = r11.dialog_id
            int r0 = r0.getDialogReadMax(r1, r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.messenger.MessagesController r1 = r25.getMessagesController()
            java.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            long r2 = r11.dialog_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r1.put(r2, r0)
        L_0x0184:
            int r0 = r0.intValue()
            int r1 = r11.id
            if (r0 >= r1) goto L_0x018e
            r0 = 1
            goto L_0x018f
        L_0x018e:
            r0 = 0
        L_0x018f:
            r11.unread = r0
        L_0x0191:
            int r3 = r11.id
            r5 = 0
            r0 = r25
            r1 = r30
            r2 = r11
            r4 = r31
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r0 = r30.getMediaExistanceFlags()
            int r1 = r11.id
            r10.id = r1
            r1 = r0
            r0 = 0
            goto L_0x01ac
        L_0x01a9:
            r12 = r9
            r0 = 1
            r1 = 0
        L_0x01ac:
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$4lFxBiThpwH7WPmjQyat8EqmmrM r3 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$4lFxBiThpwH7WPmjQyat8EqmmrM
            r3.<init>(r15)
            r2.postRunnable(r3)
            r13 = r0
            r11 = r1
            goto L_0x01bc
        L_0x01b9:
            r12 = r9
            r11 = 0
        L_0x01bb:
            r13 = 0
        L_0x01bc:
            boolean r0 = org.telegram.messenger.MessageObject.isLiveLocationMessage(r28)
            if (r0 == 0) goto L_0x01d3
            org.telegram.messenger.LocationController r0 = r25.getLocationController()
            long r1 = r10.dialog_id
            int r3 = r10.id
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r10.media
            int r4 = r4.period
            r5 = r28
            r0.addSharingLocation(r1, r3, r4, r5)
        L_0x01d3:
            if (r13 != 0) goto L_0x0293
            org.telegram.messenger.StatsController r0 = r25.getStatsController()
            int r1 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r2 = 1
            r0.incrementSentItemsCount(r1, r2, r2)
            r0 = 0
            r10.send_state = r0
            if (r9 == 0) goto L_0x022c
            if (r12 != 0) goto L_0x022c
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            r0.add(r1)
            org.telegram.messenger.MessagesController r16 = r25.getMessagesController()
            r18 = 0
            r19 = 0
            long r1 = r10.dialog_id
            org.telegram.tgnet.TLRPC$Peer r3 = r10.to_id
            int r3 = r3.channel_id
            r23 = 0
            r24 = 1
            r17 = r0
            r20 = r1
            r22 = r3
            r16.deleteMessages(r17, r18, r19, r20, r22, r23, r24)
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r11 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$vXuyGEtHuX3-zvqOAj3t_Wne6go r12 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$vXuyGEtHuX3-zvqOAj3t_Wne6go
            r0 = r12
            r1 = r25
            r2 = r7
            r3 = r30
            r4 = r28
            r5 = r6
            r6 = r26
            r7 = r14
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r11.postRunnable(r12)
            goto L_0x0293
        L_0x022c:
            org.telegram.messenger.NotificationCenter r0 = r25.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            r2 = 7
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r6)
            r4 = 0
            r2[r4] = r3
            int r3 = r10.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 1
            r2[r4] = r3
            r3 = 2
            r2[r3] = r10
            r3 = 3
            long r4 = r10.dialog_id
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            r2[r3] = r4
            r3 = 4
            r4 = 0
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            r2[r3] = r4
            r3 = 5
            java.lang.Integer r4 = java.lang.Integer.valueOf(r11)
            r2[r3] = r4
            r3 = 6
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r26)
            r2[r3] = r4
            r0.postNotificationName(r1, r2)
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r12 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$4zxklzrMNTXAVApuKjkmRn2rSdk r15 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$4zxklzrMNTXAVApuKjkmRn2rSdk
            r0 = r15
            r1 = r25
            r2 = r28
            r3 = r6
            r4 = r26
            r5 = r7
            r6 = r11
            r7 = r14
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r12.postRunnable(r15)
            goto L_0x0293
        L_0x0287:
            int r1 = r8.currentAccount
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r32
            r4 = 0
            org.telegram.ui.Components.AlertsCreator.processError(r1, r0, r4, r2, r3)
            r13 = 1
        L_0x0293:
            if (r13 == 0) goto L_0x02d5
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            r0.markMessageAsSendError(r10, r9)
            r0 = 2
            r10.send_state = r0
            org.telegram.messenger.NotificationCenter r0 = r25.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.messageSendError
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            int r3 = r10.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 0
            r2[r4] = r3
            r0.postNotificationName(r1, r2)
            int r0 = r10.id
            r8.processSentMessage(r0)
            boolean r0 = org.telegram.messenger.MessageObject.isVideoMessage(r28)
            if (r0 != 0) goto L_0x02cb
            boolean r0 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r28)
            if (r0 != 0) goto L_0x02cb
            boolean r0 = org.telegram.messenger.MessageObject.isNewGifMessage(r28)
            if (r0 == 0) goto L_0x02d0
        L_0x02cb:
            java.lang.String r0 = r10.attachPath
            r8.stopVideoService(r0)
        L_0x02d0:
            int r0 = r10.id
            r8.removeFromSendingMessages(r0, r9)
        L_0x02d5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$51$SendMessagesHelper(boolean, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, java.lang.String, org.telegram.tgnet.TLObject):void");
    }

    public /* synthetic */ void lambda$null$43$SendMessagesHelper(TLRPC$TL_updateShortSentMessage tLRPC$TL_updateShortSentMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateShortSentMessage.pts, tLRPC$TL_updateShortSentMessage.date, tLRPC$TL_updateShortSentMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$44$SendMessagesHelper(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$45$SendMessagesHelper(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$46$SendMessagesHelper(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
    }

    public /* synthetic */ void lambda$null$48$SendMessagesHelper(ArrayList arrayList, MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, boolean z, String str) {
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable(messageObject, tLRPC$Message, i, z) {
            private final /* synthetic */ MessageObject f$1;
            private final /* synthetic */ TLRPC$Message f$2;
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
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(str);
            return;
        }
    }

    public /* synthetic */ void lambda$null$47$SendMessagesHelper(MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true));
        getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$50$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, Integer.valueOf(i), tLRPC$Message2.id, 0, false, tLRPC$Message2.to_id.channel_id, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, i, i2, z) {
            private final /* synthetic */ TLRPC$Message f$1;
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
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(str);
        }
    }

    public /* synthetic */ void lambda$null$49$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), 0L, Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$54$SendMessagesHelper(TLRPC$Message tLRPC$Message) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, tLRPC$Message.id) {
            private final /* synthetic */ TLRPC$Message f$1;
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

    public /* synthetic */ void lambda$null$53$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i) {
        tLRPC$Message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(i));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0038, code lost:
        r5 = (r5 = r7.media).photo;
     */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00f3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMediaPaths(org.telegram.messenger.MessageObject r17, org.telegram.tgnet.TLRPC$Message r18, int r19, java.lang.String r20, boolean r21) {
        /*
            r16 = this;
            r0 = r17
            r7 = r18
            r8 = r20
            r1 = r21
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            java.lang.String r4 = "_"
            if (r2 == 0) goto L_0x011b
            boolean r2 = r17.isDice()
            if (r2 == 0) goto L_0x0024
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaDice) r2
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaDice) r5
            int r5 = r5.value
            r2.value = r5
            goto L_0x00bd
        L_0x0024:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            r6 = 40
            if (r5 == 0) goto L_0x004a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x0043
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x0043
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            if (r5 == 0) goto L_0x0043
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x0044
        L_0x0043:
            r5 = r2
        L_0x0044:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            goto L_0x00c0
        L_0x004a:
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            if (r5 == 0) goto L_0x006b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x0065
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x0065
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            if (r5 == 0) goto L_0x0065
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x0066
        L_0x0065:
            r5 = r2
        L_0x0066:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            goto L_0x00c0
        L_0x006b:
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            if (r2 == 0) goto L_0x00bd
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            if (r5 == 0) goto L_0x0096
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$WebPage r5 = r5.webpage
            if (r5 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            if (r5 == 0) goto L_0x008e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x008f
        L_0x008e:
            r5 = r2
        L_0x008f:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            goto L_0x00c0
        L_0x0096:
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            if (r2 == 0) goto L_0x00bd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$WebPage r5 = r5.webpage
            if (r5 == 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            if (r5 == 0) goto L_0x00b5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x00b6
        L_0x00b5:
            r5 = r2
        L_0x00b6:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            goto L_0x00c0
        L_0x00bd:
            r2 = 0
            r5 = 0
            r6 = 0
        L_0x00c0:
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r10 == 0) goto L_0x011b
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r2 == 0) goto L_0x011b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "stripped"
            r2.append(r10)
            java.lang.String r11 = org.telegram.messenger.FileRefController.getKeyForParentObject(r17)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            if (r7 == 0) goto L_0x00f3
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            java.lang.String r10 = org.telegram.messenger.FileRefController.getKeyForParentObject(r18)
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            goto L_0x0110
        L_0x00f3:
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
        L_0x0110:
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            r11.replaceImageInCache(r2, r10, r5, r1)
        L_0x011b:
            if (r7 != 0) goto L_0x011e
            return
        L_0x011e:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            java.lang.String r6 = "sent_"
            java.lang.String r12 = ".jpg"
            r13 = 4
            r14 = 0
            r15 = 1
            if (r5 == 0) goto L_0x02cc
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            if (r5 == 0) goto L_0x02cc
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x02cc
            org.telegram.tgnet.TLRPC$Photo r3 = r5.photo
            if (r3 == 0) goto L_0x02cc
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x016a
            boolean r0 = r0.scheduled
            if (r0 != 0) goto L_0x016a
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
        L_0x016a:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            int r0 = r0.size()
            if (r0 != r15) goto L_0x0196
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            java.lang.Object r0 = r0.get(r14)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.location
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r0 == 0) goto L_0x0196
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            r0.sizes = r1
            goto L_0x02b2
        L_0x0196:
            r0 = 0
        L_0x0197:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x02b2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2
            if (r2 == 0) goto L_0x02ab
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            if (r3 == 0) goto L_0x02ab
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r3 != 0) goto L_0x02ab
            java.lang.String r3 = r2.type
            if (r3 != 0) goto L_0x01bf
            goto L_0x02ab
        L_0x01bf:
            r3 = 0
        L_0x01c0:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x02ab
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            java.lang.Object r5 = r5.get(r3)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5
            if (r5 == 0) goto L_0x02a4
            org.telegram.tgnet.TLRPC$FileLocation r6 = r5.location
            if (r6 == 0) goto L_0x02a4
            java.lang.String r8 = r5.type
            if (r8 != 0) goto L_0x01e4
            goto L_0x02a4
        L_0x01e4:
            long r14 = r6.volume_id
            int r6 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x01f2
            java.lang.String r6 = r2.type
            boolean r6 = r6.equals(r8)
            if (r6 != 0) goto L_0x01fe
        L_0x01f2:
            int r6 = r2.w
            int r8 = r5.w
            if (r6 != r8) goto L_0x02a4
            int r6 = r2.h
            int r8 = r5.h
            if (r6 != r8) goto L_0x02a4
        L_0x01fe:
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
            if (r8 == 0) goto L_0x0239
            goto L_0x02ab
        L_0x0239:
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
            if (r15 != 0) goto L_0x0271
            org.telegram.tgnet.TLRPC$Photo r14 = r14.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r14.sizes
            int r14 = r14.size()
            r15 = 1
            if (r14 == r15) goto L_0x026c
            int r14 = r2.w
            r15 = 90
            if (r14 > r15) goto L_0x026c
            int r14 = r2.h
            if (r14 <= r15) goto L_0x0271
        L_0x026c:
            java.io.File r14 = org.telegram.messenger.FileLoader.getPathToAttach(r2)
            goto L_0x0289
        L_0x0271:
            java.io.File r14 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r6)
            r13.append(r12)
            java.lang.String r13 = r13.toString()
            r14.<init>(r15, r13)
        L_0x0289:
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
            goto L_0x02ab
        L_0x02a4:
            int r3 = r3 + 1
            r13 = 4
            r14 = 0
            r15 = 1
            goto L_0x01c0
        L_0x02ab:
            int r0 = r0 + 1
            r13 = 4
            r14 = 0
            r15 = 1
            goto L_0x0197
        L_0x02b2:
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
            goto L_0x05e0
        L_0x02cc:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r3 == 0) goto L_0x0594
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            if (r3 == 0) goto L_0x0594
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r9.media
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x0594
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            if (r3 == 0) goto L_0x0594
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x036b
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r18)
            if (r2 != 0) goto L_0x02f0
            boolean r3 = org.telegram.messenger.MessageObject.isGifMessage(r18)
            if (r3 == 0) goto L_0x0334
        L_0x02f0:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r5)
            if (r3 != r5) goto L_0x0334
            boolean r3 = r0.scheduled
            if (r3 != 0) goto L_0x032d
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
        L_0x032d:
            if (r2 == 0) goto L_0x036b
            java.lang.String r2 = r9.attachPath
            r7.attachPath = r2
            goto L_0x036b
        L_0x0334:
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r18)
            if (r2 != 0) goto L_0x036b
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r18)
            if (r2 != 0) goto L_0x036b
            boolean r2 = r0.scheduled
            if (r2 != 0) goto L_0x036b
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
        L_0x036b:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r3 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r3)
            if (r2 == 0) goto L_0x0421
            org.telegram.tgnet.TLRPC$FileLocation r5 = r2.location
            if (r5 == 0) goto L_0x0421
            long r5 = r5.volume_id
            int r13 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r13 != 0) goto L_0x0421
            if (r3 == 0) goto L_0x0421
            org.telegram.tgnet.TLRPC$FileLocation r5 = r3.location
            if (r5 == 0) goto L_0x0421
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x0421
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x0421
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
            if (r6 != 0) goto L_0x044a
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
            goto L_0x044a
        L_0x0421:
            if (r2 == 0) goto L_0x0430
            boolean r1 = org.telegram.messenger.MessageObject.isStickerMessage(r18)
            if (r1 == 0) goto L_0x0430
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            if (r1 == 0) goto L_0x0430
            r3.location = r1
            goto L_0x044a
        L_0x0430:
            if (r2 == 0) goto L_0x043e
            if (r2 == 0) goto L_0x043a
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r1 != 0) goto L_0x043e
        L_0x043a:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r1 == 0) goto L_0x044a
        L_0x043e:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r1.thumbs = r2
        L_0x044a:
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
        L_0x045f:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x0481
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r3 == 0) goto L_0x047e
            byte[] r3 = r2.waveform
            goto L_0x0482
        L_0x047e:
            int r1 = r1 + 1
            goto L_0x045f
        L_0x0481:
            r3 = 0
        L_0x0482:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            r1.attributes = r2
            if (r3 == 0) goto L_0x04b8
            r1 = 0
        L_0x0491:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x04b8
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r4 == 0) goto L_0x04b5
            r2.waveform = r3
            int r4 = r2.flags
            r5 = 4
            r4 = r4 | r5
            r2.flags = r4
        L_0x04b5:
            int r1 = r1 + 1
            goto L_0x0491
        L_0x04b8:
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
            if (r1 != 0) goto L_0x0512
            boolean r1 = org.telegram.messenger.MessageObject.isOut(r18)
            if (r1 == 0) goto L_0x0512
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r1)
            if (r1 == 0) goto L_0x04ec
            org.telegram.messenger.MediaDataController r1 = r16.getMediaDataController()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            int r3 = r7.date
            r1.addRecentGif(r2, r3)
            goto L_0x0512
        L_0x04ec:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r1)
            if (r1 != 0) goto L_0x0501
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            r2 = 1
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r2)
            if (r1 == 0) goto L_0x0512
        L_0x0501:
            org.telegram.messenger.MediaDataController r1 = r16.getMediaDataController()
            r2 = 0
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            int r5 = r7.date
            r6 = 0
            r3 = r18
            r1.addRecentSticker(r2, r3, r4, r5, r6)
        L_0x0512:
            java.lang.String r1 = r9.attachPath
            if (r1 == 0) goto L_0x058b
            r2 = 4
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r2)
            java.lang.String r2 = r2.getAbsolutePath()
            boolean r1 = r1.startsWith(r2)
            if (r1 == 0) goto L_0x058b
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r9.attachPath
            r1.<init>(r2)
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            int r2 = r2.ttl_seconds
            if (r2 == 0) goto L_0x0536
            r2 = 1
            goto L_0x0537
        L_0x0536:
            r2 = 0
        L_0x0537:
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r2)
            boolean r3 = r1.renameTo(r2)
            if (r3 != 0) goto L_0x055b
            boolean r1 = r1.exists()
            if (r1 == 0) goto L_0x054c
            java.lang.String r1 = r9.attachPath
            r7.attachPath = r1
            goto L_0x054f
        L_0x054c:
            r1 = 0
            r0.attachPathExists = r1
        L_0x054f:
            boolean r1 = r2.exists()
            r0.mediaExists = r1
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x05e0
        L_0x055b:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoMessage(r18)
            if (r1 == 0) goto L_0x0566
            r1 = 1
            r0.attachPathExists = r1
            goto L_0x05e0
        L_0x0566:
            boolean r1 = r0.attachPathExists
            r0.mediaExists = r1
            r1 = 0
            r0.attachPathExists = r1
            java.lang.String r0 = ""
            r9.attachPath = r0
            if (r8 == 0) goto L_0x05e0
            java.lang.String r0 = "http"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x05e0
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            java.lang.String r1 = r2.toString()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            r0.addRecentLocalFile(r8, r1, r2)
            goto L_0x05e0
        L_0x058b:
            java.lang.String r0 = r9.attachPath
            r7.attachPath = r0
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x05e0
        L_0x0594:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r7.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r1 == 0) goto L_0x05a3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r1 == 0) goto L_0x05a3
            r9.media = r0
            goto L_0x05e0
        L_0x05a3:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r7.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r1 == 0) goto L_0x05ac
            r9.media = r0
            goto L_0x05e0
        L_0x05ac:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r1 == 0) goto L_0x05bf
            org.telegram.tgnet.TLRPC$GeoPoint r0 = r0.geo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$GeoPoint r1 = r1.geo
            double r2 = r1.lat
            r0.lat = r2
            double r1 = r1._long
            r0._long = r1
            goto L_0x05e0
        L_0x05bf:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x05da
            r9.media = r0
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x05e0
            java.lang.String r0 = r7.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x05e0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r7.entities
            r9.entities = r0
            java.lang.String r0 = r7.message
            r9.message = r0
            goto L_0x05e0
        L_0x05da:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r1 == 0) goto L_0x05e0
            r9.media = r0
        L_0x05e0:
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
    public void processUnsentMessages(ArrayList<TLRPC$Message> arrayList, ArrayList<TLRPC$Message> arrayList2, ArrayList<TLRPC$User> arrayList3, ArrayList<TLRPC$Chat> arrayList4, ArrayList<TLRPC$EncryptedChat> arrayList5) {
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
            retrySendMessage(new MessageObject(this.currentAccount, (TLRPC$Message) arrayList4.get(i), false), true);
        }
        if (arrayList5 != null) {
            for (int i2 = 0; i2 < arrayList5.size(); i2++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC$Message) arrayList5.get(i2), false);
                messageObject.scheduled = true;
                retrySendMessage(messageObject, true);
            }
        }
    }

    public TLRPC$TL_photo generatePhotoSizes(String str, Uri uri) {
        return generatePhotoSizes((TLRPC$TL_photo) null, str, uri);
    }

    public TLRPC$TL_photo generatePhotoSizes(TLRPC$TL_photo tLRPC$TL_photo, String str, Uri uri) {
        Bitmap loadBitmap = ImageLoader.loadBitmap(str, uri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
        if (loadBitmap == null) {
            loadBitmap = ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true);
        }
        ArrayList<TLRPC$PhotoSize> arrayList = new ArrayList<>();
        TLRPC$PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, true);
        if (scaleAndSaveImage != null) {
            arrayList.add(scaleAndSaveImage);
        }
        TLRPC$PhotoSize scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(loadBitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
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
        if (tLRPC$TL_photo == null) {
            tLRPC$TL_photo = new TLRPC$TL_photo();
        }
        tLRPC$TL_photo.date = getConnectionsManager().getCurrentTime();
        tLRPC$TL_photo.sizes = arrayList;
        tLRPC$TL_photo.file_reference = new byte[0];
        return tLRPC$TL_photo;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0339, code lost:
        r4 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x033a, code lost:
        if (r4 == 0) goto L_0x0370;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x033d, code lost:
        if (r4 == 1) goto L_0x036b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0340, code lost:
        if (r4 == 2) goto L_0x0366;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0342, code lost:
        if (r4 == 3) goto L_0x0361;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x0345, code lost:
        if (r4 == 4) goto L_0x035c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0347, code lost:
        if (r4 == 5) goto L_0x0357;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0349, code lost:
        r0 = r19.getMimeTypeFromExtension(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x034f, code lost:
        if (r0 == null) goto L_0x0354;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0351, code lost:
        r8.mime_type = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0354, code lost:
        r8.mime_type = "application/octet-stream";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x0357, code lost:
        r8.mime_type = "audio/flac";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x035c, code lost:
        r8.mime_type = "audio/ogg";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0361, code lost:
        r8.mime_type = "audio/m4a";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0366, code lost:
        r8.mime_type = "audio/mpeg";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x036b, code lost:
        r8.mime_type = "audio/opus";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x0370, code lost:
        r8.mime_type = "image/webp";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004d, code lost:
        if (r3 == false) goto L_0x0051;
     */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0152 A[SYNTHETIC, Splitter:B:102:0x0152] */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0164 A[SYNTHETIC, Splitter:B:110:0x0164] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x016e  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01c5  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01c8  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0206 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ae  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x043b  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x0441  */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x044f  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0458  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0461  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00ae  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r30, java.lang.String r31, java.lang.String r32, android.net.Uri r33, java.lang.String r34, long r35, org.telegram.messenger.MessageObject r37, java.lang.CharSequence r38, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r39, org.telegram.messenger.MessageObject r40, boolean r41, boolean r42, int r43) {
        /*
            r0 = r31
            r1 = r32
            r2 = r33
            r3 = r34
            r4 = 0
            if (r0 == 0) goto L_0x0011
            int r5 = r31.length()
            if (r5 != 0) goto L_0x0014
        L_0x0011:
            if (r2 != 0) goto L_0x0014
            return r4
        L_0x0014:
            if (r2 == 0) goto L_0x001d
            boolean r5 = org.telegram.messenger.AndroidUtilities.isInternalUri(r33)
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
            if (r2 == 0) goto L_0x0050
            if (r3 == 0) goto L_0x003d
            java.lang.String r0 = r5.getExtensionFromMimeType(r3)
            goto L_0x003e
        L_0x003d:
            r0 = 0
        L_0x003e:
            if (r0 != 0) goto L_0x0044
            java.lang.String r0 = "txt"
            r3 = 0
            goto L_0x0045
        L_0x0044:
            r3 = 1
        L_0x0045:
            java.lang.String r2 = org.telegram.messenger.MediaController.copyFileToCache(r2, r0)
            if (r2 != 0) goto L_0x004c
            return r4
        L_0x004c:
            r13 = r2
            if (r3 != 0) goto L_0x0052
            goto L_0x0051
        L_0x0050:
            r13 = r0
        L_0x0051:
            r0 = 0
        L_0x0052:
            java.io.File r2 = new java.io.File
            r2.<init>(r13)
            boolean r3 = r2.exists()
            if (r3 == 0) goto L_0x0481
            long r7 = r2.length()
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x0069
            goto L_0x0481
        L_0x0069:
            r9 = r35
            int r3 = (int) r9
            if (r3 != 0) goto L_0x0070
            r3 = 1
            goto L_0x0071
        L_0x0070:
            r3 = 0
        L_0x0071:
            r15 = r3 ^ 1
            java.lang.String r8 = r2.getName()
            r7 = -1
            java.lang.String r6 = ""
            if (r0 == 0) goto L_0x007f
        L_0x007c:
            r16 = r0
            goto L_0x008f
        L_0x007f:
            r0 = 46
            int r0 = r13.lastIndexOf(r0)
            if (r0 == r7) goto L_0x008d
            int r0 = r0 + r14
            java.lang.String r0 = r13.substring(r0)
            goto L_0x007c
        L_0x008d:
            r16 = r6
        L_0x008f:
            java.lang.String r10 = r16.toLowerCase()
            java.lang.String r9 = "mp3"
            boolean r0 = r10.equals(r9)
            java.lang.String r4 = "flac"
            java.lang.String r11 = "opus"
            java.lang.String r12 = "m4a"
            java.lang.String r14 = "ogg"
            r31 = r15
            r15 = 2
            if (r0 != 0) goto L_0x016e
            boolean r0 = r10.equals(r12)
            if (r0 == 0) goto L_0x00ae
            goto L_0x016e
        L_0x00ae:
            boolean r0 = r10.equals(r11)
            if (r0 != 0) goto L_0x00cd
            boolean r0 = r10.equals(r14)
            if (r0 != 0) goto L_0x00cd
            boolean r0 = r10.equals(r4)
            if (r0 == 0) goto L_0x00c1
            goto L_0x00cd
        L_0x00c1:
            r18 = r8
            r19 = r9
            r0 = 0
            r8 = 0
            r9 = 0
        L_0x00c8:
            r15 = 0
        L_0x00c9:
            r21 = 0
            goto L_0x019c
        L_0x00cd:
            android.media.MediaMetadataRetriever r7 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0143, all -> 0x013f }
            r7.<init>()     // Catch:{ Exception -> 0x0143, all -> 0x013f }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x0139 }
            r7.setDataSource(r0)     // Catch:{ Exception -> 0x0139 }
            r0 = 9
            java.lang.String r0 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x0139 }
            if (r0 == 0) goto L_0x010d
            r18 = r8
            r19 = r9
            long r8 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x010b }
            float r0 = (float) r8     // Catch:{ Exception -> 0x010b }
            r8 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r8
            double r8 = (double) r0     // Catch:{ Exception -> 0x010b }
            double r8 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x010b }
            int r8 = (int) r8
            r0 = 7
            java.lang.String r9 = r7.extractMetadata(r0)     // Catch:{ Exception -> 0x0105 }
            java.lang.String r0 = r7.extractMetadata(r15)     // Catch:{ Exception -> 0x0100 }
            r20 = r8
            r8 = r0
            goto L_0x0115
        L_0x0100:
            r0 = move-exception
            r20 = r8
            r8 = 0
            goto L_0x014d
        L_0x0105:
            r0 = move-exception
            r20 = r8
            r8 = 0
            r9 = 0
            goto L_0x014d
        L_0x010b:
            r0 = move-exception
            goto L_0x0149
        L_0x010d:
            r18 = r8
            r19 = r9
            r8 = 0
            r9 = 0
            r20 = 0
        L_0x0115:
            if (r40 != 0) goto L_0x012c
            boolean r0 = r10.equals(r14)     // Catch:{ Exception -> 0x012a }
            if (r0 == 0) goto L_0x012c
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x012a }
            int r0 = org.telegram.messenger.MediaController.isOpusFile(r0)     // Catch:{ Exception -> 0x012a }
            r15 = 1
            if (r0 != r15) goto L_0x012c
            r15 = 1
            goto L_0x012d
        L_0x012a:
            r0 = move-exception
            goto L_0x014d
        L_0x012c:
            r15 = 0
        L_0x012d:
            r7.release()     // Catch:{ Exception -> 0x0131 }
            goto L_0x0136
        L_0x0131:
            r0 = move-exception
            r7 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x0136:
            r0 = r20
            goto L_0x00c9
        L_0x0139:
            r0 = move-exception
            r18 = r8
            r19 = r9
            goto L_0x0149
        L_0x013f:
            r0 = move-exception
            r1 = r0
            r6 = 0
            goto L_0x0162
        L_0x0143:
            r0 = move-exception
            r18 = r8
            r19 = r9
            r7 = 0
        L_0x0149:
            r8 = 0
            r9 = 0
            r20 = 0
        L_0x014d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x015f }
            if (r7 == 0) goto L_0x015b
            r7.release()     // Catch:{ Exception -> 0x0156 }
            goto L_0x015b
        L_0x0156:
            r0 = move-exception
            r7 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x015b:
            r0 = r20
            goto L_0x00c8
        L_0x015f:
            r0 = move-exception
            r1 = r0
            r6 = r7
        L_0x0162:
            if (r6 == 0) goto L_0x016d
            r6.release()     // Catch:{ Exception -> 0x0168 }
            goto L_0x016d
        L_0x0168:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x016d:
            throw r1
        L_0x016e:
            r18 = r8
            r19 = r9
            org.telegram.messenger.audioinfo.AudioInfo r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r2)
            if (r0 == 0) goto L_0x0190
            long r7 = r0.getDuration()
            r21 = 0
            int r9 = (r7 > r21 ? 1 : (r7 == r21 ? 0 : -1))
            if (r9 == 0) goto L_0x0192
            java.lang.String r9 = r0.getArtist()
            java.lang.String r0 = r0.getTitle()
            r23 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r23
            int r8 = (int) r7
            goto L_0x0195
        L_0x0190:
            r21 = 0
        L_0x0192:
            r0 = 0
            r8 = 0
            r9 = 0
        L_0x0195:
            r15 = 0
            r28 = r9
            r9 = r0
            r0 = r8
            r8 = r28
        L_0x019c:
            if (r0 == 0) goto L_0x01c5
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r7.<init>()
            r7.duration = r0
            r7.title = r9
            r7.performer = r8
            if (r9 != 0) goto L_0x01ad
            r7.title = r6
        L_0x01ad:
            int r0 = r7.flags
            r8 = 1
            r0 = r0 | r8
            r7.flags = r0
            java.lang.String r0 = r7.performer
            if (r0 != 0) goto L_0x01b9
            r7.performer = r6
        L_0x01b9:
            int r0 = r7.flags
            r9 = 2
            r0 = r0 | r9
            r7.flags = r0
            if (r15 == 0) goto L_0x01c3
            r7.voice = r8
        L_0x01c3:
            r0 = r7
            goto L_0x01c6
        L_0x01c5:
            r0 = 0
        L_0x01c6:
            if (r1 == 0) goto L_0x0203
            java.lang.String r7 = "attheme"
            boolean r7 = r1.endsWith(r7)
            if (r7 == 0) goto L_0x01d2
            r7 = 1
            goto L_0x0204
        L_0x01d2:
            if (r0 == 0) goto L_0x01ed
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r1)
            java.lang.String r1 = "audio"
            r7.append(r1)
            long r8 = r2.length()
            r7.append(r8)
            java.lang.String r1 = r7.toString()
            goto L_0x0203
        L_0x01ed:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r1)
            r7.append(r6)
            long r8 = r2.length()
            r7.append(r8)
            java.lang.String r1 = r7.toString()
        L_0x0203:
            r7 = 0
        L_0x0204:
            if (r7 != 0) goto L_0x0295
            if (r3 != 0) goto L_0x0295
            org.telegram.messenger.MessagesStorage r7 = r30.getMessagesStorage()
            if (r3 != 0) goto L_0x0210
            r8 = 1
            goto L_0x0211
        L_0x0210:
            r8 = 4
        L_0x0211:
            java.lang.Object[] r7 = r7.getSentFile(r1, r8)
            if (r7 == 0) goto L_0x0223
            r8 = 0
            r9 = r7[r8]
            r8 = r9
            org.telegram.tgnet.TLRPC$TL_document r8 = (org.telegram.tgnet.TLRPC$TL_document) r8
            r9 = 1
            r7 = r7[r9]
            java.lang.String r7 = (java.lang.String) r7
            goto L_0x0225
        L_0x0223:
            r7 = 0
            r8 = 0
        L_0x0225:
            if (r8 != 0) goto L_0x0263
            boolean r9 = r13.equals(r1)
            if (r9 != 0) goto L_0x0263
            if (r3 != 0) goto L_0x0263
            org.telegram.messenger.MessagesStorage r9 = r30.getMessagesStorage()
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r13)
            r34 = r7
            r33 = r8
            long r7 = r2.length()
            r15.append(r7)
            java.lang.String r7 = r15.toString()
            if (r3 != 0) goto L_0x024e
            r8 = 1
            goto L_0x024f
        L_0x024e:
            r8 = 4
        L_0x024f:
            java.lang.Object[] r7 = r9.getSentFile(r7, r8)
            if (r7 == 0) goto L_0x0267
            r8 = 0
            r9 = r7[r8]
            org.telegram.tgnet.TLRPC$TL_document r9 = (org.telegram.tgnet.TLRPC$TL_document) r9
            r8 = 1
            r7 = r7[r8]
            java.lang.String r7 = (java.lang.String) r7
            r15 = r7
            r20 = r9
            goto L_0x026b
        L_0x0263:
            r34 = r7
            r33 = r8
        L_0x0267:
            r20 = r33
            r15 = r34
        L_0x026b:
            r23 = 0
            r24 = 0
            r17 = -1
            r7 = r3
            r9 = r18
            r8 = r20
            r26 = r9
            r27 = r19
            r9 = r13
            r33 = r10
            r10 = r23
            r18 = r1
            r19 = r5
            r1 = r12
            r34 = r15
            r15 = r11
            r28 = r21
            r21 = r6
            r5 = r28
            r11 = r24
            ensureMediaThumbExists(r7, r8, r9, r10, r11)
            r7 = r34
            goto L_0x02ac
        L_0x0295:
            r33 = r10
            r15 = r11
            r26 = r18
            r27 = r19
            r17 = -1
            r18 = r1
            r19 = r5
            r1 = r12
            r28 = r21
            r21 = r6
            r5 = r28
            r7 = 0
            r20 = 0
        L_0x02ac:
            if (r20 != 0) goto L_0x043b
            org.telegram.tgnet.TLRPC$TL_document r8 = new org.telegram.tgnet.TLRPC$TL_document
            r8.<init>()
            r8.id = r5
            org.telegram.tgnet.ConnectionsManager r9 = r30.getConnectionsManager()
            int r9 = r9.getCurrentTime()
            r8.date = r9
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r9 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r9.<init>()
            r10 = r26
            r9.file_name = r10
            r10 = 0
            byte[] r11 = new byte[r10]
            r8.file_reference = r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r11 = r8.attributes
            r11.add(r9)
            long r11 = r2.length()
            int r12 = (int) r11
            r8.size = r12
            r8.dc_id = r10
            if (r0 == 0) goto L_0x02e2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r10 = r8.attributes
            r10.add(r0)
        L_0x02e2:
            int r0 = r16.length()
            java.lang.String r10 = "image/webp"
            java.lang.String r11 = "application/octet-stream"
            if (r0 == 0) goto L_0x0373
            int r0 = r33.hashCode()
            r12 = 5
            r5 = 3
            switch(r0) {
                case 106458: goto L_0x032f;
                case 108272: goto L_0x0323;
                case 109967: goto L_0x0319;
                case 3145576: goto L_0x030f;
                case 3418175: goto L_0x0305;
                case 3645340: goto L_0x02f8;
                default: goto L_0x02f5;
            }
        L_0x02f5:
            r6 = r33
            goto L_0x0339
        L_0x02f8:
            java.lang.String r0 = "webp"
            r6 = r33
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x0339
            r4 = 0
            goto L_0x033a
        L_0x0305:
            r6 = r33
            boolean r0 = r6.equals(r15)
            if (r0 == 0) goto L_0x0339
            r4 = 1
            goto L_0x033a
        L_0x030f:
            r6 = r33
            boolean r0 = r6.equals(r4)
            if (r0 == 0) goto L_0x0339
            r4 = 5
            goto L_0x033a
        L_0x0319:
            r6 = r33
            boolean r0 = r6.equals(r14)
            if (r0 == 0) goto L_0x0339
            r4 = 4
            goto L_0x033a
        L_0x0323:
            r6 = r33
            r1 = r27
            boolean r0 = r6.equals(r1)
            if (r0 == 0) goto L_0x0339
            r4 = 2
            goto L_0x033a
        L_0x032f:
            r6 = r33
            boolean r0 = r6.equals(r1)
            if (r0 == 0) goto L_0x0339
            r4 = 3
            goto L_0x033a
        L_0x0339:
            r4 = -1
        L_0x033a:
            if (r4 == 0) goto L_0x0370
            r1 = 1
            if (r4 == r1) goto L_0x036b
            r1 = 2
            if (r4 == r1) goto L_0x0366
            if (r4 == r5) goto L_0x0361
            r0 = 4
            if (r4 == r0) goto L_0x035c
            if (r4 == r12) goto L_0x0357
            r1 = r19
            java.lang.String r0 = r1.getMimeTypeFromExtension(r6)
            if (r0 == 0) goto L_0x0354
            r8.mime_type = r0
            goto L_0x0375
        L_0x0354:
            r8.mime_type = r11
            goto L_0x0375
        L_0x0357:
            java.lang.String r0 = "audio/flac"
            r8.mime_type = r0
            goto L_0x0375
        L_0x035c:
            java.lang.String r0 = "audio/ogg"
            r8.mime_type = r0
            goto L_0x0375
        L_0x0361:
            java.lang.String r0 = "audio/m4a"
            r8.mime_type = r0
            goto L_0x0375
        L_0x0366:
            java.lang.String r0 = "audio/mpeg"
            r8.mime_type = r0
            goto L_0x0375
        L_0x036b:
            java.lang.String r0 = "audio/opus"
            r8.mime_type = r0
            goto L_0x0375
        L_0x0370:
            r8.mime_type = r10
            goto L_0x0375
        L_0x0373:
            r8.mime_type = r11
        L_0x0375:
            java.lang.String r0 = r8.mime_type
            java.lang.String r1 = "image/gif"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x03c2
            if (r40 == 0) goto L_0x038b
            long r0 = r40.getGroupIdForUse()
            r4 = 0
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x03c2
        L_0x038b:
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x03be }
            r1 = 1119092736(0x42b40000, float:90.0)
            r2 = 0
            r4 = 1
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r2, r1, r1, r4)     // Catch:{ Exception -> 0x03be }
            if (r0 == 0) goto L_0x03c2
            java.lang.String r2 = "animation.gif"
            r9.file_name = r2     // Catch:{ Exception -> 0x03be }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r8.attributes     // Catch:{ Exception -> 0x03be }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x03be }
            r4.<init>()     // Catch:{ Exception -> 0x03be }
            r2.add(r4)     // Catch:{ Exception -> 0x03be }
            r2 = 55
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r1, r1, r2, r3)     // Catch:{ Exception -> 0x03be }
            if (r1 == 0) goto L_0x03ba
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r8.thumbs     // Catch:{ Exception -> 0x03be }
            r2.add(r1)     // Catch:{ Exception -> 0x03be }
            int r1 = r8.flags     // Catch:{ Exception -> 0x03be }
            r2 = 1
            r1 = r1 | r2
            r8.flags = r1     // Catch:{ Exception -> 0x03be }
        L_0x03ba:
            r0.recycle()     // Catch:{ Exception -> 0x03be }
            goto L_0x03c2
        L_0x03be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03c2:
            java.lang.String r0 = r8.mime_type
            boolean r0 = r0.equals(r10)
            if (r0 == 0) goto L_0x0437
            if (r31 == 0) goto L_0x0437
            if (r40 != 0) goto L_0x0437
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options
            r1.<init>()
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ Exception -> 0x03fd }
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x03fd }
            java.lang.String r2 = "r"
            r0.<init>(r13, r2)     // Catch:{ Exception -> 0x03fd }
            java.nio.channels.FileChannel r22 = r0.getChannel()     // Catch:{ Exception -> 0x03fd }
            java.nio.channels.FileChannel$MapMode r23 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Exception -> 0x03fd }
            r24 = 0
            int r2 = r13.length()     // Catch:{ Exception -> 0x03fd }
            long r2 = (long) r2     // Catch:{ Exception -> 0x03fd }
            r26 = r2
            java.nio.MappedByteBuffer r2 = r22.map(r23, r24, r26)     // Catch:{ Exception -> 0x03fd }
            int r3 = r2.limit()     // Catch:{ Exception -> 0x03fd }
            r4 = 0
            r5 = 1
            org.telegram.messenger.Utilities.loadWebpImage(r4, r2, r3, r1, r5)     // Catch:{ Exception -> 0x03fd }
            r0.close()     // Catch:{ Exception -> 0x03fd }
            goto L_0x0401
        L_0x03fd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0401:
            int r0 = r1.outWidth
            if (r0 == 0) goto L_0x0437
            int r2 = r1.outHeight
            if (r2 == 0) goto L_0x0437
            r3 = 800(0x320, float:1.121E-42)
            if (r0 > r3) goto L_0x0437
            if (r2 > r3) goto L_0x0437
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            r2 = r21
            r0.alt = r2
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r3.<init>()
            r0.stickerset = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r8.attributes
            r3.add(r0)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r0.<init>()
            int r3 = r1.outWidth
            r0.w = r3
            int r1 = r1.outHeight
            r0.h = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r8.attributes
            r1.add(r0)
            goto L_0x0439
        L_0x0437:
            r2 = r21
        L_0x0439:
            r3 = r8
            goto L_0x043f
        L_0x043b:
            r2 = r21
            r3 = r20
        L_0x043f:
            if (r38 == 0) goto L_0x0447
            java.lang.String r0 = r38.toString()
            r10 = r0
            goto L_0x0448
        L_0x0447:
            r10 = r2
        L_0x0448:
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            if (r18 == 0) goto L_0x0456
            java.lang.String r0 = "originalPath"
            r1 = r18
            r5.put(r0, r1)
        L_0x0456:
            if (r41 == 0) goto L_0x045f
            java.lang.String r0 = "forceDocument"
            java.lang.String r1 = "1"
            r5.put(r0, r1)
        L_0x045f:
            if (r7 == 0) goto L_0x0466
            java.lang.String r0 = "parentObject"
            r5.put(r0, r7)
        L_0x0466:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$MPg0uLkoYJmnXNFqAe0X_D_nmR0 r14 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$MPg0uLkoYJmnXNFqAe0X_D_nmR0
            r0 = r14
            r1 = r40
            r2 = r30
            r4 = r13
            r6 = r7
            r7 = r35
            r9 = r37
            r11 = r39
            r12 = r42
            r13 = r43
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r14)
            r1 = 1
            return r1
        L_0x0481:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, boolean, boolean, int):boolean");
    }

    static /* synthetic */ void lambda$prepareSendingDocumentInternal$56(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, String str3, ArrayList arrayList, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, str, hashMap, false, str2);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject2, str3, arrayList, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str2);
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
            org.telegram.tgnet.TLRPC$TL_document r17 = (org.telegram.tgnet.TLRPC$TL_document) r17
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
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3
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

    static /* synthetic */ void lambda$null$57(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, String str2, boolean z, int i) {
        MessageObject messageObject4 = messageObject2;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, messageObject4.messageOwner.attachPath, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, messageObject4.messageOwner.attachPath, j, messageObject3, str2, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str);
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
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList4.get(i3), (String) arrayList2.get(i3), (Uri) null, str2, j, messageObject, i3 == 0 ? str : null, (ArrayList<TLRPC$MessageEntity>) null, messageObject2, false, z, i)) {
                    z2 = true;
                }
                i3++;
            }
        } else {
            z2 = false;
        }
        if (arrayList5 != null) {
            while (i2 < arrayList3.size()) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) null, (String) null, (Uri) arrayList5.get(i2), str2, j, messageObject, (i2 == 0 && (arrayList4 == null || arrayList.size() == 0)) ? str : null, (ArrayList<TLRPC$MessageEntity>) null, messageObject2, false, z, i)) {
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

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, Uri uri, long j, MessageObject messageObject, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, ArrayList<TLRPC$InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject2, boolean z, int i2) {
        ArrayList<TLRPC$InputDocument> arrayList3 = arrayList2;
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

    public static void prepareSendingBotContextResult(AccountInstance accountInstance, TLRPC$BotInlineResult tLRPC$BotInlineResult, HashMap<String, String> hashMap, long j, MessageObject messageObject, boolean z, int i) {
        TLRPC$BotInlineResult tLRPC$BotInlineResult2 = tLRPC$BotInlineResult;
        if (tLRPC$BotInlineResult2 != null) {
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult2.send_message;
            if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaAuto) {
                new Thread(new Runnable(j, tLRPC$BotInlineResult, accountInstance, hashMap, messageObject, z, i) {
                    private final /* synthetic */ long f$0;
                    private final /* synthetic */ TLRPC$BotInlineResult f$1;
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
            } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageText) {
                TLRPC$TL_webPagePending tLRPC$TL_webPagePending = null;
                if (((int) j) == 0) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= tLRPC$BotInlineResult2.send_message.entities.size()) {
                            break;
                        }
                        TLRPC$MessageEntity tLRPC$MessageEntity = tLRPC$BotInlineResult2.send_message.entities.get(i2);
                        if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityUrl) {
                            tLRPC$TL_webPagePending = new TLRPC$TL_webPagePending();
                            String str = tLRPC$BotInlineResult2.send_message.message;
                            int i3 = tLRPC$MessageEntity.offset;
                            tLRPC$TL_webPagePending.url = str.substring(i3, tLRPC$MessageEntity.length + i3);
                            break;
                        }
                        i2++;
                    }
                }
                TLRPC$TL_webPagePending tLRPC$TL_webPagePending2 = tLRPC$TL_webPagePending;
                SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
                TLRPC$BotInlineMessage tLRPC$BotInlineMessage2 = tLRPC$BotInlineResult2.send_message;
                sendMessagesHelper.sendMessage(tLRPC$BotInlineMessage2.message, j, messageObject, tLRPC$TL_webPagePending2, !tLRPC$BotInlineMessage2.no_webpage, tLRPC$BotInlineMessage2.entities, tLRPC$BotInlineMessage2.reply_markup, hashMap, z, i);
            } else {
                long j2 = j;
                if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaVenue) {
                    TLRPC$TL_messageMediaVenue tLRPC$TL_messageMediaVenue = new TLRPC$TL_messageMediaVenue();
                    TLRPC$BotInlineMessage tLRPC$BotInlineMessage3 = tLRPC$BotInlineResult2.send_message;
                    tLRPC$TL_messageMediaVenue.geo = tLRPC$BotInlineMessage3.geo;
                    tLRPC$TL_messageMediaVenue.address = tLRPC$BotInlineMessage3.address;
                    tLRPC$TL_messageMediaVenue.title = tLRPC$BotInlineMessage3.title;
                    tLRPC$TL_messageMediaVenue.provider = tLRPC$BotInlineMessage3.provider;
                    tLRPC$TL_messageMediaVenue.venue_id = tLRPC$BotInlineMessage3.venue_id;
                    String str2 = tLRPC$BotInlineMessage3.venue_type;
                    tLRPC$TL_messageMediaVenue.venue_id = str2;
                    tLRPC$TL_messageMediaVenue.venue_type = str2;
                    if (str2 == null) {
                        tLRPC$TL_messageMediaVenue.venue_type = "";
                    }
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaVenue, j, messageObject, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
                } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaGeo) {
                    if (tLRPC$BotInlineMessage.period != 0) {
                        TLRPC$TL_messageMediaGeoLive tLRPC$TL_messageMediaGeoLive = new TLRPC$TL_messageMediaGeoLive();
                        TLRPC$BotInlineMessage tLRPC$BotInlineMessage4 = tLRPC$BotInlineResult2.send_message;
                        tLRPC$TL_messageMediaGeoLive.period = tLRPC$BotInlineMessage4.period;
                        tLRPC$TL_messageMediaGeoLive.geo = tLRPC$BotInlineMessage4.geo;
                        accountInstance.getSendMessagesHelper().sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaGeoLive, j, messageObject, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
                        return;
                    }
                    TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo = new TLRPC$TL_messageMediaGeo();
                    tLRPC$TL_messageMediaGeo.geo = tLRPC$BotInlineResult2.send_message.geo;
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaGeo, j, messageObject, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
                } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaContact) {
                    TLRPC$TL_user tLRPC$TL_user = new TLRPC$TL_user();
                    TLRPC$BotInlineMessage tLRPC$BotInlineMessage5 = tLRPC$BotInlineResult2.send_message;
                    tLRPC$TL_user.phone = tLRPC$BotInlineMessage5.phone_number;
                    tLRPC$TL_user.first_name = tLRPC$BotInlineMessage5.first_name;
                    tLRPC$TL_user.last_name = tLRPC$BotInlineMessage5.last_name;
                    TLRPC$TL_restrictionReason tLRPC$TL_restrictionReason = new TLRPC$TL_restrictionReason();
                    tLRPC$TL_restrictionReason.text = tLRPC$BotInlineResult2.send_message.vcard;
                    tLRPC$TL_restrictionReason.platform = "";
                    tLRPC$TL_restrictionReason.reason = "";
                    tLRPC$TL_user.restriction_reason.add(tLRPC$TL_restrictionReason);
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC$User) tLRPC$TL_user, j, messageObject, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v2, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v58, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x03ba  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x03c0  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x03cc  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0417  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingBotContextResult$62(long r20, org.telegram.tgnet.TLRPC$BotInlineResult r22, org.telegram.messenger.AccountInstance r23, java.util.HashMap r24, org.telegram.messenger.MessageObject r25, boolean r26, int r27) {
        /*
            r10 = r22
            r11 = r24
            r7 = r20
            int r0 = (int) r7
            r2 = 1
            if (r0 != 0) goto L_0x000c
            r12 = 1
            goto L_0x000d
        L_0x000c:
            r12 = 0
        L_0x000d:
            java.lang.String r3 = r10.type
            java.lang.String r4 = "game"
            boolean r3 = r4.equals(r3)
            if (r3 == 0) goto L_0x004c
            if (r0 != 0) goto L_0x001a
            return
        L_0x001a:
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
            if (r3 != 0) goto L_0x0038
            org.telegram.tgnet.TLRPC$TL_photoEmpty r3 = new org.telegram.tgnet.TLRPC$TL_photoEmpty
            r3.<init>()
            r0.photo = r3
        L_0x0038:
            org.telegram.tgnet.TLRPC$Document r3 = r10.document
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r6 == 0) goto L_0x0045
            r0.document = r3
            int r3 = r0.flags
            r3 = r3 | r2
            r0.flags = r3
        L_0x0045:
            r18 = r0
            r0 = 0
            r2 = 0
            r6 = 0
            goto L_0x03ff
        L_0x004c:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMediaResult
            if (r0 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$Document r0 = r10.document
            if (r0 == 0) goto L_0x0061
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r3 == 0) goto L_0x03f9
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            r2 = r0
            r0 = 0
        L_0x005c:
            r6 = 0
        L_0x005d:
            r18 = 0
            goto L_0x03ff
        L_0x0061:
            org.telegram.tgnet.TLRPC$Photo r0 = r10.photo
            if (r0 == 0) goto L_0x03f9
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r3 == 0) goto L_0x03f9
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            r2 = 0
            goto L_0x005c
        L_0x006d:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r10.content
            if (r0 == 0) goto L_0x03f9
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
            if (r6 == 0) goto L_0x00ac
            java.lang.String r6 = r0.getAbsolutePath()
            goto L_0x00b0
        L_0x00ac:
            org.telegram.tgnet.TLRPC$WebDocument r6 = r10.content
            java.lang.String r6 = r6.url
        L_0x00b0:
            java.lang.String r9 = r10.type
            int r14 = r9.hashCode()
            java.lang.String r4 = "voice"
            java.lang.String r3 = "video"
            java.lang.String r2 = "audio"
            java.lang.String r1 = "gif"
            java.lang.String r5 = "sticker"
            switch(r14) {
                case -1890252483: goto L_0x00f6;
                case 102340: goto L_0x00ee;
                case 3143036: goto L_0x00e6;
                case 93166550: goto L_0x00de;
                case 106642994: goto L_0x00d4;
                case 112202875: goto L_0x00cc;
                case 112386354: goto L_0x00c4;
                default: goto L_0x00c3;
            }
        L_0x00c3:
            goto L_0x00fe
        L_0x00c4:
            boolean r9 = r9.equals(r4)
            if (r9 == 0) goto L_0x00fe
            r9 = 1
            goto L_0x00ff
        L_0x00cc:
            boolean r9 = r9.equals(r3)
            if (r9 == 0) goto L_0x00fe
            r9 = 3
            goto L_0x00ff
        L_0x00d4:
            java.lang.String r14 = "photo"
            boolean r9 = r9.equals(r14)
            if (r9 == 0) goto L_0x00fe
            r9 = 6
            goto L_0x00ff
        L_0x00de:
            boolean r9 = r9.equals(r2)
            if (r9 == 0) goto L_0x00fe
            r9 = 0
            goto L_0x00ff
        L_0x00e6:
            boolean r9 = r9.equals(r15)
            if (r9 == 0) goto L_0x00fe
            r9 = 2
            goto L_0x00ff
        L_0x00ee:
            boolean r9 = r9.equals(r1)
            if (r9 == 0) goto L_0x00fe
            r9 = 5
            goto L_0x00ff
        L_0x00f6:
            boolean r9 = r9.equals(r5)
            if (r9 == 0) goto L_0x00fe
            r9 = 4
            goto L_0x00ff
        L_0x00fe:
            r9 = -1
        L_0x00ff:
            java.lang.String r14 = "x"
            switch(r9) {
                case 0: goto L_0x0158;
                case 1: goto L_0x0158;
                case 2: goto L_0x0158;
                case 3: goto L_0x0158;
                case 4: goto L_0x0158;
                case 5: goto L_0x0158;
                case 6: goto L_0x010c;
                default: goto L_0x0105;
            }
        L_0x0105:
            r5 = 0
            r0 = r5
            r2 = r0
            r18 = r2
            goto L_0x03ff
        L_0x010c:
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x011c
            org.telegram.messenger.SendMessagesHelper r0 = r23.getSendMessagesHelper()
            r1 = 0
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r6, r1)
            goto L_0x011d
        L_0x011c:
            r0 = 0
        L_0x011d:
            if (r0 != 0) goto L_0x0155
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r1 = r23.getConnectionsManager()
            int r1 = r1.getCurrentTime()
            r0.date = r1
            r1 = 0
            byte[] r2 = new byte[r1]
            r0.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize r2 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r2.<init>()
            int[] r3 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r4 = r3[r1]
            r2.w = r4
            r1 = 1
            r3 = r3[r1]
            r2.h = r3
            r2.size = r1
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r1.<init>()
            r2.location = r1
            r2.type = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r0.sizes
            r1.add(r2)
        L_0x0155:
            r2 = 0
            goto L_0x005d
        L_0x0158:
            org.telegram.tgnet.TLRPC$TL_document r9 = new org.telegram.tgnet.TLRPC$TL_document
            r9.<init>()
            r7 = 0
            r9.id = r7
            r7 = 0
            r9.size = r7
            r9.dc_id = r7
            org.telegram.tgnet.TLRPC$WebDocument r8 = r10.content
            java.lang.String r8 = r8.mime_type
            r9.mime_type = r8
            byte[] r8 = new byte[r7]
            r9.file_reference = r8
            org.telegram.tgnet.ConnectionsManager r7 = r23.getConnectionsManager()
            int r7 = r7.getCurrentTime()
            r9.date = r7
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r7.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r9.attributes
            r8.add(r7)
            java.lang.String r8 = r10.type
            int r19 = r8.hashCode()
            switch(r19) {
                case -1890252483: goto L_0x01b6;
                case 102340: goto L_0x01ae;
                case 3143036: goto L_0x01a6;
                case 93166550: goto L_0x019e;
                case 112202875: goto L_0x0196;
                case 112386354: goto L_0x018e;
                default: goto L_0x018d;
            }
        L_0x018d:
            goto L_0x01be
        L_0x018e:
            boolean r1 = r8.equals(r4)
            if (r1 == 0) goto L_0x01be
            r1 = 1
            goto L_0x01bf
        L_0x0196:
            boolean r1 = r8.equals(r3)
            if (r1 == 0) goto L_0x01be
            r1 = 4
            goto L_0x01bf
        L_0x019e:
            boolean r1 = r8.equals(r2)
            if (r1 == 0) goto L_0x01be
            r1 = 2
            goto L_0x01bf
        L_0x01a6:
            boolean r1 = r8.equals(r15)
            if (r1 == 0) goto L_0x01be
            r1 = 3
            goto L_0x01bf
        L_0x01ae:
            boolean r1 = r8.equals(r1)
            if (r1 == 0) goto L_0x01be
            r1 = 0
            goto L_0x01bf
        L_0x01b6:
            boolean r1 = r8.equals(r5)
            if (r1 == 0) goto L_0x01be
            r1 = 5
            goto L_0x01bf
        L_0x01be:
            r1 = -1
        L_0x01bf:
            r2 = 55
            if (r1 == 0) goto L_0x0356
            r3 = 1
            if (r1 == r3) goto L_0x033d
            r3 = 2
            if (r1 == r3) goto L_0x0313
            r0 = 3
            if (r1 == r0) goto L_0x02e3
            r0 = 1119092736(0x42b40000, float:90.0)
            r3 = 4
            if (r1 == r3) goto L_0x0264
            r3 = 5
            if (r1 == r3) goto L_0x01d7
        L_0x01d4:
            r5 = 0
            goto L_0x03b6
        L_0x01d7:
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r1.<init>()
            java.lang.String r3 = ""
            r1.alt = r3
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r3.<init>()
            r1.stickerset = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r9.attributes
            r3.add(r1)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r1.<init>()
            int[] r3 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r4 = 0
            r5 = r3[r4]
            r1.w = r5
            r4 = 1
            r3 = r3[r4]
            r1.h = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r9.attributes
            r3.add(r1)
            java.lang.String r1 = "sticker.webp"
            r7.file_name = r1
            org.telegram.tgnet.TLRPC$WebDocument r1 = r10.thumb     // Catch:{ all -> 0x025e }
            if (r1 == 0) goto L_0x01d4
            java.io.File r1 = new java.io.File     // Catch:{ all -> 0x025e }
            r3 = 4
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r3)     // Catch:{ all -> 0x025e }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x025e }
            r4.<init>()     // Catch:{ all -> 0x025e }
            org.telegram.tgnet.TLRPC$WebDocument r5 = r10.thumb     // Catch:{ all -> 0x025e }
            java.lang.String r5 = r5.url     // Catch:{ all -> 0x025e }
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)     // Catch:{ all -> 0x025e }
            r4.append(r5)     // Catch:{ all -> 0x025e }
            r4.append(r13)     // Catch:{ all -> 0x025e }
            org.telegram.tgnet.TLRPC$WebDocument r5 = r10.thumb     // Catch:{ all -> 0x025e }
            java.lang.String r5 = r5.url     // Catch:{ all -> 0x025e }
            java.lang.String r8 = "webp"
            java.lang.String r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r8)     // Catch:{ all -> 0x025e }
            r4.append(r5)     // Catch:{ all -> 0x025e }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x025e }
            r1.<init>(r3, r4)     // Catch:{ all -> 0x025e }
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ all -> 0x025e }
            r3 = 0
            r4 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r3, r0, r0, r4)     // Catch:{ all -> 0x025e }
            if (r1 == 0) goto L_0x01d4
            r3 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r0, r0, r2, r3)     // Catch:{ all -> 0x025e }
            if (r0 == 0) goto L_0x0259
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r9.thumbs     // Catch:{ all -> 0x025e }
            r2.add(r0)     // Catch:{ all -> 0x025e }
            int r0 = r9.flags     // Catch:{ all -> 0x025e }
            r2 = 1
            r0 = r0 | r2
            r9.flags = r0     // Catch:{ all -> 0x025e }
        L_0x0259:
            r1.recycle()     // Catch:{ all -> 0x025e }
            goto L_0x01d4
        L_0x025e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01d4
        L_0x0264:
            java.lang.String r1 = "video.mp4"
            r7.file_name = r1
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            int[] r3 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r4 = 0
            r5 = r3[r4]
            r1.w = r5
            r4 = 1
            r3 = r3[r4]
            r1.h = r3
            int r3 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r1.duration = r3
            r1.supports_streaming = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r9.attributes
            r3.add(r1)
            org.telegram.tgnet.TLRPC$WebDocument r1 = r10.thumb     // Catch:{ all -> 0x02dd }
            if (r1 == 0) goto L_0x01d4
            java.io.File r1 = new java.io.File     // Catch:{ all -> 0x02dd }
            r3 = 4
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r3)     // Catch:{ all -> 0x02dd }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x02dd }
            r4.<init>()     // Catch:{ all -> 0x02dd }
            org.telegram.tgnet.TLRPC$WebDocument r5 = r10.thumb     // Catch:{ all -> 0x02dd }
            java.lang.String r5 = r5.url     // Catch:{ all -> 0x02dd }
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)     // Catch:{ all -> 0x02dd }
            r4.append(r5)     // Catch:{ all -> 0x02dd }
            r4.append(r13)     // Catch:{ all -> 0x02dd }
            org.telegram.tgnet.TLRPC$WebDocument r5 = r10.thumb     // Catch:{ all -> 0x02dd }
            java.lang.String r5 = r5.url     // Catch:{ all -> 0x02dd }
            java.lang.String r8 = "jpg"
            java.lang.String r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r8)     // Catch:{ all -> 0x02dd }
            r4.append(r5)     // Catch:{ all -> 0x02dd }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x02dd }
            r1.<init>(r3, r4)     // Catch:{ all -> 0x02dd }
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ all -> 0x02dd }
            r3 = 0
            r4 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r3, r0, r0, r4)     // Catch:{ all -> 0x02dd }
            if (r1 == 0) goto L_0x01d4
            r3 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r0, r0, r2, r3)     // Catch:{ all -> 0x02dd }
            if (r0 == 0) goto L_0x02d8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r9.thumbs     // Catch:{ all -> 0x02dd }
            r2.add(r0)     // Catch:{ all -> 0x02dd }
            int r0 = r9.flags     // Catch:{ all -> 0x02dd }
            r2 = 1
            r0 = r0 | r2
            r9.flags = r0     // Catch:{ all -> 0x02dd }
        L_0x02d8:
            r1.recycle()     // Catch:{ all -> 0x02dd }
            goto L_0x01d4
        L_0x02dd:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01d4
        L_0x02e3:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r10.content
            java.lang.String r0 = r0.mime_type
            r1 = 47
            int r0 = r0.lastIndexOf(r1)
            r1 = -1
            if (r0 == r1) goto L_0x030f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "file."
            r1.append(r2)
            org.telegram.tgnet.TLRPC$WebDocument r2 = r10.content
            java.lang.String r2 = r2.mime_type
            r3 = 1
            int r0 = r0 + r3
            java.lang.String r0 = r2.substring(r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r7.file_name = r0
            goto L_0x01d4
        L_0x030f:
            r7.file_name = r15
            goto L_0x01d4
        L_0x0313:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r1
            java.lang.String r1 = r10.title
            r0.title = r1
            int r1 = r0.flags
            r2 = 1
            r1 = r1 | r2
            r0.flags = r1
            java.lang.String r2 = r10.description
            if (r2 == 0) goto L_0x0332
            r0.performer = r2
            r2 = 2
            r1 = r1 | r2
            r0.flags = r1
        L_0x0332:
            java.lang.String r1 = "audio.mp3"
            r7.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r9.attributes
            r1.add(r0)
            goto L_0x01d4
        L_0x033d:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r1
            r1 = 1
            r0.voice = r1
            java.lang.String r1 = "audio.ogg"
            r7.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r9.attributes
            r1.add(r0)
            goto L_0x01d4
        L_0x0356:
            java.lang.String r0 = "animation.gif"
            r7.file_name = r0
            java.lang.String r0 = "mp4"
            boolean r1 = r6.endsWith(r0)
            if (r1 == 0) goto L_0x0371
            java.lang.String r1 = "video/mp4"
            r9.mime_type = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r9.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r3.<init>()
            r1.add(r3)
            goto L_0x0375
        L_0x0371:
            java.lang.String r1 = "image/gif"
            r9.mime_type = r1
        L_0x0375:
            r1 = 90
            if (r12 == 0) goto L_0x037c
            r3 = 90
            goto L_0x037e
        L_0x037c:
            r3 = 320(0x140, float:4.48E-43)
        L_0x037e:
            boolean r0 = r6.endsWith(r0)     // Catch:{ all -> 0x03b1 }
            if (r0 == 0) goto L_0x038b
            r4 = 1
            android.graphics.Bitmap r0 = android.media.ThumbnailUtils.createVideoThumbnail(r6, r4)     // Catch:{ all -> 0x03b1 }
            r5 = 0
            goto L_0x0392
        L_0x038b:
            r4 = 1
            float r0 = (float) r3
            r5 = 0
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r6, r5, r0, r0, r4)     // Catch:{ all -> 0x03af }
        L_0x0392:
            if (r0 == 0) goto L_0x03b6
            float r4 = (float) r3     // Catch:{ all -> 0x03af }
            if (r3 <= r1) goto L_0x0399
            r2 = 80
        L_0x0399:
            r1 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r4, r4, r2, r1)     // Catch:{ all -> 0x03af }
            if (r2 == 0) goto L_0x03ab
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r9.thumbs     // Catch:{ all -> 0x03af }
            r1.add(r2)     // Catch:{ all -> 0x03af }
            int r1 = r9.flags     // Catch:{ all -> 0x03af }
            r2 = 1
            r1 = r1 | r2
            r9.flags = r1     // Catch:{ all -> 0x03af }
        L_0x03ab:
            r0.recycle()     // Catch:{ all -> 0x03af }
            goto L_0x03b6
        L_0x03af:
            r0 = move-exception
            goto L_0x03b3
        L_0x03b1:
            r0 = move-exception
            r5 = 0
        L_0x03b3:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03b6:
            java.lang.String r0 = r7.file_name
            if (r0 != 0) goto L_0x03bc
            r7.file_name = r15
        L_0x03bc:
            java.lang.String r0 = r9.mime_type
            if (r0 != 0) goto L_0x03c4
            java.lang.String r0 = "application/octet-stream"
            r9.mime_type = r0
        L_0x03c4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r9.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x03f4
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            int[] r1 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r2 = 0
            r3 = r1[r2]
            r0.w = r3
            r3 = 1
            r1 = r1[r3]
            r0.h = r1
            r0.size = r2
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r1.<init>()
            r0.location = r1
            r0.type = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r9.thumbs
            r1.add(r0)
            int r0 = r9.flags
            r0 = r0 | r3
            r9.flags = r0
        L_0x03f4:
            r0 = r5
            r18 = r0
            r2 = r9
            goto L_0x03ff
        L_0x03f9:
            r5 = 0
            r0 = r5
            r2 = r0
            r6 = r2
            r18 = r6
        L_0x03ff:
            if (r11 == 0) goto L_0x040c
            org.telegram.tgnet.TLRPC$WebDocument r1 = r10.content
            if (r1 == 0) goto L_0x040c
            java.lang.String r1 = r1.url
            java.lang.String r3 = "originalPath"
            r11.put(r3, r1)
        L_0x040c:
            r1 = 1
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r1]
            java.lang.String[] r4 = new java.lang.String[r1]
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r2)
            if (r5 == 0) goto L_0x0440
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r2.thumbs
            r7 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r7)
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r2)
            boolean r8 = r7.exists()
            if (r8 != 0) goto L_0x042d
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r1)
        L_0x042d:
            java.lang.String r14 = r7.getAbsolutePath()
            r15 = 0
            r16 = 0
            r13 = r2
            ensureMediaThumbExists(r12, r13, r14, r15, r16)
            r1 = 1
            java.lang.String r1 = getKeyForPhotoSize(r5, r3, r1, r1)
            r5 = 0
            r4[r5] = r1
        L_0x0440:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$MIZbjfh6Bb5p9kEGsqUHYA3MKhI r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$MIZbjfh6Bb5p9kEGsqUHYA3MKhI
            r1 = r16
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

    static /* synthetic */ void lambda$null$61(TLRPC$TL_document tLRPC$TL_document, Bitmap[] bitmapArr, String[] strArr, AccountInstance accountInstance, String str, long j, MessageObject messageObject, TLRPC$BotInlineResult tLRPC$BotInlineResult, HashMap hashMap, boolean z, int i, TLRPC$TL_photo tLRPC$TL_photo, TLRPC$TL_game tLRPC$TL_game) {
        TLRPC$BotInlineResult tLRPC$BotInlineResult2 = tLRPC$BotInlineResult;
        if (tLRPC$TL_document != null) {
            if (!(bitmapArr[0] == null || strArr[0] == null)) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
            }
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult2.send_message;
            sendMessagesHelper.sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject, tLRPC$BotInlineMessage.message, tLRPC$BotInlineMessage.entities, tLRPC$BotInlineMessage.reply_markup, hashMap, z, i, 0, tLRPC$BotInlineResult);
        } else if (tLRPC$TL_photo != null) {
            SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
            TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult2.content;
            String str2 = tLRPC$WebDocument != null ? tLRPC$WebDocument.url : null;
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage2 = tLRPC$BotInlineResult2.send_message;
            sendMessagesHelper2.sendMessage(tLRPC$TL_photo, str2, j, messageObject, tLRPC$BotInlineMessage2.message, tLRPC$BotInlineMessage2.entities, tLRPC$BotInlineMessage2.reply_markup, hashMap, z, i, 0, tLRPC$BotInlineResult);
        } else if (tLRPC$TL_game != null) {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_game, j, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
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
                accountInstance.getSendMessagesHelper().sendMessage(trimmedString.substring(i3, Math.min(i2 * 4096, trimmedString.length())), j, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i);
            }
        }
    }

    public static void ensureMediaThumbExists(boolean z, TLObject tLObject, String str, Uri uri, long j) {
        boolean z2;
        TLRPC$PhotoSize scaleAndSaveImage;
        TLRPC$PhotoSize scaleAndSaveImage2;
        TLObject tLObject2 = tLObject;
        String str2 = str;
        Uri uri2 = uri;
        if (tLObject2 instanceof TLRPC$TL_photo) {
            TLRPC$TL_photo tLRPC$TL_photo = (TLRPC$TL_photo) tLObject2;
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_photo.sizes, 90);
            if (closestPhotoSizeWithSize instanceof TLRPC$TL_photoStrippedSize) {
                z2 = true;
            } else {
                z2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists();
            }
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_photo.sizes, AndroidUtilities.getPhotoSize());
            boolean exists = FileLoader.getPathToAttach(closestPhotoSizeWithSize2, false).exists();
            if (!z2 || !exists) {
                Bitmap loadBitmap = ImageLoader.loadBitmap(str2, uri2, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
                if (loadBitmap == null) {
                    loadBitmap = ImageLoader.loadBitmap(str2, uri2, 800.0f, 800.0f, true);
                }
                Bitmap bitmap = loadBitmap;
                if (!exists && (scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize2, bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101, false)) != closestPhotoSizeWithSize2) {
                    tLRPC$TL_photo.sizes.add(0, scaleAndSaveImage2);
                }
                if (!z2 && (scaleAndSaveImage = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize, bitmap, 90.0f, 90.0f, 55, true, false)) != closestPhotoSizeWithSize) {
                    tLRPC$TL_photo.sizes.add(0, scaleAndSaveImage);
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        } else if (tLObject2 instanceof TLRPC$TL_document) {
            TLRPC$TL_document tLRPC$TL_document = (TLRPC$TL_document) tLObject2;
            if ((MessageObject.isVideoDocument(tLRPC$TL_document) || MessageObject.isNewGifDocument((TLRPC$Document) tLRPC$TL_document)) && MessageObject.isDocumentHasThumb(tLRPC$TL_document)) {
                int i = 320;
                TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_document.thumbs, 320);
                if (!(closestPhotoSizeWithSize3 instanceof TLRPC$TL_photoStrippedSize) && !FileLoader.getPathToAttach(closestPhotoSizeWithSize3, true).exists()) {
                    Bitmap createVideoThumbnail = createVideoThumbnail(str2, j);
                    Bitmap createVideoThumbnail2 = createVideoThumbnail == null ? ThumbnailUtils.createVideoThumbnail(str2, 1) : createVideoThumbnail;
                    if (z) {
                        i = 90;
                    }
                    float f = (float) i;
                    tLRPC$TL_document.thumbs.set(0, ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize3, createVideoThumbnail2, f, f, i > 90 ? 80 : 55, false, true));
                }
            }
        }
    }

    public static String getKeyForPhotoSize(TLRPC$PhotoSize tLRPC$PhotoSize, Bitmap[] bitmapArr, boolean z, boolean z2) {
        if (tLRPC$PhotoSize == null || tLRPC$PhotoSize.location == null) {
            return null;
        }
        Point messageSize = ChatMessageCell.getMessageSize(tLRPC$PhotoSize.w, tLRPC$PhotoSize.h);
        if (bitmapArr != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                File pathToAttach = FileLoader.getPathToAttach(tLRPC$PhotoSize, z2);
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
        return String.format(Locale.US, z ? "%d_%d@%d_%d_b" : "%d_%d@%d_%d", new Object[]{Long.valueOf(tLRPC$PhotoSize.location.volume_id), Integer.valueOf(tLRPC$PhotoSize.location.local_id), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density))});
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v0, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v1, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v2, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v3, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v4, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v5, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v6, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v30, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v7, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v8, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v9, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v10, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v11, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v12, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v5, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v117, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v118, resolved type: java.util.ArrayList} */
    /* JADX WARNING: type inference failed for: r3v32, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r6v32 */
    /* JADX WARNING: type inference failed for: r6v33 */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x07cf, code lost:
        if (r8 == (r21 - 1)) goto L_0x07d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x0ab4, code lost:
        if (r59.size() == 1) goto L_0x0abd;
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r6v20, types: [int, boolean] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02d1 A[Catch:{ Exception -> 0x02c2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02dc A[Catch:{ Exception -> 0x02c2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02e9 A[Catch:{ Exception -> 0x0308 }] */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0316  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0342  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x064e  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0735  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x075b  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x07a0  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x07a6  */
    /* JADX WARNING: Removed duplicated region for block: B:350:0x0905  */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x0946  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0ada  */
    /* JADX WARNING: Removed duplicated region for block: B:448:0x0b0a  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0ba4  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0bb1  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0bb7  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0bc9 A[LOOP:4: B:466:0x0bc3->B:468:0x0bc9, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0758 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:486:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0199  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingMedia$72(java.util.ArrayList r59, long r60, org.telegram.messenger.AccountInstance r62, boolean r63, boolean r64, org.telegram.messenger.MessageObject r65, org.telegram.messenger.MessageObject r66, boolean r67, int r68, androidx.core.view.inputmethod.InputContentInfoCompat r69) {
        /*
            r1 = r59
            r14 = r60
            r13 = r62
            long r18 = java.lang.System.currentTimeMillis()
            int r12 = r59.size()
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
            org.telegram.messenger.MessagesController r2 = r62.getMessagesController()
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
            java.lang.String r6 = ".webp"
            r7 = 73
            java.lang.String r5 = ".gif"
            r20 = 3
            java.lang.String r4 = "_"
            if (r10 == 0) goto L_0x0040
            if (r8 < r7) goto L_0x0181
        L_0x0040:
            if (r63 != 0) goto L_0x0181
            if (r64 == 0) goto L_0x0181
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r2 = 0
        L_0x004a:
            if (r2 >= r12) goto L_0x0177
            java.lang.Object r16 = r1.get(r2)
            r7 = r16
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r7 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r7
            org.telegram.messenger.MediaController$SearchImage r3 = r7.searchImage
            if (r3 != 0) goto L_0x0160
            boolean r3 = r7.isVideo
            if (r3 != 0) goto L_0x0160
            java.lang.String r3 = r7.path
            if (r3 != 0) goto L_0x006f
            android.net.Uri r9 = r7.uri
            if (r9 == 0) goto L_0x006f
            java.lang.String r3 = org.telegram.messenger.AndroidUtilities.getPath(r9)
            android.net.Uri r9 = r7.uri
            java.lang.String r9 = r9.toString()
            goto L_0x0070
        L_0x006f:
            r9 = r3
        L_0x0070:
            if (r3 == 0) goto L_0x0080
            boolean r22 = r3.endsWith(r5)
            if (r22 != 0) goto L_0x0160
            boolean r22 = r3.endsWith(r6)
            if (r22 == 0) goto L_0x0080
            goto L_0x0160
        L_0x0080:
            java.lang.String r11 = r7.path
            r23 = r2
            android.net.Uri r2 = r7.uri
            boolean r2 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r11, r2)
            if (r2 == 0) goto L_0x008e
        L_0x008c:
            goto L_0x0162
        L_0x008e:
            if (r3 != 0) goto L_0x00a3
            android.net.Uri r2 = r7.uri
            if (r2 == 0) goto L_0x00a3
            boolean r2 = org.telegram.messenger.MediaController.isGif(r2)
            if (r2 != 0) goto L_0x0162
            android.net.Uri r2 = r7.uri
            boolean r2 = org.telegram.messenger.MediaController.isWebp(r2)
            if (r2 == 0) goto L_0x00a3
            goto L_0x008c
        L_0x00a3:
            if (r3 == 0) goto L_0x00ca
            java.io.File r2 = new java.io.File
            r2.<init>(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r9)
            r11 = r5
            r9 = r6
            long r5 = r2.length()
            r3.append(r5)
            r3.append(r4)
            long r5 = r2.lastModified()
            r3.append(r5)
            java.lang.String r2 = r3.toString()
            goto L_0x00cd
        L_0x00ca:
            r11 = r5
            r9 = r6
            r2 = 0
        L_0x00cd:
            if (r10 != 0) goto L_0x0134
            int r3 = r7.ttl
            if (r3 != 0) goto L_0x0134
            org.telegram.messenger.MessagesStorage r3 = r62.getMessagesStorage()
            if (r10 != 0) goto L_0x00db
            r5 = 0
            goto L_0x00dc
        L_0x00db:
            r5 = 3
        L_0x00dc:
            java.lang.Object[] r2 = r3.getSentFile(r2, r5)
            if (r2 == 0) goto L_0x00ed
            r3 = 0
            r5 = r2[r3]
            org.telegram.tgnet.TLRPC$TL_photo r5 = (org.telegram.tgnet.TLRPC$TL_photo) r5
            r3 = 1
            r2 = r2[r3]
            java.lang.String r2 = (java.lang.String) r2
            goto L_0x00ef
        L_0x00ed:
            r2 = 0
            r5 = 0
        L_0x00ef:
            if (r5 != 0) goto L_0x0116
            android.net.Uri r3 = r7.uri
            if (r3 == 0) goto L_0x0116
            org.telegram.messenger.MessagesStorage r3 = r62.getMessagesStorage()
            android.net.Uri r6 = r7.uri
            java.lang.String r6 = org.telegram.messenger.AndroidUtilities.getPath(r6)
            r24 = r2
            if (r10 != 0) goto L_0x0105
            r2 = 0
            goto L_0x0106
        L_0x0105:
            r2 = 3
        L_0x0106:
            java.lang.Object[] r2 = r3.getSentFile(r6, r2)
            if (r2 == 0) goto L_0x0118
            r3 = 0
            r5 = r2[r3]
            org.telegram.tgnet.TLRPC$TL_photo r5 = (org.telegram.tgnet.TLRPC$TL_photo) r5
            r3 = 1
            r2 = r2[r3]
            java.lang.String r2 = (java.lang.String) r2
        L_0x0116:
            r24 = r2
        L_0x0118:
            r25 = r5
            java.lang.String r5 = r7.path
            android.net.Uri r6 = r7.uri
            r26 = 0
            r2 = r10
            r3 = r25
            r29 = r4
            r4 = r5
            r5 = r6
            r30 = r9
            r15 = 73
            r9 = r7
            r6 = r26
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r2 = r24
            goto L_0x013d
        L_0x0134:
            r29 = r4
            r30 = r9
            r15 = 73
            r9 = r7
            r2 = 0
            r3 = 0
        L_0x013d:
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r4 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker
            r14 = 0
            r4.<init>()
            r0.put(r9, r4)
            if (r3 == 0) goto L_0x014d
            r4.parentObject = r2
            r4.photo = r3
            goto L_0x016a
        L_0x014d:
            java.util.concurrent.CountDownLatch r2 = new java.util.concurrent.CountDownLatch
            r3 = 1
            r2.<init>(r3)
            r4.sync = r2
            java.util.concurrent.ThreadPoolExecutor r2 = mediaSendThreadPool
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$la6jF8kETgbN8qhomDix2KNyxgg r3 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$la6jF8kETgbN8qhomDix2KNyxgg
            r3.<init>(r13, r9, r10)
            r2.execute(r3)
            goto L_0x016a
        L_0x0160:
            r23 = r2
        L_0x0162:
            r29 = r4
            r11 = r5
            r30 = r6
            r14 = 0
            r15 = 73
        L_0x016a:
            int r2 = r23 + 1
            r14 = r60
            r5 = r11
            r4 = r29
            r6 = r30
            r7 = 73
            goto L_0x004a
        L_0x0177:
            r29 = r4
            r11 = r5
            r30 = r6
            r14 = 0
            r15 = 73
            r9 = r0
            goto L_0x018a
        L_0x0181:
            r29 = r4
            r11 = r5
            r30 = r6
            r14 = 0
            r15 = 73
            r9 = r14
        L_0x018a:
            r2 = r14
            r3 = r2
            r4 = r3
            r23 = r4
            r31 = r23
            r0 = 0
            r5 = 0
            r16 = 0
            r32 = 0
        L_0x0197:
            if (r5 >= r12) goto L_0x0b95
            java.lang.Object r24 = r1.get(r5)
            r14 = r24
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r14 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r14
            if (r64 == 0) goto L_0x01b9
            if (r10 == 0) goto L_0x01a7
            if (r8 < r15) goto L_0x01b9
        L_0x01a7:
            r15 = 1
            if (r12 <= r15) goto L_0x01b9
            int r15 = r0 % 10
            if (r15 != 0) goto L_0x01b9
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r32 = r0.nextLong()
            r34 = r32
            r16 = 0
            goto L_0x01bd
        L_0x01b9:
            r34 = r16
            r16 = r0
        L_0x01bd:
            org.telegram.messenger.MediaController$SearchImage r0 = r14.searchImage
            java.lang.String r15 = "1"
            r17 = r5
            java.lang.String r5 = "final"
            java.lang.String r6 = "groupId"
            java.lang.String r7 = "mp4"
            java.lang.String r1 = "originalPath"
            r27 = r8
            java.lang.String r8 = ""
            r38 = 4
            if (r0 == 0) goto L_0x0533
            r39 = r2
            int r2 = r0.type
            r40 = r3
            java.lang.String r3 = "jpg"
            r41 = r4
            java.lang.String r4 = "."
            r42 = r9
            r9 = 1
            if (r2 != r9) goto L_0x03bf
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            org.telegram.messenger.MediaController$SearchImage r0 = r14.searchImage
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r2 == 0) goto L_0x01f8
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r9)
            goto L_0x0224
        L_0x01f8:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r14.searchImage
            java.lang.String r2 = r2.imageUrl
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r0.append(r2)
            r0.append(r4)
            org.telegram.messenger.MediaController$SearchImage r2 = r14.searchImage
            java.lang.String r2 = r2.imageUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r2, r3)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r2 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r38)
            r2.<init>(r5, r0)
            r0 = 0
        L_0x0224:
            if (r0 != 0) goto L_0x0347
            org.telegram.tgnet.TLRPC$TL_document r5 = new org.telegram.tgnet.TLRPC$TL_document
            r5.<init>()
            r44 = r11
            r43 = r12
            r11 = 0
            r5.id = r11
            r6 = 0
            byte[] r0 = new byte[r6]
            r5.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r62.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r5.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            java.lang.String r6 = "animation.gif"
            r0.file_name = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r5.attributes
            r6.add(r0)
            org.telegram.messenger.MediaController$SearchImage r0 = r14.searchImage
            int r0 = r0.size
            r5.size = r0
            r6 = 0
            r5.dc_id = r6
            java.lang.String r0 = r2.toString()
            boolean r0 = r0.endsWith(r7)
            if (r0 == 0) goto L_0x0272
            java.lang.String r0 = "video/mp4"
            r5.mime_type = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r5.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r6.<init>()
            r0.add(r6)
            goto L_0x0276
        L_0x0272:
            java.lang.String r0 = "image/gif"
            r5.mime_type = r0
        L_0x0276:
            boolean r0 = r2.exists()
            if (r0 == 0) goto L_0x027e
            r6 = r2
            goto L_0x0280
        L_0x027e:
            r2 = 0
            r6 = 0
        L_0x0280:
            if (r2 != 0) goto L_0x02b5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r14.searchImage
            java.lang.String r2 = r2.thumbUrl
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r0.append(r2)
            r0.append(r4)
            org.telegram.messenger.MediaController$SearchImage r2 = r14.searchImage
            java.lang.String r2 = r2.thumbUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r2, r3)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r3 = new java.io.File
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r38)
            r3.<init>(r2, r0)
            boolean r0 = r3.exists()
            if (r0 != 0) goto L_0x02b6
            r3 = 0
            goto L_0x02b6
        L_0x02b5:
            r3 = r2
        L_0x02b6:
            if (r3 == 0) goto L_0x030d
            if (r10 != 0) goto L_0x02c5
            int r0 = r14.ttl     // Catch:{ Exception -> 0x02c2 }
            if (r0 == 0) goto L_0x02bf
            goto L_0x02c5
        L_0x02bf:
            r0 = 320(0x140, float:4.48E-43)
            goto L_0x02c7
        L_0x02c2:
            r0 = move-exception
            r15 = 0
            goto L_0x0309
        L_0x02c5:
            r0 = 90
        L_0x02c7:
            java.lang.String r2 = r3.getAbsolutePath()     // Catch:{ Exception -> 0x02c2 }
            boolean r2 = r2.endsWith(r7)     // Catch:{ Exception -> 0x02c2 }
            if (r2 == 0) goto L_0x02dc
            java.lang.String r2 = r3.getAbsolutePath()     // Catch:{ Exception -> 0x02c2 }
            r4 = 1
            android.graphics.Bitmap r2 = android.media.ThumbnailUtils.createVideoThumbnail(r2, r4)     // Catch:{ Exception -> 0x02c2 }
            r15 = 0
            goto L_0x02e7
        L_0x02dc:
            r4 = 1
            java.lang.String r2 = r3.getAbsolutePath()     // Catch:{ Exception -> 0x02c2 }
            float r3 = (float) r0
            r15 = 0
            android.graphics.Bitmap r2 = org.telegram.messenger.ImageLoader.loadBitmap(r2, r15, r3, r3, r4)     // Catch:{ Exception -> 0x0308 }
        L_0x02e7:
            if (r2 == 0) goto L_0x030e
            float r3 = (float) r0     // Catch:{ Exception -> 0x0308 }
            r4 = 90
            if (r0 <= r4) goto L_0x02f1
            r0 = 80
            goto L_0x02f3
        L_0x02f1:
            r0 = 55
        L_0x02f3:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r0, r10)     // Catch:{ Exception -> 0x0308 }
            if (r0 == 0) goto L_0x0304
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r5.thumbs     // Catch:{ Exception -> 0x0308 }
            r3.add(r0)     // Catch:{ Exception -> 0x0308 }
            int r0 = r5.flags     // Catch:{ Exception -> 0x0308 }
            r3 = 1
            r0 = r0 | r3
            r5.flags = r0     // Catch:{ Exception -> 0x0308 }
        L_0x0304:
            r2.recycle()     // Catch:{ Exception -> 0x0308 }
            goto L_0x030e
        L_0x0308:
            r0 = move-exception
        L_0x0309:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x030e
        L_0x030d:
            r15 = 0
        L_0x030e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r5.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0342
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r14.searchImage
            int r3 = r2.width
            r0.w = r3
            int r2 = r2.height
            r0.h = r2
            r9 = 0
            r0.size = r9
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r2 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r2.<init>()
            r0.location = r2
            java.lang.String r2 = "x"
            r0.type = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.thumbs
            r2.add(r0)
            int r0 = r5.flags
            r21 = 1
            r0 = r0 | 1
            r5.flags = r0
            goto L_0x0345
        L_0x0342:
            r9 = 0
            r21 = 1
        L_0x0345:
            r2 = r6
            goto L_0x0352
        L_0x0347:
            r44 = r11
            r43 = r12
            r9 = 0
            r11 = 0
            r15 = 0
            r21 = 1
            r5 = r0
        L_0x0352:
            org.telegram.messenger.MediaController$SearchImage r0 = r14.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r2 != 0) goto L_0x0359
            goto L_0x035d
        L_0x0359:
            java.lang.String r0 = r2.toString()
        L_0x035d:
            r6 = r0
            org.telegram.messenger.MediaController$SearchImage r0 = r14.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r0 == 0) goto L_0x0367
            r8.put(r1, r0)
        L_0x0367:
            r0 = 0
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$nqq2i9mk4k2dAnH27bdICh2H6Hg r1 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$nqq2i9mk4k2dAnH27bdICh2H6Hg
            r7 = r39
            r2 = r1
            r4 = r40
            r3 = r65
            r46 = r4
            r45 = r41
            r4 = r62
            r15 = r17
            r25 = r11
            r12 = r7
            r7 = r8
            r11 = r27
            r8 = r0
            r47 = r10
            r48 = r42
            r17 = 0
            r9 = r60
            r49 = r11
            r50 = r44
            r11 = r66
            r51 = r12
            r21 = r43
            r12 = r14
            r14 = r13
            r13 = r67
            r17 = r15
            r15 = 0
            r14 = r68
            r2.<init>(r4, r5, r6, r7, r8, r9, r11, r12, r13, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            r0 = r16
            r36 = r17
            r37 = r34
            r4 = r45
            r3 = r46
            r24 = r47
            r28 = r48
            r27 = r49
            r40 = r50
            r2 = r51
            r22 = 1
            r34 = 73
            r39 = 0
            r35 = r15
            goto L_0x0b7d
        L_0x03bf:
            r47 = r10
            r50 = r11
            r21 = r12
            r9 = r15
            r49 = r27
            r51 = r39
            r46 = r40
            r45 = r41
            r48 = r42
            r15 = 0
            r25 = 0
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r2 == 0) goto L_0x03de
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            r13 = r47
            goto L_0x03e5
        L_0x03de:
            r13 = r47
            if (r13 != 0) goto L_0x03e4
            int r0 = r14.ttl
        L_0x03e4:
            r0 = r15
        L_0x03e5:
            if (r0 != 0) goto L_0x04ab
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.messenger.MediaController$SearchImage r7 = r14.searchImage
            java.lang.String r7 = r7.imageUrl
            java.lang.String r7 = org.telegram.messenger.Utilities.MD5(r7)
            r2.append(r7)
            r2.append(r4)
            org.telegram.messenger.MediaController$SearchImage r7 = r14.searchImage
            java.lang.String r7 = r7.imageUrl
            java.lang.String r7 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r7, r3)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            java.io.File r7 = new java.io.File
            java.io.File r10 = org.telegram.messenger.FileLoader.getDirectory(r38)
            r7.<init>(r10, r2)
            boolean r2 = r7.exists()
            if (r2 == 0) goto L_0x0430
            long r10 = r7.length()
            int r2 = (r10 > r25 ? 1 : (r10 == r25 ? 0 : -1))
            if (r2 == 0) goto L_0x0430
            org.telegram.messenger.SendMessagesHelper r0 = r62.getSendMessagesHelper()
            java.lang.String r2 = r7.toString()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r15)
            if (r0 == 0) goto L_0x0430
            r11 = 0
            goto L_0x0431
        L_0x0430:
            r11 = 1
        L_0x0431:
            if (r0 != 0) goto L_0x04a9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.messenger.MediaController$SearchImage r7 = r14.searchImage
            java.lang.String r7 = r7.thumbUrl
            java.lang.String r7 = org.telegram.messenger.Utilities.MD5(r7)
            r2.append(r7)
            r2.append(r4)
            org.telegram.messenger.MediaController$SearchImage r4 = r14.searchImage
            java.lang.String r4 = r4.thumbUrl
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r3)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r38)
            r3.<init>(r4, r2)
            boolean r2 = r3.exists()
            if (r2 == 0) goto L_0x0470
            org.telegram.messenger.SendMessagesHelper r0 = r62.getSendMessagesHelper()
            java.lang.String r2 = r3.toString()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r15)
        L_0x0470:
            if (r0 != 0) goto L_0x04a9
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r2 = r62.getConnectionsManager()
            int r2 = r2.getCurrentTime()
            r0.date = r2
            r12 = 0
            byte[] r2 = new byte[r12]
            r0.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize r2 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r2.<init>()
            org.telegram.messenger.MediaController$SearchImage r3 = r14.searchImage
            int r4 = r3.width
            r2.w = r4
            int r3 = r3.height
            r2.h = r3
            r2.size = r12
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r3 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r3.<init>()
            r2.location = r3
            java.lang.String r3 = "x"
            r2.type = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r0.sizes
            r3.add(r2)
            goto L_0x04ad
        L_0x04a9:
            r12 = 0
            goto L_0x04ad
        L_0x04ab:
            r12 = 0
            r11 = 1
        L_0x04ad:
            if (r0 == 0) goto L_0x0512
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r14.searchImage
            java.lang.String r2 = r2.imageUrl
            if (r2 == 0) goto L_0x04bd
            r10.put(r1, r2)
        L_0x04bd:
            if (r64 == 0) goto L_0x04ed
            int r1 = r16 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r8)
            r7 = r34
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r10.put(r6, r2)
            r2 = 10
            if (r1 == r2) goto L_0x04e3
            int r2 = r21 + -1
            r6 = r17
            if (r6 != r2) goto L_0x04e0
            goto L_0x04e5
        L_0x04e0:
            r16 = r1
            goto L_0x04f1
        L_0x04e3:
            r6 = r17
        L_0x04e5:
            r10.put(r5, r9)
            r16 = r1
            r32 = r25
            goto L_0x04f1
        L_0x04ed:
            r6 = r17
            r7 = r34
        L_0x04f1:
            r9 = 0
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$NzRFdrMj_c6d6hXgqa8qx2Mof-g r1 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$NzRFdrMj_c6d6hXgqa8qx2Mof-g
            r2 = r1
            r3 = r65
            r4 = r62
            r5 = r0
            r52 = r6
            r6 = r11
            r53 = r7
            r7 = r14
            r8 = r10
            r10 = r60
            r14 = 0
            r12 = r66
            r15 = r13
            r13 = r67
            r14 = r68
            r2.<init>(r4, r5, r6, r7, r8, r9, r10, r12, r13, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x0517
        L_0x0512:
            r15 = r13
            r52 = r17
            r53 = r34
        L_0x0517:
            r24 = r15
            r0 = r16
            r4 = r45
            r3 = r46
            r28 = r48
            r27 = r49
            r40 = r50
            r2 = r51
            r36 = r52
            r37 = r53
            r22 = 1
            r34 = 73
            r35 = 0
            goto L_0x085a
        L_0x0533:
            r51 = r2
            r46 = r3
            r45 = r4
            r48 = r9
            r50 = r11
            r21 = r12
            r9 = r15
            r52 = r17
            r49 = r27
            r53 = r34
            r25 = 0
            r15 = r10
            boolean r0 = r14.isVideo
            if (r0 == 0) goto L_0x085e
            if (r63 == 0) goto L_0x0551
            r0 = 0
            goto L_0x055c
        L_0x0551:
            org.telegram.messenger.VideoEditedInfo r0 = r14.videoEditedInfo
            if (r0 == 0) goto L_0x0556
            goto L_0x055c
        L_0x0556:
            java.lang.String r0 = r14.path
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r0)
        L_0x055c:
            if (r63 != 0) goto L_0x081d
            if (r0 != 0) goto L_0x057a
            java.lang.String r2 = r14.path
            boolean r2 = r2.endsWith(r7)
            if (r2 == 0) goto L_0x0569
            goto L_0x057a
        L_0x0569:
            r13 = r14
            r24 = r15
            r1 = r29
            r27 = r49
            r58 = r52
            r56 = r53
            r34 = 73
            r35 = 0
            goto L_0x082c
        L_0x057a:
            java.lang.String r10 = r14.path
            java.io.File r11 = new java.io.File
            r11.<init>(r10)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r10)
            long r3 = r11.length()
            r2.append(r3)
            r12 = r29
            r2.append(r12)
            long r3 = r11.lastModified()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            if (r0 == 0) goto L_0x05f8
            boolean r3 = r0.muted
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r7 = r3
            long r2 = r0.estimatedDuration
            r4.append(r2)
            r4.append(r12)
            long r2 = r0.startTime
            r4.append(r2)
            r4.append(r12)
            long r2 = r0.endTime
            r4.append(r2)
            boolean r2 = r0.muted
            if (r2 == 0) goto L_0x05c9
            java.lang.String r2 = "_m"
            goto L_0x05ca
        L_0x05c9:
            r2 = r8
        L_0x05ca:
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            int r3 = r0.resultWidth
            int r4 = r0.originalWidth
            if (r3 == r4) goto L_0x05eb
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r12)
            int r2 = r0.resultWidth
            r3.append(r2)
            java.lang.String r2 = r3.toString()
        L_0x05eb:
            long r3 = r0.startTime
            int r13 = (r3 > r25 ? 1 : (r3 == r25 ? 0 : -1))
            if (r13 < 0) goto L_0x05f2
            goto L_0x05f4
        L_0x05f2:
            r3 = r25
        L_0x05f4:
            r13 = r2
            r17 = r7
            goto L_0x05fd
        L_0x05f8:
            r13 = r2
            r3 = r25
            r17 = 0
        L_0x05fd:
            if (r15 != 0) goto L_0x0640
            int r2 = r14.ttl
            if (r2 != 0) goto L_0x0640
            org.telegram.messenger.MessagesStorage r2 = r62.getMessagesStorage()
            if (r15 != 0) goto L_0x060b
            r7 = 2
            goto L_0x060c
        L_0x060b:
            r7 = 5
        L_0x060c:
            java.lang.Object[] r2 = r2.getSentFile(r13, r7)
            if (r2 == 0) goto L_0x0640
            r7 = 0
            r22 = r2[r7]
            org.telegram.tgnet.TLRPC$TL_document r22 = (org.telegram.tgnet.TLRPC$TL_document) r22
            r27 = r5
            r5 = 1
            r2 = r2[r5]
            r29 = r2
            java.lang.String r29 = (java.lang.String) r29
            java.lang.String r2 = r14.path
            r34 = 0
            r35 = r2
            r2 = r15
            r39 = r3
            r3 = r22
            r4 = r35
            r35 = r10
            r10 = r27
            r27 = r12
            r12 = 1
            r5 = r34
            r55 = r6
            r6 = r39
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r7 = r29
            goto L_0x064c
        L_0x0640:
            r39 = r3
            r55 = r6
            r35 = r10
            r27 = r12
            r12 = 1
            r10 = r5
            r3 = 0
            r7 = 0
        L_0x064c:
            if (r3 != 0) goto L_0x0735
            java.lang.String r2 = r14.path
            r3 = r39
            android.graphics.Bitmap r2 = createVideoThumbnail(r2, r3)
            if (r2 != 0) goto L_0x065e
            java.lang.String r2 = r14.path
            android.graphics.Bitmap r2 = android.media.ThumbnailUtils.createVideoThumbnail(r2, r12)
        L_0x065e:
            if (r2 == 0) goto L_0x068b
            if (r15 != 0) goto L_0x0674
            int r3 = r14.ttl
            if (r3 == 0) goto L_0x0667
            goto L_0x0674
        L_0x0667:
            int r3 = r2.getWidth()
            int r4 = r2.getHeight()
            int r3 = java.lang.Math.max(r3, r4)
            goto L_0x0676
        L_0x0674:
            r3 = 90
        L_0x0676:
            float r4 = (float) r3
            r5 = 90
            if (r3 <= r5) goto L_0x067e
            r3 = 80
            goto L_0x0680
        L_0x067e:
            r3 = 55
        L_0x0680:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r4, r4, r3, r15)
            r4 = 0
            r6 = 0
            java.lang.String r5 = getKeyForPhotoSize(r3, r4, r12, r6)
            goto L_0x068e
        L_0x068b:
            r6 = 0
            r3 = 0
            r5 = 0
        L_0x068e:
            org.telegram.tgnet.TLRPC$TL_document r4 = new org.telegram.tgnet.TLRPC$TL_document
            r4.<init>()
            byte[] r12 = new byte[r6]
            r4.file_reference = r12
            if (r3 == 0) goto L_0x06a4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r4.thumbs
            r12.add(r3)
            int r3 = r4.flags
            r12 = 1
            r3 = r3 | r12
            r4.flags = r3
        L_0x06a4:
            java.lang.String r3 = "video/mp4"
            r4.mime_type = r3
            org.telegram.messenger.UserConfig r3 = r62.getUserConfig()
            r3.saveConfig(r6)
            if (r15 == 0) goto L_0x06c6
            r3 = 66
            r12 = r49
            if (r12 < r3) goto L_0x06bd
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r3.<init>()
            goto L_0x06c2
        L_0x06bd:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r3.<init>()
        L_0x06c2:
            r49 = r12
            r12 = 1
            goto L_0x06d0
        L_0x06c6:
            r12 = r49
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r3.<init>()
            r12 = 1
            r3.supports_streaming = r12
        L_0x06d0:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes
            r6.add(r3)
            if (r0 == 0) goto L_0x071d
            boolean r6 = r0.needConvert()
            if (r6 == 0) goto L_0x071d
            boolean r6 = r0.muted
            if (r6 == 0) goto L_0x06f0
            java.lang.String r6 = r14.path
            fillVideoAttribute(r6, r3, r0)
            int r6 = r3.w
            r0.originalWidth = r6
            int r6 = r3.h
            r0.originalHeight = r6
            r6 = r13
            goto L_0x06fa
        L_0x06f0:
            r6 = r13
            long r12 = r0.estimatedDuration
            r39 = 1000(0x3e8, double:4.94E-321)
            long r12 = r12 / r39
            int r11 = (int) r12
            r3.duration = r11
        L_0x06fa:
            int r11 = r0.rotationValue
            r12 = 90
            if (r11 == r12) goto L_0x070e
            r12 = 270(0x10e, float:3.78E-43)
            if (r11 != r12) goto L_0x0705
            goto L_0x070e
        L_0x0705:
            int r11 = r0.resultWidth
            r3.w = r11
            int r11 = r0.resultHeight
            r3.h = r11
            goto L_0x0716
        L_0x070e:
            int r11 = r0.resultHeight
            r3.w = r11
            int r11 = r0.resultWidth
            r3.h = r11
        L_0x0716:
            long r11 = r0.estimatedSize
            int r3 = (int) r11
            r4.size = r3
            r12 = 0
            goto L_0x0731
        L_0x071d:
            r6 = r13
            boolean r12 = r11.exists()
            if (r12 == 0) goto L_0x072b
            long r11 = r11.length()
            int r12 = (int) r11
            r4.size = r12
        L_0x072b:
            java.lang.String r11 = r14.path
            r12 = 0
            fillVideoAttribute(r11, r3, r12)
        L_0x0731:
            r3 = r2
            r11 = r4
            r4 = r5
            goto L_0x073a
        L_0x0735:
            r6 = r13
            r12 = 0
            r11 = r3
            r3 = r12
            r4 = r3
        L_0x073a:
            if (r0 == 0) goto L_0x0765
            boolean r2 = r0.muted
            if (r2 == 0) goto L_0x0765
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r11.attributes
            int r2 = r2.size()
            r5 = 0
        L_0x0747:
            if (r5 >= r2) goto L_0x0758
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r11.attributes
            java.lang.Object r13 = r13.get(r5)
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            if (r13 == 0) goto L_0x0755
            r2 = 1
            goto L_0x0759
        L_0x0755:
            int r5 = r5 + 1
            goto L_0x0747
        L_0x0758:
            r2 = 0
        L_0x0759:
            if (r2 != 0) goto L_0x0765
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r11.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r5.<init>()
            r2.add(r5)
        L_0x0765:
            if (r0 == 0) goto L_0x0799
            boolean r2 = r0.needConvert()
            if (r2 == 0) goto L_0x0799
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
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r38)
            r5.<init>(r13, r2)
            org.telegram.messenger.SharedConfig.saveConfig()
            java.lang.String r2 = r5.getAbsolutePath()
            r35 = r2
        L_0x0799:
            java.util.HashMap r13 = new java.util.HashMap
            r13.<init>()
            if (r6 == 0) goto L_0x07a4
            r2 = r6
            r13.put(r1, r2)
        L_0x07a4:
            if (r7 == 0) goto L_0x07ab
            java.lang.String r1 = "parentObject"
            r13.put(r1, r7)
        L_0x07ab:
            if (r17 != 0) goto L_0x07da
            if (r64 == 0) goto L_0x07da
            int r1 = r16 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r8)
            r5 = r53
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r8 = r55
            r13.put(r8, r2)
            r2 = 10
            if (r1 == r2) goto L_0x07d2
            int r2 = r21 + -1
            r8 = r52
            if (r8 != r2) goto L_0x07e0
            goto L_0x07d4
        L_0x07d2:
            r8 = r52
        L_0x07d4:
            r13.put(r10, r9)
            r32 = r25
            goto L_0x07e0
        L_0x07da:
            r8 = r52
            r5 = r53
            r1 = r16
        L_0x07e0:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$HV6ayuYlgpOaqnUKa4BtW-YtCVc r22 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$HV6ayuYlgpOaqnUKa4BtW-YtCVc
            r2 = r22
            r9 = r5
            r5 = r65
            r28 = r1
            r1 = 0
            r6 = r62
            r29 = r7
            r7 = r0
            r16 = r8
            r8 = r11
            r10 = r9
            r9 = r35
            r56 = r10
            r10 = r13
            r11 = r29
            r17 = r12
            r1 = r27
            r27 = r49
            r12 = r60
            r29 = r14
            r14 = r66
            r24 = r15
            r58 = r16
            r35 = r17
            r34 = 73
            r15 = r29
            r16 = r67
            r17 = r68
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r22)
            r16 = r28
            goto L_0x0846
        L_0x081d:
            r24 = r15
            r1 = r29
            r27 = r49
            r58 = r52
            r56 = r53
            r34 = 73
            r35 = 0
            r13 = r14
        L_0x082c:
            java.lang.String r4 = r13.path
            r5 = 0
            r6 = 0
            java.lang.String r10 = r13.caption
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r11 = r13.entities
            r2 = r62
            r3 = r4
            r7 = r60
            r9 = r66
            r12 = r65
            r13 = r63
            r14 = r67
            r15 = r68
            prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15)
        L_0x0846:
            r29 = r1
            r0 = r16
            r4 = r45
            r3 = r46
            r28 = r48
            r40 = r50
            r2 = r51
        L_0x0854:
            r37 = r56
            r36 = r58
            r22 = 1
        L_0x085a:
            r39 = 0
            goto L_0x0b7d
        L_0x085e:
            r12 = r1
            r10 = r5
            r11 = r6
            r13 = r14
            r24 = r15
            r1 = r29
            r27 = r49
            r58 = r52
            r56 = r53
            r34 = 73
            r35 = 0
            java.lang.String r0 = r13.path
            if (r0 != 0) goto L_0x0883
            android.net.Uri r2 = r13.uri
            if (r2 == 0) goto L_0x0883
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r2)
            android.net.Uri r2 = r13.uri
            java.lang.String r2 = r2.toString()
            goto L_0x0884
        L_0x0883:
            r2 = r0
        L_0x0884:
            java.lang.String r3 = "gif"
            java.lang.String r4 = "webp"
            if (r63 != 0) goto L_0x08ed
            java.lang.String r5 = r13.path
            android.net.Uri r6 = r13.uri
            boolean r5 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r5, r6)
            if (r5 == 0) goto L_0x0896
            goto L_0x08ed
        L_0x0896:
            if (r0 == 0) goto L_0x08b2
            r15 = r50
            boolean r5 = r0.endsWith(r15)
            r14 = r30
            if (r5 != 0) goto L_0x08a8
            boolean r5 = r0.endsWith(r14)
            if (r5 == 0) goto L_0x08b6
        L_0x08a8:
            boolean r5 = r0.endsWith(r15)
            if (r5 == 0) goto L_0x08af
            goto L_0x08fc
        L_0x08af:
            r23 = r4
            goto L_0x0901
        L_0x08b2:
            r14 = r30
            r15 = r50
        L_0x08b6:
            if (r0 != 0) goto L_0x08ea
            android.net.Uri r5 = r13.uri
            if (r5 == 0) goto L_0x08ea
            boolean r5 = org.telegram.messenger.MediaController.isGif(r5)
            if (r5 == 0) goto L_0x08d2
            android.net.Uri r0 = r13.uri
            java.lang.String r2 = r0.toString()
            android.net.Uri r0 = r13.uri
            java.lang.String r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3)
            r6 = r0
            r23 = r3
            goto L_0x0902
        L_0x08d2:
            android.net.Uri r3 = r13.uri
            boolean r3 = org.telegram.messenger.MediaController.isWebp(r3)
            if (r3 == 0) goto L_0x08ea
            android.net.Uri r0 = r13.uri
            java.lang.String r2 = r0.toString()
            android.net.Uri r0 = r13.uri
            java.lang.String r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r4)
            r6 = r0
            r23 = r4
            goto L_0x0902
        L_0x08ea:
            r6 = r0
            r0 = 0
            goto L_0x0903
        L_0x08ed:
            r14 = r30
            r15 = r50
            if (r0 == 0) goto L_0x08ff
            java.io.File r3 = new java.io.File
            r3.<init>(r0)
            java.lang.String r3 = org.telegram.messenger.FileLoader.getFileExtension(r3)
        L_0x08fc:
            r23 = r3
            goto L_0x0901
        L_0x08ff:
            r23 = r8
        L_0x0901:
            r6 = r0
        L_0x0902:
            r0 = 1
        L_0x0903:
            if (r0 == 0) goto L_0x0946
            r7 = r45
            if (r7 != 0) goto L_0x0920
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r31 = new java.util.ArrayList
            r31.<init>()
            r5 = r31
            goto L_0x0927
        L_0x0920:
            r4 = r7
            r5 = r31
            r3 = r46
            r0 = r51
        L_0x0927:
            r4.add(r6)
            r3.add(r2)
            java.lang.String r2 = r13.caption
            r0.add(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r13.entities
            r5.add(r2)
            r2 = r0
            r29 = r1
            r31 = r5
            r30 = r14
            r40 = r15
            r0 = r16
            r28 = r48
            goto L_0x0854
        L_0x0946:
            r7 = r45
            if (r6 == 0) goto L_0x096e
            java.io.File r0 = new java.io.File
            r0.<init>(r6)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            long r4 = r0.length()
            r3.append(r4)
            r3.append(r1)
            long r4 = r0.lastModified()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r5 = r3
            goto L_0x0970
        L_0x096e:
            r5 = r35
        L_0x0970:
            r4 = r48
            if (r4 == 0) goto L_0x099e
            java.lang.Object r0 = r4.get(r13)
            r2 = r0
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
            if (r0 != 0) goto L_0x098f
            java.util.concurrent.CountDownLatch r0 = r2.sync     // Catch:{ Exception -> 0x0987 }
            r0.await()     // Catch:{ Exception -> 0x0987 }
            goto L_0x098b
        L_0x0987:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x098b:
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
        L_0x098f:
            r29 = r1
            r28 = r4
            r1 = r7
            r30 = r14
            r50 = r15
            r7 = r0
            r14 = r5
            r15 = r6
            r6 = r3
            goto L_0x0a45
        L_0x099e:
            if (r24 != 0) goto L_0x0a13
            int r0 = r13.ttl
            if (r0 != 0) goto L_0x0a13
            org.telegram.messenger.MessagesStorage r0 = r62.getMessagesStorage()
            if (r24 != 0) goto L_0x09ac
            r2 = 0
            goto L_0x09ad
        L_0x09ac:
            r2 = 3
        L_0x09ad:
            java.lang.Object[] r0 = r0.getSentFile(r5, r2)
            if (r0 == 0) goto L_0x09be
            r2 = 0
            r3 = r0[r2]
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3
            r2 = 1
            r0 = r0[r2]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x09c2
        L_0x09be:
            r2 = 1
            r0 = r35
            r3 = r0
        L_0x09c2:
            if (r3 != 0) goto L_0x09ef
            android.net.Uri r2 = r13.uri
            if (r2 == 0) goto L_0x09ef
            org.telegram.messenger.MessagesStorage r2 = r62.getMessagesStorage()
            r17 = r0
            android.net.Uri r0 = r13.uri
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r0)
            r29 = r1
            if (r24 != 0) goto L_0x09da
            r1 = 0
            goto L_0x09db
        L_0x09da:
            r1 = 3
        L_0x09db:
            java.lang.Object[] r0 = r2.getSentFile(r0, r1)
            if (r0 == 0) goto L_0x09f3
            r1 = 0
            r2 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_photo r2 = (org.telegram.tgnet.TLRPC$TL_photo) r2
            r1 = 1
            r0 = r0[r1]
            java.lang.String r0 = (java.lang.String) r0
            r17 = r0
            r0 = r2
            goto L_0x09f5
        L_0x09ef:
            r17 = r0
            r29 = r1
        L_0x09f3:
            r1 = 1
            r0 = r3
        L_0x09f5:
            java.lang.String r3 = r13.path
            android.net.Uri r2 = r13.uri
            r39 = 0
            r1 = r2
            r2 = r24
            r22 = r3
            r3 = r0
            r28 = r4
            r4 = r22
            r30 = r14
            r14 = r5
            r5 = r1
            r1 = r7
            r50 = r15
            r15 = r6
            r6 = r39
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            goto L_0x0a22
        L_0x0a13:
            r29 = r1
            r28 = r4
            r1 = r7
            r30 = r14
            r50 = r15
            r14 = r5
            r15 = r6
            r3 = r35
            r17 = r3
        L_0x0a22:
            if (r3 != 0) goto L_0x0a42
            org.telegram.messenger.SendMessagesHelper r0 = r62.getSendMessagesHelper()
            java.lang.String r2 = r13.path
            android.net.Uri r3 = r13.uri
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r3)
            if (r24 == 0) goto L_0x0a40
            boolean r2 = r13.canDeleteAfter
            if (r2 == 0) goto L_0x0a40
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r13.path
            r2.<init>(r3)
            r2.delete()
        L_0x0a40:
            r7 = r0
            goto L_0x0a43
        L_0x0a42:
            r7 = r3
        L_0x0a43:
            r6 = r17
        L_0x0a45:
            if (r7 == 0) goto L_0x0b3b
            java.util.HashMap r15 = new java.util.HashMap
            r15.<init>()
            r5 = 1
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r5]
            java.lang.String[] r4 = new java.lang.String[r5]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r0 = r13.masks
            if (r0 == 0) goto L_0x0a5d
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0a5d
            r0 = 1
            goto L_0x0a5e
        L_0x0a5d:
            r0 = 0
        L_0x0a5e:
            r7.has_stickers = r0
            if (r0 == 0) goto L_0x0aa1
            org.telegram.tgnet.SerializedData r0 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r13.masks
            int r2 = r2.size()
            int r2 = r2 * 20
            int r2 = r2 + 4
            r0.<init>((int) r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r13.masks
            int r2 = r2.size()
            r0.writeInt32(r2)
            r2 = 0
        L_0x0a7b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r13.masks
            int r5 = r5.size()
            if (r2 >= r5) goto L_0x0a91
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r13.masks
            java.lang.Object r5 = r5.get(r2)
            org.telegram.tgnet.TLRPC$InputDocument r5 = (org.telegram.tgnet.TLRPC$InputDocument) r5
            r5.serializeToStream(r0)
            int r2 = r2 + 1
            goto L_0x0a7b
        L_0x0a91:
            byte[] r2 = r0.toByteArray()
            java.lang.String r2 = org.telegram.messenger.Utilities.bytesToHex(r2)
            java.lang.String r5 = "masks"
            r15.put(r5, r2)
            r0.cleanup()
        L_0x0aa1:
            if (r14 == 0) goto L_0x0aa6
            r15.put(r12, r14)
        L_0x0aa6:
            if (r6 == 0) goto L_0x0aad
            java.lang.String r0 = "parentObject"
            r15.put(r0, r6)
        L_0x0aad:
            if (r64 == 0) goto L_0x0abc
            int r0 = r59.size()     // Catch:{ Exception -> 0x0ab9 }
            r5 = 1
            if (r0 != r5) goto L_0x0ab7
            goto L_0x0abd
        L_0x0ab7:
            r14 = 0
            goto L_0x0ad8
        L_0x0ab9:
            r0 = move-exception
            r5 = 1
            goto L_0x0ad4
        L_0x0abc:
            r5 = 1
        L_0x0abd:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r7.sizes     // Catch:{ Exception -> 0x0ad3 }
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()     // Catch:{ Exception -> 0x0ad3 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2)     // Catch:{ Exception -> 0x0ad3 }
            if (r0 == 0) goto L_0x0ab7
            r14 = 0
            java.lang.String r0 = getKeyForPhotoSize(r0, r3, r14, r14)     // Catch:{ Exception -> 0x0ad1 }
            r4[r14] = r0     // Catch:{ Exception -> 0x0ad1 }
            goto L_0x0ad8
        L_0x0ad1:
            r0 = move-exception
            goto L_0x0ad5
        L_0x0ad3:
            r0 = move-exception
        L_0x0ad4:
            r14 = 0
        L_0x0ad5:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0ad8:
            if (r64 == 0) goto L_0x0b0a
            int r0 = r16 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r8)
            r17 = r13
            r13 = r56
            r2.append(r13)
            java.lang.String r2 = r2.toString()
            r15.put(r11, r2)
            r2 = 10
            if (r0 == r2) goto L_0x0b00
            int r12 = r21 + -1
            r11 = r58
            if (r11 != r12) goto L_0x0afd
            goto L_0x0b02
        L_0x0afd:
            r16 = r0
            goto L_0x0b10
        L_0x0b00:
            r11 = r58
        L_0x0b02:
            r15.put(r10, r9)
            r16 = r0
            r32 = r25
            goto L_0x0b10
        L_0x0b0a:
            r17 = r13
            r13 = r56
            r11 = r58
        L_0x0b10:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$UdYxeFJJPkyzGq2o6oz82-zZGls r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$UdYxeFJJPkyzGq2o6oz82-zZGls
            r2 = r0
            r22 = 1
            r5 = r65
            r9 = r6
            r6 = r62
            r8 = r15
            r36 = r11
            r10 = r60
            r12 = r66
            r37 = r13
            r13 = r17
            r39 = 0
            r14 = r67
            r40 = r50
            r15 = r68
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            r4 = r1
            r0 = r16
            r3 = r46
            r2 = r51
            goto L_0x0b7d
        L_0x0b3b:
            r17 = r13
            r40 = r50
            r37 = r56
            r36 = r58
            r22 = 1
            r39 = 0
            if (r1 != 0) goto L_0x0b60
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r31 = new java.util.ArrayList
            r31.<init>()
            r0 = r31
            goto L_0x0b67
        L_0x0b60:
            r4 = r1
            r0 = r31
            r3 = r46
            r2 = r51
        L_0x0b67:
            r4.add(r15)
            r3.add(r14)
            r1 = r17
            java.lang.String r5 = r1.caption
            r2.add(r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
            r0.add(r1)
            r31 = r0
            r0 = r16
        L_0x0b7d:
            int r5 = r36 + 1
            r1 = r59
            r13 = r62
            r12 = r21
            r10 = r24
            r8 = r27
            r9 = r28
            r14 = r35
            r16 = r37
            r11 = r40
            r15 = 73
            goto L_0x0197
        L_0x0b95:
            r51 = r2
            r46 = r3
            r1 = r4
            r6 = r32
            r25 = 0
            r39 = 0
            int r0 = (r6 > r25 ? 1 : (r6 == r25 ? 0 : -1))
            if (r0 == 0) goto L_0x0bb1
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$pKfT1w1I30bZ85vW1f-pzxx_nIk r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$pKfT1w1I30bZ85vW1f-pzxx_nIk
            r15 = r62
            r14 = r68
            r0.<init>(r6, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0bb5
        L_0x0bb1:
            r15 = r62
            r14 = r68
        L_0x0bb5:
            if (r69 == 0) goto L_0x0bba
            r69.releasePermission()
        L_0x0bba:
            if (r1 == 0) goto L_0x0c0c
            boolean r0 = r1.isEmpty()
            if (r0 != 0) goto L_0x0c0c
            r0 = 0
        L_0x0bc3:
            int r2 = r1.size()
            if (r0 >= r2) goto L_0x0c0c
            java.lang.Object r2 = r1.get(r0)
            java.lang.String r2 = (java.lang.String) r2
            r13 = r46
            java.lang.Object r3 = r13.get(r0)
            java.lang.String r3 = (java.lang.String) r3
            r4 = 0
            r12 = r51
            java.lang.Object r5 = r12.get(r0)
            r9 = r5
            java.lang.CharSequence r9 = (java.lang.CharSequence) r9
            r11 = r31
            java.lang.Object r5 = r11.get(r0)
            r10 = r5
            java.util.ArrayList r10 = (java.util.ArrayList) r10
            r16 = r1
            r1 = r62
            r5 = r23
            r6 = r60
            r8 = r66
            r11 = r65
            r17 = r12
            r12 = r63
            r20 = r13
            r13 = r67
            r14 = r68
            prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14)
            int r0 = r0 + 1
            r1 = r16
            r51 = r17
            r46 = r20
            goto L_0x0bc3
        L_0x0c0c:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0c2a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "total send time = "
            r0.append(r1)
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r18
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0c2a:
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

    static /* synthetic */ void lambda$null$67(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, str, hashMap, false, str2);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str2);
    }

    static /* synthetic */ void lambda$null$68(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2, boolean z2, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        String str2 = null;
        if (messageObject != null) {
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            if (z) {
                str2 = sendingMediaInfo2.searchImage.imageUrl;
            }
            sendMessagesHelper.editMessageMedia(messageObject, tLRPC$TL_photo, (VideoEditedInfo) null, (TLRPC$TL_document) null, str2, hashMap, false, str);
            return;
        }
        SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
        if (z) {
            str2 = sendingMediaInfo2.searchImage.imageUrl;
        }
        sendMessagesHelper2.sendMessage(tLRPC$TL_photo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z2, i, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$69(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        Bitmap bitmap2 = bitmap;
        String str4 = str;
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmap2 == null || str4 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str4);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC$TL_photo) null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, sendingMediaInfo2.ttl, str3);
    }

    static /* synthetic */ void lambda$null$70(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, tLRPC$TL_photo, (VideoEditedInfo) null, (TLRPC$TL_document) null, (String) null, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_photo, (String) null, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, sendingMediaInfo2.ttl, str);
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
            TLRPC$TL_messages_messages tLRPC$TL_messages_messages = new TLRPC$TL_messages_messages();
            tLRPC$TL_messages_messages.messages.add(messageObject.messageOwner);
            accountInstance.getMessagesStorage().putMessages((TLRPC$messages_Messages) tLRPC$TL_messages_messages, delayedMessage.peer, -2, 0, false, i != 0);
            sendMessagesHelper.sendReadyToSendGroup(delayedMessage, true, true);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x0080 A[SYNTHETIC, Splitter:B:39:0x0080] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x008b A[SYNTHETIC, Splitter:B:45:0x008b] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00c1 A[SYNTHETIC, Splitter:B:52:0x00c1] */
    /* JADX WARNING: Removed duplicated region for block: B:58:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void fillVideoAttribute(java.lang.String r5, org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r6, org.telegram.messenger.VideoEditedInfo r7) {
        /*
            r0 = 1148846080(0x447a0000, float:1000.0)
            r1 = 0
            android.media.MediaMetadataRetriever r2 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x007a }
            r2.<init>()     // Catch:{ Exception -> 0x007a }
            r2.setDataSource(r5)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            r1 = 18
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            if (r1 == 0) goto L_0x0019
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            r6.w = r1     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
        L_0x0019:
            r1 = 19
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            if (r1 == 0) goto L_0x0027
            int r1 = java.lang.Integer.parseInt(r1)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            r6.h = r1     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
        L_0x0027:
            r1 = 9
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            if (r1 == 0) goto L_0x003d
            long r3 = java.lang.Long.parseLong(r1)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            float r1 = (float) r3     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            float r1 = r1 / r0
            double r3 = (double) r1     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            double r3 = java.lang.Math.ceil(r3)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            int r1 = (int) r3     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            r6.duration = r1     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
        L_0x003d:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            r3 = 17
            if (r1 < r3) goto L_0x0068
            r1 = 24
            java.lang.String r1 = r2.extractMetadata(r1)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            if (r1 == 0) goto L_0x0068
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt(r1)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            int r1 = r1.intValue()     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            if (r7 == 0) goto L_0x0058
            r7.rotationValue = r1     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            goto L_0x0068
        L_0x0058:
            r7 = 90
            if (r1 == r7) goto L_0x0060
            r7 = 270(0x10e, float:3.78E-43)
            if (r1 != r7) goto L_0x0068
        L_0x0060:
            int r7 = r6.w     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            int r1 = r6.h     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            r6.w = r1     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
            r6.h = r7     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
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
            r1 = r2
            goto L_0x00bf
        L_0x0075:
            r7 = move-exception
            r1 = r2
            goto L_0x007b
        L_0x0078:
            r5 = move-exception
            goto L_0x00bf
        L_0x007a:
            r7 = move-exception
        L_0x007b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ all -> 0x0078 }
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
            if (r1 == 0) goto L_0x00c9
            r1.release()     // Catch:{ Exception -> 0x00c5 }
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
            int round = Math.round((((float) videoEditedInfo.originalHeight) * f3) / 2.0f) * 2;
            videoEditedInfo.resultHeight = round;
            i = MediaController.makeVideoBitrate(videoEditedInfo.originalHeight, videoEditedInfo.originalWidth, videoBitrate, round, videoEditedInfo.resultWidth);
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
            long j3 = (long) ((int) (j2 + j));
            videoEditedInfo.estimatedSize = j3;
            videoEditedInfo.estimatedSize = j3 + ((j3 / 32768) * 16);
        }
        if (videoEditedInfo.estimatedSize == 0) {
            videoEditedInfo.estimatedSize = 1;
        }
        return videoEditedInfo;
    }

    public static void prepareSendingVideo(AccountInstance accountInstance, String str, long j, long j2, int i, int i2, VideoEditedInfo videoEditedInfo, long j3, MessageObject messageObject, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, int i3, MessageObject messageObject2, boolean z, int i4) {
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

    /* JADX WARNING: Removed duplicated region for block: B:103:0x02c1  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02ff  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0314  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x034c  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0354  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0365  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0128  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0263  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0280  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x02aa  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingVideo$74(org.telegram.messenger.VideoEditedInfo r30, java.lang.String r31, long r32, long r34, int r36, org.telegram.messenger.AccountInstance r37, int r38, int r39, long r40, java.lang.CharSequence r42, org.telegram.messenger.MessageObject r43, org.telegram.messenger.MessageObject r44, java.util.ArrayList r45, boolean r46, int r47) {
        /*
            r6 = r31
            r10 = r32
            r7 = r34
            r9 = r38
            r12 = r39
            if (r30 == 0) goto L_0x000f
            r13 = r30
            goto L_0x0014
        L_0x000f:
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r31)
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
            r0 = r37
            r1 = r31
            r2 = r31
            r5 = r32
            r7 = r44
            r8 = r42
            r9 = r45
            r10 = r43
            r11 = r12
            r12 = r46
            r13 = r47
            prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x038d
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
            if (r13 == 0) goto L_0x00cc
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
            java.lang.String r0 = "_m"
            goto L_0x009f
        L_0x009d:
            r0 = r19
        L_0x009f:
            r1.append(r0)
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
            if (r1 < 0) goto L_0x00ce
            r17 = r14
            goto L_0x00ce
        L_0x00cc:
            r19 = r14
        L_0x00ce:
            r14 = r0
            r0 = r17
            if (r4 != 0) goto L_0x011a
            if (r36 != 0) goto L_0x011a
            org.telegram.messenger.MessagesStorage r15 = r37.getMessagesStorage()
            if (r4 != 0) goto L_0x00df
            r21 = r0
            r0 = 2
            goto L_0x00e4
        L_0x00df:
            r20 = 5
            r21 = r0
            r0 = 5
        L_0x00e4:
            java.lang.Object[] r0 = r15.getSentFile(r14, r0)
            if (r0 == 0) goto L_0x0111
            r1 = 0
            r15 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_document r15 = (org.telegram.tgnet.TLRPC$TL_document) r15
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
            r2 = r31
            r22 = r3
            r3 = r23
            r9 = r5
            r23 = r14
            r14 = r4
            r4 = r24
            ensureMediaThumbExists(r0, r1, r2, r3, r4)
            r5 = r20
            goto L_0x0126
        L_0x0111:
            r15 = r2
            r9 = r5
            r23 = r14
            r24 = r21
            r22 = r3
            goto L_0x0122
        L_0x011a:
            r24 = r0
            r15 = r2
            r22 = r3
            r9 = r5
            r23 = r14
        L_0x0122:
            r14 = r4
            r5 = 0
            r21 = 0
        L_0x0126:
            if (r21 != 0) goto L_0x0314
            r0 = r24
            android.graphics.Bitmap r0 = createVideoThumbnail(r6, r0)
            if (r0 != 0) goto L_0x0135
            r1 = 1
            android.graphics.Bitmap r0 = android.media.ThumbnailUtils.createVideoThumbnail(r6, r1)
        L_0x0135:
            r1 = 90
            if (r14 != 0) goto L_0x013f
            if (r36 == 0) goto L_0x013c
            goto L_0x013f
        L_0x013c:
            r2 = 320(0x140, float:4.48E-43)
            goto L_0x0141
        L_0x013f:
            r2 = 90
        L_0x0141:
            float r3 = (float) r2
            if (r2 <= r1) goto L_0x0147
            r2 = 80
            goto L_0x0149
        L_0x0147:
            r2 = 55
        L_0x0149:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r14)
            if (r0 == 0) goto L_0x025a
            if (r2 == 0) goto L_0x025a
            if (r9 == 0) goto L_0x0257
            r3 = 21
            if (r14 == 0) goto L_0x01f7
            r4 = 1
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createScaledBitmap(r0, r1, r1, r4)
            r25 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x0165
            r26 = 0
            goto L_0x0167
        L_0x0165:
            r26 = 1
        L_0x0167:
            int r27 = r0.getWidth()
            int r28 = r0.getHeight()
            int r29 = r0.getRowBytes()
            r24 = r0
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            r25 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x0181
            r26 = 0
            goto L_0x0183
        L_0x0181:
            r26 = 1
        L_0x0183:
            int r27 = r0.getWidth()
            int r28 = r0.getHeight()
            int r29 = r0.getRowBytes()
            r24 = r0
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            r25 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x019d
            r26 = 0
            goto L_0x019f
        L_0x019d:
            r26 = 1
        L_0x019f:
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
            java.lang.String r2 = java.lang.String.format(r2, r3)
            goto L_0x025c
        L_0x01f7:
            r1 = r2
            r25 = 3
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 >= r3) goto L_0x0201
            r26 = 0
            goto L_0x0203
        L_0x0201:
            r26 = 1
        L_0x0203:
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
            java.lang.String r2 = java.lang.String.format(r2, r3)
            goto L_0x025c
        L_0x0257:
            r1 = r2
            r0 = 0
            goto L_0x025b
        L_0x025a:
            r1 = r2
        L_0x025b:
            r2 = 0
        L_0x025c:
            org.telegram.tgnet.TLRPC$TL_document r3 = new org.telegram.tgnet.TLRPC$TL_document
            r3.<init>()
            if (r1 == 0) goto L_0x026e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r3.thumbs
            r4.add(r1)
            int r1 = r3.flags
            r4 = 1
            r1 = r1 | r4
            r3.flags = r1
        L_0x026e:
            r1 = 0
            byte[] r4 = new byte[r1]
            r3.file_reference = r4
            java.lang.String r4 = "video/mp4"
            r3.mime_type = r4
            org.telegram.messenger.UserConfig r4 = r37.getUserConfig()
            r4.saveConfig(r1)
            if (r14 == 0) goto L_0x02aa
            r1 = 32
            long r14 = r10 >> r1
            int r1 = (int) r14
            org.telegram.messenger.MessagesController r4 = r37.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r4.getEncryptedChat(r1)
            if (r1 != 0) goto L_0x0294
            return
        L_0x0294:
            int r1 = r1.layer
            int r1 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r1)
            r4 = 66
            if (r1 < r4) goto L_0x02a4
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            goto L_0x02b2
        L_0x02a4:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r1.<init>()
            goto L_0x02b2
        L_0x02aa:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            r4 = 1
            r1.supports_streaming = r4
        L_0x02b2:
            r1.round_message = r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r1)
            if (r13 == 0) goto L_0x02ff
            boolean r4 = r13.needConvert()
            if (r4 == 0) goto L_0x02ff
            boolean r4 = r13.muted
            if (r4 == 0) goto L_0x02db
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r7.<init>()
            r4.add(r7)
            fillVideoAttribute(r6, r1, r13)
            int r4 = r1.w
            r13.originalWidth = r4
            int r4 = r1.h
            r13.originalHeight = r4
            goto L_0x02e1
        L_0x02db:
            r14 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 / r14
            int r4 = (int) r7
            r1.duration = r4
        L_0x02e1:
            int r4 = r13.rotationValue
            r7 = 90
            if (r4 == r7) goto L_0x02f3
            r7 = 270(0x10e, float:3.78E-43)
            if (r4 != r7) goto L_0x02ec
            goto L_0x02f3
        L_0x02ec:
            r1.w = r12
            r4 = r38
            r1.h = r4
            goto L_0x02f9
        L_0x02f3:
            r4 = r38
            r1.w = r4
            r1.h = r12
        L_0x02f9:
            r7 = r40
            int r1 = (int) r7
            r3.size = r1
            goto L_0x0310
        L_0x02ff:
            boolean r4 = r22.exists()
            if (r4 == 0) goto L_0x030c
            long r7 = r22.length()
            int r4 = (int) r7
            r3.size = r4
        L_0x030c:
            r4 = 0
            fillVideoAttribute(r6, r1, r4)
        L_0x0310:
            r1 = r0
            r21 = r3
            goto L_0x0317
        L_0x0314:
            r4 = 0
            r1 = r4
            r2 = r1
        L_0x0317:
            if (r13 == 0) goto L_0x034c
            boolean r0 = r13.needConvert()
            if (r0 == 0) goto L_0x034c
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
            goto L_0x034d
        L_0x034c:
            r7 = r6
        L_0x034d:
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            if (r42 == 0) goto L_0x035a
            java.lang.String r0 = r42.toString()
            r19 = r0
        L_0x035a:
            if (r23 == 0) goto L_0x0363
            java.lang.String r0 = "originalPath"
            r3 = r23
            r8.put(r0, r3)
        L_0x0363:
            if (r5 == 0) goto L_0x036a
            java.lang.String r0 = "parentObject"
            r8.put(r0, r5)
        L_0x036a:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$sEG1r9eCF9TKK2nk0jbM2n33M88 r18 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$sEG1r9eCF9TKK2nk0jbM2n33M88
            r0 = r18
            r3 = r43
            r4 = r37
            r20 = r5
            r5 = r13
            r6 = r21
            r9 = r20
            r10 = r32
            r12 = r44
            r13 = r19
            r14 = r45
            r15 = r46
            r16 = r47
            r17 = r36
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16, r17)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r18)
        L_0x038d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$74(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, long, int, org.telegram.messenger.AccountInstance, int, int, long, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$73(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, String str4, ArrayList arrayList, boolean z, int i, int i2) {
        Bitmap bitmap2 = bitmap;
        String str5 = str;
        if (!(bitmap2 == null || str5 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str5);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC$TL_photo) null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, str4, arrayList, (TLRPC$ReplyMarkup) null, hashMap, z, i, i2, str3);
        }
    }
}
