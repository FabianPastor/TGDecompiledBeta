package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.Toast;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineMessage;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.DecryptedMessage;
import org.telegram.tgnet.TLRPC.DecryptedMessageAction;
import org.telegram.tgnet.TLRPC.DecryptedMessageMedia;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaContact;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageText;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAbortKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionAcceptKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionCommitKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionFlushHistory;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNoop;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionReadMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionRequestKey;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionResend;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionTyping;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_document_layer82;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_fileLocation_layer82;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFile;
import org.telegram.tgnet.TLRPC.TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth;
import org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendReaction;
import org.telegram.tgnet.TLRPC.TL_messages_sendScreenshotNotification;
import org.telegram.tgnet.TLRPC.TL_messages_sendVote;
import org.telegram.tgnet.TLRPC.TL_messages_uploadMedia;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_restrictionReason;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage;
import org.telegram.tgnet.TLRPC.TL_updateShortSentMessage;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;

public class SendMessagesHelper extends BaseController implements NotificationCenterDelegate {
    private static volatile SendMessagesHelper[] Instance = new SendMessagesHelper[3];
    private static DispatchQueue mediaSendQueue = new DispatchQueue("mediaSendQueue");
    private static ThreadPoolExecutor mediaSendThreadPool;
    private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap();
    private SparseArray<Message> editingMessages = new SparseArray();
    private LocationProvider locationProvider = new LocationProvider(new LocationProviderDelegate() {
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
    private SparseArray<Message> sendingMessages = new SparseArray();
    private LongSparseArray<Integer> sendingMessagesIdDialogs = new LongSparseArray();
    private SparseArray<MessageObject> unsentMessages = new SparseArray();
    private SparseArray<Message> uploadMessages = new SparseArray();
    private LongSparseArray<Integer> uploadingMessagesIdDialogs = new LongSparseArray();
    private LongSparseArray<Long> voteSendTime = new LongSparseArray();
    private HashMap<String, Boolean> waitingForCallback = new HashMap();
    private HashMap<String, MessageObject> waitingForLocation = new HashMap();
    private HashMap<String, byte[]> waitingForVote = new HashMap();

    protected class DelayedMessage {
        public EncryptedChat encryptedChat;
        public HashMap<Object, Object> extraHashMap;
        public int finalGroupMessage;
        public long groupId;
        public String httpLocation;
        public ArrayList<String> httpLocations;
        public ArrayList<InputMedia> inputMedias;
        public InputMedia inputUploadMedia;
        public TLObject locationParent;
        public ArrayList<PhotoSize> locations;
        public ArrayList<MessageObject> messageObjects;
        public ArrayList<Message> messages;
        public MessageObject obj;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public long peer;
        public boolean performMediaUpload;
        public PhotoSize photoSize;
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
            this.messageObjects = new ArrayList();
            this.messages = new ArrayList();
            this.inputMedias = new ArrayList();
            this.originalPaths = new ArrayList();
            this.parentObjects = new ArrayList();
            this.extraHashMap = new HashMap();
            this.locations = new ArrayList();
            this.httpLocations = new ArrayList();
            this.videoEditedInfos = new ArrayList();
        }

        public void addDelayedRequest(TLObject tLObject, MessageObject messageObject, String str, Object obj, DelayedMessage delayedMessage, boolean z) {
            DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = new DelayedMessageSendAfterRequest();
            delayedMessageSendAfterRequest.request = tLObject;
            delayedMessageSendAfterRequest.msgObj = messageObject;
            delayedMessageSendAfterRequest.originalPath = str;
            delayedMessageSendAfterRequest.delayedMessage = delayedMessage;
            delayedMessageSendAfterRequest.parentObject = obj;
            delayedMessageSendAfterRequest.scheduled = z;
            if (this.requests == null) {
                this.requests = new ArrayList();
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
                this.requests = new ArrayList();
            }
            this.requests.add(delayedMessageSendAfterRequest);
        }

        public void sendDelayedRequests() {
            if (this.requests != null) {
                int i = this.type;
                if (i == 4 || i == 0) {
                    i = this.requests.size();
                    for (int i2 = 0; i2 < i; i2++) {
                        DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = (DelayedMessageSendAfterRequest) this.requests.get(i2);
                        TLObject tLObject = delayedMessageSendAfterRequest.request;
                        if (tLObject instanceof TL_messages_sendEncryptedMultiMedia) {
                            SendMessagesHelper.this.getSecretChatHelper().performSendEncryptedRequest((TL_messages_sendEncryptedMultiMedia) delayedMessageSendAfterRequest.request, this);
                        } else if (tLObject instanceof TL_messages_sendMultiMedia) {
                            SendMessagesHelper.this.performSendMessageRequestMulti((TL_messages_sendMultiMedia) tLObject, delayedMessageSendAfterRequest.msgObjs, delayedMessageSendAfterRequest.originalPaths, delayedMessageSendAfterRequest.parentObjects, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.scheduled);
                        } else {
                            SendMessagesHelper.this.performSendMessageRequest(tLObject, delayedMessageSendAfterRequest.msgObj, delayedMessageSendAfterRequest.originalPath, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.parentObject, delayedMessageSendAfterRequest.scheduled);
                        }
                    }
                    this.requests = null;
                }
            }
        }

        public void markAsError() {
            MessageObject messageObject;
            if (this.type == 4) {
                for (int i = 0; i < this.messageObjects.size(); i++) {
                    messageObject = (MessageObject) this.messageObjects.get(i);
                    SendMessagesHelper.this.getMessagesStorage().markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                    messageObject.messageOwner.send_state = 2;
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                    SendMessagesHelper.this.processSentMessage(messageObject.getId());
                    SendMessagesHelper.this.removeFromUploadingMessages(messageObject.getId(), this.scheduled);
                }
                HashMap access$800 = SendMessagesHelper.this.delayedMessages;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("group_");
                stringBuilder.append(this.groupId);
                access$800.remove(stringBuilder.toString());
            } else {
                MessagesStorage messagesStorage = SendMessagesHelper.this.getMessagesStorage();
                messageObject = this.obj;
                messagesStorage.markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                this.obj.messageOwner.send_state = 2;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(this.obj.getId()));
                SendMessagesHelper.this.processSentMessage(this.obj.getId());
                SendMessagesHelper.this.removeFromUploadingMessages(this.obj.getId(), this.scheduled);
            }
            sendDelayedRequests();
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

    public static class LocationProvider {
        private LocationProviderDelegate delegate;
        private GpsLocationListener gpsLocationListener = new GpsLocationListener(this, null);
        private Location lastKnownLocation;
        private LocationManager locationManager;
        private Runnable locationQueryCancelRunnable;
        private GpsLocationListener networkLocationListener = new GpsLocationListener(this, null);

        private class GpsLocationListener implements LocationListener {
            public void onProviderDisabled(String str) {
            }

            public void onProviderEnabled(String str) {
            }

            public void onStatusChanged(String str, int i, Bundle bundle) {
            }

            private GpsLocationListener() {
            }

            /* synthetic */ GpsLocationListener(LocationProvider locationProvider, AnonymousClass1 anonymousClass1) {
                this();
            }

            public void onLocationChanged(Location location) {
                if (location != null && LocationProvider.this.locationQueryCancelRunnable != null) {
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("found location ");
                        stringBuilder.append(location);
                        FileLog.d(stringBuilder.toString());
                    }
                    LocationProvider.this.lastKnownLocation = location;
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

        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
        }

        public LocationProvider(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        public void setDelegate(LocationProviderDelegate locationProviderDelegate) {
            this.delegate = locationProviderDelegate;
        }

        private void cleanup() {
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
                FileLog.e(e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            try {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                if (this.lastKnownLocation == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Exception e22) {
                FileLog.e(e22);
            }
            Runnable runnable = this.locationQueryCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            this.locationQueryCancelRunnable = new Runnable() {
                public void run() {
                    if (LocationProvider.this.locationQueryCancelRunnable == this) {
                        if (LocationProvider.this.delegate != null) {
                            if (LocationProvider.this.lastKnownLocation != null) {
                                LocationProvider.this.delegate.onLocationAcquired(LocationProvider.this.lastKnownLocation);
                            } else {
                                LocationProvider.this.delegate.onUnableLocationAcquire();
                            }
                        }
                        LocationProvider.this.cleanup();
                    }
                }
            };
            AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, 5000);
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

    private static class MediaSendPrepareWorker {
        public volatile String parentObject;
        public volatile TL_photo photo;
        public CountDownLatch sync;

        private MediaSendPrepareWorker() {
        }

        /* synthetic */ MediaSendPrepareWorker(AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public static class SendingMediaInfo {
        public boolean canDeleteAfter;
        public String caption;
        public ArrayList<MessageEntity> entities;
        public BotInlineResult inlineResult;
        public boolean isVideo;
        public ArrayList<InputDocument> masks;
        public HashMap<String, String> params;
        public String path;
        public SearchImage searchImage;
        public int ttl;
        public Uri uri;
        public VideoEditedInfo videoEditedInfo;
    }

    static {
        int availableProcessors = VERSION.SDK_INT >= 17 ? Runtime.getRuntime().availableProcessors() : 2;
        mediaSendThreadPool = new ThreadPoolExecutor(availableProcessors, availableProcessors, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$F3OpGpLNH47E9LDuBXXNWIdgYDE(this));
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
        int i3 = i;
        String str = "_t";
        boolean z = true;
        int i4 = 0;
        boolean z2 = true;
        DelayedMessage delayedMessage;
        String str2;
        int indexOf;
        StringBuilder stringBuilder;
        String str3;
        MessageObject messageObject;
        String str4;
        ArrayList arrayList;
        int indexOf2;
        DelayedMessage delayedMessage2;
        if (i3 == NotificationCenter.FileDidUpload) {
            String str5 = (String) objArr[0];
            InputFile inputFile = (InputFile) objArr[1];
            InputEncryptedFile inputEncryptedFile = (InputEncryptedFile) objArr[2];
            ArrayList arrayList2 = (ArrayList) this.delayedMessages.get(str5);
            if (arrayList2 != null) {
                String str6;
                while (i4 < arrayList2.size()) {
                    ArrayList arrayList3;
                    InputFile inputFile2;
                    InputEncryptedFile inputEncryptedFile2;
                    delayedMessage = (DelayedMessage) arrayList2.get(i4);
                    TLObject tLObject = delayedMessage.sendRequest;
                    InputMedia inputMedia = tLObject instanceof TL_messages_sendMedia ? ((TL_messages_sendMedia) tLObject).media : tLObject instanceof TL_messages_editMessage ? ((TL_messages_editMessage) tLObject).media : tLObject instanceof TL_messages_sendMultiMedia ? (InputMedia) delayedMessage.extraHashMap.get(str5) : null;
                    str2 = "_i";
                    HashMap hashMap;
                    if (inputFile == null || inputMedia == null) {
                        arrayList3 = arrayList2;
                        inputFile2 = inputFile;
                        str6 = str5;
                        inputEncryptedFile2 = inputEncryptedFile;
                        if (inputEncryptedFile2 != null) {
                            TLObject tLObject2 = delayedMessage.sendEncryptedRequest;
                            if (tLObject2 != null) {
                                DecryptedMessage decryptedMessage;
                                if (delayedMessage.type == z) {
                                    TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TL_messages_sendEncryptedMultiMedia) tLObject2;
                                    inputEncryptedFile = (InputEncryptedFile) delayedMessage.extraHashMap.get(str6);
                                    indexOf = tL_messages_sendEncryptedMultiMedia.files.indexOf(inputEncryptedFile);
                                    if (indexOf >= 0) {
                                        tL_messages_sendEncryptedMultiMedia.files.set(indexOf, inputEncryptedFile2);
                                        if (inputEncryptedFile.id == 1) {
                                            HashMap hashMap2 = delayedMessage.extraHashMap;
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append(str6);
                                            stringBuilder2.append(str2);
                                            MessageObject messageObject2 = (MessageObject) hashMap2.get(stringBuilder2.toString());
                                            hashMap = delayedMessage.extraHashMap;
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(str6);
                                            stringBuilder.append(str);
                                            delayedMessage.photoSize = (PhotoSize) hashMap.get(stringBuilder.toString());
                                            stopVideoService(((MessageObject) delayedMessage.messageObjects.get(indexOf)).messageOwner.attachPath);
                                        }
                                        decryptedMessage = (TL_decryptedMessage) tL_messages_sendEncryptedMultiMedia.messages.get(indexOf);
                                    } else {
                                        decryptedMessage = null;
                                    }
                                } else {
                                    decryptedMessage = (TL_decryptedMessage) tLObject2;
                                }
                                if (decryptedMessage != null) {
                                    DecryptedMessageMedia decryptedMessageMedia = decryptedMessage.media;
                                    if ((decryptedMessageMedia instanceof TL_decryptedMessageMediaVideo) || (decryptedMessageMedia instanceof TL_decryptedMessageMediaPhoto) || (decryptedMessageMedia instanceof TL_decryptedMessageMediaDocument)) {
                                        decryptedMessage.media.size = (int) ((Long) objArr[5]).longValue();
                                    }
                                    decryptedMessageMedia = decryptedMessage.media;
                                    decryptedMessageMedia.key = (byte[]) objArr[3];
                                    decryptedMessageMedia.iv = (byte[]) objArr[4];
                                    if (delayedMessage.type == 4) {
                                        uploadMultiMedia(delayedMessage, null, inputEncryptedFile2, str6);
                                    } else {
                                        SecretChatHelper secretChatHelper = getSecretChatHelper();
                                        MessageObject messageObject3 = delayedMessage.obj;
                                        secretChatHelper.performSendEncryptedRequest(decryptedMessage, messageObject3.messageOwner, delayedMessage.encryptedChat, inputEncryptedFile2, delayedMessage.originalPath, messageObject3);
                                    }
                                }
                                arrayList3.remove(i4);
                                i4--;
                            }
                        }
                    } else {
                        ArrayList arrayList4;
                        InputEncryptedFile inputEncryptedFile3;
                        int i5;
                        boolean z3 = delayedMessage.type;
                        if (z3) {
                            DelayedMessage delayedMessage3 = delayedMessage;
                            arrayList4 = arrayList2;
                            inputEncryptedFile3 = inputEncryptedFile;
                            i5 = i4;
                            inputFile2 = inputFile;
                            str6 = str5;
                            PhotoSize photoSize;
                            if (z3 != z2) {
                                delayedMessage = delayedMessage3;
                                if (z3) {
                                    if (inputMedia.file == null) {
                                        inputMedia.file = inputFile2;
                                        if (inputMedia.thumb == null) {
                                            photoSize = delayedMessage.photoSize;
                                            if (!(photoSize == null || photoSize.location == null)) {
                                                performSendDelayedMessage(delayedMessage);
                                            }
                                        }
                                        performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, delayedMessage.scheduled);
                                    } else {
                                        inputMedia.thumb = inputFile2;
                                        inputMedia.flags |= z;
                                        performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, delayedMessage.scheduled);
                                    }
                                } else if (z3) {
                                    inputMedia.file = inputFile2;
                                    performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, delayedMessage.scheduled);
                                } else if (z3 == z) {
                                    if (!(inputMedia instanceof TL_inputMediaUploadedDocument)) {
                                        inputMedia.file = inputFile2;
                                        uploadMultiMedia(delayedMessage, inputMedia, null, str6);
                                    } else if (inputMedia.file == null) {
                                        inputMedia.file = inputFile2;
                                        HashMap hashMap3 = delayedMessage.extraHashMap;
                                        StringBuilder stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(str6);
                                        stringBuilder3.append(str2);
                                        int indexOf3 = delayedMessage.messageObjects.indexOf((MessageObject) hashMap3.get(stringBuilder3.toString()));
                                        if (indexOf3 >= 0) {
                                            stopVideoService(((MessageObject) delayedMessage.messageObjects.get(indexOf3)).messageOwner.attachPath);
                                        }
                                        hashMap3 = delayedMessage.extraHashMap;
                                        stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(str6);
                                        stringBuilder3.append(str);
                                        delayedMessage.photoSize = (PhotoSize) hashMap3.get(stringBuilder3.toString());
                                        if (inputMedia.thumb == null) {
                                            PhotoSize photoSize2 = delayedMessage.photoSize;
                                            if (!(photoSize2 == null || photoSize2.location == null)) {
                                                delayedMessage.performMediaUpload = z2;
                                                performSendDelayedMessage(delayedMessage, indexOf3);
                                            }
                                        }
                                        uploadMultiMedia(delayedMessage, inputMedia, null, str6);
                                    } else {
                                        inputMedia.thumb = inputFile2;
                                        inputMedia.flags |= z;
                                        hashMap = delayedMessage.extraHashMap;
                                        StringBuilder stringBuilder4 = new StringBuilder();
                                        stringBuilder4.append(str6);
                                        stringBuilder4.append("_o");
                                        uploadMultiMedia(delayedMessage, inputMedia, null, (String) hashMap.get(stringBuilder4.toString()));
                                    }
                                }
                            } else if (inputMedia.file == null) {
                                inputMedia.file = inputFile2;
                                delayedMessage = delayedMessage3;
                                if (inputMedia.thumb == null) {
                                    photoSize = delayedMessage.photoSize;
                                    if (!(photoSize == null || photoSize.location == null)) {
                                        performSendDelayedMessage(delayedMessage);
                                    }
                                }
                                performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, delayedMessage.scheduled);
                            } else {
                                delayedMessage = delayedMessage3;
                                inputMedia.thumb = inputFile2;
                                inputMedia.flags |= z;
                                performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject, delayedMessage.scheduled);
                            }
                        } else {
                            inputMedia.file = inputFile;
                            arrayList4 = arrayList2;
                            inputEncryptedFile3 = inputEncryptedFile;
                            i5 = i4;
                            inputFile2 = inputFile;
                            str6 = str5;
                            performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, delayedMessage, true, null, delayedMessage.parentObject, delayedMessage.scheduled);
                        }
                        arrayList3 = arrayList4;
                        i4 = i5;
                        arrayList3.remove(i4);
                        i4--;
                        inputEncryptedFile2 = inputEncryptedFile3;
                    }
                    i4++;
                    arrayList2 = arrayList3;
                    inputEncryptedFile = inputEncryptedFile2;
                    inputFile = inputFile2;
                    str5 = str6;
                    z = true;
                    z2 = true;
                }
                str6 = str5;
                if (arrayList2.isEmpty()) {
                    this.delayedMessages.remove(str6);
                }
            }
        } else if (i3 == NotificationCenter.FileDidFailUpload) {
            str3 = (String) objArr[0];
            boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
            ArrayList arrayList5 = (ArrayList) this.delayedMessages.get(str3);
            if (arrayList5 != null) {
                while (i4 < arrayList5.size()) {
                    delayedMessage = (DelayedMessage) arrayList5.get(i4);
                    if ((booleanValue && delayedMessage.sendEncryptedRequest != null) || !(booleanValue || delayedMessage.sendRequest == null)) {
                        delayedMessage.markAsError();
                        arrayList5.remove(i4);
                        i4--;
                    }
                    i4++;
                }
                if (arrayList5.isEmpty()) {
                    this.delayedMessages.remove(str3);
                }
            }
        } else if (i3 == NotificationCenter.filePreparingStarted) {
            messageObject = (MessageObject) objArr[0];
            if (messageObject.getId() != 0) {
                str4 = (String) objArr[1];
                arrayList = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                if (arrayList != null) {
                    while (i4 < arrayList.size()) {
                        DelayedMessage delayedMessage4 = (DelayedMessage) arrayList.get(i4);
                        if (delayedMessage4.type == 4) {
                            indexOf2 = delayedMessage4.messageObjects.indexOf(messageObject);
                            HashMap hashMap4 = delayedMessage4.extraHashMap;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(messageObject.messageOwner.attachPath);
                            stringBuilder.append(str);
                            delayedMessage4.photoSize = (PhotoSize) hashMap4.get(stringBuilder.toString());
                            delayedMessage4.performMediaUpload = true;
                            performSendDelayedMessage(delayedMessage4, indexOf2);
                            arrayList.remove(i4);
                            break;
                        } else if (delayedMessage4.obj == messageObject) {
                            delayedMessage4.videoEditedInfo = null;
                            performSendDelayedMessage(delayedMessage4);
                            arrayList.remove(i4);
                            break;
                        } else {
                            i4++;
                        }
                    }
                    if (arrayList.isEmpty()) {
                        this.delayedMessages.remove(messageObject.messageOwner.attachPath);
                    }
                }
            }
        } else if (i3 == NotificationCenter.fileNewChunkAvailable) {
            messageObject = (MessageObject) objArr[0];
            if (messageObject.getId() != 0) {
                str2 = (String) objArr[1];
                long longValue = ((Long) objArr[2]).longValue();
                long longValue2 = ((Long) objArr[3]).longValue();
                getFileLoader().checkUploadNewDataAvailable(str2, ((int) messageObject.getDialogId()) == 0, longValue, longValue2);
                if (longValue2 != 0) {
                    stopVideoService(messageObject.messageOwner.attachPath);
                    arrayList = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                    if (arrayList != null) {
                        for (int i6 = 0; i6 < arrayList.size(); i6++) {
                            DelayedMessage delayedMessage5 = (DelayedMessage) arrayList.get(i6);
                            str = "ve";
                            if (delayedMessage5.type == 4) {
                                for (indexOf = 0; indexOf < delayedMessage5.messageObjects.size(); indexOf++) {
                                    MessageObject messageObject4 = (MessageObject) delayedMessage5.messageObjects.get(indexOf);
                                    if (messageObject4 == messageObject) {
                                        messageObject4.videoEditedInfo = null;
                                        messageObject4.messageOwner.params.remove(str);
                                        messageObject4.messageOwner.media.document.size = (int) longValue2;
                                        ArrayList arrayList6 = new ArrayList();
                                        arrayList6.add(messageObject4.messageOwner);
                                        getMessagesStorage().putMessages(arrayList6, false, true, false, 0, messageObject4.scheduled);
                                        break;
                                    }
                                }
                            } else {
                                MessageObject messageObject5 = delayedMessage5.obj;
                                if (messageObject5 == messageObject) {
                                    messageObject5.videoEditedInfo = null;
                                    messageObject5.messageOwner.params.remove(str);
                                    delayedMessage5.obj.messageOwner.media.document.size = (int) longValue2;
                                    ArrayList arrayList7 = new ArrayList();
                                    arrayList7.add(delayedMessage5.obj.messageOwner);
                                    getMessagesStorage().putMessages(arrayList7, false, true, false, 0, delayedMessage5.obj.scheduled);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else if (i3 == NotificationCenter.filePreparingFailed) {
            messageObject = (MessageObject) objArr[0];
            if (messageObject.getId() != 0) {
                str4 = (String) objArr[1];
                stopVideoService(messageObject.messageOwner.attachPath);
                arrayList = (ArrayList) this.delayedMessages.get(str4);
                if (arrayList != null) {
                    indexOf2 = 0;
                    while (indexOf2 < arrayList.size()) {
                        delayedMessage2 = (DelayedMessage) arrayList.get(indexOf2);
                        if (delayedMessage2.type == 4) {
                            int i7 = 0;
                            while (i7 < delayedMessage2.messages.size()) {
                                if (delayedMessage2.messageObjects.get(i7) == messageObject) {
                                    delayedMessage2.markAsError();
                                    arrayList.remove(indexOf2);
                                } else {
                                    i7++;
                                }
                            }
                            indexOf2++;
                        } else if (delayedMessage2.obj == messageObject) {
                            delayedMessage2.markAsError();
                            arrayList.remove(indexOf2);
                        } else {
                            indexOf2++;
                        }
                        indexOf2--;
                        indexOf2++;
                    }
                    if (arrayList.isEmpty()) {
                        this.delayedMessages.remove(str4);
                    }
                }
            }
        } else if (i3 == NotificationCenter.httpFileDidLoad) {
            String str7 = (String) objArr[0];
            ArrayList arrayList8 = (ArrayList) this.delayedMessages.get(str7);
            if (arrayList8 != null) {
                for (int i8 = 0; i8 < arrayList8.size(); i8++) {
                    MessageObject messageObject6;
                    delayedMessage2 = (DelayedMessage) arrayList8.get(i8);
                    i3 = delayedMessage2.type;
                    if (i3 == 0) {
                        messageObject6 = delayedMessage2.obj;
                        i3 = 0;
                    } else {
                        if (i3 == 2) {
                            messageObject = delayedMessage2.obj;
                        } else if (i3 == 4) {
                            messageObject = (MessageObject) delayedMessage2.extraHashMap.get(str7);
                            if (messageObject.getDocument() == null) {
                                messageObject6 = messageObject;
                                i3 = 0;
                            }
                        } else {
                            i3 = -1;
                            messageObject6 = null;
                        }
                        messageObject6 = messageObject;
                        i3 = 1;
                    }
                    StringBuilder stringBuilder5;
                    if (i3 == 0) {
                        stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(Utilities.MD5(str7));
                        stringBuilder5.append(".");
                        stringBuilder5.append(ImageLoader.getHttpUrlExtension(str7, "file"));
                        File file = new File(FileLoader.getDirectory(4), stringBuilder5.toString());
                        DispatchQueue dispatchQueue = Utilities.globalQueue;
                        -$$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv-97eY -__lambda_sendmessageshelper_1kox99gmebip9sys_e7uqv-97ey = r0;
                        -$$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv-97eY -__lambda_sendmessageshelper_1kox99gmebip9sys_e7uqv-97ey2 = new -$$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv-97eY(this, file, messageObject6, delayedMessage2, str7);
                        dispatchQueue.postRunnable(-__lambda_sendmessageshelper_1kox99gmebip9sys_e7uqv-97ey);
                    } else if (i3 == 1) {
                        stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(Utilities.MD5(str7));
                        stringBuilder5.append(".gif");
                        Utilities.globalQueue.postRunnable(new -$$Lambda$SendMessagesHelper$pp0U4GJ1r75dDYF4YGnbf9kI6EU(this, delayedMessage2, new File(FileLoader.getDirectory(4), stringBuilder5.toString()), messageObject6));
                    }
                }
                this.delayedMessages.remove(str7);
            }
        } else if (i3 == NotificationCenter.fileDidLoad) {
            str3 = (String) objArr[0];
            arrayList = (ArrayList) this.delayedMessages.get(str3);
            if (arrayList != null) {
                while (i4 < arrayList.size()) {
                    performSendDelayedMessage((DelayedMessage) arrayList.get(i4));
                    i4++;
                }
                this.delayedMessages.remove(str3);
            }
        } else if (i3 == NotificationCenter.httpFileDidFailedLoad || i3 == NotificationCenter.fileDidFailToLoad) {
            str3 = (String) objArr[0];
            arrayList = (ArrayList) this.delayedMessages.get(str3);
            if (arrayList != null) {
                while (i4 < arrayList.size()) {
                    ((DelayedMessage) arrayList.get(i4)).markAsError();
                    i4++;
                }
                this.delayedMessages.remove(str3);
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$2$SendMessagesHelper(File file, MessageObject messageObject, DelayedMessage delayedMessage, String str) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$a2su0LhJhPbUXm5fG2WcDQ_Npn4(this, generatePhotoSizes(file.toString(), null), messageObject, file, delayedMessage, str));
    }

    public /* synthetic */ void lambda$null$1$SendMessagesHelper(TL_photo tL_photo, MessageObject messageObject, File file, DelayedMessage delayedMessage, String str) {
        if (tL_photo != null) {
            Message message = messageObject.messageOwner;
            message.media.photo = tL_photo;
            message.attachPath = file.toString();
            ArrayList arrayList = new ArrayList();
            arrayList.add(messageObject.messageOwner);
            getMessagesStorage().putMessages(arrayList, false, true, false, 0, messageObject.scheduled);
            getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, messageObject.messageOwner);
            ArrayList arrayList2 = tL_photo.sizes;
            delayedMessage.photoSize = (PhotoSize) arrayList2.get(arrayList2.size() - 1);
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("can't load image ");
            stringBuilder.append(str);
            stringBuilder.append(" to file ");
            stringBuilder.append(file.toString());
            FileLog.e(stringBuilder.toString());
        }
        delayedMessage.markAsError();
    }

    public /* synthetic */ void lambda$didReceivedNotification$4$SendMessagesHelper(DelayedMessage delayedMessage, File file, MessageObject messageObject) {
        Document document = delayedMessage.obj.getDocument();
        boolean z = false;
        if (document.thumbs.isEmpty() || (((PhotoSize) document.thumbs.get(0)).location instanceof TL_fileLocationUnavailable)) {
            try {
                Bitmap loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
                if (loadBitmap != null) {
                    document.thumbs.clear();
                    ArrayList arrayList = document.thumbs;
                    if (delayedMessage.sendEncryptedRequest != null) {
                        z = true;
                    }
                    arrayList.add(ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, z));
                    loadBitmap.recycle();
                }
            } catch (Exception e) {
                document.thumbs.clear();
                FileLog.e(e);
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$ivsY9c3O0var_RgXSqAIraHVU0Fk(this, delayedMessage, file, document, messageObject));
    }

    public /* synthetic */ void lambda$null$3$SendMessagesHelper(DelayedMessage delayedMessage, File file, Document document, MessageObject messageObject) {
        delayedMessage.httpLocation = null;
        delayedMessage.obj.messageOwner.attachPath = file.toString();
        if (!document.thumbs.isEmpty()) {
            delayedMessage.photoSize = (PhotoSize) document.thumbs.get(0);
            delayedMessage.locationParent = document;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject.messageOwner);
        getMessagesStorage().putMessages(arrayList, false, true, false, 0, messageObject.scheduled);
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
        getNotificationCenter().postNotificationName(NotificationCenter.updateMessageMedia, delayedMessage.obj.messageOwner);
    }

    private void revertEditingMessageObject(MessageObject messageObject) {
        messageObject.cancelEditing = true;
        Message message = messageObject.messageOwner;
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
        getMessagesStorage().putMessages(arrayList, false, true, false, 0, messageObject.scheduled);
        new ArrayList().add(messageObject);
        getNotificationCenter().postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), r1);
    }

    public void cancelSendingMessage(MessageObject messageObject) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject);
        cancelSendingMessage(arrayList);
    }

    public void cancelSendingMessage(ArrayList<MessageObject> arrayList) {
        int i;
        ArrayList<MessageObject> arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        long j = 0;
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        boolean z2 = false;
        while (i2 < arrayList.size()) {
            boolean z3;
            long j2;
            MessageObject messageObject = (MessageObject) arrayList2.get(i2);
            if (messageObject.scheduled) {
                j = messageObject.getDialogId();
                z2 = true;
            }
            arrayList5.add(Integer.valueOf(messageObject.getId()));
            int i4 = messageObject.messageOwner.to_id.channel_id;
            Message removeFromSendingMessages = removeFromSendingMessages(messageObject.getId(), messageObject.scheduled);
            if (removeFromSendingMessages != null) {
                getConnectionsManager().cancelRequest(removeFromSendingMessages.reqId, true);
            }
            for (Entry entry : this.delayedMessages.entrySet()) {
                ArrayList arrayList6 = (ArrayList) entry.getValue();
                i = 0;
                while (i < arrayList6.size()) {
                    DelayedMessage delayedMessage = (DelayedMessage) arrayList6.get(i);
                    z3 = z;
                    j2 = j;
                    if (delayedMessage.type == 4) {
                        i = -1;
                        Object obj = null;
                        for (int i5 = 0; i5 < delayedMessage.messageObjects.size(); i5++) {
                            obj = (MessageObject) delayedMessage.messageObjects.get(i5);
                            if (obj.getId() == messageObject.getId()) {
                                removeFromUploadingMessages(messageObject.getId(), messageObject.scheduled);
                                i = i5;
                                break;
                            }
                        }
                        if (i >= 0) {
                            delayedMessage.messageObjects.remove(i);
                            delayedMessage.messages.remove(i);
                            delayedMessage.originalPaths.remove(i);
                            TLObject tLObject = delayedMessage.sendRequest;
                            if (tLObject != null) {
                                ((TL_messages_sendMultiMedia) tLObject).multi_media.remove(i);
                            } else {
                                TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
                                tL_messages_sendEncryptedMultiMedia.messages.remove(i);
                                tL_messages_sendEncryptedMultiMedia.files.remove(i);
                            }
                            MediaController.getInstance().cancelVideoConvert(messageObject);
                            String str = (String) delayedMessage.extraHashMap.get(obj);
                            if (str != null) {
                                arrayList3.add(str);
                            }
                            if (delayedMessage.messageObjects.isEmpty()) {
                                delayedMessage.sendDelayedRequests();
                            } else {
                                if (delayedMessage.finalGroupMessage == messageObject.getId()) {
                                    ArrayList arrayList7 = delayedMessage.messageObjects;
                                    MessageObject messageObject2 = (MessageObject) arrayList7.get(arrayList7.size() - 1);
                                    delayedMessage.finalGroupMessage = messageObject2.getId();
                                    messageObject2.messageOwner.params.put("final", "1");
                                    messages_Messages tL_messages_messages = new TL_messages_messages();
                                    tL_messages_messages.messages.add(messageObject2.messageOwner);
                                    getMessagesStorage().putMessages(tL_messages_messages, delayedMessage.peer, -2, 0, false, z2);
                                }
                                if (!arrayList4.contains(delayedMessage)) {
                                    arrayList4.add(delayedMessage);
                                }
                            }
                        }
                    } else if (delayedMessage.obj.getId() == messageObject.getId()) {
                        removeFromUploadingMessages(messageObject.getId(), messageObject.scheduled);
                        arrayList6.remove(i);
                        delayedMessage.sendDelayedRequests();
                        MediaController.getInstance().cancelVideoConvert(delayedMessage.obj);
                        if (arrayList6.size() == 0) {
                            arrayList3.add(entry.getKey());
                            if (delayedMessage.sendEncryptedRequest != null) {
                                z = true;
                                j = j2;
                            }
                        }
                    } else {
                        i++;
                        z = z3;
                        j = j2;
                    }
                    z = z3;
                    j = j2;
                }
                z3 = z;
                j2 = j;
                z = z3;
                j = j2;
            }
            z3 = z;
            j2 = j;
            i2++;
            i3 = i4;
        }
        for (i = 0; i < arrayList3.size(); i++) {
            String str2 = (String) arrayList3.get(i);
            if (str2.startsWith("http")) {
                ImageLoader.getInstance().cancelLoadHttpFile(str2);
            } else {
                getFileLoader().cancelUploadFile(str2, z);
            }
            stopVideoService(str2);
            this.delayedMessages.remove(str2);
        }
        int size = arrayList4.size();
        for (i = 0; i < size; i++) {
            sendReadyToSendGroup((DelayedMessage) arrayList4.get(i), false, true);
        }
        if (arrayList.size() == 1 && ((MessageObject) arrayList2.get(0)).isEditing() && ((MessageObject) arrayList2.get(0)).previousMedia != null) {
            revertEditingMessageObject((MessageObject) arrayList2.get(0));
        } else {
            getMessagesController().deleteMessages(arrayList5, null, null, j, i3, false, z2);
        }
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean z) {
        if (messageObject.getId() >= 0) {
            if (messageObject.isEditing()) {
                editMessageMedia(messageObject, null, null, null, null, null, true, messageObject);
            }
            return false;
        }
        MessageAction messageAction = messageObject.messageOwner.action;
        if (messageAction instanceof TL_messageEncryptedAction) {
            EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                getMessagesStorage().markMessageAsSendError(messageObject.messageOwner, messageObject.scheduled);
                messageObject.messageOwner.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            Message message = messageObject.messageOwner;
            if (message.random_id == 0) {
                message.random_id = getNextRandomId();
            }
            DecryptedMessageAction decryptedMessageAction = messageObject.messageOwner.action.encryptedAction;
            if (decryptedMessageAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                getSecretChatHelper().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionDeleteMessages) {
                getSecretChatHelper().sendMessagesDeleteMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionFlushHistory) {
                getSecretChatHelper().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionNotifyLayer) {
                getSecretChatHelper().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionReadMessages) {
                getSecretChatHelper().sendMessagesReadMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                getSecretChatHelper().sendScreenshotMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (!(decryptedMessageAction instanceof TL_decryptedMessageActionTyping)) {
                if (decryptedMessageAction instanceof TL_decryptedMessageActionResend) {
                    getSecretChatHelper().sendResendMessage(encryptedChat, 0, 0, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionCommitKey) {
                    getSecretChatHelper().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionAbortKey) {
                    getSecretChatHelper().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionRequestKey) {
                    getSecretChatHelper().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionAcceptKey) {
                    getSecretChatHelper().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionNoop) {
                    getSecretChatHelper().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        }
        if (messageAction instanceof TL_messageActionScreenshotTaken) {
            User user = getMessagesController().getUser(Integer.valueOf((int) messageObject.getDialogId()));
            Message message2 = messageObject.messageOwner;
            sendScreenshotMessage(user, message2.reply_to_msg_id, message2);
        }
        if (z) {
            this.unsentMessages.put(messageObject.getId(), messageObject);
        }
        sendMessage(messageObject);
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void processSentMessage(int i) {
        int size = this.unsentMessages.size();
        this.unsentMessages.remove(i);
        if (size != 0 && this.unsentMessages.size() == 0) {
            checkUnsentMessages();
        }
    }

    public void processForwardFromMyName(MessageObject messageObject, long j) {
        MessageObject messageObject2 = messageObject;
        long j2 = j;
        if (messageObject2 != null) {
            Message message = messageObject2.messageOwner;
            MessageMedia messageMedia = message.media;
            MessageMedia messageMedia2;
            ArrayList arrayList;
            if (messageMedia == null || (messageMedia instanceof TL_messageMediaEmpty) || (messageMedia instanceof TL_messageMediaWebPage) || (messageMedia instanceof TL_messageMediaGame) || (messageMedia instanceof TL_messageMediaInvoice)) {
                message = messageObject2.messageOwner;
                if (message.message != null) {
                    ArrayList arrayList2;
                    messageMedia2 = message.media;
                    WebPage webPage = messageMedia2 instanceof TL_messageMediaWebPage ? messageMedia2.webpage : null;
                    ArrayList arrayList3 = messageObject2.messageOwner.entities;
                    if (arrayList3 == null || arrayList3.isEmpty()) {
                        arrayList2 = null;
                    } else {
                        arrayList3 = new ArrayList();
                        for (int i = 0; i < messageObject2.messageOwner.entities.size(); i++) {
                            MessageEntity messageEntity = (MessageEntity) messageObject2.messageOwner.entities.get(i);
                            if ((messageEntity instanceof TL_messageEntityBold) || (messageEntity instanceof TL_messageEntityItalic) || (messageEntity instanceof TL_messageEntityPre) || (messageEntity instanceof TL_messageEntityCode) || (messageEntity instanceof TL_messageEntityTextUrl)) {
                                arrayList3.add(messageEntity);
                            }
                        }
                        arrayList2 = arrayList3;
                    }
                    sendMessage(messageObject2.messageOwner.message, j, messageObject2.replyMessageObject, webPage, true, arrayList2, null, null, true, 0);
                } else if (((int) j2) != 0) {
                    arrayList = new ArrayList();
                    arrayList.add(messageObject2);
                    sendMessage(arrayList, j, true, 0);
                }
            } else {
                HashMap hashMap;
                int i2 = (int) j2;
                if (i2 == 0 && message.to_id != null && ((messageMedia.photo instanceof TL_photo) || (messageMedia.document instanceof TL_document))) {
                    HashMap hashMap2 = new HashMap();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("sent_");
                    stringBuilder.append(messageObject2.messageOwner.to_id.channel_id);
                    stringBuilder.append("_");
                    stringBuilder.append(messageObject.getId());
                    hashMap2.put("parentObject", stringBuilder.toString());
                    hashMap = hashMap2;
                } else {
                    hashMap = null;
                }
                message = messageObject2.messageOwner;
                messageMedia = message.media;
                Photo photo = messageMedia.photo;
                if (photo instanceof TL_photo) {
                    sendMessage((TL_photo) photo, null, j, messageObject2.replyMessageObject, message.message, message.entities, null, hashMap, true, 0, messageMedia.ttl_seconds, messageObject);
                } else {
                    Document document = messageMedia.document;
                    if (document instanceof TL_document) {
                        sendMessage((TL_document) document, null, message.attachPath, j, messageObject2.replyMessageObject, message.message, message.entities, null, hashMap, true, 0, messageMedia.ttl_seconds, messageObject);
                    } else if ((messageMedia instanceof TL_messageMediaVenue) || (messageMedia instanceof TL_messageMediaGeo)) {
                        sendMessage(messageObject2.messageOwner.media, j, messageObject2.replyMessageObject, null, null, true, 0);
                    } else if (messageMedia.phone_number != null) {
                        User tL_userContact_old2 = new TL_userContact_old2();
                        messageMedia2 = messageObject2.messageOwner.media;
                        tL_userContact_old2.phone = messageMedia2.phone_number;
                        tL_userContact_old2.first_name = messageMedia2.first_name;
                        tL_userContact_old2.last_name = messageMedia2.last_name;
                        tL_userContact_old2.id = messageMedia2.user_id;
                        sendMessage(tL_userContact_old2, j, messageObject2.replyMessageObject, null, null, true, 0);
                    } else if (i2 != 0) {
                        arrayList = new ArrayList();
                        arrayList.add(messageObject2);
                        sendMessage(arrayList, j, true, 0);
                    }
                }
            }
        }
    }

    public void sendScreenshotMessage(User user, int i, Message message) {
        if (user != null && i != 0 && user.id != getUserConfig().getClientUserId()) {
            TL_messages_sendScreenshotNotification tL_messages_sendScreenshotNotification = new TL_messages_sendScreenshotNotification();
            tL_messages_sendScreenshotNotification.peer = new TL_inputPeerUser();
            InputPeer inputPeer = tL_messages_sendScreenshotNotification.peer;
            inputPeer.access_hash = user.access_hash;
            inputPeer.user_id = user.id;
            if (message != null) {
                tL_messages_sendScreenshotNotification.reply_to_msg_id = i;
                tL_messages_sendScreenshotNotification.random_id = message.random_id;
            } else {
                message = new TL_messageService();
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
                message.to_id = new TL_peerUser();
                message.to_id.user_id = user.id;
                message.date = getConnectionsManager().getCurrentTime();
                message.action = new TL_messageActionScreenshotTaken();
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
            getMessagesStorage().putMessages(arrayList2, false, true, false, 0, false);
            performSendMessageRequest(tL_messages_sendScreenshotNotification, messageObject, null, null, null, false);
        }
    }

    public void sendSticker(Document document, long j, MessageObject messageObject, Object obj, boolean z, int i) {
        Document document2 = document;
        long j2 = j;
        if (document2 != null) {
            if (((int) j2) == 0) {
                if (getMessagesController().getEncryptedChat(Integer.valueOf((int) (j2 >> 32))) != null) {
                    Document tL_document_layer82 = new TL_document_layer82();
                    tL_document_layer82.id = document2.id;
                    tL_document_layer82.access_hash = document2.access_hash;
                    tL_document_layer82.date = document2.date;
                    tL_document_layer82.mime_type = document2.mime_type;
                    tL_document_layer82.file_reference = document2.file_reference;
                    if (tL_document_layer82.file_reference == null) {
                        tL_document_layer82.file_reference = new byte[0];
                    }
                    tL_document_layer82.size = document2.size;
                    tL_document_layer82.dc_id = document2.dc_id;
                    tL_document_layer82.attributes = new ArrayList(document2.attributes);
                    if (tL_document_layer82.mime_type == null) {
                        tL_document_layer82.mime_type = "";
                    }
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90);
                    if (closestPhotoSizeWithSize instanceof TL_photoSize) {
                        File pathToAttach = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                        if (pathToAttach.exists()) {
                            try {
                                pathToAttach.length();
                                byte[] bArr = new byte[((int) pathToAttach.length())];
                                new RandomAccessFile(pathToAttach, "r").readFully(bArr);
                                TL_photoCachedSize tL_photoCachedSize = new TL_photoCachedSize();
                                TL_fileLocation_layer82 tL_fileLocation_layer82 = new TL_fileLocation_layer82();
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
                                tL_document_layer82.thumbs.add(tL_photoCachedSize);
                                tL_document_layer82.flags |= 1;
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                    }
                    if (tL_document_layer82.thumbs.isEmpty()) {
                        TL_photoSizeEmpty tL_photoSizeEmpty = new TL_photoSizeEmpty();
                        tL_photoSizeEmpty.type = "s";
                        tL_document_layer82.thumbs.add(tL_photoSizeEmpty);
                    }
                    document2 = tL_document_layer82;
                } else {
                    return;
                }
            }
            if (document2 instanceof TL_document) {
                sendMessage((TL_document) document2, null, null, j, messageObject, null, null, null, null, z, i, 0, obj);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:238:0x05c0  */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x05fa  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x05d0  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01be  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x018e  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0264  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x0298  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02a0  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02ad  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02d1  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x036f  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0379  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0396  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x040f  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x042e  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x0499  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x04b0  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x04ae  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x04c5  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x04c3  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x04cd  */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r46, long r47, boolean r49, int r50) {
        /*
        r45 = this;
        r12 = r45;
        r13 = r46;
        r14 = r47;
        r11 = r50;
        r10 = 0;
        if (r13 == 0) goto L_0x0707;
    L_0x000b:
        r0 = r46.isEmpty();
        if (r0 == 0) goto L_0x0013;
    L_0x0011:
        goto L_0x0707;
    L_0x0013:
        r0 = (int) r14;
        if (r0 == 0) goto L_0x06e7;
    L_0x0016:
        r1 = r45.getMessagesController();
        r9 = r1.getPeer(r0);
        r16 = 0;
        if (r0 <= 0) goto L_0x0040;
    L_0x0022:
        r1 = r45.getMessagesController();
        r2 = java.lang.Integer.valueOf(r0);
        r1 = r1.getUser(r2);
        if (r1 != 0) goto L_0x0031;
    L_0x0030:
        return r10;
    L_0x0031:
        r7 = r16;
        r17 = 1;
        r18 = 1;
        r19 = 1;
        r20 = 0;
        r21 = 1;
        r22 = 0;
        goto L_0x0077;
    L_0x0040:
        r1 = r45.getMessagesController();
        r2 = -r0;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getChat(r2);
        r2 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r2 == 0) goto L_0x0058;
    L_0x0053:
        r2 = r1.megagroup;
        r3 = r1.signatures;
        goto L_0x005a;
    L_0x0058:
        r2 = 0;
        r3 = 0;
    L_0x005a:
        r4 = org.telegram.messenger.ChatObject.canSendStickers(r1);
        r5 = org.telegram.messenger.ChatObject.canSendMedia(r1);
        r6 = org.telegram.messenger.ChatObject.canSendEmbed(r1);
        r7 = org.telegram.messenger.ChatObject.canSendPolls(r1);
        r20 = r2;
        r22 = r3;
        r17 = r4;
        r18 = r5;
        r21 = r6;
        r19 = r7;
        r7 = r1;
    L_0x0077:
        r6 = new android.util.LongSparseArray;
        r6.<init>();
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r5 = new android.util.LongSparseArray;
        r5.<init>();
        r10 = r45.getMessagesController();
        r10 = r10.getInputPeer(r0);
        r0 = r45.getUserConfig();
        r0 = r0.getClientUserId();
        r24 = r9;
        r8 = (long) r0;
        r0 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1));
        if (r0 != 0) goto L_0x00af;
    L_0x00ac:
        r26 = 1;
        goto L_0x00b1;
    L_0x00af:
        r26 = 0;
    L_0x00b1:
        r28 = r2;
        r2 = r4;
        r27 = 0;
        r4 = r3;
        r3 = r5;
        r5 = 0;
    L_0x00b9:
        r0 = r46.size();
        if (r5 >= r0) goto L_0x06e4;
    L_0x00bf:
        r0 = r13.get(r5);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r29 = r0.getId();
        if (r29 <= 0) goto L_0x067a;
    L_0x00cb:
        r29 = r0.needDrawBluredPreview();
        if (r29 == 0) goto L_0x00d3;
    L_0x00d1:
        goto L_0x067a;
    L_0x00d3:
        r29 = r1;
        if (r17 != 0) goto L_0x012a;
    L_0x00d7:
        r31 = r0.isSticker();
        if (r31 != 0) goto L_0x00ef;
    L_0x00dd:
        r31 = r0.isAnimatedSticker();
        if (r31 != 0) goto L_0x00ef;
    L_0x00e3:
        r31 = r0.isGif();
        if (r31 != 0) goto L_0x00ef;
    L_0x00e9:
        r31 = r0.isGame();
        if (r31 == 0) goto L_0x012a;
    L_0x00ef:
        if (r27 != 0) goto L_0x0111;
    L_0x00f1:
        r0 = 8;
        r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r7, r0);
        if (r0 == 0) goto L_0x00fc;
    L_0x00f9:
        r27 = 4;
        goto L_0x00fe;
    L_0x00fc:
        r27 = 1;
    L_0x00fe:
        r15 = r5;
        r30 = r6;
        r34 = r7;
        r31 = r8;
        r35 = r10;
        r33 = r24;
        r1 = r29;
        r36 = 1;
        r37 = 0;
        goto L_0x06ce;
    L_0x0111:
        r25 = r3;
        r15 = r5;
        r30 = r6;
        r34 = r7;
        r31 = r8;
        r35 = r10;
        r33 = r24;
        r23 = r29;
        r36 = 1;
        r37 = 0;
        r24 = r2;
        r29 = r4;
        goto L_0x06c6;
    L_0x012a:
        r31 = 2;
        if (r18 != 0) goto L_0x014a;
    L_0x012e:
        r1 = r0.messageOwner;
        r1 = r1.media;
        r12 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r12 != 0) goto L_0x013a;
    L_0x0136:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r1 == 0) goto L_0x014a;
    L_0x013a:
        if (r27 != 0) goto L_0x0111;
    L_0x013c:
        r0 = 7;
        r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r7, r0);
        if (r0 == 0) goto L_0x0147;
    L_0x0143:
        r0 = 5;
        r27 = 5;
        goto L_0x00fe;
    L_0x0147:
        r27 = 2;
        goto L_0x00fe;
    L_0x014a:
        if (r19 != 0) goto L_0x0166;
    L_0x014c:
        r1 = r0.messageOwner;
        r1 = r1.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r1 == 0) goto L_0x0166;
    L_0x0154:
        if (r27 != 0) goto L_0x0111;
    L_0x0156:
        r0 = 10;
        r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r7, r0);
        if (r0 == 0) goto L_0x0162;
    L_0x015e:
        r0 = 6;
        r27 = 6;
        goto L_0x00fe;
    L_0x0162:
        r0 = 3;
        r27 = 3;
        goto L_0x00fe;
    L_0x0166:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;
        r1.<init>();
        r33 = r0.getDialogId();
        r12 = (r33 > r8 ? 1 : (r33 == r8 ? 0 : -1));
        if (r12 != 0) goto L_0x0185;
    L_0x0173:
        r12 = r0.messageOwner;
        r12 = r12.from_id;
        r33 = r45.getUserConfig();
        r34 = r7;
        r7 = r33.getClientUserId();
        if (r12 != r7) goto L_0x0187;
    L_0x0183:
        r7 = 1;
        goto L_0x0188;
    L_0x0185:
        r34 = r7;
    L_0x0187:
        r7 = 0;
    L_0x0188:
        r12 = r0.isForwarded();
        if (r12 == 0) goto L_0x01be;
    L_0x018e:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader;
        r7.<init>();
        r1.fwd_from = r7;
        r7 = r1.fwd_from;
        r12 = r0.messageOwner;
        r12 = r12.fwd_from;
        r33 = r10;
        r10 = r12.flags;
        r7.flags = r10;
        r10 = r12.from_id;
        r7.from_id = r10;
        r10 = r12.date;
        r7.date = r10;
        r10 = r12.channel_id;
        r7.channel_id = r10;
        r10 = r12.channel_post;
        r7.channel_post = r10;
        r10 = r12.post_author;
        r7.post_author = r10;
        r10 = r12.from_name;
        r7.from_name = r10;
        r10 = 4;
        r1.flags = r10;
        goto L_0x0260;
    L_0x01be:
        r33 = r10;
        r10 = 4;
        if (r7 != 0) goto L_0x0260;
    L_0x01c3:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader;
        r7.<init>();
        r1.fwd_from = r7;
        r7 = r1.fwd_from;
        r12 = r0.getId();
        r7.channel_post = r12;
        r7 = r1.fwd_from;
        r12 = r7.flags;
        r12 = r12 | r10;
        r7.flags = r12;
        r7 = r0.isFromUser();
        if (r7 == 0) goto L_0x01ee;
    L_0x01df:
        r7 = r1.fwd_from;
        r10 = r0.messageOwner;
        r10 = r10.from_id;
        r7.from_id = r10;
        r10 = r7.flags;
        r12 = 1;
        r10 = r10 | r12;
        r7.flags = r10;
        goto L_0x020e;
    L_0x01ee:
        r7 = r1.fwd_from;
        r10 = r0.messageOwner;
        r12 = r10.to_id;
        r12 = r12.channel_id;
        r7.channel_id = r12;
        r12 = r7.flags;
        r12 = r12 | 2;
        r7.flags = r12;
        r12 = r10.post;
        if (r12 == 0) goto L_0x020e;
    L_0x0202:
        r10 = r10.from_id;
        if (r10 <= 0) goto L_0x020e;
    L_0x0206:
        r7.from_id = r10;
        r10 = r7.flags;
        r12 = 1;
        r10 = r10 | r12;
        r7.flags = r10;
    L_0x020e:
        r7 = r0.messageOwner;
        r7 = r7.post_author;
        if (r7 == 0) goto L_0x0220;
    L_0x0214:
        r10 = r1.fwd_from;
        r10.post_author = r7;
        r7 = r10.flags;
        r12 = 8;
        r7 = r7 | r12;
        r10.flags = r7;
        goto L_0x0257;
    L_0x0220:
        r7 = r0.isOutOwner();
        if (r7 != 0) goto L_0x0257;
    L_0x0226:
        r7 = r0.messageOwner;
        r10 = r7.from_id;
        if (r10 <= 0) goto L_0x0257;
    L_0x022c:
        r7 = r7.post;
        if (r7 == 0) goto L_0x0257;
    L_0x0230:
        r7 = r45.getMessagesController();
        r10 = r0.messageOwner;
        r10 = r10.from_id;
        r10 = java.lang.Integer.valueOf(r10);
        r7 = r7.getUser(r10);
        if (r7 == 0) goto L_0x0257;
    L_0x0242:
        r10 = r1.fwd_from;
        r12 = r7.first_name;
        r7 = r7.last_name;
        r7 = org.telegram.messenger.ContactsController.formatName(r12, r7);
        r10.post_author = r7;
        r7 = r1.fwd_from;
        r10 = r7.flags;
        r12 = 8;
        r10 = r10 | r12;
        r7.flags = r10;
    L_0x0257:
        r7 = r0.messageOwner;
        r7 = r7.date;
        r1.date = r7;
        r7 = 4;
        r1.flags = r7;
    L_0x0260:
        r7 = (r14 > r8 ? 1 : (r14 == r8 ? 0 : -1));
        if (r7 != 0) goto L_0x027c;
    L_0x0264:
        r7 = r1.fwd_from;
        if (r7 == 0) goto L_0x027c;
    L_0x0268:
        r10 = r7.flags;
        r10 = r10 | 16;
        r7.flags = r10;
        r10 = r0.getId();
        r7.saved_from_msg_id = r10;
        r7 = r1.fwd_from;
        r10 = r0.messageOwner;
        r10 = r10.to_id;
        r7.saved_from_peer = r10;
    L_0x027c:
        if (r21 != 0) goto L_0x028e;
    L_0x027e:
        r7 = r0.messageOwner;
        r7 = r7.media;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r7 == 0) goto L_0x028e;
    L_0x0286:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r7.<init>();
        r1.media = r7;
        goto L_0x0294;
    L_0x028e:
        r7 = r0.messageOwner;
        r7 = r7.media;
        r1.media = r7;
    L_0x0294:
        r7 = r1.media;
        if (r7 == 0) goto L_0x029e;
    L_0x0298:
        r7 = r1.flags;
        r7 = r7 | 512;
        r1.flags = r7;
    L_0x029e:
        if (r20 == 0) goto L_0x02a7;
    L_0x02a0:
        r7 = r1.flags;
        r10 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r7 = r7 | r10;
        r1.flags = r7;
    L_0x02a7:
        r7 = r0.messageOwner;
        r7 = r7.via_bot_id;
        if (r7 == 0) goto L_0x02b5;
    L_0x02ad:
        r1.via_bot_id = r7;
        r7 = r1.flags;
        r7 = r7 | 2048;
        r1.flags = r7;
    L_0x02b5:
        r7 = r0.messageOwner;
        r7 = r7.message;
        r1.message = r7;
        r7 = r0.getId();
        r1.fwd_msg_id = r7;
        r7 = r0.messageOwner;
        r10 = r7.attachPath;
        r1.attachPath = r10;
        r10 = r7.entities;
        r1.entities = r10;
        r7 = r7.reply_markup;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
        if (r7 == 0) goto L_0x0365;
    L_0x02d1:
        r7 = r1.flags;
        r7 = r7 | 64;
        r1.flags = r7;
        r7 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
        r7.<init>();
        r1.reply_markup = r7;
        r7 = r0.messageOwner;
        r7 = r7.reply_markup;
        r7 = r7.rows;
        r7 = r7.size();
        r10 = 0;
    L_0x02e9:
        if (r10 >= r7) goto L_0x0365;
    L_0x02eb:
        r12 = r0.messageOwner;
        r12 = r12.reply_markup;
        r12 = r12.rows;
        r12 = r12.get(r10);
        r12 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r12;
        r30 = r7;
        r7 = r12.buttons;
        r7 = r7.size();
        r31 = r8;
        r9 = r16;
        r8 = 0;
    L_0x0304:
        if (r8 >= r7) goto L_0x035c;
    L_0x0306:
        r35 = r7;
        r7 = r12.buttons;
        r7 = r7.get(r8);
        r7 = (org.telegram.tgnet.TLRPC.KeyboardButton) r7;
        r36 = r12;
        r12 = r7 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth;
        if (r12 != 0) goto L_0x031e;
    L_0x0316:
        r14 = r7 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
        if (r14 != 0) goto L_0x031e;
    L_0x031a:
        r14 = r7 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
        if (r14 == 0) goto L_0x0353;
    L_0x031e:
        if (r12 == 0) goto L_0x033f;
    L_0x0320:
        r12 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
        r12.<init>();
        r14 = r7.flags;
        r12.flags = r14;
        r14 = r7.fwd_text;
        if (r14 == 0) goto L_0x0332;
    L_0x032d:
        r12.fwd_text = r14;
        r12.text = r14;
        goto L_0x0336;
    L_0x0332:
        r14 = r7.text;
        r12.text = r14;
    L_0x0336:
        r14 = r7.url;
        r12.url = r14;
        r7 = r7.button_id;
        r12.button_id = r7;
        goto L_0x0340;
    L_0x033f:
        r12 = r7;
    L_0x0340:
        if (r9 != 0) goto L_0x034e;
    L_0x0342:
        r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow;
        r9.<init>();
        r7 = r1.reply_markup;
        r7 = r7.rows;
        r7.add(r9);
    L_0x034e:
        r7 = r9.buttons;
        r7.add(r12);
    L_0x0353:
        r8 = r8 + 1;
        r14 = r47;
        r7 = r35;
        r12 = r36;
        goto L_0x0304;
    L_0x035c:
        r10 = r10 + 1;
        r14 = r47;
        r7 = r30;
        r8 = r31;
        goto L_0x02e9;
    L_0x0365:
        r31 = r8;
        r7 = r1.entities;
        r7 = r7.isEmpty();
        if (r7 != 0) goto L_0x0375;
    L_0x036f:
        r7 = r1.flags;
        r7 = r7 | 128;
        r1.flags = r7;
    L_0x0375:
        r7 = r1.attachPath;
        if (r7 != 0) goto L_0x037d;
    L_0x0379:
        r7 = "";
        r1.attachPath = r7;
    L_0x037d:
        r7 = r45.getUserConfig();
        r7 = r7.getNewMessageId();
        r1.id = r7;
        r1.local_id = r7;
        r7 = 1;
        r1.out = r7;
        r7 = r0.messageOwner;
        r7 = r7.grouped_id;
        r9 = 0;
        r12 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
        if (r12 == 0) goto L_0x03bc;
    L_0x0396:
        r12 = r6.get(r7);
        r12 = (java.lang.Long) r12;
        if (r12 != 0) goto L_0x03af;
    L_0x039e:
        r12 = org.telegram.messenger.Utilities.random;
        r14 = r12.nextLong();
        r12 = java.lang.Long.valueOf(r14);
        r14 = r0.messageOwner;
        r14 = r14.grouped_id;
        r6.put(r14, r12);
    L_0x03af:
        r14 = r12.longValue();
        r1.grouped_id = r14;
        r12 = r1.flags;
        r14 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r12 = r12 | r14;
        r1.flags = r12;
    L_0x03bc:
        r12 = r46.size();
        r14 = 1;
        r12 = r12 - r14;
        if (r5 == r12) goto L_0x03dc;
    L_0x03c4:
        r12 = r5 + 1;
        r12 = r13.get(r12);
        r12 = (org.telegram.messenger.MessageObject) r12;
        r12 = r12.messageOwner;
        r14 = r12.grouped_id;
        r12 = r0.messageOwner;
        r9 = r12.grouped_id;
        r12 = (r14 > r9 ? 1 : (r14 == r9 ? 0 : -1));
        if (r12 == 0) goto L_0x03dc;
    L_0x03d8:
        r9 = r24;
        r10 = 1;
        goto L_0x03df;
    L_0x03dc:
        r9 = r24;
        r10 = 0;
    L_0x03df:
        r12 = r9.channel_id;
        if (r12 == 0) goto L_0x03f7;
    L_0x03e3:
        if (r20 != 0) goto L_0x03f7;
    L_0x03e5:
        if (r22 == 0) goto L_0x03f0;
    L_0x03e7:
        r12 = r45.getUserConfig();
        r12 = r12.getClientUserId();
        goto L_0x03f1;
    L_0x03f0:
        r12 = -r12;
    L_0x03f1:
        r1.from_id = r12;
        r12 = 1;
        r1.post = r12;
        goto L_0x0407;
    L_0x03f7:
        r12 = r45.getUserConfig();
        r12 = r12.getClientUserId();
        r1.from_id = r12;
        r12 = r1.flags;
        r12 = r12 | 256;
        r1.flags = r12;
    L_0x0407:
        r14 = r1.random_id;
        r35 = 0;
        r12 = (r14 > r35 ? 1 : (r14 == r35 ? 0 : -1));
        if (r12 != 0) goto L_0x0415;
    L_0x040f:
        r14 = r45.getNextRandomId();
        r1.random_id = r14;
    L_0x0415:
        r14 = r1.random_id;
        r12 = java.lang.Long.valueOf(r14);
        r4.add(r12);
        r14 = r1.random_id;
        r3.put(r14, r1);
        r12 = r1.fwd_msg_id;
        r12 = java.lang.Integer.valueOf(r12);
        r2.add(r12);
        if (r11 == 0) goto L_0x0430;
    L_0x042e:
        r12 = r11;
        goto L_0x0438;
    L_0x0430:
        r12 = r45.getConnectionsManager();
        r12 = r12.getCurrentTime();
    L_0x0438:
        r1.date = r12;
        r12 = r33;
        r14 = r12 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r14 == 0) goto L_0x0451;
    L_0x0440:
        if (r20 != 0) goto L_0x0451;
    L_0x0442:
        if (r11 != 0) goto L_0x044d;
    L_0x0444:
        r15 = 1;
        r1.views = r15;
        r15 = r1.flags;
        r15 = r15 | 1024;
        r1.flags = r15;
    L_0x044d:
        r15 = r2;
        r24 = r3;
        goto L_0x046b;
    L_0x0451:
        r15 = r0.messageOwner;
        r24 = r3;
        r3 = r15.flags;
        r3 = r3 & 1024;
        if (r3 == 0) goto L_0x0467;
    L_0x045b:
        if (r11 != 0) goto L_0x0467;
    L_0x045d:
        r3 = r15.views;
        r1.views = r3;
        r3 = r1.flags;
        r3 = r3 | 1024;
        r1.flags = r3;
    L_0x0467:
        r3 = 1;
        r1.unread = r3;
        r15 = r2;
    L_0x046b:
        r2 = r47;
        r1.dialog_id = r2;
        r1.to_id = r9;
        r30 = org.telegram.messenger.MessageObject.isVoiceMessage(r1);
        if (r30 != 0) goto L_0x047d;
    L_0x0477:
        r30 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r1);
        if (r30 == 0) goto L_0x048f;
    L_0x047d:
        if (r14 == 0) goto L_0x048c;
    L_0x047f:
        r14 = r0.getChannelId();
        if (r14 == 0) goto L_0x048c;
    L_0x0485:
        r14 = r0.isContentUnread();
        r1.media_unread = r14;
        goto L_0x048f;
    L_0x048c:
        r14 = 1;
        r1.media_unread = r14;
    L_0x048f:
        r14 = r0.messageOwner;
        r14 = r14.to_id;
        r30 = r6;
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;
        if (r6 == 0) goto L_0x049e;
    L_0x0499:
        r6 = r14.channel_id;
        r6 = -r6;
        r1.ttl = r6;
    L_0x049e:
        r6 = new org.telegram.messenger.MessageObject;
        r14 = r45;
        r33 = r9;
        r9 = r14.currentAccount;
        r37 = r15;
        r15 = 1;
        r6.<init>(r9, r1, r15);
        if (r11 == 0) goto L_0x04b0;
    L_0x04ae:
        r9 = 1;
        goto L_0x04b1;
    L_0x04b0:
        r9 = 0;
    L_0x04b1:
        r6.scheduled = r9;
        r9 = r6.messageOwner;
        r9.send_state = r15;
        r9 = r29;
        r9.add(r6);
        r6 = r28;
        r6.add(r1);
        if (r11 == 0) goto L_0x04c5;
    L_0x04c3:
        r15 = 1;
        goto L_0x04c6;
    L_0x04c5:
        r15 = 0;
    L_0x04c6:
        r14.putToSendingMessages(r1, r15);
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x0501;
    L_0x04cd:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r15 = "forward message user_id = ";
        r1.append(r15);
        r15 = r12.user_id;
        r1.append(r15);
        r15 = " chat_id = ";
        r1.append(r15);
        r15 = r12.chat_id;
        r1.append(r15);
        r15 = " channel_id = ";
        r1.append(r15);
        r15 = r12.channel_id;
        r1.append(r15);
        r15 = " access_hash = ";
        r1.append(r15);
        r14 = r12.access_hash;
        r1.append(r14);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
    L_0x0501:
        if (r10 == 0) goto L_0x0509;
    L_0x0503:
        r1 = r6.size();
        if (r1 > 0) goto L_0x0548;
    L_0x0509:
        r1 = r6.size();
        r10 = 100;
        if (r1 == r10) goto L_0x0548;
    L_0x0511:
        r1 = r46.size();
        r10 = 1;
        r1 = r1 - r10;
        if (r5 == r1) goto L_0x0548;
    L_0x0519:
        r1 = r46.size();
        r1 = r1 - r10;
        if (r5 == r1) goto L_0x0535;
    L_0x0520:
        r1 = r5 + 1;
        r1 = r13.get(r1);
        r1 = (org.telegram.messenger.MessageObject) r1;
        r14 = r1.getDialogId();
        r28 = r0.getDialogId();
        r1 = (r14 > r28 ? 1 : (r14 == r28 ? 0 : -1));
        if (r1 == 0) goto L_0x0535;
    L_0x0534:
        goto L_0x0548;
    L_0x0535:
        r29 = r4;
        r15 = r5;
        r28 = r6;
        r23 = r9;
        r35 = r12;
        r25 = r24;
        r24 = r37;
        r36 = 1;
        r37 = 0;
        goto L_0x06c6;
    L_0x0548:
        r38 = r45.getMessagesStorage();
        r1 = new java.util.ArrayList;
        r1.<init>(r6);
        r40 = 0;
        r41 = 1;
        r42 = 0;
        r43 = 0;
        if (r11 == 0) goto L_0x055e;
    L_0x055b:
        r44 = 1;
        goto L_0x0560;
    L_0x055e:
        r44 = 0;
    L_0x0560:
        r39 = r1;
        r38.putMessages(r39, r40, r41, r42, r43, r44);
        r1 = r45.getMessagesController();
        if (r11 == 0) goto L_0x056d;
    L_0x056b:
        r10 = 1;
        goto L_0x056e;
    L_0x056d:
        r10 = 0;
    L_0x056e:
        r1.updateInterfaceWithMessages(r2, r9, r10);
        r1 = r45.getNotificationCenter();
        r10 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r14 = 0;
        r15 = new java.lang.Object[r14];
        r1.postNotificationName(r10, r15);
        r1 = r45.getUserConfig();
        r1.saveConfig(r14);
        r14 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;
        r14.<init>();
        r14.to_peer = r12;
        r28 = 0;
        r1 = (r7 > r28 ? 1 : (r7 == r28 ? 0 : -1));
        if (r1 == 0) goto L_0x0593;
    L_0x0591:
        r1 = 1;
        goto L_0x0594;
    L_0x0593:
        r1 = 0;
    L_0x0594:
        r14.grouped = r1;
        r15 = r45;
        if (r49 == 0) goto L_0x05bb;
    L_0x059a:
        r1 = r15.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getNotificationsSettings(r1);
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "silent_";
        r7.append(r8);
        r7.append(r2);
        r7 = r7.toString();
        r8 = 0;
        r1 = r1.getBoolean(r7, r8);
        if (r1 == 0) goto L_0x05b9;
    L_0x05b8:
        goto L_0x05bb;
    L_0x05b9:
        r1 = 0;
        goto L_0x05bc;
    L_0x05bb:
        r1 = 1;
    L_0x05bc:
        r14.silent = r1;
        if (r11 == 0) goto L_0x05c8;
    L_0x05c0:
        r14.schedule_date = r11;
        r1 = r14.flags;
        r1 = r1 | 1024;
        r14.flags = r1;
    L_0x05c8:
        r1 = r0.messageOwner;
        r1 = r1.to_id;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;
        if (r1 == 0) goto L_0x05fa;
    L_0x05d0:
        r1 = r45.getMessagesController();
        r7 = r0.messageOwner;
        r7 = r7.to_id;
        r7 = r7.channel_id;
        r7 = java.lang.Integer.valueOf(r7);
        r1 = r1.getChat(r7);
        r7 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
        r7.<init>();
        r14.from_peer = r7;
        r7 = r14.from_peer;
        r0 = r0.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        r7.channel_id = r0;
        if (r1 == 0) goto L_0x0601;
    L_0x05f5:
        r0 = r1.access_hash;
        r7.access_hash = r0;
        goto L_0x0601;
    L_0x05fa:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r0.<init>();
        r14.from_peer = r0;
    L_0x0601:
        r14.random_id = r4;
        r7 = r37;
        r14.id = r7;
        r0 = r46.size();
        r8 = 1;
        r10 = 0;
        if (r0 != r8) goto L_0x061d;
    L_0x060f:
        r0 = r13.get(r10);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.messageOwner;
        r0 = r0.with_my_score;
        if (r0 == 0) goto L_0x061d;
    L_0x061b:
        r0 = 1;
        goto L_0x061e;
    L_0x061d:
        r0 = 0;
    L_0x061e:
        r14.with_my_score = r0;
        r1 = r45.getConnectionsManager();
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Cq-Pp6aJXk_DqFJ6S7nu4b2IR0k;
        r28 = r6;
        r6 = r0;
        r23 = r9;
        r9 = r1;
        r1 = r45;
        r25 = r24;
        r24 = r7;
        r2 = r47;
        r29 = r4;
        r4 = r50;
        r7 = r5;
        r5 = r20;
        r35 = r12;
        r12 = r6;
        r6 = r26;
        r15 = r7;
        r7 = r25;
        r36 = 1;
        r8 = r28;
        r13 = r9;
        r9 = r23;
        r37 = 0;
        r10 = r33;
        r11 = r14;
        r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11);
        r0 = 68;
        r13.sendRequest(r14, r12, r0);
        r0 = r46.size();
        r0 = r0 + -1;
        if (r15 == r0) goto L_0x06c6;
    L_0x065f:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r28 = new java.util.ArrayList;
        r28.<init>();
        r0 = new java.util.ArrayList;
        r0.<init>();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new android.util.LongSparseArray;
        r3.<init>();
        r4 = r0;
        goto L_0x06ce;
    L_0x067a:
        r23 = r1;
        r25 = r3;
        r29 = r4;
        r15 = r5;
        r30 = r6;
        r34 = r7;
        r31 = r8;
        r35 = r10;
        r33 = r24;
        r36 = 1;
        r37 = 0;
        r24 = r2;
        r1 = r0.type;
        if (r1 != 0) goto L_0x06c6;
    L_0x0695:
        r1 = r0.messageText;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x06c6;
    L_0x069d:
        r1 = r0.messageOwner;
        r1 = r1.media;
        if (r1 == 0) goto L_0x06a7;
    L_0x06a3:
        r1 = r1.webpage;
        r5 = r1;
        goto L_0x06a9;
    L_0x06a7:
        r5 = r16;
    L_0x06a9:
        r1 = r0.messageText;
        r1 = r1.toString();
        r4 = 0;
        if (r5 == 0) goto L_0x06b4;
    L_0x06b2:
        r6 = 1;
        goto L_0x06b5;
    L_0x06b4:
        r6 = 0;
    L_0x06b5:
        r0 = r0.messageOwner;
        r7 = r0.entities;
        r8 = 0;
        r9 = 0;
        r0 = r45;
        r2 = r47;
        r10 = r49;
        r11 = r50;
        r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x06c6:
        r1 = r23;
        r2 = r24;
        r3 = r25;
        r4 = r29;
    L_0x06ce:
        r5 = r15 + 1;
        r12 = r45;
        r13 = r46;
        r14 = r47;
        r11 = r50;
        r6 = r30;
        r8 = r31;
        r24 = r33;
        r7 = r34;
        r10 = r35;
        goto L_0x00b9;
    L_0x06e4:
        r3 = r45;
        goto L_0x0706;
    L_0x06e7:
        r37 = 0;
        r0 = 0;
    L_0x06ea:
        r1 = r46.size();
        if (r0 >= r1) goto L_0x0702;
    L_0x06f0:
        r1 = r46;
        r2 = r1.get(r0);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r3 = r45;
        r4 = r47;
        r3.processForwardFromMyName(r2, r4);
        r0 = r0 + 1;
        goto L_0x06ea;
    L_0x0702:
        r3 = r45;
        r27 = 0;
    L_0x0706:
        return r27;
    L_0x0707:
        r3 = r12;
        r37 = 0;
        return r37;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, int):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x00e8  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0109  */
    public /* synthetic */ void lambda$sendMessage$9$SendMessagesHelper(long r24, int r26, boolean r27, boolean r28, android.util.LongSparseArray r29, java.util.ArrayList r30, java.util.ArrayList r31, org.telegram.tgnet.TLRPC.Peer r32, org.telegram.tgnet.TLRPC.TL_messages_forwardMessages r33, org.telegram.tgnet.TLObject r34, org.telegram.tgnet.TLRPC.TL_error r35) {
        /*
        r23 = this;
        r11 = r23;
        r12 = r26;
        r13 = r30;
        r14 = r31;
        r0 = r35;
        r15 = 1;
        r10 = 0;
        if (r0 != 0) goto L_0x01b8;
    L_0x000e:
        r9 = new org.telegram.messenger.support.SparseLongArray;
        r9.<init>();
        r7 = r34;
        r7 = (org.telegram.tgnet.TLRPC.Updates) r7;
        r0 = 0;
    L_0x0018:
        r1 = r7.updates;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x003e;
    L_0x0020:
        r1 = r7.updates;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Update) r1;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateMessageID;
        if (r2 == 0) goto L_0x003c;
    L_0x002c:
        r1 = (org.telegram.tgnet.TLRPC.TL_updateMessageID) r1;
        r2 = r1.id;
        r3 = r1.random_id;
        r9.put(r2, r3);
        r1 = r7.updates;
        r1.remove(r0);
        r0 = r0 + -1;
    L_0x003c:
        r0 = r0 + r15;
        goto L_0x0018;
    L_0x003e:
        r0 = r23.getMessagesController();
        r0 = r0.dialogs_read_outbox_max;
        r1 = java.lang.Long.valueOf(r24);
        r0 = r0.get(r1);
        r0 = (java.lang.Integer) r0;
        if (r12 != 0) goto L_0x0071;
    L_0x0050:
        if (r0 != 0) goto L_0x006e;
    L_0x0052:
        r0 = r23.getMessagesStorage();
        r5 = r24;
        r0 = r0.getDialogReadMax(r15, r5);
        r0 = java.lang.Integer.valueOf(r0);
        r1 = r23.getMessagesController();
        r1 = r1.dialogs_read_outbox_max;
        r2 = java.lang.Long.valueOf(r24);
        r1.put(r2, r0);
        goto L_0x0077;
    L_0x006e:
        r5 = r24;
        goto L_0x0077;
    L_0x0071:
        r5 = r24;
        r0 = java.lang.Integer.valueOf(r10);
    L_0x0077:
        r16 = r0;
        r0 = 0;
        r8 = 0;
    L_0x007b:
        r1 = r7.updates;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x019a;
    L_0x0083:
        r1 = r7.updates;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Update) r1;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewMessage;
        if (r2 != 0) goto L_0x00a1;
    L_0x008f:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r3 != 0) goto L_0x00a1;
    L_0x0093:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage;
        if (r3 == 0) goto L_0x0098;
    L_0x0097:
        goto L_0x00a1;
    L_0x0098:
        r17 = r0;
        r14 = r7;
        r20 = r9;
        r1 = 1;
        r13 = 0;
        goto L_0x0189;
    L_0x00a1:
        r3 = r7.updates;
        r3.remove(r0);
        r17 = r0 + -1;
        r0 = -1;
        if (r2 == 0) goto L_0x00bc;
    L_0x00ab:
        r1 = (org.telegram.tgnet.TLRPC.TL_updateNewMessage) r1;
        r2 = r1.message;
        r3 = r23.getMessagesController();
        r4 = r1.pts;
        r1 = r1.pts_count;
        r3.processNewDifferenceParams(r0, r4, r0, r1);
    L_0x00ba:
        r4 = r2;
        goto L_0x00e3;
    L_0x00bc:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage;
        if (r2 == 0) goto L_0x00c6;
    L_0x00c0:
        r1 = (org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage) r1;
        r1 = r1.message;
        r4 = r1;
        goto L_0x00e3;
    L_0x00c6:
        r1 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r1;
        r2 = r1.message;
        r3 = r23.getMessagesController();
        r4 = r1.pts;
        r1 = r1.pts_count;
        r0 = r2.to_id;
        r0 = r0.channel_id;
        r3.processNewChannelDifferenceParams(r4, r1, r0);
        if (r27 == 0) goto L_0x00ba;
    L_0x00db:
        r0 = r2.flags;
        r1 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r0 = r0 | r1;
        r2.flags = r0;
        goto L_0x00ba;
    L_0x00e3:
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r4);
        if (r12 != 0) goto L_0x00f5;
    L_0x00e8:
        r0 = r16.intValue();
        r1 = r4.id;
        if (r0 >= r1) goto L_0x00f2;
    L_0x00f0:
        r0 = 1;
        goto L_0x00f3;
    L_0x00f2:
        r0 = 0;
    L_0x00f3:
        r4.unread = r0;
    L_0x00f5:
        if (r28 == 0) goto L_0x00fd;
    L_0x00f7:
        r4.out = r15;
        r4.unread = r10;
        r4.media_unread = r10;
    L_0x00fd:
        r0 = r4.id;
        r0 = r9.get(r0);
        r2 = 0;
        r18 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r18 == 0) goto L_0x0184;
    L_0x0109:
        r3 = r29;
        r0 = r3.get(r0);
        r2 = r0;
        r2 = (org.telegram.tgnet.TLRPC.Message) r2;
        if (r2 != 0) goto L_0x0116;
    L_0x0114:
        goto L_0x0184;
    L_0x0116:
        r0 = r13.indexOf(r2);
        r1 = -1;
        if (r0 != r1) goto L_0x011f;
    L_0x011d:
        goto L_0x0184;
    L_0x011f:
        r1 = r14.get(r0);
        r18 = r1;
        r18 = (org.telegram.messenger.MessageObject) r18;
        r13.remove(r0);
        r14.remove(r0);
        r1 = r2.id;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r0.add(r4);
        r10 = r4.id;
        r19 = 0;
        r20 = 1;
        r21 = r0;
        r0 = r23;
        r22 = r1;
        r1 = r18;
        r15 = r2;
        r2 = r4;
        r3 = r10;
        r10 = r4;
        r4 = r19;
        r5 = r20;
        r0.updateMediaPaths(r1, r2, r3, r4, r5);
        r18 = r18.getMediaExistanceFlags();
        r0 = r10.id;
        r15.id = r0;
        r19 = r8 + 1;
        r0 = r23.getMessagesStorage();
        r8 = r0.getStorageQueue();
        r6 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$prDl05bngA_A-GBOjFAUKuCXkqM;
        r0 = r6;
        r1 = r23;
        r2 = r15;
        r3 = r22;
        r4 = r32;
        r5 = r26;
        r15 = r6;
        r6 = r21;
        r14 = r7;
        r12 = r8;
        r7 = r24;
        r20 = r9;
        r9 = r10;
        r13 = 0;
        r10 = r18;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10);
        r12.postRunnable(r15);
        r8 = r19;
        goto L_0x0188;
    L_0x0184:
        r14 = r7;
        r20 = r9;
        r13 = 0;
    L_0x0188:
        r1 = 1;
    L_0x0189:
        r0 = r17 + 1;
        r5 = r24;
        r12 = r26;
        r13 = r30;
        r7 = r14;
        r9 = r20;
        r10 = 0;
        r15 = 1;
        r14 = r31;
        goto L_0x007b;
    L_0x019a:
        r14 = r7;
        r1 = 1;
        r13 = 0;
        r0 = r14.updates;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x01ac;
    L_0x01a5:
        r0 = r23.getMessagesController();
        r0.processUpdates(r14, r13);
    L_0x01ac:
        r0 = r23.getStatsController();
        r2 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType();
        r0.incrementSentItemsCount(r2, r1, r8);
        goto L_0x01c4;
    L_0x01b8:
        r1 = 1;
        r13 = 0;
        r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$l40Sz3n3XBnqm5WNgM6XcCJnSAw;
        r3 = r33;
        r2.<init>(r11, r0, r3);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x01c4:
        r0 = 0;
    L_0x01c5:
        r2 = r30.size();
        if (r0 >= r2) goto L_0x01ec;
    L_0x01cb:
        r2 = r30;
        r3 = r2.get(r0);
        r3 = (org.telegram.tgnet.TLRPC.Message) r3;
        r4 = r23.getMessagesStorage();
        r5 = r26;
        if (r5 == 0) goto L_0x01dd;
    L_0x01db:
        r6 = 1;
        goto L_0x01de;
    L_0x01dd:
        r6 = 0;
    L_0x01de:
        r4.markMessageAsSendError(r3, r6);
        r4 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$OHBcFTYbHbfqBpK4P0nk4CqrEKo;
        r4.<init>(r11, r3, r5);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
        r0 = r0 + 1;
        goto L_0x01c5;
    L_0x01ec:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendMessage$9$SendMessagesHelper(long, int, boolean, boolean, android.util.LongSparseArray, java.util.ArrayList, java.util.ArrayList, org.telegram.tgnet.TLRPC$Peer, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$null$6$SendMessagesHelper(Message message, int i, Peer peer, int i2, ArrayList arrayList, long j, Message message2, int i3) {
        Message message3 = message;
        getMessagesStorage().updateMessageStateAndId(message3.random_id, Integer.valueOf(i), message3.id, 0, false, peer.channel_id, i2 != 0 ? 1 : 0);
        getMessagesStorage().putMessages(arrayList, true, false, false, 0, i2 != 0);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$ZBaXhX5lq7HHk30Z9QRR_QRW04U(this, message, j, i, message2, i3, i2));
    }

    public /* synthetic */ void lambda$null$5$SendMessagesHelper(Message message, long j, int i, Message message2, int i2, int i3) {
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
        objArr[4] = Long.valueOf(0);
        objArr[5] = Integer.valueOf(i2);
        objArr[6] = Boolean.valueOf(i3 != 0);
        notificationCenter.postNotificationName(i4, objArr);
        processSentMessage(i);
        if (i3 != 0) {
            z = true;
        }
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$7$SendMessagesHelper(TL_error tL_error, TL_messages_forwardMessages tL_messages_forwardMessages) {
        AlertsCreator.processError(this.currentAccount, tL_error, null, tL_messages_forwardMessages, new Object[0]);
    }

    public /* synthetic */ void lambda$null$8$SendMessagesHelper(Message message, int i) {
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

    private void writePreviousMessageData(Message message, SerializedData serializedData) {
        message.media.serializeToStream(serializedData);
        String str = message.message;
        String str2 = "";
        if (str == null) {
            str = str2;
        }
        serializedData.writeString(str);
        str = message.attachPath;
        if (str == null) {
            str = str2;
        }
        serializedData.writeString(str);
        int size = message.entities.size();
        serializedData.writeInt32(size);
        for (int i = 0; i < size; i++) {
            ((MessageEntity) message.entities.get(i)).serializeToStream(serializedData);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00fd A:{Catch:{ Exception -> 0x04a6 }} */
    private void editMessageMedia(org.telegram.messenger.MessageObject r26, org.telegram.tgnet.TLRPC.TL_photo r27, org.telegram.messenger.VideoEditedInfo r28, org.telegram.tgnet.TLRPC.TL_document r29, java.lang.String r30, java.util.HashMap<java.lang.String, java.lang.String> r31, boolean r32, java.lang.Object r33) {
        /*
        r25 = this;
        r10 = r25;
        r11 = r26;
        r0 = r27;
        r1 = r29;
        r2 = r30;
        r8 = r33;
        r3 = "originalPath";
        if (r11 != 0) goto L_0x0011;
    L_0x0010:
        return;
    L_0x0011:
        r4 = r11.messageOwner;
        r5 = 0;
        r11.cancelEditing = r5;
        r6 = r26.getDialogId();	 Catch:{ Exception -> 0x04a6 }
        r9 = "http";
        r13 = 1;
        if (r32 == 0) goto L_0x0058;
    L_0x001f:
        r2 = r11.messageOwner;	 Catch:{ Exception -> 0x04a6 }
        r2 = r2.media;	 Catch:{ Exception -> 0x04a6 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x04a6 }
        if (r2 == 0) goto L_0x0033;
    L_0x0027:
        r0 = r11.messageOwner;	 Catch:{ Exception -> 0x04a6 }
        r0 = r0.media;	 Catch:{ Exception -> 0x04a6 }
        r0 = r0.photo;	 Catch:{ Exception -> 0x04a6 }
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;	 Catch:{ Exception -> 0x04a6 }
        r12 = r28;
        r2 = 2;
        goto L_0x0049;
    L_0x0033:
        r1 = r11.messageOwner;	 Catch:{ Exception -> 0x04a6 }
        r1 = r1.media;	 Catch:{ Exception -> 0x04a6 }
        r1 = r1.document;	 Catch:{ Exception -> 0x04a6 }
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;	 Catch:{ Exception -> 0x04a6 }
        r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1);	 Catch:{ Exception -> 0x04a6 }
        if (r2 != 0) goto L_0x0046;
    L_0x0041:
        if (r28 == 0) goto L_0x0044;
    L_0x0043:
        goto L_0x0046;
    L_0x0044:
        r2 = 7;
        goto L_0x0047;
    L_0x0046:
        r2 = 3;
    L_0x0047:
        r12 = r11.videoEditedInfo;	 Catch:{ Exception -> 0x04a6 }
    L_0x0049:
        r14 = r4.params;	 Catch:{ Exception -> 0x04a6 }
        r15 = r4.message;	 Catch:{ Exception -> 0x04a6 }
        r11.editingMessage = r15;	 Catch:{ Exception -> 0x04a6 }
        r15 = r4.entities;	 Catch:{ Exception -> 0x04a6 }
        r11.editingMessageEntities = r15;	 Catch:{ Exception -> 0x04a6 }
        r15 = r4.attachPath;	 Catch:{ Exception -> 0x04a6 }
        r13 = r2;
        goto L_0x0113;
    L_0x0058:
        r12 = r4.media;	 Catch:{ Exception -> 0x04a6 }
        r11.previousMedia = r12;	 Catch:{ Exception -> 0x04a6 }
        r12 = r4.message;	 Catch:{ Exception -> 0x04a6 }
        r11.previousCaption = r12;	 Catch:{ Exception -> 0x04a6 }
        r12 = r4.entities;	 Catch:{ Exception -> 0x04a6 }
        r11.previousCaptionEntities = r12;	 Catch:{ Exception -> 0x04a6 }
        r12 = r4.attachPath;	 Catch:{ Exception -> 0x04a6 }
        r11.previousAttachPath = r12;	 Catch:{ Exception -> 0x04a6 }
        r12 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x04a6 }
        r12.<init>(r13);	 Catch:{ Exception -> 0x04a6 }
        r10.writePreviousMessageData(r4, r12);	 Catch:{ Exception -> 0x04a6 }
        r14 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x04a6 }
        r12 = r12.length();	 Catch:{ Exception -> 0x04a6 }
        r14.<init>(r12);	 Catch:{ Exception -> 0x04a6 }
        r10.writePreviousMessageData(r4, r14);	 Catch:{ Exception -> 0x04a6 }
        if (r31 != 0) goto L_0x0084;
    L_0x007e:
        r12 = new java.util.HashMap;	 Catch:{ Exception -> 0x04a6 }
        r12.<init>();	 Catch:{ Exception -> 0x04a6 }
        goto L_0x0086;
    L_0x0084:
        r12 = r31;
    L_0x0086:
        r15 = "prevMedia";
        r13 = r14.toByteArray();	 Catch:{ Exception -> 0x04a6 }
        r13 = android.util.Base64.encodeToString(r13, r5);	 Catch:{ Exception -> 0x04a6 }
        r12.put(r15, r13);	 Catch:{ Exception -> 0x04a6 }
        r14.cleanup();	 Catch:{ Exception -> 0x04a6 }
        if (r0 == 0) goto L_0x00da;
    L_0x0098:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x04a6 }
        r13.<init>();	 Catch:{ Exception -> 0x04a6 }
        r4.media = r13;	 Catch:{ Exception -> 0x04a6 }
        r13 = r4.media;	 Catch:{ Exception -> 0x04a6 }
        r14 = r13.flags;	 Catch:{ Exception -> 0x04a6 }
        r15 = 3;
        r14 = r14 | r15;
        r13.flags = r14;	 Catch:{ Exception -> 0x04a6 }
        r13 = r4.media;	 Catch:{ Exception -> 0x04a6 }
        r13.photo = r0;	 Catch:{ Exception -> 0x04a6 }
        if (r2 == 0) goto L_0x00bc;
    L_0x00ad:
        r13 = r30.length();	 Catch:{ Exception -> 0x04a6 }
        if (r13 <= 0) goto L_0x00bc;
    L_0x00b3:
        r13 = r2.startsWith(r9);	 Catch:{ Exception -> 0x04a6 }
        if (r13 == 0) goto L_0x00bc;
    L_0x00b9:
        r4.attachPath = r2;	 Catch:{ Exception -> 0x04a6 }
        goto L_0x00d8;
    L_0x00bc:
        r13 = r0.sizes;	 Catch:{ Exception -> 0x04a6 }
        r14 = r0.sizes;	 Catch:{ Exception -> 0x04a6 }
        r14 = r14.size();	 Catch:{ Exception -> 0x04a6 }
        r15 = 1;
        r14 = r14 - r15;
        r13 = r13.get(r14);	 Catch:{ Exception -> 0x04a6 }
        r13 = (org.telegram.tgnet.TLRPC.PhotoSize) r13;	 Catch:{ Exception -> 0x04a6 }
        r13 = r13.location;	 Catch:{ Exception -> 0x04a6 }
        r13 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r15);	 Catch:{ Exception -> 0x04a6 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x04a6 }
        r4.attachPath = r13;	 Catch:{ Exception -> 0x04a6 }
    L_0x00d8:
        r13 = 2;
        goto L_0x010a;
    L_0x00da:
        if (r1 == 0) goto L_0x0109;
    L_0x00dc:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x04a6 }
        r13.<init>();	 Catch:{ Exception -> 0x04a6 }
        r4.media = r13;	 Catch:{ Exception -> 0x04a6 }
        r13 = r4.media;	 Catch:{ Exception -> 0x04a6 }
        r14 = r13.flags;	 Catch:{ Exception -> 0x04a6 }
        r15 = 3;
        r14 = r14 | r15;
        r13.flags = r14;	 Catch:{ Exception -> 0x04a6 }
        r13 = r4.media;	 Catch:{ Exception -> 0x04a6 }
        r13.document = r1;	 Catch:{ Exception -> 0x04a6 }
        r13 = org.telegram.messenger.MessageObject.isVideoDocument(r29);	 Catch:{ Exception -> 0x04a6 }
        if (r13 != 0) goto L_0x00fa;
    L_0x00f5:
        if (r28 == 0) goto L_0x00f8;
    L_0x00f7:
        goto L_0x00fa;
    L_0x00f8:
        r13 = 7;
        goto L_0x00fb;
    L_0x00fa:
        r13 = 3;
    L_0x00fb:
        if (r28 == 0) goto L_0x0106;
    L_0x00fd:
        r14 = r28.getString();	 Catch:{ Exception -> 0x04a6 }
        r15 = "ve";
        r12.put(r15, r14);	 Catch:{ Exception -> 0x04a6 }
    L_0x0106:
        r4.attachPath = r2;	 Catch:{ Exception -> 0x04a6 }
        goto L_0x010a;
    L_0x0109:
        r13 = -1;
    L_0x010a:
        r4.params = r12;	 Catch:{ Exception -> 0x04a6 }
        r14 = 3;
        r4.send_state = r14;	 Catch:{ Exception -> 0x04a6 }
        r15 = r2;
        r14 = r12;
        r12 = r28;
    L_0x0113:
        r2 = r4.attachPath;	 Catch:{ Exception -> 0x04a6 }
        if (r2 != 0) goto L_0x011b;
    L_0x0117:
        r2 = "";
        r4.attachPath = r2;	 Catch:{ Exception -> 0x04a6 }
    L_0x011b:
        r4.local_id = r5;	 Catch:{ Exception -> 0x04a6 }
        r2 = r11.type;	 Catch:{ Exception -> 0x04a6 }
        r5 = 3;
        if (r2 == r5) goto L_0x0129;
    L_0x0122:
        if (r12 != 0) goto L_0x0129;
    L_0x0124:
        r2 = r11.type;	 Catch:{ Exception -> 0x04a6 }
        r5 = 2;
        if (r2 != r5) goto L_0x0134;
    L_0x0129:
        r2 = r4.attachPath;	 Catch:{ Exception -> 0x04a6 }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x04a6 }
        if (r2 != 0) goto L_0x0134;
    L_0x0131:
        r2 = 1;
        r11.attachPathExists = r2;	 Catch:{ Exception -> 0x04a6 }
    L_0x0134:
        r2 = r11.videoEditedInfo;	 Catch:{ Exception -> 0x04a6 }
        if (r2 == 0) goto L_0x013c;
    L_0x0138:
        if (r12 != 0) goto L_0x013c;
    L_0x013a:
        r12 = r11.videoEditedInfo;	 Catch:{ Exception -> 0x04a6 }
    L_0x013c:
        if (r32 != 0) goto L_0x01bf;
    L_0x013e:
        r5 = r11.editingMessage;	 Catch:{ Exception -> 0x04a6 }
        if (r5 == 0) goto L_0x0175;
    L_0x0142:
        r5 = r11.editingMessage;	 Catch:{ Exception -> 0x04a6 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x04a6 }
        r4.message = r5;	 Catch:{ Exception -> 0x04a6 }
        r5 = r11.editingMessageEntities;	 Catch:{ Exception -> 0x04a6 }
        if (r5 == 0) goto L_0x0154;
    L_0x014e:
        r5 = r11.editingMessageEntities;	 Catch:{ Exception -> 0x04a6 }
        r4.entities = r5;	 Catch:{ Exception -> 0x04a6 }
    L_0x0152:
        r2 = 0;
        goto L_0x0170;
    L_0x0154:
        r5 = 1;
        r2 = new java.lang.CharSequence[r5];	 Catch:{ Exception -> 0x04a6 }
        r5 = r11.editingMessage;	 Catch:{ Exception -> 0x04a6 }
        r17 = 0;
        r2[r17] = r5;	 Catch:{ Exception -> 0x04a6 }
        r5 = r25.getMediaDataController();	 Catch:{ Exception -> 0x04a6 }
        r2 = r5.getEntities(r2);	 Catch:{ Exception -> 0x04a6 }
        if (r2 == 0) goto L_0x0152;
    L_0x0167:
        r5 = r2.isEmpty();	 Catch:{ Exception -> 0x04a6 }
        if (r5 != 0) goto L_0x0152;
    L_0x016d:
        r4.entities = r2;	 Catch:{ Exception -> 0x04a6 }
        goto L_0x0152;
    L_0x0170:
        r11.caption = r2;	 Catch:{ Exception -> 0x04a6 }
        r26.generateCaption();	 Catch:{ Exception -> 0x04a6 }
    L_0x0175:
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x04a6 }
        r2.<init>();	 Catch:{ Exception -> 0x04a6 }
        r2.add(r4);	 Catch:{ Exception -> 0x04a6 }
        r18 = r25.getMessagesStorage();	 Catch:{ Exception -> 0x04a6 }
        r20 = 0;
        r21 = 1;
        r22 = 0;
        r23 = 0;
        r4 = r11.scheduled;	 Catch:{ Exception -> 0x04a6 }
        r19 = r2;
        r24 = r4;
        r18.putMessages(r19, r20, r21, r22, r23, r24);	 Catch:{ Exception -> 0x04a6 }
        r2 = -1;
        r11.type = r2;	 Catch:{ Exception -> 0x04a6 }
        r26.setType();	 Catch:{ Exception -> 0x04a6 }
        r26.createMessageSendInfo();	 Catch:{ Exception -> 0x04a6 }
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x04a6 }
        r2.<init>();	 Catch:{ Exception -> 0x04a6 }
        r2.add(r11);	 Catch:{ Exception -> 0x04a6 }
        r4 = r25.getNotificationCenter();	 Catch:{ Exception -> 0x04a6 }
        r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects;	 Catch:{ Exception -> 0x04a6 }
        r16 = r1;
        r28 = r12;
        r12 = 2;
        r1 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x04a6 }
        r12 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x04a6 }
        r17 = 0;
        r1[r17] = r12;	 Catch:{ Exception -> 0x04a6 }
        r12 = 1;
        r1[r12] = r2;	 Catch:{ Exception -> 0x04a6 }
        r4.postNotificationName(r5, r1);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x01c3;
    L_0x01bf:
        r16 = r1;
        r28 = r12;
    L_0x01c3:
        if (r14 == 0) goto L_0x01d3;
    L_0x01c5:
        r1 = r14.containsKey(r3);	 Catch:{ Exception -> 0x04a6 }
        if (r1 == 0) goto L_0x01d3;
    L_0x01cb:
        r1 = r14.get(r3);	 Catch:{ Exception -> 0x04a6 }
        r1 = (java.lang.String) r1;	 Catch:{ Exception -> 0x04a6 }
        r4 = r1;
        goto L_0x01d4;
    L_0x01d3:
        r4 = 0;
    L_0x01d4:
        r1 = 8;
        r2 = 1;
        if (r13 < r2) goto L_0x01dc;
    L_0x01d9:
        r2 = 3;
        if (r13 <= r2) goto L_0x01e1;
    L_0x01dc:
        r2 = 5;
        if (r13 < r2) goto L_0x04ad;
    L_0x01df:
        if (r13 > r1) goto L_0x04ad;
    L_0x01e1:
        r5 = 2;
        if (r13 != r5) goto L_0x0299;
    L_0x01e4:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x04a6 }
        r5.<init>();	 Catch:{ Exception -> 0x04a6 }
        if (r14 == 0) goto L_0x0224;
    L_0x01eb:
        r12 = "masks";
        r12 = r14.get(r12);	 Catch:{ Exception -> 0x04a6 }
        r12 = (java.lang.String) r12;	 Catch:{ Exception -> 0x04a6 }
        if (r12 == 0) goto L_0x0224;
    L_0x01f5:
        r14 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x04a6 }
        r12 = org.telegram.messenger.Utilities.hexToBytes(r12);	 Catch:{ Exception -> 0x04a6 }
        r14.<init>(r12);	 Catch:{ Exception -> 0x04a6 }
        r12 = 0;
        r1 = r14.readInt32(r12);	 Catch:{ Exception -> 0x04a6 }
        r2 = 0;
    L_0x0204:
        if (r2 >= r1) goto L_0x021b;
    L_0x0206:
        r3 = r5.stickers;	 Catch:{ Exception -> 0x04a6 }
        r28 = r1;
        r1 = r14.readInt32(r12);	 Catch:{ Exception -> 0x04a6 }
        r1 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r14, r1, r12);	 Catch:{ Exception -> 0x04a6 }
        r3.add(r1);	 Catch:{ Exception -> 0x04a6 }
        r2 = r2 + 1;
        r1 = r28;
        r12 = 0;
        goto L_0x0204;
    L_0x021b:
        r1 = r5.flags;	 Catch:{ Exception -> 0x04a6 }
        r2 = 1;
        r1 = r1 | r2;
        r5.flags = r1;	 Catch:{ Exception -> 0x04a6 }
        r14.cleanup();	 Catch:{ Exception -> 0x04a6 }
    L_0x0224:
        r1 = r0.access_hash;	 Catch:{ Exception -> 0x04a6 }
        r18 = 0;
        r3 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1));
        if (r3 != 0) goto L_0x0230;
    L_0x022c:
        r2 = r5;
        r3 = r13;
        r1 = 1;
        goto L_0x025e;
    L_0x0230:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x04a6 }
        r1.<init>();	 Catch:{ Exception -> 0x04a6 }
        r2 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x04a6 }
        r2.<init>();	 Catch:{ Exception -> 0x04a6 }
        r1.id = r2;	 Catch:{ Exception -> 0x04a6 }
        r2 = r1.id;	 Catch:{ Exception -> 0x04a6 }
        r3 = r13;
        r12 = r0.id;	 Catch:{ Exception -> 0x04a6 }
        r2.id = r12;	 Catch:{ Exception -> 0x04a6 }
        r2 = r1.id;	 Catch:{ Exception -> 0x04a6 }
        r12 = r0.access_hash;	 Catch:{ Exception -> 0x04a6 }
        r2.access_hash = r12;	 Catch:{ Exception -> 0x04a6 }
        r2 = r1.id;	 Catch:{ Exception -> 0x04a6 }
        r12 = r0.file_reference;	 Catch:{ Exception -> 0x04a6 }
        r2.file_reference = r12;	 Catch:{ Exception -> 0x04a6 }
        r2 = r1.id;	 Catch:{ Exception -> 0x04a6 }
        r2 = r2.file_reference;	 Catch:{ Exception -> 0x04a6 }
        if (r2 != 0) goto L_0x025c;
    L_0x0255:
        r2 = r1.id;	 Catch:{ Exception -> 0x04a6 }
        r12 = 0;
        r13 = new byte[r12];	 Catch:{ Exception -> 0x04a6 }
        r2.file_reference = r13;	 Catch:{ Exception -> 0x04a6 }
    L_0x025c:
        r2 = r1;
        r1 = 0;
    L_0x025e:
        r12 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x04a6 }
        r12.<init>(r6);	 Catch:{ Exception -> 0x04a6 }
        r13 = 0;
        r12.type = r13;	 Catch:{ Exception -> 0x04a6 }
        r12.obj = r11;	 Catch:{ Exception -> 0x04a6 }
        r12.originalPath = r4;	 Catch:{ Exception -> 0x04a6 }
        r12.parentObject = r8;	 Catch:{ Exception -> 0x04a6 }
        r12.inputUploadMedia = r5;	 Catch:{ Exception -> 0x04a6 }
        r12.performMediaUpload = r1;	 Catch:{ Exception -> 0x04a6 }
        if (r15 == 0) goto L_0x0281;
    L_0x0272:
        r5 = r15.length();	 Catch:{ Exception -> 0x04a6 }
        if (r5 <= 0) goto L_0x0281;
    L_0x0278:
        r5 = r15.startsWith(r9);	 Catch:{ Exception -> 0x04a6 }
        if (r5 == 0) goto L_0x0281;
    L_0x027e:
        r12.httpLocation = r15;	 Catch:{ Exception -> 0x04a6 }
        goto L_0x0295;
    L_0x0281:
        r5 = r0.sizes;	 Catch:{ Exception -> 0x04a6 }
        r9 = r0.sizes;	 Catch:{ Exception -> 0x04a6 }
        r9 = r9.size();	 Catch:{ Exception -> 0x04a6 }
        r13 = 1;
        r9 = r9 - r13;
        r5 = r5.get(r9);	 Catch:{ Exception -> 0x04a6 }
        r5 = (org.telegram.tgnet.TLRPC.PhotoSize) r5;	 Catch:{ Exception -> 0x04a6 }
        r12.photoSize = r5;	 Catch:{ Exception -> 0x04a6 }
        r12.locationParent = r0;	 Catch:{ Exception -> 0x04a6 }
    L_0x0295:
        r5 = r1;
        r9 = r12;
        goto L_0x03a2;
    L_0x0299:
        r3 = r13;
        r0 = 3;
        if (r3 != r0) goto L_0x032e;
    L_0x029d:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x04a6 }
        r0.<init>();	 Catch:{ Exception -> 0x04a6 }
        r1 = r16;
        r2 = r1.mime_type;	 Catch:{ Exception -> 0x04a6 }
        r0.mime_type = r2;	 Catch:{ Exception -> 0x04a6 }
        r2 = r1.attributes;	 Catch:{ Exception -> 0x04a6 }
        r0.attributes = r2;	 Catch:{ Exception -> 0x04a6 }
        r2 = r26.isGif();	 Catch:{ Exception -> 0x04a6 }
        if (r2 != 0) goto L_0x02ca;
    L_0x02b2:
        if (r28 == 0) goto L_0x02bb;
    L_0x02b4:
        r12 = r28;
        r2 = r12.muted;	 Catch:{ Exception -> 0x04a6 }
        if (r2 != 0) goto L_0x02cc;
    L_0x02ba:
        goto L_0x02bd;
    L_0x02bb:
        r12 = r28;
    L_0x02bd:
        r2 = 1;
        r0.nosound_video = r2;	 Catch:{ Exception -> 0x04a6 }
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x04a6 }
        if (r2 == 0) goto L_0x02cc;
    L_0x02c4:
        r2 = "nosound_video = true";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x02cc;
    L_0x02ca:
        r12 = r28;
    L_0x02cc:
        r13 = r1.access_hash;	 Catch:{ Exception -> 0x04a6 }
        r18 = 0;
        r2 = (r13 > r18 ? 1 : (r13 == r18 ? 0 : -1));
        if (r2 != 0) goto L_0x02d7;
    L_0x02d4:
        r2 = r0;
        r5 = 1;
        goto L_0x0303;
    L_0x02d7:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x04a6 }
        r2.<init>();	 Catch:{ Exception -> 0x04a6 }
        r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x04a6 }
        r5.<init>();	 Catch:{ Exception -> 0x04a6 }
        r2.id = r5;	 Catch:{ Exception -> 0x04a6 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r13 = r1.id;	 Catch:{ Exception -> 0x04a6 }
        r5.id = r13;	 Catch:{ Exception -> 0x04a6 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r13 = r1.access_hash;	 Catch:{ Exception -> 0x04a6 }
        r5.access_hash = r13;	 Catch:{ Exception -> 0x04a6 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r9 = r1.file_reference;	 Catch:{ Exception -> 0x04a6 }
        r5.file_reference = r9;	 Catch:{ Exception -> 0x04a6 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r5 = r5.file_reference;	 Catch:{ Exception -> 0x04a6 }
        if (r5 != 0) goto L_0x0302;
    L_0x02fb:
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r9 = 0;
        r13 = new byte[r9];	 Catch:{ Exception -> 0x04a6 }
        r5.file_reference = r13;	 Catch:{ Exception -> 0x04a6 }
    L_0x0302:
        r5 = 0;
    L_0x0303:
        r9 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x04a6 }
        r9.<init>(r6);	 Catch:{ Exception -> 0x04a6 }
        r13 = 1;
        r9.type = r13;	 Catch:{ Exception -> 0x04a6 }
        r9.obj = r11;	 Catch:{ Exception -> 0x04a6 }
        r9.originalPath = r4;	 Catch:{ Exception -> 0x04a6 }
        r9.parentObject = r8;	 Catch:{ Exception -> 0x04a6 }
        r9.inputUploadMedia = r0;	 Catch:{ Exception -> 0x04a6 }
        r9.performMediaUpload = r5;	 Catch:{ Exception -> 0x04a6 }
        r0 = r1.thumbs;	 Catch:{ Exception -> 0x04a6 }
        r0 = r0.isEmpty();	 Catch:{ Exception -> 0x04a6 }
        if (r0 != 0) goto L_0x032a;
    L_0x031d:
        r0 = r1.thumbs;	 Catch:{ Exception -> 0x04a6 }
        r13 = 0;
        r0 = r0.get(r13);	 Catch:{ Exception -> 0x04a6 }
        r0 = (org.telegram.tgnet.TLRPC.PhotoSize) r0;	 Catch:{ Exception -> 0x04a6 }
        r9.photoSize = r0;	 Catch:{ Exception -> 0x04a6 }
        r9.locationParent = r1;	 Catch:{ Exception -> 0x04a6 }
    L_0x032a:
        r9.videoEditedInfo = r12;	 Catch:{ Exception -> 0x04a6 }
        goto L_0x03a2;
    L_0x032e:
        r1 = r16;
        r0 = 7;
        if (r3 != r0) goto L_0x039f;
    L_0x0333:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x04a6 }
        r0.<init>();	 Catch:{ Exception -> 0x04a6 }
        r2 = r1.mime_type;	 Catch:{ Exception -> 0x04a6 }
        r0.mime_type = r2;	 Catch:{ Exception -> 0x04a6 }
        r2 = r1.attributes;	 Catch:{ Exception -> 0x04a6 }
        r0.attributes = r2;	 Catch:{ Exception -> 0x04a6 }
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x04a6 }
        r14 = 0;
        r2 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r2 != 0) goto L_0x034b;
    L_0x0348:
        r2 = r0;
        r5 = 1;
        goto L_0x0377;
    L_0x034b:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x04a6 }
        r2.<init>();	 Catch:{ Exception -> 0x04a6 }
        r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x04a6 }
        r5.<init>();	 Catch:{ Exception -> 0x04a6 }
        r2.id = r5;	 Catch:{ Exception -> 0x04a6 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r12 = r1.id;	 Catch:{ Exception -> 0x04a6 }
        r5.id = r12;	 Catch:{ Exception -> 0x04a6 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x04a6 }
        r5.access_hash = r12;	 Catch:{ Exception -> 0x04a6 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r9 = r1.file_reference;	 Catch:{ Exception -> 0x04a6 }
        r5.file_reference = r9;	 Catch:{ Exception -> 0x04a6 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r5 = r5.file_reference;	 Catch:{ Exception -> 0x04a6 }
        if (r5 != 0) goto L_0x0376;
    L_0x036f:
        r5 = r2.id;	 Catch:{ Exception -> 0x04a6 }
        r9 = 0;
        r12 = new byte[r9];	 Catch:{ Exception -> 0x04a6 }
        r5.file_reference = r12;	 Catch:{ Exception -> 0x04a6 }
    L_0x0376:
        r5 = 0;
    L_0x0377:
        r9 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x04a6 }
        r9.<init>(r6);	 Catch:{ Exception -> 0x04a6 }
        r9.originalPath = r4;	 Catch:{ Exception -> 0x04a6 }
        r12 = 2;
        r9.type = r12;	 Catch:{ Exception -> 0x04a6 }
        r9.obj = r11;	 Catch:{ Exception -> 0x04a6 }
        r12 = r1.thumbs;	 Catch:{ Exception -> 0x04a6 }
        r12 = r12.isEmpty();	 Catch:{ Exception -> 0x04a6 }
        if (r12 != 0) goto L_0x0398;
    L_0x038b:
        r12 = r1.thumbs;	 Catch:{ Exception -> 0x04a6 }
        r13 = 0;
        r12 = r12.get(r13);	 Catch:{ Exception -> 0x04a6 }
        r12 = (org.telegram.tgnet.TLRPC.PhotoSize) r12;	 Catch:{ Exception -> 0x04a6 }
        r9.photoSize = r12;	 Catch:{ Exception -> 0x04a6 }
        r9.locationParent = r1;	 Catch:{ Exception -> 0x04a6 }
    L_0x0398:
        r9.parentObject = r8;	 Catch:{ Exception -> 0x04a6 }
        r9.inputUploadMedia = r0;	 Catch:{ Exception -> 0x04a6 }
        r9.performMediaUpload = r5;	 Catch:{ Exception -> 0x04a6 }
        goto L_0x03a2;
    L_0x039f:
        r2 = 0;
        r5 = 0;
        r9 = 0;
    L_0x03a2:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage;	 Catch:{ Exception -> 0x04a6 }
        r0.<init>();	 Catch:{ Exception -> 0x04a6 }
        r1 = r26.getId();	 Catch:{ Exception -> 0x04a6 }
        r0.id = r1;	 Catch:{ Exception -> 0x04a6 }
        r1 = r25.getMessagesController();	 Catch:{ Exception -> 0x04a6 }
        r7 = (int) r6;	 Catch:{ Exception -> 0x04a6 }
        r1 = r1.getInputPeer(r7);	 Catch:{ Exception -> 0x04a6 }
        r0.peer = r1;	 Catch:{ Exception -> 0x04a6 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04a6 }
        r1 = r1 | 16384;
        r0.flags = r1;	 Catch:{ Exception -> 0x04a6 }
        r0.media = r2;	 Catch:{ Exception -> 0x04a6 }
        r1 = r11.scheduled;	 Catch:{ Exception -> 0x04a6 }
        if (r1 == 0) goto L_0x03d2;
    L_0x03c4:
        r1 = r11.messageOwner;	 Catch:{ Exception -> 0x04a6 }
        r1 = r1.date;	 Catch:{ Exception -> 0x04a6 }
        r0.schedule_date = r1;	 Catch:{ Exception -> 0x04a6 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04a6 }
        r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x04a6 }
    L_0x03d2:
        r1 = r11.editingMessage;	 Catch:{ Exception -> 0x04a6 }
        if (r1 == 0) goto L_0x041b;
    L_0x03d6:
        r1 = r11.editingMessage;	 Catch:{ Exception -> 0x04a6 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x04a6 }
        r0.message = r1;	 Catch:{ Exception -> 0x04a6 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04a6 }
        r1 = r1 | 2048;
        r0.flags = r1;	 Catch:{ Exception -> 0x04a6 }
        r1 = r11.editingMessageEntities;	 Catch:{ Exception -> 0x04a6 }
        if (r1 == 0) goto L_0x03f5;
    L_0x03e8:
        r1 = r11.editingMessageEntities;	 Catch:{ Exception -> 0x04a6 }
        r0.entities = r1;	 Catch:{ Exception -> 0x04a6 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04a6 }
        r2 = 8;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x04a6 }
    L_0x03f3:
        r1 = 0;
        goto L_0x0417;
    L_0x03f5:
        r1 = 1;
        r2 = new java.lang.CharSequence[r1];	 Catch:{ Exception -> 0x04a6 }
        r1 = r11.editingMessage;	 Catch:{ Exception -> 0x04a6 }
        r6 = 0;
        r2[r6] = r1;	 Catch:{ Exception -> 0x04a6 }
        r1 = r25.getMediaDataController();	 Catch:{ Exception -> 0x04a6 }
        r1 = r1.getEntities(r2);	 Catch:{ Exception -> 0x04a6 }
        if (r1 == 0) goto L_0x03f3;
    L_0x0407:
        r2 = r1.isEmpty();	 Catch:{ Exception -> 0x04a6 }
        if (r2 != 0) goto L_0x03f3;
    L_0x040d:
        r0.entities = r1;	 Catch:{ Exception -> 0x04a6 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04a6 }
        r2 = 8;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x04a6 }
        goto L_0x03f3;
    L_0x0417:
        r11.editingMessage = r1;	 Catch:{ Exception -> 0x04a6 }
        r11.editingMessageEntities = r1;	 Catch:{ Exception -> 0x04a6 }
    L_0x041b:
        if (r9 == 0) goto L_0x041f;
    L_0x041d:
        r9.sendRequest = r0;	 Catch:{ Exception -> 0x04a6 }
    L_0x041f:
        r1 = 1;
        if (r3 != r1) goto L_0x0432;
    L_0x0422:
        r4 = 0;
        r7 = r11.scheduled;	 Catch:{ Exception -> 0x04a6 }
        r1 = r25;
        r2 = r0;
        r3 = r26;
        r5 = r9;
        r6 = r33;
        r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x0432:
        r1 = 2;
        if (r3 != r1) goto L_0x044e;
    L_0x0435:
        if (r5 == 0) goto L_0x043c;
    L_0x0437:
        r10.performSendDelayedMessage(r9);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x043c:
        r5 = 0;
        r6 = 1;
        r12 = r11.scheduled;	 Catch:{ Exception -> 0x04a6 }
        r1 = r25;
        r2 = r0;
        r3 = r26;
        r7 = r9;
        r8 = r33;
        r9 = r12;
        r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x044e:
        r1 = 3;
        if (r3 != r1) goto L_0x0466;
    L_0x0451:
        if (r5 == 0) goto L_0x0458;
    L_0x0453:
        r10.performSendDelayedMessage(r9);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x0458:
        r7 = r11.scheduled;	 Catch:{ Exception -> 0x04a6 }
        r1 = r25;
        r2 = r0;
        r3 = r26;
        r5 = r9;
        r6 = r33;
        r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x0466:
        r1 = 6;
        if (r3 != r1) goto L_0x0477;
    L_0x0469:
        r7 = r11.scheduled;	 Catch:{ Exception -> 0x04a6 }
        r1 = r25;
        r2 = r0;
        r3 = r26;
        r5 = r9;
        r6 = r33;
        r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x0477:
        r1 = 7;
        if (r3 != r1) goto L_0x048e;
    L_0x047a:
        if (r5 == 0) goto L_0x0480;
    L_0x047c:
        r10.performSendDelayedMessage(r9);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x0480:
        r7 = r11.scheduled;	 Catch:{ Exception -> 0x04a6 }
        r1 = r25;
        r2 = r0;
        r3 = r26;
        r5 = r9;
        r6 = r33;
        r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x048e:
        r1 = 8;
        if (r3 != r1) goto L_0x04ad;
    L_0x0492:
        if (r5 == 0) goto L_0x0498;
    L_0x0494:
        r10.performSendDelayedMessage(r9);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x0498:
        r7 = r11.scheduled;	 Catch:{ Exception -> 0x04a6 }
        r1 = r25;
        r2 = r0;
        r3 = r26;
        r5 = r9;
        r6 = r33;
        r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x04a6 }
        goto L_0x04ad;
    L_0x04a6:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r25.revertEditingMessageObject(r26);
    L_0x04ad:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessageMedia(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, BaseFragment baseFragment, ArrayList<MessageEntity> arrayList, int i, Runnable runnable) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return 0;
        }
        TL_messages_editMessage tL_messages_editMessage = new TL_messages_editMessage();
        tL_messages_editMessage.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        if (str != null) {
            tL_messages_editMessage.message = str;
            tL_messages_editMessage.flags |= 2048;
            tL_messages_editMessage.no_webpage = z ^ 1;
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
        return getConnectionsManager().sendRequest(tL_messages_editMessage, new -$$Lambda$SendMessagesHelper$0k3RdsQSyxpPqVyuBg3ZosCuce8(this, baseFragment, tL_messages_editMessage, runnable));
    }

    public /* synthetic */ void lambda$editMessage$11$SendMessagesHelper(BaseFragment baseFragment, TL_messages_editMessage tL_messages_editMessage, Runnable runnable, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((Updates) tLObject, false);
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$ag0T9ipOWU0deF9XfslwNyap3gA(this, tL_error, baseFragment, tL_messages_editMessage));
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public /* synthetic */ void lambda$null$10$SendMessagesHelper(TL_error tL_error, BaseFragment baseFragment, TL_messages_editMessage tL_messages_editMessage) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_editMessage, new Object[0]);
    }

    private void sendLocation(Location location) {
        MessageMedia tL_messageMediaGeo = new TL_messageMediaGeo();
        tL_messageMediaGeo.geo = new TL_geoPoint();
        tL_messageMediaGeo.geo.lat = AndroidUtilities.fixLocationCoord(location.getLatitude());
        tL_messageMediaGeo.geo._long = AndroidUtilities.fixLocationCoord(location.getLongitude());
        for (Entry value : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) value.getValue();
            sendMessage(tL_messageMediaGeo, messageObject.getDialogId(), messageObject, null, null, true, 0);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, KeyboardButton keyboardButton) {
        if (messageObject != null && keyboardButton != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(messageObject.getDialogId());
            String str = "_";
            stringBuilder.append(str);
            stringBuilder.append(messageObject.getId());
            stringBuilder.append(str);
            stringBuilder.append(Utilities.bytesToHex(keyboardButton.data));
            stringBuilder.append(str);
            stringBuilder.append(keyboardButton instanceof TL_keyboardButtonGame ? "1" : "0");
            this.waitingForLocation.put(stringBuilder.toString(), messageObject);
            this.locationProvider.start();
        }
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, KeyboardButton keyboardButton) {
        if (messageObject == null || keyboardButton == null) {
            return false;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageObject.getDialogId());
        String str = "_";
        stringBuilder.append(str);
        stringBuilder.append(messageObject.getId());
        stringBuilder.append(str);
        stringBuilder.append(Utilities.bytesToHex(keyboardButton.data));
        stringBuilder.append(str);
        stringBuilder.append(keyboardButton instanceof TL_keyboardButtonGame ? "1" : "0");
        return this.waitingForLocation.containsKey(stringBuilder.toString());
    }

    public void sendNotificationCallback(long j, int i, byte[] bArr) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$HbRNU4Jc_Y0XpAaKW1pVIkZttzI(this, j, i, bArr));
    }

    public /* synthetic */ void lambda$sendNotificationCallback$14$SendMessagesHelper(long j, int i, byte[] bArr) {
        int i2 = i;
        byte[] bArr2 = bArr;
        int i3 = (int) j;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(j);
        String str = "_";
        stringBuilder.append(str);
        stringBuilder.append(i);
        stringBuilder.append(str);
        stringBuilder.append(Utilities.bytesToHex(bArr));
        stringBuilder.append(str);
        stringBuilder.append(0);
        String stringBuilder2 = stringBuilder.toString();
        this.waitingForCallback.put(stringBuilder2, Boolean.valueOf(true));
        if (i3 <= 0) {
            int i4 = -i3;
            if (getMessagesController().getChat(Integer.valueOf(i4)) == null) {
                Chat chatSync = getMessagesStorage().getChatSync(i4);
                if (chatSync != null) {
                    getMessagesController().putChat(chatSync, true);
                }
            }
        } else if (getMessagesController().getUser(Integer.valueOf(i3)) == null) {
            User userSync = getMessagesStorage().getUserSync(i3);
            if (userSync != null) {
                getMessagesController().putUser(userSync, true);
            }
        }
        TL_messages_getBotCallbackAnswer tL_messages_getBotCallbackAnswer = new TL_messages_getBotCallbackAnswer();
        tL_messages_getBotCallbackAnswer.peer = getMessagesController().getInputPeer(i3);
        tL_messages_getBotCallbackAnswer.msg_id = i2;
        tL_messages_getBotCallbackAnswer.game = false;
        if (bArr2 != null) {
            tL_messages_getBotCallbackAnswer.flags |= 1;
            tL_messages_getBotCallbackAnswer.data = bArr2;
        }
        getConnectionsManager().sendRequest(tL_messages_getBotCallbackAnswer, new -$$Lambda$SendMessagesHelper$t77TIdvR5oVS88nA5fB-WrU7owc(this, stringBuilder2), 2);
        getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, true, 0);
    }

    public /* synthetic */ void lambda$null$12$SendMessagesHelper(String str) {
        Boolean bool = (Boolean) this.waitingForCallback.remove(str);
    }

    public /* synthetic */ void lambda$null$13$SendMessagesHelper(String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$Vs48x-dR1zvnzDoILUm1BxyX1bc(this, str));
    }

    public byte[] isSendingVote(MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("poll_");
        stringBuilder.append(messageObject.getPollId());
        return (byte[]) this.waitingForVote.get(stringBuilder.toString());
    }

    public int sendVote(MessageObject messageObject, TL_pollAnswer tL_pollAnswer, Runnable runnable) {
        if (messageObject == null) {
            return 0;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("poll_");
        stringBuilder.append(messageObject.getPollId());
        String stringBuilder2 = stringBuilder.toString();
        if (this.waitingForCallback.containsKey(stringBuilder2)) {
            return 0;
        }
        this.waitingForVote.put(stringBuilder2, tL_pollAnswer != null ? tL_pollAnswer.option : new byte[0]);
        TL_messages_sendVote tL_messages_sendVote = new TL_messages_sendVote();
        tL_messages_sendVote.msg_id = messageObject.getId();
        tL_messages_sendVote.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        if (tL_pollAnswer != null) {
            tL_messages_sendVote.options.add(tL_pollAnswer.option);
        }
        return getConnectionsManager().sendRequest(tL_messages_sendVote, new -$$Lambda$SendMessagesHelper$uhLM6ViQVMMdzN56xkU0rITU9CY(this, messageObject, stringBuilder2, runnable));
    }

    public /* synthetic */ void lambda$sendVote$15$SendMessagesHelper(MessageObject messageObject, final String str, final Runnable runnable, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(0));
            getMessagesController().processUpdates((Updates) tLObject, false);
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(SystemClock.uptimeMillis()));
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SendMessagesHelper.this.waitingForVote.remove(str);
                Runnable runnable = runnable;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public long getVoteSendTime(long j) {
        return ((Long) this.voteSendTime.get(j, Long.valueOf(0))).longValue();
    }

    public void sendReaction(MessageObject messageObject, CharSequence charSequence, ChatActivity chatActivity) {
        if (messageObject != null && chatActivity != null) {
            TL_messages_sendReaction tL_messages_sendReaction = new TL_messages_sendReaction();
            tL_messages_sendReaction.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
            tL_messages_sendReaction.msg_id = messageObject.getId();
            if (charSequence != null) {
                tL_messages_sendReaction.reaction = charSequence.toString();
                tL_messages_sendReaction.flags |= 1;
            }
            getConnectionsManager().sendRequest(tL_messages_sendReaction, new -$$Lambda$SendMessagesHelper$vhEb-4dsqJOW_hry0Z7bhJ2eXeg(this));
        }
    }

    public /* synthetic */ void lambda$sendReaction$16$SendMessagesHelper(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((Updates) tLObject, false);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0079  */
    public void sendCallback(boolean r18, org.telegram.messenger.MessageObject r19, org.telegram.tgnet.TLRPC.KeyboardButton r20, org.telegram.ui.ChatActivity r21) {
        /*
        r17 = this;
        r8 = r19;
        r9 = r20;
        if (r8 == 0) goto L_0x0113;
    L_0x0006:
        if (r9 == 0) goto L_0x0113;
    L_0x0008:
        if (r21 != 0) goto L_0x000c;
    L_0x000a:
        goto L_0x0113;
    L_0x000c:
        r10 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth;
        r11 = 1;
        r12 = 0;
        r13 = 2;
        if (r10 == 0) goto L_0x0016;
    L_0x0013:
        r0 = 3;
    L_0x0014:
        r14 = 0;
        goto L_0x0027;
    L_0x0016:
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
        if (r0 == 0) goto L_0x001c;
    L_0x001a:
        r0 = 1;
        goto L_0x0014;
    L_0x001c:
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r0 == 0) goto L_0x0024;
    L_0x0020:
        r14 = r18;
        r0 = 2;
        goto L_0x0027;
    L_0x0024:
        r14 = r18;
        r0 = 0;
    L_0x0027:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = r19.getDialogId();
        r1.append(r2);
        r2 = "_";
        r1.append(r2);
        r3 = r19.getId();
        r1.append(r3);
        r1.append(r2);
        r3 = r9.data;
        r3 = org.telegram.messenger.Utilities.bytesToHex(r3);
        r1.append(r3);
        r1.append(r2);
        r1.append(r0);
        r15 = r1.toString();
        r7 = r17;
        r0 = r7.waitingForCallback;
        r1 = java.lang.Boolean.valueOf(r11);
        r0.put(r15, r1);
        r6 = new org.telegram.tgnet.TLObject[r11];
        r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$iPASdTg44HF0dJIxo_Ief9xbqUk;
        r0 = r5;
        r1 = r17;
        r2 = r15;
        r3 = r14;
        r4 = r19;
        r11 = r5;
        r5 = r20;
        r16 = r6;
        r6 = r21;
        r7 = r16;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        if (r14 == 0) goto L_0x0082;
    L_0x0079:
        r0 = r17.getMessagesStorage();
        r0.getBotCache(r15, r11);
        goto L_0x0113;
    L_0x0082:
        if (r10 == 0) goto L_0x00ac;
    L_0x0084:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth;
        r0.<init>();
        r1 = r17.getMessagesController();
        r2 = r19.getDialogId();
        r3 = (int) r2;
        r1 = r1.getInputPeer(r3);
        r0.peer = r1;
        r1 = r19.getId();
        r0.msg_id = r1;
        r1 = r9.button_id;
        r0.button_id = r1;
        r16[r12] = r0;
        r1 = r17.getConnectionsManager();
        r1.sendRequest(r0, r11, r13);
        goto L_0x0113;
    L_0x00ac:
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r0 == 0) goto L_0x00e2;
    L_0x00b0:
        r0 = r8.messageOwner;
        r0 = r0.media;
        r0 = r0.flags;
        r0 = r0 & 4;
        if (r0 != 0) goto L_0x00cd;
    L_0x00ba:
        r0 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm;
        r0.<init>();
        r1 = r19.getId();
        r0.msg_id = r1;
        r1 = r17.getConnectionsManager();
        r1.sendRequest(r0, r11, r13);
        goto L_0x0113;
    L_0x00cd:
        r0 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentReceipt;
        r0.<init>();
        r1 = r8.messageOwner;
        r1 = r1.media;
        r1 = r1.receipt_msg_id;
        r0.msg_id = r1;
        r1 = r17.getConnectionsManager();
        r1.sendRequest(r0, r11, r13);
        goto L_0x0113;
    L_0x00e2:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer;
        r0.<init>();
        r1 = r17.getMessagesController();
        r2 = r19.getDialogId();
        r3 = (int) r2;
        r1 = r1.getInputPeer(r3);
        r0.peer = r1;
        r1 = r19.getId();
        r0.msg_id = r1;
        r1 = r9 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
        r0.game = r1;
        r1 = r9.data;
        if (r1 == 0) goto L_0x010c;
    L_0x0104:
        r2 = r0.flags;
        r3 = 1;
        r2 = r2 | r3;
        r0.flags = r2;
        r0.data = r1;
    L_0x010c:
        r1 = r17.getConnectionsManager();
        r1.sendRequest(r0, r11, r13);
    L_0x0113:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendCallback(boolean, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity):void");
    }

    public /* synthetic */ void lambda$sendCallback$18$SendMessagesHelper(String str, boolean z, MessageObject messageObject, KeyboardButton keyboardButton, ChatActivity chatActivity, TLObject[] tLObjectArr, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$wWfMwlcbIwBvJ9KNPDO6RIDrxDo(this, str, z, tLObject, messageObject, keyboardButton, chatActivity, tLObjectArr));
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00cc  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00f9  */
    /* JADX WARNING: Missing block: B:76:0x015b, code skipped:
            if (r1.getBoolean(r2.toString(), true) != false) goto L_0x015f;
     */
    public /* synthetic */ void lambda$null$17$SendMessagesHelper(java.lang.String r12, boolean r13, org.telegram.tgnet.TLObject r14, org.telegram.messenger.MessageObject r15, org.telegram.tgnet.TLRPC.KeyboardButton r16, org.telegram.ui.ChatActivity r17, org.telegram.tgnet.TLObject[] r18) {
        /*
        r11 = this;
        r0 = r11;
        r1 = r12;
        r2 = r14;
        r3 = r15;
        r4 = r16;
        r5 = r17;
        r6 = r0.waitingForCallback;
        r6.remove(r12);
        r6 = 0;
        if (r13 == 0) goto L_0x0017;
    L_0x0010:
        if (r2 != 0) goto L_0x0017;
    L_0x0012:
        r11.sendCallback(r6, r15, r4, r5);
        goto L_0x016f;
    L_0x0017:
        if (r2 == 0) goto L_0x016f;
    L_0x0019:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth;
        r8 = 1;
        if (r7 == 0) goto L_0x004c;
    L_0x001e:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_urlAuthResultRequest;
        if (r1 == 0) goto L_0x0030;
    L_0x0022:
        r1 = r2;
        r1 = (org.telegram.tgnet.TLRPC.TL_urlAuthResultRequest) r1;
        r2 = r18[r6];
        r2 = (org.telegram.tgnet.TLRPC.TL_messages_requestUrlAuth) r2;
        r3 = r4.url;
        r5.showRequestUrlAlert(r1, r2, r3);
        goto L_0x016f;
    L_0x0030:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_urlAuthResultAccepted;
        if (r1 == 0) goto L_0x003e;
    L_0x0034:
        r1 = r2;
        r1 = (org.telegram.tgnet.TLRPC.TL_urlAuthResultAccepted) r1;
        r1 = r1.url;
        r5.showOpenUrlAlert(r1, r6);
        goto L_0x016f;
    L_0x003e:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_urlAuthResultDefault;
        if (r1 == 0) goto L_0x016f;
    L_0x0042:
        r1 = r2;
        r1 = (org.telegram.tgnet.TLRPC.TL_urlAuthResultDefault) r1;
        r1 = r4.url;
        r5.showOpenUrlAlert(r1, r8);
        goto L_0x016f;
    L_0x004c:
        r7 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r7 == 0) goto L_0x007a;
    L_0x0050:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_paymentForm;
        if (r1 == 0) goto L_0x006a;
    L_0x0054:
        r1 = r2;
        r1 = (org.telegram.tgnet.TLRPC.TL_payments_paymentForm) r1;
        r2 = r11.getMessagesController();
        r4 = r1.users;
        r2.putUsers(r4, r6);
        r2 = new org.telegram.ui.PaymentFormActivity;
        r2.<init>(r1, r15);
        r5.presentFragment(r2);
        goto L_0x016f;
    L_0x006a:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt;
        if (r1 == 0) goto L_0x016f;
    L_0x006e:
        r1 = new org.telegram.ui.PaymentFormActivity;
        r2 = (org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt) r2;
        r1.<init>(r15, r2);
        r5.presentFragment(r1);
        goto L_0x016f;
    L_0x007a:
        r2 = (org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer) r2;
        if (r13 != 0) goto L_0x0089;
    L_0x007e:
        r7 = r2.cache_time;
        if (r7 == 0) goto L_0x0089;
    L_0x0082:
        r7 = r11.getMessagesStorage();
        r7.saveBotCache(r12, r2);
    L_0x0089:
        r1 = r2.message;
        r7 = 0;
        if (r1 == 0) goto L_0x0100;
    L_0x008e:
        r1 = r3.messageOwner;
        r3 = r1.from_id;
        r1 = r1.via_bot_id;
        if (r1 == 0) goto L_0x0097;
    L_0x0096:
        goto L_0x0098;
    L_0x0097:
        r1 = r3;
    L_0x0098:
        if (r1 <= 0) goto L_0x00b1;
    L_0x009a:
        r3 = r11.getMessagesController();
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r3.getUser(r1);
        if (r1 == 0) goto L_0x00c3;
    L_0x00a8:
        r3 = r1.first_name;
        r1 = r1.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r3, r1);
        goto L_0x00c4;
    L_0x00b1:
        r3 = r11.getMessagesController();
        r1 = -r1;
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r3.getChat(r1);
        if (r1 == 0) goto L_0x00c3;
    L_0x00c0:
        r1 = r1.title;
        goto L_0x00c4;
    L_0x00c3:
        r1 = r7;
    L_0x00c4:
        if (r1 != 0) goto L_0x00c8;
    L_0x00c6:
        r1 = "bot";
    L_0x00c8:
        r3 = r2.alert;
        if (r3 == 0) goto L_0x00f9;
    L_0x00cc:
        r3 = r17.getParentActivity();
        if (r3 != 0) goto L_0x00d3;
    L_0x00d2:
        return;
    L_0x00d3:
        r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r4 = r17.getParentActivity();
        r3.<init>(r4);
        r3.setTitle(r1);
        r1 = NUM; // 0x7f0e070e float:1.88787E38 double:1.053163049E-314;
        r4 = "OK";
        r1 = org.telegram.messenger.LocaleController.getString(r4, r1);
        r3.setPositiveButton(r1, r7);
        r1 = r2.message;
        r3.setMessage(r1);
        r1 = r3.create();
        r5.showDialog(r1);
        goto L_0x016f;
    L_0x00f9:
        r2 = r2.message;
        r5.showAlert(r1, r2);
        goto L_0x016f;
    L_0x0100:
        r1 = r2.url;
        if (r1 == 0) goto L_0x016f;
    L_0x0104:
        r1 = r17.getParentActivity();
        if (r1 != 0) goto L_0x010b;
    L_0x010a:
        return;
    L_0x010b:
        r1 = r3.messageOwner;
        r9 = r1.from_id;
        r1 = r1.via_bot_id;
        if (r1 == 0) goto L_0x0114;
    L_0x0113:
        r9 = r1;
    L_0x0114:
        r1 = r11.getMessagesController();
        r10 = java.lang.Integer.valueOf(r9);
        r1 = r1.getUser(r10);
        if (r1 == 0) goto L_0x0128;
    L_0x0122:
        r1 = r1.verified;
        if (r1 == 0) goto L_0x0128;
    L_0x0126:
        r1 = 1;
        goto L_0x0129;
    L_0x0128:
        r1 = 0;
    L_0x0129:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
        if (r4 == 0) goto L_0x016a;
    L_0x012d:
        r4 = r3.messageOwner;
        r4 = r4.media;
        r10 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r10 == 0) goto L_0x0138;
    L_0x0135:
        r4 = r4.game;
        goto L_0x0139;
    L_0x0138:
        r4 = r7;
    L_0x0139:
        if (r4 != 0) goto L_0x013c;
    L_0x013b:
        return;
    L_0x013c:
        r7 = r2.url;
        if (r1 != 0) goto L_0x015e;
    L_0x0140:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getNotificationsSettings(r1);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r10 = "askgame_";
        r2.append(r10);
        r2.append(r9);
        r2 = r2.toString();
        r1 = r1.getBoolean(r2, r8);
        if (r1 == 0) goto L_0x015e;
    L_0x015d:
        goto L_0x015f;
    L_0x015e:
        r8 = 0;
    L_0x015f:
        r1 = r17;
        r2 = r4;
        r3 = r15;
        r4 = r7;
        r5 = r8;
        r6 = r9;
        r1.showOpenGameAlert(r2, r3, r4, r5, r6);
        goto L_0x016f;
    L_0x016a:
        r1 = r2.url;
        r5.showOpenUrlAlert(r1, r6);
    L_0x016f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$17$SendMessagesHelper(java.lang.String, boolean, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity, org.telegram.tgnet.TLObject[]):void");
    }

    public boolean isSendingCallback(MessageObject messageObject, KeyboardButton keyboardButton) {
        int i = 0;
        if (messageObject == null || keyboardButton == null) {
            return false;
        }
        if (keyboardButton instanceof TL_keyboardButtonUrlAuth) {
            i = 3;
        } else if (keyboardButton instanceof TL_keyboardButtonGame) {
            i = 1;
        } else if (keyboardButton instanceof TL_keyboardButtonBuy) {
            i = 2;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(messageObject.getDialogId());
        String str = "_";
        stringBuilder.append(str);
        stringBuilder.append(messageObject.getId());
        stringBuilder.append(str);
        stringBuilder.append(Utilities.bytesToHex(keyboardButton.data));
        stringBuilder.append(str);
        stringBuilder.append(i);
        return this.waitingForCallback.containsKey(stringBuilder.toString());
    }

    public void sendGame(InputPeer inputPeer, TL_inputMediaGame tL_inputMediaGame, long j, long j2) {
        Throwable e;
        if (inputPeer != null && tL_inputMediaGame != null) {
            long j3;
            TL_messages_sendMedia tL_messages_sendMedia = new TL_messages_sendMedia();
            tL_messages_sendMedia.peer = inputPeer;
            InputPeer inputPeer2 = tL_messages_sendMedia.peer;
            String str = "silent_";
            SharedPreferences notificationsSettings;
            StringBuilder stringBuilder;
            if (inputPeer2 instanceof TL_inputPeerChannel) {
                notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(-inputPeer.channel_id);
                tL_messages_sendMedia.silent = notificationsSettings.getBoolean(stringBuilder.toString(), false);
            } else if (inputPeer2 instanceof TL_inputPeerChat) {
                notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(-inputPeer.chat_id);
                tL_messages_sendMedia.silent = notificationsSettings.getBoolean(stringBuilder.toString(), false);
            } else {
                notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(inputPeer.user_id);
                tL_messages_sendMedia.silent = notificationsSettings.getBoolean(stringBuilder.toString(), false);
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
                NativeByteBuffer nativeByteBuffer;
                try {
                    nativeByteBuffer = new NativeByteBuffer(((inputPeer.getObjectSize() + tL_inputMediaGame.getObjectSize()) + 4) + 8);
                    try {
                        nativeByteBuffer.writeInt32(3);
                        nativeByteBuffer.writeInt64(j);
                        inputPeer.serializeToStream(nativeByteBuffer);
                        tL_inputMediaGame.serializeToStream(nativeByteBuffer);
                    } catch (Exception e2) {
                        e = e2;
                    }
                } catch (Exception e3) {
                    e = e3;
                    nativeByteBuffer = null;
                    FileLog.e(e);
                    j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
                    getConnectionsManager().sendRequest(tL_messages_sendMedia, new -$$Lambda$SendMessagesHelper$6dX6A5oqaB-enzNBh0U8LkOvNHc(this, j2));
                }
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tL_messages_sendMedia, new -$$Lambda$SendMessagesHelper$6dX6A5oqaB-enzNBh0U8LkOvNHc(this, j2));
        }
    }

    public /* synthetic */ void lambda$sendGame$19$SendMessagesHelper(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void sendMessage(MessageObject messageObject) {
        MessageObject messageObject2 = messageObject;
        long dialogId = messageObject.getDialogId();
        Message message = messageObject2.messageOwner;
        String str = message.attachPath;
        ReplyMarkup replyMarkup = message.reply_markup;
        HashMap hashMap = message.params;
        int i = message.silent ^ 1;
        int i2 = messageObject2.scheduled ? message.date : 0;
        String str2 = str;
        sendMessage(null, null, null, null, null, null, null, null, null, dialogId, str2, null, null, true, messageObject, null, replyMarkup, hashMap, i, i2, 0, null);
    }

    public void sendMessage(User user, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage(null, null, null, null, null, user, null, null, null, j, null, messageObject, null, true, null, null, replyMarkup, hashMap, z, i, 0, null);
    }

    public void sendMessage(TL_document tL_document, VideoEditedInfo videoEditedInfo, String str, long j, MessageObject messageObject, String str2, ArrayList<MessageEntity> arrayList, ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        String str3 = str2;
        sendMessage(null, str3, null, null, videoEditedInfo, null, tL_document, null, null, j, str, messageObject, null, true, null, arrayList, replyMarkup, hashMap, z, i, i2, obj);
    }

    public void sendMessage(String str, long j, MessageObject messageObject, WebPage webPage, boolean z, ArrayList<MessageEntity> arrayList, ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z2, int i) {
        sendMessage(str, null, null, null, null, null, null, null, null, j, null, messageObject, webPage, z, null, arrayList, replyMarkup, hashMap, z2, i, 0, null);
    }

    public void sendMessage(MessageMedia messageMedia, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage(null, null, messageMedia, null, null, null, null, null, null, j, null, messageObject, null, true, null, null, replyMarkup, hashMap, z, i, 0, null);
    }

    public void sendMessage(TL_messageMediaPoll tL_messageMediaPoll, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage(null, null, null, null, null, null, null, null, tL_messageMediaPoll, j, null, messageObject, null, true, null, null, replyMarkup, hashMap, z, i, 0, null);
    }

    public void sendMessage(TL_game tL_game, long j, ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage(null, null, null, null, null, null, null, tL_game, null, j, null, null, null, true, null, null, replyMarkup, hashMap, z, i, 0, null);
    }

    public void sendMessage(TL_photo tL_photo, String str, long j, MessageObject messageObject, String str2, ArrayList<MessageEntity> arrayList, ReplyMarkup replyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        String str3 = str2;
        sendMessage(null, str3, null, tL_photo, null, null, null, null, null, j, str, messageObject, null, true, null, arrayList, replyMarkup, hashMap, z, i, i2, obj);
    }

    /* JADX WARNING: Removed duplicated region for block: B:104:0x0240  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x022a A:{Catch:{ Exception -> 0x0258 }} */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0566 A:{SYNTHETIC, Splitter:B:302:0x0566} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x057b A:{SYNTHETIC, Splitter:B:309:0x057b} */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0578 A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0585 A:{SYNTHETIC, Splitter:B:317:0x0585} */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0566 A:{SYNTHETIC, Splitter:B:302:0x0566} */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0578 A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x057b A:{SYNTHETIC, Splitter:B:309:0x057b} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0585 A:{SYNTHETIC, Splitter:B:317:0x0585} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0598 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0498 A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x04c4 A:{Catch:{ Exception -> 0x055c }} */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0498 A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x04ab A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x04c4 A:{Catch:{ Exception -> 0x055c }} */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0566 A:{SYNTHETIC, Splitter:B:302:0x0566} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x057b A:{SYNTHETIC, Splitter:B:309:0x057b} */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0578 A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0585 A:{SYNTHETIC, Splitter:B:317:0x0585} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0598 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0566 A:{SYNTHETIC, Splitter:B:302:0x0566} */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0578 A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x057b A:{SYNTHETIC, Splitter:B:309:0x057b} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0585 A:{SYNTHETIC, Splitter:B:317:0x0585} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0598 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0566 A:{SYNTHETIC, Splitter:B:302:0x0566} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x057b A:{SYNTHETIC, Splitter:B:309:0x057b} */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0578 A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0585 A:{SYNTHETIC, Splitter:B:317:0x0585} */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0598 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:1125:0x15ef A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1129:0x1616 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x1608 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1138:0x1645 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1137:0x1643 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1162:0x16ae A:{Catch:{ Exception -> 0x16f9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1165:0x16d4 A:{Catch:{ Exception -> 0x16f9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1169:0x16e5 A:{Catch:{ Exception -> 0x16f9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1168:0x16e3 A:{Catch:{ Exception -> 0x16f9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:779:0x0db6 A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:781:0x0dc2 A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:799:0x0e6a A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:806:0x0e8f A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:808:0x0e99 A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:789:0x0e2c A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x0d70 A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:812:0x0ea4 A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x0e9f A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x0d70 A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:789:0x0e2c A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x0e9f A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:812:0x0ea4 A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:789:0x0e2c A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:766:0x0d70 A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:812:0x0ea4 A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x0e9f A:{Catch:{ Exception -> 0x0var_ }} */
    /* JADX WARNING: Removed duplicated region for block: B:1077:0x1508 A:{SYNTHETIC, Splitter:B:1077:0x1508} */
    /* JADX WARNING: Removed duplicated region for block: B:1097:0x1553 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1081:0x150f A:{SYNTHETIC, Splitter:B:1081:0x150f} */
    /* JADX WARNING: Removed duplicated region for block: B:1100:0x1559 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1081:0x150f A:{SYNTHETIC, Splitter:B:1081:0x150f} */
    /* JADX WARNING: Removed duplicated region for block: B:1097:0x1553 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1100:0x1559 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:941:0x1104 A:{Catch:{ Exception -> 0x0fc6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:947:0x1136 A:{Catch:{ Exception -> 0x0fc6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:965:0x11ae A:{Catch:{ Exception -> 0x11c8 }} */
    /* JADX WARNING: Removed duplicated region for block: B:952:0x1178 A:{Catch:{ Exception -> 0x0fc6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:968:0x11bf A:{Catch:{ Exception -> 0x11c8 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1097:0x1553 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1081:0x150f A:{SYNTHETIC, Splitter:B:1081:0x150f} */
    /* JADX WARNING: Removed duplicated region for block: B:1100:0x1559 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1081:0x150f A:{SYNTHETIC, Splitter:B:1081:0x150f} */
    /* JADX WARNING: Removed duplicated region for block: B:1097:0x1553 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1100:0x1559 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1097:0x1553 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1081:0x150f A:{SYNTHETIC, Splitter:B:1081:0x150f} */
    /* JADX WARNING: Removed duplicated region for block: B:1100:0x1559 A:{Catch:{ Exception -> 0x1659 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1191:0x174d A:{Catch:{ Exception -> 0x179b }} */
    /* JADX WARNING: Removed duplicated region for block: B:1193:0x1759 A:{Catch:{ Exception -> 0x179b }} */
    /* JADX WARNING: Removed duplicated region for block: B:1199:0x176f A:{Catch:{ Exception -> 0x179b }} */
    /* JADX WARNING: Removed duplicated region for block: B:1203:0x177d A:{Catch:{ Exception -> 0x179b }} */
    /* JADX WARNING: Removed duplicated region for block: B:1202:0x177b A:{Catch:{ Exception -> 0x179b }} */
    /* JADX WARNING: Removed duplicated region for block: B:1206:0x1791 A:{Catch:{ Exception -> 0x179b }} */
    /* JADX WARNING: Removed duplicated region for block: B:1249:0x183d A:{Catch:{ Exception -> 0x1847 }} */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x08c8  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x086e A:{SYNTHETIC, Splitter:B:506:0x086e} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x098a A:{Catch:{ Exception -> 0x08ef }} */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17a1  */
    /* JADX WARNING: Removed duplicated region for block: B:1177:0x170f A:{SYNTHETIC, Splitter:B:1177:0x170f} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x07ff  */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x07fd  */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0808 A:{SYNTHETIC, Splitter:B:475:0x0808} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x0863  */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0833 A:{Catch:{ Exception -> 0x0820 }} */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x086e A:{SYNTHETIC, Splitter:B:506:0x086e} */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x08c8  */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x091b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x098a A:{Catch:{ Exception -> 0x08ef }} */
    /* JADX WARNING: Removed duplicated region for block: B:1177:0x170f A:{SYNTHETIC, Splitter:B:1177:0x170f} */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17a1  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x026c  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ce A:{SYNTHETIC, Splitter:B:42:0x00ce} */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x05d8 A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x05fb A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x05ea A:{Catch:{ Exception -> 0x185d }} */
    /* JADX WARNING: Removed duplicated region for block: B:355:0x0625 A:{SYNTHETIC, Splitter:B:355:0x0625} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0623  */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x0678  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0633 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x06c0 A:{Catch:{ Exception -> 0x0671 }} */
    /* JADX WARNING: Removed duplicated region for block: B:381:0x068c  */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x06d8 A:{Catch:{ Exception -> 0x0671 }} */
    /* JADX WARNING: Removed duplicated region for block: B:460:0x07de A:{Catch:{ Exception -> 0x07ee }} */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x07fd  */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x07ff  */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0808 A:{SYNTHETIC, Splitter:B:475:0x0808} */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x082a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x0833 A:{Catch:{ Exception -> 0x0820 }} */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x0863  */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x08c8  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x086e A:{SYNTHETIC, Splitter:B:506:0x086e} */
    /* JADX WARNING: Removed duplicated region for block: B:553:0x091b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:558:0x098a A:{Catch:{ Exception -> 0x08ef }} */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17a1  */
    /* JADX WARNING: Removed duplicated region for block: B:1177:0x170f A:{SYNTHETIC, Splitter:B:1177:0x170f} */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Removed duplicated region for block: B:1272:0x1882  */
    /* JADX WARNING: Removed duplicated region for block: B:1271:0x1880  */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1888  */
    /* JADX WARNING: Missing block: B:191:0x0397, code skipped:
            if (r12.containsKey(r4) != false) goto L_0x02f4;
     */
    /* JADX WARNING: Missing block: B:272:0x04cf, code skipped:
            if (org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, false) != false) goto L_0x04d1;
     */
    /* JADX WARNING: Missing block: B:691:0x0bfd, code skipped:
            if (r49.containsKey("forceDocument") != false) goto L_0x0bff;
     */
    private void sendMessage(java.lang.String r43, java.lang.String r44, org.telegram.tgnet.TLRPC.MessageMedia r45, org.telegram.tgnet.TLRPC.TL_photo r46, org.telegram.messenger.VideoEditedInfo r47, org.telegram.tgnet.TLRPC.User r48, org.telegram.tgnet.TLRPC.TL_document r49, org.telegram.tgnet.TLRPC.TL_game r50, org.telegram.tgnet.TLRPC.TL_messageMediaPoll r51, long r52, java.lang.String r54, org.telegram.messenger.MessageObject r55, org.telegram.tgnet.TLRPC.WebPage r56, boolean r57, org.telegram.messenger.MessageObject r58, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r59, org.telegram.tgnet.TLRPC.ReplyMarkup r60, java.util.HashMap<java.lang.String, java.lang.String> r61, boolean r62, int r63, int r64, java.lang.Object r65) {
        /*
        r42 = this;
        r1 = r42;
        r2 = r43;
        r3 = r45;
        r4 = r46;
        r5 = r48;
        r6 = r49;
        r7 = r50;
        r8 = r51;
        r9 = r52;
        r11 = r54;
        r12 = r55;
        r13 = r56;
        r14 = r58;
        r15 = r59;
        r12 = r61;
        r6 = r64;
        if (r5 == 0) goto L_0x0027;
    L_0x0022:
        r7 = r5.phone;
        if (r7 != 0) goto L_0x0027;
    L_0x0026:
        return;
    L_0x0027:
        r16 = 0;
        r7 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1));
        if (r7 != 0) goto L_0x002e;
    L_0x002d:
        return;
    L_0x002e:
        r7 = "";
        if (r2 != 0) goto L_0x0037;
    L_0x0032:
        if (r44 != 0) goto L_0x0037;
    L_0x0034:
        r18 = r7;
        goto L_0x0039;
    L_0x0037:
        r18 = r44;
    L_0x0039:
        if (r12 == 0) goto L_0x004c;
    L_0x003b:
        r5 = "originalPath";
        r5 = r12.containsKey(r5);
        if (r5 == 0) goto L_0x004c;
    L_0x0043:
        r5 = "originalPath";
        r5 = r12.get(r5);
        r5 = (java.lang.String) r5;
        goto L_0x004d;
    L_0x004c:
        r5 = 0;
    L_0x004d:
        r19 = -1;
        r20 = r5;
        r5 = (int) r9;
        r21 = 32;
        r3 = r9 >> r21;
        r4 = (int) r3;
        if (r5 == 0) goto L_0x0062;
    L_0x0059:
        r3 = r42.getMessagesController();
        r3 = r3.getInputPeer(r5);
        goto L_0x0063;
    L_0x0062:
        r3 = 0;
    L_0x0063:
        if (r5 != 0) goto L_0x00a7;
    L_0x0065:
        r10 = r42.getMessagesController();
        r9 = java.lang.Integer.valueOf(r4);
        r9 = r10.getEncryptedChat(r9);
        if (r9 != 0) goto L_0x00a4;
    L_0x0073:
        if (r14 == 0) goto L_0x00a3;
    L_0x0075:
        r2 = r42.getMessagesStorage();
        r3 = r14.messageOwner;
        r4 = r14.scheduled;
        r2.markMessageAsSendError(r3, r4);
        r2 = r14.messageOwner;
        r3 = 2;
        r2.send_state = r3;
        r2 = r42.getNotificationCenter();
        r3 = org.telegram.messenger.NotificationCenter.messageSendError;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = r58.getId();
        r5 = java.lang.Integer.valueOf(r5);
        r6 = 0;
        r4[r6] = r5;
        r2.postNotificationName(r3, r4);
        r2 = r58.getId();
        r1.processSentMessage(r2);
    L_0x00a3:
        return;
    L_0x00a4:
        r23 = r4;
        goto L_0x00c9;
    L_0x00a7:
        r9 = r3 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r9 == 0) goto L_0x00c6;
    L_0x00ab:
        r9 = r42.getMessagesController();
        r10 = r3.channel_id;
        r10 = java.lang.Integer.valueOf(r10);
        r9 = r9.getChat(r10);
        if (r9 == 0) goto L_0x00c1;
    L_0x00bb:
        r9 = r9.megagroup;
        if (r9 != 0) goto L_0x00c1;
    L_0x00bf:
        r10 = 1;
        goto L_0x00c2;
    L_0x00c1:
        r10 = 0;
    L_0x00c2:
        r23 = r4;
        r9 = 0;
        goto L_0x00ca;
    L_0x00c6:
        r23 = r4;
        r9 = 0;
    L_0x00c9:
        r10 = 0;
    L_0x00ca:
        r4 = "query_id";
        if (r14 == 0) goto L_0x026c;
    L_0x00ce:
        r1 = r14.messageOwner;	 Catch:{ Exception -> 0x0262 }
        r26 = r58.isForwarded();	 Catch:{ Exception -> 0x0258 }
        if (r26 == 0) goto L_0x00ee;
    L_0x00d6:
        r43 = r46;
        r19 = r4;
        r26 = r5;
        r28 = r10;
        r31 = r13;
        r50 = r18;
        r15 = 4;
        r13 = r48;
        r4 = r49;
        r5 = r1;
        r1 = r2;
        r2 = r8;
        r8 = r45;
        goto L_0x05d2;
    L_0x00ee:
        r26 = r5;
        r5 = r14.type;	 Catch:{ Exception -> 0x0258 }
        if (r5 == 0) goto L_0x01fe;
    L_0x00f4:
        r5 = r58.isAnimatedEmoji();	 Catch:{ Exception -> 0x0258 }
        if (r5 == 0) goto L_0x00fc;
    L_0x00fa:
        goto L_0x01fe;
    L_0x00fc:
        r5 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r27 = r3;
        r3 = 4;
        if (r5 != r3) goto L_0x0114;
    L_0x0103:
        r3 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r19 = r49;
        r29 = r8;
        r28 = r10;
        r5 = 2;
        r22 = 1;
        r8 = r46;
        r10 = r48;
        goto L_0x021a;
    L_0x0114:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 1;
        if (r3 != r5) goto L_0x012f;
    L_0x0119:
        r3 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r3 = r3.photo;	 Catch:{ Exception -> 0x0258 }
        r3 = (org.telegram.tgnet.TLRPC.TL_photo) r3;	 Catch:{ Exception -> 0x0258 }
        r19 = r49;
        r29 = r8;
        r28 = r10;
        r5 = 2;
        r22 = 2;
        r10 = r48;
        r8 = r3;
    L_0x012b:
        r3 = r45;
        goto L_0x021a;
    L_0x012f:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 3;
        if (r3 == r5) goto L_0x01ea;
    L_0x0134:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 5;
        if (r3 == r5) goto L_0x01ea;
    L_0x0139:
        if (r47 == 0) goto L_0x013d;
    L_0x013b:
        goto L_0x01ea;
    L_0x013d:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 12;
        if (r3 != r5) goto L_0x0182;
    L_0x0143:
        r3 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2;	 Catch:{ Exception -> 0x0258 }
        r3.<init>();	 Catch:{ Exception -> 0x0258 }
        r5 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r5.phone_number;	 Catch:{ Exception -> 0x0258 }
        r3.phone = r5;	 Catch:{ Exception -> 0x0258 }
        r5 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r5.first_name;	 Catch:{ Exception -> 0x0258 }
        r3.first_name = r5;	 Catch:{ Exception -> 0x0258 }
        r5 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r5.last_name;	 Catch:{ Exception -> 0x0258 }
        r3.last_name = r5;	 Catch:{ Exception -> 0x0258 }
        r5 = new org.telegram.tgnet.TLRPC$TL_restrictionReason;	 Catch:{ Exception -> 0x0258 }
        r5.<init>();	 Catch:{ Exception -> 0x0258 }
        r5.platform = r7;	 Catch:{ Exception -> 0x0258 }
        r5.reason = r7;	 Catch:{ Exception -> 0x0258 }
        r28 = r10;
        r10 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r10 = r10.vcard;	 Catch:{ Exception -> 0x0258 }
        r5.text = r10;	 Catch:{ Exception -> 0x0258 }
        r10 = r3.restriction_reason;	 Catch:{ Exception -> 0x0258 }
        r10.add(r5);	 Catch:{ Exception -> 0x0258 }
        r5 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r5.user_id;	 Catch:{ Exception -> 0x0258 }
        r3.id = r5;	 Catch:{ Exception -> 0x0258 }
        r19 = 6;
        r19 = r49;
        r10 = r3;
        r29 = r8;
        r5 = 2;
        r22 = 6;
        goto L_0x01fb;
    L_0x0182:
        r28 = r10;
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 8;
        if (r3 == r5) goto L_0x01d8;
    L_0x018a:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 9;
        if (r3 == r5) goto L_0x01d8;
    L_0x0190:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 13;
        if (r3 == r5) goto L_0x01d8;
    L_0x0196:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 14;
        if (r3 == r5) goto L_0x01d8;
    L_0x019c:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 15;
        if (r3 != r5) goto L_0x01a3;
    L_0x01a2:
        goto L_0x01d8;
    L_0x01a3:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r5 = 2;
        if (r3 != r5) goto L_0x01b7;
    L_0x01a8:
        r3 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r3 = r3.document;	 Catch:{ Exception -> 0x0258 }
        r3 = (org.telegram.tgnet.TLRPC.TL_document) r3;	 Catch:{ Exception -> 0x0258 }
        r10 = r48;
        r19 = r3;
        r29 = r8;
        r22 = 8;
        goto L_0x01fb;
    L_0x01b7:
        r3 = r14.type;	 Catch:{ Exception -> 0x0258 }
        r10 = 17;
        if (r3 != r10) goto L_0x01cd;
    L_0x01bd:
        r3 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3;	 Catch:{ Exception -> 0x0258 }
        r8 = r46;
        r10 = r48;
        r19 = r49;
        r29 = r3;
        r22 = 10;
        goto L_0x012b;
    L_0x01cd:
        r3 = r45;
        r10 = r48;
        r19 = r49;
        r29 = r8;
        r22 = -1;
        goto L_0x0218;
    L_0x01d8:
        r5 = 2;
        r3 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r3 = r3.document;	 Catch:{ Exception -> 0x0258 }
        r3 = (org.telegram.tgnet.TLRPC.TL_document) r3;	 Catch:{ Exception -> 0x0258 }
        r19 = 7;
        r10 = r48;
        r19 = r3;
        r29 = r8;
        r22 = 7;
        goto L_0x01fb;
    L_0x01ea:
        r28 = r10;
        r5 = 2;
        r3 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r3 = r3.document;	 Catch:{ Exception -> 0x0258 }
        r3 = (org.telegram.tgnet.TLRPC.TL_document) r3;	 Catch:{ Exception -> 0x0258 }
        r10 = r48;
        r19 = r3;
        r29 = r8;
        r22 = 3;
    L_0x01fb:
        r3 = r45;
        goto L_0x0218;
    L_0x01fe:
        r27 = r3;
        r28 = r10;
        r5 = 2;
        r3 = r14.messageOwner;	 Catch:{ Exception -> 0x0258 }
        r3 = r3.media;	 Catch:{ Exception -> 0x0258 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;	 Catch:{ Exception -> 0x0258 }
        if (r3 == 0) goto L_0x020c;
    L_0x020b:
        goto L_0x020e;
    L_0x020c:
        r2 = r1.message;	 Catch:{ Exception -> 0x0258 }
    L_0x020e:
        r3 = r45;
        r10 = r48;
        r19 = r49;
        r29 = r8;
        r22 = 0;
    L_0x0218:
        r8 = r46;
    L_0x021a:
        if (r12 == 0) goto L_0x0224;
    L_0x021c:
        r30 = r12.containsKey(r4);	 Catch:{ Exception -> 0x0258 }
        if (r30 == 0) goto L_0x0224;
    L_0x0222:
        r22 = 9;
    L_0x0224:
        r5 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r5.ttl_seconds;	 Catch:{ Exception -> 0x0258 }
        if (r5 <= 0) goto L_0x0240;
    L_0x022a:
        r5 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r5.ttl_seconds;	 Catch:{ Exception -> 0x0258 }
        r6 = r5;
        r43 = r8;
        r31 = r13;
        r50 = r18;
        r15 = r22;
        r5 = r1;
        r1 = r2;
        r8 = r3;
        r13 = r10;
        r3 = r27;
        r2 = r29;
        goto L_0x0250;
    L_0x0240:
        r5 = r1;
        r1 = r2;
        r43 = r8;
        r31 = r13;
        r50 = r18;
        r15 = r22;
        r2 = r29;
        r8 = r3;
        r13 = r10;
        r3 = r27;
    L_0x0250:
        r41 = r19;
        r19 = r4;
        r4 = r41;
        goto L_0x05d2;
    L_0x0258:
        r0 = move-exception;
        r7 = r42;
        r2 = r0;
        r5 = r1;
        r11 = 0;
        r1 = r63;
        goto L_0x1877;
    L_0x0262:
        r0 = move-exception;
        r7 = r42;
        r1 = r63;
        r2 = r0;
        r5 = 0;
    L_0x0269:
        r11 = 0;
        goto L_0x1877;
    L_0x026c:
        r27 = r3;
        r26 = r5;
        r28 = r10;
        if (r2 == 0) goto L_0x02be;
    L_0x0274:
        if (r9 == 0) goto L_0x027c;
    L_0x0276:
        r1 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0262 }
        r1.<init>();	 Catch:{ Exception -> 0x0262 }
        goto L_0x0281;
    L_0x027c:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0262 }
        r1.<init>();	 Catch:{ Exception -> 0x0262 }
    L_0x0281:
        r5 = r1;
        if (r9 == 0) goto L_0x0298;
    L_0x0284:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending;	 Catch:{ Exception -> 0x185d }
        if (r1 == 0) goto L_0x0298;
    L_0x0288:
        r1 = r13.url;	 Catch:{ Exception -> 0x185d }
        if (r1 == 0) goto L_0x0297;
    L_0x028c:
        r1 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending;	 Catch:{ Exception -> 0x185d }
        r1.<init>();	 Catch:{ Exception -> 0x185d }
        r3 = r13.url;	 Catch:{ Exception -> 0x185d }
        r1.url = r3;	 Catch:{ Exception -> 0x185d }
        r13 = r1;
        goto L_0x0298;
    L_0x0297:
        r13 = 0;
    L_0x0298:
        if (r13 != 0) goto L_0x02a2;
    L_0x029a:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Exception -> 0x185d }
        r1.<init>();	 Catch:{ Exception -> 0x185d }
        r5.media = r1;	 Catch:{ Exception -> 0x185d }
        goto L_0x02ad;
    L_0x02a2:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;	 Catch:{ Exception -> 0x185d }
        r1.<init>();	 Catch:{ Exception -> 0x185d }
        r5.media = r1;	 Catch:{ Exception -> 0x185d }
        r1 = r5.media;	 Catch:{ Exception -> 0x185d }
        r1.webpage = r13;	 Catch:{ Exception -> 0x185d }
    L_0x02ad:
        if (r12 == 0) goto L_0x02b8;
    L_0x02af:
        r1 = r12.containsKey(r4);	 Catch:{ Exception -> 0x185d }
        if (r1 == 0) goto L_0x02b8;
    L_0x02b5:
        r19 = 9;
        goto L_0x02ba;
    L_0x02b8:
        r19 = 0;
    L_0x02ba:
        r5.message = r2;	 Catch:{ Exception -> 0x185d }
        goto L_0x039b;
    L_0x02be:
        if (r8 == 0) goto L_0x02d8;
    L_0x02c0:
        if (r9 == 0) goto L_0x02c8;
    L_0x02c2:
        r1 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0262 }
        r1.<init>();	 Catch:{ Exception -> 0x0262 }
        goto L_0x02cd;
    L_0x02c8:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0262 }
        r1.<init>();	 Catch:{ Exception -> 0x0262 }
    L_0x02cd:
        r5 = r1;
        r5.media = r8;	 Catch:{ Exception -> 0x185d }
        r10 = r48;
        r1 = r49;
        r19 = 10;
        goto L_0x0564;
    L_0x02d8:
        r3 = r45;
        if (r3 == 0) goto L_0x0304;
    L_0x02dc:
        if (r9 == 0) goto L_0x02e4;
    L_0x02de:
        r1 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0262 }
        r1.<init>();	 Catch:{ Exception -> 0x0262 }
        goto L_0x02e9;
    L_0x02e4:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0262 }
        r1.<init>();	 Catch:{ Exception -> 0x0262 }
    L_0x02e9:
        r5 = r1;
        r5.media = r3;	 Catch:{ Exception -> 0x185d }
        if (r12 == 0) goto L_0x02fc;
    L_0x02ee:
        r1 = r12.containsKey(r4);	 Catch:{ Exception -> 0x185d }
        if (r1 == 0) goto L_0x02fc;
    L_0x02f4:
        r10 = r48;
        r1 = r49;
        r19 = 9;
        goto L_0x0564;
    L_0x02fc:
        r10 = r48;
        r1 = r49;
        r19 = 1;
        goto L_0x0564;
    L_0x0304:
        r1 = r46;
        if (r1 == 0) goto L_0x037d;
    L_0x0308:
        if (r9 == 0) goto L_0x0310;
    L_0x030a:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0262 }
        r5.<init>();	 Catch:{ Exception -> 0x0262 }
        goto L_0x0315;
    L_0x0310:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0262 }
        r5.<init>();	 Catch:{ Exception -> 0x0262 }
    L_0x0315:
        r10 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x185d }
        r10.<init>();	 Catch:{ Exception -> 0x185d }
        r5.media = r10;	 Catch:{ Exception -> 0x185d }
        r10 = r5.media;	 Catch:{ Exception -> 0x185d }
        r2 = r10.flags;	 Catch:{ Exception -> 0x185d }
        r19 = 3;
        r2 = r2 | 3;
        r10.flags = r2;	 Catch:{ Exception -> 0x185d }
        if (r15 == 0) goto L_0x032a;
    L_0x0328:
        r5.entities = r15;	 Catch:{ Exception -> 0x185d }
    L_0x032a:
        if (r6 == 0) goto L_0x033c;
    L_0x032c:
        r2 = r5.media;	 Catch:{ Exception -> 0x185d }
        r2.ttl_seconds = r6;	 Catch:{ Exception -> 0x185d }
        r5.ttl = r6;	 Catch:{ Exception -> 0x185d }
        r2 = r5.media;	 Catch:{ Exception -> 0x185d }
        r10 = r2.flags;	 Catch:{ Exception -> 0x185d }
        r19 = 4;
        r10 = r10 | 4;
        r2.flags = r10;	 Catch:{ Exception -> 0x185d }
    L_0x033c:
        r2 = r5.media;	 Catch:{ Exception -> 0x185d }
        r2.photo = r1;	 Catch:{ Exception -> 0x185d }
        if (r12 == 0) goto L_0x034b;
    L_0x0342:
        r2 = r12.containsKey(r4);	 Catch:{ Exception -> 0x185d }
        if (r2 == 0) goto L_0x034b;
    L_0x0348:
        r19 = 9;
        goto L_0x034d;
    L_0x034b:
        r19 = 2;
    L_0x034d:
        if (r11 == 0) goto L_0x0360;
    L_0x034f:
        r2 = r54.length();	 Catch:{ Exception -> 0x185d }
        if (r2 <= 0) goto L_0x0360;
    L_0x0355:
        r2 = "http";
        r2 = r11.startsWith(r2);	 Catch:{ Exception -> 0x185d }
        if (r2 == 0) goto L_0x0360;
    L_0x035d:
        r5.attachPath = r11;	 Catch:{ Exception -> 0x185d }
        goto L_0x039b;
    L_0x0360:
        r2 = r1.sizes;	 Catch:{ Exception -> 0x185d }
        r10 = r1.sizes;	 Catch:{ Exception -> 0x185d }
        r10 = r10.size();	 Catch:{ Exception -> 0x185d }
        r1 = 1;
        r10 = r10 - r1;
        r2 = r2.get(r10);	 Catch:{ Exception -> 0x185d }
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;	 Catch:{ Exception -> 0x185d }
        r2 = r2.location;	 Catch:{ Exception -> 0x185d }
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r1);	 Catch:{ Exception -> 0x185d }
        r1 = r2.toString();	 Catch:{ Exception -> 0x185d }
        r5.attachPath = r1;	 Catch:{ Exception -> 0x185d }
        goto L_0x039b;
    L_0x037d:
        r1 = r50;
        if (r1 == 0) goto L_0x03a1;
    L_0x0381:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0262 }
        r5.<init>();	 Catch:{ Exception -> 0x0262 }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame;	 Catch:{ Exception -> 0x185d }
        r2.<init>();	 Catch:{ Exception -> 0x185d }
        r5.media = r2;	 Catch:{ Exception -> 0x185d }
        r2 = r5.media;	 Catch:{ Exception -> 0x185d }
        r2.game = r1;	 Catch:{ Exception -> 0x185d }
        if (r12 == 0) goto L_0x039b;
    L_0x0393:
        r1 = r12.containsKey(r4);	 Catch:{ Exception -> 0x185d }
        if (r1 == 0) goto L_0x039b;
    L_0x0399:
        goto L_0x02f4;
    L_0x039b:
        r10 = r48;
    L_0x039d:
        r1 = r49;
        goto L_0x0564;
    L_0x03a1:
        r10 = r48;
        r5 = 0;
        if (r10 == 0) goto L_0x0434;
    L_0x03a6:
        if (r9 == 0) goto L_0x03b7;
    L_0x03a8:
        r1 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x03ae }
        r1.<init>();	 Catch:{ Exception -> 0x03ae }
        goto L_0x03bc;
    L_0x03ae:
        r0 = move-exception;
        r7 = r42;
        r1 = r63;
        r2 = r0;
        r11 = r5;
        goto L_0x1877;
    L_0x03b7:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0262 }
        r1.<init>();	 Catch:{ Exception -> 0x0262 }
    L_0x03bc:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact;	 Catch:{ Exception -> 0x0258 }
        r2.<init>();	 Catch:{ Exception -> 0x0258 }
        r1.media = r2;	 Catch:{ Exception -> 0x0258 }
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r10.phone;	 Catch:{ Exception -> 0x0258 }
        r2.phone_number = r5;	 Catch:{ Exception -> 0x0258 }
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r10.first_name;	 Catch:{ Exception -> 0x0258 }
        r2.first_name = r5;	 Catch:{ Exception -> 0x0258 }
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r10.last_name;	 Catch:{ Exception -> 0x0258 }
        r2.last_name = r5;	 Catch:{ Exception -> 0x0258 }
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r10.id;	 Catch:{ Exception -> 0x0258 }
        r2.user_id = r5;	 Catch:{ Exception -> 0x0258 }
        r2 = r10.restriction_reason;	 Catch:{ Exception -> 0x0258 }
        r2 = r2.isEmpty();	 Catch:{ Exception -> 0x0258 }
        if (r2 != 0) goto L_0x0406;
    L_0x03e3:
        r2 = r10.restriction_reason;	 Catch:{ Exception -> 0x0258 }
        r5 = 0;
        r2 = r2.get(r5);	 Catch:{ Exception -> 0x0258 }
        r2 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r2;	 Catch:{ Exception -> 0x0258 }
        r2 = r2.text;	 Catch:{ Exception -> 0x0258 }
        r5 = "BEGIN:VCARD";
        r2 = r2.startsWith(r5);	 Catch:{ Exception -> 0x0258 }
        if (r2 == 0) goto L_0x0406;
    L_0x03f6:
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r5 = r10.restriction_reason;	 Catch:{ Exception -> 0x0258 }
        r3 = 0;
        r5 = r5.get(r3);	 Catch:{ Exception -> 0x0258 }
        r5 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r5;	 Catch:{ Exception -> 0x0258 }
        r3 = r5.text;	 Catch:{ Exception -> 0x0258 }
        r2.vcard = r3;	 Catch:{ Exception -> 0x0258 }
        goto L_0x040a;
    L_0x0406:
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r2.vcard = r7;	 Catch:{ Exception -> 0x0258 }
    L_0x040a:
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r2 = r2.first_name;	 Catch:{ Exception -> 0x0258 }
        if (r2 != 0) goto L_0x0416;
    L_0x0410:
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r2.first_name = r7;	 Catch:{ Exception -> 0x0258 }
        r10.first_name = r7;	 Catch:{ Exception -> 0x0258 }
    L_0x0416:
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r2 = r2.last_name;	 Catch:{ Exception -> 0x0258 }
        if (r2 != 0) goto L_0x0422;
    L_0x041c:
        r2 = r1.media;	 Catch:{ Exception -> 0x0258 }
        r2.last_name = r7;	 Catch:{ Exception -> 0x0258 }
        r10.last_name = r7;	 Catch:{ Exception -> 0x0258 }
    L_0x0422:
        if (r12 == 0) goto L_0x042f;
    L_0x0424:
        r2 = r12.containsKey(r4);	 Catch:{ Exception -> 0x0258 }
        if (r2 == 0) goto L_0x042f;
    L_0x042a:
        r5 = r1;
        r19 = 9;
        goto L_0x039d;
    L_0x042f:
        r19 = 6;
        r5 = r1;
        goto L_0x039d;
    L_0x0434:
        r1 = r49;
        if (r1 == 0) goto L_0x0561;
    L_0x0438:
        if (r9 == 0) goto L_0x0440;
    L_0x043a:
        r2 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0262 }
        r2.<init>();	 Catch:{ Exception -> 0x0262 }
        goto L_0x0445;
    L_0x0440:
        r2 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0262 }
        r2.<init>();	 Catch:{ Exception -> 0x0262 }
    L_0x0445:
        r5 = r2;
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x055c }
        r2.<init>();	 Catch:{ Exception -> 0x055c }
        r5.media = r2;	 Catch:{ Exception -> 0x055c }
        r2 = r5.media;	 Catch:{ Exception -> 0x055c }
        r3 = r2.flags;	 Catch:{ Exception -> 0x055c }
        r19 = 3;
        r3 = r3 | 3;
        r2.flags = r3;	 Catch:{ Exception -> 0x055c }
        if (r6 == 0) goto L_0x0469;
    L_0x0459:
        r2 = r5.media;	 Catch:{ Exception -> 0x185d }
        r2.ttl_seconds = r6;	 Catch:{ Exception -> 0x185d }
        r5.ttl = r6;	 Catch:{ Exception -> 0x185d }
        r2 = r5.media;	 Catch:{ Exception -> 0x185d }
        r3 = r2.flags;	 Catch:{ Exception -> 0x185d }
        r19 = 4;
        r3 = r3 | 4;
        r2.flags = r3;	 Catch:{ Exception -> 0x185d }
    L_0x0469:
        r2 = r5.media;	 Catch:{ Exception -> 0x055c }
        r2.document = r1;	 Catch:{ Exception -> 0x055c }
        if (r12 == 0) goto L_0x0478;
    L_0x046f:
        r2 = r12.containsKey(r4);	 Catch:{ Exception -> 0x185d }
        if (r2 == 0) goto L_0x0478;
    L_0x0475:
        r19 = 9;
        goto L_0x0496;
    L_0x0478:
        r2 = org.telegram.messenger.MessageObject.isVideoDocument(r49);	 Catch:{ Exception -> 0x055c }
        if (r2 != 0) goto L_0x0494;
    L_0x047e:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r49);	 Catch:{ Exception -> 0x185d }
        if (r2 != 0) goto L_0x0494;
    L_0x0484:
        if (r47 == 0) goto L_0x0487;
    L_0x0486:
        goto L_0x0494;
    L_0x0487:
        r2 = org.telegram.messenger.MessageObject.isVoiceDocument(r49);	 Catch:{ Exception -> 0x185d }
        if (r2 == 0) goto L_0x0490;
    L_0x048d:
        r19 = 8;
        goto L_0x0496;
    L_0x0490:
        r2 = 7;
        r19 = 7;
        goto L_0x0496;
    L_0x0494:
        r19 = 3;
    L_0x0496:
        if (r47 == 0) goto L_0x04a9;
    L_0x0498:
        r2 = r47.getString();	 Catch:{ Exception -> 0x185d }
        if (r12 != 0) goto L_0x04a4;
    L_0x049e:
        r3 = new java.util.HashMap;	 Catch:{ Exception -> 0x185d }
        r3.<init>();	 Catch:{ Exception -> 0x185d }
        r12 = r3;
    L_0x04a4:
        r3 = "ve";
        r12.put(r3, r2);	 Catch:{ Exception -> 0x185d }
    L_0x04a9:
        if (r9 == 0) goto L_0x04c0;
    L_0x04ab:
        r2 = r1.dc_id;	 Catch:{ Exception -> 0x185d }
        if (r2 <= 0) goto L_0x04c0;
    L_0x04af:
        r2 = org.telegram.messenger.MessageObject.isStickerDocument(r49);	 Catch:{ Exception -> 0x185d }
        if (r2 != 0) goto L_0x04c0;
    L_0x04b5:
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r49);	 Catch:{ Exception -> 0x185d }
        r2 = r2.toString();	 Catch:{ Exception -> 0x185d }
        r5.attachPath = r2;	 Catch:{ Exception -> 0x185d }
        goto L_0x04c2;
    L_0x04c0:
        r5.attachPath = r11;	 Catch:{ Exception -> 0x055c }
    L_0x04c2:
        if (r9 == 0) goto L_0x0551;
    L_0x04c4:
        r2 = org.telegram.messenger.MessageObject.isStickerDocument(r49);	 Catch:{ Exception -> 0x055c }
        if (r2 != 0) goto L_0x04d1;
    L_0x04ca:
        r2 = 0;
        r3 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r2);	 Catch:{ Exception -> 0x185d }
        if (r3 == 0) goto L_0x0551;
    L_0x04d1:
        r2 = 0;
    L_0x04d2:
        r3 = r1.attributes;	 Catch:{ Exception -> 0x055c }
        r3 = r3.size();	 Catch:{ Exception -> 0x055c }
        if (r2 >= r3) goto L_0x0551;
    L_0x04da:
        r3 = r1.attributes;	 Catch:{ Exception -> 0x055c }
        r3 = r3.get(r2);	 Catch:{ Exception -> 0x055c }
        r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3;	 Catch:{ Exception -> 0x055c }
        r22 = r5;
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;	 Catch:{ Exception -> 0x0547 }
        if (r5 == 0) goto L_0x053e;
    L_0x04e8:
        r5 = r1.attributes;	 Catch:{ Exception -> 0x0547 }
        r5.remove(r2);	 Catch:{ Exception -> 0x0547 }
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55;	 Catch:{ Exception -> 0x0547 }
        r2.<init>();	 Catch:{ Exception -> 0x0547 }
        r5 = r1.attributes;	 Catch:{ Exception -> 0x0547 }
        r5.add(r2);	 Catch:{ Exception -> 0x0547 }
        r5 = r3.alt;	 Catch:{ Exception -> 0x0547 }
        r2.alt = r5;	 Catch:{ Exception -> 0x0547 }
        r5 = r3.stickerset;	 Catch:{ Exception -> 0x0547 }
        if (r5 == 0) goto L_0x0534;
    L_0x04ff:
        r5 = r3.stickerset;	 Catch:{ Exception -> 0x0547 }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x0547 }
        if (r5 == 0) goto L_0x050c;
    L_0x0505:
        r3 = r3.stickerset;	 Catch:{ Exception -> 0x0547 }
        r3 = r3.short_name;	 Catch:{ Exception -> 0x0547 }
        r50 = r12;
        goto L_0x051a;
    L_0x050c:
        r5 = r42.getMediaDataController();	 Catch:{ Exception -> 0x0547 }
        r3 = r3.stickerset;	 Catch:{ Exception -> 0x0547 }
        r50 = r12;
        r12 = r3.id;	 Catch:{ Exception -> 0x0547 }
        r3 = r5.getStickerSetName(r12);	 Catch:{ Exception -> 0x0547 }
    L_0x051a:
        r5 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Exception -> 0x0547 }
        if (r5 != 0) goto L_0x052c;
    L_0x0520:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x0547 }
        r5.<init>();	 Catch:{ Exception -> 0x0547 }
        r2.stickerset = r5;	 Catch:{ Exception -> 0x0547 }
        r2 = r2.stickerset;	 Catch:{ Exception -> 0x0547 }
        r2.short_name = r3;	 Catch:{ Exception -> 0x0547 }
        goto L_0x0555;
    L_0x052c:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x0547 }
        r3.<init>();	 Catch:{ Exception -> 0x0547 }
        r2.stickerset = r3;	 Catch:{ Exception -> 0x0547 }
        goto L_0x0555;
    L_0x0534:
        r50 = r12;
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x0547 }
        r3.<init>();	 Catch:{ Exception -> 0x0547 }
        r2.stickerset = r3;	 Catch:{ Exception -> 0x0547 }
        goto L_0x0555;
    L_0x053e:
        r50 = r12;
        r2 = r2 + 1;
        r13 = r56;
        r5 = r22;
        goto L_0x04d2;
    L_0x0547:
        r0 = move-exception;
        r7 = r42;
        r1 = r63;
        r2 = r0;
        r5 = r22;
        goto L_0x0269;
    L_0x0551:
        r22 = r5;
        r50 = r12;
    L_0x0555:
        r12 = r50;
        r13 = r56;
        r5 = r22;
        goto L_0x0564;
    L_0x055c:
        r0 = move-exception;
        r22 = r5;
        goto L_0x185e;
    L_0x0561:
        r13 = r56;
        r5 = 0;
    L_0x0564:
        if (r15 == 0) goto L_0x0574;
    L_0x0566:
        r2 = r59.isEmpty();	 Catch:{ Exception -> 0x185d }
        if (r2 != 0) goto L_0x0574;
    L_0x056c:
        r5.entities = r15;	 Catch:{ Exception -> 0x185d }
        r2 = r5.flags;	 Catch:{ Exception -> 0x185d }
        r2 = r2 | 128;
        r5.flags = r2;	 Catch:{ Exception -> 0x185d }
    L_0x0574:
        r2 = r18;
        if (r2 == 0) goto L_0x057b;
    L_0x0578:
        r5.message = r2;	 Catch:{ Exception -> 0x185d }
        goto L_0x0581;
    L_0x057b:
        r3 = r5.message;	 Catch:{ Exception -> 0x186f }
        if (r3 != 0) goto L_0x0581;
    L_0x057f:
        r5.message = r7;	 Catch:{ Exception -> 0x185d }
    L_0x0581:
        r3 = r5.attachPath;	 Catch:{ Exception -> 0x186f }
        if (r3 != 0) goto L_0x0587;
    L_0x0585:
        r5.attachPath = r7;	 Catch:{ Exception -> 0x185d }
    L_0x0587:
        r3 = r42.getUserConfig();	 Catch:{ Exception -> 0x186f }
        r3 = r3.getNewMessageId();	 Catch:{ Exception -> 0x186f }
        r5.id = r3;	 Catch:{ Exception -> 0x186f }
        r5.local_id = r3;	 Catch:{ Exception -> 0x186f }
        r3 = 1;
        r5.out = r3;	 Catch:{ Exception -> 0x186f }
        if (r28 == 0) goto L_0x05a2;
    L_0x0598:
        if (r27 == 0) goto L_0x05a2;
    L_0x059a:
        r3 = r27;
        r1 = r3.channel_id;	 Catch:{ Exception -> 0x185d }
        r1 = -r1;
        r5.from_id = r1;	 Catch:{ Exception -> 0x185d }
        goto L_0x05b4;
    L_0x05a2:
        r3 = r27;
        r1 = r42.getUserConfig();	 Catch:{ Exception -> 0x186f }
        r1 = r1.getClientUserId();	 Catch:{ Exception -> 0x186f }
        r5.from_id = r1;	 Catch:{ Exception -> 0x186f }
        r1 = r5.flags;	 Catch:{ Exception -> 0x186f }
        r1 = r1 | 256;
        r5.flags = r1;	 Catch:{ Exception -> 0x186f }
    L_0x05b4:
        r1 = r42.getUserConfig();	 Catch:{ Exception -> 0x186f }
        r18 = r5;
        r5 = 0;
        r1.saveConfig(r5);	 Catch:{ Exception -> 0x1865 }
        r1 = r43;
        r43 = r46;
        r50 = r2;
        r2 = r8;
        r31 = r13;
        r5 = r18;
        r15 = r19;
        r8 = r45;
        r19 = r4;
        r13 = r10;
        r4 = r49;
    L_0x05d2:
        r10 = r5.random_id;	 Catch:{ Exception -> 0x185d }
        r18 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1));
        if (r18 != 0) goto L_0x05de;
    L_0x05d8:
        r10 = r42.getNextRandomId();	 Catch:{ Exception -> 0x185d }
        r5.random_id = r10;	 Catch:{ Exception -> 0x185d }
    L_0x05de:
        if (r12 == 0) goto L_0x0613;
    L_0x05e0:
        r10 = "bot";
        r10 = r12.containsKey(r10);	 Catch:{ Exception -> 0x185d }
        if (r10 == 0) goto L_0x0613;
    L_0x05e8:
        if (r9 == 0) goto L_0x05fb;
    L_0x05ea:
        r10 = "bot_name";
        r10 = r12.get(r10);	 Catch:{ Exception -> 0x185d }
        r10 = (java.lang.String) r10;	 Catch:{ Exception -> 0x185d }
        r5.via_bot_name = r10;	 Catch:{ Exception -> 0x185d }
        r10 = r5.via_bot_name;	 Catch:{ Exception -> 0x185d }
        if (r10 != 0) goto L_0x060d;
    L_0x05f8:
        r5.via_bot_name = r7;	 Catch:{ Exception -> 0x185d }
        goto L_0x060d;
    L_0x05fb:
        r10 = "bot";
        r10 = r12.get(r10);	 Catch:{ Exception -> 0x185d }
        r10 = (java.lang.CharSequence) r10;	 Catch:{ Exception -> 0x185d }
        r10 = org.telegram.messenger.Utilities.parseInt(r10);	 Catch:{ Exception -> 0x185d }
        r10 = r10.intValue();	 Catch:{ Exception -> 0x185d }
        r5.via_bot_id = r10;	 Catch:{ Exception -> 0x185d }
    L_0x060d:
        r10 = r5.flags;	 Catch:{ Exception -> 0x185d }
        r10 = r10 | 2048;
        r5.flags = r10;	 Catch:{ Exception -> 0x185d }
    L_0x0613:
        r5.params = r12;	 Catch:{ Exception -> 0x185d }
        if (r14 == 0) goto L_0x061f;
    L_0x0617:
        r10 = r14.resendAsIs;	 Catch:{ Exception -> 0x185d }
        if (r10 != 0) goto L_0x061c;
    L_0x061b:
        goto L_0x061f;
    L_0x061c:
        r10 = r63;
        goto L_0x067b;
    L_0x061f:
        r10 = r63;
        if (r10 == 0) goto L_0x0625;
    L_0x0623:
        r11 = r10;
        goto L_0x062d;
    L_0x0625:
        r11 = r42.getConnectionsManager();	 Catch:{ Exception -> 0x1858 }
        r11 = r11.getCurrentTime();	 Catch:{ Exception -> 0x1858 }
    L_0x062d:
        r5.date = r11;	 Catch:{ Exception -> 0x1858 }
        r11 = r3 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;	 Catch:{ Exception -> 0x1858 }
        if (r11 == 0) goto L_0x0678;
    L_0x0633:
        if (r10 != 0) goto L_0x0640;
    L_0x0635:
        if (r28 == 0) goto L_0x0640;
    L_0x0637:
        r11 = 1;
        r5.views = r11;	 Catch:{ Exception -> 0x0671 }
        r11 = r5.flags;	 Catch:{ Exception -> 0x0671 }
        r11 = r11 | 1024;
        r5.flags = r11;	 Catch:{ Exception -> 0x0671 }
    L_0x0640:
        r11 = r42.getMessagesController();	 Catch:{ Exception -> 0x0671 }
        r14 = r3.channel_id;	 Catch:{ Exception -> 0x0671 }
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0671 }
        r11 = r11.getChat(r14);	 Catch:{ Exception -> 0x0671 }
        if (r11 == 0) goto L_0x067b;
    L_0x0650:
        r14 = r11.megagroup;	 Catch:{ Exception -> 0x0671 }
        if (r14 == 0) goto L_0x065f;
    L_0x0654:
        r11 = r5.flags;	 Catch:{ Exception -> 0x0671 }
        r14 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r11 = r11 | r14;
        r5.flags = r11;	 Catch:{ Exception -> 0x0671 }
        r14 = 1;
        r5.unread = r14;	 Catch:{ Exception -> 0x0671 }
        goto L_0x067b;
    L_0x065f:
        r14 = 1;
        r5.post = r14;	 Catch:{ Exception -> 0x0671 }
        r11 = r11.signatures;	 Catch:{ Exception -> 0x0671 }
        if (r11 == 0) goto L_0x067b;
    L_0x0666:
        r11 = r42.getUserConfig();	 Catch:{ Exception -> 0x0671 }
        r11 = r11.getClientUserId();	 Catch:{ Exception -> 0x0671 }
        r5.from_id = r11;	 Catch:{ Exception -> 0x0671 }
        goto L_0x067b;
    L_0x0671:
        r0 = move-exception;
        r7 = r42;
    L_0x0674:
        r2 = r0;
        r1 = r10;
        goto L_0x0269;
    L_0x0678:
        r11 = 1;
        r5.unread = r11;	 Catch:{ Exception -> 0x1858 }
    L_0x067b:
        r11 = r5.flags;	 Catch:{ Exception -> 0x1858 }
        r11 = r11 | 512;
        r5.flags = r11;	 Catch:{ Exception -> 0x1858 }
        r45 = r13;
        r11 = 2;
        r13 = r52;
        r5.dialog_id = r13;	 Catch:{ Exception -> 0x1858 }
        r11 = r55;
        if (r11 == 0) goto L_0x06c0;
    L_0x068c:
        if (r9 == 0) goto L_0x06ac;
    L_0x068e:
        r18 = r2;
        r2 = r11.messageOwner;	 Catch:{ Exception -> 0x0671 }
        r27 = r7;
        r28 = r8;
        r7 = r2.random_id;	 Catch:{ Exception -> 0x0671 }
        r2 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x06b2;
    L_0x069c:
        r2 = r11.messageOwner;	 Catch:{ Exception -> 0x0671 }
        r7 = r2.random_id;	 Catch:{ Exception -> 0x0671 }
        r5.reply_to_random_id = r7;	 Catch:{ Exception -> 0x0671 }
        r2 = r5.flags;	 Catch:{ Exception -> 0x0671 }
        r7 = 8;
        r2 = r2 | r7;
        r5.flags = r2;	 Catch:{ Exception -> 0x0671 }
        r7 = 8;
        goto L_0x06b9;
    L_0x06ac:
        r18 = r2;
        r27 = r7;
        r28 = r8;
    L_0x06b2:
        r2 = r5.flags;	 Catch:{ Exception -> 0x0671 }
        r7 = 8;
        r2 = r2 | r7;
        r5.flags = r2;	 Catch:{ Exception -> 0x0671 }
    L_0x06b9:
        r2 = r55.getId();	 Catch:{ Exception -> 0x0671 }
        r5.reply_to_msg_id = r2;	 Catch:{ Exception -> 0x0671 }
        goto L_0x06c8;
    L_0x06c0:
        r18 = r2;
        r27 = r7;
        r28 = r8;
        r7 = 8;
    L_0x06c8:
        r2 = r60;
        if (r2 == 0) goto L_0x06d6;
    L_0x06cc:
        if (r9 != 0) goto L_0x06d6;
    L_0x06ce:
        r8 = r5.flags;	 Catch:{ Exception -> 0x0671 }
        r8 = r8 | 64;
        r5.flags = r8;	 Catch:{ Exception -> 0x0671 }
        r5.reply_markup = r2;	 Catch:{ Exception -> 0x0671 }
    L_0x06d6:
        if (r26 == 0) goto L_0x0713;
    L_0x06d8:
        r2 = r42.getMessagesController();	 Catch:{ Exception -> 0x0671 }
        r8 = r26;
        r2 = r2.getPeer(r8);	 Catch:{ Exception -> 0x0671 }
        r5.to_id = r2;	 Catch:{ Exception -> 0x0671 }
        if (r8 <= 0) goto L_0x0708;
    L_0x06e6:
        r2 = r42.getMessagesController();	 Catch:{ Exception -> 0x0671 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0671 }
        r2 = r2.getUser(r8);	 Catch:{ Exception -> 0x0671 }
        if (r2 != 0) goto L_0x06fc;
    L_0x06f4:
        r1 = r5.id;	 Catch:{ Exception -> 0x0671 }
        r7 = r42;
        r7.processSentMessage(r1);	 Catch:{ Exception -> 0x07ee }
        return;
    L_0x06fc:
        r8 = 8;
        r7 = r42;
        r2 = r2.bot;	 Catch:{ Exception -> 0x07ee }
        if (r2 == 0) goto L_0x070c;
    L_0x0704:
        r2 = 0;
        r5.unread = r2;	 Catch:{ Exception -> 0x07ee }
        goto L_0x070c;
    L_0x0708:
        r8 = 8;
        r7 = r42;
    L_0x070c:
        r46 = r6;
    L_0x070e:
        r2 = r23;
        r6 = 1;
        goto L_0x07dc;
    L_0x0713:
        r8 = 8;
        r7 = r42;
        r2 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Exception -> 0x1856 }
        r2.<init>();	 Catch:{ Exception -> 0x1856 }
        r5.to_id = r2;	 Catch:{ Exception -> 0x1856 }
        r2 = r9.participant_id;	 Catch:{ Exception -> 0x1856 }
        r25 = r42.getUserConfig();	 Catch:{ Exception -> 0x1856 }
        r8 = r25.getClientUserId();	 Catch:{ Exception -> 0x1856 }
        if (r2 != r8) goto L_0x0731;
    L_0x072a:
        r2 = r5.to_id;	 Catch:{ Exception -> 0x07ee }
        r8 = r9.admin_id;	 Catch:{ Exception -> 0x07ee }
        r2.user_id = r8;	 Catch:{ Exception -> 0x07ee }
        goto L_0x0737;
    L_0x0731:
        r2 = r5.to_id;	 Catch:{ Exception -> 0x1856 }
        r8 = r9.participant_id;	 Catch:{ Exception -> 0x1856 }
        r2.user_id = r8;	 Catch:{ Exception -> 0x1856 }
    L_0x0737:
        if (r6 == 0) goto L_0x073c;
    L_0x0739:
        r5.ttl = r6;	 Catch:{ Exception -> 0x07ee }
        goto L_0x0758;
    L_0x073c:
        r2 = r9.ttl;	 Catch:{ Exception -> 0x1856 }
        r5.ttl = r2;	 Catch:{ Exception -> 0x1856 }
        r2 = r5.ttl;	 Catch:{ Exception -> 0x1856 }
        if (r2 == 0) goto L_0x0758;
    L_0x0744:
        r2 = r5.media;	 Catch:{ Exception -> 0x07ee }
        if (r2 == 0) goto L_0x0758;
    L_0x0748:
        r2 = r5.media;	 Catch:{ Exception -> 0x07ee }
        r8 = r5.ttl;	 Catch:{ Exception -> 0x07ee }
        r2.ttl_seconds = r8;	 Catch:{ Exception -> 0x07ee }
        r2 = r5.media;	 Catch:{ Exception -> 0x07ee }
        r8 = r2.flags;	 Catch:{ Exception -> 0x07ee }
        r24 = 4;
        r8 = r8 | 4;
        r2.flags = r8;	 Catch:{ Exception -> 0x07ee }
    L_0x0758:
        r2 = r5.ttl;	 Catch:{ Exception -> 0x1856 }
        if (r2 == 0) goto L_0x070c;
    L_0x075c:
        r2 = r5.media;	 Catch:{ Exception -> 0x07ee }
        r2 = r2.document;	 Catch:{ Exception -> 0x07ee }
        if (r2 == 0) goto L_0x070c;
    L_0x0762:
        r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r5);	 Catch:{ Exception -> 0x07ee }
        if (r2 == 0) goto L_0x079e;
    L_0x0768:
        r2 = 0;
    L_0x0769:
        r8 = r5.media;	 Catch:{ Exception -> 0x07ee }
        r8 = r8.document;	 Catch:{ Exception -> 0x07ee }
        r8 = r8.attributes;	 Catch:{ Exception -> 0x07ee }
        r8 = r8.size();	 Catch:{ Exception -> 0x07ee }
        if (r2 >= r8) goto L_0x078f;
    L_0x0775:
        r8 = r5.media;	 Catch:{ Exception -> 0x07ee }
        r8 = r8.document;	 Catch:{ Exception -> 0x07ee }
        r8 = r8.attributes;	 Catch:{ Exception -> 0x07ee }
        r8 = r8.get(r2);	 Catch:{ Exception -> 0x07ee }
        r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8;	 Catch:{ Exception -> 0x07ee }
        r46 = r6;
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;	 Catch:{ Exception -> 0x07ee }
        if (r6 == 0) goto L_0x078a;
    L_0x0787:
        r2 = r8.duration;	 Catch:{ Exception -> 0x07ee }
        goto L_0x0792;
    L_0x078a:
        r2 = r2 + 1;
        r6 = r46;
        goto L_0x0769;
    L_0x078f:
        r46 = r6;
        r2 = 0;
    L_0x0792:
        r6 = r5.ttl;	 Catch:{ Exception -> 0x07ee }
        r8 = 1;
        r2 = r2 + r8;
        r2 = java.lang.Math.max(r6, r2);	 Catch:{ Exception -> 0x07ee }
        r5.ttl = r2;	 Catch:{ Exception -> 0x07ee }
        goto L_0x070e;
    L_0x079e:
        r46 = r6;
        r2 = org.telegram.messenger.MessageObject.isVideoMessage(r5);	 Catch:{ Exception -> 0x07ee }
        if (r2 != 0) goto L_0x07ac;
    L_0x07a6:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r5);	 Catch:{ Exception -> 0x07ee }
        if (r2 == 0) goto L_0x070e;
    L_0x07ac:
        r2 = 0;
    L_0x07ad:
        r6 = r5.media;	 Catch:{ Exception -> 0x07ee }
        r6 = r6.document;	 Catch:{ Exception -> 0x07ee }
        r6 = r6.attributes;	 Catch:{ Exception -> 0x07ee }
        r6 = r6.size();	 Catch:{ Exception -> 0x07ee }
        if (r2 >= r6) goto L_0x07cf;
    L_0x07b9:
        r6 = r5.media;	 Catch:{ Exception -> 0x07ee }
        r6 = r6.document;	 Catch:{ Exception -> 0x07ee }
        r6 = r6.attributes;	 Catch:{ Exception -> 0x07ee }
        r6 = r6.get(r2);	 Catch:{ Exception -> 0x07ee }
        r6 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r6;	 Catch:{ Exception -> 0x07ee }
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x07ee }
        if (r8 == 0) goto L_0x07cc;
    L_0x07c9:
        r2 = r6.duration;	 Catch:{ Exception -> 0x07ee }
        goto L_0x07d0;
    L_0x07cc:
        r2 = r2 + 1;
        goto L_0x07ad;
    L_0x07cf:
        r2 = 0;
    L_0x07d0:
        r6 = r5.ttl;	 Catch:{ Exception -> 0x07ee }
        r8 = 1;
        r2 = r2 + r8;
        r2 = java.lang.Math.max(r6, r2);	 Catch:{ Exception -> 0x07ee }
        r5.ttl = r2;	 Catch:{ Exception -> 0x07ee }
        goto L_0x070e;
    L_0x07dc:
        if (r2 == r6) goto L_0x07f1;
    L_0x07de:
        r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r5);	 Catch:{ Exception -> 0x07ee }
        if (r2 != 0) goto L_0x07ea;
    L_0x07e4:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r5);	 Catch:{ Exception -> 0x07ee }
        if (r2 == 0) goto L_0x07f1;
    L_0x07ea:
        r2 = 1;
        r5.media_unread = r2;	 Catch:{ Exception -> 0x07ee }
        goto L_0x07f2;
    L_0x07ee:
        r0 = move-exception;
        goto L_0x0674;
    L_0x07f1:
        r2 = 1;
    L_0x07f2:
        r5.send_state = r2;	 Catch:{ Exception -> 0x1856 }
        r6 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x1856 }
        r8 = r7.currentAccount;	 Catch:{ Exception -> 0x1856 }
        r6.<init>(r8, r5, r11, r2);	 Catch:{ Exception -> 0x1856 }
        if (r10 == 0) goto L_0x07ff;
    L_0x07fd:
        r2 = 1;
        goto L_0x0800;
    L_0x07ff:
        r2 = 0;
    L_0x0800:
        r6.scheduled = r2;	 Catch:{ Exception -> 0x1851 }
        r2 = r6.isForwarded();	 Catch:{ Exception -> 0x1851 }
        if (r2 != 0) goto L_0x0826;
    L_0x0808:
        r2 = r6.type;	 Catch:{ Exception -> 0x0820 }
        r8 = 3;
        if (r2 == r8) goto L_0x0814;
    L_0x080d:
        if (r47 != 0) goto L_0x0814;
    L_0x080f:
        r2 = r6.type;	 Catch:{ Exception -> 0x0820 }
        r8 = 2;
        if (r2 != r8) goto L_0x0826;
    L_0x0814:
        r2 = r5.attachPath;	 Catch:{ Exception -> 0x0820 }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x0820 }
        if (r2 != 0) goto L_0x0826;
    L_0x081c:
        r2 = 1;
        r6.attachPathExists = r2;	 Catch:{ Exception -> 0x0820 }
        goto L_0x0826;
    L_0x0820:
        r0 = move-exception;
        r2 = r0;
        r11 = r6;
    L_0x0823:
        r1 = r10;
        goto L_0x1877;
    L_0x0826:
        r2 = r6.videoEditedInfo;	 Catch:{ Exception -> 0x1851 }
        if (r2 == 0) goto L_0x082f;
    L_0x082a:
        if (r47 != 0) goto L_0x082f;
    L_0x082c:
        r2 = r6.videoEditedInfo;	 Catch:{ Exception -> 0x0820 }
        goto L_0x0831;
    L_0x082f:
        r2 = r47;
    L_0x0831:
        if (r12 == 0) goto L_0x0863;
    L_0x0833:
        r8 = "groupId";
        r8 = r12.get(r8);	 Catch:{ Exception -> 0x0820 }
        r8 = (java.lang.String) r8;	 Catch:{ Exception -> 0x0820 }
        if (r8 == 0) goto L_0x0853;
    L_0x083d:
        r8 = org.telegram.messenger.Utilities.parseLong(r8);	 Catch:{ Exception -> 0x0820 }
        r48 = r1;
        r47 = r2;
        r1 = r8.longValue();	 Catch:{ Exception -> 0x0820 }
        r5.grouped_id = r1;	 Catch:{ Exception -> 0x0820 }
        r8 = r5.flags;	 Catch:{ Exception -> 0x0820 }
        r11 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r8 = r8 | r11;
        r5.flags = r8;	 Catch:{ Exception -> 0x0820 }
        goto L_0x0859;
    L_0x0853:
        r48 = r1;
        r47 = r2;
        r1 = r16;
    L_0x0859:
        r8 = "final";
        r8 = r12.get(r8);	 Catch:{ Exception -> 0x0820 }
        if (r8 == 0) goto L_0x0869;
    L_0x0861:
        r8 = 1;
        goto L_0x086a;
    L_0x0863:
        r48 = r1;
        r47 = r2;
        r1 = r16;
    L_0x0869:
        r8 = 0;
    L_0x086a:
        r11 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1));
        if (r11 != 0) goto L_0x08c8;
    L_0x086e:
        r8 = new java.util.ArrayList;	 Catch:{ Exception -> 0x08c0 }
        r8.<init>();	 Catch:{ Exception -> 0x08c0 }
        r8.add(r6);	 Catch:{ Exception -> 0x08c0 }
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x08c0 }
        r11.<init>();	 Catch:{ Exception -> 0x08c0 }
        r11.add(r5);	 Catch:{ Exception -> 0x08c0 }
        r49 = r12;
        r12 = r7.currentAccount;	 Catch:{ Exception -> 0x08c0 }
        r32 = org.telegram.messenger.MessagesStorage.getInstance(r12);	 Catch:{ Exception -> 0x08c0 }
        r34 = 0;
        r35 = 1;
        r36 = 0;
        r37 = 0;
        if (r10 == 0) goto L_0x0893;
    L_0x0890:
        r38 = 1;
        goto L_0x0895;
    L_0x0893:
        r38 = 0;
    L_0x0895:
        r33 = r11;
        r32.putMessages(r33, r34, r35, r36, r37, r38);	 Catch:{ Exception -> 0x08c0 }
        r11 = r7.currentAccount;	 Catch:{ Exception -> 0x08c0 }
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);	 Catch:{ Exception -> 0x08c0 }
        if (r10 == 0) goto L_0x08a4;
    L_0x08a2:
        r12 = 1;
        goto L_0x08a5;
    L_0x08a4:
        r12 = 0;
    L_0x08a5:
        r11.updateInterfaceWithMessages(r13, r8, r12);	 Catch:{ Exception -> 0x08c0 }
        if (r10 != 0) goto L_0x08bb;
    L_0x08aa:
        r8 = r7.currentAccount;	 Catch:{ Exception -> 0x08c0 }
        r8 = org.telegram.messenger.NotificationCenter.getInstance(r8);	 Catch:{ Exception -> 0x08c0 }
        r11 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;	 Catch:{ Exception -> 0x08c0 }
        r56 = r6;
        r12 = 0;
        r6 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x08ef }
        r8.postNotificationName(r11, r6);	 Catch:{ Exception -> 0x08ef }
        goto L_0x08bd;
    L_0x08bb:
        r56 = r6;
    L_0x08bd:
        r6 = 0;
        r11 = 0;
        goto L_0x0915;
    L_0x08c0:
        r0 = move-exception;
        r56 = r6;
    L_0x08c3:
        r11 = r56;
    L_0x08c5:
        r2 = r0;
        goto L_0x0823;
    L_0x08c8:
        r56 = r6;
        r49 = r12;
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x184d }
        r6.<init>();	 Catch:{ Exception -> 0x184d }
        r11 = "group_";
        r6.append(r11);	 Catch:{ Exception -> 0x184d }
        r6.append(r1);	 Catch:{ Exception -> 0x184d }
        r6 = r6.toString();	 Catch:{ Exception -> 0x184d }
        r11 = r7.delayedMessages;	 Catch:{ Exception -> 0x184d }
        r6 = r11.get(r6);	 Catch:{ Exception -> 0x184d }
        r6 = (java.util.ArrayList) r6;	 Catch:{ Exception -> 0x184d }
        if (r6 == 0) goto L_0x08f1;
    L_0x08e7:
        r11 = 0;
        r6 = r6.get(r11);	 Catch:{ Exception -> 0x08ef }
        r6 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r6;	 Catch:{ Exception -> 0x08ef }
        goto L_0x08f2;
    L_0x08ef:
        r0 = move-exception;
        goto L_0x08c3;
    L_0x08f1:
        r6 = 0;
    L_0x08f2:
        if (r6 != 0) goto L_0x0905;
    L_0x08f4:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08ef }
        r6.<init>(r13);	 Catch:{ Exception -> 0x08ef }
        r6.initForGroup(r1);	 Catch:{ Exception -> 0x08ef }
        r6.encryptedChat = r9;	 Catch:{ Exception -> 0x08ef }
        if (r10 == 0) goto L_0x0902;
    L_0x0900:
        r11 = 1;
        goto L_0x0903;
    L_0x0902:
        r11 = 0;
    L_0x0903:
        r6.scheduled = r11;	 Catch:{ Exception -> 0x08ef }
    L_0x0905:
        r11 = 0;
        r6.performMediaUpload = r11;	 Catch:{ Exception -> 0x184d }
        r11 = 0;
        r6.photoSize = r11;	 Catch:{ Exception -> 0x184d }
        r6.videoEditedInfo = r11;	 Catch:{ Exception -> 0x184d }
        r6.httpLocation = r11;	 Catch:{ Exception -> 0x184d }
        if (r8 == 0) goto L_0x0915;
    L_0x0911:
        r8 = r5.id;	 Catch:{ Exception -> 0x08ef }
        r6.finalGroupMessage = r8;	 Catch:{ Exception -> 0x08ef }
    L_0x0915:
        r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x184d }
        r12 = "silent_";
        if (r8 == 0) goto L_0x0982;
    L_0x091b:
        if (r3 == 0) goto L_0x0982;
    L_0x091d:
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x08ef }
        r8.<init>();	 Catch:{ Exception -> 0x08ef }
        r11 = "send message user_id = ";
        r8.append(r11);	 Catch:{ Exception -> 0x08ef }
        r11 = r3.user_id;	 Catch:{ Exception -> 0x08ef }
        r8.append(r11);	 Catch:{ Exception -> 0x08ef }
        r11 = " chat_id = ";
        r8.append(r11);	 Catch:{ Exception -> 0x08ef }
        r11 = r3.chat_id;	 Catch:{ Exception -> 0x08ef }
        r8.append(r11);	 Catch:{ Exception -> 0x08ef }
        r11 = " channel_id = ";
        r8.append(r11);	 Catch:{ Exception -> 0x08ef }
        r11 = r3.channel_id;	 Catch:{ Exception -> 0x08ef }
        r8.append(r11);	 Catch:{ Exception -> 0x08ef }
        r11 = " access_hash = ";
        r8.append(r11);	 Catch:{ Exception -> 0x08ef }
        r60 = r1;
        r1 = r3.access_hash;	 Catch:{ Exception -> 0x08ef }
        r8.append(r1);	 Catch:{ Exception -> 0x08ef }
        r1 = " notify = ";
        r8.append(r1);	 Catch:{ Exception -> 0x08ef }
        r1 = r62;
        r8.append(r1);	 Catch:{ Exception -> 0x08ef }
        r2 = " silent = ";
        r8.append(r2);	 Catch:{ Exception -> 0x08ef }
        r2 = r7.currentAccount;	 Catch:{ Exception -> 0x08ef }
        r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2);	 Catch:{ Exception -> 0x08ef }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x08ef }
        r11.<init>();	 Catch:{ Exception -> 0x08ef }
        r11.append(r12);	 Catch:{ Exception -> 0x08ef }
        r11.append(r13);	 Catch:{ Exception -> 0x08ef }
        r11 = r11.toString();	 Catch:{ Exception -> 0x08ef }
        r51 = r12;
        r12 = 0;
        r2 = r2.getBoolean(r11, r12);	 Catch:{ Exception -> 0x08ef }
        r8.append(r2);	 Catch:{ Exception -> 0x08ef }
        r2 = r8.toString();	 Catch:{ Exception -> 0x08ef }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x08ef }
        goto L_0x0988;
    L_0x0982:
        r60 = r1;
        r51 = r12;
        r1 = r62;
    L_0x0988:
        if (r15 == 0) goto L_0x1703;
    L_0x098a:
        r2 = 9;
        if (r15 != r2) goto L_0x0994;
    L_0x098e:
        if (r48 == 0) goto L_0x0994;
    L_0x0990:
        if (r9 == 0) goto L_0x0994;
    L_0x0992:
        goto L_0x1703;
    L_0x0994:
        r2 = 1;
        if (r15 < r2) goto L_0x099a;
    L_0x0997:
        r2 = 3;
        if (r15 <= r2) goto L_0x09ab;
    L_0x099a:
        r2 = 5;
        if (r15 < r2) goto L_0x09a1;
    L_0x099d:
        r2 = 8;
        if (r15 <= r2) goto L_0x09ab;
    L_0x09a1:
        r2 = 9;
        if (r15 != r2) goto L_0x09a7;
    L_0x09a5:
        if (r9 != 0) goto L_0x09ab;
    L_0x09a7:
        r2 = 10;
        if (r15 != r2) goto L_0x1574;
    L_0x09ab:
        if (r9 != 0) goto L_0x0var_;
    L_0x09ad:
        r2 = 1;
        if (r15 != r2) goto L_0x0a0a;
    L_0x09b0:
        r2 = r28;
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x08ef }
        if (r4 == 0) goto L_0x09d0;
    L_0x09b6:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue;	 Catch:{ Exception -> 0x08ef }
        r4.<init>();	 Catch:{ Exception -> 0x08ef }
        r8 = r2.address;	 Catch:{ Exception -> 0x08ef }
        r4.address = r8;	 Catch:{ Exception -> 0x08ef }
        r8 = r2.title;	 Catch:{ Exception -> 0x08ef }
        r4.title = r8;	 Catch:{ Exception -> 0x08ef }
        r8 = r2.provider;	 Catch:{ Exception -> 0x08ef }
        r4.provider = r8;	 Catch:{ Exception -> 0x08ef }
        r8 = r2.venue_id;	 Catch:{ Exception -> 0x08ef }
        r4.venue_id = r8;	 Catch:{ Exception -> 0x08ef }
        r8 = r27;
        r4.venue_type = r8;	 Catch:{ Exception -> 0x08ef }
        goto L_0x09e9;
    L_0x09d0:
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;	 Catch:{ Exception -> 0x08ef }
        if (r4 == 0) goto L_0x09e4;
    L_0x09d4:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive;	 Catch:{ Exception -> 0x08ef }
        r4.<init>();	 Catch:{ Exception -> 0x08ef }
        r8 = r2.period;	 Catch:{ Exception -> 0x08ef }
        r4.period = r8;	 Catch:{ Exception -> 0x08ef }
        r8 = r4.flags;	 Catch:{ Exception -> 0x08ef }
        r9 = 2;
        r8 = r8 | r9;
        r4.flags = r8;	 Catch:{ Exception -> 0x08ef }
        goto L_0x09e9;
    L_0x09e4:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint;	 Catch:{ Exception -> 0x08ef }
        r4.<init>();	 Catch:{ Exception -> 0x08ef }
    L_0x09e9:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint;	 Catch:{ Exception -> 0x08ef }
        r8.<init>();	 Catch:{ Exception -> 0x08ef }
        r4.geo_point = r8;	 Catch:{ Exception -> 0x08ef }
        r8 = r4.geo_point;	 Catch:{ Exception -> 0x08ef }
        r9 = r2.geo;	 Catch:{ Exception -> 0x08ef }
        r11 = r9.lat;	 Catch:{ Exception -> 0x08ef }
        r8.lat = r11;	 Catch:{ Exception -> 0x08ef }
        r8 = r4.geo_point;	 Catch:{ Exception -> 0x08ef }
        r2 = r2.geo;	 Catch:{ Exception -> 0x08ef }
        r11 = r2._long;	 Catch:{ Exception -> 0x08ef }
        r8._long = r11;	 Catch:{ Exception -> 0x08ef }
        r11 = r56;
        r27 = r3;
        r2 = r4;
        r3 = r5;
        r12 = r20;
        goto L_0x0bbe;
    L_0x0a0a:
        r8 = r27;
        r2 = 2;
        if (r15 == r2) goto L_0x0c8e;
    L_0x0a0f:
        r2 = 9;
        if (r15 != r2) goto L_0x0a17;
    L_0x0a13:
        if (r43 == 0) goto L_0x0a17;
    L_0x0a15:
        goto L_0x0c8e;
    L_0x0a17:
        r2 = 3;
        if (r15 != r2) goto L_0x0adf;
    L_0x0a1a:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x08ef }
        r2.<init>();	 Catch:{ Exception -> 0x08ef }
        r8 = r4.mime_type;	 Catch:{ Exception -> 0x08ef }
        r2.mime_type = r8;	 Catch:{ Exception -> 0x08ef }
        r8 = r4.attributes;	 Catch:{ Exception -> 0x08ef }
        r2.attributes = r8;	 Catch:{ Exception -> 0x08ef }
        r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r4);	 Catch:{ Exception -> 0x08ef }
        if (r8 != 0) goto L_0x0a49;
    L_0x0a2d:
        if (r47 == 0) goto L_0x0a3a;
    L_0x0a2f:
        r11 = r47;
        r8 = r11.muted;	 Catch:{ Exception -> 0x08ef }
        if (r8 != 0) goto L_0x0a4b;
    L_0x0a35:
        r8 = r11.roundVideo;	 Catch:{ Exception -> 0x08ef }
        if (r8 != 0) goto L_0x0a4b;
    L_0x0a39:
        goto L_0x0a3c;
    L_0x0a3a:
        r11 = r47;
    L_0x0a3c:
        r8 = 1;
        r2.nosound_video = r8;	 Catch:{ Exception -> 0x08ef }
        r8 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x08ef }
        if (r8 == 0) goto L_0x0a4b;
    L_0x0a43:
        r8 = "nosound_video = true";
        org.telegram.messenger.FileLog.d(r8);	 Catch:{ Exception -> 0x08ef }
        goto L_0x0a4b;
    L_0x0a49:
        r11 = r47;
    L_0x0a4b:
        if (r46 == 0) goto L_0x0a59;
    L_0x0a4d:
        r9 = r46;
        r2.ttl_seconds = r9;	 Catch:{ Exception -> 0x08ef }
        r5.ttl = r9;	 Catch:{ Exception -> 0x08ef }
        r8 = r2.flags;	 Catch:{ Exception -> 0x08ef }
        r9 = 2;
        r8 = r8 | r9;
        r2.flags = r8;	 Catch:{ Exception -> 0x08ef }
    L_0x0a59:
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x08ef }
        r12 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r12 != 0) goto L_0x0a64;
    L_0x0a5f:
        r9 = r2;
        r47 = r11;
        r8 = 1;
        goto L_0x0a93;
    L_0x0a64:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x08ef }
        r8.<init>();	 Catch:{ Exception -> 0x08ef }
        r9 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x08ef }
        r9.<init>();	 Catch:{ Exception -> 0x08ef }
        r8.id = r9;	 Catch:{ Exception -> 0x08ef }
        r9 = r8.id;	 Catch:{ Exception -> 0x08ef }
        r47 = r11;
        r11 = r4.id;	 Catch:{ Exception -> 0x08ef }
        r9.id = r11;	 Catch:{ Exception -> 0x08ef }
        r9 = r8.id;	 Catch:{ Exception -> 0x08ef }
        r11 = r4.access_hash;	 Catch:{ Exception -> 0x08ef }
        r9.access_hash = r11;	 Catch:{ Exception -> 0x08ef }
        r9 = r8.id;	 Catch:{ Exception -> 0x08ef }
        r11 = r4.file_reference;	 Catch:{ Exception -> 0x08ef }
        r9.file_reference = r11;	 Catch:{ Exception -> 0x08ef }
        r9 = r8.id;	 Catch:{ Exception -> 0x08ef }
        r9 = r9.file_reference;	 Catch:{ Exception -> 0x08ef }
        if (r9 != 0) goto L_0x0a91;
    L_0x0a8a:
        r9 = r8.id;	 Catch:{ Exception -> 0x08ef }
        r11 = 0;
        r12 = new byte[r11];	 Catch:{ Exception -> 0x08ef }
        r9.file_reference = r12;	 Catch:{ Exception -> 0x08ef }
    L_0x0a91:
        r9 = r8;
        r8 = 0;
    L_0x0a93:
        if (r6 != 0) goto L_0x0ab3;
    L_0x0a95:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08ef }
        r6.<init>(r13);	 Catch:{ Exception -> 0x08ef }
        r11 = 1;
        r6.type = r11;	 Catch:{ Exception -> 0x08ef }
        r11 = r56;
        r6.obj = r11;	 Catch:{ Exception -> 0x0b2f }
        r12 = r20;
        r6.originalPath = r12;	 Catch:{ Exception -> 0x0b2f }
        r1 = r65;
        r6.parentObject = r1;	 Catch:{ Exception -> 0x0b2f }
        r43 = r9;
        if (r10 == 0) goto L_0x0aaf;
    L_0x0aad:
        r9 = 1;
        goto L_0x0ab0;
    L_0x0aaf:
        r9 = 0;
    L_0x0ab0:
        r6.scheduled = r9;	 Catch:{ Exception -> 0x0b2f }
        goto L_0x0abb;
    L_0x0ab3:
        r11 = r56;
        r1 = r65;
        r43 = r9;
        r12 = r20;
    L_0x0abb:
        r6.inputUploadMedia = r2;	 Catch:{ Exception -> 0x0b2f }
        r6.performMediaUpload = r8;	 Catch:{ Exception -> 0x0b2f }
        r2 = r4.thumbs;	 Catch:{ Exception -> 0x0b2f }
        r2 = r2.isEmpty();	 Catch:{ Exception -> 0x0b2f }
        if (r2 != 0) goto L_0x0ad4;
    L_0x0ac7:
        r2 = r4.thumbs;	 Catch:{ Exception -> 0x0b2f }
        r9 = 0;
        r2 = r2.get(r9);	 Catch:{ Exception -> 0x0b2f }
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;	 Catch:{ Exception -> 0x0b2f }
        r6.photoSize = r2;	 Catch:{ Exception -> 0x0b2f }
        r6.locationParent = r4;	 Catch:{ Exception -> 0x0b2f }
    L_0x0ad4:
        r2 = r47;
        r6.videoEditedInfo = r2;	 Catch:{ Exception -> 0x0b2f }
        r2 = r43;
        r27 = r3;
    L_0x0adc:
        r3 = r5;
        goto L_0x0d6c;
    L_0x0adf:
        r9 = r46;
        r11 = r56;
        r1 = r65;
        r12 = r20;
        r2 = 6;
        if (r15 != r2) goto L_0x0b32;
    L_0x0aea:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact;	 Catch:{ Exception -> 0x0b2f }
        r2.<init>();	 Catch:{ Exception -> 0x0b2f }
        r4 = r45;
        r9 = r4.phone;	 Catch:{ Exception -> 0x0b2f }
        r2.phone_number = r9;	 Catch:{ Exception -> 0x0b2f }
        r9 = r4.first_name;	 Catch:{ Exception -> 0x0b2f }
        r2.first_name = r9;	 Catch:{ Exception -> 0x0b2f }
        r9 = r4.last_name;	 Catch:{ Exception -> 0x0b2f }
        r2.last_name = r9;	 Catch:{ Exception -> 0x0b2f }
        r9 = r4.restriction_reason;	 Catch:{ Exception -> 0x0b2f }
        r9 = r9.isEmpty();	 Catch:{ Exception -> 0x0b2f }
        if (r9 != 0) goto L_0x0b29;
    L_0x0b05:
        r9 = r4.restriction_reason;	 Catch:{ Exception -> 0x0b2f }
        r27 = r3;
        r3 = 0;
        r9 = r9.get(r3);	 Catch:{ Exception -> 0x0b2f }
        r9 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r9;	 Catch:{ Exception -> 0x0b2f }
        r3 = r9.text;	 Catch:{ Exception -> 0x0b2f }
        r9 = "BEGIN:VCARD";
        r3 = r3.startsWith(r9);	 Catch:{ Exception -> 0x0b2f }
        if (r3 == 0) goto L_0x0b2b;
    L_0x0b1a:
        r3 = r4.restriction_reason;	 Catch:{ Exception -> 0x0b2f }
        r4 = 0;
        r3 = r3.get(r4);	 Catch:{ Exception -> 0x0b2f }
        r3 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r3;	 Catch:{ Exception -> 0x0b2f }
        r3 = r3.text;	 Catch:{ Exception -> 0x0b2f }
        r2.vcard = r3;	 Catch:{ Exception -> 0x0b2f }
        goto L_0x0bba;
    L_0x0b29:
        r27 = r3;
    L_0x0b2b:
        r2.vcard = r8;	 Catch:{ Exception -> 0x0b2f }
        goto L_0x0bba;
    L_0x0b2f:
        r0 = move-exception;
        goto L_0x08c5;
    L_0x0b32:
        r27 = r3;
        r2 = 7;
        if (r15 == r2) goto L_0x0bc1;
    L_0x0b37:
        r2 = 9;
        if (r15 != r2) goto L_0x0b3d;
    L_0x0b3b:
        goto L_0x0bc1;
    L_0x0b3d:
        r2 = 8;
        if (r15 != r2) goto L_0x0bab;
    L_0x0b41:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0b2f }
        r2.<init>();	 Catch:{ Exception -> 0x0b2f }
        r3 = r4.mime_type;	 Catch:{ Exception -> 0x0b2f }
        r2.mime_type = r3;	 Catch:{ Exception -> 0x0b2f }
        r3 = r4.attributes;	 Catch:{ Exception -> 0x0b2f }
        r2.attributes = r3;	 Catch:{ Exception -> 0x0b2f }
        if (r9 == 0) goto L_0x0b5a;
    L_0x0b50:
        r2.ttl_seconds = r9;	 Catch:{ Exception -> 0x0b2f }
        r5.ttl = r9;	 Catch:{ Exception -> 0x0b2f }
        r3 = r2.flags;	 Catch:{ Exception -> 0x0b2f }
        r6 = 2;
        r3 = r3 | r6;
        r2.flags = r3;	 Catch:{ Exception -> 0x0b2f }
    L_0x0b5a:
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x0b2f }
        r3 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r3 != 0) goto L_0x0b63;
    L_0x0b60:
        r4 = r2;
        r3 = 1;
        goto L_0x0b90;
    L_0x0b63:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0b2f }
        r3.<init>();	 Catch:{ Exception -> 0x0b2f }
        r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0b2f }
        r6.<init>();	 Catch:{ Exception -> 0x0b2f }
        r3.id = r6;	 Catch:{ Exception -> 0x0b2f }
        r6 = r3.id;	 Catch:{ Exception -> 0x0b2f }
        r8 = r4.id;	 Catch:{ Exception -> 0x0b2f }
        r6.id = r8;	 Catch:{ Exception -> 0x0b2f }
        r6 = r3.id;	 Catch:{ Exception -> 0x0b2f }
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x0b2f }
        r6.access_hash = r8;	 Catch:{ Exception -> 0x0b2f }
        r6 = r3.id;	 Catch:{ Exception -> 0x0b2f }
        r4 = r4.file_reference;	 Catch:{ Exception -> 0x0b2f }
        r6.file_reference = r4;	 Catch:{ Exception -> 0x0b2f }
        r4 = r3.id;	 Catch:{ Exception -> 0x0b2f }
        r4 = r4.file_reference;	 Catch:{ Exception -> 0x0b2f }
        if (r4 != 0) goto L_0x0b8e;
    L_0x0b87:
        r4 = r3.id;	 Catch:{ Exception -> 0x0b2f }
        r6 = 0;
        r8 = new byte[r6];	 Catch:{ Exception -> 0x0b2f }
        r4.file_reference = r8;	 Catch:{ Exception -> 0x0b2f }
    L_0x0b8e:
        r4 = r3;
        r3 = 0;
    L_0x0b90:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0b2f }
        r6.<init>(r13);	 Catch:{ Exception -> 0x0b2f }
        r8 = 3;
        r6.type = r8;	 Catch:{ Exception -> 0x0b2f }
        r6.obj = r11;	 Catch:{ Exception -> 0x0b2f }
        r6.parentObject = r1;	 Catch:{ Exception -> 0x0b2f }
        r6.inputUploadMedia = r2;	 Catch:{ Exception -> 0x0b2f }
        r6.performMediaUpload = r3;	 Catch:{ Exception -> 0x0b2f }
        if (r10 == 0) goto L_0x0ba4;
    L_0x0ba2:
        r2 = 1;
        goto L_0x0ba5;
    L_0x0ba4:
        r2 = 0;
    L_0x0ba5:
        r6.scheduled = r2;	 Catch:{ Exception -> 0x0b2f }
        r8 = r3;
        r2 = r4;
        goto L_0x0adc;
    L_0x0bab:
        r2 = 10;
        if (r15 != r2) goto L_0x0bbc;
    L_0x0baf:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll;	 Catch:{ Exception -> 0x0b2f }
        r2.<init>();	 Catch:{ Exception -> 0x0b2f }
        r8 = r18;
        r3 = r8.poll;	 Catch:{ Exception -> 0x0b2f }
        r2.poll = r3;	 Catch:{ Exception -> 0x0b2f }
    L_0x0bba:
        r3 = r5;
        goto L_0x0bbe;
    L_0x0bbc:
        r3 = r5;
        r2 = 0;
    L_0x0bbe:
        r8 = 0;
        goto L_0x0d6c;
    L_0x0bc1:
        if (r12 != 0) goto L_0x0bd0;
    L_0x0bc3:
        r3 = r54;
        if (r3 != 0) goto L_0x0bd0;
    L_0x0bc7:
        r2 = r4.access_hash;	 Catch:{ Exception -> 0x0b2f }
        r8 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1));
        if (r8 != 0) goto L_0x0bce;
    L_0x0bcd:
        goto L_0x0bd0;
    L_0x0bce:
        r2 = 0;
        goto L_0x0c0a;
    L_0x0bd0:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0CLASSNAME }
        r2.<init>();	 Catch:{ Exception -> 0x0CLASSNAME }
        if (r9 == 0) goto L_0x0be1;
    L_0x0bd7:
        r2.ttl_seconds = r9;	 Catch:{ Exception -> 0x0b2f }
        r5.ttl = r9;	 Catch:{ Exception -> 0x0b2f }
        r3 = r2.flags;	 Catch:{ Exception -> 0x0b2f }
        r8 = 2;
        r3 = r3 | r8;
        r2.flags = r3;	 Catch:{ Exception -> 0x0b2f }
    L_0x0be1:
        r3 = android.text.TextUtils.isEmpty(r54);	 Catch:{ Exception -> 0x0CLASSNAME }
        if (r3 != 0) goto L_0x0CLASSNAME;
    L_0x0be7:
        r3 = r54.toLowerCase();	 Catch:{ Exception -> 0x0b2f }
        r8 = "mp4";
        r3 = r3.endsWith(r8);	 Catch:{ Exception -> 0x0b2f }
        if (r3 == 0) goto L_0x0CLASSNAME;
    L_0x0bf3:
        if (r49 == 0) goto L_0x0bff;
    L_0x0bf5:
        r3 = "forceDocument";
        r8 = r49;
        r3 = r8.containsKey(r3);	 Catch:{ Exception -> 0x0b2f }
        if (r3 == 0) goto L_0x0CLASSNAME;
    L_0x0bff:
        r3 = 1;
        r2.nosound_video = r3;	 Catch:{ Exception -> 0x0b2f }
    L_0x0CLASSNAME:
        r3 = r4.mime_type;	 Catch:{ Exception -> 0x0CLASSNAME }
        r2.mime_type = r3;	 Catch:{ Exception -> 0x0CLASSNAME }
        r3 = r4.attributes;	 Catch:{ Exception -> 0x0CLASSNAME }
        r2.attributes = r3;	 Catch:{ Exception -> 0x0CLASSNAME }
    L_0x0c0a:
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x0CLASSNAME }
        r3 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r3 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0b2f }
        r18 = r5;
        r55 = r6;
        r5 = r2;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0CLASSNAME }
        r3.<init>();	 Catch:{ Exception -> 0x0CLASSNAME }
        r8 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0CLASSNAME }
        r8.<init>();	 Catch:{ Exception -> 0x0CLASSNAME }
        r3.id = r8;	 Catch:{ Exception -> 0x0CLASSNAME }
        r8 = r3.id;	 Catch:{ Exception -> 0x0CLASSNAME }
        r18 = r5;
        r55 = r6;
        r5 = r4.id;	 Catch:{ Exception -> 0x0c7a }
        r8.id = r5;	 Catch:{ Exception -> 0x0c7a }
        r5 = r3.id;	 Catch:{ Exception -> 0x0c7a }
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x0c7a }
        r5.access_hash = r8;	 Catch:{ Exception -> 0x0c7a }
        r5 = r3.id;	 Catch:{ Exception -> 0x0c7a }
        r6 = r4.file_reference;	 Catch:{ Exception -> 0x0c7a }
        r5.file_reference = r6;	 Catch:{ Exception -> 0x0c7a }
        r5 = r3.id;	 Catch:{ Exception -> 0x0c7a }
        r5 = r5.file_reference;	 Catch:{ Exception -> 0x0c7a }
        if (r5 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r5 = r3.id;	 Catch:{ Exception -> 0x0c7a }
        r6 = 0;
        r8 = new byte[r6];	 Catch:{ Exception -> 0x0c7a }
        r5.file_reference = r8;	 Catch:{ Exception -> 0x0c7a }
    L_0x0CLASSNAME:
        r5 = r3;
        r3 = 0;
    L_0x0CLASSNAME:
        if (r2 == 0) goto L_0x0CLASSNAME;
    L_0x0c4b:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c7a }
        r6.<init>(r13);	 Catch:{ Exception -> 0x0c7a }
        r6.originalPath = r12;	 Catch:{ Exception -> 0x0c7a }
        r8 = 2;
        r6.type = r8;	 Catch:{ Exception -> 0x0c7a }
        r6.obj = r11;	 Catch:{ Exception -> 0x0c7a }
        r8 = r4.thumbs;	 Catch:{ Exception -> 0x0c7a }
        r8 = r8.isEmpty();	 Catch:{ Exception -> 0x0c7a }
        if (r8 != 0) goto L_0x0c6c;
    L_0x0c5f:
        r8 = r4.thumbs;	 Catch:{ Exception -> 0x0c7a }
        r9 = 0;
        r8 = r8.get(r9);	 Catch:{ Exception -> 0x0c7a }
        r8 = (org.telegram.tgnet.TLRPC.PhotoSize) r8;	 Catch:{ Exception -> 0x0c7a }
        r6.photoSize = r8;	 Catch:{ Exception -> 0x0c7a }
        r6.locationParent = r4;	 Catch:{ Exception -> 0x0c7a }
    L_0x0c6c:
        r6.parentObject = r1;	 Catch:{ Exception -> 0x0c7a }
        r6.inputUploadMedia = r2;	 Catch:{ Exception -> 0x0c7a }
        r6.performMediaUpload = r3;	 Catch:{ Exception -> 0x0c7a }
        if (r10 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = 1;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = 0;
    L_0x0CLASSNAME:
        r6.scheduled = r2;	 Catch:{ Exception -> 0x0c7a }
        goto L_0x0CLASSNAME;
    L_0x0c7a:
        r0 = move-exception;
        r2 = r0;
        r1 = r10;
    L_0x0c7d:
        r5 = r18;
        goto L_0x1877;
    L_0x0CLASSNAME:
        r6 = r55;
    L_0x0CLASSNAME:
        r8 = r3;
        r2 = r5;
        r3 = r18;
        goto L_0x0d6c;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r18 = r5;
        goto L_0x08c5;
    L_0x0c8e:
        r9 = r46;
        r8 = r49;
        r11 = r56;
        r1 = r65;
        r27 = r3;
        r18 = r5;
        r55 = r6;
        r12 = r20;
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x0f8a }
        r2.<init>();	 Catch:{ Exception -> 0x0f8a }
        if (r9 == 0) goto L_0x0cb2;
    L_0x0ca5:
        r2.ttl_seconds = r9;	 Catch:{ Exception -> 0x0f8a }
        r3 = r18;
        r3.ttl = r9;	 Catch:{ Exception -> 0x0var_ }
        r4 = r2.flags;	 Catch:{ Exception -> 0x0var_ }
        r5 = 2;
        r4 = r4 | r5;
        r2.flags = r4;	 Catch:{ Exception -> 0x0var_ }
        goto L_0x0cb4;
    L_0x0cb2:
        r3 = r18;
    L_0x0cb4:
        if (r8 == 0) goto L_0x0ced;
    L_0x0cb6:
        r4 = "masks";
        r4 = r8.get(r4);	 Catch:{ Exception -> 0x0var_ }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0var_ }
        if (r4 == 0) goto L_0x0ced;
    L_0x0cc0:
        r5 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0var_ }
        r4 = org.telegram.messenger.Utilities.hexToBytes(r4);	 Catch:{ Exception -> 0x0var_ }
        r5.<init>(r4);	 Catch:{ Exception -> 0x0var_ }
        r4 = 0;
        r6 = r5.readInt32(r4);	 Catch:{ Exception -> 0x0var_ }
        r8 = 0;
    L_0x0ccf:
        if (r8 >= r6) goto L_0x0ce4;
    L_0x0cd1:
        r9 = r2.stickers;	 Catch:{ Exception -> 0x0var_ }
        r1 = r5.readInt32(r4);	 Catch:{ Exception -> 0x0var_ }
        r1 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r5, r1, r4);	 Catch:{ Exception -> 0x0var_ }
        r9.add(r1);	 Catch:{ Exception -> 0x0var_ }
        r8 = r8 + 1;
        r1 = r65;
        r4 = 0;
        goto L_0x0ccf;
    L_0x0ce4:
        r1 = r2.flags;	 Catch:{ Exception -> 0x0var_ }
        r4 = 1;
        r1 = r1 | r4;
        r2.flags = r1;	 Catch:{ Exception -> 0x0var_ }
        r5.cleanup();	 Catch:{ Exception -> 0x0var_ }
    L_0x0ced:
        r1 = r43;
        r4 = r1.access_hash;	 Catch:{ Exception -> 0x0var_ }
        r6 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1));
        if (r6 != 0) goto L_0x0cf8;
    L_0x0cf5:
        r5 = r2;
        r4 = 1;
        goto L_0x0d25;
    L_0x0cf8:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x0var_ }
        r4.<init>();	 Catch:{ Exception -> 0x0var_ }
        r5 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x0var_ }
        r5.<init>();	 Catch:{ Exception -> 0x0var_ }
        r4.id = r5;	 Catch:{ Exception -> 0x0var_ }
        r5 = r4.id;	 Catch:{ Exception -> 0x0var_ }
        r8 = r1.id;	 Catch:{ Exception -> 0x0var_ }
        r5.id = r8;	 Catch:{ Exception -> 0x0var_ }
        r5 = r4.id;	 Catch:{ Exception -> 0x0var_ }
        r8 = r1.access_hash;	 Catch:{ Exception -> 0x0var_ }
        r5.access_hash = r8;	 Catch:{ Exception -> 0x0var_ }
        r5 = r4.id;	 Catch:{ Exception -> 0x0var_ }
        r6 = r1.file_reference;	 Catch:{ Exception -> 0x0var_ }
        r5.file_reference = r6;	 Catch:{ Exception -> 0x0var_ }
        r5 = r4.id;	 Catch:{ Exception -> 0x0var_ }
        r5 = r5.file_reference;	 Catch:{ Exception -> 0x0var_ }
        if (r5 != 0) goto L_0x0d23;
    L_0x0d1c:
        r5 = r4.id;	 Catch:{ Exception -> 0x0var_ }
        r6 = 0;
        r8 = new byte[r6];	 Catch:{ Exception -> 0x0var_ }
        r5.file_reference = r8;	 Catch:{ Exception -> 0x0var_ }
    L_0x0d23:
        r5 = r4;
        r4 = 0;
    L_0x0d25:
        if (r55 != 0) goto L_0x0d3b;
    L_0x0d27:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0var_ }
        r6.<init>(r13);	 Catch:{ Exception -> 0x0var_ }
        r8 = 0;
        r6.type = r8;	 Catch:{ Exception -> 0x0var_ }
        r6.obj = r11;	 Catch:{ Exception -> 0x0var_ }
        r6.originalPath = r12;	 Catch:{ Exception -> 0x0var_ }
        if (r10 == 0) goto L_0x0d37;
    L_0x0d35:
        r8 = 1;
        goto L_0x0d38;
    L_0x0d37:
        r8 = 0;
    L_0x0d38:
        r6.scheduled = r8;	 Catch:{ Exception -> 0x0var_ }
        goto L_0x0d3d;
    L_0x0d3b:
        r6 = r55;
    L_0x0d3d:
        r6.inputUploadMedia = r2;	 Catch:{ Exception -> 0x0var_ }
        r6.performMediaUpload = r4;	 Catch:{ Exception -> 0x0var_ }
        r2 = r54;
        if (r2 == 0) goto L_0x0d56;
    L_0x0d45:
        r8 = r54.length();	 Catch:{ Exception -> 0x0var_ }
        if (r8 <= 0) goto L_0x0d56;
    L_0x0d4b:
        r8 = "http";
        r8 = r2.startsWith(r8);	 Catch:{ Exception -> 0x0var_ }
        if (r8 == 0) goto L_0x0d56;
    L_0x0d53:
        r6.httpLocation = r2;	 Catch:{ Exception -> 0x0var_ }
        goto L_0x0d6a;
    L_0x0d56:
        r2 = r1.sizes;	 Catch:{ Exception -> 0x0var_ }
        r8 = r1.sizes;	 Catch:{ Exception -> 0x0var_ }
        r8 = r8.size();	 Catch:{ Exception -> 0x0var_ }
        r9 = 1;
        r8 = r8 - r9;
        r2 = r2.get(r8);	 Catch:{ Exception -> 0x0var_ }
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;	 Catch:{ Exception -> 0x0var_ }
        r6.photoSize = r2;	 Catch:{ Exception -> 0x0var_ }
        r6.locationParent = r1;	 Catch:{ Exception -> 0x0var_ }
    L_0x0d6a:
        r8 = r4;
        r2 = r5;
    L_0x0d6c:
        r1 = (r60 > r16 ? 1 : (r60 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x0e2c;
    L_0x0d70:
        r1 = r6.sendRequest;	 Catch:{ Exception -> 0x0var_ }
        if (r1 == 0) goto L_0x0d7d;
    L_0x0d74:
        r1 = r6.sendRequest;	 Catch:{ Exception -> 0x0var_ }
        r1 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r1;	 Catch:{ Exception -> 0x0var_ }
        r5 = r65;
        r43 = r15;
        goto L_0x0dcc;
    L_0x0d7d:
        r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;	 Catch:{ Exception -> 0x0var_ }
        r1.<init>();	 Catch:{ Exception -> 0x0var_ }
        r4 = r27;
        r1.peer = r4;	 Catch:{ Exception -> 0x0var_ }
        r5 = r65;
        if (r62 == 0) goto L_0x0dad;
    L_0x0d8a:
        r4 = r7.currentAccount;	 Catch:{ Exception -> 0x0var_ }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0var_ }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0var_ }
        r9.<init>();	 Catch:{ Exception -> 0x0var_ }
        r43 = r15;
        r15 = r51;
        r9.append(r15);	 Catch:{ Exception -> 0x0var_ }
        r9.append(r13);	 Catch:{ Exception -> 0x0var_ }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0var_ }
        r13 = 0;
        r4 = r4.getBoolean(r9, r13);	 Catch:{ Exception -> 0x0var_ }
        if (r4 == 0) goto L_0x0dab;
    L_0x0daa:
        goto L_0x0daf;
    L_0x0dab:
        r4 = 0;
        goto L_0x0db0;
    L_0x0dad:
        r43 = r15;
    L_0x0daf:
        r4 = 1;
    L_0x0db0:
        r1.silent = r4;	 Catch:{ Exception -> 0x0var_ }
        r4 = r3.reply_to_msg_id;	 Catch:{ Exception -> 0x0var_ }
        if (r4 == 0) goto L_0x0dc0;
    L_0x0db6:
        r4 = r1.flags;	 Catch:{ Exception -> 0x0var_ }
        r9 = 1;
        r4 = r4 | r9;
        r1.flags = r4;	 Catch:{ Exception -> 0x0var_ }
        r4 = r3.reply_to_msg_id;	 Catch:{ Exception -> 0x0var_ }
        r1.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0var_ }
    L_0x0dc0:
        if (r10 == 0) goto L_0x0dca;
    L_0x0dc2:
        r1.schedule_date = r10;	 Catch:{ Exception -> 0x0var_ }
        r4 = r1.flags;	 Catch:{ Exception -> 0x0var_ }
        r4 = r4 | 1024;
        r1.flags = r4;	 Catch:{ Exception -> 0x0var_ }
    L_0x0dca:
        r6.sendRequest = r1;	 Catch:{ Exception -> 0x0var_ }
    L_0x0dcc:
        r4 = r6.messageObjects;	 Catch:{ Exception -> 0x0var_ }
        r4.add(r11);	 Catch:{ Exception -> 0x0var_ }
        r4 = r6.parentObjects;	 Catch:{ Exception -> 0x0var_ }
        r4.add(r5);	 Catch:{ Exception -> 0x0var_ }
        r4 = r6.locations;	 Catch:{ Exception -> 0x0var_ }
        r9 = r6.photoSize;	 Catch:{ Exception -> 0x0var_ }
        r4.add(r9);	 Catch:{ Exception -> 0x0var_ }
        r4 = r6.videoEditedInfos;	 Catch:{ Exception -> 0x0var_ }
        r9 = r6.videoEditedInfo;	 Catch:{ Exception -> 0x0var_ }
        r4.add(r9);	 Catch:{ Exception -> 0x0var_ }
        r4 = r6.httpLocations;	 Catch:{ Exception -> 0x0var_ }
        r9 = r6.httpLocation;	 Catch:{ Exception -> 0x0var_ }
        r4.add(r9);	 Catch:{ Exception -> 0x0var_ }
        r4 = r6.inputMedias;	 Catch:{ Exception -> 0x0var_ }
        r9 = r6.inputUploadMedia;	 Catch:{ Exception -> 0x0var_ }
        r4.add(r9);	 Catch:{ Exception -> 0x0var_ }
        r4 = r6.messages;	 Catch:{ Exception -> 0x0var_ }
        r4.add(r3);	 Catch:{ Exception -> 0x0var_ }
        r4 = r6.originalPaths;	 Catch:{ Exception -> 0x0var_ }
        r4.add(r12);	 Catch:{ Exception -> 0x0var_ }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia;	 Catch:{ Exception -> 0x0var_ }
        r4.<init>();	 Catch:{ Exception -> 0x0var_ }
        r13 = r3.random_id;	 Catch:{ Exception -> 0x0var_ }
        r4.random_id = r13;	 Catch:{ Exception -> 0x0var_ }
        r4.media = r2;	 Catch:{ Exception -> 0x0var_ }
        r9 = r50;
        r4.message = r9;	 Catch:{ Exception -> 0x0var_ }
        r9 = r43;
        r2 = r59;
        if (r2 == 0) goto L_0x0e1f;
    L_0x0e11:
        r13 = r59.isEmpty();	 Catch:{ Exception -> 0x0var_ }
        if (r13 != 0) goto L_0x0e1f;
    L_0x0e17:
        r4.entities = r2;	 Catch:{ Exception -> 0x0var_ }
        r2 = r4.flags;	 Catch:{ Exception -> 0x0var_ }
        r13 = 1;
        r2 = r2 | r13;
        r4.flags = r2;	 Catch:{ Exception -> 0x0var_ }
    L_0x0e1f:
        r2 = r1.multi_media;	 Catch:{ Exception -> 0x0var_ }
        r2.add(r4);	 Catch:{ Exception -> 0x0var_ }
        r43 = r8;
        r5 = r9;
        r20 = r12;
        r12 = r1;
        goto L_0x0e9b;
    L_0x0e2c:
        r9 = r50;
        r1 = r59;
        r20 = r12;
        r5 = r15;
        r4 = r27;
        r15 = r51;
        r12 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia;	 Catch:{ Exception -> 0x0var_ }
        r12.<init>();	 Catch:{ Exception -> 0x0var_ }
        r12.peer = r4;	 Catch:{ Exception -> 0x0var_ }
        if (r62 == 0) goto L_0x0e61;
    L_0x0e40:
        r4 = r7.currentAccount;	 Catch:{ Exception -> 0x0var_ }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0var_ }
        r43 = r8;
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0var_ }
        r8.<init>();	 Catch:{ Exception -> 0x0var_ }
        r8.append(r15);	 Catch:{ Exception -> 0x0var_ }
        r8.append(r13);	 Catch:{ Exception -> 0x0var_ }
        r8 = r8.toString();	 Catch:{ Exception -> 0x0var_ }
        r13 = 0;
        r4 = r4.getBoolean(r8, r13);	 Catch:{ Exception -> 0x0var_ }
        if (r4 == 0) goto L_0x0e5f;
    L_0x0e5e:
        goto L_0x0e63;
    L_0x0e5f:
        r4 = 0;
        goto L_0x0e64;
    L_0x0e61:
        r43 = r8;
    L_0x0e63:
        r4 = 1;
    L_0x0e64:
        r12.silent = r4;	 Catch:{ Exception -> 0x0var_ }
        r4 = r3.reply_to_msg_id;	 Catch:{ Exception -> 0x0var_ }
        if (r4 == 0) goto L_0x0e74;
    L_0x0e6a:
        r4 = r12.flags;	 Catch:{ Exception -> 0x0var_ }
        r8 = 1;
        r4 = r4 | r8;
        r12.flags = r4;	 Catch:{ Exception -> 0x0var_ }
        r4 = r3.reply_to_msg_id;	 Catch:{ Exception -> 0x0var_ }
        r12.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0var_ }
    L_0x0e74:
        r13 = r3.random_id;	 Catch:{ Exception -> 0x0var_ }
        r12.random_id = r13;	 Catch:{ Exception -> 0x0var_ }
        r12.media = r2;	 Catch:{ Exception -> 0x0var_ }
        r12.message = r9;	 Catch:{ Exception -> 0x0var_ }
        if (r1 == 0) goto L_0x0e8d;
    L_0x0e7e:
        r2 = r59.isEmpty();	 Catch:{ Exception -> 0x0var_ }
        if (r2 != 0) goto L_0x0e8d;
    L_0x0e84:
        r12.entities = r1;	 Catch:{ Exception -> 0x0var_ }
        r1 = r12.flags;	 Catch:{ Exception -> 0x0var_ }
        r2 = 8;
        r1 = r1 | r2;
        r12.flags = r1;	 Catch:{ Exception -> 0x0var_ }
    L_0x0e8d:
        if (r10 == 0) goto L_0x0e97;
    L_0x0e8f:
        r12.schedule_date = r10;	 Catch:{ Exception -> 0x0var_ }
        r1 = r12.flags;	 Catch:{ Exception -> 0x0var_ }
        r1 = r1 | 1024;
        r12.flags = r1;	 Catch:{ Exception -> 0x0var_ }
    L_0x0e97:
        if (r6 == 0) goto L_0x0e9b;
    L_0x0e99:
        r6.sendRequest = r12;	 Catch:{ Exception -> 0x0var_ }
    L_0x0e9b:
        r1 = (r60 > r16 ? 1 : (r60 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x0ea4;
    L_0x0e9f:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0ea4:
        r1 = 1;
        if (r5 != r1) goto L_0x0ec0;
    L_0x0ea7:
        r1 = 0;
        if (r10 == 0) goto L_0x0eac;
    L_0x0eaa:
        r2 = 1;
        goto L_0x0ead;
    L_0x0eac:
        r2 = 0;
    L_0x0ead:
        r43 = r42;
        r44 = r12;
        r45 = r11;
        r46 = r1;
        r47 = r6;
        r48 = r65;
        r49 = r2;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0ec0:
        r1 = 2;
        if (r5 != r1) goto L_0x0ee8;
    L_0x0ec3:
        if (r43 == 0) goto L_0x0eca;
    L_0x0ec5:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0eca:
        r1 = 0;
        r2 = 1;
        if (r10 == 0) goto L_0x0ed0;
    L_0x0ece:
        r4 = 1;
        goto L_0x0ed1;
    L_0x0ed0:
        r4 = 0;
    L_0x0ed1:
        r43 = r42;
        r44 = r12;
        r45 = r11;
        r46 = r20;
        r47 = r1;
        r48 = r2;
        r49 = r6;
        r50 = r65;
        r51 = r4;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0ee8:
        r1 = 3;
        if (r5 != r1) goto L_0x0f0a;
    L_0x0eeb:
        if (r43 == 0) goto L_0x0ef2;
    L_0x0eed:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0ef2:
        if (r10 == 0) goto L_0x0ef6;
    L_0x0ef4:
        r1 = 1;
        goto L_0x0ef7;
    L_0x0ef6:
        r1 = 0;
    L_0x0ef7:
        r43 = r42;
        r44 = r12;
        r45 = r11;
        r46 = r20;
        r47 = r6;
        r48 = r65;
        r49 = r1;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0f0a:
        r1 = 6;
        if (r5 != r1) goto L_0x0var_;
    L_0x0f0d:
        if (r10 == 0) goto L_0x0var_;
    L_0x0f0f:
        r1 = 1;
        goto L_0x0var_;
    L_0x0var_:
        r1 = 0;
    L_0x0var_:
        r43 = r42;
        r44 = r12;
        r45 = r11;
        r46 = r20;
        r47 = r6;
        r48 = r65;
        r49 = r1;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0var_:
        r1 = 7;
        if (r5 != r1) goto L_0x0var_;
    L_0x0var_:
        if (r43 == 0) goto L_0x0var_;
    L_0x0f2a:
        if (r6 == 0) goto L_0x0var_;
    L_0x0f2c:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0var_:
        if (r10 == 0) goto L_0x0var_;
    L_0x0var_:
        r1 = 1;
        goto L_0x0var_;
    L_0x0var_:
        r1 = 0;
    L_0x0var_:
        r43 = r42;
        r44 = r12;
        r45 = r11;
        r46 = r20;
        r47 = r6;
        r48 = r65;
        r49 = r1;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0var_:
        r1 = 8;
        if (r5 != r1) goto L_0x0f6c;
    L_0x0f4d:
        if (r43 == 0) goto L_0x0var_;
    L_0x0f4f:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0var_:
        if (r10 == 0) goto L_0x0var_;
    L_0x0var_:
        r1 = 1;
        goto L_0x0var_;
    L_0x0var_:
        r1 = 0;
    L_0x0var_:
        r43 = r42;
        r44 = r12;
        r45 = r11;
        r46 = r20;
        r47 = r6;
        r48 = r65;
        r49 = r1;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0f6c:
        r1 = 10;
        if (r5 != r1) goto L_0x18a7;
    L_0x0var_:
        if (r10 == 0) goto L_0x0var_;
    L_0x0var_:
        r1 = 1;
        goto L_0x0var_;
    L_0x0var_:
        r1 = 0;
    L_0x0var_:
        r43 = r42;
        r44 = r12;
        r45 = r11;
        r46 = r20;
        r47 = r6;
        r48 = r65;
        r49 = r1;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49);	 Catch:{ Exception -> 0x0var_ }
        goto L_0x18a7;
    L_0x0var_:
        r0 = move-exception;
        goto L_0x0f8d;
    L_0x0f8a:
        r0 = move-exception;
        r3 = r18;
    L_0x0f8d:
        r2 = r0;
        r5 = r3;
        goto L_0x0823;
    L_0x0var_:
        r1 = r43;
        r40 = r45;
        r39 = r47;
        r11 = r56;
        r12 = r59;
        r3 = r5;
        r55 = r6;
        r5 = r15;
        r8 = r27;
        r2 = r28;
        r15 = r49;
        r6 = r50;
        r10 = r9.layer;	 Catch:{ Exception -> 0x156c }
        r10 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r10);	 Catch:{ Exception -> 0x156c }
        r13 = 73;
        if (r10 < r13) goto L_0x0fcd;
    L_0x0fb1:
        r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x0fc6 }
        r10.<init>();	 Catch:{ Exception -> 0x0fc6 }
        r13 = (r60 > r16 ? 1 : (r60 == r16 ? 0 : -1));
        if (r13 == 0) goto L_0x0fd2;
    L_0x0fba:
        r13 = r60;
        r10.grouped_id = r13;	 Catch:{ Exception -> 0x0fc6 }
        r13 = r10.flags;	 Catch:{ Exception -> 0x0fc6 }
        r14 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r13 = r13 | r14;
        r10.flags = r13;	 Catch:{ Exception -> 0x0fc6 }
        goto L_0x0fd2;
    L_0x0fc6:
        r0 = move-exception;
        r1 = r63;
    L_0x0fc9:
        r2 = r0;
        r5 = r3;
        goto L_0x1877;
    L_0x0fcd:
        r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x156c }
        r10.<init>();	 Catch:{ Exception -> 0x156c }
    L_0x0fd2:
        r13 = r3.ttl;	 Catch:{ Exception -> 0x156c }
        r10.ttl = r13;	 Catch:{ Exception -> 0x156c }
        if (r12 == 0) goto L_0x0fe6;
    L_0x0fd8:
        r13 = r59.isEmpty();	 Catch:{ Exception -> 0x0fc6 }
        if (r13 != 0) goto L_0x0fe6;
    L_0x0fde:
        r10.entities = r12;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r10.flags;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r12 | 128;
        r10.flags = r12;	 Catch:{ Exception -> 0x0fc6 }
    L_0x0fe6:
        r12 = r3.reply_to_random_id;	 Catch:{ Exception -> 0x156c }
        r14 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
        if (r14 == 0) goto L_0x0ff7;
    L_0x0fec:
        r12 = r3.reply_to_random_id;	 Catch:{ Exception -> 0x0fc6 }
        r10.reply_to_random_id = r12;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r10.flags;	 Catch:{ Exception -> 0x0fc6 }
        r13 = 8;
        r12 = r12 | r13;
        r10.flags = r12;	 Catch:{ Exception -> 0x0fc6 }
    L_0x0ff7:
        r12 = r10.flags;	 Catch:{ Exception -> 0x156c }
        r12 = r12 | 512;
        r10.flags = r12;	 Catch:{ Exception -> 0x156c }
        if (r15 == 0) goto L_0x1017;
    L_0x0fff:
        r12 = "bot_name";
        r12 = r15.get(r12);	 Catch:{ Exception -> 0x0fc6 }
        if (r12 == 0) goto L_0x1017;
    L_0x1007:
        r12 = "bot_name";
        r12 = r15.get(r12);	 Catch:{ Exception -> 0x0fc6 }
        r12 = (java.lang.String) r12;	 Catch:{ Exception -> 0x0fc6 }
        r10.via_bot_name = r12;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r10.flags;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r12 | 2048;
        r10.flags = r12;	 Catch:{ Exception -> 0x0fc6 }
    L_0x1017:
        r12 = r3.random_id;	 Catch:{ Exception -> 0x156c }
        r10.random_id = r12;	 Catch:{ Exception -> 0x156c }
        r10.message = r8;	 Catch:{ Exception -> 0x156c }
        r8 = 1;
        if (r5 != r8) goto L_0x1080;
    L_0x1020:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x0fc6 }
        if (r1 == 0) goto L_0x1044;
    L_0x1024:
        r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue;	 Catch:{ Exception -> 0x0fc6 }
        r1.<init>();	 Catch:{ Exception -> 0x0fc6 }
        r10.media = r1;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r4 = r2.address;	 Catch:{ Exception -> 0x0fc6 }
        r1.address = r4;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r4 = r2.title;	 Catch:{ Exception -> 0x0fc6 }
        r1.title = r4;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r4 = r2.provider;	 Catch:{ Exception -> 0x0fc6 }
        r1.provider = r4;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r4 = r2.venue_id;	 Catch:{ Exception -> 0x0fc6 }
        r1.venue_id = r4;	 Catch:{ Exception -> 0x0fc6 }
        goto L_0x104b;
    L_0x1044:
        r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint;	 Catch:{ Exception -> 0x0fc6 }
        r1.<init>();	 Catch:{ Exception -> 0x0fc6 }
        r10.media = r1;	 Catch:{ Exception -> 0x0fc6 }
    L_0x104b:
        r1 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r4 = r2.geo;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r4.lat;	 Catch:{ Exception -> 0x0fc6 }
        r1.lat = r12;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r2 = r2.geo;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r2._long;	 Catch:{ Exception -> 0x0fc6 }
        r1._long = r12;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r42.getSecretChatHelper();	 Catch:{ Exception -> 0x0fc6 }
        r2 = r11.messageOwner;	 Catch:{ Exception -> 0x0fc6 }
        r4 = 0;
        r6 = 0;
        r45 = r1;
        r46 = r10;
        r47 = r2;
        r48 = r9;
        r49 = r4;
        r50 = r6;
        r51 = r11;
        r45.performSendEncryptedRequest(r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x0fc6 }
        r13 = r52;
        r1 = r63;
        r18 = r3;
        r43 = r5;
        r8 = r20;
        goto L_0x13e6;
    L_0x1080:
        r2 = "parentObject";
        r8 = 2;
        if (r5 == r8) goto L_0x13ea;
    L_0x1085:
        r8 = 9;
        if (r5 != r8) goto L_0x108d;
    L_0x1089:
        if (r1 == 0) goto L_0x108d;
    L_0x108b:
        goto L_0x13ea;
    L_0x108d:
        r1 = 3;
        if (r5 != r1) goto L_0x11cb;
    L_0x1090:
        r1 = r4.thumbs;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r7.getThumbForSecretChat(r1);	 Catch:{ Exception -> 0x0fc6 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1);	 Catch:{ Exception -> 0x0fc6 }
        r8 = org.telegram.messenger.MessageObject.isNewGifDocument(r4);	 Catch:{ Exception -> 0x0fc6 }
        if (r8 != 0) goto L_0x10c6;
    L_0x109f:
        r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r4);	 Catch:{ Exception -> 0x0fc6 }
        if (r8 == 0) goto L_0x10a6;
    L_0x10a5:
        goto L_0x10c6;
    L_0x10a6:
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo;	 Catch:{ Exception -> 0x0fc6 }
        r8.<init>();	 Catch:{ Exception -> 0x0fc6 }
        r10.media = r8;	 Catch:{ Exception -> 0x0fc6 }
        if (r1 == 0) goto L_0x10bc;
    L_0x10af:
        r8 = r1.bytes;	 Catch:{ Exception -> 0x0fc6 }
        if (r8 == 0) goto L_0x10bc;
    L_0x10b3:
        r8 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r8 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r8;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r1.bytes;	 Catch:{ Exception -> 0x0fc6 }
        r8.thumb = r12;	 Catch:{ Exception -> 0x0fc6 }
        goto L_0x10eb;
    L_0x10bc:
        r8 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r8 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r8;	 Catch:{ Exception -> 0x0fc6 }
        r12 = 0;
        r13 = new byte[r12];	 Catch:{ Exception -> 0x0fc6 }
        r8.thumb = r13;	 Catch:{ Exception -> 0x0fc6 }
        goto L_0x10eb;
    L_0x10c6:
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x0fc6 }
        r8.<init>();	 Catch:{ Exception -> 0x0fc6 }
        r10.media = r8;	 Catch:{ Exception -> 0x0fc6 }
        r8 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r4.attributes;	 Catch:{ Exception -> 0x0fc6 }
        r8.attributes = r12;	 Catch:{ Exception -> 0x0fc6 }
        if (r1 == 0) goto L_0x10e2;
    L_0x10d5:
        r8 = r1.bytes;	 Catch:{ Exception -> 0x0fc6 }
        if (r8 == 0) goto L_0x10e2;
    L_0x10d9:
        r8 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r8 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r8;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r1.bytes;	 Catch:{ Exception -> 0x0fc6 }
        r8.thumb = r12;	 Catch:{ Exception -> 0x0fc6 }
        goto L_0x10eb;
    L_0x10e2:
        r8 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r8 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r8;	 Catch:{ Exception -> 0x0fc6 }
        r12 = 0;
        r13 = new byte[r12];	 Catch:{ Exception -> 0x0fc6 }
        r8.thumb = r13;	 Catch:{ Exception -> 0x0fc6 }
    L_0x10eb:
        r8 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r8.caption = r6;	 Catch:{ Exception -> 0x0fc6 }
        r6 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r8 = "video/mp4";
        r6.mime_type = r8;	 Catch:{ Exception -> 0x0fc6 }
        r6 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r8 = r4.size;	 Catch:{ Exception -> 0x0fc6 }
        r6.size = r8;	 Catch:{ Exception -> 0x0fc6 }
        r6 = 0;
    L_0x10fc:
        r8 = r4.attributes;	 Catch:{ Exception -> 0x0fc6 }
        r8 = r8.size();	 Catch:{ Exception -> 0x0fc6 }
        if (r6 >= r8) goto L_0x1126;
    L_0x1104:
        r8 = r4.attributes;	 Catch:{ Exception -> 0x0fc6 }
        r8 = r8.get(r6);	 Catch:{ Exception -> 0x0fc6 }
        r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x0fc6 }
        if (r12 == 0) goto L_0x1123;
    L_0x1110:
        r6 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r8.w;	 Catch:{ Exception -> 0x0fc6 }
        r6.w = r12;	 Catch:{ Exception -> 0x0fc6 }
        r6 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r8.h;	 Catch:{ Exception -> 0x0fc6 }
        r6.h = r12;	 Catch:{ Exception -> 0x0fc6 }
        r6 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r8 = r8.duration;	 Catch:{ Exception -> 0x0fc6 }
        r6.duration = r8;	 Catch:{ Exception -> 0x0fc6 }
        goto L_0x1126;
    L_0x1123:
        r6 = r6 + 1;
        goto L_0x10fc;
    L_0x1126:
        r6 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r8 = r1.h;	 Catch:{ Exception -> 0x0fc6 }
        r6.thumb_h = r8;	 Catch:{ Exception -> 0x0fc6 }
        r6 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r1.w;	 Catch:{ Exception -> 0x0fc6 }
        r6.thumb_w = r1;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r4.key;	 Catch:{ Exception -> 0x0fc6 }
        if (r1 == 0) goto L_0x1176;
    L_0x1136:
        r1 = (r60 > r16 ? 1 : (r60 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x113b;
    L_0x113a:
        goto L_0x1176;
    L_0x113b:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0fc6 }
        r1.<init>();	 Catch:{ Exception -> 0x0fc6 }
        r12 = r4.id;	 Catch:{ Exception -> 0x0fc6 }
        r1.id = r12;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r4.access_hash;	 Catch:{ Exception -> 0x0fc6 }
        r1.access_hash = r12;	 Catch:{ Exception -> 0x0fc6 }
        r2 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r6 = r4.key;	 Catch:{ Exception -> 0x0fc6 }
        r2.key = r6;	 Catch:{ Exception -> 0x0fc6 }
        r2 = r10.media;	 Catch:{ Exception -> 0x0fc6 }
        r4 = r4.iv;	 Catch:{ Exception -> 0x0fc6 }
        r2.iv = r4;	 Catch:{ Exception -> 0x0fc6 }
        r2 = r42.getSecretChatHelper();	 Catch:{ Exception -> 0x0fc6 }
        r4 = r11.messageOwner;	 Catch:{ Exception -> 0x0fc6 }
        r6 = 0;
        r45 = r2;
        r46 = r10;
        r47 = r4;
        r48 = r9;
        r49 = r1;
        r50 = r6;
        r51 = r11;
        r45.performSendEncryptedRequest(r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x0fc6 }
        r13 = r52;
        r6 = r55;
        r1 = r63;
        r12 = r5;
        r8 = r20;
        goto L_0x11c2;
    L_0x1176:
        if (r55 != 0) goto L_0x11ae;
    L_0x1178:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0fc6 }
        r13 = r52;
        r6.<init>(r13);	 Catch:{ Exception -> 0x0fc6 }
        r6.encryptedChat = r9;	 Catch:{ Exception -> 0x0fc6 }
        r1 = 1;
        r6.type = r1;	 Catch:{ Exception -> 0x0fc6 }
        r6.sendEncryptedRequest = r10;	 Catch:{ Exception -> 0x0fc6 }
        r8 = r20;
        r6.originalPath = r8;	 Catch:{ Exception -> 0x0fc6 }
        r6.obj = r11;	 Catch:{ Exception -> 0x0fc6 }
        if (r15 == 0) goto L_0x119c;
    L_0x118e:
        r1 = r15.containsKey(r2);	 Catch:{ Exception -> 0x0fc6 }
        if (r1 == 0) goto L_0x119c;
    L_0x1194:
        r1 = r15.get(r2);	 Catch:{ Exception -> 0x0fc6 }
        r6.parentObject = r1;	 Catch:{ Exception -> 0x0fc6 }
        r12 = r5;
        goto L_0x11a1;
    L_0x119c:
        r12 = r5;
        r5 = r65;
        r6.parentObject = r5;	 Catch:{ Exception -> 0x0fc6 }
    L_0x11a1:
        r1 = 1;
        r6.performMediaUpload = r1;	 Catch:{ Exception -> 0x0fc6 }
        r1 = r63;
        if (r1 == 0) goto L_0x11aa;
    L_0x11a8:
        r2 = 1;
        goto L_0x11ab;
    L_0x11aa:
        r2 = 0;
    L_0x11ab:
        r6.scheduled = r2;	 Catch:{ Exception -> 0x11c8 }
        goto L_0x11b7;
    L_0x11ae:
        r13 = r52;
        r1 = r63;
        r12 = r5;
        r8 = r20;
        r6 = r55;
    L_0x11b7:
        r2 = r39;
        r6.videoEditedInfo = r2;	 Catch:{ Exception -> 0x11c8 }
        r2 = (r60 > r16 ? 1 : (r60 == r16 ? 0 : -1));
        if (r2 != 0) goto L_0x11c2;
    L_0x11bf:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x11c8 }
    L_0x11c2:
        r18 = r3;
    L_0x11c4:
        r43 = r12;
        goto L_0x150b;
    L_0x11c8:
        r0 = move-exception;
        goto L_0x0fc9;
    L_0x11cb:
        r13 = r52;
        r1 = r63;
        r18 = r3;
        r12 = r5;
        r8 = r20;
        r5 = r65;
        r3 = 6;
        if (r12 != r3) goto L_0x1219;
    L_0x11d9:
        r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact;	 Catch:{ Exception -> 0x1215 }
        r2.<init>();	 Catch:{ Exception -> 0x1215 }
        r10.media = r2;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = r40;
        r4 = r3.phone;	 Catch:{ Exception -> 0x1215 }
        r2.phone_number = r4;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r4 = r3.first_name;	 Catch:{ Exception -> 0x1215 }
        r2.first_name = r4;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r4 = r3.last_name;	 Catch:{ Exception -> 0x1215 }
        r2.last_name = r4;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = r3.id;	 Catch:{ Exception -> 0x1215 }
        r2.user_id = r3;	 Catch:{ Exception -> 0x1215 }
        r2 = r42.getSecretChatHelper();	 Catch:{ Exception -> 0x1215 }
        r3 = r11.messageOwner;	 Catch:{ Exception -> 0x1215 }
        r4 = 0;
        r5 = 0;
        r45 = r2;
        r46 = r10;
        r47 = r3;
        r48 = r9;
        r49 = r4;
        r50 = r5;
        r51 = r11;
        r45.performSendEncryptedRequest(r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x1215 }
        goto L_0x1298;
    L_0x1215:
        r0 = move-exception;
    L_0x1216:
        r2 = r0;
        goto L_0x0c7d;
    L_0x1219:
        r3 = 7;
        if (r12 == r3) goto L_0x129c;
    L_0x121c:
        r3 = 9;
        if (r12 != r3) goto L_0x1224;
    L_0x1220:
        if (r4 == 0) goto L_0x1224;
    L_0x1222:
        goto L_0x129c;
    L_0x1224:
        r2 = 8;
        if (r12 != r2) goto L_0x1298;
    L_0x1228:
        r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x1215 }
        r2.<init>(r13);	 Catch:{ Exception -> 0x1215 }
        r2.encryptedChat = r9;	 Catch:{ Exception -> 0x1215 }
        r2.sendEncryptedRequest = r10;	 Catch:{ Exception -> 0x1215 }
        r2.obj = r11;	 Catch:{ Exception -> 0x1215 }
        r3 = 3;
        r2.type = r3;	 Catch:{ Exception -> 0x1215 }
        r2.parentObject = r5;	 Catch:{ Exception -> 0x1215 }
        r3 = 1;
        r2.performMediaUpload = r3;	 Catch:{ Exception -> 0x1215 }
        if (r1 == 0) goto L_0x123f;
    L_0x123d:
        r3 = 1;
        goto L_0x1240;
    L_0x123f:
        r3 = 0;
    L_0x1240:
        r2.scheduled = r3;	 Catch:{ Exception -> 0x1215 }
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x1215 }
        r3.<init>();	 Catch:{ Exception -> 0x1215 }
        r10.media = r3;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r5 = r4.attributes;	 Catch:{ Exception -> 0x1215 }
        r3.attributes = r5;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3.caption = r6;	 Catch:{ Exception -> 0x1215 }
        r3 = r4.thumbs;	 Catch:{ Exception -> 0x1215 }
        r3 = r7.getThumbForSecretChat(r3);	 Catch:{ Exception -> 0x1215 }
        if (r3 == 0) goto L_0x1273;
    L_0x125b:
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3);	 Catch:{ Exception -> 0x1215 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r5 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r5;	 Catch:{ Exception -> 0x1215 }
        r6 = r3.bytes;	 Catch:{ Exception -> 0x1215 }
        r5.thumb = r6;	 Catch:{ Exception -> 0x1215 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r6 = r3.h;	 Catch:{ Exception -> 0x1215 }
        r5.thumb_h = r6;	 Catch:{ Exception -> 0x1215 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = r3.w;	 Catch:{ Exception -> 0x1215 }
        r5.thumb_w = r3;	 Catch:{ Exception -> 0x1215 }
        goto L_0x1284;
    L_0x1273:
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r3;	 Catch:{ Exception -> 0x1215 }
        r5 = 0;
        r6 = new byte[r5];	 Catch:{ Exception -> 0x1215 }
        r3.thumb = r6;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3.thumb_h = r5;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3.thumb_w = r5;	 Catch:{ Exception -> 0x1215 }
    L_0x1284:
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r5 = r4.mime_type;	 Catch:{ Exception -> 0x1215 }
        r3.mime_type = r5;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r4 = r4.size;	 Catch:{ Exception -> 0x1215 }
        r3.size = r4;	 Catch:{ Exception -> 0x1215 }
        r2.originalPath = r8;	 Catch:{ Exception -> 0x1215 }
        r7.performSendDelayedMessage(r2);	 Catch:{ Exception -> 0x1215 }
        r6 = r2;
        goto L_0x11c4;
    L_0x1298:
        r43 = r12;
        goto L_0x13e6;
    L_0x129c:
        r3 = org.telegram.messenger.MessageObject.isStickerDocument(r4);	 Catch:{ Exception -> 0x1215 }
        if (r3 != 0) goto L_0x1376;
    L_0x12a2:
        r3 = 0;
        r19 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r3);	 Catch:{ Exception -> 0x1215 }
        if (r19 == 0) goto L_0x12ab;
    L_0x12a9:
        goto L_0x1376;
    L_0x12ab:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x1215 }
        r3.<init>();	 Catch:{ Exception -> 0x1215 }
        r10.media = r3;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r43 = r12;
        r12 = r4.attributes;	 Catch:{ Exception -> 0x1215 }
        r3.attributes = r12;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3.caption = r6;	 Catch:{ Exception -> 0x1215 }
        r3 = r4.thumbs;	 Catch:{ Exception -> 0x1215 }
        r3 = r7.getThumbForSecretChat(r3);	 Catch:{ Exception -> 0x1215 }
        if (r3 == 0) goto L_0x12de;
    L_0x12c6:
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3);	 Catch:{ Exception -> 0x1215 }
        r6 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r6 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r6;	 Catch:{ Exception -> 0x1215 }
        r12 = r3.bytes;	 Catch:{ Exception -> 0x1215 }
        r6.thumb = r12;	 Catch:{ Exception -> 0x1215 }
        r6 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r12 = r3.h;	 Catch:{ Exception -> 0x1215 }
        r6.thumb_h = r12;	 Catch:{ Exception -> 0x1215 }
        r6 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = r3.w;	 Catch:{ Exception -> 0x1215 }
        r6.thumb_w = r3;	 Catch:{ Exception -> 0x1215 }
        goto L_0x12ef;
    L_0x12de:
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r3;	 Catch:{ Exception -> 0x1215 }
        r6 = 0;
        r12 = new byte[r6];	 Catch:{ Exception -> 0x1215 }
        r3.thumb = r12;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3.thumb_h = r6;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3.thumb_w = r6;	 Catch:{ Exception -> 0x1215 }
    L_0x12ef:
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r6 = r4.size;	 Catch:{ Exception -> 0x1215 }
        r3.size = r6;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r6 = r4.mime_type;	 Catch:{ Exception -> 0x1215 }
        r3.mime_type = r6;	 Catch:{ Exception -> 0x1215 }
        r3 = r4.key;	 Catch:{ Exception -> 0x1215 }
        if (r3 != 0) goto L_0x1344;
    L_0x12ff:
        r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x1215 }
        r3.<init>(r13);	 Catch:{ Exception -> 0x1215 }
        r3.originalPath = r8;	 Catch:{ Exception -> 0x1215 }
        r3.sendEncryptedRequest = r10;	 Catch:{ Exception -> 0x1215 }
        r4 = 2;
        r3.type = r4;	 Catch:{ Exception -> 0x1215 }
        r3.obj = r11;	 Catch:{ Exception -> 0x1215 }
        if (r15 == 0) goto L_0x131c;
    L_0x130f:
        r4 = r15.containsKey(r2);	 Catch:{ Exception -> 0x1215 }
        if (r4 == 0) goto L_0x131c;
    L_0x1315:
        r2 = r15.get(r2);	 Catch:{ Exception -> 0x1215 }
        r3.parentObject = r2;	 Catch:{ Exception -> 0x1215 }
        goto L_0x131e;
    L_0x131c:
        r3.parentObject = r5;	 Catch:{ Exception -> 0x1215 }
    L_0x131e:
        r3.encryptedChat = r9;	 Catch:{ Exception -> 0x1215 }
        r2 = 1;
        r3.performMediaUpload = r2;	 Catch:{ Exception -> 0x1215 }
        r4 = r54;
        if (r4 == 0) goto L_0x1337;
    L_0x1327:
        r2 = r54.length();	 Catch:{ Exception -> 0x1215 }
        if (r2 <= 0) goto L_0x1337;
    L_0x132d:
        r2 = "http";
        r2 = r4.startsWith(r2);	 Catch:{ Exception -> 0x1215 }
        if (r2 == 0) goto L_0x1337;
    L_0x1335:
        r3.httpLocation = r4;	 Catch:{ Exception -> 0x1215 }
    L_0x1337:
        if (r1 == 0) goto L_0x133b;
    L_0x1339:
        r2 = 1;
        goto L_0x133c;
    L_0x133b:
        r2 = 0;
    L_0x133c:
        r3.scheduled = r2;	 Catch:{ Exception -> 0x1215 }
        r7.performSendDelayedMessage(r3);	 Catch:{ Exception -> 0x1215 }
        r6 = r3;
        goto L_0x150b;
    L_0x1344:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x1215 }
        r2.<init>();	 Catch:{ Exception -> 0x1215 }
        r5 = r4.id;	 Catch:{ Exception -> 0x1215 }
        r2.id = r5;	 Catch:{ Exception -> 0x1215 }
        r5 = r4.access_hash;	 Catch:{ Exception -> 0x1215 }
        r2.access_hash = r5;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r5 = r4.key;	 Catch:{ Exception -> 0x1215 }
        r3.key = r5;	 Catch:{ Exception -> 0x1215 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r4 = r4.iv;	 Catch:{ Exception -> 0x1215 }
        r3.iv = r4;	 Catch:{ Exception -> 0x1215 }
        r3 = r42.getSecretChatHelper();	 Catch:{ Exception -> 0x1215 }
        r4 = r11.messageOwner;	 Catch:{ Exception -> 0x1215 }
        r5 = 0;
        r45 = r3;
        r46 = r10;
        r47 = r4;
        r48 = r9;
        r49 = r2;
        r50 = r5;
        r51 = r11;
        r45.performSendEncryptedRequest(r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x1215 }
        goto L_0x13e6;
    L_0x1376:
        r43 = r12;
        r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument;	 Catch:{ Exception -> 0x1215 }
        r2.<init>();	 Catch:{ Exception -> 0x1215 }
        r10.media = r2;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r5 = r4.id;	 Catch:{ Exception -> 0x1215 }
        r2.id = r5;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = r4.date;	 Catch:{ Exception -> 0x1215 }
        r2.date = r3;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r5 = r4.access_hash;	 Catch:{ Exception -> 0x1215 }
        r2.access_hash = r5;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = r4.mime_type;	 Catch:{ Exception -> 0x1215 }
        r2.mime_type = r3;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = r4.size;	 Catch:{ Exception -> 0x1215 }
        r2.size = r3;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = r4.dc_id;	 Catch:{ Exception -> 0x1215 }
        r2.dc_id = r3;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = r4.attributes;	 Catch:{ Exception -> 0x1215 }
        r2.attributes = r3;	 Catch:{ Exception -> 0x1215 }
        r2 = r4.thumbs;	 Catch:{ Exception -> 0x1215 }
        r2 = r7.getThumbForSecretChat(r2);	 Catch:{ Exception -> 0x1215 }
        if (r2 == 0) goto L_0x13b8;
    L_0x13b1:
        r3 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r3;	 Catch:{ Exception -> 0x1215 }
        r3.thumb = r2;	 Catch:{ Exception -> 0x1215 }
        goto L_0x13cd;
    L_0x13b8:
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r2 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r2;	 Catch:{ Exception -> 0x1215 }
        r3 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x1215 }
        r3.<init>();	 Catch:{ Exception -> 0x1215 }
        r2.thumb = r3;	 Catch:{ Exception -> 0x1215 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1215 }
        r2 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r2;	 Catch:{ Exception -> 0x1215 }
        r2 = r2.thumb;	 Catch:{ Exception -> 0x1215 }
        r3 = "s";
        r2.type = r3;	 Catch:{ Exception -> 0x1215 }
    L_0x13cd:
        r2 = r42.getSecretChatHelper();	 Catch:{ Exception -> 0x1215 }
        r3 = r11.messageOwner;	 Catch:{ Exception -> 0x1215 }
        r4 = 0;
        r5 = 0;
        r45 = r2;
        r46 = r10;
        r47 = r3;
        r48 = r9;
        r49 = r4;
        r50 = r5;
        r51 = r11;
        r45.performSendEncryptedRequest(r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x1215 }
    L_0x13e6:
        r6 = r55;
        goto L_0x150b;
    L_0x13ea:
        r13 = r52;
        r4 = r54;
        r18 = r3;
        r43 = r5;
        r8 = r20;
        r3 = r63;
        r5 = r65;
        r12 = r1.sizes;	 Catch:{ Exception -> 0x1567 }
        r4 = 0;
        r12 = r12.get(r4);	 Catch:{ Exception -> 0x1567 }
        r12 = (org.telegram.tgnet.TLRPC.PhotoSize) r12;	 Catch:{ Exception -> 0x1567 }
        r4 = r1.sizes;	 Catch:{ Exception -> 0x1567 }
        r3 = r1.sizes;	 Catch:{ Exception -> 0x1563 }
        r3 = r3.size();	 Catch:{ Exception -> 0x1563 }
        r19 = 1;
        r3 = r3 + -1;
        r3 = r4.get(r3);	 Catch:{ Exception -> 0x1563 }
        r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3;	 Catch:{ Exception -> 0x1563 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r12);	 Catch:{ Exception -> 0x1563 }
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto;	 Catch:{ Exception -> 0x1563 }
        r4.<init>();	 Catch:{ Exception -> 0x1563 }
        r10.media = r4;	 Catch:{ Exception -> 0x1563 }
        r4 = r10.media;	 Catch:{ Exception -> 0x1563 }
        r4.caption = r6;	 Catch:{ Exception -> 0x1563 }
        r4 = r12.bytes;	 Catch:{ Exception -> 0x1563 }
        if (r4 == 0) goto L_0x1435;
    L_0x1425:
        r4 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x1430 }
        r6 = r12.bytes;	 Catch:{ Exception -> 0x1430 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x1430 }
        r19 = r1;
        goto L_0x1440;
    L_0x1430:
        r0 = move-exception;
        r1 = r63;
        goto L_0x1216;
    L_0x1435:
        r4 = r10.media;	 Catch:{ Exception -> 0x1563 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x1563 }
        r19 = r1;
        r6 = 0;
        r1 = new byte[r6];	 Catch:{ Exception -> 0x1563 }
        r4.thumb = r1;	 Catch:{ Exception -> 0x1563 }
    L_0x1440:
        r1 = r10.media;	 Catch:{ Exception -> 0x1563 }
        r4 = r12.h;	 Catch:{ Exception -> 0x1563 }
        r1.thumb_h = r4;	 Catch:{ Exception -> 0x1563 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1563 }
        r4 = r12.w;	 Catch:{ Exception -> 0x1563 }
        r1.thumb_w = r4;	 Catch:{ Exception -> 0x1563 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1563 }
        r4 = r3.w;	 Catch:{ Exception -> 0x1563 }
        r1.w = r4;	 Catch:{ Exception -> 0x1563 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1563 }
        r4 = r3.h;	 Catch:{ Exception -> 0x1563 }
        r1.h = r4;	 Catch:{ Exception -> 0x1563 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1563 }
        r4 = r3.size;	 Catch:{ Exception -> 0x1563 }
        r1.size = r4;	 Catch:{ Exception -> 0x1563 }
        r1 = r3.location;	 Catch:{ Exception -> 0x1563 }
        r1 = r1.key;	 Catch:{ Exception -> 0x1563 }
        if (r1 == 0) goto L_0x14a7;
    L_0x1464:
        r1 = (r60 > r16 ? 1 : (r60 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x1469;
    L_0x1468:
        goto L_0x14a7;
    L_0x1469:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x1430 }
        r1.<init>();	 Catch:{ Exception -> 0x1430 }
        r2 = r3.location;	 Catch:{ Exception -> 0x1430 }
        r4 = r2.volume_id;	 Catch:{ Exception -> 0x1430 }
        r1.id = r4;	 Catch:{ Exception -> 0x1430 }
        r2 = r3.location;	 Catch:{ Exception -> 0x1430 }
        r4 = r2.secret;	 Catch:{ Exception -> 0x1430 }
        r1.access_hash = r4;	 Catch:{ Exception -> 0x1430 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r4 = r3.location;	 Catch:{ Exception -> 0x1430 }
        r4 = r4.key;	 Catch:{ Exception -> 0x1430 }
        r2.key = r4;	 Catch:{ Exception -> 0x1430 }
        r2 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r3 = r3.location;	 Catch:{ Exception -> 0x1430 }
        r3 = r3.iv;	 Catch:{ Exception -> 0x1430 }
        r2.iv = r3;	 Catch:{ Exception -> 0x1430 }
        r2 = r42.getSecretChatHelper();	 Catch:{ Exception -> 0x1430 }
        r3 = r11.messageOwner;	 Catch:{ Exception -> 0x1430 }
        r4 = 0;
        r45 = r2;
        r46 = r10;
        r47 = r3;
        r48 = r9;
        r49 = r1;
        r50 = r4;
        r51 = r11;
        r45.performSendEncryptedRequest(r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x1430 }
        r6 = r55;
        r1 = r63;
        goto L_0x150b;
    L_0x14a7:
        if (r55 != 0) goto L_0x14d7;
    L_0x14a9:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x1430 }
        r6.<init>(r13);	 Catch:{ Exception -> 0x1430 }
        r6.encryptedChat = r9;	 Catch:{ Exception -> 0x1430 }
        r1 = 0;
        r6.type = r1;	 Catch:{ Exception -> 0x1430 }
        r6.originalPath = r8;	 Catch:{ Exception -> 0x1430 }
        r6.sendEncryptedRequest = r10;	 Catch:{ Exception -> 0x1430 }
        r6.obj = r11;	 Catch:{ Exception -> 0x1430 }
        if (r15 == 0) goto L_0x14c8;
    L_0x14bb:
        r1 = r15.containsKey(r2);	 Catch:{ Exception -> 0x1430 }
        if (r1 == 0) goto L_0x14c8;
    L_0x14c1:
        r1 = r15.get(r2);	 Catch:{ Exception -> 0x1430 }
        r6.parentObject = r1;	 Catch:{ Exception -> 0x1430 }
        goto L_0x14ca;
    L_0x14c8:
        r6.parentObject = r5;	 Catch:{ Exception -> 0x1430 }
    L_0x14ca:
        r1 = 1;
        r6.performMediaUpload = r1;	 Catch:{ Exception -> 0x1430 }
        r1 = r63;
        if (r1 == 0) goto L_0x14d3;
    L_0x14d1:
        r2 = 1;
        goto L_0x14d4;
    L_0x14d3:
        r2 = 0;
    L_0x14d4:
        r6.scheduled = r2;	 Catch:{ Exception -> 0x1215 }
        goto L_0x14db;
    L_0x14d7:
        r1 = r63;
        r6 = r55;
    L_0x14db:
        r2 = android.text.TextUtils.isEmpty(r54);	 Catch:{ Exception -> 0x1551 }
        if (r2 != 0) goto L_0x14ee;
    L_0x14e1:
        r2 = "http";
        r3 = r54;
        r2 = r3.startsWith(r2);	 Catch:{ Exception -> 0x1215 }
        if (r2 == 0) goto L_0x14ee;
    L_0x14eb:
        r6.httpLocation = r3;	 Catch:{ Exception -> 0x1215 }
        goto L_0x1504;
    L_0x14ee:
        r2 = r19;
        r3 = r2.sizes;	 Catch:{ Exception -> 0x1551 }
        r4 = r2.sizes;	 Catch:{ Exception -> 0x1551 }
        r4 = r4.size();	 Catch:{ Exception -> 0x1551 }
        r5 = 1;
        r4 = r4 - r5;
        r3 = r3.get(r4);	 Catch:{ Exception -> 0x1551 }
        r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3;	 Catch:{ Exception -> 0x1551 }
        r6.photoSize = r3;	 Catch:{ Exception -> 0x1551 }
        r6.locationParent = r2;	 Catch:{ Exception -> 0x1551 }
    L_0x1504:
        r2 = (r60 > r16 ? 1 : (r60 == r16 ? 0 : -1));
        if (r2 != 0) goto L_0x150b;
    L_0x1508:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x1215 }
    L_0x150b:
        r2 = (r60 > r16 ? 1 : (r60 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x1553;
    L_0x150f:
        r2 = r6.sendEncryptedRequest;	 Catch:{ Exception -> 0x1551 }
        if (r2 == 0) goto L_0x1518;
    L_0x1513:
        r2 = r6.sendEncryptedRequest;	 Catch:{ Exception -> 0x1215 }
        r2 = (org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r2;	 Catch:{ Exception -> 0x1215 }
        goto L_0x151f;
    L_0x1518:
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;	 Catch:{ Exception -> 0x1551 }
        r2.<init>();	 Catch:{ Exception -> 0x1551 }
        r6.sendEncryptedRequest = r2;	 Catch:{ Exception -> 0x1551 }
    L_0x151f:
        r3 = r6.messageObjects;	 Catch:{ Exception -> 0x1551 }
        r3.add(r11);	 Catch:{ Exception -> 0x1551 }
        r3 = r6.messages;	 Catch:{ Exception -> 0x1551 }
        r9 = r18;
        r3.add(r9);	 Catch:{ Exception -> 0x1659 }
        r3 = r6.originalPaths;	 Catch:{ Exception -> 0x1659 }
        r3.add(r8);	 Catch:{ Exception -> 0x1659 }
        r3 = 1;
        r6.performMediaUpload = r3;	 Catch:{ Exception -> 0x1659 }
        r3 = r2.messages;	 Catch:{ Exception -> 0x1659 }
        r3.add(r10);	 Catch:{ Exception -> 0x1659 }
        r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x1659 }
        r3.<init>();	 Catch:{ Exception -> 0x1659 }
        r8 = r43;
        r4 = 3;
        if (r8 != r4) goto L_0x1544;
    L_0x1542:
        r16 = 1;
    L_0x1544:
        r4 = r16;
        r3.id = r4;	 Catch:{ Exception -> 0x1659 }
        r2 = r2.files;	 Catch:{ Exception -> 0x1659 }
        r2.add(r3);	 Catch:{ Exception -> 0x1659 }
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x1659 }
        goto L_0x1555;
    L_0x1551:
        r0 = move-exception;
        goto L_0x1569;
    L_0x1553:
        r9 = r18;
    L_0x1555:
        r2 = r58;
        if (r2 != 0) goto L_0x18a7;
    L_0x1559:
        r2 = r42.getMediaDataController();	 Catch:{ Exception -> 0x1659 }
        r3 = 0;
        r2.cleanDraft(r13, r3);	 Catch:{ Exception -> 0x1659 }
        goto L_0x18a7;
    L_0x1563:
        r0 = move-exception;
        r1 = r63;
        goto L_0x1569;
    L_0x1567:
        r0 = move-exception;
        r1 = r3;
    L_0x1569:
        r9 = r18;
        goto L_0x1570;
    L_0x156c:
        r0 = move-exception;
        r1 = r63;
        r9 = r3;
    L_0x1570:
        r2 = r0;
        r5 = r9;
        goto L_0x1877;
    L_0x1574:
        r11 = r56;
        r2 = r58;
        r4 = r3;
        r9 = r5;
        r1 = r10;
        r8 = r15;
        r6 = 4;
        r3 = r49;
        r15 = r51;
        r5 = r65;
        if (r8 != r6) goto L_0x165c;
    L_0x1585:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;	 Catch:{ Exception -> 0x1659 }
        r3.<init>();	 Catch:{ Exception -> 0x1659 }
        r3.to_peer = r4;	 Catch:{ Exception -> 0x1659 }
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x1659 }
        r4 = r4.with_my_score;	 Catch:{ Exception -> 0x1659 }
        r3.with_my_score = r4;	 Catch:{ Exception -> 0x1659 }
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x1659 }
        r4 = r4.ttl;	 Catch:{ Exception -> 0x1659 }
        if (r4 == 0) goto L_0x15c2;
    L_0x1598:
        r4 = r42.getMessagesController();	 Catch:{ Exception -> 0x1659 }
        r6 = r2.messageOwner;	 Catch:{ Exception -> 0x1659 }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x1659 }
        r6 = -r6;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x1659 }
        r4 = r4.getChat(r6);	 Catch:{ Exception -> 0x1659 }
        r6 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;	 Catch:{ Exception -> 0x1659 }
        r6.<init>();	 Catch:{ Exception -> 0x1659 }
        r3.from_peer = r6;	 Catch:{ Exception -> 0x1659 }
        r6 = r3.from_peer;	 Catch:{ Exception -> 0x1659 }
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x1659 }
        r8 = r8.ttl;	 Catch:{ Exception -> 0x1659 }
        r8 = -r8;
        r6.channel_id = r8;	 Catch:{ Exception -> 0x1659 }
        if (r4 == 0) goto L_0x15c9;
    L_0x15bb:
        r6 = r3.from_peer;	 Catch:{ Exception -> 0x1659 }
        r4 = r4.access_hash;	 Catch:{ Exception -> 0x1659 }
        r6.access_hash = r4;	 Catch:{ Exception -> 0x1659 }
        goto L_0x15c9;
    L_0x15c2:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;	 Catch:{ Exception -> 0x1659 }
        r4.<init>();	 Catch:{ Exception -> 0x1659 }
        r3.from_peer = r4;	 Catch:{ Exception -> 0x1659 }
    L_0x15c9:
        if (r62 == 0) goto L_0x15ea;
    L_0x15cb:
        r4 = r7.currentAccount;	 Catch:{ Exception -> 0x1659 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x1659 }
        r5 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x1659 }
        r5.<init>();	 Catch:{ Exception -> 0x1659 }
        r5.append(r15);	 Catch:{ Exception -> 0x1659 }
        r5.append(r13);	 Catch:{ Exception -> 0x1659 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x1659 }
        r6 = 0;
        r4 = r4.getBoolean(r5, r6);	 Catch:{ Exception -> 0x1659 }
        if (r4 == 0) goto L_0x15e8;
    L_0x15e7:
        goto L_0x15ea;
    L_0x15e8:
        r4 = 0;
        goto L_0x15eb;
    L_0x15ea:
        r4 = 1;
    L_0x15eb:
        r3.silent = r4;	 Catch:{ Exception -> 0x1659 }
        if (r1 == 0) goto L_0x15f7;
    L_0x15ef:
        r3.schedule_date = r1;	 Catch:{ Exception -> 0x1659 }
        r4 = r3.flags;	 Catch:{ Exception -> 0x1659 }
        r4 = r4 | 1024;
        r3.flags = r4;	 Catch:{ Exception -> 0x1659 }
    L_0x15f7:
        r4 = r3.random_id;	 Catch:{ Exception -> 0x1659 }
        r5 = r9.random_id;	 Catch:{ Exception -> 0x1659 }
        r5 = java.lang.Long.valueOf(r5);	 Catch:{ Exception -> 0x1659 }
        r4.add(r5);	 Catch:{ Exception -> 0x1659 }
        r4 = r58.getId();	 Catch:{ Exception -> 0x1659 }
        if (r4 < 0) goto L_0x1616;
    L_0x1608:
        r4 = r3.id;	 Catch:{ Exception -> 0x1659 }
        r2 = r58.getId();	 Catch:{ Exception -> 0x1659 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1659 }
        r4.add(r2);	 Catch:{ Exception -> 0x1659 }
        goto L_0x163f;
    L_0x1616:
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x1659 }
        r4 = r4.fwd_msg_id;	 Catch:{ Exception -> 0x1659 }
        if (r4 == 0) goto L_0x162a;
    L_0x161c:
        r4 = r3.id;	 Catch:{ Exception -> 0x1659 }
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x1659 }
        r2 = r2.fwd_msg_id;	 Catch:{ Exception -> 0x1659 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1659 }
        r4.add(r2);	 Catch:{ Exception -> 0x1659 }
        goto L_0x163f;
    L_0x162a:
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x1659 }
        r4 = r4.fwd_from;	 Catch:{ Exception -> 0x1659 }
        if (r4 == 0) goto L_0x163f;
    L_0x1630:
        r4 = r3.id;	 Catch:{ Exception -> 0x1659 }
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x1659 }
        r2 = r2.fwd_from;	 Catch:{ Exception -> 0x1659 }
        r2 = r2.channel_post;	 Catch:{ Exception -> 0x1659 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1659 }
        r4.add(r2);	 Catch:{ Exception -> 0x1659 }
    L_0x163f:
        r2 = 0;
        r4 = 0;
        if (r1 == 0) goto L_0x1645;
    L_0x1643:
        r5 = 1;
        goto L_0x1646;
    L_0x1645:
        r5 = 0;
    L_0x1646:
        r43 = r42;
        r44 = r3;
        r45 = r11;
        r46 = r2;
        r47 = r4;
        r48 = r65;
        r49 = r5;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49);	 Catch:{ Exception -> 0x1659 }
        goto L_0x18a7;
    L_0x1659:
        r0 = move-exception;
        goto L_0x1570;
    L_0x165c:
        r5 = 9;
        if (r8 != r5) goto L_0x18a7;
    L_0x1660:
        r5 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult;	 Catch:{ Exception -> 0x16fe }
        r5.<init>();	 Catch:{ Exception -> 0x16fe }
        r5.peer = r4;	 Catch:{ Exception -> 0x16fe }
        r56 = r11;
        r10 = r9.random_id;	 Catch:{ Exception -> 0x16f9 }
        r5.random_id = r10;	 Catch:{ Exception -> 0x16f9 }
        r4 = "bot";
        r4 = r3.containsKey(r4);	 Catch:{ Exception -> 0x16f9 }
        if (r4 != 0) goto L_0x1677;
    L_0x1675:
        r4 = 1;
        goto L_0x1678;
    L_0x1677:
        r4 = 0;
    L_0x1678:
        r5.hide_via = r4;	 Catch:{ Exception -> 0x16f9 }
        r4 = r9.reply_to_msg_id;	 Catch:{ Exception -> 0x16f9 }
        if (r4 == 0) goto L_0x1688;
    L_0x167e:
        r4 = r5.flags;	 Catch:{ Exception -> 0x16f9 }
        r6 = 1;
        r4 = r4 | r6;
        r5.flags = r4;	 Catch:{ Exception -> 0x16f9 }
        r4 = r9.reply_to_msg_id;	 Catch:{ Exception -> 0x16f9 }
        r5.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x16f9 }
    L_0x1688:
        if (r62 == 0) goto L_0x16a9;
    L_0x168a:
        r4 = r7.currentAccount;	 Catch:{ Exception -> 0x16f9 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x16f9 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x16f9 }
        r6.<init>();	 Catch:{ Exception -> 0x16f9 }
        r6.append(r15);	 Catch:{ Exception -> 0x16f9 }
        r6.append(r13);	 Catch:{ Exception -> 0x16f9 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x16f9 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x16f9 }
        if (r4 == 0) goto L_0x16a7;
    L_0x16a6:
        goto L_0x16a9;
    L_0x16a7:
        r4 = 0;
        goto L_0x16aa;
    L_0x16a9:
        r4 = 1;
    L_0x16aa:
        r5.silent = r4;	 Catch:{ Exception -> 0x16f9 }
        if (r1 == 0) goto L_0x16b6;
    L_0x16ae:
        r5.schedule_date = r1;	 Catch:{ Exception -> 0x16f9 }
        r4 = r5.flags;	 Catch:{ Exception -> 0x16f9 }
        r4 = r4 | 1024;
        r5.flags = r4;	 Catch:{ Exception -> 0x16f9 }
    L_0x16b6:
        r4 = r19;
        r4 = r3.get(r4);	 Catch:{ Exception -> 0x16f9 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x16f9 }
        r4 = org.telegram.messenger.Utilities.parseLong(r4);	 Catch:{ Exception -> 0x16f9 }
        r10 = r4.longValue();	 Catch:{ Exception -> 0x16f9 }
        r5.query_id = r10;	 Catch:{ Exception -> 0x16f9 }
        r4 = "id";
        r3 = r3.get(r4);	 Catch:{ Exception -> 0x16f9 }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x16f9 }
        r5.id = r3;	 Catch:{ Exception -> 0x16f9 }
        if (r2 != 0) goto L_0x16df;
    L_0x16d4:
        r2 = 1;
        r5.clear_draft = r2;	 Catch:{ Exception -> 0x16f9 }
        r2 = r42.getMediaDataController();	 Catch:{ Exception -> 0x16f9 }
        r3 = 0;
        r2.cleanDraft(r13, r3);	 Catch:{ Exception -> 0x16f9 }
    L_0x16df:
        r2 = 0;
        r3 = 0;
        if (r1 == 0) goto L_0x16e5;
    L_0x16e3:
        r4 = 1;
        goto L_0x16e6;
    L_0x16e5:
        r4 = 0;
    L_0x16e6:
        r43 = r42;
        r44 = r5;
        r45 = r56;
        r46 = r2;
        r47 = r3;
        r48 = r65;
        r49 = r4;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49);	 Catch:{ Exception -> 0x16f9 }
        goto L_0x18a7;
    L_0x16f9:
        r0 = move-exception;
        r11 = r56;
        goto L_0x1570;
    L_0x16fe:
        r0 = move-exception;
        r56 = r11;
        goto L_0x1570;
    L_0x1703:
        r15 = r51;
        r2 = r58;
        r12 = r59;
        r4 = r3;
        r1 = r10;
        r3 = r49;
        if (r9 != 0) goto L_0x17a1;
    L_0x170f:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage;	 Catch:{ Exception -> 0x179b }
        r3.<init>();	 Catch:{ Exception -> 0x179b }
        r6 = r48;
        r3.message = r6;	 Catch:{ Exception -> 0x179b }
        if (r2 != 0) goto L_0x171c;
    L_0x171a:
        r6 = 1;
        goto L_0x171d;
    L_0x171c:
        r6 = 0;
    L_0x171d:
        r3.clear_draft = r6;	 Catch:{ Exception -> 0x179b }
        if (r62 == 0) goto L_0x1740;
    L_0x1721:
        r6 = r7.currentAccount;	 Catch:{ Exception -> 0x179b }
        r6 = org.telegram.messenger.MessagesController.getNotificationsSettings(r6);	 Catch:{ Exception -> 0x179b }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x179b }
        r8.<init>();	 Catch:{ Exception -> 0x179b }
        r8.append(r15);	 Catch:{ Exception -> 0x179b }
        r8.append(r13);	 Catch:{ Exception -> 0x179b }
        r8 = r8.toString();	 Catch:{ Exception -> 0x179b }
        r9 = 0;
        r6 = r6.getBoolean(r8, r9);	 Catch:{ Exception -> 0x179b }
        if (r6 == 0) goto L_0x173e;
    L_0x173d:
        goto L_0x1740;
    L_0x173e:
        r6 = 0;
        goto L_0x1741;
    L_0x1740:
        r6 = 1;
    L_0x1741:
        r3.silent = r6;	 Catch:{ Exception -> 0x179b }
        r3.peer = r4;	 Catch:{ Exception -> 0x179b }
        r8 = r5.random_id;	 Catch:{ Exception -> 0x179b }
        r3.random_id = r8;	 Catch:{ Exception -> 0x179b }
        r4 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x179b }
        if (r4 == 0) goto L_0x1757;
    L_0x174d:
        r4 = r3.flags;	 Catch:{ Exception -> 0x179b }
        r6 = 1;
        r4 = r4 | r6;
        r3.flags = r4;	 Catch:{ Exception -> 0x179b }
        r4 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x179b }
        r3.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x179b }
    L_0x1757:
        if (r57 != 0) goto L_0x175c;
    L_0x1759:
        r4 = 1;
        r3.no_webpage = r4;	 Catch:{ Exception -> 0x179b }
    L_0x175c:
        if (r12 == 0) goto L_0x176d;
    L_0x175e:
        r4 = r59.isEmpty();	 Catch:{ Exception -> 0x179b }
        if (r4 != 0) goto L_0x176d;
    L_0x1764:
        r3.entities = r12;	 Catch:{ Exception -> 0x179b }
        r4 = r3.flags;	 Catch:{ Exception -> 0x179b }
        r6 = 8;
        r4 = r4 | r6;
        r3.flags = r4;	 Catch:{ Exception -> 0x179b }
    L_0x176d:
        if (r1 == 0) goto L_0x1777;
    L_0x176f:
        r3.schedule_date = r1;	 Catch:{ Exception -> 0x179b }
        r4 = r3.flags;	 Catch:{ Exception -> 0x179b }
        r4 = r4 | 1024;
        r3.flags = r4;	 Catch:{ Exception -> 0x179b }
    L_0x1777:
        r4 = 0;
        r6 = 0;
        if (r1 == 0) goto L_0x177d;
    L_0x177b:
        r8 = 1;
        goto L_0x177e;
    L_0x177d:
        r8 = 0;
    L_0x177e:
        r43 = r42;
        r44 = r3;
        r45 = r56;
        r46 = r4;
        r47 = r6;
        r48 = r65;
        r49 = r8;
        r43.performSendMessageRequest(r44, r45, r46, r47, r48, r49);	 Catch:{ Exception -> 0x179b }
        if (r2 != 0) goto L_0x18a7;
    L_0x1791:
        r2 = r42.getMediaDataController();	 Catch:{ Exception -> 0x179b }
        r3 = 0;
        r2.cleanDraft(r13, r3);	 Catch:{ Exception -> 0x179b }
        goto L_0x18a7;
    L_0x179b:
        r0 = move-exception;
        r11 = r56;
        r2 = r0;
        goto L_0x1877;
    L_0x17a1:
        r6 = r48;
        r4 = r9.layer;	 Catch:{ Exception -> 0x1849 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x1849 }
        r8 = 73;
        if (r4 < r8) goto L_0x17b3;
    L_0x17ad:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x179b }
        r4.<init>();	 Catch:{ Exception -> 0x179b }
        goto L_0x17b8;
    L_0x17b3:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x1849 }
        r4.<init>();	 Catch:{ Exception -> 0x1849 }
    L_0x17b8:
        r8 = r5.ttl;	 Catch:{ Exception -> 0x1849 }
        r4.ttl = r8;	 Catch:{ Exception -> 0x1849 }
        if (r12 == 0) goto L_0x17cc;
    L_0x17be:
        r8 = r59.isEmpty();	 Catch:{ Exception -> 0x179b }
        if (r8 != 0) goto L_0x17cc;
    L_0x17c4:
        r4.entities = r12;	 Catch:{ Exception -> 0x179b }
        r8 = r4.flags;	 Catch:{ Exception -> 0x179b }
        r8 = r8 | 128;
        r4.flags = r8;	 Catch:{ Exception -> 0x179b }
    L_0x17cc:
        r10 = r5.reply_to_random_id;	 Catch:{ Exception -> 0x1849 }
        r8 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1));
        if (r8 == 0) goto L_0x17dd;
    L_0x17d2:
        r10 = r5.reply_to_random_id;	 Catch:{ Exception -> 0x179b }
        r4.reply_to_random_id = r10;	 Catch:{ Exception -> 0x179b }
        r8 = r4.flags;	 Catch:{ Exception -> 0x179b }
        r10 = 8;
        r8 = r8 | r10;
        r4.flags = r8;	 Catch:{ Exception -> 0x179b }
    L_0x17dd:
        if (r3 == 0) goto L_0x17f7;
    L_0x17df:
        r8 = "bot_name";
        r8 = r3.get(r8);	 Catch:{ Exception -> 0x179b }
        if (r8 == 0) goto L_0x17f7;
    L_0x17e7:
        r8 = "bot_name";
        r3 = r3.get(r8);	 Catch:{ Exception -> 0x179b }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x179b }
        r4.via_bot_name = r3;	 Catch:{ Exception -> 0x179b }
        r3 = r4.flags;	 Catch:{ Exception -> 0x179b }
        r3 = r3 | 2048;
        r4.flags = r3;	 Catch:{ Exception -> 0x179b }
    L_0x17f7:
        r10 = r5.random_id;	 Catch:{ Exception -> 0x1849 }
        r4.random_id = r10;	 Catch:{ Exception -> 0x1849 }
        r4.message = r6;	 Catch:{ Exception -> 0x1849 }
        r3 = r31;
        if (r3 == 0) goto L_0x1819;
    L_0x1801:
        r6 = r3.url;	 Catch:{ Exception -> 0x179b }
        if (r6 == 0) goto L_0x1819;
    L_0x1805:
        r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage;	 Catch:{ Exception -> 0x179b }
        r6.<init>();	 Catch:{ Exception -> 0x179b }
        r4.media = r6;	 Catch:{ Exception -> 0x179b }
        r6 = r4.media;	 Catch:{ Exception -> 0x179b }
        r3 = r3.url;	 Catch:{ Exception -> 0x179b }
        r6.url = r3;	 Catch:{ Exception -> 0x179b }
        r3 = r4.flags;	 Catch:{ Exception -> 0x179b }
        r3 = r3 | 512;
        r4.flags = r3;	 Catch:{ Exception -> 0x179b }
        goto L_0x1820;
    L_0x1819:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty;	 Catch:{ Exception -> 0x1849 }
        r3.<init>();	 Catch:{ Exception -> 0x1849 }
        r4.media = r3;	 Catch:{ Exception -> 0x1849 }
    L_0x1820:
        r3 = r42.getSecretChatHelper();	 Catch:{ Exception -> 0x1849 }
        r6 = r56;
        r8 = r6.messageOwner;	 Catch:{ Exception -> 0x1847 }
        r10 = 0;
        r11 = 0;
        r45 = r3;
        r46 = r4;
        r47 = r8;
        r48 = r9;
        r49 = r10;
        r50 = r11;
        r51 = r6;
        r45.performSendEncryptedRequest(r46, r47, r48, r49, r50, r51);	 Catch:{ Exception -> 0x1847 }
        if (r2 != 0) goto L_0x18a7;
    L_0x183d:
        r2 = r42.getMediaDataController();	 Catch:{ Exception -> 0x1847 }
        r3 = 0;
        r2.cleanDraft(r13, r3);	 Catch:{ Exception -> 0x1847 }
        goto L_0x18a7;
    L_0x1847:
        r0 = move-exception;
        goto L_0x1853;
    L_0x1849:
        r0 = move-exception;
        r6 = r56;
        goto L_0x1853;
    L_0x184d:
        r0 = move-exception;
        r6 = r56;
        goto L_0x1852;
    L_0x1851:
        r0 = move-exception;
    L_0x1852:
        r1 = r10;
    L_0x1853:
        r2 = r0;
        r11 = r6;
        goto L_0x1877;
    L_0x1856:
        r0 = move-exception;
        goto L_0x185b;
    L_0x1858:
        r0 = move-exception;
        r7 = r42;
    L_0x185b:
        r1 = r10;
        goto L_0x1862;
    L_0x185d:
        r0 = move-exception;
    L_0x185e:
        r7 = r42;
        r1 = r63;
    L_0x1862:
        r2 = r0;
        goto L_0x0269;
    L_0x1865:
        r0 = move-exception;
        r7 = r42;
        r1 = r63;
        r2 = r0;
        r5 = r18;
        goto L_0x0269;
    L_0x186f:
        r0 = move-exception;
        r7 = r42;
        r1 = r63;
        r18 = r5;
        goto L_0x1862;
    L_0x1877:
        org.telegram.messenger.FileLog.e(r2);
        r2 = r42.getMessagesStorage();
        if (r1 == 0) goto L_0x1882;
    L_0x1880:
        r1 = 1;
        goto L_0x1883;
    L_0x1882:
        r1 = 0;
    L_0x1883:
        r2.markMessageAsSendError(r5, r1);
        if (r11 == 0) goto L_0x188d;
    L_0x1888:
        r1 = r11.messageOwner;
        r2 = 2;
        r1.send_state = r2;
    L_0x188d:
        r1 = r42.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.messageSendError;
        r3 = 1;
        r3 = new java.lang.Object[r3];
        r4 = r5.id;
        r4 = java.lang.Integer.valueOf(r4);
        r6 = 0;
        r3[r6] = r4;
        r1.postNotificationName(r2, r3);
        r1 = r5.id;
        r7.processSentMessage(r1);
    L_0x18a7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, org.telegram.tgnet.TLRPC$TL_messageMediaPoll, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, boolean, int, int, java.lang.Object):void");
    }

    private void performSendDelayedMessage(DelayedMessage delayedMessage) {
        performSendDelayedMessage(delayedMessage, -1);
    }

    private PhotoSize getThumbForSecretChat(ArrayList<PhotoSize> arrayList) {
        if (!(arrayList == null || arrayList.isEmpty())) {
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                PhotoSize photoSize = (PhotoSize) arrayList.get(i);
                if (photoSize == null || (photoSize instanceof TL_photoStrippedSize) || (photoSize instanceof TL_photoSizeEmpty) || photoSize.location == null) {
                    i++;
                } else {
                    TL_photoSize tL_photoSize = new TL_photoSize();
                    tL_photoSize.type = photoSize.type;
                    tL_photoSize.w = photoSize.w;
                    tL_photoSize.h = photoSize.h;
                    tL_photoSize.size = photoSize.size;
                    tL_photoSize.bytes = photoSize.bytes;
                    if (tL_photoSize.bytes == null) {
                        tL_photoSize.bytes = new byte[0];
                    }
                    tL_photoSize.location = new TL_fileLocation_layer82();
                    FileLocation fileLocation = tL_photoSize.location;
                    FileLocation fileLocation2 = photoSize.location;
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
        DelayedMessage delayedMessage2 = delayedMessage;
        int i2 = delayedMessage2.type;
        String str = "file";
        boolean z = false;
        boolean z2 = true;
        String str2;
        if (i2 == 0) {
            str2 = delayedMessage2.httpLocation;
            if (str2 != null) {
                putToDelayedMessages(str2, delayedMessage2);
                ImageLoader.getInstance().loadHttpFile(delayedMessage2.httpLocation, str, this.currentAccount);
            } else if (delayedMessage2.sendRequest != null) {
                str2 = FileLoader.getPathToAttach(delayedMessage2.photoSize).toString();
                putToDelayedMessages(str2, delayedMessage2);
                getFileLoader().uploadFile(str2, false, true, 16777216);
                putToUploadingMessages(delayedMessage2.obj);
            } else {
                str2 = FileLoader.getPathToAttach(delayedMessage2.photoSize).toString();
                if (!(delayedMessage2.sendEncryptedRequest == null || delayedMessage2.photoSize.location.dc_id == 0)) {
                    File file = new File(str2);
                    if (!file.exists()) {
                        str2 = FileLoader.getPathToAttach(delayedMessage2.photoSize, true).toString();
                        file = new File(str2);
                    }
                    if (!file.exists()) {
                        putToDelayedMessages(FileLoader.getAttachFileName(delayedMessage2.photoSize), delayedMessage2);
                        getFileLoader().loadFile(ImageLocation.getForObject(delayedMessage2.photoSize, delayedMessage2.locationParent), delayedMessage2.parentObject, "jpg", 2, 0);
                        return;
                    }
                }
                putToDelayedMessages(str2, delayedMessage2);
                getFileLoader().uploadFile(str2, true, true, 16777216);
                putToUploadingMessages(delayedMessage2.obj);
            }
        } else {
            String str3 = ".jpg";
            String str4 = "_";
            String str5 = ".mp4";
            String str6 = "/";
            TLObject tLObject;
            InputMedia inputMedia;
            MessageObject messageObject;
            Document document;
            StringBuilder stringBuilder;
            String str7;
            StringBuilder stringBuilder2;
            String str8;
            if (i2 == 1) {
                VideoEditedInfo videoEditedInfo = delayedMessage2.videoEditedInfo;
                if (videoEditedInfo == null || !videoEditedInfo.needConvert()) {
                    VideoEditedInfo videoEditedInfo2;
                    videoEditedInfo = delayedMessage2.videoEditedInfo;
                    if (videoEditedInfo != null) {
                        if (videoEditedInfo.file != null) {
                            tLObject = delayedMessage2.sendRequest;
                            if (tLObject instanceof TL_messages_sendMedia) {
                                inputMedia = ((TL_messages_sendMedia) tLObject).media;
                            } else {
                                inputMedia = ((TL_messages_editMessage) tLObject).media;
                            }
                            videoEditedInfo2 = delayedMessage2.videoEditedInfo;
                            inputMedia.file = videoEditedInfo2.file;
                            videoEditedInfo2.file = null;
                        } else if (videoEditedInfo.encryptedFile != null) {
                            TL_decryptedMessage tL_decryptedMessage = (TL_decryptedMessage) delayedMessage2.sendEncryptedRequest;
                            DecryptedMessageMedia decryptedMessageMedia = tL_decryptedMessage.media;
                            decryptedMessageMedia.size = (int) videoEditedInfo.estimatedSize;
                            decryptedMessageMedia.key = videoEditedInfo.key;
                            decryptedMessageMedia.iv = videoEditedInfo.iv;
                            SecretChatHelper secretChatHelper = getSecretChatHelper();
                            MessageObject messageObject2 = delayedMessage2.obj;
                            secretChatHelper.performSendEncryptedRequest(tL_decryptedMessage, messageObject2.messageOwner, delayedMessage2.encryptedChat, delayedMessage2.videoEditedInfo.encryptedFile, delayedMessage2.originalPath, messageObject2);
                            delayedMessage2.videoEditedInfo.encryptedFile = null;
                            return;
                        }
                    }
                    tLObject = delayedMessage2.sendRequest;
                    if (tLObject != null) {
                        if (tLObject instanceof TL_messages_sendMedia) {
                            inputMedia = ((TL_messages_sendMedia) tLObject).media;
                        } else {
                            inputMedia = ((TL_messages_editMessage) tLObject).media;
                        }
                        if (inputMedia.file == null) {
                            messageObject = delayedMessage2.obj;
                            str = messageObject.messageOwner.attachPath;
                            document = messageObject.getDocument();
                            if (str == null) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(FileLoader.getDirectory(4));
                                stringBuilder.append(str6);
                                stringBuilder.append(document.id);
                                stringBuilder.append(str5);
                                str = stringBuilder.toString();
                            }
                            str7 = str;
                            putToDelayedMessages(str7, delayedMessage2);
                            videoEditedInfo2 = delayedMessage2.obj.videoEditedInfo;
                            if (videoEditedInfo2 == null || !videoEditedInfo2.needConvert()) {
                                getFileLoader().uploadFile(str7, false, false, 33554432);
                            } else {
                                getFileLoader().uploadFile(str7, false, false, document.size, 33554432);
                            }
                            putToUploadingMessages(delayedMessage2.obj);
                        } else {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(FileLoader.getDirectory(4));
                            stringBuilder2.append(str6);
                            stringBuilder2.append(delayedMessage2.photoSize.location.volume_id);
                            stringBuilder2.append(str4);
                            stringBuilder2.append(delayedMessage2.photoSize.location.local_id);
                            stringBuilder2.append(str3);
                            str2 = stringBuilder2.toString();
                            putToDelayedMessages(str2, delayedMessage2);
                            getFileLoader().uploadFile(str2, false, true, 16777216);
                            putToUploadingMessages(delayedMessage2.obj);
                        }
                    } else {
                        messageObject = delayedMessage2.obj;
                        str = messageObject.messageOwner.attachPath;
                        document = messageObject.getDocument();
                        if (str == null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(FileLoader.getDirectory(4));
                            stringBuilder.append(str6);
                            stringBuilder.append(document.id);
                            stringBuilder.append(str5);
                            str = stringBuilder.toString();
                        }
                        str7 = str;
                        if (delayedMessage2.sendEncryptedRequest == null || document.dc_id == 0 || new File(str7).exists()) {
                            putToDelayedMessages(str7, delayedMessage2);
                            videoEditedInfo2 = delayedMessage2.obj.videoEditedInfo;
                            if (videoEditedInfo2 == null || !videoEditedInfo2.needConvert()) {
                                getFileLoader().uploadFile(str7, true, false, 33554432);
                            } else {
                                getFileLoader().uploadFile(str7, true, false, document.size, 33554432);
                            }
                            putToUploadingMessages(delayedMessage2.obj);
                        } else {
                            putToDelayedMessages(FileLoader.getAttachFileName(document), delayedMessage2);
                            getFileLoader().loadFile(document, delayedMessage2.parentObject, 2, 0);
                            return;
                        }
                    }
                }
                messageObject = delayedMessage2.obj;
                str = messageObject.messageOwner.attachPath;
                document = messageObject.getDocument();
                if (str == null) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(FileLoader.getDirectory(4));
                    stringBuilder.append(str6);
                    stringBuilder.append(document.id);
                    stringBuilder.append(str5);
                    str = stringBuilder.toString();
                }
                putToDelayedMessages(str, delayedMessage2);
                MediaController.getInstance().scheduleVideoConvert(delayedMessage2.obj);
                putToUploadingMessages(delayedMessage2.obj);
            } else if (i2 == 2) {
                str2 = delayedMessage2.httpLocation;
                if (str2 != null) {
                    putToDelayedMessages(str2, delayedMessage2);
                    ImageLoader.getInstance().loadHttpFile(delayedMessage2.httpLocation, "gif", this.currentAccount);
                } else {
                    tLObject = delayedMessage2.sendRequest;
                    if (tLObject != null) {
                        if (tLObject instanceof TL_messages_sendMedia) {
                            inputMedia = ((TL_messages_sendMedia) tLObject).media;
                        } else {
                            inputMedia = ((TL_messages_editMessage) tLObject).media;
                        }
                        if (inputMedia.file == null) {
                            str2 = delayedMessage2.obj.messageOwner.attachPath;
                            putToDelayedMessages(str2, delayedMessage2);
                            FileLoader fileLoader = getFileLoader();
                            if (delayedMessage2.sendRequest != null) {
                                z2 = false;
                            }
                            fileLoader.uploadFile(str2, z2, false, 67108864);
                            putToUploadingMessages(delayedMessage2.obj);
                        } else if (inputMedia.thumb == null && delayedMessage2.photoSize != null) {
                            stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(FileLoader.getDirectory(4));
                            stringBuilder2.append(str6);
                            stringBuilder2.append(delayedMessage2.photoSize.location.volume_id);
                            stringBuilder2.append(str4);
                            stringBuilder2.append(delayedMessage2.photoSize.location.local_id);
                            stringBuilder2.append(str3);
                            str2 = stringBuilder2.toString();
                            putToDelayedMessages(str2, delayedMessage2);
                            getFileLoader().uploadFile(str2, false, true, 16777216);
                            putToUploadingMessages(delayedMessage2.obj);
                        }
                    } else {
                        messageObject = delayedMessage2.obj;
                        str8 = messageObject.messageOwner.attachPath;
                        document = messageObject.getDocument();
                        if (delayedMessage2.sendEncryptedRequest == null || document.dc_id == 0 || new File(str8).exists()) {
                            putToDelayedMessages(str8, delayedMessage2);
                            getFileLoader().uploadFile(str8, true, false, 67108864);
                            putToUploadingMessages(delayedMessage2.obj);
                        } else {
                            putToDelayedMessages(FileLoader.getAttachFileName(document), delayedMessage2);
                            getFileLoader().loadFile(document, delayedMessage2.parentObject, 2, 0);
                        }
                    }
                }
            } else if (i2 == 3) {
                str2 = delayedMessage2.obj.messageOwner.attachPath;
                putToDelayedMessages(str2, delayedMessage2);
                FileLoader fileLoader2 = getFileLoader();
                if (delayedMessage2.sendRequest == null) {
                    z = true;
                }
                fileLoader2.uploadFile(str2, z, true, 50331648);
                putToUploadingMessages(delayedMessage2.obj);
            } else if (i2 == 4) {
                boolean z3;
                boolean z4 = i < 0;
                boolean z5;
                if (delayedMessage2.performMediaUpload) {
                    int size = i < 0 ? delayedMessage2.messageObjects.size() - 1 : i;
                    MessageObject messageObject3 = (MessageObject) delayedMessage2.messageObjects.get(size);
                    if (messageObject3.getDocument() != null) {
                        str7 = "_t";
                        str8 = "_i";
                        PhotoSize photoSize;
                        HashMap hashMap;
                        StringBuilder stringBuilder3;
                        if (delayedMessage2.videoEditedInfo != null) {
                            str = messageObject3.messageOwner.attachPath;
                            Document document2 = messageObject3.getDocument();
                            if (str == null) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(FileLoader.getDirectory(4));
                                stringBuilder.append(str6);
                                stringBuilder.append(document2.id);
                                stringBuilder.append(str5);
                                str = stringBuilder.toString();
                            }
                            putToDelayedMessages(str, delayedMessage2);
                            delayedMessage2.extraHashMap.put(messageObject3, str);
                            HashMap hashMap2 = delayedMessage2.extraHashMap;
                            StringBuilder stringBuilder4 = new StringBuilder();
                            stringBuilder4.append(str);
                            stringBuilder4.append(str8);
                            hashMap2.put(stringBuilder4.toString(), messageObject3);
                            photoSize = delayedMessage2.photoSize;
                            if (!(photoSize == null || photoSize.location == null)) {
                                hashMap = delayedMessage2.extraHashMap;
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(str);
                                stringBuilder3.append(str7);
                                hashMap.put(stringBuilder3.toString(), delayedMessage2.photoSize);
                            }
                            MediaController.getInstance().scheduleVideoConvert(messageObject3);
                            delayedMessage2.obj = messageObject3;
                            putToUploadingMessages(messageObject3);
                        } else {
                            MessageObject messageObject4;
                            Document document3 = messageObject3.getDocument();
                            String str9 = messageObject3.messageOwner.attachPath;
                            if (str9 == null) {
                                StringBuilder stringBuilder5 = new StringBuilder();
                                stringBuilder5.append(FileLoader.getDirectory(4));
                                stringBuilder5.append(str6);
                                messageObject4 = messageObject3;
                                stringBuilder5.append(document3.id);
                                stringBuilder5.append(str5);
                                str9 = stringBuilder5.toString();
                            } else {
                                messageObject4 = messageObject3;
                            }
                            TLObject tLObject2 = delayedMessage2.sendRequest;
                            HashMap hashMap3;
                            StringBuilder stringBuilder6;
                            VideoEditedInfo videoEditedInfo3;
                            if (tLObject2 != null) {
                                InputMedia inputMedia2 = ((TL_inputSingleMedia) ((TL_messages_sendMultiMedia) tLObject2).multi_media.get(size)).media;
                                if (inputMedia2.file == null) {
                                    putToDelayedMessages(str9, delayedMessage2);
                                    messageObject3 = messageObject4;
                                    delayedMessage2.extraHashMap.put(messageObject3, str9);
                                    delayedMessage2.extraHashMap.put(str9, inputMedia2);
                                    hashMap3 = delayedMessage2.extraHashMap;
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append(str9);
                                    stringBuilder3.append(str8);
                                    hashMap3.put(stringBuilder3.toString(), messageObject3);
                                    photoSize = delayedMessage2.photoSize;
                                    if (!(photoSize == null || photoSize.location == null)) {
                                        hashMap = delayedMessage2.extraHashMap;
                                        stringBuilder6 = new StringBuilder();
                                        stringBuilder6.append(str9);
                                        stringBuilder6.append(str7);
                                        hashMap.put(stringBuilder6.toString(), delayedMessage2.photoSize);
                                    }
                                    videoEditedInfo3 = messageObject3.videoEditedInfo;
                                    if (videoEditedInfo3 == null || !videoEditedInfo3.needConvert()) {
                                        getFileLoader().uploadFile(str9, false, false, 33554432);
                                    } else {
                                        getFileLoader().uploadFile(str9, false, false, document3.size, 33554432);
                                    }
                                    putToUploadingMessages(messageObject3);
                                } else {
                                    messageObject3 = messageObject4;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(FileLoader.getDirectory(4));
                                    stringBuilder.append(str6);
                                    stringBuilder.append(delayedMessage2.photoSize.location.volume_id);
                                    stringBuilder.append(str4);
                                    stringBuilder.append(delayedMessage2.photoSize.location.local_id);
                                    stringBuilder.append(str3);
                                    str = stringBuilder.toString();
                                    putToDelayedMessages(str, delayedMessage2);
                                    hashMap = delayedMessage2.extraHashMap;
                                    stringBuilder6 = new StringBuilder();
                                    stringBuilder6.append(str);
                                    stringBuilder6.append("_o");
                                    hashMap.put(stringBuilder6.toString(), str9);
                                    delayedMessage2.extraHashMap.put(messageObject3, str);
                                    delayedMessage2.extraHashMap.put(str, inputMedia2);
                                    getFileLoader().uploadFile(str, false, true, 16777216);
                                    putToUploadingMessages(messageObject3);
                                }
                            } else {
                                messageObject3 = messageObject4;
                                TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TL_messages_sendEncryptedMultiMedia) delayedMessage2.sendEncryptedRequest;
                                putToDelayedMessages(str9, delayedMessage2);
                                delayedMessage2.extraHashMap.put(messageObject3, str9);
                                delayedMessage2.extraHashMap.put(str9, tL_messages_sendEncryptedMultiMedia.files.get(size));
                                hashMap3 = delayedMessage2.extraHashMap;
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(str9);
                                stringBuilder3.append(str8);
                                hashMap3.put(stringBuilder3.toString(), messageObject3);
                                photoSize = delayedMessage2.photoSize;
                                if (!(photoSize == null || photoSize.location == null)) {
                                    hashMap = delayedMessage2.extraHashMap;
                                    stringBuilder6 = new StringBuilder();
                                    stringBuilder6.append(str9);
                                    stringBuilder6.append(str7);
                                    hashMap.put(stringBuilder6.toString(), delayedMessage2.photoSize);
                                }
                                videoEditedInfo3 = messageObject3.videoEditedInfo;
                                if (videoEditedInfo3 == null || !videoEditedInfo3.needConvert()) {
                                    getFileLoader().uploadFile(str9, true, false, 33554432);
                                } else {
                                    getFileLoader().uploadFile(str9, true, false, document3.size, 33554432);
                                }
                                putToUploadingMessages(messageObject3);
                            }
                        }
                        delayedMessage2.videoEditedInfo = null;
                        delayedMessage2.photoSize = null;
                    } else {
                        str8 = delayedMessage2.httpLocation;
                        if (str8 != null) {
                            putToDelayedMessages(str8, delayedMessage2);
                            delayedMessage2.extraHashMap.put(messageObject3, delayedMessage2.httpLocation);
                            delayedMessage2.extraHashMap.put(delayedMessage2.httpLocation, messageObject3);
                            ImageLoader.getInstance().loadHttpFile(delayedMessage2.httpLocation, str, this.currentAccount);
                            delayedMessage2.httpLocation = null;
                        } else {
                            Object obj;
                            TLObject tLObject3 = delayedMessage2.sendRequest;
                            if (tLObject3 != null) {
                                obj = ((TL_inputSingleMedia) ((TL_messages_sendMultiMedia) tLObject3).multi_media.get(size)).media;
                            } else {
                                obj = (TLObject) ((TL_messages_sendEncryptedMultiMedia) delayedMessage2.sendEncryptedRequest).files.get(size);
                            }
                            str8 = FileLoader.getPathToAttach(delayedMessage2.photoSize).toString();
                            putToDelayedMessages(str8, delayedMessage2);
                            delayedMessage2.extraHashMap.put(str8, obj);
                            delayedMessage2.extraHashMap.put(messageObject3, str8);
                            z3 = true;
                            getFileLoader().uploadFile(str8, delayedMessage2.sendEncryptedRequest != null, true, 16777216);
                            putToUploadingMessages(messageObject3);
                            delayedMessage2.photoSize = null;
                            z5 = false;
                            delayedMessage2.performMediaUpload = z5;
                        }
                    }
                    z5 = false;
                    z3 = true;
                    delayedMessage2.performMediaUpload = z5;
                } else {
                    z5 = false;
                    z3 = true;
                    if (!delayedMessage2.messageObjects.isEmpty()) {
                        ArrayList arrayList = delayedMessage2.messageObjects;
                        Message message = ((MessageObject) arrayList.get(arrayList.size() - 1)).messageOwner;
                        if (delayedMessage2.finalGroupMessage != 0) {
                            z5 = true;
                        }
                        putToSendingMessages(message, z5);
                    }
                }
                sendReadyToSendGroup(delayedMessage2, z4, z3);
            }
        }
    }

    private void uploadMultiMedia(DelayedMessage delayedMessage, InputMedia inputMedia, InputEncryptedFile inputEncryptedFile, String str) {
        Float valueOf = Float.valueOf(1.0f);
        Boolean valueOf2 = Boolean.valueOf(false);
        int i;
        if (inputMedia != null) {
            TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TL_messages_sendMultiMedia) delayedMessage.sendRequest;
            for (i = 0; i < tL_messages_sendMultiMedia.multi_media.size(); i++) {
                if (((TL_inputSingleMedia) tL_messages_sendMultiMedia.multi_media.get(i)).media == inputMedia) {
                    putToSendingMessages((Message) delayedMessage.messages.get(i), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, valueOf, valueOf2);
                    break;
                }
            }
            TL_messages_uploadMedia tL_messages_uploadMedia = new TL_messages_uploadMedia();
            tL_messages_uploadMedia.media = inputMedia;
            tL_messages_uploadMedia.peer = ((TL_messages_sendMultiMedia) delayedMessage.sendRequest).peer;
            getConnectionsManager().sendRequest(tL_messages_uploadMedia, new -$$Lambda$SendMessagesHelper$RLXwYYsRORY3jGEkK3Ou05-sCL4(this, inputMedia, delayedMessage));
        } else if (inputEncryptedFile != null) {
            TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
            for (i = 0; i < tL_messages_sendEncryptedMultiMedia.files.size(); i++) {
                if (tL_messages_sendEncryptedMultiMedia.files.get(i) == inputEncryptedFile) {
                    putToSendingMessages((Message) delayedMessage.messages.get(i), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, valueOf, valueOf2);
                    break;
                }
            }
            sendReadyToSendGroup(delayedMessage, false, true);
        }
    }

    public /* synthetic */ void lambda$uploadMultiMedia$21$SendMessagesHelper(InputMedia inputMedia, DelayedMessage delayedMessage, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$XhaEuHg16H1H8fLlbmKx3IyGHxE(this, tLObject, inputMedia, delayedMessage));
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    public /* synthetic */ void lambda$null$20$SendMessagesHelper(org.telegram.tgnet.TLObject r6, org.telegram.tgnet.TLRPC.InputMedia r7, org.telegram.messenger.SendMessagesHelper.DelayedMessage r8) {
        /*
        r5 = this;
        if (r6 == 0) goto L_0x004e;
    L_0x0002:
        r6 = (org.telegram.tgnet.TLRPC.MessageMedia) r6;
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedPhoto;
        if (r0 == 0) goto L_0x0029;
    L_0x0008:
        r0 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 == 0) goto L_0x0029;
    L_0x000c:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;
        r0.<init>();
        r1 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;
        r1.<init>();
        r0.id = r1;
        r1 = r0.id;
        r6 = r6.photo;
        r2 = r6.id;
        r1.id = r2;
        r2 = r6.access_hash;
        r1.access_hash = r2;
        r6 = r6.file_reference;
        r1.file_reference = r6;
        goto L_0x004f;
    L_0x0029:
        r0 = r7 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;
        if (r0 == 0) goto L_0x004e;
    L_0x002d:
        r0 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r0 == 0) goto L_0x004e;
    L_0x0031:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;
        r0.<init>();
        r1 = new org.telegram.tgnet.TLRPC$TL_inputDocument;
        r1.<init>();
        r0.id = r1;
        r1 = r0.id;
        r6 = r6.document;
        r2 = r6.id;
        r1.id = r2;
        r2 = r6.access_hash;
        r1.access_hash = r2;
        r6 = r6.file_reference;
        r1.file_reference = r6;
        goto L_0x004f;
    L_0x004e:
        r0 = 0;
    L_0x004f:
        if (r0 == 0) goto L_0x0089;
    L_0x0051:
        r6 = r7.ttl_seconds;
        r1 = 1;
        if (r6 == 0) goto L_0x005d;
    L_0x0056:
        r0.ttl_seconds = r6;
        r6 = r0.flags;
        r6 = r6 | r1;
        r0.flags = r6;
    L_0x005d:
        r6 = r8.sendRequest;
        r6 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r6;
        r2 = 0;
        r3 = 0;
    L_0x0063:
        r4 = r6.multi_media;
        r4 = r4.size();
        if (r3 >= r4) goto L_0x0085;
    L_0x006b:
        r4 = r6.multi_media;
        r4 = r4.get(r3);
        r4 = (org.telegram.tgnet.TLRPC.TL_inputSingleMedia) r4;
        r4 = r4.media;
        if (r4 != r7) goto L_0x0082;
    L_0x0077:
        r6 = r6.multi_media;
        r6 = r6.get(r3);
        r6 = (org.telegram.tgnet.TLRPC.TL_inputSingleMedia) r6;
        r6.media = r0;
        goto L_0x0085;
    L_0x0082:
        r3 = r3 + 1;
        goto L_0x0063;
    L_0x0085:
        r5.sendReadyToSendGroup(r8, r2, r1);
        goto L_0x008c;
    L_0x0089:
        r8.markAsError();
    L_0x008c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$20$SendMessagesHelper(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$InputMedia, org.telegram.messenger.SendMessagesHelper$DelayedMessage):void");
    }

    private void sendReadyToSendGroup(DelayedMessage delayedMessage, boolean z, boolean z2) {
        if (delayedMessage.messageObjects.isEmpty()) {
            delayedMessage.markAsError();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("group_");
        stringBuilder.append(delayedMessage.groupId);
        String stringBuilder2 = stringBuilder.toString();
        int i = delayedMessage.finalGroupMessage;
        ArrayList arrayList = delayedMessage.messageObjects;
        if (i != ((MessageObject) arrayList.get(arrayList.size() - 1)).getId()) {
            if (z) {
                putToDelayedMessages(stringBuilder2, delayedMessage);
            }
            return;
        }
        i = 0;
        if (z) {
            this.delayedMessages.remove(stringBuilder2);
            getMessagesStorage().putMessages(delayedMessage.messages, false, true, false, 0, delayedMessage.scheduled);
            getMessagesController().updateInterfaceWithMessages(delayedMessage.peer, delayedMessage.messageObjects, delayedMessage.scheduled);
            if (!delayedMessage.scheduled) {
                getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
        }
        TLObject tLObject = delayedMessage.sendRequest;
        if (tLObject instanceof TL_messages_sendMultiMedia) {
            TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TL_messages_sendMultiMedia) tLObject;
            while (i < tL_messages_sendMultiMedia.multi_media.size()) {
                InputMedia inputMedia = ((TL_inputSingleMedia) tL_messages_sendMultiMedia.multi_media.get(i)).media;
                if (!(inputMedia instanceof TL_inputMediaUploadedPhoto) && !(inputMedia instanceof TL_inputMediaUploadedDocument)) {
                    i++;
                } else {
                    return;
                }
            }
            if (z2) {
                DelayedMessage findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(delayedMessage.finalGroupMessage, delayedMessage.peer);
                if (findMaxDelayedMessageForMessageId != null) {
                    findMaxDelayedMessageForMessageId.addDelayedRequest(delayedMessage.sendRequest, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage, delayedMessage.scheduled);
                    ArrayList arrayList2 = delayedMessage.requests;
                    if (arrayList2 != null) {
                        findMaxDelayedMessageForMessageId.requests.addAll(arrayList2);
                    }
                    return;
                }
            }
        }
        TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
        while (i < tL_messages_sendEncryptedMultiMedia.files.size()) {
            if (!(((InputEncryptedFile) tL_messages_sendEncryptedMultiMedia.files.get(i)) instanceof TL_inputEncryptedFile)) {
                i++;
            } else {
                return;
            }
        }
        tLObject = delayedMessage.sendRequest;
        if (tLObject instanceof TL_messages_sendMultiMedia) {
            performSendMessageRequestMulti((TL_messages_sendMultiMedia) tLObject, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage, delayedMessage.scheduled);
        } else {
            getSecretChatHelper().performSendEncryptedRequest((TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest, delayedMessage);
        }
        delayedMessage.sendDelayedRequests();
    }

    public /* synthetic */ void lambda$null$22$SendMessagesHelper(String str) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, str, Integer.valueOf(this.currentAccount));
    }

    public /* synthetic */ void lambda$stopVideoService$23$SendMessagesHelper(String str) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$3pNSVwvhFgR9pNLLpfFy9Psg-xw(this, str));
    }

    /* Access modifiers changed, original: protected */
    public void stopVideoService(String str) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$Dg3QTil7mAE2shO_mdI1reoBk9M(this, str));
    }

    /* Access modifiers changed, original: protected */
    public void putToSendingMessages(Message message, boolean z) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$ZVeLcx4KxV6K-7kn-uG_Rx4zzE8(this, message, z));
        } else {
            putToSendingMessages(message, z, true);
        }
    }

    public /* synthetic */ void lambda$putToSendingMessages$24$SendMessagesHelper(Message message, boolean z) {
        putToSendingMessages(message, z, true);
    }

    /* Access modifiers changed, original: protected */
    public void putToSendingMessages(Message message, boolean z, boolean z2) {
        if (message != null) {
            int i = message.id;
            if (i > 0) {
                this.editingMessages.put(i, message);
            } else {
                Object obj = this.sendingMessages.indexOfKey(i) >= 0 ? 1 : null;
                removeFromUploadingMessages(message.id, z);
                this.sendingMessages.put(message.id, message);
                if (!z && obj == null) {
                    long dialogId = MessageObject.getDialogId(message);
                    LongSparseArray longSparseArray = this.sendingMessagesIdDialogs;
                    longSparseArray.put(dialogId, Integer.valueOf(((Integer) longSparseArray.get(dialogId, Integer.valueOf(0))).intValue() + 1));
                    if (z2) {
                        getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
                    }
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public Message removeFromSendingMessages(int i, boolean z) {
        if (i > 0) {
            Message message = (Message) this.editingMessages.get(i);
            if (message == null) {
                return message;
            }
            this.editingMessages.remove(i);
            return message;
        }
        Message message2 = (Message) this.sendingMessages.get(i);
        if (message2 != null) {
            this.sendingMessages.remove(i);
            if (!z) {
                long dialogId = MessageObject.getDialogId(message2);
                Integer num = (Integer) this.sendingMessagesIdDialogs.get(dialogId);
                if (num != null) {
                    int intValue = num.intValue() - 1;
                    if (intValue <= 0) {
                        this.sendingMessagesIdDialogs.remove(dialogId);
                    } else {
                        this.sendingMessagesIdDialogs.put(dialogId, Integer.valueOf(intValue));
                    }
                    getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
                }
            }
        }
        return message2;
    }

    public int getSendingMessageId(long j) {
        int i;
        Message message;
        for (i = 0; i < this.sendingMessages.size(); i++) {
            message = (Message) this.sendingMessages.valueAt(i);
            if (message.dialog_id == j) {
                return message.id;
            }
        }
        for (i = 0; i < this.uploadMessages.size(); i++) {
            message = (Message) this.uploadMessages.valueAt(i);
            if (message.dialog_id == j) {
                return message.id;
            }
        }
        return 0;
    }

    /* Access modifiers changed, original: protected */
    public void putToUploadingMessages(MessageObject messageObject) {
        if (messageObject != null && messageObject.getId() <= 0 && !messageObject.scheduled) {
            Message message = messageObject.messageOwner;
            Object obj = this.uploadMessages.indexOfKey(message.id) >= 0 ? 1 : null;
            this.uploadMessages.put(message.id, message);
            if (obj == null) {
                long dialogId = MessageObject.getDialogId(message);
                LongSparseArray longSparseArray = this.uploadingMessagesIdDialogs;
                longSparseArray.put(dialogId, Integer.valueOf(((Integer) longSparseArray.get(dialogId, Integer.valueOf(0))).intValue() + 1));
                getNotificationCenter().postNotificationName(NotificationCenter.sendingMessagesChanged, new Object[0]);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void removeFromUploadingMessages(int i, boolean z) {
        if (i <= 0 && !z) {
            Message message = (Message) this.uploadMessages.get(i);
            if (message != null) {
                this.uploadMessages.remove(i);
                long dialogId = MessageObject.getDialogId(message);
                Integer num = (Integer) this.uploadingMessagesIdDialogs.get(dialogId);
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
    }

    public boolean isSendingMessage(int i) {
        return this.sendingMessages.indexOfKey(i) >= 0 || this.editingMessages.indexOfKey(i) >= 0;
    }

    public boolean isSendingMessageIdDialog(long j) {
        return ((Integer) this.sendingMessagesIdDialogs.get(j, Integer.valueOf(0))).intValue() > 0;
    }

    public boolean isUploadingMessageIdDialog(long j) {
        return ((Integer) this.uploadingMessagesIdDialogs.get(j, Integer.valueOf(0))).intValue() > 0;
    }

    /* Access modifiers changed, original: protected */
    public void performSendMessageRequestMulti(TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList<MessageObject> arrayList, ArrayList<String> arrayList2, ArrayList<Object> arrayList3, DelayedMessage delayedMessage, boolean z) {
        ArrayList<MessageObject> arrayList4;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList4 = arrayList;
            putToSendingMessages(((MessageObject) arrayList.get(i)).messageOwner, z);
        }
        arrayList4 = arrayList;
        boolean z2 = z;
        TL_messages_sendMultiMedia tL_messages_sendMultiMedia2 = tL_messages_sendMultiMedia;
        getConnectionsManager().sendRequest((TLObject) tL_messages_sendMultiMedia, new -$$Lambda$SendMessagesHelper$TKch5h5XAPJtFMP9aSm_X8491Aw(this, arrayList3, tL_messages_sendMultiMedia, arrayList4, arrayList2, delayedMessage, z2), null, 68);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$31$SendMessagesHelper(ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, boolean z, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$xznCANcJWyi8znsjq8i-J_AXO7A(this, tL_error, arrayList, tL_messages_sendMultiMedia, arrayList2, arrayList3, delayedMessage, z, tLObject));
    }

    public /* synthetic */ void lambda$null$30$SendMessagesHelper(TL_error tL_error, ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, boolean z, TLObject tLObject) {
        ArrayList arrayList4;
        Message message;
        int i;
        int i2;
        Object obj;
        TL_error tL_error2 = tL_error;
        ArrayList arrayList5 = arrayList;
        TLObject tLObject2 = tL_messages_sendMultiMedia;
        ArrayList arrayList6 = arrayList2;
        ArrayList arrayList7 = arrayList3;
        boolean z2 = z;
        if (tL_error2 != null && FileRefController.isFileRefError(tL_error2.text)) {
            if (arrayList5 != null) {
                getFileRefController().requestReference(arrayList4, tLObject2, arrayList6, arrayList7, new ArrayList(arrayList5), delayedMessage, Boolean.valueOf(z));
                return;
            } else if (delayedMessage != null) {
                final TL_messages_sendMultiMedia tL_messages_sendMultiMedia2 = tL_messages_sendMultiMedia;
                final DelayedMessage delayedMessage2 = delayedMessage;
                final ArrayList arrayList8 = arrayList2;
                final boolean z3 = z;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int size = tL_messages_sendMultiMedia2.multi_media.size();
                        for (int i = 0; i < size; i++) {
                            if (delayedMessage2.parentObjects.get(i) != null) {
                                SendMessagesHelper.this.removeFromSendingMessages(((MessageObject) arrayList8.get(i)).getId(), z3);
                                TL_inputSingleMedia tL_inputSingleMedia = (TL_inputSingleMedia) tL_messages_sendMultiMedia2.multi_media.get(i);
                                InputMedia inputMedia = tL_inputSingleMedia.media;
                                if (inputMedia instanceof TL_inputMediaPhoto) {
                                    tL_inputSingleMedia.media = (InputMedia) delayedMessage2.inputMedias.get(i);
                                } else if (inputMedia instanceof TL_inputMediaDocument) {
                                    tL_inputSingleMedia.media = (InputMedia) delayedMessage2.inputMedias.get(i);
                                }
                                DelayedMessage delayedMessage = delayedMessage2;
                                delayedMessage.videoEditedInfo = (VideoEditedInfo) delayedMessage.videoEditedInfos.get(i);
                                delayedMessage = delayedMessage2;
                                delayedMessage.httpLocation = (String) delayedMessage.httpLocations.get(i);
                                delayedMessage = delayedMessage2;
                                delayedMessage.photoSize = (PhotoSize) delayedMessage.locations.get(i);
                                delayedMessage = delayedMessage2;
                                delayedMessage.performMediaUpload = true;
                                SendMessagesHelper.this.performSendDelayedMessage(delayedMessage, i);
                            }
                        }
                    }
                });
                return;
            }
        }
        int i3;
        if (tL_error2 == null) {
            Updates updates;
            SparseArray sparseArray = new SparseArray();
            LongSparseArray longSparseArray = new LongSparseArray();
            Updates updates2 = (Updates) tLObject;
            arrayList4 = updates2.updates;
            int i4 = 0;
            while (i4 < arrayList4.size()) {
                Update update = (Update) arrayList4.get(i4);
                Message message2;
                if (update instanceof TL_updateMessageID) {
                    TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) update;
                    longSparseArray.put(tL_updateMessageID.random_id, Integer.valueOf(tL_updateMessageID.id));
                    arrayList4.remove(i4);
                } else if (update instanceof TL_updateNewMessage) {
                    TL_updateNewMessage tL_updateNewMessage = (TL_updateNewMessage) update;
                    message2 = tL_updateNewMessage.message;
                    sparseArray.put(message2.id, message2);
                    Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$WEf3-J9E0RuM0uFpMpKJwD2vmTI(this, tL_updateNewMessage));
                    arrayList4.remove(i4);
                } else if (update instanceof TL_updateNewChannelMessage) {
                    TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                    message2 = tL_updateNewChannelMessage.message;
                    sparseArray.put(message2.id, message2);
                    Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$Wl2FfokI5thW6ljuxQy9XyKtwF0(this, tL_updateNewChannelMessage));
                    arrayList4.remove(i4);
                } else if (update instanceof TL_updateNewScheduledMessage) {
                    Message message3 = ((TL_updateNewScheduledMessage) update).message;
                    sparseArray.put(message3.id, message3);
                    arrayList4.remove(i4);
                } else {
                    i4++;
                }
                i4--;
                i4++;
            }
            int i5 = 0;
            while (i5 < arrayList2.size()) {
                MessageObject messageObject = (MessageObject) arrayList6.get(i5);
                String str = (String) arrayList7.get(i5);
                message = messageObject.messageOwner;
                i = message.id;
                ArrayList arrayList9 = new ArrayList();
                String str2 = message.attachPath;
                Integer num = (Integer) longSparseArray.get(message.random_id);
                if (num != null) {
                    Message message4 = (Message) sparseArray.get(num.intValue());
                    if (message4 != null) {
                        arrayList9.add(message4);
                        int i6 = i;
                        Message message5 = message;
                        updates = updates2;
                        LongSparseArray longSparseArray2 = longSparseArray;
                        SparseArray sparseArray2 = sparseArray;
                        updateMediaPaths(messageObject, message4, message4.id, str, false);
                        int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                        Message message6 = message5;
                        message6.id = message4.id;
                        if ((message6.flags & Integer.MIN_VALUE) != 0) {
                            message4.flags |= Integer.MIN_VALUE;
                        }
                        long j = message4.grouped_id;
                        if (!z2) {
                            Integer num2 = (Integer) getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message4.dialog_id));
                            if (num2 == null) {
                                num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(message4.out, message4.dialog_id));
                                getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message4.dialog_id), num2);
                            }
                            message4.unread = num2.intValue() < message4.id;
                        }
                        getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                        message6.send_state = 0;
                        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i6), Integer.valueOf(message6.id), message6, Long.valueOf(message6.dialog_id), Long.valueOf(j), Integer.valueOf(mediaExistanceFlags), Boolean.valueOf(z));
                        DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                        -$$Lambda$SendMessagesHelper$SN9ctj1DJZVrgVAT-7xSWqr6wLU -__lambda_sendmessageshelper_sn9ctj1djzvrgvat-7xswqr6wlu = r0;
                        -$$Lambda$SendMessagesHelper$SN9ctj1DJZVrgVAT-7xSWqr6wLU -__lambda_sendmessageshelper_sn9ctj1djzvrgvat-7xswqr6wlu2 = new -$$Lambda$SendMessagesHelper$SN9ctj1DJZVrgVAT-7xSWqr6wLU(this, message6, i6, z, arrayList9, j, mediaExistanceFlags);
                        storageQueue.postRunnable(-__lambda_sendmessageshelper_sn9ctj1djzvrgvat-7xswqr6wlu);
                        i5++;
                        arrayList7 = arrayList3;
                        z2 = z;
                        updates2 = updates;
                        longSparseArray = longSparseArray2;
                        sparseArray = sparseArray2;
                    }
                }
                updates = updates2;
                i2 = 2;
                i3 = 0;
                obj = 1;
            }
            updates = updates2;
            i2 = 2;
            i3 = 0;
            obj = null;
            Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$1NIkJ-4NYL7cJrXLFelaiYrM_ks(this, updates));
        } else {
            i2 = 2;
            i3 = 0;
            AlertsCreator.processError(this.currentAccount, tL_error2, null, tLObject2, new Object[0]);
            obj = 1;
        }
        if (obj != null) {
            for (i = 0; i < arrayList2.size(); i++) {
                message = ((MessageObject) arrayList6.get(i)).messageOwner;
                boolean z4 = z;
                getMessagesStorage().markMessageAsSendError(message, z4);
                message.send_state = i2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
                processSentMessage(message.id);
                removeFromSendingMessages(message.id, z4);
            }
        }
    }

    public /* synthetic */ void lambda$null$25$SendMessagesHelper(TL_updateNewMessage tL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$26$SendMessagesHelper(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$28$SendMessagesHelper(Message message, int i, boolean z, ArrayList arrayList, long j, int i2) {
        Message message2 = message;
        getMessagesStorage().updateMessageStateAndId(message2.random_id, Integer.valueOf(i), message2.id, 0, false, message2.to_id.channel_id, z);
        getMessagesStorage().putMessages(arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$GksW3O6OAJuxNf2ttkVGhe-LT-U(this, message, i, j, i2, z));
    }

    public /* synthetic */ void lambda$null$27$SendMessagesHelper(Message message, int i, long j, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), Long.valueOf(j), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$29$SendMessagesHelper(Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    private void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, Object obj, boolean z) {
        performSendMessageRequest(tLObject, messageObject, str, null, false, delayedMessage, obj, z);
    }

    private DelayedMessage findMaxDelayedMessageForMessageId(int i, long j) {
        DelayedMessage delayedMessage = null;
        int i2 = Integer.MIN_VALUE;
        for (Entry value : this.delayedMessages.entrySet()) {
            ArrayList arrayList = (ArrayList) value.getValue();
            int size = arrayList.size();
            int i3 = i2;
            DelayedMessage delayedMessage2 = delayedMessage;
            for (int i4 = 0; i4 < size; i4++) {
                DelayedMessage delayedMessage3 = (DelayedMessage) arrayList.get(i4);
                int i5 = delayedMessage3.type;
                if ((i5 == 4 || i5 == 0) && delayedMessage3.peer == j) {
                    MessageObject messageObject = delayedMessage3.obj;
                    if (messageObject != null) {
                        i5 = messageObject.getId();
                    } else {
                        ArrayList arrayList2 = delayedMessage3.messageObjects;
                        if (arrayList2 == null || arrayList2.isEmpty()) {
                            i5 = 0;
                        } else {
                            arrayList2 = delayedMessage3.messageObjects;
                            i5 = ((MessageObject) arrayList2.get(arrayList2.size() - 1)).getId();
                        }
                    }
                    if (i5 != 0 && i5 > i && delayedMessage2 == null && i3 < i5) {
                        delayedMessage2 = delayedMessage3;
                        i3 = i5;
                    }
                }
            }
            delayedMessage = delayedMessage2;
            i2 = i3;
        }
        return delayedMessage;
    }

    /* Access modifiers changed, original: protected */
    public void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, Object obj, boolean z2) {
        TLObject tLObject2 = tLObject;
        DelayedMessage delayedMessage3 = delayedMessage;
        if (!(tLObject2 instanceof TL_messages_editMessage) && z) {
            DelayedMessage findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(messageObject.getId(), messageObject.getDialogId());
            if (findMaxDelayedMessageForMessageId != null) {
                findMaxDelayedMessageForMessageId.addDelayedRequest(tLObject, messageObject, str, obj, delayedMessage2, delayedMessage3 != null ? delayedMessage3.scheduled : false);
                if (delayedMessage3 != null) {
                    ArrayList arrayList = delayedMessage3.requests;
                    if (arrayList != null) {
                        findMaxDelayedMessageForMessageId.requests.addAll(arrayList);
                    }
                }
                return;
            }
        }
        Message message = messageObject.messageOwner;
        boolean z3 = z2;
        putToSendingMessages(message, z3);
        RequestDelegate requestDelegate = r0;
        ConnectionsManager connectionsManager = getConnectionsManager();
        -$$Lambda$SendMessagesHelper$OGo7iVrzEVtn_VXwZc8-VVr-sOI -__lambda_sendmessageshelper_ogo7ivrzevtn_vxwzc8-vvr-soi = new -$$Lambda$SendMessagesHelper$OGo7iVrzEVtn_VXwZc8-VVr-sOI(this, tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, z3, message);
        message.reqId = connectionsManager.sendRequest(tLObject2, requestDelegate, new -$$Lambda$SendMessagesHelper$zDWuXZ4QQhTdHBax3KvIHfigGOU(this, message), (tLObject2 instanceof TL_messages_sendMessage ? 128 : 0) | 68);
        if (delayedMessage != null) {
            delayedMessage.sendDelayedRequests();
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequest$42$SendMessagesHelper(TLObject tLObject, Object obj, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, boolean z2, Message message, TLObject tLObject2, TL_error tL_error) {
        TLObject tLObject3 = tLObject;
        Object obj2 = obj;
        TL_error tL_error2 = tL_error;
        if (tL_error2 != null && (((tLObject3 instanceof TL_messages_sendMedia) || (tLObject3 instanceof TL_messages_editMessage)) && FileRefController.isFileRefError(tL_error2.text))) {
            if (obj2 != null) {
                getFileRefController().requestReference(obj, tLObject3, messageObject, str, delayedMessage, Boolean.valueOf(z), delayedMessage2, Boolean.valueOf(z2));
                return;
            } else if (delayedMessage2 != null) {
                final Message message2 = message;
                final boolean z3 = z2;
                final TLObject tLObject4 = tLObject;
                final DelayedMessage delayedMessage3 = delayedMessage2;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        SendMessagesHelper.this.removeFromSendingMessages(message2.id, z3);
                        TLObject tLObject = tLObject4;
                        InputMedia inputMedia;
                        if (tLObject instanceof TL_messages_sendMedia) {
                            TL_messages_sendMedia tL_messages_sendMedia = (TL_messages_sendMedia) tLObject;
                            inputMedia = tL_messages_sendMedia.media;
                            if (inputMedia instanceof TL_inputMediaPhoto) {
                                tL_messages_sendMedia.media = delayedMessage3.inputUploadMedia;
                            } else if (inputMedia instanceof TL_inputMediaDocument) {
                                tL_messages_sendMedia.media = delayedMessage3.inputUploadMedia;
                            }
                        } else if (tLObject instanceof TL_messages_editMessage) {
                            TL_messages_editMessage tL_messages_editMessage = (TL_messages_editMessage) tLObject;
                            inputMedia = tL_messages_editMessage.media;
                            if (inputMedia instanceof TL_inputMediaPhoto) {
                                tL_messages_editMessage.media = delayedMessage3.inputUploadMedia;
                            } else if (inputMedia instanceof TL_inputMediaDocument) {
                                tL_messages_editMessage.media = delayedMessage3.inputUploadMedia;
                            }
                        }
                        DelayedMessage delayedMessage = delayedMessage3;
                        delayedMessage.performMediaUpload = true;
                        SendMessagesHelper.this.performSendDelayedMessage(delayedMessage);
                    }
                });
                return;
            }
        }
        if (tLObject3 instanceof TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$7qvafeBQqMzx_tuLeMhgq4B_AQk(this, tL_error, message, tLObject2, messageObject, str, z2, tLObject));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$FMop2xVtrxzYYwFIeD2ffegZsLM(this, tL_error, message, tLObject2, messageObject, z2, str, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$34$SendMessagesHelper(TL_error tL_error, Message message, TLObject tLObject, MessageObject messageObject, String str, boolean z, TLObject tLObject2) {
        int i = 0;
        Message message2 = null;
        if (tL_error == null) {
            String str2 = message.attachPath;
            Updates updates = (Updates) tLObject;
            ArrayList arrayList = updates.updates;
            while (i < arrayList.size()) {
                Update update = (Update) arrayList.get(i);
                if (update instanceof TL_updateEditMessage) {
                    message2 = ((TL_updateEditMessage) update).message;
                    break;
                } else if (update instanceof TL_updateEditChannelMessage) {
                    message2 = ((TL_updateEditChannelMessage) update).message;
                    break;
                } else if (update instanceof TL_updateNewScheduledMessage) {
                    message2 = ((TL_updateNewScheduledMessage) update).message;
                    break;
                } else {
                    i++;
                }
            }
            Message message3 = message2;
            if (message3 != null) {
                ImageLoader.saveMessageThumbs(message3);
                updateMediaPaths(messageObject, message3, message3.id, str, false);
            }
            Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$PdW_pKJVWk0744XRiNGlh7K_NuI(this, updates, message, z));
            if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
                stopVideoService(str2);
                return;
            }
            return;
        }
        AlertsCreator.processError(this.currentAccount, tL_error, null, tLObject2, new Object[0]);
        if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
            stopVideoService(message.attachPath);
        }
        removeFromSendingMessages(message.id, z);
        revertEditingMessageObject(messageObject);
    }

    public /* synthetic */ void lambda$null$33$SendMessagesHelper(Updates updates, Message message, boolean z) {
        getMessagesController().processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$x1goTJZC7MbcmchwJN71o_97ai8(this, message, z));
    }

    public /* synthetic */ void lambda$null$32$SendMessagesHelper(Message message, boolean z) {
        processSentMessage(message.id);
        removeFromSendingMessages(message.id, z);
    }

    public /* synthetic */ void lambda$null$41$SendMessagesHelper(TL_error tL_error, Message message, TLObject tLObject, MessageObject messageObject, boolean z, String str, TLObject tLObject2) {
        Object obj;
        TL_error tL_error2 = tL_error;
        Message message2 = message;
        TLObject tLObject3 = tLObject;
        boolean z2 = z;
        Message message3 = null;
        if (tL_error2 == null) {
            int i;
            int i2 = message2.id;
            ArrayList arrayList = new ArrayList();
            String str2 = message2.attachPath;
            int mediaExistanceFlags;
            int i3;
            if (tLObject3 instanceof TL_updateShortSentMessage) {
                TL_updateShortSentMessage tL_updateShortSentMessage = (TL_updateShortSentMessage) tLObject3;
                updateMediaPaths(messageObject, null, tL_updateShortSentMessage.id, null, false);
                mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                i3 = tL_updateShortSentMessage.id;
                message2.id = i3;
                message2.local_id = i3;
                message2.date = tL_updateShortSentMessage.date;
                message2.entities = tL_updateShortSentMessage.entities;
                message2.out = tL_updateShortSentMessage.out;
                MessageMedia messageMedia = tL_updateShortSentMessage.media;
                if (messageMedia != null) {
                    message2.media = messageMedia;
                    message2.flags |= 512;
                    ImageLoader.saveMessageThumbs(message);
                }
                if ((tL_updateShortSentMessage.media instanceof TL_messageMediaGame) && !TextUtils.isEmpty(tL_updateShortSentMessage.message)) {
                    message2.message = tL_updateShortSentMessage.message;
                }
                if (!message2.entities.isEmpty()) {
                    message2.flags |= 128;
                }
                Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$gc3RGANwMTCB1x46daWO-RmqCeA(this, tL_updateShortSentMessage));
                arrayList.add(message2);
                i = mediaExistanceFlags;
                obj = null;
            } else if (tLObject3 instanceof Updates) {
                Object obj2;
                Updates updates = (Updates) tLObject3;
                ArrayList arrayList2 = updates.updates;
                i3 = 0;
                while (i3 < arrayList2.size()) {
                    Update update = (Update) arrayList2.get(i3);
                    if (update instanceof TL_updateNewMessage) {
                        TL_updateNewMessage tL_updateNewMessage = (TL_updateNewMessage) update;
                        message3 = tL_updateNewMessage.message;
                        arrayList.add(message3);
                        Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$1N98oIZfO72oZTruBKvG29-QI2s(this, tL_updateNewMessage));
                        arrayList2.remove(i3);
                        break;
                    } else if (update instanceof TL_updateNewChannelMessage) {
                        TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                        message3 = tL_updateNewChannelMessage.message;
                        arrayList.add(message3);
                        if ((message2.flags & Integer.MIN_VALUE) != 0) {
                            Message message4 = tL_updateNewChannelMessage.message;
                            message4.flags = Integer.MIN_VALUE | message4.flags;
                        }
                        Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$b_RUqgMaYG2nFSme4SlQyoe2uwc(this, tL_updateNewChannelMessage));
                        arrayList2.remove(i3);
                    } else if (update instanceof TL_updateNewScheduledMessage) {
                        TL_updateNewScheduledMessage tL_updateNewScheduledMessage = (TL_updateNewScheduledMessage) update;
                        message3 = tL_updateNewScheduledMessage.message;
                        arrayList.add(message3);
                        if ((message2.flags & Integer.MIN_VALUE) != 0) {
                            Message message5 = tL_updateNewScheduledMessage.message;
                            message5.flags |= Integer.MIN_VALUE;
                        }
                        arrayList2.remove(i3);
                    } else {
                        i3++;
                    }
                }
                Message message6 = message3;
                if (message6 != null) {
                    ImageLoader.saveMessageThumbs(message6);
                    if (!z2) {
                        Integer num = (Integer) getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message6.dialog_id));
                        if (num == null) {
                            num = Integer.valueOf(getMessagesStorage().getDialogReadMax(message6.out, message6.dialog_id));
                            getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message6.dialog_id), num);
                        }
                        message6.unread = num.intValue() < message6.id;
                    }
                    updateMediaPaths(messageObject, message6, message6.id, str, false);
                    mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                    message2.id = message6.id;
                    i3 = mediaExistanceFlags;
                    obj2 = null;
                } else {
                    obj2 = 1;
                    i3 = 0;
                }
                Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$e3nM-WzLFDb8Xk9xv1WTZ8pYeUo(this, updates));
                obj = obj2;
                i = i3;
            } else {
                obj = null;
                i = 0;
            }
            if (MessageObject.isLiveLocationMessage(message)) {
                getLocationController().addSharingLocation(message2.dialog_id, message2.id, message2.media.period, message);
            }
            if (obj == null) {
                getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                message2.send_state = 0;
                getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i2), Integer.valueOf(message2.id), message2, Long.valueOf(message2.dialog_id), Long.valueOf(0), Integer.valueOf(i), Boolean.valueOf(z));
                int i4 = i2;
                DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                i2 = i;
                -$$Lambda$SendMessagesHelper$9hW9qBWiufCfkqqOjdtJD9K379s -__lambda_sendmessageshelper_9hw9qbwiufcfkqqojdtjd9k379s = r0;
                -$$Lambda$SendMessagesHelper$9hW9qBWiufCfkqqOjdtJD9K379s -__lambda_sendmessageshelper_9hw9qbwiufcfkqqojdtjd9k379s2 = new -$$Lambda$SendMessagesHelper$9hW9qBWiufCfkqqOjdtJD9K379s(this, message, i4, z, arrayList, i2, str2);
                storageQueue.postRunnable(-__lambda_sendmessageshelper_9hw9qbwiufcfkqqojdtjd9k379s);
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error2, null, tLObject2, new Object[0]);
            obj = 1;
        }
        if (obj != null) {
            getMessagesStorage().markMessageAsSendError(message2, z2);
            message2.send_state = 2;
            getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message2.id));
            processSentMessage(message2.id);
            if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
                stopVideoService(message2.attachPath);
            }
            removeFromSendingMessages(message2.id, z2);
        }
    }

    public /* synthetic */ void lambda$null$35$SendMessagesHelper(TL_updateShortSentMessage tL_updateShortSentMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateShortSentMessage.pts, tL_updateShortSentMessage.date, tL_updateShortSentMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$36$SendMessagesHelper(TL_updateNewMessage tL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$37$SendMessagesHelper(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$38$SendMessagesHelper(Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    public /* synthetic */ void lambda$null$40$SendMessagesHelper(Message message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        Message message2 = message;
        getMessagesStorage().updateMessageStateAndId(message2.random_id, Integer.valueOf(i), message2.id, 0, false, message2.to_id.channel_id, z);
        getMessagesStorage().putMessages(arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$o_LaUekgoGbVnBQ60e5PVu5JzEw(this, message, i, i2, z));
        if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
            stopVideoService(str);
        }
    }

    public /* synthetic */ void lambda$null$39$SendMessagesHelper(Message message, int i, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), Long.valueOf(0), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$44$SendMessagesHelper(Message message) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$EX23EshquKJz-SF5uOOHEHAjsz4(this, message, message.id));
    }

    public /* synthetic */ void lambda$null$43$SendMessagesHelper(Message message, int i) {
        message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(i));
    }

    /* JADX WARNING: Removed duplicated region for block: B:186:0x0455  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x047a  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x04d6  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x04c8  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0455  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x047a  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x04c8  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x04d6  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0500  */
    private void updateMediaPaths(org.telegram.messenger.MessageObject r17, org.telegram.tgnet.TLRPC.Message r18, int r19, java.lang.String r20, boolean r21) {
        /*
        r16 = this;
        r0 = r17;
        r7 = r18;
        r8 = r20;
        r1 = r21;
        r9 = r0.messageOwner;
        r2 = r9.media;
        r4 = "_";
        if (r2 == 0) goto L_0x0105;
    L_0x0010:
        r5 = r2.photo;
        r6 = 40;
        if (r5 == 0) goto L_0x0034;
    L_0x0016:
        r2 = r5.sizes;
        r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6);
        if (r7 == 0) goto L_0x002d;
    L_0x001e:
        r5 = r7.media;
        if (r5 == 0) goto L_0x002d;
    L_0x0022:
        r5 = r5.photo;
        if (r5 == 0) goto L_0x002d;
    L_0x0026:
        r5 = r5.sizes;
        r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6);
        goto L_0x002e;
    L_0x002d:
        r5 = r2;
    L_0x002e:
        r6 = r9.media;
        r6 = r6.photo;
        goto L_0x00aa;
    L_0x0034:
        r5 = r2.document;
        if (r5 == 0) goto L_0x0055;
    L_0x0038:
        r2 = r5.thumbs;
        r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6);
        if (r7 == 0) goto L_0x004f;
    L_0x0040:
        r5 = r7.media;
        if (r5 == 0) goto L_0x004f;
    L_0x0044:
        r5 = r5.document;
        if (r5 == 0) goto L_0x004f;
    L_0x0048:
        r5 = r5.thumbs;
        r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6);
        goto L_0x0050;
    L_0x004f:
        r5 = r2;
    L_0x0050:
        r6 = r9.media;
        r6 = r6.document;
        goto L_0x00aa;
    L_0x0055:
        r2 = r2.webpage;
        if (r2 == 0) goto L_0x00a7;
    L_0x0059:
        r5 = r2.photo;
        if (r5 == 0) goto L_0x0080;
    L_0x005d:
        r2 = r5.sizes;
        r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6);
        if (r7 == 0) goto L_0x0078;
    L_0x0065:
        r5 = r7.media;
        if (r5 == 0) goto L_0x0078;
    L_0x0069:
        r5 = r5.webpage;
        if (r5 == 0) goto L_0x0078;
    L_0x006d:
        r5 = r5.photo;
        if (r5 == 0) goto L_0x0078;
    L_0x0071:
        r5 = r5.sizes;
        r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6);
        goto L_0x0079;
    L_0x0078:
        r5 = r2;
    L_0x0079:
        r6 = r9.media;
        r6 = r6.webpage;
        r6 = r6.photo;
        goto L_0x00aa;
    L_0x0080:
        r2 = r2.document;
        if (r2 == 0) goto L_0x00a7;
    L_0x0084:
        r2 = r2.thumbs;
        r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r6);
        if (r7 == 0) goto L_0x009f;
    L_0x008c:
        r5 = r7.media;
        if (r5 == 0) goto L_0x009f;
    L_0x0090:
        r5 = r5.webpage;
        if (r5 == 0) goto L_0x009f;
    L_0x0094:
        r5 = r5.document;
        if (r5 == 0) goto L_0x009f;
    L_0x0098:
        r5 = r5.thumbs;
        r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6);
        goto L_0x00a0;
    L_0x009f:
        r5 = r2;
    L_0x00a0:
        r6 = r9.media;
        r6 = r6.webpage;
        r6 = r6.document;
        goto L_0x00aa;
    L_0x00a7:
        r2 = 0;
        r5 = 0;
        r6 = 0;
    L_0x00aa:
        r10 = r5 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r10 == 0) goto L_0x0105;
    L_0x00ae:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r2 == 0) goto L_0x0105;
    L_0x00b2:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r10 = "stripped";
        r2.append(r10);
        r11 = org.telegram.messenger.FileRefController.getKeyForParentObject(r17);
        r2.append(r11);
        r2 = r2.toString();
        if (r7 == 0) goto L_0x00dd;
    L_0x00c9:
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r11.append(r10);
        r10 = org.telegram.messenger.FileRefController.getKeyForParentObject(r18);
        r11.append(r10);
        r10 = r11.toString();
        goto L_0x00fa;
    L_0x00dd:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "strippedmessage";
        r10.append(r11);
        r11 = r19;
        r10.append(r11);
        r10.append(r4);
        r11 = r17.getChannelId();
        r10.append(r11);
        r10 = r10.toString();
    L_0x00fa:
        r11 = org.telegram.messenger.ImageLoader.getInstance();
        r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6);
        r11.replaceImageInCache(r2, r10, r5, r1);
    L_0x0105:
        if (r7 != 0) goto L_0x0108;
    L_0x0107:
        return;
    L_0x0108:
        r2 = r7.media;
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        r10 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r6 = "sent_";
        r12 = ".jpg";
        r13 = 4;
        r14 = 0;
        r15 = 1;
        if (r5 == 0) goto L_0x02b6;
    L_0x0118:
        r5 = r2.photo;
        if (r5 == 0) goto L_0x02b6;
    L_0x011c:
        r5 = r9.media;
        r3 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 == 0) goto L_0x02b6;
    L_0x0122:
        r3 = r5.photo;
        if (r3 == 0) goto L_0x02b6;
    L_0x0126:
        r2 = r2.ttl_seconds;
        if (r2 != 0) goto L_0x0154;
    L_0x012a:
        r0 = r0.scheduled;
        if (r0 != 0) goto L_0x0154;
    L_0x012e:
        r0 = r16.getMessagesStorage();
        r2 = r7.media;
        r2 = r2.photo;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r6);
        r5 = r7.to_id;
        r5 = r5.channel_id;
        r3.append(r5);
        r3.append(r4);
        r5 = r7.id;
        r3.append(r5);
        r3 = r3.toString();
        r0.putSentFile(r8, r2, r14, r3);
    L_0x0154:
        r0 = r9.media;
        r0 = r0.photo;
        r0 = r0.sizes;
        r0 = r0.size();
        if (r0 != r15) goto L_0x0180;
    L_0x0160:
        r0 = r9.media;
        r0 = r0.photo;
        r0 = r0.sizes;
        r0 = r0.get(r14);
        r0 = (org.telegram.tgnet.TLRPC.PhotoSize) r0;
        r0 = r0.location;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r0 == 0) goto L_0x0180;
    L_0x0172:
        r0 = r9.media;
        r0 = r0.photo;
        r1 = r7.media;
        r1 = r1.photo;
        r1 = r1.sizes;
        r0.sizes = r1;
        goto L_0x029c;
    L_0x0180:
        r0 = 0;
    L_0x0181:
        r2 = r7.media;
        r2 = r2.photo;
        r2 = r2.sizes;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x029c;
    L_0x018d:
        r2 = r7.media;
        r2 = r2.photo;
        r2 = r2.sizes;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;
        if (r2 == 0) goto L_0x0295;
    L_0x019b:
        r3 = r2.location;
        if (r3 == 0) goto L_0x0295;
    L_0x019f:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r3 != 0) goto L_0x0295;
    L_0x01a3:
        r3 = r2.type;
        if (r3 != 0) goto L_0x01a9;
    L_0x01a7:
        goto L_0x0295;
    L_0x01a9:
        r3 = 0;
    L_0x01aa:
        r5 = r9.media;
        r5 = r5.photo;
        r5 = r5.sizes;
        r5 = r5.size();
        if (r3 >= r5) goto L_0x0295;
    L_0x01b6:
        r5 = r9.media;
        r5 = r5.photo;
        r5 = r5.sizes;
        r5 = r5.get(r3);
        r5 = (org.telegram.tgnet.TLRPC.PhotoSize) r5;
        if (r5 == 0) goto L_0x028e;
    L_0x01c4:
        r6 = r5.location;
        if (r6 == 0) goto L_0x028e;
    L_0x01c8:
        r8 = r5.type;
        if (r8 != 0) goto L_0x01ce;
    L_0x01cc:
        goto L_0x028e;
    L_0x01ce:
        r14 = r6.volume_id;
        r6 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1));
        if (r6 != 0) goto L_0x01dc;
    L_0x01d4:
        r6 = r2.type;
        r6 = r6.equals(r8);
        if (r6 != 0) goto L_0x01e8;
    L_0x01dc:
        r6 = r2.w;
        r8 = r5.w;
        if (r6 != r8) goto L_0x028e;
    L_0x01e2:
        r6 = r2.h;
        r8 = r5.h;
        if (r6 != r8) goto L_0x028e;
    L_0x01e8:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r6 = r5.location;
        r14 = r6.volume_id;
        r3.append(r14);
        r3.append(r4);
        r6 = r5.location;
        r6 = r6.local_id;
        r3.append(r6);
        r3 = r3.toString();
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r8 = r2.location;
        r14 = r8.volume_id;
        r6.append(r14);
        r6.append(r4);
        r8 = r2.location;
        r8 = r8.local_id;
        r6.append(r8);
        r6 = r6.toString();
        r8 = r3.equals(r6);
        if (r8 == 0) goto L_0x0223;
    L_0x0222:
        goto L_0x0295;
    L_0x0223:
        r8 = new java.io.File;
        r14 = org.telegram.messenger.FileLoader.getDirectory(r13);
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r15.append(r3);
        r15.append(r12);
        r15 = r15.toString();
        r8.<init>(r14, r15);
        r14 = r7.media;
        r15 = r14.ttl_seconds;
        if (r15 != 0) goto L_0x025b;
    L_0x0241:
        r14 = r14.photo;
        r14 = r14.sizes;
        r14 = r14.size();
        r15 = 1;
        if (r14 == r15) goto L_0x0256;
    L_0x024c:
        r14 = r2.w;
        r15 = 90;
        if (r14 > r15) goto L_0x0256;
    L_0x0252:
        r14 = r2.h;
        if (r14 <= r15) goto L_0x025b;
    L_0x0256:
        r14 = org.telegram.messenger.FileLoader.getPathToAttach(r2);
        goto L_0x0273;
    L_0x025b:
        r14 = new java.io.File;
        r15 = org.telegram.messenger.FileLoader.getDirectory(r13);
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r6);
        r13.append(r12);
        r13 = r13.toString();
        r14.<init>(r15, r13);
    L_0x0273:
        r8.renameTo(r14);
        r8 = org.telegram.messenger.ImageLoader.getInstance();
        r13 = r7.media;
        r13 = r13.photo;
        r13 = org.telegram.messenger.ImageLocation.getForPhoto(r2, r13);
        r8.replaceImageInCache(r3, r6, r13, r1);
        r3 = r2.location;
        r5.location = r3;
        r2 = r2.size;
        r5.size = r2;
        goto L_0x0295;
    L_0x028e:
        r3 = r3 + 1;
        r13 = 4;
        r14 = 0;
        r15 = 1;
        goto L_0x01aa;
    L_0x0295:
        r0 = r0 + 1;
        r13 = 4;
        r14 = 0;
        r15 = 1;
        goto L_0x0181;
    L_0x029c:
        r0 = r9.message;
        r7.message = r0;
        r0 = r9.attachPath;
        r7.attachPath = r0;
        r0 = r9.media;
        r0 = r0.photo;
        r1 = r7.media;
        r1 = r1.photo;
        r2 = r1.id;
        r0.id = r2;
        r1 = r1.access_hash;
        r0.access_hash = r1;
        goto L_0x05cc;
    L_0x02b6:
        r2 = r7.media;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r3 == 0) goto L_0x057e;
    L_0x02bc:
        r3 = r2.document;
        if (r3 == 0) goto L_0x057e;
    L_0x02c0:
        r3 = r9.media;
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r5 == 0) goto L_0x057e;
    L_0x02c6:
        r3 = r3.document;
        if (r3 == 0) goto L_0x057e;
    L_0x02ca:
        r2 = r2.ttl_seconds;
        if (r2 != 0) goto L_0x0355;
    L_0x02ce:
        r2 = org.telegram.messenger.MessageObject.isVideoMessage(r18);
        if (r2 != 0) goto L_0x02da;
    L_0x02d4:
        r3 = org.telegram.messenger.MessageObject.isGifMessage(r18);
        if (r3 == 0) goto L_0x031e;
    L_0x02da:
        r3 = r7.media;
        r3 = r3.document;
        r3 = org.telegram.messenger.MessageObject.isGifDocument(r3);
        r5 = r9.media;
        r5 = r5.document;
        r5 = org.telegram.messenger.MessageObject.isGifDocument(r5);
        if (r3 != r5) goto L_0x031e;
    L_0x02ec:
        r3 = r0.scheduled;
        if (r3 != 0) goto L_0x0317;
    L_0x02f0:
        r3 = r16.getMessagesStorage();
        r5 = r7.media;
        r5 = r5.document;
        r13 = 2;
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r6);
        r6 = r7.to_id;
        r6 = r6.channel_id;
        r14.append(r6);
        r14.append(r4);
        r6 = r7.id;
        r14.append(r6);
        r6 = r14.toString();
        r3.putSentFile(r8, r5, r13, r6);
    L_0x0317:
        if (r2 == 0) goto L_0x0355;
    L_0x0319:
        r2 = r9.attachPath;
        r7.attachPath = r2;
        goto L_0x0355;
    L_0x031e:
        r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r18);
        if (r2 != 0) goto L_0x0355;
    L_0x0324:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r18);
        if (r2 != 0) goto L_0x0355;
    L_0x032a:
        r2 = r0.scheduled;
        if (r2 != 0) goto L_0x0355;
    L_0x032e:
        r2 = r16.getMessagesStorage();
        r3 = r7.media;
        r3 = r3.document;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r6);
        r6 = r7.to_id;
        r6 = r6.channel_id;
        r5.append(r6);
        r5.append(r4);
        r6 = r7.id;
        r5.append(r6);
        r5 = r5.toString();
        r6 = 1;
        r2.putSentFile(r8, r3, r6, r5);
    L_0x0355:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.thumbs;
        r3 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3);
        r5 = r7.media;
        r5 = r5.document;
        r5 = r5.thumbs;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r3);
        if (r2 == 0) goto L_0x040b;
    L_0x036d:
        r5 = r2.location;
        if (r5 == 0) goto L_0x040b;
    L_0x0371:
        r5 = r5.volume_id;
        r13 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1));
        if (r13 != 0) goto L_0x040b;
    L_0x0377:
        if (r3 == 0) goto L_0x040b;
    L_0x0379:
        r5 = r3.location;
        if (r5 == 0) goto L_0x040b;
    L_0x037d:
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r5 != 0) goto L_0x040b;
    L_0x0381:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r5 != 0) goto L_0x040b;
    L_0x0385:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = r2.location;
        r10 = r6.volume_id;
        r5.append(r10);
        r5.append(r4);
        r6 = r2.location;
        r6 = r6.local_id;
        r5.append(r6);
        r5 = r5.toString();
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r10 = r3.location;
        r10 = r10.volume_id;
        r6.append(r10);
        r6.append(r4);
        r4 = r3.location;
        r4 = r4.local_id;
        r6.append(r4);
        r4 = r6.toString();
        r6 = r5.equals(r4);
        if (r6 != 0) goto L_0x0434;
    L_0x03bf:
        r6 = new java.io.File;
        r10 = 4;
        r11 = org.telegram.messenger.FileLoader.getDirectory(r10);
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r5);
        r13.append(r12);
        r13 = r13.toString();
        r6.<init>(r11, r13);
        r11 = new java.io.File;
        r13 = org.telegram.messenger.FileLoader.getDirectory(r10);
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r4);
        r10.append(r12);
        r10 = r10.toString();
        r11.<init>(r13, r10);
        r6.renameTo(r11);
        r6 = org.telegram.messenger.ImageLoader.getInstance();
        r10 = r7.media;
        r10 = r10.document;
        r10 = org.telegram.messenger.ImageLocation.getForDocument(r3, r10);
        r6.replaceImageInCache(r5, r4, r10, r1);
        r1 = r3.location;
        r2.location = r1;
        r1 = r3.size;
        r2.size = r1;
        goto L_0x0434;
    L_0x040b:
        if (r2 == 0) goto L_0x041a;
    L_0x040d:
        r1 = org.telegram.messenger.MessageObject.isStickerMessage(r18);
        if (r1 == 0) goto L_0x041a;
    L_0x0413:
        r1 = r2.location;
        if (r1 == 0) goto L_0x041a;
    L_0x0417:
        r3.location = r1;
        goto L_0x0434;
    L_0x041a:
        if (r2 == 0) goto L_0x0428;
    L_0x041c:
        if (r2 == 0) goto L_0x0424;
    L_0x041e:
        r1 = r2.location;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r1 != 0) goto L_0x0428;
    L_0x0424:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r1 == 0) goto L_0x0434;
    L_0x0428:
        r1 = r9.media;
        r1 = r1.document;
        r2 = r7.media;
        r2 = r2.document;
        r2 = r2.thumbs;
        r1.thumbs = r2;
    L_0x0434:
        r1 = r9.media;
        r1 = r1.document;
        r2 = r7.media;
        r2 = r2.document;
        r3 = r2.dc_id;
        r1.dc_id = r3;
        r3 = r2.id;
        r1.id = r3;
        r2 = r2.access_hash;
        r1.access_hash = r2;
        r1 = 0;
    L_0x0449:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.size();
        if (r1 >= r2) goto L_0x046b;
    L_0x0455:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.get(r1);
        r2 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r2;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
        if (r3 == 0) goto L_0x0468;
    L_0x0465:
        r3 = r2.waveform;
        goto L_0x046c;
    L_0x0468:
        r1 = r1 + 1;
        goto L_0x0449;
    L_0x046b:
        r3 = 0;
    L_0x046c:
        r1 = r9.media;
        r1 = r1.document;
        r2 = r7.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r1.attributes = r2;
        if (r3 == 0) goto L_0x04a2;
    L_0x047a:
        r1 = 0;
    L_0x047b:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.size();
        if (r1 >= r2) goto L_0x04a2;
    L_0x0487:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.get(r1);
        r2 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r2;
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
        if (r4 == 0) goto L_0x049f;
    L_0x0497:
        r2.waveform = r3;
        r4 = r2.flags;
        r5 = 4;
        r4 = r4 | r5;
        r2.flags = r4;
    L_0x049f:
        r1 = r1 + 1;
        goto L_0x047b;
    L_0x04a2:
        r1 = r9.media;
        r1 = r1.document;
        r2 = r7.media;
        r2 = r2.document;
        r3 = r2.size;
        r1.size = r3;
        r2 = r2.mime_type;
        r1.mime_type = r2;
        r1 = r7.flags;
        r2 = 4;
        r1 = r1 & r2;
        if (r1 != 0) goto L_0x04fc;
    L_0x04b8:
        r1 = org.telegram.messenger.MessageObject.isOut(r18);
        if (r1 == 0) goto L_0x04fc;
    L_0x04be:
        r1 = r7.media;
        r1 = r1.document;
        r1 = org.telegram.messenger.MessageObject.isNewGifDocument(r1);
        if (r1 == 0) goto L_0x04d6;
    L_0x04c8:
        r1 = r16.getMediaDataController();
        r2 = r7.media;
        r2 = r2.document;
        r3 = r7.date;
        r1.addRecentGif(r2, r3);
        goto L_0x04fc;
    L_0x04d6:
        r1 = r7.media;
        r1 = r1.document;
        r1 = org.telegram.messenger.MessageObject.isStickerDocument(r1);
        if (r1 != 0) goto L_0x04eb;
    L_0x04e0:
        r1 = r7.media;
        r1 = r1.document;
        r2 = 1;
        r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r2);
        if (r1 == 0) goto L_0x04fc;
    L_0x04eb:
        r1 = r16.getMediaDataController();
        r2 = 0;
        r3 = r7.media;
        r4 = r3.document;
        r5 = r7.date;
        r6 = 0;
        r3 = r18;
        r1.addRecentSticker(r2, r3, r4, r5, r6);
    L_0x04fc:
        r1 = r9.attachPath;
        if (r1 == 0) goto L_0x0575;
    L_0x0500:
        r2 = 4;
        r2 = org.telegram.messenger.FileLoader.getDirectory(r2);
        r2 = r2.getAbsolutePath();
        r1 = r1.startsWith(r2);
        if (r1 == 0) goto L_0x0575;
    L_0x050f:
        r1 = new java.io.File;
        r2 = r9.attachPath;
        r1.<init>(r2);
        r2 = r7.media;
        r3 = r2.document;
        r2 = r2.ttl_seconds;
        if (r2 == 0) goto L_0x0520;
    L_0x051e:
        r2 = 1;
        goto L_0x0521;
    L_0x0520:
        r2 = 0;
    L_0x0521:
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r2);
        r3 = r1.renameTo(r2);
        if (r3 != 0) goto L_0x0545;
    L_0x052b:
        r1 = r1.exists();
        if (r1 == 0) goto L_0x0536;
    L_0x0531:
        r1 = r9.attachPath;
        r7.attachPath = r1;
        goto L_0x0539;
    L_0x0536:
        r1 = 0;
        r0.attachPathExists = r1;
    L_0x0539:
        r1 = r2.exists();
        r0.mediaExists = r1;
        r0 = r9.message;
        r7.message = r0;
        goto L_0x05cc;
    L_0x0545:
        r1 = org.telegram.messenger.MessageObject.isVideoMessage(r18);
        if (r1 == 0) goto L_0x0550;
    L_0x054b:
        r1 = 1;
        r0.attachPathExists = r1;
        goto L_0x05cc;
    L_0x0550:
        r1 = r0.attachPathExists;
        r0.mediaExists = r1;
        r1 = 0;
        r0.attachPathExists = r1;
        r0 = "";
        r9.attachPath = r0;
        if (r8 == 0) goto L_0x05cc;
    L_0x055d:
        r0 = "http";
        r0 = r8.startsWith(r0);
        if (r0 == 0) goto L_0x05cc;
    L_0x0565:
        r0 = r16.getMessagesStorage();
        r1 = r2.toString();
        r2 = r9.media;
        r2 = r2.document;
        r0.addRecentLocalFile(r8, r1, r2);
        goto L_0x05cc;
    L_0x0575:
        r0 = r9.attachPath;
        r7.attachPath = r0;
        r0 = r9.message;
        r7.message = r0;
        goto L_0x05cc;
    L_0x057e:
        r0 = r7.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r1 == 0) goto L_0x058d;
    L_0x0584:
        r1 = r9.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r1 == 0) goto L_0x058d;
    L_0x058a:
        r9.media = r0;
        goto L_0x05cc;
    L_0x058d:
        r0 = r7.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r1 == 0) goto L_0x0596;
    L_0x0593:
        r9.media = r0;
        goto L_0x05cc;
    L_0x0596:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r1 == 0) goto L_0x05a9;
    L_0x059a:
        r0 = r0.geo;
        r1 = r9.media;
        r1 = r1.geo;
        r2 = r1.lat;
        r0.lat = r2;
        r1 = r1._long;
        r0._long = r1;
        goto L_0x05cc;
    L_0x05a9:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 == 0) goto L_0x05c6;
    L_0x05ad:
        r9.media = r0;
        r0 = r9.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x05cc;
    L_0x05b5:
        r0 = r7.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x05cc;
    L_0x05bd:
        r0 = r7.entities;
        r9.entities = r0;
        r0 = r7.message;
        r9.message = r0;
        goto L_0x05cc;
    L_0x05c6:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r1 == 0) goto L_0x05cc;
    L_0x05ca:
        r9.media = r0;
    L_0x05cc:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.updateMediaPaths(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Message, int, java.lang.String, boolean):void");
    }

    private void putToDelayedMessages(String str, DelayedMessage delayedMessage) {
        ArrayList arrayList = (ArrayList) this.delayedMessages.get(str);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.delayedMessages.put(str, arrayList);
        }
        arrayList.add(delayedMessage);
    }

    /* Access modifiers changed, original: protected */
    public ArrayList<DelayedMessage> getDelayedMessages(String str) {
        return (ArrayList) this.delayedMessages.get(str);
    }

    /* Access modifiers changed, original: protected */
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

    /* Access modifiers changed, original: protected */
    public void processUnsentMessages(ArrayList<Message> arrayList, ArrayList<Message> arrayList2, ArrayList<User> arrayList3, ArrayList<Chat> arrayList4, ArrayList<EncryptedChat> arrayList5) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$s6WlcCky7h9LdyM11HpQpl3LZGs(this, arrayList3, arrayList4, arrayList5, arrayList, arrayList2));
    }

    public /* synthetic */ void lambda$processUnsentMessages$45$SendMessagesHelper(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        int i;
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (i = 0; i < arrayList4.size(); i++) {
            retrySendMessage(new MessageObject(this.currentAccount, (Message) arrayList4.get(i), false), true);
        }
        if (arrayList5 != null) {
            for (i = 0; i < arrayList5.size(); i++) {
                MessageObject messageObject = new MessageObject(this.currentAccount, (Message) arrayList5.get(i), false);
                messageObject.scheduled = true;
                retrySendMessage(messageObject, true);
            }
        }
    }

    public TL_photo generatePhotoSizes(String str, Uri uri) {
        return generatePhotoSizes(null, str, uri);
    }

    public TL_photo generatePhotoSizes(TL_photo tL_photo, String str, Uri uri) {
        Bitmap loadBitmap = ImageLoader.loadBitmap(str, uri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
        if (loadBitmap == null) {
            loadBitmap = ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true);
        }
        ArrayList arrayList = new ArrayList();
        PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, 90.0f, 90.0f, 55, true);
        if (scaleAndSaveImage != null) {
            arrayList.add(scaleAndSaveImage);
        }
        scaleAndSaveImage = ImageLoader.scaleAndSaveImage(loadBitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
        if (scaleAndSaveImage != null) {
            arrayList.add(scaleAndSaveImage);
        }
        if (loadBitmap != null) {
            loadBitmap.recycle();
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        Photo tL_photo2;
        getUserConfig().saveConfig(false);
        if (tL_photo2 == null) {
            tL_photo2 = new TL_photo();
        }
        tL_photo2.date = getConnectionsManager().getCurrentTime();
        tL_photo2.sizes = arrayList;
        tL_photo2.file_reference = new byte[0];
        return tL_photo2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:254:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01c0  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0200 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01c0  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0200 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0152 A:{SYNTHETIC, Splitter:B:102:0x0152} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01c0  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0200 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0152 A:{SYNTHETIC, Splitter:B:102:0x0152} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01c0  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0200 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0152 A:{SYNTHETIC, Splitter:B:102:0x0152} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01c0  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0200 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0152 A:{SYNTHETIC, Splitter:B:102:0x0152} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01c0  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0200 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0152 A:{SYNTHETIC, Splitter:B:102:0x0152} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01c0  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0200 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0436  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x043e  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0447  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0163 A:{SYNTHETIC, Splitter:B:110:0x0163} */
    /* JADX WARNING: Missing block: B:26:0x004d, code skipped:
            if (r3 == null) goto L_0x0051;
     */
    /* JADX WARNING: Missing block: B:195:0x0327, code skipped:
            r4 = -1;
     */
    /* JADX WARNING: Missing block: B:196:0x0328, code skipped:
            if (r4 == 0) goto L_0x0360;
     */
    /* JADX WARNING: Missing block: B:198:0x032b, code skipped:
            if (r4 == 1) goto L_0x035b;
     */
    /* JADX WARNING: Missing block: B:200:0x032e, code skipped:
            if (r4 == 2) goto L_0x0356;
     */
    /* JADX WARNING: Missing block: B:202:0x0331, code skipped:
            if (r4 == 3) goto L_0x0351;
     */
    /* JADX WARNING: Missing block: B:204:0x0334, code skipped:
            if (r4 == 4) goto L_0x034c;
     */
    /* JADX WARNING: Missing block: B:206:0x0337, code skipped:
            if (r4 == 5) goto L_0x0347;
     */
    /* JADX WARNING: Missing block: B:207:0x0339, code skipped:
            r0 = r19.getMimeTypeFromExtension(r9);
     */
    /* JADX WARNING: Missing block: B:208:0x033f, code skipped:
            if (r0 == null) goto L_0x0344;
     */
    /* JADX WARNING: Missing block: B:209:0x0341, code skipped:
            r7.mime_type = r0;
     */
    /* JADX WARNING: Missing block: B:210:0x0344, code skipped:
            r7.mime_type = r1;
     */
    /* JADX WARNING: Missing block: B:211:0x0347, code skipped:
            r7.mime_type = "audio/flac";
     */
    /* JADX WARNING: Missing block: B:212:0x034c, code skipped:
            r7.mime_type = "audio/ogg";
     */
    /* JADX WARNING: Missing block: B:213:0x0351, code skipped:
            r7.mime_type = "audio/m4a";
     */
    /* JADX WARNING: Missing block: B:214:0x0356, code skipped:
            r7.mime_type = "audio/mpeg";
     */
    /* JADX WARNING: Missing block: B:215:0x035b, code skipped:
            r7.mime_type = "audio/opus";
     */
    /* JADX WARNING: Missing block: B:216:0x0360, code skipped:
            r7.mime_type = "image/webp";
     */
    private static boolean prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r27, java.lang.String r28, java.lang.String r29, android.net.Uri r30, java.lang.String r31, long r32, org.telegram.messenger.MessageObject r34, java.lang.CharSequence r35, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r36, org.telegram.messenger.MessageObject r37, boolean r38, boolean r39, int r40) {
        /*
        r0 = r28;
        r1 = r29;
        r2 = r30;
        r3 = r31;
        r4 = 0;
        if (r0 == 0) goto L_0x0011;
    L_0x000b:
        r5 = r28.length();
        if (r5 != 0) goto L_0x0014;
    L_0x0011:
        if (r2 != 0) goto L_0x0014;
    L_0x0013:
        return r4;
    L_0x0014:
        if (r2 == 0) goto L_0x001d;
    L_0x0016:
        r5 = org.telegram.messenger.AndroidUtilities.isInternalUri(r30);
        if (r5 == 0) goto L_0x001d;
    L_0x001c:
        return r4;
    L_0x001d:
        if (r0 == 0) goto L_0x002f;
    L_0x001f:
        r5 = new java.io.File;
        r5.<init>(r0);
        r5 = android.net.Uri.fromFile(r5);
        r5 = org.telegram.messenger.AndroidUtilities.isInternalUri(r5);
        if (r5 == 0) goto L_0x002f;
    L_0x002e:
        return r4;
    L_0x002f:
        r5 = android.webkit.MimeTypeMap.getSingleton();
        r14 = 1;
        if (r2 == 0) goto L_0x0050;
    L_0x0036:
        if (r3 == 0) goto L_0x003d;
    L_0x0038:
        r0 = r5.getExtensionFromMimeType(r3);
        goto L_0x003e;
    L_0x003d:
        r0 = 0;
    L_0x003e:
        if (r0 != 0) goto L_0x0044;
    L_0x0040:
        r0 = "txt";
        r3 = 0;
        goto L_0x0045;
    L_0x0044:
        r3 = 1;
    L_0x0045:
        r2 = org.telegram.messenger.MediaController.copyFileToCache(r2, r0);
        if (r2 != 0) goto L_0x004c;
    L_0x004b:
        return r4;
    L_0x004c:
        r13 = r2;
        if (r3 != 0) goto L_0x0052;
    L_0x004f:
        goto L_0x0051;
    L_0x0050:
        r13 = r0;
    L_0x0051:
        r0 = 0;
    L_0x0052:
        r2 = new java.io.File;
        r2.<init>(r13);
        r3 = r2.exists();
        if (r3 == 0) goto L_0x046a;
    L_0x005d:
        r7 = r2.length();
        r11 = 0;
        r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1));
        if (r3 != 0) goto L_0x0069;
    L_0x0067:
        goto L_0x046a;
    L_0x0069:
        r9 = r32;
        r3 = (int) r9;
        if (r3 != 0) goto L_0x0070;
    L_0x006e:
        r3 = 1;
        goto L_0x0071;
    L_0x0070:
        r3 = 0;
    L_0x0071:
        r15 = r3 ^ 1;
        r8 = r2.getName();
        r7 = -1;
        r6 = "";
        if (r0 == 0) goto L_0x007f;
    L_0x007c:
        r16 = r0;
        goto L_0x008f;
    L_0x007f:
        r0 = 46;
        r0 = r13.lastIndexOf(r0);
        if (r0 == r7) goto L_0x008d;
    L_0x0087:
        r0 = r0 + r14;
        r0 = r13.substring(r0);
        goto L_0x007c;
    L_0x008d:
        r16 = r6;
    L_0x008f:
        r10 = r16.toLowerCase();
        r9 = "mp3";
        r0 = r10.equals(r9);
        r4 = "flac";
        r11 = "opus";
        r12 = "m4a";
        r14 = "ogg";
        r28 = r15;
        r15 = 2;
        if (r0 != 0) goto L_0x016d;
    L_0x00a6:
        r0 = r10.equals(r12);
        if (r0 == 0) goto L_0x00ae;
    L_0x00ac:
        goto L_0x016d;
    L_0x00ae:
        r0 = r10.equals(r11);
        if (r0 != 0) goto L_0x00cd;
    L_0x00b4:
        r0 = r10.equals(r14);
        if (r0 != 0) goto L_0x00cd;
    L_0x00ba:
        r0 = r10.equals(r4);
        if (r0 == 0) goto L_0x00c1;
    L_0x00c0:
        goto L_0x00cd;
    L_0x00c1:
        r18 = r8;
        r19 = r9;
        r0 = 0;
        r8 = 0;
        r9 = 0;
    L_0x00c8:
        r15 = 0;
    L_0x00c9:
        r22 = 0;
        goto L_0x0192;
    L_0x00cd:
        r7 = new android.media.MediaMetadataRetriever;	 Catch:{ Exception -> 0x0143, all -> 0x013f }
        r7.<init>();	 Catch:{ Exception -> 0x0143, all -> 0x013f }
        r0 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x0139 }
        r7.setDataSource(r0);	 Catch:{ Exception -> 0x0139 }
        r0 = 9;
        r0 = r7.extractMetadata(r0);	 Catch:{ Exception -> 0x0139 }
        if (r0 == 0) goto L_0x010d;
    L_0x00e1:
        r18 = r8;
        r19 = r9;
        r8 = java.lang.Long.parseLong(r0);	 Catch:{ Exception -> 0x010b }
        r0 = (float) r8;	 Catch:{ Exception -> 0x010b }
        r8 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r0 = r0 / r8;
        r8 = (double) r0;	 Catch:{ Exception -> 0x010b }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x010b }
        r8 = (int) r8;
        r0 = 7;
        r9 = r7.extractMetadata(r0);	 Catch:{ Exception -> 0x0105 }
        r0 = r7.extractMetadata(r15);	 Catch:{ Exception -> 0x0100 }
        r20 = r8;
        r8 = r0;
        goto L_0x0115;
    L_0x0100:
        r0 = move-exception;
        r20 = r8;
        r8 = 0;
        goto L_0x014d;
    L_0x0105:
        r0 = move-exception;
        r20 = r8;
        r8 = 0;
        r9 = 0;
        goto L_0x014d;
    L_0x010b:
        r0 = move-exception;
        goto L_0x0149;
    L_0x010d:
        r18 = r8;
        r19 = r9;
        r8 = 0;
        r9 = 0;
        r20 = 0;
    L_0x0115:
        if (r37 != 0) goto L_0x012c;
    L_0x0117:
        r0 = r10.equals(r14);	 Catch:{ Exception -> 0x012a }
        if (r0 == 0) goto L_0x012c;
    L_0x011d:
        r0 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x012a }
        r0 = org.telegram.messenger.MediaController.isOpusFile(r0);	 Catch:{ Exception -> 0x012a }
        r15 = 1;
        if (r0 != r15) goto L_0x012c;
    L_0x0128:
        r15 = 1;
        goto L_0x012d;
    L_0x012a:
        r0 = move-exception;
        goto L_0x014d;
    L_0x012c:
        r15 = 0;
    L_0x012d:
        r7.release();	 Catch:{ Exception -> 0x0131 }
        goto L_0x0136;
    L_0x0131:
        r0 = move-exception;
        r7 = r0;
        org.telegram.messenger.FileLog.e(r7);
    L_0x0136:
        r0 = r20;
        goto L_0x00c9;
    L_0x0139:
        r0 = move-exception;
        r18 = r8;
        r19 = r9;
        goto L_0x0149;
    L_0x013f:
        r0 = move-exception;
        r1 = r0;
        r7 = 0;
        goto L_0x0161;
    L_0x0143:
        r0 = move-exception;
        r18 = r8;
        r19 = r9;
        r7 = 0;
    L_0x0149:
        r8 = 0;
        r9 = 0;
        r20 = 0;
    L_0x014d:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x015f }
        if (r7 == 0) goto L_0x015b;
    L_0x0152:
        r7.release();	 Catch:{ Exception -> 0x0156 }
        goto L_0x015b;
    L_0x0156:
        r0 = move-exception;
        r7 = r0;
        org.telegram.messenger.FileLog.e(r7);
    L_0x015b:
        r0 = r20;
        goto L_0x00c8;
    L_0x015f:
        r0 = move-exception;
        r1 = r0;
    L_0x0161:
        if (r7 == 0) goto L_0x016c;
    L_0x0163:
        r7.release();	 Catch:{ Exception -> 0x0167 }
        goto L_0x016c;
    L_0x0167:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x016c:
        throw r1;
    L_0x016d:
        r18 = r8;
        r19 = r9;
        r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r2);
        if (r0 == 0) goto L_0x018a;
    L_0x0177:
        r7 = r0.getDuration();
        r22 = 0;
        r9 = (r7 > r22 ? 1 : (r7 == r22 ? 0 : -1));
        if (r9 == 0) goto L_0x018c;
    L_0x0181:
        r7 = r0.getArtist();
        r0 = r0.getTitle();
        goto L_0x018e;
    L_0x018a:
        r22 = 0;
    L_0x018c:
        r0 = 0;
        r7 = 0;
    L_0x018e:
        r9 = r0;
        r8 = r7;
        r0 = 0;
        r15 = 0;
    L_0x0192:
        if (r0 == 0) goto L_0x01bd;
    L_0x0194:
        r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
        r7.<init>();
        r7.duration = r0;
        r7.title = r9;
        r7.performer = r8;
        r0 = r7.title;
        if (r0 != 0) goto L_0x01a5;
    L_0x01a3:
        r7.title = r6;
    L_0x01a5:
        r0 = r7.flags;
        r8 = 1;
        r0 = r0 | r8;
        r7.flags = r0;
        r0 = r7.performer;
        if (r0 != 0) goto L_0x01b1;
    L_0x01af:
        r7.performer = r6;
    L_0x01b1:
        r0 = r7.flags;
        r9 = 2;
        r0 = r0 | r9;
        r7.flags = r0;
        if (r15 == 0) goto L_0x01bb;
    L_0x01b9:
        r7.voice = r8;
    L_0x01bb:
        r0 = r7;
        goto L_0x01be;
    L_0x01bd:
        r0 = 0;
    L_0x01be:
        if (r1 == 0) goto L_0x01fc;
    L_0x01c0:
        r7 = "attheme";
        r7 = r1.endsWith(r7);
        if (r7 == 0) goto L_0x01cb;
    L_0x01c8:
        r15 = r1;
        r1 = 1;
        goto L_0x01fe;
    L_0x01cb:
        if (r0 == 0) goto L_0x01e6;
    L_0x01cd:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r1);
        r1 = "audio";
        r7.append(r1);
        r8 = r2.length();
        r7.append(r8);
        r1 = r7.toString();
        goto L_0x01fc;
    L_0x01e6:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r1);
        r7.append(r6);
        r8 = r2.length();
        r7.append(r8);
        r1 = r7.toString();
    L_0x01fc:
        r15 = r1;
        r1 = 0;
    L_0x01fe:
        if (r1 != 0) goto L_0x0289;
    L_0x0200:
        if (r3 != 0) goto L_0x0289;
    L_0x0202:
        r1 = r27.getMessagesStorage();
        if (r3 != 0) goto L_0x020a;
    L_0x0208:
        r7 = 1;
        goto L_0x020b;
    L_0x020a:
        r7 = 4;
    L_0x020b:
        r1 = r1.getSentFile(r15, r7);
        if (r1 == 0) goto L_0x021d;
    L_0x0211:
        r7 = 0;
        r8 = r1[r7];
        r7 = r8;
        r7 = (org.telegram.tgnet.TLRPC.TL_document) r7;
        r8 = 1;
        r1 = r1[r8];
        r1 = (java.lang.String) r1;
        goto L_0x021f;
    L_0x021d:
        r1 = 0;
        r7 = 0;
    L_0x021f:
        if (r7 != 0) goto L_0x025d;
    L_0x0221:
        r8 = r13.equals(r15);
        if (r8 != 0) goto L_0x025d;
    L_0x0227:
        if (r3 != 0) goto L_0x025d;
    L_0x0229:
        r8 = r27.getMessagesStorage();
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r13);
        r30 = r10;
        r31 = r11;
        r10 = r2.length();
        r9.append(r10);
        r9 = r9.toString();
        if (r3 != 0) goto L_0x0248;
    L_0x0246:
        r10 = 1;
        goto L_0x0249;
    L_0x0248:
        r10 = 4;
    L_0x0249:
        r8 = r8.getSentFile(r9, r10);
        if (r8 == 0) goto L_0x0261;
    L_0x024f:
        r9 = 0;
        r1 = r8[r9];
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;
        r7 = 1;
        r8 = r8[r7];
        r7 = r8;
        r7 = (java.lang.String) r7;
        r20 = r7;
        goto L_0x0264;
    L_0x025d:
        r30 = r10;
        r31 = r11;
    L_0x0261:
        r20 = r1;
        r1 = r7;
    L_0x0264:
        r10 = 0;
        r24 = 0;
        r17 = -1;
        r7 = r3;
        r11 = r18;
        r8 = r1;
        r26 = r19;
        r9 = r13;
        r29 = r30;
        r30 = r1;
        r19 = r5;
        r1 = r11;
        r18 = r15;
        r15 = r31;
        r31 = r6;
        r5 = r22;
        r22 = r12;
        r11 = r24;
        ensureMediaThumbExists(r7, r8, r9, r10, r11);
        r7 = r30;
        goto L_0x029f;
    L_0x0289:
        r31 = r6;
        r29 = r10;
        r1 = r18;
        r26 = r19;
        r17 = -1;
        r19 = r5;
        r18 = r15;
        r5 = r22;
        r15 = r11;
        r22 = r12;
        r7 = 0;
        r20 = 0;
    L_0x029f:
        if (r7 != 0) goto L_0x042b;
    L_0x02a1:
        r7 = new org.telegram.tgnet.TLRPC$TL_document;
        r7.<init>();
        r7.id = r5;
        r8 = r27.getConnectionsManager();
        r8 = r8.getCurrentTime();
        r7.date = r8;
        r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r8.<init>();
        r8.file_name = r1;
        r1 = 0;
        r9 = new byte[r1];
        r7.file_reference = r9;
        r9 = r7.attributes;
        r9.add(r8);
        r9 = r2.length();
        r10 = (int) r9;
        r7.size = r10;
        r7.dc_id = r1;
        if (r0 == 0) goto L_0x02d3;
    L_0x02ce:
        r1 = r7.attributes;
        r1.add(r0);
    L_0x02d3:
        r0 = r16.length();
        r1 = "application/octet-stream";
        if (r0 == 0) goto L_0x0365;
    L_0x02db:
        r0 = r29.hashCode();
        switch(r0) {
            case 106458: goto L_0x031b;
            case 108272: goto L_0x030f;
            case 109967: goto L_0x0305;
            case 3145576: goto L_0x02fb;
            case 3418175: goto L_0x02f1;
            case 3645340: goto L_0x02e5;
            default: goto L_0x02e2;
        };
    L_0x02e2:
        r9 = r29;
        goto L_0x0327;
    L_0x02e5:
        r0 = "webp";
        r9 = r29;
        r0 = r9.equals(r0);
        if (r0 == 0) goto L_0x0327;
    L_0x02ef:
        r4 = 0;
        goto L_0x0328;
    L_0x02f1:
        r9 = r29;
        r0 = r9.equals(r15);
        if (r0 == 0) goto L_0x0327;
    L_0x02f9:
        r4 = 1;
        goto L_0x0328;
    L_0x02fb:
        r9 = r29;
        r0 = r9.equals(r4);
        if (r0 == 0) goto L_0x0327;
    L_0x0303:
        r4 = 5;
        goto L_0x0328;
    L_0x0305:
        r9 = r29;
        r0 = r9.equals(r14);
        if (r0 == 0) goto L_0x0327;
    L_0x030d:
        r4 = 4;
        goto L_0x0328;
    L_0x030f:
        r9 = r29;
        r4 = r26;
        r0 = r9.equals(r4);
        if (r0 == 0) goto L_0x0327;
    L_0x0319:
        r4 = 2;
        goto L_0x0328;
    L_0x031b:
        r9 = r29;
        r4 = r22;
        r0 = r9.equals(r4);
        if (r0 == 0) goto L_0x0327;
    L_0x0325:
        r4 = 3;
        goto L_0x0328;
    L_0x0327:
        r4 = -1;
    L_0x0328:
        if (r4 == 0) goto L_0x0360;
    L_0x032a:
        r10 = 1;
        if (r4 == r10) goto L_0x035b;
    L_0x032d:
        r10 = 2;
        if (r4 == r10) goto L_0x0356;
    L_0x0330:
        r0 = 3;
        if (r4 == r0) goto L_0x0351;
    L_0x0333:
        r0 = 4;
        if (r4 == r0) goto L_0x034c;
    L_0x0336:
        r0 = 5;
        if (r4 == r0) goto L_0x0347;
    L_0x0339:
        r4 = r19;
        r0 = r4.getMimeTypeFromExtension(r9);
        if (r0 == 0) goto L_0x0344;
    L_0x0341:
        r7.mime_type = r0;
        goto L_0x0367;
    L_0x0344:
        r7.mime_type = r1;
        goto L_0x0367;
    L_0x0347:
        r0 = "audio/flac";
        r7.mime_type = r0;
        goto L_0x0367;
    L_0x034c:
        r0 = "audio/ogg";
        r7.mime_type = r0;
        goto L_0x0367;
    L_0x0351:
        r0 = "audio/m4a";
        r7.mime_type = r0;
        goto L_0x0367;
    L_0x0356:
        r0 = "audio/mpeg";
        r7.mime_type = r0;
        goto L_0x0367;
    L_0x035b:
        r0 = "audio/opus";
        r7.mime_type = r0;
        goto L_0x0367;
    L_0x0360:
        r0 = "image/webp";
        r7.mime_type = r0;
        goto L_0x0367;
    L_0x0365:
        r7.mime_type = r1;
    L_0x0367:
        r0 = r7.mime_type;
        r1 = "image/gif";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x03b2;
    L_0x0371:
        if (r37 == 0) goto L_0x037b;
    L_0x0373:
        r0 = r37.getGroupIdForUse();
        r4 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
        if (r4 != 0) goto L_0x03b2;
    L_0x037b:
        r0 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x03ae }
        r1 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r2 = 0;
        r4 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r2, r1, r1, r4);	 Catch:{ Exception -> 0x03ae }
        if (r0 == 0) goto L_0x03b2;
    L_0x0389:
        r2 = "animation.gif";
        r8.file_name = r2;	 Catch:{ Exception -> 0x03ae }
        r2 = r7.attributes;	 Catch:{ Exception -> 0x03ae }
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;	 Catch:{ Exception -> 0x03ae }
        r4.<init>();	 Catch:{ Exception -> 0x03ae }
        r2.add(r4);	 Catch:{ Exception -> 0x03ae }
        r2 = 55;
        r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r1, r1, r2, r3);	 Catch:{ Exception -> 0x03ae }
        if (r1 == 0) goto L_0x03aa;
    L_0x039f:
        r2 = r7.thumbs;	 Catch:{ Exception -> 0x03ae }
        r2.add(r1);	 Catch:{ Exception -> 0x03ae }
        r1 = r7.flags;	 Catch:{ Exception -> 0x03ae }
        r2 = 1;
        r1 = r1 | r2;
        r7.flags = r1;	 Catch:{ Exception -> 0x03ae }
    L_0x03aa:
        r0.recycle();	 Catch:{ Exception -> 0x03ae }
        goto L_0x03b2;
    L_0x03ae:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03b2:
        r0 = r7.mime_type;
        r1 = "image/webp";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x042b;
    L_0x03bc:
        if (r28 == 0) goto L_0x042b;
    L_0x03be:
        if (r37 != 0) goto L_0x042b;
    L_0x03c0:
        r1 = new android.graphics.BitmapFactory$Options;
        r1.<init>();
        r2 = 1;
        r1.inJustDecodeBounds = r2;	 Catch:{ Exception -> 0x03ef }
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x03ef }
        r2 = "r";
        r0.<init>(r13, r2);	 Catch:{ Exception -> 0x03ef }
        r21 = r0.getChannel();	 Catch:{ Exception -> 0x03ef }
        r22 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Exception -> 0x03ef }
        r23 = 0;
        r2 = r13.length();	 Catch:{ Exception -> 0x03ef }
        r2 = (long) r2;	 Catch:{ Exception -> 0x03ef }
        r25 = r2;
        r2 = r21.map(r22, r23, r25);	 Catch:{ Exception -> 0x03ef }
        r3 = r2.limit();	 Catch:{ Exception -> 0x03ef }
        r4 = 0;
        r5 = 1;
        org.telegram.messenger.Utilities.loadWebpImage(r4, r2, r3, r1, r5);	 Catch:{ Exception -> 0x03ef }
        r0.close();	 Catch:{ Exception -> 0x03ef }
        goto L_0x03f3;
    L_0x03ef:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03f3:
        r0 = r1.outWidth;
        if (r0 == 0) goto L_0x042b;
    L_0x03f7:
        r2 = r1.outHeight;
        if (r2 == 0) goto L_0x042b;
    L_0x03fb:
        r3 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r0 > r3) goto L_0x042b;
    L_0x03ff:
        r0 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r2 > r0) goto L_0x042b;
    L_0x0403:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
        r0.<init>();
        r2 = r31;
        r0.alt = r2;
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
        r3.<init>();
        r0.stickerset = r3;
        r3 = r7.attributes;
        r3.add(r0);
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
        r0.<init>();
        r3 = r1.outWidth;
        r0.w = r3;
        r1 = r1.outHeight;
        r0.h = r1;
        r1 = r7.attributes;
        r1.add(r0);
        goto L_0x042d;
    L_0x042b:
        r2 = r31;
    L_0x042d:
        r3 = r7;
        if (r35 == 0) goto L_0x0436;
    L_0x0430:
        r0 = r35.toString();
        r10 = r0;
        goto L_0x0437;
    L_0x0436:
        r10 = r2;
    L_0x0437:
        r5 = new java.util.HashMap;
        r5.<init>();
        if (r18 == 0) goto L_0x0445;
    L_0x043e:
        r0 = "originalPath";
        r1 = r18;
        r5.put(r0, r1);
    L_0x0445:
        if (r38 == 0) goto L_0x044e;
    L_0x0447:
        r0 = "forceDocument";
        r1 = "1";
        r5.put(r0, r1);
    L_0x044e:
        r14 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$eOOBvCF2SPks3QtWeyAVQPVxSpg;
        r0 = r14;
        r1 = r37;
        r2 = r27;
        r4 = r13;
        r6 = r20;
        r7 = r32;
        r9 = r34;
        r11 = r36;
        r12 = r39;
        r13 = r40;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r14);
        r1 = 1;
        return r1;
    L_0x046a:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, boolean, boolean, int):boolean");
    }

    static /* synthetic */ void lambda$prepareSendingDocumentInternal$46(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, String str3, ArrayList arrayList, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, null, tL_document, str, hashMap, false, str2);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tL_document, null, str, j, messageObject2, str3, arrayList, null, hashMap, z, i, 0, str2);
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

    public static void prepareSendingAudioDocuments(AccountInstance accountInstance, ArrayList<MessageObject> arrayList, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i) {
        new Thread(new -$$Lambda$SendMessagesHelper$2QGxqjnJ4c-gp4gK7LnNcHc5nKA(arrayList, j, accountInstance, messageObject2, messageObject, z, i)).start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0094 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x008f  */
    static /* synthetic */ void lambda$prepareSendingAudioDocuments$48(java.util.ArrayList r22, long r23, org.telegram.messenger.AccountInstance r25, org.telegram.messenger.MessageObject r26, org.telegram.messenger.MessageObject r27, boolean r28, int r29) {
        /*
        r12 = r23;
        r14 = r22.size();
        r15 = 0;
        r11 = 0;
    L_0x0008:
        if (r11 >= r14) goto L_0x00b0;
    L_0x000a:
        r10 = r22;
        r0 = r10.get(r11);
        r4 = r0;
        r4 = (org.telegram.messenger.MessageObject) r4;
        r0 = r4.messageOwner;
        r0 = r0.attachPath;
        r1 = new java.io.File;
        r1.<init>(r0);
        r2 = (int) r12;
        r3 = 1;
        if (r2 != 0) goto L_0x0022;
    L_0x0020:
        r2 = 1;
        goto L_0x0023;
    L_0x0022:
        r2 = 0;
    L_0x0023:
        if (r0 == 0) goto L_0x003d;
    L_0x0025:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r0);
        r0 = "audio";
        r5.append(r0);
        r0 = r1.length();
        r5.append(r0);
        r0 = r5.toString();
    L_0x003d:
        r1 = 0;
        if (r2 != 0) goto L_0x0066;
    L_0x0040:
        r5 = r25.getMessagesStorage();
        if (r2 != 0) goto L_0x0048;
    L_0x0046:
        r6 = 1;
        goto L_0x0049;
    L_0x0048:
        r6 = 4;
    L_0x0049:
        r5 = r5.getSentFile(r0, r6);
        if (r5 == 0) goto L_0x0066;
    L_0x004f:
        r1 = r5[r15];
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;
        r3 = r5[r3];
        r3 = (java.lang.String) r3;
        r19 = 0;
        r20 = 0;
        r16 = r2;
        r17 = r1;
        r18 = r0;
        ensureMediaThumbExists(r16, r17, r18, r19, r20);
        r6 = r3;
        goto L_0x0067;
    L_0x0066:
        r6 = r1;
    L_0x0067:
        if (r1 != 0) goto L_0x0071;
    L_0x0069:
        r1 = r4.messageOwner;
        r1 = r1.media;
        r1 = r1.document;
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;
    L_0x0071:
        r3 = r1;
        if (r2 == 0) goto L_0x0088;
    L_0x0074:
        r1 = 32;
        r1 = r12 >> r1;
        r2 = (int) r1;
        r1 = r25.getMessagesController();
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getEncryptedChat(r2);
        if (r1 != 0) goto L_0x0088;
    L_0x0087:
        return;
    L_0x0088:
        r5 = new java.util.HashMap;
        r5.<init>();
        if (r0 == 0) goto L_0x0094;
    L_0x008f:
        r1 = "originalPath";
        r5.put(r1, r0);
    L_0x0094:
        r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$mUUPfaIBlUabuc9Z5GYWApaWVCY;
        r0 = r16;
        r1 = r26;
        r2 = r25;
        r7 = r23;
        r9 = r27;
        r10 = r28;
        r17 = r11;
        r11 = r29;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);
        r11 = r17 + 1;
        goto L_0x0008;
    L_0x00b0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$48(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$47(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, boolean z, int i) {
        MessageObject messageObject4 = messageObject2;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, null, tL_document, messageObject4.messageOwner.attachPath, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, null, messageObject4.messageOwner.attachPath, j, messageObject3, null, null, null, hashMap, z, i, 0, str);
    }

    public static void prepareSendingDocuments(AccountInstance accountInstance, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<Uri> arrayList3, String str, String str2, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject2, boolean z, int i) {
        if (!(arrayList == null && arrayList2 == null && arrayList3 == null) && (arrayList == null || arrayList2 == null || arrayList.size() == arrayList2.size())) {
            new Thread(new -$$Lambda$SendMessagesHelper$vyQWxLq1i5IURAMERuzJNwY8Qaw(arrayList, accountInstance, arrayList2, str2, j, messageObject, str, messageObject2, z, i, arrayList3, inputContentInfoCompat)).start();
        }
    }

    static /* synthetic */ void lambda$prepareSendingDocuments$50(ArrayList arrayList, AccountInstance accountInstance, ArrayList arrayList2, String str, long j, MessageObject messageObject, String str2, MessageObject messageObject2, boolean z, int i, ArrayList arrayList3, InputContentInfoCompat inputContentInfoCompat) {
        Object obj;
        ArrayList arrayList4 = arrayList;
        ArrayList arrayList5 = arrayList3;
        if (arrayList4 != null) {
            obj = null;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList4.get(i2), (String) arrayList2.get(i2), null, str, j, messageObject, str2, null, messageObject2, false, z, i)) {
                    obj = 1;
                }
            }
        } else {
            obj = null;
        }
        if (arrayList5 != null) {
            for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                if (!prepareSendingDocumentInternal(accountInstance, null, null, (Uri) arrayList5.get(i3), str, j, messageObject, str2, null, messageObject2, false, z, i)) {
                    obj = 1;
                }
            }
        }
        if (inputContentInfoCompat != null) {
            inputContentInfoCompat.releasePermission();
        }
        if (obj != null) {
            AndroidUtilities.runOnUIThread(-$$Lambda$SendMessagesHelper$1c1cWdfyPKATFd2rzsAuIdPSdPc.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$null$49() {
        try {
            Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", NUM), 0).show();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, Uri uri, long j, MessageObject messageObject, CharSequence charSequence, ArrayList<MessageEntity> arrayList, ArrayList<InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject2, boolean z, int i2) {
        ArrayList<InputDocument> arrayList3 = arrayList2;
        SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
        sendingMediaInfo.path = str;
        sendingMediaInfo.uri = uri;
        if (charSequence != null) {
            sendingMediaInfo.caption = charSequence.toString();
        }
        sendingMediaInfo.entities = arrayList;
        sendingMediaInfo.ttl = i;
        if (!(arrayList3 == null || arrayList2.isEmpty())) {
            sendingMediaInfo.masks = new ArrayList(arrayList3);
        }
        ArrayList arrayList4 = new ArrayList();
        arrayList4.add(sendingMediaInfo);
        prepareSendingMedia(accountInstance, arrayList4, j, messageObject, inputContentInfoCompat, false, false, messageObject2, z, i2);
    }

    public static void prepareSendingBotContextResult(AccountInstance accountInstance, BotInlineResult botInlineResult, HashMap<String, String> hashMap, long j, MessageObject messageObject, boolean z, int i) {
        BotInlineResult botInlineResult2 = botInlineResult;
        if (botInlineResult2 != null) {
            BotInlineMessage botInlineMessage = botInlineResult2.send_message;
            String str;
            if (botInlineMessage instanceof TL_botInlineMessageMediaAuto) {
                new Thread(new -$$Lambda$SendMessagesHelper$w3PCtVxs7yHEHdXnJkfDZkdLT4E(j, botInlineResult, accountInstance, hashMap, messageObject, z, i)).run();
            } else if (botInlineMessage instanceof TL_botInlineMessageText) {
                WebPage webPage = null;
                if (((int) j) == 0) {
                    for (int i2 = 0; i2 < botInlineResult2.send_message.entities.size(); i2++) {
                        MessageEntity messageEntity = (MessageEntity) botInlineResult2.send_message.entities.get(i2);
                        if (messageEntity instanceof TL_messageEntityUrl) {
                            webPage = new TL_webPagePending();
                            str = botInlineResult2.send_message.message;
                            int i3 = messageEntity.offset;
                            webPage.url = str.substring(i3, messageEntity.length + i3);
                            break;
                        }
                    }
                }
                WebPage webPage2 = webPage;
                SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
                botInlineMessage = botInlineResult2.send_message;
                sendMessagesHelper.sendMessage(botInlineMessage.message, j, messageObject, webPage2, botInlineMessage.no_webpage ^ 1, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap, z, i);
            } else {
                long j2 = j;
                String str2 = "";
                BotInlineMessage botInlineMessage2;
                if (botInlineMessage instanceof TL_botInlineMessageMediaVenue) {
                    MessageMedia tL_messageMediaVenue = new TL_messageMediaVenue();
                    botInlineMessage2 = botInlineResult2.send_message;
                    tL_messageMediaVenue.geo = botInlineMessage2.geo;
                    tL_messageMediaVenue.address = botInlineMessage2.address;
                    tL_messageMediaVenue.title = botInlineMessage2.title;
                    tL_messageMediaVenue.provider = botInlineMessage2.provider;
                    tL_messageMediaVenue.venue_id = botInlineMessage2.venue_id;
                    str = botInlineMessage2.venue_type;
                    tL_messageMediaVenue.venue_id = str;
                    tL_messageMediaVenue.venue_type = str;
                    if (tL_messageMediaVenue.venue_type == null) {
                        tL_messageMediaVenue.venue_type = str2;
                    }
                    accountInstance.getSendMessagesHelper().sendMessage(tL_messageMediaVenue, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap, z, i);
                } else if (botInlineMessage instanceof TL_botInlineMessageMediaGeo) {
                    MessageMedia tL_messageMediaGeoLive;
                    if (botInlineMessage.period != 0) {
                        tL_messageMediaGeoLive = new TL_messageMediaGeoLive();
                        botInlineMessage = botInlineResult2.send_message;
                        tL_messageMediaGeoLive.period = botInlineMessage.period;
                        tL_messageMediaGeoLive.geo = botInlineMessage.geo;
                        accountInstance.getSendMessagesHelper().sendMessage(tL_messageMediaGeoLive, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap, z, i);
                    } else {
                        tL_messageMediaGeoLive = new TL_messageMediaGeo();
                        tL_messageMediaGeoLive.geo = botInlineResult2.send_message.geo;
                        accountInstance.getSendMessagesHelper().sendMessage(tL_messageMediaGeoLive, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap, z, i);
                    }
                } else if (botInlineMessage instanceof TL_botInlineMessageMediaContact) {
                    User tL_user = new TL_user();
                    botInlineMessage2 = botInlineResult2.send_message;
                    tL_user.phone = botInlineMessage2.phone_number;
                    tL_user.first_name = botInlineMessage2.first_name;
                    tL_user.last_name = botInlineMessage2.last_name;
                    TL_restrictionReason tL_restrictionReason = new TL_restrictionReason();
                    tL_restrictionReason.text = botInlineResult2.send_message.vcard;
                    tL_restrictionReason.platform = str2;
                    tL_restrictionReason.reason = str2;
                    tL_user.restriction_reason.add(tL_restrictionReason);
                    accountInstance.getSendMessagesHelper().sendMessage(tL_user, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap, z, i);
                }
            }
        }
    }

    static /* synthetic */ void lambda$prepareSendingBotContextResult$52(long r17, org.telegram.tgnet.TLRPC.BotInlineResult r19, org.telegram.messenger.AccountInstance r20, java.util.HashMap r21, org.telegram.messenger.MessageObject r22, boolean r23, int r24) {
        /*
        r8 = r19;
        r9 = r21;
        r5 = r17;
        r0 = (int) r5;
        r2 = 1;
        if (r0 != 0) goto L_0x000c;
    L_0x000a:
        r3 = 1;
        goto L_0x000d;
    L_0x000c:
        r3 = 0;
    L_0x000d:
        r4 = r8.type;
        r7 = "game";
        r4 = r7.equals(r4);
        r7 = 0;
        if (r4 == 0) goto L_0x004e;
    L_0x0018:
        if (r0 != 0) goto L_0x001b;
    L_0x001a:
        return;
    L_0x001b:
        r0 = new org.telegram.tgnet.TLRPC$TL_game;
        r0.<init>();
        r1 = r8.title;
        r0.title = r1;
        r1 = r8.description;
        r0.description = r1;
        r1 = r8.id;
        r0.short_name = r1;
        r1 = r8.photo;
        r0.photo = r1;
        r1 = r0.photo;
        if (r1 != 0) goto L_0x003b;
    L_0x0034:
        r1 = new org.telegram.tgnet.TLRPC$TL_photoEmpty;
        r1.<init>();
        r0.photo = r1;
    L_0x003b:
        r1 = r8.document;
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r3 == 0) goto L_0x0048;
    L_0x0041:
        r0.document = r1;
        r1 = r0.flags;
        r1 = r1 | r2;
        r0.flags = r1;
    L_0x0048:
        r13 = r0;
        r2 = r7;
        r4 = r2;
        r12 = r4;
        goto L_0x03ff;
    L_0x004e:
        r0 = r8 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMediaResult;
        if (r0 == 0) goto L_0x0070;
    L_0x0052:
        r0 = r8.document;
        if (r0 == 0) goto L_0x0060;
    L_0x0056:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r1 == 0) goto L_0x03fb;
    L_0x005a:
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;
        r2 = r0;
        r4 = r7;
        goto L_0x03fd;
    L_0x0060:
        r0 = r8.photo;
        if (r0 == 0) goto L_0x03fb;
    L_0x0064:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r1 == 0) goto L_0x03fb;
    L_0x0068:
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        r12 = r0;
        r2 = r7;
        r4 = r2;
        r13 = r4;
        goto L_0x03ff;
    L_0x0070:
        r0 = r8.content;
        if (r0 == 0) goto L_0x03fb;
    L_0x0074:
        r0 = new java.io.File;
        r4 = 4;
        r10 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r12 = r8.content;
        r12 = r12.url;
        r12 = org.telegram.messenger.Utilities.MD5(r12);
        r11.append(r12);
        r12 = ".";
        r11.append(r12);
        r13 = r8.content;
        r13 = r13.url;
        r14 = "file";
        r13 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r13, r14);
        r11.append(r13);
        r11 = r11.toString();
        r0.<init>(r10, r11);
        r10 = r0.exists();
        if (r10 == 0) goto L_0x00af;
    L_0x00aa:
        r10 = r0.getAbsolutePath();
        goto L_0x00b3;
    L_0x00af:
        r10 = r8.content;
        r10 = r10.url;
    L_0x00b3:
        r11 = r8.type;
        r13 = r11.hashCode();
        r15 = "audio";
        r4 = "gif";
        r2 = "sticker";
        switch(r13) {
            case -1890252483: goto L_0x00f9;
            case 102340: goto L_0x00f1;
            case 3143036: goto L_0x00e9;
            case 93166550: goto L_0x00e1;
            case 106642994: goto L_0x00d7;
            case 112202875: goto L_0x00cd;
            case 112386354: goto L_0x00c3;
            default: goto L_0x00c2;
        };
    L_0x00c2:
        goto L_0x0101;
    L_0x00c3:
        r13 = "voice";
        r11 = r11.equals(r13);
        if (r11 == 0) goto L_0x0101;
    L_0x00cb:
        r11 = 1;
        goto L_0x0102;
    L_0x00cd:
        r13 = "video";
        r11 = r11.equals(r13);
        if (r11 == 0) goto L_0x0101;
    L_0x00d5:
        r11 = 3;
        goto L_0x0102;
    L_0x00d7:
        r13 = "photo";
        r11 = r11.equals(r13);
        if (r11 == 0) goto L_0x0101;
    L_0x00df:
        r11 = 6;
        goto L_0x0102;
    L_0x00e1:
        r11 = r11.equals(r15);
        if (r11 == 0) goto L_0x0101;
    L_0x00e7:
        r11 = 0;
        goto L_0x0102;
    L_0x00e9:
        r11 = r11.equals(r14);
        if (r11 == 0) goto L_0x0101;
    L_0x00ef:
        r11 = 2;
        goto L_0x0102;
    L_0x00f1:
        r11 = r11.equals(r4);
        if (r11 == 0) goto L_0x0101;
    L_0x00f7:
        r11 = 5;
        goto L_0x0102;
    L_0x00f9:
        r11 = r11.equals(r2);
        if (r11 == 0) goto L_0x0101;
    L_0x00ff:
        r11 = 4;
        goto L_0x0102;
    L_0x0101:
        r11 = -1;
    L_0x0102:
        switch(r11) {
            case 0: goto L_0x0159;
            case 1: goto L_0x0159;
            case 2: goto L_0x0159;
            case 3: goto L_0x0159;
            case 4: goto L_0x0159;
            case 5: goto L_0x0159;
            case 6: goto L_0x010b;
            default: goto L_0x0105;
        };
    L_0x0105:
        r2 = r7;
        r12 = r2;
        r13 = r12;
    L_0x0108:
        r4 = r10;
        goto L_0x03ff;
    L_0x010b:
        r0 = r0.exists();
        if (r0 == 0) goto L_0x011a;
    L_0x0111:
        r0 = r20.getSendMessagesHelper();
        r0 = r0.generatePhotoSizes(r10, r7);
        goto L_0x011b;
    L_0x011a:
        r0 = r7;
    L_0x011b:
        if (r0 != 0) goto L_0x0155;
    L_0x011d:
        r0 = new org.telegram.tgnet.TLRPC$TL_photo;
        r0.<init>();
        r1 = r20.getConnectionsManager();
        r1 = r1.getCurrentTime();
        r0.date = r1;
        r1 = 0;
        r2 = new byte[r1];
        r0.file_reference = r2;
        r2 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r2.<init>();
        r3 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r19);
        r1 = r3[r1];
        r2.w = r1;
        r1 = 1;
        r3 = r3[r1];
        r2.h = r3;
        r2.size = r1;
        r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r1.<init>();
        r2.location = r1;
        r1 = "x";
        r2.type = r1;
        r1 = r0.sizes;
        r1.add(r2);
    L_0x0155:
        r12 = r0;
        r2 = r7;
        r13 = r2;
        goto L_0x0108;
    L_0x0159:
        r11 = new org.telegram.tgnet.TLRPC$TL_document;
        r11.<init>();
        r0 = r2;
        r1 = 0;
        r11.id = r1;
        r1 = 0;
        r11.size = r1;
        r11.dc_id = r1;
        r2 = r8.content;
        r2 = r2.mime_type;
        r11.mime_type = r2;
        r2 = new byte[r1];
        r11.file_reference = r2;
        r1 = r20.getConnectionsManager();
        r1 = r1.getCurrentTime();
        r11.date = r1;
        r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r1.<init>();
        r2 = r11.attributes;
        r2.add(r1);
        r2 = r8.type;
        r16 = r2.hashCode();
        switch(r16) {
            case -1890252483: goto L_0x01bc;
            case 102340: goto L_0x01b4;
            case 3143036: goto L_0x01ac;
            case 93166550: goto L_0x01a4;
            case 112202875: goto L_0x019a;
            case 112386354: goto L_0x0190;
            default: goto L_0x018f;
        };
    L_0x018f:
        goto L_0x01c4;
    L_0x0190:
        r0 = "voice";
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x01c4;
    L_0x0198:
        r0 = 1;
        goto L_0x01c5;
    L_0x019a:
        r0 = "video";
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x01c4;
    L_0x01a2:
        r0 = 4;
        goto L_0x01c5;
    L_0x01a4:
        r0 = r2.equals(r15);
        if (r0 == 0) goto L_0x01c4;
    L_0x01aa:
        r0 = 2;
        goto L_0x01c5;
    L_0x01ac:
        r0 = r2.equals(r14);
        if (r0 == 0) goto L_0x01c4;
    L_0x01b2:
        r0 = 3;
        goto L_0x01c5;
    L_0x01b4:
        r0 = r2.equals(r4);
        if (r0 == 0) goto L_0x01c4;
    L_0x01ba:
        r0 = 0;
        goto L_0x01c5;
    L_0x01bc:
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x01c4;
    L_0x01c2:
        r0 = 5;
        goto L_0x01c5;
    L_0x01c4:
        r0 = -1;
    L_0x01c5:
        r2 = 55;
        if (r0 == 0) goto L_0x0359;
    L_0x01c9:
        r4 = 1;
        if (r0 == r4) goto L_0x0341;
    L_0x01cc:
        r3 = 2;
        if (r0 == r3) goto L_0x0315;
    L_0x01cf:
        r3 = 3;
        if (r0 == r3) goto L_0x02e5;
    L_0x01d2:
        r3 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r4 = 4;
        if (r0 == r4) goto L_0x0267;
    L_0x01d7:
        r4 = 5;
        if (r0 == r4) goto L_0x01dc;
    L_0x01da:
        goto L_0x03b6;
    L_0x01dc:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
        r0.<init>();
        r4 = "";
        r0.alt = r4;
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
        r4.<init>();
        r0.stickerset = r4;
        r4 = r11.attributes;
        r4.add(r0);
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
        r0.<init>();
        r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r19);
        r13 = 0;
        r15 = r4[r13];
        r0.w = r15;
        r13 = 1;
        r4 = r4[r13];
        r0.h = r4;
        r4 = r11.attributes;
        r4.add(r0);
        r0 = "sticker.webp";
        r1.file_name = r0;
        r0 = r8.thumb;	 Catch:{ all -> 0x0261 }
        if (r0 == 0) goto L_0x03b6;
    L_0x0211:
        r0 = new java.io.File;	 Catch:{ all -> 0x0261 }
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ all -> 0x0261 }
        r13 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0261 }
        r13.<init>();	 Catch:{ all -> 0x0261 }
        r15 = r8.thumb;	 Catch:{ all -> 0x0261 }
        r15 = r15.url;	 Catch:{ all -> 0x0261 }
        r15 = org.telegram.messenger.Utilities.MD5(r15);	 Catch:{ all -> 0x0261 }
        r13.append(r15);	 Catch:{ all -> 0x0261 }
        r13.append(r12);	 Catch:{ all -> 0x0261 }
        r12 = r8.thumb;	 Catch:{ all -> 0x0261 }
        r12 = r12.url;	 Catch:{ all -> 0x0261 }
        r15 = "webp";
        r12 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r12, r15);	 Catch:{ all -> 0x0261 }
        r13.append(r12);	 Catch:{ all -> 0x0261 }
        r12 = r13.toString();	 Catch:{ all -> 0x0261 }
        r0.<init>(r4, r12);	 Catch:{ all -> 0x0261 }
        r0 = r0.getAbsolutePath();	 Catch:{ all -> 0x0261 }
        r4 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r7, r3, r3, r4);	 Catch:{ all -> 0x0261 }
        if (r0 == 0) goto L_0x03b6;
    L_0x024a:
        r4 = 0;
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r4);	 Catch:{ all -> 0x0261 }
        if (r2 == 0) goto L_0x025c;
    L_0x0251:
        r3 = r11.thumbs;	 Catch:{ all -> 0x0261 }
        r3.add(r2);	 Catch:{ all -> 0x0261 }
        r2 = r11.flags;	 Catch:{ all -> 0x0261 }
        r3 = 1;
        r2 = r2 | r3;
        r11.flags = r2;	 Catch:{ all -> 0x0261 }
    L_0x025c:
        r0.recycle();	 Catch:{ all -> 0x0261 }
        goto L_0x03b6;
    L_0x0261:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x03b6;
    L_0x0267:
        r0 = "video.mp4";
        r1.file_name = r0;
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r0.<init>();
        r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r19);
        r13 = 0;
        r15 = r4[r13];
        r0.w = r15;
        r13 = 1;
        r4 = r4[r13];
        r0.h = r4;
        r4 = org.telegram.messenger.MessageObject.getInlineResultDuration(r19);
        r0.duration = r4;
        r0.supports_streaming = r13;
        r4 = r11.attributes;
        r4.add(r0);
        r0 = r8.thumb;	 Catch:{ all -> 0x02df }
        if (r0 == 0) goto L_0x03b6;
    L_0x028f:
        r0 = new java.io.File;	 Catch:{ all -> 0x02df }
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ all -> 0x02df }
        r13 = new java.lang.StringBuilder;	 Catch:{ all -> 0x02df }
        r13.<init>();	 Catch:{ all -> 0x02df }
        r15 = r8.thumb;	 Catch:{ all -> 0x02df }
        r15 = r15.url;	 Catch:{ all -> 0x02df }
        r15 = org.telegram.messenger.Utilities.MD5(r15);	 Catch:{ all -> 0x02df }
        r13.append(r15);	 Catch:{ all -> 0x02df }
        r13.append(r12);	 Catch:{ all -> 0x02df }
        r12 = r8.thumb;	 Catch:{ all -> 0x02df }
        r12 = r12.url;	 Catch:{ all -> 0x02df }
        r15 = "jpg";
        r12 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r12, r15);	 Catch:{ all -> 0x02df }
        r13.append(r12);	 Catch:{ all -> 0x02df }
        r12 = r13.toString();	 Catch:{ all -> 0x02df }
        r0.<init>(r4, r12);	 Catch:{ all -> 0x02df }
        r0 = r0.getAbsolutePath();	 Catch:{ all -> 0x02df }
        r4 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r7, r3, r3, r4);	 Catch:{ all -> 0x02df }
        if (r0 == 0) goto L_0x03b6;
    L_0x02c8:
        r4 = 0;
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r4);	 Catch:{ all -> 0x02df }
        if (r2 == 0) goto L_0x02da;
    L_0x02cf:
        r3 = r11.thumbs;	 Catch:{ all -> 0x02df }
        r3.add(r2);	 Catch:{ all -> 0x02df }
        r2 = r11.flags;	 Catch:{ all -> 0x02df }
        r3 = 1;
        r2 = r2 | r3;
        r11.flags = r2;	 Catch:{ all -> 0x02df }
    L_0x02da:
        r0.recycle();	 Catch:{ all -> 0x02df }
        goto L_0x03b6;
    L_0x02df:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x03b6;
    L_0x02e5:
        r0 = r8.content;
        r0 = r0.mime_type;
        r2 = 47;
        r0 = r0.lastIndexOf(r2);
        r2 = -1;
        if (r0 == r2) goto L_0x0311;
    L_0x02f2:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "file.";
        r2.append(r3);
        r3 = r8.content;
        r3 = r3.mime_type;
        r4 = 1;
        r0 = r0 + r4;
        r0 = r3.substring(r0);
        r2.append(r0);
        r0 = r2.toString();
        r1.file_name = r0;
        goto L_0x03b6;
    L_0x0311:
        r1.file_name = r14;
        goto L_0x03b6;
    L_0x0315:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
        r0.<init>();
        r2 = org.telegram.messenger.MessageObject.getInlineResultDuration(r19);
        r0.duration = r2;
        r2 = r8.title;
        r0.title = r2;
        r2 = r0.flags;
        r3 = 1;
        r2 = r2 | r3;
        r0.flags = r2;
        r2 = r8.description;
        if (r2 == 0) goto L_0x0336;
    L_0x032e:
        r0.performer = r2;
        r2 = r0.flags;
        r3 = 2;
        r2 = r2 | r3;
        r0.flags = r2;
    L_0x0336:
        r2 = "audio.mp3";
        r1.file_name = r2;
        r2 = r11.attributes;
        r2.add(r0);
        goto L_0x03b6;
    L_0x0341:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
        r0.<init>();
        r2 = org.telegram.messenger.MessageObject.getInlineResultDuration(r19);
        r0.duration = r2;
        r2 = 1;
        r0.voice = r2;
        r2 = "audio.ogg";
        r1.file_name = r2;
        r2 = r11.attributes;
        r2.add(r0);
        goto L_0x03b6;
    L_0x0359:
        r0 = "animation.gif";
        r1.file_name = r0;
        r0 = "mp4";
        r0 = r10.endsWith(r0);
        if (r0 == 0) goto L_0x0374;
    L_0x0365:
        r0 = "video/mp4";
        r11.mime_type = r0;
        r0 = r11.attributes;
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r4.<init>();
        r0.add(r4);
        goto L_0x0378;
    L_0x0374:
        r0 = "image/gif";
        r11.mime_type = r0;
    L_0x0378:
        if (r3 == 0) goto L_0x037d;
    L_0x037a:
        r0 = 90;
        goto L_0x037f;
    L_0x037d:
        r0 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
    L_0x037f:
        r3 = "mp4";
        r3 = r10.endsWith(r3);	 Catch:{ all -> 0x03b2 }
        if (r3 == 0) goto L_0x038d;
    L_0x0387:
        r3 = 1;
        r4 = android.media.ThumbnailUtils.createVideoThumbnail(r10, r3);	 Catch:{ all -> 0x03b2 }
        goto L_0x0393;
    L_0x038d:
        r3 = 1;
        r4 = (float) r0;	 Catch:{ all -> 0x03b2 }
        r4 = org.telegram.messenger.ImageLoader.loadBitmap(r10, r7, r4, r4, r3);	 Catch:{ all -> 0x03b2 }
    L_0x0393:
        if (r4 == 0) goto L_0x03b6;
    L_0x0395:
        r3 = (float) r0;	 Catch:{ all -> 0x03b2 }
        r12 = 90;
        if (r0 <= r12) goto L_0x039c;
    L_0x039a:
        r2 = 80;
    L_0x039c:
        r12 = 0;
        r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r4, r3, r3, r2, r12);	 Catch:{ all -> 0x03b2 }
        if (r0 == 0) goto L_0x03ae;
    L_0x03a3:
        r2 = r11.thumbs;	 Catch:{ all -> 0x03b2 }
        r2.add(r0);	 Catch:{ all -> 0x03b2 }
        r0 = r11.flags;	 Catch:{ all -> 0x03b2 }
        r2 = 1;
        r0 = r0 | r2;
        r11.flags = r0;	 Catch:{ all -> 0x03b2 }
    L_0x03ae:
        r4.recycle();	 Catch:{ all -> 0x03b2 }
        goto L_0x03b6;
    L_0x03b2:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03b6:
        r0 = r1.file_name;
        if (r0 != 0) goto L_0x03bc;
    L_0x03ba:
        r1.file_name = r14;
    L_0x03bc:
        r0 = r11.mime_type;
        if (r0 != 0) goto L_0x03c4;
    L_0x03c0:
        r0 = "application/octet-stream";
        r11.mime_type = r0;
    L_0x03c4:
        r0 = r11.thumbs;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x03f6;
    L_0x03cc:
        r0 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r0.<init>();
        r1 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r19);
        r2 = 0;
        r3 = r1[r2];
        r0.w = r3;
        r3 = 1;
        r1 = r1[r3];
        r0.h = r1;
        r0.size = r2;
        r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r1.<init>();
        r0.location = r1;
        r1 = "x";
        r0.type = r1;
        r1 = r11.thumbs;
        r1.add(r0);
        r0 = r11.flags;
        r0 = r0 | r3;
        r11.flags = r0;
    L_0x03f6:
        r12 = r7;
        r13 = r12;
        r4 = r10;
        r2 = r11;
        goto L_0x03ff;
    L_0x03fb:
        r2 = r7;
        r4 = r2;
    L_0x03fd:
        r12 = r4;
        r13 = r12;
    L_0x03ff:
        if (r9 == 0) goto L_0x040c;
    L_0x0401:
        r0 = r8.content;
        if (r0 == 0) goto L_0x040c;
    L_0x0405:
        r0 = r0.url;
        r1 = "originalPath";
        r9.put(r1, r0);
    L_0x040c:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$xaBvUsFESjazUZmz9eG9U2Qrt_U;
        r1 = r0;
        r3 = r20;
        r5 = r17;
        r7 = r22;
        r8 = r19;
        r9 = r21;
        r10 = r23;
        r11 = r24;
        r1.<init>(r2, r3, r4, r5, r7, r8, r9, r10, r11, r12, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$52(long, org.telegram.tgnet.TLRPC$BotInlineResult, org.telegram.messenger.AccountInstance, java.util.HashMap, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$51(TL_document tL_document, AccountInstance accountInstance, String str, long j, MessageObject messageObject, BotInlineResult botInlineResult, HashMap hashMap, boolean z, int i, TL_photo tL_photo, TL_game tL_game) {
        BotInlineResult botInlineResult2 = botInlineResult;
        SendMessagesHelper sendMessagesHelper;
        BotInlineMessage botInlineMessage;
        if (tL_document != null) {
            sendMessagesHelper = accountInstance.getSendMessagesHelper();
            botInlineMessage = botInlineResult2.send_message;
            sendMessagesHelper.sendMessage(tL_document, null, str, j, messageObject, botInlineMessage.message, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap, z, i, 0, botInlineResult);
        } else if (tL_photo != null) {
            sendMessagesHelper = accountInstance.getSendMessagesHelper();
            WebDocument webDocument = botInlineResult2.content;
            String str2 = webDocument != null ? webDocument.url : null;
            botInlineMessage = botInlineResult2.send_message;
            sendMessagesHelper.sendMessage(tL_photo, str2, j, messageObject, botInlineMessage.message, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap, z, i, 0, botInlineResult);
        } else if (tL_game != null) {
            accountInstance.getSendMessagesHelper().sendMessage(tL_game, j, botInlineResult2.send_message.reply_markup, hashMap, z, i);
        }
    }

    private static String getTrimmedString(String str) {
        String trim = str.trim();
        if (trim.length() == 0) {
            return trim;
        }
        while (true) {
            trim = "\n";
            if (!str.startsWith(trim)) {
                break;
            }
            str = str.substring(1);
        }
        while (str.endsWith(trim)) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static void prepareSendingText(AccountInstance accountInstance, String str, long j, boolean z, int i) {
        accountInstance.getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$FxC1TRWQ1N-uJd8aQUz-Xx0MfLo(str, accountInstance, j, z, i));
    }

    static /* synthetic */ void lambda$null$53(String str, AccountInstance accountInstance, long j, boolean z, int i) {
        String trimmedString = getTrimmedString(str);
        if (trimmedString.length() != 0) {
            int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / 4096.0f));
            int i2 = 0;
            while (i2 < ceil) {
                int i3 = i2 * 4096;
                i2++;
                accountInstance.getSendMessagesHelper().sendMessage(trimmedString.substring(i3, Math.min(i2 * 4096, trimmedString.length())), j, null, null, true, null, null, null, z, i);
            }
        }
    }

    public static void ensureMediaThumbExists(boolean z, TLObject tLObject, String str, Uri uri, long j) {
        TLObject tLObject2 = tLObject;
        String str2 = str;
        Uri uri2 = uri;
        Bitmap loadBitmap;
        PhotoSize scaleAndSaveImage;
        if (tLObject2 instanceof TL_photo) {
            boolean z2;
            TL_photo tL_photo = (TL_photo) tLObject2;
            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tL_photo.sizes, 90);
            if (closestPhotoSizeWithSize instanceof TL_photoStrippedSize) {
                z2 = true;
            } else {
                z2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists();
            }
            PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tL_photo.sizes, AndroidUtilities.getPhotoSize());
            boolean exists = FileLoader.getPathToAttach(closestPhotoSizeWithSize2, false).exists();
            if (!(z2 && exists)) {
                Bitmap loadBitmap2 = ImageLoader.loadBitmap(str2, uri2, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
                loadBitmap = loadBitmap2 == null ? ImageLoader.loadBitmap(str2, uri2, 800.0f, 800.0f, true) : loadBitmap2;
                if (!exists) {
                    scaleAndSaveImage = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize2, loadBitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
                    if (scaleAndSaveImage != closestPhotoSizeWithSize2) {
                        tL_photo.sizes.add(0, scaleAndSaveImage);
                    }
                }
                if (!z2) {
                    scaleAndSaveImage = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize, loadBitmap, 90.0f, 90.0f, 55, true);
                    if (scaleAndSaveImage != closestPhotoSizeWithSize) {
                        tL_photo.sizes.add(0, scaleAndSaveImage);
                    }
                }
                if (loadBitmap != null) {
                    loadBitmap.recycle();
                }
            }
        } else if (tLObject2 instanceof TL_document) {
            Document document = (TL_document) tLObject2;
            if ((MessageObject.isVideoDocument(document) || MessageObject.isNewGifDocument(document)) && MessageObject.isDocumentHasThumb(document)) {
                int i = 320;
                scaleAndSaveImage = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                if (!((scaleAndSaveImage instanceof TL_photoStrippedSize) || FileLoader.getPathToAttach(scaleAndSaveImage, true).exists())) {
                    Bitmap createVideoThumbnail = createVideoThumbnail(str2, j);
                    loadBitmap = createVideoThumbnail == null ? ThumbnailUtils.createVideoThumbnail(str2, 1) : createVideoThumbnail;
                    if (z) {
                        i = 90;
                    }
                    float f = (float) i;
                    document.thumbs.set(0, ImageLoader.scaleAndSaveImage(scaleAndSaveImage, loadBitmap, f, f, i > 90 ? 80 : 55, false));
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0050  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0078  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x009a A:{SYNTHETIC, Splitter:B:32:0x009a} */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00e9  */
    private static java.lang.String getKeyForPhotoSize(org.telegram.tgnet.TLRPC.PhotoSize r10, android.graphics.Bitmap[] r11, boolean r12) {
        /*
        r0 = 0;
        if (r10 != 0) goto L_0x0004;
    L_0x0003:
        return r0;
    L_0x0004:
        r1 = org.telegram.messenger.AndroidUtilities.isTablet();
        r2 = NUM; // 0x3var_ float:0.7 double:5.23867711E-315;
        if (r1 == 0) goto L_0x0016;
    L_0x000d:
        r1 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
    L_0x0011:
        r1 = (float) r1;
        r1 = r1 * r2;
        r1 = (int) r1;
        goto L_0x0039;
    L_0x0016:
        r1 = r10.w;
        r3 = r10.h;
        if (r1 < r3) goto L_0x002e;
    L_0x001c:
        r1 = org.telegram.messenger.AndroidUtilities.displaySize;
        r2 = r1.x;
        r1 = r1.y;
        r1 = java.lang.Math.min(r2, r1);
        r2 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 - r2;
        goto L_0x0039;
    L_0x002e:
        r1 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r1.x;
        r1 = r1.y;
        r1 = java.lang.Math.min(r3, r1);
        goto L_0x0011;
    L_0x0039:
        r2 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r1;
        r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        if (r1 <= r3) goto L_0x004a;
    L_0x0046:
        r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x004a:
        r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        if (r2 <= r3) goto L_0x0054;
    L_0x0050:
        r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x0054:
        r3 = r10.w;
        r4 = (float) r3;
        r1 = (float) r1;
        r4 = r4 / r1;
        r3 = (float) r3;
        r3 = r3 / r4;
        r3 = (int) r3;
        r5 = r10.h;
        r5 = (float) r5;
        r5 = r5 / r4;
        r4 = (int) r5;
        r5 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        if (r3 != 0) goto L_0x0069;
    L_0x0065:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r5);
    L_0x0069:
        if (r4 != 0) goto L_0x006f;
    L_0x006b:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
    L_0x006f:
        if (r4 <= r2) goto L_0x0078;
    L_0x0071:
        r1 = (float) r4;
        r4 = (float) r2;
        r1 = r1 / r4;
        r3 = (float) r3;
        r3 = r3 / r1;
        r3 = (int) r3;
        goto L_0x0096;
    L_0x0078:
        r2 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r2);
        if (r4 >= r5) goto L_0x0095;
    L_0x0080:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r4 = r10.h;
        r4 = (float) r4;
        r5 = (float) r2;
        r4 = r4 / r5;
        r5 = r10.w;
        r6 = (float) r5;
        r6 = r6 / r4;
        r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1));
        if (r1 >= 0) goto L_0x0096;
    L_0x0091:
        r1 = (float) r5;
        r1 = r1 / r4;
        r3 = (int) r1;
        goto L_0x0096;
    L_0x0095:
        r2 = r4;
    L_0x0096:
        r1 = 1;
        r4 = 0;
        if (r11 == 0) goto L_0x00e5;
    L_0x009a:
        r5 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x00e4 }
        r5.<init>();	 Catch:{ all -> 0x00e4 }
        r5.inJustDecodeBounds = r1;	 Catch:{ all -> 0x00e4 }
        r6 = org.telegram.messenger.FileLoader.getPathToAttach(r10);	 Catch:{ all -> 0x00e4 }
        r7 = new java.io.FileInputStream;	 Catch:{ all -> 0x00e4 }
        r7.<init>(r6);	 Catch:{ all -> 0x00e4 }
        android.graphics.BitmapFactory.decodeStream(r7, r0, r5);	 Catch:{ all -> 0x00e4 }
        r7.close();	 Catch:{ all -> 0x00e4 }
        r7 = r5.outWidth;	 Catch:{ all -> 0x00e4 }
        r7 = (float) r7;	 Catch:{ all -> 0x00e4 }
        r8 = r5.outHeight;	 Catch:{ all -> 0x00e4 }
        r8 = (float) r8;	 Catch:{ all -> 0x00e4 }
        r9 = (float) r3;	 Catch:{ all -> 0x00e4 }
        r7 = r7 / r9;
        r9 = (float) r2;	 Catch:{ all -> 0x00e4 }
        r8 = r8 / r9;
        r7 = java.lang.Math.max(r7, r8);	 Catch:{ all -> 0x00e4 }
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
        if (r9 >= 0) goto L_0x00c6;
    L_0x00c4:
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x00c6:
        r5.inJustDecodeBounds = r4;	 Catch:{ all -> 0x00e4 }
        r7 = (int) r7;	 Catch:{ all -> 0x00e4 }
        r5.inSampleSize = r7;	 Catch:{ all -> 0x00e4 }
        r7 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ all -> 0x00e4 }
        r5.inPreferredConfig = r7;	 Catch:{ all -> 0x00e4 }
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x00e4 }
        r8 = 21;
        if (r7 < r8) goto L_0x00e5;
    L_0x00d5:
        r7 = new java.io.FileInputStream;	 Catch:{ all -> 0x00e4 }
        r7.<init>(r6);	 Catch:{ all -> 0x00e4 }
        r0 = android.graphics.BitmapFactory.decodeStream(r7, r0, r5);	 Catch:{ all -> 0x00e4 }
        r11[r4] = r0;	 Catch:{ all -> 0x00e4 }
        r7.close();	 Catch:{ all -> 0x00e4 }
        goto L_0x00e5;
    L_0x00e5:
        r11 = java.util.Locale.US;
        if (r12 == 0) goto L_0x00ec;
    L_0x00e9:
        r12 = "%d_%d@%d_%d_b";
        goto L_0x00ee;
    L_0x00ec:
        r12 = "%d_%d@%d_%d";
    L_0x00ee:
        r0 = 4;
        r0 = new java.lang.Object[r0];
        r5 = r10.location;
        r5 = r5.volume_id;
        r5 = java.lang.Long.valueOf(r5);
        r0[r4] = r5;
        r10 = r10.location;
        r10 = r10.local_id;
        r10 = java.lang.Integer.valueOf(r10);
        r0[r1] = r10;
        r10 = 2;
        r1 = (float) r3;
        r3 = org.telegram.messenger.AndroidUtilities.density;
        r1 = r1 / r3;
        r1 = (int) r1;
        r1 = java.lang.Integer.valueOf(r1);
        r0[r10] = r1;
        r10 = 3;
        r1 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.density;
        r1 = r1 / r2;
        r1 = (int) r1;
        r1 = java.lang.Integer.valueOf(r1);
        r0[r10] = r1;
        r10 = java.lang.String.format(r11, r12, r0);
        return r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.getKeyForPhotoSize(org.telegram.tgnet.TLRPC$PhotoSize, android.graphics.Bitmap[], boolean):java.lang.String");
    }

    public static void prepareSendingMedia(AccountInstance accountInstance, ArrayList<SendingMediaInfo> arrayList, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, boolean z, boolean z2, MessageObject messageObject2, boolean z3, int i) {
        if (!arrayList.isEmpty()) {
            mediaSendQueue.postRunnable(new -$$Lambda$SendMessagesHelper$Do7_Hi1P8_rnnKoD190DpHz_frw(arrayList, j, accountInstance, z, z2, messageObject2, messageObject, z3, i, inputContentInfoCompat));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02bc A:{Catch:{ Exception -> 0x02a4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02b2 A:{Catch:{ Exception -> 0x02a4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02c9 A:{Catch:{ Exception -> 0x02a4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x06e4  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0604  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x06f9  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x070b  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0753  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0a80  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0a80  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x08ef  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x08a9  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x08a9  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x08ef  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x08ef  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x08a9  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x08a3  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0899  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0b25  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0b18  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0b2b  */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x0b3d A:{LOOP_END, LOOP:4: B:448:0x0b37->B:450:0x0b3d} */
    /* JADX WARNING: Removed duplicated region for block: B:468:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x0b80  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0a80  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a51  */
    /* JADX WARNING: Missing block: B:40:0x009d, code skipped:
            if (org.telegram.messenger.MediaController.isWebp(r3.uri) != false) goto L_0x0157;
     */
    /* JADX WARNING: Missing block: B:427:0x0a71, code skipped:
            if (r6 == (r21 - 1)) goto L_0x0a76;
     */
    static /* synthetic */ void lambda$prepareSendingMedia$62(java.util.ArrayList r55, long r56, org.telegram.messenger.AccountInstance r58, boolean r59, boolean r60, org.telegram.messenger.MessageObject r61, org.telegram.messenger.MessageObject r62, boolean r63, int r64, androidx.core.view.inputmethod.InputContentInfoCompat r65) {
        /*
        r1 = r55;
        r14 = r56;
        r13 = r58;
        r18 = java.lang.System.currentTimeMillis();
        r12 = r55.size();
        r0 = (int) r14;
        if (r0 != 0) goto L_0x0013;
    L_0x0011:
        r10 = 1;
        goto L_0x0014;
    L_0x0013:
        r10 = 0;
    L_0x0014:
        if (r10 == 0) goto L_0x0031;
    L_0x0016:
        r0 = 32;
        r2 = r14 >> r0;
        r0 = (int) r2;
        r2 = r58.getMessagesController();
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r2.getEncryptedChat(r0);
        if (r0 == 0) goto L_0x0031;
    L_0x0029:
        r0 = r0.layer;
        r0 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r0);
        r8 = r0;
        goto L_0x0032;
    L_0x0031:
        r8 = 0;
    L_0x0032:
        r6 = ".gif";
        r20 = 3;
        r7 = "_";
        if (r10 == 0) goto L_0x003e;
    L_0x003a:
        r0 = 73;
        if (r8 < r0) goto L_0x016b;
    L_0x003e:
        if (r59 != 0) goto L_0x016b;
    L_0x0040:
        if (r60 == 0) goto L_0x016b;
    L_0x0042:
        r0 = new java.util.HashMap;
        r0.<init>();
        r4 = 0;
    L_0x0048:
        if (r4 >= r12) goto L_0x0165;
    L_0x004a:
        r2 = r1.get(r4);
        r3 = r2;
        r3 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r3;
        r2 = r3.searchImage;
        if (r2 != 0) goto L_0x0157;
    L_0x0055:
        r2 = r3.isVideo;
        if (r2 != 0) goto L_0x0157;
    L_0x0059:
        r2 = r3.path;
        if (r2 != 0) goto L_0x006c;
    L_0x005d:
        r5 = r3.uri;
        if (r5 == 0) goto L_0x006c;
    L_0x0061:
        r2 = org.telegram.messenger.AndroidUtilities.getPath(r5);
        r5 = r3.uri;
        r5 = r5.toString();
        goto L_0x006d;
    L_0x006c:
        r5 = r2;
    L_0x006d:
        if (r2 == 0) goto L_0x007f;
    L_0x006f:
        r17 = r2.endsWith(r6);
        if (r17 != 0) goto L_0x0157;
    L_0x0075:
        r9 = ".webp";
        r9 = r2.endsWith(r9);
        if (r9 == 0) goto L_0x007f;
    L_0x007d:
        goto L_0x0157;
    L_0x007f:
        r9 = r3.path;
        r11 = r3.uri;
        r9 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r9, r11);
        if (r9 == 0) goto L_0x008b;
    L_0x0089:
        goto L_0x0157;
    L_0x008b:
        if (r2 != 0) goto L_0x00a1;
    L_0x008d:
        r9 = r3.uri;
        if (r9 == 0) goto L_0x00a1;
    L_0x0091:
        r9 = org.telegram.messenger.MediaController.isGif(r9);
        if (r9 != 0) goto L_0x0157;
    L_0x0097:
        r9 = r3.uri;
        r9 = org.telegram.messenger.MediaController.isWebp(r9);
        if (r9 == 0) goto L_0x00a1;
    L_0x009f:
        goto L_0x0157;
    L_0x00a1:
        if (r2 == 0) goto L_0x00c7;
    L_0x00a3:
        r9 = new java.io.File;
        r9.<init>(r2);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r5);
        r11 = r4;
        r4 = r9.length();
        r2.append(r4);
        r2.append(r7);
        r4 = r9.lastModified();
        r2.append(r4);
        r5 = r2.toString();
        goto L_0x00c9;
    L_0x00c7:
        r11 = r4;
        r5 = 0;
    L_0x00c9:
        if (r10 != 0) goto L_0x012b;
    L_0x00cb:
        r2 = r3.ttl;
        if (r2 != 0) goto L_0x012b;
    L_0x00cf:
        r2 = r58.getMessagesStorage();
        if (r10 != 0) goto L_0x00d7;
    L_0x00d5:
        r4 = 0;
        goto L_0x00d8;
    L_0x00d7:
        r4 = 3;
    L_0x00d8:
        r2 = r2.getSentFile(r5, r4);
        if (r2 == 0) goto L_0x00e9;
    L_0x00de:
        r4 = 0;
        r5 = r2[r4];
        r5 = (org.telegram.tgnet.TLRPC.TL_photo) r5;
        r4 = 1;
        r2 = r2[r4];
        r2 = (java.lang.String) r2;
        goto L_0x00eb;
    L_0x00e9:
        r2 = 0;
        r5 = 0;
    L_0x00eb:
        if (r5 != 0) goto L_0x0112;
    L_0x00ed:
        r4 = r3.uri;
        if (r4 == 0) goto L_0x0112;
    L_0x00f1:
        r4 = r58.getMessagesStorage();
        r9 = r3.uri;
        r9 = org.telegram.messenger.AndroidUtilities.getPath(r9);
        r22 = r2;
        if (r10 != 0) goto L_0x0101;
    L_0x00ff:
        r2 = 0;
        goto L_0x0102;
    L_0x0101:
        r2 = 3;
    L_0x0102:
        r2 = r4.getSentFile(r9, r2);
        if (r2 == 0) goto L_0x0114;
    L_0x0108:
        r4 = 0;
        r5 = r2[r4];
        r5 = (org.telegram.tgnet.TLRPC.TL_photo) r5;
        r4 = 1;
        r2 = r2[r4];
        r2 = (java.lang.String) r2;
    L_0x0112:
        r22 = r2;
    L_0x0114:
        r9 = r5;
        r4 = r3.path;
        r5 = r3.uri;
        r23 = 0;
        r2 = r10;
        r25 = r3;
        r3 = r9;
        r15 = 0;
        r14 = r6;
        r26 = r7;
        r6 = r23;
        ensureMediaThumbExists(r2, r3, r4, r5, r6);
        r2 = r22;
        goto L_0x0133;
    L_0x012b:
        r25 = r3;
        r14 = r6;
        r26 = r7;
        r15 = 0;
        r2 = r15;
        r9 = r2;
    L_0x0133:
        r3 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker;
        r3.<init>(r15);
        r4 = r25;
        r0.put(r4, r3);
        if (r9 == 0) goto L_0x0144;
    L_0x013f:
        r3.parentObject = r2;
        r3.photo = r9;
        goto L_0x015c;
    L_0x0144:
        r2 = new java.util.concurrent.CountDownLatch;
        r5 = 1;
        r2.<init>(r5);
        r3.sync = r2;
        r2 = mediaSendThreadPool;
        r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$W9GumeDASydN1PZR2rR68Zk7Txo;
        r5.<init>(r3, r13, r4, r10);
        r2.execute(r5);
        goto L_0x015c;
    L_0x0157:
        r11 = r4;
        r14 = r6;
        r26 = r7;
        r15 = 0;
    L_0x015c:
        r4 = r11 + 1;
        r6 = r14;
        r7 = r26;
        r14 = r56;
        goto L_0x0048;
    L_0x0165:
        r14 = r6;
        r26 = r7;
        r15 = 0;
        r11 = r0;
        goto L_0x0170;
    L_0x016b:
        r14 = r6;
        r26 = r7;
        r15 = 0;
        r11 = r15;
    L_0x0170:
        r4 = r15;
        r5 = r4;
        r22 = r5;
        r27 = r22;
        r28 = r27;
        r0 = 0;
        r2 = 0;
        r9 = 0;
        r23 = 0;
    L_0x017e:
        if (r9 >= r12) goto L_0x0b0d;
    L_0x0180:
        r16 = r1.get(r9);
        r15 = r16;
        r15 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r15;
        if (r60 == 0) goto L_0x01a3;
    L_0x018a:
        if (r10 == 0) goto L_0x0190;
    L_0x018c:
        r6 = 73;
        if (r8 < r6) goto L_0x01a3;
    L_0x0190:
        r6 = 1;
        if (r12 <= r6) goto L_0x01a3;
    L_0x0193:
        r6 = r0 % 10;
        if (r6 != 0) goto L_0x01a3;
    L_0x0197:
        r0 = org.telegram.messenger.Utilities.random;
        r2 = r0.nextLong();
        r6 = r2;
        r23 = r6;
        r16 = 0;
        goto L_0x01a9;
    L_0x01a3:
        r16 = r0;
        r6 = r23;
        r23 = r2;
    L_0x01a9:
        r0 = r15.searchImage;
        r2 = "mp4";
        r3 = "originalPath";
        r31 = r5;
        r5 = "";
        r34 = 4;
        if (r0 == 0) goto L_0x0503;
    L_0x01b7:
        r1 = r0.type;
        r35 = r4;
        r4 = "jpg";
        r36 = r6;
        r6 = ".";
        r7 = 1;
        if (r1 != r7) goto L_0x0385;
    L_0x01c4:
        r1 = new java.util.HashMap;
        r1.<init>();
        r32 = 0;
        r0 = r15.searchImage;
        r0 = r0.document;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r5 == 0) goto L_0x01db;
    L_0x01d3:
        r5 = r0;
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r5, r7);
        goto L_0x0208;
    L_0x01db:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r5 = r15.searchImage;
        r5 = r5.imageUrl;
        r5 = org.telegram.messenger.Utilities.MD5(r5);
        r0.append(r5);
        r0.append(r6);
        r5 = r15.searchImage;
        r5 = r5.imageUrl;
        r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r4);
        r0.append(r5);
        r0 = r0.toString();
        r5 = new java.io.File;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r34);
        r5.<init>(r7, r0);
        r0 = r5;
        r5 = 0;
    L_0x0208:
        if (r5 != 0) goto L_0x0322;
    L_0x020a:
        r5 = new org.telegram.tgnet.TLRPC$TL_document;
        r5.<init>();
        r38 = r8;
        r7 = 0;
        r5.id = r7;
        r7 = 0;
        r8 = new byte[r7];
        r5.file_reference = r8;
        r7 = r58.getConnectionsManager();
        r7 = r7.getCurrentTime();
        r5.date = r7;
        r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r7.<init>();
        r8 = "animation.gif";
        r7.file_name = r8;
        r8 = r5.attributes;
        r8.add(r7);
        r7 = r15.searchImage;
        r7 = r7.size;
        r5.size = r7;
        r7 = 0;
        r5.dc_id = r7;
        r7 = r0.toString();
        r7 = r7.endsWith(r2);
        if (r7 == 0) goto L_0x0254;
    L_0x0245:
        r7 = "video/mp4";
        r5.mime_type = r7;
        r7 = r5.attributes;
        r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r8.<init>();
        r7.add(r8);
        goto L_0x0258;
    L_0x0254:
        r7 = "image/gif";
        r5.mime_type = r7;
    L_0x0258:
        r7 = r0.exists();
        if (r7 == 0) goto L_0x0260;
    L_0x025e:
        r7 = r0;
        goto L_0x0262;
    L_0x0260:
        r0 = 0;
        r7 = 0;
    L_0x0262:
        if (r0 != 0) goto L_0x0298;
    L_0x0264:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r8 = r15.searchImage;
        r8 = r8.thumbUrl;
        r8 = org.telegram.messenger.Utilities.MD5(r8);
        r0.append(r8);
        r0.append(r6);
        r6 = r15.searchImage;
        r6 = r6.thumbUrl;
        r4 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r4);
        r0.append(r4);
        r0 = r0.toString();
        r4 = new java.io.File;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r34);
        r4.<init>(r6, r0);
        r0 = r4.exists();
        if (r0 != 0) goto L_0x0297;
    L_0x0295:
        r0 = 0;
        goto L_0x0298;
    L_0x0297:
        r0 = r4;
    L_0x0298:
        if (r0 == 0) goto L_0x02eb;
    L_0x029a:
        if (r10 != 0) goto L_0x02a6;
    L_0x029c:
        r4 = r15.ttl;	 Catch:{ Exception -> 0x02a4 }
        if (r4 == 0) goto L_0x02a1;
    L_0x02a0:
        goto L_0x02a6;
    L_0x02a1:
        r4 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x02a8;
    L_0x02a4:
        r0 = move-exception;
        goto L_0x02e8;
    L_0x02a6:
        r4 = 90;
    L_0x02a8:
        r6 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x02a4 }
        r2 = r6.endsWith(r2);	 Catch:{ Exception -> 0x02a4 }
        if (r2 == 0) goto L_0x02bc;
    L_0x02b2:
        r0 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x02a4 }
        r2 = 1;
        r0 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r2);	 Catch:{ Exception -> 0x02a4 }
        goto L_0x02c7;
    L_0x02bc:
        r2 = 1;
        r0 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x02a4 }
        r6 = (float) r4;	 Catch:{ Exception -> 0x02a4 }
        r8 = 0;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r8, r6, r6, r2);	 Catch:{ Exception -> 0x02a4 }
    L_0x02c7:
        if (r0 == 0) goto L_0x02eb;
    L_0x02c9:
        r2 = (float) r4;	 Catch:{ Exception -> 0x02a4 }
        r6 = 90;
        if (r4 <= r6) goto L_0x02d1;
    L_0x02ce:
        r4 = 80;
        goto L_0x02d3;
    L_0x02d1:
        r4 = 55;
    L_0x02d3:
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r4, r10);	 Catch:{ Exception -> 0x02a4 }
        if (r2 == 0) goto L_0x02e4;
    L_0x02d9:
        r4 = r5.thumbs;	 Catch:{ Exception -> 0x02a4 }
        r4.add(r2);	 Catch:{ Exception -> 0x02a4 }
        r2 = r5.flags;	 Catch:{ Exception -> 0x02a4 }
        r4 = 1;
        r2 = r2 | r4;
        r5.flags = r2;	 Catch:{ Exception -> 0x02a4 }
    L_0x02e4:
        r0.recycle();	 Catch:{ Exception -> 0x02a4 }
        goto L_0x02eb;
    L_0x02e8:
        org.telegram.messenger.FileLog.e(r0);
    L_0x02eb:
        r0 = r5.thumbs;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x031e;
    L_0x02f3:
        r0 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r0.<init>();
        r2 = r15.searchImage;
        r4 = r2.width;
        r0.w = r4;
        r2 = r2.height;
        r0.h = r2;
        r8 = 0;
        r0.size = r8;
        r2 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r2.<init>();
        r0.location = r2;
        r2 = "x";
        r0.type = r2;
        r2 = r5.thumbs;
        r2.add(r0);
        r0 = r5.flags;
        r17 = 1;
        r0 = r0 | 1;
        r5.flags = r0;
        goto L_0x0328;
    L_0x031e:
        r8 = 0;
        r17 = 1;
        goto L_0x0328;
    L_0x0322:
        r38 = r8;
        r8 = 0;
        r17 = 1;
        r7 = r0;
    L_0x0328:
        r0 = r15.searchImage;
        r0 = r0.imageUrl;
        if (r7 != 0) goto L_0x032f;
    L_0x032e:
        goto L_0x0333;
    L_0x032f:
        r0 = r7.toString();
    L_0x0333:
        r6 = r0;
        r0 = r15.searchImage;
        r0 = r0.imageUrl;
        if (r0 == 0) goto L_0x033d;
    L_0x033a:
        r1.put(r3, r0);
    L_0x033d:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$zaE01xRc3ThP0VMNJOQVsiyVb9Y;
        r2 = r0;
        r3 = r61;
        r7 = r35;
        r4 = r58;
        r39 = r31;
        r40 = r36;
        r29 = 0;
        r42 = r7;
        r7 = r1;
        r1 = r38;
        r21 = 0;
        r8 = r32;
        r44 = r9;
        r43 = r10;
        r9 = r56;
        r45 = r11;
        r11 = r62;
        r21 = r12;
        r12 = r15;
        r15 = r13;
        r13 = r63;
        r46 = r14;
        r14 = r64;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        r0 = r16;
        r2 = r23;
        r33 = r26;
        r5 = r39;
        r34 = r40;
        r4 = r42;
        r26 = r43;
        r31 = r44;
        r32 = r45;
        r36 = r46;
        goto L_0x04fd;
    L_0x0385:
        r1 = r8;
        r44 = r9;
        r43 = r10;
        r45 = r11;
        r21 = r12;
        r46 = r14;
        r39 = r31;
        r42 = r35;
        r40 = r36;
        r29 = 0;
        r14 = r13;
        r9 = 0;
        r0 = r0.photo;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r2 == 0) goto L_0x03a5;
    L_0x03a0:
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        r13 = r43;
        goto L_0x03ac;
    L_0x03a5:
        r13 = r43;
        if (r13 != 0) goto L_0x03ab;
    L_0x03a9:
        r0 = r15.ttl;
    L_0x03ab:
        r0 = 0;
    L_0x03ac:
        if (r0 != 0) goto L_0x0474;
    L_0x03ae:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r7 = r15.searchImage;
        r7 = r7.imageUrl;
        r7 = org.telegram.messenger.Utilities.MD5(r7);
        r2.append(r7);
        r2.append(r6);
        r7 = r15.searchImage;
        r7 = r7.imageUrl;
        r7 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r7, r4);
        r2.append(r7);
        r2 = r2.toString();
        r7 = new java.io.File;
        r8 = org.telegram.messenger.FileLoader.getDirectory(r34);
        r7.<init>(r8, r2);
        r2 = r7.exists();
        if (r2 == 0) goto L_0x03f8;
    L_0x03df:
        r10 = r7.length();
        r2 = (r10 > r29 ? 1 : (r10 == r29 ? 0 : -1));
        if (r2 == 0) goto L_0x03f8;
    L_0x03e7:
        r0 = r58.getSendMessagesHelper();
        r2 = r7.toString();
        r7 = 0;
        r0 = r0.generatePhotoSizes(r2, r7);
        if (r0 == 0) goto L_0x03f8;
    L_0x03f6:
        r2 = 0;
        goto L_0x03f9;
    L_0x03f8:
        r2 = 1;
    L_0x03f9:
        if (r0 != 0) goto L_0x0471;
    L_0x03fb:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = r15.searchImage;
        r8 = r8.thumbUrl;
        r8 = org.telegram.messenger.Utilities.MD5(r8);
        r7.append(r8);
        r7.append(r6);
        r6 = r15.searchImage;
        r6 = r6.thumbUrl;
        r4 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r4);
        r7.append(r4);
        r4 = r7.toString();
        r6 = new java.io.File;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r34);
        r6.<init>(r7, r4);
        r4 = r6.exists();
        if (r4 == 0) goto L_0x0439;
    L_0x042c:
        r0 = r58.getSendMessagesHelper();
        r4 = r6.toString();
        r6 = 0;
        r0 = r0.generatePhotoSizes(r4, r6);
    L_0x0439:
        if (r0 != 0) goto L_0x0471;
    L_0x043b:
        r0 = new org.telegram.tgnet.TLRPC$TL_photo;
        r0.<init>();
        r4 = r58.getConnectionsManager();
        r4 = r4.getCurrentTime();
        r0.date = r4;
        r12 = 0;
        r4 = new byte[r12];
        r0.file_reference = r4;
        r4 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r4.<init>();
        r6 = r15.searchImage;
        r7 = r6.width;
        r4.w = r7;
        r6 = r6.height;
        r4.h = r6;
        r4.size = r12;
        r6 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r6.<init>();
        r4.location = r6;
        r6 = "x";
        r4.type = r6;
        r6 = r0.sizes;
        r6.add(r4);
        goto L_0x0472;
    L_0x0471:
        r12 = 0;
    L_0x0472:
        r6 = r2;
        goto L_0x0476;
    L_0x0474:
        r12 = 0;
        r6 = 1;
    L_0x0476:
        if (r0 == 0) goto L_0x04e0;
    L_0x0478:
        r8 = new java.util.HashMap;
        r8.<init>();
        r2 = r15.searchImage;
        r2 = r2.imageUrl;
        if (r2 == 0) goto L_0x0486;
    L_0x0483:
        r8.put(r3, r2);
    L_0x0486:
        if (r60 == 0) goto L_0x04bc;
    L_0x0488:
        r2 = r16 + 1;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r5);
        r10 = r40;
        r3.append(r10);
        r3 = r3.toString();
        r4 = "groupId";
        r8.put(r4, r3);
        r7 = 10;
        if (r2 == r7) goto L_0x04ae;
    L_0x04a4:
        r3 = r21 + -1;
        r7 = r44;
        if (r7 != r3) goto L_0x04ab;
    L_0x04aa:
        goto L_0x04b0;
    L_0x04ab:
        r16 = r2;
        goto L_0x04c0;
    L_0x04ae:
        r7 = r44;
    L_0x04b0:
        r3 = "final";
        r4 = "1";
        r8.put(r3, r4);
        r16 = r2;
        r23 = r29;
        goto L_0x04c0;
    L_0x04bc:
        r10 = r40;
        r7 = r44;
    L_0x04c0:
        r17 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$nQEBUMkmjihJTbxn7rMeSSzzVGE;
        r2 = r17;
        r3 = r61;
        r4 = r58;
        r5 = r0;
        r47 = r7;
        r7 = r15;
        r48 = r10;
        r10 = r56;
        r15 = 0;
        r12 = r62;
        r50 = r13;
        r13 = r63;
        r14 = r64;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r17);
        goto L_0x04e7;
    L_0x04e0:
        r50 = r13;
        r48 = r40;
        r47 = r44;
        r15 = 0;
    L_0x04e7:
        r38 = r1;
        r0 = r16;
        r2 = r23;
        r33 = r26;
        r5 = r39;
        r4 = r42;
        r32 = r45;
        r36 = r46;
        r31 = r47;
        r34 = r48;
        r26 = r50;
    L_0x04fd:
        r17 = 0;
        r25 = 0;
        goto L_0x0af5;
    L_0x0503:
        r42 = r4;
        r48 = r6;
        r1 = r8;
        r47 = r9;
        r50 = r10;
        r45 = r11;
        r21 = r12;
        r46 = r14;
        r39 = r31;
        r6 = 90;
        r7 = 10;
        r14 = 0;
        r29 = 0;
        r0 = r15.isVideo;
        if (r0 == 0) goto L_0x080b;
    L_0x051f:
        if (r59 == 0) goto L_0x0523;
    L_0x0521:
        r0 = 0;
        goto L_0x052e;
    L_0x0523:
        r0 = r15.videoEditedInfo;
        if (r0 == 0) goto L_0x0528;
    L_0x0527:
        goto L_0x052e;
    L_0x0528:
        r0 = r15.path;
        r0 = createCompressionSettings(r0);
    L_0x052e:
        if (r59 != 0) goto L_0x07cb;
    L_0x0530:
        if (r0 != 0) goto L_0x053a;
    L_0x0532:
        r4 = r15.path;
        r2 = r4.endsWith(r2);
        if (r2 == 0) goto L_0x07cb;
    L_0x053a:
        r8 = r15.path;
        r9 = new java.io.File;
        r9.<init>(r8);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r8);
        r10 = r9.length();
        r2.append(r10);
        r12 = r26;
        r2.append(r12);
        r10 = r9.lastModified();
        r2.append(r10);
        r2 = r2.toString();
        if (r0 == 0) goto L_0x05b7;
    L_0x0562:
        r11 = r0.muted;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r6 = r0.estimatedDuration;
        r4.append(r6);
        r4.append(r12);
        r6 = r0.startTime;
        r4.append(r6);
        r4.append(r12);
        r6 = r0.endTime;
        r4.append(r6);
        r2 = r0.muted;
        if (r2 == 0) goto L_0x0588;
    L_0x0585:
        r2 = "_m";
        goto L_0x0589;
    L_0x0588:
        r2 = r5;
    L_0x0589:
        r4.append(r2);
        r2 = r4.toString();
        r4 = r0.resultWidth;
        r6 = r0.originalWidth;
        if (r4 == r6) goto L_0x05aa;
    L_0x0596:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r4.append(r12);
        r2 = r0.resultWidth;
        r4.append(r2);
        r2 = r4.toString();
    L_0x05aa:
        r6 = r0.startTime;
        r4 = (r6 > r29 ? 1 : (r6 == r29 ? 0 : -1));
        if (r4 < 0) goto L_0x05b1;
    L_0x05b0:
        goto L_0x05b3;
    L_0x05b1:
        r6 = r29;
    L_0x05b3:
        r10 = r2;
        r13 = r50;
        goto L_0x05bd;
    L_0x05b7:
        r10 = r2;
        r6 = r29;
        r13 = r50;
        r11 = 0;
    L_0x05bd:
        if (r13 != 0) goto L_0x05f7;
    L_0x05bf:
        r2 = r15.ttl;
        if (r2 != 0) goto L_0x05f7;
    L_0x05c3:
        r2 = r58.getMessagesStorage();
        if (r13 != 0) goto L_0x05cb;
    L_0x05c9:
        r4 = 2;
        goto L_0x05cc;
    L_0x05cb:
        r4 = 5;
    L_0x05cc:
        r2 = r2.getSentFile(r10, r4);
        if (r2 == 0) goto L_0x05f7;
    L_0x05d2:
        r4 = r2[r14];
        r17 = r4;
        r17 = (org.telegram.tgnet.TLRPC.TL_document) r17;
        r4 = 1;
        r2 = r2[r4];
        r26 = r2;
        r26 = (java.lang.String) r26;
        r2 = r15.path;
        r31 = 0;
        r35 = r2;
        r2 = r13;
        r51 = r3;
        r3 = r17;
        r14 = 1;
        r4 = r35;
        r52 = r5;
        r5 = r31;
        r31 = r6;
        ensureMediaThumbExists(r2, r3, r4, r5, r6);
        goto L_0x0602;
    L_0x05f7:
        r51 = r3;
        r52 = r5;
        r31 = r6;
        r14 = 1;
        r17 = 0;
        r26 = 0;
    L_0x0602:
        if (r17 != 0) goto L_0x06e4;
    L_0x0604:
        r2 = r15.path;
        r6 = r31;
        r2 = createVideoThumbnail(r2, r6);
        if (r2 != 0) goto L_0x0614;
    L_0x060e:
        r2 = r15.path;
        r2 = android.media.ThumbnailUtils.createVideoThumbnail(r2, r14);
    L_0x0614:
        if (r2 == 0) goto L_0x0640;
    L_0x0616:
        if (r13 != 0) goto L_0x062a;
    L_0x0618:
        r3 = r15.ttl;
        if (r3 == 0) goto L_0x061d;
    L_0x061c:
        goto L_0x062a;
    L_0x061d:
        r3 = r2.getWidth();
        r4 = r2.getHeight();
        r5 = java.lang.Math.max(r3, r4);
        goto L_0x062c;
    L_0x062a:
        r5 = 90;
    L_0x062c:
        r3 = (float) r5;
        r4 = 90;
        if (r5 <= r4) goto L_0x0634;
    L_0x0631:
        r5 = 80;
        goto L_0x0636;
    L_0x0634:
        r5 = 55;
    L_0x0636:
        r5 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r5, r13);
        r3 = 0;
        r6 = getKeyForPhotoSize(r5, r3, r14);
        goto L_0x0644;
    L_0x0640:
        r4 = 90;
        r5 = 0;
        r6 = 0;
    L_0x0644:
        r3 = new org.telegram.tgnet.TLRPC$TL_document;
        r3.<init>();
        r7 = 0;
        r4 = new byte[r7];
        r3.file_reference = r4;
        if (r5 == 0) goto L_0x065a;
    L_0x0650:
        r4 = r3.thumbs;
        r4.add(r5);
        r4 = r3.flags;
        r4 = r4 | r14;
        r3.flags = r4;
    L_0x065a:
        r4 = "video/mp4";
        r3.mime_type = r4;
        r4 = r58.getUserConfig();
        r7 = 0;
        r4.saveConfig(r7);
        if (r13 == 0) goto L_0x0678;
    L_0x0668:
        r4 = 66;
        if (r1 < r4) goto L_0x0672;
    L_0x066c:
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r4.<init>();
        goto L_0x067f;
    L_0x0672:
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r4.<init>();
        goto L_0x067f;
    L_0x0678:
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r4.<init>();
        r4.supports_streaming = r14;
    L_0x067f:
        r5 = r3.attributes;
        r5.add(r4);
        if (r0 == 0) goto L_0x06cc;
    L_0x0686:
        r5 = r0.needConvert();
        if (r5 == 0) goto L_0x06cc;
    L_0x068c:
        r5 = r0.muted;
        if (r5 == 0) goto L_0x069f;
    L_0x0690:
        r5 = r15.path;
        fillVideoAttribute(r5, r4, r0);
        r5 = r4.w;
        r0.originalWidth = r5;
        r5 = r4.h;
        r0.originalHeight = r5;
        r5 = r8;
        goto L_0x06a9;
    L_0x069f:
        r5 = r8;
        r7 = r0.estimatedDuration;
        r31 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 / r31;
        r8 = (int) r7;
        r4.duration = r8;
    L_0x06a9:
        r7 = r0.rotationValue;
        r8 = 90;
        if (r7 == r8) goto L_0x06bd;
    L_0x06af:
        r8 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r7 != r8) goto L_0x06b4;
    L_0x06b3:
        goto L_0x06bd;
    L_0x06b4:
        r7 = r0.resultWidth;
        r4.w = r7;
        r7 = r0.resultHeight;
        r4.h = r7;
        goto L_0x06c5;
    L_0x06bd:
        r7 = r0.resultHeight;
        r4.w = r7;
        r7 = r0.resultWidth;
        r4.h = r7;
    L_0x06c5:
        r7 = r0.estimatedSize;
        r4 = (int) r7;
        r3.size = r4;
        r9 = 0;
        goto L_0x06e0;
    L_0x06cc:
        r5 = r8;
        r7 = r9.exists();
        if (r7 == 0) goto L_0x06da;
    L_0x06d3:
        r7 = r9.length();
        r8 = (int) r7;
        r3.size = r8;
    L_0x06da:
        r7 = r15.path;
        r9 = 0;
        fillVideoAttribute(r7, r4, r9);
    L_0x06e0:
        r8 = r3;
        r4 = r6;
        r3 = r2;
        goto L_0x06ea;
    L_0x06e4:
        r5 = r8;
        r9 = 0;
        r3 = r9;
        r4 = r3;
        r8 = r17;
    L_0x06ea:
        if (r0 == 0) goto L_0x0715;
    L_0x06ec:
        r2 = r0.muted;
        if (r2 == 0) goto L_0x0715;
    L_0x06f0:
        r2 = r8.attributes;
        r2 = r2.size();
        r6 = 0;
    L_0x06f7:
        if (r6 >= r2) goto L_0x0708;
    L_0x06f9:
        r7 = r8.attributes;
        r7 = r7.get(r6);
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
        if (r7 == 0) goto L_0x0705;
    L_0x0703:
        r2 = 1;
        goto L_0x0709;
    L_0x0705:
        r6 = r6 + 1;
        goto L_0x06f7;
    L_0x0708:
        r2 = 0;
    L_0x0709:
        if (r2 != 0) goto L_0x0715;
    L_0x070b:
        r2 = r8.attributes;
        r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r6.<init>();
        r2.add(r6);
    L_0x0715:
        if (r0 == 0) goto L_0x074a;
    L_0x0717:
        r2 = r0.needConvert();
        if (r2 == 0) goto L_0x074a;
    L_0x071d:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r5 = "-2147483648_";
        r2.append(r5);
        r5 = org.telegram.messenger.SharedConfig.getLastLocalId();
        r2.append(r5);
        r5 = ".mp4";
        r2.append(r5);
        r2 = r2.toString();
        r5 = new java.io.File;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r34);
        r5.<init>(r6, r2);
        org.telegram.messenger.SharedConfig.saveConfig();
        r2 = r5.getAbsolutePath();
        r17 = r2;
        goto L_0x074c;
    L_0x074a:
        r17 = r5;
    L_0x074c:
        r7 = new java.util.HashMap;
        r7.<init>();
        if (r10 == 0) goto L_0x0758;
    L_0x0753:
        r6 = r51;
        r7.put(r6, r10);
    L_0x0758:
        if (r11 != 0) goto L_0x0794;
    L_0x075a:
        if (r60 == 0) goto L_0x0794;
    L_0x075c:
        r2 = r16 + 1;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r10 = r52;
        r5.append(r10);
        r10 = r48;
        r5.append(r10);
        r5 = r5.toString();
        r6 = "groupId";
        r7.put(r6, r5);
        r5 = 10;
        if (r2 == r5) goto L_0x0786;
    L_0x077a:
        r5 = r21 + -1;
        r6 = r47;
        if (r6 != r5) goto L_0x0781;
    L_0x0780:
        goto L_0x0788;
    L_0x0781:
        r31 = r23;
        r23 = r2;
        goto L_0x079c;
    L_0x0786:
        r6 = r47;
    L_0x0788:
        r5 = "final";
        r9 = "1";
        r7.put(r5, r9);
        r23 = r2;
        r31 = r29;
        goto L_0x079c;
    L_0x0794:
        r6 = r47;
        r10 = r48;
        r31 = r23;
        r23 = r16;
    L_0x079c:
        r24 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Jvj9apu3nMDCQr39j9Qil_TMmyM;
        r2 = r24;
        r5 = r61;
        r9 = r6;
        r6 = r58;
        r16 = r7;
        r33 = 0;
        r7 = r0;
        r38 = r1;
        r1 = r9;
        r25 = 0;
        r9 = r17;
        r53 = r10;
        r10 = r16;
        r11 = r26;
        r44 = r1;
        r1 = r12;
        r26 = r13;
        r12 = r56;
        r14 = r62;
        r16 = r63;
        r17 = r64;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r24);
        goto L_0x07f5;
    L_0x07cb:
        r38 = r1;
        r1 = r26;
        r44 = r47;
        r53 = r48;
        r26 = r50;
        r25 = 0;
        r4 = r15.path;
        r5 = 0;
        r6 = 0;
        r10 = r15.caption;
        r11 = r15.entities;
        r2 = r58;
        r3 = r4;
        r7 = r56;
        r9 = r62;
        r12 = r61;
        r13 = r59;
        r14 = r63;
        r15 = r64;
        prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15);
        r31 = r23;
        r23 = r16;
    L_0x07f5:
        r33 = r1;
        r0 = r23;
        r2 = r31;
        r5 = r39;
        r4 = r42;
        r31 = r44;
        r32 = r45;
        r36 = r46;
    L_0x0805:
        r34 = r53;
        r17 = 0;
        goto L_0x0af5;
    L_0x080b:
        r38 = r1;
        r6 = r3;
        r10 = r5;
        r1 = r26;
        r44 = r47;
        r53 = r48;
        r26 = r50;
        r5 = 10;
        r25 = 0;
        r0 = r15.path;
        if (r0 != 0) goto L_0x082e;
    L_0x081f:
        r2 = r15.uri;
        if (r2 == 0) goto L_0x082e;
    L_0x0823:
        r0 = org.telegram.messenger.AndroidUtilities.getPath(r2);
        r2 = r15.uri;
        r2 = r2.toString();
        goto L_0x082f;
    L_0x082e:
        r2 = r0;
    L_0x082f:
        if (r59 != 0) goto L_0x0895;
    L_0x0831:
        r3 = r15.path;
        r4 = r15.uri;
        r3 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r3, r4);
        if (r3 == 0) goto L_0x083c;
    L_0x083b:
        goto L_0x0895;
    L_0x083c:
        r14 = r46;
        if (r0 == 0) goto L_0x085c;
    L_0x0840:
        r3 = r0.endsWith(r14);
        if (r3 != 0) goto L_0x084e;
    L_0x0846:
        r3 = ".webp";
        r3 = r0.endsWith(r3);
        if (r3 == 0) goto L_0x085c;
    L_0x084e:
        r3 = r0.endsWith(r14);
        if (r3 == 0) goto L_0x0857;
    L_0x0854:
        r3 = "gif";
        goto L_0x0859;
    L_0x0857:
        r3 = "webp";
    L_0x0859:
        r22 = r3;
        goto L_0x08a5;
    L_0x085c:
        if (r0 != 0) goto L_0x0892;
    L_0x085e:
        r3 = r15.uri;
        if (r3 == 0) goto L_0x0892;
    L_0x0862:
        r3 = org.telegram.messenger.MediaController.isGif(r3);
        if (r3 == 0) goto L_0x0879;
    L_0x0868:
        r0 = r15.uri;
        r2 = r0.toString();
        r0 = r15.uri;
        r3 = "gif";
        r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3);
        r22 = "gif";
        goto L_0x08a5;
    L_0x0879:
        r3 = r15.uri;
        r3 = org.telegram.messenger.MediaController.isWebp(r3);
        if (r3 == 0) goto L_0x0892;
    L_0x0881:
        r0 = r15.uri;
        r2 = r0.toString();
        r0 = r15.uri;
        r3 = "webp";
        r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3);
        r22 = "webp";
        goto L_0x08a5;
    L_0x0892:
        r8 = r0;
        r0 = 0;
        goto L_0x08a7;
    L_0x0895:
        r14 = r46;
        if (r0 == 0) goto L_0x08a3;
    L_0x0899:
        r3 = new java.io.File;
        r3.<init>(r0);
        r3 = org.telegram.messenger.FileLoader.getFileExtension(r3);
        goto L_0x0859;
    L_0x08a3:
        r22 = r10;
    L_0x08a5:
        r8 = r0;
        r0 = 1;
    L_0x08a7:
        if (r0 == 0) goto L_0x08ef;
    L_0x08a9:
        r13 = r39;
        if (r13 != 0) goto L_0x08c6;
    L_0x08ad:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r27 = new java.util.ArrayList;
        r27.<init>();
        r28 = new java.util.ArrayList;
        r28.<init>();
        r0 = r27;
        r3 = r28;
        goto L_0x08cd;
    L_0x08c6:
        r5 = r13;
        r0 = r27;
        r3 = r28;
        r4 = r42;
    L_0x08cd:
        r5.add(r8);
        r4.add(r2);
        r2 = r15.caption;
        r0.add(r2);
        r2 = r15.entities;
        r3.add(r2);
        r27 = r0;
        r33 = r1;
        r28 = r3;
        r36 = r14;
        r0 = r16;
        r2 = r23;
        r31 = r44;
        r32 = r45;
        goto L_0x0805;
    L_0x08ef:
        r13 = r39;
        if (r8 == 0) goto L_0x0917;
    L_0x08f3:
        r0 = new java.io.File;
        r0.<init>(r8);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r11 = r0.length();
        r3.append(r11);
        r3.append(r1);
        r11 = r0.lastModified();
        r3.append(r11);
        r0 = r3.toString();
        r9 = r0;
        goto L_0x0919;
    L_0x0917:
        r9 = r25;
    L_0x0919:
        r12 = r45;
        if (r12 == 0) goto L_0x0941;
    L_0x091d:
        r0 = r12.get(r15);
        r2 = r0;
        r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2;
        r0 = r2.photo;
        r3 = r2.parentObject;
        if (r0 != 0) goto L_0x0938;
    L_0x092a:
        r0 = r2.sync;	 Catch:{ Exception -> 0x0930 }
        r0.await();	 Catch:{ Exception -> 0x0930 }
        goto L_0x0934;
    L_0x0930:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0934:
        r0 = r2.photo;
        r3 = r2.parentObject;
    L_0x0938:
        r7 = r0;
        r33 = r1;
        r17 = r3;
        r11 = r6;
        r1 = 1;
        goto L_0x09c9;
    L_0x0941:
        if (r26 != 0) goto L_0x09a2;
    L_0x0943:
        r0 = r15.ttl;
        if (r0 != 0) goto L_0x09a2;
    L_0x0947:
        r0 = r58.getMessagesStorage();
        if (r26 != 0) goto L_0x094f;
    L_0x094d:
        r2 = 0;
        goto L_0x0950;
    L_0x094f:
        r2 = 3;
    L_0x0950:
        r0 = r0.getSentFile(r9, r2);
        if (r0 == 0) goto L_0x0961;
    L_0x0956:
        r11 = 0;
        r2 = r0[r11];
        r2 = (org.telegram.tgnet.TLRPC.TL_photo) r2;
        r7 = 1;
        r0 = r0[r7];
        r0 = (java.lang.String) r0;
        goto L_0x0966;
    L_0x0961:
        r7 = 1;
        r11 = 0;
        r0 = r25;
        r2 = r0;
    L_0x0966:
        if (r2 != 0) goto L_0x098c;
    L_0x0968:
        r3 = r15.uri;
        if (r3 == 0) goto L_0x098c;
    L_0x096c:
        r3 = r58.getMessagesStorage();
        r4 = r15.uri;
        r4 = org.telegram.messenger.AndroidUtilities.getPath(r4);
        if (r26 != 0) goto L_0x097a;
    L_0x0978:
        r5 = 0;
        goto L_0x097b;
    L_0x097a:
        r5 = 3;
    L_0x097b:
        r3 = r3.getSentFile(r4, r5);
        if (r3 == 0) goto L_0x098c;
    L_0x0981:
        r0 = r3[r11];
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        r2 = r3[r7];
        r2 = (java.lang.String) r2;
        r17 = r2;
        goto L_0x098f;
    L_0x098c:
        r17 = r0;
        r0 = r2;
    L_0x098f:
        r4 = r15.path;
        r5 = r15.uri;
        r31 = 0;
        r2 = r26;
        r3 = r0;
        r33 = r1;
        r11 = r6;
        r1 = 1;
        r6 = r31;
        ensureMediaThumbExists(r2, r3, r4, r5, r6);
        goto L_0x09aa;
    L_0x09a2:
        r33 = r1;
        r11 = r6;
        r1 = 1;
        r0 = r25;
        r17 = r0;
    L_0x09aa:
        if (r0 != 0) goto L_0x09c8;
    L_0x09ac:
        r0 = r58.getSendMessagesHelper();
        r2 = r15.path;
        r3 = r15.uri;
        r0 = r0.generatePhotoSizes(r2, r3);
        if (r26 == 0) goto L_0x09c8;
    L_0x09ba:
        r2 = r15.canDeleteAfter;
        if (r2 == 0) goto L_0x09c8;
    L_0x09be:
        r2 = new java.io.File;
        r3 = r15.path;
        r2.<init>(r3);
        r2.delete();
    L_0x09c8:
        r7 = r0;
    L_0x09c9:
        if (r7 == 0) goto L_0x0ab0;
    L_0x09cb:
        r8 = new java.util.HashMap;
        r8.<init>();
        r3 = new android.graphics.Bitmap[r1];
        r4 = new java.lang.String[r1];
        r0 = r15.masks;
        if (r0 == 0) goto L_0x09e0;
    L_0x09d8:
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x09e0;
    L_0x09de:
        r0 = 1;
        goto L_0x09e1;
    L_0x09e0:
        r0 = 0;
    L_0x09e1:
        r7.has_stickers = r0;
        if (r0 == 0) goto L_0x0a24;
    L_0x09e5:
        r0 = new org.telegram.tgnet.SerializedData;
        r2 = r15.masks;
        r2 = r2.size();
        r2 = r2 * 20;
        r2 = r2 + 4;
        r0.<init>(r2);
        r2 = r15.masks;
        r2 = r2.size();
        r0.writeInt32(r2);
        r2 = 0;
    L_0x09fe:
        r5 = r15.masks;
        r5 = r5.size();
        if (r2 >= r5) goto L_0x0a14;
    L_0x0a06:
        r5 = r15.masks;
        r5 = r5.get(r2);
        r5 = (org.telegram.tgnet.TLRPC.InputDocument) r5;
        r5.serializeToStream(r0);
        r2 = r2 + 1;
        goto L_0x09fe;
    L_0x0a14:
        r2 = r0.toByteArray();
        r2 = org.telegram.messenger.Utilities.bytesToHex(r2);
        r5 = "masks";
        r8.put(r5, r2);
        r0.cleanup();
    L_0x0a24:
        if (r9 == 0) goto L_0x0a29;
    L_0x0a26:
        r8.put(r11, r9);
    L_0x0a29:
        if (r60 == 0) goto L_0x0a34;
    L_0x0a2b:
        r0 = r55.size();	 Catch:{ Exception -> 0x0a4a }
        if (r0 != r1) goto L_0x0a32;
    L_0x0a31:
        goto L_0x0a34;
    L_0x0a32:
        r11 = 0;
        goto L_0x0a4f;
    L_0x0a34:
        r0 = r7.sizes;	 Catch:{ Exception -> 0x0a4a }
        r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0a4a }
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2);	 Catch:{ Exception -> 0x0a4a }
        if (r0 == 0) goto L_0x0a32;
    L_0x0a40:
        r11 = 0;
        r0 = getKeyForPhotoSize(r0, r3, r11);	 Catch:{ Exception -> 0x0a48 }
        r4[r11] = r0;	 Catch:{ Exception -> 0x0a48 }
        goto L_0x0a4f;
    L_0x0a48:
        r0 = move-exception;
        goto L_0x0a4c;
    L_0x0a4a:
        r0 = move-exception;
        r11 = 0;
    L_0x0a4c:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0a4f:
        if (r60 == 0) goto L_0x0a80;
    L_0x0a51:
        r0 = r16 + 1;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r10);
        r9 = r53;
        r2.append(r9);
        r2 = r2.toString();
        r5 = "groupId";
        r8.put(r5, r2);
        r2 = 10;
        if (r0 == r2) goto L_0x0a74;
    L_0x0a6d:
        r2 = r21 + -1;
        r6 = r44;
        if (r6 != r2) goto L_0x0a86;
    L_0x0a73:
        goto L_0x0a76;
    L_0x0a74:
        r6 = r44;
    L_0x0a76:
        r2 = "final";
        r5 = "1";
        r8.put(r2, r5);
        r23 = r29;
        goto L_0x0a86;
    L_0x0a80:
        r6 = r44;
        r9 = r53;
        r0 = r16;
    L_0x0a86:
        r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$nc6Scafqy8vNHePpYzkI8smc2ws;
        r2 = r16;
        r5 = r61;
        r31 = r6;
        r6 = r58;
        r34 = r9;
        r9 = r17;
        r17 = 0;
        r10 = r56;
        r32 = r12;
        r12 = r62;
        r1 = r13;
        r13 = r15;
        r36 = r14;
        r14 = r63;
        r15 = r64;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);
        r5 = r1;
        r2 = r23;
        r4 = r42;
        goto L_0x0af5;
    L_0x0ab0:
        r32 = r12;
        r1 = r13;
        r36 = r14;
        r31 = r44;
        r34 = r53;
        r17 = 0;
        if (r1 != 0) goto L_0x0ad6;
    L_0x0abd:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r27 = new java.util.ArrayList;
        r27.<init>();
        r28 = new java.util.ArrayList;
        r28.<init>();
        r0 = r27;
        r1 = r28;
        goto L_0x0add;
    L_0x0ad6:
        r5 = r1;
        r0 = r27;
        r1 = r28;
        r4 = r42;
    L_0x0add:
        r5.add(r8);
        r4.add(r9);
        r2 = r15.caption;
        r0.add(r2);
        r2 = r15.entities;
        r1.add(r2);
        r27 = r0;
        r28 = r1;
        r0 = r16;
        r2 = r23;
    L_0x0af5:
        r9 = r31 + 1;
        r1 = r55;
        r13 = r58;
        r12 = r21;
        r15 = r25;
        r10 = r26;
        r11 = r32;
        r26 = r33;
        r23 = r34;
        r14 = r36;
        r8 = r38;
        goto L_0x017e;
    L_0x0b0d:
        r42 = r4;
        r1 = r5;
        r17 = 0;
        r29 = 0;
        r0 = (r2 > r29 ? 1 : (r2 == r29 ? 0 : -1));
        if (r0 == 0) goto L_0x0b25;
    L_0x0b18:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$qGewQ949pD132CLASSNAMEiKmTQN7QSVI;
        r15 = r58;
        r14 = r64;
        r0.<init>(r15, r2, r14);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        goto L_0x0b29;
    L_0x0b25:
        r15 = r58;
        r14 = r64;
    L_0x0b29:
        if (r65 == 0) goto L_0x0b2e;
    L_0x0b2b:
        r65.releasePermission();
    L_0x0b2e:
        if (r1 == 0) goto L_0x0b7c;
    L_0x0b30:
        r0 = r1.isEmpty();
        if (r0 != 0) goto L_0x0b7c;
    L_0x0b36:
        r0 = 0;
    L_0x0b37:
        r2 = r1.size();
        if (r0 >= r2) goto L_0x0b7c;
    L_0x0b3d:
        r2 = r1.get(r0);
        r2 = (java.lang.String) r2;
        r13 = r42;
        r3 = r13.get(r0);
        r3 = (java.lang.String) r3;
        r4 = 0;
        r12 = r27;
        r5 = r12.get(r0);
        r9 = r5;
        r9 = (java.lang.CharSequence) r9;
        r11 = r28;
        r5 = r11.get(r0);
        r10 = r5;
        r10 = (java.util.ArrayList) r10;
        r16 = r1;
        r1 = r58;
        r5 = r22;
        r6 = r56;
        r8 = r62;
        r11 = r61;
        r12 = r59;
        r17 = r13;
        r13 = r63;
        r14 = r64;
        prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14);
        r0 = r0 + 1;
        r1 = r16;
        r42 = r17;
        goto L_0x0b37;
    L_0x0b7c:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0b9a;
    L_0x0b80:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "total send time = ";
        r0.append(r1);
        r1 = java.lang.System.currentTimeMillis();
        r1 = r1 - r18;
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0b9a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$62(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, boolean, boolean, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    static /* synthetic */ void lambda$null$56(MediaSendPrepareWorker mediaSendPrepareWorker, AccountInstance accountInstance, SendingMediaInfo sendingMediaInfo, boolean z) {
        mediaSendPrepareWorker.photo = accountInstance.getSendMessagesHelper().generatePhotoSizes(sendingMediaInfo.path, sendingMediaInfo.uri);
        if (z && sendingMediaInfo.canDeleteAfter) {
            new File(sendingMediaInfo.path).delete();
        }
        mediaSendPrepareWorker.sync.countDown();
    }

    static /* synthetic */ void lambda$null$57(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, null, tL_document, str, hashMap, false, str2);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, null, str, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, z, i, 0, str2);
    }

    static /* synthetic */ void lambda$null$58(MessageObject messageObject, AccountInstance accountInstance, TL_photo tL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2, boolean z2, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        String str2 = null;
        if (messageObject != null) {
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            if (z) {
                str2 = sendingMediaInfo2.searchImage.imageUrl;
            }
            sendMessagesHelper.editMessageMedia(messageObject, tL_photo, null, null, str2, hashMap, false, str);
            return;
        }
        SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
        if (z) {
            str2 = sendingMediaInfo2.searchImage.imageUrl;
        }
        sendMessagesHelper2.sendMessage(tL_photo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, z2, i, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$59(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        Bitmap bitmap2 = bitmap;
        String str4 = str;
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmap2 == null || str4 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str4);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, videoEditedInfo, tL_document, str2, hashMap, false, str3);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, videoEditedInfo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, z, i, sendingMediaInfo2.ttl, str3);
    }

    static /* synthetic */ void lambda$null$60(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TL_photo tL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, tL_photo, null, null, null, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_photo, null, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, z, i, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$61(AccountInstance accountInstance, long j, int i) {
        SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
        HashMap hashMap = sendMessagesHelper.delayedMessages;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("group_");
        stringBuilder.append(j);
        ArrayList arrayList = (ArrayList) hashMap.get(stringBuilder.toString());
        if (arrayList != null && !arrayList.isEmpty()) {
            DelayedMessage delayedMessage = (DelayedMessage) arrayList.get(0);
            ArrayList arrayList2 = delayedMessage.messageObjects;
            MessageObject messageObject = (MessageObject) arrayList2.get(arrayList2.size() - 1);
            delayedMessage.finalGroupMessage = messageObject.getId();
            messageObject.messageOwner.params.put("final", "1");
            messages_Messages tL_messages_messages = new TL_messages_messages();
            tL_messages_messages.messages.add(messageObject.messageOwner);
            accountInstance.getMessagesStorage().putMessages(tL_messages_messages, delayedMessage.peer, -2, 0, false, i != 0);
            sendMessagesHelper.sendReadyToSendGroup(delayedMessage, true, true);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x008b A:{SYNTHETIC, Splitter:B:45:0x008b} */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0080 A:{SYNTHETIC, Splitter:B:39:0x0080} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x008b A:{SYNTHETIC, Splitter:B:45:0x008b} */
    /* JADX WARNING: Removed duplicated region for block: B:58:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00c1 A:{SYNTHETIC, Splitter:B:52:0x00c1} */
    private static void fillVideoAttribute(java.lang.String r5, org.telegram.tgnet.TLRPC.TL_documentAttributeVideo r6, org.telegram.messenger.VideoEditedInfo r7) {
        /*
        r0 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r1 = 0;
        r2 = new android.media.MediaMetadataRetriever;	 Catch:{ Exception -> 0x007a }
        r2.<init>();	 Catch:{ Exception -> 0x007a }
        r2.setDataSource(r5);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r1 = 18;
        r1 = r2.extractMetadata(r1);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        if (r1 == 0) goto L_0x0019;
    L_0x0013:
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r6.w = r1;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
    L_0x0019:
        r1 = 19;
        r1 = r2.extractMetadata(r1);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        if (r1 == 0) goto L_0x0027;
    L_0x0021:
        r1 = java.lang.Integer.parseInt(r1);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r6.h = r1;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
    L_0x0027:
        r1 = 9;
        r1 = r2.extractMetadata(r1);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        if (r1 == 0) goto L_0x003d;
    L_0x002f:
        r3 = java.lang.Long.parseLong(r1);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r1 = (float) r3;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r1 = r1 / r0;
        r3 = (double) r1;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r3 = java.lang.Math.ceil(r3);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r1 = (int) r3;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r6.duration = r1;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
    L_0x003d:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r3 = 17;
        if (r1 < r3) goto L_0x0068;
    L_0x0043:
        r1 = 24;
        r1 = r2.extractMetadata(r1);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        if (r1 == 0) goto L_0x0068;
    L_0x004b:
        r1 = org.telegram.messenger.Utilities.parseInt(r1);	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r1 = r1.intValue();	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        if (r7 == 0) goto L_0x0058;
    L_0x0055:
        r7.rotationValue = r1;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        goto L_0x0068;
    L_0x0058:
        r7 = 90;
        if (r1 == r7) goto L_0x0060;
    L_0x005c:
        r7 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r1 != r7) goto L_0x0068;
    L_0x0060:
        r7 = r6.w;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r1 = r6.h;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r6.w = r1;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
        r6.h = r7;	 Catch:{ Exception -> 0x0074, all -> 0x0072 }
    L_0x0068:
        r7 = 1;
        r2.release();	 Catch:{ Exception -> 0x006d }
        goto L_0x0089;
    L_0x006d:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x0089;
    L_0x0072:
        r5 = move-exception;
        goto L_0x00bf;
    L_0x0074:
        r7 = move-exception;
        r1 = r2;
        goto L_0x007b;
    L_0x0077:
        r5 = move-exception;
        r2 = r1;
        goto L_0x00bf;
    L_0x007a:
        r7 = move-exception;
    L_0x007b:
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ all -> 0x0077 }
        if (r1 == 0) goto L_0x0088;
    L_0x0080:
        r1.release();	 Catch:{ Exception -> 0x0084 }
        goto L_0x0088;
    L_0x0084:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x0088:
        r7 = 0;
    L_0x0089:
        if (r7 != 0) goto L_0x00be;
    L_0x008b:
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00ba }
        r1 = new java.io.File;	 Catch:{ Exception -> 0x00ba }
        r1.<init>(r5);	 Catch:{ Exception -> 0x00ba }
        r5 = android.net.Uri.fromFile(r1);	 Catch:{ Exception -> 0x00ba }
        r5 = android.media.MediaPlayer.create(r7, r5);	 Catch:{ Exception -> 0x00ba }
        if (r5 == 0) goto L_0x00be;
    L_0x009c:
        r7 = r5.getDuration();	 Catch:{ Exception -> 0x00ba }
        r7 = (float) r7;	 Catch:{ Exception -> 0x00ba }
        r7 = r7 / r0;
        r0 = (double) r7;	 Catch:{ Exception -> 0x00ba }
        r0 = java.lang.Math.ceil(r0);	 Catch:{ Exception -> 0x00ba }
        r7 = (int) r0;	 Catch:{ Exception -> 0x00ba }
        r6.duration = r7;	 Catch:{ Exception -> 0x00ba }
        r7 = r5.getVideoWidth();	 Catch:{ Exception -> 0x00ba }
        r6.w = r7;	 Catch:{ Exception -> 0x00ba }
        r7 = r5.getVideoHeight();	 Catch:{ Exception -> 0x00ba }
        r6.h = r7;	 Catch:{ Exception -> 0x00ba }
        r5.release();	 Catch:{ Exception -> 0x00ba }
        goto L_0x00be;
    L_0x00ba:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);
    L_0x00be:
        return;
    L_0x00bf:
        if (r2 == 0) goto L_0x00c9;
    L_0x00c1:
        r2.release();	 Catch:{ Exception -> 0x00c5 }
        goto L_0x00c9;
    L_0x00c5:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
    L_0x00c9:
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.fillVideoAttribute(java.lang.String, org.telegram.tgnet.TLRPC$TL_documentAttributeVideo, org.telegram.messenger.VideoEditedInfo):void");
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0019 */
    private static android.graphics.Bitmap createVideoThumbnail(java.lang.String r2, long r3) {
        /*
        r0 = new android.media.MediaMetadataRetriever;
        r0.<init>();
        r1 = 0;
        r0.setDataSource(r2);	 Catch:{ Exception -> 0x0019, all -> 0x0014 }
        r2 = 1;
        r2 = r0.getFrameAtTime(r3, r2);	 Catch:{ Exception -> 0x0019, all -> 0x0014 }
        r0.release();	 Catch:{ RuntimeException -> 0x0012 }
        goto L_0x001d;
        goto L_0x001d;
    L_0x0014:
        r2 = move-exception;
        r0.release();	 Catch:{ RuntimeException -> 0x0018 }
    L_0x0018:
        throw r2;
    L_0x0019:
        r0.release();	 Catch:{ RuntimeException -> 0x001c }
    L_0x001c:
        r2 = r1;
    L_0x001d:
        if (r2 != 0) goto L_0x0020;
    L_0x001f:
        return r1;
    L_0x0020:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.createVideoThumbnail(java.lang.String, long):android.graphics.Bitmap");
    }

    /* JADX WARNING: Removed duplicated region for block: B:63:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0188  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01a5  */
    private static org.telegram.messenger.VideoEditedInfo createCompressionSettings(java.lang.String r16) {
        /*
        r0 = r16;
        r1 = "video/avc";
        r2 = 9;
        r2 = new int[r2];
        org.telegram.ui.Components.AnimatedFileDrawable.getVideoInfo(r0, r2);
        r3 = 0;
        r3 = r2[r3];
        r4 = 0;
        if (r3 != 0) goto L_0x001b;
    L_0x0011:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x001a;
    L_0x0015:
        r0 = "video hasn't avc1 atom";
        org.telegram.messenger.FileLog.d(r0);
    L_0x001a:
        return r4;
    L_0x001b:
        r3 = 3;
        r5 = r2[r3];
        r6 = r2[r3];
        r7 = 4;
        r8 = r2[r7];
        r8 = (float) r8;
        r9 = 6;
        r9 = r2[r9];
        r9 = (long) r9;
        r11 = 5;
        r12 = r2[r11];
        r12 = (long) r12;
        r14 = 7;
        r14 = r2[r14];
        r15 = 900000; // 0xdbba0 float:1.261169E-39 double:4.44659E-318;
        if (r6 <= r15) goto L_0x0037;
    L_0x0034:
        r6 = 900000; // 0xdbba0 float:1.261169E-39 double:4.44659E-318;
    L_0x0037:
        r3 = android.os.Build.VERSION.SDK_INT;
        r7 = 18;
        if (r3 >= r7) goto L_0x00b3;
    L_0x003d:
        r3 = org.telegram.messenger.MediaController.selectCodec(r1);	 Catch:{ Exception -> 0x00b2 }
        if (r3 != 0) goto L_0x004d;
    L_0x0043:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x00b2 }
        if (r0 == 0) goto L_0x004c;
    L_0x0047:
        r0 = "no codec info for video/avc";
        org.telegram.messenger.FileLog.d(r0);	 Catch:{ Exception -> 0x00b2 }
    L_0x004c:
        return r4;
    L_0x004d:
        r7 = r3.getName();	 Catch:{ Exception -> 0x00b2 }
        r11 = "OMX.google.h264.encoder";
        r11 = r7.equals(r11);	 Catch:{ Exception -> 0x00b2 }
        if (r11 != 0) goto L_0x009a;
    L_0x0059:
        r11 = "OMX.ST.VFM.H264Enc";
        r11 = r7.equals(r11);	 Catch:{ Exception -> 0x00b2 }
        if (r11 != 0) goto L_0x009a;
    L_0x0061:
        r11 = "OMX.Exynos.avc.enc";
        r11 = r7.equals(r11);	 Catch:{ Exception -> 0x00b2 }
        if (r11 != 0) goto L_0x009a;
    L_0x0069:
        r11 = "OMX.MARVELL.VIDEO.HW.CODA7542ENCODER";
        r11 = r7.equals(r11);	 Catch:{ Exception -> 0x00b2 }
        if (r11 != 0) goto L_0x009a;
    L_0x0071:
        r11 = "OMX.MARVELL.VIDEO.H264ENCODER";
        r11 = r7.equals(r11);	 Catch:{ Exception -> 0x00b2 }
        if (r11 != 0) goto L_0x009a;
    L_0x0079:
        r11 = "OMX.k3.video.encoder.avc";
        r11 = r7.equals(r11);	 Catch:{ Exception -> 0x00b2 }
        if (r11 != 0) goto L_0x009a;
    L_0x0081:
        r11 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r11 = r7.equals(r11);	 Catch:{ Exception -> 0x00b2 }
        if (r11 == 0) goto L_0x008a;
    L_0x0089:
        goto L_0x009a;
    L_0x008a:
        r1 = org.telegram.messenger.MediaController.selectColorFormat(r3, r1);	 Catch:{ Exception -> 0x00b2 }
        if (r1 != 0) goto L_0x00b3;
    L_0x0090:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x00b2 }
        if (r0 == 0) goto L_0x0099;
    L_0x0094:
        r0 = "no color format for video/avc";
        org.telegram.messenger.FileLog.d(r0);	 Catch:{ Exception -> 0x00b2 }
    L_0x0099:
        return r4;
    L_0x009a:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x00b2 }
        if (r0 == 0) goto L_0x00b2;
    L_0x009e:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00b2 }
        r0.<init>();	 Catch:{ Exception -> 0x00b2 }
        r1 = "unsupported encoder = ";
        r0.append(r1);	 Catch:{ Exception -> 0x00b2 }
        r0.append(r7);	 Catch:{ Exception -> 0x00b2 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x00b2 }
        org.telegram.messenger.FileLog.d(r0);	 Catch:{ Exception -> 0x00b2 }
    L_0x00b2:
        return r4;
    L_0x00b3:
        r1 = new org.telegram.messenger.VideoEditedInfo;
        r1.<init>();
        r3 = -1;
        r1.startTime = r3;
        r1.endTime = r3;
        r1.bitrate = r6;
        r1.originalPath = r0;
        r1.framerate = r14;
        r3 = (double) r8;
        r3 = java.lang.Math.ceil(r3);
        r3 = (long) r3;
        r1.estimatedDuration = r3;
        r3 = 1;
        r4 = r2[r3];
        r1.originalWidth = r4;
        r1.resultWidth = r4;
        r4 = 2;
        r7 = r2[r4];
        r1.originalHeight = r7;
        r1.resultHeight = r7;
        r7 = 8;
        r2 = r2[r7];
        r1.rotationValue = r2;
        r2 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r7 = "compress_video2";
        r2 = r2.getInt(r7, r3);
        r7 = r1.originalWidth;
        r11 = 1280; // 0x500 float:1.794E-42 double:6.324E-321;
        if (r7 > r11) goto L_0x0112;
    L_0x00f0:
        r14 = r1.originalHeight;
        if (r14 <= r11) goto L_0x00f5;
    L_0x00f4:
        goto L_0x0112;
    L_0x00f5:
        r11 = 848; // 0x350 float:1.188E-42 double:4.19E-321;
        if (r7 > r11) goto L_0x0110;
    L_0x00f9:
        if (r14 <= r11) goto L_0x00fc;
    L_0x00fb:
        goto L_0x0110;
    L_0x00fc:
        r11 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
        if (r7 > r11) goto L_0x010e;
    L_0x0100:
        if (r14 <= r11) goto L_0x0103;
    L_0x0102:
        goto L_0x010e;
    L_0x0103:
        r11 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r7 > r11) goto L_0x010c;
    L_0x0107:
        if (r14 <= r11) goto L_0x010a;
    L_0x0109:
        goto L_0x010c;
    L_0x010a:
        r7 = 1;
        goto L_0x0113;
    L_0x010c:
        r7 = 2;
        goto L_0x0113;
    L_0x010e:
        r7 = 3;
        goto L_0x0113;
    L_0x0110:
        r7 = 4;
        goto L_0x0113;
    L_0x0112:
        r7 = 5;
    L_0x0113:
        if (r2 < r7) goto L_0x0117;
    L_0x0115:
        r2 = r7 + -1;
    L_0x0117:
        r7 = r7 - r3;
        if (r2 == r7) goto L_0x016e;
    L_0x011a:
        if (r2 == 0) goto L_0x012f;
    L_0x011c:
        if (r2 == r3) goto L_0x012c;
    L_0x011e:
        if (r2 == r4) goto L_0x0126;
    L_0x0120:
        r15 = 2500000; // 0x2625a0 float:3.503246E-39 double:1.235164E-317;
        r3 = NUM; // 0x44a00000 float:1280.0 double:5.68835786E-315;
        goto L_0x0134;
    L_0x0126:
        r3 = NUM; // 0x44540000 float:848.0 double:5.66374975E-315;
        r15 = 1100000; // 0x10c8e0 float:1.541428E-39 double:5.43472E-318;
        goto L_0x0134;
    L_0x012c:
        r3 = NUM; // 0x44200000 float:640.0 double:5.646912627E-315;
        goto L_0x0134;
    L_0x012f:
        r3 = NUM; // 0x43d80000 float:432.0 double:5.623599685E-315;
        r15 = 400000; // 0x61a80 float:5.6052E-40 double:1.976263E-318;
    L_0x0134:
        r11 = r1.originalWidth;
        r14 = r1.originalHeight;
        if (r11 <= r14) goto L_0x013c;
    L_0x013a:
        r11 = (float) r11;
        goto L_0x013d;
    L_0x013c:
        r11 = (float) r14;
    L_0x013d:
        r3 = r3 / r11;
        r11 = r1.originalWidth;
        r11 = (float) r11;
        r11 = r11 * r3;
        r14 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r11 = r11 / r14;
        r11 = java.lang.Math.round(r11);
        r11 = r11 * 2;
        r1.resultWidth = r11;
        r11 = r1.originalHeight;
        r11 = (float) r11;
        r11 = r11 * r3;
        r11 = r11 / r14;
        r11 = java.lang.Math.round(r11);
        r11 = r11 * 2;
        r1.resultHeight = r11;
        if (r6 == 0) goto L_0x016e;
    L_0x015e:
        r4 = (float) r5;
        r4 = r4 / r3;
        r3 = (int) r4;
        r6 = java.lang.Math.min(r15, r3);
        r3 = r6 / 8;
        r3 = (float) r3;
        r3 = r3 * r8;
        r4 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r3 = r3 / r4;
        r9 = (long) r3;
    L_0x016e:
        if (r2 != r7) goto L_0x0188;
    L_0x0170:
        r2 = r1.originalWidth;
        r1.resultWidth = r2;
        r2 = r1.originalHeight;
        r1.resultHeight = r2;
        r1.bitrate = r5;
        r2 = new java.io.File;
        r2.<init>(r0);
        r2 = r2.length();
        r0 = (int) r2;
        r2 = (long) r0;
        r1.estimatedSize = r2;
        goto L_0x019d;
    L_0x0188:
        r1.bitrate = r6;
        r12 = r12 + r9;
        r0 = (int) r12;
        r2 = (long) r0;
        r1.estimatedSize = r2;
        r2 = r1.estimatedSize;
        r4 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r4 = r2 / r4;
        r6 = 16;
        r4 = r4 * r6;
        r2 = r2 + r4;
        r1.estimatedSize = r2;
    L_0x019d:
        r2 = r1.estimatedSize;
        r4 = 0;
        r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r0 != 0) goto L_0x01a9;
    L_0x01a5:
        r2 = 1;
        r1.estimatedSize = r2;
    L_0x01a9:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.createCompressionSettings(java.lang.String):org.telegram.messenger.VideoEditedInfo");
    }

    public static void prepareSendingVideo(AccountInstance accountInstance, String str, long j, long j2, int i, int i2, VideoEditedInfo videoEditedInfo, long j3, MessageObject messageObject, CharSequence charSequence, ArrayList<MessageEntity> arrayList, int i3, MessageObject messageObject2, boolean z, int i4) {
        if (str != null && str.length() != 0) {
            -$$Lambda$SendMessagesHelper$owEU7ANs-6baLYIST3Xkq2me00g -__lambda_sendmessageshelper_oweu7ans-6balyist3xkq2me00g = r0;
            -$$Lambda$SendMessagesHelper$owEU7ANs-6baLYIST3Xkq2me00g -__lambda_sendmessageshelper_oweu7ans-6balyist3xkq2me00g2 = new -$$Lambda$SendMessagesHelper$owEU7ANs-6baLYIST3Xkq2me00g(videoEditedInfo, str, j3, j2, i3, accountInstance, i2, i, j, charSequence, messageObject2, messageObject, arrayList, z, i4);
            new Thread(-__lambda_sendmessageshelper_oweu7ans-6balyist3xkq2me00g).start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:79:0x0223  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x026a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0240  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x02c4  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0313  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x031b  */
    static /* synthetic */ void lambda$prepareSendingVideo$64(org.telegram.messenger.VideoEditedInfo r32, java.lang.String r33, long r34, long r36, int r38, org.telegram.messenger.AccountInstance r39, int r40, int r41, long r42, java.lang.CharSequence r44, org.telegram.messenger.MessageObject r45, org.telegram.messenger.MessageObject r46, java.util.ArrayList r47, boolean r48, int r49) {
        /*
        r6 = r33;
        r10 = r34;
        r7 = r36;
        r9 = r40;
        r12 = r41;
        if (r32 == 0) goto L_0x000f;
    L_0x000c:
        r13 = r32;
        goto L_0x0014;
    L_0x000f:
        r0 = createCompressionSettings(r33);
        r13 = r0;
    L_0x0014:
        r0 = (int) r10;
        if (r0 != 0) goto L_0x0019;
    L_0x0017:
        r4 = 1;
        goto L_0x001a;
    L_0x0019:
        r4 = 0;
    L_0x001a:
        if (r13 == 0) goto L_0x0022;
    L_0x001c:
        r0 = r13.roundVideo;
        if (r0 == 0) goto L_0x0022;
    L_0x0020:
        r5 = 1;
        goto L_0x0023;
    L_0x0022:
        r5 = 0;
    L_0x0023:
        if (r13 != 0) goto L_0x004d;
    L_0x0025:
        r0 = "mp4";
        r0 = r6.endsWith(r0);
        if (r0 != 0) goto L_0x004d;
    L_0x002d:
        if (r5 == 0) goto L_0x0030;
    L_0x002f:
        goto L_0x004d;
    L_0x0030:
        r3 = 0;
        r4 = 0;
        r12 = 0;
        r0 = r39;
        r1 = r33;
        r2 = r33;
        r5 = r34;
        r7 = r46;
        r8 = r44;
        r9 = r47;
        r10 = r45;
        r11 = r12;
        r12 = r48;
        r13 = r49;
        prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11, r12, r13);
        goto L_0x0343;
    L_0x004d:
        r3 = new java.io.File;
        r3.<init>(r6);
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r6);
        r1 = r3.length();
        r0.append(r1);
        r2 = "_";
        r0.append(r2);
        r14 = r3.lastModified();
        r0.append(r14);
        r0 = r0.toString();
        r14 = "";
        r17 = 0;
        if (r13 == 0) goto L_0x00d2;
    L_0x0077:
        if (r5 != 0) goto L_0x00c1;
    L_0x0079:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r1.append(r7);
        r1.append(r2);
        r19 = r14;
        r14 = r13.startTime;
        r1.append(r14);
        r1.append(r2);
        r14 = r13.endTime;
        r1.append(r14);
        r0 = r13.muted;
        if (r0 == 0) goto L_0x009d;
    L_0x009a:
        r14 = "_m";
        goto L_0x009f;
    L_0x009d:
        r14 = r19;
    L_0x009f:
        r1.append(r14);
        r0 = r1.toString();
        r1 = r13.resultWidth;
        r14 = r13.originalWidth;
        if (r1 == r14) goto L_0x00c3;
    L_0x00ac:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r1.append(r2);
        r0 = r13.resultWidth;
        r1.append(r0);
        r0 = r1.toString();
        goto L_0x00c3;
    L_0x00c1:
        r19 = r14;
    L_0x00c3:
        r14 = r13.startTime;
        r1 = (r14 > r17 ? 1 : (r14 == r17 ? 0 : -1));
        if (r1 < 0) goto L_0x00ca;
    L_0x00c9:
        goto L_0x00cc;
    L_0x00ca:
        r14 = r17;
    L_0x00cc:
        r30 = r14;
        r14 = r0;
        r0 = r30;
        goto L_0x00d7;
    L_0x00d2:
        r19 = r14;
        r14 = r0;
        r0 = r17;
    L_0x00d7:
        if (r4 != 0) goto L_0x011e;
    L_0x00d9:
        if (r38 != 0) goto L_0x011e;
    L_0x00db:
        r15 = r39.getMessagesStorage();
        if (r4 != 0) goto L_0x00e5;
    L_0x00e1:
        r21 = r0;
        r0 = 2;
        goto L_0x00ea;
    L_0x00e5:
        r20 = 5;
        r21 = r0;
        r0 = 5;
    L_0x00ea:
        r0 = r15.getSentFile(r14, r0);
        if (r0 == 0) goto L_0x0115;
    L_0x00f0:
        r1 = 0;
        r15 = r0[r1];
        r15 = (org.telegram.tgnet.TLRPC.TL_document) r15;
        r1 = 1;
        r0 = r0[r1];
        r20 = r0;
        r20 = (java.lang.String) r20;
        r23 = 0;
        r24 = r21;
        r0 = r4;
        r1 = r15;
        r21 = r15;
        r15 = r2;
        r2 = r33;
        r22 = r3;
        r3 = r23;
        r9 = r5;
        r23 = r14;
        r14 = r4;
        r4 = r24;
        ensureMediaThumbExists(r0, r1, r2, r3, r4);
        goto L_0x012b;
    L_0x0115:
        r15 = r2;
        r9 = r5;
        r23 = r14;
        r24 = r21;
        r22 = r3;
        goto L_0x0126;
    L_0x011e:
        r24 = r0;
        r15 = r2;
        r22 = r3;
        r9 = r5;
        r23 = r14;
    L_0x0126:
        r14 = r4;
        r20 = 0;
        r21 = 0;
    L_0x012b:
        if (r21 != 0) goto L_0x02d4;
    L_0x012d:
        r0 = r24;
        r0 = createVideoThumbnail(r6, r0);
        if (r0 != 0) goto L_0x013a;
    L_0x0135:
        r1 = 1;
        r0 = android.media.ThumbnailUtils.createVideoThumbnail(r6, r1);
    L_0x013a:
        r1 = 90;
        if (r14 != 0) goto L_0x0144;
    L_0x013e:
        if (r38 == 0) goto L_0x0141;
    L_0x0140:
        goto L_0x0144;
    L_0x0141:
        r2 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x0146;
    L_0x0144:
        r2 = 90;
    L_0x0146:
        r3 = (float) r2;
        if (r2 <= r1) goto L_0x014c;
    L_0x0149:
        r2 = 80;
        goto L_0x014e;
    L_0x014c:
        r2 = 55;
    L_0x014e:
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r14);
        if (r0 == 0) goto L_0x021b;
    L_0x0154:
        if (r2 == 0) goto L_0x021b;
    L_0x0156:
        if (r9 == 0) goto L_0x021a;
    L_0x0158:
        r3 = 21;
        if (r14 == 0) goto L_0x01bb;
    L_0x015c:
        r25 = 7;
        r4 = android.os.Build.VERSION.SDK_INT;
        if (r4 >= r3) goto L_0x0165;
    L_0x0162:
        r26 = 0;
        goto L_0x0167;
    L_0x0165:
        r26 = 1;
    L_0x0167:
        r27 = r0.getWidth();
        r28 = r0.getHeight();
        r29 = r0.getRowBytes();
        r24 = r0;
        org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = r2.location;
        r4 = r4.volume_id;
        r3.append(r4);
        r3.append(r15);
        r4 = r2.location;
        r4 = r4.local_id;
        r3.append(r4);
        r4 = "@%d_%d_b2";
        r3.append(r4);
        r3 = r3.toString();
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r5 = (float) r5;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r5 = r5 / r15;
        r5 = (int) r5;
        r5 = java.lang.Integer.valueOf(r5);
        r15 = 0;
        r4[r15] = r5;
        r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r5 = (float) r5;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r5 = r5 / r15;
        r5 = (int) r5;
        r5 = java.lang.Integer.valueOf(r5);
        r15 = 1;
        r4[r15] = r5;
        r15 = java.lang.String.format(r3, r4);
        goto L_0x021c;
    L_0x01bb:
        r25 = 3;
        r4 = android.os.Build.VERSION.SDK_INT;
        if (r4 >= r3) goto L_0x01c4;
    L_0x01c1:
        r26 = 0;
        goto L_0x01c6;
    L_0x01c4:
        r26 = 1;
    L_0x01c6:
        r27 = r0.getWidth();
        r28 = r0.getHeight();
        r29 = r0.getRowBytes();
        r24 = r0;
        org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = r2.location;
        r4 = r4.volume_id;
        r3.append(r4);
        r3.append(r15);
        r4 = r2.location;
        r4 = r4.local_id;
        r3.append(r4);
        r4 = "@%d_%d_b";
        r3.append(r4);
        r3 = r3.toString();
        r4 = 2;
        r4 = new java.lang.Object[r4];
        r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r5 = (float) r5;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r5 = r5 / r15;
        r5 = (int) r5;
        r5 = java.lang.Integer.valueOf(r5);
        r15 = 0;
        r4[r15] = r5;
        r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r5 = (float) r5;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r5 = r5 / r15;
        r5 = (int) r5;
        r5 = java.lang.Integer.valueOf(r5);
        r15 = 1;
        r4[r15] = r5;
        r15 = java.lang.String.format(r3, r4);
        goto L_0x021c;
    L_0x021a:
        r0 = 0;
    L_0x021b:
        r15 = 0;
    L_0x021c:
        r3 = new org.telegram.tgnet.TLRPC$TL_document;
        r3.<init>();
        if (r2 == 0) goto L_0x022e;
    L_0x0223:
        r4 = r3.thumbs;
        r4.add(r2);
        r2 = r3.flags;
        r4 = 1;
        r2 = r2 | r4;
        r3.flags = r2;
    L_0x022e:
        r2 = 0;
        r4 = new byte[r2];
        r3.file_reference = r4;
        r4 = "video/mp4";
        r3.mime_type = r4;
        r4 = r39.getUserConfig();
        r4.saveConfig(r2);
        if (r14 == 0) goto L_0x026a;
    L_0x0240:
        r2 = 32;
        r4 = r10 >> r2;
        r2 = (int) r4;
        r4 = r39.getMessagesController();
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r4.getEncryptedChat(r2);
        if (r2 != 0) goto L_0x0254;
    L_0x0253:
        return;
    L_0x0254:
        r2 = r2.layer;
        r2 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r2);
        r4 = 66;
        if (r2 < r4) goto L_0x0264;
    L_0x025e:
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r2.<init>();
        goto L_0x0272;
    L_0x0264:
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r2.<init>();
        goto L_0x0272;
    L_0x026a:
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r2.<init>();
        r4 = 1;
        r2.supports_streaming = r4;
    L_0x0272:
        r2.round_message = r9;
        r4 = r3.attributes;
        r4.add(r2);
        if (r13 == 0) goto L_0x02be;
    L_0x027b:
        r4 = r13.needConvert();
        if (r4 == 0) goto L_0x02be;
    L_0x0281:
        r4 = r13.muted;
        if (r4 == 0) goto L_0x029b;
    L_0x0285:
        r4 = r3.attributes;
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r5.<init>();
        r4.add(r5);
        fillVideoAttribute(r6, r2, r13);
        r4 = r2.w;
        r13.originalWidth = r4;
        r4 = r2.h;
        r13.originalHeight = r4;
        goto L_0x02a2;
    L_0x029b:
        r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r4 = r7 / r4;
        r5 = (int) r4;
        r2.duration = r5;
    L_0x02a2:
        r4 = r13.rotationValue;
        if (r4 == r1) goto L_0x02b2;
    L_0x02a6:
        r1 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r4 != r1) goto L_0x02ab;
    L_0x02aa:
        goto L_0x02b2;
    L_0x02ab:
        r2.w = r12;
        r1 = r40;
        r2.h = r1;
        goto L_0x02b8;
    L_0x02b2:
        r1 = r40;
        r2.w = r1;
        r2.h = r12;
    L_0x02b8:
        r1 = r42;
        r2 = (int) r1;
        r3.size = r2;
        goto L_0x02cf;
    L_0x02be:
        r1 = r22.exists();
        if (r1 == 0) goto L_0x02cb;
    L_0x02c4:
        r4 = r22.length();
        r1 = (int) r4;
        r3.size = r1;
    L_0x02cb:
        r1 = 0;
        fillVideoAttribute(r6, r2, r1);
    L_0x02cf:
        r1 = r0;
        r21 = r3;
        r2 = r15;
        goto L_0x02d6;
    L_0x02d4:
        r1 = 0;
        r2 = r1;
    L_0x02d6:
        if (r13 == 0) goto L_0x030b;
    L_0x02d8:
        r0 = r13.needConvert();
        if (r0 == 0) goto L_0x030b;
    L_0x02de:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = "-2147483648_";
        r0.append(r3);
        r3 = org.telegram.messenger.SharedConfig.getLastLocalId();
        r0.append(r3);
        r3 = ".mp4";
        r0.append(r3);
        r0 = r0.toString();
        r3 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r3.<init>(r4, r0);
        org.telegram.messenger.SharedConfig.saveConfig();
        r0 = r3.getAbsolutePath();
        r7 = r0;
        goto L_0x030c;
    L_0x030b:
        r7 = r6;
    L_0x030c:
        r8 = new java.util.HashMap;
        r8.<init>();
        if (r44 == 0) goto L_0x0319;
    L_0x0313:
        r0 = r44.toString();
        r19 = r0;
    L_0x0319:
        if (r23 == 0) goto L_0x0322;
    L_0x031b:
        r0 = "originalPath";
        r3 = r23;
        r8.put(r0, r3);
    L_0x0322:
        r18 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$wEBYQCLASSNAMEOcsOy27No9lSN7-6Kn8;
        r0 = r18;
        r3 = r45;
        r4 = r39;
        r5 = r13;
        r6 = r21;
        r9 = r20;
        r10 = r34;
        r12 = r46;
        r13 = r19;
        r14 = r47;
        r15 = r48;
        r16 = r49;
        r17 = r38;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16, r17);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r18);
    L_0x0343:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$64(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, long, int, org.telegram.messenger.AccountInstance, int, int, long, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$63(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, String str4, ArrayList arrayList, boolean z, int i, int i2) {
        Bitmap bitmap2 = bitmap;
        String str5 = str;
        if (!(bitmap2 == null || str5 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str5);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, videoEditedInfo, tL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tL_document, videoEditedInfo, str2, j, messageObject2, str4, arrayList, null, hashMap, z, i, i2, str3);
        }
    }
}
