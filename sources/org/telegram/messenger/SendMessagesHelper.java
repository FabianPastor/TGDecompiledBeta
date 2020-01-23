package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.Point;

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
            this.locationQueryCancelRunnable = new -$$Lambda$SendMessagesHelper$LocationProvider$Kc-6YgD2bSOGi90-WwdXYCZI1bg(this);
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
            Document document3;
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
                    document3 = tL_document_layer82;
                } else {
                    return;
                }
            }
            document3 = document2;
            if (MessageObject.isGifDocument(document3)) {
                mediaSendQueue.postRunnable(new -$$Lambda$SendMessagesHelper$MpLQRRhv7itAJQJhQaJUkhxz2qQ(this, document3, j, messageObject, z, i, obj));
            } else {
                sendMessage((TL_document) document3, null, null, j, messageObject, null, null, null, null, z, i, 0, obj);
            }
        }
    }

    public /* synthetic */ void lambda$sendSticker$6$SendMessagesHelper(Document document, long j, MessageObject messageObject, boolean z, int i, Object obj) {
        String str;
        Document document2 = document;
        Bitmap[] bitmapArr = new Bitmap[1];
        String[] strArr = new String[1];
        String key = ImageLocation.getForDocument(document).getKey(null, null, false);
        if ("video/mp4".equals(document2.mime_type)) {
            str = ".mp4";
        } else {
            str = "video/x-matroska".equals(document2.mime_type) ? ".mkv" : "";
        }
        File directory = FileLoader.getDirectory(3);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(key);
        stringBuilder.append(str);
        File file = new File(directory, stringBuilder.toString());
        if (!file.exists()) {
            directory = FileLoader.getDirectory(2);
            stringBuilder = new StringBuilder();
            stringBuilder.append(key);
            stringBuilder.append(str);
            file = new File(directory, stringBuilder.toString());
        }
        ensureMediaThumbExists(false, document, file.getAbsolutePath(), null, 0);
        strArr[0] = getKeyForPhotoSize(FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 320), bitmapArr, true, true);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$ZAFd0mJxAtsRECtRTOH98EyJls4(this, bitmapArr, strArr, document, j, messageObject, z, i, obj));
    }

    public /* synthetic */ void lambda$null$5$SendMessagesHelper(Bitmap[] bitmapArr, String[] strArr, Document document, long j, MessageObject messageObject, boolean z, int i, Object obj) {
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        sendMessage((TL_document) document, null, null, j, messageObject, null, null, null, null, z, i, 0, obj);
    }

    /* JADX WARNING: Removed duplicated region for block: B:257:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0667  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x063a  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0696  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0693  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x06e2  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x049f  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x049d  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0517  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x052a  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x056b  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0534  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0427  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0431  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0452 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x049d  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x049f  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x04af A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x04ea  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0517  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x052a  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0534  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x056b  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x056f  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02eb  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x031c  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0427  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0431  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0452 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x049f  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x049d  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x04af A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x04ea  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0517  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x052a  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x056b  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0534  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x056f  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01f9  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x029d  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02eb  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x031c  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0427  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0431  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0452 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x049d  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x049f  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x04af A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x04ea  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0517  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x052a  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0534  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x056b  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x056f  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01c2  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x01b0  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01f9  */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x029d  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02eb  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02f8  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x031c  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0427  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0431  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0452 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x049f  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x049d  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x04af A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x04ea  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x0517  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x052c  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x052a  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x056b  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0534  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x056f  */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r49, long r50, boolean r52, int r53) {
        /*
        r48 = this;
        r14 = r48;
        r15 = r49;
        r12 = r50;
        r11 = r53;
        r10 = 0;
        if (r15 == 0) goto L_0x0797;
    L_0x000b:
        r0 = r49.isEmpty();
        if (r0 == 0) goto L_0x0013;
    L_0x0011:
        goto L_0x0797;
    L_0x0013:
        r0 = (int) r12;
        if (r0 == 0) goto L_0x0779;
    L_0x0016:
        r1 = r48.getMessagesController();
        r9 = r1.getPeer(r0);
        if (r0 <= 0) goto L_0x003d;
    L_0x0020:
        r1 = r48.getMessagesController();
        r2 = java.lang.Integer.valueOf(r0);
        r1 = r1.getUser(r2);
        if (r1 != 0) goto L_0x002f;
    L_0x002e:
        return r10;
    L_0x002f:
        r6 = 0;
        r16 = 1;
        r17 = 1;
        r18 = 1;
        r19 = 0;
        r20 = 1;
        r21 = 0;
        goto L_0x0074;
    L_0x003d:
        r1 = r48.getMessagesController();
        r2 = -r0;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getChat(r2);
        r2 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r2 == 0) goto L_0x0055;
    L_0x0050:
        r2 = r1.megagroup;
        r3 = r1.signatures;
        goto L_0x0057;
    L_0x0055:
        r2 = 0;
        r3 = 0;
    L_0x0057:
        r4 = org.telegram.messenger.ChatObject.canSendStickers(r1);
        r5 = org.telegram.messenger.ChatObject.canSendMedia(r1);
        r6 = org.telegram.messenger.ChatObject.canSendEmbed(r1);
        r16 = org.telegram.messenger.ChatObject.canSendPolls(r1);
        r19 = r2;
        r21 = r3;
        r17 = r5;
        r20 = r6;
        r18 = r16;
        r6 = r1;
        r16 = r4;
    L_0x0074:
        r5 = new android.util.LongSparseArray;
        r5.<init>();
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r22 = new android.util.LongSparseArray;
        r22.<init>();
        r10 = r48.getMessagesController();
        r10 = r10.getInputPeer(r0);
        r0 = r48.getUserConfig();
        r0 = r0.getClientUserId();
        r24 = r9;
        r8 = (long) r0;
        r26 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1));
        if (r26 != 0) goto L_0x00ac;
    L_0x00a9:
        r26 = 1;
        goto L_0x00ae;
    L_0x00ac:
        r26 = 0;
    L_0x00ae:
        r27 = r1;
        r28 = r2;
        r2 = r3;
        r3 = r4;
        r1 = r22;
        r4 = 0;
        r22 = 0;
    L_0x00b9:
        r7 = r49.size();
        if (r4 >= r7) goto L_0x0774;
    L_0x00bf:
        r7 = r15.get(r4);
        r7 = (org.telegram.messenger.MessageObject) r7;
        r30 = r7.getId();
        if (r30 <= 0) goto L_0x0702;
    L_0x00cb:
        r30 = r7.needDrawBluredPreview();
        if (r30 == 0) goto L_0x00d3;
    L_0x00d1:
        goto L_0x0702;
    L_0x00d3:
        if (r16 != 0) goto L_0x0130;
    L_0x00d5:
        r31 = r7.isSticker();
        if (r31 != 0) goto L_0x00ed;
    L_0x00db:
        r31 = r7.isAnimatedSticker();
        if (r31 != 0) goto L_0x00ed;
    L_0x00e1:
        r31 = r7.isGif();
        if (r31 != 0) goto L_0x00ed;
    L_0x00e7:
        r31 = r7.isGame();
        if (r31 == 0) goto L_0x0130;
    L_0x00ed:
        if (r22 != 0) goto L_0x0113;
    L_0x00ef:
        r7 = 8;
        r7 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r7);
        if (r7 == 0) goto L_0x00fa;
    L_0x00f7:
        r22 = 4;
        goto L_0x00fc;
    L_0x00fa:
        r22 = 1;
    L_0x00fc:
        r29 = r0;
        r15 = r4;
        r35 = r6;
        r30 = r8;
        r25 = r10;
        r33 = r24;
        r0 = r27;
        r37 = 0;
        r38 = 1;
        r39 = 0;
        r27 = r5;
        goto L_0x075a;
    L_0x0113:
        r29 = r0;
        r34 = r2;
        r36 = r3;
        r15 = r4;
        r35 = r6;
        r30 = r8;
        r25 = r10;
        r33 = r24;
        r40 = r27;
        r37 = 0;
        r38 = 1;
        r39 = 0;
        r24 = r1;
        r27 = r5;
        goto L_0x0752;
    L_0x0130:
        r31 = 2;
        if (r17 != 0) goto L_0x0185;
    L_0x0134:
        r14 = r7.messageOwner;
        r14 = r14.media;
        r33 = r10;
        r10 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r10 != 0) goto L_0x0142;
    L_0x013e:
        r10 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r10 == 0) goto L_0x0187;
    L_0x0142:
        if (r22 != 0) goto L_0x0168;
    L_0x0144:
        r7 = 7;
        r7 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r7);
        if (r7 == 0) goto L_0x014f;
    L_0x014b:
        r7 = 5;
        r22 = 5;
        goto L_0x0151;
    L_0x014f:
        r22 = 2;
    L_0x0151:
        r29 = r0;
        r15 = r4;
        r35 = r6;
        r30 = r8;
        r0 = r27;
        r25 = r33;
        r37 = 0;
        r38 = 1;
        r39 = 0;
        r27 = r5;
        r33 = r24;
        goto L_0x075a;
    L_0x0168:
        r29 = r0;
        r34 = r2;
        r36 = r3;
        r15 = r4;
        r35 = r6;
        r30 = r8;
        r40 = r27;
        r25 = r33;
        r37 = 0;
        r38 = 1;
        r39 = 0;
        r27 = r5;
        r33 = r24;
        r24 = r1;
        goto L_0x0752;
    L_0x0185:
        r33 = r10;
    L_0x0187:
        if (r18 != 0) goto L_0x01a3;
    L_0x0189:
        r10 = r7.messageOwner;
        r10 = r10.media;
        r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r10 == 0) goto L_0x01a3;
    L_0x0191:
        if (r22 != 0) goto L_0x0168;
    L_0x0193:
        r7 = 10;
        r7 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r6, r7);
        if (r7 == 0) goto L_0x019f;
    L_0x019b:
        r7 = 6;
        r22 = 6;
        goto L_0x0151;
    L_0x019f:
        r7 = 3;
        r22 = 3;
        goto L_0x0151;
    L_0x01a3:
        r10 = new org.telegram.tgnet.TLRPC$TL_message;
        r10.<init>();
        r34 = r7.getDialogId();
        r14 = (r34 > r8 ? 1 : (r34 == r8 ? 0 : -1));
        if (r14 != 0) goto L_0x01c2;
    L_0x01b0:
        r14 = r7.messageOwner;
        r14 = r14.from_id;
        r34 = r48.getUserConfig();
        r35 = r6;
        r6 = r34.getClientUserId();
        if (r14 != r6) goto L_0x01c4;
    L_0x01c0:
        r6 = 1;
        goto L_0x01c5;
    L_0x01c2:
        r35 = r6;
    L_0x01c4:
        r6 = 0;
    L_0x01c5:
        r14 = r7.isForwarded();
        if (r14 == 0) goto L_0x01f9;
    L_0x01cb:
        r6 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader;
        r6.<init>();
        r10.fwd_from = r6;
        r6 = r10.fwd_from;
        r14 = r7.messageOwner;
        r14 = r14.fwd_from;
        r11 = r14.flags;
        r6.flags = r11;
        r11 = r14.from_id;
        r6.from_id = r11;
        r11 = r14.date;
        r6.date = r11;
        r11 = r14.channel_id;
        r6.channel_id = r11;
        r11 = r14.channel_post;
        r6.channel_post = r11;
        r11 = r14.post_author;
        r6.post_author = r11;
        r11 = r14.from_name;
        r6.from_name = r11;
        r11 = 4;
        r10.flags = r11;
        goto L_0x0299;
    L_0x01f9:
        r11 = 4;
        if (r6 != 0) goto L_0x0299;
    L_0x01fc:
        r6 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader;
        r6.<init>();
        r10.fwd_from = r6;
        r6 = r10.fwd_from;
        r14 = r7.getId();
        r6.channel_post = r14;
        r6 = r10.fwd_from;
        r14 = r6.flags;
        r14 = r14 | r11;
        r6.flags = r14;
        r6 = r7.isFromUser();
        if (r6 == 0) goto L_0x0227;
    L_0x0218:
        r6 = r10.fwd_from;
        r11 = r7.messageOwner;
        r11 = r11.from_id;
        r6.from_id = r11;
        r11 = r6.flags;
        r14 = 1;
        r11 = r11 | r14;
        r6.flags = r11;
        goto L_0x0247;
    L_0x0227:
        r6 = r10.fwd_from;
        r11 = r7.messageOwner;
        r14 = r11.to_id;
        r14 = r14.channel_id;
        r6.channel_id = r14;
        r14 = r6.flags;
        r14 = r14 | 2;
        r6.flags = r14;
        r14 = r11.post;
        if (r14 == 0) goto L_0x0247;
    L_0x023b:
        r11 = r11.from_id;
        if (r11 <= 0) goto L_0x0247;
    L_0x023f:
        r6.from_id = r11;
        r11 = r6.flags;
        r14 = 1;
        r11 = r11 | r14;
        r6.flags = r11;
    L_0x0247:
        r6 = r7.messageOwner;
        r6 = r6.post_author;
        if (r6 == 0) goto L_0x0259;
    L_0x024d:
        r11 = r10.fwd_from;
        r11.post_author = r6;
        r6 = r11.flags;
        r14 = 8;
        r6 = r6 | r14;
        r11.flags = r6;
        goto L_0x0290;
    L_0x0259:
        r6 = r7.isOutOwner();
        if (r6 != 0) goto L_0x0290;
    L_0x025f:
        r6 = r7.messageOwner;
        r11 = r6.from_id;
        if (r11 <= 0) goto L_0x0290;
    L_0x0265:
        r6 = r6.post;
        if (r6 == 0) goto L_0x0290;
    L_0x0269:
        r6 = r48.getMessagesController();
        r11 = r7.messageOwner;
        r11 = r11.from_id;
        r11 = java.lang.Integer.valueOf(r11);
        r6 = r6.getUser(r11);
        if (r6 == 0) goto L_0x0290;
    L_0x027b:
        r11 = r10.fwd_from;
        r14 = r6.first_name;
        r6 = r6.last_name;
        r6 = org.telegram.messenger.ContactsController.formatName(r14, r6);
        r11.post_author = r6;
        r6 = r10.fwd_from;
        r11 = r6.flags;
        r14 = 8;
        r11 = r11 | r14;
        r6.flags = r11;
    L_0x0290:
        r6 = r7.messageOwner;
        r6 = r6.date;
        r10.date = r6;
        r6 = 4;
        r10.flags = r6;
    L_0x0299:
        r6 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1));
        if (r6 != 0) goto L_0x02c5;
    L_0x029d:
        r6 = r10.fwd_from;
        if (r6 == 0) goto L_0x02c5;
    L_0x02a1:
        r11 = r6.flags;
        r11 = r11 | 16;
        r6.flags = r11;
        r11 = r7.getId();
        r6.saved_from_msg_id = r11;
        r6 = r10.fwd_from;
        r11 = r7.messageOwner;
        r11 = r11.to_id;
        r6.saved_from_peer = r11;
        r6 = r6.saved_from_peer;
        r11 = r6.user_id;
        if (r11 != r0) goto L_0x02c5;
    L_0x02bb:
        r30 = r8;
        r8 = r7.getDialogId();
        r9 = (int) r8;
        r6.user_id = r9;
        goto L_0x02c7;
    L_0x02c5:
        r30 = r8;
    L_0x02c7:
        if (r20 != 0) goto L_0x02d9;
    L_0x02c9:
        r6 = r7.messageOwner;
        r6 = r6.media;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r6 == 0) goto L_0x02d9;
    L_0x02d1:
        r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r6.<init>();
        r10.media = r6;
        goto L_0x02df;
    L_0x02d9:
        r6 = r7.messageOwner;
        r6 = r6.media;
        r10.media = r6;
    L_0x02df:
        r6 = r10.media;
        if (r6 == 0) goto L_0x02e9;
    L_0x02e3:
        r6 = r10.flags;
        r6 = r6 | 512;
        r10.flags = r6;
    L_0x02e9:
        if (r19 == 0) goto L_0x02f2;
    L_0x02eb:
        r6 = r10.flags;
        r8 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r6 = r6 | r8;
        r10.flags = r6;
    L_0x02f2:
        r6 = r7.messageOwner;
        r6 = r6.via_bot_id;
        if (r6 == 0) goto L_0x0300;
    L_0x02f8:
        r10.via_bot_id = r6;
        r6 = r10.flags;
        r6 = r6 | 2048;
        r10.flags = r6;
    L_0x0300:
        r6 = r7.messageOwner;
        r6 = r6.message;
        r10.message = r6;
        r6 = r7.getId();
        r10.fwd_msg_id = r6;
        r6 = r7.messageOwner;
        r8 = r6.attachPath;
        r10.attachPath = r8;
        r8 = r6.entities;
        r10.entities = r8;
        r6 = r6.reply_markup;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
        if (r6 == 0) goto L_0x03c9;
    L_0x031c:
        r6 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
        r6.<init>();
        r10.reply_markup = r6;
        r6 = r7.messageOwner;
        r6 = r6.reply_markup;
        r6 = r6.rows;
        r6 = r6.size();
        r8 = 0;
        r9 = 0;
    L_0x032f:
        if (r8 >= r6) goto L_0x03b6;
    L_0x0331:
        r11 = r7.messageOwner;
        r11 = r11.reply_markup;
        r11 = r11.rows;
        r11 = r11.get(r8);
        r11 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r11;
        r14 = r11.buttons;
        r14 = r14.size();
        r32 = r0;
        r0 = 0;
        r34 = 0;
    L_0x0348:
        r36 = r6;
        if (r0 >= r14) goto L_0x03a9;
    L_0x034c:
        r6 = r11.buttons;
        r6 = r6.get(r0);
        r6 = (org.telegram.tgnet.TLRPC.KeyboardButton) r6;
        r37 = r9;
        r9 = r6 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth;
        r38 = r11;
        if (r9 != 0) goto L_0x0367;
    L_0x035c:
        r11 = r6 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
        if (r11 != 0) goto L_0x0367;
    L_0x0360:
        r11 = r6 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
        if (r11 == 0) goto L_0x0365;
    L_0x0364:
        goto L_0x0367;
    L_0x0365:
        r9 = 1;
        goto L_0x03ab;
    L_0x0367:
        if (r9 == 0) goto L_0x0388;
    L_0x0369:
        r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
        r9.<init>();
        r11 = r6.flags;
        r9.flags = r11;
        r11 = r6.fwd_text;
        if (r11 == 0) goto L_0x037b;
    L_0x0376:
        r9.fwd_text = r11;
        r9.text = r11;
        goto L_0x037f;
    L_0x037b:
        r11 = r6.text;
        r9.text = r11;
    L_0x037f:
        r11 = r6.url;
        r9.url = r11;
        r6 = r6.button_id;
        r9.button_id = r6;
        r6 = r9;
    L_0x0388:
        if (r34 != 0) goto L_0x0397;
    L_0x038a:
        r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow;
        r9.<init>();
        r11 = r10.reply_markup;
        r11 = r11.rows;
        r11.add(r9);
        goto L_0x0399;
    L_0x0397:
        r9 = r34;
    L_0x0399:
        r11 = r9.buttons;
        r11.add(r6);
        r0 = r0 + 1;
        r34 = r9;
        r6 = r36;
        r9 = r37;
        r11 = r38;
        goto L_0x0348;
    L_0x03a9:
        r37 = r9;
    L_0x03ab:
        if (r9 == 0) goto L_0x03ae;
    L_0x03ad:
        goto L_0x03b8;
    L_0x03ae:
        r8 = r8 + 1;
        r0 = r32;
        r6 = r36;
        goto L_0x032f;
    L_0x03b6:
        r32 = r0;
    L_0x03b8:
        r37 = r9;
        if (r37 != 0) goto L_0x03c3;
    L_0x03bc:
        r0 = r10.flags;
        r0 = r0 | 64;
        r10.flags = r0;
        goto L_0x03cb;
    L_0x03c3:
        r0 = r7.messageOwner;
        r8 = 0;
        r0.reply_markup = r8;
        goto L_0x03cc;
    L_0x03c9:
        r32 = r0;
    L_0x03cb:
        r8 = 0;
    L_0x03cc:
        r0 = r10.entities;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x03da;
    L_0x03d4:
        r0 = r10.flags;
        r0 = r0 | 128;
        r10.flags = r0;
    L_0x03da:
        r0 = r10.attachPath;
        if (r0 != 0) goto L_0x03e2;
    L_0x03de:
        r0 = "";
        r10.attachPath = r0;
    L_0x03e2:
        r0 = r48.getUserConfig();
        r0 = r0.getNewMessageId();
        r10.id = r0;
        r10.local_id = r0;
        r0 = 1;
        r10.out = r0;
        r0 = r7.messageOwner;
        r8 = r0.grouped_id;
        r36 = 0;
        r0 = (r8 > r36 ? 1 : (r8 == r36 ? 0 : -1));
        if (r0 == 0) goto L_0x0427;
    L_0x03fb:
        r0 = r5.get(r8);
        r0 = (java.lang.Long) r0;
        if (r0 != 0) goto L_0x0417;
    L_0x0403:
        r0 = org.telegram.messenger.Utilities.random;
        r38 = r0.nextLong();
        r0 = java.lang.Long.valueOf(r38);
        r6 = r7.messageOwner;
        r38 = r8;
        r8 = r6.grouped_id;
        r5.put(r8, r0);
        goto L_0x0419;
    L_0x0417:
        r38 = r8;
    L_0x0419:
        r8 = r0.longValue();
        r10.grouped_id = r8;
        r0 = r10.flags;
        r6 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r0 = r0 | r6;
        r10.flags = r0;
        goto L_0x0429;
    L_0x0427:
        r38 = r8;
    L_0x0429:
        r0 = r49.size();
        r6 = 1;
        r0 = r0 - r6;
        if (r4 == r0) goto L_0x044a;
    L_0x0431:
        r0 = r4 + 1;
        r0 = r15.get(r0);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.messageOwner;
        r8 = r0.grouped_id;
        r0 = r7.messageOwner;
        r11 = r5;
        r5 = r0.grouped_id;
        r0 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1));
        if (r0 == 0) goto L_0x044b;
    L_0x0446:
        r9 = r24;
        r0 = 1;
        goto L_0x044e;
    L_0x044a:
        r11 = r5;
    L_0x044b:
        r9 = r24;
        r0 = 0;
    L_0x044e:
        r5 = r9.channel_id;
        if (r5 == 0) goto L_0x0466;
    L_0x0452:
        if (r19 != 0) goto L_0x0466;
    L_0x0454:
        if (r21 == 0) goto L_0x045f;
    L_0x0456:
        r5 = r48.getUserConfig();
        r5 = r5.getClientUserId();
        goto L_0x0460;
    L_0x045f:
        r5 = -r5;
    L_0x0460:
        r10.from_id = r5;
        r5 = 1;
        r10.post = r5;
        goto L_0x0476;
    L_0x0466:
        r5 = r48.getUserConfig();
        r5 = r5.getClientUserId();
        r10.from_id = r5;
        r5 = r10.flags;
        r5 = r5 | 256;
        r10.flags = r5;
    L_0x0476:
        r5 = r10.random_id;
        r8 = (r5 > r36 ? 1 : (r5 == r36 ? 0 : -1));
        if (r8 != 0) goto L_0x0482;
    L_0x047c:
        r5 = r48.getNextRandomId();
        r10.random_id = r5;
    L_0x0482:
        r5 = r10.random_id;
        r5 = java.lang.Long.valueOf(r5);
        r2.add(r5);
        r5 = r10.random_id;
        r1.put(r5, r10);
        r5 = r10.fwd_msg_id;
        r5 = java.lang.Integer.valueOf(r5);
        r3.add(r5);
        r14 = r53;
        if (r14 == 0) goto L_0x049f;
    L_0x049d:
        r5 = r14;
        goto L_0x04a7;
    L_0x049f:
        r5 = r48.getConnectionsManager();
        r5 = r5.getCurrentTime();
    L_0x04a7:
        r10.date = r5;
        r8 = r33;
        r5 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r5 == 0) goto L_0x04bf;
    L_0x04af:
        if (r19 != 0) goto L_0x04bf;
    L_0x04b1:
        if (r14 != 0) goto L_0x04bc;
    L_0x04b3:
        r6 = 1;
        r10.views = r6;
        r6 = r10.flags;
        r6 = r6 | 1024;
        r10.flags = r6;
    L_0x04bc:
        r24 = r1;
        goto L_0x04d8;
    L_0x04bf:
        r6 = r7.messageOwner;
        r24 = r1;
        r1 = r6.flags;
        r1 = r1 & 1024;
        if (r1 == 0) goto L_0x04d5;
    L_0x04c9:
        if (r14 != 0) goto L_0x04d5;
    L_0x04cb:
        r1 = r6.views;
        r10.views = r1;
        r1 = r10.flags;
        r1 = r1 | 1024;
        r10.flags = r1;
    L_0x04d5:
        r1 = 1;
        r10.unread = r1;
    L_0x04d8:
        r10.dialog_id = r12;
        r10.to_id = r9;
        r1 = org.telegram.messenger.MessageObject.isVoiceMessage(r10);
        if (r1 != 0) goto L_0x04e8;
    L_0x04e2:
        r1 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r10);
        if (r1 == 0) goto L_0x04fa;
    L_0x04e8:
        if (r5 == 0) goto L_0x04f7;
    L_0x04ea:
        r1 = r7.getChannelId();
        if (r1 == 0) goto L_0x04f7;
    L_0x04f0:
        r1 = r7.isContentUnread();
        r10.media_unread = r1;
        goto L_0x04fa;
    L_0x04f7:
        r1 = 1;
        r10.media_unread = r1;
    L_0x04fa:
        r1 = r7.messageOwner;
        r1 = r1.to_id;
        r5 = r1 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;
        if (r5 == 0) goto L_0x0507;
    L_0x0502:
        r1 = r1.channel_id;
        r1 = -r1;
        r10.ttl = r1;
    L_0x0507:
        r1 = new org.telegram.messenger.MessageObject;
        r6 = r48;
        r5 = r6.currentAccount;
        r33 = r9;
        r9 = 1;
        r1.<init>(r5, r10, r9);
        if (r14 == 0) goto L_0x0517;
    L_0x0515:
        r5 = 1;
        goto L_0x0518;
    L_0x0517:
        r5 = 0;
    L_0x0518:
        r1.scheduled = r5;
        r5 = r1.messageOwner;
        r5.send_state = r9;
        r9 = r27;
        r9.add(r1);
        r5 = r28;
        r5.add(r10);
        if (r14 == 0) goto L_0x052c;
    L_0x052a:
        r1 = 1;
        goto L_0x052d;
    L_0x052c:
        r1 = 0;
    L_0x052d:
        r6.putToSendingMessages(r10, r1);
        r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r1 == 0) goto L_0x056b;
    L_0x0534:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r10 = "forward message user_id = ";
        r1.append(r10);
        r10 = r8.user_id;
        r1.append(r10);
        r10 = " chat_id = ";
        r1.append(r10);
        r10 = r8.chat_id;
        r1.append(r10);
        r10 = " channel_id = ";
        r1.append(r10);
        r10 = r8.channel_id;
        r1.append(r10);
        r10 = " access_hash = ";
        r1.append(r10);
        r27 = r11;
        r10 = r8.access_hash;
        r1.append(r10);
        r1 = r1.toString();
        org.telegram.messenger.FileLog.d(r1);
        goto L_0x056d;
    L_0x056b:
        r27 = r11;
    L_0x056d:
        if (r0 == 0) goto L_0x0575;
    L_0x056f:
        r0 = r5.size();
        if (r0 > 0) goto L_0x05b6;
    L_0x0575:
        r0 = r5.size();
        r1 = 100;
        if (r0 == r1) goto L_0x05b6;
    L_0x057d:
        r0 = r49.size();
        r1 = 1;
        r0 = r0 - r1;
        if (r4 == r0) goto L_0x05b6;
    L_0x0585:
        r0 = r49.size();
        r0 = r0 - r1;
        if (r4 == r0) goto L_0x05a1;
    L_0x058c:
        r0 = r4 + 1;
        r0 = r15.get(r0);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.getDialogId();
        r10 = r7.getDialogId();
        r28 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r28 == 0) goto L_0x05a1;
    L_0x05a0:
        goto L_0x05b6;
    L_0x05a1:
        r34 = r2;
        r36 = r3;
        r15 = r4;
        r28 = r5;
        r25 = r8;
        r40 = r9;
        r29 = r32;
        r37 = 0;
        r38 = 1;
        r39 = 0;
        goto L_0x0752;
    L_0x05b6:
        r40 = r48.getMessagesStorage();
        r0 = new java.util.ArrayList;
        r0.<init>(r5);
        r42 = 0;
        r43 = 1;
        r44 = 0;
        r45 = 0;
        if (r14 == 0) goto L_0x05cc;
    L_0x05c9:
        r46 = 1;
        goto L_0x05ce;
    L_0x05cc:
        r46 = 0;
    L_0x05ce:
        r41 = r0;
        r40.putMessages(r41, r42, r43, r44, r45, r46);
        r0 = r48.getMessagesController();
        if (r14 == 0) goto L_0x05db;
    L_0x05d9:
        r1 = 1;
        goto L_0x05dc;
    L_0x05db:
        r1 = 0;
    L_0x05dc:
        r0.updateInterfaceWithMessages(r12, r9, r1);
        r0 = r48.getNotificationCenter();
        r1 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r10 = 0;
        r11 = new java.lang.Object[r10];
        r0.postNotificationName(r1, r11);
        r0 = r48.getUserConfig();
        r0.saveConfig(r10);
        r11 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;
        r11.<init>();
        r11.to_peer = r8;
        r0 = (r38 > r36 ? 1 : (r38 == r36 ? 0 : -1));
        if (r0 == 0) goto L_0x05ff;
    L_0x05fd:
        r0 = 1;
        goto L_0x0600;
    L_0x05ff:
        r0 = 0;
    L_0x0600:
        r11.grouped = r0;
        if (r52 == 0) goto L_0x0625;
    L_0x0604:
        r0 = r6.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r10 = "silent_";
        r1.append(r10);
        r1.append(r12);
        r1 = r1.toString();
        r10 = 0;
        r0 = r0.getBoolean(r1, r10);
        if (r0 == 0) goto L_0x0623;
    L_0x0622:
        goto L_0x0625;
    L_0x0623:
        r0 = 0;
        goto L_0x0626;
    L_0x0625:
        r0 = 1;
    L_0x0626:
        r11.silent = r0;
        if (r14 == 0) goto L_0x0632;
    L_0x062a:
        r11.schedule_date = r14;
        r0 = r11.flags;
        r0 = r0 | 1024;
        r11.flags = r0;
    L_0x0632:
        r0 = r7.messageOwner;
        r0 = r0.to_id;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;
        if (r0 == 0) goto L_0x0667;
    L_0x063a:
        r0 = r48.getMessagesController();
        r1 = r7.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
        r1.<init>();
        r11.from_peer = r1;
        r1 = r11.from_peer;
        r10 = r7.messageOwner;
        r10 = r10.to_id;
        r10 = r10.channel_id;
        r1.channel_id = r10;
        r10 = r4;
        r28 = r5;
        if (r0 == 0) goto L_0x0671;
    L_0x0662:
        r4 = r0.access_hash;
        r1.access_hash = r4;
        goto L_0x0671;
    L_0x0667:
        r10 = r4;
        r28 = r5;
        r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r0.<init>();
        r11.from_peer = r0;
    L_0x0671:
        r11.random_id = r2;
        r11.id = r3;
        r0 = r49.size();
        r5 = 1;
        r4 = 0;
        if (r0 != r5) goto L_0x068b;
    L_0x067d:
        r0 = r15.get(r4);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.messageOwner;
        r0 = r0.with_my_score;
        if (r0 == 0) goto L_0x068b;
    L_0x0689:
        r0 = 1;
        goto L_0x068c;
    L_0x068b:
        r0 = 0;
    L_0x068c:
        r11.with_my_score = r0;
        r0 = NUM; // 0x7ffffffe float:NaN double:1.0609978945E-314;
        if (r14 != r0) goto L_0x0696;
    L_0x0693:
        r23 = 1;
        goto L_0x0698;
    L_0x0696:
        r23 = 0;
    L_0x0698:
        r1 = r48.getConnectionsManager();
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$flpU3A3e0tPwUqp389LcbHlF_dc;
        r29 = r32;
        r32 = r0;
        r14 = r1;
        r1 = r48;
        r34 = r2;
        r36 = r3;
        r2 = r50;
        r37 = 0;
        r4 = r53;
        r38 = 1;
        r5 = r19;
        r6 = r23;
        r23 = r7;
        r7 = r26;
        r25 = r8;
        r39 = 0;
        r8 = r24;
        r40 = r9;
        r9 = r28;
        r15 = r10;
        r10 = r40;
        r41 = r11;
        r11 = r23;
        r12 = r33;
        r13 = r41;
        r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        r0 = 68;
        r2 = r32;
        r1 = r41;
        r14.sendRequest(r1, r2, r0);
        r0 = r49.size();
        r0 = r0 + -1;
        if (r15 == r0) goto L_0x0752;
    L_0x06e2:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r28 = new java.util.ArrayList;
        r28.<init>();
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new android.util.LongSparseArray;
        r3.<init>();
        r47 = r2;
        r2 = r1;
        r1 = r3;
        r3 = r47;
        goto L_0x075a;
    L_0x0702:
        r29 = r0;
        r34 = r2;
        r36 = r3;
        r15 = r4;
        r35 = r6;
        r30 = r8;
        r25 = r10;
        r33 = r24;
        r40 = r27;
        r37 = 0;
        r38 = 1;
        r39 = 0;
        r24 = r1;
        r27 = r5;
        r0 = r7.type;
        if (r0 != 0) goto L_0x0752;
    L_0x0721:
        r0 = r7.messageText;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0752;
    L_0x0729:
        r0 = r7.messageOwner;
        r0 = r0.media;
        if (r0 == 0) goto L_0x0733;
    L_0x072f:
        r0 = r0.webpage;
        r5 = r0;
        goto L_0x0735;
    L_0x0733:
        r5 = r39;
    L_0x0735:
        r0 = r7.messageText;
        r1 = r0.toString();
        r4 = 0;
        if (r5 == 0) goto L_0x0740;
    L_0x073e:
        r6 = 1;
        goto L_0x0741;
    L_0x0740:
        r6 = 0;
    L_0x0741:
        r0 = r7.messageOwner;
        r7 = r0.entities;
        r8 = 0;
        r9 = 0;
        r0 = r48;
        r2 = r50;
        r10 = r52;
        r11 = r53;
        r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x0752:
        r1 = r24;
        r2 = r34;
        r3 = r36;
        r0 = r40;
    L_0x075a:
        r4 = r15 + 1;
        r14 = r48;
        r15 = r49;
        r12 = r50;
        r11 = r53;
        r10 = r25;
        r5 = r27;
        r8 = r30;
        r24 = r33;
        r6 = r35;
        r27 = r0;
        r0 = r29;
        goto L_0x00b9;
    L_0x0774:
        r3 = r48;
        r37 = r22;
        goto L_0x0796;
    L_0x0779:
        r37 = 0;
        r0 = 0;
    L_0x077c:
        r1 = r49.size();
        if (r0 >= r1) goto L_0x0794;
    L_0x0782:
        r1 = r49;
        r2 = r1.get(r0);
        r2 = (org.telegram.messenger.MessageObject) r2;
        r3 = r48;
        r4 = r50;
        r3.processForwardFromMyName(r2, r4);
        r0 = r0 + 1;
        goto L_0x077c;
    L_0x0794:
        r3 = r48;
    L_0x0796:
        return r37;
    L_0x0797:
        r3 = r14;
        r37 = 0;
        return r37;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, int):int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f1  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0115  */
    public /* synthetic */ void lambda$sendMessage$14$SendMessagesHelper(long r26, int r28, boolean r29, boolean r30, boolean r31, android.util.LongSparseArray r32, java.util.ArrayList r33, java.util.ArrayList r34, org.telegram.messenger.MessageObject r35, org.telegram.tgnet.TLRPC.Peer r36, org.telegram.tgnet.TLRPC.TL_messages_forwardMessages r37, org.telegram.tgnet.TLObject r38, org.telegram.tgnet.TLRPC.TL_error r39) {
        /*
        r25 = this;
        r11 = r25;
        r12 = r28;
        r13 = r33;
        r14 = r34;
        r0 = r39;
        r15 = 1;
        if (r0 != 0) goto L_0x01e1;
    L_0x000d:
        r9 = new org.telegram.messenger.support.SparseLongArray;
        r9.<init>();
        r7 = r38;
        r7 = (org.telegram.tgnet.TLRPC.Updates) r7;
        r0 = 0;
    L_0x0017:
        r1 = r7.updates;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x003d;
    L_0x001f:
        r1 = r7.updates;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Update) r1;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateMessageID;
        if (r2 == 0) goto L_0x003b;
    L_0x002b:
        r1 = (org.telegram.tgnet.TLRPC.TL_updateMessageID) r1;
        r2 = r1.id;
        r3 = r1.random_id;
        r9.put(r2, r3);
        r1 = r7.updates;
        r1.remove(r0);
        r0 = r0 + -1;
    L_0x003b:
        r0 = r0 + r15;
        goto L_0x0017;
    L_0x003d:
        r0 = r25.getMessagesController();
        r0 = r0.dialogs_read_outbox_max;
        r1 = java.lang.Long.valueOf(r26);
        r0 = r0.get(r1);
        r0 = (java.lang.Integer) r0;
        if (r0 != 0) goto L_0x006b;
    L_0x004f:
        r0 = r25.getMessagesStorage();
        r5 = r26;
        r0 = r0.getDialogReadMax(r15, r5);
        r0 = java.lang.Integer.valueOf(r0);
        r1 = r25.getMessagesController();
        r1 = r1.dialogs_read_outbox_max;
        r2 = java.lang.Long.valueOf(r26);
        r1.put(r2, r0);
        goto L_0x006d;
    L_0x006b:
        r5 = r26;
    L_0x006d:
        r16 = r0;
        r0 = 0;
        r8 = 0;
    L_0x0071:
        r1 = r7.updates;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x01c3;
    L_0x0079:
        r1 = r7.updates;
        r1 = r1.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.Update) r1;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewMessage;
        if (r2 != 0) goto L_0x0097;
    L_0x0085:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r3 != 0) goto L_0x0097;
    L_0x0089:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage;
        if (r3 == 0) goto L_0x008e;
    L_0x008d:
        goto L_0x0097;
    L_0x008e:
        r17 = r0;
        r14 = r7;
        r19 = r9;
        r1 = 1;
        r13 = 0;
        goto L_0x01b3;
    L_0x0097:
        if (r12 == 0) goto L_0x009b;
    L_0x0099:
        r3 = 1;
        goto L_0x009c;
    L_0x009b:
        r3 = 0;
    L_0x009c:
        r4 = r7.updates;
        r4.remove(r0);
        r17 = r0 + -1;
        r0 = -1;
        if (r2 == 0) goto L_0x00b7;
    L_0x00a6:
        r1 = (org.telegram.tgnet.TLRPC.TL_updateNewMessage) r1;
        r2 = r1.message;
        r4 = r25.getMessagesController();
        r10 = r1.pts;
        r1 = r1.pts_count;
        r4.processNewDifferenceParams(r0, r10, r0, r1);
    L_0x00b5:
        r10 = r2;
        goto L_0x00de;
    L_0x00b7:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage;
        if (r2 == 0) goto L_0x00c1;
    L_0x00bb:
        r1 = (org.telegram.tgnet.TLRPC.TL_updateNewScheduledMessage) r1;
        r1 = r1.message;
        r10 = r1;
        goto L_0x00de;
    L_0x00c1:
        r1 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r1;
        r2 = r1.message;
        r4 = r25.getMessagesController();
        r10 = r1.pts;
        r1 = r1.pts_count;
        r0 = r2.to_id;
        r0 = r0.channel_id;
        r4.processNewChannelDifferenceParams(r10, r1, r0);
        if (r29 == 0) goto L_0x00b5;
    L_0x00d6:
        r0 = r2.flags;
        r1 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r0 = r0 | r1;
        r2.flags = r0;
        goto L_0x00b5;
    L_0x00de:
        if (r30 == 0) goto L_0x00ea;
    L_0x00e0:
        r0 = r10.date;
        r1 = NUM; // 0x7ffffffe float:NaN double:1.0609978945E-314;
        if (r0 == r1) goto L_0x00ea;
    L_0x00e7:
        r19 = 0;
        goto L_0x00ec;
    L_0x00ea:
        r19 = r3;
    L_0x00ec:
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r10);
        if (r19 != 0) goto L_0x00fe;
    L_0x00f1:
        r0 = r16.intValue();
        r1 = r10.id;
        if (r0 >= r1) goto L_0x00fb;
    L_0x00f9:
        r0 = 1;
        goto L_0x00fc;
    L_0x00fb:
        r0 = 0;
    L_0x00fc:
        r10.unread = r0;
    L_0x00fe:
        if (r31 == 0) goto L_0x0108;
    L_0x0100:
        r10.out = r15;
        r4 = 0;
        r10.unread = r4;
        r10.media_unread = r4;
        goto L_0x0109;
    L_0x0108:
        r4 = 0;
    L_0x0109:
        r0 = r10.id;
        r0 = r9.get(r0);
        r2 = 0;
        r18 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r18 == 0) goto L_0x01ae;
    L_0x0115:
        r3 = r32;
        r0 = r3.get(r0);
        r2 = r0;
        r2 = (org.telegram.tgnet.TLRPC.Message) r2;
        if (r2 != 0) goto L_0x0122;
    L_0x0120:
        goto L_0x01ae;
    L_0x0122:
        r0 = r13.indexOf(r2);
        r1 = -1;
        if (r0 != r1) goto L_0x012b;
    L_0x0129:
        goto L_0x01ae;
    L_0x012b:
        r1 = r14.get(r0);
        r18 = r1;
        r18 = (org.telegram.messenger.MessageObject) r18;
        r13.remove(r0);
        r14.remove(r0);
        r1 = r2.id;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r0.add(r10);
        r4 = r10.id;
        r20 = 0;
        r21 = 1;
        r22 = r0;
        r0 = r25;
        r23 = r1;
        r1 = r18;
        r15 = r2;
        r2 = r10;
        r3 = r4;
        r24 = 0;
        r4 = r20;
        r5 = r21;
        r0.updateMediaPaths(r1, r2, r3, r4, r5);
        r18 = r18.getMediaExistanceFlags();
        r0 = r10.id;
        r15.id = r0;
        r20 = r8 + 1;
        if (r12 == 0) goto L_0x0184;
    L_0x0169:
        if (r19 != 0) goto L_0x0184;
    L_0x016b:
        r8 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$_gHTxqQKXqvVN-u6Eg96RvX1uR8;
        r0 = r8;
        r1 = r25;
        r2 = r23;
        r3 = r15;
        r4 = r22;
        r5 = r35;
        r6 = r28;
        r0.<init>(r1, r2, r3, r4, r5, r6);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r8);
        r14 = r7;
        r19 = r9;
        r13 = 0;
        goto L_0x01ab;
    L_0x0184:
        r0 = r25.getMessagesStorage();
        r8 = r0.getStorageQueue();
        r6 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$ZCbfkTtQsDOKM8NiFiE3mHK3dIo;
        r0 = r6;
        r1 = r25;
        r2 = r15;
        r3 = r23;
        r4 = r36;
        r5 = r28;
        r15 = r6;
        r6 = r22;
        r14 = r7;
        r12 = r8;
        r7 = r26;
        r19 = r9;
        r9 = r10;
        r13 = 0;
        r10 = r18;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10);
        r12.postRunnable(r15);
    L_0x01ab:
        r8 = r20;
        goto L_0x01b2;
    L_0x01ae:
        r14 = r7;
        r19 = r9;
        r13 = 0;
    L_0x01b2:
        r1 = 1;
    L_0x01b3:
        r0 = r17 + 1;
        r5 = r26;
        r12 = r28;
        r13 = r33;
        r7 = r14;
        r9 = r19;
        r15 = 1;
        r14 = r34;
        goto L_0x0071;
    L_0x01c3:
        r14 = r7;
        r1 = 1;
        r13 = 0;
        r0 = r14.updates;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x01d5;
    L_0x01ce:
        r0 = r25.getMessagesController();
        r0.processUpdates(r14, r13);
    L_0x01d5:
        r0 = r25.getStatsController();
        r2 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType();
        r0.incrementSentItemsCount(r2, r1, r8);
        goto L_0x01ed;
    L_0x01e1:
        r1 = 1;
        r13 = 0;
        r2 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Np5Q7xHh1zw91IDVJqdT9IWuXcI;
        r3 = r37;
        r2.<init>(r11, r0, r3);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2);
    L_0x01ed:
        r0 = 0;
    L_0x01ee:
        r2 = r33.size();
        if (r0 >= r2) goto L_0x0215;
    L_0x01f4:
        r2 = r33;
        r3 = r2.get(r0);
        r3 = (org.telegram.tgnet.TLRPC.Message) r3;
        r4 = r25.getMessagesStorage();
        r5 = r28;
        if (r5 == 0) goto L_0x0206;
    L_0x0204:
        r6 = 1;
        goto L_0x0207;
    L_0x0206:
        r6 = 0;
    L_0x0207:
        r4.markMessageAsSendError(r3, r6);
        r4 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$9-4ORe2MVG9HpTDZt06-uF5tbLE;
        r4.<init>(r11, r3, r5);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
        r0 = r0 + 1;
        goto L_0x01ee;
    L_0x0215:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendMessage$14$SendMessagesHelper(long, int, boolean, boolean, boolean, android.util.LongSparseArray, java.util.ArrayList, java.util.ArrayList, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Peer, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    public /* synthetic */ void lambda$null$9$SendMessagesHelper(int i, Message message, ArrayList arrayList, MessageObject messageObject, int i2) {
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(Integer.valueOf(i));
        getMessagesController().deleteMessages(arrayList2, null, null, message.dialog_id, message.to_id.channel_id, false, true);
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$Ct13ZeHRLdoGiQ7h7J3beb7EfP8(this, arrayList, messageObject, message, i, i2));
    }

    public /* synthetic */ void lambda$null$8$SendMessagesHelper(ArrayList arrayList, MessageObject messageObject, Message message, int i, int i2) {
        getMessagesStorage().putMessages(arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$l_vPSY0TrX1eOG0IYDS4JPSnWJA(this, messageObject, message, i, i2));
    }

    public /* synthetic */ void lambda$null$7$SendMessagesHelper(MessageObject messageObject, Message message, int i, int i2) {
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

    public /* synthetic */ void lambda$null$11$SendMessagesHelper(Message message, int i, Peer peer, int i2, ArrayList arrayList, long j, Message message2, int i3) {
        Message message3 = message;
        getMessagesStorage().updateMessageStateAndId(message3.random_id, Integer.valueOf(i), message3.id, 0, false, peer.channel_id, i2 != 0 ? 1 : 0);
        getMessagesStorage().putMessages(arrayList, true, false, false, 0, i2 != 0);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$cyCIasAPwsmtUIgEWJDqZK-t1BI(this, message, j, i, message2, i3, i2));
    }

    public /* synthetic */ void lambda$null$10$SendMessagesHelper(Message message, long j, int i, Message message2, int i2, int i3) {
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

    public /* synthetic */ void lambda$null$12$SendMessagesHelper(TL_error tL_error, TL_messages_forwardMessages tL_messages_forwardMessages) {
        AlertsCreator.processError(this.currentAccount, tL_error, null, tL_messages_forwardMessages, new Object[0]);
    }

    public /* synthetic */ void lambda$null$13$SendMessagesHelper(Message message, int i) {
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

    /* JADX WARNING: Removed duplicated region for block: B:58:0x0137 A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008e A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0041 A:{SYNTHETIC, Splitter:B:16:0x0041} */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0154 A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01ff A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x017e A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x02db A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0226 A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0403 A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0415 A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x045e A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x0479 A:{Catch:{ Exception -> 0x0508 }} */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0463 A:{Catch:{ Exception -> 0x0508 }} */
    private void editMessageMedia(org.telegram.messenger.MessageObject r28, org.telegram.tgnet.TLRPC.TL_photo r29, org.telegram.messenger.VideoEditedInfo r30, org.telegram.tgnet.TLRPC.TL_document r31, java.lang.String r32, java.util.HashMap<java.lang.String, java.lang.String> r33, boolean r34, java.lang.Object r35) {
        /*
        r27 = this;
        r10 = r27;
        r11 = r28;
        r0 = r29;
        r1 = r31;
        r2 = r32;
        r3 = "originalPath";
        r4 = "parentObject";
        if (r11 != 0) goto L_0x0011;
    L_0x0010:
        return;
    L_0x0011:
        r5 = r11.messageOwner;
        r6 = 0;
        r11.cancelEditing = r6;
        r7 = r28.getDialogId();	 Catch:{ Exception -> 0x0508 }
        r9 = (int) r7;	 Catch:{ Exception -> 0x0508 }
        if (r9 != 0) goto L_0x003c;
    L_0x001d:
        r13 = 32;
        r13 = r7 >> r13;
        r14 = (int) r13;	 Catch:{ Exception -> 0x0508 }
        r13 = r27.getMessagesController();	 Catch:{ Exception -> 0x0508 }
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0508 }
        r13 = r13.getEncryptedChat(r14);	 Catch:{ Exception -> 0x0508 }
        if (r13 == 0) goto L_0x003a;
    L_0x0030:
        r13 = r13.layer;	 Catch:{ Exception -> 0x0508 }
        r13 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r13);	 Catch:{ Exception -> 0x0508 }
        r14 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r13 >= r14) goto L_0x003c;
    L_0x003a:
        r13 = 0;
        goto L_0x003d;
    L_0x003c:
        r13 = 1;
    L_0x003d:
        r14 = "http";
        if (r34 == 0) goto L_0x008e;
    L_0x0041:
        r2 = r11.messageOwner;	 Catch:{ Exception -> 0x0508 }
        r2 = r2.media;	 Catch:{ Exception -> 0x0508 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0508 }
        if (r2 == 0) goto L_0x0055;
    L_0x0049:
        r0 = r11.messageOwner;	 Catch:{ Exception -> 0x0508 }
        r0 = r0.media;	 Catch:{ Exception -> 0x0508 }
        r0 = r0.photo;	 Catch:{ Exception -> 0x0508 }
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;	 Catch:{ Exception -> 0x0508 }
        r15 = r30;
        r2 = 2;
        goto L_0x006b;
    L_0x0055:
        r1 = r11.messageOwner;	 Catch:{ Exception -> 0x0508 }
        r1 = r1.media;	 Catch:{ Exception -> 0x0508 }
        r1 = r1.document;	 Catch:{ Exception -> 0x0508 }
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;	 Catch:{ Exception -> 0x0508 }
        r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1);	 Catch:{ Exception -> 0x0508 }
        if (r2 != 0) goto L_0x0068;
    L_0x0063:
        if (r30 == 0) goto L_0x0066;
    L_0x0065:
        goto L_0x0068;
    L_0x0066:
        r2 = 7;
        goto L_0x0069;
    L_0x0068:
        r2 = 3;
    L_0x0069:
        r15 = r11.videoEditedInfo;	 Catch:{ Exception -> 0x0508 }
    L_0x006b:
        r6 = r5.params;	 Catch:{ Exception -> 0x0508 }
        if (r35 != 0) goto L_0x007c;
    L_0x006f:
        if (r6 == 0) goto L_0x007c;
    L_0x0071:
        r18 = r6.containsKey(r4);	 Catch:{ Exception -> 0x0508 }
        if (r18 == 0) goto L_0x007c;
    L_0x0077:
        r4 = r6.get(r4);	 Catch:{ Exception -> 0x0508 }
        goto L_0x007e;
    L_0x007c:
        r4 = r35;
    L_0x007e:
        r12 = r5.message;	 Catch:{ Exception -> 0x0508 }
        r11.editingMessage = r12;	 Catch:{ Exception -> 0x0508 }
        r12 = r5.entities;	 Catch:{ Exception -> 0x0508 }
        r11.editingMessageEntities = r12;	 Catch:{ Exception -> 0x0508 }
        r12 = r5.attachPath;	 Catch:{ Exception -> 0x0508 }
        r19 = r9;
        r9 = r4;
        r4 = r6;
        goto L_0x0150;
    L_0x008e:
        r4 = r5.media;	 Catch:{ Exception -> 0x0508 }
        r11.previousMedia = r4;	 Catch:{ Exception -> 0x0508 }
        r4 = r5.message;	 Catch:{ Exception -> 0x0508 }
        r11.previousCaption = r4;	 Catch:{ Exception -> 0x0508 }
        r4 = r5.entities;	 Catch:{ Exception -> 0x0508 }
        r11.previousCaptionEntities = r4;	 Catch:{ Exception -> 0x0508 }
        r4 = r5.attachPath;	 Catch:{ Exception -> 0x0508 }
        r11.previousAttachPath = r4;	 Catch:{ Exception -> 0x0508 }
        r4 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0508 }
        r6 = 1;
        r4.<init>(r6);	 Catch:{ Exception -> 0x0508 }
        r10.writePreviousMessageData(r5, r4);	 Catch:{ Exception -> 0x0508 }
        r6 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0508 }
        r4 = r4.length();	 Catch:{ Exception -> 0x0508 }
        r6.<init>(r4);	 Catch:{ Exception -> 0x0508 }
        r10.writePreviousMessageData(r5, r6);	 Catch:{ Exception -> 0x0508 }
        if (r33 != 0) goto L_0x00bb;
    L_0x00b5:
        r4 = new java.util.HashMap;	 Catch:{ Exception -> 0x0508 }
        r4.<init>();	 Catch:{ Exception -> 0x0508 }
        goto L_0x00bd;
    L_0x00bb:
        r4 = r33;
    L_0x00bd:
        r12 = "prevMedia";
        r15 = r6.toByteArray();	 Catch:{ Exception -> 0x0508 }
        r19 = r9;
        r9 = 0;
        r15 = android.util.Base64.encodeToString(r15, r9);	 Catch:{ Exception -> 0x0508 }
        r4.put(r12, r15);	 Catch:{ Exception -> 0x0508 }
        r6.cleanup();	 Catch:{ Exception -> 0x0508 }
        if (r0 == 0) goto L_0x0114;
    L_0x00d2:
        r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0508 }
        r6.<init>();	 Catch:{ Exception -> 0x0508 }
        r5.media = r6;	 Catch:{ Exception -> 0x0508 }
        r6 = r5.media;	 Catch:{ Exception -> 0x0508 }
        r9 = r6.flags;	 Catch:{ Exception -> 0x0508 }
        r12 = 3;
        r9 = r9 | r12;
        r6.flags = r9;	 Catch:{ Exception -> 0x0508 }
        r6 = r5.media;	 Catch:{ Exception -> 0x0508 }
        r6.photo = r0;	 Catch:{ Exception -> 0x0508 }
        if (r2 == 0) goto L_0x00f6;
    L_0x00e7:
        r6 = r32.length();	 Catch:{ Exception -> 0x0508 }
        if (r6 <= 0) goto L_0x00f6;
    L_0x00ed:
        r6 = r2.startsWith(r14);	 Catch:{ Exception -> 0x0508 }
        if (r6 == 0) goto L_0x00f6;
    L_0x00f3:
        r5.attachPath = r2;	 Catch:{ Exception -> 0x0508 }
        goto L_0x0112;
    L_0x00f6:
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0508 }
        r9 = r0.sizes;	 Catch:{ Exception -> 0x0508 }
        r9 = r9.size();	 Catch:{ Exception -> 0x0508 }
        r12 = 1;
        r9 = r9 - r12;
        r6 = r6.get(r9);	 Catch:{ Exception -> 0x0508 }
        r6 = (org.telegram.tgnet.TLRPC.PhotoSize) r6;	 Catch:{ Exception -> 0x0508 }
        r6 = r6.location;	 Catch:{ Exception -> 0x0508 }
        r6 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r12);	 Catch:{ Exception -> 0x0508 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0508 }
        r5.attachPath = r6;	 Catch:{ Exception -> 0x0508 }
    L_0x0112:
        r15 = 2;
        goto L_0x0145;
    L_0x0114:
        if (r1 == 0) goto L_0x0144;
    L_0x0116:
        r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0508 }
        r6.<init>();	 Catch:{ Exception -> 0x0508 }
        r5.media = r6;	 Catch:{ Exception -> 0x0508 }
        r6 = r5.media;	 Catch:{ Exception -> 0x0508 }
        r9 = r6.flags;	 Catch:{ Exception -> 0x0508 }
        r12 = 3;
        r9 = r9 | r12;
        r6.flags = r9;	 Catch:{ Exception -> 0x0508 }
        r6 = r5.media;	 Catch:{ Exception -> 0x0508 }
        r6.document = r1;	 Catch:{ Exception -> 0x0508 }
        r6 = org.telegram.messenger.MessageObject.isVideoDocument(r31);	 Catch:{ Exception -> 0x0508 }
        if (r6 != 0) goto L_0x0134;
    L_0x012f:
        if (r30 == 0) goto L_0x0132;
    L_0x0131:
        goto L_0x0134;
    L_0x0132:
        r15 = 7;
        goto L_0x0135;
    L_0x0134:
        r15 = 3;
    L_0x0135:
        if (r30 == 0) goto L_0x0141;
    L_0x0137:
        r6 = r30.getString();	 Catch:{ Exception -> 0x0508 }
        r9 = "ve";
        r4.put(r9, r6);	 Catch:{ Exception -> 0x0508 }
    L_0x0141:
        r5.attachPath = r2;	 Catch:{ Exception -> 0x0508 }
        goto L_0x0145;
    L_0x0144:
        r15 = -1;
    L_0x0145:
        r5.params = r4;	 Catch:{ Exception -> 0x0508 }
        r6 = 3;
        r5.send_state = r6;	 Catch:{ Exception -> 0x0508 }
        r9 = r35;
        r12 = r2;
        r2 = r15;
        r15 = r30;
    L_0x0150:
        r6 = r5.attachPath;	 Catch:{ Exception -> 0x0508 }
        if (r6 != 0) goto L_0x0158;
    L_0x0154:
        r6 = "";
        r5.attachPath = r6;	 Catch:{ Exception -> 0x0508 }
    L_0x0158:
        r6 = 0;
        r5.local_id = r6;	 Catch:{ Exception -> 0x0508 }
        r6 = r11.type;	 Catch:{ Exception -> 0x0508 }
        r29 = r1;
        r1 = 3;
        if (r6 == r1) goto L_0x0169;
    L_0x0162:
        if (r15 != 0) goto L_0x0169;
    L_0x0164:
        r1 = r11.type;	 Catch:{ Exception -> 0x0508 }
        r6 = 2;
        if (r1 != r6) goto L_0x0174;
    L_0x0169:
        r1 = r5.attachPath;	 Catch:{ Exception -> 0x0508 }
        r1 = android.text.TextUtils.isEmpty(r1);	 Catch:{ Exception -> 0x0508 }
        if (r1 != 0) goto L_0x0174;
    L_0x0171:
        r1 = 1;
        r11.attachPathExists = r1;	 Catch:{ Exception -> 0x0508 }
    L_0x0174:
        r1 = r11.videoEditedInfo;	 Catch:{ Exception -> 0x0508 }
        if (r1 == 0) goto L_0x017c;
    L_0x0178:
        if (r15 != 0) goto L_0x017c;
    L_0x017a:
        r15 = r11.videoEditedInfo;	 Catch:{ Exception -> 0x0508 }
    L_0x017c:
        if (r34 != 0) goto L_0x01ff;
    L_0x017e:
        r6 = r11.editingMessage;	 Catch:{ Exception -> 0x0508 }
        if (r6 == 0) goto L_0x01b5;
    L_0x0182:
        r6 = r11.editingMessage;	 Catch:{ Exception -> 0x0508 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0508 }
        r5.message = r6;	 Catch:{ Exception -> 0x0508 }
        r6 = r11.editingMessageEntities;	 Catch:{ Exception -> 0x0508 }
        if (r6 == 0) goto L_0x0194;
    L_0x018e:
        r6 = r11.editingMessageEntities;	 Catch:{ Exception -> 0x0508 }
        r5.entities = r6;	 Catch:{ Exception -> 0x0508 }
    L_0x0192:
        r1 = 0;
        goto L_0x01b0;
    L_0x0194:
        r6 = 1;
        r1 = new java.lang.CharSequence[r6];	 Catch:{ Exception -> 0x0508 }
        r6 = r11.editingMessage;	 Catch:{ Exception -> 0x0508 }
        r17 = 0;
        r1[r17] = r6;	 Catch:{ Exception -> 0x0508 }
        r6 = r27.getMediaDataController();	 Catch:{ Exception -> 0x0508 }
        r1 = r6.getEntities(r1, r13);	 Catch:{ Exception -> 0x0508 }
        if (r1 == 0) goto L_0x0192;
    L_0x01a7:
        r6 = r1.isEmpty();	 Catch:{ Exception -> 0x0508 }
        if (r6 != 0) goto L_0x0192;
    L_0x01ad:
        r5.entities = r1;	 Catch:{ Exception -> 0x0508 }
        goto L_0x0192;
    L_0x01b0:
        r11.caption = r1;	 Catch:{ Exception -> 0x0508 }
        r28.generateCaption();	 Catch:{ Exception -> 0x0508 }
    L_0x01b5:
        r1 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0508 }
        r1.<init>();	 Catch:{ Exception -> 0x0508 }
        r1.add(r5);	 Catch:{ Exception -> 0x0508 }
        r20 = r27.getMessagesStorage();	 Catch:{ Exception -> 0x0508 }
        r22 = 0;
        r23 = 1;
        r24 = 0;
        r25 = 0;
        r5 = r11.scheduled;	 Catch:{ Exception -> 0x0508 }
        r21 = r1;
        r26 = r5;
        r20.putMessages(r21, r22, r23, r24, r25, r26);	 Catch:{ Exception -> 0x0508 }
        r1 = -1;
        r11.type = r1;	 Catch:{ Exception -> 0x0508 }
        r28.setType();	 Catch:{ Exception -> 0x0508 }
        r28.createMessageSendInfo();	 Catch:{ Exception -> 0x0508 }
        r1 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0508 }
        r1.<init>();	 Catch:{ Exception -> 0x0508 }
        r1.add(r11);	 Catch:{ Exception -> 0x0508 }
        r5 = r27.getNotificationCenter();	 Catch:{ Exception -> 0x0508 }
        r6 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects;	 Catch:{ Exception -> 0x0508 }
        r16 = r13;
        r31 = r15;
        r13 = 2;
        r15 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0508 }
        r13 = java.lang.Long.valueOf(r7);	 Catch:{ Exception -> 0x0508 }
        r17 = 0;
        r15[r17] = r13;	 Catch:{ Exception -> 0x0508 }
        r13 = 1;
        r15[r13] = r1;	 Catch:{ Exception -> 0x0508 }
        r5.postNotificationName(r6, r15);	 Catch:{ Exception -> 0x0508 }
        goto L_0x0203;
    L_0x01ff:
        r16 = r13;
        r31 = r15;
    L_0x0203:
        if (r4 == 0) goto L_0x0213;
    L_0x0205:
        r1 = r4.containsKey(r3);	 Catch:{ Exception -> 0x0508 }
        if (r1 == 0) goto L_0x0213;
    L_0x020b:
        r1 = r4.get(r3);	 Catch:{ Exception -> 0x0508 }
        r1 = (java.lang.String) r1;	 Catch:{ Exception -> 0x0508 }
        r5 = r1;
        goto L_0x0214;
    L_0x0213:
        r5 = 0;
    L_0x0214:
        r1 = 8;
        r3 = 1;
        if (r2 < r3) goto L_0x021c;
    L_0x0219:
        r3 = 3;
        if (r2 <= r3) goto L_0x0221;
    L_0x021c:
        r3 = 5;
        if (r2 < r3) goto L_0x050f;
    L_0x021f:
        if (r2 > r1) goto L_0x050f;
    L_0x0221:
        r20 = 0;
        r3 = 2;
        if (r2 != r3) goto L_0x02db;
    L_0x0226:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x0508 }
        r3.<init>();	 Catch:{ Exception -> 0x0508 }
        if (r4 == 0) goto L_0x0268;
    L_0x022d:
        r6 = "masks";
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0508 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0508 }
        if (r4 == 0) goto L_0x0268;
    L_0x0237:
        r6 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0508 }
        r4 = org.telegram.messenger.Utilities.hexToBytes(r4);	 Catch:{ Exception -> 0x0508 }
        r6.<init>(r4);	 Catch:{ Exception -> 0x0508 }
        r4 = 0;
        r13 = r6.readInt32(r4);	 Catch:{ Exception -> 0x0508 }
        r15 = 0;
    L_0x0246:
        if (r15 >= r13) goto L_0x025f;
    L_0x0248:
        r1 = r3.stickers;	 Catch:{ Exception -> 0x0508 }
        r29 = r13;
        r13 = r6.readInt32(r4);	 Catch:{ Exception -> 0x0508 }
        r13 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r6, r13, r4);	 Catch:{ Exception -> 0x0508 }
        r1.add(r13);	 Catch:{ Exception -> 0x0508 }
        r15 = r15 + 1;
        r13 = r29;
        r1 = 8;
        r4 = 0;
        goto L_0x0246;
    L_0x025f:
        r1 = r3.flags;	 Catch:{ Exception -> 0x0508 }
        r4 = 1;
        r1 = r1 | r4;
        r3.flags = r1;	 Catch:{ Exception -> 0x0508 }
        r6.cleanup();	 Catch:{ Exception -> 0x0508 }
    L_0x0268:
        r4 = r2;
        r1 = r0.access_hash;	 Catch:{ Exception -> 0x0508 }
        r6 = (r1 > r20 ? 1 : (r1 == r20 ? 0 : -1));
        if (r6 != 0) goto L_0x0273;
    L_0x026f:
        r2 = r3;
        r6 = r14;
        r1 = 1;
        goto L_0x02a1;
    L_0x0273:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x0508 }
        r1.<init>();	 Catch:{ Exception -> 0x0508 }
        r2 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x0508 }
        r2.<init>();	 Catch:{ Exception -> 0x0508 }
        r1.id = r2;	 Catch:{ Exception -> 0x0508 }
        r2 = r1.id;	 Catch:{ Exception -> 0x0508 }
        r6 = r14;
        r13 = r0.id;	 Catch:{ Exception -> 0x0508 }
        r2.id = r13;	 Catch:{ Exception -> 0x0508 }
        r2 = r1.id;	 Catch:{ Exception -> 0x0508 }
        r13 = r0.access_hash;	 Catch:{ Exception -> 0x0508 }
        r2.access_hash = r13;	 Catch:{ Exception -> 0x0508 }
        r2 = r1.id;	 Catch:{ Exception -> 0x0508 }
        r13 = r0.file_reference;	 Catch:{ Exception -> 0x0508 }
        r2.file_reference = r13;	 Catch:{ Exception -> 0x0508 }
        r2 = r1.id;	 Catch:{ Exception -> 0x0508 }
        r2 = r2.file_reference;	 Catch:{ Exception -> 0x0508 }
        if (r2 != 0) goto L_0x029f;
    L_0x0298:
        r2 = r1.id;	 Catch:{ Exception -> 0x0508 }
        r13 = 0;
        r14 = new byte[r13];	 Catch:{ Exception -> 0x0508 }
        r2.file_reference = r14;	 Catch:{ Exception -> 0x0508 }
    L_0x029f:
        r2 = r1;
        r1 = 0;
    L_0x02a1:
        r13 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0508 }
        r13.<init>(r7);	 Catch:{ Exception -> 0x0508 }
        r7 = 0;
        r13.type = r7;	 Catch:{ Exception -> 0x0508 }
        r13.obj = r11;	 Catch:{ Exception -> 0x0508 }
        r13.originalPath = r5;	 Catch:{ Exception -> 0x0508 }
        r13.parentObject = r9;	 Catch:{ Exception -> 0x0508 }
        r13.inputUploadMedia = r3;	 Catch:{ Exception -> 0x0508 }
        r13.performMediaUpload = r1;	 Catch:{ Exception -> 0x0508 }
        if (r12 == 0) goto L_0x02c4;
    L_0x02b5:
        r3 = r12.length();	 Catch:{ Exception -> 0x0508 }
        if (r3 <= 0) goto L_0x02c4;
    L_0x02bb:
        r3 = r12.startsWith(r6);	 Catch:{ Exception -> 0x0508 }
        if (r3 == 0) goto L_0x02c4;
    L_0x02c1:
        r13.httpLocation = r12;	 Catch:{ Exception -> 0x0508 }
        goto L_0x02d8;
    L_0x02c4:
        r3 = r0.sizes;	 Catch:{ Exception -> 0x0508 }
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0508 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0508 }
        r7 = 1;
        r6 = r6 - r7;
        r3 = r3.get(r6);	 Catch:{ Exception -> 0x0508 }
        r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3;	 Catch:{ Exception -> 0x0508 }
        r13.photoSize = r3;	 Catch:{ Exception -> 0x0508 }
        r13.locationParent = r0;	 Catch:{ Exception -> 0x0508 }
    L_0x02d8:
        r6 = r1;
        goto L_0x03e0;
    L_0x02db:
        r4 = r2;
        r0 = 3;
        if (r4 != r0) goto L_0x036d;
    L_0x02df:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0508 }
        r0.<init>();	 Catch:{ Exception -> 0x0508 }
        r1 = r29;
        r2 = r1.mime_type;	 Catch:{ Exception -> 0x0508 }
        r0.mime_type = r2;	 Catch:{ Exception -> 0x0508 }
        r2 = r1.attributes;	 Catch:{ Exception -> 0x0508 }
        r0.attributes = r2;	 Catch:{ Exception -> 0x0508 }
        r2 = r28.isGif();	 Catch:{ Exception -> 0x0508 }
        if (r2 != 0) goto L_0x030c;
    L_0x02f4:
        if (r31 == 0) goto L_0x02fd;
    L_0x02f6:
        r15 = r31;
        r2 = r15.muted;	 Catch:{ Exception -> 0x0508 }
        if (r2 != 0) goto L_0x030e;
    L_0x02fc:
        goto L_0x02ff;
    L_0x02fd:
        r15 = r31;
    L_0x02ff:
        r2 = 1;
        r0.nosound_video = r2;	 Catch:{ Exception -> 0x0508 }
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x0508 }
        if (r2 == 0) goto L_0x030e;
    L_0x0306:
        r2 = "nosound_video = true";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x0508 }
        goto L_0x030e;
    L_0x030c:
        r15 = r31;
    L_0x030e:
        r2 = r1.access_hash;	 Catch:{ Exception -> 0x0508 }
        r6 = (r2 > r20 ? 1 : (r2 == r20 ? 0 : -1));
        if (r6 != 0) goto L_0x0317;
    L_0x0314:
        r2 = r0;
        r6 = 1;
        goto L_0x0343;
    L_0x0317:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0508 }
        r2.<init>();	 Catch:{ Exception -> 0x0508 }
        r3 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0508 }
        r3.<init>();	 Catch:{ Exception -> 0x0508 }
        r2.id = r3;	 Catch:{ Exception -> 0x0508 }
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r12 = r1.id;	 Catch:{ Exception -> 0x0508 }
        r3.id = r12;	 Catch:{ Exception -> 0x0508 }
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x0508 }
        r3.access_hash = r12;	 Catch:{ Exception -> 0x0508 }
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r6 = r1.file_reference;	 Catch:{ Exception -> 0x0508 }
        r3.file_reference = r6;	 Catch:{ Exception -> 0x0508 }
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r3 = r3.file_reference;	 Catch:{ Exception -> 0x0508 }
        if (r3 != 0) goto L_0x0342;
    L_0x033b:
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r6 = 0;
        r12 = new byte[r6];	 Catch:{ Exception -> 0x0508 }
        r3.file_reference = r12;	 Catch:{ Exception -> 0x0508 }
    L_0x0342:
        r6 = 0;
    L_0x0343:
        r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0508 }
        r3.<init>(r7);	 Catch:{ Exception -> 0x0508 }
        r7 = 1;
        r3.type = r7;	 Catch:{ Exception -> 0x0508 }
        r3.obj = r11;	 Catch:{ Exception -> 0x0508 }
        r3.originalPath = r5;	 Catch:{ Exception -> 0x0508 }
        r3.parentObject = r9;	 Catch:{ Exception -> 0x0508 }
        r3.inputUploadMedia = r0;	 Catch:{ Exception -> 0x0508 }
        r3.performMediaUpload = r6;	 Catch:{ Exception -> 0x0508 }
        r0 = r1.thumbs;	 Catch:{ Exception -> 0x0508 }
        r0 = r0.isEmpty();	 Catch:{ Exception -> 0x0508 }
        if (r0 != 0) goto L_0x036a;
    L_0x035d:
        r0 = r1.thumbs;	 Catch:{ Exception -> 0x0508 }
        r7 = 0;
        r0 = r0.get(r7);	 Catch:{ Exception -> 0x0508 }
        r0 = (org.telegram.tgnet.TLRPC.PhotoSize) r0;	 Catch:{ Exception -> 0x0508 }
        r3.photoSize = r0;	 Catch:{ Exception -> 0x0508 }
        r3.locationParent = r1;	 Catch:{ Exception -> 0x0508 }
    L_0x036a:
        r3.videoEditedInfo = r15;	 Catch:{ Exception -> 0x0508 }
        goto L_0x03db;
    L_0x036d:
        r1 = r29;
        r0 = 7;
        if (r4 != r0) goto L_0x03dd;
    L_0x0372:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0508 }
        r0.<init>();	 Catch:{ Exception -> 0x0508 }
        r2 = r1.mime_type;	 Catch:{ Exception -> 0x0508 }
        r0.mime_type = r2;	 Catch:{ Exception -> 0x0508 }
        r2 = r1.attributes;	 Catch:{ Exception -> 0x0508 }
        r0.attributes = r2;	 Catch:{ Exception -> 0x0508 }
        r2 = r1.access_hash;	 Catch:{ Exception -> 0x0508 }
        r6 = (r2 > r20 ? 1 : (r2 == r20 ? 0 : -1));
        if (r6 != 0) goto L_0x0388;
    L_0x0385:
        r2 = r0;
        r6 = 1;
        goto L_0x03b4;
    L_0x0388:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0508 }
        r2.<init>();	 Catch:{ Exception -> 0x0508 }
        r3 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0508 }
        r3.<init>();	 Catch:{ Exception -> 0x0508 }
        r2.id = r3;	 Catch:{ Exception -> 0x0508 }
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r12 = r1.id;	 Catch:{ Exception -> 0x0508 }
        r3.id = r12;	 Catch:{ Exception -> 0x0508 }
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x0508 }
        r3.access_hash = r12;	 Catch:{ Exception -> 0x0508 }
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r6 = r1.file_reference;	 Catch:{ Exception -> 0x0508 }
        r3.file_reference = r6;	 Catch:{ Exception -> 0x0508 }
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r3 = r3.file_reference;	 Catch:{ Exception -> 0x0508 }
        if (r3 != 0) goto L_0x03b3;
    L_0x03ac:
        r3 = r2.id;	 Catch:{ Exception -> 0x0508 }
        r6 = 0;
        r12 = new byte[r6];	 Catch:{ Exception -> 0x0508 }
        r3.file_reference = r12;	 Catch:{ Exception -> 0x0508 }
    L_0x03b3:
        r6 = 0;
    L_0x03b4:
        r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0508 }
        r3.<init>(r7);	 Catch:{ Exception -> 0x0508 }
        r3.originalPath = r5;	 Catch:{ Exception -> 0x0508 }
        r7 = 2;
        r3.type = r7;	 Catch:{ Exception -> 0x0508 }
        r3.obj = r11;	 Catch:{ Exception -> 0x0508 }
        r7 = r1.thumbs;	 Catch:{ Exception -> 0x0508 }
        r7 = r7.isEmpty();	 Catch:{ Exception -> 0x0508 }
        if (r7 != 0) goto L_0x03d5;
    L_0x03c8:
        r7 = r1.thumbs;	 Catch:{ Exception -> 0x0508 }
        r8 = 0;
        r7 = r7.get(r8);	 Catch:{ Exception -> 0x0508 }
        r7 = (org.telegram.tgnet.TLRPC.PhotoSize) r7;	 Catch:{ Exception -> 0x0508 }
        r3.photoSize = r7;	 Catch:{ Exception -> 0x0508 }
        r3.locationParent = r1;	 Catch:{ Exception -> 0x0508 }
    L_0x03d5:
        r3.parentObject = r9;	 Catch:{ Exception -> 0x0508 }
        r3.inputUploadMedia = r0;	 Catch:{ Exception -> 0x0508 }
        r3.performMediaUpload = r6;	 Catch:{ Exception -> 0x0508 }
    L_0x03db:
        r13 = r3;
        goto L_0x03e0;
    L_0x03dd:
        r2 = 0;
        r6 = 0;
        r13 = 0;
    L_0x03e0:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage;	 Catch:{ Exception -> 0x0508 }
        r0.<init>();	 Catch:{ Exception -> 0x0508 }
        r1 = r28.getId();	 Catch:{ Exception -> 0x0508 }
        r0.id = r1;	 Catch:{ Exception -> 0x0508 }
        r1 = r27.getMessagesController();	 Catch:{ Exception -> 0x0508 }
        r3 = r19;
        r1 = r1.getInputPeer(r3);	 Catch:{ Exception -> 0x0508 }
        r0.peer = r1;	 Catch:{ Exception -> 0x0508 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x0508 }
        r1 = r1 | 16384;
        r0.flags = r1;	 Catch:{ Exception -> 0x0508 }
        r0.media = r2;	 Catch:{ Exception -> 0x0508 }
        r1 = r11.scheduled;	 Catch:{ Exception -> 0x0508 }
        if (r1 == 0) goto L_0x0411;
    L_0x0403:
        r1 = r11.messageOwner;	 Catch:{ Exception -> 0x0508 }
        r1 = r1.date;	 Catch:{ Exception -> 0x0508 }
        r0.schedule_date = r1;	 Catch:{ Exception -> 0x0508 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x0508 }
        r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x0508 }
    L_0x0411:
        r1 = r11.editingMessage;	 Catch:{ Exception -> 0x0508 }
        if (r1 == 0) goto L_0x045c;
    L_0x0415:
        r1 = r11.editingMessage;	 Catch:{ Exception -> 0x0508 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0508 }
        r0.message = r1;	 Catch:{ Exception -> 0x0508 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x0508 }
        r1 = r1 | 2048;
        r0.flags = r1;	 Catch:{ Exception -> 0x0508 }
        r1 = r11.editingMessageEntities;	 Catch:{ Exception -> 0x0508 }
        if (r1 == 0) goto L_0x0434;
    L_0x0427:
        r1 = r11.editingMessageEntities;	 Catch:{ Exception -> 0x0508 }
        r0.entities = r1;	 Catch:{ Exception -> 0x0508 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x0508 }
        r2 = 8;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x0508 }
    L_0x0432:
        r1 = 0;
        goto L_0x0458;
    L_0x0434:
        r1 = 1;
        r2 = new java.lang.CharSequence[r1];	 Catch:{ Exception -> 0x0508 }
        r1 = r11.editingMessage;	 Catch:{ Exception -> 0x0508 }
        r3 = 0;
        r2[r3] = r1;	 Catch:{ Exception -> 0x0508 }
        r1 = r27.getMediaDataController();	 Catch:{ Exception -> 0x0508 }
        r12 = r16;
        r1 = r1.getEntities(r2, r12);	 Catch:{ Exception -> 0x0508 }
        if (r1 == 0) goto L_0x0432;
    L_0x0448:
        r2 = r1.isEmpty();	 Catch:{ Exception -> 0x0508 }
        if (r2 != 0) goto L_0x0432;
    L_0x044e:
        r0.entities = r1;	 Catch:{ Exception -> 0x0508 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x0508 }
        r2 = 8;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x0508 }
        goto L_0x0432;
    L_0x0458:
        r11.editingMessage = r1;	 Catch:{ Exception -> 0x0508 }
        r11.editingMessageEntities = r1;	 Catch:{ Exception -> 0x0508 }
    L_0x045c:
        if (r13 == 0) goto L_0x0460;
    L_0x045e:
        r13.sendRequest = r0;	 Catch:{ Exception -> 0x0508 }
    L_0x0460:
        r1 = 1;
        if (r4 != r1) goto L_0x0479;
    L_0x0463:
        r1 = 0;
        r2 = r11.scheduled;	 Catch:{ Exception -> 0x0508 }
        r29 = r27;
        r30 = r0;
        r31 = r28;
        r32 = r1;
        r33 = r13;
        r34 = r9;
        r35 = r2;
        r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x0479:
        r1 = 2;
        if (r4 != r1) goto L_0x0497;
    L_0x047c:
        if (r6 == 0) goto L_0x0483;
    L_0x047e:
        r10.performSendDelayedMessage(r13);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x0483:
        r6 = 0;
        r7 = 1;
        r12 = r11.scheduled;	 Catch:{ Exception -> 0x0508 }
        r1 = r27;
        r2 = r0;
        r3 = r28;
        r4 = r5;
        r5 = r6;
        r6 = r7;
        r7 = r13;
        r8 = r9;
        r9 = r12;
        r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x0497:
        r1 = 3;
        if (r4 != r1) goto L_0x04b6;
    L_0x049a:
        if (r6 == 0) goto L_0x04a1;
    L_0x049c:
        r10.performSendDelayedMessage(r13);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x04a1:
        r1 = r11.scheduled;	 Catch:{ Exception -> 0x0508 }
        r29 = r27;
        r30 = r0;
        r31 = r28;
        r32 = r5;
        r33 = r13;
        r34 = r9;
        r35 = r1;
        r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x04b6:
        r1 = 6;
        if (r4 != r1) goto L_0x04cd;
    L_0x04b9:
        r1 = r11.scheduled;	 Catch:{ Exception -> 0x0508 }
        r29 = r27;
        r30 = r0;
        r31 = r28;
        r32 = r5;
        r33 = r13;
        r34 = r9;
        r35 = r1;
        r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x04cd:
        r1 = 7;
        if (r4 != r1) goto L_0x04ea;
    L_0x04d0:
        if (r6 == 0) goto L_0x04d6;
    L_0x04d2:
        r10.performSendDelayedMessage(r13);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x04d6:
        r1 = r11.scheduled;	 Catch:{ Exception -> 0x0508 }
        r29 = r27;
        r30 = r0;
        r31 = r28;
        r32 = r5;
        r33 = r13;
        r34 = r9;
        r35 = r1;
        r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x04ea:
        r1 = 8;
        if (r4 != r1) goto L_0x050f;
    L_0x04ee:
        if (r6 == 0) goto L_0x04f4;
    L_0x04f0:
        r10.performSendDelayedMessage(r13);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x04f4:
        r1 = r11.scheduled;	 Catch:{ Exception -> 0x0508 }
        r29 = r27;
        r30 = r0;
        r31 = r28;
        r32 = r5;
        r33 = r13;
        r34 = r9;
        r35 = r1;
        r29.performSendMessageRequest(r30, r31, r32, r33, r34, r35);	 Catch:{ Exception -> 0x0508 }
        goto L_0x050f;
    L_0x0508:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r27.revertEditingMessageObject(r28);
    L_0x050f:
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
        return getConnectionsManager().sendRequest(tL_messages_editMessage, new -$$Lambda$SendMessagesHelper$JsdGM0zMCODeUq2Vr9rMiMgxkX4(this, baseFragment, tL_messages_editMessage, runnable));
    }

    public /* synthetic */ void lambda$editMessage$16$SendMessagesHelper(BaseFragment baseFragment, TL_messages_editMessage tL_messages_editMessage, Runnable runnable, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((Updates) tLObject, false);
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$BiIbtQCAbk4oW3HTe_8ft5na8kw(this, tL_error, baseFragment, tL_messages_editMessage));
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public /* synthetic */ void lambda$null$15$SendMessagesHelper(TL_error tL_error, BaseFragment baseFragment, TL_messages_editMessage tL_messages_editMessage) {
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$0uLjEY-cO38dYAef7BOpiioWvd0(this, j, i, bArr));
    }

    public /* synthetic */ void lambda$sendNotificationCallback$19$SendMessagesHelper(long j, int i, byte[] bArr) {
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
        getConnectionsManager().sendRequest(tL_messages_getBotCallbackAnswer, new -$$Lambda$SendMessagesHelper$5UNvPm8h-W0ysFH3aU7qM3sNNKs(this, stringBuilder2), 2);
        getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, true, 0);
    }

    public /* synthetic */ void lambda$null$17$SendMessagesHelper(String str) {
        Boolean bool = (Boolean) this.waitingForCallback.remove(str);
    }

    public /* synthetic */ void lambda$null$18$SendMessagesHelper(String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$lmMm35jU9kNJ1iJ22PkG0ANzflA(this, str));
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

    public int sendVote(MessageObject messageObject, ArrayList<TL_pollAnswer> arrayList, Runnable runnable) {
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
        Object obj;
        TL_messages_sendVote tL_messages_sendVote = new TL_messages_sendVote();
        tL_messages_sendVote.msg_id = messageObject.getId();
        tL_messages_sendVote.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        if (arrayList != null) {
            obj = new byte[arrayList.size()];
            for (int i = 0; i < arrayList.size(); i++) {
                TL_pollAnswer tL_pollAnswer = (TL_pollAnswer) arrayList.get(i);
                tL_messages_sendVote.options.add(tL_pollAnswer.option);
                obj[i] = tL_pollAnswer.option[0];
            }
        } else {
            obj = new byte[0];
        }
        this.waitingForVote.put(stringBuilder2, obj);
        return getConnectionsManager().sendRequest(tL_messages_sendVote, new -$$Lambda$SendMessagesHelper$ys-CdefWaWugQDVNhCPtcm2hCzc(this, messageObject, stringBuilder2, runnable));
    }

    public /* synthetic */ void lambda$sendVote$21$SendMessagesHelper(MessageObject messageObject, String str, Runnable runnable, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(0));
            getMessagesController().processUpdates((Updates) tLObject, false);
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(SystemClock.elapsedRealtime()));
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$LwweBgl5GzaYakuYZQFJVy0hJIg(this, str, runnable));
    }

    public /* synthetic */ void lambda$null$20$SendMessagesHelper(String str, Runnable runnable) {
        this.waitingForVote.remove(str);
        if (runnable != null) {
            runnable.run();
        }
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
            getConnectionsManager().sendRequest(tL_messages_sendReaction, new -$$Lambda$SendMessagesHelper$SEw5IDt77PQJsopndUCrAMIHxLc(this));
        }
    }

    public /* synthetic */ void lambda$sendReaction$22$SendMessagesHelper(TLObject tLObject, TL_error tL_error) {
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
        r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$jARrrSNLwypH2Y4M6FY3LifNwFc;
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

    public /* synthetic */ void lambda$sendCallback$24$SendMessagesHelper(String str, boolean z, MessageObject messageObject, KeyboardButton keyboardButton, ChatActivity chatActivity, TLObject[] tLObjectArr, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$QBwLU519xB-CaGJ_Rimq-heG_so(this, str, z, tLObject, messageObject, keyboardButton, chatActivity, tLObjectArr));
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
    public /* synthetic */ void lambda$null$23$SendMessagesHelper(java.lang.String r12, boolean r13, org.telegram.tgnet.TLObject r14, org.telegram.messenger.MessageObject r15, org.telegram.tgnet.TLRPC.KeyboardButton r16, org.telegram.ui.ChatActivity r17, org.telegram.tgnet.TLObject[] r18) {
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
        r1 = NUM; // 0x7f0e0775 float:1.887891E38 double:1.0531631E-314;
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$23$SendMessagesHelper(java.lang.String, boolean, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity, org.telegram.tgnet.TLObject[]):void");
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
                    getConnectionsManager().sendRequest(tL_messages_sendMedia, new -$$Lambda$SendMessagesHelper$L1N1WK2z-7u2Y5cBqA9cZoVdm-8(this, j2));
                }
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tL_messages_sendMedia, new -$$Lambda$SendMessagesHelper$L1N1WK2z-7u2Y5cBqA9cZoVdm-8(this, j2));
        }
    }

    public /* synthetic */ void lambda$sendGame$25$SendMessagesHelper(long j, TLObject tLObject, TL_error tL_error) {
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

    /* JADX WARNING: Removed duplicated region for block: B:1137:0x1633 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1141:0x165a A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1140:0x164c A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1150:0x1689 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1149:0x1687 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1169:0x16ed A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1172:0x1713 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1176:0x1724 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1175:0x1722 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:804:0x0e05 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:806:0x0e11 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x0ebc A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x0ee1 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:833:0x0eeb A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x0e7c A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x0dc3 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x0ef7 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:837:0x0ef2 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:726:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x0CLASSNAME A:{SYNTHETIC, Splitter:B:723:0x0CLASSNAME} */
    /* JADX WARNING: Removed duplicated region for block: B:746:0x0cd2  */
    /* JADX WARNING: Removed duplicated region for block: B:735:0x0c9c A:{Catch:{ Exception -> 0x0ccb }} */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x0dc3 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x0e7c A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:837:0x0ef2 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x0ef7 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x0e7c A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x0dc3 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x0ef7 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:837:0x0ef2 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x0dc3 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x0e7c A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:837:0x0ef2 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x0ef7 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:814:0x0e7c A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:791:0x0dc3 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:838:0x0ef7 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:837:0x0ef2 A:{Catch:{ Exception -> 0x0b38 }} */
    /* JADX WARNING: Removed duplicated region for block: B:970:0x11ed  */
    /* JADX WARNING: Removed duplicated region for block: B:969:0x11eb  */
    /* JADX WARNING: Removed duplicated region for block: B:949:0x1148 A:{Catch:{ Exception -> 0x15b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:955:0x117a A:{Catch:{ Exception -> 0x15b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:973:0x11f1 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:960:0x11bb A:{Catch:{ Exception -> 0x15b4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:976:0x1201 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x159b A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1096:0x1556 A:{SYNTHETIC, Splitter:B:1096:0x1556} */
    /* JADX WARNING: Removed duplicated region for block: B:1114:0x15a1 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1092:0x154f A:{SYNTHETIC, Splitter:B:1092:0x154f} */
    /* JADX WARNING: Removed duplicated region for block: B:1096:0x1556 A:{SYNTHETIC, Splitter:B:1096:0x1556} */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x159b A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1114:0x15a1 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1111:0x159b A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1096:0x1556 A:{SYNTHETIC, Splitter:B:1096:0x1556} */
    /* JADX WARNING: Removed duplicated region for block: B:1114:0x15a1 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1193:0x1786 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1195:0x1792 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1201:0x17a8 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1205:0x17b6 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1204:0x17b4 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1208:0x17ca A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x08cf  */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0875 A:{SYNTHETIC, Splitter:B:517:0x0875} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0991 A:{Catch:{ Exception -> 0x08f6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17d4 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1180:0x1748 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0806  */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0804  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x080f A:{SYNTHETIC, Splitter:B:486:0x080f} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x086a  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x083a A:{Catch:{ Exception -> 0x0827 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0875 A:{SYNTHETIC, Splitter:B:517:0x0875} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x08cf  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0922 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0991 A:{Catch:{ Exception -> 0x08f6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1180:0x1748 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17d4 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x07e8 A:{Catch:{ Exception -> 0x0678 }} */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0804  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0806  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x080f A:{SYNTHETIC, Splitter:B:486:0x080f} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0831 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x083a A:{Catch:{ Exception -> 0x0827 }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x086a  */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x08cf  */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0875 A:{SYNTHETIC, Splitter:B:517:0x0875} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0922 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0991 A:{Catch:{ Exception -> 0x08f6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17d4 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1180:0x1748 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x0241 A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x05df A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0602 A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x05f1 A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x062c A:{SYNTHETIC, Splitter:B:364:0x062c} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x067d  */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x063a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x06c3 A:{Catch:{ Exception -> 0x0678 }} */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0691  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x071b  */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x06d9 A:{SYNTHETIC, Splitter:B:404:0x06d9} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x07e8 A:{Catch:{ Exception -> 0x0678 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0806  */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0804  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x080f A:{SYNTHETIC, Splitter:B:486:0x080f} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0831 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x086a  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x083a A:{Catch:{ Exception -> 0x0827 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0875 A:{SYNTHETIC, Splitter:B:517:0x0875} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x08cf  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0922 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0991 A:{Catch:{ Exception -> 0x08f6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1180:0x1748 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17d4 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0104 A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00eb A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x056b A:{SYNTHETIC, Splitter:B:311:0x056b} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0580 A:{SYNTHETIC, Splitter:B:318:0x0580} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x057d A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x058a A:{SYNTHETIC, Splitter:B:326:0x058a} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x056b A:{SYNTHETIC, Splitter:B:311:0x056b} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x057d A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0580 A:{SYNTHETIC, Splitter:B:318:0x0580} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x058a A:{SYNTHETIC, Splitter:B:326:0x058a} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x059d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x049e A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x04cb A:{Catch:{ Exception -> 0x0561 }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x049e A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x04b2 A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x04cb A:{Catch:{ Exception -> 0x0561 }} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x056b A:{SYNTHETIC, Splitter:B:311:0x056b} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0580 A:{SYNTHETIC, Splitter:B:318:0x0580} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x057d A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x058a A:{SYNTHETIC, Splitter:B:326:0x058a} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x059d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x056b A:{SYNTHETIC, Splitter:B:311:0x056b} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x057d A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0580 A:{SYNTHETIC, Splitter:B:318:0x0580} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x058a A:{SYNTHETIC, Splitter:B:326:0x058a} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x059d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x056b A:{SYNTHETIC, Splitter:B:311:0x056b} */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x0580 A:{SYNTHETIC, Splitter:B:318:0x0580} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x057d A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:326:0x058a A:{SYNTHETIC, Splitter:B:326:0x058a} */
    /* JADX WARNING: Removed duplicated region for block: B:331:0x059d A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x05df A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x05f1 A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0602 A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x061e A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x062c A:{SYNTHETIC, Splitter:B:364:0x062c} */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x063a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x067d  */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0691  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x06c3 A:{Catch:{ Exception -> 0x0678 }} */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x06d9 A:{SYNTHETIC, Splitter:B:404:0x06d9} */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x071b  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x07e8 A:{Catch:{ Exception -> 0x0678 }} */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0804  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0806  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x080f A:{SYNTHETIC, Splitter:B:486:0x080f} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0831 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x083a A:{Catch:{ Exception -> 0x0827 }} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x086a  */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x08cf  */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0875 A:{SYNTHETIC, Splitter:B:517:0x0875} */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0922 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0991 A:{Catch:{ Exception -> 0x08f6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17d4 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1180:0x1748 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x026c  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00d2 A:{SYNTHETIC, Splitter:B:42:0x00d2} */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x05df A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0602 A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x05f1 A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x061e A:{Catch:{ Exception -> 0x025f }} */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x062c A:{SYNTHETIC, Splitter:B:364:0x062c} */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x062a  */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x067d  */
    /* JADX WARNING: Removed duplicated region for block: B:368:0x063a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x06c3 A:{Catch:{ Exception -> 0x0678 }} */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0691  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x071b  */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x06d9 A:{SYNTHETIC, Splitter:B:404:0x06d9} */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x07e8 A:{Catch:{ Exception -> 0x0678 }} */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0806  */
    /* JADX WARNING: Removed duplicated region for block: B:481:0x0804  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x080f A:{SYNTHETIC, Splitter:B:486:0x080f} */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0831 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x086a  */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x083a A:{Catch:{ Exception -> 0x0827 }} */
    /* JADX WARNING: Removed duplicated region for block: B:517:0x0875 A:{SYNTHETIC, Splitter:B:517:0x0875} */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x08cf  */
    /* JADX WARNING: Removed duplicated region for block: B:564:0x0922 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:569:0x0991 A:{Catch:{ Exception -> 0x08f6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1180:0x1748 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1209:0x17d4 A:{Catch:{ Exception -> 0x1879 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Removed duplicated region for block: B:1250:0x18a0  */
    /* JADX WARNING: Removed duplicated region for block: B:1251:0x18a2  */
    /* JADX WARNING: Removed duplicated region for block: B:1254:0x18a8  */
    /* JADX WARNING: Missing block: B:198:0x0397, code skipped:
            if (r12.containsKey(r4) != false) goto L_0x02f4;
     */
    /* JADX WARNING: Missing block: B:281:0x04d6, code skipped:
            if (org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, false) != false) goto L_0x04d8;
     */
    private void sendMessage(java.lang.String r44, java.lang.String r45, org.telegram.tgnet.TLRPC.MessageMedia r46, org.telegram.tgnet.TLRPC.TL_photo r47, org.telegram.messenger.VideoEditedInfo r48, org.telegram.tgnet.TLRPC.User r49, org.telegram.tgnet.TLRPC.TL_document r50, org.telegram.tgnet.TLRPC.TL_game r51, org.telegram.tgnet.TLRPC.TL_messageMediaPoll r52, long r53, java.lang.String r55, org.telegram.messenger.MessageObject r56, org.telegram.tgnet.TLRPC.WebPage r57, boolean r58, org.telegram.messenger.MessageObject r59, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r60, org.telegram.tgnet.TLRPC.ReplyMarkup r61, java.util.HashMap<java.lang.String, java.lang.String> r62, boolean r63, int r64, int r65, java.lang.Object r66) {
        /*
        r43 = this;
        r1 = r43;
        r2 = r44;
        r3 = r46;
        r4 = r47;
        r5 = r49;
        r6 = r50;
        r7 = r51;
        r8 = r52;
        r9 = r53;
        r11 = r55;
        r12 = r56;
        r13 = r57;
        r14 = r59;
        r15 = r60;
        r12 = r62;
        r6 = r65;
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
        if (r45 != 0) goto L_0x0037;
    L_0x0034:
        r18 = r7;
        goto L_0x0039;
    L_0x0037:
        r18 = r45;
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
        r3 = r43.getMessagesController();
        r3 = r3.getInputPeer(r5);
        goto L_0x0063;
    L_0x0062:
        r3 = 0;
    L_0x0063:
        if (r5 != 0) goto L_0x00a7;
    L_0x0065:
        r10 = r43.getMessagesController();
        r9 = java.lang.Integer.valueOf(r4);
        r9 = r10.getEncryptedChat(r9);
        if (r9 != 0) goto L_0x00a4;
    L_0x0073:
        if (r14 == 0) goto L_0x00a3;
    L_0x0075:
        r2 = r43.getMessagesStorage();
        r3 = r14.messageOwner;
        r4 = r14.scheduled;
        r2.markMessageAsSendError(r3, r4);
        r2 = r14.messageOwner;
        r3 = 2;
        r2.send_state = r3;
        r2 = r43.getNotificationCenter();
        r3 = org.telegram.messenger.NotificationCenter.messageSendError;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = r59.getId();
        r5 = java.lang.Integer.valueOf(r5);
        r6 = 0;
        r4[r6] = r5;
        r2.postNotificationName(r3, r4);
        r2 = r59.getId();
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
        r9 = r43.getMessagesController();
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
        r1 = "parentObject";
        r25 = r5;
        if (r14 == 0) goto L_0x026c;
    L_0x00d2:
        r5 = r14.messageOwner;	 Catch:{ Exception -> 0x0264 }
        if (r66 != 0) goto L_0x00e3;
    L_0x00d6:
        if (r12 == 0) goto L_0x00e3;
    L_0x00d8:
        r26 = r12.containsKey(r1);	 Catch:{ Exception -> 0x025f }
        if (r26 == 0) goto L_0x00e3;
    L_0x00de:
        r26 = r12.get(r1);	 Catch:{ Exception -> 0x025f }
        goto L_0x00e5;
    L_0x00e3:
        r26 = r66;
    L_0x00e5:
        r27 = r59.isForwarded();	 Catch:{ Exception -> 0x025f }
        if (r27 == 0) goto L_0x0104;
    L_0x00eb:
        r44 = r47;
        r27 = r1;
        r1 = r2;
        r19 = r4;
        r2 = r8;
        r29 = r10;
        r32 = r13;
        r51 = r18;
        r18 = r26;
        r15 = 4;
        r8 = r46;
        r13 = r49;
    L_0x0100:
        r4 = r50;
        goto L_0x05d9;
    L_0x0104:
        r27 = r1;
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        if (r1 == 0) goto L_0x0213;
    L_0x010a:
        r1 = r59.isAnimatedEmoji();	 Catch:{ Exception -> 0x025f }
        if (r1 == 0) goto L_0x0112;
    L_0x0110:
        goto L_0x0213;
    L_0x0112:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r28 = r3;
        r3 = 4;
        if (r1 != r3) goto L_0x012a;
    L_0x0119:
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r19 = r50;
        r30 = r8;
        r29 = r10;
        r3 = 2;
        r22 = 1;
        r8 = r47;
        r10 = r49;
        goto L_0x0231;
    L_0x012a:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 1;
        if (r1 != r3) goto L_0x0145;
    L_0x012f:
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r1 = r1.photo;	 Catch:{ Exception -> 0x025f }
        r1 = (org.telegram.tgnet.TLRPC.TL_photo) r1;	 Catch:{ Exception -> 0x025f }
        r19 = r50;
        r30 = r8;
        r29 = r10;
        r3 = 2;
        r22 = 2;
        r10 = r49;
        r8 = r1;
    L_0x0141:
        r1 = r46;
        goto L_0x0231;
    L_0x0145:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 3;
        if (r1 == r3) goto L_0x0201;
    L_0x014a:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 5;
        if (r1 == r3) goto L_0x0201;
    L_0x014f:
        if (r48 == 0) goto L_0x0153;
    L_0x0151:
        goto L_0x0201;
    L_0x0153:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 12;
        if (r1 != r3) goto L_0x0198;
    L_0x0159:
        r1 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2;	 Catch:{ Exception -> 0x025f }
        r1.<init>();	 Catch:{ Exception -> 0x025f }
        r3 = r5.media;	 Catch:{ Exception -> 0x025f }
        r3 = r3.phone_number;	 Catch:{ Exception -> 0x025f }
        r1.phone = r3;	 Catch:{ Exception -> 0x025f }
        r3 = r5.media;	 Catch:{ Exception -> 0x025f }
        r3 = r3.first_name;	 Catch:{ Exception -> 0x025f }
        r1.first_name = r3;	 Catch:{ Exception -> 0x025f }
        r3 = r5.media;	 Catch:{ Exception -> 0x025f }
        r3 = r3.last_name;	 Catch:{ Exception -> 0x025f }
        r1.last_name = r3;	 Catch:{ Exception -> 0x025f }
        r3 = new org.telegram.tgnet.TLRPC$TL_restrictionReason;	 Catch:{ Exception -> 0x025f }
        r3.<init>();	 Catch:{ Exception -> 0x025f }
        r3.platform = r7;	 Catch:{ Exception -> 0x025f }
        r3.reason = r7;	 Catch:{ Exception -> 0x025f }
        r29 = r10;
        r10 = r5.media;	 Catch:{ Exception -> 0x025f }
        r10 = r10.vcard;	 Catch:{ Exception -> 0x025f }
        r3.text = r10;	 Catch:{ Exception -> 0x025f }
        r10 = r1.restriction_reason;	 Catch:{ Exception -> 0x025f }
        r10.add(r3);	 Catch:{ Exception -> 0x025f }
        r3 = r5.media;	 Catch:{ Exception -> 0x025f }
        r3 = r3.user_id;	 Catch:{ Exception -> 0x025f }
        r1.id = r3;	 Catch:{ Exception -> 0x025f }
        r19 = 6;
        r19 = r50;
        r10 = r1;
        r30 = r8;
        r3 = 2;
        r22 = 6;
        goto L_0x022d;
    L_0x0198:
        r29 = r10;
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 8;
        if (r1 == r3) goto L_0x01ef;
    L_0x01a0:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 9;
        if (r1 == r3) goto L_0x01ef;
    L_0x01a6:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 13;
        if (r1 == r3) goto L_0x01ef;
    L_0x01ac:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 14;
        if (r1 == r3) goto L_0x01ef;
    L_0x01b2:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 15;
        if (r1 != r3) goto L_0x01b9;
    L_0x01b8:
        goto L_0x01ef;
    L_0x01b9:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r3 = 2;
        if (r1 != r3) goto L_0x01ce;
    L_0x01be:
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r1 = r1.document;	 Catch:{ Exception -> 0x025f }
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;	 Catch:{ Exception -> 0x025f }
        r10 = r49;
        r19 = r1;
        r30 = r8;
        r22 = 8;
        goto L_0x022d;
    L_0x01ce:
        r1 = r14.type;	 Catch:{ Exception -> 0x025f }
        r10 = 17;
        if (r1 != r10) goto L_0x01e4;
    L_0x01d4:
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r1 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r1;	 Catch:{ Exception -> 0x025f }
        r8 = r47;
        r10 = r49;
        r19 = r50;
        r30 = r1;
        r22 = 10;
        goto L_0x0141;
    L_0x01e4:
        r1 = r46;
        r10 = r49;
        r19 = r50;
        r30 = r8;
        r22 = -1;
        goto L_0x022f;
    L_0x01ef:
        r3 = 2;
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r1 = r1.document;	 Catch:{ Exception -> 0x025f }
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;	 Catch:{ Exception -> 0x025f }
        r19 = 7;
        r10 = r49;
        r19 = r1;
        r30 = r8;
        r22 = 7;
        goto L_0x022d;
    L_0x0201:
        r29 = r10;
        r3 = 2;
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r1 = r1.document;	 Catch:{ Exception -> 0x025f }
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;	 Catch:{ Exception -> 0x025f }
        r10 = r49;
        r19 = r1;
        r30 = r8;
        r22 = 3;
        goto L_0x022d;
    L_0x0213:
        r28 = r3;
        r29 = r10;
        r3 = 2;
        r1 = r14.messageOwner;	 Catch:{ Exception -> 0x025f }
        r1 = r1.media;	 Catch:{ Exception -> 0x025f }
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;	 Catch:{ Exception -> 0x025f }
        if (r1 == 0) goto L_0x0222;
    L_0x0220:
        r1 = r2;
        goto L_0x0224;
    L_0x0222:
        r1 = r5.message;	 Catch:{ Exception -> 0x025f }
    L_0x0224:
        r10 = r49;
        r19 = r50;
        r2 = r1;
        r30 = r8;
        r22 = 0;
    L_0x022d:
        r1 = r46;
    L_0x022f:
        r8 = r47;
    L_0x0231:
        if (r12 == 0) goto L_0x023b;
    L_0x0233:
        r31 = r12.containsKey(r4);	 Catch:{ Exception -> 0x025f }
        if (r31 == 0) goto L_0x023b;
    L_0x0239:
        r22 = 9;
    L_0x023b:
        r3 = r5.media;	 Catch:{ Exception -> 0x025f }
        r3 = r3.ttl_seconds;	 Catch:{ Exception -> 0x025f }
        if (r3 <= 0) goto L_0x0246;
    L_0x0241:
        r3 = r5.media;	 Catch:{ Exception -> 0x025f }
        r3 = r3.ttl_seconds;	 Catch:{ Exception -> 0x025f }
        r6 = r3;
    L_0x0246:
        r44 = r8;
        r32 = r13;
        r51 = r18;
        r15 = r22;
        r18 = r26;
        r3 = r28;
        r8 = r1;
        r1 = r2;
        r13 = r10;
        r2 = r30;
        r42 = r19;
        r19 = r4;
        r4 = r42;
        goto L_0x05d9;
    L_0x025f:
        r0 = move-exception;
    L_0x0260:
        r1 = r64;
    L_0x0262:
        r2 = r0;
        goto L_0x0269;
    L_0x0264:
        r0 = move-exception;
        r1 = r64;
        r2 = r0;
        r5 = 0;
    L_0x0269:
        r11 = 0;
        goto L_0x1897;
    L_0x026c:
        r27 = r1;
        r28 = r3;
        r29 = r10;
        if (r2 == 0) goto L_0x02be;
    L_0x0274:
        if (r9 == 0) goto L_0x027c;
    L_0x0276:
        r1 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0264 }
        r1.<init>();	 Catch:{ Exception -> 0x0264 }
        goto L_0x0281;
    L_0x027c:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0264 }
        r1.<init>();	 Catch:{ Exception -> 0x0264 }
    L_0x0281:
        r5 = r1;
        if (r9 == 0) goto L_0x0298;
    L_0x0284:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending;	 Catch:{ Exception -> 0x025f }
        if (r1 == 0) goto L_0x0298;
    L_0x0288:
        r1 = r13.url;	 Catch:{ Exception -> 0x025f }
        if (r1 == 0) goto L_0x0297;
    L_0x028c:
        r1 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending;	 Catch:{ Exception -> 0x025f }
        r1.<init>();	 Catch:{ Exception -> 0x025f }
        r3 = r13.url;	 Catch:{ Exception -> 0x025f }
        r1.url = r3;	 Catch:{ Exception -> 0x025f }
        r13 = r1;
        goto L_0x0298;
    L_0x0297:
        r13 = 0;
    L_0x0298:
        if (r13 != 0) goto L_0x02a2;
    L_0x029a:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Exception -> 0x025f }
        r1.<init>();	 Catch:{ Exception -> 0x025f }
        r5.media = r1;	 Catch:{ Exception -> 0x025f }
        goto L_0x02ad;
    L_0x02a2:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;	 Catch:{ Exception -> 0x025f }
        r1.<init>();	 Catch:{ Exception -> 0x025f }
        r5.media = r1;	 Catch:{ Exception -> 0x025f }
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r1.webpage = r13;	 Catch:{ Exception -> 0x025f }
    L_0x02ad:
        if (r12 == 0) goto L_0x02b8;
    L_0x02af:
        r1 = r12.containsKey(r4);	 Catch:{ Exception -> 0x025f }
        if (r1 == 0) goto L_0x02b8;
    L_0x02b5:
        r19 = 9;
        goto L_0x02ba;
    L_0x02b8:
        r19 = 0;
    L_0x02ba:
        r5.message = r2;	 Catch:{ Exception -> 0x025f }
        goto L_0x039b;
    L_0x02be:
        if (r8 == 0) goto L_0x02d8;
    L_0x02c0:
        if (r9 == 0) goto L_0x02c8;
    L_0x02c2:
        r1 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0264 }
        r1.<init>();	 Catch:{ Exception -> 0x0264 }
        goto L_0x02cd;
    L_0x02c8:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0264 }
        r1.<init>();	 Catch:{ Exception -> 0x0264 }
    L_0x02cd:
        r5 = r1;
        r5.media = r8;	 Catch:{ Exception -> 0x025f }
        r10 = r49;
        r1 = r50;
        r19 = 10;
        goto L_0x0569;
    L_0x02d8:
        r1 = r46;
        if (r1 == 0) goto L_0x0304;
    L_0x02dc:
        if (r9 == 0) goto L_0x02e4;
    L_0x02de:
        r3 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0264 }
        r3.<init>();	 Catch:{ Exception -> 0x0264 }
        goto L_0x02e9;
    L_0x02e4:
        r3 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0264 }
        r3.<init>();	 Catch:{ Exception -> 0x0264 }
    L_0x02e9:
        r5 = r3;
        r5.media = r1;	 Catch:{ Exception -> 0x025f }
        if (r12 == 0) goto L_0x02fc;
    L_0x02ee:
        r3 = r12.containsKey(r4);	 Catch:{ Exception -> 0x025f }
        if (r3 == 0) goto L_0x02fc;
    L_0x02f4:
        r10 = r49;
        r1 = r50;
        r19 = 9;
        goto L_0x0569;
    L_0x02fc:
        r10 = r49;
        r1 = r50;
        r19 = 1;
        goto L_0x0569;
    L_0x0304:
        r3 = r47;
        if (r3 == 0) goto L_0x037d;
    L_0x0308:
        if (r9 == 0) goto L_0x0310;
    L_0x030a:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0264 }
        r5.<init>();	 Catch:{ Exception -> 0x0264 }
        goto L_0x0315;
    L_0x0310:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0264 }
        r5.<init>();	 Catch:{ Exception -> 0x0264 }
    L_0x0315:
        r10 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x025f }
        r10.<init>();	 Catch:{ Exception -> 0x025f }
        r5.media = r10;	 Catch:{ Exception -> 0x025f }
        r10 = r5.media;	 Catch:{ Exception -> 0x025f }
        r1 = r10.flags;	 Catch:{ Exception -> 0x025f }
        r19 = 3;
        r1 = r1 | 3;
        r10.flags = r1;	 Catch:{ Exception -> 0x025f }
        if (r15 == 0) goto L_0x032a;
    L_0x0328:
        r5.entities = r15;	 Catch:{ Exception -> 0x025f }
    L_0x032a:
        if (r6 == 0) goto L_0x033c;
    L_0x032c:
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r1.ttl_seconds = r6;	 Catch:{ Exception -> 0x025f }
        r5.ttl = r6;	 Catch:{ Exception -> 0x025f }
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r10 = r1.flags;	 Catch:{ Exception -> 0x025f }
        r19 = 4;
        r10 = r10 | 4;
        r1.flags = r10;	 Catch:{ Exception -> 0x025f }
    L_0x033c:
        r1 = r5.media;	 Catch:{ Exception -> 0x025f }
        r1.photo = r3;	 Catch:{ Exception -> 0x025f }
        if (r12 == 0) goto L_0x034b;
    L_0x0342:
        r1 = r12.containsKey(r4);	 Catch:{ Exception -> 0x025f }
        if (r1 == 0) goto L_0x034b;
    L_0x0348:
        r19 = 9;
        goto L_0x034d;
    L_0x034b:
        r19 = 2;
    L_0x034d:
        if (r11 == 0) goto L_0x0360;
    L_0x034f:
        r1 = r55.length();	 Catch:{ Exception -> 0x025f }
        if (r1 <= 0) goto L_0x0360;
    L_0x0355:
        r1 = "http";
        r1 = r11.startsWith(r1);	 Catch:{ Exception -> 0x025f }
        if (r1 == 0) goto L_0x0360;
    L_0x035d:
        r5.attachPath = r11;	 Catch:{ Exception -> 0x025f }
        goto L_0x039b;
    L_0x0360:
        r1 = r3.sizes;	 Catch:{ Exception -> 0x025f }
        r10 = r3.sizes;	 Catch:{ Exception -> 0x025f }
        r10 = r10.size();	 Catch:{ Exception -> 0x025f }
        r2 = 1;
        r10 = r10 - r2;
        r1 = r1.get(r10);	 Catch:{ Exception -> 0x025f }
        r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1;	 Catch:{ Exception -> 0x025f }
        r1 = r1.location;	 Catch:{ Exception -> 0x025f }
        r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r2);	 Catch:{ Exception -> 0x025f }
        r1 = r1.toString();	 Catch:{ Exception -> 0x025f }
        r5.attachPath = r1;	 Catch:{ Exception -> 0x025f }
        goto L_0x039b;
    L_0x037d:
        r1 = r51;
        if (r1 == 0) goto L_0x03a1;
    L_0x0381:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0264 }
        r5.<init>();	 Catch:{ Exception -> 0x0264 }
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame;	 Catch:{ Exception -> 0x025f }
        r2.<init>();	 Catch:{ Exception -> 0x025f }
        r5.media = r2;	 Catch:{ Exception -> 0x025f }
        r2 = r5.media;	 Catch:{ Exception -> 0x025f }
        r2.game = r1;	 Catch:{ Exception -> 0x025f }
        if (r12 == 0) goto L_0x039b;
    L_0x0393:
        r1 = r12.containsKey(r4);	 Catch:{ Exception -> 0x025f }
        if (r1 == 0) goto L_0x039b;
    L_0x0399:
        goto L_0x02f4;
    L_0x039b:
        r10 = r49;
    L_0x039d:
        r1 = r50;
        goto L_0x0569;
    L_0x03a1:
        r10 = r49;
        r5 = 0;
        if (r10 == 0) goto L_0x043a;
    L_0x03a6:
        if (r9 == 0) goto L_0x03b5;
    L_0x03a8:
        r1 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x03ae }
        r1.<init>();	 Catch:{ Exception -> 0x03ae }
        goto L_0x03ba;
    L_0x03ae:
        r0 = move-exception;
        r1 = r64;
        r2 = r0;
        r11 = r5;
        goto L_0x1897;
    L_0x03b5:
        r1 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0264 }
        r1.<init>();	 Catch:{ Exception -> 0x0264 }
    L_0x03ba:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact;	 Catch:{ Exception -> 0x0432 }
        r2.<init>();	 Catch:{ Exception -> 0x0432 }
        r1.media = r2;	 Catch:{ Exception -> 0x0432 }
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r5 = r10.phone;	 Catch:{ Exception -> 0x0432 }
        r2.phone_number = r5;	 Catch:{ Exception -> 0x0432 }
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r5 = r10.first_name;	 Catch:{ Exception -> 0x0432 }
        r2.first_name = r5;	 Catch:{ Exception -> 0x0432 }
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r5 = r10.last_name;	 Catch:{ Exception -> 0x0432 }
        r2.last_name = r5;	 Catch:{ Exception -> 0x0432 }
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r5 = r10.id;	 Catch:{ Exception -> 0x0432 }
        r2.user_id = r5;	 Catch:{ Exception -> 0x0432 }
        r2 = r10.restriction_reason;	 Catch:{ Exception -> 0x0432 }
        r2 = r2.isEmpty();	 Catch:{ Exception -> 0x0432 }
        if (r2 != 0) goto L_0x0404;
    L_0x03e1:
        r2 = r10.restriction_reason;	 Catch:{ Exception -> 0x0432 }
        r5 = 0;
        r2 = r2.get(r5);	 Catch:{ Exception -> 0x0432 }
        r2 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r2;	 Catch:{ Exception -> 0x0432 }
        r2 = r2.text;	 Catch:{ Exception -> 0x0432 }
        r5 = "BEGIN:VCARD";
        r2 = r2.startsWith(r5);	 Catch:{ Exception -> 0x0432 }
        if (r2 == 0) goto L_0x0404;
    L_0x03f4:
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r5 = r10.restriction_reason;	 Catch:{ Exception -> 0x0432 }
        r3 = 0;
        r5 = r5.get(r3);	 Catch:{ Exception -> 0x0432 }
        r5 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r5;	 Catch:{ Exception -> 0x0432 }
        r3 = r5.text;	 Catch:{ Exception -> 0x0432 }
        r2.vcard = r3;	 Catch:{ Exception -> 0x0432 }
        goto L_0x0408;
    L_0x0404:
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r2.vcard = r7;	 Catch:{ Exception -> 0x0432 }
    L_0x0408:
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r2 = r2.first_name;	 Catch:{ Exception -> 0x0432 }
        if (r2 != 0) goto L_0x0414;
    L_0x040e:
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r2.first_name = r7;	 Catch:{ Exception -> 0x0432 }
        r10.first_name = r7;	 Catch:{ Exception -> 0x0432 }
    L_0x0414:
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r2 = r2.last_name;	 Catch:{ Exception -> 0x0432 }
        if (r2 != 0) goto L_0x0420;
    L_0x041a:
        r2 = r1.media;	 Catch:{ Exception -> 0x0432 }
        r2.last_name = r7;	 Catch:{ Exception -> 0x0432 }
        r10.last_name = r7;	 Catch:{ Exception -> 0x0432 }
    L_0x0420:
        if (r12 == 0) goto L_0x042d;
    L_0x0422:
        r2 = r12.containsKey(r4);	 Catch:{ Exception -> 0x0432 }
        if (r2 == 0) goto L_0x042d;
    L_0x0428:
        r5 = r1;
        r19 = 9;
        goto L_0x039d;
    L_0x042d:
        r19 = 6;
        r5 = r1;
        goto L_0x039d;
    L_0x0432:
        r0 = move-exception;
        r2 = r0;
        r5 = r1;
        r11 = 0;
        r1 = r64;
        goto L_0x1897;
    L_0x043a:
        r1 = r50;
        if (r1 == 0) goto L_0x0566;
    L_0x043e:
        if (r9 == 0) goto L_0x0446;
    L_0x0440:
        r2 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x0264 }
        r2.<init>();	 Catch:{ Exception -> 0x0264 }
        goto L_0x044b;
    L_0x0446:
        r2 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0264 }
        r2.<init>();	 Catch:{ Exception -> 0x0264 }
    L_0x044b:
        r5 = r2;
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0561 }
        r2.<init>();	 Catch:{ Exception -> 0x0561 }
        r5.media = r2;	 Catch:{ Exception -> 0x0561 }
        r2 = r5.media;	 Catch:{ Exception -> 0x0561 }
        r3 = r2.flags;	 Catch:{ Exception -> 0x0561 }
        r19 = 3;
        r3 = r3 | 3;
        r2.flags = r3;	 Catch:{ Exception -> 0x0561 }
        if (r6 == 0) goto L_0x046f;
    L_0x045f:
        r2 = r5.media;	 Catch:{ Exception -> 0x025f }
        r2.ttl_seconds = r6;	 Catch:{ Exception -> 0x025f }
        r5.ttl = r6;	 Catch:{ Exception -> 0x025f }
        r2 = r5.media;	 Catch:{ Exception -> 0x025f }
        r3 = r2.flags;	 Catch:{ Exception -> 0x025f }
        r19 = 4;
        r3 = r3 | 4;
        r2.flags = r3;	 Catch:{ Exception -> 0x025f }
    L_0x046f:
        r2 = r5.media;	 Catch:{ Exception -> 0x0561 }
        r2.document = r1;	 Catch:{ Exception -> 0x0561 }
        if (r12 == 0) goto L_0x047e;
    L_0x0475:
        r2 = r12.containsKey(r4);	 Catch:{ Exception -> 0x025f }
        if (r2 == 0) goto L_0x047e;
    L_0x047b:
        r19 = 9;
        goto L_0x049c;
    L_0x047e:
        r2 = org.telegram.messenger.MessageObject.isVideoDocument(r50);	 Catch:{ Exception -> 0x0561 }
        if (r2 != 0) goto L_0x049a;
    L_0x0484:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r50);	 Catch:{ Exception -> 0x025f }
        if (r2 != 0) goto L_0x049a;
    L_0x048a:
        if (r48 == 0) goto L_0x048d;
    L_0x048c:
        goto L_0x049a;
    L_0x048d:
        r2 = org.telegram.messenger.MessageObject.isVoiceDocument(r50);	 Catch:{ Exception -> 0x025f }
        if (r2 == 0) goto L_0x0496;
    L_0x0493:
        r19 = 8;
        goto L_0x049c;
    L_0x0496:
        r2 = 7;
        r19 = 7;
        goto L_0x049c;
    L_0x049a:
        r19 = 3;
    L_0x049c:
        if (r48 == 0) goto L_0x04b0;
    L_0x049e:
        r2 = r48.getString();	 Catch:{ Exception -> 0x025f }
        if (r12 != 0) goto L_0x04aa;
    L_0x04a4:
        r3 = new java.util.HashMap;	 Catch:{ Exception -> 0x025f }
        r3.<init>();	 Catch:{ Exception -> 0x025f }
        r12 = r3;
    L_0x04aa:
        r3 = "ve";
        r12.put(r3, r2);	 Catch:{ Exception -> 0x025f }
    L_0x04b0:
        if (r9 == 0) goto L_0x04c7;
    L_0x04b2:
        r2 = r1.dc_id;	 Catch:{ Exception -> 0x025f }
        if (r2 <= 0) goto L_0x04c7;
    L_0x04b6:
        r2 = org.telegram.messenger.MessageObject.isStickerDocument(r50);	 Catch:{ Exception -> 0x025f }
        if (r2 != 0) goto L_0x04c7;
    L_0x04bc:
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r50);	 Catch:{ Exception -> 0x025f }
        r2 = r2.toString();	 Catch:{ Exception -> 0x025f }
        r5.attachPath = r2;	 Catch:{ Exception -> 0x025f }
        goto L_0x04c9;
    L_0x04c7:
        r5.attachPath = r11;	 Catch:{ Exception -> 0x0561 }
    L_0x04c9:
        if (r9 == 0) goto L_0x0556;
    L_0x04cb:
        r2 = org.telegram.messenger.MessageObject.isStickerDocument(r50);	 Catch:{ Exception -> 0x0561 }
        if (r2 != 0) goto L_0x04d8;
    L_0x04d1:
        r2 = 0;
        r3 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r2);	 Catch:{ Exception -> 0x025f }
        if (r3 == 0) goto L_0x0556;
    L_0x04d8:
        r2 = 0;
    L_0x04d9:
        r3 = r1.attributes;	 Catch:{ Exception -> 0x0561 }
        r3 = r3.size();	 Catch:{ Exception -> 0x0561 }
        if (r2 >= r3) goto L_0x0556;
    L_0x04e1:
        r3 = r1.attributes;	 Catch:{ Exception -> 0x0561 }
        r3 = r3.get(r2);	 Catch:{ Exception -> 0x0561 }
        r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3;	 Catch:{ Exception -> 0x0561 }
        r22 = r5;
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;	 Catch:{ Exception -> 0x054e }
        if (r5 == 0) goto L_0x0545;
    L_0x04ef:
        r5 = r1.attributes;	 Catch:{ Exception -> 0x054e }
        r5.remove(r2);	 Catch:{ Exception -> 0x054e }
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55;	 Catch:{ Exception -> 0x054e }
        r2.<init>();	 Catch:{ Exception -> 0x054e }
        r5 = r1.attributes;	 Catch:{ Exception -> 0x054e }
        r5.add(r2);	 Catch:{ Exception -> 0x054e }
        r5 = r3.alt;	 Catch:{ Exception -> 0x054e }
        r2.alt = r5;	 Catch:{ Exception -> 0x054e }
        r5 = r3.stickerset;	 Catch:{ Exception -> 0x054e }
        if (r5 == 0) goto L_0x053b;
    L_0x0506:
        r5 = r3.stickerset;	 Catch:{ Exception -> 0x054e }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x054e }
        if (r5 == 0) goto L_0x0513;
    L_0x050c:
        r3 = r3.stickerset;	 Catch:{ Exception -> 0x054e }
        r3 = r3.short_name;	 Catch:{ Exception -> 0x054e }
        r51 = r12;
        goto L_0x0521;
    L_0x0513:
        r5 = r43.getMediaDataController();	 Catch:{ Exception -> 0x054e }
        r3 = r3.stickerset;	 Catch:{ Exception -> 0x054e }
        r51 = r12;
        r12 = r3.id;	 Catch:{ Exception -> 0x054e }
        r3 = r5.getStickerSetName(r12);	 Catch:{ Exception -> 0x054e }
    L_0x0521:
        r5 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Exception -> 0x054e }
        if (r5 != 0) goto L_0x0533;
    L_0x0527:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x054e }
        r5.<init>();	 Catch:{ Exception -> 0x054e }
        r2.stickerset = r5;	 Catch:{ Exception -> 0x054e }
        r2 = r2.stickerset;	 Catch:{ Exception -> 0x054e }
        r2.short_name = r3;	 Catch:{ Exception -> 0x054e }
        goto L_0x055a;
    L_0x0533:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x054e }
        r3.<init>();	 Catch:{ Exception -> 0x054e }
        r2.stickerset = r3;	 Catch:{ Exception -> 0x054e }
        goto L_0x055a;
    L_0x053b:
        r51 = r12;
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x054e }
        r3.<init>();	 Catch:{ Exception -> 0x054e }
        r2.stickerset = r3;	 Catch:{ Exception -> 0x054e }
        goto L_0x055a;
    L_0x0545:
        r51 = r12;
        r2 = r2 + 1;
        r13 = r57;
        r5 = r22;
        goto L_0x04d9;
    L_0x054e:
        r0 = move-exception;
        r1 = r64;
        r2 = r0;
        r5 = r22;
        goto L_0x0269;
    L_0x0556:
        r22 = r5;
        r51 = r12;
    L_0x055a:
        r12 = r51;
        r13 = r57;
        r5 = r22;
        goto L_0x0569;
    L_0x0561:
        r0 = move-exception;
        r22 = r5;
        goto L_0x0260;
    L_0x0566:
        r13 = r57;
        r5 = 0;
    L_0x0569:
        if (r15 == 0) goto L_0x0579;
    L_0x056b:
        r2 = r60.isEmpty();	 Catch:{ Exception -> 0x025f }
        if (r2 != 0) goto L_0x0579;
    L_0x0571:
        r5.entities = r15;	 Catch:{ Exception -> 0x025f }
        r2 = r5.flags;	 Catch:{ Exception -> 0x025f }
        r2 = r2 | 128;
        r5.flags = r2;	 Catch:{ Exception -> 0x025f }
    L_0x0579:
        r2 = r18;
        if (r2 == 0) goto L_0x0580;
    L_0x057d:
        r5.message = r2;	 Catch:{ Exception -> 0x025f }
        goto L_0x0586;
    L_0x0580:
        r3 = r5.message;	 Catch:{ Exception -> 0x1890 }
        if (r3 != 0) goto L_0x0586;
    L_0x0584:
        r5.message = r7;	 Catch:{ Exception -> 0x025f }
    L_0x0586:
        r3 = r5.attachPath;	 Catch:{ Exception -> 0x1890 }
        if (r3 != 0) goto L_0x058c;
    L_0x058a:
        r5.attachPath = r7;	 Catch:{ Exception -> 0x025f }
    L_0x058c:
        r3 = r43.getUserConfig();	 Catch:{ Exception -> 0x1890 }
        r3 = r3.getNewMessageId();	 Catch:{ Exception -> 0x1890 }
        r5.id = r3;	 Catch:{ Exception -> 0x1890 }
        r5.local_id = r3;	 Catch:{ Exception -> 0x1890 }
        r3 = 1;
        r5.out = r3;	 Catch:{ Exception -> 0x1890 }
        if (r29 == 0) goto L_0x05a7;
    L_0x059d:
        if (r28 == 0) goto L_0x05a7;
    L_0x059f:
        r3 = r28;
        r1 = r3.channel_id;	 Catch:{ Exception -> 0x025f }
        r1 = -r1;
        r5.from_id = r1;	 Catch:{ Exception -> 0x025f }
        goto L_0x05b9;
    L_0x05a7:
        r3 = r28;
        r1 = r43.getUserConfig();	 Catch:{ Exception -> 0x1890 }
        r1 = r1.getClientUserId();	 Catch:{ Exception -> 0x1890 }
        r5.from_id = r1;	 Catch:{ Exception -> 0x1890 }
        r1 = r5.flags;	 Catch:{ Exception -> 0x1890 }
        r1 = r1 | 256;
        r5.flags = r1;	 Catch:{ Exception -> 0x1890 }
    L_0x05b9:
        r1 = r43.getUserConfig();	 Catch:{ Exception -> 0x1890 }
        r18 = r5;
        r5 = 0;
        r1.saveConfig(r5);	 Catch:{ Exception -> 0x1888 }
        r1 = r44;
        r44 = r47;
        r51 = r2;
        r2 = r8;
        r32 = r13;
        r5 = r18;
        r15 = r19;
        r8 = r46;
        r18 = r66;
        r19 = r4;
        r13 = r10;
        goto L_0x0100;
    L_0x05d9:
        r10 = r5.random_id;	 Catch:{ Exception -> 0x025f }
        r22 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1));
        if (r22 != 0) goto L_0x05e5;
    L_0x05df:
        r10 = r43.getNextRandomId();	 Catch:{ Exception -> 0x025f }
        r5.random_id = r10;	 Catch:{ Exception -> 0x025f }
    L_0x05e5:
        if (r12 == 0) goto L_0x061a;
    L_0x05e7:
        r10 = "bot";
        r10 = r12.containsKey(r10);	 Catch:{ Exception -> 0x025f }
        if (r10 == 0) goto L_0x061a;
    L_0x05ef:
        if (r9 == 0) goto L_0x0602;
    L_0x05f1:
        r10 = "bot_name";
        r10 = r12.get(r10);	 Catch:{ Exception -> 0x025f }
        r10 = (java.lang.String) r10;	 Catch:{ Exception -> 0x025f }
        r5.via_bot_name = r10;	 Catch:{ Exception -> 0x025f }
        r10 = r5.via_bot_name;	 Catch:{ Exception -> 0x025f }
        if (r10 != 0) goto L_0x0614;
    L_0x05ff:
        r5.via_bot_name = r7;	 Catch:{ Exception -> 0x025f }
        goto L_0x0614;
    L_0x0602:
        r10 = "bot";
        r10 = r12.get(r10);	 Catch:{ Exception -> 0x025f }
        r10 = (java.lang.CharSequence) r10;	 Catch:{ Exception -> 0x025f }
        r10 = org.telegram.messenger.Utilities.parseInt(r10);	 Catch:{ Exception -> 0x025f }
        r10 = r10.intValue();	 Catch:{ Exception -> 0x025f }
        r5.via_bot_id = r10;	 Catch:{ Exception -> 0x025f }
    L_0x0614:
        r10 = r5.flags;	 Catch:{ Exception -> 0x025f }
        r10 = r10 | 2048;
        r5.flags = r10;	 Catch:{ Exception -> 0x025f }
    L_0x061a:
        r5.params = r12;	 Catch:{ Exception -> 0x025f }
        if (r14 == 0) goto L_0x0626;
    L_0x061e:
        r10 = r14.resendAsIs;	 Catch:{ Exception -> 0x025f }
        if (r10 != 0) goto L_0x0623;
    L_0x0622:
        goto L_0x0626;
    L_0x0623:
        r10 = r64;
        goto L_0x0680;
    L_0x0626:
        r10 = r64;
        if (r10 == 0) goto L_0x062c;
    L_0x062a:
        r11 = r10;
        goto L_0x0634;
    L_0x062c:
        r11 = r43.getConnectionsManager();	 Catch:{ Exception -> 0x1884 }
        r11 = r11.getCurrentTime();	 Catch:{ Exception -> 0x1884 }
    L_0x0634:
        r5.date = r11;	 Catch:{ Exception -> 0x1884 }
        r11 = r3 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;	 Catch:{ Exception -> 0x1884 }
        if (r11 == 0) goto L_0x067d;
    L_0x063a:
        if (r10 != 0) goto L_0x0647;
    L_0x063c:
        if (r29 == 0) goto L_0x0647;
    L_0x063e:
        r11 = 1;
        r5.views = r11;	 Catch:{ Exception -> 0x0678 }
        r11 = r5.flags;	 Catch:{ Exception -> 0x0678 }
        r11 = r11 | 1024;
        r5.flags = r11;	 Catch:{ Exception -> 0x0678 }
    L_0x0647:
        r11 = r43.getMessagesController();	 Catch:{ Exception -> 0x0678 }
        r14 = r3.channel_id;	 Catch:{ Exception -> 0x0678 }
        r14 = java.lang.Integer.valueOf(r14);	 Catch:{ Exception -> 0x0678 }
        r11 = r11.getChat(r14);	 Catch:{ Exception -> 0x0678 }
        if (r11 == 0) goto L_0x0680;
    L_0x0657:
        r14 = r11.megagroup;	 Catch:{ Exception -> 0x0678 }
        if (r14 == 0) goto L_0x0666;
    L_0x065b:
        r11 = r5.flags;	 Catch:{ Exception -> 0x0678 }
        r14 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r11 = r11 | r14;
        r5.flags = r11;	 Catch:{ Exception -> 0x0678 }
        r14 = 1;
        r5.unread = r14;	 Catch:{ Exception -> 0x0678 }
        goto L_0x0680;
    L_0x0666:
        r14 = 1;
        r5.post = r14;	 Catch:{ Exception -> 0x0678 }
        r11 = r11.signatures;	 Catch:{ Exception -> 0x0678 }
        if (r11 == 0) goto L_0x0680;
    L_0x066d:
        r11 = r43.getUserConfig();	 Catch:{ Exception -> 0x0678 }
        r11 = r11.getClientUserId();	 Catch:{ Exception -> 0x0678 }
        r5.from_id = r11;	 Catch:{ Exception -> 0x0678 }
        goto L_0x0680;
    L_0x0678:
        r0 = move-exception;
    L_0x0679:
        r2 = r0;
        r1 = r10;
        goto L_0x0269;
    L_0x067d:
        r11 = 1;
        r5.unread = r11;	 Catch:{ Exception -> 0x1884 }
    L_0x0680:
        r11 = r5.flags;	 Catch:{ Exception -> 0x1884 }
        r11 = r11 | 512;
        r5.flags = r11;	 Catch:{ Exception -> 0x1884 }
        r46 = r13;
        r11 = 2;
        r13 = r53;
        r5.dialog_id = r13;	 Catch:{ Exception -> 0x1884 }
        r11 = r56;
        if (r11 == 0) goto L_0x06c3;
    L_0x0691:
        if (r9 == 0) goto L_0x06af;
    L_0x0693:
        r26 = r2;
        r2 = r11.messageOwner;	 Catch:{ Exception -> 0x0678 }
        r28 = r7;
        r29 = r8;
        r7 = r2.random_id;	 Catch:{ Exception -> 0x0678 }
        r2 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x06b5;
    L_0x06a1:
        r2 = r11.messageOwner;	 Catch:{ Exception -> 0x0678 }
        r7 = r2.random_id;	 Catch:{ Exception -> 0x0678 }
        r5.reply_to_random_id = r7;	 Catch:{ Exception -> 0x0678 }
        r2 = r5.flags;	 Catch:{ Exception -> 0x0678 }
        r7 = 8;
        r2 = r2 | r7;
        r5.flags = r2;	 Catch:{ Exception -> 0x0678 }
        goto L_0x06bc;
    L_0x06af:
        r26 = r2;
        r28 = r7;
        r29 = r8;
    L_0x06b5:
        r2 = r5.flags;	 Catch:{ Exception -> 0x0678 }
        r7 = 8;
        r2 = r2 | r7;
        r5.flags = r2;	 Catch:{ Exception -> 0x0678 }
    L_0x06bc:
        r2 = r56.getId();	 Catch:{ Exception -> 0x0678 }
        r5.reply_to_msg_id = r2;	 Catch:{ Exception -> 0x0678 }
        goto L_0x06c9;
    L_0x06c3:
        r26 = r2;
        r28 = r7;
        r29 = r8;
    L_0x06c9:
        r2 = r61;
        if (r2 == 0) goto L_0x06d7;
    L_0x06cd:
        if (r9 != 0) goto L_0x06d7;
    L_0x06cf:
        r7 = r5.flags;	 Catch:{ Exception -> 0x0678 }
        r7 = r7 | 64;
        r5.flags = r7;	 Catch:{ Exception -> 0x0678 }
        r5.reply_markup = r2;	 Catch:{ Exception -> 0x0678 }
    L_0x06d7:
        if (r25 == 0) goto L_0x071b;
    L_0x06d9:
        r2 = r43.getMessagesController();	 Catch:{ Exception -> 0x0716 }
        r7 = r25;
        r2 = r2.getPeer(r7);	 Catch:{ Exception -> 0x0716 }
        r5.to_id = r2;	 Catch:{ Exception -> 0x0716 }
        if (r7 <= 0) goto L_0x070d;
    L_0x06e7:
        r2 = r43.getMessagesController();	 Catch:{ Exception -> 0x0716 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x0716 }
        r2 = r2.getUser(r7);	 Catch:{ Exception -> 0x0716 }
        if (r2 != 0) goto L_0x06fd;
    L_0x06f5:
        r1 = r5.id;	 Catch:{ Exception -> 0x0716 }
        r7 = r43;
        r7.processSentMessage(r1);	 Catch:{ Exception -> 0x0678 }
        return;
    L_0x06fd:
        r7 = r43;
        r8 = r27;
        r2 = r2.bot;	 Catch:{ Exception -> 0x0678 }
        if (r2 == 0) goto L_0x0708;
    L_0x0705:
        r2 = 0;
        r5.unread = r2;	 Catch:{ Exception -> 0x0678 }
    L_0x0708:
        r47 = r6;
        r27 = r8;
        goto L_0x0711;
    L_0x070d:
        r7 = r43;
    L_0x070f:
        r47 = r6;
    L_0x0711:
        r2 = r23;
        r6 = 1;
        goto L_0x07e6;
    L_0x0716:
        r0 = move-exception;
        r7 = r43;
        goto L_0x0679;
    L_0x071b:
        r7 = r43;
        r8 = r27;
        r2 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Exception -> 0x1884 }
        r2.<init>();	 Catch:{ Exception -> 0x1884 }
        r5.to_id = r2;	 Catch:{ Exception -> 0x1884 }
        r2 = r9.participant_id;	 Catch:{ Exception -> 0x1884 }
        r25 = r43.getUserConfig();	 Catch:{ Exception -> 0x1884 }
        r27 = r8;
        r8 = r25.getClientUserId();	 Catch:{ Exception -> 0x1884 }
        if (r2 != r8) goto L_0x073b;
    L_0x0734:
        r2 = r5.to_id;	 Catch:{ Exception -> 0x0678 }
        r8 = r9.admin_id;	 Catch:{ Exception -> 0x0678 }
        r2.user_id = r8;	 Catch:{ Exception -> 0x0678 }
        goto L_0x0741;
    L_0x073b:
        r2 = r5.to_id;	 Catch:{ Exception -> 0x1884 }
        r8 = r9.participant_id;	 Catch:{ Exception -> 0x1884 }
        r2.user_id = r8;	 Catch:{ Exception -> 0x1884 }
    L_0x0741:
        if (r6 == 0) goto L_0x0746;
    L_0x0743:
        r5.ttl = r6;	 Catch:{ Exception -> 0x0678 }
        goto L_0x0762;
    L_0x0746:
        r2 = r9.ttl;	 Catch:{ Exception -> 0x1884 }
        r5.ttl = r2;	 Catch:{ Exception -> 0x1884 }
        r2 = r5.ttl;	 Catch:{ Exception -> 0x1884 }
        if (r2 == 0) goto L_0x0762;
    L_0x074e:
        r2 = r5.media;	 Catch:{ Exception -> 0x0678 }
        if (r2 == 0) goto L_0x0762;
    L_0x0752:
        r2 = r5.media;	 Catch:{ Exception -> 0x0678 }
        r8 = r5.ttl;	 Catch:{ Exception -> 0x0678 }
        r2.ttl_seconds = r8;	 Catch:{ Exception -> 0x0678 }
        r2 = r5.media;	 Catch:{ Exception -> 0x0678 }
        r8 = r2.flags;	 Catch:{ Exception -> 0x0678 }
        r24 = 4;
        r8 = r8 | 4;
        r2.flags = r8;	 Catch:{ Exception -> 0x0678 }
    L_0x0762:
        r2 = r5.ttl;	 Catch:{ Exception -> 0x1884 }
        if (r2 == 0) goto L_0x070f;
    L_0x0766:
        r2 = r5.media;	 Catch:{ Exception -> 0x0678 }
        r2 = r2.document;	 Catch:{ Exception -> 0x0678 }
        if (r2 == 0) goto L_0x070f;
    L_0x076c:
        r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r5);	 Catch:{ Exception -> 0x0678 }
        if (r2 == 0) goto L_0x07a8;
    L_0x0772:
        r2 = 0;
    L_0x0773:
        r8 = r5.media;	 Catch:{ Exception -> 0x0678 }
        r8 = r8.document;	 Catch:{ Exception -> 0x0678 }
        r8 = r8.attributes;	 Catch:{ Exception -> 0x0678 }
        r8 = r8.size();	 Catch:{ Exception -> 0x0678 }
        if (r2 >= r8) goto L_0x0799;
    L_0x077f:
        r8 = r5.media;	 Catch:{ Exception -> 0x0678 }
        r8 = r8.document;	 Catch:{ Exception -> 0x0678 }
        r8 = r8.attributes;	 Catch:{ Exception -> 0x0678 }
        r8 = r8.get(r2);	 Catch:{ Exception -> 0x0678 }
        r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8;	 Catch:{ Exception -> 0x0678 }
        r47 = r6;
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;	 Catch:{ Exception -> 0x0678 }
        if (r6 == 0) goto L_0x0794;
    L_0x0791:
        r2 = r8.duration;	 Catch:{ Exception -> 0x0678 }
        goto L_0x079c;
    L_0x0794:
        r2 = r2 + 1;
        r6 = r47;
        goto L_0x0773;
    L_0x0799:
        r47 = r6;
        r2 = 0;
    L_0x079c:
        r6 = r5.ttl;	 Catch:{ Exception -> 0x0678 }
        r8 = 1;
        r2 = r2 + r8;
        r2 = java.lang.Math.max(r6, r2);	 Catch:{ Exception -> 0x0678 }
        r5.ttl = r2;	 Catch:{ Exception -> 0x0678 }
        goto L_0x0711;
    L_0x07a8:
        r47 = r6;
        r2 = org.telegram.messenger.MessageObject.isVideoMessage(r5);	 Catch:{ Exception -> 0x0678 }
        if (r2 != 0) goto L_0x07b6;
    L_0x07b0:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r5);	 Catch:{ Exception -> 0x0678 }
        if (r2 == 0) goto L_0x0711;
    L_0x07b6:
        r2 = 0;
    L_0x07b7:
        r6 = r5.media;	 Catch:{ Exception -> 0x0678 }
        r6 = r6.document;	 Catch:{ Exception -> 0x0678 }
        r6 = r6.attributes;	 Catch:{ Exception -> 0x0678 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0678 }
        if (r2 >= r6) goto L_0x07d9;
    L_0x07c3:
        r6 = r5.media;	 Catch:{ Exception -> 0x0678 }
        r6 = r6.document;	 Catch:{ Exception -> 0x0678 }
        r6 = r6.attributes;	 Catch:{ Exception -> 0x0678 }
        r6 = r6.get(r2);	 Catch:{ Exception -> 0x0678 }
        r6 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r6;	 Catch:{ Exception -> 0x0678 }
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x0678 }
        if (r8 == 0) goto L_0x07d6;
    L_0x07d3:
        r2 = r6.duration;	 Catch:{ Exception -> 0x0678 }
        goto L_0x07da;
    L_0x07d6:
        r2 = r2 + 1;
        goto L_0x07b7;
    L_0x07d9:
        r2 = 0;
    L_0x07da:
        r6 = r5.ttl;	 Catch:{ Exception -> 0x0678 }
        r8 = 1;
        r2 = r2 + r8;
        r2 = java.lang.Math.max(r6, r2);	 Catch:{ Exception -> 0x0678 }
        r5.ttl = r2;	 Catch:{ Exception -> 0x0678 }
        goto L_0x0711;
    L_0x07e6:
        if (r2 == r6) goto L_0x07f8;
    L_0x07e8:
        r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r5);	 Catch:{ Exception -> 0x0678 }
        if (r2 != 0) goto L_0x07f4;
    L_0x07ee:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r5);	 Catch:{ Exception -> 0x0678 }
        if (r2 == 0) goto L_0x07f8;
    L_0x07f4:
        r2 = 1;
        r5.media_unread = r2;	 Catch:{ Exception -> 0x0678 }
        goto L_0x07f9;
    L_0x07f8:
        r2 = 1;
    L_0x07f9:
        r5.send_state = r2;	 Catch:{ Exception -> 0x1884 }
        r6 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x1884 }
        r8 = r7.currentAccount;	 Catch:{ Exception -> 0x1884 }
        r6.<init>(r8, r5, r11, r2);	 Catch:{ Exception -> 0x1884 }
        if (r10 == 0) goto L_0x0806;
    L_0x0804:
        r2 = 1;
        goto L_0x0807;
    L_0x0806:
        r2 = 0;
    L_0x0807:
        r6.scheduled = r2;	 Catch:{ Exception -> 0x187f }
        r2 = r6.isForwarded();	 Catch:{ Exception -> 0x187f }
        if (r2 != 0) goto L_0x082d;
    L_0x080f:
        r2 = r6.type;	 Catch:{ Exception -> 0x0827 }
        r8 = 3;
        if (r2 == r8) goto L_0x081b;
    L_0x0814:
        if (r48 != 0) goto L_0x081b;
    L_0x0816:
        r2 = r6.type;	 Catch:{ Exception -> 0x0827 }
        r8 = 2;
        if (r2 != r8) goto L_0x082d;
    L_0x081b:
        r2 = r5.attachPath;	 Catch:{ Exception -> 0x0827 }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x0827 }
        if (r2 != 0) goto L_0x082d;
    L_0x0823:
        r2 = 1;
        r6.attachPathExists = r2;	 Catch:{ Exception -> 0x0827 }
        goto L_0x082d;
    L_0x0827:
        r0 = move-exception;
        r2 = r0;
        r11 = r6;
    L_0x082a:
        r1 = r10;
        goto L_0x1897;
    L_0x082d:
        r2 = r6.videoEditedInfo;	 Catch:{ Exception -> 0x187f }
        if (r2 == 0) goto L_0x0836;
    L_0x0831:
        if (r48 != 0) goto L_0x0836;
    L_0x0833:
        r2 = r6.videoEditedInfo;	 Catch:{ Exception -> 0x0827 }
        goto L_0x0838;
    L_0x0836:
        r2 = r48;
    L_0x0838:
        if (r12 == 0) goto L_0x086a;
    L_0x083a:
        r8 = "groupId";
        r8 = r12.get(r8);	 Catch:{ Exception -> 0x0827 }
        r8 = (java.lang.String) r8;	 Catch:{ Exception -> 0x0827 }
        if (r8 == 0) goto L_0x085a;
    L_0x0844:
        r8 = org.telegram.messenger.Utilities.parseLong(r8);	 Catch:{ Exception -> 0x0827 }
        r49 = r1;
        r48 = r2;
        r1 = r8.longValue();	 Catch:{ Exception -> 0x0827 }
        r5.grouped_id = r1;	 Catch:{ Exception -> 0x0827 }
        r8 = r5.flags;	 Catch:{ Exception -> 0x0827 }
        r11 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r8 = r8 | r11;
        r5.flags = r8;	 Catch:{ Exception -> 0x0827 }
        goto L_0x0860;
    L_0x085a:
        r49 = r1;
        r48 = r2;
        r1 = r16;
    L_0x0860:
        r8 = "final";
        r8 = r12.get(r8);	 Catch:{ Exception -> 0x0827 }
        if (r8 == 0) goto L_0x0870;
    L_0x0868:
        r8 = 1;
        goto L_0x0871;
    L_0x086a:
        r49 = r1;
        r48 = r2;
        r1 = r16;
    L_0x0870:
        r8 = 0;
    L_0x0871:
        r11 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1));
        if (r11 != 0) goto L_0x08cf;
    L_0x0875:
        r8 = new java.util.ArrayList;	 Catch:{ Exception -> 0x08c7 }
        r8.<init>();	 Catch:{ Exception -> 0x08c7 }
        r8.add(r6);	 Catch:{ Exception -> 0x08c7 }
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x08c7 }
        r11.<init>();	 Catch:{ Exception -> 0x08c7 }
        r11.add(r5);	 Catch:{ Exception -> 0x08c7 }
        r50 = r12;
        r12 = r7.currentAccount;	 Catch:{ Exception -> 0x08c7 }
        r33 = org.telegram.messenger.MessagesStorage.getInstance(r12);	 Catch:{ Exception -> 0x08c7 }
        r35 = 0;
        r36 = 1;
        r37 = 0;
        r38 = 0;
        if (r10 == 0) goto L_0x089a;
    L_0x0897:
        r39 = 1;
        goto L_0x089c;
    L_0x089a:
        r39 = 0;
    L_0x089c:
        r34 = r11;
        r33.putMessages(r34, r35, r36, r37, r38, r39);	 Catch:{ Exception -> 0x08c7 }
        r11 = r7.currentAccount;	 Catch:{ Exception -> 0x08c7 }
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);	 Catch:{ Exception -> 0x08c7 }
        if (r10 == 0) goto L_0x08ab;
    L_0x08a9:
        r12 = 1;
        goto L_0x08ac;
    L_0x08ab:
        r12 = 0;
    L_0x08ac:
        r11.updateInterfaceWithMessages(r13, r8, r12);	 Catch:{ Exception -> 0x08c7 }
        if (r10 != 0) goto L_0x08c2;
    L_0x08b1:
        r8 = r7.currentAccount;	 Catch:{ Exception -> 0x08c7 }
        r8 = org.telegram.messenger.NotificationCenter.getInstance(r8);	 Catch:{ Exception -> 0x08c7 }
        r11 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;	 Catch:{ Exception -> 0x08c7 }
        r57 = r6;
        r12 = 0;
        r6 = new java.lang.Object[r12];	 Catch:{ Exception -> 0x08f6 }
        r8.postNotificationName(r11, r6);	 Catch:{ Exception -> 0x08f6 }
        goto L_0x08c4;
    L_0x08c2:
        r57 = r6;
    L_0x08c4:
        r6 = 0;
        r11 = 0;
        goto L_0x091c;
    L_0x08c7:
        r0 = move-exception;
        r57 = r6;
    L_0x08ca:
        r11 = r57;
    L_0x08cc:
        r2 = r0;
        goto L_0x082a;
    L_0x08cf:
        r57 = r6;
        r50 = r12;
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x187b }
        r6.<init>();	 Catch:{ Exception -> 0x187b }
        r11 = "group_";
        r6.append(r11);	 Catch:{ Exception -> 0x187b }
        r6.append(r1);	 Catch:{ Exception -> 0x187b }
        r6 = r6.toString();	 Catch:{ Exception -> 0x187b }
        r11 = r7.delayedMessages;	 Catch:{ Exception -> 0x187b }
        r6 = r11.get(r6);	 Catch:{ Exception -> 0x187b }
        r6 = (java.util.ArrayList) r6;	 Catch:{ Exception -> 0x187b }
        if (r6 == 0) goto L_0x08f8;
    L_0x08ee:
        r11 = 0;
        r6 = r6.get(r11);	 Catch:{ Exception -> 0x08f6 }
        r6 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r6;	 Catch:{ Exception -> 0x08f6 }
        goto L_0x08f9;
    L_0x08f6:
        r0 = move-exception;
        goto L_0x08ca;
    L_0x08f8:
        r6 = 0;
    L_0x08f9:
        if (r6 != 0) goto L_0x090c;
    L_0x08fb:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f6 }
        r6.<init>(r13);	 Catch:{ Exception -> 0x08f6 }
        r6.initForGroup(r1);	 Catch:{ Exception -> 0x08f6 }
        r6.encryptedChat = r9;	 Catch:{ Exception -> 0x08f6 }
        if (r10 == 0) goto L_0x0909;
    L_0x0907:
        r11 = 1;
        goto L_0x090a;
    L_0x0909:
        r11 = 0;
    L_0x090a:
        r6.scheduled = r11;	 Catch:{ Exception -> 0x08f6 }
    L_0x090c:
        r11 = 0;
        r6.performMediaUpload = r11;	 Catch:{ Exception -> 0x187b }
        r11 = 0;
        r6.photoSize = r11;	 Catch:{ Exception -> 0x187b }
        r6.videoEditedInfo = r11;	 Catch:{ Exception -> 0x187b }
        r6.httpLocation = r11;	 Catch:{ Exception -> 0x187b }
        if (r8 == 0) goto L_0x091c;
    L_0x0918:
        r8 = r5.id;	 Catch:{ Exception -> 0x08f6 }
        r6.finalGroupMessage = r8;	 Catch:{ Exception -> 0x08f6 }
    L_0x091c:
        r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x187b }
        r12 = "silent_";
        if (r8 == 0) goto L_0x0989;
    L_0x0922:
        if (r3 == 0) goto L_0x0989;
    L_0x0924:
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x08f6 }
        r8.<init>();	 Catch:{ Exception -> 0x08f6 }
        r11 = "send message user_id = ";
        r8.append(r11);	 Catch:{ Exception -> 0x08f6 }
        r11 = r3.user_id;	 Catch:{ Exception -> 0x08f6 }
        r8.append(r11);	 Catch:{ Exception -> 0x08f6 }
        r11 = " chat_id = ";
        r8.append(r11);	 Catch:{ Exception -> 0x08f6 }
        r11 = r3.chat_id;	 Catch:{ Exception -> 0x08f6 }
        r8.append(r11);	 Catch:{ Exception -> 0x08f6 }
        r11 = " channel_id = ";
        r8.append(r11);	 Catch:{ Exception -> 0x08f6 }
        r11 = r3.channel_id;	 Catch:{ Exception -> 0x08f6 }
        r8.append(r11);	 Catch:{ Exception -> 0x08f6 }
        r11 = " access_hash = ";
        r8.append(r11);	 Catch:{ Exception -> 0x08f6 }
        r61 = r1;
        r1 = r3.access_hash;	 Catch:{ Exception -> 0x08f6 }
        r8.append(r1);	 Catch:{ Exception -> 0x08f6 }
        r1 = " notify = ";
        r8.append(r1);	 Catch:{ Exception -> 0x08f6 }
        r1 = r63;
        r8.append(r1);	 Catch:{ Exception -> 0x08f6 }
        r2 = " silent = ";
        r8.append(r2);	 Catch:{ Exception -> 0x08f6 }
        r2 = r7.currentAccount;	 Catch:{ Exception -> 0x08f6 }
        r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2);	 Catch:{ Exception -> 0x08f6 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x08f6 }
        r11.<init>();	 Catch:{ Exception -> 0x08f6 }
        r11.append(r12);	 Catch:{ Exception -> 0x08f6 }
        r11.append(r13);	 Catch:{ Exception -> 0x08f6 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x08f6 }
        r52 = r12;
        r12 = 0;
        r2 = r2.getBoolean(r11, r12);	 Catch:{ Exception -> 0x08f6 }
        r8.append(r2);	 Catch:{ Exception -> 0x08f6 }
        r2 = r8.toString();	 Catch:{ Exception -> 0x08f6 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x08f6 }
        goto L_0x098f;
    L_0x0989:
        r61 = r1;
        r52 = r12;
        r1 = r63;
    L_0x098f:
        if (r15 == 0) goto L_0x1738;
    L_0x0991:
        r2 = 9;
        if (r15 != r2) goto L_0x099b;
    L_0x0995:
        if (r49 == 0) goto L_0x099b;
    L_0x0997:
        if (r9 == 0) goto L_0x099b;
    L_0x0999:
        goto L_0x1738;
    L_0x099b:
        r2 = 1;
        if (r15 < r2) goto L_0x09a1;
    L_0x099e:
        r2 = 3;
        if (r15 <= r2) goto L_0x09b2;
    L_0x09a1:
        r2 = 5;
        if (r15 < r2) goto L_0x09a8;
    L_0x09a4:
        r2 = 8;
        if (r15 <= r2) goto L_0x09b2;
    L_0x09a8:
        r2 = 9;
        if (r15 != r2) goto L_0x09ae;
    L_0x09ac:
        if (r9 != 0) goto L_0x09b2;
    L_0x09ae:
        r2 = 10;
        if (r15 != r2) goto L_0x15b9;
    L_0x09b2:
        if (r9 != 0) goto L_0x0fe0;
    L_0x09b4:
        r2 = 1;
        if (r15 != r2) goto L_0x0a11;
    L_0x09b7:
        r2 = r29;
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x08f6 }
        if (r4 == 0) goto L_0x09d7;
    L_0x09bd:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue;	 Catch:{ Exception -> 0x08f6 }
        r4.<init>();	 Catch:{ Exception -> 0x08f6 }
        r8 = r2.address;	 Catch:{ Exception -> 0x08f6 }
        r4.address = r8;	 Catch:{ Exception -> 0x08f6 }
        r8 = r2.title;	 Catch:{ Exception -> 0x08f6 }
        r4.title = r8;	 Catch:{ Exception -> 0x08f6 }
        r8 = r2.provider;	 Catch:{ Exception -> 0x08f6 }
        r4.provider = r8;	 Catch:{ Exception -> 0x08f6 }
        r8 = r2.venue_id;	 Catch:{ Exception -> 0x08f6 }
        r4.venue_id = r8;	 Catch:{ Exception -> 0x08f6 }
        r8 = r28;
        r4.venue_type = r8;	 Catch:{ Exception -> 0x08f6 }
        goto L_0x09f0;
    L_0x09d7:
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;	 Catch:{ Exception -> 0x08f6 }
        if (r4 == 0) goto L_0x09eb;
    L_0x09db:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive;	 Catch:{ Exception -> 0x08f6 }
        r4.<init>();	 Catch:{ Exception -> 0x08f6 }
        r8 = r2.period;	 Catch:{ Exception -> 0x08f6 }
        r4.period = r8;	 Catch:{ Exception -> 0x08f6 }
        r8 = r4.flags;	 Catch:{ Exception -> 0x08f6 }
        r9 = 2;
        r8 = r8 | r9;
        r4.flags = r8;	 Catch:{ Exception -> 0x08f6 }
        goto L_0x09f0;
    L_0x09eb:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint;	 Catch:{ Exception -> 0x08f6 }
        r4.<init>();	 Catch:{ Exception -> 0x08f6 }
    L_0x09f0:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint;	 Catch:{ Exception -> 0x08f6 }
        r8.<init>();	 Catch:{ Exception -> 0x08f6 }
        r4.geo_point = r8;	 Catch:{ Exception -> 0x08f6 }
        r8 = r4.geo_point;	 Catch:{ Exception -> 0x08f6 }
        r9 = r2.geo;	 Catch:{ Exception -> 0x08f6 }
        r11 = r9.lat;	 Catch:{ Exception -> 0x08f6 }
        r8.lat = r11;	 Catch:{ Exception -> 0x08f6 }
        r8 = r4.geo_point;	 Catch:{ Exception -> 0x08f6 }
        r2 = r2.geo;	 Catch:{ Exception -> 0x08f6 }
        r11 = r2._long;	 Catch:{ Exception -> 0x08f6 }
        r8._long = r11;	 Catch:{ Exception -> 0x08f6 }
        r11 = r57;
        r28 = r3;
        r3 = r18;
        r12 = r20;
        goto L_0x0CLASSNAME;
    L_0x0a11:
        r8 = r28;
        r2 = 2;
        if (r15 == r2) goto L_0x0cde;
    L_0x0a16:
        r2 = 9;
        if (r15 != r2) goto L_0x0a1e;
    L_0x0a1a:
        if (r44 == 0) goto L_0x0a1e;
    L_0x0a1c:
        goto L_0x0cde;
    L_0x0a1e:
        r2 = 3;
        if (r15 != r2) goto L_0x0ae7;
    L_0x0a21:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x08f6 }
        r2.<init>();	 Catch:{ Exception -> 0x08f6 }
        r8 = r4.mime_type;	 Catch:{ Exception -> 0x08f6 }
        r2.mime_type = r8;	 Catch:{ Exception -> 0x08f6 }
        r8 = r4.attributes;	 Catch:{ Exception -> 0x08f6 }
        r2.attributes = r8;	 Catch:{ Exception -> 0x08f6 }
        r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r4);	 Catch:{ Exception -> 0x08f6 }
        if (r8 != 0) goto L_0x0a50;
    L_0x0a34:
        if (r48 == 0) goto L_0x0a41;
    L_0x0a36:
        r11 = r48;
        r8 = r11.muted;	 Catch:{ Exception -> 0x08f6 }
        if (r8 != 0) goto L_0x0a52;
    L_0x0a3c:
        r8 = r11.roundVideo;	 Catch:{ Exception -> 0x08f6 }
        if (r8 != 0) goto L_0x0a52;
    L_0x0a40:
        goto L_0x0a43;
    L_0x0a41:
        r11 = r48;
    L_0x0a43:
        r8 = 1;
        r2.nosound_video = r8;	 Catch:{ Exception -> 0x08f6 }
        r8 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x08f6 }
        if (r8 == 0) goto L_0x0a52;
    L_0x0a4a:
        r8 = "nosound_video = true";
        org.telegram.messenger.FileLog.d(r8);	 Catch:{ Exception -> 0x08f6 }
        goto L_0x0a52;
    L_0x0a50:
        r11 = r48;
    L_0x0a52:
        if (r47 == 0) goto L_0x0a60;
    L_0x0a54:
        r9 = r47;
        r2.ttl_seconds = r9;	 Catch:{ Exception -> 0x08f6 }
        r5.ttl = r9;	 Catch:{ Exception -> 0x08f6 }
        r8 = r2.flags;	 Catch:{ Exception -> 0x08f6 }
        r9 = 2;
        r8 = r8 | r9;
        r2.flags = r8;	 Catch:{ Exception -> 0x08f6 }
    L_0x0a60:
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x08f6 }
        r12 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r12 != 0) goto L_0x0a6b;
    L_0x0a66:
        r9 = r2;
        r48 = r11;
        r8 = 1;
        goto L_0x0a9a;
    L_0x0a6b:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x08f6 }
        r8.<init>();	 Catch:{ Exception -> 0x08f6 }
        r9 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x08f6 }
        r9.<init>();	 Catch:{ Exception -> 0x08f6 }
        r8.id = r9;	 Catch:{ Exception -> 0x08f6 }
        r9 = r8.id;	 Catch:{ Exception -> 0x08f6 }
        r48 = r11;
        r11 = r4.id;	 Catch:{ Exception -> 0x08f6 }
        r9.id = r11;	 Catch:{ Exception -> 0x08f6 }
        r9 = r8.id;	 Catch:{ Exception -> 0x08f6 }
        r11 = r4.access_hash;	 Catch:{ Exception -> 0x08f6 }
        r9.access_hash = r11;	 Catch:{ Exception -> 0x08f6 }
        r9 = r8.id;	 Catch:{ Exception -> 0x08f6 }
        r11 = r4.file_reference;	 Catch:{ Exception -> 0x08f6 }
        r9.file_reference = r11;	 Catch:{ Exception -> 0x08f6 }
        r9 = r8.id;	 Catch:{ Exception -> 0x08f6 }
        r9 = r9.file_reference;	 Catch:{ Exception -> 0x08f6 }
        if (r9 != 0) goto L_0x0a98;
    L_0x0a91:
        r9 = r8.id;	 Catch:{ Exception -> 0x08f6 }
        r11 = 0;
        r12 = new byte[r11];	 Catch:{ Exception -> 0x08f6 }
        r9.file_reference = r12;	 Catch:{ Exception -> 0x08f6 }
    L_0x0a98:
        r9 = r8;
        r8 = 0;
    L_0x0a9a:
        if (r6 != 0) goto L_0x0aba;
    L_0x0a9c:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x08f6 }
        r6.<init>(r13);	 Catch:{ Exception -> 0x08f6 }
        r11 = 1;
        r6.type = r11;	 Catch:{ Exception -> 0x08f6 }
        r11 = r57;
        r6.obj = r11;	 Catch:{ Exception -> 0x0b38 }
        r12 = r20;
        r6.originalPath = r12;	 Catch:{ Exception -> 0x0b38 }
        r1 = r18;
        r6.parentObject = r1;	 Catch:{ Exception -> 0x0b38 }
        r44 = r9;
        if (r10 == 0) goto L_0x0ab6;
    L_0x0ab4:
        r9 = 1;
        goto L_0x0ab7;
    L_0x0ab6:
        r9 = 0;
    L_0x0ab7:
        r6.scheduled = r9;	 Catch:{ Exception -> 0x0b38 }
        goto L_0x0ac2;
    L_0x0aba:
        r11 = r57;
        r44 = r9;
        r1 = r18;
        r12 = r20;
    L_0x0ac2:
        r6.inputUploadMedia = r2;	 Catch:{ Exception -> 0x0b38 }
        r6.performMediaUpload = r8;	 Catch:{ Exception -> 0x0b38 }
        r2 = r4.thumbs;	 Catch:{ Exception -> 0x0b38 }
        r2 = r2.isEmpty();	 Catch:{ Exception -> 0x0b38 }
        if (r2 != 0) goto L_0x0adb;
    L_0x0ace:
        r2 = r4.thumbs;	 Catch:{ Exception -> 0x0b38 }
        r9 = 0;
        r2 = r2.get(r9);	 Catch:{ Exception -> 0x0b38 }
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;	 Catch:{ Exception -> 0x0b38 }
        r6.photoSize = r2;	 Catch:{ Exception -> 0x0b38 }
        r6.locationParent = r4;	 Catch:{ Exception -> 0x0b38 }
    L_0x0adb:
        r2 = r48;
        r6.videoEditedInfo = r2;	 Catch:{ Exception -> 0x0b38 }
        r4 = r44;
        r28 = r3;
        r2 = r8;
    L_0x0ae4:
        r3 = r1;
        goto L_0x0dbf;
    L_0x0ae7:
        r9 = r47;
        r11 = r57;
        r1 = r18;
        r12 = r20;
        r2 = 6;
        if (r15 != r2) goto L_0x0b3b;
    L_0x0af2:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact;	 Catch:{ Exception -> 0x0b38 }
        r2.<init>();	 Catch:{ Exception -> 0x0b38 }
        r4 = r46;
        r9 = r4.phone;	 Catch:{ Exception -> 0x0b38 }
        r2.phone_number = r9;	 Catch:{ Exception -> 0x0b38 }
        r9 = r4.first_name;	 Catch:{ Exception -> 0x0b38 }
        r2.first_name = r9;	 Catch:{ Exception -> 0x0b38 }
        r9 = r4.last_name;	 Catch:{ Exception -> 0x0b38 }
        r2.last_name = r9;	 Catch:{ Exception -> 0x0b38 }
        r9 = r4.restriction_reason;	 Catch:{ Exception -> 0x0b38 }
        r9 = r9.isEmpty();	 Catch:{ Exception -> 0x0b38 }
        if (r9 != 0) goto L_0x0b30;
    L_0x0b0d:
        r9 = r4.restriction_reason;	 Catch:{ Exception -> 0x0b38 }
        r28 = r3;
        r3 = 0;
        r9 = r9.get(r3);	 Catch:{ Exception -> 0x0b38 }
        r9 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r9;	 Catch:{ Exception -> 0x0b38 }
        r3 = r9.text;	 Catch:{ Exception -> 0x0b38 }
        r9 = "BEGIN:VCARD";
        r3 = r3.startsWith(r9);	 Catch:{ Exception -> 0x0b38 }
        if (r3 == 0) goto L_0x0b32;
    L_0x0b22:
        r3 = r4.restriction_reason;	 Catch:{ Exception -> 0x0b38 }
        r4 = 0;
        r3 = r3.get(r4);	 Catch:{ Exception -> 0x0b38 }
        r3 = (org.telegram.tgnet.TLRPC.TL_restrictionReason) r3;	 Catch:{ Exception -> 0x0b38 }
        r3 = r3.text;	 Catch:{ Exception -> 0x0b38 }
        r2.vcard = r3;	 Catch:{ Exception -> 0x0b38 }
        goto L_0x0b34;
    L_0x0b30:
        r28 = r3;
    L_0x0b32:
        r2.vcard = r8;	 Catch:{ Exception -> 0x0b38 }
    L_0x0b34:
        r3 = r1;
        r4 = r2;
        goto L_0x0CLASSNAME;
    L_0x0b38:
        r0 = move-exception;
        goto L_0x08cc;
    L_0x0b3b:
        r28 = r3;
        r2 = 7;
        if (r15 == r2) goto L_0x0c0c;
    L_0x0b40:
        r2 = 9;
        if (r15 != r2) goto L_0x0b46;
    L_0x0b44:
        goto L_0x0c0c;
    L_0x0b46:
        r2 = 8;
        if (r15 != r2) goto L_0x0bb3;
    L_0x0b4a:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0b38 }
        r2.<init>();	 Catch:{ Exception -> 0x0b38 }
        r3 = r4.mime_type;	 Catch:{ Exception -> 0x0b38 }
        r2.mime_type = r3;	 Catch:{ Exception -> 0x0b38 }
        r3 = r4.attributes;	 Catch:{ Exception -> 0x0b38 }
        r2.attributes = r3;	 Catch:{ Exception -> 0x0b38 }
        if (r9 == 0) goto L_0x0b63;
    L_0x0b59:
        r2.ttl_seconds = r9;	 Catch:{ Exception -> 0x0b38 }
        r5.ttl = r9;	 Catch:{ Exception -> 0x0b38 }
        r3 = r2.flags;	 Catch:{ Exception -> 0x0b38 }
        r6 = 2;
        r3 = r3 | r6;
        r2.flags = r3;	 Catch:{ Exception -> 0x0b38 }
    L_0x0b63:
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x0b38 }
        r3 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r3 != 0) goto L_0x0b6c;
    L_0x0b69:
        r4 = r2;
        r3 = 1;
        goto L_0x0b99;
    L_0x0b6c:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0b38 }
        r3.<init>();	 Catch:{ Exception -> 0x0b38 }
        r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0b38 }
        r6.<init>();	 Catch:{ Exception -> 0x0b38 }
        r3.id = r6;	 Catch:{ Exception -> 0x0b38 }
        r6 = r3.id;	 Catch:{ Exception -> 0x0b38 }
        r8 = r4.id;	 Catch:{ Exception -> 0x0b38 }
        r6.id = r8;	 Catch:{ Exception -> 0x0b38 }
        r6 = r3.id;	 Catch:{ Exception -> 0x0b38 }
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x0b38 }
        r6.access_hash = r8;	 Catch:{ Exception -> 0x0b38 }
        r6 = r3.id;	 Catch:{ Exception -> 0x0b38 }
        r4 = r4.file_reference;	 Catch:{ Exception -> 0x0b38 }
        r6.file_reference = r4;	 Catch:{ Exception -> 0x0b38 }
        r4 = r3.id;	 Catch:{ Exception -> 0x0b38 }
        r4 = r4.file_reference;	 Catch:{ Exception -> 0x0b38 }
        if (r4 != 0) goto L_0x0b97;
    L_0x0b90:
        r4 = r3.id;	 Catch:{ Exception -> 0x0b38 }
        r6 = 0;
        r8 = new byte[r6];	 Catch:{ Exception -> 0x0b38 }
        r4.file_reference = r8;	 Catch:{ Exception -> 0x0b38 }
    L_0x0b97:
        r4 = r3;
        r3 = 0;
    L_0x0b99:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0b38 }
        r6.<init>(r13);	 Catch:{ Exception -> 0x0b38 }
        r8 = 3;
        r6.type = r8;	 Catch:{ Exception -> 0x0b38 }
        r6.obj = r11;	 Catch:{ Exception -> 0x0b38 }
        r6.parentObject = r1;	 Catch:{ Exception -> 0x0b38 }
        r6.inputUploadMedia = r2;	 Catch:{ Exception -> 0x0b38 }
        r6.performMediaUpload = r3;	 Catch:{ Exception -> 0x0b38 }
        if (r10 == 0) goto L_0x0bad;
    L_0x0bab:
        r2 = 1;
        goto L_0x0bae;
    L_0x0bad:
        r2 = 0;
    L_0x0bae:
        r6.scheduled = r2;	 Catch:{ Exception -> 0x0b38 }
        r2 = r3;
        goto L_0x0ae4;
    L_0x0bb3:
        r2 = 10;
        if (r15 != r2) goto L_0x0CLASSNAME;
    L_0x0bb7:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll;	 Catch:{ Exception -> 0x0b38 }
        r2.<init>();	 Catch:{ Exception -> 0x0b38 }
        r8 = r26;
        r3 = r8.poll;	 Catch:{ Exception -> 0x0b38 }
        r2.poll = r3;	 Catch:{ Exception -> 0x0b38 }
        if (r50 == 0) goto L_0x0bff;
    L_0x0bc4:
        r3 = "answers";
        r8 = r50;
        r3 = r8.containsKey(r3);	 Catch:{ Exception -> 0x0b38 }
        if (r3 == 0) goto L_0x0bff;
    L_0x0bce:
        r3 = "answers";
        r3 = r8.get(r3);	 Catch:{ Exception -> 0x0b38 }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x0b38 }
        r3 = org.telegram.messenger.Utilities.hexToBytes(r3);	 Catch:{ Exception -> 0x0b38 }
        r4 = r3.length;	 Catch:{ Exception -> 0x0b38 }
        if (r4 <= 0) goto L_0x0bff;
    L_0x0bdd:
        r4 = 0;
    L_0x0bde:
        r8 = r3.length;	 Catch:{ Exception -> 0x0b38 }
        if (r4 >= r8) goto L_0x0bf6;
    L_0x0be1:
        r8 = r2.correct_answers;	 Catch:{ Exception -> 0x0b38 }
        r18 = r1;
        r9 = 1;
        r1 = new byte[r9];	 Catch:{ Exception -> 0x0b38 }
        r9 = r3[r4];	 Catch:{ Exception -> 0x0b38 }
        r19 = 0;
        r1[r19] = r9;	 Catch:{ Exception -> 0x0b38 }
        r8.add(r1);	 Catch:{ Exception -> 0x0b38 }
        r4 = r4 + 1;
        r1 = r18;
        goto L_0x0bde;
    L_0x0bf6:
        r18 = r1;
        r1 = r2.flags;	 Catch:{ Exception -> 0x0b38 }
        r3 = 1;
        r1 = r1 | r3;
        r2.flags = r1;	 Catch:{ Exception -> 0x0b38 }
        goto L_0x0CLASSNAME;
    L_0x0bff:
        r18 = r1;
    L_0x0CLASSNAME:
        r4 = r2;
        r3 = r18;
    L_0x0CLASSNAME:
        r2 = 0;
        goto L_0x0dbf;
    L_0x0CLASSNAME:
        r3 = r1;
        r2 = 0;
        r4 = 0;
        goto L_0x0dbf;
    L_0x0c0c:
        r8 = r50;
        r18 = r1;
        if (r12 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r55;
        r3 = r18;
        if (r1 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r4.access_hash;	 Catch:{ Exception -> 0x0b38 }
        r18 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1));
        if (r18 != 0) goto L_0x0c1f;
    L_0x0c1e:
        goto L_0x0CLASSNAME;
    L_0x0c1f:
        r1 = 0;
        goto L_0x0c5b;
    L_0x0CLASSNAME:
        r3 = r18;
    L_0x0CLASSNAME:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0cd9 }
        r1.<init>();	 Catch:{ Exception -> 0x0cd9 }
        if (r9 == 0) goto L_0x0CLASSNAME;
    L_0x0c2a:
        r1.ttl_seconds = r9;	 Catch:{ Exception -> 0x0b38 }
        r5.ttl = r9;	 Catch:{ Exception -> 0x0b38 }
        r2 = r1.flags;	 Catch:{ Exception -> 0x0b38 }
        r9 = 2;
        r2 = r2 | r9;
        r1.flags = r2;	 Catch:{ Exception -> 0x0b38 }
    L_0x0CLASSNAME:
        r2 = android.text.TextUtils.isEmpty(r55);	 Catch:{ Exception -> 0x0cd9 }
        if (r2 != 0) goto L_0x0CLASSNAME;
    L_0x0c3a:
        r2 = r55.toLowerCase();	 Catch:{ Exception -> 0x0b38 }
        r9 = "mp4";
        r2 = r2.endsWith(r9);	 Catch:{ Exception -> 0x0b38 }
        if (r2 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        if (r8 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = "forceDocument";
        r2 = r8.containsKey(r2);	 Catch:{ Exception -> 0x0b38 }
        if (r2 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = 1;
        r1.nosound_video = r2;	 Catch:{ Exception -> 0x0b38 }
    L_0x0CLASSNAME:
        r2 = r4.mime_type;	 Catch:{ Exception -> 0x0cd9 }
        r1.mime_type = r2;	 Catch:{ Exception -> 0x0cd9 }
        r2 = r4.attributes;	 Catch:{ Exception -> 0x0cd9 }
        r1.attributes = r2;	 Catch:{ Exception -> 0x0cd9 }
    L_0x0c5b:
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x0cd9 }
        r2 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r2 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0b38 }
        r18 = r5;
        r56 = r6;
        r5 = r1;
        goto L_0x0c9a;
    L_0x0CLASSNAME:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0cd9 }
        r2.<init>();	 Catch:{ Exception -> 0x0cd9 }
        r8 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0cd9 }
        r8.<init>();	 Catch:{ Exception -> 0x0cd9 }
        r2.id = r8;	 Catch:{ Exception -> 0x0cd9 }
        r8 = r2.id;	 Catch:{ Exception -> 0x0cd9 }
        r18 = r5;
        r56 = r6;
        r5 = r4.id;	 Catch:{ Exception -> 0x0ccb }
        r8.id = r5;	 Catch:{ Exception -> 0x0ccb }
        r5 = r2.id;	 Catch:{ Exception -> 0x0ccb }
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x0ccb }
        r5.access_hash = r8;	 Catch:{ Exception -> 0x0ccb }
        r5 = r2.id;	 Catch:{ Exception -> 0x0ccb }
        r6 = r4.file_reference;	 Catch:{ Exception -> 0x0ccb }
        r5.file_reference = r6;	 Catch:{ Exception -> 0x0ccb }
        r5 = r2.id;	 Catch:{ Exception -> 0x0ccb }
        r5 = r5.file_reference;	 Catch:{ Exception -> 0x0ccb }
        if (r5 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r5 = r2.id;	 Catch:{ Exception -> 0x0ccb }
        r6 = 0;
        r8 = new byte[r6];	 Catch:{ Exception -> 0x0ccb }
        r5.file_reference = r8;	 Catch:{ Exception -> 0x0ccb }
    L_0x0CLASSNAME:
        r5 = r2;
        r2 = 0;
    L_0x0c9a:
        if (r1 == 0) goto L_0x0cd2;
    L_0x0c9c:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ccb }
        r6.<init>(r13);	 Catch:{ Exception -> 0x0ccb }
        r6.originalPath = r12;	 Catch:{ Exception -> 0x0ccb }
        r8 = 2;
        r6.type = r8;	 Catch:{ Exception -> 0x0ccb }
        r6.obj = r11;	 Catch:{ Exception -> 0x0ccb }
        r8 = r4.thumbs;	 Catch:{ Exception -> 0x0ccb }
        r8 = r8.isEmpty();	 Catch:{ Exception -> 0x0ccb }
        if (r8 != 0) goto L_0x0cbd;
    L_0x0cb0:
        r8 = r4.thumbs;	 Catch:{ Exception -> 0x0ccb }
        r9 = 0;
        r8 = r8.get(r9);	 Catch:{ Exception -> 0x0ccb }
        r8 = (org.telegram.tgnet.TLRPC.PhotoSize) r8;	 Catch:{ Exception -> 0x0ccb }
        r6.photoSize = r8;	 Catch:{ Exception -> 0x0ccb }
        r6.locationParent = r4;	 Catch:{ Exception -> 0x0ccb }
    L_0x0cbd:
        r6.parentObject = r3;	 Catch:{ Exception -> 0x0ccb }
        r6.inputUploadMedia = r1;	 Catch:{ Exception -> 0x0ccb }
        r6.performMediaUpload = r2;	 Catch:{ Exception -> 0x0ccb }
        if (r10 == 0) goto L_0x0cc7;
    L_0x0cc5:
        r1 = 1;
        goto L_0x0cc8;
    L_0x0cc7:
        r1 = 0;
    L_0x0cc8:
        r6.scheduled = r1;	 Catch:{ Exception -> 0x0ccb }
        goto L_0x0cd4;
    L_0x0ccb:
        r0 = move-exception;
        r2 = r0;
        r1 = r10;
    L_0x0cce:
        r5 = r18;
        goto L_0x1897;
    L_0x0cd2:
        r6 = r56;
    L_0x0cd4:
        r4 = r5;
        r5 = r18;
        goto L_0x0dbf;
    L_0x0cd9:
        r0 = move-exception;
        r18 = r5;
        goto L_0x08cc;
    L_0x0cde:
        r9 = r47;
        r8 = r50;
        r11 = r57;
        r28 = r3;
        r56 = r6;
        r3 = r18;
        r12 = r20;
        r18 = r5;
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x0fdb }
        r1.<init>();	 Catch:{ Exception -> 0x0fdb }
        if (r9 == 0) goto L_0x0d02;
    L_0x0cf5:
        r1.ttl_seconds = r9;	 Catch:{ Exception -> 0x0fdb }
        r5 = r18;
        r5.ttl = r9;	 Catch:{ Exception -> 0x0b38 }
        r2 = r1.flags;	 Catch:{ Exception -> 0x0b38 }
        r4 = 2;
        r2 = r2 | r4;
        r1.flags = r2;	 Catch:{ Exception -> 0x0b38 }
        goto L_0x0d04;
    L_0x0d02:
        r5 = r18;
    L_0x0d04:
        if (r8 == 0) goto L_0x0d3f;
    L_0x0d06:
        r2 = "masks";
        r2 = r8.get(r2);	 Catch:{ Exception -> 0x0b38 }
        r2 = (java.lang.String) r2;	 Catch:{ Exception -> 0x0b38 }
        if (r2 == 0) goto L_0x0d3f;
    L_0x0d10:
        r4 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0b38 }
        r2 = org.telegram.messenger.Utilities.hexToBytes(r2);	 Catch:{ Exception -> 0x0b38 }
        r4.<init>(r2);	 Catch:{ Exception -> 0x0b38 }
        r2 = 0;
        r6 = r4.readInt32(r2);	 Catch:{ Exception -> 0x0b38 }
        r8 = 0;
    L_0x0d1f:
        if (r8 >= r6) goto L_0x0d36;
    L_0x0d21:
        r9 = r1.stickers;	 Catch:{ Exception -> 0x0b38 }
        r45 = r6;
        r6 = r4.readInt32(r2);	 Catch:{ Exception -> 0x0b38 }
        r6 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r4, r6, r2);	 Catch:{ Exception -> 0x0b38 }
        r9.add(r6);	 Catch:{ Exception -> 0x0b38 }
        r8 = r8 + 1;
        r6 = r45;
        r2 = 0;
        goto L_0x0d1f;
    L_0x0d36:
        r2 = r1.flags;	 Catch:{ Exception -> 0x0b38 }
        r6 = 1;
        r2 = r2 | r6;
        r1.flags = r2;	 Catch:{ Exception -> 0x0b38 }
        r4.cleanup();	 Catch:{ Exception -> 0x0b38 }
    L_0x0d3f:
        r6 = r44;
        r8 = r6.access_hash;	 Catch:{ Exception -> 0x0b38 }
        r2 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r2 != 0) goto L_0x0d4a;
    L_0x0d47:
        r4 = r1;
        r2 = 1;
        goto L_0x0d77;
    L_0x0d4a:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x0b38 }
        r2.<init>();	 Catch:{ Exception -> 0x0b38 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x0b38 }
        r4.<init>();	 Catch:{ Exception -> 0x0b38 }
        r2.id = r4;	 Catch:{ Exception -> 0x0b38 }
        r4 = r2.id;	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.id;	 Catch:{ Exception -> 0x0b38 }
        r4.id = r8;	 Catch:{ Exception -> 0x0b38 }
        r4 = r2.id;	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.access_hash;	 Catch:{ Exception -> 0x0b38 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0b38 }
        r4 = r2.id;	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.file_reference;	 Catch:{ Exception -> 0x0b38 }
        r4.file_reference = r8;	 Catch:{ Exception -> 0x0b38 }
        r4 = r2.id;	 Catch:{ Exception -> 0x0b38 }
        r4 = r4.file_reference;	 Catch:{ Exception -> 0x0b38 }
        if (r4 != 0) goto L_0x0d75;
    L_0x0d6e:
        r4 = r2.id;	 Catch:{ Exception -> 0x0b38 }
        r8 = 0;
        r9 = new byte[r8];	 Catch:{ Exception -> 0x0b38 }
        r4.file_reference = r9;	 Catch:{ Exception -> 0x0b38 }
    L_0x0d75:
        r4 = r2;
        r2 = 0;
    L_0x0d77:
        if (r56 != 0) goto L_0x0d8d;
    L_0x0d79:
        r8 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0b38 }
        r8.<init>(r13);	 Catch:{ Exception -> 0x0b38 }
        r9 = 0;
        r8.type = r9;	 Catch:{ Exception -> 0x0b38 }
        r8.obj = r11;	 Catch:{ Exception -> 0x0b38 }
        r8.originalPath = r12;	 Catch:{ Exception -> 0x0b38 }
        if (r10 == 0) goto L_0x0d89;
    L_0x0d87:
        r9 = 1;
        goto L_0x0d8a;
    L_0x0d89:
        r9 = 0;
    L_0x0d8a:
        r8.scheduled = r9;	 Catch:{ Exception -> 0x0b38 }
        goto L_0x0d8f;
    L_0x0d8d:
        r8 = r56;
    L_0x0d8f:
        r8.inputUploadMedia = r1;	 Catch:{ Exception -> 0x0b38 }
        r8.performMediaUpload = r2;	 Catch:{ Exception -> 0x0b38 }
        r1 = r55;
        if (r1 == 0) goto L_0x0da8;
    L_0x0d97:
        r9 = r55.length();	 Catch:{ Exception -> 0x0b38 }
        if (r9 <= 0) goto L_0x0da8;
    L_0x0d9d:
        r9 = "http";
        r9 = r1.startsWith(r9);	 Catch:{ Exception -> 0x0b38 }
        if (r9 == 0) goto L_0x0da8;
    L_0x0da5:
        r8.httpLocation = r1;	 Catch:{ Exception -> 0x0b38 }
        goto L_0x0dbe;
    L_0x0da8:
        r1 = r6.sizes;	 Catch:{ Exception -> 0x0b38 }
        r9 = r6.sizes;	 Catch:{ Exception -> 0x0b38 }
        r9 = r9.size();	 Catch:{ Exception -> 0x0b38 }
        r18 = 1;
        r9 = r9 + -1;
        r1 = r1.get(r9);	 Catch:{ Exception -> 0x0b38 }
        r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1;	 Catch:{ Exception -> 0x0b38 }
        r8.photoSize = r1;	 Catch:{ Exception -> 0x0b38 }
        r8.locationParent = r6;	 Catch:{ Exception -> 0x0b38 }
    L_0x0dbe:
        r6 = r8;
    L_0x0dbf:
        r1 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x0e7c;
    L_0x0dc3:
        r1 = r6.sendRequest;	 Catch:{ Exception -> 0x0b38 }
        if (r1 == 0) goto L_0x0dce;
    L_0x0dc7:
        r1 = r6.sendRequest;	 Catch:{ Exception -> 0x0b38 }
        r1 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r1;	 Catch:{ Exception -> 0x0b38 }
        r44 = r15;
        goto L_0x0e1b;
    L_0x0dce:
        r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;	 Catch:{ Exception -> 0x0b38 }
        r1.<init>();	 Catch:{ Exception -> 0x0b38 }
        r8 = r28;
        r1.peer = r8;	 Catch:{ Exception -> 0x0b38 }
        if (r63 == 0) goto L_0x0dfc;
    L_0x0dd9:
        r8 = r7.currentAccount;	 Catch:{ Exception -> 0x0b38 }
        r8 = org.telegram.messenger.MessagesController.getNotificationsSettings(r8);	 Catch:{ Exception -> 0x0b38 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b38 }
        r9.<init>();	 Catch:{ Exception -> 0x0b38 }
        r44 = r15;
        r15 = r52;
        r9.append(r15);	 Catch:{ Exception -> 0x0b38 }
        r9.append(r13);	 Catch:{ Exception -> 0x0b38 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x0b38 }
        r13 = 0;
        r8 = r8.getBoolean(r9, r13);	 Catch:{ Exception -> 0x0b38 }
        if (r8 == 0) goto L_0x0dfa;
    L_0x0df9:
        goto L_0x0dfe;
    L_0x0dfa:
        r8 = 0;
        goto L_0x0dff;
    L_0x0dfc:
        r44 = r15;
    L_0x0dfe:
        r8 = 1;
    L_0x0dff:
        r1.silent = r8;	 Catch:{ Exception -> 0x0b38 }
        r8 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x0b38 }
        if (r8 == 0) goto L_0x0e0f;
    L_0x0e05:
        r8 = r1.flags;	 Catch:{ Exception -> 0x0b38 }
        r9 = 1;
        r8 = r8 | r9;
        r1.flags = r8;	 Catch:{ Exception -> 0x0b38 }
        r8 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x0b38 }
        r1.reply_to_msg_id = r8;	 Catch:{ Exception -> 0x0b38 }
    L_0x0e0f:
        if (r10 == 0) goto L_0x0e19;
    L_0x0e11:
        r1.schedule_date = r10;	 Catch:{ Exception -> 0x0b38 }
        r8 = r1.flags;	 Catch:{ Exception -> 0x0b38 }
        r8 = r8 | 1024;
        r1.flags = r8;	 Catch:{ Exception -> 0x0b38 }
    L_0x0e19:
        r6.sendRequest = r1;	 Catch:{ Exception -> 0x0b38 }
    L_0x0e1b:
        r8 = r6.messageObjects;	 Catch:{ Exception -> 0x0b38 }
        r8.add(r11);	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.parentObjects;	 Catch:{ Exception -> 0x0b38 }
        r8.add(r3);	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.locations;	 Catch:{ Exception -> 0x0b38 }
        r9 = r6.photoSize;	 Catch:{ Exception -> 0x0b38 }
        r8.add(r9);	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.videoEditedInfos;	 Catch:{ Exception -> 0x0b38 }
        r9 = r6.videoEditedInfo;	 Catch:{ Exception -> 0x0b38 }
        r8.add(r9);	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.httpLocations;	 Catch:{ Exception -> 0x0b38 }
        r9 = r6.httpLocation;	 Catch:{ Exception -> 0x0b38 }
        r8.add(r9);	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.inputMedias;	 Catch:{ Exception -> 0x0b38 }
        r9 = r6.inputUploadMedia;	 Catch:{ Exception -> 0x0b38 }
        r8.add(r9);	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.messages;	 Catch:{ Exception -> 0x0b38 }
        r8.add(r5);	 Catch:{ Exception -> 0x0b38 }
        r8 = r6.originalPaths;	 Catch:{ Exception -> 0x0b38 }
        r8.add(r12);	 Catch:{ Exception -> 0x0b38 }
        r8 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia;	 Catch:{ Exception -> 0x0b38 }
        r8.<init>();	 Catch:{ Exception -> 0x0b38 }
        r13 = r5.random_id;	 Catch:{ Exception -> 0x0b38 }
        r8.random_id = r13;	 Catch:{ Exception -> 0x0b38 }
        r8.media = r4;	 Catch:{ Exception -> 0x0b38 }
        r9 = r51;
        r8.message = r9;	 Catch:{ Exception -> 0x0b38 }
        r9 = r44;
        r4 = r60;
        if (r4 == 0) goto L_0x0e6e;
    L_0x0e60:
        r13 = r60.isEmpty();	 Catch:{ Exception -> 0x0b38 }
        if (r13 != 0) goto L_0x0e6e;
    L_0x0e66:
        r8.entities = r4;	 Catch:{ Exception -> 0x0b38 }
        r4 = r8.flags;	 Catch:{ Exception -> 0x0b38 }
        r13 = 1;
        r4 = r4 | r13;
        r8.flags = r4;	 Catch:{ Exception -> 0x0b38 }
    L_0x0e6e:
        r4 = r1.multi_media;	 Catch:{ Exception -> 0x0b38 }
        r4.add(r8);	 Catch:{ Exception -> 0x0b38 }
        r44 = r2;
        r18 = r3;
        r20 = r12;
        r12 = r9;
        goto L_0x0eee;
    L_0x0e7c:
        r9 = r51;
        r1 = r60;
        r44 = r2;
        r20 = r12;
        r12 = r15;
        r8 = r28;
        r15 = r52;
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia;	 Catch:{ Exception -> 0x0b38 }
        r2.<init>();	 Catch:{ Exception -> 0x0b38 }
        r2.peer = r8;	 Catch:{ Exception -> 0x0b38 }
        if (r63 == 0) goto L_0x0eb3;
    L_0x0e92:
        r8 = r7.currentAccount;	 Catch:{ Exception -> 0x0b38 }
        r8 = org.telegram.messenger.MessagesController.getNotificationsSettings(r8);	 Catch:{ Exception -> 0x0b38 }
        r18 = r3;
        r3 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0b38 }
        r3.<init>();	 Catch:{ Exception -> 0x0b38 }
        r3.append(r15);	 Catch:{ Exception -> 0x0b38 }
        r3.append(r13);	 Catch:{ Exception -> 0x0b38 }
        r3 = r3.toString();	 Catch:{ Exception -> 0x0b38 }
        r13 = 0;
        r3 = r8.getBoolean(r3, r13);	 Catch:{ Exception -> 0x0b38 }
        if (r3 == 0) goto L_0x0eb1;
    L_0x0eb0:
        goto L_0x0eb5;
    L_0x0eb1:
        r3 = 0;
        goto L_0x0eb6;
    L_0x0eb3:
        r18 = r3;
    L_0x0eb5:
        r3 = 1;
    L_0x0eb6:
        r2.silent = r3;	 Catch:{ Exception -> 0x0b38 }
        r3 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x0b38 }
        if (r3 == 0) goto L_0x0ec6;
    L_0x0ebc:
        r3 = r2.flags;	 Catch:{ Exception -> 0x0b38 }
        r8 = 1;
        r3 = r3 | r8;
        r2.flags = r3;	 Catch:{ Exception -> 0x0b38 }
        r3 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x0b38 }
        r2.reply_to_msg_id = r3;	 Catch:{ Exception -> 0x0b38 }
    L_0x0ec6:
        r13 = r5.random_id;	 Catch:{ Exception -> 0x0b38 }
        r2.random_id = r13;	 Catch:{ Exception -> 0x0b38 }
        r2.media = r4;	 Catch:{ Exception -> 0x0b38 }
        r2.message = r9;	 Catch:{ Exception -> 0x0b38 }
        if (r1 == 0) goto L_0x0edf;
    L_0x0ed0:
        r3 = r60.isEmpty();	 Catch:{ Exception -> 0x0b38 }
        if (r3 != 0) goto L_0x0edf;
    L_0x0ed6:
        r2.entities = r1;	 Catch:{ Exception -> 0x0b38 }
        r1 = r2.flags;	 Catch:{ Exception -> 0x0b38 }
        r3 = 8;
        r1 = r1 | r3;
        r2.flags = r1;	 Catch:{ Exception -> 0x0b38 }
    L_0x0edf:
        if (r10 == 0) goto L_0x0ee9;
    L_0x0ee1:
        r2.schedule_date = r10;	 Catch:{ Exception -> 0x0b38 }
        r1 = r2.flags;	 Catch:{ Exception -> 0x0b38 }
        r1 = r1 | 1024;
        r2.flags = r1;	 Catch:{ Exception -> 0x0b38 }
    L_0x0ee9:
        if (r6 == 0) goto L_0x0eed;
    L_0x0eeb:
        r6.sendRequest = r2;	 Catch:{ Exception -> 0x0b38 }
    L_0x0eed:
        r1 = r2;
    L_0x0eee:
        r2 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x0ef7;
    L_0x0ef2:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0ef7:
        r2 = 1;
        if (r12 != r2) goto L_0x0var_;
    L_0x0efa:
        r2 = 0;
        if (r10 == 0) goto L_0x0eff;
    L_0x0efd:
        r3 = 1;
        goto L_0x0var_;
    L_0x0eff:
        r3 = 0;
    L_0x0var_:
        r44 = r43;
        r45 = r1;
        r46 = r11;
        r47 = r2;
        r48 = r6;
        r49 = r18;
        r50 = r3;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0var_:
        r2 = 2;
        if (r12 != r2) goto L_0x0f3b;
    L_0x0var_:
        if (r44 == 0) goto L_0x0f1d;
    L_0x0var_:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0f1d:
        r2 = 0;
        r3 = 1;
        if (r10 == 0) goto L_0x0var_;
    L_0x0var_:
        r4 = 1;
        goto L_0x0var_;
    L_0x0var_:
        r4 = 0;
    L_0x0var_:
        r44 = r43;
        r45 = r1;
        r46 = r11;
        r47 = r20;
        r48 = r2;
        r49 = r3;
        r50 = r6;
        r51 = r18;
        r52 = r4;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50, r51, r52);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0f3b:
        r2 = 3;
        if (r12 != r2) goto L_0x0f5d;
    L_0x0f3e:
        if (r44 == 0) goto L_0x0var_;
    L_0x0var_:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0var_:
        if (r10 == 0) goto L_0x0var_;
    L_0x0var_:
        r2 = 1;
        goto L_0x0f4a;
    L_0x0var_:
        r2 = 0;
    L_0x0f4a:
        r44 = r43;
        r45 = r1;
        r46 = r11;
        r47 = r20;
        r48 = r6;
        r49 = r18;
        r50 = r2;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0f5d:
        r2 = 6;
        if (r12 != r2) goto L_0x0var_;
    L_0x0var_:
        if (r10 == 0) goto L_0x0var_;
    L_0x0var_:
        r2 = 1;
        goto L_0x0var_;
    L_0x0var_:
        r2 = 0;
    L_0x0var_:
        r44 = r43;
        r45 = r1;
        r46 = r11;
        r47 = r20;
        r48 = r6;
        r49 = r18;
        r50 = r2;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0var_:
        r2 = 7;
        if (r12 != r2) goto L_0x0f9c;
    L_0x0f7b:
        if (r44 == 0) goto L_0x0var_;
    L_0x0f7d:
        if (r6 == 0) goto L_0x0var_;
    L_0x0f7f:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0var_:
        if (r10 == 0) goto L_0x0var_;
    L_0x0var_:
        r2 = 1;
        goto L_0x0var_;
    L_0x0var_:
        r2 = 0;
    L_0x0var_:
        r44 = r43;
        r45 = r1;
        r46 = r11;
        r47 = r20;
        r48 = r6;
        r49 = r18;
        r50 = r2;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0f9c:
        r2 = 8;
        if (r12 != r2) goto L_0x0fbf;
    L_0x0fa0:
        if (r44 == 0) goto L_0x0fa7;
    L_0x0fa2:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0fa7:
        if (r10 == 0) goto L_0x0fab;
    L_0x0fa9:
        r2 = 1;
        goto L_0x0fac;
    L_0x0fab:
        r2 = 0;
    L_0x0fac:
        r44 = r43;
        r45 = r1;
        r46 = r11;
        r47 = r20;
        r48 = r6;
        r49 = r18;
        r50 = r2;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0fbf:
        r2 = 10;
        if (r12 != r2) goto L_0x1876;
    L_0x0fc3:
        if (r10 == 0) goto L_0x0fc7;
    L_0x0fc5:
        r2 = 1;
        goto L_0x0fc8;
    L_0x0fc7:
        r2 = 0;
    L_0x0fc8:
        r44 = r43;
        r45 = r1;
        r46 = r11;
        r47 = r20;
        r48 = r6;
        r49 = r18;
        r50 = r2;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50);	 Catch:{ Exception -> 0x0b38 }
        goto L_0x1876;
    L_0x0fdb:
        r0 = move-exception;
        r5 = r18;
        goto L_0x08cc;
    L_0x0fe0:
        r41 = r46;
        r40 = r48;
        r1 = r51;
        r11 = r57;
        r3 = r60;
        r56 = r6;
        r12 = r15;
        r8 = r28;
        r2 = r29;
        r6 = r44;
        r15 = r50;
        r10 = r9.layer;	 Catch:{ Exception -> 0x15b4 }
        r10 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r10);	 Catch:{ Exception -> 0x15b4 }
        r13 = 73;
        if (r10 < r13) goto L_0x1014;
    L_0x0fff:
        r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x15b4 }
        r10.<init>();	 Catch:{ Exception -> 0x15b4 }
        r13 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1));
        if (r13 == 0) goto L_0x1019;
    L_0x1008:
        r13 = r61;
        r10.grouped_id = r13;	 Catch:{ Exception -> 0x15b4 }
        r13 = r10.flags;	 Catch:{ Exception -> 0x15b4 }
        r14 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r13 = r13 | r14;
        r10.flags = r13;	 Catch:{ Exception -> 0x15b4 }
        goto L_0x1019;
    L_0x1014:
        r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x15b4 }
        r10.<init>();	 Catch:{ Exception -> 0x15b4 }
    L_0x1019:
        r13 = r5.ttl;	 Catch:{ Exception -> 0x15b4 }
        r10.ttl = r13;	 Catch:{ Exception -> 0x15b4 }
        if (r3 == 0) goto L_0x102d;
    L_0x101f:
        r13 = r60.isEmpty();	 Catch:{ Exception -> 0x15b4 }
        if (r13 != 0) goto L_0x102d;
    L_0x1025:
        r10.entities = r3;	 Catch:{ Exception -> 0x15b4 }
        r3 = r10.flags;	 Catch:{ Exception -> 0x15b4 }
        r3 = r3 | 128;
        r10.flags = r3;	 Catch:{ Exception -> 0x15b4 }
    L_0x102d:
        r13 = r5.reply_to_random_id;	 Catch:{ Exception -> 0x15b4 }
        r3 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1));
        if (r3 == 0) goto L_0x103e;
    L_0x1033:
        r13 = r5.reply_to_random_id;	 Catch:{ Exception -> 0x15b4 }
        r10.reply_to_random_id = r13;	 Catch:{ Exception -> 0x15b4 }
        r3 = r10.flags;	 Catch:{ Exception -> 0x15b4 }
        r13 = 8;
        r3 = r3 | r13;
        r10.flags = r3;	 Catch:{ Exception -> 0x15b4 }
    L_0x103e:
        r3 = r10.flags;	 Catch:{ Exception -> 0x15b4 }
        r3 = r3 | 512;
        r10.flags = r3;	 Catch:{ Exception -> 0x15b4 }
        if (r15 == 0) goto L_0x105e;
    L_0x1046:
        r3 = "bot_name";
        r3 = r15.get(r3);	 Catch:{ Exception -> 0x15b4 }
        if (r3 == 0) goto L_0x105e;
    L_0x104e:
        r3 = "bot_name";
        r3 = r15.get(r3);	 Catch:{ Exception -> 0x15b4 }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x15b4 }
        r10.via_bot_name = r3;	 Catch:{ Exception -> 0x15b4 }
        r3 = r10.flags;	 Catch:{ Exception -> 0x15b4 }
        r3 = r3 | 2048;
        r10.flags = r3;	 Catch:{ Exception -> 0x15b4 }
    L_0x105e:
        r13 = r5.random_id;	 Catch:{ Exception -> 0x15b4 }
        r10.random_id = r13;	 Catch:{ Exception -> 0x15b4 }
        r10.message = r8;	 Catch:{ Exception -> 0x15b4 }
        r3 = 1;
        if (r12 != r3) goto L_0x10c5;
    L_0x1067:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x15b4 }
        if (r1 == 0) goto L_0x108b;
    L_0x106b:
        r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue;	 Catch:{ Exception -> 0x15b4 }
        r1.<init>();	 Catch:{ Exception -> 0x15b4 }
        r10.media = r1;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r2.address;	 Catch:{ Exception -> 0x15b4 }
        r1.address = r3;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r2.title;	 Catch:{ Exception -> 0x15b4 }
        r1.title = r3;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r2.provider;	 Catch:{ Exception -> 0x15b4 }
        r1.provider = r3;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r2.venue_id;	 Catch:{ Exception -> 0x15b4 }
        r1.venue_id = r3;	 Catch:{ Exception -> 0x15b4 }
        goto L_0x1092;
    L_0x108b:
        r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint;	 Catch:{ Exception -> 0x15b4 }
        r1.<init>();	 Catch:{ Exception -> 0x15b4 }
        r10.media = r1;	 Catch:{ Exception -> 0x15b4 }
    L_0x1092:
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r2.geo;	 Catch:{ Exception -> 0x15b4 }
        r3 = r3.lat;	 Catch:{ Exception -> 0x15b4 }
        r1.lat = r3;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r2 = r2.geo;	 Catch:{ Exception -> 0x15b4 }
        r2 = r2._long;	 Catch:{ Exception -> 0x15b4 }
        r1._long = r2;	 Catch:{ Exception -> 0x15b4 }
        r1 = r43.getSecretChatHelper();	 Catch:{ Exception -> 0x15b4 }
        r2 = r11.messageOwner;	 Catch:{ Exception -> 0x15b4 }
        r3 = 0;
        r4 = 0;
        r46 = r1;
        r47 = r10;
        r48 = r2;
        r49 = r9;
        r50 = r3;
        r51 = r4;
        r52 = r11;
        r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52);	 Catch:{ Exception -> 0x15b4 }
        r13 = r53;
        r18 = r5;
        r44 = r12;
        r2 = r20;
        goto L_0x142a;
    L_0x10c5:
        r2 = 2;
        if (r12 == r2) goto L_0x1436;
    L_0x10c8:
        r2 = 9;
        if (r12 != r2) goto L_0x10d0;
    L_0x10cc:
        if (r6 == 0) goto L_0x10d0;
    L_0x10ce:
        goto L_0x1436;
    L_0x10d0:
        r2 = 3;
        if (r12 != r2) goto L_0x120a;
    L_0x10d3:
        r2 = r4.thumbs;	 Catch:{ Exception -> 0x15b4 }
        r2 = r7.getThumbForSecretChat(r2);	 Catch:{ Exception -> 0x15b4 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r2);	 Catch:{ Exception -> 0x15b4 }
        r3 = org.telegram.messenger.MessageObject.isNewGifDocument(r4);	 Catch:{ Exception -> 0x15b4 }
        if (r3 != 0) goto L_0x1109;
    L_0x10e2:
        r3 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r4);	 Catch:{ Exception -> 0x15b4 }
        if (r3 == 0) goto L_0x10e9;
    L_0x10e8:
        goto L_0x1109;
    L_0x10e9:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo;	 Catch:{ Exception -> 0x15b4 }
        r3.<init>();	 Catch:{ Exception -> 0x15b4 }
        r10.media = r3;	 Catch:{ Exception -> 0x15b4 }
        if (r2 == 0) goto L_0x10ff;
    L_0x10f2:
        r3 = r2.bytes;	 Catch:{ Exception -> 0x15b4 }
        if (r3 == 0) goto L_0x10ff;
    L_0x10f6:
        r3 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r3;	 Catch:{ Exception -> 0x15b4 }
        r6 = r2.bytes;	 Catch:{ Exception -> 0x15b4 }
        r3.thumb = r6;	 Catch:{ Exception -> 0x15b4 }
        goto L_0x112e;
    L_0x10ff:
        r3 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r3;	 Catch:{ Exception -> 0x15b4 }
        r6 = 0;
        r8 = new byte[r6];	 Catch:{ Exception -> 0x15b4 }
        r3.thumb = r8;	 Catch:{ Exception -> 0x15b4 }
        goto L_0x112e;
    L_0x1109:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x15b4 }
        r3.<init>();	 Catch:{ Exception -> 0x15b4 }
        r10.media = r3;	 Catch:{ Exception -> 0x15b4 }
        r3 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r6 = r4.attributes;	 Catch:{ Exception -> 0x15b4 }
        r3.attributes = r6;	 Catch:{ Exception -> 0x15b4 }
        if (r2 == 0) goto L_0x1125;
    L_0x1118:
        r3 = r2.bytes;	 Catch:{ Exception -> 0x15b4 }
        if (r3 == 0) goto L_0x1125;
    L_0x111c:
        r3 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r3;	 Catch:{ Exception -> 0x15b4 }
        r6 = r2.bytes;	 Catch:{ Exception -> 0x15b4 }
        r3.thumb = r6;	 Catch:{ Exception -> 0x15b4 }
        goto L_0x112e;
    L_0x1125:
        r3 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r3;	 Catch:{ Exception -> 0x15b4 }
        r6 = 0;
        r8 = new byte[r6];	 Catch:{ Exception -> 0x15b4 }
        r3.thumb = r8;	 Catch:{ Exception -> 0x15b4 }
    L_0x112e:
        r3 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3.caption = r1;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = "video/mp4";
        r1.mime_type = r3;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r4.size;	 Catch:{ Exception -> 0x15b4 }
        r1.size = r3;	 Catch:{ Exception -> 0x15b4 }
        r1 = 0;
    L_0x1140:
        r3 = r4.attributes;	 Catch:{ Exception -> 0x15b4 }
        r3 = r3.size();	 Catch:{ Exception -> 0x15b4 }
        if (r1 >= r3) goto L_0x116a;
    L_0x1148:
        r3 = r4.attributes;	 Catch:{ Exception -> 0x15b4 }
        r3 = r3.get(r1);	 Catch:{ Exception -> 0x15b4 }
        r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3;	 Catch:{ Exception -> 0x15b4 }
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x15b4 }
        if (r6 == 0) goto L_0x1167;
    L_0x1154:
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r6 = r3.w;	 Catch:{ Exception -> 0x15b4 }
        r1.w = r6;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r6 = r3.h;	 Catch:{ Exception -> 0x15b4 }
        r1.h = r6;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r3.duration;	 Catch:{ Exception -> 0x15b4 }
        r1.duration = r3;	 Catch:{ Exception -> 0x15b4 }
        goto L_0x116a;
    L_0x1167:
        r1 = r1 + 1;
        goto L_0x1140;
    L_0x116a:
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r2.h;	 Catch:{ Exception -> 0x15b4 }
        r1.thumb_h = r3;	 Catch:{ Exception -> 0x15b4 }
        r1 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r2 = r2.w;	 Catch:{ Exception -> 0x15b4 }
        r1.thumb_w = r2;	 Catch:{ Exception -> 0x15b4 }
        r1 = r4.key;	 Catch:{ Exception -> 0x15b4 }
        if (r1 == 0) goto L_0x11b9;
    L_0x117a:
        r1 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x117f;
    L_0x117e:
        goto L_0x11b9;
    L_0x117f:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x15b4 }
        r1.<init>();	 Catch:{ Exception -> 0x15b4 }
        r2 = r4.id;	 Catch:{ Exception -> 0x15b4 }
        r1.id = r2;	 Catch:{ Exception -> 0x15b4 }
        r2 = r4.access_hash;	 Catch:{ Exception -> 0x15b4 }
        r1.access_hash = r2;	 Catch:{ Exception -> 0x15b4 }
        r2 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r4.key;	 Catch:{ Exception -> 0x15b4 }
        r2.key = r3;	 Catch:{ Exception -> 0x15b4 }
        r2 = r10.media;	 Catch:{ Exception -> 0x15b4 }
        r3 = r4.iv;	 Catch:{ Exception -> 0x15b4 }
        r2.iv = r3;	 Catch:{ Exception -> 0x15b4 }
        r2 = r43.getSecretChatHelper();	 Catch:{ Exception -> 0x15b4 }
        r3 = r11.messageOwner;	 Catch:{ Exception -> 0x15b4 }
        r4 = 0;
        r46 = r2;
        r47 = r10;
        r48 = r3;
        r49 = r9;
        r50 = r1;
        r51 = r4;
        r52 = r11;
        r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52);	 Catch:{ Exception -> 0x15b4 }
        r13 = r53;
        r6 = r56;
        r1 = r64;
        r2 = r20;
        goto L_0x1204;
    L_0x11b9:
        if (r56 != 0) goto L_0x11f1;
    L_0x11bb:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x15b4 }
        r13 = r53;
        r6.<init>(r13);	 Catch:{ Exception -> 0x15b4 }
        r6.encryptedChat = r9;	 Catch:{ Exception -> 0x15b4 }
        r1 = 1;
        r6.type = r1;	 Catch:{ Exception -> 0x15b4 }
        r6.sendEncryptedRequest = r10;	 Catch:{ Exception -> 0x15b4 }
        r2 = r20;
        r6.originalPath = r2;	 Catch:{ Exception -> 0x15b4 }
        r6.obj = r11;	 Catch:{ Exception -> 0x15b4 }
        if (r15 == 0) goto L_0x11e0;
    L_0x11d1:
        r3 = r27;
        r1 = r15.containsKey(r3);	 Catch:{ Exception -> 0x15b4 }
        if (r1 == 0) goto L_0x11e0;
    L_0x11d9:
        r1 = r15.get(r3);	 Catch:{ Exception -> 0x15b4 }
        r6.parentObject = r1;	 Catch:{ Exception -> 0x15b4 }
        goto L_0x11e4;
    L_0x11e0:
        r8 = r18;
        r6.parentObject = r8;	 Catch:{ Exception -> 0x15b4 }
    L_0x11e4:
        r1 = 1;
        r6.performMediaUpload = r1;	 Catch:{ Exception -> 0x15b4 }
        r1 = r64;
        if (r1 == 0) goto L_0x11ed;
    L_0x11eb:
        r3 = 1;
        goto L_0x11ee;
    L_0x11ed:
        r3 = 0;
    L_0x11ee:
        r6.scheduled = r3;	 Catch:{ Exception -> 0x1879 }
        goto L_0x11f9;
    L_0x11f1:
        r13 = r53;
        r1 = r64;
        r2 = r20;
        r6 = r56;
    L_0x11f9:
        r3 = r40;
        r6.videoEditedInfo = r3;	 Catch:{ Exception -> 0x1879 }
        r3 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1));
        if (r3 != 0) goto L_0x1204;
    L_0x1201:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x1879 }
    L_0x1204:
        r18 = r5;
        r44 = r12;
        goto L_0x1552;
    L_0x120a:
        r13 = r53;
        r6 = r64;
        r8 = r18;
        r2 = r20;
        r3 = r27;
        r18 = r5;
        r5 = 6;
        if (r12 != r5) goto L_0x125a;
    L_0x1219:
        r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact;	 Catch:{ Exception -> 0x1255 }
        r1.<init>();	 Catch:{ Exception -> 0x1255 }
        r10.media = r1;	 Catch:{ Exception -> 0x1255 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r3 = r41;
        r4 = r3.phone;	 Catch:{ Exception -> 0x1255 }
        r1.phone_number = r4;	 Catch:{ Exception -> 0x1255 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r4 = r3.first_name;	 Catch:{ Exception -> 0x1255 }
        r1.first_name = r4;	 Catch:{ Exception -> 0x1255 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r4 = r3.last_name;	 Catch:{ Exception -> 0x1255 }
        r1.last_name = r4;	 Catch:{ Exception -> 0x1255 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r3 = r3.id;	 Catch:{ Exception -> 0x1255 }
        r1.user_id = r3;	 Catch:{ Exception -> 0x1255 }
        r1 = r43.getSecretChatHelper();	 Catch:{ Exception -> 0x1255 }
        r3 = r11.messageOwner;	 Catch:{ Exception -> 0x1255 }
        r4 = 0;
        r5 = 0;
        r46 = r1;
        r47 = r10;
        r48 = r3;
        r49 = r9;
        r50 = r4;
        r51 = r5;
        r52 = r11;
        r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52);	 Catch:{ Exception -> 0x1255 }
        goto L_0x12dc;
    L_0x1255:
        r0 = move-exception;
        r2 = r0;
        r1 = r6;
        goto L_0x0cce;
    L_0x125a:
        r5 = 7;
        if (r12 == r5) goto L_0x12e0;
    L_0x125d:
        r5 = 9;
        if (r12 != r5) goto L_0x1265;
    L_0x1261:
        if (r4 == 0) goto L_0x1265;
    L_0x1263:
        goto L_0x12e0;
    L_0x1265:
        r3 = 8;
        if (r12 != r3) goto L_0x12dc;
    L_0x1269:
        r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x1255 }
        r3.<init>(r13);	 Catch:{ Exception -> 0x1255 }
        r3.encryptedChat = r9;	 Catch:{ Exception -> 0x1255 }
        r3.sendEncryptedRequest = r10;	 Catch:{ Exception -> 0x1255 }
        r3.obj = r11;	 Catch:{ Exception -> 0x1255 }
        r5 = 3;
        r3.type = r5;	 Catch:{ Exception -> 0x1255 }
        r3.parentObject = r8;	 Catch:{ Exception -> 0x1255 }
        r5 = 1;
        r3.performMediaUpload = r5;	 Catch:{ Exception -> 0x1255 }
        if (r6 == 0) goto L_0x1280;
    L_0x127e:
        r5 = 1;
        goto L_0x1281;
    L_0x1280:
        r5 = 0;
    L_0x1281:
        r3.scheduled = r5;	 Catch:{ Exception -> 0x1255 }
        r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x1255 }
        r5.<init>();	 Catch:{ Exception -> 0x1255 }
        r10.media = r5;	 Catch:{ Exception -> 0x1255 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r8 = r4.attributes;	 Catch:{ Exception -> 0x1255 }
        r5.attributes = r8;	 Catch:{ Exception -> 0x1255 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r5.caption = r1;	 Catch:{ Exception -> 0x1255 }
        r1 = r4.thumbs;	 Catch:{ Exception -> 0x1255 }
        r1 = r7.getThumbForSecretChat(r1);	 Catch:{ Exception -> 0x1255 }
        if (r1 == 0) goto L_0x12b4;
    L_0x129c:
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1);	 Catch:{ Exception -> 0x1255 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r5 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r5;	 Catch:{ Exception -> 0x1255 }
        r8 = r1.bytes;	 Catch:{ Exception -> 0x1255 }
        r5.thumb = r8;	 Catch:{ Exception -> 0x1255 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r8 = r1.h;	 Catch:{ Exception -> 0x1255 }
        r5.thumb_h = r8;	 Catch:{ Exception -> 0x1255 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r1 = r1.w;	 Catch:{ Exception -> 0x1255 }
        r5.thumb_w = r1;	 Catch:{ Exception -> 0x1255 }
        goto L_0x12c5;
    L_0x12b4:
        r1 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r1;	 Catch:{ Exception -> 0x1255 }
        r5 = 0;
        r8 = new byte[r5];	 Catch:{ Exception -> 0x1255 }
        r1.thumb = r8;	 Catch:{ Exception -> 0x1255 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r1.thumb_h = r5;	 Catch:{ Exception -> 0x1255 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r1.thumb_w = r5;	 Catch:{ Exception -> 0x1255 }
    L_0x12c5:
        r1 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r5 = r4.mime_type;	 Catch:{ Exception -> 0x1255 }
        r1.mime_type = r5;	 Catch:{ Exception -> 0x1255 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r4 = r4.size;	 Catch:{ Exception -> 0x1255 }
        r1.size = r4;	 Catch:{ Exception -> 0x1255 }
        r3.originalPath = r2;	 Catch:{ Exception -> 0x1255 }
        r7.performSendDelayedMessage(r3);	 Catch:{ Exception -> 0x1255 }
        r1 = r6;
        r44 = r12;
        r6 = r3;
        goto L_0x1552;
    L_0x12dc:
        r44 = r12;
        goto L_0x142a;
    L_0x12e0:
        r5 = org.telegram.messenger.MessageObject.isStickerDocument(r4);	 Catch:{ Exception -> 0x1430 }
        if (r5 != 0) goto L_0x13ba;
    L_0x12e6:
        r5 = 0;
        r19 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r5);	 Catch:{ Exception -> 0x1430 }
        if (r19 == 0) goto L_0x12ef;
    L_0x12ed:
        goto L_0x13ba;
    L_0x12ef:
        r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x1430 }
        r5.<init>();	 Catch:{ Exception -> 0x1430 }
        r10.media = r5;	 Catch:{ Exception -> 0x1430 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r44 = r12;
        r12 = r4.attributes;	 Catch:{ Exception -> 0x1430 }
        r5.attributes = r12;	 Catch:{ Exception -> 0x1430 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r5.caption = r1;	 Catch:{ Exception -> 0x1430 }
        r1 = r4.thumbs;	 Catch:{ Exception -> 0x1430 }
        r1 = r7.getThumbForSecretChat(r1);	 Catch:{ Exception -> 0x1430 }
        if (r1 == 0) goto L_0x1322;
    L_0x130a:
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1);	 Catch:{ Exception -> 0x1255 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r5 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r5;	 Catch:{ Exception -> 0x1255 }
        r12 = r1.bytes;	 Catch:{ Exception -> 0x1255 }
        r5.thumb = r12;	 Catch:{ Exception -> 0x1255 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r12 = r1.h;	 Catch:{ Exception -> 0x1255 }
        r5.thumb_h = r12;	 Catch:{ Exception -> 0x1255 }
        r5 = r10.media;	 Catch:{ Exception -> 0x1255 }
        r1 = r1.w;	 Catch:{ Exception -> 0x1255 }
        r5.thumb_w = r1;	 Catch:{ Exception -> 0x1255 }
        goto L_0x1333;
    L_0x1322:
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r1;	 Catch:{ Exception -> 0x1430 }
        r5 = 0;
        r12 = new byte[r5];	 Catch:{ Exception -> 0x1430 }
        r1.thumb = r12;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r1.thumb_h = r5;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r1.thumb_w = r5;	 Catch:{ Exception -> 0x1430 }
    L_0x1333:
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r5 = r4.size;	 Catch:{ Exception -> 0x1430 }
        r1.size = r5;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r5 = r4.mime_type;	 Catch:{ Exception -> 0x1430 }
        r1.mime_type = r5;	 Catch:{ Exception -> 0x1430 }
        r1 = r4.key;	 Catch:{ Exception -> 0x1430 }
        if (r1 != 0) goto L_0x1388;
    L_0x1343:
        r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x1255 }
        r1.<init>(r13);	 Catch:{ Exception -> 0x1255 }
        r1.originalPath = r2;	 Catch:{ Exception -> 0x1255 }
        r1.sendEncryptedRequest = r10;	 Catch:{ Exception -> 0x1255 }
        r4 = 2;
        r1.type = r4;	 Catch:{ Exception -> 0x1255 }
        r1.obj = r11;	 Catch:{ Exception -> 0x1255 }
        if (r15 == 0) goto L_0x1360;
    L_0x1353:
        r4 = r15.containsKey(r3);	 Catch:{ Exception -> 0x1255 }
        if (r4 == 0) goto L_0x1360;
    L_0x1359:
        r3 = r15.get(r3);	 Catch:{ Exception -> 0x1255 }
        r1.parentObject = r3;	 Catch:{ Exception -> 0x1255 }
        goto L_0x1362;
    L_0x1360:
        r1.parentObject = r8;	 Catch:{ Exception -> 0x1255 }
    L_0x1362:
        r1.encryptedChat = r9;	 Catch:{ Exception -> 0x1255 }
        r3 = 1;
        r1.performMediaUpload = r3;	 Catch:{ Exception -> 0x1255 }
        r4 = r55;
        if (r4 == 0) goto L_0x137b;
    L_0x136b:
        r3 = r55.length();	 Catch:{ Exception -> 0x1255 }
        if (r3 <= 0) goto L_0x137b;
    L_0x1371:
        r3 = "http";
        r3 = r4.startsWith(r3);	 Catch:{ Exception -> 0x1255 }
        if (r3 == 0) goto L_0x137b;
    L_0x1379:
        r1.httpLocation = r4;	 Catch:{ Exception -> 0x1255 }
    L_0x137b:
        if (r6 == 0) goto L_0x137f;
    L_0x137d:
        r3 = 1;
        goto L_0x1380;
    L_0x137f:
        r3 = 0;
    L_0x1380:
        r1.scheduled = r3;	 Catch:{ Exception -> 0x1255 }
        r7.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x1255 }
        r6 = r1;
        goto L_0x142c;
    L_0x1388:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x1430 }
        r1.<init>();	 Catch:{ Exception -> 0x1430 }
        r5 = r4.id;	 Catch:{ Exception -> 0x1430 }
        r1.id = r5;	 Catch:{ Exception -> 0x1430 }
        r5 = r4.access_hash;	 Catch:{ Exception -> 0x1430 }
        r1.access_hash = r5;	 Catch:{ Exception -> 0x1430 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r5 = r4.key;	 Catch:{ Exception -> 0x1430 }
        r3.key = r5;	 Catch:{ Exception -> 0x1430 }
        r3 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r4 = r4.iv;	 Catch:{ Exception -> 0x1430 }
        r3.iv = r4;	 Catch:{ Exception -> 0x1430 }
        r3 = r43.getSecretChatHelper();	 Catch:{ Exception -> 0x1430 }
        r4 = r11.messageOwner;	 Catch:{ Exception -> 0x1430 }
        r5 = 0;
        r46 = r3;
        r47 = r10;
        r48 = r4;
        r49 = r9;
        r50 = r1;
        r51 = r5;
        r52 = r11;
        r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52);	 Catch:{ Exception -> 0x1430 }
        goto L_0x142a;
    L_0x13ba:
        r44 = r12;
        r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument;	 Catch:{ Exception -> 0x1430 }
        r1.<init>();	 Catch:{ Exception -> 0x1430 }
        r10.media = r1;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r5 = r4.id;	 Catch:{ Exception -> 0x1430 }
        r1.id = r5;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r3 = r4.date;	 Catch:{ Exception -> 0x1430 }
        r1.date = r3;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r5 = r4.access_hash;	 Catch:{ Exception -> 0x1430 }
        r1.access_hash = r5;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r3 = r4.mime_type;	 Catch:{ Exception -> 0x1430 }
        r1.mime_type = r3;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r3 = r4.size;	 Catch:{ Exception -> 0x1430 }
        r1.size = r3;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r3 = r4.dc_id;	 Catch:{ Exception -> 0x1430 }
        r1.dc_id = r3;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r3 = r4.attributes;	 Catch:{ Exception -> 0x1430 }
        r1.attributes = r3;	 Catch:{ Exception -> 0x1430 }
        r1 = r4.thumbs;	 Catch:{ Exception -> 0x1430 }
        r1 = r7.getThumbForSecretChat(r1);	 Catch:{ Exception -> 0x1430 }
        if (r1 == 0) goto L_0x13fc;
    L_0x13f5:
        r3 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r3;	 Catch:{ Exception -> 0x1430 }
        r3.thumb = r1;	 Catch:{ Exception -> 0x1430 }
        goto L_0x1411;
    L_0x13fc:
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r1;	 Catch:{ Exception -> 0x1430 }
        r3 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x1430 }
        r3.<init>();	 Catch:{ Exception -> 0x1430 }
        r1.thumb = r3;	 Catch:{ Exception -> 0x1430 }
        r1 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r1;	 Catch:{ Exception -> 0x1430 }
        r1 = r1.thumb;	 Catch:{ Exception -> 0x1430 }
        r3 = "s";
        r1.type = r3;	 Catch:{ Exception -> 0x1430 }
    L_0x1411:
        r1 = r43.getSecretChatHelper();	 Catch:{ Exception -> 0x1430 }
        r3 = r11.messageOwner;	 Catch:{ Exception -> 0x1430 }
        r4 = 0;
        r5 = 0;
        r46 = r1;
        r47 = r10;
        r48 = r3;
        r49 = r9;
        r50 = r4;
        r51 = r5;
        r52 = r11;
        r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52);	 Catch:{ Exception -> 0x1430 }
    L_0x142a:
        r6 = r56;
    L_0x142c:
        r1 = r64;
        goto L_0x1552;
    L_0x1430:
        r0 = move-exception;
        r1 = r64;
    L_0x1433:
        r2 = r0;
        goto L_0x0cce;
    L_0x1436:
        r13 = r53;
        r4 = r55;
        r44 = r12;
        r8 = r18;
        r2 = r20;
        r3 = r27;
        r18 = r5;
        r5 = r1;
        r1 = r64;
        r12 = r6.sizes;	 Catch:{ Exception -> 0x15af }
        r4 = 0;
        r12 = r12.get(r4);	 Catch:{ Exception -> 0x15af }
        r12 = (org.telegram.tgnet.TLRPC.PhotoSize) r12;	 Catch:{ Exception -> 0x15af }
        r4 = r6.sizes;	 Catch:{ Exception -> 0x15af }
        r1 = r6.sizes;	 Catch:{ Exception -> 0x15ab }
        r1 = r1.size();	 Catch:{ Exception -> 0x15ab }
        r19 = 1;
        r1 = r1 + -1;
        r1 = r4.get(r1);	 Catch:{ Exception -> 0x15ab }
        r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1;	 Catch:{ Exception -> 0x15ab }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r12);	 Catch:{ Exception -> 0x15ab }
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto;	 Catch:{ Exception -> 0x15ab }
        r4.<init>();	 Catch:{ Exception -> 0x15ab }
        r10.media = r4;	 Catch:{ Exception -> 0x15ab }
        r4 = r10.media;	 Catch:{ Exception -> 0x15ab }
        r4.caption = r5;	 Catch:{ Exception -> 0x15ab }
        r4 = r12.bytes;	 Catch:{ Exception -> 0x15ab }
        if (r4 == 0) goto L_0x147f;
    L_0x1474:
        r4 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x1430 }
        r5 = r12.bytes;	 Catch:{ Exception -> 0x1430 }
        r4.thumb = r5;	 Catch:{ Exception -> 0x1430 }
        r19 = r6;
        goto L_0x148a;
    L_0x147f:
        r4 = r10.media;	 Catch:{ Exception -> 0x15ab }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x15ab }
        r19 = r6;
        r5 = 0;
        r6 = new byte[r5];	 Catch:{ Exception -> 0x15ab }
        r4.thumb = r6;	 Catch:{ Exception -> 0x15ab }
    L_0x148a:
        r4 = r10.media;	 Catch:{ Exception -> 0x15ab }
        r5 = r12.h;	 Catch:{ Exception -> 0x15ab }
        r4.thumb_h = r5;	 Catch:{ Exception -> 0x15ab }
        r4 = r10.media;	 Catch:{ Exception -> 0x15ab }
        r5 = r12.w;	 Catch:{ Exception -> 0x15ab }
        r4.thumb_w = r5;	 Catch:{ Exception -> 0x15ab }
        r4 = r10.media;	 Catch:{ Exception -> 0x15ab }
        r5 = r1.w;	 Catch:{ Exception -> 0x15ab }
        r4.w = r5;	 Catch:{ Exception -> 0x15ab }
        r4 = r10.media;	 Catch:{ Exception -> 0x15ab }
        r5 = r1.h;	 Catch:{ Exception -> 0x15ab }
        r4.h = r5;	 Catch:{ Exception -> 0x15ab }
        r4 = r10.media;	 Catch:{ Exception -> 0x15ab }
        r5 = r1.size;	 Catch:{ Exception -> 0x15ab }
        r4.size = r5;	 Catch:{ Exception -> 0x15ab }
        r4 = r1.location;	 Catch:{ Exception -> 0x15ab }
        r4 = r4.key;	 Catch:{ Exception -> 0x15ab }
        if (r4 == 0) goto L_0x14ee;
    L_0x14ae:
        r4 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1));
        if (r4 == 0) goto L_0x14b3;
    L_0x14b2:
        goto L_0x14ee;
    L_0x14b3:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x1430 }
        r3.<init>();	 Catch:{ Exception -> 0x1430 }
        r4 = r1.location;	 Catch:{ Exception -> 0x1430 }
        r4 = r4.volume_id;	 Catch:{ Exception -> 0x1430 }
        r3.id = r4;	 Catch:{ Exception -> 0x1430 }
        r4 = r1.location;	 Catch:{ Exception -> 0x1430 }
        r4 = r4.secret;	 Catch:{ Exception -> 0x1430 }
        r3.access_hash = r4;	 Catch:{ Exception -> 0x1430 }
        r4 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r5 = r1.location;	 Catch:{ Exception -> 0x1430 }
        r5 = r5.key;	 Catch:{ Exception -> 0x1430 }
        r4.key = r5;	 Catch:{ Exception -> 0x1430 }
        r4 = r10.media;	 Catch:{ Exception -> 0x1430 }
        r1 = r1.location;	 Catch:{ Exception -> 0x1430 }
        r1 = r1.iv;	 Catch:{ Exception -> 0x1430 }
        r4.iv = r1;	 Catch:{ Exception -> 0x1430 }
        r1 = r43.getSecretChatHelper();	 Catch:{ Exception -> 0x1430 }
        r4 = r11.messageOwner;	 Catch:{ Exception -> 0x1430 }
        r5 = 0;
        r46 = r1;
        r47 = r10;
        r48 = r4;
        r49 = r9;
        r50 = r3;
        r51 = r5;
        r52 = r11;
        r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52);	 Catch:{ Exception -> 0x1430 }
        goto L_0x142a;
    L_0x14ee:
        if (r56 != 0) goto L_0x151e;
    L_0x14f0:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x1430 }
        r6.<init>(r13);	 Catch:{ Exception -> 0x1430 }
        r6.encryptedChat = r9;	 Catch:{ Exception -> 0x1430 }
        r1 = 0;
        r6.type = r1;	 Catch:{ Exception -> 0x1430 }
        r6.originalPath = r2;	 Catch:{ Exception -> 0x1430 }
        r6.sendEncryptedRequest = r10;	 Catch:{ Exception -> 0x1430 }
        r6.obj = r11;	 Catch:{ Exception -> 0x1430 }
        if (r15 == 0) goto L_0x150f;
    L_0x1502:
        r1 = r15.containsKey(r3);	 Catch:{ Exception -> 0x1430 }
        if (r1 == 0) goto L_0x150f;
    L_0x1508:
        r1 = r15.get(r3);	 Catch:{ Exception -> 0x1430 }
        r6.parentObject = r1;	 Catch:{ Exception -> 0x1430 }
        goto L_0x1511;
    L_0x150f:
        r6.parentObject = r8;	 Catch:{ Exception -> 0x1430 }
    L_0x1511:
        r1 = 1;
        r6.performMediaUpload = r1;	 Catch:{ Exception -> 0x1430 }
        r1 = r64;
        if (r1 == 0) goto L_0x151a;
    L_0x1518:
        r3 = 1;
        goto L_0x151b;
    L_0x151a:
        r3 = 0;
    L_0x151b:
        r6.scheduled = r3;	 Catch:{ Exception -> 0x155f }
        goto L_0x1522;
    L_0x151e:
        r1 = r64;
        r6 = r56;
    L_0x1522:
        r3 = android.text.TextUtils.isEmpty(r55);	 Catch:{ Exception -> 0x15af }
        if (r3 != 0) goto L_0x1535;
    L_0x1528:
        r3 = "http";
        r4 = r55;
        r3 = r4.startsWith(r3);	 Catch:{ Exception -> 0x155f }
        if (r3 == 0) goto L_0x1535;
    L_0x1532:
        r6.httpLocation = r4;	 Catch:{ Exception -> 0x155f }
        goto L_0x154b;
    L_0x1535:
        r8 = r19;
        r3 = r8.sizes;	 Catch:{ Exception -> 0x15af }
        r4 = r8.sizes;	 Catch:{ Exception -> 0x15af }
        r4 = r4.size();	 Catch:{ Exception -> 0x15af }
        r5 = 1;
        r4 = r4 - r5;
        r3 = r3.get(r4);	 Catch:{ Exception -> 0x15af }
        r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3;	 Catch:{ Exception -> 0x15af }
        r6.photoSize = r3;	 Catch:{ Exception -> 0x15af }
        r6.locationParent = r8;	 Catch:{ Exception -> 0x15af }
    L_0x154b:
        r3 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1));
        if (r3 != 0) goto L_0x1552;
    L_0x154f:
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x155f }
    L_0x1552:
        r3 = (r61 > r16 ? 1 : (r61 == r16 ? 0 : -1));
        if (r3 == 0) goto L_0x159b;
    L_0x1556:
        r3 = r6.sendEncryptedRequest;	 Catch:{ Exception -> 0x15af }
        if (r3 == 0) goto L_0x1562;
    L_0x155a:
        r3 = r6.sendEncryptedRequest;	 Catch:{ Exception -> 0x155f }
        r3 = (org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r3;	 Catch:{ Exception -> 0x155f }
        goto L_0x1569;
    L_0x155f:
        r0 = move-exception;
        goto L_0x1433;
    L_0x1562:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;	 Catch:{ Exception -> 0x15af }
        r3.<init>();	 Catch:{ Exception -> 0x15af }
        r6.sendEncryptedRequest = r3;	 Catch:{ Exception -> 0x15af }
    L_0x1569:
        r4 = r6.messageObjects;	 Catch:{ Exception -> 0x15af }
        r4.add(r11);	 Catch:{ Exception -> 0x15af }
        r4 = r6.messages;	 Catch:{ Exception -> 0x15af }
        r5 = r18;
        r4.add(r5);	 Catch:{ Exception -> 0x1879 }
        r4 = r6.originalPaths;	 Catch:{ Exception -> 0x1879 }
        r4.add(r2);	 Catch:{ Exception -> 0x1879 }
        r2 = 1;
        r6.performMediaUpload = r2;	 Catch:{ Exception -> 0x1879 }
        r2 = r3.messages;	 Catch:{ Exception -> 0x1879 }
        r2.add(r10);	 Catch:{ Exception -> 0x1879 }
        r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x1879 }
        r2.<init>();	 Catch:{ Exception -> 0x1879 }
        r4 = r44;
        r8 = 3;
        if (r4 != r8) goto L_0x158e;
    L_0x158c:
        r16 = 1;
    L_0x158e:
        r8 = r16;
        r2.id = r8;	 Catch:{ Exception -> 0x1879 }
        r3 = r3.files;	 Catch:{ Exception -> 0x1879 }
        r3.add(r2);	 Catch:{ Exception -> 0x1879 }
        r7.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x1879 }
        goto L_0x159d;
    L_0x159b:
        r5 = r18;
    L_0x159d:
        r2 = r59;
        if (r2 != 0) goto L_0x1876;
    L_0x15a1:
        r2 = r43.getMediaDataController();	 Catch:{ Exception -> 0x1879 }
        r3 = 0;
        r2.cleanDraft(r13, r3);	 Catch:{ Exception -> 0x1879 }
        goto L_0x1876;
    L_0x15ab:
        r0 = move-exception;
        r1 = r64;
        goto L_0x15b0;
    L_0x15af:
        r0 = move-exception;
    L_0x15b0:
        r5 = r18;
        goto L_0x1882;
    L_0x15b4:
        r0 = move-exception;
        r1 = r64;
        goto L_0x1882;
    L_0x15b9:
        r12 = r50;
        r11 = r57;
        r2 = r59;
        r8 = r3;
        r1 = r10;
        r4 = r15;
        r26 = r18;
        r3 = 4;
        r15 = r52;
        if (r4 != r3) goto L_0x169d;
    L_0x15c9:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;	 Catch:{ Exception -> 0x1879 }
        r3.<init>();	 Catch:{ Exception -> 0x1879 }
        r3.to_peer = r8;	 Catch:{ Exception -> 0x1879 }
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x1879 }
        r4 = r4.with_my_score;	 Catch:{ Exception -> 0x1879 }
        r3.with_my_score = r4;	 Catch:{ Exception -> 0x1879 }
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x1879 }
        r4 = r4.ttl;	 Catch:{ Exception -> 0x1879 }
        if (r4 == 0) goto L_0x1606;
    L_0x15dc:
        r4 = r43.getMessagesController();	 Catch:{ Exception -> 0x1879 }
        r6 = r2.messageOwner;	 Catch:{ Exception -> 0x1879 }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x1879 }
        r6 = -r6;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x1879 }
        r4 = r4.getChat(r6);	 Catch:{ Exception -> 0x1879 }
        r6 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;	 Catch:{ Exception -> 0x1879 }
        r6.<init>();	 Catch:{ Exception -> 0x1879 }
        r3.from_peer = r6;	 Catch:{ Exception -> 0x1879 }
        r6 = r3.from_peer;	 Catch:{ Exception -> 0x1879 }
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x1879 }
        r8 = r8.ttl;	 Catch:{ Exception -> 0x1879 }
        r8 = -r8;
        r6.channel_id = r8;	 Catch:{ Exception -> 0x1879 }
        if (r4 == 0) goto L_0x160d;
    L_0x15ff:
        r6 = r3.from_peer;	 Catch:{ Exception -> 0x1879 }
        r8 = r4.access_hash;	 Catch:{ Exception -> 0x1879 }
        r6.access_hash = r8;	 Catch:{ Exception -> 0x1879 }
        goto L_0x160d;
    L_0x1606:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;	 Catch:{ Exception -> 0x1879 }
        r4.<init>();	 Catch:{ Exception -> 0x1879 }
        r3.from_peer = r4;	 Catch:{ Exception -> 0x1879 }
    L_0x160d:
        if (r63 == 0) goto L_0x162e;
    L_0x160f:
        r4 = r7.currentAccount;	 Catch:{ Exception -> 0x1879 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x1879 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x1879 }
        r6.<init>();	 Catch:{ Exception -> 0x1879 }
        r6.append(r15);	 Catch:{ Exception -> 0x1879 }
        r6.append(r13);	 Catch:{ Exception -> 0x1879 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x1879 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x1879 }
        if (r4 == 0) goto L_0x162c;
    L_0x162b:
        goto L_0x162e;
    L_0x162c:
        r4 = 0;
        goto L_0x162f;
    L_0x162e:
        r4 = 1;
    L_0x162f:
        r3.silent = r4;	 Catch:{ Exception -> 0x1879 }
        if (r1 == 0) goto L_0x163b;
    L_0x1633:
        r3.schedule_date = r1;	 Catch:{ Exception -> 0x1879 }
        r4 = r3.flags;	 Catch:{ Exception -> 0x1879 }
        r4 = r4 | 1024;
        r3.flags = r4;	 Catch:{ Exception -> 0x1879 }
    L_0x163b:
        r4 = r3.random_id;	 Catch:{ Exception -> 0x1879 }
        r8 = r5.random_id;	 Catch:{ Exception -> 0x1879 }
        r6 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x1879 }
        r4.add(r6);	 Catch:{ Exception -> 0x1879 }
        r4 = r59.getId();	 Catch:{ Exception -> 0x1879 }
        if (r4 < 0) goto L_0x165a;
    L_0x164c:
        r4 = r3.id;	 Catch:{ Exception -> 0x1879 }
        r2 = r59.getId();	 Catch:{ Exception -> 0x1879 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1879 }
        r4.add(r2);	 Catch:{ Exception -> 0x1879 }
        goto L_0x1683;
    L_0x165a:
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x1879 }
        r4 = r4.fwd_msg_id;	 Catch:{ Exception -> 0x1879 }
        if (r4 == 0) goto L_0x166e;
    L_0x1660:
        r4 = r3.id;	 Catch:{ Exception -> 0x1879 }
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x1879 }
        r2 = r2.fwd_msg_id;	 Catch:{ Exception -> 0x1879 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1879 }
        r4.add(r2);	 Catch:{ Exception -> 0x1879 }
        goto L_0x1683;
    L_0x166e:
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x1879 }
        r4 = r4.fwd_from;	 Catch:{ Exception -> 0x1879 }
        if (r4 == 0) goto L_0x1683;
    L_0x1674:
        r4 = r3.id;	 Catch:{ Exception -> 0x1879 }
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x1879 }
        r2 = r2.fwd_from;	 Catch:{ Exception -> 0x1879 }
        r2 = r2.channel_post;	 Catch:{ Exception -> 0x1879 }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x1879 }
        r4.add(r2);	 Catch:{ Exception -> 0x1879 }
    L_0x1683:
        r2 = 0;
        r4 = 0;
        if (r1 == 0) goto L_0x1689;
    L_0x1687:
        r6 = 1;
        goto L_0x168a;
    L_0x1689:
        r6 = 0;
    L_0x168a:
        r44 = r43;
        r45 = r3;
        r46 = r11;
        r47 = r2;
        r48 = r4;
        r49 = r26;
        r50 = r6;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50);	 Catch:{ Exception -> 0x1879 }
        goto L_0x1876;
    L_0x169d:
        r3 = 9;
        if (r4 != r3) goto L_0x1876;
    L_0x16a1:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult;	 Catch:{ Exception -> 0x1879 }
        r3.<init>();	 Catch:{ Exception -> 0x1879 }
        r3.peer = r8;	 Catch:{ Exception -> 0x1879 }
        r8 = r5.random_id;	 Catch:{ Exception -> 0x1879 }
        r3.random_id = r8;	 Catch:{ Exception -> 0x1879 }
        r4 = "bot";
        r4 = r12.containsKey(r4);	 Catch:{ Exception -> 0x1879 }
        if (r4 != 0) goto L_0x16b6;
    L_0x16b4:
        r4 = 1;
        goto L_0x16b7;
    L_0x16b6:
        r4 = 0;
    L_0x16b7:
        r3.hide_via = r4;	 Catch:{ Exception -> 0x1879 }
        r4 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x1879 }
        if (r4 == 0) goto L_0x16c7;
    L_0x16bd:
        r4 = r3.flags;	 Catch:{ Exception -> 0x1879 }
        r6 = 1;
        r4 = r4 | r6;
        r3.flags = r4;	 Catch:{ Exception -> 0x1879 }
        r4 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x1879 }
        r3.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x1879 }
    L_0x16c7:
        if (r63 == 0) goto L_0x16e8;
    L_0x16c9:
        r4 = r7.currentAccount;	 Catch:{ Exception -> 0x1879 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x1879 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x1879 }
        r6.<init>();	 Catch:{ Exception -> 0x1879 }
        r6.append(r15);	 Catch:{ Exception -> 0x1879 }
        r6.append(r13);	 Catch:{ Exception -> 0x1879 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x1879 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x1879 }
        if (r4 == 0) goto L_0x16e6;
    L_0x16e5:
        goto L_0x16e8;
    L_0x16e6:
        r4 = 0;
        goto L_0x16e9;
    L_0x16e8:
        r4 = 1;
    L_0x16e9:
        r3.silent = r4;	 Catch:{ Exception -> 0x1879 }
        if (r1 == 0) goto L_0x16f5;
    L_0x16ed:
        r3.schedule_date = r1;	 Catch:{ Exception -> 0x1879 }
        r4 = r3.flags;	 Catch:{ Exception -> 0x1879 }
        r4 = r4 | 1024;
        r3.flags = r4;	 Catch:{ Exception -> 0x1879 }
    L_0x16f5:
        r4 = r19;
        r4 = r12.get(r4);	 Catch:{ Exception -> 0x1879 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x1879 }
        r4 = org.telegram.messenger.Utilities.parseLong(r4);	 Catch:{ Exception -> 0x1879 }
        r8 = r4.longValue();	 Catch:{ Exception -> 0x1879 }
        r3.query_id = r8;	 Catch:{ Exception -> 0x1879 }
        r4 = "id";
        r4 = r12.get(r4);	 Catch:{ Exception -> 0x1879 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x1879 }
        r3.id = r4;	 Catch:{ Exception -> 0x1879 }
        if (r2 != 0) goto L_0x171e;
    L_0x1713:
        r2 = 1;
        r3.clear_draft = r2;	 Catch:{ Exception -> 0x1879 }
        r2 = r43.getMediaDataController();	 Catch:{ Exception -> 0x1879 }
        r4 = 0;
        r2.cleanDraft(r13, r4);	 Catch:{ Exception -> 0x1879 }
    L_0x171e:
        r2 = 0;
        r4 = 0;
        if (r1 == 0) goto L_0x1724;
    L_0x1722:
        r6 = 1;
        goto L_0x1725;
    L_0x1724:
        r6 = 0;
    L_0x1725:
        r44 = r43;
        r45 = r3;
        r46 = r11;
        r47 = r2;
        r48 = r4;
        r49 = r26;
        r50 = r6;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50);	 Catch:{ Exception -> 0x1879 }
        goto L_0x1876;
    L_0x1738:
        r12 = r50;
        r15 = r52;
        r11 = r57;
        r2 = r59;
        r8 = r3;
        r1 = r10;
        r26 = r18;
        r3 = r60;
        if (r9 != 0) goto L_0x17d4;
    L_0x1748:
        r4 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage;	 Catch:{ Exception -> 0x1879 }
        r4.<init>();	 Catch:{ Exception -> 0x1879 }
        r6 = r49;
        r4.message = r6;	 Catch:{ Exception -> 0x1879 }
        if (r2 != 0) goto L_0x1755;
    L_0x1753:
        r6 = 1;
        goto L_0x1756;
    L_0x1755:
        r6 = 0;
    L_0x1756:
        r4.clear_draft = r6;	 Catch:{ Exception -> 0x1879 }
        if (r63 == 0) goto L_0x1779;
    L_0x175a:
        r6 = r7.currentAccount;	 Catch:{ Exception -> 0x1879 }
        r6 = org.telegram.messenger.MessagesController.getNotificationsSettings(r6);	 Catch:{ Exception -> 0x1879 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x1879 }
        r9.<init>();	 Catch:{ Exception -> 0x1879 }
        r9.append(r15);	 Catch:{ Exception -> 0x1879 }
        r9.append(r13);	 Catch:{ Exception -> 0x1879 }
        r9 = r9.toString();	 Catch:{ Exception -> 0x1879 }
        r10 = 0;
        r6 = r6.getBoolean(r9, r10);	 Catch:{ Exception -> 0x1879 }
        if (r6 == 0) goto L_0x1777;
    L_0x1776:
        goto L_0x1779;
    L_0x1777:
        r6 = 0;
        goto L_0x177a;
    L_0x1779:
        r6 = 1;
    L_0x177a:
        r4.silent = r6;	 Catch:{ Exception -> 0x1879 }
        r4.peer = r8;	 Catch:{ Exception -> 0x1879 }
        r8 = r5.random_id;	 Catch:{ Exception -> 0x1879 }
        r4.random_id = r8;	 Catch:{ Exception -> 0x1879 }
        r6 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x1879 }
        if (r6 == 0) goto L_0x1790;
    L_0x1786:
        r6 = r4.flags;	 Catch:{ Exception -> 0x1879 }
        r8 = 1;
        r6 = r6 | r8;
        r4.flags = r6;	 Catch:{ Exception -> 0x1879 }
        r6 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x1879 }
        r4.reply_to_msg_id = r6;	 Catch:{ Exception -> 0x1879 }
    L_0x1790:
        if (r58 != 0) goto L_0x1795;
    L_0x1792:
        r6 = 1;
        r4.no_webpage = r6;	 Catch:{ Exception -> 0x1879 }
    L_0x1795:
        if (r3 == 0) goto L_0x17a6;
    L_0x1797:
        r6 = r60.isEmpty();	 Catch:{ Exception -> 0x1879 }
        if (r6 != 0) goto L_0x17a6;
    L_0x179d:
        r4.entities = r3;	 Catch:{ Exception -> 0x1879 }
        r3 = r4.flags;	 Catch:{ Exception -> 0x1879 }
        r6 = 8;
        r3 = r3 | r6;
        r4.flags = r3;	 Catch:{ Exception -> 0x1879 }
    L_0x17a6:
        if (r1 == 0) goto L_0x17b0;
    L_0x17a8:
        r4.schedule_date = r1;	 Catch:{ Exception -> 0x1879 }
        r3 = r4.flags;	 Catch:{ Exception -> 0x1879 }
        r3 = r3 | 1024;
        r4.flags = r3;	 Catch:{ Exception -> 0x1879 }
    L_0x17b0:
        r3 = 0;
        r6 = 0;
        if (r1 == 0) goto L_0x17b6;
    L_0x17b4:
        r8 = 1;
        goto L_0x17b7;
    L_0x17b6:
        r8 = 0;
    L_0x17b7:
        r44 = r43;
        r45 = r4;
        r46 = r11;
        r47 = r3;
        r48 = r6;
        r49 = r26;
        r50 = r8;
        r44.performSendMessageRequest(r45, r46, r47, r48, r49, r50);	 Catch:{ Exception -> 0x1879 }
        if (r2 != 0) goto L_0x1876;
    L_0x17ca:
        r2 = r43.getMediaDataController();	 Catch:{ Exception -> 0x1879 }
        r3 = 0;
        r2.cleanDraft(r13, r3);	 Catch:{ Exception -> 0x1879 }
        goto L_0x1876;
    L_0x17d4:
        r6 = r49;
        r4 = r9.layer;	 Catch:{ Exception -> 0x1879 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x1879 }
        r8 = 73;
        if (r4 < r8) goto L_0x17e6;
    L_0x17e0:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x1879 }
        r4.<init>();	 Catch:{ Exception -> 0x1879 }
        goto L_0x17eb;
    L_0x17e6:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x1879 }
        r4.<init>();	 Catch:{ Exception -> 0x1879 }
    L_0x17eb:
        r8 = r5.ttl;	 Catch:{ Exception -> 0x1879 }
        r4.ttl = r8;	 Catch:{ Exception -> 0x1879 }
        if (r3 == 0) goto L_0x17ff;
    L_0x17f1:
        r8 = r60.isEmpty();	 Catch:{ Exception -> 0x1879 }
        if (r8 != 0) goto L_0x17ff;
    L_0x17f7:
        r4.entities = r3;	 Catch:{ Exception -> 0x1879 }
        r3 = r4.flags;	 Catch:{ Exception -> 0x1879 }
        r3 = r3 | 128;
        r4.flags = r3;	 Catch:{ Exception -> 0x1879 }
    L_0x17ff:
        r7 = r5.reply_to_random_id;	 Catch:{ Exception -> 0x1879 }
        r3 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1));
        if (r3 == 0) goto L_0x1810;
    L_0x1805:
        r7 = r5.reply_to_random_id;	 Catch:{ Exception -> 0x1879 }
        r4.reply_to_random_id = r7;	 Catch:{ Exception -> 0x1879 }
        r3 = r4.flags;	 Catch:{ Exception -> 0x1879 }
        r7 = 8;
        r3 = r3 | r7;
        r4.flags = r3;	 Catch:{ Exception -> 0x1879 }
    L_0x1810:
        if (r12 == 0) goto L_0x182a;
    L_0x1812:
        r3 = "bot_name";
        r3 = r12.get(r3);	 Catch:{ Exception -> 0x1879 }
        if (r3 == 0) goto L_0x182a;
    L_0x181a:
        r3 = "bot_name";
        r3 = r12.get(r3);	 Catch:{ Exception -> 0x1879 }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x1879 }
        r4.via_bot_name = r3;	 Catch:{ Exception -> 0x1879 }
        r3 = r4.flags;	 Catch:{ Exception -> 0x1879 }
        r3 = r3 | 2048;
        r4.flags = r3;	 Catch:{ Exception -> 0x1879 }
    L_0x182a:
        r7 = r5.random_id;	 Catch:{ Exception -> 0x1879 }
        r4.random_id = r7;	 Catch:{ Exception -> 0x1879 }
        r4.message = r6;	 Catch:{ Exception -> 0x1879 }
        r3 = r32;
        if (r3 == 0) goto L_0x184c;
    L_0x1834:
        r6 = r3.url;	 Catch:{ Exception -> 0x1879 }
        if (r6 == 0) goto L_0x184c;
    L_0x1838:
        r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage;	 Catch:{ Exception -> 0x1879 }
        r6.<init>();	 Catch:{ Exception -> 0x1879 }
        r4.media = r6;	 Catch:{ Exception -> 0x1879 }
        r6 = r4.media;	 Catch:{ Exception -> 0x1879 }
        r3 = r3.url;	 Catch:{ Exception -> 0x1879 }
        r6.url = r3;	 Catch:{ Exception -> 0x1879 }
        r3 = r4.flags;	 Catch:{ Exception -> 0x1879 }
        r3 = r3 | 512;
        r4.flags = r3;	 Catch:{ Exception -> 0x1879 }
        goto L_0x1853;
    L_0x184c:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty;	 Catch:{ Exception -> 0x1879 }
        r3.<init>();	 Catch:{ Exception -> 0x1879 }
        r4.media = r3;	 Catch:{ Exception -> 0x1879 }
    L_0x1853:
        r3 = r43.getSecretChatHelper();	 Catch:{ Exception -> 0x1879 }
        r6 = r11.messageOwner;	 Catch:{ Exception -> 0x1879 }
        r7 = 0;
        r8 = 0;
        r46 = r3;
        r47 = r4;
        r48 = r6;
        r49 = r9;
        r50 = r7;
        r51 = r8;
        r52 = r11;
        r46.performSendEncryptedRequest(r47, r48, r49, r50, r51, r52);	 Catch:{ Exception -> 0x1879 }
        if (r2 != 0) goto L_0x1876;
    L_0x186e:
        r2 = r43.getMediaDataController();	 Catch:{ Exception -> 0x1879 }
        r3 = 0;
        r2.cleanDraft(r13, r3);	 Catch:{ Exception -> 0x1879 }
    L_0x1876:
        r2 = r43;
        goto L_0x18c9;
    L_0x1879:
        r0 = move-exception;
        goto L_0x1882;
    L_0x187b:
        r0 = move-exception;
        r11 = r57;
        goto L_0x1881;
    L_0x187f:
        r0 = move-exception;
        r11 = r6;
    L_0x1881:
        r1 = r10;
    L_0x1882:
        r2 = r0;
        goto L_0x1897;
    L_0x1884:
        r0 = move-exception;
        r1 = r10;
        goto L_0x0262;
    L_0x1888:
        r0 = move-exception;
        r1 = r64;
        r2 = r0;
        r5 = r18;
        goto L_0x0269;
    L_0x1890:
        r0 = move-exception;
        r1 = r64;
        r18 = r5;
        goto L_0x0262;
    L_0x1897:
        org.telegram.messenger.FileLog.e(r2);
        r2 = r43.getMessagesStorage();
        if (r1 == 0) goto L_0x18a2;
    L_0x18a0:
        r1 = 1;
        goto L_0x18a3;
    L_0x18a2:
        r1 = 0;
    L_0x18a3:
        r2.markMessageAsSendError(r5, r1);
        if (r11 == 0) goto L_0x18ad;
    L_0x18a8:
        r1 = r11.messageOwner;
        r2 = 2;
        r1.send_state = r2;
    L_0x18ad:
        r1 = r43.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.messageSendError;
        r3 = 1;
        r3 = new java.lang.Object[r3];
        r4 = r5.id;
        r4 = java.lang.Integer.valueOf(r4);
        r6 = 0;
        r3[r6] = r4;
        r1.postNotificationName(r2, r3);
        r1 = r5.id;
        r2 = r43;
        r2.processSentMessage(r1);
    L_0x18c9:
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
        Long valueOf = Long.valueOf(-1);
        Boolean valueOf2 = Boolean.valueOf(false);
        int i;
        if (inputMedia != null) {
            TL_messages_sendMultiMedia tL_messages_sendMultiMedia = (TL_messages_sendMultiMedia) delayedMessage.sendRequest;
            for (i = 0; i < tL_messages_sendMultiMedia.multi_media.size(); i++) {
                if (((TL_inputSingleMedia) tL_messages_sendMultiMedia.multi_media.get(i)).media == inputMedia) {
                    putToSendingMessages((Message) delayedMessage.messages.get(i), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, valueOf, valueOf, valueOf2);
                    break;
                }
            }
            TL_messages_uploadMedia tL_messages_uploadMedia = new TL_messages_uploadMedia();
            tL_messages_uploadMedia.media = inputMedia;
            tL_messages_uploadMedia.peer = ((TL_messages_sendMultiMedia) delayedMessage.sendRequest).peer;
            getConnectionsManager().sendRequest(tL_messages_uploadMedia, new -$$Lambda$SendMessagesHelper$iGLp2xlVpjSCLASSNAMEYuBMiqWlVuN-o(this, inputMedia, delayedMessage));
        } else if (inputEncryptedFile != null) {
            TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
            for (i = 0; i < tL_messages_sendEncryptedMultiMedia.files.size(); i++) {
                if (tL_messages_sendEncryptedMultiMedia.files.get(i) == inputEncryptedFile) {
                    putToSendingMessages((Message) delayedMessage.messages.get(i), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, valueOf, valueOf, valueOf2);
                    break;
                }
            }
            sendReadyToSendGroup(delayedMessage, false, true);
        }
    }

    public /* synthetic */ void lambda$uploadMultiMedia$27$SendMessagesHelper(InputMedia inputMedia, DelayedMessage delayedMessage, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$FZjjnxbzRKAZfXzHRe_dY-fA1KE(this, tLObject, inputMedia, delayedMessage));
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    public /* synthetic */ void lambda$null$26$SendMessagesHelper(org.telegram.tgnet.TLObject r6, org.telegram.tgnet.TLRPC.InputMedia r7, org.telegram.messenger.SendMessagesHelper.DelayedMessage r8) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$26$SendMessagesHelper(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$InputMedia, org.telegram.messenger.SendMessagesHelper$DelayedMessage):void");
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

    public /* synthetic */ void lambda$null$28$SendMessagesHelper(String str) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, str, Integer.valueOf(this.currentAccount));
    }

    public /* synthetic */ void lambda$stopVideoService$29$SendMessagesHelper(String str) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$z9h7wTeZuZv4NalBcrhryX7vPgM(this, str));
    }

    /* Access modifiers changed, original: protected */
    public void stopVideoService(String str) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$5zfQKDWl_3j7EMSs2fi0_LlrqP8(this, str));
    }

    /* Access modifiers changed, original: protected */
    public void putToSendingMessages(Message message, boolean z) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$ZjpbgJikNfL8S2-0PUFYfDQWKYc(this, message, z));
        } else {
            putToSendingMessages(message, z, true);
        }
    }

    public /* synthetic */ void lambda$putToSendingMessages$30$SendMessagesHelper(Message message, boolean z) {
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
        getConnectionsManager().sendRequest((TLObject) tL_messages_sendMultiMedia, new -$$Lambda$SendMessagesHelper$ZYsiGPlj9hRHiRl2LlxJG53Fm0s(this, arrayList3, tL_messages_sendMultiMedia, arrayList4, arrayList2, delayedMessage, z2), null, 68);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$38$SendMessagesHelper(ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, boolean z, TLObject tLObject, TL_error tL_error) {
        ArrayList arrayList4 = arrayList;
        TL_error tL_error2 = tL_error;
        if (tL_error2 != null && FileRefController.isFileRefError(tL_error2.text)) {
            if (arrayList4 != null) {
                ArrayList arrayList5 = new ArrayList(arrayList);
                getFileRefController().requestReference(arrayList5, tL_messages_sendMultiMedia, arrayList2, arrayList3, arrayList5, delayedMessage, Boolean.valueOf(z));
                return;
            } else if (delayedMessage != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$u2MYlCdSqK8HK7SN4n3B6vSgLvc(this, tL_messages_sendMultiMedia, delayedMessage, arrayList2, z));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$1UlSeYagrlax_3OOs5dqbDoqf8E(this, tL_error, tLObject, arrayList2, arrayList3, z, tL_messages_sendMultiMedia));
    }

    public /* synthetic */ void lambda$null$31$SendMessagesHelper(TL_messages_sendMultiMedia tL_messages_sendMultiMedia, DelayedMessage delayedMessage, ArrayList arrayList, boolean z) {
        int size = tL_messages_sendMultiMedia.multi_media.size();
        for (int i = 0; i < size; i++) {
            if (delayedMessage.parentObjects.get(i) != null) {
                removeFromSendingMessages(((MessageObject) arrayList.get(i)).getId(), z);
                TL_inputSingleMedia tL_inputSingleMedia = (TL_inputSingleMedia) tL_messages_sendMultiMedia.multi_media.get(i);
                InputMedia inputMedia = tL_inputSingleMedia.media;
                if (inputMedia instanceof TL_inputMediaPhoto) {
                    tL_inputSingleMedia.media = (InputMedia) delayedMessage.inputMedias.get(i);
                } else if (inputMedia instanceof TL_inputMediaDocument) {
                    tL_inputSingleMedia.media = (InputMedia) delayedMessage.inputMedias.get(i);
                }
                delayedMessage.videoEditedInfo = (VideoEditedInfo) delayedMessage.videoEditedInfos.get(i);
                delayedMessage.httpLocation = (String) delayedMessage.httpLocations.get(i);
                delayedMessage.photoSize = (PhotoSize) delayedMessage.locations.get(i);
                delayedMessage.performMediaUpload = true;
                performSendDelayedMessage(delayedMessage, i);
            }
        }
    }

    public /* synthetic */ void lambda$null$37$SendMessagesHelper(TL_error tL_error, TLObject tLObject, ArrayList arrayList, ArrayList arrayList2, boolean z, TL_messages_sendMultiMedia tL_messages_sendMultiMedia) {
        Object obj;
        TL_error tL_error2 = tL_error;
        ArrayList arrayList3 = arrayList;
        boolean z2 = z;
        if (tL_error2 == null) {
            Message message;
            Updates updates;
            SparseArray sparseArray = new SparseArray();
            LongSparseArray longSparseArray = new LongSparseArray();
            Updates updates2 = (Updates) tLObject;
            ArrayList arrayList4 = updates2.updates;
            int i = 0;
            while (i < arrayList4.size()) {
                Update update = (Update) arrayList4.get(i);
                if (update instanceof TL_updateMessageID) {
                    TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) update;
                    longSparseArray.put(tL_updateMessageID.random_id, Integer.valueOf(tL_updateMessageID.id));
                    arrayList4.remove(i);
                } else if (update instanceof TL_updateNewMessage) {
                    TL_updateNewMessage tL_updateNewMessage = (TL_updateNewMessage) update;
                    message = tL_updateNewMessage.message;
                    sparseArray.put(message.id, message);
                    Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$hA_OiBdi7WsYpCb6VsoxuFLYyp8(this, tL_updateNewMessage));
                    arrayList4.remove(i);
                } else if (update instanceof TL_updateNewChannelMessage) {
                    TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                    message = tL_updateNewChannelMessage.message;
                    sparseArray.put(message.id, message);
                    Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$ZcYcLSm3-21nBbdlCLASSNAMEd6DkuXvY(this, tL_updateNewChannelMessage));
                    arrayList4.remove(i);
                } else if (update instanceof TL_updateNewScheduledMessage) {
                    Message message2 = ((TL_updateNewScheduledMessage) update).message;
                    sparseArray.put(message2.id, message2);
                    arrayList4.remove(i);
                } else {
                    i++;
                }
                i--;
                i++;
            }
            int i2 = 0;
            while (i2 < arrayList.size()) {
                MessageObject messageObject = (MessageObject) arrayList3.get(i2);
                String str = (String) arrayList2.get(i2);
                message = messageObject.messageOwner;
                int i3 = message.id;
                ArrayList arrayList5 = new ArrayList();
                String str2 = message.attachPath;
                Integer num = (Integer) longSparseArray.get(message.random_id);
                if (num != null) {
                    Message message3 = (Message) sparseArray.get(num.intValue());
                    if (message3 != null) {
                        arrayList5.add(message3);
                        ArrayList arrayList6 = arrayList5;
                        int i4 = i3;
                        Message message4 = message;
                        MessageObject messageObject2 = messageObject;
                        updateMediaPaths(messageObject, message3, message3.id, str, false);
                        int mediaExistanceFlags = messageObject2.getMediaExistanceFlags();
                        message4.id = message3.id;
                        if ((message4.flags & Integer.MIN_VALUE) != 0) {
                            message3.flags |= Integer.MIN_VALUE;
                        }
                        long j = message3.grouped_id;
                        if (!z2) {
                            num = (Integer) getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message3.dialog_id));
                            if (num == null) {
                                num = Integer.valueOf(getMessagesStorage().getDialogReadMax(message3.out, message3.dialog_id));
                                getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message3.dialog_id), num);
                            }
                            message3.unread = num.intValue() < message3.id;
                        }
                        getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                        message4.send_state = 0;
                        NotificationCenter notificationCenter = getNotificationCenter();
                        int i5 = NotificationCenter.messageReceivedByServer;
                        r12 = new Object[7];
                        Updates updates3 = updates2;
                        int i6 = i2;
                        r12[3] = Long.valueOf(message4.dialog_id);
                        r12[4] = Long.valueOf(j);
                        r12[5] = Integer.valueOf(mediaExistanceFlags);
                        r12[6] = Boolean.valueOf(z);
                        notificationCenter.postNotificationName(i5, r12);
                        DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                        updates = updates3;
                        SparseArray sparseArray2 = sparseArray;
                        -$$Lambda$SendMessagesHelper$IfuL9XcMh89YHY1N-1peNifOal8 -__lambda_sendmessageshelper_iful9xcmh89yhy1n-1penifoal8 = r0;
                        LongSparseArray longSparseArray2 = longSparseArray;
                        -$$Lambda$SendMessagesHelper$IfuL9XcMh89YHY1N-1peNifOal8 -__lambda_sendmessageshelper_iful9xcmh89yhy1n-1penifoal82 = new -$$Lambda$SendMessagesHelper$IfuL9XcMh89YHY1N-1peNifOal8(this, message4, i4, z, arrayList6, j, mediaExistanceFlags);
                        storageQueue.postRunnable(-__lambda_sendmessageshelper_iful9xcmh89yhy1n-1penifoal8);
                        i2 = i6 + 1;
                        sparseArray = sparseArray2;
                        updates2 = updates;
                        longSparseArray = longSparseArray2;
                    }
                }
                updates = updates2;
                obj = 1;
            }
            updates = updates2;
            obj = null;
            Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$WQ_rgU9i_CLASSNAMEKG3_Zo7CgxwtnA(this, updates));
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error2, null, tL_messages_sendMultiMedia, new Object[0]);
            obj = 1;
        }
        if (obj != null) {
            for (int i7 = 0; i7 < arrayList.size(); i7++) {
                Message message5 = ((MessageObject) arrayList3.get(i7)).messageOwner;
                getMessagesStorage().markMessageAsSendError(message5, z2);
                message5.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message5.id));
                processSentMessage(message5.id);
                removeFromSendingMessages(message5.id, z2);
            }
        }
    }

    public /* synthetic */ void lambda$null$32$SendMessagesHelper(TL_updateNewMessage tL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$33$SendMessagesHelper(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$35$SendMessagesHelper(Message message, int i, boolean z, ArrayList arrayList, long j, int i2) {
        Message message2 = message;
        getMessagesStorage().updateMessageStateAndId(message2.random_id, Integer.valueOf(i), message2.id, 0, false, message2.to_id.channel_id, z);
        getMessagesStorage().putMessages(arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$VpBjyjxKtctL7U40Q2hYkft4jnA(this, message, i, j, i2, z));
    }

    public /* synthetic */ void lambda$null$34$SendMessagesHelper(Message message, int i, long j, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), Long.valueOf(j), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$36$SendMessagesHelper(Updates updates) {
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
        -$$Lambda$SendMessagesHelper$anaw0NqrAe1M6q_iXvar_cgrWPg -__lambda_sendmessageshelper_anaw0nqrae1m6q_ixvar_cgrwpg = new -$$Lambda$SendMessagesHelper$anaw0NqrAe1M6q_iXvar_cgrWPg(this, tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, z3, message);
        message.reqId = connectionsManager.sendRequest(tLObject2, requestDelegate, new -$$Lambda$SendMessagesHelper$imitI6Sb8tp9vqQgNVvalbaeW_Q(this, message), (tLObject2 instanceof TL_messages_sendMessage ? 128 : 0) | 68);
        if (delayedMessage != null) {
            delayedMessage.sendDelayedRequests();
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequest$52$SendMessagesHelper(TLObject tLObject, Object obj, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, boolean z2, Message message, TLObject tLObject2, TL_error tL_error) {
        TLObject tLObject3 = tLObject;
        Object obj2 = obj;
        TL_error tL_error2 = tL_error;
        if (tL_error2 != null && (((tLObject3 instanceof TL_messages_sendMedia) || (tLObject3 instanceof TL_messages_editMessage)) && FileRefController.isFileRefError(tL_error2.text))) {
            if (obj2 != null) {
                getFileRefController().requestReference(obj, tLObject3, messageObject, str, delayedMessage, Boolean.valueOf(z), delayedMessage2, Boolean.valueOf(z2));
                return;
            } else if (delayedMessage2 != null) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$Z-r73k_daSxgwkVfswtomRn77RE(this, message, z2, tLObject, delayedMessage2));
                return;
            }
        }
        if (tLObject3 instanceof TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$3EWAFK_9EZlvSGYW2QO7epSuWB4(this, tL_error, message, tLObject2, messageObject, str, z2, tLObject));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$QV6T6rEjswk_mSdV2Rqp7QbM3vs(this, z2, tL_error, message, tLObject2, messageObject, str, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$39$SendMessagesHelper(Message message, boolean z, TLObject tLObject, DelayedMessage delayedMessage) {
        removeFromSendingMessages(message.id, z);
        InputMedia inputMedia;
        if (tLObject instanceof TL_messages_sendMedia) {
            TL_messages_sendMedia tL_messages_sendMedia = (TL_messages_sendMedia) tLObject;
            inputMedia = tL_messages_sendMedia.media;
            if (inputMedia instanceof TL_inputMediaPhoto) {
                tL_messages_sendMedia.media = delayedMessage.inputUploadMedia;
            } else if (inputMedia instanceof TL_inputMediaDocument) {
                tL_messages_sendMedia.media = delayedMessage.inputUploadMedia;
            }
        } else if (tLObject instanceof TL_messages_editMessage) {
            TL_messages_editMessage tL_messages_editMessage = (TL_messages_editMessage) tLObject;
            inputMedia = tL_messages_editMessage.media;
            if (inputMedia instanceof TL_inputMediaPhoto) {
                tL_messages_editMessage.media = delayedMessage.inputUploadMedia;
            } else if (inputMedia instanceof TL_inputMediaDocument) {
                tL_messages_editMessage.media = delayedMessage.inputUploadMedia;
            }
        }
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
    }

    public /* synthetic */ void lambda$null$42$SendMessagesHelper(TL_error tL_error, Message message, TLObject tLObject, MessageObject messageObject, String str, boolean z, TLObject tLObject2) {
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
            Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$trgLmTBAhyoz0NB4EcjJc1xRn98(this, updates, message, z));
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

    public /* synthetic */ void lambda$null$41$SendMessagesHelper(Updates updates, Message message, boolean z) {
        getMessagesController().processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$44UwBrbc_0c8t7HE5nZu3rtwHxg(this, message, z));
    }

    public /* synthetic */ void lambda$null$40$SendMessagesHelper(Message message, boolean z) {
        processSentMessage(message.id);
        removeFromSendingMessages(message.id, z);
    }

    public /* synthetic */ void lambda$null$51$SendMessagesHelper(boolean z, TL_error tL_error, Message message, TLObject tLObject, MessageObject messageObject, String str, TLObject tLObject2) {
        Object obj;
        boolean z2 = z;
        TL_error tL_error2 = tL_error;
        Message message2 = message;
        TLObject tLObject3 = tLObject;
        if (tL_error2 == null) {
            int i;
            int i2 = message2.id;
            ArrayList arrayList = new ArrayList();
            String str2 = message2.attachPath;
            Object obj2 = message2.date == NUM ? 1 : null;
            boolean z3;
            if (tLObject3 instanceof TL_updateShortSentMessage) {
                TL_updateShortSentMessage tL_updateShortSentMessage = (TL_updateShortSentMessage) tLObject3;
                updateMediaPaths(messageObject, null, tL_updateShortSentMessage.id, null, false);
                int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                int i3 = tL_updateShortSentMessage.id;
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
                Integer num = (Integer) getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message2.dialog_id));
                if (num == null) {
                    num = Integer.valueOf(getMessagesStorage().getDialogReadMax(message2.out, message2.dialog_id));
                    getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message2.dialog_id), num);
                }
                message2.unread = num.intValue() < message2.id;
                Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$2cls1P7tqQZ1I3-97ssFZ8jhbps(this, tL_updateShortSentMessage));
                arrayList.add(message2);
                i = mediaExistanceFlags;
                obj = null;
                z3 = false;
            } else if (tLObject3 instanceof Updates) {
                Message message3;
                boolean z4;
                int mediaExistanceFlags2;
                Updates updates = (Updates) tLObject3;
                ArrayList arrayList2 = updates.updates;
                int i4 = 0;
                while (i4 < arrayList2.size()) {
                    Message message4;
                    Update update = (Update) arrayList2.get(i4);
                    if (update instanceof TL_updateNewMessage) {
                        TL_updateNewMessage tL_updateNewMessage = (TL_updateNewMessage) update;
                        message4 = tL_updateNewMessage.message;
                        arrayList.add(message4);
                        Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$KnZaYqmrVc-tQGTwKl5NtVyQ2iU(this, tL_updateNewMessage));
                        arrayList2.remove(i4);
                    } else if (update instanceof TL_updateNewChannelMessage) {
                        TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                        message4 = tL_updateNewChannelMessage.message;
                        arrayList.add(message4);
                        if ((message2.flags & Integer.MIN_VALUE) != 0) {
                            Message message5 = tL_updateNewChannelMessage.message;
                            message5.flags = Integer.MIN_VALUE | message5.flags;
                        }
                        Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$aMzUgpeWOQtaba-1QJrysWUjyos(this, tL_updateNewChannelMessage));
                        arrayList2.remove(i4);
                    } else if (update instanceof TL_updateNewScheduledMessage) {
                        TL_updateNewScheduledMessage tL_updateNewScheduledMessage = (TL_updateNewScheduledMessage) update;
                        message4 = tL_updateNewScheduledMessage.message;
                        arrayList.add(message4);
                        if ((message2.flags & Integer.MIN_VALUE) != 0) {
                            Message message6 = tL_updateNewScheduledMessage.message;
                            message6.flags = Integer.MIN_VALUE | message6.flags;
                        }
                        arrayList2.remove(i4);
                    } else {
                        i4++;
                    }
                    message3 = message4;
                }
                message3 = null;
                if (message3 != null) {
                    z4 = (obj2 == null || message3.date == NUM) ? z2 : false;
                    ImageLoader.saveMessageThumbs(message3);
                    if (!z4) {
                        Integer num2 = (Integer) getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message3.dialog_id));
                        if (num2 == null) {
                            num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(message3.out, message3.dialog_id));
                            getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message3.dialog_id), num2);
                        }
                        message3.unread = num2.intValue() < message3.id;
                    }
                    updateMediaPaths(messageObject, message3, message3.id, str, false);
                    mediaExistanceFlags2 = messageObject.getMediaExistanceFlags();
                    message2.id = message3.id;
                    obj2 = null;
                } else {
                    z4 = z2;
                    obj2 = 1;
                    mediaExistanceFlags2 = 0;
                }
                Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$4lFxBiThpwH7WPmjQyat8EqmmrM(this, updates));
                i = mediaExistanceFlags2;
                z3 = z4;
                obj = obj2;
            } else {
                z3 = z2;
                i = 0;
                obj = null;
            }
            if (MessageObject.isLiveLocationMessage(message)) {
                getLocationController().addSharingLocation(message2.dialog_id, message2.id, message2.media.period, message);
            }
            if (obj == null) {
                getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                message2.send_state = 0;
                if (!z2 || z3) {
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i2), Integer.valueOf(message2.id), message2, Long.valueOf(message2.dialog_id), Long.valueOf(0), Integer.valueOf(i), Boolean.valueOf(z));
                    getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$4zxklzrMNTXAVApuKjkmRn2rSdk(this, message, i2, z, arrayList, i, str2));
                } else {
                    ArrayList arrayList3 = new ArrayList();
                    arrayList3.add(Integer.valueOf(i2));
                    getMessagesController().deleteMessages(arrayList3, null, null, message2.dialog_id, message2.to_id.channel_id, false, true);
                    getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$vXuyGEtHuX3-zvqOAj3t_Wne6go(this, arrayList, messageObject, message, i2, z, str2));
                }
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

    public /* synthetic */ void lambda$null$43$SendMessagesHelper(TL_updateShortSentMessage tL_updateShortSentMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateShortSentMessage.pts, tL_updateShortSentMessage.date, tL_updateShortSentMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$44$SendMessagesHelper(TL_updateNewMessage tL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$45$SendMessagesHelper(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$46$SendMessagesHelper(Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    public /* synthetic */ void lambda$null$48$SendMessagesHelper(ArrayList arrayList, MessageObject messageObject, Message message, int i, boolean z, String str) {
        getMessagesStorage().putMessages(arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$kkmr9vJmrTrp59EZ0DkcNp1Bf6U(this, messageObject, message, i, z));
        if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
            stopVideoService(str);
            return;
        }
    }

    public /* synthetic */ void lambda$null$47$SendMessagesHelper(MessageObject messageObject, Message message, int i, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true));
        getMessagesController().updateInterfaceWithMessages(message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$null$50$SendMessagesHelper(Message message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        Message message2 = message;
        getMessagesStorage().updateMessageStateAndId(message2.random_id, Integer.valueOf(i), message2.id, 0, false, message2.to_id.channel_id, z);
        getMessagesStorage().putMessages(arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$f0doYsVdEKrznCueiiaFUoeyz0g(this, message, i, i2, z));
        if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
            stopVideoService(str);
        }
    }

    public /* synthetic */ void lambda$null$49$SendMessagesHelper(Message message, int i, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), Long.valueOf(0), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$54$SendMessagesHelper(Message message) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$mYrYnNY4WWyODqWaBT7AWhsLL44(this, message, message.id));
    }

    public /* synthetic */ void lambda$null$53$SendMessagesHelper(Message message, int i) {
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
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$XwJ6MKmNJOqB3jOINGZsX_D7jQA(this, arrayList3, arrayList4, arrayList5, arrayList, arrayList2));
    }

    public /* synthetic */ void lambda$processUnsentMessages$55$SendMessagesHelper(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
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

    /* JADX WARNING: Removed duplicated region for block: B:254:0x0442  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043c  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0442  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0191  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0179  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x020a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0442  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043c  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x020a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0442  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0153 A:{SYNTHETIC, Splitter:B:102:0x0153} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x020a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0442  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043c  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0153 A:{SYNTHETIC, Splitter:B:102:0x0153} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x020a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0442  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0153 A:{SYNTHETIC, Splitter:B:102:0x0153} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x020a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0442  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043c  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0153 A:{SYNTHETIC, Splitter:B:102:0x0153} */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x020a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0442  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0165 A:{SYNTHETIC, Splitter:B:110:0x0165} */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0153 A:{SYNTHETIC, Splitter:B:102:0x0153} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x01c7  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x01ca  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x020a A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0442  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x043c  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0453  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x045c  */
    /* JADX WARNING: Missing block: B:26:0x004d, code skipped:
            if (r3 == null) goto L_0x0051;
     */
    /* JADX WARNING: Missing block: B:195:0x0333, code skipped:
            r4 = -1;
     */
    /* JADX WARNING: Missing block: B:196:0x0334, code skipped:
            if (r4 == 0) goto L_0x036c;
     */
    /* JADX WARNING: Missing block: B:198:0x0337, code skipped:
            if (r4 == 1) goto L_0x0367;
     */
    /* JADX WARNING: Missing block: B:200:0x033a, code skipped:
            if (r4 == 2) goto L_0x0362;
     */
    /* JADX WARNING: Missing block: B:202:0x033d, code skipped:
            if (r4 == 3) goto L_0x035d;
     */
    /* JADX WARNING: Missing block: B:204:0x0340, code skipped:
            if (r4 == 4) goto L_0x0358;
     */
    /* JADX WARNING: Missing block: B:206:0x0343, code skipped:
            if (r4 == 5) goto L_0x0353;
     */
    /* JADX WARNING: Missing block: B:207:0x0345, code skipped:
            r0 = r19.getMimeTypeFromExtension(r10);
     */
    /* JADX WARNING: Missing block: B:208:0x034b, code skipped:
            if (r0 == null) goto L_0x0350;
     */
    /* JADX WARNING: Missing block: B:209:0x034d, code skipped:
            r7.mime_type = r0;
     */
    /* JADX WARNING: Missing block: B:210:0x0350, code skipped:
            r7.mime_type = r1;
     */
    /* JADX WARNING: Missing block: B:211:0x0353, code skipped:
            r7.mime_type = "audio/flac";
     */
    /* JADX WARNING: Missing block: B:212:0x0358, code skipped:
            r7.mime_type = "audio/ogg";
     */
    /* JADX WARNING: Missing block: B:213:0x035d, code skipped:
            r7.mime_type = "audio/m4a";
     */
    /* JADX WARNING: Missing block: B:214:0x0362, code skipped:
            r7.mime_type = "audio/mpeg";
     */
    /* JADX WARNING: Missing block: B:215:0x0367, code skipped:
            r7.mime_type = "audio/opus";
     */
    /* JADX WARNING: Missing block: B:216:0x036c, code skipped:
            r7.mime_type = "image/webp";
     */
    private static boolean prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r28, java.lang.String r29, java.lang.String r30, android.net.Uri r31, java.lang.String r32, long r33, org.telegram.messenger.MessageObject r35, java.lang.CharSequence r36, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r37, org.telegram.messenger.MessageObject r38, boolean r39, boolean r40, int r41) {
        /*
        r0 = r29;
        r1 = r30;
        r2 = r31;
        r3 = r32;
        r4 = 0;
        if (r0 == 0) goto L_0x0011;
    L_0x000b:
        r5 = r29.length();
        if (r5 != 0) goto L_0x0014;
    L_0x0011:
        if (r2 != 0) goto L_0x0014;
    L_0x0013:
        return r4;
    L_0x0014:
        if (r2 == 0) goto L_0x001d;
    L_0x0016:
        r5 = org.telegram.messenger.AndroidUtilities.isInternalUri(r31);
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
        if (r3 == 0) goto L_0x047c;
    L_0x005d:
        r7 = r2.length();
        r11 = 0;
        r3 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1));
        if (r3 != 0) goto L_0x0069;
    L_0x0067:
        goto L_0x047c;
    L_0x0069:
        r9 = r33;
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
        r29 = r15;
        r15 = 2;
        if (r0 != 0) goto L_0x016f;
    L_0x00a6:
        r0 = r10.equals(r12);
        if (r0 == 0) goto L_0x00ae;
    L_0x00ac:
        goto L_0x016f;
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
        goto L_0x019c;
    L_0x00cd:
        r7 = new android.media.MediaMetadataRetriever;	 Catch:{ Exception -> 0x0144, all -> 0x0140 }
        r7.<init>();	 Catch:{ Exception -> 0x0144, all -> 0x0140 }
        r0 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x013a }
        r7.setDataSource(r0);	 Catch:{ Exception -> 0x013a }
        r0 = 9;
        r0 = r7.extractMetadata(r0);	 Catch:{ Exception -> 0x013a }
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
        goto L_0x014e;
    L_0x0105:
        r0 = move-exception;
        r20 = r8;
        r8 = 0;
        r9 = 0;
        goto L_0x014e;
    L_0x010b:
        r0 = move-exception;
        goto L_0x014a;
    L_0x010d:
        r18 = r8;
        r19 = r9;
        r8 = 0;
        r9 = 0;
        r20 = 0;
    L_0x0115:
        if (r38 != 0) goto L_0x012c;
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
        goto L_0x014e;
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
        r0 = r8;
        r8 = r20;
        goto L_0x00c9;
    L_0x013a:
        r0 = move-exception;
        r18 = r8;
        r19 = r9;
        goto L_0x014a;
    L_0x0140:
        r0 = move-exception;
        r1 = r0;
        r7 = 0;
        goto L_0x0163;
    L_0x0144:
        r0 = move-exception;
        r18 = r8;
        r19 = r9;
        r7 = 0;
    L_0x014a:
        r8 = 0;
        r9 = 0;
        r20 = 0;
    L_0x014e:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0161 }
        if (r7 == 0) goto L_0x015c;
    L_0x0153:
        r7.release();	 Catch:{ Exception -> 0x0157 }
        goto L_0x015c;
    L_0x0157:
        r0 = move-exception;
        r7 = r0;
        org.telegram.messenger.FileLog.e(r7);
    L_0x015c:
        r0 = r8;
        r8 = r20;
        goto L_0x00c8;
    L_0x0161:
        r0 = move-exception;
        r1 = r0;
    L_0x0163:
        if (r7 == 0) goto L_0x016e;
    L_0x0165:
        r7.release();	 Catch:{ Exception -> 0x0169 }
        goto L_0x016e;
    L_0x0169:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x016e:
        throw r1;
    L_0x016f:
        r18 = r8;
        r19 = r9;
        r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r2);
        if (r0 == 0) goto L_0x0191;
    L_0x0179:
        r7 = r0.getDuration();
        r22 = 0;
        r9 = (r7 > r22 ? 1 : (r7 == r22 ? 0 : -1));
        if (r9 == 0) goto L_0x0193;
    L_0x0183:
        r9 = r0.getArtist();
        r0 = r0.getTitle();
        r24 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 / r24;
        r8 = (int) r7;
        goto L_0x0196;
    L_0x0191:
        r22 = 0;
    L_0x0193:
        r0 = 0;
        r8 = 0;
        r9 = 0;
    L_0x0196:
        r15 = 0;
        r27 = r9;
        r9 = r0;
        r0 = r27;
    L_0x019c:
        if (r8 == 0) goto L_0x01c7;
    L_0x019e:
        r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
        r7.<init>();
        r7.duration = r8;
        r7.title = r9;
        r7.performer = r0;
        r0 = r7.title;
        if (r0 != 0) goto L_0x01af;
    L_0x01ad:
        r7.title = r6;
    L_0x01af:
        r0 = r7.flags;
        r8 = 1;
        r0 = r0 | r8;
        r7.flags = r0;
        r0 = r7.performer;
        if (r0 != 0) goto L_0x01bb;
    L_0x01b9:
        r7.performer = r6;
    L_0x01bb:
        r0 = r7.flags;
        r9 = 2;
        r0 = r0 | r9;
        r7.flags = r0;
        if (r15 == 0) goto L_0x01c5;
    L_0x01c3:
        r7.voice = r8;
    L_0x01c5:
        r0 = r7;
        goto L_0x01c8;
    L_0x01c7:
        r0 = 0;
    L_0x01c8:
        if (r1 == 0) goto L_0x0206;
    L_0x01ca:
        r7 = "attheme";
        r7 = r1.endsWith(r7);
        if (r7 == 0) goto L_0x01d5;
    L_0x01d2:
        r15 = r1;
        r1 = 1;
        goto L_0x0208;
    L_0x01d5:
        if (r0 == 0) goto L_0x01f0;
    L_0x01d7:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r1);
        r1 = "audio";
        r7.append(r1);
        r8 = r2.length();
        r7.append(r8);
        r1 = r7.toString();
        goto L_0x0206;
    L_0x01f0:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r1);
        r7.append(r6);
        r8 = r2.length();
        r7.append(r8);
        r1 = r7.toString();
    L_0x0206:
        r15 = r1;
        r1 = 0;
    L_0x0208:
        if (r1 != 0) goto L_0x0295;
    L_0x020a:
        if (r3 != 0) goto L_0x0295;
    L_0x020c:
        r1 = r28.getMessagesStorage();
        if (r3 != 0) goto L_0x0214;
    L_0x0212:
        r7 = 1;
        goto L_0x0215;
    L_0x0214:
        r7 = 4;
    L_0x0215:
        r1 = r1.getSentFile(r15, r7);
        if (r1 == 0) goto L_0x0227;
    L_0x021b:
        r7 = 0;
        r8 = r1[r7];
        r7 = r8;
        r7 = (org.telegram.tgnet.TLRPC.TL_document) r7;
        r8 = 1;
        r1 = r1[r8];
        r1 = (java.lang.String) r1;
        goto L_0x0229;
    L_0x0227:
        r1 = 0;
        r7 = 0;
    L_0x0229:
        if (r7 != 0) goto L_0x0267;
    L_0x022b:
        r8 = r13.equals(r15);
        if (r8 != 0) goto L_0x0267;
    L_0x0231:
        if (r3 != 0) goto L_0x0267;
    L_0x0233:
        r8 = r28.getMessagesStorage();
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r13);
        r31 = r10;
        r32 = r11;
        r10 = r2.length();
        r9.append(r10);
        r9 = r9.toString();
        if (r3 != 0) goto L_0x0252;
    L_0x0250:
        r10 = 1;
        goto L_0x0253;
    L_0x0252:
        r10 = 4;
    L_0x0253:
        r8 = r8.getSentFile(r9, r10);
        if (r8 == 0) goto L_0x026b;
    L_0x0259:
        r9 = 0;
        r1 = r8[r9];
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;
        r7 = 1;
        r8 = r8[r7];
        r7 = r8;
        r7 = (java.lang.String) r7;
        r20 = r7;
        goto L_0x026e;
    L_0x0267:
        r31 = r10;
        r32 = r11;
    L_0x026b:
        r20 = r1;
        r1 = r7;
    L_0x026e:
        r10 = 0;
        r24 = 0;
        r17 = -1;
        r7 = r3;
        r11 = r18;
        r8 = r1;
        r26 = r19;
        r9 = r13;
        r30 = r31;
        r31 = r1;
        r19 = r5;
        r1 = r11;
        r18 = r15;
        r15 = r32;
        r32 = r6;
        r5 = r22;
        r22 = r12;
        r11 = r24;
        ensureMediaThumbExists(r7, r8, r9, r10, r11);
        r7 = r31;
        r8 = r20;
        goto L_0x02aa;
    L_0x0295:
        r32 = r6;
        r30 = r10;
        r1 = r18;
        r26 = r19;
        r17 = -1;
        r19 = r5;
        r18 = r15;
        r5 = r22;
        r15 = r11;
        r22 = r12;
        r7 = 0;
        r8 = 0;
    L_0x02aa:
        if (r7 != 0) goto L_0x0437;
    L_0x02ac:
        r7 = new org.telegram.tgnet.TLRPC$TL_document;
        r7.<init>();
        r7.id = r5;
        r9 = r28.getConnectionsManager();
        r9 = r9.getCurrentTime();
        r7.date = r9;
        r9 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r9.<init>();
        r9.file_name = r1;
        r1 = 0;
        r10 = new byte[r1];
        r7.file_reference = r10;
        r10 = r7.attributes;
        r10.add(r9);
        r10 = r2.length();
        r11 = (int) r10;
        r7.size = r11;
        r7.dc_id = r1;
        if (r0 == 0) goto L_0x02de;
    L_0x02d9:
        r1 = r7.attributes;
        r1.add(r0);
    L_0x02de:
        r0 = r16.length();
        r1 = "application/octet-stream";
        if (r0 == 0) goto L_0x0371;
    L_0x02e6:
        r0 = r30.hashCode();
        switch(r0) {
            case 106458: goto L_0x0327;
            case 108272: goto L_0x031b;
            case 109967: goto L_0x0311;
            case 3145576: goto L_0x0307;
            case 3418175: goto L_0x02fd;
            case 3645340: goto L_0x02f0;
            default: goto L_0x02ed;
        };
    L_0x02ed:
        r10 = r30;
        goto L_0x0333;
    L_0x02f0:
        r0 = "webp";
        r10 = r30;
        r0 = r10.equals(r0);
        if (r0 == 0) goto L_0x0333;
    L_0x02fb:
        r4 = 0;
        goto L_0x0334;
    L_0x02fd:
        r10 = r30;
        r0 = r10.equals(r15);
        if (r0 == 0) goto L_0x0333;
    L_0x0305:
        r4 = 1;
        goto L_0x0334;
    L_0x0307:
        r10 = r30;
        r0 = r10.equals(r4);
        if (r0 == 0) goto L_0x0333;
    L_0x030f:
        r4 = 5;
        goto L_0x0334;
    L_0x0311:
        r10 = r30;
        r0 = r10.equals(r14);
        if (r0 == 0) goto L_0x0333;
    L_0x0319:
        r4 = 4;
        goto L_0x0334;
    L_0x031b:
        r10 = r30;
        r4 = r26;
        r0 = r10.equals(r4);
        if (r0 == 0) goto L_0x0333;
    L_0x0325:
        r4 = 2;
        goto L_0x0334;
    L_0x0327:
        r10 = r30;
        r4 = r22;
        r0 = r10.equals(r4);
        if (r0 == 0) goto L_0x0333;
    L_0x0331:
        r4 = 3;
        goto L_0x0334;
    L_0x0333:
        r4 = -1;
    L_0x0334:
        if (r4 == 0) goto L_0x036c;
    L_0x0336:
        r11 = 1;
        if (r4 == r11) goto L_0x0367;
    L_0x0339:
        r11 = 2;
        if (r4 == r11) goto L_0x0362;
    L_0x033c:
        r0 = 3;
        if (r4 == r0) goto L_0x035d;
    L_0x033f:
        r0 = 4;
        if (r4 == r0) goto L_0x0358;
    L_0x0342:
        r0 = 5;
        if (r4 == r0) goto L_0x0353;
    L_0x0345:
        r4 = r19;
        r0 = r4.getMimeTypeFromExtension(r10);
        if (r0 == 0) goto L_0x0350;
    L_0x034d:
        r7.mime_type = r0;
        goto L_0x0373;
    L_0x0350:
        r7.mime_type = r1;
        goto L_0x0373;
    L_0x0353:
        r0 = "audio/flac";
        r7.mime_type = r0;
        goto L_0x0373;
    L_0x0358:
        r0 = "audio/ogg";
        r7.mime_type = r0;
        goto L_0x0373;
    L_0x035d:
        r0 = "audio/m4a";
        r7.mime_type = r0;
        goto L_0x0373;
    L_0x0362:
        r0 = "audio/mpeg";
        r7.mime_type = r0;
        goto L_0x0373;
    L_0x0367:
        r0 = "audio/opus";
        r7.mime_type = r0;
        goto L_0x0373;
    L_0x036c:
        r0 = "image/webp";
        r7.mime_type = r0;
        goto L_0x0373;
    L_0x0371:
        r7.mime_type = r1;
    L_0x0373:
        r0 = r7.mime_type;
        r1 = "image/gif";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x03be;
    L_0x037d:
        if (r38 == 0) goto L_0x0387;
    L_0x037f:
        r0 = r38.getGroupIdForUse();
        r4 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
        if (r4 != 0) goto L_0x03be;
    L_0x0387:
        r0 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x03ba }
        r1 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r2 = 0;
        r4 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r2, r1, r1, r4);	 Catch:{ Exception -> 0x03ba }
        if (r0 == 0) goto L_0x03be;
    L_0x0395:
        r2 = "animation.gif";
        r9.file_name = r2;	 Catch:{ Exception -> 0x03ba }
        r2 = r7.attributes;	 Catch:{ Exception -> 0x03ba }
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;	 Catch:{ Exception -> 0x03ba }
        r4.<init>();	 Catch:{ Exception -> 0x03ba }
        r2.add(r4);	 Catch:{ Exception -> 0x03ba }
        r2 = 55;
        r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r1, r1, r2, r3);	 Catch:{ Exception -> 0x03ba }
        if (r1 == 0) goto L_0x03b6;
    L_0x03ab:
        r2 = r7.thumbs;	 Catch:{ Exception -> 0x03ba }
        r2.add(r1);	 Catch:{ Exception -> 0x03ba }
        r1 = r7.flags;	 Catch:{ Exception -> 0x03ba }
        r2 = 1;
        r1 = r1 | r2;
        r7.flags = r1;	 Catch:{ Exception -> 0x03ba }
    L_0x03b6:
        r0.recycle();	 Catch:{ Exception -> 0x03ba }
        goto L_0x03be;
    L_0x03ba:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03be:
        r0 = r7.mime_type;
        r1 = "image/webp";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0437;
    L_0x03c8:
        if (r29 == 0) goto L_0x0437;
    L_0x03ca:
        if (r38 != 0) goto L_0x0437;
    L_0x03cc:
        r1 = new android.graphics.BitmapFactory$Options;
        r1.<init>();
        r2 = 1;
        r1.inJustDecodeBounds = r2;	 Catch:{ Exception -> 0x03fb }
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x03fb }
        r2 = "r";
        r0.<init>(r13, r2);	 Catch:{ Exception -> 0x03fb }
        r19 = r0.getChannel();	 Catch:{ Exception -> 0x03fb }
        r20 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Exception -> 0x03fb }
        r21 = 0;
        r2 = r13.length();	 Catch:{ Exception -> 0x03fb }
        r2 = (long) r2;	 Catch:{ Exception -> 0x03fb }
        r23 = r2;
        r2 = r19.map(r20, r21, r23);	 Catch:{ Exception -> 0x03fb }
        r3 = r2.limit();	 Catch:{ Exception -> 0x03fb }
        r4 = 0;
        r5 = 1;
        org.telegram.messenger.Utilities.loadWebpImage(r4, r2, r3, r1, r5);	 Catch:{ Exception -> 0x03fb }
        r0.close();	 Catch:{ Exception -> 0x03fb }
        goto L_0x03ff;
    L_0x03fb:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03ff:
        r0 = r1.outWidth;
        if (r0 == 0) goto L_0x0437;
    L_0x0403:
        r2 = r1.outHeight;
        if (r2 == 0) goto L_0x0437;
    L_0x0407:
        r3 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r0 > r3) goto L_0x0437;
    L_0x040b:
        r0 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r2 > r0) goto L_0x0437;
    L_0x040f:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
        r0.<init>();
        r2 = r32;
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
        goto L_0x0439;
    L_0x0437:
        r2 = r32;
    L_0x0439:
        r3 = r7;
        if (r36 == 0) goto L_0x0442;
    L_0x043c:
        r0 = r36.toString();
        r10 = r0;
        goto L_0x0443;
    L_0x0442:
        r10 = r2;
    L_0x0443:
        r5 = new java.util.HashMap;
        r5.<init>();
        if (r18 == 0) goto L_0x0451;
    L_0x044a:
        r0 = "originalPath";
        r1 = r18;
        r5.put(r0, r1);
    L_0x0451:
        if (r39 == 0) goto L_0x045a;
    L_0x0453:
        r0 = "forceDocument";
        r1 = "1";
        r5.put(r0, r1);
    L_0x045a:
        if (r8 == 0) goto L_0x0461;
    L_0x045c:
        r0 = "parentObject";
        r5.put(r0, r8);
    L_0x0461:
        r14 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$MPg0uLkoYJmnXNFqAe0X_D_nmR0;
        r0 = r14;
        r1 = r38;
        r2 = r28;
        r4 = r13;
        r6 = r8;
        r7 = r33;
        r9 = r35;
        r11 = r37;
        r12 = r40;
        r13 = r41;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r14);
        r1 = 1;
        return r1;
    L_0x047c:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, boolean, boolean, int):boolean");
    }

    static /* synthetic */ void lambda$prepareSendingDocumentInternal$56(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, String str3, ArrayList arrayList, boolean z, int i) {
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

    public static void prepareSendingAudioDocuments(AccountInstance accountInstance, ArrayList<MessageObject> arrayList, String str, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i) {
        new Thread(new -$$Lambda$SendMessagesHelper$hp894u1eYSMhq2Ig3gKvBjIqLnM(arrayList, j, accountInstance, str, messageObject2, messageObject, z, i)).start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0078  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00a5 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00a0  */
    static /* synthetic */ void lambda$prepareSendingAudioDocuments$58(java.util.ArrayList r19, long r20, org.telegram.messenger.AccountInstance r22, java.lang.String r23, org.telegram.messenger.MessageObject r24, org.telegram.messenger.MessageObject r25, boolean r26, int r27) {
        /*
        r13 = r20;
        r15 = r19.size();
        r16 = 0;
        r12 = 0;
    L_0x0009:
        if (r12 >= r15) goto L_0x00c1;
    L_0x000b:
        r11 = r19;
        r0 = r11.get(r12);
        r4 = r0;
        r4 = (org.telegram.messenger.MessageObject) r4;
        r0 = r4.messageOwner;
        r0 = r0.attachPath;
        r1 = new java.io.File;
        r1.<init>(r0);
        r2 = (int) r13;
        r3 = 1;
        if (r2 != 0) goto L_0x0023;
    L_0x0021:
        r2 = 1;
        goto L_0x0024;
    L_0x0023:
        r2 = 0;
    L_0x0024:
        if (r0 == 0) goto L_0x003e;
    L_0x0026:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r0);
        r0 = "audio";
        r5.append(r0);
        r0 = r1.length();
        r5.append(r0);
        r0 = r5.toString();
    L_0x003e:
        r1 = 0;
        if (r2 != 0) goto L_0x0066;
    L_0x0041:
        r5 = r22.getMessagesStorage();
        if (r2 != 0) goto L_0x0049;
    L_0x0047:
        r6 = 1;
        goto L_0x004a;
    L_0x0049:
        r6 = 4;
    L_0x004a:
        r5 = r5.getSentFile(r0, r6);
        if (r5 == 0) goto L_0x0066;
    L_0x0050:
        r6 = r5[r16];
        r17 = r6;
        r17 = (org.telegram.tgnet.TLRPC.TL_document) r17;
        r3 = r5[r3];
        r3 = (java.lang.String) r3;
        r8 = 0;
        r9 = 0;
        r5 = r2;
        r6 = r17;
        r7 = r0;
        ensureMediaThumbExists(r5, r6, r7, r8, r9);
        r6 = r3;
        goto L_0x0069;
    L_0x0066:
        r6 = r1;
        r17 = r6;
    L_0x0069:
        if (r17 != 0) goto L_0x0074;
    L_0x006b:
        r3 = r4.messageOwner;
        r3 = r3.media;
        r3 = r3.document;
        r3 = (org.telegram.tgnet.TLRPC.TL_document) r3;
        goto L_0x0076;
    L_0x0074:
        r3 = r17;
    L_0x0076:
        if (r2 == 0) goto L_0x008c;
    L_0x0078:
        r2 = 32;
        r7 = r13 >> r2;
        r2 = (int) r7;
        r5 = r22.getMessagesController();
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r5.getEncryptedChat(r2);
        if (r2 != 0) goto L_0x008c;
    L_0x008b:
        return;
    L_0x008c:
        if (r12 != 0) goto L_0x0091;
    L_0x008e:
        r10 = r23;
        goto L_0x0092;
    L_0x0091:
        r10 = r1;
    L_0x0092:
        r5 = new java.util.HashMap;
        r5.<init>();
        if (r0 == 0) goto L_0x009e;
    L_0x0099:
        r1 = "originalPath";
        r5.put(r1, r0);
    L_0x009e:
        if (r6 == 0) goto L_0x00a5;
    L_0x00a0:
        r0 = "parentObject";
        r5.put(r0, r6);
    L_0x00a5:
        r17 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$ncpM1KI8cZ87_6ujC8P1vKoYuis;
        r0 = r17;
        r1 = r24;
        r2 = r22;
        r7 = r20;
        r9 = r25;
        r11 = r26;
        r18 = r12;
        r12 = r27;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r17);
        r12 = r18 + 1;
        goto L_0x0009;
    L_0x00c1:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$58(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$57(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, String str2, boolean z, int i) {
        MessageObject messageObject4 = messageObject2;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, null, tL_document, messageObject4.messageOwner.attachPath, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, null, messageObject4.messageOwner.attachPath, j, messageObject3, str2, null, null, hashMap, z, i, 0, str);
    }

    public static void prepareSendingDocuments(AccountInstance accountInstance, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<Uri> arrayList3, String str, String str2, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject2, boolean z, int i) {
        if (!(arrayList == null && arrayList2 == null && arrayList3 == null) && (arrayList == null || arrayList2 == null || arrayList.size() == arrayList2.size())) {
            new Thread(new -$$Lambda$SendMessagesHelper$F2a6gaItfzT11lGkSYNnjd9bVfM(arrayList, str, accountInstance, arrayList2, str2, j, messageObject, messageObject2, z, i, arrayList3, inputContentInfoCompat)).start();
        }
    }

    static /* synthetic */ void lambda$prepareSendingDocuments$60(ArrayList arrayList, String str, AccountInstance accountInstance, ArrayList arrayList2, String str2, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, ArrayList arrayList3, InputContentInfoCompat inputContentInfoCompat) {
        Object obj;
        ArrayList arrayList4 = arrayList;
        ArrayList arrayList5 = arrayList3;
        int i2 = 0;
        if (arrayList4 != null) {
            int i3 = 0;
            obj = null;
            while (i3 < arrayList.size()) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList4.get(i3), (String) arrayList2.get(i3), null, str2, j, messageObject, i3 == 0 ? str : null, null, messageObject2, false, z, i)) {
                    obj = 1;
                }
                i3++;
            }
        } else {
            obj = null;
        }
        if (arrayList5 != null) {
            while (i2 < arrayList3.size()) {
                CharSequence charSequence = (i2 == 0 && (arrayList4 == null || arrayList.size() == 0)) ? str : null;
                if (!prepareSendingDocumentInternal(accountInstance, null, null, (Uri) arrayList5.get(i2), str2, j, messageObject, charSequence, null, messageObject2, false, z, i)) {
                    obj = 1;
                }
                i2++;
            }
        }
        if (inputContentInfoCompat != null) {
            inputContentInfoCompat.releasePermission();
        }
        if (obj != null) {
            AndroidUtilities.runOnUIThread(-$$Lambda$SendMessagesHelper$9FWiBOGXPt-Jb0IsUnmHjYiHVSA.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$null$59() {
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
                new Thread(new -$$Lambda$SendMessagesHelper$NliVaxhcWd_dBNnHSZCerOgfUH8(j, botInlineResult, accountInstance, hashMap, messageObject, z, i)).run();
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

    /* JADX WARNING: Removed duplicated region for block: B:161:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03cf  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0428  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03cf  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0428  */
    static /* synthetic */ void lambda$prepareSendingBotContextResult$62(long r20, org.telegram.tgnet.TLRPC.BotInlineResult r22, org.telegram.messenger.AccountInstance r23, java.util.HashMap r24, org.telegram.messenger.MessageObject r25, boolean r26, int r27) {
        /*
        r10 = r22;
        r11 = r24;
        r7 = r20;
        r0 = (int) r7;
        r1 = 0;
        r2 = 1;
        if (r0 != 0) goto L_0x000d;
    L_0x000b:
        r12 = 1;
        goto L_0x000e;
    L_0x000d:
        r12 = 0;
    L_0x000e:
        r3 = r10.type;
        r4 = "game";
        r3 = r4.equals(r3);
        r5 = 0;
        if (r3 == 0) goto L_0x004f;
    L_0x0019:
        if (r0 != 0) goto L_0x001c;
    L_0x001b:
        return;
    L_0x001c:
        r0 = new org.telegram.tgnet.TLRPC$TL_game;
        r0.<init>();
        r3 = r10.title;
        r0.title = r3;
        r3 = r10.description;
        r0.description = r3;
        r3 = r10.id;
        r0.short_name = r3;
        r3 = r10.photo;
        r0.photo = r3;
        r3 = r0.photo;
        if (r3 != 0) goto L_0x003c;
    L_0x0035:
        r3 = new org.telegram.tgnet.TLRPC$TL_photoEmpty;
        r3.<init>();
        r0.photo = r3;
    L_0x003c:
        r3 = r10.document;
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r6 == 0) goto L_0x0049;
    L_0x0042:
        r0.document = r3;
        r3 = r0.flags;
        r3 = r3 | r2;
        r0.flags = r3;
    L_0x0049:
        r18 = r0;
        r0 = r5;
        r6 = r0;
        goto L_0x0410;
    L_0x004f:
        r0 = r10 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMediaResult;
        if (r0 == 0) goto L_0x0071;
    L_0x0053:
        r0 = r10.document;
        if (r0 == 0) goto L_0x0065;
    L_0x0057:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r3 == 0) goto L_0x040a;
    L_0x005b:
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;
        r6 = r5;
        r18 = r6;
        r5 = r0;
        r0 = r18;
        goto L_0x0410;
    L_0x0065:
        r0 = r10.photo;
        if (r0 == 0) goto L_0x040a;
    L_0x0069:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r3 == 0) goto L_0x040a;
    L_0x006d:
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        goto L_0x040d;
    L_0x0071:
        r0 = r10.content;
        if (r0 == 0) goto L_0x040a;
    L_0x0075:
        r0 = new java.io.File;
        r3 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r3);
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r13 = r10.content;
        r13 = r13.url;
        r13 = org.telegram.messenger.Utilities.MD5(r13);
        r9.append(r13);
        r13 = ".";
        r9.append(r13);
        r14 = r10.content;
        r14 = r14.url;
        r15 = "file";
        r14 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r14, r15);
        r9.append(r14);
        r9 = r9.toString();
        r0.<init>(r6, r9);
        r6 = r0.exists();
        if (r6 == 0) goto L_0x00b0;
    L_0x00ab:
        r6 = r0.getAbsolutePath();
        goto L_0x00b4;
    L_0x00b0:
        r6 = r10.content;
        r6 = r6.url;
    L_0x00b4:
        r9 = r10.type;
        r14 = r9.hashCode();
        r4 = "gif";
        r3 = "sticker";
        r2 = 2;
        switch(r14) {
            case -1890252483: goto L_0x00fd;
            case 102340: goto L_0x00f5;
            case 3143036: goto L_0x00ed;
            case 93166550: goto L_0x00e3;
            case 106642994: goto L_0x00d9;
            case 112202875: goto L_0x00ce;
            case 112386354: goto L_0x00c3;
            default: goto L_0x00c2;
        };
    L_0x00c2:
        goto L_0x0105;
    L_0x00c3:
        r14 = "voice";
        r9 = r9.equals(r14);
        if (r9 == 0) goto L_0x0105;
    L_0x00cc:
        r9 = 1;
        goto L_0x0106;
    L_0x00ce:
        r14 = "video";
        r9 = r9.equals(r14);
        if (r9 == 0) goto L_0x0105;
    L_0x00d7:
        r9 = 3;
        goto L_0x0106;
    L_0x00d9:
        r14 = "photo";
        r9 = r9.equals(r14);
        if (r9 == 0) goto L_0x0105;
    L_0x00e1:
        r9 = 6;
        goto L_0x0106;
    L_0x00e3:
        r14 = "audio";
        r9 = r9.equals(r14);
        if (r9 == 0) goto L_0x0105;
    L_0x00eb:
        r9 = 0;
        goto L_0x0106;
    L_0x00ed:
        r9 = r9.equals(r15);
        if (r9 == 0) goto L_0x0105;
    L_0x00f3:
        r9 = 2;
        goto L_0x0106;
    L_0x00f5:
        r9 = r9.equals(r4);
        if (r9 == 0) goto L_0x0105;
    L_0x00fb:
        r9 = 5;
        goto L_0x0106;
    L_0x00fd:
        r9 = r9.equals(r3);
        if (r9 == 0) goto L_0x0105;
    L_0x0103:
        r9 = 4;
        goto L_0x0106;
    L_0x0105:
        r9 = -1;
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
        };
    L_0x0109:
        r13 = r5;
        r0 = r13;
        r5 = r0;
    L_0x010c:
        r18 = r5;
        goto L_0x0410;
    L_0x0110:
        r0 = r0.exists();
        if (r0 == 0) goto L_0x011f;
    L_0x0116:
        r0 = r23.getSendMessagesHelper();
        r0 = r0.generatePhotoSizes(r6, r5);
        goto L_0x0120;
    L_0x011f:
        r0 = r5;
    L_0x0120:
        if (r0 != 0) goto L_0x010c;
    L_0x0122:
        r0 = new org.telegram.tgnet.TLRPC$TL_photo;
        r0.<init>();
        r2 = r23.getConnectionsManager();
        r2 = r2.getCurrentTime();
        r0.date = r2;
        r2 = new byte[r1];
        r0.file_reference = r2;
        r2 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r2.<init>();
        r3 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22);
        r4 = r3[r1];
        r2.w = r4;
        r4 = 1;
        r3 = r3[r4];
        r2.h = r3;
        r2.size = r4;
        r3 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r3.<init>();
        r2.location = r3;
        r3 = "x";
        r2.type = r3;
        r3 = r0.sizes;
        r3.add(r2);
        goto L_0x010c;
    L_0x015b:
        r9 = new org.telegram.tgnet.TLRPC$TL_document;
        r9.<init>();
        r19 = r6;
        r5 = 0;
        r9.id = r5;
        r9.size = r1;
        r9.dc_id = r1;
        r0 = r10.content;
        r0 = r0.mime_type;
        r9.mime_type = r0;
        r0 = new byte[r1];
        r9.file_reference = r0;
        r0 = r23.getConnectionsManager();
        r0 = r0.getCurrentTime();
        r9.date = r0;
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r5.<init>();
        r0 = r9.attributes;
        r0.add(r5);
        r0 = r10.type;
        r6 = r0.hashCode();
        switch(r6) {
            case -1890252483: goto L_0x01c2;
            case 102340: goto L_0x01ba;
            case 3143036: goto L_0x01b2;
            case 93166550: goto L_0x01a8;
            case 112202875: goto L_0x019d;
            case 112386354: goto L_0x0192;
            default: goto L_0x0191;
        };
    L_0x0191:
        goto L_0x01ca;
    L_0x0192:
        r3 = "voice";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x01ca;
    L_0x019b:
        r0 = 1;
        goto L_0x01cb;
    L_0x019d:
        r3 = "video";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x01ca;
    L_0x01a6:
        r0 = 4;
        goto L_0x01cb;
    L_0x01a8:
        r3 = "audio";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x01ca;
    L_0x01b0:
        r0 = 2;
        goto L_0x01cb;
    L_0x01b2:
        r0 = r0.equals(r15);
        if (r0 == 0) goto L_0x01ca;
    L_0x01b8:
        r0 = 3;
        goto L_0x01cb;
    L_0x01ba:
        r0 = r0.equals(r4);
        if (r0 == 0) goto L_0x01ca;
    L_0x01c0:
        r0 = 0;
        goto L_0x01cb;
    L_0x01c2:
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x01ca;
    L_0x01c8:
        r0 = 5;
        goto L_0x01cb;
    L_0x01ca:
        r0 = -1;
    L_0x01cb:
        r3 = 55;
        if (r0 == 0) goto L_0x0361;
    L_0x01cf:
        r4 = 1;
        if (r0 == r4) goto L_0x0348;
    L_0x01d2:
        if (r0 == r2) goto L_0x031d;
    L_0x01d4:
        r4 = 3;
        if (r0 == r4) goto L_0x02ed;
    L_0x01d7:
        r2 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r4 = 4;
        if (r0 == r4) goto L_0x026f;
    L_0x01dc:
        r4 = 5;
        if (r0 == r4) goto L_0x01e4;
    L_0x01df:
        r6 = r19;
        r13 = 0;
        goto L_0x03c5;
    L_0x01e4:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
        r0.<init>();
        r4 = "";
        r0.alt = r4;
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
        r4.<init>();
        r0.stickerset = r4;
        r4 = r9.attributes;
        r4.add(r0);
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
        r0.<init>();
        r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22);
        r6 = r4[r1];
        r0.w = r6;
        r6 = 1;
        r4 = r4[r6];
        r0.h = r4;
        r4 = r9.attributes;
        r4.add(r0);
        r0 = "sticker.webp";
        r5.file_name = r0;
        r0 = r10.thumb;	 Catch:{ all -> 0x0269 }
        if (r0 == 0) goto L_0x01df;
    L_0x0218:
        r0 = new java.io.File;	 Catch:{ all -> 0x0269 }
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ all -> 0x0269 }
        r6 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0269 }
        r6.<init>();	 Catch:{ all -> 0x0269 }
        r14 = r10.thumb;	 Catch:{ all -> 0x0269 }
        r14 = r14.url;	 Catch:{ all -> 0x0269 }
        r14 = org.telegram.messenger.Utilities.MD5(r14);	 Catch:{ all -> 0x0269 }
        r6.append(r14);	 Catch:{ all -> 0x0269 }
        r6.append(r13);	 Catch:{ all -> 0x0269 }
        r13 = r10.thumb;	 Catch:{ all -> 0x0269 }
        r13 = r13.url;	 Catch:{ all -> 0x0269 }
        r14 = "webp";
        r13 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r13, r14);	 Catch:{ all -> 0x0269 }
        r6.append(r13);	 Catch:{ all -> 0x0269 }
        r6 = r6.toString();	 Catch:{ all -> 0x0269 }
        r0.<init>(r4, r6);	 Catch:{ all -> 0x0269 }
        r0 = r0.getAbsolutePath();	 Catch:{ all -> 0x0269 }
        r4 = 0;
        r6 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r4, r2, r2, r6);	 Catch:{ all -> 0x0269 }
        if (r0 == 0) goto L_0x01df;
    L_0x0253:
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r3, r1);	 Catch:{ all -> 0x0269 }
        if (r2 == 0) goto L_0x0264;
    L_0x0259:
        r3 = r9.thumbs;	 Catch:{ all -> 0x0269 }
        r3.add(r2);	 Catch:{ all -> 0x0269 }
        r2 = r9.flags;	 Catch:{ all -> 0x0269 }
        r3 = 1;
        r2 = r2 | r3;
        r9.flags = r2;	 Catch:{ all -> 0x0269 }
    L_0x0264:
        r0.recycle();	 Catch:{ all -> 0x0269 }
        goto L_0x01df;
    L_0x0269:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x01df;
    L_0x026f:
        r0 = "video.mp4";
        r5.file_name = r0;
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r0.<init>();
        r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22);
        r6 = r4[r1];
        r0.w = r6;
        r6 = 1;
        r4 = r4[r6];
        r0.h = r4;
        r4 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22);
        r0.duration = r4;
        r0.supports_streaming = r6;
        r4 = r9.attributes;
        r4.add(r0);
        r0 = r10.thumb;	 Catch:{ all -> 0x02e7 }
        if (r0 == 0) goto L_0x01df;
    L_0x0297:
        r0 = new java.io.File;	 Catch:{ all -> 0x02e7 }
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ all -> 0x02e7 }
        r6 = new java.lang.StringBuilder;	 Catch:{ all -> 0x02e7 }
        r6.<init>();	 Catch:{ all -> 0x02e7 }
        r14 = r10.thumb;	 Catch:{ all -> 0x02e7 }
        r14 = r14.url;	 Catch:{ all -> 0x02e7 }
        r14 = org.telegram.messenger.Utilities.MD5(r14);	 Catch:{ all -> 0x02e7 }
        r6.append(r14);	 Catch:{ all -> 0x02e7 }
        r6.append(r13);	 Catch:{ all -> 0x02e7 }
        r13 = r10.thumb;	 Catch:{ all -> 0x02e7 }
        r13 = r13.url;	 Catch:{ all -> 0x02e7 }
        r14 = "jpg";
        r13 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r13, r14);	 Catch:{ all -> 0x02e7 }
        r6.append(r13);	 Catch:{ all -> 0x02e7 }
        r6 = r6.toString();	 Catch:{ all -> 0x02e7 }
        r0.<init>(r4, r6);	 Catch:{ all -> 0x02e7 }
        r0 = r0.getAbsolutePath();	 Catch:{ all -> 0x02e7 }
        r4 = 0;
        r6 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r4, r2, r2, r6);	 Catch:{ all -> 0x02e7 }
        if (r0 == 0) goto L_0x01df;
    L_0x02d1:
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r3, r1);	 Catch:{ all -> 0x02e7 }
        if (r2 == 0) goto L_0x02e2;
    L_0x02d7:
        r3 = r9.thumbs;	 Catch:{ all -> 0x02e7 }
        r3.add(r2);	 Catch:{ all -> 0x02e7 }
        r2 = r9.flags;	 Catch:{ all -> 0x02e7 }
        r3 = 1;
        r2 = r2 | r3;
        r9.flags = r2;	 Catch:{ all -> 0x02e7 }
    L_0x02e2:
        r0.recycle();	 Catch:{ all -> 0x02e7 }
        goto L_0x01df;
    L_0x02e7:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x01df;
    L_0x02ed:
        r0 = r10.content;
        r0 = r0.mime_type;
        r2 = 47;
        r0 = r0.lastIndexOf(r2);
        r2 = -1;
        if (r0 == r2) goto L_0x0319;
    L_0x02fa:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "file.";
        r2.append(r3);
        r3 = r10.content;
        r3 = r3.mime_type;
        r4 = 1;
        r0 = r0 + r4;
        r0 = r3.substring(r0);
        r2.append(r0);
        r0 = r2.toString();
        r5.file_name = r0;
        goto L_0x01df;
    L_0x0319:
        r5.file_name = r15;
        goto L_0x01df;
    L_0x031d:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
        r0.<init>();
        r3 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22);
        r0.duration = r3;
        r3 = r10.title;
        r0.title = r3;
        r3 = r0.flags;
        r4 = 1;
        r3 = r3 | r4;
        r0.flags = r3;
        r3 = r10.description;
        if (r3 == 0) goto L_0x033d;
    L_0x0336:
        r0.performer = r3;
        r3 = r0.flags;
        r2 = r2 | r3;
        r0.flags = r2;
    L_0x033d:
        r2 = "audio.mp3";
        r5.file_name = r2;
        r2 = r9.attributes;
        r2.add(r0);
        goto L_0x01df;
    L_0x0348:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
        r0.<init>();
        r2 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22);
        r0.duration = r2;
        r2 = 1;
        r0.voice = r2;
        r2 = "audio.ogg";
        r5.file_name = r2;
        r2 = r9.attributes;
        r2.add(r0);
        goto L_0x01df;
    L_0x0361:
        r0 = "animation.gif";
        r5.file_name = r0;
        r0 = "mp4";
        r6 = r19;
        r0 = r6.endsWith(r0);
        if (r0 == 0) goto L_0x037f;
    L_0x036f:
        r0 = "video/mp4";
        r9.mime_type = r0;
        r0 = r9.attributes;
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r2.<init>();
        r0.add(r2);
        goto L_0x0383;
    L_0x037f:
        r0 = "image/gif";
        r9.mime_type = r0;
    L_0x0383:
        if (r12 == 0) goto L_0x0388;
    L_0x0385:
        r4 = 90;
        goto L_0x038a;
    L_0x0388:
        r4 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
    L_0x038a:
        r0 = "mp4";
        r0 = r6.endsWith(r0);	 Catch:{ all -> 0x03c0 }
        if (r0 == 0) goto L_0x0399;
    L_0x0392:
        r2 = 1;
        r0 = android.media.ThumbnailUtils.createVideoThumbnail(r6, r2);	 Catch:{ all -> 0x03c0 }
        r13 = 0;
        goto L_0x03a0;
    L_0x0399:
        r2 = 1;
        r0 = (float) r4;
        r13 = 0;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r6, r13, r0, r0, r2);	 Catch:{ all -> 0x03be }
    L_0x03a0:
        if (r0 == 0) goto L_0x03c5;
    L_0x03a2:
        r2 = (float) r4;	 Catch:{ all -> 0x03be }
        r14 = 90;
        if (r4 <= r14) goto L_0x03a9;
    L_0x03a7:
        r3 = 80;
    L_0x03a9:
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r3, r1);	 Catch:{ all -> 0x03be }
        if (r2 == 0) goto L_0x03ba;
    L_0x03af:
        r3 = r9.thumbs;	 Catch:{ all -> 0x03be }
        r3.add(r2);	 Catch:{ all -> 0x03be }
        r2 = r9.flags;	 Catch:{ all -> 0x03be }
        r3 = 1;
        r2 = r2 | r3;
        r9.flags = r2;	 Catch:{ all -> 0x03be }
    L_0x03ba:
        r0.recycle();	 Catch:{ all -> 0x03be }
        goto L_0x03c5;
    L_0x03be:
        r0 = move-exception;
        goto L_0x03c2;
    L_0x03c0:
        r0 = move-exception;
        r13 = 0;
    L_0x03c2:
        org.telegram.messenger.FileLog.e(r0);
    L_0x03c5:
        r0 = r5.file_name;
        if (r0 != 0) goto L_0x03cb;
    L_0x03c9:
        r5.file_name = r15;
    L_0x03cb:
        r0 = r9.mime_type;
        if (r0 != 0) goto L_0x03d3;
    L_0x03cf:
        r0 = "application/octet-stream";
        r9.mime_type = r0;
    L_0x03d3:
        r0 = r9.thumbs;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0405;
    L_0x03db:
        r0 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r0.<init>();
        r2 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22);
        r3 = r2[r1];
        r0.w = r3;
        r3 = 1;
        r2 = r2[r3];
        r0.h = r2;
        r0.size = r1;
        r2 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r2.<init>();
        r0.location = r2;
        r2 = "x";
        r0.type = r2;
        r2 = r9.thumbs;
        r2.add(r0);
        r0 = r9.flags;
        r0 = r0 | r3;
        r9.flags = r0;
    L_0x0405:
        r5 = r9;
        r0 = r13;
        r18 = r0;
        goto L_0x0410;
    L_0x040a:
        r13 = r5;
        r0 = r13;
        r5 = r0;
    L_0x040d:
        r6 = r5;
        r18 = r6;
    L_0x0410:
        if (r11 == 0) goto L_0x041d;
    L_0x0412:
        r2 = r10.content;
        if (r2 == 0) goto L_0x041d;
    L_0x0416:
        r2 = r2.url;
        r3 = "originalPath";
        r11.put(r3, r2);
    L_0x041d:
        r2 = 1;
        r3 = new android.graphics.Bitmap[r2];
        r4 = new java.lang.String[r2];
        r9 = org.telegram.messenger.MessageObject.isGifDocument(r5);
        if (r9 == 0) goto L_0x0450;
    L_0x0428:
        r9 = r5.thumbs;
        r13 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r13);
        r13 = org.telegram.messenger.FileLoader.getPathToAttach(r5);
        r14 = r13.exists();
        if (r14 != 0) goto L_0x043e;
    L_0x043a:
        r13 = org.telegram.messenger.FileLoader.getPathToAttach(r5, r2);
    L_0x043e:
        r14 = r13.getAbsolutePath();
        r15 = 0;
        r16 = 0;
        r13 = r5;
        ensureMediaThumbExists(r12, r13, r14, r15, r16);
        r2 = 1;
        r2 = getKeyForPhotoSize(r9, r3, r2, r2);
        r4[r1] = r2;
    L_0x0450:
        r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$MIZbjfh6Bb5p9kEGsqUHYA3MKhI;
        r1 = r16;
        r2 = r5;
        r5 = r23;
        r7 = r20;
        r9 = r25;
        r10 = r22;
        r11 = r24;
        r12 = r26;
        r13 = r27;
        r14 = r0;
        r15 = r18;
        r1.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$62(long, org.telegram.tgnet.TLRPC$BotInlineResult, org.telegram.messenger.AccountInstance, java.util.HashMap, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$61(TL_document tL_document, Bitmap[] bitmapArr, String[] strArr, AccountInstance accountInstance, String str, long j, MessageObject messageObject, BotInlineResult botInlineResult, HashMap hashMap, boolean z, int i, TL_photo tL_photo, TL_game tL_game) {
        BotInlineResult botInlineResult2 = botInlineResult;
        SendMessagesHelper sendMessagesHelper;
        BotInlineMessage botInlineMessage;
        if (tL_document != null) {
            if (!(bitmapArr[0] == null || strArr[0] == null)) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
            }
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
        accountInstance.getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$RQSyvT-WSvUvFCbrmyspyHUtI7M(str, accountInstance, j, z, i));
    }

    static /* synthetic */ void lambda$null$63(String str, AccountInstance accountInstance, long j, boolean z, int i) {
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
        PhotoSize closestPhotoSizeWithSize;
        if (tLObject2 instanceof TL_photo) {
            boolean z2;
            TL_photo tL_photo = (TL_photo) tLObject2;
            PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tL_photo.sizes, 90);
            if (closestPhotoSizeWithSize2 instanceof TL_photoStrippedSize) {
                z2 = true;
            } else {
                z2 = FileLoader.getPathToAttach(closestPhotoSizeWithSize2, true).exists();
            }
            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tL_photo.sizes, AndroidUtilities.getPhotoSize());
            boolean exists = FileLoader.getPathToAttach(closestPhotoSizeWithSize, false).exists();
            if (!(z2 && exists)) {
                PhotoSize scaleAndSaveImage;
                Bitmap loadBitmap = ImageLoader.loadBitmap(str2, uri2, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
                Bitmap loadBitmap2 = loadBitmap == null ? ImageLoader.loadBitmap(str2, uri2, 800.0f, 800.0f, true) : loadBitmap;
                if (!exists) {
                    scaleAndSaveImage = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize, loadBitmap2, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101, false);
                    if (scaleAndSaveImage != closestPhotoSizeWithSize) {
                        tL_photo.sizes.add(0, scaleAndSaveImage);
                    }
                }
                if (!z2) {
                    scaleAndSaveImage = ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize2, loadBitmap2, 90.0f, 90.0f, 55, true, false);
                    if (scaleAndSaveImage != closestPhotoSizeWithSize2) {
                        tL_photo.sizes.add(0, scaleAndSaveImage);
                    }
                }
                if (loadBitmap2 != null) {
                    loadBitmap2.recycle();
                }
            }
        } else if (tLObject2 instanceof TL_document) {
            Document document = (TL_document) tLObject2;
            if ((MessageObject.isVideoDocument(document) || MessageObject.isNewGifDocument(document)) && MessageObject.isDocumentHasThumb(document)) {
                int i = 320;
                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                if (!((closestPhotoSizeWithSize instanceof TL_photoStrippedSize) || FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists())) {
                    Bitmap createVideoThumbnail = createVideoThumbnail(str2, j);
                    Bitmap createVideoThumbnail2 = createVideoThumbnail == null ? ThumbnailUtils.createVideoThumbnail(str2, 1) : createVideoThumbnail;
                    if (z) {
                        i = 90;
                    }
                    float f = (float) i;
                    document.thumbs.set(0, ImageLoader.scaleAndSaveImage(closestPhotoSizeWithSize, createVideoThumbnail2, f, f, i > 90 ? 80 : 55, false, true));
                }
            }
        }
    }

    public static String getKeyForPhotoSize(PhotoSize photoSize, Bitmap[] bitmapArr, boolean z, boolean z2) {
        if (photoSize == null || photoSize.location == null) {
            return null;
        }
        Point messageSize = ChatMessageCell.getMessageSize(photoSize.w, photoSize.h);
        if (bitmapArr != null) {
            try {
                Options options = new Options();
                options.inJustDecodeBounds = true;
                File pathToAttach = FileLoader.getPathToAttach(photoSize, z2);
                FileInputStream fileInputStream = new FileInputStream(pathToAttach);
                BitmapFactory.decodeStream(fileInputStream, null, options);
                fileInputStream.close();
                float max = Math.max(((float) options.outWidth) / messageSize.x, ((float) options.outHeight) / messageSize.y);
                if (max < 1.0f) {
                    max = 1.0f;
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = (int) max;
                options.inPreferredConfig = Config.RGB_565;
                if (VERSION.SDK_INT >= 21) {
                    fileInputStream = new FileInputStream(pathToAttach);
                    bitmapArr[0] = BitmapFactory.decodeStream(fileInputStream, null, options);
                    fileInputStream.close();
                }
            } catch (Throwable unused) {
            }
        }
        return String.format(Locale.US, z ? "%d_%d@%d_%d_b" : "%d_%d@%d_%d", new Object[]{Long.valueOf(photoSize.location.volume_id), Integer.valueOf(photoSize.location.local_id), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density))});
    }

    public static void prepareSendingMedia(AccountInstance accountInstance, ArrayList<SendingMediaInfo> arrayList, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, boolean z, boolean z2, MessageObject messageObject2, boolean z3, int i) {
        if (!arrayList.isEmpty()) {
            boolean z4;
            int size = arrayList.size();
            int i2 = 0;
            while (true) {
                ArrayList<SendingMediaInfo> arrayList2 = arrayList;
                if (i2 >= size) {
                    z4 = z2;
                    break;
                } else if (((SendingMediaInfo) arrayList2.get(i2)).ttl > 0) {
                    z4 = false;
                    break;
                } else {
                    i2++;
                }
            }
            mediaSendQueue.postRunnable(new -$$Lambda$SendMessagesHelper$EHYR3cc2EDsREL-ScT4lVDi5pJQ(arrayList, j, accountInstance, z, z4, messageObject2, messageObject, z3, i, inputContentInfoCompat));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x013f  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02bb A:{Catch:{ Exception -> 0x02a3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02b1 A:{Catch:{ Exception -> 0x02a3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02c8 A:{Catch:{ Exception -> 0x02a3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x06f0  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x060b  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x0706  */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0718  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x075e  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x0765  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0a90  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0a61  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0a61  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0a90  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x08f7  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x08b3  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x08b3  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x08f7  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x08f7  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x08b3  */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x08ad  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x08a3  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0b2b  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x0b3e  */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x0b50 A:{LOOP_END, LOOP:4: B:451:0x0b4a->B:453:0x0b50} */
    /* JADX WARNING: Removed duplicated region for block: B:471:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0b9a  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0a90  */
    /* JADX WARNING: Removed duplicated region for block: B:427:0x0a61  */
    /* JADX WARNING: Missing block: B:40:0x009d, code skipped:
            if (org.telegram.messenger.MediaController.isWebp(r3.uri) != false) goto L_0x0157;
     */
    /* JADX WARNING: Missing block: B:430:0x0a81, code skipped:
            if (r5 == (r31 - 1)) goto L_0x0a86;
     */
    static /* synthetic */ void lambda$prepareSendingMedia$72(java.util.ArrayList r56, long r57, org.telegram.messenger.AccountInstance r59, boolean r60, boolean r61, org.telegram.messenger.MessageObject r62, org.telegram.messenger.MessageObject r63, boolean r64, int r65, androidx.core.view.inputmethod.InputContentInfoCompat r66) {
        /*
        r1 = r56;
        r14 = r57;
        r13 = r59;
        r18 = java.lang.System.currentTimeMillis();
        r12 = r56.size();
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
        r2 = r59.getMessagesController();
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
        if (r60 != 0) goto L_0x016b;
    L_0x0040:
        if (r61 == 0) goto L_0x016b;
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
        r2 = r59.getMessagesStorage();
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
        r4 = r59.getMessagesStorage();
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
        r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$la6jF8kETgbN8qhomDix2KNyxgg;
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
        r14 = r57;
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
        if (r9 >= r12) goto L_0x0b20;
    L_0x0180:
        r16 = r1.get(r9);
        r15 = r16;
        r15 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r15;
        if (r61 == 0) goto L_0x01a3;
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
        if (r0 == 0) goto L_0x0505;
    L_0x01b7:
        r1 = r0.type;
        r35 = r4;
        r4 = "jpg";
        r36 = r6;
        r6 = ".";
        r7 = 1;
        if (r1 != r7) goto L_0x0386;
    L_0x01c4:
        r1 = new java.util.HashMap;
        r1.<init>();
        r0 = r15.searchImage;
        r0 = r0.document;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r5 == 0) goto L_0x01d9;
    L_0x01d1:
        r5 = r0;
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r5, r7);
        goto L_0x0206;
    L_0x01d9:
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
    L_0x0206:
        if (r5 != 0) goto L_0x0322;
    L_0x0208:
        r5 = new org.telegram.tgnet.TLRPC$TL_document;
        r5.<init>();
        r38 = r8;
        r7 = 0;
        r5.id = r7;
        r7 = 0;
        r8 = new byte[r7];
        r5.file_reference = r8;
        r7 = r59.getConnectionsManager();
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
        if (r7 == 0) goto L_0x0253;
    L_0x0243:
        r7 = "video/mp4";
        r5.mime_type = r7;
        r7 = r5.attributes;
        r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r8.<init>();
        r7.add(r8);
        goto L_0x0257;
    L_0x0253:
        r7 = "image/gif";
        r5.mime_type = r7;
    L_0x0257:
        r7 = r0.exists();
        if (r7 == 0) goto L_0x025f;
    L_0x025d:
        r7 = r0;
        goto L_0x0261;
    L_0x025f:
        r0 = 0;
        r7 = 0;
    L_0x0261:
        if (r0 != 0) goto L_0x0297;
    L_0x0263:
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
        if (r0 != 0) goto L_0x0296;
    L_0x0294:
        r0 = 0;
        goto L_0x0297;
    L_0x0296:
        r0 = r4;
    L_0x0297:
        if (r0 == 0) goto L_0x02ea;
    L_0x0299:
        if (r10 != 0) goto L_0x02a5;
    L_0x029b:
        r4 = r15.ttl;	 Catch:{ Exception -> 0x02a3 }
        if (r4 == 0) goto L_0x02a0;
    L_0x029f:
        goto L_0x02a5;
    L_0x02a0:
        r4 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x02a7;
    L_0x02a3:
        r0 = move-exception;
        goto L_0x02e7;
    L_0x02a5:
        r4 = 90;
    L_0x02a7:
        r6 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x02a3 }
        r2 = r6.endsWith(r2);	 Catch:{ Exception -> 0x02a3 }
        if (r2 == 0) goto L_0x02bb;
    L_0x02b1:
        r0 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x02a3 }
        r2 = 1;
        r0 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r2);	 Catch:{ Exception -> 0x02a3 }
        goto L_0x02c6;
    L_0x02bb:
        r2 = 1;
        r0 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x02a3 }
        r6 = (float) r4;	 Catch:{ Exception -> 0x02a3 }
        r8 = 0;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r8, r6, r6, r2);	 Catch:{ Exception -> 0x02a3 }
    L_0x02c6:
        if (r0 == 0) goto L_0x02ea;
    L_0x02c8:
        r2 = (float) r4;	 Catch:{ Exception -> 0x02a3 }
        r6 = 90;
        if (r4 <= r6) goto L_0x02d0;
    L_0x02cd:
        r4 = 80;
        goto L_0x02d2;
    L_0x02d0:
        r4 = 55;
    L_0x02d2:
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r4, r10);	 Catch:{ Exception -> 0x02a3 }
        if (r2 == 0) goto L_0x02e3;
    L_0x02d8:
        r4 = r5.thumbs;	 Catch:{ Exception -> 0x02a3 }
        r4.add(r2);	 Catch:{ Exception -> 0x02a3 }
        r2 = r5.flags;	 Catch:{ Exception -> 0x02a3 }
        r4 = 1;
        r2 = r2 | r4;
        r5.flags = r2;	 Catch:{ Exception -> 0x02a3 }
    L_0x02e3:
        r0.recycle();	 Catch:{ Exception -> 0x02a3 }
        goto L_0x02ea;
    L_0x02e7:
        org.telegram.messenger.FileLog.e(r0);
    L_0x02ea:
        r0 = r5.thumbs;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x031e;
    L_0x02f2:
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
        r0 = 0;
        r21 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$nqq2i9mk4k2dAnH27bdICh2H6Hg;
        r2 = r21;
        r3 = r62;
        r7 = r35;
        r4 = r59;
        r39 = r31;
        r40 = r36;
        r29 = 0;
        r42 = r7;
        r7 = r1;
        r1 = r38;
        r31 = 0;
        r8 = r0;
        r44 = r9;
        r43 = r10;
        r9 = r57;
        r45 = r11;
        r11 = r63;
        r31 = r12;
        r12 = r15;
        r15 = r13;
        r13 = r64;
        r46 = r14;
        r14 = r65;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r21);
        r0 = r16;
        r2 = r23;
        r37 = r26;
        r5 = r39;
        r33 = r40;
        r4 = r42;
        r26 = r43;
        r21 = r44;
        r25 = r45;
        r35 = r46;
        goto L_0x04ff;
    L_0x0386:
        r1 = r8;
        r44 = r9;
        r43 = r10;
        r45 = r11;
        r46 = r14;
        r39 = r31;
        r42 = r35;
        r40 = r36;
        r29 = 0;
        r31 = r12;
        r14 = r13;
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
        if (r0 != 0) goto L_0x0475;
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
        r8 = r7.length();
        r2 = (r8 > r29 ? 1 : (r8 == r29 ? 0 : -1));
        if (r2 == 0) goto L_0x03f8;
    L_0x03e7:
        r0 = r59.getSendMessagesHelper();
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
        if (r0 != 0) goto L_0x0472;
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
        r0 = r59.getSendMessagesHelper();
        r4 = r6.toString();
        r6 = 0;
        r0 = r0.generatePhotoSizes(r4, r6);
    L_0x0439:
        if (r0 != 0) goto L_0x0472;
    L_0x043b:
        r0 = new org.telegram.tgnet.TLRPC$TL_photo;
        r0.<init>();
        r4 = r59.getConnectionsManager();
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
        goto L_0x0473;
    L_0x0472:
        r12 = 0;
    L_0x0473:
        r6 = r2;
        goto L_0x0477;
    L_0x0475:
        r12 = 0;
        r6 = 1;
    L_0x0477:
        if (r0 == 0) goto L_0x04e3;
    L_0x0479:
        r8 = new java.util.HashMap;
        r8.<init>();
        r2 = r15.searchImage;
        r2 = r2.imageUrl;
        if (r2 == 0) goto L_0x0487;
    L_0x0484:
        r8.put(r3, r2);
    L_0x0487:
        if (r61 == 0) goto L_0x04bd;
    L_0x0489:
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
        if (r2 == r7) goto L_0x04af;
    L_0x04a5:
        r3 = r31 + -1;
        r9 = r44;
        if (r9 != r3) goto L_0x04ac;
    L_0x04ab:
        goto L_0x04b1;
    L_0x04ac:
        r16 = r2;
        goto L_0x04c1;
    L_0x04af:
        r9 = r44;
    L_0x04b1:
        r3 = "final";
        r4 = "1";
        r8.put(r3, r4);
        r16 = r2;
        r23 = r29;
        goto L_0x04c1;
    L_0x04bd:
        r10 = r40;
        r9 = r44;
    L_0x04c1:
        r17 = 0;
        r21 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$NzRFdrMj_c6d6hXgqa8qx2Mof-g;
        r2 = r21;
        r3 = r62;
        r4 = r59;
        r5 = r0;
        r7 = r15;
        r15 = r9;
        r9 = r17;
        r47 = r10;
        r10 = r57;
        r12 = r63;
        r49 = r13;
        r13 = r64;
        r14 = r65;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r21);
        goto L_0x04e9;
    L_0x04e3:
        r49 = r13;
        r47 = r40;
        r15 = r44;
    L_0x04e9:
        r38 = r1;
        r21 = r15;
        r0 = r16;
        r2 = r23;
        r37 = r26;
        r5 = r39;
        r4 = r42;
        r25 = r45;
        r35 = r46;
        r33 = r47;
        r26 = r49;
    L_0x04ff:
        r17 = 0;
        r32 = 0;
        goto L_0x0b08;
    L_0x0505:
        r42 = r4;
        r47 = r6;
        r1 = r8;
        r49 = r10;
        r45 = r11;
        r46 = r14;
        r39 = r31;
        r6 = 90;
        r7 = 10;
        r29 = 0;
        r14 = r9;
        r31 = r12;
        r0 = r15.isVideo;
        if (r0 == 0) goto L_0x0812;
    L_0x051f:
        if (r60 == 0) goto L_0x0523;
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
        if (r60 != 0) goto L_0x07d4;
    L_0x0530:
        if (r0 != 0) goto L_0x053a;
    L_0x0532:
        r4 = r15.path;
        r2 = r4.endsWith(r2);
        if (r2 == 0) goto L_0x07d4;
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
        r13 = r49;
        goto L_0x05bd;
    L_0x05b7:
        r10 = r2;
        r6 = r29;
        r13 = r49;
        r11 = 0;
    L_0x05bd:
        if (r13 != 0) goto L_0x05fd;
    L_0x05bf:
        r2 = r15.ttl;
        if (r2 != 0) goto L_0x05fd;
    L_0x05c3:
        r2 = r59.getMessagesStorage();
        if (r13 != 0) goto L_0x05cb;
    L_0x05c9:
        r4 = 2;
        goto L_0x05cc;
    L_0x05cb:
        r4 = 5;
    L_0x05cc:
        r2 = r2.getSentFile(r10, r4);
        if (r2 == 0) goto L_0x05fd;
    L_0x05d2:
        r4 = 0;
        r17 = r2[r4];
        r17 = (org.telegram.tgnet.TLRPC.TL_document) r17;
        r21 = r5;
        r5 = 1;
        r2 = r2[r5];
        r26 = r2;
        r26 = (java.lang.String) r26;
        r2 = r15.path;
        r35 = 0;
        r36 = r2;
        r2 = r13;
        r50 = r3;
        r3 = r17;
        r37 = r12;
        r12 = 0;
        r4 = r36;
        r51 = r21;
        r12 = 1;
        r5 = r35;
        r35 = r6;
        ensureMediaThumbExists(r2, r3, r4, r5, r6);
        r7 = r26;
        goto L_0x0609;
    L_0x05fd:
        r50 = r3;
        r51 = r5;
        r35 = r6;
        r37 = r12;
        r12 = 1;
        r7 = 0;
        r17 = 0;
    L_0x0609:
        if (r17 != 0) goto L_0x06f0;
    L_0x060b:
        r2 = r15.path;
        r3 = r35;
        r2 = createVideoThumbnail(r2, r3);
        if (r2 != 0) goto L_0x061b;
    L_0x0615:
        r2 = r15.path;
        r2 = android.media.ThumbnailUtils.createVideoThumbnail(r2, r12);
    L_0x061b:
        if (r2 == 0) goto L_0x0648;
    L_0x061d:
        if (r13 != 0) goto L_0x0631;
    L_0x061f:
        r3 = r15.ttl;
        if (r3 == 0) goto L_0x0624;
    L_0x0623:
        goto L_0x0631;
    L_0x0624:
        r3 = r2.getWidth();
        r4 = r2.getHeight();
        r5 = java.lang.Math.max(r3, r4);
        goto L_0x0633;
    L_0x0631:
        r5 = 90;
    L_0x0633:
        r3 = (float) r5;
        r4 = 90;
        if (r5 <= r4) goto L_0x063b;
    L_0x0638:
        r4 = 80;
        goto L_0x063d;
    L_0x063b:
        r4 = 55;
    L_0x063d:
        r5 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r4, r13);
        r3 = 0;
        r4 = 0;
        r6 = getKeyForPhotoSize(r5, r3, r12, r4);
        goto L_0x064b;
    L_0x0648:
        r4 = 0;
        r5 = 0;
        r6 = 0;
    L_0x064b:
        r3 = new org.telegram.tgnet.TLRPC$TL_document;
        r3.<init>();
        r12 = new byte[r4];
        r3.file_reference = r12;
        if (r5 == 0) goto L_0x0661;
    L_0x0656:
        r4 = r3.thumbs;
        r4.add(r5);
        r4 = r3.flags;
        r5 = 1;
        r4 = r4 | r5;
        r3.flags = r4;
    L_0x0661:
        r4 = "video/mp4";
        r3.mime_type = r4;
        r4 = r59.getUserConfig();
        r12 = 0;
        r4.saveConfig(r12);
        if (r13 == 0) goto L_0x0680;
    L_0x0670:
        r4 = 66;
        if (r1 < r4) goto L_0x067a;
    L_0x0674:
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r4.<init>();
        goto L_0x0688;
    L_0x067a:
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r4.<init>();
        goto L_0x0688;
    L_0x0680:
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r4.<init>();
        r5 = 1;
        r4.supports_streaming = r5;
    L_0x0688:
        r5 = r3.attributes;
        r5.add(r4);
        if (r0 == 0) goto L_0x06d7;
    L_0x068f:
        r5 = r0.needConvert();
        if (r5 == 0) goto L_0x06d7;
    L_0x0695:
        r5 = r0.muted;
        if (r5 == 0) goto L_0x06a9;
    L_0x0699:
        r5 = r15.path;
        fillVideoAttribute(r5, r4, r0);
        r5 = r4.w;
        r0.originalWidth = r5;
        r5 = r4.h;
        r0.originalHeight = r5;
        r43 = r13;
        goto L_0x06b4;
    L_0x06a9:
        r43 = r13;
        r12 = r0.estimatedDuration;
        r35 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r12 = r12 / r35;
        r5 = (int) r12;
        r4.duration = r5;
    L_0x06b4:
        r5 = r0.rotationValue;
        r9 = 90;
        if (r5 == r9) goto L_0x06c8;
    L_0x06ba:
        r9 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r5 != r9) goto L_0x06bf;
    L_0x06be:
        goto L_0x06c8;
    L_0x06bf:
        r5 = r0.resultWidth;
        r4.w = r5;
        r5 = r0.resultHeight;
        r4.h = r5;
        goto L_0x06d0;
    L_0x06c8:
        r5 = r0.resultHeight;
        r4.w = r5;
        r5 = r0.resultWidth;
        r4.h = r5;
    L_0x06d0:
        r4 = r0.estimatedSize;
        r5 = (int) r4;
        r3.size = r5;
        r12 = 0;
        goto L_0x06ec;
    L_0x06d7:
        r43 = r13;
        r5 = r9.exists();
        if (r5 == 0) goto L_0x06e6;
    L_0x06df:
        r12 = r9.length();
        r5 = (int) r12;
        r3.size = r5;
    L_0x06e6:
        r5 = r15.path;
        r12 = 0;
        fillVideoAttribute(r5, r4, r12);
    L_0x06ec:
        r9 = r3;
        r4 = r6;
        r3 = r2;
        goto L_0x06f7;
    L_0x06f0:
        r43 = r13;
        r12 = 0;
        r3 = r12;
        r4 = r3;
        r9 = r17;
    L_0x06f7:
        if (r0 == 0) goto L_0x0722;
    L_0x06f9:
        r2 = r0.muted;
        if (r2 == 0) goto L_0x0722;
    L_0x06fd:
        r2 = r9.attributes;
        r2 = r2.size();
        r5 = 0;
    L_0x0704:
        if (r5 >= r2) goto L_0x0715;
    L_0x0706:
        r6 = r9.attributes;
        r6 = r6.get(r5);
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
        if (r6 == 0) goto L_0x0712;
    L_0x0710:
        r2 = 1;
        goto L_0x0716;
    L_0x0712:
        r5 = r5 + 1;
        goto L_0x0704;
    L_0x0715:
        r2 = 0;
    L_0x0716:
        if (r2 != 0) goto L_0x0722;
    L_0x0718:
        r2 = r9.attributes;
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r5.<init>();
        r2.add(r5);
    L_0x0722:
        if (r0 == 0) goto L_0x0756;
    L_0x0724:
        r2 = r0.needConvert();
        if (r2 == 0) goto L_0x0756;
    L_0x072a:
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
        r13 = r2;
        goto L_0x0757;
    L_0x0756:
        r13 = r8;
    L_0x0757:
        r8 = new java.util.HashMap;
        r8.<init>();
        if (r10 == 0) goto L_0x0763;
    L_0x075e:
        r6 = r50;
        r8.put(r6, r10);
    L_0x0763:
        if (r7 == 0) goto L_0x076a;
    L_0x0765:
        r2 = "parentObject";
        r8.put(r2, r7);
    L_0x076a:
        if (r11 != 0) goto L_0x07a0;
    L_0x076c:
        if (r61 == 0) goto L_0x07a0;
    L_0x076e:
        r2 = r16 + 1;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r10 = r51;
        r5.append(r10);
        r10 = r47;
        r5.append(r10);
        r5 = r5.toString();
        r6 = "groupId";
        r8.put(r6, r5);
        r5 = 10;
        if (r2 == r5) goto L_0x0794;
    L_0x078c:
        r5 = r31 + -1;
        if (r14 != r5) goto L_0x0791;
    L_0x0790:
        goto L_0x0794;
    L_0x0791:
        r21 = r2;
        goto L_0x07a4;
    L_0x0794:
        r5 = "final";
        r6 = "1";
        r8.put(r5, r6);
        r21 = r2;
        r23 = r29;
        goto L_0x07a4;
    L_0x07a0:
        r10 = r47;
        r21 = r16;
    L_0x07a4:
        r25 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$HV6ayuYlgpOaqnUKa4BtW-YtCVc;
        r2 = r25;
        r16 = 1;
        r5 = r62;
        r6 = r59;
        r26 = r7;
        r7 = r0;
        r0 = r8;
        r8 = r9;
        r9 = r13;
        r52 = r10;
        r10 = r0;
        r11 = r26;
        r38 = r1;
        r16 = r12;
        r1 = r37;
        r26 = r43;
        r12 = r57;
        r54 = r14;
        r14 = r63;
        r32 = r16;
        r16 = r64;
        r17 = r65;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r25);
        goto L_0x07fc;
    L_0x07d4:
        r38 = r1;
        r54 = r14;
        r1 = r26;
        r52 = r47;
        r26 = r49;
        r32 = 0;
        r4 = r15.path;
        r5 = 0;
        r6 = 0;
        r10 = r15.caption;
        r11 = r15.entities;
        r2 = r59;
        r3 = r4;
        r7 = r57;
        r9 = r63;
        r12 = r62;
        r13 = r60;
        r14 = r64;
        r15 = r65;
        prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15);
        r21 = r16;
    L_0x07fc:
        r37 = r1;
        r0 = r21;
        r2 = r23;
        r5 = r39;
        r4 = r42;
        r25 = r45;
        r35 = r46;
    L_0x080a:
        r33 = r52;
        r21 = r54;
        r17 = 0;
        goto L_0x0b08;
    L_0x0812:
        r38 = r1;
        r6 = r3;
        r10 = r5;
        r54 = r14;
        r1 = r26;
        r52 = r47;
        r26 = r49;
        r5 = 10;
        r32 = 0;
        r0 = r15.path;
        if (r0 != 0) goto L_0x0835;
    L_0x0826:
        r2 = r15.uri;
        if (r2 == 0) goto L_0x0835;
    L_0x082a:
        r0 = org.telegram.messenger.AndroidUtilities.getPath(r2);
        r2 = r15.uri;
        r2 = r2.toString();
        goto L_0x0836;
    L_0x0835:
        r2 = r0;
    L_0x0836:
        if (r60 != 0) goto L_0x089f;
    L_0x0838:
        r3 = r15.path;
        r4 = r15.uri;
        r3 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r3, r4);
        if (r3 == 0) goto L_0x0843;
    L_0x0842:
        goto L_0x089f;
    L_0x0843:
        r14 = r46;
        if (r0 == 0) goto L_0x0864;
    L_0x0847:
        r3 = r0.endsWith(r14);
        if (r3 != 0) goto L_0x0855;
    L_0x084d:
        r3 = ".webp";
        r3 = r0.endsWith(r3);
        if (r3 == 0) goto L_0x0864;
    L_0x0855:
        r3 = r0.endsWith(r14);
        if (r3 == 0) goto L_0x085e;
    L_0x085b:
        r3 = "gif";
        goto L_0x0861;
    L_0x085e:
        r3 = "webp";
    L_0x0861:
        r22 = r3;
        goto L_0x08af;
    L_0x0864:
        if (r0 != 0) goto L_0x089c;
    L_0x0866:
        r3 = r15.uri;
        if (r3 == 0) goto L_0x089c;
    L_0x086a:
        r3 = org.telegram.messenger.MediaController.isGif(r3);
        if (r3 == 0) goto L_0x0881;
    L_0x0870:
        r0 = r15.uri;
        r2 = r0.toString();
        r0 = r15.uri;
        r3 = "gif";
        r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3);
        r22 = "gif";
        goto L_0x08af;
    L_0x0881:
        r3 = r15.uri;
        r3 = org.telegram.messenger.MediaController.isWebp(r3);
        if (r3 == 0) goto L_0x089c;
    L_0x0889:
        r0 = r15.uri;
        r2 = r0.toString();
        r0 = r15.uri;
        r3 = "webp";
        r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3);
        r22 = "webp";
        goto L_0x08af;
    L_0x089c:
        r8 = r0;
        r0 = 0;
        goto L_0x08b1;
    L_0x089f:
        r14 = r46;
        if (r0 == 0) goto L_0x08ad;
    L_0x08a3:
        r3 = new java.io.File;
        r3.<init>(r0);
        r3 = org.telegram.messenger.FileLoader.getFileExtension(r3);
        goto L_0x0861;
    L_0x08ad:
        r22 = r10;
    L_0x08af:
        r8 = r0;
        r0 = 1;
    L_0x08b1:
        if (r0 == 0) goto L_0x08f7;
    L_0x08b3:
        r13 = r39;
        if (r13 != 0) goto L_0x08d0;
    L_0x08b7:
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
        goto L_0x08d7;
    L_0x08d0:
        r5 = r13;
        r0 = r27;
        r3 = r28;
        r4 = r42;
    L_0x08d7:
        r5.add(r8);
        r4.add(r2);
        r2 = r15.caption;
        r0.add(r2);
        r2 = r15.entities;
        r3.add(r2);
        r27 = r0;
        r37 = r1;
        r28 = r3;
        r35 = r14;
        r0 = r16;
        r2 = r23;
        r25 = r45;
        goto L_0x080a;
    L_0x08f7:
        r13 = r39;
        if (r8 == 0) goto L_0x091f;
    L_0x08fb:
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
        goto L_0x0921;
    L_0x091f:
        r9 = r32;
    L_0x0921:
        r12 = r45;
        if (r12 == 0) goto L_0x0948;
    L_0x0925:
        r0 = r12.get(r15);
        r2 = r0;
        r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2;
        r0 = r2.photo;
        r3 = r2.parentObject;
        if (r0 != 0) goto L_0x0940;
    L_0x0932:
        r0 = r2.sync;	 Catch:{ Exception -> 0x0938 }
        r0.await();	 Catch:{ Exception -> 0x0938 }
        goto L_0x093c;
    L_0x0938:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x093c:
        r0 = r2.photo;
        r3 = r2.parentObject;
    L_0x0940:
        r7 = r0;
        r37 = r1;
        r11 = r6;
        r1 = 1;
        r6 = r3;
        goto L_0x09d2;
    L_0x0948:
        if (r26 != 0) goto L_0x09a9;
    L_0x094a:
        r0 = r15.ttl;
        if (r0 != 0) goto L_0x09a9;
    L_0x094e:
        r0 = r59.getMessagesStorage();
        if (r26 != 0) goto L_0x0956;
    L_0x0954:
        r2 = 0;
        goto L_0x0957;
    L_0x0956:
        r2 = 3;
    L_0x0957:
        r0 = r0.getSentFile(r9, r2);
        if (r0 == 0) goto L_0x0968;
    L_0x095d:
        r11 = 0;
        r2 = r0[r11];
        r2 = (org.telegram.tgnet.TLRPC.TL_photo) r2;
        r7 = 1;
        r0 = r0[r7];
        r0 = (java.lang.String) r0;
        goto L_0x096d;
    L_0x0968:
        r7 = 1;
        r11 = 0;
        r0 = r32;
        r2 = r0;
    L_0x096d:
        if (r2 != 0) goto L_0x0993;
    L_0x096f:
        r3 = r15.uri;
        if (r3 == 0) goto L_0x0993;
    L_0x0973:
        r3 = r59.getMessagesStorage();
        r4 = r15.uri;
        r4 = org.telegram.messenger.AndroidUtilities.getPath(r4);
        if (r26 != 0) goto L_0x0981;
    L_0x097f:
        r5 = 0;
        goto L_0x0982;
    L_0x0981:
        r5 = 3;
    L_0x0982:
        r3 = r3.getSentFile(r4, r5);
        if (r3 == 0) goto L_0x0993;
    L_0x0988:
        r0 = r3[r11];
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        r2 = r3[r7];
        r2 = (java.lang.String) r2;
        r17 = r2;
        goto L_0x0996;
    L_0x0993:
        r17 = r0;
        r0 = r2;
    L_0x0996:
        r4 = r15.path;
        r5 = r15.uri;
        r35 = 0;
        r2 = r26;
        r3 = r0;
        r37 = r1;
        r11 = r6;
        r1 = 1;
        r6 = r35;
        ensureMediaThumbExists(r2, r3, r4, r5, r6);
        goto L_0x09b1;
    L_0x09a9:
        r37 = r1;
        r11 = r6;
        r1 = 1;
        r0 = r32;
        r17 = r0;
    L_0x09b1:
        if (r0 != 0) goto L_0x09cf;
    L_0x09b3:
        r0 = r59.getSendMessagesHelper();
        r2 = r15.path;
        r3 = r15.uri;
        r0 = r0.generatePhotoSizes(r2, r3);
        if (r26 == 0) goto L_0x09cf;
    L_0x09c1:
        r2 = r15.canDeleteAfter;
        if (r2 == 0) goto L_0x09cf;
    L_0x09c5:
        r2 = new java.io.File;
        r3 = r15.path;
        r2.<init>(r3);
        r2.delete();
    L_0x09cf:
        r7 = r0;
        r6 = r17;
    L_0x09d2:
        if (r7 == 0) goto L_0x0ac3;
    L_0x09d4:
        r8 = new java.util.HashMap;
        r8.<init>();
        r3 = new android.graphics.Bitmap[r1];
        r4 = new java.lang.String[r1];
        r0 = r15.masks;
        if (r0 == 0) goto L_0x09e9;
    L_0x09e1:
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x09e9;
    L_0x09e7:
        r0 = 1;
        goto L_0x09ea;
    L_0x09e9:
        r0 = 0;
    L_0x09ea:
        r7.has_stickers = r0;
        if (r0 == 0) goto L_0x0a2d;
    L_0x09ee:
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
    L_0x0a07:
        r5 = r15.masks;
        r5 = r5.size();
        if (r2 >= r5) goto L_0x0a1d;
    L_0x0a0f:
        r5 = r15.masks;
        r5 = r5.get(r2);
        r5 = (org.telegram.tgnet.TLRPC.InputDocument) r5;
        r5.serializeToStream(r0);
        r2 = r2 + 1;
        goto L_0x0a07;
    L_0x0a1d:
        r2 = r0.toByteArray();
        r2 = org.telegram.messenger.Utilities.bytesToHex(r2);
        r5 = "masks";
        r8.put(r5, r2);
        r0.cleanup();
    L_0x0a2d:
        if (r9 == 0) goto L_0x0a32;
    L_0x0a2f:
        r8.put(r11, r9);
    L_0x0a32:
        if (r6 == 0) goto L_0x0a39;
    L_0x0a34:
        r0 = "parentObject";
        r8.put(r0, r6);
    L_0x0a39:
        if (r61 == 0) goto L_0x0a44;
    L_0x0a3b:
        r0 = r56.size();	 Catch:{ Exception -> 0x0a5a }
        if (r0 != r1) goto L_0x0a42;
    L_0x0a41:
        goto L_0x0a44;
    L_0x0a42:
        r11 = 0;
        goto L_0x0a5f;
    L_0x0a44:
        r0 = r7.sizes;	 Catch:{ Exception -> 0x0a5a }
        r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0a5a }
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2);	 Catch:{ Exception -> 0x0a5a }
        if (r0 == 0) goto L_0x0a42;
    L_0x0a50:
        r11 = 0;
        r0 = getKeyForPhotoSize(r0, r3, r11, r11);	 Catch:{ Exception -> 0x0a58 }
        r4[r11] = r0;	 Catch:{ Exception -> 0x0a58 }
        goto L_0x0a5f;
    L_0x0a58:
        r0 = move-exception;
        goto L_0x0a5c;
    L_0x0a5a:
        r0 = move-exception;
        r11 = 0;
    L_0x0a5c:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0a5f:
        if (r61 == 0) goto L_0x0a90;
    L_0x0a61:
        r0 = r16 + 1;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r10);
        r9 = r52;
        r2.append(r9);
        r2 = r2.toString();
        r5 = "groupId";
        r8.put(r5, r2);
        r2 = 10;
        if (r0 == r2) goto L_0x0a84;
    L_0x0a7d:
        r2 = r31 + -1;
        r5 = r54;
        if (r5 != r2) goto L_0x0a96;
    L_0x0a83:
        goto L_0x0a86;
    L_0x0a84:
        r5 = r54;
    L_0x0a86:
        r2 = "final";
        r1 = "1";
        r8.put(r2, r1);
        r23 = r29;
        goto L_0x0a96;
    L_0x0a90:
        r9 = r52;
        r5 = r54;
        r0 = r16;
    L_0x0a96:
        r1 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$UdYxeFJJPkyzGq2o6oz82-zZGls;
        r2 = r1;
        r21 = r5;
        r5 = r62;
        r17 = r6;
        r6 = r59;
        r33 = r9;
        r9 = r17;
        r17 = 0;
        r10 = r57;
        r25 = r12;
        r12 = r63;
        r55 = r13;
        r13 = r15;
        r35 = r14;
        r14 = r64;
        r15 = r65;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r1);
        r2 = r23;
        r4 = r42;
        r5 = r55;
        goto L_0x0b08;
    L_0x0ac3:
        r25 = r12;
        r35 = r14;
        r33 = r52;
        r21 = r54;
        r17 = 0;
        r14 = r13;
        if (r14 != 0) goto L_0x0ae9;
    L_0x0ad0:
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
        goto L_0x0af0;
    L_0x0ae9:
        r5 = r14;
        r0 = r27;
        r1 = r28;
        r4 = r42;
    L_0x0af0:
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
    L_0x0b08:
        r9 = r21 + 1;
        r1 = r56;
        r13 = r59;
        r11 = r25;
        r10 = r26;
        r12 = r31;
        r15 = r32;
        r23 = r33;
        r14 = r35;
        r26 = r37;
        r8 = r38;
        goto L_0x017e;
    L_0x0b20:
        r42 = r4;
        r14 = r5;
        r17 = 0;
        r29 = 0;
        r0 = (r2 > r29 ? 1 : (r2 == r29 ? 0 : -1));
        if (r0 == 0) goto L_0x0b38;
    L_0x0b2b:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$pKfT1w1I30bZ85vW1f-pzxx_nIk;
        r15 = r59;
        r13 = r65;
        r0.<init>(r15, r2, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        goto L_0x0b3c;
    L_0x0b38:
        r15 = r59;
        r13 = r65;
    L_0x0b3c:
        if (r66 == 0) goto L_0x0b41;
    L_0x0b3e:
        r66.releasePermission();
    L_0x0b41:
        if (r14 == 0) goto L_0x0b96;
    L_0x0b43:
        r0 = r14.isEmpty();
        if (r0 != 0) goto L_0x0b96;
    L_0x0b49:
        r0 = 0;
    L_0x0b4a:
        r1 = r14.size();
        if (r0 >= r1) goto L_0x0b96;
    L_0x0b50:
        r1 = r14.get(r0);
        r2 = r1;
        r2 = (java.lang.String) r2;
        r12 = r42;
        r1 = r12.get(r0);
        r3 = r1;
        r3 = (java.lang.String) r3;
        r4 = 0;
        r11 = r27;
        r1 = r11.get(r0);
        r9 = r1;
        r9 = (java.lang.CharSequence) r9;
        r10 = r28;
        r1 = r10.get(r0);
        r16 = r1;
        r16 = (java.util.ArrayList) r16;
        r1 = r59;
        r5 = r22;
        r6 = r57;
        r8 = r63;
        r10 = r16;
        r11 = r62;
        r16 = r12;
        r12 = r60;
        r13 = r64;
        r17 = r14;
        r14 = r65;
        prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14);
        r0 = r0 + 1;
        r13 = r65;
        r42 = r16;
        r14 = r17;
        goto L_0x0b4a;
    L_0x0b96:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0bb4;
    L_0x0b9a:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "total send time = ";
        r0.append(r1);
        r1 = java.lang.System.currentTimeMillis();
        r1 = r1 - r18;
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0bb4:
        return;
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

    static /* synthetic */ void lambda$null$67(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, null, tL_document, str, hashMap, false, str2);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, null, str, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, z, i, 0, str2);
    }

    static /* synthetic */ void lambda$null$68(MessageObject messageObject, AccountInstance accountInstance, TL_photo tL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2, boolean z2, int i) {
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

    static /* synthetic */ void lambda$null$69(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
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

    static /* synthetic */ void lambda$null$70(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TL_photo tL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
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

    static /* synthetic */ void lambda$null$71(AccountInstance accountInstance, long j, int i) {
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x0015 */
    private static android.graphics.Bitmap createVideoThumbnail(java.lang.String r2, long r3) {
        /*
        r0 = new android.media.MediaMetadataRetriever;
        r0.<init>();
        r1 = 0;
        r0.setDataSource(r2);	 Catch:{ Exception -> 0x0015, all -> 0x0019 }
        r2 = 1;
        r1 = r0.getFrameAtTime(r3, r2);	 Catch:{ Exception -> 0x0015, all -> 0x0019 }
        if (r1 != 0) goto L_0x0015;
    L_0x0010:
        r2 = 3;
        r1 = r0.getFrameAtTime(r3, r2);	 Catch:{ Exception -> 0x0015, all -> 0x0019 }
    L_0x0015:
        r0.release();	 Catch:{ RuntimeException -> 0x001e }
        goto L_0x001e;
    L_0x0019:
        r2 = move-exception;
        r0.release();	 Catch:{ RuntimeException -> 0x001d }
    L_0x001d:
        throw r2;
    L_0x001e:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.createVideoThumbnail(java.lang.String, long):android.graphics.Bitmap");
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x015c  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x015f  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0194  */
    private static org.telegram.messenger.VideoEditedInfo createCompressionSettings(java.lang.String r15) {
        /*
        r0 = "video/avc";
        r1 = 11;
        r1 = new int[r1];
        org.telegram.ui.Components.AnimatedFileDrawable.getVideoInfo(r15, r1);
        r2 = 0;
        r2 = r1[r2];
        r3 = 0;
        if (r2 != 0) goto L_0x001b;
    L_0x0010:
        r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r15 == 0) goto L_0x001a;
    L_0x0014:
        r15 = "video hasn't avc1 atom";
        org.telegram.messenger.FileLog.d(r15);
    L_0x001a:
        return r3;
    L_0x001b:
        r2 = org.telegram.messenger.MediaController.getVideoBitrate(r15);
        r4 = 4;
        r5 = r1[r4];
        r5 = (float) r5;
        r6 = 6;
        r6 = r1[r6];
        r6 = (long) r6;
        r8 = 5;
        r9 = r1[r8];
        r9 = (long) r9;
        r11 = 7;
        r11 = r1[r11];
        r12 = android.os.Build.VERSION.SDK_INT;
        r13 = 18;
        if (r12 >= r13) goto L_0x00aa;
    L_0x0034:
        r12 = org.telegram.messenger.MediaController.selectCodec(r0);	 Catch:{ Exception -> 0x00a9 }
        if (r12 != 0) goto L_0x0044;
    L_0x003a:
        r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x00a9 }
        if (r15 == 0) goto L_0x0043;
    L_0x003e:
        r15 = "no codec info for video/avc";
        org.telegram.messenger.FileLog.d(r15);	 Catch:{ Exception -> 0x00a9 }
    L_0x0043:
        return r3;
    L_0x0044:
        r13 = r12.getName();	 Catch:{ Exception -> 0x00a9 }
        r14 = "OMX.google.h264.encoder";
        r14 = r13.equals(r14);	 Catch:{ Exception -> 0x00a9 }
        if (r14 != 0) goto L_0x0091;
    L_0x0050:
        r14 = "OMX.ST.VFM.H264Enc";
        r14 = r13.equals(r14);	 Catch:{ Exception -> 0x00a9 }
        if (r14 != 0) goto L_0x0091;
    L_0x0058:
        r14 = "OMX.Exynos.avc.enc";
        r14 = r13.equals(r14);	 Catch:{ Exception -> 0x00a9 }
        if (r14 != 0) goto L_0x0091;
    L_0x0060:
        r14 = "OMX.MARVELL.VIDEO.HW.CODA7542ENCODER";
        r14 = r13.equals(r14);	 Catch:{ Exception -> 0x00a9 }
        if (r14 != 0) goto L_0x0091;
    L_0x0068:
        r14 = "OMX.MARVELL.VIDEO.H264ENCODER";
        r14 = r13.equals(r14);	 Catch:{ Exception -> 0x00a9 }
        if (r14 != 0) goto L_0x0091;
    L_0x0070:
        r14 = "OMX.k3.video.encoder.avc";
        r14 = r13.equals(r14);	 Catch:{ Exception -> 0x00a9 }
        if (r14 != 0) goto L_0x0091;
    L_0x0078:
        r14 = "OMX.TI.DUCATI1.VIDEO.H264E";
        r14 = r13.equals(r14);	 Catch:{ Exception -> 0x00a9 }
        if (r14 == 0) goto L_0x0081;
    L_0x0080:
        goto L_0x0091;
    L_0x0081:
        r0 = org.telegram.messenger.MediaController.selectColorFormat(r12, r0);	 Catch:{ Exception -> 0x00a9 }
        if (r0 != 0) goto L_0x00aa;
    L_0x0087:
        r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x00a9 }
        if (r15 == 0) goto L_0x0090;
    L_0x008b:
        r15 = "no color format for video/avc";
        org.telegram.messenger.FileLog.d(r15);	 Catch:{ Exception -> 0x00a9 }
    L_0x0090:
        return r3;
    L_0x0091:
        r15 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x00a9 }
        if (r15 == 0) goto L_0x00a9;
    L_0x0095:
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00a9 }
        r15.<init>();	 Catch:{ Exception -> 0x00a9 }
        r0 = "unsupported encoder = ";
        r15.append(r0);	 Catch:{ Exception -> 0x00a9 }
        r15.append(r13);	 Catch:{ Exception -> 0x00a9 }
        r15 = r15.toString();	 Catch:{ Exception -> 0x00a9 }
        org.telegram.messenger.FileLog.d(r15);	 Catch:{ Exception -> 0x00a9 }
    L_0x00a9:
        return r3;
    L_0x00aa:
        r0 = new org.telegram.messenger.VideoEditedInfo;
        r0.<init>();
        r12 = -1;
        r0.startTime = r12;
        r0.endTime = r12;
        r0.bitrate = r2;
        r0.originalPath = r15;
        r0.framerate = r11;
        r11 = (double) r5;
        r11 = java.lang.Math.ceil(r11);
        r11 = (long) r11;
        r0.estimatedDuration = r11;
        r3 = 1;
        r11 = r1[r3];
        r0.originalWidth = r11;
        r0.resultWidth = r11;
        r11 = 2;
        r12 = r1[r11];
        r0.originalHeight = r12;
        r0.resultHeight = r12;
        r12 = 8;
        r1 = r1[r12];
        r0.rotationValue = r1;
        r1 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r5 = r5 * r1;
        r12 = (long) r5;
        r0.originalDuration = r12;
        r1 = org.telegram.messenger.MessagesController.getGlobalMainSettings();
        r5 = "compress_video2";
        r1 = r1.getInt(r5, r3);
        r5 = r0.originalWidth;
        r12 = 1280; // 0x500 float:1.794E-42 double:6.324E-321;
        if (r5 > r12) goto L_0x010e;
    L_0x00ee:
        r13 = r0.originalHeight;
        if (r13 <= r12) goto L_0x00f3;
    L_0x00f2:
        goto L_0x010e;
    L_0x00f3:
        r8 = 848; // 0x350 float:1.188E-42 double:4.19E-321;
        if (r5 > r8) goto L_0x010f;
    L_0x00f7:
        if (r13 <= r8) goto L_0x00fa;
    L_0x00f9:
        goto L_0x010f;
    L_0x00fa:
        r4 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
        if (r5 > r4) goto L_0x010c;
    L_0x00fe:
        if (r13 <= r4) goto L_0x0101;
    L_0x0100:
        goto L_0x010c;
    L_0x0101:
        r4 = 480; // 0x1e0 float:6.73E-43 double:2.37E-321;
        if (r5 > r4) goto L_0x010a;
    L_0x0105:
        if (r13 <= r4) goto L_0x0108;
    L_0x0107:
        goto L_0x010a;
    L_0x0108:
        r4 = 1;
        goto L_0x010f;
    L_0x010a:
        r4 = 2;
        goto L_0x010f;
    L_0x010c:
        r4 = 3;
        goto L_0x010f;
    L_0x010e:
        r4 = 5;
    L_0x010f:
        if (r1 < r4) goto L_0x0113;
    L_0x0111:
        r1 = r4 + -1;
    L_0x0113:
        r4 = r4 - r3;
        if (r1 == r4) goto L_0x015c;
    L_0x0116:
        if (r1 == 0) goto L_0x0125;
    L_0x0118:
        if (r1 == r3) goto L_0x0122;
    L_0x011a:
        if (r1 == r11) goto L_0x011f;
    L_0x011c:
        r3 = NUM; // 0x44a00000 float:1280.0 double:5.68835786E-315;
        goto L_0x0127;
    L_0x011f:
        r3 = NUM; // 0x44540000 float:848.0 double:5.66374975E-315;
        goto L_0x0127;
    L_0x0122:
        r3 = NUM; // 0x44200000 float:640.0 double:5.646912627E-315;
        goto L_0x0127;
    L_0x0125:
        r3 = NUM; // 0x43d80000 float:432.0 double:5.623599685E-315;
    L_0x0127:
        r5 = r0.originalWidth;
        r8 = r0.originalHeight;
        if (r5 <= r8) goto L_0x012f;
    L_0x012d:
        r5 = (float) r5;
        goto L_0x0130;
    L_0x012f:
        r5 = (float) r8;
    L_0x0130:
        r3 = r3 / r5;
        r5 = r0.originalWidth;
        r5 = (float) r5;
        r5 = r5 * r3;
        r8 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = r5 / r8;
        r5 = java.lang.Math.round(r5);
        r5 = r5 * 2;
        r0.resultWidth = r5;
        r5 = r0.originalHeight;
        r5 = (float) r5;
        r5 = r5 * r3;
        r5 = r5 / r8;
        r3 = java.lang.Math.round(r5);
        r3 = r3 * 2;
        r0.resultHeight = r3;
        r3 = r0.originalHeight;
        r5 = r0.originalWidth;
        r8 = r0.resultHeight;
        r11 = r0.resultWidth;
        r3 = org.telegram.messenger.MediaController.makeVideoBitrate(r3, r5, r2, r8, r11);
        goto L_0x015d;
    L_0x015c:
        r3 = r2;
    L_0x015d:
        if (r1 != r4) goto L_0x0177;
    L_0x015f:
        r1 = r0.originalWidth;
        r0.resultWidth = r1;
        r1 = r0.originalHeight;
        r0.resultHeight = r1;
        r0.bitrate = r2;
        r1 = new java.io.File;
        r1.<init>(r15);
        r1 = r1.length();
        r15 = (int) r1;
        r1 = (long) r15;
        r0.estimatedSize = r1;
        goto L_0x018c;
    L_0x0177:
        r0.bitrate = r3;
        r9 = r9 + r6;
        r15 = (int) r9;
        r1 = (long) r15;
        r0.estimatedSize = r1;
        r1 = r0.estimatedSize;
        r3 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r3 = r1 / r3;
        r5 = 16;
        r3 = r3 * r5;
        r1 = r1 + r3;
        r0.estimatedSize = r1;
    L_0x018c:
        r1 = r0.estimatedSize;
        r3 = 0;
        r15 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r15 != 0) goto L_0x0198;
    L_0x0194:
        r1 = 1;
        r0.estimatedSize = r1;
    L_0x0198:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.createCompressionSettings(java.lang.String):org.telegram.messenger.VideoEditedInfo");
    }

    public static void prepareSendingVideo(AccountInstance accountInstance, String str, long j, long j2, int i, int i2, VideoEditedInfo videoEditedInfo, long j3, MessageObject messageObject, CharSequence charSequence, ArrayList<MessageEntity> arrayList, int i3, MessageObject messageObject2, boolean z, int i4) {
        if (str != null && str.length() != 0) {
            -$$Lambda$SendMessagesHelper$VHKs7qA0gndu87-lOdAqsXp0B48 -__lambda_sendmessageshelper_vhks7qa0gndu87-lodaqsxp0b48 = r0;
            -$$Lambda$SendMessagesHelper$VHKs7qA0gndu87-lOdAqsXp0B48 -__lambda_sendmessageshelper_vhks7qa0gndu87-lodaqsxp0b482 = new -$$Lambda$SendMessagesHelper$VHKs7qA0gndu87-lOdAqsXp0B48(videoEditedInfo, str, j3, j2, i3, accountInstance, i2, i, j, charSequence, messageObject2, messageObject, arrayList, z, i4);
            new Thread(-__lambda_sendmessageshelper_vhks7qa0gndu87-lodaqsxp0b48).start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:80:0x022c  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0274  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x024a  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02d0  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0320  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0328  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0331  */
    static /* synthetic */ void lambda$prepareSendingVideo$74(org.telegram.messenger.VideoEditedInfo r32, java.lang.String r33, long r34, long r36, int r38, org.telegram.messenger.AccountInstance r39, int r40, int r41, long r42, java.lang.CharSequence r44, org.telegram.messenger.MessageObject r45, org.telegram.messenger.MessageObject r46, java.util.ArrayList r47, boolean r48, int r49) {
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
        goto L_0x0359;
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
        if (r4 != 0) goto L_0x0120;
    L_0x00d9:
        if (r38 != 0) goto L_0x0120;
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
        if (r0 == 0) goto L_0x0117;
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
        r5 = r20;
        goto L_0x012c;
    L_0x0117:
        r15 = r2;
        r9 = r5;
        r23 = r14;
        r24 = r21;
        r22 = r3;
        goto L_0x0128;
    L_0x0120:
        r24 = r0;
        r15 = r2;
        r22 = r3;
        r9 = r5;
        r23 = r14;
    L_0x0128:
        r14 = r4;
        r5 = 0;
        r21 = 0;
    L_0x012c:
        if (r21 != 0) goto L_0x02e0;
    L_0x012e:
        r0 = r24;
        r0 = createVideoThumbnail(r6, r0);
        if (r0 != 0) goto L_0x013b;
    L_0x0136:
        r1 = 1;
        r0 = android.media.ThumbnailUtils.createVideoThumbnail(r6, r1);
    L_0x013b:
        r1 = 90;
        if (r14 != 0) goto L_0x0145;
    L_0x013f:
        if (r38 == 0) goto L_0x0142;
    L_0x0141:
        goto L_0x0145;
    L_0x0142:
        r2 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x0147;
    L_0x0145:
        r2 = 90;
    L_0x0147:
        r3 = (float) r2;
        if (r2 <= r1) goto L_0x014d;
    L_0x014a:
        r2 = 80;
        goto L_0x014f;
    L_0x014d:
        r2 = 55;
    L_0x014f:
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r14);
        if (r0 == 0) goto L_0x0223;
    L_0x0155:
        if (r2 == 0) goto L_0x0223;
    L_0x0157:
        if (r9 == 0) goto L_0x0220;
    L_0x0159:
        r3 = 21;
        if (r14 == 0) goto L_0x01c0;
    L_0x015d:
        r25 = 7;
        r4 = android.os.Build.VERSION.SDK_INT;
        if (r4 >= r3) goto L_0x0166;
    L_0x0163:
        r26 = 0;
        goto L_0x0168;
    L_0x0166:
        r26 = 1;
    L_0x0168:
        r27 = r0.getWidth();
        r28 = r0.getHeight();
        r29 = r0.getRowBytes();
        r24 = r0;
        org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = r2.location;
        r21 = r2;
        r1 = r4.volume_id;
        r3.append(r1);
        r3.append(r15);
        r1 = r21;
        r2 = r1.location;
        r2 = r2.local_id;
        r3.append(r2);
        r2 = "@%d_%d_b2";
        r3.append(r2);
        r2 = r3.toString();
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r4 = (float) r4;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r15;
        r4 = (int) r4;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = 0;
        r3[r15] = r4;
        r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r4 = (float) r4;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r15;
        r4 = (int) r4;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = 1;
        r3[r15] = r4;
        r15 = java.lang.String.format(r2, r3);
        goto L_0x0225;
    L_0x01c0:
        r1 = r2;
        r25 = 3;
        r2 = android.os.Build.VERSION.SDK_INT;
        if (r2 >= r3) goto L_0x01ca;
    L_0x01c7:
        r26 = 0;
        goto L_0x01cc;
    L_0x01ca:
        r26 = 1;
    L_0x01cc:
        r27 = r0.getWidth();
        r28 = r0.getHeight();
        r29 = r0.getRowBytes();
        r24 = r0;
        org.telegram.messenger.Utilities.blurBitmap(r24, r25, r26, r27, r28, r29);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = r1.location;
        r3 = r3.volume_id;
        r2.append(r3);
        r2.append(r15);
        r3 = r1.location;
        r3 = r3.local_id;
        r2.append(r3);
        r3 = "@%d_%d_b";
        r2.append(r3);
        r2 = r2.toString();
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r4 = (float) r4;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r15;
        r4 = (int) r4;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = 0;
        r3[r15] = r4;
        r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r4 = (float) r4;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r4 = r4 / r15;
        r4 = (int) r4;
        r4 = java.lang.Integer.valueOf(r4);
        r15 = 1;
        r3[r15] = r4;
        r15 = java.lang.String.format(r2, r3);
        goto L_0x0225;
    L_0x0220:
        r1 = r2;
        r0 = 0;
        goto L_0x0224;
    L_0x0223:
        r1 = r2;
    L_0x0224:
        r15 = 0;
    L_0x0225:
        r2 = new org.telegram.tgnet.TLRPC$TL_document;
        r2.<init>();
        if (r1 == 0) goto L_0x0237;
    L_0x022c:
        r3 = r2.thumbs;
        r3.add(r1);
        r1 = r2.flags;
        r3 = 1;
        r1 = r1 | r3;
        r2.flags = r1;
    L_0x0237:
        r1 = 0;
        r3 = new byte[r1];
        r2.file_reference = r3;
        r3 = "video/mp4";
        r2.mime_type = r3;
        r3 = r39.getUserConfig();
        r3.saveConfig(r1);
        if (r14 == 0) goto L_0x0274;
    L_0x024a:
        r1 = 32;
        r3 = r10 >> r1;
        r1 = (int) r3;
        r3 = r39.getMessagesController();
        r1 = java.lang.Integer.valueOf(r1);
        r1 = r3.getEncryptedChat(r1);
        if (r1 != 0) goto L_0x025e;
    L_0x025d:
        return;
    L_0x025e:
        r1 = r1.layer;
        r1 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r1);
        r3 = 66;
        if (r1 < r3) goto L_0x026e;
    L_0x0268:
        r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r1.<init>();
        goto L_0x027c;
    L_0x026e:
        r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r1.<init>();
        goto L_0x027c;
    L_0x0274:
        r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r1.<init>();
        r3 = 1;
        r1.supports_streaming = r3;
    L_0x027c:
        r1.round_message = r9;
        r3 = r2.attributes;
        r3.add(r1);
        if (r13 == 0) goto L_0x02ca;
    L_0x0285:
        r3 = r13.needConvert();
        if (r3 == 0) goto L_0x02ca;
    L_0x028b:
        r3 = r13.muted;
        if (r3 == 0) goto L_0x02a5;
    L_0x028f:
        r3 = r2.attributes;
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r4.<init>();
        r3.add(r4);
        fillVideoAttribute(r6, r1, r13);
        r3 = r1.w;
        r13.originalWidth = r3;
        r3 = r1.h;
        r13.originalHeight = r3;
        goto L_0x02ac;
    L_0x02a5:
        r3 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = r7 / r3;
        r4 = (int) r3;
        r1.duration = r4;
    L_0x02ac:
        r3 = r13.rotationValue;
        r4 = 90;
        if (r3 == r4) goto L_0x02be;
    L_0x02b2:
        r4 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r3 != r4) goto L_0x02b7;
    L_0x02b6:
        goto L_0x02be;
    L_0x02b7:
        r1.w = r12;
        r3 = r40;
        r1.h = r3;
        goto L_0x02c4;
    L_0x02be:
        r3 = r40;
        r1.w = r3;
        r1.h = r12;
    L_0x02c4:
        r3 = r42;
        r1 = (int) r3;
        r2.size = r1;
        goto L_0x02db;
    L_0x02ca:
        r3 = r22.exists();
        if (r3 == 0) goto L_0x02d7;
    L_0x02d0:
        r3 = r22.length();
        r4 = (int) r3;
        r2.size = r4;
    L_0x02d7:
        r3 = 0;
        fillVideoAttribute(r6, r1, r3);
    L_0x02db:
        r1 = r0;
        r21 = r2;
        r2 = r15;
        goto L_0x02e3;
    L_0x02e0:
        r3 = 0;
        r1 = r3;
        r2 = r1;
    L_0x02e3:
        if (r13 == 0) goto L_0x0318;
    L_0x02e5:
        r0 = r13.needConvert();
        if (r0 == 0) goto L_0x0318;
    L_0x02eb:
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
        goto L_0x0319;
    L_0x0318:
        r7 = r6;
    L_0x0319:
        r8 = new java.util.HashMap;
        r8.<init>();
        if (r44 == 0) goto L_0x0326;
    L_0x0320:
        r0 = r44.toString();
        r19 = r0;
    L_0x0326:
        if (r23 == 0) goto L_0x032f;
    L_0x0328:
        r0 = "originalPath";
        r3 = r23;
        r8.put(r0, r3);
    L_0x032f:
        if (r5 == 0) goto L_0x0336;
    L_0x0331:
        r0 = "parentObject";
        r8.put(r0, r5);
    L_0x0336:
        r18 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$sEG1r9eCF9TKK2nk0jbM2n33M88;
        r0 = r18;
        r3 = r45;
        r4 = r39;
        r20 = r5;
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
    L_0x0359:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$74(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, long, int, org.telegram.messenger.AccountInstance, int, int, long, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, int):void");
    }

    static /* synthetic */ void lambda$null$73(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, String str4, ArrayList arrayList, boolean z, int i, int i2) {
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
