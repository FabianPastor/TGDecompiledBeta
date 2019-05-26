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
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.tgnet.TLRPC.TL_payments_getPaymentForm;
import org.telegram.tgnet.TLRPC.TL_payments_getPaymentReceipt;
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

public class SendMessagesHelper implements NotificationCenterDelegate {
    private static volatile SendMessagesHelper[] Instance = new SendMessagesHelper[3];
    private static DispatchQueue mediaSendQueue = new DispatchQueue("mediaSendQueue");
    private static ThreadPoolExecutor mediaSendThreadPool;
    private int currentAccount;
    private ChatFull currentChatInfo = null;
    private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap();
    private LocationProvider locationProvider = new LocationProvider(new LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            SendMessagesHelper.this.sendLocation(location);
            SendMessagesHelper.this.waitingForLocation.clear();
        }

        public void onUnableLocationAcquire() {
            HashMap hashMap = new HashMap(SendMessagesHelper.this.waitingForLocation);
            NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, hashMap);
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
                            SecretChatHelper.getInstance(SendMessagesHelper.this.currentAccount).performSendEncryptedRequest((TL_messages_sendEncryptedMultiMedia) delayedMessageSendAfterRequest.request, this);
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
                    MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(messageObject.messageOwner);
                    messageObject.messageOwner.send_state = 2;
                    NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                    SendMessagesHelper.this.processSentMessage(messageObject.getId());
                }
                HashMap access$900 = SendMessagesHelper.this.delayedMessages;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("group_");
                stringBuilder.append(this.groupId);
                access$900.remove(stringBuilder.toString());
            } else {
                MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(this.obj.messageOwner);
                this.obj.messageOwner.send_state = 2;
                NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(this.obj.getId()));
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
        this.currentAccount = i;
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$F3OpGpLNH47E9LDuBXXNWIdgYDE(this));
    }

    public /* synthetic */ void lambda$new$0$SendMessagesHelper() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingStarted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailedLoad);
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
                                        SecretChatHelper instance = SecretChatHelper.getInstance(this.currentAccount);
                                        messageObject3 = delayedMessage.obj;
                                        instance.performSendEncryptedRequest(decryptedMessage, messageObject3.messageOwner, delayedMessage.encryptedChat, inputEncryptedFile2, delayedMessage.originalPath, messageObject3);
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
                FileLoader.getInstance(this.currentAccount).checkUploadNewDataAvailable(str5, ((int) messageObject.getDialogId()) == 0, longValue, longValue2);
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
                                        MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList7, false, true, false, 0);
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
                                    MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList8, false, true, false, 0);
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
            MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, false, true, false, 0);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, messageObject.messageOwner);
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
        MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, false, true, false, 0);
        delayedMessage.performMediaUpload = true;
        performSendDelayedMessage(delayedMessage);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, delayedMessage.obj.messageOwner);
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
        MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, false, true, false, 0);
        new ArrayList().add(messageObject);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), r1);
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
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(removeFromSendingMessages.reqId, true);
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
                                    MessagesStorage.getInstance(this.currentAccount).putMessages(tL_messages_messages, delayedMessage.peer, -2, 0, false);
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
                FileLoader.getInstance(this.currentAccount).cancelUploadFile(str2, z);
            }
            stopVideoService(str2);
            this.delayedMessages.remove(str2);
        }
        if (arrayList.size() == 1 && ((MessageObject) arrayList2.get(0)).isEditing() && ((MessageObject) arrayList2.get(0)).previousMedia != null) {
            revertEditingMessageObject((MessageObject) arrayList2.get(0));
        } else {
            MessagesController.getInstance(this.currentAccount).deleteMessages(arrayList4, null, null, i3, false);
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
            EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(messageObject.messageOwner);
                messageObject.messageOwner.send_state = 2;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            Message message = messageObject.messageOwner;
            if (message.random_id == 0) {
                message.random_id = getNextRandomId();
            }
            DecryptedMessageAction decryptedMessageAction = messageObject.messageOwner.action.encryptedAction;
            if (decryptedMessageAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                SecretChatHelper.getInstance(this.currentAccount).sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionDeleteMessages) {
                SecretChatHelper.getInstance(this.currentAccount).sendMessagesDeleteMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionFlushHistory) {
                SecretChatHelper.getInstance(this.currentAccount).sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionNotifyLayer) {
                SecretChatHelper.getInstance(this.currentAccount).sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionReadMessages) {
                SecretChatHelper.getInstance(this.currentAccount).sendMessagesReadMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (decryptedMessageAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                SecretChatHelper.getInstance(this.currentAccount).sendScreenshotMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (!((decryptedMessageAction instanceof TL_decryptedMessageActionTyping) || (decryptedMessageAction instanceof TL_decryptedMessageActionResend))) {
                if (decryptedMessageAction instanceof TL_decryptedMessageActionCommitKey) {
                    SecretChatHelper.getInstance(this.currentAccount).sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionAbortKey) {
                    SecretChatHelper.getInstance(this.currentAccount).sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionRequestKey) {
                    SecretChatHelper.getInstance(this.currentAccount).sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionAcceptKey) {
                    SecretChatHelper.getInstance(this.currentAccount).sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionNoop) {
                    SecretChatHelper.getInstance(this.currentAccount).sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        }
        if (messageAction instanceof TL_messageActionScreenshotTaken) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) messageObject.getDialogId()));
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
        if (user != null && i != 0 && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
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
                int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                message.id = newMessageId;
                message.local_id = newMessageId;
                message.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                message.flags |= 256;
                message.flags |= 8;
                message.reply_to_msg_id = i;
                message.to_id = new TL_peerUser();
                message.to_id.user_id = user.id;
                message.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                message.action = new TL_messageActionScreenshotTaken();
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
            }
            tL_messages_sendScreenshotNotification.random_id = message.random_id;
            MessageObject messageObject = new MessageObject(this.currentAccount, message, false);
            messageObject.messageOwner.send_state = 1;
            ArrayList arrayList = new ArrayList();
            arrayList.add(messageObject);
            MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(message.dialog_id, arrayList);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(message);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList2, false, true, false, 0);
            performSendMessageRequest(tL_messages_sendScreenshotNotification, messageObject, null, null, null);
        }
    }

    public void sendSticker(Document document, long j, MessageObject messageObject, Object obj) {
        Document document2 = document;
        long j2 = j;
        if (document2 != null) {
            if (((int) j2) == 0) {
                if (MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (j2 >> 32))) != null) {
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

    /* JADX WARNING: Removed duplicated region for block: B:150:0x03ac  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03f3  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x042a  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0483  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01e5  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x02c1  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02d6  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x02fc  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0306  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0353  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0327  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0376  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x037e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x03ac  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x03db A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03f3  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x042a  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0483  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0487  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01e5  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x02a7  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x02c1  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02d6  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x02fc  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0306  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0327  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0353  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x035d  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0376  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x037e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x03ac  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x03db A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03f3  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0412  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x042a  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0483  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0487  */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r46, long r47) {
        /*
        r45 = this;
        r11 = r45;
        r12 = r46;
        r13 = r47;
        r15 = 0;
        if (r12 == 0) goto L_0x067d;
    L_0x0009:
        r0 = r46.isEmpty();
        if (r0 == 0) goto L_0x0011;
    L_0x000f:
        goto L_0x067d;
    L_0x0011:
        r0 = (int) r13;
        if (r0 == 0) goto L_0x0661;
    L_0x0014:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r10 = r1.getPeer(r0);
        r16 = 0;
        if (r0 <= 0) goto L_0x0042;
    L_0x0022:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = java.lang.Integer.valueOf(r0);
        r1 = r1.getUser(r2);
        if (r1 != 0) goto L_0x0033;
    L_0x0032:
        return r15;
    L_0x0033:
        r8 = r16;
        r17 = 1;
        r18 = 1;
        r19 = 1;
        r20 = 1;
        r21 = 0;
        r22 = 0;
        goto L_0x007b;
    L_0x0042:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = -r0;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getChat(r2);
        r2 = org.telegram.messenger.ChatObject.isChannel(r1);
        if (r2 == 0) goto L_0x005c;
    L_0x0057:
        r2 = r1.megagroup;
        r3 = r1.signatures;
        goto L_0x005e;
    L_0x005c:
        r2 = 0;
        r3 = 0;
    L_0x005e:
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
    L_0x007b:
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
        r6 = r11.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r6 = r6.getInputPeer(r0);
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.getClientUserId();
        r23 = r10;
        r9 = (long) r0;
        r0 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1));
        if (r0 != 0) goto L_0x00b7;
    L_0x00b4:
        r25 = 1;
        goto L_0x00b9;
    L_0x00b7:
        r25 = 0;
    L_0x00b9:
        r0 = r1;
        r1 = r2;
        r2 = r4;
        r26 = 0;
        r4 = r3;
        r3 = r5;
        r5 = 0;
    L_0x00c1:
        r15 = r46.size();
        if (r5 >= r15) goto L_0x065e;
    L_0x00c7:
        r15 = r12.get(r5);
        r15 = (org.telegram.messenger.MessageObject) r15;
        r27 = r15.getId();
        if (r27 <= 0) goto L_0x05fa;
    L_0x00d3:
        r27 = r15.needDrawBluredPreview();
        if (r27 == 0) goto L_0x00db;
    L_0x00d9:
        goto L_0x05fa;
    L_0x00db:
        r27 = r1;
        if (r17 != 0) goto L_0x012a;
    L_0x00df:
        r29 = r15.isSticker();
        if (r29 != 0) goto L_0x00f1;
    L_0x00e5:
        r29 = r15.isGif();
        if (r29 != 0) goto L_0x00f1;
    L_0x00eb:
        r29 = r15.isGame();
        if (r29 == 0) goto L_0x012a;
    L_0x00f1:
        if (r26 != 0) goto L_0x0111;
    L_0x00f3:
        r15 = 8;
        r15 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r15);
        if (r15 == 0) goto L_0x00fe;
    L_0x00fb:
        r26 = 4;
        goto L_0x0100;
    L_0x00fe:
        r26 = 1;
    L_0x0100:
        r11 = r5;
        r33 = r6;
        r36 = r7;
        r32 = r8;
        r28 = r9;
        r1 = r27;
    L_0x010b:
        r37 = 1;
        r27 = r23;
        goto L_0x064c;
    L_0x0111:
        r24 = r0;
        r31 = r3;
        r34 = r4;
        r11 = r5;
        r33 = r6;
        r36 = r7;
        r32 = r8;
        r28 = r9;
        r30 = r27;
        r37 = 1;
    L_0x0124:
        r27 = r23;
    L_0x0126:
        r23 = r2;
        goto L_0x0642;
    L_0x012a:
        r29 = 2;
        if (r18 != 0) goto L_0x016d;
    L_0x012e:
        r1 = r15.messageOwner;
        r1 = r1.media;
        r31 = r0;
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x013c;
    L_0x0138:
        r0 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r0 == 0) goto L_0x016f;
    L_0x013c:
        if (r26 != 0) goto L_0x0159;
    L_0x013e:
        r0 = 7;
        r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r0);
        if (r0 == 0) goto L_0x0149;
    L_0x0145:
        r0 = 5;
        r26 = 5;
        goto L_0x014b;
    L_0x0149:
        r26 = 2;
    L_0x014b:
        r11 = r5;
        r33 = r6;
        r36 = r7;
        r32 = r8;
        r28 = r9;
        r1 = r27;
        r0 = r31;
        goto L_0x010b;
    L_0x0159:
        r34 = r4;
        r11 = r5;
        r33 = r6;
        r36 = r7;
        r32 = r8;
        r28 = r9;
        r30 = r27;
        r24 = r31;
        r37 = 1;
        r31 = r3;
        goto L_0x0124;
    L_0x016d:
        r31 = r0;
    L_0x016f:
        if (r19 != 0) goto L_0x018b;
    L_0x0171:
        r0 = r15.messageOwner;
        r0 = r0.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r0 == 0) goto L_0x018b;
    L_0x0179:
        if (r26 != 0) goto L_0x0159;
    L_0x017b:
        r0 = 10;
        r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r0);
        if (r0 == 0) goto L_0x0187;
    L_0x0183:
        r0 = 6;
        r26 = 6;
        goto L_0x014b;
    L_0x0187:
        r0 = 3;
        r26 = 3;
        goto L_0x014b;
    L_0x018b:
        r0 = new org.telegram.tgnet.TLRPC$TL_message;
        r0.<init>();
        r32 = r15.getDialogId();
        r1 = (r32 > r9 ? 1 : (r32 == r9 ? 0 : -1));
        if (r1 != 0) goto L_0x01ac;
    L_0x0198:
        r1 = r15.messageOwner;
        r1 = r1.from_id;
        r32 = r8;
        r8 = r11.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.getClientUserId();
        if (r1 != r8) goto L_0x01ae;
    L_0x01aa:
        r1 = 1;
        goto L_0x01af;
    L_0x01ac:
        r32 = r8;
    L_0x01ae:
        r1 = 0;
    L_0x01af:
        r8 = r15.isForwarded();
        if (r8 == 0) goto L_0x01e5;
    L_0x01b5:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader;
        r1.<init>();
        r0.fwd_from = r1;
        r1 = r0.fwd_from;
        r8 = r15.messageOwner;
        r8 = r8.fwd_from;
        r33 = r6;
        r6 = r8.flags;
        r1.flags = r6;
        r6 = r8.from_id;
        r1.from_id = r6;
        r6 = r8.date;
        r1.date = r6;
        r6 = r8.channel_id;
        r1.channel_id = r6;
        r6 = r8.channel_post;
        r1.channel_post = r6;
        r6 = r8.post_author;
        r1.post_author = r6;
        r6 = r8.from_name;
        r1.from_name = r6;
        r6 = 4;
        r0.flags = r6;
        goto L_0x0289;
    L_0x01e5:
        r33 = r6;
        r6 = 4;
        if (r1 != 0) goto L_0x0289;
    L_0x01ea:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader;
        r1.<init>();
        r0.fwd_from = r1;
        r1 = r0.fwd_from;
        r8 = r15.getId();
        r1.channel_post = r8;
        r1 = r0.fwd_from;
        r8 = r1.flags;
        r8 = r8 | r6;
        r1.flags = r8;
        r1 = r15.isFromUser();
        if (r1 == 0) goto L_0x0215;
    L_0x0206:
        r1 = r0.fwd_from;
        r6 = r15.messageOwner;
        r6 = r6.from_id;
        r1.from_id = r6;
        r6 = r1.flags;
        r8 = 1;
        r6 = r6 | r8;
        r1.flags = r6;
        goto L_0x0235;
    L_0x0215:
        r1 = r0.fwd_from;
        r6 = r15.messageOwner;
        r8 = r6.to_id;
        r8 = r8.channel_id;
        r1.channel_id = r8;
        r8 = r1.flags;
        r8 = r8 | 2;
        r1.flags = r8;
        r8 = r6.post;
        if (r8 == 0) goto L_0x0235;
    L_0x0229:
        r6 = r6.from_id;
        if (r6 <= 0) goto L_0x0235;
    L_0x022d:
        r1.from_id = r6;
        r6 = r1.flags;
        r8 = 1;
        r6 = r6 | r8;
        r1.flags = r6;
    L_0x0235:
        r1 = r15.messageOwner;
        r1 = r1.post_author;
        if (r1 == 0) goto L_0x0247;
    L_0x023b:
        r6 = r0.fwd_from;
        r6.post_author = r1;
        r1 = r6.flags;
        r8 = 8;
        r1 = r1 | r8;
        r6.flags = r1;
        goto L_0x0280;
    L_0x0247:
        r1 = r15.isOutOwner();
        if (r1 != 0) goto L_0x0280;
    L_0x024d:
        r1 = r15.messageOwner;
        r6 = r1.from_id;
        if (r6 <= 0) goto L_0x0280;
    L_0x0253:
        r1 = r1.post;
        if (r1 == 0) goto L_0x0280;
    L_0x0257:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r6 = r15.messageOwner;
        r6 = r6.from_id;
        r6 = java.lang.Integer.valueOf(r6);
        r1 = r1.getUser(r6);
        if (r1 == 0) goto L_0x0280;
    L_0x026b:
        r6 = r0.fwd_from;
        r8 = r1.first_name;
        r1 = r1.last_name;
        r1 = org.telegram.messenger.ContactsController.formatName(r8, r1);
        r6.post_author = r1;
        r1 = r0.fwd_from;
        r6 = r1.flags;
        r8 = 8;
        r6 = r6 | r8;
        r1.flags = r6;
    L_0x0280:
        r1 = r15.messageOwner;
        r1 = r1.date;
        r0.date = r1;
        r1 = 4;
        r0.flags = r1;
    L_0x0289:
        r1 = (r13 > r9 ? 1 : (r13 == r9 ? 0 : -1));
        if (r1 != 0) goto L_0x02a5;
    L_0x028d:
        r1 = r0.fwd_from;
        if (r1 == 0) goto L_0x02a5;
    L_0x0291:
        r6 = r1.flags;
        r6 = r6 | 16;
        r1.flags = r6;
        r6 = r15.getId();
        r1.saved_from_msg_id = r6;
        r1 = r0.fwd_from;
        r6 = r15.messageOwner;
        r6 = r6.to_id;
        r1.saved_from_peer = r6;
    L_0x02a5:
        if (r20 != 0) goto L_0x02b7;
    L_0x02a7:
        r1 = r15.messageOwner;
        r1 = r1.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r1 == 0) goto L_0x02b7;
    L_0x02af:
        r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r1.<init>();
        r0.media = r1;
        goto L_0x02bd;
    L_0x02b7:
        r1 = r15.messageOwner;
        r1 = r1.media;
        r0.media = r1;
    L_0x02bd:
        r1 = r0.media;
        if (r1 == 0) goto L_0x02c7;
    L_0x02c1:
        r1 = r0.flags;
        r1 = r1 | 512;
        r0.flags = r1;
    L_0x02c7:
        if (r21 == 0) goto L_0x02d0;
    L_0x02c9:
        r1 = r0.flags;
        r6 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r1 = r1 | r6;
        r0.flags = r1;
    L_0x02d0:
        r1 = r15.messageOwner;
        r1 = r1.via_bot_id;
        if (r1 == 0) goto L_0x02de;
    L_0x02d6:
        r0.via_bot_id = r1;
        r1 = r0.flags;
        r1 = r1 | 2048;
        r0.flags = r1;
    L_0x02de:
        r1 = r15.messageOwner;
        r1 = r1.message;
        r0.message = r1;
        r1 = r15.getId();
        r0.fwd_msg_id = r1;
        r1 = r15.messageOwner;
        r6 = r1.attachPath;
        r0.attachPath = r6;
        r1 = r1.entities;
        r0.entities = r1;
        r1 = r0.entities;
        r1 = r1.isEmpty();
        if (r1 != 0) goto L_0x0302;
    L_0x02fc:
        r1 = r0.flags;
        r1 = r1 | 128;
        r0.flags = r1;
    L_0x0302:
        r1 = r0.attachPath;
        if (r1 != 0) goto L_0x030a;
    L_0x0306:
        r1 = "";
        r0.attachPath = r1;
    L_0x030a:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getNewMessageId();
        r0.id = r1;
        r0.local_id = r1;
        r1 = 1;
        r0.out = r1;
        r1 = r15.messageOwner;
        r28 = r9;
        r8 = r1.grouped_id;
        r34 = 0;
        r1 = (r8 > r34 ? 1 : (r8 == r34 ? 0 : -1));
        if (r1 == 0) goto L_0x0353;
    L_0x0327:
        r1 = r7.get(r8);
        r1 = (java.lang.Long) r1;
        if (r1 != 0) goto L_0x0343;
    L_0x032f:
        r1 = org.telegram.messenger.Utilities.random;
        r36 = r1.nextLong();
        r1 = java.lang.Long.valueOf(r36);
        r6 = r15.messageOwner;
        r36 = r8;
        r8 = r6.grouped_id;
        r7.put(r8, r1);
        goto L_0x0345;
    L_0x0343:
        r36 = r8;
    L_0x0345:
        r8 = r1.longValue();
        r0.grouped_id = r8;
        r1 = r0.flags;
        r6 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r1 = r1 | r6;
        r0.flags = r1;
        goto L_0x0355;
    L_0x0353:
        r36 = r8;
    L_0x0355:
        r1 = r46.size();
        r6 = 1;
        r1 = r1 - r6;
        if (r5 == r1) goto L_0x0376;
    L_0x035d:
        r1 = r5 + 1;
        r1 = r12.get(r1);
        r1 = (org.telegram.messenger.MessageObject) r1;
        r1 = r1.messageOwner;
        r8 = r1.grouped_id;
        r1 = r15.messageOwner;
        r10 = r7;
        r6 = r1.grouped_id;
        r1 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r1 == 0) goto L_0x0377;
    L_0x0372:
        r9 = r23;
        r1 = 1;
        goto L_0x037a;
    L_0x0376:
        r10 = r7;
    L_0x0377:
        r9 = r23;
        r1 = 0;
    L_0x037a:
        r6 = r9.channel_id;
        if (r6 == 0) goto L_0x0394;
    L_0x037e:
        if (r21 != 0) goto L_0x0394;
    L_0x0380:
        if (r22 == 0) goto L_0x038d;
    L_0x0382:
        r6 = r11.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getClientUserId();
        goto L_0x038e;
    L_0x038d:
        r6 = -r6;
    L_0x038e:
        r0.from_id = r6;
        r6 = 1;
        r0.post = r6;
        goto L_0x03a6;
    L_0x0394:
        r6 = r11.currentAccount;
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);
        r6 = r6.getClientUserId();
        r0.from_id = r6;
        r6 = r0.flags;
        r6 = r6 | 256;
        r0.flags = r6;
    L_0x03a6:
        r6 = r0.random_id;
        r8 = (r6 > r34 ? 1 : (r6 == r34 ? 0 : -1));
        if (r8 != 0) goto L_0x03b2;
    L_0x03ac:
        r6 = r45.getNextRandomId();
        r0.random_id = r6;
    L_0x03b2:
        r6 = r0.random_id;
        r6 = java.lang.Long.valueOf(r6);
        r4.add(r6);
        r6 = r0.random_id;
        r3.put(r6, r0);
        r6 = r0.fwd_msg_id;
        r6 = java.lang.Integer.valueOf(r6);
        r2.add(r6);
        r6 = r11.currentAccount;
        r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6);
        r6 = r6.getCurrentTime();
        r0.date = r6;
        r6 = r33;
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r7 == 0) goto L_0x03e9;
    L_0x03db:
        if (r21 != 0) goto L_0x03e9;
    L_0x03dd:
        r8 = 1;
        r0.views = r8;
        r8 = r0.flags;
        r8 = r8 | 1024;
        r0.flags = r8;
        r23 = r3;
        goto L_0x0400;
    L_0x03e9:
        r8 = r15.messageOwner;
        r23 = r3;
        r3 = r8.flags;
        r3 = r3 & 1024;
        if (r3 == 0) goto L_0x03fd;
    L_0x03f3:
        r3 = r8.views;
        r0.views = r3;
        r3 = r0.flags;
        r3 = r3 | 1024;
        r0.flags = r3;
    L_0x03fd:
        r3 = 1;
        r0.unread = r3;
    L_0x0400:
        r0.dialog_id = r13;
        r0.to_id = r9;
        r3 = org.telegram.messenger.MessageObject.isVoiceMessage(r0);
        if (r3 != 0) goto L_0x0410;
    L_0x040a:
        r3 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r0);
        if (r3 == 0) goto L_0x0422;
    L_0x0410:
        if (r7 == 0) goto L_0x041f;
    L_0x0412:
        r3 = r15.getChannelId();
        if (r3 == 0) goto L_0x041f;
    L_0x0418:
        r3 = r15.isContentUnread();
        r0.media_unread = r3;
        goto L_0x0422;
    L_0x041f:
        r3 = 1;
        r0.media_unread = r3;
    L_0x0422:
        r3 = r15.messageOwner;
        r3 = r3.to_id;
        r7 = r3 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;
        if (r7 == 0) goto L_0x042f;
    L_0x042a:
        r3 = r3.channel_id;
        r3 = -r3;
        r0.ttl = r3;
    L_0x042f:
        r3 = new org.telegram.messenger.MessageObject;
        r7 = r11.currentAccount;
        r8 = 1;
        r3.<init>(r7, r0, r8);
        r7 = r3.messageOwner;
        r7.send_state = r8;
        r7 = r31;
        r7.add(r3);
        r3 = r27;
        r3.add(r0);
        r11.putToSendingMessages(r0);
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0483;
    L_0x044c:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r8 = "forward message user_id = ";
        r0.append(r8);
        r8 = r6.user_id;
        r0.append(r8);
        r8 = " chat_id = ";
        r0.append(r8);
        r8 = r6.chat_id;
        r0.append(r8);
        r8 = " channel_id = ";
        r0.append(r8);
        r8 = r6.channel_id;
        r0.append(r8);
        r8 = " access_hash = ";
        r0.append(r8);
        r27 = r9;
        r8 = r6.access_hash;
        r0.append(r8);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x0485;
    L_0x0483:
        r27 = r9;
    L_0x0485:
        if (r1 == 0) goto L_0x048d;
    L_0x0487:
        r0 = r3.size();
        if (r0 > 0) goto L_0x04ca;
    L_0x048d:
        r0 = r3.size();
        r1 = 100;
        if (r0 == r1) goto L_0x04ca;
    L_0x0495:
        r0 = r46.size();
        r1 = 1;
        r0 = r0 - r1;
        if (r5 == r0) goto L_0x04ca;
    L_0x049d:
        r0 = r46.size();
        r0 = r0 - r1;
        if (r5 == r0) goto L_0x04b9;
    L_0x04a4:
        r0 = r5 + 1;
        r0 = r12.get(r0);
        r0 = (org.telegram.messenger.MessageObject) r0;
        r0 = r0.getDialogId();
        r8 = r15.getDialogId();
        r30 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1));
        if (r30 == 0) goto L_0x04b9;
    L_0x04b8:
        goto L_0x04ca;
    L_0x04b9:
        r30 = r3;
        r34 = r4;
        r11 = r5;
        r33 = r6;
        r24 = r7;
        r36 = r10;
        r31 = r23;
        r37 = 1;
        goto L_0x0126;
    L_0x04ca:
        r0 = r11.currentAccount;
        r38 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r0 = new java.util.ArrayList;
        r0.<init>(r3);
        r40 = 0;
        r41 = 1;
        r42 = 0;
        r43 = 0;
        r39 = r0;
        r38.putMessages(r39, r40, r41, r42, r43);
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0.updateInterfaceWithMessages(r13, r7);
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.NotificationCenter.getInstance(r0);
        r1 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r8 = 0;
        r9 = new java.lang.Object[r8];
        r0.postNotificationName(r1, r9);
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0.saveConfig(r8);
        r9 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;
        r9.<init>();
        r9.to_peer = r6;
        r0 = (r36 > r34 ? 1 : (r36 == r34 ? 0 : -1));
        if (r0 == 0) goto L_0x050f;
    L_0x050d:
        r0 = 1;
        goto L_0x0510;
    L_0x050f:
        r0 = 0;
    L_0x0510:
        r9.grouped = r0;
        r0 = r9.to_peer;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r0 == 0) goto L_0x0536;
    L_0x0518:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r8 = "silent_";
        r1.append(r8);
        r1.append(r13);
        r1 = r1.toString();
        r8 = 0;
        r0 = r0.getBoolean(r1, r8);
        r9.silent = r0;
    L_0x0536:
        r0 = r15.messageOwner;
        r0 = r0.to_id;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;
        if (r0 == 0) goto L_0x0571;
    L_0x053e:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r15.messageOwner;
        r1 = r1.to_id;
        r1 = r1.channel_id;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getChat(r1);
        r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
        r1.<init>();
        r9.from_peer = r1;
        r1 = r9.from_peer;
        r8 = r15.messageOwner;
        r8 = r8.to_id;
        r8 = r8.channel_id;
        r1.channel_id = r8;
        if (r0 == 0) goto L_0x056d;
    L_0x0565:
        r8 = r5;
        r33 = r6;
        r5 = r0.access_hash;
        r1.access_hash = r5;
        goto L_0x057b;
    L_0x056d:
        r8 = r5;
        r33 = r6;
        goto L_0x057b;
    L_0x0571:
        r8 = r5;
        r33 = r6;
        r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r0.<init>();
        r9.from_peer = r0;
    L_0x057b:
        r9.random_id = r4;
        r9.id = r2;
        r0 = r46.size();
        r15 = 1;
        if (r0 != r15) goto L_0x0595;
    L_0x0586:
        r0 = 0;
        r1 = r12.get(r0);
        r1 = (org.telegram.messenger.MessageObject) r1;
        r0 = r1.messageOwner;
        r0 = r0.with_my_score;
        if (r0 == 0) goto L_0x0595;
    L_0x0593:
        r0 = 1;
        goto L_0x0596;
    L_0x0595:
        r0 = 0;
    L_0x0596:
        r9.with_my_score = r0;
        r0 = r11.currentAccount;
        r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$GtPQ6DFMMI1Gm-S7QANSsM7url8;
        r24 = r7;
        r0 = r5;
        r30 = r3;
        r1 = r45;
        r31 = r23;
        r23 = r2;
        r2 = r47;
        r34 = r4;
        r4 = r21;
        r7 = r5;
        r5 = r25;
        r15 = r6;
        r6 = r31;
        r36 = r10;
        r10 = r7;
        r7 = r30;
        r11 = r8;
        r8 = r24;
        r35 = r9;
        r37 = 1;
        r9 = r27;
        r13 = r10;
        r10 = r35;
        r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10);
        r0 = 68;
        r1 = r35;
        r15.sendRequest(r1, r13, r0);
        r0 = r46.size();
        r0 = r0 + -1;
        if (r11 == r0) goto L_0x0642;
    L_0x05da:
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
        r44 = r4;
        r4 = r2;
        r2 = r3;
        r3 = r44;
        goto L_0x064c;
    L_0x05fa:
        r24 = r0;
        r30 = r1;
        r31 = r3;
        r34 = r4;
        r11 = r5;
        r33 = r6;
        r36 = r7;
        r32 = r8;
        r28 = r9;
        r27 = r23;
        r37 = 1;
        r23 = r2;
        r0 = r15.type;
        if (r0 != 0) goto L_0x0642;
    L_0x0615:
        r0 = r15.messageText;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x0642;
    L_0x061d:
        r0 = r15.messageOwner;
        r0 = r0.media;
        if (r0 == 0) goto L_0x0627;
    L_0x0623:
        r0 = r0.webpage;
        r5 = r0;
        goto L_0x0629;
    L_0x0627:
        r5 = r16;
    L_0x0629:
        r0 = r15.messageText;
        r1 = r0.toString();
        r4 = 0;
        if (r5 == 0) goto L_0x0634;
    L_0x0632:
        r6 = 1;
        goto L_0x0635;
    L_0x0634:
        r6 = 0;
    L_0x0635:
        r0 = r15.messageOwner;
        r7 = r0.entities;
        r8 = 0;
        r9 = 0;
        r0 = r45;
        r2 = r47;
        r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9);
    L_0x0642:
        r2 = r23;
        r0 = r24;
        r1 = r30;
        r3 = r31;
        r4 = r34;
    L_0x064c:
        r5 = r11 + 1;
        r11 = r45;
        r13 = r47;
        r23 = r27;
        r9 = r28;
        r8 = r32;
        r6 = r33;
        r7 = r36;
        goto L_0x00c1;
    L_0x065e:
        r2 = r45;
        goto L_0x067c;
    L_0x0661:
        r0 = 0;
    L_0x0662:
        r1 = r46.size();
        if (r0 >= r1) goto L_0x0678;
    L_0x0668:
        r1 = r12.get(r0);
        r1 = (org.telegram.messenger.MessageObject) r1;
        r2 = r45;
        r3 = r47;
        r2.processForwardFromMyName(r1, r3);
        r0 = r0 + 1;
        goto L_0x0662;
    L_0x0678:
        r2 = r45;
        r26 = 0;
    L_0x067c:
        return r26;
    L_0x067d:
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
            Integer num = (Integer) MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(j));
            if (num == null) {
                num = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, j));
                MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(j), num);
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
                        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
                    } else {
                        TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                        message = tL_updateNewChannelMessage.message;
                        MessagesController.getInstance(this.currentAccount).processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, message.to_id.channel_id);
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
                                DispatchQueue storageQueue = MessagesStorage.getInstance(this.currentAccount).getStorageQueue();
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
                MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            }
            StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, i2);
        } else {
            indexOf = 0;
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$l40Sz3n3XBnqm5WNgM6XcCJnSAw(this, tL_error2, tL_messages_forwardMessages));
        }
        while (indexOf < arrayList.size()) {
            Message message7 = (Message) arrayList3.get(indexOf);
            MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(message7);
            AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$OpZpi03weztqM5vuYHFqnb91tB4(this, message7));
            indexOf++;
        }
    }

    public /* synthetic */ void lambda$null$6$SendMessagesHelper(Message message, int i, Peer peer, ArrayList arrayList, long j, Message message2, int i2) {
        Message message3 = message;
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(message3.random_id, Integer.valueOf(i), message3.id, 0, false, peer.channel_id);
        MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, true, false, false, 0);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$imJ61LkDqCKC2zsI4rTSXfDp6Z0(this, message3, j, i, message2, i2));
    }

    public /* synthetic */ void lambda$null$5$SendMessagesHelper(Message message, long j, int i, Message message2, int i2) {
        message.send_state = 0;
        DataQuery.getInstance(this.currentAccount).increasePeerRaiting(j);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message2.id), message2, Long.valueOf(j), Long.valueOf(0), Integer.valueOf(i2));
        processSentMessage(i);
        removeFromSendingMessages(i);
    }

    public /* synthetic */ void lambda$null$7$SendMessagesHelper(TL_error tL_error, TL_messages_forwardMessages tL_messages_forwardMessages) {
        AlertsCreator.processError(this.currentAccount, tL_error, null, tL_messages_forwardMessages, new Object[0]);
    }

    public /* synthetic */ void lambda$null$8$SendMessagesHelper(Message message) {
        message.send_state = 2;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.id));
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

    /* JADX WARNING: Removed duplicated region for block: B:43:0x00fd A:{Catch:{ Exception -> 0x04de }} */
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
        r6 = r25.getDialogId();	 Catch:{ Exception -> 0x04de }
        r12 = "http";
        r13 = 1;
        if (r31 == 0) goto L_0x0058;
    L_0x001f:
        r2 = r10.messageOwner;	 Catch:{ Exception -> 0x04de }
        r2 = r2.media;	 Catch:{ Exception -> 0x04de }
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x04de }
        if (r2 == 0) goto L_0x0033;
    L_0x0027:
        r0 = r10.messageOwner;	 Catch:{ Exception -> 0x04de }
        r0 = r0.media;	 Catch:{ Exception -> 0x04de }
        r0 = r0.photo;	 Catch:{ Exception -> 0x04de }
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;	 Catch:{ Exception -> 0x04de }
        r11 = r27;
        r2 = 2;
        goto L_0x0049;
    L_0x0033:
        r1 = r10.messageOwner;	 Catch:{ Exception -> 0x04de }
        r1 = r1.media;	 Catch:{ Exception -> 0x04de }
        r1 = r1.document;	 Catch:{ Exception -> 0x04de }
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;	 Catch:{ Exception -> 0x04de }
        r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1);	 Catch:{ Exception -> 0x04de }
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
        r11 = r10.videoEditedInfo;	 Catch:{ Exception -> 0x04de }
    L_0x0049:
        r14 = r4.params;	 Catch:{ Exception -> 0x04de }
        r15 = r4.message;	 Catch:{ Exception -> 0x04de }
        r10.editingMessage = r15;	 Catch:{ Exception -> 0x04de }
        r15 = r4.entities;	 Catch:{ Exception -> 0x04de }
        r10.editingMessageEntities = r15;	 Catch:{ Exception -> 0x04de }
        r15 = r4.attachPath;	 Catch:{ Exception -> 0x04de }
        r13 = r2;
        goto L_0x0113;
    L_0x0058:
        r11 = r4.media;	 Catch:{ Exception -> 0x04de }
        r10.previousMedia = r11;	 Catch:{ Exception -> 0x04de }
        r11 = r4.message;	 Catch:{ Exception -> 0x04de }
        r10.previousCaption = r11;	 Catch:{ Exception -> 0x04de }
        r11 = r4.entities;	 Catch:{ Exception -> 0x04de }
        r10.previousCaptionEntities = r11;	 Catch:{ Exception -> 0x04de }
        r11 = r4.attachPath;	 Catch:{ Exception -> 0x04de }
        r10.previousAttachPath = r11;	 Catch:{ Exception -> 0x04de }
        r11 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x04de }
        r11.<init>(r13);	 Catch:{ Exception -> 0x04de }
        r9.writePreviousMessageData(r4, r11);	 Catch:{ Exception -> 0x04de }
        r14 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x04de }
        r11 = r11.length();	 Catch:{ Exception -> 0x04de }
        r14.<init>(r11);	 Catch:{ Exception -> 0x04de }
        r9.writePreviousMessageData(r4, r14);	 Catch:{ Exception -> 0x04de }
        if (r30 != 0) goto L_0x0084;
    L_0x007e:
        r11 = new java.util.HashMap;	 Catch:{ Exception -> 0x04de }
        r11.<init>();	 Catch:{ Exception -> 0x04de }
        goto L_0x0086;
    L_0x0084:
        r11 = r30;
    L_0x0086:
        r15 = "prevMedia";
        r13 = r14.toByteArray();	 Catch:{ Exception -> 0x04de }
        r13 = android.util.Base64.encodeToString(r13, r5);	 Catch:{ Exception -> 0x04de }
        r11.put(r15, r13);	 Catch:{ Exception -> 0x04de }
        r14.cleanup();	 Catch:{ Exception -> 0x04de }
        if (r0 == 0) goto L_0x00da;
    L_0x0098:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x04de }
        r13.<init>();	 Catch:{ Exception -> 0x04de }
        r4.media = r13;	 Catch:{ Exception -> 0x04de }
        r13 = r4.media;	 Catch:{ Exception -> 0x04de }
        r14 = r13.flags;	 Catch:{ Exception -> 0x04de }
        r15 = 3;
        r14 = r14 | r15;
        r13.flags = r14;	 Catch:{ Exception -> 0x04de }
        r13 = r4.media;	 Catch:{ Exception -> 0x04de }
        r13.photo = r0;	 Catch:{ Exception -> 0x04de }
        if (r2 == 0) goto L_0x00bc;
    L_0x00ad:
        r13 = r29.length();	 Catch:{ Exception -> 0x04de }
        if (r13 <= 0) goto L_0x00bc;
    L_0x00b3:
        r13 = r2.startsWith(r12);	 Catch:{ Exception -> 0x04de }
        if (r13 == 0) goto L_0x00bc;
    L_0x00b9:
        r4.attachPath = r2;	 Catch:{ Exception -> 0x04de }
        goto L_0x00d8;
    L_0x00bc:
        r13 = r0.sizes;	 Catch:{ Exception -> 0x04de }
        r14 = r0.sizes;	 Catch:{ Exception -> 0x04de }
        r14 = r14.size();	 Catch:{ Exception -> 0x04de }
        r15 = 1;
        r14 = r14 - r15;
        r13 = r13.get(r14);	 Catch:{ Exception -> 0x04de }
        r13 = (org.telegram.tgnet.TLRPC.PhotoSize) r13;	 Catch:{ Exception -> 0x04de }
        r13 = r13.location;	 Catch:{ Exception -> 0x04de }
        r13 = org.telegram.messenger.FileLoader.getPathToAttach(r13, r15);	 Catch:{ Exception -> 0x04de }
        r13 = r13.toString();	 Catch:{ Exception -> 0x04de }
        r4.attachPath = r13;	 Catch:{ Exception -> 0x04de }
    L_0x00d8:
        r13 = 2;
        goto L_0x010a;
    L_0x00da:
        if (r1 == 0) goto L_0x0109;
    L_0x00dc:
        r13 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x04de }
        r13.<init>();	 Catch:{ Exception -> 0x04de }
        r4.media = r13;	 Catch:{ Exception -> 0x04de }
        r13 = r4.media;	 Catch:{ Exception -> 0x04de }
        r14 = r13.flags;	 Catch:{ Exception -> 0x04de }
        r15 = 3;
        r14 = r14 | r15;
        r13.flags = r14;	 Catch:{ Exception -> 0x04de }
        r13 = r4.media;	 Catch:{ Exception -> 0x04de }
        r13.document = r1;	 Catch:{ Exception -> 0x04de }
        r13 = org.telegram.messenger.MessageObject.isVideoDocument(r28);	 Catch:{ Exception -> 0x04de }
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
        r14 = r27.getString();	 Catch:{ Exception -> 0x04de }
        r15 = "ve";
        r11.put(r15, r14);	 Catch:{ Exception -> 0x04de }
    L_0x0106:
        r4.attachPath = r2;	 Catch:{ Exception -> 0x04de }
        goto L_0x010a;
    L_0x0109:
        r13 = -1;
    L_0x010a:
        r4.params = r11;	 Catch:{ Exception -> 0x04de }
        r14 = 3;
        r4.send_state = r14;	 Catch:{ Exception -> 0x04de }
        r15 = r2;
        r14 = r11;
        r11 = r27;
    L_0x0113:
        r2 = r4.attachPath;	 Catch:{ Exception -> 0x04de }
        if (r2 != 0) goto L_0x011b;
    L_0x0117:
        r2 = "";
        r4.attachPath = r2;	 Catch:{ Exception -> 0x04de }
    L_0x011b:
        r4.local_id = r5;	 Catch:{ Exception -> 0x04de }
        r2 = r10.type;	 Catch:{ Exception -> 0x04de }
        r5 = 3;
        if (r2 == r5) goto L_0x0129;
    L_0x0122:
        if (r11 != 0) goto L_0x0129;
    L_0x0124:
        r2 = r10.type;	 Catch:{ Exception -> 0x04de }
        r5 = 2;
        if (r2 != r5) goto L_0x0134;
    L_0x0129:
        r2 = r4.attachPath;	 Catch:{ Exception -> 0x04de }
        r2 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x04de }
        if (r2 != 0) goto L_0x0134;
    L_0x0131:
        r2 = 1;
        r10.attachPathExists = r2;	 Catch:{ Exception -> 0x04de }
    L_0x0134:
        r2 = r10.videoEditedInfo;	 Catch:{ Exception -> 0x04de }
        if (r2 == 0) goto L_0x013c;
    L_0x0138:
        if (r11 != 0) goto L_0x013c;
    L_0x013a:
        r11 = r10.videoEditedInfo;	 Catch:{ Exception -> 0x04de }
    L_0x013c:
        if (r31 != 0) goto L_0x01c1;
    L_0x013e:
        r5 = r10.editingMessage;	 Catch:{ Exception -> 0x04de }
        if (r5 == 0) goto L_0x0177;
    L_0x0142:
        r5 = r10.editingMessage;	 Catch:{ Exception -> 0x04de }
        r5 = r5.toString();	 Catch:{ Exception -> 0x04de }
        r4.message = r5;	 Catch:{ Exception -> 0x04de }
        r5 = r10.editingMessageEntities;	 Catch:{ Exception -> 0x04de }
        if (r5 == 0) goto L_0x0154;
    L_0x014e:
        r5 = r10.editingMessageEntities;	 Catch:{ Exception -> 0x04de }
        r4.entities = r5;	 Catch:{ Exception -> 0x04de }
    L_0x0152:
        r2 = 0;
        goto L_0x0172;
    L_0x0154:
        r5 = 1;
        r2 = new java.lang.CharSequence[r5];	 Catch:{ Exception -> 0x04de }
        r5 = r10.editingMessage;	 Catch:{ Exception -> 0x04de }
        r17 = 0;
        r2[r17] = r5;	 Catch:{ Exception -> 0x04de }
        r5 = r9.currentAccount;	 Catch:{ Exception -> 0x04de }
        r5 = org.telegram.messenger.DataQuery.getInstance(r5);	 Catch:{ Exception -> 0x04de }
        r2 = r5.getEntities(r2);	 Catch:{ Exception -> 0x04de }
        if (r2 == 0) goto L_0x0152;
    L_0x0169:
        r5 = r2.isEmpty();	 Catch:{ Exception -> 0x04de }
        if (r5 != 0) goto L_0x0152;
    L_0x016f:
        r4.entities = r2;	 Catch:{ Exception -> 0x04de }
        goto L_0x0152;
    L_0x0172:
        r10.caption = r2;	 Catch:{ Exception -> 0x04de }
        r25.generateCaption();	 Catch:{ Exception -> 0x04de }
    L_0x0177:
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x04de }
        r2.<init>();	 Catch:{ Exception -> 0x04de }
        r2.add(r4);	 Catch:{ Exception -> 0x04de }
        r4 = r9.currentAccount;	 Catch:{ Exception -> 0x04de }
        r18 = org.telegram.messenger.MessagesStorage.getInstance(r4);	 Catch:{ Exception -> 0x04de }
        r20 = 0;
        r21 = 1;
        r22 = 0;
        r23 = 0;
        r19 = r2;
        r18.putMessages(r19, r20, r21, r22, r23);	 Catch:{ Exception -> 0x04de }
        r2 = -1;
        r10.type = r2;	 Catch:{ Exception -> 0x04de }
        r25.setType();	 Catch:{ Exception -> 0x04de }
        r25.createMessageSendInfo();	 Catch:{ Exception -> 0x04de }
        r2 = new java.util.ArrayList;	 Catch:{ Exception -> 0x04de }
        r2.<init>();	 Catch:{ Exception -> 0x04de }
        r2.add(r10);	 Catch:{ Exception -> 0x04de }
        r4 = r9.currentAccount;	 Catch:{ Exception -> 0x04de }
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);	 Catch:{ Exception -> 0x04de }
        r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects;	 Catch:{ Exception -> 0x04de }
        r16 = r1;
        r27 = r11;
        r11 = 2;
        r1 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x04de }
        r11 = java.lang.Long.valueOf(r6);	 Catch:{ Exception -> 0x04de }
        r17 = 0;
        r1[r17] = r11;	 Catch:{ Exception -> 0x04de }
        r11 = 1;
        r1[r11] = r2;	 Catch:{ Exception -> 0x04de }
        r4.postNotificationName(r5, r1);	 Catch:{ Exception -> 0x04de }
        goto L_0x01c5;
    L_0x01c1:
        r16 = r1;
        r27 = r11;
    L_0x01c5:
        if (r14 == 0) goto L_0x01d5;
    L_0x01c7:
        r1 = r14.containsKey(r3);	 Catch:{ Exception -> 0x04de }
        if (r1 == 0) goto L_0x01d5;
    L_0x01cd:
        r1 = r14.get(r3);	 Catch:{ Exception -> 0x04de }
        r1 = (java.lang.String) r1;	 Catch:{ Exception -> 0x04de }
        r4 = r1;
        goto L_0x01d6;
    L_0x01d5:
        r4 = 0;
    L_0x01d6:
        r1 = 8;
        r2 = 1;
        if (r13 < r2) goto L_0x01de;
    L_0x01db:
        r2 = 3;
        if (r13 <= r2) goto L_0x01e3;
    L_0x01de:
        r2 = 5;
        if (r13 < r2) goto L_0x04e5;
    L_0x01e1:
        if (r13 > r1) goto L_0x04e5;
    L_0x01e3:
        r5 = 2;
        if (r13 != r5) goto L_0x029b;
    L_0x01e6:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x04de }
        r5.<init>();	 Catch:{ Exception -> 0x04de }
        if (r14 == 0) goto L_0x0226;
    L_0x01ed:
        r11 = "masks";
        r11 = r14.get(r11);	 Catch:{ Exception -> 0x04de }
        r11 = (java.lang.String) r11;	 Catch:{ Exception -> 0x04de }
        if (r11 == 0) goto L_0x0226;
    L_0x01f7:
        r14 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x04de }
        r11 = org.telegram.messenger.Utilities.hexToBytes(r11);	 Catch:{ Exception -> 0x04de }
        r14.<init>(r11);	 Catch:{ Exception -> 0x04de }
        r11 = 0;
        r1 = r14.readInt32(r11);	 Catch:{ Exception -> 0x04de }
        r2 = 0;
    L_0x0206:
        if (r2 >= r1) goto L_0x021d;
    L_0x0208:
        r3 = r5.stickers;	 Catch:{ Exception -> 0x04de }
        r27 = r1;
        r1 = r14.readInt32(r11);	 Catch:{ Exception -> 0x04de }
        r1 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r14, r1, r11);	 Catch:{ Exception -> 0x04de }
        r3.add(r1);	 Catch:{ Exception -> 0x04de }
        r2 = r2 + 1;
        r1 = r27;
        r11 = 0;
        goto L_0x0206;
    L_0x021d:
        r1 = r5.flags;	 Catch:{ Exception -> 0x04de }
        r2 = 1;
        r1 = r1 | r2;
        r5.flags = r1;	 Catch:{ Exception -> 0x04de }
        r14.cleanup();	 Catch:{ Exception -> 0x04de }
    L_0x0226:
        r1 = r0.access_hash;	 Catch:{ Exception -> 0x04de }
        r18 = 0;
        r3 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1));
        if (r3 != 0) goto L_0x0232;
    L_0x022e:
        r2 = r5;
        r3 = r13;
        r1 = 1;
        goto L_0x0260;
    L_0x0232:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x04de }
        r1.<init>();	 Catch:{ Exception -> 0x04de }
        r2 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x04de }
        r2.<init>();	 Catch:{ Exception -> 0x04de }
        r1.id = r2;	 Catch:{ Exception -> 0x04de }
        r2 = r1.id;	 Catch:{ Exception -> 0x04de }
        r3 = r13;
        r13 = r0.id;	 Catch:{ Exception -> 0x04de }
        r2.id = r13;	 Catch:{ Exception -> 0x04de }
        r2 = r1.id;	 Catch:{ Exception -> 0x04de }
        r13 = r0.access_hash;	 Catch:{ Exception -> 0x04de }
        r2.access_hash = r13;	 Catch:{ Exception -> 0x04de }
        r2 = r1.id;	 Catch:{ Exception -> 0x04de }
        r11 = r0.file_reference;	 Catch:{ Exception -> 0x04de }
        r2.file_reference = r11;	 Catch:{ Exception -> 0x04de }
        r2 = r1.id;	 Catch:{ Exception -> 0x04de }
        r2 = r2.file_reference;	 Catch:{ Exception -> 0x04de }
        if (r2 != 0) goto L_0x025e;
    L_0x0257:
        r2 = r1.id;	 Catch:{ Exception -> 0x04de }
        r11 = 0;
        r13 = new byte[r11];	 Catch:{ Exception -> 0x04de }
        r2.file_reference = r13;	 Catch:{ Exception -> 0x04de }
    L_0x025e:
        r2 = r1;
        r1 = 0;
    L_0x0260:
        r11 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x04de }
        r11.<init>(r6);	 Catch:{ Exception -> 0x04de }
        r13 = 0;
        r11.type = r13;	 Catch:{ Exception -> 0x04de }
        r11.obj = r10;	 Catch:{ Exception -> 0x04de }
        r11.originalPath = r4;	 Catch:{ Exception -> 0x04de }
        r11.parentObject = r8;	 Catch:{ Exception -> 0x04de }
        r11.inputUploadMedia = r5;	 Catch:{ Exception -> 0x04de }
        r11.performMediaUpload = r1;	 Catch:{ Exception -> 0x04de }
        if (r15 == 0) goto L_0x0283;
    L_0x0274:
        r5 = r15.length();	 Catch:{ Exception -> 0x04de }
        if (r5 <= 0) goto L_0x0283;
    L_0x027a:
        r5 = r15.startsWith(r12);	 Catch:{ Exception -> 0x04de }
        if (r5 == 0) goto L_0x0283;
    L_0x0280:
        r11.httpLocation = r15;	 Catch:{ Exception -> 0x04de }
        goto L_0x0297;
    L_0x0283:
        r5 = r0.sizes;	 Catch:{ Exception -> 0x04de }
        r12 = r0.sizes;	 Catch:{ Exception -> 0x04de }
        r12 = r12.size();	 Catch:{ Exception -> 0x04de }
        r13 = 1;
        r12 = r12 - r13;
        r5 = r5.get(r12);	 Catch:{ Exception -> 0x04de }
        r5 = (org.telegram.tgnet.TLRPC.PhotoSize) r5;	 Catch:{ Exception -> 0x04de }
        r11.photoSize = r5;	 Catch:{ Exception -> 0x04de }
        r11.locationParent = r0;	 Catch:{ Exception -> 0x04de }
    L_0x0297:
        r12 = r11;
        r11 = r1;
        goto L_0x03e1;
    L_0x029b:
        r3 = r13;
        r0 = 3;
        if (r3 != r0) goto L_0x0331;
    L_0x029f:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x04de }
        r0.<init>();	 Catch:{ Exception -> 0x04de }
        r1 = r16;
        r2 = r1.mime_type;	 Catch:{ Exception -> 0x04de }
        r0.mime_type = r2;	 Catch:{ Exception -> 0x04de }
        r2 = r1.attributes;	 Catch:{ Exception -> 0x04de }
        r0.attributes = r2;	 Catch:{ Exception -> 0x04de }
        r2 = r25.isGif();	 Catch:{ Exception -> 0x04de }
        if (r2 != 0) goto L_0x02cc;
    L_0x02b4:
        if (r27 == 0) goto L_0x02bd;
    L_0x02b6:
        r11 = r27;
        r2 = r11.muted;	 Catch:{ Exception -> 0x04de }
        if (r2 != 0) goto L_0x02ce;
    L_0x02bc:
        goto L_0x02bf;
    L_0x02bd:
        r11 = r27;
    L_0x02bf:
        r2 = 1;
        r0.nosound_video = r2;	 Catch:{ Exception -> 0x04de }
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x04de }
        if (r2 == 0) goto L_0x02ce;
    L_0x02c6:
        r2 = "nosound_video = true";
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x04de }
        goto L_0x02ce;
    L_0x02cc:
        r11 = r27;
    L_0x02ce:
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x04de }
        r14 = 0;
        r2 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r2 != 0) goto L_0x02d9;
    L_0x02d6:
        r2 = r0;
        r5 = 1;
        goto L_0x0305;
    L_0x02d9:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x04de }
        r2.<init>();	 Catch:{ Exception -> 0x04de }
        r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x04de }
        r5.<init>();	 Catch:{ Exception -> 0x04de }
        r2.id = r5;	 Catch:{ Exception -> 0x04de }
        r5 = r2.id;	 Catch:{ Exception -> 0x04de }
        r12 = r1.id;	 Catch:{ Exception -> 0x04de }
        r5.id = r12;	 Catch:{ Exception -> 0x04de }
        r5 = r2.id;	 Catch:{ Exception -> 0x04de }
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x04de }
        r5.access_hash = r12;	 Catch:{ Exception -> 0x04de }
        r5 = r2.id;	 Catch:{ Exception -> 0x04de }
        r12 = r1.file_reference;	 Catch:{ Exception -> 0x04de }
        r5.file_reference = r12;	 Catch:{ Exception -> 0x04de }
        r5 = r2.id;	 Catch:{ Exception -> 0x04de }
        r5 = r5.file_reference;	 Catch:{ Exception -> 0x04de }
        if (r5 != 0) goto L_0x0304;
    L_0x02fd:
        r5 = r2.id;	 Catch:{ Exception -> 0x04de }
        r12 = 0;
        r13 = new byte[r12];	 Catch:{ Exception -> 0x04de }
        r5.file_reference = r13;	 Catch:{ Exception -> 0x04de }
    L_0x0304:
        r5 = 0;
    L_0x0305:
        r12 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x04de }
        r12.<init>(r6);	 Catch:{ Exception -> 0x04de }
        r13 = 1;
        r12.type = r13;	 Catch:{ Exception -> 0x04de }
        r12.obj = r10;	 Catch:{ Exception -> 0x04de }
        r12.originalPath = r4;	 Catch:{ Exception -> 0x04de }
        r12.parentObject = r8;	 Catch:{ Exception -> 0x04de }
        r12.inputUploadMedia = r0;	 Catch:{ Exception -> 0x04de }
        r12.performMediaUpload = r5;	 Catch:{ Exception -> 0x04de }
        r0 = r1.thumbs;	 Catch:{ Exception -> 0x04de }
        r0 = r0.isEmpty();	 Catch:{ Exception -> 0x04de }
        if (r0 != 0) goto L_0x032c;
    L_0x031f:
        r0 = r1.thumbs;	 Catch:{ Exception -> 0x04de }
        r13 = 0;
        r0 = r0.get(r13);	 Catch:{ Exception -> 0x04de }
        r0 = (org.telegram.tgnet.TLRPC.PhotoSize) r0;	 Catch:{ Exception -> 0x04de }
        r12.photoSize = r0;	 Catch:{ Exception -> 0x04de }
        r12.locationParent = r1;	 Catch:{ Exception -> 0x04de }
    L_0x032c:
        r12.videoEditedInfo = r11;	 Catch:{ Exception -> 0x04de }
        r11 = r5;
        goto L_0x03e1;
    L_0x0331:
        r1 = r16;
        r0 = 7;
        if (r3 != r0) goto L_0x03de;
    L_0x0336:
        if (r4 == 0) goto L_0x0369;
    L_0x0338:
        r0 = r4.length();	 Catch:{ Exception -> 0x04de }
        if (r0 <= 0) goto L_0x0369;
    L_0x033e:
        r0 = r4.startsWith(r12);	 Catch:{ Exception -> 0x04de }
        if (r0 == 0) goto L_0x0369;
    L_0x0344:
        if (r14 == 0) goto L_0x0369;
    L_0x0346:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaGifExternal;	 Catch:{ Exception -> 0x04de }
        r0.<init>();	 Catch:{ Exception -> 0x04de }
        r2 = "url";
        r2 = r14.get(r2);	 Catch:{ Exception -> 0x04de }
        r2 = (java.lang.String) r2;	 Catch:{ Exception -> 0x04de }
        r5 = "\\|";
        r2 = r2.split(r5);	 Catch:{ Exception -> 0x04de }
        r5 = r2.length;	 Catch:{ Exception -> 0x04de }
        r11 = 2;
        if (r5 != r11) goto L_0x0367;
    L_0x035d:
        r5 = 0;
        r11 = r2[r5];	 Catch:{ Exception -> 0x04de }
        r0.url = r11;	 Catch:{ Exception -> 0x04de }
        r5 = 1;
        r2 = r2[r5];	 Catch:{ Exception -> 0x04de }
        r0.q = r2;	 Catch:{ Exception -> 0x04de }
    L_0x0367:
        r2 = 1;
        goto L_0x036f;
    L_0x0369:
        r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x04de }
        r0.<init>();	 Catch:{ Exception -> 0x04de }
        r2 = 0;
    L_0x036f:
        r5 = r1.mime_type;	 Catch:{ Exception -> 0x04de }
        r0.mime_type = r5;	 Catch:{ Exception -> 0x04de }
        r5 = r1.attributes;	 Catch:{ Exception -> 0x04de }
        r0.attributes = r5;	 Catch:{ Exception -> 0x04de }
        r11 = r1.access_hash;	 Catch:{ Exception -> 0x04de }
        r13 = 0;
        r5 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r5 != 0) goto L_0x0384;
    L_0x037f:
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x04de }
        r11 = r5;
        r5 = r0;
        goto L_0x03b0;
    L_0x0384:
        r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x04de }
        r5.<init>();	 Catch:{ Exception -> 0x04de }
        r11 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x04de }
        r11.<init>();	 Catch:{ Exception -> 0x04de }
        r5.id = r11;	 Catch:{ Exception -> 0x04de }
        r11 = r5.id;	 Catch:{ Exception -> 0x04de }
        r12 = r1.id;	 Catch:{ Exception -> 0x04de }
        r11.id = r12;	 Catch:{ Exception -> 0x04de }
        r11 = r5.id;	 Catch:{ Exception -> 0x04de }
        r12 = r1.access_hash;	 Catch:{ Exception -> 0x04de }
        r11.access_hash = r12;	 Catch:{ Exception -> 0x04de }
        r11 = r5.id;	 Catch:{ Exception -> 0x04de }
        r12 = r1.file_reference;	 Catch:{ Exception -> 0x04de }
        r11.file_reference = r12;	 Catch:{ Exception -> 0x04de }
        r11 = r5.id;	 Catch:{ Exception -> 0x04de }
        r11 = r11.file_reference;	 Catch:{ Exception -> 0x04de }
        if (r11 != 0) goto L_0x03af;
    L_0x03a8:
        r11 = r5.id;	 Catch:{ Exception -> 0x04de }
        r12 = 0;
        r13 = new byte[r12];	 Catch:{ Exception -> 0x04de }
        r11.file_reference = r13;	 Catch:{ Exception -> 0x04de }
    L_0x03af:
        r11 = 0;
    L_0x03b0:
        if (r2 != 0) goto L_0x03dc;
    L_0x03b2:
        r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x04de }
        r2.<init>(r6);	 Catch:{ Exception -> 0x04de }
        r2.originalPath = r4;	 Catch:{ Exception -> 0x04de }
        r12 = 2;
        r2.type = r12;	 Catch:{ Exception -> 0x04de }
        r2.obj = r10;	 Catch:{ Exception -> 0x04de }
        r12 = r1.thumbs;	 Catch:{ Exception -> 0x04de }
        r12 = r12.isEmpty();	 Catch:{ Exception -> 0x04de }
        if (r12 != 0) goto L_0x03d3;
    L_0x03c6:
        r12 = r1.thumbs;	 Catch:{ Exception -> 0x04de }
        r13 = 0;
        r12 = r12.get(r13);	 Catch:{ Exception -> 0x04de }
        r12 = (org.telegram.tgnet.TLRPC.PhotoSize) r12;	 Catch:{ Exception -> 0x04de }
        r2.photoSize = r12;	 Catch:{ Exception -> 0x04de }
        r2.locationParent = r1;	 Catch:{ Exception -> 0x04de }
    L_0x03d3:
        r2.parentObject = r8;	 Catch:{ Exception -> 0x04de }
        r2.inputUploadMedia = r0;	 Catch:{ Exception -> 0x04de }
        r2.performMediaUpload = r11;	 Catch:{ Exception -> 0x04de }
        r12 = r2;
        r2 = r5;
        goto L_0x03e1;
    L_0x03dc:
        r2 = r5;
        goto L_0x03e0;
    L_0x03de:
        r2 = 0;
        r11 = 0;
    L_0x03e0:
        r12 = 0;
    L_0x03e1:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage;	 Catch:{ Exception -> 0x04de }
        r0.<init>();	 Catch:{ Exception -> 0x04de }
        r1 = r25.getId();	 Catch:{ Exception -> 0x04de }
        r0.id = r1;	 Catch:{ Exception -> 0x04de }
        r1 = r9.currentAccount;	 Catch:{ Exception -> 0x04de }
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);	 Catch:{ Exception -> 0x04de }
        r5 = (int) r6;	 Catch:{ Exception -> 0x04de }
        r1 = r1.getInputPeer(r5);	 Catch:{ Exception -> 0x04de }
        r0.peer = r1;	 Catch:{ Exception -> 0x04de }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04de }
        r1 = r1 | 16384;
        r0.flags = r1;	 Catch:{ Exception -> 0x04de }
        r0.media = r2;	 Catch:{ Exception -> 0x04de }
        r1 = r10.editingMessage;	 Catch:{ Exception -> 0x04de }
        if (r1 == 0) goto L_0x044c;
    L_0x0405:
        r1 = r10.editingMessage;	 Catch:{ Exception -> 0x04de }
        r1 = r1.toString();	 Catch:{ Exception -> 0x04de }
        r0.message = r1;	 Catch:{ Exception -> 0x04de }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04de }
        r1 = r1 | 2048;
        r0.flags = r1;	 Catch:{ Exception -> 0x04de }
        r1 = r10.editingMessageEntities;	 Catch:{ Exception -> 0x04de }
        if (r1 == 0) goto L_0x0424;
    L_0x0417:
        r1 = r10.editingMessageEntities;	 Catch:{ Exception -> 0x04de }
        r0.entities = r1;	 Catch:{ Exception -> 0x04de }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04de }
        r2 = 8;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x04de }
    L_0x0422:
        r1 = 0;
        goto L_0x0448;
    L_0x0424:
        r1 = 1;
        r2 = new java.lang.CharSequence[r1];	 Catch:{ Exception -> 0x04de }
        r1 = r10.editingMessage;	 Catch:{ Exception -> 0x04de }
        r5 = 0;
        r2[r5] = r1;	 Catch:{ Exception -> 0x04de }
        r1 = r9.currentAccount;	 Catch:{ Exception -> 0x04de }
        r1 = org.telegram.messenger.DataQuery.getInstance(r1);	 Catch:{ Exception -> 0x04de }
        r1 = r1.getEntities(r2);	 Catch:{ Exception -> 0x04de }
        if (r1 == 0) goto L_0x0422;
    L_0x0438:
        r2 = r1.isEmpty();	 Catch:{ Exception -> 0x04de }
        if (r2 != 0) goto L_0x0422;
    L_0x043e:
        r0.entities = r1;	 Catch:{ Exception -> 0x04de }
        r1 = r0.flags;	 Catch:{ Exception -> 0x04de }
        r2 = 8;
        r1 = r1 | r2;
        r0.flags = r1;	 Catch:{ Exception -> 0x04de }
        goto L_0x0422;
    L_0x0448:
        r10.editingMessage = r1;	 Catch:{ Exception -> 0x04de }
        r10.editingMessageEntities = r1;	 Catch:{ Exception -> 0x04de }
    L_0x044c:
        if (r12 == 0) goto L_0x0450;
    L_0x044e:
        r12.sendRequest = r0;	 Catch:{ Exception -> 0x04de }
    L_0x0450:
        r1 = 1;
        if (r3 != r1) goto L_0x0465;
    L_0x0453:
        r1 = 0;
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r1;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x0465:
        r1 = 2;
        if (r3 != r1) goto L_0x047e;
    L_0x0468:
        if (r11 == 0) goto L_0x046f;
    L_0x046a:
        r9.performSendDelayedMessage(r12);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x046f:
        r5 = 0;
        r6 = 1;
        r1 = r24;
        r2 = r0;
        r3 = r25;
        r7 = r12;
        r8 = r32;
        r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x047e:
        r1 = 3;
        if (r3 != r1) goto L_0x0498;
    L_0x0481:
        if (r11 == 0) goto L_0x0488;
    L_0x0483:
        r9.performSendDelayedMessage(r12);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x0488:
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r4;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x0498:
        r1 = 6;
        if (r3 != r1) goto L_0x04ab;
    L_0x049b:
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r4;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x04ab:
        r1 = 7;
        if (r3 != r1) goto L_0x04c4;
    L_0x04ae:
        if (r11 == 0) goto L_0x04b4;
    L_0x04b0:
        r9.performSendDelayedMessage(r12);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x04b4:
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r4;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x04c4:
        r1 = 8;
        if (r3 != r1) goto L_0x04e5;
    L_0x04c8:
        if (r11 == 0) goto L_0x04ce;
    L_0x04ca:
        r9.performSendDelayedMessage(r12);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x04ce:
        r26 = r24;
        r27 = r0;
        r28 = r25;
        r29 = r4;
        r30 = r12;
        r31 = r32;
        r26.performSendMessageRequest(r27, r28, r29, r30, r31);	 Catch:{ Exception -> 0x04de }
        goto L_0x04e5;
    L_0x04de:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r24.revertEditingMessageObject(r25);
    L_0x04e5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessageMedia(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, BaseFragment baseFragment, ArrayList<MessageEntity> arrayList, Runnable runnable) {
        if (baseFragment == null || baseFragment.getParentActivity() == null || runnable == null) {
            return 0;
        }
        TL_messages_editMessage tL_messages_editMessage = new TL_messages_editMessage();
        tL_messages_editMessage.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) messageObject.getDialogId());
        tL_messages_editMessage.message = str;
        tL_messages_editMessage.flags |= 2048;
        tL_messages_editMessage.id = messageObject.getId();
        tL_messages_editMessage.no_webpage = z ^ 1;
        if (arrayList != null) {
            tL_messages_editMessage.entities = arrayList;
            tL_messages_editMessage.flags |= 8;
        }
        return ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_editMessage, new -$$Lambda$SendMessagesHelper$0k3RdsQSyxpPqVyuBg3ZosCuce8(this, baseFragment, tL_messages_editMessage, runnable));
    }

    public /* synthetic */ void lambda$editMessage$11$SendMessagesHelper(BaseFragment baseFragment, TL_messages_editMessage tL_messages_editMessage, Runnable runnable, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) tLObject, false);
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
        int i2 = (int) j;
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
        if (i2 <= 0) {
            int i3 = -i2;
            if (MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i3)) == null) {
                Chat chatSync = MessagesStorage.getInstance(this.currentAccount).getChatSync(i3);
                if (chatSync != null) {
                    MessagesController.getInstance(this.currentAccount).putChat(chatSync, true);
                }
            }
        } else if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i2)) == null) {
            User userSync = MessagesStorage.getInstance(this.currentAccount).getUserSync(i2);
            if (userSync != null) {
                MessagesController.getInstance(this.currentAccount).putUser(userSync, true);
            }
        }
        TL_messages_getBotCallbackAnswer tL_messages_getBotCallbackAnswer = new TL_messages_getBotCallbackAnswer();
        tL_messages_getBotCallbackAnswer.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i2);
        tL_messages_getBotCallbackAnswer.msg_id = i;
        tL_messages_getBotCallbackAnswer.game = false;
        if (bArr != null) {
            tL_messages_getBotCallbackAnswer.flags |= 1;
            tL_messages_getBotCallbackAnswer.data = bArr;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getBotCallbackAnswer, new -$$Lambda$SendMessagesHelper$t77TIdvR5oVS88nA5fB-WrU7owc(this, stringBuilder2), 2);
        MessagesController.getInstance(this.currentAccount).markDialogAsRead(j, i, i, 0, false, 0, true);
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
        tL_messages_sendVote.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) messageObject.getDialogId());
        if (tL_pollAnswer != null) {
            tL_messages_sendVote.options.add(tL_pollAnswer.option);
        }
        return ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_sendVote, new -$$Lambda$SendMessagesHelper$uhLM6ViQVMMdzN56xkU0rITU9CY(this, messageObject, stringBuilder2, runnable));
    }

    public /* synthetic */ void lambda$sendVote$15$SendMessagesHelper(MessageObject messageObject, final String str, final Runnable runnable, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(0));
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) tLObject, false);
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

    public void sendCallback(boolean z, MessageObject messageObject, KeyboardButton keyboardButton, ChatActivity chatActivity) {
        MessageObject messageObject2 = messageObject;
        KeyboardButton keyboardButton2 = keyboardButton;
        if (messageObject2 != null && keyboardButton2 != null && chatActivity != null) {
            boolean z2;
            boolean z3 = keyboardButton2 instanceof TL_keyboardButtonGame;
            int i = 0;
            if (z3) {
                i = 1;
                z2 = false;
            } else if (keyboardButton2 instanceof TL_keyboardButtonBuy) {
                z2 = z;
                i = 2;
            } else {
                z2 = z;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(messageObject.getDialogId());
            String str = "_";
            stringBuilder.append(str);
            stringBuilder.append(messageObject.getId());
            stringBuilder.append(str);
            stringBuilder.append(Utilities.bytesToHex(keyboardButton2.data));
            stringBuilder.append(str);
            stringBuilder.append(i);
            String stringBuilder2 = stringBuilder.toString();
            this.waitingForCallback.put(stringBuilder2, Boolean.valueOf(true));
            -$$Lambda$SendMessagesHelper$dI2Pjo3q3FWw7MFM1jQfwpbp8Kg -__lambda_sendmessageshelper_di2pjo3q3fww7mfm1jqfwpbp8kg = new -$$Lambda$SendMessagesHelper$dI2Pjo3q3FWw7MFM1jQfwpbp8Kg(this, stringBuilder2, z2, messageObject, keyboardButton, chatActivity);
            if (z2) {
                MessagesStorage.getInstance(this.currentAccount).getBotCache(stringBuilder2, -__lambda_sendmessageshelper_di2pjo3q3fww7mfm1jqfwpbp8kg);
            } else if (!(keyboardButton2 instanceof TL_keyboardButtonBuy)) {
                TL_messages_getBotCallbackAnswer tL_messages_getBotCallbackAnswer = new TL_messages_getBotCallbackAnswer();
                tL_messages_getBotCallbackAnswer.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) messageObject.getDialogId());
                tL_messages_getBotCallbackAnswer.msg_id = messageObject.getId();
                tL_messages_getBotCallbackAnswer.game = z3;
                byte[] bArr = keyboardButton2.data;
                if (bArr != null) {
                    tL_messages_getBotCallbackAnswer.flags |= 1;
                    tL_messages_getBotCallbackAnswer.data = bArr;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getBotCallbackAnswer, -__lambda_sendmessageshelper_di2pjo3q3fww7mfm1jqfwpbp8kg, 2);
            } else if ((messageObject2.messageOwner.media.flags & 4) == 0) {
                TL_payments_getPaymentForm tL_payments_getPaymentForm = new TL_payments_getPaymentForm();
                tL_payments_getPaymentForm.msg_id = messageObject.getId();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentForm, -__lambda_sendmessageshelper_di2pjo3q3fww7mfm1jqfwpbp8kg, 2);
            } else {
                TL_payments_getPaymentReceipt tL_payments_getPaymentReceipt = new TL_payments_getPaymentReceipt();
                tL_payments_getPaymentReceipt.msg_id = messageObject2.messageOwner.media.receipt_msg_id;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_getPaymentReceipt, -__lambda_sendmessageshelper_di2pjo3q3fww7mfm1jqfwpbp8kg, 2);
            }
        }
    }

    public /* synthetic */ void lambda$sendCallback$17$SendMessagesHelper(String str, boolean z, MessageObject messageObject, KeyboardButton keyboardButton, ChatActivity chatActivity, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$IZdTubNXATOuG8G9OfLvNMDwHLY(this, str, z, tLObject, messageObject, keyboardButton, chatActivity));
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00c5  */
    public /* synthetic */ void lambda$null$16$SendMessagesHelper(java.lang.String r9, boolean r10, org.telegram.tgnet.TLObject r11, org.telegram.messenger.MessageObject r12, org.telegram.tgnet.TLRPC.KeyboardButton r13, org.telegram.ui.ChatActivity r14) {
        /*
        r8 = this;
        r0 = r8.waitingForCallback;
        r0.remove(r9);
        r0 = 0;
        if (r10 == 0) goto L_0x000f;
    L_0x0008:
        if (r11 != 0) goto L_0x000f;
    L_0x000a:
        r8.sendCallback(r0, r12, r13, r14);
        goto L_0x013b;
    L_0x000f:
        if (r11 == 0) goto L_0x013b;
    L_0x0011:
        r1 = r13 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r1 == 0) goto L_0x0040;
    L_0x0015:
        r9 = r11 instanceof org.telegram.tgnet.TLRPC.TL_payments_paymentForm;
        if (r9 == 0) goto L_0x0030;
    L_0x0019:
        r11 = (org.telegram.tgnet.TLRPC.TL_payments_paymentForm) r11;
        r9 = r8.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getInstance(r9);
        r10 = r11.users;
        r9.putUsers(r10, r0);
        r9 = new org.telegram.ui.PaymentFormActivity;
        r9.<init>(r11, r12);
        r14.presentFragment(r9);
        goto L_0x013b;
    L_0x0030:
        r9 = r11 instanceof org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt;
        if (r9 == 0) goto L_0x013b;
    L_0x0034:
        r9 = new org.telegram.ui.PaymentFormActivity;
        r11 = (org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt) r11;
        r9.<init>(r12, r11);
        r14.presentFragment(r9);
        goto L_0x013b;
    L_0x0040:
        r11 = (org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer) r11;
        if (r10 != 0) goto L_0x0051;
    L_0x0044:
        r10 = r11.cache_time;
        if (r10 == 0) goto L_0x0051;
    L_0x0048:
        r10 = r8.currentAccount;
        r10 = org.telegram.messenger.MessagesStorage.getInstance(r10);
        r10.saveBotCache(r9, r11);
    L_0x0051:
        r9 = r11.message;
        r10 = 0;
        if (r9 == 0) goto L_0x00cc;
    L_0x0056:
        r9 = r12.messageOwner;
        r12 = r9.from_id;
        r9 = r9.via_bot_id;
        if (r9 == 0) goto L_0x005f;
    L_0x005e:
        goto L_0x0060;
    L_0x005f:
        r9 = r12;
    L_0x0060:
        if (r9 <= 0) goto L_0x007b;
    L_0x0062:
        r12 = r8.currentAccount;
        r12 = org.telegram.messenger.MessagesController.getInstance(r12);
        r9 = java.lang.Integer.valueOf(r9);
        r9 = r12.getUser(r9);
        if (r9 == 0) goto L_0x008f;
    L_0x0072:
        r12 = r9.first_name;
        r9 = r9.last_name;
        r9 = org.telegram.messenger.ContactsController.formatName(r12, r9);
        goto L_0x0090;
    L_0x007b:
        r12 = r8.currentAccount;
        r12 = org.telegram.messenger.MessagesController.getInstance(r12);
        r9 = -r9;
        r9 = java.lang.Integer.valueOf(r9);
        r9 = r12.getChat(r9);
        if (r9 == 0) goto L_0x008f;
    L_0x008c:
        r9 = r9.title;
        goto L_0x0090;
    L_0x008f:
        r9 = r10;
    L_0x0090:
        if (r9 != 0) goto L_0x0094;
    L_0x0092:
        r9 = "bot";
    L_0x0094:
        r12 = r11.alert;
        if (r12 == 0) goto L_0x00c5;
    L_0x0098:
        r12 = r14.getParentActivity();
        if (r12 != 0) goto L_0x009f;
    L_0x009e:
        return;
    L_0x009f:
        r12 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r13 = r14.getParentActivity();
        r12.<init>(r13);
        r12.setTitle(r9);
        r9 = NUM; // 0x7f0d0679 float:1.8745476E38 double:1.053130596E-314;
        r13 = "OK";
        r9 = org.telegram.messenger.LocaleController.getString(r13, r9);
        r12.setPositiveButton(r9, r10);
        r9 = r11.message;
        r12.setMessage(r9);
        r9 = r12.create();
        r14.showDialog(r9);
        goto L_0x013b;
    L_0x00c5:
        r10 = r11.message;
        r14.showAlert(r9, r10);
        goto L_0x013b;
    L_0x00cc:
        r9 = r11.url;
        if (r9 == 0) goto L_0x013b;
    L_0x00d0:
        r9 = r14.getParentActivity();
        if (r9 != 0) goto L_0x00d7;
    L_0x00d6:
        return;
    L_0x00d7:
        r9 = r12.messageOwner;
        r1 = r9.from_id;
        r9 = r9.via_bot_id;
        if (r9 == 0) goto L_0x00e1;
    L_0x00df:
        r7 = r9;
        goto L_0x00e2;
    L_0x00e1:
        r7 = r1;
    L_0x00e2:
        r9 = r8.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getInstance(r9);
        r1 = java.lang.Integer.valueOf(r7);
        r9 = r9.getUser(r1);
        r1 = 1;
        if (r9 == 0) goto L_0x00f9;
    L_0x00f3:
        r9 = r9.verified;
        if (r9 == 0) goto L_0x00f9;
    L_0x00f7:
        r9 = 1;
        goto L_0x00fa;
    L_0x00f9:
        r9 = 0;
    L_0x00fa:
        r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
        if (r13 == 0) goto L_0x0136;
    L_0x00fe:
        r13 = r12.messageOwner;
        r13 = r13.media;
        r2 = r13 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r2 == 0) goto L_0x0108;
    L_0x0106:
        r10 = r13.game;
    L_0x0108:
        r3 = r10;
        if (r3 != 0) goto L_0x010c;
    L_0x010b:
        return;
    L_0x010c:
        r5 = r11.url;
        if (r9 != 0) goto L_0x012f;
    L_0x0110:
        r9 = r8.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getNotificationsSettings(r9);
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "askgame_";
        r10.append(r11);
        r10.append(r7);
        r10 = r10.toString();
        r9 = r9.getBoolean(r10, r1);
        if (r9 == 0) goto L_0x012f;
    L_0x012d:
        r6 = 1;
        goto L_0x0130;
    L_0x012f:
        r6 = 0;
    L_0x0130:
        r2 = r14;
        r4 = r12;
        r2.showOpenGameAlert(r3, r4, r5, r6, r7);
        goto L_0x013b;
    L_0x0136:
        r9 = r11.url;
        r14.showOpenUrlAlert(r9, r0);
    L_0x013b:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$null$16$SendMessagesHelper(java.lang.String, boolean, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity):void");
    }

    public boolean isSendingCallback(MessageObject messageObject, KeyboardButton keyboardButton) {
        int i = 0;
        if (messageObject == null || keyboardButton == null) {
            return false;
        }
        if (keyboardButton instanceof TL_keyboardButtonGame) {
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
                    j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_sendMedia, new -$$Lambda$SendMessagesHelper$ydKQSIv3UJTKVK2ad12P2kFNfXM(this, j2));
                }
                j2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(nativeByteBuffer);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_sendMedia, new -$$Lambda$SendMessagesHelper$ydKQSIv3UJTKVK2ad12P2kFNfXM(this, j2));
        }
    }

    public /* synthetic */ void lambda$sendGame$18$SendMessagesHelper(long j, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) tLObject, false);
        }
        if (j != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(j);
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

    /* JADX WARNING: Removed duplicated region for block: B:101:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01d8 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01c5 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01d8 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01c5 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01d8 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01c5 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01d8 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01c5 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01f2  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01d8 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x04cf A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x04cc A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x04d9 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x04cc A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x04cf A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x04d9 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x04ee A:{SKIP, Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x04cf A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x04cc A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x04d9 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x04ee A:{SKIP, Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x04cc A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x04cf A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x04d9 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x04ee A:{SKIP, Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x04cf A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x04cc A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x04d9 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x04ee A:{SKIP, Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x04cc A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x04cf A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x04d9 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x04ee A:{SKIP, Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0bda A:{SYNTHETIC, Splitter:B:660:0x0bda} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0b98 A:{SYNTHETIC, Splitter:B:639:0x0b98} */
    /* JADX WARNING: Removed duplicated region for block: B:644:0x0ba8 A:{SYNTHETIC, Splitter:B:644:0x0ba8} */
    /* JADX WARNING: Removed duplicated region for block: B:657:0x0bd1 A:{SYNTHETIC, Splitter:B:657:0x0bd1} */
    /* JADX WARNING: Removed duplicated region for block: B:660:0x0bda A:{SYNTHETIC, Splitter:B:660:0x0bda} */
    /* JADX WARNING: Removed duplicated region for block: B:672:0x0c0c A:{SKIP, Catch:{ Exception -> 0x0f1f }} */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x14cd A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x148b A:{SYNTHETIC, Splitter:B:989:0x148b} */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x14d3 A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:873:0x10ad A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x10df A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:891:0x114d A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x1120 A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:894:0x115b A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x148b A:{SYNTHETIC, Splitter:B:989:0x148b} */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x14cd A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x14d3 A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:873:0x10ad A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:879:0x10df A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x1120 A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:891:0x114d A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:894:0x115b A:{Catch:{ Exception -> 0x11ba }} */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x14cd A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x148b A:{SYNTHETIC, Splitter:B:989:0x148b} */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x14d3 A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x148b A:{SYNTHETIC, Splitter:B:989:0x148b} */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x14cd A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x14d3 A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x14cd A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x148b A:{SYNTHETIC, Splitter:B:989:0x148b} */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x14d3 A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x148b A:{SYNTHETIC, Splitter:B:989:0x148b} */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x14cd A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x14d3 A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:984:0x1483 A:{SYNTHETIC, Splitter:B:984:0x1483} */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x14cd A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x148b A:{SYNTHETIC, Splitter:B:989:0x148b} */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x14d3 A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x148b A:{SYNTHETIC, Splitter:B:989:0x148b} */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x14cd A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x14d3 A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1003:0x14cd A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:989:0x148b A:{SYNTHETIC, Splitter:B:989:0x148b} */
    /* JADX WARNING: Removed duplicated region for block: B:1006:0x14d3 A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x07d5 A:{SYNTHETIC, Splitter:B:443:0x07d5} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x082f  */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x07ff A:{Catch:{ Exception -> 0x07ed }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0886  */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0837  */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x090f A:{Catch:{ Exception -> 0x087c }} */
    /* JADX WARNING: Removed duplicated region for block: B:1075:0x171a A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1050:0x165c A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x060c A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x05de  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x06de  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0620 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x07b3 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x07d5 A:{SYNTHETIC, Splitter:B:443:0x07d5} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x07f6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x07ff A:{Catch:{ Exception -> 0x07ed }} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x082f  */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0837  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0886  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x08d2 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x090f A:{Catch:{ Exception -> 0x087c }} */
    /* JADX WARNING: Removed duplicated region for block: B:1050:0x165c A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1075:0x171a A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0213  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00dc A:{SYNTHETIC, Splitter:B:42:0x00dc} */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x0531 A:{SYNTHETIC, Splitter:B:298:0x0531} */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x053b A:{SYNTHETIC, Splitter:B:302:0x053b} */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x0570 A:{SYNTHETIC, Splitter:B:314:0x0570} */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x05ca  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x0588  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x05de  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x060c A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0620 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x06de  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x07b3 A:{Catch:{ Exception -> 0x020a }} */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x07d5 A:{SYNTHETIC, Splitter:B:443:0x07d5} */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x07f6 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x082f  */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x07ff A:{Catch:{ Exception -> 0x07ed }} */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0886  */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0837  */
    /* JADX WARNING: Removed duplicated region for block: B:505:0x08d2 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:510:0x090f A:{Catch:{ Exception -> 0x087c }} */
    /* JADX WARNING: Removed duplicated region for block: B:1075:0x171a A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1050:0x165c A:{Catch:{ Exception -> 0x17bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x17de  */
    /* JADX WARNING: Missing block: B:189:0x0337, code skipped:
            if (r12.containsKey(r6) != false) goto L_0x0295;
     */
    private void sendMessage(java.lang.String r39, java.lang.String r40, org.telegram.tgnet.TLRPC.MessageMedia r41, org.telegram.tgnet.TLRPC.TL_photo r42, org.telegram.messenger.VideoEditedInfo r43, org.telegram.tgnet.TLRPC.User r44, org.telegram.tgnet.TLRPC.TL_document r45, org.telegram.tgnet.TLRPC.TL_game r46, org.telegram.tgnet.TLRPC.TL_messageMediaPoll r47, long r48, java.lang.String r50, org.telegram.messenger.MessageObject r51, org.telegram.tgnet.TLRPC.WebPage r52, boolean r53, org.telegram.messenger.MessageObject r54, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r55, org.telegram.tgnet.TLRPC.ReplyMarkup r56, java.util.HashMap<java.lang.String, java.lang.String> r57, int r58, java.lang.Object r59) {
        /*
        r38 = this;
        r1 = r38;
        r2 = r39;
        r3 = r41;
        r4 = r42;
        r5 = r44;
        r6 = r45;
        r7 = r46;
        r8 = r47;
        r9 = r48;
        r11 = r50;
        r12 = r51;
        r13 = r52;
        r14 = r54;
        r15 = r55;
        r12 = r57;
        r6 = r58;
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
        if (r40 != 0) goto L_0x0039;
    L_0x0034:
        r18 = r7;
        r19 = r18;
        goto L_0x003d;
    L_0x0039:
        r18 = r40;
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
        if (r7 == 0) goto L_0x0068;
    L_0x005d:
        r5 = r1.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r5 = r5.getInputPeer(r7);
        goto L_0x0069;
    L_0x0068:
        r5 = 0;
    L_0x0069:
        if (r7 != 0) goto L_0x00b1;
    L_0x006b:
        r10 = r1.currentAccount;
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);
        r9 = java.lang.Integer.valueOf(r6);
        r9 = r10.getEncryptedChat(r9);
        if (r9 != 0) goto L_0x00ae;
    L_0x007b:
        if (r14 == 0) goto L_0x00ad;
    L_0x007d:
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r3 = r14.messageOwner;
        r2.markMessageAsSendError(r3);
        r2 = r14.messageOwner;
        r3 = 2;
        r2.send_state = r3;
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.messageSendError;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = r54.getId();
        r5 = java.lang.Integer.valueOf(r5);
        r6 = 0;
        r4[r6] = r5;
        r2.postNotificationName(r3, r4);
        r2 = r54.getId();
        r1.processSentMessage(r2);
    L_0x00ad:
        return;
    L_0x00ae:
        r23 = r6;
        goto L_0x00d5;
    L_0x00b1:
        r9 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r9 == 0) goto L_0x00d2;
    L_0x00b5:
        r9 = r1.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getInstance(r9);
        r10 = r5.channel_id;
        r10 = java.lang.Integer.valueOf(r10);
        r9 = r9.getChat(r10);
        if (r9 == 0) goto L_0x00cd;
    L_0x00c7:
        r9 = r9.megagroup;
        if (r9 != 0) goto L_0x00cd;
    L_0x00cb:
        r10 = 1;
        goto L_0x00ce;
    L_0x00cd:
        r10 = 0;
    L_0x00ce:
        r23 = r6;
        r9 = 0;
        goto L_0x00d6;
    L_0x00d2:
        r23 = r6;
        r9 = 0;
    L_0x00d5:
        r10 = 0;
    L_0x00d6:
        r6 = "query_id";
        r25 = r7;
        if (r14 == 0) goto L_0x0213;
    L_0x00dc:
        r7 = r14.messageOwner;	 Catch:{ Exception -> 0x020d }
        r26 = r54.isForwarded();	 Catch:{ Exception -> 0x020a }
        if (r26 == 0) goto L_0x00fb;
    L_0x00e4:
        r15 = r44;
        r39 = r58;
        r11 = r8;
        r27 = r10;
        r29 = r13;
        r46 = r18;
        r10 = r19;
        r19 = r45;
        r8 = r3;
        r13 = r4;
        r18 = r6;
        r6 = 4;
        r4 = r2;
        goto L_0x052b;
    L_0x00fb:
        r26 = r5;
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        if (r5 != 0) goto L_0x0114;
    L_0x0101:
        r5 = r14.messageOwner;	 Catch:{ Exception -> 0x020a }
        r5 = r5.media;	 Catch:{ Exception -> 0x020a }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;	 Catch:{ Exception -> 0x020a }
        if (r5 == 0) goto L_0x010a;
    L_0x0109:
        goto L_0x010c;
    L_0x010a:
        r2 = r7.message;	 Catch:{ Exception -> 0x020a }
    L_0x010c:
        r5 = r44;
        r27 = r10;
        r20 = 0;
    L_0x0112:
        r10 = r8;
        goto L_0x0122;
    L_0x0114:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r27 = r10;
        r10 = 4;
        if (r5 != r10) goto L_0x0126;
    L_0x011b:
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r5 = r44;
        r10 = r8;
        r20 = 1;
    L_0x0122:
        r8 = r45;
        goto L_0x01c3;
    L_0x0126:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 1;
        if (r5 != r10) goto L_0x0137;
    L_0x012b:
        r4 = r7.media;	 Catch:{ Exception -> 0x020a }
        r4 = r4.photo;	 Catch:{ Exception -> 0x020a }
        r4 = (org.telegram.tgnet.TLRPC.TL_photo) r4;	 Catch:{ Exception -> 0x020a }
        r5 = r44;
        r10 = r8;
        r20 = 2;
        goto L_0x0122;
    L_0x0137:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 3;
        if (r5 == r10) goto L_0x01b7;
    L_0x013c:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 5;
        if (r5 == r10) goto L_0x01b7;
    L_0x0141:
        if (r43 == 0) goto L_0x0145;
    L_0x0143:
        goto L_0x01b7;
    L_0x0145:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 12;
        if (r5 != r10) goto L_0x0171;
    L_0x014b:
        r5 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2;	 Catch:{ Exception -> 0x020a }
        r5.<init>();	 Catch:{ Exception -> 0x020a }
        r10 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r10.phone_number;	 Catch:{ Exception -> 0x020a }
        r5.phone = r10;	 Catch:{ Exception -> 0x020a }
        r10 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r10.first_name;	 Catch:{ Exception -> 0x020a }
        r5.first_name = r10;	 Catch:{ Exception -> 0x020a }
        r10 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r10.last_name;	 Catch:{ Exception -> 0x020a }
        r5.last_name = r10;	 Catch:{ Exception -> 0x020a }
        r10 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r10.vcard;	 Catch:{ Exception -> 0x020a }
        r5.restriction_reason = r10;	 Catch:{ Exception -> 0x020a }
        r10 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r10.user_id;	 Catch:{ Exception -> 0x020a }
        r5.id = r10;	 Catch:{ Exception -> 0x020a }
        r20 = 6;
        goto L_0x0112;
    L_0x0171:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 8;
        if (r5 == r10) goto L_0x01ad;
    L_0x0177:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 9;
        if (r5 == r10) goto L_0x01ad;
    L_0x017d:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 13;
        if (r5 == r10) goto L_0x01ad;
    L_0x0183:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 14;
        if (r5 != r10) goto L_0x018a;
    L_0x0189:
        goto L_0x01ad;
    L_0x018a:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 2;
        if (r5 != r10) goto L_0x0199;
    L_0x018f:
        r5 = r7.media;	 Catch:{ Exception -> 0x020a }
        r5 = r5.document;	 Catch:{ Exception -> 0x020a }
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;	 Catch:{ Exception -> 0x020a }
        r10 = r8;
        r20 = 8;
        goto L_0x01c0;
    L_0x0199:
        r5 = r14.type;	 Catch:{ Exception -> 0x020a }
        r10 = 17;
        if (r5 != r10) goto L_0x01a9;
    L_0x019f:
        r5 = r7.media;	 Catch:{ Exception -> 0x020a }
        r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5;	 Catch:{ Exception -> 0x020a }
        r8 = r45;
        r10 = r5;
        r20 = 10;
        goto L_0x01c1;
    L_0x01a9:
        r5 = r44;
        goto L_0x0112;
    L_0x01ad:
        r5 = r7.media;	 Catch:{ Exception -> 0x020a }
        r5 = r5.document;	 Catch:{ Exception -> 0x020a }
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;	 Catch:{ Exception -> 0x020a }
        r10 = r8;
        r20 = 7;
        goto L_0x01c0;
    L_0x01b7:
        r5 = r7.media;	 Catch:{ Exception -> 0x020a }
        r5 = r5.document;	 Catch:{ Exception -> 0x020a }
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;	 Catch:{ Exception -> 0x020a }
        r10 = r8;
        r20 = 3;
    L_0x01c0:
        r8 = r5;
    L_0x01c1:
        r5 = r44;
    L_0x01c3:
        if (r12 == 0) goto L_0x01d0;
    L_0x01c5:
        r28 = r12.containsKey(r6);	 Catch:{ Exception -> 0x020a }
        if (r28 == 0) goto L_0x01d0;
    L_0x01cb:
        r39 = r2;
        r20 = 9;
        goto L_0x01d2;
    L_0x01d0:
        r39 = r2;
    L_0x01d2:
        r2 = r7.media;	 Catch:{ Exception -> 0x020a }
        r2 = r2.ttl_seconds;	 Catch:{ Exception -> 0x020a }
        if (r2 <= 0) goto L_0x01f2;
    L_0x01d8:
        r2 = r7.media;	 Catch:{ Exception -> 0x020a }
        r2 = r2.ttl_seconds;	 Catch:{ Exception -> 0x020a }
        r15 = r5;
        r11 = r10;
        r29 = r13;
        r46 = r18;
        r10 = r19;
        r5 = r26;
        r13 = r4;
        r18 = r6;
        r19 = r8;
        r6 = r20;
        r4 = r39;
        r39 = r2;
        goto L_0x0207;
    L_0x01f2:
        r15 = r5;
        r11 = r10;
        r29 = r13;
        r46 = r18;
        r10 = r19;
        r5 = r26;
        r13 = r4;
        r18 = r6;
        r19 = r8;
        r6 = r20;
        r4 = r39;
        r39 = r58;
    L_0x0207:
        r8 = r3;
        goto L_0x052b;
    L_0x020a:
        r0 = move-exception;
    L_0x020b:
        r2 = r0;
        goto L_0x0210;
    L_0x020d:
        r0 = move-exception;
        r2 = r0;
        r7 = 0;
    L_0x0210:
        r14 = 0;
        goto L_0x17d0;
    L_0x0213:
        r26 = r5;
        r27 = r10;
        if (r2 == 0) goto L_0x0263;
    L_0x0219:
        if (r9 == 0) goto L_0x0221;
    L_0x021b:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x020d }
        r5.<init>();	 Catch:{ Exception -> 0x020d }
        goto L_0x0226;
    L_0x0221:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x020d }
        r5.<init>();	 Catch:{ Exception -> 0x020d }
    L_0x0226:
        r7 = r5;
        if (r9 == 0) goto L_0x023d;
    L_0x0229:
        r5 = r13 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending;	 Catch:{ Exception -> 0x020a }
        if (r5 == 0) goto L_0x023d;
    L_0x022d:
        r5 = r13.url;	 Catch:{ Exception -> 0x020a }
        if (r5 == 0) goto L_0x023c;
    L_0x0231:
        r5 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending;	 Catch:{ Exception -> 0x020a }
        r5.<init>();	 Catch:{ Exception -> 0x020a }
        r10 = r13.url;	 Catch:{ Exception -> 0x020a }
        r5.url = r10;	 Catch:{ Exception -> 0x020a }
        r13 = r5;
        goto L_0x023d;
    L_0x023c:
        r13 = 0;
    L_0x023d:
        if (r13 != 0) goto L_0x0247;
    L_0x023f:
        r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Exception -> 0x020a }
        r5.<init>();	 Catch:{ Exception -> 0x020a }
        r7.media = r5;	 Catch:{ Exception -> 0x020a }
        goto L_0x0252;
    L_0x0247:
        r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;	 Catch:{ Exception -> 0x020a }
        r5.<init>();	 Catch:{ Exception -> 0x020a }
        r7.media = r5;	 Catch:{ Exception -> 0x020a }
        r5 = r7.media;	 Catch:{ Exception -> 0x020a }
        r5.webpage = r13;	 Catch:{ Exception -> 0x020a }
    L_0x0252:
        if (r12 == 0) goto L_0x025d;
    L_0x0254:
        r5 = r12.containsKey(r6);	 Catch:{ Exception -> 0x020a }
        if (r5 == 0) goto L_0x025d;
    L_0x025a:
        r20 = 9;
        goto L_0x025f;
    L_0x025d:
        r20 = 0;
    L_0x025f:
        r7.message = r2;	 Catch:{ Exception -> 0x020a }
        goto L_0x033b;
    L_0x0263:
        if (r8 == 0) goto L_0x027b;
    L_0x0265:
        if (r9 == 0) goto L_0x026d;
    L_0x0267:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x020d }
        r5.<init>();	 Catch:{ Exception -> 0x020d }
        goto L_0x0272;
    L_0x026d:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x020d }
        r5.<init>();	 Catch:{ Exception -> 0x020d }
    L_0x0272:
        r7 = r5;
        r7.media = r8;	 Catch:{ Exception -> 0x020a }
        r10 = r19;
        r20 = 10;
        goto L_0x04b8;
    L_0x027b:
        if (r3 == 0) goto L_0x02a1;
    L_0x027d:
        if (r9 == 0) goto L_0x0285;
    L_0x027f:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x020d }
        r5.<init>();	 Catch:{ Exception -> 0x020d }
        goto L_0x028a;
    L_0x0285:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x020d }
        r5.<init>();	 Catch:{ Exception -> 0x020d }
    L_0x028a:
        r7 = r5;
        r7.media = r3;	 Catch:{ Exception -> 0x020a }
        if (r12 == 0) goto L_0x029b;
    L_0x028f:
        r5 = r12.containsKey(r6);	 Catch:{ Exception -> 0x020a }
        if (r5 == 0) goto L_0x029b;
    L_0x0295:
        r10 = r19;
    L_0x0297:
        r20 = 9;
        goto L_0x04b8;
    L_0x029b:
        r10 = r19;
        r20 = 1;
        goto L_0x04b8;
    L_0x02a1:
        if (r4 == 0) goto L_0x031b;
    L_0x02a3:
        if (r9 == 0) goto L_0x02ab;
    L_0x02a5:
        r5 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x020d }
        r5.<init>();	 Catch:{ Exception -> 0x020d }
        goto L_0x02b0;
    L_0x02ab:
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x020d }
        r5.<init>();	 Catch:{ Exception -> 0x020d }
    L_0x02b0:
        r7 = r5;
        r5 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x020a }
        r5.<init>();	 Catch:{ Exception -> 0x020a }
        r7.media = r5;	 Catch:{ Exception -> 0x020a }
        r5 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r5.flags;	 Catch:{ Exception -> 0x020a }
        r20 = 3;
        r10 = r10 | 3;
        r5.flags = r10;	 Catch:{ Exception -> 0x020a }
        if (r15 == 0) goto L_0x02c6;
    L_0x02c4:
        r7.entities = r15;	 Catch:{ Exception -> 0x020a }
    L_0x02c6:
        r5 = r58;
        if (r5 == 0) goto L_0x02da;
    L_0x02ca:
        r10 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10.ttl_seconds = r5;	 Catch:{ Exception -> 0x020a }
        r7.ttl = r5;	 Catch:{ Exception -> 0x020a }
        r10 = r7.media;	 Catch:{ Exception -> 0x020a }
        r2 = r10.flags;	 Catch:{ Exception -> 0x020a }
        r20 = 4;
        r2 = r2 | 4;
        r10.flags = r2;	 Catch:{ Exception -> 0x020a }
    L_0x02da:
        r2 = r7.media;	 Catch:{ Exception -> 0x020a }
        r2.photo = r4;	 Catch:{ Exception -> 0x020a }
        if (r12 == 0) goto L_0x02e9;
    L_0x02e0:
        r2 = r12.containsKey(r6);	 Catch:{ Exception -> 0x020a }
        if (r2 == 0) goto L_0x02e9;
    L_0x02e6:
        r20 = 9;
        goto L_0x02eb;
    L_0x02e9:
        r20 = 2;
    L_0x02eb:
        if (r11 == 0) goto L_0x02fe;
    L_0x02ed:
        r2 = r50.length();	 Catch:{ Exception -> 0x020a }
        if (r2 <= 0) goto L_0x02fe;
    L_0x02f3:
        r2 = "http";
        r2 = r11.startsWith(r2);	 Catch:{ Exception -> 0x020a }
        if (r2 == 0) goto L_0x02fe;
    L_0x02fb:
        r7.attachPath = r11;	 Catch:{ Exception -> 0x020a }
        goto L_0x033b;
    L_0x02fe:
        r2 = r4.sizes;	 Catch:{ Exception -> 0x020a }
        r10 = r4.sizes;	 Catch:{ Exception -> 0x020a }
        r10 = r10.size();	 Catch:{ Exception -> 0x020a }
        r3 = 1;
        r10 = r10 - r3;
        r2 = r2.get(r10);	 Catch:{ Exception -> 0x020a }
        r2 = (org.telegram.tgnet.TLRPC.PhotoSize) r2;	 Catch:{ Exception -> 0x020a }
        r2 = r2.location;	 Catch:{ Exception -> 0x020a }
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r3);	 Catch:{ Exception -> 0x020a }
        r2 = r2.toString();	 Catch:{ Exception -> 0x020a }
        r7.attachPath = r2;	 Catch:{ Exception -> 0x020a }
        goto L_0x033b;
    L_0x031b:
        r2 = r46;
        r5 = r58;
        if (r2 == 0) goto L_0x033f;
    L_0x0321:
        r7 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x020d }
        r7.<init>();	 Catch:{ Exception -> 0x020d }
        r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame;	 Catch:{ Exception -> 0x020a }
        r3.<init>();	 Catch:{ Exception -> 0x020a }
        r7.media = r3;	 Catch:{ Exception -> 0x020a }
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r3.game = r2;	 Catch:{ Exception -> 0x020a }
        if (r12 == 0) goto L_0x033b;
    L_0x0333:
        r2 = r12.containsKey(r6);	 Catch:{ Exception -> 0x020a }
        if (r2 == 0) goto L_0x033b;
    L_0x0339:
        goto L_0x0295;
    L_0x033b:
        r10 = r19;
        goto L_0x04b8;
    L_0x033f:
        r2 = r44;
        if (r2 == 0) goto L_0x03b3;
    L_0x0343:
        if (r9 == 0) goto L_0x034b;
    L_0x0345:
        r3 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x020d }
        r3.<init>();	 Catch:{ Exception -> 0x020d }
        goto L_0x0350;
    L_0x034b:
        r3 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x020d }
        r3.<init>();	 Catch:{ Exception -> 0x020d }
    L_0x0350:
        r7 = r3;
        r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact;	 Catch:{ Exception -> 0x020a }
        r3.<init>();	 Catch:{ Exception -> 0x020a }
        r7.media = r3;	 Catch:{ Exception -> 0x020a }
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r2.phone;	 Catch:{ Exception -> 0x020a }
        r3.phone_number = r10;	 Catch:{ Exception -> 0x020a }
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r2.first_name;	 Catch:{ Exception -> 0x020a }
        r3.first_name = r10;	 Catch:{ Exception -> 0x020a }
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r2.last_name;	 Catch:{ Exception -> 0x020a }
        r3.last_name = r10;	 Catch:{ Exception -> 0x020a }
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r2.id;	 Catch:{ Exception -> 0x020a }
        r3.user_id = r10;	 Catch:{ Exception -> 0x020a }
        r3 = r2.restriction_reason;	 Catch:{ Exception -> 0x020a }
        if (r3 == 0) goto L_0x0387;
    L_0x0374:
        r3 = r2.restriction_reason;	 Catch:{ Exception -> 0x020a }
        r10 = "BEGIN:VCARD";
        r3 = r3.startsWith(r10);	 Catch:{ Exception -> 0x020a }
        if (r3 == 0) goto L_0x0387;
    L_0x037e:
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r2.restriction_reason;	 Catch:{ Exception -> 0x020a }
        r3.vcard = r10;	 Catch:{ Exception -> 0x020a }
        r10 = r19;
        goto L_0x038d;
    L_0x0387:
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r10 = r19;
        r3.vcard = r10;	 Catch:{ Exception -> 0x020a }
    L_0x038d:
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r3 = r3.first_name;	 Catch:{ Exception -> 0x020a }
        if (r3 != 0) goto L_0x0399;
    L_0x0393:
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r3.first_name = r10;	 Catch:{ Exception -> 0x020a }
        r2.first_name = r10;	 Catch:{ Exception -> 0x020a }
    L_0x0399:
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r3 = r3.last_name;	 Catch:{ Exception -> 0x020a }
        if (r3 != 0) goto L_0x03a5;
    L_0x039f:
        r3 = r7.media;	 Catch:{ Exception -> 0x020a }
        r3.last_name = r10;	 Catch:{ Exception -> 0x020a }
        r2.last_name = r10;	 Catch:{ Exception -> 0x020a }
    L_0x03a5:
        if (r12 == 0) goto L_0x03af;
    L_0x03a7:
        r3 = r12.containsKey(r6);	 Catch:{ Exception -> 0x020a }
        if (r3 == 0) goto L_0x03af;
    L_0x03ad:
        goto L_0x0297;
    L_0x03af:
        r20 = 6;
        goto L_0x04b8;
    L_0x03b3:
        r3 = r45;
        r10 = r19;
        if (r3 == 0) goto L_0x04b7;
    L_0x03b9:
        if (r9 == 0) goto L_0x03c1;
    L_0x03bb:
        r7 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x020d }
        r7.<init>();	 Catch:{ Exception -> 0x020d }
        goto L_0x03c6;
    L_0x03c1:
        r7 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x020d }
        r7.<init>();	 Catch:{ Exception -> 0x020d }
    L_0x03c6:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x020a }
        r2.<init>();	 Catch:{ Exception -> 0x020a }
        r7.media = r2;	 Catch:{ Exception -> 0x020a }
        r2 = r7.media;	 Catch:{ Exception -> 0x020a }
        r4 = r2.flags;	 Catch:{ Exception -> 0x020a }
        r19 = 3;
        r4 = r4 | 3;
        r2.flags = r4;	 Catch:{ Exception -> 0x020a }
        if (r5 == 0) goto L_0x03e9;
    L_0x03d9:
        r2 = r7.media;	 Catch:{ Exception -> 0x020a }
        r2.ttl_seconds = r5;	 Catch:{ Exception -> 0x020a }
        r7.ttl = r5;	 Catch:{ Exception -> 0x020a }
        r2 = r7.media;	 Catch:{ Exception -> 0x020a }
        r4 = r2.flags;	 Catch:{ Exception -> 0x020a }
        r19 = 4;
        r4 = r4 | 4;
        r2.flags = r4;	 Catch:{ Exception -> 0x020a }
    L_0x03e9:
        r2 = r7.media;	 Catch:{ Exception -> 0x020a }
        r2.document = r3;	 Catch:{ Exception -> 0x020a }
        if (r12 == 0) goto L_0x03f8;
    L_0x03ef:
        r2 = r12.containsKey(r6);	 Catch:{ Exception -> 0x020a }
        if (r2 == 0) goto L_0x03f8;
    L_0x03f5:
        r20 = 9;
        goto L_0x0415;
    L_0x03f8:
        r2 = org.telegram.messenger.MessageObject.isVideoDocument(r45);	 Catch:{ Exception -> 0x020a }
        if (r2 != 0) goto L_0x0413;
    L_0x03fe:
        r2 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r45);	 Catch:{ Exception -> 0x020a }
        if (r2 != 0) goto L_0x0413;
    L_0x0404:
        if (r43 == 0) goto L_0x0407;
    L_0x0406:
        goto L_0x0413;
    L_0x0407:
        r2 = org.telegram.messenger.MessageObject.isVoiceDocument(r45);	 Catch:{ Exception -> 0x020a }
        if (r2 == 0) goto L_0x0410;
    L_0x040d:
        r20 = 8;
        goto L_0x0415;
    L_0x0410:
        r20 = 7;
        goto L_0x0415;
    L_0x0413:
        r20 = 3;
    L_0x0415:
        if (r43 == 0) goto L_0x0428;
    L_0x0417:
        r2 = r43.getString();	 Catch:{ Exception -> 0x020a }
        if (r12 != 0) goto L_0x0423;
    L_0x041d:
        r4 = new java.util.HashMap;	 Catch:{ Exception -> 0x020a }
        r4.<init>();	 Catch:{ Exception -> 0x020a }
        r12 = r4;
    L_0x0423:
        r4 = "ve";
        r12.put(r4, r2);	 Catch:{ Exception -> 0x020a }
    L_0x0428:
        if (r9 == 0) goto L_0x043f;
    L_0x042a:
        r2 = r3.dc_id;	 Catch:{ Exception -> 0x020a }
        if (r2 <= 0) goto L_0x043f;
    L_0x042e:
        r2 = org.telegram.messenger.MessageObject.isStickerDocument(r45);	 Catch:{ Exception -> 0x020a }
        if (r2 != 0) goto L_0x043f;
    L_0x0434:
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r45);	 Catch:{ Exception -> 0x020a }
        r2 = r2.toString();	 Catch:{ Exception -> 0x020a }
        r7.attachPath = r2;	 Catch:{ Exception -> 0x020a }
        goto L_0x0441;
    L_0x043f:
        r7.attachPath = r11;	 Catch:{ Exception -> 0x020a }
    L_0x0441:
        if (r9 == 0) goto L_0x04b8;
    L_0x0443:
        r2 = org.telegram.messenger.MessageObject.isStickerDocument(r45);	 Catch:{ Exception -> 0x020a }
        if (r2 == 0) goto L_0x04b8;
    L_0x0449:
        r2 = 0;
    L_0x044a:
        r4 = r3.attributes;	 Catch:{ Exception -> 0x020a }
        r4 = r4.size();	 Catch:{ Exception -> 0x020a }
        if (r2 >= r4) goto L_0x04b8;
    L_0x0452:
        r4 = r3.attributes;	 Catch:{ Exception -> 0x020a }
        r4 = r4.get(r2);	 Catch:{ Exception -> 0x020a }
        r4 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r4;	 Catch:{ Exception -> 0x020a }
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;	 Catch:{ Exception -> 0x020a }
        if (r5 == 0) goto L_0x04b0;
    L_0x045e:
        r5 = r3.attributes;	 Catch:{ Exception -> 0x020a }
        r5.remove(r2);	 Catch:{ Exception -> 0x020a }
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55;	 Catch:{ Exception -> 0x020a }
        r2.<init>();	 Catch:{ Exception -> 0x020a }
        r5 = r3.attributes;	 Catch:{ Exception -> 0x020a }
        r5.add(r2);	 Catch:{ Exception -> 0x020a }
        r5 = r4.alt;	 Catch:{ Exception -> 0x020a }
        r2.alt = r5;	 Catch:{ Exception -> 0x020a }
        r5 = r4.stickerset;	 Catch:{ Exception -> 0x020a }
        if (r5 == 0) goto L_0x04a8;
    L_0x0475:
        r5 = r4.stickerset;	 Catch:{ Exception -> 0x020a }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x020a }
        if (r5 == 0) goto L_0x0480;
    L_0x047b:
        r4 = r4.stickerset;	 Catch:{ Exception -> 0x020a }
        r4 = r4.short_name;	 Catch:{ Exception -> 0x020a }
        goto L_0x048e;
    L_0x0480:
        r5 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r5 = org.telegram.messenger.DataQuery.getInstance(r5);	 Catch:{ Exception -> 0x020a }
        r4 = r4.stickerset;	 Catch:{ Exception -> 0x020a }
        r3 = r4.id;	 Catch:{ Exception -> 0x020a }
        r4 = r5.getStickerSetName(r3);	 Catch:{ Exception -> 0x020a }
    L_0x048e:
        r3 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Exception -> 0x020a }
        if (r3 != 0) goto L_0x04a0;
    L_0x0494:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x020a }
        r3.<init>();	 Catch:{ Exception -> 0x020a }
        r2.stickerset = r3;	 Catch:{ Exception -> 0x020a }
        r2 = r2.stickerset;	 Catch:{ Exception -> 0x020a }
        r2.short_name = r4;	 Catch:{ Exception -> 0x020a }
        goto L_0x04b8;
    L_0x04a0:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x020a }
        r3.<init>();	 Catch:{ Exception -> 0x020a }
        r2.stickerset = r3;	 Catch:{ Exception -> 0x020a }
        goto L_0x04b8;
    L_0x04a8:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x020a }
        r3.<init>();	 Catch:{ Exception -> 0x020a }
        r2.stickerset = r3;	 Catch:{ Exception -> 0x020a }
        goto L_0x04b8;
    L_0x04b0:
        r2 = r2 + 1;
        r3 = r45;
        r5 = r58;
        goto L_0x044a;
    L_0x04b7:
        r7 = 0;
    L_0x04b8:
        if (r15 == 0) goto L_0x04c8;
    L_0x04ba:
        r2 = r55.isEmpty();	 Catch:{ Exception -> 0x020a }
        if (r2 != 0) goto L_0x04c8;
    L_0x04c0:
        r7.entities = r15;	 Catch:{ Exception -> 0x020a }
        r2 = r7.flags;	 Catch:{ Exception -> 0x020a }
        r2 = r2 | 128;
        r7.flags = r2;	 Catch:{ Exception -> 0x020a }
    L_0x04c8:
        r2 = r18;
        if (r2 == 0) goto L_0x04cf;
    L_0x04cc:
        r7.message = r2;	 Catch:{ Exception -> 0x020a }
        goto L_0x04d5;
    L_0x04cf:
        r3 = r7.message;	 Catch:{ Exception -> 0x020a }
        if (r3 != 0) goto L_0x04d5;
    L_0x04d3:
        r7.message = r10;	 Catch:{ Exception -> 0x020a }
    L_0x04d5:
        r3 = r7.attachPath;	 Catch:{ Exception -> 0x020a }
        if (r3 != 0) goto L_0x04db;
    L_0x04d9:
        r7.attachPath = r10;	 Catch:{ Exception -> 0x020a }
    L_0x04db:
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);	 Catch:{ Exception -> 0x020a }
        r3 = r3.getNewMessageId();	 Catch:{ Exception -> 0x020a }
        r7.id = r3;	 Catch:{ Exception -> 0x020a }
        r7.local_id = r3;	 Catch:{ Exception -> 0x020a }
        r3 = 1;
        r7.out = r3;	 Catch:{ Exception -> 0x020a }
        if (r27 == 0) goto L_0x04f8;
    L_0x04ee:
        if (r26 == 0) goto L_0x04f8;
    L_0x04f0:
        r5 = r26;
        r3 = r5.channel_id;	 Catch:{ Exception -> 0x020a }
        r3 = -r3;
        r7.from_id = r3;	 Catch:{ Exception -> 0x020a }
        goto L_0x050c;
    L_0x04f8:
        r5 = r26;
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);	 Catch:{ Exception -> 0x020a }
        r3 = r3.getClientUserId();	 Catch:{ Exception -> 0x020a }
        r7.from_id = r3;	 Catch:{ Exception -> 0x020a }
        r3 = r7.flags;	 Catch:{ Exception -> 0x020a }
        r3 = r3 | 256;
        r7.flags = r3;	 Catch:{ Exception -> 0x020a }
    L_0x050c:
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);	 Catch:{ Exception -> 0x020a }
        r4 = 0;
        r3.saveConfig(r4);	 Catch:{ Exception -> 0x020a }
        r4 = r39;
        r15 = r44;
        r19 = r45;
        r39 = r58;
        r46 = r2;
        r18 = r6;
        r11 = r8;
        r29 = r13;
        r6 = r20;
        r8 = r41;
        r13 = r42;
    L_0x052b:
        r2 = r7.random_id;	 Catch:{ Exception -> 0x17cc }
        r20 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1));
        if (r20 != 0) goto L_0x0537;
    L_0x0531:
        r2 = r38.getNextRandomId();	 Catch:{ Exception -> 0x020a }
        r7.random_id = r2;	 Catch:{ Exception -> 0x020a }
    L_0x0537:
        r2 = "bot_name";
        if (r12 == 0) goto L_0x056c;
    L_0x053b:
        r3 = "bot";
        r3 = r12.containsKey(r3);	 Catch:{ Exception -> 0x020a }
        if (r3 == 0) goto L_0x056c;
    L_0x0543:
        if (r9 == 0) goto L_0x0554;
    L_0x0545:
        r3 = r12.get(r2);	 Catch:{ Exception -> 0x020a }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x020a }
        r7.via_bot_name = r3;	 Catch:{ Exception -> 0x020a }
        r3 = r7.via_bot_name;	 Catch:{ Exception -> 0x020a }
        if (r3 != 0) goto L_0x0566;
    L_0x0551:
        r7.via_bot_name = r10;	 Catch:{ Exception -> 0x020a }
        goto L_0x0566;
    L_0x0554:
        r3 = "bot";
        r3 = r12.get(r3);	 Catch:{ Exception -> 0x020a }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x020a }
        r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ Exception -> 0x020a }
        r3 = r3.intValue();	 Catch:{ Exception -> 0x020a }
        r7.via_bot_id = r3;	 Catch:{ Exception -> 0x020a }
    L_0x0566:
        r3 = r7.flags;	 Catch:{ Exception -> 0x020a }
        r3 = r3 | 2048;
        r7.flags = r3;	 Catch:{ Exception -> 0x020a }
    L_0x056c:
        r7.params = r12;	 Catch:{ Exception -> 0x17cc }
        if (r14 == 0) goto L_0x0578;
    L_0x0570:
        r3 = r14.resendAsIs;	 Catch:{ Exception -> 0x020a }
        if (r3 != 0) goto L_0x0575;
    L_0x0574:
        goto L_0x0578;
    L_0x0575:
        r41 = r2;
        goto L_0x05cf;
    L_0x0578:
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x17cc }
        r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3);	 Catch:{ Exception -> 0x17cc }
        r3 = r3.getCurrentTime();	 Catch:{ Exception -> 0x17cc }
        r7.date = r3;	 Catch:{ Exception -> 0x17cc }
        r3 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;	 Catch:{ Exception -> 0x17cc }
        if (r3 == 0) goto L_0x05ca;
    L_0x0588:
        if (r27 == 0) goto L_0x0593;
    L_0x058a:
        r3 = 1;
        r7.views = r3;	 Catch:{ Exception -> 0x020a }
        r3 = r7.flags;	 Catch:{ Exception -> 0x020a }
        r3 = r3 | 1024;
        r7.flags = r3;	 Catch:{ Exception -> 0x020a }
    L_0x0593:
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);	 Catch:{ Exception -> 0x020a }
        r41 = r2;
        r2 = r5.channel_id;	 Catch:{ Exception -> 0x020a }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x020a }
        r2 = r3.getChat(r2);	 Catch:{ Exception -> 0x020a }
        if (r2 == 0) goto L_0x05cf;
    L_0x05a7:
        r3 = r2.megagroup;	 Catch:{ Exception -> 0x020a }
        if (r3 == 0) goto L_0x05b6;
    L_0x05ab:
        r2 = r7.flags;	 Catch:{ Exception -> 0x020a }
        r3 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r2 = r2 | r3;
        r7.flags = r2;	 Catch:{ Exception -> 0x020a }
        r3 = 1;
        r7.unread = r3;	 Catch:{ Exception -> 0x020a }
        goto L_0x05cf;
    L_0x05b6:
        r3 = 1;
        r7.post = r3;	 Catch:{ Exception -> 0x020a }
        r2 = r2.signatures;	 Catch:{ Exception -> 0x020a }
        if (r2 == 0) goto L_0x05cf;
    L_0x05bd:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);	 Catch:{ Exception -> 0x020a }
        r2 = r2.getClientUserId();	 Catch:{ Exception -> 0x020a }
        r7.from_id = r2;	 Catch:{ Exception -> 0x020a }
        goto L_0x05cf;
    L_0x05ca:
        r41 = r2;
        r2 = 1;
        r7.unread = r2;	 Catch:{ Exception -> 0x17cc }
    L_0x05cf:
        r2 = r7.flags;	 Catch:{ Exception -> 0x17cc }
        r2 = r2 | 512;
        r7.flags = r2;	 Catch:{ Exception -> 0x17cc }
        r2 = r48;
        r14 = 2;
        r7.dialog_id = r2;	 Catch:{ Exception -> 0x17cc }
        r14 = r51;
        if (r14 == 0) goto L_0x060c;
    L_0x05de:
        if (r9 == 0) goto L_0x05fa;
    L_0x05e0:
        r20 = r11;
        r11 = r14.messageOwner;	 Catch:{ Exception -> 0x020a }
        r26 = r10;
        r10 = r11.random_id;	 Catch:{ Exception -> 0x020a }
        r27 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1));
        if (r27 == 0) goto L_0x05fe;
    L_0x05ec:
        r10 = r14.messageOwner;	 Catch:{ Exception -> 0x020a }
        r10 = r10.random_id;	 Catch:{ Exception -> 0x020a }
        r7.reply_to_random_id = r10;	 Catch:{ Exception -> 0x020a }
        r10 = r7.flags;	 Catch:{ Exception -> 0x020a }
        r11 = 8;
        r10 = r10 | r11;
        r7.flags = r10;	 Catch:{ Exception -> 0x020a }
        goto L_0x0605;
    L_0x05fa:
        r26 = r10;
        r20 = r11;
    L_0x05fe:
        r10 = r7.flags;	 Catch:{ Exception -> 0x020a }
        r11 = 8;
        r10 = r10 | r11;
        r7.flags = r10;	 Catch:{ Exception -> 0x020a }
    L_0x0605:
        r10 = r51.getId();	 Catch:{ Exception -> 0x020a }
        r7.reply_to_msg_id = r10;	 Catch:{ Exception -> 0x020a }
        goto L_0x0610;
    L_0x060c:
        r26 = r10;
        r20 = r11;
    L_0x0610:
        r10 = r56;
        if (r10 == 0) goto L_0x061e;
    L_0x0614:
        if (r9 != 0) goto L_0x061e;
    L_0x0616:
        r11 = r7.flags;	 Catch:{ Exception -> 0x020a }
        r11 = r11 | 64;
        r7.flags = r11;	 Catch:{ Exception -> 0x020a }
        r7.reply_markup = r10;	 Catch:{ Exception -> 0x020a }
    L_0x061e:
        if (r25 == 0) goto L_0x06de;
    L_0x0620:
        r10 = r23;
        r11 = 1;
        if (r10 != r11) goto L_0x06a9;
    L_0x0625:
        r11 = r1.currentChatInfo;	 Catch:{ Exception -> 0x020a }
        if (r11 != 0) goto L_0x064f;
    L_0x0629:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);	 Catch:{ Exception -> 0x020a }
        r2.markMessageAsSendError(r7);	 Catch:{ Exception -> 0x020a }
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);	 Catch:{ Exception -> 0x020a }
        r3 = org.telegram.messenger.NotificationCenter.messageSendError;	 Catch:{ Exception -> 0x020a }
        r4 = 1;
        r5 = new java.lang.Object[r4];	 Catch:{ Exception -> 0x020a }
        r4 = r7.id;	 Catch:{ Exception -> 0x020a }
        r4 = java.lang.Integer.valueOf(r4);	 Catch:{ Exception -> 0x020a }
        r6 = 0;
        r5[r6] = r4;	 Catch:{ Exception -> 0x020a }
        r2.postNotificationName(r3, r5);	 Catch:{ Exception -> 0x020a }
        r2 = r7.id;	 Catch:{ Exception -> 0x020a }
        r1.processSentMessage(r2);	 Catch:{ Exception -> 0x020a }
        return;
    L_0x064f:
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x020a }
        r11.<init>();	 Catch:{ Exception -> 0x020a }
        r23 = r15;
        r15 = r1.currentChatInfo;	 Catch:{ Exception -> 0x020a }
        r15 = r15.participants;	 Catch:{ Exception -> 0x020a }
        r15 = r15.participants;	 Catch:{ Exception -> 0x020a }
        r15 = r15.iterator();	 Catch:{ Exception -> 0x020a }
    L_0x0660:
        r27 = r15.hasNext();	 Catch:{ Exception -> 0x020a }
        if (r27 == 0) goto L_0x0696;
    L_0x0666:
        r27 = r15.next();	 Catch:{ Exception -> 0x020a }
        r42 = r15;
        r15 = r27;
        r15 = (org.telegram.tgnet.TLRPC.ChatParticipant) r15;	 Catch:{ Exception -> 0x020a }
        r44 = r13;
        r13 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r13 = org.telegram.messenger.MessagesController.getInstance(r13);	 Catch:{ Exception -> 0x020a }
        r15 = r15.user_id;	 Catch:{ Exception -> 0x020a }
        r15 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x020a }
        r13 = r13.getUser(r15);	 Catch:{ Exception -> 0x020a }
        r15 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r15 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Exception -> 0x020a }
        r13 = r15.getInputUser(r13);	 Catch:{ Exception -> 0x020a }
        if (r13 == 0) goto L_0x0691;
    L_0x068e:
        r11.add(r13);	 Catch:{ Exception -> 0x020a }
    L_0x0691:
        r15 = r42;
        r13 = r44;
        goto L_0x0660;
    L_0x0696:
        r44 = r13;
        r13 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Exception -> 0x020a }
        r13.<init>();	 Catch:{ Exception -> 0x020a }
        r7.to_id = r13;	 Catch:{ Exception -> 0x020a }
        r13 = r7.to_id;	 Catch:{ Exception -> 0x020a }
        r15 = r25;
        r13.chat_id = r15;	 Catch:{ Exception -> 0x020a }
        r25 = r39;
        goto L_0x07b0;
    L_0x06a9:
        r44 = r13;
        r23 = r15;
        r15 = r25;
        r11 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);	 Catch:{ Exception -> 0x020a }
        r11 = r11.getPeer(r15);	 Catch:{ Exception -> 0x020a }
        r7.to_id = r11;	 Catch:{ Exception -> 0x020a }
        if (r15 <= 0) goto L_0x06da;
    L_0x06bd:
        r11 = r1.currentAccount;	 Catch:{ Exception -> 0x020a }
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);	 Catch:{ Exception -> 0x020a }
        r13 = java.lang.Integer.valueOf(r15);	 Catch:{ Exception -> 0x020a }
        r11 = r11.getUser(r13);	 Catch:{ Exception -> 0x020a }
        if (r11 != 0) goto L_0x06d3;
    L_0x06cd:
        r2 = r7.id;	 Catch:{ Exception -> 0x020a }
        r1.processSentMessage(r2);	 Catch:{ Exception -> 0x020a }
        return;
    L_0x06d3:
        r11 = r11.bot;	 Catch:{ Exception -> 0x020a }
        if (r11 == 0) goto L_0x06da;
    L_0x06d7:
        r11 = 0;
        r7.unread = r11;	 Catch:{ Exception -> 0x020a }
    L_0x06da:
        r25 = r39;
        goto L_0x07af;
    L_0x06de:
        r44 = r13;
        r10 = r23;
        r23 = r15;
        r11 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Exception -> 0x17cc }
        r11.<init>();	 Catch:{ Exception -> 0x17cc }
        r7.to_id = r11;	 Catch:{ Exception -> 0x17cc }
        r11 = r9.participant_id;	 Catch:{ Exception -> 0x17cc }
        r13 = r1.currentAccount;	 Catch:{ Exception -> 0x17cc }
        r13 = org.telegram.messenger.UserConfig.getInstance(r13);	 Catch:{ Exception -> 0x17cc }
        r13 = r13.getClientUserId();	 Catch:{ Exception -> 0x17cc }
        if (r11 != r13) goto L_0x0700;
    L_0x06f9:
        r11 = r7.to_id;	 Catch:{ Exception -> 0x020a }
        r13 = r9.admin_id;	 Catch:{ Exception -> 0x020a }
        r11.user_id = r13;	 Catch:{ Exception -> 0x020a }
        goto L_0x0706;
    L_0x0700:
        r11 = r7.to_id;	 Catch:{ Exception -> 0x17cc }
        r13 = r9.participant_id;	 Catch:{ Exception -> 0x17cc }
        r11.user_id = r13;	 Catch:{ Exception -> 0x17cc }
    L_0x0706:
        if (r39 == 0) goto L_0x070d;
    L_0x0708:
        r11 = r39;
        r7.ttl = r11;	 Catch:{ Exception -> 0x020a }
        goto L_0x072b;
    L_0x070d:
        r11 = r39;
        r13 = r9.ttl;	 Catch:{ Exception -> 0x17cc }
        r7.ttl = r13;	 Catch:{ Exception -> 0x17cc }
        r13 = r7.ttl;	 Catch:{ Exception -> 0x17cc }
        if (r13 == 0) goto L_0x072b;
    L_0x0717:
        r13 = r7.media;	 Catch:{ Exception -> 0x020a }
        if (r13 == 0) goto L_0x072b;
    L_0x071b:
        r13 = r7.media;	 Catch:{ Exception -> 0x020a }
        r15 = r7.ttl;	 Catch:{ Exception -> 0x020a }
        r13.ttl_seconds = r15;	 Catch:{ Exception -> 0x020a }
        r13 = r7.media;	 Catch:{ Exception -> 0x020a }
        r15 = r13.flags;	 Catch:{ Exception -> 0x020a }
        r24 = 4;
        r15 = r15 | 4;
        r13.flags = r15;	 Catch:{ Exception -> 0x020a }
    L_0x072b:
        r13 = r7.ttl;	 Catch:{ Exception -> 0x17cc }
        if (r13 == 0) goto L_0x07ad;
    L_0x072f:
        r13 = r7.media;	 Catch:{ Exception -> 0x020a }
        r13 = r13.document;	 Catch:{ Exception -> 0x020a }
        if (r13 == 0) goto L_0x07ad;
    L_0x0735:
        r13 = org.telegram.messenger.MessageObject.isVoiceMessage(r7);	 Catch:{ Exception -> 0x020a }
        if (r13 == 0) goto L_0x0770;
    L_0x073b:
        r13 = 0;
    L_0x073c:
        r15 = r7.media;	 Catch:{ Exception -> 0x020a }
        r15 = r15.document;	 Catch:{ Exception -> 0x020a }
        r15 = r15.attributes;	 Catch:{ Exception -> 0x020a }
        r15 = r15.size();	 Catch:{ Exception -> 0x020a }
        if (r13 >= r15) goto L_0x0762;
    L_0x0748:
        r15 = r7.media;	 Catch:{ Exception -> 0x020a }
        r15 = r15.document;	 Catch:{ Exception -> 0x020a }
        r15 = r15.attributes;	 Catch:{ Exception -> 0x020a }
        r15 = r15.get(r13);	 Catch:{ Exception -> 0x020a }
        r15 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r15;	 Catch:{ Exception -> 0x020a }
        r25 = r11;
        r11 = r15 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;	 Catch:{ Exception -> 0x020a }
        if (r11 == 0) goto L_0x075d;
    L_0x075a:
        r11 = r15.duration;	 Catch:{ Exception -> 0x020a }
        goto L_0x0765;
    L_0x075d:
        r13 = r13 + 1;
        r11 = r25;
        goto L_0x073c;
    L_0x0762:
        r25 = r11;
        r11 = 0;
    L_0x0765:
        r13 = r7.ttl;	 Catch:{ Exception -> 0x020a }
        r15 = 1;
        r11 = r11 + r15;
        r11 = java.lang.Math.max(r13, r11);	 Catch:{ Exception -> 0x020a }
        r7.ttl = r11;	 Catch:{ Exception -> 0x020a }
        goto L_0x07af;
    L_0x0770:
        r25 = r11;
        r11 = org.telegram.messenger.MessageObject.isVideoMessage(r7);	 Catch:{ Exception -> 0x020a }
        if (r11 != 0) goto L_0x077e;
    L_0x0778:
        r11 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r7);	 Catch:{ Exception -> 0x020a }
        if (r11 == 0) goto L_0x07af;
    L_0x077e:
        r11 = 0;
    L_0x077f:
        r13 = r7.media;	 Catch:{ Exception -> 0x020a }
        r13 = r13.document;	 Catch:{ Exception -> 0x020a }
        r13 = r13.attributes;	 Catch:{ Exception -> 0x020a }
        r13 = r13.size();	 Catch:{ Exception -> 0x020a }
        if (r11 >= r13) goto L_0x07a1;
    L_0x078b:
        r13 = r7.media;	 Catch:{ Exception -> 0x020a }
        r13 = r13.document;	 Catch:{ Exception -> 0x020a }
        r13 = r13.attributes;	 Catch:{ Exception -> 0x020a }
        r13 = r13.get(r11);	 Catch:{ Exception -> 0x020a }
        r13 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r13;	 Catch:{ Exception -> 0x020a }
        r15 = r13 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x020a }
        if (r15 == 0) goto L_0x079e;
    L_0x079b:
        r11 = r13.duration;	 Catch:{ Exception -> 0x020a }
        goto L_0x07a2;
    L_0x079e:
        r11 = r11 + 1;
        goto L_0x077f;
    L_0x07a1:
        r11 = 0;
    L_0x07a2:
        r13 = r7.ttl;	 Catch:{ Exception -> 0x020a }
        r15 = 1;
        r11 = r11 + r15;
        r11 = java.lang.Math.max(r13, r11);	 Catch:{ Exception -> 0x020a }
        r7.ttl = r11;	 Catch:{ Exception -> 0x020a }
        goto L_0x07af;
    L_0x07ad:
        r25 = r11;
    L_0x07af:
        r11 = 0;
    L_0x07b0:
        r13 = 1;
        if (r10 == r13) goto L_0x07c3;
    L_0x07b3:
        r10 = org.telegram.messenger.MessageObject.isVoiceMessage(r7);	 Catch:{ Exception -> 0x020a }
        if (r10 != 0) goto L_0x07bf;
    L_0x07b9:
        r10 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r7);	 Catch:{ Exception -> 0x020a }
        if (r10 == 0) goto L_0x07c3;
    L_0x07bf:
        r10 = 1;
        r7.media_unread = r10;	 Catch:{ Exception -> 0x020a }
        goto L_0x07c4;
    L_0x07c3:
        r10 = 1;
    L_0x07c4:
        r7.send_state = r10;	 Catch:{ Exception -> 0x17cc }
        r13 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x17cc }
        r15 = r1.currentAccount;	 Catch:{ Exception -> 0x17cc }
        r13.<init>(r15, r7, r10);	 Catch:{ Exception -> 0x17cc }
        r13.replyMessageObject = r14;	 Catch:{ Exception -> 0x17c2 }
        r10 = r13.isForwarded();	 Catch:{ Exception -> 0x17c2 }
        if (r10 != 0) goto L_0x07f2;
    L_0x07d5:
        r10 = r13.type;	 Catch:{ Exception -> 0x07ed }
        r14 = 3;
        if (r10 == r14) goto L_0x07e1;
    L_0x07da:
        if (r43 != 0) goto L_0x07e1;
    L_0x07dc:
        r10 = r13.type;	 Catch:{ Exception -> 0x07ed }
        r14 = 2;
        if (r10 != r14) goto L_0x07f2;
    L_0x07e1:
        r10 = r7.attachPath;	 Catch:{ Exception -> 0x07ed }
        r10 = android.text.TextUtils.isEmpty(r10);	 Catch:{ Exception -> 0x07ed }
        if (r10 != 0) goto L_0x07f2;
    L_0x07e9:
        r10 = 1;
        r13.attachPathExists = r10;	 Catch:{ Exception -> 0x07ed }
        goto L_0x07f2;
    L_0x07ed:
        r0 = move-exception;
        r2 = r0;
        r14 = r13;
        goto L_0x17d0;
    L_0x07f2:
        r10 = r13.videoEditedInfo;	 Catch:{ Exception -> 0x17c2 }
        if (r10 == 0) goto L_0x07fb;
    L_0x07f6:
        if (r43 != 0) goto L_0x07fb;
    L_0x07f8:
        r10 = r13.videoEditedInfo;	 Catch:{ Exception -> 0x07ed }
        goto L_0x07fd;
    L_0x07fb:
        r10 = r43;
    L_0x07fd:
        if (r12 == 0) goto L_0x082f;
    L_0x07ff:
        r14 = "groupId";
        r14 = r12.get(r14);	 Catch:{ Exception -> 0x07ed }
        r14 = (java.lang.String) r14;	 Catch:{ Exception -> 0x07ed }
        if (r14 == 0) goto L_0x081d;
    L_0x0809:
        r14 = org.telegram.messenger.Utilities.parseLong(r14);	 Catch:{ Exception -> 0x07ed }
        r14 = r14.longValue();	 Catch:{ Exception -> 0x07ed }
        r7.grouped_id = r14;	 Catch:{ Exception -> 0x07ed }
        r42 = r14;
        r14 = r7.flags;	 Catch:{ Exception -> 0x07ed }
        r15 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r14 = r14 | r15;
        r7.flags = r14;	 Catch:{ Exception -> 0x07ed }
        goto L_0x081f;
    L_0x081d:
        r42 = r16;
    L_0x081f:
        r14 = "final";
        r14 = r12.get(r14);	 Catch:{ Exception -> 0x07ed }
        if (r14 == 0) goto L_0x0829;
    L_0x0827:
        r14 = 1;
        goto L_0x082a;
    L_0x0829:
        r14 = 0;
    L_0x082a:
        r27 = r14;
        r14 = r42;
        goto L_0x0833;
    L_0x082f:
        r14 = r16;
        r27 = 0;
    L_0x0833:
        r28 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r28 != 0) goto L_0x0886;
    L_0x0837:
        r39 = r11;
        r11 = new java.util.ArrayList;	 Catch:{ Exception -> 0x087e }
        r11.<init>();	 Catch:{ Exception -> 0x087e }
        r11.add(r13);	 Catch:{ Exception -> 0x087e }
        r42 = r12;
        r12 = new java.util.ArrayList;	 Catch:{ Exception -> 0x087e }
        r12.<init>();	 Catch:{ Exception -> 0x087e }
        r12.add(r7);	 Catch:{ Exception -> 0x087e }
        r52 = r13;
        r13 = r1.currentAccount;	 Catch:{ Exception -> 0x087c }
        r30 = org.telegram.messenger.MessagesStorage.getInstance(r13);	 Catch:{ Exception -> 0x087c }
        r32 = 0;
        r33 = 1;
        r34 = 0;
        r35 = 0;
        r31 = r12;
        r30.putMessages(r31, r32, r33, r34, r35);	 Catch:{ Exception -> 0x087c }
        r12 = r1.currentAccount;	 Catch:{ Exception -> 0x087c }
        r12 = org.telegram.messenger.MessagesController.getInstance(r12);	 Catch:{ Exception -> 0x087c }
        r12.updateInterfaceWithMessages(r2, r11);	 Catch:{ Exception -> 0x087c }
        r11 = r1.currentAccount;	 Catch:{ Exception -> 0x087c }
        r11 = org.telegram.messenger.NotificationCenter.getInstance(r11);	 Catch:{ Exception -> 0x087c }
        r12 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;	 Catch:{ Exception -> 0x087c }
        r43 = r10;
        r13 = 0;
        r10 = new java.lang.Object[r13];	 Catch:{ Exception -> 0x087c }
        r11.postNotificationName(r12, r10);	 Catch:{ Exception -> 0x087c }
        r10 = 0;
        r13 = 0;
        goto L_0x08ce;
    L_0x087c:
        r0 = move-exception;
        goto L_0x0881;
    L_0x087e:
        r0 = move-exception;
        r52 = r13;
    L_0x0881:
        r14 = r52;
    L_0x0883:
        r2 = r0;
        goto L_0x17d0;
    L_0x0886:
        r43 = r10;
        r39 = r11;
        r42 = r12;
        r52 = r13;
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x17bd }
        r10.<init>();	 Catch:{ Exception -> 0x17bd }
        r11 = "group_";
        r10.append(r11);	 Catch:{ Exception -> 0x17bd }
        r10.append(r14);	 Catch:{ Exception -> 0x17bd }
        r10 = r10.toString();	 Catch:{ Exception -> 0x17bd }
        r11 = r1.delayedMessages;	 Catch:{ Exception -> 0x17bd }
        r10 = r11.get(r10);	 Catch:{ Exception -> 0x17bd }
        r10 = (java.util.ArrayList) r10;	 Catch:{ Exception -> 0x17bd }
        if (r10 == 0) goto L_0x08b1;
    L_0x08a9:
        r11 = 0;
        r10 = r10.get(r11);	 Catch:{ Exception -> 0x087c }
        r10 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r10;	 Catch:{ Exception -> 0x087c }
        goto L_0x08b2;
    L_0x08b1:
        r10 = 0;
    L_0x08b2:
        if (r10 != 0) goto L_0x08be;
    L_0x08b4:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x087c }
        r10.<init>(r2);	 Catch:{ Exception -> 0x087c }
        r10.initForGroup(r14);	 Catch:{ Exception -> 0x087c }
        r10.encryptedChat = r9;	 Catch:{ Exception -> 0x087c }
    L_0x08be:
        r11 = 0;
        r10.performMediaUpload = r11;	 Catch:{ Exception -> 0x17bd }
        r13 = 0;
        r10.photoSize = r13;	 Catch:{ Exception -> 0x17bd }
        r10.videoEditedInfo = r13;	 Catch:{ Exception -> 0x17bd }
        r10.httpLocation = r13;	 Catch:{ Exception -> 0x17bd }
        if (r27 == 0) goto L_0x08ce;
    L_0x08ca:
        r11 = r7.id;	 Catch:{ Exception -> 0x087c }
        r10.finalGroupMessage = r11;	 Catch:{ Exception -> 0x087c }
    L_0x08ce:
        r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x17bd }
        if (r11 == 0) goto L_0x090b;
    L_0x08d2:
        if (r5 == 0) goto L_0x090b;
    L_0x08d4:
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x087c }
        r11.<init>();	 Catch:{ Exception -> 0x087c }
        r12 = "send message user_id = ";
        r11.append(r12);	 Catch:{ Exception -> 0x087c }
        r12 = r5.user_id;	 Catch:{ Exception -> 0x087c }
        r11.append(r12);	 Catch:{ Exception -> 0x087c }
        r12 = " chat_id = ";
        r11.append(r12);	 Catch:{ Exception -> 0x087c }
        r12 = r5.chat_id;	 Catch:{ Exception -> 0x087c }
        r11.append(r12);	 Catch:{ Exception -> 0x087c }
        r12 = " channel_id = ";
        r11.append(r12);	 Catch:{ Exception -> 0x087c }
        r12 = r5.channel_id;	 Catch:{ Exception -> 0x087c }
        r11.append(r12);	 Catch:{ Exception -> 0x087c }
        r12 = " access_hash = ";
        r11.append(r12);	 Catch:{ Exception -> 0x087c }
        r56 = r14;
        r13 = r5.access_hash;	 Catch:{ Exception -> 0x087c }
        r11.append(r13);	 Catch:{ Exception -> 0x087c }
        r11 = r11.toString();	 Catch:{ Exception -> 0x087c }
        org.telegram.messenger.FileLog.d(r11);	 Catch:{ Exception -> 0x087c }
        goto L_0x090d;
    L_0x090b:
        r56 = r14;
    L_0x090d:
        if (r6 == 0) goto L_0x1649;
    L_0x090f:
        r11 = 9;
        if (r6 != r11) goto L_0x0919;
    L_0x0913:
        if (r4 == 0) goto L_0x0919;
    L_0x0915:
        if (r9 == 0) goto L_0x0919;
    L_0x0917:
        goto L_0x1649;
    L_0x0919:
        r4 = 1;
        if (r6 < r4) goto L_0x091f;
    L_0x091c:
        r4 = 3;
        if (r6 <= r4) goto L_0x0930;
    L_0x091f:
        r4 = 5;
        if (r6 < r4) goto L_0x0926;
    L_0x0922:
        r4 = 8;
        if (r6 <= r4) goto L_0x0930;
    L_0x0926:
        r4 = 9;
        if (r6 != r4) goto L_0x092c;
    L_0x092a:
        if (r9 != 0) goto L_0x0930;
    L_0x092c:
        r4 = 10;
        if (r6 != r4) goto L_0x14e8;
    L_0x0930:
        if (r9 != 0) goto L_0x0var_;
    L_0x0932:
        r4 = 1;
        if (r6 != r4) goto L_0x0996;
    L_0x0935:
        r4 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x087c }
        if (r4 == 0) goto L_0x0953;
    L_0x0939:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue;	 Catch:{ Exception -> 0x087c }
        r4.<init>();	 Catch:{ Exception -> 0x087c }
        r9 = r8.address;	 Catch:{ Exception -> 0x087c }
        r4.address = r9;	 Catch:{ Exception -> 0x087c }
        r9 = r8.title;	 Catch:{ Exception -> 0x087c }
        r4.title = r9;	 Catch:{ Exception -> 0x087c }
        r9 = r8.provider;	 Catch:{ Exception -> 0x087c }
        r4.provider = r9;	 Catch:{ Exception -> 0x087c }
        r9 = r8.venue_id;	 Catch:{ Exception -> 0x087c }
        r4.venue_id = r9;	 Catch:{ Exception -> 0x087c }
        r11 = r26;
        r4.venue_type = r11;	 Catch:{ Exception -> 0x087c }
        goto L_0x096e;
    L_0x0953:
        r11 = r26;
        r4 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;	 Catch:{ Exception -> 0x087c }
        if (r4 == 0) goto L_0x0969;
    L_0x0959:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive;	 Catch:{ Exception -> 0x087c }
        r4.<init>();	 Catch:{ Exception -> 0x087c }
        r9 = r8.period;	 Catch:{ Exception -> 0x087c }
        r4.period = r9;	 Catch:{ Exception -> 0x087c }
        r9 = r4.flags;	 Catch:{ Exception -> 0x087c }
        r12 = 2;
        r9 = r9 | r12;
        r4.flags = r9;	 Catch:{ Exception -> 0x087c }
        goto L_0x096e;
    L_0x0969:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint;	 Catch:{ Exception -> 0x087c }
        r4.<init>();	 Catch:{ Exception -> 0x087c }
    L_0x096e:
        r9 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint;	 Catch:{ Exception -> 0x087c }
        r9.<init>();	 Catch:{ Exception -> 0x087c }
        r4.geo_point = r9;	 Catch:{ Exception -> 0x087c }
        r9 = r4.geo_point;	 Catch:{ Exception -> 0x087c }
        r12 = r8.geo;	 Catch:{ Exception -> 0x087c }
        r12 = r12.lat;	 Catch:{ Exception -> 0x087c }
        r9.lat = r12;	 Catch:{ Exception -> 0x087c }
        r9 = r4.geo_point;	 Catch:{ Exception -> 0x087c }
        r8 = r8.geo;	 Catch:{ Exception -> 0x087c }
        r12 = r8._long;	 Catch:{ Exception -> 0x087c }
        r9._long = r12;	 Catch:{ Exception -> 0x087c }
        r26 = r5;
        r19 = r11;
        r15 = r21;
        r18 = 0;
        r5 = r59;
        r11 = r7;
        r7 = r52;
        r52 = r6;
        goto L_0x0d1c;
    L_0x0996:
        r11 = r26;
        r4 = 2;
        if (r6 == r4) goto L_0x0CLASSNAME;
    L_0x099b:
        r4 = 9;
        if (r6 != r4) goto L_0x09a3;
    L_0x099f:
        if (r44 == 0) goto L_0x09a3;
    L_0x09a1:
        goto L_0x0CLASSNAME;
    L_0x09a3:
        r4 = 3;
        if (r6 != r4) goto L_0x0a64;
    L_0x09a6:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x087c }
        r4.<init>();	 Catch:{ Exception -> 0x087c }
        r12 = r19;
        r8 = r12.mime_type;	 Catch:{ Exception -> 0x087c }
        r4.mime_type = r8;	 Catch:{ Exception -> 0x087c }
        r8 = r12.attributes;	 Catch:{ Exception -> 0x087c }
        r4.attributes = r8;	 Catch:{ Exception -> 0x087c }
        r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r12);	 Catch:{ Exception -> 0x087c }
        if (r8 != 0) goto L_0x09d7;
    L_0x09bb:
        if (r43 == 0) goto L_0x09c8;
    L_0x09bd:
        r13 = r43;
        r8 = r13.muted;	 Catch:{ Exception -> 0x087c }
        if (r8 != 0) goto L_0x09d9;
    L_0x09c3:
        r8 = r13.roundVideo;	 Catch:{ Exception -> 0x087c }
        if (r8 != 0) goto L_0x09d9;
    L_0x09c7:
        goto L_0x09ca;
    L_0x09c8:
        r13 = r43;
    L_0x09ca:
        r8 = 1;
        r4.nosound_video = r8;	 Catch:{ Exception -> 0x087c }
        r8 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x087c }
        if (r8 == 0) goto L_0x09d9;
    L_0x09d1:
        r8 = "nosound_video = true";
        org.telegram.messenger.FileLog.d(r8);	 Catch:{ Exception -> 0x087c }
        goto L_0x09d9;
    L_0x09d7:
        r13 = r43;
    L_0x09d9:
        if (r25 == 0) goto L_0x09e7;
    L_0x09db:
        r8 = r25;
        r4.ttl_seconds = r8;	 Catch:{ Exception -> 0x087c }
        r7.ttl = r8;	 Catch:{ Exception -> 0x087c }
        r8 = r4.flags;	 Catch:{ Exception -> 0x087c }
        r9 = 2;
        r8 = r8 | r9;
        r4.flags = r8;	 Catch:{ Exception -> 0x087c }
    L_0x09e7:
        r8 = r12.access_hash;	 Catch:{ Exception -> 0x087c }
        r14 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r14 != 0) goto L_0x09f0;
    L_0x09ed:
        r9 = r4;
        r8 = 1;
        goto L_0x0a1d;
    L_0x09f0:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x087c }
        r8.<init>();	 Catch:{ Exception -> 0x087c }
        r9 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x087c }
        r9.<init>();	 Catch:{ Exception -> 0x087c }
        r8.id = r9;	 Catch:{ Exception -> 0x087c }
        r9 = r8.id;	 Catch:{ Exception -> 0x087c }
        r14 = r12.id;	 Catch:{ Exception -> 0x087c }
        r9.id = r14;	 Catch:{ Exception -> 0x087c }
        r9 = r8.id;	 Catch:{ Exception -> 0x087c }
        r14 = r12.access_hash;	 Catch:{ Exception -> 0x087c }
        r9.access_hash = r14;	 Catch:{ Exception -> 0x087c }
        r9 = r8.id;	 Catch:{ Exception -> 0x087c }
        r14 = r12.file_reference;	 Catch:{ Exception -> 0x087c }
        r9.file_reference = r14;	 Catch:{ Exception -> 0x087c }
        r9 = r8.id;	 Catch:{ Exception -> 0x087c }
        r9 = r9.file_reference;	 Catch:{ Exception -> 0x087c }
        if (r9 != 0) goto L_0x0a1b;
    L_0x0a14:
        r9 = r8.id;	 Catch:{ Exception -> 0x087c }
        r14 = 0;
        r15 = new byte[r14];	 Catch:{ Exception -> 0x087c }
        r9.file_reference = r15;	 Catch:{ Exception -> 0x087c }
    L_0x0a1b:
        r9 = r8;
        r8 = 0;
    L_0x0a1d:
        if (r10 != 0) goto L_0x0a36;
    L_0x0a1f:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x087c }
        r10.<init>(r2);	 Catch:{ Exception -> 0x087c }
        r14 = 1;
        r10.type = r14;	 Catch:{ Exception -> 0x087c }
        r14 = r52;
        r10.obj = r14;	 Catch:{ Exception -> 0x0aa5 }
        r15 = r21;
        r10.originalPath = r15;	 Catch:{ Exception -> 0x0aa5 }
        r26 = r5;
        r5 = r59;
        r10.parentObject = r5;	 Catch:{ Exception -> 0x0aa5 }
        goto L_0x0a3e;
    L_0x0a36:
        r14 = r52;
        r26 = r5;
        r15 = r21;
        r5 = r59;
    L_0x0a3e:
        r10.inputUploadMedia = r4;	 Catch:{ Exception -> 0x0aa5 }
        r10.performMediaUpload = r8;	 Catch:{ Exception -> 0x0aa5 }
        r4 = r12.thumbs;	 Catch:{ Exception -> 0x0aa5 }
        r4 = r4.isEmpty();	 Catch:{ Exception -> 0x0aa5 }
        if (r4 != 0) goto L_0x0a5a;
    L_0x0a4a:
        r4 = r12.thumbs;	 Catch:{ Exception -> 0x0aa5 }
        r18 = r8;
        r8 = 0;
        r4 = r4.get(r8);	 Catch:{ Exception -> 0x0aa5 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0aa5 }
        r10.photoSize = r4;	 Catch:{ Exception -> 0x0aa5 }
        r10.locationParent = r12;	 Catch:{ Exception -> 0x0aa5 }
        goto L_0x0a5c;
    L_0x0a5a:
        r18 = r8;
    L_0x0a5c:
        r10.videoEditedInfo = r13;	 Catch:{ Exception -> 0x0aa5 }
        r52 = r6;
        r4 = r9;
        r19 = r11;
        goto L_0x0aa1;
    L_0x0a64:
        r14 = r52;
        r26 = r5;
        r12 = r19;
        r15 = r21;
        r8 = r25;
        r5 = r59;
        r4 = 6;
        if (r6 != r4) goto L_0x0aa8;
    L_0x0a73:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact;	 Catch:{ Exception -> 0x0aa5 }
        r4.<init>();	 Catch:{ Exception -> 0x0aa5 }
        r8 = r23;
        r9 = r8.phone;	 Catch:{ Exception -> 0x0aa5 }
        r4.phone_number = r9;	 Catch:{ Exception -> 0x0aa5 }
        r9 = r8.first_name;	 Catch:{ Exception -> 0x0aa5 }
        r4.first_name = r9;	 Catch:{ Exception -> 0x0aa5 }
        r9 = r8.last_name;	 Catch:{ Exception -> 0x0aa5 }
        r4.last_name = r9;	 Catch:{ Exception -> 0x0aa5 }
        r9 = r8.restriction_reason;	 Catch:{ Exception -> 0x0aa5 }
        if (r9 == 0) goto L_0x0a99;
    L_0x0a8a:
        r9 = r8.restriction_reason;	 Catch:{ Exception -> 0x0aa5 }
        r12 = "BEGIN:VCARD";
        r9 = r9.startsWith(r12);	 Catch:{ Exception -> 0x0aa5 }
        if (r9 == 0) goto L_0x0a99;
    L_0x0a94:
        r8 = r8.restriction_reason;	 Catch:{ Exception -> 0x0aa5 }
        r4.vcard = r8;	 Catch:{ Exception -> 0x0aa5 }
        goto L_0x0a9b;
    L_0x0a99:
        r4.vcard = r11;	 Catch:{ Exception -> 0x0aa5 }
    L_0x0a9b:
        r52 = r6;
        r19 = r11;
        r18 = 0;
    L_0x0aa1:
        r11 = r7;
        r7 = r14;
        goto L_0x0d1c;
    L_0x0aa5:
        r0 = move-exception;
        goto L_0x0883;
    L_0x0aa8:
        r4 = 7;
        if (r6 == r4) goto L_0x0b3f;
    L_0x0aab:
        r4 = 9;
        if (r6 != r4) goto L_0x0ab1;
    L_0x0aaf:
        goto L_0x0b3f;
    L_0x0ab1:
        r4 = 8;
        if (r6 != r4) goto L_0x0b20;
    L_0x0ab5:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0aa5 }
        r4.<init>();	 Catch:{ Exception -> 0x0aa5 }
        r9 = r12.mime_type;	 Catch:{ Exception -> 0x0aa5 }
        r4.mime_type = r9;	 Catch:{ Exception -> 0x0aa5 }
        r9 = r12.attributes;	 Catch:{ Exception -> 0x0aa5 }
        r4.attributes = r9;	 Catch:{ Exception -> 0x0aa5 }
        if (r8 == 0) goto L_0x0ace;
    L_0x0ac4:
        r4.ttl_seconds = r8;	 Catch:{ Exception -> 0x0aa5 }
        r7.ttl = r8;	 Catch:{ Exception -> 0x0aa5 }
        r8 = r4.flags;	 Catch:{ Exception -> 0x0aa5 }
        r9 = 2;
        r8 = r8 | r9;
        r4.flags = r8;	 Catch:{ Exception -> 0x0aa5 }
    L_0x0ace:
        r8 = r12.access_hash;	 Catch:{ Exception -> 0x0aa5 }
        r10 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r10 != 0) goto L_0x0ad9;
    L_0x0ad4:
        r8 = r4;
        r19 = r11;
        r10 = 1;
        goto L_0x0b07;
    L_0x0ad9:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0aa5 }
        r8.<init>();	 Catch:{ Exception -> 0x0aa5 }
        r9 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0aa5 }
        r9.<init>();	 Catch:{ Exception -> 0x0aa5 }
        r8.id = r9;	 Catch:{ Exception -> 0x0aa5 }
        r9 = r8.id;	 Catch:{ Exception -> 0x0aa5 }
        r19 = r11;
        r10 = r12.id;	 Catch:{ Exception -> 0x0aa5 }
        r9.id = r10;	 Catch:{ Exception -> 0x0aa5 }
        r9 = r8.id;	 Catch:{ Exception -> 0x0aa5 }
        r10 = r12.access_hash;	 Catch:{ Exception -> 0x0aa5 }
        r9.access_hash = r10;	 Catch:{ Exception -> 0x0aa5 }
        r9 = r8.id;	 Catch:{ Exception -> 0x0aa5 }
        r10 = r12.file_reference;	 Catch:{ Exception -> 0x0aa5 }
        r9.file_reference = r10;	 Catch:{ Exception -> 0x0aa5 }
        r9 = r8.id;	 Catch:{ Exception -> 0x0aa5 }
        r9 = r9.file_reference;	 Catch:{ Exception -> 0x0aa5 }
        if (r9 != 0) goto L_0x0b06;
    L_0x0aff:
        r9 = r8.id;	 Catch:{ Exception -> 0x0aa5 }
        r10 = 0;
        r11 = new byte[r10];	 Catch:{ Exception -> 0x0aa5 }
        r9.file_reference = r11;	 Catch:{ Exception -> 0x0aa5 }
    L_0x0b06:
        r10 = 0;
    L_0x0b07:
        r9 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0aa5 }
        r9.<init>(r2);	 Catch:{ Exception -> 0x0aa5 }
        r11 = 3;
        r9.type = r11;	 Catch:{ Exception -> 0x0aa5 }
        r9.obj = r14;	 Catch:{ Exception -> 0x0aa5 }
        r9.parentObject = r5;	 Catch:{ Exception -> 0x0aa5 }
        r9.inputUploadMedia = r4;	 Catch:{ Exception -> 0x0aa5 }
        r9.performMediaUpload = r10;	 Catch:{ Exception -> 0x0aa5 }
        r52 = r6;
        r11 = r7;
        r4 = r8;
        r18 = r10;
        r7 = r14;
        goto L_0x0d1b;
    L_0x0b20:
        r19 = r11;
        r4 = 10;
        if (r6 != r4) goto L_0x0b36;
    L_0x0b26:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll;	 Catch:{ Exception -> 0x0aa5 }
        r4.<init>();	 Catch:{ Exception -> 0x0aa5 }
        r8 = r20;
        r8 = r8.poll;	 Catch:{ Exception -> 0x0aa5 }
        r4.poll = r8;	 Catch:{ Exception -> 0x0aa5 }
        r52 = r6;
        r11 = r7;
        r7 = r14;
        goto L_0x0b3b;
    L_0x0b36:
        r52 = r6;
        r11 = r7;
        r7 = r14;
        r4 = 0;
    L_0x0b3b:
        r18 = 0;
        goto L_0x0d1c;
    L_0x0b3f:
        r19 = r11;
        if (r15 != 0) goto L_0x0b54;
    L_0x0b43:
        r4 = r50;
        if (r4 != 0) goto L_0x0b56;
    L_0x0b47:
        r51 = r10;
        r10 = r12.access_hash;	 Catch:{ Exception -> 0x0aa5 }
        r13 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1));
        if (r13 != 0) goto L_0x0b50;
    L_0x0b4f:
        goto L_0x0b58;
    L_0x0b50:
        r4 = 0;
        r10 = 0;
        goto L_0x0bcb;
    L_0x0b54:
        r4 = r50;
    L_0x0b56:
        r51 = r10;
    L_0x0b58:
        if (r9 != 0) goto L_0x0b8f;
    L_0x0b5a:
        r9 = android.text.TextUtils.isEmpty(r15);	 Catch:{ Exception -> 0x0aa5 }
        if (r9 != 0) goto L_0x0b8f;
    L_0x0b60:
        r9 = "http";
        r9 = r15.startsWith(r9);	 Catch:{ Exception -> 0x0aa5 }
        if (r9 == 0) goto L_0x0b8f;
    L_0x0b68:
        if (r42 == 0) goto L_0x0b8f;
    L_0x0b6a:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGifExternal;	 Catch:{ Exception -> 0x0aa5 }
        r4.<init>();	 Catch:{ Exception -> 0x0aa5 }
        r8 = "url";
        r10 = r42;
        r8 = r10.get(r8);	 Catch:{ Exception -> 0x0aa5 }
        r8 = (java.lang.String) r8;	 Catch:{ Exception -> 0x0aa5 }
        r9 = "\\|";
        r8 = r8.split(r9);	 Catch:{ Exception -> 0x0aa5 }
        r9 = r8.length;	 Catch:{ Exception -> 0x0aa5 }
        r10 = 2;
        if (r9 != r10) goto L_0x0b8d;
    L_0x0b83:
        r9 = 0;
        r10 = r8[r9];	 Catch:{ Exception -> 0x0aa5 }
        r4.url = r10;	 Catch:{ Exception -> 0x0aa5 }
        r9 = 1;
        r8 = r8[r9];	 Catch:{ Exception -> 0x0aa5 }
        r4.q = r8;	 Catch:{ Exception -> 0x0aa5 }
    L_0x0b8d:
        r10 = 1;
        goto L_0x0bc3;
    L_0x0b8f:
        r10 = r42;
        r9 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0c3f }
        r9.<init>();	 Catch:{ Exception -> 0x0c3f }
        if (r8 == 0) goto L_0x0ba2;
    L_0x0b98:
        r9.ttl_seconds = r8;	 Catch:{ Exception -> 0x0aa5 }
        r7.ttl = r8;	 Catch:{ Exception -> 0x0aa5 }
        r8 = r9.flags;	 Catch:{ Exception -> 0x0aa5 }
        r11 = 2;
        r8 = r8 | r11;
        r9.flags = r8;	 Catch:{ Exception -> 0x0aa5 }
    L_0x0ba2:
        r8 = android.text.TextUtils.isEmpty(r50);	 Catch:{ Exception -> 0x0c3f }
        if (r8 != 0) goto L_0x0bc1;
    L_0x0ba8:
        r4 = r50.toLowerCase();	 Catch:{ Exception -> 0x0aa5 }
        r8 = "mp4";
        r4 = r4.endsWith(r8);	 Catch:{ Exception -> 0x0aa5 }
        if (r4 == 0) goto L_0x0bc1;
    L_0x0bb4:
        if (r10 == 0) goto L_0x0bbe;
    L_0x0bb6:
        r4 = "forceDocument";
        r4 = r10.containsKey(r4);	 Catch:{ Exception -> 0x0aa5 }
        if (r4 == 0) goto L_0x0bc1;
    L_0x0bbe:
        r4 = 1;
        r9.nosound_video = r4;	 Catch:{ Exception -> 0x0aa5 }
    L_0x0bc1:
        r4 = r9;
        r10 = 0;
    L_0x0bc3:
        r8 = r12.mime_type;	 Catch:{ Exception -> 0x0c3f }
        r4.mime_type = r8;	 Catch:{ Exception -> 0x0c3f }
        r8 = r12.attributes;	 Catch:{ Exception -> 0x0c3f }
        r4.attributes = r8;	 Catch:{ Exception -> 0x0c3f }
    L_0x0bcb:
        r8 = r12.access_hash;	 Catch:{ Exception -> 0x0c3f }
        r11 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r11 != 0) goto L_0x0bda;
    L_0x0bd1:
        r8 = r4 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0aa5 }
        r52 = r6;
        r11 = r7;
        r6 = r8;
        r7 = r14;
        r8 = r4;
        goto L_0x0c0a;
    L_0x0bda:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0c3f }
        r8.<init>();	 Catch:{ Exception -> 0x0c3f }
        r9 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0c3f }
        r9.<init>();	 Catch:{ Exception -> 0x0c3f }
        r8.id = r9;	 Catch:{ Exception -> 0x0c3f }
        r9 = r8.id;	 Catch:{ Exception -> 0x0c3f }
        r52 = r6;
        r11 = r7;
        r6 = r12.id;	 Catch:{ Exception -> 0x0c3d }
        r9.id = r6;	 Catch:{ Exception -> 0x0c3d }
        r6 = r8.id;	 Catch:{ Exception -> 0x0c3d }
        r7 = r14;
        r13 = r12.access_hash;	 Catch:{ Exception -> 0x0f1f }
        r6.access_hash = r13;	 Catch:{ Exception -> 0x0f1f }
        r6 = r8.id;	 Catch:{ Exception -> 0x0f1f }
        r9 = r12.file_reference;	 Catch:{ Exception -> 0x0f1f }
        r6.file_reference = r9;	 Catch:{ Exception -> 0x0f1f }
        r6 = r8.id;	 Catch:{ Exception -> 0x0f1f }
        r6 = r6.file_reference;	 Catch:{ Exception -> 0x0f1f }
        if (r6 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r6 = r8.id;	 Catch:{ Exception -> 0x0f1f }
        r9 = 0;
        r13 = new byte[r9];	 Catch:{ Exception -> 0x0f1f }
        r6.file_reference = r13;	 Catch:{ Exception -> 0x0f1f }
    L_0x0CLASSNAME:
        r6 = 0;
    L_0x0c0a:
        if (r10 != 0) goto L_0x0CLASSNAME;
    L_0x0c0c:
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0c0e:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0f1f }
        r10.<init>(r2);	 Catch:{ Exception -> 0x0f1f }
        r10.originalPath = r15;	 Catch:{ Exception -> 0x0f1f }
        r9 = 2;
        r10.type = r9;	 Catch:{ Exception -> 0x0f1f }
        r10.obj = r7;	 Catch:{ Exception -> 0x0f1f }
        r9 = r12.thumbs;	 Catch:{ Exception -> 0x0f1f }
        r9 = r9.isEmpty();	 Catch:{ Exception -> 0x0f1f }
        if (r9 != 0) goto L_0x0c2f;
    L_0x0CLASSNAME:
        r9 = r12.thumbs;	 Catch:{ Exception -> 0x0f1f }
        r13 = 0;
        r9 = r9.get(r13);	 Catch:{ Exception -> 0x0f1f }
        r9 = (org.telegram.tgnet.TLRPC.PhotoSize) r9;	 Catch:{ Exception -> 0x0f1f }
        r10.photoSize = r9;	 Catch:{ Exception -> 0x0f1f }
        r10.locationParent = r12;	 Catch:{ Exception -> 0x0f1f }
    L_0x0c2f:
        r10.parentObject = r5;	 Catch:{ Exception -> 0x0f1f }
        r10.inputUploadMedia = r4;	 Catch:{ Exception -> 0x0f1f }
        r10.performMediaUpload = r6;	 Catch:{ Exception -> 0x0f1f }
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r10 = r51;
    L_0x0CLASSNAME:
        r18 = r6;
        r4 = r8;
        goto L_0x0d1c;
    L_0x0c3d:
        r0 = move-exception;
        goto L_0x0CLASSNAME;
    L_0x0c3f:
        r0 = move-exception;
        r11 = r7;
    L_0x0CLASSNAME:
        r7 = r14;
        r2 = r0;
        goto L_0x0var_;
    L_0x0CLASSNAME:
        r4 = r50;
        r26 = r5;
        r51 = r10;
        r19 = r11;
        r15 = r21;
        r8 = r25;
        r10 = r42;
        r5 = r59;
        r11 = r7;
        r7 = r52;
        r52 = r6;
        r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x0f1f }
        r6.<init>();	 Catch:{ Exception -> 0x0f1f }
        if (r8 == 0) goto L_0x0c6c;
    L_0x0CLASSNAME:
        r6.ttl_seconds = r8;	 Catch:{ Exception -> 0x0f1f }
        r11.ttl = r8;	 Catch:{ Exception -> 0x0f1f }
        r8 = r6.flags;	 Catch:{ Exception -> 0x0f1f }
        r9 = 2;
        r8 = r8 | r9;
        r6.flags = r8;	 Catch:{ Exception -> 0x0f1f }
        goto L_0x0c6d;
    L_0x0c6c:
        r9 = 2;
    L_0x0c6d:
        if (r10 == 0) goto L_0x0ca5;
    L_0x0c6f:
        r8 = "masks";
        r8 = r10.get(r8);	 Catch:{ Exception -> 0x0f1f }
        r8 = (java.lang.String) r8;	 Catch:{ Exception -> 0x0f1f }
        if (r8 == 0) goto L_0x0ca5;
    L_0x0CLASSNAME:
        r10 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0f1f }
        r8 = org.telegram.messenger.Utilities.hexToBytes(r8);	 Catch:{ Exception -> 0x0f1f }
        r10.<init>(r8);	 Catch:{ Exception -> 0x0f1f }
        r8 = 0;
        r12 = r10.readInt32(r8);	 Catch:{ Exception -> 0x0f1f }
        r13 = 0;
    L_0x0CLASSNAME:
        if (r13 >= r12) goto L_0x0c9c;
    L_0x0c8a:
        r14 = r6.stickers;	 Catch:{ Exception -> 0x0f1f }
        r9 = r10.readInt32(r8);	 Catch:{ Exception -> 0x0f1f }
        r9 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r10, r9, r8);	 Catch:{ Exception -> 0x0f1f }
        r14.add(r9);	 Catch:{ Exception -> 0x0f1f }
        r13 = r13 + 1;
        r8 = 0;
        r9 = 2;
        goto L_0x0CLASSNAME;
    L_0x0c9c:
        r8 = r6.flags;	 Catch:{ Exception -> 0x0f1f }
        r9 = 1;
        r8 = r8 | r9;
        r6.flags = r8;	 Catch:{ Exception -> 0x0f1f }
        r10.cleanup();	 Catch:{ Exception -> 0x0f1f }
    L_0x0ca5:
        r14 = r44;
        r8 = r14.access_hash;	 Catch:{ Exception -> 0x0f1f }
        r10 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1));
        if (r10 != 0) goto L_0x0cb0;
    L_0x0cad:
        r8 = r6;
        r10 = 1;
        goto L_0x0cdc;
    L_0x0cb0:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x0f1f }
        r8.<init>();	 Catch:{ Exception -> 0x0f1f }
        r9 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x0f1f }
        r9.<init>();	 Catch:{ Exception -> 0x0f1f }
        r8.id = r9;	 Catch:{ Exception -> 0x0f1f }
        r9 = r8.id;	 Catch:{ Exception -> 0x0f1f }
        r12 = r14.id;	 Catch:{ Exception -> 0x0f1f }
        r9.id = r12;	 Catch:{ Exception -> 0x0f1f }
        r9 = r8.id;	 Catch:{ Exception -> 0x0f1f }
        r12 = r14.access_hash;	 Catch:{ Exception -> 0x0f1f }
        r9.access_hash = r12;	 Catch:{ Exception -> 0x0f1f }
        r9 = r8.id;	 Catch:{ Exception -> 0x0f1f }
        r10 = r14.file_reference;	 Catch:{ Exception -> 0x0f1f }
        r9.file_reference = r10;	 Catch:{ Exception -> 0x0f1f }
        r9 = r8.id;	 Catch:{ Exception -> 0x0f1f }
        r9 = r9.file_reference;	 Catch:{ Exception -> 0x0f1f }
        if (r9 != 0) goto L_0x0cdb;
    L_0x0cd4:
        r9 = r8.id;	 Catch:{ Exception -> 0x0f1f }
        r10 = 0;
        r12 = new byte[r10];	 Catch:{ Exception -> 0x0f1f }
        r9.file_reference = r12;	 Catch:{ Exception -> 0x0f1f }
    L_0x0cdb:
        r10 = 0;
    L_0x0cdc:
        if (r51 != 0) goto L_0x0ceb;
    L_0x0cde:
        r9 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0f1f }
        r9.<init>(r2);	 Catch:{ Exception -> 0x0f1f }
        r12 = 0;
        r9.type = r12;	 Catch:{ Exception -> 0x0f1f }
        r9.obj = r7;	 Catch:{ Exception -> 0x0f1f }
        r9.originalPath = r15;	 Catch:{ Exception -> 0x0f1f }
        goto L_0x0ced;
    L_0x0ceb:
        r9 = r51;
    L_0x0ced:
        r9.inputUploadMedia = r6;	 Catch:{ Exception -> 0x0f1f }
        r9.performMediaUpload = r10;	 Catch:{ Exception -> 0x0f1f }
        if (r4 == 0) goto L_0x0d04;
    L_0x0cf3:
        r6 = r50.length();	 Catch:{ Exception -> 0x0f1f }
        if (r6 <= 0) goto L_0x0d04;
    L_0x0cf9:
        r6 = "http";
        r6 = r4.startsWith(r6);	 Catch:{ Exception -> 0x0f1f }
        if (r6 == 0) goto L_0x0d04;
    L_0x0d01:
        r9.httpLocation = r4;	 Catch:{ Exception -> 0x0f1f }
        goto L_0x0d18;
    L_0x0d04:
        r4 = r14.sizes;	 Catch:{ Exception -> 0x0f1f }
        r6 = r14.sizes;	 Catch:{ Exception -> 0x0f1f }
        r6 = r6.size();	 Catch:{ Exception -> 0x0f1f }
        r12 = 1;
        r6 = r6 - r12;
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0f1f }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0f1f }
        r9.photoSize = r4;	 Catch:{ Exception -> 0x0f1f }
        r9.locationParent = r14;	 Catch:{ Exception -> 0x0f1f }
    L_0x0d18:
        r4 = r8;
        r18 = r10;
    L_0x0d1b:
        r10 = r9;
    L_0x0d1c:
        if (r39 == 0) goto L_0x0d61;
    L_0x0d1e:
        r6 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x0f1f }
        r6.<init>();	 Catch:{ Exception -> 0x0f1f }
        r8 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0f1f }
        r8.<init>();	 Catch:{ Exception -> 0x0f1f }
        r9 = 0;
    L_0x0d29:
        r12 = r39.size();	 Catch:{ Exception -> 0x0f1f }
        if (r9 >= r12) goto L_0x0d3f;
    L_0x0d2f:
        r12 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0f1f }
        r12 = r12.nextLong();	 Catch:{ Exception -> 0x0f1f }
        r12 = java.lang.Long.valueOf(r12);	 Catch:{ Exception -> 0x0f1f }
        r8.add(r12);	 Catch:{ Exception -> 0x0f1f }
        r9 = r9 + 1;
        goto L_0x0d29;
    L_0x0d3f:
        r12 = r39;
        r6.contacts = r12;	 Catch:{ Exception -> 0x0f1f }
        r6.media = r4;	 Catch:{ Exception -> 0x0f1f }
        r6.random_id = r8;	 Catch:{ Exception -> 0x0f1f }
        r4 = r19;
        r6.message = r4;	 Catch:{ Exception -> 0x0f1f }
        if (r10 == 0) goto L_0x0d4f;
    L_0x0d4d:
        r10.sendRequest = r6;	 Catch:{ Exception -> 0x0f1f }
    L_0x0d4f:
        r4 = r54;
        r8 = 2;
        if (r4 != 0) goto L_0x0d5e;
    L_0x0d54:
        r4 = r1.currentAccount;	 Catch:{ Exception -> 0x0f1f }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0f1f }
        r9 = 0;
        r4.cleanDraft(r2, r9);	 Catch:{ Exception -> 0x0f1f }
    L_0x0d5e:
        r12 = r6;
        goto L_0x0e61;
    L_0x0d61:
        r8 = 2;
        r6 = (r56 > r16 ? 1 : (r56 == r16 ? 0 : -1));
        if (r6 == 0) goto L_0x0e05;
    L_0x0d66:
        r6 = r10.sendRequest;	 Catch:{ Exception -> 0x0f1f }
        if (r6 == 0) goto L_0x0d6f;
    L_0x0d6a:
        r2 = r10.sendRequest;	 Catch:{ Exception -> 0x0f1f }
        r2 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r2;	 Catch:{ Exception -> 0x0f1f }
        goto L_0x0dad;
    L_0x0d6f:
        r6 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;	 Catch:{ Exception -> 0x0f1f }
        r6.<init>();	 Catch:{ Exception -> 0x0f1f }
        r13 = r26;
        r6.peer = r13;	 Catch:{ Exception -> 0x0f1f }
        r9 = r11.to_id;	 Catch:{ Exception -> 0x0f1f }
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0f1f }
        if (r9 == 0) goto L_0x0d9c;
    L_0x0d7e:
        r9 = r1.currentAccount;	 Catch:{ Exception -> 0x0f1f }
        r9 = org.telegram.messenger.MessagesController.getNotificationsSettings(r9);	 Catch:{ Exception -> 0x0f1f }
        r12 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0f1f }
        r12.<init>();	 Catch:{ Exception -> 0x0f1f }
        r13 = "silent_";
        r12.append(r13);	 Catch:{ Exception -> 0x0f1f }
        r12.append(r2);	 Catch:{ Exception -> 0x0f1f }
        r2 = r12.toString();	 Catch:{ Exception -> 0x0f1f }
        r3 = 0;
        r2 = r9.getBoolean(r2, r3);	 Catch:{ Exception -> 0x0f1f }
        r6.silent = r2;	 Catch:{ Exception -> 0x0f1f }
    L_0x0d9c:
        r2 = r11.reply_to_msg_id;	 Catch:{ Exception -> 0x0f1f }
        if (r2 == 0) goto L_0x0daa;
    L_0x0da0:
        r2 = r6.flags;	 Catch:{ Exception -> 0x0f1f }
        r3 = 1;
        r2 = r2 | r3;
        r6.flags = r2;	 Catch:{ Exception -> 0x0f1f }
        r2 = r11.reply_to_msg_id;	 Catch:{ Exception -> 0x0f1f }
        r6.reply_to_msg_id = r2;	 Catch:{ Exception -> 0x0f1f }
    L_0x0daa:
        r10.sendRequest = r6;	 Catch:{ Exception -> 0x0f1f }
        r2 = r6;
    L_0x0dad:
        r3 = r10.messageObjects;	 Catch:{ Exception -> 0x0f1f }
        r3.add(r7);	 Catch:{ Exception -> 0x0f1f }
        r3 = r10.parentObjects;	 Catch:{ Exception -> 0x0f1f }
        r3.add(r5);	 Catch:{ Exception -> 0x0f1f }
        r3 = r10.locations;	 Catch:{ Exception -> 0x0f1f }
        r6 = r10.photoSize;	 Catch:{ Exception -> 0x0f1f }
        r3.add(r6);	 Catch:{ Exception -> 0x0f1f }
        r3 = r10.videoEditedInfos;	 Catch:{ Exception -> 0x0f1f }
        r6 = r10.videoEditedInfo;	 Catch:{ Exception -> 0x0f1f }
        r3.add(r6);	 Catch:{ Exception -> 0x0f1f }
        r3 = r10.httpLocations;	 Catch:{ Exception -> 0x0f1f }
        r6 = r10.httpLocation;	 Catch:{ Exception -> 0x0f1f }
        r3.add(r6);	 Catch:{ Exception -> 0x0f1f }
        r3 = r10.inputMedias;	 Catch:{ Exception -> 0x0f1f }
        r6 = r10.inputUploadMedia;	 Catch:{ Exception -> 0x0f1f }
        r3.add(r6);	 Catch:{ Exception -> 0x0f1f }
        r3 = r10.messages;	 Catch:{ Exception -> 0x0f1f }
        r3.add(r11);	 Catch:{ Exception -> 0x0f1f }
        r3 = r10.originalPaths;	 Catch:{ Exception -> 0x0f1f }
        r3.add(r15);	 Catch:{ Exception -> 0x0f1f }
        r3 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia;	 Catch:{ Exception -> 0x0f1f }
        r3.<init>();	 Catch:{ Exception -> 0x0f1f }
        r12 = r11.random_id;	 Catch:{ Exception -> 0x0f1f }
        r3.random_id = r12;	 Catch:{ Exception -> 0x0f1f }
        r3.media = r4;	 Catch:{ Exception -> 0x0f1f }
        r6 = r46;
        r3.message = r6;	 Catch:{ Exception -> 0x0f1f }
        r9 = r55;
        if (r9 == 0) goto L_0x0dfe;
    L_0x0df0:
        r4 = r55.isEmpty();	 Catch:{ Exception -> 0x0f1f }
        if (r4 != 0) goto L_0x0dfe;
    L_0x0df6:
        r3.entities = r9;	 Catch:{ Exception -> 0x0f1f }
        r4 = r3.flags;	 Catch:{ Exception -> 0x0f1f }
        r6 = 1;
        r4 = r4 | r6;
        r3.flags = r4;	 Catch:{ Exception -> 0x0f1f }
    L_0x0dfe:
        r4 = r2.multi_media;	 Catch:{ Exception -> 0x0f1f }
        r4.add(r3);	 Catch:{ Exception -> 0x0f1f }
        r12 = r2;
        goto L_0x0e61;
    L_0x0e05:
        r6 = r46;
        r9 = r55;
        r13 = r26;
        r12 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia;	 Catch:{ Exception -> 0x0f1f }
        r12.<init>();	 Catch:{ Exception -> 0x0f1f }
        r12.peer = r13;	 Catch:{ Exception -> 0x0f1f }
        r13 = r11.to_id;	 Catch:{ Exception -> 0x0f1f }
        r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0f1f }
        if (r13 == 0) goto L_0x0e36;
    L_0x0e18:
        r13 = r1.currentAccount;	 Catch:{ Exception -> 0x0f1f }
        r13 = org.telegram.messenger.MessagesController.getNotificationsSettings(r13);	 Catch:{ Exception -> 0x0f1f }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0f1f }
        r14.<init>();	 Catch:{ Exception -> 0x0f1f }
        r8 = "silent_";
        r14.append(r8);	 Catch:{ Exception -> 0x0f1f }
        r14.append(r2);	 Catch:{ Exception -> 0x0f1f }
        r2 = r14.toString();	 Catch:{ Exception -> 0x0f1f }
        r3 = 0;
        r2 = r13.getBoolean(r2, r3);	 Catch:{ Exception -> 0x0f1f }
        r12.silent = r2;	 Catch:{ Exception -> 0x0f1f }
    L_0x0e36:
        r2 = r11.reply_to_msg_id;	 Catch:{ Exception -> 0x0f1f }
        if (r2 == 0) goto L_0x0e44;
    L_0x0e3a:
        r2 = r12.flags;	 Catch:{ Exception -> 0x0f1f }
        r3 = 1;
        r2 = r2 | r3;
        r12.flags = r2;	 Catch:{ Exception -> 0x0f1f }
        r2 = r11.reply_to_msg_id;	 Catch:{ Exception -> 0x0f1f }
        r12.reply_to_msg_id = r2;	 Catch:{ Exception -> 0x0f1f }
    L_0x0e44:
        r2 = r11.random_id;	 Catch:{ Exception -> 0x0f1f }
        r12.random_id = r2;	 Catch:{ Exception -> 0x0f1f }
        r12.media = r4;	 Catch:{ Exception -> 0x0f1f }
        r12.message = r6;	 Catch:{ Exception -> 0x0f1f }
        if (r9 == 0) goto L_0x0e5d;
    L_0x0e4e:
        r2 = r55.isEmpty();	 Catch:{ Exception -> 0x0f1f }
        if (r2 != 0) goto L_0x0e5d;
    L_0x0e54:
        r12.entities = r9;	 Catch:{ Exception -> 0x0f1f }
        r2 = r12.flags;	 Catch:{ Exception -> 0x0f1f }
        r3 = 8;
        r2 = r2 | r3;
        r12.flags = r2;	 Catch:{ Exception -> 0x0f1f }
    L_0x0e5d:
        if (r10 == 0) goto L_0x0e61;
    L_0x0e5f:
        r10.sendRequest = r12;	 Catch:{ Exception -> 0x0f1f }
    L_0x0e61:
        r2 = (r56 > r16 ? 1 : (r56 == r16 ? 0 : -1));
        if (r2 == 0) goto L_0x0e6a;
    L_0x0e65:
        r1.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0e6a:
        r2 = r52;
        r3 = 1;
        if (r2 != r3) goto L_0x0e81;
    L_0x0e6f:
        r2 = 0;
        r39 = r38;
        r40 = r12;
        r41 = r7;
        r42 = r2;
        r43 = r10;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0e81:
        r3 = 2;
        if (r2 != r3) goto L_0x0ea2;
    L_0x0e84:
        if (r18 == 0) goto L_0x0e8b;
    L_0x0e86:
        r1.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0e8b:
        r2 = 0;
        r4 = 1;
        r39 = r38;
        r40 = r12;
        r41 = r7;
        r42 = r15;
        r43 = r2;
        r44 = r4;
        r45 = r10;
        r46 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44, r45, r46);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0ea2:
        r4 = 3;
        if (r2 != r4) goto L_0x0ebd;
    L_0x0ea5:
        if (r18 == 0) goto L_0x0eac;
    L_0x0ea7:
        r1.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0eac:
        r39 = r38;
        r40 = r12;
        r41 = r7;
        r42 = r15;
        r43 = r10;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0ebd:
        r4 = 6;
        if (r2 != r4) goto L_0x0ed1;
    L_0x0ec0:
        r39 = r38;
        r40 = r12;
        r41 = r7;
        r42 = r15;
        r43 = r10;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0ed1:
        r4 = 7;
        if (r2 != r4) goto L_0x0eee;
    L_0x0ed4:
        if (r18 == 0) goto L_0x0edd;
    L_0x0ed6:
        if (r10 == 0) goto L_0x0edd;
    L_0x0ed8:
        r1.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0edd:
        r39 = r38;
        r40 = r12;
        r41 = r7;
        r42 = r15;
        r43 = r10;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0eee:
        r4 = 8;
        if (r2 != r4) goto L_0x0f0a;
    L_0x0ef2:
        if (r18 == 0) goto L_0x0ef9;
    L_0x0ef4:
        r1.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0ef9:
        r39 = r38;
        r40 = r12;
        r41 = r7;
        r42 = r15;
        r43 = r10;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0f0a:
        r4 = 10;
        if (r2 != r4) goto L_0x17ff;
    L_0x0f0e:
        r39 = r38;
        r40 = r12;
        r41 = r7;
        r42 = r15;
        r43 = r10;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x0f1f }
        goto L_0x17ff;
    L_0x0f1f:
        r0 = move-exception;
        r2 = r0;
        r14 = r7;
    L_0x0var_:
        r7 = r11;
        goto L_0x17d0;
    L_0x0var_:
        r14 = r44;
        r4 = r55;
        r13 = r6;
        r11 = r7;
        r51 = r10;
        r12 = r19;
        r36 = r23;
        r5 = r26;
        r10 = r42;
        r6 = r46;
        r7 = r52;
        r15 = r9.layer;	 Catch:{ Exception -> 0x14e4 }
        r15 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r15);	 Catch:{ Exception -> 0x14e4 }
        r2 = 73;
        if (r15 < r2) goto L_0x0var_;
    L_0x0var_:
        r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x0f1f }
        r2.<init>();	 Catch:{ Exception -> 0x0f1f }
        r3 = (r56 > r16 ? 1 : (r56 == r16 ? 0 : -1));
        if (r3 == 0) goto L_0x0f5b;
    L_0x0f4c:
        r44 = r14;
        r14 = r56;
        r2.grouped_id = r14;	 Catch:{ Exception -> 0x0f1f }
        r3 = r2.flags;	 Catch:{ Exception -> 0x0f1f }
        r18 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r3 = r3 | r18;
        r2.flags = r3;	 Catch:{ Exception -> 0x0f1f }
        goto L_0x0var_;
    L_0x0f5b:
        r44 = r14;
        r14 = r56;
        goto L_0x0var_;
    L_0x0var_:
        r44 = r14;
        r14 = r56;
        r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x14e4 }
        r2.<init>();	 Catch:{ Exception -> 0x14e4 }
    L_0x0var_:
        r3 = r11.ttl;	 Catch:{ Exception -> 0x14e4 }
        r2.ttl = r3;	 Catch:{ Exception -> 0x14e4 }
        if (r4 == 0) goto L_0x0f7d;
    L_0x0f6f:
        r3 = r55.isEmpty();	 Catch:{ Exception -> 0x0f1f }
        if (r3 != 0) goto L_0x0f7d;
    L_0x0var_:
        r2.entities = r4;	 Catch:{ Exception -> 0x0f1f }
        r3 = r2.flags;	 Catch:{ Exception -> 0x0f1f }
        r3 = r3 | 128;
        r2.flags = r3;	 Catch:{ Exception -> 0x0f1f }
    L_0x0f7d:
        r3 = r11.reply_to_random_id;	 Catch:{ Exception -> 0x14e4 }
        r18 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1));
        if (r18 == 0) goto L_0x0f8e;
    L_0x0var_:
        r3 = r11.reply_to_random_id;	 Catch:{ Exception -> 0x0f1f }
        r2.reply_to_random_id = r3;	 Catch:{ Exception -> 0x0f1f }
        r3 = r2.flags;	 Catch:{ Exception -> 0x0f1f }
        r4 = 8;
        r3 = r3 | r4;
        r2.flags = r3;	 Catch:{ Exception -> 0x0f1f }
    L_0x0f8e:
        r3 = r2.flags;	 Catch:{ Exception -> 0x14e4 }
        r3 = r3 | 512;
        r2.flags = r3;	 Catch:{ Exception -> 0x14e4 }
        if (r10 == 0) goto L_0x0fac;
    L_0x0var_:
        r3 = r41;
        r4 = r10.get(r3);	 Catch:{ Exception -> 0x0f1f }
        if (r4 == 0) goto L_0x0fac;
    L_0x0f9e:
        r3 = r10.get(r3);	 Catch:{ Exception -> 0x0f1f }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x0f1f }
        r2.via_bot_name = r3;	 Catch:{ Exception -> 0x0f1f }
        r3 = r2.flags;	 Catch:{ Exception -> 0x0f1f }
        r3 = r3 | 2048;
        r2.flags = r3;	 Catch:{ Exception -> 0x0f1f }
    L_0x0fac:
        r3 = r11.random_id;	 Catch:{ Exception -> 0x14e4 }
        r2.random_id = r3;	 Catch:{ Exception -> 0x14e4 }
        r2.message = r5;	 Catch:{ Exception -> 0x14e4 }
        r3 = 1;
        if (r13 != r3) goto L_0x1015;
    L_0x0fb5:
        r3 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x0f1f }
        if (r3 == 0) goto L_0x0fd9;
    L_0x0fb9:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue;	 Catch:{ Exception -> 0x0f1f }
        r3.<init>();	 Catch:{ Exception -> 0x0f1f }
        r2.media = r3;	 Catch:{ Exception -> 0x0f1f }
        r3 = r2.media;	 Catch:{ Exception -> 0x0f1f }
        r4 = r8.address;	 Catch:{ Exception -> 0x0f1f }
        r3.address = r4;	 Catch:{ Exception -> 0x0f1f }
        r3 = r2.media;	 Catch:{ Exception -> 0x0f1f }
        r4 = r8.title;	 Catch:{ Exception -> 0x0f1f }
        r3.title = r4;	 Catch:{ Exception -> 0x0f1f }
        r3 = r2.media;	 Catch:{ Exception -> 0x0f1f }
        r4 = r8.provider;	 Catch:{ Exception -> 0x0f1f }
        r3.provider = r4;	 Catch:{ Exception -> 0x0f1f }
        r3 = r2.media;	 Catch:{ Exception -> 0x0f1f }
        r4 = r8.venue_id;	 Catch:{ Exception -> 0x0f1f }
        r3.venue_id = r4;	 Catch:{ Exception -> 0x0f1f }
        goto L_0x0fe0;
    L_0x0fd9:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint;	 Catch:{ Exception -> 0x0f1f }
        r3.<init>();	 Catch:{ Exception -> 0x0f1f }
        r2.media = r3;	 Catch:{ Exception -> 0x0f1f }
    L_0x0fe0:
        r3 = r2.media;	 Catch:{ Exception -> 0x0f1f }
        r4 = r8.geo;	 Catch:{ Exception -> 0x0f1f }
        r4 = r4.lat;	 Catch:{ Exception -> 0x0f1f }
        r3.lat = r4;	 Catch:{ Exception -> 0x0f1f }
        r3 = r2.media;	 Catch:{ Exception -> 0x0f1f }
        r4 = r8.geo;	 Catch:{ Exception -> 0x0f1f }
        r4 = r4._long;	 Catch:{ Exception -> 0x0f1f }
        r3._long = r4;	 Catch:{ Exception -> 0x0f1f }
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x0f1f }
        r3 = org.telegram.messenger.SecretChatHelper.getInstance(r3);	 Catch:{ Exception -> 0x0f1f }
        r4 = r7.messageOwner;	 Catch:{ Exception -> 0x0f1f }
        r5 = 0;
        r6 = 0;
        r41 = r3;
        r42 = r2;
        r43 = r4;
        r44 = r9;
        r45 = r5;
        r46 = r6;
        r47 = r7;
        r41.performSendEncryptedRequest(r42, r43, r44, r45, r46, r47);	 Catch:{ Exception -> 0x0f1f }
        r4 = r48;
        r19 = r11;
        r56 = r14;
        r8 = r21;
        goto L_0x12a8;
    L_0x1015:
        r3 = "parentObject";
        r4 = 2;
        if (r13 == r4) goto L_0x1373;
    L_0x101a:
        r4 = 9;
        if (r13 != r4) goto L_0x1030;
    L_0x101e:
        if (r44 == 0) goto L_0x1030;
    L_0x1020:
        r4 = r48;
        r12 = r50;
        r19 = r11;
        r56 = r14;
        r8 = r21;
        r14 = r44;
        r11 = r59;
        goto L_0x1381;
    L_0x1030:
        r4 = 3;
        if (r13 != r4) goto L_0x116f;
    L_0x1033:
        r4 = r12.thumbs;	 Catch:{ Exception -> 0x1166 }
        r4 = r1.getThumbForSecretChat(r4);	 Catch:{ Exception -> 0x1166 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r4);	 Catch:{ Exception -> 0x1166 }
        r5 = org.telegram.messenger.MessageObject.isNewGifDocument(r12);	 Catch:{ Exception -> 0x1166 }
        if (r5 != 0) goto L_0x106d;
    L_0x1042:
        r5 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r12);	 Catch:{ Exception -> 0x1166 }
        if (r5 == 0) goto L_0x1049;
    L_0x1048:
        goto L_0x106d;
    L_0x1049:
        r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo;	 Catch:{ Exception -> 0x1166 }
        r5.<init>();	 Catch:{ Exception -> 0x1166 }
        r2.media = r5;	 Catch:{ Exception -> 0x1166 }
        if (r4 == 0) goto L_0x1061;
    L_0x1052:
        r5 = r4.bytes;	 Catch:{ Exception -> 0x0f1f }
        if (r5 == 0) goto L_0x1061;
    L_0x1056:
        r5 = r2.media;	 Catch:{ Exception -> 0x0f1f }
        r5 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r5;	 Catch:{ Exception -> 0x0f1f }
        r8 = r4.bytes;	 Catch:{ Exception -> 0x0f1f }
        r5.thumb = r8;	 Catch:{ Exception -> 0x0f1f }
        r19 = r11;
        goto L_0x1094;
    L_0x1061:
        r5 = r2.media;	 Catch:{ Exception -> 0x1166 }
        r5 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r5;	 Catch:{ Exception -> 0x1166 }
        r19 = r11;
        r8 = 0;
        r11 = new byte[r8];	 Catch:{ Exception -> 0x11ba }
        r5.thumb = r11;	 Catch:{ Exception -> 0x11ba }
        goto L_0x1094;
    L_0x106d:
        r19 = r11;
        r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x11ba }
        r5.<init>();	 Catch:{ Exception -> 0x11ba }
        r2.media = r5;	 Catch:{ Exception -> 0x11ba }
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r8 = r12.attributes;	 Catch:{ Exception -> 0x11ba }
        r5.attributes = r8;	 Catch:{ Exception -> 0x11ba }
        if (r4 == 0) goto L_0x108b;
    L_0x107e:
        r5 = r4.bytes;	 Catch:{ Exception -> 0x11ba }
        if (r5 == 0) goto L_0x108b;
    L_0x1082:
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r5 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r5;	 Catch:{ Exception -> 0x11ba }
        r8 = r4.bytes;	 Catch:{ Exception -> 0x11ba }
        r5.thumb = r8;	 Catch:{ Exception -> 0x11ba }
        goto L_0x1094;
    L_0x108b:
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r5 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r5;	 Catch:{ Exception -> 0x11ba }
        r8 = 0;
        r11 = new byte[r8];	 Catch:{ Exception -> 0x11ba }
        r5.thumb = r11;	 Catch:{ Exception -> 0x11ba }
    L_0x1094:
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r5.caption = r6;	 Catch:{ Exception -> 0x11ba }
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = "video/mp4";
        r5.mime_type = r6;	 Catch:{ Exception -> 0x11ba }
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.size;	 Catch:{ Exception -> 0x11ba }
        r5.size = r6;	 Catch:{ Exception -> 0x11ba }
        r5 = 0;
    L_0x10a5:
        r6 = r12.attributes;	 Catch:{ Exception -> 0x11ba }
        r6 = r6.size();	 Catch:{ Exception -> 0x11ba }
        if (r5 >= r6) goto L_0x10cf;
    L_0x10ad:
        r6 = r12.attributes;	 Catch:{ Exception -> 0x11ba }
        r6 = r6.get(r5);	 Catch:{ Exception -> 0x11ba }
        r6 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r6;	 Catch:{ Exception -> 0x11ba }
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x11ba }
        if (r8 == 0) goto L_0x10cc;
    L_0x10b9:
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r8 = r6.w;	 Catch:{ Exception -> 0x11ba }
        r5.w = r8;	 Catch:{ Exception -> 0x11ba }
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r8 = r6.h;	 Catch:{ Exception -> 0x11ba }
        r5.h = r8;	 Catch:{ Exception -> 0x11ba }
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r6.duration;	 Catch:{ Exception -> 0x11ba }
        r5.duration = r6;	 Catch:{ Exception -> 0x11ba }
        goto L_0x10cf;
    L_0x10cc:
        r5 = r5 + 1;
        goto L_0x10a5;
    L_0x10cf:
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r4.h;	 Catch:{ Exception -> 0x11ba }
        r5.thumb_h = r6;	 Catch:{ Exception -> 0x11ba }
        r5 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r4 = r4.w;	 Catch:{ Exception -> 0x11ba }
        r5.thumb_w = r4;	 Catch:{ Exception -> 0x11ba }
        r4 = r12.key;	 Catch:{ Exception -> 0x11ba }
        if (r4 == 0) goto L_0x111e;
    L_0x10df:
        r4 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r4 == 0) goto L_0x10e4;
    L_0x10e3:
        goto L_0x111e;
    L_0x10e4:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x11ba }
        r3.<init>();	 Catch:{ Exception -> 0x11ba }
        r4 = r12.id;	 Catch:{ Exception -> 0x11ba }
        r3.id = r4;	 Catch:{ Exception -> 0x11ba }
        r4 = r12.access_hash;	 Catch:{ Exception -> 0x11ba }
        r3.access_hash = r4;	 Catch:{ Exception -> 0x11ba }
        r4 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r5 = r12.key;	 Catch:{ Exception -> 0x11ba }
        r4.key = r5;	 Catch:{ Exception -> 0x11ba }
        r4 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r5 = r12.iv;	 Catch:{ Exception -> 0x11ba }
        r4.iv = r5;	 Catch:{ Exception -> 0x11ba }
        r4 = r1.currentAccount;	 Catch:{ Exception -> 0x11ba }
        r4 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x11ba }
        r5 = r7.messageOwner;	 Catch:{ Exception -> 0x11ba }
        r6 = 0;
        r41 = r4;
        r42 = r2;
        r43 = r5;
        r44 = r9;
        r45 = r3;
        r46 = r6;
        r47 = r7;
        r41.performSendEncryptedRequest(r42, r43, r44, r45, r46, r47);	 Catch:{ Exception -> 0x11ba }
        r5 = r48;
        r10 = r51;
        r8 = r21;
        goto L_0x115f;
    L_0x111e:
        if (r51 != 0) goto L_0x114d;
    L_0x1120:
        r4 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x11ba }
        r5 = r48;
        r4.<init>(r5);	 Catch:{ Exception -> 0x11ba }
        r4.encryptedChat = r9;	 Catch:{ Exception -> 0x11ba }
        r8 = 1;
        r4.type = r8;	 Catch:{ Exception -> 0x11ba }
        r4.sendEncryptedRequest = r2;	 Catch:{ Exception -> 0x11ba }
        r8 = r21;
        r4.originalPath = r8;	 Catch:{ Exception -> 0x11ba }
        r4.obj = r7;	 Catch:{ Exception -> 0x11ba }
        if (r10 == 0) goto L_0x1143;
    L_0x1136:
        r9 = r10.containsKey(r3);	 Catch:{ Exception -> 0x11ba }
        if (r9 == 0) goto L_0x1143;
    L_0x113c:
        r3 = r10.get(r3);	 Catch:{ Exception -> 0x11ba }
        r4.parentObject = r3;	 Catch:{ Exception -> 0x11ba }
        goto L_0x1147;
    L_0x1143:
        r11 = r59;
        r4.parentObject = r11;	 Catch:{ Exception -> 0x11ba }
    L_0x1147:
        r3 = 1;
        r4.performMediaUpload = r3;	 Catch:{ Exception -> 0x11ba }
        r10 = r43;
        goto L_0x1155;
    L_0x114d:
        r5 = r48;
        r8 = r21;
        r10 = r43;
        r4 = r51;
    L_0x1155:
        r4.videoEditedInfo = r10;	 Catch:{ Exception -> 0x11ba }
        r3 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r3 != 0) goto L_0x115e;
    L_0x115b:
        r1.performSendDelayedMessage(r4);	 Catch:{ Exception -> 0x11ba }
    L_0x115e:
        r10 = r4;
    L_0x115f:
        r4 = r5;
        r52 = r13;
        r56 = r14;
        goto L_0x1487;
    L_0x1166:
        r0 = move-exception;
        r19 = r11;
    L_0x1169:
        r2 = r0;
        r14 = r7;
        r7 = r19;
        goto L_0x17d0;
    L_0x116f:
        r4 = r48;
        r19 = r11;
        r56 = r14;
        r8 = r21;
        r11 = r59;
        r14 = 6;
        if (r13 != r14) goto L_0x11bc;
    L_0x117c:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact;	 Catch:{ Exception -> 0x11ba }
        r3.<init>();	 Catch:{ Exception -> 0x11ba }
        r2.media = r3;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r36;
        r10 = r6.phone;	 Catch:{ Exception -> 0x11ba }
        r3.phone_number = r10;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r10 = r6.first_name;	 Catch:{ Exception -> 0x11ba }
        r3.first_name = r10;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r10 = r6.last_name;	 Catch:{ Exception -> 0x11ba }
        r3.last_name = r10;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r6.id;	 Catch:{ Exception -> 0x11ba }
        r3.user_id = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x11ba }
        r3 = org.telegram.messenger.SecretChatHelper.getInstance(r3);	 Catch:{ Exception -> 0x11ba }
        r6 = r7.messageOwner;	 Catch:{ Exception -> 0x11ba }
        r10 = 0;
        r11 = 0;
        r41 = r3;
        r42 = r2;
        r43 = r6;
        r44 = r9;
        r45 = r10;
        r46 = r11;
        r47 = r7;
        r41.performSendEncryptedRequest(r42, r43, r44, r45, r46, r47);	 Catch:{ Exception -> 0x11ba }
        goto L_0x12a8;
    L_0x11ba:
        r0 = move-exception;
        goto L_0x1169;
    L_0x11bc:
        r14 = 7;
        if (r13 == r14) goto L_0x1232;
    L_0x11bf:
        r14 = 9;
        if (r13 != r14) goto L_0x11c6;
    L_0x11c3:
        if (r12 == 0) goto L_0x11c6;
    L_0x11c5:
        goto L_0x1232;
    L_0x11c6:
        r3 = 8;
        if (r13 != r3) goto L_0x12a8;
    L_0x11ca:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x11ba }
        r10.<init>(r4);	 Catch:{ Exception -> 0x11ba }
        r10.encryptedChat = r9;	 Catch:{ Exception -> 0x11ba }
        r10.sendEncryptedRequest = r2;	 Catch:{ Exception -> 0x11ba }
        r10.obj = r7;	 Catch:{ Exception -> 0x11ba }
        r3 = 3;
        r10.type = r3;	 Catch:{ Exception -> 0x11ba }
        r10.parentObject = r11;	 Catch:{ Exception -> 0x11ba }
        r3 = 1;
        r10.performMediaUpload = r3;	 Catch:{ Exception -> 0x11ba }
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x11ba }
        r3.<init>();	 Catch:{ Exception -> 0x11ba }
        r2.media = r3;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r9 = r12.attributes;	 Catch:{ Exception -> 0x11ba }
        r3.attributes = r9;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r3.caption = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r12.thumbs;	 Catch:{ Exception -> 0x11ba }
        r3 = r1.getThumbForSecretChat(r3);	 Catch:{ Exception -> 0x11ba }
        if (r3 == 0) goto L_0x120e;
    L_0x11f6:
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3);	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r6;	 Catch:{ Exception -> 0x11ba }
        r9 = r3.bytes;	 Catch:{ Exception -> 0x11ba }
        r6.thumb = r9;	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r9 = r3.h;	 Catch:{ Exception -> 0x11ba }
        r6.thumb_h = r9;	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r3 = r3.w;	 Catch:{ Exception -> 0x11ba }
        r6.thumb_w = r3;	 Catch:{ Exception -> 0x11ba }
        goto L_0x121f;
    L_0x120e:
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r3;	 Catch:{ Exception -> 0x11ba }
        r6 = 0;
        r9 = new byte[r6];	 Catch:{ Exception -> 0x11ba }
        r3.thumb = r9;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r3.thumb_h = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r3.thumb_w = r6;	 Catch:{ Exception -> 0x11ba }
    L_0x121f:
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.mime_type;	 Catch:{ Exception -> 0x11ba }
        r3.mime_type = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.size;	 Catch:{ Exception -> 0x11ba }
        r3.size = r6;	 Catch:{ Exception -> 0x11ba }
        r10.originalPath = r8;	 Catch:{ Exception -> 0x11ba }
        r1.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x11ba }
        goto L_0x12aa;
    L_0x1232:
        r14 = org.telegram.messenger.MessageObject.isStickerDocument(r12);	 Catch:{ Exception -> 0x11ba }
        if (r14 == 0) goto L_0x12ae;
    L_0x1238:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument;	 Catch:{ Exception -> 0x11ba }
        r3.<init>();	 Catch:{ Exception -> 0x11ba }
        r2.media = r3;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r10 = r12.id;	 Catch:{ Exception -> 0x11ba }
        r3.id = r10;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.date;	 Catch:{ Exception -> 0x11ba }
        r3.date = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r10 = r12.access_hash;	 Catch:{ Exception -> 0x11ba }
        r3.access_hash = r10;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.mime_type;	 Catch:{ Exception -> 0x11ba }
        r3.mime_type = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.size;	 Catch:{ Exception -> 0x11ba }
        r3.size = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.dc_id;	 Catch:{ Exception -> 0x11ba }
        r3.dc_id = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.attributes;	 Catch:{ Exception -> 0x11ba }
        r3.attributes = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r12.thumbs;	 Catch:{ Exception -> 0x11ba }
        r3 = r1.getThumbForSecretChat(r3);	 Catch:{ Exception -> 0x11ba }
        if (r3 == 0) goto L_0x1278;
    L_0x1271:
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r6;	 Catch:{ Exception -> 0x11ba }
        r6.thumb = r3;	 Catch:{ Exception -> 0x11ba }
        goto L_0x128d;
    L_0x1278:
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r3;	 Catch:{ Exception -> 0x11ba }
        r6 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x11ba }
        r6.<init>();	 Catch:{ Exception -> 0x11ba }
        r3.thumb = r6;	 Catch:{ Exception -> 0x11ba }
        r3 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r3 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r3;	 Catch:{ Exception -> 0x11ba }
        r3 = r3.thumb;	 Catch:{ Exception -> 0x11ba }
        r6 = "s";
        r3.type = r6;	 Catch:{ Exception -> 0x11ba }
    L_0x128d:
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x11ba }
        r3 = org.telegram.messenger.SecretChatHelper.getInstance(r3);	 Catch:{ Exception -> 0x11ba }
        r6 = r7.messageOwner;	 Catch:{ Exception -> 0x11ba }
        r10 = 0;
        r11 = 0;
        r41 = r3;
        r42 = r2;
        r43 = r6;
        r44 = r9;
        r45 = r10;
        r46 = r11;
        r47 = r7;
        r41.performSendEncryptedRequest(r42, r43, r44, r45, r46, r47);	 Catch:{ Exception -> 0x11ba }
    L_0x12a8:
        r10 = r51;
    L_0x12aa:
        r52 = r13;
        goto L_0x1487;
    L_0x12ae:
        r14 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x11ba }
        r14.<init>();	 Catch:{ Exception -> 0x11ba }
        r2.media = r14;	 Catch:{ Exception -> 0x11ba }
        r14 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r15 = r12.attributes;	 Catch:{ Exception -> 0x11ba }
        r14.attributes = r15;	 Catch:{ Exception -> 0x11ba }
        r14 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r14.caption = r6;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.thumbs;	 Catch:{ Exception -> 0x11ba }
        r6 = r1.getThumbForSecretChat(r6);	 Catch:{ Exception -> 0x11ba }
        if (r6 == 0) goto L_0x12df;
    L_0x12c7:
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r6);	 Catch:{ Exception -> 0x11ba }
        r14 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r14 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r14;	 Catch:{ Exception -> 0x11ba }
        r15 = r6.bytes;	 Catch:{ Exception -> 0x11ba }
        r14.thumb = r15;	 Catch:{ Exception -> 0x11ba }
        r14 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r15 = r6.h;	 Catch:{ Exception -> 0x11ba }
        r14.thumb_h = r15;	 Catch:{ Exception -> 0x11ba }
        r14 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = r6.w;	 Catch:{ Exception -> 0x11ba }
        r14.thumb_w = r6;	 Catch:{ Exception -> 0x11ba }
        goto L_0x12f0;
    L_0x12df:
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r6;	 Catch:{ Exception -> 0x11ba }
        r14 = 0;
        r15 = new byte[r14];	 Catch:{ Exception -> 0x11ba }
        r6.thumb = r15;	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6.thumb_h = r14;	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6.thumb_w = r14;	 Catch:{ Exception -> 0x11ba }
    L_0x12f0:
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r14 = r12.size;	 Catch:{ Exception -> 0x11ba }
        r6.size = r14;	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r14 = r12.mime_type;	 Catch:{ Exception -> 0x11ba }
        r6.mime_type = r14;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.key;	 Catch:{ Exception -> 0x11ba }
        if (r6 != 0) goto L_0x133e;
    L_0x1300:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x11ba }
        r6.<init>(r4);	 Catch:{ Exception -> 0x11ba }
        r6.originalPath = r8;	 Catch:{ Exception -> 0x11ba }
        r6.sendEncryptedRequest = r2;	 Catch:{ Exception -> 0x11ba }
        r12 = 2;
        r6.type = r12;	 Catch:{ Exception -> 0x11ba }
        r6.obj = r7;	 Catch:{ Exception -> 0x11ba }
        if (r10 == 0) goto L_0x131d;
    L_0x1310:
        r12 = r10.containsKey(r3);	 Catch:{ Exception -> 0x11ba }
        if (r12 == 0) goto L_0x131d;
    L_0x1316:
        r3 = r10.get(r3);	 Catch:{ Exception -> 0x11ba }
        r6.parentObject = r3;	 Catch:{ Exception -> 0x11ba }
        goto L_0x131f;
    L_0x131d:
        r6.parentObject = r11;	 Catch:{ Exception -> 0x11ba }
    L_0x131f:
        r6.encryptedChat = r9;	 Catch:{ Exception -> 0x11ba }
        r3 = 1;
        r6.performMediaUpload = r3;	 Catch:{ Exception -> 0x11ba }
        r12 = r50;
        if (r12 == 0) goto L_0x1338;
    L_0x1328:
        r3 = r50.length();	 Catch:{ Exception -> 0x11ba }
        if (r3 <= 0) goto L_0x1338;
    L_0x132e:
        r3 = "http";
        r3 = r12.startsWith(r3);	 Catch:{ Exception -> 0x11ba }
        if (r3 == 0) goto L_0x1338;
    L_0x1336:
        r6.httpLocation = r12;	 Catch:{ Exception -> 0x11ba }
    L_0x1338:
        r1.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x11ba }
        r10 = r6;
        goto L_0x12aa;
    L_0x133e:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x11ba }
        r3.<init>();	 Catch:{ Exception -> 0x11ba }
        r10 = r12.id;	 Catch:{ Exception -> 0x11ba }
        r3.id = r10;	 Catch:{ Exception -> 0x11ba }
        r10 = r12.access_hash;	 Catch:{ Exception -> 0x11ba }
        r3.access_hash = r10;	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r10 = r12.key;	 Catch:{ Exception -> 0x11ba }
        r6.key = r10;	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r10 = r12.iv;	 Catch:{ Exception -> 0x11ba }
        r6.iv = r10;	 Catch:{ Exception -> 0x11ba }
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x11ba }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance(r6);	 Catch:{ Exception -> 0x11ba }
        r10 = r7.messageOwner;	 Catch:{ Exception -> 0x11ba }
        r11 = 0;
        r41 = r6;
        r42 = r2;
        r43 = r10;
        r44 = r9;
        r45 = r3;
        r46 = r11;
        r47 = r7;
        r41.performSendEncryptedRequest(r42, r43, r44, r45, r46, r47);	 Catch:{ Exception -> 0x11ba }
        goto L_0x12a8;
    L_0x1373:
        r4 = r48;
        r12 = r50;
        r19 = r11;
        r56 = r14;
        r8 = r21;
        r11 = r59;
        r14 = r44;
    L_0x1381:
        r15 = r14.sizes;	 Catch:{ Exception -> 0x14df }
        r52 = r13;
        r13 = 0;
        r15 = r15.get(r13);	 Catch:{ Exception -> 0x14df }
        r15 = (org.telegram.tgnet.TLRPC.PhotoSize) r15;	 Catch:{ Exception -> 0x14df }
        r13 = r14.sizes;	 Catch:{ Exception -> 0x14df }
        r12 = r14.sizes;	 Catch:{ Exception -> 0x14df }
        r12 = r12.size();	 Catch:{ Exception -> 0x14df }
        r18 = 1;
        r12 = r12 + -1;
        r12 = r13.get(r12);	 Catch:{ Exception -> 0x14df }
        r12 = (org.telegram.tgnet.TLRPC.PhotoSize) r12;	 Catch:{ Exception -> 0x14df }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r15);	 Catch:{ Exception -> 0x14df }
        r13 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto;	 Catch:{ Exception -> 0x14df }
        r13.<init>();	 Catch:{ Exception -> 0x14df }
        r2.media = r13;	 Catch:{ Exception -> 0x14df }
        r13 = r2.media;	 Catch:{ Exception -> 0x14df }
        r13.caption = r6;	 Catch:{ Exception -> 0x14df }
        r6 = r15.bytes;	 Catch:{ Exception -> 0x14df }
        if (r6 == 0) goto L_0x13bb;
    L_0x13b0:
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r6 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r6;	 Catch:{ Exception -> 0x11ba }
        r13 = r15.bytes;	 Catch:{ Exception -> 0x11ba }
        r6.thumb = r13;	 Catch:{ Exception -> 0x11ba }
        r44 = r14;
        goto L_0x13c6;
    L_0x13bb:
        r6 = r2.media;	 Catch:{ Exception -> 0x14df }
        r6 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r6;	 Catch:{ Exception -> 0x14df }
        r44 = r14;
        r13 = 0;
        r14 = new byte[r13];	 Catch:{ Exception -> 0x14df }
        r6.thumb = r14;	 Catch:{ Exception -> 0x14df }
    L_0x13c6:
        r6 = r2.media;	 Catch:{ Exception -> 0x14df }
        r13 = r15.h;	 Catch:{ Exception -> 0x14df }
        r6.thumb_h = r13;	 Catch:{ Exception -> 0x14df }
        r6 = r2.media;	 Catch:{ Exception -> 0x14df }
        r13 = r15.w;	 Catch:{ Exception -> 0x14df }
        r6.thumb_w = r13;	 Catch:{ Exception -> 0x14df }
        r6 = r2.media;	 Catch:{ Exception -> 0x14df }
        r13 = r12.w;	 Catch:{ Exception -> 0x14df }
        r6.w = r13;	 Catch:{ Exception -> 0x14df }
        r6 = r2.media;	 Catch:{ Exception -> 0x14df }
        r13 = r12.h;	 Catch:{ Exception -> 0x14df }
        r6.h = r13;	 Catch:{ Exception -> 0x14df }
        r6 = r2.media;	 Catch:{ Exception -> 0x14df }
        r13 = r12.size;	 Catch:{ Exception -> 0x14df }
        r6.size = r13;	 Catch:{ Exception -> 0x14df }
        r6 = r12.location;	 Catch:{ Exception -> 0x14df }
        r6 = r6.key;	 Catch:{ Exception -> 0x14df }
        if (r6 == 0) goto L_0x142d;
    L_0x13ea:
        r6 = (r56 > r16 ? 1 : (r56 == r16 ? 0 : -1));
        if (r6 == 0) goto L_0x13ef;
    L_0x13ee:
        goto L_0x142d;
    L_0x13ef:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x11ba }
        r3.<init>();	 Catch:{ Exception -> 0x11ba }
        r6 = r12.location;	 Catch:{ Exception -> 0x11ba }
        r10 = r6.volume_id;	 Catch:{ Exception -> 0x11ba }
        r3.id = r10;	 Catch:{ Exception -> 0x11ba }
        r6 = r12.location;	 Catch:{ Exception -> 0x11ba }
        r10 = r6.secret;	 Catch:{ Exception -> 0x11ba }
        r3.access_hash = r10;	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r10 = r12.location;	 Catch:{ Exception -> 0x11ba }
        r10 = r10.key;	 Catch:{ Exception -> 0x11ba }
        r6.key = r10;	 Catch:{ Exception -> 0x11ba }
        r6 = r2.media;	 Catch:{ Exception -> 0x11ba }
        r10 = r12.location;	 Catch:{ Exception -> 0x11ba }
        r10 = r10.iv;	 Catch:{ Exception -> 0x11ba }
        r6.iv = r10;	 Catch:{ Exception -> 0x11ba }
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x11ba }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance(r6);	 Catch:{ Exception -> 0x11ba }
        r10 = r7.messageOwner;	 Catch:{ Exception -> 0x11ba }
        r11 = 0;
        r41 = r6;
        r42 = r2;
        r43 = r10;
        r44 = r9;
        r45 = r3;
        r46 = r11;
        r47 = r7;
        r41.performSendEncryptedRequest(r42, r43, r44, r45, r46, r47);	 Catch:{ Exception -> 0x11ba }
        r10 = r51;
        goto L_0x1487;
    L_0x142d:
        if (r51 != 0) goto L_0x1454;
    L_0x142f:
        r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x11ba }
        r6.<init>(r4);	 Catch:{ Exception -> 0x11ba }
        r6.encryptedChat = r9;	 Catch:{ Exception -> 0x11ba }
        r9 = 0;
        r6.type = r9;	 Catch:{ Exception -> 0x11ba }
        r6.originalPath = r8;	 Catch:{ Exception -> 0x11ba }
        r6.sendEncryptedRequest = r2;	 Catch:{ Exception -> 0x11ba }
        r6.obj = r7;	 Catch:{ Exception -> 0x11ba }
        if (r10 == 0) goto L_0x144e;
    L_0x1441:
        r9 = r10.containsKey(r3);	 Catch:{ Exception -> 0x11ba }
        if (r9 == 0) goto L_0x144e;
    L_0x1447:
        r3 = r10.get(r3);	 Catch:{ Exception -> 0x11ba }
        r6.parentObject = r3;	 Catch:{ Exception -> 0x11ba }
        goto L_0x1450;
    L_0x144e:
        r6.parentObject = r11;	 Catch:{ Exception -> 0x11ba }
    L_0x1450:
        r3 = 1;
        r6.performMediaUpload = r3;	 Catch:{ Exception -> 0x11ba }
        goto L_0x1456;
    L_0x1454:
        r6 = r51;
    L_0x1456:
        r3 = android.text.TextUtils.isEmpty(r50);	 Catch:{ Exception -> 0x14df }
        if (r3 != 0) goto L_0x1469;
    L_0x145c:
        r3 = "http";
        r9 = r50;
        r3 = r9.startsWith(r3);	 Catch:{ Exception -> 0x11ba }
        if (r3 == 0) goto L_0x1469;
    L_0x1466:
        r6.httpLocation = r9;	 Catch:{ Exception -> 0x11ba }
        goto L_0x147f;
    L_0x1469:
        r3 = r44;
        r9 = r3.sizes;	 Catch:{ Exception -> 0x14df }
        r10 = r3.sizes;	 Catch:{ Exception -> 0x14df }
        r10 = r10.size();	 Catch:{ Exception -> 0x14df }
        r11 = 1;
        r10 = r10 - r11;
        r9 = r9.get(r10);	 Catch:{ Exception -> 0x14df }
        r9 = (org.telegram.tgnet.TLRPC.PhotoSize) r9;	 Catch:{ Exception -> 0x14df }
        r6.photoSize = r9;	 Catch:{ Exception -> 0x14df }
        r6.locationParent = r3;	 Catch:{ Exception -> 0x14df }
    L_0x147f:
        r3 = (r56 > r16 ? 1 : (r56 == r16 ? 0 : -1));
        if (r3 != 0) goto L_0x1486;
    L_0x1483:
        r1.performSendDelayedMessage(r6);	 Catch:{ Exception -> 0x11ba }
    L_0x1486:
        r10 = r6;
    L_0x1487:
        r3 = (r56 > r16 ? 1 : (r56 == r16 ? 0 : -1));
        if (r3 == 0) goto L_0x14cd;
    L_0x148b:
        r3 = r10.sendEncryptedRequest;	 Catch:{ Exception -> 0x14df }
        if (r3 == 0) goto L_0x1494;
    L_0x148f:
        r3 = r10.sendEncryptedRequest;	 Catch:{ Exception -> 0x11ba }
        r3 = (org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r3;	 Catch:{ Exception -> 0x11ba }
        goto L_0x149b;
    L_0x1494:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;	 Catch:{ Exception -> 0x14df }
        r3.<init>();	 Catch:{ Exception -> 0x14df }
        r10.sendEncryptedRequest = r3;	 Catch:{ Exception -> 0x14df }
    L_0x149b:
        r6 = r10.messageObjects;	 Catch:{ Exception -> 0x14df }
        r6.add(r7);	 Catch:{ Exception -> 0x14df }
        r6 = r10.messages;	 Catch:{ Exception -> 0x14df }
        r13 = r19;
        r6.add(r13);	 Catch:{ Exception -> 0x17bb }
        r6 = r10.originalPaths;	 Catch:{ Exception -> 0x17bb }
        r6.add(r8);	 Catch:{ Exception -> 0x17bb }
        r6 = 1;
        r10.performMediaUpload = r6;	 Catch:{ Exception -> 0x17bb }
        r6 = r3.messages;	 Catch:{ Exception -> 0x17bb }
        r6.add(r2);	 Catch:{ Exception -> 0x17bb }
        r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x17bb }
        r2.<init>();	 Catch:{ Exception -> 0x17bb }
        r6 = r52;
        r8 = 3;
        if (r6 != r8) goto L_0x14c0;
    L_0x14be:
        r16 = 1;
    L_0x14c0:
        r8 = r16;
        r2.id = r8;	 Catch:{ Exception -> 0x17bb }
        r3 = r3.files;	 Catch:{ Exception -> 0x17bb }
        r3.add(r2);	 Catch:{ Exception -> 0x17bb }
        r1.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x17bb }
        goto L_0x14cf;
    L_0x14cd:
        r13 = r19;
    L_0x14cf:
        r2 = r54;
        if (r2 != 0) goto L_0x17ff;
    L_0x14d3:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x17bb }
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);	 Catch:{ Exception -> 0x17bb }
        r3 = 0;
        r2.cleanDraft(r4, r3);	 Catch:{ Exception -> 0x17bb }
        goto L_0x17ff;
    L_0x14df:
        r0 = move-exception;
        r13 = r19;
        goto L_0x17c8;
    L_0x14e4:
        r0 = move-exception;
        r13 = r11;
        goto L_0x17c8;
    L_0x14e8:
        r10 = r42;
        r11 = r59;
        r8 = r5;
        r13 = r7;
        r7 = r52;
        r4 = r2;
        r3 = 4;
        r2 = r54;
        if (r6 != r3) goto L_0x15bd;
    L_0x14f6:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;	 Catch:{ Exception -> 0x17bb }
        r3.<init>();	 Catch:{ Exception -> 0x17bb }
        r3.to_peer = r8;	 Catch:{ Exception -> 0x17bb }
        r6 = r2.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r6 = r6.with_my_score;	 Catch:{ Exception -> 0x17bb }
        r3.with_my_score = r6;	 Catch:{ Exception -> 0x17bb }
        r6 = r2.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x17bb }
        if (r6 == 0) goto L_0x1535;
    L_0x1509:
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x17bb }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x17bb }
        r8 = r2.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r8 = r8.ttl;	 Catch:{ Exception -> 0x17bb }
        r8 = -r8;
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x17bb }
        r6 = r6.getChat(r8);	 Catch:{ Exception -> 0x17bb }
        r8 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;	 Catch:{ Exception -> 0x17bb }
        r8.<init>();	 Catch:{ Exception -> 0x17bb }
        r3.from_peer = r8;	 Catch:{ Exception -> 0x17bb }
        r8 = r3.from_peer;	 Catch:{ Exception -> 0x17bb }
        r9 = r2.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r9 = r9.ttl;	 Catch:{ Exception -> 0x17bb }
        r9 = -r9;
        r8.channel_id = r9;	 Catch:{ Exception -> 0x17bb }
        if (r6 == 0) goto L_0x153c;
    L_0x152e:
        r8 = r3.from_peer;	 Catch:{ Exception -> 0x17bb }
        r9 = r6.access_hash;	 Catch:{ Exception -> 0x17bb }
        r8.access_hash = r9;	 Catch:{ Exception -> 0x17bb }
        goto L_0x153c;
    L_0x1535:
        r6 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;	 Catch:{ Exception -> 0x17bb }
        r6.<init>();	 Catch:{ Exception -> 0x17bb }
        r3.from_peer = r6;	 Catch:{ Exception -> 0x17bb }
    L_0x153c:
        r6 = r2.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r6 = r6.to_id;	 Catch:{ Exception -> 0x17bb }
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x17bb }
        if (r6 == 0) goto L_0x1562;
    L_0x1544:
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x17bb }
        r6 = org.telegram.messenger.MessagesController.getNotificationsSettings(r6);	 Catch:{ Exception -> 0x17bb }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x17bb }
        r8.<init>();	 Catch:{ Exception -> 0x17bb }
        r9 = "silent_";
        r8.append(r9);	 Catch:{ Exception -> 0x17bb }
        r8.append(r4);	 Catch:{ Exception -> 0x17bb }
        r4 = r8.toString();	 Catch:{ Exception -> 0x17bb }
        r5 = 0;
        r4 = r6.getBoolean(r4, r5);	 Catch:{ Exception -> 0x17bb }
        r3.silent = r4;	 Catch:{ Exception -> 0x17bb }
    L_0x1562:
        r4 = r3.random_id;	 Catch:{ Exception -> 0x17bb }
        r5 = r13.random_id;	 Catch:{ Exception -> 0x17bb }
        r5 = java.lang.Long.valueOf(r5);	 Catch:{ Exception -> 0x17bb }
        r4.add(r5);	 Catch:{ Exception -> 0x17bb }
        r4 = r54.getId();	 Catch:{ Exception -> 0x17bb }
        if (r4 < 0) goto L_0x1581;
    L_0x1573:
        r4 = r3.id;	 Catch:{ Exception -> 0x17bb }
        r2 = r54.getId();	 Catch:{ Exception -> 0x17bb }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x17bb }
        r4.add(r2);	 Catch:{ Exception -> 0x17bb }
        goto L_0x15aa;
    L_0x1581:
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r4 = r4.fwd_msg_id;	 Catch:{ Exception -> 0x17bb }
        if (r4 == 0) goto L_0x1595;
    L_0x1587:
        r4 = r3.id;	 Catch:{ Exception -> 0x17bb }
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r2 = r2.fwd_msg_id;	 Catch:{ Exception -> 0x17bb }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x17bb }
        r4.add(r2);	 Catch:{ Exception -> 0x17bb }
        goto L_0x15aa;
    L_0x1595:
        r4 = r2.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r4 = r4.fwd_from;	 Catch:{ Exception -> 0x17bb }
        if (r4 == 0) goto L_0x15aa;
    L_0x159b:
        r4 = r3.id;	 Catch:{ Exception -> 0x17bb }
        r2 = r2.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r2 = r2.fwd_from;	 Catch:{ Exception -> 0x17bb }
        r2 = r2.channel_post;	 Catch:{ Exception -> 0x17bb }
        r2 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x17bb }
        r4.add(r2);	 Catch:{ Exception -> 0x17bb }
    L_0x15aa:
        r2 = 0;
        r4 = 0;
        r39 = r38;
        r40 = r3;
        r41 = r7;
        r42 = r2;
        r43 = r4;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x17bb }
        goto L_0x17ff;
    L_0x15bd:
        r3 = 9;
        if (r6 != r3) goto L_0x17ff;
    L_0x15c1:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult;	 Catch:{ Exception -> 0x17bb }
        r3.<init>();	 Catch:{ Exception -> 0x17bb }
        r3.peer = r8;	 Catch:{ Exception -> 0x17bb }
        r8 = r13.random_id;	 Catch:{ Exception -> 0x17bb }
        r3.random_id = r8;	 Catch:{ Exception -> 0x17bb }
        r6 = "bot";
        r6 = r10.containsKey(r6);	 Catch:{ Exception -> 0x17bb }
        if (r6 != 0) goto L_0x15d6;
    L_0x15d4:
        r6 = 1;
        goto L_0x15d7;
    L_0x15d6:
        r6 = 0;
    L_0x15d7:
        r3.hide_via = r6;	 Catch:{ Exception -> 0x17bb }
        r6 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x17bb }
        if (r6 == 0) goto L_0x15e7;
    L_0x15dd:
        r6 = r3.flags;	 Catch:{ Exception -> 0x17bb }
        r8 = 1;
        r6 = r6 | r8;
        r3.flags = r6;	 Catch:{ Exception -> 0x17bb }
        r6 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x17bb }
        r3.reply_to_msg_id = r6;	 Catch:{ Exception -> 0x17bb }
    L_0x15e7:
        r6 = r13.to_id;	 Catch:{ Exception -> 0x17bb }
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x17bb }
        if (r6 == 0) goto L_0x160b;
    L_0x15ed:
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x17bb }
        r6 = org.telegram.messenger.MessagesController.getNotificationsSettings(r6);	 Catch:{ Exception -> 0x17bb }
        r8 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x17bb }
        r8.<init>();	 Catch:{ Exception -> 0x17bb }
        r9 = "silent_";
        r8.append(r9);	 Catch:{ Exception -> 0x17bb }
        r8.append(r4);	 Catch:{ Exception -> 0x17bb }
        r8 = r8.toString();	 Catch:{ Exception -> 0x17bb }
        r9 = 0;
        r6 = r6.getBoolean(r8, r9);	 Catch:{ Exception -> 0x17bb }
        r3.silent = r6;	 Catch:{ Exception -> 0x17bb }
    L_0x160b:
        r6 = r18;
        r6 = r10.get(r6);	 Catch:{ Exception -> 0x17bb }
        r6 = (java.lang.String) r6;	 Catch:{ Exception -> 0x17bb }
        r6 = org.telegram.messenger.Utilities.parseLong(r6);	 Catch:{ Exception -> 0x17bb }
        r8 = r6.longValue();	 Catch:{ Exception -> 0x17bb }
        r3.query_id = r8;	 Catch:{ Exception -> 0x17bb }
        r6 = "id";
        r6 = r10.get(r6);	 Catch:{ Exception -> 0x17bb }
        r6 = (java.lang.String) r6;	 Catch:{ Exception -> 0x17bb }
        r3.id = r6;	 Catch:{ Exception -> 0x17bb }
        if (r2 != 0) goto L_0x1636;
    L_0x1629:
        r2 = 1;
        r3.clear_draft = r2;	 Catch:{ Exception -> 0x17bb }
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x17bb }
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);	 Catch:{ Exception -> 0x17bb }
        r6 = 0;
        r2.cleanDraft(r4, r6);	 Catch:{ Exception -> 0x17bb }
    L_0x1636:
        r2 = 0;
        r4 = 0;
        r39 = r38;
        r40 = r3;
        r41 = r7;
        r42 = r2;
        r43 = r4;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x17bb }
        goto L_0x17ff;
    L_0x1649:
        r12 = r39;
        r10 = r42;
        r14 = r55;
        r11 = r59;
        r8 = r5;
        r13 = r7;
        r7 = r52;
        r5 = r2;
        r3 = r41;
        r2 = r54;
        if (r9 != 0) goto L_0x171a;
    L_0x165c:
        if (r12 == 0) goto L_0x169f;
    L_0x165e:
        r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x17bb }
        r2.<init>();	 Catch:{ Exception -> 0x17bb }
        r3 = new java.util.ArrayList;	 Catch:{ Exception -> 0x17bb }
        r3.<init>();	 Catch:{ Exception -> 0x17bb }
        r5 = 0;
    L_0x1669:
        r6 = r12.size();	 Catch:{ Exception -> 0x17bb }
        if (r5 >= r6) goto L_0x167f;
    L_0x166f:
        r6 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x17bb }
        r8 = r6.nextLong();	 Catch:{ Exception -> 0x17bb }
        r6 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x17bb }
        r3.add(r6);	 Catch:{ Exception -> 0x17bb }
        r5 = r5 + 1;
        goto L_0x1669;
    L_0x167f:
        r2.message = r4;	 Catch:{ Exception -> 0x17bb }
        r2.contacts = r12;	 Catch:{ Exception -> 0x17bb }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaEmpty;	 Catch:{ Exception -> 0x17bb }
        r4.<init>();	 Catch:{ Exception -> 0x17bb }
        r2.media = r4;	 Catch:{ Exception -> 0x17bb }
        r2.random_id = r3;	 Catch:{ Exception -> 0x17bb }
        r3 = 0;
        r4 = 0;
        r39 = r38;
        r40 = r2;
        r41 = r7;
        r42 = r3;
        r43 = r4;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x17bb }
        goto L_0x17ff;
    L_0x169f:
        r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage;	 Catch:{ Exception -> 0x17bb }
        r3.<init>();	 Catch:{ Exception -> 0x17bb }
        r3.message = r4;	 Catch:{ Exception -> 0x17bb }
        if (r2 != 0) goto L_0x16aa;
    L_0x16a8:
        r4 = 1;
        goto L_0x16ab;
    L_0x16aa:
        r4 = 0;
    L_0x16ab:
        r3.clear_draft = r4;	 Catch:{ Exception -> 0x17bb }
        r4 = r13.to_id;	 Catch:{ Exception -> 0x17bb }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x17bb }
        if (r4 == 0) goto L_0x16d1;
    L_0x16b3:
        r4 = r1.currentAccount;	 Catch:{ Exception -> 0x17bb }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x17bb }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x17bb }
        r9.<init>();	 Catch:{ Exception -> 0x17bb }
        r10 = "silent_";
        r9.append(r10);	 Catch:{ Exception -> 0x17bb }
        r9.append(r5);	 Catch:{ Exception -> 0x17bb }
        r9 = r9.toString();	 Catch:{ Exception -> 0x17bb }
        r10 = 0;
        r4 = r4.getBoolean(r9, r10);	 Catch:{ Exception -> 0x17bb }
        r3.silent = r4;	 Catch:{ Exception -> 0x17bb }
    L_0x16d1:
        r3.peer = r8;	 Catch:{ Exception -> 0x17bb }
        r8 = r13.random_id;	 Catch:{ Exception -> 0x17bb }
        r3.random_id = r8;	 Catch:{ Exception -> 0x17bb }
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x17bb }
        if (r4 == 0) goto L_0x16e5;
    L_0x16db:
        r4 = r3.flags;	 Catch:{ Exception -> 0x17bb }
        r8 = 1;
        r4 = r4 | r8;
        r3.flags = r4;	 Catch:{ Exception -> 0x17bb }
        r4 = r13.reply_to_msg_id;	 Catch:{ Exception -> 0x17bb }
        r3.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x17bb }
    L_0x16e5:
        if (r53 != 0) goto L_0x16ea;
    L_0x16e7:
        r4 = 1;
        r3.no_webpage = r4;	 Catch:{ Exception -> 0x17bb }
    L_0x16ea:
        if (r14 == 0) goto L_0x16fb;
    L_0x16ec:
        r4 = r55.isEmpty();	 Catch:{ Exception -> 0x17bb }
        if (r4 != 0) goto L_0x16fb;
    L_0x16f2:
        r3.entities = r14;	 Catch:{ Exception -> 0x17bb }
        r4 = r3.flags;	 Catch:{ Exception -> 0x17bb }
        r8 = 8;
        r4 = r4 | r8;
        r3.flags = r4;	 Catch:{ Exception -> 0x17bb }
    L_0x16fb:
        r4 = 0;
        r8 = 0;
        r39 = r38;
        r40 = r3;
        r41 = r7;
        r42 = r4;
        r43 = r8;
        r44 = r59;
        r39.performSendMessageRequest(r40, r41, r42, r43, r44);	 Catch:{ Exception -> 0x17bb }
        if (r2 != 0) goto L_0x17ff;
    L_0x170e:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x17bb }
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);	 Catch:{ Exception -> 0x17bb }
        r3 = 0;
        r2.cleanDraft(r5, r3);	 Catch:{ Exception -> 0x17bb }
        goto L_0x17ff;
    L_0x171a:
        r8 = r9.layer;	 Catch:{ Exception -> 0x17bb }
        r8 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r8);	 Catch:{ Exception -> 0x17bb }
        r11 = 73;
        if (r8 < r11) goto L_0x172a;
    L_0x1724:
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x17bb }
        r8.<init>();	 Catch:{ Exception -> 0x17bb }
        goto L_0x172f;
    L_0x172a:
        r8 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x17bb }
        r8.<init>();	 Catch:{ Exception -> 0x17bb }
    L_0x172f:
        r11 = r13.ttl;	 Catch:{ Exception -> 0x17bb }
        r8.ttl = r11;	 Catch:{ Exception -> 0x17bb }
        if (r14 == 0) goto L_0x1743;
    L_0x1735:
        r11 = r55.isEmpty();	 Catch:{ Exception -> 0x17bb }
        if (r11 != 0) goto L_0x1743;
    L_0x173b:
        r8.entities = r14;	 Catch:{ Exception -> 0x17bb }
        r11 = r8.flags;	 Catch:{ Exception -> 0x17bb }
        r11 = r11 | 128;
        r8.flags = r11;	 Catch:{ Exception -> 0x17bb }
    L_0x1743:
        r11 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x17bb }
        r14 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1));
        if (r14 == 0) goto L_0x1754;
    L_0x1749:
        r11 = r13.reply_to_random_id;	 Catch:{ Exception -> 0x17bb }
        r8.reply_to_random_id = r11;	 Catch:{ Exception -> 0x17bb }
        r11 = r8.flags;	 Catch:{ Exception -> 0x17bb }
        r12 = 8;
        r11 = r11 | r12;
        r8.flags = r11;	 Catch:{ Exception -> 0x17bb }
    L_0x1754:
        if (r10 == 0) goto L_0x176a;
    L_0x1756:
        r11 = r10.get(r3);	 Catch:{ Exception -> 0x17bb }
        if (r11 == 0) goto L_0x176a;
    L_0x175c:
        r3 = r10.get(r3);	 Catch:{ Exception -> 0x17bb }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x17bb }
        r8.via_bot_name = r3;	 Catch:{ Exception -> 0x17bb }
        r3 = r8.flags;	 Catch:{ Exception -> 0x17bb }
        r3 = r3 | 2048;
        r8.flags = r3;	 Catch:{ Exception -> 0x17bb }
    L_0x176a:
        r10 = r13.random_id;	 Catch:{ Exception -> 0x17bb }
        r8.random_id = r10;	 Catch:{ Exception -> 0x17bb }
        r8.message = r4;	 Catch:{ Exception -> 0x17bb }
        r3 = r29;
        if (r3 == 0) goto L_0x178c;
    L_0x1774:
        r4 = r3.url;	 Catch:{ Exception -> 0x17bb }
        if (r4 == 0) goto L_0x178c;
    L_0x1778:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage;	 Catch:{ Exception -> 0x17bb }
        r4.<init>();	 Catch:{ Exception -> 0x17bb }
        r8.media = r4;	 Catch:{ Exception -> 0x17bb }
        r4 = r8.media;	 Catch:{ Exception -> 0x17bb }
        r3 = r3.url;	 Catch:{ Exception -> 0x17bb }
        r4.url = r3;	 Catch:{ Exception -> 0x17bb }
        r3 = r8.flags;	 Catch:{ Exception -> 0x17bb }
        r3 = r3 | 512;
        r8.flags = r3;	 Catch:{ Exception -> 0x17bb }
        goto L_0x1793;
    L_0x178c:
        r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty;	 Catch:{ Exception -> 0x17bb }
        r3.<init>();	 Catch:{ Exception -> 0x17bb }
        r8.media = r3;	 Catch:{ Exception -> 0x17bb }
    L_0x1793:
        r3 = r1.currentAccount;	 Catch:{ Exception -> 0x17bb }
        r3 = org.telegram.messenger.SecretChatHelper.getInstance(r3);	 Catch:{ Exception -> 0x17bb }
        r4 = r7.messageOwner;	 Catch:{ Exception -> 0x17bb }
        r10 = 0;
        r11 = 0;
        r41 = r3;
        r42 = r8;
        r43 = r4;
        r44 = r9;
        r45 = r10;
        r46 = r11;
        r47 = r7;
        r41.performSendEncryptedRequest(r42, r43, r44, r45, r46, r47);	 Catch:{ Exception -> 0x17bb }
        if (r2 != 0) goto L_0x17ff;
    L_0x17b0:
        r2 = r1.currentAccount;	 Catch:{ Exception -> 0x17bb }
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);	 Catch:{ Exception -> 0x17bb }
        r3 = 0;
        r2.cleanDraft(r5, r3);	 Catch:{ Exception -> 0x17bb }
        goto L_0x17ff;
    L_0x17bb:
        r0 = move-exception;
        goto L_0x17c8;
    L_0x17bd:
        r0 = move-exception;
        r13 = r7;
        r7 = r52;
        goto L_0x17c8;
    L_0x17c2:
        r0 = move-exception;
        r37 = r13;
        r13 = r7;
        r7 = r37;
    L_0x17c8:
        r2 = r0;
        r14 = r7;
        r7 = r13;
        goto L_0x17d0;
    L_0x17cc:
        r0 = move-exception;
        r13 = r7;
        goto L_0x020b;
    L_0x17d0:
        org.telegram.messenger.FileLog.e(r2);
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r2.markMessageAsSendError(r7);
        if (r14 == 0) goto L_0x17e3;
    L_0x17de:
        r2 = r14.messageOwner;
        r3 = 2;
        r2.send_state = r3;
    L_0x17e3:
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.messageSendError;
        r4 = 1;
        r4 = new java.lang.Object[r4];
        r5 = r7.id;
        r5 = java.lang.Integer.valueOf(r5);
        r6 = 0;
        r4[r6] = r5;
        r2.postNotificationName(r3, r4);
        r2 = r7.id;
        r1.processSentMessage(r2);
    L_0x17ff:
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
                FileLoader.getInstance(this.currentAccount).uploadFile(str2, false, true, 16777216);
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
                        FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForObject(delayedMessage2.photoSize, delayedMessage2.locationParent), delayedMessage2.parentObject, "jpg", 2, 0);
                        return;
                    }
                }
                putToDelayedMessages(str2, delayedMessage2);
                FileLoader.getInstance(this.currentAccount).uploadFile(str2, true, true, 16777216);
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
                            SecretChatHelper instance = SecretChatHelper.getInstance(this.currentAccount);
                            MessageObject messageObject2 = delayedMessage2.obj;
                            instance.performSendEncryptedRequest(tL_decryptedMessage, messageObject2.messageOwner, delayedMessage2.encryptedChat, delayedMessage2.videoEditedInfo.encryptedFile, delayedMessage2.originalPath, messageObject2);
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
                                FileLoader.getInstance(this.currentAccount).uploadFile(str7, false, false, 33554432);
                            } else {
                                FileLoader.getInstance(this.currentAccount).uploadFile(str7, false, false, document.size, 33554432);
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
                            FileLoader.getInstance(this.currentAccount).uploadFile(str2, false, true, 16777216);
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
                                FileLoader.getInstance(this.currentAccount).uploadFile(str7, true, false, 33554432);
                            } else {
                                FileLoader.getInstance(this.currentAccount).uploadFile(str7, true, false, document.size, 33554432);
                            }
                        } else {
                            putToDelayedMessages(FileLoader.getAttachFileName(document), delayedMessage2);
                            FileLoader.getInstance(this.currentAccount).loadFile(document, delayedMessage2.parentObject, 2, 0);
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
                            FileLoader instance2 = FileLoader.getInstance(this.currentAccount);
                            if (delayedMessage2.sendRequest != null) {
                                z2 = false;
                            }
                            instance2.uploadFile(str2, z2, false, 67108864);
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
                            FileLoader.getInstance(this.currentAccount).uploadFile(str2, false, true, 16777216);
                        }
                    } else {
                        messageObject = delayedMessage2.obj;
                        str8 = messageObject.messageOwner.attachPath;
                        document = messageObject.getDocument();
                        if (delayedMessage2.sendEncryptedRequest == null || document.dc_id == 0 || new File(str8).exists()) {
                            putToDelayedMessages(str8, delayedMessage2);
                            FileLoader.getInstance(this.currentAccount).uploadFile(str8, true, false, 67108864);
                        } else {
                            putToDelayedMessages(FileLoader.getAttachFileName(document), delayedMessage2);
                            FileLoader.getInstance(this.currentAccount).loadFile(document, delayedMessage2.parentObject, 2, 0);
                        }
                    }
                }
            } else if (i2 == 3) {
                str2 = delayedMessage2.obj.messageOwner.attachPath;
                putToDelayedMessages(str2, delayedMessage2);
                FileLoader instance3 = FileLoader.getInstance(this.currentAccount);
                if (delayedMessage2.sendRequest == null) {
                    z = true;
                }
                instance3.uploadFile(str2, z, true, 50331648);
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
                                        FileLoader.getInstance(this.currentAccount).uploadFile(str9, false, false, 33554432);
                                    } else {
                                        FileLoader.getInstance(this.currentAccount).uploadFile(str9, false, false, document3.size, 33554432);
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
                                    FileLoader.getInstance(this.currentAccount).uploadFile(str, false, true, 16777216);
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
                                    FileLoader.getInstance(this.currentAccount).uploadFile(str9, true, false, 33554432);
                                } else {
                                    FileLoader.getInstance(this.currentAccount).uploadFile(str9, true, false, document3.size, 33554432);
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
                            FileLoader.getInstance(this.currentAccount).uploadFile(str8, delayedMessage2.sendEncryptedRequest != null, true, 16777216);
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
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileUploadProgressChanged, str, valueOf, valueOf2);
                    break;
                }
            }
            TL_messages_uploadMedia tL_messages_uploadMedia = new TL_messages_uploadMedia();
            tL_messages_uploadMedia.media = inputMedia;
            tL_messages_uploadMedia.peer = ((TL_messages_sendMultiMedia) delayedMessage.sendRequest).peer;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_uploadMedia, new -$$Lambda$SendMessagesHelper$9ikUx4RLVeYwBBYbegYSYKLcFew(this, inputMedia, delayedMessage));
        } else if (inputEncryptedFile != null) {
            TL_messages_sendEncryptedMultiMedia tL_messages_sendEncryptedMultiMedia = (TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
            for (i = 0; i < tL_messages_sendEncryptedMultiMedia.files.size(); i++) {
                if (tL_messages_sendEncryptedMultiMedia.files.get(i) == inputEncryptedFile) {
                    putToSendingMessages((Message) delayedMessage.messages.get(i));
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileUploadProgressChanged, str, valueOf, valueOf2);
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
            MessagesStorage.getInstance(this.currentAccount).putMessages(delayedMessage.messages, false, true, false, 0);
            MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(delayedMessage.peer, delayedMessage.messageObjects);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
            SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest((TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest, delayedMessage);
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$30d877KfsrPIYnw1mIlTicl4KVY(this, str));
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject) tL_messages_sendMultiMedia, new -$$Lambda$SendMessagesHelper$KTc-sr8270evlRbeKgXtKisOYgM(this, arrayList3, tL_messages_sendMultiMedia, arrayList, arrayList2, delayedMessage), null, 68);
    }

    public /* synthetic */ void lambda$performSendMessageRequestMulti$29$SendMessagesHelper(ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$GqwnlURs2UJWXkcpzjv2EBiLrD0(this, tL_error, arrayList, tL_messages_sendMultiMedia, arrayList2, arrayList3, delayedMessage, tLObject));
    }

    public /* synthetic */ void lambda$null$28$SendMessagesHelper(TL_error tL_error, ArrayList arrayList, TL_messages_sendMultiMedia tL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, TLObject tLObject) {
        ArrayList arrayList4;
        SendMessagesHelper thisR;
        int i;
        Object obj;
        SendMessagesHelper sendMessagesHelper = this;
        TL_error tL_error2 = tL_error;
        ArrayList arrayList5 = arrayList;
        final TL_messages_sendMultiMedia tL_messages_sendMultiMedia2 = tL_messages_sendMultiMedia;
        final ArrayList arrayList6 = arrayList2;
        ArrayList arrayList7 = arrayList3;
        final DelayedMessage delayedMessage2 = delayedMessage;
        int i2 = 1;
        if (tL_error2 != null && FileRefController.isFileRefError(tL_error2.text)) {
            if (arrayList5 != null) {
                FileRefController.getInstance(sendMessagesHelper.currentAccount).requestReference(arrayList4, tL_messages_sendMultiMedia2, arrayList6, arrayList7, new ArrayList(arrayList5), delayedMessage2);
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
                    Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$LtIzRBxRfP0h_IpuxXXH0r91OLg(sendMessagesHelper, tL_updateNewMessage));
                    arrayList4.remove(i3);
                } else if (update instanceof TL_updateNewChannelMessage) {
                    TL_updateNewChannelMessage tL_updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                    message = tL_updateNewChannelMessage.message;
                    sparseArray.put(message.id, message);
                    Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$QOjj_wrwvuHdKpPrDByKhoWSuVs(sendMessagesHelper, tL_updateNewChannelMessage));
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
                        num = (Integer) MessagesController.getInstance(sendMessagesHelper.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(message2.dialog_id));
                        if (num == null) {
                            num = Integer.valueOf(MessagesStorage.getInstance(sendMessagesHelper.currentAccount).getDialogReadMax(message2.out, message2.dialog_id));
                            MessagesController.getInstance(sendMessagesHelper.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(message2.dialog_id), num);
                        }
                        message2.unread = num.intValue() < message2.id;
                        StatsController.getInstance(sendMessagesHelper.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), i2, i2);
                        message3.send_state = 0;
                        NotificationCenter.getInstance(sendMessagesHelper.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i6), Integer.valueOf(message3.id), message3, Long.valueOf(message3.dialog_id), Long.valueOf(j), Integer.valueOf(mediaExistanceFlags));
                        -$$Lambda$SendMessagesHelper$s-p-jfZFDyfDIdh58D5Hp9jBe_A -__lambda_sendmessageshelper_s-p-jfzfdyfdidh58d5hp9jbe_a = r0;
                        Message message4 = message3;
                        DispatchQueue storageQueue = MessagesStorage.getInstance(this.currentAccount).getStorageQueue();
                        SparseArray sparseArray2 = sparseArray;
                        -$$Lambda$SendMessagesHelper$s-p-jfZFDyfDIdh58D5Hp9jBe_A -__lambda_sendmessageshelper_s-p-jfzfdyfdidh58d5hp9jbe_a2 = new -$$Lambda$SendMessagesHelper$s-p-jfZFDyfDIdh58D5Hp9jBe_A(this, message4, i6, arrayList8, j, mediaExistanceFlags);
                        storageQueue.postRunnable(-__lambda_sendmessageshelper_s-p-jfzfdyfdidh58d5hp9jbe_a);
                        i4++;
                        arrayList7 = arrayList3;
                        sendMessagesHelper = this;
                        updates2 = updates;
                        longSparseArray = longSparseArray2;
                        sparseArray = sparseArray2;
                        i2 = 1;
                    }
                }
                updates = updates2;
                thisR = sendMessagesHelper;
                i = 1;
                obj = 1;
            }
            updates = updates2;
            thisR = sendMessagesHelper;
            i = 1;
            obj = null;
            Utilities.stageQueue.postRunnable(new -$$Lambda$SendMessagesHelper$4Kecy4z15gyx9k2PnYvYSIzID4o(thisR, updates));
        } else {
            thisR = sendMessagesHelper;
            i = 1;
            AlertsCreator.processError(thisR.currentAccount, tL_error2, null, tL_messages_sendMultiMedia2, new Object[0]);
            obj = 1;
        }
        if (obj != null) {
            for (int i7 = 0; i7 < arrayList2.size(); i7++) {
                Message message5 = ((MessageObject) arrayList6.get(i7)).messageOwner;
                MessagesStorage.getInstance(thisR.currentAccount).markMessageAsSendError(message5);
                message5.send_state = 2;
                NotificationCenter instance = NotificationCenter.getInstance(thisR.currentAccount);
                int i8 = NotificationCenter.messageSendError;
                Object[] objArr = new Object[i];
                objArr[0] = Integer.valueOf(message5.id);
                instance.postNotificationName(i8, objArr);
                thisR.processSentMessage(message5.id);
                thisR.removeFromSendingMessages(message5.id);
            }
        }
    }

    public /* synthetic */ void lambda$null$23$SendMessagesHelper(TL_updateNewMessage tL_updateNewMessage) {
        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$24$SendMessagesHelper(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        MessagesController.getInstance(this.currentAccount).processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$26$SendMessagesHelper(Message message, int i, ArrayList arrayList, long j, int i2) {
        Message message2 = message;
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(message2.random_id, Integer.valueOf(i), message2.id, 0, false, message2.to_id.channel_id);
        MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, true, false, false, 0);
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$jV9Gq3Q6H66Q6pHxnGZUWi6v2TA(this, message2, i, j, i2));
    }

    public /* synthetic */ void lambda$null$25$SendMessagesHelper(Message message, int i, long j, int i2) {
        DataQuery.getInstance(this.currentAccount).increasePeerRaiting(message.dialog_id);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(message.id), message, Long.valueOf(message.dialog_id), Long.valueOf(j), Integer.valueOf(i2));
        processSentMessage(i);
        removeFromSendingMessages(i);
    }

    public /* synthetic */ void lambda$null$27$SendMessagesHelper(Updates updates) {
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
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
        message.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject2, new -$$Lambda$SendMessagesHelper$Qe-YOTDVha4ZEN36efTByrQcLKk(this, tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, message), new -$$Lambda$SendMessagesHelper$_wu1JxbPB7vnkTrFhWBgUdpKleg(this, message), (tLObject2 instanceof TL_messages_sendMessage ? 128 : 0) | 68);
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
                FileRefController.getInstance(this.currentAccount).requestReference(obj, tLObject3, messageObject, str, delayedMessage, Boolean.valueOf(z), delayedMessage3);
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
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
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
                    Integer num = (Integer) MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(message5.dialog_id));
                    if (num == null) {
                        num = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message5.out, message5.dialog_id));
                        MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(message5.dialog_id), num);
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
                LocationController.getInstance(this.currentAccount).addSharingLocation(message2.dialog_id, message2.id, message2.media.period, message);
            }
            if (obj == null) {
                StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                message2.send_state = 0;
                NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
                i = NotificationCenter.messageReceivedByServer;
                Object[] objArr = new Object[6];
                objArr[0] = Integer.valueOf(i3);
                objArr[1] = Integer.valueOf(z ? i3 : message2.id);
                objArr[2] = message2;
                objArr[3] = Long.valueOf(message2.dialog_id);
                objArr[4] = Long.valueOf(0);
                objArr[5] = Integer.valueOf(i2);
                instance.postNotificationName(i, objArr);
                int i4 = i3;
                boolean z2 = z;
                ArrayList arrayList3 = arrayList;
                -$$Lambda$SendMessagesHelper$oDX4i73L5lyJayv_jik2z2hGP7Q -__lambda_sendmessageshelper_odx4i73l5lyjayv_jik2z2hgp7q = r0;
                i3 = i2;
                DispatchQueue storageQueue = MessagesStorage.getInstance(this.currentAccount).getStorageQueue();
                -$$Lambda$SendMessagesHelper$oDX4i73L5lyJayv_jik2z2hGP7Q -__lambda_sendmessageshelper_odx4i73l5lyjayv_jik2z2hgp7q2 = new -$$Lambda$SendMessagesHelper$oDX4i73L5lyJayv_jik2z2hGP7Q(this, message, i4, z2, arrayList3, i3, str2);
                storageQueue.postRunnable(-__lambda_sendmessageshelper_odx4i73l5lyjayv_jik2z2hgp7q);
            }
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error2, null, tLObject3, new Object[0]);
            obj = 1;
        }
        if (obj != null) {
            MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(message2);
            message2.send_state = 2;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message2.id));
            processSentMessage(message2.id);
            if (MessageObject.isVideoMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isNewGifMessage(message)) {
                stopVideoService(message2.attachPath);
            }
            removeFromSendingMessages(message2.id);
        }
    }

    public /* synthetic */ void lambda$null$33$SendMessagesHelper(TL_updateShortSentMessage tL_updateShortSentMessage) {
        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, tL_updateShortSentMessage.pts, tL_updateShortSentMessage.date, tL_updateShortSentMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$34$SendMessagesHelper(TL_updateNewMessage tL_updateNewMessage) {
        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, tL_updateNewMessage.pts, -1, tL_updateNewMessage.pts_count);
    }

    public /* synthetic */ void lambda$null$35$SendMessagesHelper(TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        MessagesController.getInstance(this.currentAccount).processNewChannelDifferenceParams(tL_updateNewChannelMessage.pts, tL_updateNewChannelMessage.pts_count, tL_updateNewChannelMessage.message.to_id.channel_id);
    }

    public /* synthetic */ void lambda$null$36$SendMessagesHelper(Updates updates) {
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
    }

    public /* synthetic */ void lambda$null$38$SendMessagesHelper(Message message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        Message message2 = message;
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(message2.random_id, Integer.valueOf(i), z ? i : message2.id, 0, false, message2.to_id.channel_id);
        MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList, true, false, z, 0);
        if (z) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(message2);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arrayList2, true, false, false, 0);
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
                MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(messageObject.getDialogId(), arrayList2, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        DataQuery.getInstance(this.currentAccount).increasePeerRaiting(message.dialog_id);
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        i3 = NotificationCenter.messageReceivedByServer;
        Object[] objArr = new Object[6];
        objArr[0] = Integer.valueOf(i);
        objArr[1] = Integer.valueOf(z ? i : message.id);
        objArr[2] = message;
        objArr[3] = Long.valueOf(message.dialog_id);
        objArr[4] = Long.valueOf(0);
        objArr[5] = Integer.valueOf(i2);
        instance.postNotificationName(i3, objArr);
        processSentMessage(i);
        removeFromSendingMessages(i);
    }

    public /* synthetic */ void lambda$performSendMessageRequest$42$SendMessagesHelper(Message message) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$s7kB2gRrnjLRX4z7Rxo0J3e9TgU(this, message, message.id));
    }

    public /* synthetic */ void lambda$null$41$SendMessagesHelper(Message message, int i) {
        message.send_state = 0;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(i));
    }

    /* JADX WARNING: Removed duplicated region for block: B:180:0x045a  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x047f  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x04cd  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x045a  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x047f  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x04cd  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x04fe  */
    private void updateMediaPaths(org.telegram.messenger.MessageObject r17, org.telegram.tgnet.TLRPC.Message r18, int r19, java.lang.String r20, boolean r21) {
        /*
        r16 = this;
        r0 = r16;
        r1 = r17;
        r8 = r18;
        r9 = r20;
        r2 = r21;
        r10 = r1.messageOwner;
        r3 = r10.media;
        r5 = "_";
        if (r3 == 0) goto L_0x0107;
    L_0x0012:
        r6 = r3.photo;
        r7 = 40;
        if (r6 == 0) goto L_0x0036;
    L_0x0018:
        r3 = r6.sizes;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r7);
        if (r8 == 0) goto L_0x002f;
    L_0x0020:
        r6 = r8.media;
        if (r6 == 0) goto L_0x002f;
    L_0x0024:
        r6 = r6.photo;
        if (r6 == 0) goto L_0x002f;
    L_0x0028:
        r6 = r6.sizes;
        r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7);
        goto L_0x0030;
    L_0x002f:
        r6 = r3;
    L_0x0030:
        r7 = r10.media;
        r7 = r7.photo;
        goto L_0x00ac;
    L_0x0036:
        r6 = r3.document;
        if (r6 == 0) goto L_0x0057;
    L_0x003a:
        r3 = r6.thumbs;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r7);
        if (r8 == 0) goto L_0x0051;
    L_0x0042:
        r6 = r8.media;
        if (r6 == 0) goto L_0x0051;
    L_0x0046:
        r6 = r6.document;
        if (r6 == 0) goto L_0x0051;
    L_0x004a:
        r6 = r6.thumbs;
        r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7);
        goto L_0x0052;
    L_0x0051:
        r6 = r3;
    L_0x0052:
        r7 = r10.media;
        r7 = r7.document;
        goto L_0x00ac;
    L_0x0057:
        r3 = r3.webpage;
        if (r3 == 0) goto L_0x00a9;
    L_0x005b:
        r6 = r3.photo;
        if (r6 == 0) goto L_0x0082;
    L_0x005f:
        r3 = r6.sizes;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r7);
        if (r8 == 0) goto L_0x007a;
    L_0x0067:
        r6 = r8.media;
        if (r6 == 0) goto L_0x007a;
    L_0x006b:
        r6 = r6.webpage;
        if (r6 == 0) goto L_0x007a;
    L_0x006f:
        r6 = r6.photo;
        if (r6 == 0) goto L_0x007a;
    L_0x0073:
        r6 = r6.sizes;
        r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7);
        goto L_0x007b;
    L_0x007a:
        r6 = r3;
    L_0x007b:
        r7 = r10.media;
        r7 = r7.webpage;
        r7 = r7.photo;
        goto L_0x00ac;
    L_0x0082:
        r3 = r3.document;
        if (r3 == 0) goto L_0x00a9;
    L_0x0086:
        r3 = r3.thumbs;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r7);
        if (r8 == 0) goto L_0x00a1;
    L_0x008e:
        r6 = r8.media;
        if (r6 == 0) goto L_0x00a1;
    L_0x0092:
        r6 = r6.webpage;
        if (r6 == 0) goto L_0x00a1;
    L_0x0096:
        r6 = r6.document;
        if (r6 == 0) goto L_0x00a1;
    L_0x009a:
        r6 = r6.thumbs;
        r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7);
        goto L_0x00a2;
    L_0x00a1:
        r6 = r3;
    L_0x00a2:
        r7 = r10.media;
        r7 = r7.webpage;
        r7 = r7.document;
        goto L_0x00ac;
    L_0x00a9:
        r3 = 0;
        r6 = 0;
        r7 = 0;
    L_0x00ac:
        r11 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r11 == 0) goto L_0x0107;
    L_0x00b0:
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r3 == 0) goto L_0x0107;
    L_0x00b4:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r11 = "stripped";
        r3.append(r11);
        r12 = org.telegram.messenger.FileRefController.getKeyForParentObject(r17);
        r3.append(r12);
        r3 = r3.toString();
        if (r8 == 0) goto L_0x00df;
    L_0x00cb:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r12.append(r11);
        r11 = org.telegram.messenger.FileRefController.getKeyForParentObject(r18);
        r12.append(r11);
        r11 = r12.toString();
        goto L_0x00fc;
    L_0x00df:
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r12 = "strippedmessage";
        r11.append(r12);
        r12 = r19;
        r11.append(r12);
        r11.append(r5);
        r12 = r17.getChannelId();
        r11.append(r12);
        r11 = r11.toString();
    L_0x00fc:
        r12 = org.telegram.messenger.ImageLoader.getInstance();
        r6 = org.telegram.messenger.ImageLocation.getForObject(r6, r7);
        r12.replaceImageInCache(r3, r11, r6, r2);
    L_0x0107:
        if (r8 != 0) goto L_0x010a;
    L_0x0109:
        return;
    L_0x010a:
        r3 = r8.media;
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        r11 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r7 = "sent_";
        r13 = ".jpg";
        r15 = 0;
        r14 = 1;
        if (r6 == 0) goto L_0x02bc;
    L_0x0119:
        r6 = r3.photo;
        if (r6 == 0) goto L_0x02bc;
    L_0x011d:
        r6 = r10.media;
        r4 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r4 == 0) goto L_0x02bc;
    L_0x0123:
        r4 = r6.photo;
        if (r4 == 0) goto L_0x02bc;
    L_0x0127:
        r1 = r3.ttl_seconds;
        if (r1 != 0) goto L_0x0153;
    L_0x012b:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r3 = r8.media;
        r3 = r3.photo;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r7);
        r6 = r8.to_id;
        r6 = r6.channel_id;
        r4.append(r6);
        r4.append(r5);
        r6 = r8.id;
        r4.append(r6);
        r4 = r4.toString();
        r1.putSentFile(r9, r3, r15, r4);
    L_0x0153:
        r1 = r10.media;
        r1 = r1.photo;
        r1 = r1.sizes;
        r1 = r1.size();
        if (r1 != r14) goto L_0x017f;
    L_0x015f:
        r1 = r10.media;
        r1 = r1.photo;
        r1 = r1.sizes;
        r1 = r1.get(r15);
        r1 = (org.telegram.tgnet.TLRPC.PhotoSize) r1;
        r1 = r1.location;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r1 == 0) goto L_0x017f;
    L_0x0171:
        r1 = r10.media;
        r1 = r1.photo;
        r2 = r8.media;
        r2 = r2.photo;
        r2 = r2.sizes;
        r1.sizes = r2;
        goto L_0x02a2;
    L_0x017f:
        r1 = 0;
    L_0x0180:
        r3 = r8.media;
        r3 = r3.photo;
        r3 = r3.sizes;
        r3 = r3.size();
        if (r1 >= r3) goto L_0x02a2;
    L_0x018c:
        r3 = r8.media;
        r3 = r3.photo;
        r3 = r3.sizes;
        r3 = r3.get(r1);
        r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3;
        if (r3 == 0) goto L_0x0299;
    L_0x019a:
        r4 = r3.location;
        if (r4 == 0) goto L_0x0299;
    L_0x019e:
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r4 != 0) goto L_0x0299;
    L_0x01a2:
        r4 = r3.type;
        if (r4 != 0) goto L_0x01a8;
    L_0x01a6:
        goto L_0x0299;
    L_0x01a8:
        r4 = 0;
    L_0x01a9:
        r6 = r10.media;
        r6 = r6.photo;
        r6 = r6.sizes;
        r6 = r6.size();
        if (r4 >= r6) goto L_0x0299;
    L_0x01b5:
        r6 = r10.media;
        r6 = r6.photo;
        r6 = r6.sizes;
        r6 = r6.get(r4);
        r6 = (org.telegram.tgnet.TLRPC.PhotoSize) r6;
        if (r6 == 0) goto L_0x0290;
    L_0x01c3:
        r7 = r6.location;
        if (r7 == 0) goto L_0x0290;
    L_0x01c7:
        r9 = r6.type;
        if (r9 != 0) goto L_0x01cd;
    L_0x01cb:
        goto L_0x0290;
    L_0x01cd:
        r14 = r7.volume_id;
        r7 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1));
        if (r7 != 0) goto L_0x01db;
    L_0x01d3:
        r7 = r3.type;
        r7 = r7.equals(r9);
        if (r7 != 0) goto L_0x01e7;
    L_0x01db:
        r7 = r3.w;
        r9 = r6.w;
        if (r7 != r9) goto L_0x0290;
    L_0x01e1:
        r7 = r3.h;
        r9 = r6.h;
        if (r7 != r9) goto L_0x0290;
    L_0x01e7:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r7 = r6.location;
        r14 = r7.volume_id;
        r4.append(r14);
        r4.append(r5);
        r7 = r6.location;
        r7 = r7.local_id;
        r4.append(r7);
        r4 = r4.toString();
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r9 = r3.location;
        r14 = r9.volume_id;
        r7.append(r14);
        r7.append(r5);
        r9 = r3.location;
        r9 = r9.local_id;
        r7.append(r9);
        r7 = r7.toString();
        r9 = r4.equals(r7);
        if (r9 == 0) goto L_0x0223;
    L_0x0221:
        goto L_0x0299;
    L_0x0223:
        r9 = new java.io.File;
        r14 = 4;
        r15 = org.telegram.messenger.FileLoader.getDirectory(r14);
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r4);
        r14.append(r13);
        r14 = r14.toString();
        r9.<init>(r15, r14);
        r14 = r8.media;
        r15 = r14.ttl_seconds;
        if (r15 != 0) goto L_0x025c;
    L_0x0242:
        r14 = r14.photo;
        r14 = r14.sizes;
        r14 = r14.size();
        r15 = 1;
        if (r14 == r15) goto L_0x0257;
    L_0x024d:
        r14 = r3.w;
        r15 = 90;
        if (r14 > r15) goto L_0x0257;
    L_0x0253:
        r14 = r3.h;
        if (r14 <= r15) goto L_0x025c;
    L_0x0257:
        r14 = org.telegram.messenger.FileLoader.getPathToAttach(r3);
        goto L_0x0275;
    L_0x025c:
        r14 = new java.io.File;
        r15 = 4;
        r11 = org.telegram.messenger.FileLoader.getDirectory(r15);
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r12.append(r7);
        r12.append(r13);
        r12 = r12.toString();
        r14.<init>(r11, r12);
    L_0x0275:
        r9.renameTo(r14);
        r9 = org.telegram.messenger.ImageLoader.getInstance();
        r11 = r8.media;
        r11 = r11.photo;
        r11 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r11);
        r9.replaceImageInCache(r4, r7, r11, r2);
        r4 = r3.location;
        r6.location = r4;
        r3 = r3.size;
        r6.size = r3;
        goto L_0x0299;
    L_0x0290:
        r4 = r4 + 1;
        r11 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r14 = 1;
        r15 = 0;
        goto L_0x01a9;
    L_0x0299:
        r1 = r1 + 1;
        r11 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r14 = 1;
        r15 = 0;
        goto L_0x0180;
    L_0x02a2:
        r1 = r10.message;
        r8.message = r1;
        r1 = r10.attachPath;
        r8.attachPath = r1;
        r1 = r10.media;
        r1 = r1.photo;
        r2 = r8.media;
        r2 = r2.photo;
        r3 = r2.id;
        r1.id = r3;
        r2 = r2.access_hash;
        r1.access_hash = r2;
        goto L_0x05cc;
    L_0x02bc:
        r3 = r8.media;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r4 == 0) goto L_0x057e;
    L_0x02c2:
        r4 = r3.document;
        if (r4 == 0) goto L_0x057e;
    L_0x02c6:
        r4 = r10.media;
        r6 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r6 == 0) goto L_0x057e;
    L_0x02cc:
        r4 = r4.document;
        if (r4 == 0) goto L_0x057e;
    L_0x02d0:
        r3 = r3.ttl_seconds;
        if (r3 != 0) goto L_0x0357;
    L_0x02d4:
        r3 = org.telegram.messenger.MessageObject.isVideoMessage(r18);
        if (r3 != 0) goto L_0x02e0;
    L_0x02da:
        r4 = org.telegram.messenger.MessageObject.isGifMessage(r18);
        if (r4 == 0) goto L_0x0322;
    L_0x02e0:
        r4 = r8.media;
        r4 = r4.document;
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r4);
        r6 = r10.media;
        r6 = r6.document;
        r6 = org.telegram.messenger.MessageObject.isGifDocument(r6);
        if (r4 != r6) goto L_0x0322;
    L_0x02f2:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r6 = r8.media;
        r6 = r6.document;
        r11 = 2;
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r12.append(r7);
        r7 = r8.to_id;
        r7 = r7.channel_id;
        r12.append(r7);
        r12.append(r5);
        r7 = r8.id;
        r12.append(r7);
        r7 = r12.toString();
        r4.putSentFile(r9, r6, r11, r7);
        if (r3 == 0) goto L_0x0357;
    L_0x031d:
        r3 = r10.attachPath;
        r8.attachPath = r3;
        goto L_0x0357;
    L_0x0322:
        r3 = org.telegram.messenger.MessageObject.isVoiceMessage(r18);
        if (r3 != 0) goto L_0x0357;
    L_0x0328:
        r3 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r18);
        if (r3 != 0) goto L_0x0357;
    L_0x032e:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r4 = r8.media;
        r4 = r4.document;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r7);
        r7 = r8.to_id;
        r7 = r7.channel_id;
        r6.append(r7);
        r6.append(r5);
        r7 = r8.id;
        r6.append(r7);
        r6 = r6.toString();
        r7 = 1;
        r3.putSentFile(r9, r4, r7, r6);
    L_0x0357:
        r3 = r10.media;
        r3 = r3.document;
        r3 = r3.thumbs;
        r4 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4);
        r6 = r8.media;
        r6 = r6.document;
        r6 = r6.thumbs;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r4);
        if (r3 == 0) goto L_0x0410;
    L_0x036f:
        r6 = r3.location;
        if (r6 == 0) goto L_0x0410;
    L_0x0373:
        r6 = r6.volume_id;
        r11 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r14 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r14 != 0) goto L_0x0410;
    L_0x037c:
        if (r4 == 0) goto L_0x0410;
    L_0x037e:
        r6 = r4.location;
        if (r6 == 0) goto L_0x0410;
    L_0x0382:
        r6 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r6 != 0) goto L_0x0410;
    L_0x0386:
        r6 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r6 != 0) goto L_0x0410;
    L_0x038a:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = r3.location;
        r11 = r7.volume_id;
        r6.append(r11);
        r6.append(r5);
        r7 = r3.location;
        r7 = r7.local_id;
        r6.append(r7);
        r6 = r6.toString();
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r11 = r4.location;
        r11 = r11.volume_id;
        r7.append(r11);
        r7.append(r5);
        r5 = r4.location;
        r5 = r5.local_id;
        r7.append(r5);
        r5 = r7.toString();
        r7 = r6.equals(r5);
        if (r7 != 0) goto L_0x0439;
    L_0x03c4:
        r7 = new java.io.File;
        r11 = 4;
        r12 = org.telegram.messenger.FileLoader.getDirectory(r11);
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r6);
        r14.append(r13);
        r14 = r14.toString();
        r7.<init>(r12, r14);
        r12 = new java.io.File;
        r14 = org.telegram.messenger.FileLoader.getDirectory(r11);
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r11.append(r5);
        r11.append(r13);
        r11 = r11.toString();
        r12.<init>(r14, r11);
        r7.renameTo(r12);
        r7 = org.telegram.messenger.ImageLoader.getInstance();
        r11 = r8.media;
        r11 = r11.document;
        r11 = org.telegram.messenger.ImageLocation.getForDocument(r4, r11);
        r7.replaceImageInCache(r6, r5, r11, r2);
        r2 = r4.location;
        r3.location = r2;
        r2 = r4.size;
        r3.size = r2;
        goto L_0x0439;
    L_0x0410:
        if (r3 == 0) goto L_0x041f;
    L_0x0412:
        r2 = org.telegram.messenger.MessageObject.isStickerMessage(r18);
        if (r2 == 0) goto L_0x041f;
    L_0x0418:
        r2 = r3.location;
        if (r2 == 0) goto L_0x041f;
    L_0x041c:
        r4.location = r2;
        goto L_0x0439;
    L_0x041f:
        if (r3 == 0) goto L_0x042d;
    L_0x0421:
        if (r3 == 0) goto L_0x0429;
    L_0x0423:
        r2 = r3.location;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r2 != 0) goto L_0x042d;
    L_0x0429:
        r2 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r2 == 0) goto L_0x0439;
    L_0x042d:
        r2 = r10.media;
        r2 = r2.document;
        r3 = r8.media;
        r3 = r3.document;
        r3 = r3.thumbs;
        r2.thumbs = r3;
    L_0x0439:
        r2 = r10.media;
        r2 = r2.document;
        r3 = r8.media;
        r3 = r3.document;
        r4 = r3.dc_id;
        r2.dc_id = r4;
        r4 = r3.id;
        r2.id = r4;
        r3 = r3.access_hash;
        r2.access_hash = r3;
        r2 = 0;
    L_0x044e:
        r3 = r10.media;
        r3 = r3.document;
        r3 = r3.attributes;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x0470;
    L_0x045a:
        r3 = r10.media;
        r3 = r3.document;
        r3 = r3.attributes;
        r3 = r3.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
        if (r4 == 0) goto L_0x046d;
    L_0x046a:
        r4 = r3.waveform;
        goto L_0x0471;
    L_0x046d:
        r2 = r2 + 1;
        goto L_0x044e;
    L_0x0470:
        r4 = 0;
    L_0x0471:
        r2 = r10.media;
        r2 = r2.document;
        r3 = r8.media;
        r3 = r3.document;
        r3 = r3.attributes;
        r2.attributes = r3;
        if (r4 == 0) goto L_0x04a7;
    L_0x047f:
        r2 = 0;
    L_0x0480:
        r3 = r10.media;
        r3 = r3.document;
        r3 = r3.attributes;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x04a7;
    L_0x048c:
        r3 = r10.media;
        r3 = r3.document;
        r3 = r3.attributes;
        r3 = r3.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3;
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
        if (r5 == 0) goto L_0x04a4;
    L_0x049c:
        r3.waveform = r4;
        r5 = r3.flags;
        r6 = 4;
        r5 = r5 | r6;
        r3.flags = r5;
    L_0x04a4:
        r2 = r2 + 1;
        goto L_0x0480;
    L_0x04a7:
        r2 = r10.media;
        r2 = r2.document;
        r3 = r8.media;
        r3 = r3.document;
        r4 = r3.size;
        r2.size = r4;
        r3 = r3.mime_type;
        r2.mime_type = r3;
        r2 = r8.flags;
        r3 = 4;
        r2 = r2 & r3;
        if (r2 != 0) goto L_0x04fa;
    L_0x04bd:
        r2 = org.telegram.messenger.MessageObject.isOut(r18);
        if (r2 == 0) goto L_0x04fa;
    L_0x04c3:
        r2 = r8.media;
        r2 = r2.document;
        r2 = org.telegram.messenger.MessageObject.isNewGifDocument(r2);
        if (r2 == 0) goto L_0x04dd;
    L_0x04cd:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);
        r3 = r8.media;
        r3 = r3.document;
        r4 = r8.date;
        r2.addRecentGif(r3, r4);
        goto L_0x04fa;
    L_0x04dd:
        r2 = r8.media;
        r2 = r2.document;
        r2 = org.telegram.messenger.MessageObject.isStickerDocument(r2);
        if (r2 == 0) goto L_0x04fa;
    L_0x04e7:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.DataQuery.getInstance(r2);
        r3 = 0;
        r4 = r8.media;
        r5 = r4.document;
        r6 = r8.date;
        r7 = 0;
        r4 = r18;
        r2.addRecentSticker(r3, r4, r5, r6, r7);
    L_0x04fa:
        r2 = r10.attachPath;
        if (r2 == 0) goto L_0x0575;
    L_0x04fe:
        r3 = 4;
        r3 = org.telegram.messenger.FileLoader.getDirectory(r3);
        r3 = r3.getAbsolutePath();
        r2 = r2.startsWith(r3);
        if (r2 == 0) goto L_0x0575;
    L_0x050d:
        r2 = new java.io.File;
        r3 = r10.attachPath;
        r2.<init>(r3);
        r3 = r8.media;
        r4 = r3.document;
        r3 = r3.ttl_seconds;
        if (r3 == 0) goto L_0x051e;
    L_0x051c:
        r3 = 1;
        goto L_0x051f;
    L_0x051e:
        r3 = 0;
    L_0x051f:
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r4, r3);
        r4 = r2.renameTo(r3);
        if (r4 != 0) goto L_0x0543;
    L_0x0529:
        r2 = r2.exists();
        if (r2 == 0) goto L_0x0534;
    L_0x052f:
        r2 = r10.attachPath;
        r8.attachPath = r2;
        goto L_0x0537;
    L_0x0534:
        r2 = 0;
        r1.attachPathExists = r2;
    L_0x0537:
        r2 = r3.exists();
        r1.mediaExists = r2;
        r1 = r10.message;
        r8.message = r1;
        goto L_0x05cc;
    L_0x0543:
        r2 = org.telegram.messenger.MessageObject.isVideoMessage(r18);
        if (r2 == 0) goto L_0x054e;
    L_0x0549:
        r2 = 1;
        r1.attachPathExists = r2;
        goto L_0x05cc;
    L_0x054e:
        r2 = r1.attachPathExists;
        r1.mediaExists = r2;
        r2 = 0;
        r1.attachPathExists = r2;
        r1 = "";
        r10.attachPath = r1;
        if (r9 == 0) goto L_0x05cc;
    L_0x055b:
        r1 = "http";
        r1 = r9.startsWith(r1);
        if (r1 == 0) goto L_0x05cc;
    L_0x0563:
        r1 = r0.currentAccount;
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r1);
        r2 = r3.toString();
        r3 = r10.media;
        r3 = r3.document;
        r1.addRecentLocalFile(r9, r2, r3);
        goto L_0x05cc;
    L_0x0575:
        r1 = r10.attachPath;
        r8.attachPath = r1;
        r1 = r10.message;
        r8.message = r1;
        goto L_0x05cc;
    L_0x057e:
        r1 = r8.media;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r2 == 0) goto L_0x058d;
    L_0x0584:
        r2 = r10.media;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r2 == 0) goto L_0x058d;
    L_0x058a:
        r10.media = r1;
        goto L_0x05cc;
    L_0x058d:
        r1 = r8.media;
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r2 == 0) goto L_0x0596;
    L_0x0593:
        r10.media = r1;
        goto L_0x05cc;
    L_0x0596:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r2 == 0) goto L_0x05a9;
    L_0x059a:
        r1 = r1.geo;
        r2 = r10.media;
        r2 = r2.geo;
        r3 = r2.lat;
        r1.lat = r3;
        r2 = r2._long;
        r1._long = r2;
        goto L_0x05cc;
    L_0x05a9:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r2 == 0) goto L_0x05c6;
    L_0x05ad:
        r10.media = r1;
        r1 = r10.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 == 0) goto L_0x05cc;
    L_0x05b5:
        r1 = r8.message;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x05cc;
    L_0x05bd:
        r1 = r8.entities;
        r10.entities = r1;
        r1 = r8.message;
        r10.message = r1;
        goto L_0x05cc;
    L_0x05c6:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r2 == 0) goto L_0x05cc;
    L_0x05ca:
        r10.media = r1;
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
        MessagesStorage.getInstance(this.currentAccount).getUnsentMessages(1000);
    }

    /* Access modifiers changed, original: protected */
    public void processUnsentMessages(ArrayList<Message> arrayList, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, ArrayList<EncryptedChat> arrayList4) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SendMessagesHelper$uLBi7R9ZOYopc-Gzh-Qk8zK9YAo(this, arrayList2, arrayList3, arrayList4, arrayList));
    }

    public /* synthetic */ void lambda$processUnsentMessages$43$SendMessagesHelper(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(arrayList3, true);
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
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        if (tL_photo2 == null) {
            tL_photo2 = new TL_photo();
        }
        tL_photo2.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        tL_photo2.sizes = arrayList;
        tL_photo2.file_reference = new byte[0];
        return tL_photo2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x015e A:{SYNTHETIC, Splitter:B:113:0x015e} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x014c A:{SYNTHETIC, Splitter:B:105:0x014c} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x01b5  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x01fa A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0406  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0414  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x041d  */
    private static boolean prepareSendingDocumentInternal(int r27, java.lang.String r28, java.lang.String r29, android.net.Uri r30, java.lang.String r31, long r32, org.telegram.messenger.MessageObject r34, java.lang.CharSequence r35, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r36, org.telegram.messenger.MessageObject r37, boolean r38) {
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
        if (r3 == 0) goto L_0x043b;
    L_0x005f:
        r8 = r2.length();
        r10 = 0;
        r3 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r3 != 0) goto L_0x006b;
    L_0x0069:
        goto L_0x043b;
    L_0x006b:
        r8 = r32;
        r3 = (int) r8;
        if (r3 != 0) goto L_0x0072;
    L_0x0070:
        r3 = 1;
        goto L_0x0073;
    L_0x0072:
        r3 = 0;
    L_0x0073:
        r20 = r3 ^ 1;
        r15 = r2.getName();
        r14 = -1;
        r13 = "";
        if (r0 == 0) goto L_0x0081;
    L_0x007e:
        r21 = r0;
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
        r21 = r13;
    L_0x0091:
        r6 = r21.toLowerCase();
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
        r22 = r10;
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
        r22 = r10;
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
        r22 = r10;
        r9 = 0;
        r10 = 0;
        r16 = 0;
    L_0x0108:
        if (r37 != 0) goto L_0x0123;
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
        r22 = r10;
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
        r22 = r10;
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
        r22 = r10;
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
        if (r1 != 0) goto L_0x027a;
    L_0x01fa:
        if (r3 != 0) goto L_0x027a;
    L_0x01fc:
        r1 = org.telegram.messenger.MessagesStorage.getInstance(r27);
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
        r15 = org.telegram.messenger.MessagesStorage.getInstance(r27);
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
        r23 = 0;
        r15 = r17;
        r13 = r27;
        r25 = -1;
        r14 = r3;
        r26 = r9;
        r28 = r10;
        r9 = r15;
        r10 = r16;
        r15 = r1;
        r16 = r7;
        r17 = r18;
        r18 = r23;
        ensureMediaThumbExists(r13, r14, r15, r16, r17, r18);
        r13 = r28;
        goto L_0x0283;
    L_0x027a:
        r26 = r9;
        r9 = r13;
        r10 = r16;
        r25 = -1;
        r1 = 0;
        r13 = 0;
    L_0x0283:
        if (r1 != 0) goto L_0x0403;
    L_0x0285:
        r1 = new org.telegram.tgnet.TLRPC$TL_document;
        r1.<init>();
        r14 = 0;
        r1.id = r14;
        r14 = org.telegram.tgnet.ConnectionsManager.getInstance(r27);
        r14 = r14.getCurrentTime();
        r1.date = r14;
        r14 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r14.<init>();
        r14.file_name = r10;
        r10 = 0;
        r15 = new byte[r10];
        r1.file_reference = r15;
        r15 = r1.attributes;
        r15.add(r14);
        r28 = r11;
        r10 = r2.length();
        r11 = (int) r10;
        r1.size = r11;
        r10 = 0;
        r1.dc_id = r10;
        if (r0 == 0) goto L_0x02bc;
    L_0x02b7:
        r10 = r1.attributes;
        r10.add(r0);
    L_0x02bc:
        r0 = r21.length();
        r10 = "application/octet-stream";
        if (r0 == 0) goto L_0x033e;
    L_0x02c4:
        r0 = r6.hashCode();
        switch(r0) {
            case 106458: goto L_0x02fa;
            case 108272: goto L_0x02f2;
            case 109967: goto L_0x02ea;
            case 3145576: goto L_0x02e0;
            case 3418175: goto L_0x02d6;
            case 3645340: goto L_0x02cc;
            default: goto L_0x02cb;
        };
    L_0x02cb:
        goto L_0x0302;
    L_0x02cc:
        r0 = "webp";
        r0 = r6.equals(r0);
        if (r0 == 0) goto L_0x0302;
    L_0x02d4:
        r4 = 0;
        goto L_0x0303;
    L_0x02d6:
        r4 = r28;
        r0 = r6.equals(r4);
        if (r0 == 0) goto L_0x0302;
    L_0x02de:
        r4 = 1;
        goto L_0x0303;
    L_0x02e0:
        r4 = r22;
        r0 = r6.equals(r4);
        if (r0 == 0) goto L_0x0302;
    L_0x02e8:
        r4 = 5;
        goto L_0x0303;
    L_0x02ea:
        r0 = r6.equals(r8);
        if (r0 == 0) goto L_0x0302;
    L_0x02f0:
        r4 = 4;
        goto L_0x0303;
    L_0x02f2:
        r0 = r6.equals(r4);
        if (r0 == 0) goto L_0x0302;
    L_0x02f8:
        r4 = 2;
        goto L_0x0303;
    L_0x02fa:
        r0 = r6.equals(r12);
        if (r0 == 0) goto L_0x0302;
    L_0x0300:
        r4 = 3;
        goto L_0x0303;
    L_0x0302:
        r4 = -1;
    L_0x0303:
        if (r4 == 0) goto L_0x0339;
    L_0x0305:
        r8 = 1;
        if (r4 == r8) goto L_0x0334;
    L_0x0308:
        r8 = 2;
        if (r4 == r8) goto L_0x032f;
    L_0x030b:
        r0 = 3;
        if (r4 == r0) goto L_0x032a;
    L_0x030e:
        r0 = 4;
        if (r4 == r0) goto L_0x0325;
    L_0x0311:
        r0 = 5;
        if (r4 == r0) goto L_0x0320;
    L_0x0314:
        r0 = r5.getMimeTypeFromExtension(r6);
        if (r0 == 0) goto L_0x031d;
    L_0x031a:
        r1.mime_type = r0;
        goto L_0x0340;
    L_0x031d:
        r1.mime_type = r10;
        goto L_0x0340;
    L_0x0320:
        r0 = "audio/flac";
        r1.mime_type = r0;
        goto L_0x0340;
    L_0x0325:
        r0 = "audio/ogg";
        r1.mime_type = r0;
        goto L_0x0340;
    L_0x032a:
        r0 = "audio/m4a";
        r1.mime_type = r0;
        goto L_0x0340;
    L_0x032f:
        r0 = "audio/mpeg";
        r1.mime_type = r0;
        goto L_0x0340;
    L_0x0334:
        r0 = "audio/opus";
        r1.mime_type = r0;
        goto L_0x0340;
    L_0x0339:
        r0 = "image/webp";
        r1.mime_type = r0;
        goto L_0x0340;
    L_0x033e:
        r1.mime_type = r10;
    L_0x0340:
        r0 = r1.mime_type;
        r4 = "image/gif";
        r0 = r0.equals(r4);
        if (r0 == 0) goto L_0x038d;
    L_0x034a:
        if (r37 == 0) goto L_0x0356;
    L_0x034c:
        r4 = r37.getGroupIdForUse();
        r10 = 0;
        r0 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r0 != 0) goto L_0x038d;
    L_0x0356:
        r0 = r2.getAbsolutePath();	 Catch:{ Exception -> 0x0389 }
        r2 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r4 = 0;
        r5 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r4, r2, r2, r5);	 Catch:{ Exception -> 0x0389 }
        if (r0 == 0) goto L_0x038d;
    L_0x0364:
        r4 = "animation.gif";
        r14.file_name = r4;	 Catch:{ Exception -> 0x0389 }
        r4 = r1.attributes;	 Catch:{ Exception -> 0x0389 }
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;	 Catch:{ Exception -> 0x0389 }
        r5.<init>();	 Catch:{ Exception -> 0x0389 }
        r4.add(r5);	 Catch:{ Exception -> 0x0389 }
        r4 = 55;
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r4, r3);	 Catch:{ Exception -> 0x0389 }
        if (r2 == 0) goto L_0x0385;
    L_0x037a:
        r3 = r1.thumbs;	 Catch:{ Exception -> 0x0389 }
        r3.add(r2);	 Catch:{ Exception -> 0x0389 }
        r2 = r1.flags;	 Catch:{ Exception -> 0x0389 }
        r3 = 1;
        r2 = r2 | r3;
        r1.flags = r2;	 Catch:{ Exception -> 0x0389 }
    L_0x0385:
        r0.recycle();	 Catch:{ Exception -> 0x0389 }
        goto L_0x038d;
    L_0x0389:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x038d:
        r0 = r1.mime_type;
        r2 = "image/webp";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x0403;
    L_0x0397:
        if (r20 == 0) goto L_0x0403;
    L_0x0399:
        if (r37 != 0) goto L_0x0403;
    L_0x039b:
        r2 = new android.graphics.BitmapFactory$Options;
        r2.<init>();
        r3 = 1;
        r2.inJustDecodeBounds = r3;	 Catch:{ Exception -> 0x03ca }
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x03ca }
        r3 = "r";
        r0.<init>(r7, r3);	 Catch:{ Exception -> 0x03ca }
        r14 = r0.getChannel();	 Catch:{ Exception -> 0x03ca }
        r15 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Exception -> 0x03ca }
        r16 = 0;
        r3 = r7.length();	 Catch:{ Exception -> 0x03ca }
        r3 = (long) r3;	 Catch:{ Exception -> 0x03ca }
        r18 = r3;
        r3 = r14.map(r15, r16, r18);	 Catch:{ Exception -> 0x03ca }
        r4 = r3.limit();	 Catch:{ Exception -> 0x03ca }
        r5 = 0;
        r6 = 1;
        org.telegram.messenger.Utilities.loadWebpImage(r5, r3, r4, r2, r6);	 Catch:{ Exception -> 0x03ca }
        r0.close();	 Catch:{ Exception -> 0x03ca }
        goto L_0x03ce;
    L_0x03ca:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03ce:
        r0 = r2.outWidth;
        if (r0 == 0) goto L_0x0403;
    L_0x03d2:
        r3 = r2.outHeight;
        if (r3 == 0) goto L_0x0403;
    L_0x03d6:
        r4 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r0 > r4) goto L_0x0403;
    L_0x03da:
        r0 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r3 > r0) goto L_0x0403;
    L_0x03de:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
        r0.<init>();
        r0.alt = r9;
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
        r3.<init>();
        r0.stickerset = r3;
        r3 = r1.attributes;
        r3.add(r0);
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
        r0.<init>();
        r3 = r2.outWidth;
        r0.w = r3;
        r2 = r2.outHeight;
        r0.h = r2;
        r2 = r1.attributes;
        r2.add(r0);
    L_0x0403:
        r3 = r1;
        if (r35 == 0) goto L_0x040c;
    L_0x0406:
        r0 = r35.toString();
        r10 = r0;
        goto L_0x040d;
    L_0x040c:
        r10 = r9;
    L_0x040d:
        r5 = new java.util.HashMap;
        r5.<init>();
        if (r26 == 0) goto L_0x041b;
    L_0x0414:
        r0 = "originalPath";
        r1 = r26;
        r5.put(r0, r1);
    L_0x041b:
        if (r38 == 0) goto L_0x0424;
    L_0x041d:
        r0 = "forceDocument";
        r1 = "1";
        r5.put(r0, r1);
    L_0x0424:
        r12 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$oWtR0AbMdP04znHWUTItf-tR4Wc;
        r0 = r12;
        r1 = r37;
        r2 = r27;
        r4 = r7;
        r6 = r13;
        r7 = r32;
        r9 = r34;
        r11 = r36;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r12);
        r1 = 1;
        return r1;
    L_0x043b:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(int, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, boolean):boolean");
    }

    static /* synthetic */ void lambda$prepareSendingDocumentInternal$44(MessageObject messageObject, int i, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, String str3, ArrayList arrayList) {
        if (messageObject != null) {
            getInstance(i).editMessageMedia(messageObject, null, null, tL_document, str, hashMap, false, str2);
        } else {
            getInstance(i).sendMessage(tL_document, null, str, j, messageObject2, str3, arrayList, null, hashMap, 0, str2);
        }
    }

    public static void prepareSendingDocument(String str, String str2, Uri uri, String str3, String str4, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject2) {
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
            ArrayList arrayList4 = arrayList3;
            if (str5 != null) {
                arrayList.add(str);
                arrayList2.add(str2);
            }
            prepareSendingDocuments(arrayList, arrayList2, arrayList4, str3, str4, j, messageObject, inputContentInfoCompat, messageObject2);
        }
    }

    public static void prepareSendingAudioDocuments(ArrayList<MessageObject> arrayList, long j, MessageObject messageObject, MessageObject messageObject2) {
        new Thread(new -$$Lambda$SendMessagesHelper$4kP0iTjQJZDiTNhQsrN7GEn6QHw(arrayList, j, UserConfig.selectedAccount, messageObject2, messageObject)).start();
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0096 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0091  */
    static /* synthetic */ void lambda$prepareSendingAudioDocuments$46(java.util.ArrayList r23, long r24, int r26, org.telegram.messenger.MessageObject r27, org.telegram.messenger.MessageObject r28) {
        /*
        r10 = r24;
        r12 = r23.size();
        r13 = 0;
        r14 = 0;
    L_0x0008:
        if (r14 >= r12) goto L_0x00ac;
    L_0x000a:
        r15 = r23;
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
        if (r2 != 0) goto L_0x0068;
    L_0x0040:
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r26);
        if (r2 != 0) goto L_0x0048;
    L_0x0046:
        r6 = 1;
        goto L_0x0049;
    L_0x0048:
        r6 = 4;
    L_0x0049:
        r5 = r5.getSentFile(r0, r6);
        if (r5 == 0) goto L_0x0068;
    L_0x004f:
        r1 = r5[r13];
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;
        r3 = r5[r3];
        r3 = (java.lang.String) r3;
        r20 = 0;
        r21 = 0;
        r16 = r26;
        r17 = r2;
        r18 = r1;
        r19 = r0;
        ensureMediaThumbExists(r16, r17, r18, r19, r20, r21);
        r6 = r3;
        goto L_0x0069;
    L_0x0068:
        r6 = r1;
    L_0x0069:
        if (r1 != 0) goto L_0x0073;
    L_0x006b:
        r1 = r4.messageOwner;
        r1 = r1.media;
        r1 = r1.document;
        r1 = (org.telegram.tgnet.TLRPC.TL_document) r1;
    L_0x0073:
        r3 = r1;
        if (r2 == 0) goto L_0x008a;
    L_0x0076:
        r1 = 32;
        r1 = r10 >> r1;
        r2 = (int) r1;
        r1 = org.telegram.messenger.MessagesController.getInstance(r26);
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getEncryptedChat(r2);
        if (r1 != 0) goto L_0x008a;
    L_0x0089:
        return;
    L_0x008a:
        r5 = new java.util.HashMap;
        r5.<init>();
        if (r0 == 0) goto L_0x0096;
    L_0x0091:
        r1 = "originalPath";
        r5.put(r1, r0);
    L_0x0096:
        r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$ScXQOgmc-sCdZ0TlvzYeQcRtFgQ;
        r0 = r16;
        r1 = r27;
        r2 = r26;
        r7 = r24;
        r9 = r28;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);
        r14 = r14 + 1;
        goto L_0x0008;
    L_0x00ac:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$46(java.util.ArrayList, long, int, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject):void");
    }

    static /* synthetic */ void lambda$null$45(MessageObject messageObject, int i, TL_document tL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3) {
        MessageObject messageObject4 = messageObject2;
        if (messageObject != null) {
            getInstance(i).editMessageMedia(messageObject, null, null, tL_document, messageObject4.messageOwner.attachPath, hashMap, false, str);
            return;
        }
        getInstance(i).sendMessage(tL_document, null, messageObject4.messageOwner.attachPath, j, messageObject3, null, null, null, hashMap, 0, str);
    }

    public static void prepareSendingDocuments(ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<Uri> arrayList3, String str, String str2, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, MessageObject messageObject2) {
        if (!(arrayList == null && arrayList2 == null && arrayList3 == null) && (arrayList == null || arrayList2 == null || arrayList.size() == arrayList2.size())) {
            new Thread(new -$$Lambda$SendMessagesHelper$u-MDqklxXvAmqp9ZSz6JzWZDqR0(arrayList, UserConfig.selectedAccount, arrayList2, str2, j, messageObject, str, messageObject2, arrayList3, inputContentInfoCompat)).start();
        }
    }

    static /* synthetic */ void lambda$prepareSendingDocuments$48(ArrayList arrayList, int i, ArrayList arrayList2, String str, long j, MessageObject messageObject, String str2, MessageObject messageObject2, ArrayList arrayList3, InputContentInfoCompat inputContentInfoCompat) {
        Object obj;
        ArrayList arrayList4 = arrayList;
        ArrayList arrayList5 = arrayList3;
        if (arrayList4 != null) {
            obj = null;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (!prepareSendingDocumentInternal(i, (String) arrayList4.get(i2), (String) arrayList2.get(i2), null, str, j, messageObject, str2, null, messageObject2, false)) {
                    obj = 1;
                }
            }
        } else {
            obj = null;
        }
        if (arrayList5 != null) {
            for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                if (!prepareSendingDocumentInternal(i, null, null, (Uri) arrayList5.get(i3), str, j, messageObject, str2, null, messageObject2, false)) {
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

    public static void prepareSendingPhoto(String str, Uri uri, long j, MessageObject messageObject, CharSequence charSequence, ArrayList<MessageEntity> arrayList, ArrayList<InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject2) {
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
        prepareSendingMedia(arrayList4, j, messageObject, inputContentInfoCompat, false, false, messageObject2);
    }

    public static void prepareSendingBotContextResult(BotInlineResult botInlineResult, HashMap<String, String> hashMap, long j, MessageObject messageObject) {
        BotInlineResult botInlineResult2 = botInlineResult;
        if (botInlineResult2 != null) {
            int i = UserConfig.selectedAccount;
            BotInlineMessage botInlineMessage = botInlineResult2.send_message;
            if (botInlineMessage instanceof TL_botInlineMessageMediaAuto) {
                new Thread(new -$$Lambda$SendMessagesHelper$6wwCb-aD-ompqDA1xQoA5U41dQc(j, botInlineResult, i, hashMap, messageObject)).run();
            } else if (botInlineMessage instanceof TL_botInlineMessageText) {
                WebPage webPage = null;
                if (((int) j) == 0) {
                    for (int i2 = 0; i2 < botInlineResult2.send_message.entities.size(); i2++) {
                        MessageEntity messageEntity = (MessageEntity) botInlineResult2.send_message.entities.get(i2);
                        if (messageEntity instanceof TL_messageEntityUrl) {
                            webPage = new TL_webPagePending();
                            String str = botInlineResult2.send_message.message;
                            int i3 = messageEntity.offset;
                            webPage.url = str.substring(i3, messageEntity.length + i3);
                            break;
                        }
                    }
                }
                WebPage webPage2 = webPage;
                SendMessagesHelper instance = getInstance(i);
                botInlineMessage = botInlineResult2.send_message;
                instance.sendMessage(botInlineMessage.message, j, messageObject, webPage2, botInlineMessage.no_webpage ^ 1, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap);
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
                    getInstance(i).sendMessage(tL_messageMediaVenue, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap);
                } else if (botInlineMessage instanceof TL_botInlineMessageMediaGeo) {
                    if (botInlineMessage.period != 0) {
                        tL_messageMediaVenue = new TL_messageMediaGeoLive();
                        botInlineMessage = botInlineResult2.send_message;
                        tL_messageMediaVenue.period = botInlineMessage.period;
                        tL_messageMediaVenue.geo = botInlineMessage.geo;
                        getInstance(i).sendMessage(tL_messageMediaVenue, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap);
                    } else {
                        tL_messageMediaVenue = new TL_messageMediaGeo();
                        tL_messageMediaVenue.geo = botInlineResult2.send_message.geo;
                        getInstance(i).sendMessage(tL_messageMediaVenue, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap);
                    }
                } else if (botInlineMessage instanceof TL_botInlineMessageMediaContact) {
                    User tL_user = new TL_user();
                    botInlineMessage = botInlineResult2.send_message;
                    tL_user.phone = botInlineMessage.phone_number;
                    tL_user.first_name = botInlineMessage.first_name;
                    tL_user.last_name = botInlineMessage.last_name;
                    tL_user.restriction_reason = botInlineMessage.vcard;
                    getInstance(i).sendMessage(tL_user, j, messageObject, botInlineResult2.send_message.reply_markup, (HashMap) hashMap);
                }
            }
        }
    }

    static /* synthetic */ void lambda$prepareSendingBotContextResult$50(long r17, org.telegram.tgnet.TLRPC.BotInlineResult r19, int r20, java.util.HashMap r21, org.telegram.messenger.MessageObject r22) {
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
        goto L_0x0402;
    L_0x004e:
        r0 = r8 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMediaResult;
        if (r0 == 0) goto L_0x0070;
    L_0x0052:
        r0 = r8.document;
        if (r0 == 0) goto L_0x0060;
    L_0x0056:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r1 == 0) goto L_0x03fe;
    L_0x005a:
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;
        r2 = r0;
        r4 = r7;
        goto L_0x0400;
    L_0x0060:
        r0 = r8.photo;
        if (r0 == 0) goto L_0x03fe;
    L_0x0064:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r1 == 0) goto L_0x03fe;
    L_0x0068:
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        r10 = r0;
        r2 = r7;
        r4 = r2;
        r11 = r4;
        goto L_0x0402;
    L_0x0070:
        r0 = r8.content;
        if (r0 == 0) goto L_0x03fe;
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
            case 0: goto L_0x015c;
            case 1: goto L_0x015c;
            case 2: goto L_0x015c;
            case 3: goto L_0x015c;
            case 4: goto L_0x015c;
            case 5: goto L_0x015c;
            case 6: goto L_0x010b;
            default: goto L_0x0105;
        };
    L_0x0105:
        r2 = r7;
        r11 = r2;
        r4 = r10;
        r10 = r11;
        goto L_0x0402;
    L_0x010b:
        r0 = r0.exists();
        if (r0 == 0) goto L_0x011a;
    L_0x0111:
        r0 = getInstance(r20);
        r0 = r0.generatePhotoSizes(r10, r7);
        goto L_0x011b;
    L_0x011a:
        r0 = r7;
    L_0x011b:
        if (r0 != 0) goto L_0x0156;
    L_0x011d:
        r0 = new org.telegram.tgnet.TLRPC$TL_photo;
        r0.<init>();
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r20);
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
    L_0x0156:
        r2 = r7;
        r11 = r2;
        r4 = r10;
        r10 = r0;
        goto L_0x0402;
    L_0x015c:
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
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r20);
        r1 = r1.getCurrentTime();
        r11.date = r1;
        r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r1.<init>();
        r2 = r11.attributes;
        r2.add(r1);
        r2 = r8.type;
        r16 = r2.hashCode();
        switch(r16) {
            case -1890252483: goto L_0x01bf;
            case 102340: goto L_0x01b7;
            case 3143036: goto L_0x01af;
            case 93166550: goto L_0x01a7;
            case 112202875: goto L_0x019d;
            case 112386354: goto L_0x0193;
            default: goto L_0x0192;
        };
    L_0x0192:
        goto L_0x01c7;
    L_0x0193:
        r0 = "voice";
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x01c7;
    L_0x019b:
        r0 = 1;
        goto L_0x01c8;
    L_0x019d:
        r0 = "video";
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x01c7;
    L_0x01a5:
        r0 = 4;
        goto L_0x01c8;
    L_0x01a7:
        r0 = r2.equals(r15);
        if (r0 == 0) goto L_0x01c7;
    L_0x01ad:
        r0 = 2;
        goto L_0x01c8;
    L_0x01af:
        r0 = r2.equals(r14);
        if (r0 == 0) goto L_0x01c7;
    L_0x01b5:
        r0 = 3;
        goto L_0x01c8;
    L_0x01b7:
        r0 = r2.equals(r4);
        if (r0 == 0) goto L_0x01c7;
    L_0x01bd:
        r0 = 0;
        goto L_0x01c8;
    L_0x01bf:
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x01c7;
    L_0x01c5:
        r0 = 5;
        goto L_0x01c8;
    L_0x01c7:
        r0 = -1;
    L_0x01c8:
        r2 = 55;
        if (r0 == 0) goto L_0x035c;
    L_0x01cc:
        r4 = 1;
        if (r0 == r4) goto L_0x0344;
    L_0x01cf:
        r3 = 2;
        if (r0 == r3) goto L_0x0318;
    L_0x01d2:
        r3 = 3;
        if (r0 == r3) goto L_0x02e8;
    L_0x01d5:
        r3 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r4 = 4;
        if (r0 == r4) goto L_0x026a;
    L_0x01da:
        r4 = 5;
        if (r0 == r4) goto L_0x01df;
    L_0x01dd:
        goto L_0x03b9;
    L_0x01df:
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
        r0 = r8.thumb;	 Catch:{ Throwable -> 0x0264 }
        if (r0 == 0) goto L_0x03b9;
    L_0x0214:
        r0 = new java.io.File;	 Catch:{ Throwable -> 0x0264 }
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ Throwable -> 0x0264 }
        r13 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0264 }
        r13.<init>();	 Catch:{ Throwable -> 0x0264 }
        r15 = r8.thumb;	 Catch:{ Throwable -> 0x0264 }
        r15 = r15.url;	 Catch:{ Throwable -> 0x0264 }
        r15 = org.telegram.messenger.Utilities.MD5(r15);	 Catch:{ Throwable -> 0x0264 }
        r13.append(r15);	 Catch:{ Throwable -> 0x0264 }
        r13.append(r12);	 Catch:{ Throwable -> 0x0264 }
        r12 = r8.thumb;	 Catch:{ Throwable -> 0x0264 }
        r12 = r12.url;	 Catch:{ Throwable -> 0x0264 }
        r15 = "webp";
        r12 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r12, r15);	 Catch:{ Throwable -> 0x0264 }
        r13.append(r12);	 Catch:{ Throwable -> 0x0264 }
        r12 = r13.toString();	 Catch:{ Throwable -> 0x0264 }
        r0.<init>(r4, r12);	 Catch:{ Throwable -> 0x0264 }
        r0 = r0.getAbsolutePath();	 Catch:{ Throwable -> 0x0264 }
        r4 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r7, r3, r3, r4);	 Catch:{ Throwable -> 0x0264 }
        if (r0 == 0) goto L_0x03b9;
    L_0x024d:
        r4 = 0;
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r4);	 Catch:{ Throwable -> 0x0264 }
        if (r2 == 0) goto L_0x025f;
    L_0x0254:
        r3 = r11.thumbs;	 Catch:{ Throwable -> 0x0264 }
        r3.add(r2);	 Catch:{ Throwable -> 0x0264 }
        r2 = r11.flags;	 Catch:{ Throwable -> 0x0264 }
        r3 = 1;
        r2 = r2 | r3;
        r11.flags = r2;	 Catch:{ Throwable -> 0x0264 }
    L_0x025f:
        r0.recycle();	 Catch:{ Throwable -> 0x0264 }
        goto L_0x03b9;
    L_0x0264:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x03b9;
    L_0x026a:
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
        r0 = r8.thumb;	 Catch:{ Throwable -> 0x02e2 }
        if (r0 == 0) goto L_0x03b9;
    L_0x0292:
        r0 = new java.io.File;	 Catch:{ Throwable -> 0x02e2 }
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ Throwable -> 0x02e2 }
        r13 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x02e2 }
        r13.<init>();	 Catch:{ Throwable -> 0x02e2 }
        r15 = r8.thumb;	 Catch:{ Throwable -> 0x02e2 }
        r15 = r15.url;	 Catch:{ Throwable -> 0x02e2 }
        r15 = org.telegram.messenger.Utilities.MD5(r15);	 Catch:{ Throwable -> 0x02e2 }
        r13.append(r15);	 Catch:{ Throwable -> 0x02e2 }
        r13.append(r12);	 Catch:{ Throwable -> 0x02e2 }
        r12 = r8.thumb;	 Catch:{ Throwable -> 0x02e2 }
        r12 = r12.url;	 Catch:{ Throwable -> 0x02e2 }
        r15 = "jpg";
        r12 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r12, r15);	 Catch:{ Throwable -> 0x02e2 }
        r13.append(r12);	 Catch:{ Throwable -> 0x02e2 }
        r12 = r13.toString();	 Catch:{ Throwable -> 0x02e2 }
        r0.<init>(r4, r12);	 Catch:{ Throwable -> 0x02e2 }
        r0 = r0.getAbsolutePath();	 Catch:{ Throwable -> 0x02e2 }
        r4 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r7, r3, r3, r4);	 Catch:{ Throwable -> 0x02e2 }
        if (r0 == 0) goto L_0x03b9;
    L_0x02cb:
        r4 = 0;
        r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r3, r2, r4);	 Catch:{ Throwable -> 0x02e2 }
        if (r2 == 0) goto L_0x02dd;
    L_0x02d2:
        r3 = r11.thumbs;	 Catch:{ Throwable -> 0x02e2 }
        r3.add(r2);	 Catch:{ Throwable -> 0x02e2 }
        r2 = r11.flags;	 Catch:{ Throwable -> 0x02e2 }
        r3 = 1;
        r2 = r2 | r3;
        r11.flags = r2;	 Catch:{ Throwable -> 0x02e2 }
    L_0x02dd:
        r0.recycle();	 Catch:{ Throwable -> 0x02e2 }
        goto L_0x03b9;
    L_0x02e2:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x03b9;
    L_0x02e8:
        r0 = r8.content;
        r0 = r0.mime_type;
        r2 = 47;
        r0 = r0.lastIndexOf(r2);
        r2 = -1;
        if (r0 == r2) goto L_0x0314;
    L_0x02f5:
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
        goto L_0x03b9;
    L_0x0314:
        r1.file_name = r14;
        goto L_0x03b9;
    L_0x0318:
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
        if (r2 == 0) goto L_0x0339;
    L_0x0331:
        r0.performer = r2;
        r2 = r0.flags;
        r3 = 2;
        r2 = r2 | r3;
        r0.flags = r2;
    L_0x0339:
        r2 = "audio.mp3";
        r1.file_name = r2;
        r2 = r11.attributes;
        r2.add(r0);
        goto L_0x03b9;
    L_0x0344:
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
        goto L_0x03b9;
    L_0x035c:
        r0 = "animation.gif";
        r1.file_name = r0;
        r0 = "mp4";
        r0 = r10.endsWith(r0);
        if (r0 == 0) goto L_0x0377;
    L_0x0368:
        r0 = "video/mp4";
        r11.mime_type = r0;
        r0 = r11.attributes;
        r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r4.<init>();
        r0.add(r4);
        goto L_0x037b;
    L_0x0377:
        r0 = "image/gif";
        r11.mime_type = r0;
    L_0x037b:
        if (r3 == 0) goto L_0x0380;
    L_0x037d:
        r0 = 90;
        goto L_0x0382;
    L_0x0380:
        r0 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
    L_0x0382:
        r3 = "mp4";
        r3 = r10.endsWith(r3);	 Catch:{ Throwable -> 0x03b5 }
        if (r3 == 0) goto L_0x0390;
    L_0x038a:
        r3 = 1;
        r4 = android.media.ThumbnailUtils.createVideoThumbnail(r10, r3);	 Catch:{ Throwable -> 0x03b5 }
        goto L_0x0396;
    L_0x0390:
        r3 = 1;
        r4 = (float) r0;	 Catch:{ Throwable -> 0x03b5 }
        r4 = org.telegram.messenger.ImageLoader.loadBitmap(r10, r7, r4, r4, r3);	 Catch:{ Throwable -> 0x03b5 }
    L_0x0396:
        if (r4 == 0) goto L_0x03b9;
    L_0x0398:
        r3 = (float) r0;	 Catch:{ Throwable -> 0x03b5 }
        r12 = 90;
        if (r0 <= r12) goto L_0x039f;
    L_0x039d:
        r2 = 80;
    L_0x039f:
        r12 = 0;
        r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r4, r3, r3, r2, r12);	 Catch:{ Throwable -> 0x03b5 }
        if (r0 == 0) goto L_0x03b1;
    L_0x03a6:
        r2 = r11.thumbs;	 Catch:{ Throwable -> 0x03b5 }
        r2.add(r0);	 Catch:{ Throwable -> 0x03b5 }
        r0 = r11.flags;	 Catch:{ Throwable -> 0x03b5 }
        r2 = 1;
        r0 = r0 | r2;
        r11.flags = r0;	 Catch:{ Throwable -> 0x03b5 }
    L_0x03b1:
        r4.recycle();	 Catch:{ Throwable -> 0x03b5 }
        goto L_0x03b9;
    L_0x03b5:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03b9:
        r0 = r1.file_name;
        if (r0 != 0) goto L_0x03bf;
    L_0x03bd:
        r1.file_name = r14;
    L_0x03bf:
        r0 = r11.mime_type;
        if (r0 != 0) goto L_0x03c7;
    L_0x03c3:
        r0 = "application/octet-stream";
        r11.mime_type = r0;
    L_0x03c7:
        r0 = r11.thumbs;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x03fa;
    L_0x03cf:
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
    L_0x03fa:
        r4 = r10;
        r2 = r11;
        r10 = r7;
        goto L_0x0401;
    L_0x03fe:
        r2 = r7;
        r4 = r2;
    L_0x0400:
        r10 = r4;
    L_0x0401:
        r11 = r10;
    L_0x0402:
        if (r9 == 0) goto L_0x040f;
    L_0x0404:
        r0 = r8.content;
        if (r0 == 0) goto L_0x040f;
    L_0x0408:
        r0 = r0.url;
        r1 = "originalPath";
        r9.put(r1, r0);
    L_0x040f:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$CPpK9yWRb5dnrmUMGY1znfyz2J0;
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$50(long, org.telegram.tgnet.TLRPC$BotInlineResult, int, java.util.HashMap, org.telegram.messenger.MessageObject):void");
    }

    static /* synthetic */ void lambda$null$49(TL_document tL_document, int i, String str, long j, MessageObject messageObject, BotInlineResult botInlineResult, HashMap hashMap, TL_photo tL_photo, TL_game tL_game) {
        BotInlineResult botInlineResult2 = botInlineResult;
        SendMessagesHelper instance;
        BotInlineMessage botInlineMessage;
        if (tL_document != null) {
            instance = getInstance(i);
            botInlineMessage = botInlineResult2.send_message;
            instance.sendMessage(tL_document, null, str, j, messageObject, botInlineMessage.message, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap, 0, botInlineResult);
        } else if (tL_photo != null) {
            instance = getInstance(i);
            WebDocument webDocument = botInlineResult2.content;
            String str2 = webDocument != null ? webDocument.url : null;
            botInlineMessage = botInlineResult2.send_message;
            instance.sendMessage(tL_photo, str2, j, messageObject, botInlineMessage.message, botInlineMessage.entities, botInlineMessage.reply_markup, hashMap, 0, botInlineResult);
        } else if (tL_game != null) {
            getInstance(i).sendMessage(tL_game, j, botInlineResult2.send_message.reply_markup, hashMap);
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

    public static void prepareSendingText(String str, long j) {
        int i = UserConfig.selectedAccount;
        MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new -$$Lambda$SendMessagesHelper$Hr6-JyEMN0ABFvY-59Un0Q8hT8s(str, i, j));
    }

    static /* synthetic */ void lambda$null$51(String str, int i, long j) {
        String trimmedString = getTrimmedString(str);
        if (trimmedString.length() != 0) {
            int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / 4096.0f));
            int i2 = 0;
            while (i2 < ceil) {
                int i3 = i2 * 4096;
                i2++;
                getInstance(i).sendMessage(trimmedString.substring(i3, Math.min(i2 * 4096, trimmedString.length())), j, null, null, true, null, null, null);
            }
        }
    }

    public static void ensureMediaThumbExists(int i, boolean z, TLObject tLObject, String str, Uri uri, long j) {
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
                int i2 = 320;
                scaleAndSaveImage = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                if (!((scaleAndSaveImage instanceof TL_photoStrippedSize) || FileLoader.getPathToAttach(scaleAndSaveImage, true).exists())) {
                    Bitmap createVideoThumbnail = createVideoThumbnail(str2, j);
                    loadBitmap = createVideoThumbnail == null ? ThumbnailUtils.createVideoThumbnail(str2, 1) : createVideoThumbnail;
                    if (z) {
                        i2 = 90;
                    }
                    float f = (float) i2;
                    document.thumbs.set(0, ImageLoader.scaleAndSaveImage(scaleAndSaveImage, loadBitmap, f, f, i2 > 90 ? 80 : 55, false));
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

    public static void prepareSendingMedia(ArrayList<SendingMediaInfo> arrayList, long j, MessageObject messageObject, InputContentInfoCompat inputContentInfoCompat, boolean z, boolean z2, MessageObject messageObject2) {
        if (!arrayList.isEmpty()) {
            mediaSendQueue.postRunnable(new -$$Lambda$SendMessagesHelper$nIPx8XV4Osh4kaHGRtjXGnNga_o(arrayList, j, UserConfig.selectedAccount, z, z2, messageObject2, messageObject, inputContentInfoCompat));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0148  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02d5 A:{Catch:{ Exception -> 0x02bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x02ca A:{Catch:{ Exception -> 0x02bb }} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02e2 A:{Catch:{ Exception -> 0x0301 }} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x040c  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x06f3  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x0618  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0707  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0719  */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x075f  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0a80  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x08e6  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x08a3  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x08a3  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x08e6  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x08e6  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x08a3  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x089d  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0893  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0b1d  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0b12  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0b21  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0b33 A:{LOOP_END, LOOP:4: B:459:0x0b2d->B:461:0x0b33} */
    /* JADX WARNING: Removed duplicated region for block: B:479:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0b6c  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x033b  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0351  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0a51  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0a80  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x030f  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x033b  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0351  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0a80  */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0a51  */
    /* JADX WARNING: Missing block: B:40:0x009d, code skipped:
            if (org.telegram.messenger.MediaController.isWebp(r3.uri) != false) goto L_0x0160;
     */
    /* JADX WARNING: Missing block: B:419:0x0a2b, code skipped:
            if (r53.size() == 1) goto L_0x0a34;
     */
    /* JADX WARNING: Missing block: B:438:0x0a71, code skipped:
            if (r11 == (r20 - 1)) goto L_0x0a76;
     */
    static /* synthetic */ void lambda$prepareSendingMedia$60(java.util.ArrayList r53, long r54, int r56, boolean r57, boolean r58, org.telegram.messenger.MessageObject r59, org.telegram.messenger.MessageObject r60, androidx.core.view.inputmethod.InputContentInfoCompat r61) {
        /*
        r1 = r53;
        r14 = r54;
        r13 = r56;
        r16 = java.lang.System.currentTimeMillis();
        r12 = r53.size();
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
        r2 = org.telegram.messenger.MessagesController.getInstance(r56);
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r2.getEncryptedChat(r0);
        if (r0 == 0) goto L_0x0031;
    L_0x0029:
        r0 = r0.layer;
        r0 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r0);
        r7 = r0;
        goto L_0x0032;
    L_0x0031:
        r7 = 0;
    L_0x0032:
        r8 = ".gif";
        r18 = 3;
        r6 = "_";
        if (r10 == 0) goto L_0x003e;
    L_0x003a:
        r0 = 73;
        if (r7 < r0) goto L_0x0177;
    L_0x003e:
        if (r57 != 0) goto L_0x0177;
    L_0x0040:
        if (r58 == 0) goto L_0x0177;
    L_0x0042:
        r0 = new java.util.HashMap;
        r0.<init>();
        r4 = 0;
    L_0x0048:
        if (r4 >= r12) goto L_0x0170;
    L_0x004a:
        r2 = r1.get(r4);
        r3 = r2;
        r3 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r3;
        r2 = r3.searchImage;
        if (r2 != 0) goto L_0x0160;
    L_0x0055:
        r2 = r3.isVideo;
        if (r2 != 0) goto L_0x0160;
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
        r20 = r2.endsWith(r8);
        if (r20 != 0) goto L_0x0160;
    L_0x0075:
        r9 = ".webp";
        r9 = r2.endsWith(r9);
        if (r9 == 0) goto L_0x007f;
    L_0x007d:
        goto L_0x0160;
    L_0x007f:
        r9 = r3.path;
        r11 = r3.uri;
        r9 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r9, r11);
        if (r9 == 0) goto L_0x008b;
    L_0x0089:
        goto L_0x0160;
    L_0x008b:
        if (r2 != 0) goto L_0x00a1;
    L_0x008d:
        r9 = r3.uri;
        if (r9 == 0) goto L_0x00a1;
    L_0x0091:
        r9 = org.telegram.messenger.MediaController.isGif(r9);
        if (r9 != 0) goto L_0x0160;
    L_0x0097:
        r9 = r3.uri;
        r9 = org.telegram.messenger.MediaController.isWebp(r9);
        if (r9 == 0) goto L_0x00a1;
    L_0x009f:
        goto L_0x0160;
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
        r2.append(r6);
        r4 = r9.lastModified();
        r2.append(r4);
        r5 = r2.toString();
        goto L_0x00c9;
    L_0x00c7:
        r11 = r4;
        r5 = 0;
    L_0x00c9:
        if (r10 != 0) goto L_0x0133;
    L_0x00cb:
        r2 = r3.ttl;
        if (r2 != 0) goto L_0x0133;
    L_0x00cf:
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r56);
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
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r56);
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
        r5 = r3.path;
        r4 = r3.uri;
        r23 = 0;
        r2 = r56;
        r25 = r3;
        r3 = r10;
        r26 = r4;
        r4 = r9;
        r15 = r6;
        r6 = r26;
        r14 = r7;
        r28 = r8;
        r7 = r23;
        ensureMediaThumbExists(r2, r3, r4, r5, r6, r7);
        r2 = r22;
        goto L_0x013b;
    L_0x0133:
        r25 = r3;
        r15 = r6;
        r14 = r7;
        r28 = r8;
        r2 = 0;
        r9 = 0;
    L_0x013b:
        r3 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker;
        r8 = 0;
        r3.<init>(r8);
        r4 = r25;
        r0.put(r4, r3);
        if (r9 == 0) goto L_0x014d;
    L_0x0148:
        r3.parentObject = r2;
        r3.photo = r9;
        goto L_0x0166;
    L_0x014d:
        r2 = new java.util.concurrent.CountDownLatch;
        r5 = 1;
        r2.<init>(r5);
        r3.sync = r2;
        r2 = mediaSendThreadPool;
        r5 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$0g5iS90DmKAoHgZX7FJyyhE5gX4;
        r5.<init>(r3, r13, r4, r10);
        r2.execute(r5);
        goto L_0x0166;
    L_0x0160:
        r11 = r4;
        r15 = r6;
        r14 = r7;
        r28 = r8;
        r8 = 0;
    L_0x0166:
        r4 = r11 + 1;
        r7 = r14;
        r6 = r15;
        r8 = r28;
        r14 = r54;
        goto L_0x0048;
    L_0x0170:
        r15 = r6;
        r14 = r7;
        r28 = r8;
        r8 = 0;
        r11 = r0;
        goto L_0x017d;
    L_0x0177:
        r15 = r6;
        r14 = r7;
        r28 = r8;
        r8 = 0;
        r11 = r8;
    L_0x017d:
        r4 = r8;
        r5 = r4;
        r19 = r5;
        r29 = r19;
        r30 = r29;
        r0 = 0;
        r2 = 0;
        r9 = 0;
        r22 = 0;
    L_0x018b:
        if (r9 >= r12) goto L_0x0b07;
    L_0x018d:
        r24 = r1.get(r9);
        r8 = r24;
        r8 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r8;
        if (r58 == 0) goto L_0x01b0;
    L_0x0197:
        if (r10 == 0) goto L_0x019d;
    L_0x0199:
        r6 = 73;
        if (r14 < r6) goto L_0x01b0;
    L_0x019d:
        r6 = 1;
        if (r12 <= r6) goto L_0x01b0;
    L_0x01a0:
        r6 = r0 % 10;
        if (r6 != 0) goto L_0x01b0;
    L_0x01a4:
        r0 = org.telegram.messenger.Utilities.random;
        r2 = r0.nextLong();
        r6 = r2;
        r31 = r6;
        r22 = 0;
        goto L_0x01b6;
    L_0x01b0:
        r31 = r2;
        r6 = r22;
        r22 = r0;
    L_0x01b6:
        r0 = r8.searchImage;
        r2 = "mp4";
        r3 = "originalPath";
        r33 = r6;
        r6 = "";
        r26 = 4;
        if (r0 == 0) goto L_0x0510;
    L_0x01c4:
        r7 = r0.type;
        r1 = "jpg";
        r36 = r4;
        r4 = ".";
        r37 = r5;
        r5 = 1;
        if (r7 != r5) goto L_0x039c;
    L_0x01d1:
        r7 = new java.util.HashMap;
        r7.<init>();
        r23 = 0;
        r0 = r8.searchImage;
        r0 = r0.document;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r6 == 0) goto L_0x01e8;
    L_0x01e0:
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;
        r6 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r5);
        r5 = r6;
        goto L_0x0214;
    L_0x01e8:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r5 = r8.searchImage;
        r5 = r5.imageUrl;
        r5 = org.telegram.messenger.Utilities.MD5(r5);
        r0.append(r5);
        r0.append(r4);
        r5 = r8.searchImage;
        r5 = r5.imageUrl;
        r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r1);
        r0.append(r5);
        r0 = r0.toString();
        r5 = new java.io.File;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r26);
        r5.<init>(r6, r0);
        r0 = 0;
    L_0x0214:
        if (r0 != 0) goto L_0x0340;
    L_0x0216:
        r0 = r8.searchImage;
        r0 = r0.localUrl;
        if (r0 == 0) goto L_0x0221;
    L_0x021c:
        r6 = "url";
        r7.put(r6, r0);
    L_0x0221:
        r6 = new org.telegram.tgnet.TLRPC$TL_document;
        r6.<init>();
        r39 = r11;
        r38 = r12;
        r11 = 0;
        r6.id = r11;
        r11 = 0;
        r0 = new byte[r11];
        r6.file_reference = r0;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r56);
        r0 = r0.getCurrentTime();
        r6.date = r0;
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r0.<init>();
        r11 = "animation.gif";
        r0.file_name = r11;
        r11 = r6.attributes;
        r11.add(r0);
        r0 = r8.searchImage;
        r0 = r0.size;
        r6.size = r0;
        r11 = 0;
        r6.dc_id = r11;
        r0 = r5.toString();
        r0 = r0.endsWith(r2);
        if (r0 == 0) goto L_0x026d;
    L_0x025e:
        r0 = "video/mp4";
        r6.mime_type = r0;
        r0 = r6.attributes;
        r11 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r11.<init>();
        r0.add(r11);
        goto L_0x0271;
    L_0x026d:
        r0 = "image/gif";
        r6.mime_type = r0;
    L_0x0271:
        r0 = r5.exists();
        if (r0 == 0) goto L_0x0279;
    L_0x0277:
        r11 = r5;
        goto L_0x027b;
    L_0x0279:
        r5 = 0;
        r11 = 0;
    L_0x027b:
        if (r5 != 0) goto L_0x02af;
    L_0x027d:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r5 = r8.searchImage;
        r5 = r5.thumbUrl;
        r5 = org.telegram.messenger.Utilities.MD5(r5);
        r0.append(r5);
        r0.append(r4);
        r4 = r8.searchImage;
        r4 = r4.thumbUrl;
        r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r1);
        r0.append(r1);
        r0 = r0.toString();
        r5 = new java.io.File;
        r1 = org.telegram.messenger.FileLoader.getDirectory(r26);
        r5.<init>(r1, r0);
        r0 = r5.exists();
        if (r0 != 0) goto L_0x02af;
    L_0x02ae:
        r5 = 0;
    L_0x02af:
        if (r5 == 0) goto L_0x0306;
    L_0x02b1:
        if (r10 != 0) goto L_0x02be;
    L_0x02b3:
        r0 = r8.ttl;	 Catch:{ Exception -> 0x02bb }
        if (r0 == 0) goto L_0x02b8;
    L_0x02b7:
        goto L_0x02be;
    L_0x02b8:
        r0 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x02c0;
    L_0x02bb:
        r0 = move-exception;
        r12 = 0;
        goto L_0x0302;
    L_0x02be:
        r0 = 90;
    L_0x02c0:
        r1 = r5.getAbsolutePath();	 Catch:{ Exception -> 0x02bb }
        r1 = r1.endsWith(r2);	 Catch:{ Exception -> 0x02bb }
        if (r1 == 0) goto L_0x02d5;
    L_0x02ca:
        r1 = r5.getAbsolutePath();	 Catch:{ Exception -> 0x02bb }
        r2 = 1;
        r1 = android.media.ThumbnailUtils.createVideoThumbnail(r1, r2);	 Catch:{ Exception -> 0x02bb }
        r12 = 0;
        goto L_0x02e0;
    L_0x02d5:
        r2 = 1;
        r1 = r5.getAbsolutePath();	 Catch:{ Exception -> 0x02bb }
        r4 = (float) r0;
        r12 = 0;
        r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r12, r4, r4, r2);	 Catch:{ Exception -> 0x0301 }
    L_0x02e0:
        if (r1 == 0) goto L_0x0307;
    L_0x02e2:
        r2 = (float) r0;	 Catch:{ Exception -> 0x0301 }
        r5 = 90;
        if (r0 <= r5) goto L_0x02ea;
    L_0x02e7:
        r0 = 80;
        goto L_0x02ec;
    L_0x02ea:
        r0 = 55;
    L_0x02ec:
        r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r2, r2, r0, r10);	 Catch:{ Exception -> 0x0301 }
        if (r0 == 0) goto L_0x02fd;
    L_0x02f2:
        r2 = r6.thumbs;	 Catch:{ Exception -> 0x0301 }
        r2.add(r0);	 Catch:{ Exception -> 0x0301 }
        r0 = r6.flags;	 Catch:{ Exception -> 0x0301 }
        r2 = 1;
        r0 = r0 | r2;
        r6.flags = r0;	 Catch:{ Exception -> 0x0301 }
    L_0x02fd:
        r1.recycle();	 Catch:{ Exception -> 0x0301 }
        goto L_0x0307;
    L_0x0301:
        r0 = move-exception;
    L_0x0302:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0307;
    L_0x0306:
        r12 = 0;
    L_0x0307:
        r0 = r6.thumbs;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x033b;
    L_0x030f:
        r0 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r0.<init>();
        r1 = r8.searchImage;
        r2 = r1.width;
        r0.w = r2;
        r1 = r1.height;
        r0.h = r1;
        r1 = 0;
        r0.size = r1;
        r2 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r2.<init>();
        r0.location = r2;
        r2 = "x";
        r0.type = r2;
        r2 = r6.thumbs;
        r2.add(r0);
        r0 = r6.flags;
        r20 = 1;
        r0 = r0 | 1;
        r6.flags = r0;
        goto L_0x033e;
    L_0x033b:
        r1 = 0;
        r20 = 1;
    L_0x033e:
        r5 = r6;
        goto L_0x034a;
    L_0x0340:
        r39 = r11;
        r38 = r12;
        r1 = 0;
        r12 = 0;
        r20 = 1;
        r11 = r5;
        r5 = r0;
    L_0x034a:
        r0 = r8.searchImage;
        r0 = r0.imageUrl;
        if (r11 != 0) goto L_0x0351;
    L_0x0350:
        goto L_0x0355;
    L_0x0351:
        r0 = r11.toString();
    L_0x0355:
        r6 = r0;
        r0 = r8.searchImage;
        r0 = r0.imageUrl;
        if (r0 == 0) goto L_0x035f;
    L_0x035c:
        r7.put(r3, r0);
    L_0x035f:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$IgEbMQWK-TLroZ0hW2gzVnOcN5Y;
        r2 = r0;
        r3 = r59;
        r11 = r36;
        r4 = r56;
        r40 = r37;
        r41 = r33;
        r24 = 0;
        r13 = r12;
        r12 = r8;
        r8 = r23;
        r43 = r9;
        r13 = r10;
        r9 = r54;
        r1 = r11;
        r44 = r39;
        r11 = r60;
        r20 = r38;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r11, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        r47 = r1;
        r23 = r13;
        r34 = r14;
        r35 = r15;
        r0 = r22;
        r14 = r28;
        r2 = r31;
        r5 = r40;
        r26 = r41;
        r21 = r43;
        r36 = r44;
        goto L_0x0509;
    L_0x039c:
        r43 = r9;
        r13 = r10;
        r44 = r11;
        r20 = r12;
        r41 = r33;
        r10 = r36;
        r40 = r37;
        r24 = 0;
        r12 = r8;
        r9 = 0;
        r0 = r0.photo;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r2 == 0) goto L_0x03b7;
    L_0x03b3:
        r5 = r0;
        r5 = (org.telegram.tgnet.TLRPC.TL_photo) r5;
        goto L_0x03bc;
    L_0x03b7:
        if (r13 != 0) goto L_0x03bb;
    L_0x03b9:
        r0 = r12.ttl;
    L_0x03bb:
        r5 = 0;
    L_0x03bc:
        if (r5 != 0) goto L_0x0487;
    L_0x03be:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = r12.searchImage;
        r2 = r2.imageUrl;
        r2 = org.telegram.messenger.Utilities.MD5(r2);
        r0.append(r2);
        r0.append(r4);
        r2 = r12.searchImage;
        r2 = r2.imageUrl;
        r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r2, r1);
        r0.append(r2);
        r0 = r0.toString();
        r2 = new java.io.File;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r26);
        r2.<init>(r7, r0);
        r0 = r2.exists();
        if (r0 == 0) goto L_0x0408;
    L_0x03ef:
        r7 = r2.length();
        r0 = (r7 > r24 ? 1 : (r7 == r24 ? 0 : -1));
        if (r0 == 0) goto L_0x0408;
    L_0x03f7:
        r0 = getInstance(r56);
        r2 = r2.toString();
        r5 = 0;
        r0 = r0.generatePhotoSizes(r2, r5);
        if (r0 == 0) goto L_0x0409;
    L_0x0406:
        r2 = 0;
        goto L_0x040a;
    L_0x0408:
        r0 = r5;
    L_0x0409:
        r2 = 1;
    L_0x040a:
        if (r0 != 0) goto L_0x0483;
    L_0x040c:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r7 = r12.searchImage;
        r7 = r7.thumbUrl;
        r7 = org.telegram.messenger.Utilities.MD5(r7);
        r5.append(r7);
        r5.append(r4);
        r4 = r12.searchImage;
        r4 = r4.thumbUrl;
        r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r1);
        r5.append(r1);
        r1 = r5.toString();
        r4 = new java.io.File;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r26);
        r4.<init>(r5, r1);
        r1 = r4.exists();
        if (r1 == 0) goto L_0x044a;
    L_0x043d:
        r0 = getInstance(r56);
        r1 = r4.toString();
        r4 = 0;
        r0 = r0.generatePhotoSizes(r1, r4);
    L_0x044a:
        if (r0 != 0) goto L_0x0483;
    L_0x044c:
        r0 = new org.telegram.tgnet.TLRPC$TL_photo;
        r0.<init>();
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r56);
        r1 = r1.getCurrentTime();
        r0.date = r1;
        r1 = 0;
        r4 = new byte[r1];
        r0.file_reference = r4;
        r4 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r4.<init>();
        r5 = r12.searchImage;
        r7 = r5.width;
        r4.w = r7;
        r5 = r5.height;
        r4.h = r5;
        r4.size = r1;
        r5 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r5.<init>();
        r4.location = r5;
        r5 = "x";
        r4.type = r5;
        r5 = r0.sizes;
        r5.add(r4);
        goto L_0x0484;
    L_0x0483:
        r1 = 0;
    L_0x0484:
        r5 = r0;
        r0 = r2;
        goto L_0x0489;
    L_0x0487:
        r1 = 0;
        r0 = 1;
    L_0x0489:
        if (r5 == 0) goto L_0x04ef;
    L_0x048b:
        r8 = new java.util.HashMap;
        r8.<init>();
        r2 = r12.searchImage;
        r2 = r2.imageUrl;
        if (r2 == 0) goto L_0x0499;
    L_0x0496:
        r8.put(r3, r2);
    L_0x0499:
        if (r58 == 0) goto L_0x04cf;
    L_0x049b:
        r2 = r22 + 1;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r6);
        r6 = r41;
        r3.append(r6);
        r3 = r3.toString();
        r4 = "groupId";
        r8.put(r4, r3);
        r11 = 10;
        if (r2 == r11) goto L_0x04c1;
    L_0x04b7:
        r3 = r20 + -1;
        r11 = r43;
        if (r11 != r3) goto L_0x04be;
    L_0x04bd:
        goto L_0x04c3;
    L_0x04be:
        r22 = r2;
        goto L_0x04d3;
    L_0x04c1:
        r11 = r43;
    L_0x04c3:
        r3 = "final";
        r4 = "1";
        r8.put(r3, r4);
        r22 = r2;
        r31 = r24;
        goto L_0x04d3;
    L_0x04cf:
        r6 = r41;
        r11 = r43;
    L_0x04d3:
        r21 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$OvF-VOkZ5BprZm7FnL7H2r-jxRY;
        r2 = r21;
        r3 = r59;
        r4 = r56;
        r45 = r6;
        r6 = r0;
        r7 = r12;
        r47 = r10;
        r12 = r11;
        r10 = r54;
        r48 = r12;
        r12 = r60;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r21);
        goto L_0x04f5;
    L_0x04ef:
        r47 = r10;
        r45 = r41;
        r48 = r43;
    L_0x04f5:
        r23 = r13;
        r34 = r14;
        r35 = r15;
        r0 = r22;
        r14 = r28;
        r2 = r31;
        r5 = r40;
        r36 = r44;
        r26 = r45;
        r21 = r48;
    L_0x0509:
        r4 = 1;
        r28 = 0;
        r33 = 0;
        goto L_0x0aed;
    L_0x0510:
        r47 = r4;
        r40 = r5;
        r48 = r9;
        r13 = r10;
        r44 = r11;
        r20 = r12;
        r45 = r33;
        r1 = 0;
        r5 = 90;
        r11 = 10;
        r24 = 0;
        r12 = r8;
        r0 = r12.isVideo;
        if (r0 == 0) goto L_0x0804;
    L_0x0529:
        if (r57 == 0) goto L_0x052d;
    L_0x052b:
        r0 = 0;
        goto L_0x0538;
    L_0x052d:
        r0 = r12.videoEditedInfo;
        if (r0 == 0) goto L_0x0532;
    L_0x0531:
        goto L_0x0538;
    L_0x0532:
        r0 = r12.path;
        r0 = createCompressionSettings(r0);
    L_0x0538:
        if (r57 != 0) goto L_0x07cd;
    L_0x053a:
        if (r0 != 0) goto L_0x0544;
    L_0x053c:
        r4 = r12.path;
        r2 = r4.endsWith(r2);
        if (r2 == 0) goto L_0x07cd;
    L_0x0544:
        r9 = r12.path;
        r10 = new java.io.File;
        r10.<init>(r9);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r9);
        r7 = r10.length();
        r2.append(r7);
        r2.append(r15);
        r7 = r10.lastModified();
        r2.append(r7);
        r2 = r2.toString();
        if (r0 == 0) goto L_0x05c1;
    L_0x056a:
        r4 = r0.muted;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r2);
        r8 = r6;
        r5 = r0.estimatedDuration;
        r7.append(r5);
        r7.append(r15);
        r5 = r0.startTime;
        r7.append(r5);
        r7.append(r15);
        r5 = r0.endTime;
        r7.append(r5);
        r2 = r0.muted;
        if (r2 == 0) goto L_0x0591;
    L_0x058e:
        r6 = "_m";
        goto L_0x0592;
    L_0x0591:
        r6 = r8;
    L_0x0592:
        r7.append(r6);
        r2 = r7.toString();
        r5 = r0.resultWidth;
        r6 = r0.originalWidth;
        if (r5 == r6) goto L_0x05b3;
    L_0x059f:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r2);
        r5.append(r15);
        r2 = r0.resultWidth;
        r5.append(r2);
        r2 = r5.toString();
    L_0x05b3:
        r6 = r0.startTime;
        r5 = (r6 > r24 ? 1 : (r6 == r24 ? 0 : -1));
        if (r5 < 0) goto L_0x05ba;
    L_0x05b9:
        goto L_0x05bc;
    L_0x05ba:
        r6 = r24;
    L_0x05bc:
        r21 = r4;
        r5 = r6;
        r7 = r2;
        goto L_0x05c7;
    L_0x05c1:
        r8 = r6;
        r7 = r2;
        r5 = r24;
        r21 = 0;
    L_0x05c7:
        if (r13 != 0) goto L_0x060a;
    L_0x05c9:
        r2 = r12.ttl;
        if (r2 != 0) goto L_0x060a;
    L_0x05cd:
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r56);
        if (r13 != 0) goto L_0x05d5;
    L_0x05d3:
        r4 = 2;
        goto L_0x05d6;
    L_0x05d5:
        r4 = 5;
    L_0x05d6:
        r2 = r2.getSentFile(r7, r4);
        if (r2 == 0) goto L_0x060a;
    L_0x05dc:
        r4 = r2[r1];
        r23 = r4;
        r23 = (org.telegram.tgnet.TLRPC.TL_document) r23;
        r4 = 1;
        r2 = r2[r4];
        r33 = r2;
        r33 = (java.lang.String) r33;
        r2 = r12.path;
        r34 = 0;
        r36 = r2;
        r2 = r56;
        r11 = r3;
        r3 = r13;
        r1 = 1;
        r4 = r23;
        r38 = r5;
        r35 = 90;
        r5 = r36;
        r49 = r8;
        r8 = 10;
        r6 = r34;
        r50 = r7;
        r7 = r38;
        ensureMediaThumbExists(r2, r3, r4, r5, r6, r7);
        goto L_0x0616;
    L_0x060a:
        r11 = r3;
        r38 = r5;
        r50 = r7;
        r49 = r8;
        r1 = 1;
        r23 = 0;
        r33 = 0;
    L_0x0616:
        if (r23 != 0) goto L_0x06f3;
    L_0x0618:
        r2 = r12.path;
        r6 = r38;
        r2 = createVideoThumbnail(r2, r6);
        if (r2 != 0) goto L_0x0628;
    L_0x0622:
        r2 = r12.path;
        r2 = android.media.ThumbnailUtils.createVideoThumbnail(r2, r1);
    L_0x0628:
        if (r2 == 0) goto L_0x0654;
    L_0x062a:
        if (r13 != 0) goto L_0x063e;
    L_0x062c:
        r3 = r12.ttl;
        if (r3 == 0) goto L_0x0631;
    L_0x0630:
        goto L_0x063e;
    L_0x0631:
        r3 = r2.getWidth();
        r4 = r2.getHeight();
        r7 = java.lang.Math.max(r3, r4);
        goto L_0x0640;
    L_0x063e:
        r7 = 90;
    L_0x0640:
        r3 = (float) r7;
        r4 = 90;
        if (r7 <= r4) goto L_0x0648;
    L_0x0645:
        r5 = 80;
        goto L_0x064a;
    L_0x0648:
        r5 = 55;
    L_0x064a:
        r5 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r5, r13);
        r3 = 0;
        r6 = getKeyForPhotoSize(r5, r3, r1);
        goto L_0x0658;
    L_0x0654:
        r4 = 90;
        r5 = 0;
        r6 = 0;
    L_0x0658:
        r3 = new org.telegram.tgnet.TLRPC$TL_document;
        r3.<init>();
        r7 = 0;
        r8 = new byte[r7];
        r3.file_reference = r8;
        if (r5 == 0) goto L_0x066e;
    L_0x0664:
        r7 = r3.thumbs;
        r7.add(r5);
        r5 = r3.flags;
        r5 = r5 | r1;
        r3.flags = r5;
    L_0x066e:
        r5 = "video/mp4";
        r3.mime_type = r5;
        r5 = org.telegram.messenger.UserConfig.getInstance(r56);
        r7 = 0;
        r5.saveConfig(r7);
        if (r13 == 0) goto L_0x068c;
    L_0x067c:
        r5 = 66;
        if (r14 < r5) goto L_0x0686;
    L_0x0680:
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r5.<init>();
        goto L_0x0693;
    L_0x0686:
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r5.<init>();
        goto L_0x0693;
    L_0x068c:
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r5.<init>();
        r5.supports_streaming = r1;
    L_0x0693:
        r7 = r3.attributes;
        r7.add(r5);
        if (r0 == 0) goto L_0x06dc;
    L_0x069a:
        r7 = r0.needConvert();
        if (r7 == 0) goto L_0x06dc;
    L_0x06a0:
        r7 = r0.muted;
        if (r7 == 0) goto L_0x06b2;
    L_0x06a4:
        r7 = r12.path;
        fillVideoAttribute(r7, r5, r0);
        r7 = r5.w;
        r0.originalWidth = r7;
        r7 = r5.h;
        r0.originalHeight = r7;
        goto L_0x06bb;
    L_0x06b2:
        r7 = r0.estimatedDuration;
        r34 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r7 = r7 / r34;
        r8 = (int) r7;
        r5.duration = r8;
    L_0x06bb:
        r7 = r0.rotationValue;
        if (r7 == r4) goto L_0x06cd;
    L_0x06bf:
        r4 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r7 != r4) goto L_0x06c4;
    L_0x06c3:
        goto L_0x06cd;
    L_0x06c4:
        r4 = r0.resultWidth;
        r5.w = r4;
        r4 = r0.resultHeight;
        r5.h = r4;
        goto L_0x06d5;
    L_0x06cd:
        r4 = r0.resultHeight;
        r5.w = r4;
        r4 = r0.resultWidth;
        r5.h = r4;
    L_0x06d5:
        r4 = r0.estimatedSize;
        r5 = (int) r4;
        r3.size = r5;
        r10 = 0;
        goto L_0x06ef;
    L_0x06dc:
        r4 = r10.exists();
        if (r4 == 0) goto L_0x06e9;
    L_0x06e2:
        r7 = r10.length();
        r4 = (int) r7;
        r3.size = r4;
    L_0x06e9:
        r4 = r12.path;
        r10 = 0;
        fillVideoAttribute(r4, r5, r10);
    L_0x06ef:
        r8 = r3;
        r4 = r6;
        r3 = r2;
        goto L_0x06f8;
    L_0x06f3:
        r10 = 0;
        r3 = r10;
        r4 = r3;
        r8 = r23;
    L_0x06f8:
        if (r0 == 0) goto L_0x0723;
    L_0x06fa:
        r2 = r0.muted;
        if (r2 == 0) goto L_0x0723;
    L_0x06fe:
        r2 = r8.attributes;
        r2 = r2.size();
        r5 = 0;
    L_0x0705:
        if (r5 >= r2) goto L_0x0716;
    L_0x0707:
        r6 = r8.attributes;
        r6 = r6.get(r5);
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
        if (r6 == 0) goto L_0x0713;
    L_0x0711:
        r2 = 1;
        goto L_0x0717;
    L_0x0713:
        r5 = r5 + 1;
        goto L_0x0705;
    L_0x0716:
        r2 = 0;
    L_0x0717:
        if (r2 != 0) goto L_0x0723;
    L_0x0719:
        r2 = r8.attributes;
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r5.<init>();
        r2.add(r5);
    L_0x0723:
        if (r0 == 0) goto L_0x0756;
    L_0x0725:
        r2 = r0.needConvert();
        if (r2 == 0) goto L_0x0756;
    L_0x072b:
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
        r6 = org.telegram.messenger.FileLoader.getDirectory(r26);
        r5.<init>(r6, r2);
        org.telegram.messenger.SharedConfig.saveConfig();
        r2 = r5.getAbsolutePath();
        r9 = r2;
    L_0x0756:
        r7 = new java.util.HashMap;
        r7.<init>();
        r2 = r50;
        if (r2 == 0) goto L_0x0762;
    L_0x075f:
        r7.put(r11, r2);
    L_0x0762:
        if (r21 != 0) goto L_0x079e;
    L_0x0764:
        if (r58 == 0) goto L_0x079e;
    L_0x0766:
        r2 = r22 + 1;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = r49;
        r5.append(r6);
        r11 = r2;
        r1 = r45;
        r5.append(r1);
        r5 = r5.toString();
        r6 = "groupId";
        r7.put(r6, r5);
        r5 = r11;
        r6 = 10;
        if (r5 == r6) goto L_0x0790;
    L_0x0786:
        r6 = r20 + -1;
        r11 = r48;
        if (r11 != r6) goto L_0x078d;
    L_0x078c:
        goto L_0x0792;
    L_0x078d:
        r22 = r5;
        goto L_0x07a2;
    L_0x0790:
        r11 = r48;
    L_0x0792:
        r6 = "final";
        r10 = "1";
        r7.put(r6, r10);
        r22 = r5;
        r31 = r24;
        goto L_0x07a2;
    L_0x079e:
        r1 = r45;
        r11 = r48;
    L_0x07a2:
        r21 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$-1b0gcqRnvhRQ_W1CknWoWwRI6Q;
        r5 = r1;
        r2 = r21;
        r51 = r5;
        r5 = r59;
        r6 = r56;
        r1 = r7;
        r7 = r0;
        r23 = 0;
        r10 = r1;
        r1 = r11;
        r11 = r33;
        r27 = r12;
        r33 = r23;
        r23 = r13;
        r12 = r54;
        r34 = r14;
        r14 = r60;
        r43 = r1;
        r1 = r15;
        r15 = r27;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r21);
        goto L_0x07ef;
    L_0x07cd:
        r23 = r13;
        r34 = r14;
        r1 = r15;
        r51 = r45;
        r43 = r48;
        r33 = 0;
        r13 = r12;
        r4 = r13.path;
        r5 = 0;
        r6 = 0;
        r10 = r13.caption;
        r11 = r13.entities;
        r2 = r56;
        r3 = r4;
        r7 = r54;
        r9 = r60;
        r12 = r59;
        r13 = r57;
        prepareSendingDocumentInternal(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13);
    L_0x07ef:
        r35 = r1;
        r0 = r22;
        r14 = r28;
        r2 = r31;
        r5 = r40;
    L_0x07f9:
        r21 = r43;
        r36 = r44;
        r26 = r51;
        r4 = 1;
        r28 = 0;
        goto L_0x0aed;
    L_0x0804:
        r11 = r3;
        r23 = r13;
        r34 = r14;
        r1 = r15;
        r51 = r45;
        r43 = r48;
        r9 = 10;
        r33 = 0;
        r13 = r12;
        r0 = r13.path;
        if (r0 != 0) goto L_0x0826;
    L_0x0817:
        r2 = r13.uri;
        if (r2 == 0) goto L_0x0826;
    L_0x081b:
        r0 = org.telegram.messenger.AndroidUtilities.getPath(r2);
        r2 = r13.uri;
        r2 = r2.toString();
        goto L_0x0827;
    L_0x0826:
        r2 = r0;
    L_0x0827:
        if (r57 != 0) goto L_0x088f;
    L_0x0829:
        r3 = r13.path;
        r4 = r13.uri;
        r3 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r3, r4);
        if (r3 == 0) goto L_0x0834;
    L_0x0833:
        goto L_0x088f;
    L_0x0834:
        if (r0 == 0) goto L_0x0854;
    L_0x0836:
        r14 = r28;
        r3 = r0.endsWith(r14);
        if (r3 != 0) goto L_0x0846;
    L_0x083e:
        r3 = ".webp";
        r3 = r0.endsWith(r3);
        if (r3 == 0) goto L_0x0856;
    L_0x0846:
        r3 = r0.endsWith(r14);
        if (r3 == 0) goto L_0x084f;
    L_0x084c:
        r3 = "gif";
        goto L_0x0851;
    L_0x084f:
        r3 = "webp";
    L_0x0851:
        r19 = r3;
        goto L_0x089f;
    L_0x0854:
        r14 = r28;
    L_0x0856:
        if (r0 != 0) goto L_0x088c;
    L_0x0858:
        r3 = r13.uri;
        if (r3 == 0) goto L_0x088c;
    L_0x085c:
        r3 = org.telegram.messenger.MediaController.isGif(r3);
        if (r3 == 0) goto L_0x0873;
    L_0x0862:
        r0 = r13.uri;
        r2 = r0.toString();
        r0 = r13.uri;
        r3 = "gif";
        r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3);
        r19 = "gif";
        goto L_0x089f;
    L_0x0873:
        r3 = r13.uri;
        r3 = org.telegram.messenger.MediaController.isWebp(r3);
        if (r3 == 0) goto L_0x088c;
    L_0x087b:
        r0 = r13.uri;
        r2 = r0.toString();
        r0 = r13.uri;
        r3 = "webp";
        r0 = org.telegram.messenger.MediaController.copyFileToCache(r0, r3);
        r19 = "webp";
        goto L_0x089f;
    L_0x088c:
        r10 = r0;
        r0 = 0;
        goto L_0x08a1;
    L_0x088f:
        r14 = r28;
        if (r0 == 0) goto L_0x089d;
    L_0x0893:
        r3 = new java.io.File;
        r3.<init>(r0);
        r3 = org.telegram.messenger.FileLoader.getFileExtension(r3);
        goto L_0x0851;
    L_0x089d:
        r19 = r6;
    L_0x089f:
        r10 = r0;
        r0 = 1;
    L_0x08a1:
        if (r0 == 0) goto L_0x08e6;
    L_0x08a3:
        r15 = r40;
        if (r15 != 0) goto L_0x08c1;
    L_0x08a7:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r29 = new java.util.ArrayList;
        r29.<init>();
        r30 = new java.util.ArrayList;
        r30.<init>();
        r15 = r5;
        r0 = r29;
        r3 = r30;
        goto L_0x08c7;
    L_0x08c1:
        r0 = r29;
        r3 = r30;
        r4 = r47;
    L_0x08c7:
        r15.add(r10);
        r4.add(r2);
        r2 = r13.caption;
        r0.add(r2);
        r2 = r13.entities;
        r3.add(r2);
        r29 = r0;
        r35 = r1;
        r30 = r3;
        r47 = r4;
        r5 = r15;
        r0 = r22;
        r2 = r31;
        goto L_0x07f9;
    L_0x08e6:
        r15 = r40;
        if (r10 == 0) goto L_0x090e;
    L_0x08ea:
        r0 = new java.io.File;
        r0.<init>(r10);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r4 = r0.length();
        r3.append(r4);
        r3.append(r1);
        r4 = r0.lastModified();
        r3.append(r4);
        r5 = r3.toString();
        r7 = r5;
        goto L_0x0910;
    L_0x090e:
        r7 = r33;
    L_0x0910:
        r12 = r44;
        if (r12 == 0) goto L_0x0937;
    L_0x0914:
        r0 = r12.get(r13);
        r2 = r0;
        r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2;
        r0 = r2.photo;
        r3 = r2.parentObject;
        if (r0 != 0) goto L_0x092f;
    L_0x0921:
        r0 = r2.sync;	 Catch:{ Exception -> 0x0927 }
        r0.await();	 Catch:{ Exception -> 0x0927 }
        goto L_0x092b;
    L_0x0927:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x092b:
        r0 = r2.photo;
        r3 = r2.parentObject;
    L_0x092f:
        r35 = r1;
        r21 = r3;
        r9 = r6;
        r1 = r7;
        goto L_0x09c2;
    L_0x0937:
        if (r23 != 0) goto L_0x099c;
    L_0x0939:
        r0 = r13.ttl;
        if (r0 != 0) goto L_0x099c;
    L_0x093d:
        r0 = org.telegram.messenger.MessagesStorage.getInstance(r56);
        if (r23 != 0) goto L_0x0945;
    L_0x0943:
        r2 = 0;
        goto L_0x0946;
    L_0x0945:
        r2 = 3;
    L_0x0946:
        r0 = r0.getSentFile(r7, r2);
        if (r0 == 0) goto L_0x0958;
    L_0x094c:
        r2 = 0;
        r3 = r0[r2];
        r5 = r3;
        r5 = (org.telegram.tgnet.TLRPC.TL_photo) r5;
        r2 = 1;
        r0 = r0[r2];
        r0 = (java.lang.String) r0;
        goto L_0x095b;
    L_0x0958:
        r0 = r33;
        r5 = r0;
    L_0x095b:
        if (r5 != 0) goto L_0x0983;
    L_0x095d:
        r2 = r13.uri;
        if (r2 == 0) goto L_0x0983;
    L_0x0961:
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r56);
        r3 = r13.uri;
        r3 = org.telegram.messenger.AndroidUtilities.getPath(r3);
        if (r23 != 0) goto L_0x096f;
    L_0x096d:
        r4 = 0;
        goto L_0x0970;
    L_0x096f:
        r4 = 3;
    L_0x0970:
        r2 = r2.getSentFile(r3, r4);
        if (r2 == 0) goto L_0x0983;
    L_0x0976:
        r3 = 0;
        r0 = r2[r3];
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;
        r3 = 1;
        r2 = r2[r3];
        r2 = (java.lang.String) r2;
        r21 = r2;
        goto L_0x0986;
    L_0x0983:
        r21 = r0;
        r0 = r5;
    L_0x0986:
        r5 = r13.path;
        r8 = r13.uri;
        r27 = 0;
        r2 = r56;
        r3 = r23;
        r4 = r0;
        r9 = r6;
        r6 = r8;
        r35 = r1;
        r1 = r7;
        r7 = r27;
        ensureMediaThumbExists(r2, r3, r4, r5, r6, r7);
        goto L_0x09a4;
    L_0x099c:
        r35 = r1;
        r9 = r6;
        r1 = r7;
        r0 = r33;
        r21 = r0;
    L_0x09a4:
        if (r0 != 0) goto L_0x09c2;
    L_0x09a6:
        r0 = getInstance(r56);
        r2 = r13.path;
        r3 = r13.uri;
        r0 = r0.generatePhotoSizes(r2, r3);
        if (r23 == 0) goto L_0x09c2;
    L_0x09b4:
        r2 = r13.canDeleteAfter;
        if (r2 == 0) goto L_0x09c2;
    L_0x09b8:
        r2 = new java.io.File;
        r3 = r13.path;
        r2.<init>(r3);
        r2.delete();
    L_0x09c2:
        r7 = r0;
        if (r7 == 0) goto L_0x0aa7;
    L_0x09c5:
        r8 = new java.util.HashMap;
        r8.<init>();
        r2 = 1;
        r3 = new android.graphics.Bitmap[r2];
        r4 = new java.lang.String[r2];
        r0 = r13.masks;
        if (r0 == 0) goto L_0x09db;
    L_0x09d3:
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x09db;
    L_0x09d9:
        r0 = 1;
        goto L_0x09dc;
    L_0x09db:
        r0 = 0;
    L_0x09dc:
        r7.has_stickers = r0;
        if (r0 == 0) goto L_0x0a1f;
    L_0x09e0:
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
    L_0x09f9:
        r5 = r13.masks;
        r5 = r5.size();
        if (r2 >= r5) goto L_0x0a0f;
    L_0x0a01:
        r5 = r13.masks;
        r5 = r5.get(r2);
        r5 = (org.telegram.tgnet.TLRPC.InputDocument) r5;
        r5.serializeToStream(r0);
        r2 = r2 + 1;
        goto L_0x09f9;
    L_0x0a0f:
        r2 = r0.toByteArray();
        r2 = org.telegram.messenger.Utilities.bytesToHex(r2);
        r5 = "masks";
        r8.put(r5, r2);
        r0.cleanup();
    L_0x0a1f:
        if (r1 == 0) goto L_0x0a24;
    L_0x0a21:
        r8.put(r11, r1);
    L_0x0a24:
        if (r58 == 0) goto L_0x0a33;
    L_0x0a26:
        r0 = r53.size();	 Catch:{ Exception -> 0x0a30 }
        r1 = 1;
        if (r0 != r1) goto L_0x0a2e;
    L_0x0a2d:
        goto L_0x0a34;
    L_0x0a2e:
        r10 = 0;
        goto L_0x0a4f;
    L_0x0a30:
        r0 = move-exception;
        r1 = 1;
        goto L_0x0a4b;
    L_0x0a33:
        r1 = 1;
    L_0x0a34:
        r0 = r7.sizes;	 Catch:{ Exception -> 0x0a4a }
        r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0a4a }
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2);	 Catch:{ Exception -> 0x0a4a }
        if (r0 == 0) goto L_0x0a2e;
    L_0x0a40:
        r10 = 0;
        r0 = getKeyForPhotoSize(r0, r3, r10);	 Catch:{ Exception -> 0x0a48 }
        r4[r10] = r0;	 Catch:{ Exception -> 0x0a48 }
        goto L_0x0a4f;
    L_0x0a48:
        r0 = move-exception;
        goto L_0x0a4c;
    L_0x0a4a:
        r0 = move-exception;
    L_0x0a4b:
        r10 = 0;
    L_0x0a4c:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0a4f:
        if (r58 == 0) goto L_0x0a80;
    L_0x0a51:
        r0 = r22 + 1;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r9);
        r5 = r51;
        r2.append(r5);
        r2 = r2.toString();
        r9 = "groupId";
        r8.put(r9, r2);
        r2 = 10;
        if (r0 == r2) goto L_0x0a74;
    L_0x0a6d:
        r2 = r20 + -1;
        r11 = r43;
        if (r11 != r2) goto L_0x0a86;
    L_0x0a73:
        goto L_0x0a76;
    L_0x0a74:
        r11 = r43;
    L_0x0a76:
        r2 = "final";
        r9 = "1";
        r8.put(r2, r9);
        r31 = r24;
        goto L_0x0a86;
    L_0x0a80:
        r11 = r43;
        r5 = r51;
        r0 = r22;
    L_0x0a86:
        r22 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$y8ZvqnjS4LBoWsFR5SKpceGPC-c;
        r2 = r22;
        r26 = r5;
        r5 = r59;
        r6 = r56;
        r9 = r21;
        r21 = r11;
        r28 = 0;
        r10 = r54;
        r36 = r12;
        r12 = r60;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r22);
        r5 = r15;
        r2 = r31;
        r4 = 1;
        goto L_0x0aed;
    L_0x0aa7:
        r36 = r12;
        r21 = r43;
        r26 = r51;
        r4 = 1;
        r28 = 0;
        if (r15 != 0) goto L_0x0acc;
    L_0x0ab2:
        r5 = new java.util.ArrayList;
        r5.<init>();
        r0 = new java.util.ArrayList;
        r0.<init>();
        r29 = new java.util.ArrayList;
        r29.<init>();
        r30 = new java.util.ArrayList;
        r30.<init>();
        r15 = r5;
        r2 = r29;
        r3 = r30;
        goto L_0x0ad2;
    L_0x0acc:
        r2 = r29;
        r3 = r30;
        r0 = r47;
    L_0x0ad2:
        r15.add(r10);
        r0.add(r1);
        r1 = r13.caption;
        r2.add(r1);
        r1 = r13.entities;
        r3.add(r1);
        r47 = r0;
        r29 = r2;
        r30 = r3;
        r5 = r15;
        r0 = r22;
        r2 = r31;
    L_0x0aed:
        r9 = r21 + 1;
        r1 = r53;
        r13 = r56;
        r28 = r14;
        r12 = r20;
        r10 = r23;
        r22 = r26;
        r8 = r33;
        r14 = r34;
        r15 = r35;
        r11 = r36;
        r4 = r47;
        goto L_0x018b;
    L_0x0b07:
        r47 = r4;
        r15 = r5;
        r24 = 0;
        r28 = 0;
        r0 = (r2 > r24 ? 1 : (r2 == r24 ? 0 : -1));
        if (r0 == 0) goto L_0x0b1d;
    L_0x0b12:
        r0 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$Pt_JmP03Oq1fmlBa3qgnN3eURQI;
        r13 = r56;
        r0.<init>(r13, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        goto L_0x0b1f;
    L_0x0b1d:
        r13 = r56;
    L_0x0b1f:
        if (r61 == 0) goto L_0x0b24;
    L_0x0b21:
        r61.releasePermission();
    L_0x0b24:
        if (r15 == 0) goto L_0x0b68;
    L_0x0b26:
        r0 = r15.isEmpty();
        if (r0 != 0) goto L_0x0b68;
    L_0x0b2c:
        r0 = 0;
    L_0x0b2d:
        r1 = r15.size();
        if (r0 >= r1) goto L_0x0b68;
    L_0x0b33:
        r1 = r15.get(r0);
        r2 = r1;
        r2 = (java.lang.String) r2;
        r14 = r47;
        r1 = r14.get(r0);
        r3 = r1;
        r3 = (java.lang.String) r3;
        r4 = 0;
        r12 = r29;
        r1 = r12.get(r0);
        r9 = r1;
        r9 = (java.lang.CharSequence) r9;
        r11 = r30;
        r1 = r11.get(r0);
        r10 = r1;
        r10 = (java.util.ArrayList) r10;
        r1 = r56;
        r5 = r19;
        r6 = r54;
        r8 = r60;
        r11 = r59;
        r12 = r57;
        prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12);
        r0 = r0 + 1;
        goto L_0x0b2d;
    L_0x0b68:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0b86;
    L_0x0b6c:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "total send time = ";
        r0.append(r1);
        r1 = java.lang.System.currentTimeMillis();
        r1 = r1 - r16;
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0b86:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$60(java.util.ArrayList, long, int, boolean, boolean, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    static /* synthetic */ void lambda$null$54(MediaSendPrepareWorker mediaSendPrepareWorker, int i, SendingMediaInfo sendingMediaInfo, boolean z) {
        mediaSendPrepareWorker.photo = getInstance(i).generatePhotoSizes(sendingMediaInfo.path, sendingMediaInfo.uri);
        if (z && sendingMediaInfo.canDeleteAfter) {
            new File(sendingMediaInfo.path).delete();
        }
        mediaSendPrepareWorker.sync.countDown();
    }

    static /* synthetic */ void lambda$null$55(MessageObject messageObject, int i, TL_document tL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (messageObject != null) {
            getInstance(i).editMessageMedia(messageObject, null, null, tL_document, str, hashMap, false, str2);
            return;
        }
        getInstance(i).sendMessage(tL_document, null, str, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, 0, str2);
    }

    static /* synthetic */ void lambda$null$56(MessageObject messageObject, int i, TL_photo tL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        String str2 = null;
        if (messageObject != null) {
            SendMessagesHelper instance = getInstance(i);
            if (z) {
                str2 = sendingMediaInfo2.searchImage.imageUrl;
            }
            instance.editMessageMedia(messageObject, tL_photo, null, null, str2, hashMap, false, str);
            return;
        }
        SendMessagesHelper instance2 = getInstance(i);
        if (z) {
            str2 = sendingMediaInfo2.searchImage.imageUrl;
        }
        instance2.sendMessage(tL_photo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$57(Bitmap bitmap, String str, MessageObject messageObject, int i, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        Bitmap bitmap2 = bitmap;
        String str4 = str;
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmap2 == null || str4 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str4);
        }
        if (messageObject != null) {
            getInstance(i).editMessageMedia(messageObject, null, videoEditedInfo, tL_document, str2, hashMap, false, str3);
            return;
        }
        getInstance(i).sendMessage(tL_document, videoEditedInfo, str2, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, sendingMediaInfo2.ttl, str3);
    }

    static /* synthetic */ void lambda$null$58(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, int i, TL_photo tL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, SendingMediaInfo sendingMediaInfo) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        if (messageObject != null) {
            getInstance(i).editMessageMedia(messageObject, tL_photo, null, null, null, hashMap, false, str);
            return;
        }
        getInstance(i).sendMessage(tL_photo, null, j, messageObject2, sendingMediaInfo2.caption, sendingMediaInfo2.entities, null, hashMap, sendingMediaInfo2.ttl, str);
    }

    static /* synthetic */ void lambda$null$59(int i, long j) {
        SendMessagesHelper instance = getInstance(i);
        HashMap hashMap = instance.delayedMessages;
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
            MessagesStorage.getInstance(i).putMessages(tL_messages_messages, delayedMessage.peer, -2, 0, false);
            instance.sendReadyToSendGroup(delayedMessage, true, true);
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

    public static void prepareSendingVideo(String str, long j, long j2, int i, int i2, VideoEditedInfo videoEditedInfo, long j3, MessageObject messageObject, CharSequence charSequence, ArrayList<MessageEntity> arrayList, int i3, MessageObject messageObject2) {
        if (str != null && str.length() != 0) {
            -$$Lambda$SendMessagesHelper$-u_NUp1HNltMrAG4T3CMDFsJPAs -__lambda_sendmessageshelper_-u_nup1hnltmrag4t3cmdfsjpas = r0;
            -$$Lambda$SendMessagesHelper$-u_NUp1HNltMrAG4T3CMDFsJPAs -__lambda_sendmessageshelper_-u_nup1hnltmrag4t3cmdfsjpas2 = new -$$Lambda$SendMessagesHelper$-u_NUp1HNltMrAG4T3CMDFsJPAs(videoEditedInfo, str, j3, j2, i3, UserConfig.selectedAccount, i2, i, j, charSequence, messageObject2, messageObject, arrayList);
            new Thread(-__lambda_sendmessageshelper_-u_nup1hnltmrag4t3cmdfsjpas).start();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:77:0x0212  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x0259  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x02b9  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02c8  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x030e  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0308  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0314  */
    static /* synthetic */ void lambda$prepareSendingVideo$62(org.telegram.messenger.VideoEditedInfo r26, java.lang.String r27, long r28, long r30, int r32, int r33, int r34, int r35, long r36, java.lang.CharSequence r38, org.telegram.messenger.MessageObject r39, org.telegram.messenger.MessageObject r40, java.util.ArrayList r41) {
        /*
        r7 = r27;
        r10 = r28;
        r8 = r30;
        r12 = r34;
        r13 = r35;
        if (r26 == 0) goto L_0x000f;
    L_0x000c:
        r14 = r26;
        goto L_0x0014;
    L_0x000f:
        r0 = createCompressionSettings(r27);
        r14 = r0;
    L_0x0014:
        r0 = (int) r10;
        r15 = 0;
        if (r0 != 0) goto L_0x001a;
    L_0x0018:
        r6 = 1;
        goto L_0x001b;
    L_0x001a:
        r6 = 0;
    L_0x001b:
        if (r14 == 0) goto L_0x0023;
    L_0x001d:
        r0 = r14.roundVideo;
        if (r0 == 0) goto L_0x0023;
    L_0x0021:
        r4 = 1;
        goto L_0x0024;
    L_0x0023:
        r4 = 0;
    L_0x0024:
        if (r14 != 0) goto L_0x004a;
    L_0x0026:
        r0 = "mp4";
        r0 = r7.endsWith(r0);
        if (r0 != 0) goto L_0x004a;
    L_0x002e:
        if (r4 == 0) goto L_0x0031;
    L_0x0030:
        goto L_0x004a;
    L_0x0031:
        r3 = 0;
        r4 = 0;
        r12 = 0;
        r0 = r33;
        r1 = r27;
        r2 = r27;
        r5 = r28;
        r7 = r40;
        r8 = r38;
        r9 = r41;
        r10 = r39;
        r11 = r12;
        prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11);
        goto L_0x0332;
    L_0x004a:
        r3 = new java.io.File;
        r3.<init>(r7);
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r7);
        r1 = r3.length();
        r0.append(r1);
        r2 = "_";
        r0.append(r2);
        r16 = r6;
        r5 = r3.lastModified();
        r0.append(r5);
        r0 = r0.toString();
        r17 = "";
        if (r14 == 0) goto L_0x00c6;
    L_0x0074:
        if (r4 != 0) goto L_0x00bb;
    L_0x0076:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r1.append(r8);
        r1.append(r2);
        r5 = r14.startTime;
        r1.append(r5);
        r1.append(r2);
        r5 = r14.endTime;
        r1.append(r5);
        r0 = r14.muted;
        if (r0 == 0) goto L_0x0098;
    L_0x0095:
        r0 = "_m";
        goto L_0x009a;
    L_0x0098:
        r0 = r17;
    L_0x009a:
        r1.append(r0);
        r0 = r1.toString();
        r1 = r14.resultWidth;
        r5 = r14.originalWidth;
        if (r1 == r5) goto L_0x00bb;
    L_0x00a7:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r1.append(r2);
        r0 = r14.resultWidth;
        r1.append(r0);
        r0 = r1.toString();
    L_0x00bb:
        r5 = r14.startTime;
        r18 = 0;
        r1 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1));
        if (r1 < 0) goto L_0x00c8;
    L_0x00c3:
        r18 = r5;
        goto L_0x00c8;
    L_0x00c6:
        r18 = 0;
    L_0x00c8:
        r5 = r0;
        r0 = r18;
        r18 = r4;
        if (r16 != 0) goto L_0x010b;
    L_0x00cf:
        if (r32 != 0) goto L_0x010b;
    L_0x00d1:
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r33);
        if (r16 != 0) goto L_0x00d9;
    L_0x00d7:
        r6 = 2;
        goto L_0x00dc;
    L_0x00d9:
        r20 = 5;
        r6 = 5;
    L_0x00dc:
        r4 = r4.getSentFile(r5, r6);
        if (r4 == 0) goto L_0x010b;
    L_0x00e2:
        r6 = r4[r15];
        r20 = r6;
        r20 = (org.telegram.tgnet.TLRPC.TL_document) r20;
        r6 = 1;
        r4 = r4[r6];
        r21 = r4;
        r21 = (java.lang.String) r21;
        r4 = 0;
        r22 = r0;
        r0 = r33;
        r1 = r16;
        r15 = r2;
        r2 = r20;
        r24 = r3;
        r3 = r27;
        r12 = r18;
        r25 = r5;
        r13 = r16;
        r8 = 2;
        r9 = 1;
        r5 = r22;
        ensureMediaThumbExists(r0, r1, r2, r3, r4, r5);
        goto L_0x011c;
    L_0x010b:
        r22 = r0;
        r15 = r2;
        r24 = r3;
        r25 = r5;
        r13 = r16;
        r12 = r18;
        r8 = 2;
        r9 = 1;
        r20 = 0;
        r21 = 0;
    L_0x011c:
        if (r20 != 0) goto L_0x02c8;
    L_0x011e:
        r0 = r22;
        r0 = createVideoThumbnail(r7, r0);
        if (r0 != 0) goto L_0x012a;
    L_0x0126:
        r0 = android.media.ThumbnailUtils.createVideoThumbnail(r7, r9);
    L_0x012a:
        r6 = 90;
        if (r13 != 0) goto L_0x0134;
    L_0x012e:
        if (r32 == 0) goto L_0x0131;
    L_0x0130:
        goto L_0x0134;
    L_0x0131:
        r1 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x0136;
    L_0x0134:
        r1 = 90;
    L_0x0136:
        r2 = (float) r1;
        if (r1 <= r6) goto L_0x013c;
    L_0x0139:
        r1 = 80;
        goto L_0x013e;
    L_0x013c:
        r1 = 55;
    L_0x013e:
        r5 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r1, r13);
        if (r0 == 0) goto L_0x0209;
    L_0x0144:
        if (r5 == 0) goto L_0x0209;
    L_0x0146:
        if (r12 == 0) goto L_0x0206;
    L_0x0148:
        r1 = 21;
        if (r13 == 0) goto L_0x01ab;
    L_0x014c:
        r2 = 7;
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 >= r1) goto L_0x0153;
    L_0x0151:
        r3 = 0;
        goto L_0x0154;
    L_0x0153:
        r3 = 1;
    L_0x0154:
        r4 = r0.getWidth();
        r16 = r0.getHeight();
        r18 = r0.getRowBytes();
        r1 = r0;
        r9 = r5;
        r5 = r16;
        r6 = r18;
        org.telegram.messenger.Utilities.blurBitmap(r1, r2, r3, r4, r5, r6);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = r9.location;
        r2 = r2.volume_id;
        r1.append(r2);
        r1.append(r15);
        r2 = r9.location;
        r2 = r2.local_id;
        r1.append(r2);
        r2 = "@%d_%d_b2";
        r1.append(r2);
        r1 = r1.toString();
        r2 = new java.lang.Object[r8];
        r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.density;
        r3 = r3 / r4;
        r3 = (int) r3;
        r3 = java.lang.Integer.valueOf(r3);
        r4 = 0;
        r2[r4] = r3;
        r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.density;
        r3 = r3 / r4;
        r3 = (int) r3;
        r3 = java.lang.Integer.valueOf(r3);
        r4 = 1;
        r2[r4] = r3;
        r4 = java.lang.String.format(r1, r2);
        goto L_0x020b;
    L_0x01ab:
        r9 = r5;
        r2 = 3;
        r3 = android.os.Build.VERSION.SDK_INT;
        if (r3 >= r1) goto L_0x01b3;
    L_0x01b1:
        r3 = 0;
        goto L_0x01b4;
    L_0x01b3:
        r3 = 1;
    L_0x01b4:
        r4 = r0.getWidth();
        r5 = r0.getHeight();
        r6 = r0.getRowBytes();
        r1 = r0;
        org.telegram.messenger.Utilities.blurBitmap(r1, r2, r3, r4, r5, r6);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = r9.location;
        r2 = r2.volume_id;
        r1.append(r2);
        r1.append(r15);
        r2 = r9.location;
        r2 = r2.local_id;
        r1.append(r2);
        r2 = "@%d_%d_b";
        r1.append(r2);
        r1 = r1.toString();
        r2 = new java.lang.Object[r8];
        r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.density;
        r3 = r3 / r4;
        r3 = (int) r3;
        r3 = java.lang.Integer.valueOf(r3);
        r4 = 0;
        r2[r4] = r3;
        r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.density;
        r3 = r3 / r4;
        r3 = (int) r3;
        r3 = java.lang.Integer.valueOf(r3);
        r4 = 1;
        r2[r4] = r3;
        r4 = java.lang.String.format(r1, r2);
        goto L_0x020b;
    L_0x0206:
        r9 = r5;
        r0 = 0;
        goto L_0x020a;
    L_0x0209:
        r9 = r5;
    L_0x020a:
        r4 = 0;
    L_0x020b:
        r1 = new org.telegram.tgnet.TLRPC$TL_document;
        r1.<init>();
        if (r9 == 0) goto L_0x021d;
    L_0x0212:
        r2 = r1.thumbs;
        r2.add(r9);
        r2 = r1.flags;
        r3 = 1;
        r2 = r2 | r3;
        r1.flags = r2;
    L_0x021d:
        r2 = 0;
        r3 = new byte[r2];
        r1.file_reference = r3;
        r3 = "video/mp4";
        r1.mime_type = r3;
        r3 = org.telegram.messenger.UserConfig.getInstance(r33);
        r3.saveConfig(r2);
        if (r13 == 0) goto L_0x0259;
    L_0x022f:
        r2 = 32;
        r2 = r10 >> r2;
        r3 = (int) r2;
        r2 = org.telegram.messenger.MessagesController.getInstance(r33);
        r3 = java.lang.Integer.valueOf(r3);
        r2 = r2.getEncryptedChat(r3);
        if (r2 != 0) goto L_0x0243;
    L_0x0242:
        return;
    L_0x0243:
        r2 = r2.layer;
        r2 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r2);
        r3 = 66;
        if (r2 < r3) goto L_0x0253;
    L_0x024d:
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r2.<init>();
        goto L_0x0261;
    L_0x0253:
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r2.<init>();
        goto L_0x0261;
    L_0x0259:
        r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r2.<init>();
        r3 = 1;
        r2.supports_streaming = r3;
    L_0x0261:
        r2.round_message = r12;
        r3 = r1.attributes;
        r3.add(r2);
        if (r14 == 0) goto L_0x02b3;
    L_0x026a:
        r3 = r14.needConvert();
        if (r3 == 0) goto L_0x02b3;
    L_0x0270:
        r3 = r14.muted;
        if (r3 == 0) goto L_0x028a;
    L_0x0274:
        r3 = r1.attributes;
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r5.<init>();
        r3.add(r5);
        fillVideoAttribute(r7, r2, r14);
        r3 = r2.w;
        r14.originalWidth = r3;
        r3 = r2.h;
        r14.originalHeight = r3;
        goto L_0x0291;
    L_0x028a:
        r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r30 / r5;
        r3 = (int) r5;
        r2.duration = r3;
    L_0x0291:
        r3 = r14.rotationValue;
        r5 = 90;
        if (r3 == r5) goto L_0x02a5;
    L_0x0297:
        r5 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r3 != r5) goto L_0x029c;
    L_0x029b:
        goto L_0x02a5;
    L_0x029c:
        r3 = r35;
        r2.w = r3;
        r5 = r34;
        r2.h = r5;
        goto L_0x02ad;
    L_0x02a5:
        r5 = r34;
        r3 = r35;
        r2.w = r5;
        r2.h = r3;
    L_0x02ad:
        r2 = r36;
        r3 = (int) r2;
        r1.size = r3;
        goto L_0x02c4;
    L_0x02b3:
        r3 = r24.exists();
        if (r3 == 0) goto L_0x02c0;
    L_0x02b9:
        r5 = r24.length();
        r3 = (int) r5;
        r1.size = r3;
    L_0x02c0:
        r3 = 0;
        fillVideoAttribute(r7, r2, r3);
    L_0x02c4:
        r6 = r1;
        r2 = r4;
        r1 = r0;
        goto L_0x02cd;
    L_0x02c8:
        r3 = 0;
        r1 = r3;
        r2 = r1;
        r6 = r20;
    L_0x02cd:
        if (r14 == 0) goto L_0x0301;
    L_0x02cf:
        r0 = r14.needConvert();
        if (r0 == 0) goto L_0x0301;
    L_0x02d5:
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
    L_0x0301:
        r8 = new java.util.HashMap;
        r8.<init>();
        if (r38 == 0) goto L_0x030e;
    L_0x0308:
        r0 = r38.toString();
        r13 = r0;
        goto L_0x0310;
    L_0x030e:
        r13 = r17;
    L_0x0310:
        r0 = r25;
        if (r0 == 0) goto L_0x0319;
    L_0x0314:
        r3 = "originalPath";
        r8.put(r3, r0);
    L_0x0319:
        r16 = new org.telegram.messenger.-$$Lambda$SendMessagesHelper$0jwFjd59Y5g2sOGoTG8-DHTqhII;
        r0 = r16;
        r3 = r39;
        r4 = r33;
        r5 = r14;
        r9 = r21;
        r10 = r28;
        r12 = r40;
        r14 = r41;
        r15 = r32;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r16);
    L_0x0332:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$62(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, long, int, int, int, int, long, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList):void");
    }

    static /* synthetic */ void lambda$null$61(Bitmap bitmap, String str, MessageObject messageObject, int i, VideoEditedInfo videoEditedInfo, TL_document tL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, String str4, ArrayList arrayList, int i2) {
        Bitmap bitmap2 = bitmap;
        String str5 = str;
        if (!(bitmap2 == null || str5 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str5);
        }
        if (messageObject != null) {
            getInstance(i).editMessageMedia(messageObject, null, videoEditedInfo, tL_document, str2, hashMap, false, str3);
        } else {
            getInstance(i).sendMessage(tL_document, videoEditedInfo, str2, j, messageObject2, str4, arrayList, null, hashMap, i2, str3);
        }
    }
}
