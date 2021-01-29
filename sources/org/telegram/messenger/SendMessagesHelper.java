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
import android.text.TextUtils;
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
import org.telegram.tgnet.TLRPC$TL_photoPathSize;
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
            $$Lambda$SendMessagesHelper$LocationProvider$UF_XFXfCoxrzj_3NU6Lp5Nkfmro r0 = new Runnable() {
                public final void run() {
                    SendMessagesHelper.LocationProvider.this.lambda$start$0$SendMessagesHelper$LocationProvider();
                }
            };
            this.locationQueryCancelRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 5000);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$start$0 */
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
        public boolean retriedToSend;
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
            ArrayList<DelayedMessageSendAfterRequest> arrayList = this.requests;
            if (arrayList != null) {
                int i = this.type;
                if (i == 4 || i == 0) {
                    int size = arrayList.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = this.requests.get(i2);
                        TLObject tLObject = delayedMessageSendAfterRequest.request;
                        if (tLObject instanceof TLRPC$TL_messages_sendEncryptedMultiMedia) {
                            SendMessagesHelper.this.getSecretChatHelper().performSendEncryptedRequest((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessageSendAfterRequest.request, this);
                        } else if (tLObject instanceof TLRPC$TL_messages_sendMultiMedia) {
                            SendMessagesHelper.this.performSendMessageRequestMulti((TLRPC$TL_messages_sendMultiMedia) tLObject, delayedMessageSendAfterRequest.msgObjs, delayedMessageSendAfterRequest.originalPaths, delayedMessageSendAfterRequest.parentObjects, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.scheduled);
                        } else {
                            SendMessagesHelper.this.performSendMessageRequest(tLObject, delayedMessageSendAfterRequest.msgObj, delayedMessageSendAfterRequest.originalPath, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.parentObject, (HashMap<String, String>) null, delayedMessageSendAfterRequest.scheduled);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
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
        TLRPC$InputFile tLRPC$InputFile;
        String str2;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile;
        ArrayList arrayList2;
        TLObject tLObject;
        TLRPC$TL_decryptedMessage tLRPC$TL_decryptedMessage;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile2;
        int i3;
        ArrayList arrayList3;
        int i4;
        String str3;
        TLRPC$PhotoSize tLRPC$PhotoSize;
        TLRPC$PhotoSize tLRPC$PhotoSize2;
        int i5 = i;
        int i6 = 0;
        if (i5 == NotificationCenter.FileDidUpload) {
            String str4 = objArr[0];
            TLRPC$InputFile tLRPC$InputFile2 = objArr[1];
            TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile3 = objArr[2];
            ArrayList arrayList4 = this.delayedMessages.get(str4);
            if (arrayList4 != null) {
                while (i6 < arrayList4.size()) {
                    DelayedMessage delayedMessage = (DelayedMessage) arrayList4.get(i6);
                    TLObject tLObject2 = delayedMessage.sendRequest;
                    if (tLObject2 instanceof TLRPC$TL_messages_sendMedia) {
                        tLRPC$InputMedia = ((TLRPC$TL_messages_sendMedia) tLObject2).media;
                    } else if (tLObject2 instanceof TLRPC$TL_messages_editMessage) {
                        tLRPC$InputMedia = ((TLRPC$TL_messages_editMessage) tLObject2).media;
                    } else {
                        tLRPC$InputMedia = tLObject2 instanceof TLRPC$TL_messages_sendMultiMedia ? (TLRPC$InputMedia) delayedMessage.extraHashMap.get(str4) : null;
                    }
                    if (tLRPC$InputFile2 == null || tLRPC$InputMedia == null) {
                        arrayList2 = arrayList4;
                        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile4 = tLRPC$InputEncryptedFile3;
                        tLRPC$InputFile = tLRPC$InputFile2;
                        str2 = str4;
                        tLRPC$InputEncryptedFile = tLRPC$InputEncryptedFile4;
                        if (!(tLRPC$InputEncryptedFile == null || (tLObject = delayedMessage.sendEncryptedRequest) == null)) {
                            if (delayedMessage.type == 4) {
                                TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) tLObject;
                                TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile5 = (TLRPC$InputEncryptedFile) delayedMessage.extraHashMap.get(str2);
                                int indexOf = tLRPC$TL_messages_sendEncryptedMultiMedia.files.indexOf(tLRPC$InputEncryptedFile5);
                                if (indexOf >= 0) {
                                    tLRPC$TL_messages_sendEncryptedMultiMedia.files.set(indexOf, tLRPC$InputEncryptedFile);
                                    if (tLRPC$InputEncryptedFile5.id == 1) {
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
                            i4 = i6;
                            tLRPC$InputFile = tLRPC$InputFile2;
                            str3 = str4;
                            performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, delayedMessage, true, (DelayedMessage) null, delayedMessage.parentObject, (HashMap<String, String>) null, delayedMessage.scheduled);
                        } else {
                            DelayedMessage delayedMessage2 = delayedMessage;
                            arrayList3 = arrayList4;
                            tLRPC$InputEncryptedFile2 = tLRPC$InputEncryptedFile3;
                            i4 = i6;
                            tLRPC$InputFile = tLRPC$InputFile2;
                            str3 = str4;
                            if (i7 != 1) {
                                DelayedMessage delayedMessage3 = delayedMessage2;
                                if (i7 == 2) {
                                    if (tLRPC$InputMedia.file == null) {
                                        tLRPC$InputMedia.file = tLRPC$InputFile;
                                        if (tLRPC$InputMedia.thumb != null || (tLRPC$PhotoSize = delayedMessage3.photoSize) == null || tLRPC$PhotoSize.location == null) {
                                            performSendMessageRequest(delayedMessage3.sendRequest, delayedMessage3.obj, delayedMessage3.originalPath, (DelayedMessage) null, delayedMessage3.parentObject, (HashMap<String, String>) null, delayedMessage3.scheduled);
                                        } else {
                                            performSendDelayedMessage(delayedMessage3);
                                        }
                                    } else {
                                        tLRPC$InputMedia.thumb = tLRPC$InputFile;
                                        tLRPC$InputMedia.flags |= 4;
                                        performSendMessageRequest(delayedMessage3.sendRequest, delayedMessage3.obj, delayedMessage3.originalPath, (DelayedMessage) null, delayedMessage3.parentObject, (HashMap<String, String>) null, delayedMessage3.scheduled);
                                    }
                                } else if (i7 == 3) {
                                    tLRPC$InputMedia.file = tLRPC$InputFile;
                                    performSendMessageRequest(delayedMessage3.sendRequest, delayedMessage3.obj, delayedMessage3.originalPath, (DelayedMessage) null, delayedMessage3.parentObject, (HashMap<String, String>) null, delayedMessage3.scheduled);
                                } else {
                                    if (i7 != 4) {
                                        str2 = str3;
                                    } else if (!(tLRPC$InputMedia instanceof TLRPC$TL_inputMediaUploadedDocument)) {
                                        str2 = str3;
                                        tLRPC$InputMedia.file = tLRPC$InputFile;
                                        uploadMultiMedia(delayedMessage3, tLRPC$InputMedia, (TLRPC$InputEncryptedFile) null, str2);
                                    } else if (tLRPC$InputMedia.file == null) {
                                        tLRPC$InputMedia.file = tLRPC$InputFile;
                                        HashMap<Object, Object> hashMap = delayedMessage3.extraHashMap;
                                        StringBuilder sb = new StringBuilder();
                                        str2 = str3;
                                        sb.append(str2);
                                        sb.append("_i");
                                        int indexOf2 = delayedMessage3.messageObjects.indexOf((MessageObject) hashMap.get(sb.toString()));
                                        if (indexOf2 >= 0) {
                                            stopVideoService(delayedMessage3.messageObjects.get(indexOf2).messageOwner.attachPath);
                                        }
                                        TLRPC$PhotoSize tLRPC$PhotoSize3 = (TLRPC$PhotoSize) delayedMessage3.extraHashMap.get(str2 + "_t");
                                        delayedMessage3.photoSize = tLRPC$PhotoSize3;
                                        if (tLRPC$InputMedia.thumb != null || tLRPC$PhotoSize3 == null || tLRPC$PhotoSize3.location == null) {
                                            uploadMultiMedia(delayedMessage3, tLRPC$InputMedia, (TLRPC$InputEncryptedFile) null, str2);
                                        } else {
                                            delayedMessage3.performMediaUpload = true;
                                            performSendDelayedMessage(delayedMessage3, indexOf2);
                                        }
                                    } else {
                                        str2 = str3;
                                        tLRPC$InputMedia.thumb = tLRPC$InputFile;
                                        tLRPC$InputMedia.flags |= 4;
                                        uploadMultiMedia(delayedMessage3, tLRPC$InputMedia, (TLRPC$InputEncryptedFile) null, (String) delayedMessage3.extraHashMap.get(str2 + "_o"));
                                    }
                                    arrayList2 = arrayList3;
                                    i3 = i4;
                                    arrayList2.remove(i3);
                                    i6 = i3 - 1;
                                    tLRPC$InputEncryptedFile = tLRPC$InputEncryptedFile2;
                                }
                            } else if (tLRPC$InputMedia.file == null) {
                                tLRPC$InputMedia.file = tLRPC$InputFile;
                                DelayedMessage delayedMessage4 = delayedMessage2;
                                if (tLRPC$InputMedia.thumb != null || (tLRPC$PhotoSize2 = delayedMessage4.photoSize) == null || tLRPC$PhotoSize2.location == null) {
                                    performSendMessageRequest(delayedMessage4.sendRequest, delayedMessage4.obj, delayedMessage4.originalPath, (DelayedMessage) null, delayedMessage4.parentObject, (HashMap<String, String>) null, delayedMessage4.scheduled);
                                } else {
                                    performSendDelayedMessage(delayedMessage4);
                                }
                            } else {
                                DelayedMessage delayedMessage5 = delayedMessage2;
                                tLRPC$InputMedia.thumb = tLRPC$InputFile;
                                tLRPC$InputMedia.flags |= 4;
                                performSendMessageRequest(delayedMessage5.sendRequest, delayedMessage5.obj, delayedMessage5.originalPath, (DelayedMessage) null, delayedMessage5.parentObject, (HashMap<String, String>) null, delayedMessage5.scheduled);
                            }
                        }
                        arrayList2 = arrayList3;
                        i3 = i4;
                        str2 = str3;
                        arrayList2.remove(i3);
                        i6 = i3 - 1;
                        tLRPC$InputEncryptedFile = tLRPC$InputEncryptedFile2;
                    }
                    i6++;
                    arrayList4 = arrayList2;
                    str4 = str2;
                    tLRPC$InputFile2 = tLRPC$InputFile;
                    tLRPC$InputEncryptedFile3 = tLRPC$InputEncryptedFile;
                }
                String str5 = str4;
                if (arrayList4.isEmpty()) {
                    this.delayedMessages.remove(str5);
                }
            }
        } else if (i5 == NotificationCenter.FileDidFailUpload) {
            String str6 = objArr[0];
            boolean booleanValue = objArr[1].booleanValue();
            ArrayList arrayList5 = this.delayedMessages.get(str6);
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
                    this.delayedMessages.remove(str6);
                }
            }
        } else if (i5 == NotificationCenter.filePreparingStarted) {
            MessageObject messageObject5 = objArr[0];
            if (messageObject5.getId() != 0) {
                String str7 = objArr[1];
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
        } else if (i5 == NotificationCenter.fileNewChunkAvailable) {
            MessageObject messageObject6 = objArr[0];
            if (messageObject6.getId() != 0) {
                String str8 = objArr[1];
                long longValue = objArr[2].longValue();
                long longValue2 = objArr[3].longValue();
                getFileLoader().checkUploadNewDataAvailable(str8, ((int) messageObject6.getDialogId()) == 0, longValue, longValue2);
                if (longValue2 != 0) {
                    stopVideoService(messageObject6.messageOwner.attachPath);
                    ArrayList arrayList7 = this.delayedMessages.get(messageObject6.messageOwner.attachPath);
                    if (arrayList7 != null) {
                        for (int i8 = 0; i8 < arrayList7.size(); i8++) {
                            DelayedMessage delayedMessage8 = (DelayedMessage) arrayList7.get(i8);
                            if (delayedMessage8.type == 4) {
                                int i9 = 0;
                                while (true) {
                                    if (i9 >= delayedMessage8.messageObjects.size()) {
                                        break;
                                    }
                                    MessageObject messageObject7 = delayedMessage8.messageObjects.get(i9);
                                    if (messageObject7 == messageObject6) {
                                        delayedMessage8.obj.shouldRemoveVideoEditedInfo = true;
                                        messageObject7.messageOwner.params.remove("ve");
                                        messageObject7.messageOwner.media.document.size = (int) longValue2;
                                        ArrayList arrayList8 = new ArrayList();
                                        arrayList8.add(messageObject7.messageOwner);
                                        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList8, false, true, false, 0, messageObject7.scheduled);
                                        break;
                                    }
                                    i9++;
                                }
                            } else {
                                MessageObject messageObject8 = delayedMessage8.obj;
                                if (messageObject8 == messageObject6) {
                                    messageObject8.shouldRemoveVideoEditedInfo = true;
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
        } else if (i5 == NotificationCenter.filePreparingFailed) {
            MessageObject messageObject9 = objArr[0];
            if (messageObject9.getId() != 0) {
                String str9 = objArr[1];
                stopVideoService(messageObject9.messageOwner.attachPath);
                ArrayList arrayList10 = this.delayedMessages.get(str9);
                if (arrayList10 != null) {
                    int i10 = 0;
                    while (i10 < arrayList10.size()) {
                        DelayedMessage delayedMessage9 = (DelayedMessage) arrayList10.get(i10);
                        if (delayedMessage9.type == 4) {
                            int i11 = 0;
                            while (true) {
                                if (i11 >= delayedMessage9.messages.size()) {
                                    break;
                                } else if (delayedMessage9.messageObjects.get(i11) == messageObject9) {
                                    delayedMessage9.markAsError();
                                    arrayList10.remove(i10);
                                    break;
                                } else {
                                    i11++;
                                }
                            }
                            i10++;
                        } else if (delayedMessage9.obj == messageObject9) {
                            delayedMessage9.markAsError();
                            arrayList10.remove(i10);
                        } else {
                            i10++;
                        }
                        i10--;
                        i10++;
                    }
                    if (arrayList10.isEmpty()) {
                        this.delayedMessages.remove(str9);
                    }
                }
            }
        } else if (i5 == NotificationCenter.httpFileDidLoad) {
            String str10 = objArr[0];
            ArrayList arrayList11 = this.delayedMessages.get(str10);
            if (arrayList11 != null) {
                for (int i12 = 0; i12 < arrayList11.size(); i12++) {
                    DelayedMessage delayedMessage10 = (DelayedMessage) arrayList11.get(i12);
                    int i13 = delayedMessage10.type;
                    if (i13 == 0) {
                        messageObject = delayedMessage10.obj;
                        c = 0;
                    } else {
                        if (i13 == 2) {
                            messageObject2 = delayedMessage10.obj;
                        } else if (i13 == 4) {
                            messageObject2 = (MessageObject) delayedMessage10.extraHashMap.get(str10);
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
                        Utilities.globalQueue.postRunnable(new Runnable(new File(FileLoader.getDirectory(4), Utilities.MD5(str10) + "." + ImageLoader.getHttpUrlExtension(str10, "file")), messageObject, delayedMessage10, str10) {
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
                        });
                    } else if (c == 1) {
                        Utilities.globalQueue.postRunnable(new Runnable(delayedMessage10, new File(FileLoader.getDirectory(4), Utilities.MD5(str10) + ".gif"), messageObject) {
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
                    }
                }
                this.delayedMessages.remove(str10);
            }
        } else if (i5 == NotificationCenter.fileDidLoad) {
            String str11 = objArr[0];
            ArrayList arrayList12 = this.delayedMessages.get(str11);
            if (arrayList12 != null) {
                while (i6 < arrayList12.size()) {
                    performSendDelayedMessage((DelayedMessage) arrayList12.get(i6));
                    i6++;
                }
                this.delayedMessages.remove(str11);
            }
        } else if ((i5 == NotificationCenter.httpFileDidFailedLoad || i5 == NotificationCenter.fileDidFailToLoad) && (arrayList = this.delayedMessages.get(str)) != null) {
            while (i6 < arrayList.size()) {
                ((DelayedMessage) arrayList.get(i6)).markAsError();
                i6++;
            }
            this.delayedMessages.remove((str = objArr[0]));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$2 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$4 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
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
        tLRPC$Message.message = messageObject.previousMessage;
        ArrayList<TLRPC$MessageEntity> arrayList = messageObject.previousMessageEntities;
        tLRPC$Message.entities = arrayList;
        tLRPC$Message.attachPath = messageObject.previousAttachPath;
        tLRPC$Message.send_state = 0;
        if (arrayList != null) {
            tLRPC$Message.flags |= 128;
        } else {
            tLRPC$Message.flags &= -129;
        }
        messageObject.previousMedia = null;
        messageObject.previousMessage = null;
        messageObject.previousMessageEntities = null;
        messageObject.previousAttachPath = null;
        messageObject.videoEditedInfo = null;
        messageObject.type = -1;
        messageObject.setType();
        messageObject.caption = null;
        if (messageObject.type != 0) {
            messageObject.generateCaption();
        } else {
            messageObject.generateLayout((TLRPC$User) null);
        }
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(messageObject.messageOwner);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList2, false, true, false, 0, messageObject.scheduled);
        ArrayList arrayList3 = new ArrayList();
        arrayList3.add(messageObject);
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList3);
    }

    public void cancelSendingMessage(MessageObject messageObject) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject);
        cancelSendingMessage((ArrayList<MessageObject>) arrayList);
    }

    public void cancelSendingMessage(ArrayList<MessageObject> arrayList) {
        boolean z;
        long j;
        ArrayList<MessageObject> arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        long j2 = 0;
        int i = 0;
        boolean z2 = false;
        int i2 = 0;
        boolean z3 = false;
        while (i < arrayList.size()) {
            MessageObject messageObject = arrayList2.get(i);
            if (messageObject.scheduled) {
                j2 = messageObject.getDialogId();
                z3 = true;
            }
            arrayList5.add(Integer.valueOf(messageObject.getId()));
            int i3 = messageObject.messageOwner.peer_id.channel_id;
            TLRPC$Message removeFromSendingMessages = removeFromSendingMessages(messageObject.getId(), messageObject.scheduled);
            if (removeFromSendingMessages != null) {
                getConnectionsManager().cancelRequest(removeFromSendingMessages.reqId, true);
            }
            for (Map.Entry next : this.delayedMessages.entrySet()) {
                ArrayList arrayList6 = (ArrayList) next.getValue();
                int i4 = 0;
                while (true) {
                    if (i4 >= arrayList6.size()) {
                        z = z2;
                        j = j2;
                        break;
                    }
                    DelayedMessage delayedMessage = (DelayedMessage) arrayList6.get(i4);
                    z = z2;
                    j = j2;
                    if (delayedMessage.type == 4) {
                        int i5 = -1;
                        MessageObject messageObject2 = null;
                        int i6 = 0;
                        while (true) {
                            if (i6 >= delayedMessage.messageObjects.size()) {
                                break;
                            }
                            messageObject2 = delayedMessage.messageObjects.get(i6);
                            if (messageObject2.getId() == messageObject.getId()) {
                                removeFromUploadingMessages(messageObject.getId(), messageObject.scheduled);
                                i5 = i6;
                                break;
                            }
                            i6++;
                        }
                        if (i5 >= 0) {
                            delayedMessage.messageObjects.remove(i5);
                            delayedMessage.messages.remove(i5);
                            delayedMessage.originalPaths.remove(i5);
                            if (!delayedMessage.parentObjects.isEmpty()) {
                                delayedMessage.parentObjects.remove(i5);
                            }
                            TLObject tLObject = delayedMessage.sendRequest;
                            if (tLObject != null) {
                                ((TLRPC$TL_messages_sendMultiMedia) tLObject).multi_media.remove(i5);
                            } else {
                                TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
                                tLRPC$TL_messages_sendEncryptedMultiMedia.messages.remove(i5);
                                tLRPC$TL_messages_sendEncryptedMultiMedia.files.remove(i5);
                            }
                            MediaController.getInstance().cancelVideoConvert(messageObject);
                            String str = (String) delayedMessage.extraHashMap.get(messageObject2);
                            if (str != null) {
                                arrayList3.add(str);
                            }
                            if (delayedMessage.messageObjects.isEmpty()) {
                                delayedMessage.sendDelayedRequests();
                            } else {
                                if (delayedMessage.finalGroupMessage == messageObject.getId()) {
                                    ArrayList<MessageObject> arrayList7 = delayedMessage.messageObjects;
                                    MessageObject messageObject3 = arrayList7.get(arrayList7.size() - 1);
                                    delayedMessage.finalGroupMessage = messageObject3.getId();
                                    messageObject3.messageOwner.params.put("final", "1");
                                    TLRPC$TL_messages_messages tLRPC$TL_messages_messages = new TLRPC$TL_messages_messages();
                                    tLRPC$TL_messages_messages.messages.add(messageObject3.messageOwner);
                                    getMessagesStorage().putMessages((TLRPC$messages_Messages) tLRPC$TL_messages_messages, delayedMessage.peer, -2, 0, false, z3);
                                }
                                if (!arrayList4.contains(delayedMessage)) {
                                    arrayList4.add(delayedMessage);
                                }
                            }
                        }
                    } else if (delayedMessage.obj.getId() == messageObject.getId()) {
                        removeFromUploadingMessages(messageObject.getId(), messageObject.scheduled);
                        arrayList6.remove(i4);
                        delayedMessage.sendDelayedRequests();
                        MediaController.getInstance().cancelVideoConvert(delayedMessage.obj);
                        if (arrayList6.size() == 0) {
                            arrayList3.add(next.getKey());
                            if (delayedMessage.sendEncryptedRequest != null) {
                                z2 = true;
                            }
                        }
                    } else {
                        i4++;
                        z2 = z;
                        j2 = j;
                    }
                }
                z2 = z;
                j2 = j;
            }
            boolean z4 = z2;
            long j3 = j2;
            i++;
            i2 = i3;
        }
        for (int i7 = 0; i7 < arrayList3.size(); i7++) {
            String str2 = (String) arrayList3.get(i7);
            if (str2.startsWith("http")) {
                ImageLoader.getInstance().cancelLoadHttpFile(str2);
            } else {
                getFileLoader().cancelUploadFile(str2, z2);
            }
            stopVideoService(str2);
            this.delayedMessages.remove(str2);
        }
        int size = arrayList4.size();
        for (int i8 = 0; i8 < size; i8++) {
            sendReadyToSendGroup((DelayedMessage) arrayList4.get(i8), false, true);
        }
        if (arrayList.size() != 1 || !arrayList2.get(0).isEditing() || arrayList2.get(0).previousMedia == null) {
            getMessagesController().deleteMessages(arrayList5, (ArrayList<Long>) null, (TLRPC$EncryptedChat) null, j2, i2, false, z3);
        } else {
            revertEditingMessageObject(arrayList2.get(0));
        }
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean z) {
        if (messageObject.getId() >= 0) {
            if (messageObject.isEditing()) {
                editMessage(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$TL_document) null, (String) null, (HashMap<String, String>) null, true, messageObject);
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
            if (tLRPC$MessageMedia != null && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaEmpty) && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaGame) && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice)) {
                int i = (int) j2;
                if (i != 0 || tLRPC$Message.peer_id == null || (!(tLRPC$MessageMedia.photo instanceof TLRPC$TL_photo) && !(tLRPC$MessageMedia.document instanceof TLRPC$TL_document))) {
                    hashMap = null;
                } else {
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("parentObject", "sent_" + messageObject2.messageOwner.peer_id.channel_id + "_" + messageObject.getId());
                    hashMap = hashMap2;
                }
                TLRPC$Message tLRPC$Message2 = messageObject2.messageOwner;
                TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message2.media;
                TLRPC$Photo tLRPC$Photo = tLRPC$MessageMedia2.photo;
                if (tLRPC$Photo instanceof TLRPC$TL_photo) {
                    sendMessage((TLRPC$TL_photo) tLRPC$Photo, (String) null, j, messageObject2.replyMessageObject, (MessageObject) null, tLRPC$Message2.message, tLRPC$Message2.entities, (TLRPC$ReplyMarkup) null, hashMap, true, 0, tLRPC$MessageMedia2.ttl_seconds, messageObject);
                    return;
                }
                TLRPC$Document tLRPC$Document = tLRPC$MessageMedia2.document;
                if (tLRPC$Document instanceof TLRPC$TL_document) {
                    sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, tLRPC$Message2.attachPath, j, messageObject2.replyMessageObject, (MessageObject) null, tLRPC$Message2.message, tLRPC$Message2.entities, (TLRPC$ReplyMarkup) null, hashMap, true, 0, tLRPC$MessageMedia2.ttl_seconds, messageObject);
                } else if ((tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaVenue) || (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaGeo)) {
                    sendMessage(tLRPC$MessageMedia2, j, messageObject2.replyMessageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (tLRPC$MessageMedia2.phone_number != null) {
                    TLRPC$TL_userContact_old2 tLRPC$TL_userContact_old2 = new TLRPC$TL_userContact_old2();
                    TLRPC$MessageMedia tLRPC$MessageMedia3 = messageObject2.messageOwner.media;
                    tLRPC$TL_userContact_old2.phone = tLRPC$MessageMedia3.phone_number;
                    tLRPC$TL_userContact_old2.first_name = tLRPC$MessageMedia3.first_name;
                    tLRPC$TL_userContact_old2.last_name = tLRPC$MessageMedia3.last_name;
                    tLRPC$TL_userContact_old2.id = tLRPC$MessageMedia3.user_id;
                    sendMessage((TLRPC$User) tLRPC$TL_userContact_old2, j, messageObject2.replyMessageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (i != 0) {
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(messageObject2);
                    sendMessage(arrayList2, j, true, 0);
                }
            } else if (tLRPC$Message.message != null) {
                TLRPC$WebPage tLRPC$WebPage = tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage ? tLRPC$MessageMedia.webpage : null;
                ArrayList<TLRPC$MessageEntity> arrayList3 = tLRPC$Message.entities;
                if (arrayList3 == null || arrayList3.isEmpty()) {
                    arrayList = null;
                } else {
                    ArrayList arrayList4 = new ArrayList();
                    for (int i2 = 0; i2 < messageObject2.messageOwner.entities.size(); i2++) {
                        TLRPC$MessageEntity tLRPC$MessageEntity = messageObject2.messageOwner.entities.get(i2);
                        if ((tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityBold) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityItalic) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityPre) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCode) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityTextUrl)) {
                            arrayList4.add(tLRPC$MessageEntity);
                        }
                    }
                    arrayList = arrayList4;
                }
                sendMessage(messageObject2.messageOwner.message, j, messageObject2.replyMessageObject, (MessageObject) null, tLRPC$WebPage, true, arrayList, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
            } else if (((int) j2) != 0) {
                ArrayList arrayList5 = new ArrayList();
                arrayList5.add(messageObject2);
                sendMessage(arrayList5, j, true, 0);
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
            performSendMessageRequest(tLRPC$TL_messages_sendScreenshotNotification, messageObject, (String) null, (DelayedMessage) null, (Object) null, (HashMap<String, String>) null, false);
        }
    }

    public void sendSticker(TLRPC$Document tLRPC$Document, String str, long j, MessageObject messageObject, MessageObject messageObject2, Object obj, boolean z, int i) {
        TLRPC$TL_document_layer82 tLRPC$TL_document_layer82;
        HashMap hashMap;
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
                                TLRPC$FileLocation tLRPC$FileLocation = closestPhotoSizeWithSize.location;
                                tLRPC$TL_fileLocation_layer82.dc_id = tLRPC$FileLocation.dc_id;
                                tLRPC$TL_fileLocation_layer82.volume_id = tLRPC$FileLocation.volume_id;
                                tLRPC$TL_fileLocation_layer82.local_id = tLRPC$FileLocation.local_id;
                                tLRPC$TL_fileLocation_layer82.secret = tLRPC$FileLocation.secret;
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
            if (!TextUtils.isEmpty(str)) {
                hashMap = new HashMap();
                hashMap.put("query", str);
            } else {
                hashMap = null;
            }
            sendMessage((TLRPC$TL_document) tLRPC$TL_document_layer82, (VideoEditedInfo) null, (String) null, j, messageObject, messageObject2, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, obj);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendSticker$6 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$SendMessagesHelper(Bitmap[] bitmapArr, String[] strArr, TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, Object obj) {
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, (String) null, j, messageObject, messageObject2, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:253:0x05fe, code lost:
        if (r5.get(r6 + 1).getDialogId() != r1.getDialogId()) goto L_0x0625;
     */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x032e  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x033f  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0347  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0351  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0367  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x039b  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0457  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0461  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x047e  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x04aa A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x04c4  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x04c7  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x04dc  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0503  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x0505  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0515 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0552  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0559  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x0564  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0577  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0579  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x058e  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0590  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x0598  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x05cf  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x05d9  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0621  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0638  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x063b  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x064a  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x064c  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0696  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x06a6  */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x06cf  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x06ef  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x06f1  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x06fe  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0701  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0755  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01a3  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01b1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r43, long r44, boolean r46, int r47) {
        /*
            r42 = this;
            r13 = r42
            r14 = r43
            r11 = r44
            r15 = r47
            r10 = 0
            if (r14 == 0) goto L_0x0815
            boolean r0 = r43.isEmpty()
            if (r0 == 0) goto L_0x0013
            goto L_0x0815
        L_0x0013:
            int r0 = (int) r11
            org.telegram.messenger.UserConfig r1 = r42.getUserConfig()
            int r9 = r1.getClientUserId()
            if (r0 == 0) goto L_0x07f6
            org.telegram.messenger.MessagesController r1 = r42.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r8 = r1.getPeer(r0)
            r6 = 1
            if (r0 <= 0) goto L_0x0047
            org.telegram.messenger.MessagesController r1 = r42.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r1 != 0) goto L_0x0038
            return r10
        L_0x0038:
            r4 = 0
            r5 = 0
            r16 = 0
            r17 = 0
            r18 = 1
            r19 = 1
            r20 = 1
            r21 = 1
            goto L_0x0094
        L_0x0047:
            org.telegram.messenger.MessagesController r1 = r42.getMessagesController()
            int r2 = -r0
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0074
            boolean r2 = r1.signatures
            boolean r3 = r1.megagroup
            r3 = r3 ^ r6
            if (r3 == 0) goto L_0x0076
            boolean r4 = r1.has_link
            if (r4 == 0) goto L_0x0076
            org.telegram.messenger.MessagesController r4 = r42.getMessagesController()
            int r5 = r1.id
            org.telegram.tgnet.TLRPC$ChatFull r4 = r4.getChatFull(r5)
            if (r4 == 0) goto L_0x0076
            int r4 = r4.linked_chat_id
            goto L_0x0077
        L_0x0074:
            r2 = 0
            r3 = 0
        L_0x0076:
            r4 = 0
        L_0x0077:
            boolean r5 = org.telegram.messenger.ChatObject.canSendStickers(r1)
            boolean r16 = org.telegram.messenger.ChatObject.canSendMedia(r1)
            boolean r17 = org.telegram.messenger.ChatObject.canSendEmbed(r1)
            boolean r18 = org.telegram.messenger.ChatObject.canSendPolls(r1)
            r19 = r16
            r20 = r17
            r21 = r18
            r16 = r2
            r17 = r3
            r18 = r5
            r5 = r1
        L_0x0094:
            android.util.LongSparseArray r2 = new android.util.LongSparseArray
            r2.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r22 = new java.util.ArrayList
            r22.<init>()
            java.util.ArrayList r23 = new java.util.ArrayList
            r23.<init>()
            android.util.LongSparseArray r24 = new android.util.LongSparseArray
            r24.<init>()
            org.telegram.messenger.MessagesController r10 = r42.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r10 = r10.getInputPeer((int) r0)
            r26 = r8
            long r7 = (long) r9
            int r0 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x00c4
            r28 = 1
            goto L_0x00c6
        L_0x00c4:
            r28 = 0
        L_0x00c6:
            r0 = r22
            r29 = r23
            r30 = r24
            r6 = 0
            r23 = 0
            r24 = r3
            r3 = r1
        L_0x00d2:
            int r1 = r43.size()
            if (r6 >= r1) goto L_0x07f1
            java.lang.Object r1 = r14.get(r6)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r31 = r1.getId()
            if (r31 <= 0) goto L_0x0775
            boolean r31 = r1.needDrawBluredPreview()
            if (r31 == 0) goto L_0x00ec
            goto L_0x0775
        L_0x00ec:
            if (r18 != 0) goto L_0x0153
            boolean r32 = r1.isSticker()
            if (r32 != 0) goto L_0x0106
            boolean r32 = r1.isAnimatedSticker()
            if (r32 != 0) goto L_0x0106
            boolean r32 = r1.isGif()
            if (r32 != 0) goto L_0x0106
            boolean r32 = r1.isGame()
            if (r32 == 0) goto L_0x0153
        L_0x0106:
            if (r23 != 0) goto L_0x0132
            r1 = 8
            boolean r1 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r5, r1)
            if (r1 == 0) goto L_0x0112
            r14 = 4
            goto L_0x0113
        L_0x0112:
            r14 = 1
        L_0x0113:
            r36 = r4
            r13 = r6
            r37 = r7
            r41 = r9
            r34 = r10
            r23 = r14
            r1 = r24
            r31 = r29
            r15 = r30
        L_0x0124:
            r27 = 0
            r29 = 0
            r33 = 1
            r24 = r5
            r30 = r26
            r26 = r2
            goto L_0x07d1
        L_0x0132:
            r32 = r0
            r35 = r3
            r36 = r4
            r13 = r6
            r37 = r7
            r41 = r9
            r34 = r10
            r25 = r24
            r31 = r29
            r15 = r30
        L_0x0145:
            r27 = 0
            r29 = 0
            r33 = 1
            r24 = r5
            r30 = r26
            r26 = r2
            goto L_0x07cb
        L_0x0153:
            if (r19 != 0) goto L_0x0197
            org.telegram.tgnet.TLRPC$Message r14 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r14 = r14.media
            r33 = r6
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 != 0) goto L_0x0163
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x0199
        L_0x0163:
            if (r23 != 0) goto L_0x0182
            r1 = 7
            boolean r1 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r5, r1)
            if (r1 == 0) goto L_0x016e
            r1 = 5
            goto L_0x016f
        L_0x016e:
            r1 = 2
        L_0x016f:
            r23 = r1
            r36 = r4
            r37 = r7
            r41 = r9
            r34 = r10
            r1 = r24
            r31 = r29
            r15 = r30
            r13 = r33
            goto L_0x0124
        L_0x0182:
            r32 = r0
            r35 = r3
            r36 = r4
            r37 = r7
            r41 = r9
            r34 = r10
            r25 = r24
            r31 = r29
            r15 = r30
            r13 = r33
            goto L_0x0145
        L_0x0197:
            r33 = r6
        L_0x0199:
            if (r21 != 0) goto L_0x01b1
            org.telegram.tgnet.TLRPC$Message r6 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x01b1
            if (r23 != 0) goto L_0x0182
            r1 = 10
            boolean r1 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r5, r1)
            if (r1 == 0) goto L_0x01af
            r1 = 6
            goto L_0x016f
        L_0x01af:
            r1 = 3
            goto L_0x016f
        L_0x01b1:
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            long r34 = r1.getDialogId()
            int r14 = (r34 > r7 ? 1 : (r34 == r7 ? 0 : -1))
            if (r14 != 0) goto L_0x01ce
            boolean r14 = r1.isFromUser()
            if (r14 == 0) goto L_0x01ce
            org.telegram.tgnet.TLRPC$Message r14 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id
            int r14 = r14.user_id
            if (r14 != r9) goto L_0x01ce
            r14 = 1
            goto L_0x01cf
        L_0x01ce:
            r14 = 0
        L_0x01cf:
            boolean r34 = r1.isForwarded()
            if (r34 == 0) goto L_0x0261
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r14 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r14.<init>()
            r6.fwd_from = r14
            r34 = r3
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r3.fwd_from
            int r13 = r3.flags
            r22 = 1
            r13 = r13 & 1
            if (r13 == 0) goto L_0x01f4
            int r13 = r14.flags
            r13 = r13 | 1
            r14.flags = r13
            org.telegram.tgnet.TLRPC$Peer r13 = r3.from_id
            r14.from_id = r13
        L_0x01f4:
            int r13 = r3.flags
            r13 = r13 & 32
            if (r13 == 0) goto L_0x0204
            int r13 = r14.flags
            r13 = r13 | 32
            r14.flags = r13
            java.lang.String r13 = r3.from_name
            r14.from_name = r13
        L_0x0204:
            int r13 = r3.flags
            r32 = 4
            r13 = r13 & 4
            if (r13 == 0) goto L_0x0216
            int r13 = r14.flags
            r13 = r13 | 4
            r14.flags = r13
            int r13 = r3.channel_post
            r14.channel_post = r13
        L_0x0216:
            int r13 = r3.flags
            r31 = 8
            r13 = r13 & 8
            if (r13 == 0) goto L_0x0228
            int r13 = r14.flags
            r13 = r13 | 8
            r14.flags = r13
            java.lang.String r13 = r3.post_author
            r14.post_author = r13
        L_0x0228:
            int r13 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r13 == 0) goto L_0x022e
            if (r17 == 0) goto L_0x0252
        L_0x022e:
            int r3 = r3.flags
            r3 = r3 & 16
            if (r3 == 0) goto L_0x0252
            long r13 = r1.getDialogId()
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((long) r13)
            if (r3 != 0) goto L_0x0252
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r6.fwd_from
            int r13 = r3.flags
            r13 = r13 | 16
            r3.flags = r13
            org.telegram.tgnet.TLRPC$Message r13 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r13 = r13.fwd_from
            org.telegram.tgnet.TLRPC$Peer r14 = r13.saved_from_peer
            r3.saved_from_peer = r14
            int r13 = r13.saved_from_msg_id
            r3.saved_from_msg_id = r13
        L_0x0252:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r6.fwd_from
            org.telegram.tgnet.TLRPC$Message r13 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r13 = r13.fwd_from
            int r13 = r13.date
            r3.date = r13
            r3 = 4
            r6.flags = r3
            goto L_0x030a
        L_0x0261:
            r34 = r3
            r3 = 4
            if (r14 != 0) goto L_0x030a
            int r13 = r1.getFromChatId()
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r14 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r14.<init>()
            r6.fwd_from = r14
            int r3 = r1.getId()
            r14.channel_post = r3
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r6.fwd_from
            int r14 = r3.flags
            r32 = 4
            r14 = r14 | 4
            r3.flags = r14
            boolean r3 = r1.isFromUser()
            if (r3 == 0) goto L_0x029c
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r6.fwd_from
            org.telegram.tgnet.TLRPC$Message r14 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id
            r3.from_id = r14
            int r14 = r3.flags
            r22 = 1
            r14 = r14 | 1
            r3.flags = r14
            r36 = r0
            r35 = r10
            goto L_0x02c8
        L_0x029c:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r6.fwd_from
            org.telegram.tgnet.TLRPC$TL_peerChannel r14 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r14.<init>()
            r3.from_id = r14
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r6.fwd_from
            org.telegram.tgnet.TLRPC$Peer r14 = r3.from_id
            r35 = r10
            org.telegram.tgnet.TLRPC$Message r10 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r15 = r10.peer_id
            r36 = r0
            int r0 = r15.channel_id
            r14.channel_id = r0
            int r0 = r3.flags
            r14 = 1
            r0 = r0 | r14
            r3.flags = r0
            boolean r0 = r10.post
            if (r0 == 0) goto L_0x02c8
            if (r13 <= 0) goto L_0x02c8
            org.telegram.tgnet.TLRPC$Peer r0 = r10.from_id
            if (r0 == 0) goto L_0x02c6
            r15 = r0
        L_0x02c6:
            r3.from_id = r15
        L_0x02c8:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.post_author
            if (r0 == 0) goto L_0x02cf
            goto L_0x0300
        L_0x02cf:
            boolean r0 = r1.isOutOwner()
            if (r0 != 0) goto L_0x0300
            if (r13 <= 0) goto L_0x0300
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            boolean r0 = r0.post
            if (r0 == 0) goto L_0x0300
            org.telegram.messenger.MessagesController r0 = r42.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            if (r0 == 0) goto L_0x0300
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r6.fwd_from
            java.lang.String r10 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r10, r0)
            r3.post_author = r0
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r6.fwd_from
            int r3 = r0.flags
            r10 = 8
            r3 = r3 | r10
            r0.flags = r3
        L_0x0300:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.date
            r6.date = r0
            r0 = 4
            r6.flags = r0
            goto L_0x030e
        L_0x030a:
            r36 = r0
            r35 = r10
        L_0x030e:
            int r0 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x0335
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r6.fwd_from
            if (r0 == 0) goto L_0x0335
            int r3 = r0.flags
            r3 = r3 | 16
            r0.flags = r3
            int r3 = r1.getId()
            r0.saved_from_msg_id = r3
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r6.fwd_from
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            r0.saved_from_peer = r3
            int r0 = r3.user_id
            if (r0 != r9) goto L_0x0335
            long r13 = r1.getDialogId()
            int r0 = (int) r13
            r3.user_id = r0
        L_0x0335:
            if (r20 != 0) goto L_0x0347
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r0 == 0) goto L_0x0347
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r0 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r0.<init>()
            r6.media = r0
            goto L_0x034d
        L_0x0347:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            r6.media = r0
        L_0x034d:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r6.media
            if (r0 == 0) goto L_0x0357
            int r0 = r6.flags
            r0 = r0 | 512(0x200, float:7.175E-43)
            r6.flags = r0
        L_0x0357:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.via_bot_id
            if (r0 == 0) goto L_0x0365
            r6.via_bot_id = r0
            int r0 = r6.flags
            r0 = r0 | 2048(0x800, float:2.87E-42)
            r6.flags = r0
        L_0x0365:
            if (r4 == 0) goto L_0x037f
            org.telegram.tgnet.TLRPC$TL_messageReplies r0 = new org.telegram.tgnet.TLRPC$TL_messageReplies
            r0.<init>()
            r6.replies = r0
            r3 = 1
            r0.comments = r3
            r0.channel_id = r4
            int r10 = r0.flags
            r10 = r10 | r3
            r0.flags = r10
            int r0 = r6.flags
            r3 = 8388608(0x800000, float:1.17549435E-38)
            r0 = r0 | r3
            r6.flags = r0
        L_0x037f:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            r6.message = r0
            int r0 = r1.getId()
            r6.fwd_msg_id = r0
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r3 = r0.attachPath
            r6.attachPath = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r0.entities
            r6.entities = r3
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            if (r0 == 0) goto L_0x044a
            org.telegram.tgnet.TLRPC$TL_replyInlineMarkup r0 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            r0.<init>()
            r6.reply_markup = r0
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r0 = r0.size()
            r3 = 0
            r10 = 0
        L_0x03ae:
            if (r3 >= r0) goto L_0x0437
            org.telegram.tgnet.TLRPC$Message r13 = r1.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r13 = r13.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r13 = r13.rows
            java.lang.Object r13 = r13.get(r3)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r13 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r14 = r13.buttons
            int r14 = r14.size()
            r15 = 0
            r31 = 0
        L_0x03c5:
            r32 = r0
            if (r15 >= r14) goto L_0x0426
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r0 = r13.buttons
            java.lang.Object r0 = r0.get(r15)
            org.telegram.tgnet.TLRPC$KeyboardButton r0 = (org.telegram.tgnet.TLRPC$KeyboardButton) r0
            r37 = r4
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r38 = r7
            if (r4 != 0) goto L_0x03e4
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl
            if (r7 != 0) goto L_0x03e4
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline
            if (r7 == 0) goto L_0x03e2
            goto L_0x03e4
        L_0x03e2:
            r10 = 1
            goto L_0x042a
        L_0x03e4:
            if (r4 == 0) goto L_0x0405
            org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth r4 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r4.<init>()
            int r7 = r0.flags
            r4.flags = r7
            java.lang.String r7 = r0.fwd_text
            if (r7 == 0) goto L_0x03f8
            r4.fwd_text = r7
            r4.text = r7
            goto L_0x03fc
        L_0x03f8:
            java.lang.String r7 = r0.text
            r4.text = r7
        L_0x03fc:
            java.lang.String r7 = r0.url
            r4.url = r7
            int r0 = r0.button_id
            r4.button_id = r0
            r0 = r4
        L_0x0405:
            if (r31 != 0) goto L_0x0414
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r4 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow
            r4.<init>()
            org.telegram.tgnet.TLRPC$ReplyMarkup r7 = r6.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r7 = r7.rows
            r7.add(r4)
            goto L_0x0416
        L_0x0414:
            r4 = r31
        L_0x0416:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r7 = r4.buttons
            r7.add(r0)
            int r15 = r15 + 1
            r31 = r4
            r0 = r32
            r4 = r37
            r7 = r38
            goto L_0x03c5
        L_0x0426:
            r37 = r4
            r38 = r7
        L_0x042a:
            if (r10 == 0) goto L_0x042d
            goto L_0x043b
        L_0x042d:
            int r3 = r3 + 1
            r0 = r32
            r4 = r37
            r7 = r38
            goto L_0x03ae
        L_0x0437:
            r37 = r4
            r38 = r7
        L_0x043b:
            if (r10 != 0) goto L_0x0444
            int r0 = r6.flags
            r0 = r0 | 64
            r6.flags = r0
            goto L_0x044e
        L_0x0444:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            r7 = 0
            r0.reply_markup = r7
            goto L_0x044f
        L_0x044a:
            r37 = r4
            r38 = r7
        L_0x044e:
            r7 = 0
        L_0x044f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r6.entities
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x045d
            int r0 = r6.flags
            r0 = r0 | 128(0x80, float:1.794E-43)
            r6.flags = r0
        L_0x045d:
            java.lang.String r0 = r6.attachPath
            if (r0 != 0) goto L_0x0465
            java.lang.String r0 = ""
            r6.attachPath = r0
        L_0x0465:
            org.telegram.messenger.UserConfig r0 = r42.getUserConfig()
            int r0 = r0.getNewMessageId()
            r6.id = r0
            r6.local_id = r0
            r0 = 1
            r6.out = r0
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            long r3 = r0.grouped_id
            r13 = 0
            int r0 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r0 == 0) goto L_0x04a4
            java.lang.Object r0 = r2.get(r3)
            java.lang.Long r0 = (java.lang.Long) r0
            if (r0 != 0) goto L_0x0497
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r3 = r0.nextLong()
            java.lang.Long r0 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            long r3 = r3.grouped_id
            r2.put(r3, r0)
        L_0x0497:
            long r3 = r0.longValue()
            r6.grouped_id = r3
            int r0 = r6.flags
            r3 = 131072(0x20000, float:1.83671E-40)
            r0 = r0 | r3
            r6.flags = r0
        L_0x04a4:
            r8 = r26
            int r0 = r8.channel_id
            if (r0 == 0) goto L_0x04be
            if (r17 == 0) goto L_0x04be
            if (r16 == 0) goto L_0x04b8
            org.telegram.tgnet.TLRPC$TL_peerUser r0 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r0.<init>()
            r6.from_id = r0
            r0.user_id = r9
            goto L_0x04ba
        L_0x04b8:
            r6.from_id = r8
        L_0x04ba:
            r0 = 1
            r6.post = r0
            goto L_0x04d6
        L_0x04be:
            boolean r0 = org.telegram.messenger.ChatObject.shouldSendAnonymously(r5)
            if (r0 == 0) goto L_0x04c7
            r6.from_id = r8
            goto L_0x04d6
        L_0x04c7:
            org.telegram.tgnet.TLRPC$TL_peerUser r0 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r0.<init>()
            r6.from_id = r0
            r0.user_id = r9
            int r0 = r6.flags
            r0 = r0 | 256(0x100, float:3.59E-43)
            r6.flags = r0
        L_0x04d6:
            long r3 = r6.random_id
            int r0 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x04e2
            long r3 = r42.getNextRandomId()
            r6.random_id = r3
        L_0x04e2:
            long r3 = r6.random_id
            java.lang.Long r0 = java.lang.Long.valueOf(r3)
            r3 = r36
            r3.add(r0)
            long r13 = r6.random_id
            r15 = r30
            r15.put(r13, r6)
            int r0 = r6.fwd_msg_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r13 = r29
            r13.add(r0)
            r14 = r47
            if (r14 == 0) goto L_0x0505
            r0 = r14
            goto L_0x050d
        L_0x0505:
            org.telegram.tgnet.ConnectionsManager r0 = r42.getConnectionsManager()
            int r0 = r0.getCurrentTime()
        L_0x050d:
            r6.date = r0
            r10 = r35
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r0 == 0) goto L_0x0523
            if (r17 == 0) goto L_0x0523
            if (r14 != 0) goto L_0x053a
            r4 = 1
            r6.views = r4
            int r4 = r6.flags
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r6.flags = r4
            goto L_0x053a
        L_0x0523:
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            int r7 = r4.flags
            r7 = r7 & 1024(0x400, float:1.435E-42)
            if (r7 == 0) goto L_0x0537
            if (r14 != 0) goto L_0x0537
            int r4 = r4.views
            r6.views = r4
            int r4 = r6.flags
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r6.flags = r4
        L_0x0537:
            r4 = 1
            r6.unread = r4
        L_0x053a:
            r6.dialog_id = r11
            r6.peer_id = r8
            boolean r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r6)
            if (r4 != 0) goto L_0x054a
            boolean r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r6)
            if (r4 == 0) goto L_0x055c
        L_0x054a:
            if (r0 == 0) goto L_0x0559
            int r0 = r1.getChannelId()
            if (r0 == 0) goto L_0x0559
            boolean r0 = r1.isContentUnread()
            r6.media_unread = r0
            goto L_0x055c
        L_0x0559:
            r0 = 1
            r6.media_unread = r0
        L_0x055c:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r4 == 0) goto L_0x0569
            int r0 = r0.channel_id
            int r0 = -r0
            r6.ttl = r0
        L_0x0569:
            org.telegram.messenger.MessageObject r0 = new org.telegram.messenger.MessageObject
            r7 = r42
            int r4 = r7.currentAccount
            r26 = r2
            r2 = 1
            r0.<init>(r4, r6, r2, r2)
            if (r14 == 0) goto L_0x0579
            r4 = 1
            goto L_0x057a
        L_0x0579:
            r4 = 0
        L_0x057a:
            r0.scheduled = r4
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            r4.send_state = r2
            r0.wasJustSent = r2
            r2 = r34
            r2.add(r0)
            r4 = r24
            r4.add(r6)
            if (r14 == 0) goto L_0x0590
            r0 = 1
            goto L_0x0591
        L_0x0590:
            r0 = 0
        L_0x0591:
            r7.putToSendingMessages(r6, r0)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05cf
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "forward message user_id = "
            r0.append(r6)
            int r6 = r10.user_id
            r0.append(r6)
            java.lang.String r6 = " chat_id = "
            r0.append(r6)
            int r6 = r10.chat_id
            r0.append(r6)
            java.lang.String r6 = " channel_id = "
            r0.append(r6)
            int r6 = r10.channel_id
            r0.append(r6)
            java.lang.String r6 = " access_hash = "
            r0.append(r6)
            r24 = r5
            long r5 = r10.access_hash
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x05d1
        L_0x05cf:
            r24 = r5
        L_0x05d1:
            int r0 = r4.size()
            r5 = 100
            if (r0 == r5) goto L_0x0621
            int r0 = r43.size()
            r5 = 1
            int r0 = r0 - r5
            r6 = r33
            if (r6 == r0) goto L_0x061e
            int r0 = r43.size()
            int r0 = r0 - r5
            if (r6 == r0) goto L_0x0601
            int r0 = r6 + 1
            r5 = r43
            java.lang.Object r0 = r5.get(r0)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r29 = r0.getDialogId()
            long r31 = r1.getDialogId()
            int r0 = (r29 > r31 ? 1 : (r29 == r31 ? 0 : -1))
            if (r0 == 0) goto L_0x0603
            goto L_0x0625
        L_0x0601:
            r5 = r43
        L_0x0603:
            r35 = r2
            r32 = r3
            r25 = r4
            r30 = r8
            r41 = r9
            r34 = r10
            r31 = r13
            r36 = r37
            r37 = r38
            r27 = 0
            r29 = 0
            r33 = 1
            r13 = r6
            goto L_0x07cb
        L_0x061e:
            r5 = r43
            goto L_0x0625
        L_0x0621:
            r5 = r43
            r6 = r33
        L_0x0625:
            org.telegram.messenger.MessagesStorage r29 = r42.getMessagesStorage()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r4)
            r31 = 0
            r32 = 1
            r33 = 0
            r34 = 0
            if (r14 == 0) goto L_0x063b
            r35 = 1
            goto L_0x063d
        L_0x063b:
            r35 = 0
        L_0x063d:
            r30 = r0
            r29.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r30, (boolean) r31, (boolean) r32, (boolean) r33, (int) r34, (boolean) r35)
            org.telegram.messenger.MessagesController r0 = r42.getMessagesController()
            r29 = r4
            if (r14 == 0) goto L_0x064c
            r4 = 1
            goto L_0x064d
        L_0x064c:
            r4 = 0
        L_0x064d:
            r0.updateInterfaceWithMessages(r11, r2, r4)
            org.telegram.messenger.NotificationCenter r0 = r42.getNotificationCenter()
            int r4 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r33 = r6
            r30 = r8
            r6 = 0
            java.lang.Object[] r8 = new java.lang.Object[r6]
            r0.postNotificationName(r4, r8)
            org.telegram.messenger.UserConfig r0 = r42.getUserConfig()
            r0.saveConfig(r6)
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r8 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages
            r8.<init>()
            r8.to_peer = r10
            if (r46 == 0) goto L_0x0691
            int r0 = r7.currentAccount
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "silent_"
            r4.append(r6)
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            r6 = 0
            boolean r0 = r0.getBoolean(r4, r6)
            if (r0 == 0) goto L_0x068f
            goto L_0x0691
        L_0x068f:
            r0 = 0
            goto L_0x0692
        L_0x0691:
            r0 = 1
        L_0x0692:
            r8.silent = r0
            if (r14 == 0) goto L_0x069e
            r8.schedule_date = r14
            int r0 = r8.flags
            r0 = r0 | 1024(0x400, float:1.435E-42)
            r8.flags = r0
        L_0x069e:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r0 == 0) goto L_0x06cf
            org.telegram.messenger.MessagesController r0 = r42.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            int r4 = r4.channel_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            r4.<init>()
            r8.from_peer = r4
            org.telegram.tgnet.TLRPC$Message r6 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            int r6 = r6.channel_id
            r4.channel_id = r6
            r6 = r1
            if (r0 == 0) goto L_0x06d7
            long r0 = r0.access_hash
            r4.access_hash = r0
            goto L_0x06d7
        L_0x06cf:
            r6 = r1
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r0.<init>()
            r8.from_peer = r0
        L_0x06d7:
            r8.random_id = r3
            r8.id = r13
            int r0 = r43.size()
            r4 = 1
            r1 = 0
            if (r0 != r4) goto L_0x06f1
            java.lang.Object r0 = r5.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0.with_my_score
            if (r0 == 0) goto L_0x06f1
            r0 = 1
            goto L_0x06f2
        L_0x06f1:
            r0 = 0
        L_0x06f2:
            r8.with_my_score = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r2)
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r14 != r1) goto L_0x0701
            r22 = 1
            goto L_0x0703
        L_0x0701:
            r22 = 0
        L_0x0703:
            org.telegram.tgnet.ConnectionsManager r1 = r42.getConnectionsManager()
            r31 = r13
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$S_9fl6ACrfKnQfdTXc8UzVMP9RI r13 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$S_9fl6ACrfKnQfdTXc8UzVMP9RI
            r34 = r0
            r32 = r3
            r0 = r13
            r3 = r1
            r25 = r29
            r29 = 0
            r1 = r42
            r35 = r2
            r14 = r3
            r2 = r44
            r36 = r37
            r37 = 1
            r4 = r47
            r5 = r22
            r22 = r6
            r40 = r33
            r33 = 1
            r6 = r28
            r37 = r38
            r27 = 0
            r7 = r15
            r39 = r8
            r8 = r25
            r41 = r9
            r9 = r34
            r34 = r10
            r10 = r22
            r11 = r30
            r12 = r39
            r0.<init>(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r0 = 68
            r1 = r39
            r14.sendRequest(r1, r13, r0)
            int r0 = r43.size()
            int r0 = r0 + -1
            r13 = r40
            if (r13 == r0) goto L_0x07cb
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
            r31 = r3
            r15 = r4
            r3 = r0
            r0 = r2
            goto L_0x07d1
        L_0x0775:
            r32 = r0
            r35 = r3
            r36 = r4
            r13 = r6
            r37 = r7
            r41 = r9
            r34 = r10
            r25 = r24
            r31 = r29
            r15 = r30
            r27 = 0
            r29 = 0
            r33 = 1
            r24 = r5
            r30 = r26
            r26 = r2
            int r0 = r1.type
            if (r0 != 0) goto L_0x07cb
            java.lang.CharSequence r0 = r1.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x07cb
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x07aa
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            r6 = r0
            goto L_0x07ac
        L_0x07aa:
            r6 = r27
        L_0x07ac:
            java.lang.CharSequence r0 = r1.messageText
            java.lang.String r2 = r0.toString()
            r4 = 0
            r5 = 0
            if (r6 == 0) goto L_0x07b8
            r7 = 1
            goto L_0x07b9
        L_0x07b8:
            r7 = 0
        L_0x07b9:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r0.entities
            r9 = 0
            r10 = 0
            r0 = r42
            r1 = r2
            r2 = r44
            r11 = r46
            r12 = r47
            r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
        L_0x07cb:
            r1 = r25
            r0 = r32
            r3 = r35
        L_0x07d1:
            int r6 = r13 + 1
            r13 = r42
            r14 = r43
            r11 = r44
            r5 = r24
            r2 = r26
            r26 = r30
            r29 = r31
            r10 = r34
            r4 = r36
            r7 = r37
            r9 = r41
            r24 = r1
            r30 = r15
            r15 = r47
            goto L_0x00d2
        L_0x07f1:
            r2 = r42
            r10 = r23
            goto L_0x0814
        L_0x07f6:
            r29 = 0
            r10 = 0
        L_0x07f9:
            int r0 = r43.size()
            if (r10 >= r0) goto L_0x0811
            r0 = r43
            java.lang.Object r1 = r0.get(r10)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r2 = r42
            r3 = r44
            r2.processForwardFromMyName(r1, r3)
            int r10 = r10 + 1
            goto L_0x07f9
        L_0x0811:
            r2 = r42
            r10 = 0
        L_0x0814:
            return r10
        L_0x0815:
            r2 = r13
            r29 = 0
            return r29
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, int):int");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x01a9  */
    /* renamed from: lambda$sendMessage$14 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendMessage$14$SendMessagesHelper(long r27, int r29, boolean r30, boolean r31, android.util.LongSparseArray r32, java.util.ArrayList r33, java.util.ArrayList r34, org.telegram.messenger.MessageObject r35, org.telegram.tgnet.TLRPC$Peer r36, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r37, org.telegram.tgnet.TLObject r38, org.telegram.tgnet.TLRPC$TL_error r39) {
        /*
            r26 = this;
            r11 = r26
            r12 = r29
            r13 = r33
            r14 = r34
            r0 = r39
            r15 = 1
            if (r0 != 0) goto L_0x01dd
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
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r0.dialogs_read_outbox_max
            java.lang.Long r1 = java.lang.Long.valueOf(r27)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x006b
            org.telegram.messenger.MessagesStorage r0 = r26.getMessagesStorage()
            r5 = r27
            int r0 = r0.getDialogReadMax(r15, r5)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            java.lang.Long r2 = java.lang.Long.valueOf(r27)
            r1.put(r2, r0)
            goto L_0x006d
        L_0x006b:
            r5 = r27
        L_0x006d:
            r16 = r0
            r0 = 0
            r8 = 0
        L_0x0071:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r7.updates
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x01bf
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
            r15 = r7
            r19 = r9
            r1 = 1
            r13 = 0
            goto L_0x01b0
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
            org.telegram.messenger.MessagesController r4 = r26.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            r4.processNewDifferenceParams(r0, r10, r0, r1)
        L_0x00b3:
            r10 = r2
            goto L_0x00d3
        L_0x00b5:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x00bf
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage) r1
            org.telegram.tgnet.TLRPC$Message r1 = r1.message
            r10 = r1
            goto L_0x00d3
        L_0x00bf:
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage) r1
            org.telegram.tgnet.TLRPC$Message r2 = r1.message
            org.telegram.messenger.MessagesController r4 = r26.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            org.telegram.tgnet.TLRPC$Peer r0 = r2.peer_id
            int r0 = r0.channel_id
            r4.processNewChannelDifferenceParams(r10, r1, r0)
            goto L_0x00b3
        L_0x00d3:
            if (r30 == 0) goto L_0x00df
            int r0 = r10.date
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 == r1) goto L_0x00df
            r19 = 0
            goto L_0x00e1
        L_0x00df:
            r19 = r3
        L_0x00e1:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r10)
            if (r19 != 0) goto L_0x00f3
            int r0 = r16.intValue()
            int r1 = r10.id
            if (r0 >= r1) goto L_0x00f0
            r0 = 1
            goto L_0x00f1
        L_0x00f0:
            r0 = 0
        L_0x00f1:
            r10.unread = r0
        L_0x00f3:
            if (r31 == 0) goto L_0x00fd
            r10.out = r15
            r4 = 0
            r10.unread = r4
            r10.media_unread = r4
            goto L_0x00fe
        L_0x00fd:
            r4 = 0
        L_0x00fe:
            int r0 = r10.id
            long r0 = r9.get(r0)
            r2 = 0
            int r18 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r18 == 0) goto L_0x01a9
            r3 = r32
            java.lang.Object r0 = r3.get(r0)
            r2 = r0
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC$Message) r2
            if (r2 != 0) goto L_0x0117
            goto L_0x01a9
        L_0x0117:
            int r0 = r13.indexOf(r2)
            r1 = -1
            if (r0 != r1) goto L_0x0120
            goto L_0x01a9
        L_0x0120:
            java.lang.Object r1 = r14.get(r0)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r13.remove(r0)
            r14.remove(r0)
            int r0 = r2.id
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            r15.add(r10)
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            r38 = r0
            java.lang.String r0 = r10.post_author
            r4.post_author = r0
            int r4 = r10.id
            r20 = 0
            r21 = 1
            r22 = r38
            r0 = r26
            r23 = r1
            r24 = r2
            r2 = r10
            r3 = r4
            r25 = 0
            r4 = r20
            r5 = r21
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r20 = r23.getMediaExistanceFlags()
            int r0 = r10.id
            r3 = r24
            r3.id = r0
            int r21 = r8 + 1
            if (r12 == 0) goto L_0x017e
            if (r19 != 0) goto L_0x017e
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$tYmOflOPntSUE6Q4m9ZveJNGZFY r8 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$tYmOflOPntSUE6Q4m9ZveJNGZFY
            r0 = r8
            r1 = r26
            r2 = r22
            r4 = r15
            r5 = r35
            r6 = r29
            r0.<init>(r2, r3, r4, r5, r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8)
            r15 = r7
            r19 = r9
            r13 = 0
            goto L_0x01a4
        L_0x017e:
            org.telegram.messenger.MessagesStorage r0 = r26.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$ii0tP_7nBpP4GsJdzG0pHHuxSVA r6 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$ii0tP_7nBpP4GsJdzG0pHHuxSVA
            r0 = r6
            r1 = r26
            r2 = r3
            r3 = r22
            r4 = r36
            r5 = r29
            r14 = r6
            r6 = r15
            r15 = r7
            r12 = r8
            r7 = r27
            r19 = r9
            r9 = r10
            r13 = 0
            r10 = r20
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10)
            r12.postRunnable(r14)
        L_0x01a4:
            r0 = r17
            r8 = r21
            goto L_0x01af
        L_0x01a9:
            r15 = r7
            r19 = r9
            r13 = 0
            r0 = r17
        L_0x01af:
            r1 = 1
        L_0x01b0:
            int r0 = r0 + r1
            r5 = r27
            r12 = r29
            r13 = r33
            r14 = r34
            r7 = r15
            r9 = r19
            r15 = 1
            goto L_0x0071
        L_0x01bf:
            r15 = r7
            r1 = 1
            r13 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r0 = r15.updates
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01d1
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            r0.processUpdates(r15, r13)
        L_0x01d1:
            org.telegram.messenger.StatsController r0 = r26.getStatsController()
            int r2 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r0.incrementSentItemsCount(r2, r1, r8)
            goto L_0x01e9
        L_0x01dd:
            r1 = 1
            r13 = 0
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$1ZpmZtZuzHCt9PtLfFId-0gqQyE r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$1ZpmZtZuzHCt9PtLfFId-0gqQyE
            r3 = r37
            r2.<init>(r0, r3)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x01e9:
            r10 = 0
        L_0x01ea:
            int r0 = r33.size()
            if (r10 >= r0) goto L_0x0213
            r0 = r33
            r2 = 0
            java.lang.Object r3 = r0.get(r10)
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            org.telegram.messenger.MessagesStorage r4 = r26.getMessagesStorage()
            r5 = r29
            if (r5 == 0) goto L_0x0203
            r6 = 1
            goto L_0x0204
        L_0x0203:
            r6 = 0
        L_0x0204:
            r4.markMessageAsSendError(r3, r6)
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$i_e5HlVBOC4IMhnPRqTRpjcaKQY r4 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$i_e5HlVBOC4IMhnPRqTRpjcaKQY
            r4.<init>(r3, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            int r10 = r10 + 1
            r13 = 0
            goto L_0x01ea
        L_0x0213:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendMessage$14$SendMessagesHelper(long, int, boolean, boolean, android.util.LongSparseArray, java.util.ArrayList, java.util.ArrayList, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Peer, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$9 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$11 */
    public /* synthetic */ void lambda$null$11$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, TLRPC$Peer tLRPC$Peer, int i2, ArrayList arrayList, long j, TLRPC$Message tLRPC$Message2, int i3) {
        TLRPC$Message tLRPC$Message3 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message3.random_id, Long.valueOf((long) i), tLRPC$Message3.id, 0, false, tLRPC$Peer.channel_id, i2 != 0 ? 1 : 0);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$12 */
    public /* synthetic */ void lambda$null$12$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_forwardMessages tLRPC$TL_messages_forwardMessages) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, (BaseFragment) null, tLRPC$TL_messages_forwardMessages, new Object[0]);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$13 */
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
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia == null) {
            new TLRPC$TL_messageMediaEmpty().serializeToStream(serializedData);
        } else {
            tLRPC$MessageMedia.serializeToStream(serializedData);
        }
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
    /* JADX WARNING: Removed duplicated region for block: B:122:0x024c A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0258 A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0260  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0272  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x004b A[SYNTHETIC, Splitter:B:19:0x004b] */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0472 A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x047e A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0490 A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x04dc A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x04e1 A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x04f0 A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00a6 A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0149 A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0167 A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x018f A[Catch:{ Exception -> 0x0565 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void editMessage(org.telegram.messenger.MessageObject r28, org.telegram.tgnet.TLRPC$TL_photo r29, org.telegram.messenger.VideoEditedInfo r30, org.telegram.tgnet.TLRPC$TL_document r31, java.lang.String r32, java.util.HashMap<java.lang.String, java.lang.String> r33, boolean r34, java.lang.Object r35) {
        /*
            r27 = this;
            r11 = r27
            r12 = r28
            r0 = r29
            r1 = r31
            r2 = r32
            java.lang.String r3 = "originalPath"
            java.lang.String r4 = "parentObject"
            if (r12 != 0) goto L_0x0011
            return
        L_0x0011:
            if (r33 != 0) goto L_0x0019
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            goto L_0x001b
        L_0x0019:
            r5 = r33
        L_0x001b:
            org.telegram.tgnet.TLRPC$Message r6 = r12.messageOwner
            r7 = 0
            r12.cancelEditing = r7
            long r8 = r28.getDialogId()     // Catch:{ Exception -> 0x0565 }
            int r10 = (int) r8     // Catch:{ Exception -> 0x0565 }
            if (r10 != 0) goto L_0x0046
            r14 = 32
            long r14 = r8 >> r14
            int r15 = (int) r14     // Catch:{ Exception -> 0x0565 }
            org.telegram.messenger.MessagesController r14 = r27.getMessagesController()     // Catch:{ Exception -> 0x0565 }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$EncryptedChat r14 = r14.getEncryptedChat(r15)     // Catch:{ Exception -> 0x0565 }
            if (r14 == 0) goto L_0x0044
            int r14 = r14.layer     // Catch:{ Exception -> 0x0565 }
            int r14 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r14)     // Catch:{ Exception -> 0x0565 }
            r15 = 101(0x65, float:1.42E-43)
            if (r14 >= r15) goto L_0x0046
        L_0x0044:
            r14 = 0
            goto L_0x0047
        L_0x0046:
            r14 = 1
        L_0x0047:
            java.lang.String r15 = "http"
            if (r34 == 0) goto L_0x00a6
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media     // Catch:{ Exception -> 0x0565 }
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x0565 }
            if (r5 != 0) goto L_0x0081
            if (r2 == 0) goto L_0x0081
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x0565 }
            if (r5 == 0) goto L_0x005a
            goto L_0x0081
        L_0x005a:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0565 }
            if (r5 == 0) goto L_0x0066
            org.telegram.tgnet.TLRPC$Photo r0 = r2.photo     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0     // Catch:{ Exception -> 0x0565 }
            r5 = r30
            r2 = 2
            goto L_0x0084
        L_0x0066:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0565 }
            if (r5 == 0) goto L_0x007d
            org.telegram.tgnet.TLRPC$Document r1 = r2.document     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC$TL_document) r1     // Catch:{ Exception -> 0x0565 }
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1)     // Catch:{ Exception -> 0x0565 }
            if (r2 != 0) goto L_0x0079
            if (r30 == 0) goto L_0x0077
            goto L_0x0079
        L_0x0077:
            r2 = 7
            goto L_0x007a
        L_0x0079:
            r2 = 3
        L_0x007a:
            org.telegram.messenger.VideoEditedInfo r5 = r12.videoEditedInfo     // Catch:{ Exception -> 0x0565 }
            goto L_0x0084
        L_0x007d:
            r5 = r30
            r2 = -1
            goto L_0x0084
        L_0x0081:
            r5 = r30
            r2 = 1
        L_0x0084:
            java.util.HashMap<java.lang.String, java.lang.String> r7 = r6.params     // Catch:{ Exception -> 0x0565 }
            if (r35 != 0) goto L_0x0095
            if (r7 == 0) goto L_0x0095
            boolean r17 = r7.containsKey(r4)     // Catch:{ Exception -> 0x0565 }
            if (r17 == 0) goto L_0x0095
            java.lang.Object r4 = r7.get(r4)     // Catch:{ Exception -> 0x0565 }
            goto L_0x0097
        L_0x0095:
            r4 = r35
        L_0x0097:
            java.lang.String r13 = r6.message     // Catch:{ Exception -> 0x0565 }
            r12.editingMessage = r13     // Catch:{ Exception -> 0x0565 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r13 = r6.entities     // Catch:{ Exception -> 0x0565 }
            r12.editingMessageEntities = r13     // Catch:{ Exception -> 0x0565 }
            java.lang.String r13 = r6.attachPath     // Catch:{ Exception -> 0x0565 }
            r18 = r10
            r10 = r4
            goto L_0x0163
        L_0x00a6:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r6.media     // Catch:{ Exception -> 0x0565 }
            r12.previousMedia = r4     // Catch:{ Exception -> 0x0565 }
            java.lang.String r7 = r6.message     // Catch:{ Exception -> 0x0565 }
            r12.previousMessage = r7     // Catch:{ Exception -> 0x0565 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r6.entities     // Catch:{ Exception -> 0x0565 }
            r12.previousMessageEntities = r7     // Catch:{ Exception -> 0x0565 }
            java.lang.String r7 = r6.attachPath     // Catch:{ Exception -> 0x0565 }
            r12.previousAttachPath = r7     // Catch:{ Exception -> 0x0565 }
            if (r4 != 0) goto L_0x00bd
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x0565 }
            r4.<init>()     // Catch:{ Exception -> 0x0565 }
        L_0x00bd:
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0565 }
            r7 = 1
            r4.<init>((boolean) r7)     // Catch:{ Exception -> 0x0565 }
            r11.writePreviousMessageData(r6, r4)     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.SerializedData r7 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0565 }
            int r4 = r4.length()     // Catch:{ Exception -> 0x0565 }
            r7.<init>((int) r4)     // Catch:{ Exception -> 0x0565 }
            r11.writePreviousMessageData(r6, r7)     // Catch:{ Exception -> 0x0565 }
            if (r5 != 0) goto L_0x00d9
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ Exception -> 0x0565 }
            r5.<init>()     // Catch:{ Exception -> 0x0565 }
        L_0x00d9:
            java.lang.String r4 = "prevMedia"
            byte[] r13 = r7.toByteArray()     // Catch:{ Exception -> 0x0565 }
            r18 = r10
            r10 = 0
            java.lang.String r13 = android.util.Base64.encodeToString(r13, r10)     // Catch:{ Exception -> 0x0565 }
            r5.put(r4, r13)     // Catch:{ Exception -> 0x0565 }
            r7.cleanup()     // Catch:{ Exception -> 0x0565 }
            if (r0 == 0) goto L_0x012a
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0565 }
            r4.<init>()     // Catch:{ Exception -> 0x0565 }
            r6.media = r4     // Catch:{ Exception -> 0x0565 }
            int r7 = r4.flags     // Catch:{ Exception -> 0x0565 }
            r10 = 3
            r7 = r7 | r10
            r4.flags = r7     // Catch:{ Exception -> 0x0565 }
            r4.photo = r0     // Catch:{ Exception -> 0x0565 }
            if (r2 == 0) goto L_0x010e
            int r4 = r32.length()     // Catch:{ Exception -> 0x0565 }
            if (r4 <= 0) goto L_0x010e
            boolean r4 = r2.startsWith(r15)     // Catch:{ Exception -> 0x0565 }
            if (r4 == 0) goto L_0x010e
            r6.attachPath = r2     // Catch:{ Exception -> 0x0565 }
            goto L_0x0128
        L_0x010e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r0.sizes     // Catch:{ Exception -> 0x0565 }
            int r7 = r4.size()     // Catch:{ Exception -> 0x0565 }
            r10 = 1
            int r7 = r7 - r10
            java.lang.Object r4 = r4.get(r7)     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = (org.telegram.tgnet.TLRPC$PhotoSize) r4     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.location     // Catch:{ Exception -> 0x0565 }
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r4, r10)     // Catch:{ Exception -> 0x0565 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0565 }
            r6.attachPath = r4     // Catch:{ Exception -> 0x0565 }
        L_0x0128:
            r4 = 2
            goto L_0x0157
        L_0x012a:
            if (r1 == 0) goto L_0x0156
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0565 }
            r4.<init>()     // Catch:{ Exception -> 0x0565 }
            r6.media = r4     // Catch:{ Exception -> 0x0565 }
            int r7 = r4.flags     // Catch:{ Exception -> 0x0565 }
            r10 = 3
            r7 = r7 | r10
            r4.flags = r7     // Catch:{ Exception -> 0x0565 }
            r4.document = r1     // Catch:{ Exception -> 0x0565 }
            boolean r4 = org.telegram.messenger.MessageObject.isVideoDocument(r31)     // Catch:{ Exception -> 0x0565 }
            if (r4 != 0) goto L_0x0146
            if (r30 == 0) goto L_0x0144
            goto L_0x0146
        L_0x0144:
            r4 = 7
            goto L_0x0147
        L_0x0146:
            r4 = 3
        L_0x0147:
            if (r30 == 0) goto L_0x0153
            java.lang.String r7 = r30.getString()     // Catch:{ Exception -> 0x0565 }
            java.lang.String r10 = "ve"
            r5.put(r10, r7)     // Catch:{ Exception -> 0x0565 }
        L_0x0153:
            r6.attachPath = r2     // Catch:{ Exception -> 0x0565 }
            goto L_0x0157
        L_0x0156:
            r4 = 1
        L_0x0157:
            r6.params = r5     // Catch:{ Exception -> 0x0565 }
            r7 = 3
            r6.send_state = r7     // Catch:{ Exception -> 0x0565 }
            r10 = r35
            r13 = r2
            r2 = r4
            r7 = r5
            r5 = r30
        L_0x0163:
            java.lang.String r4 = r6.attachPath     // Catch:{ Exception -> 0x0565 }
            if (r4 != 0) goto L_0x016b
            java.lang.String r4 = ""
            r6.attachPath = r4     // Catch:{ Exception -> 0x0565 }
        L_0x016b:
            r4 = 0
            r6.local_id = r4     // Catch:{ Exception -> 0x0565 }
            int r4 = r12.type     // Catch:{ Exception -> 0x0565 }
            r29 = r1
            r1 = 3
            if (r4 == r1) goto L_0x017a
            if (r5 != 0) goto L_0x017a
            r1 = 2
            if (r4 != r1) goto L_0x0185
        L_0x017a:
            java.lang.String r1 = r6.attachPath     // Catch:{ Exception -> 0x0565 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0565 }
            if (r1 != 0) goto L_0x0185
            r1 = 1
            r12.attachPathExists = r1     // Catch:{ Exception -> 0x0565 }
        L_0x0185:
            org.telegram.messenger.VideoEditedInfo r1 = r12.videoEditedInfo     // Catch:{ Exception -> 0x0565 }
            if (r1 == 0) goto L_0x018c
            if (r5 != 0) goto L_0x018c
            r5 = r1
        L_0x018c:
            r1 = 0
            if (r34 != 0) goto L_0x024c
            java.lang.CharSequence r4 = r12.editingMessage     // Catch:{ Exception -> 0x0565 }
            if (r4 == 0) goto L_0x01ea
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0565 }
            r6.message = r4     // Catch:{ Exception -> 0x0565 }
            r12.caption = r1     // Catch:{ Exception -> 0x0565 }
            r4 = 1
            if (r2 != r4) goto L_0x01b2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r12.editingMessageEntities     // Catch:{ Exception -> 0x0565 }
            if (r4 == 0) goto L_0x01ab
            r6.entities = r4     // Catch:{ Exception -> 0x0565 }
            int r4 = r6.flags     // Catch:{ Exception -> 0x0565 }
            r4 = r4 | 128(0x80, float:1.794E-43)
            r6.flags = r4     // Catch:{ Exception -> 0x0565 }
            goto L_0x01ea
        L_0x01ab:
            int r4 = r6.flags     // Catch:{ Exception -> 0x0565 }
            r4 = r4 & -129(0xffffffffffffff7f, float:NaN)
            r6.flags = r4     // Catch:{ Exception -> 0x0565 }
            goto L_0x01ea
        L_0x01b2:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r12.editingMessageEntities     // Catch:{ Exception -> 0x0565 }
            if (r4 == 0) goto L_0x01bf
            r6.entities = r4     // Catch:{ Exception -> 0x0565 }
            int r4 = r6.flags     // Catch:{ Exception -> 0x0565 }
            r4 = r4 | 128(0x80, float:1.794E-43)
            r6.flags = r4     // Catch:{ Exception -> 0x0565 }
            goto L_0x01e7
        L_0x01bf:
            r4 = 1
            java.lang.CharSequence[] r1 = new java.lang.CharSequence[r4]     // Catch:{ Exception -> 0x0565 }
            java.lang.CharSequence r4 = r12.editingMessage     // Catch:{ Exception -> 0x0565 }
            r19 = 0
            r1[r19] = r4     // Catch:{ Exception -> 0x0565 }
            org.telegram.messenger.MediaDataController r4 = r27.getMediaDataController()     // Catch:{ Exception -> 0x0565 }
            java.util.ArrayList r1 = r4.getEntities(r1, r14)     // Catch:{ Exception -> 0x0565 }
            if (r1 == 0) goto L_0x01e1
            boolean r4 = r1.isEmpty()     // Catch:{ Exception -> 0x0565 }
            if (r4 != 0) goto L_0x01e1
            r6.entities = r1     // Catch:{ Exception -> 0x0565 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x0565 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x0565 }
            goto L_0x01e7
        L_0x01e1:
            int r1 = r6.flags     // Catch:{ Exception -> 0x0565 }
            r1 = r1 & -129(0xffffffffffffff7f, float:NaN)
            r6.flags = r1     // Catch:{ Exception -> 0x0565 }
        L_0x01e7:
            r28.generateCaption()     // Catch:{ Exception -> 0x0565 }
        L_0x01ea:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0565 }
            r1.<init>()     // Catch:{ Exception -> 0x0565 }
            r1.add(r6)     // Catch:{ Exception -> 0x0565 }
            org.telegram.messenger.MessagesStorage r19 = r27.getMessagesStorage()     // Catch:{ Exception -> 0x0565 }
            r21 = 0
            r22 = 1
            r23 = 0
            r24 = 0
            boolean r4 = r12.scheduled     // Catch:{ Exception -> 0x0565 }
            r20 = r1
            r25 = r4
            r19.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r20, (boolean) r21, (boolean) r22, (boolean) r23, (int) r24, (boolean) r25)     // Catch:{ Exception -> 0x0565 }
            r1 = -1
            r12.type = r1     // Catch:{ Exception -> 0x0565 }
            r28.setType()     // Catch:{ Exception -> 0x0565 }
            r1 = 1
            if (r2 != r1) goto L_0x0225
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media     // Catch:{ Exception -> 0x0565 }
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0565 }
            if (r4 != 0) goto L_0x0222
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0565 }
            if (r1 == 0) goto L_0x021d
            goto L_0x0222
        L_0x021d:
            r1 = 0
            r12.generateLayout(r1)     // Catch:{ Exception -> 0x0565 }
            goto L_0x0225
        L_0x0222:
            r28.generateCaption()     // Catch:{ Exception -> 0x0565 }
        L_0x0225:
            r28.createMessageSendInfo()     // Catch:{ Exception -> 0x0565 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0565 }
            r1.<init>()     // Catch:{ Exception -> 0x0565 }
            r1.add(r12)     // Catch:{ Exception -> 0x0565 }
            org.telegram.messenger.NotificationCenter r4 = r27.getNotificationCenter()     // Catch:{ Exception -> 0x0565 }
            int r6 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects     // Catch:{ Exception -> 0x0565 }
            r31 = r5
            r16 = r14
            r14 = 2
            java.lang.Object[] r5 = new java.lang.Object[r14]     // Catch:{ Exception -> 0x0565 }
            java.lang.Long r14 = java.lang.Long.valueOf(r8)     // Catch:{ Exception -> 0x0565 }
            r19 = 0
            r5[r19] = r14     // Catch:{ Exception -> 0x0565 }
            r14 = 1
            r5[r14] = r1     // Catch:{ Exception -> 0x0565 }
            r4.postNotificationName(r6, r5)     // Catch:{ Exception -> 0x0565 }
            goto L_0x0250
        L_0x024c:
            r31 = r5
            r16 = r14
        L_0x0250:
            if (r7 == 0) goto L_0x0260
            boolean r1 = r7.containsKey(r3)     // Catch:{ Exception -> 0x0565 }
            if (r1 == 0) goto L_0x0260
            java.lang.Object r1 = r7.get(r3)     // Catch:{ Exception -> 0x0565 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0565 }
            r4 = r1
            goto L_0x0261
        L_0x0260:
            r4 = 0
        L_0x0261:
            r1 = 8
            r3 = 1
            if (r2 < r3) goto L_0x0269
            r5 = 3
            if (r2 <= r5) goto L_0x026e
        L_0x0269:
            r5 = 5
            if (r2 < r5) goto L_0x056c
            if (r2 > r1) goto L_0x056c
        L_0x026e:
            if (r2 != r3) goto L_0x0272
            goto L_0x0456
        L_0x0272:
            java.lang.String r3 = "masks"
            r14 = 2
            if (r2 != r14) goto L_0x0329
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r14 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x0565 }
            r14.<init>()     // Catch:{ Exception -> 0x0565 }
            if (r7 == 0) goto L_0x02bc
            java.lang.Object r3 = r7.get(r3)     // Catch:{ Exception -> 0x0565 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0565 }
            if (r3 == 0) goto L_0x02bc
            org.telegram.tgnet.SerializedData r1 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0565 }
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x0565 }
            r1.<init>((byte[]) r3)     // Catch:{ Exception -> 0x0565 }
            r3 = 0
            int r5 = r1.readInt32(r3)     // Catch:{ Exception -> 0x0565 }
            r6 = 0
        L_0x0295:
            if (r6 >= r5) goto L_0x02b0
            r29 = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r14.stickers     // Catch:{ Exception -> 0x0565 }
            r19 = r7
            int r7 = r1.readInt32(r3)     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$InputDocument r7 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r1, r7, r3)     // Catch:{ Exception -> 0x0565 }
            r5.add(r7)     // Catch:{ Exception -> 0x0565 }
            int r6 = r6 + 1
            r5 = r29
            r7 = r19
            r3 = 0
            goto L_0x0295
        L_0x02b0:
            r19 = r7
            int r3 = r14.flags     // Catch:{ Exception -> 0x0565 }
            r5 = 1
            r3 = r3 | r5
            r14.flags = r3     // Catch:{ Exception -> 0x0565 }
            r1.cleanup()     // Catch:{ Exception -> 0x0565 }
            goto L_0x02be
        L_0x02bc:
            r19 = r7
        L_0x02be:
            long r5 = r0.access_hash     // Catch:{ Exception -> 0x0565 }
            r20 = 0
            int r1 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1))
            if (r1 != 0) goto L_0x02c9
            r3 = r14
            r1 = 1
            goto L_0x02ea
        L_0x02c9:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x0565 }
            r1.<init>()     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r3 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x0565 }
            r3.<init>()     // Catch:{ Exception -> 0x0565 }
            r1.id = r3     // Catch:{ Exception -> 0x0565 }
            long r5 = r0.id     // Catch:{ Exception -> 0x0565 }
            r3.id = r5     // Catch:{ Exception -> 0x0565 }
            long r5 = r0.access_hash     // Catch:{ Exception -> 0x0565 }
            r3.access_hash = r5     // Catch:{ Exception -> 0x0565 }
            byte[] r5 = r0.file_reference     // Catch:{ Exception -> 0x0565 }
            r3.file_reference = r5     // Catch:{ Exception -> 0x0565 }
            if (r5 != 0) goto L_0x02e8
            r5 = 0
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x0565 }
            r3.file_reference = r6     // Catch:{ Exception -> 0x0565 }
        L_0x02e8:
            r3 = r1
            r1 = 0
        L_0x02ea:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r5 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0565 }
            r5.<init>(r8)     // Catch:{ Exception -> 0x0565 }
            r6 = 0
            r5.type = r6     // Catch:{ Exception -> 0x0565 }
            r5.obj = r12     // Catch:{ Exception -> 0x0565 }
            r5.originalPath = r4     // Catch:{ Exception -> 0x0565 }
            r5.parentObject = r10     // Catch:{ Exception -> 0x0565 }
            r5.inputUploadMedia = r14     // Catch:{ Exception -> 0x0565 }
            r5.performMediaUpload = r1     // Catch:{ Exception -> 0x0565 }
            if (r13 == 0) goto L_0x030d
            int r6 = r13.length()     // Catch:{ Exception -> 0x0565 }
            if (r6 <= 0) goto L_0x030d
            boolean r6 = r13.startsWith(r15)     // Catch:{ Exception -> 0x0565 }
            if (r6 == 0) goto L_0x030d
            r5.httpLocation = r13     // Catch:{ Exception -> 0x0565 }
            goto L_0x031f
        L_0x030d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes     // Catch:{ Exception -> 0x0565 }
            int r7 = r6.size()     // Catch:{ Exception -> 0x0565 }
            r8 = 1
            int r7 = r7 - r8
            java.lang.Object r6 = r6.get(r7)     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC$PhotoSize) r6     // Catch:{ Exception -> 0x0565 }
            r5.photoSize = r6     // Catch:{ Exception -> 0x0565 }
            r5.locationParent = r0     // Catch:{ Exception -> 0x0565 }
        L_0x031f:
            r13 = r5
            r7 = r19
            r26 = r3
            r3 = r1
            r1 = r26
            goto L_0x0459
        L_0x0329:
            r19 = r7
            r0 = 3
            if (r2 != r0) goto L_0x03ec
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0565 }
            r0.<init>()     // Catch:{ Exception -> 0x0565 }
            r7 = r19
            if (r19 == 0) goto L_0x036a
            java.lang.Object r1 = r7.get(r3)     // Catch:{ Exception -> 0x0565 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0565 }
            if (r1 == 0) goto L_0x036a
            org.telegram.tgnet.SerializedData r3 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0565 }
            byte[] r1 = org.telegram.messenger.Utilities.hexToBytes(r1)     // Catch:{ Exception -> 0x0565 }
            r3.<init>((byte[]) r1)     // Catch:{ Exception -> 0x0565 }
            r1 = 0
            int r5 = r3.readInt32(r1)     // Catch:{ Exception -> 0x0565 }
            r6 = 0
        L_0x034e:
            if (r6 >= r5) goto L_0x0361
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r13 = r0.stickers     // Catch:{ Exception -> 0x0565 }
            int r14 = r3.readInt32(r1)     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$InputDocument r14 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r3, r14, r1)     // Catch:{ Exception -> 0x0565 }
            r13.add(r14)     // Catch:{ Exception -> 0x0565 }
            int r6 = r6 + 1
            r1 = 0
            goto L_0x034e
        L_0x0361:
            int r1 = r0.flags     // Catch:{ Exception -> 0x0565 }
            r5 = 1
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0565 }
            r3.cleanup()     // Catch:{ Exception -> 0x0565 }
        L_0x036a:
            r1 = r29
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x0565 }
            r0.mime_type = r3     // Catch:{ Exception -> 0x0565 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0565 }
            r0.attributes = r3     // Catch:{ Exception -> 0x0565 }
            boolean r3 = r28.isGif()     // Catch:{ Exception -> 0x0565 }
            if (r3 != 0) goto L_0x0392
            if (r31 == 0) goto L_0x0383
            r5 = r31
            boolean r3 = r5.muted     // Catch:{ Exception -> 0x0565 }
            if (r3 != 0) goto L_0x0394
            goto L_0x0385
        L_0x0383:
            r5 = r31
        L_0x0385:
            r3 = 1
            r0.nosound_video = r3     // Catch:{ Exception -> 0x0565 }
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0565 }
            if (r3 == 0) goto L_0x0394
            java.lang.String r3 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x0565 }
            goto L_0x0394
        L_0x0392:
            r5 = r31
        L_0x0394:
            long r13 = r1.access_hash     // Catch:{ Exception -> 0x0565 }
            r19 = 0
            int r3 = (r13 > r19 ? 1 : (r13 == r19 ? 0 : -1))
            if (r3 != 0) goto L_0x039f
            r6 = r0
            r3 = 1
            goto L_0x03c0
        L_0x039f:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0565 }
            r3.<init>()     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0565 }
            r6.<init>()     // Catch:{ Exception -> 0x0565 }
            r3.id = r6     // Catch:{ Exception -> 0x0565 }
            long r13 = r1.id     // Catch:{ Exception -> 0x0565 }
            r6.id = r13     // Catch:{ Exception -> 0x0565 }
            long r13 = r1.access_hash     // Catch:{ Exception -> 0x0565 }
            r6.access_hash = r13     // Catch:{ Exception -> 0x0565 }
            byte[] r13 = r1.file_reference     // Catch:{ Exception -> 0x0565 }
            r6.file_reference = r13     // Catch:{ Exception -> 0x0565 }
            if (r13 != 0) goto L_0x03be
            r13 = 0
            byte[] r14 = new byte[r13]     // Catch:{ Exception -> 0x0565 }
            r6.file_reference = r14     // Catch:{ Exception -> 0x0565 }
        L_0x03be:
            r6 = r3
            r3 = 0
        L_0x03c0:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r13 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0565 }
            r13.<init>(r8)     // Catch:{ Exception -> 0x0565 }
            r8 = 1
            r13.type = r8     // Catch:{ Exception -> 0x0565 }
            r13.obj = r12     // Catch:{ Exception -> 0x0565 }
            r13.originalPath = r4     // Catch:{ Exception -> 0x0565 }
            r13.parentObject = r10     // Catch:{ Exception -> 0x0565 }
            r13.inputUploadMedia = r0     // Catch:{ Exception -> 0x0565 }
            r13.performMediaUpload = r3     // Catch:{ Exception -> 0x0565 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0565 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0565 }
            if (r0 != 0) goto L_0x03e7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0565 }
            r8 = 0
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0     // Catch:{ Exception -> 0x0565 }
            r13.photoSize = r0     // Catch:{ Exception -> 0x0565 }
            r13.locationParent = r1     // Catch:{ Exception -> 0x0565 }
        L_0x03e7:
            r13.videoEditedInfo = r5     // Catch:{ Exception -> 0x0565 }
            r1 = r6
            goto L_0x0459
        L_0x03ec:
            r1 = r29
            r7 = r19
            r0 = 7
            if (r2 != r0) goto L_0x0456
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0565 }
            r0.<init>()     // Catch:{ Exception -> 0x0565 }
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x0565 }
            r0.mime_type = r3     // Catch:{ Exception -> 0x0565 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0565 }
            r0.attributes = r3     // Catch:{ Exception -> 0x0565 }
            long r5 = r1.access_hash     // Catch:{ Exception -> 0x0565 }
            r13 = 0
            int r3 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r3 != 0) goto L_0x040b
            r3 = r0
            r5 = 1
            goto L_0x042b
        L_0x040b:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0565 }
            r3.<init>()     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0565 }
            r5.<init>()     // Catch:{ Exception -> 0x0565 }
            r3.id = r5     // Catch:{ Exception -> 0x0565 }
            long r13 = r1.id     // Catch:{ Exception -> 0x0565 }
            r5.id = r13     // Catch:{ Exception -> 0x0565 }
            long r13 = r1.access_hash     // Catch:{ Exception -> 0x0565 }
            r5.access_hash = r13     // Catch:{ Exception -> 0x0565 }
            byte[] r6 = r1.file_reference     // Catch:{ Exception -> 0x0565 }
            r5.file_reference = r6     // Catch:{ Exception -> 0x0565 }
            if (r6 != 0) goto L_0x042a
            r6 = 0
            byte[] r13 = new byte[r6]     // Catch:{ Exception -> 0x0565 }
            r5.file_reference = r13     // Catch:{ Exception -> 0x0565 }
        L_0x042a:
            r5 = 0
        L_0x042b:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0565 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x0565 }
            r6.originalPath = r4     // Catch:{ Exception -> 0x0565 }
            r8 = 2
            r6.type = r8     // Catch:{ Exception -> 0x0565 }
            r6.obj = r12     // Catch:{ Exception -> 0x0565 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r1.thumbs     // Catch:{ Exception -> 0x0565 }
            boolean r8 = r8.isEmpty()     // Catch:{ Exception -> 0x0565 }
            if (r8 != 0) goto L_0x044c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r1.thumbs     // Catch:{ Exception -> 0x0565 }
            r9 = 0
            java.lang.Object r8 = r8.get(r9)     // Catch:{ Exception -> 0x0565 }
            org.telegram.tgnet.TLRPC$PhotoSize r8 = (org.telegram.tgnet.TLRPC$PhotoSize) r8     // Catch:{ Exception -> 0x0565 }
            r6.photoSize = r8     // Catch:{ Exception -> 0x0565 }
            r6.locationParent = r1     // Catch:{ Exception -> 0x0565 }
        L_0x044c:
            r6.parentObject = r10     // Catch:{ Exception -> 0x0565 }
            r6.inputUploadMedia = r0     // Catch:{ Exception -> 0x0565 }
            r6.performMediaUpload = r5     // Catch:{ Exception -> 0x0565 }
            r1 = r3
            r3 = r5
            r13 = r6
            goto L_0x0459
        L_0x0456:
            r1 = 0
            r3 = 0
            r13 = 0
        L_0x0459:
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r0 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage     // Catch:{ Exception -> 0x0565 }
            r0.<init>()     // Catch:{ Exception -> 0x0565 }
            int r5 = r28.getId()     // Catch:{ Exception -> 0x0565 }
            r0.id = r5     // Catch:{ Exception -> 0x0565 }
            org.telegram.messenger.MessagesController r5 = r27.getMessagesController()     // Catch:{ Exception -> 0x0565 }
            r6 = r18
            org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer((int) r6)     // Catch:{ Exception -> 0x0565 }
            r0.peer = r5     // Catch:{ Exception -> 0x0565 }
            if (r1 == 0) goto L_0x047a
            int r5 = r0.flags     // Catch:{ Exception -> 0x0565 }
            r5 = r5 | 16384(0x4000, float:2.2959E-41)
            r0.flags = r5     // Catch:{ Exception -> 0x0565 }
            r0.media = r1     // Catch:{ Exception -> 0x0565 }
        L_0x047a:
            boolean r1 = r12.scheduled     // Catch:{ Exception -> 0x0565 }
            if (r1 == 0) goto L_0x048c
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner     // Catch:{ Exception -> 0x0565 }
            int r1 = r1.date     // Catch:{ Exception -> 0x0565 }
            r0.schedule_date = r1     // Catch:{ Exception -> 0x0565 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0565 }
            r5 = 32768(0x8000, float:4.5918E-41)
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0565 }
        L_0x048c:
            java.lang.CharSequence r1 = r12.editingMessage     // Catch:{ Exception -> 0x0565 }
            if (r1 == 0) goto L_0x04da
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0565 }
            r0.message = r1     // Catch:{ Exception -> 0x0565 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0565 }
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r0.flags = r1     // Catch:{ Exception -> 0x0565 }
            boolean r5 = r12.editingMessageSearchWebPage     // Catch:{ Exception -> 0x0565 }
            if (r5 != 0) goto L_0x04a2
            r5 = 1
            goto L_0x04a3
        L_0x04a2:
            r5 = 0
        L_0x04a3:
            r0.no_webpage = r5     // Catch:{ Exception -> 0x0565 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r12.editingMessageEntities     // Catch:{ Exception -> 0x0565 }
            if (r5 == 0) goto L_0x04b2
            r0.entities = r5     // Catch:{ Exception -> 0x0565 }
            r5 = 8
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0565 }
        L_0x04b0:
            r1 = 0
            goto L_0x04d6
        L_0x04b2:
            r1 = 1
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r1]     // Catch:{ Exception -> 0x0565 }
            java.lang.CharSequence r1 = r12.editingMessage     // Catch:{ Exception -> 0x0565 }
            r6 = 0
            r5[r6] = r1     // Catch:{ Exception -> 0x0565 }
            org.telegram.messenger.MediaDataController r1 = r27.getMediaDataController()     // Catch:{ Exception -> 0x0565 }
            r6 = r16
            java.util.ArrayList r1 = r1.getEntities(r5, r6)     // Catch:{ Exception -> 0x0565 }
            if (r1 == 0) goto L_0x04b0
            boolean r5 = r1.isEmpty()     // Catch:{ Exception -> 0x0565 }
            if (r5 != 0) goto L_0x04b0
            r0.entities = r1     // Catch:{ Exception -> 0x0565 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0565 }
            r5 = 8
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0565 }
            goto L_0x04b0
        L_0x04d6:
            r12.editingMessage = r1     // Catch:{ Exception -> 0x0565 }
            r12.editingMessageEntities = r1     // Catch:{ Exception -> 0x0565 }
        L_0x04da:
            if (r13 == 0) goto L_0x04de
            r13.sendRequest = r0     // Catch:{ Exception -> 0x0565 }
        L_0x04de:
            r1 = 1
            if (r2 != r1) goto L_0x04f0
            r4 = 0
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0565 }
            r1 = r27
            r2 = r0
            r3 = r28
            r5 = r13
            r6 = r10
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x04f0:
            r1 = 2
            if (r2 != r1) goto L_0x050c
            if (r3 == 0) goto L_0x04fa
            r11.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x04fa:
            r5 = 0
            r6 = 1
            boolean r14 = r12.scheduled     // Catch:{ Exception -> 0x0565 }
            r1 = r27
            r2 = r0
            r3 = r28
            r9 = r7
            r7 = r13
            r8 = r10
            r10 = r14
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x050c:
            r9 = r7
            r1 = 3
            if (r2 != r1) goto L_0x0525
            if (r3 == 0) goto L_0x0517
            r11.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x0517:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0565 }
            r1 = r27
            r2 = r0
            r3 = r28
            r5 = r13
            r6 = r10
            r7 = r9
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x0525:
            r1 = 6
            if (r2 != r1) goto L_0x0536
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0565 }
            r1 = r27
            r2 = r0
            r3 = r28
            r5 = r13
            r6 = r10
            r7 = r9
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x0536:
            r1 = 7
            if (r2 != r1) goto L_0x054d
            if (r3 == 0) goto L_0x053f
            r11.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x053f:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0565 }
            r1 = r27
            r2 = r0
            r3 = r28
            r5 = r13
            r6 = r10
            r7 = r9
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x054d:
            r1 = 8
            if (r2 != r1) goto L_0x056c
            if (r3 == 0) goto L_0x0557
            r11.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x0557:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0565 }
            r1 = r27
            r2 = r0
            r3 = r28
            r5 = r13
            r6 = r10
            r7 = r9
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0565 }
            goto L_0x056c
        L_0x0565:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r27.revertEditingMessageObject(r28)
        L_0x056c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessage(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, BaseFragment baseFragment, ArrayList<TLRPC$MessageEntity> arrayList, int i) {
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
        return getConnectionsManager().sendRequest(tLRPC$TL_messages_editMessage, new RequestDelegate(baseFragment, tLRPC$TL_messages_editMessage) {
            public final /* synthetic */ BaseFragment f$1;
            public final /* synthetic */ TLRPC$TL_messages_editMessage f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SendMessagesHelper.this.lambda$editMessage$16$SendMessagesHelper(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$editMessage$16 */
    public /* synthetic */ void lambda$editMessage$16$SendMessagesHelper(BaseFragment baseFragment, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$15 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendNotificationCallback$19 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$17 */
    public /* synthetic */ void lambda$null$17$SendMessagesHelper(String str) {
        Boolean remove = this.waitingForCallback.remove(str);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$18 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendVote$21 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$20 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendReaction$22 */
    public /* synthetic */ void lambda$sendReaction$22$SendMessagesHelper(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public void sendCallback(boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity) {
        lambda$null$23(z, messageObject, tLRPC$KeyboardButton, (TLRPC$InputCheckPasswordSRP) null, (TwoStepVerificationActivity) null, chatActivity);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0089  */
    /* renamed from: sendCallback */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$null$23(boolean r19, org.telegram.messenger.MessageObject r20, org.telegram.tgnet.TLRPC$KeyboardButton r21, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP r22, org.telegram.ui.TwoStepVerificationActivity r23, org.telegram.ui.ChatActivity r24) {
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
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$s_WFKVY6ceq9r2nJFzqFNOHKIyw r6 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$s_WFKVY6ceq9r2nJFzqFNOHKIyw
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$23(boolean, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP, org.telegram.ui.TwoStepVerificationActivity, org.telegram.ui.ChatActivity):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendCallback$29 */
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0170, code lost:
        if (org.telegram.messenger.MessagesController.getNotificationsSettings(r7.currentAccount).getBoolean("askgame_" + r0, true) != false) goto L_0x0174;
     */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x010c  */
    /* renamed from: lambda$null$28 */
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
            r8 = 2131626306(0x7f0e0942, float:1.8879844E38)
            java.lang.String r9 = "OK"
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
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r8)
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
            r12 = 2131624589(0x7f0e028d, float:1.8876362E38)
            java.lang.String r13 = "Cancel"
            if (r0 == 0) goto L_0x01f9
            if (r37 != 0) goto L_0x04a5
            org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r33.getParentActivity()
            r8.<init>((android.content.Context) r0)
            r0 = 2131624537(0x7f0e0259, float:1.8876257E38)
            java.lang.String r1 = "BotOwnershipTransfer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r8.setTitle(r0)
            r0 = 2131624540(0x7f0e025c, float:1.8876263E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = "BotOwnershipTransferReadyAlertText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setMessage(r0)
            r0 = 2131624539(0x7f0e025b, float:1.887626E38)
            java.lang.String r1 = "BotOwnershipTransferChangeOwner"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$jdFeanfPWygqcof8ljFLqby3YU4 r11 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$jdFeanfPWygqcof8ljFLqby3YU4
            r0 = r11
            r1 = r27
            r2 = r38
            r3 = r31
            r4 = r32
            r5 = r33
            r0.<init>(r2, r3, r4, r5)
            r8.setPositiveButton(r9, r11)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r12)
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
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$VAiZotCpr7XS-iStBfzZ9I7e_Uc r10 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$VAiZotCpr7XS-iStBfzZ9I7e_Uc
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
            r4 = 2131625157(0x7f0e04c5, float:1.8877514E38)
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
            r10 = 2131624538(0x7f0e025a, float:1.8876259E38)
            java.lang.Object[] r12 = new java.lang.Object[r3]
            java.lang.String r8 = "BotOwnershipTransferAlertText"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r8, r10, r12)
            android.text.SpannableStringBuilder r8 = org.telegram.messenger.AndroidUtilities.replaceTags(r8)
            r5.setText(r8)
            r8 = -1
            r10 = -2
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r10)
            r4.addView(r5, r12)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            android.app.Activity r12 = r33.getParentActivity()
            r5.<init>(r12)
            r5.setOrientation(r3)
            r18 = -1
            r19 = -2
            r20 = 0
            r21 = 1093664768(0x41300000, float:11.0)
            r22 = 0
            r23 = 0
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r18, r19, r20, r21, r22, r23)
            r4.addView(r5, r12)
            android.widget.ImageView r12 = new android.widget.ImageView
            android.app.Activity r8 = r33.getParentActivity()
            r12.<init>(r8)
            r8 = 2131165579(0x7var_b, float:1.794538E38)
            r12.setImageResource(r8)
            boolean r16 = org.telegram.messenger.LocaleController.isRTL
            r18 = 1093664768(0x41300000, float:11.0)
            if (r16 == 0) goto L_0x0309
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8 = r16
            goto L_0x030a
        L_0x0309:
            r8 = 0
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
            r12.setPadding(r8, r10, r11, r3)
            android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r8.<init>(r10, r11)
            r12.setColorFilter(r8)
            android.widget.TextView r8 = new android.widget.TextView
            android.app.Activity r10 = r33.getParentActivity()
            r8.<init>(r10)
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r8.setTextColor(r10)
            r10 = 1
            r8.setTextSize(r10, r15)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0347
            r10 = 5
            goto L_0x0348
        L_0x0347:
            r10 = 3
        L_0x0348:
            r10 = r10 | 48
            r8.setGravity(r10)
            r10 = 2131625154(0x7f0e04c2, float:1.8877508E38)
            java.lang.String r11 = "EditAdminTransferAlertText1"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.replaceTags(r10)
            r8.setText(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0373
            r10 = -1
            r11 = -2
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r8, r15)
            r8 = 5
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r8)
            r5.addView(r12, r15)
            goto L_0x0383
        L_0x0373:
            r10 = -1
            r11 = -2
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
            r5.addView(r12, r15)
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r8, r12)
        L_0x0383:
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            android.app.Activity r8 = r33.getParentActivity()
            r5.<init>(r8)
            r5.setOrientation(r3)
            r21 = -1
            r22 = -2
            r23 = 0
            r24 = 1093664768(0x41300000, float:11.0)
            r25 = 0
            r26 = 0
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r21, r22, r23, r24, r25, r26)
            r4.addView(r5, r8)
            android.widget.ImageView r8 = new android.widget.ImageView
            android.app.Activity r10 = r33.getParentActivity()
            r8.<init>(r10)
            r10 = 2131165579(0x7var_b, float:1.794538E38)
            r8.setImageResource(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x03ba
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            goto L_0x03bb
        L_0x03ba:
            r10 = 0
        L_0x03bb:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x03c5
            r12 = 0
            goto L_0x03c9
        L_0x03c5:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r18)
        L_0x03c9:
            r8.setPadding(r10, r11, r12, r3)
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r10, r11)
            r8.setColorFilter(r3)
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
            r10 = 2131625155(0x7f0e04c3, float:1.887751E38)
            java.lang.String r11 = "EditAdminTransferAlertText2"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.replaceTags(r10)
            r3.setText(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0422
            r10 = -1
            r11 = -2
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r3, r10)
            r12 = 5
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r12)
            r5.addView(r8, r3)
            goto L_0x0433
        L_0x0422:
            r10 = -1
            r11 = -2
            r12 = 5
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
            r5.addView(r8, r15)
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r3, r8)
        L_0x0433:
            java.lang.String r2 = r2.text
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0458
            r1 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r2 = "EditAdminTransferSetPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$uwBjzTJVNNuAuxAPSJ95QSOHWQY r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$uwBjzTJVNNuAuxAPSJ95QSOHWQY
            r2.<init>()
            r0.setPositiveButton(r1, r2)
            r1 = 2131624589(0x7f0e028d, float:1.8876362E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r13, r1)
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
            r2 = 2131625156(0x7f0e04c4, float:1.8877512E38)
            java.lang.String r3 = "EditAdminTransferAlertText3"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            r10 = -1
            r11 = -2
            r12 = 0
            r13 = 1093664768(0x41300000, float:11.0)
            r14 = 0
            r15 = 0
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11, r12, r13, r14, r15)
            r4.addView(r1, r2)
            r1 = 2131626306(0x7f0e0942, float:1.8879844E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$24 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$27 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$26 */
    public /* synthetic */ void lambda$null$26$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            twoStepVerificationActivity.setCurrentPasswordInfo((byte[]) null, tLRPC$TL_account_password);
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            lambda$null$23(z, messageObject, tLRPC$KeyboardButton, twoStepVerificationActivity.getNewSrpPassword(), twoStepVerificationActivity, chatActivity);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendGame$30 */
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v1, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v1, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v9, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v52, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v56, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v4, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v5, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v18, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v22, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v5, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v8, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v93, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v26, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v27, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v26, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v74, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v11, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v12, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v13, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v38, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v14, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v15, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v16, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v77, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v98, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v99, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v101, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v17, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v18, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v19, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v93, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v20, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v94, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v21, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v22, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v142, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v97, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v144, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v145, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v147, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v28, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v98, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v29, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v30, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v31, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v32, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v33, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v34, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v35, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v104, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v67, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v106, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v108, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v36, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v37, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v109, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v38, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v39, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v110, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v113, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v114, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v115, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v41, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v42, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v43, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v44, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v121, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v122, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v123, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v45, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v100, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v46, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v47, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v156, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r29v3, resolved type: org.telegram.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v79, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v232, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v259, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v263, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v28, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r58v30, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v270, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v144, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v20, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v271, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v50, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v272, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v147, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v273, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v148, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v274, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v276, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v151, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v152, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v153, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v277, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v278, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v156, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v158, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v176, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v177, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v178, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v295, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v179, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v312, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v313, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v314, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v315, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v316, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v317, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v318, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v194, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v319, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v321, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v167, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v168, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v169, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v170, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v324, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v328, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v200, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v201, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v202, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v203, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v177, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v204, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v145, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v330, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v38, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v75, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v76, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v77, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v331, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v332, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v147, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v333, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v334, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v148, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v335, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v337, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v344, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v345, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v149, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v346, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v353, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v228, resolved type: org.telegram.tgnet.TLRPC$TL_userRequest_old2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v150, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v41, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v92, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v93, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v241, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v160, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v162, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v175, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v176, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v179, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v180, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v181, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v182, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v243, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v244, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v368, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v245, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v117, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v248, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v249, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v250, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v251, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v252, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v253, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v254, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v255, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v257, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v258, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v259, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v260, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaVenue} */
    /* JADX WARNING: type inference failed for: r4v83, types: [org.telegram.tgnet.TLRPC$Document] */
    /* JADX WARNING: type inference failed for: r11v14 */
    /* JADX WARNING: type inference failed for: r8v63, types: [org.telegram.tgnet.TLRPC$Document, org.telegram.tgnet.TLObject] */
    /* JADX WARNING: type inference failed for: r9v29 */
    /* JADX WARNING: type inference failed for: r2v65, types: [org.telegram.tgnet.TLRPC$TL_inputDocument, org.telegram.tgnet.TLRPC$InputDocument] */
    /* JADX WARNING: type inference failed for: r7v44 */
    /* JADX WARNING: type inference failed for: r7v45 */
    /* JADX WARNING: type inference failed for: r9v39 */
    /* JADX WARNING: type inference failed for: r8v81, types: [org.telegram.tgnet.TLRPC$Document, org.telegram.tgnet.TLObject] */
    /* JADX WARNING: type inference failed for: r27v3 */
    /* JADX WARNING: type inference failed for: r3v248, types: [org.telegram.tgnet.TLRPC$TL_document] */
    /* JADX WARNING: type inference failed for: r3v251, types: [org.telegram.tgnet.TLRPC$TL_document] */
    /* JADX WARNING: type inference failed for: r3v257, types: [org.telegram.tgnet.TLRPC$TL_document] */
    /* JADX WARNING: type inference failed for: r7v107 */
    /* JADX WARNING: type inference failed for: r9v83 */
    /* JADX WARNING: type inference failed for: r9v84 */
    /* JADX WARNING: type inference failed for: r9v85 */
    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0de3: MOVE  (r5v128 org.telegram.tgnet.TLRPC$TL_photo) = (r44v0 org.telegram.tgnet.TLRPC$TL_photo)
        	at jadx.core.dex.instructions.args.InsnArg.wrapArg(InsnArg.java:164)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.assignInline(CodeShrinkVisitor.java:133)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.checkInline(CodeShrinkVisitor.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:65)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.regions.TernaryMod.makeTernaryInsn(TernaryMod.java:122)
        	at jadx.core.dex.visitors.regions.TernaryMod.visitRegion(TernaryMod.java:34)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:73)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterativeStepInternal(DepthRegionTraversal.java:78)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseIterative(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.IfRegionVisitor.visit(IfRegionVisitor.java:31)
        */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1013:0x125a A[Catch:{ Exception -> 0x149c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1014:0x125d A[Catch:{ Exception -> 0x149c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1021:0x127f A[Catch:{ Exception -> 0x1293 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1030:0x129b A[SYNTHETIC, Splitter:B:1030:0x129b] */
    /* JADX WARNING: Removed duplicated region for block: B:1041:0x12c4 A[Catch:{ Exception -> 0x149c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1064:0x1351 A[Catch:{ Exception -> 0x11fa }] */
    /* JADX WARNING: Removed duplicated region for block: B:1080:0x13a1 A[Catch:{ Exception -> 0x11fa }] */
    /* JADX WARNING: Removed duplicated region for block: B:1081:0x13a6 A[Catch:{ Exception -> 0x11fa }] */
    /* JADX WARNING: Removed duplicated region for block: B:1139:0x14a3  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0291 A[Catch:{ Exception -> 0x02eb }] */
    /* JADX WARNING: Removed duplicated region for block: B:1215:0x1608 A[Catch:{ Exception -> 0x14de }] */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0296 A[Catch:{ Exception -> 0x02eb }] */
    /* JADX WARNING: Removed duplicated region for block: B:1221:0x1634 A[Catch:{ Exception -> 0x14de }] */
    /* JADX WARNING: Removed duplicated region for block: B:1226:0x1673 A[Catch:{ Exception -> 0x14de }] */
    /* JADX WARNING: Removed duplicated region for block: B:1235:0x16a3  */
    /* JADX WARNING: Removed duplicated region for block: B:1236:0x16a5  */
    /* JADX WARNING: Removed duplicated region for block: B:1239:0x16aa A[Catch:{ Exception -> 0x1a46 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1242:0x16ba A[Catch:{ Exception -> 0x1a46 }] */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x029e  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x02c3  */
    /* JADX WARNING: Removed duplicated region for block: B:1354:0x19c4  */
    /* JADX WARNING: Removed duplicated region for block: B:1355:0x19c6  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x02fd  */
    /* JADX WARNING: Removed duplicated region for block: B:1373:0x19fb A[SYNTHETIC, Splitter:B:1373:0x19fb] */
    /* JADX WARNING: Removed duplicated region for block: B:1377:0x1a02 A[SYNTHETIC, Splitter:B:1377:0x1a02] */
    /* JADX WARNING: Removed duplicated region for block: B:1397:0x1a4e  */
    /* JADX WARNING: Removed duplicated region for block: B:1399:0x1a52 A[SYNTHETIC, Splitter:B:1399:0x1a52] */
    /* JADX WARNING: Removed duplicated region for block: B:1413:0x1a83 A[Catch:{ Exception -> 0x1ba3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1441:0x1af9 A[Catch:{ Exception -> 0x1ba3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1488:0x1bce  */
    /* JADX WARNING: Removed duplicated region for block: B:1489:0x1bd0  */
    /* JADX WARNING: Removed duplicated region for block: B:1492:0x1bd6  */
    /* JADX WARNING: Removed duplicated region for block: B:1506:0x1626 A[EDGE_INSN: B:1506:0x1626->B:1219:0x1626 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:1523:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x061d A[Catch:{ Exception -> 0x05d0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x0641 A[Catch:{ Exception -> 0x05d0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:329:0x064c A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0650 A[Catch:{ Exception -> 0x0748 }] */
    /* JADX WARNING: Removed duplicated region for block: B:382:0x076a A[SYNTHETIC, Splitter:B:382:0x076a] */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0786 A[Catch:{ Exception -> 0x077b }] */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0789 A[SYNTHETIC, Splitter:B:392:0x0789] */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0793 A[SYNTHETIC, Splitter:B:400:0x0793] */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x07a6 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x07c2 A[Catch:{ Exception -> 0x077b }] */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x07cd A[SYNTHETIC, Splitter:B:411:0x07cd] */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x0805  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0831  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x083e A[SYNTHETIC, Splitter:B:430:0x083e] */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x084a A[SYNTHETIC, Splitter:B:434:0x084a] */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x087d  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0884  */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x0888  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x088a A[SYNTHETIC, Splitter:B:454:0x088a] */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0898 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x08ce  */
    /* JADX WARNING: Removed duplicated region for block: B:477:0x08dd A[SYNTHETIC, Splitter:B:477:0x08dd] */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x092f A[Catch:{ Exception -> 0x0928 }] */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0939 A[Catch:{ Exception -> 0x0928 }] */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x0966 A[Catch:{ Exception -> 0x0928 }] */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x0992 A[SYNTHETIC, Splitter:B:509:0x0992] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0110 A[SYNTHETIC, Splitter:B:51:0x0110] */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x0a4b A[SYNTHETIC, Splitter:B:565:0x0a4b] */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x0a54  */
    /* JADX WARNING: Removed duplicated region for block: B:582:0x0a81  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0abb A[Catch:{ Exception -> 0x1ba5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x0abd A[Catch:{ Exception -> 0x1ba5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x0ac6 A[SYNTHETIC, Splitter:B:596:0x0ac6] */
    /* JADX WARNING: Removed duplicated region for block: B:615:0x0af3 A[SYNTHETIC, Splitter:B:615:0x0af3] */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0b44  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0129 A[Catch:{ Exception -> 0x02eb }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x015a A[Catch:{ Exception -> 0x02eb }] */
    /* JADX WARNING: Removed duplicated region for block: B:652:0x0b94  */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0CLASSNAME A[Catch:{ Exception -> 0x0adc }] */
    /* JADX WARNING: Removed duplicated region for block: B:659:0x0CLASSNAME A[Catch:{ Exception -> 0x0adc }] */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0d6a A[Catch:{ Exception -> 0x0adc }] */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x0e2d A[Catch:{ Exception -> 0x0adc }] */
    /* JADX WARNING: Removed duplicated region for block: B:783:0x0e3d  */
    /* JADX WARNING: Removed duplicated region for block: B:797:0x0e94 A[Catch:{ Exception -> 0x0var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x0e97 A[Catch:{ Exception -> 0x0var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:807:0x0ed0 A[Catch:{ Exception -> 0x0var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:812:0x0eea A[Catch:{ Exception -> 0x0var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x0efa A[Catch:{ Exception -> 0x0var_ }] */
    /* JADX WARNING: Removed duplicated region for block: B:932:0x111a A[SYNTHETIC, Splitter:B:932:0x111a] */
    /* JADX WARNING: Removed duplicated region for block: B:938:0x1129 A[SYNTHETIC, Splitter:B:938:0x1129] */
    /* JADX WARNING: Removed duplicated region for block: B:955:0x116d  */
    /* JADX WARNING: Removed duplicated region for block: B:976:0x11b5  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:1310:0x18c2=Splitter:B:1310:0x18c2, B:1356:0x19c7=Splitter:B:1356:0x19c7} */
    private void sendMessage(java.lang.String r56, java.lang.String r57, org.telegram.tgnet.TLRPC$MessageMedia r58, org.telegram.tgnet.TLRPC$TL_photo r59, org.telegram.messenger.VideoEditedInfo r60, org.telegram.tgnet.TLRPC$User r61, org.telegram.tgnet.TLRPC$TL_document r62, org.telegram.tgnet.TLRPC$TL_game r63, org.telegram.tgnet.TLRPC$TL_messageMediaPoll r64, long r65, java.lang.String r67, org.telegram.messenger.MessageObject r68, org.telegram.messenger.MessageObject r69, org.telegram.tgnet.TLRPC$WebPage r70, boolean r71, org.telegram.messenger.MessageObject r72, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r73, org.telegram.tgnet.TLRPC$ReplyMarkup r74, java.util.HashMap<java.lang.String, java.lang.String> r75, boolean r76, int r77, int r78, java.lang.Object r79) {
        /*
            r55 = this;
            r1 = r55
            r2 = r56
            r3 = r58
            r4 = r59
            r5 = r61
            r6 = r62
            r7 = r63
            r8 = r64
            r9 = r65
            r11 = r67
            r12 = r68
            r13 = r69
            r14 = r70
            r15 = r72
            r13 = r73
            r12 = r75
            r6 = r77
            r7 = r78
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
            if (r57 != 0) goto L_0x003b
            r18 = r11
            goto L_0x003d
        L_0x003b:
            r18 = r57
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
            org.telegram.messenger.MessagesController r3 = r55.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((int) r5)
            goto L_0x0067
        L_0x0066:
            r3 = 0
        L_0x0067:
            org.telegram.messenger.UserConfig r21 = r55.getUserConfig()
            int r9 = r21.getClientUserId()
            if (r5 != 0) goto L_0x00b5
            org.telegram.messenger.MessagesController r10 = r55.getMessagesController()
            r23 = r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r10.getEncryptedChat(r9)
            if (r9 != 0) goto L_0x00b2
            if (r15 == 0) goto L_0x00b1
            org.telegram.messenger.MessagesStorage r2 = r55.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner
            boolean r4 = r15.scheduled
            r2.markMessageAsSendError(r3, r4)
            org.telegram.tgnet.TLRPC$Message r2 = r15.messageOwner
            r3 = 2
            r2.send_state = r3
            org.telegram.messenger.NotificationCenter r2 = r55.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.messageSendError
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r5 = r72.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 0
            r4[r6] = r5
            r2.postNotificationName(r3, r4)
            int r2 = r72.getId()
            r1.processSentMessage(r2)
        L_0x00b1:
            return
        L_0x00b2:
            r25 = r4
            goto L_0x00ff
        L_0x00b5:
            r23 = r9
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r9 == 0) goto L_0x00fc
            org.telegram.messenger.MessagesController r9 = r55.getMessagesController()
            int r10 = r3.channel_id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r10)
            if (r9 == 0) goto L_0x00d1
            boolean r10 = r9.megagroup
            if (r10 != 0) goto L_0x00d1
            r10 = 1
            goto L_0x00d2
        L_0x00d1:
            r10 = 0
        L_0x00d2:
            if (r10 == 0) goto L_0x00ee
            r24 = r10
            boolean r10 = r9.has_link
            if (r10 == 0) goto L_0x00eb
            org.telegram.messenger.MessagesController r10 = r55.getMessagesController()
            r25 = r4
            int r4 = r9.id
            org.telegram.tgnet.TLRPC$ChatFull r4 = r10.getChatFull(r4)
            if (r4 == 0) goto L_0x00f2
            int r4 = r4.linked_chat_id
            goto L_0x00f3
        L_0x00eb:
            r25 = r4
            goto L_0x00f2
        L_0x00ee:
            r25 = r4
            r24 = r10
        L_0x00f2:
            r4 = 0
        L_0x00f3:
            boolean r9 = org.telegram.messenger.ChatObject.shouldSendAnonymously(r9)
            r26 = r4
            r10 = r9
            r9 = 0
            goto L_0x0104
        L_0x00fc:
            r25 = r4
            r9 = 0
        L_0x00ff:
            r10 = 0
            r24 = 0
            r26 = 0
        L_0x0104:
            java.lang.String r4 = "http"
            r27 = r10
            java.lang.String r10 = "query_id"
            r29 = r3
            java.lang.String r3 = "parentObject"
            if (r15 == 0) goto L_0x02fd
            org.telegram.tgnet.TLRPC$Message r1 = r15.messageOwner     // Catch:{ Exception -> 0x02f4 }
            if (r79 != 0) goto L_0x0121
            if (r12 == 0) goto L_0x0121
            boolean r27 = r12.containsKey(r3)     // Catch:{ Exception -> 0x02eb }
            if (r27 == 0) goto L_0x0121
            java.lang.Object r27 = r12.get(r3)     // Catch:{ Exception -> 0x02eb }
            goto L_0x0123
        L_0x0121:
            r27 = r79
        L_0x0123:
            boolean r31 = r72.isForwarded()     // Catch:{ Exception -> 0x02eb }
            if (r31 == 0) goto L_0x015a
            r19 = r62
            r56 = r2
            r31 = r3
            r34 = r4
            r2 = r5
            r13 = r7
            r36 = r8
            r30 = r10
            r37 = r14
            r38 = r18
            r5 = r23
            r63 = r27
            r3 = r29
            r10 = 4
            r29 = 0
            r32 = 0
            r27 = r58
            r4 = r59
            r18 = r61
            r14 = r65
            r8 = r76
            r7 = r6
            r6 = r55
        L_0x0153:
            r54 = r12
            r12 = r1
            r1 = r54
            goto L_0x0803
        L_0x015a:
            boolean r31 = r72.isDice()     // Catch:{ Exception -> 0x02eb }
            if (r31 == 0) goto L_0x017a
            java.lang.String r2 = r72.getDiceEmoji()     // Catch:{ Exception -> 0x02eb }
            r31 = r3
            r33 = r4
            r32 = r5
            r18 = r8
            r19 = r11
            r34 = 11
            r3 = r58
            r4 = r59
            r5 = r61
        L_0x0176:
            r8 = r62
            goto L_0x0289
        L_0x017a:
            r31 = r3
            int r3 = r15.type     // Catch:{ Exception -> 0x02eb }
            if (r3 == 0) goto L_0x026c
            boolean r3 = r72.isAnimatedEmoji()     // Catch:{ Exception -> 0x02eb }
            if (r3 == 0) goto L_0x0188
            goto L_0x026c
        L_0x0188:
            int r3 = r15.type     // Catch:{ Exception -> 0x02eb }
            r32 = r5
            r5 = 4
            if (r3 != r5) goto L_0x019d
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02eb }
            r5 = r61
            r33 = r4
            r19 = r18
            r34 = 1
            r4 = r59
            goto L_0x0285
        L_0x019d:
            r5 = 1
            if (r3 != r5) goto L_0x01b4
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3     // Catch:{ Exception -> 0x02eb }
            r5 = r61
            r33 = r4
            r19 = r18
            r34 = 2
            r4 = r3
            r18 = r8
            r3 = r58
            goto L_0x0176
        L_0x01b4:
            r5 = 3
            if (r3 == r5) goto L_0x0256
            r5 = 5
            if (r3 == r5) goto L_0x0256
            org.telegram.messenger.VideoEditedInfo r5 = r15.videoEditedInfo     // Catch:{ Exception -> 0x02eb }
            if (r5 == 0) goto L_0x01c0
            goto L_0x0256
        L_0x01c0:
            r5 = 12
            if (r3 != r5) goto L_0x01fe
            org.telegram.tgnet.TLRPC$TL_userRequest_old2 r3 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2     // Catch:{ Exception -> 0x02eb }
            r3.<init>()     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media     // Catch:{ Exception -> 0x02eb }
            r33 = r4
            java.lang.String r4 = r5.phone_number     // Catch:{ Exception -> 0x02eb }
            r3.phone = r4     // Catch:{ Exception -> 0x02eb }
            java.lang.String r4 = r5.first_name     // Catch:{ Exception -> 0x02eb }
            r3.first_name = r4     // Catch:{ Exception -> 0x02eb }
            java.lang.String r4 = r5.last_name     // Catch:{ Exception -> 0x02eb }
            r3.last_name = r4     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r4 = new org.telegram.tgnet.TLRPC$TL_restrictionReason     // Catch:{ Exception -> 0x02eb }
            r4.<init>()     // Catch:{ Exception -> 0x02eb }
            r4.platform = r11     // Catch:{ Exception -> 0x02eb }
            r4.reason = r11     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media     // Catch:{ Exception -> 0x02eb }
            java.lang.String r5 = r5.vcard     // Catch:{ Exception -> 0x02eb }
            r4.text = r5     // Catch:{ Exception -> 0x02eb }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r5 = r3.restriction_reason     // Catch:{ Exception -> 0x02eb }
            r5.add(r4)     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media     // Catch:{ Exception -> 0x02eb }
            int r4 = r4.user_id     // Catch:{ Exception -> 0x02eb }
            r3.id = r4     // Catch:{ Exception -> 0x02eb }
            r4 = r59
            r5 = r3
            r19 = r18
            r34 = 6
            r3 = r58
            goto L_0x0285
        L_0x01fe:
            r33 = r4
            r4 = 8
            if (r3 == r4) goto L_0x0247
            r4 = 9
            if (r3 == r4) goto L_0x0247
            r4 = 13
            if (r3 == r4) goto L_0x0247
            r4 = 14
            if (r3 == r4) goto L_0x0247
            r4 = 15
            if (r3 != r4) goto L_0x0215
            goto L_0x0247
        L_0x0215:
            r4 = 2
            if (r3 != r4) goto L_0x0227
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x02eb }
            r4 = r59
            r5 = r61
            r19 = r18
            r34 = 8
            goto L_0x0266
        L_0x0227:
            r4 = 17
            if (r3 != r4) goto L_0x023c
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3     // Catch:{ Exception -> 0x02eb }
            r4 = r59
            r5 = r61
            r8 = r62
            r19 = r18
            r34 = 10
            r18 = r3
            goto L_0x0269
        L_0x023c:
            r3 = r58
            r4 = r59
            r5 = r61
            r19 = r18
            r34 = -1
            goto L_0x0285
        L_0x0247:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x02eb }
            r4 = r59
            r5 = r61
            r19 = r18
            r34 = 7
            goto L_0x0266
        L_0x0256:
            r33 = r4
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x02eb }
            r4 = r59
            r5 = r61
            r19 = r18
            r34 = 3
        L_0x0266:
            r18 = r8
            r8 = r3
        L_0x0269:
            r3 = r58
            goto L_0x0289
        L_0x026c:
            r33 = r4
            r32 = r5
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner     // Catch:{ Exception -> 0x02eb }
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media     // Catch:{ Exception -> 0x02eb }
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x02eb }
            if (r3 == 0) goto L_0x0279
            goto L_0x027b
        L_0x0279:
            java.lang.String r2 = r1.message     // Catch:{ Exception -> 0x02eb }
        L_0x027b:
            r3 = r58
            r4 = r59
            r5 = r61
            r19 = r18
            r34 = 0
        L_0x0285:
            r18 = r8
            goto L_0x0176
        L_0x0289:
            if (r12 == 0) goto L_0x0296
            boolean r35 = r12.containsKey(r10)     // Catch:{ Exception -> 0x02eb }
            if (r35 == 0) goto L_0x0296
            r56 = r2
            r34 = 9
            goto L_0x0298
        L_0x0296:
            r56 = r2
        L_0x0298:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x02eb }
            int r2 = r2.ttl_seconds     // Catch:{ Exception -> 0x02eb }
            if (r2 <= 0) goto L_0x02c3
            r13 = r2
            r7 = r6
            r30 = r10
            r37 = r14
            r36 = r18
            r38 = r19
            r63 = r27
            r2 = r32
            r10 = r34
            r32 = 0
            r6 = r55
            r14 = r65
            r27 = r3
            r18 = r5
            r19 = r8
            r5 = r23
            r3 = r29
            r34 = r33
            r29 = 0
            goto L_0x02e7
        L_0x02c3:
            r13 = r7
            r30 = r10
            r37 = r14
            r36 = r18
            r38 = r19
            r63 = r27
            r2 = r32
            r10 = r34
            r32 = 0
            r14 = r65
            r27 = r3
            r18 = r5
            r7 = r6
            r19 = r8
            r5 = r23
            r3 = r29
            r34 = r33
            r29 = 0
            r6 = r55
        L_0x02e7:
            r8 = r76
            goto L_0x0153
        L_0x02eb:
            r0 = move-exception
            r2 = r55
            r5 = r1
            r11 = r6
        L_0x02f0:
            r7 = 0
        L_0x02f1:
            r1 = r0
            goto L_0x1bc5
        L_0x02f4:
            r0 = move-exception
            r2 = r55
            r1 = r0
            r11 = r6
        L_0x02f9:
            r5 = 0
        L_0x02fa:
            r7 = 0
            goto L_0x1bc5
        L_0x02fd:
            r31 = r3
            r33 = r4
            r32 = r5
            if (r2 == 0) goto L_0x03a0
            if (r9 == 0) goto L_0x030d
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x02f4 }
            r1.<init>()     // Catch:{ Exception -> 0x02f4 }
            goto L_0x0312
        L_0x030d:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02f4 }
            r1.<init>()     // Catch:{ Exception -> 0x02f4 }
        L_0x0312:
            if (r9 == 0) goto L_0x0328
            boolean r3 = r14 instanceof org.telegram.tgnet.TLRPC$TL_webPagePending     // Catch:{ Exception -> 0x02eb }
            if (r3 == 0) goto L_0x0328
            java.lang.String r3 = r14.url     // Catch:{ Exception -> 0x02eb }
            if (r3 == 0) goto L_0x0327
            org.telegram.tgnet.TLRPC$TL_webPageUrlPending r3 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending     // Catch:{ Exception -> 0x02eb }
            r3.<init>()     // Catch:{ Exception -> 0x02eb }
            java.lang.String r4 = r14.url     // Catch:{ Exception -> 0x02eb }
            r3.url = r4     // Catch:{ Exception -> 0x02eb }
            r14 = r3
            goto L_0x0328
        L_0x0327:
            r14 = 0
        L_0x0328:
            int r3 = r56.length()     // Catch:{ Exception -> 0x02eb }
            r4 = 30
            if (r3 >= r4) goto L_0x036e
            if (r14 != 0) goto L_0x036e
            if (r13 == 0) goto L_0x033a
            boolean r3 = r73.isEmpty()     // Catch:{ Exception -> 0x02eb }
            if (r3 == 0) goto L_0x036e
        L_0x033a:
            org.telegram.messenger.MessagesController r3 = r55.getMessagesController()     // Catch:{ Exception -> 0x02eb }
            java.util.HashSet<java.lang.String> r3 = r3.diceEmojies     // Catch:{ Exception -> 0x02eb }
            java.lang.String r4 = "️"
            java.lang.String r4 = r2.replace(r4, r11)     // Catch:{ Exception -> 0x02eb }
            boolean r3 = r3.contains(r4)     // Catch:{ Exception -> 0x02eb }
            if (r3 == 0) goto L_0x036e
            if (r9 != 0) goto L_0x036e
            if (r6 != 0) goto L_0x036e
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaDice     // Catch:{ Exception -> 0x02eb }
            r3.<init>()     // Catch:{ Exception -> 0x02eb }
            r3.emoticon = r2     // Catch:{ Exception -> 0x02eb }
            r4 = -1
            r3.value = r4     // Catch:{ Exception -> 0x02eb }
            r1.media = r3     // Catch:{ Exception -> 0x02eb }
            r4 = r1
            r7 = r6
            r3 = r11
            r63 = r14
            r5 = r23
            r2 = r32
            r34 = r33
            r8 = 0
            r18 = 0
            r19 = 11
            goto L_0x03c2
        L_0x036e:
            if (r14 != 0) goto L_0x0378
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x02eb }
            r3.<init>()     // Catch:{ Exception -> 0x02eb }
            r1.media = r3     // Catch:{ Exception -> 0x02eb }
            goto L_0x0381
        L_0x0378:
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x02eb }
            r3.<init>()     // Catch:{ Exception -> 0x02eb }
            r1.media = r3     // Catch:{ Exception -> 0x02eb }
            r3.webpage = r14     // Catch:{ Exception -> 0x02eb }
        L_0x0381:
            if (r12 == 0) goto L_0x038c
            boolean r3 = r12.containsKey(r10)     // Catch:{ Exception -> 0x02eb }
            if (r3 == 0) goto L_0x038c
            r19 = 9
            goto L_0x038e
        L_0x038c:
            r19 = 0
        L_0x038e:
            r1.message = r2     // Catch:{ Exception -> 0x02eb }
            r4 = r1
            r7 = r6
            r63 = r14
            r3 = r18
            r5 = r23
            r2 = r32
            r34 = r33
            r8 = 0
            r18 = 0
            goto L_0x03c2
        L_0x03a0:
            if (r8 == 0) goto L_0x03ca
            if (r9 == 0) goto L_0x03aa
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x02f4 }
            r1.<init>()     // Catch:{ Exception -> 0x02f4 }
            goto L_0x03af
        L_0x03aa:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02f4 }
            r1.<init>()     // Catch:{ Exception -> 0x02f4 }
        L_0x03af:
            r1.media = r8     // Catch:{ Exception -> 0x02eb }
            r4 = r1
            r7 = r6
            r63 = r14
            r3 = r18
            r5 = r23
            r2 = r32
            r34 = r33
            r8 = 0
            r18 = 0
            r19 = 10
        L_0x03c2:
            r6 = r55
            r14 = r65
            r1 = r67
            goto L_0x0768
        L_0x03ca:
            r1 = r58
            if (r1 == 0) goto L_0x0419
            if (r9 == 0) goto L_0x03d6
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x02f4 }
            r3.<init>()     // Catch:{ Exception -> 0x02f4 }
            goto L_0x03db
        L_0x03d6:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02f4 }
            r3.<init>()     // Catch:{ Exception -> 0x02f4 }
        L_0x03db:
            r3.media = r1     // Catch:{ Exception -> 0x0412 }
            if (r12 == 0) goto L_0x03f9
            boolean r4 = r12.containsKey(r10)     // Catch:{ Exception -> 0x0412 }
            if (r4 == 0) goto L_0x03f9
            r1 = r67
            r4 = r3
            r7 = r6
            r63 = r14
            r3 = r18
            r5 = r23
            r2 = r32
            r34 = r33
        L_0x03f3:
            r8 = 0
            r18 = 0
            r19 = 9
            goto L_0x040c
        L_0x03f9:
            r1 = r67
            r4 = r3
            r7 = r6
            r63 = r14
            r3 = r18
            r5 = r23
            r2 = r32
            r34 = r33
            r8 = 0
            r18 = 0
            r19 = 1
        L_0x040c:
            r6 = r55
        L_0x040e:
            r14 = r65
            goto L_0x0768
        L_0x0412:
            r0 = move-exception
            r2 = r55
            r1 = r0
            r5 = r3
            goto L_0x04ce
        L_0x0419:
            r3 = r59
            if (r3 == 0) goto L_0x0491
            if (r9 == 0) goto L_0x0425
            org.telegram.tgnet.TLRPC$TL_message_secret r4 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x02f4 }
            r4.<init>()     // Catch:{ Exception -> 0x02f4 }
            goto L_0x042a
        L_0x0425:
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02f4 }
            r4.<init>()     // Catch:{ Exception -> 0x02f4 }
        L_0x042a:
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x04c9 }
            r5.<init>()     // Catch:{ Exception -> 0x04c9 }
            r4.media = r5     // Catch:{ Exception -> 0x04c9 }
            int r1 = r5.flags     // Catch:{ Exception -> 0x04c9 }
            r19 = 3
            r1 = r1 | 3
            r5.flags = r1     // Catch:{ Exception -> 0x04c9 }
            if (r13 == 0) goto L_0x043d
            r4.entities = r13     // Catch:{ Exception -> 0x04c9 }
        L_0x043d:
            if (r7 == 0) goto L_0x0449
            r5.ttl_seconds = r7     // Catch:{ Exception -> 0x04c9 }
            r4.ttl = r7     // Catch:{ Exception -> 0x04c9 }
            r19 = 4
            r1 = r1 | 4
            r5.flags = r1     // Catch:{ Exception -> 0x04c9 }
        L_0x0449:
            r5.photo = r3     // Catch:{ Exception -> 0x04c9 }
            if (r12 == 0) goto L_0x0458
            boolean r1 = r12.containsKey(r10)     // Catch:{ Exception -> 0x04c9 }
            if (r1 == 0) goto L_0x0458
            r1 = r67
            r19 = 9
            goto L_0x045c
        L_0x0458:
            r1 = r67
            r19 = 2
        L_0x045c:
            if (r1 == 0) goto L_0x0471
            int r5 = r67.length()     // Catch:{ Exception -> 0x04c9 }
            if (r5 <= 0) goto L_0x0471
            r5 = r33
            boolean r33 = r1.startsWith(r5)     // Catch:{ Exception -> 0x04c9 }
            if (r33 == 0) goto L_0x0473
            r4.attachPath = r1     // Catch:{ Exception -> 0x04c9 }
            r34 = r5
            goto L_0x04bb
        L_0x0471:
            r5 = r33
        L_0x0473:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r3.sizes     // Catch:{ Exception -> 0x04c9 }
            int r33 = r2.size()     // Catch:{ Exception -> 0x04c9 }
            r34 = r5
            r3 = 1
            int r5 = r33 + -1
            java.lang.Object r2 = r2.get(r5)     // Catch:{ Exception -> 0x04c9 }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2     // Catch:{ Exception -> 0x04c9 }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.location     // Catch:{ Exception -> 0x04c9 }
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r3)     // Catch:{ Exception -> 0x04c9 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x04c9 }
            r4.attachPath = r2     // Catch:{ Exception -> 0x04c9 }
            goto L_0x04bb
        L_0x0491:
            r2 = r63
            r1 = r67
            r3 = r7
            r34 = r33
            if (r2 == 0) goto L_0x04d1
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x02f4 }
            r4.<init>()     // Catch:{ Exception -> 0x02f4 }
            org.telegram.tgnet.TLRPC$TL_messageMediaGame r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x04c9 }
            r5.<init>()     // Catch:{ Exception -> 0x04c9 }
            r4.media = r5     // Catch:{ Exception -> 0x04c9 }
            r5.game = r2     // Catch:{ Exception -> 0x04c9 }
            if (r12 == 0) goto L_0x04bb
            boolean r2 = r12.containsKey(r10)     // Catch:{ Exception -> 0x04c9 }
            if (r2 == 0) goto L_0x04bb
            r7 = r6
            r63 = r14
            r3 = r18
            r5 = r23
            r2 = r32
            goto L_0x03f3
        L_0x04bb:
            r7 = r6
            r63 = r14
            r3 = r18
            r5 = r23
            r2 = r32
            r8 = 0
            r18 = 0
            goto L_0x040c
        L_0x04c9:
            r0 = move-exception
            r2 = r55
            r1 = r0
            r5 = r4
        L_0x04ce:
            r11 = r6
            goto L_0x02fa
        L_0x04d1:
            r2 = r61
            r7 = 0
            if (r2 == 0) goto L_0x0584
            if (r9 == 0) goto L_0x04e6
            org.telegram.tgnet.TLRPC$TL_message_secret r4 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x04de }
            r4.<init>()     // Catch:{ Exception -> 0x04de }
            goto L_0x04eb
        L_0x04de:
            r0 = move-exception
            r2 = r55
            r1 = r0
            r11 = r6
            r5 = r7
            goto L_0x1bc5
        L_0x04e6:
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x057c }
            r4.<init>()     // Catch:{ Exception -> 0x057c }
        L_0x04eb:
            r5 = r4
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact     // Catch:{ Exception -> 0x0574 }
            r4.<init>()     // Catch:{ Exception -> 0x0574 }
            r5.media = r4     // Catch:{ Exception -> 0x0574 }
            java.lang.String r7 = r2.phone     // Catch:{ Exception -> 0x0574 }
            r4.phone_number = r7     // Catch:{ Exception -> 0x0574 }
            java.lang.String r7 = r2.first_name     // Catch:{ Exception -> 0x0574 }
            r4.first_name = r7     // Catch:{ Exception -> 0x0574 }
            java.lang.String r7 = r2.last_name     // Catch:{ Exception -> 0x0574 }
            r4.last_name = r7     // Catch:{ Exception -> 0x0574 }
            int r7 = r2.id     // Catch:{ Exception -> 0x0574 }
            r4.user_id = r7     // Catch:{ Exception -> 0x0574 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r4 = r2.restriction_reason     // Catch:{ Exception -> 0x0574 }
            boolean r4 = r4.isEmpty()     // Catch:{ Exception -> 0x0574 }
            if (r4 != 0) goto L_0x052e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r4 = r2.restriction_reason     // Catch:{ Exception -> 0x0574 }
            r7 = 0
            java.lang.Object r4 = r4.get(r7)     // Catch:{ Exception -> 0x0574 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r4 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r4     // Catch:{ Exception -> 0x0574 }
            java.lang.String r4 = r4.text     // Catch:{ Exception -> 0x0574 }
            java.lang.String r7 = "BEGIN:VCARD"
            boolean r4 = r4.startsWith(r7)     // Catch:{ Exception -> 0x0574 }
            if (r4 == 0) goto L_0x052e
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0574 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r7 = r2.restriction_reason     // Catch:{ Exception -> 0x0574 }
            r6 = 0
            java.lang.Object r7 = r7.get(r6)     // Catch:{ Exception -> 0x0574 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r7 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r7     // Catch:{ Exception -> 0x0574 }
            java.lang.String r6 = r7.text     // Catch:{ Exception -> 0x0574 }
            r4.vcard = r6     // Catch:{ Exception -> 0x0574 }
            goto L_0x0532
        L_0x052e:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0574 }
            r4.vcard = r11     // Catch:{ Exception -> 0x0574 }
        L_0x0532:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r5.media     // Catch:{ Exception -> 0x0574 }
            java.lang.String r6 = r4.first_name     // Catch:{ Exception -> 0x0574 }
            if (r6 != 0) goto L_0x053c
            r4.first_name = r11     // Catch:{ Exception -> 0x0574 }
            r2.first_name = r11     // Catch:{ Exception -> 0x0574 }
        L_0x053c:
            java.lang.String r6 = r4.last_name     // Catch:{ Exception -> 0x0574 }
            if (r6 != 0) goto L_0x0544
            r4.last_name = r11     // Catch:{ Exception -> 0x0574 }
            r2.last_name = r11     // Catch:{ Exception -> 0x0574 }
        L_0x0544:
            if (r12 == 0) goto L_0x0560
            boolean r4 = r12.containsKey(r10)     // Catch:{ Exception -> 0x0574 }
            if (r4 == 0) goto L_0x0560
            r6 = r55
            r7 = r77
            r4 = r5
            r63 = r14
            r3 = r18
            r5 = r23
            r2 = r32
            r8 = 0
            r18 = 0
            r19 = 9
            goto L_0x040e
        L_0x0560:
            r6 = r55
            r7 = r77
            r4 = r5
            r63 = r14
            r3 = r18
            r5 = r23
            r2 = r32
            r8 = 0
            r18 = 0
            r19 = 6
            goto L_0x040e
        L_0x0574:
            r0 = move-exception
            r2 = r55
            r11 = r77
            r1 = r0
            goto L_0x02fa
        L_0x057c:
            r0 = move-exception
            r2 = r55
            r11 = r77
            r1 = r0
            goto L_0x02f9
        L_0x0584:
            r4 = r62
            r7 = r6
            if (r4 == 0) goto L_0x0756
            if (r9 == 0) goto L_0x0591
            org.telegram.tgnet.TLRPC$TL_message_secret r5 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x074f }
            r5.<init>()     // Catch:{ Exception -> 0x074f }
            goto L_0x0596
        L_0x0591:
            org.telegram.tgnet.TLRPC$TL_message r5 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x074f }
            r5.<init>()     // Catch:{ Exception -> 0x074f }
        L_0x0596:
            if (r32 >= 0) goto L_0x05d3
            org.telegram.messenger.MessagesController r6 = r55.getMessagesController()     // Catch:{ Exception -> 0x05d0 }
            r2 = r32
            int r8 = -r2
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x05d0 }
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r8)     // Catch:{ Exception -> 0x05d0 }
            if (r6 == 0) goto L_0x05d5
            boolean r6 = org.telegram.messenger.ChatObject.canSendStickers(r6)     // Catch:{ Exception -> 0x05d0 }
            if (r6 != 0) goto L_0x05d5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes     // Catch:{ Exception -> 0x05d0 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x05d0 }
            r8 = 0
        L_0x05b6:
            if (r8 >= r6) goto L_0x05d5
            r63 = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes     // Catch:{ Exception -> 0x05d0 }
            java.lang.Object r6 = r6.get(r8)     // Catch:{ Exception -> 0x05d0 }
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x05d0 }
            if (r6 == 0) goto L_0x05cb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes     // Catch:{ Exception -> 0x05d0 }
            r6.remove(r8)     // Catch:{ Exception -> 0x05d0 }
            r6 = 1
            goto L_0x05d6
        L_0x05cb:
            int r8 = r8 + 1
            r6 = r63
            goto L_0x05b6
        L_0x05d0:
            r0 = move-exception
            goto L_0x074b
        L_0x05d3:
            r2 = r32
        L_0x05d5:
            r6 = 0
        L_0x05d6:
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r8 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0748 }
            r8.<init>()     // Catch:{ Exception -> 0x0748 }
            r5.media = r8     // Catch:{ Exception -> 0x0748 }
            r63 = r6
            int r6 = r8.flags     // Catch:{ Exception -> 0x0748 }
            r19 = 3
            r6 = r6 | 3
            r8.flags = r6     // Catch:{ Exception -> 0x0748 }
            if (r3 == 0) goto L_0x05f3
            r8.ttl_seconds = r3     // Catch:{ Exception -> 0x05d0 }
            r5.ttl = r3     // Catch:{ Exception -> 0x05d0 }
            r28 = 4
            r6 = r6 | 4
            r8.flags = r6     // Catch:{ Exception -> 0x05d0 }
        L_0x05f3:
            r8.document = r4     // Catch:{ Exception -> 0x0748 }
            if (r12 == 0) goto L_0x0600
            boolean r6 = r12.containsKey(r10)     // Catch:{ Exception -> 0x05d0 }
            if (r6 == 0) goto L_0x0600
            r6 = 9
            goto L_0x061b
        L_0x0600:
            boolean r6 = org.telegram.messenger.MessageObject.isVideoDocument(r62)     // Catch:{ Exception -> 0x0748 }
            if (r6 != 0) goto L_0x061a
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r62)     // Catch:{ Exception -> 0x05d0 }
            if (r6 != 0) goto L_0x061a
            if (r60 == 0) goto L_0x060f
            goto L_0x061a
        L_0x060f:
            boolean r6 = org.telegram.messenger.MessageObject.isVoiceDocument(r62)     // Catch:{ Exception -> 0x05d0 }
            if (r6 == 0) goto L_0x0618
            r6 = 8
            goto L_0x061b
        L_0x0618:
            r6 = 7
            goto L_0x061b
        L_0x061a:
            r6 = 3
        L_0x061b:
            if (r60 == 0) goto L_0x062e
            java.lang.String r8 = r60.getString()     // Catch:{ Exception -> 0x05d0 }
            if (r12 != 0) goto L_0x0628
            java.util.HashMap r12 = new java.util.HashMap     // Catch:{ Exception -> 0x05d0 }
            r12.<init>()     // Catch:{ Exception -> 0x05d0 }
        L_0x0628:
            java.lang.String r3 = "ve"
            r12.put(r3, r8)     // Catch:{ Exception -> 0x05d0 }
        L_0x062e:
            if (r9 == 0) goto L_0x064c
            int r3 = r4.dc_id     // Catch:{ Exception -> 0x05d0 }
            if (r3 <= 0) goto L_0x064c
            boolean r3 = org.telegram.messenger.MessageObject.isStickerDocument(r62)     // Catch:{ Exception -> 0x05d0 }
            if (r3 != 0) goto L_0x064c
            r3 = 1
            boolean r8 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r3)     // Catch:{ Exception -> 0x05d0 }
            if (r8 != 0) goto L_0x064c
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r62)     // Catch:{ Exception -> 0x05d0 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x05d0 }
            r5.attachPath = r3     // Catch:{ Exception -> 0x05d0 }
            goto L_0x064e
        L_0x064c:
            r5.attachPath = r1     // Catch:{ Exception -> 0x0748 }
        L_0x064e:
            if (r9 == 0) goto L_0x072e
            boolean r3 = org.telegram.messenger.MessageObject.isStickerDocument(r62)     // Catch:{ Exception -> 0x0748 }
            if (r3 != 0) goto L_0x065d
            r3 = 1
            boolean r8 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r3)     // Catch:{ Exception -> 0x05d0 }
            if (r8 == 0) goto L_0x072e
        L_0x065d:
            r3 = 0
        L_0x065e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r4.attributes     // Catch:{ Exception -> 0x0748 }
            int r8 = r8.size()     // Catch:{ Exception -> 0x0748 }
            if (r3 >= r8) goto L_0x072e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r4.attributes     // Catch:{ Exception -> 0x0748 }
            java.lang.Object r8 = r8.get(r3)     // Catch:{ Exception -> 0x0748 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r8 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r8     // Catch:{ Exception -> 0x0748 }
            r30 = r5
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker     // Catch:{ Exception -> 0x0725 }
            if (r5 == 0) goto L_0x070b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r4.attributes     // Catch:{ Exception -> 0x0725 }
            r5.remove(r3)     // Catch:{ Exception -> 0x0725 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55 r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55     // Catch:{ Exception -> 0x0725 }
            r3.<init>()     // Catch:{ Exception -> 0x0725 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r4.attributes     // Catch:{ Exception -> 0x0725 }
            r5.add(r3)     // Catch:{ Exception -> 0x0725 }
            java.lang.String r5 = r8.alt     // Catch:{ Exception -> 0x0725 }
            r3.alt = r5     // Catch:{ Exception -> 0x0725 }
            org.telegram.tgnet.TLRPC$InputStickerSet r5 = r8.stickerset     // Catch:{ Exception -> 0x0725 }
            if (r5 == 0) goto L_0x06f5
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x0725 }
            if (r4 == 0) goto L_0x0694
            java.lang.String r4 = r5.short_name     // Catch:{ Exception -> 0x0725 }
            r32 = r6
            goto L_0x06a2
        L_0x0694:
            org.telegram.messenger.MediaDataController r4 = r55.getMediaDataController()     // Catch:{ Exception -> 0x0725 }
            org.telegram.tgnet.TLRPC$InputStickerSet r5 = r8.stickerset     // Catch:{ Exception -> 0x0725 }
            r32 = r6
            long r5 = r5.id     // Catch:{ Exception -> 0x0725 }
            java.lang.String r4 = r4.getStickerSetName(r5)     // Catch:{ Exception -> 0x0725 }
        L_0x06a2:
            boolean r5 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0725 }
            if (r5 != 0) goto L_0x06bb
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r5 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x0725 }
            r5.<init>()     // Catch:{ Exception -> 0x0725 }
            r3.stickerset = r5     // Catch:{ Exception -> 0x0725 }
            r5.short_name = r4     // Catch:{ Exception -> 0x0725 }
            r6 = r55
            r14 = r65
            r75 = r12
            r5 = r23
            r4 = 0
            goto L_0x06eb
        L_0x06bb:
            org.telegram.tgnet.TLRPC$InputStickerSet r4 = r8.stickerset     // Catch:{ Exception -> 0x0725 }
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetID     // Catch:{ Exception -> 0x0725 }
            if (r4 == 0) goto L_0x06db
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r4 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0725 }
            r6 = r55
            r14 = r65
            r75 = r12
            r5 = r23
            r12 = 3
            r4.<init>(r14)     // Catch:{ Exception -> 0x0707 }
            r4.encryptedChat = r9     // Catch:{ Exception -> 0x0707 }
            r4.locationParent = r3     // Catch:{ Exception -> 0x0707 }
            r12 = 5
            r4.type = r12     // Catch:{ Exception -> 0x0707 }
            org.telegram.tgnet.TLRPC$InputStickerSet r8 = r8.stickerset     // Catch:{ Exception -> 0x0707 }
            r4.parentObject = r8     // Catch:{ Exception -> 0x0707 }
            goto L_0x06e4
        L_0x06db:
            r6 = r55
            r14 = r65
            r75 = r12
            r5 = r23
            r4 = 0
        L_0x06e4:
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r8 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0707 }
            r8.<init>()     // Catch:{ Exception -> 0x0707 }
            r3.stickerset = r8     // Catch:{ Exception -> 0x0707 }
        L_0x06eb:
            r12 = r75
            r8 = r4
            r3 = r18
            r4 = r30
            r19 = r32
            goto L_0x0743
        L_0x06f5:
            r14 = r65
            r32 = r6
            r75 = r12
            r5 = r23
            r6 = r55
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0707 }
            r4.<init>()     // Catch:{ Exception -> 0x0707 }
            r3.stickerset = r4     // Catch:{ Exception -> 0x0707 }
            goto L_0x073a
        L_0x0707:
            r0 = move-exception
            r1 = r0
            r2 = r6
            goto L_0x0729
        L_0x070b:
            r14 = r65
            r32 = r6
            r75 = r12
            r5 = r23
            r6 = r55
            int r3 = r3 + 1
            r4 = r62
            r14 = r70
            r15 = r72
            r5 = r30
            r6 = r32
            r19 = 3
            goto L_0x065e
        L_0x0725:
            r0 = move-exception
            r2 = r55
            r1 = r0
        L_0x0729:
            r11 = r7
            r5 = r30
            goto L_0x02fa
        L_0x072e:
            r14 = r65
            r30 = r5
            r32 = r6
            r75 = r12
            r5 = r23
            r6 = r55
        L_0x073a:
            r12 = r75
            r3 = r18
            r4 = r30
            r19 = r32
            r8 = 0
        L_0x0743:
            r18 = r63
            r63 = r70
            goto L_0x0768
        L_0x0748:
            r0 = move-exception
            r30 = r5
        L_0x074b:
            r2 = r55
            r1 = r0
            goto L_0x077f
        L_0x074f:
            r0 = move-exception
            r2 = r55
            r1 = r0
            r11 = r7
            goto L_0x02f9
        L_0x0756:
            r6 = r55
            r14 = r65
            r5 = r23
            r2 = r32
            r63 = r70
            r12 = r75
            r3 = r18
            r4 = 0
            r8 = 0
            r18 = 0
        L_0x0768:
            if (r13 == 0) goto L_0x0782
            boolean r30 = r73.isEmpty()     // Catch:{ Exception -> 0x077b }
            if (r30 != 0) goto L_0x0782
            r4.entities = r13     // Catch:{ Exception -> 0x077b }
            r70 = r8
            int r8 = r4.flags     // Catch:{ Exception -> 0x077b }
            r8 = r8 | 128(0x80, float:1.794E-43)
            r4.flags = r8     // Catch:{ Exception -> 0x077b }
            goto L_0x0784
        L_0x077b:
            r0 = move-exception
            r1 = r0
            r5 = r4
            r2 = r6
        L_0x077f:
            r11 = r7
            goto L_0x02fa
        L_0x0782:
            r70 = r8
        L_0x0784:
            if (r3 == 0) goto L_0x0789
            r4.message = r3     // Catch:{ Exception -> 0x077b }
            goto L_0x078f
        L_0x0789:
            java.lang.String r8 = r4.message     // Catch:{ Exception -> 0x1bbe }
            if (r8 != 0) goto L_0x078f
            r4.message = r11     // Catch:{ Exception -> 0x077b }
        L_0x078f:
            java.lang.String r8 = r4.attachPath     // Catch:{ Exception -> 0x1bbe }
            if (r8 != 0) goto L_0x0795
            r4.attachPath = r11     // Catch:{ Exception -> 0x077b }
        L_0x0795:
            org.telegram.messenger.UserConfig r8 = r55.getUserConfig()     // Catch:{ Exception -> 0x1bbe }
            int r8 = r8.getNewMessageId()     // Catch:{ Exception -> 0x1bbe }
            r4.id = r8     // Catch:{ Exception -> 0x1bbe }
            r4.local_id = r8     // Catch:{ Exception -> 0x1bbe }
            r8 = 1
            r4.out = r8     // Catch:{ Exception -> 0x1bbe }
            if (r24 == 0) goto L_0x07ba
            if (r29 == 0) goto L_0x07ba
            org.telegram.tgnet.TLRPC$TL_peerChannel r8 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ Exception -> 0x077b }
            r8.<init>()     // Catch:{ Exception -> 0x077b }
            r4.from_id = r8     // Catch:{ Exception -> 0x077b }
            r75 = r3
            r3 = r29
            r29 = r12
            int r12 = r3.channel_id     // Catch:{ Exception -> 0x077b }
            r8.channel_id = r12     // Catch:{ Exception -> 0x077b }
            goto L_0x07dc
        L_0x07ba:
            r75 = r3
            r3 = r29
            r29 = r12
            if (r27 == 0) goto L_0x07cd
            org.telegram.messenger.MessagesController r8 = r55.getMessagesController()     // Catch:{ Exception -> 0x077b }
            org.telegram.tgnet.TLRPC$Peer r8 = r8.getPeer(r2)     // Catch:{ Exception -> 0x077b }
            r4.from_id = r8     // Catch:{ Exception -> 0x077b }
            goto L_0x07dc
        L_0x07cd:
            org.telegram.tgnet.TLRPC$TL_peerUser r8 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x1bbe }
            r8.<init>()     // Catch:{ Exception -> 0x1bbe }
            r4.from_id = r8     // Catch:{ Exception -> 0x1bbe }
            r8.user_id = r5     // Catch:{ Exception -> 0x1bbe }
            int r8 = r4.flags     // Catch:{ Exception -> 0x1bbe }
            r8 = r8 | 256(0x100, float:3.59E-43)
            r4.flags = r8     // Catch:{ Exception -> 0x1bbe }
        L_0x07dc:
            org.telegram.messenger.UserConfig r8 = r55.getUserConfig()     // Catch:{ Exception -> 0x1bbe }
            r12 = 0
            r8.saveConfig(r12)     // Catch:{ Exception -> 0x1bbe }
            r27 = r58
            r37 = r63
            r36 = r64
            r38 = r75
            r8 = r76
            r13 = r78
            r63 = r79
            r12 = r4
            r30 = r10
            r32 = r18
            r10 = r19
            r1 = r29
            r4 = r59
            r18 = r61
            r19 = r62
            r29 = r70
        L_0x0803:
            if (r8 == 0) goto L_0x0831
            r58 = r4
            int r4 = r6.currentAccount     // Catch:{ Exception -> 0x082a }
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4)     // Catch:{ Exception -> 0x082a }
            r70 = r10
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x082a }
            r10.<init>()     // Catch:{ Exception -> 0x082a }
            java.lang.String r8 = "silent_"
            r10.append(r8)     // Catch:{ Exception -> 0x082a }
            r10.append(r14)     // Catch:{ Exception -> 0x082a }
            java.lang.String r8 = r10.toString()     // Catch:{ Exception -> 0x082a }
            r10 = 0
            boolean r4 = r4.getBoolean(r8, r10)     // Catch:{ Exception -> 0x082a }
            if (r4 == 0) goto L_0x0828
            goto L_0x0835
        L_0x0828:
            r4 = 0
            goto L_0x0836
        L_0x082a:
            r0 = move-exception
            r1 = r0
            r2 = r6
            r11 = r7
        L_0x082e:
            r5 = r12
            goto L_0x02fa
        L_0x0831:
            r58 = r4
            r70 = r10
        L_0x0835:
            r4 = 1
        L_0x0836:
            r12.silent = r4     // Catch:{ Exception -> 0x1bb7 }
            long r14 = r12.random_id     // Catch:{ Exception -> 0x1bb7 }
            int r4 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x0844
            long r14 = r55.getNextRandomId()     // Catch:{ Exception -> 0x082a }
            r12.random_id = r14     // Catch:{ Exception -> 0x082a }
        L_0x0844:
            java.lang.String r8 = "bot"
            java.lang.String r10 = "bot_name"
            if (r1 == 0) goto L_0x0875
            boolean r4 = r1.containsKey(r8)     // Catch:{ Exception -> 0x082a }
            if (r4 == 0) goto L_0x0875
            if (r9 == 0) goto L_0x085f
            java.lang.Object r4 = r1.get(r10)     // Catch:{ Exception -> 0x082a }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x082a }
            r12.via_bot_name = r4     // Catch:{ Exception -> 0x082a }
            if (r4 != 0) goto L_0x086f
            r12.via_bot_name = r11     // Catch:{ Exception -> 0x082a }
            goto L_0x086f
        L_0x085f:
            java.lang.Object r4 = r1.get(r8)     // Catch:{ Exception -> 0x082a }
            java.lang.CharSequence r4 = (java.lang.CharSequence) r4     // Catch:{ Exception -> 0x082a }
            java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r4)     // Catch:{ Exception -> 0x082a }
            int r4 = r4.intValue()     // Catch:{ Exception -> 0x082a }
            r12.via_bot_id = r4     // Catch:{ Exception -> 0x082a }
        L_0x086f:
            int r4 = r12.flags     // Catch:{ Exception -> 0x082a }
            r4 = r4 | 2048(0x800, float:2.87E-42)
            r12.flags = r4     // Catch:{ Exception -> 0x082a }
        L_0x0875:
            r12.params = r1     // Catch:{ Exception -> 0x1bb7 }
            r14 = r65
            r4 = r72
            if (r4 == 0) goto L_0x0884
            r59 = r10
            boolean r10 = r4.resendAsIs     // Catch:{ Exception -> 0x082a }
            if (r10 != 0) goto L_0x08d1
            goto L_0x0886
        L_0x0884:
            r59 = r10
        L_0x0886:
            if (r7 == 0) goto L_0x088a
            r10 = r7
            goto L_0x0892
        L_0x088a:
            org.telegram.tgnet.ConnectionsManager r10 = r55.getConnectionsManager()     // Catch:{ Exception -> 0x1bb7 }
            int r10 = r10.getCurrentTime()     // Catch:{ Exception -> 0x1bb7 }
        L_0x0892:
            r12.date = r10     // Catch:{ Exception -> 0x1bb7 }
            boolean r10 = r3 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x1bb7 }
            if (r10 == 0) goto L_0x08ce
            if (r7 != 0) goto L_0x08a5
            if (r24 == 0) goto L_0x08a5
            r10 = 1
            r12.views = r10     // Catch:{ Exception -> 0x082a }
            int r10 = r12.flags     // Catch:{ Exception -> 0x082a }
            r10 = r10 | 1024(0x400, float:1.435E-42)
            r12.flags = r10     // Catch:{ Exception -> 0x082a }
        L_0x08a5:
            org.telegram.messenger.MessagesController r10 = r55.getMessagesController()     // Catch:{ Exception -> 0x082a }
            int r4 = r3.channel_id     // Catch:{ Exception -> 0x082a }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x082a }
            org.telegram.tgnet.TLRPC$Chat r4 = r10.getChat(r4)     // Catch:{ Exception -> 0x082a }
            if (r4 == 0) goto L_0x08d1
            boolean r10 = r4.megagroup     // Catch:{ Exception -> 0x082a }
            if (r10 == 0) goto L_0x08bd
            r10 = 1
            r12.unread = r10     // Catch:{ Exception -> 0x082a }
            goto L_0x08d1
        L_0x08bd:
            r10 = 1
            r12.post = r10     // Catch:{ Exception -> 0x082a }
            boolean r4 = r4.signatures     // Catch:{ Exception -> 0x082a }
            if (r4 == 0) goto L_0x08d1
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x082a }
            r4.<init>()     // Catch:{ Exception -> 0x082a }
            r12.from_id = r4     // Catch:{ Exception -> 0x082a }
            r4.user_id = r5     // Catch:{ Exception -> 0x082a }
            goto L_0x08d1
        L_0x08ce:
            r4 = 1
            r12.unread = r4     // Catch:{ Exception -> 0x1bb7 }
        L_0x08d1:
            int r4 = r12.flags     // Catch:{ Exception -> 0x1bb7 }
            r4 = r4 | 512(0x200, float:7.175E-43)
            r12.flags = r4     // Catch:{ Exception -> 0x1bb7 }
            r12.dialog_id = r14     // Catch:{ Exception -> 0x1bb7 }
            r10 = r68
            if (r10 == 0) goto L_0x092f
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = new org.telegram.tgnet.TLRPC$TL_messageReplyHeader     // Catch:{ Exception -> 0x0928 }
            r4.<init>()     // Catch:{ Exception -> 0x0928 }
            r12.reply_to = r4     // Catch:{ Exception -> 0x0928 }
            if (r9 == 0) goto L_0x08fc
            r24 = r3
            org.telegram.tgnet.TLRPC$Message r3 = r10.messageOwner     // Catch:{ Exception -> 0x0928 }
            r61 = r8
            long r7 = r3.random_id     // Catch:{ Exception -> 0x0928 }
            int r3 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x0900
            r4.reply_to_random_id = r7     // Catch:{ Exception -> 0x0928 }
            int r3 = r12.flags     // Catch:{ Exception -> 0x0928 }
            r7 = 8
            r3 = r3 | r7
            r12.flags = r3     // Catch:{ Exception -> 0x0928 }
            goto L_0x0907
        L_0x08fc:
            r24 = r3
            r61 = r8
        L_0x0900:
            int r3 = r12.flags     // Catch:{ Exception -> 0x0928 }
            r7 = 8
            r3 = r3 | r7
            r12.flags = r3     // Catch:{ Exception -> 0x0928 }
        L_0x0907:
            int r3 = r68.getId()     // Catch:{ Exception -> 0x0928 }
            r4.reply_to_msg_id = r3     // Catch:{ Exception -> 0x0928 }
            r8 = r69
            r7 = r73
            if (r8 == 0) goto L_0x0937
            if (r8 == r10) goto L_0x0937
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r12.reply_to     // Catch:{ Exception -> 0x0928 }
            int r4 = r69.getId()     // Catch:{ Exception -> 0x0928 }
            r3.reply_to_top_id = r4     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r12.reply_to     // Catch:{ Exception -> 0x0928 }
            int r4 = r3.flags     // Catch:{ Exception -> 0x0928 }
            r21 = 2
            r4 = r4 | 2
            r3.flags = r4     // Catch:{ Exception -> 0x0928 }
            goto L_0x0937
        L_0x0928:
            r0 = move-exception
            r11 = r77
            r1 = r0
            r2 = r6
            goto L_0x082e
        L_0x092f:
            r7 = r73
            r24 = r3
            r61 = r8
            r8 = r69
        L_0x0937:
            if (r26 == 0) goto L_0x0956
            org.telegram.tgnet.TLRPC$TL_messageReplies r3 = new org.telegram.tgnet.TLRPC$TL_messageReplies     // Catch:{ Exception -> 0x0928 }
            r3.<init>()     // Catch:{ Exception -> 0x0928 }
            r12.replies = r3     // Catch:{ Exception -> 0x0928 }
            r4 = 1
            r3.comments = r4     // Catch:{ Exception -> 0x0928 }
            r4 = r26
            r3.channel_id = r4     // Catch:{ Exception -> 0x0928 }
            int r4 = r3.flags     // Catch:{ Exception -> 0x0928 }
            r22 = 1
            r4 = r4 | 1
            r3.flags = r4     // Catch:{ Exception -> 0x0928 }
            int r3 = r12.flags     // Catch:{ Exception -> 0x0928 }
            r4 = 8388608(0x800000, float:1.17549435E-38)
            r3 = r3 | r4
            r12.flags = r3     // Catch:{ Exception -> 0x0928 }
        L_0x0956:
            r3 = r74
            if (r3 == 0) goto L_0x0964
            if (r9 != 0) goto L_0x0964
            int r4 = r12.flags     // Catch:{ Exception -> 0x0928 }
            r4 = r4 | 64
            r12.flags = r4     // Catch:{ Exception -> 0x0928 }
            r12.reply_markup = r3     // Catch:{ Exception -> 0x0928 }
        L_0x0964:
            if (r2 == 0) goto L_0x0992
            org.telegram.messenger.MessagesController r3 = r55.getMessagesController()     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$Peer r3 = r3.getPeer(r2)     // Catch:{ Exception -> 0x0928 }
            r12.peer_id = r3     // Catch:{ Exception -> 0x0928 }
            if (r2 <= 0) goto L_0x098d
            org.telegram.messenger.MessagesController r3 = r55.getMessagesController()     // Catch:{ Exception -> 0x0928 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)     // Catch:{ Exception -> 0x0928 }
            if (r2 != 0) goto L_0x0986
            int r1 = r12.id     // Catch:{ Exception -> 0x0928 }
            r6.processSentMessage(r1)     // Catch:{ Exception -> 0x0928 }
            return
        L_0x0986:
            boolean r2 = r2.bot     // Catch:{ Exception -> 0x0928 }
            if (r2 == 0) goto L_0x098d
            r2 = 0
            r12.unread = r2     // Catch:{ Exception -> 0x0928 }
        L_0x098d:
            r2 = r25
            r3 = 1
            goto L_0x0a36
        L_0x0992:
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x1bb2 }
            r2.<init>()     // Catch:{ Exception -> 0x1bb2 }
            r12.peer_id = r2     // Catch:{ Exception -> 0x1bb2 }
            int r3 = r9.participant_id     // Catch:{ Exception -> 0x1bb2 }
            if (r3 != r5) goto L_0x09a2
            int r3 = r9.admin_id     // Catch:{ Exception -> 0x0928 }
            r2.user_id = r3     // Catch:{ Exception -> 0x0928 }
            goto L_0x09a4
        L_0x09a2:
            r2.user_id = r3     // Catch:{ Exception -> 0x1bb2 }
        L_0x09a4:
            if (r13 == 0) goto L_0x09a9
            r12.ttl = r13     // Catch:{ Exception -> 0x0928 }
            goto L_0x09bb
        L_0x09a9:
            int r2 = r9.ttl     // Catch:{ Exception -> 0x1bb2 }
            r12.ttl = r2     // Catch:{ Exception -> 0x1bb2 }
            if (r2 == 0) goto L_0x09bb
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r12.media     // Catch:{ Exception -> 0x0928 }
            if (r3 == 0) goto L_0x09bb
            r3.ttl_seconds = r2     // Catch:{ Exception -> 0x0928 }
            int r2 = r3.flags     // Catch:{ Exception -> 0x0928 }
            r4 = 4
            r2 = r2 | r4
            r3.flags = r2     // Catch:{ Exception -> 0x0928 }
        L_0x09bb:
            int r2 = r12.ttl     // Catch:{ Exception -> 0x1bb2 }
            if (r2 == 0) goto L_0x098d
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r12.media     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x0928 }
            if (r2 == 0) goto L_0x098d
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r12)     // Catch:{ Exception -> 0x0928 }
            if (r2 == 0) goto L_0x09fa
            r2 = 0
        L_0x09cc:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r12.media     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0928 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes     // Catch:{ Exception -> 0x0928 }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0928 }
            if (r2 >= r3) goto L_0x09ee
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r12.media     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0928 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes     // Catch:{ Exception -> 0x0928 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3     // Catch:{ Exception -> 0x0928 }
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ Exception -> 0x0928 }
            if (r4 == 0) goto L_0x09eb
            int r2 = r3.duration     // Catch:{ Exception -> 0x0928 }
            goto L_0x09ef
        L_0x09eb:
            int r2 = r2 + 1
            goto L_0x09cc
        L_0x09ee:
            r2 = 0
        L_0x09ef:
            int r3 = r12.ttl     // Catch:{ Exception -> 0x0928 }
            r4 = 1
            int r2 = r2 + r4
            int r2 = java.lang.Math.max(r3, r2)     // Catch:{ Exception -> 0x0928 }
            r12.ttl = r2     // Catch:{ Exception -> 0x0928 }
            goto L_0x098d
        L_0x09fa:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r12)     // Catch:{ Exception -> 0x0928 }
            if (r2 != 0) goto L_0x0a06
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r12)     // Catch:{ Exception -> 0x0928 }
            if (r2 == 0) goto L_0x098d
        L_0x0a06:
            r2 = 0
        L_0x0a07:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r12.media     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0928 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes     // Catch:{ Exception -> 0x0928 }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0928 }
            if (r2 >= r3) goto L_0x0a29
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r12.media     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0928 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes     // Catch:{ Exception -> 0x0928 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0928 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3     // Catch:{ Exception -> 0x0928 }
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x0928 }
            if (r4 == 0) goto L_0x0a26
            int r2 = r3.duration     // Catch:{ Exception -> 0x0928 }
            goto L_0x0a2a
        L_0x0a26:
            int r2 = r2 + 1
            goto L_0x0a07
        L_0x0a29:
            r2 = 0
        L_0x0a2a:
            int r3 = r12.ttl     // Catch:{ Exception -> 0x0928 }
            r4 = 1
            int r2 = r2 + r4
            int r2 = java.lang.Math.max(r3, r2)     // Catch:{ Exception -> 0x0928 }
            r12.ttl = r2     // Catch:{ Exception -> 0x0928 }
            goto L_0x098d
        L_0x0a36:
            if (r2 == r3) goto L_0x0a47
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r12)     // Catch:{ Exception -> 0x0928 }
            if (r2 != 0) goto L_0x0a44
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r12)     // Catch:{ Exception -> 0x0928 }
            if (r2 == 0) goto L_0x0a47
        L_0x0a44:
            r2 = 1
            r12.media_unread = r2     // Catch:{ Exception -> 0x0928 }
        L_0x0a47:
            org.telegram.tgnet.TLRPC$Peer r2 = r12.from_id     // Catch:{ Exception -> 0x1bb2 }
            if (r2 != 0) goto L_0x0a4f
            org.telegram.tgnet.TLRPC$Peer r2 = r12.peer_id     // Catch:{ Exception -> 0x0928 }
            r12.from_id = r2     // Catch:{ Exception -> 0x0928 }
        L_0x0a4f:
            r2 = 1
            r12.send_state = r2     // Catch:{ Exception -> 0x1bb2 }
            if (r1 == 0) goto L_0x0a81
            java.lang.String r2 = "groupId"
            java.lang.Object r2 = r1.get(r2)     // Catch:{ Exception -> 0x0928 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0928 }
            if (r2 == 0) goto L_0x0a70
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r2)     // Catch:{ Exception -> 0x0928 }
            long r2 = r2.longValue()     // Catch:{ Exception -> 0x0928 }
            r12.grouped_id = r2     // Catch:{ Exception -> 0x0928 }
            int r4 = r12.flags     // Catch:{ Exception -> 0x0928 }
            r5 = 131072(0x20000, float:1.83671E-40)
            r4 = r4 | r5
            r12.flags = r4     // Catch:{ Exception -> 0x0928 }
            goto L_0x0a72
        L_0x0a70:
            r2 = r16
        L_0x0a72:
            java.lang.String r4 = "final"
            java.lang.Object r4 = r1.get(r4)     // Catch:{ Exception -> 0x0928 }
            if (r4 == 0) goto L_0x0a7c
            r4 = 1
            goto L_0x0a7d
        L_0x0a7c:
            r4 = 0
        L_0x0a7d:
            r25 = r4
            r4 = r2
            goto L_0x0a85
        L_0x0a81:
            r4 = r16
            r25 = 0
        L_0x0a85:
            org.telegram.messenger.MessageObject r3 = new org.telegram.messenger.MessageObject     // Catch:{ Exception -> 0x1bb2 }
            int r2 = r6.currentAccount     // Catch:{ Exception -> 0x1bb2 }
            r26 = 1
            r33 = 1
            r35 = r2
            r2 = r3
            r40 = r3
            r39 = r24
            r41 = r31
            r3 = r35
            r44 = r58
            r42 = r4
            r45 = r34
            r5 = r72
            r4 = r12
            r46 = r20
            r5 = r68
            r10 = r6
            r6 = r26
            r20 = r11
            r57 = r13
            r11 = 0
            r13 = r77
            r7 = r33
            r2.<init>((int) r3, (org.telegram.tgnet.TLRPC$Message) r4, (org.telegram.messenger.MessageObject) r5, (boolean) r6, (boolean) r7)     // Catch:{ Exception -> 0x1bad }
            r2 = r40
            r3 = 1
            r2.wasJustSent = r3     // Catch:{ Exception -> 0x1ba5 }
            if (r13 == 0) goto L_0x0abd
            r3 = 1
            goto L_0x0abe
        L_0x0abd:
            r3 = 0
        L_0x0abe:
            r2.scheduled = r3     // Catch:{ Exception -> 0x1ba5 }
            boolean r3 = r2.isForwarded()     // Catch:{ Exception -> 0x1ba5 }
            if (r3 != 0) goto L_0x0ae4
            int r3 = r2.type     // Catch:{ Exception -> 0x0adc }
            r4 = 3
            if (r3 == r4) goto L_0x0ad0
            if (r60 != 0) goto L_0x0ad0
            r4 = 2
            if (r3 != r4) goto L_0x0ae4
        L_0x0ad0:
            java.lang.String r3 = r12.attachPath     // Catch:{ Exception -> 0x0adc }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0adc }
            if (r3 != 0) goto L_0x0ae4
            r3 = 1
            r2.attachPathExists = r3     // Catch:{ Exception -> 0x0adc }
            goto L_0x0ae4
        L_0x0adc:
            r0 = move-exception
            r1 = r0
            r7 = r2
            r2 = r10
            r5 = r12
        L_0x0ae1:
            r11 = r13
            goto L_0x1bc5
        L_0x0ae4:
            org.telegram.messenger.VideoEditedInfo r3 = r2.videoEditedInfo     // Catch:{ Exception -> 0x1ba5 }
            if (r3 == 0) goto L_0x0aeb
            if (r60 != 0) goto L_0x0aeb
            goto L_0x0aed
        L_0x0aeb:
            r3 = r60
        L_0x0aed:
            r4 = r42
            int r6 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r6 != 0) goto L_0x0b44
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x0adc }
            r6.<init>()     // Catch:{ Exception -> 0x0adc }
            r6.add(r2)     // Catch:{ Exception -> 0x0adc }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x0adc }
            r7.<init>()     // Catch:{ Exception -> 0x0adc }
            r7.add(r12)     // Catch:{ Exception -> 0x0adc }
            int r11 = r10.currentAccount     // Catch:{ Exception -> 0x0adc }
            org.telegram.messenger.MessagesStorage r47 = org.telegram.messenger.MessagesStorage.getInstance(r11)     // Catch:{ Exception -> 0x0adc }
            r49 = 0
            r50 = 1
            r51 = 0
            r52 = 0
            if (r13 == 0) goto L_0x0b16
            r53 = 1
            goto L_0x0b18
        L_0x0b16:
            r53 = 0
        L_0x0b18:
            r48 = r7
            r47.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r48, (boolean) r49, (boolean) r50, (boolean) r51, (int) r52, (boolean) r53)     // Catch:{ Exception -> 0x0adc }
            int r7 = r10.currentAccount     // Catch:{ Exception -> 0x0adc }
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)     // Catch:{ Exception -> 0x0adc }
            if (r13 == 0) goto L_0x0b27
            r11 = 1
            goto L_0x0b28
        L_0x0b27:
            r11 = 0
        L_0x0b28:
            r7.updateInterfaceWithMessages(r14, r6, r11)     // Catch:{ Exception -> 0x0adc }
            if (r13 != 0) goto L_0x0b3e
            int r6 = r10.currentAccount     // Catch:{ Exception -> 0x0adc }
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getInstance(r6)     // Catch:{ Exception -> 0x0adc }
            int r7 = org.telegram.messenger.NotificationCenter.dialogsNeedReload     // Catch:{ Exception -> 0x0adc }
            r58 = r3
            r11 = 0
            java.lang.Object[] r3 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x0adc }
            r6.postNotificationName(r7, r3)     // Catch:{ Exception -> 0x0adc }
            goto L_0x0b40
        L_0x0b3e:
            r58 = r3
        L_0x0b40:
            r3 = r29
            r6 = 0
            goto L_0x0b90
        L_0x0b44:
            r58 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1ba5 }
            r3.<init>()     // Catch:{ Exception -> 0x1ba5 }
            java.lang.String r6 = "group_"
            r3.append(r6)     // Catch:{ Exception -> 0x1ba5 }
            r3.append(r4)     // Catch:{ Exception -> 0x1ba5 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x1ba5 }
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r6 = r10.delayedMessages     // Catch:{ Exception -> 0x1ba5 }
            java.lang.Object r3 = r6.get(r3)     // Catch:{ Exception -> 0x1ba5 }
            java.util.ArrayList r3 = (java.util.ArrayList) r3     // Catch:{ Exception -> 0x1ba5 }
            if (r3 == 0) goto L_0x0b6a
            r6 = 0
            java.lang.Object r3 = r3.get(r6)     // Catch:{ Exception -> 0x0adc }
            r29 = r3
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r29 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r29     // Catch:{ Exception -> 0x0adc }
        L_0x0b6a:
            if (r29 != 0) goto L_0x0b7e
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0adc }
            r3.<init>(r14)     // Catch:{ Exception -> 0x0adc }
            r3.initForGroup(r4)     // Catch:{ Exception -> 0x0adc }
            r3.encryptedChat = r9     // Catch:{ Exception -> 0x0adc }
            if (r13 == 0) goto L_0x0b7a
            r6 = 1
            goto L_0x0b7b
        L_0x0b7a:
            r6 = 0
        L_0x0b7b:
            r3.scheduled = r6     // Catch:{ Exception -> 0x0adc }
            goto L_0x0b80
        L_0x0b7e:
            r3 = r29
        L_0x0b80:
            r6 = 0
            r3.performMediaUpload = r6     // Catch:{ Exception -> 0x1ba5 }
            r6 = 0
            r3.photoSize = r6     // Catch:{ Exception -> 0x1ba5 }
            r3.videoEditedInfo = r6     // Catch:{ Exception -> 0x1ba5 }
            r3.httpLocation = r6     // Catch:{ Exception -> 0x1ba5 }
            if (r25 == 0) goto L_0x0b90
            int r7 = r12.id     // Catch:{ Exception -> 0x0adc }
            r3.finalGroupMessage = r7     // Catch:{ Exception -> 0x0adc }
        L_0x0b90:
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1ba5 }
            if (r7 == 0) goto L_0x0CLASSNAME
            r7 = r39
            if (r7 == 0) goto L_0x0bfd
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0adc }
            r11.<init>()     // Catch:{ Exception -> 0x0adc }
            java.lang.String r6 = "send message user_id = "
            r11.append(r6)     // Catch:{ Exception -> 0x0adc }
            int r6 = r7.user_id     // Catch:{ Exception -> 0x0adc }
            r11.append(r6)     // Catch:{ Exception -> 0x0adc }
            java.lang.String r6 = " chat_id = "
            r11.append(r6)     // Catch:{ Exception -> 0x0adc }
            int r6 = r7.chat_id     // Catch:{ Exception -> 0x0adc }
            r11.append(r6)     // Catch:{ Exception -> 0x0adc }
            java.lang.String r6 = " channel_id = "
            r11.append(r6)     // Catch:{ Exception -> 0x0adc }
            int r6 = r7.channel_id     // Catch:{ Exception -> 0x0adc }
            r11.append(r6)     // Catch:{ Exception -> 0x0adc }
            java.lang.String r6 = " access_hash = "
            r11.append(r6)     // Catch:{ Exception -> 0x0adc }
            r42 = r4
            long r4 = r7.access_hash     // Catch:{ Exception -> 0x0adc }
            r11.append(r4)     // Catch:{ Exception -> 0x0adc }
            java.lang.String r4 = " notify = "
            r11.append(r4)     // Catch:{ Exception -> 0x0adc }
            r4 = r76
            r11.append(r4)     // Catch:{ Exception -> 0x0adc }
            java.lang.String r4 = " silent = "
            r11.append(r4)     // Catch:{ Exception -> 0x0adc }
            int r4 = r10.currentAccount     // Catch:{ Exception -> 0x0adc }
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4)     // Catch:{ Exception -> 0x0adc }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0adc }
            r5.<init>()     // Catch:{ Exception -> 0x0adc }
            java.lang.String r6 = "silent_"
            r5.append(r6)     // Catch:{ Exception -> 0x0adc }
            r5.append(r14)     // Catch:{ Exception -> 0x0adc }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0adc }
            r6 = 0
            boolean r4 = r4.getBoolean(r5, r6)     // Catch:{ Exception -> 0x0adc }
            r11.append(r4)     // Catch:{ Exception -> 0x0adc }
            java.lang.String r4 = r11.toString()     // Catch:{ Exception -> 0x0adc }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0adc }
            goto L_0x0CLASSNAME
        L_0x0bfd:
            r42 = r4
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r42 = r4
            r7 = r39
        L_0x0CLASSNAME:
            if (r70 == 0) goto L_0x1a73
            r4 = r70
            r5 = 9
            if (r4 != r5) goto L_0x0CLASSNAME
            if (r56 == 0) goto L_0x0CLASSNAME
            if (r9 == 0) goto L_0x0CLASSNAME
            goto L_0x1a73
        L_0x0CLASSNAME:
            r5 = 1
            if (r4 < r5) goto L_0x0c1d
            r5 = 3
            if (r4 <= r5) goto L_0x0CLASSNAME
            goto L_0x0c1d
        L_0x0CLASSNAME:
            r5 = r72
            goto L_0x0d68
        L_0x0c1d:
            r5 = 5
            if (r4 < r5) goto L_0x0CLASSNAME
            r5 = 8
            if (r4 <= r5) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r5 = 9
            if (r4 != r5) goto L_0x0c2a
            if (r9 != 0) goto L_0x0CLASSNAME
        L_0x0c2a:
            r5 = 10
            if (r4 == r5) goto L_0x0CLASSNAME
            r5 = 11
            if (r4 != r5) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r5 = 4
            if (r4 != r5) goto L_0x0cde
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r3 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages     // Catch:{ Exception -> 0x0adc }
            r3.<init>()     // Catch:{ Exception -> 0x0adc }
            r3.to_peer = r7     // Catch:{ Exception -> 0x0adc }
            r5 = r72
            org.telegram.tgnet.TLRPC$Message r4 = r5.messageOwner     // Catch:{ Exception -> 0x0adc }
            boolean r6 = r4.with_my_score     // Catch:{ Exception -> 0x0adc }
            r3.with_my_score = r6     // Catch:{ Exception -> 0x0adc }
            int r4 = r4.ttl     // Catch:{ Exception -> 0x0adc }
            if (r4 == 0) goto L_0x0c6f
            org.telegram.messenger.MessagesController r4 = r55.getMessagesController()     // Catch:{ Exception -> 0x0adc }
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner     // Catch:{ Exception -> 0x0adc }
            int r6 = r6.ttl     // Catch:{ Exception -> 0x0adc }
            int r6 = -r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x0adc }
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r6)     // Catch:{ Exception -> 0x0adc }
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r6 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x0adc }
            r6.<init>()     // Catch:{ Exception -> 0x0adc }
            r3.from_peer = r6     // Catch:{ Exception -> 0x0adc }
            org.telegram.tgnet.TLRPC$Message r7 = r5.messageOwner     // Catch:{ Exception -> 0x0adc }
            int r7 = r7.ttl     // Catch:{ Exception -> 0x0adc }
            int r7 = -r7
            r6.channel_id = r7     // Catch:{ Exception -> 0x0adc }
            if (r4 == 0) goto L_0x0CLASSNAME
            long r7 = r4.access_hash     // Catch:{ Exception -> 0x0adc }
            r6.access_hash = r7     // Catch:{ Exception -> 0x0adc }
            goto L_0x0CLASSNAME
        L_0x0c6f:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0adc }
            r4.<init>()     // Catch:{ Exception -> 0x0adc }
            r3.from_peer = r4     // Catch:{ Exception -> 0x0adc }
        L_0x0CLASSNAME:
            boolean r4 = r12.silent     // Catch:{ Exception -> 0x0adc }
            r3.silent = r4     // Catch:{ Exception -> 0x0adc }
            if (r13 == 0) goto L_0x0CLASSNAME
            r3.schedule_date = r13     // Catch:{ Exception -> 0x0adc }
            int r4 = r3.flags     // Catch:{ Exception -> 0x0adc }
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r3.flags = r4     // Catch:{ Exception -> 0x0adc }
        L_0x0CLASSNAME:
            java.util.ArrayList<java.lang.Long> r4 = r3.random_id     // Catch:{ Exception -> 0x0adc }
            long r6 = r12.random_id     // Catch:{ Exception -> 0x0adc }
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x0adc }
            r4.add(r6)     // Catch:{ Exception -> 0x0adc }
            int r4 = r72.getId()     // Catch:{ Exception -> 0x0adc }
            if (r4 < 0) goto L_0x0ca3
            java.util.ArrayList<java.lang.Integer> r4 = r3.id     // Catch:{ Exception -> 0x0adc }
            int r5 = r72.getId()     // Catch:{ Exception -> 0x0adc }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0adc }
            r4.add(r5)     // Catch:{ Exception -> 0x0adc }
            goto L_0x0cc2
        L_0x0ca3:
            org.telegram.tgnet.TLRPC$Message r4 = r5.messageOwner     // Catch:{ Exception -> 0x0adc }
            int r5 = r4.fwd_msg_id     // Catch:{ Exception -> 0x0adc }
            if (r5 == 0) goto L_0x0cb3
            java.util.ArrayList<java.lang.Integer> r4 = r3.id     // Catch:{ Exception -> 0x0adc }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0adc }
            r4.add(r5)     // Catch:{ Exception -> 0x0adc }
            goto L_0x0cc2
        L_0x0cb3:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r4.fwd_from     // Catch:{ Exception -> 0x0adc }
            if (r4 == 0) goto L_0x0cc2
            java.util.ArrayList<java.lang.Integer> r5 = r3.id     // Catch:{ Exception -> 0x0adc }
            int r4 = r4.channel_post     // Catch:{ Exception -> 0x0adc }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0adc }
            r5.add(r4)     // Catch:{ Exception -> 0x0adc }
        L_0x0cc2:
            r4 = 0
            r5 = 0
            if (r13 == 0) goto L_0x0cc8
            r6 = 1
            goto L_0x0cc9
        L_0x0cc8:
            r6 = 0
        L_0x0cc9:
            r56 = r55
            r57 = r3
            r58 = r2
            r59 = r4
            r60 = r5
            r61 = r63
            r62 = r1
            r63 = r6
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x0adc }
            goto L_0x0d65
        L_0x0cde:
            r5 = r72
            r3 = 9
            if (r4 != r3) goto L_0x0d65
            org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult     // Catch:{ Exception -> 0x0adc }
            r3.<init>()     // Catch:{ Exception -> 0x0adc }
            r3.peer = r7     // Catch:{ Exception -> 0x0adc }
            long r6 = r12.random_id     // Catch:{ Exception -> 0x0adc }
            r3.random_id = r6     // Catch:{ Exception -> 0x0adc }
            r4 = r61
            boolean r4 = r1.containsKey(r4)     // Catch:{ Exception -> 0x0adc }
            if (r4 != 0) goto L_0x0cf9
            r4 = 1
            goto L_0x0cfa
        L_0x0cf9:
            r4 = 0
        L_0x0cfa:
            r3.hide_via = r4     // Catch:{ Exception -> 0x0adc }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = r12.reply_to     // Catch:{ Exception -> 0x0adc }
            if (r4 == 0) goto L_0x0d0c
            int r4 = r4.reply_to_msg_id     // Catch:{ Exception -> 0x0adc }
            if (r4 == 0) goto L_0x0d0c
            int r6 = r3.flags     // Catch:{ Exception -> 0x0adc }
            r7 = 1
            r6 = r6 | r7
            r3.flags = r6     // Catch:{ Exception -> 0x0adc }
            r3.reply_to_msg_id = r4     // Catch:{ Exception -> 0x0adc }
        L_0x0d0c:
            boolean r4 = r12.silent     // Catch:{ Exception -> 0x0adc }
            r3.silent = r4     // Catch:{ Exception -> 0x0adc }
            if (r13 == 0) goto L_0x0d1a
            r3.schedule_date = r13     // Catch:{ Exception -> 0x0adc }
            int r4 = r3.flags     // Catch:{ Exception -> 0x0adc }
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r3.flags = r4     // Catch:{ Exception -> 0x0adc }
        L_0x0d1a:
            r4 = r30
            java.lang.Object r4 = r1.get(r4)     // Catch:{ Exception -> 0x0adc }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0adc }
            java.lang.Long r4 = org.telegram.messenger.Utilities.parseLong(r4)     // Catch:{ Exception -> 0x0adc }
            long r6 = r4.longValue()     // Catch:{ Exception -> 0x0adc }
            r3.query_id = r6     // Catch:{ Exception -> 0x0adc }
            java.lang.String r4 = "id"
            java.lang.Object r4 = r1.get(r4)     // Catch:{ Exception -> 0x0adc }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0adc }
            r3.id = r4     // Catch:{ Exception -> 0x0adc }
            if (r5 != 0) goto L_0x0d4b
            r4 = 1
            r3.clear_draft = r4     // Catch:{ Exception -> 0x0adc }
            org.telegram.messenger.MediaDataController r4 = r55.getMediaDataController()     // Catch:{ Exception -> 0x0adc }
            if (r8 == 0) goto L_0x0d46
            int r5 = r69.getId()     // Catch:{ Exception -> 0x0adc }
            goto L_0x0d47
        L_0x0d46:
            r5 = 0
        L_0x0d47:
            r6 = 0
            r4.cleanDraft(r14, r5, r6)     // Catch:{ Exception -> 0x0adc }
        L_0x0d4b:
            r4 = 0
            r5 = 0
            if (r13 == 0) goto L_0x0d51
            r6 = 1
            goto L_0x0d52
        L_0x0d51:
            r6 = 0
        L_0x0d52:
            r56 = r55
            r57 = r3
            r58 = r2
            r59 = r4
            r60 = r5
            r61 = r63
            r62 = r1
            r63 = r6
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x0adc }
        L_0x0d65:
            r2 = r10
            goto L_0x1bf5
        L_0x0d68:
            if (r9 != 0) goto L_0x14a3
            r6 = 1
            if (r4 != r6) goto L_0x0dda
            r6 = r27
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x0adc }
            if (r5 == 0) goto L_0x0d8d
            org.telegram.tgnet.TLRPC$TL_inputMediaVenue r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue     // Catch:{ Exception -> 0x0adc }
            r5.<init>()     // Catch:{ Exception -> 0x0adc }
            java.lang.String r8 = r6.address     // Catch:{ Exception -> 0x0adc }
            r5.address = r8     // Catch:{ Exception -> 0x0adc }
            java.lang.String r8 = r6.title     // Catch:{ Exception -> 0x0adc }
            r5.title = r8     // Catch:{ Exception -> 0x0adc }
            java.lang.String r8 = r6.provider     // Catch:{ Exception -> 0x0adc }
            r5.provider = r8     // Catch:{ Exception -> 0x0adc }
            java.lang.String r8 = r6.venue_id     // Catch:{ Exception -> 0x0adc }
            r5.venue_id = r8     // Catch:{ Exception -> 0x0adc }
            r11 = r20
            r5.venue_type = r11     // Catch:{ Exception -> 0x0adc }
            goto L_0x0dbd
        L_0x0d8d:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive     // Catch:{ Exception -> 0x0adc }
            if (r5 == 0) goto L_0x0db8
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive     // Catch:{ Exception -> 0x0adc }
            r5.<init>()     // Catch:{ Exception -> 0x0adc }
            int r8 = r6.period     // Catch:{ Exception -> 0x0adc }
            r5.period = r8     // Catch:{ Exception -> 0x0adc }
            int r8 = r5.flags     // Catch:{ Exception -> 0x0adc }
            r9 = 2
            r8 = r8 | r9
            r5.flags = r8     // Catch:{ Exception -> 0x0adc }
            int r9 = r6.heading     // Catch:{ Exception -> 0x0adc }
            if (r9 == 0) goto L_0x0daa
            r5.heading = r9     // Catch:{ Exception -> 0x0adc }
            r9 = 4
            r8 = r8 | r9
            r5.flags = r8     // Catch:{ Exception -> 0x0adc }
        L_0x0daa:
            int r8 = r6.proximity_notification_radius     // Catch:{ Exception -> 0x0adc }
            if (r8 == 0) goto L_0x0dbd
            r5.proximity_notification_radius = r8     // Catch:{ Exception -> 0x0adc }
            int r8 = r5.flags     // Catch:{ Exception -> 0x0adc }
            r9 = 8
            r8 = r8 | r9
            r5.flags = r8     // Catch:{ Exception -> 0x0adc }
            goto L_0x0dbd
        L_0x0db8:
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint     // Catch:{ Exception -> 0x0adc }
            r5.<init>()     // Catch:{ Exception -> 0x0adc }
        L_0x0dbd:
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r8 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint     // Catch:{ Exception -> 0x0adc }
            r8.<init>()     // Catch:{ Exception -> 0x0adc }
            r5.geo_point = r8     // Catch:{ Exception -> 0x0adc }
            org.telegram.tgnet.TLRPC$GeoPoint r6 = r6.geo     // Catch:{ Exception -> 0x0adc }
            double r14 = r6.lat     // Catch:{ Exception -> 0x0adc }
            r8.lat = r14     // Catch:{ Exception -> 0x0adc }
            double r14 = r6._long     // Catch:{ Exception -> 0x0adc }
            r8._long = r14     // Catch:{ Exception -> 0x0adc }
            r18 = r63
            r64 = r1
            r70 = r4
            r39 = r7
            r19 = r12
            goto L_0x108a
        L_0x0dda:
            r11 = r20
            r5 = 2
            if (r4 == r5) goto L_0x11d3
            r5 = 9
            if (r4 != r5) goto L_0x0df1
            r5 = r44
            if (r5 == 0) goto L_0x0df1
            r9 = r63
            r70 = r4
            r39 = r7
            r19 = r12
            goto L_0x11dd
        L_0x0df1:
            java.lang.String r5 = "query"
            r6 = 3
            if (r4 != r6) goto L_0x0f1b
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0var_ }
            r6.<init>()     // Catch:{ Exception -> 0x0var_ }
            r8 = r19
            java.lang.String r9 = r8.mime_type     // Catch:{ Exception -> 0x0var_ }
            r6.mime_type = r9     // Catch:{ Exception -> 0x0var_ }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r9 = r8.attributes     // Catch:{ Exception -> 0x0var_ }
            r6.attributes = r9     // Catch:{ Exception -> 0x0var_ }
            if (r32 != 0) goto L_0x0e1d
            boolean r9 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r8)     // Catch:{ Exception -> 0x0adc }
            if (r9 != 0) goto L_0x0e1a
            if (r58 == 0) goto L_0x0e1d
            r9 = r58
            boolean r11 = r9.muted     // Catch:{ Exception -> 0x0adc }
            if (r11 != 0) goto L_0x0e2b
            boolean r11 = r9.roundVideo     // Catch:{ Exception -> 0x0adc }
            if (r11 != 0) goto L_0x0e2b
            goto L_0x0e1f
        L_0x0e1a:
            r9 = r58
            goto L_0x0e2b
        L_0x0e1d:
            r9 = r58
        L_0x0e1f:
            r11 = 1
            r6.nosound_video = r11     // Catch:{ Exception -> 0x0var_ }
            boolean r11 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0var_ }
            if (r11 == 0) goto L_0x0e2b
            java.lang.String r11 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r11)     // Catch:{ Exception -> 0x0adc }
        L_0x0e2b:
            if (r57 == 0) goto L_0x0e3b
            r11 = r57
            r6.ttl_seconds = r11     // Catch:{ Exception -> 0x0adc }
            r12.ttl = r11     // Catch:{ Exception -> 0x0adc }
            int r11 = r6.flags     // Catch:{ Exception -> 0x0adc }
            r18 = 2
            r11 = r11 | 2
            r6.flags = r11     // Catch:{ Exception -> 0x0adc }
        L_0x0e3b:
            if (r1 == 0) goto L_0x0e86
            java.lang.String r11 = "masks"
            java.lang.Object r11 = r1.get(r11)     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r11 = (java.lang.String) r11     // Catch:{ Exception -> 0x0var_ }
            if (r11 == 0) goto L_0x0e86
            r39 = r7
            org.telegram.tgnet.SerializedData r7 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0var_ }
            byte[] r11 = org.telegram.messenger.Utilities.hexToBytes(r11)     // Catch:{ Exception -> 0x0var_ }
            r7.<init>((byte[]) r11)     // Catch:{ Exception -> 0x0var_ }
            r19 = r12
            r11 = 0
            int r12 = r7.readInt32(r11)     // Catch:{ Exception -> 0x0var_ }
        L_0x0e59:
            if (r11 >= r12) goto L_0x0e78
            r56 = r12
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r12 = r6.stickers     // Catch:{ Exception -> 0x0var_ }
            r70 = r4
            r58 = r9
            r4 = 0
            int r9 = r7.readInt32(r4)     // Catch:{ Exception -> 0x0var_ }
            org.telegram.tgnet.TLRPC$InputDocument r9 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r7, r9, r4)     // Catch:{ Exception -> 0x0var_ }
            r12.add(r9)     // Catch:{ Exception -> 0x0var_ }
            int r11 = r11 + 1
            r12 = r56
            r9 = r58
            r4 = r70
            goto L_0x0e59
        L_0x0e78:
            r70 = r4
            r58 = r9
            int r4 = r6.flags     // Catch:{ Exception -> 0x0var_ }
            r9 = 1
            r4 = r4 | r9
            r6.flags = r4     // Catch:{ Exception -> 0x0var_ }
            r7.cleanup()     // Catch:{ Exception -> 0x0var_ }
            goto L_0x0e8e
        L_0x0e86:
            r70 = r4
            r39 = r7
            r58 = r9
            r19 = r12
        L_0x0e8e:
            long r11 = r8.access_hash     // Catch:{ Exception -> 0x0var_ }
            int r4 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x0e97
            r5 = r6
            r4 = 1
            goto L_0x0ece
        L_0x0e97:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0var_ }
            r4.<init>()     // Catch:{ Exception -> 0x0var_ }
            org.telegram.tgnet.TLRPC$TL_inputDocument r7 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0var_ }
            r7.<init>()     // Catch:{ Exception -> 0x0var_ }
            r4.id = r7     // Catch:{ Exception -> 0x0var_ }
            long r11 = r8.id     // Catch:{ Exception -> 0x0var_ }
            r7.id = r11     // Catch:{ Exception -> 0x0var_ }
            long r11 = r8.access_hash     // Catch:{ Exception -> 0x0var_ }
            r7.access_hash = r11     // Catch:{ Exception -> 0x0var_ }
            byte[] r9 = r8.file_reference     // Catch:{ Exception -> 0x0var_ }
            r7.file_reference = r9     // Catch:{ Exception -> 0x0var_ }
            if (r9 != 0) goto L_0x0eb6
            r9 = 0
            byte[] r11 = new byte[r9]     // Catch:{ Exception -> 0x0var_ }
            r7.file_reference = r11     // Catch:{ Exception -> 0x0var_ }
        L_0x0eb6:
            if (r1 == 0) goto L_0x0ecc
            boolean r7 = r1.containsKey(r5)     // Catch:{ Exception -> 0x0var_ }
            if (r7 == 0) goto L_0x0ecc
            java.lang.Object r5 = r1.get(r5)     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x0var_ }
            r4.query = r5     // Catch:{ Exception -> 0x0var_ }
            int r5 = r4.flags     // Catch:{ Exception -> 0x0var_ }
            r7 = 2
            r5 = r5 | r7
            r4.flags = r5     // Catch:{ Exception -> 0x0var_ }
        L_0x0ecc:
            r5 = r4
            r4 = 0
        L_0x0ece:
            if (r3 != 0) goto L_0x0eea
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0var_ }
            r3.<init>(r14)     // Catch:{ Exception -> 0x0var_ }
            r7 = 1
            r3.type = r7     // Catch:{ Exception -> 0x0var_ }
            r3.obj = r2     // Catch:{ Exception -> 0x0var_ }
            r7 = r46
            r3.originalPath = r7     // Catch:{ Exception -> 0x0var_ }
            r12 = r63
            r3.parentObject = r12     // Catch:{ Exception -> 0x0var_ }
            if (r13 == 0) goto L_0x0ee6
            r9 = 1
            goto L_0x0ee7
        L_0x0ee6:
            r9 = 0
        L_0x0ee7:
            r3.scheduled = r9     // Catch:{ Exception -> 0x0var_ }
            goto L_0x0eee
        L_0x0eea:
            r12 = r63
            r7 = r46
        L_0x0eee:
            r3.inputUploadMedia = r6     // Catch:{ Exception -> 0x0var_ }
            r3.performMediaUpload = r4     // Catch:{ Exception -> 0x0var_ }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r8.thumbs     // Catch:{ Exception -> 0x0var_ }
            boolean r6 = r6.isEmpty()     // Catch:{ Exception -> 0x0var_ }
            if (r6 != 0) goto L_0x0var_
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r8.thumbs     // Catch:{ Exception -> 0x0var_ }
            r9 = 0
            java.lang.Object r6 = r6.get(r9)     // Catch:{ Exception -> 0x0var_ }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC$PhotoSize) r6     // Catch:{ Exception -> 0x0var_ }
            r3.photoSize = r6     // Catch:{ Exception -> 0x0var_ }
            r3.locationParent = r8     // Catch:{ Exception -> 0x0var_ }
        L_0x0var_:
            r6 = r58
            r3.videoEditedInfo = r6     // Catch:{ Exception -> 0x0var_ }
            r64 = r1
            r18 = r12
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r19 = r12
        L_0x0var_:
            r1 = r0
            r7 = r2
            r2 = r10
        L_0x0var_:
            r11 = r13
        L_0x0var_:
            r5 = r19
            goto L_0x1bc5
        L_0x0f1b:
            r6 = r4
            r39 = r7
            r8 = r19
            r7 = r46
            r9 = 6
            r4 = r57
            r19 = r12
            r12 = r63
            if (r6 != r9) goto L_0x0f7a
            org.telegram.tgnet.TLRPC$TL_inputMediaContact r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact     // Catch:{ Exception -> 0x0var_ }
            r5.<init>()     // Catch:{ Exception -> 0x0var_ }
            r4 = r18
            java.lang.String r8 = r4.phone     // Catch:{ Exception -> 0x0var_ }
            r5.phone_number = r8     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r8 = r4.first_name     // Catch:{ Exception -> 0x0var_ }
            r5.first_name = r8     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r8 = r4.last_name     // Catch:{ Exception -> 0x0var_ }
            r5.last_name = r8     // Catch:{ Exception -> 0x0var_ }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r8 = r4.restriction_reason     // Catch:{ Exception -> 0x0var_ }
            boolean r8 = r8.isEmpty()     // Catch:{ Exception -> 0x0var_ }
            if (r8 != 0) goto L_0x0var_
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r8 = r4.restriction_reason     // Catch:{ Exception -> 0x0var_ }
            r9 = 0
            java.lang.Object r8 = r8.get(r9)     // Catch:{ Exception -> 0x0var_ }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r8 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r8     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r8 = r8.text     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r9 = "BEGIN:VCARD"
            boolean r8 = r8.startsWith(r9)     // Catch:{ Exception -> 0x0var_ }
            if (r8 == 0) goto L_0x0var_
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r4 = r4.restriction_reason     // Catch:{ Exception -> 0x0var_ }
            r8 = 0
            java.lang.Object r4 = r4.get(r8)     // Catch:{ Exception -> 0x0var_ }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r4 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r4     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r4 = r4.text     // Catch:{ Exception -> 0x0var_ }
            r5.vcard = r4     // Catch:{ Exception -> 0x0var_ }
            goto L_0x0var_
        L_0x0var_:
            r5.vcard = r11     // Catch:{ Exception -> 0x0var_ }
        L_0x0var_:
            r64 = r1
            r70 = r6
            r18 = r12
            r4 = 0
        L_0x0var_:
            r54 = r7
            r7 = r2
            r2 = r10
            r10 = r54
            goto L_0x12c0
        L_0x0var_:
            r0 = move-exception
            goto L_0x0var_
        L_0x0f7a:
            r9 = 7
            if (r6 == r9) goto L_0x109c
            r9 = 9
            if (r6 != r9) goto L_0x0var_
            goto L_0x109c
        L_0x0var_:
            r9 = 8
            if (r6 != r9) goto L_0x1010
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x100b }
            r3.<init>()     // Catch:{ Exception -> 0x100b }
            java.lang.String r9 = r8.mime_type     // Catch:{ Exception -> 0x100b }
            r3.mime_type = r9     // Catch:{ Exception -> 0x100b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r9 = r8.attributes     // Catch:{ Exception -> 0x100b }
            r3.attributes = r9     // Catch:{ Exception -> 0x100b }
            if (r4 == 0) goto L_0x0fa3
            r3.ttl_seconds = r4     // Catch:{ Exception -> 0x100b }
            r9 = r19
            r9.ttl = r4     // Catch:{ Exception -> 0x10b7 }
            int r4 = r3.flags     // Catch:{ Exception -> 0x10b7 }
            r11 = 2
            r4 = r4 | r11
            r3.flags = r4     // Catch:{ Exception -> 0x10b7 }
            goto L_0x0fa5
        L_0x0fa3:
            r9 = r19
        L_0x0fa5:
            r70 = r6
            r46 = r7
            long r6 = r8.access_hash     // Catch:{ Exception -> 0x10b7 }
            int r4 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x0fb3
            r5 = r3
            r7 = r12
            r4 = 1
            goto L_0x0feb
        L_0x0fb3:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x10b7 }
            r4.<init>()     // Catch:{ Exception -> 0x10b7 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x10b7 }
            r6.<init>()     // Catch:{ Exception -> 0x10b7 }
            r4.id = r6     // Catch:{ Exception -> 0x10b7 }
            r7 = r12
            long r11 = r8.id     // Catch:{ Exception -> 0x10b7 }
            r6.id = r11     // Catch:{ Exception -> 0x10b7 }
            long r11 = r8.access_hash     // Catch:{ Exception -> 0x10b7 }
            r6.access_hash = r11     // Catch:{ Exception -> 0x10b7 }
            byte[] r8 = r8.file_reference     // Catch:{ Exception -> 0x10b7 }
            r6.file_reference = r8     // Catch:{ Exception -> 0x10b7 }
            if (r8 != 0) goto L_0x0fd3
            r8 = 0
            byte[] r11 = new byte[r8]     // Catch:{ Exception -> 0x10b7 }
            r6.file_reference = r11     // Catch:{ Exception -> 0x10b7 }
        L_0x0fd3:
            if (r1 == 0) goto L_0x0fe9
            boolean r6 = r1.containsKey(r5)     // Catch:{ Exception -> 0x10b7 }
            if (r6 == 0) goto L_0x0fe9
            java.lang.Object r5 = r1.get(r5)     // Catch:{ Exception -> 0x10b7 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x10b7 }
            r4.query = r5     // Catch:{ Exception -> 0x10b7 }
            int r5 = r4.flags     // Catch:{ Exception -> 0x10b7 }
            r6 = 2
            r5 = r5 | r6
            r4.flags = r5     // Catch:{ Exception -> 0x10b7 }
        L_0x0fe9:
            r5 = r4
            r4 = 0
        L_0x0feb:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x10b7 }
            r6.<init>(r14)     // Catch:{ Exception -> 0x10b7 }
            r8 = 3
            r6.type = r8     // Catch:{ Exception -> 0x10b7 }
            r6.obj = r2     // Catch:{ Exception -> 0x10b7 }
            r6.parentObject = r7     // Catch:{ Exception -> 0x10b7 }
            r6.inputUploadMedia = r3     // Catch:{ Exception -> 0x10b7 }
            r6.performMediaUpload = r4     // Catch:{ Exception -> 0x10b7 }
            if (r13 == 0) goto L_0x0fff
            r3 = 1
            goto L_0x1000
        L_0x0fff:
            r3 = 0
        L_0x1000:
            r6.scheduled = r3     // Catch:{ Exception -> 0x10b7 }
            r64 = r1
            r3 = r6
            r18 = r7
            r19 = r9
            goto L_0x1096
        L_0x100b:
            r0 = move-exception
            r9 = r19
            goto L_0x10b8
        L_0x1010:
            r46 = r7
            r7 = r12
            r9 = r19
            r4 = 10
            if (r6 != r4) goto L_0x1075
            org.telegram.tgnet.TLRPC$TL_inputMediaPoll r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll     // Catch:{ Exception -> 0x10b7 }
            r5.<init>()     // Catch:{ Exception -> 0x10b7 }
            r8 = r36
            org.telegram.tgnet.TLRPC$Poll r4 = r8.poll     // Catch:{ Exception -> 0x10b7 }
            r5.poll = r4     // Catch:{ Exception -> 0x10b7 }
            if (r1 == 0) goto L_0x1058
            java.lang.String r4 = "answers"
            boolean r4 = r1.containsKey(r4)     // Catch:{ Exception -> 0x10b7 }
            if (r4 == 0) goto L_0x1058
            java.lang.String r4 = "answers"
            java.lang.Object r4 = r1.get(r4)     // Catch:{ Exception -> 0x10b7 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x10b7 }
            byte[] r4 = org.telegram.messenger.Utilities.hexToBytes(r4)     // Catch:{ Exception -> 0x10b7 }
            int r11 = r4.length     // Catch:{ Exception -> 0x10b7 }
            if (r11 <= 0) goto L_0x1058
            r11 = 0
        L_0x103e:
            int r12 = r4.length     // Catch:{ Exception -> 0x10b7 }
            if (r11 >= r12) goto L_0x1052
            java.util.ArrayList<byte[]> r12 = r5.correct_answers     // Catch:{ Exception -> 0x10b7 }
            r14 = 1
            byte[] r15 = new byte[r14]     // Catch:{ Exception -> 0x10b7 }
            byte r14 = r4[r11]     // Catch:{ Exception -> 0x10b7 }
            r18 = 0
            r15[r18] = r14     // Catch:{ Exception -> 0x10b7 }
            r12.add(r15)     // Catch:{ Exception -> 0x10b7 }
            int r11 = r11 + 1
            goto L_0x103e
        L_0x1052:
            int r4 = r5.flags     // Catch:{ Exception -> 0x10b7 }
            r11 = 1
            r4 = r4 | r11
            r5.flags = r4     // Catch:{ Exception -> 0x10b7 }
        L_0x1058:
            org.telegram.tgnet.TLRPC$PollResults r4 = r8.results     // Catch:{ Exception -> 0x10b7 }
            if (r4 == 0) goto L_0x1082
            java.lang.String r4 = r4.solution     // Catch:{ Exception -> 0x10b7 }
            boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x10b7 }
            if (r4 != 0) goto L_0x1082
            org.telegram.tgnet.TLRPC$PollResults r4 = r8.results     // Catch:{ Exception -> 0x10b7 }
            java.lang.String r8 = r4.solution     // Catch:{ Exception -> 0x10b7 }
            r5.solution = r8     // Catch:{ Exception -> 0x10b7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r4.solution_entities     // Catch:{ Exception -> 0x10b7 }
            r5.solution_entities = r4     // Catch:{ Exception -> 0x10b7 }
            int r4 = r5.flags     // Catch:{ Exception -> 0x10b7 }
            r8 = 2
            r4 = r4 | r8
            r5.flags = r4     // Catch:{ Exception -> 0x10b7 }
            goto L_0x1082
        L_0x1075:
            r4 = 11
            if (r6 != r4) goto L_0x108c
            org.telegram.tgnet.TLRPC$TL_inputMediaDice r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDice     // Catch:{ Exception -> 0x10b7 }
            r5.<init>()     // Catch:{ Exception -> 0x10b7 }
            r4 = r56
            r5.emoticon = r4     // Catch:{ Exception -> 0x10b7 }
        L_0x1082:
            r64 = r1
            r70 = r6
            r18 = r7
            r19 = r9
        L_0x108a:
            r4 = 0
            goto L_0x1096
        L_0x108c:
            r64 = r1
            r70 = r6
            r18 = r7
            r19 = r9
            r4 = 0
            r5 = 0
        L_0x1096:
            r7 = r2
            r2 = r10
            r10 = r46
            goto L_0x12c0
        L_0x109c:
            r46 = r7
            r7 = r12
            r9 = r19
            r12 = r1
            if (r46 != 0) goto L_0x10c3
            r1 = r67
            if (r1 != 0) goto L_0x10be
            r70 = r6
            r18 = r7
            long r6 = r8.access_hash     // Catch:{ Exception -> 0x10b7 }
            int r11 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r11 != 0) goto L_0x10b3
            goto L_0x10c9
        L_0x10b3:
            r40 = r2
            r6 = 0
            goto L_0x1114
        L_0x10b7:
            r0 = move-exception
        L_0x10b8:
            r1 = r0
            r7 = r2
            r5 = r9
            r2 = r10
            goto L_0x0ae1
        L_0x10be:
            r70 = r6
            r18 = r7
            goto L_0x10c9
        L_0x10c3:
            r70 = r6
            r18 = r7
            r1 = r67
        L_0x10c9:
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x11cc }
            r6.<init>()     // Catch:{ Exception -> 0x11cc }
            if (r4 == 0) goto L_0x10da
            r6.ttl_seconds = r4     // Catch:{ Exception -> 0x10b7 }
            r9.ttl = r4     // Catch:{ Exception -> 0x10b7 }
            int r4 = r6.flags     // Catch:{ Exception -> 0x10b7 }
            r7 = 2
            r4 = r4 | r7
            r6.flags = r4     // Catch:{ Exception -> 0x10b7 }
        L_0x10da:
            if (r32 != 0) goto L_0x10f8
            boolean r4 = android.text.TextUtils.isEmpty(r67)     // Catch:{ Exception -> 0x10b7 }
            if (r4 != 0) goto L_0x10fb
            java.lang.String r1 = r67.toLowerCase()     // Catch:{ Exception -> 0x10b7 }
            java.lang.String r4 = "mp4"
            boolean r1 = r1.endsWith(r4)     // Catch:{ Exception -> 0x10b7 }
            if (r1 == 0) goto L_0x10fb
            if (r12 == 0) goto L_0x10f8
            java.lang.String r1 = "forceDocument"
            boolean r1 = r12.containsKey(r1)     // Catch:{ Exception -> 0x10b7 }
            if (r1 == 0) goto L_0x10fb
        L_0x10f8:
            r1 = 1
            r6.nosound_video = r1     // Catch:{ Exception -> 0x11cc }
        L_0x10fb:
            if (r12 == 0) goto L_0x1107
            java.lang.String r1 = "forceDocument"
            boolean r1 = r12.containsKey(r1)     // Catch:{ Exception -> 0x10b7 }
            if (r1 == 0) goto L_0x1107
            r1 = 1
            goto L_0x1108
        L_0x1107:
            r1 = 0
        L_0x1108:
            r6.force_file = r1     // Catch:{ Exception -> 0x11cc }
            java.lang.String r1 = r8.mime_type     // Catch:{ Exception -> 0x11cc }
            r6.mime_type = r1     // Catch:{ Exception -> 0x11cc }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r8.attributes     // Catch:{ Exception -> 0x11cc }
            r6.attributes = r1     // Catch:{ Exception -> 0x11cc }
            r40 = r2
        L_0x1114:
            long r1 = r8.access_hash     // Catch:{ Exception -> 0x11c4 }
            int r4 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x1129
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x1120 }
            r5 = r6
            r19 = r9
            goto L_0x116b
        L_0x1120:
            r0 = move-exception
            r1 = r0
            r5 = r9
            r2 = r10
            r11 = r13
        L_0x1125:
            r7 = r40
            goto L_0x1bc5
        L_0x1129:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x11c4 }
            r1.<init>()     // Catch:{ Exception -> 0x11c4 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x11c4 }
            r2.<init>()     // Catch:{ Exception -> 0x11c4 }
            r1.id = r2     // Catch:{ Exception -> 0x11c4 }
            r19 = r9
            long r9 = r8.id     // Catch:{ Exception -> 0x118f }
            r2.id = r9     // Catch:{ Exception -> 0x118f }
            long r9 = r8.access_hash     // Catch:{ Exception -> 0x118f }
            r2.access_hash = r9     // Catch:{ Exception -> 0x118f }
            byte[] r4 = r8.file_reference     // Catch:{ Exception -> 0x118f }
            r2.file_reference = r4     // Catch:{ Exception -> 0x118f }
            if (r4 != 0) goto L_0x1153
            r4 = 0
            byte[] r7 = new byte[r4]     // Catch:{ Exception -> 0x114b }
            r2.file_reference = r7     // Catch:{ Exception -> 0x114b }
            goto L_0x1153
        L_0x114b:
            r0 = move-exception
            r2 = r55
            r1 = r0
            r11 = r13
            r5 = r19
            goto L_0x1125
        L_0x1153:
            if (r12 == 0) goto L_0x1169
            boolean r2 = r12.containsKey(r5)     // Catch:{ Exception -> 0x114b }
            if (r2 == 0) goto L_0x1169
            java.lang.Object r2 = r12.get(r5)     // Catch:{ Exception -> 0x114b }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x114b }
            r1.query = r2     // Catch:{ Exception -> 0x114b }
            int r2 = r1.flags     // Catch:{ Exception -> 0x114b }
            r4 = 2
            r2 = r2 | r4
            r1.flags = r2     // Catch:{ Exception -> 0x114b }
        L_0x1169:
            r5 = r1
            r1 = 0
        L_0x116b:
            if (r6 == 0) goto L_0x11b5
            if (r3 != 0) goto L_0x1193
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x118f }
            r2 = r55
            r3.<init>(r14)     // Catch:{ Exception -> 0x118d }
            r4 = 2
            r3.type = r4     // Catch:{ Exception -> 0x118d }
            r7 = r40
            r3.obj = r7     // Catch:{ Exception -> 0x1293 }
            r10 = r46
            r3.originalPath = r10     // Catch:{ Exception -> 0x1293 }
            r9 = r18
            r3.parentObject = r9     // Catch:{ Exception -> 0x1293 }
            if (r13 == 0) goto L_0x1189
            r4 = 1
            goto L_0x118a
        L_0x1189:
            r4 = 0
        L_0x118a:
            r3.scheduled = r4     // Catch:{ Exception -> 0x1293 }
            goto L_0x119b
        L_0x118d:
            r0 = move-exception
            goto L_0x11c8
        L_0x118f:
            r0 = move-exception
            r2 = r55
            goto L_0x11c8
        L_0x1193:
            r2 = r55
            r9 = r18
            r7 = r40
            r10 = r46
        L_0x119b:
            r3.inputUploadMedia = r6     // Catch:{ Exception -> 0x1293 }
            r3.performMediaUpload = r1     // Catch:{ Exception -> 0x1293 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r8.thumbs     // Catch:{ Exception -> 0x1293 }
            boolean r4 = r4.isEmpty()     // Catch:{ Exception -> 0x1293 }
            if (r4 != 0) goto L_0x11bd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r8.thumbs     // Catch:{ Exception -> 0x1293 }
            r6 = 0
            java.lang.Object r4 = r4.get(r6)     // Catch:{ Exception -> 0x1293 }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = (org.telegram.tgnet.TLRPC$PhotoSize) r4     // Catch:{ Exception -> 0x1293 }
            r3.photoSize = r4     // Catch:{ Exception -> 0x1293 }
            r3.locationParent = r8     // Catch:{ Exception -> 0x1293 }
            goto L_0x11bd
        L_0x11b5:
            r2 = r55
            r9 = r18
            r7 = r40
            r10 = r46
        L_0x11bd:
            r4 = r1
            r18 = r9
            r64 = r12
            goto L_0x12c0
        L_0x11c4:
            r0 = move-exception
            r19 = r9
            r2 = r10
        L_0x11c8:
            r7 = r40
            goto L_0x124b
        L_0x11cc:
            r0 = move-exception
            r7 = r2
            r19 = r9
            r2 = r10
            goto L_0x124b
        L_0x11d3:
            r9 = r63
            r70 = r4
            r39 = r7
            r19 = r12
            r5 = r44
        L_0x11dd:
            r4 = r57
            r12 = r1
            r7 = r2
            r2 = r10
            r10 = r46
            r1 = r67
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x149c }
            r6.<init>()     // Catch:{ Exception -> 0x149c }
            if (r4 == 0) goto L_0x11fd
            r6.ttl_seconds = r4     // Catch:{ Exception -> 0x149c }
            r8 = r19
            r8.ttl = r4     // Catch:{ Exception -> 0x11fa }
            int r4 = r6.flags     // Catch:{ Exception -> 0x11fa }
            r11 = 2
            r4 = r4 | r11
            r6.flags = r4     // Catch:{ Exception -> 0x11fa }
            goto L_0x11ff
        L_0x11fa:
            r0 = move-exception
            goto L_0x149f
        L_0x11fd:
            r8 = r19
        L_0x11ff:
            if (r12 == 0) goto L_0x124e
            java.lang.String r4 = "masks"
            java.lang.Object r4 = r12.get(r4)     // Catch:{ Exception -> 0x1248 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x1248 }
            if (r4 == 0) goto L_0x124e
            org.telegram.tgnet.SerializedData r11 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x1248 }
            byte[] r4 = org.telegram.messenger.Utilities.hexToBytes(r4)     // Catch:{ Exception -> 0x1248 }
            r11.<init>((byte[]) r4)     // Catch:{ Exception -> 0x1248 }
            r64 = r12
            r4 = 0
            int r12 = r11.readInt32(r4)     // Catch:{ Exception -> 0x1248 }
        L_0x121b:
            if (r4 >= r12) goto L_0x123a
            r56 = r12
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r12 = r6.stickers     // Catch:{ Exception -> 0x1248 }
            r19 = r8
            r18 = r9
            r9 = 0
            int r8 = r11.readInt32(r9)     // Catch:{ Exception -> 0x1293 }
            org.telegram.tgnet.TLRPC$InputDocument r8 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r11, r8, r9)     // Catch:{ Exception -> 0x1293 }
            r12.add(r8)     // Catch:{ Exception -> 0x1293 }
            int r4 = r4 + 1
            r12 = r56
            r9 = r18
            r8 = r19
            goto L_0x121b
        L_0x123a:
            r19 = r8
            r18 = r9
            int r4 = r6.flags     // Catch:{ Exception -> 0x1293 }
            r8 = 1
            r4 = r4 | r8
            r6.flags = r4     // Catch:{ Exception -> 0x1293 }
            r11.cleanup()     // Catch:{ Exception -> 0x1293 }
            goto L_0x1254
        L_0x1248:
            r0 = move-exception
            r19 = r8
        L_0x124b:
            r1 = r0
            goto L_0x0var_
        L_0x124e:
            r19 = r8
            r18 = r9
            r64 = r12
        L_0x1254:
            long r8 = r5.access_hash     // Catch:{ Exception -> 0x149c }
            int r4 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x125d
            r4 = r6
            r8 = 1
            goto L_0x127d
        L_0x125d:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x149c }
            r4.<init>()     // Catch:{ Exception -> 0x149c }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r8 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x149c }
            r8.<init>()     // Catch:{ Exception -> 0x149c }
            r4.id = r8     // Catch:{ Exception -> 0x149c }
            long r11 = r5.id     // Catch:{ Exception -> 0x149c }
            r8.id = r11     // Catch:{ Exception -> 0x149c }
            long r11 = r5.access_hash     // Catch:{ Exception -> 0x149c }
            r8.access_hash = r11     // Catch:{ Exception -> 0x149c }
            byte[] r9 = r5.file_reference     // Catch:{ Exception -> 0x149c }
            r8.file_reference = r9     // Catch:{ Exception -> 0x149c }
            if (r9 != 0) goto L_0x127c
            r9 = 0
            byte[] r11 = new byte[r9]     // Catch:{ Exception -> 0x1293 }
            r8.file_reference = r11     // Catch:{ Exception -> 0x1293 }
        L_0x127c:
            r8 = 0
        L_0x127d:
            if (r3 != 0) goto L_0x1295
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1293 }
            r3.<init>(r14)     // Catch:{ Exception -> 0x1293 }
            r9 = 0
            r3.type = r9     // Catch:{ Exception -> 0x1293 }
            r3.obj = r7     // Catch:{ Exception -> 0x1293 }
            r3.originalPath = r10     // Catch:{ Exception -> 0x1293 }
            if (r13 == 0) goto L_0x128f
            r9 = 1
            goto L_0x1290
        L_0x128f:
            r9 = 0
        L_0x1290:
            r3.scheduled = r9     // Catch:{ Exception -> 0x1293 }
            goto L_0x1295
        L_0x1293:
            r0 = move-exception
            goto L_0x124b
        L_0x1295:
            r3.inputUploadMedia = r6     // Catch:{ Exception -> 0x149c }
            r3.performMediaUpload = r8     // Catch:{ Exception -> 0x149c }
            if (r1 == 0) goto L_0x12ac
            int r6 = r67.length()     // Catch:{ Exception -> 0x1293 }
            if (r6 <= 0) goto L_0x12ac
            r12 = r45
            boolean r6 = r1.startsWith(r12)     // Catch:{ Exception -> 0x1293 }
            if (r6 == 0) goto L_0x12ac
            r3.httpLocation = r1     // Catch:{ Exception -> 0x1293 }
            goto L_0x12be
        L_0x12ac:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r5.sizes     // Catch:{ Exception -> 0x149c }
            int r6 = r1.size()     // Catch:{ Exception -> 0x149c }
            r9 = 1
            int r6 = r6 - r9
            java.lang.Object r1 = r1.get(r6)     // Catch:{ Exception -> 0x149c }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = (org.telegram.tgnet.TLRPC$PhotoSize) r1     // Catch:{ Exception -> 0x149c }
            r3.photoSize = r1     // Catch:{ Exception -> 0x149c }
            r3.locationParent = r5     // Catch:{ Exception -> 0x149c }
        L_0x12be:
            r5 = r4
            r4 = r8
        L_0x12c0:
            int r1 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1351
            org.telegram.tgnet.TLObject r1 = r3.sendRequest     // Catch:{ Exception -> 0x149c }
            if (r1 == 0) goto L_0x12cd
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r1     // Catch:{ Exception -> 0x1293 }
            r8 = r19
            goto L_0x12f8
        L_0x12cd:
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia     // Catch:{ Exception -> 0x149c }
            r1.<init>()     // Catch:{ Exception -> 0x149c }
            r6 = r39
            r1.peer = r6     // Catch:{ Exception -> 0x149c }
            r8 = r19
            boolean r6 = r8.silent     // Catch:{ Exception -> 0x11fa }
            r1.silent = r6     // Catch:{ Exception -> 0x11fa }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r6 = r8.reply_to     // Catch:{ Exception -> 0x11fa }
            if (r6 == 0) goto L_0x12ec
            int r6 = r6.reply_to_msg_id     // Catch:{ Exception -> 0x11fa }
            if (r6 == 0) goto L_0x12ec
            int r9 = r1.flags     // Catch:{ Exception -> 0x11fa }
            r11 = 1
            r9 = r9 | r11
            r1.flags = r9     // Catch:{ Exception -> 0x11fa }
            r1.reply_to_msg_id = r6     // Catch:{ Exception -> 0x11fa }
        L_0x12ec:
            if (r13 == 0) goto L_0x12f6
            r1.schedule_date = r13     // Catch:{ Exception -> 0x11fa }
            int r6 = r1.flags     // Catch:{ Exception -> 0x11fa }
            r6 = r6 | 1024(0x400, float:1.435E-42)
            r1.flags = r6     // Catch:{ Exception -> 0x11fa }
        L_0x12f6:
            r3.sendRequest = r1     // Catch:{ Exception -> 0x11fa }
        L_0x12f8:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r3.messageObjects     // Catch:{ Exception -> 0x11fa }
            r6.add(r7)     // Catch:{ Exception -> 0x11fa }
            java.util.ArrayList<java.lang.Object> r6 = r3.parentObjects     // Catch:{ Exception -> 0x11fa }
            r9 = r18
            r6.add(r9)     // Catch:{ Exception -> 0x11fa }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r3.locations     // Catch:{ Exception -> 0x11fa }
            org.telegram.tgnet.TLRPC$PhotoSize r11 = r3.photoSize     // Catch:{ Exception -> 0x11fa }
            r6.add(r11)     // Catch:{ Exception -> 0x11fa }
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo> r6 = r3.videoEditedInfos     // Catch:{ Exception -> 0x11fa }
            org.telegram.messenger.VideoEditedInfo r11 = r3.videoEditedInfo     // Catch:{ Exception -> 0x11fa }
            r6.add(r11)     // Catch:{ Exception -> 0x11fa }
            java.util.ArrayList<java.lang.String> r6 = r3.httpLocations     // Catch:{ Exception -> 0x11fa }
            java.lang.String r11 = r3.httpLocation     // Catch:{ Exception -> 0x11fa }
            r6.add(r11)     // Catch:{ Exception -> 0x11fa }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputMedia> r6 = r3.inputMedias     // Catch:{ Exception -> 0x11fa }
            org.telegram.tgnet.TLRPC$InputMedia r11 = r3.inputUploadMedia     // Catch:{ Exception -> 0x11fa }
            r6.add(r11)     // Catch:{ Exception -> 0x11fa }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r6 = r3.messages     // Catch:{ Exception -> 0x11fa }
            r6.add(r8)     // Catch:{ Exception -> 0x11fa }
            java.util.ArrayList<java.lang.String> r6 = r3.originalPaths     // Catch:{ Exception -> 0x11fa }
            r6.add(r10)     // Catch:{ Exception -> 0x11fa }
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r6 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia     // Catch:{ Exception -> 0x11fa }
            r6.<init>()     // Catch:{ Exception -> 0x11fa }
            long r11 = r8.random_id     // Catch:{ Exception -> 0x11fa }
            r6.random_id = r11     // Catch:{ Exception -> 0x11fa }
            r6.media = r5     // Catch:{ Exception -> 0x11fa }
            r11 = r38
            r6.message = r11     // Catch:{ Exception -> 0x11fa }
            r12 = r73
            if (r12 == 0) goto L_0x134b
            boolean r5 = r73.isEmpty()     // Catch:{ Exception -> 0x11fa }
            if (r5 != 0) goto L_0x134b
            r6.entities = r12     // Catch:{ Exception -> 0x11fa }
            int r5 = r6.flags     // Catch:{ Exception -> 0x11fa }
            r11 = 1
            r5 = r5 | r11
            r6.flags = r5     // Catch:{ Exception -> 0x11fa }
        L_0x134b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r5 = r1.multi_media     // Catch:{ Exception -> 0x11fa }
            r5.add(r6)     // Catch:{ Exception -> 0x11fa }
            goto L_0x139d
        L_0x1351:
            r12 = r73
            r9 = r18
            r8 = r19
            r11 = r38
            r6 = r39
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia     // Catch:{ Exception -> 0x11fa }
            r1.<init>()     // Catch:{ Exception -> 0x11fa }
            r1.peer = r6     // Catch:{ Exception -> 0x11fa }
            boolean r6 = r8.silent     // Catch:{ Exception -> 0x11fa }
            r1.silent = r6     // Catch:{ Exception -> 0x11fa }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r6 = r8.reply_to     // Catch:{ Exception -> 0x11fa }
            if (r6 == 0) goto L_0x1376
            int r6 = r6.reply_to_msg_id     // Catch:{ Exception -> 0x11fa }
            if (r6 == 0) goto L_0x1376
            int r14 = r1.flags     // Catch:{ Exception -> 0x11fa }
            r15 = 1
            r14 = r14 | r15
            r1.flags = r14     // Catch:{ Exception -> 0x11fa }
            r1.reply_to_msg_id = r6     // Catch:{ Exception -> 0x11fa }
        L_0x1376:
            long r14 = r8.random_id     // Catch:{ Exception -> 0x11fa }
            r1.random_id = r14     // Catch:{ Exception -> 0x11fa }
            r1.media = r5     // Catch:{ Exception -> 0x11fa }
            r1.message = r11     // Catch:{ Exception -> 0x11fa }
            if (r12 == 0) goto L_0x138f
            boolean r5 = r73.isEmpty()     // Catch:{ Exception -> 0x11fa }
            if (r5 != 0) goto L_0x138f
            r1.entities = r12     // Catch:{ Exception -> 0x11fa }
            int r5 = r1.flags     // Catch:{ Exception -> 0x11fa }
            r6 = 8
            r5 = r5 | r6
            r1.flags = r5     // Catch:{ Exception -> 0x11fa }
        L_0x138f:
            if (r13 == 0) goto L_0x1399
            r1.schedule_date = r13     // Catch:{ Exception -> 0x11fa }
            int r5 = r1.flags     // Catch:{ Exception -> 0x11fa }
            r5 = r5 | 1024(0x400, float:1.435E-42)
            r1.flags = r5     // Catch:{ Exception -> 0x11fa }
        L_0x1399:
            if (r3 == 0) goto L_0x139d
            r3.sendRequest = r1     // Catch:{ Exception -> 0x11fa }
        L_0x139d:
            int r5 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r5 == 0) goto L_0x13a6
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x13a6:
            r5 = r70
            r6 = 1
            if (r5 != r6) goto L_0x13c6
            r4 = 0
            if (r13 == 0) goto L_0x13b0
            r5 = 1
            goto L_0x13b1
        L_0x13b0:
            r5 = 0
        L_0x13b1:
            r56 = r55
            r57 = r1
            r58 = r7
            r59 = r4
            r60 = r3
            r61 = r9
            r62 = r64
            r63 = r5
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x13c6:
            r6 = 2
            if (r5 != r6) goto L_0x13ee
            if (r4 == 0) goto L_0x13d0
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x13d0:
            r4 = 0
            r5 = 1
            if (r13 == 0) goto L_0x13d6
            r6 = 1
            goto L_0x13d7
        L_0x13d6:
            r6 = 0
        L_0x13d7:
            r56 = r55
            r57 = r1
            r58 = r7
            r59 = r10
            r60 = r4
            r61 = r5
            r62 = r3
            r63 = r9
            r65 = r6
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63, r64, r65)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x13ee:
            r6 = 3
            if (r5 != r6) goto L_0x1412
            if (r4 == 0) goto L_0x13f8
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x13f8:
            if (r13 == 0) goto L_0x13fc
            r4 = 1
            goto L_0x13fd
        L_0x13fc:
            r4 = 0
        L_0x13fd:
            r56 = r55
            r57 = r1
            r58 = r7
            r59 = r10
            r60 = r3
            r61 = r9
            r62 = r64
            r63 = r4
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x1412:
            r6 = 6
            if (r5 != r6) goto L_0x142f
            if (r13 == 0) goto L_0x1419
            r4 = 1
            goto L_0x141a
        L_0x1419:
            r4 = 0
        L_0x141a:
            r56 = r55
            r57 = r1
            r58 = r7
            r59 = r10
            r60 = r3
            r61 = r9
            r62 = r64
            r63 = r4
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x142f:
            r6 = 7
            if (r5 != r6) goto L_0x1455
            if (r4 == 0) goto L_0x143b
            if (r3 == 0) goto L_0x143b
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x143b:
            if (r13 == 0) goto L_0x143f
            r4 = 1
            goto L_0x1440
        L_0x143f:
            r4 = 0
        L_0x1440:
            r56 = r55
            r57 = r1
            r58 = r7
            r59 = r10
            r60 = r3
            r61 = r9
            r62 = r64
            r63 = r4
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x1455:
            r6 = 8
            if (r5 != r6) goto L_0x147a
            if (r4 == 0) goto L_0x1460
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x1460:
            if (r13 == 0) goto L_0x1464
            r4 = 1
            goto L_0x1465
        L_0x1464:
            r4 = 0
        L_0x1465:
            r56 = r55
            r57 = r1
            r58 = r7
            r59 = r10
            r60 = r3
            r61 = r9
            r62 = r64
            r63 = r4
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x147a:
            r4 = 10
            if (r5 == r4) goto L_0x1482
            r4 = 11
            if (r5 != r4) goto L_0x1bf5
        L_0x1482:
            if (r13 == 0) goto L_0x1486
            r4 = 1
            goto L_0x1487
        L_0x1486:
            r4 = 0
        L_0x1487:
            r56 = r55
            r57 = r1
            r58 = r7
            r59 = r10
            r60 = r3
            r61 = r9
            r62 = r64
            r63 = r4
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x11fa }
            goto L_0x1bf5
        L_0x149c:
            r0 = move-exception
            r8 = r19
        L_0x149f:
            r1 = r0
            r5 = r8
            goto L_0x0ae1
        L_0x14a3:
            r8 = r73
            r64 = r1
            r7 = r2
            r13 = r4
            r2 = r10
            r5 = r12
            r4 = r19
            r11 = r20
            r6 = r27
            r12 = r38
            r1 = r44
            r19 = r63
            int r10 = r9.layer     // Catch:{ Exception -> 0x1a6d }
            int r10 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r10)     // Catch:{ Exception -> 0x1a6d }
            r14 = 73
            if (r10 < r14) goto L_0x14e3
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x14de }
            r10.<init>()     // Catch:{ Exception -> 0x14de }
            int r14 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r14 == 0) goto L_0x14d9
            r14 = r42
            r10.grouped_id = r14     // Catch:{ Exception -> 0x14de }
            r56 = r3
            int r3 = r10.flags     // Catch:{ Exception -> 0x14de }
            r20 = 131072(0x20000, float:1.83671E-40)
            r3 = r3 | r20
            r10.flags = r3     // Catch:{ Exception -> 0x14de }
            goto L_0x14ec
        L_0x14d9:
            r56 = r3
            r14 = r42
            goto L_0x14ec
        L_0x14de:
            r0 = move-exception
            r11 = r77
            goto L_0x02f1
        L_0x14e3:
            r56 = r3
            r14 = r42
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x1a6d }
            r10.<init>()     // Catch:{ Exception -> 0x1a6d }
        L_0x14ec:
            int r3 = r5.ttl     // Catch:{ Exception -> 0x1a6d }
            r10.ttl = r3     // Catch:{ Exception -> 0x1a6d }
            if (r8 == 0) goto L_0x1500
            boolean r3 = r73.isEmpty()     // Catch:{ Exception -> 0x14de }
            if (r3 != 0) goto L_0x1500
            r10.entities = r8     // Catch:{ Exception -> 0x14de }
            int r3 = r10.flags     // Catch:{ Exception -> 0x14de }
            r3 = r3 | 128(0x80, float:1.794E-43)
            r10.flags = r3     // Catch:{ Exception -> 0x14de }
        L_0x1500:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r5.reply_to     // Catch:{ Exception -> 0x1a6d }
            r42 = r14
            if (r3 == 0) goto L_0x1515
            long r14 = r3.reply_to_random_id     // Catch:{ Exception -> 0x14de }
            int r3 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x1515
            r10.reply_to_random_id = r14     // Catch:{ Exception -> 0x14de }
            int r3 = r10.flags     // Catch:{ Exception -> 0x14de }
            r8 = 8
            r3 = r3 | r8
            r10.flags = r3     // Catch:{ Exception -> 0x14de }
        L_0x1515:
            boolean r3 = r5.silent     // Catch:{ Exception -> 0x1a6d }
            r10.silent = r3     // Catch:{ Exception -> 0x1a6d }
            int r3 = r10.flags     // Catch:{ Exception -> 0x1a6d }
            r3 = r3 | 512(0x200, float:7.175E-43)
            r10.flags = r3     // Catch:{ Exception -> 0x1a6d }
            if (r64 == 0) goto L_0x153a
            r8 = r59
            r3 = r64
            java.lang.Object r14 = r3.get(r8)     // Catch:{ Exception -> 0x14de }
            if (r14 == 0) goto L_0x153c
            java.lang.Object r8 = r3.get(r8)     // Catch:{ Exception -> 0x14de }
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ Exception -> 0x14de }
            r10.via_bot_name = r8     // Catch:{ Exception -> 0x14de }
            int r8 = r10.flags     // Catch:{ Exception -> 0x14de }
            r8 = r8 | 2048(0x800, float:2.87E-42)
            r10.flags = r8     // Catch:{ Exception -> 0x14de }
            goto L_0x153c
        L_0x153a:
            r3 = r64
        L_0x153c:
            long r14 = r5.random_id     // Catch:{ Exception -> 0x1a6d }
            r10.random_id = r14     // Catch:{ Exception -> 0x1a6d }
            r10.message = r11     // Catch:{ Exception -> 0x1a6d }
            r8 = 1
            if (r13 != r8) goto L_0x1597
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x14de }
            if (r1 == 0) goto L_0x1561
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue     // Catch:{ Exception -> 0x14de }
            r1.<init>()     // Catch:{ Exception -> 0x14de }
            r10.media = r1     // Catch:{ Exception -> 0x14de }
            java.lang.String r3 = r6.address     // Catch:{ Exception -> 0x14de }
            r1.address = r3     // Catch:{ Exception -> 0x14de }
            java.lang.String r3 = r6.title     // Catch:{ Exception -> 0x14de }
            r1.title = r3     // Catch:{ Exception -> 0x14de }
            java.lang.String r3 = r6.provider     // Catch:{ Exception -> 0x14de }
            r1.provider = r3     // Catch:{ Exception -> 0x14de }
            java.lang.String r3 = r6.venue_id     // Catch:{ Exception -> 0x14de }
            r1.venue_id = r3     // Catch:{ Exception -> 0x14de }
            goto L_0x1568
        L_0x1561:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint     // Catch:{ Exception -> 0x14de }
            r1.<init>()     // Catch:{ Exception -> 0x14de }
            r10.media = r1     // Catch:{ Exception -> 0x14de }
        L_0x1568:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x14de }
            org.telegram.tgnet.TLRPC$GeoPoint r3 = r6.geo     // Catch:{ Exception -> 0x14de }
            double r11 = r3.lat     // Catch:{ Exception -> 0x14de }
            r1.lat = r11     // Catch:{ Exception -> 0x14de }
            double r3 = r3._long     // Catch:{ Exception -> 0x14de }
            r1._long = r3     // Catch:{ Exception -> 0x14de }
            org.telegram.messenger.SecretChatHelper r1 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x14de }
            org.telegram.tgnet.TLRPC$Message r3 = r7.messageOwner     // Catch:{ Exception -> 0x14de }
            r4 = 0
            r6 = 0
            r58 = r1
            r59 = r10
            r60 = r3
            r61 = r9
            r62 = r4
            r63 = r6
            r64 = r7
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x14de }
            r14 = r65
            r11 = r77
            r19 = r5
            r5 = r56
            goto L_0x1809
        L_0x1597:
            r6 = 2
            if (r13 == r6) goto L_0x18ea
            r6 = 9
            if (r13 != r6) goto L_0x15a2
            if (r1 == 0) goto L_0x15a2
            goto L_0x18ea
        L_0x15a2:
            r1 = 3
            if (r13 != r1) goto L_0x16c3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs     // Catch:{ Exception -> 0x14de }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r2.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x14de }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1)     // Catch:{ Exception -> 0x14de }
            boolean r6 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r4)     // Catch:{ Exception -> 0x14de }
            if (r6 != 0) goto L_0x15d5
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r4)     // Catch:{ Exception -> 0x14de }
            if (r6 == 0) goto L_0x15bb
            goto L_0x15d5
        L_0x15bb:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo     // Catch:{ Exception -> 0x14de }
            r6.<init>()     // Catch:{ Exception -> 0x14de }
            r10.media = r6     // Catch:{ Exception -> 0x14de }
            if (r1 == 0) goto L_0x15cd
            byte[] r8 = r1.bytes     // Catch:{ Exception -> 0x14de }
            if (r8 == 0) goto L_0x15cd
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r6     // Catch:{ Exception -> 0x14de }
            r6.thumb = r8     // Catch:{ Exception -> 0x14de }
            goto L_0x15f2
        L_0x15cd:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r6     // Catch:{ Exception -> 0x14de }
            r8 = 0
            byte[] r11 = new byte[r8]     // Catch:{ Exception -> 0x14de }
            r6.thumb = r11     // Catch:{ Exception -> 0x14de }
            goto L_0x15f2
        L_0x15d5:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x14de }
            r6.<init>()     // Catch:{ Exception -> 0x14de }
            r10.media = r6     // Catch:{ Exception -> 0x14de }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r4.attributes     // Catch:{ Exception -> 0x14de }
            r6.attributes = r8     // Catch:{ Exception -> 0x14de }
            if (r1 == 0) goto L_0x15eb
            byte[] r8 = r1.bytes     // Catch:{ Exception -> 0x14de }
            if (r8 == 0) goto L_0x15eb
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r6     // Catch:{ Exception -> 0x14de }
            r6.thumb = r8     // Catch:{ Exception -> 0x14de }
            goto L_0x15f2
        L_0x15eb:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r6     // Catch:{ Exception -> 0x14de }
            r8 = 0
            byte[] r11 = new byte[r8]     // Catch:{ Exception -> 0x14de }
            r6.thumb = r11     // Catch:{ Exception -> 0x14de }
        L_0x15f2:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r10.media     // Catch:{ Exception -> 0x14de }
            r6.caption = r12     // Catch:{ Exception -> 0x14de }
            java.lang.String r8 = "video/mp4"
            r6.mime_type = r8     // Catch:{ Exception -> 0x14de }
            int r8 = r4.size     // Catch:{ Exception -> 0x14de }
            r6.size = r8     // Catch:{ Exception -> 0x14de }
            r6 = 0
        L_0x1600:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r4.attributes     // Catch:{ Exception -> 0x14de }
            int r8 = r8.size()     // Catch:{ Exception -> 0x14de }
            if (r6 >= r8) goto L_0x1626
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r4.attributes     // Catch:{ Exception -> 0x14de }
            java.lang.Object r8 = r8.get(r6)     // Catch:{ Exception -> 0x14de }
            org.telegram.tgnet.TLRPC$DocumentAttribute r8 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r8     // Catch:{ Exception -> 0x14de }
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x14de }
            if (r11 == 0) goto L_0x1623
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r10.media     // Catch:{ Exception -> 0x14de }
            int r11 = r8.w     // Catch:{ Exception -> 0x14de }
            r6.w = r11     // Catch:{ Exception -> 0x14de }
            int r11 = r8.h     // Catch:{ Exception -> 0x14de }
            r6.h = r11     // Catch:{ Exception -> 0x14de }
            int r8 = r8.duration     // Catch:{ Exception -> 0x14de }
            r6.duration = r8     // Catch:{ Exception -> 0x14de }
            goto L_0x1626
        L_0x1623:
            int r6 = r6 + 1
            goto L_0x1600
        L_0x1626:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r10.media     // Catch:{ Exception -> 0x14de }
            int r8 = r1.h     // Catch:{ Exception -> 0x14de }
            r6.thumb_h = r8     // Catch:{ Exception -> 0x14de }
            int r1 = r1.w     // Catch:{ Exception -> 0x14de }
            r6.thumb_w = r1     // Catch:{ Exception -> 0x14de }
            byte[] r1 = r4.key     // Catch:{ Exception -> 0x14de }
            if (r1 == 0) goto L_0x1671
            int r1 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1639
            goto L_0x1671
        L_0x1639:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x14de }
            r1.<init>()     // Catch:{ Exception -> 0x14de }
            long r11 = r4.id     // Catch:{ Exception -> 0x14de }
            r1.id = r11     // Catch:{ Exception -> 0x14de }
            long r11 = r4.access_hash     // Catch:{ Exception -> 0x14de }
            r1.access_hash = r11     // Catch:{ Exception -> 0x14de }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x14de }
            byte[] r6 = r4.key     // Catch:{ Exception -> 0x14de }
            r3.key = r6     // Catch:{ Exception -> 0x14de }
            byte[] r4 = r4.iv     // Catch:{ Exception -> 0x14de }
            r3.iv = r4     // Catch:{ Exception -> 0x14de }
            org.telegram.messenger.SecretChatHelper r3 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x14de }
            org.telegram.tgnet.TLRPC$Message r4 = r7.messageOwner     // Catch:{ Exception -> 0x14de }
            r6 = 0
            r58 = r3
            r59 = r10
            r60 = r4
            r61 = r9
            r62 = r1
            r63 = r6
            r64 = r7
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x14de }
            r3 = r56
            r14 = r65
            r11 = r77
            r6 = r46
            goto L_0x16bd
        L_0x1671:
            if (r56 != 0) goto L_0x16aa
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x14de }
            r14 = r65
            r1.<init>(r14)     // Catch:{ Exception -> 0x14de }
            r1.encryptedChat = r9     // Catch:{ Exception -> 0x14de }
            r4 = 1
            r1.type = r4     // Catch:{ Exception -> 0x14de }
            r1.sendEncryptedRequest = r10     // Catch:{ Exception -> 0x14de }
            r6 = r46
            r1.originalPath = r6     // Catch:{ Exception -> 0x14de }
            r1.obj = r7     // Catch:{ Exception -> 0x14de }
            if (r3 == 0) goto L_0x1698
            r8 = r41
            boolean r4 = r3.containsKey(r8)     // Catch:{ Exception -> 0x14de }
            if (r4 == 0) goto L_0x1698
            java.lang.Object r3 = r3.get(r8)     // Catch:{ Exception -> 0x14de }
            r1.parentObject = r3     // Catch:{ Exception -> 0x14de }
            goto L_0x169c
        L_0x1698:
            r3 = r19
            r1.parentObject = r3     // Catch:{ Exception -> 0x14de }
        L_0x169c:
            r3 = 1
            r1.performMediaUpload = r3     // Catch:{ Exception -> 0x14de }
            r11 = r77
            if (r11 == 0) goto L_0x16a5
            r3 = 1
            goto L_0x16a6
        L_0x16a5:
            r3 = 0
        L_0x16a6:
            r1.scheduled = r3     // Catch:{ Exception -> 0x1a46 }
            r3 = r1
            goto L_0x16b2
        L_0x16aa:
            r14 = r65
            r11 = r77
            r6 = r46
            r3 = r56
        L_0x16b2:
            r1 = r58
            r3.videoEditedInfo = r1     // Catch:{ Exception -> 0x1a46 }
            int r1 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r1 != 0) goto L_0x16bd
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x1a46 }
        L_0x16bd:
            r19 = r5
        L_0x16bf:
            r70 = r13
            goto L_0x19fe
        L_0x16c3:
            r14 = r65
            r11 = r77
            r1 = r19
            r8 = r41
            r6 = r46
            r19 = r5
            r5 = 6
            if (r13 != r5) goto L_0x170a
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact     // Catch:{ Exception -> 0x1706 }
            r1.<init>()     // Catch:{ Exception -> 0x1706 }
            r10.media = r1     // Catch:{ Exception -> 0x1706 }
            r5 = r18
            java.lang.String r3 = r5.phone     // Catch:{ Exception -> 0x1706 }
            r1.phone_number = r3     // Catch:{ Exception -> 0x1706 }
            java.lang.String r3 = r5.first_name     // Catch:{ Exception -> 0x1706 }
            r1.first_name = r3     // Catch:{ Exception -> 0x1706 }
            java.lang.String r3 = r5.last_name     // Catch:{ Exception -> 0x1706 }
            r1.last_name = r3     // Catch:{ Exception -> 0x1706 }
            int r3 = r5.id     // Catch:{ Exception -> 0x1706 }
            r1.user_id = r3     // Catch:{ Exception -> 0x1706 }
            org.telegram.messenger.SecretChatHelper r1 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$Message r3 = r7.messageOwner     // Catch:{ Exception -> 0x1706 }
            r4 = 0
            r5 = 0
            r58 = r1
            r59 = r10
            r60 = r3
            r61 = r9
            r62 = r4
            r63 = r5
            r64 = r7
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1706 }
            goto L_0x177b
        L_0x1706:
            r0 = move-exception
        L_0x1707:
            r1 = r0
            goto L_0x0var_
        L_0x170a:
            r5 = 7
            if (r13 == r5) goto L_0x1781
            r5 = 9
            if (r13 != r5) goto L_0x1714
            if (r4 == 0) goto L_0x1714
            goto L_0x1781
        L_0x1714:
            r3 = 8
            if (r13 != r3) goto L_0x177b
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1706 }
            r3.<init>(r14)     // Catch:{ Exception -> 0x1706 }
            r3.encryptedChat = r9     // Catch:{ Exception -> 0x1706 }
            r3.sendEncryptedRequest = r10     // Catch:{ Exception -> 0x1706 }
            r3.obj = r7     // Catch:{ Exception -> 0x1706 }
            r5 = 3
            r3.type = r5     // Catch:{ Exception -> 0x1706 }
            r3.parentObject = r1     // Catch:{ Exception -> 0x1706 }
            r1 = 1
            r3.performMediaUpload = r1     // Catch:{ Exception -> 0x1706 }
            if (r11 == 0) goto L_0x172f
            r1 = 1
            goto L_0x1730
        L_0x172f:
            r1 = 0
        L_0x1730:
            r3.scheduled = r1     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1706 }
            r1.<init>()     // Catch:{ Exception -> 0x1706 }
            r10.media = r1     // Catch:{ Exception -> 0x1706 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r4.attributes     // Catch:{ Exception -> 0x1706 }
            r1.attributes = r5     // Catch:{ Exception -> 0x1706 }
            r1.caption = r12     // Catch:{ Exception -> 0x1706 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r2.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x1706 }
            if (r1 == 0) goto L_0x175c
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1)     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r10.media     // Catch:{ Exception -> 0x1706 }
            r8 = r5
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r8 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r8     // Catch:{ Exception -> 0x1706 }
            byte[] r9 = r1.bytes     // Catch:{ Exception -> 0x1706 }
            r8.thumb = r9     // Catch:{ Exception -> 0x1706 }
            int r8 = r1.h     // Catch:{ Exception -> 0x1706 }
            r5.thumb_h = r8     // Catch:{ Exception -> 0x1706 }
            int r1 = r1.w     // Catch:{ Exception -> 0x1706 }
            r5.thumb_w = r1     // Catch:{ Exception -> 0x1706 }
            goto L_0x176a
        L_0x175c:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1706 }
            r5 = r1
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1706 }
            r8 = 0
            byte[] r9 = new byte[r8]     // Catch:{ Exception -> 0x1706 }
            r5.thumb = r9     // Catch:{ Exception -> 0x1706 }
            r1.thumb_h = r8     // Catch:{ Exception -> 0x1706 }
            r1.thumb_w = r8     // Catch:{ Exception -> 0x1706 }
        L_0x176a:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1706 }
            java.lang.String r5 = r4.mime_type     // Catch:{ Exception -> 0x1706 }
            r1.mime_type = r5     // Catch:{ Exception -> 0x1706 }
            int r4 = r4.size     // Catch:{ Exception -> 0x1706 }
            r1.size = r4     // Catch:{ Exception -> 0x1706 }
            r3.originalPath = r6     // Catch:{ Exception -> 0x1706 }
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x1706 }
            goto L_0x16bf
        L_0x177b:
            r5 = r56
            r46 = r6
            goto L_0x1809
        L_0x1781:
            r46 = r6
            long r5 = r4.access_hash     // Catch:{ Exception -> 0x1706 }
            int r18 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r18 == 0) goto L_0x1810
            boolean r5 = org.telegram.messenger.MessageObject.isStickerDocument(r4)     // Catch:{ Exception -> 0x1706 }
            if (r5 != 0) goto L_0x1796
            r5 = 1
            boolean r6 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r5)     // Catch:{ Exception -> 0x1706 }
            if (r6 == 0) goto L_0x1810
        L_0x1796:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument     // Catch:{ Exception -> 0x1706 }
            r1.<init>()     // Catch:{ Exception -> 0x1706 }
            r10.media = r1     // Catch:{ Exception -> 0x1706 }
            long r5 = r4.id     // Catch:{ Exception -> 0x1706 }
            r1.id = r5     // Catch:{ Exception -> 0x1706 }
            int r3 = r4.date     // Catch:{ Exception -> 0x1706 }
            r1.date = r3     // Catch:{ Exception -> 0x1706 }
            long r5 = r4.access_hash     // Catch:{ Exception -> 0x1706 }
            r1.access_hash = r5     // Catch:{ Exception -> 0x1706 }
            java.lang.String r3 = r4.mime_type     // Catch:{ Exception -> 0x1706 }
            r1.mime_type = r3     // Catch:{ Exception -> 0x1706 }
            int r3 = r4.size     // Catch:{ Exception -> 0x1706 }
            r1.size = r3     // Catch:{ Exception -> 0x1706 }
            int r3 = r4.dc_id     // Catch:{ Exception -> 0x1706 }
            r1.dc_id = r3     // Catch:{ Exception -> 0x1706 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r4.attributes     // Catch:{ Exception -> 0x1706 }
            r1.attributes = r3     // Catch:{ Exception -> 0x1706 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r2.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x1706 }
            if (r1 == 0) goto L_0x17c8
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r3 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r3     // Catch:{ Exception -> 0x1706 }
            r3.thumb = r1     // Catch:{ Exception -> 0x1706 }
            goto L_0x17dd
        L_0x17c8:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r3 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty     // Catch:{ Exception -> 0x1706 }
            r3.<init>()     // Catch:{ Exception -> 0x1706 }
            r1.thumb = r3     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r10.media     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r1.thumb     // Catch:{ Exception -> 0x1706 }
            java.lang.String r3 = "s"
            r1.type = r3     // Catch:{ Exception -> 0x1706 }
        L_0x17dd:
            if (r56 == 0) goto L_0x17ee
            r5 = r56
            int r1 = r5.type     // Catch:{ Exception -> 0x1706 }
            r3 = 5
            if (r1 != r3) goto L_0x17f0
            r5.sendEncryptedRequest = r10     // Catch:{ Exception -> 0x1706 }
            r5.obj = r7     // Catch:{ Exception -> 0x1706 }
            r2.performSendDelayedMessage(r5)     // Catch:{ Exception -> 0x1706 }
            goto L_0x1809
        L_0x17ee:
            r5 = r56
        L_0x17f0:
            org.telegram.messenger.SecretChatHelper r1 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$Message r3 = r7.messageOwner     // Catch:{ Exception -> 0x1706 }
            r4 = 0
            r6 = 0
            r58 = r1
            r59 = r10
            r60 = r3
            r61 = r9
            r62 = r4
            r63 = r6
            r64 = r7
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1706 }
        L_0x1809:
            r3 = r5
            r70 = r13
        L_0x180c:
            r6 = r46
            goto L_0x19fe
        L_0x1810:
            r5 = r56
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1706 }
            r6.<init>()     // Catch:{ Exception -> 0x1706 }
            r10.media = r6     // Catch:{ Exception -> 0x1706 }
            r70 = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r4.attributes     // Catch:{ Exception -> 0x1706 }
            r6.attributes = r13     // Catch:{ Exception -> 0x1706 }
            r6.caption = r12     // Catch:{ Exception -> 0x1706 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r4.thumbs     // Catch:{ Exception -> 0x1706 }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r2.getThumbForSecretChat(r6)     // Catch:{ Exception -> 0x1706 }
            if (r6 == 0) goto L_0x183e
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r6)     // Catch:{ Exception -> 0x1930 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r12 = r10.media     // Catch:{ Exception -> 0x1930 }
            r13 = r12
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r13 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r13     // Catch:{ Exception -> 0x1930 }
            byte[] r11 = r6.bytes     // Catch:{ Exception -> 0x1930 }
            r13.thumb = r11     // Catch:{ Exception -> 0x1930 }
            int r11 = r6.h     // Catch:{ Exception -> 0x1930 }
            r12.thumb_h = r11     // Catch:{ Exception -> 0x1930 }
            int r6 = r6.w     // Catch:{ Exception -> 0x1930 }
            r12.thumb_w = r6     // Catch:{ Exception -> 0x1930 }
            goto L_0x184c
        L_0x183e:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r10.media     // Catch:{ Exception -> 0x1930 }
            r11 = r6
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r11 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r11     // Catch:{ Exception -> 0x1930 }
            r12 = 0
            byte[] r13 = new byte[r12]     // Catch:{ Exception -> 0x1930 }
            r11.thumb = r13     // Catch:{ Exception -> 0x1930 }
            r6.thumb_h = r12     // Catch:{ Exception -> 0x1930 }
            r6.thumb_w = r12     // Catch:{ Exception -> 0x1930 }
        L_0x184c:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r10.media     // Catch:{ Exception -> 0x1930 }
            int r11 = r4.size     // Catch:{ Exception -> 0x1930 }
            r6.size = r11     // Catch:{ Exception -> 0x1930 }
            java.lang.String r11 = r4.mime_type     // Catch:{ Exception -> 0x1930 }
            r6.mime_type = r11     // Catch:{ Exception -> 0x1930 }
            byte[] r6 = r4.key     // Catch:{ Exception -> 0x1930 }
            if (r6 == 0) goto L_0x1893
            int r6 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r6 == 0) goto L_0x185f
            goto L_0x1893
        L_0x185f:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1930 }
            r1.<init>()     // Catch:{ Exception -> 0x1930 }
            long r11 = r4.id     // Catch:{ Exception -> 0x1930 }
            r1.id = r11     // Catch:{ Exception -> 0x1930 }
            long r11 = r4.access_hash     // Catch:{ Exception -> 0x1930 }
            r1.access_hash = r11     // Catch:{ Exception -> 0x1930 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r10.media     // Catch:{ Exception -> 0x1930 }
            byte[] r6 = r4.key     // Catch:{ Exception -> 0x1930 }
            r3.key = r6     // Catch:{ Exception -> 0x1930 }
            byte[] r4 = r4.iv     // Catch:{ Exception -> 0x1930 }
            r3.iv = r4     // Catch:{ Exception -> 0x1930 }
            org.telegram.messenger.SecretChatHelper r3 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1930 }
            org.telegram.tgnet.TLRPC$Message r4 = r7.messageOwner     // Catch:{ Exception -> 0x1930 }
            r6 = 0
            r58 = r3
            r59 = r10
            r60 = r4
            r61 = r9
            r62 = r1
            r63 = r6
            r64 = r7
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1930 }
            r11 = r77
            r3 = r5
            goto L_0x180c
        L_0x1893:
            if (r5 != 0) goto L_0x18c8
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r4 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1930 }
            r4.<init>(r14)     // Catch:{ Exception -> 0x1930 }
            r4.encryptedChat = r9     // Catch:{ Exception -> 0x1930 }
            r5 = 2
            r4.type = r5     // Catch:{ Exception -> 0x1930 }
            r4.sendEncryptedRequest = r10     // Catch:{ Exception -> 0x1930 }
            r6 = r46
            r4.originalPath = r6     // Catch:{ Exception -> 0x1930 }
            r4.obj = r7     // Catch:{ Exception -> 0x1930 }
            if (r3 == 0) goto L_0x18b6
            boolean r5 = r3.containsKey(r8)     // Catch:{ Exception -> 0x1930 }
            if (r5 == 0) goto L_0x18b6
            java.lang.Object r1 = r3.get(r8)     // Catch:{ Exception -> 0x1930 }
            r4.parentObject = r1     // Catch:{ Exception -> 0x1930 }
            goto L_0x18b8
        L_0x18b6:
            r4.parentObject = r1     // Catch:{ Exception -> 0x1930 }
        L_0x18b8:
            r1 = 1
            r4.performMediaUpload = r1     // Catch:{ Exception -> 0x1930 }
            r11 = r77
            if (r11 == 0) goto L_0x18c1
            r1 = 1
            goto L_0x18c2
        L_0x18c1:
            r1 = 0
        L_0x18c2:
            r4.scheduled = r1     // Catch:{ Exception -> 0x1706 }
            r3 = r4
            r4 = r67
            goto L_0x18cf
        L_0x18c8:
            r11 = r77
            r6 = r46
            r4 = r67
            r3 = r5
        L_0x18cf:
            if (r4 == 0) goto L_0x18e1
            int r1 = r67.length()     // Catch:{ Exception -> 0x1706 }
            if (r1 <= 0) goto L_0x18e1
            r13 = r45
            boolean r1 = r4.startsWith(r13)     // Catch:{ Exception -> 0x1706 }
            if (r1 == 0) goto L_0x18e1
            r3.httpLocation = r4     // Catch:{ Exception -> 0x1706 }
        L_0x18e1:
            int r1 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r1 != 0) goto L_0x19fe
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x1706 }
            goto L_0x19fe
        L_0x18ea:
            r14 = r65
            r11 = r77
            r4 = r1
            r70 = r13
            r1 = r19
            r8 = r41
            r6 = r46
            r19 = r5
            r5 = r56
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r13 = r4.sizes     // Catch:{ Exception -> 0x1a68 }
            r11 = 0
            java.lang.Object r13 = r13.get(r11)     // Catch:{ Exception -> 0x1a64 }
            org.telegram.tgnet.TLRPC$PhotoSize r13 = (org.telegram.tgnet.TLRPC$PhotoSize) r13     // Catch:{ Exception -> 0x1a64 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r4.sizes     // Catch:{ Exception -> 0x1a64 }
            int r18 = r11.size()     // Catch:{ Exception -> 0x1a64 }
            r44 = r4
            r20 = 1
            int r4 = r18 + -1
            java.lang.Object r4 = r11.get(r4)     // Catch:{ Exception -> 0x1a64 }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = (org.telegram.tgnet.TLRPC$PhotoSize) r4     // Catch:{ Exception -> 0x1a64 }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r13)     // Catch:{ Exception -> 0x1a64 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r11 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto     // Catch:{ Exception -> 0x1a64 }
            r11.<init>()     // Catch:{ Exception -> 0x1a64 }
            r10.media = r11     // Catch:{ Exception -> 0x1a64 }
            r11.caption = r12     // Catch:{ Exception -> 0x1a64 }
            byte[] r12 = r13.bytes     // Catch:{ Exception -> 0x1a64 }
            if (r12 == 0) goto L_0x1935
            r18 = r1
            r1 = r11
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r1     // Catch:{ Exception -> 0x1930 }
            r1.thumb = r12     // Catch:{ Exception -> 0x1930 }
            r41 = r8
            goto L_0x1941
        L_0x1930:
            r0 = move-exception
            r11 = r77
            goto L_0x1707
        L_0x1935:
            r18 = r1
            r1 = r11
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r1     // Catch:{ Exception -> 0x1a64 }
            r41 = r8
            r12 = 0
            byte[] r8 = new byte[r12]     // Catch:{ Exception -> 0x1a64 }
            r1.thumb = r8     // Catch:{ Exception -> 0x1a64 }
        L_0x1941:
            int r1 = r13.h     // Catch:{ Exception -> 0x1a64 }
            r11.thumb_h = r1     // Catch:{ Exception -> 0x1a64 }
            int r1 = r13.w     // Catch:{ Exception -> 0x1a64 }
            r11.thumb_w = r1     // Catch:{ Exception -> 0x1a64 }
            int r1 = r4.w     // Catch:{ Exception -> 0x1a64 }
            r11.w = r1     // Catch:{ Exception -> 0x1a64 }
            int r1 = r4.h     // Catch:{ Exception -> 0x1a64 }
            r11.h = r1     // Catch:{ Exception -> 0x1a64 }
            int r1 = r4.size     // Catch:{ Exception -> 0x1a64 }
            r11.size = r1     // Catch:{ Exception -> 0x1a64 }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r4.location     // Catch:{ Exception -> 0x1a64 }
            byte[] r1 = r1.key     // Catch:{ Exception -> 0x1a64 }
            if (r1 == 0) goto L_0x1996
            int r1 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1960
            goto L_0x1996
        L_0x1960:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1930 }
            r1.<init>()     // Catch:{ Exception -> 0x1930 }
            org.telegram.tgnet.TLRPC$FileLocation r3 = r4.location     // Catch:{ Exception -> 0x1930 }
            long r11 = r3.volume_id     // Catch:{ Exception -> 0x1930 }
            r1.id = r11     // Catch:{ Exception -> 0x1930 }
            long r11 = r3.secret     // Catch:{ Exception -> 0x1930 }
            r1.access_hash = r11     // Catch:{ Exception -> 0x1930 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r10.media     // Catch:{ Exception -> 0x1930 }
            byte[] r8 = r3.key     // Catch:{ Exception -> 0x1930 }
            r4.key = r8     // Catch:{ Exception -> 0x1930 }
            byte[] r3 = r3.iv     // Catch:{ Exception -> 0x1930 }
            r4.iv = r3     // Catch:{ Exception -> 0x1930 }
            org.telegram.messenger.SecretChatHelper r3 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1930 }
            org.telegram.tgnet.TLRPC$Message r4 = r7.messageOwner     // Catch:{ Exception -> 0x1930 }
            r8 = 0
            r58 = r3
            r59 = r10
            r60 = r4
            r61 = r9
            r62 = r1
            r63 = r8
            r64 = r7
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1930 }
            r11 = r77
            r3 = r5
            goto L_0x19fe
        L_0x1996:
            if (r5 != 0) goto L_0x19cb
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1930 }
            r1.<init>(r14)     // Catch:{ Exception -> 0x1930 }
            r1.encryptedChat = r9     // Catch:{ Exception -> 0x1930 }
            r4 = 0
            r1.type = r4     // Catch:{ Exception -> 0x1930 }
            r1.originalPath = r6     // Catch:{ Exception -> 0x1930 }
            r1.sendEncryptedRequest = r10     // Catch:{ Exception -> 0x1930 }
            r1.obj = r7     // Catch:{ Exception -> 0x1930 }
            if (r3 == 0) goto L_0x19b9
            r4 = r41
            boolean r5 = r3.containsKey(r4)     // Catch:{ Exception -> 0x1930 }
            if (r5 == 0) goto L_0x19b9
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x1930 }
            r1.parentObject = r3     // Catch:{ Exception -> 0x1930 }
            goto L_0x19bd
        L_0x19b9:
            r5 = r18
            r1.parentObject = r5     // Catch:{ Exception -> 0x1930 }
        L_0x19bd:
            r3 = 1
            r1.performMediaUpload = r3     // Catch:{ Exception -> 0x1930 }
            r11 = r77
            if (r11 == 0) goto L_0x19c6
            r3 = 1
            goto L_0x19c7
        L_0x19c6:
            r3 = 0
        L_0x19c7:
            r1.scheduled = r3     // Catch:{ Exception -> 0x1706 }
            r3 = r1
            goto L_0x19ce
        L_0x19cb:
            r11 = r77
            r3 = r5
        L_0x19ce:
            boolean r1 = android.text.TextUtils.isEmpty(r67)     // Catch:{ Exception -> 0x1a68 }
            if (r1 != 0) goto L_0x19e3
            r1 = r67
            r4 = r44
            r5 = r45
            boolean r5 = r1.startsWith(r5)     // Catch:{ Exception -> 0x1706 }
            if (r5 == 0) goto L_0x19e5
            r3.httpLocation = r1     // Catch:{ Exception -> 0x1706 }
            goto L_0x19f7
        L_0x19e3:
            r4 = r44
        L_0x19e5:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.sizes     // Catch:{ Exception -> 0x1a68 }
            int r5 = r1.size()     // Catch:{ Exception -> 0x1a68 }
            r8 = 1
            int r5 = r5 - r8
            java.lang.Object r1 = r1.get(r5)     // Catch:{ Exception -> 0x1a68 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = (org.telegram.tgnet.TLRPC$PhotoSize) r1     // Catch:{ Exception -> 0x1a68 }
            r3.photoSize = r1     // Catch:{ Exception -> 0x1a68 }
            r3.locationParent = r4     // Catch:{ Exception -> 0x1a68 }
        L_0x19f7:
            int r1 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r1 != 0) goto L_0x19fe
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x1706 }
        L_0x19fe:
            int r1 = (r42 > r16 ? 1 : (r42 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1a4e
            org.telegram.tgnet.TLObject r1 = r3.sendEncryptedRequest     // Catch:{ Exception -> 0x1a49 }
            if (r1 == 0) goto L_0x1a09
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r1 = (org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia) r1     // Catch:{ Exception -> 0x1706 }
            goto L_0x1a10
        L_0x1a09:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia     // Catch:{ Exception -> 0x1a49 }
            r1.<init>()     // Catch:{ Exception -> 0x1a49 }
            r3.sendEncryptedRequest = r1     // Catch:{ Exception -> 0x1a49 }
        L_0x1a10:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r3.messageObjects     // Catch:{ Exception -> 0x1a49 }
            r4.add(r7)     // Catch:{ Exception -> 0x1a49 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r3.messages     // Catch:{ Exception -> 0x1a49 }
            r5 = r19
            r4.add(r5)     // Catch:{ Exception -> 0x1a46 }
            java.util.ArrayList<java.lang.String> r4 = r3.originalPaths     // Catch:{ Exception -> 0x1a46 }
            r4.add(r6)     // Catch:{ Exception -> 0x1a46 }
            r4 = 1
            r3.performMediaUpload = r4     // Catch:{ Exception -> 0x1a46 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r4 = r1.messages     // Catch:{ Exception -> 0x1a46 }
            r4.add(r10)     // Catch:{ Exception -> 0x1a46 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r4 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1a46 }
            r4.<init>()     // Catch:{ Exception -> 0x1a46 }
            r6 = r70
            r8 = 3
            if (r6 == r8) goto L_0x1a36
            r8 = 7
            if (r6 != r8) goto L_0x1a38
        L_0x1a36:
            r16 = 1
        L_0x1a38:
            r8 = r16
            r4.id = r8     // Catch:{ Exception -> 0x1a46 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r1 = r1.files     // Catch:{ Exception -> 0x1a46 }
            r1.add(r4)     // Catch:{ Exception -> 0x1a46 }
            r2.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x1a46 }
            r1 = r5
            goto L_0x1a50
        L_0x1a46:
            r0 = move-exception
            goto L_0x02f1
        L_0x1a49:
            r0 = move-exception
            r5 = r19
            goto L_0x02f1
        L_0x1a4e:
            r1 = r19
        L_0x1a50:
            if (r72 != 0) goto L_0x1bf5
            org.telegram.messenger.MediaDataController r3 = r55.getMediaDataController()     // Catch:{ Exception -> 0x1ba3 }
            if (r69 == 0) goto L_0x1a5d
            int r4 = r69.getId()     // Catch:{ Exception -> 0x1ba3 }
            goto L_0x1a5e
        L_0x1a5d:
            r4 = 0
        L_0x1a5e:
            r5 = 0
            r3.cleanDraft(r14, r4, r5)     // Catch:{ Exception -> 0x1ba3 }
            goto L_0x1bf5
        L_0x1a64:
            r0 = move-exception
            r11 = r77
            goto L_0x1a69
        L_0x1a68:
            r0 = move-exception
        L_0x1a69:
            r1 = r19
            goto L_0x1baa
        L_0x1a6d:
            r0 = move-exception
            r11 = r77
            r1 = r5
            goto L_0x02f1
        L_0x1a73:
            r4 = r56
            r8 = r59
            r5 = r63
            r3 = r1
            r6 = r7
            r1 = r12
            r11 = r13
            r7 = r2
            r2 = r10
            r10 = r73
            if (r9 != 0) goto L_0x1af9
            org.telegram.tgnet.TLRPC$TL_messages_sendMessage r8 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage     // Catch:{ Exception -> 0x1ba3 }
            r8.<init>()     // Catch:{ Exception -> 0x1ba3 }
            r8.message = r4     // Catch:{ Exception -> 0x1ba3 }
            if (r72 != 0) goto L_0x1a8e
            r4 = 1
            goto L_0x1a8f
        L_0x1a8e:
            r4 = 0
        L_0x1a8f:
            r8.clear_draft = r4     // Catch:{ Exception -> 0x1ba3 }
            boolean r4 = r1.silent     // Catch:{ Exception -> 0x1ba3 }
            r8.silent = r4     // Catch:{ Exception -> 0x1ba3 }
            r8.peer = r6     // Catch:{ Exception -> 0x1ba3 }
            long r12 = r1.random_id     // Catch:{ Exception -> 0x1ba3 }
            r8.random_id = r12     // Catch:{ Exception -> 0x1ba3 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = r1.reply_to     // Catch:{ Exception -> 0x1ba3 }
            if (r4 == 0) goto L_0x1aab
            int r4 = r4.reply_to_msg_id     // Catch:{ Exception -> 0x1ba3 }
            if (r4 == 0) goto L_0x1aab
            int r6 = r8.flags     // Catch:{ Exception -> 0x1ba3 }
            r9 = 1
            r6 = r6 | r9
            r8.flags = r6     // Catch:{ Exception -> 0x1ba3 }
            r8.reply_to_msg_id = r4     // Catch:{ Exception -> 0x1ba3 }
        L_0x1aab:
            if (r71 != 0) goto L_0x1ab0
            r4 = 1
            r8.no_webpage = r4     // Catch:{ Exception -> 0x1ba3 }
        L_0x1ab0:
            if (r10 == 0) goto L_0x1ac1
            boolean r4 = r73.isEmpty()     // Catch:{ Exception -> 0x1ba3 }
            if (r4 != 0) goto L_0x1ac1
            r8.entities = r10     // Catch:{ Exception -> 0x1ba3 }
            int r4 = r8.flags     // Catch:{ Exception -> 0x1ba3 }
            r6 = 8
            r4 = r4 | r6
            r8.flags = r4     // Catch:{ Exception -> 0x1ba3 }
        L_0x1ac1:
            if (r11 == 0) goto L_0x1acb
            r8.schedule_date = r11     // Catch:{ Exception -> 0x1ba3 }
            int r4 = r8.flags     // Catch:{ Exception -> 0x1ba3 }
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r8.flags = r4     // Catch:{ Exception -> 0x1ba3 }
        L_0x1acb:
            r4 = 0
            r6 = 0
            if (r11 == 0) goto L_0x1ad1
            r9 = 1
            goto L_0x1ad2
        L_0x1ad1:
            r9 = 0
        L_0x1ad2:
            r56 = r55
            r57 = r8
            r58 = r7
            r59 = r4
            r60 = r6
            r61 = r5
            r62 = r3
            r63 = r9
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x1ba3 }
            if (r72 != 0) goto L_0x1bf5
            org.telegram.messenger.MediaDataController r3 = r55.getMediaDataController()     // Catch:{ Exception -> 0x1ba3 }
            if (r69 == 0) goto L_0x1af2
            int r4 = r69.getId()     // Catch:{ Exception -> 0x1ba3 }
            goto L_0x1af3
        L_0x1af2:
            r4 = 0
        L_0x1af3:
            r5 = 0
            r3.cleanDraft(r14, r4, r5)     // Catch:{ Exception -> 0x1ba3 }
            goto L_0x1bf5
        L_0x1af9:
            int r5 = r9.layer     // Catch:{ Exception -> 0x1ba3 }
            int r5 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r5)     // Catch:{ Exception -> 0x1ba3 }
            r6 = 73
            if (r5 < r6) goto L_0x1b09
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1ba3 }
            r5.<init>()     // Catch:{ Exception -> 0x1ba3 }
            goto L_0x1b0e
        L_0x1b09:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x1ba3 }
            r5.<init>()     // Catch:{ Exception -> 0x1ba3 }
        L_0x1b0e:
            int r6 = r1.ttl     // Catch:{ Exception -> 0x1ba3 }
            r5.ttl = r6     // Catch:{ Exception -> 0x1ba3 }
            if (r10 == 0) goto L_0x1b22
            boolean r6 = r73.isEmpty()     // Catch:{ Exception -> 0x1ba3 }
            if (r6 != 0) goto L_0x1b22
            r5.entities = r10     // Catch:{ Exception -> 0x1ba3 }
            int r6 = r5.flags     // Catch:{ Exception -> 0x1ba3 }
            r6 = r6 | 128(0x80, float:1.794E-43)
            r5.flags = r6     // Catch:{ Exception -> 0x1ba3 }
        L_0x1b22:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r6 = r1.reply_to     // Catch:{ Exception -> 0x1ba3 }
            if (r6 == 0) goto L_0x1b35
            long r12 = r6.reply_to_random_id     // Catch:{ Exception -> 0x1ba3 }
            int r6 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r6 == 0) goto L_0x1b35
            r5.reply_to_random_id = r12     // Catch:{ Exception -> 0x1ba3 }
            int r6 = r5.flags     // Catch:{ Exception -> 0x1ba3 }
            r10 = 8
            r6 = r6 | r10
            r5.flags = r6     // Catch:{ Exception -> 0x1ba3 }
        L_0x1b35:
            if (r3 == 0) goto L_0x1b4b
            java.lang.Object r6 = r3.get(r8)     // Catch:{ Exception -> 0x1ba3 }
            if (r6 == 0) goto L_0x1b4b
            java.lang.Object r3 = r3.get(r8)     // Catch:{ Exception -> 0x1ba3 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x1ba3 }
            r5.via_bot_name = r3     // Catch:{ Exception -> 0x1ba3 }
            int r3 = r5.flags     // Catch:{ Exception -> 0x1ba3 }
            r3 = r3 | 2048(0x800, float:2.87E-42)
            r5.flags = r3     // Catch:{ Exception -> 0x1ba3 }
        L_0x1b4b:
            boolean r3 = r1.silent     // Catch:{ Exception -> 0x1ba3 }
            r5.silent = r3     // Catch:{ Exception -> 0x1ba3 }
            long r12 = r1.random_id     // Catch:{ Exception -> 0x1ba3 }
            r5.random_id = r12     // Catch:{ Exception -> 0x1ba3 }
            r5.message = r4     // Catch:{ Exception -> 0x1ba3 }
            r3 = r37
            if (r3 == 0) goto L_0x1b6f
            java.lang.String r4 = r3.url     // Catch:{ Exception -> 0x1ba3 }
            if (r4 == 0) goto L_0x1b6f
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage     // Catch:{ Exception -> 0x1ba3 }
            r4.<init>()     // Catch:{ Exception -> 0x1ba3 }
            r5.media = r4     // Catch:{ Exception -> 0x1ba3 }
            java.lang.String r3 = r3.url     // Catch:{ Exception -> 0x1ba3 }
            r4.url = r3     // Catch:{ Exception -> 0x1ba3 }
            int r3 = r5.flags     // Catch:{ Exception -> 0x1ba3 }
            r3 = r3 | 512(0x200, float:7.175E-43)
            r5.flags = r3     // Catch:{ Exception -> 0x1ba3 }
            goto L_0x1b76
        L_0x1b6f:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty     // Catch:{ Exception -> 0x1ba3 }
            r3.<init>()     // Catch:{ Exception -> 0x1ba3 }
            r5.media = r3     // Catch:{ Exception -> 0x1ba3 }
        L_0x1b76:
            org.telegram.messenger.SecretChatHelper r3 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1ba3 }
            org.telegram.tgnet.TLRPC$Message r4 = r7.messageOwner     // Catch:{ Exception -> 0x1ba3 }
            r6 = 0
            r8 = 0
            r58 = r3
            r59 = r5
            r60 = r4
            r61 = r9
            r62 = r6
            r63 = r8
            r64 = r7
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1ba3 }
            if (r72 != 0) goto L_0x1bf5
            org.telegram.messenger.MediaDataController r3 = r55.getMediaDataController()     // Catch:{ Exception -> 0x1ba3 }
            if (r69 == 0) goto L_0x1b9c
            int r4 = r69.getId()     // Catch:{ Exception -> 0x1ba3 }
            goto L_0x1b9d
        L_0x1b9c:
            r4 = 0
        L_0x1b9d:
            r5 = 0
            r3.cleanDraft(r14, r4, r5)     // Catch:{ Exception -> 0x1ba3 }
            goto L_0x1bf5
        L_0x1ba3:
            r0 = move-exception
            goto L_0x1baa
        L_0x1ba5:
            r0 = move-exception
            r7 = r2
            r2 = r10
            r1 = r12
            r11 = r13
        L_0x1baa:
            r5 = r1
            goto L_0x02f1
        L_0x1bad:
            r0 = move-exception
            r2 = r10
            r1 = r12
            r11 = r13
            goto L_0x1bbb
        L_0x1bb2:
            r0 = move-exception
            r11 = r77
            r2 = r6
            goto L_0x1bba
        L_0x1bb7:
            r0 = move-exception
            r2 = r6
            r11 = r7
        L_0x1bba:
            r1 = r12
        L_0x1bbb:
            r5 = r1
            goto L_0x02f0
        L_0x1bbe:
            r0 = move-exception
            r2 = r6
            r11 = r7
            r1 = r0
            r5 = r4
            goto L_0x02fa
        L_0x1bc5:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            org.telegram.messenger.MessagesStorage r1 = r55.getMessagesStorage()
            if (r11 == 0) goto L_0x1bd0
            r4 = 1
            goto L_0x1bd1
        L_0x1bd0:
            r4 = 0
        L_0x1bd1:
            r1.markMessageAsSendError(r5, r4)
            if (r7 == 0) goto L_0x1bdb
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            r3 = 2
            r1.send_state = r3
        L_0x1bdb:
            org.telegram.messenger.NotificationCenter r1 = r55.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.messageSendError
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r6 = r5.id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r7 = 0
            r4[r7] = r6
            r1.postNotificationName(r3, r4)
            int r1 = r5.id
            r2.processSentMessage(r1)
        L_0x1bf5:
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
                if (tLRPC$PhotoSize == null || (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) || (tLRPC$PhotoSize instanceof TLRPC$TL_photoPathSize) || (tLRPC$PhotoSize instanceof TLRPC$TL_photoSizeEmpty) || tLRPC$PhotoSize.location == null) {
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
                    TLRPC$InputFile tLRPC$InputFile = videoEditedInfo2.file;
                    if (tLRPC$InputFile != null) {
                        TLObject tLObject = delayedMessage2.sendRequest;
                        if (tLObject instanceof TLRPC$TL_messages_sendMedia) {
                            tLRPC$InputMedia3 = ((TLRPC$TL_messages_sendMedia) tLObject).media;
                        } else {
                            tLRPC$InputMedia3 = ((TLRPC$TL_messages_editMessage) tLObject).media;
                        }
                        tLRPC$InputMedia3.file = tLRPC$InputFile;
                        videoEditedInfo2.file = null;
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
                        VideoEditedInfo videoEditedInfo3 = delayedMessage2.obj.videoEditedInfo;
                        if (videoEditedInfo3 == null || !videoEditedInfo3.needConvert()) {
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
                if (delayedMessage2.sendEncryptedRequest == null || document2.dc_id == 0 || new File(str5).exists()) {
                    putToDelayedMessages(str5, delayedMessage2);
                    VideoEditedInfo videoEditedInfo4 = delayedMessage2.obj.videoEditedInfo;
                    if (videoEditedInfo4 == null || !videoEditedInfo4.needConvert()) {
                        getFileLoader().uploadFile(str5, true, false, 33554432);
                    } else {
                        getFileLoader().uploadFile(str5, true, false, document2.size, 33554432);
                    }
                    putToUploadingMessages(delayedMessage2.obj);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(document2), delayedMessage2);
                getFileLoader().loadFile(document2, delayedMessage2.parentObject, 2, 0);
                return;
            }
            MessageObject messageObject5 = delayedMessage2.obj;
            String str6 = messageObject5.messageOwner.attachPath;
            TLRPC$Document document3 = messageObject5.getDocument();
            if (str6 == null) {
                str6 = FileLoader.getDirectory(4) + "/" + document3.id + ".mp4";
            }
            putToDelayedMessages(str6, delayedMessage2);
            MediaController.getInstance().scheduleVideoConvert(delayedMessage2.obj);
            putToUploadingMessages(delayedMessage2.obj);
        } else if (i2 == 2) {
            String str7 = delayedMessage2.httpLocation;
            if (str7 != null) {
                putToDelayedMessages(str7, delayedMessage2);
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
                    String str8 = delayedMessage2.obj.messageOwner.attachPath;
                    putToDelayedMessages(str8, delayedMessage2);
                    FileLoader fileLoader = getFileLoader();
                    if (delayedMessage2.sendRequest != null) {
                        z4 = false;
                    }
                    fileLoader.uploadFile(str8, z4, false, 67108864);
                    putToUploadingMessages(delayedMessage2.obj);
                } else if (tLRPC$InputMedia.thumb == null && delayedMessage2.photoSize != null) {
                    String str9 = FileLoader.getDirectory(4) + "/" + delayedMessage2.photoSize.location.volume_id + "_" + delayedMessage2.photoSize.location.local_id + ".jpg";
                    putToDelayedMessages(str9, delayedMessage2);
                    getFileLoader().uploadFile(str9, false, true, 16777216);
                    putToUploadingMessages(delayedMessage2.obj);
                }
            } else {
                MessageObject messageObject6 = delayedMessage2.obj;
                String str10 = messageObject6.messageOwner.attachPath;
                TLRPC$Document document4 = messageObject6.getDocument();
                if (delayedMessage2.sendEncryptedRequest == null || document4.dc_id == 0 || new File(str10).exists()) {
                    putToDelayedMessages(str10, delayedMessage2);
                    getFileLoader().uploadFile(str10, true, false, 67108864);
                    putToUploadingMessages(delayedMessage2.obj);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(document4), delayedMessage2);
                getFileLoader().loadFile(document4, delayedMessage2.parentObject, 2, 0);
            }
        } else if (i2 == 3) {
            String str11 = delayedMessage2.obj.messageOwner.attachPath;
            putToDelayedMessages(str11, delayedMessage2);
            FileLoader fileLoader2 = getFileLoader();
            if (delayedMessage2.sendRequest == null) {
                z3 = true;
            }
            fileLoader2.uploadFile(str11, z3, true, 50331648);
            putToUploadingMessages(delayedMessage2.obj);
        } else if (i2 == 4) {
            boolean z5 = i < 0;
            if (delayedMessage2.performMediaUpload) {
                int size = i < 0 ? delayedMessage2.messageObjects.size() - 1 : i;
                MessageObject messageObject7 = delayedMessage2.messageObjects.get(size);
                if (messageObject7.getDocument() != null) {
                    if (delayedMessage2.videoEditedInfo != null) {
                        String str12 = messageObject7.messageOwner.attachPath;
                        TLRPC$Document document5 = messageObject7.getDocument();
                        if (str12 == null) {
                            str12 = FileLoader.getDirectory(4) + "/" + document5.id + ".mp4";
                        }
                        putToDelayedMessages(str12, delayedMessage2);
                        delayedMessage2.extraHashMap.put(messageObject7, str12);
                        delayedMessage2.extraHashMap.put(str12 + "_i", messageObject7);
                        TLRPC$PhotoSize tLRPC$PhotoSize = delayedMessage2.photoSize;
                        if (!(tLRPC$PhotoSize == null || tLRPC$PhotoSize.location == null)) {
                            delayedMessage2.extraHashMap.put(str12 + "_t", delayedMessage2.photoSize);
                        }
                        MediaController.getInstance().scheduleVideoConvert(messageObject7);
                        delayedMessage2.obj = messageObject7;
                        putToUploadingMessages(messageObject7);
                    } else {
                        TLRPC$Document document6 = messageObject7.getDocument();
                        String str13 = messageObject7.messageOwner.attachPath;
                        if (str13 == null) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(FileLoader.getDirectory(4));
                            sb.append("/");
                            messageObject = messageObject7;
                            sb.append(document6.id);
                            sb.append(".mp4");
                            str13 = sb.toString();
                        } else {
                            messageObject = messageObject7;
                        }
                        TLObject tLObject4 = delayedMessage2.sendRequest;
                        if (tLObject4 != null) {
                            TLRPC$InputMedia tLRPC$InputMedia4 = ((TLRPC$TL_messages_sendMultiMedia) tLObject4).multi_media.get(size).media;
                            if (tLRPC$InputMedia4.file == null) {
                                putToDelayedMessages(str13, delayedMessage2);
                                MessageObject messageObject8 = messageObject;
                                delayedMessage2.extraHashMap.put(messageObject8, str13);
                                delayedMessage2.extraHashMap.put(str13, tLRPC$InputMedia4);
                                delayedMessage2.extraHashMap.put(str13 + "_i", messageObject8);
                                TLRPC$PhotoSize tLRPC$PhotoSize2 = delayedMessage2.photoSize;
                                if (!(tLRPC$PhotoSize2 == null || tLRPC$PhotoSize2.location == null)) {
                                    delayedMessage2.extraHashMap.put(str13 + "_t", delayedMessage2.photoSize);
                                }
                                VideoEditedInfo videoEditedInfo5 = messageObject8.videoEditedInfo;
                                if (videoEditedInfo5 == null || !videoEditedInfo5.needConvert()) {
                                    getFileLoader().uploadFile(str13, false, false, 33554432);
                                } else {
                                    getFileLoader().uploadFile(str13, false, false, document6.size, 33554432);
                                }
                                putToUploadingMessages(messageObject8);
                            } else {
                                MessageObject messageObject9 = messageObject;
                                if (delayedMessage2.photoSize != null) {
                                    String str14 = FileLoader.getDirectory(4) + "/" + delayedMessage2.photoSize.location.volume_id + "_" + delayedMessage2.photoSize.location.local_id + ".jpg";
                                    putToDelayedMessages(str14, delayedMessage2);
                                    delayedMessage2.extraHashMap.put(str14 + "_o", str13);
                                    delayedMessage2.extraHashMap.put(messageObject9, str14);
                                    delayedMessage2.extraHashMap.put(str14, tLRPC$InputMedia4);
                                    getFileLoader().uploadFile(str14, false, true, 16777216);
                                    putToUploadingMessages(messageObject9);
                                }
                            }
                        } else {
                            MessageObject messageObject10 = messageObject;
                            putToDelayedMessages(str13, delayedMessage2);
                            delayedMessage2.extraHashMap.put(messageObject10, str13);
                            delayedMessage2.extraHashMap.put(str13, ((TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage2.sendEncryptedRequest).files.get(size));
                            delayedMessage2.extraHashMap.put(str13 + "_i", messageObject10);
                            TLRPC$PhotoSize tLRPC$PhotoSize3 = delayedMessage2.photoSize;
                            if (!(tLRPC$PhotoSize3 == null || tLRPC$PhotoSize3.location == null)) {
                                delayedMessage2.extraHashMap.put(str13 + "_t", delayedMessage2.photoSize);
                            }
                            VideoEditedInfo videoEditedInfo6 = messageObject10.videoEditedInfo;
                            if (videoEditedInfo6 == null || !videoEditedInfo6.needConvert()) {
                                getFileLoader().uploadFile(str13, true, false, 33554432);
                            } else {
                                getFileLoader().uploadFile(str13, true, false, document6.size, 33554432);
                            }
                            putToUploadingMessages(messageObject10);
                        }
                    }
                    delayedMessage2.videoEditedInfo = null;
                    delayedMessage2.photoSize = null;
                } else {
                    String str15 = delayedMessage2.httpLocation;
                    if (str15 != null) {
                        putToDelayedMessages(str15, delayedMessage2);
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
            String str16 = "stickerset_" + delayedMessage2.obj.getId();
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            tLRPC$TL_messages_getStickerSet.stickerset = (TLRPC$InputStickerSet) delayedMessage2.parentObject;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new RequestDelegate(delayedMessage2, str16) {
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
            putToDelayedMessages(str16, delayedMessage2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$performSendDelayedMessage$32 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$31 */
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
        Boolean bool = Boolean.FALSE;
        if (tLRPC$InputMedia != null) {
            TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = (TLRPC$TL_messages_sendMultiMedia) delayedMessage.sendRequest;
            int i = 0;
            while (true) {
                if (i >= tLRPC$TL_messages_sendMultiMedia.multi_media.size()) {
                    break;
                } else if (tLRPC$TL_messages_sendMultiMedia.multi_media.get(i).media == tLRPC$InputMedia) {
                    putToSendingMessages(delayedMessage.messages.get(i), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, -1L, -1L, bool);
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
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, -1L, -1L, bool);
                    break;
                } else {
                    i2++;
                }
            }
            sendReadyToSendGroup(delayedMessage, false, true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$uploadMultiMedia$34 */
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
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0085  */
    /* renamed from: lambda$null$33 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$35 */
    public /* synthetic */ void lambda$null$35$SendMessagesHelper(String str) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, str, Integer.valueOf(this.currentAccount));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$stopVideoService$36 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$putToSendingMessages$37 */
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
        $$Lambda$SendMessagesHelper$J5g75M4OvnNcUJEwRvnGtbytqM r2 = new RequestDelegate(arrayList3, tLRPC$TL_messages_sendMultiMedia, arrayList, arrayList2, delayedMessage, z) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$performSendMessageRequestMulti$45 */
    public /* synthetic */ void lambda$performSendMessageRequestMulti$45$SendMessagesHelper(ArrayList arrayList, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList arrayList4 = arrayList;
        DelayedMessage delayedMessage2 = delayedMessage;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        if (tLRPC$TL_error2 != null && FileRefController.isFileRefError(tLRPC$TL_error2.text)) {
            if (arrayList4 != null) {
                ArrayList arrayList5 = new ArrayList(arrayList);
                getFileRefController().requestReference(arrayList5, tLRPC$TL_messages_sendMultiMedia, arrayList2, arrayList3, arrayList5, delayedMessage2, Boolean.valueOf(z));
                return;
            } else if (delayedMessage2 != null && !delayedMessage2.retriedToSend) {
                delayedMessage2.retriedToSend = true;
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$38 */
    public /* synthetic */ void lambda$null$38$SendMessagesHelper(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, DelayedMessage delayedMessage, ArrayList arrayList, boolean z) {
        int size = tLRPC$TL_messages_sendMultiMedia.multi_media.size();
        boolean z2 = false;
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
                TLRPC$PhotoSize tLRPC$PhotoSize = delayedMessage.locations.get(i);
                delayedMessage.photoSize = tLRPC$PhotoSize;
                delayedMessage.performMediaUpload = true;
                if (tLRPC$TL_inputSingleMedia.media.file == null || tLRPC$PhotoSize != null) {
                    z2 = true;
                }
                performSendDelayedMessage(delayedMessage, i);
            }
        }
        if (!z2) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC$Message tLRPC$Message = ((MessageObject) arrayList.get(i2)).messageOwner;
                getMessagesStorage().markMessageAsSendError(tLRPC$Message, z);
                tLRPC$Message.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message.id));
                processSentMessage(tLRPC$Message.id);
                removeFromSendingMessages(tLRPC$Message.id, z);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$44 */
    public /* synthetic */ void lambda$null$44$SendMessagesHelper(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, ArrayList arrayList, ArrayList arrayList2, boolean z, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia) {
        boolean z2;
        TLRPC$Updates tLRPC$Updates;
        boolean z3;
        TLRPC$Message tLRPC$Message;
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        ArrayList arrayList3 = arrayList;
        boolean z4 = z;
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
                    z3 = false;
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
                    z3 = true;
                } else {
                    MessageObject.getDialogId(tLRPC$Message);
                    arrayList5.add(tLRPC$Message);
                    ArrayList arrayList6 = arrayList5;
                    int i5 = i4;
                    TLRPC$Message tLRPC$Message6 = tLRPC$Message5;
                    updateMediaPaths(messageObject, tLRPC$Message, tLRPC$Message.id, str, false);
                    int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                    tLRPC$Message6.id = tLRPC$Message.id;
                    long j = tLRPC$Message.grouped_id;
                    if (!z4) {
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
                    $$Lambda$SendMessagesHelper$H9lnht4yAq7LDAjcpXpv_zBDzE r15 = r0;
                    $$Lambda$SendMessagesHelper$H9lnht4yAq7LDAjcpXpv_zBDzE r0 = new Runnable(tLRPC$Message6, i5, z, arrayList6, j, mediaExistanceFlags) {
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
            z3 = true;
            Utilities.stageQueue.postRunnable(new Runnable(tLRPC$Updates) {
                public final /* synthetic */ TLRPC$Updates f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    SendMessagesHelper.this.lambda$null$43$SendMessagesHelper(this.f$1);
                }
            });
            z2 = z3;
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error2, (BaseFragment) null, tLRPC$TL_messages_sendMultiMedia, new Object[0]);
            z2 = true;
        }
        if (z2) {
            for (int i6 = 0; i6 < arrayList.size(); i6++) {
                TLRPC$Message tLRPC$Message7 = ((MessageObject) arrayList3.get(i6)).messageOwner;
                getMessagesStorage().markMessageAsSendError(tLRPC$Message7, z4);
                tLRPC$Message7.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message7.id));
                processSentMessage(tLRPC$Message7.id);
                removeFromSendingMessages(tLRPC$Message7.id, z4);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$39 */
    public /* synthetic */ void lambda$null$39$SendMessagesHelper(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$40 */
    public /* synthetic */ void lambda$null$40$SendMessagesHelper(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.peer_id.channel_id);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$42 */
    public /* synthetic */ void lambda$null$42$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, boolean z, ArrayList arrayList, long j, int i2) {
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, Long.valueOf((long) i), tLRPC$Message2.id, 0, false, tLRPC$Message2.peer_id.channel_id, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Message, i, j, i2, z) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$41 */
    public /* synthetic */ void lambda$null$41$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, long j, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), Long.valueOf(j), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$43 */
    public /* synthetic */ void lambda$null$43$SendMessagesHelper(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
    }

    /* access modifiers changed from: private */
    public void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, Object obj, HashMap<String, String> hashMap, boolean z) {
        performSendMessageRequest(tLObject, messageObject, str, (DelayedMessage) null, false, delayedMessage, obj, hashMap, z);
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
    public void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, Object obj, HashMap<String, String> hashMap, boolean z2) {
        DelayedMessage findMaxDelayedMessageForMessageId;
        ArrayList<DelayedMessageSendAfterRequest> arrayList;
        TLObject tLObject2 = tLObject;
        DelayedMessage delayedMessage3 = delayedMessage;
        if ((tLObject2 instanceof TLRPC$TL_messages_editMessage) || !z || (findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(messageObject.getId(), messageObject.getDialogId())) == null) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            boolean z3 = z2;
            putToSendingMessages(tLRPC$Message, z3);
            $$Lambda$SendMessagesHelper$9NoheQ6hOpzcUBgwpqQh1eL0VXk r14 = r0;
            ConnectionsManager connectionsManager = getConnectionsManager();
            $$Lambda$SendMessagesHelper$9NoheQ6hOpzcUBgwpqQh1eL0VXk r0 = new RequestDelegate(tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, z3, tLRPC$Message) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$performSendMessageRequest$59 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$46 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$49 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$48 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$47 */
    public /* synthetic */ void lambda$null$47$SendMessagesHelper(TLRPC$Message tLRPC$Message, boolean z) {
        processSentMessage(tLRPC$Message.id);
        removeFromSendingMessages(tLRPC$Message.id, z);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x027d  */
    /* renamed from: lambda$null$58 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$58$SendMessagesHelper(boolean r28, org.telegram.tgnet.TLRPC$TL_error r29, org.telegram.tgnet.TLRPC$Message r30, org.telegram.tgnet.TLObject r31, org.telegram.messenger.MessageObject r32, java.lang.String r33, org.telegram.tgnet.TLObject r34) {
        /*
            r27 = this;
            r8 = r27
            r9 = r28
            r0 = r29
            r10 = r30
            r1 = r31
            if (r0 != 0) goto L_0x0333
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
            r5 = 4
            if (r4 == 0) goto L_0x00d1
            r4 = r1
            org.telegram.tgnet.TLRPC$TL_updateShortSentMessage r4 = (org.telegram.tgnet.TLRPC$TL_updateShortSentMessage) r4
            r2 = 0
            int r3 = r4.id
            r16 = 0
            r17 = 0
            r0 = r27
            r1 = r32
            r15 = r4
            r4 = r16
            r11 = 4
            r5 = r17
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r0 = r32.getMediaExistanceFlags()
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
            if (r1 == 0) goto L_0x0060
            r10.media = r1
            int r1 = r10.flags
            r1 = r1 | 512(0x200, float:7.175E-43)
            r10.flags = r1
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r30)
        L_0x0060:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r15.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0072
            java.lang.String r1 = r15.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0072
            java.lang.String r1 = r15.message
            r10.message = r1
        L_0x0072:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r10.entities
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0080
            int r1 = r10.flags
            r1 = r1 | 128(0x80, float:1.794E-43)
            r10.flags = r1
        L_0x0080:
            org.telegram.messenger.MessagesController r1 = r27.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            long r2 = r10.dialog_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x00b3
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
        L_0x00b3:
            int r1 = r1.intValue()
            int r2 = r10.id
            if (r1 >= r2) goto L_0x00bd
            r1 = 1
            goto L_0x00be
        L_0x00bd:
            r1 = 0
        L_0x00be:
            r10.unread = r1
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$YdidETimnCaFb_etYUGb8rWzMLo r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$YdidETimnCaFb_etYUGb8rWzMLo
            r2.<init>(r15)
            r1.postRunnable(r2)
            r7.add(r10)
            r12 = r0
            r13 = 0
            goto L_0x0261
        L_0x00d1:
            r11 = 4
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$Updates
            if (r4 == 0) goto L_0x025f
            r15 = r1
            org.telegram.tgnet.TLRPC$Updates r15 = (org.telegram.tgnet.TLRPC$Updates) r15
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r15.updates
            r4 = 0
        L_0x00dc:
            int r5 = r1.size()
            if (r4 >= r5) goto L_0x01a6
            java.lang.Object r5 = r1.get(r4)
            org.telegram.tgnet.TLRPC$Update r5 = (org.telegram.tgnet.TLRPC$Update) r5
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewMessage
            if (r3 == 0) goto L_0x0104
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewMessage) r5
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$0KwhN6PATgYUqj6UddAe3H0TNpk r11 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$0KwhN6PATgYUqj6UddAe3H0TNpk
            r11.<init>(r5)
            r2.postRunnable(r11)
            r1.remove(r4)
        L_0x0100:
            r12 = r3
            r2 = 0
            goto L_0x01a8
        L_0x0104:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage
            if (r2 == 0) goto L_0x018e
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage) r5
            int r2 = org.telegram.messenger.MessagesController.getUpdateChannelId(r5)
            org.telegram.messenger.MessagesController r3 = r27.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r3.getChat(r2)
            if (r2 == 0) goto L_0x0120
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x0179
        L_0x0120:
            org.telegram.tgnet.TLRPC$Message r2 = r5.message
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r2.reply_to
            if (r2 == 0) goto L_0x0179
            int r3 = r2.reply_to_top_id
            if (r3 != 0) goto L_0x012e
            int r2 = r2.reply_to_msg_id
            if (r2 == 0) goto L_0x0179
        L_0x012e:
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            long r12 = org.telegram.messenger.MessageObject.getDialogId(r3)
            int r3 = (int) r12
            java.lang.Object r12 = r2.get(r3)
            android.util.SparseArray r12 = (android.util.SparseArray) r12
            if (r12 != 0) goto L_0x014a
            android.util.SparseArray r12 = new android.util.SparseArray
            r12.<init>()
            r2.put(r3, r12)
        L_0x014a:
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r3.reply_to
            int r13 = r3.reply_to_top_id
            if (r13 == 0) goto L_0x0153
            goto L_0x0155
        L_0x0153:
            int r13 = r3.reply_to_msg_id
        L_0x0155:
            java.lang.Object r3 = r12.get(r13)
            org.telegram.tgnet.TLRPC$TL_messageReplies r3 = (org.telegram.tgnet.TLRPC$TL_messageReplies) r3
            if (r3 != 0) goto L_0x0165
            org.telegram.tgnet.TLRPC$TL_messageReplies r3 = new org.telegram.tgnet.TLRPC$TL_messageReplies
            r3.<init>()
            r12.put(r13, r3)
        L_0x0165:
            org.telegram.tgnet.TLRPC$Message r12 = r5.message
            org.telegram.tgnet.TLRPC$Peer r12 = r12.from_id
            if (r12 == 0) goto L_0x0171
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r13 = r3.recent_repliers
            r11 = 0
            r13.add(r11, r12)
        L_0x0171:
            int r11 = r3.replies
            r12 = 1
            int r13 = r11 + 1
            r3.replies = r13
            goto L_0x017a
        L_0x0179:
            r2 = 0
        L_0x017a:
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            org.telegram.messenger.DispatchQueue r12 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$b3k8Uljhu8nPPFZvNGV-uRWzWHU r13 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$b3k8Uljhu8nPPFZvNGV-uRWzWHU
            r13.<init>(r5)
            r12.postRunnable(r13)
            r1.remove(r4)
            r12 = r3
            goto L_0x01a8
        L_0x018e:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x019e
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage) r5
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            r1.remove(r4)
            goto L_0x0100
        L_0x019e:
            int r4 = r4 + 1
            r3 = 2147483646(0x7ffffffe, float:NaN)
            r11 = 4
            goto L_0x00dc
        L_0x01a6:
            r2 = 0
            r12 = 0
        L_0x01a8:
            if (r2 == 0) goto L_0x01d6
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
            r13 = 0
            r5[r13] = r4
            r11 = 1
            r5[r11] = r4
            r4 = 2
            r5[r4] = r2
            java.lang.Boolean r2 = java.lang.Boolean.TRUE
            r4 = 3
            r5[r4] = r2
            r1.postNotificationName(r3, r5)
        L_0x01d6:
            if (r12 == 0) goto L_0x024f
            org.telegram.messenger.MessageObject.getDialogId(r12)
            if (r0 == 0) goto L_0x01e6
            int r0 = r12.date
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 == r1) goto L_0x01e6
            r13 = 0
            goto L_0x01e7
        L_0x01e6:
            r13 = r9
        L_0x01e7:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r12)
            if (r13 != 0) goto L_0x022c
            org.telegram.messenger.MessagesController r0 = r27.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r0.dialogs_read_outbox_max
            long r1 = r12.dialog_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x021f
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
        L_0x021f:
            int r0 = r0.intValue()
            int r1 = r12.id
            if (r0 >= r1) goto L_0x0229
            r0 = 1
            goto L_0x022a
        L_0x0229:
            r0 = 0
        L_0x022a:
            r12.unread = r0
        L_0x022c:
            r5 = r32
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.lang.String r1 = r12.post_author
            r0.post_author = r1
            int r3 = r12.id
            r17 = 0
            r0 = r27
            r1 = r32
            r2 = r12
            r4 = r33
            r5 = r17
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r0 = r32.getMediaExistanceFlags()
            int r1 = r12.id
            r10.id = r1
            r1 = r0
            r0 = 0
            goto L_0x0252
        L_0x024f:
            r13 = r9
            r0 = 1
            r1 = 0
        L_0x0252:
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$mwEau-HZPWENJiNNxHVtGvC3aNY r3 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$mwEau-HZPWENJiNNxHVtGvC3aNY
            r3.<init>(r15)
            r2.postRunnable(r3)
            r15 = r0
            r12 = r1
            goto L_0x0262
        L_0x025f:
            r13 = r9
            r12 = 0
        L_0x0261:
            r15 = 0
        L_0x0262:
            boolean r0 = org.telegram.messenger.MessageObject.isLiveLocationMessage(r30)
            if (r0 == 0) goto L_0x027b
            int r0 = r10.via_bot_id
            if (r0 != 0) goto L_0x027b
            java.lang.String r0 = r10.via_bot_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x027b
            org.telegram.messenger.LocationController r0 = r27.getLocationController()
            r0.addSharingLocation(r10)
        L_0x027b:
            if (r15 != 0) goto L_0x033f
            org.telegram.messenger.StatsController r0 = r27.getStatsController()
            int r1 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r2 = 1
            r0.incrementSentItemsCount(r1, r2, r2)
            r0 = 0
            r10.send_state = r0
            if (r9 == 0) goto L_0x02d5
            if (r13 != 0) goto L_0x02d5
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
            org.telegram.messenger.DispatchQueue r12 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$Zbr5OZxbp9Y07apnnHVz6z6H6IU r13 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Zbr5OZxbp9Y07apnnHVz6z6H6IU
            r0 = r13
            r1 = r27
            r2 = r7
            r3 = r32
            r4 = r30
            r5 = r6
            r6 = r28
            r7 = r14
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r12.postRunnable(r13)
            goto L_0x033f
        L_0x02d5:
            org.telegram.messenger.NotificationCenter r0 = r27.getNotificationCenter()
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
            long r3 = r10.dialog_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r4 = 3
            r2[r4] = r3
            r3 = 0
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r4 = 4
            r2[r4] = r3
            r3 = 5
            java.lang.Integer r4 = java.lang.Integer.valueOf(r12)
            r2[r3] = r4
            r3 = 6
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r28)
            r2[r3] = r4
            r0.postNotificationName(r1, r2)
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r13 = r0.getStorageQueue()
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$d-FCxrE6hEFkYcqEPfwNZ6zX5Tk r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$d-FCxrE6hEFkYcqEPfwNZ6zX5Tk
            r0 = r5
            r1 = r27
            r2 = r30
            r3 = r6
            r4 = r28
            r6 = r5
            r5 = r7
            r7 = r6
            r6 = r12
            r12 = r7
            r7 = r14
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r13.postRunnable(r12)
            goto L_0x033f
        L_0x0333:
            int r1 = r8.currentAccount
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r34
            r4 = 0
            org.telegram.ui.Components.AlertsCreator.processError(r1, r0, r4, r2, r3)
            r15 = 1
        L_0x033f:
            if (r15 == 0) goto L_0x0381
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
            r4 = 0
            r2[r4] = r3
            r0.postNotificationName(r1, r2)
            int r0 = r10.id
            r8.processSentMessage(r0)
            boolean r0 = org.telegram.messenger.MessageObject.isVideoMessage(r30)
            if (r0 != 0) goto L_0x0377
            boolean r0 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r30)
            if (r0 != 0) goto L_0x0377
            boolean r0 = org.telegram.messenger.MessageObject.isNewGifMessage(r30)
            if (r0 == 0) goto L_0x037c
        L_0x0377:
            java.lang.String r0 = r10.attachPath
            r8.stopVideoService(r0)
        L_0x037c:
            int r0 = r10.id
            r8.removeFromSendingMessages(r0, r9)
        L_0x0381:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$58$SendMessagesHelper(boolean, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, java.lang.String, org.telegram.tgnet.TLObject):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$50 */
    public /* synthetic */ void lambda$null$50$SendMessagesHelper(TLRPC$TL_updateShortSentMessage tLRPC$TL_updateShortSentMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateShortSentMessage.pts, tLRPC$TL_updateShortSentMessage.date, tLRPC$TL_updateShortSentMessage.pts_count);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$51 */
    public /* synthetic */ void lambda$null$51$SendMessagesHelper(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$52 */
    public /* synthetic */ void lambda$null$52$SendMessagesHelper(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.peer_id.channel_id);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$53 */
    public /* synthetic */ void lambda$null$53$SendMessagesHelper(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$55 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$54 */
    public /* synthetic */ void lambda$null$54$SendMessagesHelper(MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true, true));
        getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$57 */
    public /* synthetic */ void lambda$null$57$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, Long.valueOf((long) i), tLRPC$Message2.id, 0, false, tLRPC$Message2.peer_id.channel_id, z ? 1 : 0);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$56 */
    public /* synthetic */ void lambda$null$56$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), 0L, Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$performSendMessageRequest$61 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$60 */
    public /* synthetic */ void lambda$null$60$SendMessagesHelper(TLRPC$Message tLRPC$Message, int i) {
        tLRPC$Message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(i));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004c, code lost:
        r5 = (r5 = r7.media).photo;
     */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0107  */
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
            if (r2 == 0) goto L_0x0137
            boolean r2 = r17.isLiveLocation()
            if (r2 == 0) goto L_0x0024
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0024
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            int r2 = r2.period
            r5.period = r2
            goto L_0x00d1
        L_0x0024:
            boolean r2 = r17.isDice()
            if (r2 == 0) goto L_0x0038
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaDice) r2
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaDice) r5
            int r5 = r5.value
            r2.value = r5
            goto L_0x00d1
        L_0x0038:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            r6 = 40
            if (r5 == 0) goto L_0x005e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x0057
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x0057
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            if (r5 == 0) goto L_0x0057
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x0058
        L_0x0057:
            r5 = r2
        L_0x0058:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            goto L_0x00d4
        L_0x005e:
            org.telegram.tgnet.TLRPC$Document r5 = r2.document
            if (r5 == 0) goto L_0x007f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            if (r5 == 0) goto L_0x0079
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x007a
        L_0x0079:
            r5 = r2
        L_0x007a:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            goto L_0x00d4
        L_0x007f:
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            if (r2 == 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            if (r5 == 0) goto L_0x00aa
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x00a2
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x00a2
            org.telegram.tgnet.TLRPC$WebPage r5 = r5.webpage
            if (r5 == 0) goto L_0x00a2
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            if (r5 == 0) goto L_0x00a2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x00a3
        L_0x00a2:
            r5 = r2
        L_0x00a3:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            goto L_0x00d4
        L_0x00aa:
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            if (r2 == 0) goto L_0x00d1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6)
            if (r7 == 0) goto L_0x00c9
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            if (r5 == 0) goto L_0x00c9
            org.telegram.tgnet.TLRPC$WebPage r5 = r5.webpage
            if (r5 == 0) goto L_0x00c9
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            if (r5 == 0) goto L_0x00c9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
            goto L_0x00ca
        L_0x00c9:
            r5 = r2
        L_0x00ca:
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            goto L_0x00d4
        L_0x00d1:
            r2 = 0
            r5 = 0
            r6 = 0
        L_0x00d4:
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r10 == 0) goto L_0x0137
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r2 == 0) goto L_0x0137
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "stripped"
            r2.append(r10)
            java.lang.String r11 = org.telegram.messenger.FileRefController.getKeyForParentObject(r17)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            if (r7 == 0) goto L_0x0107
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            java.lang.String r10 = org.telegram.messenger.FileRefController.getKeyForParentObject(r18)
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            goto L_0x012c
        L_0x0107:
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
        L_0x012c:
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            r11.replaceImageInCache(r2, r10, r5, r1)
        L_0x0137:
            if (r7 != 0) goto L_0x013a
            return
        L_0x013a:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            java.lang.String r6 = "sent_"
            java.lang.String r12 = ".jpg"
            r13 = 4
            r14 = 0
            r15 = 1
            if (r5 == 0) goto L_0x02ec
            org.telegram.tgnet.TLRPC$Photo r5 = r2.photo
            if (r5 == 0) goto L_0x02ec
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x02ec
            org.telegram.tgnet.TLRPC$Photo r3 = r5.photo
            if (r3 == 0) goto L_0x02ec
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x0186
            boolean r0 = r0.scheduled
            if (r0 != 0) goto L_0x0186
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
        L_0x0186:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            int r0 = r0.size()
            if (r0 != r15) goto L_0x01b2
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            java.lang.Object r0 = r0.get(r14)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.location
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r0 == 0) goto L_0x01b2
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            r0.sizes = r1
            goto L_0x02ce
        L_0x01b2:
            r0 = 0
        L_0x01b3:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x02ce
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2
            if (r2 == 0) goto L_0x02c7
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            if (r3 == 0) goto L_0x02c7
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r3 != 0) goto L_0x02c7
            java.lang.String r3 = r2.type
            if (r3 != 0) goto L_0x01db
            goto L_0x02c7
        L_0x01db:
            r3 = 0
        L_0x01dc:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x02c7
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.sizes
            java.lang.Object r5 = r5.get(r3)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5
            if (r5 == 0) goto L_0x02c0
            org.telegram.tgnet.TLRPC$FileLocation r6 = r5.location
            if (r6 == 0) goto L_0x02c0
            java.lang.String r8 = r5.type
            if (r8 != 0) goto L_0x0200
            goto L_0x02c0
        L_0x0200:
            long r14 = r6.volume_id
            int r6 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x020e
            java.lang.String r6 = r2.type
            boolean r6 = r6.equals(r8)
            if (r6 != 0) goto L_0x021a
        L_0x020e:
            int r6 = r2.w
            int r8 = r5.w
            if (r6 != r8) goto L_0x02c0
            int r6 = r2.h
            int r8 = r5.h
            if (r6 != r8) goto L_0x02c0
        L_0x021a:
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
            if (r8 == 0) goto L_0x0255
            goto L_0x02c7
        L_0x0255:
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
            if (r15 != 0) goto L_0x028d
            org.telegram.tgnet.TLRPC$Photo r14 = r14.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r14.sizes
            int r14 = r14.size()
            r15 = 1
            if (r14 == r15) goto L_0x0288
            int r14 = r2.w
            r15 = 90
            if (r14 > r15) goto L_0x0288
            int r14 = r2.h
            if (r14 <= r15) goto L_0x028d
        L_0x0288:
            java.io.File r14 = org.telegram.messenger.FileLoader.getPathToAttach(r2)
            goto L_0x02a5
        L_0x028d:
            java.io.File r14 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r6)
            r13.append(r12)
            java.lang.String r13 = r13.toString()
            r14.<init>(r15, r13)
        L_0x02a5:
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
            goto L_0x02c7
        L_0x02c0:
            int r3 = r3 + 1
            r13 = 4
            r14 = 0
            r15 = 1
            goto L_0x01dc
        L_0x02c7:
            int r0 = r0 + 1
            r13 = 4
            r14 = 0
            r15 = 1
            goto L_0x01b3
        L_0x02ce:
            java.lang.String r0 = r7.message
            r9.message = r0
            java.lang.String r0 = r9.attachPath
            r7.attachPath = r0
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            long r2 = r1.id
            r0.id = r2
            int r2 = r1.dc_id
            r0.dc_id = r2
            long r1 = r1.access_hash
            r0.access_hash = r1
            goto L_0x0626
        L_0x02ec:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r3 == 0) goto L_0x05de
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            if (r3 == 0) goto L_0x05de
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r9.media
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x05de
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            if (r3 == 0) goto L_0x05de
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x039f
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            if (r2 == 0) goto L_0x0318
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r3 = r2.mediaEntities
            if (r3 != 0) goto L_0x039f
            java.lang.String r2 = r2.paintPath
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x039f
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            org.telegram.messenger.MediaController$CropState r2 = r2.cropState
            if (r2 != 0) goto L_0x039f
        L_0x0318:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r18)
            if (r2 != 0) goto L_0x0324
            boolean r3 = org.telegram.messenger.MessageObject.isGifMessage(r18)
            if (r3 == 0) goto L_0x0368
        L_0x0324:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r5)
            if (r3 != r5) goto L_0x0368
            boolean r3 = r0.scheduled
            if (r3 != 0) goto L_0x0361
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
        L_0x0361:
            if (r2 == 0) goto L_0x039f
            java.lang.String r2 = r9.attachPath
            r7.attachPath = r2
            goto L_0x039f
        L_0x0368:
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r18)
            if (r2 != 0) goto L_0x039f
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r18)
            if (r2 != 0) goto L_0x039f
            boolean r2 = r0.scheduled
            if (r2 != 0) goto L_0x039f
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
        L_0x039f:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r3 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r3)
            if (r2 == 0) goto L_0x0455
            org.telegram.tgnet.TLRPC$FileLocation r5 = r2.location
            if (r5 == 0) goto L_0x0455
            long r5 = r5.volume_id
            int r13 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r13 != 0) goto L_0x0455
            if (r3 == 0) goto L_0x0455
            org.telegram.tgnet.TLRPC$FileLocation r5 = r3.location
            if (r5 == 0) goto L_0x0455
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x0455
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x0455
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
            if (r6 != 0) goto L_0x0480
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
            goto L_0x0480
        L_0x0455:
            if (r3 == 0) goto L_0x0466
            if (r2 == 0) goto L_0x0466
            boolean r1 = org.telegram.messenger.MessageObject.isStickerMessage(r18)
            if (r1 == 0) goto L_0x0466
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            if (r1 == 0) goto L_0x0466
            r3.location = r1
            goto L_0x0480
        L_0x0466:
            if (r2 == 0) goto L_0x0474
            if (r2 == 0) goto L_0x0470
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r1 != 0) goto L_0x0474
        L_0x0470:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r1 == 0) goto L_0x0480
        L_0x0474:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r1.thumbs = r2
        L_0x0480:
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
        L_0x0495:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x04b7
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r3 == 0) goto L_0x04b4
            byte[] r3 = r2.waveform
            goto L_0x04b8
        L_0x04b4:
            int r1 = r1 + 1
            goto L_0x0495
        L_0x04b7:
            r3 = 0
        L_0x04b8:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            r1.attributes = r2
            if (r3 == 0) goto L_0x04ee
            r1 = 0
        L_0x04c7:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x04ee
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r4 == 0) goto L_0x04eb
            r2.waveform = r3
            int r4 = r2.flags
            r5 = 4
            r4 = r4 | r5
            r2.flags = r4
        L_0x04eb:
            int r1 = r1 + 1
            goto L_0x04c7
        L_0x04ee:
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
            if (r1 != 0) goto L_0x055c
            boolean r1 = org.telegram.messenger.MessageObject.isOut(r18)
            if (r1 == 0) goto L_0x055c
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r1)
            if (r1 == 0) goto L_0x0536
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isDocumentHasAttachedStickers(r1)
            if (r1 == 0) goto L_0x0525
            org.telegram.messenger.MessagesController r1 = r16.getMessagesController()
            boolean r1 = r1.saveGifsWithStickers
            goto L_0x0526
        L_0x0525:
            r1 = 1
        L_0x0526:
            if (r1 == 0) goto L_0x055c
            org.telegram.messenger.MediaDataController r1 = r16.getMediaDataController()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            int r3 = r7.date
            r1.addRecentGif(r2, r3)
            goto L_0x055c
        L_0x0536:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r1)
            if (r1 != 0) goto L_0x054b
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            r2 = 1
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r2)
            if (r1 == 0) goto L_0x055c
        L_0x054b:
            org.telegram.messenger.MediaDataController r1 = r16.getMediaDataController()
            r2 = 0
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            int r5 = r7.date
            r6 = 0
            r3 = r18
            r1.addRecentSticker(r2, r3, r4, r5, r6)
        L_0x055c:
            java.lang.String r1 = r9.attachPath
            if (r1 == 0) goto L_0x05d5
            r2 = 4
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r2)
            java.lang.String r2 = r2.getAbsolutePath()
            boolean r1 = r1.startsWith(r2)
            if (r1 == 0) goto L_0x05d5
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r9.attachPath
            r1.<init>(r2)
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            int r2 = r2.ttl_seconds
            if (r2 == 0) goto L_0x0580
            r2 = 1
            goto L_0x0581
        L_0x0580:
            r2 = 0
        L_0x0581:
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r2)
            boolean r3 = r1.renameTo(r2)
            if (r3 != 0) goto L_0x05a5
            boolean r1 = r1.exists()
            if (r1 == 0) goto L_0x0596
            java.lang.String r1 = r9.attachPath
            r7.attachPath = r1
            goto L_0x0599
        L_0x0596:
            r1 = 0
            r0.attachPathExists = r1
        L_0x0599:
            boolean r1 = r2.exists()
            r0.mediaExists = r1
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x0626
        L_0x05a5:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoMessage(r18)
            if (r1 == 0) goto L_0x05b0
            r1 = 1
            r0.attachPathExists = r1
            goto L_0x0626
        L_0x05b0:
            boolean r1 = r0.attachPathExists
            r0.mediaExists = r1
            r1 = 0
            r0.attachPathExists = r1
            java.lang.String r0 = ""
            r9.attachPath = r0
            if (r8 == 0) goto L_0x0626
            java.lang.String r0 = "http"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x0626
            org.telegram.messenger.MessagesStorage r0 = r16.getMessagesStorage()
            java.lang.String r1 = r2.toString()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            r0.addRecentLocalFile(r8, r1, r2)
            goto L_0x0626
        L_0x05d5:
            java.lang.String r0 = r9.attachPath
            r7.attachPath = r0
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x0626
        L_0x05de:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r0 == 0) goto L_0x05eb
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r0 == 0) goto L_0x05eb
            r9.media = r2
            goto L_0x0626
        L_0x05eb:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r0 == 0) goto L_0x05f2
            r9.media = r2
            goto L_0x0626
        L_0x05f2:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r0 == 0) goto L_0x0605
            org.telegram.tgnet.TLRPC$GeoPoint r0 = r2.geo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$GeoPoint r1 = r1.geo
            double r2 = r1.lat
            r0.lat = r2
            double r1 = r1._long
            r0._long = r1
            goto L_0x0626
        L_0x0605:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0620
            r9.media = r2
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0626
            java.lang.String r0 = r7.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0626
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r7.entities
            r9.entities = r0
            java.lang.String r0 = r7.message
            r9.message = r0
            goto L_0x0626
        L_0x0620:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0626
            r9.media = r2
        L_0x0626:
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$processUnsentMessages$62 */
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

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0051, code lost:
        if (r3 == false) goto L_0x0055;
     */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0150 A[SYNTHETIC, Splitter:B:105:0x0150] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0165 A[SYNTHETIC, Splitter:B:113:0x0165] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x016f  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x01c1  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0203 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0441  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x0446  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0454  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0468  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0470  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x04af  */
    /* JADX WARNING: Removed duplicated region for block: B:284:0x04b3 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00af  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r29, java.lang.String r30, java.lang.String r31, android.net.Uri r32, java.lang.String r33, long r34, org.telegram.messenger.MessageObject r36, org.telegram.messenger.MessageObject r37, java.lang.CharSequence r38, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r39, org.telegram.messenger.MessageObject r40, long[] r41, boolean r42, boolean r43, boolean r44, int r45, java.lang.Integer[] r46) {
        /*
            r0 = r30
            r1 = r31
            r2 = r32
            r3 = r33
            r4 = 0
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)
            if (r0 == 0) goto L_0x0015
            int r6 = r30.length()
            if (r6 != 0) goto L_0x0018
        L_0x0015:
            if (r2 != 0) goto L_0x0018
            return r4
        L_0x0018:
            if (r2 == 0) goto L_0x0021
            boolean r6 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r32)
            if (r6 == 0) goto L_0x0021
            return r4
        L_0x0021:
            if (r0 == 0) goto L_0x0033
            java.io.File r6 = new java.io.File
            r6.<init>(r0)
            android.net.Uri r6 = android.net.Uri.fromFile(r6)
            boolean r6 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r6)
            if (r6 == 0) goto L_0x0033
            return r4
        L_0x0033:
            android.webkit.MimeTypeMap r6 = android.webkit.MimeTypeMap.getSingleton()
            r15 = 1
            if (r2 == 0) goto L_0x0054
            if (r3 == 0) goto L_0x0041
            java.lang.String r0 = r6.getExtensionFromMimeType(r3)
            goto L_0x0042
        L_0x0041:
            r0 = 0
        L_0x0042:
            if (r0 != 0) goto L_0x0048
            java.lang.String r0 = "txt"
            r3 = 0
            goto L_0x0049
        L_0x0048:
            r3 = 1
        L_0x0049:
            java.lang.String r2 = org.telegram.messenger.MediaController.copyFileToCache(r2, r0)
            if (r2 != 0) goto L_0x0050
            return r4
        L_0x0050:
            r14 = r2
            if (r3 != 0) goto L_0x0056
            goto L_0x0055
        L_0x0054:
            r14 = r0
        L_0x0055:
            r0 = 0
        L_0x0056:
            java.io.File r2 = new java.io.File
            r2.<init>(r14)
            boolean r3 = r2.exists()
            if (r3 == 0) goto L_0x0513
            long r8 = r2.length()
            r12 = 0
            int r3 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r3 != 0) goto L_0x006d
            goto L_0x0513
        L_0x006d:
            r10 = r34
            int r3 = (int) r10
            if (r3 != 0) goto L_0x0074
            r3 = 1
            goto L_0x0075
        L_0x0074:
            r3 = 0
        L_0x0075:
            java.lang.String r9 = r2.getName()
            r8 = -1
            java.lang.String r7 = ""
            if (r0 == 0) goto L_0x0081
        L_0x007e:
            r16 = r0
            goto L_0x0091
        L_0x0081:
            r0 = 46
            int r0 = r14.lastIndexOf(r0)
            if (r0 == r8) goto L_0x008f
            int r0 = r0 + r15
            java.lang.String r0 = r14.substring(r0)
            goto L_0x007e
        L_0x008f:
            r16 = r7
        L_0x0091:
            java.lang.String r11 = r16.toLowerCase()
            java.lang.String r10 = "mp3"
            boolean r0 = r11.equals(r10)
            java.lang.String r4 = "flac"
            java.lang.String r12 = "opus"
            java.lang.String r13 = "m4a"
            java.lang.String r15 = "ogg"
            r17 = r5
            if (r0 != 0) goto L_0x016f
            boolean r0 = r11.equals(r13)
            if (r0 == 0) goto L_0x00af
            goto L_0x016f
        L_0x00af:
            boolean r0 = r11.equals(r12)
            if (r0 != 0) goto L_0x00cc
            boolean r0 = r11.equals(r15)
            if (r0 != 0) goto L_0x00cc
            boolean r0 = r11.equals(r4)
            if (r0 == 0) goto L_0x00c2
            goto L_0x00cc
        L_0x00c2:
            r18 = r6
            r0 = 0
            r5 = 0
            r6 = 0
            r8 = 0
        L_0x00c8:
            r19 = 0
            goto L_0x019a
        L_0x00cc:
            android.media.MediaMetadataRetriever r8 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0142, all -> 0x013e }
            r8.<init>()     // Catch:{ Exception -> 0x0142, all -> 0x013e }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x0139 }
            r8.setDataSource(r0)     // Catch:{ Exception -> 0x0139 }
            r0 = 9
            java.lang.String r0 = r8.extractMetadata(r0)     // Catch:{ Exception -> 0x0139 }
            if (r0 == 0) goto L_0x0107
            r18 = r6
            long r5 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x0105 }
            float r0 = (float) r5     // Catch:{ Exception -> 0x0105 }
            r5 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r5
            double r5 = (double) r0     // Catch:{ Exception -> 0x0105 }
            double r5 = java.lang.Math.ceil(r5)     // Catch:{ Exception -> 0x0105 }
            int r5 = (int) r5
            r0 = 7
            java.lang.String r6 = r8.extractMetadata(r0)     // Catch:{ Exception -> 0x0100 }
            r19 = r5
            r5 = 2
            java.lang.String r0 = r8.extractMetadata(r5)     // Catch:{ Exception -> 0x00fe }
            r5 = r0
            goto L_0x010d
        L_0x00fe:
            r0 = move-exception
            goto L_0x0149
        L_0x0100:
            r0 = move-exception
            r19 = r5
            r6 = 0
            goto L_0x0149
        L_0x0105:
            r0 = move-exception
            goto L_0x013c
        L_0x0107:
            r18 = r6
            r5 = 0
            r6 = 0
            r19 = 0
        L_0x010d:
            if (r40 != 0) goto L_0x0128
            boolean r0 = r11.equals(r15)     // Catch:{ Exception -> 0x0124 }
            if (r0 == 0) goto L_0x0128
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x0124 }
            int r0 = org.telegram.messenger.MediaController.isOpusFile(r0)     // Catch:{ Exception -> 0x0124 }
            r20 = r5
            r5 = 1
            if (r0 != r5) goto L_0x012a
            r5 = 1
            goto L_0x012b
        L_0x0124:
            r0 = move-exception
            r20 = r5
            goto L_0x014b
        L_0x0128:
            r20 = r5
        L_0x012a:
            r5 = 0
        L_0x012b:
            r8.release()     // Catch:{ Exception -> 0x012f }
            goto L_0x0134
        L_0x012f:
            r0 = move-exception
            r8 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0134:
            r0 = r19
            r8 = r20
            goto L_0x00c8
        L_0x0139:
            r0 = move-exception
            r18 = r6
        L_0x013c:
            r6 = 0
            goto L_0x0147
        L_0x013e:
            r0 = move-exception
            r1 = r0
            r7 = 0
            goto L_0x0163
        L_0x0142:
            r0 = move-exception
            r18 = r6
            r6 = 0
            r8 = 0
        L_0x0147:
            r19 = 0
        L_0x0149:
            r20 = 0
        L_0x014b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0160 }
            if (r8 == 0) goto L_0x0159
            r8.release()     // Catch:{ Exception -> 0x0154 }
            goto L_0x0159
        L_0x0154:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x0159:
            r0 = r19
            r8 = r20
            r5 = 0
            goto L_0x00c8
        L_0x0160:
            r0 = move-exception
            r1 = r0
            r7 = r8
        L_0x0163:
            if (r7 == 0) goto L_0x016e
            r7.release()     // Catch:{ Exception -> 0x0169 }
            goto L_0x016e
        L_0x0169:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x016e:
            throw r1
        L_0x016f:
            r18 = r6
            org.telegram.messenger.audioinfo.AudioInfo r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r2)
            if (r0 == 0) goto L_0x018f
            long r5 = r0.getDuration()
            r19 = 0
            int r8 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r8 == 0) goto L_0x0191
            java.lang.String r8 = r0.getArtist()
            java.lang.String r0 = r0.getTitle()
            r21 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r21
            int r6 = (int) r5
            goto L_0x0194
        L_0x018f:
            r19 = 0
        L_0x0191:
            r0 = 0
            r6 = 0
            r8 = 0
        L_0x0194:
            r5 = 0
            r27 = r6
            r6 = r0
            r0 = r27
        L_0x019a:
            r32 = r9
            if (r0 == 0) goto L_0x01c1
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r9 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r9.<init>()
            r9.duration = r0
            r9.title = r6
            r9.performer = r8
            if (r6 != 0) goto L_0x01ad
            r9.title = r7
        L_0x01ad:
            int r0 = r9.flags
            r6 = 1
            r0 = r0 | r6
            r9.flags = r0
            if (r8 != 0) goto L_0x01b7
            r9.performer = r7
        L_0x01b7:
            r8 = 2
            r0 = r0 | r8
            r9.flags = r0
            if (r5 == 0) goto L_0x01bf
            r9.voice = r6
        L_0x01bf:
            r5 = r9
            goto L_0x01c2
        L_0x01c1:
            r5 = 0
        L_0x01c2:
            if (r1 == 0) goto L_0x0200
            java.lang.String r0 = "attheme"
            boolean r0 = r1.endsWith(r0)
            if (r0 == 0) goto L_0x01ce
            r0 = 1
            goto L_0x0201
        L_0x01ce:
            if (r5 == 0) goto L_0x01e9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            java.lang.String r1 = "audio"
            r0.append(r1)
            long r8 = r2.length()
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            goto L_0x01ff
        L_0x01e9:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r0.append(r7)
            long r8 = r2.length()
            r0.append(r8)
            java.lang.String r0 = r0.toString()
        L_0x01ff:
            r1 = r0
        L_0x0200:
            r0 = 0
        L_0x0201:
            if (r0 != 0) goto L_0x029a
            if (r3 != 0) goto L_0x029a
            org.telegram.messenger.MessagesStorage r0 = r29.getMessagesStorage()
            if (r3 != 0) goto L_0x020d
            r8 = 1
            goto L_0x020e
        L_0x020d:
            r8 = 4
        L_0x020e:
            java.lang.Object[] r0 = r0.getSentFile(r1, r8)
            if (r0 == 0) goto L_0x0226
            r8 = 0
            r9 = r0[r8]
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r9 == 0) goto L_0x0226
            r9 = r0[r8]
            r8 = r9
            org.telegram.tgnet.TLRPC$TL_document r8 = (org.telegram.tgnet.TLRPC$TL_document) r8
            r9 = 1
            r0 = r0[r9]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x0228
        L_0x0226:
            r0 = 0
            r8 = 0
        L_0x0228:
            if (r8 != 0) goto L_0x026e
            boolean r9 = r14.equals(r1)
            if (r9 != 0) goto L_0x026e
            if (r3 != 0) goto L_0x026e
            org.telegram.messenger.MessagesStorage r9 = r29.getMessagesStorage()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r14)
            r21 = r10
            r33 = r11
            long r10 = r2.length()
            r6.append(r10)
            java.lang.String r6 = r6.toString()
            if (r3 != 0) goto L_0x0251
            r10 = 1
            goto L_0x0252
        L_0x0251:
            r10 = 4
        L_0x0252:
            java.lang.Object[] r6 = r9.getSentFile(r6, r10)
            if (r6 == 0) goto L_0x0272
            r9 = 0
            r10 = r6[r9]
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r10 == 0) goto L_0x0272
            r0 = r6[r9]
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            r8 = 1
            r6 = r6[r8]
            java.lang.String r6 = (java.lang.String) r6
            r27 = r6
            r6 = r0
            r0 = r27
            goto L_0x0273
        L_0x026e:
            r21 = r10
            r33 = r11
        L_0x0272:
            r6 = r8
        L_0x0273:
            r11 = 0
            r22 = 0
            r24 = -1
            r8 = r3
            r10 = r32
            r9 = r6
            r25 = r10
            r26 = r21
            r10 = r14
            r30 = r33
            r32 = r6
            r6 = r12
            r33 = r15
            r27 = r19
            r19 = r1
            r1 = r13
            r20 = r14
            r14 = r27
            r12 = r22
            ensureMediaThumbExists(r8, r9, r10, r11, r12)
            r8 = r0
            r0 = r32
            goto L_0x02b0
        L_0x029a:
            r25 = r32
            r26 = r10
            r30 = r11
            r6 = r12
            r33 = r15
            r24 = -1
            r27 = r19
            r19 = r1
            r1 = r13
            r20 = r14
            r14 = r27
            r0 = 0
            r8 = 0
        L_0x02b0:
            if (r0 != 0) goto L_0x0441
            org.telegram.tgnet.TLRPC$TL_document r9 = new org.telegram.tgnet.TLRPC$TL_document
            r9.<init>()
            r9.id = r14
            org.telegram.tgnet.ConnectionsManager r0 = r29.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r9.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            r10 = r25
            r0.file_name = r10
            r10 = 0
            byte[] r11 = new byte[r10]
            r9.file_reference = r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r11 = r9.attributes
            r11.add(r0)
            long r11 = r2.length()
            int r12 = (int) r11
            r9.size = r12
            r9.dc_id = r10
            if (r5 == 0) goto L_0x02e6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r10 = r9.attributes
            r10.add(r5)
        L_0x02e6:
            int r10 = r16.length()
            java.lang.String r11 = "image/webp"
            java.lang.String r12 = "application/octet-stream"
            if (r10 == 0) goto L_0x037a
            r30.hashCode()
            int r10 = r30.hashCode()
            switch(r10) {
                case 106458: goto L_0x0342;
                case 108272: goto L_0x0334;
                case 109967: goto L_0x0326;
                case 3145576: goto L_0x0319;
                case 3418175: goto L_0x030d;
                case 3645340: goto L_0x02fd;
                default: goto L_0x02fa;
            }
        L_0x02fa:
            r10 = r30
            goto L_0x034d
        L_0x02fd:
            java.lang.String r1 = "webp"
            r10 = r30
            boolean r1 = r10.equals(r1)
            if (r1 != 0) goto L_0x0309
            goto L_0x034d
        L_0x0309:
            r1 = 5
            r24 = 5
            goto L_0x034d
        L_0x030d:
            r10 = r30
            boolean r1 = r10.equals(r6)
            if (r1 != 0) goto L_0x0316
            goto L_0x034d
        L_0x0316:
            r24 = 4
            goto L_0x034d
        L_0x0319:
            r10 = r30
            boolean r1 = r10.equals(r4)
            if (r1 != 0) goto L_0x0322
            goto L_0x034d
        L_0x0322:
            r1 = 3
            r24 = 3
            goto L_0x034d
        L_0x0326:
            r10 = r30
            r1 = r33
            boolean r1 = r10.equals(r1)
            if (r1 != 0) goto L_0x0331
            goto L_0x034d
        L_0x0331:
            r24 = 2
            goto L_0x034d
        L_0x0334:
            r10 = r30
            r1 = r26
            boolean r1 = r10.equals(r1)
            if (r1 != 0) goto L_0x033f
            goto L_0x034d
        L_0x033f:
            r24 = 1
            goto L_0x034d
        L_0x0342:
            r10 = r30
            boolean r1 = r10.equals(r1)
            if (r1 != 0) goto L_0x034b
            goto L_0x034d
        L_0x034b:
            r24 = 0
        L_0x034d:
            switch(r24) {
                case 0: goto L_0x0375;
                case 1: goto L_0x0370;
                case 2: goto L_0x036b;
                case 3: goto L_0x0366;
                case 4: goto L_0x0361;
                case 5: goto L_0x035e;
                default: goto L_0x0350;
            }
        L_0x0350:
            r1 = r18
            java.lang.String r1 = r1.getMimeTypeFromExtension(r10)
            if (r1 == 0) goto L_0x035b
            r9.mime_type = r1
            goto L_0x037c
        L_0x035b:
            r9.mime_type = r12
            goto L_0x037c
        L_0x035e:
            r9.mime_type = r11
            goto L_0x037c
        L_0x0361:
            java.lang.String r1 = "audio/opus"
            r9.mime_type = r1
            goto L_0x037c
        L_0x0366:
            java.lang.String r1 = "audio/flac"
            r9.mime_type = r1
            goto L_0x037c
        L_0x036b:
            java.lang.String r1 = "audio/ogg"
            r9.mime_type = r1
            goto L_0x037c
        L_0x0370:
            java.lang.String r1 = "audio/mpeg"
            r9.mime_type = r1
            goto L_0x037c
        L_0x0375:
            java.lang.String r1 = "audio/m4a"
            r9.mime_type = r1
            goto L_0x037c
        L_0x037a:
            r9.mime_type = r12
        L_0x037c:
            if (r43 != 0) goto L_0x03c9
            java.lang.String r1 = r9.mime_type
            java.lang.String r4 = "image/gif"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x03c9
            if (r40 == 0) goto L_0x0392
            long r12 = r40.getGroupIdForUse()
            int r1 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r1 != 0) goto L_0x03c9
        L_0x0392:
            java.lang.String r1 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x03c5 }
            r2 = 1119092736(0x42b40000, float:90.0)
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r4, r2, r2, r6)     // Catch:{ Exception -> 0x03c5 }
            if (r1 == 0) goto L_0x03c9
            java.lang.String r4 = "animation.gif"
            r0.file_name = r4     // Catch:{ Exception -> 0x03c5 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r9.attributes     // Catch:{ Exception -> 0x03c5 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x03c5 }
            r4.<init>()     // Catch:{ Exception -> 0x03c5 }
            r0.add(r4)     // Catch:{ Exception -> 0x03c5 }
            r0 = 55
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r2, r2, r0, r3)     // Catch:{ Exception -> 0x03c5 }
            if (r0 == 0) goto L_0x03c1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r9.thumbs     // Catch:{ Exception -> 0x03c5 }
            r2.add(r0)     // Catch:{ Exception -> 0x03c5 }
            int r0 = r9.flags     // Catch:{ Exception -> 0x03c5 }
            r2 = 1
            r0 = r0 | r2
            r9.flags = r0     // Catch:{ Exception -> 0x03c5 }
        L_0x03c1:
            r1.recycle()     // Catch:{ Exception -> 0x03c5 }
            goto L_0x03c9
        L_0x03c5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03c9:
            java.lang.String r0 = r9.mime_type
            boolean r0 = r0.equals(r11)
            if (r0 == 0) goto L_0x043e
            if (r40 != 0) goto L_0x043e
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options
            r1.<init>()
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ Exception -> 0x0404 }
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0404 }
            java.lang.String r2 = "r"
            r4 = r20
            r0.<init>(r4, r2)     // Catch:{ Exception -> 0x0402 }
            java.nio.channels.FileChannel r10 = r0.getChannel()     // Catch:{ Exception -> 0x0402 }
            java.nio.channels.FileChannel$MapMode r11 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Exception -> 0x0402 }
            r12 = 0
            int r2 = r4.length()     // Catch:{ Exception -> 0x0402 }
            long r14 = (long) r2     // Catch:{ Exception -> 0x0402 }
            java.nio.MappedByteBuffer r2 = r10.map(r11, r12, r14)     // Catch:{ Exception -> 0x0402 }
            int r6 = r2.limit()     // Catch:{ Exception -> 0x0402 }
            r10 = 0
            r11 = 1
            org.telegram.messenger.Utilities.loadWebpImage(r10, r2, r6, r1, r11)     // Catch:{ Exception -> 0x0402 }
            r0.close()     // Catch:{ Exception -> 0x0402 }
            goto L_0x040a
        L_0x0402:
            r0 = move-exception
            goto L_0x0407
        L_0x0404:
            r0 = move-exception
            r4 = r20
        L_0x0407:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x040a:
            int r0 = r1.outWidth
            if (r0 == 0) goto L_0x0444
            int r2 = r1.outHeight
            if (r2 == 0) goto L_0x0444
            r6 = 800(0x320, float:1.121E-42)
            if (r0 > r6) goto L_0x0444
            if (r2 > r6) goto L_0x0444
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            r0.alt = r7
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r2.<init>()
            r0.stickerset = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r9.attributes
            r2.add(r0)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r0.<init>()
            int r2 = r1.outWidth
            r0.w = r2
            int r1 = r1.outHeight
            r0.h = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r9.attributes
            r1.add(r0)
            goto L_0x0444
        L_0x043e:
            r4 = r20
            goto L_0x0444
        L_0x0441:
            r4 = r20
            r9 = r0
        L_0x0444:
            if (r38 == 0) goto L_0x044c
            java.lang.String r0 = r38.toString()
            r11 = r0
            goto L_0x044d
        L_0x044c:
            r11 = r7
        L_0x044d:
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            if (r19 == 0) goto L_0x045b
            java.lang.String r0 = "originalPath"
            r1 = r19
            r6.put(r0, r1)
        L_0x045b:
            java.lang.String r0 = "1"
            if (r43 == 0) goto L_0x0466
            if (r5 != 0) goto L_0x0466
            java.lang.String r1 = "forceDocument"
            r6.put(r1, r0)
        L_0x0466:
            if (r8 == 0) goto L_0x046d
            java.lang.String r1 = "parentObject"
            r6.put(r1, r8)
        L_0x046d:
            r1 = 0
            if (r46 == 0) goto L_0x04af
            r2 = r46[r1]
            java.lang.String r1 = r9.mime_type
            if (r1 == 0) goto L_0x0491
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r10 = "image/"
            boolean r1 = r1.startsWith(r10)
            if (r1 != 0) goto L_0x0497
            java.lang.String r1 = r9.mime_type
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r10 = "video/mp4"
            boolean r1 = r1.startsWith(r10)
            if (r1 != 0) goto L_0x0497
        L_0x0491:
            boolean r1 = org.telegram.messenger.MessageObject.canPreviewDocument(r9)
            if (r1 == 0) goto L_0x04a0
        L_0x0497:
            r1 = 1
            java.lang.Integer r5 = java.lang.Integer.valueOf(r1)
            r1 = 0
            r46[r1] = r5
            goto L_0x04ad
        L_0x04a0:
            r1 = 0
            if (r5 == 0) goto L_0x04ab
            r5 = 2
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r46[r1] = r5
            goto L_0x04ad
        L_0x04ab:
            r46[r1] = r17
        L_0x04ad:
            r5 = r2
            goto L_0x04b1
        L_0x04af:
            r5 = r17
        L_0x04b1:
            if (r3 != 0) goto L_0x04f1
            if (r41 == 0) goto L_0x04f1
            if (r46 == 0) goto L_0x04cf
            if (r5 == 0) goto L_0x04cf
            r2 = r46[r1]
            if (r5 == r2) goto L_0x04cf
            r2 = r41[r1]
            r5 = r29
            r14 = r45
            finishGroup(r5, r2, r14)
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random
            long r2 = r2.nextLong()
            r41[r1] = r2
            goto L_0x04d3
        L_0x04cf:
            r5 = r29
            r14 = r45
        L_0x04d3:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            r12 = r41[r1]
            r2.append(r12)
            java.lang.String r1 = r2.toString()
            java.lang.String r2 = "groupId"
            r6.put(r2, r1)
            if (r42 == 0) goto L_0x04f5
            java.lang.String r1 = "final"
            r6.put(r1, r0)
            goto L_0x04f5
        L_0x04f1:
            r5 = r29
            r14 = r45
        L_0x04f5:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$IqBSFfOE9F3L4bgPy8WdkGcEk3E r15 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$IqBSFfOE9F3L4bgPy8WdkGcEk3E
            r0 = r15
            r1 = r40
            r2 = r29
            r3 = r9
            r5 = r6
            r6 = r8
            r7 = r34
            r9 = r36
            r10 = r37
            r12 = r39
            r13 = r44
            r14 = r45
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r15)
            r1 = 1
            return r1
        L_0x0513:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, long[], boolean, boolean, boolean, int, java.lang.Integer[]):boolean");
    }

    static /* synthetic */ void lambda$prepareSendingDocumentInternal$63(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, String str3, ArrayList arrayList, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, str, hashMap, false, str2);
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

    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00c6 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingAudioDocuments$65(java.util.ArrayList r26, long r27, org.telegram.messenger.AccountInstance r29, java.lang.String r30, org.telegram.messenger.MessageObject r31, org.telegram.messenger.MessageObject r32, org.telegram.messenger.MessageObject r33, boolean r34, int r35) {
        /*
            r14 = r27
            int r13 = r26.size()
            r16 = 0
            r0 = 0
            r2 = 0
            r12 = 0
        L_0x000c:
            if (r12 >= r13) goto L_0x0139
            r11 = r26
            java.lang.Object r3 = r11.get(r12)
            r4 = r3
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            java.lang.String r3 = r3.attachPath
            java.io.File r5 = new java.io.File
            r5.<init>(r3)
            int r6 = (int) r14
            r7 = 1
            if (r6 != 0) goto L_0x0026
            r6 = 1
            goto L_0x0027
        L_0x0026:
            r6 = 0
        L_0x0027:
            r8 = 32
            if (r6 == 0) goto L_0x0041
            long r9 = r14 >> r8
            int r10 = (int) r9
            org.telegram.messenger.MessagesController r9 = r29.getMessagesController()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r9.getEncryptedChat(r10)
            if (r9 == 0) goto L_0x0041
            int r9 = r9.layer
            org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r9)
        L_0x0041:
            if (r6 != 0) goto L_0x0052
            if (r13 <= r7) goto L_0x0052
            int r9 = r2 % 10
            if (r9 != 0) goto L_0x0052
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r0 = r0.nextLong()
            r9 = r0
            r2 = 0
            goto L_0x0053
        L_0x0052:
            r9 = r0
        L_0x0053:
            if (r3 == 0) goto L_0x0070
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            java.lang.String r1 = "audio"
            r0.append(r1)
            r23 = r9
            long r8 = r5.length()
            r0.append(r8)
            java.lang.String r3 = r0.toString()
            goto L_0x0072
        L_0x0070:
            r23 = r9
        L_0x0072:
            r0 = 0
            if (r6 != 0) goto L_0x00a5
            org.telegram.messenger.MessagesStorage r5 = r29.getMessagesStorage()
            if (r6 != 0) goto L_0x007d
            r8 = 1
            goto L_0x007e
        L_0x007d:
            r8 = 4
        L_0x007e:
            java.lang.Object[] r5 = r5.getSentFile(r3, r8)
            if (r5 == 0) goto L_0x00a5
            r8 = r5[r16]
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r8 == 0) goto L_0x00a5
            r8 = r5[r16]
            org.telegram.tgnet.TLRPC$TL_document r8 = (org.telegram.tgnet.TLRPC$TL_document) r8
            r5 = r5[r7]
            java.lang.String r5 = (java.lang.String) r5
            r20 = 0
            r21 = 0
            r17 = r6
            r18 = r8
            r19 = r3
            ensureMediaThumbExists(r17, r18, r19, r20, r21)
            r25 = r8
            r8 = r5
            r5 = r25
            goto L_0x00a7
        L_0x00a5:
            r5 = r0
            r8 = r5
        L_0x00a7:
            if (r5 != 0) goto L_0x00b1
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            org.telegram.tgnet.TLRPC$TL_document r5 = (org.telegram.tgnet.TLRPC$TL_document) r5
        L_0x00b1:
            if (r6 == 0) goto L_0x00c7
            r1 = 32
            long r9 = r14 >> r1
            int r1 = (int) r9
            org.telegram.messenger.MessagesController r6 = r29.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r6.getEncryptedChat(r1)
            if (r1 != 0) goto L_0x00c7
            return
        L_0x00c7:
            if (r12 != 0) goto L_0x00cc
            r17 = r30
            goto L_0x00ce
        L_0x00cc:
            r17 = r0
        L_0x00ce:
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            if (r3 == 0) goto L_0x00da
            java.lang.String r0 = "originalPath"
            r6.put(r0, r3)
        L_0x00da:
            if (r8 == 0) goto L_0x00e1
            java.lang.String r0 = "parentObject"
            r6.put(r0, r8)
        L_0x00e1:
            int r10 = r2 + 1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = ""
            r0.append(r1)
            r2 = r23
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "groupId"
            r6.put(r1, r0)
            r0 = 10
            if (r10 == r0) goto L_0x0103
            int r0 = r13 + -1
            if (r12 != r0) goto L_0x010a
        L_0x0103:
            java.lang.String r0 = "final"
            java.lang.String r1 = "1"
            r6.put(r0, r1)
        L_0x010a:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$u_nxXmlyBwbUeFE4OVguOgMQDDo r18 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$u_nxXmlyBwbUeFE4OVguOgMQDDo
            r0 = r18
            r1 = r31
            r19 = r2
            r2 = r29
            r3 = r5
            r5 = r6
            r6 = r8
            r7 = r27
            r9 = r32
            r21 = r10
            r10 = r33
            r11 = r17
            r17 = r12
            r12 = r34
            r22 = r13
            r13 = r35
            r0.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r18)
            int r12 = r17 + 1
            r0 = r19
            r2 = r21
            r13 = r22
            goto L_0x000c
        L_0x0139:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$65(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$64(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, MessageObject messageObject4, String str2, boolean z, int i) {
        MessageObject messageObject5 = messageObject2;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, messageObject5.messageOwner.attachPath, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, messageObject5.messageOwner.attachPath, j, messageObject3, messageObject4, str2, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str);
    }

    private static void finishGroup(AccountInstance accountInstance, long j, int i) {
        AndroidUtilities.runOnUIThread(new Runnable(j, i) {
            public final /* synthetic */ long f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                SendMessagesHelper.lambda$finishGroup$66(AccountInstance.this, this.f$1, this.f$2);
            }
        });
    }

    static /* synthetic */ void lambda$finishGroup$66(AccountInstance accountInstance, long j, int i) {
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

    public static void prepareSendingDocuments(AccountInstance accountInstance, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<Uri> arrayList3, String str, String str2, long j, MessageObject messageObject, MessageObject messageObject2, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject3, boolean z, int i) {
        if (arrayList != null || arrayList2 != null || arrayList3 != null) {
            if (arrayList == null || arrayList2 == null || arrayList.size() == arrayList2.size()) {
                $$Lambda$SendMessagesHelper$WNNsQ0HEcED9k8_no1CeKn2cNPo r15 = r0;
                $$Lambda$SendMessagesHelper$WNNsQ0HEcED9k8_no1CeKn2cNPo r0 = new Runnable(j, accountInstance, arrayList, str, i, arrayList2, str2, messageObject, messageObject2, messageObject3, inputContentInfoCompat, z, arrayList3) {
                    public final /* synthetic */ long f$0;
                    public final /* synthetic */ AccountInstance f$1;
                    public final /* synthetic */ InputContentInfoCompat f$10;
                    public final /* synthetic */ boolean f$11;
                    public final /* synthetic */ ArrayList f$12;
                    public final /* synthetic */ ArrayList f$2;
                    public final /* synthetic */ String f$3;
                    public final /* synthetic */ int f$4;
                    public final /* synthetic */ ArrayList f$5;
                    public final /* synthetic */ String f$6;
                    public final /* synthetic */ MessageObject f$7;
                    public final /* synthetic */ MessageObject f$8;
                    public final /* synthetic */ MessageObject f$9;

                    {
                        this.f$0 = r1;
                        this.f$1 = r3;
                        this.f$2 = r4;
                        this.f$3 = r5;
                        this.f$4 = r6;
                        this.f$5 = r7;
                        this.f$6 = r8;
                        this.f$7 = r9;
                        this.f$8 = r10;
                        this.f$9 = r11;
                        this.f$10 = r12;
                        this.f$11 = r13;
                        this.f$12 = r14;
                    }

                    public final void run() {
                        SendMessagesHelper.lambda$prepareSendingDocuments$68(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12);
                    }
                };
                new Thread(r15).start();
            }
        }
    }

    static /* synthetic */ void lambda$prepareSendingDocuments$68(long j, AccountInstance accountInstance, ArrayList arrayList, String str, int i, ArrayList arrayList2, String str2, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, InputContentInfoCompat inputContentInfoCompat, boolean z, ArrayList arrayList3) {
        long[] jArr;
        Integer[] numArr;
        boolean z2;
        ArrayList arrayList4;
        long j2 = j;
        AccountInstance accountInstance2 = accountInstance;
        ArrayList arrayList5 = arrayList;
        int i2 = i;
        ArrayList arrayList6 = arrayList3;
        int i3 = 1;
        long[] jArr2 = new long[1];
        Integer[] numArr2 = new Integer[1];
        boolean z3 = ((int) j2) == 0;
        if (z3) {
            TLRPC$EncryptedChat encryptedChat = accountInstance.getMessagesController().getEncryptedChat(Integer.valueOf((int) (j2 >> 32)));
            if (encryptedChat != null) {
                AndroidUtilities.getPeerLayerVersion(encryptedChat.layer);
            }
        }
        int i4 = 10;
        if (arrayList5 != null) {
            int size = arrayList.size();
            int i5 = 0;
            int i6 = 0;
            z2 = false;
            while (i6 < size) {
                String str3 = i6 == 0 ? str : null;
                if (!z3 && size > i3 && i5 % 10 == 0) {
                    if (jArr2[0] != 0) {
                        finishGroup(accountInstance2, jArr2[0], i2);
                    }
                    jArr2[0] = Utilities.random.nextLong();
                    i5 = 0;
                }
                int i7 = i5 + 1;
                long j3 = jArr2[0];
                int i8 = i7;
                int i9 = i6;
                int i10 = size;
                Integer[] numArr3 = numArr2;
                long[] jArr3 = jArr2;
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList5.get(i6), (String) arrayList2.get(i6), (Uri) null, str2, j, messageObject, messageObject2, str3, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, jArr3, i7 == i4 || i6 == size + -1, inputContentInfoCompat == null, z, i, numArr3)) {
                    z2 = true;
                }
                i5 = j3 != jArr3[0] ? 1 : i8;
                i6 = i9 + 1;
                long j4 = j;
                accountInstance2 = accountInstance;
                arrayList5 = arrayList;
                i2 = i;
                ArrayList arrayList7 = arrayList3;
                size = i10;
                numArr2 = numArr3;
                jArr2 = jArr3;
                i4 = 10;
                i3 = 1;
            }
            numArr = numArr2;
            jArr = jArr2;
            arrayList4 = arrayList3;
        } else {
            numArr = numArr2;
            jArr = jArr2;
            arrayList4 = arrayList3;
            z2 = false;
        }
        if (arrayList4 != null) {
            jArr[0] = 0;
            int size2 = arrayList3.size();
            int i11 = 0;
            int i12 = 0;
            while (i12 < arrayList3.size()) {
                String str4 = (i12 == 0 && (arrayList == null || arrayList.size() == 0)) ? str : null;
                if (z3) {
                    AccountInstance accountInstance3 = accountInstance;
                    int i13 = i;
                } else if (size2 <= 1 || i11 % 10 != 0) {
                    AccountInstance accountInstance4 = accountInstance;
                    int i14 = i;
                } else {
                    if (jArr[0] != 0) {
                        finishGroup(accountInstance, jArr[0], i);
                    } else {
                        AccountInstance accountInstance5 = accountInstance;
                        int i15 = i;
                    }
                    jArr[0] = Utilities.random.nextLong();
                    i11 = 0;
                }
                int i16 = i11 + 1;
                long j5 = jArr[0];
                int i17 = i16;
                int i18 = i12;
                int i19 = size2;
                if (!prepareSendingDocumentInternal(accountInstance, (String) null, (String) null, (Uri) arrayList4.get(i12), str2, j, messageObject, messageObject2, str4, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, jArr, i16 == 10 || i12 == size2 + -1, inputContentInfoCompat == null, z, i, numArr)) {
                    z2 = true;
                }
                i11 = j5 != jArr[0] ? 1 : i17;
                i12 = i18 + 1;
                arrayList4 = arrayList3;
                size2 = i19;
            }
        }
        if (inputContentInfoCompat != null) {
            inputContentInfoCompat.releasePermission();
        }
        if (z2) {
            AndroidUtilities.runOnUIThread($$Lambda$SendMessagesHelper$Chf8yROnsqnSiqT8Z0j6gUzXQs4.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$null$67() {
        try {
            Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", NUM), 0).show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, Uri uri, long j, MessageObject messageObject, MessageObject messageObject2, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, ArrayList<TLRPC$InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject3, boolean z, int i2) {
        prepareSendingPhoto(accountInstance, str, (String) null, uri, j, messageObject, messageObject2, charSequence, arrayList, arrayList2, inputContentInfoCompat, i, messageObject3, (VideoEditedInfo) null, z, i2);
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, String str2, Uri uri, long j, MessageObject messageObject, MessageObject messageObject2, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, ArrayList<TLRPC$InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject3, VideoEditedInfo videoEditedInfo, boolean z, int i2) {
        ArrayList<TLRPC$InputDocument> arrayList3 = arrayList2;
        SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
        sendingMediaInfo.path = str;
        sendingMediaInfo.thumbPath = str2;
        sendingMediaInfo.uri = uri;
        if (charSequence != null) {
            sendingMediaInfo.caption = charSequence.toString();
        }
        sendingMediaInfo.entities = arrayList;
        sendingMediaInfo.ttl = i;
        if (arrayList3 != null) {
            sendingMediaInfo.masks = new ArrayList<>(arrayList3);
        }
        sendingMediaInfo.videoEditedInfo = videoEditedInfo;
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
                        SendMessagesHelper.lambda$prepareSendingBotContextResult$70(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
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
                    if (tLRPC$BotInlineMessage.period == 0 && tLRPC$BotInlineMessage.proximity_notification_radius == 0) {
                        TLRPC$TL_messageMediaGeo tLRPC$TL_messageMediaGeo = new TLRPC$TL_messageMediaGeo();
                        TLRPC$BotInlineMessage tLRPC$BotInlineMessage4 = tLRPC$BotInlineResult2.send_message;
                        tLRPC$TL_messageMediaGeo.geo = tLRPC$BotInlineMessage4.geo;
                        tLRPC$TL_messageMediaGeo.heading = tLRPC$BotInlineMessage4.heading;
                        accountInstance.getSendMessagesHelper().sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaGeo, j, messageObject, messageObject2, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
                        return;
                    }
                    TLRPC$TL_messageMediaGeoLive tLRPC$TL_messageMediaGeoLive = new TLRPC$TL_messageMediaGeoLive();
                    TLRPC$BotInlineMessage tLRPC$BotInlineMessage5 = tLRPC$BotInlineResult2.send_message;
                    int i4 = tLRPC$BotInlineMessage5.period;
                    if (i4 == 0) {
                        i4 = 900;
                    }
                    tLRPC$TL_messageMediaGeoLive.period = i4;
                    tLRPC$TL_messageMediaGeoLive.geo = tLRPC$BotInlineMessage5.geo;
                    tLRPC$TL_messageMediaGeoLive.heading = tLRPC$BotInlineMessage5.heading;
                    tLRPC$TL_messageMediaGeoLive.proximity_notification_radius = tLRPC$BotInlineMessage5.proximity_notification_radius;
                    accountInstance.getSendMessagesHelper().sendMessage((TLRPC$MessageMedia) tLRPC$TL_messageMediaGeoLive, j, messageObject, messageObject2, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
                } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaContact) {
                    TLRPC$TL_user tLRPC$TL_user = new TLRPC$TL_user();
                    TLRPC$BotInlineMessage tLRPC$BotInlineMessage6 = tLRPC$BotInlineResult2.send_message;
                    tLRPC$TL_user.phone = tLRPC$BotInlineMessage6.phone_number;
                    tLRPC$TL_user.first_name = tLRPC$BotInlineMessage6.first_name;
                    tLRPC$TL_user.last_name = tLRPC$BotInlineMessage6.last_name;
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v0, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v63, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v64, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v65, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v66, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v68, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v69, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v70, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v95, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v35, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v97, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v98, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r0v99 */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01c1, code lost:
        if (r0.equals("voice") == false) goto L_0x01ba;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0443  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0449  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0455  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x04a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingBotContextResult$70(long r21, org.telegram.tgnet.TLRPC$BotInlineResult r23, org.telegram.messenger.AccountInstance r24, java.util.HashMap r25, org.telegram.messenger.MessageObject r26, org.telegram.messenger.MessageObject r27, boolean r28, int r29) {
        /*
            r11 = r23
            r12 = r25
            r7 = r21
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
            r20 = r0
            r0 = r5
            r2 = r0
            r6 = r2
            goto L_0x0489
        L_0x004d:
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMediaResult
            if (r0 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$Document r0 = r11.document
            if (r0 == 0) goto L_0x0060
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r3 == 0) goto L_0x0483
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            r2 = r0
            r0 = r5
            r6 = r0
            goto L_0x0487
        L_0x0060:
            org.telegram.tgnet.TLRPC$Photo r0 = r11.photo
            if (r0 == 0) goto L_0x0483
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r3 == 0) goto L_0x0483
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            r2 = r5
            goto L_0x0486
        L_0x006d:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            if (r0 == 0) goto L_0x0483
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
            r0.hashCode()
            int r14 = r0.hashCode()
            java.lang.String r15 = "voice"
            r16 = 5
            java.lang.String r4 = "video"
            r18 = 3
            java.lang.String r9 = "audio"
            java.lang.String r2 = "gif"
            java.lang.String r1 = "sticker"
            r19 = 2
            java.lang.String r5 = "file"
            switch(r14) {
                case -1890252483: goto L_0x0122;
                case 102340: goto L_0x0119;
                case 3143036: goto L_0x0110;
                case 93166550: goto L_0x0107;
                case 106642994: goto L_0x00fc;
                case 112202875: goto L_0x00f3;
                case 112386354: goto L_0x00ea;
                default: goto L_0x00e8;
            }
        L_0x00e8:
            r0 = -1
            goto L_0x012a
        L_0x00ea:
            boolean r0 = r0.equals(r15)
            if (r0 != 0) goto L_0x00f1
            goto L_0x00e8
        L_0x00f1:
            r0 = 6
            goto L_0x012a
        L_0x00f3:
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00fa
            goto L_0x00e8
        L_0x00fa:
            r0 = 5
            goto L_0x012a
        L_0x00fc:
            java.lang.String r14 = "photo"
            boolean r0 = r0.equals(r14)
            if (r0 != 0) goto L_0x0105
            goto L_0x00e8
        L_0x0105:
            r0 = 4
            goto L_0x012a
        L_0x0107:
            boolean r0 = r0.equals(r9)
            if (r0 != 0) goto L_0x010e
            goto L_0x00e8
        L_0x010e:
            r0 = 3
            goto L_0x012a
        L_0x0110:
            boolean r0 = r0.equals(r5)
            if (r0 != 0) goto L_0x0117
            goto L_0x00e8
        L_0x0117:
            r0 = 2
            goto L_0x012a
        L_0x0119:
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x0120
            goto L_0x00e8
        L_0x0120:
            r0 = 1
            goto L_0x012a
        L_0x0122:
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x0129
            goto L_0x00e8
        L_0x0129:
            r0 = 0
        L_0x012a:
            java.lang.String r14 = "x"
            switch(r0) {
                case 0: goto L_0x0182;
                case 1: goto L_0x0182;
                case 2: goto L_0x0182;
                case 3: goto L_0x0182;
                case 4: goto L_0x0137;
                case 5: goto L_0x0182;
                case 6: goto L_0x0182;
                default: goto L_0x0130;
            }
        L_0x0130:
            r6 = r10
            r0 = 0
        L_0x0132:
            r2 = 0
            r20 = 0
            goto L_0x0489
        L_0x0137:
            boolean r0 = r3.exists()
            if (r0 == 0) goto L_0x0147
            org.telegram.messenger.SendMessagesHelper r0 = r24.getSendMessagesHelper()
            r1 = 0
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r10, r1)
            goto L_0x0148
        L_0x0147:
            r0 = 0
        L_0x0148:
            if (r0 != 0) goto L_0x0180
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r1 = r24.getConnectionsManager()
            int r1 = r1.getCurrentTime()
            r0.date = r1
            r1 = 0
            byte[] r2 = new byte[r1]
            r0.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize r2 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r2.<init>()
            int[] r3 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
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
        L_0x0180:
            r6 = r10
            goto L_0x0132
        L_0x0182:
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
            org.telegram.tgnet.ConnectionsManager r0 = r24.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r3.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r7.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r3.attributes
            r0.add(r7)
            java.lang.String r0 = r11.type
            r0.hashCode()
            int r8 = r0.hashCode()
            switch(r8) {
                case -1890252483: goto L_0x01ec;
                case 102340: goto L_0x01e2;
                case 3143036: goto L_0x01d8;
                case 93166550: goto L_0x01ce;
                case 112202875: goto L_0x01c4;
                case 112386354: goto L_0x01bd;
                default: goto L_0x01ba;
            }
        L_0x01ba:
            r16 = -1
            goto L_0x01f5
        L_0x01bd:
            boolean r0 = r0.equals(r15)
            if (r0 != 0) goto L_0x01f5
            goto L_0x01ba
        L_0x01c4:
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x01cb
            goto L_0x01ba
        L_0x01cb:
            r16 = 4
            goto L_0x01f5
        L_0x01ce:
            boolean r0 = r0.equals(r9)
            if (r0 != 0) goto L_0x01d5
            goto L_0x01ba
        L_0x01d5:
            r16 = 3
            goto L_0x01f5
        L_0x01d8:
            boolean r0 = r0.equals(r5)
            if (r0 != 0) goto L_0x01df
            goto L_0x01ba
        L_0x01df:
            r16 = 2
            goto L_0x01f5
        L_0x01e2:
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x01e9
            goto L_0x01ba
        L_0x01e9:
            r16 = 1
            goto L_0x01f5
        L_0x01ec:
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x01f3
            goto L_0x01ba
        L_0x01f3:
            r16 = 0
        L_0x01f5:
            r0 = 55
            r1 = 1119092736(0x42b40000, float:90.0)
            switch(r16) {
                case 0: goto L_0x03b2;
                case 1: goto L_0x02f1;
                case 2: goto L_0x02c1;
                case 3: goto L_0x0297;
                case 4: goto L_0x0217;
                case 5: goto L_0x01ff;
                default: goto L_0x01fc;
            }
        L_0x01fc:
            r4 = 0
            goto L_0x043f
        L_0x01ff:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r23)
            r0.duration = r1
            r1 = 1
            r0.voice = r1
            java.lang.String r1 = "audio.ogg"
            r7.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r3.attributes
            r1.add(r0)
            goto L_0x01fc
        L_0x0217:
            java.lang.String r2 = "video.mp4"
            r7.file_name = r2
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r2.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r8 = 0
            r9 = r4[r8]
            r2.w = r9
            r8 = 1
            r4 = r4[r8]
            r2.h = r4
            int r4 = org.telegram.messenger.MessageObject.getInlineResultDuration(r23)
            r2.duration = r4
            r2.supports_streaming = r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r2)
            org.telegram.tgnet.TLRPC$WebDocument r2 = r11.thumb     // Catch:{ all -> 0x0291 }
            if (r2 == 0) goto L_0x01fc
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x0291 }
            r4 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ all -> 0x0291 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0291 }
            r8.<init>()     // Catch:{ all -> 0x0291 }
            org.telegram.tgnet.TLRPC$WebDocument r9 = r11.thumb     // Catch:{ all -> 0x0291 }
            java.lang.String r9 = r9.url     // Catch:{ all -> 0x0291 }
            java.lang.String r9 = org.telegram.messenger.Utilities.MD5(r9)     // Catch:{ all -> 0x0291 }
            r8.append(r9)     // Catch:{ all -> 0x0291 }
            r8.append(r6)     // Catch:{ all -> 0x0291 }
            org.telegram.tgnet.TLRPC$WebDocument r6 = r11.thumb     // Catch:{ all -> 0x0291 }
            java.lang.String r6 = r6.url     // Catch:{ all -> 0x0291 }
            java.lang.String r9 = "jpg"
            java.lang.String r6 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r9)     // Catch:{ all -> 0x0291 }
            r8.append(r6)     // Catch:{ all -> 0x0291 }
            java.lang.String r6 = r8.toString()     // Catch:{ all -> 0x0291 }
            r2.<init>(r4, r6)     // Catch:{ all -> 0x0291 }
            java.lang.String r2 = r2.getAbsolutePath()     // Catch:{ all -> 0x0291 }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r2 = org.telegram.messenger.ImageLoader.loadBitmap(r2, r4, r1, r1, r6)     // Catch:{ all -> 0x0291 }
            if (r2 == 0) goto L_0x01fc
            r4 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r1, r1, r0, r4)     // Catch:{ all -> 0x0291 }
            if (r0 == 0) goto L_0x028c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs     // Catch:{ all -> 0x0291 }
            r1.add(r0)     // Catch:{ all -> 0x0291 }
            int r0 = r3.flags     // Catch:{ all -> 0x0291 }
            r1 = 1
            r0 = r0 | r1
            r3.flags = r0     // Catch:{ all -> 0x0291 }
        L_0x028c:
            r2.recycle()     // Catch:{ all -> 0x0291 }
            goto L_0x01fc
        L_0x0291:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01fc
        L_0x0297:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r23)
            r0.duration = r1
            java.lang.String r1 = r11.title
            r0.title = r1
            int r1 = r0.flags
            r2 = 1
            r1 = r1 | r2
            r0.flags = r1
            java.lang.String r2 = r11.description
            if (r2 == 0) goto L_0x02b6
            r0.performer = r2
            r1 = r1 | 2
            r0.flags = r1
        L_0x02b6:
            java.lang.String r1 = "audio.mp3"
            r7.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r3.attributes
            r1.add(r0)
            goto L_0x01fc
        L_0x02c1:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r1 = 47
            int r0 = r0.lastIndexOf(r1)
            r1 = -1
            if (r0 == r1) goto L_0x02ed
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
            goto L_0x01fc
        L_0x02ed:
            r7.file_name = r5
            goto L_0x01fc
        L_0x02f1:
            java.lang.String r1 = "animation.gif"
            r7.file_name = r1
            java.lang.String r1 = "mp4"
            boolean r2 = r10.endsWith(r1)
            java.lang.String r4 = "video/mp4"
            if (r2 == 0) goto L_0x030d
            r3.mime_type = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r3.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r8.<init>()
            r2.add(r8)
            goto L_0x0311
        L_0x030d:
            java.lang.String r2 = "image/gif"
            r3.mime_type = r2
        L_0x0311:
            r2 = 90
            if (r13 == 0) goto L_0x0318
            r8 = 90
            goto L_0x031a
        L_0x0318:
            r8 = 320(0x140, float:4.48E-43)
        L_0x031a:
            boolean r1 = r10.endsWith(r1)     // Catch:{ all -> 0x03ac }
            if (r1 == 0) goto L_0x0387
            r1 = 1
            android.graphics.Bitmap r9 = createVideoThumbnail(r10, r1)     // Catch:{ all -> 0x03ac }
            if (r9 != 0) goto L_0x038e
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.thumb     // Catch:{ all -> 0x03ac }
            boolean r15 = r1 instanceof org.telegram.tgnet.TLRPC$TL_webDocument     // Catch:{ all -> 0x03ac }
            if (r15 == 0) goto L_0x038e
            java.lang.String r1 = r1.mime_type     // Catch:{ all -> 0x03ac }
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03ac }
            if (r1 == 0) goto L_0x038e
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.thumb     // Catch:{ all -> 0x03ac }
            java.lang.String r1 = r1.url     // Catch:{ all -> 0x03ac }
            r4 = 0
            java.lang.String r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r4)     // Catch:{ all -> 0x03ac }
            boolean r4 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03ac }
            if (r4 == 0) goto L_0x034d
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.thumb     // Catch:{ all -> 0x03ac }
            java.lang.String r1 = r1.mime_type     // Catch:{ all -> 0x03ac }
            java.lang.String r1 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r1)     // Catch:{ all -> 0x03ac }
            goto L_0x035c
        L_0x034d:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ac }
            r4.<init>()     // Catch:{ all -> 0x03ac }
            r4.append(r6)     // Catch:{ all -> 0x03ac }
            r4.append(r1)     // Catch:{ all -> 0x03ac }
            java.lang.String r1 = r4.toString()     // Catch:{ all -> 0x03ac }
        L_0x035c:
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x03ac }
            r6 = 4
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r6)     // Catch:{ all -> 0x03ac }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ac }
            r9.<init>()     // Catch:{ all -> 0x03ac }
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.thumb     // Catch:{ all -> 0x03ac }
            java.lang.String r15 = r15.url     // Catch:{ all -> 0x03ac }
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)     // Catch:{ all -> 0x03ac }
            r9.append(r15)     // Catch:{ all -> 0x03ac }
            r9.append(r1)     // Catch:{ all -> 0x03ac }
            java.lang.String r1 = r9.toString()     // Catch:{ all -> 0x03ac }
            r4.<init>(r6, r1)     // Catch:{ all -> 0x03ac }
            java.lang.String r1 = r4.getAbsolutePath()     // Catch:{ all -> 0x03ac }
            r4 = 1
            android.graphics.Bitmap r9 = createVideoThumbnail(r1, r4)     // Catch:{ all -> 0x03ac }
            goto L_0x038e
        L_0x0387:
            float r1 = (float) r8     // Catch:{ all -> 0x03ac }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r9 = org.telegram.messenger.ImageLoader.loadBitmap(r10, r4, r1, r1, r6)     // Catch:{ all -> 0x03ac }
        L_0x038e:
            if (r9 == 0) goto L_0x01fc
            float r1 = (float) r8     // Catch:{ all -> 0x03ac }
            if (r8 <= r2) goto L_0x0395
            r0 = 80
        L_0x0395:
            r2 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r9, r1, r1, r0, r2)     // Catch:{ all -> 0x03ac }
            if (r0 == 0) goto L_0x03a7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs     // Catch:{ all -> 0x03ac }
            r1.add(r0)     // Catch:{ all -> 0x03ac }
            int r0 = r3.flags     // Catch:{ all -> 0x03ac }
            r1 = 1
            r0 = r0 | r1
            r3.flags = r0     // Catch:{ all -> 0x03ac }
        L_0x03a7:
            r9.recycle()     // Catch:{ all -> 0x03ac }
            goto L_0x01fc
        L_0x03ac:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01fc
        L_0x03b2:
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r2.<init>()
            java.lang.String r4 = ""
            r2.alt = r4
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r4.<init>()
            r2.stickerset = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r2)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r2.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r8 = 0
            r9 = r4[r8]
            r2.w = r9
            r8 = 1
            r4 = r4[r8]
            r2.h = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r2)
            java.lang.String r2 = "sticker.webp"
            r7.file_name = r2
            org.telegram.tgnet.TLRPC$WebDocument r2 = r11.thumb     // Catch:{ all -> 0x043a }
            if (r2 == 0) goto L_0x01fc
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x043a }
            r4 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ all -> 0x043a }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x043a }
            r8.<init>()     // Catch:{ all -> 0x043a }
            org.telegram.tgnet.TLRPC$WebDocument r9 = r11.thumb     // Catch:{ all -> 0x043a }
            java.lang.String r9 = r9.url     // Catch:{ all -> 0x043a }
            java.lang.String r9 = org.telegram.messenger.Utilities.MD5(r9)     // Catch:{ all -> 0x043a }
            r8.append(r9)     // Catch:{ all -> 0x043a }
            r8.append(r6)     // Catch:{ all -> 0x043a }
            org.telegram.tgnet.TLRPC$WebDocument r6 = r11.thumb     // Catch:{ all -> 0x043a }
            java.lang.String r6 = r6.url     // Catch:{ all -> 0x043a }
            java.lang.String r9 = "webp"
            java.lang.String r6 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r9)     // Catch:{ all -> 0x043a }
            r8.append(r6)     // Catch:{ all -> 0x043a }
            java.lang.String r6 = r8.toString()     // Catch:{ all -> 0x043a }
            r2.<init>(r4, r6)     // Catch:{ all -> 0x043a }
            java.lang.String r2 = r2.getAbsolutePath()     // Catch:{ all -> 0x043a }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r2 = org.telegram.messenger.ImageLoader.loadBitmap(r2, r4, r1, r1, r6)     // Catch:{ all -> 0x0438 }
            if (r2 == 0) goto L_0x043f
            r6 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r1, r1, r0, r6)     // Catch:{ all -> 0x0438 }
            if (r0 == 0) goto L_0x0434
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs     // Catch:{ all -> 0x0438 }
            r1.add(r0)     // Catch:{ all -> 0x0438 }
            int r0 = r3.flags     // Catch:{ all -> 0x0438 }
            r1 = 1
            r0 = r0 | r1
            r3.flags = r0     // Catch:{ all -> 0x0438 }
        L_0x0434:
            r2.recycle()     // Catch:{ all -> 0x0438 }
            goto L_0x043f
        L_0x0438:
            r0 = move-exception
            goto L_0x043c
        L_0x043a:
            r0 = move-exception
            r4 = 0
        L_0x043c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x043f:
            java.lang.String r0 = r7.file_name
            if (r0 != 0) goto L_0x0445
            r7.file_name = r5
        L_0x0445:
            java.lang.String r0 = r3.mime_type
            if (r0 != 0) goto L_0x044d
            java.lang.String r0 = "application/octet-stream"
            r3.mime_type = r0
        L_0x044d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r3.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x047d
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            int[] r1 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
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
        L_0x047d:
            r2 = r3
            r0 = r4
            r20 = r0
            r6 = r10
            goto L_0x0489
        L_0x0483:
            r4 = r5
            r0 = r4
            r2 = r0
        L_0x0486:
            r6 = r2
        L_0x0487:
            r20 = r6
        L_0x0489:
            if (r12 == 0) goto L_0x0496
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.content
            if (r1 == 0) goto L_0x0496
            java.lang.String r1 = r1.url
            java.lang.String r3 = "originalPath"
            r12.put(r3, r1)
        L_0x0496:
            r1 = 1
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r1]
            java.lang.String[] r4 = new java.lang.String[r1]
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r2)
            if (r5 == 0) goto L_0x04cb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r2.thumbs
            r7 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r7)
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r2)
            boolean r8 = r7.exists()
            if (r8 != 0) goto L_0x04b7
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r1)
        L_0x04b7:
            java.lang.String r15 = r7.getAbsolutePath()
            r16 = 0
            r17 = 0
            r14 = r2
            ensureMediaThumbExists(r13, r14, r15, r16, r17)
            r1 = 1
            java.lang.String r1 = getKeyForPhotoSize(r5, r3, r1, r1)
            r5 = 0
            r4[r5] = r1
        L_0x04cb:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$yTXVtfZgKZTlKHZGSnRPMyyFP8o r17 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$yTXVtfZgKZTlKHZGSnRPMyyFP8o
            r1 = r17
            r5 = r24
            r7 = r21
            r9 = r26
            r10 = r27
            r11 = r23
            r12 = r25
            r13 = r28
            r14 = r29
            r15 = r0
            r16 = r20
            r1.<init>(r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15, r16)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r17)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$70(long, org.telegram.tgnet.TLRPC$BotInlineResult, org.telegram.messenger.AccountInstance, java.util.HashMap, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$69(TLRPC$TL_document tLRPC$TL_document, Bitmap[] bitmapArr, String[] strArr, AccountInstance accountInstance, String str, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$BotInlineResult tLRPC$BotInlineResult, HashMap hashMap, boolean z, int i, TLRPC$TL_photo tLRPC$TL_photo, TLRPC$TL_game tLRPC$TL_game) {
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
                                SendMessagesHelper.lambda$null$71(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
                            }
                        });
                    }
                });
            }
        });
    }

    static /* synthetic */ void lambda$null$71(String str, AccountInstance accountInstance, long j, boolean z, int i) {
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
        TLRPC$PhotoSize scaleAndSaveImage;
        TLRPC$PhotoSize scaleAndSaveImage2;
        TLObject tLObject2 = tLObject;
        String str2 = str;
        Uri uri2 = uri;
        if (tLObject2 instanceof TLRPC$TL_photo) {
            TLRPC$TL_photo tLRPC$TL_photo = (TLRPC$TL_photo) tLObject2;
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_photo.sizes, 90);
            boolean exists = ((closestPhotoSizeWithSize instanceof TLRPC$TL_photoStrippedSize) || (closestPhotoSizeWithSize instanceof TLRPC$TL_photoPathSize)) ? true : FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists();
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_photo.sizes, AndroidUtilities.getPhotoSize());
            boolean exists2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize2, false).exists();
            if (!exists || !exists2) {
                Bitmap loadBitmap = ImageLoader.loadBitmap(str2, uri2, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
                if (loadBitmap == null) {
                    loadBitmap = ImageLoader.loadBitmap(str2, uri2, 800.0f, 800.0f, true);
                }
                Bitmap bitmap = loadBitmap;
                if (!exists2 && (scaleAndSaveImage2 = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize2, bitmap, Bitmap.CompressFormat.JPEG, true, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101, false)) != closestPhotoSizeWithSize2) {
                    tLRPC$TL_photo.sizes.add(0, scaleAndSaveImage2);
                }
                if (!exists && (scaleAndSaveImage = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize, bitmap, 90.0f, 90.0f, 55, true, false)) != closestPhotoSizeWithSize) {
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
                if (!(closestPhotoSizeWithSize3 instanceof TLRPC$TL_photoStrippedSize) && !(closestPhotoSizeWithSize3 instanceof TLRPC$TL_photoPathSize) && !FileLoader.getPathToAttach(closestPhotoSizeWithSize3, true).exists()) {
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v53, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v3, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v4, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v5, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v108, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v6, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v7, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v8, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v201, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v202, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v203, resolved type: java.util.ArrayList} */
    /* JADX WARNING: type inference failed for: r5v42 */
    /* JADX WARNING: type inference failed for: r5v44 */
    /* JADX WARNING: type inference failed for: r0v107, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r2v130 */
    /* JADX WARNING: type inference failed for: r3v121 */
    /* JADX WARNING: Can't wrap try/catch for region: R(3:(10:220|221|222|223|224|225|226|227|228|(2:230|231))|232|233) */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x0651, code lost:
        if (r6 != null) goto L_0x0634;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x068b, code lost:
        if (r3.endsWith(r10) != false) goto L_0x06a6;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r5v33, types: [boolean, int] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:232:0x0634 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02e8 A[Catch:{ Exception -> 0x02d9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x02f3 A[Catch:{ Exception -> 0x02d9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0300 A[Catch:{ Exception -> 0x031f }] */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x032d  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0359  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x036e  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0378  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x05ba  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x064c A[SYNTHETIC, Splitter:B:245:0x064c] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x06d7  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x0732  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x08e9  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x091b  */
    /* JADX WARNING: Removed duplicated region for block: B:399:0x09c8  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0b46  */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0c6a  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0c7d  */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x0cc8  */
    /* JADX WARNING: Removed duplicated region for block: B:540:0x0ccd  */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0cd4 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:559:0x0d4f A[LOOP:4: B:557:0x0d47->B:559:0x0d4f, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:571:0x0e3a  */
    /* JADX WARNING: Removed duplicated region for block: B:573:0x0e3f  */
    /* JADX WARNING: Removed duplicated region for block: B:579:0x0e55  */
    /* JADX WARNING: Removed duplicated region for block: B:592:0x0ea5  */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x0ea8  */
    /* JADX WARNING: Removed duplicated region for block: B:597:0x0ef8  */
    /* JADX WARNING: Removed duplicated region for block: B:609:0x0c7a A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:615:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01ab  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingMedia$79(java.util.ArrayList r69, long r70, org.telegram.messenger.AccountInstance r72, boolean r73, boolean r74, org.telegram.messenger.MessageObject r75, org.telegram.messenger.MessageObject r76, org.telegram.messenger.MessageObject r77, boolean r78, int r79, androidx.core.view.inputmethod.InputContentInfoCompat r80) {
        /*
            r1 = r69
            r14 = r70
            r13 = r72
            long r19 = java.lang.System.currentTimeMillis()
            int r12 = r69.size()
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
            org.telegram.messenger.MessagesController r2 = r72.getMessagesController()
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
            if (r73 != 0) goto L_0x0191
            if (r74 == 0) goto L_0x0191
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
            org.telegram.messenger.MessagesStorage r3 = r72.getMessagesStorage()
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
            org.telegram.messenger.MessagesStorage r3 = r72.getMessagesStorage()
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
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$z8OdSZOckxktbzQcpu2RlQzjksY r4 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$z8OdSZOckxktbzQcpu2RlQzjksY
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
            r14 = r70
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
            if (r9 >= r12) goto L_0x0e20
            java.lang.Object r17 = r1.get(r9)
            r6 = r17
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r6 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r6
            if (r74 == 0) goto L_0x01cb
            if (r10 == 0) goto L_0x01b9
            if (r8 < r14) goto L_0x01cb
        L_0x01b9:
            r7 = 1
            if (r12 <= r7) goto L_0x01cb
            int r7 = r0 % 10
            if (r7 != 0) goto L_0x01cb
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r30 = r0.nextLong()
            r34 = r30
            r17 = 0
            goto L_0x01cf
        L_0x01cb:
            r17 = r0
            r34 = r24
        L_0x01cf:
            org.telegram.messenger.MediaController$SearchImage r0 = r6.searchImage
            java.lang.String r7 = "1"
            java.lang.String r14 = "final"
            java.lang.String r11 = "groupId"
            java.lang.String r1 = "mp4"
            r36 = r8
            java.lang.String r8 = "originalPath"
            r37 = r5
            java.lang.String r5 = ""
            r39 = r2
            java.lang.String r2 = "jpg"
            r40 = r3
            java.lang.String r3 = "."
            r41 = 4
            if (r0 == 0) goto L_0x0590
            r42 = r4
            org.telegram.messenger.VideoEditedInfo r4 = r6.videoEditedInfo
            if (r4 != 0) goto L_0x0577
            int r4 = r0.type
            r43 = r9
            r9 = 1
            if (r4 != r9) goto L_0x03dc
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            org.telegram.messenger.MediaController$SearchImage r0 = r6.searchImage
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r4 == 0) goto L_0x020e
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r9)
            goto L_0x023a
        L_0x020e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r4 = r6.searchImage
            java.lang.String r4 = r4.imageUrl
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)
            r0.append(r4)
            r0.append(r3)
            org.telegram.messenger.MediaController$SearchImage r4 = r6.searchImage
            java.lang.String r4 = r4.imageUrl
            java.lang.String r4 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r2)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.io.File r4 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r4.<init>(r5, r0)
            r0 = 0
        L_0x023a:
            if (r0 != 0) goto L_0x035e
            org.telegram.tgnet.TLRPC$TL_document r5 = new org.telegram.tgnet.TLRPC$TL_document
            r5.<init>()
            r44 = r12
            r11 = 0
            r5.id = r11
            r9 = 0
            byte[] r0 = new byte[r9]
            r5.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r72.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r5.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            java.lang.String r9 = "animation.gif"
            r0.file_name = r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r9 = r5.attributes
            r9.add(r0)
            org.telegram.messenger.MediaController$SearchImage r0 = r6.searchImage
            int r0 = r0.size
            r5.size = r0
            r9 = 0
            r5.dc_id = r9
            if (r73 != 0) goto L_0x0289
            java.lang.String r0 = r4.toString()
            boolean r0 = r0.endsWith(r1)
            if (r0 == 0) goto L_0x0289
            java.lang.String r0 = "video/mp4"
            r5.mime_type = r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r5.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r9 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r9.<init>()
            r0.add(r9)
            goto L_0x028d
        L_0x0289:
            java.lang.String r0 = "image/gif"
            r5.mime_type = r0
        L_0x028d:
            boolean r0 = r4.exists()
            if (r0 == 0) goto L_0x0295
            r9 = r4
            goto L_0x0297
        L_0x0295:
            r4 = 0
            r9 = 0
        L_0x0297:
            if (r4 != 0) goto L_0x02cc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r4 = r6.searchImage
            java.lang.String r4 = r4.thumbUrl
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)
            r0.append(r4)
            r0.append(r3)
            org.telegram.messenger.MediaController$SearchImage r3 = r6.searchImage
            java.lang.String r3 = r3.thumbUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r3, r2)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r3 = new java.io.File
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r3.<init>(r2, r0)
            boolean r0 = r3.exists()
            if (r0 != 0) goto L_0x02cd
            r3 = 0
            goto L_0x02cd
        L_0x02cc:
            r3 = r4
        L_0x02cd:
            if (r3 == 0) goto L_0x0324
            if (r10 != 0) goto L_0x02dc
            int r0 = r6.ttl     // Catch:{ Exception -> 0x02d9 }
            if (r0 == 0) goto L_0x02d6
            goto L_0x02dc
        L_0x02d6:
            r0 = 320(0x140, float:4.48E-43)
            goto L_0x02de
        L_0x02d9:
            r0 = move-exception
            r14 = 0
            goto L_0x0320
        L_0x02dc:
            r0 = 90
        L_0x02de:
            java.lang.String r2 = r3.getAbsolutePath()     // Catch:{ Exception -> 0x02d9 }
            boolean r1 = r2.endsWith(r1)     // Catch:{ Exception -> 0x02d9 }
            if (r1 == 0) goto L_0x02f3
            java.lang.String r1 = r3.getAbsolutePath()     // Catch:{ Exception -> 0x02d9 }
            r2 = 1
            android.graphics.Bitmap r1 = createVideoThumbnail(r1, r2)     // Catch:{ Exception -> 0x02d9 }
            r14 = 0
            goto L_0x02fe
        L_0x02f3:
            r2 = 1
            java.lang.String r1 = r3.getAbsolutePath()     // Catch:{ Exception -> 0x02d9 }
            float r3 = (float) r0
            r14 = 0
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r14, r3, r3, r2)     // Catch:{ Exception -> 0x031f }
        L_0x02fe:
            if (r1 == 0) goto L_0x0325
            float r2 = (float) r0     // Catch:{ Exception -> 0x031f }
            r4 = 90
            if (r0 <= r4) goto L_0x0308
            r0 = 80
            goto L_0x030a
        L_0x0308:
            r0 = 55
        L_0x030a:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r2, r2, r0, r10)     // Catch:{ Exception -> 0x031f }
            if (r0 == 0) goto L_0x031b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.thumbs     // Catch:{ Exception -> 0x031f }
            r2.add(r0)     // Catch:{ Exception -> 0x031f }
            int r0 = r5.flags     // Catch:{ Exception -> 0x031f }
            r2 = 1
            r0 = r0 | r2
            r5.flags = r0     // Catch:{ Exception -> 0x031f }
        L_0x031b:
            r1.recycle()     // Catch:{ Exception -> 0x031f }
            goto L_0x0325
        L_0x031f:
            r0 = move-exception
        L_0x0320:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0325
        L_0x0324:
            r14 = 0
        L_0x0325:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r5.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0359
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r1 = r6.searchImage
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
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.thumbs
            r2.add(r0)
            int r0 = r5.flags
            r18 = 1
            r0 = r0 | 1
            r5.flags = r0
            goto L_0x035c
        L_0x0359:
            r1 = 0
            r18 = 1
        L_0x035c:
            r4 = r9
            goto L_0x0367
        L_0x035e:
            r44 = r12
            r1 = 0
            r11 = 0
            r14 = 0
            r18 = 1
            r5 = r0
        L_0x0367:
            org.telegram.messenger.MediaController$SearchImage r0 = r6.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r4 != 0) goto L_0x036e
            goto L_0x0372
        L_0x036e:
            java.lang.String r0 = r4.toString()
        L_0x0372:
            org.telegram.messenger.MediaController$SearchImage r2 = r6.searchImage
            java.lang.String r2 = r2.imageUrl
            if (r2 == 0) goto L_0x037b
            r7.put(r8, r2)
        L_0x037b:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$3n_EF2WdfnTd6iK80HTETFQb0Ek r22 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$3n_EF2WdfnTd6iK80HTETFQb0Ek
            r8 = 0
            r9 = r39
            r2 = r22
            r4 = r40
            r3 = r75
            r46 = r4
            r45 = r42
            r4 = r72
            r47 = r37
            r32 = r11
            r12 = r6
            r6 = r0
            r11 = r36
            r50 = r9
            r48 = r10
            r49 = r43
            r9 = r70
            r1 = r14
            r14 = r11
            r11 = r76
            r26 = r12
            r51 = r44
            r12 = r77
            r13 = r26
            r52 = r14
            r53 = r16
            r24 = 73
            r14 = r78
            r54 = r15
            r15 = r79
            r2.<init>(r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r22)
            r26 = r1
            r0 = r17
            r40 = r34
            r4 = r45
            r3 = r46
            r5 = r47
            r39 = r48
            r1 = r49
            r2 = r50
            r68 = r51
            r37 = r52
            r22 = r53
            r36 = r54
            r38 = 0
            r34 = r27
            r27 = r28
            goto L_0x0e06
        L_0x03dc:
            r26 = r6
            r48 = r10
            r51 = r12
            r54 = r15
            r53 = r16
            r52 = r36
            r47 = r37
            r50 = r39
            r46 = r40
            r45 = r42
            r49 = r43
            r1 = 0
            r24 = 73
            r32 = 0
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r4 == 0) goto L_0x0404
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            r13 = r26
            r15 = r48
            goto L_0x040d
        L_0x0404:
            r15 = r48
            r13 = r26
            if (r15 != 0) goto L_0x040c
            int r0 = r13.ttl
        L_0x040c:
            r0 = r1
        L_0x040d:
            if (r0 != 0) goto L_0x04d4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            org.telegram.messenger.MediaController$SearchImage r6 = r13.searchImage
            java.lang.String r6 = r6.imageUrl
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r6)
            r4.append(r6)
            r4.append(r3)
            org.telegram.messenger.MediaController$SearchImage r6 = r13.searchImage
            java.lang.String r6 = r6.imageUrl
            java.lang.String r6 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r2)
            r4.append(r6)
            java.lang.String r4 = r4.toString()
            java.io.File r6 = new java.io.File
            java.io.File r9 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r6.<init>(r9, r4)
            boolean r4 = r6.exists()
            if (r4 == 0) goto L_0x0458
            long r9 = r6.length()
            int r4 = (r9 > r32 ? 1 : (r9 == r32 ? 0 : -1))
            if (r4 == 0) goto L_0x0458
            org.telegram.messenger.SendMessagesHelper r0 = r72.getSendMessagesHelper()
            java.lang.String r4 = r6.toString()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r4, r1)
            if (r0 == 0) goto L_0x0458
            r4 = 0
            goto L_0x0459
        L_0x0458:
            r4 = 1
        L_0x0459:
            if (r0 != 0) goto L_0x04d1
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            org.telegram.messenger.MediaController$SearchImage r9 = r13.searchImage
            java.lang.String r9 = r9.thumbUrl
            java.lang.String r9 = org.telegram.messenger.Utilities.MD5(r9)
            r6.append(r9)
            r6.append(r3)
            org.telegram.messenger.MediaController$SearchImage r3 = r13.searchImage
            java.lang.String r3 = r3.thumbUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r3, r2)
            r6.append(r2)
            java.lang.String r2 = r6.toString()
            java.io.File r3 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r3.<init>(r6, r2)
            boolean r2 = r3.exists()
            if (r2 == 0) goto L_0x0498
            org.telegram.messenger.SendMessagesHelper r0 = r72.getSendMessagesHelper()
            java.lang.String r2 = r3.toString()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r1)
        L_0x0498:
            if (r0 != 0) goto L_0x04d1
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r2 = r72.getConnectionsManager()
            int r2 = r2.getCurrentTime()
            r0.date = r2
            r12 = 0
            byte[] r2 = new byte[r12]
            r0.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize r2 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r2.<init>()
            org.telegram.messenger.MediaController$SearchImage r3 = r13.searchImage
            int r6 = r3.width
            r2.w = r6
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
            goto L_0x04d2
        L_0x04d1:
            r12 = 0
        L_0x04d2:
            r6 = r4
            goto L_0x04d6
        L_0x04d4:
            r12 = 0
            r6 = 1
        L_0x04d6:
            if (r0 == 0) goto L_0x054b
            java.util.HashMap r9 = new java.util.HashMap
            r9.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r13.searchImage
            java.lang.String r2 = r2.imageUrl
            if (r2 == 0) goto L_0x04e6
            r9.put(r8, r2)
        L_0x04e6:
            if (r74 == 0) goto L_0x051a
            int r2 = r17 + 1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r5)
            r4 = r34
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r9.put(r11, r3)
            r3 = 10
            if (r2 == r3) goto L_0x050e
            r10 = r51
            int r3 = r10 + -1
            r11 = r49
            if (r11 != r3) goto L_0x050b
            goto L_0x0512
        L_0x050b:
            r17 = r2
            goto L_0x0520
        L_0x050e:
            r11 = r49
            r10 = r51
        L_0x0512:
            r9.put(r14, r7)
            r17 = r2
            r30 = r32
            goto L_0x0520
        L_0x051a:
            r4 = r34
            r11 = r49
            r10 = r51
        L_0x0520:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$qWzyIKnxy2u9yt3VPZ0GIcz7Zbg r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$qWzyIKnxy2u9yt3VPZ0GIcz7Zbg
            r14 = 0
            r2 = r16
            r3 = r75
            r7 = r4
            r4 = r72
            r5 = r0
            r55 = r7
            r7 = r13
            r8 = r9
            r9 = r14
            r14 = r10
            r13 = r11
            r10 = r70
            r18 = 0
            r12 = r76
            r57 = r13
            r13 = r77
            r58 = r14
            r14 = r78
            r59 = r15
            r15 = r79
            r2.<init>(r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r16)
            goto L_0x0555
        L_0x054b:
            r59 = r15
            r55 = r34
            r57 = r49
            r58 = r51
            r18 = 0
        L_0x0555:
            r26 = r1
            r0 = r17
            r34 = r27
            r27 = r28
            r4 = r45
            r3 = r46
            r5 = r47
            r2 = r50
            r37 = r52
            r22 = r53
            r36 = r54
            r40 = r55
            r1 = r57
            r68 = r58
            r39 = r59
        L_0x0573:
            r38 = 0
            goto L_0x0e06
        L_0x0577:
            r0 = r1
            r13 = r6
            r57 = r9
            r59 = r10
            r58 = r12
            r54 = r15
            r53 = r16
            r55 = r34
            r52 = r36
            r47 = r37
            r50 = r39
            r46 = r40
            r45 = r42
            goto L_0x05a8
        L_0x0590:
            r0 = r1
            r45 = r4
            r13 = r6
            r57 = r9
            r59 = r10
            r58 = r12
            r54 = r15
            r53 = r16
            r55 = r34
            r52 = r36
            r47 = r37
            r50 = r39
            r46 = r40
        L_0x05a8:
            r1 = 0
            r4 = 90
            r15 = 0
            r24 = 73
            r32 = 0
            boolean r6 = r13.isVideo
            if (r6 != 0) goto L_0x09c8
            org.telegram.messenger.VideoEditedInfo r6 = r13.videoEditedInfo
            if (r6 == 0) goto L_0x05ba
            goto L_0x09c8
        L_0x05ba:
            java.lang.String r0 = r13.path
            if (r0 != 0) goto L_0x05ce
            android.net.Uri r2 = r13.uri
            if (r2 == 0) goto L_0x05ce
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r2)
            android.net.Uri r2 = r13.uri
            java.lang.String r2 = r2.toString()
            r3 = r0
            goto L_0x05d0
        L_0x05ce:
            r2 = r0
            r3 = r2
        L_0x05d0:
            if (r80 == 0) goto L_0x0663
            android.net.Uri r0 = r13.uri
            if (r0 == 0) goto L_0x0663
            android.content.ClipDescription r0 = r80.getDescription()
            java.lang.String r4 = "image/png"
            boolean r0 = r0.hasMimeType(r4)
            if (r0 == 0) goto L_0x0663
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0642 }
            r0.<init>()     // Catch:{ all -> 0x0642 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0642 }
            android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x0642 }
            android.net.Uri r6 = r13.uri     // Catch:{ all -> 0x0642 }
            java.io.InputStream r4 = r4.openInputStream(r6)     // Catch:{ all -> 0x0642 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r4, r1, r0)     // Catch:{ all -> 0x063e }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x063e }
            r6.<init>()     // Catch:{ all -> 0x063e }
            java.lang.String r9 = "-2147483648_"
            r6.append(r9)     // Catch:{ all -> 0x063e }
            int r9 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ all -> 0x063e }
            r6.append(r9)     // Catch:{ all -> 0x063e }
            r12 = r28
            r6.append(r12)     // Catch:{ all -> 0x063c }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x063c }
            java.io.File r9 = org.telegram.messenger.FileLoader.getDirectory(r41)     // Catch:{ all -> 0x063c }
            java.io.File r10 = new java.io.File     // Catch:{ all -> 0x063c }
            r10.<init>(r9, r6)     // Catch:{ all -> 0x063c }
            java.io.FileOutputStream r6 = new java.io.FileOutputStream     // Catch:{ all -> 0x063c }
            r6.<init>(r10)     // Catch:{ all -> 0x063c }
            android.graphics.Bitmap$CompressFormat r9 = android.graphics.Bitmap.CompressFormat.WEBP     // Catch:{ all -> 0x063a }
            r1 = 100
            r0.compress(r9, r1, r6)     // Catch:{ all -> 0x063a }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ all -> 0x063a }
            android.net.Uri r0 = android.net.Uri.fromFile(r10)     // Catch:{ all -> 0x063a }
            r13.uri = r0     // Catch:{ all -> 0x063a }
            if (r4 == 0) goto L_0x0634
            r4.close()     // Catch:{ Exception -> 0x0634 }
        L_0x0634:
            r6.close()     // Catch:{ Exception -> 0x0638 }
            goto L_0x0665
        L_0x0638:
            goto L_0x0665
        L_0x063a:
            r0 = move-exception
            goto L_0x0647
        L_0x063c:
            r0 = move-exception
            goto L_0x0646
        L_0x063e:
            r0 = move-exception
            r12 = r28
            goto L_0x0646
        L_0x0642:
            r0 = move-exception
            r12 = r28
            r4 = 0
        L_0x0646:
            r6 = 0
        L_0x0647:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0654 }
            if (r4 == 0) goto L_0x0651
            r4.close()     // Catch:{ Exception -> 0x0650 }
            goto L_0x0651
        L_0x0650:
        L_0x0651:
            if (r6 == 0) goto L_0x0665
            goto L_0x0634
        L_0x0654:
            r0 = move-exception
            r1 = r0
            if (r4 == 0) goto L_0x065d
            r4.close()     // Catch:{ Exception -> 0x065c }
            goto L_0x065d
        L_0x065c:
        L_0x065d:
            if (r6 == 0) goto L_0x0662
            r6.close()     // Catch:{ Exception -> 0x0662 }
        L_0x0662:
            throw r1
        L_0x0663:
            r12 = r28
        L_0x0665:
            java.lang.String r0 = "gif"
            java.lang.String r1 = "webp"
            if (r73 != 0) goto L_0x06c3
            java.lang.String r4 = r13.path
            android.net.Uri r6 = r13.uri
            boolean r4 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r4, r6)
            if (r4 == 0) goto L_0x0677
            goto L_0x06c3
        L_0x0677:
            r10 = r53
            if (r3 == 0) goto L_0x068e
            boolean r4 = r3.endsWith(r10)
            if (r4 != 0) goto L_0x0687
            boolean r4 = r3.endsWith(r12)
            if (r4 == 0) goto L_0x068e
        L_0x0687:
            boolean r4 = r3.endsWith(r10)
            if (r4 == 0) goto L_0x06bd
            goto L_0x06a6
        L_0x068e:
            if (r3 != 0) goto L_0x06c0
            android.net.Uri r4 = r13.uri
            if (r4 == 0) goto L_0x06c0
            boolean r4 = org.telegram.messenger.MediaController.isGif(r4)
            if (r4 == 0) goto L_0x06a9
            android.net.Uri r1 = r13.uri
            java.lang.String r2 = r1.toString()
            android.net.Uri r1 = r13.uri
            java.lang.String r3 = org.telegram.messenger.MediaController.copyFileToCache(r1, r0)
        L_0x06a6:
            r23 = r0
            goto L_0x06d3
        L_0x06a9:
            android.net.Uri r0 = r13.uri
            boolean r0 = org.telegram.messenger.MediaController.isWebp(r0)
            if (r0 == 0) goto L_0x06c0
            android.net.Uri r0 = r13.uri
            java.lang.String r2 = r0.toString()
            android.net.Uri r0 = r13.uri
            java.lang.String r3 = org.telegram.messenger.MediaController.copyFileToCache(r0, r1)
        L_0x06bd:
            r23 = r1
            goto L_0x06d3
        L_0x06c0:
            r1 = r3
            r0 = 0
            goto L_0x06d5
        L_0x06c3:
            r10 = r53
            if (r3 == 0) goto L_0x06d1
            java.io.File r0 = new java.io.File
            r0.<init>(r3)
            java.lang.String r0 = org.telegram.messenger.FileLoader.getFileExtension(r0)
            goto L_0x06a6
        L_0x06d1:
            r23 = r5
        L_0x06d3:
            r1 = r3
            r0 = 1
        L_0x06d5:
            if (r0 == 0) goto L_0x0732
            r9 = r47
            if (r9 != 0) goto L_0x06f7
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
            goto L_0x0700
        L_0x06f7:
            r5 = r9
            r6 = r29
            r4 = r45
            r3 = r46
            r0 = r50
        L_0x0700:
            r5.add(r1)
            r4.add(r2)
            android.net.Uri r1 = r13.uri
            r3.add(r1)
            java.lang.String r1 = r13.caption
            r0.add(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r13.entities
            r6.add(r1)
            r2 = r0
            r29 = r6
            r22 = r10
            r0 = r17
            r34 = r27
            r37 = r52
            r36 = r54
            r40 = r55
            r1 = r57
            r68 = r58
            r39 = r59
            r26 = 0
            r38 = 0
            r27 = r12
            goto L_0x0e06
        L_0x0732:
            r9 = r47
            if (r1 == 0) goto L_0x075f
            java.io.File r0 = new java.io.File
            r0.<init>(r1)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r6 = r5
            long r4 = r0.length()
            r3.append(r4)
            r5 = r27
            r3.append(r5)
            long r4 = r0.lastModified()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r5 = r3
            r4 = r54
            goto L_0x0763
        L_0x075f:
            r6 = r5
            r4 = r54
            r5 = 0
        L_0x0763:
            if (r4 == 0) goto L_0x0794
            java.lang.Object r0 = r4.get(r13)
            r2 = r0
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
            if (r0 != 0) goto L_0x0780
            java.util.concurrent.CountDownLatch r0 = r2.sync     // Catch:{ Exception -> 0x0778 }
            r0.await()     // Catch:{ Exception -> 0x0778 }
            goto L_0x077c
        L_0x0778:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x077c:
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
        L_0x0780:
            r16 = r1
            r36 = r4
            r15 = r6
            r47 = r9
            r53 = r10
            r61 = r27
            r1 = 1
            r6 = r0
            r9 = r5
            r10 = r7
            r7 = r59
            r5 = r3
            goto L_0x0854
        L_0x0794:
            r3 = r59
            if (r3 != 0) goto L_0x081a
            int r0 = r13.ttl
            if (r0 != 0) goto L_0x081a
            org.telegram.messenger.MessagesStorage r0 = r72.getMessagesStorage()
            if (r3 != 0) goto L_0x07a4
            r2 = 0
            goto L_0x07a5
        L_0x07a4:
            r2 = 3
        L_0x07a5:
            java.lang.Object[] r0 = r0.getSentFile(r5, r2)
            if (r0 == 0) goto L_0x07bd
            r2 = r0[r15]
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r2 == 0) goto L_0x07bd
            r2 = r0[r15]
            org.telegram.tgnet.TLRPC$TL_photo r2 = (org.telegram.tgnet.TLRPC$TL_photo) r2
            r16 = r6
            r6 = 1
            r0 = r0[r6]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x07c2
        L_0x07bd:
            r16 = r6
            r6 = 1
            r0 = 0
            r2 = 0
        L_0x07c2:
            if (r2 != 0) goto L_0x07ef
            android.net.Uri r6 = r13.uri
            if (r6 == 0) goto L_0x07f2
            org.telegram.messenger.MessagesStorage r6 = r72.getMessagesStorage()
            android.net.Uri r15 = r13.uri
            java.lang.String r15 = org.telegram.messenger.AndroidUtilities.getPath(r15)
            r22 = r0
            if (r3 != 0) goto L_0x07d8
            r0 = 0
            goto L_0x07d9
        L_0x07d8:
            r0 = 3
        L_0x07d9:
            java.lang.Object[] r0 = r6.getSentFile(r15, r0)
            if (r0 == 0) goto L_0x07f4
            r6 = 0
            r15 = r0[r6]
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r15 == 0) goto L_0x07f4
            r2 = r0[r6]
            org.telegram.tgnet.TLRPC$TL_photo r2 = (org.telegram.tgnet.TLRPC$TL_photo) r2
            r6 = 1
            r0 = r0[r6]
            java.lang.String r0 = (java.lang.String) r0
        L_0x07ef:
            r22 = r0
            goto L_0x07f5
        L_0x07f2:
            r22 = r0
        L_0x07f4:
            r6 = 1
        L_0x07f5:
            r0 = r2
            java.lang.String r15 = r13.path
            android.net.Uri r2 = r13.uri
            r34 = 0
            r28 = r2
            r2 = r3
            r60 = r3
            r3 = r0
            r36 = r4
            r4 = r15
            r47 = r9
            r15 = r16
            r61 = r27
            r9 = r5
            r5 = r28
            r16 = r1
            r53 = r10
            r1 = 1
            r10 = r7
            r6 = r34
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            goto L_0x082d
        L_0x081a:
            r16 = r1
            r60 = r3
            r36 = r4
            r15 = r6
            r47 = r9
            r53 = r10
            r61 = r27
            r1 = 1
            r9 = r5
            r10 = r7
            r3 = 0
            r22 = 0
        L_0x082d:
            if (r3 != 0) goto L_0x084f
            org.telegram.messenger.SendMessagesHelper r0 = r72.getSendMessagesHelper()
            java.lang.String r2 = r13.path
            android.net.Uri r3 = r13.uri
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r3)
            r7 = r60
            if (r7 == 0) goto L_0x084d
            boolean r2 = r13.canDeleteAfter
            if (r2 == 0) goto L_0x084d
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r13.path
            r2.<init>(r3)
            r2.delete()
        L_0x084d:
            r6 = r0
            goto L_0x0852
        L_0x084f:
            r7 = r60
            r6 = r3
        L_0x0852:
            r5 = r22
        L_0x0854:
            if (r6 == 0) goto L_0x0962
            java.util.HashMap r4 = new java.util.HashMap
            r4.<init>()
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r1]
            java.lang.String[] r2 = new java.lang.String[r1]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r0 = r13.masks
            if (r0 == 0) goto L_0x086b
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x086b
            r0 = 1
            goto L_0x086c
        L_0x086b:
            r0 = 0
        L_0x086c:
            r6.has_stickers = r0
            if (r0 == 0) goto L_0x08b2
            org.telegram.tgnet.SerializedData r0 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r13.masks
            int r1 = r1.size()
            int r1 = r1 * 20
            int r1 = r1 + 4
            r0.<init>((int) r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r13.masks
            int r1 = r1.size()
            r0.writeInt32(r1)
            r48 = r7
            r1 = 0
        L_0x088b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r7 = r13.masks
            int r7 = r7.size()
            if (r1 >= r7) goto L_0x08a1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r7 = r13.masks
            java.lang.Object r7 = r7.get(r1)
            org.telegram.tgnet.TLRPC$InputDocument r7 = (org.telegram.tgnet.TLRPC$InputDocument) r7
            r7.serializeToStream(r0)
            int r1 = r1 + 1
            goto L_0x088b
        L_0x08a1:
            byte[] r1 = r0.toByteArray()
            java.lang.String r1 = org.telegram.messenger.Utilities.bytesToHex(r1)
            java.lang.String r7 = "masks"
            r4.put(r7, r1)
            r0.cleanup()
            goto L_0x08b4
        L_0x08b2:
            r48 = r7
        L_0x08b4:
            if (r9 == 0) goto L_0x08b9
            r4.put(r8, r9)
        L_0x08b9:
            if (r5 == 0) goto L_0x08c0
            java.lang.String r0 = "parentObject"
            r4.put(r0, r5)
        L_0x08c0:
            if (r74 == 0) goto L_0x08cc
            int r0 = r69.size()     // Catch:{ Exception -> 0x08e2 }
            r1 = 1
            if (r0 != r1) goto L_0x08ca
            goto L_0x08cc
        L_0x08ca:
            r1 = 0
            goto L_0x08e7
        L_0x08cc:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r6.sizes     // Catch:{ Exception -> 0x08e2 }
            int r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize()     // Catch:{ Exception -> 0x08e2 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r1)     // Catch:{ Exception -> 0x08e2 }
            if (r0 == 0) goto L_0x08ca
            r1 = 0
            java.lang.String r0 = getKeyForPhotoSize(r0, r3, r1, r1)     // Catch:{ Exception -> 0x08e0 }
            r2[r1] = r0     // Catch:{ Exception -> 0x08e0 }
            goto L_0x08e7
        L_0x08e0:
            r0 = move-exception
            goto L_0x08e4
        L_0x08e2:
            r0 = move-exception
            r1 = 0
        L_0x08e4:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x08e7:
            if (r74 == 0) goto L_0x091b
            int r0 = r17 + 1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r15)
            r8 = r55
            r7.append(r8)
            java.lang.String r7 = r7.toString()
            r4.put(r11, r7)
            r7 = 10
            if (r0 == r7) goto L_0x090f
            r15 = r58
            int r7 = r15 + -1
            r11 = r57
            if (r11 != r7) goto L_0x090c
            goto L_0x0913
        L_0x090c:
            r17 = r0
            goto L_0x0921
        L_0x090f:
            r11 = r57
            r15 = r58
        L_0x0913:
            r4.put(r14, r10)
            r17 = r0
            r30 = r32
            goto L_0x0921
        L_0x091b:
            r8 = r55
            r11 = r57
            r15 = r58
        L_0x0921:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$Ld7ejTQFGVlq9Wt5NCC-Z9_jJzs r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Ld7ejTQFGVlq9Wt5NCC-Z9_jJzs
            r7 = r2
            r2 = r0
            r10 = r4
            r4 = r7
            r22 = r5
            r5 = r75
            r7 = r6
            r6 = r72
            r14 = r48
            r62 = r8
            r8 = r10
            r10 = r47
            r9 = r22
            r1 = r10
            r49 = r11
            r22 = r53
            r10 = r70
            r27 = r12
            r12 = r76
            r28 = r13
            r13 = r77
            r64 = r14
            r14 = r28
            r65 = r15
            r15 = r78
            r16 = r79
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            r5 = r1
            r0 = r17
            r4 = r45
            r3 = r46
            r1 = r49
            r2 = r50
            goto L_0x09ba
        L_0x0962:
            r64 = r7
            r27 = r12
            r28 = r13
            r1 = r47
            r22 = r53
            r62 = r55
            r49 = r57
            r65 = r58
            if (r1 != 0) goto L_0x0992
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
            r1 = r16
            r0 = r29
            goto L_0x099d
        L_0x0992:
            r5 = r1
            r1 = r16
            r0 = r29
            r4 = r45
            r3 = r46
            r2 = r50
        L_0x099d:
            r5.add(r1)
            r4.add(r9)
            r12 = r28
            android.net.Uri r1 = r12.uri
            r3.add(r1)
            java.lang.String r1 = r12.caption
            r2.add(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r12.entities
            r0.add(r1)
            r29 = r0
            r0 = r17
            r1 = r49
        L_0x09ba:
            r37 = r52
            r34 = r61
            r40 = r62
            r39 = r64
            r68 = r65
            r26 = 0
            goto L_0x0573
        L_0x09c8:
            r15 = r5
            r10 = r7
            r12 = r13
            r61 = r27
            r27 = r28
            r1 = r47
            r22 = r53
            r36 = r54
            r62 = r55
            r49 = r57
            r65 = r58
            r64 = r59
            if (r73 == 0) goto L_0x09e1
            r9 = 0
            goto L_0x09ed
        L_0x09e1:
            org.telegram.messenger.VideoEditedInfo r5 = r12.videoEditedInfo
            if (r5 == 0) goto L_0x09e6
            goto L_0x09ec
        L_0x09e6:
            java.lang.String r5 = r12.path
            org.telegram.messenger.VideoEditedInfo r5 = createCompressionSettings(r5)
        L_0x09ec:
            r9 = r5
        L_0x09ed:
            if (r73 != 0) goto L_0x0da9
            if (r9 != 0) goto L_0x09f9
            java.lang.String r5 = r12.path
            boolean r0 = r5.endsWith(r0)
            if (r0 == 0) goto L_0x0da9
        L_0x09f9:
            java.lang.String r0 = r12.path
            if (r0 != 0) goto L_0x0a44
            org.telegram.messenger.MediaController$SearchImage r0 = r12.searchImage
            if (r0 == 0) goto L_0x0a44
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r5 == 0) goto L_0x0a13
            r5 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r5)
            java.lang.String r0 = r0.getAbsolutePath()
            r12.path = r0
            goto L_0x0a44
        L_0x0a13:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r5 = r12.searchImage
            java.lang.String r5 = r5.imageUrl
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)
            r0.append(r5)
            r0.append(r3)
            org.telegram.messenger.MediaController$SearchImage r3 = r12.searchImage
            java.lang.String r3 = r3.imageUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r3, r2)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r2 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r2.<init>(r3, r0)
            java.lang.String r0 = r2.getAbsolutePath()
            r12.path = r0
        L_0x0a44:
            java.lang.String r0 = r12.path
            java.io.File r13 = new java.io.File
            r13.<init>(r0)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            long r5 = r13.length()
            r2.append(r5)
            r6 = r61
            r2.append(r6)
            long r4 = r13.lastModified()
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            if (r9 == 0) goto L_0x0ac5
            boolean r3 = r9.muted
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r5 = r3
            long r2 = r9.estimatedDuration
            r4.append(r2)
            r4.append(r6)
            long r2 = r9.startTime
            r4.append(r2)
            r4.append(r6)
            long r2 = r9.endTime
            r4.append(r2)
            boolean r2 = r9.muted
            if (r2 == 0) goto L_0x0a93
            java.lang.String r2 = "_m"
            goto L_0x0a94
        L_0x0a93:
            r2 = r15
        L_0x0a94:
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            int r3 = r9.resultWidth
            int r4 = r9.originalWidth
            if (r3 == r4) goto L_0x0ab5
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r6)
            int r2 = r9.resultWidth
            r3.append(r2)
            java.lang.String r2 = r3.toString()
        L_0x0ab5:
            long r3 = r9.startTime
            int r7 = (r3 > r32 ? 1 : (r3 == r32 ? 0 : -1))
            if (r7 < 0) goto L_0x0abc
            goto L_0x0abe
        L_0x0abc:
            r3 = r32
        L_0x0abe:
            r7 = r2
            r16 = r5
            r4 = r3
            r3 = r64
            goto L_0x0acc
        L_0x0ac5:
            r7 = r2
            r4 = r32
            r3 = r64
            r16 = 0
        L_0x0acc:
            if (r3 != 0) goto L_0x0b36
            int r2 = r12.ttl
            if (r2 != 0) goto L_0x0b36
            if (r9 == 0) goto L_0x0ae4
            org.telegram.messenger.MediaController$SavedFilterState r2 = r9.filterState
            if (r2 != 0) goto L_0x0b36
            java.lang.String r2 = r9.paintPath
            if (r2 != 0) goto L_0x0b36
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r2 = r9.mediaEntities
            if (r2 != 0) goto L_0x0b36
            org.telegram.messenger.MediaController$CropState r2 = r9.cropState
            if (r2 != 0) goto L_0x0b36
        L_0x0ae4:
            org.telegram.messenger.MessagesStorage r2 = r72.getMessagesStorage()
            if (r3 != 0) goto L_0x0af0
            r18 = 2
            r28 = r0
            r0 = 2
            goto L_0x0af5
        L_0x0af0:
            r18 = 5
            r28 = r0
            r0 = 5
        L_0x0af5:
            java.lang.Object[] r0 = r2.getSentFile(r7, r0)
            if (r0 == 0) goto L_0x0b38
            r34 = r4
            r2 = 0
            r4 = r0[r2]
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r4 == 0) goto L_0x0b2b
            r4 = r0[r2]
            r18 = r4
            org.telegram.tgnet.TLRPC$TL_document r18 = (org.telegram.tgnet.TLRPC$TL_document) r18
            r4 = 1
            r0 = r0[r4]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r4 = r12.path
            r5 = 0
            r2 = r3
            r66 = r3
            r3 = r18
            r39 = r34
            r34 = 90
            r35 = r0
            r0 = 90
            r34 = r6
            r67 = r7
            r6 = r39
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r7 = r35
            goto L_0x0b44
        L_0x0b2b:
            r66 = r3
            r67 = r7
            r39 = r34
            r0 = 90
            r34 = r6
            goto L_0x0b42
        L_0x0b36:
            r28 = r0
        L_0x0b38:
            r66 = r3
            r39 = r4
            r34 = r6
            r67 = r7
            r0 = 90
        L_0x0b42:
            r3 = 0
            r7 = 0
        L_0x0b44:
            if (r3 != 0) goto L_0x0CLASSNAME
            java.lang.String r2 = r12.thumbPath
            if (r2 == 0) goto L_0x0b4f
            android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeFile(r2)
            goto L_0x0b50
        L_0x0b4f:
            r3 = 0
        L_0x0b50:
            if (r3 != 0) goto L_0x0b64
            java.lang.String r2 = r12.path
            r3 = r39
            android.graphics.Bitmap r3 = createVideoThumbnailAtTime(r2, r3)
            if (r3 != 0) goto L_0x0b64
            java.lang.String r2 = r12.path
            r3 = 1
            android.graphics.Bitmap r2 = createVideoThumbnail(r2, r3)
            r3 = r2
        L_0x0b64:
            if (r3 == 0) goto L_0x0b94
            r6 = r66
            if (r6 != 0) goto L_0x0b7c
            int r2 = r12.ttl
            if (r2 == 0) goto L_0x0b6f
            goto L_0x0b7c
        L_0x0b6f:
            int r2 = r3.getWidth()
            int r4 = r3.getHeight()
            int r5 = java.lang.Math.max(r2, r4)
            goto L_0x0b7e
        L_0x0b7c:
            r5 = 90
        L_0x0b7e:
            float r2 = (float) r5
            if (r5 <= r0) goto L_0x0b84
            r4 = 80
            goto L_0x0b86
        L_0x0b84:
            r4 = 55
        L_0x0b86:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r3, r2, r2, r4, r6)
            r18 = r3
            r3 = 1
            r4 = 0
            r5 = 0
            java.lang.String r35 = getKeyForPhotoSize(r2, r4, r3, r5)
            goto L_0x0b9d
        L_0x0b94:
            r18 = r3
            r6 = r66
            r3 = 1
            r5 = 0
            r2 = 0
            r35 = 0
        L_0x0b9d:
            org.telegram.tgnet.TLRPC$TL_document r4 = new org.telegram.tgnet.TLRPC$TL_document
            r4.<init>()
            byte[] r0 = new byte[r5]
            r4.file_reference = r0
            if (r2 == 0) goto L_0x0bb2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r4.thumbs
            r0.add(r2)
            int r0 = r4.flags
            r0 = r0 | r3
            r4.flags = r0
        L_0x0bb2:
            java.lang.String r0 = "video/mp4"
            r4.mime_type = r0
            org.telegram.messenger.UserConfig r0 = r72.getUserConfig()
            r0.saveConfig(r5)
            if (r6 == 0) goto L_0x0bd2
            r0 = 66
            r2 = r52
            if (r2 < r0) goto L_0x0bcc
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r0.<init>()
            goto L_0x0bdc
        L_0x0bcc:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r0.<init>()
            goto L_0x0bdc
        L_0x0bd2:
            r2 = r52
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r0.<init>()
            r3 = 1
            r0.supports_streaming = r3
        L_0x0bdc:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r4.attributes
            r3.add(r0)
            if (r9 == 0) goto L_0x0CLASSNAME
            boolean r3 = r9.needConvert()
            if (r3 != 0) goto L_0x0bed
            boolean r3 = r12.isVideo
            if (r3 != 0) goto L_0x0CLASSNAME
        L_0x0bed:
            boolean r3 = r12.isVideo
            if (r3 == 0) goto L_0x0CLASSNAME
            boolean r3 = r9.muted
            if (r3 == 0) goto L_0x0CLASSNAME
            java.lang.String r3 = r12.path
            fillVideoAttribute(r3, r0, r9)
            int r3 = r0.w
            r9.originalWidth = r3
            int r3 = r0.h
            r9.originalHeight = r3
            r48 = r6
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r48 = r6
            long r5 = r9.estimatedDuration
            r39 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r39
            int r3 = (int) r5
            r0.duration = r3
        L_0x0CLASSNAME:
            int r3 = r9.rotationValue
            org.telegram.messenger.MediaController$CropState r5 = r9.cropState
            if (r5 == 0) goto L_0x0c1b
            int r6 = r5.transformWidth
            int r5 = r5.transformHeight
            goto L_0x0c1f
        L_0x0c1b:
            int r6 = r9.resultWidth
            int r5 = r9.resultHeight
        L_0x0c1f:
            r13 = 90
            if (r3 == r13) goto L_0x0c2d
            r13 = 270(0x10e, float:3.78E-43)
            if (r3 != r13) goto L_0x0CLASSNAME
            goto L_0x0c2d
        L_0x0CLASSNAME:
            r0.w = r6
            r0.h = r5
            goto L_0x0CLASSNAME
        L_0x0c2d:
            r0.w = r5
            r0.h = r6
        L_0x0CLASSNAME:
            long r5 = r9.estimatedSize
            int r0 = (int) r5
            r4.size = r0
            r13 = 0
            goto L_0x0c4d
        L_0x0CLASSNAME:
            r48 = r6
            boolean r3 = r13.exists()
            if (r3 == 0) goto L_0x0CLASSNAME
            long r5 = r13.length()
            int r3 = (int) r5
            r4.size = r3
        L_0x0CLASSNAME:
            java.lang.String r3 = r12.path
            r13 = 0
            fillVideoAttribute(r3, r0, r13)
        L_0x0c4d:
            r0 = r4
            r3 = r18
            r4 = r35
            goto L_0x0c5b
        L_0x0CLASSNAME:
            r2 = r52
            r48 = r66
            r13 = 0
            r0 = r3
            r3 = r13
            r4 = r3
        L_0x0c5b:
            if (r9 == 0) goto L_0x0CLASSNAME
            boolean r5 = r9.muted
            if (r5 == 0) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r0.attributes
            int r5 = r5.size()
            r6 = 0
        L_0x0CLASSNAME:
            if (r6 >= r5) goto L_0x0c7a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r0.attributes
            java.lang.Object r13 = r13.get(r6)
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            if (r13 == 0) goto L_0x0CLASSNAME
            r5 = 1
            goto L_0x0c7b
        L_0x0CLASSNAME:
            int r6 = r6 + 1
            r13 = 0
            goto L_0x0CLASSNAME
        L_0x0c7a:
            r5 = 0
        L_0x0c7b:
            if (r5 != 0) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r0.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r6.<init>()
            r5.add(r6)
        L_0x0CLASSNAME:
            if (r9 == 0) goto L_0x0cbf
            boolean r5 = r9.needConvert()
            if (r5 != 0) goto L_0x0CLASSNAME
            boolean r5 = r12.isVideo
            if (r5 != 0) goto L_0x0cbf
        L_0x0CLASSNAME:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "-2147483648_"
            r5.append(r6)
            int r6 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r5.append(r6)
            java.lang.String r6 = ".mp4"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.io.File r6 = new java.io.File
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r6.<init>(r13, r5)
            org.telegram.messenger.SharedConfig.saveConfig()
            java.lang.String r5 = r6.getAbsolutePath()
            r28 = r5
        L_0x0cbf:
            java.util.HashMap r13 = new java.util.HashMap
            r13.<init>()
            r5 = r67
            if (r5 == 0) goto L_0x0ccb
            r13.put(r8, r5)
        L_0x0ccb:
            if (r7 == 0) goto L_0x0cd2
            java.lang.String r5 = "parentObject"
            r13.put(r5, r7)
        L_0x0cd2:
            if (r16 != 0) goto L_0x0d0d
            if (r74 == 0) goto L_0x0d0d
            int r5 = r17 + 1
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r15)
            r15 = r7
            r7 = r62
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r13.put(r11, r6)
            r6 = 10
            if (r5 == r6) goto L_0x0cff
            r11 = r65
            int r6 = r11 + -1
            r47 = r1
            r1 = r49
            if (r1 != r6) goto L_0x0cfc
            goto L_0x0d05
        L_0x0cfc:
            r25 = r5
            goto L_0x0d18
        L_0x0cff:
            r47 = r1
            r1 = r49
            r11 = r65
        L_0x0d05:
            r13.put(r14, r10)
            r25 = r5
            r30 = r32
            goto L_0x0d18
        L_0x0d0d:
            r47 = r1
            r15 = r7
            r1 = r49
            r7 = r62
            r11 = r65
            r25 = r17
        L_0x0d18:
            if (r48 != 0) goto L_0x0d6d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r12.masks
            if (r5 == 0) goto L_0x0d6d
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0d6d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r0.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers
            r6.<init>()
            r5.add(r6)
            org.telegram.tgnet.SerializedData r5 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r6 = r12.masks
            int r6 = r6.size()
            int r6 = r6 * 20
            int r6 = r6 + 4
            r5.<init>((int) r6)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r6 = r12.masks
            int r6 = r6.size()
            r5.writeInt32(r6)
            r6 = 0
        L_0x0d47:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r10 = r12.masks
            int r10 = r10.size()
            if (r6 >= r10) goto L_0x0d5d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r10 = r12.masks
            java.lang.Object r10 = r10.get(r6)
            org.telegram.tgnet.TLRPC$InputDocument r10 = (org.telegram.tgnet.TLRPC$InputDocument) r10
            r10.serializeToStream(r5)
            int r6 = r6 + 1
            goto L_0x0d47
        L_0x0d5d:
            byte[] r6 = r5.toByteArray()
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)
            java.lang.String r10 = "masks"
            r13.put(r10, r6)
            r5.cleanup()
        L_0x0d6d:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$rfPFTaYVAXx9vbstjTS-wnTMmbc r35 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$rfPFTaYVAXx9vbstjTS-wnTMmbc
            r37 = r2
            r2 = r35
            r38 = 0
            r5 = r75
            r39 = r48
            r6 = r72
            r40 = r7
            r14 = r15
            r7 = r9
            r8 = r0
            r9 = r28
            r10 = r13
            r15 = r11
            r11 = r14
            r14 = r12
            r26 = 0
            r12 = r70
            r28 = r14
            r14 = r76
            r68 = r15
            r15 = r77
            r16 = r28
            r17 = r78
            r18 = r79
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r35)
            r17 = r25
            r4 = r45
            r3 = r46
            r5 = r47
            r2 = r50
            goto L_0x0e04
        L_0x0da9:
            r47 = r1
            r28 = r12
            r1 = r49
            r37 = r52
            r34 = r61
            r40 = r62
            r39 = r64
            r68 = r65
            r26 = 0
            r38 = 0
            if (r47 != 0) goto L_0x0ddd
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
            r6 = r28
            r0 = r29
            goto L_0x0de9
        L_0x0ddd:
            r6 = r28
            r0 = r29
            r4 = r45
            r3 = r46
            r5 = r47
            r2 = r50
        L_0x0de9:
            java.lang.String r7 = r6.path
            r5.add(r7)
            java.lang.String r7 = r6.path
            r4.add(r7)
            android.net.Uri r7 = r6.uri
            r3.add(r7)
            java.lang.String r7 = r6.caption
            r2.add(r7)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r6.entities
            r0.add(r6)
            r29 = r0
        L_0x0e04:
            r0 = r17
        L_0x0e06:
            int r9 = r1 + 1
            r1 = r69
            r13 = r72
            r16 = r22
            r28 = r27
            r27 = r34
            r15 = r36
            r8 = r37
            r10 = r39
            r24 = r40
            r12 = r68
            r14 = 73
            goto L_0x01a9
        L_0x0e20:
            r50 = r2
            r46 = r3
            r45 = r4
            r47 = r5
            r39 = r10
            r68 = r12
            r6 = r30
            r32 = 0
            r38 = 0
            int r1 = (r6 > r32 ? 1 : (r6 == r32 ? 0 : -1))
            r15 = r72
            r14 = r79
            if (r1 == 0) goto L_0x0e3d
            finishGroup(r15, r6, r14)
        L_0x0e3d:
            if (r80 == 0) goto L_0x0e42
            r80.releasePermission()
        L_0x0e42:
            if (r47 == 0) goto L_0x0ef4
            boolean r1 = r47.isEmpty()
            if (r1 != 0) goto L_0x0ef4
            r1 = 1
            long[] r13 = new long[r1]
            int r12 = r47.size()
            r11 = r0
            r0 = 0
        L_0x0e53:
            if (r0 >= r12) goto L_0x0ef4
            if (r73 == 0) goto L_0x0e6c
            if (r39 != 0) goto L_0x0e6c
            r10 = r68
            if (r10 <= r1) goto L_0x0e6e
            int r1 = r11 % 10
            if (r1 != 0) goto L_0x0e6e
            java.security.SecureRandom r1 = org.telegram.messenger.Utilities.random
            long r1 = r1.nextLong()
            r13[r38] = r1
            r1 = 1
            r11 = 0
            goto L_0x0e6f
        L_0x0e6c:
            r10 = r68
        L_0x0e6e:
            r1 = 1
        L_0x0e6f:
            int r11 = r11 + r1
            r5 = r47
            java.lang.Object r2 = r5.get(r0)
            java.lang.String r2 = (java.lang.String) r2
            r9 = r45
            java.lang.Object r3 = r9.get(r0)
            java.lang.String r3 = (java.lang.String) r3
            r8 = r46
            java.lang.Object r4 = r8.get(r0)
            android.net.Uri r4 = (android.net.Uri) r4
            r6 = r50
            java.lang.Object r7 = r6.get(r0)
            r16 = r7
            java.lang.CharSequence r16 = (java.lang.CharSequence) r16
            r7 = r29
            java.lang.Object r17 = r7.get(r0)
            java.util.ArrayList r17 = (java.util.ArrayList) r17
            r21 = r13
            r13 = 10
            if (r11 == r13) goto L_0x0ea8
            int r1 = r12 + -1
            if (r0 != r1) goto L_0x0ea5
            goto L_0x0ea8
        L_0x0ea5:
            r22 = 0
            goto L_0x0eaa
        L_0x0ea8:
            r22 = 1
        L_0x0eaa:
            r18 = 0
            r24 = r5
            r25 = 1
            r1 = r72
            r5 = r23
            r26 = r6
            r29 = r7
            r6 = r70
            r27 = r8
            r8 = r76
            r28 = r9
            r9 = r77
            r30 = r10
            r10 = r16
            r31 = r11
            r32 = 10
            r11 = r17
            r33 = r12
            r12 = r75
            r13 = r21
            r14 = r22
            r15 = r73
            r16 = r78
            r17 = r79
            prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            int r0 = r0 + 1
            r15 = r72
            r14 = r79
            r47 = r24
            r50 = r26
            r46 = r27
            r45 = r28
            r68 = r30
            r11 = r31
            r12 = r33
            r1 = 1
            goto L_0x0e53
        L_0x0ef4:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0var_
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "total send time = "
            r0.append(r1)
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r19
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0var_:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$79(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, boolean, boolean, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    static /* synthetic */ void lambda$null$74(MediaSendPrepareWorker mediaSendPrepareWorker, AccountInstance accountInstance, SendingMediaInfo sendingMediaInfo, boolean z) {
        mediaSendPrepareWorker.photo = accountInstance.getSendMessagesHelper().generatePhotoSizes(sendingMediaInfo.path, sendingMediaInfo.uri);
        if (z && sendingMediaInfo.canDeleteAfter) {
            new File(sendingMediaInfo.path).delete();
        }
        mediaSendPrepareWorker.sync.countDown();
    }

    static /* synthetic */ void lambda$null$75(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, str, hashMap, false, str2);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str2);
    }

    static /* synthetic */ void lambda$null$76(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, boolean z2, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        String str2 = null;
        if (messageObject != null) {
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            if (z) {
                str2 = sendingMediaInfo2.searchImage.imageUrl;
            }
            sendMessagesHelper.editMessage(messageObject, tLRPC$TL_photo, (VideoEditedInfo) null, (TLRPC$TL_document) null, str2, hashMap, false, str);
            return;
        }
        SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
        if (z) {
            str2 = sendingMediaInfo2.searchImage.imageUrl;
        }
        sendMessagesHelper2.sendMessage(tLRPC$TL_photo, str2, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z2, i, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$77(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        Bitmap bitmap2 = bitmap;
        String str4 = str;
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmap2 == null || str4 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str4);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, sendingMediaInfo2.ttl, str3);
    }

    static /* synthetic */ void lambda$null$78(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, tLRPC$TL_photo, (VideoEditedInfo) null, (TLRPC$TL_document) null, (String) null, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_photo, (String) null, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, sendingMediaInfo2.ttl, str);
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
            videoEditedInfo.resultWidth = Math.round((((float) i7) * f3) / 2.0f) * 2;
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

    /* JADX WARNING: Removed duplicated region for block: B:128:0x0311  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x031d  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x034a  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0352  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0358  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x0363  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x011f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ void lambda$prepareSendingVideo$81(org.telegram.messenger.VideoEditedInfo r28, java.lang.String r29, long r30, int r32, org.telegram.messenger.AccountInstance r33, java.lang.CharSequence r34, org.telegram.messenger.MessageObject r35, org.telegram.messenger.MessageObject r36, org.telegram.messenger.MessageObject r37, java.util.ArrayList r38, boolean r39, int r40) {
        /*
            r6 = r29
            r10 = r30
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r28 == 0) goto L_0x000b
            r8 = r28
            goto L_0x0010
        L_0x000b:
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r29)
            r8 = r0
        L_0x0010:
            int r0 = (int) r10
            r12 = 1
            if (r0 != 0) goto L_0x0016
            r13 = 1
            goto L_0x0017
        L_0x0016:
            r13 = 0
        L_0x0017:
            if (r8 == 0) goto L_0x001f
            boolean r0 = r8.roundVideo
            if (r0 == 0) goto L_0x001f
            r14 = 1
            goto L_0x0020
        L_0x001f:
            r14 = 0
        L_0x0020:
            if (r8 != 0) goto L_0x004f
            java.lang.String r0 = "mp4"
            boolean r0 = r6.endsWith(r0)
            if (r0 != 0) goto L_0x004f
            if (r14 == 0) goto L_0x002d
            goto L_0x004f
        L_0x002d:
            r3 = 0
            r4 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r17 = 0
            r0 = r33
            r1 = r29
            r2 = r29
            r5 = r30
            r7 = r36
            r8 = r37
            r9 = r34
            r10 = r38
            r11 = r35
            r15 = r39
            r16 = r40
            prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
            goto L_0x038c
        L_0x004f:
            java.io.File r15 = new java.io.File
            r15.<init>(r6)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r6)
            long r1 = r15.length()
            r0.append(r1)
            java.lang.String r4 = "_"
            r0.append(r4)
            long r1 = r15.lastModified()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r16 = ""
            r1 = 0
            if (r8 == 0) goto L_0x00c9
            if (r14 != 0) goto L_0x00c2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            long r9 = r8.estimatedDuration
            r3.append(r9)
            r3.append(r4)
            long r9 = r8.startTime
            r3.append(r9)
            r3.append(r4)
            long r9 = r8.endTime
            r3.append(r9)
            boolean r0 = r8.muted
            if (r0 == 0) goto L_0x009f
            java.lang.String r0 = "_m"
            goto L_0x00a1
        L_0x009f:
            r0 = r16
        L_0x00a1:
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            int r3 = r8.resultWidth
            int r5 = r8.originalWidth
            if (r3 == r5) goto L_0x00c2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r4)
            int r0 = r8.resultWidth
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x00c2:
            long r9 = r8.startTime
            int r3 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r3 < 0) goto L_0x00c9
            r1 = r9
        L_0x00c9:
            r9 = r0
            r10 = r1
            r5 = 2
            r3 = 0
            if (r13 != 0) goto L_0x0119
            if (r32 != 0) goto L_0x0119
            if (r8 == 0) goto L_0x00e3
            org.telegram.messenger.MediaController$SavedFilterState r0 = r8.filterState
            if (r0 != 0) goto L_0x0119
            java.lang.String r0 = r8.paintPath
            if (r0 != 0) goto L_0x0119
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r8.mediaEntities
            if (r0 != 0) goto L_0x0119
            org.telegram.messenger.MediaController$CropState r0 = r8.cropState
            if (r0 != 0) goto L_0x0119
        L_0x00e3:
            org.telegram.messenger.MessagesStorage r0 = r33.getMessagesStorage()
            if (r13 != 0) goto L_0x00eb
            r1 = 2
            goto L_0x00ec
        L_0x00eb:
            r1 = 5
        L_0x00ec:
            java.lang.Object[] r0 = r0.getSentFile(r9, r1)
            if (r0 == 0) goto L_0x0119
            r1 = 0
            r2 = r0[r1]
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r2 == 0) goto L_0x0119
            r2 = r0[r1]
            r17 = r2
            org.telegram.tgnet.TLRPC$TL_document r17 = (org.telegram.tgnet.TLRPC$TL_document) r17
            r0 = r0[r12]
            r18 = r0
            java.lang.String r18 = (java.lang.String) r18
            r19 = 0
            r0 = r13
            r1 = r17
            r2 = r29
            r3 = r19
            r21 = r4
            r4 = r10
            ensureMediaThumbExists(r0, r1, r2, r3, r4)
            r3 = r17
            r5 = r18
            goto L_0x011d
        L_0x0119:
            r21 = r4
            r3 = 0
            r5 = 0
        L_0x011d:
            if (r3 != 0) goto L_0x0311
            android.graphics.Bitmap r0 = createVideoThumbnailAtTime(r6, r10)
            if (r0 != 0) goto L_0x0129
            android.graphics.Bitmap r0 = createVideoThumbnail(r6, r12)
        L_0x0129:
            r3 = r0
            r0 = 90
            if (r13 != 0) goto L_0x0134
            if (r32 == 0) goto L_0x0131
            goto L_0x0134
        L_0x0131:
            r1 = 320(0x140, float:4.48E-43)
            goto L_0x0136
        L_0x0134:
            r1 = 90
        L_0x0136:
            float r2 = (float) r1
            if (r1 <= r0) goto L_0x013c
            r1 = 80
            goto L_0x013e
        L_0x013c:
            r1 = 55
        L_0x013e:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r3, r2, r2, r1, r13)
            if (r3 == 0) goto L_0x024a
            if (r1 == 0) goto L_0x024a
            if (r14 == 0) goto L_0x0245
            r2 = 21
            if (r13 == 0) goto L_0x01e4
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createScaledBitmap(r3, r0, r0, r12)
            r23 = 7
            if (r7 >= r2) goto L_0x0157
            r24 = 0
            goto L_0x0159
        L_0x0157:
            r24 = 1
        L_0x0159:
            int r25 = r3.getWidth()
            int r26 = r3.getHeight()
            int r27 = r3.getRowBytes()
            r22 = r3
            org.telegram.messenger.Utilities.blurBitmap(r22, r23, r24, r25, r26, r27)
            r23 = 7
            if (r7 >= r2) goto L_0x0171
            r24 = 0
            goto L_0x0173
        L_0x0171:
            r24 = 1
        L_0x0173:
            int r25 = r3.getWidth()
            int r26 = r3.getHeight()
            int r27 = r3.getRowBytes()
            r22 = r3
            org.telegram.messenger.Utilities.blurBitmap(r22, r23, r24, r25, r26, r27)
            r23 = 7
            if (r7 >= r2) goto L_0x018b
            r24 = 0
            goto L_0x018d
        L_0x018b:
            r24 = 1
        L_0x018d:
            int r25 = r3.getWidth()
            int r26 = r3.getHeight()
            int r27 = r3.getRowBytes()
            r22 = r3
            org.telegram.messenger.Utilities.blurBitmap(r22, r23, r24, r25, r26, r27)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            long r10 = r4.volume_id
            r2.append(r10)
            r4 = r21
            r2.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            int r4 = r4.local_id
            r2.append(r4)
            java.lang.String r4 = "@%d_%d_b2"
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r10 = 2
            java.lang.Object[] r4 = new java.lang.Object[r10]
            int r7 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r7 = (float) r7
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r7 / r10
            int r7 = (int) r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r10 = 0
            r4[r10] = r7
            int r7 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r7 = (float) r7
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r7 / r10
            int r7 = (int) r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r4[r12] = r7
            java.lang.String r2 = java.lang.String.format(r2, r4)
            r17 = r13
            goto L_0x024d
        L_0x01e4:
            r4 = r21
            r10 = 2
            r23 = 3
            if (r7 >= r2) goto L_0x01ee
            r24 = 0
            goto L_0x01f0
        L_0x01ee:
            r24 = 1
        L_0x01f0:
            int r25 = r3.getWidth()
            int r26 = r3.getHeight()
            int r27 = r3.getRowBytes()
            r22 = r3
            org.telegram.messenger.Utilities.blurBitmap(r22, r23, r24, r25, r26, r27)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r7 = r1.location
            r17 = r13
            long r12 = r7.volume_id
            r2.append(r12)
            r2.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            int r4 = r4.local_id
            r2.append(r4)
            java.lang.String r4 = "@%d_%d_b"
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            java.lang.Object[] r4 = new java.lang.Object[r10]
            int r7 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r7 = (float) r7
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r7 / r10
            int r7 = (int) r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r10 = 0
            r4[r10] = r7
            int r7 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r7 = (float) r7
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r7 = r7 / r10
            int r7 = (int) r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r10 = 1
            r4[r10] = r7
            java.lang.String r2 = java.lang.String.format(r2, r4)
            goto L_0x024d
        L_0x0245:
            r17 = r13
            r2 = 0
            r3 = 0
            goto L_0x024d
        L_0x024a:
            r17 = r13
            r2 = 0
        L_0x024d:
            org.telegram.tgnet.TLRPC$TL_document r4 = new org.telegram.tgnet.TLRPC$TL_document
            r4.<init>()
            if (r1 == 0) goto L_0x025f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r4.thumbs
            r7.add(r1)
            int r1 = r4.flags
            r7 = 1
            r1 = r1 | r7
            r4.flags = r1
        L_0x025f:
            r1 = 0
            byte[] r7 = new byte[r1]
            r4.file_reference = r7
            java.lang.String r7 = "video/mp4"
            r4.mime_type = r7
            org.telegram.messenger.UserConfig r7 = r33.getUserConfig()
            r7.saveConfig(r1)
            if (r17 == 0) goto L_0x029c
            r1 = 32
            long r10 = r30 >> r1
            int r1 = (int) r10
            org.telegram.messenger.MessagesController r7 = r33.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r7.getEncryptedChat(r1)
            if (r1 != 0) goto L_0x0286
            return
        L_0x0286:
            int r1 = r1.layer
            int r1 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r1)
            r7 = 66
            if (r1 < r7) goto L_0x0296
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            goto L_0x02a4
        L_0x0296:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65 r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65
            r1.<init>()
            goto L_0x02a4
        L_0x029c:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            r7 = 1
            r1.supports_streaming = r7
        L_0x02a4:
            r1.round_message = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r4.attributes
            r7.add(r1)
            if (r8 == 0) goto L_0x02fd
            boolean r7 = r8.needConvert()
            if (r7 == 0) goto L_0x02fd
            boolean r7 = r8.muted
            if (r7 == 0) goto L_0x02cd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r4.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r10 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r10.<init>()
            r7.add(r10)
            fillVideoAttribute(r6, r1, r8)
            int r7 = r1.w
            r8.originalWidth = r7
            int r7 = r1.h
            r8.originalHeight = r7
            goto L_0x02d5
        L_0x02cd:
            long r10 = r8.estimatedDuration
            r12 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 / r12
            int r7 = (int) r10
            r1.duration = r7
        L_0x02d5:
            int r7 = r8.rotationValue
            org.telegram.messenger.MediaController$CropState r10 = r8.cropState
            if (r10 == 0) goto L_0x02e3
            int r11 = r10.transformWidth
            int r12 = r10.transformHeight
            int r10 = r10.transformRotation
            int r7 = r7 + r10
            goto L_0x02e7
        L_0x02e3:
            int r11 = r8.resultWidth
            int r12 = r8.resultHeight
        L_0x02e7:
            if (r7 == r0) goto L_0x02f3
            r0 = 270(0x10e, float:3.78E-43)
            if (r7 != r0) goto L_0x02ee
            goto L_0x02f3
        L_0x02ee:
            r1.w = r11
            r1.h = r12
            goto L_0x02f7
        L_0x02f3:
            r1.w = r12
            r1.h = r11
        L_0x02f7:
            long r0 = r8.estimatedSize
            int r1 = (int) r0
            r4.size = r1
            goto L_0x030e
        L_0x02fd:
            boolean r0 = r15.exists()
            if (r0 == 0) goto L_0x030a
            long r10 = r15.length()
            int r0 = (int) r10
            r4.size = r0
        L_0x030a:
            r0 = 0
            fillVideoAttribute(r6, r1, r0)
        L_0x030e:
            r1 = r3
            r7 = r4
            goto L_0x0315
        L_0x0311:
            r0 = 0
            r1 = r0
            r2 = r1
            r7 = r3
        L_0x0315:
            if (r8 == 0) goto L_0x034a
            boolean r0 = r8.needConvert()
            if (r0 == 0) goto L_0x034a
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
            r10 = r0
            goto L_0x034b
        L_0x034a:
            r10 = r6
        L_0x034b:
            java.util.HashMap r11 = new java.util.HashMap
            r11.<init>()
            if (r34 == 0) goto L_0x0358
            java.lang.String r0 = r34.toString()
            r14 = r0
            goto L_0x035a
        L_0x0358:
            r14 = r16
        L_0x035a:
            if (r9 == 0) goto L_0x0361
            java.lang.String r0 = "originalPath"
            r11.put(r0, r9)
        L_0x0361:
            if (r5 == 0) goto L_0x0368
            java.lang.String r0 = "parentObject"
            r11.put(r0, r5)
        L_0x0368:
            org.telegram.messenger.-$$Lambda$SendMessagesHelper$8KUnV7gzRpEa8ZqErdYPaX5b3i8 r19 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$8KUnV7gzRpEa8ZqErdYPaX5b3i8
            r0 = r19
            r3 = r35
            r4 = r33
            r20 = r5
            r5 = r8
            r6 = r7
            r7 = r10
            r8 = r11
            r9 = r20
            r10 = r30
            r12 = r36
            r13 = r37
            r15 = r38
            r16 = r39
            r17 = r40
            r18 = r32
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r19)
        L_0x038c:
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
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, messageObject3, str4, arrayList, (TLRPC$ReplyMarkup) null, hashMap, z, i, i2, str3);
        }
    }
}
