package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
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
import org.telegram.tgnet.TLRPC$DecryptedMessage;
import org.telegram.tgnet.TLRPC$DecryptedMessageAction;
import org.telegram.tgnet.TLRPC$DecryptedMessageMedia;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$InputDocument;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$InputMedia;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_account_password;
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
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55;
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
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
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
import org.telegram.tgnet.TLRPC$TL_messageReplies;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendReaction;
import org.telegram.tgnet.TLRPC$TL_messages_sendScreenshotNotification;
import org.telegram.tgnet.TLRPC$TL_messages_sendVote;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_uploadMedia;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoCachedSize;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC$TL_photoSizeProgressive;
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
import org.telegram.ui.TwoStepVerificationActivity;

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
        public String paintPath;
        public HashMap<String, String> params;
        public String path;
        public MediaController.SearchImage searchImage;
        public String thumbPath;
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
        public int topMessageId;
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
        } else {
            MessageObject messageObject6 = null;
            if (i4 == NotificationCenter.fileNewChunkAvailable) {
                MessageObject messageObject7 = objArr[0];
                if (messageObject7.getId() != 0) {
                    String str7 = objArr[1];
                    long longValue = objArr[2].longValue();
                    long longValue2 = objArr[3].longValue();
                    getFileLoader().checkUploadNewDataAvailable(str7, ((int) messageObject7.getDialogId()) == 0, longValue, longValue2);
                    if (longValue2 != 0) {
                        stopVideoService(messageObject7.messageOwner.attachPath);
                        ArrayList arrayList7 = this.delayedMessages.get(messageObject7.messageOwner.attachPath);
                        if (arrayList7 != null) {
                            for (int i9 = 0; i9 < arrayList7.size(); i9++) {
                                DelayedMessage delayedMessage8 = (DelayedMessage) arrayList7.get(i9);
                                if (delayedMessage8.type == 4) {
                                    int i10 = 0;
                                    while (true) {
                                        if (i10 >= delayedMessage8.messageObjects.size()) {
                                            break;
                                        }
                                        MessageObject messageObject8 = delayedMessage8.messageObjects.get(i10);
                                        if (messageObject8 == messageObject7) {
                                            delayedMessage8.obj.shouldRemoveVideoEditedInfo = true;
                                            messageObject8.messageOwner.params.remove("ve");
                                            messageObject8.messageOwner.media.document.size = (int) longValue2;
                                            ArrayList arrayList8 = new ArrayList();
                                            arrayList8.add(messageObject8.messageOwner);
                                            getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList8, false, true, false, 0, messageObject8.scheduled);
                                            break;
                                        }
                                        i10++;
                                    }
                                } else {
                                    MessageObject messageObject9 = delayedMessage8.obj;
                                    if (messageObject9 == messageObject7) {
                                        messageObject9.shouldRemoveVideoEditedInfo = true;
                                        messageObject9.messageOwner.params.remove("ve");
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
                MessageObject messageObject10 = objArr[0];
                if (messageObject10.getId() != 0) {
                    String str8 = objArr[1];
                    stopVideoService(messageObject10.messageOwner.attachPath);
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
                                    } else if (delayedMessage9.messageObjects.get(i12) == messageObject10) {
                                        delayedMessage9.markAsError();
                                        arrayList10.remove(i11);
                                        break;
                                    } else {
                                        i12++;
                                    }
                                }
                                i11++;
                            } else if (delayedMessage9.obj == messageObject10) {
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
                    int i13 = 0;
                    while (i13 < arrayList11.size()) {
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
                                messageObject = messageObject6;
                            }
                            messageObject = messageObject2;
                            c = 1;
                        }
                        if (c == 0) {
                            File file = new File(FileLoader.getDirectory(4), Utilities.MD5(str9) + "." + ImageLoader.getHttpUrlExtension(str9, "file"));
                            DispatchQueue dispatchQueue = Utilities.globalQueue;
                            $$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv97eY r7 = r0;
                            $$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv97eY r0 = new Runnable(file, messageObject, delayedMessage10, str9) {
                                public final /* synthetic */ File f$1;
                                public final /* synthetic */ MessageObject f$2;
                                public final /* synthetic */ SendMessagesHelper.DelayedMessage f$3;
                                public final /* synthetic */ String f$4;

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
                            dispatchQueue.postRunnable(r7);
                        } else if (c == 1) {
                            Utilities.globalQueue.postRunnable(new Runnable(delayedMessage10, new File(FileLoader.getDirectory(4), Utilities.MD5(str9) + ".gif"), messageObject) {
                                public final /* synthetic */ SendMessagesHelper.DelayedMessage f$1;
                                public final /* synthetic */ File f$2;
                                public final /* synthetic */ MessageObject f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void run() {
                                    SendMessagesHelper.this.lambda$didReceivedNotification$4$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
                                }
                            });
                            i13++;
                            messageObject6 = null;
                        }
                        i13++;
                        messageObject6 = null;
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
    }

    public /* synthetic */ void lambda$didReceivedNotification$2$SendMessagesHelper(File file, MessageObject messageObject, DelayedMessage delayedMessage, String str) {
        AndroidUtilities.runOnUIThread(new Runnable(generatePhotoSizes(file.toString(), (Uri) null), messageObject, file, delayedMessage, str) {
            public final /* synthetic */ TLRPC$TL_photo f$1;
            public final /* synthetic */ MessageObject f$2;
            public final /* synthetic */ File f$3;
            public final /* synthetic */ SendMessagesHelper.DelayedMessage f$4;
            public final /* synthetic */ String f$5;

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
            public final /* synthetic */ SendMessagesHelper.DelayedMessage f$1;
            public final /* synthetic */ File f$2;
            public final /* synthetic */ TLRPC$Document f$3;
            public final /* synthetic */ MessageObject f$4;

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
            org.telegram.tgnet.TLRPC$Peer r11 = r11.peer_id
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
            sendScreenshotMessage(getMessagesController().getUser(Integer.valueOf((int) messageObject.getDialogId())), messageObject.getReplyMsgId(), messageObject.messageOwner);
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
                    sendMessage(messageObject2.messageOwner.message, j, messageObject2.replyMessageObject, (MessageObject) null, tLRPC$WebPage, true, arrayList, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (((int) j2) != 0) {
                    ArrayList arrayList4 = new ArrayList();
                    arrayList4.add(messageObject2);
                    sendMessage(arrayList4, j, true, 0);
                }
            } else {
                int i2 = (int) j2;
                if (i2 != 0 || tLRPC$Message.peer_id == null || (!(tLRPC$MessageMedia.photo instanceof TLRPC$TL_photo) && !(tLRPC$MessageMedia.document instanceof TLRPC$TL_document))) {
                    hashMap = null;
                } else {
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("parentObject", "sent_" + messageObject2.messageOwner.peer_id.channel_id + "_" + messageObject.getId());
                    hashMap = hashMap2;
                }
                TLRPC$Message tLRPC$Message3 = messageObject2.messageOwner;
                TLRPC$MessageMedia tLRPC$MessageMedia3 = tLRPC$Message3.media;
                TLRPC$Photo tLRPC$Photo = tLRPC$MessageMedia3.photo;
                if (tLRPC$Photo instanceof TLRPC$TL_photo) {
                    sendMessage((TLRPC$TL_photo) tLRPC$Photo, (String) null, j, messageObject2.replyMessageObject, (MessageObject) null, tLRPC$Message3.message, tLRPC$Message3.entities, (TLRPC$ReplyMarkup) null, hashMap, true, 0, tLRPC$MessageMedia3.ttl_seconds, messageObject);
                    return;
                }
                TLRPC$Document tLRPC$Document = tLRPC$MessageMedia3.document;
                if (tLRPC$Document instanceof TLRPC$TL_document) {
                    sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, tLRPC$Message3.attachPath, j, messageObject2.replyMessageObject, (MessageObject) null, tLRPC$Message3.message, tLRPC$Message3.entities, (TLRPC$ReplyMarkup) null, hashMap, true, 0, tLRPC$MessageMedia3.ttl_seconds, messageObject);
                } else if ((tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaVenue) || (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaGeo)) {
                    sendMessage(messageObject2.messageOwner.media, j, messageObject2.replyMessageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (tLRPC$MessageMedia3.phone_number != null) {
                    TLRPC$TL_userContact_old2 tLRPC$TL_userContact_old2 = new TLRPC$TL_userContact_old2();
                    TLRPC$MessageMedia tLRPC$MessageMedia4 = messageObject2.messageOwner.media;
                    tLRPC$TL_userContact_old2.phone = tLRPC$MessageMedia4.phone_number;
                    tLRPC$TL_userContact_old2.first_name = tLRPC$MessageMedia4.first_name;
                    tLRPC$TL_userContact_old2.last_name = tLRPC$MessageMedia4.last_name;
                    tLRPC$TL_userContact_old2.id = tLRPC$MessageMedia4.user_id;
                    sendMessage((TLRPC$User) tLRPC$TL_userContact_old2, j, messageObject2.replyMessageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
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
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$Message.from_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = getUserConfig().getClientUserId();
                int i2 = tLRPC$Message.flags | 256;
                tLRPC$Message.flags = i2;
                tLRPC$Message.flags = i2 | 8;
                TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
                tLRPC$Message.reply_to = tLRPC$TL_messageReplyHeader;
                tLRPC$TL_messageReplyHeader.reply_to_msg_id = i;
                TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
                tLRPC$Message.peer_id = tLRPC$TL_peerUser2;
                tLRPC$TL_peerUser2.user_id = tLRPC$User.id;
                tLRPC$Message.date = getConnectionsManager().getCurrentTime();
                tLRPC$Message.action = new TLRPC$TL_messageActionScreenshotTaken();
                getUserConfig().saveConfig(false);
            }
            tLRPC$TL_messages_sendScreenshotNotification.random_id = tLRPC$Message.random_id;
            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message, false, true);
            messageObject.messageOwner.send_state = 1;
            messageObject.wasJustSent = true;
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

    public void sendSticker(TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, MessageObject messageObject2, Object obj, boolean z, int i) {
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
                    if ((closestPhotoSizeWithSize instanceof TLRPC$TL_photoSize) || (closestPhotoSizeWithSize instanceof TLRPC$TL_photoSizeProgressive)) {
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
                mediaSendQueue.postRunnable(new Runnable(tLRPC$TL_document_layer82, j, messageObject, messageObject2, z, i, obj) {
                    public final /* synthetic */ TLRPC$Document f$1;
                    public final /* synthetic */ long f$2;
                    public final /* synthetic */ MessageObject f$3;
                    public final /* synthetic */ MessageObject f$4;
                    public final /* synthetic */ boolean f$5;
                    public final /* synthetic */ int f$6;
                    public final /* synthetic */ Object f$7;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                        this.f$4 = r6;
                        this.f$5 = r7;
                        this.f$6 = r8;
                        this.f$7 = r9;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$sendSticker$6$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                    }
                });
                return;
            }
            sendMessage((TLRPC$TL_document) tLRPC$TL_document_layer82, (VideoEditedInfo) null, (String) null, j, messageObject, messageObject2, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj);
        }
    }

    public /* synthetic */ void lambda$sendSticker$6$SendMessagesHelper(TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, Object obj) {
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
        AndroidUtilities.runOnUIThread(new Runnable(bitmapArr, strArr, tLRPC$Document, j, messageObject, messageObject2, z, i, obj) {
            public final /* synthetic */ Bitmap[] f$1;
            public final /* synthetic */ String[] f$2;
            public final /* synthetic */ TLRPC$Document f$3;
            public final /* synthetic */ long f$4;
            public final /* synthetic */ MessageObject f$5;
            public final /* synthetic */ MessageObject f$6;
            public final /* synthetic */ boolean f$7;
            public final /* synthetic */ int f$8;
            public final /* synthetic */ Object f$9;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r7;
                this.f$6 = r8;
                this.f$7 = r9;
                this.f$8 = r10;
                this.f$9 = r11;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$5$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$SendMessagesHelper(Bitmap[] bitmapArr, String[] strArr, TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, Object obj) {
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, (String) null, j, messageObject, messageObject2, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:255:0x064f, code lost:
        if (r6.get(r7 + 1).getDialogId() != r3.getDialogId()) goto L_0x067a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0362  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0373  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x037b  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0385  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x038d  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x039a  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x03a4  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03d8  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0487  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0494  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x049e  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x04bb  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x04e7 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0509  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x050c  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0529  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x0550  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0552  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0562 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x05a3  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x05aa  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x05b5  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x05c8  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x05ca  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x05df  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x05e1  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x05e9  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0676  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x068d  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0690  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x069f  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x06a1  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x06c5  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x06eb  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x06fb  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x0723  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0742  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0744  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0751  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x0754  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x07b4  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01b7  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01d1  */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0201  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r49, long r50, boolean r52, int r53) {
        /*
            r48 = this;
            r14 = r48
            r15 = r49
            r12 = r50
            r11 = r53
            r10 = 0
            if (r15 == 0) goto L_0x0874
            boolean r0 = r49.isEmpty()
            if (r0 == 0) goto L_0x0013
            goto L_0x0874
        L_0x0013:
            int r0 = (int) r12
            if (r0 == 0) goto L_0x0855
            org.telegram.messenger.MessagesController r1 = r48.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r9 = r1.getPeer(r0)
            if (r0 <= 0) goto L_0x003e
            org.telegram.messenger.MessagesController r1 = r48.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r1 != 0) goto L_0x002f
            return r10
        L_0x002f:
            r5 = 0
            r6 = 0
            r16 = 0
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 1
            r21 = 1
            goto L_0x008b
        L_0x003e:
            org.telegram.messenger.MessagesController r1 = r48.getMessagesController()
            int r2 = -r0
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x006a
            boolean r2 = r1.megagroup
            boolean r3 = r1.signatures
            if (r2 != 0) goto L_0x006c
            boolean r4 = r1.has_link
            if (r4 == 0) goto L_0x006c
            org.telegram.messenger.MessagesController r4 = r48.getMessagesController()
            int r5 = r1.id
            org.telegram.tgnet.TLRPC$ChatFull r4 = r4.getChatFull(r5)
            if (r4 == 0) goto L_0x006c
            int r4 = r4.linked_chat_id
            goto L_0x006d
        L_0x006a:
            r2 = 0
            r3 = 0
        L_0x006c:
            r4 = 0
        L_0x006d:
            boolean r5 = org.telegram.messenger.ChatObject.canSendStickers(r1)
            boolean r6 = org.telegram.messenger.ChatObject.canSendMedia(r1)
            boolean r16 = org.telegram.messenger.ChatObject.canSendEmbed(r1)
            boolean r17 = org.telegram.messenger.ChatObject.canSendPolls(r1)
            r18 = r5
            r19 = r6
            r20 = r16
            r21 = r17
            r6 = r1
            r16 = r2
            r17 = r3
            r5 = r4
        L_0x008b:
            android.util.LongSparseArray r4 = new android.util.LongSparseArray
            r4.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r22 = new java.util.ArrayList
            r22.<init>()
            android.util.LongSparseArray r23 = new android.util.LongSparseArray
            r23.<init>()
            org.telegram.messenger.MessagesController r10 = r48.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r10 = r10.getInputPeer((int) r0)
            org.telegram.messenger.UserConfig r0 = r48.getUserConfig()
            int r0 = r0.getClientUserId()
            r25 = r9
            long r8 = (long) r0
            int r27 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r27 != 0) goto L_0x00c3
            r27 = 1
            goto L_0x00c5
        L_0x00c3:
            r27 = 0
        L_0x00c5:
            r30 = r2
            r28 = r22
            r29 = r23
            r7 = 0
            r23 = 0
            r2 = r1
            r1 = r3
        L_0x00d0:
            int r3 = r49.size()
            if (r7 >= r3) goto L_0x0850
            java.lang.Object r3 = r15.get(r7)
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            int r31 = r3.getId()
            if (r31 <= 0) goto L_0x07d3
            boolean r31 = r3.needDrawBluredPreview()
            if (r31 == 0) goto L_0x00ea
            goto L_0x07d3
        L_0x00ea:
            if (r18 != 0) goto L_0x0151
            boolean r32 = r3.isSticker()
            if (r32 != 0) goto L_0x0104
            boolean r32 = r3.isAnimatedSticker()
            if (r32 != 0) goto L_0x0104
            boolean r32 = r3.isGif()
            if (r32 != 0) goto L_0x0104
            boolean r32 = r3.isGame()
            if (r32 == 0) goto L_0x0151
        L_0x0104:
            if (r23 != 0) goto L_0x0130
            r3 = 8
            boolean r3 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r3)
            if (r3 == 0) goto L_0x0110
            r15 = 4
            goto L_0x0111
        L_0x0110:
            r15 = 1
        L_0x0111:
            r31 = r0
            r13 = r7
            r34 = r8
            r36 = r10
            r23 = r15
            r41 = r25
            r3 = r28
            r37 = r30
            r22 = 1
            r26 = 0
            r30 = 0
            r25 = r4
            r28 = r5
        L_0x012a:
            r4 = r29
            r29 = r6
            goto L_0x0830
        L_0x0130:
            r31 = r0
            r32 = r1
            r33 = r2
            r13 = r7
            r34 = r8
            r36 = r10
            r41 = r25
            r24 = r28
            r38 = r29
            r37 = r30
            r22 = 1
            r26 = 0
            r30 = 0
            r25 = r4
            r28 = r5
            r29 = r6
            goto L_0x0828
        L_0x0151:
            r32 = 2
            if (r19 != 0) goto L_0x01ab
            org.telegram.tgnet.TLRPC$Message r15 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r15 = r15.media
            r34 = r7
            boolean r7 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r7 != 0) goto L_0x0163
            boolean r7 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r7 == 0) goto L_0x01ad
        L_0x0163:
            if (r23 != 0) goto L_0x0189
            r3 = 7
            boolean r3 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r3)
            if (r3 == 0) goto L_0x016e
            r32 = 5
        L_0x016e:
            r31 = r0
            r36 = r10
            r41 = r25
            r3 = r28
            r37 = r30
            r23 = r32
        L_0x017a:
            r13 = r34
            r22 = 1
            r26 = 0
            r30 = 0
            r25 = r4
            r28 = r5
            r34 = r8
            goto L_0x012a
        L_0x0189:
            r31 = r0
            r32 = r1
            r33 = r2
            r36 = r10
            r41 = r25
            r24 = r28
            r38 = r29
            r37 = r30
            r13 = r34
            r22 = 1
            r26 = 0
            r30 = 0
            r25 = r4
            r28 = r5
            r29 = r6
            r34 = r8
            goto L_0x0828
        L_0x01ab:
            r34 = r7
        L_0x01ad:
            if (r21 != 0) goto L_0x01d1
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r7 == 0) goto L_0x01d1
            if (r23 != 0) goto L_0x0189
            r3 = 10
            boolean r3 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r3)
            if (r3 == 0) goto L_0x01c3
            r3 = 6
            goto L_0x01c4
        L_0x01c3:
            r3 = 3
        L_0x01c4:
            r31 = r0
            r23 = r3
            r36 = r10
            r41 = r25
            r3 = r28
            r37 = r30
            goto L_0x017a
        L_0x01d1:
            org.telegram.tgnet.TLRPC$TL_message r7 = new org.telegram.tgnet.TLRPC$TL_message
            r7.<init>()
            long r35 = r3.getDialogId()
            int r15 = (r35 > r8 ? 1 : (r35 == r8 ? 0 : -1))
            if (r15 != 0) goto L_0x01f8
            boolean r15 = r3.isFromUser()
            if (r15 == 0) goto L_0x01f8
            org.telegram.tgnet.TLRPC$Message r15 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r15 = r15.from_id
            int r15 = r15.user_id
            org.telegram.messenger.UserConfig r35 = r48.getUserConfig()
            r36 = r2
            int r2 = r35.getClientUserId()
            if (r15 != r2) goto L_0x01fa
            r2 = 1
            goto L_0x01fb
        L_0x01f8:
            r36 = r2
        L_0x01fa:
            r2 = 0
        L_0x01fb:
            boolean r15 = r3.isForwarded()
            if (r15 == 0) goto L_0x028d
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r2 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r2.<init>()
            r7.fwd_from = r2
            org.telegram.tgnet.TLRPC$Message r15 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r15 = r15.fwd_from
            int r14 = r15.flags
            r22 = 1
            r14 = r14 & 1
            if (r14 == 0) goto L_0x021e
            int r14 = r2.flags
            r14 = r14 | 1
            r2.flags = r14
            org.telegram.tgnet.TLRPC$Peer r14 = r15.from_id
            r2.from_id = r14
        L_0x021e:
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from
            int r14 = r2.flags
            r14 = r14 & 32
            if (r14 == 0) goto L_0x0234
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r7.fwd_from
            int r15 = r14.flags
            r15 = r15 | 32
            r14.flags = r15
            java.lang.String r2 = r2.from_name
            r14.from_name = r2
        L_0x0234:
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from
            int r14 = r2.flags
            r15 = 4
            r14 = r14 & r15
            if (r14 == 0) goto L_0x024c
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r7.fwd_from
            r35 = r10
            int r10 = r14.flags
            r10 = r10 | r15
            r14.flags = r10
            int r2 = r2.channel_post
            r14.channel_post = r2
            goto L_0x024e
        L_0x024c:
            r35 = r10
        L_0x024e:
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from
            int r10 = r2.flags
            r14 = 8
            r10 = r10 & r14
            if (r10 == 0) goto L_0x0264
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r7.fwd_from
            int r15 = r10.flags
            r14 = r14 | r15
            r10.flags = r14
            java.lang.String r2 = r2.post_author
            r10.post_author = r2
        L_0x0264:
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from
            int r10 = r2.flags
            r10 = r10 & 16
            if (r10 == 0) goto L_0x027e
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r7.fwd_from
            int r14 = r10.flags
            r14 = r14 | 16
            r10.flags = r14
            org.telegram.tgnet.TLRPC$Peer r14 = r2.saved_from_peer
            r10.saved_from_peer = r14
            int r2 = r2.saved_from_msg_id
            r10.saved_from_msg_id = r2
        L_0x027e:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r7.fwd_from
            org.telegram.tgnet.TLRPC$Message r10 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r10.fwd_from
            int r10 = r10.date
            r2.date = r10
            r10 = 4
            r7.flags = r10
            goto L_0x0340
        L_0x028d:
            r35 = r10
            r10 = 4
            if (r2 != 0) goto L_0x0340
            int r2 = r3.getFromChatId()
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r14 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r14.<init>()
            r7.fwd_from = r14
            int r15 = r3.getId()
            r14.channel_post = r15
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r7.fwd_from
            int r15 = r14.flags
            r15 = r15 | r10
            r14.flags = r15
            boolean r10 = r3.isFromUser()
            if (r10 == 0) goto L_0x02c1
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r7.fwd_from
            org.telegram.tgnet.TLRPC$Message r14 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id
            r10.from_id = r14
            int r14 = r10.flags
            r15 = 1
            r14 = r14 | r15
            r10.flags = r14
            r37 = r1
            goto L_0x02f3
        L_0x02c1:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r7.fwd_from
            org.telegram.tgnet.TLRPC$TL_peerChannel r14 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r14.<init>()
            r10.from_id = r14
            org.telegram.tgnet.TLRPC$MessageFwdHeader r10 = r7.fwd_from
            org.telegram.tgnet.TLRPC$Peer r14 = r10.from_id
            org.telegram.tgnet.TLRPC$Message r15 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r11 = r15.peer_id
            r37 = r1
            int r1 = r11.channel_id
            r14.channel_id = r1
            int r1 = r10.flags
            r1 = r1 | 2
            r10.flags = r1
            boolean r1 = r15.post
            if (r1 == 0) goto L_0x02f3
            if (r2 <= 0) goto L_0x02f3
            org.telegram.tgnet.TLRPC$Peer r1 = r15.from_id
            if (r1 == 0) goto L_0x02e9
            r11 = r1
        L_0x02e9:
            r10.from_id = r11
            org.telegram.tgnet.TLRPC$MessageFwdHeader r1 = r7.fwd_from
            int r10 = r1.flags
            r11 = 1
            r10 = r10 | r11
            r1.flags = r10
        L_0x02f3:
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            java.lang.String r1 = r1.post_author
            if (r1 == 0) goto L_0x0305
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r7.fwd_from
            r2.post_author = r1
            int r1 = r2.flags
            r10 = 8
            r1 = r1 | r10
            r2.flags = r1
            goto L_0x0336
        L_0x0305:
            boolean r1 = r3.isOutOwner()
            if (r1 != 0) goto L_0x0336
            if (r2 <= 0) goto L_0x0336
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            boolean r1 = r1.post
            if (r1 == 0) goto L_0x0336
            org.telegram.messenger.MessagesController r1 = r48.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r1 == 0) goto L_0x0336
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r7.fwd_from
            java.lang.String r10 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r10, r1)
            r2.post_author = r1
            org.telegram.tgnet.TLRPC$MessageFwdHeader r1 = r7.fwd_from
            int r2 = r1.flags
            r10 = 8
            r2 = r2 | r10
            r1.flags = r2
        L_0x0336:
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            int r1 = r1.date
            r7.date = r1
            r1 = 4
            r7.flags = r1
            goto L_0x0342
        L_0x0340:
            r37 = r1
        L_0x0342:
            int r1 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r1 != 0) goto L_0x0369
            org.telegram.tgnet.TLRPC$MessageFwdHeader r1 = r7.fwd_from
            if (r1 == 0) goto L_0x0369
            int r2 = r1.flags
            r2 = r2 | 16
            r1.flags = r2
            int r2 = r3.getId()
            r1.saved_from_msg_id = r2
            org.telegram.tgnet.TLRPC$MessageFwdHeader r1 = r7.fwd_from
            org.telegram.tgnet.TLRPC$Message r2 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            r1.saved_from_peer = r2
            int r1 = r2.user_id
            if (r1 != r0) goto L_0x0369
            long r10 = r3.getDialogId()
            int r1 = (int) r10
            r2.user_id = r1
        L_0x0369:
            if (r20 != 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r1 == 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r7.media = r1
            goto L_0x0381
        L_0x037b:
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            r7.media = r1
        L_0x0381:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            if (r1 == 0) goto L_0x038b
            int r1 = r7.flags
            r1 = r1 | 512(0x200, float:7.175E-43)
            r7.flags = r1
        L_0x038b:
            if (r16 == 0) goto L_0x0394
            int r1 = r7.flags
            r2 = -2147483648(0xfffffffvar_, float:-0.0)
            r1 = r1 | r2
            r7.flags = r1
        L_0x0394:
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            int r1 = r1.via_bot_id
            if (r1 == 0) goto L_0x03a2
            r7.via_bot_id = r1
            int r1 = r7.flags
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r7.flags = r1
        L_0x03a2:
            if (r5 == 0) goto L_0x03bc
            org.telegram.tgnet.TLRPC$TL_messageReplies r1 = new org.telegram.tgnet.TLRPC$TL_messageReplies
            r1.<init>()
            r7.replies = r1
            r2 = 1
            r1.comments = r2
            r1.channel_id = r5
            int r10 = r1.flags
            r10 = r10 | r2
            r1.flags = r10
            int r1 = r7.flags
            r2 = 8388608(0x800000, float:1.17549435E-38)
            r1 = r1 | r2
            r7.flags = r1
        L_0x03bc:
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            java.lang.String r1 = r1.message
            r7.message = r1
            int r1 = r3.getId()
            r7.fwd_msg_id = r1
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            java.lang.String r2 = r1.attachPath
            r7.attachPath = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r1.entities
            r7.entities = r2
            org.telegram.tgnet.TLRPC$ReplyMarkup r1 = r1.reply_markup
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            if (r1 == 0) goto L_0x0487
            org.telegram.tgnet.TLRPC$TL_replyInlineMarkup r1 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            r1.<init>()
            r7.reply_markup = r1
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r1 = r1.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r1 = r1.rows
            int r1 = r1.size()
            r2 = 0
            r10 = 0
        L_0x03eb:
            if (r2 >= r1) goto L_0x0474
            org.telegram.tgnet.TLRPC$Message r11 = r3.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r11 = r11.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r11 = r11.rows
            java.lang.Object r11 = r11.get(r2)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r11 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r14 = r11.buttons
            int r14 = r14.size()
            r15 = 0
            r31 = 0
        L_0x0402:
            r32 = r0
            if (r15 >= r14) goto L_0x0463
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r0 = r11.buttons
            java.lang.Object r0 = r0.get(r15)
            org.telegram.tgnet.TLRPC$KeyboardButton r0 = (org.telegram.tgnet.TLRPC$KeyboardButton) r0
            r33 = r1
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r38 = r5
            if (r1 != 0) goto L_0x0421
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl
            if (r5 != 0) goto L_0x0421
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline
            if (r5 == 0) goto L_0x041f
            goto L_0x0421
        L_0x041f:
            r10 = 1
            goto L_0x0467
        L_0x0421:
            if (r1 == 0) goto L_0x0442
            org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth r1 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r1.<init>()
            int r5 = r0.flags
            r1.flags = r5
            java.lang.String r5 = r0.fwd_text
            if (r5 == 0) goto L_0x0435
            r1.fwd_text = r5
            r1.text = r5
            goto L_0x0439
        L_0x0435:
            java.lang.String r5 = r0.text
            r1.text = r5
        L_0x0439:
            java.lang.String r5 = r0.url
            r1.url = r5
            int r0 = r0.button_id
            r1.button_id = r0
            r0 = r1
        L_0x0442:
            if (r31 != 0) goto L_0x0451
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r1 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow
            r1.<init>()
            org.telegram.tgnet.TLRPC$ReplyMarkup r5 = r7.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r5 = r5.rows
            r5.add(r1)
            goto L_0x0453
        L_0x0451:
            r1 = r31
        L_0x0453:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r5 = r1.buttons
            r5.add(r0)
            int r15 = r15 + 1
            r31 = r1
            r0 = r32
            r1 = r33
            r5 = r38
            goto L_0x0402
        L_0x0463:
            r33 = r1
            r38 = r5
        L_0x0467:
            if (r10 == 0) goto L_0x046a
            goto L_0x0478
        L_0x046a:
            int r2 = r2 + 1
            r0 = r32
            r1 = r33
            r5 = r38
            goto L_0x03eb
        L_0x0474:
            r32 = r0
            r38 = r5
        L_0x0478:
            if (r10 != 0) goto L_0x0481
            int r0 = r7.flags
            r0 = r0 | 64
            r7.flags = r0
            goto L_0x048b
        L_0x0481:
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            r10 = 0
            r0.reply_markup = r10
            goto L_0x048c
        L_0x0487:
            r32 = r0
            r38 = r5
        L_0x048b:
            r10 = 0
        L_0x048c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r7.entities
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x049a
            int r0 = r7.flags
            r0 = r0 | 128(0x80, float:1.794E-43)
            r7.flags = r0
        L_0x049a:
            java.lang.String r0 = r7.attachPath
            if (r0 != 0) goto L_0x04a2
            java.lang.String r0 = ""
            r7.attachPath = r0
        L_0x04a2:
            org.telegram.messenger.UserConfig r0 = r48.getUserConfig()
            int r0 = r0.getNewMessageId()
            r7.id = r0
            r7.local_id = r0
            r0 = 1
            r7.out = r0
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            long r0 = r0.grouped_id
            r14 = 0
            int r2 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r2 == 0) goto L_0x04e1
            java.lang.Object r0 = r4.get(r0)
            java.lang.Long r0 = (java.lang.Long) r0
            if (r0 != 0) goto L_0x04d4
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r0 = r0.nextLong()
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            long r1 = r1.grouped_id
            r4.put(r1, r0)
        L_0x04d4:
            long r0 = r0.longValue()
            r7.grouped_id = r0
            int r0 = r7.flags
            r1 = 131072(0x20000, float:1.83671E-40)
            r0 = r0 | r1
            r7.flags = r0
        L_0x04e1:
            r11 = r25
            int r0 = r11.channel_id
            if (r0 == 0) goto L_0x0503
            if (r16 != 0) goto L_0x0503
            if (r17 == 0) goto L_0x04fd
            org.telegram.tgnet.TLRPC$TL_peerUser r0 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r0.<init>()
            r7.from_id = r0
            org.telegram.messenger.UserConfig r1 = r48.getUserConfig()
            int r1 = r1.getClientUserId()
            r0.user_id = r1
            goto L_0x04ff
        L_0x04fd:
            r7.from_id = r11
        L_0x04ff:
            r0 = 1
            r7.post = r0
            goto L_0x0523
        L_0x0503:
            boolean r0 = org.telegram.messenger.ChatObject.shouldSendAnonymously(r6)
            if (r0 == 0) goto L_0x050c
            r7.from_id = r11
            goto L_0x0523
        L_0x050c:
            org.telegram.tgnet.TLRPC$TL_peerUser r0 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r0.<init>()
            r7.from_id = r0
            org.telegram.messenger.UserConfig r1 = r48.getUserConfig()
            int r1 = r1.getClientUserId()
            r0.user_id = r1
            int r0 = r7.flags
            r0 = r0 | 256(0x100, float:3.59E-43)
            r7.flags = r0
        L_0x0523:
            long r0 = r7.random_id
            int r2 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r2 != 0) goto L_0x052f
            long r0 = r48.getNextRandomId()
            r7.random_id = r0
        L_0x052f:
            long r0 = r7.random_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            r1 = r37
            r1.add(r0)
            long r14 = r7.random_id
            r5 = r29
            r5.put(r14, r7)
            int r0 = r7.fwd_msg_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r14 = r28
            r14.add(r0)
            r15 = r53
            if (r15 == 0) goto L_0x0552
            r0 = r15
            goto L_0x055a
        L_0x0552:
            org.telegram.tgnet.ConnectionsManager r0 = r48.getConnectionsManager()
            int r0 = r0.getCurrentTime()
        L_0x055a:
            r7.date = r0
            r2 = r35
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r0 == 0) goto L_0x0572
            if (r16 != 0) goto L_0x0572
            if (r15 != 0) goto L_0x056f
            r10 = 1
            r7.views = r10
            int r10 = r7.flags
            r10 = r10 | 1024(0x400, float:1.435E-42)
            r7.flags = r10
        L_0x056f:
            r25 = r4
            goto L_0x058b
        L_0x0572:
            org.telegram.tgnet.TLRPC$Message r10 = r3.messageOwner
            r25 = r4
            int r4 = r10.flags
            r4 = r4 & 1024(0x400, float:1.435E-42)
            if (r4 == 0) goto L_0x0588
            if (r15 != 0) goto L_0x0588
            int r4 = r10.views
            r7.views = r4
            int r4 = r7.flags
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r7.flags = r4
        L_0x0588:
            r4 = 1
            r7.unread = r4
        L_0x058b:
            r7.dialog_id = r12
            r7.peer_id = r11
            boolean r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r7)
            if (r4 != 0) goto L_0x059b
            boolean r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r7)
            if (r4 == 0) goto L_0x05ad
        L_0x059b:
            if (r0 == 0) goto L_0x05aa
            int r0 = r3.getChannelId()
            if (r0 == 0) goto L_0x05aa
            boolean r0 = r3.isContentUnread()
            r7.media_unread = r0
            goto L_0x05ad
        L_0x05aa:
            r0 = 1
            r7.media_unread = r0
        L_0x05ad:
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r4 == 0) goto L_0x05ba
            int r0 = r0.channel_id
            int r0 = -r0
            r7.ttl = r0
        L_0x05ba:
            org.telegram.messenger.MessageObject r0 = new org.telegram.messenger.MessageObject
            r10 = r48
            int r4 = r10.currentAccount
            r28 = r5
            r5 = 1
            r0.<init>(r4, r7, r5, r5)
            if (r15 == 0) goto L_0x05ca
            r4 = 1
            goto L_0x05cb
        L_0x05ca:
            r4 = 0
        L_0x05cb:
            r0.scheduled = r4
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            r4.send_state = r5
            r0.wasJustSent = r5
            r4 = r36
            r4.add(r0)
            r5 = r30
            r5.add(r7)
            if (r15 == 0) goto L_0x05e1
            r0 = 1
            goto L_0x05e2
        L_0x05e1:
            r0 = 0
        L_0x05e2:
            r10.putToSendingMessages(r7, r0)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0620
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r7 = "forward message user_id = "
            r0.append(r7)
            int r7 = r2.user_id
            r0.append(r7)
            java.lang.String r7 = " chat_id = "
            r0.append(r7)
            int r7 = r2.chat_id
            r0.append(r7)
            java.lang.String r7 = " channel_id = "
            r0.append(r7)
            int r7 = r2.channel_id
            r0.append(r7)
            java.lang.String r7 = " access_hash = "
            r0.append(r7)
            r29 = r6
            long r6 = r2.access_hash
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x0622
        L_0x0620:
            r29 = r6
        L_0x0622:
            int r0 = r5.size()
            r6 = 100
            if (r0 == r6) goto L_0x0676
            int r0 = r49.size()
            r6 = 1
            int r0 = r0 - r6
            r7 = r34
            if (r7 == r0) goto L_0x0673
            int r0 = r49.size()
            int r0 = r0 - r6
            if (r7 == r0) goto L_0x0652
            int r0 = r7 + 1
            r6 = r49
            java.lang.Object r0 = r6.get(r0)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r30 = r0.getDialogId()
            long r33 = r3.getDialogId()
            int r0 = (r30 > r33 ? 1 : (r30 == r33 ? 0 : -1))
            if (r0 == 0) goto L_0x0654
            goto L_0x067a
        L_0x0652:
            r6 = r49
        L_0x0654:
            r36 = r2
            r33 = r4
            r37 = r5
            r13 = r7
            r34 = r8
            r41 = r11
            r24 = r14
            r31 = r32
            r22 = 1
            r26 = 0
            r30 = 0
            r32 = r1
            r47 = r38
            r38 = r28
            r28 = r47
            goto L_0x0828
        L_0x0673:
            r6 = r49
            goto L_0x067a
        L_0x0676:
            r6 = r49
            r7 = r34
        L_0x067a:
            org.telegram.messenger.MessagesStorage r39 = r48.getMessagesStorage()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r5)
            r41 = 0
            r42 = 1
            r43 = 0
            r44 = 0
            if (r15 == 0) goto L_0x0690
            r45 = 1
            goto L_0x0692
        L_0x0690:
            r45 = 0
        L_0x0692:
            r40 = r0
            r39.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r40, (boolean) r41, (boolean) r42, (boolean) r43, (int) r44, (boolean) r45)
            org.telegram.messenger.MessagesController r0 = r48.getMessagesController()
            r30 = r5
            if (r15 == 0) goto L_0x06a1
            r5 = 1
            goto L_0x06a2
        L_0x06a1:
            r5 = 0
        L_0x06a2:
            r0.updateInterfaceWithMessages(r12, r4, r5)
            org.telegram.messenger.NotificationCenter r0 = r48.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r34 = r7
            r35 = r8
            r7 = 0
            java.lang.Object[] r8 = new java.lang.Object[r7]
            r0.postNotificationName(r5, r8)
            org.telegram.messenger.UserConfig r0 = r48.getUserConfig()
            r0.saveConfig(r7)
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r9 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages
            r9.<init>()
            r9.to_peer = r2
            if (r52 == 0) goto L_0x06e6
            int r0 = r10.currentAccount
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "silent_"
            r5.append(r7)
            r5.append(r12)
            java.lang.String r5 = r5.toString()
            r7 = 0
            boolean r0 = r0.getBoolean(r5, r7)
            if (r0 == 0) goto L_0x06e4
            goto L_0x06e6
        L_0x06e4:
            r0 = 0
            goto L_0x06e7
        L_0x06e6:
            r0 = 1
        L_0x06e7:
            r9.silent = r0
            if (r15 == 0) goto L_0x06f3
            r9.schedule_date = r15
            int r0 = r9.flags
            r0 = r0 | 1024(0x400, float:1.435E-42)
            r9.flags = r0
        L_0x06f3:
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r0 == 0) goto L_0x0723
            org.telegram.messenger.MessagesController r0 = r48.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            int r5 = r5.channel_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r5)
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r5 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            r5.<init>()
            r9.from_peer = r5
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            int r7 = r7.channel_id
            r5.channel_id = r7
            if (r0 == 0) goto L_0x072a
            long r7 = r0.access_hash
            r5.access_hash = r7
            goto L_0x072a
        L_0x0723:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r0.<init>()
            r9.from_peer = r0
        L_0x072a:
            r9.random_id = r1
            r9.id = r14
            int r0 = r49.size()
            r7 = 1
            r8 = 0
            if (r0 != r7) goto L_0x0744
            java.lang.Object r0 = r6.get(r8)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0.with_my_score
            if (r0 == 0) goto L_0x0744
            r0 = 1
            goto L_0x0745
        L_0x0744:
            r0 = 0
        L_0x0745:
            r9.with_my_score = r0
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>(r4)
            r0 = 2147483646(0x7ffffffe, float:NaN)
            if (r15 != r0) goto L_0x0754
            r22 = 1
            goto L_0x0756
        L_0x0754:
            r22 = 0
        L_0x0756:
            org.telegram.tgnet.ConnectionsManager r0 = r48.getConnectionsManager()
            r24 = r14
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$flpU3A3e0tPwUqp389LcbHlF_dc r14 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$flpU3A3e0tPwUqp389LcbHlF_dc
            r15 = r0
            r31 = r32
            r0 = r14
            r32 = r1
            r1 = r48
            r33 = r4
            r37 = r30
            r30 = r2
            r4 = r3
            r2 = r50
            r39 = r4
            r4 = r53
            r40 = r5
            r47 = r38
            r38 = r28
            r28 = r47
            r5 = r16
            r6 = r22
            r46 = r34
            r22 = 1
            r7 = r27
            r34 = r35
            r26 = 0
            r36 = 0
            r8 = r38
            r41 = r11
            r11 = r9
            r9 = r37
            r36 = r30
            r30 = 0
            r10 = r40
            r40 = r11
            r11 = r39
            r12 = r41
            r13 = r40
            r0.<init>(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r0 = 68
            r1 = r40
            r15.sendRequest(r1, r14, r0)
            int r0 = r49.size()
            int r0 = r0 + -1
            r13 = r46
            if (r13 == r0) goto L_0x0828
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
            r37 = r1
            r1 = r2
            r2 = r0
            goto L_0x0830
        L_0x07d3:
            r31 = r0
            r32 = r1
            r33 = r2
            r13 = r7
            r34 = r8
            r36 = r10
            r41 = r25
            r24 = r28
            r38 = r29
            r37 = r30
            r22 = 1
            r26 = 0
            r30 = 0
            r25 = r4
            r28 = r5
            r29 = r6
            int r0 = r3.type
            if (r0 != 0) goto L_0x0828
            java.lang.CharSequence r0 = r3.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0828
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0808
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            r6 = r0
            goto L_0x080a
        L_0x0808:
            r6 = r26
        L_0x080a:
            java.lang.CharSequence r0 = r3.messageText
            java.lang.String r1 = r0.toString()
            r4 = 0
            r5 = 0
            if (r6 == 0) goto L_0x0816
            r7 = 1
            goto L_0x0817
        L_0x0816:
            r7 = 0
        L_0x0817:
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r0.entities
            r9 = 0
            r10 = 0
            r0 = r48
            r2 = r50
            r11 = r52
            r12 = r53
            r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
        L_0x0828:
            r3 = r24
            r1 = r32
            r2 = r33
            r4 = r38
        L_0x0830:
            int r7 = r13 + 1
            r14 = r48
            r15 = r49
            r12 = r50
            r11 = r53
            r5 = r28
            r6 = r29
            r0 = r31
            r8 = r34
            r10 = r36
            r30 = r37
            r28 = r3
            r29 = r4
            r4 = r25
            r25 = r41
            goto L_0x00d0
        L_0x0850:
            r2 = r48
            r10 = r23
            goto L_0x0873
        L_0x0855:
            r30 = 0
            r10 = 0
        L_0x0858:
            int r0 = r49.size()
            if (r10 >= r0) goto L_0x0870
            r0 = r49
            java.lang.Object r1 = r0.get(r10)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r2 = r48
            r3 = r50
            r2.processForwardFromMyName(r1, r3)
            int r10 = r10 + 1
            goto L_0x0858
        L_0x0870:
            r2 = r48
            r10 = 0
        L_0x0873:
            return r10
        L_0x0874:
            r2 = r14
            r30 = 0
            return r30
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
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r0.dialogs_read_outbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r26)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x006b
            org.telegram.messenger.MessagesStorage r0 = r25.getMessagesStorage()
            r5 = r26
            int r0 = r0.getDialogReadMax(r15, r5)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.messenger.MessagesController r1 = r25.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
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
            org.telegram.tgnet.TLRPC$Peer r0 = r2.peer_id
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
        getMessagesController().deleteMessages(arrayList2, (ArrayList<Long>) null, (TLRPC$EncryptedChat) null, tLRPC$Message.dialog_id, tLRPC$Message.peer_id.channel_id, false, true);
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(arrayList, messageObject, tLRPC$Message, i, i2) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ MessageObject f$2;
            public final /* synthetic */ TLRPC$Message f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ int f$5;

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
            public final /* synthetic */ MessageObject f$1;
            public final /* synthetic */ TLRPC$Message f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ int f$4;

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
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true, true));
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
            public final /* synthetic */ TLRPC$Message f$1;
            public final /* synthetic */ long f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ TLRPC$Message f$4;
            public final /* synthetic */ int f$5;
            public final /* synthetic */ int f$6;

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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v36, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v38, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v11, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v17, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v18, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v49, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v50, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v33, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v50, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v36, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v38, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v75, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x021f A[SYNTHETIC, Splitter:B:107:0x021f] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x02cd A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0041 A[SYNTHETIC, Splitter:B:16:0x0041] */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0412 A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0422 A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0469 A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x046e A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0484 A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008d A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x012e A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x014b A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0175 A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01f6 A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0202 A[Catch:{ Exception -> 0x0510 }] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x020a  */
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
            long r7 = r28.getDialogId()     // Catch:{ Exception -> 0x0510 }
            int r9 = (int) r7     // Catch:{ Exception -> 0x0510 }
            if (r9 != 0) goto L_0x003c
            r13 = 32
            long r13 = r7 >> r13
            int r14 = (int) r13     // Catch:{ Exception -> 0x0510 }
            org.telegram.messenger.MessagesController r13 = r27.getMessagesController()     // Catch:{ Exception -> 0x0510 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$EncryptedChat r13 = r13.getEncryptedChat(r14)     // Catch:{ Exception -> 0x0510 }
            if (r13 == 0) goto L_0x003a
            int r13 = r13.layer     // Catch:{ Exception -> 0x0510 }
            int r13 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r13)     // Catch:{ Exception -> 0x0510 }
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
            org.telegram.tgnet.TLRPC$Message r2 = r11.messageOwner     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media     // Catch:{ Exception -> 0x0510 }
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0510 }
            if (r2 == 0) goto L_0x0055
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0     // Catch:{ Exception -> 0x0510 }
            r15 = r30
            r2 = 2
            goto L_0x006b
        L_0x0055:
            org.telegram.tgnet.TLRPC$Message r1 = r11.messageOwner     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$Document r1 = r1.document     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC$TL_document) r1     // Catch:{ Exception -> 0x0510 }
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1)     // Catch:{ Exception -> 0x0510 }
            if (r2 != 0) goto L_0x0068
            if (r30 == 0) goto L_0x0066
            goto L_0x0068
        L_0x0066:
            r2 = 7
            goto L_0x0069
        L_0x0068:
            r2 = 3
        L_0x0069:
            org.telegram.messenger.VideoEditedInfo r15 = r11.videoEditedInfo     // Catch:{ Exception -> 0x0510 }
        L_0x006b:
            java.util.HashMap<java.lang.String, java.lang.String> r6 = r5.params     // Catch:{ Exception -> 0x0510 }
            if (r35 != 0) goto L_0x007c
            if (r6 == 0) goto L_0x007c
            boolean r18 = r6.containsKey(r4)     // Catch:{ Exception -> 0x0510 }
            if (r18 == 0) goto L_0x007c
            java.lang.Object r4 = r6.get(r4)     // Catch:{ Exception -> 0x0510 }
            goto L_0x007e
        L_0x007c:
            r4 = r35
        L_0x007e:
            java.lang.String r12 = r5.message     // Catch:{ Exception -> 0x0510 }
            r11.editingMessage = r12     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r12 = r5.entities     // Catch:{ Exception -> 0x0510 }
            r11.editingMessageEntities = r12     // Catch:{ Exception -> 0x0510 }
            java.lang.String r12 = r5.attachPath     // Catch:{ Exception -> 0x0510 }
            r19 = r9
            r9 = r4
            goto L_0x0147
        L_0x008d:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0510 }
            r11.previousMedia = r4     // Catch:{ Exception -> 0x0510 }
            java.lang.String r4 = r5.message     // Catch:{ Exception -> 0x0510 }
            r11.previousCaption = r4     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r5.entities     // Catch:{ Exception -> 0x0510 }
            r11.previousCaptionEntities = r4     // Catch:{ Exception -> 0x0510 }
            java.lang.String r4 = r5.attachPath     // Catch:{ Exception -> 0x0510 }
            r11.previousAttachPath = r4     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0510 }
            r6 = 1
            r4.<init>((boolean) r6)     // Catch:{ Exception -> 0x0510 }
            r10.writePreviousMessageData(r5, r4)     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.SerializedData r6 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0510 }
            int r4 = r4.length()     // Catch:{ Exception -> 0x0510 }
            r6.<init>((int) r4)     // Catch:{ Exception -> 0x0510 }
            r10.writePreviousMessageData(r5, r6)     // Catch:{ Exception -> 0x0510 }
            if (r33 != 0) goto L_0x00ba
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ Exception -> 0x0510 }
            r4.<init>()     // Catch:{ Exception -> 0x0510 }
            goto L_0x00bc
        L_0x00ba:
            r4 = r33
        L_0x00bc:
            java.lang.String r12 = "prevMedia"
            byte[] r15 = r6.toByteArray()     // Catch:{ Exception -> 0x0510 }
            r19 = r9
            r9 = 0
            java.lang.String r15 = android.util.Base64.encodeToString(r15, r9)     // Catch:{ Exception -> 0x0510 }
            r4.put(r12, r15)     // Catch:{ Exception -> 0x0510 }
            r6.cleanup()     // Catch:{ Exception -> 0x0510 }
            if (r0 == 0) goto L_0x010f
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0510 }
            r6.<init>()     // Catch:{ Exception -> 0x0510 }
            r5.media = r6     // Catch:{ Exception -> 0x0510 }
            int r9 = r6.flags     // Catch:{ Exception -> 0x0510 }
            r12 = 3
            r9 = r9 | r12
            r6.flags = r9     // Catch:{ Exception -> 0x0510 }
            r6.photo = r0     // Catch:{ Exception -> 0x0510 }
            if (r2 == 0) goto L_0x00f1
            int r6 = r32.length()     // Catch:{ Exception -> 0x0510 }
            if (r6 <= 0) goto L_0x00f1
            boolean r6 = r2.startsWith(r14)     // Catch:{ Exception -> 0x0510 }
            if (r6 == 0) goto L_0x00f1
            r5.attachPath = r2     // Catch:{ Exception -> 0x0510 }
            goto L_0x010d
        L_0x00f1:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r0.sizes     // Catch:{ Exception -> 0x0510 }
            int r9 = r9.size()     // Catch:{ Exception -> 0x0510 }
            r12 = 1
            int r9 = r9 - r12
            java.lang.Object r6 = r6.get(r9)     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC$PhotoSize) r6     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.location     // Catch:{ Exception -> 0x0510 }
            java.io.File r6 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r12)     // Catch:{ Exception -> 0x0510 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0510 }
            r5.attachPath = r6     // Catch:{ Exception -> 0x0510 }
        L_0x010d:
            r6 = 2
            goto L_0x013b
        L_0x010f:
            if (r1 == 0) goto L_0x013a
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0510 }
            r6.<init>()     // Catch:{ Exception -> 0x0510 }
            r5.media = r6     // Catch:{ Exception -> 0x0510 }
            int r9 = r6.flags     // Catch:{ Exception -> 0x0510 }
            r12 = 3
            r9 = r9 | r12
            r6.flags = r9     // Catch:{ Exception -> 0x0510 }
            r6.document = r1     // Catch:{ Exception -> 0x0510 }
            boolean r6 = org.telegram.messenger.MessageObject.isVideoDocument(r31)     // Catch:{ Exception -> 0x0510 }
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
            java.lang.String r9 = r30.getString()     // Catch:{ Exception -> 0x0510 }
            java.lang.String r12 = "ve"
            r4.put(r12, r9)     // Catch:{ Exception -> 0x0510 }
        L_0x0137:
            r5.attachPath = r2     // Catch:{ Exception -> 0x0510 }
            goto L_0x013b
        L_0x013a:
            r6 = -1
        L_0x013b:
            r5.params = r4     // Catch:{ Exception -> 0x0510 }
            r9 = 3
            r5.send_state = r9     // Catch:{ Exception -> 0x0510 }
            r15 = r30
            r9 = r35
            r12 = r2
            r2 = r6
            r6 = r4
        L_0x0147:
            java.lang.String r4 = r5.attachPath     // Catch:{ Exception -> 0x0510 }
            if (r4 != 0) goto L_0x014f
            java.lang.String r4 = ""
            r5.attachPath = r4     // Catch:{ Exception -> 0x0510 }
        L_0x014f:
            r4 = 0
            r5.local_id = r4     // Catch:{ Exception -> 0x0510 }
            int r4 = r11.type     // Catch:{ Exception -> 0x0510 }
            r29 = r1
            r1 = 3
            if (r4 == r1) goto L_0x0160
            if (r15 != 0) goto L_0x0160
            int r1 = r11.type     // Catch:{ Exception -> 0x0510 }
            r4 = 2
            if (r1 != r4) goto L_0x016b
        L_0x0160:
            java.lang.String r1 = r5.attachPath     // Catch:{ Exception -> 0x0510 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0510 }
            if (r1 != 0) goto L_0x016b
            r1 = 1
            r11.attachPathExists = r1     // Catch:{ Exception -> 0x0510 }
        L_0x016b:
            org.telegram.messenger.VideoEditedInfo r1 = r11.videoEditedInfo     // Catch:{ Exception -> 0x0510 }
            if (r1 == 0) goto L_0x0173
            if (r15 != 0) goto L_0x0173
            org.telegram.messenger.VideoEditedInfo r15 = r11.videoEditedInfo     // Catch:{ Exception -> 0x0510 }
        L_0x0173:
            if (r34 != 0) goto L_0x01f6
            java.lang.CharSequence r4 = r11.editingMessage     // Catch:{ Exception -> 0x0510 }
            if (r4 == 0) goto L_0x01ac
            java.lang.CharSequence r4 = r11.editingMessage     // Catch:{ Exception -> 0x0510 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0510 }
            r5.message = r4     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r11.editingMessageEntities     // Catch:{ Exception -> 0x0510 }
            if (r4 == 0) goto L_0x018b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r11.editingMessageEntities     // Catch:{ Exception -> 0x0510 }
            r5.entities = r4     // Catch:{ Exception -> 0x0510 }
        L_0x0189:
            r1 = 0
            goto L_0x01a7
        L_0x018b:
            r4 = 1
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r4]     // Catch:{ Exception -> 0x0510 }
            java.lang.CharSequence r4 = r11.editingMessage     // Catch:{ Exception -> 0x0510 }
            r17 = 0
            r1[r17] = r4     // Catch:{ Exception -> 0x0510 }
            org.telegram.messenger.MediaDataController r4 = r27.getMediaDataController()     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList r1 = r4.getEntities(r1, r13)     // Catch:{ Exception -> 0x0510 }
            if (r1 == 0) goto L_0x0189
            boolean r4 = r1.isEmpty()     // Catch:{ Exception -> 0x0510 }
            if (r4 != 0) goto L_0x0189
            r5.entities = r1     // Catch:{ Exception -> 0x0510 }
            goto L_0x0189
        L_0x01a7:
            r11.caption = r1     // Catch:{ Exception -> 0x0510 }
            r28.generateCaption()     // Catch:{ Exception -> 0x0510 }
        L_0x01ac:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0510 }
            r1.<init>()     // Catch:{ Exception -> 0x0510 }
            r1.add(r5)     // Catch:{ Exception -> 0x0510 }
            org.telegram.messenger.MessagesStorage r20 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x0510 }
            r22 = 0
            r23 = 1
            r24 = 0
            r25 = 0
            boolean r4 = r11.scheduled     // Catch:{ Exception -> 0x0510 }
            r21 = r1
            r26 = r4
            r20.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r21, (boolean) r22, (boolean) r23, (boolean) r24, (int) r25, (boolean) r26)     // Catch:{ Exception -> 0x0510 }
            r1 = -1
            r11.type = r1     // Catch:{ Exception -> 0x0510 }
            r28.setType()     // Catch:{ Exception -> 0x0510 }
            r28.createMessageSendInfo()     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0510 }
            r1.<init>()     // Catch:{ Exception -> 0x0510 }
            r1.add(r11)     // Catch:{ Exception -> 0x0510 }
            org.telegram.messenger.NotificationCenter r4 = r27.getNotificationCenter()     // Catch:{ Exception -> 0x0510 }
            int r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects     // Catch:{ Exception -> 0x0510 }
            r16 = r13
            r31 = r15
            r13 = 2
            java.lang.Object[] r15 = new java.lang.Object[r13]     // Catch:{ Exception -> 0x0510 }
            java.lang.Long r13 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x0510 }
            r17 = 0
            r15[r17] = r13     // Catch:{ Exception -> 0x0510 }
            r13 = 1
            r15[r13] = r1     // Catch:{ Exception -> 0x0510 }
            r4.postNotificationName(r5, r15)     // Catch:{ Exception -> 0x0510 }
            goto L_0x01fa
        L_0x01f6:
            r16 = r13
            r31 = r15
        L_0x01fa:
            if (r6 == 0) goto L_0x020a
            boolean r1 = r6.containsKey(r3)     // Catch:{ Exception -> 0x0510 }
            if (r1 == 0) goto L_0x020a
            java.lang.Object r1 = r6.get(r3)     // Catch:{ Exception -> 0x0510 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0510 }
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
            if (r2 < r3) goto L_0x0517
            if (r2 > r1) goto L_0x0517
        L_0x0218:
            java.lang.String r3 = "masks"
            r20 = 0
            r5 = 2
            if (r2 != r5) goto L_0x02cd
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x0510 }
            r5.<init>()     // Catch:{ Exception -> 0x0510 }
            if (r6 == 0) goto L_0x025f
            java.lang.Object r3 = r6.get(r3)     // Catch:{ Exception -> 0x0510 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0510 }
            if (r3 == 0) goto L_0x025f
            org.telegram.tgnet.SerializedData r6 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0510 }
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x0510 }
            r6.<init>((byte[]) r3)     // Catch:{ Exception -> 0x0510 }
            r3 = 0
            int r13 = r6.readInt32(r3)     // Catch:{ Exception -> 0x0510 }
            r15 = 0
        L_0x023d:
            if (r15 >= r13) goto L_0x0256
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r5.stickers     // Catch:{ Exception -> 0x0510 }
            r29 = r13
            int r13 = r6.readInt32(r3)     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$InputDocument r13 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r6, r13, r3)     // Catch:{ Exception -> 0x0510 }
            r1.add(r13)     // Catch:{ Exception -> 0x0510 }
            int r15 = r15 + 1
            r13 = r29
            r1 = 8
            r3 = 0
            goto L_0x023d
        L_0x0256:
            int r1 = r5.flags     // Catch:{ Exception -> 0x0510 }
            r3 = 1
            r1 = r1 | r3
            r5.flags = r1     // Catch:{ Exception -> 0x0510 }
            r6.cleanup()     // Catch:{ Exception -> 0x0510 }
        L_0x025f:
            r13 = r2
            long r1 = r0.access_hash     // Catch:{ Exception -> 0x0510 }
            int r3 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1))
            if (r3 != 0) goto L_0x026c
            r1 = r5
            r29 = r13
            r15 = r14
            r2 = 1
            goto L_0x028f
        L_0x026c:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x0510 }
            r1.<init>()     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r2 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x0510 }
            r2.<init>()     // Catch:{ Exception -> 0x0510 }
            r1.id = r2     // Catch:{ Exception -> 0x0510 }
            r29 = r13
            r15 = r14
            long r13 = r0.id     // Catch:{ Exception -> 0x0510 }
            r2.id = r13     // Catch:{ Exception -> 0x0510 }
            long r13 = r0.access_hash     // Catch:{ Exception -> 0x0510 }
            r2.access_hash = r13     // Catch:{ Exception -> 0x0510 }
            byte[] r3 = r0.file_reference     // Catch:{ Exception -> 0x0510 }
            r2.file_reference = r3     // Catch:{ Exception -> 0x0510 }
            if (r3 != 0) goto L_0x028e
            r3 = 0
            byte[] r6 = new byte[r3]     // Catch:{ Exception -> 0x0510 }
            r2.file_reference = r6     // Catch:{ Exception -> 0x0510 }
        L_0x028e:
            r2 = 0
        L_0x028f:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0510 }
            r3.<init>(r7)     // Catch:{ Exception -> 0x0510 }
            r6 = 0
            r3.type = r6     // Catch:{ Exception -> 0x0510 }
            r3.obj = r11     // Catch:{ Exception -> 0x0510 }
            r3.originalPath = r4     // Catch:{ Exception -> 0x0510 }
            r3.parentObject = r9     // Catch:{ Exception -> 0x0510 }
            r3.inputUploadMedia = r5     // Catch:{ Exception -> 0x0510 }
            r3.performMediaUpload = r2     // Catch:{ Exception -> 0x0510 }
            if (r12 == 0) goto L_0x02b3
            int r5 = r12.length()     // Catch:{ Exception -> 0x0510 }
            if (r5 <= 0) goto L_0x02b3
            r5 = r15
            boolean r5 = r12.startsWith(r5)     // Catch:{ Exception -> 0x0510 }
            if (r5 == 0) goto L_0x02b3
            r3.httpLocation = r12     // Catch:{ Exception -> 0x0510 }
            goto L_0x02c7
        L_0x02b3:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r0.sizes     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes     // Catch:{ Exception -> 0x0510 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0510 }
            r7 = 1
            int r6 = r6 - r7
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5     // Catch:{ Exception -> 0x0510 }
            r3.photoSize = r5     // Catch:{ Exception -> 0x0510 }
            r3.locationParent = r0     // Catch:{ Exception -> 0x0510 }
        L_0x02c7:
            r5 = r2
            r0 = r3
            r2 = r29
            goto L_0x03ef
        L_0x02cd:
            r0 = 3
            if (r2 != r0) goto L_0x0387
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0510 }
            r0.<init>()     // Catch:{ Exception -> 0x0510 }
            if (r6 == 0) goto L_0x030a
            java.lang.Object r1 = r6.get(r3)     // Catch:{ Exception -> 0x0510 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0510 }
            if (r1 == 0) goto L_0x030a
            org.telegram.tgnet.SerializedData r3 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0510 }
            byte[] r1 = org.telegram.messenger.Utilities.hexToBytes(r1)     // Catch:{ Exception -> 0x0510 }
            r3.<init>((byte[]) r1)     // Catch:{ Exception -> 0x0510 }
            r1 = 0
            int r5 = r3.readInt32(r1)     // Catch:{ Exception -> 0x0510 }
            r6 = 0
        L_0x02ee:
            if (r6 >= r5) goto L_0x0301
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r12 = r0.stickers     // Catch:{ Exception -> 0x0510 }
            int r13 = r3.readInt32(r1)     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$InputDocument r13 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r3, r13, r1)     // Catch:{ Exception -> 0x0510 }
            r12.add(r13)     // Catch:{ Exception -> 0x0510 }
            int r6 = r6 + 1
            r1 = 0
            goto L_0x02ee
        L_0x0301:
            int r1 = r0.flags     // Catch:{ Exception -> 0x0510 }
            r5 = 1
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0510 }
            r3.cleanup()     // Catch:{ Exception -> 0x0510 }
        L_0x030a:
            r1 = r29
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x0510 }
            r0.mime_type = r3     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0510 }
            r0.attributes = r3     // Catch:{ Exception -> 0x0510 }
            boolean r3 = r28.isGif()     // Catch:{ Exception -> 0x0510 }
            if (r3 != 0) goto L_0x0332
            if (r31 == 0) goto L_0x0323
            r15 = r31
            boolean r3 = r15.muted     // Catch:{ Exception -> 0x0510 }
            if (r3 != 0) goto L_0x0334
            goto L_0x0325
        L_0x0323:
            r15 = r31
        L_0x0325:
            r3 = 1
            r0.nosound_video = r3     // Catch:{ Exception -> 0x0510 }
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0510 }
            if (r3 == 0) goto L_0x0334
            java.lang.String r3 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0334
        L_0x0332:
            r15 = r31
        L_0x0334:
            long r5 = r1.access_hash     // Catch:{ Exception -> 0x0510 }
            int r3 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1))
            if (r3 != 0) goto L_0x033d
            r3 = r0
            r5 = 1
            goto L_0x035d
        L_0x033d:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0510 }
            r3.<init>()     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0510 }
            r5.<init>()     // Catch:{ Exception -> 0x0510 }
            r3.id = r5     // Catch:{ Exception -> 0x0510 }
            long r12 = r1.id     // Catch:{ Exception -> 0x0510 }
            r5.id = r12     // Catch:{ Exception -> 0x0510 }
            long r12 = r1.access_hash     // Catch:{ Exception -> 0x0510 }
            r5.access_hash = r12     // Catch:{ Exception -> 0x0510 }
            byte[] r6 = r1.file_reference     // Catch:{ Exception -> 0x0510 }
            r5.file_reference = r6     // Catch:{ Exception -> 0x0510 }
            if (r6 != 0) goto L_0x035c
            r6 = 0
            byte[] r12 = new byte[r6]     // Catch:{ Exception -> 0x0510 }
            r5.file_reference = r12     // Catch:{ Exception -> 0x0510 }
        L_0x035c:
            r5 = 0
        L_0x035d:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0510 }
            r6.<init>(r7)     // Catch:{ Exception -> 0x0510 }
            r7 = 1
            r6.type = r7     // Catch:{ Exception -> 0x0510 }
            r6.obj = r11     // Catch:{ Exception -> 0x0510 }
            r6.originalPath = r4     // Catch:{ Exception -> 0x0510 }
            r6.parentObject = r9     // Catch:{ Exception -> 0x0510 }
            r6.inputUploadMedia = r0     // Catch:{ Exception -> 0x0510 }
            r6.performMediaUpload = r5     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0510 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0510 }
            if (r0 != 0) goto L_0x0384
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0510 }
            r7 = 0
            java.lang.Object r0 = r0.get(r7)     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0     // Catch:{ Exception -> 0x0510 }
            r6.photoSize = r0     // Catch:{ Exception -> 0x0510 }
            r6.locationParent = r1     // Catch:{ Exception -> 0x0510 }
        L_0x0384:
            r6.videoEditedInfo = r15     // Catch:{ Exception -> 0x0510 }
            goto L_0x03e9
        L_0x0387:
            r1 = r29
            r0 = 7
            if (r2 != r0) goto L_0x03ec
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0510 }
            r0.<init>()     // Catch:{ Exception -> 0x0510 }
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x0510 }
            r0.mime_type = r3     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0510 }
            r0.attributes = r3     // Catch:{ Exception -> 0x0510 }
            long r5 = r1.access_hash     // Catch:{ Exception -> 0x0510 }
            int r3 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1))
            if (r3 != 0) goto L_0x03a2
            r3 = r0
            r5 = 1
            goto L_0x03c2
        L_0x03a2:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0510 }
            r3.<init>()     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0510 }
            r5.<init>()     // Catch:{ Exception -> 0x0510 }
            r3.id = r5     // Catch:{ Exception -> 0x0510 }
            long r12 = r1.id     // Catch:{ Exception -> 0x0510 }
            r5.id = r12     // Catch:{ Exception -> 0x0510 }
            long r12 = r1.access_hash     // Catch:{ Exception -> 0x0510 }
            r5.access_hash = r12     // Catch:{ Exception -> 0x0510 }
            byte[] r6 = r1.file_reference     // Catch:{ Exception -> 0x0510 }
            r5.file_reference = r6     // Catch:{ Exception -> 0x0510 }
            if (r6 != 0) goto L_0x03c1
            r6 = 0
            byte[] r12 = new byte[r6]     // Catch:{ Exception -> 0x0510 }
            r5.file_reference = r12     // Catch:{ Exception -> 0x0510 }
        L_0x03c1:
            r5 = 0
        L_0x03c2:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0510 }
            r6.<init>(r7)     // Catch:{ Exception -> 0x0510 }
            r6.originalPath = r4     // Catch:{ Exception -> 0x0510 }
            r7 = 2
            r6.type = r7     // Catch:{ Exception -> 0x0510 }
            r6.obj = r11     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x0510 }
            boolean r7 = r7.isEmpty()     // Catch:{ Exception -> 0x0510 }
            if (r7 != 0) goto L_0x03e3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x0510 }
            r8 = 0
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0510 }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC$PhotoSize) r7     // Catch:{ Exception -> 0x0510 }
            r6.photoSize = r7     // Catch:{ Exception -> 0x0510 }
            r6.locationParent = r1     // Catch:{ Exception -> 0x0510 }
        L_0x03e3:
            r6.parentObject = r9     // Catch:{ Exception -> 0x0510 }
            r6.inputUploadMedia = r0     // Catch:{ Exception -> 0x0510 }
            r6.performMediaUpload = r5     // Catch:{ Exception -> 0x0510 }
        L_0x03e9:
            r1 = r3
            r0 = r6
            goto L_0x03ef
        L_0x03ec:
            r0 = 0
            r1 = 0
            r5 = 0
        L_0x03ef:
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r3 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage     // Catch:{ Exception -> 0x0510 }
            r3.<init>()     // Catch:{ Exception -> 0x0510 }
            int r6 = r28.getId()     // Catch:{ Exception -> 0x0510 }
            r3.id = r6     // Catch:{ Exception -> 0x0510 }
            org.telegram.messenger.MessagesController r6 = r27.getMessagesController()     // Catch:{ Exception -> 0x0510 }
            r7 = r19
            org.telegram.tgnet.TLRPC$InputPeer r6 = r6.getInputPeer((int) r7)     // Catch:{ Exception -> 0x0510 }
            r3.peer = r6     // Catch:{ Exception -> 0x0510 }
            int r6 = r3.flags     // Catch:{ Exception -> 0x0510 }
            r6 = r6 | 16384(0x4000, float:2.2959E-41)
            r3.flags = r6     // Catch:{ Exception -> 0x0510 }
            r3.media = r1     // Catch:{ Exception -> 0x0510 }
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0510 }
            if (r1 == 0) goto L_0x041e
            org.telegram.tgnet.TLRPC$Message r1 = r11.messageOwner     // Catch:{ Exception -> 0x0510 }
            int r1 = r1.date     // Catch:{ Exception -> 0x0510 }
            r3.schedule_date = r1     // Catch:{ Exception -> 0x0510 }
            r1 = 32768(0x8000, float:4.5918E-41)
            r1 = r1 | r6
            r3.flags = r1     // Catch:{ Exception -> 0x0510 }
        L_0x041e:
            java.lang.CharSequence r1 = r11.editingMessage     // Catch:{ Exception -> 0x0510 }
            if (r1 == 0) goto L_0x0467
            java.lang.CharSequence r1 = r11.editingMessage     // Catch:{ Exception -> 0x0510 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0510 }
            r3.message = r1     // Catch:{ Exception -> 0x0510 }
            int r1 = r3.flags     // Catch:{ Exception -> 0x0510 }
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r3.flags = r1     // Catch:{ Exception -> 0x0510 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r11.editingMessageEntities     // Catch:{ Exception -> 0x0510 }
            if (r6 == 0) goto L_0x043f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r11.editingMessageEntities     // Catch:{ Exception -> 0x0510 }
            r3.entities = r6     // Catch:{ Exception -> 0x0510 }
            r6 = 8
            r1 = r1 | r6
            r3.flags = r1     // Catch:{ Exception -> 0x0510 }
        L_0x043d:
            r1 = 0
            goto L_0x0463
        L_0x043f:
            r1 = 1
            java.lang.CharSequence[] r6 = new java.lang.CharSequence[r1]     // Catch:{ Exception -> 0x0510 }
            java.lang.CharSequence r1 = r11.editingMessage     // Catch:{ Exception -> 0x0510 }
            r7 = 0
            r6[r7] = r1     // Catch:{ Exception -> 0x0510 }
            org.telegram.messenger.MediaDataController r1 = r27.getMediaDataController()     // Catch:{ Exception -> 0x0510 }
            r12 = r16
            java.util.ArrayList r1 = r1.getEntities(r6, r12)     // Catch:{ Exception -> 0x0510 }
            if (r1 == 0) goto L_0x043d
            boolean r6 = r1.isEmpty()     // Catch:{ Exception -> 0x0510 }
            if (r6 != 0) goto L_0x043d
            r3.entities = r1     // Catch:{ Exception -> 0x0510 }
            int r1 = r3.flags     // Catch:{ Exception -> 0x0510 }
            r6 = 8
            r1 = r1 | r6
            r3.flags = r1     // Catch:{ Exception -> 0x0510 }
            goto L_0x043d
        L_0x0463:
            r11.editingMessage = r1     // Catch:{ Exception -> 0x0510 }
            r11.editingMessageEntities = r1     // Catch:{ Exception -> 0x0510 }
        L_0x0467:
            if (r0 == 0) goto L_0x046b
            r0.sendRequest = r3     // Catch:{ Exception -> 0x0510 }
        L_0x046b:
            r1 = 1
            if (r2 != r1) goto L_0x0484
            r1 = 0
            boolean r2 = r11.scheduled     // Catch:{ Exception -> 0x0510 }
            r29 = r27
            r30 = r3
            r31 = r28
            r32 = r1
            r33 = r0
            r34 = r9
            r35 = r2
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x0484:
            r1 = 2
            if (r2 != r1) goto L_0x049f
            if (r5 == 0) goto L_0x048e
            r10.performSendDelayedMessage(r0)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x048e:
            r5 = 0
            r6 = 1
            boolean r12 = r11.scheduled     // Catch:{ Exception -> 0x0510 }
            r1 = r27
            r2 = r3
            r3 = r28
            r7 = r0
            r8 = r9
            r9 = r12
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x049f:
            r1 = 3
            if (r2 != r1) goto L_0x04be
            if (r5 == 0) goto L_0x04a9
            r10.performSendDelayedMessage(r0)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x04a9:
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0510 }
            r29 = r27
            r30 = r3
            r31 = r28
            r32 = r4
            r33 = r0
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x04be:
            r1 = 6
            if (r2 != r1) goto L_0x04d5
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0510 }
            r29 = r27
            r30 = r3
            r31 = r28
            r32 = r4
            r33 = r0
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x04d5:
            r1 = 7
            if (r2 != r1) goto L_0x04f2
            if (r5 == 0) goto L_0x04de
            r10.performSendDelayedMessage(r0)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x04de:
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0510 }
            r29 = r27
            r30 = r3
            r31 = r28
            r32 = r4
            r33 = r0
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x04f2:
            r1 = 8
            if (r2 != r1) goto L_0x0517
            if (r5 == 0) goto L_0x04fc
            r10.performSendDelayedMessage(r0)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x04fc:
            boolean r1 = r11.scheduled     // Catch:{ Exception -> 0x0510 }
            r29 = r27
            r30 = r3
            r31 = r28
            r32 = r4
            r33 = r0
            r34 = r9
            r35 = r1
            r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0510 }
            goto L_0x0517
        L_0x0510:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r27.revertEditingMessageObject(r28)
        L_0x0517:
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
            public final /* synthetic */ BaseFragment f$1;
            public final /* synthetic */ TLRPC$TL_messages_editMessage f$2;
            public final /* synthetic */ Runnable f$3;

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
                public final /* synthetic */ TLRPC$TL_error f$1;
                public final /* synthetic */ BaseFragment f$2;
                public final /* synthetic */ TLRPC$TL_messages_editMessage f$3;

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
            sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaGeo, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
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
            public final /* synthetic */ long f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ byte[] f$3;

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
        String str = j + "_" + i2 + "_" + Utilities.bytesToHex(bArr) + "_" + 0;
        this.waitingForCallback.put(str, Boolean.TRUE);
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
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$null$18$SendMessagesHelper(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 2);
        getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, 0, true, 0);
    }

    public /* synthetic */ void lambda$null$17$SendMessagesHelper(String str) {
        Boolean remove = this.waitingForCallback.remove(str);
    }

    public /* synthetic */ void lambda$null$18$SendMessagesHelper(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str) {
            public final /* synthetic */ String f$1;

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
            public final /* synthetic */ MessageObject f$1;
            public final /* synthetic */ String f$2;
            public final /* synthetic */ Runnable f$3;

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
            public final /* synthetic */ String f$1;
            public final /* synthetic */ Runnable f$2;

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

    public void sendCallback(boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity) {
        lambda$null$23$SendMessagesHelper(z, messageObject, tLRPC$KeyboardButton, (TLRPC$InputCheckPasswordSRP) null, (TwoStepVerificationActivity) null, chatActivity);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0089  */
    /* renamed from: sendCallback */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$null$23$SendMessagesHelper(boolean r19, org.telegram.messenger.MessageObject r20, org.telegram.tgnet.TLRPC$KeyboardButton r21, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP r22, org.telegram.ui.TwoStepVerificationActivity r23, org.telegram.ui.ChatActivity r24) {
        /*
            r18 = this;
            r11 = r20
            r12 = r21
            if (r11 == 0) goto L_0x013a
            if (r12 == 0) goto L_0x013a
            if (r24 != 0) goto L_0x000c
            goto L_0x013a
        L_0x000c:
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r14 = 1
            r10 = 2
            if (r13 == 0) goto L_0x0016
            r0 = 3
        L_0x0013:
            r16 = 0
            goto L_0x0027
        L_0x0016:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            if (r0 == 0) goto L_0x001c
            r0 = 1
            goto L_0x0013
        L_0x001c:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r0 == 0) goto L_0x0024
            r16 = r19
            r0 = 2
            goto L_0x0027
        L_0x0024:
            r16 = r19
            r0 = 0
        L_0x0027:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            long r2 = r20.getDialogId()
            r1.append(r2)
            java.lang.String r2 = "_"
            r1.append(r2)
            int r3 = r20.getId()
            r1.append(r3)
            r1.append(r2)
            byte[] r3 = r12.data
            java.lang.String r3 = org.telegram.messenger.Utilities.bytesToHex(r3)
            r1.append(r3)
            r1.append(r2)
            r1.append(r0)
            java.lang.String r9 = r1.toString()
            r8 = r18
            java.util.HashMap<java.lang.String, java.lang.Boolean> r0 = r8.waitingForCallback
            java.lang.Boolean r1 = java.lang.Boolean.TRUE
            r0.put(r9, r1)
            org.telegram.tgnet.TLObject[] r7 = new org.telegram.tgnet.TLObject[r14]
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$ghKIMmoOdRB_6YGrH0ZGJuFwDG8 r6 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$ghKIMmoOdRB_6YGrH0ZGJuFwDG8
            r0 = r6
            r1 = r18
            r2 = r9
            r3 = r16
            r4 = r20
            r5 = r21
            r14 = r6
            r6 = r24
            r17 = r7
            r7 = r23
            r8 = r17
            r15 = r9
            r9 = r22
            r11 = 2
            r10 = r19
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            if (r16 == 0) goto L_0x0089
            org.telegram.messenger.MessagesStorage r0 = r18.getMessagesStorage()
            r0.getBotCache(r15, r14)
            goto L_0x013a
        L_0x0089:
            if (r13 == 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth r0 = new org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth
            r0.<init>()
            org.telegram.messenger.MessagesController r1 = r18.getMessagesController()
            long r2 = r20.getDialogId()
            int r3 = (int) r2
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer((int) r3)
            r0.peer = r1
            int r1 = r20.getId()
            r0.msg_id = r1
            int r1 = r12.button_id
            r0.button_id = r1
            r1 = 0
            r17[r1] = r0
            org.telegram.tgnet.ConnectionsManager r1 = r18.getConnectionsManager()
            r1.sendRequest(r0, r14, r11)
            goto L_0x013a
        L_0x00b5:
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r0 == 0) goto L_0x00ee
            r0 = r20
            r1 = 2
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            int r2 = r2.flags
            r2 = r2 & 4
            if (r2 != 0) goto L_0x00d9
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm r2 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm
            r2.<init>()
            int r0 = r20.getId()
            r2.msg_id = r0
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r2, r14, r1)
            goto L_0x013a
        L_0x00d9:
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentReceipt r2 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentReceipt
            r2.<init>()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.receipt_msg_id
            r2.msg_id = r0
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r2, r14, r1)
            goto L_0x013a
        L_0x00ee:
            r0 = r20
            r1 = 2
            org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer r2 = new org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer
            r2.<init>()
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            long r4 = r20.getDialogId()
            int r5 = (int) r4
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((int) r5)
            r2.peer = r3
            int r0 = r20.getId()
            r2.msg_id = r0
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            r2.game = r0
            boolean r0 = r12.requires_password
            if (r0 == 0) goto L_0x0127
            if (r22 == 0) goto L_0x0118
            r0 = r22
            goto L_0x011d
        L_0x0118:
            org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty
            r0.<init>()
        L_0x011d:
            r2.password = r0
            r2.password = r0
            int r0 = r2.flags
            r0 = r0 | 4
            r2.flags = r0
        L_0x0127:
            byte[] r0 = r12.data
            if (r0 == 0) goto L_0x0133
            int r3 = r2.flags
            r4 = 1
            r3 = r3 | r4
            r2.flags = r3
            r2.data = r0
        L_0x0133:
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r2, r14, r1)
        L_0x013a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$23$SendMessagesHelper(boolean, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP, org.telegram.ui.TwoStepVerificationActivity, org.telegram.ui.ChatActivity):void");
    }

    public /* synthetic */ void lambda$sendCallback$29$SendMessagesHelper(String str, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, TwoStepVerificationActivity twoStepVerificationActivity, TLObject[] tLObjectArr, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, boolean z2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, z, tLObject, messageObject, tLRPC$KeyboardButton, chatActivity, twoStepVerificationActivity, tLObjectArr, tLRPC$TL_error, tLRPC$InputCheckPasswordSRP, z2) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ TLRPC$InputCheckPasswordSRP f$10;
            public final /* synthetic */ boolean f$11;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ MessageObject f$4;
            public final /* synthetic */ TLRPC$KeyboardButton f$5;
            public final /* synthetic */ ChatActivity f$6;
            public final /* synthetic */ TwoStepVerificationActivity f$7;
            public final /* synthetic */ TLObject[] f$8;
            public final /* synthetic */ TLRPC$TL_error f$9;

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
                this.f$10 = r11;
                this.f$11 = r12;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$28$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0170, code lost:
        if (org.telegram.messenger.MessagesController.getNotificationsSettings(r7.currentAccount).getBoolean("askgame_" + r0, true) != false) goto L_0x0174;
     */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x010c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$28$SendMessagesHelper(java.lang.String r28, boolean r29, org.telegram.tgnet.TLObject r30, org.telegram.messenger.MessageObject r31, org.telegram.tgnet.TLRPC$KeyboardButton r32, org.telegram.ui.ChatActivity r33, org.telegram.ui.TwoStepVerificationActivity r34, org.telegram.tgnet.TLObject[] r35, org.telegram.tgnet.TLRPC$TL_error r36, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP r37, boolean r38) {
        /*
            r27 = this;
            r7 = r27
            r0 = r28
            r1 = r30
            r4 = r31
            r5 = r32
            r6 = r33
            r2 = r36
            java.util.HashMap<java.lang.String, java.lang.Boolean> r3 = r7.waitingForCallback
            r3.remove(r0)
            r3 = 0
            if (r29 == 0) goto L_0x001d
            if (r1 != 0) goto L_0x001d
            r7.sendCallback(r3, r4, r5, r6)
            goto L_0x04a5
        L_0x001d:
            java.lang.String r8 = "OK"
            r9 = 2131626177(0x7f0e08c1, float:1.8879583E38)
            r10 = 0
            r11 = 1
            if (r1 == 0) goto L_0x018c
            if (r34 == 0) goto L_0x002e
            r34.needHideProgress()
            r34.finishFragment()
        L_0x002e:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            if (r2 == 0) goto L_0x0060
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest
            if (r0 == 0) goto L_0x0044
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest r0 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest) r0
            r1 = r35[r3]
            org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth r1 = (org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth) r1
            java.lang.String r2 = r5.url
            r6.showRequestUrlAlert(r0, r1, r2)
            goto L_0x04a5
        L_0x0044:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted
            if (r0 == 0) goto L_0x0052
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted r0 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted) r0
            java.lang.String r0 = r0.url
            org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r6, r0, r3, r3)
            goto L_0x04a5
        L_0x0052:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault
            if (r0 == 0) goto L_0x04a5
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault r0 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault) r0
            java.lang.String r0 = r5.url
            org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r6, r0, r3, r11)
            goto L_0x04a5
        L_0x0060:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r2 == 0) goto L_0x008e
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_payments_paymentForm
            if (r0 == 0) goto L_0x007e
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = (org.telegram.tgnet.TLRPC$TL_payments_paymentForm) r0
            org.telegram.messenger.MessagesController r1 = r27.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r0.users
            r1.putUsers(r2, r3)
            org.telegram.ui.PaymentFormActivity r1 = new org.telegram.ui.PaymentFormActivity
            r1.<init>((org.telegram.tgnet.TLRPC$TL_payments_paymentForm) r0, (org.telegram.messenger.MessageObject) r4)
            r6.presentFragment(r1)
            goto L_0x04a5
        L_0x007e:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt
            if (r0 == 0) goto L_0x04a5
            org.telegram.ui.PaymentFormActivity r0 = new org.telegram.ui.PaymentFormActivity
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r1 = (org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt) r1
            r0.<init>((org.telegram.messenger.MessageObject) r4, (org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt) r1)
            r6.presentFragment(r0)
            goto L_0x04a5
        L_0x008e:
            org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer r1 = (org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer) r1
            if (r29 != 0) goto L_0x00a1
            int r2 = r1.cache_time
            if (r2 == 0) goto L_0x00a1
            boolean r2 = r5.requires_password
            if (r2 != 0) goto L_0x00a1
            org.telegram.messenger.MessagesStorage r2 = r27.getMessagesStorage()
            r2.saveBotCache(r0, r1)
        L_0x00a1:
            java.lang.String r0 = r1.message
            if (r0 == 0) goto L_0x0113
            int r0 = r31.getFromChatId()
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
            int r2 = r2.via_bot_id
            if (r2 == 0) goto L_0x00b0
            r0 = r2
        L_0x00b0:
            if (r0 <= 0) goto L_0x00c9
            org.telegram.messenger.MessagesController r2 = r27.getMessagesController()
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            if (r0 == 0) goto L_0x00db
            java.lang.String r2 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)
            goto L_0x00dc
        L_0x00c9:
            org.telegram.messenger.MessagesController r2 = r27.getMessagesController()
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            if (r0 == 0) goto L_0x00db
            java.lang.String r0 = r0.title
            goto L_0x00dc
        L_0x00db:
            r0 = r10
        L_0x00dc:
            if (r0 != 0) goto L_0x00e0
            java.lang.String r0 = "bot"
        L_0x00e0:
            boolean r2 = r1.alert
            if (r2 == 0) goto L_0x010c
            android.app.Activity r2 = r33.getParentActivity()
            if (r2 != 0) goto L_0x00eb
            return
        L_0x00eb:
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r33.getParentActivity()
            r2.<init>((android.content.Context) r3)
            r2.setTitle(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r8, r9)
            r2.setPositiveButton(r0, r10)
            java.lang.String r0 = r1.message
            r2.setMessage(r0)
            org.telegram.ui.ActionBar.AlertDialog r0 = r2.create()
            r6.showDialog(r0)
            goto L_0x04a5
        L_0x010c:
            java.lang.String r1 = r1.message
            r6.showAlert(r0, r1)
            goto L_0x04a5
        L_0x0113:
            java.lang.String r0 = r1.url
            if (r0 == 0) goto L_0x04a5
            android.app.Activity r0 = r33.getParentActivity()
            if (r0 != 0) goto L_0x011e
            return
        L_0x011e:
            int r0 = r31.getFromChatId()
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
            int r2 = r2.via_bot_id
            if (r2 == 0) goto L_0x0129
            r0 = r2
        L_0x0129:
            org.telegram.messenger.MessagesController r2 = r27.getMessagesController()
            java.lang.Integer r8 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r8)
            if (r2 == 0) goto L_0x013d
            boolean r2 = r2.verified
            if (r2 == 0) goto L_0x013d
            r2 = 1
            goto L_0x013e
        L_0x013d:
            r2 = 0
        L_0x013e:
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            if (r5 == 0) goto L_0x0185
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x014d
            org.telegram.tgnet.TLRPC$TL_game r5 = r5.game
            goto L_0x014e
        L_0x014d:
            r5 = r10
        L_0x014e:
            if (r5 != 0) goto L_0x0151
            return
        L_0x0151:
            java.lang.String r1 = r1.url
            if (r2 != 0) goto L_0x0173
            int r2 = r7.currentAccount
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "askgame_"
            r8.append(r9)
            r8.append(r0)
            java.lang.String r8 = r8.toString()
            boolean r2 = r2.getBoolean(r8, r11)
            if (r2 == 0) goto L_0x0173
            goto L_0x0174
        L_0x0173:
            r11 = 0
        L_0x0174:
            r32 = r33
            r33 = r5
            r34 = r31
            r35 = r1
            r36 = r11
            r37 = r0
            r32.showOpenGameAlert(r33, r34, r35, r36, r37)
            goto L_0x04a5
        L_0x0185:
            java.lang.String r0 = r1.url
            org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r6, r0, r3, r3)
            goto L_0x04a5
        L_0x018c:
            if (r2 == 0) goto L_0x04a5
            android.app.Activity r0 = r33.getParentActivity()
            if (r0 != 0) goto L_0x0195
            return
        L_0x0195:
            java.lang.String r0 = r2.text
            java.lang.String r1 = "PASSWORD_HASH_INVALID"
            boolean r0 = r1.equals(r0)
            java.lang.String r12 = "Cancel"
            r13 = 2131624561(0x7f0e0271, float:1.8876305E38)
            if (r0 == 0) goto L_0x01f9
            if (r37 != 0) goto L_0x04a5
            org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r33.getParentActivity()
            r8.<init>((android.content.Context) r0)
            r0 = 2131624509(0x7f0e023d, float:1.88762E38)
            java.lang.String r1 = "BotOwnershipTransfer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r8.setTitle(r0)
            r0 = 2131624512(0x7f0e0240, float:1.8876206E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = "BotOwnershipTransferReadyAlertText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setMessage(r0)
            r0 = 2131624511(0x7f0e023f, float:1.8876204E38)
            java.lang.String r1 = "BotOwnershipTransferChangeOwner"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$cK4IEUZY3Zlz3ieaP588YKacV6I r11 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$cK4IEUZY3Zlz3ieaP588YKacV6I
            r0 = r11
            r1 = r27
            r2 = r38
            r3 = r31
            r4 = r32
            r5 = r33
            r0.<init>(r2, r3, r4, r5)
            r8.setPositiveButton(r9, r11)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r13)
            r8.setNegativeButton(r0, r10)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.create()
            r6.showDialog(r0)
            goto L_0x04a5
        L_0x01f9:
            java.lang.String r0 = r2.text
            java.lang.String r1 = "PASSWORD_MISSING"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0250
            java.lang.String r0 = r2.text
            java.lang.String r14 = "PASSWORD_TOO_FRESH_"
            boolean r0 = r0.startsWith(r14)
            if (r0 != 0) goto L_0x0250
            java.lang.String r0 = r2.text
            java.lang.String r14 = "SESSION_TOO_FRESH_"
            boolean r0 = r0.startsWith(r14)
            if (r0 == 0) goto L_0x0218
            goto L_0x0250
        L_0x0218:
            java.lang.String r0 = r2.text
            java.lang.String r1 = "SRP_ID_INVALID"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0246
            org.telegram.tgnet.TLRPC$TL_account_getPassword r8 = new org.telegram.tgnet.TLRPC$TL_account_getPassword
            r8.<init>()
            int r0 = r7.currentAccount
            org.telegram.tgnet.ConnectionsManager r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$HDikfPQ_fbkEFD6LxkSxh1rYwn4 r10 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$HDikfPQ_fbkEFD6LxkSxh1rYwn4
            r0 = r10
            r1 = r27
            r2 = r34
            r3 = r38
            r4 = r31
            r5 = r32
            r6 = r33
            r0.<init>(r2, r3, r4, r5, r6)
            r0 = 8
            r9.sendRequest(r8, r10, r0)
            goto L_0x04a5
        L_0x0246:
            if (r34 == 0) goto L_0x04a5
            r34.needHideProgress()
            r34.finishFragment()
            goto L_0x04a5
        L_0x0250:
            if (r34 == 0) goto L_0x0255
            r34.needHideProgress()
        L_0x0255:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r33.getParentActivity()
            r0.<init>((android.content.Context) r4)
            r4 = 2131625117(0x7f0e049d, float:1.8877433E38)
            java.lang.String r5 = "EditAdminTransferAlertTitle"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTitle(r4)
            android.widget.LinearLayout r4 = new android.widget.LinearLayout
            android.app.Activity r5 = r33.getParentActivity()
            r4.<init>(r5)
            r5 = 1103101952(0x41CLASSNAME, float:24.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r15 = 1073741824(0x40000000, float:2.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r4.setPadding(r14, r15, r5, r3)
            r4.setOrientation(r11)
            r0.setView(r4)
            android.widget.TextView r5 = new android.widget.TextView
            android.app.Activity r14 = r33.getParentActivity()
            r5.<init>(r14)
            java.lang.String r14 = "dialogTextBlack"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r5.setTextColor(r15)
            r15 = 1098907648(0x41800000, float:16.0)
            r5.setTextSize(r11, r15)
            boolean r16 = org.telegram.messenger.LocaleController.isRTL
            r17 = 3
            if (r16 == 0) goto L_0x02ac
            r16 = 5
            goto L_0x02ae
        L_0x02ac:
            r16 = 3
        L_0x02ae:
            r10 = r16 | 48
            r5.setGravity(r10)
            r10 = 2131624510(0x7f0e023e, float:1.8876202E38)
            java.lang.Object[] r13 = new java.lang.Object[r3]
            java.lang.String r9 = "BotOwnershipTransferAlertText"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r9, r10, r13)
            android.text.SpannableStringBuilder r9 = org.telegram.messenger.AndroidUtilities.replaceTags(r9)
            r5.setText(r9)
            r9 = -1
            r10 = -2
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r10)
            r4.addView(r5, r13)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            android.app.Activity r13 = r33.getParentActivity()
            r5.<init>(r13)
            r5.setOrientation(r3)
            r18 = -1
            r19 = -2
            r20 = 0
            r21 = 1093664768(0x41300000, float:11.0)
            r22 = 0
            r23 = 0
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r18, r19, r20, r21, r22, r23)
            r4.addView(r5, r13)
            android.widget.ImageView r13 = new android.widget.ImageView
            android.app.Activity r9 = r33.getParentActivity()
            r13.<init>(r9)
            r9 = 2131165571(0x7var_, float:1.7945363E38)
            r13.setImageResource(r9)
            boolean r16 = org.telegram.messenger.LocaleController.isRTL
            r18 = 1093664768(0x41300000, float:11.0)
            if (r16 == 0) goto L_0x0309
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r9 = r16
            goto L_0x030a
        L_0x0309:
            r9 = 0
        L_0x030a:
            r16 = 1091567616(0x41100000, float:9.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r19 = org.telegram.messenger.LocaleController.isRTL
            if (r19 == 0) goto L_0x0316
            r11 = 0
            goto L_0x031c
        L_0x0316:
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11 = r19
        L_0x031c:
            r13.setPadding(r9, r10, r11, r3)
            android.graphics.PorterDuffColorFilter r9 = new android.graphics.PorterDuffColorFilter
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r9.<init>(r10, r11)
            r13.setColorFilter(r9)
            android.widget.TextView r9 = new android.widget.TextView
            android.app.Activity r10 = r33.getParentActivity()
            r9.<init>(r10)
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r9.setTextColor(r10)
            r10 = 1
            r9.setTextSize(r10, r15)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0347
            r10 = 5
            goto L_0x0348
        L_0x0347:
            r10 = 3
        L_0x0348:
            r10 = r10 | 48
            r9.setGravity(r10)
            r10 = 2131625114(0x7f0e049a, float:1.8877427E38)
            java.lang.String r11 = "EditAdminTransferAlertText1"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.replaceTags(r10)
            r9.setText(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0373
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r5.addView(r9, r15)
            r9 = 5
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r10, (int) r9)
            r5.addView(r13, r15)
            goto L_0x0383
        L_0x0373:
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r10)
            r5.addView(r13, r15)
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r5.addView(r9, r13)
        L_0x0383:
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            android.app.Activity r9 = r33.getParentActivity()
            r5.<init>(r9)
            r5.setOrientation(r3)
            r21 = -1
            r22 = -2
            r23 = 0
            r24 = 1093664768(0x41300000, float:11.0)
            r25 = 0
            r26 = 0
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r21, r22, r23, r24, r25, r26)
            r4.addView(r5, r9)
            android.widget.ImageView r9 = new android.widget.ImageView
            android.app.Activity r10 = r33.getParentActivity()
            r9.<init>(r10)
            r10 = 2131165571(0x7var_, float:1.7945363E38)
            r9.setImageResource(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x03ba
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            goto L_0x03bb
        L_0x03ba:
            r10 = 0
        L_0x03bb:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r13 = org.telegram.messenger.LocaleController.isRTL
            if (r13 == 0) goto L_0x03c5
            r13 = 0
            goto L_0x03c9
        L_0x03c5:
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r18)
        L_0x03c9:
            r9.setPadding(r10, r11, r13, r3)
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r10, r11)
            r9.setColorFilter(r3)
            android.widget.TextView r3 = new android.widget.TextView
            android.app.Activity r10 = r33.getParentActivity()
            r3.<init>(r10)
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r3.setTextColor(r10)
            r10 = 1098907648(0x41800000, float:16.0)
            r11 = 1
            r3.setTextSize(r11, r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x03f6
            r10 = 5
            goto L_0x03f7
        L_0x03f6:
            r10 = 3
        L_0x03f7:
            r10 = r10 | 48
            r3.setGravity(r10)
            r10 = 2131625115(0x7f0e049b, float:1.8877429E38)
            java.lang.String r11 = "EditAdminTransferAlertText2"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.replaceTags(r10)
            r3.setText(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0422
            r10 = -2
            r11 = -1
            android.widget.LinearLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r5.addView(r3, r11)
            r13 = 5
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r10, (int) r13)
            r5.addView(r9, r3)
            goto L_0x0433
        L_0x0422:
            r10 = -2
            r11 = -1
            r13 = 5
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r10)
            r5.addView(r9, r15)
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r5.addView(r3, r9)
        L_0x0433:
            java.lang.String r2 = r2.text
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0458
            r1 = 2131625122(0x7f0e04a2, float:1.8877443E38)
            java.lang.String r2 = "EditAdminTransferSetPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$vsxn2dfnVjhqxZdbbzveeuN1wCA r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$vsxn2dfnVjhqxZdbbzveeuN1wCA
            r2.<init>()
            r0.setPositiveButton(r1, r2)
            r1 = 2131624561(0x7f0e0271, float:1.8876305E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r12, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            goto L_0x049e
        L_0x0458:
            android.widget.TextView r1 = new android.widget.TextView
            android.app.Activity r2 = r33.getParentActivity()
            r1.<init>(r2)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r1.setTextColor(r2)
            r2 = 1098907648(0x41800000, float:16.0)
            r3 = 1
            r1.setTextSize(r3, r2)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0474
            r17 = 5
        L_0x0474:
            r2 = r17 | 48
            r1.setGravity(r2)
            r2 = 2131625116(0x7f0e049c, float:1.887743E38)
            java.lang.String r3 = "EditAdminTransferAlertText3"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            r9 = -1
            r10 = -2
            r11 = 0
            r12 = 1093664768(0x41300000, float:11.0)
            r13 = 0
            r14 = 0
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r10, r11, r12, r13, r14)
            r4.addView(r1, r2)
            r1 = 2131626177(0x7f0e08c1, float:1.8879583E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
        L_0x049e:
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r6.showDialog(r0)
        L_0x04a5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$28$SendMessagesHelper(java.lang.String, boolean, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity, org.telegram.ui.TwoStepVerificationActivity, org.telegram.tgnet.TLObject[], org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP, boolean):void");
    }

    public /* synthetic */ void lambda$null$24$SendMessagesHelper(boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        twoStepVerificationActivity.setDelegate(new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate(z, messageObject, tLRPC$KeyboardButton, twoStepVerificationActivity, chatActivity) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ MessageObject f$2;
            public final /* synthetic */ TLRPC$KeyboardButton f$3;
            public final /* synthetic */ TwoStepVerificationActivity f$4;
            public final /* synthetic */ ChatActivity f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void didEnterPassword(TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP) {
                SendMessagesHelper.this.lambda$null$23$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLRPC$InputCheckPasswordSRP);
            }
        });
        chatActivity.presentFragment(twoStepVerificationActivity);
    }

    public /* synthetic */ void lambda$null$27$SendMessagesHelper(TwoStepVerificationActivity twoStepVerificationActivity, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, twoStepVerificationActivity, z, messageObject, tLRPC$KeyboardButton, chatActivity) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ TwoStepVerificationActivity f$3;
            public final /* synthetic */ boolean f$4;
            public final /* synthetic */ MessageObject f$5;
            public final /* synthetic */ TLRPC$KeyboardButton f$6;
            public final /* synthetic */ ChatActivity f$7;

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
                SendMessagesHelper.this.lambda$null$26$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$26$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            twoStepVerificationActivity.setCurrentPasswordInfo((byte[]) null, tLRPC$TL_account_password);
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            lambda$null$23$SendMessagesHelper(z, messageObject, tLRPC$KeyboardButton, twoStepVerificationActivity.getNewSrpPassword(), twoStepVerificationActivity, chatActivity);
        }
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
                        public final /* synthetic */ long f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            SendMessagesHelper.this.lambda$sendGame$30$SendMessagesHelper(this.f$1, tLObject, tLRPC$TL_error);
                        }
                    });
                }
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMedia, new RequestDelegate(j2) {
                public final /* synthetic */ long f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SendMessagesHelper.this.lambda$sendGame$30$SendMessagesHelper(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$sendGame$30$SendMessagesHelper(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, dialogId, tLRPC$Message.attachPath, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, messageObject, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$Message.reply_markup, tLRPC$Message.params, !tLRPC$Message.silent, messageObject2.scheduled ? tLRPC$Message.date : 0, 0, (Object) null);
    }

    public void sendMessage(TLRPC$User tLRPC$User, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$User, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, (String) null, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$TL_document tLRPC$TL_document, VideoEditedInfo videoEditedInfo, String str, long j, MessageObject messageObject, MessageObject messageObject2, String str2, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        String str3 = str2;
        sendMessage((String) null, str3, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, videoEditedInfo, (TLRPC$User) null, tLRPC$TL_document, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, str, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, arrayList, tLRPC$ReplyMarkup, hashMap, z, i, i2, obj);
    }

    public void sendMessage(String str, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$WebPage tLRPC$WebPage, boolean z, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z2, int i) {
        sendMessage(str, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, (String) null, messageObject, messageObject2, tLRPC$WebPage, z, (MessageObject) null, arrayList, tLRPC$ReplyMarkup, hashMap, z2, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$MessageMedia tLRPC$MessageMedia, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, tLRPC$MessageMedia, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, (String) null, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, tLRPC$TL_messageMediaPoll, j, (String) null, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$TL_game tLRPC$TL_game, long j, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, tLRPC$TL_game, (TLRPC$TL_messageMediaPoll) null, j, (String) null, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null);
    }

    public void sendMessage(TLRPC$TL_photo tLRPC$TL_photo, String str, long j, MessageObject messageObject, MessageObject messageObject2, String str2, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        String str3 = str2;
        sendMessage((String) null, str3, (TLRPC$MessageMedia) null, tLRPC$TL_photo, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, j, str, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, arrayList, tLRPC$ReplyMarkup, hashMap, z, i, i2, obj);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v1, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v5, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v24, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v25, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v28, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v29, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v30, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v69, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v70, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v71, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v72, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v20, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v62, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v32, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v52, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v53, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v54, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v55, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v41, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v42, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v43, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v78, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v50, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v14, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v55, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v17, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v61, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v27, resolved type: org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v81, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v28, resolved type: org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v83, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v18, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v31, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v122, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v22, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v38, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v133, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v134, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v39, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v135, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v136, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v124, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v125, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v127, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v78, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v193, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v80, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v29, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v207, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v208, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v30, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v183, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v229, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v172, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r52v15, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v238, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v240, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v41, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v178, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v42, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v247, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v249, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v147, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v46, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v154, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v156, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: org.telegram.tgnet.TLRPC$TL_userRequest_old2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r60v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v65, resolved type: org.telegram.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v66, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v48, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v49, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v241, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v242, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v243, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v139, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v309, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v310, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v236, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v311, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v312, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v313, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v314, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v244, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v245, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v315, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v316, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX WARNING: type inference failed for: r13v18 */
    /* JADX WARNING: Code restructure failed: missing block: B:1326:0x19d0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:1327:0x19d1, code lost:
        r1 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x0647, code lost:
        if (org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, true) != false) goto L_0x0649;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x065e, code lost:
        r4.attributes.remove(r6);
        r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55();
        r4.attributes.add(r3);
        r3.alt = r12.alt;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x0673, code lost:
        if (r12.stickerset == null) goto L_0x06ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x0679, code lost:
        if ((r12.stickerset instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName) == false) goto L_0x0682;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:?, code lost:
        r6 = r12.stickerset.short_name;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x067f, code lost:
        r22 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x0688, code lost:
        r22 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:?, code lost:
        r6 = getMediaDataController().getStickerSetName(r12.stickerset.id);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x0694, code lost:
        if (android.text.TextUtils.isEmpty(r6) != false) goto L_0x06ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:?, code lost:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName();
        r3.stickerset = r4;
        r4.short_name = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x069f, code lost:
        r4 = null;
        r5 = r51;
        r71 = r8;
        r7 = r61;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x06a8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x06a9, code lost:
        r2 = r0;
        r1 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x06b1, code lost:
        if ((r12.stickerset instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetID) == false) goto L_0x06cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x06b5, code lost:
        r5 = r51;
        r71 = r8;
        r7 = r61;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:?, code lost:
        r4 = new org.telegram.messenger.SendMessagesHelper.DelayedMessage(r5, r7);
        r4.encryptedChat = r9;
        r4.locationParent = r3;
        r4.type = 5;
        r4.parentObject = r12.stickerset;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x06cd, code lost:
        r5 = r51;
        r71 = r8;
        r7 = r61;
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x06d5, code lost:
        r3.stickerset = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x06dc, code lost:
        r28 = r59;
        r3 = r66;
        r12 = r71;
        r14 = r18;
        r6 = r22;
        r18 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x06ec, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x06ee, code lost:
        r22 = r5;
        r71 = r8;
        r5 = r51;
        r7 = r61;
        r3.stickerset = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x0701, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x0e22, code lost:
        if (r8.roundVideo == false) goto L_0x0e2a;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1078:0x13e6  */
    /* JADX WARNING: Removed duplicated region for block: B:1151:0x1562 A[Catch:{ Exception -> 0x1417 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1157:0x1594 A[Catch:{ Exception -> 0x1417 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1162:0x15d6 A[Catch:{ Exception -> 0x1417 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1171:0x1607  */
    /* JADX WARNING: Removed duplicated region for block: B:1172:0x1609  */
    /* JADX WARNING: Removed duplicated region for block: B:1175:0x160f A[Catch:{ Exception -> 0x162a }] */
    /* JADX WARNING: Removed duplicated region for block: B:1178:0x1620 A[Catch:{ Exception -> 0x162a }] */
    /* JADX WARNING: Removed duplicated region for block: B:1295:0x1967 A[SYNTHETIC, Splitter:B:1295:0x1967] */
    /* JADX WARNING: Removed duplicated region for block: B:1299:0x196e A[SYNTHETIC, Splitter:B:1299:0x196e] */
    /* JADX WARNING: Removed duplicated region for block: B:1317:0x19b6 A[Catch:{ Exception -> 0x1b18 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1319:0x19ba A[Catch:{ Exception -> 0x1b18 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1333:0x19ed A[Catch:{ Exception -> 0x1b18 }] */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02f6  */
    /* JADX WARNING: Removed duplicated region for block: B:1361:0x1a67 A[Catch:{ Exception -> 0x1b18 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1404:0x1b3b  */
    /* JADX WARNING: Removed duplicated region for block: B:1405:0x1b3d  */
    /* JADX WARNING: Removed duplicated region for block: B:1408:0x1b43  */
    /* JADX WARNING: Removed duplicated region for block: B:1421:0x1584 A[EDGE_INSN: B:1421:0x1584->B:1155:0x1584 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0606 A[Catch:{ Exception -> 0x05b3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x0619 A[Catch:{ Exception -> 0x05b3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x062d A[Catch:{ Exception -> 0x05b3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0638 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x063c A[Catch:{ Exception -> 0x0735 }] */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0759 A[SYNTHETIC, Splitter:B:393:0x0759] */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0771 A[Catch:{ Exception -> 0x0768 }] */
    /* JADX WARNING: Removed duplicated region for block: B:401:0x0774 A[Catch:{ Exception -> 0x0768 }] */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x077e A[Catch:{ Exception -> 0x0768 }] */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0791 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0768 }] */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x07ad A[Catch:{ Exception -> 0x0768 }] */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x07b8 A[Catch:{ Exception -> 0x0768 }] */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x07f6  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0825  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0834 A[SYNTHETIC, Splitter:B:431:0x0834] */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0840 A[SYNTHETIC, Splitter:B:435:0x0840] */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x086f A[SYNTHETIC, Splitter:B:447:0x086f] */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x087f  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0882 A[SYNTHETIC, Splitter:B:454:0x0882] */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0890 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x08d5  */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x08e4 A[SYNTHETIC, Splitter:B:477:0x08e4] */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x0937 A[Catch:{ Exception -> 0x081d }] */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0943 A[Catch:{ Exception -> 0x081d }] */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x096d A[Catch:{ Exception -> 0x081d }] */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x0999 A[SYNTHETIC, Splitter:B:507:0x0999] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0104 A[SYNTHETIC, Splitter:B:50:0x0104] */
    /* JADX WARNING: Removed duplicated region for block: B:563:0x0a64 A[SYNTHETIC, Splitter:B:563:0x0a64] */
    /* JADX WARNING: Removed duplicated region for block: B:575:0x0aa1 A[Catch:{ Exception -> 0x1b1a }] */
    /* JADX WARNING: Removed duplicated region for block: B:576:0x0aa3 A[Catch:{ Exception -> 0x1b1a }] */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x0aac A[SYNTHETIC, Splitter:B:579:0x0aac] */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0ace A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x0ad7 A[Catch:{ Exception -> 0x0ac4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:606:0x0aff  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0b06 A[SYNTHETIC, Splitter:B:610:0x0b06] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x011d A[Catch:{ Exception -> 0x02ea }] */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b5e  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0146 A[Catch:{ Exception -> 0x02ea }] */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0bb0  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0c1e A[Catch:{ Exception -> 0x0ac4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:659:0x0CLASSNAME A[Catch:{ Exception -> 0x0ac4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x0d95 A[Catch:{ Exception -> 0x0ac4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:776:0x0e38 A[Catch:{ Exception -> 0x0ac4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:778:0x0e48  */
    /* JADX WARNING: Removed duplicated region for block: B:792:0x0e97 A[Catch:{ Exception -> 0x0f5b }] */
    /* JADX WARNING: Removed duplicated region for block: B:793:0x0e9c A[Catch:{ Exception -> 0x0f5b }] */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x0ec1 A[Catch:{ Exception -> 0x0f5b }] */
    /* JADX WARNING: Removed duplicated region for block: B:803:0x0edb A[Catch:{ Exception -> 0x0f5b }] */
    /* JADX WARNING: Removed duplicated region for block: B:806:0x0eeb A[Catch:{ Exception -> 0x0f5b }] */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x10d0 A[SYNTHETIC, Splitter:B:919:0x10d0] */
    /* JADX WARNING: Removed duplicated region for block: B:922:0x10d7 A[SYNTHETIC, Splitter:B:922:0x10d7] */
    /* JADX WARNING: Removed duplicated region for block: B:931:0x10fb A[Catch:{ Exception -> 0x0f5b }] */
    /* JADX WARNING: Removed duplicated region for block: B:939:0x112a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendMessage(java.lang.String r52, java.lang.String r53, org.telegram.tgnet.TLRPC$MessageMedia r54, org.telegram.tgnet.TLRPC$TL_photo r55, org.telegram.messenger.VideoEditedInfo r56, org.telegram.tgnet.TLRPC$User r57, org.telegram.tgnet.TLRPC$TL_document r58, org.telegram.tgnet.TLRPC$TL_game r59, org.telegram.tgnet.TLRPC$TL_messageMediaPoll r60, long r61, java.lang.String r63, org.telegram.messenger.MessageObject r64, org.telegram.messenger.MessageObject r65, org.telegram.tgnet.TLRPC$WebPage r66, boolean r67, org.telegram.messenger.MessageObject r68, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r69, org.telegram.tgnet.TLRPC$ReplyMarkup r70, java.util.HashMap<java.lang.String, java.lang.String> r71, boolean r72, int r73, int r74, java.lang.Object r75) {
        /*
            r51 = this;
            r1 = r51
            r2 = r52
            r3 = r54
            r4 = r55
            r5 = r57
            r6 = r58
            r7 = r59
            r8 = r60
            r9 = r61
            r11 = r63
            r12 = r64
            r13 = r65
            r14 = r66
            r15 = r68
            r13 = r69
            r12 = r71
            r6 = r73
            r7 = r74
            if (r5 == 0) goto L_0x002b
            java.lang.String r11 = r5.phone
            if (r11 != 0) goto L_0x002b
            return
        L_0x002b:
            r16 = 0
            int r11 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r11 != 0) goto L_0x0032
            return
        L_0x0032:
            java.lang.String r11 = ""
            if (r2 != 0) goto L_0x003b
            if (r53 != 0) goto L_0x003b
            r18 = r11
            goto L_0x003d
        L_0x003b:
            r18 = r53
        L_0x003d:
            if (r12 == 0) goto L_0x0050
            java.lang.String r5 = "originalPath"
            boolean r5 = r12.containsKey(r5)
            if (r5 == 0) goto L_0x0050
            java.lang.String r5 = "originalPath"
            java.lang.Object r5 = r12.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            goto L_0x0051
        L_0x0050:
            r5 = 0
        L_0x0051:
            r19 = -1
            r20 = r5
            int r5 = (int) r9
            r21 = 32
            long r3 = r9 >> r21
            int r4 = (int) r3
            if (r5 == 0) goto L_0x0066
            org.telegram.messenger.MessagesController r3 = r51.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((int) r5)
            goto L_0x0067
        L_0x0066:
            r3 = 0
        L_0x0067:
            if (r5 != 0) goto L_0x00ac
            org.telegram.messenger.MessagesController r10 = r51.getMessagesController()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r10.getEncryptedChat(r9)
            if (r9 != 0) goto L_0x00a8
            if (r15 == 0) goto L_0x00a7
            org.telegram.messenger.MessagesStorage r2 = r51.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner
            boolean r4 = r15.scheduled
            r2.markMessageAsSendError(r3, r4)
            org.telegram.tgnet.TLRPC$Message r2 = r15.messageOwner
            r3 = 2
            r2.send_state = r3
            org.telegram.messenger.NotificationCenter r2 = r51.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.messageSendError
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r5 = r68.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 0
            r4[r6] = r5
            r2.postNotificationName(r3, r4)
            int r2 = r68.getId()
            r1.processSentMessage(r2)
        L_0x00a7:
            return
        L_0x00a8:
            r24 = r4
            r4 = 0
            goto L_0x00f3
        L_0x00ac:
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r9 == 0) goto L_0x00ef
            org.telegram.messenger.MessagesController r9 = r51.getMessagesController()
            int r10 = r3.channel_id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
            if (r9 == 0) goto L_0x00c6
            boolean r10 = r9.megagroup
            if (r10 != 0) goto L_0x00c6
            r10 = 1
            goto L_0x00c7
        L_0x00c6:
            r10 = 0
        L_0x00c7:
            if (r10 == 0) goto L_0x00e3
            r23 = r10
            boolean r10 = r9.has_link
            if (r10 == 0) goto L_0x00e0
            org.telegram.messenger.MessagesController r10 = r51.getMessagesController()
            r24 = r4
            int r4 = r9.id
            org.telegram.tgnet.TLRPC$ChatFull r4 = r10.getChatFull(r4)
            if (r4 == 0) goto L_0x00e7
            int r4 = r4.linked_chat_id
            goto L_0x00e8
        L_0x00e0:
            r24 = r4
            goto L_0x00e7
        L_0x00e3:
            r24 = r4
            r23 = r10
        L_0x00e7:
            r4 = 0
        L_0x00e8:
            boolean r9 = org.telegram.messenger.ChatObject.shouldSendAnonymously(r9)
            r10 = r9
            r9 = 0
            goto L_0x00f6
        L_0x00ef:
            r24 = r4
            r4 = 0
            r9 = 0
        L_0x00f3:
            r10 = 0
            r23 = 0
        L_0x00f6:
            r25 = r4
            java.lang.String r4 = "http"
            r26 = r10
            java.lang.String r10 = "query_id"
            r27 = r3
            java.lang.String r3 = "parentObject"
            if (r15 == 0) goto L_0x02f6
            org.telegram.tgnet.TLRPC$Message r1 = r15.messageOwner     // Catch:{ Exception -> 0x02ef }
            if (r75 != 0) goto L_0x0115
            if (r12 == 0) goto L_0x0115
            boolean r26 = r12.containsKey(r3)     // Catch:{ Exception -> 0x02ea }
            if (r26 == 0) goto L_0x0115
            java.lang.Object r26 = r12.get(r3)     // Catch:{ Exception -> 0x02ea }
            goto L_0x0117
        L_0x0115:
            r26 = r75
        L_0x0117:
            boolean r29 = r68.isForwarded()     // Catch:{ Exception -> 0x02ea }
            if (r29 == 0) goto L_0x0146
            r19 = r54
            r52 = r55
            r34 = r58
            r29 = r3
            r31 = r4
            r13 = r7
            r35 = r8
            r6 = r12
            r36 = r14
            r37 = r18
            r60 = r26
            r3 = r27
            r14 = 4
            r18 = 0
            r28 = 0
            r4 = r57
            r7 = r61
            r12 = r2
            r2 = r5
            r26 = r10
            r5 = r51
        L_0x0142:
            r10 = r72
            goto L_0x07f4
        L_0x0146:
            boolean r29 = r68.isDice()     // Catch:{ Exception -> 0x02ea }
            if (r29 == 0) goto L_0x0167
            java.lang.String r2 = r68.getDiceEmoji()     // Catch:{ Exception -> 0x02ea }
            r18 = r58
            r29 = r3
            r31 = r4
            r30 = r5
            r19 = r8
            r22 = r11
            r5 = 2
            r32 = 11
            r3 = r54
            r4 = r55
            r8 = r57
            goto L_0x029a
        L_0x0167:
            r29 = r3
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            if (r3 == 0) goto L_0x027c
            boolean r3 = r68.isAnimatedEmoji()     // Catch:{ Exception -> 0x02ea }
            if (r3 == 0) goto L_0x0175
            goto L_0x027c
        L_0x0175:
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r30 = r5
            r5 = 4
            if (r3 != r5) goto L_0x018b
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02ea }
            r31 = r4
            r19 = r8
            r22 = r18
            r5 = 2
            r32 = 1
            r4 = r55
            goto L_0x0296
        L_0x018b:
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r5 = 1
            if (r3 != r5) goto L_0x01a8
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3     // Catch:{ Exception -> 0x02ea }
            r31 = r4
            r19 = r8
            r22 = r18
            r5 = 2
            r32 = 2
            r8 = r57
            r18 = r58
            r4 = r3
        L_0x01a4:
            r3 = r54
            goto L_0x029a
        L_0x01a8:
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r5 = 3
            if (r3 == r5) goto L_0x0265
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r5 = 5
            if (r3 == r5) goto L_0x0265
            org.telegram.messenger.VideoEditedInfo r3 = r15.videoEditedInfo     // Catch:{ Exception -> 0x02ea }
            if (r3 == 0) goto L_0x01b8
            goto L_0x0265
        L_0x01b8:
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r5 = 12
            if (r3 != r5) goto L_0x01fe
            org.telegram.tgnet.TLRPC$TL_userRequest_old2 r3 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2     // Catch:{ Exception -> 0x02ea }
            r3.<init>()     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media     // Catch:{ Exception -> 0x02ea }
            java.lang.String r5 = r5.phone_number     // Catch:{ Exception -> 0x02ea }
            r3.phone = r5     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media     // Catch:{ Exception -> 0x02ea }
            java.lang.String r5 = r5.first_name     // Catch:{ Exception -> 0x02ea }
            r3.first_name = r5     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media     // Catch:{ Exception -> 0x02ea }
            java.lang.String r5 = r5.last_name     // Catch:{ Exception -> 0x02ea }
            r3.last_name = r5     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r5 = new org.telegram.tgnet.TLRPC$TL_restrictionReason     // Catch:{ Exception -> 0x02ea }
            r5.<init>()     // Catch:{ Exception -> 0x02ea }
            r5.platform = r11     // Catch:{ Exception -> 0x02ea }
            r5.reason = r11     // Catch:{ Exception -> 0x02ea }
            r31 = r4
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media     // Catch:{ Exception -> 0x02ea }
            java.lang.String r4 = r4.vcard     // Catch:{ Exception -> 0x02ea }
            r5.text = r4     // Catch:{ Exception -> 0x02ea }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r4 = r3.restriction_reason     // Catch:{ Exception -> 0x02ea }
            r4.add(r5)     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media     // Catch:{ Exception -> 0x02ea }
            int r4 = r4.user_id     // Catch:{ Exception -> 0x02ea }
            r3.id = r4     // Catch:{ Exception -> 0x02ea }
            r4 = r55
            r19 = r8
            r22 = r18
            r5 = 2
            r32 = 6
            r18 = r58
            r8 = r3
            goto L_0x01a4
        L_0x01fe:
            r31 = r4
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r4 = 8
            if (r3 == r4) goto L_0x0255
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r5 = 9
            if (r3 == r5) goto L_0x0255
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r5 = 13
            if (r3 == r5) goto L_0x0255
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r5 = 14
            if (r3 == r5) goto L_0x0255
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r5 = 15
            if (r3 != r5) goto L_0x021f
            goto L_0x0255
        L_0x021f:
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r5 = 2
            if (r3 != r5) goto L_0x0233
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x02ea }
            r4 = r55
            r19 = r8
            r22 = r18
            r32 = 8
            goto L_0x0276
        L_0x0233:
            int r3 = r15.type     // Catch:{ Exception -> 0x02ea }
            r4 = 17
            if (r3 != r4) goto L_0x024a
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3     // Catch:{ Exception -> 0x02ea }
            r4 = r55
            r8 = r57
            r19 = r3
            r22 = r18
            r32 = 10
            r3 = r54
            goto L_0x0298
        L_0x024a:
            r3 = r54
            r4 = r55
            r19 = r8
            r22 = r18
            r32 = -1
            goto L_0x0296
        L_0x0255:
            r5 = 2
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x02ea }
            r4 = r55
            r19 = r8
            r22 = r18
            r32 = 7
            goto L_0x0276
        L_0x0265:
            r31 = r4
            r5 = 2
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x02ea }
            r4 = r55
            r19 = r8
            r22 = r18
            r32 = 3
        L_0x0276:
            r8 = r57
            r18 = r3
            goto L_0x01a4
        L_0x027c:
            r31 = r4
            r30 = r5
            r5 = 2
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner     // Catch:{ Exception -> 0x02ea }
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media     // Catch:{ Exception -> 0x02ea }
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x02ea }
            if (r3 == 0) goto L_0x028a
            goto L_0x028c
        L_0x028a:
            java.lang.String r2 = r1.message     // Catch:{ Exception -> 0x02ea }
        L_0x028c:
            r3 = r54
            r4 = r55
            r19 = r8
            r22 = r18
            r32 = 0
        L_0x0296:
            r8 = r57
        L_0x0298:
            r18 = r58
        L_0x029a:
            if (r12 == 0) goto L_0x02a4
            boolean r33 = r12.containsKey(r10)     // Catch:{ Exception -> 0x02ea }
            if (r33 == 0) goto L_0x02a4
            r32 = 9
        L_0x02a4:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media     // Catch:{ Exception -> 0x02ea }
            int r5 = r5.ttl_seconds     // Catch:{ Exception -> 0x02ea }
            if (r5 <= 0) goto L_0x02c6
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media     // Catch:{ Exception -> 0x02ea }
            int r5 = r5.ttl_seconds     // Catch:{ Exception -> 0x02ea }
            r52 = r4
            r13 = r5
            r4 = r8
            r6 = r12
            r36 = r14
            r34 = r18
            r35 = r19
            r37 = r22
            r60 = r26
            r14 = r32
            r18 = 0
            r28 = 0
            r5 = r51
            goto L_0x02dd
        L_0x02c6:
            r5 = r51
            r52 = r4
            r13 = r7
            r4 = r8
            r6 = r12
            r36 = r14
            r34 = r18
            r35 = r19
            r37 = r22
            r60 = r26
            r14 = r32
            r18 = 0
            r28 = 0
        L_0x02dd:
            r7 = r61
            r12 = r2
            r19 = r3
            r26 = r10
            r3 = r27
            r2 = r30
            goto L_0x0142
        L_0x02ea:
            r0 = move-exception
            r2 = r0
            r5 = r1
        L_0x02ed:
            r1 = r6
            goto L_0x02f3
        L_0x02ef:
            r0 = move-exception
            r2 = r0
            r1 = r6
        L_0x02f2:
            r5 = 0
        L_0x02f3:
            r12 = 0
            goto L_0x1b32
        L_0x02f6:
            r29 = r3
            r31 = r4
            r30 = r5
            if (r2 == 0) goto L_0x039a
            if (r9 == 0) goto L_0x0306
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x02ef }
            r1.<init>()     // Catch:{ Exception -> 0x02ef }
            goto L_0x030b
        L_0x0306:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02ef }
            r1.<init>()     // Catch:{ Exception -> 0x02ef }
        L_0x030b:
            if (r9 == 0) goto L_0x0321
            boolean r3 = r14 instanceof org.telegram.tgnet.TLRPC$TL_webPagePending     // Catch:{ Exception -> 0x02ea }
            if (r3 == 0) goto L_0x0321
            java.lang.String r3 = r14.url     // Catch:{ Exception -> 0x02ea }
            if (r3 == 0) goto L_0x0320
            org.telegram.tgnet.TLRPC$TL_webPageUrlPending r3 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending     // Catch:{ Exception -> 0x02ea }
            r3.<init>()     // Catch:{ Exception -> 0x02ea }
            java.lang.String r4 = r14.url     // Catch:{ Exception -> 0x02ea }
            r3.url = r4     // Catch:{ Exception -> 0x02ea }
            r14 = r3
            goto L_0x0321
        L_0x0320:
            r14 = 0
        L_0x0321:
            int r3 = r52.length()     // Catch:{ Exception -> 0x02ea }
            r4 = 30
            if (r3 >= r4) goto L_0x036b
            if (r14 != 0) goto L_0x036b
            if (r13 == 0) goto L_0x0333
            boolean r3 = r69.isEmpty()     // Catch:{ Exception -> 0x02ea }
            if (r3 == 0) goto L_0x036b
        L_0x0333:
            org.telegram.messenger.MessagesController r3 = r51.getMessagesController()     // Catch:{ Exception -> 0x02ea }
            java.util.HashSet<java.lang.String> r3 = r3.diceEmojies     // Catch:{ Exception -> 0x02ea }
            java.lang.String r4 = "️"
            java.lang.String r4 = r2.replace(r4, r11)     // Catch:{ Exception -> 0x02ea }
            boolean r3 = r3.contains(r4)     // Catch:{ Exception -> 0x02ea }
            if (r3 == 0) goto L_0x036b
            if (r9 != 0) goto L_0x036b
            if (r6 != 0) goto L_0x036b
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaDice     // Catch:{ Exception -> 0x02ea }
            r3.<init>()     // Catch:{ Exception -> 0x02ea }
            r3.emoticon = r2     // Catch:{ Exception -> 0x02ea }
            r4 = -1
            r3.value = r4     // Catch:{ Exception -> 0x02ea }
            r1.media = r3     // Catch:{ Exception -> 0x02ea }
            r4 = 8
            r18 = 0
            r5 = r51
            r7 = r61
            r6 = r1
            r3 = r14
            r2 = r30
            r19 = 11
            r28 = 0
            r1 = r63
            r14 = r11
            goto L_0x0757
        L_0x036b:
            if (r14 != 0) goto L_0x0375
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x02ea }
            r3.<init>()     // Catch:{ Exception -> 0x02ea }
            r1.media = r3     // Catch:{ Exception -> 0x02ea }
            goto L_0x037e
        L_0x0375:
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x02ea }
            r3.<init>()     // Catch:{ Exception -> 0x02ea }
            r1.media = r3     // Catch:{ Exception -> 0x02ea }
            r3.webpage = r14     // Catch:{ Exception -> 0x02ea }
        L_0x037e:
            if (r12 == 0) goto L_0x0389
            boolean r3 = r12.containsKey(r10)     // Catch:{ Exception -> 0x02ea }
            if (r3 == 0) goto L_0x0389
            r19 = 9
            goto L_0x038b
        L_0x0389:
            r19 = 0
        L_0x038b:
            r1.message = r2     // Catch:{ Exception -> 0x02ea }
            r4 = 8
            r5 = r51
            r7 = r61
            r6 = r1
            r3 = r14
            r14 = r18
            r2 = r30
            goto L_0x03b9
        L_0x039a:
            if (r8 == 0) goto L_0x03c1
            if (r9 == 0) goto L_0x03a4
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x02ef }
            r1.<init>()     // Catch:{ Exception -> 0x02ef }
            goto L_0x03a9
        L_0x03a4:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02ef }
            r1.<init>()     // Catch:{ Exception -> 0x02ef }
        L_0x03a9:
            r1.media = r8     // Catch:{ Exception -> 0x02ea }
            r4 = 8
            r5 = r51
            r7 = r61
            r6 = r1
            r3 = r14
            r14 = r18
            r2 = r30
            r19 = 10
        L_0x03b9:
            r28 = 0
            r18 = 0
            r1 = r63
            goto L_0x0757
        L_0x03c1:
            r1 = r54
            if (r1 == 0) goto L_0x0408
            if (r9 == 0) goto L_0x03cd
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x02ef }
            r3.<init>()     // Catch:{ Exception -> 0x02ef }
            goto L_0x03d2
        L_0x03cd:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02ef }
            r3.<init>()     // Catch:{ Exception -> 0x02ef }
        L_0x03d2:
            r3.media = r1     // Catch:{ Exception -> 0x0403 }
            if (r12 == 0) goto L_0x03ed
            boolean r4 = r12.containsKey(r10)     // Catch:{ Exception -> 0x0403 }
            if (r4 == 0) goto L_0x03ed
            r4 = 8
            r5 = r51
            r7 = r61
            r1 = r63
            r6 = r3
            r3 = r14
            r14 = r18
            r2 = r30
            r19 = 9
            goto L_0x03fd
        L_0x03ed:
            r4 = 8
            r5 = r51
            r7 = r61
            r1 = r63
            r6 = r3
            r3 = r14
            r14 = r18
            r2 = r30
            r19 = 1
        L_0x03fd:
            r28 = 0
        L_0x03ff:
            r18 = 0
            goto L_0x0757
        L_0x0403:
            r0 = move-exception
            r2 = r0
            r5 = r3
            goto L_0x02ed
        L_0x0408:
            r3 = r55
            if (r3 == 0) goto L_0x0489
            if (r9 == 0) goto L_0x0414
            org.telegram.tgnet.TLRPC$TL_message_secret r4 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x02ef }
            r4.<init>()     // Catch:{ Exception -> 0x02ef }
            goto L_0x0419
        L_0x0414:
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02ef }
            r4.<init>()     // Catch:{ Exception -> 0x02ef }
        L_0x0419:
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x04b8 }
            r5.<init>()     // Catch:{ Exception -> 0x04b8 }
            r4.media = r5     // Catch:{ Exception -> 0x04b8 }
            int r1 = r5.flags     // Catch:{ Exception -> 0x04b8 }
            r19 = 3
            r1 = r1 | 3
            r5.flags = r1     // Catch:{ Exception -> 0x04b8 }
            if (r13 == 0) goto L_0x042c
            r4.entities = r13     // Catch:{ Exception -> 0x04b8 }
        L_0x042c:
            if (r7 == 0) goto L_0x043e
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r4.media     // Catch:{ Exception -> 0x04b8 }
            r1.ttl_seconds = r7     // Catch:{ Exception -> 0x04b8 }
            r4.ttl = r7     // Catch:{ Exception -> 0x04b8 }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r4.media     // Catch:{ Exception -> 0x04b8 }
            int r5 = r1.flags     // Catch:{ Exception -> 0x04b8 }
            r19 = 4
            r5 = r5 | 4
            r1.flags = r5     // Catch:{ Exception -> 0x04b8 }
        L_0x043e:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r4.media     // Catch:{ Exception -> 0x04b8 }
            r1.photo = r3     // Catch:{ Exception -> 0x04b8 }
            if (r12 == 0) goto L_0x044f
            boolean r1 = r12.containsKey(r10)     // Catch:{ Exception -> 0x04b8 }
            if (r1 == 0) goto L_0x044f
            r1 = r63
            r19 = 9
            goto L_0x0453
        L_0x044f:
            r1 = r63
            r19 = 2
        L_0x0453:
            if (r1 == 0) goto L_0x0468
            int r5 = r63.length()     // Catch:{ Exception -> 0x04b8 }
            if (r5 <= 0) goto L_0x0468
            r5 = r31
            boolean r22 = r1.startsWith(r5)     // Catch:{ Exception -> 0x04b8 }
            if (r22 == 0) goto L_0x046a
            r4.attachPath = r1     // Catch:{ Exception -> 0x04b8 }
            r31 = r5
            goto L_0x04a8
        L_0x0468:
            r5 = r31
        L_0x046a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r3.sizes     // Catch:{ Exception -> 0x04b8 }
            r31 = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r3.sizes     // Catch:{ Exception -> 0x04b8 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x04b8 }
            r3 = 1
            int r5 = r5 - r3
            java.lang.Object r2 = r2.get(r5)     // Catch:{ Exception -> 0x04b8 }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2     // Catch:{ Exception -> 0x04b8 }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.location     // Catch:{ Exception -> 0x04b8 }
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r3)     // Catch:{ Exception -> 0x04b8 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x04b8 }
            r4.attachPath = r2     // Catch:{ Exception -> 0x04b8 }
            goto L_0x04a8
        L_0x0489:
            r2 = r59
            r1 = r63
            r3 = r7
            if (r2 == 0) goto L_0x04bd
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02ef }
            r4.<init>()     // Catch:{ Exception -> 0x02ef }
            org.telegram.tgnet.TLRPC$TL_messageMediaGame r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x04b8 }
            r5.<init>()     // Catch:{ Exception -> 0x04b8 }
            r4.media = r5     // Catch:{ Exception -> 0x04b8 }
            r5.game = r2     // Catch:{ Exception -> 0x04b8 }
            if (r12 == 0) goto L_0x04a8
            boolean r2 = r12.containsKey(r10)     // Catch:{ Exception -> 0x04b8 }
            if (r2 == 0) goto L_0x04a8
            r19 = 9
        L_0x04a8:
            r28 = 0
            r5 = r51
            r7 = r61
            r6 = r4
            r3 = r14
            r14 = r18
            r2 = r30
            r4 = 8
            goto L_0x03ff
        L_0x04b8:
            r0 = move-exception
            r2 = r0
            r5 = r4
            goto L_0x02ed
        L_0x04bd:
            r2 = r57
            r7 = 0
            if (r2 == 0) goto L_0x0562
            if (r9 == 0) goto L_0x04d1
            org.telegram.tgnet.TLRPC$TL_message_secret r4 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x04ca }
            r4.<init>()     // Catch:{ Exception -> 0x04ca }
            goto L_0x04d6
        L_0x04ca:
            r0 = move-exception
            r2 = r0
            r1 = r6
            r5 = r7
            r12 = r5
            goto L_0x1b32
        L_0x04d1:
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x055c }
            r4.<init>()     // Catch:{ Exception -> 0x055c }
        L_0x04d6:
            r5 = r4
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact     // Catch:{ Exception -> 0x0556 }
            r4.<init>()     // Catch:{ Exception -> 0x0556 }
            r5.media = r4     // Catch:{ Exception -> 0x0556 }
            java.lang.String r7 = r2.phone     // Catch:{ Exception -> 0x0556 }
            r4.phone_number = r7     // Catch:{ Exception -> 0x0556 }
            java.lang.String r7 = r2.first_name     // Catch:{ Exception -> 0x0556 }
            r4.first_name = r7     // Catch:{ Exception -> 0x0556 }
            java.lang.String r7 = r2.last_name     // Catch:{ Exception -> 0x0556 }
            r4.last_name = r7     // Catch:{ Exception -> 0x0556 }
            int r7 = r2.id     // Catch:{ Exception -> 0x0556 }
            r4.user_id = r7     // Catch:{ Exception -> 0x0556 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r4 = r2.restriction_reason     // Catch:{ Exception -> 0x0556 }
            boolean r4 = r4.isEmpty()     // Catch:{ Exception -> 0x0556 }
            if (r4 != 0) goto L_0x0519
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r4 = r2.restriction_reason     // Catch:{ Exception -> 0x0556 }
            r7 = 0
            java.lang.Object r4 = r4.get(r7)     // Catch:{ Exception -> 0x0556 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r4 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r4     // Catch:{ Exception -> 0x0556 }
            java.lang.String r4 = r4.text     // Catch:{ Exception -> 0x0556 }
            java.lang.String r7 = "BEGIN:VCARD"
            boolean r4 = r4.startsWith(r7)     // Catch:{ Exception -> 0x0556 }
            if (r4 == 0) goto L_0x0519
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0556 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r7 = r2.restriction_reason     // Catch:{ Exception -> 0x0556 }
            r6 = 0
            java.lang.Object r7 = r7.get(r6)     // Catch:{ Exception -> 0x0556 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r7 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r7     // Catch:{ Exception -> 0x0556 }
            java.lang.String r6 = r7.text     // Catch:{ Exception -> 0x0556 }
            r4.vcard = r6     // Catch:{ Exception -> 0x0556 }
            goto L_0x051d
        L_0x0519:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0556 }
            r4.vcard = r11     // Catch:{ Exception -> 0x0556 }
        L_0x051d:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0556 }
            java.lang.String r4 = r4.first_name     // Catch:{ Exception -> 0x0556 }
            if (r4 != 0) goto L_0x0529
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0556 }
            r4.first_name = r11     // Catch:{ Exception -> 0x0556 }
            r2.first_name = r11     // Catch:{ Exception -> 0x0556 }
        L_0x0529:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0556 }
            java.lang.String r4 = r4.last_name     // Catch:{ Exception -> 0x0556 }
            if (r4 != 0) goto L_0x0535
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0556 }
            r4.last_name = r11     // Catch:{ Exception -> 0x0556 }
            r2.last_name = r11     // Catch:{ Exception -> 0x0556 }
        L_0x0535:
            if (r12 == 0) goto L_0x0542
            boolean r4 = r12.containsKey(r10)     // Catch:{ Exception -> 0x0556 }
            if (r4 == 0) goto L_0x0542
            r4 = 8
            r19 = 9
            goto L_0x0546
        L_0x0542:
            r4 = 8
            r19 = 6
        L_0x0546:
            r28 = 0
            r7 = r61
            r6 = r5
            r3 = r14
            r14 = r18
            r2 = r30
            r18 = 0
            r5 = r51
            goto L_0x0757
        L_0x0556:
            r0 = move-exception
            r1 = r73
            r2 = r0
            goto L_0x02f3
        L_0x055c:
            r0 = move-exception
        L_0x055d:
            r1 = r73
            r2 = r0
            goto L_0x02f2
        L_0x0562:
            r4 = r58
            r7 = r6
            if (r4 == 0) goto L_0x0746
            if (r9 == 0) goto L_0x0574
            org.telegram.tgnet.TLRPC$TL_message_secret r5 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x056f }
            r5.<init>()     // Catch:{ Exception -> 0x056f }
            goto L_0x0579
        L_0x056f:
            r0 = move-exception
            r2 = r0
            r1 = r7
            goto L_0x02f2
        L_0x0574:
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0741 }
            r5.<init>()     // Catch:{ Exception -> 0x0741 }
        L_0x0579:
            if (r30 >= 0) goto L_0x05b8
            org.telegram.messenger.MessagesController r6 = r51.getMessagesController()     // Catch:{ Exception -> 0x05b3 }
            r2 = r30
            int r8 = -r2
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x05b3 }
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r8)     // Catch:{ Exception -> 0x05b3 }
            if (r6 == 0) goto L_0x05ba
            boolean r6 = org.telegram.messenger.ChatObject.canSendStickers(r6)     // Catch:{ Exception -> 0x05b3 }
            if (r6 != 0) goto L_0x05ba
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes     // Catch:{ Exception -> 0x05b3 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x05b3 }
            r8 = 0
        L_0x0599:
            if (r8 >= r6) goto L_0x05ba
            r59 = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes     // Catch:{ Exception -> 0x05b3 }
            java.lang.Object r6 = r6.get(r8)     // Catch:{ Exception -> 0x05b3 }
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x05b3 }
            if (r6 == 0) goto L_0x05ae
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes     // Catch:{ Exception -> 0x05b3 }
            r6.remove(r8)     // Catch:{ Exception -> 0x05b3 }
            r6 = 1
            goto L_0x05bb
        L_0x05ae:
            int r8 = r8 + 1
            r6 = r59
            goto L_0x0599
        L_0x05b3:
            r0 = move-exception
            r2 = r0
            r1 = r7
            goto L_0x02f3
        L_0x05b8:
            r2 = r30
        L_0x05ba:
            r6 = 0
        L_0x05bb:
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r8 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0735 }
            r8.<init>()     // Catch:{ Exception -> 0x0735 }
            r5.media = r8     // Catch:{ Exception -> 0x0735 }
            r59 = r6
            int r6 = r8.flags     // Catch:{ Exception -> 0x0735 }
            r19 = 3
            r6 = r6 | 3
            r8.flags = r6     // Catch:{ Exception -> 0x0735 }
            if (r3 == 0) goto L_0x05d8
            r8.ttl_seconds = r3     // Catch:{ Exception -> 0x05b3 }
            r5.ttl = r3     // Catch:{ Exception -> 0x05b3 }
            r19 = 4
            r6 = r6 | 4
            r8.flags = r6     // Catch:{ Exception -> 0x05b3 }
        L_0x05d8:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r5.media     // Catch:{ Exception -> 0x0735 }
            r6.document = r4     // Catch:{ Exception -> 0x0735 }
            if (r12 == 0) goto L_0x05e7
            boolean r6 = r12.containsKey(r10)     // Catch:{ Exception -> 0x05b3 }
            if (r6 == 0) goto L_0x05e7
            r19 = 9
            goto L_0x0604
        L_0x05e7:
            boolean r6 = org.telegram.messenger.MessageObject.isVideoDocument(r58)     // Catch:{ Exception -> 0x0735 }
            if (r6 != 0) goto L_0x0602
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r58)     // Catch:{ Exception -> 0x05b3 }
            if (r6 != 0) goto L_0x0602
            if (r56 == 0) goto L_0x05f6
            goto L_0x0602
        L_0x05f6:
            boolean r6 = org.telegram.messenger.MessageObject.isVoiceDocument(r58)     // Catch:{ Exception -> 0x05b3 }
            if (r6 == 0) goto L_0x05ff
            r19 = 8
            goto L_0x0604
        L_0x05ff:
            r19 = 7
            goto L_0x0604
        L_0x0602:
            r19 = 3
        L_0x0604:
            if (r56 == 0) goto L_0x0619
            java.lang.String r6 = r56.getString()     // Catch:{ Exception -> 0x05b3 }
            if (r12 != 0) goto L_0x0612
            java.util.HashMap r8 = new java.util.HashMap     // Catch:{ Exception -> 0x05b3 }
            r8.<init>()     // Catch:{ Exception -> 0x05b3 }
            goto L_0x0613
        L_0x0612:
            r8 = r12
        L_0x0613:
            java.lang.String r12 = "ve"
            r8.put(r12, r6)     // Catch:{ Exception -> 0x05b3 }
            goto L_0x061a
        L_0x0619:
            r8 = r12
        L_0x061a:
            if (r9 == 0) goto L_0x0638
            int r6 = r4.dc_id     // Catch:{ Exception -> 0x05b3 }
            if (r6 <= 0) goto L_0x0638
            boolean r6 = org.telegram.messenger.MessageObject.isStickerDocument(r58)     // Catch:{ Exception -> 0x05b3 }
            if (r6 != 0) goto L_0x0638
            r6 = 1
            boolean r12 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r6)     // Catch:{ Exception -> 0x05b3 }
            if (r12 != 0) goto L_0x0638
            java.io.File r6 = org.telegram.messenger.FileLoader.getPathToAttach(r58)     // Catch:{ Exception -> 0x05b3 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x05b3 }
            r5.attachPath = r6     // Catch:{ Exception -> 0x05b3 }
            goto L_0x063a
        L_0x0638:
            r5.attachPath = r1     // Catch:{ Exception -> 0x0735 }
        L_0x063a:
            if (r9 == 0) goto L_0x071e
            boolean r6 = org.telegram.messenger.MessageObject.isStickerDocument(r58)     // Catch:{ Exception -> 0x0735 }
            if (r6 != 0) goto L_0x0649
            r6 = 1
            boolean r12 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r6)     // Catch:{ Exception -> 0x05b3 }
            if (r12 == 0) goto L_0x071e
        L_0x0649:
            r6 = 0
        L_0x064a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r4.attributes     // Catch:{ Exception -> 0x0735 }
            int r12 = r12.size()     // Catch:{ Exception -> 0x0735 }
            if (r6 >= r12) goto L_0x071e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r4.attributes     // Catch:{ Exception -> 0x0735 }
            java.lang.Object r12 = r12.get(r6)     // Catch:{ Exception -> 0x0735 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r12 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r12     // Catch:{ Exception -> 0x0735 }
            boolean r3 = r12 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker     // Catch:{ Exception -> 0x0735 }
            if (r3 == 0) goto L_0x0703
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r4.attributes     // Catch:{ Exception -> 0x0735 }
            r3.remove(r6)     // Catch:{ Exception -> 0x0735 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55 r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55     // Catch:{ Exception -> 0x0735 }
            r3.<init>()     // Catch:{ Exception -> 0x0735 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes     // Catch:{ Exception -> 0x0735 }
            r6.add(r3)     // Catch:{ Exception -> 0x0735 }
            java.lang.String r6 = r12.alt     // Catch:{ Exception -> 0x0735 }
            r3.alt = r6     // Catch:{ Exception -> 0x0735 }
            org.telegram.tgnet.TLRPC$InputStickerSet r6 = r12.stickerset     // Catch:{ Exception -> 0x0735 }
            if (r6 == 0) goto L_0x06ee
            org.telegram.tgnet.TLRPC$InputStickerSet r6 = r12.stickerset     // Catch:{ Exception -> 0x0735 }
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x0735 }
            if (r6 == 0) goto L_0x0682
            org.telegram.tgnet.TLRPC$InputStickerSet r6 = r12.stickerset     // Catch:{ Exception -> 0x05b3 }
            java.lang.String r6 = r6.short_name     // Catch:{ Exception -> 0x05b3 }
            r22 = r5
            goto L_0x0690
        L_0x0682:
            org.telegram.messenger.MediaDataController r6 = r51.getMediaDataController()     // Catch:{ Exception -> 0x0735 }
            org.telegram.tgnet.TLRPC$InputStickerSet r4 = r12.stickerset     // Catch:{ Exception -> 0x0735 }
            r22 = r5
            long r4 = r4.id     // Catch:{ Exception -> 0x06ec }
            java.lang.String r6 = r6.getStickerSetName(r4)     // Catch:{ Exception -> 0x06ec }
        L_0x0690:
            boolean r4 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x06ec }
            if (r4 != 0) goto L_0x06ad
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x06a8 }
            r4.<init>()     // Catch:{ Exception -> 0x06a8 }
            r3.stickerset = r4     // Catch:{ Exception -> 0x06a8 }
            r4.short_name = r6     // Catch:{ Exception -> 0x06a8 }
            r4 = 0
            r14 = 2
            r5 = r51
            r71 = r8
            r7 = r61
            goto L_0x06dc
        L_0x06a8:
            r0 = move-exception
            r2 = r0
            r1 = r7
            goto L_0x073d
        L_0x06ad:
            org.telegram.tgnet.TLRPC$InputStickerSet r4 = r12.stickerset     // Catch:{ Exception -> 0x06ec }
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetID     // Catch:{ Exception -> 0x06ec }
            if (r4 == 0) goto L_0x06cd
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r4 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x06ec }
            r6 = 8
            r14 = 2
            r5 = r51
            r71 = r8
            r7 = r61
            r4.<init>(r7)     // Catch:{ Exception -> 0x0701 }
            r4.encryptedChat = r9     // Catch:{ Exception -> 0x0701 }
            r4.locationParent = r3     // Catch:{ Exception -> 0x0701 }
            r6 = 5
            r4.type = r6     // Catch:{ Exception -> 0x0701 }
            org.telegram.tgnet.TLRPC$InputStickerSet r6 = r12.stickerset     // Catch:{ Exception -> 0x0701 }
            r4.parentObject = r6     // Catch:{ Exception -> 0x0701 }
            goto L_0x06d5
        L_0x06cd:
            r14 = 2
            r5 = r51
            r71 = r8
            r7 = r61
            r4 = 0
        L_0x06d5:
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r6 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0701 }
            r6.<init>()     // Catch:{ Exception -> 0x0701 }
            r3.stickerset = r6     // Catch:{ Exception -> 0x0701 }
        L_0x06dc:
            r28 = r59
            r3 = r66
            r12 = r71
            r14 = r18
            r6 = r22
            r18 = r4
            r4 = 8
            goto L_0x0757
        L_0x06ec:
            r0 = move-exception
            goto L_0x0738
        L_0x06ee:
            r14 = 2
            r22 = r5
            r71 = r8
            r5 = r51
            r7 = r61
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0701 }
            r4.<init>()     // Catch:{ Exception -> 0x0701 }
            r3.stickerset = r4     // Catch:{ Exception -> 0x0701 }
            r4 = 8
            goto L_0x0729
        L_0x0701:
            r0 = move-exception
            goto L_0x073a
        L_0x0703:
            r4 = 8
            r14 = 2
            r22 = r5
            r71 = r8
            r5 = r51
            r7 = r61
            int r6 = r6 + 1
            r4 = r58
            r14 = r66
            r8 = r71
            r7 = r73
            r3 = r74
            r5 = r22
            goto L_0x064a
        L_0x071e:
            r4 = 8
            r14 = 2
            r22 = r5
            r71 = r8
            r5 = r51
            r7 = r61
        L_0x0729:
            r28 = r59
            r3 = r66
            r12 = r71
            r14 = r18
            r6 = r22
            goto L_0x03ff
        L_0x0735:
            r0 = move-exception
            r22 = r5
        L_0x0738:
            r5 = r51
        L_0x073a:
            r1 = r73
            r2 = r0
        L_0x073d:
            r5 = r22
            goto L_0x02f3
        L_0x0741:
            r0 = move-exception
            r5 = r51
            goto L_0x055d
        L_0x0746:
            r4 = 8
            r5 = r51
            r7 = r61
            r2 = r30
            r3 = r66
            r14 = r18
            r6 = 0
            r18 = 0
            r28 = 0
        L_0x0757:
            if (r13 == 0) goto L_0x076f
            boolean r30 = r69.isEmpty()     // Catch:{ Exception -> 0x0768 }
            if (r30 != 0) goto L_0x076f
            r6.entities = r13     // Catch:{ Exception -> 0x0768 }
            int r4 = r6.flags     // Catch:{ Exception -> 0x0768 }
            r4 = r4 | 128(0x80, float:1.794E-43)
            r6.flags = r4     // Catch:{ Exception -> 0x0768 }
            goto L_0x076f
        L_0x0768:
            r0 = move-exception
            r1 = r73
            r2 = r0
            r5 = r6
            goto L_0x02f3
        L_0x076f:
            if (r14 == 0) goto L_0x0774
            r6.message = r14     // Catch:{ Exception -> 0x0768 }
            goto L_0x077a
        L_0x0774:
            java.lang.String r4 = r6.message     // Catch:{ Exception -> 0x0768 }
            if (r4 != 0) goto L_0x077a
            r6.message = r11     // Catch:{ Exception -> 0x0768 }
        L_0x077a:
            java.lang.String r4 = r6.attachPath     // Catch:{ Exception -> 0x0768 }
            if (r4 != 0) goto L_0x0780
            r6.attachPath = r11     // Catch:{ Exception -> 0x0768 }
        L_0x0780:
            org.telegram.messenger.UserConfig r4 = r51.getUserConfig()     // Catch:{ Exception -> 0x0768 }
            int r4 = r4.getNewMessageId()     // Catch:{ Exception -> 0x0768 }
            r6.id = r4     // Catch:{ Exception -> 0x0768 }
            r6.local_id = r4     // Catch:{ Exception -> 0x0768 }
            r4 = 1
            r6.out = r4     // Catch:{ Exception -> 0x0768 }
            if (r23 == 0) goto L_0x07a5
            if (r27 == 0) goto L_0x07a5
            org.telegram.tgnet.TLRPC$TL_peerChannel r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ Exception -> 0x0768 }
            r4.<init>()     // Catch:{ Exception -> 0x0768 }
            r6.from_id = r4     // Catch:{ Exception -> 0x0768 }
            r66 = r3
            r71 = r12
            r3 = r27
            int r12 = r3.channel_id     // Catch:{ Exception -> 0x0768 }
            r4.channel_id = r12     // Catch:{ Exception -> 0x0768 }
            goto L_0x07cf
        L_0x07a5:
            r66 = r3
            r71 = r12
            r3 = r27
            if (r26 == 0) goto L_0x07b8
            org.telegram.messenger.MessagesController r4 = r51.getMessagesController()     // Catch:{ Exception -> 0x0768 }
            org.telegram.tgnet.TLRPC$Peer r4 = r4.getPeer(r2)     // Catch:{ Exception -> 0x0768 }
            r6.from_id = r4     // Catch:{ Exception -> 0x0768 }
            goto L_0x07cf
        L_0x07b8:
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x0768 }
            r4.<init>()     // Catch:{ Exception -> 0x0768 }
            r6.from_id = r4     // Catch:{ Exception -> 0x0768 }
            org.telegram.messenger.UserConfig r12 = r51.getUserConfig()     // Catch:{ Exception -> 0x0768 }
            int r12 = r12.getClientUserId()     // Catch:{ Exception -> 0x0768 }
            r4.user_id = r12     // Catch:{ Exception -> 0x0768 }
            int r4 = r6.flags     // Catch:{ Exception -> 0x0768 }
            r4 = r4 | 256(0x100, float:3.59E-43)
            r6.flags = r4     // Catch:{ Exception -> 0x0768 }
        L_0x07cf:
            org.telegram.messenger.UserConfig r4 = r51.getUserConfig()     // Catch:{ Exception -> 0x0768 }
            r12 = 0
            r4.saveConfig(r12)     // Catch:{ Exception -> 0x0768 }
            r12 = r52
            r52 = r55
            r4 = r57
            r34 = r58
            r35 = r60
            r36 = r66
            r13 = r74
            r60 = r75
            r1 = r6
            r26 = r10
            r37 = r14
            r14 = r19
            r19 = r54
            r6 = r71
            goto L_0x0142
        L_0x07f4:
            if (r10 == 0) goto L_0x0825
            r54 = r4
            int r4 = r5.currentAccount     // Catch:{ Exception -> 0x081d }
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4)     // Catch:{ Exception -> 0x081d }
            r55 = r12
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x081d }
            r12.<init>()     // Catch:{ Exception -> 0x081d }
            r66 = r14
            java.lang.String r14 = "silent_"
            r12.append(r14)     // Catch:{ Exception -> 0x081d }
            r12.append(r7)     // Catch:{ Exception -> 0x081d }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x081d }
            r14 = 0
            boolean r4 = r4.getBoolean(r12, r14)     // Catch:{ Exception -> 0x081d }
            if (r4 == 0) goto L_0x081b
            goto L_0x082b
        L_0x081b:
            r4 = 0
            goto L_0x082c
        L_0x081d:
            r0 = move-exception
            r2 = r0
            r5 = r1
            r12 = 0
            r1 = r73
            goto L_0x1b32
        L_0x0825:
            r54 = r4
            r55 = r12
            r66 = r14
        L_0x082b:
            r4 = 1
        L_0x082c:
            r1.silent = r4     // Catch:{ Exception -> 0x1b2a }
            long r7 = r1.random_id     // Catch:{ Exception -> 0x1b2a }
            int r4 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x083a
            long r7 = r51.getNextRandomId()     // Catch:{ Exception -> 0x081d }
            r1.random_id = r7     // Catch:{ Exception -> 0x081d }
        L_0x083a:
            java.lang.String r8 = "bot"
            java.lang.String r12 = "bot_name"
            if (r6 == 0) goto L_0x086b
            boolean r4 = r6.containsKey(r8)     // Catch:{ Exception -> 0x081d }
            if (r4 == 0) goto L_0x086b
            if (r9 == 0) goto L_0x0855
            java.lang.Object r4 = r6.get(r12)     // Catch:{ Exception -> 0x081d }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x081d }
            r1.via_bot_name = r4     // Catch:{ Exception -> 0x081d }
            if (r4 != 0) goto L_0x0865
            r1.via_bot_name = r11     // Catch:{ Exception -> 0x081d }
            goto L_0x0865
        L_0x0855:
            java.lang.Object r4 = r6.get(r8)     // Catch:{ Exception -> 0x081d }
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4     // Catch:{ Exception -> 0x081d }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ Exception -> 0x081d }
            int r4 = r4.intValue()     // Catch:{ Exception -> 0x081d }
            r1.via_bot_id = r4     // Catch:{ Exception -> 0x081d }
        L_0x0865:
            int r4 = r1.flags     // Catch:{ Exception -> 0x081d }
            r4 = r4 | 2048(0x800, float:2.87E-42)
            r1.flags = r4     // Catch:{ Exception -> 0x081d }
        L_0x086b:
            r1.params = r6     // Catch:{ Exception -> 0x1b2a }
            if (r15 == 0) goto L_0x0879
            boolean r4 = r15.resendAsIs     // Catch:{ Exception -> 0x081d }
            if (r4 != 0) goto L_0x0874
            goto L_0x0879
        L_0x0874:
            r57 = r8
            r7 = r61
            goto L_0x08d8
        L_0x0879:
            r57 = r8
            r7 = r61
            if (r73 == 0) goto L_0x0882
            r4 = r73
            goto L_0x088a
        L_0x0882:
            org.telegram.tgnet.ConnectionsManager r4 = r51.getConnectionsManager()     // Catch:{ Exception -> 0x1b2a }
            int r4 = r4.getCurrentTime()     // Catch:{ Exception -> 0x1b2a }
        L_0x088a:
            r1.date = r4     // Catch:{ Exception -> 0x1b2a }
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x1b2a }
            if (r4 == 0) goto L_0x08d5
            if (r73 != 0) goto L_0x089d
            if (r23 == 0) goto L_0x089d
            r4 = 1
            r1.views = r4     // Catch:{ Exception -> 0x081d }
            int r4 = r1.flags     // Catch:{ Exception -> 0x081d }
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r1.flags = r4     // Catch:{ Exception -> 0x081d }
        L_0x089d:
            org.telegram.messenger.MessagesController r4 = r51.getMessagesController()     // Catch:{ Exception -> 0x081d }
            int r14 = r3.channel_id     // Catch:{ Exception -> 0x081d }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r14)     // Catch:{ Exception -> 0x081d }
            if (r4 == 0) goto L_0x08d8
            boolean r14 = r4.megagroup     // Catch:{ Exception -> 0x081d }
            if (r14 == 0) goto L_0x08bc
            int r4 = r1.flags     // Catch:{ Exception -> 0x081d }
            r14 = -2147483648(0xfffffffvar_, float:-0.0)
            r4 = r4 | r14
            r1.flags = r4     // Catch:{ Exception -> 0x081d }
            r14 = 1
            r1.unread = r14     // Catch:{ Exception -> 0x081d }
            goto L_0x08d8
        L_0x08bc:
            r14 = 1
            r1.post = r14     // Catch:{ Exception -> 0x081d }
            boolean r4 = r4.signatures     // Catch:{ Exception -> 0x081d }
            if (r4 == 0) goto L_0x08d8
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x081d }
            r4.<init>()     // Catch:{ Exception -> 0x081d }
            r1.from_id = r4     // Catch:{ Exception -> 0x081d }
            org.telegram.messenger.UserConfig r14 = r51.getUserConfig()     // Catch:{ Exception -> 0x081d }
            int r14 = r14.getClientUserId()     // Catch:{ Exception -> 0x081d }
            r4.user_id = r14     // Catch:{ Exception -> 0x081d }
            goto L_0x08d8
        L_0x08d5:
            r4 = 1
            r1.unread = r4     // Catch:{ Exception -> 0x1b2a }
        L_0x08d8:
            int r4 = r1.flags     // Catch:{ Exception -> 0x1b2a }
            r4 = r4 | 512(0x200, float:7.175E-43)
            r1.flags = r4     // Catch:{ Exception -> 0x1b2a }
            r1.dialog_id = r7     // Catch:{ Exception -> 0x1b2a }
            r14 = r64
            if (r14 == 0) goto L_0x0937
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = new org.telegram.tgnet.TLRPC$TL_messageReplyHeader     // Catch:{ Exception -> 0x081d }
            r4.<init>()     // Catch:{ Exception -> 0x081d }
            r1.reply_to = r4     // Catch:{ Exception -> 0x081d }
            if (r9 == 0) goto L_0x0909
            r27 = r3
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x081d }
            r58 = r6
            long r6 = r3.random_id     // Catch:{ Exception -> 0x081d }
            int r3 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x090d
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x081d }
            long r6 = r3.random_id     // Catch:{ Exception -> 0x081d }
            r4.reply_to_random_id = r6     // Catch:{ Exception -> 0x081d }
            int r3 = r1.flags     // Catch:{ Exception -> 0x081d }
            r4 = 8
            r3 = r3 | r4
            r1.flags = r3     // Catch:{ Exception -> 0x081d }
            r4 = 8
            goto L_0x0914
        L_0x0909:
            r27 = r3
            r58 = r6
        L_0x090d:
            int r3 = r1.flags     // Catch:{ Exception -> 0x081d }
            r4 = 8
            r3 = r3 | r4
            r1.flags = r3     // Catch:{ Exception -> 0x081d }
        L_0x0914:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r1.reply_to     // Catch:{ Exception -> 0x081d }
            int r6 = r64.getId()     // Catch:{ Exception -> 0x081d }
            r3.reply_to_msg_id = r6     // Catch:{ Exception -> 0x081d }
            r8 = r65
            r7 = r69
            if (r8 == 0) goto L_0x0941
            if (r8 == r14) goto L_0x0941
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r1.reply_to     // Catch:{ Exception -> 0x081d }
            int r6 = r65.getId()     // Catch:{ Exception -> 0x081d }
            r3.reply_to_top_id = r6     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r1.reply_to     // Catch:{ Exception -> 0x081d }
            int r6 = r3.flags     // Catch:{ Exception -> 0x081d }
            r22 = 2
            r6 = r6 | 2
            r3.flags = r6     // Catch:{ Exception -> 0x081d }
            goto L_0x0941
        L_0x0937:
            r8 = r65
            r7 = r69
            r27 = r3
            r58 = r6
            r4 = 8
        L_0x0941:
            if (r25 == 0) goto L_0x095d
            org.telegram.tgnet.TLRPC$TL_messageReplies r3 = new org.telegram.tgnet.TLRPC$TL_messageReplies     // Catch:{ Exception -> 0x081d }
            r3.<init>()     // Catch:{ Exception -> 0x081d }
            r1.replies = r3     // Catch:{ Exception -> 0x081d }
            r6 = 1
            r3.comments = r6     // Catch:{ Exception -> 0x081d }
            r4 = r25
            r3.channel_id = r4     // Catch:{ Exception -> 0x081d }
            int r4 = r3.flags     // Catch:{ Exception -> 0x081d }
            r4 = r4 | r6
            r3.flags = r4     // Catch:{ Exception -> 0x081d }
            int r3 = r1.flags     // Catch:{ Exception -> 0x081d }
            r4 = 8388608(0x800000, float:1.17549435E-38)
            r3 = r3 | r4
            r1.flags = r3     // Catch:{ Exception -> 0x081d }
        L_0x095d:
            r3 = r70
            if (r3 == 0) goto L_0x096b
            if (r9 != 0) goto L_0x096b
            int r4 = r1.flags     // Catch:{ Exception -> 0x081d }
            r4 = r4 | 64
            r1.flags = r4     // Catch:{ Exception -> 0x081d }
            r1.reply_markup = r3     // Catch:{ Exception -> 0x081d }
        L_0x096b:
            if (r2 == 0) goto L_0x0999
            org.telegram.messenger.MessagesController r3 = r51.getMessagesController()     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$Peer r3 = r3.getPeer(r2)     // Catch:{ Exception -> 0x081d }
            r1.peer_id = r3     // Catch:{ Exception -> 0x081d }
            if (r2 <= 0) goto L_0x0994
            org.telegram.messenger.MessagesController r3 = r51.getMessagesController()     // Catch:{ Exception -> 0x081d }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)     // Catch:{ Exception -> 0x081d }
            if (r2 != 0) goto L_0x098d
            int r2 = r1.id     // Catch:{ Exception -> 0x081d }
            r5.processSentMessage(r2)     // Catch:{ Exception -> 0x081d }
            return
        L_0x098d:
            boolean r2 = r2.bot     // Catch:{ Exception -> 0x081d }
            if (r2 == 0) goto L_0x0994
            r2 = 0
            r1.unread = r2     // Catch:{ Exception -> 0x081d }
        L_0x0994:
            r2 = r24
            r3 = 1
            goto L_0x0a4f
        L_0x0999:
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x1b2a }
            r2.<init>()     // Catch:{ Exception -> 0x1b2a }
            r1.peer_id = r2     // Catch:{ Exception -> 0x1b2a }
            int r2 = r9.participant_id     // Catch:{ Exception -> 0x1b2a }
            org.telegram.messenger.UserConfig r3 = r51.getUserConfig()     // Catch:{ Exception -> 0x1b2a }
            int r3 = r3.getClientUserId()     // Catch:{ Exception -> 0x1b2a }
            if (r2 != r3) goto L_0x09b3
            org.telegram.tgnet.TLRPC$Peer r2 = r1.peer_id     // Catch:{ Exception -> 0x081d }
            int r3 = r9.admin_id     // Catch:{ Exception -> 0x081d }
            r2.user_id = r3     // Catch:{ Exception -> 0x081d }
            goto L_0x09b9
        L_0x09b3:
            org.telegram.tgnet.TLRPC$Peer r2 = r1.peer_id     // Catch:{ Exception -> 0x1b2a }
            int r3 = r9.participant_id     // Catch:{ Exception -> 0x1b2a }
            r2.user_id = r3     // Catch:{ Exception -> 0x1b2a }
        L_0x09b9:
            if (r13 == 0) goto L_0x09be
            r1.ttl = r13     // Catch:{ Exception -> 0x081d }
            goto L_0x09d4
        L_0x09be:
            int r2 = r9.ttl     // Catch:{ Exception -> 0x1b2a }
            r1.ttl = r2     // Catch:{ Exception -> 0x1b2a }
            if (r2 == 0) goto L_0x09d4
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x081d }
            if (r3 == 0) goto L_0x09d4
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x081d }
            r3.ttl_seconds = r2     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x081d }
            int r3 = r2.flags     // Catch:{ Exception -> 0x081d }
            r4 = 4
            r3 = r3 | r4
            r2.flags = r3     // Catch:{ Exception -> 0x081d }
        L_0x09d4:
            int r2 = r1.ttl     // Catch:{ Exception -> 0x1b2a }
            if (r2 == 0) goto L_0x0994
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x081d }
            if (r2 == 0) goto L_0x0994
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r1)     // Catch:{ Exception -> 0x081d }
            if (r2 == 0) goto L_0x0a13
            r2 = 0
        L_0x09e5:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x081d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes     // Catch:{ Exception -> 0x081d }
            int r3 = r3.size()     // Catch:{ Exception -> 0x081d }
            if (r2 >= r3) goto L_0x0a07
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x081d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes     // Catch:{ Exception -> 0x081d }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3     // Catch:{ Exception -> 0x081d }
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ Exception -> 0x081d }
            if (r4 == 0) goto L_0x0a04
            int r2 = r3.duration     // Catch:{ Exception -> 0x081d }
            goto L_0x0a08
        L_0x0a04:
            int r2 = r2 + 1
            goto L_0x09e5
        L_0x0a07:
            r2 = 0
        L_0x0a08:
            int r3 = r1.ttl     // Catch:{ Exception -> 0x081d }
            r4 = 1
            int r2 = r2 + r4
            int r2 = java.lang.Math.max(r3, r2)     // Catch:{ Exception -> 0x081d }
            r1.ttl = r2     // Catch:{ Exception -> 0x081d }
            goto L_0x0994
        L_0x0a13:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r1)     // Catch:{ Exception -> 0x081d }
            if (r2 != 0) goto L_0x0a1f
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r1)     // Catch:{ Exception -> 0x081d }
            if (r2 == 0) goto L_0x0994
        L_0x0a1f:
            r2 = 0
        L_0x0a20:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x081d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes     // Catch:{ Exception -> 0x081d }
            int r3 = r3.size()     // Catch:{ Exception -> 0x081d }
            if (r2 >= r3) goto L_0x0a42
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x081d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes     // Catch:{ Exception -> 0x081d }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x081d }
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3     // Catch:{ Exception -> 0x081d }
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x081d }
            if (r4 == 0) goto L_0x0a3f
            int r2 = r3.duration     // Catch:{ Exception -> 0x081d }
            goto L_0x0a43
        L_0x0a3f:
            int r2 = r2 + 1
            goto L_0x0a20
        L_0x0a42:
            r2 = 0
        L_0x0a43:
            int r3 = r1.ttl     // Catch:{ Exception -> 0x081d }
            r4 = 1
            int r2 = r2 + r4
            int r2 = java.lang.Math.max(r3, r2)     // Catch:{ Exception -> 0x081d }
            r1.ttl = r2     // Catch:{ Exception -> 0x081d }
            goto L_0x0994
        L_0x0a4f:
            if (r2 == r3) goto L_0x0a60
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r1)     // Catch:{ Exception -> 0x081d }
            if (r2 != 0) goto L_0x0a5d
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r1)     // Catch:{ Exception -> 0x081d }
            if (r2 == 0) goto L_0x0a60
        L_0x0a5d:
            r2 = 1
            r1.media_unread = r2     // Catch:{ Exception -> 0x081d }
        L_0x0a60:
            org.telegram.tgnet.TLRPC$Peer r2 = r1.from_id     // Catch:{ Exception -> 0x1b2a }
            if (r2 != 0) goto L_0x0a68
            org.telegram.tgnet.TLRPC$Peer r2 = r1.peer_id     // Catch:{ Exception -> 0x081d }
            r1.from_id = r2     // Catch:{ Exception -> 0x081d }
        L_0x0a68:
            r2 = 1
            r1.send_state = r2     // Catch:{ Exception -> 0x1b2a }
            org.telegram.messenger.MessageObject r6 = new org.telegram.messenger.MessageObject     // Catch:{ Exception -> 0x1b2a }
            int r3 = r5.currentAccount     // Catch:{ Exception -> 0x1b2a }
            r23 = 1
            r24 = 1
            r2 = r6
            r4 = r27
            r38 = r29
            r40 = r54
            r39 = r4
            r41 = r31
            r4 = r1
            r42 = r20
            r5 = r64
            r14 = r58
            r54 = r13
            r13 = r52
            r52 = r12
            r12 = r6
            r6 = r23
            r20 = r11
            r53 = r13
            r8 = 0
            r10 = r61
            r13 = r73
            r7 = r24
            r2.<init>((int) r3, (org.telegram.tgnet.TLRPC$Message) r4, (org.telegram.messenger.MessageObject) r5, (boolean) r6, (boolean) r7)     // Catch:{ Exception -> 0x1b23 }
            r2 = 1
            r12.wasJustSent = r2     // Catch:{ Exception -> 0x1b1a }
            if (r13 == 0) goto L_0x0aa3
            r2 = 1
            goto L_0x0aa4
        L_0x0aa3:
            r2 = 0
        L_0x0aa4:
            r12.scheduled = r2     // Catch:{ Exception -> 0x1b1a }
            boolean r2 = r12.isForwarded()     // Catch:{ Exception -> 0x1b1a }
            if (r2 != 0) goto L_0x0aca
            int r2 = r12.type     // Catch:{ Exception -> 0x0ac4 }
            r3 = 3
            if (r2 == r3) goto L_0x0ab8
            if (r56 != 0) goto L_0x0ab8
            int r2 = r12.type     // Catch:{ Exception -> 0x0ac4 }
            r3 = 2
            if (r2 != r3) goto L_0x0aca
        L_0x0ab8:
            java.lang.String r2 = r1.attachPath     // Catch:{ Exception -> 0x0ac4 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0ac4 }
            if (r2 != 0) goto L_0x0aca
            r2 = 1
            r12.attachPathExists = r2     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0aca
        L_0x0ac4:
            r0 = move-exception
        L_0x0ac5:
            r2 = r0
            r5 = r1
        L_0x0ac7:
            r1 = r13
            goto L_0x1b32
        L_0x0aca:
            org.telegram.messenger.VideoEditedInfo r2 = r12.videoEditedInfo     // Catch:{ Exception -> 0x1b1a }
            if (r2 == 0) goto L_0x0ad3
            if (r56 != 0) goto L_0x0ad3
            org.telegram.messenger.VideoEditedInfo r2 = r12.videoEditedInfo     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0ad5
        L_0x0ad3:
            r2 = r56
        L_0x0ad5:
            if (r14 == 0) goto L_0x0aff
            java.lang.String r3 = "groupId"
            java.lang.Object r3 = r14.get(r3)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0ac4 }
            if (r3 == 0) goto L_0x0af3
            java.lang.Long r3 = org.telegram.messenger.Utilities.parseLong(r3)     // Catch:{ Exception -> 0x0ac4 }
            long r3 = r3.longValue()     // Catch:{ Exception -> 0x0ac4 }
            r1.grouped_id = r3     // Catch:{ Exception -> 0x0ac4 }
            int r5 = r1.flags     // Catch:{ Exception -> 0x0ac4 }
            r6 = 131072(0x20000, float:1.83671E-40)
            r5 = r5 | r6
            r1.flags = r5     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0af5
        L_0x0af3:
            r3 = r16
        L_0x0af5:
            java.lang.String r5 = "final"
            java.lang.Object r5 = r14.get(r5)     // Catch:{ Exception -> 0x0ac4 }
            if (r5 == 0) goto L_0x0b01
            r5 = 1
            goto L_0x0b02
        L_0x0aff:
            r3 = r16
        L_0x0b01:
            r5 = 0
        L_0x0b02:
            int r6 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r6 != 0) goto L_0x0b5e
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x0b59 }
            r5.<init>()     // Catch:{ Exception -> 0x0b59 }
            r5.add(r12)     // Catch:{ Exception -> 0x0b59 }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x0b59 }
            r6.<init>()     // Catch:{ Exception -> 0x0b59 }
            r6.add(r1)     // Catch:{ Exception -> 0x0b59 }
            r7 = r51
            int r8 = r7.currentAccount     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.messenger.MessagesStorage r43 = org.telegram.messenger.MessagesStorage.getInstance(r8)     // Catch:{ Exception -> 0x0ac4 }
            r45 = 0
            r46 = 1
            r47 = 0
            r48 = 0
            if (r13 == 0) goto L_0x0b2b
            r49 = 1
            goto L_0x0b2d
        L_0x0b2b:
            r49 = 0
        L_0x0b2d:
            r44 = r6
            r43.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r44, (boolean) r45, (boolean) r46, (boolean) r47, (int) r48, (boolean) r49)     // Catch:{ Exception -> 0x0ac4 }
            int r6 = r7.currentAccount     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)     // Catch:{ Exception -> 0x0ac4 }
            if (r13 == 0) goto L_0x0b3c
            r8 = 1
            goto L_0x0b3d
        L_0x0b3c:
            r8 = 0
        L_0x0b3d:
            r6.updateInterfaceWithMessages(r10, r5, r8)     // Catch:{ Exception -> 0x0ac4 }
            if (r13 != 0) goto L_0x0b53
            int r5 = r7.currentAccount     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)     // Catch:{ Exception -> 0x0ac4 }
            int r6 = org.telegram.messenger.NotificationCenter.dialogsNeedReload     // Catch:{ Exception -> 0x0ac4 }
            r56 = r2
            r8 = 0
            java.lang.Object[] r2 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x0ac4 }
            r5.postNotificationName(r6, r2)     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0b55
        L_0x0b53:
            r56 = r2
        L_0x0b55:
            r2 = r18
            r6 = 0
            goto L_0x0bac
        L_0x0b59:
            r0 = move-exception
            r7 = r51
            goto L_0x0ac5
        L_0x0b5e:
            r7 = r51
            r56 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1b1a }
            r2.<init>()     // Catch:{ Exception -> 0x1b1a }
            java.lang.String r6 = "group_"
            r2.append(r6)     // Catch:{ Exception -> 0x1b1a }
            r2.append(r3)     // Catch:{ Exception -> 0x1b1a }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x1b1a }
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r6 = r7.delayedMessages     // Catch:{ Exception -> 0x1b1a }
            java.lang.Object r2 = r6.get(r2)     // Catch:{ Exception -> 0x1b1a }
            java.util.ArrayList r2 = (java.util.ArrayList) r2     // Catch:{ Exception -> 0x1b1a }
            if (r2 == 0) goto L_0x0b86
            r6 = 0
            java.lang.Object r2 = r2.get(r6)     // Catch:{ Exception -> 0x0ac4 }
            r18 = r2
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r18 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r18     // Catch:{ Exception -> 0x0ac4 }
        L_0x0b86:
            if (r18 != 0) goto L_0x0b9a
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0ac4 }
            r2.<init>(r10)     // Catch:{ Exception -> 0x0ac4 }
            r2.initForGroup(r3)     // Catch:{ Exception -> 0x0ac4 }
            r2.encryptedChat = r9     // Catch:{ Exception -> 0x0ac4 }
            if (r13 == 0) goto L_0x0b96
            r6 = 1
            goto L_0x0b97
        L_0x0b96:
            r6 = 0
        L_0x0b97:
            r2.scheduled = r6     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0b9c
        L_0x0b9a:
            r2 = r18
        L_0x0b9c:
            r6 = 0
            r2.performMediaUpload = r6     // Catch:{ Exception -> 0x1b1a }
            r6 = 0
            r2.photoSize = r6     // Catch:{ Exception -> 0x1b1a }
            r2.videoEditedInfo = r6     // Catch:{ Exception -> 0x1b1a }
            r2.httpLocation = r6     // Catch:{ Exception -> 0x1b1a }
            if (r5 == 0) goto L_0x0bac
            int r5 = r1.id     // Catch:{ Exception -> 0x0ac4 }
            r2.finalGroupMessage = r5     // Catch:{ Exception -> 0x0ac4 }
        L_0x0bac:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1b1a }
            if (r5 == 0) goto L_0x0c1e
            r5 = r39
            if (r5 == 0) goto L_0x0c1b
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ac4 }
            r8.<init>()     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r6 = "send message user_id = "
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            int r6 = r5.user_id     // Catch:{ Exception -> 0x0ac4 }
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r6 = " chat_id = "
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            int r6 = r5.chat_id     // Catch:{ Exception -> 0x0ac4 }
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r6 = " channel_id = "
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            int r6 = r5.channel_id     // Catch:{ Exception -> 0x0ac4 }
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r6 = " access_hash = "
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            long r10 = r5.access_hash     // Catch:{ Exception -> 0x0ac4 }
            r8.append(r10)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r6 = " notify = "
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            r10 = r61
            r6 = r72
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r6 = " silent = "
            r8.append(r6)     // Catch:{ Exception -> 0x0ac4 }
            int r6 = r7.currentAccount     // Catch:{ Exception -> 0x0ac4 }
            android.content.SharedPreferences r6 = org.telegram.messenger.MessagesController.getNotificationsSettings(r6)     // Catch:{ Exception -> 0x0ac4 }
            r70 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0ac4 }
            r3.<init>()     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r4 = "silent_"
            r3.append(r4)     // Catch:{ Exception -> 0x0ac4 }
            r3.append(r10)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0ac4 }
            r4 = 0
            boolean r3 = r6.getBoolean(r3, r4)     // Catch:{ Exception -> 0x0ac4 }
            r8.append(r3)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r3 = r8.toString()     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0CLASSNAME
        L_0x0c1b:
            r70 = r3
            goto L_0x0CLASSNAME
        L_0x0c1e:
            r70 = r3
            r5 = r39
        L_0x0CLASSNAME:
            if (r66 == 0) goto L_0x19dc
            r3 = r66
            r4 = 9
            if (r3 != r4) goto L_0x0CLASSNAME
            if (r55 == 0) goto L_0x0CLASSNAME
            if (r9 == 0) goto L_0x0CLASSNAME
            goto L_0x19dc
        L_0x0CLASSNAME:
            r4 = 1
            if (r3 < r4) goto L_0x0c3c
            r4 = 3
            if (r3 <= r4) goto L_0x0CLASSNAME
            goto L_0x0c3c
        L_0x0CLASSNAME:
            r4 = 8
        L_0x0CLASSNAME:
            r8 = 0
            goto L_0x0d93
        L_0x0c3c:
            r4 = 5
            if (r3 < r4) goto L_0x0CLASSNAME
            r4 = 8
            if (r3 <= r4) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r4 = 8
        L_0x0CLASSNAME:
            r6 = 9
            if (r3 != r6) goto L_0x0c4c
            if (r9 != 0) goto L_0x0CLASSNAME
        L_0x0c4c:
            r6 = 10
            if (r3 == r6) goto L_0x0CLASSNAME
            r6 = 11
            if (r3 != r6) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r6 = 4
            if (r3 != r6) goto L_0x0d08
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r2 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages     // Catch:{ Exception -> 0x0ac4 }
            r2.<init>()     // Catch:{ Exception -> 0x0ac4 }
            r2.to_peer = r5     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner     // Catch:{ Exception -> 0x0ac4 }
            boolean r3 = r3.with_my_score     // Catch:{ Exception -> 0x0ac4 }
            r2.with_my_score = r3     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner     // Catch:{ Exception -> 0x0ac4 }
            int r3 = r3.ttl     // Catch:{ Exception -> 0x0ac4 }
            if (r3 == 0) goto L_0x0CLASSNAME
            org.telegram.messenger.MessagesController r3 = r51.getMessagesController()     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$Message r4 = r15.messageOwner     // Catch:{ Exception -> 0x0ac4 }
            int r4 = r4.ttl     // Catch:{ Exception -> 0x0ac4 }
            int r4 = -r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x0ac4 }
            r4.<init>()     // Catch:{ Exception -> 0x0ac4 }
            r2.from_peer = r4     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$Message r5 = r15.messageOwner     // Catch:{ Exception -> 0x0ac4 }
            int r5 = r5.ttl     // Catch:{ Exception -> 0x0ac4 }
            int r5 = -r5
            r4.channel_id = r5     // Catch:{ Exception -> 0x0ac4 }
            if (r3 == 0) goto L_0x0CLASSNAME
            long r5 = r3.access_hash     // Catch:{ Exception -> 0x0ac4 }
            r4.access_hash = r5     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r3 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0ac4 }
            r3.<init>()     // Catch:{ Exception -> 0x0ac4 }
            r2.from_peer = r3     // Catch:{ Exception -> 0x0ac4 }
        L_0x0CLASSNAME:
            boolean r3 = r1.silent     // Catch:{ Exception -> 0x0ac4 }
            r2.silent = r3     // Catch:{ Exception -> 0x0ac4 }
            if (r13 == 0) goto L_0x0ca6
            r2.schedule_date = r13     // Catch:{ Exception -> 0x0ac4 }
            int r3 = r2.flags     // Catch:{ Exception -> 0x0ac4 }
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r2.flags = r3     // Catch:{ Exception -> 0x0ac4 }
        L_0x0ca6:
            java.util.ArrayList<java.lang.Long> r3 = r2.random_id     // Catch:{ Exception -> 0x0ac4 }
            long r4 = r1.random_id     // Catch:{ Exception -> 0x0ac4 }
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x0ac4 }
            r3.add(r4)     // Catch:{ Exception -> 0x0ac4 }
            int r3 = r68.getId()     // Catch:{ Exception -> 0x0ac4 }
            if (r3 < 0) goto L_0x0cc5
            java.util.ArrayList<java.lang.Integer> r3 = r2.id     // Catch:{ Exception -> 0x0ac4 }
            int r4 = r68.getId()     // Catch:{ Exception -> 0x0ac4 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0ac4 }
            r3.add(r4)     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0cee
        L_0x0cc5:
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner     // Catch:{ Exception -> 0x0ac4 }
            int r3 = r3.fwd_msg_id     // Catch:{ Exception -> 0x0ac4 }
            if (r3 == 0) goto L_0x0cd9
            java.util.ArrayList<java.lang.Integer> r3 = r2.id     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$Message r4 = r15.messageOwner     // Catch:{ Exception -> 0x0ac4 }
            int r4 = r4.fwd_msg_id     // Catch:{ Exception -> 0x0ac4 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0ac4 }
            r3.add(r4)     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0cee
        L_0x0cd9:
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from     // Catch:{ Exception -> 0x0ac4 }
            if (r3 == 0) goto L_0x0cee
            java.util.ArrayList<java.lang.Integer> r3 = r2.id     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$Message r4 = r15.messageOwner     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r4.fwd_from     // Catch:{ Exception -> 0x0ac4 }
            int r4 = r4.channel_post     // Catch:{ Exception -> 0x0ac4 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0ac4 }
            r3.add(r4)     // Catch:{ Exception -> 0x0ac4 }
        L_0x0cee:
            r3 = 0
            r4 = 0
            if (r13 == 0) goto L_0x0cf4
            r5 = 1
            goto L_0x0cf5
        L_0x0cf4:
            r5 = 0
        L_0x0cf5:
            r52 = r51
            r53 = r2
            r54 = r12
            r55 = r3
            r56 = r4
            r57 = r60
            r58 = r5
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58)     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x1b15
        L_0x0d08:
            r2 = 9
            if (r3 != r2) goto L_0x1b15
            org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult     // Catch:{ Exception -> 0x0ac4 }
            r2.<init>()     // Catch:{ Exception -> 0x0ac4 }
            r2.peer = r5     // Catch:{ Exception -> 0x0ac4 }
            long r3 = r1.random_id     // Catch:{ Exception -> 0x0ac4 }
            r2.random_id = r3     // Catch:{ Exception -> 0x0ac4 }
            r3 = r57
            boolean r3 = r14.containsKey(r3)     // Catch:{ Exception -> 0x0ac4 }
            if (r3 != 0) goto L_0x0d21
            r3 = 1
            goto L_0x0d22
        L_0x0d21:
            r3 = 0
        L_0x0d22:
            r2.hide_via = r3     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r1.reply_to     // Catch:{ Exception -> 0x0ac4 }
            if (r3 == 0) goto L_0x0d3a
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r1.reply_to     // Catch:{ Exception -> 0x0ac4 }
            int r3 = r3.reply_to_msg_id     // Catch:{ Exception -> 0x0ac4 }
            if (r3 == 0) goto L_0x0d3a
            int r3 = r2.flags     // Catch:{ Exception -> 0x0ac4 }
            r4 = 1
            r3 = r3 | r4
            r2.flags = r3     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r1.reply_to     // Catch:{ Exception -> 0x0ac4 }
            int r3 = r3.reply_to_msg_id     // Catch:{ Exception -> 0x0ac4 }
            r2.reply_to_msg_id = r3     // Catch:{ Exception -> 0x0ac4 }
        L_0x0d3a:
            boolean r3 = r1.silent     // Catch:{ Exception -> 0x0ac4 }
            r2.silent = r3     // Catch:{ Exception -> 0x0ac4 }
            if (r13 == 0) goto L_0x0d48
            r2.schedule_date = r13     // Catch:{ Exception -> 0x0ac4 }
            int r3 = r2.flags     // Catch:{ Exception -> 0x0ac4 }
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r2.flags = r3     // Catch:{ Exception -> 0x0ac4 }
        L_0x0d48:
            r3 = r26
            java.lang.Object r3 = r14.get(r3)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0ac4 }
            java.lang.Long r3 = org.telegram.messenger.Utilities.parseLong(r3)     // Catch:{ Exception -> 0x0ac4 }
            long r3 = r3.longValue()     // Catch:{ Exception -> 0x0ac4 }
            r2.query_id = r3     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r3 = "id"
            java.lang.Object r3 = r14.get(r3)     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0ac4 }
            r2.id = r3     // Catch:{ Exception -> 0x0ac4 }
            if (r15 != 0) goto L_0x0d79
            r3 = 1
            r2.clear_draft = r3     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.messenger.MediaDataController r3 = r51.getMediaDataController()     // Catch:{ Exception -> 0x0ac4 }
            if (r65 == 0) goto L_0x0d74
            int r4 = r65.getId()     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0d75
        L_0x0d74:
            r4 = 0
        L_0x0d75:
            r5 = 0
            r3.cleanDraft(r10, r4, r5)     // Catch:{ Exception -> 0x0ac4 }
        L_0x0d79:
            r3 = 0
            r4 = 0
            if (r13 == 0) goto L_0x0d7f
            r5 = 1
            goto L_0x0d80
        L_0x0d7f:
            r5 = 0
        L_0x0d80:
            r52 = r51
            r53 = r2
            r54 = r12
            r55 = r3
            r56 = r4
            r57 = r60
            r58 = r5
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58)     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x1b15
        L_0x0d93:
            if (r9 != 0) goto L_0x13e6
            r6 = 1
            if (r3 != r6) goto L_0x0df1
            r6 = r19
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x0ac4 }
            if (r8 == 0) goto L_0x0db8
            org.telegram.tgnet.TLRPC$TL_inputMediaVenue r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue     // Catch:{ Exception -> 0x0ac4 }
            r8.<init>()     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r9 = r6.address     // Catch:{ Exception -> 0x0ac4 }
            r8.address = r9     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r9 = r6.title     // Catch:{ Exception -> 0x0ac4 }
            r8.title = r9     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r9 = r6.provider     // Catch:{ Exception -> 0x0ac4 }
            r8.provider = r9     // Catch:{ Exception -> 0x0ac4 }
            java.lang.String r9 = r6.venue_id     // Catch:{ Exception -> 0x0ac4 }
            r8.venue_id = r9     // Catch:{ Exception -> 0x0ac4 }
            r9 = r20
            r8.venue_type = r9     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0dd1
        L_0x0db8:
            boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive     // Catch:{ Exception -> 0x0ac4 }
            if (r8 == 0) goto L_0x0dcc
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive     // Catch:{ Exception -> 0x0ac4 }
            r8.<init>()     // Catch:{ Exception -> 0x0ac4 }
            int r9 = r6.period     // Catch:{ Exception -> 0x0ac4 }
            r8.period = r9     // Catch:{ Exception -> 0x0ac4 }
            int r9 = r8.flags     // Catch:{ Exception -> 0x0ac4 }
            r10 = 2
            r9 = r9 | r10
            r8.flags = r9     // Catch:{ Exception -> 0x0ac4 }
            goto L_0x0dd1
        L_0x0dcc:
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint     // Catch:{ Exception -> 0x0ac4 }
            r8.<init>()     // Catch:{ Exception -> 0x0ac4 }
        L_0x0dd1:
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r9 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint     // Catch:{ Exception -> 0x0ac4 }
            r9.<init>()     // Catch:{ Exception -> 0x0ac4 }
            r8.geo_point = r9     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$GeoPoint r10 = r6.geo     // Catch:{ Exception -> 0x0ac4 }
            double r10 = r10.lat     // Catch:{ Exception -> 0x0ac4 }
            r9.lat = r10     // Catch:{ Exception -> 0x0ac4 }
            org.telegram.tgnet.TLRPC$GeoPoint r6 = r6.geo     // Catch:{ Exception -> 0x0ac4 }
            double r10 = r6._long     // Catch:{ Exception -> 0x0ac4 }
            r9._long = r10     // Catch:{ Exception -> 0x0ac4 }
            r66 = r3
            r27 = r5
            r5 = r8
            r4 = r42
            r9 = 0
            r3 = r1
            r1 = r60
            goto L_0x1209
        L_0x0df1:
            r9 = r20
            r6 = 2
            if (r3 == r6) goto L_0x1138
            r6 = 9
            if (r3 != r6) goto L_0x0dfe
            if (r53 == 0) goto L_0x0dfe
            goto L_0x1138
        L_0x0dfe:
            r6 = 3
            if (r3 != r6) goto L_0x0f0b
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0var_ }
            r6.<init>()     // Catch:{ Exception -> 0x0var_ }
            r15 = r34
            java.lang.String r8 = r15.mime_type     // Catch:{ Exception -> 0x0var_ }
            r6.mime_type = r8     // Catch:{ Exception -> 0x0var_ }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r15.attributes     // Catch:{ Exception -> 0x0var_ }
            r6.attributes = r8     // Catch:{ Exception -> 0x0var_ }
            if (r28 != 0) goto L_0x0e28
            boolean r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r15)     // Catch:{ Exception -> 0x0ac4 }
            if (r8 != 0) goto L_0x0e25
            if (r56 == 0) goto L_0x0e28
            r8 = r56
            boolean r9 = r8.muted     // Catch:{ Exception -> 0x0ac4 }
            if (r9 != 0) goto L_0x0e36
            boolean r9 = r8.roundVideo     // Catch:{ Exception -> 0x0ac4 }
            if (r9 != 0) goto L_0x0e36
            goto L_0x0e2a
        L_0x0e25:
            r8 = r56
            goto L_0x0e36
        L_0x0e28:
            r8 = r56
        L_0x0e2a:
            r9 = 1
            r6.nosound_video = r9     // Catch:{ Exception -> 0x0var_ }
            boolean r9 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0var_ }
            if (r9 == 0) goto L_0x0e36
            java.lang.String r9 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ Exception -> 0x0ac4 }
        L_0x0e36:
            if (r54 == 0) goto L_0x0e46
            r9 = r54
            r6.ttl_seconds = r9     // Catch:{ Exception -> 0x0ac4 }
            r1.ttl = r9     // Catch:{ Exception -> 0x0ac4 }
            int r9 = r6.flags     // Catch:{ Exception -> 0x0ac4 }
            r18 = 2
            r9 = r9 | 2
            r6.flags = r9     // Catch:{ Exception -> 0x0ac4 }
        L_0x0e46:
            if (r14 == 0) goto L_0x0e8d
            java.lang.String r9 = "masks"
            java.lang.Object r9 = r14.get(r9)     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ Exception -> 0x0var_ }
            if (r9 == 0) goto L_0x0e8d
            org.telegram.tgnet.SerializedData r14 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0var_ }
            byte[] r9 = org.telegram.messenger.Utilities.hexToBytes(r9)     // Catch:{ Exception -> 0x0var_ }
            r14.<init>((byte[]) r9)     // Catch:{ Exception -> 0x0var_ }
            r9 = 0
            int r4 = r14.readInt32(r9)     // Catch:{ Exception -> 0x0var_ }
        L_0x0e60:
            if (r9 >= r4) goto L_0x0e7f
            r52 = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r4 = r6.stickers     // Catch:{ Exception -> 0x0var_ }
            r18 = r1
            r27 = r5
            r5 = 0
            int r1 = r14.readInt32(r5)     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$InputDocument r1 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r14, r1, r5)     // Catch:{ Exception -> 0x0f5b }
            r4.add(r1)     // Catch:{ Exception -> 0x0f5b }
            int r9 = r9 + 1
            r4 = r52
            r1 = r18
            r5 = r27
            goto L_0x0e60
        L_0x0e7f:
            r18 = r1
            r27 = r5
            int r1 = r6.flags     // Catch:{ Exception -> 0x0f5b }
            r4 = 1
            r1 = r1 | r4
            r6.flags = r1     // Catch:{ Exception -> 0x0f5b }
            r14.cleanup()     // Catch:{ Exception -> 0x0f5b }
            goto L_0x0e91
        L_0x0e8d:
            r18 = r1
            r27 = r5
        L_0x0e91:
            long r4 = r15.access_hash     // Catch:{ Exception -> 0x0f5b }
            int r1 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r1 != 0) goto L_0x0e9c
            r5 = r6
            r56 = r8
            r1 = 1
            goto L_0x0ebf
        L_0x0e9c:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0f5b }
            r1.<init>()     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_inputDocument r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0f5b }
            r4.<init>()     // Catch:{ Exception -> 0x0f5b }
            r1.id = r4     // Catch:{ Exception -> 0x0f5b }
            r56 = r8
            long r8 = r15.id     // Catch:{ Exception -> 0x0f5b }
            r4.id = r8     // Catch:{ Exception -> 0x0f5b }
            long r8 = r15.access_hash     // Catch:{ Exception -> 0x0f5b }
            r4.access_hash = r8     // Catch:{ Exception -> 0x0f5b }
            byte[] r5 = r15.file_reference     // Catch:{ Exception -> 0x0f5b }
            r4.file_reference = r5     // Catch:{ Exception -> 0x0f5b }
            if (r5 != 0) goto L_0x0ebd
            r5 = 0
            byte[] r8 = new byte[r5]     // Catch:{ Exception -> 0x0f5b }
            r4.file_reference = r8     // Catch:{ Exception -> 0x0f5b }
        L_0x0ebd:
            r5 = r1
            r1 = 0
        L_0x0ebf:
            if (r2 != 0) goto L_0x0edb
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0f5b }
            r2.<init>(r10)     // Catch:{ Exception -> 0x0f5b }
            r4 = 1
            r2.type = r4     // Catch:{ Exception -> 0x0f5b }
            r2.obj = r12     // Catch:{ Exception -> 0x0f5b }
            r4 = r42
            r2.originalPath = r4     // Catch:{ Exception -> 0x0f5b }
            r8 = r60
            r2.parentObject = r8     // Catch:{ Exception -> 0x0f5b }
            if (r13 == 0) goto L_0x0ed7
            r9 = 1
            goto L_0x0ed8
        L_0x0ed7:
            r9 = 0
        L_0x0ed8:
            r2.scheduled = r9     // Catch:{ Exception -> 0x0f5b }
            goto L_0x0edf
        L_0x0edb:
            r8 = r60
            r4 = r42
        L_0x0edf:
            r2.inputUploadMedia = r6     // Catch:{ Exception -> 0x0f5b }
            r2.performMediaUpload = r1     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r15.thumbs     // Catch:{ Exception -> 0x0f5b }
            boolean r6 = r6.isEmpty()     // Catch:{ Exception -> 0x0f5b }
            if (r6 != 0) goto L_0x0ef8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r15.thumbs     // Catch:{ Exception -> 0x0f5b }
            r9 = 0
            java.lang.Object r6 = r6.get(r9)     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC$PhotoSize) r6     // Catch:{ Exception -> 0x0f5b }
            r2.photoSize = r6     // Catch:{ Exception -> 0x0f5b }
            r2.locationParent = r15     // Catch:{ Exception -> 0x0f5b }
        L_0x0ef8:
            r6 = r56
            r2.videoEditedInfo = r6     // Catch:{ Exception -> 0x0f5b }
            r9 = r1
            r66 = r3
            r1 = r8
            goto L_0x112f
        L_0x0var_:
            r0 = move-exception
            r18 = r1
        L_0x0var_:
            r2 = r0
            r1 = r13
        L_0x0var_:
            r5 = r18
            goto L_0x1b32
        L_0x0f0b:
            r18 = r1
            r27 = r5
            r15 = r34
            r4 = r42
            r6 = 6
            r5 = r54
            r1 = r60
            if (r3 != r6) goto L_0x0f5d
            org.telegram.tgnet.TLRPC$TL_inputMediaContact r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact     // Catch:{ Exception -> 0x0f5b }
            r5.<init>()     // Catch:{ Exception -> 0x0f5b }
            r8 = r40
            java.lang.String r6 = r8.phone     // Catch:{ Exception -> 0x0f5b }
            r5.phone_number = r6     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r6 = r8.first_name     // Catch:{ Exception -> 0x0f5b }
            r5.first_name = r6     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r6 = r8.last_name     // Catch:{ Exception -> 0x0f5b }
            r5.last_name = r6     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r6 = r8.restriction_reason     // Catch:{ Exception -> 0x0f5b }
            boolean r6 = r6.isEmpty()     // Catch:{ Exception -> 0x0f5b }
            if (r6 != 0) goto L_0x0var_
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r6 = r8.restriction_reason     // Catch:{ Exception -> 0x0f5b }
            r10 = 0
            java.lang.Object r6 = r6.get(r10)     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r6 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r6     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r6 = r6.text     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r10 = "BEGIN:VCARD"
            boolean r6 = r6.startsWith(r10)     // Catch:{ Exception -> 0x0f5b }
            if (r6 == 0) goto L_0x0var_
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r6 = r8.restriction_reason     // Catch:{ Exception -> 0x0f5b }
            r8 = 0
            java.lang.Object r6 = r6.get(r8)     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r6 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r6     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r6 = r6.text     // Catch:{ Exception -> 0x0f5b }
            r5.vcard = r6     // Catch:{ Exception -> 0x0f5b }
            goto L_0x1054
        L_0x0var_:
            r5.vcard = r9     // Catch:{ Exception -> 0x0f5b }
            goto L_0x1054
        L_0x0f5b:
            r0 = move-exception
            goto L_0x0var_
        L_0x0f5d:
            r6 = 7
            if (r3 == r6) goto L_0x105f
            r6 = 9
            if (r3 != r6) goto L_0x0var_
            goto L_0x105f
        L_0x0var_:
            r6 = 8
            if (r3 != r6) goto L_0x0fe4
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0f5b }
            r2.<init>()     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r6 = r15.mime_type     // Catch:{ Exception -> 0x0f5b }
            r2.mime_type = r6     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r15.attributes     // Catch:{ Exception -> 0x0f5b }
            r2.attributes = r6     // Catch:{ Exception -> 0x0f5b }
            if (r5 == 0) goto L_0x0f8f
            r2.ttl_seconds = r5     // Catch:{ Exception -> 0x0var_ }
            r6 = r18
            r6.ttl = r5     // Catch:{ Exception -> 0x0var_ }
            int r5 = r2.flags     // Catch:{ Exception -> 0x0var_ }
            r8 = 2
            r5 = r5 | r8
            r2.flags = r5     // Catch:{ Exception -> 0x0var_ }
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            goto L_0x0f8b
        L_0x0var_:
            r0 = move-exception
            r6 = r18
        L_0x0f8b:
            r2 = r0
            r5 = r6
            goto L_0x0ac7
        L_0x0f8f:
            r6 = r18
        L_0x0var_:
            long r8 = r15.access_hash     // Catch:{ Exception -> 0x0fdf }
            int r5 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r5 != 0) goto L_0x0f9c
            r5 = r2
            r18 = r6
            r6 = 1
            goto L_0x0fc2
        L_0x0f9c:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0fdf }
            r5.<init>()     // Catch:{ Exception -> 0x0fdf }
            org.telegram.tgnet.TLRPC$TL_inputDocument r8 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0fdf }
            r8.<init>()     // Catch:{ Exception -> 0x0fdf }
            r5.id = r8     // Catch:{ Exception -> 0x0fdf }
            r52 = r5
            r18 = r6
            long r5 = r15.id     // Catch:{ Exception -> 0x0f5b }
            r8.id = r5     // Catch:{ Exception -> 0x0f5b }
            long r5 = r15.access_hash     // Catch:{ Exception -> 0x0f5b }
            r8.access_hash = r5     // Catch:{ Exception -> 0x0f5b }
            byte[] r5 = r15.file_reference     // Catch:{ Exception -> 0x0f5b }
            r8.file_reference = r5     // Catch:{ Exception -> 0x0f5b }
            if (r5 != 0) goto L_0x0fbf
            r5 = 0
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x0f5b }
            r8.file_reference = r6     // Catch:{ Exception -> 0x0f5b }
        L_0x0fbf:
            r5 = r52
            r6 = 0
        L_0x0fc2:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r8 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0f5b }
            r8.<init>(r10)     // Catch:{ Exception -> 0x0f5b }
            r9 = 3
            r8.type = r9     // Catch:{ Exception -> 0x0f5b }
            r8.obj = r12     // Catch:{ Exception -> 0x0f5b }
            r8.parentObject = r1     // Catch:{ Exception -> 0x0f5b }
            r8.inputUploadMedia = r2     // Catch:{ Exception -> 0x0f5b }
            r8.performMediaUpload = r6     // Catch:{ Exception -> 0x0f5b }
            if (r13 == 0) goto L_0x0fd6
            r2 = 1
            goto L_0x0fd7
        L_0x0fd6:
            r2 = 0
        L_0x0fd7:
            r8.scheduled = r2     // Catch:{ Exception -> 0x0f5b }
            r66 = r3
            r9 = r6
            r2 = r8
            goto L_0x112f
        L_0x0fdf:
            r0 = move-exception
            r18 = r6
            goto L_0x0var_
        L_0x0fe4:
            r5 = 10
            if (r3 != r5) goto L_0x1047
            org.telegram.tgnet.TLRPC$TL_inputMediaPoll r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll     // Catch:{ Exception -> 0x0f5b }
            r5.<init>()     // Catch:{ Exception -> 0x0f5b }
            r8 = r35
            org.telegram.tgnet.TLRPC$Poll r6 = r8.poll     // Catch:{ Exception -> 0x0f5b }
            r5.poll = r6     // Catch:{ Exception -> 0x0f5b }
            if (r14 == 0) goto L_0x1026
            java.lang.String r6 = "answers"
            boolean r6 = r14.containsKey(r6)     // Catch:{ Exception -> 0x0f5b }
            if (r6 == 0) goto L_0x1026
            java.lang.String r6 = "answers"
            java.lang.Object r6 = r14.get(r6)     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x0f5b }
            byte[] r6 = org.telegram.messenger.Utilities.hexToBytes(r6)     // Catch:{ Exception -> 0x0f5b }
            int r9 = r6.length     // Catch:{ Exception -> 0x0f5b }
            if (r9 <= 0) goto L_0x1026
            r9 = 0
        L_0x100d:
            int r10 = r6.length     // Catch:{ Exception -> 0x0f5b }
            if (r9 >= r10) goto L_0x1020
            java.util.ArrayList<byte[]> r10 = r5.correct_answers     // Catch:{ Exception -> 0x0f5b }
            r11 = 1
            byte[] r14 = new byte[r11]     // Catch:{ Exception -> 0x0f5b }
            byte r11 = r6[r9]     // Catch:{ Exception -> 0x0f5b }
            r15 = 0
            r14[r15] = r11     // Catch:{ Exception -> 0x0f5b }
            r10.add(r14)     // Catch:{ Exception -> 0x0f5b }
            int r9 = r9 + 1
            goto L_0x100d
        L_0x1020:
            int r6 = r5.flags     // Catch:{ Exception -> 0x0f5b }
            r9 = 1
            r6 = r6 | r9
            r5.flags = r6     // Catch:{ Exception -> 0x0f5b }
        L_0x1026:
            org.telegram.tgnet.TLRPC$PollResults r6 = r8.results     // Catch:{ Exception -> 0x0f5b }
            if (r6 == 0) goto L_0x1054
            org.telegram.tgnet.TLRPC$PollResults r6 = r8.results     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r6 = r6.solution     // Catch:{ Exception -> 0x0f5b }
            boolean r6 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x0f5b }
            if (r6 != 0) goto L_0x1054
            org.telegram.tgnet.TLRPC$PollResults r6 = r8.results     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r6 = r6.solution     // Catch:{ Exception -> 0x0f5b }
            r5.solution = r6     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$PollResults r6 = r8.results     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r6.solution_entities     // Catch:{ Exception -> 0x0f5b }
            r5.solution_entities = r6     // Catch:{ Exception -> 0x0f5b }
            int r6 = r5.flags     // Catch:{ Exception -> 0x0f5b }
            r8 = 2
            r6 = r6 | r8
            r5.flags = r6     // Catch:{ Exception -> 0x0f5b }
            goto L_0x1054
        L_0x1047:
            r5 = 11
            if (r3 != r5) goto L_0x1057
            org.telegram.tgnet.TLRPC$TL_inputMediaDice r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDice     // Catch:{ Exception -> 0x0f5b }
            r5.<init>()     // Catch:{ Exception -> 0x0f5b }
            r6 = r55
            r5.emoticon = r6     // Catch:{ Exception -> 0x0f5b }
        L_0x1054:
            r66 = r3
            goto L_0x105a
        L_0x1057:
            r66 = r3
            r5 = r8
        L_0x105a:
            r3 = r18
            r9 = 0
            goto L_0x1209
        L_0x105f:
            r6 = r63
            if (r4 != 0) goto L_0x107a
            r9 = r18
            if (r6 != 0) goto L_0x1078
            r18 = r9
            long r8 = r15.access_hash     // Catch:{ Exception -> 0x0f5b }
            int r19 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r19 != 0) goto L_0x1070
            goto L_0x107a
        L_0x1070:
            r64 = r2
            r66 = r3
            r9 = r18
            r5 = 0
            goto L_0x10ca
        L_0x1078:
            r18 = r9
        L_0x107a:
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0f5b }
            r8.<init>()     // Catch:{ Exception -> 0x0f5b }
            if (r5 == 0) goto L_0x1097
            r8.ttl_seconds = r5     // Catch:{ Exception -> 0x1090 }
            r9 = r18
            r9.ttl = r5     // Catch:{ Exception -> 0x10b8 }
            int r5 = r8.flags     // Catch:{ Exception -> 0x10b8 }
            r18 = 2
            r5 = r5 | 2
            r8.flags = r5     // Catch:{ Exception -> 0x10b8 }
            goto L_0x1099
        L_0x1090:
            r0 = move-exception
            r9 = r18
        L_0x1093:
            r2 = r0
            r5 = r9
            goto L_0x0ac7
        L_0x1097:
            r9 = r18
        L_0x1099:
            if (r28 != 0) goto L_0x10ba
            boolean r5 = android.text.TextUtils.isEmpty(r63)     // Catch:{ Exception -> 0x10b8 }
            if (r5 != 0) goto L_0x10bd
            java.lang.String r5 = r63.toLowerCase()     // Catch:{ Exception -> 0x10b8 }
            java.lang.String r6 = "mp4"
            boolean r5 = r5.endsWith(r6)     // Catch:{ Exception -> 0x10b8 }
            if (r5 == 0) goto L_0x10bd
            if (r14 == 0) goto L_0x10ba
            java.lang.String r5 = "forceDocument"
            boolean r5 = r14.containsKey(r5)     // Catch:{ Exception -> 0x10b8 }
            if (r5 == 0) goto L_0x10bd
            goto L_0x10ba
        L_0x10b8:
            r0 = move-exception
            goto L_0x1093
        L_0x10ba:
            r5 = 1
            r8.nosound_video = r5     // Catch:{ Exception -> 0x1133 }
        L_0x10bd:
            java.lang.String r5 = r15.mime_type     // Catch:{ Exception -> 0x1133 }
            r8.mime_type = r5     // Catch:{ Exception -> 0x1133 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r15.attributes     // Catch:{ Exception -> 0x1133 }
            r8.attributes = r5     // Catch:{ Exception -> 0x1133 }
            r64 = r2
            r66 = r3
            r5 = r8
        L_0x10ca:
            long r2 = r15.access_hash     // Catch:{ Exception -> 0x1133 }
            int r6 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r6 != 0) goto L_0x10d7
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x10b8 }
            r3 = r2
            r2 = r5
            r18 = r9
            goto L_0x10f9
        L_0x10d7:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x1133 }
            r2.<init>()     // Catch:{ Exception -> 0x1133 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x1133 }
            r3.<init>()     // Catch:{ Exception -> 0x1133 }
            r2.id = r3     // Catch:{ Exception -> 0x1133 }
            r18 = r9
            long r8 = r15.id     // Catch:{ Exception -> 0x0f5b }
            r3.id = r8     // Catch:{ Exception -> 0x0f5b }
            long r8 = r15.access_hash     // Catch:{ Exception -> 0x0f5b }
            r3.access_hash = r8     // Catch:{ Exception -> 0x0f5b }
            byte[] r6 = r15.file_reference     // Catch:{ Exception -> 0x0f5b }
            r3.file_reference = r6     // Catch:{ Exception -> 0x0f5b }
            if (r6 != 0) goto L_0x10f8
            r6 = 0
            byte[] r8 = new byte[r6]     // Catch:{ Exception -> 0x0f5b }
            r3.file_reference = r8     // Catch:{ Exception -> 0x0f5b }
        L_0x10f8:
            r3 = 0
        L_0x10f9:
            if (r5 == 0) goto L_0x112a
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0f5b }
            r6.<init>(r10)     // Catch:{ Exception -> 0x0f5b }
            r6.originalPath = r4     // Catch:{ Exception -> 0x0f5b }
            r8 = 2
            r6.type = r8     // Catch:{ Exception -> 0x0f5b }
            r6.obj = r12     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r15.thumbs     // Catch:{ Exception -> 0x0f5b }
            boolean r8 = r8.isEmpty()     // Catch:{ Exception -> 0x0f5b }
            if (r8 != 0) goto L_0x111c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r15.thumbs     // Catch:{ Exception -> 0x0f5b }
            r9 = 0
            java.lang.Object r8 = r8.get(r9)     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$PhotoSize r8 = (org.telegram.tgnet.TLRPC$PhotoSize) r8     // Catch:{ Exception -> 0x0f5b }
            r6.photoSize = r8     // Catch:{ Exception -> 0x0f5b }
            r6.locationParent = r15     // Catch:{ Exception -> 0x0f5b }
        L_0x111c:
            r6.parentObject = r1     // Catch:{ Exception -> 0x0f5b }
            r6.inputUploadMedia = r5     // Catch:{ Exception -> 0x0f5b }
            r6.performMediaUpload = r3     // Catch:{ Exception -> 0x0f5b }
            if (r13 == 0) goto L_0x1126
            r5 = 1
            goto L_0x1127
        L_0x1126:
            r5 = 0
        L_0x1127:
            r6.scheduled = r5     // Catch:{ Exception -> 0x0f5b }
            goto L_0x112c
        L_0x112a:
            r6 = r64
        L_0x112c:
            r5 = r2
            r9 = r3
            r2 = r6
        L_0x112f:
            r3 = r18
            goto L_0x1209
        L_0x1133:
            r0 = move-exception
            r18 = r9
            goto L_0x0var_
        L_0x1138:
            r6 = r63
            r18 = r1
            r64 = r2
            r66 = r3
            r27 = r5
            r4 = r42
            r5 = r54
            r1 = r60
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x13df }
            r2.<init>()     // Catch:{ Exception -> 0x13df }
            if (r5 == 0) goto L_0x115c
            r2.ttl_seconds = r5     // Catch:{ Exception -> 0x13df }
            r3 = r18
            r3.ttl = r5     // Catch:{ Exception -> 0x13dd }
            int r5 = r2.flags     // Catch:{ Exception -> 0x13dd }
            r8 = 2
            r5 = r5 | r8
            r2.flags = r5     // Catch:{ Exception -> 0x13dd }
            goto L_0x115e
        L_0x115c:
            r3 = r18
        L_0x115e:
            if (r14 == 0) goto L_0x1199
            java.lang.String r5 = "masks"
            java.lang.Object r5 = r14.get(r5)     // Catch:{ Exception -> 0x13dd }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x13dd }
            if (r5 == 0) goto L_0x1199
            org.telegram.tgnet.SerializedData r8 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x13dd }
            byte[] r5 = org.telegram.messenger.Utilities.hexToBytes(r5)     // Catch:{ Exception -> 0x13dd }
            r8.<init>((byte[]) r5)     // Catch:{ Exception -> 0x13dd }
            r5 = 0
            int r9 = r8.readInt32(r5)     // Catch:{ Exception -> 0x13dd }
            r14 = 0
        L_0x1179:
            if (r14 >= r9) goto L_0x1190
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r15 = r2.stickers     // Catch:{ Exception -> 0x13dd }
            r52 = r9
            int r9 = r8.readInt32(r5)     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$InputDocument r9 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r8, r9, r5)     // Catch:{ Exception -> 0x13dd }
            r15.add(r9)     // Catch:{ Exception -> 0x13dd }
            int r14 = r14 + 1
            r9 = r52
            r5 = 0
            goto L_0x1179
        L_0x1190:
            int r5 = r2.flags     // Catch:{ Exception -> 0x13dd }
            r9 = 1
            r5 = r5 | r9
            r2.flags = r5     // Catch:{ Exception -> 0x13dd }
            r8.cleanup()     // Catch:{ Exception -> 0x13dd }
        L_0x1199:
            r5 = r53
            long r8 = r5.access_hash     // Catch:{ Exception -> 0x13dd }
            int r14 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x11a4
            r8 = r2
            r9 = 1
            goto L_0x11c4
        L_0x11a4:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x13dd }
            r8.<init>()     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r9 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x13dd }
            r9.<init>()     // Catch:{ Exception -> 0x13dd }
            r8.id = r9     // Catch:{ Exception -> 0x13dd }
            long r14 = r5.id     // Catch:{ Exception -> 0x13dd }
            r9.id = r14     // Catch:{ Exception -> 0x13dd }
            long r14 = r5.access_hash     // Catch:{ Exception -> 0x13dd }
            r9.access_hash = r14     // Catch:{ Exception -> 0x13dd }
            byte[] r14 = r5.file_reference     // Catch:{ Exception -> 0x13dd }
            r9.file_reference = r14     // Catch:{ Exception -> 0x13dd }
            if (r14 != 0) goto L_0x11c3
            r14 = 0
            byte[] r15 = new byte[r14]     // Catch:{ Exception -> 0x13dd }
            r9.file_reference = r15     // Catch:{ Exception -> 0x13dd }
        L_0x11c3:
            r9 = 0
        L_0x11c4:
            if (r64 != 0) goto L_0x11da
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r14 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x13dd }
            r14.<init>(r10)     // Catch:{ Exception -> 0x13dd }
            r10 = 0
            r14.type = r10     // Catch:{ Exception -> 0x13dd }
            r14.obj = r12     // Catch:{ Exception -> 0x13dd }
            r14.originalPath = r4     // Catch:{ Exception -> 0x13dd }
            if (r13 == 0) goto L_0x11d6
            r10 = 1
            goto L_0x11d7
        L_0x11d6:
            r10 = 0
        L_0x11d7:
            r14.scheduled = r10     // Catch:{ Exception -> 0x13dd }
            goto L_0x11dc
        L_0x11da:
            r14 = r64
        L_0x11dc:
            r14.inputUploadMedia = r2     // Catch:{ Exception -> 0x13dd }
            r14.performMediaUpload = r9     // Catch:{ Exception -> 0x13dd }
            if (r6 == 0) goto L_0x11f3
            int r2 = r63.length()     // Catch:{ Exception -> 0x13dd }
            if (r2 <= 0) goto L_0x11f3
            r2 = r41
            boolean r2 = r6.startsWith(r2)     // Catch:{ Exception -> 0x13dd }
            if (r2 == 0) goto L_0x11f3
            r14.httpLocation = r6     // Catch:{ Exception -> 0x13dd }
            goto L_0x1207
        L_0x11f3:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.sizes     // Catch:{ Exception -> 0x13dd }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r5.sizes     // Catch:{ Exception -> 0x13dd }
            int r6 = r6.size()     // Catch:{ Exception -> 0x13dd }
            r10 = 1
            int r6 = r6 - r10
            java.lang.Object r2 = r2.get(r6)     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2     // Catch:{ Exception -> 0x13dd }
            r14.photoSize = r2     // Catch:{ Exception -> 0x13dd }
            r14.locationParent = r5     // Catch:{ Exception -> 0x13dd }
        L_0x1207:
            r5 = r8
            r2 = r14
        L_0x1209:
            int r6 = (r70 > r16 ? 1 : (r70 == r16 ? 0 : -1))
            if (r6 == 0) goto L_0x129c
            org.telegram.tgnet.TLObject r6 = r2.sendRequest     // Catch:{ Exception -> 0x13dd }
            if (r6 == 0) goto L_0x1216
            org.telegram.tgnet.TLObject r6 = r2.sendRequest     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r6 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r6     // Catch:{ Exception -> 0x13dd }
            goto L_0x1245
        L_0x1216:
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r6 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia     // Catch:{ Exception -> 0x13dd }
            r6.<init>()     // Catch:{ Exception -> 0x13dd }
            r8 = r27
            r6.peer = r8     // Catch:{ Exception -> 0x13dd }
            boolean r8 = r3.silent     // Catch:{ Exception -> 0x13dd }
            r6.silent = r8     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r8 = r3.reply_to     // Catch:{ Exception -> 0x13dd }
            if (r8 == 0) goto L_0x1239
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r8 = r3.reply_to     // Catch:{ Exception -> 0x13dd }
            int r8 = r8.reply_to_msg_id     // Catch:{ Exception -> 0x13dd }
            if (r8 == 0) goto L_0x1239
            int r8 = r6.flags     // Catch:{ Exception -> 0x13dd }
            r10 = 1
            r8 = r8 | r10
            r6.flags = r8     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r8 = r3.reply_to     // Catch:{ Exception -> 0x13dd }
            int r8 = r8.reply_to_msg_id     // Catch:{ Exception -> 0x13dd }
            r6.reply_to_msg_id = r8     // Catch:{ Exception -> 0x13dd }
        L_0x1239:
            if (r13 == 0) goto L_0x1243
            r6.schedule_date = r13     // Catch:{ Exception -> 0x13dd }
            int r8 = r6.flags     // Catch:{ Exception -> 0x13dd }
            r8 = r8 | 1024(0x400, float:1.435E-42)
            r6.flags = r8     // Catch:{ Exception -> 0x13dd }
        L_0x1243:
            r2.sendRequest = r6     // Catch:{ Exception -> 0x13dd }
        L_0x1245:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r2.messageObjects     // Catch:{ Exception -> 0x13dd }
            r8.add(r12)     // Catch:{ Exception -> 0x13dd }
            java.util.ArrayList<java.lang.Object> r8 = r2.parentObjects     // Catch:{ Exception -> 0x13dd }
            r8.add(r1)     // Catch:{ Exception -> 0x13dd }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r2.locations     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$PhotoSize r10 = r2.photoSize     // Catch:{ Exception -> 0x13dd }
            r8.add(r10)     // Catch:{ Exception -> 0x13dd }
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo> r8 = r2.videoEditedInfos     // Catch:{ Exception -> 0x13dd }
            org.telegram.messenger.VideoEditedInfo r10 = r2.videoEditedInfo     // Catch:{ Exception -> 0x13dd }
            r8.add(r10)     // Catch:{ Exception -> 0x13dd }
            java.util.ArrayList<java.lang.String> r8 = r2.httpLocations     // Catch:{ Exception -> 0x13dd }
            java.lang.String r10 = r2.httpLocation     // Catch:{ Exception -> 0x13dd }
            r8.add(r10)     // Catch:{ Exception -> 0x13dd }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputMedia> r8 = r2.inputMedias     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$InputMedia r10 = r2.inputUploadMedia     // Catch:{ Exception -> 0x13dd }
            r8.add(r10)     // Catch:{ Exception -> 0x13dd }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r8 = r2.messages     // Catch:{ Exception -> 0x13dd }
            r8.add(r3)     // Catch:{ Exception -> 0x13dd }
            java.util.ArrayList<java.lang.String> r8 = r2.originalPaths     // Catch:{ Exception -> 0x13dd }
            r8.add(r4)     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r8 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia     // Catch:{ Exception -> 0x13dd }
            r8.<init>()     // Catch:{ Exception -> 0x13dd }
            long r10 = r3.random_id     // Catch:{ Exception -> 0x13dd }
            r8.random_id = r10     // Catch:{ Exception -> 0x13dd }
            r8.media = r5     // Catch:{ Exception -> 0x13dd }
            r10 = r37
            r8.message = r10     // Catch:{ Exception -> 0x13dd }
            r11 = r69
            if (r11 == 0) goto L_0x1296
            boolean r5 = r69.isEmpty()     // Catch:{ Exception -> 0x13dd }
            if (r5 != 0) goto L_0x1296
            r8.entities = r11     // Catch:{ Exception -> 0x13dd }
            int r5 = r8.flags     // Catch:{ Exception -> 0x13dd }
            r10 = 1
            r5 = r5 | r10
            r8.flags = r5     // Catch:{ Exception -> 0x13dd }
        L_0x1296:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r5 = r6.multi_media     // Catch:{ Exception -> 0x13dd }
            r5.add(r8)     // Catch:{ Exception -> 0x13dd }
            goto L_0x12ea
        L_0x129c:
            r11 = r69
            r8 = r27
            r10 = r37
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r6 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia     // Catch:{ Exception -> 0x13dd }
            r6.<init>()     // Catch:{ Exception -> 0x13dd }
            r6.peer = r8     // Catch:{ Exception -> 0x13dd }
            boolean r8 = r3.silent     // Catch:{ Exception -> 0x13dd }
            r6.silent = r8     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r8 = r3.reply_to     // Catch:{ Exception -> 0x13dd }
            if (r8 == 0) goto L_0x12c3
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r8 = r3.reply_to     // Catch:{ Exception -> 0x13dd }
            int r8 = r8.reply_to_msg_id     // Catch:{ Exception -> 0x13dd }
            if (r8 == 0) goto L_0x12c3
            int r8 = r6.flags     // Catch:{ Exception -> 0x13dd }
            r14 = 1
            r8 = r8 | r14
            r6.flags = r8     // Catch:{ Exception -> 0x13dd }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r8 = r3.reply_to     // Catch:{ Exception -> 0x13dd }
            int r8 = r8.reply_to_msg_id     // Catch:{ Exception -> 0x13dd }
            r6.reply_to_msg_id = r8     // Catch:{ Exception -> 0x13dd }
        L_0x12c3:
            long r14 = r3.random_id     // Catch:{ Exception -> 0x13dd }
            r6.random_id = r14     // Catch:{ Exception -> 0x13dd }
            r6.media = r5     // Catch:{ Exception -> 0x13dd }
            r6.message = r10     // Catch:{ Exception -> 0x13dd }
            if (r11 == 0) goto L_0x12dc
            boolean r5 = r69.isEmpty()     // Catch:{ Exception -> 0x13dd }
            if (r5 != 0) goto L_0x12dc
            r6.entities = r11     // Catch:{ Exception -> 0x13dd }
            int r5 = r6.flags     // Catch:{ Exception -> 0x13dd }
            r8 = 8
            r5 = r5 | r8
            r6.flags = r5     // Catch:{ Exception -> 0x13dd }
        L_0x12dc:
            if (r13 == 0) goto L_0x12e6
            r6.schedule_date = r13     // Catch:{ Exception -> 0x13dd }
            int r5 = r6.flags     // Catch:{ Exception -> 0x13dd }
            r5 = r5 | 1024(0x400, float:1.435E-42)
            r6.flags = r5     // Catch:{ Exception -> 0x13dd }
        L_0x12e6:
            if (r2 == 0) goto L_0x12ea
            r2.sendRequest = r6     // Catch:{ Exception -> 0x13dd }
        L_0x12ea:
            int r5 = (r70 > r16 ? 1 : (r70 == r16 ? 0 : -1))
            if (r5 == 0) goto L_0x12f3
            r7.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x12f3:
            r5 = r66
            r8 = 1
            if (r5 != r8) goto L_0x1311
            r4 = 0
            if (r13 == 0) goto L_0x12fd
            r5 = 1
            goto L_0x12fe
        L_0x12fd:
            r5 = 0
        L_0x12fe:
            r52 = r51
            r53 = r6
            r54 = r12
            r55 = r4
            r56 = r2
            r57 = r1
            r58 = r5
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x1311:
            r8 = 2
            if (r5 != r8) goto L_0x1339
            if (r9 == 0) goto L_0x131b
            r7.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x131b:
            r5 = 0
            r8 = 1
            if (r13 == 0) goto L_0x1321
            r9 = 1
            goto L_0x1322
        L_0x1321:
            r9 = 0
        L_0x1322:
            r52 = r51
            r53 = r6
            r54 = r12
            r55 = r4
            r56 = r5
            r57 = r8
            r58 = r2
            r59 = r1
            r60 = r9
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58, r59, r60)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x1339:
            r8 = 3
            if (r5 != r8) goto L_0x135b
            if (r9 == 0) goto L_0x1343
            r7.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x1343:
            if (r13 == 0) goto L_0x1347
            r5 = 1
            goto L_0x1348
        L_0x1347:
            r5 = 0
        L_0x1348:
            r52 = r51
            r53 = r6
            r54 = r12
            r55 = r4
            r56 = r2
            r57 = r1
            r58 = r5
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x135b:
            r8 = 6
            if (r5 != r8) goto L_0x1376
            if (r13 == 0) goto L_0x1362
            r5 = 1
            goto L_0x1363
        L_0x1362:
            r5 = 0
        L_0x1363:
            r52 = r51
            r53 = r6
            r54 = r12
            r55 = r4
            r56 = r2
            r57 = r1
            r58 = r5
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x1376:
            r8 = 7
            if (r5 != r8) goto L_0x139a
            if (r9 == 0) goto L_0x1382
            if (r2 == 0) goto L_0x1382
            r7.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x1382:
            if (r13 == 0) goto L_0x1386
            r5 = 1
            goto L_0x1387
        L_0x1386:
            r5 = 0
        L_0x1387:
            r52 = r51
            r53 = r6
            r54 = r12
            r55 = r4
            r56 = r2
            r57 = r1
            r58 = r5
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x139a:
            r8 = 8
            if (r5 != r8) goto L_0x13bd
            if (r9 == 0) goto L_0x13a5
            r7.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x13a5:
            if (r13 == 0) goto L_0x13a9
            r5 = 1
            goto L_0x13aa
        L_0x13a9:
            r5 = 0
        L_0x13aa:
            r52 = r51
            r53 = r6
            r54 = r12
            r55 = r4
            r56 = r2
            r57 = r1
            r58 = r5
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x13bd:
            r8 = 10
            if (r5 == r8) goto L_0x13c5
            r8 = 11
            if (r5 != r8) goto L_0x1b15
        L_0x13c5:
            if (r13 == 0) goto L_0x13c9
            r5 = 1
            goto L_0x13ca
        L_0x13c9:
            r5 = 0
        L_0x13ca:
            r52 = r51
            r53 = r6
            r54 = r12
            r55 = r4
            r56 = r2
            r57 = r1
            r58 = r5
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58)     // Catch:{ Exception -> 0x13dd }
            goto L_0x1b15
        L_0x13dd:
            r0 = move-exception
            goto L_0x13e2
        L_0x13df:
            r0 = move-exception
            r3 = r18
        L_0x13e2:
            r2 = r0
            r5 = r3
            goto L_0x0ac7
        L_0x13e6:
            r5 = r53
            r18 = r60
            r15 = r69
            r64 = r2
            r13 = r3
            r6 = r19
            r8 = r34
            r3 = r37
            r2 = r1
            r1 = r20
            int r4 = r9.layer     // Catch:{ Exception -> 0x19d6 }
            int r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4)     // Catch:{ Exception -> 0x19d6 }
            r10 = 73
            if (r4 < r10) goto L_0x141e
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1417 }
            r4.<init>()     // Catch:{ Exception -> 0x1417 }
            int r10 = (r70 > r16 ? 1 : (r70 == r16 ? 0 : -1))
            if (r10 == 0) goto L_0x1423
            r10 = r70
            r4.grouped_id = r10     // Catch:{ Exception -> 0x1417 }
            int r10 = r4.flags     // Catch:{ Exception -> 0x1417 }
            r11 = 131072(0x20000, float:1.83671E-40)
            r10 = r10 | r11
            r4.flags = r10     // Catch:{ Exception -> 0x1417 }
            goto L_0x1423
        L_0x1417:
            r0 = move-exception
            r1 = r73
            r5 = r2
        L_0x141b:
            r2 = r0
            goto L_0x1b32
        L_0x141e:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x19d6 }
            r4.<init>()     // Catch:{ Exception -> 0x19d6 }
        L_0x1423:
            int r10 = r2.ttl     // Catch:{ Exception -> 0x19d6 }
            r4.ttl = r10     // Catch:{ Exception -> 0x19d6 }
            if (r15 == 0) goto L_0x1437
            boolean r10 = r69.isEmpty()     // Catch:{ Exception -> 0x1417 }
            if (r10 != 0) goto L_0x1437
            r4.entities = r15     // Catch:{ Exception -> 0x1417 }
            int r10 = r4.flags     // Catch:{ Exception -> 0x1417 }
            r10 = r10 | 128(0x80, float:1.794E-43)
            r4.flags = r10     // Catch:{ Exception -> 0x1417 }
        L_0x1437:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r10 = r2.reply_to     // Catch:{ Exception -> 0x19d6 }
            if (r10 == 0) goto L_0x1450
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r10 = r2.reply_to     // Catch:{ Exception -> 0x1417 }
            long r10 = r10.reply_to_random_id     // Catch:{ Exception -> 0x1417 }
            int r15 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r15 == 0) goto L_0x1450
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r10 = r2.reply_to     // Catch:{ Exception -> 0x1417 }
            long r10 = r10.reply_to_random_id     // Catch:{ Exception -> 0x1417 }
            r4.reply_to_random_id = r10     // Catch:{ Exception -> 0x1417 }
            int r10 = r4.flags     // Catch:{ Exception -> 0x1417 }
            r11 = 8
            r10 = r10 | r11
            r4.flags = r10     // Catch:{ Exception -> 0x1417 }
        L_0x1450:
            boolean r10 = r2.silent     // Catch:{ Exception -> 0x19d6 }
            r4.silent = r10     // Catch:{ Exception -> 0x19d6 }
            int r10 = r4.flags     // Catch:{ Exception -> 0x19d6 }
            r10 = r10 | 512(0x200, float:7.175E-43)
            r4.flags = r10     // Catch:{ Exception -> 0x19d6 }
            if (r14 == 0) goto L_0x1472
            r10 = r52
            java.lang.Object r11 = r14.get(r10)     // Catch:{ Exception -> 0x1417 }
            if (r11 == 0) goto L_0x1472
            java.lang.Object r10 = r14.get(r10)     // Catch:{ Exception -> 0x1417 }
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ Exception -> 0x1417 }
            r4.via_bot_name = r10     // Catch:{ Exception -> 0x1417 }
            int r10 = r4.flags     // Catch:{ Exception -> 0x1417 }
            r10 = r10 | 2048(0x800, float:2.87E-42)
            r4.flags = r10     // Catch:{ Exception -> 0x1417 }
        L_0x1472:
            long r10 = r2.random_id     // Catch:{ Exception -> 0x19d6 }
            r4.random_id = r10     // Catch:{ Exception -> 0x19d6 }
            r4.message = r1     // Catch:{ Exception -> 0x19d6 }
            r1 = 1
            if (r13 != r1) goto L_0x14d5
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x1417 }
            if (r1 == 0) goto L_0x1497
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue     // Catch:{ Exception -> 0x1417 }
            r1.<init>()     // Catch:{ Exception -> 0x1417 }
            r4.media = r1     // Catch:{ Exception -> 0x1417 }
            java.lang.String r3 = r6.address     // Catch:{ Exception -> 0x1417 }
            r1.address = r3     // Catch:{ Exception -> 0x1417 }
            java.lang.String r3 = r6.title     // Catch:{ Exception -> 0x1417 }
            r1.title = r3     // Catch:{ Exception -> 0x1417 }
            java.lang.String r3 = r6.provider     // Catch:{ Exception -> 0x1417 }
            r1.provider = r3     // Catch:{ Exception -> 0x1417 }
            java.lang.String r3 = r6.venue_id     // Catch:{ Exception -> 0x1417 }
            r1.venue_id = r3     // Catch:{ Exception -> 0x1417 }
            goto L_0x149e
        L_0x1497:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint     // Catch:{ Exception -> 0x1417 }
            r1.<init>()     // Catch:{ Exception -> 0x1417 }
            r4.media = r1     // Catch:{ Exception -> 0x1417 }
        L_0x149e:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$GeoPoint r3 = r6.geo     // Catch:{ Exception -> 0x1417 }
            double r10 = r3.lat     // Catch:{ Exception -> 0x1417 }
            r1.lat = r10     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$GeoPoint r3 = r6.geo     // Catch:{ Exception -> 0x1417 }
            double r5 = r3._long     // Catch:{ Exception -> 0x1417 }
            r1._long = r5     // Catch:{ Exception -> 0x1417 }
            org.telegram.messenger.SecretChatHelper r1 = r51.getSecretChatHelper()     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner     // Catch:{ Exception -> 0x1417 }
            r5 = 0
            r6 = 0
            r54 = r1
            r55 = r4
            r56 = r3
            r57 = r9
            r58 = r5
            r59 = r6
            r60 = r12
            r54.performSendEncryptedRequest(r55, r56, r57, r58, r59, r60)     // Catch:{ Exception -> 0x1417 }
            r10 = r61
            r18 = r2
            r66 = r13
            r6 = r42
            r2 = r64
            r13 = r73
            goto L_0x177a
        L_0x14d5:
            r1 = 2
            if (r13 == r1) goto L_0x1848
            r1 = 9
            if (r13 != r1) goto L_0x14f1
            if (r5 == 0) goto L_0x14f1
            r10 = r61
            r1 = r3
            r66 = r13
            r8 = r18
            r15 = r38
            r3 = r41
            r6 = r42
            r13 = r73
            r18 = r2
            goto L_0x185b
        L_0x14f1:
            r1 = 3
            if (r13 != r1) goto L_0x162f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r8.thumbs     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r7.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x1417 }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1)     // Catch:{ Exception -> 0x1417 }
            boolean r5 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r8)     // Catch:{ Exception -> 0x1417 }
            if (r5 != 0) goto L_0x1528
            boolean r5 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r8)     // Catch:{ Exception -> 0x1417 }
            if (r5 == 0) goto L_0x150a
            goto L_0x1528
        L_0x150a:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo     // Catch:{ Exception -> 0x1417 }
            r5.<init>()     // Catch:{ Exception -> 0x1417 }
            r4.media = r5     // Catch:{ Exception -> 0x1417 }
            if (r1 == 0) goto L_0x151e
            byte[] r6 = r1.bytes     // Catch:{ Exception -> 0x1417 }
            if (r6 == 0) goto L_0x151e
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r5     // Catch:{ Exception -> 0x1417 }
            byte[] r6 = r1.bytes     // Catch:{ Exception -> 0x1417 }
            r5.thumb = r6     // Catch:{ Exception -> 0x1417 }
            goto L_0x1549
        L_0x151e:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r4.media     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r5     // Catch:{ Exception -> 0x1417 }
            r6 = 0
            byte[] r10 = new byte[r6]     // Catch:{ Exception -> 0x1417 }
            r5.thumb = r10     // Catch:{ Exception -> 0x1417 }
            goto L_0x1549
        L_0x1528:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1417 }
            r5.<init>()     // Catch:{ Exception -> 0x1417 }
            r4.media = r5     // Catch:{ Exception -> 0x1417 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r8.attributes     // Catch:{ Exception -> 0x1417 }
            r5.attributes = r6     // Catch:{ Exception -> 0x1417 }
            if (r1 == 0) goto L_0x1540
            byte[] r6 = r1.bytes     // Catch:{ Exception -> 0x1417 }
            if (r6 == 0) goto L_0x1540
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1417 }
            byte[] r6 = r1.bytes     // Catch:{ Exception -> 0x1417 }
            r5.thumb = r6     // Catch:{ Exception -> 0x1417 }
            goto L_0x1549
        L_0x1540:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r4.media     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1417 }
            r6 = 0
            byte[] r10 = new byte[r6]     // Catch:{ Exception -> 0x1417 }
            r5.thumb = r10     // Catch:{ Exception -> 0x1417 }
        L_0x1549:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r4.media     // Catch:{ Exception -> 0x1417 }
            r5.caption = r3     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x1417 }
            java.lang.String r5 = "video/mp4"
            r3.mime_type = r5     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x1417 }
            int r5 = r8.size     // Catch:{ Exception -> 0x1417 }
            r3.size = r5     // Catch:{ Exception -> 0x1417 }
            r3 = 0
        L_0x155a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r8.attributes     // Catch:{ Exception -> 0x1417 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x1417 }
            if (r3 >= r5) goto L_0x1584
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r8.attributes     // Catch:{ Exception -> 0x1417 }
            java.lang.Object r5 = r5.get(r3)     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r5     // Catch:{ Exception -> 0x1417 }
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x1417 }
            if (r6 == 0) goto L_0x1581
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x1417 }
            int r6 = r5.w     // Catch:{ Exception -> 0x1417 }
            r3.w = r6     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x1417 }
            int r6 = r5.h     // Catch:{ Exception -> 0x1417 }
            r3.h = r6     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x1417 }
            int r5 = r5.duration     // Catch:{ Exception -> 0x1417 }
            r3.duration = r5     // Catch:{ Exception -> 0x1417 }
            goto L_0x1584
        L_0x1581:
            int r3 = r3 + 1
            goto L_0x155a
        L_0x1584:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x1417 }
            int r5 = r1.h     // Catch:{ Exception -> 0x1417 }
            r3.thumb_h = r5     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x1417 }
            int r1 = r1.w     // Catch:{ Exception -> 0x1417 }
            r3.thumb_w = r1     // Catch:{ Exception -> 0x1417 }
            byte[] r1 = r8.key     // Catch:{ Exception -> 0x1417 }
            if (r1 == 0) goto L_0x15d4
            int r1 = (r70 > r16 ? 1 : (r70 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1599
            goto L_0x15d4
        L_0x1599:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1417 }
            r1.<init>()     // Catch:{ Exception -> 0x1417 }
            long r5 = r8.id     // Catch:{ Exception -> 0x1417 }
            r1.id = r5     // Catch:{ Exception -> 0x1417 }
            long r5 = r8.access_hash     // Catch:{ Exception -> 0x1417 }
            r1.access_hash = r5     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x1417 }
            byte[] r5 = r8.key     // Catch:{ Exception -> 0x1417 }
            r3.key = r5     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x1417 }
            byte[] r5 = r8.iv     // Catch:{ Exception -> 0x1417 }
            r3.iv = r5     // Catch:{ Exception -> 0x1417 }
            org.telegram.messenger.SecretChatHelper r3 = r51.getSecretChatHelper()     // Catch:{ Exception -> 0x1417 }
            org.telegram.tgnet.TLRPC$Message r5 = r12.messageOwner     // Catch:{ Exception -> 0x1417 }
            r6 = 0
            r54 = r3
            r55 = r4
            r56 = r5
            r57 = r9
            r58 = r1
            r59 = r6
            r60 = r12
            r54.performSendEncryptedRequest(r55, r56, r57, r58, r59, r60)     // Catch:{ Exception -> 0x1417 }
            r10 = r61
            r1 = r64
            r3 = r13
            r6 = r42
            r13 = r73
            goto L_0x1623
        L_0x15d4:
            if (r64 != 0) goto L_0x160f
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1417 }
            r10 = r61
            r1.<init>(r10)     // Catch:{ Exception -> 0x1417 }
            r1.encryptedChat = r9     // Catch:{ Exception -> 0x1417 }
            r3 = 1
            r1.type = r3     // Catch:{ Exception -> 0x1417 }
            r1.sendEncryptedRequest = r4     // Catch:{ Exception -> 0x1417 }
            r6 = r42
            r1.originalPath = r6     // Catch:{ Exception -> 0x1417 }
            r1.obj = r12     // Catch:{ Exception -> 0x1417 }
            if (r14 == 0) goto L_0x15fb
            r15 = r38
            boolean r3 = r14.containsKey(r15)     // Catch:{ Exception -> 0x1417 }
            if (r3 == 0) goto L_0x15fb
            java.lang.Object r3 = r14.get(r15)     // Catch:{ Exception -> 0x1417 }
            r1.parentObject = r3     // Catch:{ Exception -> 0x1417 }
            goto L_0x15ff
        L_0x15fb:
            r5 = r18
            r1.parentObject = r5     // Catch:{ Exception -> 0x1417 }
        L_0x15ff:
            r3 = 1
            r1.performMediaUpload = r3     // Catch:{ Exception -> 0x1417 }
            r3 = r13
            r13 = r73
            if (r13 == 0) goto L_0x1609
            r5 = 1
            goto L_0x160a
        L_0x1609:
            r5 = 0
        L_0x160a:
            r1.scheduled = r5     // Catch:{ Exception -> 0x162a }
            r5 = r56
            goto L_0x161a
        L_0x160f:
            r10 = r61
            r3 = r13
            r6 = r42
            r13 = r73
            r5 = r56
            r1 = r64
        L_0x161a:
            r1.videoEditedInfo = r5     // Catch:{ Exception -> 0x162a }
            int r5 = (r70 > r16 ? 1 : (r70 == r16 ? 0 : -1))
            if (r5 != 0) goto L_0x1623
            r7.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x162a }
        L_0x1623:
            r18 = r2
            r66 = r3
        L_0x1627:
            r2 = r1
            goto L_0x177a
        L_0x162a:
            r0 = move-exception
            r5 = r2
            r1 = r13
            goto L_0x141b
        L_0x162f:
            r10 = r61
            r1 = r13
            r5 = r18
            r15 = r38
            r6 = r42
            r13 = r73
            r18 = r2
            r2 = 6
            if (r1 != r2) goto L_0x1673
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact     // Catch:{ Exception -> 0x0f5b }
            r2.<init>()     // Catch:{ Exception -> 0x0f5b }
            r4.media = r2     // Catch:{ Exception -> 0x0f5b }
            r8 = r40
            java.lang.String r3 = r8.phone     // Catch:{ Exception -> 0x0f5b }
            r2.phone_number = r3     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r3 = r8.first_name     // Catch:{ Exception -> 0x0f5b }
            r2.first_name = r3     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r3 = r8.last_name     // Catch:{ Exception -> 0x0f5b }
            r2.last_name = r3     // Catch:{ Exception -> 0x0f5b }
            int r3 = r8.id     // Catch:{ Exception -> 0x0f5b }
            r2.user_id = r3     // Catch:{ Exception -> 0x0f5b }
            org.telegram.messenger.SecretChatHelper r2 = r51.getSecretChatHelper()     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner     // Catch:{ Exception -> 0x0f5b }
            r5 = 0
            r8 = 0
            r54 = r2
            r55 = r4
            r56 = r3
            r57 = r9
            r58 = r5
            r59 = r8
            r60 = r12
            r54.performSendEncryptedRequest(r55, r56, r57, r58, r59, r60)     // Catch:{ Exception -> 0x0f5b }
            goto L_0x16ef
        L_0x1673:
            r2 = 7
            if (r1 == r2) goto L_0x16f2
            r2 = 9
            if (r1 != r2) goto L_0x167e
            if (r8 == 0) goto L_0x167e
            goto L_0x16f2
        L_0x167e:
            r2 = 8
            if (r1 != r2) goto L_0x16ef
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0f5b }
            r2.<init>(r10)     // Catch:{ Exception -> 0x0f5b }
            r2.encryptedChat = r9     // Catch:{ Exception -> 0x0f5b }
            r2.sendEncryptedRequest = r4     // Catch:{ Exception -> 0x0f5b }
            r2.obj = r12     // Catch:{ Exception -> 0x0f5b }
            r9 = 3
            r2.type = r9     // Catch:{ Exception -> 0x0f5b }
            r2.parentObject = r5     // Catch:{ Exception -> 0x0f5b }
            r5 = 1
            r2.performMediaUpload = r5     // Catch:{ Exception -> 0x0f5b }
            if (r13 == 0) goto L_0x1699
            r5 = 1
            goto L_0x169a
        L_0x1699:
            r5 = 0
        L_0x169a:
            r2.scheduled = r5     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x0f5b }
            r5.<init>()     // Catch:{ Exception -> 0x0f5b }
            r4.media = r5     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r9 = r8.attributes     // Catch:{ Exception -> 0x0f5b }
            r5.attributes = r9     // Catch:{ Exception -> 0x0f5b }
            r5.caption = r3     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r8.thumbs     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r7.getThumbForSecretChat(r3)     // Catch:{ Exception -> 0x0f5b }
            if (r3 == 0) goto L_0x16c9
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r4.media     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x0f5b }
            byte[] r9 = r3.bytes     // Catch:{ Exception -> 0x0f5b }
            r5.thumb = r9     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r4.media     // Catch:{ Exception -> 0x0f5b }
            int r9 = r3.h     // Catch:{ Exception -> 0x0f5b }
            r5.thumb_h = r9     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r4.media     // Catch:{ Exception -> 0x0f5b }
            int r3 = r3.w     // Catch:{ Exception -> 0x0f5b }
            r5.thumb_w = r3     // Catch:{ Exception -> 0x0f5b }
            goto L_0x16da
        L_0x16c9:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r3 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r3     // Catch:{ Exception -> 0x0f5b }
            r5 = 0
            byte[] r9 = new byte[r5]     // Catch:{ Exception -> 0x0f5b }
            r3.thumb = r9     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x0f5b }
            r3.thumb_h = r5     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x0f5b }
            r3.thumb_w = r5     // Catch:{ Exception -> 0x0f5b }
        L_0x16da:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r5 = r8.mime_type     // Catch:{ Exception -> 0x0f5b }
            r3.mime_type = r5     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r4.media     // Catch:{ Exception -> 0x0f5b }
            int r5 = r8.size     // Catch:{ Exception -> 0x0f5b }
            r3.size = r5     // Catch:{ Exception -> 0x0f5b }
            r2.originalPath = r6     // Catch:{ Exception -> 0x0f5b }
            r7.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x0f5b }
        L_0x16eb:
            r66 = r1
            goto L_0x177a
        L_0x16ef:
            r2 = r64
            goto L_0x16eb
        L_0x16f2:
            r66 = r1
            long r1 = r8.access_hash     // Catch:{ Exception -> 0x0f5b }
            int r19 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r19 == 0) goto L_0x177d
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r8)     // Catch:{ Exception -> 0x0f5b }
            if (r1 != 0) goto L_0x1707
            r1 = 1
            boolean r2 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r8, r1)     // Catch:{ Exception -> 0x0f5b }
            if (r2 == 0) goto L_0x177d
        L_0x1707:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument     // Catch:{ Exception -> 0x0f5b }
            r1.<init>()     // Catch:{ Exception -> 0x0f5b }
            r4.media = r1     // Catch:{ Exception -> 0x0f5b }
            long r2 = r8.id     // Catch:{ Exception -> 0x0f5b }
            r1.id = r2     // Catch:{ Exception -> 0x0f5b }
            int r2 = r8.date     // Catch:{ Exception -> 0x0f5b }
            r1.date = r2     // Catch:{ Exception -> 0x0f5b }
            long r2 = r8.access_hash     // Catch:{ Exception -> 0x0f5b }
            r1.access_hash = r2     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r2 = r8.mime_type     // Catch:{ Exception -> 0x0f5b }
            r1.mime_type = r2     // Catch:{ Exception -> 0x0f5b }
            int r2 = r8.size     // Catch:{ Exception -> 0x0f5b }
            r1.size = r2     // Catch:{ Exception -> 0x0f5b }
            int r2 = r8.dc_id     // Catch:{ Exception -> 0x0f5b }
            r1.dc_id = r2     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r8.attributes     // Catch:{ Exception -> 0x0f5b }
            r1.attributes = r2     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r8.thumbs     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r7.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x0f5b }
            if (r1 == 0) goto L_0x1739
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r2 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r2     // Catch:{ Exception -> 0x0f5b }
            r2.thumb = r1     // Catch:{ Exception -> 0x0f5b }
            goto L_0x174e
        L_0x1739:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r2 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty     // Catch:{ Exception -> 0x0f5b }
            r2.<init>()     // Catch:{ Exception -> 0x0f5b }
            r1.thumb = r2     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r1.thumb     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r2 = "s"
            r1.type = r2     // Catch:{ Exception -> 0x0f5b }
        L_0x174e:
            if (r64 == 0) goto L_0x175f
            r2 = r64
            int r1 = r2.type     // Catch:{ Exception -> 0x0f5b }
            r3 = 5
            if (r1 != r3) goto L_0x1761
            r2.sendEncryptedRequest = r4     // Catch:{ Exception -> 0x0f5b }
            r2.obj = r12     // Catch:{ Exception -> 0x0f5b }
            r7.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x0f5b }
            goto L_0x177a
        L_0x175f:
            r2 = r64
        L_0x1761:
            org.telegram.messenger.SecretChatHelper r1 = r51.getSecretChatHelper()     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner     // Catch:{ Exception -> 0x0f5b }
            r5 = 0
            r8 = 0
            r54 = r1
            r55 = r4
            r56 = r3
            r57 = r9
            r58 = r5
            r59 = r8
            r60 = r12
            r54.performSendEncryptedRequest(r55, r56, r57, r58, r59, r60)     // Catch:{ Exception -> 0x0f5b }
        L_0x177a:
            r1 = r13
            goto L_0x196a
        L_0x177d:
            r2 = r64
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x0f5b }
            r1.<init>()     // Catch:{ Exception -> 0x0f5b }
            r4.media = r1     // Catch:{ Exception -> 0x0f5b }
            r64 = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r8.attributes     // Catch:{ Exception -> 0x0f5b }
            r1.attributes = r2     // Catch:{ Exception -> 0x0f5b }
            r1.caption = r3     // Catch:{ Exception -> 0x0f5b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r8.thumbs     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r7.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x0f5b }
            if (r1 == 0) goto L_0x17ae
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1)     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r2 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r2     // Catch:{ Exception -> 0x0f5b }
            byte[] r3 = r1.bytes     // Catch:{ Exception -> 0x0f5b }
            r2.thumb = r3     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media     // Catch:{ Exception -> 0x0f5b }
            int r3 = r1.h     // Catch:{ Exception -> 0x0f5b }
            r2.thumb_h = r3     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media     // Catch:{ Exception -> 0x0f5b }
            int r1 = r1.w     // Catch:{ Exception -> 0x0f5b }
            r2.thumb_w = r1     // Catch:{ Exception -> 0x0f5b }
            goto L_0x17bf
        L_0x17ae:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r1     // Catch:{ Exception -> 0x0f5b }
            r2 = 0
            byte[] r3 = new byte[r2]     // Catch:{ Exception -> 0x0f5b }
            r1.thumb = r3     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x0f5b }
            r1.thumb_h = r2     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x0f5b }
            r1.thumb_w = r2     // Catch:{ Exception -> 0x0f5b }
        L_0x17bf:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x0f5b }
            int r2 = r8.size     // Catch:{ Exception -> 0x0f5b }
            r1.size = r2     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x0f5b }
            java.lang.String r2 = r8.mime_type     // Catch:{ Exception -> 0x0f5b }
            r1.mime_type = r2     // Catch:{ Exception -> 0x0f5b }
            byte[] r1 = r8.key     // Catch:{ Exception -> 0x0f5b }
            if (r1 != 0) goto L_0x1813
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0f5b }
            r1.<init>(r10)     // Catch:{ Exception -> 0x0f5b }
            r1.originalPath = r6     // Catch:{ Exception -> 0x0f5b }
            r1.sendEncryptedRequest = r4     // Catch:{ Exception -> 0x0f5b }
            r2 = 2
            r1.type = r2     // Catch:{ Exception -> 0x0f5b }
            r1.obj = r12     // Catch:{ Exception -> 0x0f5b }
            if (r14 == 0) goto L_0x17ec
            boolean r2 = r14.containsKey(r15)     // Catch:{ Exception -> 0x0f5b }
            if (r2 == 0) goto L_0x17ec
            java.lang.Object r2 = r14.get(r15)     // Catch:{ Exception -> 0x0f5b }
            r1.parentObject = r2     // Catch:{ Exception -> 0x0f5b }
            goto L_0x17ee
        L_0x17ec:
            r1.parentObject = r5     // Catch:{ Exception -> 0x0f5b }
        L_0x17ee:
            r1.encryptedChat = r9     // Catch:{ Exception -> 0x0f5b }
            r2 = 1
            r1.performMediaUpload = r2     // Catch:{ Exception -> 0x0f5b }
            r2 = r63
            if (r2 == 0) goto L_0x1807
            int r3 = r63.length()     // Catch:{ Exception -> 0x0f5b }
            if (r3 <= 0) goto L_0x1807
            r3 = r41
            boolean r3 = r2.startsWith(r3)     // Catch:{ Exception -> 0x0f5b }
            if (r3 == 0) goto L_0x1807
            r1.httpLocation = r2     // Catch:{ Exception -> 0x0f5b }
        L_0x1807:
            if (r13 == 0) goto L_0x180b
            r2 = 1
            goto L_0x180c
        L_0x180b:
            r2 = 0
        L_0x180c:
            r1.scheduled = r2     // Catch:{ Exception -> 0x0f5b }
            r7.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x0f5b }
            goto L_0x1627
        L_0x1813:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x0f5b }
            r1.<init>()     // Catch:{ Exception -> 0x0f5b }
            long r2 = r8.id     // Catch:{ Exception -> 0x0f5b }
            r1.id = r2     // Catch:{ Exception -> 0x0f5b }
            long r2 = r8.access_hash     // Catch:{ Exception -> 0x0f5b }
            r1.access_hash = r2     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media     // Catch:{ Exception -> 0x0f5b }
            byte[] r3 = r8.key     // Catch:{ Exception -> 0x0f5b }
            r2.key = r3     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media     // Catch:{ Exception -> 0x0f5b }
            byte[] r3 = r8.iv     // Catch:{ Exception -> 0x0f5b }
            r2.iv = r3     // Catch:{ Exception -> 0x0f5b }
            org.telegram.messenger.SecretChatHelper r2 = r51.getSecretChatHelper()     // Catch:{ Exception -> 0x0f5b }
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner     // Catch:{ Exception -> 0x0f5b }
            r5 = 0
            r54 = r2
            r55 = r4
            r56 = r3
            r57 = r9
            r58 = r1
            r59 = r5
            r60 = r12
            r54.performSendEncryptedRequest(r55, r56, r57, r58, r59, r60)     // Catch:{ Exception -> 0x0f5b }
            r2 = r64
            goto L_0x177a
        L_0x1848:
            r10 = r61
            r1 = r3
            r66 = r13
            r8 = r18
            r15 = r38
            r3 = r41
            r6 = r42
            r13 = r73
            r18 = r2
            r2 = r63
        L_0x185b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.sizes     // Catch:{ Exception -> 0x19d0 }
            r41 = r3
            r3 = 0
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x19d0 }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2     // Catch:{ Exception -> 0x19d0 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r5.sizes     // Catch:{ Exception -> 0x19d0 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r13 = r5.sizes     // Catch:{ Exception -> 0x19cc }
            int r13 = r13.size()     // Catch:{ Exception -> 0x19cc }
            r19 = 1
            int r13 = r13 + -1
            java.lang.Object r3 = r3.get(r13)     // Catch:{ Exception -> 0x19cc }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x19cc }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r2)     // Catch:{ Exception -> 0x19cc }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r13 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto     // Catch:{ Exception -> 0x19cc }
            r13.<init>()     // Catch:{ Exception -> 0x19cc }
            r4.media = r13     // Catch:{ Exception -> 0x19cc }
            r13.caption = r1     // Catch:{ Exception -> 0x19cc }
            byte[] r1 = r2.bytes     // Catch:{ Exception -> 0x19cc }
            if (r1 == 0) goto L_0x1896
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r13 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r13     // Catch:{ Exception -> 0x1891 }
            byte[] r1 = r2.bytes     // Catch:{ Exception -> 0x1891 }
            r13.thumb = r1     // Catch:{ Exception -> 0x1891 }
            r53 = r5
            goto L_0x189f
        L_0x1891:
            r0 = move-exception
            r1 = r73
            goto L_0x1978
        L_0x1896:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r13 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r13     // Catch:{ Exception -> 0x19cc }
            r53 = r5
            r1 = 0
            byte[] r5 = new byte[r1]     // Catch:{ Exception -> 0x19cc }
            r13.thumb = r5     // Catch:{ Exception -> 0x19cc }
        L_0x189f:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x19cc }
            int r5 = r2.h     // Catch:{ Exception -> 0x19cc }
            r1.thumb_h = r5     // Catch:{ Exception -> 0x19cc }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x19cc }
            int r2 = r2.w     // Catch:{ Exception -> 0x19cc }
            r1.thumb_w = r2     // Catch:{ Exception -> 0x19cc }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x19cc }
            int r2 = r3.w     // Catch:{ Exception -> 0x19cc }
            r1.w = r2     // Catch:{ Exception -> 0x19cc }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x19cc }
            int r2 = r3.h     // Catch:{ Exception -> 0x19cc }
            r1.h = r2     // Catch:{ Exception -> 0x19cc }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r4.media     // Catch:{ Exception -> 0x19cc }
            int r2 = r3.size     // Catch:{ Exception -> 0x19cc }
            r1.size = r2     // Catch:{ Exception -> 0x19cc }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r3.location     // Catch:{ Exception -> 0x19cc }
            byte[] r1 = r1.key     // Catch:{ Exception -> 0x19cc }
            if (r1 == 0) goto L_0x1906
            int r1 = (r70 > r16 ? 1 : (r70 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x18c8
            goto L_0x1906
        L_0x18c8:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1891 }
            r1.<init>()     // Catch:{ Exception -> 0x1891 }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r3.location     // Catch:{ Exception -> 0x1891 }
            long r13 = r2.volume_id     // Catch:{ Exception -> 0x1891 }
            r1.id = r13     // Catch:{ Exception -> 0x1891 }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r3.location     // Catch:{ Exception -> 0x1891 }
            long r13 = r2.secret     // Catch:{ Exception -> 0x1891 }
            r1.access_hash = r13     // Catch:{ Exception -> 0x1891 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media     // Catch:{ Exception -> 0x1891 }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r3.location     // Catch:{ Exception -> 0x1891 }
            byte[] r5 = r5.key     // Catch:{ Exception -> 0x1891 }
            r2.key = r5     // Catch:{ Exception -> 0x1891 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r2 = r4.media     // Catch:{ Exception -> 0x1891 }
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.location     // Catch:{ Exception -> 0x1891 }
            byte[] r3 = r3.iv     // Catch:{ Exception -> 0x1891 }
            r2.iv = r3     // Catch:{ Exception -> 0x1891 }
            org.telegram.messenger.SecretChatHelper r2 = r51.getSecretChatHelper()     // Catch:{ Exception -> 0x1891 }
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner     // Catch:{ Exception -> 0x1891 }
            r5 = 0
            r54 = r2
            r55 = r4
            r56 = r3
            r57 = r9
            r58 = r1
            r59 = r5
            r60 = r12
            r54.performSendEncryptedRequest(r55, r56, r57, r58, r59, r60)     // Catch:{ Exception -> 0x1891 }
            r2 = r64
            r1 = r73
            goto L_0x196a
        L_0x1906:
            if (r64 != 0) goto L_0x1936
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1891 }
            r2.<init>(r10)     // Catch:{ Exception -> 0x1891 }
            r2.encryptedChat = r9     // Catch:{ Exception -> 0x1891 }
            r1 = 0
            r2.type = r1     // Catch:{ Exception -> 0x1891 }
            r2.originalPath = r6     // Catch:{ Exception -> 0x1891 }
            r2.sendEncryptedRequest = r4     // Catch:{ Exception -> 0x1891 }
            r2.obj = r12     // Catch:{ Exception -> 0x1891 }
            if (r14 == 0) goto L_0x1927
            boolean r1 = r14.containsKey(r15)     // Catch:{ Exception -> 0x1891 }
            if (r1 == 0) goto L_0x1927
            java.lang.Object r1 = r14.get(r15)     // Catch:{ Exception -> 0x1891 }
            r2.parentObject = r1     // Catch:{ Exception -> 0x1891 }
            goto L_0x1929
        L_0x1927:
            r2.parentObject = r8     // Catch:{ Exception -> 0x1891 }
        L_0x1929:
            r1 = 1
            r2.performMediaUpload = r1     // Catch:{ Exception -> 0x1891 }
            r1 = r73
            if (r1 == 0) goto L_0x1932
            r3 = 1
            goto L_0x1933
        L_0x1932:
            r3 = 0
        L_0x1933:
            r2.scheduled = r3     // Catch:{ Exception -> 0x1977 }
            goto L_0x193a
        L_0x1936:
            r1 = r73
            r2 = r64
        L_0x193a:
            boolean r3 = android.text.TextUtils.isEmpty(r63)     // Catch:{ Exception -> 0x19b4 }
            if (r3 != 0) goto L_0x194d
            r3 = r63
            r5 = r41
            boolean r5 = r3.startsWith(r5)     // Catch:{ Exception -> 0x1977 }
            if (r5 == 0) goto L_0x194d
            r2.httpLocation = r3     // Catch:{ Exception -> 0x1977 }
            goto L_0x1963
        L_0x194d:
            r3 = r53
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r3.sizes     // Catch:{ Exception -> 0x19b4 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r3.sizes     // Catch:{ Exception -> 0x19b4 }
            int r8 = r8.size()     // Catch:{ Exception -> 0x19b4 }
            r9 = 1
            int r8 = r8 - r9
            java.lang.Object r5 = r5.get(r8)     // Catch:{ Exception -> 0x19b4 }
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5     // Catch:{ Exception -> 0x19b4 }
            r2.photoSize = r5     // Catch:{ Exception -> 0x19b4 }
            r2.locationParent = r3     // Catch:{ Exception -> 0x19b4 }
        L_0x1963:
            int r3 = (r70 > r16 ? 1 : (r70 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x196a
            r7.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x1977 }
        L_0x196a:
            int r3 = (r70 > r16 ? 1 : (r70 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x19b6
            org.telegram.tgnet.TLObject r3 = r2.sendEncryptedRequest     // Catch:{ Exception -> 0x19b4 }
            if (r3 == 0) goto L_0x197b
            org.telegram.tgnet.TLObject r3 = r2.sendEncryptedRequest     // Catch:{ Exception -> 0x1977 }
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r3 = (org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia) r3     // Catch:{ Exception -> 0x1977 }
            goto L_0x1982
        L_0x1977:
            r0 = move-exception
        L_0x1978:
            r2 = r0
            goto L_0x0var_
        L_0x197b:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia     // Catch:{ Exception -> 0x19b4 }
            r3.<init>()     // Catch:{ Exception -> 0x19b4 }
            r2.sendEncryptedRequest = r3     // Catch:{ Exception -> 0x19b4 }
        L_0x1982:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r2.messageObjects     // Catch:{ Exception -> 0x19b4 }
            r5.add(r12)     // Catch:{ Exception -> 0x19b4 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r5 = r2.messages     // Catch:{ Exception -> 0x19b4 }
            r13 = r18
            r5.add(r13)     // Catch:{ Exception -> 0x1b18 }
            java.util.ArrayList<java.lang.String> r5 = r2.originalPaths     // Catch:{ Exception -> 0x1b18 }
            r5.add(r6)     // Catch:{ Exception -> 0x1b18 }
            r5 = 1
            r2.performMediaUpload = r5     // Catch:{ Exception -> 0x1b18 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r5 = r3.messages     // Catch:{ Exception -> 0x1b18 }
            r5.add(r4)     // Catch:{ Exception -> 0x1b18 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r4 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1b18 }
            r4.<init>()     // Catch:{ Exception -> 0x1b18 }
            r5 = r66
            r6 = 3
            if (r5 != r6) goto L_0x19a7
            r16 = 1
        L_0x19a7:
            r5 = r16
            r4.id = r5     // Catch:{ Exception -> 0x1b18 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r3 = r3.files     // Catch:{ Exception -> 0x1b18 }
            r3.add(r4)     // Catch:{ Exception -> 0x1b18 }
            r7.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x1b18 }
            goto L_0x19b8
        L_0x19b4:
            r0 = move-exception
            goto L_0x19d2
        L_0x19b6:
            r13 = r18
        L_0x19b8:
            if (r68 != 0) goto L_0x1b15
            org.telegram.messenger.MediaDataController r2 = r51.getMediaDataController()     // Catch:{ Exception -> 0x1b18 }
            if (r65 == 0) goto L_0x19c5
            int r3 = r65.getId()     // Catch:{ Exception -> 0x1b18 }
            goto L_0x19c6
        L_0x19c5:
            r3 = 0
        L_0x19c6:
            r4 = 0
            r2.cleanDraft(r10, r3, r4)     // Catch:{ Exception -> 0x1b18 }
            goto L_0x1b15
        L_0x19cc:
            r0 = move-exception
            r1 = r73
            goto L_0x19d2
        L_0x19d0:
            r0 = move-exception
            r1 = r13
        L_0x19d2:
            r13 = r18
            goto L_0x1b20
        L_0x19d6:
            r0 = move-exception
            r1 = r73
            r13 = r2
            goto L_0x1b20
        L_0x19dc:
            r6 = r55
            r26 = r60
            r4 = r69
            r8 = r5
            r2 = r10
            r10 = r52
            r50 = r13
            r13 = r1
            r1 = r50
            if (r9 != 0) goto L_0x1a67
            org.telegram.tgnet.TLRPC$TL_messages_sendMessage r5 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage     // Catch:{ Exception -> 0x1b18 }
            r5.<init>()     // Catch:{ Exception -> 0x1b18 }
            r5.message = r6     // Catch:{ Exception -> 0x1b18 }
            if (r68 != 0) goto L_0x19f8
            r6 = 1
            goto L_0x19f9
        L_0x19f8:
            r6 = 0
        L_0x19f9:
            r5.clear_draft = r6     // Catch:{ Exception -> 0x1b18 }
            boolean r6 = r13.silent     // Catch:{ Exception -> 0x1b18 }
            r5.silent = r6     // Catch:{ Exception -> 0x1b18 }
            r5.peer = r8     // Catch:{ Exception -> 0x1b18 }
            long r8 = r13.random_id     // Catch:{ Exception -> 0x1b18 }
            r5.random_id = r8     // Catch:{ Exception -> 0x1b18 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r6 = r13.reply_to     // Catch:{ Exception -> 0x1b18 }
            if (r6 == 0) goto L_0x1a1b
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r6 = r13.reply_to     // Catch:{ Exception -> 0x1b18 }
            int r6 = r6.reply_to_msg_id     // Catch:{ Exception -> 0x1b18 }
            if (r6 == 0) goto L_0x1a1b
            int r6 = r5.flags     // Catch:{ Exception -> 0x1b18 }
            r8 = 1
            r6 = r6 | r8
            r5.flags = r6     // Catch:{ Exception -> 0x1b18 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r6 = r13.reply_to     // Catch:{ Exception -> 0x1b18 }
            int r6 = r6.reply_to_msg_id     // Catch:{ Exception -> 0x1b18 }
            r5.reply_to_msg_id = r6     // Catch:{ Exception -> 0x1b18 }
        L_0x1a1b:
            if (r67 != 0) goto L_0x1a20
            r6 = 1
            r5.no_webpage = r6     // Catch:{ Exception -> 0x1b18 }
        L_0x1a20:
            if (r4 == 0) goto L_0x1a31
            boolean r6 = r69.isEmpty()     // Catch:{ Exception -> 0x1b18 }
            if (r6 != 0) goto L_0x1a31
            r5.entities = r4     // Catch:{ Exception -> 0x1b18 }
            int r4 = r5.flags     // Catch:{ Exception -> 0x1b18 }
            r6 = 8
            r4 = r4 | r6
            r5.flags = r4     // Catch:{ Exception -> 0x1b18 }
        L_0x1a31:
            if (r1 == 0) goto L_0x1a3b
            r5.schedule_date = r1     // Catch:{ Exception -> 0x1b18 }
            int r4 = r5.flags     // Catch:{ Exception -> 0x1b18 }
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r5.flags = r4     // Catch:{ Exception -> 0x1b18 }
        L_0x1a3b:
            r4 = 0
            r6 = 0
            if (r1 == 0) goto L_0x1a41
            r8 = 1
            goto L_0x1a42
        L_0x1a41:
            r8 = 0
        L_0x1a42:
            r52 = r51
            r53 = r5
            r54 = r12
            r55 = r4
            r56 = r6
            r57 = r26
            r58 = r8
            r52.performSendMessageRequest(r53, r54, r55, r56, r57, r58)     // Catch:{ Exception -> 0x1b18 }
            if (r68 != 0) goto L_0x1b15
            org.telegram.messenger.MediaDataController r4 = r51.getMediaDataController()     // Catch:{ Exception -> 0x1b18 }
            if (r65 == 0) goto L_0x1a60
            int r5 = r65.getId()     // Catch:{ Exception -> 0x1b18 }
            goto L_0x1a61
        L_0x1a60:
            r5 = 0
        L_0x1a61:
            r6 = 0
            r4.cleanDraft(r2, r5, r6)     // Catch:{ Exception -> 0x1b18 }
            goto L_0x1b15
        L_0x1a67:
            int r5 = r9.layer     // Catch:{ Exception -> 0x1b18 }
            int r5 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r5)     // Catch:{ Exception -> 0x1b18 }
            r8 = 73
            if (r5 < r8) goto L_0x1a77
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1b18 }
            r5.<init>()     // Catch:{ Exception -> 0x1b18 }
            goto L_0x1a7c
        L_0x1a77:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x1b18 }
            r5.<init>()     // Catch:{ Exception -> 0x1b18 }
        L_0x1a7c:
            int r8 = r13.ttl     // Catch:{ Exception -> 0x1b18 }
            r5.ttl = r8     // Catch:{ Exception -> 0x1b18 }
            if (r4 == 0) goto L_0x1a90
            boolean r8 = r69.isEmpty()     // Catch:{ Exception -> 0x1b18 }
            if (r8 != 0) goto L_0x1a90
            r5.entities = r4     // Catch:{ Exception -> 0x1b18 }
            int r4 = r5.flags     // Catch:{ Exception -> 0x1b18 }
            r4 = r4 | 128(0x80, float:1.794E-43)
            r5.flags = r4     // Catch:{ Exception -> 0x1b18 }
        L_0x1a90:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = r13.reply_to     // Catch:{ Exception -> 0x1b18 }
            if (r4 == 0) goto L_0x1aa9
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = r13.reply_to     // Catch:{ Exception -> 0x1b18 }
            long r7 = r4.reply_to_random_id     // Catch:{ Exception -> 0x1b18 }
            int r4 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x1aa9
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = r13.reply_to     // Catch:{ Exception -> 0x1b18 }
            long r7 = r4.reply_to_random_id     // Catch:{ Exception -> 0x1b18 }
            r5.reply_to_random_id = r7     // Catch:{ Exception -> 0x1b18 }
            int r4 = r5.flags     // Catch:{ Exception -> 0x1b18 }
            r7 = 8
            r4 = r4 | r7
            r5.flags = r4     // Catch:{ Exception -> 0x1b18 }
        L_0x1aa9:
            if (r14 == 0) goto L_0x1abf
            java.lang.Object r4 = r14.get(r10)     // Catch:{ Exception -> 0x1b18 }
            if (r4 == 0) goto L_0x1abf
            java.lang.Object r4 = r14.get(r10)     // Catch:{ Exception -> 0x1b18 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x1b18 }
            r5.via_bot_name = r4     // Catch:{ Exception -> 0x1b18 }
            int r4 = r5.flags     // Catch:{ Exception -> 0x1b18 }
            r4 = r4 | 2048(0x800, float:2.87E-42)
            r5.flags = r4     // Catch:{ Exception -> 0x1b18 }
        L_0x1abf:
            boolean r4 = r13.silent     // Catch:{ Exception -> 0x1b18 }
            r5.silent = r4     // Catch:{ Exception -> 0x1b18 }
            long r7 = r13.random_id     // Catch:{ Exception -> 0x1b18 }
            r5.random_id = r7     // Catch:{ Exception -> 0x1b18 }
            r5.message = r6     // Catch:{ Exception -> 0x1b18 }
            r14 = r36
            if (r14 == 0) goto L_0x1ae3
            java.lang.String r4 = r14.url     // Catch:{ Exception -> 0x1b18 }
            if (r4 == 0) goto L_0x1ae3
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage     // Catch:{ Exception -> 0x1b18 }
            r4.<init>()     // Catch:{ Exception -> 0x1b18 }
            r5.media = r4     // Catch:{ Exception -> 0x1b18 }
            java.lang.String r6 = r14.url     // Catch:{ Exception -> 0x1b18 }
            r4.url = r6     // Catch:{ Exception -> 0x1b18 }
            int r4 = r5.flags     // Catch:{ Exception -> 0x1b18 }
            r4 = r4 | 512(0x200, float:7.175E-43)
            r5.flags = r4     // Catch:{ Exception -> 0x1b18 }
            goto L_0x1aea
        L_0x1ae3:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty     // Catch:{ Exception -> 0x1b18 }
            r4.<init>()     // Catch:{ Exception -> 0x1b18 }
            r5.media = r4     // Catch:{ Exception -> 0x1b18 }
        L_0x1aea:
            org.telegram.messenger.SecretChatHelper r4 = r51.getSecretChatHelper()     // Catch:{ Exception -> 0x1b18 }
            org.telegram.tgnet.TLRPC$Message r6 = r12.messageOwner     // Catch:{ Exception -> 0x1b18 }
            r7 = 0
            r8 = 0
            r54 = r4
            r55 = r5
            r56 = r6
            r57 = r9
            r58 = r7
            r59 = r8
            r60 = r12
            r54.performSendEncryptedRequest(r55, r56, r57, r58, r59, r60)     // Catch:{ Exception -> 0x1b18 }
            if (r68 != 0) goto L_0x1b15
            org.telegram.messenger.MediaDataController r4 = r51.getMediaDataController()     // Catch:{ Exception -> 0x1b18 }
            if (r65 == 0) goto L_0x1b10
            int r5 = r65.getId()     // Catch:{ Exception -> 0x1b18 }
            goto L_0x1b11
        L_0x1b10:
            r5 = 0
        L_0x1b11:
            r6 = 0
            r4.cleanDraft(r2, r5, r6)     // Catch:{ Exception -> 0x1b18 }
        L_0x1b15:
            r2 = r51
            goto L_0x1b64
        L_0x1b18:
            r0 = move-exception
            goto L_0x1b20
        L_0x1b1a:
            r0 = move-exception
            r50 = r13
            r13 = r1
            r1 = r50
        L_0x1b20:
            r2 = r0
            r5 = r13
            goto L_0x1b32
        L_0x1b23:
            r0 = move-exception
            r50 = r13
            r13 = r1
            r1 = r50
            goto L_0x1b2e
        L_0x1b2a:
            r0 = move-exception
            r13 = r1
            r1 = r73
        L_0x1b2e:
            r2 = r0
            r5 = r13
            goto L_0x02f3
        L_0x1b32:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
            org.telegram.messenger.MessagesStorage r2 = r51.getMessagesStorage()
            if (r1 == 0) goto L_0x1b3d
            r4 = 1
            goto L_0x1b3e
        L_0x1b3d:
            r4 = 0
        L_0x1b3e:
            r2.markMessageAsSendError(r5, r4)
            if (r12 == 0) goto L_0x1b48
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner
            r2 = 2
            r1.send_state = r2
        L_0x1b48:
            org.telegram.messenger.NotificationCenter r1 = r51.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messageSendError
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r4 = r5.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r6 = 0
            r3[r6] = r4
            r1.postNotificationName(r2, r3)
            int r1 = r5.id
            r2 = r51
            r2.processSentMessage(r1)
        L_0x1b64:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, org.telegram.tgnet.TLRPC$TL_messageMediaPoll, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, boolean, int, int, java.lang.Object):void");
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
        } else if (i2 == 5) {
            String str17 = "stickerset_" + delayedMessage2.obj.getId();
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            tLRPC$TL_messages_getStickerSet.stickerset = (TLRPC$InputStickerSet) delayedMessage2.parentObject;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate(delayedMessage2, str17) {
                public final /* synthetic */ SendMessagesHelper.DelayedMessage f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SendMessagesHelper.this.lambda$performSendDelayedMessage$32$SendMessagesHelper(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
            putToDelayedMessages(str17, delayedMessage2);
        }
    }

    public /* synthetic */ void lambda$performSendDelayedMessage$32$SendMessagesHelper(DelayedMessage delayedMessage, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, delayedMessage, str) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;
            public final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$31$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$31$SendMessagesHelper(TLObject tLObject, DelayedMessage delayedMessage, String str) {
        boolean z;
        if (tLObject != null) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) tLObject;
            getMediaDataController().storeTempStickerSet(tLRPC$TL_messages_stickerSet);
            TLRPC$TL_inputStickerSetShortName tLRPC$TL_inputStickerSetShortName = new TLRPC$TL_inputStickerSetShortName();
            ((TLRPC$TL_documentAttributeSticker_layer55) delayedMessage.locationParent).stickerset = tLRPC$TL_inputStickerSetShortName;
            tLRPC$TL_inputStickerSetShortName.short_name = tLRPC$TL_messages_stickerSet.set.short_name;
            z = true;
        } else {
            z = false;
        }
        ArrayList remove = this.delayedMessages.remove(str);
        if (remove != null && !remove.isEmpty()) {
            if (z) {
                getMessagesStorage().replaceMessageIfExists(((DelayedMessage) remove.get(0)).obj.messageOwner, (ArrayList<TLRPC$User>) null, (ArrayList<TLRPC$Chat>) null, false);
            }
            MessageObject messageObject = delayedMessage.obj;
            getSecretChatHelper().performSendEncryptedRequest((TLRPC$DecryptedMessage) delayedMessage.sendEncryptedRequest, messageObject.messageOwner, delayedMessage.encryptedChat, (TLRPC$InputEncryptedFile) null, (String) null, messageObject);
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
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, -1L, -1L, Boolean.FALSE);
                    break;
                } else {
                    i++;
                }
            }
            TLRPC$TL_messages_uploadMedia tLRPC$TL_messages_uploadMedia = new TLRPC$TL_messages_uploadMedia();
            tLRPC$TL_messages_uploadMedia.media = tLRPC$InputMedia;
            tLRPC$TL_messages_uploadMedia.peer = ((TLRPC$TL_messages_sendMultiMedia) delayedMessage.sendRequest).peer;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_uploadMedia, new RequestDelegate(tLRPC$InputMedia, delayedMessage) {
                public final /* synthetic */ TLRPC$InputMedia f$1;
                public final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    SendMessagesHelper.this.lambda$uploadMultiMedia$34$SendMessagesHelper(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, -1L, -1L, Boolean.FALSE);
                    break;
                } else {
                    i2++;
                }
            }
            sendReadyToSendGroup(delayedMessage, false, true);
        }
    }

    public /* synthetic */ void lambda$uploadMultiMedia$34$SendMessagesHelper(TLRPC$InputMedia tLRPC$InputMedia, DelayedMessage delayedMessage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$InputMedia, delayedMessage) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ TLRPC$InputMedia f$2;
            public final /* synthetic */ SendMessagesHelper.DelayedMessage f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$33$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
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
    public /* synthetic */ void lambda$null$33$SendMessagesHelper(org.telegram.tgnet.TLObject r6, org.telegram.tgnet.TLRPC$InputMedia r7, org.telegram.messenger.SendMessagesHelper.DelayedMessage r8) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$33$SendMessagesHelper(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$InputMedia, org.telegram.messenger.SendMessagesHelper$DelayedMessage):void");
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

    public /* synthetic */ void lambda$null$35$SendMessagesHelper(String str) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, str, Integer.valueOf(this.currentAccount));
    }

    public /* synthetic */ void lambda$stopVideoService$36$SendMessagesHelper(String str) {
        AndroidUtilities.runOnUIThread(new Runnable(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$35$SendMessagesHelper(this.f$1);
            }
        });
    }

    public void stopVideoService(String str) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$stopVideoService$36$SendMessagesHelper(this.f$1);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void putToSendingMessages(TLRPC$Message tLRPC$Message, boolean z) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, z) {
                public final /* synthetic */ TLRPC$Message f$1;
                public final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$putToSendingMessages$37$SendMessagesHelper(this.f$1, this.f$2);
                }
            });
        } else {
            putToSendingMessages(tLRPC$Message, z, true);
        }
    }

    public /* synthetic */ void lambda$putToSendingMessages$37$SendMessagesHelper(TLRPC$Message tLRPC$Message, boolean z) {
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
        $$Lambda$SendMessagesHelper$jSSj8AzLJc8YePfGn32oRIzLki0 r2 = new RequestDelegate(arrayList3, tLRPC$TL_messages_sendMultiMedia, arrayList, arrayList2, delayedMessage, z) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$2;
            public final /* synthetic */ ArrayList f$3;
            public final /* synthetic */ ArrayList f$4;
            public final /* synthetic */ SendMessagesHelper.DelayedMessage f$5;
            public final /* synthetic */ boolean f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$performSendMessageRequestMulti$45$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
            }
        };
        TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia2 = tLRPC$TL_messages_sendMultiMedia;
        connectionsManager.sendRequest((TLObject) tLRPC$TL_messages_sendMultiMedia, (RequestDelegate) r2, (QuickAckDelegate) null, 68);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$45$SendMessagesHelper(ArrayList arrayList, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList arrayList4 = arrayList;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        if (tLRPC$TL_error2 != null && FileRefController.isFileRefError(tLRPC$TL_error2.text)) {
            if (arrayList4 != null) {
                ArrayList arrayList5 = new ArrayList(arrayList);
                getFileRefController().requestReference(arrayList5, tLRPC$TL_messages_sendMultiMedia, arrayList2, arrayList3, arrayList5, delayedMessage, Boolean.valueOf(z));
                return;
            } else if (delayedMessage != null) {
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_messages_sendMultiMedia, delayedMessage, arrayList2, z) {
                    public final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$1;
                    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;
                    public final /* synthetic */ ArrayList f$3;
                    public final /* synthetic */ boolean f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$null$38$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, arrayList2, arrayList3, z, tLRPC$TL_messages_sendMultiMedia) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ ArrayList f$3;
            public final /* synthetic */ ArrayList f$4;
            public final /* synthetic */ boolean f$5;
            public final /* synthetic */ TLRPC$TL_messages_sendMultiMedia f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$44$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$38$SendMessagesHelper(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, DelayedMessage delayedMessage, ArrayList arrayList, boolean z) {
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

    public /* synthetic */ void lambda$null$44$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, ArrayList arrayList, ArrayList arrayList2, boolean z, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia) {
        boolean z2;
        TLRPC$Updates tLRPC$Updates;
        TLRPC$Message tLRPC$Message;
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        ArrayList arrayList3 = arrayList;
        boolean z3 = z;
        if (tLRPC$TL_error2 == null) {
            SparseArray sparseArray = new SparseArray();
            LongSparseArray longSparseArray = new LongSparseArray();
            TLRPC$Updates tLRPC$Updates2 = (TLRPC$Updates) tLObject;
            ArrayList<TLRPC$Update> arrayList4 = tLRPC$Updates2.updates;
            SparseArray sparseArray2 = null;
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
                        public final /* synthetic */ TLRPC$TL_updateNewMessage f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$39$SendMessagesHelper(this.f$1);
                        }
                    });
                    arrayList4.remove(i);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) {
                    TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage = (TLRPC$TL_updateNewChannelMessage) tLRPC$Update;
                    TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(MessagesController.getUpdateChannelId(tLRPC$TL_updateNewChannelMessage)));
                    if (!((chat != null && !chat.megagroup) || (tLRPC$TL_messageReplyHeader = tLRPC$TL_updateNewChannelMessage.message.reply_to) == null || (tLRPC$TL_messageReplyHeader.reply_to_top_id == 0 && tLRPC$TL_messageReplyHeader.reply_to_msg_id == 0))) {
                        if (sparseArray2 == null) {
                            sparseArray2 = new SparseArray();
                        }
                        int dialogId = (int) MessageObject.getDialogId(tLRPC$TL_updateNewChannelMessage.message);
                        SparseArray sparseArray3 = (SparseArray) sparseArray2.get(dialogId);
                        if (sparseArray3 == null) {
                            sparseArray3 = new SparseArray();
                            sparseArray2.put(dialogId, sparseArray3);
                        }
                        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader2 = tLRPC$TL_updateNewChannelMessage.message.reply_to;
                        int i2 = tLRPC$TL_messageReplyHeader2.reply_to_top_id;
                        if (i2 == 0) {
                            i2 = tLRPC$TL_messageReplyHeader2.reply_to_msg_id;
                        }
                        TLRPC$TL_messageReplies tLRPC$TL_messageReplies = (TLRPC$TL_messageReplies) sparseArray3.get(i2);
                        if (tLRPC$TL_messageReplies == null) {
                            tLRPC$TL_messageReplies = new TLRPC$TL_messageReplies();
                            sparseArray3.put(i2, tLRPC$TL_messageReplies);
                        }
                        TLRPC$Peer tLRPC$Peer = tLRPC$TL_updateNewChannelMessage.message.from_id;
                        if (tLRPC$Peer != null) {
                            tLRPC$TL_messageReplies.recent_repliers.add(0, tLRPC$Peer);
                        }
                        tLRPC$TL_messageReplies.replies++;
                    }
                    TLRPC$Message tLRPC$Message3 = tLRPC$TL_updateNewChannelMessage.message;
                    sparseArray.put(tLRPC$Message3.id, tLRPC$Message3);
                    Utilities.stageQueue.postRunnable(new Runnable(tLRPC$TL_updateNewChannelMessage) {
                        public final /* synthetic */ TLRPC$TL_updateNewChannelMessage f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$40$SendMessagesHelper(this.f$1);
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
            if (sparseArray2 != null) {
                getMessagesStorage().putChannelViews((SparseArray<SparseIntArray>) null, (SparseArray<SparseIntArray>) null, sparseArray2, true, true);
                getNotificationCenter().postNotificationName(NotificationCenter.didUpdateMessagesViews, null, null, sparseArray2, Boolean.TRUE);
            }
            int i3 = 0;
            while (true) {
                if (i3 >= arrayList.size()) {
                    tLRPC$Updates = tLRPC$Updates2;
                    z2 = false;
                    break;
                }
                MessageObject messageObject = (MessageObject) arrayList3.get(i3);
                String str = (String) arrayList2.get(i3);
                TLRPC$Message tLRPC$Message5 = messageObject.messageOwner;
                int i4 = tLRPC$Message5.id;
                ArrayList arrayList5 = new ArrayList();
                String str2 = tLRPC$Message5.attachPath;
                Integer num = (Integer) longSparseArray.get(tLRPC$Message5.random_id);
                if (num == null || (tLRPC$Message = (TLRPC$Message) sparseArray.get(num.intValue())) == null) {
                    tLRPC$Updates = tLRPC$Updates2;
                    z2 = true;
                } else {
                    MessageObject.getDialogId(tLRPC$Message);
                    arrayList5.add(tLRPC$Message);
                    ArrayList arrayList6 = arrayList5;
                    int i5 = i4;
                    TLRPC$Message tLRPC$Message6 = tLRPC$Message5;
                    updateMediaPaths(messageObject, tLRPC$Message, tLRPC$Message.id, str, false);
                    int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                    tLRPC$Message6.id = tLRPC$Message.id;
                    if ((tLRPC$Message6.flags & Integer.MIN_VALUE) != 0) {
                        tLRPC$Message.flags |= Integer.MIN_VALUE;
                    }
                    long j = tLRPC$Message.grouped_id;
                    if (!z3) {
                        Integer num2 = (Integer) getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(tLRPC$Message.dialog_id));
                        if (num2 == null) {
                            num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(tLRPC$Message.out, tLRPC$Message.dialog_id));
                            getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(tLRPC$Message.dialog_id), num2);
                        }
                        tLRPC$Message.unread = num2.intValue() < tLRPC$Message.id;
                    }
                    getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                    tLRPC$Message6.send_state = 0;
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i5), Integer.valueOf(tLRPC$Message6.id), tLRPC$Message6, Long.valueOf(tLRPC$Message6.dialog_id), Long.valueOf(j), Integer.valueOf(mediaExistanceFlags), Boolean.valueOf(z));
                    DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                    SparseArray sparseArray4 = sparseArray;
                    $$Lambda$SendMessagesHelper$Ao7ILsxk6H_oNUrGG5iDdfk90qM r15 = r0;
                    $$Lambda$SendMessagesHelper$Ao7ILsxk6H_oNUrGG5iDdfk90qM r0 = new Runnable(tLRPC$Message6, i5, z, arrayList6, j, mediaExistanceFlags) {
                        public final /* synthetic */ TLRPC$Message f$1;
                        public final /* synthetic */ int f$2;
                        public final /* synthetic */ boolean f$3;
                        public final /* synthetic */ ArrayList f$4;
                        public final /* synthetic */ long f$5;
                        public final /* synthetic */ int f$6;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r8;
                        }

                        public final void run() {
                            SendMessagesHelper.this.lambda$null$42$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                        }
                    };
                    storageQueue.postRunnable(r15);
                    i3++;
                    sparseArray = sparseArray4;
                    tLRPC$Updates2 = tLRPC$Updates2;
                    longSparseArray = longSparseArray;
                }
            }
            tLRPC$Updates = tLRPC$Updates2;
            z2 = true;
            Utilities.stageQueue.postRunnable(new Runnable(tLRPC$Updates) {
                public final /* synthetic */ TLRPC$Updates f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$43$SendMessagesHelper(this.f$1);
                }
            });
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error2, (BaseFragment) null, tLRPC$TL_messages_sendMultiMedia, new Object[0]);
            z2 = true;
        }
        if (z2) {
            for (int i6 = 0; i6 < arrayList.size(); i6++) {
                TLRPC$Message tLRPC$Message7 = ((MessageObject) arrayList3.get(i6)).messageOwner;
                getMessagesStorage().markMessageAsSendError(tLRPC$Message7, z3);
                tLRPC$Message7.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message7.id));
                processSentMessage(tLRPC$Message7.id);
                removeFromSendingMessages(tLRPC$Message7.id, z3);
            }
        }
    }

    public /* synthetic */ void lambda$null$39$SendMessagesHelper(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$40$SendMessagesHelper(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.peer_id.channel_id);
    }

    public /* synthetic */ void lambda$null$42$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, boolean z, ArrayList arrayList, long j, int i2) {
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, Integer.valueOf(i), tLRPC$Message2.id, 0, false, tLRPC$Message2.peer_id.channel_id, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message2, i, j, i2, z) {
            public final /* synthetic */ TLRPC$Message f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ long f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ boolean f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r6;
                this.f$5 = r7;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$41$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$null$41$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, long j, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), Long.valueOf(j), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$43$SendMessagesHelper(TLRPC$Updates tLRPC$Updates) {
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
            $$Lambda$SendMessagesHelper$igjN84Ahqf0BJOt34JKvsk1HSI r14 = r0;
            ConnectionsManager connectionsManager = getConnectionsManager();
            $$Lambda$SendMessagesHelper$igjN84Ahqf0BJOt34JKvsk1HSI r0 = new RequestDelegate(tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, z3, tLRPC$Message) {
                public final /* synthetic */ TLObject f$1;
                public final /* synthetic */ Object f$2;
                public final /* synthetic */ MessageObject f$3;
                public final /* synthetic */ String f$4;
                public final /* synthetic */ SendMessagesHelper.DelayedMessage f$5;
                public final /* synthetic */ boolean f$6;
                public final /* synthetic */ SendMessagesHelper.DelayedMessage f$7;
                public final /* synthetic */ boolean f$8;
                public final /* synthetic */ TLRPC$Message f$9;

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
                    SendMessagesHelper.this.lambda$performSendMessageRequest$59$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tLRPC$TL_error);
                }
            };
            tLRPC$Message.reqId = connectionsManager.sendRequest(tLObject2, (RequestDelegate) r14, (QuickAckDelegate) new QuickAckDelegate(tLRPC$Message) {
                public final /* synthetic */ TLRPC$Message f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$performSendMessageRequest$61$SendMessagesHelper(this.f$1);
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

    public /* synthetic */ void lambda$performSendMessageRequest$59$SendMessagesHelper(TLObject tLObject, Object obj, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, boolean z2, TLRPC$Message tLRPC$Message, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        TLObject tLObject3 = tLObject;
        Object obj2 = obj;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        if (tLRPC$TL_error2 != null && (((tLObject3 instanceof TLRPC$TL_messages_sendMedia) || (tLObject3 instanceof TLRPC$TL_messages_editMessage)) && FileRefController.isFileRefError(tLRPC$TL_error2.text))) {
            if (obj2 != null) {
                getFileRefController().requestReference(obj, tLObject3, messageObject, str, delayedMessage, Boolean.valueOf(z), delayedMessage2, Boolean.valueOf(z2));
                return;
            } else if (delayedMessage2 != null) {
                AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, z2, tLObject, delayedMessage2) {
                    public final /* synthetic */ TLRPC$Message f$1;
                    public final /* synthetic */ boolean f$2;
                    public final /* synthetic */ TLObject f$3;
                    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        SendMessagesHelper.this.lambda$null$46$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
                return;
            }
        }
        if (tLObject3 instanceof TLRPC$TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, z2, tLObject) {
                public final /* synthetic */ TLRPC$TL_error f$1;
                public final /* synthetic */ TLRPC$Message f$2;
                public final /* synthetic */ TLObject f$3;
                public final /* synthetic */ MessageObject f$4;
                public final /* synthetic */ String f$5;
                public final /* synthetic */ boolean f$6;
                public final /* synthetic */ TLObject f$7;

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
                    SendMessagesHelper.this.lambda$null$49$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(z2, tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, tLObject) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ TLRPC$TL_error f$2;
                public final /* synthetic */ TLRPC$Message f$3;
                public final /* synthetic */ TLObject f$4;
                public final /* synthetic */ MessageObject f$5;
                public final /* synthetic */ String f$6;
                public final /* synthetic */ TLObject f$7;

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
                    SendMessagesHelper.this.lambda$null$58$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$46$SendMessagesHelper(TLRPC$Message tLRPC$Message, boolean z, TLObject tLObject, DelayedMessage delayedMessage) {
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

    public /* synthetic */ void lambda$null$49$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, TLRPC$Message tLRPC$Message, TLObject tLObject, MessageObject messageObject, String str, boolean z, TLObject tLObject2) {
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
                public final /* synthetic */ TLRPC$Updates f$1;
                public final /* synthetic */ TLRPC$Message f$2;
                public final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$48$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
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

    public /* synthetic */ void lambda$null$48$SendMessagesHelper(TLRPC$Updates tLRPC$Updates, TLRPC$Message tLRPC$Message, boolean z) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, z) {
            public final /* synthetic */ TLRPC$Message f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$47$SendMessagesHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$47$SendMessagesHelper(TLRPC$Message tLRPC$Message, boolean z) {
        processSentMessage(tLRPC$Message.id);
        removeFromSendingMessages(tLRPC$Message.id, z);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:71:0x01b7, code lost:
        r12 = r3;
        r2 = null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0277  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x028a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$58$SendMessagesHelper(boolean r28, org.telegram.tgnet.TLRPC$TL_error r29, org.telegram.tgnet.TLRPC$Message r30, org.telegram.tgnet.TLObject r31, org.telegram.messenger.MessageObject r32, java.lang.String r33, org.telegram.tgnet.TLObject r34) {
        /*
            r27 = this;
            r8 = r27
            r9 = r28
            r0 = r29
            r10 = r30
            r1 = r31
            r12 = 1
            r13 = 0
            if (r0 != 0) goto L_0x033d
            int r6 = r10.id
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.lang.String r14 = r10.attachPath
            int r0 = r10.date
            r3 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 != r3) goto L_0x0020
            r0 = 1
            goto L_0x0021
        L_0x0020:
            r0 = 0
        L_0x0021:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateShortSentMessage
            r16 = 3
            if (r4 == 0) goto L_0x00d0
            r5 = r1
            org.telegram.tgnet.TLRPC$TL_updateShortSentMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateShortSentMessage) r5
            r2 = 0
            int r3 = r5.id
            r4 = 0
            r17 = 0
            r0 = r27
            r1 = r32
            r11 = r5
            r5 = r17
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r0 = r32.getMediaExistanceFlags()
            int r1 = r11.id
            r10.id = r1
            r10.local_id = r1
            int r1 = r11.date
            r10.date = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r11.entities
            r10.entities = r1
            boolean r1 = r11.out
            r10.out = r1
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r11.media
            if (r1 == 0) goto L_0x005f
            r10.media = r1
            int r1 = r10.flags
            r1 = r1 | 512(0x200, float:7.175E-43)
            r10.flags = r1
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r30)
        L_0x005f:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r11.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0071
            java.lang.String r1 = r11.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0071
            java.lang.String r1 = r11.message
            r10.message = r1
        L_0x0071:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r10.entities
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x007f
            int r1 = r10.flags
            r1 = r1 | 128(0x80, float:1.794E-43)
            r10.flags = r1
        L_0x007f:
            org.telegram.messenger.MessagesController r1 = r27.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            long r2 = r10.dialog_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x00b2
            org.telegram.messenger.MessagesStorage r1 = r27.getMessagesStorage()
            boolean r2 = r10.out
            long r3 = r10.dialog_id
            int r1 = r1.getDialogReadMax(r2, r3)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.messenger.MessagesController r2 = r27.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r2 = r2.dialogs_read_outbox_max
            long r3 = r10.dialog_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r2.put(r3, r1)
        L_0x00b2:
            int r1 = r1.intValue()
            int r2 = r10.id
            if (r1 >= r2) goto L_0x00bc
            r1 = 1
            goto L_0x00bd
        L_0x00bc:
            r1 = 0
        L_0x00bd:
            r10.unread = r1
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$JaSbOqonYDaD5yhui0mxWRaMWd4 r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$JaSbOqonYDaD5yhui0mxWRaMWd4
            r2.<init>(r11)
            r1.postRunnable(r2)
            r7.add(r10)
            r11 = r0
            r12 = 0
            goto L_0x0270
        L_0x00d0:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$Updates
            if (r4 == 0) goto L_0x026e
            r11 = r1
            org.telegram.tgnet.TLRPC$Updates r11 = (org.telegram.tgnet.TLRPC$Updates) r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r11.updates
            r4 = 0
        L_0x00da:
            int r5 = r1.size()
            if (r4 >= r5) goto L_0x01c2
            java.lang.Object r5 = r1.get(r4)
            org.telegram.tgnet.TLRPC$Update r5 = (org.telegram.tgnet.TLRPC$Update) r5
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewMessage
            if (r3 == 0) goto L_0x0100
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewMessage) r5
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$cuujM4oLXaow5vi3zYSe9gSFFog r15 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$cuujM4oLXaow5vi3zYSe9gSFFog
            r15.<init>(r5)
            r2.postRunnable(r15)
            r1.remove(r4)
            goto L_0x01b7
        L_0x0100:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage
            if (r2 == 0) goto L_0x019b
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage) r5
            int r2 = org.telegram.messenger.MessagesController.getUpdateChannelId(r5)
            org.telegram.messenger.MessagesController r15 = r27.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r15.getChat(r2)
            if (r2 == 0) goto L_0x011c
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x0174
        L_0x011c:
            org.telegram.tgnet.TLRPC$Message r2 = r5.message
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r2.reply_to
            if (r2 == 0) goto L_0x0174
            int r15 = r2.reply_to_top_id
            if (r15 != 0) goto L_0x012a
            int r2 = r2.reply_to_msg_id
            if (r2 == 0) goto L_0x0174
        L_0x012a:
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            org.telegram.tgnet.TLRPC$Message r15 = r5.message
            r31 = r4
            long r3 = org.telegram.messenger.MessageObject.getDialogId(r15)
            int r4 = (int) r3
            java.lang.Object r3 = r2.get(r4)
            android.util.SparseArray r3 = (android.util.SparseArray) r3
            if (r3 != 0) goto L_0x0148
            android.util.SparseArray r3 = new android.util.SparseArray
            r3.<init>()
            r2.put(r4, r3)
        L_0x0148:
            org.telegram.tgnet.TLRPC$Message r4 = r5.message
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = r4.reply_to
            int r15 = r4.reply_to_top_id
            if (r15 == 0) goto L_0x0151
            goto L_0x0153
        L_0x0151:
            int r15 = r4.reply_to_msg_id
        L_0x0153:
            java.lang.Object r4 = r3.get(r15)
            org.telegram.tgnet.TLRPC$TL_messageReplies r4 = (org.telegram.tgnet.TLRPC$TL_messageReplies) r4
            if (r4 != 0) goto L_0x0163
            org.telegram.tgnet.TLRPC$TL_messageReplies r4 = new org.telegram.tgnet.TLRPC$TL_messageReplies
            r4.<init>()
            r3.put(r15, r4)
        L_0x0163:
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id
            if (r3 == 0) goto L_0x016e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r15 = r4.recent_repliers
            r15.add(r13, r3)
        L_0x016e:
            int r3 = r4.replies
            int r3 = r3 + r12
            r4.replies = r3
            goto L_0x0177
        L_0x0174:
            r31 = r4
            r2 = 0
        L_0x0177:
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            int r4 = r10.flags
            r15 = -2147483648(0xfffffffvar_, float:-0.0)
            r4 = r4 & r15
            if (r4 == 0) goto L_0x018a
            org.telegram.tgnet.TLRPC$Message r4 = r5.message
            int r12 = r4.flags
            r12 = r12 | r15
            r4.flags = r12
        L_0x018a:
            org.telegram.messenger.DispatchQueue r4 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$fkA8U0rxzUVs0fMKfQvar_QPYBNg r12 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$fkA8U0rxzUVs0fMKfQvar_QPYBNg
            r12.<init>(r5)
            r4.postRunnable(r12)
            r4 = r31
            r1.remove(r4)
            r12 = r3
            goto L_0x01c4
        L_0x019b:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x01ba
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage) r5
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            int r2 = r10.flags
            r12 = -2147483648(0xfffffffvar_, float:-0.0)
            r2 = r2 & r12
            if (r2 == 0) goto L_0x01b4
            org.telegram.tgnet.TLRPC$Message r2 = r5.message
            int r5 = r2.flags
            r5 = r5 | r12
            r2.flags = r5
        L_0x01b4:
            r1.remove(r4)
        L_0x01b7:
            r12 = r3
            r2 = 0
            goto L_0x01c4
        L_0x01ba:
            int r4 = r4 + 1
            r3 = 2147483646(0x7ffffffe, float:NaN)
            r12 = 1
            goto L_0x00da
        L_0x01c2:
            r2 = 0
            r12 = 0
        L_0x01c4:
            if (r2 == 0) goto L_0x01f0
            org.telegram.messenger.MessagesStorage r18 = r27.getMessagesStorage()
            r19 = 0
            r20 = 0
            r22 = 1
            r23 = 1
            r21 = r2
            r18.putChannelViews(r19, r20, r21, r22, r23)
            org.telegram.messenger.NotificationCenter r1 = r27.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.didUpdateMessagesViews
            r4 = 4
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r4 = 0
            r5[r13] = r4
            r15 = 1
            r5[r15] = r4
            r4 = 2
            r5[r4] = r2
            java.lang.Boolean r2 = java.lang.Boolean.TRUE
            r5[r16] = r2
            r1.postNotificationName(r3, r5)
        L_0x01f0:
            if (r12 == 0) goto L_0x025d
            org.telegram.messenger.MessageObject.getDialogId(r12)
            if (r0 == 0) goto L_0x0200
            int r0 = r12.date
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 == r1) goto L_0x0200
            r15 = 0
            goto L_0x0201
        L_0x0200:
            r15 = r9
        L_0x0201:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r12)
            if (r15 != 0) goto L_0x0246
            org.telegram.messenger.MessagesController r0 = r27.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r0.dialogs_read_outbox_max
            long r1 = r12.dialog_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0239
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            boolean r1 = r12.out
            long r2 = r12.dialog_id
            int r0 = r0.getDialogReadMax(r1, r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.messenger.MessagesController r1 = r27.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            long r2 = r12.dialog_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r1.put(r2, r0)
        L_0x0239:
            int r0 = r0.intValue()
            int r1 = r12.id
            if (r0 >= r1) goto L_0x0243
            r0 = 1
            goto L_0x0244
        L_0x0243:
            r0 = 0
        L_0x0244:
            r12.unread = r0
        L_0x0246:
            int r3 = r12.id
            r5 = 0
            r0 = r27
            r1 = r32
            r2 = r12
            r4 = r33
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r0 = r32.getMediaExistanceFlags()
            int r1 = r12.id
            r10.id = r1
            r1 = 0
            goto L_0x0260
        L_0x025d:
            r15 = r9
            r0 = 0
            r1 = 1
        L_0x0260:
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$P6BdwuVvMQasrb5WWSiqSrfOHfY r3 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$P6BdwuVvMQasrb5WWSiqSrfOHfY
            r3.<init>(r11)
            r2.postRunnable(r3)
            r11 = r0
            r12 = r15
            r15 = r1
            goto L_0x0271
        L_0x026e:
            r12 = r9
            r11 = 0
        L_0x0270:
            r15 = 0
        L_0x0271:
            boolean r0 = org.telegram.messenger.MessageObject.isLiveLocationMessage(r30)
            if (r0 == 0) goto L_0x0288
            org.telegram.messenger.LocationController r0 = r27.getLocationController()
            long r1 = r10.dialog_id
            int r3 = r10.id
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r10.media
            int r4 = r4.period
            r5 = r30
            r0.addSharingLocation(r1, r3, r4, r5)
        L_0x0288:
            if (r15 != 0) goto L_0x0348
            org.telegram.messenger.StatsController r0 = r27.getStatsController()
            int r1 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r2 = 1
            r0.incrementSentItemsCount(r1, r2, r2)
            r10.send_state = r13
            if (r9 == 0) goto L_0x02e1
            if (r12 != 0) goto L_0x02e1
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            r0.add(r1)
            org.telegram.messenger.MessagesController r18 = r27.getMessagesController()
            r20 = 0
            r21 = 0
            long r1 = r10.dialog_id
            org.telegram.tgnet.TLRPC$Peer r3 = r10.peer_id
            int r3 = r3.channel_id
            r25 = 0
            r26 = 1
            r19 = r0
            r22 = r1
            r24 = r3
            r18.deleteMessages(r19, r20, r21, r22, r24, r25, r26)
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r11 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$bMw-6mTvSOr7WHC9DoiTHdi8gEA r12 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$bMw-6mTvSOr7WHC9DoiTHdi8gEA
            r0 = r12
            r1 = r27
            r2 = r7
            r3 = r32
            r4 = r30
            r5 = r6
            r6 = r28
            r7 = r14
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r11.postRunnable(r12)
            goto L_0x0348
        L_0x02e1:
            org.telegram.messenger.NotificationCenter r0 = r27.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            r2 = 7
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r6)
            r2[r13] = r3
            int r3 = r10.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 1
            r2[r4] = r3
            r3 = 2
            r2[r3] = r10
            long r3 = r10.dialog_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r2[r16] = r3
            r3 = 0
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r4 = 4
            r2[r4] = r3
            r3 = 5
            java.lang.Integer r4 = java.lang.Integer.valueOf(r11)
            r2[r3] = r4
            r3 = 6
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r28)
            r2[r3] = r4
            r0.postNotificationName(r1, r2)
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r12 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$PvjDXHuWRWLdGMwZvrRqUgNdII0 r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$PvjDXHuWRWLdGMwZvrRqUgNdII0
            r0 = r5
            r1 = r27
            r2 = r30
            r3 = r6
            r4 = r28
            r6 = r5
            r5 = r7
            r7 = r6
            r6 = r11
            r11 = r7
            r7 = r14
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r12.postRunnable(r11)
            goto L_0x0348
        L_0x033d:
            int r1 = r8.currentAccount
            java.lang.Object[] r2 = new java.lang.Object[r13]
            r3 = r34
            r4 = 0
            org.telegram.ui.Components.AlertsCreator.processError(r1, r0, r4, r3, r2)
            r15 = 1
        L_0x0348:
            if (r15 == 0) goto L_0x0389
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            r0.markMessageAsSendError(r10, r9)
            r0 = 2
            r10.send_state = r0
            org.telegram.messenger.NotificationCenter r0 = r27.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.messageSendError
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            int r3 = r10.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r13] = r3
            r0.postNotificationName(r1, r2)
            int r0 = r10.id
            r8.processSentMessage(r0)
            boolean r0 = org.telegram.messenger.MessageObject.isVideoMessage(r30)
            if (r0 != 0) goto L_0x037f
            boolean r0 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r30)
            if (r0 != 0) goto L_0x037f
            boolean r0 = org.telegram.messenger.MessageObject.isNewGifMessage(r30)
            if (r0 == 0) goto L_0x0384
        L_0x037f:
            java.lang.String r0 = r10.attachPath
            r8.stopVideoService(r0)
        L_0x0384:
            int r0 = r10.id
            r8.removeFromSendingMessages(r0, r9)
        L_0x0389:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$58$SendMessagesHelper(boolean, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, java.lang.String, org.telegram.tgnet.TLObject):void");
    }

    public /* synthetic */ void lambda$null$50$SendMessagesHelper(TLRPC$TL_updateShortSentMessage tLRPC$TL_updateShortSentMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateShortSentMessage.pts, tLRPC$TL_updateShortSentMessage.date, tLRPC$TL_updateShortSentMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$51$SendMessagesHelper(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$52$SendMessagesHelper(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.peer_id.channel_id);
    }

    public /* synthetic */ void lambda$null$53$SendMessagesHelper(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
    }

    public /* synthetic */ void lambda$null$55$SendMessagesHelper(ArrayList arrayList, MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, boolean z, String str) {
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new Runnable(messageObject, tLRPC$Message, i, z) {
            public final /* synthetic */ MessageObject f$1;
            public final /* synthetic */ TLRPC$Message f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$54$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(str);
            return;
        }
    }

    public /* synthetic */ void lambda$null$54$SendMessagesHelper(MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true, true));
        getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$57$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, Integer.valueOf(i), tLRPC$Message2.id, 0, false, tLRPC$Message2.peer_id.channel_id, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, i, i2, z) {
            public final /* synthetic */ TLRPC$Message f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$56$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(str);
        }
    }

    public /* synthetic */ void lambda$null$56$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), 0L, Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$61$SendMessagesHelper(TLRPC$Message tLRPC$Message) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, tLRPC$Message.id) {
            public final /* synthetic */ TLRPC$Message f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$null$60$SendMessagesHelper(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$60$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i) {
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
            if (r2 == 0) goto L_0x0123
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
            if (r10 == 0) goto L_0x0123
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r2 == 0) goto L_0x0123
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
            goto L_0x0118
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
            r10.append(r4)
            boolean r11 = r0.scheduled
            r10.append(r11)
            java.lang.String r10 = r10.toString()
        L_0x0118:
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            r11.replaceImageInCache(r2, r10, r5, r1)
        L_0x0123:
            if (r7 != 0) goto L_0x0126
            return
        L_0x0126:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            java.lang.String r6 = "sent_"
            java.lang.String r12 = ".jpg"
            r13 = 4
            r14 = 0
            r15 = 1
            if (r5 == 0) goto L_0x02d4
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            if (r5 == 0) goto L_0x02d4
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x02d4
            org.telegram.tgnet.TLRPC$Photo r3 = r5.photo
            if (r3 == 0) goto L_0x02d4
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x0172
            boolean r0 = r0.scheduled
            if (r0 != 0) goto L_0x0172
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            org.telegram.tgnet.TLRPC$Peer r5 = r7.peer_id
            int r5 = r5.channel_id
            r3.append(r5)
            r3.append(r4)
            int r5 = r7.id
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            r0.putSentFile(r8, r2, r14, r3)
        L_0x0172:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            int r0 = r0.size()
            if (r0 != r15) goto L_0x019e
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            java.lang.Object r0 = r0.get(r14)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.location
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r0 == 0) goto L_0x019e
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            r0.sizes = r1
            goto L_0x02ba
        L_0x019e:
            r0 = 0
        L_0x019f:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x02ba
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2
            if (r2 == 0) goto L_0x02b3
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            if (r3 == 0) goto L_0x02b3
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r3 != 0) goto L_0x02b3
            java.lang.String r3 = r2.type
            if (r3 != 0) goto L_0x01c7
            goto L_0x02b3
        L_0x01c7:
            r3 = 0
        L_0x01c8:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x02b3
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            java.lang.Object r5 = r5.get(r3)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5
            if (r5 == 0) goto L_0x02ac
            org.telegram.tgnet.TLRPC$FileLocation r6 = r5.location
            if (r6 == 0) goto L_0x02ac
            java.lang.String r8 = r5.type
            if (r8 != 0) goto L_0x01ec
            goto L_0x02ac
        L_0x01ec:
            long r14 = r6.volume_id
            int r6 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x01fa
            java.lang.String r6 = r2.type
            boolean r6 = r6.equals(r8)
            if (r6 != 0) goto L_0x0206
        L_0x01fa:
            int r6 = r2.w
            int r8 = r5.w
            if (r6 != r8) goto L_0x02ac
            int r6 = r2.h
            int r8 = r5.h
            if (r6 != r8) goto L_0x02ac
        L_0x0206:
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
            if (r8 == 0) goto L_0x0241
            goto L_0x02b3
        L_0x0241:
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
            if (r15 != 0) goto L_0x0279
            org.telegram.tgnet.TLRPC$Photo r14 = r14.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r14.sizes
            int r14 = r14.size()
            r15 = 1
            if (r14 == r15) goto L_0x0274
            int r14 = r2.w
            r15 = 90
            if (r14 > r15) goto L_0x0274
            int r14 = r2.h
            if (r14 <= r15) goto L_0x0279
        L_0x0274:
            java.io.File r14 = org.telegram.messenger.FileLoader.getPathToAttach(r2)
            goto L_0x0291
        L_0x0279:
            java.io.File r14 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r6)
            r13.append(r12)
            java.lang.String r13 = r13.toString()
            r14.<init>(r15, r13)
        L_0x0291:
            r8.renameTo(r14)
            org.telegram.messenger.ImageLoader r8 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$MessageMedia r13 = r7.media
            org.telegram.tgnet.TLRPC$Photo r13 = r13.photo
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r2, (org.telegram.tgnet.TLRPC$Photo) r13)
            r8.replaceImageInCache(r3, r6, r13, r1)
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            r5.location = r3
            int r2 = r2.size
            r5.size = r2
            goto L_0x02b3
        L_0x02ac:
            int r3 = r3 + 1
            r13 = 4
            r14 = 0
            r15 = 1
            goto L_0x01c8
        L_0x02b3:
            int r0 = r0 + 1
            r13 = 4
            r14 = 0
            r15 = 1
            goto L_0x019f
        L_0x02ba:
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
            goto L_0x0612
        L_0x02d4:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r3 == 0) goto L_0x05c6
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            if (r3 == 0) goto L_0x05c6
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r9.media
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x05c6
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            if (r3 == 0) goto L_0x05c6
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x0389
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            if (r2 == 0) goto L_0x0302
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r3 = r2.mediaEntities
            if (r3 != 0) goto L_0x0389
            java.lang.String r2 = r2.paintPath
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x0389
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            org.telegram.messenger.MediaController$CropState r2 = r2.cropState
            if (r2 != 0) goto L_0x0389
        L_0x0302:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r18)
            if (r2 != 0) goto L_0x030e
            boolean r3 = org.telegram.messenger.MessageObject.isGifMessage(r18)
            if (r3 == 0) goto L_0x0352
        L_0x030e:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r5)
            if (r3 != r5) goto L_0x0352
            boolean r3 = r0.scheduled
            if (r3 != 0) goto L_0x034b
            org.telegram.messenger.MessagesStorage r3 = r16.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            r13 = 2
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r6)
            org.telegram.tgnet.TLRPC$Peer r6 = r7.peer_id
            int r6 = r6.channel_id
            r14.append(r6)
            r14.append(r4)
            int r6 = r7.id
            r14.append(r6)
            java.lang.String r6 = r14.toString()
            r3.putSentFile(r8, r5, r13, r6)
        L_0x034b:
            if (r2 == 0) goto L_0x0389
            java.lang.String r2 = r9.attachPath
            r7.attachPath = r2
            goto L_0x0389
        L_0x0352:
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r18)
            if (r2 != 0) goto L_0x0389
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r18)
            if (r2 != 0) goto L_0x0389
            boolean r2 = r0.scheduled
            if (r2 != 0) goto L_0x0389
            org.telegram.messenger.MessagesStorage r2 = r16.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r6)
            org.telegram.tgnet.TLRPC$Peer r6 = r7.peer_id
            int r6 = r6.channel_id
            r5.append(r6)
            r5.append(r4)
            int r6 = r7.id
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 1
            r2.putSentFile(r8, r3, r6, r5)
        L_0x0389:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r3 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r3)
            if (r2 == 0) goto L_0x043f
            org.telegram.tgnet.TLRPC$FileLocation r5 = r2.location
            if (r5 == 0) goto L_0x043f
            long r5 = r5.volume_id
            int r13 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r13 != 0) goto L_0x043f
            if (r3 == 0) goto L_0x043f
            org.telegram.tgnet.TLRPC$FileLocation r5 = r3.location
            if (r5 == 0) goto L_0x043f
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x043f
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x043f
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
            if (r6 != 0) goto L_0x0468
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
            org.telegram.messenger.ImageLocation r10 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Document) r10)
            r6.replaceImageInCache(r5, r4, r10, r1)
            org.telegram.tgnet.TLRPC$FileLocation r1 = r3.location
            r2.location = r1
            int r1 = r3.size
            r2.size = r1
            goto L_0x0468
        L_0x043f:
            if (r2 == 0) goto L_0x044e
            boolean r1 = org.telegram.messenger.MessageObject.isStickerMessage(r18)
            if (r1 == 0) goto L_0x044e
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            if (r1 == 0) goto L_0x044e
            r3.location = r1
            goto L_0x0468
        L_0x044e:
            if (r2 == 0) goto L_0x045c
            if (r2 == 0) goto L_0x0458
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r1 != 0) goto L_0x045c
        L_0x0458:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r1 == 0) goto L_0x0468
        L_0x045c:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r1.thumbs = r2
        L_0x0468:
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
        L_0x047d:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x049f
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r3 == 0) goto L_0x049c
            byte[] r3 = r2.waveform
            goto L_0x04a0
        L_0x049c:
            int r1 = r1 + 1
            goto L_0x047d
        L_0x049f:
            r3 = 0
        L_0x04a0:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            r1.attributes = r2
            if (r3 == 0) goto L_0x04d6
            r1 = 0
        L_0x04af:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x04d6
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r4 == 0) goto L_0x04d3
            r2.waveform = r3
            int r4 = r2.flags
            r5 = 4
            r4 = r4 | r5
            r2.flags = r4
        L_0x04d3:
            int r1 = r1 + 1
            goto L_0x04af
        L_0x04d6:
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
            if (r1 != 0) goto L_0x0544
            boolean r1 = org.telegram.messenger.MessageObject.isOut(r18)
            if (r1 == 0) goto L_0x0544
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r1)
            if (r1 == 0) goto L_0x051e
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isDocumentHasAttachedStickers(r1)
            if (r1 == 0) goto L_0x050d
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            boolean r1 = r1.saveGifsWithStickers
            goto L_0x050e
        L_0x050d:
            r1 = 1
        L_0x050e:
            if (r1 == 0) goto L_0x0544
            org.telegram.messenger.MediaDataController r1 = r16.getMediaDataController()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            int r3 = r7.date
            r1.addRecentGif(r2, r3)
            goto L_0x0544
        L_0x051e:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r1)
            if (r1 != 0) goto L_0x0533
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            r2 = 1
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r2)
            if (r1 == 0) goto L_0x0544
        L_0x0533:
            org.telegram.messenger.MediaDataController r1 = r16.getMediaDataController()
            r2 = 0
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            int r5 = r7.date
            r6 = 0
            r3 = r18
            r1.addRecentSticker(r2, r3, r4, r5, r6)
        L_0x0544:
            java.lang.String r1 = r9.attachPath
            if (r1 == 0) goto L_0x05bd
            r2 = 4
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r2)
            java.lang.String r2 = r2.getAbsolutePath()
            boolean r1 = r1.startsWith(r2)
            if (r1 == 0) goto L_0x05bd
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r9.attachPath
            r1.<init>(r2)
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            int r2 = r2.ttl_seconds
            if (r2 == 0) goto L_0x0568
            r2 = 1
            goto L_0x0569
        L_0x0568:
            r2 = 0
        L_0x0569:
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r2)
            boolean r3 = r1.renameTo(r2)
            if (r3 != 0) goto L_0x058d
            boolean r1 = r1.exists()
            if (r1 == 0) goto L_0x057e
            java.lang.String r1 = r9.attachPath
            r7.attachPath = r1
            goto L_0x0581
        L_0x057e:
            r1 = 0
            r0.attachPathExists = r1
        L_0x0581:
            boolean r1 = r2.exists()
            r0.mediaExists = r1
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x0612
        L_0x058d:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoMessage(r18)
            if (r1 == 0) goto L_0x0598
            r1 = 1
            r0.attachPathExists = r1
            goto L_0x0612
        L_0x0598:
            boolean r1 = r0.attachPathExists
            r0.mediaExists = r1
            r1 = 0
            r0.attachPathExists = r1
            java.lang.String r0 = ""
            r9.attachPath = r0
            if (r8 == 0) goto L_0x0612
            java.lang.String r0 = "http"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x0612
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            java.lang.String r1 = r2.toString()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            r0.addRecentLocalFile(r8, r1, r2)
            goto L_0x0612
        L_0x05bd:
            java.lang.String r0 = r9.attachPath
            r7.attachPath = r0
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x0612
        L_0x05c6:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r7.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r1 == 0) goto L_0x05d5
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r1 == 0) goto L_0x05d5
            r9.media = r0
            goto L_0x0612
        L_0x05d5:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r7.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r1 == 0) goto L_0x05de
            r9.media = r0
            goto L_0x0612
        L_0x05de:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r1 == 0) goto L_0x05f1
            org.telegram.tgnet.TLRPC$GeoPoint r0 = r0.geo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$GeoPoint r1 = r1.geo
            double r2 = r1.lat
            r0.lat = r2
            double r1 = r1._long
            r0._long = r1
            goto L_0x0612
        L_0x05f1:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x060c
            r9.media = r0
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0612
            java.lang.String r0 = r7.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0612
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r7.entities
            r9.entities = r0
            java.lang.String r0 = r7.message
            r9.message = r0
            goto L_0x0612
        L_0x060c:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r1 == 0) goto L_0x0612
            r9.media = r0
        L_0x0612:
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
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ ArrayList f$2;
            public final /* synthetic */ ArrayList f$3;
            public final /* synthetic */ ArrayList f$4;
            public final /* synthetic */ ArrayList f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                SendMessagesHelper.this.lambda$processUnsentMessages$62$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public /* synthetic */ void lambda$processUnsentMessages$62$SendMessagesHelper(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (int i = 0; i < arrayList4.size(); i++) {
            retrySendMessage(new MessageObject(this.currentAccount, (TLRPC$Message) arrayList4.get(i), false, true), true);
        }
        if (arrayList5 != null) {
            for (int i2 = 0; i2 < arrayList5.size(); i2++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC$Message) arrayList5.get(i2), false, true);
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
        TLRPC$PhotoSize scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(loadBitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true, 80, false, 101, 101);
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
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004d, code lost:
        if (r3 == false) goto L_0x0051;
     */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0154 A[SYNTHETIC, Splitter:B:104:0x0154] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0169 A[SYNTHETIC, Splitter:B:113:0x0169] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0173  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01c8  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0207  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x020b A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02c0  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x0443  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x044b  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0451  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0459  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0462  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x046b  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00ab  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r27, java.lang.String r28, java.lang.String r29, android.net.Uri r30, java.lang.String r31, long r32, org.telegram.messenger.MessageObject r34, org.telegram.messenger.MessageObject r35, java.lang.CharSequence r36, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r37, org.telegram.messenger.MessageObject r38, boolean r39, boolean r40, int r41) {
        /*
            r0 = r28
            r1 = r29
            r2 = r30
            r3 = r31
            r4 = 0
            if (r0 == 0) goto L_0x0011
            int r5 = r28.length()
            if (r5 != 0) goto L_0x0014
        L_0x0011:
            if (r2 != 0) goto L_0x0014
            return r4
        L_0x0014:
            if (r2 == 0) goto L_0x001d
            boolean r5 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r30)
            if (r5 == 0) goto L_0x001d
            return r4
        L_0x001d:
            if (r0 == 0) goto L_0x002f
            java.io.File r5 = new java.io.File
            r5.<init>(r0)
            android.net.Uri r5 = android.net.Uri.fromFile(r5)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r5)
            if (r5 == 0) goto L_0x002f
            return r4
        L_0x002f:
            android.webkit.MimeTypeMap r5 = android.webkit.MimeTypeMap.getSingleton()
            r15 = 1
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
            if (r3 == 0) goto L_0x048c
            long r7 = r2.length()
            r11 = 0
            int r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x0069
            goto L_0x048c
        L_0x0069:
            r9 = r32
            int r3 = (int) r9
            if (r3 != 0) goto L_0x0070
            r3 = 1
            goto L_0x0071
        L_0x0070:
            r3 = 0
        L_0x0071:
            java.lang.String r14 = r2.getName()
            r8 = -1
            java.lang.String r7 = ""
            if (r0 == 0) goto L_0x007d
        L_0x007a:
            r16 = r0
            goto L_0x008d
        L_0x007d:
            r0 = 46
            int r0 = r13.lastIndexOf(r0)
            if (r0 == r8) goto L_0x008b
            int r0 = r0 + r15
            java.lang.String r0 = r13.substring(r0)
            goto L_0x007a
        L_0x008b:
            r16 = r7
        L_0x008d:
            java.lang.String r6 = r16.toLowerCase()
            java.lang.String r10 = "mp3"
            boolean r0 = r6.equals(r10)
            java.lang.String r9 = "flac"
            r28 = r10
            java.lang.String r10 = "opus"
            java.lang.String r4 = "m4a"
            java.lang.String r11 = "ogg"
            if (r0 != 0) goto L_0x0173
            boolean r0 = r6.equals(r4)
            if (r0 == 0) goto L_0x00ab
            goto L_0x0173
        L_0x00ab:
            boolean r0 = r6.equals(r10)
            if (r0 != 0) goto L_0x00ca
            boolean r0 = r6.equals(r11)
            if (r0 != 0) goto L_0x00ca
            boolean r0 = r6.equals(r9)
            if (r0 == 0) goto L_0x00be
            goto L_0x00ca
        L_0x00be:
            r20 = r9
            r18 = r13
            r0 = 0
            r8 = 0
            r9 = 0
            r12 = 0
            r15 = 0
            goto L_0x019b
        L_0x00ca:
            android.media.MediaMetadataRetriever r8 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0146, all -> 0x0142 }
            r8.<init>()     // Catch:{ Exception -> 0x0146, all -> 0x0142 }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x013c }
            r8.setDataSource(r0)     // Catch:{ Exception -> 0x013c }
            r0 = 9
            java.lang.String r0 = r8.extractMetadata(r0)     // Catch:{ Exception -> 0x013c }
            if (r0 == 0) goto L_0x010c
            r18 = r13
            long r12 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x0108 }
            float r0 = (float) r12     // Catch:{ Exception -> 0x0108 }
            r12 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r12
            double r12 = (double) r0     // Catch:{ Exception -> 0x0108 }
            double r12 = java.lang.Math.ceil(r12)     // Catch:{ Exception -> 0x0108 }
            int r12 = (int) r12
            r0 = 7
            java.lang.String r13 = r8.extractMetadata(r0)     // Catch:{ Exception -> 0x0101 }
            r15 = 2
            java.lang.String r0 = r8.extractMetadata(r15)     // Catch:{ Exception -> 0x00fb }
            r15 = r12
            r12 = r0
            goto L_0x0111
        L_0x00fb:
            r0 = move-exception
            r20 = r9
            r15 = r12
            r12 = 0
            goto L_0x014f
        L_0x0101:
            r0 = move-exception
            r20 = r9
            r15 = r12
            r12 = 0
            r13 = 0
            goto L_0x014f
        L_0x0108:
            r0 = move-exception
            r20 = r9
            goto L_0x014c
        L_0x010c:
            r18 = r13
            r12 = 0
            r13 = 0
            r15 = 0
        L_0x0111:
            if (r38 != 0) goto L_0x012c
            boolean r0 = r6.equals(r11)     // Catch:{ Exception -> 0x0128 }
            if (r0 == 0) goto L_0x012c
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x0128 }
            int r0 = org.telegram.messenger.MediaController.isOpusFile(r0)     // Catch:{ Exception -> 0x0128 }
            r20 = r9
            r9 = 1
            if (r0 != r9) goto L_0x012e
            r9 = 1
            goto L_0x012f
        L_0x0128:
            r0 = move-exception
            r20 = r9
            goto L_0x014f
        L_0x012c:
            r20 = r9
        L_0x012e:
            r9 = 0
        L_0x012f:
            r8.release()     // Catch:{ Exception -> 0x0133 }
            goto L_0x0138
        L_0x0133:
            r0 = move-exception
            r8 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0138:
            r8 = r9
            r0 = r13
            r9 = r15
            goto L_0x0160
        L_0x013c:
            r0 = move-exception
            r20 = r9
            r18 = r13
            goto L_0x014c
        L_0x0142:
            r0 = move-exception
            r1 = r0
            r6 = 0
            goto L_0x0167
        L_0x0146:
            r0 = move-exception
            r20 = r9
            r18 = r13
            r8 = 0
        L_0x014c:
            r12 = 0
            r13 = 0
            r15 = 0
        L_0x014f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0164 }
            if (r8 == 0) goto L_0x015d
            r8.release()     // Catch:{ Exception -> 0x0158 }
            goto L_0x015d
        L_0x0158:
            r0 = move-exception
            r8 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x015d:
            r0 = r13
            r9 = r15
            r8 = 0
        L_0x0160:
            r15 = r12
            r12 = 0
            goto L_0x019b
        L_0x0164:
            r0 = move-exception
            r1 = r0
            r6 = r8
        L_0x0167:
            if (r6 == 0) goto L_0x0172
            r6.release()     // Catch:{ Exception -> 0x016d }
            goto L_0x0172
        L_0x016d:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0172:
            throw r1
        L_0x0173:
            r20 = r9
            r18 = r13
            org.telegram.messenger.audioinfo.AudioInfo r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r2)
            if (r0 == 0) goto L_0x0195
            long r8 = r0.getDuration()
            r12 = 0
            int r15 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r15 == 0) goto L_0x0197
            java.lang.String r15 = r0.getArtist()
            java.lang.String r0 = r0.getTitle()
            r21 = 1000(0x3e8, double:4.94E-321)
            long r8 = r8 / r21
            int r9 = (int) r8
            goto L_0x019a
        L_0x0195:
            r12 = 0
        L_0x0197:
            r0 = 0
            r9 = 0
            r15 = 0
        L_0x019a:
            r8 = 0
        L_0x019b:
            if (r9 == 0) goto L_0x01c4
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r12 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r12.<init>()
            r12.duration = r9
            r12.title = r0
            r12.performer = r15
            if (r0 != 0) goto L_0x01ac
            r12.title = r7
        L_0x01ac:
            int r0 = r12.flags
            r9 = 1
            r0 = r0 | r9
            r12.flags = r0
            java.lang.String r0 = r12.performer
            if (r0 != 0) goto L_0x01b8
            r12.performer = r7
        L_0x01b8:
            int r0 = r12.flags
            r13 = 2
            r0 = r0 | r13
            r12.flags = r0
            if (r8 == 0) goto L_0x01c2
            r12.voice = r9
        L_0x01c2:
            r0 = r12
            goto L_0x01c6
        L_0x01c4:
            r13 = 2
            r0 = 0
        L_0x01c6:
            if (r1 == 0) goto L_0x0207
            java.lang.String r8 = "attheme"
            boolean r8 = r1.endsWith(r8)
            if (r8 == 0) goto L_0x01d3
            r15 = r14
            r8 = 1
            goto L_0x0209
        L_0x01d3:
            if (r0 == 0) goto L_0x01ef
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r1)
            java.lang.String r1 = "audio"
            r8.append(r1)
            r15 = r14
            long r13 = r2.length()
            r8.append(r13)
            java.lang.String r1 = r8.toString()
            goto L_0x0208
        L_0x01ef:
            r15 = r14
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r1)
            r8.append(r7)
            long r12 = r2.length()
            r8.append(r12)
            java.lang.String r1 = r8.toString()
            goto L_0x0208
        L_0x0207:
            r15 = r14
        L_0x0208:
            r8 = 0
        L_0x0209:
            if (r8 != 0) goto L_0x02a9
            if (r3 != 0) goto L_0x02a9
            org.telegram.messenger.MessagesStorage r8 = r27.getMessagesStorage()
            if (r3 != 0) goto L_0x0215
            r9 = 1
            goto L_0x0216
        L_0x0215:
            r9 = 4
        L_0x0216:
            java.lang.Object[] r8 = r8.getSentFile(r1, r9)
            if (r8 == 0) goto L_0x022e
            r9 = 0
            r12 = r8[r9]
            boolean r12 = r12 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r12 == 0) goto L_0x022e
            r12 = r8[r9]
            r9 = r12
            org.telegram.tgnet.TLRPC$TL_document r9 = (org.telegram.tgnet.TLRPC$TL_document) r9
            r12 = 1
            r8 = r8[r12]
            java.lang.String r8 = (java.lang.String) r8
            goto L_0x0230
        L_0x022e:
            r8 = 0
            r9 = 0
        L_0x0230:
            if (r9 != 0) goto L_0x0279
            r14 = r18
            boolean r12 = r14.equals(r1)
            if (r12 != 0) goto L_0x0276
            if (r3 != 0) goto L_0x0276
            org.telegram.messenger.MessagesStorage r12 = r27.getMessagesStorage()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r14)
            r18 = r7
            r21 = r8
            long r7 = r2.length()
            r13.append(r7)
            java.lang.String r7 = r13.toString()
            if (r3 != 0) goto L_0x025b
            r8 = 1
            goto L_0x025c
        L_0x025b:
            r8 = 4
        L_0x025c:
            java.lang.Object[] r7 = r12.getSentFile(r7, r8)
            if (r7 == 0) goto L_0x027f
            r8 = 0
            r12 = r7[r8]
            boolean r12 = r12 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r12 == 0) goto L_0x027f
            r9 = r7[r8]
            org.telegram.tgnet.TLRPC$TL_document r9 = (org.telegram.tgnet.TLRPC$TL_document) r9
            r8 = 1
            r7 = r7[r8]
            r8 = r7
            java.lang.String r8 = (java.lang.String) r8
        L_0x0273:
            r21 = r8
            goto L_0x027f
        L_0x0276:
            r18 = r7
            goto L_0x0273
        L_0x0279:
            r21 = r8
            r14 = r18
            r18 = r7
        L_0x027f:
            r13 = r9
            r12 = 0
            r22 = 0
            r9 = r18
            r7 = r3
            r17 = -1
            r8 = r13
            r25 = r9
            r24 = r20
            r9 = r14
            r18 = r13
            r13 = r10
            r26 = r15
            r15 = r28
            r28 = r26
            r10 = r12
            r20 = r1
            r1 = r11
            r19 = r14
            r30 = r15
            r14 = 0
            r11 = r22
            ensureMediaThumbExists(r7, r8, r9, r10, r11)
            r7 = r21
            goto L_0x02be
        L_0x02a9:
            r30 = r28
            r25 = r7
            r13 = r10
            r28 = r15
            r19 = r18
            r24 = r20
            r14 = 0
            r17 = -1
            r20 = r1
            r1 = r11
            r7 = 0
            r18 = 0
        L_0x02be:
            if (r18 != 0) goto L_0x0443
            org.telegram.tgnet.TLRPC$TL_document r8 = new org.telegram.tgnet.TLRPC$TL_document
            r8.<init>()
            r8.id = r14
            org.telegram.tgnet.ConnectionsManager r9 = r27.getConnectionsManager()
            int r9 = r9.getCurrentTime()
            r8.date = r9
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r9 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r9.<init>()
            r10 = r28
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
            if (r0 == 0) goto L_0x02f4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r10 = r8.attributes
            r10.add(r0)
        L_0x02f4:
            int r0 = r16.length()
            java.lang.String r10 = "image/webp"
            java.lang.String r11 = "application/octet-stream"
            if (r0 == 0) goto L_0x0377
            int r0 = r6.hashCode()
            r12 = 5
            r14 = 3
            switch(r0) {
                case 106458: goto L_0x0337;
                case 108272: goto L_0x032d;
                case 109967: goto L_0x0325;
                case 3145576: goto L_0x031b;
                case 3418175: goto L_0x0313;
                case 3645340: goto L_0x0308;
                default: goto L_0x0307;
            }
        L_0x0307:
            goto L_0x033f
        L_0x0308:
            java.lang.String r0 = "webp"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x033f
            r4 = 0
            goto L_0x0340
        L_0x0313:
            boolean r0 = r6.equals(r13)
            if (r0 == 0) goto L_0x033f
            r4 = 1
            goto L_0x0340
        L_0x031b:
            r1 = r24
            boolean r0 = r6.equals(r1)
            if (r0 == 0) goto L_0x033f
            r4 = 5
            goto L_0x0340
        L_0x0325:
            boolean r0 = r6.equals(r1)
            if (r0 == 0) goto L_0x033f
            r4 = 4
            goto L_0x0340
        L_0x032d:
            r1 = r30
            boolean r0 = r6.equals(r1)
            if (r0 == 0) goto L_0x033f
            r4 = 2
            goto L_0x0340
        L_0x0337:
            boolean r0 = r6.equals(r4)
            if (r0 == 0) goto L_0x033f
            r4 = 3
            goto L_0x0340
        L_0x033f:
            r4 = -1
        L_0x0340:
            if (r4 == 0) goto L_0x0374
            r1 = 1
            if (r4 == r1) goto L_0x036f
            r1 = 2
            if (r4 == r1) goto L_0x036a
            if (r4 == r14) goto L_0x0365
            r0 = 4
            if (r4 == r0) goto L_0x0360
            if (r4 == r12) goto L_0x035b
            java.lang.String r0 = r5.getMimeTypeFromExtension(r6)
            if (r0 == 0) goto L_0x0358
            r8.mime_type = r0
            goto L_0x0379
        L_0x0358:
            r8.mime_type = r11
            goto L_0x0379
        L_0x035b:
            java.lang.String r0 = "audio/flac"
            r8.mime_type = r0
            goto L_0x0379
        L_0x0360:
            java.lang.String r0 = "audio/ogg"
            r8.mime_type = r0
            goto L_0x0379
        L_0x0365:
            java.lang.String r0 = "audio/m4a"
            r8.mime_type = r0
            goto L_0x0379
        L_0x036a:
            java.lang.String r0 = "audio/mpeg"
            r8.mime_type = r0
            goto L_0x0379
        L_0x036f:
            java.lang.String r0 = "audio/opus"
            r8.mime_type = r0
            goto L_0x0379
        L_0x0374:
            r8.mime_type = r10
            goto L_0x0379
        L_0x0377:
            r8.mime_type = r11
        L_0x0379:
            java.lang.String r0 = r8.mime_type
            java.lang.String r1 = "image/gif"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x03c6
            if (r38 == 0) goto L_0x038f
            long r0 = r38.getGroupIdForUse()
            r4 = 0
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x03c6
        L_0x038f:
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x03c2 }
            r1 = 1119092736(0x42b40000, float:90.0)
            r2 = 0
            r4 = 1
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r2, r1, r1, r4)     // Catch:{ Exception -> 0x03c2 }
            if (r0 == 0) goto L_0x03c6
            java.lang.String r2 = "animation.gif"
            r9.file_name = r2     // Catch:{ Exception -> 0x03c2 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r8.attributes     // Catch:{ Exception -> 0x03c2 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x03c2 }
            r4.<init>()     // Catch:{ Exception -> 0x03c2 }
            r2.add(r4)     // Catch:{ Exception -> 0x03c2 }
            r2 = 55
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r1, r1, r2, r3)     // Catch:{ Exception -> 0x03c2 }
            if (r1 == 0) goto L_0x03be
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r8.thumbs     // Catch:{ Exception -> 0x03c2 }
            r2.add(r1)     // Catch:{ Exception -> 0x03c2 }
            int r1 = r8.flags     // Catch:{ Exception -> 0x03c2 }
            r2 = 1
            r1 = r1 | r2
            r8.flags = r1     // Catch:{ Exception -> 0x03c2 }
        L_0x03be:
            r0.recycle()     // Catch:{ Exception -> 0x03c2 }
            goto L_0x03c6
        L_0x03c2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03c6:
            java.lang.String r0 = r8.mime_type
            boolean r0 = r0.equals(r10)
            if (r0 == 0) goto L_0x043d
            if (r38 != 0) goto L_0x043d
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options
            r1.<init>()
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ Exception -> 0x0401 }
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0401 }
            java.lang.String r2 = "r"
            r4 = r19
            r0.<init>(r4, r2)     // Catch:{ Exception -> 0x03ff }
            java.nio.channels.FileChannel r9 = r0.getChannel()     // Catch:{ Exception -> 0x03ff }
            java.nio.channels.FileChannel$MapMode r10 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Exception -> 0x03ff }
            r11 = 0
            int r2 = r4.length()     // Catch:{ Exception -> 0x03ff }
            long r13 = (long) r2     // Catch:{ Exception -> 0x03ff }
            java.nio.MappedByteBuffer r2 = r9.map(r10, r11, r13)     // Catch:{ Exception -> 0x03ff }
            int r3 = r2.limit()     // Catch:{ Exception -> 0x03ff }
            r5 = 0
            r6 = 1
            org.telegram.messenger.Utilities.loadWebpImage(r5, r2, r3, r1, r6)     // Catch:{ Exception -> 0x03ff }
            r0.close()     // Catch:{ Exception -> 0x03ff }
            goto L_0x0407
        L_0x03ff:
            r0 = move-exception
            goto L_0x0404
        L_0x0401:
            r0 = move-exception
            r4 = r19
        L_0x0404:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0407:
            int r0 = r1.outWidth
            if (r0 == 0) goto L_0x043f
            int r2 = r1.outHeight
            if (r2 == 0) goto L_0x043f
            r3 = 800(0x320, float:1.121E-42)
            if (r0 > r3) goto L_0x043f
            if (r2 > r3) goto L_0x043f
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            r2 = r25
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
            goto L_0x0441
        L_0x043d:
            r4 = r19
        L_0x043f:
            r2 = r25
        L_0x0441:
            r3 = r8
            goto L_0x0449
        L_0x0443:
            r4 = r19
            r2 = r25
            r3 = r18
        L_0x0449:
            if (r36 == 0) goto L_0x0451
            java.lang.String r0 = r36.toString()
            r11 = r0
            goto L_0x0452
        L_0x0451:
            r11 = r2
        L_0x0452:
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            if (r20 == 0) goto L_0x0460
            java.lang.String r0 = "originalPath"
            r1 = r20
            r5.put(r0, r1)
        L_0x0460:
            if (r39 == 0) goto L_0x0469
            java.lang.String r0 = "forceDocument"
            java.lang.String r1 = "1"
            r5.put(r0, r1)
        L_0x0469:
            if (r7 == 0) goto L_0x0470
            java.lang.String r0 = "parentObject"
            r5.put(r0, r7)
        L_0x0470:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$Fci1rOTScgigeZEN7gdLfzu_fqg r15 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Fci1rOTScgigeZEN7gdLfzu_fqg
            r0 = r15
            r1 = r38
            r2 = r27
            r6 = r7
            r7 = r32
            r9 = r34
            r10 = r35
            r12 = r37
            r13 = r40
            r14 = r41
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r15)
            r1 = 1
            return r1
        L_0x048c:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, boolean, boolean, int):boolean");
    }

    static /* synthetic */ void lambda$prepareSendingDocumentInternal$63(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, String str3, ArrayList arrayList, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, str, hashMap, false, str2);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject2, messageObject3, str3, arrayList, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str2);
        }
    }

    public static void prepareSendingDocument(AccountInstance accountInstance, String str, String str2, Uri uri, String str3, String str4, long j, MessageObject messageObject, MessageObject messageObject2, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject3, boolean z, int i) {
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
                arrayList2.add(str6);
            }
            prepareSendingDocuments(accountInstance, arrayList, arrayList2, arrayList3, str3, str4, j, messageObject, messageObject2, inputContentInfoCompat, messageObject3, z, i);
        }
    }

    public static void prepareSendingAudioDocuments(AccountInstance accountInstance, ArrayList<MessageObject> arrayList, String str, long j, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, boolean z, int i) {
        new Thread(new Runnable(arrayList, j, accountInstance, str, messageObject3, messageObject, messageObject2, z, i) {
            public final /* synthetic */ ArrayList f$0;
            public final /* synthetic */ long f$1;
            public final /* synthetic */ AccountInstance f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ MessageObject f$4;
            public final /* synthetic */ MessageObject f$5;
            public final /* synthetic */ MessageObject f$6;
            public final /* synthetic */ boolean f$7;
            public final /* synthetic */ int f$8;

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
            }

            public final void run() {
                SendMessagesHelper.lambda$prepareSendingAudioDocuments$65(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        }).start();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v6, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x007a  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0091 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingAudioDocuments$65(java.util.ArrayList r20, long r21, org.telegram.messenger.AccountInstance r23, java.lang.String r24, org.telegram.messenger.MessageObject r25, org.telegram.messenger.MessageObject r26, org.telegram.messenger.MessageObject r27, boolean r28, int r29) {
        /*
            r14 = r21
            int r13 = r20.size()
            r16 = 0
            r12 = 0
        L_0x0009:
            if (r12 >= r13) goto L_0x00d0
            r11 = r20
            java.lang.Object r0 = r11.get(r12)
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            java.lang.String r0 = r0.attachPath
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            int r2 = (int) r14
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
            if (r2 != 0) goto L_0x006c
            org.telegram.messenger.MessagesStorage r5 = r23.getMessagesStorage()
            if (r2 != 0) goto L_0x0049
            r6 = 1
            goto L_0x004a
        L_0x0049:
            r6 = 4
        L_0x004a:
            java.lang.Object[] r5 = r5.getSentFile(r0, r6)
            if (r5 == 0) goto L_0x006c
            r6 = r5[r16]
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r6 == 0) goto L_0x006c
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
            goto L_0x006f
        L_0x006c:
            r6 = r1
            r17 = r6
        L_0x006f:
            if (r17 != 0) goto L_0x007a
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3
            goto L_0x007c
        L_0x007a:
            r3 = r17
        L_0x007c:
            if (r2 == 0) goto L_0x0092
            r2 = 32
            long r7 = r14 >> r2
            int r2 = (int) r7
            org.telegram.messenger.MessagesController r5 = r23.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r5.getEncryptedChat(r2)
            if (r2 != 0) goto L_0x0092
            return
        L_0x0092:
            if (r12 != 0) goto L_0x0097
            r17 = r24
            goto L_0x0099
        L_0x0097:
            r17 = r1
        L_0x0099:
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            if (r0 == 0) goto L_0x00a5
            java.lang.String r1 = "originalPath"
            r5.put(r1, r0)
        L_0x00a5:
            if (r6 == 0) goto L_0x00ac
            java.lang.String r0 = "parentObject"
            r5.put(r0, r6)
        L_0x00ac:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$Tz6AUGOX0kQGoN3VfYntUMokz0c r18 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Tz6AUGOX0kQGoN3VfYntUMokz0c
            r0 = r18
            r1 = r25
            r2 = r23
            r7 = r21
            r9 = r26
            r10 = r27
            r11 = r17
            r17 = r12
            r12 = r28
            r19 = r13
            r13 = r29
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r18)
            int r12 = r17 + 1
            r13 = r19
            goto L_0x0009
        L_0x00d0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$65(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$64(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, MessageObject messageObject4, String str2, boolean z, int i) {
        MessageObject messageObject5 = messageObject2;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, messageObject5.messageOwner.attachPath, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, messageObject5.messageOwner.attachPath, j, messageObject3, messageObject4, str2, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str);
    }

    public static void prepareSendingDocuments(AccountInstance accountInstance, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<Uri> arrayList3, String str, String str2, long j, MessageObject messageObject, MessageObject messageObject2, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject3, boolean z, int i) {
        if (arrayList != null || arrayList2 != null || arrayList3 != null) {
            if (arrayList == null || arrayList2 == null || arrayList.size() == arrayList2.size()) {
                $$Lambda$SendMessagesHelper$mEALqRXfCdFZme3rh4glOZMNXRI r15 = r0;
                $$Lambda$SendMessagesHelper$mEALqRXfCdFZme3rh4glOZMNXRI r0 = new Runnable(arrayList, str, accountInstance, arrayList2, str2, j, messageObject, messageObject2, messageObject3, z, i, arrayList3, inputContentInfoCompat) {
                    public final /* synthetic */ ArrayList f$0;
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ int f$10;
                    public final /* synthetic */ ArrayList f$11;
                    public final /* synthetic */ InputContentInfoCompat f$12;
                    public final /* synthetic */ AccountInstance f$2;
                    public final /* synthetic */ ArrayList f$3;
                    public final /* synthetic */ String f$4;
                    public final /* synthetic */ long f$5;
                    public final /* synthetic */ MessageObject f$6;
                    public final /* synthetic */ MessageObject f$7;
                    public final /* synthetic */ MessageObject f$8;
                    public final /* synthetic */ boolean f$9;

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
                        this.f$12 = r14;
                    }

                    public final void run() {
                        SendMessagesHelper.lambda$prepareSendingDocuments$67(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
                    }
                };
                new Thread(r15).start();
            }
        }
    }

    static /* synthetic */ void lambda$prepareSendingDocuments$67(ArrayList arrayList, String str, AccountInstance accountInstance, ArrayList arrayList2, String str2, long j, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, boolean z, int i, ArrayList arrayList3, InputContentInfoCompat inputContentInfoCompat) {
        boolean z2;
        ArrayList arrayList4 = arrayList;
        ArrayList arrayList5 = arrayList3;
        int i2 = 0;
        if (arrayList4 != null) {
            int i3 = 0;
            z2 = false;
            while (i3 < arrayList.size()) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList4.get(i3), (String) arrayList2.get(i3), (Uri) null, str2, j, messageObject, messageObject2, i3 == 0 ? str : null, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, false, z, i)) {
                    z2 = true;
                }
                i3++;
            }
        } else {
            z2 = false;
        }
        if (arrayList5 != null) {
            while (i2 < arrayList3.size()) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) null, (String) null, (Uri) arrayList5.get(i2), str2, j, messageObject, messageObject2, (i2 == 0 && (arrayList4 == null || arrayList.size() == 0)) ? str : null, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, false, z, i)) {
                    z2 = true;
                }
                i2++;
            }
        }
        if (inputContentInfoCompat != null) {
            inputContentInfoCompat.releasePermission();
        }
        if (z2) {
            AndroidUtilities.runOnUIThread($$Lambda$SendMessagesHelper$1qO7MMFZawGoD5bH0kPM7GdKrM.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$null$66() {
        try {
            Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", NUM), 0).show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, Uri uri, long j, MessageObject messageObject, MessageObject messageObject2, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, ArrayList<TLRPC$InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject3, boolean z, int i2) {
        ArrayList<TLRPC$InputDocument> arrayList3 = arrayList2;
        SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
        sendingMediaInfo.path = str;
        sendingMediaInfo.uri = uri;
        if (charSequence != null) {
            sendingMediaInfo.caption = charSequence.toString();
        }
        sendingMediaInfo.entities = arrayList;
        sendingMediaInfo.ttl = i;
        if (arrayList3 != null) {
            sendingMediaInfo.masks = new ArrayList<>(arrayList3);
        }
        ArrayList arrayList4 = new ArrayList();
        arrayList4.add(sendingMediaInfo);
        prepareSendingMedia(accountInstance, arrayList4, j, messageObject, messageObject2, inputContentInfoCompat, false, false, messageObject3, z, i2);
    }

    public static void prepareSendingBotContextResult(AccountInstance accountInstance, TLRPC$BotInlineResult tLRPC$BotInlineResult, HashMap<String, String> hashMap, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i) {
        TLRPC$BotInlineResult tLRPC$BotInlineResult2 = tLRPC$BotInlineResult;
        if (tLRPC$BotInlineResult2 != null) {
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult2.send_message;
            if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaAuto) {
                new Thread(new Runnable(j, tLRPC$BotInlineResult, accountInstance, hashMap, messageObject, messageObject2, z, i) {
                    public final /* synthetic */ long f$0;
                    public final /* synthetic */ TLRPC$BotInlineResult f$1;
                    public final /* synthetic */ AccountInstance f$2;
                    public final /* synthetic */ HashMap f$3;
                    public final /* synthetic */ MessageObject f$4;
                    public final /* synthetic */ MessageObject f$5;
                    public final /* synthetic */ boolean f$6;
                    public final /* synthetic */ int f$7;

                    {
                        this.f$0 = r1;
                        this.f$1 = r3;
                        this.f$2 = r4;
                        this.f$3 = r5;
                        this.f$4 = r6;
                        this.f$5 = r7;
                        this.f$6 = r8;
                        this.f$7 = r9;
                    }

                    public final void run() {
                        SendMessagesHelper.lambda$prepareSendingBotContextResult$69(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
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
                sendMessagesHelper.sendMessage(tLRPC$BotInlineMessage2.message, j, messageObject, messageObject2, tLRPC$TL_webPagePending2, !tLRPC$BotInlineMessage2.no_webpage, tLRPC$BotInlineMessage2.entities, tLRPC$BotInlineMessage2.reply_markup, hashMap, z, i);
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
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaVenue, j, messageObject, messageObject2, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
                } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaGeo) {
                    if (tLRPC$BotInlineMessage.period != 0) {
                        TLRPC$TL_messageMediaGeoLive tLRPC$TL_messageMediaGeoLive = new TLRPC$TL_messageMediaGeoLive();
                        TLRPC$BotInlineMessage tLRPC$BotInlineMessage4 = tLRPC$BotInlineResult2.send_message;
                        tLRPC$TL_messageMediaGeoLive.period = tLRPC$BotInlineMessage4.period;
                        tLRPC$TL_messageMediaGeoLive.geo = tLRPC$BotInlineMessage4.geo;
                        accountInstance.getSendMessagesHelper().sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaGeoLive, j, messageObject, messageObject2, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
                        return;
                    }
                    TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo = new TLRPC$TL_messageMediaGeo();
                    tLRPC$TL_messageMediaGeo.geo = tLRPC$BotInlineResult2.send_message.geo;
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaGeo, j, messageObject, messageObject2, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
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
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC$User) tLRPC$TL_user, j, messageObject, messageObject2, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v0, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v91, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v92, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v93, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v94, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v96, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v123, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v35, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v125, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v126, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r0v97 */
    /* JADX WARNING: type inference failed for: r0v127 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x043b  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0441  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x044d  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0499  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingBotContextResult$69(long r20, org.telegram.tgnet.TLRPC$BotInlineResult r22, org.telegram.messenger.AccountInstance r23, java.util.HashMap r24, org.telegram.messenger.MessageObject r25, org.telegram.messenger.MessageObject r26, boolean r27, int r28) {
        /*
            r11 = r22
            r12 = r24
            r7 = r20
            int r0 = (int) r7
            r2 = 1
            if (r0 != 0) goto L_0x000c
            r13 = 1
            goto L_0x000d
        L_0x000c:
            r13 = 0
        L_0x000d:
            java.lang.String r3 = r11.type
            java.lang.String r4 = "game"
            boolean r3 = r4.equals(r3)
            r5 = 0
            if (r3 == 0) goto L_0x004d
            if (r0 != 0) goto L_0x001b
            return
        L_0x001b:
            org.telegram.tgnet.TLRPC$TL_game r0 = new org.telegram.tgnet.TLRPC$TL_game
            r0.<init>()
            java.lang.String r3 = r11.title
            r0.title = r3
            java.lang.String r3 = r11.description
            r0.description = r3
            java.lang.String r3 = r11.id
            r0.short_name = r3
            org.telegram.tgnet.TLRPC$Photo r3 = r11.photo
            r0.photo = r3
            if (r3 != 0) goto L_0x0039
            org.telegram.tgnet.TLRPC$TL_photoEmpty r3 = new org.telegram.tgnet.TLRPC$TL_photoEmpty
            r3.<init>()
            r0.photo = r3
        L_0x0039:
            org.telegram.tgnet.TLRPC$Document r3 = r11.document
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r6 == 0) goto L_0x0046
            r0.document = r3
            int r3 = r0.flags
            r3 = r3 | r2
            r0.flags = r3
        L_0x0046:
            r19 = r0
            r0 = r5
            r2 = r0
            r6 = r2
            goto L_0x0481
        L_0x004d:
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMediaResult
            if (r0 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$Document r0 = r11.document
            if (r0 == 0) goto L_0x0060
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r3 == 0) goto L_0x047b
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            r2 = r0
            r0 = r5
            r6 = r0
            goto L_0x047f
        L_0x0060:
            org.telegram.tgnet.TLRPC$Photo r0 = r11.photo
            if (r0 == 0) goto L_0x047b
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r3 == 0) goto L_0x047b
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            r2 = r5
            goto L_0x047e
        L_0x006d:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            if (r0 == 0) goto L_0x047b
            java.lang.String r0 = r0.url
            java.lang.String r0 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r0, r5)
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            java.lang.String r6 = "."
            if (r3 == 0) goto L_0x0088
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            java.lang.String r0 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r0)
            goto L_0x0097
        L_0x0088:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x0097:
            java.io.File r3 = new java.io.File
            r9 = 4
            java.io.File r10 = org.telegram.messenger.FileLoader.getDirectory(r9)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.content
            java.lang.String r15 = r15.url
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)
            r14.append(r15)
            r14.append(r0)
            java.lang.String r0 = r14.toString()
            r3.<init>(r10, r0)
            boolean r0 = r3.exists()
            if (r0 == 0) goto L_0x00c3
            java.lang.String r0 = r3.getAbsolutePath()
            goto L_0x00c7
        L_0x00c3:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.url
        L_0x00c7:
            r10 = r0
            java.lang.String r0 = r11.type
            int r14 = r0.hashCode()
            java.lang.String r15 = "voice"
            java.lang.String r4 = "video"
            java.lang.String r9 = "audio"
            java.lang.String r2 = "gif"
            java.lang.String r1 = "sticker"
            java.lang.String r5 = "file"
            switch(r14) {
                case -1890252483: goto L_0x0110;
                case 102340: goto L_0x0108;
                case 3143036: goto L_0x0100;
                case 93166550: goto L_0x00f8;
                case 106642994: goto L_0x00ee;
                case 112202875: goto L_0x00e6;
                case 112386354: goto L_0x00de;
                default: goto L_0x00dd;
            }
        L_0x00dd:
            goto L_0x0118
        L_0x00de:
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x0118
            r0 = 1
            goto L_0x0119
        L_0x00e6:
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x0118
            r0 = 3
            goto L_0x0119
        L_0x00ee:
            java.lang.String r14 = "photo"
            boolean r0 = r0.equals(r14)
            if (r0 == 0) goto L_0x0118
            r0 = 6
            goto L_0x0119
        L_0x00f8:
            boolean r0 = r0.equals(r9)
            if (r0 == 0) goto L_0x0118
            r0 = 0
            goto L_0x0119
        L_0x0100:
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x0118
            r0 = 2
            goto L_0x0119
        L_0x0108:
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x0118
            r0 = 5
            goto L_0x0119
        L_0x0110:
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0118
            r0 = 4
            goto L_0x0119
        L_0x0118:
            r0 = -1
        L_0x0119:
            java.lang.String r14 = "x"
            switch(r0) {
                case 0: goto L_0x0176;
                case 1: goto L_0x0176;
                case 2: goto L_0x0176;
                case 3: goto L_0x0176;
                case 4: goto L_0x0176;
                case 5: goto L_0x0176;
                case 6: goto L_0x0127;
                default: goto L_0x011f;
            }
        L_0x011f:
            r4 = 0
            r0 = r4
            r2 = r0
            r19 = r2
        L_0x0124:
            r6 = r10
            goto L_0x0481
        L_0x0127:
            boolean r0 = r3.exists()
            if (r0 == 0) goto L_0x0137
            org.telegram.messenger.SendMessagesHelper r0 = r23.getSendMessagesHelper()
            r1 = 0
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r10, r1)
            goto L_0x0138
        L_0x0137:
            r0 = 0
        L_0x0138:
            if (r0 != 0) goto L_0x0170
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
        L_0x0170:
            r6 = r10
            r2 = 0
            r19 = 0
            goto L_0x0481
        L_0x0176:
            org.telegram.tgnet.TLRPC$TL_document r3 = new org.telegram.tgnet.TLRPC$TL_document
            r3.<init>()
            r7 = 0
            r3.id = r7
            r7 = 0
            r3.size = r7
            r3.dc_id = r7
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r3.mime_type = r0
            byte[] r0 = new byte[r7]
            r3.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r23.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r3.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r7.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r3.attributes
            r0.add(r7)
            java.lang.String r0 = r11.type
            int r8 = r0.hashCode()
            switch(r8) {
                case -1890252483: goto L_0x01d4;
                case 102340: goto L_0x01cc;
                case 3143036: goto L_0x01c4;
                case 93166550: goto L_0x01bc;
                case 112202875: goto L_0x01b4;
                case 112386354: goto L_0x01ac;
                default: goto L_0x01ab;
            }
        L_0x01ab:
            goto L_0x01dc
        L_0x01ac:
            boolean r0 = r0.equals(r15)
            if (r0 == 0) goto L_0x01dc
            r0 = 1
            goto L_0x01dd
        L_0x01b4:
            boolean r0 = r0.equals(r4)
            if (r0 == 0) goto L_0x01dc
            r0 = 4
            goto L_0x01dd
        L_0x01bc:
            boolean r0 = r0.equals(r9)
            if (r0 == 0) goto L_0x01dc
            r0 = 2
            goto L_0x01dd
        L_0x01c4:
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x01dc
            r0 = 3
            goto L_0x01dd
        L_0x01cc:
            boolean r0 = r0.equals(r2)
            if (r0 == 0) goto L_0x01dc
            r0 = 0
            goto L_0x01dd
        L_0x01d4:
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x01dc
            r0 = 5
            goto L_0x01dd
        L_0x01dc:
            r0 = -1
        L_0x01dd:
            r1 = 55
            if (r0 == 0) goto L_0x0374
            r2 = 1
            if (r0 == r2) goto L_0x035b
            r2 = 2
            if (r0 == r2) goto L_0x0331
            r2 = 3
            if (r0 == r2) goto L_0x0301
            r2 = 1119092736(0x42b40000, float:90.0)
            r4 = 4
            if (r0 == r4) goto L_0x0282
            r4 = 5
            if (r0 == r4) goto L_0x01f5
        L_0x01f2:
            r4 = 0
            goto L_0x0437
        L_0x01f5:
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            java.lang.String r4 = ""
            r0.alt = r4
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r4.<init>()
            r0.stickerset = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r0)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r0.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r8 = 0
            r9 = r4[r8]
            r0.w = r9
            r8 = 1
            r4 = r4[r8]
            r0.h = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r0)
            java.lang.String r0 = "sticker.webp"
            r7.file_name = r0
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.thumb     // Catch:{ all -> 0x027c }
            if (r0 == 0) goto L_0x01f2
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x027c }
            r4 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ all -> 0x027c }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x027c }
            r8.<init>()     // Catch:{ all -> 0x027c }
            org.telegram.tgnet.TLRPC$WebDocument r9 = r11.thumb     // Catch:{ all -> 0x027c }
            java.lang.String r9 = r9.url     // Catch:{ all -> 0x027c }
            java.lang.String r9 = org.telegram.messenger.Utilities.MD5(r9)     // Catch:{ all -> 0x027c }
            r8.append(r9)     // Catch:{ all -> 0x027c }
            r8.append(r6)     // Catch:{ all -> 0x027c }
            org.telegram.tgnet.TLRPC$WebDocument r6 = r11.thumb     // Catch:{ all -> 0x027c }
            java.lang.String r6 = r6.url     // Catch:{ all -> 0x027c }
            java.lang.String r9 = "webp"
            java.lang.String r6 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r9)     // Catch:{ all -> 0x027c }
            r8.append(r6)     // Catch:{ all -> 0x027c }
            java.lang.String r6 = r8.toString()     // Catch:{ all -> 0x027c }
            r0.<init>(r4, r6)     // Catch:{ all -> 0x027c }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x027c }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r4, r2, r2, r6)     // Catch:{ all -> 0x027c }
            if (r0 == 0) goto L_0x01f2
            r4 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r1, r4)     // Catch:{ all -> 0x027c }
            if (r1 == 0) goto L_0x0277
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r3.thumbs     // Catch:{ all -> 0x027c }
            r2.add(r1)     // Catch:{ all -> 0x027c }
            int r1 = r3.flags     // Catch:{ all -> 0x027c }
            r2 = 1
            r1 = r1 | r2
            r3.flags = r1     // Catch:{ all -> 0x027c }
        L_0x0277:
            r0.recycle()     // Catch:{ all -> 0x027c }
            goto L_0x01f2
        L_0x027c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01f2
        L_0x0282:
            java.lang.String r0 = "video.mp4"
            r7.file_name = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r0.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r8 = 0
            r9 = r4[r8]
            r0.w = r9
            r8 = 1
            r4 = r4[r8]
            r0.h = r4
            int r4 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r4
            r0.supports_streaming = r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r0)
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.thumb     // Catch:{ all -> 0x02fb }
            if (r0 == 0) goto L_0x01f2
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x02fb }
            r4 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ all -> 0x02fb }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x02fb }
            r8.<init>()     // Catch:{ all -> 0x02fb }
            org.telegram.tgnet.TLRPC$WebDocument r9 = r11.thumb     // Catch:{ all -> 0x02fb }
            java.lang.String r9 = r9.url     // Catch:{ all -> 0x02fb }
            java.lang.String r9 = org.telegram.messenger.Utilities.MD5(r9)     // Catch:{ all -> 0x02fb }
            r8.append(r9)     // Catch:{ all -> 0x02fb }
            r8.append(r6)     // Catch:{ all -> 0x02fb }
            org.telegram.tgnet.TLRPC$WebDocument r6 = r11.thumb     // Catch:{ all -> 0x02fb }
            java.lang.String r6 = r6.url     // Catch:{ all -> 0x02fb }
            java.lang.String r9 = "jpg"
            java.lang.String r6 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r9)     // Catch:{ all -> 0x02fb }
            r8.append(r6)     // Catch:{ all -> 0x02fb }
            java.lang.String r6 = r8.toString()     // Catch:{ all -> 0x02fb }
            r0.<init>(r4, r6)     // Catch:{ all -> 0x02fb }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x02fb }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r4, r2, r2, r6)     // Catch:{ all -> 0x02fb }
            if (r0 == 0) goto L_0x01f2
            r4 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r1, r4)     // Catch:{ all -> 0x02fb }
            if (r1 == 0) goto L_0x02f6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r3.thumbs     // Catch:{ all -> 0x02fb }
            r2.add(r1)     // Catch:{ all -> 0x02fb }
            int r1 = r3.flags     // Catch:{ all -> 0x02fb }
            r2 = 1
            r1 = r1 | r2
            r3.flags = r1     // Catch:{ all -> 0x02fb }
        L_0x02f6:
            r0.recycle()     // Catch:{ all -> 0x02fb }
            goto L_0x01f2
        L_0x02fb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01f2
        L_0x0301:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r1 = 47
            int r0 = r0.lastIndexOf(r1)
            r1 = -1
            if (r0 == r1) goto L_0x032d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "file."
            r1.append(r2)
            org.telegram.tgnet.TLRPC$WebDocument r2 = r11.content
            java.lang.String r2 = r2.mime_type
            r4 = 1
            int r0 = r0 + r4
            java.lang.String r0 = r2.substring(r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r7.file_name = r0
            goto L_0x01f2
        L_0x032d:
            r7.file_name = r5
            goto L_0x01f2
        L_0x0331:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r1
            java.lang.String r1 = r11.title
            r0.title = r1
            int r1 = r0.flags
            r2 = 1
            r1 = r1 | r2
            r0.flags = r1
            java.lang.String r2 = r11.description
            if (r2 == 0) goto L_0x0350
            r0.performer = r2
            r2 = 2
            r1 = r1 | r2
            r0.flags = r1
        L_0x0350:
            java.lang.String r1 = "audio.mp3"
            r7.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r3.attributes
            r1.add(r0)
            goto L_0x01f2
        L_0x035b:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r1
            r1 = 1
            r0.voice = r1
            java.lang.String r1 = "audio.ogg"
            r7.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r3.attributes
            r1.add(r0)
            goto L_0x01f2
        L_0x0374:
            java.lang.String r0 = "animation.gif"
            r7.file_name = r0
            java.lang.String r0 = "mp4"
            boolean r2 = r10.endsWith(r0)
            java.lang.String r4 = "video/mp4"
            if (r2 == 0) goto L_0x038f
            r3.mime_type = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r3.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r8.<init>()
            r2.add(r8)
            goto L_0x0393
        L_0x038f:
            java.lang.String r2 = "image/gif"
            r3.mime_type = r2
        L_0x0393:
            r2 = 90
            if (r13 == 0) goto L_0x039a
            r8 = 90
            goto L_0x039c
        L_0x039a:
            r8 = 320(0x140, float:4.48E-43)
        L_0x039c:
            boolean r0 = r10.endsWith(r0)     // Catch:{ all -> 0x0432 }
            if (r0 == 0) goto L_0x040c
            r9 = 1
            android.graphics.Bitmap r0 = createVideoThumbnail(r10, r9)     // Catch:{ all -> 0x0432 }
            if (r0 != 0) goto L_0x040a
            org.telegram.tgnet.TLRPC$WebDocument r9 = r11.thumb     // Catch:{ all -> 0x0432 }
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_webDocument     // Catch:{ all -> 0x0432 }
            if (r9 == 0) goto L_0x040a
            org.telegram.tgnet.TLRPC$WebDocument r9 = r11.thumb     // Catch:{ all -> 0x0432 }
            java.lang.String r9 = r9.mime_type     // Catch:{ all -> 0x0432 }
            boolean r4 = r4.equals(r9)     // Catch:{ all -> 0x0432 }
            if (r4 == 0) goto L_0x040a
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.thumb     // Catch:{ all -> 0x0432 }
            java.lang.String r0 = r0.url     // Catch:{ all -> 0x0432 }
            r4 = 0
            java.lang.String r0 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r0, r4)     // Catch:{ all -> 0x0430 }
            boolean r4 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0432 }
            if (r4 == 0) goto L_0x03d1
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.thumb     // Catch:{ all -> 0x0432 }
            java.lang.String r0 = r0.mime_type     // Catch:{ all -> 0x0432 }
            java.lang.String r0 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r0)     // Catch:{ all -> 0x0432 }
            goto L_0x03e0
        L_0x03d1:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0432 }
            r4.<init>()     // Catch:{ all -> 0x0432 }
            r4.append(r6)     // Catch:{ all -> 0x0432 }
            r4.append(r0)     // Catch:{ all -> 0x0432 }
            java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x0432 }
        L_0x03e0:
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x0432 }
            r6 = 4
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r6)     // Catch:{ all -> 0x0432 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0432 }
            r9.<init>()     // Catch:{ all -> 0x0432 }
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.thumb     // Catch:{ all -> 0x0432 }
            java.lang.String r15 = r15.url     // Catch:{ all -> 0x0432 }
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)     // Catch:{ all -> 0x0432 }
            r9.append(r15)     // Catch:{ all -> 0x0432 }
            r9.append(r0)     // Catch:{ all -> 0x0432 }
            java.lang.String r0 = r9.toString()     // Catch:{ all -> 0x0432 }
            r4.<init>(r6, r0)     // Catch:{ all -> 0x0432 }
            java.lang.String r0 = r4.getAbsolutePath()     // Catch:{ all -> 0x0432 }
            r4 = 1
            android.graphics.Bitmap r0 = createVideoThumbnail(r0, r4)     // Catch:{ all -> 0x0432 }
        L_0x040a:
            r4 = 0
            goto L_0x0413
        L_0x040c:
            float r0 = (float) r8
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r10, r4, r0, r0, r6)     // Catch:{ all -> 0x0430 }
        L_0x0413:
            if (r0 == 0) goto L_0x0437
            float r6 = (float) r8     // Catch:{ all -> 0x0430 }
            if (r8 <= r2) goto L_0x041a
            r1 = 80
        L_0x041a:
            r2 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r6, r6, r1, r2)     // Catch:{ all -> 0x0430 }
            if (r1 == 0) goto L_0x042c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r3.thumbs     // Catch:{ all -> 0x0430 }
            r2.add(r1)     // Catch:{ all -> 0x0430 }
            int r1 = r3.flags     // Catch:{ all -> 0x0430 }
            r2 = 1
            r1 = r1 | r2
            r3.flags = r1     // Catch:{ all -> 0x0430 }
        L_0x042c:
            r0.recycle()     // Catch:{ all -> 0x0430 }
            goto L_0x0437
        L_0x0430:
            r0 = move-exception
            goto L_0x0434
        L_0x0432:
            r0 = move-exception
            r4 = 0
        L_0x0434:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0437:
            java.lang.String r0 = r7.file_name
            if (r0 != 0) goto L_0x043d
            r7.file_name = r5
        L_0x043d:
            java.lang.String r0 = r3.mime_type
            if (r0 != 0) goto L_0x0445
            java.lang.String r0 = "application/octet-stream"
            r3.mime_type = r0
        L_0x0445:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r3.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0475
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            int[] r1 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r2 = 0
            r5 = r1[r2]
            r0.w = r5
            r5 = 1
            r1 = r1[r5]
            r0.h = r1
            r0.size = r2
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r1.<init>()
            r0.location = r1
            r0.type = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs
            r1.add(r0)
            int r0 = r3.flags
            r0 = r0 | r5
            r3.flags = r0
        L_0x0475:
            r2 = r3
            r0 = r4
            r19 = r0
            goto L_0x0124
        L_0x047b:
            r4 = r5
            r0 = r4
            r2 = r0
        L_0x047e:
            r6 = r2
        L_0x047f:
            r19 = r6
        L_0x0481:
            if (r12 == 0) goto L_0x048e
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.content
            if (r1 == 0) goto L_0x048e
            java.lang.String r1 = r1.url
            java.lang.String r3 = "originalPath"
            r12.put(r3, r1)
        L_0x048e:
            r1 = 1
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r1]
            java.lang.String[] r4 = new java.lang.String[r1]
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r2)
            if (r5 == 0) goto L_0x04c3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r2.thumbs
            r7 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r7)
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r2)
            boolean r8 = r7.exists()
            if (r8 != 0) goto L_0x04af
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r1)
        L_0x04af:
            java.lang.String r15 = r7.getAbsolutePath()
            r16 = 0
            r17 = 0
            r14 = r2
            ensureMediaThumbExists(r13, r14, r15, r16, r17)
            r1 = 1
            java.lang.String r1 = getKeyForPhotoSize(r5, r3, r1, r1)
            r5 = 0
            r4[r5] = r1
        L_0x04c3:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$kJDMIdf-FMHxlNqMOo_CkfodlOk r17 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$kJDMIdf-FMHxlNqMOo_CkfodlOk
            r1 = r17
            r5 = r23
            r7 = r20
            r9 = r25
            r10 = r26
            r11 = r22
            r12 = r24
            r13 = r27
            r14 = r28
            r15 = r0
            r16 = r19
            r1.<init>(r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15, r16)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r17)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$69(long, org.telegram.tgnet.TLRPC$BotInlineResult, org.telegram.messenger.AccountInstance, java.util.HashMap, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$68(TLRPC$TL_document tLRPC$TL_document, Bitmap[] bitmapArr, String[] strArr, AccountInstance accountInstance, String str, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$BotInlineResult tLRPC$BotInlineResult, HashMap hashMap, boolean z, int i, TLRPC$TL_photo tLRPC$TL_photo, TLRPC$TL_game tLRPC$TL_game) {
        TLRPC$BotInlineResult tLRPC$BotInlineResult2 = tLRPC$BotInlineResult;
        if (tLRPC$TL_document != null) {
            if (!(bitmapArr[0] == null || strArr[0] == null)) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
            }
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult2.send_message;
            sendMessagesHelper.sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject, messageObject2, tLRPC$BotInlineMessage.message, tLRPC$BotInlineMessage.entities, tLRPC$BotInlineMessage.reply_markup, hashMap, z, i, 0, tLRPC$BotInlineResult);
        } else if (tLRPC$TL_photo != null) {
            SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
            TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult2.content;
            String str2 = tLRPC$WebDocument != null ? tLRPC$WebDocument.url : null;
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage2 = tLRPC$BotInlineResult2.send_message;
            sendMessagesHelper2.sendMessage(tLRPC$TL_photo, str2, j, messageObject, messageObject2, tLRPC$BotInlineMessage2.message, tLRPC$BotInlineMessage2.entities, tLRPC$BotInlineMessage2.reply_markup, hashMap, z, i, 0, tLRPC$BotInlineResult);
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
            public final /* synthetic */ String f$0;
            public final /* synthetic */ AccountInstance f$1;
            public final /* synthetic */ long f$2;
            public final /* synthetic */ boolean f$3;
            public final /* synthetic */ int f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
                this.f$4 = r6;
            }

            public final void run() {
                Utilities.stageQueue.postRunnable(new Runnable(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4) {
                    public final /* synthetic */ String f$0;
                    public final /* synthetic */ AccountInstance f$1;
                    public final /* synthetic */ long f$2;
                    public final /* synthetic */ boolean f$3;
                    public final /* synthetic */ int f$4;

                    {
                        this.f$0 = r1;
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r5;
                        this.f$4 = r6;
                    }

                    public final void run() {
                        AndroidUtilities.runOnUIThread(new Runnable(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4) {
                            public final /* synthetic */ String f$0;
                            public final /* synthetic */ AccountInstance f$1;
                            public final /* synthetic */ long f$2;
                            public final /* synthetic */ boolean f$3;
                            public final /* synthetic */ int f$4;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r5;
                                this.f$4 = r6;
                            }

                            public final void run() {
                                SendMessagesHelper.lambda$null$70(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                            }
                        });
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$null$70(String str, AccountInstance accountInstance, long j, boolean z, int i) {
        String trimmedString = getTrimmedString(str);
        if (trimmedString.length() != 0) {
            int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / 4096.0f));
            int i2 = 0;
            while (i2 < ceil) {
                int i3 = i2 * 4096;
                i2++;
                accountInstance.getSendMessagesHelper().sendMessage(trimmedString.substring(i3, Math.min(i2 * 4096, trimmedString.length())), j, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i);
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
                if (!exists && (scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize2, bitmap, Bitmap.CompressFormat.JPEG, true, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101, false)) != closestPhotoSizeWithSize2) {
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
                    Bitmap createVideoThumbnailAtTime = createVideoThumbnailAtTime(str2, j);
                    Bitmap createVideoThumbnail = createVideoThumbnailAtTime == null ? createVideoThumbnail(str2, 1) : createVideoThumbnailAtTime;
                    if (z) {
                        i = 90;
                    }
                    float f = (float) i;
                    tLRPC$TL_document.thumbs.set(0, ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize3, createVideoThumbnail, f, f, i > 90 ? 80 : 55, false, true));
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

    public static void prepareSendingMedia(AccountInstance accountInstance, ArrayList<SendingMediaInfo> arrayList, long j, MessageObject messageObject, MessageObject messageObject2, InputContentInfoCompat inputContentInfoCompat, boolean z, boolean z2, MessageObject messageObject3, boolean z3, int i) {
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
            mediaSendQueue.postRunnable(new Runnable(arrayList, j, accountInstance, z, z4, messageObject3, messageObject, messageObject2, z3, i, inputContentInfoCompat) {
                public final /* synthetic */ ArrayList f$0;
                public final /* synthetic */ long f$1;
                public final /* synthetic */ InputContentInfoCompat f$10;
                public final /* synthetic */ AccountInstance f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ boolean f$4;
                public final /* synthetic */ MessageObject f$5;
                public final /* synthetic */ MessageObject f$6;
                public final /* synthetic */ MessageObject f$7;
                public final /* synthetic */ boolean f$8;
                public final /* synthetic */ int f$9;

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
                    this.f$10 = r12;
                }

                public final void run() {
                    SendMessagesHelper.lambda$prepareSendingMedia$79(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
                }
            });
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v0, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v1, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v2, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r33v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v46, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v3, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v4, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v5, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v6, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v100, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v7, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v8, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v9, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v10, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v180, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v181, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v182, resolved type: java.util.ArrayList} */
    /* JADX WARNING: type inference failed for: r0v99, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r2v168 */
    /* JADX WARNING: type inference failed for: r3v134 */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:217|218|219|(8:220|221|222|223|224|225|226|(2:228|229))|230|231) */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x0626, code lost:
        if (r5 != null) goto L_0x0609;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x088b, code lost:
        if (r61.size() == 1) goto L_0x0894;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:230:0x0609 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02e7 A[Catch:{ Exception -> 0x02d8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x02f2 A[Catch:{ Exception -> 0x02d8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x02ff A[Catch:{ Exception -> 0x031e }] */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x032c  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0369  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x036b  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0376  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x058e  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0621 A[SYNTHETIC, Splitter:B:243:0x0621] */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x06b0  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x0709  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x08b1  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x08df  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0974  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0af8  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0bff  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:539:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x0ce8 A[LOOP:4: B:556:0x0ce0->B:558:0x0ce8, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:567:0x0d98  */
    /* JADX WARNING: Removed duplicated region for block: B:568:0x0da5  */
    /* JADX WARNING: Removed duplicated region for block: B:570:0x0dab  */
    /* JADX WARNING: Removed duplicated region for block: B:577:0x0dbd A[LOOP:5: B:575:0x0db7->B:577:0x0dbd, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:580:0x0e1d  */
    /* JADX WARNING: Removed duplicated region for block: B:590:0x0CLASSNAME A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:595:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01ab  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingMedia$79(java.util.ArrayList r61, long r62, org.telegram.messenger.AccountInstance r64, boolean r65, boolean r66, org.telegram.messenger.MessageObject r67, org.telegram.messenger.MessageObject r68, org.telegram.messenger.MessageObject r69, boolean r70, int r71, androidx.core.view.inputmethod.InputContentInfoCompat r72) {
        /*
            r1 = r61
            r14 = r62
            r13 = r64
            long r19 = java.lang.System.currentTimeMillis()
            int r12 = r61.size()
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
            org.telegram.messenger.MessagesController r2 = r64.getMessagesController()
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
            r6 = 73
            java.lang.String r7 = ".webp"
            java.lang.String r5 = ".gif"
            r21 = 3
            java.lang.String r4 = "_"
            if (r10 == 0) goto L_0x0040
            if (r8 < r6) goto L_0x0191
        L_0x0040:
            if (r65 != 0) goto L_0x0191
            if (r66 == 0) goto L_0x0191
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r2 = 0
        L_0x004a:
            if (r2 >= r12) goto L_0x0186
            java.lang.Object r16 = r1.get(r2)
            r6 = r16
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r6 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r6
            org.telegram.messenger.MediaController$SearchImage r3 = r6.searchImage
            if (r3 != 0) goto L_0x016d
            boolean r3 = r6.isVideo
            if (r3 != 0) goto L_0x016d
            org.telegram.messenger.VideoEditedInfo r3 = r6.videoEditedInfo
            if (r3 != 0) goto L_0x016d
            java.lang.String r3 = r6.path
            if (r3 != 0) goto L_0x0073
            android.net.Uri r9 = r6.uri
            if (r9 == 0) goto L_0x0073
            java.lang.String r3 = org.telegram.messenger.AndroidUtilities.getPath(r9)
            android.net.Uri r9 = r6.uri
            java.lang.String r9 = r9.toString()
            goto L_0x0074
        L_0x0073:
            r9 = r3
        L_0x0074:
            if (r3 == 0) goto L_0x0084
            boolean r22 = r3.endsWith(r5)
            if (r22 != 0) goto L_0x016d
            boolean r22 = r3.endsWith(r7)
            if (r22 == 0) goto L_0x0084
            goto L_0x016d
        L_0x0084:
            java.lang.String r11 = r6.path
            r23 = r2
            android.net.Uri r2 = r6.uri
            boolean r2 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r11, r2)
            if (r2 == 0) goto L_0x0092
            goto L_0x016f
        L_0x0092:
            if (r3 != 0) goto L_0x00a8
            android.net.Uri r2 = r6.uri
            if (r2 == 0) goto L_0x00a8
            boolean r2 = org.telegram.messenger.MediaController.isGif(r2)
            if (r2 != 0) goto L_0x016f
            android.net.Uri r2 = r6.uri
            boolean r2 = org.telegram.messenger.MediaController.isWebp(r2)
            if (r2 == 0) goto L_0x00a8
            goto L_0x016f
        L_0x00a8:
            if (r3 == 0) goto L_0x00cd
            java.io.File r2 = new java.io.File
            r2.<init>(r3)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r9)
            long r14 = r2.length()
            r3.append(r14)
            r3.append(r4)
            long r14 = r2.lastModified()
            r3.append(r14)
            java.lang.String r2 = r3.toString()
            goto L_0x00ce
        L_0x00cd:
            r2 = 0
        L_0x00ce:
            if (r10 != 0) goto L_0x013f
            int r3 = r6.ttl
            if (r3 != 0) goto L_0x013f
            org.telegram.messenger.MessagesStorage r3 = r64.getMessagesStorage()
            if (r10 != 0) goto L_0x00dc
            r9 = 0
            goto L_0x00dd
        L_0x00dc:
            r9 = 3
        L_0x00dd:
            java.lang.Object[] r2 = r3.getSentFile(r2, r9)
            if (r2 == 0) goto L_0x00f4
            r3 = 0
            r9 = r2[r3]
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r9 == 0) goto L_0x00f4
            r9 = r2[r3]
            org.telegram.tgnet.TLRPC$TL_photo r9 = (org.telegram.tgnet.TLRPC$TL_photo) r9
            r3 = 1
            r2 = r2[r3]
            java.lang.String r2 = (java.lang.String) r2
            goto L_0x00f6
        L_0x00f4:
            r2 = 0
            r9 = 0
        L_0x00f6:
            if (r9 != 0) goto L_0x0123
            android.net.Uri r3 = r6.uri
            if (r3 == 0) goto L_0x0123
            org.telegram.messenger.MessagesStorage r3 = r64.getMessagesStorage()
            android.net.Uri r11 = r6.uri
            java.lang.String r11 = org.telegram.messenger.AndroidUtilities.getPath(r11)
            if (r10 != 0) goto L_0x010a
            r14 = 0
            goto L_0x010b
        L_0x010a:
            r14 = 3
        L_0x010b:
            java.lang.Object[] r3 = r3.getSentFile(r11, r14)
            if (r3 == 0) goto L_0x0123
            r11 = 0
            r14 = r3[r11]
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r14 == 0) goto L_0x0123
            r2 = r3[r11]
            r9 = r2
            org.telegram.tgnet.TLRPC$TL_photo r9 = (org.telegram.tgnet.TLRPC$TL_photo) r9
            r2 = 1
            r3 = r3[r2]
            r2 = r3
            java.lang.String r2 = (java.lang.String) r2
        L_0x0123:
            r11 = r9
            r9 = r2
            java.lang.String r14 = r6.path
            android.net.Uri r15 = r6.uri
            r24 = 0
            r2 = r10
            r3 = r11
            r27 = r4
            r4 = r14
            r14 = r5
            r5 = r15
            r15 = r6
            r28 = r7
            r16 = r14
            r14 = 73
            r6 = r24
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            goto L_0x014a
        L_0x013f:
            r27 = r4
            r16 = r5
            r15 = r6
            r28 = r7
            r14 = 73
            r3 = 0
            r9 = 0
        L_0x014a:
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker
            r11 = 0
            r2.<init>()
            r0.put(r15, r2)
            if (r3 == 0) goto L_0x015a
            r2.parentObject = r9
            r2.photo = r3
            goto L_0x0178
        L_0x015a:
            java.util.concurrent.CountDownLatch r3 = new java.util.concurrent.CountDownLatch
            r4 = 1
            r3.<init>(r4)
            r2.sync = r3
            java.util.concurrent.ThreadPoolExecutor r3 = mediaSendThreadPool
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$M05o6TcQTfAI1lmwBMG0k-pUo1M r4 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$M05o6TcQTfAI1lmwBMG0k-pUo1M
            r4.<init>(r13, r15, r10)
            r3.execute(r4)
            goto L_0x0178
        L_0x016d:
            r23 = r2
        L_0x016f:
            r27 = r4
            r16 = r5
            r28 = r7
            r11 = 0
            r14 = 73
        L_0x0178:
            int r2 = r23 + 1
            r14 = r62
            r5 = r16
            r4 = r27
            r7 = r28
            r6 = 73
            goto L_0x004a
        L_0x0186:
            r27 = r4
            r16 = r5
            r28 = r7
            r11 = 0
            r14 = 73
            r15 = r0
            goto L_0x019b
        L_0x0191:
            r27 = r4
            r16 = r5
            r28 = r7
            r11 = 0
            r14 = 73
            r15 = r11
        L_0x019b:
            r2 = r11
            r3 = r2
            r4 = r3
            r5 = r4
            r23 = r5
            r29 = r23
            r0 = 0
            r9 = 0
            r24 = 0
            r30 = 0
        L_0x01a9:
            if (r9 >= r12) goto L_0x0d86
            java.lang.Object r17 = r1.get(r9)
            r11 = r17
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r11 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r11
            if (r66 == 0) goto L_0x01cb
            if (r10 == 0) goto L_0x01b9
            if (r8 < r14) goto L_0x01cb
        L_0x01b9:
            r14 = 1
            if (r12 <= r14) goto L_0x01cb
            int r14 = r0 % 10
            if (r14 != 0) goto L_0x01cb
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r30 = r0.nextLong()
            r32 = r30
            r24 = 0
            goto L_0x01cf
        L_0x01cb:
            r32 = r24
            r24 = r0
        L_0x01cf:
            org.telegram.messenger.MediaController$SearchImage r0 = r11.searchImage
            java.lang.String r14 = "1"
            java.lang.String r6 = "final"
            java.lang.String r7 = "groupId"
            java.lang.String r1 = "mp4"
            r25 = r8
            java.lang.String r8 = "originalPath"
            r36 = r9
            r37 = r5
            java.lang.String r9 = ""
            java.lang.String r5 = "jpg"
            r40 = r2
            java.lang.String r2 = "."
            r41 = 4
            if (r0 == 0) goto L_0x0566
            r42 = r3
            org.telegram.messenger.VideoEditedInfo r3 = r11.videoEditedInfo
            if (r3 != 0) goto L_0x0551
            int r3 = r0.type
            r43 = r4
            r4 = 1
            if (r3 != r4) goto L_0x03cc
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            org.telegram.messenger.MediaController$SearchImage r0 = r11.searchImage
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r3 == 0) goto L_0x020f
            r3 = r0
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r4)
            goto L_0x023c
        L_0x020f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r3 = r11.searchImage
            java.lang.String r3 = r3.imageUrl
            java.lang.String r3 = org.telegram.messenger.Utilities.MD5(r3)
            r0.append(r3)
            r0.append(r2)
            org.telegram.messenger.MediaController$SearchImage r3 = r11.searchImage
            java.lang.String r3 = r3.imageUrl
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r3, r5)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r3.<init>(r4, r0)
            r0 = r3
            r3 = 0
        L_0x023c:
            if (r3 != 0) goto L_0x035b
            org.telegram.tgnet.TLRPC$TL_document r3 = new org.telegram.tgnet.TLRPC$TL_document
            r3.<init>()
            r44 = r12
            r12 = 0
            r3.id = r12
            r4 = 0
            byte[] r6 = new byte[r4]
            r3.file_reference = r6
            org.telegram.tgnet.ConnectionsManager r4 = r64.getConnectionsManager()
            int r4 = r4.getCurrentTime()
            r3.date = r4
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r4.<init>()
            java.lang.String r6 = "animation.gif"
            r4.file_name = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r3.attributes
            r6.add(r4)
            org.telegram.messenger.MediaController$SearchImage r4 = r11.searchImage
            int r4 = r4.size
            r3.size = r4
            r4 = 0
            r3.dc_id = r4
            java.lang.String r4 = r0.toString()
            boolean r4 = r4.endsWith(r1)
            if (r4 == 0) goto L_0x0288
            java.lang.String r4 = "video/mp4"
            r3.mime_type = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r6.<init>()
            r4.add(r6)
            goto L_0x028c
        L_0x0288:
            java.lang.String r4 = "image/gif"
            r3.mime_type = r4
        L_0x028c:
            boolean r4 = r0.exists()
            if (r4 == 0) goto L_0x0294
            r4 = r0
            goto L_0x0296
        L_0x0294:
            r0 = 0
            r4 = 0
        L_0x0296:
            if (r0 != 0) goto L_0x02cc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r6 = r11.searchImage
            java.lang.String r6 = r6.thumbUrl
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r6)
            r0.append(r6)
            r0.append(r2)
            org.telegram.messenger.MediaController$SearchImage r2 = r11.searchImage
            java.lang.String r2 = r2.thumbUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r2, r5)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r2 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r2.<init>(r5, r0)
            boolean r0 = r2.exists()
            if (r0 != 0) goto L_0x02cb
            r0 = 0
            goto L_0x02cc
        L_0x02cb:
            r0 = r2
        L_0x02cc:
            if (r0 == 0) goto L_0x0323
            if (r10 != 0) goto L_0x02db
            int r2 = r11.ttl     // Catch:{ Exception -> 0x02d8 }
            if (r2 == 0) goto L_0x02d5
            goto L_0x02db
        L_0x02d5:
            r2 = 320(0x140, float:4.48E-43)
            goto L_0x02dd
        L_0x02d8:
            r0 = move-exception
            r14 = 0
            goto L_0x031f
        L_0x02db:
            r2 = 90
        L_0x02dd:
            java.lang.String r5 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x02d8 }
            boolean r1 = r5.endsWith(r1)     // Catch:{ Exception -> 0x02d8 }
            if (r1 == 0) goto L_0x02f2
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x02d8 }
            r1 = 1
            android.graphics.Bitmap r0 = createVideoThumbnail(r0, r1)     // Catch:{ Exception -> 0x02d8 }
            r14 = 0
            goto L_0x02fd
        L_0x02f2:
            r1 = 1
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x02d8 }
            float r5 = (float) r2
            r14 = 0
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r14, r5, r5, r1)     // Catch:{ Exception -> 0x031e }
        L_0x02fd:
            if (r0 == 0) goto L_0x0324
            float r1 = (float) r2     // Catch:{ Exception -> 0x031e }
            r5 = 90
            if (r2 <= r5) goto L_0x0307
            r2 = 80
            goto L_0x0309
        L_0x0307:
            r2 = 55
        L_0x0309:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r1, r1, r2, r10)     // Catch:{ Exception -> 0x031e }
            if (r1 == 0) goto L_0x031a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r3.thumbs     // Catch:{ Exception -> 0x031e }
            r2.add(r1)     // Catch:{ Exception -> 0x031e }
            int r1 = r3.flags     // Catch:{ Exception -> 0x031e }
            r2 = 1
            r1 = r1 | r2
            r3.flags = r1     // Catch:{ Exception -> 0x031e }
        L_0x031a:
            r0.recycle()     // Catch:{ Exception -> 0x031e }
            goto L_0x0324
        L_0x031e:
            r0 = move-exception
        L_0x031f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0324
        L_0x0323:
            r14 = 0
        L_0x0324:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r3.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0356
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r1 = r11.searchImage
            int r2 = r1.width
            r0.w = r2
            int r1 = r1.height
            r0.h = r1
            r1 = 0
            r0.size = r1
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r2 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r2.<init>()
            r0.location = r2
            java.lang.String r2 = "x"
            r0.type = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r3.thumbs
            r2.add(r0)
            int r0 = r3.flags
            r9 = 1
            r0 = r0 | r9
            r3.flags = r0
            goto L_0x0358
        L_0x0356:
            r1 = 0
            r9 = 1
        L_0x0358:
            r5 = r3
            r0 = r4
            goto L_0x0363
        L_0x035b:
            r44 = r12
            r1 = 0
            r9 = 1
            r12 = 0
            r14 = 0
            r5 = r3
        L_0x0363:
            org.telegram.messenger.MediaController$SearchImage r2 = r11.searchImage
            java.lang.String r2 = r2.imageUrl
            if (r0 != 0) goto L_0x036b
            r6 = r2
            goto L_0x0370
        L_0x036b:
            java.lang.String r0 = r0.toString()
            r6 = r0
        L_0x0370:
            org.telegram.messenger.MediaController$SearchImage r0 = r11.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r0 == 0) goto L_0x0379
            r7.put(r8, r0)
        L_0x0379:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$_kIjqLoyJQqqhp4eWzR6SruuXUo r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$_kIjqLoyJQqqhp4eWzR6SruuXUo
            r8 = 0
            r4 = r40
            r2 = r0
            r45 = r42
            r3 = r67
            r47 = r4
            r46 = r43
            r4 = r64
            r48 = r37
            r34 = r12
            r13 = r25
            r12 = r10
            r49 = r36
            r9 = r62
            r1 = r14
            r14 = r11
            r11 = r68
            r1 = r12
            r25 = r44
            r12 = r69
            r50 = r13
            r13 = r14
            r51 = r16
            r36 = 73
            r14 = r70
            r52 = r15
            r15 = r71
            r2.<init>(r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            r38 = r1
            r0 = r24
            r39 = r27
            r27 = r28
            r41 = r32
            r3 = r45
            r4 = r46
            r2 = r47
            r5 = r48
            r1 = r49
            r33 = r50
            r32 = r51
            r28 = r52
            goto L_0x054b
        L_0x03cc:
            r1 = r10
            r52 = r15
            r51 = r16
            r50 = r25
            r49 = r36
            r48 = r37
            r47 = r40
            r45 = r42
            r46 = r43
            r34 = 0
            r36 = 73
            r15 = r11
            r25 = r12
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r3 == 0) goto L_0x03ee
            r3 = r0
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3
            goto L_0x03f3
        L_0x03ee:
            if (r1 != 0) goto L_0x03f2
            int r0 = r15.ttl
        L_0x03f2:
            r3 = 0
        L_0x03f3:
            if (r3 != 0) goto L_0x04be
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r4 = r15.searchImage
            java.lang.String r4 = r4.imageUrl
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)
            r0.append(r4)
            r0.append(r2)
            org.telegram.messenger.MediaController$SearchImage r4 = r15.searchImage
            java.lang.String r4 = r4.imageUrl
            java.lang.String r4 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r5)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.io.File r4 = new java.io.File
            java.io.File r10 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r4.<init>(r10, r0)
            boolean r0 = r4.exists()
            if (r0 == 0) goto L_0x043f
            long r10 = r4.length()
            int r0 = (r10 > r34 ? 1 : (r10 == r34 ? 0 : -1))
            if (r0 == 0) goto L_0x043f
            org.telegram.messenger.SendMessagesHelper r0 = r64.getSendMessagesHelper()
            java.lang.String r3 = r4.toString()
            r4 = 0
            org.telegram.tgnet.TLRPC$TL_photo r3 = r0.generatePhotoSizes(r3, r4)
            if (r3 == 0) goto L_0x043f
            r11 = 0
            goto L_0x0440
        L_0x043f:
            r11 = 1
        L_0x0440:
            if (r3 != 0) goto L_0x04bb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r4 = r15.searchImage
            java.lang.String r4 = r4.thumbUrl
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)
            r0.append(r4)
            r0.append(r2)
            org.telegram.messenger.MediaController$SearchImage r2 = r15.searchImage
            java.lang.String r2 = r2.thumbUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r2, r5)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r2 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r2.<init>(r4, r0)
            boolean r0 = r2.exists()
            if (r0 == 0) goto L_0x0481
            org.telegram.messenger.SendMessagesHelper r0 = r64.getSendMessagesHelper()
            java.lang.String r2 = r2.toString()
            r3 = 0
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r3)
            r3 = r0
        L_0x0481:
            if (r3 != 0) goto L_0x04bb
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r2 = r64.getConnectionsManager()
            int r2 = r2.getCurrentTime()
            r0.date = r2
            r13 = 0
            byte[] r2 = new byte[r13]
            r0.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize r2 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r2.<init>()
            org.telegram.messenger.MediaController$SearchImage r3 = r15.searchImage
            int r4 = r3.width
            r2.w = r4
            int r3 = r3.height
            r2.h = r3
            r2.size = r13
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r3 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r3.<init>()
            r2.location = r3
            java.lang.String r3 = "x"
            r2.type = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r0.sizes
            r3.add(r2)
            r5 = r0
            goto L_0x04c1
        L_0x04bb:
            r13 = 0
            r5 = r3
            goto L_0x04c1
        L_0x04be:
            r13 = 0
            r5 = r3
            r11 = 1
        L_0x04c1:
            if (r5 == 0) goto L_0x052b
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r15.searchImage
            java.lang.String r2 = r2.imageUrl
            if (r2 == 0) goto L_0x04d1
            r0.put(r8, r2)
        L_0x04d1:
            if (r66 == 0) goto L_0x0501
            int r2 = r24 + 1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r9)
            r9 = r32
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            r0.put(r7, r3)
            r3 = 10
            if (r2 == r3) goto L_0x04f7
            int r12 = r25 + -1
            r8 = r49
            if (r8 != r12) goto L_0x04f4
            goto L_0x04f9
        L_0x04f4:
            r24 = r2
            goto L_0x0505
        L_0x04f7:
            r8 = r49
        L_0x04f9:
            r0.put(r6, r14)
            r24 = r2
            r30 = r34
            goto L_0x0505
        L_0x0501:
            r9 = r32
            r8 = r49
        L_0x0505:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$VJegaBRp8T3xIpPQ6cVoMXtCLASSNAMEU r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$VJegaBRp8T3xIpPQ6cVoMXtCLASSNAMEU
            r12 = 0
            r2 = r16
            r3 = r67
            r4 = r64
            r6 = r11
            r7 = r15
            r15 = r8
            r8 = r0
            r10 = r9
            r9 = r12
            r53 = r10
            r10 = r62
            r12 = r68
            r17 = 0
            r13 = r69
            r14 = r70
            r55 = r15
            r15 = r71
            r2.<init>(r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r16)
            goto L_0x0531
        L_0x052b:
            r53 = r32
            r55 = r49
            r17 = 0
        L_0x0531:
            r38 = r1
            r0 = r24
            r39 = r27
            r27 = r28
            r3 = r45
            r4 = r46
            r2 = r47
            r5 = r48
            r33 = r50
            r32 = r51
            r28 = r52
            r41 = r53
            r1 = r55
        L_0x054b:
            r22 = 0
            r37 = 0
            goto L_0x0d6a
        L_0x0551:
            r0 = r1
            r46 = r4
            r1 = r10
            r52 = r15
            r51 = r16
            r50 = r25
            r53 = r32
            r55 = r36
            r48 = r37
            r47 = r40
            r45 = r42
            goto L_0x057a
        L_0x0566:
            r0 = r1
            r45 = r3
            r46 = r4
            r1 = r10
            r52 = r15
            r51 = r16
            r50 = r25
            r53 = r32
            r55 = r36
            r48 = r37
            r47 = r40
        L_0x057a:
            r10 = 90
            r13 = 0
            r34 = 0
            r36 = 73
            r15 = r11
            r25 = r12
            boolean r3 = r15.isVideo
            if (r3 != 0) goto L_0x0974
            org.telegram.messenger.VideoEditedInfo r3 = r15.videoEditedInfo
            if (r3 == 0) goto L_0x058e
            goto L_0x0974
        L_0x058e:
            java.lang.String r0 = r15.path
            if (r0 != 0) goto L_0x05a2
            android.net.Uri r2 = r15.uri
            if (r2 == 0) goto L_0x05a2
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r2)
            android.net.Uri r2 = r15.uri
            java.lang.String r2 = r2.toString()
            r3 = r0
            goto L_0x05a4
        L_0x05a2:
            r2 = r0
            r3 = r2
        L_0x05a4:
            if (r72 == 0) goto L_0x0638
            android.net.Uri r0 = r15.uri
            if (r0 == 0) goto L_0x0638
            android.content.ClipDescription r0 = r72.getDescription()
            java.lang.String r4 = "image/png"
            boolean r0 = r0.hasMimeType(r4)
            if (r0 == 0) goto L_0x0638
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0617 }
            r0.<init>()     // Catch:{ all -> 0x0617 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0617 }
            android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x0617 }
            android.net.Uri r5 = r15.uri     // Catch:{ all -> 0x0617 }
            java.io.InputStream r4 = r4.openInputStream(r5)     // Catch:{ all -> 0x0617 }
            r5 = 0
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r4, r5, r0)     // Catch:{ all -> 0x0613 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0613 }
            r5.<init>()     // Catch:{ all -> 0x0613 }
            java.lang.String r10 = "-2147483648_"
            r5.append(r10)     // Catch:{ all -> 0x0613 }
            int r10 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ all -> 0x0613 }
            r5.append(r10)     // Catch:{ all -> 0x0613 }
            r12 = r28
            r5.append(r12)     // Catch:{ all -> 0x0611 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0611 }
            java.io.File r10 = org.telegram.messenger.FileLoader.getDirectory(r41)     // Catch:{ all -> 0x0611 }
            java.io.File r11 = new java.io.File     // Catch:{ all -> 0x0611 }
            r11.<init>(r10, r5)     // Catch:{ all -> 0x0611 }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ all -> 0x0611 }
            r5.<init>(r11)     // Catch:{ all -> 0x0611 }
            android.graphics.Bitmap$CompressFormat r10 = android.graphics.Bitmap.CompressFormat.WEBP     // Catch:{ all -> 0x060f }
            r13 = 100
            r0.compress(r10, r13, r5)     // Catch:{ all -> 0x060f }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ all -> 0x060f }
            android.net.Uri r0 = android.net.Uri.fromFile(r11)     // Catch:{ all -> 0x060f }
            r15.uri = r0     // Catch:{ all -> 0x060f }
            if (r4 == 0) goto L_0x0609
            r4.close()     // Catch:{ Exception -> 0x0609 }
        L_0x0609:
            r5.close()     // Catch:{ Exception -> 0x060d }
            goto L_0x063a
        L_0x060d:
            goto L_0x063a
        L_0x060f:
            r0 = move-exception
            goto L_0x061c
        L_0x0611:
            r0 = move-exception
            goto L_0x061b
        L_0x0613:
            r0 = move-exception
            r12 = r28
            goto L_0x061b
        L_0x0617:
            r0 = move-exception
            r12 = r28
            r4 = 0
        L_0x061b:
            r5 = 0
        L_0x061c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0629 }
            if (r4 == 0) goto L_0x0626
            r4.close()     // Catch:{ Exception -> 0x0625 }
            goto L_0x0626
        L_0x0625:
        L_0x0626:
            if (r5 == 0) goto L_0x063a
            goto L_0x0609
        L_0x0629:
            r0 = move-exception
            r1 = r0
            if (r4 == 0) goto L_0x0632
            r4.close()     // Catch:{ Exception -> 0x0631 }
            goto L_0x0632
        L_0x0631:
        L_0x0632:
            if (r5 == 0) goto L_0x0637
            r5.close()     // Catch:{ Exception -> 0x0637 }
        L_0x0637:
            throw r1
        L_0x0638:
            r12 = r28
        L_0x063a:
            java.lang.String r0 = "gif"
            java.lang.String r4 = "webp"
            if (r65 != 0) goto L_0x069c
            java.lang.String r5 = r15.path
            android.net.Uri r10 = r15.uri
            boolean r5 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r5, r10)
            if (r5 == 0) goto L_0x064c
            goto L_0x069c
        L_0x064c:
            r13 = r51
            if (r3 == 0) goto L_0x0666
            boolean r5 = r3.endsWith(r13)
            if (r5 != 0) goto L_0x065c
            boolean r5 = r3.endsWith(r12)
            if (r5 == 0) goto L_0x0666
        L_0x065c:
            boolean r5 = r3.endsWith(r13)
            if (r5 == 0) goto L_0x0663
            goto L_0x067e
        L_0x0663:
            r23 = r4
            goto L_0x06ac
        L_0x0666:
            if (r3 != 0) goto L_0x0699
            android.net.Uri r5 = r15.uri
            if (r5 == 0) goto L_0x0699
            boolean r5 = org.telegram.messenger.MediaController.isGif(r5)
            if (r5 == 0) goto L_0x0681
            android.net.Uri r2 = r15.uri
            java.lang.String r2 = r2.toString()
            android.net.Uri r3 = r15.uri
            java.lang.String r3 = org.telegram.messenger.MediaController.copyFileToCache(r3, r0)
        L_0x067e:
            r23 = r0
            goto L_0x06ac
        L_0x0681:
            android.net.Uri r0 = r15.uri
            boolean r0 = org.telegram.messenger.MediaController.isWebp(r0)
            if (r0 == 0) goto L_0x0699
            android.net.Uri r0 = r15.uri
            java.lang.String r2 = r0.toString()
            android.net.Uri r0 = r15.uri
            java.lang.String r3 = org.telegram.messenger.MediaController.copyFileToCache(r0, r4)
            r10 = r3
            r23 = r4
            goto L_0x06ad
        L_0x0699:
            r10 = r3
            r11 = 0
            goto L_0x06ae
        L_0x069c:
            r13 = r51
            if (r3 == 0) goto L_0x06aa
            java.io.File r0 = new java.io.File
            r0.<init>(r3)
            java.lang.String r0 = org.telegram.messenger.FileLoader.getFileExtension(r0)
            goto L_0x067e
        L_0x06aa:
            r23 = r9
        L_0x06ac:
            r10 = r3
        L_0x06ad:
            r11 = 1
        L_0x06ae:
            if (r11 == 0) goto L_0x0709
            r11 = r48
            if (r11 != 0) goto L_0x06d0
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r29 = new java.util.ArrayList
            r29.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r6 = r29
            goto L_0x06d9
        L_0x06d0:
            r5 = r11
            r6 = r29
            r3 = r45
            r4 = r46
            r0 = r47
        L_0x06d9:
            r5.add(r10)
            r4.add(r2)
            android.net.Uri r2 = r15.uri
            r3.add(r2)
            java.lang.String r2 = r15.caption
            r0.add(r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r15.entities
            r6.add(r2)
            r2 = r0
            r38 = r1
            r29 = r6
            r32 = r13
            r0 = r24
            r39 = r27
            r33 = r50
            r28 = r52
            r41 = r53
            r1 = r55
            r22 = 0
            r37 = 0
            r27 = r12
            goto L_0x0d6a
        L_0x0709:
            r11 = r48
            if (r10 == 0) goto L_0x0735
            java.io.File r0 = new java.io.File
            r0.<init>(r10)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            long r4 = r0.length()
            r3.append(r4)
            r5 = r27
            r3.append(r5)
            long r4 = r0.lastModified()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r5 = r3
            r4 = r52
            goto L_0x0738
        L_0x0735:
            r4 = r52
            r5 = 0
        L_0x0738:
            if (r4 == 0) goto L_0x0766
            java.lang.Object r0 = r4.get(r15)
            r2 = r0
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
            if (r0 != 0) goto L_0x0755
            java.util.concurrent.CountDownLatch r0 = r2.sync     // Catch:{ Exception -> 0x074d }
            r0.await()     // Catch:{ Exception -> 0x074d }
            goto L_0x0751
        L_0x074d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0751:
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
        L_0x0755:
            r28 = r4
            r48 = r11
            r18 = r12
            r51 = r13
            r56 = r27
            r11 = r5
            r12 = r6
            r13 = r7
            r7 = r0
            r6 = r3
            goto L_0x081c
        L_0x0766:
            if (r1 != 0) goto L_0x07e9
            int r0 = r15.ttl
            if (r0 != 0) goto L_0x07e9
            org.telegram.messenger.MessagesStorage r0 = r64.getMessagesStorage()
            if (r1 != 0) goto L_0x0774
            r2 = 0
            goto L_0x0775
        L_0x0774:
            r2 = 3
        L_0x0775:
            java.lang.Object[] r0 = r0.getSentFile(r5, r2)
            if (r0 == 0) goto L_0x078c
            r2 = 0
            r3 = r0[r2]
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r3 == 0) goto L_0x078c
            r3 = r0[r2]
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3
            r2 = 1
            r0 = r0[r2]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x078f
        L_0x078c:
            r2 = 1
            r0 = 0
            r3 = 0
        L_0x078f:
            if (r3 != 0) goto L_0x07be
            android.net.Uri r2 = r15.uri
            if (r2 == 0) goto L_0x07c3
            org.telegram.messenger.MessagesStorage r2 = r64.getMessagesStorage()
            r16 = r0
            android.net.Uri r0 = r15.uri
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r0)
            r17 = r3
            if (r1 != 0) goto L_0x07a7
            r3 = 0
            goto L_0x07a8
        L_0x07a7:
            r3 = 3
        L_0x07a8:
            java.lang.Object[] r0 = r2.getSentFile(r0, r3)
            if (r0 == 0) goto L_0x07c7
            r2 = 0
            r3 = r0[r2]
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r3 == 0) goto L_0x07c7
            r3 = r0[r2]
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3
            r2 = 1
            r0 = r0[r2]
            java.lang.String r0 = (java.lang.String) r0
        L_0x07be:
            r16 = r0
            r17 = r3
            goto L_0x07c8
        L_0x07c3:
            r16 = r0
            r17 = r3
        L_0x07c7:
            r2 = 1
        L_0x07c8:
            java.lang.String r0 = r15.path
            android.net.Uri r3 = r15.uri
            r32 = 0
            r2 = r1
            r18 = r3
            r3 = r17
            r28 = r4
            r4 = r0
            r48 = r11
            r56 = r27
            r11 = r5
            r5 = r18
            r18 = r12
            r51 = r13
            r12 = r6
            r13 = r7
            r6 = r32
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            goto L_0x07f9
        L_0x07e9:
            r28 = r4
            r48 = r11
            r18 = r12
            r51 = r13
            r56 = r27
            r11 = r5
            r12 = r6
            r13 = r7
            r3 = 0
            r16 = 0
        L_0x07f9:
            if (r3 != 0) goto L_0x0819
            org.telegram.messenger.SendMessagesHelper r0 = r64.getSendMessagesHelper()
            java.lang.String r2 = r15.path
            android.net.Uri r3 = r15.uri
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r3)
            if (r1 == 0) goto L_0x0817
            boolean r2 = r15.canDeleteAfter
            if (r2 == 0) goto L_0x0817
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r15.path
            r2.<init>(r3)
            r2.delete()
        L_0x0817:
            r7 = r0
            goto L_0x081a
        L_0x0819:
            r7 = r3
        L_0x081a:
            r6 = r16
        L_0x081c:
            if (r7 == 0) goto L_0x091c
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            r5 = 1
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r5]
            java.lang.String[] r4 = new java.lang.String[r5]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r0 = r15.masks
            if (r0 == 0) goto L_0x0834
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0834
            r0 = 1
            goto L_0x0835
        L_0x0834:
            r0 = 0
        L_0x0835:
            r7.has_stickers = r0
            if (r0 == 0) goto L_0x0878
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
        L_0x0852:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r15.masks
            int r5 = r5.size()
            if (r2 >= r5) goto L_0x0868
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r15.masks
            java.lang.Object r5 = r5.get(r2)
            org.telegram.tgnet.TLRPC$InputDocument r5 = (org.telegram.tgnet.TLRPC$InputDocument) r5
            r5.serializeToStream(r0)
            int r2 = r2 + 1
            goto L_0x0852
        L_0x0868:
            byte[] r2 = r0.toByteArray()
            java.lang.String r2 = org.telegram.messenger.Utilities.bytesToHex(r2)
            java.lang.String r5 = "masks"
            r10.put(r5, r2)
            r0.cleanup()
        L_0x0878:
            if (r11 == 0) goto L_0x087d
            r10.put(r8, r11)
        L_0x087d:
            if (r6 == 0) goto L_0x0884
            java.lang.String r0 = "parentObject"
            r10.put(r0, r6)
        L_0x0884:
            if (r66 == 0) goto L_0x0893
            int r0 = r61.size()     // Catch:{ Exception -> 0x0890 }
            r5 = 1
            if (r0 != r5) goto L_0x088e
            goto L_0x0894
        L_0x088e:
            r11 = 0
            goto L_0x08af
        L_0x0890:
            r0 = move-exception
            r5 = 1
            goto L_0x08ab
        L_0x0893:
            r5 = 1
        L_0x0894:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r7.sizes     // Catch:{ Exception -> 0x08aa }
            int r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize()     // Catch:{ Exception -> 0x08aa }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2)     // Catch:{ Exception -> 0x08aa }
            if (r0 == 0) goto L_0x088e
            r11 = 0
            java.lang.String r0 = getKeyForPhotoSize(r0, r3, r11, r11)     // Catch:{ Exception -> 0x08a8 }
            r4[r11] = r0     // Catch:{ Exception -> 0x08a8 }
            goto L_0x08af
        L_0x08a8:
            r0 = move-exception
            goto L_0x08ac
        L_0x08aa:
            r0 = move-exception
        L_0x08ab:
            r11 = 0
        L_0x08ac:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x08af:
            if (r66 == 0) goto L_0x08df
            int r0 = r24 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r9)
            r8 = r53
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            r10.put(r13, r2)
            r2 = 10
            if (r0 == r2) goto L_0x08d5
            int r2 = r25 + -1
            r13 = r55
            if (r13 != r2) goto L_0x08d2
            goto L_0x08d7
        L_0x08d2:
            r24 = r0
            goto L_0x08e3
        L_0x08d5:
            r13 = r55
        L_0x08d7:
            r10.put(r12, r14)
            r24 = r0
            r30 = r34
            goto L_0x08e3
        L_0x08df:
            r8 = r53
            r13 = r55
        L_0x08e3:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$CPnKeQCwbM37D9aqShGZgET6tjk r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$CPnKeQCwbM37D9aqShGZgET6tjk
            r2 = r0
            r14 = 1
            r5 = r67
            r16 = r6
            r6 = r64
            r57 = r8
            r8 = r10
            r9 = r16
            r12 = r48
            r16 = 0
            r10 = r62
            r22 = r1
            r1 = r12
            r27 = r18
            r12 = r68
            r59 = r13
            r32 = r51
            r13 = r69
            r14 = r15
            r15 = r70
            r16 = r71
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            r5 = r1
            r38 = r22
            r0 = r24
            r3 = r45
            r4 = r46
            r2 = r47
            goto L_0x096a
        L_0x091c:
            r22 = r1
            r27 = r18
            r1 = r48
            r32 = r51
            r57 = r53
            r59 = r55
            if (r1 != 0) goto L_0x0946
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r29 = new java.util.ArrayList
            r29.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0 = r29
            goto L_0x094f
        L_0x0946:
            r5 = r1
            r0 = r29
            r3 = r45
            r4 = r46
            r2 = r47
        L_0x094f:
            r5.add(r10)
            r4.add(r11)
            android.net.Uri r1 = r15.uri
            r3.add(r1)
            java.lang.String r1 = r15.caption
            r2.add(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r15.entities
            r0.add(r1)
            r29 = r0
            r38 = r22
            r0 = r24
        L_0x096a:
            r33 = r50
            r39 = r56
            r41 = r57
            r1 = r59
            goto L_0x054b
        L_0x0974:
            r22 = r1
            r12 = r6
            r13 = r7
            r56 = r27
            r27 = r28
            r1 = r48
            r32 = r51
            r28 = r52
            r57 = r53
            r59 = r55
            if (r65 == 0) goto L_0x098a
            r11 = 0
            goto L_0x0996
        L_0x098a:
            org.telegram.messenger.VideoEditedInfo r3 = r15.videoEditedInfo
            if (r3 == 0) goto L_0x098f
            goto L_0x0995
        L_0x098f:
            java.lang.String r3 = r15.path
            org.telegram.messenger.VideoEditedInfo r3 = createCompressionSettings(r3)
        L_0x0995:
            r11 = r3
        L_0x0996:
            if (r65 != 0) goto L_0x0d33
            if (r11 != 0) goto L_0x09b6
            java.lang.String r3 = r15.path
            boolean r0 = r3.endsWith(r0)
            if (r0 == 0) goto L_0x09a3
            goto L_0x09b6
        L_0x09a3:
            r48 = r1
            r2 = r15
            r38 = r22
            r33 = r50
            r39 = r56
            r41 = r57
            r1 = r59
            r22 = 0
            r37 = 0
            goto L_0x0d44
        L_0x09b6:
            java.lang.String r0 = r15.path
            if (r0 != 0) goto L_0x0a03
            org.telegram.messenger.MediaController$SearchImage r0 = r15.searchImage
            if (r0 == 0) goto L_0x0a03
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r3 == 0) goto L_0x09d0
            r6 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r6)
            java.lang.String r0 = r0.getAbsolutePath()
            r15.path = r0
            goto L_0x0a04
        L_0x09d0:
            r6 = 1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r3 = r15.searchImage
            java.lang.String r3 = r3.imageUrl
            java.lang.String r3 = org.telegram.messenger.Utilities.MD5(r3)
            r0.append(r3)
            r0.append(r2)
            org.telegram.messenger.MediaController$SearchImage r2 = r15.searchImage
            java.lang.String r2 = r2.imageUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r2, r5)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r2 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r2.<init>(r3, r0)
            java.lang.String r0 = r2.getAbsolutePath()
            r15.path = r0
            goto L_0x0a04
        L_0x0a03:
            r6 = 1
        L_0x0a04:
            java.lang.String r0 = r15.path
            java.io.File r7 = new java.io.File
            r7.<init>(r0)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            long r3 = r7.length()
            r2.append(r3)
            r5 = r56
            r2.append(r5)
            long r3 = r7.lastModified()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            if (r11 == 0) goto L_0x0a84
            boolean r3 = r11.muted
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r16 = r7
            long r6 = r11.estimatedDuration
            r4.append(r6)
            r4.append(r5)
            long r6 = r11.startTime
            r4.append(r6)
            r4.append(r5)
            long r6 = r11.endTime
            r4.append(r6)
            boolean r2 = r11.muted
            if (r2 == 0) goto L_0x0a54
            java.lang.String r2 = "_m"
            goto L_0x0a55
        L_0x0a54:
            r2 = r9
        L_0x0a55:
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            int r4 = r11.resultWidth
            int r6 = r11.originalWidth
            if (r4 == r6) goto L_0x0a76
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r4.append(r5)
            int r2 = r11.resultWidth
            r4.append(r2)
            java.lang.String r2 = r4.toString()
        L_0x0a76:
            long r6 = r11.startTime
            int r4 = (r6 > r34 ? 1 : (r6 == r34 ? 0 : -1))
            if (r4 < 0) goto L_0x0a7d
            goto L_0x0a7f
        L_0x0a7d:
            r6 = r34
        L_0x0a7f:
            r17 = r3
            r3 = r6
            r6 = r2
            goto L_0x0a8b
        L_0x0a84:
            r16 = r7
            r6 = r2
            r3 = r34
            r17 = 0
        L_0x0a8b:
            if (r22 != 0) goto L_0x0ae7
            int r2 = r15.ttl
            if (r2 != 0) goto L_0x0ae7
            if (r11 == 0) goto L_0x0aa3
            org.telegram.messenger.MediaController$SavedFilterState r2 = r11.filterState
            if (r2 != 0) goto L_0x0ae7
            java.lang.String r2 = r11.paintPath
            if (r2 != 0) goto L_0x0ae7
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r2 = r11.mediaEntities
            if (r2 != 0) goto L_0x0ae7
            org.telegram.messenger.MediaController$CropState r2 = r11.cropState
            if (r2 != 0) goto L_0x0ae7
        L_0x0aa3:
            org.telegram.messenger.MessagesStorage r2 = r64.getMessagesStorage()
            if (r22 != 0) goto L_0x0aab
            r7 = 2
            goto L_0x0aac
        L_0x0aab:
            r7 = 5
        L_0x0aac:
            java.lang.Object[] r2 = r2.getSentFile(r6, r7)
            if (r2 == 0) goto L_0x0ae7
            r7 = 0
            r10 = r2[r7]
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r10 == 0) goto L_0x0ae7
            r10 = r2[r7]
            org.telegram.tgnet.TLRPC$TL_document r10 = (org.telegram.tgnet.TLRPC$TL_document) r10
            r18 = 1
            r2 = r2[r18]
            r33 = r2
            java.lang.String r33 = (java.lang.String) r33
            java.lang.String r2 = r15.path
            r37 = 0
            r40 = r2
            r2 = r22
            r42 = r3
            r3 = r10
            r4 = r40
            r40 = r0
            r39 = r5
            r0 = 90
            r5 = r37
            r48 = r1
            r60 = r6
            r1 = 1
            r6 = r42
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r10 = r33
            goto L_0x0af6
        L_0x0ae7:
            r40 = r0
            r48 = r1
            r42 = r3
            r39 = r5
            r60 = r6
            r0 = 90
            r1 = 1
            r3 = 0
            r10 = 0
        L_0x0af6:
            if (r3 != 0) goto L_0x0bff
            java.lang.String r2 = r15.thumbPath
            if (r2 == 0) goto L_0x0b01
            android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeFile(r2)
            goto L_0x0b02
        L_0x0b01:
            r3 = 0
        L_0x0b02:
            if (r3 != 0) goto L_0x0b14
            java.lang.String r2 = r15.path
            r6 = r42
            android.graphics.Bitmap r3 = createVideoThumbnailAtTime(r2, r6)
            if (r3 != 0) goto L_0x0b14
            java.lang.String r2 = r15.path
            android.graphics.Bitmap r3 = createVideoThumbnail(r2, r1)
        L_0x0b14:
            if (r3 == 0) goto L_0x0b41
            if (r22 != 0) goto L_0x0b2a
            int r2 = r15.ttl
            if (r2 == 0) goto L_0x0b1d
            goto L_0x0b2a
        L_0x0b1d:
            int r2 = r3.getWidth()
            int r4 = r3.getHeight()
            int r5 = java.lang.Math.max(r2, r4)
            goto L_0x0b2c
        L_0x0b2a:
            r5 = 90
        L_0x0b2c:
            float r2 = (float) r5
            if (r5 <= r0) goto L_0x0b32
            r4 = 80
            goto L_0x0b34
        L_0x0b32:
            r4 = 55
        L_0x0b34:
            r7 = r22
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r3, r2, r2, r4, r7)
            r4 = 0
            r5 = 0
            java.lang.String r6 = getKeyForPhotoSize(r2, r4, r1, r5)
            goto L_0x0b46
        L_0x0b41:
            r7 = r22
            r5 = 0
            r2 = 0
            r6 = 0
        L_0x0b46:
            org.telegram.tgnet.TLRPC$TL_document r4 = new org.telegram.tgnet.TLRPC$TL_document
            r4.<init>()
            byte[] r0 = new byte[r5]
            r4.file_reference = r0
            if (r2 == 0) goto L_0x0b5b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r4.thumbs
            r0.add(r2)
            int r0 = r4.flags
            r0 = r0 | r1
            r4.flags = r0
        L_0x0b5b:
            java.lang.String r0 = "video/mp4"
            r4.mime_type = r0
            org.telegram.messenger.UserConfig r0 = r64.getUserConfig()
            r5 = 0
            r0.saveConfig(r5)
            if (r7 == 0) goto L_0x0b7b
            r0 = 66
            r2 = r50
            if (r2 < r0) goto L_0x0b75
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r0.<init>()
            goto L_0x0b84
        L_0x0b75:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r0.<init>()
            goto L_0x0b84
        L_0x0b7b:
            r2 = r50
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r0.<init>()
            r0.supports_streaming = r1
        L_0x0b84:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r4.attributes
            r1.add(r0)
            if (r11 == 0) goto L_0x0be4
            boolean r1 = r11.needConvert()
            if (r1 != 0) goto L_0x0b95
            boolean r1 = r15.isVideo
            if (r1 != 0) goto L_0x0be4
        L_0x0b95:
            boolean r1 = r15.isVideo
            if (r1 == 0) goto L_0x0bac
            boolean r1 = r11.muted
            if (r1 == 0) goto L_0x0bac
            java.lang.String r1 = r15.path
            fillVideoAttribute(r1, r0, r11)
            int r1 = r0.w
            r11.originalWidth = r1
            int r1 = r0.h
            r11.originalHeight = r1
            r1 = r6
            goto L_0x0bb6
        L_0x0bac:
            r1 = r6
            long r5 = r11.estimatedDuration
            r42 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r42
            int r6 = (int) r5
            r0.duration = r6
        L_0x0bb6:
            int r5 = r11.rotationValue
            org.telegram.messenger.MediaController$CropState r6 = r11.cropState
            if (r6 == 0) goto L_0x0bc3
            r33 = r1
            int r1 = r6.transformWidth
            int r6 = r6.transformHeight
            goto L_0x0bc9
        L_0x0bc3:
            r33 = r1
            int r1 = r11.resultWidth
            int r6 = r11.resultHeight
        L_0x0bc9:
            r50 = r2
            r2 = 90
            if (r5 == r2) goto L_0x0bd9
            r2 = 270(0x10e, float:3.78E-43)
            if (r5 != r2) goto L_0x0bd4
            goto L_0x0bd9
        L_0x0bd4:
            r0.w = r1
            r0.h = r6
            goto L_0x0bdd
        L_0x0bd9:
            r0.w = r6
            r0.h = r1
        L_0x0bdd:
            long r0 = r11.estimatedSize
            int r1 = (int) r0
            r4.size = r1
            r6 = 0
            goto L_0x0bfb
        L_0x0be4:
            r50 = r2
            r33 = r6
            boolean r1 = r16.exists()
            if (r1 == 0) goto L_0x0bf5
            long r1 = r16.length()
            int r2 = (int) r1
            r4.size = r2
        L_0x0bf5:
            java.lang.String r1 = r15.path
            r6 = 0
            fillVideoAttribute(r1, r0, r6)
        L_0x0bfb:
            r0 = r4
            r4 = r33
            goto L_0x0CLASSNAME
        L_0x0bff:
            r7 = r22
            r6 = 0
            r0 = r3
            r3 = r6
            r4 = r3
        L_0x0CLASSNAME:
            if (r11 == 0) goto L_0x0CLASSNAME
            boolean r1 = r11.muted
            if (r1 == 0) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r0.attributes
            int r1 = r1.size()
            r2 = 0
        L_0x0CLASSNAME:
            if (r2 >= r1) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r0.attributes
            java.lang.Object r5 = r5.get(r2)
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            if (r5 == 0) goto L_0x0CLASSNAME
            r1 = 1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            int r2 = r2 + 1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1 = 0
        L_0x0CLASSNAME:
            if (r1 != 0) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r0.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r2.<init>()
            r1.add(r2)
        L_0x0CLASSNAME:
            if (r11 == 0) goto L_0x0CLASSNAME
            boolean r1 = r11.needConvert()
            if (r1 != 0) goto L_0x0c3c
            boolean r1 = r15.isVideo
            if (r1 != 0) goto L_0x0CLASSNAME
        L_0x0c3c:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "-2147483648_"
            r1.append(r2)
            int r2 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r1.append(r2)
            java.lang.String r2 = ".mp4"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.io.File r2 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r2.<init>(r5, r1)
            org.telegram.messenger.SharedConfig.saveConfig()
            java.lang.String r1 = r2.getAbsolutePath()
            r40 = r1
        L_0x0CLASSNAME:
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r2 = r60
            if (r2 == 0) goto L_0x0CLASSNAME
            r1.put(r8, r2)
        L_0x0CLASSNAME:
            if (r10 == 0) goto L_0x0c7b
            java.lang.String r2 = "parentObject"
            r1.put(r2, r10)
        L_0x0c7b:
            if (r17 != 0) goto L_0x0cad
            if (r66 == 0) goto L_0x0cad
            int r2 = r24 + 1
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r9)
            r8 = r57
            r5.append(r8)
            java.lang.String r5 = r5.toString()
            r1.put(r13, r5)
            r5 = 10
            if (r2 == r5) goto L_0x0ca3
            int r5 = r25 + -1
            r13 = r59
            if (r13 != r5) goto L_0x0ca0
            goto L_0x0ca5
        L_0x0ca0:
            r24 = r2
            goto L_0x0cb1
        L_0x0ca3:
            r13 = r59
        L_0x0ca5:
            r1.put(r12, r14)
            r24 = r2
            r30 = r34
            goto L_0x0cb1
        L_0x0cad:
            r8 = r57
            r13 = r59
        L_0x0cb1:
            if (r7 != 0) goto L_0x0d06
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r15.masks
            if (r2 == 0) goto L_0x0d06
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0d06
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r0.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers
            r5.<init>()
            r2.add(r5)
            org.telegram.tgnet.SerializedData r2 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r15.masks
            int r5 = r5.size()
            int r5 = r5 * 20
            int r5 = r5 + 4
            r2.<init>((int) r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r15.masks
            int r5 = r5.size()
            r2.writeInt32(r5)
            r5 = 0
        L_0x0ce0:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r12 = r15.masks
            int r12 = r12.size()
            if (r5 >= r12) goto L_0x0cf6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r12 = r15.masks
            java.lang.Object r12 = r12.get(r5)
            org.telegram.tgnet.TLRPC$InputDocument r12 = (org.telegram.tgnet.TLRPC$InputDocument) r12
            r12.serializeToStream(r2)
            int r5 = r5 + 1
            goto L_0x0ce0
        L_0x0cf6:
            byte[] r5 = r2.toByteArray()
            java.lang.String r5 = org.telegram.messenger.Utilities.bytesToHex(r5)
            java.lang.String r12 = "masks"
            r1.put(r12, r5)
            r2.cleanup()
        L_0x0d06:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$R0VhnMLYoWNPv0gz0ondjjQIKK8 r26 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$R0VhnMLYoWNPv0gz0ondjjQIKK8
            r33 = r50
            r2 = r26
            r22 = 0
            r5 = r67
            r37 = r6
            r6 = r64
            r38 = r7
            r7 = r11
            r41 = r8
            r8 = r0
            r9 = r40
            r0 = r10
            r10 = r1
            r11 = r0
            r1 = r13
            r12 = r62
            r14 = r68
            r16 = r15
            r15 = r69
            r17 = r70
            r18 = r71
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r26)
            goto L_0x0d60
        L_0x0d33:
            r48 = r1
            r38 = r22
            r33 = r50
            r39 = r56
            r41 = r57
            r1 = r59
            r22 = 0
            r37 = 0
            r2 = r15
        L_0x0d44:
            java.lang.String r4 = r2.path
            r5 = 0
            r6 = 0
            java.lang.String r11 = r2.caption
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r12 = r2.entities
            r2 = r64
            r3 = r4
            r7 = r62
            r9 = r68
            r10 = r69
            r13 = r67
            r14 = r65
            r15 = r70
            r16 = r71
            prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15, r16)
        L_0x0d60:
            r0 = r24
            r3 = r45
            r4 = r46
            r2 = r47
            r5 = r48
        L_0x0d6a:
            int r9 = r1 + 1
            r1 = r61
            r13 = r64
            r12 = r25
            r15 = r28
            r16 = r32
            r8 = r33
            r11 = r37
            r10 = r38
            r24 = r41
            r14 = 73
            r28 = r27
            r27 = r39
            goto L_0x01a9
        L_0x0d86:
            r47 = r2
            r45 = r3
            r46 = r4
            r48 = r5
            r6 = r30
            r22 = 0
            r34 = 0
            int r0 = (r6 > r34 ? 1 : (r6 == r34 ? 0 : -1))
            if (r0 == 0) goto L_0x0da5
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$_L_bcA45NcWWpyRplQ69TB7aHtY r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$_L_bcA45NcWWpyRplQ69TB7aHtY
            r15 = r64
            r14 = r71
            r0.<init>(r6, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0da9
        L_0x0da5:
            r15 = r64
            r14 = r71
        L_0x0da9:
            if (r72 == 0) goto L_0x0dae
            r72.releasePermission()
        L_0x0dae:
            if (r48 == 0) goto L_0x0e19
            boolean r0 = r48.isEmpty()
            if (r0 != 0) goto L_0x0e19
            r0 = 0
        L_0x0db7:
            int r1 = r48.size()
            if (r0 >= r1) goto L_0x0e19
            r5 = r48
            java.lang.Object r1 = r5.get(r0)
            r2 = r1
            java.lang.String r2 = (java.lang.String) r2
            r13 = r46
            java.lang.Object r1 = r13.get(r0)
            r3 = r1
            java.lang.String r3 = (java.lang.String) r3
            r12 = r45
            java.lang.Object r1 = r12.get(r0)
            r4 = r1
            android.net.Uri r4 = (android.net.Uri) r4
            r11 = r47
            java.lang.Object r1 = r11.get(r0)
            r10 = r1
            java.lang.CharSequence r10 = (java.lang.CharSequence) r10
            r9 = r29
            java.lang.Object r1 = r9.get(r0)
            r16 = r1
            java.util.ArrayList r16 = (java.util.ArrayList) r16
            r17 = r5
            r1 = r64
            r5 = r23
            r6 = r62
            r8 = r68
            r9 = r69
            r18 = r11
            r11 = r16
            r16 = r12
            r12 = r67
            r21 = r13
            r13 = r65
            r14 = r70
            r15 = r71
            prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14, r15)
            int r0 = r0 + 1
            r15 = r64
            r14 = r71
            r45 = r16
            r48 = r17
            r47 = r18
            r46 = r21
            goto L_0x0db7
        L_0x0e19:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0e37
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "total send time = "
            r0.append(r1)
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r19
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0e37:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$79(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, boolean, boolean, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    static /* synthetic */ void lambda$null$73(MediaSendPrepareWorker mediaSendPrepareWorker, AccountInstance accountInstance, SendingMediaInfo sendingMediaInfo, boolean z) {
        mediaSendPrepareWorker.photo = accountInstance.getSendMessagesHelper().generatePhotoSizes(sendingMediaInfo.path, sendingMediaInfo.uri);
        if (z && sendingMediaInfo.canDeleteAfter) {
            new File(sendingMediaInfo.path).delete();
        }
        mediaSendPrepareWorker.sync.countDown();
    }

    static /* synthetic */ void lambda$null$74(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, str, hashMap, false, str2);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str2);
    }

    static /* synthetic */ void lambda$null$75(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, boolean z2, int i) {
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
        sendMessagesHelper2.sendMessage(tLRPC$TL_photo, str2, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z2, i, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$76(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
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
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, sendingMediaInfo2.ttl, str3);
    }

    static /* synthetic */ void lambda$null$77(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, tLRPC$TL_photo, (VideoEditedInfo) null, (TLRPC$TL_document) null, (String) null, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_photo, (String) null, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$78(AccountInstance accountInstance, long j, int i) {
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

    public static Bitmap createVideoThumbnail(String str, int i) {
        float f = i == 2 ? 1920.0f : i == 3 ? 96.0f : 512.0f;
        Bitmap createVideoThumbnailAtTime = createVideoThumbnailAtTime(str, 0);
        if (createVideoThumbnailAtTime == null) {
            return createVideoThumbnailAtTime;
        }
        int width = createVideoThumbnailAtTime.getWidth();
        int height = createVideoThumbnailAtTime.getHeight();
        float f2 = (float) width;
        if (f2 <= f && ((float) height) <= f) {
            return createVideoThumbnailAtTime;
        }
        float max = ((float) Math.max(width, height)) / f;
        return Bitmap.createScaledBitmap(createVideoThumbnailAtTime, (int) (f2 / max), (int) (((float) height) / max), true);
    }

    public static Bitmap createVideoThumbnailAtTime(String str, long j) {
        return createVideoThumbnailAtTime(str, j, (int[]) null, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r3.release();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004d A[ExcHandler: all (r0v1 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:7:0x0039] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap createVideoThumbnailAtTime(java.lang.String r16, long r17, int[] r19, boolean r20) {
        /*
            r0 = r16
            r1 = r17
            r3 = r20
            if (r3 == 0) goto L_0x0033
            org.telegram.ui.Components.AnimatedFileDrawable r15 = new org.telegram.ui.Components.AnimatedFileDrawable
            java.io.File r5 = new java.io.File
            r5.<init>(r0)
            r6 = 1
            r7 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r14 = 0
            r0 = 1
            r4 = r15
            r1 = r15
            r15 = r0
            r4.<init>(r5, r6, r7, r9, r10, r11, r12, r14, r15)
            r0 = r1
            r1 = r17
            android.graphics.Bitmap r1 = r0.getFrameAtTime(r1, r3)
            if (r19 == 0) goto L_0x002f
            r2 = 0
            int r3 = r0.getOrientation()
            r19[r2] = r3
        L_0x002f:
            r0.recycle()
            goto L_0x0054
        L_0x0033:
            android.media.MediaMetadataRetriever r3 = new android.media.MediaMetadataRetriever
            r3.<init>()
            r4 = 0
            r3.setDataSource(r0)     // Catch:{ Exception -> 0x0052, all -> 0x004d }
            r0 = 1
            android.graphics.Bitmap r0 = r3.getFrameAtTime(r1, r0)     // Catch:{ Exception -> 0x0052, all -> 0x004d }
            if (r0 != 0) goto L_0x0048
            r4 = 3
            android.graphics.Bitmap r0 = r3.getFrameAtTime(r1, r4)     // Catch:{ Exception -> 0x0048, all -> 0x004d }
        L_0x0048:
            r1 = r0
        L_0x0049:
            r3.release()     // Catch:{ RuntimeException -> 0x0054 }
            goto L_0x0054
        L_0x004d:
            r0 = move-exception
            r3.release()     // Catch:{ RuntimeException -> 0x0051 }
        L_0x0051:
            throw r0
        L_0x0052:
            r1 = r4
            goto L_0x0049
        L_0x0054:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.createVideoThumbnailAtTime(java.lang.String, long, int[], boolean):android.graphics.Bitmap");
    }

    private static VideoEditedInfo createCompressionSettings(String str) {
        int i;
        int[] iArr = new int[11];
        AnimatedFileDrawable.getVideoInfo(str, iArr);
        if (iArr[0] == 0) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("video hasn't avc1 atom");
            }
            return null;
        }
        int videoBitrate = MediaController.getVideoBitrate(str);
        int i2 = 4;
        float f = (float) iArr[4];
        long j = (long) iArr[6];
        long j2 = (long) iArr[5];
        int i3 = iArr[7];
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
        videoEditedInfo.framerate = i3;
        videoEditedInfo.estimatedDuration = (long) Math.ceil((double) f);
        int i4 = iArr[1];
        videoEditedInfo.originalWidth = i4;
        videoEditedInfo.resultWidth = i4;
        int i5 = iArr[2];
        videoEditedInfo.originalHeight = i5;
        videoEditedInfo.resultHeight = i5;
        videoEditedInfo.rotationValue = iArr[8];
        videoEditedInfo.originalDuration = (long) (f * 1000.0f);
        float max = (float) Math.max(i4, i5);
        float f2 = 640.0f;
        if (max <= 1280.0f) {
            i2 = max > 854.0f ? 3 : max > 640.0f ? 2 : 1;
        }
        int round = Math.round(((float) DownloadController.getInstance(UserConfig.selectedAccount).getMaxVideoBitrate()) / (100.0f / ((float) i2))) - 1;
        if (round >= i2) {
            round = i2 - 1;
        }
        int i6 = i2 - 1;
        if (round != i6) {
            if (round == 1) {
                f2 = 432.0f;
            } else if (round != 2) {
                f2 = round != 3 ? 1280.0f : 848.0f;
            }
            int i7 = videoEditedInfo.originalWidth;
            int i8 = videoEditedInfo.originalHeight;
            float f3 = f2 / (i7 > i8 ? (float) i7 : (float) i8);
            videoEditedInfo.resultWidth = Math.round((((float) videoEditedInfo.originalWidth) * f3) / 2.0f) * 2;
            int round2 = Math.round((((float) videoEditedInfo.originalHeight) * f3) / 2.0f) * 2;
            videoEditedInfo.resultHeight = round2;
            i = MediaController.makeVideoBitrate(videoEditedInfo.originalHeight, videoEditedInfo.originalWidth, videoBitrate, round2, videoEditedInfo.resultWidth);
        } else {
            i = videoBitrate;
        }
        if (round == i6) {
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

    public static void prepareSendingVideo(AccountInstance accountInstance, String str, VideoEditedInfo videoEditedInfo, long j, MessageObject messageObject, MessageObject messageObject2, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, int i, MessageObject messageObject3, boolean z, int i2) {
        if (str != null && str.length() != 0) {
            new Thread(new Runnable(str, j, i, accountInstance, charSequence, messageObject3, messageObject, messageObject2, arrayList, z, i2) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ boolean f$10;
                public final /* synthetic */ int f$11;
                public final /* synthetic */ long f$2;
                public final /* synthetic */ int f$3;
                public final /* synthetic */ AccountInstance f$4;
                public final /* synthetic */ CharSequence f$5;
                public final /* synthetic */ MessageObject f$6;
                public final /* synthetic */ MessageObject f$7;
                public final /* synthetic */ MessageObject f$8;
                public final /* synthetic */ ArrayList f$9;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                    this.f$7 = r9;
                    this.f$8 = r10;
                    this.f$9 = r11;
                    this.f$10 = r12;
                    this.f$11 = r13;
                }

                public final void run() {
                    SendMessagesHelper.lambda$prepareSendingVideo$81(VideoEditedInfo.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11);
                }
            }).start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:129:0x0310  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x031c  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0349  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0351  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0357  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x011f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingVideo$81(org.telegram.messenger.VideoEditedInfo r30, java.lang.String r31, long r32, int r34, org.telegram.messenger.AccountInstance r35, java.lang.CharSequence r36, org.telegram.messenger.MessageObject r37, org.telegram.messenger.MessageObject r38, org.telegram.messenger.MessageObject r39, java.util.ArrayList r40, boolean r41, int r42) {
        /*
            r6 = r31
            r10 = r32
            if (r30 == 0) goto L_0x0009
            r7 = r30
            goto L_0x000e
        L_0x0009:
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r31)
            r7 = r0
        L_0x000e:
            int r0 = (int) r10
            r8 = 0
            r9 = 1
            if (r0 != 0) goto L_0x0015
            r12 = 1
            goto L_0x0016
        L_0x0015:
            r12 = 0
        L_0x0016:
            if (r7 == 0) goto L_0x001e
            boolean r0 = r7.roundVideo
            if (r0 == 0) goto L_0x001e
            r13 = 1
            goto L_0x001f
        L_0x001e:
            r13 = 0
        L_0x001f:
            if (r7 != 0) goto L_0x004a
            java.lang.String r0 = "mp4"
            boolean r0 = r6.endsWith(r0)
            if (r0 != 0) goto L_0x004a
            if (r13 == 0) goto L_0x002c
            goto L_0x004a
        L_0x002c:
            r3 = 0
            r4 = 0
            r12 = 0
            r0 = r35
            r1 = r31
            r2 = r31
            r5 = r32
            r7 = r38
            r8 = r39
            r9 = r36
            r10 = r40
            r11 = r37
            r13 = r41
            r14 = r42
            prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11, r12, r13, r14)
            goto L_0x038d
        L_0x004a:
            java.io.File r14 = new java.io.File
            r14.<init>(r6)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r6)
            long r1 = r14.length()
            r0.append(r1)
            java.lang.String r15 = "_"
            r0.append(r15)
            long r1 = r14.lastModified()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r16 = ""
            r1 = 0
            if (r7 == 0) goto L_0x00c4
            if (r13 != 0) goto L_0x00bd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            long r4 = r7.estimatedDuration
            r3.append(r4)
            r3.append(r15)
            long r4 = r7.startTime
            r3.append(r4)
            r3.append(r15)
            long r4 = r7.endTime
            r3.append(r4)
            boolean r0 = r7.muted
            if (r0 == 0) goto L_0x009a
            java.lang.String r0 = "_m"
            goto L_0x009c
        L_0x009a:
            r0 = r16
        L_0x009c:
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            int r3 = r7.resultWidth
            int r4 = r7.originalWidth
            if (r3 == r4) goto L_0x00bd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r15)
            int r0 = r7.resultWidth
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x00bd:
            long r3 = r7.startTime
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 < 0) goto L_0x00c4
            r1 = r3
        L_0x00c4:
            r4 = r0
            r2 = r1
            r5 = 2
            if (r12 != 0) goto L_0x0116
            if (r34 != 0) goto L_0x0116
            if (r7 == 0) goto L_0x00dd
            org.telegram.messenger.MediaController$SavedFilterState r0 = r7.filterState
            if (r0 != 0) goto L_0x0116
            java.lang.String r0 = r7.paintPath
            if (r0 != 0) goto L_0x0116
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r7.mediaEntities
            if (r0 != 0) goto L_0x0116
            org.telegram.messenger.MediaController$CropState r0 = r7.cropState
            if (r0 != 0) goto L_0x0116
        L_0x00dd:
            org.telegram.messenger.MessagesStorage r0 = r35.getMessagesStorage()
            if (r12 != 0) goto L_0x00e5
            r1 = 2
            goto L_0x00e8
        L_0x00e5:
            r17 = 5
            r1 = 5
        L_0x00e8:
            java.lang.Object[] r0 = r0.getSentFile(r4, r1)
            if (r0 == 0) goto L_0x0116
            r1 = r0[r8]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r1 == 0) goto L_0x0116
            r1 = r0[r8]
            r17 = r1
            org.telegram.tgnet.TLRPC$TL_document r17 = (org.telegram.tgnet.TLRPC$TL_document) r17
            r0 = r0[r9]
            r18 = r0
            java.lang.String r18 = (java.lang.String) r18
            r19 = 0
            r0 = r12
            r1 = r17
            r21 = r2
            r2 = r31
            r3 = r19
            r23 = r4
            r8 = 2
            r4 = r21
            ensureMediaThumbExists(r0, r1, r2, r3, r4)
            r5 = r18
            goto L_0x011d
        L_0x0116:
            r21 = r2
            r23 = r4
            r8 = 2
            r1 = 0
            r5 = 0
        L_0x011d:
            if (r1 != 0) goto L_0x0310
            r2 = r21
            android.graphics.Bitmap r0 = createVideoThumbnailAtTime(r6, r2)
            if (r0 != 0) goto L_0x012b
            android.graphics.Bitmap r0 = createVideoThumbnail(r6, r9)
        L_0x012b:
            r1 = r0
            r0 = 90
            if (r12 != 0) goto L_0x0136
            if (r34 == 0) goto L_0x0133
            goto L_0x0136
        L_0x0133:
            r2 = 320(0x140, float:4.48E-43)
            goto L_0x0138
        L_0x0136:
            r2 = 90
        L_0x0138:
            float r3 = (float) r2
            if (r2 <= r0) goto L_0x013e
            r2 = 80
            goto L_0x0140
        L_0x013e:
            r2 = 55
        L_0x0140:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r3, r3, r2, r12)
            if (r1 == 0) goto L_0x024c
            if (r2 == 0) goto L_0x024c
            if (r13 == 0) goto L_0x0249
            r3 = 21
            if (r12 == 0) goto L_0x01ec
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createScaledBitmap(r1, r0, r0, r9)
            r25 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x015b
            r26 = 0
            goto L_0x015d
        L_0x015b:
            r26 = 1
        L_0x015d:
            int r27 = r1.getWidth()
            int r28 = r1.getHeight()
            int r29 = r1.getRowBytes()
            r24 = r1
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            r25 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x0177
            r26 = 0
            goto L_0x0179
        L_0x0177:
            r26 = 1
        L_0x0179:
            int r27 = r1.getWidth()
            int r28 = r1.getHeight()
            int r29 = r1.getRowBytes()
            r24 = r1
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            r25 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x0193
            r26 = 0
            goto L_0x0195
        L_0x0193:
            r26 = 1
        L_0x0195:
            int r27 = r1.getWidth()
            int r28 = r1.getHeight()
            int r29 = r1.getRowBytes()
            r24 = r1
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r4 = r2.location
            r18 = r1
            long r0 = r4.volume_id
            r3.append(r0)
            r3.append(r15)
            org.telegram.tgnet.TLRPC$FileLocation r0 = r2.location
            int r0 = r0.local_id
            r3.append(r0)
            java.lang.String r0 = "@%d_%d_b2"
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            java.lang.Object[] r1 = new java.lang.Object[r8]
            int r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r3 = (float) r3
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r4
            int r3 = (int) r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 0
            r1[r4] = r3
            int r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r3 = (float) r3
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r4
            int r3 = (int) r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r1[r9] = r3
            java.lang.String r1 = java.lang.String.format(r0, r1)
            r0 = r1
            r1 = r18
            goto L_0x024d
        L_0x01ec:
            r25 = 3
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 >= r3) goto L_0x01f5
            r26 = 0
            goto L_0x01f7
        L_0x01f5:
            r26 = 1
        L_0x01f7:
            int r27 = r1.getWidth()
            int r28 = r1.getHeight()
            int r29 = r1.getRowBytes()
            r24 = r1
            org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            long r3 = r3.volume_id
            r0.append(r3)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            int r3 = r3.local_id
            r0.append(r3)
            java.lang.String r3 = "@%d_%d_b"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r3 = new java.lang.Object[r8]
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r4 = (float) r4
            float r8 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r8
            int r4 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r8 = 0
            r3[r8] = r4
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r4 = (float) r4
            float r8 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r8
            int r4 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r9] = r4
            java.lang.String r0 = java.lang.String.format(r0, r3)
            goto L_0x024d
        L_0x0249:
            r0 = 0
            r1 = 0
            goto L_0x024d
        L_0x024c:
            r0 = 0
        L_0x024d:
            org.telegram.tgnet.TLRPC$TL_document r3 = new org.telegram.tgnet.TLRPC$TL_document
            r3.<init>()
            if (r2 == 0) goto L_0x025e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r3.thumbs
            r4.add(r2)
            int r2 = r3.flags
            r2 = r2 | r9
            r3.flags = r2
        L_0x025e:
            r2 = 0
            byte[] r4 = new byte[r2]
            r3.file_reference = r4
            java.lang.String r4 = "video/mp4"
            r3.mime_type = r4
            org.telegram.messenger.UserConfig r4 = r35.getUserConfig()
            r4.saveConfig(r2)
            if (r12 == 0) goto L_0x029a
            r2 = 32
            long r8 = r10 >> r2
            int r2 = (int) r8
            org.telegram.messenger.MessagesController r4 = r35.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r4.getEncryptedChat(r2)
            if (r2 != 0) goto L_0x0284
            return
        L_0x0284:
            int r2 = r2.layer
            int r2 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r2)
            r4 = 66
            if (r2 < r4) goto L_0x0294
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r2.<init>()
            goto L_0x02a1
        L_0x0294:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r2.<init>()
            goto L_0x02a1
        L_0x029a:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r2.<init>()
            r2.supports_streaming = r9
        L_0x02a1:
            r2.round_message = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r2)
            if (r7 == 0) goto L_0x02fc
            boolean r4 = r7.needConvert()
            if (r4 == 0) goto L_0x02fc
            boolean r4 = r7.muted
            if (r4 == 0) goto L_0x02ca
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r8.<init>()
            r4.add(r8)
            fillVideoAttribute(r6, r2, r7)
            int r4 = r2.w
            r7.originalWidth = r4
            int r4 = r2.h
            r7.originalHeight = r4
            goto L_0x02d2
        L_0x02ca:
            long r8 = r7.estimatedDuration
            r12 = 1000(0x3e8, double:4.94E-321)
            long r8 = r8 / r12
            int r4 = (int) r8
            r2.duration = r4
        L_0x02d2:
            int r4 = r7.rotationValue
            org.telegram.messenger.MediaController$CropState r8 = r7.cropState
            if (r8 == 0) goto L_0x02e0
            int r9 = r8.transformWidth
            int r12 = r8.transformHeight
            int r8 = r8.transformRotation
            int r4 = r4 + r8
            goto L_0x02e4
        L_0x02e0:
            int r9 = r7.resultWidth
            int r12 = r7.resultHeight
        L_0x02e4:
            r8 = 90
            if (r4 == r8) goto L_0x02f2
            r8 = 270(0x10e, float:3.78E-43)
            if (r4 != r8) goto L_0x02ed
            goto L_0x02f2
        L_0x02ed:
            r2.w = r9
            r2.h = r12
            goto L_0x02f6
        L_0x02f2:
            r2.w = r12
            r2.h = r9
        L_0x02f6:
            long r8 = r7.estimatedSize
            int r2 = (int) r8
            r3.size = r2
            goto L_0x030d
        L_0x02fc:
            boolean r4 = r14.exists()
            if (r4 == 0) goto L_0x0309
            long r8 = r14.length()
            int r4 = (int) r8
            r3.size = r4
        L_0x0309:
            r4 = 0
            fillVideoAttribute(r6, r2, r4)
        L_0x030d:
            r2 = r0
            r8 = r3
            goto L_0x0314
        L_0x0310:
            r4 = 0
            r8 = r1
            r1 = r4
            r2 = r1
        L_0x0314:
            if (r7 == 0) goto L_0x0349
            boolean r0 = r7.needConvert()
            if (r0 == 0) goto L_0x0349
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
            r9 = r0
            goto L_0x034a
        L_0x0349:
            r9 = r6
        L_0x034a:
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
            if (r36 == 0) goto L_0x0357
            java.lang.String r0 = r36.toString()
            r14 = r0
            goto L_0x0359
        L_0x0357:
            r14 = r16
        L_0x0359:
            r0 = r23
            if (r0 == 0) goto L_0x0362
            java.lang.String r3 = "originalPath"
            r12.put(r3, r0)
        L_0x0362:
            if (r5 == 0) goto L_0x0369
            java.lang.String r0 = "parentObject"
            r12.put(r0, r5)
        L_0x0369:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$LcSCmtiExtoeckNYnfIbR8T6FU4 r19 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$LcSCmtiExtoeckNYnfIbR8T6FU4
            r0 = r19
            r3 = r37
            r4 = r35
            r20 = r5
            r5 = r7
            r6 = r8
            r7 = r9
            r8 = r12
            r9 = r20
            r10 = r32
            r12 = r38
            r13 = r39
            r15 = r40
            r16 = r41
            r17 = r42
            r18 = r34
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r19)
        L_0x038d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$81(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, int, org.telegram.messenger.AccountInstance, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$80(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, MessageObject messageObject3, String str4, ArrayList arrayList, boolean z, int i, int i2) {
        Bitmap bitmap2 = bitmap;
        String str5 = str;
        if (!(bitmap2 == null || str5 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str5);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, (TLRPC$TL_photo) null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, messageObject3, str4, arrayList, (TLRPC$ReplyMarkup) null, hashMap, z, i, i2, str3);
        }
    }
}
