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
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineMessage;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
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
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia;
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
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
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
    private ChatFull currentChatInfo = null;
    private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap();
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
    private SparseArray<MessageObject> unsentMessages = new SparseArray();
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

        public void addDelayedRequest(TLObject tLObject, MessageObject messageObject, String str, Object obj, DelayedMessage delayedMessage) {
            DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = new DelayedMessageSendAfterRequest();
            delayedMessageSendAfterRequest.request = tLObject;
            delayedMessageSendAfterRequest.msgObj = messageObject;
            delayedMessageSendAfterRequest.originalPath = str;
            delayedMessageSendAfterRequest.delayedMessage = delayedMessage;
            delayedMessageSendAfterRequest.parentObject = obj;
            if (this.requests == null) {
                this.requests = new ArrayList();
            }
            this.requests.add(delayedMessageSendAfterRequest);
        }

        public void addDelayedRequest(TLObject tLObject, ArrayList<MessageObject> arrayList, ArrayList<String> arrayList2, ArrayList<Object> arrayList3, DelayedMessage delayedMessage) {
            DelayedMessageSendAfterRequest delayedMessageSendAfterRequest = new DelayedMessageSendAfterRequest();
            delayedMessageSendAfterRequest.request = tLObject;
            delayedMessageSendAfterRequest.msgObjs = arrayList;
            delayedMessageSendAfterRequest.originalPaths = arrayList2;
            delayedMessageSendAfterRequest.delayedMessage = delayedMessage;
            delayedMessageSendAfterRequest.parentObjects = arrayList3;
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
                            SendMessagesHelper.this.performSendMessageRequestMulti((TL_messages_sendMultiMedia) tLObject, delayedMessageSendAfterRequest.msgObjs, delayedMessageSendAfterRequest.originalPaths, delayedMessageSendAfterRequest.parentObjects, delayedMessageSendAfterRequest.delayedMessage);
                        } else {
                            SendMessagesHelper.this.performSendMessageRequest(tLObject, delayedMessageSendAfterRequest.msgObj, delayedMessageSendAfterRequest.originalPath, delayedMessageSendAfterRequest.delayedMessage, delayedMessageSendAfterRequest.parentObject);
                        }
                    }
                    this.requests = null;
                }
            }
        }

        public void markAsError() {
            if (this.type == 4) {
                for (int i = 0; i < this.messageObjects.size(); i++) {
                    MessageObject messageObject = (MessageObject) this.messageObjects.get(i);
                    SendMessagesHelper.this.getMessagesStorage().markMessageAsSendError(messageObject.messageOwner);
                    messageObject.messageOwner.send_state = 2;
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                    SendMessagesHelper.this.processSentMessage(messageObject.getId());
                }
                HashMap access$800 = SendMessagesHelper.this.delayedMessages;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("group_");
                stringBuilder.append(this.groupId);
                access$800.remove(stringBuilder.toString());
            } else {
                SendMessagesHelper.this.getMessagesStorage().markMessageAsSendError(this.obj.messageOwner);
                this.obj.messageOwner.send_state = 2;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(this.obj.getId()));
                SendMessagesHelper.this.processSentMessage(this.obj.getId());
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
        public boolean isVideo;
        public ArrayList<InputDocument> masks;
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
        getNotificationCenter().addObserver(this, NotificationCenter.fileDidFailedLoad);
    }

    public void cleanup() {
        this.delayedMessages.clear();
        this.unsentMessages.clear();
        this.sendingMessages.clear();
        this.waitingForLocation.clear();
        this.waitingForCallback.clear();
        this.waitingForVote.clear();
        this.currentChatInfo = null;
        this.locationProvider.stop();
    }

    public void setCurrentChatInfo(ChatFull chatFull) {
        this.currentChatInfo = chatFull;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = i;
        String str = "_t";
        int i4 = 2;
        int i5 = 0;
        DelayedMessage delayedMessage;
        String str2;
        int indexOf;
        StringBuilder stringBuilder;
        String str3;
        ArrayList arrayList;
        MessageObject messageObject;
        ArrayList arrayList2;
        int indexOf2;
        MessageObject messageObject2;
        if (i3 == NotificationCenter.FileDidUpload) {
            String str4 = (String) objArr[0];
            InputFile inputFile = (InputFile) objArr[1];
            InputEncryptedFile inputEncryptedFile = (InputEncryptedFile) objArr[2];
            ArrayList arrayList3 = (ArrayList) this.delayedMessages.get(str4);
            if (arrayList3 != null) {
                while (i5 < arrayList3.size()) {
                    ArrayList arrayList4;
                    InputFile inputFile2;
                    InputEncryptedFile inputEncryptedFile2;
                    delayedMessage = (DelayedMessage) arrayList3.get(i5);
                    TLObject tLObject = delayedMessage.sendRequest;
                    InputMedia inputMedia = tLObject instanceof TL_messages_sendMedia ? ((TL_messages_sendMedia) tLObject).media : tLObject instanceof TL_messages_editMessage ? ((TL_messages_editMessage) tLObject).media : tLObject instanceof TL_messages_sendBroadcast ? ((TL_messages_sendBroadcast) tLObject).media : tLObject instanceof TL_messages_sendMultiMedia ? (InputMedia) delayedMessage.extraHashMap.get(str4) : null;
                    str2 = "_i";
                    HashMap hashMap;
                    if (inputFile == null || inputMedia == null) {
                        arrayList4 = arrayList3;
                        inputFile2 = inputFile;
                        inputEncryptedFile2 = inputEncryptedFile;
                        if (inputEncryptedFile2 != null) {
                            TLObject tLObject2 = delayedMessage.sendEncryptedRequest;
                            if (tLObject2 != null) {
                                MessageObject messageObject3;
                                DecryptedMessage decryptedMessage;
                                if (delayedMessage.type == 4) {
                                    TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TL_messages_sendEncryptedMultiMedia) tLObject2;
                                    inputEncryptedFile = (InputEncryptedFile) delayedMessage.extraHashMap.get(str4);
                                    indexOf = tL_messages_sendEncryptedMultiMedia.files.indexOf(inputEncryptedFile);
                                    if (indexOf >= 0) {
                                        tL_messages_sendEncryptedMultiMedia.files.set(indexOf, inputEncryptedFile2);
                                        if (inputEncryptedFile.id == 1) {
                                            HashMap hashMap2 = delayedMessage.extraHashMap;
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append(str4);
                                            stringBuilder2.append(str2);
                                            messageObject3 = (MessageObject) hashMap2.get(stringBuilder2.toString());
                                            hashMap = delayedMessage.extraHashMap;
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(str4);
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
                                        uploadMultiMedia(delayedMessage, null, inputEncryptedFile2, str4);
                                    } else {
                                        SecretChatHelper secretChatHelper = getSecretChatHelper();
                                        messageObject3 = delayedMessage.obj;
                                        secretChatHelper.performSendEncryptedRequest(decryptedMessage, messageObject3.messageOwner, delayedMessage.encryptedChat, inputEncryptedFile2, delayedMessage.originalPath, messageObject3);
                                    }
                                }
                                arrayList4.remove(i5);
                                i5--;
                            }
                        }
                    } else {
                        ArrayList arrayList5;
                        InputEncryptedFile inputEncryptedFile3;
                        int i6;
                        int i7 = delayedMessage.type;
                        if (i7 == 0) {
                            inputMedia.file = inputFile;
                            arrayList5 = arrayList3;
                            inputEncryptedFile3 = inputEncryptedFile;
                            i6 = i5;
                            inputFile2 = inputFile;
                            performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, delayedMessage, true, null, delayedMessage.parentObject);
                        } else {
                            DelayedMessage delayedMessage2 = delayedMessage;
                            arrayList5 = arrayList3;
                            inputEncryptedFile3 = inputEncryptedFile;
                            i6 = i5;
                            inputFile2 = inputFile;
                            PhotoSize photoSize;
                            if (i7 != 1) {
                                delayedMessage = delayedMessage2;
                                if (i7 == i4) {
                                    if (inputMedia.file == null) {
                                        inputMedia.file = inputFile2;
                                        if (inputMedia.thumb == null) {
                                            photoSize = delayedMessage.photoSize;
                                            if (!(photoSize == null || photoSize.location == null)) {
                                                performSendDelayedMessage(delayedMessage);
                                            }
                                        }
                                        performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject);
                                    } else {
                                        inputMedia.thumb = inputFile2;
                                        inputMedia.flags |= 4;
                                        performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject);
                                    }
                                } else if (i7 == 3) {
                                    inputMedia.file = inputFile2;
                                    performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject);
                                } else if (i7 == 4) {
                                    if (!(inputMedia instanceof TL_inputMediaUploadedDocument)) {
                                        inputMedia.file = inputFile2;
                                        uploadMultiMedia(delayedMessage, inputMedia, null, str4);
                                    } else if (inputMedia.file == null) {
                                        inputMedia.file = inputFile2;
                                        HashMap hashMap3 = delayedMessage.extraHashMap;
                                        StringBuilder stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(str4);
                                        stringBuilder3.append(str2);
                                        int indexOf3 = delayedMessage.messageObjects.indexOf((MessageObject) hashMap3.get(stringBuilder3.toString()));
                                        hashMap3 = delayedMessage.extraHashMap;
                                        stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(str4);
                                        stringBuilder3.append(str);
                                        delayedMessage.photoSize = (PhotoSize) hashMap3.get(stringBuilder3.toString());
                                        stopVideoService(((MessageObject) delayedMessage.messageObjects.get(indexOf3)).messageOwner.attachPath);
                                        if (inputMedia.thumb != null || delayedMessage.photoSize == null) {
                                            uploadMultiMedia(delayedMessage, inputMedia, null, str4);
                                        } else {
                                            delayedMessage.performMediaUpload = true;
                                            performSendDelayedMessage(delayedMessage, indexOf3);
                                        }
                                    } else {
                                        inputMedia.thumb = inputFile2;
                                        inputMedia.flags |= 4;
                                        hashMap = delayedMessage.extraHashMap;
                                        StringBuilder stringBuilder4 = new StringBuilder();
                                        stringBuilder4.append(str4);
                                        stringBuilder4.append("_o");
                                        uploadMultiMedia(delayedMessage, inputMedia, null, (String) hashMap.get(stringBuilder4.toString()));
                                    }
                                }
                            } else if (inputMedia.file == null) {
                                inputMedia.file = inputFile2;
                                if (inputMedia.thumb == null) {
                                    delayedMessage = delayedMessage2;
                                    photoSize = delayedMessage.photoSize;
                                    if (!(photoSize == null || photoSize.location == null)) {
                                        performSendDelayedMessage(delayedMessage);
                                    }
                                } else {
                                    delayedMessage = delayedMessage2;
                                }
                                performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject);
                            } else {
                                delayedMessage = delayedMessage2;
                                inputMedia.thumb = inputFile2;
                                inputMedia.flags |= 4;
                                performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, null, delayedMessage.parentObject);
                            }
                        }
                        arrayList4 = arrayList5;
                        i5 = i6;
                        arrayList4.remove(i5);
                        i5--;
                        inputEncryptedFile2 = inputEncryptedFile3;
                    }
                    i5++;
                    arrayList3 = arrayList4;
                    inputEncryptedFile = inputEncryptedFile2;
                    inputFile = inputFile2;
                    i4 = 2;
                }
                if (arrayList3.isEmpty()) {
                    this.delayedMessages.remove(str4);
                }
            }
        } else if (i3 == NotificationCenter.FileDidFailUpload) {
            str3 = (String) objArr[0];
            boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
            arrayList = (ArrayList) this.delayedMessages.get(str3);
            if (arrayList != null) {
                while (i5 < arrayList.size()) {
                    delayedMessage = (DelayedMessage) arrayList.get(i5);
                    if ((booleanValue && delayedMessage.sendEncryptedRequest != null) || !(booleanValue || delayedMessage.sendRequest == null)) {
                        delayedMessage.markAsError();
                        arrayList.remove(i5);
                        i5--;
                    }
                    i5++;
                }
                if (arrayList.isEmpty()) {
                    this.delayedMessages.remove(str3);
                }
            }
        } else if (i3 == NotificationCenter.filePreparingStarted) {
            messageObject = (MessageObject) objArr[0];
            if (messageObject.getId() != 0) {
                str2 = (String) objArr[1];
                arrayList2 = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                if (arrayList2 != null) {
                    while (i5 < arrayList2.size()) {
                        DelayedMessage delayedMessage3 = (DelayedMessage) arrayList2.get(i5);
                        if (delayedMessage3.type == 4) {
                            indexOf2 = delayedMessage3.messageObjects.indexOf(messageObject);
                            HashMap hashMap4 = delayedMessage3.extraHashMap;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(messageObject.messageOwner.attachPath);
                            stringBuilder.append(str);
                            delayedMessage3.photoSize = (PhotoSize) hashMap4.get(stringBuilder.toString());
                            delayedMessage3.performMediaUpload = true;
                            performSendDelayedMessage(delayedMessage3, indexOf2);
                            arrayList2.remove(i5);
                            break;
                        } else if (delayedMessage3.obj == messageObject) {
                            delayedMessage3.videoEditedInfo = null;
                            performSendDelayedMessage(delayedMessage3);
                            arrayList2.remove(i5);
                            break;
                        } else {
                            i5++;
                        }
                    }
                    if (arrayList2.isEmpty()) {
                        this.delayedMessages.remove(messageObject.messageOwner.attachPath);
                    }
                }
            }
        } else if (i3 == NotificationCenter.fileNewChunkAvailable) {
            messageObject = (MessageObject) objArr[0];
            if (messageObject.getId() != 0) {
                String str5 = (String) objArr[1];
                long longValue = ((Long) objArr[2]).longValue();
                long longValue2 = ((Long) objArr[3]).longValue();
                getFileLoader().checkUploadNewDataAvailable(str5, ((int) messageObject.getDialogId()) == 0, longValue, longValue2);
                if (longValue2 != 0) {
                    stopVideoService(messageObject.messageOwner.attachPath);
                    ArrayList arrayList6 = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                    if (arrayList6 != null) {
                        for (int i8 = 0; i8 < arrayList6.size(); i8++) {
                            DelayedMessage delayedMessage4 = (DelayedMessage) arrayList6.get(i8);
                            str = "ve";
                            if (delayedMessage4.type == 4) {
                                for (indexOf = 0; indexOf < delayedMessage4.messageObjects.size(); indexOf++) {
                                    messageObject2 = (MessageObject) delayedMessage4.messageObjects.get(indexOf);
                                    if (messageObject2 == messageObject) {
                                        messageObject2.videoEditedInfo = null;
                                        messageObject2.messageOwner.params.remove(str);
                                        messageObject2.messageOwner.media.document.size = (int) longValue2;
                                        ArrayList arrayList7 = new ArrayList();
                                        arrayList7.add(messageObject2.messageOwner);
                                        getMessagesStorage().putMessages(arrayList7, false, true, false, 0);
                                        break;
                                    }
                                }
                            } else {
                                MessageObject messageObject4 = delayedMessage4.obj;
                                if (messageObject4 == messageObject) {
                                    messageObject4.videoEditedInfo = null;
                                    messageObject4.messageOwner.params.remove(str);
                                    delayedMessage4.obj.messageOwner.media.document.size = (int) longValue2;
                                    ArrayList arrayList8 = new ArrayList();
                                    arrayList8.add(delayedMessage4.obj.messageOwner);
                                    getMessagesStorage().putMessages(arrayList8, false, true, false, 0);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            messageObject2 = null;
            DelayedMessage delayedMessage5;
            if (i3 == NotificationCenter.filePreparingFailed) {
                messageObject = (MessageObject) objArr[0];
                if (messageObject.getId() != 0) {
                    str2 = (String) objArr[1];
                    stopVideoService(messageObject.messageOwner.attachPath);
                    arrayList = (ArrayList) this.delayedMessages.get(str2);
                    if (arrayList != null) {
                        indexOf2 = 0;
                        while (indexOf2 < arrayList.size()) {
                            delayedMessage5 = (DelayedMessage) arrayList.get(indexOf2);
                            if (delayedMessage5.type == 4) {
                                int i9 = 0;
                                while (i9 < delayedMessage5.messages.size()) {
                                    if (delayedMessage5.messageObjects.get(i9) == messageObject) {
                                        delayedMessage5.markAsError();
                                        arrayList.remove(indexOf2);
                                    } else {
                                        i9++;
                                    }
                                }
                                indexOf2++;
                            } else if (delayedMessage5.obj == messageObject) {
                                delayedMessage5.markAsError();
                                arrayList.remove(indexOf2);
                            } else {
                                indexOf2++;
                            }
                            indexOf2--;
                            indexOf2++;
                        }
                        if (arrayList.isEmpty()) {
                            this.delayedMessages.remove(str2);
                        }
                    }
                }
            } else if (i3 == NotificationCenter.httpFileDidLoad) {
                String str6 = (String) objArr[0];
                ArrayList arrayList9 = (ArrayList) this.delayedMessages.get(str6);
                if (arrayList9 != null) {
                    i4 = 0;
                    while (i4 < arrayList9.size()) {
                        MessageObject messageObject5;
                        Object obj;
                        delayedMessage5 = (DelayedMessage) arrayList9.get(i4);
                        i3 = delayedMessage5.type;
                        if (i3 == 0) {
                            messageObject5 = delayedMessage5.obj;
                            obj = null;
                        } else {
                            if (i3 == 2) {
                                messageObject = delayedMessage5.obj;
                            } else if (i3 == 4) {
                                messageObject = (MessageObject) delayedMessage5.extraHashMap.get(str6);
                                if (messageObject.getDocument() == null) {
                                    messageObject5 = messageObject;
                                    obj = null;
                                }
                            } else {
                                obj = -1;
                                messageObject5 = messageObject2;
                            }
                            messageObject5 = messageObject;
                            obj = 1;
                        }
                        StringBuilder stringBuilder5;
                        if (obj == null) {
                            stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(Utilities.MD5(str6));
                            stringBuilder5.append(".");
                            stringBuilder5.append(ImageLoader.getHttpUrlExtension(str6, "file"));
                            File file = new File(FileLoader.getDirectory(4), stringBuilder5.toString());
                            DispatchQueue dispatchQueue = Utilities.globalQueue;
                            -$$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv-97eY -__lambda_sendmessageshelper_1kox99gmebip9sys_e7uqv-97ey = r0;
                            -$$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv-97eY -__lambda_sendmessageshelper_1kox99gmebip9sys_e7uqv-97ey2 = new -$$Lambda$SendMessagesHelper$1kOX99gMEbip9sYs_E7UQv-97eY(this, file, messageObject5, delayedMessage5, str6);
                            dispatchQueue.postRunnable(-__lambda_sendmessageshelper_1kox99gmebip9sys_e7uqv-97ey);
                        } else if (obj == 1) {
                            stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(Utilities.MD5(str6));
                            stringBuilder5.append(".gif");
                            Utilities.globalQueue.postRunnable(new -$$Lambda$SendMessagesHelper$pp0U4GJ1r75dDYF4YGnbf9kI6EU(this, delayedMessage5, new File(FileLoader.getDirectory(4), stringBuilder5.toString()), messageObject5));
                        }
                        i4++;
                        messageObject2 = null;
                    }
                    this.delayedMessages.remove(str6);
                }
            } else if (i3 == NotificationCenter.fileDidLoad) {
                str3 = (String) objArr[0];
                arrayList2 = (ArrayList) this.delayedMessages.get(str3);
                if (arrayList2 != null) {
                    while (i5 < arrayList2.size()) {
                        performSendDelayedMessage((DelayedMessage) arrayList2.get(i5));
                        i5++;
                    }
                    this.delayedMessages.remove(str3);
                }
            } else if (i3 == NotificationCenter.httpFileDidFailedLoad || i3 == NotificationCenter.fileDidFailedLoad) {
                str3 = (String) objArr[0];
                arrayList2 = (ArrayList) this.delayedMessages.get(str3);
                if (arrayList2 != null) {
                    while (i5 < arrayList2.size()) {
                        ((DelayedMessage) arrayList2.get(i5)).markAsError();
                        i5++;
                    }
                    this.delayedMessages.remove(str3);
                }
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
            getMessagesStorage().putMessages(arrayList, false, true, false, 0);
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
        getMessagesStorage().putMessages(arrayList, false, true, false, 0);
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
        getMessagesStorage().putMessages(arrayList, false, true, false, 0);
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
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        while (i2 < arrayList.size()) {
            MessageObject messageObject = (MessageObject) arrayList2.get(i2);
            arrayList4.add(Integer.valueOf(messageObject.getId()));
            int i4 = messageObject.messageOwner.to_id.channel_id;
            Message removeFromSendingMessages = removeFromSendingMessages(messageObject.getId());
            if (removeFromSendingMessages != null) {
                getConnectionsManager().cancelRequest(removeFromSendingMessages.reqId, true);
            }
            for (Entry entry : this.delayedMessages.entrySet()) {
                ArrayList arrayList5 = (ArrayList) entry.getValue();
                int i5 = 0;
                while (i5 < arrayList5.size()) {
                    DelayedMessage delayedMessage = (DelayedMessage) arrayList5.get(i5);
                    if (delayedMessage.type == 4) {
                        i = -1;
                        Object obj = null;
                        for (int i6 = 0; i6 < delayedMessage.messageObjects.size(); i6++) {
                            obj = (MessageObject) delayedMessage.messageObjects.get(i6);
                            if (obj.getId() == messageObject.getId()) {
                                i = i6;
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
                                    ArrayList arrayList6 = delayedMessage.messageObjects;
                                    MessageObject messageObject2 = (MessageObject) arrayList6.get(arrayList6.size() - 1);
                                    delayedMessage.finalGroupMessage = messageObject2.getId();
                                    messageObject2.messageOwner.params.put("final", "1");
                                    messages_Messages tL_messages_messages = new TL_messages_messages();
                                    tL_messages_messages.messages.add(messageObject2.messageOwner);
                                    getMessagesStorage().putMessages(tL_messages_messages, delayedMessage.peer, -2, 0, false);
                                }
                                sendReadyToSendGroup(delayedMessage, false, true);
                            }
                        }
                    } else if (delayedMessage.obj.getId() == messageObject.getId()) {
                        arrayList5.remove(i5);
                        delayedMessage.sendDelayedRequests();
                        MediaController.getInstance().cancelVideoConvert(delayedMessage.obj);
                        if (arrayList5.size() == 0) {
                            arrayList3.add(entry.getKey());
                            if (delayedMessage.sendEncryptedRequest != null) {
                                z = true;
                            }
                        }
                    } else {
                        i5++;
                    }
                }
            }
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
        if (arrayList.size() == 1 && ((MessageObject) arrayList2.get(0)).isEditing() && ((MessageObject) arrayList2.get(0)).previousMedia != null) {
            revertEditingMessageObject((MessageObject) arrayList2.get(0));
        } else {
            getMessagesController().deleteMessages(arrayList4, null, null, i3, false);
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
                getMessagesStorage().markMessageAsSendError(messageObject.messageOwner);
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
            } else if (!((decryptedMessageAction instanceof TL_decryptedMessageActionTyping) || (decryptedMessageAction instanceof TL_decryptedMessageActionResend))) {
                if (decryptedMessageAction instanceof TL_decryptedMessageActionCommitKey) {
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
                    arrayList = messageObject2.messageOwner.entities;
                    if (arrayList == null || arrayList.isEmpty()) {
                        arrayList2 = null;
                    } else {
                        arrayList = new ArrayList();
                        for (int i = 0; i < messageObject2.messageOwner.entities.size(); i++) {
                            MessageEntity messageEntity = (MessageEntity) messageObject2.messageOwner.entities.get(i);
                            if ((messageEntity instanceof TL_messageEntityBold) || (messageEntity instanceof TL_messageEntityItalic) || (messageEntity instanceof TL_messageEntityPre) || (messageEntity instanceof TL_messageEntityCode) || (messageEntity instanceof TL_messageEntityTextUrl)) {
                                arrayList.add(messageEntity);
                            }
                        }
                        arrayList2 = arrayList;
                    }
                    sendMessage(messageObject2.messageOwner.message, j, messageObject2.replyMessageObject, webPage, true, arrayList2, null, null);
                } else if (((int) j2) != 0) {
                    arrayList = new ArrayList();
                    arrayList.add(messageObject2);
                    sendMessage(arrayList, j2);
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
                    sendMessage((TL_photo) photo, null, j, messageObject2.replyMessageObject, message.message, message.entities, null, hashMap, messageMedia.ttl_seconds, messageObject);
                } else {
                    Document document = messageMedia.document;
                    if (document instanceof TL_document) {
                        sendMessage((TL_document) document, null, message.attachPath, j, messageObject2.replyMessageObject, message.message, message.entities, null, hashMap, messageMedia.ttl_seconds, messageObject);
                    } else if ((messageMedia instanceof TL_messageMediaVenue) || (messageMedia instanceof TL_messageMediaGeo)) {
                        sendMessage(messageObject2.messageOwner.media, j, messageObject2.replyMessageObject, null, null);
                    } else if (messageMedia.phone_number != null) {
                        User tL_userContact_old2 = new TL_userContact_old2();
                        messageMedia2 = messageObject2.messageOwner.media;
                        tL_userContact_old2.phone = messageMedia2.phone_number;
                        tL_userContact_old2.first_name = messageMedia2.first_name;
                        tL_userContact_old2.last_name = messageMedia2.last_name;
                        tL_userContact_old2.id = messageMedia2.user_id;
                        sendMessage(tL_userContact_old2, j, messageObject2.replyMessageObject, null, null);
                    } else if (i2 != 0) {
                        arrayList = new ArrayList();
                        arrayList.add(messageObject2);
                        sendMessage(arrayList, j2);
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
            getMessagesController().updateInterfaceWithMessages(message.dialog_id, arrayList);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(message);
            getMessagesStorage().putMessages(arrayList2, false, true, false, 0);
            performSendMessageRequest(tL_messages_sendScreenshotNotification, messageObject, null, null, null);
        }
    }

    public void sendSticker(Document document, long j, MessageObject messageObject, Object obj) {
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
                sendMessage((TL_document) document2, null, null, j, messageObject, null, null, null, null, 0, obj);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:79:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01b2  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0284  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02b8  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02c0  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02cd  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02f1  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x038e  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0398  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03b5  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x042e  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x046d  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x04a8  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x04d0  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01a9  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0197  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01b2  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01e0  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0284  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x029e  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x02b8  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02c0  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02cd  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02f1  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x038e  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0398  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03b5  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03e3  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x0402 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x042e  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0459 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x046d  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x048e  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x04a8  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x04d0  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x0506  */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r44, long r45) {
        /*
        r43 = this;
        r11 = r43;
        r12 = r44;
        r13 = r45;
        r15 = 0;
        if (r12 == 0) goto L_0x06e2;
    L_0x0009:
        r0 = r44.isEmpty();
        if (r0 == 0) goto L_0x0011;
    L_0x000f:
        goto L_0x06e2;
    L_0x0011:
        r0 = (int) r13;
        if (r0 == 0) goto L_0x06c6;
    L_0x0014:
        r1 = r43.getMessagesController();
        r10 = r1.getPeer(r0);
        r16 = 0;
        if (r0 <= 0) goto L_0x003e;
    L_0x0020:
        r1 = r43.getMessagesController();
        r2 = java.lang.Integer.valueOf(r0);
        r1 = r1.getUser(r2);
        if (r1 != 0) goto L_0x002f;
    L_0x002e:
        return r15;
    L_0x002f:
        r8 = r16;
        r17 = 1;
        r18 = 1;
        r19 = 1;
        r20 = 1;
        r21 = 0;
        r22 = 0;
        goto L_0x0075;
    L_0x003e:
        r1 = r43.getMessagesController();
        r2 = -r0;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getChat(r2);
        r2 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r2 == 0) goto L_0x0056;
    L_0x0051:
        r2 = r1.megagroup;
        r3 = r1.signatures;
        goto L_0x0058;
    L_0x0056:
        r2 = 0;
        r3 = 0;
    L_0x0058:
        r4 = org.telegram.messenger.ChatObject.canSendStickers(r1);
        r5 = org.telegram.messenger.ChatObject.canSendMedia(r1);
        r6 = org.telegram.messenger.ChatObject.canSendEmbed(r1);
        r7 = org.telegram.messenger.ChatObject.canSendPolls(r1);
        r8 = r1;
        r21 = r2;
        r22 = r3;
        r17 = r4;
        r18 = r5;
        r20 = r6;
        r19 = r7;
    L_0x0075:
        r7 = new android.util.LongSparseArray;
        r7.<init>();
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
        r6 = r43.getMessagesController();
        r6 = r6.getInputPeer(r0);
        r0 = r43.getUserConfig();
        r0 = r0.getClientUserId();
        r23 = r10;
        r9 = (long) r0;
        r0 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1));
        if (r0 != 0) goto L_0x00ad;
    L_0x00aa:
        r25 = 1;
        goto L_0x00af;
    L_0x00ad:
        r25 = 0;
    L_0x00af:
        r0 = r1;
        r1 = r2;
        r2 = r4;
        r26 = 0;
        r4 = r3;
        r3 = r5;
        r5 = 0;
    L_0x00b7:
        r15 = r44.size();
        if (r5 >= r15) goto L_0x06c3;
    L_0x00bd:
        r15 = r12.get(r5);
        r15 = (org.telegram.messenger.MessageObject) r15;
        r27 = r15.getId();
        if (r27 <= 0) goto L_0x065f;
    L_0x00c9:
        r27 = r15.needDrawBluredPreview();
        if (r27 == 0) goto L_0x00d1;
    L_0x00cf:
        goto L_0x065f;
    L_0x00d1:
        r27 = r1;
        if (r17 != 0) goto L_0x0124;
    L_0x00d5:
        r29 = r15.isSticker();
        if (r29 != 0) goto L_0x00ed;
    L_0x00db:
        r29 = r15.isAnimatedSticker();
        if (r29 != 0) goto L_0x00ed;
    L_0x00e1:
        r29 = r15.isGif();
        if (r29 != 0) goto L_0x00ed;
    L_0x00e7:
        r29 = r15.isGame();
        if (r29 == 0) goto L_0x0124;
    L_0x00ed:
        if (r26 != 0) goto L_0x010d;
    L_0x00ef:
        r15 = 8;
        r15 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r15);
        if (r15 == 0) goto L_0x00fa;
    L_0x00f7:
        r26 = 4;
        goto L_0x00fc;
    L_0x00fa:
        r26 = 1;
    L_0x00fc:
        r13 = r5;
        r35 = r6;
        r28 = r7;
        r33 = r8;
        r29 = r9;
        r32 = r23;
        r1 = r27;
    L_0x0109:
        r36 = 1;
        goto L_0x06b1;
    L_0x010d:
        r24 = r0;
        r31 = r3;
        r34 = r4;
        r13 = r5;
        r35 = r6;
        r28 = r7;
        r33 = r8;
        r29 = r9;
        r32 = r23;
    L_0x011e:
        r36 = 1;
        r23 = r2;
        goto L_0x06a7;
    L_0x0124:
        r29 = 2;
        if (r18 != 0) goto L_0x016c;
    L_0x0128:
        r1 = r15.messageOwner;
        r1 = r1.media;
        r31 = r0;
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x0136;
    L_0x0132:
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r0 == 0) goto L_0x016e;
    L_0x0136:
        if (r26 != 0) goto L_0x0155;
    L_0x0138:
        r0 = 7;
        r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r0);
        if (r0 == 0) goto L_0x0143;
    L_0x013f:
        r0 = 5;
        r26 = 5;
        goto L_0x0145;
    L_0x0143:
        r26 = 2;
    L_0x0145:
        r13 = r5;
        r35 = r6;
        r28 = r7;
        r33 = r8;
        r29 = r9;
        r32 = r23;
        r1 = r27;
        r0 = r31;
        goto L_0x0109;
    L_0x0155:
        r34 = r4;
        r13 = r5;
        r35 = r6;
        r28 = r7;
        r33 = r8;
        r29 = r9;
        r32 = r23;
        r24 = r31;
        r36 = 1;
        r23 = r2;
        r31 = r3;
        goto L_0x06a7;
    L_0x016c:
        r31 = r0;
    L_0x016e:
        if (r19 != 0) goto L_0x018a;
    L_0x0170:
        r0 = r15.messageOwner;
        r0 = r0.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r0 == 0) goto L_0x018a;
    L_0x0178:
        if (r26 != 0) goto L_0x0155;
    L_0x017a:
        r0 = 10;
        r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r0);
        if (r0 == 0) goto L_0x0186;
    L_0x0182:
        r0 = 6;
        r26 = 6;
        goto L_0x0145;
    L_0x0186:
        r0 = 3;
        r26 = 3;
        goto L_0x0145;
    L_0x018a:
        r0 = new org.telegram.tgnet.TLRPC$TL_message;
        r0.<init>();
        r32 = r15.getDialogId();
        r1 = (r32 > r9 ? 1 : (r32 == r9 ? 0 : -1));
        if (r1 != 0) goto L_0x01a9;
    L_0x0197:
        r1 = r15.messageOwner;
        r1 = r1.from_id;
        r32 = r43.getUserConfig();
        r33 = r8;
        r8 = r32.getClientUserId();
        if (r1 != r8) goto L_0x01ab;
    L_0x01a7:
        r1 = 1;
        goto L_0x01ac;
    L_0x01a9:
        r33 = r8;
    L_0x01ab:
        r1 = 0;
    L_0x01ac:
        r8 = r15.isForwarded();
        if (r8 == 0) goto L_0x01e0;
    L_0x01b2:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader;
        r1.<init>();
        r0.fwd_from = r1;
        r1 = r0.fwd_from;
        r8 = r15.messageOwner;
        r8 = r8.fwd_from;
        r11 = r8.flags;
        r1.flags = r11;
        r11 = r8.from_id;
        r1.from_id = r11;
        r11 = r8.date;
        r1.date = r11;
        r11 = r8.channel_id;
        r1.channel_id = r11;
        r11 = r8.channel_post;
        r1.channel_post = r11;
        r11 = r8.post_author;
        r1.post_author = r11;
        r8 = r8.from_name;
        r1.from_name = r8;
        r8 = 4;
        r0.flags = r8;
        goto L_0x0280;
    L_0x01e0:
        r8 = 4;
        if (r1 != 0) goto L_0x0280;
    L_0x01e3:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader;
        r1.<init>();
        r0.fwd_from = r1;
        r1 = r0.fwd_from;
        r11 = r15.getId();
        r1.channel_post = r11;
        r1 = r0.fwd_from;
        r11 = r1.flags;
        r11 = r11 | r8;
        r1.flags = r11;
        r1 = r15.isFromUser();
        if (r1 == 0) goto L_0x020e;
    L_0x01ff:
        r1 = r0.fwd_from;
        r8 = r15.messageOwner;
        r8 = r8.from_id;
        r1.from_id = r8;
        r8 = r1.flags;
        r11 = 1;
        r8 = r8 | r11;
        r1.flags = r8;
        goto L_0x022e;
    L_0x020e:
        r1 = r0.fwd_from;
        r8 = r15.messageOwner;
        r11 = r8.to_id;
        r11 = r11.channel_id;
        r1.channel_id = r11;
        r11 = r1.flags;
        r11 = r11 | 2;
        r1.flags = r11;
        r11 = r8.post;
        if (r11 == 0) goto L_0x022e;
    L_0x0222:
        r8 = r8.from_id;
        if (r8 <= 0) goto L_0x022e;
    L_0x0226:
        r1.from_id = r8;
        r8 = r1.flags;
        r11 = 1;
        r8 = r8 | r11;
        r1.flags = r8;
    L_0x022e:
        r1 = r15.messageOwner;
        r1 = r1.post_author;
        if (r1 == 0) goto L_0x0240;
    L_0x0234:
        r8 = r0.fwd_from;
        r8.post_author = r1;
        r1 = r8.flags;
        r11 = 8;
        r1 = r1 | r11;
        r8.flags = r1;
        goto L_0x0277;
    L_0x0240:
        r1 = r15.isOutOwner();
        if (r1 != 0) goto L_0x0277;
    L_0x0246:
        r1 = r15.messageOwner;
        r8 = r1.from_id;
        if (r8 <= 0) goto L_0x0277;
    L_0x024c:
        r1 = r1.post;
        if (r1 == 0) goto L_0x0277;
    L_0x0250:
        r1 = r43.getMessagesController();
        r8 = r15.messageOwner;
        r8 = r8.from_id;
        r8 = java.lang.Integer.valueOf(r8);
        r1 = r1.getUser(r8);
        if (r1 == 0) goto L_0x0277;
    L_0x0262:
        r8 = r0.fwd_from;
        r11 = r1.first_name;
        r1 = r1.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r11, r1);
        r8.post_author = r1;
        r1 = r0.fwd_from;
        r8 = r1.flags;
        r11 = 8;
        r8 = r8 | r11;
        r1.flags = r8;
    L_0x0277:
        r1 = r15.messageOwner;
        r1 = r1.date;
        r0.date = r1;
        r1 = 4;
        r0.flags = r1;
    L_0x0280:
        r1 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1));
        if (r1 != 0) goto L_0x029c;
    L_0x0284:
        r1 = r0.fwd_from;
        if (r1 == 0) goto L_0x029c;
    L_0x0288:
        r8 = r1.flags;
        r8 = r8 | 16;
        r1.flags = r8;
        r8 = r15.getId();
        r1.saved_from_msg_id = r8;
        r1 = r0.fwd_from;
        r8 = r15.messageOwner;
        r8 = r8.to_id;
        r1.saved_from_peer = r8;
    L_0x029c:
        if (r20 != 0) goto L_0x02ae;
    L_0x029e:
        r1 = r15.messageOwner;
        r1 = r1.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r1 == 0) goto L_0x02ae;
    L_0x02a6:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r1.<init>();
        r0.media = r1;
        goto L_0x02b4;
    L_0x02ae:
        r1 = r15.messageOwner;
        r1 = r1.media;
        r0.media = r1;
    L_0x02b4:
        r1 = r0.media;
        if (r1 == 0) goto L_0x02be;
    L_0x02b8:
        r1 = r0.flags;
        r1 = r1 | 512;
        r0.flags = r1;
    L_0x02be:
        if (r21 == 0) goto L_0x02c7;
    L_0x02c0:
        r1 = r0.flags;
        r8 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r1 = r1 | r8;
        r0.flags = r1;
    L_0x02c7:
        r1 = r15.messageOwner;
        r1 = r1.via_bot_id;
        if (r1 == 0) goto L_0x02d5;
    L_0x02cd:
        r0.via_bot_id = r1;
        r1 = r0.flags;
        r1 = r1 | 2048;
        r0.flags = r1;
    L_0x02d5:
        r1 = r15.messageOwner;
        r1 = r1.message;
        r0.message = r1;
        r1 = r15.getId();
        r0.fwd_msg_id = r1;
        r1 = r15.messageOwner;
        r8 = r1.attachPath;
        r0.attachPath = r8;
        r8 = r1.entities;
        r0.entities = r8;
        r1 = r1.reply_markup;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
        if (r1 == 0) goto L_0x0384;
    L_0x02f1:
        r1 = r0.flags;
        r1 = r1 | 64;
        r0.flags = r1;
        r1 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
        r1.<init>();
        r0.reply_markup = r1;
        r1 = r15.messageOwner;
        r1 = r1.reply_markup;
        r1 = r1.rows;
        r1 = r1.size();
        r8 = 0;
    L_0x0309:
        if (r8 >= r1) goto L_0x0384;
    L_0x030b:
        r11 = r15.messageOwner;
        r11 = r11.reply_markup;
        r11 = r11.rows;
        r11 = r11.get(r8);
        r11 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r11;
        r28 = r1;
        r1 = r11.buttons;
        r1 = r1.size();
        r29 = r9;
        r10 = r16;
        r9 = 0;
    L_0x0324:
        if (r9 >= r1) goto L_0x037b;
    L_0x0326:
        r32 = r1;
        r1 = r11.buttons;
        r1 = r1.get(r9);
        r1 = (org.telegram.tgnet.TLRPC.KeyboardButton) r1;
        r34 = r11;
        r11 = r1 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth;
        if (r11 != 0) goto L_0x033e;
    L_0x0336:
        r13 = r1 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
        if (r13 != 0) goto L_0x033e;
    L_0x033a:
        r13 = r1 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
        if (r13 == 0) goto L_0x0372;
    L_0x033e:
        if (r11 == 0) goto L_0x035f;
    L_0x0340:
        r11 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
        r11.<init>();
        r13 = r1.flags;
        r11.flags = r13;
        r13 = r1.fwd_text;
        if (r13 == 0) goto L_0x0352;
    L_0x034d:
        r11.fwd_text = r13;
        r11.text = r13;
        goto L_0x0356;
    L_0x0352:
        r13 = r1.text;
        r11.text = r13;
    L_0x0356:
        r13 = r1.url;
        r11.url = r13;
        r1 = r1.button_id;
        r11.button_id = r1;
        r1 = r11;
    L_0x035f:
        if (r10 != 0) goto L_0x036d;
    L_0x0361:
        r10 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow;
        r10.<init>();
        r11 = r0.reply_markup;
        r11 = r11.rows;
        r11.add(r10);
    L_0x036d:
        r11 = r10.buttons;
        r11.add(r1);
    L_0x0372:
        r9 = r9 + 1;
        r13 = r45;
        r1 = r32;
        r11 = r34;
        goto L_0x0324;
    L_0x037b:
        r8 = r8 + 1;
        r13 = r45;
        r1 = r28;
        r9 = r29;
        goto L_0x0309;
    L_0x0384:
        r29 = r9;
        r1 = r0.entities;
        r1 = r1.isEmpty();
        if (r1 != 0) goto L_0x0394;
    L_0x038e:
        r1 = r0.flags;
        r1 = r1 | 128;
        r0.flags = r1;
    L_0x0394:
        r1 = r0.attachPath;
        if (r1 != 0) goto L_0x039c;
    L_0x0398:
        r1 = "";
        r0.attachPath = r1;
    L_0x039c:
        r1 = r43.getUserConfig();
        r1 = r1.getNewMessageId();
        r0.id = r1;
        r0.local_id = r1;
        r1 = 1;
        r0.out = r1;
        r1 = r15.messageOwner;
        r8 = r1.grouped_id;
        r10 = 0;
        r1 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r1 == 0) goto L_0x03db;
    L_0x03b5:
        r1 = r7.get(r8);
        r1 = (java.lang.Long) r1;
        if (r1 != 0) goto L_0x03ce;
    L_0x03bd:
        r1 = org.telegram.messenger.Utilities.random;
        r13 = r1.nextLong();
        r1 = java.lang.Long.valueOf(r13);
        r13 = r15.messageOwner;
        r13 = r13.grouped_id;
        r7.put(r13, r1);
    L_0x03ce:
        r13 = r1.longValue();
        r0.grouped_id = r13;
        r1 = r0.flags;
        r13 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r1 = r1 | r13;
        r0.flags = r1;
    L_0x03db:
        r1 = r44.size();
        r13 = 1;
        r1 = r1 - r13;
        if (r5 == r1) goto L_0x03fb;
    L_0x03e3:
        r1 = r5 + 1;
        r1 = r12.get(r1);
        r1 = (org.telegram.messenger.MessageObject) r1;
        r1 = r1.messageOwner;
        r13 = r1.grouped_id;
        r1 = r15.messageOwner;
        r10 = r1.grouped_id;
        r1 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1));
        if (r1 == 0) goto L_0x03fb;
    L_0x03f7:
        r10 = r23;
        r1 = 1;
        goto L_0x03fe;
    L_0x03fb:
        r10 = r23;
        r1 = 0;
    L_0x03fe:
        r11 = r10.channel_id;
        if (r11 == 0) goto L_0x0416;
    L_0x0402:
        if (r21 != 0) goto L_0x0416;
    L_0x0404:
        if (r22 == 0) goto L_0x040f;
    L_0x0406:
        r11 = r43.getUserConfig();
        r11 = r11.getClientUserId();
        goto L_0x0410;
    L_0x040f:
        r11 = -r11;
    L_0x0410:
        r0.from_id = r11;
        r11 = 1;
        r0.post = r11;
        goto L_0x0426;
    L_0x0416:
        r11 = r43.getUserConfig();
        r11 = r11.getClientUserId();
        r0.from_id = r11;
        r11 = r0.flags;
        r11 = r11 | 256;
        r0.flags = r11;
    L_0x0426:
        r13 = r0.random_id;
        r34 = 0;
        r11 = (r13 > r34 ? 1 : (r13 == r34 ? 0 : -1));
        if (r11 != 0) goto L_0x0434;
    L_0x042e:
        r13 = r43.getNextRandomId();
        r0.random_id = r13;
    L_0x0434:
        r13 = r0.random_id;
        r11 = java.lang.Long.valueOf(r13);
        r4.add(r11);
        r13 = r0.random_id;
        r3.put(r13, r0);
        r11 = r0.fwd_msg_id;
        r11 = java.lang.Integer.valueOf(r11);
        r2.add(r11);
        r11 = r43.getConnectionsManager();
        r11 = r11.getCurrentTime();
        r0.date = r11;
        r11 = r6 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r11 == 0) goto L_0x0465;
    L_0x0459:
        if (r21 != 0) goto L_0x0465;
    L_0x045b:
        r13 = 1;
        r0.views = r13;
        r13 = r0.flags;
        r13 = r13 | 1024;
        r0.flags = r13;
        goto L_0x047a;
    L_0x0465:
        r13 = r15.messageOwner;
        r14 = r13.flags;
        r14 = r14 & 1024;
        if (r14 == 0) goto L_0x0477;
    L_0x046d:
        r13 = r13.views;
        r0.views = r13;
        r13 = r0.flags;
        r13 = r13 | 1024;
        r0.flags = r13;
    L_0x0477:
        r13 = 1;
        r0.unread = r13;
    L_0x047a:
        r13 = r45;
        r0.dialog_id = r13;
        r0.to_id = r10;
        r23 = org.telegram.messenger.MessageObject.isVoiceMessage(r0);
        if (r23 != 0) goto L_0x048c;
    L_0x0486:
        r23 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r0);
        if (r23 == 0) goto L_0x049e;
    L_0x048c:
        if (r11 == 0) goto L_0x049b;
    L_0x048e:
        r11 = r15.getChannelId();
        if (r11 == 0) goto L_0x049b;
    L_0x0494:
        r11 = r15.isContentUnread();
        r0.media_unread = r11;
        goto L_0x049e;
    L_0x049b:
        r11 = 1;
        r0.media_unread = r11;
    L_0x049e:
        r11 = r15.messageOwner;
        r11 = r11.to_id;
        r23 = r3;
        r3 = r11 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;
        if (r3 == 0) goto L_0x04ad;
    L_0x04a8:
        r3 = r11.channel_id;
        r3 = -r3;
        r0.ttl = r3;
    L_0x04ad:
        r3 = new org.telegram.messenger.MessageObject;
        r11 = r43;
        r28 = r7;
        r7 = r11.currentAccount;
        r32 = r10;
        r10 = 1;
        r3.<init>(r7, r0, r10);
        r7 = r3.messageOwner;
        r7.send_state = r10;
        r7 = r31;
        r7.add(r3);
        r3 = r27;
        r3.add(r0);
        r11.putToSendingMessages(r0);
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0504;
    L_0x04d0:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r10 = "forward message user_id = ";
        r0.append(r10);
        r10 = r6.user_id;
        r0.append(r10);
        r10 = " chat_id = ";
        r0.append(r10);
        r10 = r6.chat_id;
        r0.append(r10);
        r10 = " channel_id = ";
        r0.append(r10);
        r10 = r6.channel_id;
        r0.append(r10);
        r10 = " access_hash = ";
        r0.append(r10);
        r10 = r6.access_hash;
        r0.append(r10);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0504:
        if (r1 == 0) goto L_0x050c;
    L_0x0506:
        r0 = r3.size();
        if (r0 > 0) goto L_0x0545;
    L_0x050c:
        r0 = r3.size();
        r1 = 100;
        if (r0 == r1) goto L_0x0545;
    L_0x0514:
        r0 = r44.size();
        r1 = 1;
        r0 = r0 - r1;
        if (r5 == r0) goto L_0x0545;
    L_0x051c:
        r0 = r44.size();
        r0 = r0 - r1;
        if (r5 == r0) goto L_0x0538;
    L_0x0523:
        r0 = r5 + 1;
        r0 = r12.get(r0);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.getDialogId();
        r10 = r15.getDialogId();
        r27 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r27 == 0) goto L_0x0538;
    L_0x0537:
        goto L_0x0545;
    L_0x0538:
        r27 = r3;
        r34 = r4;
        r13 = r5;
        r35 = r6;
        r24 = r7;
        r31 = r23;
        goto L_0x011e;
    L_0x0545:
        r36 = r43.getMessagesStorage();
        r0 = new java.util.ArrayList;
        r0.<init>(r3);
        r38 = 0;
        r39 = 1;
        r40 = 0;
        r41 = 0;
        r37 = r0;
        r36.putMessages(r37, r38, r39, r40, r41);
        r0 = r43.getMessagesController();
        r0.updateInterfaceWithMessages(r13, r7);
        r0 = r43.getNotificationCenter();
        r1 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r10 = 0;
        r11 = new java.lang.Object[r10];
        r0.postNotificationName(r1, r11);
        r0 = r43.getUserConfig();
        r0.saveConfig(r10);
        r11 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;
        r11.<init>();
        r11.to_peer = r6;
        r0 = 0;
        r10 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r10 == 0) goto L_0x0584;
    L_0x0582:
        r0 = 1;
        goto L_0x0585;
    L_0x0584:
        r0 = 0;
    L_0x0585:
        r11.grouped = r0;
        r0 = r11.to_peer;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r0 == 0) goto L_0x05ae;
    L_0x058d:
        r10 = r43;
        r0 = r10.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r8 = "silent_";
        r1.append(r8);
        r1.append(r13);
        r1 = r1.toString();
        r8 = 0;
        r0 = r0.getBoolean(r1, r8);
        r11.silent = r0;
        goto L_0x05b0;
    L_0x05ae:
        r10 = r43;
    L_0x05b0:
        r0 = r15.messageOwner;
        r0 = r0.to_id;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;
        if (r0 == 0) goto L_0x05e2;
    L_0x05b8:
        r0 = r43.getMessagesController();
        r1 = r15.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
        r1.<init>();
        r11.from_peer = r1;
        r1 = r11.from_peer;
        r8 = r15.messageOwner;
        r8 = r8.to_id;
        r8 = r8.channel_id;
        r1.channel_id = r8;
        if (r0 == 0) goto L_0x05e9;
    L_0x05dd:
        r8 = r0.access_hash;
        r1.access_hash = r8;
        goto L_0x05e9;
    L_0x05e2:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r0.<init>();
        r11.from_peer = r0;
    L_0x05e9:
        r11.random_id = r4;
        r11.id = r2;
        r0 = r44.size();
        r9 = 1;
        if (r0 != r9) goto L_0x0603;
    L_0x05f4:
        r0 = 0;
        r1 = r12.get(r0);
        r1 = (org.telegram.messenger.MessageObject) r1;
        r0 = r1.messageOwner;
        r0 = r0.with_my_score;
        if (r0 == 0) goto L_0x0603;
    L_0x0601:
        r0 = 1;
        goto L_0x0604;
    L_0x0603:
        r0 = 0;
    L_0x0604:
        r11.with_my_score = r0;
        r15 = r43.getConnectionsManager();
        r8 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$GtPQ6DFMMI1Gm-S7QANSsM7url8;
        r24 = r7;
        r0 = r8;
        r27 = r3;
        r1 = r43;
        r31 = r23;
        r23 = r2;
        r2 = r45;
        r34 = r4;
        r4 = r21;
        r7 = r5;
        r5 = r25;
        r35 = r6;
        r6 = r31;
        r13 = r7;
        r7 = r27;
        r14 = r8;
        r8 = r24;
        r36 = 1;
        r9 = r32;
        r10 = r11;
        r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10);
        r0 = 68;
        r15.sendRequest(r11, r14, r0);
        r0 = r44.size();
        r0 = r0 + -1;
        if (r13 == r0) goto L_0x06a7;
    L_0x063f:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = new android.util.LongSparseArray;
        r4.<init>();
        r42 = r4;
        r4 = r2;
        r2 = r3;
        r3 = r42;
        goto L_0x06b1;
    L_0x065f:
        r24 = r0;
        r27 = r1;
        r31 = r3;
        r34 = r4;
        r13 = r5;
        r35 = r6;
        r28 = r7;
        r33 = r8;
        r29 = r9;
        r32 = r23;
        r36 = 1;
        r23 = r2;
        r0 = r15.type;
        if (r0 != 0) goto L_0x06a7;
    L_0x067a:
        r0 = r15.messageText;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x06a7;
    L_0x0682:
        r0 = r15.messageOwner;
        r0 = r0.media;
        if (r0 == 0) goto L_0x068c;
    L_0x0688:
        r0 = r0.webpage;
        r5 = r0;
        goto L_0x068e;
    L_0x068c:
        r5 = r16;
    L_0x068e:
        r0 = r15.messageText;
        r1 = r0.toString();
        r4 = 0;
        if (r5 == 0) goto L_0x0699;
    L_0x0697:
        r6 = 1;
        goto L_0x069a;
    L_0x0699:
        r6 = 0;
    L_0x069a:
        r0 = r15.messageOwner;
        r7 = r0.entities;
        r8 = 0;
        r9 = 0;
        r0 = r43;
        r2 = r45;
        r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9);
    L_0x06a7:
        r2 = r23;
        r0 = r24;
        r1 = r27;
        r3 = r31;
        r4 = r34;
    L_0x06b1:
        r5 = r13 + 1;
        r11 = r43;
        r13 = r45;
        r7 = r28;
        r9 = r29;
        r23 = r32;
        r8 = r33;
        r6 = r35;
        goto L_0x00b7;
    L_0x06c3:
        r2 = r43;
        goto L_0x06e1;
    L_0x06c6:
        r0 = 0;
    L_0x06c7:
        r1 = r44.size();
        if (r0 >= r1) goto L_0x06dd;
    L_0x06cd:
        r1 = r12.get(r0);
        r1 = (org.telegram.messenger.MessageObject) r1;
        r2 = r43;
        r3 = r45;
        r2.processForwardFromMyName(r1, r3);
        r0 = r0 + 1;
        goto L_0x06c7;
    L_0x06dd:
        r2 = r43;
        r26 = 0;
    L_0x06e1:
        return r26;
    L_0x06e2:
        r2 = r11;
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long):int");
    }

    public /* synthetic */ void lambda$sendMessage$9$SendMessagesHelper(long j, boolean z, boolean z2, LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, Peer peer, TL_messages_forwardMessages tL_messages_forwardMessages, TLObject tLObject, TL_error tL_error) {
        int indexOf;
        ArrayList arrayList3 = arrayList;
        ArrayList arrayList4 = arrayList2;
        TL_error tL_error2 = tL_error;
        boolean z3 = false;
        if (tL_error2 == null) {
            boolean z4;
            Update update;
            long j2;
            SparseLongArray sparseLongArray = new SparseLongArray();
            Updates updates = (Updates) tLObject;
            int i = 0;
            while (true) {
                z4 = true;
                if (i >= updates.updates.size()) {
                    break;
                }
                update = (Update) updates.updates.get(i);
                if (update instanceof TL_updateMessageID) {
                    TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) update;
                    sparseLongArray.put(tL_updateMessageID.id, tL_updateMessageID.random_id);
                    updates.updates.remove(i);
                    i--;
                }
                i++;
            }
            Integer num = (Integer) getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(j));
            if (num == null) {
                num = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j));
                getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(j), num);
            } else {
                j2 = j;
            }
            Integer num2 = num;
            i = 0;
            int i2 = 0;
            while (i < updates.updates.size()) {
                int i3;
                SparseLongArray sparseLongArray2;
                update = (Update) updates.updates.get(i);
                boolean z5 = update instanceof TL_updateNewMessage;
                if (z5 || (update instanceof TL_updateNewChannelMessage)) {
                    Message message;
                    updates.updates.remove(i);
                    i3 = i - 1;
                    if (z5) {
                        TL_updateNewMessage tL_updateNewMessage = (TL_updateNewMessage) update;
                        message = tL_updateNewMessage.message;
                        getMessagesController().processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
                    } else {
                        TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                        message = tL_updateNewChannelMessage.message;
                        getMessagesController().processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, message.to_id.channel_id);
                        if (z) {
                            message.flags |= Integer.MIN_VALUE;
                        }
                    }
                    Message message2 = message;
                    ImageLoader.saveMessageThumbs(message2);
                    message2.unread = num2.intValue() < message2.id;
                    if (z2) {
                        message2.out = z4;
                        message2.unread = z3;
                        message2.media_unread = z3;
                    }
                    long j3 = sparseLongArray.get(message2.id);
                    if (j3 != 0) {
                        Message message3 = (Message) longSparseArray.get(j3);
                        if (message3 != null) {
                            indexOf = arrayList3.indexOf(message3);
                            if (indexOf != -1) {
                                MessageObject messageObject = (MessageObject) arrayList4.get(indexOf);
                                arrayList3.remove(indexOf);
                                arrayList4.remove(indexOf);
                                int i4 = message3.id;
                                ArrayList arrayList5 = new ArrayList();
                                arrayList5.add(message2);
                                ArrayList arrayList6 = arrayList5;
                                int i5 = i4;
                                Message message4 = message3;
                                Message message5 = message2;
                                updateMediaPaths(messageObject, message2, message2.id, null, true);
                                int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                                message4.id = message5.id;
                                int i6 = i2 + 1;
                                DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                                Message message6 = message5;
                                -$$Lambda$SendMessagesHelper$AguYDqZ0V89DG_eBOiVamirE7iE -__lambda_sendmessageshelper_aguydqz0v89dg_eboivamire7ie = r0;
                                sparseLongArray2 = sparseLongArray;
                                -$$Lambda$SendMessagesHelper$AguYDqZ0V89DG_eBOiVamirE7iE -__lambda_sendmessageshelper_aguydqz0v89dg_eboivamire7ie2 = new -$$Lambda$SendMessagesHelper$AguYDqZ0V89DG_eBOiVamirE7iE(this, message4, i5, peer, arrayList6, j, message6, mediaExistanceFlags);
                                storageQueue.postRunnable(-__lambda_sendmessageshelper_aguydqz0v89dg_eboivamire7ie);
                                i2 = i6;
                                i = i3 + 1;
                                j2 = j;
                                arrayList4 = arrayList2;
                                sparseLongArray = sparseLongArray2;
                                z4 = true;
                                z3 = false;
                            }
                        }
                    }
                } else {
                    i3 = i;
                }
                sparseLongArray2 = sparseLongArray;
                i = i3 + 1;
                j2 = j;
                arrayList4 = arrayList2;
                sparseLongArray = sparseLongArray2;
                z4 = true;
                z3 = false;
            }
            if (updates.updates.isEmpty()) {
                indexOf = false;
            } else {
                indexOf = false;
                getMessagesController().processUpdates(updates, false);
            }
            getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, i2);
        } else {
            indexOf = 0;
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$l40Sz3n3XBnqm5WNgM6XcCJnSAw(this, tL_error2, tL_messages_forwardMessages));
        }
        while (indexOf < arrayList.size()) {
            Message message7 = (Message) arrayList3.get(indexOf);
            getMessagesStorage().markMessageAsSendError(message7);
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$OpZpi03weztqM5vuYHFqnb91tB4(this, message7));
            indexOf++;
        }
    }

    public /* synthetic */ void lambda$null$6$SendMessagesHelper(Message message, int i, Peer peer, ArrayList arrayList, long j, Message message2, int i2) {
        Message message3 = message;
        getMessagesStorage().updateMessageStateAndId(message3.random_id, Integer.valueOf(i), message3.id, 0, false, peer.channel_id);
        getMessagesStorage().putMessages(arrayList, true, false, false, 0);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$imJ61LkDqCKC2zsI4rTSXfDp6Z0(this, message3, j, i, message2, i2));
    }

    public /* synthetic */ void lambda$null$5$SendMessagesHelper(Message message, long j, int i, Message message2, int i2) {
        message.send_state = 0;
        getMediaDataController().increasePeerRaiting(j);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message2.id), message2, Long.valueOf(j), Long.valueOf(0), Integer.valueOf(i2));
        processSentMessage(i);
        removeFromSendingMessages(i);
    }

    public /* synthetic */ void lambda$null$7$SendMessagesHelper(TL_error tL_error, TL_messages_forwardMessages tL_messages_forwardMessages) {
        AlertsCreator.processError(this.currentAccount, tL_error, null, tL_messages_forwardMessages, new Object[0]);
    }

    public /* synthetic */ void lambda$null$8$SendMessagesHelper(Message message) {
        message.send_state = 2;
        getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
        processSentMessage(message.id);
        removeFromSendingMessages(message.id);
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

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00fd A:{Catch:{ Exception -> 0x04d4 }} */
    private void editMessageMedia(org.telegram.messenger.MessageObject r25, org.telegram.tgnet.TLRPC.TL_photo r26, org.telegram.messenger.VideoEditedInfo r27, org.telegram.tgnet.TLRPC.TL_document r28, java.lang.String r29, java.util.HashMap<java.lang.String, java.lang.String> r30, boolean r31, java.lang.Object r32) {
        /*
        r24 = this;
        r9 = r24;
        r10 = r25;
        r0 = r26;
        r1 = r28;
        r2 = r29;
        r8 = r32;
        r3 = "originalPath";
        if (r10 != 0) goto L_0x0011;
    L_0x0010:
        return;
    L_0x0011:
        r4 = r10.messageOwner;
        r5 = 0;
        r10.cancelEditing = r5;
        r6 = r25.getDialogId();	 Catch:{ Exception -> 0x04d4 }
        r12 = "http";
        r13 = 1;
        if (r31 == 0) goto L_0x0058;
    L_0x001f:
        r2 = r10.messageOwner;	 Catch:{ Exception -> 0x04d4 }
        r2 = r2.media;	 Catch:{ Exception -> 0x04d4 }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x04d4 }
        if (r2 == 0) goto L_0x0033;
    L_0x0027:
        r0 = r10.messageOwner;	 Catch:{ Exception -> 0x04d4 }
        r0 = r0.media;	 Catch:{ Exception -> 0x04d4 }
        r0 = r0.photo;	 Catch:{ Exception -> 0x04d4 }
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;	 Catch:{ Exception -> 0x04d4 }
        r11 = r27;
        r2 = 2;
        goto L_0x0049;
    L_0x0033:
        r1 = r10.messageOwner;	 Catch:{ Exception -> 0x04d4 }
        r1 = r1.media;	 Catch:{ Exception -> 0x04d4 }
        r1 = r1.document;	 Catch:{ Exception -> 0x04d4 }
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;	 Catch:{ Exception -> 0x04d4 }
        r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1);	 Catch:{ Exception -> 0x04d4 }
        if (r2 != 0) goto L_0x0046;
    L_0x0041:
        if (r27 == 0) goto L_0x0044;
    L_0x0043:
        goto L_0x0046;
    L_0x0044:
        r2 = 7;
        goto L_0x0047;
    L_0x0046:
        r2 = 3;
    L_0x0047:
        r11 = r10.videoEditedInfo;	 Catch:{ Exception -> 0x04d4 }
    L_0x0049:
        r14 = r4.params;	 Catch:{ Exception -> 0x04d4 }
        r15 = r4.message;	 Catch:{ Exception -> 0x04d4 }
        r10.editingMessage = r15;	 Catch:{ Exception -> 0x04d4 }
        r15 = r4.entities;	 Catch:{ Exception -> 0x04d4 }
        r10.editingMessageEntities = r15;	 Catch:{ Exception -> 0x04d4 }
        r15 = r4.attachPath;	 Catch:{ Exception -> 0x04d4 }
        r13 = r2;
        goto L_0x0113;
    L_0x0058:
        r11 = r4.media;	 Catch:{ Exception -> 0x04d4 }
        r10.previousMedia = r11;	 Catch:{ Exception -> 0x04d4 }
        r11 = r4.message;	 Catch:{ Exception -> 0x04d4 }
        r10.previousCaption = r11;	 Catch:{ Exception -> 0x04d4 }
        r11 = r4.entities;	 Catch:{ Exception -> 0x04d4 }
        r10.previousCaptionEntities = r11;	 Catch:{ Exception -> 0x04d4 }
        r11 = r4.attachPath;	 Catch:{ Exception -> 0x04d4 }
        r10.previousAttachPath = r11;	 Catch:{ Exception -> 0x04d4 }
        r11 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x04d4 }
        r11.<init>(r13);	 Catch:{ Exception -> 0x04d4 }
        r9.writePreviousMessageData(r4, r11);	 Catch:{ Exception -> 0x04d4 }
        r14 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x04d4 }
        r11 = r11.length();	 Catch:{ Exception -> 0x04d4 }
        r14.<init>(r11);	 Catch:{ Exception -> 0x04d4 }
        r9.writePreviousMessageData(r4, r14);	 Catch:{ Exception -> 0x04d4 }
        if (r30 != 0) goto L_0x0084;
    L_0x007e:
        r11 = new java.util.HashMap;	 Catch:{ Exception -> 0x04d4 }
        r11.<init>();	 Catch:{ Exception -> 0x04d4 }
        goto L_0x0086;
    L_0x0084:
        r11 = r30;
    L_0x0086:
        r15 = "prevMedia";
        r13 = r14.toByteArray();	 Catch:{ Exception -> 0x04d4 }
        r13 = android.util.Base64.encodeToString(r13, r5);	 Catch:{ Exception -> 0x04d4 }
        r11.put(r15, r13);	 Catch:{ Exception -> 0x04d4 }
        r14.cleanup();	 Catch:{ Exception -> 0x04d4 }
        if (r0 == 0) goto L_0x00da;
    L_0x0098:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x04d4 }
        r13.<init>();	 Catch:{ Exception -> 0x04d4 }
        r4.media = r13;	 Catch:{ Exception -> 0x04d4 }
        r13 = r4.media;	 Catch:{ Exception -> 0x04d4 }
        r14 = r13.flags;	 Catch:{ Exception -> 0x04d4 }
        r15 = 3;
        r14 = r14 | r15;
        r13.flags = r14;	 Catch:{ Exception -> 0x04d4 }
        r13 = r4.media;	 Catch:{ Exception -> 0x04d4 }
        r13.photo = r0;	 Catch:{ Exception -> 0x04d4 }
        if (r2 == 0) goto L_0x00bc;
    L_0x00ad:
        r13 = r29.length();	 Catch:{ Exception -> 0x04d4 }
        if (r13 <= 0) goto L_0x00bc;
    L_0x00b3:
        r13 = r2.startsWith(r12);	 Catch:{ Exception -> 0x04d4 }
        if (r13 == 0) goto L_0x00bc;
    L_0x00b9:
        r4.attachPath = r2;	 Catch:{ Exception -> 0x04d4 }
        goto L_0x00d8;
    L_0x00bc:
        r13 = r0.sizes;	 Catch:{ Exception -> 0x04d4 }
        r14 = r0.sizes;	 Catch:{ Exception -> 0x04d4 }
        r14 = r14.size();	 Catch:{ Exception -> 0x04d4 }
        r15 = 1;
        r14 = r14 - r15;
        r13 = r13.get(r14);	 Catch:{ Exception -> 0x04d4 }
        r13 = (org.telegram.tgnet.TLRPC.PhotoSize) r13;	 Catch:{ Exception -> 0x04d4 }
        r13 = r13.location;	 Catch:{ Exception -> 0x04d4 }
        r13 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r15);	 Catch:{ Exception -> 0x04d4 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x04d4 }
        r4.attachPath = r13;	 Catch:{ Exception -> 0x04d4 }
    L_0x00d8:
        r13 = 2;
        goto L_0x010a;
    L_0x00da:
        if (r1 == 0) goto L_0x0109;
    L_0x00dc:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x04d4 }
        r13.<init>();	 Catch:{ Exception -> 0x04d4 }
        r4.media = r13;	 Catch:{ Exception -> 0x04d4 }
        r13 = r4.media;	 Catch:{ Exception -> 0x04d4 }
        r14 = r13.flags;	 Catch:{ Exception -> 0x04d4 }
        r15 = 3;
        r14 = r14 | r15;
        r13.flags = r14;	 Catch:{ Exception -> 0x04d4 }
        r13 = r4.media;	 Catch:{ Exception -> 0x04d4 }
        r13.document = r1;	 Catch:{ Exception -> 0x04d4 }
        r13 = org.telegram.messenger.MessageObject.isVideoDocument(r28);	 Catch:{ Exception -> 0x04d4 }
        if (r13 != 0) goto L_0x00fa;
    L_0x00f5:
        if (r27 == 0) goto L_0x00f8;
    L_0x00f7:
        goto L_0x00fa;
    L_0x00f8:
        r13 = 7;
        goto L_0x00fb;
    L_0x00fa:
        r13 = 3;
    L_0x00fb:
        if (r27 == 0) goto L_0x0106;
    L_0x00fd:
        r14 = r27.getString();	 Catch:{ Exception -> 0x04d4 }
        r15 = "ve";
        r11.put(r15, r14);	 Catch:{ Exception -> 0x04d4 }
    L_0x0106:
        r4.attachPath = r2;	 Catch:{ Exception -> 0x04d4 }
        goto L_0x010a;
    L_0x0109:
        r13 = -1;
    L_0x010a:
        r4.params = r11;	 Catch:{ Exception -> 0x04d4 }
        r14 = 3;
        r4.send_state = r14;	 Catch:{ Exception -> 0x04d4 }
        r15 = r2;
        r14 = r11;
        r11 = r27;
    L_0x0113:
        r2 = r4.attachPath;	 Catch:{ Exception -> 0x04d4 }
        if (r2 != 0) goto L_0x011b;
    L_0x0117:
        r2 = "";
        r4.attachPath = r2;	 Catch:{ Exception -> 0x04d4 }
    L_0x011b:
        r4.local_id = r5;	 Catch:{ Exception -> 0x04d4 }
        r2 = r10.type;	 Catch:{ Exception -> 0x04d4 }
        r5 = 3;
        if (r2 == r5) goto L_0x0129;
    L_0x0122:
        if (r11 != 0) goto L_0x0129;
    L_0x0124:
        r2 = r10.type;	 Catch:{ Exception -> 0x04d4 }
        r5 = 2;
        if (r2 != r5) goto L_0x0134;
    L_0x0129:
        r2 = r4.attachPath;	 Catch:{ Exception -> 0x04d4 }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x04d4 }
        if (r2 != 0) goto L_0x0134;
    L_0x0131:
        r2 = 1;
        r10.attachPathExists = r2;	 Catch:{ Exception -> 0x04d4 }
    L_0x0134:
        r2 = r10.videoEditedInfo;	 Catch:{ Exception -> 0x04d4 }
        if (r2 == 0) goto L_0x013c;
    L_0x0138:
        if (r11 != 0) goto L_0x013c;
    L_0x013a:
        r11 = r10.videoEditedInfo;	 Catch:{ Exception -> 0x04d4 }
    L_0x013c:
        if (r31 != 0) goto L_0x01bb;
    L_0x013e:
        r5 = r10.editingMessage;	 Catch:{ Exception -> 0x04d4 }
        if (r5 == 0) goto L_0x0175;
    L_0x0142:
        r5 = r10.editingMessage;	 Catch:{ Exception -> 0x04d4 }
        r5 = r5.toString();	 Catch:{ Exception -> 0x04d4 }
        r4.message = r5;	 Catch:{ Exception -> 0x04d4 }
        r5 = r10.editingMessageEntities;	 Catch:{ Exception -> 0x04d4 }
        if (r5 == 0) goto L_0x0154;
    L_0x014e:
        r5 = r10.editingMessageEntities;	 Catch:{ Exception -> 0x04d4 }
        r4.entities = r5;	 Catch:{ Exception -> 0x04d4 }
    L_0x0152:
        r2 = 0;
        goto L_0x0170;
    L_0x0154:
        r5 = 1;
        r2 = new java.lang.CharSequence[r5];	 Catch:{ Exception -> 0x04d4 }
        r5 = r10.editingMessage;	 Catch:{ Exception -> 0x04d4 }
        r17 = 0;
        r2[r17] = r5;	 Catch:{ Exception -> 0x04d4 }
        r5 = r24.getMediaDataController();	 Catch:{ Exception -> 0x04d4 }
        r2 = r5.getEntities(r2);	 Catch:{ Exception -> 0x04d4 }
        if (r2 == 0) goto L_0x0152;
    L_0x0167:
        r5 = r2.isEmpty();	 Catch:{ Exception -> 0x04d4 }
        if (r5 != 0) goto L_0x0152;
    L_0x016d:
        r4.entities = r2;	 Catch:{ Exception -> 0x04d4 }
        goto L_0x0152;
    L_0x0170:
        r10.caption = r2;	 Catch:{ Exception -> 0x04d4 }
        r25.generateCaption();	 Catch:{ Exception -> 0x04d4 }
    L_0x0175:
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x04d4 }
        r2.<init>();	 Catch:{ Exception -> 0x04d4 }
        r2.add(r4);	 Catch:{ Exception -> 0x04d4 }
        r18 = r24.getMessagesStorage();	 Catch:{ Exception -> 0x04d4 }
        r20 = 0;
        r21 = 1;
        r22 = 0;
        r23 = 0;
        r19 = r2;
        r18.putMessages(r19, r20, r21, r22, r23);	 Catch:{ Exception -> 0x04d4 }
        r2 = -1;
        r10.type = r2;	 Catch:{ Exception -> 0x04d4 }
        r25.setType();	 Catch:{ Exception -> 0x04d4 }
        r25.createMessageSendInfo();	 Catch:{ Exception -> 0x04d4 }
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x04d4 }
        r2.<init>();	 Catch:{ Exception -> 0x04d4 }
        r2.add(r10);	 Catch:{ Exception -> 0x04d4 }
        r4 = r24.getNotificationCenter();	 Catch:{ Exception -> 0x04d4 }
        r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects;	 Catch:{ Exception -> 0x04d4 }
        r16 = r1;
        r27 = r11;
        r11 = 2;
        r1 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x04d4 }
        r11 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x04d4 }
        r17 = 0;
        r1[r17] = r11;	 Catch:{ Exception -> 0x04d4 }
        r11 = 1;
        r1[r11] = r2;	 Catch:{ Exception -> 0x04d4 }
        r4.postNotificationName(r5, r1);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x01bf;
    L_0x01bb:
        r16 = r1;
        r27 = r11;
    L_0x01bf:
        if (r14 == 0) goto L_0x01cf;
    L_0x01c1:
        r1 = r14.containsKey(r3);	 Catch:{ Exception -> 0x04d4 }
        if (r1 == 0) goto L_0x01cf;
    L_0x01c7:
        r1 = r14.get(r3);	 Catch:{ Exception -> 0x04d4 }
        r1 = (java.lang.String) r1;	 Catch:{ Exception -> 0x04d4 }
        r4 = r1;
        goto L_0x01d0;
    L_0x01cf:
        r4 = 0;
    L_0x01d0:
        r1 = 8;
        r2 = 1;
        if (r13 < r2) goto L_0x01d8;
    L_0x01d5:
        r2 = 3;
        if (r13 <= r2) goto L_0x01dd;
    L_0x01d8:
        r2 = 5;
        if (r13 < r2) goto L_0x04db;
    L_0x01db:
        if (r13 > r1) goto L_0x04db;
    L_0x01dd:
        r5 = 2;
        if (r13 != r5) goto L_0x0295;
    L_0x01e0:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x04d4 }
        r5.<init>();	 Catch:{ Exception -> 0x04d4 }
        if (r14 == 0) goto L_0x0220;
    L_0x01e7:
        r11 = "masks";
        r11 = r14.get(r11);	 Catch:{ Exception -> 0x04d4 }
        r11 = (java.lang.String) r11;	 Catch:{ Exception -> 0x04d4 }
        if (r11 == 0) goto L_0x0220;
    L_0x01f1:
        r14 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x04d4 }
        r11 = org.telegram.messenger.Utilities.hexToBytes(r11);	 Catch:{ Exception -> 0x04d4 }
        r14.<init>(r11);	 Catch:{ Exception -> 0x04d4 }
        r11 = 0;
        r1 = r14.readInt32(r11);	 Catch:{ Exception -> 0x04d4 }
        r2 = 0;
    L_0x0200:
        if (r2 >= r1) goto L_0x0217;
    L_0x0202:
        r3 = r5.stickers;	 Catch:{ Exception -> 0x04d4 }
        r27 = r1;
        r1 = r14.readInt32(r11);	 Catch:{ Exception -> 0x04d4 }
        r1 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r14, r1, r11);	 Catch:{ Exception -> 0x04d4 }
        r3.add(r1);	 Catch:{ Exception -> 0x04d4 }
        r2 = r2 + 1;
        r1 = r27;
        r11 = 0;
        goto L_0x0200;
    L_0x0217:
        r1 = r5.flags;	 Catch:{ Exception -> 0x04d4 }
        r2 = 1;
        r1 = r1 | r2;
        r5.flags = r1;	 Catch:{ Exception -> 0x04d4 }
        r14.cleanup();	 Catch:{ Exception -> 0x04d4 }
    L_0x0220:
        r1 = r0.access_hash;	 Catch:{ Exception -> 0x04d4 }
        r18 = 0;
        r3 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1));
        if (r3 != 0) goto L_0x022c;
    L_0x0228:
        r2 = r5;
        r3 = r13;
        r1 = 1;
        goto L_0x025a;
    L_0x022c:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x04d4 }
        r1.<init>();	 Catch:{ Exception -> 0x04d4 }
        r2 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x04d4 }
        r2.<init>();	 Catch:{ Exception -> 0x04d4 }
        r1.id = r2;	 Catch:{ Exception -> 0x04d4 }
        r2 = r1.id;	 Catch:{ Exception -> 0x04d4 }
        r3 = r13;
        r13 = r0.id;	 Catch:{ Exception -> 0x04d4 }
        r2.id = r13;	 Catch:{ Exception -> 0x04d4 }
        r2 = r1.id;	 Catch:{ Exception -> 0x04d4 }
        r13 = r0.access_hash;	 Catch:{ Exception -> 0x04d4 }
        r2.access_hash = r13;	 Catch:{ Exception -> 0x04d4 }
        r2 = r1.id;	 Catch:{ Exception -> 0x04d4 }
        r11 = r0.file_reference;	 Catch:{ Exception -> 0x04d4 }
        r2.file_reference = r11;	 Catch:{ Exception -> 0x04d4 }
        r2 = r1.id;	 Catch:{ Exception -> 0x04d4 }
        r2 = r2.file_reference;	 Catch:{ Exception -> 0x04d4 }
        if (r2 != 0) goto L_0x0258;
    L_0x0251:
        r2 = r1.id;	 Catch:{ Exception -> 0x04d4 }
        r11 = 0;
        r13 = new byte[r11];	 Catch:{ Exception -> 0x04d4 }
        r2.file_reference = r13;	 Catch:{ Exception -> 0x04d4 }
    L_0x0258:
        r2 = r1;
        r1 = 0;
    L_0x025a:
        r11 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x04d4 }
        r11.<init>(r6);	 Catch:{ Exception -> 0x04d4 }
        r13 = 0;
        r11.type = r13;	 Catch:{ Exception -> 0x04d4 }
        r11.obj = r10;	 Catch:{ Exception -> 0x04d4 }
        r11.originalPath = r4;	 Catch:{ Exception -> 0x04d4 }
        r11.parentObject = r8;	 Catch:{ Exception -> 0x04d4 }
        r11.inputUploadMedia = r5;	 Catch:{ Exception -> 0x04d4 }
        r11.performMediaUpload = r1;	 Catch:{ Exception -> 0x04d4 }
        if (r15 == 0) goto L_0x027d;
    L_0x026e:
        r5 = r15.length();	 Catch:{ Exception -> 0x04d4 }
        if (r5 <= 0) goto L_0x027d;
    L_0x0274:
        r5 = r15.startsWith(r12);	 Catch:{ Exception -> 0x04d4 }
        if (r5 == 0) goto L_0x027d;
    L_0x027a:
        r11.httpLocation = r15;	 Catch:{ Exception -> 0x04d4 }
        goto L_0x0291;
    L_0x027d:
        r5 = r0.sizes;	 Catch:{ Exception -> 0x04d4 }
        r12 = r0.sizes;	 Catch:{ Exception -> 0x04d4 }
        r12 = r12.size();	 Catch:{ Exception -> 0x04d4 }
        r13 = 1;
        r12 = r12 - r13;
        r5 = r5.get(r12);	 Catch:{ Exception -> 0x04d4 }
        r5 = (org.telegram.tgnet.TLRPC.PhotoSize) r5;	 Catch:{ Exception -> 0x04d4 }
        r11.photoSize = r5;	 Catch:{ Exception -> 0x04d4 }
        r11.locationParent = r0;	 Catch:{ Exception -> 0x04d4 }
    L_0x0291:
        r12 = r11;
        r11 = r1;
        goto L_0x03db;
    L_0x0295:
        r3 = r13;
        r0 = 3;
        if (r3 != r0) goto L_0x032b;
    L_0x0299:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x04d4 }
        r0.<init>();	 Catch:{ Exception -> 0x04d4 }
        r1 = r16;
        r2 = r1.mime_type;	 Catch:{ Exception -> 0x04d4 }
        r0.mime_type = r2;	 Catch:{ Exception -> 0x04d4 }
        r2 = r1.attributes;	 Catch:{ Exception -> 0x04d4 }
        r0.attributes = r2;	 Catch:{ Exception -> 0x04d4 }
        r2 = r25.isGif();	 Catch:{ Exception -> 0x04d4 }
        if (r2 != 0) goto L_0x02c6;
    L_0x02ae:
        if (r27 == 0) goto L_0x02b7;
    L_0x02b0:
        r11 = r27;
        r2 = r11.muted;	 Catch:{ Exception -> 0x04d4 }
        if (r2 != 0) goto L_0x02c8;
    L_0x02b6:
        goto L_0x02b9;
    L_0x02b7:
        r11 = r27;
    L_0x02b9:
        r2 = 1;
        r0.nosound_video = r2;	 Catch:{ Exception -> 0x04d4 }
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x04d4 }
        if (r2 == 0) goto L_0x02c8;
    L_0x02c0:
        r2 = "nosound_video = true";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x02c8;
    L_0x02c6:
        r11 = r27;
    L_0x02c8:
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x04d4 }
        r14 = 0;
        r2 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r2 != 0) goto L_0x02d3;
    L_0x02d0:
        r2 = r0;
        r5 = 1;
        goto L_0x02ff;
    L_0x02d3:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x04d4 }
        r2.<init>();	 Catch:{ Exception -> 0x04d4 }
        r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x04d4 }
        r5.<init>();	 Catch:{ Exception -> 0x04d4 }
        r2.id = r5;	 Catch:{ Exception -> 0x04d4 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04d4 }
        r12 = r1.id;	 Catch:{ Exception -> 0x04d4 }
        r5.id = r12;	 Catch:{ Exception -> 0x04d4 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04d4 }
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x04d4 }
        r5.access_hash = r12;	 Catch:{ Exception -> 0x04d4 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04d4 }
        r12 = r1.file_reference;	 Catch:{ Exception -> 0x04d4 }
        r5.file_reference = r12;	 Catch:{ Exception -> 0x04d4 }
        r5 = r2.id;	 Catch:{ Exception -> 0x04d4 }
        r5 = r5.file_reference;	 Catch:{ Exception -> 0x04d4 }
        if (r5 != 0) goto L_0x02fe;
    L_0x02f7:
        r5 = r2.id;	 Catch:{ Exception -> 0x04d4 }
        r12 = 0;
        r13 = new byte[r12];	 Catch:{ Exception -> 0x04d4 }
        r5.file_reference = r13;	 Catch:{ Exception -> 0x04d4 }
    L_0x02fe:
        r5 = 0;
    L_0x02ff:
        r12 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x04d4 }
        r12.<init>(r6);	 Catch:{ Exception -> 0x04d4 }
        r13 = 1;
        r12.type = r13;	 Catch:{ Exception -> 0x04d4 }
        r12.obj = r10;	 Catch:{ Exception -> 0x04d4 }
        r12.originalPath = r4;	 Catch:{ Exception -> 0x04d4 }
        r12.parentObject = r8;	 Catch:{ Exception -> 0x04d4 }
        r12.inputUploadMedia = r0;	 Catch:{ Exception -> 0x04d4 }
        r12.performMediaUpload = r5;	 Catch:{ Exception -> 0x04d4 }
        r0 = r1.thumbs;	 Catch:{ Exception -> 0x04d4 }
        r0 = r0.isEmpty();	 Catch:{ Exception -> 0x04d4 }
        if (r0 != 0) goto L_0x0326;
    L_0x0319:
        r0 = r1.thumbs;	 Catch:{ Exception -> 0x04d4 }
        r13 = 0;
        r0 = r0.get(r13);	 Catch:{ Exception -> 0x04d4 }
        r0 = (org.telegram.tgnet.TLRPC.PhotoSize) r0;	 Catch:{ Exception -> 0x04d4 }
        r12.photoSize = r0;	 Catch:{ Exception -> 0x04d4 }
        r12.locationParent = r1;	 Catch:{ Exception -> 0x04d4 }
    L_0x0326:
        r12.videoEditedInfo = r11;	 Catch:{ Exception -> 0x04d4 }
        r11 = r5;
        goto L_0x03db;
    L_0x032b:
        r1 = r16;
        r0 = 7;
        if (r3 != r0) goto L_0x03d8;
    L_0x0330:
        if (r4 == 0) goto L_0x0363;
    L_0x0332:
        r0 = r4.length();	 Catch:{ Exception -> 0x04d4 }
        if (r0 <= 0) goto L_0x0363;
    L_0x0338:
        r0 = r4.startsWith(r12);	 Catch:{ Exception -> 0x04d4 }
        if (r0 == 0) goto L_0x0363;
    L_0x033e:
        if (r14 == 0) goto L_0x0363;
    L_0x0340:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaGifExternal;	 Catch:{ Exception -> 0x04d4 }
        r0.<init>();	 Catch:{ Exception -> 0x04d4 }
        r2 = "url";
        r2 = r14.get(r2);	 Catch:{ Exception -> 0x04d4 }
        r2 = (java.lang.String) r2;	 Catch:{ Exception -> 0x04d4 }
        r5 = "\\|";
        r2 = r2.split(r5);	 Catch:{ Exception -> 0x04d4 }
        r5 = r2.length;	 Catch:{ Exception -> 0x04d4 }
        r11 = 2;
        if (r5 != r11) goto L_0x0361;
    L_0x0357:
        r5 = 0;
        r11 = r2[r5];	 Catch:{ Exception -> 0x04d4 }
        r0.url = r11;	 Catch:{ Exception -> 0x04d4 }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ Exception -> 0x04d4 }
        r0.q = r2;	 Catch:{ Exception -> 0x04d4 }
    L_0x0361:
        r2 = 1;
        goto L_0x0369;
    L_0x0363:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x04d4 }
        r0.<init>();	 Catch:{ Exception -> 0x04d4 }
        r2 = 0;
    L_0x0369:
        r5 = r1.mime_type;	 Catch:{ Exception -> 0x04d4 }
        r0.mime_type = r5;	 Catch:{ Exception -> 0x04d4 }
        r5 = r1.attributes;	 Catch:{ Exception -> 0x04d4 }
        r0.attributes = r5;	 Catch:{ Exception -> 0x04d4 }
        r11 = r1.access_hash;	 Catch:{ Exception -> 0x04d4 }
        r13 = 0;
        r5 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r5 != 0) goto L_0x037e;
    L_0x0379:
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x04d4 }
        r11 = r5;
        r5 = r0;
        goto L_0x03aa;
    L_0x037e:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x04d4 }
        r5.<init>();	 Catch:{ Exception -> 0x04d4 }
        r11 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x04d4 }
        r11.<init>();	 Catch:{ Exception -> 0x04d4 }
        r5.id = r11;	 Catch:{ Exception -> 0x04d4 }
        r11 = r5.id;	 Catch:{ Exception -> 0x04d4 }
        r12 = r1.id;	 Catch:{ Exception -> 0x04d4 }
        r11.id = r12;	 Catch:{ Exception -> 0x04d4 }
        r11 = r5.id;	 Catch:{ Exception -> 0x04d4 }
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x04d4 }
        r11.access_hash = r12;	 Catch:{ Exception -> 0x04d4 }
        r11 = r5.id;	 Catch:{ Exception -> 0x04d4 }
        r12 = r1.file_reference;	 Catch:{ Exception -> 0x04d4 }
        r11.file_reference = r12;	 Catch:{ Exception -> 0x04d4 }
        r11 = r5.id;	 Catch:{ Exception -> 0x04d4 }
        r11 = r11.file_reference;	 Catch:{ Exception -> 0x04d4 }
        if (r11 != 0) goto L_0x03a9;
    L_0x03a2:
        r11 = r5.id;	 Catch:{ Exception -> 0x04d4 }
        r12 = 0;
        r13 = new byte[r12];	 Catch:{ Exception -> 0x04d4 }
        r11.file_reference = r13;	 Catch:{ Exception -> 0x04d4 }
    L_0x03a9:
        r11 = 0;
    L_0x03aa:
        if (r2 != 0) goto L_0x03d6;
    L_0x03ac:
        r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x04d4 }
        r2.<init>(r6);	 Catch:{ Exception -> 0x04d4 }
        r2.originalPath = r4;	 Catch:{ Exception -> 0x04d4 }
        r12 = 2;
        r2.type = r12;	 Catch:{ Exception -> 0x04d4 }
        r2.obj = r10;	 Catch:{ Exception -> 0x04d4 }
        r12 = r1.thumbs;	 Catch:{ Exception -> 0x04d4 }
        r12 = r12.isEmpty();	 Catch:{ Exception -> 0x04d4 }
        if (r12 != 0) goto L_0x03cd;
    L_0x03c0:
        r12 = r1.thumbs;	 Catch:{ Exception -> 0x04d4 }
        r13 = 0;
        r12 = r12.get(r13);	 Catch:{ Exception -> 0x04d4 }
        r12 = (org.telegram.tgnet.TLRPC.PhotoSize) r12;	 Catch:{ Exception -> 0x04d4 }
        r2.photoSize = r12;	 Catch:{ Exception -> 0x04d4 }
        r2.locationParent = r1;	 Catch:{ Exception -> 0x04d4 }
    L_0x03cd:
        r2.parentObject = r8;	 Catch:{ Exception -> 0x04d4 }
        r2.inputUploadMedia = r0;	 Catch:{ Exception -> 0x04d4 }
        r2.performMediaUpload = r11;	 Catch:{ Exception -> 0x04d4 }
        r12 = r2;
        r2 = r5;
        goto L_0x03db;
    L_0x03d6:
        r2 = r5;
        goto L_0x03da;
    L_0x03d8:
        r2 = 0;
        r11 = 0;
    L_0x03da:
        r12 = 0;
    L_0x03db:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage;	 Catch:{ Exception -> 0x04d4 }
        r0.<init>();	 Catch:{ Exception -> 0x04d4 }
        r1 = r25.getId();	 Catch:{ Exception -> 0x04d4 }
        r0.id = r1;	 Catch:{ Exception -> 0x04d4 }
        r1 = r24.getMessagesController();	 Catch:{ Exception -> 0x04d4 }
        r5 = (int) r6;	 Catch:{ Exception -> 0x04d4 }
        r1 = r1.getInputPeer(r5);	 Catch:{ Exception -> 0x04d4 }
        r0.peer = r1;	 Catch:{ Exception -> 0x04d4 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04d4 }
        r1 = r1 | 16384;
        r0.flags = r1;	 Catch:{ Exception -> 0x04d4 }
        r0.media = r2;	 Catch:{ Exception -> 0x04d4 }
        r1 = r10.editingMessage;	 Catch:{ Exception -> 0x04d4 }
        if (r1 == 0) goto L_0x0442;
    L_0x03fd:
        r1 = r10.editingMessage;	 Catch:{ Exception -> 0x04d4 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x04d4 }
        r0.message = r1;	 Catch:{ Exception -> 0x04d4 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04d4 }
        r1 = r1 | 2048;
        r0.flags = r1;	 Catch:{ Exception -> 0x04d4 }
        r1 = r10.editingMessageEntities;	 Catch:{ Exception -> 0x04d4 }
        if (r1 == 0) goto L_0x041c;
    L_0x040f:
        r1 = r10.editingMessageEntities;	 Catch:{ Exception -> 0x04d4 }
        r0.entities = r1;	 Catch:{ Exception -> 0x04d4 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04d4 }
        r2 = 8;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x04d4 }
    L_0x041a:
        r1 = 0;
        goto L_0x043e;
    L_0x041c:
        r1 = 1;
        r2 = new java.lang.CharSequence[r1];	 Catch:{ Exception -> 0x04d4 }
        r1 = r10.editingMessage;	 Catch:{ Exception -> 0x04d4 }
        r5 = 0;
        r2[r5] = r1;	 Catch:{ Exception -> 0x04d4 }
        r1 = r24.getMediaDataController();	 Catch:{ Exception -> 0x04d4 }
        r1 = r1.getEntities(r2);	 Catch:{ Exception -> 0x04d4 }
        if (r1 == 0) goto L_0x041a;
    L_0x042e:
        r2 = r1.isEmpty();	 Catch:{ Exception -> 0x04d4 }
        if (r2 != 0) goto L_0x041a;
    L_0x0434:
        r0.entities = r1;	 Catch:{ Exception -> 0x04d4 }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04d4 }
        r2 = 8;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x04d4 }
        goto L_0x041a;
    L_0x043e:
        r10.editingMessage = r1;	 Catch:{ Exception -> 0x04d4 }
        r10.editingMessageEntities = r1;	 Catch:{ Exception -> 0x04d4 }
    L_0x0442:
        if (r12 == 0) goto L_0x0446;
    L_0x0444:
        r12.sendRequest = r0;	 Catch:{ Exception -> 0x04d4 }
    L_0x0446:
        r1 = 1;
        if (r3 != r1) goto L_0x045b;
    L_0x0449:
        r1 = 0;
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r1;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x045b:
        r1 = 2;
        if (r3 != r1) goto L_0x0474;
    L_0x045e:
        if (r11 == 0) goto L_0x0465;
    L_0x0460:
        r9.performSendDelayedMessage(r12);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x0465:
        r5 = 0;
        r6 = 1;
        r1 = r24;
        r2 = r0;
        r3 = r25;
        r7 = r12;
        r8 = r32;
        r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x0474:
        r1 = 3;
        if (r3 != r1) goto L_0x048e;
    L_0x0477:
        if (r11 == 0) goto L_0x047e;
    L_0x0479:
        r9.performSendDelayedMessage(r12);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x047e:
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r4;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x048e:
        r1 = 6;
        if (r3 != r1) goto L_0x04a1;
    L_0x0491:
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r4;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x04a1:
        r1 = 7;
        if (r3 != r1) goto L_0x04ba;
    L_0x04a4:
        if (r11 == 0) goto L_0x04aa;
    L_0x04a6:
        r9.performSendDelayedMessage(r12);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x04aa:
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r4;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x04ba:
        r1 = 8;
        if (r3 != r1) goto L_0x04db;
    L_0x04be:
        if (r11 == 0) goto L_0x04c4;
    L_0x04c0:
        r9.performSendDelayedMessage(r12);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x04c4:
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r4;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04d4 }
        goto L_0x04db;
    L_0x04d4:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r24.revertEditingMessageObject(r25);
    L_0x04db:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessageMedia(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, BaseFragment baseFragment, ArrayList<MessageEntity> arrayList, Runnable runnable) {
        if (baseFragment == null || baseFragment.getParentActivity() == null || runnable == null) {
            return 0;
        }
        TL_messages_editMessage tL_messages_editMessage = new TL_messages_editMessage();
        tL_messages_editMessage.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
        tL_messages_editMessage.message = str;
        tL_messages_editMessage.flags |= 2048;
        tL_messages_editMessage.id = messageObject.getId();
        tL_messages_editMessage.no_webpage = z ^ 1;
        if (arrayList != null) {
            tL_messages_editMessage.entities = arrayList;
            tL_messages_editMessage.flags |= 8;
        }
        return getConnectionsManager().sendRequest(tL_messages_editMessage, new -$$Lambda$SendMessagesHelper$0k3RdsQSyxpPqVyuBg3ZosCuce8(this, baseFragment, tL_messages_editMessage, runnable));
    }

    public /* synthetic */ void lambda$editMessage$11$SendMessagesHelper(BaseFragment baseFragment, TL_messages_editMessage tL_messages_editMessage, Runnable runnable, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((Updates) tLObject, false);
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$ag0T9ipOWU0deF9XfslwNyap3gA(this, tL_error, baseFragment, tL_messages_editMessage));
        }
        AndroidUtilities.runOnUIThread(runnable);
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
            sendMessage(tL_messageMediaGeo, messageObject.getDialogId(), messageObject, null, null);
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
        getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, true);
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
        r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$mrT1SKD1wBToK3yGrtE1zSzHHGY;
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

    public /* synthetic */ void lambda$sendCallback$17$SendMessagesHelper(String str, boolean z, MessageObject messageObject, KeyboardButton keyboardButton, ChatActivity chatActivity, TLObject[] tLObjectArr, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$4H8dDsDRj446uqFYh4DKm4UlDEU(this, str, z, tLObject, messageObject, keyboardButton, chatActivity, tLObjectArr));
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
    public /* synthetic */ void lambda$null$16$SendMessagesHelper(java.lang.String r12, boolean r13, org.telegram.tgnet.TLObject r14, org.telegram.messenger.MessageObject r15, org.telegram.tgnet.TLRPC.KeyboardButton r16, org.telegram.ui.ChatActivity r17, org.telegram.tgnet.TLObject[] r18) {
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
        r1 = NUM; // 0x7f0d06da float:1.8745672E38 double:1.053130644E-314;
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$16$SendMessagesHelper(java.lang.String, boolean, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity, org.telegram.tgnet.TLObject[]):void");
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
            if (tL_messages_sendMedia.peer instanceof TL_inputPeerChannel) {
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("silent_");
                stringBuilder.append(inputPeer.channel_id);
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
                    getConnectionsManager().sendRequest(tL_messages_sendMedia, new -$$Lambda$SendMessagesHelper$ydKQSIv3UJTKVK2ad12P2kFNfXM(this, j2));
                }
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tL_messages_sendMedia, new -$$Lambda$SendMessagesHelper$ydKQSIv3UJTKVK2ad12P2kFNfXM(this, j2));
        }
    }

    public /* synthetic */ void lambda$sendGame$18$SendMessagesHelper(long j, TLObject tLObject, TL_error tL_error) {
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
        Message message = messageObject.messageOwner;
        sendMessage(null, null, null, null, null, null, null, null, null, dialogId, message.attachPath, null, null, true, messageObject2, null, message.reply_markup, message.params, 0, null);
    }

    public void sendMessage(User user, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(null, null, null, null, null, user, null, null, null, j, null, messageObject, null, true, null, null, replyMarkup, hashMap, 0, null);
    }

    public void sendMessage(TL_document tL_document, VideoEditedInfo videoEditedInfo, String str, long j, MessageObject messageObject, String str2, ArrayList<MessageEntity> arrayList, ReplyMarkup replyMarkup, HashMap<String, String> hashMap, int i, Object obj) {
        String str3 = str2;
        sendMessage(null, str3, null, null, videoEditedInfo, null, tL_document, null, null, j, str, messageObject, null, true, null, arrayList, replyMarkup, hashMap, i, obj);
    }

    public void sendMessage(String str, long j, MessageObject messageObject, WebPage webPage, boolean z, ArrayList<MessageEntity> arrayList, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(str, null, null, null, null, null, null, null, null, j, null, messageObject, webPage, z, null, arrayList, replyMarkup, hashMap, 0, null);
    }

    public void sendMessage(MessageMedia messageMedia, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(null, null, messageMedia, null, null, null, null, null, null, j, null, messageObject, null, true, null, null, replyMarkup, hashMap, 0, null);
    }

    public void sendMessage(TL_messageMediaPoll tL_messageMediaPoll, long j, MessageObject messageObject, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(null, null, null, null, null, null, null, null, tL_messageMediaPoll, j, null, messageObject, null, true, null, null, replyMarkup, hashMap, 0, null);
    }

    public void sendMessage(TL_game tL_game, long j, ReplyMarkup replyMarkup, HashMap<String, String> hashMap) {
        sendMessage(null, null, null, null, null, null, null, tL_game, null, j, null, null, null, true, null, null, replyMarkup, hashMap, 0, null);
    }

    public void sendMessage(TL_photo tL_photo, String str, long j, MessageObject messageObject, String str2, ArrayList<MessageEntity> arrayList, ReplyMarkup replyMarkup, HashMap<String, String> hashMap, int i, Object obj) {
        String str3 = str2;
        sendMessage(null, str3, null, tL_photo, null, null, null, null, null, j, str, messageObject, null, true, null, arrayList, replyMarkup, hashMap, i, obj);
    }

    /* JADX WARNING: Removed duplicated region for block: B:97:0x01d4  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01cf A:{Catch:{ Exception -> 0x01eb }} */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0410 A:{Catch:{ Exception -> 0x03db }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x043c A:{Catch:{ Exception -> 0x04c1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0410 A:{Catch:{ Exception -> 0x03db }} */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x0423 A:{Catch:{ Exception -> 0x03db }} */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x043c A:{Catch:{ Exception -> 0x04c1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x04ce A:{SYNTHETIC, Splitter:B:300:0x04ce} */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x04eb A:{SYNTHETIC, Splitter:B:309:0x04eb} */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x04e8 A:{Catch:{ Exception -> 0x04dd }} */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x04f5 A:{SYNTHETIC, Splitter:B:317:0x04f5} */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0bb9 A:{Catch:{ Exception -> 0x0eef }} */
    /* JADX WARNING: Removed duplicated region for block: B:677:0x0bb0 A:{Catch:{ Exception -> 0x0eef }} */
    /* JADX WARNING: Removed duplicated region for block: B:994:0x1449 A:{SYNTHETIC, Splitter:B:994:0x1449} */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1492 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:998:0x1450 A:{SYNTHETIC, Splitter:B:998:0x1450} */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x1498 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:870:0x106b A:{Catch:{ Exception -> 0x0eef }} */
    /* JADX WARNING: Removed duplicated region for block: B:876:0x109d A:{Catch:{ Exception -> 0x0eef }} */
    /* JADX WARNING: Removed duplicated region for block: B:888:0x110a A:{Catch:{ Exception -> 0x0eef }} */
    /* JADX WARNING: Removed duplicated region for block: B:881:0x10dc A:{Catch:{ Exception -> 0x0eef }} */
    /* JADX WARNING: Removed duplicated region for block: B:891:0x1118 A:{Catch:{ Exception -> 0x0eef }} */
    /* JADX WARNING: Removed duplicated region for block: B:998:0x1450 A:{SYNTHETIC, Splitter:B:998:0x1450} */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1492 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x1498 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1492 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:998:0x1450 A:{SYNTHETIC, Splitter:B:998:0x1450} */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x1498 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x086f  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x082b A:{SYNTHETIC, Splitter:B:510:0x082b} */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x08f3 A:{Catch:{ Exception -> 0x0898 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1084:0x16d9 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1059:0x1619 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x07a8 A:{Catch:{ Exception -> 0x06df }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x07cb A:{SYNTHETIC, Splitter:B:480:0x07cb} */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x0822  */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x07f5 A:{Catch:{ Exception -> 0x07e3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x082b A:{SYNTHETIC, Splitter:B:510:0x082b} */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x086f  */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x08f3 A:{Catch:{ Exception -> 0x0898 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1059:0x1619 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1084:0x16d9 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01fb  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00cb A:{SYNTHETIC, Splitter:B:41:0x00cb} */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x054b A:{SYNTHETIC, Splitter:B:338:0x054b} */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0555 A:{SYNTHETIC, Splitter:B:342:0x0555} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x058a A:{SYNTHETIC, Splitter:B:354:0x058a} */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x05d8  */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x059c  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x0616 A:{Catch:{ Exception -> 0x0266 }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x05ec  */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x06e2  */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x0628  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x07a8 A:{Catch:{ Exception -> 0x06df }} */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x07cb A:{SYNTHETIC, Splitter:B:480:0x07cb} */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x07ec A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x07f5 A:{Catch:{ Exception -> 0x07e3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:506:0x0822  */
    /* JADX WARNING: Removed duplicated region for block: B:519:0x086f  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x082b A:{SYNTHETIC, Splitter:B:510:0x082b} */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x08f3 A:{Catch:{ Exception -> 0x0898 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1084:0x16d9 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1059:0x1619 A:{Catch:{ Exception -> 0x1778 }} */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Removed duplicated region for block: B:1128:0x17aa  */
    /* JADX WARNING: Missing block: B:189:0x0324, code skipped:
            if (r12.containsKey(r1) != false) goto L_0x0282;
     */
    /* JADX WARNING: Missing block: B:267:0x0446, code skipped:
            if (org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r44) != false) goto L_0x0448;
     */
    /* JADX WARNING: Missing block: B:356:0x058c, code skipped:
            if (r14.resendAsIs == false) goto L_0x058e;
     */
    private void sendMessage(java.lang.String r38, java.lang.String r39, org.telegram.tgnet.TLRPC.MessageMedia r40, org.telegram.tgnet.TLRPC.TL_photo r41, org.telegram.messenger.VideoEditedInfo r42, org.telegram.tgnet.TLRPC.User r43, org.telegram.tgnet.TLRPC.TL_document r44, org.telegram.tgnet.TLRPC.TL_game r45, org.telegram.tgnet.TLRPC.TL_messageMediaPoll r46, long r47, java.lang.String r49, org.telegram.messenger.MessageObject r50, org.telegram.tgnet.TLRPC.WebPage r51, boolean r52, org.telegram.messenger.MessageObject r53, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r54, org.telegram.tgnet.TLRPC.ReplyMarkup r55, java.util.HashMap<java.lang.String, java.lang.String> r56, int r57, java.lang.Object r58) {
        /*
        r37 = this;
        r1 = r37;
        r2 = r38;
        r3 = r40;
        r4 = r41;
        r5 = r43;
        r6 = r44;
        r7 = r45;
        r8 = r46;
        r9 = r47;
        r11 = r49;
        r12 = r50;
        r13 = r51;
        r14 = r53;
        r15 = r54;
        r12 = r56;
        r6 = r57;
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
        if (r2 != 0) goto L_0x0039;
    L_0x0032:
        if (r39 != 0) goto L_0x0039;
    L_0x0034:
        r18 = r7;
        r19 = r18;
        goto L_0x003d;
    L_0x0039:
        r18 = r39;
        r19 = r7;
    L_0x003d:
        if (r12 == 0) goto L_0x0050;
    L_0x003f:
        r7 = "originalPath";
        r7 = r12.containsKey(r7);
        if (r7 == 0) goto L_0x0050;
    L_0x0047:
        r7 = "originalPath";
        r7 = r12.get(r7);
        r7 = (java.lang.String) r7;
        goto L_0x0051;
    L_0x0050:
        r7 = 0;
    L_0x0051:
        r20 = -1;
        r21 = r7;
        r7 = (int) r9;
        r22 = 32;
        r5 = r9 >> r22;
        r6 = (int) r5;
        if (r7 == 0) goto L_0x0066;
    L_0x005d:
        r5 = r37.getMessagesController();
        r5 = r5.getInputPeer(r7);
        goto L_0x0067;
    L_0x0066:
        r5 = 0;
    L_0x0067:
        if (r7 != 0) goto L_0x00a6;
    L_0x0069:
        r10 = r37.getMessagesController();
        r9 = java.lang.Integer.valueOf(r6);
        r9 = r10.getEncryptedChat(r9);
        if (r9 != 0) goto L_0x00c4;
    L_0x0077:
        if (r14 == 0) goto L_0x00a5;
    L_0x0079:
        r2 = r37.getMessagesStorage();
        r3 = r14.messageOwner;
        r2.markMessageAsSendError(r3);
        r2 = r14.messageOwner;
        r3 = 2;
        r2.send_state = r3;
        r2 = r37.getNotificationCenter();
        r3 = org.telegram.messenger.NotificationCenter.messageSendError;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = r53.getId();
        r5 = java.lang.Integer.valueOf(r5);
        r6 = 0;
        r4[r6] = r5;
        r2.postNotificationName(r3, r4);
        r2 = r53.getId();
        r1.processSentMessage(r2);
    L_0x00a5:
        return;
    L_0x00a6:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r9 == 0) goto L_0x00c3;
    L_0x00aa:
        r9 = r37.getMessagesController();
        r10 = r5.channel_id;
        r10 = java.lang.Integer.valueOf(r10);
        r9 = r9.getChat(r10);
        if (r9 == 0) goto L_0x00c0;
    L_0x00ba:
        r9 = r9.megagroup;
        if (r9 != 0) goto L_0x00c0;
    L_0x00be:
        r10 = 1;
        goto L_0x00c1;
    L_0x00c0:
        r10 = 0;
    L_0x00c1:
        r9 = 0;
        goto L_0x00c5;
    L_0x00c3:
        r9 = 0;
    L_0x00c4:
        r10 = 0;
    L_0x00c5:
        r1 = "query_id";
        r24 = r6;
        if (r14 == 0) goto L_0x01fb;
    L_0x00cb:
        r6 = r14.messageOwner;	 Catch:{ Exception -> 0x01f3 }
        r25 = r53.isForwarded();	 Catch:{ Exception -> 0x01eb }
        if (r25 == 0) goto L_0x00ec;
    L_0x00d3:
        r15 = r43;
        r38 = r2;
        r25 = r7;
        r11 = r8;
        r28 = r13;
        r45 = r18;
        r18 = r44;
        r2 = r57;
        r8 = r3;
        r13 = r4;
        r7 = r6;
        r6 = r19;
        r19 = r1;
        r1 = 4;
        goto L_0x0545;
    L_0x00ec:
        r25 = r7;
        r7 = r14.type;	 Catch:{ Exception -> 0x01eb }
        if (r7 != 0) goto L_0x0107;
    L_0x00f2:
        r7 = r14.messageOwner;	 Catch:{ Exception -> 0x01eb }
        r7 = r7.media;	 Catch:{ Exception -> 0x01eb }
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;	 Catch:{ Exception -> 0x01eb }
        if (r7 == 0) goto L_0x00fb;
    L_0x00fa:
        goto L_0x00fd;
    L_0x00fb:
        r2 = r6.message;	 Catch:{ Exception -> 0x01eb }
    L_0x00fd:
        r7 = r44;
        r26 = r5;
        r20 = 0;
    L_0x0103:
        r5 = r43;
        goto L_0x01ba;
    L_0x0107:
        r7 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r26 = r5;
        r5 = 4;
        if (r7 != r5) goto L_0x0118;
    L_0x010e:
        r3 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r5 = r43;
        r7 = r44;
        r20 = 1;
        goto L_0x01ba;
    L_0x0118:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 1;
        if (r5 != r7) goto L_0x012b;
    L_0x011d:
        r4 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r4 = r4.photo;	 Catch:{ Exception -> 0x01eb }
        r4 = (org.telegram.tgnet.TLRPC.TL_photo) r4;	 Catch:{ Exception -> 0x01eb }
        r5 = r43;
        r7 = r44;
        r20 = 2;
        goto L_0x01ba;
    L_0x012b:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 3;
        if (r5 == r7) goto L_0x01af;
    L_0x0130:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 5;
        if (r5 == r7) goto L_0x01af;
    L_0x0135:
        if (r42 == 0) goto L_0x0139;
    L_0x0137:
        goto L_0x01af;
    L_0x0139:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 12;
        if (r5 != r7) goto L_0x0165;
    L_0x013f:
        r5 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2;	 Catch:{ Exception -> 0x01eb }
        r5.<init>();	 Catch:{ Exception -> 0x01eb }
        r7 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r7 = r7.phone_number;	 Catch:{ Exception -> 0x01eb }
        r5.phone = r7;	 Catch:{ Exception -> 0x01eb }
        r7 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r7 = r7.first_name;	 Catch:{ Exception -> 0x01eb }
        r5.first_name = r7;	 Catch:{ Exception -> 0x01eb }
        r7 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r7 = r7.last_name;	 Catch:{ Exception -> 0x01eb }
        r5.last_name = r7;	 Catch:{ Exception -> 0x01eb }
        r7 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r7 = r7.vcard;	 Catch:{ Exception -> 0x01eb }
        r5.restriction_reason = r7;	 Catch:{ Exception -> 0x01eb }
        r7 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r7 = r7.user_id;	 Catch:{ Exception -> 0x01eb }
        r5.id = r7;	 Catch:{ Exception -> 0x01eb }
        r20 = 6;
        goto L_0x01a1;
    L_0x0165:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 8;
        if (r5 == r7) goto L_0x01a4;
    L_0x016b:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 9;
        if (r5 == r7) goto L_0x01a4;
    L_0x0171:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 13;
        if (r5 == r7) goto L_0x01a4;
    L_0x0177:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 14;
        if (r5 != r7) goto L_0x017e;
    L_0x017d:
        goto L_0x01a4;
    L_0x017e:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 2;
        if (r5 != r7) goto L_0x018e;
    L_0x0183:
        r5 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r5 = r5.document;	 Catch:{ Exception -> 0x01eb }
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;	 Catch:{ Exception -> 0x01eb }
        r7 = r5;
        r20 = 8;
        goto L_0x0103;
    L_0x018e:
        r5 = r14.type;	 Catch:{ Exception -> 0x01eb }
        r7 = 17;
        if (r5 != r7) goto L_0x019f;
    L_0x0194:
        r5 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;	 Catch:{ Exception -> 0x01eb }
        r7 = r44;
        r8 = r5;
        r20 = 10;
        goto L_0x0103;
    L_0x019f:
        r5 = r43;
    L_0x01a1:
        r7 = r44;
        goto L_0x01ba;
    L_0x01a4:
        r5 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r5 = r5.document;	 Catch:{ Exception -> 0x01eb }
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;	 Catch:{ Exception -> 0x01eb }
        r7 = r5;
        r20 = 7;
        goto L_0x0103;
    L_0x01af:
        r5 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r5 = r5.document;	 Catch:{ Exception -> 0x01eb }
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;	 Catch:{ Exception -> 0x01eb }
        r7 = r5;
        r20 = 3;
        goto L_0x0103;
    L_0x01ba:
        if (r12 == 0) goto L_0x01c7;
    L_0x01bc:
        r27 = r12.containsKey(r1);	 Catch:{ Exception -> 0x01eb }
        if (r27 == 0) goto L_0x01c7;
    L_0x01c2:
        r38 = r2;
        r20 = 9;
        goto L_0x01c9;
    L_0x01c7:
        r38 = r2;
    L_0x01c9:
        r2 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r2 = r2.ttl_seconds;	 Catch:{ Exception -> 0x01eb }
        if (r2 <= 0) goto L_0x01d4;
    L_0x01cf:
        r2 = r6.media;	 Catch:{ Exception -> 0x01eb }
        r2 = r2.ttl_seconds;	 Catch:{ Exception -> 0x01eb }
        goto L_0x01d6;
    L_0x01d4:
        r2 = r57;
    L_0x01d6:
        r15 = r5;
        r11 = r8;
        r28 = r13;
        r45 = r18;
        r5 = r26;
        r8 = r3;
        r13 = r4;
        r18 = r7;
        r7 = r6;
        r6 = r19;
        r19 = r1;
        r1 = r20;
        goto L_0x0545;
    L_0x01eb:
        r0 = move-exception;
        r15 = r37;
        r1 = r0;
        r7 = r6;
    L_0x01f0:
        r11 = 0;
        goto L_0x179e;
    L_0x01f3:
        r0 = move-exception;
        r7 = 0;
    L_0x01f5:
        r11 = 0;
        r15 = r37;
    L_0x01f8:
        r1 = r0;
        goto L_0x179e;
    L_0x01fb:
        r26 = r5;
        r25 = r7;
        if (r2 == 0) goto L_0x024b;
    L_0x0201:
        if (r9 == 0) goto L_0x0209;
    L_0x0203:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
        goto L_0x020e;
    L_0x0209:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
    L_0x020e:
        r7 = r5;
        if (r9 == 0) goto L_0x0225;
    L_0x0211:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending;	 Catch:{ Exception -> 0x0266 }
        if (r5 == 0) goto L_0x0225;
    L_0x0215:
        r5 = r13.url;	 Catch:{ Exception -> 0x0266 }
        if (r5 == 0) goto L_0x0224;
    L_0x0219:
        r5 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending;	 Catch:{ Exception -> 0x0266 }
        r5.<init>();	 Catch:{ Exception -> 0x0266 }
        r6 = r13.url;	 Catch:{ Exception -> 0x0266 }
        r5.url = r6;	 Catch:{ Exception -> 0x0266 }
        r13 = r5;
        goto L_0x0225;
    L_0x0224:
        r13 = 0;
    L_0x0225:
        if (r13 != 0) goto L_0x022f;
    L_0x0227:
        r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Exception -> 0x0266 }
        r5.<init>();	 Catch:{ Exception -> 0x0266 }
        r7.media = r5;	 Catch:{ Exception -> 0x0266 }
        goto L_0x023a;
    L_0x022f:
        r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;	 Catch:{ Exception -> 0x0266 }
        r5.<init>();	 Catch:{ Exception -> 0x0266 }
        r7.media = r5;	 Catch:{ Exception -> 0x0266 }
        r5 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r5.webpage = r13;	 Catch:{ Exception -> 0x0266 }
    L_0x023a:
        if (r12 == 0) goto L_0x0245;
    L_0x023c:
        r5 = r12.containsKey(r1);	 Catch:{ Exception -> 0x0266 }
        if (r5 == 0) goto L_0x0245;
    L_0x0242:
        r20 = 9;
        goto L_0x0247;
    L_0x0245:
        r20 = 0;
    L_0x0247:
        r7.message = r2;	 Catch:{ Exception -> 0x0266 }
        goto L_0x0328;
    L_0x024b:
        if (r8 == 0) goto L_0x0268;
    L_0x024d:
        if (r9 == 0) goto L_0x0255;
    L_0x024f:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
        goto L_0x025a;
    L_0x0255:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
    L_0x025a:
        r7 = r5;
        r7.media = r8;	 Catch:{ Exception -> 0x0266 }
        r2 = r7;
        r6 = r19;
        r20 = 10;
    L_0x0262:
        r7 = r44;
        goto L_0x04cc;
    L_0x0266:
        r0 = move-exception;
        goto L_0x01f5;
    L_0x0268:
        if (r3 == 0) goto L_0x028e;
    L_0x026a:
        if (r9 == 0) goto L_0x0272;
    L_0x026c:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
        goto L_0x0277;
    L_0x0272:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
    L_0x0277:
        r7 = r5;
        r7.media = r3;	 Catch:{ Exception -> 0x0266 }
        if (r12 == 0) goto L_0x0288;
    L_0x027c:
        r5 = r12.containsKey(r1);	 Catch:{ Exception -> 0x0266 }
        if (r5 == 0) goto L_0x0288;
    L_0x0282:
        r2 = r7;
        r6 = r19;
    L_0x0285:
        r20 = 9;
        goto L_0x0262;
    L_0x0288:
        r2 = r7;
        r6 = r19;
        r20 = 1;
        goto L_0x0262;
    L_0x028e:
        if (r4 == 0) goto L_0x0308;
    L_0x0290:
        if (r9 == 0) goto L_0x0298;
    L_0x0292:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
        goto L_0x029d;
    L_0x0298:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
    L_0x029d:
        r7 = r5;
        r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0266 }
        r5.<init>();	 Catch:{ Exception -> 0x0266 }
        r7.media = r5;	 Catch:{ Exception -> 0x0266 }
        r5 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r6 = r5.flags;	 Catch:{ Exception -> 0x0266 }
        r20 = 3;
        r6 = r6 | 3;
        r5.flags = r6;	 Catch:{ Exception -> 0x0266 }
        if (r15 == 0) goto L_0x02b3;
    L_0x02b1:
        r7.entities = r15;	 Catch:{ Exception -> 0x0266 }
    L_0x02b3:
        r5 = r57;
        if (r5 == 0) goto L_0x02c7;
    L_0x02b7:
        r6 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r6.ttl_seconds = r5;	 Catch:{ Exception -> 0x0266 }
        r7.ttl = r5;	 Catch:{ Exception -> 0x0266 }
        r6 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r2 = r6.flags;	 Catch:{ Exception -> 0x0266 }
        r20 = 4;
        r2 = r2 | 4;
        r6.flags = r2;	 Catch:{ Exception -> 0x0266 }
    L_0x02c7:
        r2 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r2.photo = r4;	 Catch:{ Exception -> 0x0266 }
        if (r12 == 0) goto L_0x02d6;
    L_0x02cd:
        r2 = r12.containsKey(r1);	 Catch:{ Exception -> 0x0266 }
        if (r2 == 0) goto L_0x02d6;
    L_0x02d3:
        r20 = 9;
        goto L_0x02d8;
    L_0x02d6:
        r20 = 2;
    L_0x02d8:
        if (r11 == 0) goto L_0x02eb;
    L_0x02da:
        r2 = r49.length();	 Catch:{ Exception -> 0x0266 }
        if (r2 <= 0) goto L_0x02eb;
    L_0x02e0:
        r2 = "http";
        r2 = r11.startsWith(r2);	 Catch:{ Exception -> 0x0266 }
        if (r2 == 0) goto L_0x02eb;
    L_0x02e8:
        r7.attachPath = r11;	 Catch:{ Exception -> 0x0266 }
        goto L_0x0328;
    L_0x02eb:
        r2 = r4.sizes;	 Catch:{ Exception -> 0x0266 }
        r6 = r4.sizes;	 Catch:{ Exception -> 0x0266 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0266 }
        r3 = 1;
        r6 = r6 - r3;
        r2 = r2.get(r6);	 Catch:{ Exception -> 0x0266 }
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;	 Catch:{ Exception -> 0x0266 }
        r2 = r2.location;	 Catch:{ Exception -> 0x0266 }
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r3);	 Catch:{ Exception -> 0x0266 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0266 }
        r7.attachPath = r2;	 Catch:{ Exception -> 0x0266 }
        goto L_0x0328;
    L_0x0308:
        r2 = r45;
        r5 = r57;
        if (r2 == 0) goto L_0x032d;
    L_0x030e:
        r7 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01f3 }
        r7.<init>();	 Catch:{ Exception -> 0x01f3 }
        r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame;	 Catch:{ Exception -> 0x0266 }
        r3.<init>();	 Catch:{ Exception -> 0x0266 }
        r7.media = r3;	 Catch:{ Exception -> 0x0266 }
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r3.game = r2;	 Catch:{ Exception -> 0x0266 }
        if (r12 == 0) goto L_0x0328;
    L_0x0320:
        r2 = r12.containsKey(r1);	 Catch:{ Exception -> 0x0266 }
        if (r2 == 0) goto L_0x0328;
    L_0x0326:
        goto L_0x0282;
    L_0x0328:
        r2 = r7;
        r6 = r19;
        goto L_0x0262;
    L_0x032d:
        r2 = r43;
        if (r2 == 0) goto L_0x03a3;
    L_0x0331:
        if (r9 == 0) goto L_0x0339;
    L_0x0333:
        r3 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01f3 }
        r3.<init>();	 Catch:{ Exception -> 0x01f3 }
        goto L_0x033e;
    L_0x0339:
        r3 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01f3 }
        r3.<init>();	 Catch:{ Exception -> 0x01f3 }
    L_0x033e:
        r7 = r3;
        r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact;	 Catch:{ Exception -> 0x0266 }
        r3.<init>();	 Catch:{ Exception -> 0x0266 }
        r7.media = r3;	 Catch:{ Exception -> 0x0266 }
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r6 = r2.phone;	 Catch:{ Exception -> 0x0266 }
        r3.phone_number = r6;	 Catch:{ Exception -> 0x0266 }
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r6 = r2.first_name;	 Catch:{ Exception -> 0x0266 }
        r3.first_name = r6;	 Catch:{ Exception -> 0x0266 }
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r6 = r2.last_name;	 Catch:{ Exception -> 0x0266 }
        r3.last_name = r6;	 Catch:{ Exception -> 0x0266 }
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r6 = r2.id;	 Catch:{ Exception -> 0x0266 }
        r3.user_id = r6;	 Catch:{ Exception -> 0x0266 }
        r3 = r2.restriction_reason;	 Catch:{ Exception -> 0x0266 }
        if (r3 == 0) goto L_0x0375;
    L_0x0362:
        r3 = r2.restriction_reason;	 Catch:{ Exception -> 0x0266 }
        r6 = "BEGIN:VCARD";
        r3 = r3.startsWith(r6);	 Catch:{ Exception -> 0x0266 }
        if (r3 == 0) goto L_0x0375;
    L_0x036c:
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r6 = r2.restriction_reason;	 Catch:{ Exception -> 0x0266 }
        r3.vcard = r6;	 Catch:{ Exception -> 0x0266 }
        r6 = r19;
        goto L_0x037b;
    L_0x0375:
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r6 = r19;
        r3.vcard = r6;	 Catch:{ Exception -> 0x0266 }
    L_0x037b:
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r3 = r3.first_name;	 Catch:{ Exception -> 0x0266 }
        if (r3 != 0) goto L_0x0387;
    L_0x0381:
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r3.first_name = r6;	 Catch:{ Exception -> 0x0266 }
        r2.first_name = r6;	 Catch:{ Exception -> 0x0266 }
    L_0x0387:
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r3 = r3.last_name;	 Catch:{ Exception -> 0x0266 }
        if (r3 != 0) goto L_0x0393;
    L_0x038d:
        r3 = r7.media;	 Catch:{ Exception -> 0x0266 }
        r3.last_name = r6;	 Catch:{ Exception -> 0x0266 }
        r2.last_name = r6;	 Catch:{ Exception -> 0x0266 }
    L_0x0393:
        if (r12 == 0) goto L_0x039e;
    L_0x0395:
        r3 = r12.containsKey(r1);	 Catch:{ Exception -> 0x0266 }
        if (r3 == 0) goto L_0x039e;
    L_0x039b:
        r2 = r7;
        goto L_0x0285;
    L_0x039e:
        r20 = 6;
        r2 = r7;
        goto L_0x0262;
    L_0x03a3:
        r7 = r44;
        r3 = r5;
        r6 = r19;
        if (r7 == 0) goto L_0x04cb;
    L_0x03aa:
        if (r9 == 0) goto L_0x03b2;
    L_0x03ac:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
        goto L_0x03b7;
    L_0x03b2:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x01f3 }
        r5.<init>();	 Catch:{ Exception -> 0x01f3 }
    L_0x03b7:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x04c1 }
        r2.<init>();	 Catch:{ Exception -> 0x04c1 }
        r5.media = r2;	 Catch:{ Exception -> 0x04c1 }
        r2 = r5.media;	 Catch:{ Exception -> 0x04c1 }
        r4 = r2.flags;	 Catch:{ Exception -> 0x04c1 }
        r19 = 3;
        r4 = r4 | 3;
        r2.flags = r4;	 Catch:{ Exception -> 0x04c1 }
        if (r3 == 0) goto L_0x03e2;
    L_0x03ca:
        r2 = r5.media;	 Catch:{ Exception -> 0x03db }
        r2.ttl_seconds = r3;	 Catch:{ Exception -> 0x03db }
        r5.ttl = r3;	 Catch:{ Exception -> 0x03db }
        r2 = r5.media;	 Catch:{ Exception -> 0x03db }
        r4 = r2.flags;	 Catch:{ Exception -> 0x03db }
        r19 = 4;
        r4 = r4 | 4;
        r2.flags = r4;	 Catch:{ Exception -> 0x03db }
        goto L_0x03e2;
    L_0x03db:
        r0 = move-exception;
        r15 = r37;
        r1 = r0;
        r7 = r5;
        goto L_0x01f0;
    L_0x03e2:
        r2 = r5.media;	 Catch:{ Exception -> 0x04c1 }
        r2.document = r7;	 Catch:{ Exception -> 0x04c1 }
        if (r12 == 0) goto L_0x03f1;
    L_0x03e8:
        r2 = r12.containsKey(r1);	 Catch:{ Exception -> 0x03db }
        if (r2 == 0) goto L_0x03f1;
    L_0x03ee:
        r20 = 9;
        goto L_0x040e;
    L_0x03f1:
        r2 = org.telegram.messenger.MessageObject.isVideoDocument(r44);	 Catch:{ Exception -> 0x04c1 }
        if (r2 != 0) goto L_0x040c;
    L_0x03f7:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r44);	 Catch:{ Exception -> 0x03db }
        if (r2 != 0) goto L_0x040c;
    L_0x03fd:
        if (r42 == 0) goto L_0x0400;
    L_0x03ff:
        goto L_0x040c;
    L_0x0400:
        r2 = org.telegram.messenger.MessageObject.isVoiceDocument(r44);	 Catch:{ Exception -> 0x03db }
        if (r2 == 0) goto L_0x0409;
    L_0x0406:
        r20 = 8;
        goto L_0x040e;
    L_0x0409:
        r20 = 7;
        goto L_0x040e;
    L_0x040c:
        r20 = 3;
    L_0x040e:
        if (r42 == 0) goto L_0x0421;
    L_0x0410:
        r2 = r42.getString();	 Catch:{ Exception -> 0x03db }
        if (r12 != 0) goto L_0x041c;
    L_0x0416:
        r4 = new java.util.HashMap;	 Catch:{ Exception -> 0x03db }
        r4.<init>();	 Catch:{ Exception -> 0x03db }
        r12 = r4;
    L_0x041c:
        r4 = "ve";
        r12.put(r4, r2);	 Catch:{ Exception -> 0x03db }
    L_0x0421:
        if (r9 == 0) goto L_0x0438;
    L_0x0423:
        r2 = r7.dc_id;	 Catch:{ Exception -> 0x03db }
        if (r2 <= 0) goto L_0x0438;
    L_0x0427:
        r2 = org.telegram.messenger.MessageObject.isStickerDocument(r44);	 Catch:{ Exception -> 0x03db }
        if (r2 != 0) goto L_0x0438;
    L_0x042d:
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r44);	 Catch:{ Exception -> 0x03db }
        r2 = r2.toString();	 Catch:{ Exception -> 0x03db }
        r5.attachPath = r2;	 Catch:{ Exception -> 0x03db }
        goto L_0x043a;
    L_0x0438:
        r5.attachPath = r11;	 Catch:{ Exception -> 0x04c1 }
    L_0x043a:
        if (r9 == 0) goto L_0x04bc;
    L_0x043c:
        r2 = org.telegram.messenger.MessageObject.isStickerDocument(r44);	 Catch:{ Exception -> 0x04c1 }
        if (r2 != 0) goto L_0x0448;
    L_0x0442:
        r2 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r44);	 Catch:{ Exception -> 0x03db }
        if (r2 == 0) goto L_0x04bc;
    L_0x0448:
        r2 = 0;
    L_0x0449:
        r4 = r7.attributes;	 Catch:{ Exception -> 0x04c1 }
        r4 = r4.size();	 Catch:{ Exception -> 0x04c1 }
        if (r2 >= r4) goto L_0x04bc;
    L_0x0451:
        r4 = r7.attributes;	 Catch:{ Exception -> 0x04c1 }
        r4 = r4.get(r2);	 Catch:{ Exception -> 0x04c1 }
        r4 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r4;	 Catch:{ Exception -> 0x04c1 }
        r3 = r4 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;	 Catch:{ Exception -> 0x04c1 }
        if (r3 == 0) goto L_0x04b5;
    L_0x045d:
        r3 = r7.attributes;	 Catch:{ Exception -> 0x04c1 }
        r3.remove(r2);	 Catch:{ Exception -> 0x04c1 }
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55;	 Catch:{ Exception -> 0x04c1 }
        r2.<init>();	 Catch:{ Exception -> 0x04c1 }
        r3 = r7.attributes;	 Catch:{ Exception -> 0x04c1 }
        r3.add(r2);	 Catch:{ Exception -> 0x04c1 }
        r3 = r4.alt;	 Catch:{ Exception -> 0x04c1 }
        r2.alt = r3;	 Catch:{ Exception -> 0x04c1 }
        r3 = r4.stickerset;	 Catch:{ Exception -> 0x04c1 }
        if (r3 == 0) goto L_0x04a9;
    L_0x0474:
        r3 = r4.stickerset;	 Catch:{ Exception -> 0x04c1 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x04c1 }
        if (r3 == 0) goto L_0x0481;
    L_0x047a:
        r3 = r4.stickerset;	 Catch:{ Exception -> 0x03db }
        r3 = r3.short_name;	 Catch:{ Exception -> 0x03db }
        r19 = r5;
        goto L_0x048f;
    L_0x0481:
        r3 = r37.getMediaDataController();	 Catch:{ Exception -> 0x04c1 }
        r4 = r4.stickerset;	 Catch:{ Exception -> 0x04c1 }
        r19 = r5;
        r4 = r4.id;	 Catch:{ Exception -> 0x04b3 }
        r3 = r3.getStickerSetName(r4);	 Catch:{ Exception -> 0x04b3 }
    L_0x048f:
        r4 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Exception -> 0x04b3 }
        if (r4 != 0) goto L_0x04a1;
    L_0x0495:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x04b3 }
        r4.<init>();	 Catch:{ Exception -> 0x04b3 }
        r2.stickerset = r4;	 Catch:{ Exception -> 0x04b3 }
        r2 = r2.stickerset;	 Catch:{ Exception -> 0x04b3 }
        r2.short_name = r3;	 Catch:{ Exception -> 0x04b3 }
        goto L_0x04be;
    L_0x04a1:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x04b3 }
        r3.<init>();	 Catch:{ Exception -> 0x04b3 }
        r2.stickerset = r3;	 Catch:{ Exception -> 0x04b3 }
        goto L_0x04be;
    L_0x04a9:
        r19 = r5;
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x04b3 }
        r3.<init>();	 Catch:{ Exception -> 0x04b3 }
        r2.stickerset = r3;	 Catch:{ Exception -> 0x04b3 }
        goto L_0x04be;
    L_0x04b3:
        r0 = move-exception;
        goto L_0x04c4;
    L_0x04b5:
        r19 = r5;
        r2 = r2 + 1;
        r3 = r57;
        goto L_0x0449;
    L_0x04bc:
        r19 = r5;
    L_0x04be:
        r2 = r19;
        goto L_0x04cc;
    L_0x04c1:
        r0 = move-exception;
        r19 = r5;
    L_0x04c4:
        r15 = r37;
        r1 = r0;
        r7 = r19;
        goto L_0x01f0;
    L_0x04cb:
        r2 = 0;
    L_0x04cc:
        if (r15 == 0) goto L_0x04e4;
    L_0x04ce:
        r3 = r54.isEmpty();	 Catch:{ Exception -> 0x04dd }
        if (r3 != 0) goto L_0x04e4;
    L_0x04d4:
        r2.entities = r15;	 Catch:{ Exception -> 0x04dd }
        r3 = r2.flags;	 Catch:{ Exception -> 0x04dd }
        r3 = r3 | 128;
        r2.flags = r3;	 Catch:{ Exception -> 0x04dd }
        goto L_0x04e4;
    L_0x04dd:
        r0 = move-exception;
        r15 = r37;
        r1 = r0;
        r7 = r2;
        goto L_0x01f0;
    L_0x04e4:
        r3 = r18;
        if (r3 == 0) goto L_0x04eb;
    L_0x04e8:
        r2.message = r3;	 Catch:{ Exception -> 0x04dd }
        goto L_0x04f1;
    L_0x04eb:
        r4 = r2.message;	 Catch:{ Exception -> 0x1794 }
        if (r4 != 0) goto L_0x04f1;
    L_0x04ef:
        r2.message = r6;	 Catch:{ Exception -> 0x04dd }
    L_0x04f1:
        r4 = r2.attachPath;	 Catch:{ Exception -> 0x1794 }
        if (r4 != 0) goto L_0x04f7;
    L_0x04f5:
        r2.attachPath = r6;	 Catch:{ Exception -> 0x04dd }
    L_0x04f7:
        r4 = r37.getUserConfig();	 Catch:{ Exception -> 0x1794 }
        r4 = r4.getNewMessageId();	 Catch:{ Exception -> 0x1794 }
        r2.id = r4;	 Catch:{ Exception -> 0x1794 }
        r2.local_id = r4;	 Catch:{ Exception -> 0x1794 }
        r4 = 1;
        r2.out = r4;	 Catch:{ Exception -> 0x1794 }
        if (r10 == 0) goto L_0x0512;
    L_0x0508:
        if (r26 == 0) goto L_0x0512;
    L_0x050a:
        r5 = r26;
        r4 = r5.channel_id;	 Catch:{ Exception -> 0x04dd }
        r4 = -r4;
        r2.from_id = r4;	 Catch:{ Exception -> 0x04dd }
        goto L_0x0524;
    L_0x0512:
        r5 = r26;
        r4 = r37.getUserConfig();	 Catch:{ Exception -> 0x1794 }
        r4 = r4.getClientUserId();	 Catch:{ Exception -> 0x1794 }
        r2.from_id = r4;	 Catch:{ Exception -> 0x1794 }
        r4 = r2.flags;	 Catch:{ Exception -> 0x1794 }
        r4 = r4 | 256;
        r2.flags = r4;	 Catch:{ Exception -> 0x1794 }
    L_0x0524:
        r4 = r37.getUserConfig();	 Catch:{ Exception -> 0x1794 }
        r18 = r2;
        r2 = 0;
        r4.saveConfig(r2);	 Catch:{ Exception -> 0x1790 }
        r15 = r43;
        r2 = r57;
        r19 = r1;
        r45 = r3;
        r11 = r8;
        r28 = r13;
        r1 = r20;
        r8 = r40;
        r13 = r41;
        r36 = r18;
        r18 = r7;
        r7 = r36;
    L_0x0545:
        r3 = r7.random_id;	 Catch:{ Exception -> 0x1789 }
        r20 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1));
        if (r20 != 0) goto L_0x0551;
    L_0x054b:
        r3 = r37.getNextRandomId();	 Catch:{ Exception -> 0x0266 }
        r7.random_id = r3;	 Catch:{ Exception -> 0x0266 }
    L_0x0551:
        r3 = "bot_name";
        if (r12 == 0) goto L_0x0586;
    L_0x0555:
        r4 = "bot";
        r4 = r12.containsKey(r4);	 Catch:{ Exception -> 0x0266 }
        if (r4 == 0) goto L_0x0586;
    L_0x055d:
        if (r9 == 0) goto L_0x056e;
    L_0x055f:
        r4 = r12.get(r3);	 Catch:{ Exception -> 0x0266 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0266 }
        r7.via_bot_name = r4;	 Catch:{ Exception -> 0x0266 }
        r4 = r7.via_bot_name;	 Catch:{ Exception -> 0x0266 }
        if (r4 != 0) goto L_0x0580;
    L_0x056b:
        r7.via_bot_name = r6;	 Catch:{ Exception -> 0x0266 }
        goto L_0x0580;
    L_0x056e:
        r4 = "bot";
        r4 = r12.get(r4);	 Catch:{ Exception -> 0x0266 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0266 }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Exception -> 0x0266 }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x0266 }
        r7.via_bot_id = r4;	 Catch:{ Exception -> 0x0266 }
    L_0x0580:
        r4 = r7.flags;	 Catch:{ Exception -> 0x0266 }
        r4 = r4 | 2048;
        r7.flags = r4;	 Catch:{ Exception -> 0x0266 }
    L_0x0586:
        r7.params = r12;	 Catch:{ Exception -> 0x1789 }
        if (r14 == 0) goto L_0x058e;
    L_0x058a:
        r4 = r14.resendAsIs;	 Catch:{ Exception -> 0x0266 }
        if (r4 != 0) goto L_0x05db;
    L_0x058e:
        r4 = r37.getConnectionsManager();	 Catch:{ Exception -> 0x1789 }
        r4 = r4.getCurrentTime();	 Catch:{ Exception -> 0x1789 }
        r7.date = r4;	 Catch:{ Exception -> 0x1789 }
        r4 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;	 Catch:{ Exception -> 0x1789 }
        if (r4 == 0) goto L_0x05d8;
    L_0x059c:
        if (r10 == 0) goto L_0x05a7;
    L_0x059e:
        r4 = 1;
        r7.views = r4;	 Catch:{ Exception -> 0x0266 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0266 }
        r4 = r4 | 1024;
        r7.flags = r4;	 Catch:{ Exception -> 0x0266 }
    L_0x05a7:
        r4 = r37.getMessagesController();	 Catch:{ Exception -> 0x0266 }
        r10 = r5.channel_id;	 Catch:{ Exception -> 0x0266 }
        r10 = java.lang.Integer.valueOf(r10);	 Catch:{ Exception -> 0x0266 }
        r4 = r4.getChat(r10);	 Catch:{ Exception -> 0x0266 }
        if (r4 == 0) goto L_0x05db;
    L_0x05b7:
        r10 = r4.megagroup;	 Catch:{ Exception -> 0x0266 }
        if (r10 == 0) goto L_0x05c6;
    L_0x05bb:
        r4 = r7.flags;	 Catch:{ Exception -> 0x0266 }
        r10 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = r4 | r10;
        r7.flags = r4;	 Catch:{ Exception -> 0x0266 }
        r10 = 1;
        r7.unread = r10;	 Catch:{ Exception -> 0x0266 }
        goto L_0x05db;
    L_0x05c6:
        r10 = 1;
        r7.post = r10;	 Catch:{ Exception -> 0x0266 }
        r4 = r4.signatures;	 Catch:{ Exception -> 0x0266 }
        if (r4 == 0) goto L_0x05db;
    L_0x05cd:
        r4 = r37.getUserConfig();	 Catch:{ Exception -> 0x0266 }
        r4 = r4.getClientUserId();	 Catch:{ Exception -> 0x0266 }
        r7.from_id = r4;	 Catch:{ Exception -> 0x0266 }
        goto L_0x05db;
    L_0x05d8:
        r4 = 1;
        r7.unread = r4;	 Catch:{ Exception -> 0x1789 }
    L_0x05db:
        r4 = r7.flags;	 Catch:{ Exception -> 0x1789 }
        r4 = r4 | 512;
        r7.flags = r4;	 Catch:{ Exception -> 0x1789 }
        r40 = r3;
        r10 = 2;
        r3 = r47;
        r7.dialog_id = r3;	 Catch:{ Exception -> 0x1789 }
        r10 = r50;
        if (r10 == 0) goto L_0x0616;
    L_0x05ec:
        if (r9 == 0) goto L_0x0606;
    L_0x05ee:
        r14 = r10.messageOwner;	 Catch:{ Exception -> 0x0266 }
        r20 = r15;
        r14 = r14.random_id;	 Catch:{ Exception -> 0x0266 }
        r26 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r26 == 0) goto L_0x0608;
    L_0x05f8:
        r14 = r10.messageOwner;	 Catch:{ Exception -> 0x0266 }
        r14 = r14.random_id;	 Catch:{ Exception -> 0x0266 }
        r7.reply_to_random_id = r14;	 Catch:{ Exception -> 0x0266 }
        r14 = r7.flags;	 Catch:{ Exception -> 0x0266 }
        r15 = 8;
        r14 = r14 | r15;
        r7.flags = r14;	 Catch:{ Exception -> 0x0266 }
        goto L_0x060f;
    L_0x0606:
        r20 = r15;
    L_0x0608:
        r14 = r7.flags;	 Catch:{ Exception -> 0x0266 }
        r15 = 8;
        r14 = r14 | r15;
        r7.flags = r14;	 Catch:{ Exception -> 0x0266 }
    L_0x060f:
        r14 = r50.getId();	 Catch:{ Exception -> 0x0266 }
        r7.reply_to_msg_id = r14;	 Catch:{ Exception -> 0x0266 }
        goto L_0x0618;
    L_0x0616:
        r20 = r15;
    L_0x0618:
        r14 = r55;
        if (r14 == 0) goto L_0x0626;
    L_0x061c:
        if (r9 != 0) goto L_0x0626;
    L_0x061e:
        r15 = r7.flags;	 Catch:{ Exception -> 0x0266 }
        r15 = r15 | 64;
        r7.flags = r15;	 Catch:{ Exception -> 0x0266 }
        r7.reply_markup = r14;	 Catch:{ Exception -> 0x0266 }
    L_0x0626:
        if (r25 == 0) goto L_0x06e2;
    L_0x0628:
        r14 = r24;
        r15 = 1;
        if (r14 != r15) goto L_0x06ac;
    L_0x062d:
        r15 = r37;
        r23 = r11;
        r11 = r15.currentChatInfo;	 Catch:{ Exception -> 0x06df }
        if (r11 != 0) goto L_0x0657;
    L_0x0635:
        r1 = r37.getMessagesStorage();	 Catch:{ Exception -> 0x06df }
        r1.markMessageAsSendError(r7);	 Catch:{ Exception -> 0x06df }
        r1 = r37.getNotificationCenter();	 Catch:{ Exception -> 0x06df }
        r2 = org.telegram.messenger.NotificationCenter.messageSendError;	 Catch:{ Exception -> 0x06df }
        r3 = 1;
        r4 = new java.lang.Object[r3];	 Catch:{ Exception -> 0x06df }
        r3 = r7.id;	 Catch:{ Exception -> 0x06df }
        r3 = java.lang.Integer.valueOf(r3);	 Catch:{ Exception -> 0x06df }
        r5 = 0;
        r4[r5] = r3;	 Catch:{ Exception -> 0x06df }
        r1.postNotificationName(r2, r4);	 Catch:{ Exception -> 0x06df }
        r1 = r7.id;	 Catch:{ Exception -> 0x06df }
        r15.processSentMessage(r1);	 Catch:{ Exception -> 0x06df }
        return;
    L_0x0657:
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x06df }
        r11.<init>();	 Catch:{ Exception -> 0x06df }
        r41 = r13;
        r13 = r15.currentChatInfo;	 Catch:{ Exception -> 0x06df }
        r13 = r13.participants;	 Catch:{ Exception -> 0x06df }
        r13 = r13.participants;	 Catch:{ Exception -> 0x06df }
        r13 = r13.iterator();	 Catch:{ Exception -> 0x06df }
    L_0x0668:
        r26 = r13.hasNext();	 Catch:{ Exception -> 0x06df }
        if (r26 == 0) goto L_0x069a;
    L_0x066e:
        r26 = r13.next();	 Catch:{ Exception -> 0x06df }
        r43 = r13;
        r13 = r26;
        r13 = (org.telegram.tgnet.TLRPC.ChatParticipant) r13;	 Catch:{ Exception -> 0x06df }
        r26 = r6;
        r6 = r37.getMessagesController();	 Catch:{ Exception -> 0x06df }
        r13 = r13.user_id;	 Catch:{ Exception -> 0x06df }
        r13 = java.lang.Integer.valueOf(r13);	 Catch:{ Exception -> 0x06df }
        r6 = r6.getUser(r13);	 Catch:{ Exception -> 0x06df }
        r13 = r37.getMessagesController();	 Catch:{ Exception -> 0x06df }
        r6 = r13.getInputUser(r6);	 Catch:{ Exception -> 0x06df }
        if (r6 == 0) goto L_0x0695;
    L_0x0692:
        r11.add(r6);	 Catch:{ Exception -> 0x06df }
    L_0x0695:
        r13 = r43;
        r6 = r26;
        goto L_0x0668;
    L_0x069a:
        r26 = r6;
        r6 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Exception -> 0x06df }
        r6.<init>();	 Catch:{ Exception -> 0x06df }
        r7.to_id = r6;	 Catch:{ Exception -> 0x06df }
        r6 = r7.to_id;	 Catch:{ Exception -> 0x06df }
        r13 = r25;
        r6.chat_id = r13;	 Catch:{ Exception -> 0x06df }
        r6 = 1;
        goto L_0x07a6;
    L_0x06ac:
        r15 = r37;
        r26 = r6;
        r23 = r11;
        r41 = r13;
        r13 = r25;
        r6 = r37.getMessagesController();	 Catch:{ Exception -> 0x06df }
        r6 = r6.getPeer(r13);	 Catch:{ Exception -> 0x06df }
        r7.to_id = r6;	 Catch:{ Exception -> 0x06df }
        if (r13 <= 0) goto L_0x07a4;
    L_0x06c2:
        r6 = r37.getMessagesController();	 Catch:{ Exception -> 0x06df }
        r11 = java.lang.Integer.valueOf(r13);	 Catch:{ Exception -> 0x06df }
        r6 = r6.getUser(r11);	 Catch:{ Exception -> 0x06df }
        if (r6 != 0) goto L_0x06d6;
    L_0x06d0:
        r1 = r7.id;	 Catch:{ Exception -> 0x06df }
        r15.processSentMessage(r1);	 Catch:{ Exception -> 0x06df }
        return;
    L_0x06d6:
        r6 = r6.bot;	 Catch:{ Exception -> 0x06df }
        if (r6 == 0) goto L_0x07a4;
    L_0x06da:
        r6 = 0;
        r7.unread = r6;	 Catch:{ Exception -> 0x06df }
        goto L_0x07a4;
    L_0x06df:
        r0 = move-exception;
        goto L_0x178d;
    L_0x06e2:
        r15 = r37;
        r26 = r6;
        r23 = r11;
        r41 = r13;
        r14 = r24;
        r6 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Exception -> 0x1787 }
        r6.<init>();	 Catch:{ Exception -> 0x1787 }
        r7.to_id = r6;	 Catch:{ Exception -> 0x1787 }
        r6 = r9.participant_id;	 Catch:{ Exception -> 0x1787 }
        r11 = r37.getUserConfig();	 Catch:{ Exception -> 0x1787 }
        r11 = r11.getClientUserId();	 Catch:{ Exception -> 0x1787 }
        if (r6 != r11) goto L_0x0706;
    L_0x06ff:
        r6 = r7.to_id;	 Catch:{ Exception -> 0x06df }
        r11 = r9.admin_id;	 Catch:{ Exception -> 0x06df }
        r6.user_id = r11;	 Catch:{ Exception -> 0x06df }
        goto L_0x070c;
    L_0x0706:
        r6 = r7.to_id;	 Catch:{ Exception -> 0x1787 }
        r11 = r9.participant_id;	 Catch:{ Exception -> 0x1787 }
        r6.user_id = r11;	 Catch:{ Exception -> 0x1787 }
    L_0x070c:
        if (r2 == 0) goto L_0x0711;
    L_0x070e:
        r7.ttl = r2;	 Catch:{ Exception -> 0x06df }
        goto L_0x072b;
    L_0x0711:
        r6 = r9.ttl;	 Catch:{ Exception -> 0x1787 }
        r7.ttl = r6;	 Catch:{ Exception -> 0x1787 }
        r6 = r7.ttl;	 Catch:{ Exception -> 0x1787 }
        if (r6 == 0) goto L_0x072b;
    L_0x0719:
        r6 = r7.media;	 Catch:{ Exception -> 0x06df }
        if (r6 == 0) goto L_0x072b;
    L_0x071d:
        r6 = r7.media;	 Catch:{ Exception -> 0x06df }
        r11 = r7.ttl;	 Catch:{ Exception -> 0x06df }
        r6.ttl_seconds = r11;	 Catch:{ Exception -> 0x06df }
        r6 = r7.media;	 Catch:{ Exception -> 0x06df }
        r11 = r6.flags;	 Catch:{ Exception -> 0x06df }
        r13 = 4;
        r11 = r11 | r13;
        r6.flags = r11;	 Catch:{ Exception -> 0x06df }
    L_0x072b:
        r6 = r7.ttl;	 Catch:{ Exception -> 0x1787 }
        if (r6 == 0) goto L_0x07a4;
    L_0x072f:
        r6 = r7.media;	 Catch:{ Exception -> 0x06df }
        r6 = r6.document;	 Catch:{ Exception -> 0x06df }
        if (r6 == 0) goto L_0x07a4;
    L_0x0735:
        r6 = org.telegram.messenger.MessageObject.isVoiceMessage(r7);	 Catch:{ Exception -> 0x06df }
        if (r6 == 0) goto L_0x076a;
    L_0x073b:
        r6 = 0;
    L_0x073c:
        r11 = r7.media;	 Catch:{ Exception -> 0x06df }
        r11 = r11.document;	 Catch:{ Exception -> 0x06df }
        r11 = r11.attributes;	 Catch:{ Exception -> 0x06df }
        r11 = r11.size();	 Catch:{ Exception -> 0x06df }
        if (r6 >= r11) goto L_0x075e;
    L_0x0748:
        r11 = r7.media;	 Catch:{ Exception -> 0x06df }
        r11 = r11.document;	 Catch:{ Exception -> 0x06df }
        r11 = r11.attributes;	 Catch:{ Exception -> 0x06df }
        r11 = r11.get(r6);	 Catch:{ Exception -> 0x06df }
        r11 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r11;	 Catch:{ Exception -> 0x06df }
        r13 = r11 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;	 Catch:{ Exception -> 0x06df }
        if (r13 == 0) goto L_0x075b;
    L_0x0758:
        r6 = r11.duration;	 Catch:{ Exception -> 0x06df }
        goto L_0x075f;
    L_0x075b:
        r6 = r6 + 1;
        goto L_0x073c;
    L_0x075e:
        r6 = 0;
    L_0x075f:
        r11 = r7.ttl;	 Catch:{ Exception -> 0x06df }
        r13 = 1;
        r6 = r6 + r13;
        r6 = java.lang.Math.max(r11, r6);	 Catch:{ Exception -> 0x06df }
        r7.ttl = r6;	 Catch:{ Exception -> 0x06df }
        goto L_0x07a4;
    L_0x076a:
        r6 = org.telegram.messenger.MessageObject.isVideoMessage(r7);	 Catch:{ Exception -> 0x06df }
        if (r6 != 0) goto L_0x0776;
    L_0x0770:
        r6 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r7);	 Catch:{ Exception -> 0x06df }
        if (r6 == 0) goto L_0x07a4;
    L_0x0776:
        r6 = 0;
    L_0x0777:
        r11 = r7.media;	 Catch:{ Exception -> 0x06df }
        r11 = r11.document;	 Catch:{ Exception -> 0x06df }
        r11 = r11.attributes;	 Catch:{ Exception -> 0x06df }
        r11 = r11.size();	 Catch:{ Exception -> 0x06df }
        if (r6 >= r11) goto L_0x0799;
    L_0x0783:
        r11 = r7.media;	 Catch:{ Exception -> 0x06df }
        r11 = r11.document;	 Catch:{ Exception -> 0x06df }
        r11 = r11.attributes;	 Catch:{ Exception -> 0x06df }
        r11 = r11.get(r6);	 Catch:{ Exception -> 0x06df }
        r11 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r11;	 Catch:{ Exception -> 0x06df }
        r13 = r11 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x06df }
        if (r13 == 0) goto L_0x0796;
    L_0x0793:
        r6 = r11.duration;	 Catch:{ Exception -> 0x06df }
        goto L_0x079a;
    L_0x0796:
        r6 = r6 + 1;
        goto L_0x0777;
    L_0x0799:
        r6 = 0;
    L_0x079a:
        r11 = r7.ttl;	 Catch:{ Exception -> 0x06df }
        r13 = 1;
        r6 = r6 + r13;
        r6 = java.lang.Math.max(r11, r6);	 Catch:{ Exception -> 0x06df }
        r7.ttl = r6;	 Catch:{ Exception -> 0x06df }
    L_0x07a4:
        r6 = 1;
        r11 = 0;
    L_0x07a6:
        if (r14 == r6) goto L_0x07ba;
    L_0x07a8:
        r6 = org.telegram.messenger.MessageObject.isVoiceMessage(r7);	 Catch:{ Exception -> 0x06df }
        if (r6 != 0) goto L_0x07b7;
    L_0x07ae:
        r6 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r7);	 Catch:{ Exception -> 0x06df }
        if (r6 == 0) goto L_0x07b5;
    L_0x07b4:
        goto L_0x07b7;
    L_0x07b5:
        r6 = 1;
        goto L_0x07ba;
    L_0x07b7:
        r6 = 1;
        r7.media_unread = r6;	 Catch:{ Exception -> 0x06df }
    L_0x07ba:
        r7.send_state = r6;	 Catch:{ Exception -> 0x1787 }
        r13 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x1787 }
        r14 = r15.currentAccount;	 Catch:{ Exception -> 0x1787 }
        r13.<init>(r14, r7, r6);	 Catch:{ Exception -> 0x1787 }
        r13.replyMessageObject = r10;	 Catch:{ Exception -> 0x1782 }
        r6 = r13.isForwarded();	 Catch:{ Exception -> 0x1782 }
        if (r6 != 0) goto L_0x07e8;
    L_0x07cb:
        r6 = r13.type;	 Catch:{ Exception -> 0x07e3 }
        r10 = 3;
        if (r6 == r10) goto L_0x07d7;
    L_0x07d0:
        if (r42 != 0) goto L_0x07d7;
    L_0x07d2:
        r6 = r13.type;	 Catch:{ Exception -> 0x07e3 }
        r10 = 2;
        if (r6 != r10) goto L_0x07e8;
    L_0x07d7:
        r6 = r7.attachPath;	 Catch:{ Exception -> 0x07e3 }
        r6 = android.text.TextUtils.isEmpty(r6);	 Catch:{ Exception -> 0x07e3 }
        if (r6 != 0) goto L_0x07e8;
    L_0x07df:
        r6 = 1;
        r13.attachPathExists = r6;	 Catch:{ Exception -> 0x07e3 }
        goto L_0x07e8;
    L_0x07e3:
        r0 = move-exception;
        r1 = r0;
        r11 = r13;
        goto L_0x179e;
    L_0x07e8:
        r6 = r13.videoEditedInfo;	 Catch:{ Exception -> 0x1782 }
        if (r6 == 0) goto L_0x07f1;
    L_0x07ec:
        if (r42 != 0) goto L_0x07f1;
    L_0x07ee:
        r6 = r13.videoEditedInfo;	 Catch:{ Exception -> 0x07e3 }
        goto L_0x07f3;
    L_0x07f1:
        r6 = r42;
    L_0x07f3:
        if (r12 == 0) goto L_0x0822;
    L_0x07f5:
        r10 = "groupId";
        r10 = r12.get(r10);	 Catch:{ Exception -> 0x07e3 }
        r10 = (java.lang.String) r10;	 Catch:{ Exception -> 0x07e3 }
        if (r10 == 0) goto L_0x0814;
    L_0x07ff:
        r10 = org.telegram.messenger.Utilities.parseLong(r10);	 Catch:{ Exception -> 0x07e3 }
        r43 = r11;
        r10 = r10.longValue();	 Catch:{ Exception -> 0x07e3 }
        r7.grouped_id = r10;	 Catch:{ Exception -> 0x07e3 }
        r14 = r7.flags;	 Catch:{ Exception -> 0x07e3 }
        r25 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r14 = r14 | r25;
        r7.flags = r14;	 Catch:{ Exception -> 0x07e3 }
        goto L_0x0818;
    L_0x0814:
        r43 = r11;
        r10 = r16;
    L_0x0818:
        r14 = "final";
        r14 = r12.get(r14);	 Catch:{ Exception -> 0x07e3 }
        if (r14 == 0) goto L_0x0826;
    L_0x0820:
        r14 = 1;
        goto L_0x0827;
    L_0x0822:
        r43 = r11;
        r10 = r16;
    L_0x0826:
        r14 = 0;
    L_0x0827:
        r25 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1));
        if (r25 != 0) goto L_0x086f;
    L_0x082b:
        r14 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0868 }
        r14.<init>();	 Catch:{ Exception -> 0x0868 }
        r14.add(r13);	 Catch:{ Exception -> 0x0868 }
        r44 = r12;
        r12 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0868 }
        r12.<init>();	 Catch:{ Exception -> 0x0868 }
        r12.add(r7);	 Catch:{ Exception -> 0x0868 }
        r29 = r37.getMessagesStorage();	 Catch:{ Exception -> 0x0868 }
        r31 = 0;
        r32 = 1;
        r33 = 0;
        r34 = 0;
        r30 = r12;
        r29.putMessages(r30, r31, r32, r33, r34);	 Catch:{ Exception -> 0x0868 }
        r12 = r37.getMessagesController();	 Catch:{ Exception -> 0x0868 }
        r12.updateInterfaceWithMessages(r3, r14);	 Catch:{ Exception -> 0x0868 }
        r12 = r37.getNotificationCenter();	 Catch:{ Exception -> 0x0868 }
        r14 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;	 Catch:{ Exception -> 0x0868 }
        r46 = r2;
        r51 = r13;
        r13 = 0;
        r2 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x0898 }
        r12.postNotificationName(r14, r2);	 Catch:{ Exception -> 0x0898 }
        r2 = 0;
        r13 = 0;
        goto L_0x08b7;
    L_0x0868:
        r0 = move-exception;
        r51 = r13;
    L_0x086b:
        r11 = r51;
        goto L_0x01f8;
    L_0x086f:
        r46 = r2;
        r44 = r12;
        r51 = r13;
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x177c }
        r2.<init>();	 Catch:{ Exception -> 0x177c }
        r12 = "group_";
        r2.append(r12);	 Catch:{ Exception -> 0x177c }
        r2.append(r10);	 Catch:{ Exception -> 0x177c }
        r2 = r2.toString();	 Catch:{ Exception -> 0x177c }
        r12 = r15.delayedMessages;	 Catch:{ Exception -> 0x177c }
        r2 = r12.get(r2);	 Catch:{ Exception -> 0x177c }
        r2 = (java.util.ArrayList) r2;	 Catch:{ Exception -> 0x177c }
        if (r2 == 0) goto L_0x089a;
    L_0x0890:
        r12 = 0;
        r2 = r2.get(r12);	 Catch:{ Exception -> 0x0898 }
        r2 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r2;	 Catch:{ Exception -> 0x0898 }
        goto L_0x089b;
    L_0x0898:
        r0 = move-exception;
        goto L_0x086b;
    L_0x089a:
        r2 = 0;
    L_0x089b:
        if (r2 != 0) goto L_0x08a7;
    L_0x089d:
        r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0898 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0898 }
        r2.initForGroup(r10);	 Catch:{ Exception -> 0x0898 }
        r2.encryptedChat = r9;	 Catch:{ Exception -> 0x0898 }
    L_0x08a7:
        r12 = 0;
        r2.performMediaUpload = r12;	 Catch:{ Exception -> 0x177c }
        r13 = 0;
        r2.photoSize = r13;	 Catch:{ Exception -> 0x177c }
        r2.videoEditedInfo = r13;	 Catch:{ Exception -> 0x177c }
        r2.httpLocation = r13;	 Catch:{ Exception -> 0x177c }
        if (r14 == 0) goto L_0x08b7;
    L_0x08b3:
        r12 = r7.id;	 Catch:{ Exception -> 0x0898 }
        r2.finalGroupMessage = r12;	 Catch:{ Exception -> 0x0898 }
    L_0x08b7:
        r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x177c }
        if (r12 == 0) goto L_0x08f1;
    L_0x08bb:
        if (r5 == 0) goto L_0x08f1;
    L_0x08bd:
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0898 }
        r12.<init>();	 Catch:{ Exception -> 0x0898 }
        r14 = "send message user_id = ";
        r12.append(r14);	 Catch:{ Exception -> 0x0898 }
        r14 = r5.user_id;	 Catch:{ Exception -> 0x0898 }
        r12.append(r14);	 Catch:{ Exception -> 0x0898 }
        r14 = " chat_id = ";
        r12.append(r14);	 Catch:{ Exception -> 0x0898 }
        r14 = r5.chat_id;	 Catch:{ Exception -> 0x0898 }
        r12.append(r14);	 Catch:{ Exception -> 0x0898 }
        r14 = " channel_id = ";
        r12.append(r14);	 Catch:{ Exception -> 0x0898 }
        r14 = r5.channel_id;	 Catch:{ Exception -> 0x0898 }
        r12.append(r14);	 Catch:{ Exception -> 0x0898 }
        r14 = " access_hash = ";
        r12.append(r14);	 Catch:{ Exception -> 0x0898 }
        r13 = r5.access_hash;	 Catch:{ Exception -> 0x0898 }
        r12.append(r13);	 Catch:{ Exception -> 0x0898 }
        r12 = r12.toString();	 Catch:{ Exception -> 0x0898 }
        org.telegram.messenger.FileLog.d(r12);	 Catch:{ Exception -> 0x0898 }
    L_0x08f1:
        if (r1 == 0) goto L_0x1608;
    L_0x08f3:
        r12 = 9;
        if (r1 != r12) goto L_0x08fd;
    L_0x08f7:
        if (r38 == 0) goto L_0x08fd;
    L_0x08f9:
        if (r9 == 0) goto L_0x08fd;
    L_0x08fb:
        goto L_0x1608;
    L_0x08fd:
        r12 = 1;
        if (r1 < r12) goto L_0x0903;
    L_0x0900:
        r12 = 3;
        if (r1 <= r12) goto L_0x0914;
    L_0x0903:
        r12 = 5;
        if (r1 < r12) goto L_0x090a;
    L_0x0906:
        r12 = 8;
        if (r1 <= r12) goto L_0x0914;
    L_0x090a:
        r12 = 9;
        if (r1 != r12) goto L_0x0910;
    L_0x090e:
        if (r9 != 0) goto L_0x0914;
    L_0x0910:
        r12 = 10;
        if (r1 != r12) goto L_0x14aa;
    L_0x0914:
        if (r9 != 0) goto L_0x0ef2;
    L_0x0916:
        r12 = 1;
        if (r1 != r12) goto L_0x097a;
    L_0x0919:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x0898 }
        if (r6 == 0) goto L_0x0937;
    L_0x091d:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue;	 Catch:{ Exception -> 0x0898 }
        r6.<init>();	 Catch:{ Exception -> 0x0898 }
        r9 = r8.address;	 Catch:{ Exception -> 0x0898 }
        r6.address = r9;	 Catch:{ Exception -> 0x0898 }
        r9 = r8.title;	 Catch:{ Exception -> 0x0898 }
        r6.title = r9;	 Catch:{ Exception -> 0x0898 }
        r9 = r8.provider;	 Catch:{ Exception -> 0x0898 }
        r6.provider = r9;	 Catch:{ Exception -> 0x0898 }
        r9 = r8.venue_id;	 Catch:{ Exception -> 0x0898 }
        r6.venue_id = r9;	 Catch:{ Exception -> 0x0898 }
        r12 = r26;
        r6.venue_type = r12;	 Catch:{ Exception -> 0x0898 }
        goto L_0x0952;
    L_0x0937:
        r12 = r26;
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;	 Catch:{ Exception -> 0x0898 }
        if (r6 == 0) goto L_0x094d;
    L_0x093d:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive;	 Catch:{ Exception -> 0x0898 }
        r6.<init>();	 Catch:{ Exception -> 0x0898 }
        r9 = r8.period;	 Catch:{ Exception -> 0x0898 }
        r6.period = r9;	 Catch:{ Exception -> 0x0898 }
        r9 = r6.flags;	 Catch:{ Exception -> 0x0898 }
        r13 = 2;
        r9 = r9 | r13;
        r6.flags = r9;	 Catch:{ Exception -> 0x0898 }
        goto L_0x0952;
    L_0x094d:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint;	 Catch:{ Exception -> 0x0898 }
        r6.<init>();	 Catch:{ Exception -> 0x0898 }
    L_0x0952:
        r9 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint;	 Catch:{ Exception -> 0x0898 }
        r9.<init>();	 Catch:{ Exception -> 0x0898 }
        r6.geo_point = r9;	 Catch:{ Exception -> 0x0898 }
        r9 = r6.geo_point;	 Catch:{ Exception -> 0x0898 }
        r13 = r8.geo;	 Catch:{ Exception -> 0x0898 }
        r13 = r13.lat;	 Catch:{ Exception -> 0x0898 }
        r9.lat = r13;	 Catch:{ Exception -> 0x0898 }
        r9 = r6.geo_point;	 Catch:{ Exception -> 0x0898 }
        r8 = r8.geo;	 Catch:{ Exception -> 0x0898 }
        r13 = r8._long;	 Catch:{ Exception -> 0x0898 }
        r9._long = r13;	 Catch:{ Exception -> 0x0898 }
        r26 = r5;
        r55 = r10;
        r19 = r12;
        r12 = r21;
        r10 = 0;
        r11 = r51;
        r51 = r1;
        r1 = r58;
        goto L_0x0cf0;
    L_0x097a:
        r12 = r26;
        r8 = 2;
        if (r1 == r8) goto L_0x0c1e;
    L_0x097f:
        r8 = 9;
        if (r1 != r8) goto L_0x0987;
    L_0x0983:
        if (r41 == 0) goto L_0x0987;
    L_0x0985:
        goto L_0x0c1e;
    L_0x0987:
        r8 = 3;
        if (r1 != r8) goto L_0x0a44;
    L_0x098a:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0898 }
        r8.<init>();	 Catch:{ Exception -> 0x0898 }
        r13 = r18;
        r9 = r13.mime_type;	 Catch:{ Exception -> 0x0898 }
        r8.mime_type = r9;	 Catch:{ Exception -> 0x0898 }
        r9 = r13.attributes;	 Catch:{ Exception -> 0x0898 }
        r8.attributes = r9;	 Catch:{ Exception -> 0x0898 }
        r9 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r13);	 Catch:{ Exception -> 0x0898 }
        if (r9 != 0) goto L_0x09b5;
    L_0x099f:
        if (r6 == 0) goto L_0x09a9;
    L_0x09a1:
        r9 = r6.muted;	 Catch:{ Exception -> 0x0898 }
        if (r9 != 0) goto L_0x09b5;
    L_0x09a5:
        r9 = r6.roundVideo;	 Catch:{ Exception -> 0x0898 }
        if (r9 != 0) goto L_0x09b5;
    L_0x09a9:
        r9 = 1;
        r8.nosound_video = r9;	 Catch:{ Exception -> 0x0898 }
        r9 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x0898 }
        if (r9 == 0) goto L_0x09b5;
    L_0x09b0:
        r9 = "nosound_video = true";
        org.telegram.messenger.FileLog.d(r9);	 Catch:{ Exception -> 0x0898 }
    L_0x09b5:
        if (r46 == 0) goto L_0x09c3;
    L_0x09b7:
        r14 = r46;
        r8.ttl_seconds = r14;	 Catch:{ Exception -> 0x0898 }
        r7.ttl = r14;	 Catch:{ Exception -> 0x0898 }
        r9 = r8.flags;	 Catch:{ Exception -> 0x0898 }
        r14 = 2;
        r9 = r9 | r14;
        r8.flags = r9;	 Catch:{ Exception -> 0x0898 }
    L_0x09c3:
        r55 = r10;
        r9 = r13.access_hash;	 Catch:{ Exception -> 0x0898 }
        r11 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1));
        if (r11 != 0) goto L_0x09d0;
    L_0x09cb:
        r9 = r8;
        r19 = r12;
        r10 = 1;
        goto L_0x09fe;
    L_0x09d0:
        r9 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0898 }
        r9.<init>();	 Catch:{ Exception -> 0x0898 }
        r10 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0898 }
        r10.<init>();	 Catch:{ Exception -> 0x0898 }
        r9.id = r10;	 Catch:{ Exception -> 0x0898 }
        r10 = r9.id;	 Catch:{ Exception -> 0x0898 }
        r19 = r12;
        r11 = r13.id;	 Catch:{ Exception -> 0x0898 }
        r10.id = r11;	 Catch:{ Exception -> 0x0898 }
        r10 = r9.id;	 Catch:{ Exception -> 0x0898 }
        r11 = r13.access_hash;	 Catch:{ Exception -> 0x0898 }
        r10.access_hash = r11;	 Catch:{ Exception -> 0x0898 }
        r10 = r9.id;	 Catch:{ Exception -> 0x0898 }
        r11 = r13.file_reference;	 Catch:{ Exception -> 0x0898 }
        r10.file_reference = r11;	 Catch:{ Exception -> 0x0898 }
        r10 = r9.id;	 Catch:{ Exception -> 0x0898 }
        r10 = r10.file_reference;	 Catch:{ Exception -> 0x0898 }
        if (r10 != 0) goto L_0x09fd;
    L_0x09f6:
        r10 = r9.id;	 Catch:{ Exception -> 0x0898 }
        r11 = 0;
        r12 = new byte[r11];	 Catch:{ Exception -> 0x0898 }
        r10.file_reference = r12;	 Catch:{ Exception -> 0x0898 }
    L_0x09fd:
        r10 = 0;
    L_0x09fe:
        if (r2 != 0) goto L_0x0a15;
    L_0x0a00:
        r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0898 }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0898 }
        r11 = 1;
        r2.type = r11;	 Catch:{ Exception -> 0x0898 }
        r11 = r51;
        r2.obj = r11;	 Catch:{ Exception -> 0x0eef }
        r12 = r21;
        r2.originalPath = r12;	 Catch:{ Exception -> 0x0eef }
        r14 = r58;
        r2.parentObject = r14;	 Catch:{ Exception -> 0x0eef }
        goto L_0x0a1b;
    L_0x0a15:
        r11 = r51;
        r14 = r58;
        r12 = r21;
    L_0x0a1b:
        r2.inputUploadMedia = r8;	 Catch:{ Exception -> 0x0eef }
        r2.performMediaUpload = r10;	 Catch:{ Exception -> 0x0eef }
        r8 = r13.thumbs;	 Catch:{ Exception -> 0x0eef }
        r8 = r8.isEmpty();	 Catch:{ Exception -> 0x0eef }
        if (r8 != 0) goto L_0x0a37;
    L_0x0a27:
        r8 = r13.thumbs;	 Catch:{ Exception -> 0x0eef }
        r38 = r9;
        r9 = 0;
        r8 = r8.get(r9);	 Catch:{ Exception -> 0x0eef }
        r8 = (org.telegram.tgnet.TLRPC.PhotoSize) r8;	 Catch:{ Exception -> 0x0eef }
        r2.photoSize = r8;	 Catch:{ Exception -> 0x0eef }
        r2.locationParent = r13;	 Catch:{ Exception -> 0x0eef }
        goto L_0x0a39;
    L_0x0a37:
        r38 = r9;
    L_0x0a39:
        r2.videoEditedInfo = r6;	 Catch:{ Exception -> 0x0eef }
        r6 = r38;
        r51 = r1;
        r26 = r5;
        r1 = r14;
        goto L_0x0cf0;
    L_0x0a44:
        r14 = r46;
        r55 = r10;
        r19 = r12;
        r13 = r18;
        r12 = r21;
        r11 = r51;
        r10 = r58;
        r6 = 6;
        if (r1 != r6) goto L_0x0a89;
    L_0x0a55:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact;	 Catch:{ Exception -> 0x0eef }
        r6.<init>();	 Catch:{ Exception -> 0x0eef }
        r8 = r20;
        r9 = r8.phone;	 Catch:{ Exception -> 0x0eef }
        r6.phone_number = r9;	 Catch:{ Exception -> 0x0eef }
        r9 = r8.first_name;	 Catch:{ Exception -> 0x0eef }
        r6.first_name = r9;	 Catch:{ Exception -> 0x0eef }
        r9 = r8.last_name;	 Catch:{ Exception -> 0x0eef }
        r6.last_name = r9;	 Catch:{ Exception -> 0x0eef }
        r9 = r8.restriction_reason;	 Catch:{ Exception -> 0x0eef }
        if (r9 == 0) goto L_0x0a7d;
    L_0x0a6c:
        r9 = r8.restriction_reason;	 Catch:{ Exception -> 0x0eef }
        r13 = "BEGIN:VCARD";
        r9 = r9.startsWith(r13);	 Catch:{ Exception -> 0x0eef }
        if (r9 == 0) goto L_0x0a7d;
    L_0x0a76:
        r8 = r8.restriction_reason;	 Catch:{ Exception -> 0x0eef }
        r6.vcard = r8;	 Catch:{ Exception -> 0x0eef }
        r8 = r19;
        goto L_0x0a81;
    L_0x0a7d:
        r8 = r19;
        r6.vcard = r8;	 Catch:{ Exception -> 0x0eef }
    L_0x0a81:
        r51 = r1;
        r26 = r5;
        r19 = r8;
        goto L_0x0b1a;
    L_0x0a89:
        r8 = r19;
        r6 = 7;
        if (r1 == r6) goto L_0x0b23;
    L_0x0a8e:
        r6 = 9;
        if (r1 != r6) goto L_0x0a94;
    L_0x0a92:
        goto L_0x0b23;
    L_0x0a94:
        r6 = 8;
        if (r1 != r6) goto L_0x0b04;
    L_0x0a98:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0eef }
        r2.<init>();	 Catch:{ Exception -> 0x0eef }
        r6 = r13.mime_type;	 Catch:{ Exception -> 0x0eef }
        r2.mime_type = r6;	 Catch:{ Exception -> 0x0eef }
        r6 = r13.attributes;	 Catch:{ Exception -> 0x0eef }
        r2.attributes = r6;	 Catch:{ Exception -> 0x0eef }
        if (r14 == 0) goto L_0x0ab1;
    L_0x0aa7:
        r2.ttl_seconds = r14;	 Catch:{ Exception -> 0x0eef }
        r7.ttl = r14;	 Catch:{ Exception -> 0x0eef }
        r6 = r2.flags;	 Catch:{ Exception -> 0x0eef }
        r9 = 2;
        r6 = r6 | r9;
        r2.flags = r6;	 Catch:{ Exception -> 0x0eef }
    L_0x0ab1:
        r26 = r5;
        r5 = r13.access_hash;	 Catch:{ Exception -> 0x0eef }
        r9 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1));
        if (r9 != 0) goto L_0x0abe;
    L_0x0ab9:
        r6 = r2;
        r19 = r8;
        r5 = 1;
        goto L_0x0aed;
    L_0x0abe:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0eef }
        r5.<init>();	 Catch:{ Exception -> 0x0eef }
        r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0eef }
        r6.<init>();	 Catch:{ Exception -> 0x0eef }
        r5.id = r6;	 Catch:{ Exception -> 0x0eef }
        r6 = r5.id;	 Catch:{ Exception -> 0x0eef }
        r19 = r8;
        r8 = r13.id;	 Catch:{ Exception -> 0x0eef }
        r6.id = r8;	 Catch:{ Exception -> 0x0eef }
        r6 = r5.id;	 Catch:{ Exception -> 0x0eef }
        r8 = r13.access_hash;	 Catch:{ Exception -> 0x0eef }
        r6.access_hash = r8;	 Catch:{ Exception -> 0x0eef }
        r6 = r5.id;	 Catch:{ Exception -> 0x0eef }
        r8 = r13.file_reference;	 Catch:{ Exception -> 0x0eef }
        r6.file_reference = r8;	 Catch:{ Exception -> 0x0eef }
        r6 = r5.id;	 Catch:{ Exception -> 0x0eef }
        r6 = r6.file_reference;	 Catch:{ Exception -> 0x0eef }
        if (r6 != 0) goto L_0x0aeb;
    L_0x0ae4:
        r6 = r5.id;	 Catch:{ Exception -> 0x0eef }
        r8 = 0;
        r9 = new byte[r8];	 Catch:{ Exception -> 0x0eef }
        r6.file_reference = r9;	 Catch:{ Exception -> 0x0eef }
    L_0x0aeb:
        r6 = r5;
        r5 = 0;
    L_0x0aed:
        r8 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0eef }
        r8.<init>(r3);	 Catch:{ Exception -> 0x0eef }
        r9 = 3;
        r8.type = r9;	 Catch:{ Exception -> 0x0eef }
        r8.obj = r11;	 Catch:{ Exception -> 0x0eef }
        r8.parentObject = r10;	 Catch:{ Exception -> 0x0eef }
        r8.inputUploadMedia = r2;	 Catch:{ Exception -> 0x0eef }
        r8.performMediaUpload = r5;	 Catch:{ Exception -> 0x0eef }
        r51 = r1;
        r2 = r8;
        r1 = r10;
        r10 = r5;
        goto L_0x0cf0;
    L_0x0b04:
        r26 = r5;
        r19 = r8;
        r5 = 10;
        if (r1 != r5) goto L_0x0b1c;
    L_0x0b0c:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll;	 Catch:{ Exception -> 0x0eef }
        r5.<init>();	 Catch:{ Exception -> 0x0eef }
        r8 = r23;
        r6 = r8.poll;	 Catch:{ Exception -> 0x0eef }
        r5.poll = r6;	 Catch:{ Exception -> 0x0eef }
        r51 = r1;
        r6 = r5;
    L_0x0b1a:
        r1 = r10;
        goto L_0x0b20;
    L_0x0b1c:
        r51 = r1;
        r1 = r10;
        r6 = 0;
    L_0x0b20:
        r10 = 0;
        goto L_0x0cf0;
    L_0x0b23:
        r26 = r5;
        r19 = r8;
        if (r12 != 0) goto L_0x0b38;
    L_0x0b29:
        r5 = r49;
        if (r5 != 0) goto L_0x0b38;
    L_0x0b2d:
        r5 = r13.access_hash;	 Catch:{ Exception -> 0x0eef }
        r8 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1));
        if (r8 != 0) goto L_0x0b34;
    L_0x0b33:
        goto L_0x0b38;
    L_0x0b34:
        r5 = 0;
        r6 = 0;
        goto L_0x0baa;
    L_0x0b38:
        if (r9 != 0) goto L_0x0b6f;
    L_0x0b3a:
        r5 = android.text.TextUtils.isEmpty(r12);	 Catch:{ Exception -> 0x0eef }
        if (r5 != 0) goto L_0x0b6f;
    L_0x0b40:
        r5 = "http";
        r5 = r12.startsWith(r5);	 Catch:{ Exception -> 0x0eef }
        if (r5 == 0) goto L_0x0b6f;
    L_0x0b48:
        if (r44 == 0) goto L_0x0b6f;
    L_0x0b4a:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaGifExternal;	 Catch:{ Exception -> 0x0eef }
        r5.<init>();	 Catch:{ Exception -> 0x0eef }
        r6 = "url";
        r8 = r44;
        r6 = r8.get(r6);	 Catch:{ Exception -> 0x0eef }
        r6 = (java.lang.String) r6;	 Catch:{ Exception -> 0x0eef }
        r8 = "\\|";
        r6 = r6.split(r8);	 Catch:{ Exception -> 0x0eef }
        r8 = r6.length;	 Catch:{ Exception -> 0x0eef }
        r9 = 2;
        if (r8 != r9) goto L_0x0b6d;
    L_0x0b63:
        r8 = 0;
        r9 = r6[r8];	 Catch:{ Exception -> 0x0eef }
        r5.url = r9;	 Catch:{ Exception -> 0x0eef }
        r8 = 1;
        r6 = r6[r8];	 Catch:{ Exception -> 0x0eef }
        r5.q = r6;	 Catch:{ Exception -> 0x0eef }
    L_0x0b6d:
        r6 = 1;
        goto L_0x0ba2;
    L_0x0b6f:
        r8 = r44;
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0eef }
        r5.<init>();	 Catch:{ Exception -> 0x0eef }
        if (r14 == 0) goto L_0x0b82;
    L_0x0b78:
        r5.ttl_seconds = r14;	 Catch:{ Exception -> 0x0eef }
        r7.ttl = r14;	 Catch:{ Exception -> 0x0eef }
        r6 = r5.flags;	 Catch:{ Exception -> 0x0eef }
        r9 = 2;
        r6 = r6 | r9;
        r5.flags = r6;	 Catch:{ Exception -> 0x0eef }
    L_0x0b82:
        r6 = android.text.TextUtils.isEmpty(r49);	 Catch:{ Exception -> 0x0eef }
        if (r6 != 0) goto L_0x0ba1;
    L_0x0b88:
        r6 = r49.toLowerCase();	 Catch:{ Exception -> 0x0eef }
        r9 = "mp4";
        r6 = r6.endsWith(r9);	 Catch:{ Exception -> 0x0eef }
        if (r6 == 0) goto L_0x0ba1;
    L_0x0b94:
        if (r8 == 0) goto L_0x0b9e;
    L_0x0b96:
        r6 = "forceDocument";
        r6 = r8.containsKey(r6);	 Catch:{ Exception -> 0x0eef }
        if (r6 == 0) goto L_0x0ba1;
    L_0x0b9e:
        r6 = 1;
        r5.nosound_video = r6;	 Catch:{ Exception -> 0x0eef }
    L_0x0ba1:
        r6 = 0;
    L_0x0ba2:
        r8 = r13.mime_type;	 Catch:{ Exception -> 0x0eef }
        r5.mime_type = r8;	 Catch:{ Exception -> 0x0eef }
        r8 = r13.attributes;	 Catch:{ Exception -> 0x0eef }
        r5.attributes = r8;	 Catch:{ Exception -> 0x0eef }
    L_0x0baa:
        r8 = r13.access_hash;	 Catch:{ Exception -> 0x0eef }
        r14 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r14 != 0) goto L_0x0bb9;
    L_0x0bb0:
        r8 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0eef }
        r51 = r1;
        r50 = r2;
        r10 = r8;
        r8 = r5;
        goto L_0x0be9;
    L_0x0bb9:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0eef }
        r8.<init>();	 Catch:{ Exception -> 0x0eef }
        r9 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0eef }
        r9.<init>();	 Catch:{ Exception -> 0x0eef }
        r8.id = r9;	 Catch:{ Exception -> 0x0eef }
        r9 = r8.id;	 Catch:{ Exception -> 0x0eef }
        r51 = r1;
        r50 = r2;
        r1 = r13.id;	 Catch:{ Exception -> 0x0eef }
        r9.id = r1;	 Catch:{ Exception -> 0x0eef }
        r1 = r8.id;	 Catch:{ Exception -> 0x0eef }
        r9 = r13.access_hash;	 Catch:{ Exception -> 0x0eef }
        r1.access_hash = r9;	 Catch:{ Exception -> 0x0eef }
        r1 = r8.id;	 Catch:{ Exception -> 0x0eef }
        r2 = r13.file_reference;	 Catch:{ Exception -> 0x0eef }
        r1.file_reference = r2;	 Catch:{ Exception -> 0x0eef }
        r1 = r8.id;	 Catch:{ Exception -> 0x0eef }
        r1 = r1.file_reference;	 Catch:{ Exception -> 0x0eef }
        if (r1 != 0) goto L_0x0be8;
    L_0x0be1:
        r1 = r8.id;	 Catch:{ Exception -> 0x0eef }
        r2 = 0;
        r9 = new byte[r2];	 Catch:{ Exception -> 0x0eef }
        r1.file_reference = r9;	 Catch:{ Exception -> 0x0eef }
    L_0x0be8:
        r10 = 0;
    L_0x0be9:
        if (r6 != 0) goto L_0x0CLASSNAME;
    L_0x0beb:
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0bed:
        r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0eef }
        r2.<init>(r3);	 Catch:{ Exception -> 0x0eef }
        r2.originalPath = r12;	 Catch:{ Exception -> 0x0eef }
        r1 = 2;
        r2.type = r1;	 Catch:{ Exception -> 0x0eef }
        r2.obj = r11;	 Catch:{ Exception -> 0x0eef }
        r1 = r13.thumbs;	 Catch:{ Exception -> 0x0eef }
        r1 = r1.isEmpty();	 Catch:{ Exception -> 0x0eef }
        if (r1 != 0) goto L_0x0c0e;
    L_0x0CLASSNAME:
        r1 = r13.thumbs;	 Catch:{ Exception -> 0x0eef }
        r6 = 0;
        r1 = r1.get(r6);	 Catch:{ Exception -> 0x0eef }
        r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1;	 Catch:{ Exception -> 0x0eef }
        r2.photoSize = r1;	 Catch:{ Exception -> 0x0eef }
        r2.locationParent = r13;	 Catch:{ Exception -> 0x0eef }
    L_0x0c0e:
        r1 = r58;
        r2.parentObject = r1;	 Catch:{ Exception -> 0x0eef }
        r2.inputUploadMedia = r5;	 Catch:{ Exception -> 0x0eef }
        r2.performMediaUpload = r10;	 Catch:{ Exception -> 0x0eef }
        goto L_0x0c1b;
    L_0x0CLASSNAME:
        r1 = r58;
        r2 = r50;
    L_0x0c1b:
        r6 = r8;
        goto L_0x0cf0;
    L_0x0c1e:
        r8 = r44;
        r14 = r46;
        r50 = r2;
        r26 = r5;
        r55 = r10;
        r19 = r12;
        r12 = r21;
        r11 = r51;
        r51 = r1;
        r1 = r58;
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x0eef }
        r2.<init>();	 Catch:{ Exception -> 0x0eef }
        if (r14 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2.ttl_seconds = r14;	 Catch:{ Exception -> 0x0eef }
        r7.ttl = r14;	 Catch:{ Exception -> 0x0eef }
        r5 = r2.flags;	 Catch:{ Exception -> 0x0eef }
        r6 = 2;
        r5 = r5 | r6;
        r2.flags = r5;	 Catch:{ Exception -> 0x0eef }
    L_0x0CLASSNAME:
        if (r8 == 0) goto L_0x0c7a;
    L_0x0CLASSNAME:
        r5 = "masks";
        r5 = r8.get(r5);	 Catch:{ Exception -> 0x0eef }
        r5 = (java.lang.String) r5;	 Catch:{ Exception -> 0x0eef }
        if (r5 == 0) goto L_0x0c7a;
    L_0x0c4f:
        r6 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0eef }
        r5 = org.telegram.messenger.Utilities.hexToBytes(r5);	 Catch:{ Exception -> 0x0eef }
        r6.<init>(r5);	 Catch:{ Exception -> 0x0eef }
        r5 = 0;
        r8 = r6.readInt32(r5);	 Catch:{ Exception -> 0x0eef }
        r9 = 0;
    L_0x0c5e:
        if (r9 >= r8) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r10 = r2.stickers;	 Catch:{ Exception -> 0x0eef }
        r13 = r6.readInt32(r5);	 Catch:{ Exception -> 0x0eef }
        r13 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r6, r13, r5);	 Catch:{ Exception -> 0x0eef }
        r10.add(r13);	 Catch:{ Exception -> 0x0eef }
        r9 = r9 + 1;
        r5 = 0;
        goto L_0x0c5e;
    L_0x0CLASSNAME:
        r5 = r2.flags;	 Catch:{ Exception -> 0x0eef }
        r8 = 1;
        r5 = r5 | r8;
        r2.flags = r5;	 Catch:{ Exception -> 0x0eef }
        r6.cleanup();	 Catch:{ Exception -> 0x0eef }
    L_0x0c7a:
        r5 = r41;
        r8 = r5.access_hash;	 Catch:{ Exception -> 0x0eef }
        r6 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r6 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = r2;
        r10 = 1;
        goto L_0x0cb1;
    L_0x0CLASSNAME:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x0eef }
        r6.<init>();	 Catch:{ Exception -> 0x0eef }
        r8 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x0eef }
        r8.<init>();	 Catch:{ Exception -> 0x0eef }
        r6.id = r8;	 Catch:{ Exception -> 0x0eef }
        r8 = r6.id;	 Catch:{ Exception -> 0x0eef }
        r9 = r5.id;	 Catch:{ Exception -> 0x0eef }
        r8.id = r9;	 Catch:{ Exception -> 0x0eef }
        r8 = r6.id;	 Catch:{ Exception -> 0x0eef }
        r9 = r5.access_hash;	 Catch:{ Exception -> 0x0eef }
        r8.access_hash = r9;	 Catch:{ Exception -> 0x0eef }
        r8 = r6.id;	 Catch:{ Exception -> 0x0eef }
        r9 = r5.file_reference;	 Catch:{ Exception -> 0x0eef }
        r8.file_reference = r9;	 Catch:{ Exception -> 0x0eef }
        r8 = r6.id;	 Catch:{ Exception -> 0x0eef }
        r8 = r8.file_reference;	 Catch:{ Exception -> 0x0eef }
        if (r8 != 0) goto L_0x0cb0;
    L_0x0ca9:
        r8 = r6.id;	 Catch:{ Exception -> 0x0eef }
        r9 = 0;
        r10 = new byte[r9];	 Catch:{ Exception -> 0x0eef }
        r8.file_reference = r10;	 Catch:{ Exception -> 0x0eef }
    L_0x0cb0:
        r10 = 0;
    L_0x0cb1:
        if (r50 != 0) goto L_0x0cc0;
    L_0x0cb3:
        r8 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0eef }
        r8.<init>(r3);	 Catch:{ Exception -> 0x0eef }
        r9 = 0;
        r8.type = r9;	 Catch:{ Exception -> 0x0eef }
        r8.obj = r11;	 Catch:{ Exception -> 0x0eef }
        r8.originalPath = r12;	 Catch:{ Exception -> 0x0eef }
        goto L_0x0cc2;
    L_0x0cc0:
        r8 = r50;
    L_0x0cc2:
        r8.inputUploadMedia = r2;	 Catch:{ Exception -> 0x0eef }
        r8.performMediaUpload = r10;	 Catch:{ Exception -> 0x0eef }
        r2 = r49;
        if (r2 == 0) goto L_0x0cdb;
    L_0x0cca:
        r9 = r49.length();	 Catch:{ Exception -> 0x0eef }
        if (r9 <= 0) goto L_0x0cdb;
    L_0x0cd0:
        r9 = "http";
        r9 = r2.startsWith(r9);	 Catch:{ Exception -> 0x0eef }
        if (r9 == 0) goto L_0x0cdb;
    L_0x0cd8:
        r8.httpLocation = r2;	 Catch:{ Exception -> 0x0eef }
        goto L_0x0cef;
    L_0x0cdb:
        r2 = r5.sizes;	 Catch:{ Exception -> 0x0eef }
        r9 = r5.sizes;	 Catch:{ Exception -> 0x0eef }
        r9 = r9.size();	 Catch:{ Exception -> 0x0eef }
        r13 = 1;
        r9 = r9 - r13;
        r2 = r2.get(r9);	 Catch:{ Exception -> 0x0eef }
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;	 Catch:{ Exception -> 0x0eef }
        r8.photoSize = r2;	 Catch:{ Exception -> 0x0eef }
        r8.locationParent = r5;	 Catch:{ Exception -> 0x0eef }
    L_0x0cef:
        r2 = r8;
    L_0x0cf0:
        if (r43 == 0) goto L_0x0d32;
    L_0x0cf2:
        r5 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x0eef }
        r5.<init>();	 Catch:{ Exception -> 0x0eef }
        r8 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0eef }
        r8.<init>();	 Catch:{ Exception -> 0x0eef }
        r9 = 0;
    L_0x0cfd:
        r13 = r43.size();	 Catch:{ Exception -> 0x0eef }
        if (r9 >= r13) goto L_0x0d13;
    L_0x0d03:
        r13 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0eef }
        r13 = r13.nextLong();	 Catch:{ Exception -> 0x0eef }
        r13 = java.lang.Long.valueOf(r13);	 Catch:{ Exception -> 0x0eef }
        r8.add(r13);	 Catch:{ Exception -> 0x0eef }
        r9 = r9 + 1;
        goto L_0x0cfd;
    L_0x0d13:
        r13 = r43;
        r5.contacts = r13;	 Catch:{ Exception -> 0x0eef }
        r5.media = r6;	 Catch:{ Exception -> 0x0eef }
        r5.random_id = r8;	 Catch:{ Exception -> 0x0eef }
        r6 = r19;
        r5.message = r6;	 Catch:{ Exception -> 0x0eef }
        if (r2 == 0) goto L_0x0d23;
    L_0x0d21:
        r2.sendRequest = r5;	 Catch:{ Exception -> 0x0eef }
    L_0x0d23:
        r14 = r53;
        if (r14 != 0) goto L_0x0d2f;
    L_0x0d27:
        r6 = r37.getMediaDataController();	 Catch:{ Exception -> 0x0eef }
        r8 = 0;
        r6.cleanDraft(r3, r8);	 Catch:{ Exception -> 0x0eef }
    L_0x0d2f:
        r13 = r5;
        goto L_0x0e31;
    L_0x0d32:
        r5 = (r55 > r16 ? 1 : (r55 == r16 ? 0 : -1));
        if (r5 == 0) goto L_0x0dd5;
    L_0x0d36:
        r5 = r2.sendRequest;	 Catch:{ Exception -> 0x0eef }
        if (r5 == 0) goto L_0x0d3f;
    L_0x0d3a:
        r3 = r2.sendRequest;	 Catch:{ Exception -> 0x0eef }
        r3 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r3;	 Catch:{ Exception -> 0x0eef }
        goto L_0x0d7d;
    L_0x0d3f:
        r5 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;	 Catch:{ Exception -> 0x0eef }
        r5.<init>();	 Catch:{ Exception -> 0x0eef }
        r8 = r26;
        r5.peer = r8;	 Catch:{ Exception -> 0x0eef }
        r8 = r7.to_id;	 Catch:{ Exception -> 0x0eef }
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0eef }
        if (r8 == 0) goto L_0x0d6c;
    L_0x0d4e:
        r8 = r15.currentAccount;	 Catch:{ Exception -> 0x0eef }
        r8 = org.telegram.messenger.MessagesController.getNotificationsSettings(r8);	 Catch:{ Exception -> 0x0eef }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0eef }
        r9.<init>();	 Catch:{ Exception -> 0x0eef }
        r13 = "silent_";
        r9.append(r13);	 Catch:{ Exception -> 0x0eef }
        r9.append(r3);	 Catch:{ Exception -> 0x0eef }
        r3 = r9.toString();	 Catch:{ Exception -> 0x0eef }
        r4 = 0;
        r3 = r8.getBoolean(r3, r4);	 Catch:{ Exception -> 0x0eef }
        r5.silent = r3;	 Catch:{ Exception -> 0x0eef }
    L_0x0d6c:
        r3 = r7.reply_to_msg_id;	 Catch:{ Exception -> 0x0eef }
        if (r3 == 0) goto L_0x0d7a;
    L_0x0d70:
        r3 = r5.flags;	 Catch:{ Exception -> 0x0eef }
        r4 = 1;
        r3 = r3 | r4;
        r5.flags = r3;	 Catch:{ Exception -> 0x0eef }
        r3 = r7.reply_to_msg_id;	 Catch:{ Exception -> 0x0eef }
        r5.reply_to_msg_id = r3;	 Catch:{ Exception -> 0x0eef }
    L_0x0d7a:
        r2.sendRequest = r5;	 Catch:{ Exception -> 0x0eef }
        r3 = r5;
    L_0x0d7d:
        r4 = r2.messageObjects;	 Catch:{ Exception -> 0x0eef }
        r4.add(r11);	 Catch:{ Exception -> 0x0eef }
        r4 = r2.parentObjects;	 Catch:{ Exception -> 0x0eef }
        r4.add(r1);	 Catch:{ Exception -> 0x0eef }
        r4 = r2.locations;	 Catch:{ Exception -> 0x0eef }
        r5 = r2.photoSize;	 Catch:{ Exception -> 0x0eef }
        r4.add(r5);	 Catch:{ Exception -> 0x0eef }
        r4 = r2.videoEditedInfos;	 Catch:{ Exception -> 0x0eef }
        r5 = r2.videoEditedInfo;	 Catch:{ Exception -> 0x0eef }
        r4.add(r5);	 Catch:{ Exception -> 0x0eef }
        r4 = r2.httpLocations;	 Catch:{ Exception -> 0x0eef }
        r5 = r2.httpLocation;	 Catch:{ Exception -> 0x0eef }
        r4.add(r5);	 Catch:{ Exception -> 0x0eef }
        r4 = r2.inputMedias;	 Catch:{ Exception -> 0x0eef }
        r5 = r2.inputUploadMedia;	 Catch:{ Exception -> 0x0eef }
        r4.add(r5);	 Catch:{ Exception -> 0x0eef }
        r4 = r2.messages;	 Catch:{ Exception -> 0x0eef }
        r4.add(r7);	 Catch:{ Exception -> 0x0eef }
        r4 = r2.originalPaths;	 Catch:{ Exception -> 0x0eef }
        r4.add(r12);	 Catch:{ Exception -> 0x0eef }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia;	 Catch:{ Exception -> 0x0eef }
        r4.<init>();	 Catch:{ Exception -> 0x0eef }
        r8 = r7.random_id;	 Catch:{ Exception -> 0x0eef }
        r4.random_id = r8;	 Catch:{ Exception -> 0x0eef }
        r4.media = r6;	 Catch:{ Exception -> 0x0eef }
        r5 = r45;
        r4.message = r5;	 Catch:{ Exception -> 0x0eef }
        r9 = r54;
        if (r9 == 0) goto L_0x0dce;
    L_0x0dc0:
        r5 = r54.isEmpty();	 Catch:{ Exception -> 0x0eef }
        if (r5 != 0) goto L_0x0dce;
    L_0x0dc6:
        r4.entities = r9;	 Catch:{ Exception -> 0x0eef }
        r5 = r4.flags;	 Catch:{ Exception -> 0x0eef }
        r6 = 1;
        r5 = r5 | r6;
        r4.flags = r5;	 Catch:{ Exception -> 0x0eef }
    L_0x0dce:
        r5 = r3.multi_media;	 Catch:{ Exception -> 0x0eef }
        r5.add(r4);	 Catch:{ Exception -> 0x0eef }
        r13 = r3;
        goto L_0x0e31;
    L_0x0dd5:
        r5 = r45;
        r9 = r54;
        r8 = r26;
        r13 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia;	 Catch:{ Exception -> 0x0eef }
        r13.<init>();	 Catch:{ Exception -> 0x0eef }
        r13.peer = r8;	 Catch:{ Exception -> 0x0eef }
        r8 = r7.to_id;	 Catch:{ Exception -> 0x0eef }
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0eef }
        if (r8 == 0) goto L_0x0e06;
    L_0x0de8:
        r8 = r15.currentAccount;	 Catch:{ Exception -> 0x0eef }
        r8 = org.telegram.messenger.MessagesController.getNotificationsSettings(r8);	 Catch:{ Exception -> 0x0eef }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0eef }
        r14.<init>();	 Catch:{ Exception -> 0x0eef }
        r1 = "silent_";
        r14.append(r1);	 Catch:{ Exception -> 0x0eef }
        r14.append(r3);	 Catch:{ Exception -> 0x0eef }
        r1 = r14.toString();	 Catch:{ Exception -> 0x0eef }
        r3 = 0;
        r1 = r8.getBoolean(r1, r3);	 Catch:{ Exception -> 0x0eef }
        r13.silent = r1;	 Catch:{ Exception -> 0x0eef }
    L_0x0e06:
        r1 = r7.reply_to_msg_id;	 Catch:{ Exception -> 0x0eef }
        if (r1 == 0) goto L_0x0e14;
    L_0x0e0a:
        r1 = r13.flags;	 Catch:{ Exception -> 0x0eef }
        r3 = 1;
        r1 = r1 | r3;
        r13.flags = r1;	 Catch:{ Exception -> 0x0eef }
        r1 = r7.reply_to_msg_id;	 Catch:{ Exception -> 0x0eef }
        r13.reply_to_msg_id = r1;	 Catch:{ Exception -> 0x0eef }
    L_0x0e14:
        r3 = r7.random_id;	 Catch:{ Exception -> 0x0eef }
        r13.random_id = r3;	 Catch:{ Exception -> 0x0eef }
        r13.media = r6;	 Catch:{ Exception -> 0x0eef }
        r13.message = r5;	 Catch:{ Exception -> 0x0eef }
        if (r9 == 0) goto L_0x0e2d;
    L_0x0e1e:
        r1 = r54.isEmpty();	 Catch:{ Exception -> 0x0eef }
        if (r1 != 0) goto L_0x0e2d;
    L_0x0e24:
        r13.entities = r9;	 Catch:{ Exception -> 0x0eef }
        r1 = r13.flags;	 Catch:{ Exception -> 0x0eef }
        r3 = 8;
        r1 = r1 | r3;
        r13.flags = r1;	 Catch:{ Exception -> 0x0eef }
    L_0x0e2d:
        if (r2 == 0) goto L_0x0e31;
    L_0x0e2f:
        r2.sendRequest = r13;	 Catch:{ Exception -> 0x0eef }
    L_0x0e31:
        r1 = (r55 > r16 ? 1 : (r55 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x0e3a;
    L_0x0e35:
        r15.performSendDelayedMessage(r2);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0e3a:
        r1 = r51;
        r3 = 1;
        if (r1 != r3) goto L_0x0e51;
    L_0x0e3f:
        r1 = 0;
        r38 = r37;
        r39 = r13;
        r40 = r11;
        r41 = r1;
        r42 = r2;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0e51:
        r3 = 2;
        if (r1 != r3) goto L_0x0e72;
    L_0x0e54:
        if (r10 == 0) goto L_0x0e5b;
    L_0x0e56:
        r15.performSendDelayedMessage(r2);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0e5b:
        r1 = 0;
        r3 = 1;
        r38 = r37;
        r39 = r13;
        r40 = r11;
        r41 = r12;
        r42 = r1;
        r43 = r3;
        r44 = r2;
        r45 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43, r44, r45);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0e72:
        r3 = 3;
        if (r1 != r3) goto L_0x0e8d;
    L_0x0e75:
        if (r10 == 0) goto L_0x0e7c;
    L_0x0e77:
        r15.performSendDelayedMessage(r2);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0e7c:
        r38 = r37;
        r39 = r13;
        r40 = r11;
        r41 = r12;
        r42 = r2;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0e8d:
        r3 = 6;
        if (r1 != r3) goto L_0x0ea1;
    L_0x0e90:
        r38 = r37;
        r39 = r13;
        r40 = r11;
        r41 = r12;
        r42 = r2;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0ea1:
        r3 = 7;
        if (r1 != r3) goto L_0x0ebe;
    L_0x0ea4:
        if (r10 == 0) goto L_0x0ead;
    L_0x0ea6:
        if (r2 == 0) goto L_0x0ead;
    L_0x0ea8:
        r15.performSendDelayedMessage(r2);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0ead:
        r38 = r37;
        r39 = r13;
        r40 = r11;
        r41 = r12;
        r42 = r2;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0ebe:
        r3 = 8;
        if (r1 != r3) goto L_0x0eda;
    L_0x0ec2:
        if (r10 == 0) goto L_0x0ec9;
    L_0x0ec4:
        r15.performSendDelayedMessage(r2);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0ec9:
        r38 = r37;
        r39 = r13;
        r40 = r11;
        r41 = r12;
        r42 = r2;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0eda:
        r3 = 10;
        if (r1 != r3) goto L_0x17c9;
    L_0x0ede:
        r38 = r37;
        r39 = r13;
        r40 = r11;
        r41 = r12;
        r42 = r2;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x0eef }
        goto L_0x17c9;
    L_0x0eef:
        r0 = move-exception;
        goto L_0x01f8;
    L_0x0ef2:
        r5 = r41;
        r14 = r44;
        r50 = r2;
        r41 = r6;
        r55 = r10;
        r13 = r18;
        r35 = r20;
        r6 = r26;
        r2 = r45;
        r11 = r51;
        r10 = r54;
        r12 = r9.layer;	 Catch:{ Exception -> 0x14a7 }
        r12 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r12);	 Catch:{ Exception -> 0x14a7 }
        r3 = 73;
        if (r12 < r3) goto L_0x0var_;
    L_0x0var_:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x0eef }
        r3.<init>();	 Catch:{ Exception -> 0x0eef }
        r4 = (r55 > r16 ? 1 : (r55 == r16 ? 0 : -1));
        if (r4 == 0) goto L_0x0f2b;
    L_0x0f1b:
        r4 = r13;
        r12 = r55;
        r3.grouped_id = r12;	 Catch:{ Exception -> 0x0eef }
        r38 = r4;
        r4 = r3.flags;	 Catch:{ Exception -> 0x0eef }
        r18 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r4 = r4 | r18;
        r3.flags = r4;	 Catch:{ Exception -> 0x0eef }
        goto L_0x0var_;
    L_0x0f2b:
        r38 = r13;
        r12 = r55;
        goto L_0x0var_;
    L_0x0var_:
        r38 = r13;
        r12 = r55;
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x14a7 }
        r3.<init>();	 Catch:{ Exception -> 0x14a7 }
    L_0x0var_:
        r4 = r7.ttl;	 Catch:{ Exception -> 0x14a7 }
        r3.ttl = r4;	 Catch:{ Exception -> 0x14a7 }
        if (r10 == 0) goto L_0x0f4d;
    L_0x0f3f:
        r4 = r54.isEmpty();	 Catch:{ Exception -> 0x0eef }
        if (r4 != 0) goto L_0x0f4d;
    L_0x0var_:
        r3.entities = r10;	 Catch:{ Exception -> 0x0eef }
        r4 = r3.flags;	 Catch:{ Exception -> 0x0eef }
        r4 = r4 | 128;
        r3.flags = r4;	 Catch:{ Exception -> 0x0eef }
    L_0x0f4d:
        r55 = r12;
        r12 = r7.reply_to_random_id;	 Catch:{ Exception -> 0x14a7 }
        r4 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
        if (r4 == 0) goto L_0x0var_;
    L_0x0var_:
        r12 = r7.reply_to_random_id;	 Catch:{ Exception -> 0x0eef }
        r3.reply_to_random_id = r12;	 Catch:{ Exception -> 0x0eef }
        r4 = r3.flags;	 Catch:{ Exception -> 0x0eef }
        r10 = 8;
        r4 = r4 | r10;
        r3.flags = r4;	 Catch:{ Exception -> 0x0eef }
    L_0x0var_:
        r4 = r3.flags;	 Catch:{ Exception -> 0x14a7 }
        r4 = r4 | 512;
        r3.flags = r4;	 Catch:{ Exception -> 0x14a7 }
        if (r14 == 0) goto L_0x0f7e;
    L_0x0var_:
        r4 = r40;
        r10 = r14.get(r4);	 Catch:{ Exception -> 0x0eef }
        if (r10 == 0) goto L_0x0f7e;
    L_0x0var_:
        r4 = r14.get(r4);	 Catch:{ Exception -> 0x0eef }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0eef }
        r3.via_bot_name = r4;	 Catch:{ Exception -> 0x0eef }
        r4 = r3.flags;	 Catch:{ Exception -> 0x0eef }
        r4 = r4 | 2048;
        r3.flags = r4;	 Catch:{ Exception -> 0x0eef }
    L_0x0f7e:
        r12 = r7.random_id;	 Catch:{ Exception -> 0x14a7 }
        r3.random_id = r12;	 Catch:{ Exception -> 0x14a7 }
        r3.message = r6;	 Catch:{ Exception -> 0x14a7 }
        r4 = 1;
        if (r1 != r4) goto L_0x0fe5;
    L_0x0var_:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x0eef }
        if (r2 == 0) goto L_0x0fab;
    L_0x0f8b:
        r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue;	 Catch:{ Exception -> 0x0eef }
        r2.<init>();	 Catch:{ Exception -> 0x0eef }
        r3.media = r2;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r4 = r8.address;	 Catch:{ Exception -> 0x0eef }
        r2.address = r4;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r4 = r8.title;	 Catch:{ Exception -> 0x0eef }
        r2.title = r4;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r4 = r8.provider;	 Catch:{ Exception -> 0x0eef }
        r2.provider = r4;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r4 = r8.venue_id;	 Catch:{ Exception -> 0x0eef }
        r2.venue_id = r4;	 Catch:{ Exception -> 0x0eef }
        goto L_0x0fb2;
    L_0x0fab:
        r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint;	 Catch:{ Exception -> 0x0eef }
        r2.<init>();	 Catch:{ Exception -> 0x0eef }
        r3.media = r2;	 Catch:{ Exception -> 0x0eef }
    L_0x0fb2:
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r4 = r8.geo;	 Catch:{ Exception -> 0x0eef }
        r4 = r4.lat;	 Catch:{ Exception -> 0x0eef }
        r2.lat = r4;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r4 = r8.geo;	 Catch:{ Exception -> 0x0eef }
        r4 = r4._long;	 Catch:{ Exception -> 0x0eef }
        r2._long = r4;	 Catch:{ Exception -> 0x0eef }
        r2 = r37.getSecretChatHelper();	 Catch:{ Exception -> 0x0eef }
        r4 = r11.messageOwner;	 Catch:{ Exception -> 0x0eef }
        r5 = 0;
        r6 = 0;
        r40 = r2;
        r41 = r3;
        r42 = r4;
        r43 = r9;
        r44 = r5;
        r45 = r6;
        r46 = r11;
        r40.performSendEncryptedRequest(r41, r42, r43, r44, r45, r46);	 Catch:{ Exception -> 0x0eef }
        r12 = r47;
        r51 = r1;
        r18 = r7;
        r6 = r21;
        goto L_0x1330;
    L_0x0fe5:
        r4 = "parentObject";
        r6 = 2;
        if (r1 == r6) goto L_0x133f;
    L_0x0fea:
        r6 = 9;
        if (r1 != r6) goto L_0x0ff2;
    L_0x0fee:
        if (r5 == 0) goto L_0x0ff2;
    L_0x0ff0:
        goto L_0x133f;
    L_0x0ff2:
        r5 = 3;
        if (r1 != r5) goto L_0x1120;
    L_0x0ff5:
        r5 = r38;
        r6 = r5.thumbs;	 Catch:{ Exception -> 0x0eef }
        r6 = r15.getThumbForSecretChat(r6);	 Catch:{ Exception -> 0x0eef }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r6);	 Catch:{ Exception -> 0x0eef }
        r8 = org.telegram.messenger.MessageObject.isNewGifDocument(r5);	 Catch:{ Exception -> 0x0eef }
        if (r8 != 0) goto L_0x102d;
    L_0x1006:
        r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r5);	 Catch:{ Exception -> 0x0eef }
        if (r8 == 0) goto L_0x100d;
    L_0x100c:
        goto L_0x102d;
    L_0x100d:
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo;	 Catch:{ Exception -> 0x0eef }
        r8.<init>();	 Catch:{ Exception -> 0x0eef }
        r3.media = r8;	 Catch:{ Exception -> 0x0eef }
        if (r6 == 0) goto L_0x1023;
    L_0x1016:
        r8 = r6.bytes;	 Catch:{ Exception -> 0x0eef }
        if (r8 == 0) goto L_0x1023;
    L_0x101a:
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r8;	 Catch:{ Exception -> 0x0eef }
        r10 = r6.bytes;	 Catch:{ Exception -> 0x0eef }
        r8.thumb = r10;	 Catch:{ Exception -> 0x0eef }
        goto L_0x1052;
    L_0x1023:
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r8;	 Catch:{ Exception -> 0x0eef }
        r10 = 0;
        r12 = new byte[r10];	 Catch:{ Exception -> 0x0eef }
        r8.thumb = r12;	 Catch:{ Exception -> 0x0eef }
        goto L_0x1052;
    L_0x102d:
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x0eef }
        r8.<init>();	 Catch:{ Exception -> 0x0eef }
        r3.media = r8;	 Catch:{ Exception -> 0x0eef }
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r10 = r5.attributes;	 Catch:{ Exception -> 0x0eef }
        r8.attributes = r10;	 Catch:{ Exception -> 0x0eef }
        if (r6 == 0) goto L_0x1049;
    L_0x103c:
        r8 = r6.bytes;	 Catch:{ Exception -> 0x0eef }
        if (r8 == 0) goto L_0x1049;
    L_0x1040:
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r8;	 Catch:{ Exception -> 0x0eef }
        r10 = r6.bytes;	 Catch:{ Exception -> 0x0eef }
        r8.thumb = r10;	 Catch:{ Exception -> 0x0eef }
        goto L_0x1052;
    L_0x1049:
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r8;	 Catch:{ Exception -> 0x0eef }
        r10 = 0;
        r12 = new byte[r10];	 Catch:{ Exception -> 0x0eef }
        r8.thumb = r12;	 Catch:{ Exception -> 0x0eef }
    L_0x1052:
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8.caption = r2;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = "video/mp4";
        r2.mime_type = r8;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = r5.size;	 Catch:{ Exception -> 0x0eef }
        r2.size = r8;	 Catch:{ Exception -> 0x0eef }
        r2 = 0;
    L_0x1063:
        r8 = r5.attributes;	 Catch:{ Exception -> 0x0eef }
        r8 = r8.size();	 Catch:{ Exception -> 0x0eef }
        if (r2 >= r8) goto L_0x108d;
    L_0x106b:
        r8 = r5.attributes;	 Catch:{ Exception -> 0x0eef }
        r8 = r8.get(r2);	 Catch:{ Exception -> 0x0eef }
        r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8;	 Catch:{ Exception -> 0x0eef }
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x0eef }
        if (r10 == 0) goto L_0x108a;
    L_0x1077:
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r10 = r8.w;	 Catch:{ Exception -> 0x0eef }
        r2.w = r10;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r10 = r8.h;	 Catch:{ Exception -> 0x0eef }
        r2.h = r10;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = r8.duration;	 Catch:{ Exception -> 0x0eef }
        r2.duration = r8;	 Catch:{ Exception -> 0x0eef }
        goto L_0x108d;
    L_0x108a:
        r2 = r2 + 1;
        goto L_0x1063;
    L_0x108d:
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = r6.h;	 Catch:{ Exception -> 0x0eef }
        r2.thumb_h = r8;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r6 = r6.w;	 Catch:{ Exception -> 0x0eef }
        r2.thumb_w = r6;	 Catch:{ Exception -> 0x0eef }
        r2 = r5.key;	 Catch:{ Exception -> 0x0eef }
        if (r2 == 0) goto L_0x10da;
    L_0x109d:
        r2 = (r55 > r16 ? 1 : (r55 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x10a2;
    L_0x10a1:
        goto L_0x10da;
    L_0x10a2:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0eef }
        r2.<init>();	 Catch:{ Exception -> 0x0eef }
        r12 = r5.id;	 Catch:{ Exception -> 0x0eef }
        r2.id = r12;	 Catch:{ Exception -> 0x0eef }
        r12 = r5.access_hash;	 Catch:{ Exception -> 0x0eef }
        r2.access_hash = r12;	 Catch:{ Exception -> 0x0eef }
        r4 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r6 = r5.key;	 Catch:{ Exception -> 0x0eef }
        r4.key = r6;	 Catch:{ Exception -> 0x0eef }
        r4 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r5 = r5.iv;	 Catch:{ Exception -> 0x0eef }
        r4.iv = r5;	 Catch:{ Exception -> 0x0eef }
        r4 = r37.getSecretChatHelper();	 Catch:{ Exception -> 0x0eef }
        r5 = r11.messageOwner;	 Catch:{ Exception -> 0x0eef }
        r6 = 0;
        r40 = r4;
        r41 = r3;
        r42 = r5;
        r43 = r9;
        r44 = r2;
        r45 = r6;
        r46 = r11;
        r40.performSendEncryptedRequest(r41, r42, r43, r44, r45, r46);	 Catch:{ Exception -> 0x0eef }
        r12 = r47;
        r2 = r50;
        r6 = r21;
        goto L_0x111c;
    L_0x10da:
        if (r50 != 0) goto L_0x110a;
    L_0x10dc:
        r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0eef }
        r12 = r47;
        r2.<init>(r12);	 Catch:{ Exception -> 0x0eef }
        r2.encryptedChat = r9;	 Catch:{ Exception -> 0x0eef }
        r5 = 1;
        r2.type = r5;	 Catch:{ Exception -> 0x0eef }
        r2.sendEncryptedRequest = r3;	 Catch:{ Exception -> 0x0eef }
        r6 = r21;
        r2.originalPath = r6;	 Catch:{ Exception -> 0x0eef }
        r2.obj = r11;	 Catch:{ Exception -> 0x0eef }
        if (r14 == 0) goto L_0x10ff;
    L_0x10f2:
        r5 = r14.containsKey(r4);	 Catch:{ Exception -> 0x0eef }
        if (r5 == 0) goto L_0x10ff;
    L_0x10f8:
        r4 = r14.get(r4);	 Catch:{ Exception -> 0x0eef }
        r2.parentObject = r4;	 Catch:{ Exception -> 0x0eef }
        goto L_0x1103;
    L_0x10ff:
        r10 = r58;
        r2.parentObject = r10;	 Catch:{ Exception -> 0x0eef }
    L_0x1103:
        r4 = 1;
        r2.performMediaUpload = r4;	 Catch:{ Exception -> 0x0eef }
        r4 = r2;
        r2 = r41;
        goto L_0x1112;
    L_0x110a:
        r12 = r47;
        r6 = r21;
        r2 = r41;
        r4 = r50;
    L_0x1112:
        r4.videoEditedInfo = r2;	 Catch:{ Exception -> 0x0eef }
        r2 = (r55 > r16 ? 1 : (r55 == r16 ? 0 : -1));
        if (r2 != 0) goto L_0x111b;
    L_0x1118:
        r15.performSendDelayedMessage(r4);	 Catch:{ Exception -> 0x0eef }
    L_0x111b:
        r2 = r4;
    L_0x111c:
        r51 = r1;
        goto L_0x11df;
    L_0x1120:
        r5 = r38;
        r12 = r47;
        r10 = r58;
        r6 = r21;
        r8 = 6;
        if (r1 != r8) goto L_0x1167;
    L_0x112b:
        r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact;	 Catch:{ Exception -> 0x0eef }
        r2.<init>();	 Catch:{ Exception -> 0x0eef }
        r3.media = r2;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r5 = r35;
        r4 = r5.phone;	 Catch:{ Exception -> 0x0eef }
        r2.phone_number = r4;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r4 = r5.first_name;	 Catch:{ Exception -> 0x0eef }
        r2.first_name = r4;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r4 = r5.last_name;	 Catch:{ Exception -> 0x0eef }
        r2.last_name = r4;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r4 = r5.id;	 Catch:{ Exception -> 0x0eef }
        r2.user_id = r4;	 Catch:{ Exception -> 0x0eef }
        r2 = r37.getSecretChatHelper();	 Catch:{ Exception -> 0x0eef }
        r4 = r11.messageOwner;	 Catch:{ Exception -> 0x0eef }
        r5 = 0;
        r8 = 0;
        r40 = r2;
        r41 = r3;
        r42 = r4;
        r43 = r9;
        r44 = r5;
        r45 = r8;
        r46 = r11;
        r40.performSendEncryptedRequest(r41, r42, r43, r44, r45, r46);	 Catch:{ Exception -> 0x0eef }
        goto L_0x11e3;
    L_0x1167:
        r8 = 7;
        if (r1 == r8) goto L_0x11e9;
    L_0x116a:
        r8 = 9;
        if (r1 != r8) goto L_0x1172;
    L_0x116e:
        if (r5 == 0) goto L_0x1172;
    L_0x1170:
        goto L_0x11e9;
    L_0x1172:
        r4 = 8;
        if (r1 != r4) goto L_0x11e3;
    L_0x1176:
        r4 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0eef }
        r4.<init>(r12);	 Catch:{ Exception -> 0x0eef }
        r4.encryptedChat = r9;	 Catch:{ Exception -> 0x0eef }
        r4.sendEncryptedRequest = r3;	 Catch:{ Exception -> 0x0eef }
        r4.obj = r11;	 Catch:{ Exception -> 0x0eef }
        r8 = 3;
        r4.type = r8;	 Catch:{ Exception -> 0x0eef }
        r4.parentObject = r10;	 Catch:{ Exception -> 0x0eef }
        r8 = 1;
        r4.performMediaUpload = r8;	 Catch:{ Exception -> 0x0eef }
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x0eef }
        r8.<init>();	 Catch:{ Exception -> 0x0eef }
        r3.media = r8;	 Catch:{ Exception -> 0x0eef }
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r9 = r5.attributes;	 Catch:{ Exception -> 0x0eef }
        r8.attributes = r9;	 Catch:{ Exception -> 0x0eef }
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8.caption = r2;	 Catch:{ Exception -> 0x0eef }
        r2 = r5.thumbs;	 Catch:{ Exception -> 0x0eef }
        r2 = r15.getThumbForSecretChat(r2);	 Catch:{ Exception -> 0x0eef }
        if (r2 == 0) goto L_0x11ba;
    L_0x11a2:
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r2);	 Catch:{ Exception -> 0x0eef }
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r8;	 Catch:{ Exception -> 0x0eef }
        r9 = r2.bytes;	 Catch:{ Exception -> 0x0eef }
        r8.thumb = r9;	 Catch:{ Exception -> 0x0eef }
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r9 = r2.h;	 Catch:{ Exception -> 0x0eef }
        r8.thumb_h = r9;	 Catch:{ Exception -> 0x0eef }
        r8 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r2 = r2.w;	 Catch:{ Exception -> 0x0eef }
        r8.thumb_w = r2;	 Catch:{ Exception -> 0x0eef }
        goto L_0x11cb;
    L_0x11ba:
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r2 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r2;	 Catch:{ Exception -> 0x0eef }
        r8 = 0;
        r9 = new byte[r8];	 Catch:{ Exception -> 0x0eef }
        r2.thumb = r9;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r2.thumb_h = r8;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r2.thumb_w = r8;	 Catch:{ Exception -> 0x0eef }
    L_0x11cb:
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = r5.mime_type;	 Catch:{ Exception -> 0x0eef }
        r2.mime_type = r8;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r5 = r5.size;	 Catch:{ Exception -> 0x0eef }
        r2.size = r5;	 Catch:{ Exception -> 0x0eef }
        r4.originalPath = r6;	 Catch:{ Exception -> 0x0eef }
        r15.performSendDelayedMessage(r4);	 Catch:{ Exception -> 0x0eef }
        r51 = r1;
        r2 = r4;
    L_0x11df:
        r18 = r7;
        goto L_0x144c;
    L_0x11e3:
        r51 = r1;
        r18 = r7;
        goto L_0x1330;
    L_0x11e9:
        r8 = org.telegram.messenger.MessageObject.isStickerDocument(r5);	 Catch:{ Exception -> 0x133a }
        if (r8 != 0) goto L_0x12be;
    L_0x11ef:
        r8 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r5);	 Catch:{ Exception -> 0x133a }
        if (r8 == 0) goto L_0x11f7;
    L_0x11f5:
        goto L_0x12be;
    L_0x11f7:
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x133a }
        r8.<init>();	 Catch:{ Exception -> 0x133a }
        r3.media = r8;	 Catch:{ Exception -> 0x133a }
        r8 = r3.media;	 Catch:{ Exception -> 0x133a }
        r51 = r1;
        r1 = r5.attributes;	 Catch:{ Exception -> 0x133a }
        r8.attributes = r1;	 Catch:{ Exception -> 0x133a }
        r1 = r3.media;	 Catch:{ Exception -> 0x133a }
        r1.caption = r2;	 Catch:{ Exception -> 0x133a }
        r1 = r5.thumbs;	 Catch:{ Exception -> 0x133a }
        r1 = r15.getThumbForSecretChat(r1);	 Catch:{ Exception -> 0x133a }
        if (r1 == 0) goto L_0x122a;
    L_0x1212:
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1);	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r2 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r2;	 Catch:{ Exception -> 0x0eef }
        r8 = r1.bytes;	 Catch:{ Exception -> 0x0eef }
        r2.thumb = r8;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r8 = r1.h;	 Catch:{ Exception -> 0x0eef }
        r2.thumb_h = r8;	 Catch:{ Exception -> 0x0eef }
        r2 = r3.media;	 Catch:{ Exception -> 0x0eef }
        r1 = r1.w;	 Catch:{ Exception -> 0x0eef }
        r2.thumb_w = r1;	 Catch:{ Exception -> 0x0eef }
        goto L_0x123b;
    L_0x122a:
        r1 = r3.media;	 Catch:{ Exception -> 0x133a }
        r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r1;	 Catch:{ Exception -> 0x133a }
        r2 = 0;
        r8 = new byte[r2];	 Catch:{ Exception -> 0x133a }
        r1.thumb = r8;	 Catch:{ Exception -> 0x133a }
        r1 = r3.media;	 Catch:{ Exception -> 0x133a }
        r1.thumb_h = r2;	 Catch:{ Exception -> 0x133a }
        r1 = r3.media;	 Catch:{ Exception -> 0x133a }
        r1.thumb_w = r2;	 Catch:{ Exception -> 0x133a }
    L_0x123b:
        r1 = r3.media;	 Catch:{ Exception -> 0x133a }
        r2 = r5.size;	 Catch:{ Exception -> 0x133a }
        r1.size = r2;	 Catch:{ Exception -> 0x133a }
        r1 = r3.media;	 Catch:{ Exception -> 0x133a }
        r2 = r5.mime_type;	 Catch:{ Exception -> 0x133a }
        r1.mime_type = r2;	 Catch:{ Exception -> 0x133a }
        r1 = r5.key;	 Catch:{ Exception -> 0x133a }
        if (r1 != 0) goto L_0x1289;
    L_0x124b:
        r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0eef }
        r1.<init>(r12);	 Catch:{ Exception -> 0x0eef }
        r1.originalPath = r6;	 Catch:{ Exception -> 0x0eef }
        r1.sendEncryptedRequest = r3;	 Catch:{ Exception -> 0x0eef }
        r2 = 2;
        r1.type = r2;	 Catch:{ Exception -> 0x0eef }
        r1.obj = r11;	 Catch:{ Exception -> 0x0eef }
        if (r14 == 0) goto L_0x1268;
    L_0x125b:
        r2 = r14.containsKey(r4);	 Catch:{ Exception -> 0x0eef }
        if (r2 == 0) goto L_0x1268;
    L_0x1261:
        r2 = r14.get(r4);	 Catch:{ Exception -> 0x0eef }
        r1.parentObject = r2;	 Catch:{ Exception -> 0x0eef }
        goto L_0x126a;
    L_0x1268:
        r1.parentObject = r10;	 Catch:{ Exception -> 0x0eef }
    L_0x126a:
        r1.encryptedChat = r9;	 Catch:{ Exception -> 0x0eef }
        r2 = 1;
        r1.performMediaUpload = r2;	 Catch:{ Exception -> 0x0eef }
        r2 = r49;
        if (r2 == 0) goto L_0x1283;
    L_0x1273:
        r4 = r49.length();	 Catch:{ Exception -> 0x0eef }
        if (r4 <= 0) goto L_0x1283;
    L_0x1279:
        r4 = "http";
        r4 = r2.startsWith(r4);	 Catch:{ Exception -> 0x0eef }
        if (r4 == 0) goto L_0x1283;
    L_0x1281:
        r1.httpLocation = r2;	 Catch:{ Exception -> 0x0eef }
    L_0x1283:
        r15.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0eef }
        r2 = r1;
        goto L_0x11df;
    L_0x1289:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x133a }
        r1.<init>();	 Catch:{ Exception -> 0x133a }
        r18 = r7;
        r7 = r5.id;	 Catch:{ Exception -> 0x1334 }
        r1.id = r7;	 Catch:{ Exception -> 0x1334 }
        r7 = r5.access_hash;	 Catch:{ Exception -> 0x1334 }
        r1.access_hash = r7;	 Catch:{ Exception -> 0x1334 }
        r2 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r4 = r5.key;	 Catch:{ Exception -> 0x1334 }
        r2.key = r4;	 Catch:{ Exception -> 0x1334 }
        r2 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r4 = r5.iv;	 Catch:{ Exception -> 0x1334 }
        r2.iv = r4;	 Catch:{ Exception -> 0x1334 }
        r2 = r37.getSecretChatHelper();	 Catch:{ Exception -> 0x1334 }
        r4 = r11.messageOwner;	 Catch:{ Exception -> 0x1334 }
        r5 = 0;
        r40 = r2;
        r41 = r3;
        r42 = r4;
        r43 = r9;
        r44 = r1;
        r45 = r5;
        r46 = r11;
        r40.performSendEncryptedRequest(r41, r42, r43, r44, r45, r46);	 Catch:{ Exception -> 0x1334 }
        goto L_0x1330;
    L_0x12be:
        r51 = r1;
        r18 = r7;
        r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument;	 Catch:{ Exception -> 0x1334 }
        r1.<init>();	 Catch:{ Exception -> 0x1334 }
        r3.media = r1;	 Catch:{ Exception -> 0x1334 }
        r1 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r7 = r5.id;	 Catch:{ Exception -> 0x1334 }
        r1.id = r7;	 Catch:{ Exception -> 0x1334 }
        r1 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r2 = r5.date;	 Catch:{ Exception -> 0x1334 }
        r1.date = r2;	 Catch:{ Exception -> 0x1334 }
        r1 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r7 = r5.access_hash;	 Catch:{ Exception -> 0x1334 }
        r1.access_hash = r7;	 Catch:{ Exception -> 0x1334 }
        r1 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r2 = r5.mime_type;	 Catch:{ Exception -> 0x1334 }
        r1.mime_type = r2;	 Catch:{ Exception -> 0x1334 }
        r1 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r2 = r5.size;	 Catch:{ Exception -> 0x1334 }
        r1.size = r2;	 Catch:{ Exception -> 0x1334 }
        r1 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r2 = r5.dc_id;	 Catch:{ Exception -> 0x1334 }
        r1.dc_id = r2;	 Catch:{ Exception -> 0x1334 }
        r1 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r2 = r5.attributes;	 Catch:{ Exception -> 0x1334 }
        r1.attributes = r2;	 Catch:{ Exception -> 0x1334 }
        r1 = r5.thumbs;	 Catch:{ Exception -> 0x1334 }
        r1 = r15.getThumbForSecretChat(r1);	 Catch:{ Exception -> 0x1334 }
        if (r1 == 0) goto L_0x1302;
    L_0x12fb:
        r2 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r2 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r2;	 Catch:{ Exception -> 0x1334 }
        r2.thumb = r1;	 Catch:{ Exception -> 0x1334 }
        goto L_0x1317;
    L_0x1302:
        r1 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r1;	 Catch:{ Exception -> 0x1334 }
        r2 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x1334 }
        r2.<init>();	 Catch:{ Exception -> 0x1334 }
        r1.thumb = r2;	 Catch:{ Exception -> 0x1334 }
        r1 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r1 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r1;	 Catch:{ Exception -> 0x1334 }
        r1 = r1.thumb;	 Catch:{ Exception -> 0x1334 }
        r2 = "s";
        r1.type = r2;	 Catch:{ Exception -> 0x1334 }
    L_0x1317:
        r1 = r37.getSecretChatHelper();	 Catch:{ Exception -> 0x1334 }
        r2 = r11.messageOwner;	 Catch:{ Exception -> 0x1334 }
        r4 = 0;
        r5 = 0;
        r40 = r1;
        r41 = r3;
        r42 = r2;
        r43 = r9;
        r44 = r4;
        r45 = r5;
        r46 = r11;
        r40.performSendEncryptedRequest(r41, r42, r43, r44, r45, r46);	 Catch:{ Exception -> 0x1334 }
    L_0x1330:
        r2 = r50;
        goto L_0x144c;
    L_0x1334:
        r0 = move-exception;
        r1 = r0;
        r7 = r18;
        goto L_0x179e;
    L_0x133a:
        r0 = move-exception;
        r18 = r7;
        goto L_0x01f8;
    L_0x133f:
        r12 = r47;
        r10 = r58;
        r51 = r1;
        r18 = r7;
        r6 = r21;
        r7 = r2;
        r2 = r49;
        r1 = r5.sizes;	 Catch:{ Exception -> 0x14a2 }
        r8 = 0;
        r1 = r1.get(r8);	 Catch:{ Exception -> 0x14a2 }
        r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1;	 Catch:{ Exception -> 0x14a2 }
        r8 = r5.sizes;	 Catch:{ Exception -> 0x14a2 }
        r2 = r5.sizes;	 Catch:{ Exception -> 0x14a2 }
        r2 = r2.size();	 Catch:{ Exception -> 0x14a2 }
        r19 = 1;
        r2 = r2 + -1;
        r2 = r8.get(r2);	 Catch:{ Exception -> 0x14a2 }
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;	 Catch:{ Exception -> 0x14a2 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1);	 Catch:{ Exception -> 0x14a2 }
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto;	 Catch:{ Exception -> 0x14a2 }
        r8.<init>();	 Catch:{ Exception -> 0x14a2 }
        r3.media = r8;	 Catch:{ Exception -> 0x14a2 }
        r8 = r3.media;	 Catch:{ Exception -> 0x14a2 }
        r8.caption = r7;	 Catch:{ Exception -> 0x14a2 }
        r7 = r1.bytes;	 Catch:{ Exception -> 0x14a2 }
        if (r7 == 0) goto L_0x1384;
    L_0x1379:
        r7 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r7 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r7;	 Catch:{ Exception -> 0x1334 }
        r8 = r1.bytes;	 Catch:{ Exception -> 0x1334 }
        r7.thumb = r8;	 Catch:{ Exception -> 0x1334 }
        r41 = r5;
        goto L_0x138f;
    L_0x1384:
        r7 = r3.media;	 Catch:{ Exception -> 0x14a2 }
        r7 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r7;	 Catch:{ Exception -> 0x14a2 }
        r41 = r5;
        r8 = 0;
        r5 = new byte[r8];	 Catch:{ Exception -> 0x14a2 }
        r7.thumb = r5;	 Catch:{ Exception -> 0x14a2 }
    L_0x138f:
        r5 = r3.media;	 Catch:{ Exception -> 0x14a2 }
        r7 = r1.h;	 Catch:{ Exception -> 0x14a2 }
        r5.thumb_h = r7;	 Catch:{ Exception -> 0x14a2 }
        r5 = r3.media;	 Catch:{ Exception -> 0x14a2 }
        r1 = r1.w;	 Catch:{ Exception -> 0x14a2 }
        r5.thumb_w = r1;	 Catch:{ Exception -> 0x14a2 }
        r1 = r3.media;	 Catch:{ Exception -> 0x14a2 }
        r5 = r2.w;	 Catch:{ Exception -> 0x14a2 }
        r1.w = r5;	 Catch:{ Exception -> 0x14a2 }
        r1 = r3.media;	 Catch:{ Exception -> 0x14a2 }
        r5 = r2.h;	 Catch:{ Exception -> 0x14a2 }
        r1.h = r5;	 Catch:{ Exception -> 0x14a2 }
        r1 = r3.media;	 Catch:{ Exception -> 0x14a2 }
        r5 = r2.size;	 Catch:{ Exception -> 0x14a2 }
        r1.size = r5;	 Catch:{ Exception -> 0x14a2 }
        r1 = r2.location;	 Catch:{ Exception -> 0x14a2 }
        r1 = r1.key;	 Catch:{ Exception -> 0x14a2 }
        if (r1 == 0) goto L_0x13f3;
    L_0x13b3:
        r1 = (r55 > r16 ? 1 : (r55 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x13b8;
    L_0x13b7:
        goto L_0x13f3;
    L_0x13b8:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x1334 }
        r1.<init>();	 Catch:{ Exception -> 0x1334 }
        r4 = r2.location;	 Catch:{ Exception -> 0x1334 }
        r4 = r4.volume_id;	 Catch:{ Exception -> 0x1334 }
        r1.id = r4;	 Catch:{ Exception -> 0x1334 }
        r4 = r2.location;	 Catch:{ Exception -> 0x1334 }
        r4 = r4.secret;	 Catch:{ Exception -> 0x1334 }
        r1.access_hash = r4;	 Catch:{ Exception -> 0x1334 }
        r4 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r5 = r2.location;	 Catch:{ Exception -> 0x1334 }
        r5 = r5.key;	 Catch:{ Exception -> 0x1334 }
        r4.key = r5;	 Catch:{ Exception -> 0x1334 }
        r4 = r3.media;	 Catch:{ Exception -> 0x1334 }
        r2 = r2.location;	 Catch:{ Exception -> 0x1334 }
        r2 = r2.iv;	 Catch:{ Exception -> 0x1334 }
        r4.iv = r2;	 Catch:{ Exception -> 0x1334 }
        r2 = r37.getSecretChatHelper();	 Catch:{ Exception -> 0x1334 }
        r4 = r11.messageOwner;	 Catch:{ Exception -> 0x1334 }
        r5 = 0;
        r40 = r2;
        r41 = r3;
        r42 = r4;
        r43 = r9;
        r44 = r1;
        r45 = r5;
        r46 = r11;
        r40.performSendEncryptedRequest(r41, r42, r43, r44, r45, r46);	 Catch:{ Exception -> 0x1334 }
        goto L_0x1330;
    L_0x13f3:
        if (r50 != 0) goto L_0x141a;
    L_0x13f5:
        r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x1334 }
        r2.<init>(r12);	 Catch:{ Exception -> 0x1334 }
        r2.encryptedChat = r9;	 Catch:{ Exception -> 0x1334 }
        r1 = 0;
        r2.type = r1;	 Catch:{ Exception -> 0x1334 }
        r2.originalPath = r6;	 Catch:{ Exception -> 0x1334 }
        r2.sendEncryptedRequest = r3;	 Catch:{ Exception -> 0x1334 }
        r2.obj = r11;	 Catch:{ Exception -> 0x1334 }
        if (r14 == 0) goto L_0x1414;
    L_0x1407:
        r1 = r14.containsKey(r4);	 Catch:{ Exception -> 0x1334 }
        if (r1 == 0) goto L_0x1414;
    L_0x140d:
        r1 = r14.get(r4);	 Catch:{ Exception -> 0x1334 }
        r2.parentObject = r1;	 Catch:{ Exception -> 0x1334 }
        goto L_0x1416;
    L_0x1414:
        r2.parentObject = r10;	 Catch:{ Exception -> 0x1334 }
    L_0x1416:
        r1 = 1;
        r2.performMediaUpload = r1;	 Catch:{ Exception -> 0x1334 }
        goto L_0x141c;
    L_0x141a:
        r2 = r50;
    L_0x141c:
        r1 = android.text.TextUtils.isEmpty(r49);	 Catch:{ Exception -> 0x14a2 }
        if (r1 != 0) goto L_0x142f;
    L_0x1422:
        r1 = "http";
        r4 = r49;
        r1 = r4.startsWith(r1);	 Catch:{ Exception -> 0x1334 }
        if (r1 == 0) goto L_0x142f;
    L_0x142c:
        r2.httpLocation = r4;	 Catch:{ Exception -> 0x1334 }
        goto L_0x1445;
    L_0x142f:
        r4 = r41;
        r1 = r4.sizes;	 Catch:{ Exception -> 0x14a2 }
        r5 = r4.sizes;	 Catch:{ Exception -> 0x14a2 }
        r5 = r5.size();	 Catch:{ Exception -> 0x14a2 }
        r7 = 1;
        r5 = r5 - r7;
        r1 = r1.get(r5);	 Catch:{ Exception -> 0x14a2 }
        r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1;	 Catch:{ Exception -> 0x14a2 }
        r2.photoSize = r1;	 Catch:{ Exception -> 0x14a2 }
        r2.locationParent = r4;	 Catch:{ Exception -> 0x14a2 }
    L_0x1445:
        r1 = (r55 > r16 ? 1 : (r55 == r16 ? 0 : -1));
        if (r1 != 0) goto L_0x144c;
    L_0x1449:
        r15.performSendDelayedMessage(r2);	 Catch:{ Exception -> 0x1334 }
    L_0x144c:
        r1 = (r55 > r16 ? 1 : (r55 == r16 ? 0 : -1));
        if (r1 == 0) goto L_0x1492;
    L_0x1450:
        r1 = r2.sendEncryptedRequest;	 Catch:{ Exception -> 0x14a2 }
        if (r1 == 0) goto L_0x1459;
    L_0x1454:
        r1 = r2.sendEncryptedRequest;	 Catch:{ Exception -> 0x1334 }
        r1 = (org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r1;	 Catch:{ Exception -> 0x1334 }
        goto L_0x1460;
    L_0x1459:
        r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;	 Catch:{ Exception -> 0x14a2 }
        r1.<init>();	 Catch:{ Exception -> 0x14a2 }
        r2.sendEncryptedRequest = r1;	 Catch:{ Exception -> 0x14a2 }
    L_0x1460:
        r4 = r2.messageObjects;	 Catch:{ Exception -> 0x14a2 }
        r4.add(r11);	 Catch:{ Exception -> 0x14a2 }
        r4 = r2.messages;	 Catch:{ Exception -> 0x14a2 }
        r5 = r18;
        r4.add(r5);	 Catch:{ Exception -> 0x1778 }
        r4 = r2.originalPaths;	 Catch:{ Exception -> 0x1778 }
        r4.add(r6);	 Catch:{ Exception -> 0x1778 }
        r4 = 1;
        r2.performMediaUpload = r4;	 Catch:{ Exception -> 0x1778 }
        r4 = r1.messages;	 Catch:{ Exception -> 0x1778 }
        r4.add(r3);	 Catch:{ Exception -> 0x1778 }
        r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x1778 }
        r3.<init>();	 Catch:{ Exception -> 0x1778 }
        r4 = r51;
        r6 = 3;
        if (r4 != r6) goto L_0x1485;
    L_0x1483:
        r16 = 1;
    L_0x1485:
        r6 = r16;
        r3.id = r6;	 Catch:{ Exception -> 0x1778 }
        r1 = r1.files;	 Catch:{ Exception -> 0x1778 }
        r1.add(r3);	 Catch:{ Exception -> 0x1778 }
        r15.performSendDelayedMessage(r2);	 Catch:{ Exception -> 0x1778 }
        goto L_0x1494;
    L_0x1492:
        r5 = r18;
    L_0x1494:
        r1 = r53;
        if (r1 != 0) goto L_0x17c9;
    L_0x1498:
        r1 = r37.getMediaDataController();	 Catch:{ Exception -> 0x1778 }
        r2 = 0;
        r1.cleanDraft(r12, r2);	 Catch:{ Exception -> 0x1778 }
        goto L_0x17c9;
    L_0x14a2:
        r0 = move-exception;
        r5 = r18;
        goto L_0x1779;
    L_0x14a7:
        r0 = move-exception;
        goto L_0x177f;
    L_0x14aa:
        r2 = r44;
        r11 = r51;
        r10 = r58;
        r12 = r3;
        r8 = r5;
        r5 = r7;
        r3 = 4;
        r4 = r1;
        r1 = r53;
        if (r4 != r3) goto L_0x157e;
    L_0x14b9:
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;	 Catch:{ Exception -> 0x1778 }
        r2.<init>();	 Catch:{ Exception -> 0x1778 }
        r2.to_peer = r8;	 Catch:{ Exception -> 0x1778 }
        r3 = r1.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r3 = r3.with_my_score;	 Catch:{ Exception -> 0x1778 }
        r2.with_my_score = r3;	 Catch:{ Exception -> 0x1778 }
        r3 = r1.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r3 = r3.ttl;	 Catch:{ Exception -> 0x1778 }
        if (r3 == 0) goto L_0x14f6;
    L_0x14cc:
        r3 = r37.getMessagesController();	 Catch:{ Exception -> 0x1778 }
        r4 = r1.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r4 = r4.ttl;	 Catch:{ Exception -> 0x1778 }
        r4 = -r4;
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x1778 }
        r3 = r3.getChat(r4);	 Catch:{ Exception -> 0x1778 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;	 Catch:{ Exception -> 0x1778 }
        r4.<init>();	 Catch:{ Exception -> 0x1778 }
        r2.from_peer = r4;	 Catch:{ Exception -> 0x1778 }
        r4 = r2.from_peer;	 Catch:{ Exception -> 0x1778 }
        r6 = r1.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x1778 }
        r6 = -r6;
        r4.channel_id = r6;	 Catch:{ Exception -> 0x1778 }
        if (r3 == 0) goto L_0x14fd;
    L_0x14ef:
        r4 = r2.from_peer;	 Catch:{ Exception -> 0x1778 }
        r6 = r3.access_hash;	 Catch:{ Exception -> 0x1778 }
        r4.access_hash = r6;	 Catch:{ Exception -> 0x1778 }
        goto L_0x14fd;
    L_0x14f6:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;	 Catch:{ Exception -> 0x1778 }
        r3.<init>();	 Catch:{ Exception -> 0x1778 }
        r2.from_peer = r3;	 Catch:{ Exception -> 0x1778 }
    L_0x14fd:
        r3 = r1.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r3 = r3.to_id;	 Catch:{ Exception -> 0x1778 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x1778 }
        if (r3 == 0) goto L_0x1523;
    L_0x1505:
        r3 = r15.currentAccount;	 Catch:{ Exception -> 0x1778 }
        r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3);	 Catch:{ Exception -> 0x1778 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x1778 }
        r4.<init>();	 Catch:{ Exception -> 0x1778 }
        r6 = "silent_";
        r4.append(r6);	 Catch:{ Exception -> 0x1778 }
        r4.append(r12);	 Catch:{ Exception -> 0x1778 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x1778 }
        r6 = 0;
        r3 = r3.getBoolean(r4, r6);	 Catch:{ Exception -> 0x1778 }
        r2.silent = r3;	 Catch:{ Exception -> 0x1778 }
    L_0x1523:
        r3 = r2.random_id;	 Catch:{ Exception -> 0x1778 }
        r6 = r5.random_id;	 Catch:{ Exception -> 0x1778 }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x1778 }
        r3.add(r4);	 Catch:{ Exception -> 0x1778 }
        r3 = r53.getId();	 Catch:{ Exception -> 0x1778 }
        if (r3 < 0) goto L_0x1542;
    L_0x1534:
        r3 = r2.id;	 Catch:{ Exception -> 0x1778 }
        r1 = r53.getId();	 Catch:{ Exception -> 0x1778 }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x1778 }
        r3.add(r1);	 Catch:{ Exception -> 0x1778 }
        goto L_0x156b;
    L_0x1542:
        r3 = r1.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r3 = r3.fwd_msg_id;	 Catch:{ Exception -> 0x1778 }
        if (r3 == 0) goto L_0x1556;
    L_0x1548:
        r3 = r2.id;	 Catch:{ Exception -> 0x1778 }
        r1 = r1.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r1 = r1.fwd_msg_id;	 Catch:{ Exception -> 0x1778 }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x1778 }
        r3.add(r1);	 Catch:{ Exception -> 0x1778 }
        goto L_0x156b;
    L_0x1556:
        r3 = r1.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r3 = r3.fwd_from;	 Catch:{ Exception -> 0x1778 }
        if (r3 == 0) goto L_0x156b;
    L_0x155c:
        r3 = r2.id;	 Catch:{ Exception -> 0x1778 }
        r1 = r1.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r1 = r1.fwd_from;	 Catch:{ Exception -> 0x1778 }
        r1 = r1.channel_post;	 Catch:{ Exception -> 0x1778 }
        r1 = java.lang.Integer.valueOf(r1);	 Catch:{ Exception -> 0x1778 }
        r3.add(r1);	 Catch:{ Exception -> 0x1778 }
    L_0x156b:
        r1 = 0;
        r3 = 0;
        r38 = r37;
        r39 = r2;
        r40 = r11;
        r41 = r1;
        r42 = r3;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x1778 }
        goto L_0x17c9;
    L_0x157e:
        r3 = 9;
        if (r4 != r3) goto L_0x17c9;
    L_0x1582:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult;	 Catch:{ Exception -> 0x1778 }
        r3.<init>();	 Catch:{ Exception -> 0x1778 }
        r3.peer = r8;	 Catch:{ Exception -> 0x1778 }
        r6 = r5.random_id;	 Catch:{ Exception -> 0x1778 }
        r3.random_id = r6;	 Catch:{ Exception -> 0x1778 }
        r4 = "bot";
        r4 = r2.containsKey(r4);	 Catch:{ Exception -> 0x1778 }
        if (r4 != 0) goto L_0x1597;
    L_0x1595:
        r4 = 1;
        goto L_0x1598;
    L_0x1597:
        r4 = 0;
    L_0x1598:
        r3.hide_via = r4;	 Catch:{ Exception -> 0x1778 }
        r4 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x1778 }
        if (r4 == 0) goto L_0x15a8;
    L_0x159e:
        r4 = r3.flags;	 Catch:{ Exception -> 0x1778 }
        r6 = 1;
        r4 = r4 | r6;
        r3.flags = r4;	 Catch:{ Exception -> 0x1778 }
        r4 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x1778 }
        r3.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x1778 }
    L_0x15a8:
        r4 = r5.to_id;	 Catch:{ Exception -> 0x1778 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x1778 }
        if (r4 == 0) goto L_0x15cc;
    L_0x15ae:
        r4 = r15.currentAccount;	 Catch:{ Exception -> 0x1778 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x1778 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x1778 }
        r6.<init>();	 Catch:{ Exception -> 0x1778 }
        r7 = "silent_";
        r6.append(r7);	 Catch:{ Exception -> 0x1778 }
        r6.append(r12);	 Catch:{ Exception -> 0x1778 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x1778 }
        r7 = 0;
        r4 = r4.getBoolean(r6, r7);	 Catch:{ Exception -> 0x1778 }
        r3.silent = r4;	 Catch:{ Exception -> 0x1778 }
    L_0x15cc:
        r4 = r19;
        r4 = r2.get(r4);	 Catch:{ Exception -> 0x1778 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x1778 }
        r4 = org.telegram.messenger.Utilities.parseLong(r4);	 Catch:{ Exception -> 0x1778 }
        r6 = r4.longValue();	 Catch:{ Exception -> 0x1778 }
        r3.query_id = r6;	 Catch:{ Exception -> 0x1778 }
        r4 = "id";
        r2 = r2.get(r4);	 Catch:{ Exception -> 0x1778 }
        r2 = (java.lang.String) r2;	 Catch:{ Exception -> 0x1778 }
        r3.id = r2;	 Catch:{ Exception -> 0x1778 }
        if (r1 != 0) goto L_0x15f5;
    L_0x15ea:
        r1 = 1;
        r3.clear_draft = r1;	 Catch:{ Exception -> 0x1778 }
        r1 = r37.getMediaDataController();	 Catch:{ Exception -> 0x1778 }
        r2 = 0;
        r1.cleanDraft(r12, r2);	 Catch:{ Exception -> 0x1778 }
    L_0x15f5:
        r1 = 0;
        r2 = 0;
        r38 = r37;
        r39 = r3;
        r40 = r11;
        r41 = r1;
        r42 = r2;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x1778 }
        goto L_0x17c9;
    L_0x1608:
        r13 = r43;
        r2 = r44;
        r11 = r51;
        r1 = r53;
        r10 = r54;
        r8 = r5;
        r5 = r7;
        r6 = r3;
        r4 = r40;
        if (r9 != 0) goto L_0x16d9;
    L_0x1619:
        if (r13 == 0) goto L_0x165e;
    L_0x161b:
        r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x1778 }
        r1.<init>();	 Catch:{ Exception -> 0x1778 }
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x1778 }
        r2.<init>();	 Catch:{ Exception -> 0x1778 }
        r3 = 0;
    L_0x1626:
        r4 = r13.size();	 Catch:{ Exception -> 0x1778 }
        if (r3 >= r4) goto L_0x163c;
    L_0x162c:
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x1778 }
        r6 = r4.nextLong();	 Catch:{ Exception -> 0x1778 }
        r4 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x1778 }
        r2.add(r4);	 Catch:{ Exception -> 0x1778 }
        r3 = r3 + 1;
        goto L_0x1626;
    L_0x163c:
        r3 = r38;
        r1.message = r3;	 Catch:{ Exception -> 0x1778 }
        r1.contacts = r13;	 Catch:{ Exception -> 0x1778 }
        r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaEmpty;	 Catch:{ Exception -> 0x1778 }
        r3.<init>();	 Catch:{ Exception -> 0x1778 }
        r1.media = r3;	 Catch:{ Exception -> 0x1778 }
        r1.random_id = r2;	 Catch:{ Exception -> 0x1778 }
        r2 = 0;
        r3 = 0;
        r38 = r37;
        r39 = r1;
        r40 = r11;
        r41 = r2;
        r42 = r3;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x1778 }
        goto L_0x17c9;
    L_0x165e:
        r3 = r38;
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage;	 Catch:{ Exception -> 0x1778 }
        r2.<init>();	 Catch:{ Exception -> 0x1778 }
        r2.message = r3;	 Catch:{ Exception -> 0x1778 }
        if (r1 != 0) goto L_0x166b;
    L_0x1669:
        r3 = 1;
        goto L_0x166c;
    L_0x166b:
        r3 = 0;
    L_0x166c:
        r2.clear_draft = r3;	 Catch:{ Exception -> 0x1778 }
        r3 = r5.to_id;	 Catch:{ Exception -> 0x1778 }
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x1778 }
        if (r3 == 0) goto L_0x1692;
    L_0x1674:
        r3 = r15.currentAccount;	 Catch:{ Exception -> 0x1778 }
        r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3);	 Catch:{ Exception -> 0x1778 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x1778 }
        r4.<init>();	 Catch:{ Exception -> 0x1778 }
        r9 = "silent_";
        r4.append(r9);	 Catch:{ Exception -> 0x1778 }
        r4.append(r6);	 Catch:{ Exception -> 0x1778 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x1778 }
        r9 = 0;
        r3 = r3.getBoolean(r4, r9);	 Catch:{ Exception -> 0x1778 }
        r2.silent = r3;	 Catch:{ Exception -> 0x1778 }
    L_0x1692:
        r2.peer = r8;	 Catch:{ Exception -> 0x1778 }
        r3 = r5.random_id;	 Catch:{ Exception -> 0x1778 }
        r2.random_id = r3;	 Catch:{ Exception -> 0x1778 }
        r3 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x1778 }
        if (r3 == 0) goto L_0x16a6;
    L_0x169c:
        r3 = r2.flags;	 Catch:{ Exception -> 0x1778 }
        r4 = 1;
        r3 = r3 | r4;
        r2.flags = r3;	 Catch:{ Exception -> 0x1778 }
        r3 = r5.reply_to_msg_id;	 Catch:{ Exception -> 0x1778 }
        r2.reply_to_msg_id = r3;	 Catch:{ Exception -> 0x1778 }
    L_0x16a6:
        if (r52 != 0) goto L_0x16ab;
    L_0x16a8:
        r3 = 1;
        r2.no_webpage = r3;	 Catch:{ Exception -> 0x1778 }
    L_0x16ab:
        if (r10 == 0) goto L_0x16bc;
    L_0x16ad:
        r3 = r54.isEmpty();	 Catch:{ Exception -> 0x1778 }
        if (r3 != 0) goto L_0x16bc;
    L_0x16b3:
        r2.entities = r10;	 Catch:{ Exception -> 0x1778 }
        r3 = r2.flags;	 Catch:{ Exception -> 0x1778 }
        r4 = 8;
        r3 = r3 | r4;
        r2.flags = r3;	 Catch:{ Exception -> 0x1778 }
    L_0x16bc:
        r3 = 0;
        r4 = 0;
        r38 = r37;
        r39 = r2;
        r40 = r11;
        r41 = r3;
        r42 = r4;
        r43 = r58;
        r38.performSendMessageRequest(r39, r40, r41, r42, r43);	 Catch:{ Exception -> 0x1778 }
        if (r1 != 0) goto L_0x17c9;
    L_0x16cf:
        r1 = r37.getMediaDataController();	 Catch:{ Exception -> 0x1778 }
        r2 = 0;
        r1.cleanDraft(r6, r2);	 Catch:{ Exception -> 0x1778 }
        goto L_0x17c9;
    L_0x16d9:
        r3 = r38;
        r8 = r9.layer;	 Catch:{ Exception -> 0x1778 }
        r8 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r8);	 Catch:{ Exception -> 0x1778 }
        r12 = 73;
        if (r8 < r12) goto L_0x16eb;
    L_0x16e5:
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x1778 }
        r8.<init>();	 Catch:{ Exception -> 0x1778 }
        goto L_0x16f0;
    L_0x16eb:
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x1778 }
        r8.<init>();	 Catch:{ Exception -> 0x1778 }
    L_0x16f0:
        r12 = r5.ttl;	 Catch:{ Exception -> 0x1778 }
        r8.ttl = r12;	 Catch:{ Exception -> 0x1778 }
        if (r10 == 0) goto L_0x1704;
    L_0x16f6:
        r12 = r54.isEmpty();	 Catch:{ Exception -> 0x1778 }
        if (r12 != 0) goto L_0x1704;
    L_0x16fc:
        r8.entities = r10;	 Catch:{ Exception -> 0x1778 }
        r10 = r8.flags;	 Catch:{ Exception -> 0x1778 }
        r10 = r10 | 128;
        r8.flags = r10;	 Catch:{ Exception -> 0x1778 }
    L_0x1704:
        r12 = r5.reply_to_random_id;	 Catch:{ Exception -> 0x1778 }
        r10 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
        if (r10 == 0) goto L_0x1715;
    L_0x170a:
        r12 = r5.reply_to_random_id;	 Catch:{ Exception -> 0x1778 }
        r8.reply_to_random_id = r12;	 Catch:{ Exception -> 0x1778 }
        r10 = r8.flags;	 Catch:{ Exception -> 0x1778 }
        r12 = 8;
        r10 = r10 | r12;
        r8.flags = r10;	 Catch:{ Exception -> 0x1778 }
    L_0x1715:
        if (r2 == 0) goto L_0x172b;
    L_0x1717:
        r10 = r2.get(r4);	 Catch:{ Exception -> 0x1778 }
        if (r10 == 0) goto L_0x172b;
    L_0x171d:
        r2 = r2.get(r4);	 Catch:{ Exception -> 0x1778 }
        r2 = (java.lang.String) r2;	 Catch:{ Exception -> 0x1778 }
        r8.via_bot_name = r2;	 Catch:{ Exception -> 0x1778 }
        r2 = r8.flags;	 Catch:{ Exception -> 0x1778 }
        r2 = r2 | 2048;
        r8.flags = r2;	 Catch:{ Exception -> 0x1778 }
    L_0x172b:
        r12 = r5.random_id;	 Catch:{ Exception -> 0x1778 }
        r8.random_id = r12;	 Catch:{ Exception -> 0x1778 }
        r8.message = r3;	 Catch:{ Exception -> 0x1778 }
        r13 = r28;
        if (r13 == 0) goto L_0x174d;
    L_0x1735:
        r2 = r13.url;	 Catch:{ Exception -> 0x1778 }
        if (r2 == 0) goto L_0x174d;
    L_0x1739:
        r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage;	 Catch:{ Exception -> 0x1778 }
        r2.<init>();	 Catch:{ Exception -> 0x1778 }
        r8.media = r2;	 Catch:{ Exception -> 0x1778 }
        r2 = r8.media;	 Catch:{ Exception -> 0x1778 }
        r3 = r13.url;	 Catch:{ Exception -> 0x1778 }
        r2.url = r3;	 Catch:{ Exception -> 0x1778 }
        r2 = r8.flags;	 Catch:{ Exception -> 0x1778 }
        r2 = r2 | 512;
        r8.flags = r2;	 Catch:{ Exception -> 0x1778 }
        goto L_0x1754;
    L_0x174d:
        r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty;	 Catch:{ Exception -> 0x1778 }
        r2.<init>();	 Catch:{ Exception -> 0x1778 }
        r8.media = r2;	 Catch:{ Exception -> 0x1778 }
    L_0x1754:
        r2 = r37.getSecretChatHelper();	 Catch:{ Exception -> 0x1778 }
        r3 = r11.messageOwner;	 Catch:{ Exception -> 0x1778 }
        r4 = 0;
        r10 = 0;
        r40 = r2;
        r41 = r8;
        r42 = r3;
        r43 = r9;
        r44 = r4;
        r45 = r10;
        r46 = r11;
        r40.performSendEncryptedRequest(r41, r42, r43, r44, r45, r46);	 Catch:{ Exception -> 0x1778 }
        if (r1 != 0) goto L_0x17c9;
    L_0x176f:
        r1 = r37.getMediaDataController();	 Catch:{ Exception -> 0x1778 }
        r2 = 0;
        r1.cleanDraft(r6, r2);	 Catch:{ Exception -> 0x1778 }
        goto L_0x17c9;
    L_0x1778:
        r0 = move-exception;
    L_0x1779:
        r1 = r0;
        r7 = r5;
        goto L_0x179e;
    L_0x177c:
        r0 = move-exception;
        r11 = r51;
    L_0x177f:
        r5 = r7;
        goto L_0x01f8;
    L_0x1782:
        r0 = move-exception;
        r5 = r7;
        r11 = r13;
        goto L_0x01f8;
    L_0x1787:
        r0 = move-exception;
        goto L_0x178c;
    L_0x1789:
        r0 = move-exception;
        r15 = r37;
    L_0x178c:
        r5 = r7;
    L_0x178d:
        r1 = r0;
        goto L_0x01f0;
    L_0x1790:
        r0 = move-exception;
        r15 = r37;
        goto L_0x1799;
    L_0x1794:
        r0 = move-exception;
        r15 = r37;
        r18 = r2;
    L_0x1799:
        r1 = r0;
        r7 = r18;
        goto L_0x01f0;
    L_0x179e:
        org.telegram.messenger.FileLog.e(r1);
        r1 = r37.getMessagesStorage();
        r1.markMessageAsSendError(r7);
        if (r11 == 0) goto L_0x17af;
    L_0x17aa:
        r1 = r11.messageOwner;
        r2 = 2;
        r1.send_state = r2;
    L_0x17af:
        r1 = r37.getNotificationCenter();
        r2 = org.telegram.messenger.NotificationCenter.messageSendError;
        r3 = 1;
        r3 = new java.lang.Object[r3];
        r4 = r7.id;
        r4 = java.lang.Integer.valueOf(r4);
        r5 = 0;
        r3[r5] = r4;
        r1.postNotificationName(r2, r3);
        r1 = r7.id;
        r15.processSentMessage(r1);
    L_0x17c9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, org.telegram.tgnet.TLRPC$TL_messageMediaPoll, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, int, java.lang.Object):void");
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
                    videoEditedInfo = delayedMessage2.videoEditedInfo;
                    if (videoEditedInfo != null) {
                        if (videoEditedInfo.file != null) {
                            tLObject = delayedMessage2.sendRequest;
                            if (tLObject instanceof TL_messages_sendMedia) {
                                inputMedia = ((TL_messages_sendMedia) tLObject).media;
                            } else if (tLObject instanceof TL_messages_editMessage) {
                                inputMedia = ((TL_messages_editMessage) tLObject).media;
                            } else {
                                inputMedia = ((TL_messages_sendBroadcast) tLObject).media;
                            }
                            VideoEditedInfo videoEditedInfo2 = delayedMessage2.videoEditedInfo;
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
                    VideoEditedInfo videoEditedInfo3;
                    if (tLObject != null) {
                        if (tLObject instanceof TL_messages_sendMedia) {
                            inputMedia = ((TL_messages_sendMedia) tLObject).media;
                        } else if (tLObject instanceof TL_messages_editMessage) {
                            inputMedia = ((TL_messages_editMessage) tLObject).media;
                        } else {
                            inputMedia = ((TL_messages_sendBroadcast) tLObject).media;
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
                            videoEditedInfo3 = delayedMessage2.obj.videoEditedInfo;
                            if (videoEditedInfo3 == null || !videoEditedInfo3.needConvert()) {
                                getFileLoader().uploadFile(str7, false, false, 33554432);
                            } else {
                                getFileLoader().uploadFile(str7, false, false, document.size, 33554432);
                            }
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
                            videoEditedInfo3 = delayedMessage2.obj.videoEditedInfo;
                            if (videoEditedInfo3 == null || !videoEditedInfo3.needConvert()) {
                                getFileLoader().uploadFile(str7, true, false, 33554432);
                            } else {
                                getFileLoader().uploadFile(str7, true, false, document.size, 33554432);
                            }
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
                        } else if (tLObject instanceof TL_messages_editMessage) {
                            inputMedia = ((TL_messages_editMessage) tLObject).media;
                        } else {
                            inputMedia = ((TL_messages_sendBroadcast) tLObject).media;
                        }
                        if (inputMedia.file == null) {
                            str2 = delayedMessage2.obj.messageOwner.attachPath;
                            putToDelayedMessages(str2, delayedMessage2);
                            FileLoader fileLoader = getFileLoader();
                            if (delayedMessage2.sendRequest != null) {
                                z2 = false;
                            }
                            fileLoader.uploadFile(str2, z2, false, 67108864);
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
                        }
                    } else {
                        messageObject = delayedMessage2.obj;
                        str8 = messageObject.messageOwner.attachPath;
                        document = messageObject.getDocument();
                        if (delayedMessage2.sendEncryptedRequest == null || document.dc_id == 0 || new File(str8).exists()) {
                            putToDelayedMessages(str8, delayedMessage2);
                            getFileLoader().uploadFile(str8, true, false, 67108864);
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
            } else if (i2 == 4) {
                boolean z3;
                boolean z4 = i < 0;
                if (delayedMessage2.performMediaUpload) {
                    boolean z5;
                    int size = i < 0 ? delayedMessage2.messageObjects.size() - 1 : i;
                    MessageObject messageObject3 = (MessageObject) delayedMessage2.messageObjects.get(size);
                    if (messageObject3.getDocument() != null) {
                        str7 = "_t";
                        str8 = "_i";
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
                            if (delayedMessage2.photoSize != null) {
                                hashMap = delayedMessage2.extraHashMap;
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(str);
                                stringBuilder3.append(str7);
                                hashMap.put(stringBuilder3.toString(), delayedMessage2.photoSize);
                            }
                            MediaController.getInstance().scheduleVideoConvert(messageObject3);
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
                            VideoEditedInfo videoEditedInfo4;
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
                                    if (delayedMessage2.photoSize != null) {
                                        hashMap = delayedMessage2.extraHashMap;
                                        stringBuilder6 = new StringBuilder();
                                        stringBuilder6.append(str9);
                                        stringBuilder6.append(str7);
                                        hashMap.put(stringBuilder6.toString(), delayedMessage2.photoSize);
                                    }
                                    videoEditedInfo4 = messageObject3.videoEditedInfo;
                                    if (videoEditedInfo4 == null || !videoEditedInfo4.needConvert()) {
                                        getFileLoader().uploadFile(str9, false, false, 33554432);
                                    } else {
                                        getFileLoader().uploadFile(str9, false, false, document3.size, 33554432);
                                    }
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
                                if (delayedMessage2.photoSize != null) {
                                    hashMap = delayedMessage2.extraHashMap;
                                    stringBuilder6 = new StringBuilder();
                                    stringBuilder6.append(str9);
                                    stringBuilder6.append(str7);
                                    hashMap.put(stringBuilder6.toString(), delayedMessage2.photoSize);
                                }
                                videoEditedInfo4 = messageObject3.videoEditedInfo;
                                if (videoEditedInfo4 == null || !videoEditedInfo4.needConvert()) {
                                    getFileLoader().uploadFile(str9, true, false, 33554432);
                                } else {
                                    getFileLoader().uploadFile(str9, true, false, document3.size, 33554432);
                                }
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
                            delayedMessage2.photoSize = null;
                            z5 = false;
                            delayedMessage2.performMediaUpload = z5;
                        }
                    }
                    z5 = false;
                    z3 = true;
                    delayedMessage2.performMediaUpload = z5;
                } else {
                    z3 = true;
                    if (!delayedMessage2.messageObjects.isEmpty()) {
                        ArrayList arrayList = delayedMessage2.messageObjects;
                        putToSendingMessages(((MessageObject) arrayList.get(arrayList.size() - 1)).messageOwner);
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
                    putToSendingMessages((Message) delayedMessage.messages.get(i));
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, valueOf, valueOf2);
                    break;
                }
            }
            TL_messages_uploadMedia tL_messages_uploadMedia = new TL_messages_uploadMedia();
            tL_messages_uploadMedia.media = inputMedia;
            tL_messages_uploadMedia.peer = ((TL_messages_sendMultiMedia) delayedMessage.sendRequest).peer;
            getConnectionsManager().sendRequest(tL_messages_uploadMedia, new -$$Lambda$SendMessagesHelper$9ikUx4RLVeYwBBYbegYSYKLcFew(this, inputMedia, delayedMessage));
        } else if (inputEncryptedFile != null) {
            TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
            for (i = 0; i < tL_messages_sendEncryptedMultiMedia.files.size(); i++) {
                if (tL_messages_sendEncryptedMultiMedia.files.get(i) == inputEncryptedFile) {
                    putToSendingMessages((Message) delayedMessage.messages.get(i));
                    getNotificationCenter().postNotificationName(NotificationCenter.FileUploadProgressChanged, str, valueOf, valueOf2);
                    break;
                }
            }
            sendReadyToSendGroup(delayedMessage, false, true);
        }
    }

    public /* synthetic */ void lambda$uploadMultiMedia$20$SendMessagesHelper(InputMedia inputMedia, DelayedMessage delayedMessage, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$TtlUVA9H7pRWgwj4hEtQKuSUaGg(this, tLObject, inputMedia, delayedMessage));
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0089  */
    public /* synthetic */ void lambda$null$19$SendMessagesHelper(org.telegram.tgnet.TLObject r6, org.telegram.tgnet.TLRPC.InputMedia r7, org.telegram.messenger.SendMessagesHelper.DelayedMessage r8) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$19$SendMessagesHelper(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$InputMedia, org.telegram.messenger.SendMessagesHelper$DelayedMessage):void");
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
            getMessagesStorage().putMessages(delayedMessage.messages, false, true, false, 0);
            getMessagesController().updateInterfaceWithMessages(delayedMessage.peer, delayedMessage.messageObjects);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
                    findMaxDelayedMessageForMessageId.addDelayedRequest(delayedMessage.sendRequest, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage);
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
            performSendMessageRequestMulti((TL_messages_sendMultiMedia) tLObject, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage);
        } else {
            getSecretChatHelper().performSendEncryptedRequest((TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest, delayedMessage);
        }
        delayedMessage.sendDelayedRequests();
    }

    public /* synthetic */ void lambda$null$21$SendMessagesHelper(String str) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, str, Integer.valueOf(this.currentAccount));
    }

    public /* synthetic */ void lambda$stopVideoService$22$SendMessagesHelper(String str) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$p5AvMlaNmWbZ3rWK9wlPEhiSpDU(this, str));
    }

    /* Access modifiers changed, original: protected */
    public void stopVideoService(String str) {
        getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$30d877KfsrPIYnw1mIlTicl4KVY(this, str));
    }

    /* Access modifiers changed, original: protected */
    public void putToSendingMessages(Message message) {
        this.sendingMessages.put(message.id, message);
    }

    /* Access modifiers changed, original: protected */
    public Message removeFromSendingMessages(int i) {
        Message message = (Message) this.sendingMessages.get(i);
        if (message != null) {
            this.sendingMessages.remove(i);
        }
        return message;
    }

    public boolean isSendingMessage(int i) {
        return this.sendingMessages.indexOfKey(i) >= 0;
    }

    /* Access modifiers changed, original: protected */
    public void performSendMessageRequestMulti(TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList<MessageObject> arrayList, ArrayList<String> arrayList2, ArrayList<Object> arrayList3, DelayedMessage delayedMessage) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            putToSendingMessages(((MessageObject) arrayList.get(i)).messageOwner);
        }
        getConnectionsManager().sendRequest((TLObject) tL_messages_sendMultiMedia, new -$$Lambda$SendMessagesHelper$KTc-sr8270evlRbeKgXtKisOYgM(this, arrayList3, tL_messages_sendMultiMedia, arrayList, arrayList2, delayedMessage), null, 68);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$29$SendMessagesHelper(ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$GqwnlURs2UJWXkcpzjv2EBiLrD0(this, tL_error, arrayList, tL_messages_sendMultiMedia, arrayList2, arrayList3, delayedMessage, tLObject));
    }

    public /* synthetic */ void lambda$null$28$SendMessagesHelper(TL_error tL_error, ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, TLObject tLObject) {
        ArrayList arrayList4;
        int i;
        Object obj;
        SendMessagesHelper sendMessagesHelper;
        TL_error tL_error2 = tL_error;
        ArrayList arrayList5 = arrayList;
        final TL_messages_sendMultiMedia tL_messages_sendMultiMedia2 = tL_messages_sendMultiMedia;
        final ArrayList arrayList6 = arrayList2;
        ArrayList arrayList7 = arrayList3;
        final DelayedMessage delayedMessage2 = delayedMessage;
        int i2 = 1;
        if (tL_error2 != null && FileRefController.isFileRefError(tL_error2.text)) {
            if (arrayList5 != null) {
                getFileRefController().requestReference(arrayList4, tL_messages_sendMultiMedia2, arrayList6, arrayList7, new ArrayList(arrayList5), delayedMessage2);
                return;
            } else if (delayedMessage2 != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int size = tL_messages_sendMultiMedia2.multi_media.size();
                        for (int i = 0; i < size; i++) {
                            if (delayedMessage2.parentObjects.get(i) != null) {
                                SendMessagesHelper.this.removeFromSendingMessages(((MessageObject) arrayList6.get(i)).getId());
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
        if (tL_error2 == null) {
            Message message;
            Updates updates;
            SparseArray sparseArray = new SparseArray();
            LongSparseArray longSparseArray = new LongSparseArray();
            Updates updates2 = (Updates) tLObject;
            arrayList4 = updates2.updates;
            int i3 = 0;
            while (i3 < arrayList4.size()) {
                Update update = (Update) arrayList4.get(i3);
                if (update instanceof TL_updateMessageID) {
                    TL_updateMessageID tL_updateMessageID = (TL_updateMessageID) update;
                    longSparseArray.put(tL_updateMessageID.random_id, Integer.valueOf(tL_updateMessageID.id));
                    arrayList4.remove(i3);
                } else if (update instanceof TL_updateNewMessage) {
                    TL_updateNewMessage tL_updateNewMessage = (TL_updateNewMessage) update;
                    message = tL_updateNewMessage.message;
                    sparseArray.put(message.id, message);
                    Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$LtIzRBxRfP0h_IpuxXXH0r91OLg(this, tL_updateNewMessage));
                    arrayList4.remove(i3);
                } else if (update instanceof TL_updateNewChannelMessage) {
                    TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                    message = tL_updateNewChannelMessage.message;
                    sparseArray.put(message.id, message);
                    Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$QOjj_wrwvuHdKpPrDByKhoWSuVs(this, tL_updateNewChannelMessage));
                    arrayList4.remove(i3);
                } else {
                    i3++;
                }
                i3--;
                i3++;
            }
            int i4 = 0;
            while (i4 < arrayList2.size()) {
                MessageObject messageObject = (MessageObject) arrayList6.get(i4);
                String str = (String) arrayList7.get(i4);
                message = messageObject.messageOwner;
                int i5 = message.id;
                arrayList5 = new ArrayList();
                String str2 = message.attachPath;
                Integer num = (Integer) longSparseArray.get(message.random_id);
                if (num != null) {
                    Message message2 = (Message) sparseArray.get(num.intValue());
                    if (message2 != null) {
                        arrayList5.add(message2);
                        ArrayList arrayList8 = arrayList5;
                        int i6 = i5;
                        Message message3 = message;
                        updates = updates2;
                        String str3 = str;
                        LongSparseArray longSparseArray2 = longSparseArray;
                        updateMediaPaths(messageObject, message2, message2.id, str3, false);
                        int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                        message3.id = message2.id;
                        if ((message3.flags & Integer.MIN_VALUE) != 0) {
                            message2.flags |= Integer.MIN_VALUE;
                        }
                        long j = message2.grouped_id;
                        num = (Integer) getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message2.dialog_id));
                        if (num == null) {
                            num = Integer.valueOf(getMessagesStorage().getDialogReadMax(message2.out, message2.dialog_id));
                            getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message2.dialog_id), num);
                        }
                        message2.unread = num.intValue() < message2.id;
                        getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), i2, i2);
                        message3.send_state = 0;
                        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i6), Integer.valueOf(message3.id), message3, Long.valueOf(message3.dialog_id), Long.valueOf(j), Integer.valueOf(mediaExistanceFlags));
                        Message message4 = message3;
                        -$$Lambda$SendMessagesHelper$s-p-jfZFDyfDIdh58D5Hp9jBe_A -__lambda_sendmessageshelper_s-p-jfzfdyfdidh58d5hp9jbe_a = r0;
                        long j2 = j;
                        SparseArray sparseArray2 = sparseArray;
                        DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                        -$$Lambda$SendMessagesHelper$s-p-jfZFDyfDIdh58D5Hp9jBe_A -__lambda_sendmessageshelper_s-p-jfzfdyfdidh58d5hp9jbe_a2 = new -$$Lambda$SendMessagesHelper$s-p-jfZFDyfDIdh58D5Hp9jBe_A(this, message4, i6, arrayList8, j2, mediaExistanceFlags);
                        storageQueue.postRunnable(-__lambda_sendmessageshelper_s-p-jfzfdyfdidh58d5hp9jbe_a);
                        i4++;
                        updates2 = updates;
                        longSparseArray = longSparseArray2;
                        sparseArray = sparseArray2;
                        i2 = 1;
                    }
                }
                updates = updates2;
                i = 1;
                obj = 1;
            }
            updates = updates2;
            i = 1;
            obj = null;
            sendMessagesHelper = this;
            Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$4Kecy4z15gyx9k2PnYvYSIzID4o(sendMessagesHelper, updates));
        } else {
            sendMessagesHelper = this;
            i = 1;
            AlertsCreator.processError(sendMessagesHelper.currentAccount, tL_error2, null, tL_messages_sendMultiMedia2, new Object[0]);
            obj = 1;
        }
        if (obj != null) {
            for (int i7 = 0; i7 < arrayList2.size(); i7++) {
                Message message5 = ((MessageObject) arrayList6.get(i7)).messageOwner;
                getMessagesStorage().markMessageAsSendError(message5);
                message5.send_state = 2;
                NotificationCenter notificationCenter = getNotificationCenter();
                int i8 = NotificationCenter.messageSendError;
                Object[] objArr = new Object[i];
                objArr[0] = Integer.valueOf(message5.id);
                notificationCenter.postNotificationName(i8, objArr);
                sendMessagesHelper.processSentMessage(message5.id);
                sendMessagesHelper.removeFromSendingMessages(message5.id);
            }
        }
    }

    public /* synthetic */ void lambda$null$23$SendMessagesHelper(TL_updateNewMessage tL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$24$SendMessagesHelper(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$26$SendMessagesHelper(Message message, int i, ArrayList arrayList, long j, int i2) {
        Message message2 = message;
        getMessagesStorage().updateMessageStateAndId(message2.random_id, Integer.valueOf(i), message2.id, 0, false, message2.to_id.channel_id);
        getMessagesStorage().putMessages(arrayList, true, false, false, 0);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$jV9Gq3Q6H66Q6pHxnGZUWi6v2TA(this, message2, i, j, i2));
    }

    public /* synthetic */ void lambda$null$25$SendMessagesHelper(Message message, int i, long j, int i2) {
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), Long.valueOf(j), Integer.valueOf(i2));
        processSentMessage(i);
        removeFromSendingMessages(i);
    }

    public /* synthetic */ void lambda$null$27$SendMessagesHelper(Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    private void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, Object obj) {
        performSendMessageRequest(tLObject, messageObject, str, null, false, delayedMessage, obj);
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
    public void performSendMessageRequest(TLObject tLObject, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, Object obj) {
        TLObject tLObject2 = tLObject;
        DelayedMessage delayedMessage3 = delayedMessage;
        if (!(tLObject2 instanceof TL_messages_editMessage) && z) {
            DelayedMessage findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(messageObject.getId(), messageObject.getDialogId());
            if (findMaxDelayedMessageForMessageId != null) {
                findMaxDelayedMessageForMessageId.addDelayedRequest(tLObject, messageObject, str, obj, delayedMessage2);
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
        putToSendingMessages(message);
        message.reqId = getConnectionsManager().sendRequest(tLObject2, new -$$Lambda$SendMessagesHelper$Qe-YOTDVha4ZEN36efTByrQcLKk(this, tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, message), new -$$Lambda$SendMessagesHelper$_wu1JxbPB7vnkTrFhWBgUdpKleg(this, message), (tLObject2 instanceof TL_messages_sendMessage ? 128 : 0) | 68);
        if (delayedMessage3 != null) {
            delayedMessage.sendDelayedRequests();
        }
    }

    public /* synthetic */ void lambda$performSendMessageRequest$40$SendMessagesHelper(final TLObject tLObject, Object obj, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, Message message, TLObject tLObject2, TL_error tL_error) {
        final Message message2;
        TLObject tLObject3 = tLObject;
        Object obj2 = obj;
        final DelayedMessage delayedMessage3 = delayedMessage2;
        TL_error tL_error2 = tL_error;
        if (tL_error2 != null && (((tLObject3 instanceof TL_messages_sendMedia) || (tLObject3 instanceof TL_messages_editMessage)) && FileRefController.isFileRefError(tL_error2.text))) {
            if (obj2 != null) {
                getFileRefController().requestReference(obj, tLObject3, messageObject, str, delayedMessage, Boolean.valueOf(z), delayedMessage3);
                return;
            } else if (delayedMessage3 != null) {
                message2 = message;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        SendMessagesHelper.this.removeFromSendingMessages(message2.id);
                        TLObject tLObject = tLObject;
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
        message2 = message;
        if (tLObject3 instanceof TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$C5CrRoHs0pjHmck8QByYuCo3o_s(this, tL_error, message, tLObject2, messageObject, str, tLObject));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$ep4b2HrbYOUHrh_RuM2XFeYU5D8(this, tL_error, message, tLObject, tLObject2, messageObject, str));
        }
    }

    public /* synthetic */ void lambda$null$32$SendMessagesHelper(TL_error tL_error, Message message, TLObject tLObject, MessageObject messageObject, String str, TLObject tLObject2) {
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
                } else {
                    i++;
                }
            }
            Message message3 = message2;
            if (message3 != null) {
                ImageLoader.saveMessageThumbs(message3);
                updateMediaPaths(messageObject, message3, message3.id, str, false);
            }
            Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$5FORFfWffqEc1IY97T80wKl0r1Y(this, updates, message));
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
        removeFromSendingMessages(message.id);
        revertEditingMessageObject(messageObject);
    }

    public /* synthetic */ void lambda$null$31$SendMessagesHelper(Updates updates, Message message) {
        getMessagesController().processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$8gQkjP_exgY3T8Zm64H9fCFaXkg(this, message));
    }

    public /* synthetic */ void lambda$null$30$SendMessagesHelper(Message message) {
        processSentMessage(message.id);
        removeFromSendingMessages(message.id);
    }

    public /* synthetic */ void lambda$null$39$SendMessagesHelper(TL_error tL_error, Message message, TLObject tLObject, TLObject tLObject2, MessageObject messageObject, String str) {
        Object obj;
        TL_error tL_error2 = tL_error;
        Message message2 = message;
        TLObject tLObject3 = tLObject;
        TLObject tLObject4 = tLObject2;
        Message message3 = null;
        if (tL_error2 == null) {
            int i;
            int i2;
            int i3 = message2.id;
            boolean z = tLObject3 instanceof TL_messages_sendBroadcast;
            ArrayList arrayList = new ArrayList();
            String str2 = message2.attachPath;
            int mediaExistanceFlags;
            if (tLObject4 instanceof TL_updateShortSentMessage) {
                TL_updateShortSentMessage tL_updateShortSentMessage = (TL_updateShortSentMessage) tLObject4;
                updateMediaPaths(messageObject, null, tL_updateShortSentMessage.id, null, false);
                mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                i = tL_updateShortSentMessage.id;
                message2.id = i;
                message2.local_id = i;
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
                Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$oGx4Uga4Fsoa8DwCGDnCb2Zs6E8(this, tL_updateShortSentMessage));
                arrayList.add(message2);
                i2 = mediaExistanceFlags;
                obj = null;
            } else if (tLObject4 instanceof Updates) {
                Object obj2;
                Updates updates = (Updates) tLObject4;
                ArrayList arrayList2 = updates.updates;
                i = 0;
                while (i < arrayList2.size()) {
                    Update update = (Update) arrayList2.get(i);
                    if (update instanceof TL_updateNewMessage) {
                        TL_updateNewMessage tL_updateNewMessage = (TL_updateNewMessage) update;
                        message3 = tL_updateNewMessage.message;
                        arrayList.add(message3);
                        Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$L5hF2IhINUG6nIA5dSvAtaxsz8k(this, tL_updateNewMessage));
                        arrayList2.remove(i);
                        break;
                    } else if (update instanceof TL_updateNewChannelMessage) {
                        TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                        message3 = tL_updateNewChannelMessage.message;
                        arrayList.add(message3);
                        if ((message2.flags & Integer.MIN_VALUE) != 0) {
                            Message message4 = tL_updateNewChannelMessage.message;
                            message4.flags = Integer.MIN_VALUE | message4.flags;
                        }
                        Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$XuUcVoATmHzvdWgq3344GUvreTA(this, tL_updateNewChannelMessage));
                        arrayList2.remove(i);
                    } else {
                        i++;
                    }
                }
                Message message5 = message3;
                if (message5 != null) {
                    ImageLoader.saveMessageThumbs(message5);
                    Integer num = (Integer) getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(message5.dialog_id));
                    if (num == null) {
                        num = Integer.valueOf(getMessagesStorage().getDialogReadMax(message5.out, message5.dialog_id));
                        getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(message5.dialog_id), num);
                    }
                    message5.unread = num.intValue() < message5.id;
                    updateMediaPaths(messageObject, message5, message5.id, str, false);
                    mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                    message2.id = message5.id;
                    i = mediaExistanceFlags;
                    obj2 = null;
                } else {
                    obj2 = 1;
                    i = 0;
                }
                Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$WQ_rgU9i_CLASSNAMEKG3_Zo7CgxwtnA(this, updates));
                obj = obj2;
                i2 = i;
            } else {
                obj = null;
                i2 = 0;
            }
            if (MessageObject.isLiveLocationMessage(message)) {
                getLocationController().addSharingLocation(message2.dialog_id, message2.id, message2.media.period, message);
            }
            if (obj == null) {
                getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                message2.send_state = 0;
                NotificationCenter notificationCenter = getNotificationCenter();
                i = NotificationCenter.messageReceivedByServer;
                Object[] objArr = new Object[6];
                objArr[0] = Integer.valueOf(i3);
                objArr[1] = Integer.valueOf(z ? i3 : message2.id);
                objArr[2] = message2;
                objArr[3] = Long.valueOf(message2.dialog_id);
                objArr[4] = Long.valueOf(0);
                objArr[5] = Integer.valueOf(i2);
                notificationCenter.postNotificationName(i, objArr);
                int i4 = i3;
                boolean z2 = z;
                ArrayList arrayList3 = arrayList;
                -$$Lambda$SendMessagesHelper$oDX4i73L5lyJayv_jik2z2hGP7Q -__lambda_sendmessageshelper_odx4i73l5lyjayv_jik2z2hgp7q = r0;
                i3 = i2;
                DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                -$$Lambda$SendMessagesHelper$oDX4i73L5lyJayv_jik2z2hGP7Q -__lambda_sendmessageshelper_odx4i73l5lyjayv_jik2z2hgp7q2 = new -$$Lambda$SendMessagesHelper$oDX4i73L5lyJayv_jik2z2hGP7Q(this, message, i4, z2, arrayList3, i3, str2);
                storageQueue.postRunnable(-__lambda_sendmessageshelper_odx4i73l5lyjayv_jik2z2hgp7q);
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error2, null, tLObject3, new Object[0]);
            obj = 1;
        }
        if (obj != null) {
            getMessagesStorage().markMessageAsSendError(message2);
            message2.send_state = 2;
            getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message2.id));
            processSentMessage(message2.id);
            if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
                stopVideoService(message2.attachPath);
            }
            removeFromSendingMessages(message2.id);
        }
    }

    public /* synthetic */ void lambda$null$33$SendMessagesHelper(TL_updateShortSentMessage tL_updateShortSentMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateShortSentMessage.pts, tL_updateShortSentMessage.date, tL_updateShortSentMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$34$SendMessagesHelper(TL_updateNewMessage tL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$35$SendMessagesHelper(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$36$SendMessagesHelper(Updates updates) {
        getMessagesController().processUpdates(updates, false);
    }

    public /* synthetic */ void lambda$null$38$SendMessagesHelper(Message message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        Message message2 = message;
        getMessagesStorage().updateMessageStateAndId(message2.random_id, Integer.valueOf(i), z ? i : message2.id, 0, false, message2.to_id.channel_id);
        getMessagesStorage().putMessages(arrayList, true, false, z, 0);
        if (z) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(message2);
            getMessagesStorage().putMessages(arrayList2, true, false, false, 0);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$Lyh7x_fVJCtY78eBEW_nfqaECxE(this, z, arrayList, message, i, i2));
        if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
            stopVideoService(str);
        }
    }

    public /* synthetic */ void lambda$null$37$SendMessagesHelper(boolean z, ArrayList arrayList, Message message, int i, int i2) {
        int i3;
        if (z) {
            for (i3 = 0; i3 < arrayList.size(); i3++) {
                Message message2 = (Message) arrayList.get(i3);
                ArrayList arrayList2 = new ArrayList();
                MessageObject messageObject = new MessageObject(this.currentAccount, message2, false);
                arrayList2.add(messageObject);
                getMessagesController().updateInterfaceWithMessages(messageObject.getDialogId(), arrayList2, true);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        getMediaDataController().increasePeerRaiting(message.dialog_id);
        NotificationCenter notificationCenter = getNotificationCenter();
        i3 = NotificationCenter.messageReceivedByServer;
        Object[] objArr = new Object[6];
        objArr[0] = Integer.valueOf(i);
        objArr[1] = Integer.valueOf(z ? i : message.id);
        objArr[2] = message;
        objArr[3] = Long.valueOf(message.dialog_id);
        objArr[4] = Long.valueOf(0);
        objArr[5] = Integer.valueOf(i2);
        notificationCenter.postNotificationName(i3, objArr);
        processSentMessage(i);
        removeFromSendingMessages(i);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$42$SendMessagesHelper(Message message) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$s7kB2gRrnjLRX4z7Rxo0J3e9TgU(this, message, message.id));
    }

    public /* synthetic */ void lambda$null$41$SendMessagesHelper(Message message, int i) {
        message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(i));
    }

    /* JADX WARNING: Removed duplicated region for block: B:180:0x0449  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x046e  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x04bc  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0449  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x046e  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x04bc  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x04f3  */
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
        if (r5 == 0) goto L_0x02b2;
    L_0x0118:
        r5 = r2.photo;
        if (r5 == 0) goto L_0x02b2;
    L_0x011c:
        r5 = r9.media;
        r3 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 == 0) goto L_0x02b2;
    L_0x0122:
        r3 = r5.photo;
        if (r3 == 0) goto L_0x02b2;
    L_0x0126:
        r0 = r2.ttl_seconds;
        if (r0 != 0) goto L_0x0150;
    L_0x012a:
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
    L_0x0150:
        r0 = r9.media;
        r0 = r0.photo;
        r0 = r0.sizes;
        r0 = r0.size();
        if (r0 != r15) goto L_0x017c;
    L_0x015c:
        r0 = r9.media;
        r0 = r0.photo;
        r0 = r0.sizes;
        r0 = r0.get(r14);
        r0 = (org.telegram.tgnet.TLRPC.PhotoSize) r0;
        r0 = r0.location;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r0 == 0) goto L_0x017c;
    L_0x016e:
        r0 = r9.media;
        r0 = r0.photo;
        r1 = r7.media;
        r1 = r1.photo;
        r1 = r1.sizes;
        r0.sizes = r1;
        goto L_0x0298;
    L_0x017c:
        r0 = 0;
    L_0x017d:
        r2 = r7.media;
        r2 = r2.photo;
        r2 = r2.sizes;
        r2 = r2.size();
        if (r0 >= r2) goto L_0x0298;
    L_0x0189:
        r2 = r7.media;
        r2 = r2.photo;
        r2 = r2.sizes;
        r2 = r2.get(r0);
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;
        if (r2 == 0) goto L_0x0291;
    L_0x0197:
        r3 = r2.location;
        if (r3 == 0) goto L_0x0291;
    L_0x019b:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r3 != 0) goto L_0x0291;
    L_0x019f:
        r3 = r2.type;
        if (r3 != 0) goto L_0x01a5;
    L_0x01a3:
        goto L_0x0291;
    L_0x01a5:
        r3 = 0;
    L_0x01a6:
        r5 = r9.media;
        r5 = r5.photo;
        r5 = r5.sizes;
        r5 = r5.size();
        if (r3 >= r5) goto L_0x0291;
    L_0x01b2:
        r5 = r9.media;
        r5 = r5.photo;
        r5 = r5.sizes;
        r5 = r5.get(r3);
        r5 = (org.telegram.tgnet.TLRPC.PhotoSize) r5;
        if (r5 == 0) goto L_0x028a;
    L_0x01c0:
        r6 = r5.location;
        if (r6 == 0) goto L_0x028a;
    L_0x01c4:
        r8 = r5.type;
        if (r8 != 0) goto L_0x01ca;
    L_0x01c8:
        goto L_0x028a;
    L_0x01ca:
        r14 = r6.volume_id;
        r6 = (r14 > r10 ? 1 : (r14 == r10 ? 0 : -1));
        if (r6 != 0) goto L_0x01d8;
    L_0x01d0:
        r6 = r2.type;
        r6 = r6.equals(r8);
        if (r6 != 0) goto L_0x01e4;
    L_0x01d8:
        r6 = r2.w;
        r8 = r5.w;
        if (r6 != r8) goto L_0x028a;
    L_0x01de:
        r6 = r2.h;
        r8 = r5.h;
        if (r6 != r8) goto L_0x028a;
    L_0x01e4:
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
        if (r8 == 0) goto L_0x021f;
    L_0x021e:
        goto L_0x0291;
    L_0x021f:
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
        if (r15 != 0) goto L_0x0257;
    L_0x023d:
        r14 = r14.photo;
        r14 = r14.sizes;
        r14 = r14.size();
        r15 = 1;
        if (r14 == r15) goto L_0x0252;
    L_0x0248:
        r14 = r2.w;
        r15 = 90;
        if (r14 > r15) goto L_0x0252;
    L_0x024e:
        r14 = r2.h;
        if (r14 <= r15) goto L_0x0257;
    L_0x0252:
        r14 = org.telegram.messenger.FileLoader.getPathToAttach(r2);
        goto L_0x026f;
    L_0x0257:
        r14 = new java.io.File;
        r15 = org.telegram.messenger.FileLoader.getDirectory(r13);
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r6);
        r13.append(r12);
        r13 = r13.toString();
        r14.<init>(r15, r13);
    L_0x026f:
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
        goto L_0x0291;
    L_0x028a:
        r3 = r3 + 1;
        r13 = 4;
        r14 = 0;
        r15 = 1;
        goto L_0x01a6;
    L_0x0291:
        r0 = r0 + 1;
        r13 = 4;
        r14 = 0;
        r15 = 1;
        goto L_0x017d;
    L_0x0298:
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
        goto L_0x05bf;
    L_0x02b2:
        r2 = r7.media;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r3 == 0) goto L_0x0571;
    L_0x02b8:
        r3 = r2.document;
        if (r3 == 0) goto L_0x0571;
    L_0x02bc:
        r3 = r9.media;
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r5 == 0) goto L_0x0571;
    L_0x02c2:
        r3 = r3.document;
        if (r3 == 0) goto L_0x0571;
    L_0x02c6:
        r2 = r2.ttl_seconds;
        if (r2 != 0) goto L_0x0349;
    L_0x02ca:
        r2 = org.telegram.messenger.MessageObject.isVideoMessage(r18);
        if (r2 != 0) goto L_0x02d6;
    L_0x02d0:
        r3 = org.telegram.messenger.MessageObject.isGifMessage(r18);
        if (r3 == 0) goto L_0x0316;
    L_0x02d6:
        r3 = r7.media;
        r3 = r3.document;
        r3 = org.telegram.messenger.MessageObject.isGifDocument(r3);
        r5 = r9.media;
        r5 = r5.document;
        r5 = org.telegram.messenger.MessageObject.isGifDocument(r5);
        if (r3 != r5) goto L_0x0316;
    L_0x02e8:
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
        if (r2 == 0) goto L_0x0349;
    L_0x0311:
        r2 = r9.attachPath;
        r7.attachPath = r2;
        goto L_0x0349;
    L_0x0316:
        r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r18);
        if (r2 != 0) goto L_0x0349;
    L_0x031c:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r18);
        if (r2 != 0) goto L_0x0349;
    L_0x0322:
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
    L_0x0349:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.thumbs;
        r3 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3);
        r5 = r7.media;
        r5 = r5.document;
        r5 = r5.thumbs;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r3);
        if (r2 == 0) goto L_0x03ff;
    L_0x0361:
        r5 = r2.location;
        if (r5 == 0) goto L_0x03ff;
    L_0x0365:
        r5 = r5.volume_id;
        r13 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1));
        if (r13 != 0) goto L_0x03ff;
    L_0x036b:
        if (r3 == 0) goto L_0x03ff;
    L_0x036d:
        r5 = r3.location;
        if (r5 == 0) goto L_0x03ff;
    L_0x0371:
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r5 != 0) goto L_0x03ff;
    L_0x0375:
        r5 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r5 != 0) goto L_0x03ff;
    L_0x0379:
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
        if (r6 != 0) goto L_0x0428;
    L_0x03b3:
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
        goto L_0x0428;
    L_0x03ff:
        if (r2 == 0) goto L_0x040e;
    L_0x0401:
        r1 = org.telegram.messenger.MessageObject.isStickerMessage(r18);
        if (r1 == 0) goto L_0x040e;
    L_0x0407:
        r1 = r2.location;
        if (r1 == 0) goto L_0x040e;
    L_0x040b:
        r3.location = r1;
        goto L_0x0428;
    L_0x040e:
        if (r2 == 0) goto L_0x041c;
    L_0x0410:
        if (r2 == 0) goto L_0x0418;
    L_0x0412:
        r1 = r2.location;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r1 != 0) goto L_0x041c;
    L_0x0418:
        r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r1 == 0) goto L_0x0428;
    L_0x041c:
        r1 = r9.media;
        r1 = r1.document;
        r2 = r7.media;
        r2 = r2.document;
        r2 = r2.thumbs;
        r1.thumbs = r2;
    L_0x0428:
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
    L_0x043d:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.size();
        if (r1 >= r2) goto L_0x045f;
    L_0x0449:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.get(r1);
        r2 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r2;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
        if (r3 == 0) goto L_0x045c;
    L_0x0459:
        r3 = r2.waveform;
        goto L_0x0460;
    L_0x045c:
        r1 = r1 + 1;
        goto L_0x043d;
    L_0x045f:
        r3 = 0;
    L_0x0460:
        r1 = r9.media;
        r1 = r1.document;
        r2 = r7.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r1.attributes = r2;
        if (r3 == 0) goto L_0x0496;
    L_0x046e:
        r1 = 0;
    L_0x046f:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.size();
        if (r1 >= r2) goto L_0x0496;
    L_0x047b:
        r2 = r9.media;
        r2 = r2.document;
        r2 = r2.attributes;
        r2 = r2.get(r1);
        r2 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r2;
        r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
        if (r4 == 0) goto L_0x0493;
    L_0x048b:
        r2.waveform = r3;
        r4 = r2.flags;
        r5 = 4;
        r4 = r4 | r5;
        r2.flags = r4;
    L_0x0493:
        r1 = r1 + 1;
        goto L_0x046f;
    L_0x0496:
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
        if (r1 != 0) goto L_0x04ef;
    L_0x04ac:
        r1 = org.telegram.messenger.MessageObject.isOut(r18);
        if (r1 == 0) goto L_0x04ef;
    L_0x04b2:
        r1 = r7.media;
        r1 = r1.document;
        r1 = org.telegram.messenger.MessageObject.isNewGifDocument(r1);
        if (r1 == 0) goto L_0x04ca;
    L_0x04bc:
        r1 = r16.getMediaDataController();
        r2 = r7.media;
        r2 = r2.document;
        r3 = r7.date;
        r1.addRecentGif(r2, r3);
        goto L_0x04ef;
    L_0x04ca:
        r1 = r7.media;
        r1 = r1.document;
        r1 = org.telegram.messenger.MessageObject.isStickerDocument(r1);
        if (r1 != 0) goto L_0x04de;
    L_0x04d4:
        r1 = r7.media;
        r1 = r1.document;
        r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1);
        if (r1 == 0) goto L_0x04ef;
    L_0x04de:
        r1 = r16.getMediaDataController();
        r2 = 0;
        r3 = r7.media;
        r4 = r3.document;
        r5 = r7.date;
        r6 = 0;
        r3 = r18;
        r1.addRecentSticker(r2, r3, r4, r5, r6);
    L_0x04ef:
        r1 = r9.attachPath;
        if (r1 == 0) goto L_0x0568;
    L_0x04f3:
        r2 = 4;
        r2 = org.telegram.messenger.FileLoader.getDirectory(r2);
        r2 = r2.getAbsolutePath();
        r1 = r1.startsWith(r2);
        if (r1 == 0) goto L_0x0568;
    L_0x0502:
        r1 = new java.io.File;
        r2 = r9.attachPath;
        r1.<init>(r2);
        r2 = r7.media;
        r3 = r2.document;
        r2 = r2.ttl_seconds;
        if (r2 == 0) goto L_0x0513;
    L_0x0511:
        r2 = 1;
        goto L_0x0514;
    L_0x0513:
        r2 = 0;
    L_0x0514:
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r2);
        r3 = r1.renameTo(r2);
        if (r3 != 0) goto L_0x0538;
    L_0x051e:
        r1 = r1.exists();
        if (r1 == 0) goto L_0x0529;
    L_0x0524:
        r1 = r9.attachPath;
        r7.attachPath = r1;
        goto L_0x052c;
    L_0x0529:
        r1 = 0;
        r0.attachPathExists = r1;
    L_0x052c:
        r1 = r2.exists();
        r0.mediaExists = r1;
        r0 = r9.message;
        r7.message = r0;
        goto L_0x05bf;
    L_0x0538:
        r1 = org.telegram.messenger.MessageObject.isVideoMessage(r18);
        if (r1 == 0) goto L_0x0543;
    L_0x053e:
        r1 = 1;
        r0.attachPathExists = r1;
        goto L_0x05bf;
    L_0x0543:
        r1 = r0.attachPathExists;
        r0.mediaExists = r1;
        r1 = 0;
        r0.attachPathExists = r1;
        r0 = "";
        r9.attachPath = r0;
        if (r8 == 0) goto L_0x05bf;
    L_0x0550:
        r0 = "http";
        r0 = r8.startsWith(r0);
        if (r0 == 0) goto L_0x05bf;
    L_0x0558:
        r0 = r16.getMessagesStorage();
        r1 = r2.toString();
        r2 = r9.media;
        r2 = r2.document;
        r0.addRecentLocalFile(r8, r1, r2);
        goto L_0x05bf;
    L_0x0568:
        r0 = r9.attachPath;
        r7.attachPath = r0;
        r0 = r9.message;
        r7.message = r0;
        goto L_0x05bf;
    L_0x0571:
        r0 = r7.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r1 == 0) goto L_0x0580;
    L_0x0577:
        r1 = r9.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r1 == 0) goto L_0x0580;
    L_0x057d:
        r9.media = r0;
        goto L_0x05bf;
    L_0x0580:
        r0 = r7.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r1 == 0) goto L_0x0589;
    L_0x0586:
        r9.media = r0;
        goto L_0x05bf;
    L_0x0589:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r1 == 0) goto L_0x059c;
    L_0x058d:
        r0 = r0.geo;
        r1 = r9.media;
        r1 = r1.geo;
        r2 = r1.lat;
        r0.lat = r2;
        r1 = r1._long;
        r0._long = r1;
        goto L_0x05bf;
    L_0x059c:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 == 0) goto L_0x05b9;
    L_0x05a0:
        r9.media = r0;
        r0 = r9.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x05bf;
    L_0x05a8:
        r0 = r7.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x05bf;
    L_0x05b0:
        r0 = r7.entities;
        r9.entities = r0;
        r0 = r7.message;
        r9.message = r0;
        goto L_0x05bf;
    L_0x05b9:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r1 == 0) goto L_0x05bf;
    L_0x05bd:
        r9.media = r0;
    L_0x05bf:
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
    public void processUnsentMessages(ArrayList<Message> arrayList, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, ArrayList<EncryptedChat> arrayList4) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$uLBi7R9ZOYopc-Gzh-Qk8zK9YAo(this, arrayList2, arrayList3, arrayList4, arrayList));
    }

    public /* synthetic */ void lambda$processUnsentMessages$43$SendMessagesHelper(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (int i = 0; i < arrayList4.size(); i++) {
            retrySendMessage(new MessageObject(this.currentAccount, (Message) arrayList4.get(i), false), true);
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

    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x015e A:{SYNTHETIC, Splitter:B:113:0x015e} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x0403  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0408  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x041e  */
    private static boolean prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r32, java.lang.String r33, java.lang.String r34, android.net.Uri r35, java.lang.String r36, long r37, org.telegram.messenger.MessageObject r39, java.lang.CharSequence r40, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r41, org.telegram.messenger.MessageObject r42, boolean r43) {
        /*
        r0 = r33;
        r1 = r34;
        r2 = r35;
        r3 = r36;
        r4 = 0;
        if (r0 == 0) goto L_0x0011;
    L_0x000b:
        r5 = r33.length();
        if (r5 != 0) goto L_0x0014;
    L_0x0011:
        if (r2 != 0) goto L_0x0014;
    L_0x0013:
        return r4;
    L_0x0014:
        if (r2 == 0) goto L_0x001d;
    L_0x0016:
        r5 = org.telegram.messenger.AndroidUtilities.isInternalUri(r35);
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
        r12 = 1;
        if (r2 == 0) goto L_0x0052;
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
        if (r3 != 0) goto L_0x0050;
    L_0x004e:
        r7 = r2;
        goto L_0x0053;
    L_0x0050:
        r7 = r2;
        goto L_0x0054;
    L_0x0052:
        r7 = r0;
    L_0x0053:
        r0 = 0;
    L_0x0054:
        r2 = new java.io.File;
        r2.<init>(r7);
        r3 = r2.exists();
        if (r3 == 0) goto L_0x043d;
    L_0x005f:
        r8 = r2.length();
        r10 = 0;
        r3 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r3 != 0) goto L_0x006b;
    L_0x0069:
        goto L_0x043d;
    L_0x006b:
        r8 = r37;
        r3 = (int) r8;
        if (r3 != 0) goto L_0x0072;
    L_0x0070:
        r3 = 1;
        goto L_0x0073;
    L_0x0072:
        r3 = 0;
    L_0x0073:
        r19 = r3 ^ 1;
        r15 = r2.getName();
        r14 = -1;
        r13 = "";
        if (r0 == 0) goto L_0x0081;
    L_0x007e:
        r20 = r0;
        goto L_0x0091;
    L_0x0081:
        r0 = 46;
        r0 = r7.lastIndexOf(r0);
        if (r0 == r14) goto L_0x008f;
    L_0x0089:
        r0 = r0 + r12;
        r0 = r7.substring(r0);
        goto L_0x007e;
    L_0x008f:
        r20 = r13;
    L_0x0091:
        r6 = r20.toLowerCase();
        r4 = "mp3";
        r0 = r6.equals(r4);
        r10 = "flac";
        r11 = "opus";
        r12 = "m4a";
        r8 = "ogg";
        if (r0 != 0) goto L_0x0168;
    L_0x00a5:
        r0 = r6.equals(r12);
        if (r0 == 0) goto L_0x00ad;
    L_0x00ab:
        goto L_0x0168;
    L_0x00ad:
        r0 = r6.equals(r11);
        if (r0 != 0) goto L_0x00c7;
    L_0x00b3:
        r0 = r6.equals(r8);
        if (r0 != 0) goto L_0x00c7;
    L_0x00b9:
        r0 = r6.equals(r10);
        if (r0 == 0) goto L_0x00c0;
    L_0x00bf:
        goto L_0x00c7;
    L_0x00c0:
        r21 = r10;
        r0 = 0;
        r9 = 0;
        r10 = 0;
        goto L_0x0187;
    L_0x00c7:
        r14 = new android.media.MediaMetadataRetriever;	 Catch:{ Exception -> 0x013e, all -> 0x013a }
        r14.<init>();	 Catch:{ Exception -> 0x013e, all -> 0x013a }
        r0 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x0135 }
        r14.setDataSource(r0);	 Catch:{ Exception -> 0x0135 }
        r0 = 9;
        r0 = r14.extractMetadata(r0);	 Catch:{ Exception -> 0x0135 }
        if (r0 == 0) goto L_0x0102;
    L_0x00db:
        r21 = r10;
        r9 = java.lang.Long.parseLong(r0);	 Catch:{ Exception -> 0x0100 }
        r0 = (float) r9;	 Catch:{ Exception -> 0x0100 }
        r9 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r0 = r0 / r9;
        r9 = (double) r0;	 Catch:{ Exception -> 0x0100 }
        r9 = java.lang.Math.ceil(r9);	 Catch:{ Exception -> 0x0100 }
        r9 = (int) r9;
        r0 = 7;
        r10 = r14.extractMetadata(r0);	 Catch:{ Exception -> 0x00fb }
        r16 = r9;
        r9 = 2;
        r0 = r14.extractMetadata(r9);	 Catch:{ Exception -> 0x00f9 }
        r9 = r0;
        goto L_0x0108;
    L_0x00f9:
        r0 = move-exception;
        goto L_0x0145;
    L_0x00fb:
        r0 = move-exception;
        r16 = r9;
        r10 = 0;
        goto L_0x0145;
    L_0x0100:
        r0 = move-exception;
        goto L_0x0138;
    L_0x0102:
        r21 = r10;
        r9 = 0;
        r10 = 0;
        r16 = 0;
    L_0x0108:
        if (r42 != 0) goto L_0x0123;
    L_0x010a:
        r0 = r6.equals(r8);	 Catch:{ Exception -> 0x011f }
        if (r0 == 0) goto L_0x0123;
    L_0x0110:
        r0 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x011f }
        r0 = org.telegram.messenger.MediaController.isOpusFile(r0);	 Catch:{ Exception -> 0x011f }
        r17 = r9;
        r9 = 1;
        if (r0 != r9) goto L_0x0125;
    L_0x011d:
        r9 = 1;
        goto L_0x0126;
    L_0x011f:
        r0 = move-exception;
        r17 = r9;
        goto L_0x0147;
    L_0x0123:
        r17 = r9;
    L_0x0125:
        r9 = 0;
    L_0x0126:
        r14.release();	 Catch:{ Exception -> 0x012a }
        goto L_0x012f;
    L_0x012a:
        r0 = move-exception;
        r14 = r0;
        org.telegram.messenger.FileLog.e(r14);
    L_0x012f:
        r14 = r9;
        r0 = r16;
        r9 = r17;
        goto L_0x0188;
    L_0x0135:
        r0 = move-exception;
        r21 = r10;
    L_0x0138:
        r10 = 0;
        goto L_0x0143;
    L_0x013a:
        r0 = move-exception;
        r1 = r0;
        r14 = 0;
        goto L_0x015c;
    L_0x013e:
        r0 = move-exception;
        r21 = r10;
        r10 = 0;
        r14 = 0;
    L_0x0143:
        r16 = 0;
    L_0x0145:
        r17 = 0;
    L_0x0147:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x015a }
        if (r14 == 0) goto L_0x0155;
    L_0x014c:
        r14.release();	 Catch:{ Exception -> 0x0150 }
        goto L_0x0155;
    L_0x0150:
        r0 = move-exception;
        r9 = r0;
        org.telegram.messenger.FileLog.e(r9);
    L_0x0155:
        r0 = r16;
        r9 = r17;
        goto L_0x0187;
    L_0x015a:
        r0 = move-exception;
        r1 = r0;
    L_0x015c:
        if (r14 == 0) goto L_0x0167;
    L_0x015e:
        r14.release();	 Catch:{ Exception -> 0x0162 }
        goto L_0x0167;
    L_0x0162:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
    L_0x0167:
        throw r1;
    L_0x0168:
        r21 = r10;
        r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r2);
        if (r0 == 0) goto L_0x0183;
    L_0x0170:
        r9 = r0.getDuration();
        r16 = 0;
        r14 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1));
        if (r14 == 0) goto L_0x0183;
    L_0x017a:
        r9 = r0.getArtist();
        r0 = r0.getTitle();
        goto L_0x0185;
    L_0x0183:
        r0 = 0;
        r9 = 0;
    L_0x0185:
        r10 = r0;
        r0 = 0;
    L_0x0187:
        r14 = 0;
    L_0x0188:
        if (r0 == 0) goto L_0x01b5;
    L_0x018a:
        r16 = r15;
        r15 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
        r15.<init>();
        r15.duration = r0;
        r15.title = r10;
        r15.performer = r9;
        r0 = r15.title;
        if (r0 != 0) goto L_0x019d;
    L_0x019b:
        r15.title = r13;
    L_0x019d:
        r0 = r15.flags;
        r9 = 1;
        r0 = r0 | r9;
        r15.flags = r0;
        r0 = r15.performer;
        if (r0 != 0) goto L_0x01a9;
    L_0x01a7:
        r15.performer = r13;
    L_0x01a9:
        r0 = r15.flags;
        r10 = 2;
        r0 = r0 | r10;
        r15.flags = r0;
        if (r14 == 0) goto L_0x01b3;
    L_0x01b1:
        r15.voice = r9;
    L_0x01b3:
        r0 = r15;
        goto L_0x01b8;
    L_0x01b5:
        r16 = r15;
        r0 = 0;
    L_0x01b8:
        if (r1 == 0) goto L_0x01f6;
    L_0x01ba:
        r9 = "attheme";
        r9 = r1.endsWith(r9);
        if (r9 == 0) goto L_0x01c5;
    L_0x01c2:
        r9 = r1;
        r1 = 1;
        goto L_0x01f8;
    L_0x01c5:
        if (r0 == 0) goto L_0x01e0;
    L_0x01c7:
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r1);
        r1 = "audio";
        r9.append(r1);
        r14 = r2.length();
        r9.append(r14);
        r1 = r9.toString();
        goto L_0x01f6;
    L_0x01e0:
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r1);
        r9.append(r13);
        r14 = r2.length();
        r9.append(r14);
        r1 = r9.toString();
    L_0x01f6:
        r9 = r1;
        r1 = 0;
    L_0x01f8:
        if (r1 != 0) goto L_0x0277;
    L_0x01fa:
        if (r3 != 0) goto L_0x0277;
    L_0x01fc:
        r1 = r32.getMessagesStorage();
        if (r3 != 0) goto L_0x0204;
    L_0x0202:
        r14 = 1;
        goto L_0x0205;
    L_0x0204:
        r14 = 4;
    L_0x0205:
        r1 = r1.getSentFile(r9, r14);
        if (r1 == 0) goto L_0x0217;
    L_0x020b:
        r14 = 0;
        r15 = r1[r14];
        r14 = r15;
        r14 = (org.telegram.tgnet.TLRPC.TL_document) r14;
        r15 = 1;
        r1 = r1[r15];
        r1 = (java.lang.String) r1;
        goto L_0x0219;
    L_0x0217:
        r1 = 0;
        r14 = 0;
    L_0x0219:
        if (r14 != 0) goto L_0x0254;
    L_0x021b:
        r15 = r7.equals(r9);
        if (r15 != 0) goto L_0x0254;
    L_0x0221:
        if (r3 != 0) goto L_0x0254;
    L_0x0223:
        r15 = r32.getMessagesStorage();
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r7);
        r17 = r13;
        r18 = r14;
        r13 = r2.length();
        r10.append(r13);
        r10 = r10.toString();
        if (r3 != 0) goto L_0x0242;
    L_0x0240:
        r13 = 1;
        goto L_0x0243;
    L_0x0242:
        r13 = 4;
    L_0x0243:
        r10 = r15.getSentFile(r10, r13);
        if (r10 == 0) goto L_0x0258;
    L_0x0249:
        r13 = 0;
        r1 = r10[r13];
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;
        r13 = 1;
        r10 = r10[r13];
        r10 = (java.lang.String) r10;
        goto L_0x025b;
    L_0x0254:
        r17 = r13;
        r18 = r14;
    L_0x0258:
        r10 = r1;
        r1 = r18;
    L_0x025b:
        r18 = 0;
        r22 = 0;
        r15 = r17;
        r13 = r3;
        r24 = -1;
        r14 = r1;
        r33 = r1;
        r25 = r10;
        r10 = r15;
        r1 = r16;
        r15 = r7;
        r16 = r18;
        r17 = r22;
        ensureMediaThumbExists(r13, r14, r15, r16, r17);
        r13 = r33;
        goto L_0x027f;
    L_0x0277:
        r10 = r13;
        r1 = r16;
        r24 = -1;
        r13 = 0;
        r25 = 0;
    L_0x027f:
        if (r13 != 0) goto L_0x0403;
    L_0x0281:
        r13 = new org.telegram.tgnet.TLRPC$TL_document;
        r13.<init>();
        r14 = 0;
        r13.id = r14;
        r14 = r32.getConnectionsManager();
        r14 = r14.getCurrentTime();
        r13.date = r14;
        r14 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r14.<init>();
        r14.file_name = r1;
        r1 = 0;
        r15 = new byte[r1];
        r13.file_reference = r15;
        r15 = r13.attributes;
        r15.add(r14);
        r15 = r9;
        r17 = r10;
        r9 = r2.length();
        r10 = (int) r9;
        r13.size = r10;
        r13.dc_id = r1;
        if (r0 == 0) goto L_0x02b8;
    L_0x02b3:
        r1 = r13.attributes;
        r1.add(r0);
    L_0x02b8:
        r0 = r20.length();
        r1 = "application/octet-stream";
        if (r0 == 0) goto L_0x0338;
    L_0x02c0:
        r0 = r6.hashCode();
        switch(r0) {
            case 106458: goto L_0x02f4;
            case 108272: goto L_0x02ec;
            case 109967: goto L_0x02e4;
            case 3145576: goto L_0x02da;
            case 3418175: goto L_0x02d2;
            case 3645340: goto L_0x02c8;
            default: goto L_0x02c7;
        };
    L_0x02c7:
        goto L_0x02fc;
    L_0x02c8:
        r0 = "webp";
        r0 = r6.equals(r0);
        if (r0 == 0) goto L_0x02fc;
    L_0x02d0:
        r4 = 0;
        goto L_0x02fd;
    L_0x02d2:
        r0 = r6.equals(r11);
        if (r0 == 0) goto L_0x02fc;
    L_0x02d8:
        r4 = 1;
        goto L_0x02fd;
    L_0x02da:
        r4 = r21;
        r0 = r6.equals(r4);
        if (r0 == 0) goto L_0x02fc;
    L_0x02e2:
        r4 = 5;
        goto L_0x02fd;
    L_0x02e4:
        r0 = r6.equals(r8);
        if (r0 == 0) goto L_0x02fc;
    L_0x02ea:
        r4 = 4;
        goto L_0x02fd;
    L_0x02ec:
        r0 = r6.equals(r4);
        if (r0 == 0) goto L_0x02fc;
    L_0x02f2:
        r4 = 2;
        goto L_0x02fd;
    L_0x02f4:
        r0 = r6.equals(r12);
        if (r0 == 0) goto L_0x02fc;
    L_0x02fa:
        r4 = 3;
        goto L_0x02fd;
    L_0x02fc:
        r4 = -1;
    L_0x02fd:
        if (r4 == 0) goto L_0x0333;
    L_0x02ff:
        r8 = 1;
        if (r4 == r8) goto L_0x032e;
    L_0x0302:
        r8 = 2;
        if (r4 == r8) goto L_0x0329;
    L_0x0305:
        r0 = 3;
        if (r4 == r0) goto L_0x0324;
    L_0x0308:
        r0 = 4;
        if (r4 == r0) goto L_0x031f;
    L_0x030b:
        r0 = 5;
        if (r4 == r0) goto L_0x031a;
    L_0x030e:
        r0 = r5.getMimeTypeFromExtension(r6);
        if (r0 == 0) goto L_0x0317;
    L_0x0314:
        r13.mime_type = r0;
        goto L_0x033a;
    L_0x0317:
        r13.mime_type = r1;
        goto L_0x033a;
    L_0x031a:
        r0 = "audio/flac";
        r13.mime_type = r0;
        goto L_0x033a;
    L_0x031f:
        r0 = "audio/ogg";
        r13.mime_type = r0;
        goto L_0x033a;
    L_0x0324:
        r0 = "audio/m4a";
        r13.mime_type = r0;
        goto L_0x033a;
    L_0x0329:
        r0 = "audio/mpeg";
        r13.mime_type = r0;
        goto L_0x033a;
    L_0x032e:
        r0 = "audio/opus";
        r13.mime_type = r0;
        goto L_0x033a;
    L_0x0333:
        r0 = "image/webp";
        r13.mime_type = r0;
        goto L_0x033a;
    L_0x0338:
        r13.mime_type = r1;
    L_0x033a:
        r0 = r13.mime_type;
        r1 = "image/gif";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0387;
    L_0x0344:
        if (r42 == 0) goto L_0x0350;
    L_0x0346:
        r0 = r42.getGroupIdForUse();
        r4 = 0;
        r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x0387;
    L_0x0350:
        r0 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x0383 }
        r1 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r2 = 0;
        r4 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r2, r1, r1, r4);	 Catch:{ Exception -> 0x0383 }
        if (r0 == 0) goto L_0x0387;
    L_0x035e:
        r2 = "animation.gif";
        r14.file_name = r2;	 Catch:{ Exception -> 0x0383 }
        r2 = r13.attributes;	 Catch:{ Exception -> 0x0383 }
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;	 Catch:{ Exception -> 0x0383 }
        r4.<init>();	 Catch:{ Exception -> 0x0383 }
        r2.add(r4);	 Catch:{ Exception -> 0x0383 }
        r2 = 55;
        r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r1, r1, r2, r3);	 Catch:{ Exception -> 0x0383 }
        if (r1 == 0) goto L_0x037f;
    L_0x0374:
        r2 = r13.thumbs;	 Catch:{ Exception -> 0x0383 }
        r2.add(r1);	 Catch:{ Exception -> 0x0383 }
        r1 = r13.flags;	 Catch:{ Exception -> 0x0383 }
        r2 = 1;
        r1 = r1 | r2;
        r13.flags = r1;	 Catch:{ Exception -> 0x0383 }
    L_0x037f:
        r0.recycle();	 Catch:{ Exception -> 0x0383 }
        goto L_0x0387;
    L_0x0383:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0387:
        r0 = r13.mime_type;
        r1 = "image/webp";
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x0400;
    L_0x0391:
        if (r19 == 0) goto L_0x0400;
    L_0x0393:
        if (r42 != 0) goto L_0x0400;
    L_0x0395:
        r1 = new android.graphics.BitmapFactory$Options;
        r1.<init>();
        r2 = 1;
        r1.inJustDecodeBounds = r2;	 Catch:{ Exception -> 0x03c4 }
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x03c4 }
        r2 = "r";
        r0.<init>(r7, r2);	 Catch:{ Exception -> 0x03c4 }
        r26 = r0.getChannel();	 Catch:{ Exception -> 0x03c4 }
        r27 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Exception -> 0x03c4 }
        r28 = 0;
        r2 = r7.length();	 Catch:{ Exception -> 0x03c4 }
        r2 = (long) r2;	 Catch:{ Exception -> 0x03c4 }
        r30 = r2;
        r2 = r26.map(r27, r28, r30);	 Catch:{ Exception -> 0x03c4 }
        r3 = r2.limit();	 Catch:{ Exception -> 0x03c4 }
        r4 = 0;
        r5 = 1;
        org.telegram.messenger.Utilities.loadWebpImage(r4, r2, r3, r1, r5);	 Catch:{ Exception -> 0x03c4 }
        r0.close();	 Catch:{ Exception -> 0x03c4 }
        goto L_0x03c8;
    L_0x03c4:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03c8:
        r0 = r1.outWidth;
        if (r0 == 0) goto L_0x0400;
    L_0x03cc:
        r2 = r1.outHeight;
        if (r2 == 0) goto L_0x0400;
    L_0x03d0:
        r3 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r0 > r3) goto L_0x0400;
    L_0x03d4:
        r0 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r2 > r0) goto L_0x0400;
    L_0x03d8:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
        r0.<init>();
        r2 = r17;
        r0.alt = r2;
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
        r3.<init>();
        r0.stickerset = r3;
        r3 = r13.attributes;
        r3.add(r0);
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
        r0.<init>();
        r3 = r1.outWidth;
        r0.w = r3;
        r1 = r1.outHeight;
        r0.h = r1;
        r1 = r13.attributes;
        r1.add(r0);
        goto L_0x0405;
    L_0x0400:
        r2 = r17;
        goto L_0x0405;
    L_0x0403:
        r15 = r9;
        r2 = r10;
    L_0x0405:
        r3 = r13;
        if (r40 == 0) goto L_0x040e;
    L_0x0408:
        r0 = r40.toString();
        r10 = r0;
        goto L_0x040f;
    L_0x040e:
        r10 = r2;
    L_0x040f:
        r5 = new java.util.HashMap;
        r5.<init>();
        if (r15 == 0) goto L_0x041c;
    L_0x0416:
        r0 = "originalPath";
        r1 = r15;
        r5.put(r0, r1);
    L_0x041c:
        if (r43 == 0) goto L_0x0425;
    L_0x041e:
        r0 = "forceDocument";
        r1 = "1";
        r5.put(r0, r1);
    L_0x0425:
        r12 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$GW3ba-P8hFjsLsFVQeEW783hKH0;
        r0 = r12;
        r1 = r42;
        r2 = r32;
        r4 = r7;
        r6 = r25;
        r7 = r37;
        r9 = r39;
        r11 = r41;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r12);
        r1 = 1;
        return r1;
    L_0x043d:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, boolean):boolean");
    }

    static /* synthetic */ void lambda$prepareSendingDocumentInternal$44(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, String str3, ArrayList arrayList) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, null, tL_document, str, hashMap, false, str2);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tL_document, null, str, j, messageObject2, str3, arrayList, null, hashMap, 0, str2);
        }
    }

    public static void prepareSendingDocument(AccountInstance accountInstance, String str, String str2, Uri uri, String str3, String str4, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject2) {
        String str5 = str;
        String str6 = str2;
        Uri uri2 = uri;
        if ((str5 != null && str6 != null) || uri2 != null) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = null;
            if (uri2 != null) {
                arrayList3 = new ArrayList();
                arrayList3.add(uri);
            }
            if (str5 != null) {
                arrayList.add(str);
                arrayList2.add(str2);
            }
            prepareSendingDocuments(accountInstance, arrayList, arrayList2, arrayList3, str3, str4, j, messageObject, inputContentInfoCompat, messageObject2);
        }
    }

    public static void prepareSendingAudioDocuments(AccountInstance accountInstance, ArrayList<MessageObject> arrayList, long j, MessageObject messageObject, MessageObject messageObject2) {
        new Thread(new -$$Lambda$SendMessagesHelper$Th7qee2wjQQ5HZvnA8bNq8e4HSc(arrayList, j, accountInstance, messageObject2, messageObject)).start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0094 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x008f  */
    static /* synthetic */ void lambda$prepareSendingAudioDocuments$46(java.util.ArrayList r22, long r23, org.telegram.messenger.AccountInstance r25, org.telegram.messenger.MessageObject r26, org.telegram.messenger.MessageObject r27) {
        /*
        r10 = r23;
        r12 = r22.size();
        r13 = 0;
        r14 = 0;
    L_0x0008:
        if (r14 >= r12) goto L_0x00aa;
    L_0x000a:
        r15 = r22;
        r0 = r15.get(r14);
        r4 = r0;
        r4 = (org.telegram.messenger.MessageObject) r4;
        r0 = r4.messageOwner;
        r0 = r0.attachPath;
        r1 = new java.io.File;
        r1.<init>(r0);
        r2 = (int) r10;
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
        r1 = r5[r13];
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
        r1 = r10 >> r1;
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
        r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$RdpNtTp66jQAnXJayTSc5r1Ytng;
        r0 = r16;
        r1 = r26;
        r2 = r25;
        r7 = r23;
        r9 = r27;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);
        r14 = r14 + 1;
        goto L_0x0008;
    L_0x00aa:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$46(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject):void");
    }

    static /* synthetic */ void lambda$null$45(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3) {
        MessageObject messageObject4 = messageObject2;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, null, tL_document, messageObject4.messageOwner.attachPath, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, null, messageObject4.messageOwner.attachPath, j, messageObject3, null, null, null, hashMap, 0, str);
    }

    public static void prepareSendingDocuments(AccountInstance accountInstance, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<Uri> arrayList3, String str, String str2, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject2) {
        if (!(arrayList == null && arrayList2 == null && arrayList3 == null) && (arrayList == null || arrayList2 == null || arrayList.size() == arrayList2.size())) {
            new Thread(new -$$Lambda$SendMessagesHelper$VXidY7vzhuhTmaAfAfmKEVsa6Iw(arrayList, accountInstance, arrayList2, str2, j, messageObject, str, messageObject2, arrayList3, inputContentInfoCompat)).start();
        }
    }

    static /* synthetic */ void lambda$prepareSendingDocuments$48(ArrayList arrayList, AccountInstance accountInstance, ArrayList arrayList2, String str, long j, MessageObject messageObject, String str2, MessageObject messageObject2, ArrayList arrayList3, InputContentInfoCompat inputContentInfoCompat) {
        Object obj;
        ArrayList arrayList4 = arrayList;
        ArrayList arrayList5 = arrayList3;
        if (arrayList4 != null) {
            obj = null;
            for (int i = 0; i < arrayList.size(); i++) {
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList4.get(i), (String) arrayList2.get(i), null, str, j, messageObject, str2, null, messageObject2, false)) {
                    obj = 1;
                }
            }
        } else {
            obj = null;
        }
        if (arrayList5 != null) {
            for (int i2 = 0; i2 < arrayList3.size(); i2++) {
                if (!prepareSendingDocumentInternal(accountInstance, null, null, (Uri) arrayList5.get(i2), str, j, messageObject, str2, null, messageObject2, false)) {
                    obj = 1;
                }
            }
        }
        if (inputContentInfoCompat != null) {
            inputContentInfoCompat.releasePermission();
        }
        if (obj != null) {
            AndroidUtilities.runOnUIThread(-$$Lambda$SendMessagesHelper$AzNNw46d9NqQLWk4ri-KgnUrWCU.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$null$47() {
        try {
            Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", NUM), 0).show();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, Uri uri, long j, MessageObject messageObject, CharSequence charSequence, ArrayList<MessageEntity> arrayList, ArrayList<InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject2) {
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
        prepareSendingMedia(accountInstance, arrayList4, j, messageObject, inputContentInfoCompat, false, false, messageObject2);
    }

    public static void prepareSendingBotContextResult(AccountInstance accountInstance, BotInlineResult botInlineResult, HashMap<String, String> hashMap, long j, MessageObject messageObject) {
        BotInlineResult botInlineResult2 = botInlineResult;
        if (botInlineResult2 != null) {
            BotInlineMessage botInlineMessage = botInlineResult2.send_message;
            if (botInlineMessage instanceof TL_botInlineMessageMediaAuto) {
                new Thread(new -$$Lambda$SendMessagesHelper$RlwhLE8SG2BzLNJECNyLkrGcsXs(j, botInlineResult, accountInstance, hashMap, messageObject)).run();
            } else if (botInlineMessage instanceof TL_botInlineMessageText) {
                WebPage webPage = null;
                if (((int) j) == 0) {
                    for (int i = 0; i < botInlineResult2.send_message.entities.size(); i++) {
                        MessageEntity messageEntity = (MessageEntity) botInlineResult2.send_message.entities.get(i);
                        if (messageEntity instanceof TL_messageEntityUrl) {
                            webPage = new TL_webPagePending();
                            String str = botInlineResult2.send_message.message;
                            int i2 = messageEntity.offset;
                            webPage.url = str.substring(i2, messageEntity.length + i2);
                            break;
                        }
                    }
                }
                WebPage webPage2 = webPage;
                SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
                botInlineMessage = botInlineResult2.send_message;
                sendMessagesHelper.sendMessage(botInlineMessage.message, j, messageObject, webPage2, botInlineMessage.no_webpage ^ 1, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap);
            } else {
                long j2 = j;
                MessageMedia tL_messageMediaVenue;
                if (botInlineMessage instanceof TL_botInlineMessageMediaVenue) {
                    tL_messageMediaVenue = new TL_messageMediaVenue();
                    botInlineMessage = botInlineResult2.send_message;
                    tL_messageMediaVenue.geo = botInlineMessage.geo;
                    tL_messageMediaVenue.address = botInlineMessage.address;
                    tL_messageMediaVenue.title = botInlineMessage.title;
                    tL_messageMediaVenue.provider = botInlineMessage.provider;
                    tL_messageMediaVenue.venue_id = botInlineMessage.venue_id;
                    String str2 = botInlineMessage.venue_type;
                    tL_messageMediaVenue.venue_id = str2;
                    tL_messageMediaVenue.venue_type = str2;
                    if (tL_messageMediaVenue.venue_type == null) {
                        tL_messageMediaVenue.venue_type = "";
                    }
                    accountInstance.getSendMessagesHelper().sendMessage(tL_messageMediaVenue, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap);
                } else if (botInlineMessage instanceof TL_botInlineMessageMediaGeo) {
                    if (botInlineMessage.period != 0) {
                        tL_messageMediaVenue = new TL_messageMediaGeoLive();
                        botInlineMessage = botInlineResult2.send_message;
                        tL_messageMediaVenue.period = botInlineMessage.period;
                        tL_messageMediaVenue.geo = botInlineMessage.geo;
                        accountInstance.getSendMessagesHelper().sendMessage(tL_messageMediaVenue, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap);
                    } else {
                        tL_messageMediaVenue = new TL_messageMediaGeo();
                        tL_messageMediaVenue.geo = botInlineResult2.send_message.geo;
                        accountInstance.getSendMessagesHelper().sendMessage(tL_messageMediaVenue, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap);
                    }
                } else if (botInlineMessage instanceof TL_botInlineMessageMediaContact) {
                    User tL_user = new TL_user();
                    botInlineMessage = botInlineResult2.send_message;
                    tL_user.phone = botInlineMessage.phone_number;
                    tL_user.first_name = botInlineMessage.first_name;
                    tL_user.last_name = botInlineMessage.last_name;
                    tL_user.restriction_reason = botInlineMessage.vcard;
                    accountInstance.getSendMessagesHelper().sendMessage(tL_user, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap);
                }
            }
        }
    }

    static /* synthetic */ void lambda$prepareSendingBotContextResult$50(long r17, org.telegram.tgnet.TLRPC.BotInlineResult r19, org.telegram.messenger.AccountInstance r20, java.util.HashMap r21, org.telegram.messenger.MessageObject r22) {
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
        r11 = r0;
        r2 = r7;
        r4 = r2;
        r10 = r4;
        goto L_0x0400;
    L_0x004e:
        r0 = r8 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMediaResult;
        if (r0 == 0) goto L_0x0070;
    L_0x0052:
        r0 = r8.document;
        if (r0 == 0) goto L_0x0060;
    L_0x0056:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r1 == 0) goto L_0x03fc;
    L_0x005a:
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;
        r2 = r0;
        r4 = r7;
        goto L_0x03fe;
    L_0x0060:
        r0 = r8.photo;
        if (r0 == 0) goto L_0x03fc;
    L_0x0064:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r1 == 0) goto L_0x03fc;
    L_0x0068:
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        r10 = r0;
        r2 = r7;
        r4 = r2;
        r11 = r4;
        goto L_0x0400;
    L_0x0070:
        r0 = r8.content;
        if (r0 == 0) goto L_0x03fc;
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
            case 0: goto L_0x015b;
            case 1: goto L_0x015b;
            case 2: goto L_0x015b;
            case 3: goto L_0x015b;
            case 4: goto L_0x015b;
            case 5: goto L_0x015b;
            case 6: goto L_0x010b;
            default: goto L_0x0105;
        };
    L_0x0105:
        r2 = r7;
        r11 = r2;
        r4 = r10;
        r10 = r11;
        goto L_0x0400;
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
        r2 = r7;
        r11 = r2;
        r4 = r10;
        r10 = r0;
        goto L_0x0400;
    L_0x015b:
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
            case -1890252483: goto L_0x01be;
            case 102340: goto L_0x01b6;
            case 3143036: goto L_0x01ae;
            case 93166550: goto L_0x01a6;
            case 112202875: goto L_0x019c;
            case 112386354: goto L_0x0192;
            default: goto L_0x0191;
        };
    L_0x0191:
        goto L_0x01c6;
    L_0x0192:
        r0 = "voice";
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x01c6;
    L_0x019a:
        r0 = 1;
        goto L_0x01c7;
    L_0x019c:
        r0 = "video";
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x01c6;
    L_0x01a4:
        r0 = 4;
        goto L_0x01c7;
    L_0x01a6:
        r0 = r2.equals(r15);
        if (r0 == 0) goto L_0x01c6;
    L_0x01ac:
        r0 = 2;
        goto L_0x01c7;
    L_0x01ae:
        r0 = r2.equals(r14);
        if (r0 == 0) goto L_0x01c6;
    L_0x01b4:
        r0 = 3;
        goto L_0x01c7;
    L_0x01b6:
        r0 = r2.equals(r4);
        if (r0 == 0) goto L_0x01c6;
    L_0x01bc:
        r0 = 0;
        goto L_0x01c7;
    L_0x01be:
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x01c6;
    L_0x01c4:
        r0 = 5;
        goto L_0x01c7;
    L_0x01c6:
        r0 = -1;
    L_0x01c7:
        r2 = 55;
        if (r0 == 0) goto L_0x035b;
    L_0x01cb:
        r4 = 1;
        if (r0 == r4) goto L_0x0343;
    L_0x01ce:
        r3 = 2;
        if (r0 == r3) goto L_0x0317;
    L_0x01d1:
        r3 = 3;
        if (r0 == r3) goto L_0x02e7;
    L_0x01d4:
        r3 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r4 = 4;
        if (r0 == r4) goto L_0x0269;
    L_0x01d9:
        r4 = 5;
        if (r0 == r4) goto L_0x01de;
    L_0x01dc:
        goto L_0x03b8;
    L_0x01de:
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
        r0 = r8.thumb;	 Catch:{ Throwable -> 0x0263 }
        if (r0 == 0) goto L_0x03b8;
    L_0x0213:
        r0 = new java.io.File;	 Catch:{ Throwable -> 0x0263 }
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ Throwable -> 0x0263 }
        r13 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0263 }
        r13.<init>();	 Catch:{ Throwable -> 0x0263 }
        r15 = r8.thumb;	 Catch:{ Throwable -> 0x0263 }
        r15 = r15.url;	 Catch:{ Throwable -> 0x0263 }
        r15 = org.telegram.messenger.Utilities.MD5(r15);	 Catch:{ Throwable -> 0x0263 }
        r13.append(r15);	 Catch:{ Throwable -> 0x0263 }
        r13.append(r12);	 Catch:{ Throwable -> 0x0263 }
        r12 = r8.thumb;	 Catch:{ Throwable -> 0x0263 }
        r12 = r12.url;	 Catch:{ Throwable -> 0x0263 }
        r15 = "webp";
        r12 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r12, r15);	 Catch:{ Throwable -> 0x0263 }
        r13.append(r12);	 Catch:{ Throwable -> 0x0263 }
        r12 = r13.toString();	 Catch:{ Throwable -> 0x0263 }
        r0.<init>(r4, r12);	 Catch:{ Throwable -> 0x0263 }
        r0 = r0.getAbsolutePath();	 Catch:{ Throwable -> 0x0263 }
        r4 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r7, r3, r3, r4);	 Catch:{ Throwable -> 0x0263 }
        if (r0 == 0) goto L_0x03b8;
    L_0x024c:
        r4 = 0;
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r4);	 Catch:{ Throwable -> 0x0263 }
        if (r2 == 0) goto L_0x025e;
    L_0x0253:
        r3 = r11.thumbs;	 Catch:{ Throwable -> 0x0263 }
        r3.add(r2);	 Catch:{ Throwable -> 0x0263 }
        r2 = r11.flags;	 Catch:{ Throwable -> 0x0263 }
        r3 = 1;
        r2 = r2 | r3;
        r11.flags = r2;	 Catch:{ Throwable -> 0x0263 }
    L_0x025e:
        r0.recycle();	 Catch:{ Throwable -> 0x0263 }
        goto L_0x03b8;
    L_0x0263:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x03b8;
    L_0x0269:
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
        r0 = r8.thumb;	 Catch:{ Throwable -> 0x02e1 }
        if (r0 == 0) goto L_0x03b8;
    L_0x0291:
        r0 = new java.io.File;	 Catch:{ Throwable -> 0x02e1 }
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ Throwable -> 0x02e1 }
        r13 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02e1 }
        r13.<init>();	 Catch:{ Throwable -> 0x02e1 }
        r15 = r8.thumb;	 Catch:{ Throwable -> 0x02e1 }
        r15 = r15.url;	 Catch:{ Throwable -> 0x02e1 }
        r15 = org.telegram.messenger.Utilities.MD5(r15);	 Catch:{ Throwable -> 0x02e1 }
        r13.append(r15);	 Catch:{ Throwable -> 0x02e1 }
        r13.append(r12);	 Catch:{ Throwable -> 0x02e1 }
        r12 = r8.thumb;	 Catch:{ Throwable -> 0x02e1 }
        r12 = r12.url;	 Catch:{ Throwable -> 0x02e1 }
        r15 = "jpg";
        r12 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r12, r15);	 Catch:{ Throwable -> 0x02e1 }
        r13.append(r12);	 Catch:{ Throwable -> 0x02e1 }
        r12 = r13.toString();	 Catch:{ Throwable -> 0x02e1 }
        r0.<init>(r4, r12);	 Catch:{ Throwable -> 0x02e1 }
        r0 = r0.getAbsolutePath();	 Catch:{ Throwable -> 0x02e1 }
        r4 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r7, r3, r3, r4);	 Catch:{ Throwable -> 0x02e1 }
        if (r0 == 0) goto L_0x03b8;
    L_0x02ca:
        r4 = 0;
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r4);	 Catch:{ Throwable -> 0x02e1 }
        if (r2 == 0) goto L_0x02dc;
    L_0x02d1:
        r3 = r11.thumbs;	 Catch:{ Throwable -> 0x02e1 }
        r3.add(r2);	 Catch:{ Throwable -> 0x02e1 }
        r2 = r11.flags;	 Catch:{ Throwable -> 0x02e1 }
        r3 = 1;
        r2 = r2 | r3;
        r11.flags = r2;	 Catch:{ Throwable -> 0x02e1 }
    L_0x02dc:
        r0.recycle();	 Catch:{ Throwable -> 0x02e1 }
        goto L_0x03b8;
    L_0x02e1:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x03b8;
    L_0x02e7:
        r0 = r8.content;
        r0 = r0.mime_type;
        r2 = 47;
        r0 = r0.lastIndexOf(r2);
        r2 = -1;
        if (r0 == r2) goto L_0x0313;
    L_0x02f4:
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
        goto L_0x03b8;
    L_0x0313:
        r1.file_name = r14;
        goto L_0x03b8;
    L_0x0317:
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
        if (r2 == 0) goto L_0x0338;
    L_0x0330:
        r0.performer = r2;
        r2 = r0.flags;
        r3 = 2;
        r2 = r2 | r3;
        r0.flags = r2;
    L_0x0338:
        r2 = "audio.mp3";
        r1.file_name = r2;
        r2 = r11.attributes;
        r2.add(r0);
        goto L_0x03b8;
    L_0x0343:
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
        goto L_0x03b8;
    L_0x035b:
        r0 = "animation.gif";
        r1.file_name = r0;
        r0 = "mp4";
        r0 = r10.endsWith(r0);
        if (r0 == 0) goto L_0x0376;
    L_0x0367:
        r0 = "video/mp4";
        r11.mime_type = r0;
        r0 = r11.attributes;
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r4.<init>();
        r0.add(r4);
        goto L_0x037a;
    L_0x0376:
        r0 = "image/gif";
        r11.mime_type = r0;
    L_0x037a:
        if (r3 == 0) goto L_0x037f;
    L_0x037c:
        r0 = 90;
        goto L_0x0381;
    L_0x037f:
        r0 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
    L_0x0381:
        r3 = "mp4";
        r3 = r10.endsWith(r3);	 Catch:{ Throwable -> 0x03b4 }
        if (r3 == 0) goto L_0x038f;
    L_0x0389:
        r3 = 1;
        r4 = android.media.ThumbnailUtils.createVideoThumbnail(r10, r3);	 Catch:{ Throwable -> 0x03b4 }
        goto L_0x0395;
    L_0x038f:
        r3 = 1;
        r4 = (float) r0;	 Catch:{ Throwable -> 0x03b4 }
        r4 = org.telegram.messenger.ImageLoader.loadBitmap(r10, r7, r4, r4, r3);	 Catch:{ Throwable -> 0x03b4 }
    L_0x0395:
        if (r4 == 0) goto L_0x03b8;
    L_0x0397:
        r3 = (float) r0;	 Catch:{ Throwable -> 0x03b4 }
        r12 = 90;
        if (r0 <= r12) goto L_0x039e;
    L_0x039c:
        r2 = 80;
    L_0x039e:
        r12 = 0;
        r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r4, r3, r3, r2, r12);	 Catch:{ Throwable -> 0x03b4 }
        if (r0 == 0) goto L_0x03b0;
    L_0x03a5:
        r2 = r11.thumbs;	 Catch:{ Throwable -> 0x03b4 }
        r2.add(r0);	 Catch:{ Throwable -> 0x03b4 }
        r0 = r11.flags;	 Catch:{ Throwable -> 0x03b4 }
        r2 = 1;
        r0 = r0 | r2;
        r11.flags = r0;	 Catch:{ Throwable -> 0x03b4 }
    L_0x03b0:
        r4.recycle();	 Catch:{ Throwable -> 0x03b4 }
        goto L_0x03b8;
    L_0x03b4:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03b8:
        r0 = r1.file_name;
        if (r0 != 0) goto L_0x03be;
    L_0x03bc:
        r1.file_name = r14;
    L_0x03be:
        r0 = r11.mime_type;
        if (r0 != 0) goto L_0x03c6;
    L_0x03c2:
        r0 = "application/octet-stream";
        r11.mime_type = r0;
    L_0x03c6:
        r0 = r11.thumbs;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x03f8;
    L_0x03ce:
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
    L_0x03f8:
        r4 = r10;
        r2 = r11;
        r10 = r7;
        goto L_0x03ff;
    L_0x03fc:
        r2 = r7;
        r4 = r2;
    L_0x03fe:
        r10 = r4;
    L_0x03ff:
        r11 = r10;
    L_0x0400:
        if (r9 == 0) goto L_0x040d;
    L_0x0402:
        r0 = r8.content;
        if (r0 == 0) goto L_0x040d;
    L_0x0406:
        r0 = r0.url;
        r1 = "originalPath";
        r9.put(r1, r0);
    L_0x040d:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$7D8JZr1AQYTSAuP2GztkSkNw_L8;
        r1 = r0;
        r3 = r20;
        r5 = r17;
        r7 = r22;
        r8 = r19;
        r9 = r21;
        r1.<init>(r2, r3, r4, r5, r7, r8, r9, r10, r11);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$50(long, org.telegram.tgnet.TLRPC$BotInlineResult, org.telegram.messenger.AccountInstance, java.util.HashMap, org.telegram.messenger.MessageObject):void");
    }

    static /* synthetic */ void lambda$null$49(TL_document tL_document, AccountInstance accountInstance, String str, long j, MessageObject messageObject, BotInlineResult botInlineResult, HashMap hashMap, TL_photo tL_photo, TL_game tL_game) {
        BotInlineResult botInlineResult2 = botInlineResult;
        SendMessagesHelper sendMessagesHelper;
        BotInlineMessage botInlineMessage;
        if (tL_document != null) {
            sendMessagesHelper = accountInstance.getSendMessagesHelper();
            botInlineMessage = botInlineResult2.send_message;
            sendMessagesHelper.sendMessage(tL_document, null, str, j, messageObject, botInlineMessage.message, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap, 0, botInlineResult);
        } else if (tL_photo != null) {
            sendMessagesHelper = accountInstance.getSendMessagesHelper();
            WebDocument webDocument = botInlineResult2.content;
            String str2 = webDocument != null ? webDocument.url : null;
            botInlineMessage = botInlineResult2.send_message;
            sendMessagesHelper.sendMessage(tL_photo, str2, j, messageObject, botInlineMessage.message, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap, 0, botInlineResult);
        } else if (tL_game != null) {
            accountInstance.getSendMessagesHelper().sendMessage(tL_game, j, botInlineResult2.send_message.reply_markup, hashMap);
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

    public static void prepareSendingText(AccountInstance accountInstance, String str, long j) {
        accountInstance.getMessagesStorage().getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$jhRjY8xWtPrfzqwQH7U_YEYkCUI(str, accountInstance, j));
    }

    static /* synthetic */ void lambda$null$51(String str, AccountInstance accountInstance, long j) {
        String trimmedString = getTrimmedString(str);
        if (trimmedString.length() != 0) {
            int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / 4096.0f));
            int i = 0;
            while (i < ceil) {
                int i2 = i * 4096;
                i++;
                accountInstance.getSendMessagesHelper().sendMessage(trimmedString.substring(i2, Math.min(i * 4096, trimmedString.length())), j, null, null, true, null, null, null);
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
        r5 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x00e4 }
        r5.<init>();	 Catch:{ Throwable -> 0x00e4 }
        r5.inJustDecodeBounds = r1;	 Catch:{ Throwable -> 0x00e4 }
        r6 = org.telegram.messenger.FileLoader.getPathToAttach(r10);	 Catch:{ Throwable -> 0x00e4 }
        r7 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x00e4 }
        r7.<init>(r6);	 Catch:{ Throwable -> 0x00e4 }
        android.graphics.BitmapFactory.decodeStream(r7, r0, r5);	 Catch:{ Throwable -> 0x00e4 }
        r7.close();	 Catch:{ Throwable -> 0x00e4 }
        r7 = r5.outWidth;	 Catch:{ Throwable -> 0x00e4 }
        r7 = (float) r7;	 Catch:{ Throwable -> 0x00e4 }
        r8 = r5.outHeight;	 Catch:{ Throwable -> 0x00e4 }
        r8 = (float) r8;	 Catch:{ Throwable -> 0x00e4 }
        r9 = (float) r3;	 Catch:{ Throwable -> 0x00e4 }
        r7 = r7 / r9;
        r9 = (float) r2;	 Catch:{ Throwable -> 0x00e4 }
        r8 = r8 / r9;
        r7 = java.lang.Math.max(r7, r8);	 Catch:{ Throwable -> 0x00e4 }
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
        if (r9 >= 0) goto L_0x00c6;
    L_0x00c4:
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x00c6:
        r5.inJustDecodeBounds = r4;	 Catch:{ Throwable -> 0x00e4 }
        r7 = (int) r7;	 Catch:{ Throwable -> 0x00e4 }
        r5.inSampleSize = r7;	 Catch:{ Throwable -> 0x00e4 }
        r7 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x00e4 }
        r5.inPreferredConfig = r7;	 Catch:{ Throwable -> 0x00e4 }
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x00e4 }
        r8 = 21;
        if (r7 < r8) goto L_0x00e5;
    L_0x00d5:
        r7 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x00e4 }
        r7.<init>(r6);	 Catch:{ Throwable -> 0x00e4 }
        r0 = android.graphics.BitmapFactory.decodeStream(r7, r0, r5);	 Catch:{ Throwable -> 0x00e4 }
        r11[r4] = r0;	 Catch:{ Throwable -> 0x00e4 }
        r7.close();	 Catch:{ Throwable -> 0x00e4 }
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

    public static void prepareSendingMedia(AccountInstance accountInstance, ArrayList<SendingMediaInfo> arrayList, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, boolean z, boolean z2, MessageObject messageObject2) {
        if (!arrayList.isEmpty()) {
            mediaSendQueue.postRunnable(new -$$Lambda$SendMessagesHelper$uyynx4P4Tnp8wHI_x7UE1lxua40(arrayList, j, accountInstance, z, z2, messageObject2, messageObject, inputContentInfoCompat));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0146  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02c9 A:{Catch:{ Exception -> 0x02b1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02bf A:{Catch:{ Exception -> 0x02b1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02d6 A:{Catch:{ Exception -> 0x02b1 }} */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0405  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x06ff  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0626  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0713  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0725  */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x076a  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0a88  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a59  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a59  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0a88  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x08f4  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x08b3  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x08b3  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x08f4  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x08f4  */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x08b3  */
    /* JADX WARNING: Removed duplicated region for block: B:340:0x08ad  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x08a3  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0b24  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0b19  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0b28  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0b3a A:{LOOP_END, LOOP:4: B:449:0x0b34->B:451:0x0b3a} */
    /* JADX WARNING: Removed duplicated region for block: B:469:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0b73  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0a88  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a59  */
    /* JADX WARNING: Missing block: B:40:0x009d, code skipped:
            if (org.telegram.messenger.MediaController.isWebp(r3.uri) != false) goto L_0x0159;
     */
    /* JADX WARNING: Missing block: B:428:0x0a79, code skipped:
            if (r10 == (r20 - 1)) goto L_0x0a7e;
     */
    static /* synthetic */ void lambda$prepareSendingMedia$60(java.util.ArrayList r55, long r56, org.telegram.messenger.AccountInstance r58, boolean r59, boolean r60, org.telegram.messenger.MessageObject r61, org.telegram.messenger.MessageObject r62, androidx.core.view.inputmethod.InputContentInfoCompat r63) {
        /*
        r1 = r55;
        r14 = r56;
        r13 = r58;
        r16 = java.lang.System.currentTimeMillis();
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
        r18 = 3;
        r7 = "_";
        if (r10 == 0) goto L_0x003e;
    L_0x003a:
        r0 = 73;
        if (r8 < r0) goto L_0x016d;
    L_0x003e:
        if (r59 != 0) goto L_0x016d;
    L_0x0040:
        if (r60 == 0) goto L_0x016d;
    L_0x0042:
        r0 = new java.util.HashMap;
        r0.<init>();
        r4 = 0;
    L_0x0048:
        if (r4 >= r12) goto L_0x0167;
    L_0x004a:
        r2 = r1.get(r4);
        r3 = r2;
        r3 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r3;
        r2 = r3.searchImage;
        if (r2 != 0) goto L_0x0159;
    L_0x0055:
        r2 = r3.isVideo;
        if (r2 != 0) goto L_0x0159;
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
        r20 = r2.endsWith(r6);
        if (r20 != 0) goto L_0x0159;
    L_0x0075:
        r9 = ".webp";
        r9 = r2.endsWith(r9);
        if (r9 == 0) goto L_0x007f;
    L_0x007d:
        goto L_0x0159;
    L_0x007f:
        r9 = r3.path;
        r11 = r3.uri;
        r9 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r9, r11);
        if (r9 == 0) goto L_0x008b;
    L_0x0089:
        goto L_0x0159;
    L_0x008b:
        if (r2 != 0) goto L_0x00a1;
    L_0x008d:
        r9 = r3.uri;
        if (r9 == 0) goto L_0x00a1;
    L_0x0091:
        r9 = org.telegram.messenger.MediaController.isGif(r9);
        if (r9 != 0) goto L_0x0159;
    L_0x0097:
        r9 = r3.uri;
        r9 = org.telegram.messenger.MediaController.isWebp(r9);
        if (r9 == 0) goto L_0x00a1;
    L_0x009f:
        goto L_0x0159;
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
        if (r10 != 0) goto L_0x012d;
    L_0x00cb:
        r2 = r3.ttl;
        if (r2 != 0) goto L_0x012d;
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
        if (r5 != 0) goto L_0x0114;
    L_0x00ed:
        r4 = r3.uri;
        if (r4 == 0) goto L_0x0114;
    L_0x00f1:
        r4 = r58.getMessagesStorage();
        r9 = r3.uri;
        r9 = org.telegram.messenger.AndroidUtilities.getPath(r9);
        if (r10 != 0) goto L_0x0101;
    L_0x00fd:
        r22 = r2;
        r2 = 0;
        goto L_0x0104;
    L_0x0101:
        r22 = r2;
        r2 = 3;
    L_0x0104:
        r2 = r4.getSentFile(r9, r2);
        if (r2 == 0) goto L_0x0116;
    L_0x010a:
        r4 = 0;
        r5 = r2[r4];
        r5 = (org.telegram.tgnet.TLRPC.TL_photo) r5;
        r4 = 1;
        r2 = r2[r4];
        r2 = (java.lang.String) r2;
    L_0x0114:
        r22 = r2;
    L_0x0116:
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
        goto L_0x0135;
    L_0x012d:
        r25 = r3;
        r14 = r6;
        r26 = r7;
        r15 = 0;
        r2 = r15;
        r9 = r2;
    L_0x0135:
        r3 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker;
        r3.<init>(r15);
        r4 = r25;
        r0.put(r4, r3);
        if (r9 == 0) goto L_0x0146;
    L_0x0141:
        r3.parentObject = r2;
        r3.photo = r9;
        goto L_0x015e;
    L_0x0146:
        r2 = new java.util.concurrent.CountDownLatch;
        r5 = 1;
        r2.<init>(r5);
        r3.sync = r2;
        r2 = mediaSendThreadPool;
        r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$bJxafds_UeAH5RxnW3XLs-AC6o8;
        r5.<init>(r3, r13, r4, r10);
        r2.execute(r5);
        goto L_0x015e;
    L_0x0159:
        r11 = r4;
        r14 = r6;
        r26 = r7;
        r15 = 0;
    L_0x015e:
        r4 = r11 + 1;
        r6 = r14;
        r7 = r26;
        r14 = r56;
        goto L_0x0048;
    L_0x0167:
        r14 = r6;
        r26 = r7;
        r15 = 0;
        r11 = r0;
        goto L_0x0172;
    L_0x016d:
        r14 = r6;
        r26 = r7;
        r15 = 0;
        r11 = r15;
    L_0x0172:
        r4 = r15;
        r5 = r4;
        r19 = r5;
        r27 = r19;
        r28 = r27;
        r0 = 0;
        r2 = 0;
        r9 = 0;
        r22 = 0;
    L_0x0180:
        if (r9 >= r12) goto L_0x0b0e;
    L_0x0182:
        r24 = r1.get(r9);
        r15 = r24;
        r15 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r15;
        if (r60 == 0) goto L_0x01a5;
    L_0x018c:
        if (r10 == 0) goto L_0x0192;
    L_0x018e:
        r6 = 73;
        if (r8 < r6) goto L_0x01a5;
    L_0x0192:
        r6 = 1;
        if (r12 <= r6) goto L_0x01a5;
    L_0x0195:
        r6 = r0 % 10;
        if (r6 != 0) goto L_0x01a5;
    L_0x0199:
        r0 = org.telegram.messenger.Utilities.random;
        r2 = r0.nextLong();
        r6 = r2;
        r23 = r6;
        r22 = 0;
        goto L_0x01ab;
    L_0x01a5:
        r6 = r22;
        r22 = r0;
        r23 = r2;
    L_0x01ab:
        r0 = r15.searchImage;
        r2 = "mp4";
        r3 = "originalPath";
        r31 = r5;
        r5 = "";
        r34 = 4;
        if (r0 == 0) goto L_0x050c;
    L_0x01b9:
        r1 = r0.type;
        r35 = r4;
        r4 = "jpg";
        r36 = r6;
        r6 = ".";
        r7 = 1;
        if (r1 != r7) goto L_0x0392;
    L_0x01c6:
        r1 = new java.util.HashMap;
        r1.<init>();
        r32 = 0;
        r0 = r15.searchImage;
        r0 = r0.document;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r5 == 0) goto L_0x01dd;
    L_0x01d5:
        r5 = r0;
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r5, r7);
        goto L_0x020a;
    L_0x01dd:
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
    L_0x020a:
        if (r5 != 0) goto L_0x032f;
    L_0x020c:
        r5 = r15.searchImage;
        r5 = r5.localUrl;
        if (r5 == 0) goto L_0x0217;
    L_0x0212:
        r7 = "url";
        r1.put(r7, r5);
    L_0x0217:
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
        if (r7 == 0) goto L_0x0261;
    L_0x0252:
        r7 = "video/mp4";
        r5.mime_type = r7;
        r7 = r5.attributes;
        r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r8.<init>();
        r7.add(r8);
        goto L_0x0265;
    L_0x0261:
        r7 = "image/gif";
        r5.mime_type = r7;
    L_0x0265:
        r7 = r0.exists();
        if (r7 == 0) goto L_0x026d;
    L_0x026b:
        r7 = r0;
        goto L_0x026f;
    L_0x026d:
        r0 = 0;
        r7 = 0;
    L_0x026f:
        if (r0 != 0) goto L_0x02a5;
    L_0x0271:
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
        if (r0 != 0) goto L_0x02a4;
    L_0x02a2:
        r0 = 0;
        goto L_0x02a5;
    L_0x02a4:
        r0 = r4;
    L_0x02a5:
        if (r0 == 0) goto L_0x02f8;
    L_0x02a7:
        if (r10 != 0) goto L_0x02b3;
    L_0x02a9:
        r4 = r15.ttl;	 Catch:{ Exception -> 0x02b1 }
        if (r4 == 0) goto L_0x02ae;
    L_0x02ad:
        goto L_0x02b3;
    L_0x02ae:
        r4 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x02b5;
    L_0x02b1:
        r0 = move-exception;
        goto L_0x02f5;
    L_0x02b3:
        r4 = 90;
    L_0x02b5:
        r6 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x02b1 }
        r2 = r6.endsWith(r2);	 Catch:{ Exception -> 0x02b1 }
        if (r2 == 0) goto L_0x02c9;
    L_0x02bf:
        r0 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x02b1 }
        r2 = 1;
        r0 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r2);	 Catch:{ Exception -> 0x02b1 }
        goto L_0x02d4;
    L_0x02c9:
        r2 = 1;
        r0 = r0.getAbsolutePath();	 Catch:{ Exception -> 0x02b1 }
        r6 = (float) r4;	 Catch:{ Exception -> 0x02b1 }
        r8 = 0;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r8, r6, r6, r2);	 Catch:{ Exception -> 0x02b1 }
    L_0x02d4:
        if (r0 == 0) goto L_0x02f8;
    L_0x02d6:
        r2 = (float) r4;	 Catch:{ Exception -> 0x02b1 }
        r6 = 90;
        if (r4 <= r6) goto L_0x02de;
    L_0x02db:
        r4 = 80;
        goto L_0x02e0;
    L_0x02de:
        r4 = 55;
    L_0x02e0:
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r4, r10);	 Catch:{ Exception -> 0x02b1 }
        if (r2 == 0) goto L_0x02f1;
    L_0x02e6:
        r4 = r5.thumbs;	 Catch:{ Exception -> 0x02b1 }
        r4.add(r2);	 Catch:{ Exception -> 0x02b1 }
        r2 = r5.flags;	 Catch:{ Exception -> 0x02b1 }
        r4 = 1;
        r2 = r2 | r4;
        r5.flags = r2;	 Catch:{ Exception -> 0x02b1 }
    L_0x02f1:
        r0.recycle();	 Catch:{ Exception -> 0x02b1 }
        goto L_0x02f8;
    L_0x02f5:
        org.telegram.messenger.FileLog.e(r0);
    L_0x02f8:
        r0 = r5.thumbs;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x032b;
    L_0x0300:
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
        r20 = 1;
        r0 = r0 | 1;
        r5.flags = r0;
        goto L_0x0335;
    L_0x032b:
        r8 = 0;
        r20 = 1;
        goto L_0x0335;
    L_0x032f:
        r38 = r8;
        r8 = 0;
        r20 = 1;
        r7 = r0;
    L_0x0335:
        r0 = r15.searchImage;
        r0 = r0.imageUrl;
        if (r7 != 0) goto L_0x033c;
    L_0x033b:
        goto L_0x0340;
    L_0x033c:
        r0 = r7.toString();
    L_0x0340:
        r6 = r0;
        r0 = r15.searchImage;
        r0 = r0.imageUrl;
        if (r0 == 0) goto L_0x034a;
    L_0x0347:
        r1.put(r3, r0);
    L_0x034a:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$VMVi_1zqWGe_Znr3O0I50-BSdfc;
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
        r20 = r12;
        r12 = r15;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r11, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        r0 = r22;
        r2 = r23;
        r5 = r39;
        r32 = r40;
        r4 = r42;
        r31 = r43;
        r21 = r44;
        r36 = r45;
        r25 = 0;
        r34 = 1;
        r35 = 0;
        r54 = r26;
        r26 = r1;
        r1 = r14;
        goto L_0x0508;
    L_0x0392:
        r1 = r8;
        r44 = r9;
        r43 = r10;
        r45 = r11;
        r20 = r12;
        r39 = r31;
        r42 = r35;
        r40 = r36;
        r29 = 0;
        r9 = 0;
        r0 = r0.photo;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r2 == 0) goto L_0x03af;
    L_0x03aa:
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        r12 = r43;
        goto L_0x03b6;
    L_0x03af:
        r12 = r43;
        if (r12 != 0) goto L_0x03b5;
    L_0x03b3:
        r0 = r15.ttl;
    L_0x03b5:
        r0 = 0;
    L_0x03b6:
        if (r0 != 0) goto L_0x047e;
    L_0x03b8:
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
        if (r2 == 0) goto L_0x0402;
    L_0x03e9:
        r10 = r7.length();
        r2 = (r10 > r29 ? 1 : (r10 == r29 ? 0 : -1));
        if (r2 == 0) goto L_0x0402;
    L_0x03f1:
        r0 = r58.getSendMessagesHelper();
        r2 = r7.toString();
        r7 = 0;
        r0 = r0.generatePhotoSizes(r2, r7);
        if (r0 == 0) goto L_0x0402;
    L_0x0400:
        r2 = 0;
        goto L_0x0403;
    L_0x0402:
        r2 = 1;
    L_0x0403:
        if (r0 != 0) goto L_0x047b;
    L_0x0405:
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
        if (r4 == 0) goto L_0x0443;
    L_0x0436:
        r0 = r58.getSendMessagesHelper();
        r4 = r6.toString();
        r6 = 0;
        r0 = r0.generatePhotoSizes(r4, r6);
    L_0x0443:
        if (r0 != 0) goto L_0x047b;
    L_0x0445:
        r0 = new org.telegram.tgnet.TLRPC$TL_photo;
        r0.<init>();
        r4 = r58.getConnectionsManager();
        r4 = r4.getCurrentTime();
        r0.date = r4;
        r10 = 0;
        r4 = new byte[r10];
        r0.file_reference = r4;
        r4 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r4.<init>();
        r6 = r15.searchImage;
        r7 = r6.width;
        r4.w = r7;
        r6 = r6.height;
        r4.h = r6;
        r4.size = r10;
        r6 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r6.<init>();
        r4.location = r6;
        r6 = "x";
        r4.type = r6;
        r6 = r0.sizes;
        r6.add(r4);
        goto L_0x047c;
    L_0x047b:
        r10 = 0;
    L_0x047c:
        r6 = r2;
        goto L_0x0480;
    L_0x047e:
        r10 = 0;
        r6 = 1;
    L_0x0480:
        if (r0 == 0) goto L_0x04e5;
    L_0x0482:
        r8 = new java.util.HashMap;
        r8.<init>();
        r2 = r15.searchImage;
        r2 = r2.imageUrl;
        if (r2 == 0) goto L_0x0490;
    L_0x048d:
        r8.put(r3, r2);
    L_0x0490:
        if (r60 == 0) goto L_0x04c6;
    L_0x0492:
        r2 = r22 + 1;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r5);
        r4 = r40;
        r3.append(r4);
        r3 = r3.toString();
        r7 = "groupId";
        r8.put(r7, r3);
        r7 = 10;
        if (r2 == r7) goto L_0x04b8;
    L_0x04ae:
        r3 = r20 + -1;
        r11 = r44;
        if (r11 != r3) goto L_0x04b5;
    L_0x04b4:
        goto L_0x04ba;
    L_0x04b5:
        r22 = r2;
        goto L_0x04ca;
    L_0x04b8:
        r11 = r44;
    L_0x04ba:
        r3 = "final";
        r7 = "1";
        r8.put(r3, r7);
        r22 = r2;
        r23 = r29;
        goto L_0x04ca;
    L_0x04c6:
        r4 = r40;
        r11 = r44;
    L_0x04ca:
        r21 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$fdR8FBFGf7cCJ4bYkfn1GyIA3pQ;
        r2 = r21;
        r3 = r61;
        r46 = r4;
        r4 = r58;
        r5 = r0;
        r7 = r15;
        r15 = r11;
        r10 = r56;
        r31 = r14;
        r14 = r12;
        r12 = r62;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r21);
        goto L_0x04ec;
    L_0x04e5:
        r31 = r14;
        r46 = r40;
        r15 = r44;
        r14 = r12;
    L_0x04ec:
        r21 = r15;
        r0 = r22;
        r2 = r23;
        r5 = r39;
        r4 = r42;
        r36 = r45;
        r32 = r46;
        r25 = 0;
        r34 = 1;
        r35 = 0;
        r54 = r26;
        r26 = r1;
        r1 = r31;
        r31 = r14;
    L_0x0508:
        r14 = r54;
        goto L_0x0af7;
    L_0x050c:
        r42 = r4;
        r46 = r6;
        r1 = r8;
        r45 = r11;
        r20 = r12;
        r39 = r31;
        r6 = 90;
        r7 = 10;
        r29 = 0;
        r12 = r9;
        r31 = r14;
        r14 = r10;
        r0 = r15.isVideo;
        if (r0 == 0) goto L_0x0818;
    L_0x0525:
        if (r59 == 0) goto L_0x0529;
    L_0x0527:
        r0 = 0;
        goto L_0x0534;
    L_0x0529:
        r0 = r15.videoEditedInfo;
        if (r0 == 0) goto L_0x052e;
    L_0x052d:
        goto L_0x0534;
    L_0x052e:
        r0 = r15.path;
        r0 = createCompressionSettings(r0);
    L_0x0534:
        if (r59 != 0) goto L_0x07dd;
    L_0x0536:
        if (r0 != 0) goto L_0x0552;
    L_0x0538:
        r4 = r15.path;
        r2 = r4.endsWith(r2);
        if (r2 == 0) goto L_0x0541;
    L_0x0540:
        goto L_0x0552;
    L_0x0541:
        r50 = r12;
        r13 = r15;
        r33 = r26;
        r51 = r46;
        r35 = 0;
        r26 = r1;
        r1 = r31;
        r31 = r14;
        goto L_0x07ec;
    L_0x0552:
        r8 = r15.path;
        r9 = new java.io.File;
        r9.<init>(r8);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r8);
        r10 = r9.length();
        r2.append(r10);
        r11 = r26;
        r2.append(r11);
        r6 = r9.lastModified();
        r2.append(r6);
        r2 = r2.toString();
        if (r0 == 0) goto L_0x05d2;
    L_0x057a:
        r4 = r0.muted;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r2);
        r7 = r3;
        r2 = r0.estimatedDuration;
        r6.append(r2);
        r6.append(r11);
        r2 = r0.startTime;
        r6.append(r2);
        r6.append(r11);
        r2 = r0.endTime;
        r6.append(r2);
        r2 = r0.muted;
        if (r2 == 0) goto L_0x05a1;
    L_0x059e:
        r2 = "_m";
        goto L_0x05a2;
    L_0x05a1:
        r2 = r5;
    L_0x05a2:
        r6.append(r2);
        r2 = r6.toString();
        r3 = r0.resultWidth;
        r6 = r0.originalWidth;
        if (r3 == r6) goto L_0x05c3;
    L_0x05af:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r3.append(r11);
        r2 = r0.resultWidth;
        r3.append(r2);
        r2 = r3.toString();
    L_0x05c3:
        r6 = r2;
        r2 = r0.startTime;
        r10 = (r2 > r29 ? 1 : (r2 == r29 ? 0 : -1));
        if (r10 < 0) goto L_0x05cb;
    L_0x05ca:
        goto L_0x05cd;
    L_0x05cb:
        r2 = r29;
    L_0x05cd:
        r21 = r4;
        r10 = r6;
        r3 = r2;
        goto L_0x05d8;
    L_0x05d2:
        r7 = r3;
        r10 = r2;
        r3 = r29;
        r21 = 0;
    L_0x05d8:
        if (r14 != 0) goto L_0x0617;
    L_0x05da:
        r2 = r15.ttl;
        if (r2 != 0) goto L_0x0617;
    L_0x05de:
        r2 = r58.getMessagesStorage();
        if (r14 != 0) goto L_0x05e6;
    L_0x05e4:
        r6 = 2;
        goto L_0x05e7;
    L_0x05e6:
        r6 = 5;
    L_0x05e7:
        r2 = r2.getSentFile(r10, r6);
        if (r2 == 0) goto L_0x0617;
    L_0x05ed:
        r6 = 0;
        r26 = r2[r6];
        r26 = (org.telegram.tgnet.TLRPC.TL_document) r26;
        r35 = r5;
        r5 = 1;
        r2 = r2[r5];
        r36 = r2;
        r36 = (java.lang.String) r36;
        r2 = r15.path;
        r37 = 0;
        r38 = r2;
        r2 = r14;
        r40 = r3;
        r3 = r26;
        r4 = r38;
        r38 = r8;
        r48 = r35;
        r8 = 1;
        r5 = r37;
        r49 = r7;
        r6 = r40;
        ensureMediaThumbExists(r2, r3, r4, r5, r6);
        goto L_0x0624;
    L_0x0617:
        r40 = r3;
        r48 = r5;
        r49 = r7;
        r38 = r8;
        r8 = 1;
        r26 = 0;
        r36 = 0;
    L_0x0624:
        if (r26 != 0) goto L_0x06ff;
    L_0x0626:
        r2 = r15.path;
        r3 = r40;
        r2 = createVideoThumbnail(r2, r3);
        if (r2 != 0) goto L_0x0636;
    L_0x0630:
        r2 = r15.path;
        r2 = android.media.ThumbnailUtils.createVideoThumbnail(r2, r8);
    L_0x0636:
        if (r2 == 0) goto L_0x0662;
    L_0x0638:
        if (r14 != 0) goto L_0x064c;
    L_0x063a:
        r3 = r15.ttl;
        if (r3 == 0) goto L_0x063f;
    L_0x063e:
        goto L_0x064c;
    L_0x063f:
        r3 = r2.getWidth();
        r4 = r2.getHeight();
        r5 = java.lang.Math.max(r3, r4);
        goto L_0x064e;
    L_0x064c:
        r5 = 90;
    L_0x064e:
        r3 = (float) r5;
        r4 = 90;
        if (r5 <= r4) goto L_0x0656;
    L_0x0653:
        r4 = 80;
        goto L_0x0658;
    L_0x0656:
        r4 = 55;
    L_0x0658:
        r5 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r4, r14);
        r3 = 0;
        r4 = getKeyForPhotoSize(r5, r3, r8);
        goto L_0x0664;
    L_0x0662:
        r4 = 0;
        r5 = 0;
    L_0x0664:
        r3 = new org.telegram.tgnet.TLRPC$TL_document;
        r3.<init>();
        r7 = 0;
        r6 = new byte[r7];
        r3.file_reference = r6;
        if (r5 == 0) goto L_0x067a;
    L_0x0670:
        r6 = r3.thumbs;
        r6.add(r5);
        r5 = r3.flags;
        r5 = r5 | r8;
        r3.flags = r5;
    L_0x067a:
        r5 = "video/mp4";
        r3.mime_type = r5;
        r5 = r58.getUserConfig();
        r5.saveConfig(r7);
        if (r14 == 0) goto L_0x0697;
    L_0x0687:
        r5 = 66;
        if (r1 < r5) goto L_0x0691;
    L_0x068b:
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r5.<init>();
        goto L_0x069e;
    L_0x0691:
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r5.<init>();
        goto L_0x069e;
    L_0x0697:
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r5.<init>();
        r5.supports_streaming = r8;
    L_0x069e:
        r6 = r3.attributes;
        r6.add(r5);
        if (r0 == 0) goto L_0x06e9;
    L_0x06a5:
        r6 = r0.needConvert();
        if (r6 == 0) goto L_0x06e9;
    L_0x06ab:
        r6 = r0.muted;
        if (r6 == 0) goto L_0x06bd;
    L_0x06af:
        r6 = r15.path;
        fillVideoAttribute(r6, r5, r0);
        r6 = r5.w;
        r0.originalWidth = r6;
        r6 = r5.h;
        r0.originalHeight = r6;
        goto L_0x06c6;
    L_0x06bd:
        r7 = r0.estimatedDuration;
        r40 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 / r40;
        r6 = (int) r7;
        r5.duration = r6;
    L_0x06c6:
        r6 = r0.rotationValue;
        r7 = 90;
        if (r6 == r7) goto L_0x06da;
    L_0x06cc:
        r7 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r6 != r7) goto L_0x06d1;
    L_0x06d0:
        goto L_0x06da;
    L_0x06d1:
        r6 = r0.resultWidth;
        r5.w = r6;
        r6 = r0.resultHeight;
        r5.h = r6;
        goto L_0x06e2;
    L_0x06da:
        r6 = r0.resultHeight;
        r5.w = r6;
        r6 = r0.resultWidth;
        r5.h = r6;
    L_0x06e2:
        r5 = r0.estimatedSize;
        r6 = (int) r5;
        r3.size = r6;
        r9 = 0;
        goto L_0x06fc;
    L_0x06e9:
        r6 = r9.exists();
        if (r6 == 0) goto L_0x06f6;
    L_0x06ef:
        r6 = r9.length();
        r7 = (int) r6;
        r3.size = r7;
    L_0x06f6:
        r6 = r15.path;
        r9 = 0;
        fillVideoAttribute(r6, r5, r9);
    L_0x06fc:
        r8 = r3;
        r3 = r2;
        goto L_0x0704;
    L_0x06ff:
        r9 = 0;
        r3 = r9;
        r4 = r3;
        r8 = r26;
    L_0x0704:
        if (r0 == 0) goto L_0x072f;
    L_0x0706:
        r2 = r0.muted;
        if (r2 == 0) goto L_0x072f;
    L_0x070a:
        r2 = r8.attributes;
        r2 = r2.size();
        r5 = 0;
    L_0x0711:
        if (r5 >= r2) goto L_0x0722;
    L_0x0713:
        r6 = r8.attributes;
        r6 = r6.get(r5);
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
        if (r6 == 0) goto L_0x071f;
    L_0x071d:
        r2 = 1;
        goto L_0x0723;
    L_0x071f:
        r5 = r5 + 1;
        goto L_0x0711;
    L_0x0722:
        r2 = 0;
    L_0x0723:
        if (r2 != 0) goto L_0x072f;
    L_0x0725:
        r2 = r8.attributes;
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r5.<init>();
        r2.add(r5);
    L_0x072f:
        if (r0 == 0) goto L_0x0763;
    L_0x0731:
        r2 = r0.needConvert();
        if (r2 == 0) goto L_0x0763;
    L_0x0737:
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
        r38 = r2;
    L_0x0763:
        r7 = new java.util.HashMap;
        r7.<init>();
        if (r10 == 0) goto L_0x076f;
    L_0x076a:
        r6 = r49;
        r7.put(r6, r10);
    L_0x076f:
        if (r21 != 0) goto L_0x07a9;
    L_0x0771:
        if (r60 == 0) goto L_0x07a9;
    L_0x0773:
        r2 = r22 + 1;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r10 = r48;
        r5.append(r10);
        r43 = r14;
        r21 = r15;
        r14 = r46;
        r5.append(r14);
        r5 = r5.toString();
        r6 = "groupId";
        r7.put(r6, r5);
        r5 = 10;
        if (r2 == r5) goto L_0x079d;
    L_0x0795:
        r5 = r20 + -1;
        if (r12 != r5) goto L_0x079a;
    L_0x0799:
        goto L_0x079d;
    L_0x079a:
        r22 = r2;
        goto L_0x07af;
    L_0x079d:
        r5 = "final";
        r6 = "1";
        r7.put(r5, r6);
        r22 = r2;
        r23 = r29;
        goto L_0x07af;
    L_0x07a9:
        r43 = r14;
        r21 = r15;
        r14 = r46;
    L_0x07af:
        r25 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$6NykxaLeilFJIVPFtZmyrAcnfU8;
        r2 = r25;
        r5 = r61;
        r6 = r58;
        r26 = r1;
        r10 = r7;
        r1 = 0;
        r7 = r0;
        r32 = r9;
        r9 = r38;
        r1 = r11;
        r11 = r36;
        r50 = r12;
        r12 = r56;
        r51 = r14;
        r15 = r31;
        r31 = r43;
        r14 = r62;
        r33 = r1;
        r1 = r15;
        r35 = r32;
        r15 = r21;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r25);
        goto L_0x0802;
    L_0x07dd:
        r50 = r12;
        r33 = r26;
        r51 = r46;
        r35 = 0;
        r26 = r1;
        r1 = r31;
        r31 = r14;
        r13 = r15;
    L_0x07ec:
        r4 = r13.path;
        r5 = 0;
        r6 = 0;
        r10 = r13.caption;
        r11 = r13.entities;
        r2 = r58;
        r3 = r4;
        r7 = r56;
        r9 = r62;
        r12 = r61;
        r13 = r59;
        prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13);
    L_0x0802:
        r0 = r22;
        r2 = r23;
        r14 = r33;
        r5 = r39;
        r4 = r42;
    L_0x080c:
        r36 = r45;
        r21 = r50;
        r32 = r51;
        r25 = 0;
        r34 = 1;
        goto L_0x0af7;
    L_0x0818:
        r6 = r3;
        r10 = r5;
        r50 = r12;
        r13 = r15;
        r33 = r26;
        r51 = r46;
        r35 = 0;
        r26 = r1;
        r1 = r31;
        r31 = r14;
        r0 = r13.path;
        if (r0 != 0) goto L_0x083c;
    L_0x082d:
        r2 = r13.uri;
        if (r2 == 0) goto L_0x083c;
    L_0x0831:
        r0 = org.telegram.messenger.AndroidUtilities.getPath(r2);
        r2 = r13.uri;
        r2 = r2.toString();
        goto L_0x083d;
    L_0x083c:
        r2 = r0;
    L_0x083d:
        if (r59 != 0) goto L_0x08a1;
    L_0x083f:
        r3 = r13.path;
        r4 = r13.uri;
        r3 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r3, r4);
        if (r3 == 0) goto L_0x084a;
    L_0x0849:
        goto L_0x08a1;
    L_0x084a:
        if (r0 == 0) goto L_0x0868;
    L_0x084c:
        r3 = r0.endsWith(r1);
        if (r3 != 0) goto L_0x085a;
    L_0x0852:
        r3 = ".webp";
        r3 = r0.endsWith(r3);
        if (r3 == 0) goto L_0x0868;
    L_0x085a:
        r3 = r0.endsWith(r1);
        if (r3 == 0) goto L_0x0863;
    L_0x0860:
        r3 = "gif";
        goto L_0x0865;
    L_0x0863:
        r3 = "webp";
    L_0x0865:
        r19 = r3;
        goto L_0x08af;
    L_0x0868:
        if (r0 != 0) goto L_0x089e;
    L_0x086a:
        r3 = r13.uri;
        if (r3 == 0) goto L_0x089e;
    L_0x086e:
        r3 = org.telegram.messenger.MediaController.isGif(r3);
        if (r3 == 0) goto L_0x0885;
    L_0x0874:
        r0 = r13.uri;
        r2 = r0.toString();
        r0 = r13.uri;
        r3 = "gif";
        r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3);
        r19 = "gif";
        goto L_0x08af;
    L_0x0885:
        r3 = r13.uri;
        r3 = org.telegram.messenger.MediaController.isWebp(r3);
        if (r3 == 0) goto L_0x089e;
    L_0x088d:
        r0 = r13.uri;
        r2 = r0.toString();
        r0 = r13.uri;
        r3 = "webp";
        r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3);
        r19 = "webp";
        goto L_0x08af;
    L_0x089e:
        r8 = r0;
        r0 = 0;
        goto L_0x08b1;
    L_0x08a1:
        if (r0 == 0) goto L_0x08ad;
    L_0x08a3:
        r3 = new java.io.File;
        r3.<init>(r0);
        r3 = org.telegram.messenger.FileLoader.getFileExtension(r3);
        goto L_0x0865;
    L_0x08ad:
        r19 = r10;
    L_0x08af:
        r8 = r0;
        r0 = 1;
    L_0x08b1:
        if (r0 == 0) goto L_0x08f4;
    L_0x08b3:
        r15 = r39;
        if (r15 != 0) goto L_0x08d1;
    L_0x08b7:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r27 = new java.util.ArrayList;
        r27.<init>();
        r28 = new java.util.ArrayList;
        r28.<init>();
        r15 = r5;
        r0 = r27;
        r3 = r28;
        goto L_0x08d7;
    L_0x08d1:
        r0 = r27;
        r3 = r28;
        r4 = r42;
    L_0x08d7:
        r15.add(r8);
        r4.add(r2);
        r2 = r13.caption;
        r0.add(r2);
        r2 = r13.entities;
        r3.add(r2);
        r27 = r0;
        r28 = r3;
        r5 = r15;
        r0 = r22;
        r2 = r23;
        r14 = r33;
        goto L_0x080c;
    L_0x08f4:
        r15 = r39;
        if (r8 == 0) goto L_0x091e;
    L_0x08f8:
        r0 = new java.io.File;
        r0.<init>(r8);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r4 = r0.length();
        r3.append(r4);
        r14 = r33;
        r3.append(r14);
        r4 = r0.lastModified();
        r3.append(r4);
        r5 = r3.toString();
        r9 = r5;
        goto L_0x0922;
    L_0x091e:
        r14 = r33;
        r9 = r35;
    L_0x0922:
        r12 = r45;
        if (r12 == 0) goto L_0x0949;
    L_0x0926:
        r0 = r12.get(r13);
        r2 = r0;
        r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2;
        r0 = r2.photo;
        r3 = r2.parentObject;
        if (r0 != 0) goto L_0x0941;
    L_0x0933:
        r0 = r2.sync;	 Catch:{ Exception -> 0x0939 }
        r0.await();	 Catch:{ Exception -> 0x0939 }
        goto L_0x093d;
    L_0x0939:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x093d:
        r0 = r2.photo;
        r3 = r2.parentObject;
    L_0x0941:
        r7 = r0;
        r21 = r3;
        r53 = r6;
        r11 = 1;
        goto L_0x09cf;
    L_0x0949:
        if (r31 != 0) goto L_0x09a9;
    L_0x094b:
        r0 = r13.ttl;
        if (r0 != 0) goto L_0x09a9;
    L_0x094f:
        r0 = r58.getMessagesStorage();
        if (r31 != 0) goto L_0x0957;
    L_0x0955:
        r2 = 0;
        goto L_0x0958;
    L_0x0957:
        r2 = 3;
    L_0x0958:
        r0 = r0.getSentFile(r9, r2);
        if (r0 == 0) goto L_0x096a;
    L_0x095e:
        r2 = 0;
        r3 = r0[r2];
        r5 = r3;
        r5 = (org.telegram.tgnet.TLRPC.TL_photo) r5;
        r11 = 1;
        r0 = r0[r11];
        r0 = (java.lang.String) r0;
        goto L_0x096e;
    L_0x096a:
        r11 = 1;
        r0 = r35;
        r5 = r0;
    L_0x096e:
        if (r5 != 0) goto L_0x0995;
    L_0x0970:
        r2 = r13.uri;
        if (r2 == 0) goto L_0x0995;
    L_0x0974:
        r2 = r58.getMessagesStorage();
        r3 = r13.uri;
        r3 = org.telegram.messenger.AndroidUtilities.getPath(r3);
        if (r31 != 0) goto L_0x0982;
    L_0x0980:
        r4 = 0;
        goto L_0x0983;
    L_0x0982:
        r4 = 3;
    L_0x0983:
        r2 = r2.getSentFile(r3, r4);
        if (r2 == 0) goto L_0x0995;
    L_0x0989:
        r3 = 0;
        r0 = r2[r3];
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        r2 = r2[r11];
        r2 = (java.lang.String) r2;
        r21 = r2;
        goto L_0x0998;
    L_0x0995:
        r21 = r0;
        r0 = r5;
    L_0x0998:
        r4 = r13.path;
        r5 = r13.uri;
        r36 = 0;
        r2 = r31;
        r3 = r0;
        r53 = r6;
        r6 = r36;
        ensureMediaThumbExists(r2, r3, r4, r5, r6);
        goto L_0x09b0;
    L_0x09a9:
        r53 = r6;
        r11 = 1;
        r0 = r35;
        r21 = r0;
    L_0x09b0:
        if (r0 != 0) goto L_0x09ce;
    L_0x09b2:
        r0 = r58.getSendMessagesHelper();
        r2 = r13.path;
        r3 = r13.uri;
        r0 = r0.generatePhotoSizes(r2, r3);
        if (r31 == 0) goto L_0x09ce;
    L_0x09c0:
        r2 = r13.canDeleteAfter;
        if (r2 == 0) goto L_0x09ce;
    L_0x09c4:
        r2 = new java.io.File;
        r3 = r13.path;
        r2.<init>(r3);
        r2.delete();
    L_0x09ce:
        r7 = r0;
    L_0x09cf:
        if (r7 == 0) goto L_0x0ab2;
    L_0x09d1:
        r8 = new java.util.HashMap;
        r8.<init>();
        r3 = new android.graphics.Bitmap[r11];
        r4 = new java.lang.String[r11];
        r0 = r13.masks;
        if (r0 == 0) goto L_0x09e6;
    L_0x09de:
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x09e6;
    L_0x09e4:
        r0 = 1;
        goto L_0x09e7;
    L_0x09e6:
        r0 = 0;
    L_0x09e7:
        r7.has_stickers = r0;
        if (r0 == 0) goto L_0x0a2a;
    L_0x09eb:
        r0 = new org.telegram.tgnet.SerializedData;
        r2 = r13.masks;
        r2 = r2.size();
        r2 = r2 * 20;
        r2 = r2 + 4;
        r0.<init>(r2);
        r2 = r13.masks;
        r2 = r2.size();
        r0.writeInt32(r2);
        r2 = 0;
    L_0x0a04:
        r5 = r13.masks;
        r5 = r5.size();
        if (r2 >= r5) goto L_0x0a1a;
    L_0x0a0c:
        r5 = r13.masks;
        r5 = r5.get(r2);
        r5 = (org.telegram.tgnet.TLRPC.InputDocument) r5;
        r5.serializeToStream(r0);
        r2 = r2 + 1;
        goto L_0x0a04;
    L_0x0a1a:
        r2 = r0.toByteArray();
        r2 = org.telegram.messenger.Utilities.bytesToHex(r2);
        r5 = "masks";
        r8.put(r5, r2);
        r0.cleanup();
    L_0x0a2a:
        if (r9 == 0) goto L_0x0a31;
    L_0x0a2c:
        r2 = r53;
        r8.put(r2, r9);
    L_0x0a31:
        if (r60 == 0) goto L_0x0a3c;
    L_0x0a33:
        r0 = r55.size();	 Catch:{ Exception -> 0x0a52 }
        if (r0 != r11) goto L_0x0a3a;
    L_0x0a39:
        goto L_0x0a3c;
    L_0x0a3a:
        r9 = 0;
        goto L_0x0a57;
    L_0x0a3c:
        r0 = r7.sizes;	 Catch:{ Exception -> 0x0a52 }
        r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0a52 }
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2);	 Catch:{ Exception -> 0x0a52 }
        if (r0 == 0) goto L_0x0a3a;
    L_0x0a48:
        r9 = 0;
        r0 = getKeyForPhotoSize(r0, r3, r9);	 Catch:{ Exception -> 0x0a50 }
        r4[r9] = r0;	 Catch:{ Exception -> 0x0a50 }
        goto L_0x0a57;
    L_0x0a50:
        r0 = move-exception;
        goto L_0x0a54;
    L_0x0a52:
        r0 = move-exception;
        r9 = 0;
    L_0x0a54:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0a57:
        if (r60 == 0) goto L_0x0a88;
    L_0x0a59:
        r0 = r22 + 1;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r10);
        r5 = r51;
        r2.append(r5);
        r2 = r2.toString();
        r10 = "groupId";
        r8.put(r10, r2);
        r2 = 10;
        if (r0 == r2) goto L_0x0a7c;
    L_0x0a75:
        r2 = r20 + -1;
        r10 = r50;
        if (r10 != r2) goto L_0x0a8e;
    L_0x0a7b:
        goto L_0x0a7e;
    L_0x0a7c:
        r10 = r50;
    L_0x0a7e:
        r2 = "final";
        r9 = "1";
        r8.put(r2, r9);
        r23 = r29;
        goto L_0x0a8e;
    L_0x0a88:
        r10 = r50;
        r5 = r51;
        r0 = r22;
    L_0x0a8e:
        r22 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$-ub-T3xUMNzwYnSdFhOd8tT6H1A;
        r2 = r22;
        r32 = r5;
        r5 = r61;
        r6 = r58;
        r25 = 0;
        r9 = r21;
        r21 = r10;
        r34 = 1;
        r10 = r56;
        r36 = r12;
        r12 = r62;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r22);
        r5 = r15;
        r2 = r23;
        r4 = r42;
        goto L_0x0af7;
    L_0x0ab2:
        r36 = r12;
        r21 = r50;
        r32 = r51;
        r25 = 0;
        r34 = 1;
        if (r15 != 0) goto L_0x0ad8;
    L_0x0abe:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r27 = new java.util.ArrayList;
        r27.<init>();
        r28 = new java.util.ArrayList;
        r28.<init>();
        r15 = r5;
        r0 = r27;
        r2 = r28;
        goto L_0x0ade;
    L_0x0ad8:
        r0 = r27;
        r2 = r28;
        r4 = r42;
    L_0x0ade:
        r15.add(r8);
        r4.add(r9);
        r3 = r13.caption;
        r0.add(r3);
        r3 = r13.entities;
        r2.add(r3);
        r27 = r0;
        r28 = r2;
        r5 = r15;
        r0 = r22;
        r2 = r23;
    L_0x0af7:
        r9 = r21 + 1;
        r13 = r58;
        r12 = r20;
        r8 = r26;
        r10 = r31;
        r22 = r32;
        r15 = r35;
        r11 = r36;
        r26 = r14;
        r14 = r1;
        r1 = r55;
        goto L_0x0180;
    L_0x0b0e:
        r42 = r4;
        r15 = r5;
        r25 = 0;
        r29 = 0;
        r0 = (r2 > r29 ? 1 : (r2 == r29 ? 0 : -1));
        if (r0 == 0) goto L_0x0b24;
    L_0x0b19:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$uFl-kWwxT_GWKCdwcg2qOyXVjzE;
        r13 = r58;
        r0.<init>(r13, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        goto L_0x0b26;
    L_0x0b24:
        r13 = r58;
    L_0x0b26:
        if (r63 == 0) goto L_0x0b2b;
    L_0x0b28:
        r63.releasePermission();
    L_0x0b2b:
        if (r15 == 0) goto L_0x0b6f;
    L_0x0b2d:
        r0 = r15.isEmpty();
        if (r0 != 0) goto L_0x0b6f;
    L_0x0b33:
        r0 = 0;
    L_0x0b34:
        r1 = r15.size();
        if (r0 >= r1) goto L_0x0b6f;
    L_0x0b3a:
        r1 = r15.get(r0);
        r2 = r1;
        r2 = (java.lang.String) r2;
        r14 = r42;
        r1 = r14.get(r0);
        r3 = r1;
        r3 = (java.lang.String) r3;
        r4 = 0;
        r12 = r27;
        r1 = r12.get(r0);
        r9 = r1;
        r9 = (java.lang.CharSequence) r9;
        r11 = r28;
        r1 = r11.get(r0);
        r10 = r1;
        r10 = (java.util.ArrayList) r10;
        r1 = r58;
        r5 = r19;
        r6 = r56;
        r8 = r62;
        r11 = r61;
        r12 = r59;
        prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12);
        r0 = r0 + 1;
        goto L_0x0b34;
    L_0x0b6f:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0b8d;
    L_0x0b73:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "total send time = ";
        r0.append(r1);
        r1 = java.lang.System.currentTimeMillis();
        r1 = r1 - r16;
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0b8d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$60(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, boolean, boolean, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    static /* synthetic */ void lambda$null$54(MediaSendPrepareWorker mediaSendPrepareWorker, AccountInstance accountInstance, SendingMediaInfo sendingMediaInfo, boolean z) {
        mediaSendPrepareWorker.photo = accountInstance.getSendMessagesHelper().generatePhotoSizes(sendingMediaInfo.path, sendingMediaInfo.uri);
        if (z && sendingMediaInfo.canDeleteAfter) {
            new File(sendingMediaInfo.path).delete();
        }
        mediaSendPrepareWorker.sync.countDown();
    }

    static /* synthetic */ void lambda$null$55(MessageObject messageObject, AccountInstance accountInstance, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, null, tL_document, str, hashMap, false, str2);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, null, str, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, 0, str2);
    }

    static /* synthetic */ void lambda$null$56(MessageObject messageObject, AccountInstance accountInstance, TL_photo tL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2) {
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
        sendMessagesHelper2.sendMessage(tL_photo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$57(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
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
        accountInstance.getSendMessagesHelper().sendMessage(tL_document, videoEditedInfo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, sendingMediaInfo2.ttl, str3);
    }

    static /* synthetic */ void lambda$null$58(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TL_photo tL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, tL_photo, null, null, null, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tL_photo, null, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$59(AccountInstance accountInstance, long j) {
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
            accountInstance.getMessagesStorage().putMessages(tL_messages_messages, delayedMessage.peer, -2, 0, false);
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0017 */
    private static android.graphics.Bitmap createVideoThumbnail(java.lang.String r2, long r3) {
        /*
        r0 = new android.media.MediaMetadataRetriever;
        r0.<init>();
        r1 = 0;
        r0.setDataSource(r2);	 Catch:{ Exception -> 0x0017, all -> 0x0012 }
        r2 = 1;
        r2 = r0.getFrameAtTime(r3, r2);	 Catch:{ Exception -> 0x0017, all -> 0x0012 }
        r0.release();	 Catch:{ RuntimeException -> 0x001b }
        goto L_0x001b;
    L_0x0012:
        r2 = move-exception;
        r0.release();	 Catch:{ RuntimeException -> 0x0016 }
    L_0x0016:
        throw r2;
    L_0x0017:
        r0.release();	 Catch:{ RuntimeException -> 0x001a }
    L_0x001a:
        r2 = r1;
    L_0x001b:
        if (r2 != 0) goto L_0x001e;
    L_0x001d:
        return r1;
    L_0x001e:
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

    public static void prepareSendingVideo(AccountInstance accountInstance, String str, long j, long j2, int i, int i2, VideoEditedInfo videoEditedInfo, long j3, MessageObject messageObject, CharSequence charSequence, ArrayList<MessageEntity> arrayList, int i3, MessageObject messageObject2) {
        if (str != null && str.length() != 0) {
            -$$Lambda$SendMessagesHelper$Z7xR6s7dmJd5tx9YQ_vwMv4VOvU -__lambda_sendmessageshelper_z7xr6s7dmjd5tx9yq_vwmv4vovu = r0;
            -$$Lambda$SendMessagesHelper$Z7xR6s7dmJd5tx9YQ_vwMv4VOvU -__lambda_sendmessageshelper_z7xr6s7dmjd5tx9yq_vwmv4vovu2 = new -$$Lambda$SendMessagesHelper$Z7xR6s7dmJd5tx9YQ_vwMv4VOvU(videoEditedInfo, str, j3, j2, i3, accountInstance, i2, i, j, charSequence, messageObject2, messageObject, arrayList);
            new Thread(-__lambda_sendmessageshelper_z7xr6s7dmjd5tx9yq_vwmv4vovu).start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:79:0x0219  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0260  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0236  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x02ba  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x02ca  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0311  */
    static /* synthetic */ void lambda$prepareSendingVideo$62(org.telegram.messenger.VideoEditedInfo r30, java.lang.String r31, long r32, long r34, int r36, org.telegram.messenger.AccountInstance r37, int r38, int r39, long r40, java.lang.CharSequence r42, org.telegram.messenger.MessageObject r43, org.telegram.messenger.MessageObject r44, java.util.ArrayList r45) {
        /*
        r6 = r31;
        r10 = r32;
        r7 = r34;
        r9 = r38;
        r12 = r39;
        if (r30 == 0) goto L_0x000f;
    L_0x000c:
        r13 = r30;
        goto L_0x0014;
    L_0x000f:
        r0 = createCompressionSettings(r31);
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
        if (r13 != 0) goto L_0x0049;
    L_0x0025:
        r0 = "mp4";
        r0 = r6.endsWith(r0);
        if (r0 != 0) goto L_0x0049;
    L_0x002d:
        if (r5 == 0) goto L_0x0030;
    L_0x002f:
        goto L_0x0049;
    L_0x0030:
        r3 = 0;
        r4 = 0;
        r12 = 0;
        r0 = r37;
        r1 = r31;
        r2 = r31;
        r5 = r32;
        r7 = r44;
        r8 = r42;
        r9 = r45;
        r10 = r43;
        r11 = r12;
        prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11);
        goto L_0x0335;
    L_0x0049:
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
        if (r13 == 0) goto L_0x00c8;
    L_0x0073:
        if (r5 != 0) goto L_0x00bd;
    L_0x0075:
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
        if (r0 == 0) goto L_0x0099;
    L_0x0096:
        r14 = "_m";
        goto L_0x009b;
    L_0x0099:
        r14 = r19;
    L_0x009b:
        r1.append(r14);
        r0 = r1.toString();
        r1 = r13.resultWidth;
        r14 = r13.originalWidth;
        if (r1 == r14) goto L_0x00bf;
    L_0x00a8:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r1.append(r2);
        r0 = r13.resultWidth;
        r1.append(r0);
        r0 = r1.toString();
        goto L_0x00bf;
    L_0x00bd:
        r19 = r14;
    L_0x00bf:
        r14 = r13.startTime;
        r1 = (r14 > r17 ? 1 : (r14 == r17 ? 0 : -1));
        if (r1 < 0) goto L_0x00ca;
    L_0x00c5:
        r17 = r14;
        goto L_0x00ca;
    L_0x00c8:
        r19 = r14;
    L_0x00ca:
        r14 = r0;
        r0 = r17;
        if (r4 != 0) goto L_0x0114;
    L_0x00cf:
        if (r36 != 0) goto L_0x0114;
    L_0x00d1:
        r15 = r37.getMessagesStorage();
        if (r4 != 0) goto L_0x00db;
    L_0x00d7:
        r21 = r0;
        r0 = 2;
        goto L_0x00e0;
    L_0x00db:
        r20 = 5;
        r21 = r0;
        r0 = 5;
    L_0x00e0:
        r0 = r15.getSentFile(r14, r0);
        if (r0 == 0) goto L_0x010b;
    L_0x00e6:
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
        r2 = r31;
        r22 = r3;
        r3 = r23;
        r9 = r5;
        r23 = r14;
        r14 = r4;
        r4 = r24;
        ensureMediaThumbExists(r0, r1, r2, r3, r4);
        goto L_0x0121;
    L_0x010b:
        r15 = r2;
        r9 = r5;
        r23 = r14;
        r24 = r21;
        r22 = r3;
        goto L_0x011c;
    L_0x0114:
        r24 = r0;
        r15 = r2;
        r22 = r3;
        r9 = r5;
        r23 = r14;
    L_0x011c:
        r14 = r4;
        r20 = 0;
        r21 = 0;
    L_0x0121:
        if (r21 != 0) goto L_0x02ca;
    L_0x0123:
        r0 = r24;
        r0 = createVideoThumbnail(r6, r0);
        if (r0 != 0) goto L_0x0130;
    L_0x012b:
        r1 = 1;
        r0 = android.media.ThumbnailUtils.createVideoThumbnail(r6, r1);
    L_0x0130:
        r1 = 90;
        if (r14 != 0) goto L_0x013a;
    L_0x0134:
        if (r36 == 0) goto L_0x0137;
    L_0x0136:
        goto L_0x013a;
    L_0x0137:
        r2 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x013c;
    L_0x013a:
        r2 = 90;
    L_0x013c:
        r3 = (float) r2;
        if (r2 <= r1) goto L_0x0142;
    L_0x013f:
        r2 = 80;
        goto L_0x0144;
    L_0x0142:
        r2 = 55;
    L_0x0144:
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r14);
        if (r0 == 0) goto L_0x0211;
    L_0x014a:
        if (r2 == 0) goto L_0x0211;
    L_0x014c:
        if (r9 == 0) goto L_0x0210;
    L_0x014e:
        r3 = 21;
        if (r14 == 0) goto L_0x01b1;
    L_0x0152:
        r25 = 7;
        r4 = android.os.Build.VERSION.SDK_INT;
        if (r4 >= r3) goto L_0x015b;
    L_0x0158:
        r26 = 0;
        goto L_0x015d;
    L_0x015b:
        r26 = 1;
    L_0x015d:
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
        goto L_0x0212;
    L_0x01b1:
        r25 = 3;
        r4 = android.os.Build.VERSION.SDK_INT;
        if (r4 >= r3) goto L_0x01ba;
    L_0x01b7:
        r26 = 0;
        goto L_0x01bc;
    L_0x01ba:
        r26 = 1;
    L_0x01bc:
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
        goto L_0x0212;
    L_0x0210:
        r0 = 0;
    L_0x0211:
        r15 = 0;
    L_0x0212:
        r3 = new org.telegram.tgnet.TLRPC$TL_document;
        r3.<init>();
        if (r2 == 0) goto L_0x0224;
    L_0x0219:
        r4 = r3.thumbs;
        r4.add(r2);
        r2 = r3.flags;
        r4 = 1;
        r2 = r2 | r4;
        r3.flags = r2;
    L_0x0224:
        r2 = 0;
        r4 = new byte[r2];
        r3.file_reference = r4;
        r4 = "video/mp4";
        r3.mime_type = r4;
        r4 = r37.getUserConfig();
        r4.saveConfig(r2);
        if (r14 == 0) goto L_0x0260;
    L_0x0236:
        r2 = 32;
        r4 = r10 >> r2;
        r2 = (int) r4;
        r4 = r37.getMessagesController();
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r4.getEncryptedChat(r2);
        if (r2 != 0) goto L_0x024a;
    L_0x0249:
        return;
    L_0x024a:
        r2 = r2.layer;
        r2 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r2);
        r4 = 66;
        if (r2 < r4) goto L_0x025a;
    L_0x0254:
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r2.<init>();
        goto L_0x0268;
    L_0x025a:
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r2.<init>();
        goto L_0x0268;
    L_0x0260:
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r2.<init>();
        r4 = 1;
        r2.supports_streaming = r4;
    L_0x0268:
        r2.round_message = r9;
        r4 = r3.attributes;
        r4.add(r2);
        if (r13 == 0) goto L_0x02b4;
    L_0x0271:
        r4 = r13.needConvert();
        if (r4 == 0) goto L_0x02b4;
    L_0x0277:
        r4 = r13.muted;
        if (r4 == 0) goto L_0x0291;
    L_0x027b:
        r4 = r3.attributes;
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r5.<init>();
        r4.add(r5);
        fillVideoAttribute(r6, r2, r13);
        r4 = r2.w;
        r13.originalWidth = r4;
        r4 = r2.h;
        r13.originalHeight = r4;
        goto L_0x0298;
    L_0x0291:
        r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r4 = r7 / r4;
        r5 = (int) r4;
        r2.duration = r5;
    L_0x0298:
        r4 = r13.rotationValue;
        if (r4 == r1) goto L_0x02a8;
    L_0x029c:
        r1 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r4 != r1) goto L_0x02a1;
    L_0x02a0:
        goto L_0x02a8;
    L_0x02a1:
        r2.w = r12;
        r1 = r38;
        r2.h = r1;
        goto L_0x02ae;
    L_0x02a8:
        r1 = r38;
        r2.w = r1;
        r2.h = r12;
    L_0x02ae:
        r1 = r40;
        r2 = (int) r1;
        r3.size = r2;
        goto L_0x02c5;
    L_0x02b4:
        r1 = r22.exists();
        if (r1 == 0) goto L_0x02c1;
    L_0x02ba:
        r4 = r22.length();
        r1 = (int) r4;
        r3.size = r1;
    L_0x02c1:
        r1 = 0;
        fillVideoAttribute(r6, r2, r1);
    L_0x02c5:
        r1 = r0;
        r21 = r3;
        r2 = r15;
        goto L_0x02cc;
    L_0x02ca:
        r1 = 0;
        r2 = r1;
    L_0x02cc:
        if (r13 == 0) goto L_0x0301;
    L_0x02ce:
        r0 = r13.needConvert();
        if (r0 == 0) goto L_0x0301;
    L_0x02d4:
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
        goto L_0x0302;
    L_0x0301:
        r7 = r6;
    L_0x0302:
        r8 = new java.util.HashMap;
        r8.<init>();
        if (r42 == 0) goto L_0x030f;
    L_0x0309:
        r0 = r42.toString();
        r19 = r0;
    L_0x030f:
        if (r23 == 0) goto L_0x0318;
    L_0x0311:
        r0 = "originalPath";
        r3 = r23;
        r8.put(r0, r3);
    L_0x0318:
        r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$AkATnH5EPUEZtWg83y6smDnAmSA;
        r0 = r16;
        r3 = r43;
        r4 = r37;
        r5 = r13;
        r6 = r21;
        r9 = r20;
        r10 = r32;
        r12 = r44;
        r13 = r19;
        r14 = r45;
        r15 = r36;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);
    L_0x0335:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$62(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, long, int, org.telegram.messenger.AccountInstance, int, int, long, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList):void");
    }

    static /* synthetic */ void lambda$null$61(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, String str4, ArrayList arrayList, int i) {
        Bitmap bitmap2 = bitmap;
        String str5 = str;
        if (!(bitmap2 == null || str5 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str5);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessageMedia(messageObject, null, videoEditedInfo, tL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tL_document, videoEditedInfo, str2, j, messageObject2, str4, arrayList, null, hashMap, i, str3);
        }
    }
}
