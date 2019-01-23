package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaCodecInfo;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.Toast;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_botInlineMediaResult;
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
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo_layer65;
import org.telegram.tgnet.TLRPC.TL_document_layer82;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFile;
import org.telegram.tgnet.TLRPC.TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputMediaGifExternal;
import org.telegram.tgnet.TLRPC.TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhoto;
import org.telegram.tgnet.TLRPC.TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
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
import org.telegram.tgnet.TLRPC.TL_payments_paymentForm;
import org.telegram.tgnet.TLRPC.TL_payments_paymentReceipt;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
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
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.PaymentFormActivity;

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
            HashMap<String, MessageObject> waitingForLocationCopy = new HashMap(SendMessagesHelper.this.waitingForLocation);
            NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, waitingForLocationCopy);
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
        public FileLocation location;
        public ArrayList<FileLocation> locations;
        public ArrayList<MessageObject> messageObjects;
        public ArrayList<Message> messages;
        public MessageObject obj;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public Object parentObject;
        public ArrayList<Object> parentObjects;
        public long peer;
        public boolean performMediaUpload;
        ArrayList<DelayedMessageSendAfterRequest> requests;
        public TLObject sendEncryptedRequest;
        public TLObject sendRequest;
        public int type;
        public VideoEditedInfo videoEditedInfo;
        public ArrayList<VideoEditedInfo> videoEditedInfos;

        public DelayedMessage(long peer) {
            this.peer = peer;
        }

        public void initForGroup(long id) {
            this.type = 4;
            this.groupId = id;
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

        public void addDelayedRequest(TLObject req, MessageObject msgObj, String originalPath, Object parentObject, DelayedMessage delayedMessage) {
            DelayedMessageSendAfterRequest request = new DelayedMessageSendAfterRequest();
            request.request = req;
            request.msgObj = msgObj;
            request.originalPath = originalPath;
            request.delayedMessage = delayedMessage;
            request.parentObject = parentObject;
            if (this.requests == null) {
                this.requests = new ArrayList();
            }
            this.requests.add(request);
        }

        public void addDelayedRequest(TLObject req, ArrayList<MessageObject> msgObjs, ArrayList<String> originalPaths, ArrayList<Object> parentObjects, DelayedMessage delayedMessage) {
            DelayedMessageSendAfterRequest request = new DelayedMessageSendAfterRequest();
            request.request = req;
            request.msgObjs = msgObjs;
            request.originalPaths = originalPaths;
            request.delayedMessage = delayedMessage;
            request.parentObjects = parentObjects;
            if (this.requests == null) {
                this.requests = new ArrayList();
            }
            this.requests.add(request);
        }

        public void sendDelayedRequests() {
            if (this.requests == null) {
                return;
            }
            if (this.type == 4 || this.type == 0) {
                int size = this.requests.size();
                for (int a = 0; a < size; a++) {
                    DelayedMessageSendAfterRequest request = (DelayedMessageSendAfterRequest) this.requests.get(a);
                    if (request.request instanceof TL_messages_sendEncryptedMultiMedia) {
                        SecretChatHelper.getInstance(SendMessagesHelper.this.currentAccount).performSendEncryptedRequest((TL_messages_sendEncryptedMultiMedia) request.request, this);
                    } else if (request.request instanceof TL_messages_sendMultiMedia) {
                        SendMessagesHelper.this.performSendMessageRequestMulti((TL_messages_sendMultiMedia) request.request, request.msgObjs, request.originalPaths, request.parentObjects, request.delayedMessage);
                    } else {
                        SendMessagesHelper.this.performSendMessageRequest(request.request, request.msgObj, request.originalPath, request.delayedMessage, request.parentObject);
                    }
                }
                this.requests = null;
            }
        }

        public void markAsError() {
            if (this.type == 4) {
                for (int a = 0; a < this.messageObjects.size(); a++) {
                    MessageObject obj = (MessageObject) this.messageObjects.get(a);
                    MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(obj.messageOwner);
                    obj.messageOwner.send_state = 2;
                    NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(obj.getId()));
                    SendMessagesHelper.this.processSentMessage(obj.getId());
                }
                SendMessagesHelper.this.delayedMessages.remove("group_" + this.groupId);
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

        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
        }

        private class GpsLocationListener implements LocationListener {
            private GpsLocationListener() {
            }

            /* synthetic */ GpsLocationListener(LocationProvider x0, AnonymousClass1 x1) {
                this();
            }

            public void onLocationChanged(Location location) {
                if (location != null && LocationProvider.this.locationQueryCancelRunnable != null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("found location " + location);
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

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
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
            } catch (Throwable e) {
                FileLog.e(e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
            try {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                if (this.lastKnownLocation == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Throwable e22) {
                FileLog.e(e22);
            }
            if (this.locationQueryCancelRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
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
                if (this.locationQueryCancelRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
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

        /* synthetic */ MediaSendPrepareWorker(AnonymousClass1 x0) {
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
        int cores;
        if (VERSION.SDK_INT >= 17) {
            cores = Runtime.getRuntime().availableProcessors();
        } else {
            cores = 2;
        }
        mediaSendThreadPool = new ThreadPoolExecutor(cores, cores, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }

    public static SendMessagesHelper getInstance(int num) {
        Throwable th;
        SendMessagesHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (SendMessagesHelper.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        SendMessagesHelper[] sendMessagesHelperArr = Instance;
                        SendMessagesHelper localInstance2 = new SendMessagesHelper(num);
                        try {
                            sendMessagesHelperArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public SendMessagesHelper(int instance) {
        this.currentAccount = instance;
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$0(this));
    }

    final /* synthetic */ void lambda$new$0$SendMessagesHelper() {
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

    public void setCurrentChatInfo(ChatFull info) {
        this.currentChatInfo = info;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String location;
        ArrayList<DelayedMessage> arr;
        int a;
        DelayedMessage message;
        int index;
        MessageObject messageObject;
        String finalPath;
        int b;
        String path;
        if (id == NotificationCenter.FileDidUpload) {
            location = args[0];
            InputFile file = args[1];
            InputEncryptedFile encryptedFile = args[2];
            arr = (ArrayList) this.delayedMessages.get(location);
            if (arr != null) {
                a = 0;
                while (a < arr.size()) {
                    message = (DelayedMessage) arr.get(a);
                    InputMedia media = null;
                    if (message.sendRequest instanceof TL_messages_sendMedia) {
                        media = ((TL_messages_sendMedia) message.sendRequest).media;
                    } else if (message.sendRequest instanceof TL_messages_editMessage) {
                        media = ((TL_messages_editMessage) message.sendRequest).media;
                    } else if (message.sendRequest instanceof TL_messages_sendBroadcast) {
                        media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                    } else if (message.sendRequest instanceof TL_messages_sendMultiMedia) {
                        media = (InputMedia) message.extraHashMap.get(location);
                    }
                    if (file != null && media != null) {
                        if (message.type == 0) {
                            media.file = file;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, message, true, null, message.parentObject);
                        } else if (message.type == 1) {
                            if (media.file == null) {
                                media.file = file;
                                if (media.thumb != null || message.location == null) {
                                    performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject);
                                } else {
                                    performSendDelayedMessage(message);
                                }
                            } else {
                                media.thumb = file;
                                media.flags |= 4;
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject);
                            }
                        } else if (message.type == 2) {
                            if (media.file == null) {
                                media.file = file;
                                if (media.thumb != null || message.location == null) {
                                    performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject);
                                } else {
                                    performSendDelayedMessage(message);
                                }
                            } else {
                                media.thumb = file;
                                media.flags |= 4;
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject);
                            }
                        } else if (message.type == 3) {
                            media.file = file;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, null, message.parentObject);
                        } else if (message.type == 4) {
                            if (!(media instanceof TL_inputMediaUploadedDocument)) {
                                media.file = file;
                                uploadMultiMedia(message, media, null, location);
                            } else if (media.file == null) {
                                media.file = file;
                                index = message.messageObjects.indexOf((MessageObject) message.extraHashMap.get(location + "_i"));
                                message.location = (FileLocation) message.extraHashMap.get(location + "_t");
                                stopVideoService(((MessageObject) message.messageObjects.get(index)).messageOwner.attachPath);
                                if (media.thumb != null || message.location == null) {
                                    uploadMultiMedia(message, media, null, location);
                                } else {
                                    message.performMediaUpload = true;
                                    performSendDelayedMessage(message, index);
                                }
                            } else {
                                media.thumb = file;
                                media.flags |= 4;
                                uploadMultiMedia(message, media, null, (String) message.extraHashMap.get(location + "_o"));
                            }
                        }
                        arr.remove(a);
                        a--;
                    } else if (!(encryptedFile == null || message.sendEncryptedRequest == null)) {
                        TL_decryptedMessage decryptedMessage = null;
                        if (message.type == 4) {
                            TL_messages_sendEncryptedMultiMedia req = (TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
                            InputEncryptedFile inputEncryptedFile = (InputEncryptedFile) message.extraHashMap.get(location);
                            index = req.files.indexOf(inputEncryptedFile);
                            if (index >= 0) {
                                req.files.set(index, encryptedFile);
                                if (inputEncryptedFile.id == 1) {
                                    messageObject = (MessageObject) message.extraHashMap.get(location + "_i");
                                    message.location = (FileLocation) message.extraHashMap.get(location + "_t");
                                    stopVideoService(((MessageObject) message.messageObjects.get(index)).messageOwner.attachPath);
                                }
                                decryptedMessage = (TL_decryptedMessage) req.messages.get(index);
                            }
                        } else {
                            decryptedMessage = message.sendEncryptedRequest;
                        }
                        if (decryptedMessage != null) {
                            if ((decryptedMessage.media instanceof TL_decryptedMessageMediaVideo) || (decryptedMessage.media instanceof TL_decryptedMessageMediaPhoto) || (decryptedMessage.media instanceof TL_decryptedMessageMediaDocument)) {
                                decryptedMessage.media.size = (int) ((Long) args[5]).longValue();
                            }
                            decryptedMessage.media.key = (byte[]) args[3];
                            decryptedMessage.media.iv = (byte[]) args[4];
                            if (message.type == 4) {
                                uploadMultiMedia(message, null, encryptedFile, location);
                            } else {
                                SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(decryptedMessage, message.obj.messageOwner, message.encryptedChat, encryptedFile, message.originalPath, message.obj);
                            }
                        }
                        arr.remove(a);
                        a--;
                    }
                    a++;
                }
                if (arr.isEmpty()) {
                    this.delayedMessages.remove(location);
                }
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            location = (String) args[0];
            boolean enc = ((Boolean) args[1]).booleanValue();
            arr = (ArrayList) this.delayedMessages.get(location);
            if (arr != null) {
                a = 0;
                while (a < arr.size()) {
                    DelayedMessage obj = (DelayedMessage) arr.get(a);
                    if ((enc && obj.sendEncryptedRequest != null) || !(enc || obj.sendRequest == null)) {
                        obj.markAsError();
                        arr.remove(a);
                        a--;
                    }
                    a++;
                }
                if (arr.isEmpty()) {
                    this.delayedMessages.remove(location);
                }
            }
        } else if (id == NotificationCenter.filePreparingStarted) {
            messageObject = (MessageObject) args[0];
            if (messageObject.getId() != 0) {
                finalPath = args[1];
                arr = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                if (arr != null) {
                    a = 0;
                    while (a < arr.size()) {
                        message = (DelayedMessage) arr.get(a);
                        if (message.type == 4) {
                            index = message.messageObjects.indexOf(messageObject);
                            message.location = (FileLocation) message.extraHashMap.get(messageObject.messageOwner.attachPath + "_t");
                            message.performMediaUpload = true;
                            performSendDelayedMessage(message, index);
                            arr.remove(a);
                            break;
                        } else if (message.obj == messageObject) {
                            message.videoEditedInfo = null;
                            performSendDelayedMessage(message);
                            arr.remove(a);
                            break;
                        } else {
                            a++;
                        }
                    }
                    if (arr.isEmpty()) {
                        this.delayedMessages.remove(messageObject.messageOwner.attachPath);
                    }
                }
            }
        } else if (id == NotificationCenter.fileNewChunkAvailable) {
            messageObject = (MessageObject) args[0];
            if (messageObject.getId() != 0) {
                finalPath = (String) args[1];
                long availableSize = ((Long) args[2]).longValue();
                long finalSize = ((Long) args[3]).longValue();
                FileLoader.getInstance(this.currentAccount).checkUploadNewDataAvailable(finalPath, ((int) messageObject.getDialogId()) == 0, availableSize, finalSize);
                if (finalSize != 0) {
                    stopVideoService(messageObject.messageOwner.attachPath);
                    arr = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                    if (arr != null) {
                        for (a = 0; a < arr.size(); a++) {
                            message = (DelayedMessage) arr.get(a);
                            ArrayList messages;
                            if (message.type == 4) {
                                for (b = 0; b < message.messageObjects.size(); b++) {
                                    MessageObject obj2 = (MessageObject) message.messageObjects.get(b);
                                    if (obj2 == messageObject) {
                                        obj2.videoEditedInfo = null;
                                        obj2.messageOwner.params.remove("ve");
                                        obj2.messageOwner.media.document.size = (int) finalSize;
                                        messages = new ArrayList();
                                        messages.add(obj2.messageOwner);
                                        MessagesStorage.getInstance(this.currentAccount).putMessages(messages, false, true, false, 0);
                                        break;
                                    }
                                }
                            } else if (message.obj == messageObject) {
                                message.obj.videoEditedInfo = null;
                                message.obj.messageOwner.params.remove("ve");
                                message.obj.messageOwner.media.document.size = (int) finalSize;
                                messages = new ArrayList();
                                messages.add(message.obj.messageOwner);
                                MessagesStorage.getInstance(this.currentAccount).putMessages(messages, false, true, false, 0);
                                return;
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.filePreparingFailed) {
            messageObject = (MessageObject) args[0];
            if (messageObject.getId() != 0) {
                finalPath = (String) args[1];
                stopVideoService(messageObject.messageOwner.attachPath);
                arr = (ArrayList) this.delayedMessages.get(finalPath);
                if (arr != null) {
                    a = 0;
                    while (a < arr.size()) {
                        message = (DelayedMessage) arr.get(a);
                        if (message.type == 4) {
                            for (b = 0; b < message.messages.size(); b++) {
                                if (message.messageObjects.get(b) == messageObject) {
                                    message.markAsError();
                                    arr.remove(a);
                                    a--;
                                    break;
                                }
                            }
                        } else if (message.obj == messageObject) {
                            message.markAsError();
                            arr.remove(a);
                            a--;
                        }
                        a++;
                    }
                    if (arr.isEmpty()) {
                        this.delayedMessages.remove(finalPath);
                    }
                }
            }
        } else if (id == NotificationCenter.httpFileDidLoad) {
            path = args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                for (a = 0; a < arr.size(); a++) {
                    message = (DelayedMessage) arr.get(a);
                    int fileType = -1;
                    if (message.type == 0) {
                        fileType = 0;
                        messageObject = message.obj;
                    } else if (message.type == 2) {
                        fileType = 1;
                        messageObject = message.obj;
                    } else if (message.type == 4) {
                        messageObject = (MessageObject) message.extraHashMap.get(path);
                        if (messageObject.getDocument() != null) {
                            fileType = 1;
                        } else {
                            fileType = 0;
                        }
                    } else {
                        messageObject = null;
                    }
                    if (fileType == 0) {
                        Utilities.globalQueue.postRunnable(new SendMessagesHelper$$Lambda$1(this, new File(FileLoader.getDirectory(4), Utilities.MD5(path) + "." + ImageLoader.getHttpUrlExtension(path, "file")), messageObject, message, path));
                    } else if (fileType == 1) {
                        Utilities.globalQueue.postRunnable(new SendMessagesHelper$$Lambda$2(this, message, new File(FileLoader.getDirectory(4), Utilities.MD5(path) + ".gif"), messageObject));
                    }
                }
                this.delayedMessages.remove(path);
            }
        } else if (id == NotificationCenter.fileDidLoad) {
            path = (String) args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                for (a = 0; a < arr.size(); a++) {
                    performSendDelayedMessage((DelayedMessage) arr.get(a));
                }
                this.delayedMessages.remove(path);
            }
        } else if (id == NotificationCenter.httpFileDidFailedLoad || id == NotificationCenter.fileDidFailedLoad) {
            path = (String) args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                for (a = 0; a < arr.size(); a++) {
                    ((DelayedMessage) arr.get(a)).markAsError();
                }
                this.delayedMessages.remove(path);
            }
        }
    }

    final /* synthetic */ void lambda$didReceivedNotification$2$SendMessagesHelper(File cacheFile, MessageObject messageObject, DelayedMessage message, String path) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$62(this, generatePhotoSizes(cacheFile.toString(), null), messageObject, cacheFile, message, path));
    }

    final /* synthetic */ void lambda$null$1$SendMessagesHelper(TL_photo photo, MessageObject messageObject, File cacheFile, DelayedMessage message, String path) {
        if (photo != null) {
            messageObject.messageOwner.media.photo = photo;
            messageObject.messageOwner.attachPath = cacheFile.toString();
            ArrayList messages = new ArrayList();
            messages.add(messageObject.messageOwner);
            MessagesStorage.getInstance(this.currentAccount).putMessages(messages, false, true, false, 0);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, messageObject.messageOwner);
            message.location = ((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location;
            message.httpLocation = null;
            if (message.type == 4) {
                message.performMediaUpload = true;
                performSendDelayedMessage(message, message.messageObjects.indexOf(messageObject));
                return;
            }
            performSendDelayedMessage(message);
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("can't load image " + path + " to file " + cacheFile.toString());
        }
        message.markAsError();
    }

    final /* synthetic */ void lambda$didReceivedNotification$4$SendMessagesHelper(DelayedMessage message, File cacheFile, MessageObject messageObject) {
        Document document = message.obj.getDocument();
        if (document.thumbs.isEmpty() || (((PhotoSize) document.thumbs.get(0)).location instanceof TL_fileLocationUnavailable)) {
            try {
                Bitmap bitmap = ImageLoader.loadBitmap(cacheFile.getAbsolutePath(), null, 90.0f, 90.0f, true);
                if (bitmap != null) {
                    boolean z;
                    document.thumbs.clear();
                    ArrayList arrayList = document.thumbs;
                    if (message.sendEncryptedRequest != null) {
                        z = true;
                    } else {
                        z = false;
                    }
                    arrayList.add(ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, z));
                    bitmap.recycle();
                }
            } catch (Throwable e) {
                document.thumbs.clear();
                FileLog.e(e);
            }
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$61(this, message, cacheFile, document, messageObject));
    }

    final /* synthetic */ void lambda$null$3$SendMessagesHelper(DelayedMessage message, File cacheFile, Document document, MessageObject messageObject) {
        message.httpLocation = null;
        message.obj.messageOwner.attachPath = cacheFile.toString();
        if (!document.thumbs.isEmpty()) {
            message.location = ((PhotoSize) document.thumbs.get(0)).location;
        }
        ArrayList messages = new ArrayList();
        messages.add(messageObject.messageOwner);
        MessagesStorage.getInstance(this.currentAccount).putMessages(messages, false, true, false, 0);
        message.performMediaUpload = true;
        performSendDelayedMessage(message);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, message.obj.messageOwner);
    }

    private void revertEditingMessageObject(MessageObject object) {
        object.cancelEditing = true;
        object.messageOwner.media = object.previousMedia;
        object.messageOwner.message = object.previousCaption;
        object.messageOwner.entities = object.previousCaptionEntities;
        object.messageOwner.attachPath = object.previousAttachPath;
        object.messageOwner.send_state = 0;
        object.previousMedia = null;
        object.previousCaption = null;
        object.previousCaptionEntities = null;
        object.previousAttachPath = null;
        object.videoEditedInfo = null;
        object.type = -1;
        object.setType();
        object.caption = null;
        object.generateCaption();
        ArrayList arr = new ArrayList();
        arr.add(object.messageOwner);
        MessagesStorage.getInstance(this.currentAccount).putMessages(arr, false, true, false, 0);
        new ArrayList().add(object);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(object.getDialogId()), arrayList);
    }

    public void cancelSendingMessage(MessageObject object) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(object);
        cancelSendingMessage(arrayList);
    }

    public void cancelSendingMessage(ArrayList<MessageObject> objects) {
        int a;
        ArrayList<String> keysToRemove = new ArrayList();
        ArrayList<Integer> messageIds = new ArrayList();
        boolean enc = false;
        int channelId = 0;
        int c = 0;
        while (c < objects.size()) {
            MessageObject object = (MessageObject) objects.get(c);
            messageIds.add(Integer.valueOf(object.getId()));
            int channelId2 = object.messageOwner.to_id.channel_id;
            Message sendingMessage = removeFromSendingMessages(object.getId());
            if (sendingMessage != null) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(sendingMessage.reqId, true);
            }
            for (Entry<String, ArrayList<DelayedMessage>> entry : this.delayedMessages.entrySet()) {
                ArrayList<DelayedMessage> messages = (ArrayList) entry.getValue();
                a = 0;
                while (a < messages.size()) {
                    DelayedMessage message = (DelayedMessage) messages.get(a);
                    if (message.type == 4) {
                        int index = -1;
                        MessageObject messageObject = null;
                        for (int b = 0; b < message.messageObjects.size(); b++) {
                            messageObject = (MessageObject) message.messageObjects.get(b);
                            if (messageObject.getId() == object.getId()) {
                                index = b;
                                break;
                            }
                        }
                        if (index >= 0) {
                            message.messageObjects.remove(index);
                            message.messages.remove(index);
                            message.originalPaths.remove(index);
                            if (message.sendRequest != null) {
                                ((TL_messages_sendMultiMedia) message.sendRequest).multi_media.remove(index);
                            } else {
                                TL_messages_sendEncryptedMultiMedia request = (TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
                                request.messages.remove(index);
                                request.files.remove(index);
                            }
                            MediaController.getInstance().cancelVideoConvert(object);
                            String keyToRemove = (String) message.extraHashMap.get(messageObject);
                            if (keyToRemove != null) {
                                keysToRemove.add(keyToRemove);
                            }
                            if (message.messageObjects.isEmpty()) {
                                message.sendDelayedRequests();
                            } else {
                                if (message.finalGroupMessage == object.getId()) {
                                    MessageObject prevMessage = (MessageObject) message.messageObjects.get(message.messageObjects.size() - 1);
                                    message.finalGroupMessage = prevMessage.getId();
                                    prevMessage.messageOwner.params.put("final", "1");
                                    messages_Messages messagesRes = new TL_messages_messages();
                                    messagesRes.messages.add(prevMessage.messageOwner);
                                    MessagesStorage.getInstance(this.currentAccount).putMessages(messagesRes, message.peer, -2, 0, false);
                                }
                                sendReadyToSendGroup(message, false, true);
                            }
                        }
                    } else if (message.obj.getId() == object.getId()) {
                        messages.remove(a);
                        message.sendDelayedRequests();
                        MediaController.getInstance().cancelVideoConvert(message.obj);
                        if (messages.size() == 0) {
                            keysToRemove.add(entry.getKey());
                            if (message.sendEncryptedRequest != null) {
                                enc = true;
                            }
                        }
                    } else {
                        a++;
                    }
                }
            }
            c++;
            channelId = channelId2;
        }
        for (a = 0; a < keysToRemove.size(); a++) {
            String key = (String) keysToRemove.get(a);
            if (key.startsWith("http")) {
                ImageLoader.getInstance().cancelLoadHttpFile(key);
            } else {
                FileLoader.getInstance(this.currentAccount).cancelUploadFile(key, enc);
            }
            stopVideoService(key);
            this.delayedMessages.remove(key);
        }
        if (objects.size() == 1 && ((MessageObject) objects.get(0)).isEditing() && ((MessageObject) objects.get(0)).previousMedia != null) {
            revertEditingMessageObject((MessageObject) objects.get(0));
            return;
        }
        MessagesController.getInstance(this.currentAccount).deleteMessages(messageIds, null, null, channelId, false);
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean unsent) {
        if (messageObject.getId() >= 0) {
            if (messageObject.isEditing()) {
                editMessageMedia(messageObject, null, null, null, null, null, true, messageObject);
            }
            return false;
        } else if (messageObject.messageOwner.action instanceof TL_messageEncryptedAction) {
            EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(messageObject.messageOwner);
                messageObject.messageOwner.send_state = 2;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            if (messageObject.messageOwner.random_id == 0) {
                messageObject.messageOwner.random_id = getNextRandomId();
            }
            if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                SecretChatHelper.getInstance(this.currentAccount).sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionDeleteMessages) {
                SecretChatHelper.getInstance(this.currentAccount).sendMessagesDeleteMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionFlushHistory) {
                SecretChatHelper.getInstance(this.currentAccount).sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNotifyLayer) {
                SecretChatHelper.getInstance(this.currentAccount).sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionReadMessages) {
                SecretChatHelper.getInstance(this.currentAccount).sendMessagesReadMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                SecretChatHelper.getInstance(this.currentAccount).sendScreenshotMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (!((messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionTyping) || (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionResend))) {
                if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionCommitKey) {
                    SecretChatHelper.getInstance(this.currentAccount).sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAbortKey) {
                    SecretChatHelper.getInstance(this.currentAccount).sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionRequestKey) {
                    SecretChatHelper.getInstance(this.currentAccount).sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAcceptKey) {
                    SecretChatHelper.getInstance(this.currentAccount).sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNoop) {
                    SecretChatHelper.getInstance(this.currentAccount).sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
        } else {
            if (messageObject.messageOwner.action instanceof TL_messageActionScreenshotTaken) {
                sendScreenshotMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) messageObject.getDialogId())), messageObject.messageOwner.reply_to_msg_id, messageObject.messageOwner);
            }
            if (unsent) {
                this.unsentMessages.put(messageObject.getId(), messageObject);
            }
            sendMessage(messageObject);
            return true;
        }
    }

    protected void processSentMessage(int id) {
        int prevSize = this.unsentMessages.size();
        this.unsentMessages.remove(id);
        if (prevSize != 0 && this.unsentMessages.size() == 0) {
            checkUnsentMessages();
        }
    }

    public void processForwardFromMyName(MessageObject messageObject, long did) {
        if (messageObject != null) {
            ArrayList<MessageObject> arrayList;
            if (messageObject.messageOwner.media == null || (messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || (messageObject.messageOwner.media instanceof TL_messageMediaGame) || (messageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                if (messageObject.messageOwner.message != null) {
                    ArrayList<MessageEntity> entities;
                    WebPage webPage = null;
                    if (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) {
                        webPage = messageObject.messageOwner.media.webpage;
                    }
                    if (messageObject.messageOwner.entities == null || messageObject.messageOwner.entities.isEmpty()) {
                        entities = null;
                    } else {
                        entities = new ArrayList();
                        for (int a = 0; a < messageObject.messageOwner.entities.size(); a++) {
                            MessageEntity entity = (MessageEntity) messageObject.messageOwner.entities.get(a);
                            if ((entity instanceof TL_messageEntityBold) || (entity instanceof TL_messageEntityItalic) || (entity instanceof TL_messageEntityPre) || (entity instanceof TL_messageEntityCode) || (entity instanceof TL_messageEntityTextUrl)) {
                                entities.add(entity);
                            }
                        }
                    }
                    sendMessage(messageObject.messageOwner.message, did, messageObject.replyMessageObject, webPage, true, entities, null, null);
                } else if (((int) did) != 0) {
                    arrayList = new ArrayList();
                    arrayList.add(messageObject);
                    sendMessage(arrayList, did);
                }
            } else if (messageObject.messageOwner.media.photo instanceof TL_photo) {
                sendMessage((TL_photo) messageObject.messageOwner.media.photo, null, did, messageObject.replyMessageObject, messageObject.messageOwner.message, messageObject.messageOwner.entities, null, null, messageObject.messageOwner.media.ttl_seconds, messageObject);
            } else if (messageObject.messageOwner.media.document instanceof TL_document) {
                sendMessage((TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, messageObject.replyMessageObject, messageObject.messageOwner.message, messageObject.messageOwner.entities, null, null, messageObject.messageOwner.media.ttl_seconds, messageObject);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaVenue) || (messageObject.messageOwner.media instanceof TL_messageMediaGeo)) {
                sendMessage(messageObject.messageOwner.media, did, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.phone_number != null) {
                User user = new TL_userContact_old2();
                user.phone = messageObject.messageOwner.media.phone_number;
                user.first_name = messageObject.messageOwner.media.first_name;
                user.last_name = messageObject.messageOwner.media.last_name;
                user.id = messageObject.messageOwner.media.user_id;
                sendMessage(user, did, messageObject.replyMessageObject, null, null);
            } else if (((int) did) != 0) {
                arrayList = new ArrayList();
                arrayList.add(messageObject);
                sendMessage(arrayList, did);
            }
        }
    }

    public void sendScreenshotMessage(User user, int messageId, Message resendMessage) {
        if (user != null && messageId != 0 && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            Message message;
            TLObject req = new TL_messages_sendScreenshotNotification();
            req.peer = new TL_inputPeerUser();
            req.peer.access_hash = user.access_hash;
            req.peer.user_id = user.id;
            if (resendMessage != null) {
                message = resendMessage;
                req.reply_to_msg_id = messageId;
                req.random_id = resendMessage.random_id;
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
                message.reply_to_msg_id = messageId;
                message.to_id = new TL_peerUser();
                message.to_id.user_id = user.id;
                message.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                message.action = new TL_messageActionScreenshotTaken();
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
            }
            req.random_id = message.random_id;
            MessageObject newMsgObj = new MessageObject(this.currentAccount, message, false);
            newMsgObj.messageOwner.send_state = 1;
            ArrayList<MessageObject> objArr = new ArrayList();
            objArr.add(newMsgObj);
            MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(message.dialog_id, objArr);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            ArrayList arr = new ArrayList();
            arr.add(message);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arr, false, true, false, 0);
            performSendMessageRequest(req, newMsgObj, null, null, null);
        }
    }

    public void sendSticker(Document document, long peer, MessageObject replyingMessageObject, Object parentObject) {
        if (document != null) {
            if (((int) peer) == 0) {
                if (MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (peer >> 32))) != null) {
                    Document newDocument = new TL_document_layer82();
                    newDocument.id = document.id;
                    newDocument.access_hash = document.access_hash;
                    newDocument.date = document.date;
                    newDocument.mime_type = document.mime_type;
                    newDocument.file_reference = document.file_reference;
                    if (newDocument.file_reference == null) {
                        newDocument.file_reference = new byte[0];
                    }
                    newDocument.size = document.size;
                    newDocument.dc_id = document.dc_id;
                    newDocument.attributes = new ArrayList(document.attributes);
                    if (newDocument.mime_type == null) {
                        newDocument.mime_type = "";
                    }
                    TLObject thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                    if (thumb instanceof TL_photoSize) {
                        File file = FileLoader.getPathToAttach(thumb, true);
                        if (file.exists()) {
                            try {
                                int len = (int) file.length();
                                byte[] arr = new byte[((int) file.length())];
                                new RandomAccessFile(file, "r").readFully(arr);
                                PhotoSize newThumb = new TL_photoCachedSize();
                                newThumb.location = thumb.location;
                                newThumb.size = thumb.size;
                                newThumb.w = thumb.w;
                                newThumb.h = thumb.h;
                                newThumb.type = thumb.type;
                                newThumb.bytes = arr;
                                newDocument.thumbs.add(newThumb);
                                newDocument.flags |= 1;
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                    }
                    document = newDocument;
                } else {
                    return;
                }
            }
            if (document instanceof TL_document) {
                sendMessage((TL_document) document, null, null, peer, replyingMessageObject, null, null, null, null, 0, parentObject);
            }
        }
    }

    public int sendMessage(ArrayList<MessageObject> messages, long peer) {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }
        int lower_id = (int) peer;
        int sendResult = 0;
        int a;
        if (lower_id != 0) {
            Chat chat;
            Peer to_id = MessagesController.getInstance(this.currentAccount).getPeer((int) peer);
            boolean isMegagroup = false;
            boolean isSignature = false;
            boolean canSendStickers = true;
            boolean canSendMedia = true;
            boolean canSendPolls = true;
            boolean canSendPreview = true;
            if (lower_id <= 0) {
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
                if (ChatObject.isChannel(chat)) {
                    isMegagroup = chat.megagroup;
                    isSignature = chat.signatures;
                }
                canSendStickers = ChatObject.canSendStickers(chat);
                canSendMedia = ChatObject.canSendMedia(chat);
                canSendPreview = ChatObject.canSendEmbed(chat);
                canSendPolls = ChatObject.canSendPolls(chat);
            } else if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id)) == null) {
                return 0;
            } else {
                chat = null;
            }
            LongSparseArray<Long> groupsMap = new LongSparseArray();
            ArrayList<MessageObject> objArr = new ArrayList();
            ArrayList<Message> arr = new ArrayList();
            ArrayList<Long> randomIds = new ArrayList();
            ArrayList<Integer> ids = new ArrayList();
            LongSparseArray<Message> messagesByRandomIds = new LongSparseArray();
            InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
            int myId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            boolean toMyself = peer == ((long) myId);
            a = 0;
            while (a < messages.size()) {
                MessageObject msgObj = (MessageObject) messages.get(a);
                if (msgObj.getId() > 0 && !msgObj.needDrawBluredPreview()) {
                    if (canSendStickers || !(msgObj.isSticker() || msgObj.isGif() || msgObj.isGame())) {
                        if (canSendMedia || !((msgObj.messageOwner.media instanceof TL_messageMediaPhoto) || (msgObj.messageOwner.media instanceof TL_messageMediaDocument))) {
                            if (canSendPolls || !(msgObj.messageOwner.media instanceof TL_messageMediaPoll)) {
                                MessageFwdHeader messageFwdHeader;
                                boolean groupedIdChanged = false;
                                Message newMsg = new TL_message();
                                boolean forwardFromSaved = msgObj.getDialogId() == ((long) myId) && msgObj.messageOwner.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId();
                                if (msgObj.isForwarded()) {
                                    newMsg.fwd_from = new TL_messageFwdHeader();
                                    newMsg.fwd_from.flags = msgObj.messageOwner.fwd_from.flags;
                                    newMsg.fwd_from.from_id = msgObj.messageOwner.fwd_from.from_id;
                                    newMsg.fwd_from.date = msgObj.messageOwner.fwd_from.date;
                                    newMsg.fwd_from.channel_id = msgObj.messageOwner.fwd_from.channel_id;
                                    newMsg.fwd_from.channel_post = msgObj.messageOwner.fwd_from.channel_post;
                                    newMsg.fwd_from.post_author = msgObj.messageOwner.fwd_from.post_author;
                                    newMsg.flags = 4;
                                } else if (!forwardFromSaved) {
                                    newMsg.fwd_from = new TL_messageFwdHeader();
                                    newMsg.fwd_from.channel_post = msgObj.getId();
                                    messageFwdHeader = newMsg.fwd_from;
                                    messageFwdHeader.flags |= 4;
                                    if (msgObj.isFromUser()) {
                                        newMsg.fwd_from.from_id = msgObj.messageOwner.from_id;
                                        messageFwdHeader = newMsg.fwd_from;
                                        messageFwdHeader.flags |= 1;
                                    } else {
                                        newMsg.fwd_from.channel_id = msgObj.messageOwner.to_id.channel_id;
                                        messageFwdHeader = newMsg.fwd_from;
                                        messageFwdHeader.flags |= 2;
                                        if (msgObj.messageOwner.post && msgObj.messageOwner.from_id > 0) {
                                            newMsg.fwd_from.from_id = msgObj.messageOwner.from_id;
                                            messageFwdHeader = newMsg.fwd_from;
                                            messageFwdHeader.flags |= 1;
                                        }
                                    }
                                    if (msgObj.messageOwner.post_author != null) {
                                        newMsg.fwd_from.post_author = msgObj.messageOwner.post_author;
                                        messageFwdHeader = newMsg.fwd_from;
                                        messageFwdHeader.flags |= 8;
                                    } else if (!msgObj.isOutOwner() && msgObj.messageOwner.from_id > 0 && msgObj.messageOwner.post) {
                                        User signUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(msgObj.messageOwner.from_id));
                                        if (signUser != null) {
                                            newMsg.fwd_from.post_author = ContactsController.formatName(signUser.first_name, signUser.last_name);
                                            messageFwdHeader = newMsg.fwd_from;
                                            messageFwdHeader.flags |= 8;
                                        }
                                    }
                                    newMsg.date = msgObj.messageOwner.date;
                                    newMsg.flags = 4;
                                }
                                if (peer == ((long) myId) && newMsg.fwd_from != null) {
                                    messageFwdHeader = newMsg.fwd_from;
                                    messageFwdHeader.flags |= 16;
                                    newMsg.fwd_from.saved_from_msg_id = msgObj.getId();
                                    newMsg.fwd_from.saved_from_peer = msgObj.messageOwner.to_id;
                                }
                                if (canSendPreview || !(msgObj.messageOwner.media instanceof TL_messageMediaWebPage)) {
                                    newMsg.media = msgObj.messageOwner.media;
                                } else {
                                    newMsg.media = new TL_messageMediaEmpty();
                                }
                                if (newMsg.media != null) {
                                    newMsg.flags |= 512;
                                }
                                if (isMegagroup) {
                                    newMsg.flags |= Integer.MIN_VALUE;
                                }
                                if (msgObj.messageOwner.via_bot_id != 0) {
                                    newMsg.via_bot_id = msgObj.messageOwner.via_bot_id;
                                    newMsg.flags |= 2048;
                                }
                                newMsg.message = msgObj.messageOwner.message;
                                newMsg.fwd_msg_id = msgObj.getId();
                                newMsg.attachPath = msgObj.messageOwner.attachPath;
                                newMsg.entities = msgObj.messageOwner.entities;
                                if (!newMsg.entities.isEmpty()) {
                                    newMsg.flags |= 128;
                                }
                                if (newMsg.attachPath == null) {
                                    newMsg.attachPath = "";
                                }
                                int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                                newMsg.id = newMessageId;
                                newMsg.local_id = newMessageId;
                                newMsg.out = true;
                                long lastGroupedId = msgObj.messageOwner.grouped_id;
                                if (lastGroupedId != 0) {
                                    Long gId = (Long) groupsMap.get(msgObj.messageOwner.grouped_id);
                                    if (gId == null) {
                                        gId = Long.valueOf(Utilities.random.nextLong());
                                        groupsMap.put(msgObj.messageOwner.grouped_id, gId);
                                    }
                                    newMsg.grouped_id = gId.longValue();
                                    newMsg.flags |= 131072;
                                }
                                if (a != messages.size() - 1) {
                                    if (((MessageObject) messages.get(a + 1)).messageOwner.grouped_id != msgObj.messageOwner.grouped_id) {
                                        groupedIdChanged = true;
                                    }
                                }
                                if (to_id.channel_id == 0 || isMegagroup) {
                                    newMsg.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                                    newMsg.flags |= 256;
                                } else {
                                    newMsg.from_id = isSignature ? UserConfig.getInstance(this.currentAccount).getClientUserId() : -to_id.channel_id;
                                    newMsg.post = true;
                                }
                                if (newMsg.random_id == 0) {
                                    newMsg.random_id = getNextRandomId();
                                }
                                randomIds.add(Long.valueOf(newMsg.random_id));
                                messagesByRandomIds.put(newMsg.random_id, newMsg);
                                ids.add(Integer.valueOf(newMsg.fwd_msg_id));
                                newMsg.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                                if (!(inputPeer instanceof TL_inputPeerChannel) || isMegagroup) {
                                    if ((msgObj.messageOwner.flags & 1024) != 0) {
                                        newMsg.views = msgObj.messageOwner.views;
                                        newMsg.flags |= 1024;
                                    }
                                    newMsg.unread = true;
                                } else {
                                    newMsg.views = 1;
                                    newMsg.flags |= 1024;
                                }
                                newMsg.dialog_id = peer;
                                newMsg.to_id = to_id;
                                if (MessageObject.isVoiceMessage(newMsg) || MessageObject.isRoundVideoMessage(newMsg)) {
                                    if (!(inputPeer instanceof TL_inputPeerChannel) || msgObj.getChannelId() == 0) {
                                        newMsg.media_unread = true;
                                    } else {
                                        newMsg.media_unread = msgObj.isContentUnread();
                                    }
                                }
                                if (msgObj.messageOwner.to_id instanceof TL_peerChannel) {
                                    newMsg.ttl = -msgObj.messageOwner.to_id.channel_id;
                                }
                                MessageObject messageObject = new MessageObject(this.currentAccount, newMsg, true);
                                messageObject.messageOwner.send_state = 1;
                                objArr.add(messageObject);
                                arr.add(newMsg);
                                putToSendingMessages(newMsg);
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("forward message user_id = " + inputPeer.user_id + " chat_id = " + inputPeer.chat_id + " channel_id = " + inputPeer.channel_id + " access_hash = " + inputPeer.access_hash);
                                }
                                if (!((groupedIdChanged && arr.size() > 0) || arr.size() == 100 || a == messages.size() - 1)) {
                                    if (a != messages.size() - 1) {
                                        if (((MessageObject) messages.get(a + 1)).getDialogId() == msgObj.getDialogId()) {
                                        }
                                    }
                                }
                                MessagesStorage.getInstance(this.currentAccount).putMessages(new ArrayList(arr), false, true, false, 0);
                                MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(peer, objArr);
                                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                                TL_messages_forwardMessages req = new TL_messages_forwardMessages();
                                req.to_peer = inputPeer;
                                req.grouped = lastGroupedId != 0;
                                if (req.to_peer instanceof TL_inputPeerChannel) {
                                    req.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + peer, false);
                                }
                                if (msgObj.messageOwner.to_id instanceof TL_peerChannel) {
                                    Chat channel = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(msgObj.messageOwner.to_id.channel_id));
                                    req.from_peer = new TL_inputPeerChannel();
                                    req.from_peer.channel_id = msgObj.messageOwner.to_id.channel_id;
                                    if (channel != null) {
                                        req.from_peer.access_hash = channel.access_hash;
                                    }
                                } else {
                                    req.from_peer = new TL_inputPeerEmpty();
                                }
                                req.random_id = randomIds;
                                req.id = ids;
                                boolean z = messages.size() == 1 && ((MessageObject) messages.get(0)).messageOwner.with_my_score;
                                req.with_my_score = z;
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SendMessagesHelper$$Lambda$3(this, peer, isMegagroup, toMyself, messagesByRandomIds, arr, objArr, to_id, req), 68);
                                if (a != messages.size() - 1) {
                                    objArr = new ArrayList();
                                    arr = new ArrayList();
                                    randomIds = new ArrayList();
                                    ids = new ArrayList();
                                    messagesByRandomIds = new LongSparseArray();
                                }
                            } else if (sendResult == 0) {
                                sendResult = ChatObject.isActionBannedByDefault(chat, 10) ? 6 : 3;
                            }
                        } else if (sendResult == 0) {
                            sendResult = ChatObject.isActionBannedByDefault(chat, 7) ? 5 : 2;
                        }
                    } else if (sendResult == 0) {
                        sendResult = ChatObject.isActionBannedByDefault(chat, 8) ? 4 : 1;
                    }
                }
                a++;
            }
            return sendResult;
        }
        for (a = 0; a < messages.size(); a++) {
            processForwardFromMyName((MessageObject) messages.get(a), peer);
        }
        return 0;
    }

    final /* synthetic */ void lambda$sendMessage$9$SendMessagesHelper(long peer, boolean isMegagroupFinal, boolean toMyself, LongSparseArray messagesByRandomIdsFinal, ArrayList newMsgObjArr, ArrayList newMsgArr, Peer to_id, TL_messages_forwardMessages req, TLObject response, TL_error error) {
        int a1;
        Message newMsgObj1;
        if (error == null) {
            Update update;
            SparseLongArray newMessagesByIds = new SparseLongArray();
            Updates updates = (Updates) response;
            a1 = 0;
            while (a1 < updates.updates.size()) {
                update = (Update) updates.updates.get(a1);
                if (update instanceof TL_updateMessageID) {
                    TL_updateMessageID updateMessageID = (TL_updateMessageID) update;
                    newMessagesByIds.put(updateMessageID.id, updateMessageID.random_id);
                    updates.updates.remove(a1);
                    a1--;
                }
                a1++;
            }
            Integer value = (Integer) MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(peer));
            if (value == null) {
                value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, peer));
                MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(peer), value);
            }
            int sentCount = 0;
            a1 = 0;
            while (a1 < updates.updates.size()) {
                update = (Update) updates.updates.get(a1);
                if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateNewChannelMessage)) {
                    Message message;
                    updates.updates.remove(a1);
                    a1--;
                    if (update instanceof TL_updateNewMessage) {
                        TL_updateNewMessage updateNewMessage = (TL_updateNewMessage) update;
                        message = updateNewMessage.message;
                        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, updateNewMessage.pts, -1, updateNewMessage.pts_count);
                    } else {
                        TL_updateNewChannelMessage updateNewChannelMessage = (TL_updateNewChannelMessage) update;
                        message = updateNewChannelMessage.message;
                        MessagesController.getInstance(this.currentAccount).processNewChannelDifferenceParams(updateNewChannelMessage.pts, updateNewChannelMessage.pts_count, message.to_id.channel_id);
                        if (isMegagroupFinal) {
                            message.flags |= Integer.MIN_VALUE;
                        }
                    }
                    ImageLoader.saveMessageThumbs(message);
                    message.unread = value.intValue() < message.id;
                    if (toMyself) {
                        message.out = true;
                        message.unread = false;
                        message.media_unread = false;
                    }
                    long random_id = newMessagesByIds.get(message.id);
                    if (random_id != 0) {
                        newMsgObj1 = (Message) messagesByRandomIdsFinal.get(random_id);
                        if (newMsgObj1 != null) {
                            int index = newMsgObjArr.indexOf(newMsgObj1);
                            if (index != -1) {
                                MessageObject msgObj1 = (MessageObject) newMsgArr.get(index);
                                newMsgObjArr.remove(index);
                                newMsgArr.remove(index);
                                int oldId = newMsgObj1.id;
                                ArrayList<Message> sentMessages = new ArrayList();
                                sentMessages.add(message);
                                updateMediaPaths(msgObj1, message, null, true);
                                newMsgObj1.id = message.id;
                                sentCount++;
                                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$57(this, newMsgObj1, oldId, to_id, sentMessages, peer, message));
                            }
                        }
                    }
                }
                a1++;
            }
            if (!updates.updates.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            }
            StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, sentCount);
        } else {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$58(this, error, req));
        }
        for (a1 = 0; a1 < newMsgObjArr.size(); a1++) {
            newMsgObj1 = (Message) newMsgObjArr.get(a1);
            MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsgObj1);
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$59(this, newMsgObj1));
        }
    }

    final /* synthetic */ void lambda$null$6$SendMessagesHelper(Message newMsgObj1, int oldId, Peer to_id, ArrayList sentMessages, long peer, Message message) {
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(newMsgObj1.random_id, Integer.valueOf(oldId), newMsgObj1.id, 0, false, to_id.channel_id);
        MessagesStorage.getInstance(this.currentAccount).putMessages(sentMessages, true, false, false, 0);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$60(this, newMsgObj1, peer, oldId, message));
    }

    final /* synthetic */ void lambda$null$5$SendMessagesHelper(Message newMsgObj1, long peer, int oldId, Message message) {
        newMsgObj1.send_state = 0;
        DataQuery.getInstance(this.currentAccount).increasePeerRaiting(peer);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(message.id), message, Long.valueOf(peer), Long.valueOf(0));
        processSentMessage(oldId);
        removeFromSendingMessages(oldId);
    }

    final /* synthetic */ void lambda$null$7$SendMessagesHelper(TL_error error, TL_messages_forwardMessages req) {
        AlertsCreator.processError(this.currentAccount, error, null, req, new Object[0]);
    }

    final /* synthetic */ void lambda$null$8$SendMessagesHelper(Message newMsgObj1) {
        newMsgObj1.send_state = 2;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj1.id));
        processSentMessage(newMsgObj1.id);
        removeFromSendingMessages(newMsgObj1.id);
    }

    private void writePreviousMessageData(Message message, SerializedData data) {
        message.media.serializeToStream(data);
        data.writeString(message.message != null ? message.message : "");
        data.writeString(message.attachPath != null ? message.attachPath : "");
        int count = message.entities.size();
        data.writeInt32(count);
        for (int a = 0; a < count; a++) {
            ((MessageEntity) message.entities.get(a)).serializeToStream(data);
        }
    }

    private void editMessageMedia(MessageObject messageObject, TL_photo photo, VideoEditedInfo videoEditedInfo, TL_document document, String path, HashMap<String, String> params, boolean retry, Object parentObject) {
        Throwable e;
        if (messageObject != null) {
            ArrayList<MessageEntity> entities;
            Message newMsg = messageObject.messageOwner;
            messageObject.cancelEditing = false;
            int type = -1;
            DelayedMessage delayedMessage = null;
            long peer = messageObject.getDialogId();
            if (retry) {
                if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                    photo = (TL_photo) messageObject.messageOwner.media.photo;
                    type = 2;
                } else {
                    document = (TL_document) messageObject.messageOwner.media.document;
                    if (MessageObject.isVideoDocument(document) || videoEditedInfo != null) {
                        type = 3;
                    } else {
                        type = 7;
                    }
                    videoEditedInfo = messageObject.videoEditedInfo;
                }
                params = newMsg.params;
                messageObject.editingMessage = newMsg.message;
                messageObject.editingMessageEntities = newMsg.entities;
                path = newMsg.attachPath;
            } else {
                messageObject.previousMedia = newMsg.media;
                messageObject.previousCaption = newMsg.message;
                messageObject.previousCaptionEntities = newMsg.entities;
                messageObject.previousAttachPath = newMsg.attachPath;
                SerializedData serializedData = new SerializedData(true);
                writePreviousMessageData(newMsg, serializedData);
                serializedData = new SerializedData(serializedData.length());
                writePreviousMessageData(newMsg, serializedData);
                if (params == null) {
                    params = new HashMap();
                }
                params.put("prevMedia", Base64.encodeToString(serializedData.toByteArray(), 0));
                serializedData.cleanup();
                MessageMedia messageMedia;
                if (photo != null) {
                    newMsg.media = new TL_messageMediaPhoto();
                    messageMedia = newMsg.media;
                    messageMedia.flags |= 3;
                    newMsg.media.photo = photo;
                    type = 2;
                    if (path != null && path.length() > 0) {
                        if (path.startsWith("http")) {
                            newMsg.attachPath = path;
                        }
                    }
                    try {
                        newMsg.attachPath = FileLoader.getPathToAttach(((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location, true).toString();
                    } catch (Exception e2) {
                        e = e2;
                        FileLog.e(e);
                        revertEditingMessageObject(messageObject);
                        return;
                    }
                } else if (document != null) {
                    newMsg.media = new TL_messageMediaDocument();
                    messageMedia = newMsg.media;
                    messageMedia.flags |= 3;
                    newMsg.media.document = document;
                    if (MessageObject.isVideoDocument(document) || videoEditedInfo != null) {
                        type = 3;
                    } else {
                        type = 7;
                    }
                    if (videoEditedInfo != null) {
                        params.put("ve", videoEditedInfo.getString());
                    }
                    newMsg.attachPath = path;
                }
                newMsg.params = params;
                newMsg.send_state = 3;
            }
            if (newMsg.attachPath == null) {
                newMsg.attachPath = "";
            }
            newMsg.local_id = 0;
            if ((messageObject.type == 3 || videoEditedInfo != null || messageObject.type == 2) && !TextUtils.isEmpty(newMsg.attachPath)) {
                messageObject.attachPathExists = true;
            }
            if (messageObject.videoEditedInfo != null && videoEditedInfo == null) {
                videoEditedInfo = messageObject.videoEditedInfo;
            }
            if (!retry) {
                if (messageObject.editingMessage != null) {
                    newMsg.message = messageObject.editingMessage.toString();
                    if (messageObject.editingMessageEntities != null) {
                        newMsg.entities = messageObject.editingMessageEntities;
                    } else {
                        entities = DataQuery.getInstance(this.currentAccount).getEntities(new CharSequence[]{messageObject.editingMessage});
                        if (!(entities == null || entities.isEmpty())) {
                            newMsg.entities = entities;
                        }
                    }
                    messageObject.caption = null;
                    messageObject.generateCaption();
                }
                ArrayList arr = new ArrayList();
                arr.add(newMsg);
                MessagesStorage.getInstance(this.currentAccount).putMessages(arr, false, true, false, 0);
                messageObject.type = -1;
                messageObject.setType();
                messageObject.createMessageSendInfo();
                new ArrayList().add(messageObject);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(peer), arrayList);
            }
            String originalPath = null;
            if (params != null) {
                if (params.containsKey("originalPath")) {
                    originalPath = (String) params.get("originalPath");
                }
            }
            boolean performMediaUpload = false;
            if ((type >= 1 && type <= 3) || (type >= 5 && type <= 8)) {
                InputMedia inputMedia = null;
                InputMedia media;
                DelayedMessage delayedMessage2;
                InputMedia uploadedDocument;
                if (type == 2) {
                    InputMedia uploadedPhoto = new TL_inputMediaUploadedPhoto();
                    if (params != null) {
                        String masks = (String) params.get("masks");
                        if (masks != null) {
                            AbstractSerializedData serializedData2 = new SerializedData(Utilities.hexToBytes(masks));
                            int count = serializedData2.readInt32(false);
                            for (int a = 0; a < count; a++) {
                                uploadedPhoto.stickers.add(InputDocument.TLdeserialize(serializedData2, serializedData2.readInt32(false), false));
                            }
                            uploadedPhoto.flags |= 1;
                            serializedData2.cleanup();
                        }
                    }
                    if (photo.access_hash == 0) {
                        inputMedia = uploadedPhoto;
                        performMediaUpload = true;
                    } else {
                        media = new TL_inputMediaPhoto();
                        media.id = new TL_inputPhoto();
                        media.id.id = photo.id;
                        media.id.access_hash = photo.access_hash;
                        media.id.file_reference = photo.file_reference;
                        if (media.id.file_reference == null) {
                            media.id.file_reference = new byte[0];
                        }
                        inputMedia = media;
                    }
                    delayedMessage2 = new DelayedMessage(peer);
                    try {
                        delayedMessage2.type = 0;
                        delayedMessage2.obj = messageObject;
                        delayedMessage2.originalPath = originalPath;
                        delayedMessage2.parentObject = parentObject;
                        delayedMessage2.inputUploadMedia = uploadedPhoto;
                        delayedMessage2.performMediaUpload = performMediaUpload;
                        if (path != null && path.length() > 0) {
                            if (path.startsWith("http")) {
                                delayedMessage2.httpLocation = path;
                                delayedMessage = delayedMessage2;
                            }
                        }
                        delayedMessage2.location = ((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location;
                        delayedMessage = delayedMessage2;
                    } catch (Exception e3) {
                        e = e3;
                        delayedMessage = delayedMessage;
                    }
                } else if (type == 3) {
                    uploadedDocument = new TL_inputMediaUploadedDocument();
                    uploadedDocument.mime_type = document.mime_type;
                    uploadedDocument.attributes = document.attributes;
                    if (!messageObject.isGif() && (videoEditedInfo == null || !videoEditedInfo.muted)) {
                        uploadedDocument.nosound_video = true;
                        if (BuildVars.DEBUG_VERSION) {
                            FileLog.d("nosound_video = true");
                        }
                    }
                    if (document.access_hash == 0) {
                        inputMedia = uploadedDocument;
                        performMediaUpload = true;
                    } else {
                        media = new TL_inputMediaDocument();
                        media.id = new TL_inputDocument();
                        media.id.id = document.id;
                        media.id.access_hash = document.access_hash;
                        media.id.file_reference = document.file_reference;
                        if (media.id.file_reference == null) {
                            media.id.file_reference = new byte[0];
                        }
                        inputMedia = media;
                    }
                    delayedMessage2 = new DelayedMessage(peer);
                    delayedMessage2.type = 1;
                    delayedMessage2.obj = messageObject;
                    delayedMessage2.originalPath = originalPath;
                    delayedMessage2.parentObject = parentObject;
                    delayedMessage2.inputUploadMedia = uploadedDocument;
                    delayedMessage2.performMediaUpload = performMediaUpload;
                    if (!document.thumbs.isEmpty()) {
                        delayedMessage2.location = ((PhotoSize) document.thumbs.get(0)).location;
                    }
                    delayedMessage2.videoEditedInfo = videoEditedInfo;
                    delayedMessage = delayedMessage2;
                } else if (type == 7) {
                    boolean http = false;
                    if (originalPath == null || originalPath.length() <= 0 || !originalPath.startsWith("http") || params == null) {
                        uploadedDocument = new TL_inputMediaUploadedDocument();
                    } else {
                        http = true;
                        InputMedia gifExternal = new TL_inputMediaGifExternal();
                        String[] args = ((String) params.get("url")).split("\\|");
                        if (args.length == 2) {
                            gifExternal.url = args[0];
                            gifExternal.q = args[1];
                        }
                        uploadedDocument = gifExternal;
                    }
                    uploadedDocument.mime_type = document.mime_type;
                    uploadedDocument.attributes = document.attributes;
                    if (document.access_hash == 0) {
                        inputMedia = uploadedDocument;
                        performMediaUpload = uploadedDocument instanceof TL_inputMediaUploadedDocument;
                    } else {
                        media = new TL_inputMediaDocument();
                        media.id = new TL_inputDocument();
                        media.id.id = document.id;
                        media.id.access_hash = document.access_hash;
                        media.id.file_reference = document.file_reference;
                        if (media.id.file_reference == null) {
                            media.id.file_reference = new byte[0];
                        }
                        inputMedia = media;
                    }
                    if (!http) {
                        delayedMessage2 = new DelayedMessage(peer);
                        delayedMessage2.originalPath = originalPath;
                        delayedMessage2.type = 2;
                        delayedMessage2.obj = messageObject;
                        if (!document.thumbs.isEmpty()) {
                            delayedMessage2.location = ((PhotoSize) document.thumbs.get(0)).location;
                        }
                        delayedMessage2.parentObject = parentObject;
                        delayedMessage2.inputUploadMedia = uploadedDocument;
                        delayedMessage2.performMediaUpload = performMediaUpload;
                        delayedMessage = delayedMessage2;
                    }
                }
                TLObject request = new TL_messages_editMessage();
                request.id = messageObject.getId();
                request.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) peer);
                request.flags |= 16384;
                request.media = inputMedia;
                if (messageObject.editingMessage != null) {
                    request.message = messageObject.editingMessage.toString();
                    request.flags |= 2048;
                    if (messageObject.editingMessageEntities != null) {
                        request.entities = messageObject.editingMessageEntities;
                        request.flags |= 8;
                    } else {
                        entities = DataQuery.getInstance(this.currentAccount).getEntities(new CharSequence[]{messageObject.editingMessage});
                        if (!(entities == null || entities.isEmpty())) {
                            request.entities = entities;
                            request.flags |= 8;
                        }
                    }
                    messageObject.editingMessage = null;
                    messageObject.editingMessageEntities = null;
                }
                if (delayedMessage != null) {
                    delayedMessage.sendRequest = request;
                }
                TLObject reqSend = request;
                if (type == 1) {
                    performSendMessageRequest(reqSend, messageObject, null, delayedMessage, parentObject);
                } else if (type == 2) {
                    if (performMediaUpload) {
                        performSendDelayedMessage(delayedMessage);
                    } else {
                        performSendMessageRequest(reqSend, messageObject, originalPath, null, true, delayedMessage, parentObject);
                    }
                } else if (type == 3) {
                    if (performMediaUpload) {
                        performSendDelayedMessage(delayedMessage);
                    } else {
                        performSendMessageRequest(reqSend, messageObject, originalPath, delayedMessage, parentObject);
                    }
                } else if (type == 6) {
                    performSendMessageRequest(reqSend, messageObject, originalPath, delayedMessage, parentObject);
                } else if (type == 7) {
                    if (performMediaUpload) {
                        performSendDelayedMessage(delayedMessage);
                    } else {
                        performSendMessageRequest(reqSend, messageObject, originalPath, delayedMessage, parentObject);
                    }
                } else if (type != 8) {
                } else {
                    if (performMediaUpload) {
                        performSendDelayedMessage(delayedMessage);
                    } else {
                        performSendMessageRequest(reqSend, messageObject, originalPath, delayedMessage, parentObject);
                    }
                }
            }
        }
    }

    public int editMessage(MessageObject messageObject, String message, boolean searchLinks, BaseFragment fragment, ArrayList<MessageEntity> entities, Runnable callback) {
        boolean z = false;
        if (fragment == null || fragment.getParentActivity() == null || callback == null) {
            return 0;
        }
        TL_messages_editMessage req = new TL_messages_editMessage();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) messageObject.getDialogId());
        req.message = message;
        req.flags |= 2048;
        req.id = messageObject.getId();
        if (!searchLinks) {
            z = true;
        }
        req.no_webpage = z;
        if (entities != null) {
            req.entities = entities;
            req.flags |= 8;
        }
        return ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SendMessagesHelper$$Lambda$4(this, fragment, req, callback));
    }

    final /* synthetic */ void lambda$editMessage$11$SendMessagesHelper(BaseFragment fragment, TL_messages_editMessage req, Runnable callback, TLObject response, TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) response, false);
        } else {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$56(this, error, fragment, req));
        }
        AndroidUtilities.runOnUIThread(callback);
    }

    final /* synthetic */ void lambda$null$10$SendMessagesHelper(TL_error error, BaseFragment fragment, TL_messages_editMessage req) {
        AlertsCreator.processError(this.currentAccount, error, fragment, req, new Object[0]);
    }

    private void sendLocation(Location location) {
        MessageMedia mediaGeo = new TL_messageMediaGeo();
        mediaGeo.geo = new TL_geoPoint();
        mediaGeo.geo.lat = AndroidUtilities.fixLocationCoord(location.getLatitude());
        mediaGeo.geo._long = AndroidUtilities.fixLocationCoord(location.getLongitude());
        for (Entry<String, MessageObject> entry : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) entry.getValue();
            sendMessage(mediaGeo, messageObject.getDialogId(), messageObject, null, null);
        }
    }

    public void sendCurrentLocation(MessageObject messageObject, KeyboardButton button) {
        if (messageObject != null && button != null) {
            this.waitingForLocation.put(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + (button instanceof TL_keyboardButtonGame ? "1" : "0"), messageObject);
            this.locationProvider.start();
        }
    }

    public boolean isSendingCurrentLocation(MessageObject messageObject, KeyboardButton button) {
        if (messageObject == null || button == null) {
            return false;
        }
        return this.waitingForLocation.containsKey(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + (button instanceof TL_keyboardButtonGame ? "1" : "0"));
    }

    public void sendNotificationCallback(long dialogId, int msgId, byte[] data) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$5(this, dialogId, msgId, data));
    }

    final /* synthetic */ void lambda$sendNotificationCallback$14$SendMessagesHelper(long dialogId, int msgId, byte[] data) {
        int lowerId = (int) dialogId;
        String key = dialogId + "_" + msgId + "_" + Utilities.bytesToHex(data) + "_" + 0;
        this.waitingForCallback.put(key, Boolean.valueOf(true));
        if (lowerId > 0) {
            if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lowerId)) == null) {
                User user = MessagesStorage.getInstance(this.currentAccount).getUserSync(lowerId);
                if (user != null) {
                    MessagesController.getInstance(this.currentAccount).putUser(user, true);
                }
            }
        } else if (MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lowerId)) == null) {
            Chat chat = MessagesStorage.getInstance(this.currentAccount).getChatSync(-lowerId);
            if (chat != null) {
                MessagesController.getInstance(this.currentAccount).putChat(chat, true);
            }
        }
        TL_messages_getBotCallbackAnswer req = new TL_messages_getBotCallbackAnswer();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(lowerId);
        req.msg_id = msgId;
        req.game = false;
        if (data != null) {
            req.flags |= 1;
            req.data = data;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SendMessagesHelper$$Lambda$54(this, key), 2);
        MessagesController.getInstance(this.currentAccount).markDialogAsRead(dialogId, msgId, msgId, 0, false, 0, true);
    }

    final /* synthetic */ void lambda$null$12$SendMessagesHelper(String key) {
        Boolean bool = (Boolean) this.waitingForCallback.remove(key);
    }

    final /* synthetic */ void lambda$null$13$SendMessagesHelper(String key, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$55(this, key));
    }

    public byte[] isSendingVote(MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        return (byte[]) this.waitingForVote.get("poll_" + messageObject.getPollId());
    }

    public int sendVote(MessageObject messageObject, TL_pollAnswer answer, Runnable finishRunnable) {
        if (messageObject == null) {
            return 0;
        }
        String key = "poll_" + messageObject.getPollId();
        if (this.waitingForCallback.containsKey(key)) {
            return 0;
        }
        this.waitingForVote.put(key, answer != null ? answer.option : new byte[0]);
        TL_messages_sendVote req = new TL_messages_sendVote();
        req.msg_id = messageObject.getId();
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) messageObject.getDialogId());
        if (answer != null) {
            req.options.add(answer.option);
        }
        return ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SendMessagesHelper$$Lambda$6(this, messageObject, key, finishRunnable));
    }

    final /* synthetic */ void lambda$sendVote$15$SendMessagesHelper(MessageObject messageObject, final String key, final Runnable finishRunnable, TLObject response, TL_error error) {
        if (error == null) {
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(0));
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) response, false);
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(SystemClock.uptimeMillis()));
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SendMessagesHelper.this.waitingForVote.remove(key);
                if (finishRunnable != null) {
                    finishRunnable.run();
                }
            }
        });
    }

    protected long getVoteSendTime(long pollId) {
        return ((Long) this.voteSendTime.get(pollId, Long.valueOf(0))).longValue();
    }

    public void sendCallback(boolean cache, MessageObject messageObject, KeyboardButton button, ChatActivity parentFragment) {
        if (messageObject != null && button != null && parentFragment != null) {
            boolean cacheFinal;
            int type;
            if (button instanceof TL_keyboardButtonGame) {
                cacheFinal = false;
                type = 1;
            } else {
                cacheFinal = cache;
                if (button instanceof TL_keyboardButtonBuy) {
                    type = 2;
                } else {
                    type = 0;
                }
            }
            String key = messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + type;
            this.waitingForCallback.put(key, Boolean.valueOf(true));
            RequestDelegate requestDelegate = new SendMessagesHelper$$Lambda$7(this, key, cacheFinal, messageObject, button, parentFragment);
            if (cacheFinal) {
                MessagesStorage.getInstance(this.currentAccount).getBotCache(key, requestDelegate);
            } else if (!(button instanceof TL_keyboardButtonBuy)) {
                TL_messages_getBotCallbackAnswer req = new TL_messages_getBotCallbackAnswer();
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) messageObject.getDialogId());
                req.msg_id = messageObject.getId();
                req.game = button instanceof TL_keyboardButtonGame;
                if (button.data != null) {
                    req.flags |= 1;
                    req.data = button.data;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 2);
            } else if ((messageObject.messageOwner.media.flags & 4) == 0) {
                TL_payments_getPaymentForm req2 = new TL_payments_getPaymentForm();
                req2.msg_id = messageObject.getId();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, requestDelegate, 2);
            } else {
                TL_payments_getPaymentReceipt req3 = new TL_payments_getPaymentReceipt();
                req3.msg_id = messageObject.messageOwner.media.receipt_msg_id;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req3, requestDelegate, 2);
            }
        }
    }

    final /* synthetic */ void lambda$sendCallback$17$SendMessagesHelper(String key, boolean cacheFinal, MessageObject messageObject, KeyboardButton button, ChatActivity parentFragment, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$53(this, key, cacheFinal, response, messageObject, button, parentFragment));
    }

    final /* synthetic */ void lambda$null$16$SendMessagesHelper(String key, boolean cacheFinal, TLObject response, MessageObject messageObject, KeyboardButton button, ChatActivity parentFragment) {
        this.waitingForCallback.remove(key);
        if (cacheFinal && response == null) {
            sendCallback(false, messageObject, button, parentFragment);
        } else if (response == null) {
        } else {
            if (!(button instanceof TL_keyboardButtonBuy)) {
                TL_messages_botCallbackAnswer res = (TL_messages_botCallbackAnswer) response;
                if (!(cacheFinal || res.cache_time == 0)) {
                    MessagesStorage.getInstance(this.currentAccount).saveBotCache(key, res);
                }
                int uid;
                User user;
                if (res.message != null) {
                    uid = messageObject.messageOwner.from_id;
                    if (messageObject.messageOwner.via_bot_id != 0) {
                        uid = messageObject.messageOwner.via_bot_id;
                    }
                    String name = null;
                    if (uid > 0) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
                        if (user != null) {
                            name = ContactsController.formatName(user.first_name, user.last_name);
                        }
                    } else {
                        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-uid));
                        if (chat != null) {
                            name = chat.title;
                        }
                    }
                    if (name == null) {
                        name = "bot";
                    }
                    if (!res.alert) {
                        parentFragment.showAlert(name, res.message);
                    } else if (parentFragment.getParentActivity() != null) {
                        Builder builder = new Builder(parentFragment.getParentActivity());
                        builder.setTitle(name);
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        builder.setMessage(res.message);
                        parentFragment.showDialog(builder.create());
                    }
                } else if (res.url != null && parentFragment.getParentActivity() != null) {
                    uid = messageObject.messageOwner.from_id;
                    if (messageObject.messageOwner.via_bot_id != 0) {
                        uid = messageObject.messageOwner.via_bot_id;
                    }
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid));
                    boolean verified = user != null && user.verified;
                    if (button instanceof TL_keyboardButtonGame) {
                        TL_game game = messageObject.messageOwner.media instanceof TL_messageMediaGame ? messageObject.messageOwner.media.game : null;
                        if (game != null) {
                            boolean z;
                            String str = res.url;
                            if (verified || !MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("askgame_" + uid, true)) {
                                z = false;
                            } else {
                                z = true;
                            }
                            parentFragment.showOpenGameAlert(game, messageObject, str, z, uid);
                            return;
                        }
                        return;
                    }
                    parentFragment.showOpenUrlAlert(res.url, false);
                }
            } else if (response instanceof TL_payments_paymentForm) {
                TL_payments_paymentForm form = (TL_payments_paymentForm) response;
                MessagesController.getInstance(this.currentAccount).putUsers(form.users, false);
                parentFragment.presentFragment(new PaymentFormActivity(form, messageObject));
            } else if (response instanceof TL_payments_paymentReceipt) {
                parentFragment.presentFragment(new PaymentFormActivity(messageObject, (TL_payments_paymentReceipt) response));
            }
        }
    }

    public boolean isSendingCallback(MessageObject messageObject, KeyboardButton button) {
        if (messageObject == null || button == null) {
            return false;
        }
        int type;
        if (button instanceof TL_keyboardButtonGame) {
            type = 1;
        } else if (button instanceof TL_keyboardButtonBuy) {
            type = 2;
        } else {
            type = 0;
        }
        return this.waitingForCallback.containsKey(messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + type);
    }

    public void sendGame(InputPeer peer, TL_inputMediaGame game, long random_id, long taskId) {
        Throwable e;
        long newTaskId;
        if (peer != null && game != null) {
            TL_messages_sendMedia request = new TL_messages_sendMedia();
            request.peer = peer;
            if (request.peer instanceof TL_inputPeerChannel) {
                request.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + peer.channel_id, false);
            }
            request.random_id = random_id != 0 ? random_id : getNextRandomId();
            request.message = "";
            request.media = game;
            if (taskId == 0) {
                NativeByteBuffer data = null;
                try {
                    NativeByteBuffer data2 = new NativeByteBuffer(((peer.getObjectSize() + game.getObjectSize()) + 4) + 8);
                    try {
                        data2.writeInt32(3);
                        data2.writeInt64(random_id);
                        peer.serializeToStream(data2);
                        game.serializeToStream(data2);
                        data = data2;
                    } catch (Exception e2) {
                        e = e2;
                        data = data2;
                        FileLog.e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new SendMessagesHelper$$Lambda$8(this, newTaskId));
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e(e);
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new SendMessagesHelper$$Lambda$8(this, newTaskId));
                }
                newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
            } else {
                newTaskId = taskId;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new SendMessagesHelper$$Lambda$8(this, newTaskId));
        }
    }

    final /* synthetic */ void lambda$sendGame$18$SendMessagesHelper(long newTaskId, TLObject response, TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) response, false);
        }
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
    }

    public void sendMessage(MessageObject retryMessageObject) {
        sendMessage(null, null, null, null, null, null, null, null, null, retryMessageObject.getDialogId(), retryMessageObject.messageOwner.attachPath, null, null, true, retryMessageObject, null, retryMessageObject.messageOwner.reply_markup, retryMessageObject.messageOwner.params, 0, null);
    }

    public void sendMessage(User user, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, null, user, null, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params, 0, null);
    }

    public void sendMessage(TL_document document, VideoEditedInfo videoEditedInfo, String path, long peer, MessageObject reply_to_msg, String caption, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl, Object parentObject) {
        sendMessage(null, caption, null, null, videoEditedInfo, null, document, null, null, peer, path, reply_to_msg, null, true, null, entities, replyMarkup, params, ttl, parentObject);
    }

    public void sendMessage(String message, long peer, MessageObject reply_to_msg, WebPage webPage, boolean searchLinks, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(message, null, null, null, null, null, null, null, null, peer, null, reply_to_msg, webPage, searchLinks, null, entities, replyMarkup, params, 0, null);
    }

    public void sendMessage(MessageMedia location, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, location, null, null, null, null, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params, 0, null);
    }

    public void sendMessage(TL_messageMediaPoll poll, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, null, null, null, null, poll, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params, 0, null);
    }

    public void sendMessage(TL_game game, long peer, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, null, null, null, game, null, peer, null, null, null, true, null, null, replyMarkup, params, 0, null);
    }

    public void sendMessage(TL_photo photo, String path, long peer, MessageObject reply_to_msg, String caption, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl, Object parentObject) {
        sendMessage(null, caption, null, photo, null, null, null, null, null, peer, path, reply_to_msg, null, true, null, entities, replyMarkup, params, ttl, parentObject);
    }

    /* JADX WARNING: Removed duplicated region for block: B:197:0x04fc A:{Catch:{ Exception -> 0x021a }} */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x065f A:{Catch:{ Exception -> 0x021a }} */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x06a6 A:{Catch:{ Exception -> 0x021a }} */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x065f A:{Catch:{ Exception -> 0x021a }} */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x0678 A:{Catch:{ Exception -> 0x021a }} */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x06a6 A:{Catch:{ Exception -> 0x021a }} */
    /* JADX WARNING: Removed duplicated region for block: B:786:0x1601 A:{Catch:{ Exception -> 0x0e36 }} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x022f  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x022f  */
    private void sendMessage(java.lang.String r83, java.lang.String r84, org.telegram.tgnet.TLRPC.MessageMedia r85, org.telegram.tgnet.TLRPC.TL_photo r86, org.telegram.messenger.VideoEditedInfo r87, org.telegram.tgnet.TLRPC.User r88, org.telegram.tgnet.TLRPC.TL_document r89, org.telegram.tgnet.TLRPC.TL_game r90, org.telegram.tgnet.TLRPC.TL_messageMediaPoll r91, long r92, java.lang.String r94, org.telegram.messenger.MessageObject r95, org.telegram.tgnet.TLRPC.WebPage r96, boolean r97, org.telegram.messenger.MessageObject r98, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r99, org.telegram.tgnet.TLRPC.ReplyMarkup r100, java.util.HashMap<java.lang.String, java.lang.String> r101, int r102, java.lang.Object r103) {
        /*
        r82 = this;
        if (r88 == 0) goto L_0x0009;
    L_0x0002:
        r0 = r88;
        r4 = r0.phone;
        if (r4 != 0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r8 = 0;
        r4 = (r92 > r8 ? 1 : (r92 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x0008;
    L_0x000f:
        if (r83 != 0) goto L_0x0016;
    L_0x0011:
        if (r84 != 0) goto L_0x0016;
    L_0x0013:
        r84 = "";
    L_0x0016:
        r16 = 0;
        if (r101 == 0) goto L_0x0030;
    L_0x001a:
        r4 = "originalPath";
        r0 = r101;
        r4 = r0.containsKey(r4);
        if (r4 == 0) goto L_0x0030;
    L_0x0025:
        r4 = "originalPath";
        r0 = r101;
        r16 = r0.get(r4);
        r16 = (java.lang.String) r16;
    L_0x0030:
        r57 = 0;
        r59 = 0;
        r10 = 0;
        r75 = -1;
        r0 = r92;
        r0 = (int) r0;
        r53 = r0;
        r4 = 32;
        r8 = r92 >> r4;
        r0 = (int) r8;
        r44 = r0;
        r49 = 0;
        r20 = 0;
        if (r53 == 0) goto L_0x00ab;
    L_0x0049:
        r0 = r82;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r0 = r53;
        r68 = r4.getInputPeer(r0);
    L_0x0057:
        r69 = 0;
        if (r53 != 0) goto L_0x00ae;
    L_0x005b:
        r0 = r82;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r6 = java.lang.Integer.valueOf(r44);
        r20 = r4.getEncryptedChat(r6);
        if (r20 != 0) goto L_0x00d2;
    L_0x006d:
        if (r98 == 0) goto L_0x0008;
    L_0x006f:
        r0 = r82;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r98;
        r6 = r0.messageOwner;
        r4.markMessageAsSendError(r6);
        r0 = r98;
        r4 = r0.messageOwner;
        r6 = 2;
        r4.send_state = r6;
        r0 = r82;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r6 = org.telegram.messenger.NotificationCenter.messageSendError;
        r8 = 1;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r11 = r98.getId();
        r11 = java.lang.Integer.valueOf(r11);
        r8[r9] = r11;
        r4.postNotificationName(r6, r8);
        r4 = r98.getId();
        r0 = r82;
        r0.processSentMessage(r4);
        goto L_0x0008;
    L_0x00ab:
        r68 = 0;
        goto L_0x0057;
    L_0x00ae:
        r0 = r68;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r4 == 0) goto L_0x00d2;
    L_0x00b4:
        r0 = r82;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r0 = r68;
        r6 = r0.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r35 = r4.getChat(r6);
        if (r35 == 0) goto L_0x025a;
    L_0x00ca:
        r0 = r35;
        r4 = r0.megagroup;
        if (r4 != 0) goto L_0x025a;
    L_0x00d0:
        r49 = 1;
    L_0x00d2:
        if (r98 == 0) goto L_0x037a;
    L_0x00d4:
        r0 = r98;
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x021a }
        r57 = r0;
        r4 = r98.isForwarded();	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x025e;
    L_0x00e0:
        r75 = 4;
    L_0x00e2:
        r0 = r57;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x021a }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x00f4;
    L_0x00ec:
        r8 = r82.getNextRandomId();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.random_id = r8;	 Catch:{ Exception -> 0x021a }
    L_0x00f4:
        if (r101 == 0) goto L_0x0129;
    L_0x00f6:
        r4 = "bot";
        r0 = r101;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0129;
    L_0x0101:
        if (r20 == 0) goto L_0x078f;
    L_0x0103:
        r4 = "bot_name";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x021a }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.via_bot_name = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.via_bot_name;	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x011f;
    L_0x0118:
        r4 = "";
        r0 = r57;
        r0.via_bot_name = r4;	 Catch:{ Exception -> 0x021a }
    L_0x011f:
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x021a }
        r4 = r4 | 2048;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x021a }
    L_0x0129:
        r0 = r101;
        r1 = r57;
        r1.params = r0;	 Catch:{ Exception -> 0x021a }
        if (r98 == 0) goto L_0x0137;
    L_0x0131:
        r0 = r98;
        r4 = r0.resendAsIs;	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x018a;
    L_0x0137:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r4 = r4.getCurrentTime();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.date = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r68;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x07c5;
    L_0x014d:
        if (r49 == 0) goto L_0x015e;
    L_0x014f:
        r4 = 1;
        r0 = r57;
        r0.views = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x021a }
        r4 = r4 | 1024;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x021a }
    L_0x015e:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r0 = r68;
        r6 = r0.channel_id;	 Catch:{ Exception -> 0x021a }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x021a }
        r35 = r4.getChat(r6);	 Catch:{ Exception -> 0x021a }
        if (r35 == 0) goto L_0x018a;
    L_0x0174:
        r0 = r35;
        r4 = r0.megagroup;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x07a8;
    L_0x017a:
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x021a }
        r6 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = r4 | r6;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x021a }
        r4 = 1;
        r0 = r57;
        r0.unread = r4;	 Catch:{ Exception -> 0x021a }
    L_0x018a:
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x021a }
        r4 = r4 | 512;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r92;
        r2 = r57;
        r2.dialog_id = r0;	 Catch:{ Exception -> 0x021a }
        if (r95 == 0) goto L_0x01c6;
    L_0x019c:
        if (r20 == 0) goto L_0x07cc;
    L_0x019e:
        r0 = r95;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x021a }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x021a }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 == 0) goto L_0x07cc;
    L_0x01aa:
        r0 = r95;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x021a }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.reply_to_random_id = r8;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x021a }
        r4 = r4 | 8;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x021a }
    L_0x01be:
        r4 = r95.getId();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x021a }
    L_0x01c6:
        if (r100 == 0) goto L_0x01da;
    L_0x01c8:
        if (r20 != 0) goto L_0x01da;
    L_0x01ca:
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x021a }
        r4 = r4 | 64;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r100;
        r1 = r57;
        r1.reply_markup = r0;	 Catch:{ Exception -> 0x021a }
    L_0x01da:
        if (r53 == 0) goto L_0x09d6;
    L_0x01dc:
        r4 = 1;
        r0 = r44;
        if (r0 != r4) goto L_0x0998;
    L_0x01e1:
        r0 = r82;
        r4 = r0.currentChatInfo;	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x07d8;
    L_0x01e7:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4.markMessageAsSendError(r0);	 Catch:{ Exception -> 0x021a }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r6 = org.telegram.messenger.NotificationCenter.messageSendError;	 Catch:{ Exception -> 0x021a }
        r8 = 1;
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x021a }
        r9 = 0;
        r0 = r57;
        r11 = r0.id;	 Catch:{ Exception -> 0x021a }
        r11 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x021a }
        r8[r9] = r11;	 Catch:{ Exception -> 0x021a }
        r4.postNotificationName(r6, r8);	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.id;	 Catch:{ Exception -> 0x021a }
        r0 = r82;
        r0.processSentMessage(r4);	 Catch:{ Exception -> 0x021a }
        goto L_0x0008;
    L_0x021a:
        r39 = move-exception;
        r12 = r59;
    L_0x021d:
        org.telegram.messenger.FileLog.e(r39);
        r0 = r82;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r4.markMessageAsSendError(r0);
        if (r12 == 0) goto L_0x0234;
    L_0x022f:
        r4 = r12.messageOwner;
        r6 = 2;
        r4.send_state = r6;
    L_0x0234:
        r0 = r82;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r6 = org.telegram.messenger.NotificationCenter.messageSendError;
        r8 = 1;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r57;
        r11 = r0.id;
        r11 = java.lang.Integer.valueOf(r11);
        r8[r9] = r11;
        r4.postNotificationName(r6, r8);
        r0 = r57;
        r4 = r0.id;
        r0 = r82;
        r0.processSentMessage(r4);
        goto L_0x0008;
    L_0x025a:
        r49 = 0;
        goto L_0x00d2;
    L_0x025e:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x0298;
    L_0x0264:
        r0 = r98;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x021a }
        r4 = r4.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0291;
    L_0x026e:
        r75 = 0;
    L_0x0270:
        if (r101 == 0) goto L_0x027f;
    L_0x0272:
        r4 = "query_id";
        r0 = r101;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x027f;
    L_0x027d:
        r75 = 9;
    L_0x027f:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.ttl_seconds;	 Catch:{ Exception -> 0x021a }
        if (r4 <= 0) goto L_0x00e2;
    L_0x0287:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r4.ttl_seconds;	 Catch:{ Exception -> 0x021a }
        r102 = r0;
        goto L_0x00e2;
    L_0x0291:
        r0 = r57;
        r0 = r0.message;	 Catch:{ Exception -> 0x021a }
        r83 = r0;
        goto L_0x026e;
    L_0x0298:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 4;
        if (r4 != r6) goto L_0x02a8;
    L_0x029f:
        r0 = r57;
        r0 = r0.media;	 Catch:{ Exception -> 0x021a }
        r85 = r0;
        r75 = 1;
        goto L_0x0270;
    L_0x02a8:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 1;
        if (r4 != r6) goto L_0x02bd;
    L_0x02af:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.photo;	 Catch:{ Exception -> 0x021a }
        r0 = r4;
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;	 Catch:{ Exception -> 0x021a }
        r86 = r0;
        r75 = 2;
        goto L_0x0270;
    L_0x02bd:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 3;
        if (r4 == r6) goto L_0x02cd;
    L_0x02c4:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 5;
        if (r4 == r6) goto L_0x02cd;
    L_0x02cb:
        if (r87 == 0) goto L_0x02db;
    L_0x02cd:
        r75 = 3;
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.document;	 Catch:{ Exception -> 0x021a }
        r0 = r4;
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x021a }
        r89 = r0;
        goto L_0x0270;
    L_0x02db:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 12;
        if (r4 != r6) goto L_0x0320;
    L_0x02e3:
        r79 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2;	 Catch:{ Exception -> 0x021a }
        r79.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x1ba4 }
        r4 = r4.phone_number;	 Catch:{ Exception -> 0x1ba4 }
        r0 = r79;
        r0.phone = r4;	 Catch:{ Exception -> 0x1ba4 }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x1ba4 }
        r4 = r4.first_name;	 Catch:{ Exception -> 0x1ba4 }
        r0 = r79;
        r0.first_name = r4;	 Catch:{ Exception -> 0x1ba4 }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x1ba4 }
        r4 = r4.last_name;	 Catch:{ Exception -> 0x1ba4 }
        r0 = r79;
        r0.last_name = r4;	 Catch:{ Exception -> 0x1ba4 }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x1ba4 }
        r4 = r4.vcard;	 Catch:{ Exception -> 0x1ba4 }
        r0 = r79;
        r0.restriction_reason = r4;	 Catch:{ Exception -> 0x1ba4 }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x1ba4 }
        r4 = r4.user_id;	 Catch:{ Exception -> 0x1ba4 }
        r0 = r79;
        r0.id = r4;	 Catch:{ Exception -> 0x1ba4 }
        r75 = 6;
        r88 = r79;
        goto L_0x0270;
    L_0x0320:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 8;
        if (r4 == r6) goto L_0x0340;
    L_0x0328:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 9;
        if (r4 == r6) goto L_0x0340;
    L_0x0330:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 13;
        if (r4 == r6) goto L_0x0340;
    L_0x0338:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 14;
        if (r4 != r6) goto L_0x034f;
    L_0x0340:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.document;	 Catch:{ Exception -> 0x021a }
        r0 = r4;
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x021a }
        r89 = r0;
        r75 = 7;
        goto L_0x0270;
    L_0x034f:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 2;
        if (r4 != r6) goto L_0x0365;
    L_0x0356:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.document;	 Catch:{ Exception -> 0x021a }
        r0 = r4;
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x021a }
        r89 = r0;
        r75 = 8;
        goto L_0x0270;
    L_0x0365:
        r0 = r98;
        r4 = r0.type;	 Catch:{ Exception -> 0x021a }
        r6 = 17;
        if (r4 != r6) goto L_0x0270;
    L_0x036d:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r4;
        r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0;	 Catch:{ Exception -> 0x021a }
        r91 = r0;
        r75 = 10;
        goto L_0x0270;
    L_0x037a:
        if (r83 == 0) goto L_0x0447;
    L_0x037c:
        if (r20 == 0) goto L_0x0423;
    L_0x037e:
        r58 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
    L_0x0385:
        if (r20 == 0) goto L_0x03a2;
    L_0x0387:
        r0 = r96;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x03a2;
    L_0x038d:
        r0 = r96;
        r4 = r0.url;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x042c;
    L_0x0393:
        r60 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending;	 Catch:{ Exception -> 0x021a }
        r60.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r96;
        r4 = r0.url;	 Catch:{ Exception -> 0x021a }
        r0 = r60;
        r0.url = r4;	 Catch:{ Exception -> 0x021a }
        r96 = r60;
    L_0x03a2:
        if (r96 != 0) goto L_0x0430;
    L_0x03a4:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Exception -> 0x021a }
        r4.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.media = r4;	 Catch:{ Exception -> 0x021a }
    L_0x03ad:
        if (r101 == 0) goto L_0x0443;
    L_0x03af:
        r4 = "query_id";
        r0 = r101;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0443;
    L_0x03ba:
        r75 = 9;
    L_0x03bc:
        r0 = r83;
        r1 = r57;
        r1.message = r0;	 Catch:{ Exception -> 0x021a }
    L_0x03c2:
        if (r99 == 0) goto L_0x03da;
    L_0x03c4:
        r4 = r99.isEmpty();	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x03da;
    L_0x03ca:
        r0 = r99;
        r1 = r57;
        r1.entities = r0;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x021a }
        r4 = r4 | 128;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x021a }
    L_0x03da:
        if (r84 == 0) goto L_0x0764;
    L_0x03dc:
        r0 = r84;
        r1 = r57;
        r1.message = r0;	 Catch:{ Exception -> 0x021a }
    L_0x03e2:
        r0 = r57;
        r4 = r0.attachPath;	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x03ef;
    L_0x03e8:
        r4 = "";
        r0 = r57;
        r0.attachPath = r4;	 Catch:{ Exception -> 0x021a }
    L_0x03ef:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r4 = r4.getNewMessageId();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.id = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.local_id = r4;	 Catch:{ Exception -> 0x021a }
        r4 = 1;
        r0 = r57;
        r0.out = r4;	 Catch:{ Exception -> 0x021a }
        if (r49 == 0) goto L_0x0773;
    L_0x040a:
        if (r68 == 0) goto L_0x0773;
    L_0x040c:
        r0 = r68;
        r4 = r0.channel_id;	 Catch:{ Exception -> 0x021a }
        r4 = -r4;
        r0 = r57;
        r0.from_id = r4;	 Catch:{ Exception -> 0x021a }
    L_0x0415:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r6 = 0;
        r4.saveConfig(r6);	 Catch:{ Exception -> 0x021a }
        goto L_0x00e2;
    L_0x0423:
        r58 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
        goto L_0x0385;
    L_0x042c:
        r96 = 0;
        goto L_0x03a2;
    L_0x0430:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;	 Catch:{ Exception -> 0x021a }
        r4.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.media = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r96;
        r4.webpage = r0;	 Catch:{ Exception -> 0x021a }
        goto L_0x03ad;
    L_0x0443:
        r75 = 0;
        goto L_0x03bc;
    L_0x0447:
        if (r91 == 0) goto L_0x0464;
    L_0x0449:
        if (r20 == 0) goto L_0x045c;
    L_0x044b:
        r58 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
    L_0x0452:
        r0 = r91;
        r1 = r57;
        r1.media = r0;	 Catch:{ Exception -> 0x021a }
        r75 = 10;
        goto L_0x03c2;
    L_0x045c:
        r58 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
        goto L_0x0452;
    L_0x0464:
        if (r85 == 0) goto L_0x0492;
    L_0x0466:
        if (r20 == 0) goto L_0x0486;
    L_0x0468:
        r58 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
    L_0x046f:
        r0 = r85;
        r1 = r57;
        r1.media = r0;	 Catch:{ Exception -> 0x021a }
        if (r101 == 0) goto L_0x048e;
    L_0x0477:
        r4 = "query_id";
        r0 = r101;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x048e;
    L_0x0482:
        r75 = 9;
        goto L_0x03c2;
    L_0x0486:
        r58 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
        goto L_0x046f;
    L_0x048e:
        r75 = 1;
        goto L_0x03c2;
    L_0x0492:
        if (r86 == 0) goto L_0x0538;
    L_0x0494:
        if (r20 == 0) goto L_0x0504;
    L_0x0496:
        r58 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
    L_0x049d:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x021a }
        r4.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.media = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021a }
        r6 = r6 | 3;
        r4.flags = r6;	 Catch:{ Exception -> 0x021a }
        if (r99 == 0) goto L_0x04b8;
    L_0x04b2:
        r0 = r99;
        r1 = r57;
        r1.entities = r0;	 Catch:{ Exception -> 0x021a }
    L_0x04b8:
        if (r102 == 0) goto L_0x04d2;
    L_0x04ba:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r102;
        r4.ttl_seconds = r0;	 Catch:{ Exception -> 0x021a }
        r0 = r102;
        r1 = r57;
        r1.ttl = r0;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021a }
        r6 = r6 | 4;
        r4.flags = r6;	 Catch:{ Exception -> 0x021a }
    L_0x04d2:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r86;
        r4.photo = r0;	 Catch:{ Exception -> 0x021a }
        if (r101 == 0) goto L_0x050c;
    L_0x04dc:
        r4 = "query_id";
        r0 = r101;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x050c;
    L_0x04e7:
        r75 = 9;
    L_0x04e9:
        if (r94 == 0) goto L_0x050f;
    L_0x04eb:
        r4 = r94.length();	 Catch:{ Exception -> 0x021a }
        if (r4 <= 0) goto L_0x050f;
    L_0x04f1:
        r4 = "http";
        r0 = r94;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x050f;
    L_0x04fc:
        r0 = r94;
        r1 = r57;
        r1.attachPath = r0;	 Catch:{ Exception -> 0x021a }
        goto L_0x03c2;
    L_0x0504:
        r58 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
        goto L_0x049d;
    L_0x050c:
        r75 = 2;
        goto L_0x04e9;
    L_0x050f:
        r0 = r86;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x021a }
        r0 = r86;
        r6 = r0.sizes;	 Catch:{ Exception -> 0x021a }
        r6 = r6.size();	 Catch:{ Exception -> 0x021a }
        r6 = r6 + -1;
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x021a }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x021a }
        r0 = r4.location;	 Catch:{ Exception -> 0x021a }
        r52 = r0;
        r4 = 1;
        r0 = r52;
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r4);	 Catch:{ Exception -> 0x021a }
        r4 = r4.toString();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.attachPath = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x03c2;
    L_0x0538:
        if (r90 == 0) goto L_0x0563;
    L_0x053a:
        r58 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame;	 Catch:{ Exception -> 0x1bab }
        r4.<init>();	 Catch:{ Exception -> 0x1bab }
        r0 = r58;
        r0.media = r4;	 Catch:{ Exception -> 0x1bab }
        r0 = r58;
        r4 = r0.media;	 Catch:{ Exception -> 0x1bab }
        r0 = r90;
        r4.game = r0;	 Catch:{ Exception -> 0x1bab }
        if (r101 == 0) goto L_0x1bce;
    L_0x0552:
        r4 = "query_id";
        r0 = r101;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x1bab }
        if (r4 == 0) goto L_0x1bce;
    L_0x055d:
        r75 = 9;
        r57 = r58;
        goto L_0x03c2;
    L_0x0563:
        if (r88 == 0) goto L_0x060e;
    L_0x0565:
        if (r20 == 0) goto L_0x05f7;
    L_0x0567:
        r58 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
    L_0x056e:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact;	 Catch:{ Exception -> 0x021a }
        r4.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.media = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r88;
        r6 = r0.phone;	 Catch:{ Exception -> 0x021a }
        r4.phone_number = r6;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r88;
        r6 = r0.first_name;	 Catch:{ Exception -> 0x021a }
        r4.first_name = r6;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r88;
        r6 = r0.last_name;	 Catch:{ Exception -> 0x021a }
        r4.last_name = r6;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r88;
        r6 = r0.id;	 Catch:{ Exception -> 0x021a }
        r4.user_id = r6;	 Catch:{ Exception -> 0x021a }
        r0 = r88;
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0600;
    L_0x05a5:
        r0 = r88;
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x021a }
        r6 = "BEGIN:VCARD";
        r4 = r4.startsWith(r6);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0600;
    L_0x05b2:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r88;
        r6 = r0.restriction_reason;	 Catch:{ Exception -> 0x021a }
        r4.vcard = r6;	 Catch:{ Exception -> 0x021a }
    L_0x05bc:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.first_name;	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x05d1;
    L_0x05c4:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r6 = "";
        r4.first_name = r6;	 Catch:{ Exception -> 0x021a }
        r0 = r88;
        r0.first_name = r6;	 Catch:{ Exception -> 0x021a }
    L_0x05d1:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.last_name;	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x05e6;
    L_0x05d9:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r6 = "";
        r4.last_name = r6;	 Catch:{ Exception -> 0x021a }
        r0 = r88;
        r0.last_name = r6;	 Catch:{ Exception -> 0x021a }
    L_0x05e6:
        if (r101 == 0) goto L_0x060a;
    L_0x05e8:
        r4 = "query_id";
        r0 = r101;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x060a;
    L_0x05f3:
        r75 = 9;
        goto L_0x03c2;
    L_0x05f7:
        r58 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
        goto L_0x056e;
    L_0x0600:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r6 = "";
        r4.vcard = r6;	 Catch:{ Exception -> 0x021a }
        goto L_0x05bc;
    L_0x060a:
        r75 = 6;
        goto L_0x03c2;
    L_0x060e:
        if (r89 == 0) goto L_0x03c2;
    L_0x0610:
        if (r20 == 0) goto L_0x0706;
    L_0x0612:
        r58 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
    L_0x0619:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x021a }
        r4.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.media = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021a }
        r6 = r6 | 3;
        r4.flags = r6;	 Catch:{ Exception -> 0x021a }
        if (r102 == 0) goto L_0x0646;
    L_0x062e:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r102;
        r4.ttl_seconds = r0;	 Catch:{ Exception -> 0x021a }
        r0 = r102;
        r1 = r57;
        r1.ttl = r0;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021a }
        r6 = r6 | 4;
        r4.flags = r6;	 Catch:{ Exception -> 0x021a }
    L_0x0646:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r89;
        r4.document = r0;	 Catch:{ Exception -> 0x021a }
        if (r101 == 0) goto L_0x070f;
    L_0x0650:
        r4 = "query_id";
        r0 = r101;
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x070f;
    L_0x065b:
        r75 = 9;
    L_0x065d:
        if (r87 == 0) goto L_0x0676;
    L_0x065f:
        r80 = r87.getString();	 Catch:{ Exception -> 0x021a }
        if (r101 != 0) goto L_0x066c;
    L_0x0665:
        r62 = new java.util.HashMap;	 Catch:{ Exception -> 0x021a }
        r62.<init>();	 Catch:{ Exception -> 0x021a }
        r101 = r62;
    L_0x066c:
        r4 = "ve";
        r0 = r101;
        r1 = r80;
        r0.put(r4, r1);	 Catch:{ Exception -> 0x021a }
    L_0x0676:
        if (r20 == 0) goto L_0x072f;
    L_0x0678:
        r0 = r89;
        r4 = r0.dc_id;	 Catch:{ Exception -> 0x021a }
        if (r4 <= 0) goto L_0x072f;
    L_0x067e:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r89);	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x072f;
    L_0x0684:
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r89);	 Catch:{ Exception -> 0x021a }
        r4 = r4.toString();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.attachPath = r4;	 Catch:{ Exception -> 0x021a }
    L_0x0690:
        if (r20 == 0) goto L_0x03c2;
    L_0x0692:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r89);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x03c2;
    L_0x0698:
        r29 = 0;
    L_0x069a:
        r0 = r89;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x021a }
        r4 = r4.size();	 Catch:{ Exception -> 0x021a }
        r0 = r29;
        if (r0 >= r4) goto L_0x03c2;
    L_0x06a6:
        r0 = r89;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x021a }
        r0 = r29;
        r32 = r4.get(r0);	 Catch:{ Exception -> 0x021a }
        r32 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r32;	 Catch:{ Exception -> 0x021a }
        r0 = r32;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0760;
    L_0x06b8:
        r0 = r89;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x021a }
        r0 = r29;
        r4.remove(r0);	 Catch:{ Exception -> 0x021a }
        r33 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55;	 Catch:{ Exception -> 0x021a }
        r33.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r89;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x021a }
        r0 = r33;
        r4.add(r0);	 Catch:{ Exception -> 0x021a }
        r0 = r32;
        r4 = r0.alt;	 Catch:{ Exception -> 0x021a }
        r0 = r33;
        r0.alt = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r32;
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0755;
    L_0x06dd:
        r0 = r32;
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x021a }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0737;
    L_0x06e5:
        r0 = r32;
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x021a }
        r0 = r4.short_name;	 Catch:{ Exception -> 0x021a }
        r56 = r0;
    L_0x06ed:
        r4 = android.text.TextUtils.isEmpty(r56);	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x074a;
    L_0x06f3:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x021a }
        r4.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r33;
        r0.stickerset = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r33;
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x021a }
        r0 = r56;
        r4.short_name = r0;	 Catch:{ Exception -> 0x021a }
        goto L_0x03c2;
    L_0x0706:
        r58 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021a }
        r58.<init>();	 Catch:{ Exception -> 0x021a }
        r57 = r58;
        goto L_0x0619;
    L_0x070f:
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r89);	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x071d;
    L_0x0715:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r89);	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x071d;
    L_0x071b:
        if (r87 == 0) goto L_0x0721;
    L_0x071d:
        r75 = 3;
        goto L_0x065d;
    L_0x0721:
        r4 = org.telegram.messenger.MessageObject.isVoiceDocument(r89);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x072b;
    L_0x0727:
        r75 = 8;
        goto L_0x065d;
    L_0x072b:
        r75 = 7;
        goto L_0x065d;
    L_0x072f:
        r0 = r94;
        r1 = r57;
        r1.attachPath = r0;	 Catch:{ Exception -> 0x021a }
        goto L_0x0690;
    L_0x0737:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r0 = r32;
        r6 = r0.stickerset;	 Catch:{ Exception -> 0x021a }
        r8 = r6.id;	 Catch:{ Exception -> 0x021a }
        r56 = r4.getStickerSetName(r8);	 Catch:{ Exception -> 0x021a }
        goto L_0x06ed;
    L_0x074a:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x021a }
        r4.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r33;
        r0.stickerset = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x03c2;
    L_0x0755:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x021a }
        r4.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r33;
        r0.stickerset = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x03c2;
    L_0x0760:
        r29 = r29 + 1;
        goto L_0x069a;
    L_0x0764:
        r0 = r57;
        r4 = r0.message;	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x03e2;
    L_0x076a:
        r4 = "";
        r0 = r57;
        r0.message = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x03e2;
    L_0x0773:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r4 = r4.getClientUserId();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.from_id = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x021a }
        r4 = r4 | 256;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x0415;
    L_0x078f:
        r4 = "bot";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x021a }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Exception -> 0x021a }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.via_bot_id = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x011f;
    L_0x07a8:
        r4 = 1;
        r0 = r57;
        r0.post = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r35;
        r4 = r0.signatures;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x018a;
    L_0x07b3:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r4 = r4.getClientUserId();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.from_id = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x018a;
    L_0x07c5:
        r4 = 1;
        r0 = r57;
        r0.unread = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x018a;
    L_0x07cc:
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x021a }
        r4 = r4 | 8;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x01be;
    L_0x07d8:
        r70 = new java.util.ArrayList;	 Catch:{ Exception -> 0x021a }
        r70.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r82;
        r4 = r0.currentChatInfo;	 Catch:{ Exception -> 0x0821 }
        r4 = r4.participants;	 Catch:{ Exception -> 0x0821 }
        r4 = r4.participants;	 Catch:{ Exception -> 0x0821 }
        r4 = r4.iterator();	 Catch:{ Exception -> 0x0821 }
    L_0x07e9:
        r6 = r4.hasNext();	 Catch:{ Exception -> 0x0821 }
        if (r6 == 0) goto L_0x0828;
    L_0x07ef:
        r63 = r4.next();	 Catch:{ Exception -> 0x0821 }
        r63 = (org.telegram.tgnet.TLRPC.ChatParticipant) r63;	 Catch:{ Exception -> 0x0821 }
        r0 = r82;
        r6 = r0.currentAccount;	 Catch:{ Exception -> 0x0821 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x0821 }
        r0 = r63;
        r8 = r0.user_id;	 Catch:{ Exception -> 0x0821 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0821 }
        r71 = r6.getUser(r8);	 Catch:{ Exception -> 0x0821 }
        r0 = r82;
        r6 = r0.currentAccount;	 Catch:{ Exception -> 0x0821 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x0821 }
        r0 = r71;
        r64 = r6.getInputUser(r0);	 Catch:{ Exception -> 0x0821 }
        if (r64 == 0) goto L_0x07e9;
    L_0x0819:
        r0 = r70;
        r1 = r64;
        r0.add(r1);	 Catch:{ Exception -> 0x0821 }
        goto L_0x07e9;
    L_0x0821:
        r39 = move-exception;
        r69 = r70;
        r12 = r59;
        goto L_0x021d;
    L_0x0828:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Exception -> 0x0821 }
        r4.<init>();	 Catch:{ Exception -> 0x0821 }
        r0 = r57;
        r0.to_id = r4;	 Catch:{ Exception -> 0x0821 }
        r0 = r57;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x0821 }
        r0 = r53;
        r4.chat_id = r0;	 Catch:{ Exception -> 0x0821 }
        r69 = r70;
    L_0x083b:
        r4 = 1;
        r0 = r44;
        if (r0 == r4) goto L_0x0851;
    L_0x0840:
        r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r57);	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x084c;
    L_0x0846:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r57);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0851;
    L_0x084c:
        r4 = 1;
        r0 = r57;
        r0.media_unread = r4;	 Catch:{ Exception -> 0x021a }
    L_0x0851:
        r4 = 1;
        r0 = r57;
        r0.send_state = r4;	 Catch:{ Exception -> 0x021a }
        r12 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x021a }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r6 = 1;
        r0 = r57;
        r12.<init>(r4, r0, r6);	 Catch:{ Exception -> 0x021a }
        r0 = r95;
        r12.replyMessageObject = r0;	 Catch:{ Exception -> 0x0e36 }
        r4 = r12.isForwarded();	 Catch:{ Exception -> 0x0e36 }
        if (r4 != 0) goto L_0x0885;
    L_0x086c:
        r4 = r12.type;	 Catch:{ Exception -> 0x0e36 }
        r6 = 3;
        if (r4 == r6) goto L_0x0878;
    L_0x0871:
        if (r87 != 0) goto L_0x0878;
    L_0x0873:
        r4 = r12.type;	 Catch:{ Exception -> 0x0e36 }
        r6 = 2;
        if (r4 != r6) goto L_0x0885;
    L_0x0878:
        r0 = r57;
        r4 = r0.attachPath;	 Catch:{ Exception -> 0x0e36 }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Exception -> 0x0e36 }
        if (r4 != 0) goto L_0x0885;
    L_0x0882:
        r4 = 1;
        r12.attachPathExists = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x0885:
        r4 = r12.videoEditedInfo;	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x088f;
    L_0x0889:
        if (r87 != 0) goto L_0x088f;
    L_0x088b:
        r0 = r12.videoEditedInfo;	 Catch:{ Exception -> 0x0e36 }
        r87 = r0;
    L_0x088f:
        r42 = 0;
        r50 = 0;
        if (r101 == 0) goto L_0x08c8;
    L_0x0895:
        r4 = "groupId";
        r0 = r101;
        r41 = r0.get(r4);	 Catch:{ Exception -> 0x0e36 }
        r41 = (java.lang.String) r41;	 Catch:{ Exception -> 0x0e36 }
        if (r41 == 0) goto L_0x08bb;
    L_0x08a2:
        r4 = org.telegram.messenger.Utilities.parseLong(r41);	 Catch:{ Exception -> 0x0e36 }
        r42 = r4.longValue();	 Catch:{ Exception -> 0x0e36 }
        r0 = r42;
        r2 = r57;
        r2.grouped_id = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r57;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0e36 }
        r6 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r4 = r4 | r6;
        r0 = r57;
        r0.flags = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x08bb:
        r4 = "final";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x0ade;
    L_0x08c6:
        r50 = 1;
    L_0x08c8:
        r8 = 0;
        r4 = (r42 > r8 ? 1 : (r42 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0ae2;
    L_0x08ce:
        r61 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0e36 }
        r61.<init>();	 Catch:{ Exception -> 0x0e36 }
        r0 = r61;
        r0.add(r12);	 Catch:{ Exception -> 0x0e36 }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0e36 }
        r5.<init>();	 Catch:{ Exception -> 0x0e36 }
        r0 = r57;
        r5.add(r0);	 Catch:{ Exception -> 0x0e36 }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0e36 }
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);	 Catch:{ Exception -> 0x0e36 }
        r6 = 0;
        r7 = 1;
        r8 = 0;
        r9 = 0;
        r4.putMessages(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x0e36 }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0e36 }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x0e36 }
        r0 = r92;
        r2 = r61;
        r4.updateInterfaceWithMessages(r0, r2);	 Catch:{ Exception -> 0x0e36 }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0e36 }
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);	 Catch:{ Exception -> 0x0e36 }
        r6 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;	 Catch:{ Exception -> 0x0e36 }
        r8 = 0;
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0e36 }
        r4.postNotificationName(r6, r8);	 Catch:{ Exception -> 0x0e36 }
        r37 = r10;
    L_0x0912:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0960;
    L_0x0916:
        if (r68 == 0) goto L_0x0960;
    L_0x0918:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r6 = "send message user_id = ";
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r68;
        r6 = r0.user_id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0ca9 }
        r6 = " chat_id = ";
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r68;
        r6 = r0.chat_id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0ca9 }
        r6 = " channel_id = ";
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r68;
        r6 = r0.channel_id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0ca9 }
        r6 = " access_hash = ";
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r68;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.append(r8);	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0ca9 }
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x0ca9 }
    L_0x0960:
        r65 = 0;
        if (r75 == 0) goto L_0x096e;
    L_0x0964:
        r4 = 9;
        r0 = r75;
        if (r0 != r4) goto L_0x0cae;
    L_0x096a:
        if (r83 == 0) goto L_0x0cae;
    L_0x096c:
        if (r20 == 0) goto L_0x0cae;
    L_0x096e:
        if (r20 != 0) goto L_0x0bef;
    L_0x0970:
        if (r69 == 0) goto L_0x0b60;
    L_0x0972:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x0ca9 }
        r7.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r66 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0ca9 }
        r66.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r29 = 0;
    L_0x097e:
        r4 = r69.size();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r29;
        if (r0 >= r4) goto L_0x0b3f;
    L_0x0986:
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0ca9 }
        r8 = r4.nextLong();	 Catch:{ Exception -> 0x0ca9 }
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r66;
        r0.add(r4);	 Catch:{ Exception -> 0x0ca9 }
        r29 = r29 + 1;
        goto L_0x097e;
    L_0x0998:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r0 = r53;
        r4 = r4.getPeer(r0);	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.to_id = r4;	 Catch:{ Exception -> 0x021a }
        if (r53 <= 0) goto L_0x083b;
    L_0x09ac:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x021a }
        r6 = java.lang.Integer.valueOf(r53);	 Catch:{ Exception -> 0x021a }
        r71 = r4.getUser(r6);	 Catch:{ Exception -> 0x021a }
        if (r71 != 0) goto L_0x09c9;
    L_0x09be:
        r0 = r57;
        r4 = r0.id;	 Catch:{ Exception -> 0x021a }
        r0 = r82;
        r0.processSentMessage(r4);	 Catch:{ Exception -> 0x021a }
        goto L_0x0008;
    L_0x09c9:
        r0 = r71;
        r4 = r0.bot;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x083b;
    L_0x09cf:
        r4 = 0;
        r0 = r57;
        r0.unread = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x083b;
    L_0x09d6:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Exception -> 0x021a }
        r4.<init>();	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.to_id = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r20;
        r4 = r0.participant_id;	 Catch:{ Exception -> 0x021a }
        r0 = r82;
        r6 = r0.currentAccount;	 Catch:{ Exception -> 0x021a }
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);	 Catch:{ Exception -> 0x021a }
        r6 = r6.getClientUserId();	 Catch:{ Exception -> 0x021a }
        if (r4 != r6) goto L_0x0a57;
    L_0x09f1:
        r0 = r57;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x021a }
        r0 = r20;
        r6 = r0.admin_id;	 Catch:{ Exception -> 0x021a }
        r4.user_id = r6;	 Catch:{ Exception -> 0x021a }
    L_0x09fb:
        if (r102 == 0) goto L_0x0a62;
    L_0x09fd:
        r0 = r102;
        r1 = r57;
        r1.ttl = r0;	 Catch:{ Exception -> 0x021a }
    L_0x0a03:
        r0 = r57;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x083b;
    L_0x0a09:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.document;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x083b;
    L_0x0a11:
        r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r57);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0a8f;
    L_0x0a17:
        r38 = 0;
        r29 = 0;
    L_0x0a1b:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.document;	 Catch:{ Exception -> 0x021a }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x021a }
        r4 = r4.size();	 Catch:{ Exception -> 0x021a }
        r0 = r29;
        if (r0 >= r4) goto L_0x0a47;
    L_0x0a2b:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.document;	 Catch:{ Exception -> 0x021a }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x021a }
        r0 = r29;
        r32 = r4.get(r0);	 Catch:{ Exception -> 0x021a }
        r32 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r32;	 Catch:{ Exception -> 0x021a }
        r0 = r32;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0a8c;
    L_0x0a41:
        r0 = r32;
        r0 = r0.duration;	 Catch:{ Exception -> 0x021a }
        r38 = r0;
    L_0x0a47:
        r0 = r57;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021a }
        r6 = r38 + 1;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.ttl = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x083b;
    L_0x0a57:
        r0 = r57;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x021a }
        r0 = r20;
        r6 = r0.participant_id;	 Catch:{ Exception -> 0x021a }
        r4.user_id = r6;	 Catch:{ Exception -> 0x021a }
        goto L_0x09fb;
    L_0x0a62:
        r0 = r20;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.ttl = r4;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0a03;
    L_0x0a70:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0a03;
    L_0x0a76:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r6 = r0.ttl;	 Catch:{ Exception -> 0x021a }
        r4.ttl_seconds = r6;	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021a }
        r6 = r6 | 4;
        r4.flags = r6;	 Catch:{ Exception -> 0x021a }
        goto L_0x0a03;
    L_0x0a8c:
        r29 = r29 + 1;
        goto L_0x0a1b;
    L_0x0a8f:
        r4 = org.telegram.messenger.MessageObject.isVideoMessage(r57);	 Catch:{ Exception -> 0x021a }
        if (r4 != 0) goto L_0x0a9b;
    L_0x0a95:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r57);	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x083b;
    L_0x0a9b:
        r38 = 0;
        r29 = 0;
    L_0x0a9f:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.document;	 Catch:{ Exception -> 0x021a }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x021a }
        r4 = r4.size();	 Catch:{ Exception -> 0x021a }
        r0 = r29;
        if (r0 >= r4) goto L_0x0acb;
    L_0x0aaf:
        r0 = r57;
        r4 = r0.media;	 Catch:{ Exception -> 0x021a }
        r4 = r4.document;	 Catch:{ Exception -> 0x021a }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x021a }
        r0 = r29;
        r32 = r4.get(r0);	 Catch:{ Exception -> 0x021a }
        r32 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r32;	 Catch:{ Exception -> 0x021a }
        r0 = r32;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x021a }
        if (r4 == 0) goto L_0x0adb;
    L_0x0ac5:
        r0 = r32;
        r0 = r0.duration;	 Catch:{ Exception -> 0x021a }
        r38 = r0;
    L_0x0acb:
        r0 = r57;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021a }
        r6 = r38 + 1;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x021a }
        r0 = r57;
        r0.ttl = r4;	 Catch:{ Exception -> 0x021a }
        goto L_0x083b;
    L_0x0adb:
        r29 = r29 + 1;
        goto L_0x0a9f;
    L_0x0ade:
        r50 = 0;
        goto L_0x08c8;
    L_0x0ae2:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0e36 }
        r4.<init>();	 Catch:{ Exception -> 0x0e36 }
        r6 = "group_";
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0e36 }
        r0 = r42;
        r4 = r4.append(r0);	 Catch:{ Exception -> 0x0e36 }
        r51 = r4.toString();	 Catch:{ Exception -> 0x0e36 }
        r0 = r82;
        r4 = r0.delayedMessages;	 Catch:{ Exception -> 0x0e36 }
        r0 = r51;
        r31 = r4.get(r0);	 Catch:{ Exception -> 0x0e36 }
        r31 = (java.util.ArrayList) r31;	 Catch:{ Exception -> 0x0e36 }
        if (r31 == 0) goto L_0x1bca;
    L_0x0b06:
        r4 = 0;
        r0 = r31;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0e36 }
        r0 = r4;
        r0 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r0;	 Catch:{ Exception -> 0x0e36 }
        r10 = r0;
        r37 = r10;
    L_0x0b13:
        if (r37 != 0) goto L_0x1bc6;
    L_0x0b15:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r1 = r92;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r42;
        r10.initForGroup(r0);	 Catch:{ Exception -> 0x0e36 }
        r0 = r20;
        r10.encryptedChat = r0;	 Catch:{ Exception -> 0x0e36 }
    L_0x0b27:
        r4 = 0;
        r10.performMediaUpload = r4;	 Catch:{ Exception -> 0x0e36 }
        r4 = 0;
        r10.location = r4;	 Catch:{ Exception -> 0x0e36 }
        r4 = 0;
        r10.videoEditedInfo = r4;	 Catch:{ Exception -> 0x0e36 }
        r4 = 0;
        r10.httpLocation = r4;	 Catch:{ Exception -> 0x0e36 }
        if (r50 == 0) goto L_0x0b3b;
    L_0x0b35:
        r0 = r57;
        r4 = r0.id;	 Catch:{ Exception -> 0x0e36 }
        r10.finalGroupMessage = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x0b3b:
        r37 = r10;
        goto L_0x0912;
    L_0x0b3f:
        r0 = r83;
        r7.message = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r69;
        r7.contacts = r0;	 Catch:{ Exception -> 0x0ca9 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaEmpty;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r66;
        r7.random_id = r0;	 Catch:{ Exception -> 0x0ca9 }
        r9 = 0;
        r10 = 0;
        r6 = r82;
        r8 = r12;
        r11 = r103;
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0ca9 }
        r10 = r37;
        goto L_0x0008;
    L_0x0b60:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage;	 Catch:{ Exception -> 0x0ca9 }
        r7.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r83;
        r7.message = r0;	 Catch:{ Exception -> 0x0ca9 }
        if (r98 != 0) goto L_0x0bec;
    L_0x0b6b:
        r4 = 1;
    L_0x0b6c:
        r7.clear_draft = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r57;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0b9b;
    L_0x0b76:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0ca9 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0ca9 }
        r6.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r8 = "silent_";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r92;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0ca9 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0ca9 }
        r7.silent = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0b9b:
        r0 = r68;
        r7.peer = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r57;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0ca9 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r57;
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0bb7;
    L_0x0bab:
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 1;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r57;
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0ca9 }
        r7.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0bb7:
        if (r97 != 0) goto L_0x0bbc;
    L_0x0bb9:
        r4 = 1;
        r7.no_webpage = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0bbc:
        if (r99 == 0) goto L_0x0bce;
    L_0x0bbe:
        r4 = r99.isEmpty();	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x0bce;
    L_0x0bc4:
        r0 = r99;
        r7.entities = r0;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 8;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0bce:
        r9 = 0;
        r10 = 0;
        r6 = r82;
        r8 = r12;
        r11 = r103;
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0ca9 }
        if (r98 != 0) goto L_0x0be8;
    L_0x0bda:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r0 = r92;
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0ca9 }
    L_0x0be8:
        r10 = r37;
        goto L_0x0008;
    L_0x0bec:
        r4 = 0;
        goto L_0x0b6c;
    L_0x0bef:
        r0 = r20;
        r4 = r0.layer;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x0ca9 }
        r6 = 73;
        if (r4 < r6) goto L_0x0c9a;
    L_0x0bfb:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r7.<init>();	 Catch:{ Exception -> 0x0ca9 }
    L_0x0CLASSNAME:
        r0 = r57;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x0ca9 }
        r7.ttl = r4;	 Catch:{ Exception -> 0x0ca9 }
        if (r99 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r4 = r99.isEmpty();	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x0CLASSNAME;
    L_0x0c0e:
        r0 = r99;
        r7.entities = r0;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 128;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0CLASSNAME:
        r0 = r57;
        r8 = r0.reply_to_random_id;	 Catch:{ Exception -> 0x0ca9 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 == 0) goto L_0x0c2e;
    L_0x0CLASSNAME:
        r0 = r57;
        r8 = r0.reply_to_random_id;	 Catch:{ Exception -> 0x0ca9 }
        r7.reply_to_random_id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 8;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0c2e:
        if (r101 == 0) goto L_0x0c4e;
    L_0x0CLASSNAME:
        r4 = "bot_name";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0c4e;
    L_0x0c3b:
        r4 = "bot_name";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0ca9 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0ca9 }
        r7.via_bot_name = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 2048;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0c4e:
        r0 = r57;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0ca9 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r83;
        r7.message = r0;	 Catch:{ Exception -> 0x0ca9 }
        if (r96 == 0) goto L_0x0ca1;
    L_0x0c5a:
        r0 = r96;
        r4 = r0.url;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0ca1;
    L_0x0CLASSNAME:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r96;
        r6 = r0.url;	 Catch:{ Exception -> 0x0ca9 }
        r4.url = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 512;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0CLASSNAME:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r10 = 0;
        r11 = 0;
        r9 = r20;
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x0ca9 }
        if (r98 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r0 = r92;
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0ca9 }
    L_0x0CLASSNAME:
        r10 = r37;
        goto L_0x0008;
    L_0x0c9a:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x0ca9 }
        r7.<init>();	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x0CLASSNAME;
    L_0x0ca1:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x0CLASSNAME;
    L_0x0ca9:
        r39 = move-exception;
        r10 = r37;
        goto L_0x021d;
    L_0x0cae:
        r4 = 1;
        r0 = r75;
        if (r0 < r4) goto L_0x0cb8;
    L_0x0cb3:
        r4 = 3;
        r0 = r75;
        if (r0 <= r4) goto L_0x0cd1;
    L_0x0cb8:
        r4 = 5;
        r0 = r75;
        if (r0 < r4) goto L_0x0cc3;
    L_0x0cbd:
        r4 = 8;
        r0 = r75;
        if (r0 <= r4) goto L_0x0cd1;
    L_0x0cc3:
        r4 = 9;
        r0 = r75;
        if (r0 != r4) goto L_0x0ccb;
    L_0x0cc9:
        if (r20 != 0) goto L_0x0cd1;
    L_0x0ccb:
        r4 = 10;
        r0 = r75;
        if (r0 != r4) goto L_0x1a1b;
    L_0x0cd1:
        if (r20 != 0) goto L_0x13f6;
    L_0x0cd3:
        r46 = 0;
        r4 = 1;
        r0 = r75;
        if (r0 != r4) goto L_0x0d7b;
    L_0x0cda:
        r0 = r85;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0d57;
    L_0x0ce0:
        r46 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue;	 Catch:{ Exception -> 0x0ca9 }
        r46.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r4 = r0.address;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.address = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r4 = r0.title;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.title = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r4 = r0.provider;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.provider = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r4 = r0.venue_id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.venue_id = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = "";
        r0 = r46;
        r0.venue_type = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0d0c:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.geo_point = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r4 = r0.geo_point;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r6 = r0.geo;	 Catch:{ Exception -> 0x0ca9 }
        r8 = r6.lat;	 Catch:{ Exception -> 0x0ca9 }
        r4.lat = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r4 = r0.geo_point;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r6 = r0.geo;	 Catch:{ Exception -> 0x0ca9 }
        r8 = r6._long;	 Catch:{ Exception -> 0x0ca9 }
        r4._long = r8;	 Catch:{ Exception -> 0x0ca9 }
        r10 = r37;
    L_0x0d2f:
        if (r69 == 0) goto L_0x11ef;
    L_0x0d31:
        r67 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x0e36 }
        r67.<init>();	 Catch:{ Exception -> 0x0e36 }
        r66 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0e36 }
        r66.<init>();	 Catch:{ Exception -> 0x0e36 }
        r29 = 0;
    L_0x0d3d:
        r4 = r69.size();	 Catch:{ Exception -> 0x0e36 }
        r0 = r29;
        if (r0 >= r4) goto L_0x11b1;
    L_0x0d45:
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0e36 }
        r8 = r4.nextLong();	 Catch:{ Exception -> 0x0e36 }
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0e36 }
        r0 = r66;
        r0.add(r4);	 Catch:{ Exception -> 0x0e36 }
        r29 = r29 + 1;
        goto L_0x0d3d;
    L_0x0d57:
        r0 = r85;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0d75;
    L_0x0d5d:
        r46 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive;	 Catch:{ Exception -> 0x0ca9 }
        r46.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r4 = r0.period;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.period = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 2;
        r0 = r46;
        r0.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x0d0c;
    L_0x0d75:
        r46 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint;	 Catch:{ Exception -> 0x0ca9 }
        r46.<init>();	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x0d0c;
    L_0x0d7b:
        r4 = 2;
        r0 = r75;
        if (r0 == r4) goto L_0x0d88;
    L_0x0d80:
        r4 = 9;
        r0 = r75;
        if (r0 != r4) goto L_0x0e93;
    L_0x0d86:
        if (r86 == 0) goto L_0x0e93;
    L_0x0d88:
        r78 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x0ca9 }
        r78.<init>();	 Catch:{ Exception -> 0x0ca9 }
        if (r102 == 0) goto L_0x0da5;
    L_0x0d8f:
        r0 = r102;
        r1 = r78;
        r1.ttl_seconds = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r102;
        r1 = r57;
        r1.ttl = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r78;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 2;
        r0 = r78;
        r0.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0da5:
        if (r101 == 0) goto L_0x0df3;
    L_0x0da7:
        r4 = "masks";
        r0 = r101;
        r54 = r0.get(r4);	 Catch:{ Exception -> 0x0ca9 }
        r54 = (java.lang.String) r54;	 Catch:{ Exception -> 0x0ca9 }
        if (r54 == 0) goto L_0x0df3;
    L_0x0db4:
        r72 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.Utilities.hexToBytes(r54);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r72;
        r0.<init>(r4);	 Catch:{ Exception -> 0x0ca9 }
        r4 = 0;
        r0 = r72;
        r36 = r0.readInt32(r4);	 Catch:{ Exception -> 0x0ca9 }
        r29 = 0;
    L_0x0dc8:
        r0 = r29;
        r1 = r36;
        if (r0 >= r1) goto L_0x0de6;
    L_0x0dce:
        r0 = r78;
        r4 = r0.stickers;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r0 = r72;
        r6 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0ca9 }
        r8 = 0;
        r0 = r72;
        r6 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r0, r6, r8);	 Catch:{ Exception -> 0x0ca9 }
        r4.add(r6);	 Catch:{ Exception -> 0x0ca9 }
        r29 = r29 + 1;
        goto L_0x0dc8;
    L_0x0de6:
        r0 = r78;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 1;
        r0 = r78;
        r0.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
        r72.cleanup();	 Catch:{ Exception -> 0x0ca9 }
    L_0x0df3:
        r0 = r86;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0e39;
    L_0x0dfd:
        r46 = r78;
        r65 = 1;
    L_0x0e01:
        if (r37 != 0) goto L_0x1bc2;
    L_0x0e03:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r1 = r92;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0ca9 }
        r4 = 0;
        r10.type = r4;	 Catch:{ Exception -> 0x0e36 }
        r10.obj = r12;	 Catch:{ Exception -> 0x0e36 }
        r0 = r16;
        r10.originalPath = r0;	 Catch:{ Exception -> 0x0e36 }
    L_0x0e15:
        r0 = r78;
        r10.inputUploadMedia = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r65;
        r10.performMediaUpload = r0;	 Catch:{ Exception -> 0x0e36 }
        if (r94 == 0) goto L_0x0e79;
    L_0x0e1f:
        r4 = r94.length();	 Catch:{ Exception -> 0x0e36 }
        if (r4 <= 0) goto L_0x0e79;
    L_0x0e25:
        r4 = "http";
        r0 = r94;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x0e79;
    L_0x0e30:
        r0 = r94;
        r10.httpLocation = r0;	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0d2f;
    L_0x0e36:
        r39 = move-exception;
        goto L_0x021d;
    L_0x0e39:
        r55 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x0ca9 }
        r55.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r0.id = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r86;
        r8 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4.id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r86;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r86;
        r6 = r0.file_reference;	 Catch:{ Exception -> 0x0ca9 }
        r4.file_reference = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.file_reference;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x0e76;
    L_0x0e6d:
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0ca9 }
        r4.file_reference = r6;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0e76:
        r46 = r55;
        goto L_0x0e01;
    L_0x0e79:
        r0 = r86;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0e36 }
        r0 = r86;
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0e36 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0e36 }
        r6 = r6 + -1;
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0e36 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4.location;	 Catch:{ Exception -> 0x0e36 }
        r10.location = r4;	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0d2f;
    L_0x0e93:
        r4 = 3;
        r0 = r75;
        if (r0 != r4) goto L_0x0var_;
    L_0x0e98:
        r76 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0ca9 }
        r76.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r76;
        r0.mime_type = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r76;
        r0.attributes = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r89);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x0ed0;
    L_0x0eb3:
        if (r87 == 0) goto L_0x0ec1;
    L_0x0eb5:
        r0 = r87;
        r4 = r0.muted;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x0ed0;
    L_0x0ebb:
        r0 = r87;
        r4 = r0.roundVideo;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x0ed0;
    L_0x0ec1:
        r4 = 1;
        r0 = r76;
        r0.nosound_video = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0ed0;
    L_0x0eca:
        r4 = "nosound_video = true";
        org.telegram.messenger.FileLog.d(r4);	 Catch:{ Exception -> 0x0ca9 }
    L_0x0ed0:
        if (r102 == 0) goto L_0x0ee8;
    L_0x0ed2:
        r0 = r102;
        r1 = r76;
        r1.ttl_seconds = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r102;
        r1 = r57;
        r1.ttl = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r76;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 2;
        r0 = r76;
        r0.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0ee8:
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x0var_;
    L_0x0ef2:
        r46 = r76;
        r65 = 1;
    L_0x0ef6:
        if (r37 != 0) goto L_0x1bbe;
    L_0x0ef8:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r1 = r92;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0ca9 }
        r4 = 1;
        r10.type = r4;	 Catch:{ Exception -> 0x0e36 }
        r10.obj = r12;	 Catch:{ Exception -> 0x0e36 }
        r0 = r16;
        r10.originalPath = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r103;
        r10.parentObject = r0;	 Catch:{ Exception -> 0x0e36 }
    L_0x0f0e:
        r0 = r76;
        r10.inputUploadMedia = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r65;
        r10.performMediaUpload = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r89;
        r4 = r0.thumbs;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4.isEmpty();	 Catch:{ Exception -> 0x0e36 }
        if (r4 != 0) goto L_0x0f2f;
    L_0x0var_:
        r0 = r89;
        r4 = r0.thumbs;	 Catch:{ Exception -> 0x0e36 }
        r6 = 0;
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0e36 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4.location;	 Catch:{ Exception -> 0x0e36 }
        r10.location = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x0f2f:
        r0 = r87;
        r10.videoEditedInfo = r0;	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0d2f;
    L_0x0var_:
        r55 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0ca9 }
        r55.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r0.id = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4.id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.file_reference;	 Catch:{ Exception -> 0x0ca9 }
        r4.file_reference = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.file_reference;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x0var_;
    L_0x0var_:
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0ca9 }
        r4.file_reference = r6;	 Catch:{ Exception -> 0x0ca9 }
    L_0x0var_:
        r46 = r55;
        goto L_0x0ef6;
    L_0x0var_:
        r4 = 6;
        r0 = r75;
        if (r0 != r4) goto L_0x0fc1;
    L_0x0f7a:
        r46 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact;	 Catch:{ Exception -> 0x0ca9 }
        r46.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r88;
        r4 = r0.phone;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.phone_number = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r88;
        r4 = r0.first_name;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.first_name = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r88;
        r4 = r0.last_name;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.last_name = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r88;
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0fb6;
    L_0x0f9d:
        r0 = r88;
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x0ca9 }
        r6 = "BEGIN:VCARD";
        r4 = r4.startsWith(r6);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x0fb6;
    L_0x0faa:
        r0 = r88;
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r46;
        r0.vcard = r4;	 Catch:{ Exception -> 0x0ca9 }
        r10 = r37;
        goto L_0x0d2f;
    L_0x0fb6:
        r4 = "";
        r0 = r46;
        r0.vcard = r4;	 Catch:{ Exception -> 0x0ca9 }
        r10 = r37;
        goto L_0x0d2f;
    L_0x0fc1:
        r4 = 7;
        r0 = r75;
        if (r0 == r4) goto L_0x0fcc;
    L_0x0fc6:
        r4 = 9;
        r0 = r75;
        if (r0 != r4) goto L_0x10fb;
    L_0x0fcc:
        r45 = 0;
        if (r16 != 0) goto L_0x0fdc;
    L_0x0fd0:
        if (r94 != 0) goto L_0x0fdc;
    L_0x0fd2:
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x10b6;
    L_0x0fdc:
        if (r20 != 0) goto L_0x107f;
    L_0x0fde:
        r4 = android.text.TextUtils.isEmpty(r16);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x107f;
    L_0x0fe4:
        r4 = "http";
        r0 = r16;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x107f;
    L_0x0fef:
        if (r101 == 0) goto L_0x107f;
    L_0x0ff1:
        r40 = new org.telegram.tgnet.TLRPC$TL_inputMediaGifExternal;	 Catch:{ Exception -> 0x0ca9 }
        r40.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r4 = "url";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0ca9 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0ca9 }
        r6 = "\\|";
        r30 = r4.split(r6);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r30;
        r4 = r0.length;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 2;
        if (r4 != r6) goto L_0x101c;
    L_0x100e:
        r4 = 0;
        r4 = r30[r4];	 Catch:{ Exception -> 0x0ca9 }
        r0 = r40;
        r0.url = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = 1;
        r4 = r30[r4];	 Catch:{ Exception -> 0x0ca9 }
        r0 = r40;
        r0.q = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x101c:
        r45 = 1;
        r77 = r40;
    L_0x1020:
        r0 = r89;
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r77;
        r0.mime_type = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r77;
        r0.attributes = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x1030:
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x10ba;
    L_0x103a:
        r46 = r77;
        r0 = r77;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0ca9 }
        r65 = r0;
    L_0x1042:
        if (r45 != 0) goto L_0x1bba;
    L_0x1044:
        if (r77 == 0) goto L_0x1bba;
    L_0x1046:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r1 = r92;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r16;
        r10.originalPath = r0;	 Catch:{ Exception -> 0x0e36 }
        r4 = 2;
        r10.type = r4;	 Catch:{ Exception -> 0x0e36 }
        r10.obj = r12;	 Catch:{ Exception -> 0x0e36 }
        r0 = r89;
        r4 = r0.thumbs;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4.isEmpty();	 Catch:{ Exception -> 0x0e36 }
        if (r4 != 0) goto L_0x1071;
    L_0x1062:
        r0 = r89;
        r4 = r0.thumbs;	 Catch:{ Exception -> 0x0e36 }
        r6 = 0;
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0e36 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4.location;	 Catch:{ Exception -> 0x0e36 }
        r10.location = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x1071:
        r0 = r103;
        r10.parentObject = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r77;
        r10.inputUploadMedia = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r65;
        r10.performMediaUpload = r0;	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0d2f;
    L_0x107f:
        r77 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0ca9 }
        r77.<init>();	 Catch:{ Exception -> 0x0ca9 }
        if (r102 == 0) goto L_0x109c;
    L_0x1086:
        r0 = r102;
        r1 = r77;
        r1.ttl_seconds = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r102;
        r1 = r57;
        r1.ttl = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r77;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 2;
        r0 = r77;
        r0.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x109c:
        r4 = android.text.TextUtils.isEmpty(r94);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x1020;
    L_0x10a2:
        r4 = r94.toLowerCase();	 Catch:{ Exception -> 0x0ca9 }
        r6 = "mp4";
        r4 = r4.endsWith(r6);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1020;
    L_0x10af:
        r4 = 1;
        r0 = r77;
        r0.nosound_video = r4;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x1020;
    L_0x10b6:
        r77 = 0;
        goto L_0x1030;
    L_0x10ba:
        r55 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0ca9 }
        r55.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r0.id = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4.id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.file_reference;	 Catch:{ Exception -> 0x0ca9 }
        r4.file_reference = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.file_reference;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x10f7;
    L_0x10ee:
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0ca9 }
        r4.file_reference = r6;	 Catch:{ Exception -> 0x0ca9 }
    L_0x10f7:
        r46 = r55;
        goto L_0x1042;
    L_0x10fb:
        r4 = 8;
        r0 = r75;
        if (r0 != r4) goto L_0x1198;
    L_0x1101:
        r76 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0ca9 }
        r76.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r76;
        r0.mime_type = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r76;
        r0.attributes = r4;	 Catch:{ Exception -> 0x0ca9 }
        if (r102 == 0) goto L_0x112e;
    L_0x1118:
        r0 = r102;
        r1 = r76;
        r1.ttl_seconds = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r102;
        r1 = r57;
        r1.ttl = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r76;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 2;
        r0 = r76;
        r0.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x112e:
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x1158;
    L_0x1138:
        r46 = r76;
        r65 = 1;
    L_0x113c:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r1 = r92;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0ca9 }
        r4 = 3;
        r10.type = r4;	 Catch:{ Exception -> 0x0e36 }
        r10.obj = r12;	 Catch:{ Exception -> 0x0e36 }
        r0 = r103;
        r10.parentObject = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r76;
        r10.inputUploadMedia = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r65;
        r10.performMediaUpload = r0;	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0d2f;
    L_0x1158:
        r55 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0ca9 }
        r55.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r0.id = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4.id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.file_reference;	 Catch:{ Exception -> 0x0ca9 }
        r4.file_reference = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.file_reference;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x1195;
    L_0x118c:
        r0 = r55;
        r4 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0ca9 }
        r4.file_reference = r6;	 Catch:{ Exception -> 0x0ca9 }
    L_0x1195:
        r46 = r55;
        goto L_0x113c;
    L_0x1198:
        r4 = 10;
        r0 = r75;
        if (r0 != r4) goto L_0x1bba;
    L_0x119e:
        r47 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll;	 Catch:{ Exception -> 0x0ca9 }
        r47.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r91;
        r4 = r0.poll;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r47;
        r0.poll = r4;	 Catch:{ Exception -> 0x0ca9 }
        r46 = r47;
        r10 = r37;
        goto L_0x0d2f;
    L_0x11b1:
        r0 = r69;
        r1 = r67;
        r1.contacts = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r46;
        r1 = r67;
        r1.media = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r66;
        r1 = r67;
        r1.random_id = r0;	 Catch:{ Exception -> 0x0e36 }
        r4 = "";
        r0 = r67;
        r0.message = r4;	 Catch:{ Exception -> 0x0e36 }
        if (r10 == 0) goto L_0x11d0;
    L_0x11cc:
        r0 = r67;
        r10.sendRequest = r0;	 Catch:{ Exception -> 0x0e36 }
    L_0x11d0:
        r7 = r67;
        if (r98 != 0) goto L_0x11e2;
    L_0x11d4:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0e36 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0e36 }
        r6 = 0;
        r0 = r92;
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0e36 }
    L_0x11e2:
        r8 = 0;
        r4 = (r42 > r8 ? 1 : (r42 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x1353;
    L_0x11e8:
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x11ef:
        r8 = 0;
        r4 = (r42 > r8 ? 1 : (r42 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x12cb;
    L_0x11f5:
        r4 = r10.sendRequest;	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x1273;
    L_0x11f9:
        r0 = r10.sendRequest;	 Catch:{ Exception -> 0x0e36 }
        r67 = r0;
        r67 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r67;	 Catch:{ Exception -> 0x0e36 }
    L_0x11ff:
        r4 = r10.messageObjects;	 Catch:{ Exception -> 0x0e36 }
        r4.add(r12);	 Catch:{ Exception -> 0x0e36 }
        r4 = r10.parentObjects;	 Catch:{ Exception -> 0x0e36 }
        r0 = r103;
        r4.add(r0);	 Catch:{ Exception -> 0x0e36 }
        r4 = r10.locations;	 Catch:{ Exception -> 0x0e36 }
        r6 = r10.location;	 Catch:{ Exception -> 0x0e36 }
        r4.add(r6);	 Catch:{ Exception -> 0x0e36 }
        r4 = r10.videoEditedInfos;	 Catch:{ Exception -> 0x0e36 }
        r6 = r10.videoEditedInfo;	 Catch:{ Exception -> 0x0e36 }
        r4.add(r6);	 Catch:{ Exception -> 0x0e36 }
        r4 = r10.httpLocations;	 Catch:{ Exception -> 0x0e36 }
        r6 = r10.httpLocation;	 Catch:{ Exception -> 0x0e36 }
        r4.add(r6);	 Catch:{ Exception -> 0x0e36 }
        r4 = r10.inputMedias;	 Catch:{ Exception -> 0x0e36 }
        r6 = r10.inputUploadMedia;	 Catch:{ Exception -> 0x0e36 }
        r4.add(r6);	 Catch:{ Exception -> 0x0e36 }
        r4 = r10.messages;	 Catch:{ Exception -> 0x0e36 }
        r0 = r57;
        r4.add(r0);	 Catch:{ Exception -> 0x0e36 }
        r4 = r10.originalPaths;	 Catch:{ Exception -> 0x0e36 }
        r0 = r16;
        r4.add(r0);	 Catch:{ Exception -> 0x0e36 }
        r48 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia;	 Catch:{ Exception -> 0x0e36 }
        r48.<init>();	 Catch:{ Exception -> 0x0e36 }
        r0 = r57;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0e36 }
        r0 = r48;
        r0.random_id = r8;	 Catch:{ Exception -> 0x0e36 }
        r0 = r46;
        r1 = r48;
        r1.media = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r84;
        r1 = r48;
        r1.message = r0;	 Catch:{ Exception -> 0x0e36 }
        if (r99 == 0) goto L_0x1266;
    L_0x1250:
        r4 = r99.isEmpty();	 Catch:{ Exception -> 0x0e36 }
        if (r4 != 0) goto L_0x1266;
    L_0x1256:
        r0 = r99;
        r1 = r48;
        r1.entities = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r48;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4 | 1;
        r0 = r48;
        r0.flags = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x1266:
        r0 = r67;
        r4 = r0.multi_media;	 Catch:{ Exception -> 0x0e36 }
        r0 = r48;
        r4.add(r0);	 Catch:{ Exception -> 0x0e36 }
        r7 = r67;
        goto L_0x11e2;
    L_0x1273:
        r67 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;	 Catch:{ Exception -> 0x0e36 }
        r67.<init>();	 Catch:{ Exception -> 0x0e36 }
        r0 = r68;
        r1 = r67;
        r1.peer = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r57;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x12ad;
    L_0x1286:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0e36 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0e36 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0e36 }
        r6.<init>();	 Catch:{ Exception -> 0x0e36 }
        r8 = "silent_";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0e36 }
        r0 = r92;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0e36 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0e36 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0e36 }
        r0 = r67;
        r0.silent = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x12ad:
        r0 = r57;
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x12c5;
    L_0x12b3:
        r0 = r67;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4 | 1;
        r0 = r67;
        r0.flags = r4;	 Catch:{ Exception -> 0x0e36 }
        r0 = r57;
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0e36 }
        r0 = r67;
        r0.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x12c5:
        r0 = r67;
        r10.sendRequest = r0;	 Catch:{ Exception -> 0x0e36 }
        goto L_0x11ff;
    L_0x12cb:
        r67 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia;	 Catch:{ Exception -> 0x0e36 }
        r67.<init>();	 Catch:{ Exception -> 0x0e36 }
        r0 = r68;
        r1 = r67;
        r1.peer = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r57;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x1305;
    L_0x12de:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0e36 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0e36 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0e36 }
        r6.<init>();	 Catch:{ Exception -> 0x0e36 }
        r8 = "silent_";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0e36 }
        r0 = r92;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0e36 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0e36 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0e36 }
        r0 = r67;
        r0.silent = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x1305:
        r0 = r57;
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x131d;
    L_0x130b:
        r0 = r67;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4 | 1;
        r0 = r67;
        r0.flags = r4;	 Catch:{ Exception -> 0x0e36 }
        r0 = r57;
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0e36 }
        r0 = r67;
        r0.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x131d:
        r0 = r57;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0e36 }
        r0 = r67;
        r0.random_id = r8;	 Catch:{ Exception -> 0x0e36 }
        r0 = r46;
        r1 = r67;
        r1.media = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r84;
        r1 = r67;
        r1.message = r0;	 Catch:{ Exception -> 0x0e36 }
        if (r99 == 0) goto L_0x1349;
    L_0x1333:
        r4 = r99.isEmpty();	 Catch:{ Exception -> 0x0e36 }
        if (r4 != 0) goto L_0x1349;
    L_0x1339:
        r0 = r99;
        r1 = r67;
        r1.entities = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r67;
        r4 = r0.flags;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4 | 8;
        r0 = r67;
        r0.flags = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x1349:
        if (r10 == 0) goto L_0x134f;
    L_0x134b:
        r0 = r67;
        r10.sendRequest = r0;	 Catch:{ Exception -> 0x0e36 }
    L_0x134f:
        r7 = r67;
        goto L_0x11e2;
    L_0x1353:
        r4 = 1;
        r0 = r75;
        if (r0 != r4) goto L_0x1363;
    L_0x1358:
        r9 = 0;
        r6 = r82;
        r8 = r12;
        r11 = r103;
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x1363:
        r4 = 2;
        r0 = r75;
        if (r0 != r4) goto L_0x1382;
    L_0x1368:
        if (r65 == 0) goto L_0x1371;
    L_0x136a:
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x1371:
        r17 = 0;
        r18 = 1;
        r13 = r82;
        r14 = r7;
        r15 = r12;
        r19 = r10;
        r20 = r103;
        r13.performSendMessageRequest(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x1382:
        r4 = 3;
        r0 = r75;
        if (r0 != r4) goto L_0x139c;
    L_0x1387:
        if (r65 == 0) goto L_0x1390;
    L_0x1389:
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x1390:
        r6 = r82;
        r8 = r12;
        r9 = r16;
        r11 = r103;
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x139c:
        r4 = 6;
        r0 = r75;
        if (r0 != r4) goto L_0x13ad;
    L_0x13a1:
        r6 = r82;
        r8 = r12;
        r9 = r16;
        r11 = r103;
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x13ad:
        r4 = 7;
        r0 = r75;
        if (r0 != r4) goto L_0x13c9;
    L_0x13b2:
        if (r65 == 0) goto L_0x13bd;
    L_0x13b4:
        if (r10 == 0) goto L_0x13bd;
    L_0x13b6:
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x13bd:
        r6 = r82;
        r8 = r12;
        r9 = r16;
        r11 = r103;
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x13c9:
        r4 = 8;
        r0 = r75;
        if (r0 != r4) goto L_0x13e4;
    L_0x13cf:
        if (r65 == 0) goto L_0x13d8;
    L_0x13d1:
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x13d8:
        r6 = r82;
        r8 = r12;
        r9 = r16;
        r11 = r103;
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x13e4:
        r4 = 10;
        r0 = r75;
        if (r0 != r4) goto L_0x0008;
    L_0x13ea:
        r6 = r82;
        r8 = r12;
        r9 = r16;
        r11 = r103;
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x13f6:
        r0 = r20;
        r4 = r0.layer;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x0ca9 }
        r6 = 73;
        if (r4 < r6) goto L_0x1533;
    L_0x1402:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r7.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r8 = 0;
        r4 = (r42 > r8 ? 1 : (r42 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x1418;
    L_0x140d:
        r0 = r42;
        r7.grouped_id = r0;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r4 = r4 | r6;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x1418:
        r0 = r57;
        r4 = r0.ttl;	 Catch:{ Exception -> 0x0ca9 }
        r7.ttl = r4;	 Catch:{ Exception -> 0x0ca9 }
        if (r99 == 0) goto L_0x1430;
    L_0x1420:
        r4 = r99.isEmpty();	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x1430;
    L_0x1426:
        r0 = r99;
        r7.entities = r0;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 128;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x1430:
        r0 = r57;
        r8 = r0.reply_to_random_id;	 Catch:{ Exception -> 0x0ca9 }
        r14 = 0;
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r4 == 0) goto L_0x1446;
    L_0x143a:
        r0 = r57;
        r8 = r0.reply_to_random_id;	 Catch:{ Exception -> 0x0ca9 }
        r7.reply_to_random_id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 8;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x1446:
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 512;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
        if (r101 == 0) goto L_0x146c;
    L_0x144e:
        r4 = "bot_name";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x146c;
    L_0x1459:
        r4 = "bot_name";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0ca9 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0ca9 }
        r7.via_bot_name = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 2048;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x146c:
        r0 = r57;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0ca9 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = "";
        r7.message = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = 1;
        r0 = r75;
        if (r0 != r4) goto L_0x1543;
    L_0x147c:
        r0 = r85;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x153a;
    L_0x1482:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r6 = r0.address;	 Catch:{ Exception -> 0x0ca9 }
        r4.address = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r6 = r0.title;	 Catch:{ Exception -> 0x0ca9 }
        r4.title = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r6 = r0.provider;	 Catch:{ Exception -> 0x0ca9 }
        r4.provider = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r6 = r0.venue_id;	 Catch:{ Exception -> 0x0ca9 }
        r4.venue_id = r6;	 Catch:{ Exception -> 0x0ca9 }
    L_0x14a9:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r6 = r0.geo;	 Catch:{ Exception -> 0x0ca9 }
        r8 = r6.lat;	 Catch:{ Exception -> 0x0ca9 }
        r4.lat = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r85;
        r6 = r0.geo;	 Catch:{ Exception -> 0x0ca9 }
        r8 = r6._long;	 Catch:{ Exception -> 0x0ca9 }
        r4._long = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r17 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r12.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r19 = r0;
        r21 = 0;
        r22 = 0;
        r18 = r7;
        r23 = r12;
        r17.performSendEncryptedRequest(r18, r19, r20, r21, r22, r23);	 Catch:{ Exception -> 0x0ca9 }
        r10 = r37;
    L_0x14d6:
        r8 = 0;
        r4 = (r42 > r8 ? 1 : (r42 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x1521;
    L_0x14dc:
        r4 = r10.sendEncryptedRequest;	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x1a0c;
    L_0x14e0:
        r0 = r10.sendEncryptedRequest;	 Catch:{ Exception -> 0x0e36 }
        r67 = r0;
        r67 = (org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r67;	 Catch:{ Exception -> 0x0e36 }
    L_0x14e6:
        r4 = r10.messageObjects;	 Catch:{ Exception -> 0x0e36 }
        r4.add(r12);	 Catch:{ Exception -> 0x0e36 }
        r4 = r10.messages;	 Catch:{ Exception -> 0x0e36 }
        r0 = r57;
        r4.add(r0);	 Catch:{ Exception -> 0x0e36 }
        r4 = r10.originalPaths;	 Catch:{ Exception -> 0x0e36 }
        r0 = r16;
        r4.add(r0);	 Catch:{ Exception -> 0x0e36 }
        r4 = 1;
        r10.performMediaUpload = r4;	 Catch:{ Exception -> 0x0e36 }
        r0 = r67;
        r4 = r0.messages;	 Catch:{ Exception -> 0x0e36 }
        r4.add(r7);	 Catch:{ Exception -> 0x0e36 }
        r21 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0e36 }
        r21.<init>();	 Catch:{ Exception -> 0x0e36 }
        r4 = 3;
        r0 = r75;
        if (r0 != r4) goto L_0x1a17;
    L_0x150d:
        r8 = 1;
    L_0x150f:
        r0 = r21;
        r0.id = r8;	 Catch:{ Exception -> 0x0e36 }
        r0 = r67;
        r4 = r0.files;	 Catch:{ Exception -> 0x0e36 }
        r0 = r21;
        r4.add(r0);	 Catch:{ Exception -> 0x0e36 }
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
    L_0x1521:
        if (r98 != 0) goto L_0x0008;
    L_0x1523:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0e36 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0e36 }
        r6 = 0;
        r0 = r92;
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x0008;
    L_0x1533:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x0ca9 }
        r7.<init>();	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x1418;
    L_0x153a:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x14a9;
    L_0x1543:
        r4 = 2;
        r0 = r75;
        if (r0 == r4) goto L_0x1550;
    L_0x1548:
        r4 = 9;
        r0 = r75;
        if (r0 != r4) goto L_0x1672;
    L_0x154e:
        if (r86 == 0) goto L_0x1672;
    L_0x1550:
        r0 = r86;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r73 = r4.get(r6);	 Catch:{ Exception -> 0x0ca9 }
        r73 = (org.telegram.tgnet.TLRPC.PhotoSize) r73;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r86;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r86;
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6 + -1;
        r34 = r4.get(r6);	 Catch:{ Exception -> 0x0ca9 }
        r34 = (org.telegram.tgnet.TLRPC.PhotoSize) r34;	 Catch:{ Exception -> 0x0ca9 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r73);	 Catch:{ Exception -> 0x0ca9 }
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r84;
        r4.caption = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r73;
        r4 = r0.bytes;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1608;
    L_0x1585:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r73;
        r6 = r0.bytes;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0ca9 }
    L_0x158f:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r73;
        r6 = r0.h;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r73;
        r6 = r0.w;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r34;
        r6 = r0.w;	 Catch:{ Exception -> 0x0ca9 }
        r4.w = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r34;
        r6 = r0.h;	 Catch:{ Exception -> 0x0ca9 }
        r4.h = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r34;
        r6 = r0.size;	 Catch:{ Exception -> 0x0ca9 }
        r4.size = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r34;
        r4 = r0.location;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.key;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x15c5;
    L_0x15bf:
        r8 = 0;
        r4 = (r42 > r8 ? 1 : (r42 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x162c;
    L_0x15c5:
        if (r37 != 0) goto L_0x1bb6;
    L_0x15c7:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r1 = r92;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r20;
        r10.encryptedChat = r0;	 Catch:{ Exception -> 0x0e36 }
        r4 = 0;
        r10.type = r4;	 Catch:{ Exception -> 0x0e36 }
        r0 = r16;
        r10.originalPath = r0;	 Catch:{ Exception -> 0x0e36 }
        r10.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x0e36 }
        r10.obj = r12;	 Catch:{ Exception -> 0x0e36 }
        r0 = r103;
        r10.parentObject = r0;	 Catch:{ Exception -> 0x0e36 }
        r4 = 1;
        r10.performMediaUpload = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x15e6:
        r4 = android.text.TextUtils.isEmpty(r94);	 Catch:{ Exception -> 0x0e36 }
        if (r4 != 0) goto L_0x1613;
    L_0x15ec:
        r4 = "http";
        r0 = r94;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x1613;
    L_0x15f7:
        r0 = r94;
        r10.httpLocation = r0;	 Catch:{ Exception -> 0x0e36 }
    L_0x15fb:
        r8 = 0;
        r4 = (r42 > r8 ? 1 : (r42 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x14d6;
    L_0x1601:
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x14d6;
    L_0x1608:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x158f;
    L_0x1613:
        r0 = r86;
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0e36 }
        r0 = r86;
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0e36 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0e36 }
        r6 = r6 + -1;
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0e36 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0e36 }
        r4 = r4.location;	 Catch:{ Exception -> 0x0e36 }
        r10.location = r4;	 Catch:{ Exception -> 0x0e36 }
        goto L_0x15fb;
    L_0x162c:
        r21 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0ca9 }
        r21.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r34;
        r4 = r0.location;	 Catch:{ Exception -> 0x0ca9 }
        r8 = r4.volume_id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r21;
        r0.id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r34;
        r4 = r0.location;	 Catch:{ Exception -> 0x0ca9 }
        r8 = r4.secret;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r21;
        r0.access_hash = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r34;
        r6 = r0.location;	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.key;	 Catch:{ Exception -> 0x0ca9 }
        r4.key = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r34;
        r6 = r0.location;	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.iv;	 Catch:{ Exception -> 0x0ca9 }
        r4.iv = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r17 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r12.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r19 = r0;
        r22 = 0;
        r18 = r7;
        r23 = r12;
        r17.performSendEncryptedRequest(r18, r19, r20, r21, r22, r23);	 Catch:{ Exception -> 0x0ca9 }
    L_0x166e:
        r10 = r37;
        goto L_0x14d6;
    L_0x1672:
        r4 = 3;
        r0 = r75;
        if (r0 != r4) goto L_0x17bd;
    L_0x1677:
        r0 = r89;
        r4 = r0.thumbs;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r74 = r0.getThumbForSecretChat(r4);	 Catch:{ Exception -> 0x0ca9 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r74);	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.MessageObject.isNewGifDocument(r89);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x1690;
    L_0x168a:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r89);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1757;
    L_0x1690:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.attributes;	 Catch:{ Exception -> 0x0ca9 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x0ca9 }
        if (r74 == 0) goto L_0x174c;
    L_0x16a1:
        r0 = r74;
        r4 = r0.bytes;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x174c;
    L_0x16a7:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r74;
        r6 = r0.bytes;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0ca9 }
    L_0x16b1:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r84;
        r4.caption = r0;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r6 = "video/mp4";
        r4.mime_type = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.size;	 Catch:{ Exception -> 0x0ca9 }
        r4.size = r6;	 Catch:{ Exception -> 0x0ca9 }
        r29 = 0;
    L_0x16c8:
        r0 = r89;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.size();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r29;
        if (r0 >= r4) goto L_0x16fe;
    L_0x16d4:
        r0 = r89;
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r29;
        r32 = r4.get(r0);	 Catch:{ Exception -> 0x0ca9 }
        r32 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r32;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r32;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x177d;
    L_0x16e6:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r32;
        r6 = r0.w;	 Catch:{ Exception -> 0x0ca9 }
        r4.w = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r32;
        r6 = r0.h;	 Catch:{ Exception -> 0x0ca9 }
        r4.h = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r32;
        r6 = r0.duration;	 Catch:{ Exception -> 0x0ca9 }
        r4.duration = r6;	 Catch:{ Exception -> 0x0ca9 }
    L_0x16fe:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r74;
        r6 = r0.h;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r74;
        r6 = r0.w;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r4 = r0.key;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x171a;
    L_0x1714:
        r8 = 0;
        r4 = (r42 > r8 ? 1 : (r42 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x1781;
    L_0x171a:
        if (r37 != 0) goto L_0x1bb2;
    L_0x171c:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r1 = r92;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r20;
        r10.encryptedChat = r0;	 Catch:{ Exception -> 0x0e36 }
        r4 = 1;
        r10.type = r4;	 Catch:{ Exception -> 0x0e36 }
        r10.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x0e36 }
        r0 = r16;
        r10.originalPath = r0;	 Catch:{ Exception -> 0x0e36 }
        r10.obj = r12;	 Catch:{ Exception -> 0x0e36 }
        r0 = r103;
        r10.parentObject = r0;	 Catch:{ Exception -> 0x0e36 }
        r4 = 1;
        r10.performMediaUpload = r4;	 Catch:{ Exception -> 0x0e36 }
    L_0x173b:
        r0 = r87;
        r10.videoEditedInfo = r0;	 Catch:{ Exception -> 0x0e36 }
        r8 = 0;
        r4 = (r42 > r8 ? 1 : (r42 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x14d6;
    L_0x1745:
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x14d6;
    L_0x174c:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x16b1;
    L_0x1757:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        if (r74 == 0) goto L_0x1772;
    L_0x1760:
        r0 = r74;
        r4 = r0.bytes;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1772;
    L_0x1766:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r74;
        r6 = r0.bytes;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x16b1;
    L_0x1772:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r4;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x16b1;
    L_0x177d:
        r29 = r29 + 1;
        goto L_0x16c8;
    L_0x1781:
        r21 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0ca9 }
        r21.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r21;
        r0.id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r21;
        r0.access_hash = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.key;	 Catch:{ Exception -> 0x0ca9 }
        r4.key = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.iv;	 Catch:{ Exception -> 0x0ca9 }
        r4.iv = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r17 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r12.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r19 = r0;
        r22 = 0;
        r18 = r7;
        r23 = r12;
        r17.performSendEncryptedRequest(r18, r19, r20, r21, r22, r23);	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x166e;
    L_0x17bd:
        r4 = 6;
        r0 = r75;
        if (r0 != r4) goto L_0x1806;
    L_0x17c2:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r88;
        r6 = r0.phone;	 Catch:{ Exception -> 0x0ca9 }
        r4.phone_number = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r88;
        r6 = r0.first_name;	 Catch:{ Exception -> 0x0ca9 }
        r4.first_name = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r88;
        r6 = r0.last_name;	 Catch:{ Exception -> 0x0ca9 }
        r4.last_name = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r88;
        r6 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4.user_id = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r22 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r12.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r24 = r0;
        r26 = 0;
        r27 = 0;
        r23 = r7;
        r25 = r20;
        r28 = r12;
        r22.performSendEncryptedRequest(r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x0ca9 }
        r10 = r37;
        goto L_0x14d6;
    L_0x1806:
        r4 = 7;
        r0 = r75;
        if (r0 == r4) goto L_0x1813;
    L_0x180b:
        r4 = 9;
        r0 = r75;
        if (r0 != r4) goto L_0x1981;
    L_0x1811:
        if (r89 == 0) goto L_0x1981;
    L_0x1813:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r89);	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x18a0;
    L_0x1819:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r4.id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.date;	 Catch:{ Exception -> 0x0ca9 }
        r4.date = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.mime_type;	 Catch:{ Exception -> 0x0ca9 }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.size;	 Catch:{ Exception -> 0x0ca9 }
        r4.size = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.dc_id;	 Catch:{ Exception -> 0x0ca9 }
        r4.dc_id = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.attributes;	 Catch:{ Exception -> 0x0ca9 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r4 = r0.thumbs;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r74 = r0.getThumbForSecretChat(r4);	 Catch:{ Exception -> 0x0ca9 }
        if (r74 == 0) goto L_0x1889;
    L_0x1864:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r74;
        r4.thumb = r0;	 Catch:{ Exception -> 0x0ca9 }
    L_0x186c:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r22 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r12.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r24 = r0;
        r26 = 0;
        r27 = 0;
        r23 = r7;
        r25 = r20;
        r28 = r12;
        r22.performSendEncryptedRequest(r23, r24, r25, r26, r27, r28);	 Catch:{ Exception -> 0x0ca9 }
        r10 = r37;
        goto L_0x14d6;
    L_0x1889:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r4;	 Catch:{ Exception -> 0x0ca9 }
        r6 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x0ca9 }
        r6.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.thumb;	 Catch:{ Exception -> 0x0ca9 }
        r6 = "s";
        r4.type = r6;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x186c;
    L_0x18a0:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.media = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.attributes;	 Catch:{ Exception -> 0x0ca9 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r84;
        r4.caption = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r4 = r0.thumbs;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r74 = r0.getThumbForSecretChat(r4);	 Catch:{ Exception -> 0x0ca9 }
        if (r74 == 0) goto L_0x1931;
    L_0x18c1:
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r74);	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r74;
        r6 = r0.bytes;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r74;
        r6 = r0.h;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r74;
        r6 = r0.w;	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0ca9 }
    L_0x18de:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.size;	 Catch:{ Exception -> 0x0ca9 }
        r4.size = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.mime_type;	 Catch:{ Exception -> 0x0ca9 }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r4 = r0.key;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 != 0) goto L_0x1945;
    L_0x18f4:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r1 = r92;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r16;
        r10.originalPath = r0;	 Catch:{ Exception -> 0x0e36 }
        r10.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x0e36 }
        r4 = 2;
        r10.type = r4;	 Catch:{ Exception -> 0x0e36 }
        r10.obj = r12;	 Catch:{ Exception -> 0x0e36 }
        r0 = r103;
        r10.parentObject = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r20;
        r10.encryptedChat = r0;	 Catch:{ Exception -> 0x0e36 }
        r4 = 1;
        r10.performMediaUpload = r4;	 Catch:{ Exception -> 0x0e36 }
        if (r94 == 0) goto L_0x192a;
    L_0x1915:
        r4 = r94.length();	 Catch:{ Exception -> 0x0e36 }
        if (r4 <= 0) goto L_0x192a;
    L_0x191b:
        r4 = "http";
        r0 = r94;
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0e36 }
        if (r4 == 0) goto L_0x192a;
    L_0x1926:
        r0 = r94;
        r10.httpLocation = r0;	 Catch:{ Exception -> 0x0e36 }
    L_0x192a:
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x14d6;
    L_0x1931:
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0ca9 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x18de;
    L_0x1945:
        r21 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0ca9 }
        r21.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r21;
        r0.id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r21;
        r0.access_hash = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.key;	 Catch:{ Exception -> 0x0ca9 }
        r4.key = r6;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r89;
        r6 = r0.iv;	 Catch:{ Exception -> 0x0ca9 }
        r4.iv = r6;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r17 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r12.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r19 = r0;
        r22 = 0;
        r18 = r7;
        r23 = r12;
        r17.performSendEncryptedRequest(r18, r19, r20, r21, r22, r23);	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x166e;
    L_0x1981:
        r4 = 8;
        r0 = r75;
        if (r0 != r4) goto L_0x166e;
    L_0x1987:
        r10 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r1 = r92;
        r10.<init>(r1);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r20;
        r10.encryptedChat = r0;	 Catch:{ Exception -> 0x0e36 }
        r10.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x0e36 }
        r10.obj = r12;	 Catch:{ Exception -> 0x0e36 }
        r4 = 3;
        r10.type = r4;	 Catch:{ Exception -> 0x0e36 }
        r0 = r103;
        r10.parentObject = r0;	 Catch:{ Exception -> 0x0e36 }
        r4 = 1;
        r10.performMediaUpload = r4;	 Catch:{ Exception -> 0x0e36 }
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x0e36 }
        r4.<init>();	 Catch:{ Exception -> 0x0e36 }
        r7.media = r4;	 Catch:{ Exception -> 0x0e36 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r0 = r89;
        r6 = r0.attributes;	 Catch:{ Exception -> 0x0e36 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x0e36 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r0 = r84;
        r4.caption = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r89;
        r4 = r0.thumbs;	 Catch:{ Exception -> 0x0e36 }
        r0 = r82;
        r74 = r0.getThumbForSecretChat(r4);	 Catch:{ Exception -> 0x0e36 }
        if (r74 == 0) goto L_0x19f8;
    L_0x19c3:
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0e36 }
        r0 = r74;
        r6 = r0.bytes;	 Catch:{ Exception -> 0x0e36 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0e36 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r0 = r74;
        r6 = r0.h;	 Catch:{ Exception -> 0x0e36 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0e36 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r0 = r74;
        r6 = r0.w;	 Catch:{ Exception -> 0x0e36 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0e36 }
    L_0x19dd:
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r0 = r89;
        r6 = r0.mime_type;	 Catch:{ Exception -> 0x0e36 }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x0e36 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r0 = r89;
        r6 = r0.size;	 Catch:{ Exception -> 0x0e36 }
        r4.size = r6;	 Catch:{ Exception -> 0x0e36 }
        r0 = r16;
        r10.originalPath = r0;	 Catch:{ Exception -> 0x0e36 }
        r0 = r82;
        r0.performSendDelayedMessage(r10);	 Catch:{ Exception -> 0x0e36 }
        goto L_0x14d6;
    L_0x19f8:
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0e36 }
        r6 = 0;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0e36 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0e36 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r6 = 0;
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0e36 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0e36 }
        r6 = 0;
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0e36 }
        goto L_0x19dd;
    L_0x1a0c:
        r67 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;	 Catch:{ Exception -> 0x0e36 }
        r67.<init>();	 Catch:{ Exception -> 0x0e36 }
        r0 = r67;
        r10.sendEncryptedRequest = r0;	 Catch:{ Exception -> 0x0e36 }
        goto L_0x14e6;
    L_0x1a17:
        r8 = 0;
        goto L_0x150f;
    L_0x1a1b:
        r4 = 4;
        r0 = r75;
        if (r0 != r4) goto L_0x1b08;
    L_0x1a20:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;	 Catch:{ Exception -> 0x0ca9 }
        r7.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r68;
        r7.to_peer = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r98;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.with_my_score;	 Catch:{ Exception -> 0x0ca9 }
        r7.with_my_score = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r98;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.ttl;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1ace;
    L_0x1a39:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r98;
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x0ca9 }
        r6 = -r6;
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0ca9 }
        r35 = r4.getChat(r6);	 Catch:{ Exception -> 0x0ca9 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.from_peer = r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r7.from_peer;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r98;
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x0ca9 }
        r6 = -r6;
        r4.channel_id = r6;	 Catch:{ Exception -> 0x0ca9 }
        if (r35 == 0) goto L_0x1a6c;
    L_0x1a64:
        r4 = r7.from_peer;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r35;
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0ca9 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0ca9 }
    L_0x1a6c:
        r0 = r98;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.to_id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1a9b;
    L_0x1a76:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0ca9 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0ca9 }
        r6.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r8 = "silent_";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r92;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0ca9 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0ca9 }
        r7.silent = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x1a9b:
        r4 = r7.random_id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r57;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0ca9 }
        r6 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0ca9 }
        r4.add(r6);	 Catch:{ Exception -> 0x0ca9 }
        r4 = r98.getId();	 Catch:{ Exception -> 0x0ca9 }
        if (r4 < 0) goto L_0x1ad6;
    L_0x1aae:
        r4 = r7.id;	 Catch:{ Exception -> 0x0ca9 }
        r6 = r98.getId();	 Catch:{ Exception -> 0x0ca9 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0ca9 }
        r4.add(r6);	 Catch:{ Exception -> 0x0ca9 }
    L_0x1abb:
        r25 = 0;
        r26 = 0;
        r22 = r82;
        r23 = r7;
        r24 = r12;
        r27 = r103;
        r22.performSendMessageRequest(r23, r24, r25, r26, r27);	 Catch:{ Exception -> 0x0ca9 }
        r10 = r37;
        goto L_0x0008;
    L_0x1ace:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;	 Catch:{ Exception -> 0x0ca9 }
        r4.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r7.from_peer = r4;	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x1a6c;
    L_0x1ad6:
        r0 = r98;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.fwd_msg_id;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1aee;
    L_0x1ade:
        r4 = r7.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r98;
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.fwd_msg_id;	 Catch:{ Exception -> 0x0ca9 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0ca9 }
        r4.add(r6);	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x1abb;
    L_0x1aee:
        r0 = r98;
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4.fwd_from;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1abb;
    L_0x1af6:
        r4 = r7.id;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r98;
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.fwd_from;	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.channel_post;	 Catch:{ Exception -> 0x0ca9 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0ca9 }
        r4.add(r6);	 Catch:{ Exception -> 0x0ca9 }
        goto L_0x1abb;
    L_0x1b08:
        r4 = 9;
        r0 = r75;
        if (r0 != r4) goto L_0x1ba0;
    L_0x1b0e:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult;	 Catch:{ Exception -> 0x0ca9 }
        r7.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r0 = r68;
        r7.peer = r0;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r57;
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0ca9 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r57;
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1b2f;
    L_0x1b23:
        r4 = r7.flags;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 | 1;
        r7.flags = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r57;
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0ca9 }
        r7.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x1b2f:
        r0 = r57;
        r4 = r0.to_id;	 Catch:{ Exception -> 0x0ca9 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0ca9 }
        if (r4 == 0) goto L_0x1b5c;
    L_0x1b37:
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0ca9 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0ca9 }
        r6.<init>();	 Catch:{ Exception -> 0x0ca9 }
        r8 = "silent_";
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0ca9 }
        r0 = r92;
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0ca9 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0ca9 }
        r8 = 0;
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0ca9 }
        r7.silent = r4;	 Catch:{ Exception -> 0x0ca9 }
    L_0x1b5c:
        r4 = "query_id";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0ca9 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.Utilities.parseLong(r4);	 Catch:{ Exception -> 0x0ca9 }
        r8 = r4.longValue();	 Catch:{ Exception -> 0x0ca9 }
        r7.query_id = r8;	 Catch:{ Exception -> 0x0ca9 }
        r4 = "id";
        r0 = r101;
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0ca9 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0ca9 }
        r7.id = r4;	 Catch:{ Exception -> 0x0ca9 }
        if (r98 != 0) goto L_0x1b91;
    L_0x1b80:
        r4 = 1;
        r7.clear_draft = r4;	 Catch:{ Exception -> 0x0ca9 }
        r0 = r82;
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0ca9 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0ca9 }
        r6 = 0;
        r0 = r92;
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0ca9 }
    L_0x1b91:
        r25 = 0;
        r26 = 0;
        r22 = r82;
        r23 = r7;
        r24 = r12;
        r27 = r103;
        r22.performSendMessageRequest(r23, r24, r25, r26, r27);	 Catch:{ Exception -> 0x0ca9 }
    L_0x1ba0:
        r10 = r37;
        goto L_0x0008;
    L_0x1ba4:
        r39 = move-exception;
        r12 = r59;
        r88 = r79;
        goto L_0x021d;
    L_0x1bab:
        r39 = move-exception;
        r12 = r59;
        r57 = r58;
        goto L_0x021d;
    L_0x1bb2:
        r10 = r37;
        goto L_0x173b;
    L_0x1bb6:
        r10 = r37;
        goto L_0x15e6;
    L_0x1bba:
        r10 = r37;
        goto L_0x0d2f;
    L_0x1bbe:
        r10 = r37;
        goto L_0x0f0e;
    L_0x1bc2:
        r10 = r37;
        goto L_0x0e15;
    L_0x1bc6:
        r10 = r37;
        goto L_0x0b27;
    L_0x1bca:
        r37 = r10;
        goto L_0x0b13;
    L_0x1bce:
        r57 = r58;
        goto L_0x03c2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, org.telegram.tgnet.TLRPC$TL_messageMediaPoll, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, int, java.lang.Object):void");
    }

    private void performSendDelayedMessage(DelayedMessage message) {
        performSendDelayedMessage(message, -1);
    }

    private PhotoSize getThumbForSecretChat(ArrayList<PhotoSize> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        int N = arrayList.size();
        for (int a = 0; a < N; a++) {
            PhotoSize size = (PhotoSize) arrayList.get(a);
            if (size != null && !(size instanceof TL_photoStrippedSize)) {
                return size;
            }
        }
        return null;
    }

    private void performSendDelayedMessage(DelayedMessage message, int index) {
        String location;
        InputMedia media;
        Document document;
        if (message.type == 0) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "file", this.currentAccount);
            } else if (message.sendRequest != null) {
                location = FileLoader.getPathToAttach(message.location).toString();
                putToDelayedMessages(location, message);
                FileLoader.getInstance(this.currentAccount).uploadFile(location, false, true, 16777216);
            } else {
                location = FileLoader.getPathToAttach(message.location).toString();
                if (!(message.sendEncryptedRequest == null || message.location.dc_id == 0)) {
                    File file = new File(location);
                    if (!file.exists()) {
                        location = FileLoader.getPathToAttach(message.location, true).toString();
                        file = new File(location);
                    }
                    if (!file.exists()) {
                        putToDelayedMessages(FileLoader.getAttachFileName(message.location), message);
                        FileLoader.getInstance(this.currentAccount).loadFile(message.location, message, "jpg", 0, 2, 0);
                        return;
                    }
                }
                putToDelayedMessages(location, message);
                FileLoader.getInstance(this.currentAccount).uploadFile(location, true, true, 16777216);
            }
        } else if (message.type == 1) {
            if (message.videoEditedInfo == null || !message.videoEditedInfo.needConvert()) {
                if (message.videoEditedInfo != null) {
                    if (message.videoEditedInfo.file != null) {
                        if (message.sendRequest instanceof TL_messages_sendMedia) {
                            media = ((TL_messages_sendMedia) message.sendRequest).media;
                        } else if (message.sendRequest instanceof TL_messages_editMessage) {
                            media = ((TL_messages_editMessage) message.sendRequest).media;
                        } else {
                            media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                        }
                        media.file = message.videoEditedInfo.file;
                        message.videoEditedInfo.file = null;
                    } else if (message.videoEditedInfo.encryptedFile != null) {
                        TL_decryptedMessage decryptedMessage = message.sendEncryptedRequest;
                        decryptedMessage.media.size = (int) message.videoEditedInfo.estimatedSize;
                        decryptedMessage.media.key = message.videoEditedInfo.key;
                        decryptedMessage.media.iv = message.videoEditedInfo.iv;
                        SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(decryptedMessage, message.obj.messageOwner, message.encryptedChat, message.videoEditedInfo.encryptedFile, message.originalPath, message.obj);
                        message.videoEditedInfo.encryptedFile = null;
                        return;
                    }
                }
                if (message.sendRequest != null) {
                    if (message.sendRequest instanceof TL_messages_sendMedia) {
                        media = ((TL_messages_sendMedia) message.sendRequest).media;
                    } else if (message.sendRequest instanceof TL_messages_editMessage) {
                        media = ((TL_messages_editMessage) message.sendRequest).media;
                    } else {
                        media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                    }
                    if (media.file == null) {
                        location = message.obj.messageOwner.attachPath;
                        document = message.obj.getDocument();
                        if (location == null) {
                            location = FileLoader.getDirectory(4) + "/" + document.id + ".mp4";
                        }
                        putToDelayedMessages(location, message);
                        if (message.obj.videoEditedInfo == null || !message.obj.videoEditedInfo.needConvert()) {
                            FileLoader.getInstance(this.currentAccount).uploadFile(location, false, false, 33554432);
                            return;
                        } else {
                            FileLoader.getInstance(this.currentAccount).uploadFile(location, false, false, document.size, 33554432);
                            return;
                        }
                    }
                    location = FileLoader.getDirectory(4) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance(this.currentAccount).uploadFile(location, false, true, 16777216);
                    return;
                }
                location = message.obj.messageOwner.attachPath;
                document = message.obj.getDocument();
                if (location == null) {
                    location = FileLoader.getDirectory(4) + "/" + document.id + ".mp4";
                }
                if (message.sendEncryptedRequest == null || document.dc_id == 0 || new File(location).exists()) {
                    putToDelayedMessages(location, message);
                    if (message.obj.videoEditedInfo == null || !message.obj.videoEditedInfo.needConvert()) {
                        FileLoader.getInstance(this.currentAccount).uploadFile(location, true, false, 33554432);
                        return;
                    } else {
                        FileLoader.getInstance(this.currentAccount).uploadFile(location, true, false, document.size, 33554432);
                        return;
                    }
                }
                putToDelayedMessages(FileLoader.getAttachFileName(document), message);
                FileLoader.getInstance(this.currentAccount).loadFile(document, message, 2, 0);
                return;
            }
            location = message.obj.messageOwner.attachPath;
            document = message.obj.getDocument();
            if (location == null) {
                location = FileLoader.getDirectory(4) + "/" + document.id + ".mp4";
            }
            putToDelayedMessages(location, message);
            MediaController.getInstance().scheduleVideoConvert(message.obj);
        } else if (message.type == 2) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "gif", this.currentAccount);
            } else if (message.sendRequest != null) {
                if (message.sendRequest instanceof TL_messages_sendMedia) {
                    media = ((TL_messages_sendMedia) message.sendRequest).media;
                } else if (message.sendRequest instanceof TL_messages_editMessage) {
                    media = ((TL_messages_editMessage) message.sendRequest).media;
                } else {
                    media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                }
                if (media.file == null) {
                    boolean z;
                    location = message.obj.messageOwner.attachPath;
                    putToDelayedMessages(location, message);
                    FileLoader instance = FileLoader.getInstance(this.currentAccount);
                    if (message.sendRequest == null) {
                        z = true;
                    } else {
                        z = false;
                    }
                    instance.uploadFile(location, z, false, 67108864);
                } else if (media.thumb == null && message.location != null) {
                    location = FileLoader.getDirectory(4) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance(this.currentAccount).uploadFile(location, false, true, 16777216);
                }
            } else {
                location = message.obj.messageOwner.attachPath;
                document = message.obj.getDocument();
                if (message.sendEncryptedRequest == null || document.dc_id == 0 || new File(location).exists()) {
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance(this.currentAccount).uploadFile(location, true, false, 67108864);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(document), message);
                FileLoader.getInstance(this.currentAccount).loadFile(document, message, 2, 0);
            }
        } else if (message.type == 3) {
            location = message.obj.messageOwner.attachPath;
            putToDelayedMessages(location, message);
            FileLoader.getInstance(this.currentAccount).uploadFile(location, message.sendRequest == null, true, 50331648);
        } else if (message.type == 4) {
            boolean add = index < 0;
            if (message.performMediaUpload) {
                if (index < 0) {
                    index = message.messageObjects.size() - 1;
                }
                MessageObject messageObject = (MessageObject) message.messageObjects.get(index);
                if (messageObject.getDocument() != null) {
                    if (message.videoEditedInfo != null) {
                        location = messageObject.messageOwner.attachPath;
                        document = messageObject.getDocument();
                        if (location == null) {
                            location = FileLoader.getDirectory(4) + "/" + document.id + ".mp4";
                        }
                        putToDelayedMessages(location, message);
                        message.extraHashMap.put(messageObject, location);
                        message.extraHashMap.put(location + "_i", messageObject);
                        if (message.location != null) {
                            message.extraHashMap.put(location + "_t", message.location);
                        }
                        MediaController.getInstance().scheduleVideoConvert(messageObject);
                    } else {
                        document = messageObject.getDocument();
                        String documentLocation = messageObject.messageOwner.attachPath;
                        if (documentLocation == null) {
                            documentLocation = FileLoader.getDirectory(4) + "/" + document.id + ".mp4";
                        }
                        if (message.sendRequest != null) {
                            media = ((TL_inputSingleMedia) ((TL_messages_sendMultiMedia) message.sendRequest).multi_media.get(index)).media;
                            if (media.file == null) {
                                putToDelayedMessages(documentLocation, message);
                                message.extraHashMap.put(messageObject, documentLocation);
                                message.extraHashMap.put(documentLocation, media);
                                message.extraHashMap.put(documentLocation + "_i", messageObject);
                                if (message.location != null) {
                                    message.extraHashMap.put(documentLocation + "_t", message.location);
                                }
                                if (messageObject.videoEditedInfo == null || !messageObject.videoEditedInfo.needConvert()) {
                                    FileLoader.getInstance(this.currentAccount).uploadFile(documentLocation, false, false, 33554432);
                                } else {
                                    FileLoader.getInstance(this.currentAccount).uploadFile(documentLocation, false, false, document.size, 33554432);
                                }
                            } else {
                                location = FileLoader.getDirectory(4) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                                putToDelayedMessages(location, message);
                                message.extraHashMap.put(location + "_o", documentLocation);
                                message.extraHashMap.put(messageObject, location);
                                message.extraHashMap.put(location, media);
                                FileLoader.getInstance(this.currentAccount).uploadFile(location, false, true, 16777216);
                            }
                        } else {
                            TL_messages_sendEncryptedMultiMedia request = (TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest;
                            putToDelayedMessages(documentLocation, message);
                            message.extraHashMap.put(messageObject, documentLocation);
                            message.extraHashMap.put(documentLocation, request.files.get(index));
                            message.extraHashMap.put(documentLocation + "_i", messageObject);
                            if (message.location != null) {
                                message.extraHashMap.put(documentLocation + "_t", message.location);
                            }
                            if (messageObject.videoEditedInfo == null || !messageObject.videoEditedInfo.needConvert()) {
                                FileLoader.getInstance(this.currentAccount).uploadFile(documentLocation, true, false, 33554432);
                            } else {
                                FileLoader.getInstance(this.currentAccount).uploadFile(documentLocation, true, false, document.size, 33554432);
                            }
                        }
                    }
                    message.videoEditedInfo = null;
                    message.location = null;
                } else if (message.httpLocation != null) {
                    putToDelayedMessages(message.httpLocation, message);
                    message.extraHashMap.put(messageObject, message.httpLocation);
                    message.extraHashMap.put(message.httpLocation, messageObject);
                    ImageLoader.getInstance().loadHttpFile(message.httpLocation, "file", this.currentAccount);
                    message.httpLocation = null;
                } else {
                    TLObject inputMedia;
                    if (message.sendRequest != null) {
                        inputMedia = ((TL_inputSingleMedia) ((TL_messages_sendMultiMedia) message.sendRequest).multi_media.get(index)).media;
                    } else {
                        inputMedia = (TLObject) ((TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest).files.get(index);
                    }
                    location = FileLoader.getPathToAttach(message.location).toString();
                    putToDelayedMessages(location, message);
                    message.extraHashMap.put(location, inputMedia);
                    message.extraHashMap.put(messageObject, location);
                    FileLoader.getInstance(this.currentAccount).uploadFile(location, message.sendEncryptedRequest != null, true, 16777216);
                    message.location = null;
                }
                message.performMediaUpload = false;
            } else if (!message.messageObjects.isEmpty()) {
                putToSendingMessages(((MessageObject) message.messageObjects.get(message.messageObjects.size() - 1)).messageOwner);
            }
            sendReadyToSendGroup(message, add, true);
        }
    }

    private void uploadMultiMedia(DelayedMessage message, InputMedia inputMedia, InputEncryptedFile inputEncryptedFile, String key) {
        int a;
        if (inputMedia != null) {
            TL_messages_sendMultiMedia multiMedia = message.sendRequest;
            for (a = 0; a < multiMedia.multi_media.size(); a++) {
                if (((TL_inputSingleMedia) multiMedia.multi_media.get(a)).media == inputMedia) {
                    putToSendingMessages((Message) message.messages.get(a));
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileUploadProgressChanged, key, Float.valueOf(1.0f), Boolean.valueOf(false));
                    break;
                }
            }
            TL_messages_uploadMedia req = new TL_messages_uploadMedia();
            req.media = inputMedia;
            req.peer = ((TL_messages_sendMultiMedia) message.sendRequest).peer;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SendMessagesHelper$$Lambda$9(this, inputMedia, message));
        } else if (inputEncryptedFile != null) {
            TL_messages_sendEncryptedMultiMedia multiMedia2 = message.sendEncryptedRequest;
            for (a = 0; a < multiMedia2.files.size(); a++) {
                if (multiMedia2.files.get(a) == inputEncryptedFile) {
                    putToSendingMessages((Message) message.messages.get(a));
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileUploadProgressChanged, key, Float.valueOf(1.0f), Boolean.valueOf(false));
                    break;
                }
            }
            sendReadyToSendGroup(message, false, true);
        }
    }

    final /* synthetic */ void lambda$uploadMultiMedia$20$SendMessagesHelper(InputMedia inputMedia, DelayedMessage message, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$52(this, response, inputMedia, message));
    }

    final /* synthetic */ void lambda$null$19$SendMessagesHelper(TLObject response, InputMedia inputMedia, DelayedMessage message) {
        InputMedia newInputMedia = null;
        if (response != null) {
            MessageMedia messageMedia = (MessageMedia) response;
            if ((inputMedia instanceof TL_inputMediaUploadedPhoto) && (messageMedia instanceof TL_messageMediaPhoto)) {
                InputMedia inputMediaPhoto = new TL_inputMediaPhoto();
                inputMediaPhoto.id = new TL_inputPhoto();
                inputMediaPhoto.id.id = messageMedia.photo.id;
                inputMediaPhoto.id.access_hash = messageMedia.photo.access_hash;
                inputMediaPhoto.id.file_reference = messageMedia.photo.file_reference;
                newInputMedia = inputMediaPhoto;
            } else if ((inputMedia instanceof TL_inputMediaUploadedDocument) && (messageMedia instanceof TL_messageMediaDocument)) {
                InputMedia inputMediaDocument = new TL_inputMediaDocument();
                inputMediaDocument.id = new TL_inputDocument();
                inputMediaDocument.id.id = messageMedia.document.id;
                inputMediaDocument.id.access_hash = messageMedia.document.access_hash;
                inputMediaDocument.id.file_reference = messageMedia.document.file_reference;
                newInputMedia = inputMediaDocument;
            }
        }
        if (newInputMedia != null) {
            if (inputMedia.ttl_seconds != 0) {
                newInputMedia.ttl_seconds = inputMedia.ttl_seconds;
                newInputMedia.flags |= 1;
            }
            TL_messages_sendMultiMedia req1 = message.sendRequest;
            for (int a = 0; a < req1.multi_media.size(); a++) {
                if (((TL_inputSingleMedia) req1.multi_media.get(a)).media == inputMedia) {
                    ((TL_inputSingleMedia) req1.multi_media.get(a)).media = newInputMedia;
                    break;
                }
            }
            sendReadyToSendGroup(message, false, true);
            return;
        }
        message.markAsError();
    }

    private void sendReadyToSendGroup(DelayedMessage message, boolean add, boolean check) {
        if (message.messageObjects.isEmpty()) {
            message.markAsError();
            return;
        }
        String key = "group_" + message.groupId;
        if (message.finalGroupMessage == ((MessageObject) message.messageObjects.get(message.messageObjects.size() - 1)).getId()) {
            if (add) {
                this.delayedMessages.remove(key);
                MessagesStorage.getInstance(this.currentAccount).putMessages(message.messages, false, true, false, 0);
                MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(message.peer, message.messageObjects);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            int a;
            if (message.sendRequest instanceof TL_messages_sendMultiMedia) {
                TL_messages_sendMultiMedia request = message.sendRequest;
                a = 0;
                while (a < request.multi_media.size()) {
                    InputMedia inputMedia = ((TL_inputSingleMedia) request.multi_media.get(a)).media;
                    if (!(inputMedia instanceof TL_inputMediaUploadedPhoto) && !(inputMedia instanceof TL_inputMediaUploadedDocument)) {
                        a++;
                    } else {
                        return;
                    }
                }
                if (check) {
                    DelayedMessage maxDelayedMessage = findMaxDelayedMessageForMessageId(message.finalGroupMessage, message.peer);
                    if (maxDelayedMessage != null) {
                        maxDelayedMessage.addDelayedRequest(message.sendRequest, message.messageObjects, message.originalPaths, message.parentObjects, message);
                        if (message.requests != null) {
                            maxDelayedMessage.requests.addAll(message.requests);
                            return;
                        }
                        return;
                    }
                }
            }
            TL_messages_sendEncryptedMultiMedia request2 = message.sendEncryptedRequest;
            a = 0;
            while (a < request2.files.size()) {
                if (!(((InputEncryptedFile) request2.files.get(a)) instanceof TL_inputEncryptedFile)) {
                    a++;
                } else {
                    return;
                }
            }
            if (message.sendRequest instanceof TL_messages_sendMultiMedia) {
                performSendMessageRequestMulti((TL_messages_sendMultiMedia) message.sendRequest, message.messageObjects, message.originalPaths, message.parentObjects, message);
            } else {
                SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest((TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest, message);
            }
            message.sendDelayedRequests();
        } else if (add) {
            putToDelayedMessages(key, message);
        }
    }

    final /* synthetic */ void lambda$null$21$SendMessagesHelper(String path) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, path, Integer.valueOf(this.currentAccount));
    }

    final /* synthetic */ void lambda$stopVideoService$22$SendMessagesHelper(String path) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$51(this, path));
    }

    protected void stopVideoService(String path) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$10(this, path));
    }

    protected void putToSendingMessages(Message message) {
        this.sendingMessages.put(message.id, message);
    }

    protected Message removeFromSendingMessages(int mid) {
        Message message = (Message) this.sendingMessages.get(mid);
        if (message != null) {
            this.sendingMessages.remove(mid);
        }
        return message;
    }

    public boolean isSendingMessage(int mid) {
        return this.sendingMessages.indexOfKey(mid) >= 0;
    }

    protected void performSendMessageRequestMulti(TL_messages_sendMultiMedia req, ArrayList<MessageObject> msgObjs, ArrayList<String> originalPaths, ArrayList<Object> parentObjects, DelayedMessage delayedMessage) {
        int size = msgObjs.size();
        for (int a = 0; a < size; a++) {
            putToSendingMessages(((MessageObject) msgObjs.get(a)).messageOwner);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject) req, new SendMessagesHelper$$Lambda$11(this, parentObjects, req, msgObjs, originalPaths, delayedMessage), null, 68);
    }

    final /* synthetic */ void lambda$performSendMessageRequestMulti$29$SendMessagesHelper(ArrayList parentObjects, TL_messages_sendMultiMedia req, ArrayList msgObjs, ArrayList originalPaths, DelayedMessage delayedMessage, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$45(this, error, parentObjects, req, msgObjs, originalPaths, delayedMessage, response));
    }

    final /* synthetic */ void lambda$null$28$SendMessagesHelper(TL_error error, ArrayList parentObjects, TL_messages_sendMultiMedia req, ArrayList msgObjs, ArrayList originalPaths, DelayedMessage delayedMessage, TLObject response) {
        int i;
        Message newMsgObj;
        if (error != null && FileRefController.isFileRefError(error.text)) {
            if (parentObjects != null) {
                FileRefController.getInstance(this.currentAccount).requestReference(arrayList, req, msgObjs, originalPaths, new ArrayList(parentObjects), delayedMessage);
                return;
            } else if (delayedMessage != null) {
                final TL_messages_sendMultiMedia tL_messages_sendMultiMedia = req;
                final DelayedMessage delayedMessage2 = delayedMessage;
                final ArrayList arrayList = msgObjs;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int size = tL_messages_sendMultiMedia.multi_media.size();
                        for (int a = 0; a < size; a++) {
                            if (delayedMessage2.parentObjects.get(a) != null) {
                                SendMessagesHelper.this.removeFromSendingMessages(((MessageObject) arrayList.get(a)).getId());
                                TL_inputSingleMedia request = (TL_inputSingleMedia) tL_messages_sendMultiMedia.multi_media.get(a);
                                if (request.media instanceof TL_inputMediaPhoto) {
                                    request.media = (InputMedia) delayedMessage2.inputMedias.get(a);
                                } else if (request.media instanceof TL_inputMediaDocument) {
                                    request.media = (InputMedia) delayedMessage2.inputMedias.get(a);
                                }
                                delayedMessage2.videoEditedInfo = (VideoEditedInfo) delayedMessage2.videoEditedInfos.get(a);
                                delayedMessage2.httpLocation = (String) delayedMessage2.httpLocations.get(a);
                                delayedMessage2.location = (FileLocation) delayedMessage2.locations.get(a);
                                delayedMessage2.performMediaUpload = true;
                                SendMessagesHelper.this.performSendDelayedMessage(delayedMessage2, a);
                            }
                        }
                    }
                });
                return;
            }
        }
        boolean isSentError = false;
        if (error == null) {
            SparseArray<Message> newMessages = new SparseArray();
            LongSparseArray<Integer> newIds = new LongSparseArray();
            Updates updates = (Updates) response;
            ArrayList<Update> updatesArr = ((Updates) response).updates;
            int a = 0;
            while (a < updatesArr.size()) {
                Update update = (Update) updatesArr.get(a);
                if (update instanceof TL_updateMessageID) {
                    TL_updateMessageID updateMessageID = (TL_updateMessageID) update;
                    newIds.put(updateMessageID.random_id, Integer.valueOf(updateMessageID.id));
                    updatesArr.remove(a);
                    a--;
                } else if (update instanceof TL_updateNewMessage) {
                    TL_updateNewMessage newMessage = (TL_updateNewMessage) update;
                    newMessages.put(newMessage.message.id, newMessage.message);
                    Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$46(this, newMessage));
                    updatesArr.remove(a);
                    a--;
                } else if (update instanceof TL_updateNewChannelMessage) {
                    TL_updateNewChannelMessage newMessage2 = (TL_updateNewChannelMessage) update;
                    newMessages.put(newMessage2.message.id, newMessage2.message);
                    Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$47(this, newMessage2));
                    updatesArr.remove(a);
                    a--;
                }
                a++;
            }
            for (i = 0; i < msgObjs.size(); i++) {
                MessageObject msgObj = (MessageObject) msgObjs.get(i);
                String originalPath = (String) originalPaths.get(i);
                newMsgObj = msgObj.messageOwner;
                int oldId = newMsgObj.id;
                ArrayList<Message> sentMessages = new ArrayList();
                String attachPath = newMsgObj.attachPath;
                Integer id = (Integer) newIds.get(newMsgObj.random_id);
                if (id == null) {
                    isSentError = true;
                    break;
                }
                Message message = (Message) newMessages.get(id.intValue());
                if (message == null) {
                    isSentError = true;
                    break;
                }
                sentMessages.add(message);
                newMsgObj.id = message.id;
                if ((newMsgObj.flags & Integer.MIN_VALUE) != 0) {
                    message.flags |= Integer.MIN_VALUE;
                }
                long grouped_id = message.grouped_id;
                Integer value = (Integer) MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(message.dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                    MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(message.dialog_id), value);
                }
                message.unread = value.intValue() < message.id;
                updateMediaPaths(msgObj, message, originalPath, false);
                if (null == null) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                    newMsgObj.send_state = 0;
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), Long.valueOf(grouped_id));
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$48(this, newMsgObj, oldId, sentMessages, grouped_id));
                }
            }
            Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$49(this, updates));
        } else {
            AlertsCreator.processError(this.currentAccount, error, null, req, new Object[0]);
            isSentError = true;
        }
        if (isSentError) {
            for (i = 0; i < msgObjs.size(); i++) {
                newMsgObj = ((MessageObject) msgObjs.get(i)).messageOwner;
                MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsgObj);
                newMsgObj.send_state = 2;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
                processSentMessage(newMsgObj.id);
                removeFromSendingMessages(newMsgObj.id);
            }
        }
    }

    final /* synthetic */ void lambda$null$23$SendMessagesHelper(TL_updateNewMessage newMessage) {
        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
    }

    final /* synthetic */ void lambda$null$24$SendMessagesHelper(TL_updateNewChannelMessage newMessage) {
        MessagesController.getInstance(this.currentAccount).processNewChannelDifferenceParams(newMessage.pts, newMessage.pts_count, newMessage.message.to_id.channel_id);
    }

    final /* synthetic */ void lambda$null$26$SendMessagesHelper(Message newMsgObj, int oldId, ArrayList sentMessages, long grouped_id) {
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(oldId), newMsgObj.id, 0, false, newMsgObj.to_id.channel_id);
        MessagesStorage.getInstance(this.currentAccount).putMessages(sentMessages, true, false, false, 0);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$50(this, newMsgObj, oldId, grouped_id));
    }

    final /* synthetic */ void lambda$null$25$SendMessagesHelper(Message newMsgObj, int oldId, long grouped_id) {
        DataQuery.getInstance(this.currentAccount).increasePeerRaiting(newMsgObj.dialog_id);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), Long.valueOf(grouped_id));
        processSentMessage(oldId);
        removeFromSendingMessages(oldId);
    }

    final /* synthetic */ void lambda$null$27$SendMessagesHelper(Updates updates) {
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
    }

    private void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath, DelayedMessage delayedMessage, Object parentObject) {
        performSendMessageRequest(req, msgObj, originalPath, null, false, delayedMessage, parentObject);
    }

    private DelayedMessage findMaxDelayedMessageForMessageId(int messageId, long dialogId) {
        DelayedMessage maxDelayedMessage = null;
        int maxDalyedMessageId = Integer.MIN_VALUE;
        for (Entry<String, ArrayList<DelayedMessage>> entry : this.delayedMessages.entrySet()) {
            ArrayList<DelayedMessage> messages = (ArrayList) entry.getValue();
            int size = messages.size();
            for (int a = 0; a < size; a++) {
                DelayedMessage delayedMessage = (DelayedMessage) messages.get(a);
                if ((delayedMessage.type == 4 || delayedMessage.type == 0) && delayedMessage.peer == dialogId) {
                    int mid = 0;
                    if (delayedMessage.obj != null) {
                        mid = delayedMessage.obj.getId();
                    } else if (!(delayedMessage.messageObjects == null || delayedMessage.messageObjects.isEmpty())) {
                        mid = ((MessageObject) delayedMessage.messageObjects.get(delayedMessage.messageObjects.size() - 1)).getId();
                    }
                    if (mid != 0 && mid > messageId && maxDelayedMessage == null && maxDalyedMessageId < mid) {
                        maxDelayedMessage = delayedMessage;
                        maxDalyedMessageId = mid;
                    }
                }
            }
        }
        return maxDelayedMessage;
    }

    protected void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath, DelayedMessage parentMessage, boolean check, DelayedMessage delayedMessage, Object parentObject) {
        int i;
        if (!(req instanceof TL_messages_editMessage) && check) {
            DelayedMessage maxDelayedMessage = findMaxDelayedMessageForMessageId(msgObj.getId(), msgObj.getDialogId());
            if (maxDelayedMessage != null) {
                maxDelayedMessage.addDelayedRequest(req, msgObj, originalPath, parentObject, delayedMessage);
                if (parentMessage != null && parentMessage.requests != null) {
                    maxDelayedMessage.requests.addAll(parentMessage.requests);
                    return;
                }
                return;
            }
        }
        Message newMsgObj = msgObj.messageOwner;
        putToSendingMessages(newMsgObj);
        ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
        RequestDelegate sendMessagesHelper$$Lambda$12 = new SendMessagesHelper$$Lambda$12(this, req, parentObject, msgObj, originalPath, parentMessage, check, delayedMessage, newMsgObj);
        QuickAckDelegate sendMessagesHelper$$Lambda$13 = new SendMessagesHelper$$Lambda$13(this, newMsgObj);
        if (req instanceof TL_messages_sendMessage) {
            i = 128;
        } else {
            i = 0;
        }
        newMsgObj.reqId = instance.sendRequest(req, sendMessagesHelper$$Lambda$12, sendMessagesHelper$$Lambda$13, i | 68);
        if (parentMessage != null) {
            parentMessage.sendDelayedRequests();
        }
    }

    final /* synthetic */ void lambda$performSendMessageRequest$40$SendMessagesHelper(final TLObject req, Object parentObject, MessageObject msgObj, String originalPath, DelayedMessage parentMessage, boolean check, DelayedMessage delayedMessage, Message newMsgObj, TLObject response, TL_error error) {
        if (error != null && (((req instanceof TL_messages_sendMedia) || (req instanceof TL_messages_editMessage)) && FileRefController.isFileRefError(error.text))) {
            if (parentObject != null) {
                FileRefController.getInstance(this.currentAccount).requestReference(parentObject, req, msgObj, originalPath, parentMessage, Boolean.valueOf(check), delayedMessage);
                return;
            } else if (delayedMessage != null) {
                final Message message = newMsgObj;
                final DelayedMessage delayedMessage2 = delayedMessage;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        SendMessagesHelper.this.removeFromSendingMessages(message.id);
                        if (req instanceof TL_messages_sendMedia) {
                            TL_messages_sendMedia request = req;
                            if (request.media instanceof TL_inputMediaPhoto) {
                                request.media = delayedMessage2.inputUploadMedia;
                            } else if (request.media instanceof TL_inputMediaDocument) {
                                request.media = delayedMessage2.inputUploadMedia;
                            }
                        } else if (req instanceof TL_messages_editMessage) {
                            TL_messages_editMessage request2 = req;
                            if (request2.media instanceof TL_inputMediaPhoto) {
                                request2.media = delayedMessage2.inputUploadMedia;
                            } else if (request2.media instanceof TL_inputMediaDocument) {
                                request2.media = delayedMessage2.inputUploadMedia;
                            }
                        }
                        delayedMessage2.performMediaUpload = true;
                        SendMessagesHelper.this.performSendDelayedMessage(delayedMessage2);
                    }
                });
                return;
            }
        }
        if (req instanceof TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$35(this, error, newMsgObj, response, msgObj, originalPath, req));
        } else {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$36(this, error, newMsgObj, req, response, msgObj, originalPath));
        }
    }

    final /* synthetic */ void lambda$null$32$SendMessagesHelper(TL_error error, Message newMsgObj, TLObject response, MessageObject msgObj, String originalPath, TLObject req) {
        if (error == null) {
            String attachPath = newMsgObj.attachPath;
            Updates updates = (Updates) response;
            ArrayList<Update> updatesArr = ((Updates) response).updates;
            Message message = null;
            int a = 0;
            while (a < updatesArr.size()) {
                Update update = (Update) updatesArr.get(a);
                if (update instanceof TL_updateEditMessage) {
                    message = ((TL_updateEditMessage) update).message;
                    break;
                } else if (update instanceof TL_updateEditChannelMessage) {
                    message = ((TL_updateEditChannelMessage) update).message;
                    break;
                } else {
                    a++;
                }
            }
            if (message != null) {
                ImageLoader.saveMessageThumbs(message);
                updateMediaPaths(msgObj, message, originalPath, false);
            }
            Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$43(this, updates, newMsgObj));
            if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                stopVideoService(attachPath);
                return;
            }
            return;
        }
        AlertsCreator.processError(this.currentAccount, error, null, req, new Object[0]);
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
            stopVideoService(newMsgObj.attachPath);
        }
        removeFromSendingMessages(newMsgObj.id);
        revertEditingMessageObject(msgObj);
    }

    final /* synthetic */ void lambda$null$31$SendMessagesHelper(Updates updates, Message newMsgObj) {
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$44(this, newMsgObj));
    }

    final /* synthetic */ void lambda$null$30$SendMessagesHelper(Message newMsgObj) {
        processSentMessage(newMsgObj.id);
        removeFromSendingMessages(newMsgObj.id);
    }

    final /* synthetic */ void lambda$null$39$SendMessagesHelper(TL_error error, Message newMsgObj, TLObject req, TLObject response, MessageObject msgObj, String originalPath) {
        boolean isSentError = false;
        if (error == null) {
            int i;
            int oldId = newMsgObj.id;
            boolean isBroadcast = req instanceof TL_messages_sendBroadcast;
            ArrayList<Message> sentMessages = new ArrayList();
            String attachPath = newMsgObj.attachPath;
            if (response instanceof TL_updateShortSentMessage) {
                TL_updateShortSentMessage res = (TL_updateShortSentMessage) response;
                i = res.id;
                newMsgObj.id = i;
                newMsgObj.local_id = i;
                newMsgObj.date = res.date;
                newMsgObj.entities = res.entities;
                newMsgObj.out = res.out;
                if (res.media != null) {
                    newMsgObj.media = res.media;
                    newMsgObj.flags |= 512;
                    ImageLoader.saveMessageThumbs(newMsgObj);
                }
                if ((res.media instanceof TL_messageMediaGame) && !TextUtils.isEmpty(res.message)) {
                    newMsgObj.message = res.message;
                }
                if (!newMsgObj.entities.isEmpty()) {
                    newMsgObj.flags |= 128;
                }
                Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$37(this, res));
                sentMessages.add(newMsgObj);
            } else if (response instanceof Updates) {
                Updates updates = (Updates) response;
                ArrayList<Update> updatesArr = ((Updates) response).updates;
                Message message = null;
                int a = 0;
                while (a < updatesArr.size()) {
                    Update update = (Update) updatesArr.get(a);
                    if (update instanceof TL_updateNewMessage) {
                        TL_updateNewMessage newMessage = (TL_updateNewMessage) update;
                        message = newMessage.message;
                        sentMessages.add(message);
                        Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$38(this, newMessage));
                        updatesArr.remove(a);
                        break;
                    } else if (update instanceof TL_updateNewChannelMessage) {
                        TL_updateNewChannelMessage newMessage2 = (TL_updateNewChannelMessage) update;
                        message = newMessage2.message;
                        sentMessages.add(message);
                        if ((newMsgObj.flags & Integer.MIN_VALUE) != 0) {
                            Message message2 = newMessage2.message;
                            message2.flags |= Integer.MIN_VALUE;
                        }
                        Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$39(this, newMessage2));
                        updatesArr.remove(a);
                    } else {
                        a++;
                    }
                }
                if (message != null) {
                    ImageLoader.saveMessageThumbs(message);
                    Integer value = (Integer) MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(message.dialog_id));
                    if (value == null) {
                        value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                        MessagesController.getInstance(this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(message.dialog_id), value);
                    }
                    message.unread = value.intValue() < message.id;
                    newMsgObj.id = message.id;
                    updateMediaPaths(msgObj, message, originalPath, false);
                } else {
                    isSentError = true;
                }
                Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$40(this, updates));
            }
            if (MessageObject.isLiveLocationMessage(newMsgObj)) {
                LocationController.getInstance(this.currentAccount).addSharingLocation(newMsgObj.dialog_id, newMsgObj.id, newMsgObj.media.period, newMsgObj);
            }
            if (!isSentError) {
                StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                newMsgObj.send_state = 0;
                NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
                int i2 = NotificationCenter.messageReceivedByServer;
                Object[] objArr = new Object[5];
                objArr[0] = Integer.valueOf(oldId);
                if (isBroadcast) {
                    i = oldId;
                } else {
                    i = newMsgObj.id;
                }
                objArr[1] = Integer.valueOf(i);
                objArr[2] = newMsgObj;
                objArr[3] = Long.valueOf(newMsgObj.dialog_id);
                objArr[4] = Long.valueOf(0);
                instance.postNotificationName(i2, objArr);
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$41(this, newMsgObj, oldId, isBroadcast, sentMessages, attachPath));
            }
        } else {
            AlertsCreator.processError(this.currentAccount, error, null, req, new Object[0]);
            isSentError = true;
        }
        if (isSentError) {
            MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsgObj);
            newMsgObj.send_state = 2;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
            processSentMessage(newMsgObj.id);
            if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                stopVideoService(newMsgObj.attachPath);
            }
            removeFromSendingMessages(newMsgObj.id);
        }
    }

    final /* synthetic */ void lambda$null$33$SendMessagesHelper(TL_updateShortSentMessage res) {
        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, res.pts, res.date, res.pts_count);
    }

    final /* synthetic */ void lambda$null$34$SendMessagesHelper(TL_updateNewMessage newMessage) {
        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
    }

    final /* synthetic */ void lambda$null$35$SendMessagesHelper(TL_updateNewChannelMessage newMessage) {
        MessagesController.getInstance(this.currentAccount).processNewChannelDifferenceParams(newMessage.pts, newMessage.pts_count, newMessage.message.to_id.channel_id);
    }

    final /* synthetic */ void lambda$null$36$SendMessagesHelper(Updates updates) {
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
    }

    final /* synthetic */ void lambda$null$38$SendMessagesHelper(Message newMsgObj, int oldId, boolean isBroadcast, ArrayList sentMessages, String attachPath) {
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(oldId), isBroadcast ? oldId : newMsgObj.id, 0, false, newMsgObj.to_id.channel_id);
        MessagesStorage.getInstance(this.currentAccount).putMessages(sentMessages, true, false, isBroadcast, 0);
        if (isBroadcast) {
            ArrayList currentMessage = new ArrayList();
            currentMessage.add(newMsgObj);
            MessagesStorage.getInstance(this.currentAccount).putMessages(currentMessage, true, false, false, 0);
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$42(this, isBroadcast, sentMessages, newMsgObj, oldId));
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
            stopVideoService(attachPath);
        }
    }

    final /* synthetic */ void lambda$null$37$SendMessagesHelper(boolean isBroadcast, ArrayList sentMessages, Message newMsgObj, int oldId) {
        if (isBroadcast) {
            for (int a = 0; a < sentMessages.size(); a++) {
                Message message = (Message) sentMessages.get(a);
                ArrayList<MessageObject> arr = new ArrayList();
                MessageObject messageObject = new MessageObject(this.currentAccount, message, false);
                arr.add(messageObject);
                MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(messageObject.getDialogId(), arr, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        DataQuery.getInstance(this.currentAccount).increasePeerRaiting(newMsgObj.dialog_id);
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        int i = NotificationCenter.messageReceivedByServer;
        Object[] objArr = new Object[5];
        objArr[0] = Integer.valueOf(oldId);
        objArr[1] = Integer.valueOf(isBroadcast ? oldId : newMsgObj.id);
        objArr[2] = newMsgObj;
        objArr[3] = Long.valueOf(newMsgObj.dialog_id);
        objArr[4] = Long.valueOf(0);
        instance.postNotificationName(i, objArr);
        processSentMessage(oldId);
        removeFromSendingMessages(oldId);
    }

    final /* synthetic */ void lambda$performSendMessageRequest$42$SendMessagesHelper(Message newMsgObj) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$34(this, newMsgObj, newMsgObj.id));
    }

    final /* synthetic */ void lambda$null$41$SendMessagesHelper(Message newMsgObj, int msg_id) {
        newMsgObj.send_state = 0;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(msg_id));
    }

    private void updateMediaPaths(MessageObject newMsgObj, Message sentMessage, String originalPath, boolean post) {
        Message newMsg = newMsgObj.messageOwner;
        if (sentMessage != null) {
            int a;
            PhotoSize size;
            PhotoSize size2;
            String fileName;
            String fileName2;
            File cacheFile;
            File cacheFile2;
            if ((sentMessage.media instanceof TL_messageMediaPhoto) && sentMessage.media.photo != null && (newMsg.media instanceof TL_messageMediaPhoto) && newMsg.media.photo != null) {
                if (sentMessage.media.ttl_seconds == 0) {
                    MessagesStorage.getInstance(this.currentAccount).putSentFile(originalPath, sentMessage.media.photo, 0, "sent_" + sentMessage.to_id.channel_id + "_" + sentMessage.id);
                }
                PhotoSize strippedOld = FileLoader.getClosestPhotoSizeWithSize(newMsg.media.photo.sizes, 40);
                TLObject strippedNew = FileLoader.getClosestPhotoSizeWithSize(sentMessage.media.photo.sizes, 40);
                if ((strippedNew instanceof TL_photoStrippedSize) && (strippedOld instanceof TL_photoStrippedSize)) {
                    ImageLoader.getInstance().replaceImageInCache("stripped" + FileRefController.getKeyForParentObject(newMsgObj), "stripped" + FileRefController.getKeyForParentObject(sentMessage), strippedNew, post);
                }
                if (newMsg.media.photo.sizes.size() == 1 && (((PhotoSize) newMsg.media.photo.sizes.get(0)).location instanceof TL_fileLocationUnavailable)) {
                    newMsg.media.photo.sizes = sentMessage.media.photo.sizes;
                } else {
                    for (a = 0; a < sentMessage.media.photo.sizes.size(); a++) {
                        size = (PhotoSize) sentMessage.media.photo.sizes.get(a);
                        if (!(size == null || size.location == null || (size instanceof TL_photoSizeEmpty) || size.type == null)) {
                            int b = 0;
                            while (b < newMsg.media.photo.sizes.size()) {
                                size2 = (PhotoSize) newMsg.media.photo.sizes.get(b);
                                if (size2 == null || size2.location == null || size2.type == null || !((size2.location.volume_id == -2147483648L && size.type.equals(size2.type)) || (size.w == size2.w && size.h == size2.h))) {
                                    b++;
                                } else {
                                    fileName = size2.location.volume_id + "_" + size2.location.local_id;
                                    fileName2 = size.location.volume_id + "_" + size.location.local_id;
                                    if (!fileName.equals(fileName2)) {
                                        cacheFile = new File(FileLoader.getDirectory(4), fileName + ".jpg");
                                        if (sentMessage.media.ttl_seconds != 0 || (sentMessage.media.photo.sizes.size() != 1 && size.w <= 90 && size.h <= 90)) {
                                            cacheFile2 = new File(FileLoader.getDirectory(4), fileName2 + ".jpg");
                                        } else {
                                            cacheFile2 = FileLoader.getPathToAttach(size);
                                        }
                                        cacheFile.renameTo(cacheFile2);
                                        ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, size.location, post);
                                        size2.location = size.location;
                                        size2.size = size.size;
                                    }
                                }
                            }
                        }
                    }
                }
                sentMessage.message = newMsg.message;
                sentMessage.attachPath = newMsg.attachPath;
                newMsg.media.photo.id = sentMessage.media.photo.id;
                newMsg.media.photo.access_hash = sentMessage.media.photo.access_hash;
            } else if ((sentMessage.media instanceof TL_messageMediaDocument) && sentMessage.media.document != null && (newMsg.media instanceof TL_messageMediaDocument) && newMsg.media.document != null) {
                DocumentAttribute attribute;
                if (sentMessage.media.ttl_seconds == 0) {
                    boolean isVideo = MessageObject.isVideoMessage(sentMessage);
                    if ((isVideo || MessageObject.isGifMessage(sentMessage)) && MessageObject.isGifDocument(sentMessage.media.document) == MessageObject.isGifDocument(newMsg.media.document)) {
                        MessagesStorage.getInstance(this.currentAccount).putSentFile(originalPath, sentMessage.media.document, 2, "sent_" + sentMessage.to_id.channel_id + "_" + sentMessage.id);
                        if (isVideo) {
                            sentMessage.attachPath = newMsg.attachPath;
                        }
                    } else if (!(MessageObject.isVoiceMessage(sentMessage) || MessageObject.isRoundVideoMessage(sentMessage))) {
                        MessagesStorage.getInstance(this.currentAccount).putSentFile(originalPath, sentMessage.media.document, 1, "sent_" + sentMessage.to_id.channel_id + "_" + sentMessage.id);
                    }
                }
                size2 = FileLoader.getClosestPhotoSizeWithSize(newMsg.media.document.thumbs, 320);
                size = FileLoader.getClosestPhotoSizeWithSize(sentMessage.media.document.thumbs, 320);
                if (size2 != null && size2.location != null && size2.location.volume_id == -2147483648L && size != null && size.location != null && !(size instanceof TL_photoSizeEmpty) && !(size2 instanceof TL_photoSizeEmpty)) {
                    fileName = size2.location.volume_id + "_" + size2.location.local_id;
                    fileName2 = size.location.volume_id + "_" + size.location.local_id;
                    if (!fileName.equals(fileName2)) {
                        new File(FileLoader.getDirectory(4), fileName + ".jpg").renameTo(new File(FileLoader.getDirectory(4), fileName2 + ".jpg"));
                        ImageLoader.getInstance().replaceImageInCache(fileName, fileName2, size.location, post);
                        size2.location = size.location;
                        size2.size = size.size;
                    }
                } else if (size2 != null && MessageObject.isStickerMessage(sentMessage) && size2.location != null) {
                    size.location = size2.location;
                } else if (size2 == null || ((size2 != null && (size2.location instanceof TL_fileLocationUnavailable)) || (size2 instanceof TL_photoSizeEmpty))) {
                    newMsg.media.document.thumbs = sentMessage.media.document.thumbs;
                }
                newMsg.media.document.dc_id = sentMessage.media.document.dc_id;
                newMsg.media.document.id = sentMessage.media.document.id;
                newMsg.media.document.access_hash = sentMessage.media.document.access_hash;
                byte[] oldWaveform = null;
                for (a = 0; a < newMsg.media.document.attributes.size(); a++) {
                    attribute = (DocumentAttribute) newMsg.media.document.attributes.get(a);
                    if (attribute instanceof TL_documentAttributeAudio) {
                        oldWaveform = attribute.waveform;
                        break;
                    }
                }
                newMsg.media.document.attributes = sentMessage.media.document.attributes;
                if (oldWaveform != null) {
                    for (a = 0; a < newMsg.media.document.attributes.size(); a++) {
                        attribute = (DocumentAttribute) newMsg.media.document.attributes.get(a);
                        if (attribute instanceof TL_documentAttributeAudio) {
                            attribute.waveform = oldWaveform;
                            attribute.flags |= 4;
                        }
                    }
                }
                newMsg.media.document.size = sentMessage.media.document.size;
                newMsg.media.document.mime_type = sentMessage.media.document.mime_type;
                if ((sentMessage.flags & 4) == 0 && MessageObject.isOut(sentMessage)) {
                    if (MessageObject.isNewGifDocument(sentMessage.media.document)) {
                        DataQuery.getInstance(this.currentAccount).addRecentGif(sentMessage.media.document, sentMessage.date);
                    } else if (MessageObject.isStickerDocument(sentMessage.media.document)) {
                        DataQuery.getInstance(this.currentAccount).addRecentSticker(0, sentMessage, sentMessage.media.document, sentMessage.date, false);
                    }
                }
                if (newMsg.attachPath == null || !newMsg.attachPath.startsWith(FileLoader.getDirectory(4).getAbsolutePath())) {
                    sentMessage.attachPath = newMsg.attachPath;
                    sentMessage.message = newMsg.message;
                    return;
                }
                cacheFile = new File(newMsg.attachPath);
                cacheFile2 = FileLoader.getPathToAttach(sentMessage.media.document, sentMessage.media.ttl_seconds != 0);
                if (!cacheFile.renameTo(cacheFile2)) {
                    if (cacheFile.exists()) {
                        sentMessage.attachPath = newMsg.attachPath;
                    } else {
                        newMsgObj.attachPathExists = false;
                    }
                    newMsgObj.mediaExists = cacheFile2.exists();
                    sentMessage.message = newMsg.message;
                } else if (MessageObject.isVideoMessage(sentMessage)) {
                    newMsgObj.attachPathExists = true;
                } else {
                    newMsgObj.mediaExists = newMsgObj.attachPathExists;
                    newMsgObj.attachPathExists = false;
                    newMsg.attachPath = "";
                    if (originalPath != null) {
                        if (originalPath.startsWith("http")) {
                            MessagesStorage.getInstance(this.currentAccount).addRecentLocalFile(originalPath, cacheFile2.toString(), newMsg.media.document);
                        }
                    }
                }
            } else if ((sentMessage.media instanceof TL_messageMediaContact) && (newMsg.media instanceof TL_messageMediaContact)) {
                newMsg.media = sentMessage.media;
            } else if (sentMessage.media instanceof TL_messageMediaWebPage) {
                newMsg.media = sentMessage.media;
            } else if (sentMessage.media instanceof TL_messageMediaGeo) {
                sentMessage.media.geo.lat = newMsg.media.geo.lat;
                sentMessage.media.geo._long = newMsg.media.geo._long;
            } else if (sentMessage.media instanceof TL_messageMediaGame) {
                newMsg.media = sentMessage.media;
                if ((newMsg.media instanceof TL_messageMediaGame) && !TextUtils.isEmpty(sentMessage.message)) {
                    newMsg.entities = sentMessage.entities;
                    newMsg.message = sentMessage.message;
                }
            } else if (sentMessage.media instanceof TL_messageMediaPoll) {
                newMsg.media = sentMessage.media;
            }
        }
    }

    private void putToDelayedMessages(String location, DelayedMessage message) {
        ArrayList<DelayedMessage> arrayList = (ArrayList) this.delayedMessages.get(location);
        if (arrayList == null) {
            arrayList = new ArrayList();
            this.delayedMessages.put(location, arrayList);
        }
        arrayList.add(message);
    }

    protected ArrayList<DelayedMessage> getDelayedMessages(String location) {
        return (ArrayList) this.delayedMessages.get(location);
    }

    protected long getNextRandomId() {
        long val = 0;
        while (val == 0) {
            val = Utilities.random.nextLong();
        }
        return val;
    }

    public void checkUnsentMessages() {
        MessagesStorage.getInstance(this.currentAccount).getUnsentMessages(1000);
    }

    protected void processUnsentMessages(ArrayList<Message> messages, ArrayList<User> users, ArrayList<Chat> chats, ArrayList<EncryptedChat> encryptedChats) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$14(this, users, chats, encryptedChats, messages));
    }

    final /* synthetic */ void lambda$processUnsentMessages$43$SendMessagesHelper(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList messages) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        for (int a = 0; a < messages.size(); a++) {
            retrySendMessage(new MessageObject(this.currentAccount, (Message) messages.get(a), false), true);
        }
    }

    public TL_photo generatePhotoSizes(String path, Uri imageUri) {
        return generatePhotoSizes(null, path, imageUri);
    }

    public TL_photo generatePhotoSizes(TL_photo photo, String path, Uri imageUri) {
        Bitmap bitmap = ImageLoader.loadBitmap(path, imageUri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
        if (bitmap == null) {
            bitmap = ImageLoader.loadBitmap(path, imageUri, 800.0f, 800.0f, true);
        }
        ArrayList<PhotoSize> sizes = new ArrayList();
        PhotoSize size = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, true);
        if (size != null) {
            sizes.add(size);
        }
        size = ImageLoader.scaleAndSaveImage(bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
        if (size != null) {
            sizes.add(size);
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        if (sizes.isEmpty()) {
            return null;
        }
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        if (photo == null) {
            photo = new TL_photo();
        }
        photo.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        photo.sizes = sizes;
        photo.file_reference = new byte[0];
        TL_photo tL_photo = photo;
        return photo;
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x03ff  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x016f  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0470  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02d3  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02de  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x03ff  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x016f  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02d3  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0470  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02de  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x03a3 A:{SYNTHETIC, Splitter: B:163:0x03a3} */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00c4  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0137  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x03ff  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0168  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x016f  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0470  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02d3  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02de  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x03b1 A:{SYNTHETIC, Splitter: B:169:0x03b1} */
    /* JADX WARNING: Missing block: B:139:0x033c, code:
            if (r31.equals("flac") != false) goto L_0x033e;
     */
    private static boolean prepareSendingDocumentInternal(int r51, java.lang.String r52, java.lang.String r53, android.net.Uri r54, java.lang.String r55, long r56, org.telegram.messenger.MessageObject r58, java.lang.CharSequence r59, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r60, org.telegram.messenger.MessageObject r61) {
        /*
        if (r52 == 0) goto L_0x0008;
    L_0x0002:
        r3 = r52.length();
        if (r3 != 0) goto L_0x000c;
    L_0x0008:
        if (r54 != 0) goto L_0x000c;
    L_0x000a:
        r3 = 0;
    L_0x000b:
        return r3;
    L_0x000c:
        if (r54 == 0) goto L_0x0016;
    L_0x000e:
        r3 = org.telegram.messenger.AndroidUtilities.isInternalUri(r54);
        if (r3 == 0) goto L_0x0016;
    L_0x0014:
        r3 = 0;
        goto L_0x000b;
    L_0x0016:
        if (r52 == 0) goto L_0x002b;
    L_0x0018:
        r3 = new java.io.File;
        r0 = r52;
        r3.<init>(r0);
        r3 = android.net.Uri.fromFile(r3);
        r3 = org.telegram.messenger.AndroidUtilities.isInternalUri(r3);
        if (r3 == 0) goto L_0x002b;
    L_0x0029:
        r3 = 0;
        goto L_0x000b;
    L_0x002b:
        r42 = android.webkit.MimeTypeMap.getSingleton();
        r19 = 0;
        r32 = 0;
        if (r54 == 0) goto L_0x0059;
    L_0x0035:
        r36 = 0;
        if (r55 == 0) goto L_0x0041;
    L_0x0039:
        r0 = r42;
        r1 = r55;
        r32 = r0.getExtensionFromMimeType(r1);
    L_0x0041:
        if (r32 != 0) goto L_0x0052;
    L_0x0043:
        r32 = "txt";
    L_0x0046:
        r0 = r54;
        r1 = r32;
        r52 = org.telegram.messenger.MediaController.copyFileToCache(r0, r1);
        if (r52 != 0) goto L_0x0055;
    L_0x0050:
        r3 = 0;
        goto L_0x000b;
    L_0x0052:
        r36 = 1;
        goto L_0x0046;
    L_0x0055:
        if (r36 != 0) goto L_0x0059;
    L_0x0057:
        r32 = 0;
    L_0x0059:
        r33 = new java.io.File;
        r0 = r33;
        r1 = r52;
        r0.<init>(r1);
        r3 = r33.exists();
        if (r3 == 0) goto L_0x0072;
    L_0x0068:
        r6 = r33.length();
        r8 = 0;
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 != 0) goto L_0x0074;
    L_0x0072:
        r3 = 0;
        goto L_0x000b;
    L_0x0074:
        r0 = r56;
        r3 = (int) r0;
        if (r3 != 0) goto L_0x0300;
    L_0x0079:
        r4 = 1;
    L_0x007a:
        if (r4 != 0) goto L_0x0303;
    L_0x007c:
        r2 = 1;
    L_0x007d:
        r43 = r33.getName();
        r30 = "";
        if (r32 == 0) goto L_0x0306;
    L_0x0086:
        r30 = r32;
    L_0x0088:
        r31 = r30.toLowerCase();
        r45 = 0;
        r49 = 0;
        r38 = 0;
        r28 = 0;
        r3 = "mp3";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 != 0) goto L_0x00aa;
    L_0x009f:
        r3 = "m4a";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x031d;
    L_0x00aa:
        r22 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r33);
        if (r22 == 0) goto L_0x00c2;
    L_0x00b0:
        r26 = r22.getDuration();
        r6 = 0;
        r3 = (r26 > r6 ? 1 : (r26 == r6 ? 0 : -1));
        if (r3 == 0) goto L_0x00c2;
    L_0x00ba:
        r45 = r22.getArtist();
        r49 = r22.getTitle();
    L_0x00c2:
        if (r28 == 0) goto L_0x0110;
    L_0x00c4:
        r19 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
        r19.<init>();
        r0 = r28;
        r1 = r19;
        r1.duration = r0;
        r0 = r49;
        r1 = r19;
        r1.title = r0;
        r0 = r45;
        r1 = r19;
        r1.performer = r0;
        r0 = r19;
        r3 = r0.title;
        if (r3 != 0) goto L_0x00e8;
    L_0x00e1:
        r3 = "";
        r0 = r19;
        r0.title = r3;
    L_0x00e8:
        r0 = r19;
        r3 = r0.flags;
        r3 = r3 | 1;
        r0 = r19;
        r0.flags = r3;
        r0 = r19;
        r3 = r0.performer;
        if (r3 != 0) goto L_0x00ff;
    L_0x00f8:
        r3 = "";
        r0 = r19;
        r0.performer = r3;
    L_0x00ff:
        r0 = r19;
        r3 = r0.flags;
        r3 = r3 | 2;
        r0 = r19;
        r0.flags = r3;
        if (r38 == 0) goto L_0x0110;
    L_0x010b:
        r3 = 1;
        r0 = r19;
        r0.voice = r3;
    L_0x0110:
        r46 = 0;
        if (r53 == 0) goto L_0x0121;
    L_0x0114:
        r3 = "attheme";
        r0 = r53;
        r3 = r0.endsWith(r3);
        if (r3 == 0) goto L_0x03ba;
    L_0x011f:
        r46 = 1;
    L_0x0121:
        r5 = 0;
        r44 = 0;
        if (r46 != 0) goto L_0x0183;
    L_0x0126:
        if (r4 != 0) goto L_0x0183;
    L_0x0128:
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r51);
        if (r4 != 0) goto L_0x03fc;
    L_0x012e:
        r3 = 1;
    L_0x012f:
        r0 = r53;
        r47 = r6.getSentFile(r0, r3);
        if (r47 == 0) goto L_0x0141;
    L_0x0137:
        r3 = 0;
        r5 = r47[r3];
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;
        r3 = 1;
        r44 = r47[r3];
        r44 = (java.lang.String) r44;
    L_0x0141:
        if (r5 != 0) goto L_0x0179;
    L_0x0143:
        r3 = r52.equals(r53);
        if (r3 != 0) goto L_0x0179;
    L_0x0149:
        if (r4 != 0) goto L_0x0179;
    L_0x014b:
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r51);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r0 = r52;
        r3 = r3.append(r0);
        r8 = r33.length();
        r3 = r3.append(r8);
        r7 = r3.toString();
        if (r4 != 0) goto L_0x03ff;
    L_0x0168:
        r3 = 1;
    L_0x0169:
        r47 = r6.getSentFile(r7, r3);
        if (r47 == 0) goto L_0x0179;
    L_0x016f:
        r3 = 0;
        r5 = r47[r3];
        r5 = (org.telegram.tgnet.TLRPC.TL_document) r5;
        r3 = 1;
        r44 = r47[r3];
        r44 = (java.lang.String) r44;
    L_0x0179:
        r7 = 0;
        r8 = 0;
        r3 = r51;
        r6 = r52;
        ensureMediaThumbExists(r3, r4, r5, r6, r7, r8);
    L_0x0183:
        if (r5 != 0) goto L_0x02d1;
    L_0x0185:
        r5 = new org.telegram.tgnet.TLRPC$TL_document;
        r5.<init>();
        r6 = 0;
        r5.id = r6;
        r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r51);
        r3 = r3.getCurrentTime();
        r5.date = r3;
        r35 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r35.<init>();
        r0 = r43;
        r1 = r35;
        r1.file_name = r0;
        r3 = 0;
        r3 = new byte[r3];
        r5.file_reference = r3;
        r3 = r5.attributes;
        r0 = r35;
        r3.add(r0);
        r6 = r33.length();
        r3 = (int) r6;
        r5.size = r3;
        r3 = 0;
        r5.dc_id = r3;
        if (r19 == 0) goto L_0x01c2;
    L_0x01bb:
        r3 = r5.attributes;
        r0 = r19;
        r3.add(r0);
    L_0x01c2:
        r3 = r30.length();
        if (r3 == 0) goto L_0x045d;
    L_0x01c8:
        r3 = -1;
        r6 = r31.hashCode();
        switch(r6) {
            case 109967: goto L_0x041e;
            case 3145576: goto L_0x042c;
            case 3418175: goto L_0x0410;
            case 3645340: goto L_0x0402;
            default: goto L_0x01d0;
        };
    L_0x01d0:
        switch(r3) {
            case 0: goto L_0x043a;
            case 1: goto L_0x0441;
            case 2: goto L_0x0448;
            case 3: goto L_0x044f;
            default: goto L_0x01d3;
        };
    L_0x01d3:
        r0 = r42;
        r1 = r31;
        r41 = r0.getMimeTypeFromExtension(r1);
        if (r41 == 0) goto L_0x0456;
    L_0x01dd:
        r0 = r41;
        r5.mime_type = r0;
    L_0x01e1:
        r3 = r5.mime_type;
        r6 = "image/gif";
        r3 = r3.equals(r6);
        if (r3 == 0) goto L_0x0237;
    L_0x01ec:
        if (r61 == 0) goto L_0x01f8;
    L_0x01ee:
        r6 = r61.getGroupIdForUse();
        r8 = 0;
        r3 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r3 != 0) goto L_0x0237;
    L_0x01f8:
        r3 = r33.getAbsolutePath();	 Catch:{ Exception -> 0x0464 }
        r6 = 0;
        r7 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r8 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r9 = 1;
        r23 = org.telegram.messenger.ImageLoader.loadBitmap(r3, r6, r7, r8, r9);	 Catch:{ Exception -> 0x0464 }
        if (r23 == 0) goto L_0x0237;
    L_0x0208:
        r3 = "animation.gif";
        r0 = r35;
        r0.file_name = r3;	 Catch:{ Exception -> 0x0464 }
        r3 = r5.attributes;	 Catch:{ Exception -> 0x0464 }
        r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;	 Catch:{ Exception -> 0x0464 }
        r6.<init>();	 Catch:{ Exception -> 0x0464 }
        r3.add(r6);	 Catch:{ Exception -> 0x0464 }
        r3 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r6 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r7 = 55;
        r0 = r23;
        r48 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r3, r6, r7, r4);	 Catch:{ Exception -> 0x0464 }
        if (r48 == 0) goto L_0x0234;
    L_0x0227:
        r3 = r5.thumbs;	 Catch:{ Exception -> 0x0464 }
        r0 = r48;
        r3.add(r0);	 Catch:{ Exception -> 0x0464 }
        r3 = r5.flags;	 Catch:{ Exception -> 0x0464 }
        r3 = r3 | 1;
        r5.flags = r3;	 Catch:{ Exception -> 0x0464 }
    L_0x0234:
        r23.recycle();	 Catch:{ Exception -> 0x0464 }
    L_0x0237:
        r3 = r5.mime_type;
        r6 = "image/webp";
        r3 = r3.equals(r6);
        if (r3 == 0) goto L_0x02d1;
    L_0x0242:
        if (r2 == 0) goto L_0x02d1;
    L_0x0244:
        if (r61 != 0) goto L_0x02d1;
    L_0x0246:
        r24 = new android.graphics.BitmapFactory$Options;
        r24.<init>();
        r3 = 1;
        r0 = r24;
        r0.inJustDecodeBounds = r3;	 Catch:{ Exception -> 0x046a }
        r34 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x046a }
        r3 = "r";
        r0 = r34;
        r1 = r52;
        r0.<init>(r1, r3);	 Catch:{ Exception -> 0x046a }
        r6 = r34.getChannel();	 Catch:{ Exception -> 0x046a }
        r7 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Exception -> 0x046a }
        r8 = 0;
        r3 = r52.length();	 Catch:{ Exception -> 0x046a }
        r10 = (long) r3;	 Catch:{ Exception -> 0x046a }
        r25 = r6.map(r7, r8, r10);	 Catch:{ Exception -> 0x046a }
        r3 = 0;
        r6 = r25.limit();	 Catch:{ Exception -> 0x046a }
        r7 = 1;
        r0 = r25;
        r1 = r24;
        org.telegram.messenger.Utilities.loadWebpImage(r3, r0, r6, r1, r7);	 Catch:{ Exception -> 0x046a }
        r34.close();	 Catch:{ Exception -> 0x046a }
    L_0x027d:
        r0 = r24;
        r3 = r0.outWidth;
        if (r3 == 0) goto L_0x02d1;
    L_0x0283:
        r0 = r24;
        r3 = r0.outHeight;
        if (r3 == 0) goto L_0x02d1;
    L_0x0289:
        r0 = r24;
        r3 = r0.outWidth;
        r6 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r3 > r6) goto L_0x02d1;
    L_0x0291:
        r0 = r24;
        r3 = r0.outHeight;
        r6 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r3 > r6) goto L_0x02d1;
    L_0x0299:
        r21 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
        r21.<init>();
        r3 = "";
        r0 = r21;
        r0.alt = r3;
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
        r3.<init>();
        r0 = r21;
        r0.stickerset = r3;
        r3 = r5.attributes;
        r0 = r21;
        r3.add(r0);
        r20 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
        r20.<init>();
        r0 = r24;
        r3 = r0.outWidth;
        r0 = r20;
        r0.w = r3;
        r0 = r24;
        r3 = r0.outHeight;
        r0 = r20;
        r0.h = r3;
        r3 = r5.attributes;
        r0 = r20;
        r3.add(r0);
    L_0x02d1:
        if (r59 == 0) goto L_0x0470;
    L_0x02d3:
        r17 = r59.toString();
    L_0x02d7:
        r12 = new java.util.HashMap;
        r12.<init>();
        if (r53 == 0) goto L_0x02e6;
    L_0x02de:
        r3 = "originalPath";
        r0 = r53;
        r12.put(r3, r0);
    L_0x02e6:
        r10 = r5;
        r11 = r52;
        r13 = r44;
        r7 = new org.telegram.messenger.SendMessagesHelper$$Lambda$15;
        r8 = r61;
        r9 = r51;
        r14 = r56;
        r16 = r58;
        r18 = r60;
        r7.<init>(r8, r9, r10, r11, r12, r13, r14, r16, r17, r18);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r7);
        r3 = 1;
        goto L_0x000b;
    L_0x0300:
        r4 = 0;
        goto L_0x007a;
    L_0x0303:
        r2 = 0;
        goto L_0x007d;
    L_0x0306:
        r3 = 46;
        r0 = r52;
        r37 = r0.lastIndexOf(r3);
        r3 = -1;
        r0 = r37;
        if (r0 == r3) goto L_0x0088;
    L_0x0313:
        r3 = r37 + 1;
        r0 = r52;
        r30 = r0.substring(r3);
        goto L_0x0088;
    L_0x031d:
        r3 = "opus";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 != 0) goto L_0x033e;
    L_0x0328:
        r3 = "ogg";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 != 0) goto L_0x033e;
    L_0x0333:
        r3 = "flac";
        r0 = r31;
        r3 = r0.equals(r3);
        if (r3 == 0) goto L_0x00c2;
    L_0x033e:
        r39 = 0;
        r40 = new android.media.MediaMetadataRetriever;	 Catch:{ Exception -> 0x039d }
        r40.<init>();	 Catch:{ Exception -> 0x039d }
        r3 = r33.getAbsolutePath();	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r0 = r40;
        r0.setDataSource(r3);	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r3 = 9;
        r0 = r40;
        r26 = r0.extractMetadata(r3);	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        if (r26 == 0) goto L_0x0376;
    L_0x0358:
        r6 = java.lang.Long.parseLong(r26);	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r3 = (float) r6;	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r3 = r3 / r6;
        r6 = (double) r3;	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r6 = java.lang.Math.ceil(r6);	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r0 = (int) r6;	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r28 = r0;
        r3 = 7;
        r0 = r40;
        r49 = r0.extractMetadata(r3);	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r3 = 2;
        r0 = r40;
        r45 = r0.extractMetadata(r3);	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
    L_0x0376:
        if (r61 != 0) goto L_0x0390;
    L_0x0378:
        r3 = "ogg";
        r0 = r31;
        r3 = r0.equals(r3);	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        if (r3 == 0) goto L_0x0390;
    L_0x0383:
        r3 = r33.getAbsolutePath();	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r3 = org.telegram.messenger.MediaController.isOpusFile(r3);	 Catch:{ Exception -> 0x047a, all -> 0x0475 }
        r6 = 1;
        if (r3 != r6) goto L_0x0390;
    L_0x038e:
        r38 = 1;
    L_0x0390:
        if (r40 == 0) goto L_0x00c2;
    L_0x0392:
        r40.release();	 Catch:{ Exception -> 0x0397 }
        goto L_0x00c2;
    L_0x0397:
        r29 = move-exception;
        org.telegram.messenger.FileLog.e(r29);
        goto L_0x00c2;
    L_0x039d:
        r29 = move-exception;
    L_0x039e:
        org.telegram.messenger.FileLog.e(r29);	 Catch:{ all -> 0x03ae }
        if (r39 == 0) goto L_0x00c2;
    L_0x03a3:
        r39.release();	 Catch:{ Exception -> 0x03a8 }
        goto L_0x00c2;
    L_0x03a8:
        r29 = move-exception;
        org.telegram.messenger.FileLog.e(r29);
        goto L_0x00c2;
    L_0x03ae:
        r3 = move-exception;
    L_0x03af:
        if (r39 == 0) goto L_0x03b4;
    L_0x03b1:
        r39.release();	 Catch:{ Exception -> 0x03b5 }
    L_0x03b4:
        throw r3;
    L_0x03b5:
        r29 = move-exception;
        org.telegram.messenger.FileLog.e(r29);
        goto L_0x03b4;
    L_0x03ba:
        if (r19 == 0) goto L_0x03dc;
    L_0x03bc:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r0 = r53;
        r3 = r3.append(r0);
        r6 = "audio";
        r3 = r3.append(r6);
        r6 = r33.length();
        r3 = r3.append(r6);
        r53 = r3.toString();
        goto L_0x0121;
    L_0x03dc:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r0 = r53;
        r3 = r3.append(r0);
        r6 = "";
        r3 = r3.append(r6);
        r6 = r33.length();
        r3 = r3.append(r6);
        r53 = r3.toString();
        goto L_0x0121;
    L_0x03fc:
        r3 = 4;
        goto L_0x012f;
    L_0x03ff:
        r3 = 4;
        goto L_0x0169;
    L_0x0402:
        r6 = "webp";
        r0 = r31;
        r6 = r0.equals(r6);
        if (r6 == 0) goto L_0x01d0;
    L_0x040d:
        r3 = 0;
        goto L_0x01d0;
    L_0x0410:
        r6 = "opus";
        r0 = r31;
        r6 = r0.equals(r6);
        if (r6 == 0) goto L_0x01d0;
    L_0x041b:
        r3 = 1;
        goto L_0x01d0;
    L_0x041e:
        r6 = "ogg";
        r0 = r31;
        r6 = r0.equals(r6);
        if (r6 == 0) goto L_0x01d0;
    L_0x0429:
        r3 = 2;
        goto L_0x01d0;
    L_0x042c:
        r6 = "flac";
        r0 = r31;
        r6 = r0.equals(r6);
        if (r6 == 0) goto L_0x01d0;
    L_0x0437:
        r3 = 3;
        goto L_0x01d0;
    L_0x043a:
        r3 = "image/webp";
        r5.mime_type = r3;
        goto L_0x01e1;
    L_0x0441:
        r3 = "audio/opus";
        r5.mime_type = r3;
        goto L_0x01e1;
    L_0x0448:
        r3 = "audio/ogg";
        r5.mime_type = r3;
        goto L_0x01e1;
    L_0x044f:
        r3 = "audio/flac";
        r5.mime_type = r3;
        goto L_0x01e1;
    L_0x0456:
        r3 = "application/octet-stream";
        r5.mime_type = r3;
        goto L_0x01e1;
    L_0x045d:
        r3 = "application/octet-stream";
        r5.mime_type = r3;
        goto L_0x01e1;
    L_0x0464:
        r29 = move-exception;
        org.telegram.messenger.FileLog.e(r29);
        goto L_0x0237;
    L_0x046a:
        r29 = move-exception;
        org.telegram.messenger.FileLog.e(r29);
        goto L_0x027d;
    L_0x0470:
        r17 = "";
        goto L_0x02d7;
    L_0x0475:
        r3 = move-exception;
        r39 = r40;
        goto L_0x03af;
    L_0x047a:
        r29 = move-exception;
        r39 = r40;
        goto L_0x039e;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(int, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject):boolean");
    }

    static final /* synthetic */ void lambda$prepareSendingDocumentInternal$44$SendMessagesHelper(MessageObject editingMessageObject, int currentAccount, TL_document documentFinal, String pathFinal, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, String captionFinal, ArrayList entities) {
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, null, documentFinal, pathFinal, params, false, parentFinal);
        } else {
            getInstance(currentAccount).sendMessage(documentFinal, null, pathFinal, dialog_id, reply_to_msg, captionFinal, entities, null, params, 0, parentFinal);
        }
    }

    public static void prepareSendingDocument(String path, String originalPath, Uri uri, String caption, String mine, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, MessageObject editingMessageObject) {
        if ((path != null && originalPath != null) || uri != null) {
            ArrayList<String> paths = new ArrayList();
            ArrayList<String> originalPaths = new ArrayList();
            ArrayList<Uri> uris = null;
            if (uri != null) {
                uris = new ArrayList();
                uris.add(uri);
            }
            if (path != null) {
                paths.add(path);
                originalPaths.add(originalPath);
            }
            prepareSendingDocuments(paths, originalPaths, uris, caption, mine, dialog_id, reply_to_msg, inputContent, editingMessageObject);
        }
    }

    public static void prepareSendingAudioDocuments(ArrayList<MessageObject> messageObjects, long dialog_id, MessageObject reply_to_msg, MessageObject editingMessageObject) {
        new Thread(new SendMessagesHelper$$Lambda$16(messageObjects, dialog_id, UserConfig.selectedAccount, editingMessageObject, reply_to_msg)).start();
    }

    static final /* synthetic */ void lambda$prepareSendingAudioDocuments$46$SendMessagesHelper(ArrayList messageObjects, long dialog_id, int currentAccount, MessageObject editingMessageObject, MessageObject reply_to_msg) {
        int size = messageObjects.size();
        for (int a = 0; a < size; a++) {
            MessageObject messageObject = (MessageObject) messageObjects.get(a);
            String originalPath = messageObject.messageOwner.attachPath;
            File file = new File(originalPath);
            boolean isEncrypted = ((int) dialog_id) == 0;
            if (originalPath != null) {
                originalPath = originalPath + "audio" + file.length();
            }
            TL_document document = null;
            String parentObject = null;
            if (!isEncrypted) {
                Object[] sentData = MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, !isEncrypted ? 1 : 4);
                if (sentData != null) {
                    document = sentData[0];
                    parentObject = sentData[1];
                    ensureMediaThumbExists(currentAccount, isEncrypted, document, originalPath, null, 0);
                }
            }
            if (document == null) {
                document = messageObject.messageOwner.media.document;
            }
            if (isEncrypted) {
                if (MessagesController.getInstance(currentAccount).getEncryptedChat(Integer.valueOf((int) (dialog_id >> 32))) == null) {
                    return;
                }
            }
            HashMap<String, String> params = new HashMap();
            if (originalPath != null) {
                params.put("originalPath", originalPath);
            }
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$33(editingMessageObject, currentAccount, document, messageObject, params, parentObject, dialog_id, reply_to_msg));
        }
    }

    static final /* synthetic */ void lambda$null$45$SendMessagesHelper(MessageObject editingMessageObject, int currentAccount, TL_document documentFinal, MessageObject messageObject, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg) {
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, null, documentFinal, messageObject.messageOwner.attachPath, params, false, parentFinal);
            return;
        }
        getInstance(currentAccount).sendMessage(documentFinal, null, messageObject.messageOwner.attachPath, dialog_id, reply_to_msg, null, null, null, params, 0, parentFinal);
    }

    public static void prepareSendingDocuments(ArrayList<String> paths, ArrayList<String> originalPaths, ArrayList<Uri> uris, String caption, String mime, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, MessageObject editingMessageObject) {
        if (paths != null || originalPaths != null || uris != null) {
            if (paths == null || originalPaths == null || paths.size() == originalPaths.size()) {
                new Thread(new SendMessagesHelper$$Lambda$17(paths, UserConfig.selectedAccount, originalPaths, mime, dialog_id, reply_to_msg, caption, editingMessageObject, uris, inputContent)).start();
            }
        }
    }

    static final /* synthetic */ void lambda$prepareSendingDocuments$48$SendMessagesHelper(ArrayList paths, int currentAccount, ArrayList originalPaths, String mime, long dialog_id, MessageObject reply_to_msg, String caption, MessageObject editingMessageObject, ArrayList uris, InputContentInfoCompat inputContent) {
        int a;
        boolean error = false;
        if (paths != null) {
            for (a = 0; a < paths.size(); a++) {
                if (!prepareSendingDocumentInternal(currentAccount, (String) paths.get(a), (String) originalPaths.get(a), null, mime, dialog_id, reply_to_msg, caption, null, editingMessageObject)) {
                    error = true;
                }
            }
        }
        if (uris != null) {
            for (a = 0; a < uris.size(); a++) {
                if (!prepareSendingDocumentInternal(currentAccount, null, null, (Uri) uris.get(a), mime, dialog_id, reply_to_msg, caption, null, editingMessageObject)) {
                    error = true;
                }
            }
        }
        if (inputContent != null) {
            inputContent.releasePermission();
        }
        if (error) {
            AndroidUtilities.runOnUIThread(SendMessagesHelper$$Lambda$32.$instance);
        }
    }

    static final /* synthetic */ void lambda$null$47$SendMessagesHelper() {
        try {
            Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", R.string.UnsupportedAttachment), 0).show();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static void prepareSendingPhoto(String imageFilePath, Uri imageUri, long dialog_id, MessageObject reply_to_msg, CharSequence caption, ArrayList<MessageEntity> entities, ArrayList<InputDocument> stickers, InputContentInfoCompat inputContent, int ttl, MessageObject editingMessageObject) {
        SendingMediaInfo info = new SendingMediaInfo();
        info.path = imageFilePath;
        info.uri = imageUri;
        if (caption != null) {
            info.caption = caption.toString();
        }
        info.entities = entities;
        info.ttl = ttl;
        if (!(stickers == null || stickers.isEmpty())) {
            info.masks = new ArrayList(stickers);
        }
        ArrayList<SendingMediaInfo> infos = new ArrayList();
        infos.add(info);
        prepareSendingMedia(infos, dialog_id, reply_to_msg, inputContent, false, false, editingMessageObject);
    }

    public static void prepareSendingBotContextResult(BotInlineResult result, HashMap<String, String> params, long dialog_id, MessageObject reply_to_msg) {
        if (result != null) {
            int currentAccount = UserConfig.selectedAccount;
            if (result.send_message instanceof TL_botInlineMessageMediaAuto) {
                new Thread(new SendMessagesHelper$$Lambda$18(dialog_id, result, currentAccount, params, reply_to_msg)).run();
            } else if (result.send_message instanceof TL_botInlineMessageText) {
                boolean z;
                WebPage webPage = null;
                if (((int) dialog_id) == 0) {
                    for (int a = 0; a < result.send_message.entities.size(); a++) {
                        MessageEntity entity = (MessageEntity) result.send_message.entities.get(a);
                        if (entity instanceof TL_messageEntityUrl) {
                            webPage = new TL_webPagePending();
                            webPage.url = result.send_message.message.substring(entity.offset, entity.offset + entity.length);
                            break;
                        }
                    }
                }
                SendMessagesHelper instance = getInstance(currentAccount);
                String str = result.send_message.message;
                if (result.send_message.no_webpage) {
                    z = false;
                } else {
                    z = true;
                }
                instance.sendMessage(str, dialog_id, reply_to_msg, webPage, z, result.send_message.entities, result.send_message.reply_markup, params);
            } else if (result.send_message instanceof TL_botInlineMessageMediaVenue) {
                MessageMedia venue = new TL_messageMediaVenue();
                venue.geo = result.send_message.geo;
                venue.address = result.send_message.address;
                venue.title = result.send_message.title;
                venue.provider = result.send_message.provider;
                venue.venue_id = result.send_message.venue_id;
                String str2 = result.send_message.venue_type;
                venue.venue_id = str2;
                venue.venue_type = str2;
                if (venue.venue_type == null) {
                    venue.venue_type = "";
                }
                getInstance(currentAccount).sendMessage(venue, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            } else if (result.send_message instanceof TL_botInlineMessageMediaGeo) {
                MessageMedia location;
                if (result.send_message.period != 0) {
                    location = new TL_messageMediaGeoLive();
                    location.period = result.send_message.period;
                    location.geo = result.send_message.geo;
                    getInstance(currentAccount).sendMessage(location, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
                    return;
                }
                location = new TL_messageMediaGeo();
                location.geo = result.send_message.geo;
                getInstance(currentAccount).sendMessage(location, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            } else if (result.send_message instanceof TL_botInlineMessageMediaContact) {
                User user = new TL_user();
                user.phone = result.send_message.phone_number;
                user.first_name = result.send_message.first_name;
                user.last_name = result.send_message.last_name;
                user.restriction_reason = result.send_message.vcard;
                getInstance(currentAccount).sendMessage(user, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            }
        }
    }

    static final /* synthetic */ void lambda$prepareSendingBotContextResult$50$SendMessagesHelper(long dialog_id, BotInlineResult result, int currentAccount, HashMap params, MessageObject reply_to_msg) {
        boolean isEncrypted = ((int) dialog_id) == 0;
        String finalPath = null;
        TL_document document = null;
        TL_photo photo = null;
        TL_game game = null;
        if (!(result instanceof TL_botInlineMediaResult)) {
            if (result.content != null) {
                File file = new File(FileLoader.getDirectory(4), Utilities.MD5(result.content.url) + "." + ImageLoader.getHttpUrlExtension(result.content.url, "file"));
                if (file.exists()) {
                    finalPath = file.getAbsolutePath();
                } else {
                    finalPath = result.content.url;
                }
                String str = result.type;
                Object obj = -1;
                switch (str.hashCode()) {
                    case -1890252483:
                        if (str.equals("sticker")) {
                            obj = 4;
                            break;
                        }
                        break;
                    case 102340:
                        if (str.equals("gif")) {
                            obj = 5;
                            break;
                        }
                        break;
                    case 3143036:
                        if (str.equals("file")) {
                            obj = 2;
                            break;
                        }
                        break;
                    case 93166550:
                        if (str.equals("audio")) {
                            obj = null;
                            break;
                        }
                        break;
                    case 106642994:
                        if (str.equals("photo")) {
                            obj = 6;
                            break;
                        }
                        break;
                    case 112202875:
                        if (str.equals("video")) {
                            obj = 3;
                            break;
                        }
                        break;
                    case 112386354:
                        if (str.equals("voice")) {
                            obj = 1;
                            break;
                        }
                        break;
                }
                int[] wh;
                switch (obj) {
                    case null:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        PhotoSize thumb;
                        document = new TL_document();
                        document.id = 0;
                        document.size = 0;
                        document.dc_id = 0;
                        document.mime_type = result.content.mime_type;
                        document.file_reference = new byte[0];
                        document.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                        TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                        document.attributes.add(fileName);
                        str = result.type;
                        obj = -1;
                        switch (str.hashCode()) {
                            case -1890252483:
                                if (str.equals("sticker")) {
                                    obj = 5;
                                    break;
                                }
                                break;
                            case 102340:
                                if (str.equals("gif")) {
                                    obj = null;
                                    break;
                                }
                                break;
                            case 3143036:
                                if (str.equals("file")) {
                                    obj = 3;
                                    break;
                                }
                                break;
                            case 93166550:
                                if (str.equals("audio")) {
                                    obj = 2;
                                    break;
                                }
                                break;
                            case 112202875:
                                if (str.equals("video")) {
                                    obj = 4;
                                    break;
                                }
                                break;
                            case 112386354:
                                if (str.equals("voice")) {
                                    obj = 1;
                                    break;
                                }
                                break;
                        }
                        Bitmap bitmap;
                        TL_documentAttributeAudio audio;
                        switch (obj) {
                            case null:
                                fileName.file_name = "animation.gif";
                                if (finalPath.endsWith("mp4")) {
                                    document.mime_type = "video/mp4";
                                    document.attributes.add(new TL_documentAttributeAnimated());
                                } else {
                                    document.mime_type = "image/gif";
                                }
                                int side = isEncrypted ? 90 : 320;
                                try {
                                    if (finalPath.endsWith("mp4")) {
                                        bitmap = ThumbnailUtils.createVideoThumbnail(finalPath, 1);
                                    } else {
                                        bitmap = ImageLoader.loadBitmap(finalPath, null, (float) side, (float) side, true);
                                    }
                                    if (bitmap != null) {
                                        thumb = ImageLoader.scaleAndSaveImage(bitmap, (float) side, (float) side, 55, false);
                                        if (thumb != null) {
                                            document.thumbs.add(thumb);
                                            document.flags |= 1;
                                        }
                                        bitmap.recycle();
                                        break;
                                    }
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                    break;
                                }
                                break;
                            case 1:
                                audio = new TL_documentAttributeAudio();
                                audio.duration = MessageObject.getInlineResultDuration(result);
                                audio.voice = true;
                                fileName.file_name = "audio.ogg";
                                document.attributes.add(audio);
                                break;
                            case 2:
                                audio = new TL_documentAttributeAudio();
                                audio.duration = MessageObject.getInlineResultDuration(result);
                                audio.title = result.title;
                                audio.flags |= 1;
                                if (result.description != null) {
                                    audio.performer = result.description;
                                    audio.flags |= 2;
                                }
                                fileName.file_name = "audio.mp3";
                                document.attributes.add(audio);
                                break;
                            case 3:
                                int idx = result.content.mime_type.lastIndexOf(47);
                                if (idx == -1) {
                                    fileName.file_name = "file";
                                    break;
                                } else {
                                    fileName.file_name = "file." + result.content.mime_type.substring(idx + 1);
                                    break;
                                }
                            case 4:
                                fileName.file_name = "video.mp4";
                                TL_documentAttributeVideo attributeVideo = new TL_documentAttributeVideo();
                                wh = MessageObject.getInlineResultWidthAndHeight(result);
                                attributeVideo.w = wh[0];
                                attributeVideo.h = wh[1];
                                attributeVideo.duration = MessageObject.getInlineResultDuration(result);
                                attributeVideo.supports_streaming = true;
                                document.attributes.add(attributeVideo);
                                try {
                                    if (result.thumb != null) {
                                        bitmap = ImageLoader.loadBitmap(new File(FileLoader.getDirectory(4), Utilities.MD5(result.thumb.url) + "." + ImageLoader.getHttpUrlExtension(result.thumb.url, "jpg")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                        if (bitmap != null) {
                                            thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                            if (thumb != null) {
                                                document.thumbs.add(thumb);
                                                document.flags |= 1;
                                            }
                                            bitmap.recycle();
                                            break;
                                        }
                                    }
                                } catch (Throwable e2) {
                                    FileLog.e(e2);
                                    break;
                                }
                                break;
                            case 5:
                                TL_documentAttributeSticker attributeSticker = new TL_documentAttributeSticker();
                                attributeSticker.alt = "";
                                attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                document.attributes.add(attributeSticker);
                                TL_documentAttributeImageSize attributeImageSize = new TL_documentAttributeImageSize();
                                wh = MessageObject.getInlineResultWidthAndHeight(result);
                                attributeImageSize.w = wh[0];
                                attributeImageSize.h = wh[1];
                                document.attributes.add(attributeImageSize);
                                fileName.file_name = "sticker.webp";
                                try {
                                    if (result.thumb != null) {
                                        bitmap = ImageLoader.loadBitmap(new File(FileLoader.getDirectory(4), Utilities.MD5(result.thumb.url) + "." + ImageLoader.getHttpUrlExtension(result.thumb.url, "webp")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                        if (bitmap != null) {
                                            thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                            if (thumb != null) {
                                                document.thumbs.add(thumb);
                                                document.flags |= 1;
                                            }
                                            bitmap.recycle();
                                            break;
                                        }
                                    }
                                } catch (Throwable e22) {
                                    FileLog.e(e22);
                                    break;
                                }
                                break;
                        }
                        if (fileName.file_name == null) {
                            fileName.file_name = "file";
                        }
                        if (document.mime_type == null) {
                            document.mime_type = "application/octet-stream";
                        }
                        if (document.thumbs.isEmpty()) {
                            thumb = new TL_photoSize();
                            wh = MessageObject.getInlineResultWidthAndHeight(result);
                            thumb.w = wh[0];
                            thumb.h = wh[1];
                            thumb.size = 0;
                            thumb.location = new TL_fileLocationUnavailable();
                            thumb.type = "x";
                            document.thumbs.add(thumb);
                            document.flags |= 1;
                            break;
                        }
                        break;
                    case 6:
                        if (file.exists()) {
                            photo = getInstance(currentAccount).generatePhotoSizes(finalPath, null);
                        }
                        if (photo == null) {
                            photo = new TL_photo();
                            photo.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                            photo.file_reference = new byte[0];
                            TL_photoSize photoSize = new TL_photoSize();
                            wh = MessageObject.getInlineResultWidthAndHeight(result);
                            photoSize.w = wh[0];
                            photoSize.h = wh[1];
                            photoSize.size = 1;
                            photoSize.location = new TL_fileLocationUnavailable();
                            photoSize.type = "x";
                            photo.sizes.add(photoSize);
                            break;
                        }
                        break;
                }
            }
        } else if (result.type.equals("game")) {
            if (((int) dialog_id) != 0) {
                game = new TL_game();
                game.title = result.title;
                game.description = result.description;
                game.short_name = result.id;
                game.photo = result.photo;
                if (result.document instanceof TL_document) {
                    game.document = result.document;
                    game.flags |= 1;
                }
            } else {
                return;
            }
        } else if (result.document != null) {
            if (result.document instanceof TL_document) {
                document = (TL_document) result.document;
            }
        } else if (result.photo != null && (result.photo instanceof TL_photo)) {
            photo = (TL_photo) result.photo;
        }
        String finalPathFinal = finalPath;
        TL_document finalDocument = document;
        TL_photo finalPhoto = photo;
        TL_game finalGame = game;
        if (!(params == null || result.content == null)) {
            params.put("originalPath", result.content.url);
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$31(finalDocument, currentAccount, finalPathFinal, dialog_id, reply_to_msg, result, params, finalPhoto, finalGame));
    }

    static final /* synthetic */ void lambda$null$49$SendMessagesHelper(TL_document finalDocument, int currentAccount, String finalPathFinal, long dialog_id, MessageObject reply_to_msg, BotInlineResult result, HashMap params, TL_photo finalPhoto, TL_game finalGame) {
        if (finalDocument != null) {
            getInstance(currentAccount).sendMessage(finalDocument, null, finalPathFinal, dialog_id, reply_to_msg, result.send_message.message, result.send_message.entities, result.send_message.reply_markup, params, 0, result);
        } else if (finalPhoto != null) {
            getInstance(currentAccount).sendMessage(finalPhoto, result.content != null ? result.content.url : null, dialog_id, reply_to_msg, result.send_message.message, result.send_message.entities, result.send_message.reply_markup, params, 0, result);
        } else if (finalGame != null) {
            getInstance(currentAccount).sendMessage(finalGame, dialog_id, result.send_message.reply_markup, params);
        }
    }

    private static String getTrimmedString(String src) {
        String result = src.trim();
        if (result.length() == 0) {
            return result;
        }
        while (src.startsWith("\n")) {
            src = src.substring(1);
        }
        while (src.endsWith("\n")) {
            src = src.substring(0, src.length() - 1);
        }
        return src;
    }

    public static void prepareSendingText(String text, long dialog_id) {
        int currentAccount = UserConfig.selectedAccount;
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$19(text, currentAccount, dialog_id));
    }

    static final /* synthetic */ void lambda$null$51$SendMessagesHelper(String text, int currentAccount, long dialog_id) {
        String textFinal = getTrimmedString(text);
        if (textFinal.length() != 0) {
            int count = (int) Math.ceil((double) (((float) textFinal.length()) / 4096.0f));
            for (int a = 0; a < count; a++) {
                getInstance(currentAccount).sendMessage(textFinal.substring(a * 4096, Math.min((a + 1) * 4096, textFinal.length())), dialog_id, null, null, true, null, null, null);
            }
        }
    }

    public static void ensureMediaThumbExists(int currentAccount, boolean isEncrypted, TLObject object, String path, Uri uri, long startTime) {
        if (object instanceof TL_photo) {
            boolean smallExists;
            TL_photo photo = (TL_photo) object;
            TLObject smallSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 90);
            if (smallSize instanceof TL_photoStrippedSize) {
                smallExists = true;
            } else {
                smallExists = FileLoader.getPathToAttach(smallSize, true).exists();
            }
            PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
            boolean bigExists = FileLoader.getPathToAttach(bigSize, false).exists();
            if (!smallExists || !bigExists) {
                Bitmap bitmap = ImageLoader.loadBitmap(path, uri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
                if (bitmap == null) {
                    bitmap = ImageLoader.loadBitmap(path, uri, 800.0f, 800.0f, true);
                }
                if (!bigExists) {
                    PhotoSize size = ImageLoader.scaleAndSaveImage(bigSize, bitmap, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
                    if (size != bigSize) {
                        photo.sizes.add(0, size);
                    }
                }
                if (!smallExists) {
                    TLObject size2 = ImageLoader.scaleAndSaveImage(smallSize, bitmap, 90.0f, 90.0f, 55, true);
                    if (size2 != smallSize) {
                        photo.sizes.add(0, size2);
                    }
                }
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
        } else if (object instanceof TL_document) {
            Document document = (TL_document) object;
            if ((MessageObject.isVideoDocument(document) || MessageObject.isNewGifDocument(document)) && MessageObject.isDocumentHasThumb(document)) {
                PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                if (!(photoSize instanceof TL_photoStrippedSize) && !FileLoader.getPathToAttach(photoSize, true).exists()) {
                    Bitmap thumb = createVideoThumbnail(path, startTime);
                    if (thumb == null) {
                        thumb = ThumbnailUtils.createVideoThumbnail(path, 1);
                    }
                    int side = isEncrypted ? 90 : 320;
                    document.thumbs.set(0, ImageLoader.scaleAndSaveImage(photoSize, thumb, (float) side, (float) side, 55, false));
                }
            }
        }
    }

    public static void prepareSendingMedia(ArrayList<SendingMediaInfo> media, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, boolean forceDocument, boolean groupPhotos, MessageObject editingMessageObject) {
        if (!media.isEmpty()) {
            mediaSendQueue.postRunnable(new SendMessagesHelper$$Lambda$20(media, dialog_id, UserConfig.selectedAccount, forceDocument, groupPhotos, editingMessageObject, reply_to_msg, inputContent));
        }
    }

    /* JADX WARNING: Missing block: B:295:0x098d, code:
            if (r108.endsWith(".webp") != false) goto L_0x098f;
     */
    /* JADX WARNING: Missing block: B:368:0x0b6a, code:
            if (r115.size() == 1) goto L_0x0b6c;
     */
    static final /* synthetic */ void lambda$prepareSendingMedia$60$SendMessagesHelper(java.util.ArrayList r115, long r116, int r118, boolean r119, boolean r120, org.telegram.messenger.MessageObject r121, org.telegram.messenger.MessageObject r122, android.support.v13.view.inputmethod.InputContentInfoCompat r123) {
        /*
        r58 = java.lang.System.currentTimeMillis();
        r61 = r115.size();
        r0 = r116;
        r5 = (int) r0;
        if (r5 != 0) goto L_0x009d;
    L_0x000d:
        r6 = 1;
    L_0x000e:
        r65 = 0;
        if (r6 == 0) goto L_0x002f;
    L_0x0012:
        r5 = 32;
        r8 = r116 >> r5;
        r0 = (int) r8;
        r74 = r0;
        r5 = org.telegram.messenger.MessagesController.getInstance(r118);
        r8 = java.lang.Integer.valueOf(r74);
        r64 = r5.getEncryptedChat(r8);
        if (r64 == 0) goto L_0x002f;
    L_0x0027:
        r0 = r64;
        r5 = r0.layer;
        r65 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r5);
    L_0x002f:
        if (r6 == 0) goto L_0x0037;
    L_0x0031:
        r5 = 73;
        r0 = r65;
        if (r0 < r5) goto L_0x0193;
    L_0x0037:
        if (r119 != 0) goto L_0x0193;
    L_0x0039:
        if (r120 == 0) goto L_0x0193;
    L_0x003b:
        r114 = new java.util.HashMap;
        r114.<init>();
        r54 = 0;
    L_0x0042:
        r0 = r54;
        r1 = r61;
        if (r0 >= r1) goto L_0x0195;
    L_0x0048:
        r0 = r115;
        r1 = r54;
        r19 = r0.get(r1);
        r19 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r19;
        r0 = r19;
        r5 = r0.searchImage;
        if (r5 != 0) goto L_0x009a;
    L_0x0058:
        r0 = r19;
        r5 = r0.isVideo;
        if (r5 != 0) goto L_0x009a;
    L_0x005e:
        r0 = r19;
        r0 = r0.path;
        r86 = r0;
        r0 = r19;
        r0 = r0.path;
        r108 = r0;
        if (r108 != 0) goto L_0x0082;
    L_0x006c:
        r0 = r19;
        r5 = r0.uri;
        if (r5 == 0) goto L_0x0082;
    L_0x0072:
        r0 = r19;
        r5 = r0.uri;
        r108 = org.telegram.messenger.AndroidUtilities.getPath(r5);
        r0 = r19;
        r5 = r0.uri;
        r86 = r5.toString();
    L_0x0082:
        if (r108 == 0) goto L_0x00a0;
    L_0x0084:
        r5 = ".gif";
        r0 = r108;
        r5 = r0.endsWith(r5);
        if (r5 != 0) goto L_0x009a;
    L_0x008f:
        r5 = ".webp";
        r0 = r108;
        r5 = r0.endsWith(r5);
        if (r5 == 0) goto L_0x00a0;
    L_0x009a:
        r54 = r54 + 1;
        goto L_0x0042;
    L_0x009d:
        r6 = 0;
        goto L_0x000e;
    L_0x00a0:
        r0 = r19;
        r5 = r0.path;
        r0 = r19;
        r8 = r0.uri;
        r5 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r5, r8);
        if (r5 != 0) goto L_0x009a;
    L_0x00ae:
        if (r108 != 0) goto L_0x00ca;
    L_0x00b0:
        r0 = r19;
        r5 = r0.uri;
        if (r5 == 0) goto L_0x00ca;
    L_0x00b6:
        r0 = r19;
        r5 = r0.uri;
        r5 = org.telegram.messenger.MediaController.isGif(r5);
        if (r5 != 0) goto L_0x009a;
    L_0x00c0:
        r0 = r19;
        r5 = r0.uri;
        r5 = org.telegram.messenger.MediaController.isWebp(r5);
        if (r5 != 0) goto L_0x009a;
    L_0x00ca:
        if (r108 == 0) goto L_0x0170;
    L_0x00cc:
        r107 = new java.io.File;
        r107.<init>(r108);
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r86;
        r5 = r5.append(r0);
        r8 = r107.length();
        r5 = r5.append(r8);
        r8 = "_";
        r5 = r5.append(r8);
        r8 = r107.lastModified();
        r5 = r5.append(r8);
        r86 = r5.toString();
    L_0x00f7:
        r7 = 0;
        r88 = 0;
        if (r6 != 0) goto L_0x0151;
    L_0x00fc:
        r0 = r19;
        r5 = r0.ttl;
        if (r5 != 0) goto L_0x0151;
    L_0x0102:
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r118);
        if (r6 != 0) goto L_0x0173;
    L_0x0108:
        r5 = 0;
    L_0x0109:
        r0 = r86;
        r103 = r8.getSentFile(r0, r5);
        if (r103 == 0) goto L_0x011b;
    L_0x0111:
        r5 = 0;
        r7 = r103[r5];
        r7 = (org.telegram.tgnet.TLRPC.TL_photo) r7;
        r5 = 1;
        r88 = r103[r5];
        r88 = (java.lang.String) r88;
    L_0x011b:
        if (r7 != 0) goto L_0x0142;
    L_0x011d:
        r0 = r19;
        r5 = r0.uri;
        if (r5 == 0) goto L_0x0142;
    L_0x0123:
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r118);
        r0 = r19;
        r5 = r0.uri;
        r9 = org.telegram.messenger.AndroidUtilities.getPath(r5);
        if (r6 != 0) goto L_0x0175;
    L_0x0131:
        r5 = 0;
    L_0x0132:
        r103 = r8.getSentFile(r9, r5);
        if (r103 == 0) goto L_0x0142;
    L_0x0138:
        r5 = 0;
        r7 = r103[r5];
        r7 = (org.telegram.tgnet.TLRPC.TL_photo) r7;
        r5 = 1;
        r88 = r103[r5];
        r88 = (java.lang.String) r88;
    L_0x0142:
        r0 = r19;
        r8 = r0.path;
        r0 = r19;
        r9 = r0.uri;
        r10 = 0;
        r5 = r118;
        ensureMediaThumbExists(r5, r6, r7, r8, r9, r10);
    L_0x0151:
        r113 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker;
        r5 = 0;
        r0 = r113;
        r0.<init>(r5);
        r0 = r114;
        r1 = r19;
        r2 = r113;
        r0.put(r1, r2);
        if (r7 == 0) goto L_0x0177;
    L_0x0164:
        r0 = r88;
        r1 = r113;
        r1.parentObject = r0;
        r0 = r113;
        r0.photo = r7;
        goto L_0x009a;
    L_0x0170:
        r86 = 0;
        goto L_0x00f7;
    L_0x0173:
        r5 = 3;
        goto L_0x0109;
    L_0x0175:
        r5 = 3;
        goto L_0x0132;
    L_0x0177:
        r5 = new java.util.concurrent.CountDownLatch;
        r8 = 1;
        r5.<init>(r8);
        r0 = r113;
        r0.sync = r5;
        r5 = mediaSendThreadPool;
        r8 = new org.telegram.messenger.SendMessagesHelper$$Lambda$23;
        r0 = r113;
        r1 = r118;
        r2 = r19;
        r8.<init>(r0, r1, r2, r6);
        r5.execute(r8);
        goto L_0x009a;
    L_0x0193:
        r114 = 0;
    L_0x0195:
        r70 = 0;
        r78 = 0;
        r99 = 0;
        r102 = 0;
        r100 = 0;
        r101 = 0;
        r66 = 0;
        r95 = 0;
        r54 = 0;
    L_0x01a7:
        r0 = r54;
        r1 = r61;
        if (r0 >= r1) goto L_0x0da6;
    L_0x01ad:
        r0 = r115;
        r1 = r54;
        r19 = r0.get(r1);
        r19 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r19;
        if (r120 == 0) goto L_0x01d4;
    L_0x01b9:
        if (r6 == 0) goto L_0x01c1;
    L_0x01bb:
        r5 = 73;
        r0 = r65;
        if (r0 < r5) goto L_0x01d4;
    L_0x01c1:
        r5 = 1;
        r0 = r61;
        if (r0 <= r5) goto L_0x01d4;
    L_0x01c6:
        r5 = r95 % 10;
        if (r5 != 0) goto L_0x01d4;
    L_0x01ca:
        r5 = org.telegram.messenger.Utilities.random;
        r70 = r5.nextLong();
        r78 = r70;
        r95 = 0;
    L_0x01d4:
        r0 = r19;
        r5 = r0.searchImage;
        if (r5 == 0) goto L_0x0599;
    L_0x01da:
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.type;
        r8 = 1;
        if (r5 != r8) goto L_0x040b;
    L_0x01e3:
        r14 = new java.util.HashMap;
        r14.<init>();
        r27 = 0;
        r88 = 0;
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.document;
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r5 == 0) goto L_0x039d;
    L_0x01f6:
        r0 = r19;
        r5 = r0.searchImage;
        r0 = r5.document;
        r27 = r0;
        r27 = (org.telegram.tgnet.TLRPC.TL_document) r27;
        r5 = 1;
        r0 = r27;
        r60 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r5);
    L_0x0207:
        if (r27 != 0) goto L_0x035f;
    L_0x0209:
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.localUrl;
        if (r5 == 0) goto L_0x021d;
    L_0x0211:
        r5 = "url";
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.localUrl;
        r14.put(r5, r8);
    L_0x021d:
        r110 = 0;
        r27 = new org.telegram.tgnet.TLRPC$TL_document;
        r27.<init>();
        r8 = 0;
        r0 = r27;
        r0.id = r8;
        r5 = 0;
        r5 = new byte[r5];
        r0 = r27;
        r0.file_reference = r5;
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r118);
        r5 = r5.getCurrentTime();
        r0 = r27;
        r0.date = r5;
        r68 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r68.<init>();
        r5 = "animation.gif";
        r0 = r68;
        r0.file_name = r5;
        r0 = r27;
        r5 = r0.attributes;
        r0 = r68;
        r5.add(r0);
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.size;
        r0 = r27;
        r0.size = r5;
        r5 = 0;
        r0 = r27;
        r0.dc_id = r5;
        r5 = r60.toString();
        r8 = "mp4";
        r5 = r5.endsWith(r8);
        if (r5 == 0) goto L_0x03dc;
    L_0x026e:
        r5 = "video/mp4";
        r0 = r27;
        r0.mime_type = r5;
        r0 = r27;
        r5 = r0.attributes;
        r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r8.<init>();
        r5.add(r8);
    L_0x0281:
        r5 = r60.exists();
        if (r5 == 0) goto L_0x03e5;
    L_0x0287:
        r110 = r60;
    L_0x0289:
        if (r110 != 0) goto L_0x02d0;
    L_0x028b:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.thumbUrl;
        r8 = org.telegram.messenger.Utilities.MD5(r8);
        r5 = r5.append(r8);
        r8 = ".";
        r5 = r5.append(r8);
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.thumbUrl;
        r9 = "jpg";
        r8 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r8, r9);
        r5 = r5.append(r8);
        r109 = r5.toString();
        r110 = new java.io.File;
        r5 = 4;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r0 = r110;
        r1 = r109;
        r0.<init>(r5, r1);
        r5 = r110.exists();
        if (r5 != 0) goto L_0x02d0;
    L_0x02ce:
        r110 = 0;
    L_0x02d0:
        if (r110 == 0) goto L_0x0314;
    L_0x02d2:
        if (r6 == 0) goto L_0x03e9;
    L_0x02d4:
        r105 = 90;
    L_0x02d6:
        r5 = r110.getAbsolutePath();	 Catch:{ Exception -> 0x03ff }
        r8 = "mp4";
        r5 = r5.endsWith(r8);	 Catch:{ Exception -> 0x03ff }
        if (r5 == 0) goto L_0x03ed;
    L_0x02e3:
        r5 = r110.getAbsolutePath();	 Catch:{ Exception -> 0x03ff }
        r8 = 1;
        r57 = android.media.ThumbnailUtils.createVideoThumbnail(r5, r8);	 Catch:{ Exception -> 0x03ff }
    L_0x02ec:
        if (r57 == 0) goto L_0x0314;
    L_0x02ee:
        r0 = r105;
        r5 = (float) r0;	 Catch:{ Exception -> 0x03ff }
        r0 = r105;
        r8 = (float) r0;	 Catch:{ Exception -> 0x03ff }
        r9 = 55;
        r0 = r57;
        r109 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r5, r8, r9, r6);	 Catch:{ Exception -> 0x03ff }
        if (r109 == 0) goto L_0x0311;
    L_0x02fe:
        r0 = r27;
        r5 = r0.thumbs;	 Catch:{ Exception -> 0x03ff }
        r0 = r109;
        r5.add(r0);	 Catch:{ Exception -> 0x03ff }
        r0 = r27;
        r5 = r0.flags;	 Catch:{ Exception -> 0x03ff }
        r5 = r5 | 1;
        r0 = r27;
        r0.flags = r5;	 Catch:{ Exception -> 0x03ff }
    L_0x0311:
        r57.recycle();	 Catch:{ Exception -> 0x03ff }
    L_0x0314:
        r0 = r27;
        r5 = r0.thumbs;
        r5 = r5.isEmpty();
        if (r5 == 0) goto L_0x035f;
    L_0x031e:
        r109 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r109.<init>();
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.width;
        r0 = r109;
        r0.w = r5;
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.height;
        r0 = r109;
        r0.h = r5;
        r5 = 0;
        r0 = r109;
        r0.size = r5;
        r5 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r5.<init>();
        r0 = r109;
        r0.location = r5;
        r5 = "x";
        r0 = r109;
        r0.type = r5;
        r0 = r27;
        r5 = r0.thumbs;
        r0 = r109;
        r5.add(r0);
        r0 = r27;
        r5 = r0.flags;
        r5 = r5 | 1;
        r0 = r27;
        r0.flags = r5;
    L_0x035f:
        r12 = r27;
        r15 = r88;
        r0 = r19;
        r5 = r0.searchImage;
        r0 = r5.imageUrl;
        r87 = r0;
        if (r60 != 0) goto L_0x0405;
    L_0x036d:
        r0 = r19;
        r5 = r0.searchImage;
        r13 = r5.imageUrl;
    L_0x0373:
        if (r14 == 0) goto L_0x0389;
    L_0x0375:
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.imageUrl;
        if (r5 == 0) goto L_0x0389;
    L_0x037d:
        r5 = "originalPath";
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.imageUrl;
        r14.put(r5, r8);
    L_0x0389:
        r9 = new org.telegram.messenger.SendMessagesHelper$$Lambda$24;
        r10 = r121;
        r11 = r118;
        r16 = r116;
        r18 = r122;
        r9.<init>(r10, r11, r12, r13, r14, r15, r16, r18, r19);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r9);
    L_0x0399:
        r54 = r54 + 1;
        goto L_0x01a7;
    L_0x039d:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.imageUrl;
        r8 = org.telegram.messenger.Utilities.MD5(r8);
        r5 = r5.append(r8);
        r8 = ".";
        r5 = r5.append(r8);
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.imageUrl;
        r9 = "jpg";
        r8 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r8, r9);
        r5 = r5.append(r8);
        r82 = r5.toString();
        r60 = new java.io.File;
        r5 = 4;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r0 = r60;
        r1 = r82;
        r0.<init>(r5, r1);
        goto L_0x0207;
    L_0x03dc:
        r5 = "image/gif";
        r0 = r27;
        r0.mime_type = r5;
        goto L_0x0281;
    L_0x03e5:
        r60 = 0;
        goto L_0x0289;
    L_0x03e9:
        r105 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x02d6;
    L_0x03ed:
        r5 = r110.getAbsolutePath();	 Catch:{ Exception -> 0x03ff }
        r8 = 0;
        r0 = r105;
        r9 = (float) r0;	 Catch:{ Exception -> 0x03ff }
        r0 = r105;
        r10 = (float) r0;	 Catch:{ Exception -> 0x03ff }
        r11 = 1;
        r57 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r8, r9, r10, r11);	 Catch:{ Exception -> 0x03ff }
        goto L_0x02ec;
    L_0x03ff:
        r63 = move-exception;
        org.telegram.messenger.FileLog.e(r63);
        goto L_0x0314;
    L_0x0405:
        r13 = r60.toString();
        goto L_0x0373;
    L_0x040b:
        r84 = 1;
        r7 = 0;
        r88 = 0;
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.photo;
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r5 == 0) goto L_0x058f;
    L_0x041a:
        r0 = r19;
        r5 = r0.searchImage;
        r7 = r5.photo;
        r7 = (org.telegram.tgnet.TLRPC.TL_photo) r7;
    L_0x0422:
        if (r7 != 0) goto L_0x051f;
    L_0x0424:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.imageUrl;
        r8 = org.telegram.messenger.Utilities.MD5(r8);
        r5 = r5.append(r8);
        r8 = ".";
        r5 = r5.append(r8);
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.imageUrl;
        r9 = "jpg";
        r8 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r8, r9);
        r5 = r5.append(r8);
        r82 = r5.toString();
        r60 = new java.io.File;
        r5 = 4;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r0 = r60;
        r1 = r82;
        r0.<init>(r5, r1);
        r5 = r60.exists();
        if (r5 == 0) goto L_0x0482;
    L_0x0467:
        r8 = r60.length();
        r10 = 0;
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 == 0) goto L_0x0482;
    L_0x0471:
        r5 = getInstance(r118);
        r8 = r60.toString();
        r9 = 0;
        r7 = r5.generatePhotoSizes(r8, r9);
        if (r7 == 0) goto L_0x0482;
    L_0x0480:
        r84 = 0;
    L_0x0482:
        if (r7 != 0) goto L_0x051f;
    L_0x0484:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.thumbUrl;
        r8 = org.telegram.messenger.Utilities.MD5(r8);
        r5 = r5.append(r8);
        r8 = ".";
        r5 = r5.append(r8);
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.thumbUrl;
        r9 = "jpg";
        r8 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r8, r9);
        r5 = r5.append(r8);
        r82 = r5.toString();
        r60 = new java.io.File;
        r5 = 4;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r0 = r60;
        r1 = r82;
        r0.<init>(r5, r1);
        r5 = r60.exists();
        if (r5 == 0) goto L_0x04d4;
    L_0x04c7:
        r5 = getInstance(r118);
        r8 = r60.toString();
        r9 = 0;
        r7 = r5.generatePhotoSizes(r8, r9);
    L_0x04d4:
        if (r7 != 0) goto L_0x051f;
    L_0x04d6:
        r7 = new org.telegram.tgnet.TLRPC$TL_photo;
        r7.<init>();
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r118);
        r5 = r5.getCurrentTime();
        r7.date = r5;
        r5 = 0;
        r5 = new byte[r5];
        r7.file_reference = r5;
        r92 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r92.<init>();
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.width;
        r0 = r92;
        r0.w = r5;
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.height;
        r0 = r92;
        r0.h = r5;
        r5 = 0;
        r0 = r92;
        r0.size = r5;
        r5 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r5.<init>();
        r0 = r92;
        r0.location = r5;
        r5 = "x";
        r0 = r92;
        r0.type = r5;
        r5 = r7.sizes;
        r0 = r92;
        r5.add(r0);
    L_0x051f:
        if (r7 == 0) goto L_0x0399;
    L_0x0521:
        r23 = r7;
        r15 = r88;
        r24 = r84;
        r14 = new java.util.HashMap;
        r14.<init>();
        r0 = r19;
        r5 = r0.searchImage;
        r5 = r5.imageUrl;
        if (r5 == 0) goto L_0x0540;
    L_0x0534:
        r5 = "originalPath";
        r0 = r19;
        r8 = r0.searchImage;
        r8 = r8.imageUrl;
        r14.put(r5, r8);
    L_0x0540:
        if (r120 == 0) goto L_0x0577;
    L_0x0542:
        r95 = r95 + 1;
        r5 = "groupId";
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "";
        r8 = r8.append(r9);
        r0 = r70;
        r8 = r8.append(r0);
        r8 = r8.toString();
        r14.put(r5, r8);
        r5 = 10;
        r0 = r95;
        if (r0 == r5) goto L_0x056c;
    L_0x0566:
        r5 = r61 + -1;
        r0 = r54;
        if (r0 != r5) goto L_0x0577;
    L_0x056c:
        r5 = "final";
        r8 = "1";
        r14.put(r5, r8);
        r78 = 0;
    L_0x0577:
        r20 = new org.telegram.messenger.SendMessagesHelper$$Lambda$25;
        r21 = r121;
        r22 = r118;
        r25 = r19;
        r26 = r14;
        r27 = r15;
        r28 = r116;
        r30 = r122;
        r20.<init>(r21, r22, r23, r24, r25, r26, r27, r28, r30);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r20);
        goto L_0x0399;
    L_0x058f:
        if (r6 != 0) goto L_0x0422;
    L_0x0591:
        r0 = r19;
        r5 = r0.ttl;
        if (r5 != 0) goto L_0x0422;
    L_0x0597:
        goto L_0x0422;
    L_0x0599:
        r0 = r19;
        r5 = r0.isVideo;
        if (r5 == 0) goto L_0x08f4;
    L_0x059f:
        r109 = 0;
        r111 = 0;
        if (r119 == 0) goto L_0x0852;
    L_0x05a5:
        r37 = 0;
    L_0x05a7:
        if (r119 != 0) goto L_0x08cb;
    L_0x05a9:
        if (r37 != 0) goto L_0x05b8;
    L_0x05ab:
        r0 = r19;
        r5 = r0.path;
        r8 = "mp4";
        r5 = r5.endsWith(r8);
        if (r5 == 0) goto L_0x08cb;
    L_0x05b8:
        r0 = r19;
        r0 = r0.path;
        r89 = r0;
        r0 = r19;
        r0 = r0.path;
        r86 = r0;
        r107 = new java.io.File;
        r0 = r107;
        r1 = r86;
        r0.<init>(r1);
        r30 = 0;
        r83 = 0;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r86;
        r5 = r5.append(r0);
        r8 = r107.length();
        r5 = r5.append(r8);
        r8 = "_";
        r5 = r5.append(r8);
        r8 = r107.lastModified();
        r5 = r5.append(r8);
        r86 = r5.toString();
        if (r37 == 0) goto L_0x0679;
    L_0x05f9:
        r0 = r37;
        r0 = r0.muted;
        r83 = r0;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r86;
        r5 = r5.append(r0);
        r0 = r37;
        r8 = r0.estimatedDuration;
        r5 = r5.append(r8);
        r8 = "_";
        r5 = r5.append(r8);
        r0 = r37;
        r8 = r0.startTime;
        r5 = r5.append(r8);
        r8 = "_";
        r5 = r5.append(r8);
        r0 = r37;
        r8 = r0.endTime;
        r8 = r5.append(r8);
        r0 = r37;
        r5 = r0.muted;
        if (r5 == 0) goto L_0x0869;
    L_0x0636:
        r5 = "_m";
    L_0x0639:
        r5 = r8.append(r5);
        r86 = r5.toString();
        r0 = r37;
        r5 = r0.resultWidth;
        r0 = r37;
        r8 = r0.originalWidth;
        if (r5 == r8) goto L_0x0669;
    L_0x064b:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r86;
        r5 = r5.append(r0);
        r8 = "_";
        r5 = r5.append(r8);
        r0 = r37;
        r8 = r0.resultWidth;
        r5 = r5.append(r8);
        r86 = r5.toString();
    L_0x0669:
        r0 = r37;
        r8 = r0.startTime;
        r10 = 0;
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 < 0) goto L_0x086e;
    L_0x0673:
        r0 = r37;
        r0 = r0.startTime;
        r30 = r0;
    L_0x0679:
        r27 = 0;
        r88 = 0;
        if (r6 != 0) goto L_0x06ad;
    L_0x067f:
        r0 = r19;
        r5 = r0.ttl;
        if (r5 != 0) goto L_0x06ad;
    L_0x0685:
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r118);
        if (r6 != 0) goto L_0x0872;
    L_0x068b:
        r5 = 2;
    L_0x068c:
        r0 = r86;
        r103 = r8.getSentFile(r0, r5);
        if (r103 == 0) goto L_0x06ad;
    L_0x0694:
        r5 = 0;
        r27 = r103[r5];
        r27 = (org.telegram.tgnet.TLRPC.TL_document) r27;
        r5 = 1;
        r88 = r103[r5];
        r88 = (java.lang.String) r88;
        r0 = r19;
        r0 = r0.path;
        r28 = r0;
        r29 = 0;
        r25 = r118;
        r26 = r6;
        ensureMediaThumbExists(r25, r26, r27, r28, r29, r30);
    L_0x06ad:
        if (r27 != 0) goto L_0x0774;
    L_0x06af:
        r0 = r19;
        r5 = r0.path;
        r0 = r30;
        r109 = createVideoThumbnail(r5, r0);
        if (r109 != 0) goto L_0x06c4;
    L_0x06bb:
        r0 = r19;
        r5 = r0.path;
        r8 = 1;
        r109 = android.media.ThumbnailUtils.createVideoThumbnail(r5, r8);
    L_0x06c4:
        if (r6 == 0) goto L_0x0875;
    L_0x06c6:
        r105 = 90;
    L_0x06c8:
        r0 = r105;
        r5 = (float) r0;
        r0 = r105;
        r8 = (float) r0;
        r9 = 55;
        r0 = r109;
        r106 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r5, r8, r9, r6);
        if (r109 == 0) goto L_0x06dc;
    L_0x06d8:
        if (r106 == 0) goto L_0x06dc;
    L_0x06da:
        r109 = 0;
    L_0x06dc:
        r27 = new org.telegram.tgnet.TLRPC$TL_document;
        r27.<init>();
        r5 = 0;
        r5 = new byte[r5];
        r0 = r27;
        r0.file_reference = r5;
        if (r106 == 0) goto L_0x06fd;
    L_0x06ea:
        r0 = r27;
        r5 = r0.thumbs;
        r0 = r106;
        r5.add(r0);
        r0 = r27;
        r5 = r0.flags;
        r5 = r5 | 1;
        r0 = r27;
        r0.flags = r5;
    L_0x06fd:
        r5 = "video/mp4";
        r0 = r27;
        r0.mime_type = r5;
        r5 = org.telegram.messenger.UserConfig.getInstance(r118);
        r8 = 0;
        r5.saveConfig(r8);
        if (r6 == 0) goto L_0x0880;
    L_0x070e:
        r5 = 66;
        r0 = r65;
        if (r0 < r5) goto L_0x0879;
    L_0x0714:
        r55 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r55.<init>();
    L_0x0719:
        r0 = r27;
        r5 = r0.attributes;
        r0 = r55;
        r5.add(r0);
        if (r37 == 0) goto L_0x08ac;
    L_0x0724:
        r5 = r37.needConvert();
        if (r5 == 0) goto L_0x08ac;
    L_0x072a:
        r0 = r37;
        r5 = r0.muted;
        if (r5 == 0) goto L_0x088c;
    L_0x0730:
        r0 = r19;
        r5 = r0.path;
        r0 = r55;
        r1 = r37;
        fillVideoAttribute(r5, r0, r1);
        r0 = r55;
        r5 = r0.w;
        r0 = r37;
        r0.originalWidth = r5;
        r0 = r55;
        r5 = r0.h;
        r0 = r37;
        r0.originalHeight = r5;
    L_0x074b:
        r0 = r37;
        r5 = r0.rotationValue;
        r8 = 90;
        if (r5 == r8) goto L_0x075b;
    L_0x0753:
        r0 = r37;
        r5 = r0.rotationValue;
        r8 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r5 != r8) goto L_0x089a;
    L_0x075b:
        r0 = r37;
        r5 = r0.resultHeight;
        r0 = r55;
        r0.w = r5;
        r0 = r37;
        r5 = r0.resultWidth;
        r0 = r55;
        r0.h = r5;
    L_0x076b:
        r0 = r37;
        r8 = r0.estimatedSize;
        r5 = (int) r8;
        r0 = r27;
        r0.size = r5;
    L_0x0774:
        if (r37 == 0) goto L_0x07aa;
    L_0x0776:
        r0 = r37;
        r5 = r0.muted;
        if (r5 == 0) goto L_0x07aa;
    L_0x077c:
        r69 = 0;
        r56 = 0;
        r0 = r27;
        r5 = r0.attributes;
        r4 = r5.size();
    L_0x0788:
        r0 = r56;
        if (r0 >= r4) goto L_0x079c;
    L_0x078c:
        r0 = r27;
        r5 = r0.attributes;
        r0 = r56;
        r5 = r5.get(r0);
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
        if (r5 == 0) goto L_0x08c7;
    L_0x079a:
        r69 = 1;
    L_0x079c:
        if (r69 != 0) goto L_0x07aa;
    L_0x079e:
        r0 = r27;
        r5 = r0.attributes;
        r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r8.<init>();
        r5.add(r8);
    L_0x07aa:
        if (r37 == 0) goto L_0x07e6;
    L_0x07ac:
        r5 = r37.needConvert();
        if (r5 == 0) goto L_0x07e6;
    L_0x07b2:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r8 = "-2147483648_";
        r5 = r5.append(r8);
        r8 = org.telegram.messenger.SharedConfig.getLastLocalId();
        r5 = r5.append(r8);
        r8 = ".mp4";
        r5 = r5.append(r8);
        r68 = r5.toString();
        r60 = new java.io.File;
        r5 = 4;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r0 = r60;
        r1 = r68;
        r0.<init>(r5, r1);
        org.telegram.messenger.SharedConfig.saveConfig();
        r89 = r60.getAbsolutePath();
    L_0x07e6:
        r38 = r27;
        r15 = r88;
        r87 = r86;
        r39 = r89;
        r14 = new java.util.HashMap;
        r14.<init>();
        r33 = r109;
        r34 = r111;
        if (r86 == 0) goto L_0x0801;
    L_0x07f9:
        r5 = "originalPath";
        r0 = r86;
        r14.put(r5, r0);
    L_0x0801:
        if (r83 != 0) goto L_0x083a;
    L_0x0803:
        if (r120 == 0) goto L_0x083a;
    L_0x0805:
        r95 = r95 + 1;
        r5 = "groupId";
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "";
        r8 = r8.append(r9);
        r0 = r70;
        r8 = r8.append(r0);
        r8 = r8.toString();
        r14.put(r5, r8);
        r5 = 10;
        r0 = r95;
        if (r0 == r5) goto L_0x082f;
    L_0x0829:
        r5 = r61 + -1;
        r0 = r54;
        if (r0 != r5) goto L_0x083a;
    L_0x082f:
        r5 = "final";
        r8 = "1";
        r14.put(r5, r8);
        r78 = 0;
    L_0x083a:
        r32 = new org.telegram.messenger.SendMessagesHelper$$Lambda$26;
        r35 = r121;
        r36 = r118;
        r40 = r14;
        r41 = r15;
        r42 = r116;
        r44 = r122;
        r45 = r19;
        r32.<init>(r33, r34, r35, r36, r37, r38, r39, r40, r41, r42, r44, r45);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r32);
        goto L_0x0399;
    L_0x0852:
        r0 = r19;
        r5 = r0.videoEditedInfo;
        if (r5 == 0) goto L_0x0860;
    L_0x0858:
        r0 = r19;
        r0 = r0.videoEditedInfo;
        r37 = r0;
    L_0x085e:
        goto L_0x05a7;
    L_0x0860:
        r0 = r19;
        r5 = r0.path;
        r37 = createCompressionSettings(r5);
        goto L_0x085e;
    L_0x0869:
        r5 = "";
        goto L_0x0639;
    L_0x086e:
        r30 = 0;
        goto L_0x0679;
    L_0x0872:
        r5 = 5;
        goto L_0x068c;
    L_0x0875:
        r105 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        goto L_0x06c8;
    L_0x0879:
        r55 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r55.<init>();
        goto L_0x0719;
    L_0x0880:
        r55 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r55.<init>();
        r5 = 1;
        r0 = r55;
        r0.supports_streaming = r5;
        goto L_0x0719;
    L_0x088c:
        r0 = r37;
        r8 = r0.estimatedDuration;
        r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r8 = r8 / r10;
        r5 = (int) r8;
        r0 = r55;
        r0.duration = r5;
        goto L_0x074b;
    L_0x089a:
        r0 = r37;
        r5 = r0.resultWidth;
        r0 = r55;
        r0.w = r5;
        r0 = r37;
        r5 = r0.resultHeight;
        r0 = r55;
        r0.h = r5;
        goto L_0x076b;
    L_0x08ac:
        r5 = r107.exists();
        if (r5 == 0) goto L_0x08bb;
    L_0x08b2:
        r8 = r107.length();
        r5 = (int) r8;
        r0 = r27;
        r0.size = r5;
    L_0x08bb:
        r0 = r19;
        r5 = r0.path;
        r8 = 0;
        r0 = r55;
        fillVideoAttribute(r5, r0, r8);
        goto L_0x0774;
    L_0x08c7:
        r56 = r56 + 1;
        goto L_0x0788;
    L_0x08cb:
        r0 = r19;
        r0 = r0.path;
        r42 = r0;
        r0 = r19;
        r0 = r0.path;
        r43 = r0;
        r44 = 0;
        r45 = 0;
        r0 = r19;
        r0 = r0.caption;
        r49 = r0;
        r0 = r19;
        r0 = r0.entities;
        r50 = r0;
        r41 = r118;
        r46 = r116;
        r48 = r122;
        r51 = r121;
        prepareSendingDocumentInternal(r41, r42, r43, r44, r45, r46, r48, r49, r50, r51);
        goto L_0x0399;
    L_0x08f4:
        r0 = r19;
        r0 = r0.path;
        r86 = r0;
        r0 = r19;
        r0 = r0.path;
        r108 = r0;
        if (r108 != 0) goto L_0x0918;
    L_0x0902:
        r0 = r19;
        r5 = r0.uri;
        if (r5 == 0) goto L_0x0918;
    L_0x0908:
        r0 = r19;
        r5 = r0.uri;
        r108 = org.telegram.messenger.AndroidUtilities.getPath(r5);
        r0 = r19;
        r5 = r0.uri;
        r86 = r5.toString();
    L_0x0918:
        r76 = 0;
        if (r119 != 0) goto L_0x092a;
    L_0x091c:
        r0 = r19;
        r5 = r0.path;
        r0 = r19;
        r8 = r0.uri;
        r5 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r5, r8);
        if (r5 == 0) goto L_0x0977;
    L_0x092a:
        r76 = 1;
        if (r108 == 0) goto L_0x0973;
    L_0x092e:
        r5 = new java.io.File;
        r0 = r108;
        r5.<init>(r0);
        r66 = org.telegram.messenger.FileLoader.getFileExtension(r5);
    L_0x0939:
        if (r76 == 0) goto L_0x09f4;
    L_0x093b:
        if (r99 != 0) goto L_0x0951;
    L_0x093d:
        r99 = new java.util.ArrayList;
        r99.<init>();
        r102 = new java.util.ArrayList;
        r102.<init>();
        r100 = new java.util.ArrayList;
        r100.<init>();
        r101 = new java.util.ArrayList;
        r101.<init>();
    L_0x0951:
        r0 = r99;
        r1 = r108;
        r0.add(r1);
        r0 = r102;
        r1 = r86;
        r0.add(r1);
        r0 = r19;
        r5 = r0.caption;
        r0 = r100;
        r0.add(r5);
        r0 = r19;
        r5 = r0.entities;
        r0 = r101;
        r0.add(r5);
        goto L_0x0399;
    L_0x0973:
        r66 = "";
        goto L_0x0939;
    L_0x0977:
        if (r108 == 0) goto L_0x09a4;
    L_0x0979:
        r5 = ".gif";
        r0 = r108;
        r5 = r0.endsWith(r5);
        if (r5 != 0) goto L_0x098f;
    L_0x0984:
        r5 = ".webp";
        r0 = r108;
        r5 = r0.endsWith(r5);
        if (r5 == 0) goto L_0x09a4;
    L_0x098f:
        r5 = ".gif";
        r0 = r108;
        r5 = r0.endsWith(r5);
        if (r5 == 0) goto L_0x09a0;
    L_0x099a:
        r66 = "gif";
    L_0x099d:
        r76 = 1;
        goto L_0x0939;
    L_0x09a0:
        r66 = "webp";
        goto L_0x099d;
    L_0x09a4:
        if (r108 != 0) goto L_0x0939;
    L_0x09a6:
        r0 = r19;
        r5 = r0.uri;
        if (r5 == 0) goto L_0x0939;
    L_0x09ac:
        r0 = r19;
        r5 = r0.uri;
        r5 = org.telegram.messenger.MediaController.isGif(r5);
        if (r5 == 0) goto L_0x09d0;
    L_0x09b6:
        r76 = 1;
        r0 = r19;
        r5 = r0.uri;
        r86 = r5.toString();
        r0 = r19;
        r5 = r0.uri;
        r8 = "gif";
        r108 = org.telegram.messenger.MediaController.copyFileToCache(r5, r8);
        r66 = "gif";
        goto L_0x0939;
    L_0x09d0:
        r0 = r19;
        r5 = r0.uri;
        r5 = org.telegram.messenger.MediaController.isWebp(r5);
        if (r5 == 0) goto L_0x0939;
    L_0x09da:
        r76 = 1;
        r0 = r19;
        r5 = r0.uri;
        r86 = r5.toString();
        r0 = r19;
        r5 = r0.uri;
        r8 = "webp";
        r108 = org.telegram.messenger.MediaController.copyFileToCache(r5, r8);
        r66 = "webp";
        goto L_0x0939;
    L_0x09f4:
        if (r108 == 0) goto L_0x0ab9;
    L_0x09f6:
        r107 = new java.io.File;
        r107.<init>(r108);
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r86;
        r5 = r5.append(r0);
        r8 = r107.length();
        r5 = r5.append(r8);
        r8 = "_";
        r5 = r5.append(r8);
        r8 = r107.lastModified();
        r5 = r5.append(r8);
        r86 = r5.toString();
    L_0x0a21:
        r7 = 0;
        r88 = 0;
        if (r114 == 0) goto L_0x0ac2;
    L_0x0a26:
        r0 = r114;
        r1 = r19;
        r113 = r0.get(r1);
        r113 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r113;
        r0 = r113;
        r7 = r0.photo;
        r0 = r113;
        r0 = r0.parentObject;
        r88 = r0;
        if (r7 != 0) goto L_0x0a4d;
    L_0x0a3c:
        r0 = r113;
        r5 = r0.sync;	 Catch:{ Exception -> 0x0abd }
        r5.await();	 Catch:{ Exception -> 0x0abd }
    L_0x0a43:
        r0 = r113;
        r7 = r0.photo;
        r0 = r113;
        r0 = r0.parentObject;
        r88 = r0;
    L_0x0a4d:
        if (r7 == 0) goto L_0x0d6e;
    L_0x0a4f:
        r23 = r7;
        r15 = r88;
        r14 = new java.util.HashMap;
        r14.<init>();
        r5 = 1;
        r0 = new android.graphics.Bitmap[r5];
        r41 = r0;
        r5 = 1;
        r0 = new java.lang.String[r5];
        r42 = r0;
        r0 = r19;
        r5 = r0.masks;
        if (r5 == 0) goto L_0x0b45;
    L_0x0a68:
        r0 = r19;
        r5 = r0.masks;
        r5 = r5.isEmpty();
        if (r5 != 0) goto L_0x0b45;
    L_0x0a72:
        r5 = 1;
    L_0x0a73:
        r7.has_stickers = r5;
        if (r5 == 0) goto L_0x0b59;
    L_0x0a77:
        r104 = new org.telegram.tgnet.SerializedData;
        r0 = r19;
        r5 = r0.masks;
        r5 = r5.size();
        r5 = r5 * 20;
        r5 = r5 + 4;
        r0 = r104;
        r0.<init>(r5);
        r0 = r19;
        r5 = r0.masks;
        r5 = r5.size();
        r0 = r104;
        r0.writeInt32(r5);
        r56 = 0;
    L_0x0a99:
        r0 = r19;
        r5 = r0.masks;
        r5 = r5.size();
        r0 = r56;
        if (r0 >= r5) goto L_0x0b48;
    L_0x0aa5:
        r0 = r19;
        r5 = r0.masks;
        r0 = r56;
        r5 = r5.get(r0);
        r5 = (org.telegram.tgnet.TLRPC.InputDocument) r5;
        r0 = r104;
        r5.serializeToStream(r0);
        r56 = r56 + 1;
        goto L_0x0a99;
    L_0x0ab9:
        r86 = 0;
        goto L_0x0a21;
    L_0x0abd:
        r63 = move-exception;
        org.telegram.messenger.FileLog.e(r63);
        goto L_0x0a43;
    L_0x0ac2:
        if (r6 != 0) goto L_0x0b19;
    L_0x0ac4:
        r0 = r19;
        r5 = r0.ttl;
        if (r5 != 0) goto L_0x0b19;
    L_0x0aca:
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r118);
        if (r6 != 0) goto L_0x0b41;
    L_0x0ad0:
        r5 = 0;
    L_0x0ad1:
        r0 = r86;
        r103 = r8.getSentFile(r0, r5);
        if (r103 == 0) goto L_0x0ae3;
    L_0x0ad9:
        r5 = 0;
        r7 = r103[r5];
        r7 = (org.telegram.tgnet.TLRPC.TL_photo) r7;
        r5 = 1;
        r88 = r103[r5];
        r88 = (java.lang.String) r88;
    L_0x0ae3:
        if (r7 != 0) goto L_0x0b0a;
    L_0x0ae5:
        r0 = r19;
        r5 = r0.uri;
        if (r5 == 0) goto L_0x0b0a;
    L_0x0aeb:
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r118);
        r0 = r19;
        r5 = r0.uri;
        r9 = org.telegram.messenger.AndroidUtilities.getPath(r5);
        if (r6 != 0) goto L_0x0b43;
    L_0x0af9:
        r5 = 0;
    L_0x0afa:
        r103 = r8.getSentFile(r9, r5);
        if (r103 == 0) goto L_0x0b0a;
    L_0x0b00:
        r5 = 0;
        r7 = r103[r5];
        r7 = (org.telegram.tgnet.TLRPC.TL_photo) r7;
        r5 = 1;
        r88 = r103[r5];
        r88 = (java.lang.String) r88;
    L_0x0b0a:
        r0 = r19;
        r8 = r0.path;
        r0 = r19;
        r9 = r0.uri;
        r10 = 0;
        r5 = r118;
        ensureMediaThumbExists(r5, r6, r7, r8, r9, r10);
    L_0x0b19:
        if (r7 != 0) goto L_0x0a4d;
    L_0x0b1b:
        r5 = getInstance(r118);
        r0 = r19;
        r8 = r0.path;
        r0 = r19;
        r9 = r0.uri;
        r7 = r5.generatePhotoSizes(r8, r9);
        if (r6 == 0) goto L_0x0a4d;
    L_0x0b2d:
        r0 = r19;
        r5 = r0.canDeleteAfter;
        if (r5 == 0) goto L_0x0a4d;
    L_0x0b33:
        r5 = new java.io.File;
        r0 = r19;
        r8 = r0.path;
        r5.<init>(r8);
        r5.delete();
        goto L_0x0a4d;
    L_0x0b41:
        r5 = 3;
        goto L_0x0ad1;
    L_0x0b43:
        r5 = 3;
        goto L_0x0afa;
    L_0x0b45:
        r5 = 0;
        goto L_0x0a73;
    L_0x0b48:
        r5 = "masks";
        r8 = r104.toByteArray();
        r8 = org.telegram.messenger.Utilities.bytesToHex(r8);
        r14.put(r5, r8);
        r104.cleanup();
    L_0x0b59:
        if (r86 == 0) goto L_0x0b63;
    L_0x0b5b:
        r5 = "originalPath";
        r0 = r86;
        r14.put(r5, r0);
    L_0x0b63:
        if (r120 == 0) goto L_0x0b6c;
    L_0x0b65:
        r5 = r115.size();	 Catch:{ Exception -> 0x0d68 }
        r8 = 1;
        if (r5 != r8) goto L_0x0ccb;
    L_0x0b6c:
        r0 = r23;
        r5 = r0.sizes;	 Catch:{ Exception -> 0x0d68 }
        r8 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0d68 }
        r62 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r8);	 Catch:{ Exception -> 0x0d68 }
        if (r62 == 0) goto L_0x0ccb;
    L_0x0b7a:
        r5 = org.telegram.messenger.AndroidUtilities.isTablet();	 Catch:{ Exception -> 0x0d68 }
        if (r5 == 0) goto L_0x0d1c;
    L_0x0b80:
        r5 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();	 Catch:{ Exception -> 0x0d68 }
        r5 = (float) r5;	 Catch:{ Exception -> 0x0d68 }
        r8 = NUM; // 0x3var_ float:0.7 double:5.23867711E-315;
        r5 = r5 * r8;
        r0 = (int) r5;	 Catch:{ Exception -> 0x0d68 }
        r94 = r0;
        r77 = r94;
    L_0x0b8e:
        r5 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x0d68 }
        r91 = r94 + r5;
        r5 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0d68 }
        r0 = r94;
        if (r0 <= r5) goto L_0x0ba2;
    L_0x0b9e:
        r94 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0d68 }
    L_0x0ba2:
        r5 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0d68 }
        r0 = r91;
        if (r0 <= r5) goto L_0x0bae;
    L_0x0baa:
        r91 = org.telegram.messenger.AndroidUtilities.getPhotoSize();	 Catch:{ Exception -> 0x0d68 }
    L_0x0bae:
        r0 = r62;
        r5 = r0.w;	 Catch:{ Exception -> 0x0d68 }
        r5 = (float) r5;	 Catch:{ Exception -> 0x0d68 }
        r0 = r94;
        r8 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r96 = r5 / r8;
        r0 = r62;
        r5 = r0.w;	 Catch:{ Exception -> 0x0d68 }
        r5 = (float) r5;	 Catch:{ Exception -> 0x0d68 }
        r5 = r5 / r96;
        r0 = (int) r5;	 Catch:{ Exception -> 0x0d68 }
        r112 = r0;
        r0 = r62;
        r5 = r0.h;	 Catch:{ Exception -> 0x0d68 }
        r5 = (float) r5;	 Catch:{ Exception -> 0x0d68 }
        r5 = r5 / r96;
        r0 = (int) r5;	 Catch:{ Exception -> 0x0d68 }
        r72 = r0;
        if (r112 != 0) goto L_0x0bd4;
    L_0x0bce:
        r5 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r112 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x0d68 }
    L_0x0bd4:
        if (r72 != 0) goto L_0x0bdc;
    L_0x0bd6:
        r5 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r72 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x0d68 }
    L_0x0bdc:
        r0 = r72;
        r1 = r91;
        if (r0 <= r1) goto L_0x0d34;
    L_0x0be2:
        r0 = r72;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r97 = r0;
        r72 = r91;
        r0 = r72;
        r5 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r97 = r97 / r5;
        r0 = r112;
        r5 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r5 = r5 / r97;
        r0 = (int) r5;	 Catch:{ Exception -> 0x0d68 }
        r112 = r0;
    L_0x0bf6:
        r5 = 0;
        r8 = java.util.Locale.US;	 Catch:{ Exception -> 0x0d68 }
        r9 = "%d_%d@%d_%d";
        r10 = 4;
        r10 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x0d68 }
        r11 = 0;
        r0 = r62;
        r0 = r0.location;	 Catch:{ Exception -> 0x0d68 }
        r16 = r0;
        r0 = r16;
        r0 = r0.volume_id;	 Catch:{ Exception -> 0x0d68 }
        r16 = r0;
        r16 = java.lang.Long.valueOf(r16);	 Catch:{ Exception -> 0x0d68 }
        r10[r11] = r16;	 Catch:{ Exception -> 0x0d68 }
        r11 = 1;
        r0 = r62;
        r0 = r0.location;	 Catch:{ Exception -> 0x0d68 }
        r16 = r0;
        r0 = r16;
        r0 = r0.local_id;	 Catch:{ Exception -> 0x0d68 }
        r16 = r0;
        r16 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0d68 }
        r10[r11] = r16;	 Catch:{ Exception -> 0x0d68 }
        r11 = 2;
        r0 = r112;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r16 = r0;
        r17 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Exception -> 0x0d68 }
        r16 = r16 / r17;
        r0 = r16;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0d68 }
        r16 = r0;
        r16 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0d68 }
        r10[r11] = r16;	 Catch:{ Exception -> 0x0d68 }
        r11 = 3;
        r0 = r72;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r16 = r0;
        r17 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Exception -> 0x0d68 }
        r16 = r16 / r17;
        r0 = r16;
        r0 = (int) r0;	 Catch:{ Exception -> 0x0d68 }
        r16 = r0;
        r16 = java.lang.Integer.valueOf(r16);	 Catch:{ Exception -> 0x0d68 }
        r10[r11] = r16;	 Catch:{ Exception -> 0x0d68 }
        r8 = java.lang.String.format(r8, r9, r10);	 Catch:{ Exception -> 0x0d68 }
        r42[r5] = r8;	 Catch:{ Exception -> 0x0d68 }
        r85 = new android.graphics.BitmapFactory$Options;	 Catch:{ Exception -> 0x0d68 }
        r85.<init>();	 Catch:{ Exception -> 0x0d68 }
        r5 = 1;
        r0 = r85;
        r0.inJustDecodeBounds = r5;	 Catch:{ Exception -> 0x0d68 }
        r67 = org.telegram.messenger.FileLoader.getPathToAttach(r62);	 Catch:{ Exception -> 0x0d68 }
        r75 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0d68 }
        r0 = r75;
        r1 = r67;
        r0.<init>(r1);	 Catch:{ Exception -> 0x0d68 }
        r5 = 0;
        r0 = r75;
        r1 = r85;
        android.graphics.BitmapFactory.decodeStream(r0, r5, r1);	 Catch:{ Exception -> 0x0d68 }
        r75.close();	 Catch:{ Exception -> 0x0d68 }
        r0 = r85;
        r5 = r0.outWidth;	 Catch:{ Exception -> 0x0d68 }
        r0 = (float) r5;	 Catch:{ Exception -> 0x0d68 }
        r93 = r0;
        r0 = r85;
        r5 = r0.outHeight;	 Catch:{ Exception -> 0x0d68 }
        r0 = (float) r5;	 Catch:{ Exception -> 0x0d68 }
        r90 = r0;
        r0 = r112;
        r5 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r5 = r93 / r5;
        r0 = r72;
        r8 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r8 = r90 / r8;
        r98 = java.lang.Math.max(r5, r8);	 Catch:{ Exception -> 0x0d68 }
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r98 > r5 ? 1 : (r98 == r5 ? 0 : -1));
        if (r5 >= 0) goto L_0x0c9b;
    L_0x0CLASSNAME:
        r98 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0c9b:
        r5 = 0;
        r0 = r85;
        r0.inJustDecodeBounds = r5;	 Catch:{ Exception -> 0x0d68 }
        r0 = r98;
        r5 = (int) r0;	 Catch:{ Exception -> 0x0d68 }
        r0 = r85;
        r0.inSampleSize = r5;	 Catch:{ Exception -> 0x0d68 }
        r5 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Exception -> 0x0d68 }
        r0 = r85;
        r0.inPreferredConfig = r5;	 Catch:{ Exception -> 0x0d68 }
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0d68 }
        r8 = 21;
        if (r5 < r8) goto L_0x0ccb;
    L_0x0cb3:
        r75 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0d68 }
        r0 = r75;
        r1 = r67;
        r0.<init>(r1);	 Catch:{ Exception -> 0x0d68 }
        r5 = 0;
        r8 = 0;
        r0 = r75;
        r1 = r85;
        r8 = android.graphics.BitmapFactory.decodeStream(r0, r8, r1);	 Catch:{ Exception -> 0x0d68 }
        r41[r5] = r8;	 Catch:{ Exception -> 0x0d68 }
        r75.close();	 Catch:{ Exception -> 0x0d68 }
    L_0x0ccb:
        if (r120 == 0) goto L_0x0d02;
    L_0x0ccd:
        r95 = r95 + 1;
        r5 = "groupId";
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "";
        r8 = r8.append(r9);
        r0 = r70;
        r8 = r8.append(r0);
        r8 = r8.toString();
        r14.put(r5, r8);
        r5 = 10;
        r0 = r95;
        if (r0 == r5) goto L_0x0cf7;
    L_0x0cf1:
        r5 = r61 + -1;
        r0 = r54;
        if (r0 != r5) goto L_0x0d02;
    L_0x0cf7:
        r5 = "final";
        r8 = "1";
        r14.put(r5, r8);
        r78 = 0;
    L_0x0d02:
        r40 = new org.telegram.messenger.SendMessagesHelper$$Lambda$27;
        r43 = r121;
        r44 = r118;
        r45 = r23;
        r46 = r14;
        r47 = r15;
        r48 = r116;
        r50 = r122;
        r51 = r19;
        r40.<init>(r41, r42, r43, r44, r45, r46, r47, r48, r50, r51);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r40);
        goto L_0x0399;
    L_0x0d1c:
        r5 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Exception -> 0x0d68 }
        r5 = r5.x;	 Catch:{ Exception -> 0x0d68 }
        r8 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Exception -> 0x0d68 }
        r8 = r8.y;	 Catch:{ Exception -> 0x0d68 }
        r5 = java.lang.Math.min(r5, r8);	 Catch:{ Exception -> 0x0d68 }
        r5 = (float) r5;	 Catch:{ Exception -> 0x0d68 }
        r8 = NUM; // 0x3var_ float:0.7 double:5.23867711E-315;
        r5 = r5 * r8;
        r0 = (int) r5;	 Catch:{ Exception -> 0x0d68 }
        r94 = r0;
        r77 = r94;
        goto L_0x0b8e;
    L_0x0d34:
        r5 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x0d68 }
        r0 = r72;
        if (r0 >= r5) goto L_0x0bf6;
    L_0x0d3e:
        r5 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r72 = org.telegram.messenger.AndroidUtilities.dp(r5);	 Catch:{ Exception -> 0x0d68 }
        r0 = r62;
        r5 = r0.h;	 Catch:{ Exception -> 0x0d68 }
        r5 = (float) r5;	 Catch:{ Exception -> 0x0d68 }
        r0 = r72;
        r8 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r73 = r5 / r8;
        r0 = r62;
        r5 = r0.w;	 Catch:{ Exception -> 0x0d68 }
        r5 = (float) r5;	 Catch:{ Exception -> 0x0d68 }
        r5 = r5 / r73;
        r0 = r94;
        r8 = (float) r0;	 Catch:{ Exception -> 0x0d68 }
        r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1));
        if (r5 >= 0) goto L_0x0bf6;
    L_0x0d5c:
        r0 = r62;
        r5 = r0.w;	 Catch:{ Exception -> 0x0d68 }
        r5 = (float) r5;
        r5 = r5 / r73;
        r0 = (int) r5;
        r112 = r0;
        goto L_0x0bf6;
    L_0x0d68:
        r63 = move-exception;
        org.telegram.messenger.FileLog.e(r63);
        goto L_0x0ccb;
    L_0x0d6e:
        if (r99 != 0) goto L_0x0d84;
    L_0x0d70:
        r99 = new java.util.ArrayList;
        r99.<init>();
        r102 = new java.util.ArrayList;
        r102.<init>();
        r100 = new java.util.ArrayList;
        r100.<init>();
        r101 = new java.util.ArrayList;
        r101.<init>();
    L_0x0d84:
        r0 = r99;
        r1 = r108;
        r0.add(r1);
        r0 = r102;
        r1 = r86;
        r0.add(r1);
        r0 = r19;
        r5 = r0.caption;
        r0 = r100;
        r0.add(r5);
        r0 = r19;
        r5 = r0.entities;
        r0 = r101;
        r0.add(r5);
        goto L_0x0399;
    L_0x0da6:
        r8 = 0;
        r5 = (r78 > r8 ? 1 : (r78 == r8 ? 0 : -1));
        if (r5 == 0) goto L_0x0dba;
    L_0x0dac:
        r80 = r78;
        r5 = new org.telegram.messenger.SendMessagesHelper$$Lambda$28;
        r0 = r118;
        r1 = r80;
        r5.<init>(r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r5);
    L_0x0dba:
        if (r123 == 0) goto L_0x0dbf;
    L_0x0dbc:
        r123.releasePermission();
    L_0x0dbf:
        if (r99 == 0) goto L_0x0e0b;
    L_0x0dc1:
        r5 = r99.isEmpty();
        if (r5 != 0) goto L_0x0e0b;
    L_0x0dc7:
        r54 = 0;
    L_0x0dc9:
        r5 = r99.size();
        r0 = r54;
        if (r0 >= r5) goto L_0x0e0b;
    L_0x0dd1:
        r0 = r99;
        r1 = r54;
        r44 = r0.get(r1);
        r44 = (java.lang.String) r44;
        r0 = r102;
        r1 = r54;
        r45 = r0.get(r1);
        r45 = (java.lang.String) r45;
        r46 = 0;
        r0 = r100;
        r1 = r54;
        r51 = r0.get(r1);
        r51 = (java.lang.CharSequence) r51;
        r0 = r101;
        r1 = r54;
        r52 = r0.get(r1);
        r52 = (java.util.ArrayList) r52;
        r43 = r118;
        r47 = r66;
        r48 = r116;
        r50 = r122;
        r53 = r121;
        prepareSendingDocumentInternal(r43, r44, r45, r46, r47, r48, r50, r51, r52, r53);
        r54 = r54 + 1;
        goto L_0x0dc9;
    L_0x0e0b:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0e2c;
    L_0x0e0f:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r8 = "total send time = ";
        r5 = r5.append(r8);
        r8 = java.lang.System.currentTimeMillis();
        r8 = r8 - r58;
        r5 = r5.append(r8);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x0e2c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$60$SendMessagesHelper(java.util.ArrayList, long, int, boolean, boolean, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, android.support.v13.view.inputmethod.InputContentInfoCompat):void");
    }

    static final /* synthetic */ void lambda$null$54$SendMessagesHelper(MediaSendPrepareWorker worker, int currentAccount, SendingMediaInfo info, boolean isEncrypted) {
        worker.photo = getInstance(currentAccount).generatePhotoSizes(info.path, info.uri);
        if (isEncrypted && info.canDeleteAfter) {
            new File(info.path).delete();
        }
        worker.sync.countDown();
    }

    static final /* synthetic */ void lambda$null$55$SendMessagesHelper(MessageObject editingMessageObject, int currentAccount, TL_document documentFinal, String pathFinal, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, SendingMediaInfo info) {
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, null, documentFinal, pathFinal, params, false, parentFinal);
            return;
        }
        getInstance(currentAccount).sendMessage(documentFinal, null, pathFinal, dialog_id, reply_to_msg, info.caption, info.entities, null, params, 0, parentFinal);
    }

    static final /* synthetic */ void lambda$null$56$SendMessagesHelper(MessageObject editingMessageObject, int currentAccount, TL_photo photoFinal, boolean needDownloadHttpFinal, SendingMediaInfo info, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg) {
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, photoFinal, null, null, needDownloadHttpFinal ? info.searchImage.imageUrl : null, params, false, parentFinal);
        } else {
            getInstance(currentAccount).sendMessage(photoFinal, needDownloadHttpFinal ? info.searchImage.imageUrl : null, dialog_id, reply_to_msg, info.caption, info.entities, null, params, info.ttl, parentFinal);
        }
    }

    static final /* synthetic */ void lambda$null$57$SendMessagesHelper(Bitmap thumbFinal, String thumbKeyFinal, MessageObject editingMessageObject, int currentAccount, VideoEditedInfo videoEditedInfo, TL_document videoFinal, String finalPath, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, SendingMediaInfo info) {
        if (!(thumbFinal == null || thumbKeyFinal == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(thumbFinal), thumbKeyFinal);
        }
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, videoEditedInfo, videoFinal, finalPath, params, false, parentFinal);
            return;
        }
        getInstance(currentAccount).sendMessage(videoFinal, videoEditedInfo, finalPath, dialog_id, reply_to_msg, info.caption, info.entities, null, params, info.ttl, parentFinal);
    }

    static final /* synthetic */ void lambda$null$58$SendMessagesHelper(Bitmap[] bitmapFinal, String[] keyFinal, MessageObject editingMessageObject, int currentAccount, TL_photo photoFinal, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, SendingMediaInfo info) {
        if (!(bitmapFinal[0] == null || keyFinal[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapFinal[0]), keyFinal[0]);
        }
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, photoFinal, null, null, null, params, false, parentFinal);
            return;
        }
        getInstance(currentAccount).sendMessage(photoFinal, null, dialog_id, reply_to_msg, info.caption, info.entities, null, params, info.ttl, parentFinal);
    }

    static final /* synthetic */ void lambda$null$59$SendMessagesHelper(int currentAccount, long lastGroupIdFinal) {
        SendMessagesHelper instance = getInstance(currentAccount);
        ArrayList<DelayedMessage> arrayList = (ArrayList) instance.delayedMessages.get("group_" + lastGroupIdFinal);
        if (arrayList != null && !arrayList.isEmpty()) {
            DelayedMessage message = (DelayedMessage) arrayList.get(0);
            MessageObject prevMessage = (MessageObject) message.messageObjects.get(message.messageObjects.size() - 1);
            message.finalGroupMessage = prevMessage.getId();
            prevMessage.messageOwner.params.put("final", "1");
            messages_Messages messagesRes = new TL_messages_messages();
            messagesRes.messages.add(prevMessage.messageOwner);
            MessagesStorage.getInstance(currentAccount).putMessages(messagesRes, message.peer, -2, 0, false);
            instance.sendReadyToSendGroup(message, true, true);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x00d6 A:{SYNTHETIC, Splitter: B:49:0x00d6} */
    /* JADX WARNING: Removed duplicated region for block: B:60:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006b A:{SYNTHETIC, Splitter: B:26:0x006b} */
    private static void fillVideoAttribute(java.lang.String r16, org.telegram.tgnet.TLRPC.TL_documentAttributeVideo r17, org.telegram.messenger.VideoEditedInfo r18) {
        /*
        r5 = 0;
        r6 = 0;
        r7 = new android.media.MediaMetadataRetriever;	 Catch:{ Exception -> 0x00e7 }
        r7.<init>();	 Catch:{ Exception -> 0x00e7 }
        r0 = r16;
        r7.setDataSource(r0);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r13 = 18;
        r12 = r7.extractMetadata(r13);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        if (r12 == 0) goto L_0x001c;
    L_0x0014:
        r13 = java.lang.Integer.parseInt(r12);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r0 = r17;
        r0.w = r13;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
    L_0x001c:
        r13 = 19;
        r4 = r7.extractMetadata(r13);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        if (r4 == 0) goto L_0x002c;
    L_0x0024:
        r13 = java.lang.Integer.parseInt(r4);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r0 = r17;
        r0.h = r13;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
    L_0x002c:
        r13 = 9;
        r2 = r7.extractMetadata(r13);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        if (r2 == 0) goto L_0x0046;
    L_0x0034:
        r14 = java.lang.Long.parseLong(r2);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r13 = (float) r14;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r14 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r13 = r13 / r14;
        r14 = (double) r13;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r14 = java.lang.Math.ceil(r14);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r13 = (int) r14;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r0 = r17;
        r0.duration = r13;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
    L_0x0046:
        r13 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r14 = 17;
        if (r13 < r14) goto L_0x0062;
    L_0x004c:
        r13 = 24;
        r9 = r7.extractMetadata(r13);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        if (r9 == 0) goto L_0x0062;
    L_0x0054:
        r13 = org.telegram.messenger.Utilities.parseInt(r9);	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r11 = r13.intValue();	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        if (r18 == 0) goto L_0x00a4;
    L_0x005e:
        r0 = r18;
        r0.rotationValue = r11;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
    L_0x0062:
        r5 = 1;
        if (r7 == 0) goto L_0x0068;
    L_0x0065:
        r7.release();	 Catch:{ Exception -> 0x00cd }
    L_0x0068:
        r6 = r7;
    L_0x0069:
        if (r5 != 0) goto L_0x00a3;
    L_0x006b:
        r13 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x00df }
        r14 = new java.io.File;	 Catch:{ Exception -> 0x00df }
        r0 = r16;
        r14.<init>(r0);	 Catch:{ Exception -> 0x00df }
        r14 = android.net.Uri.fromFile(r14);	 Catch:{ Exception -> 0x00df }
        r8 = android.media.MediaPlayer.create(r13, r14);	 Catch:{ Exception -> 0x00df }
        if (r8 == 0) goto L_0x00a3;
    L_0x007e:
        r13 = r8.getDuration();	 Catch:{ Exception -> 0x00df }
        r13 = (float) r13;	 Catch:{ Exception -> 0x00df }
        r14 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r13 = r13 / r14;
        r14 = (double) r13;	 Catch:{ Exception -> 0x00df }
        r14 = java.lang.Math.ceil(r14);	 Catch:{ Exception -> 0x00df }
        r13 = (int) r14;	 Catch:{ Exception -> 0x00df }
        r0 = r17;
        r0.duration = r13;	 Catch:{ Exception -> 0x00df }
        r13 = r8.getVideoWidth();	 Catch:{ Exception -> 0x00df }
        r0 = r17;
        r0.w = r13;	 Catch:{ Exception -> 0x00df }
        r13 = r8.getVideoHeight();	 Catch:{ Exception -> 0x00df }
        r0 = r17;
        r0.h = r13;	 Catch:{ Exception -> 0x00df }
        r8.release();	 Catch:{ Exception -> 0x00df }
    L_0x00a3:
        return;
    L_0x00a4:
        r13 = 90;
        if (r11 == r13) goto L_0x00ac;
    L_0x00a8:
        r13 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r11 != r13) goto L_0x0062;
    L_0x00ac:
        r0 = r17;
        r10 = r0.w;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r0 = r17;
        r13 = r0.h;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r0 = r17;
        r0.w = r13;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        r0 = r17;
        r0.h = r10;	 Catch:{ Exception -> 0x00bd, all -> 0x00e4 }
        goto L_0x0062;
    L_0x00bd:
        r3 = move-exception;
        r6 = r7;
    L_0x00bf:
        org.telegram.messenger.FileLog.e(r3);	 Catch:{ all -> 0x00d3 }
        if (r6 == 0) goto L_0x0069;
    L_0x00c4:
        r6.release();	 Catch:{ Exception -> 0x00c8 }
        goto L_0x0069;
    L_0x00c8:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x0069;
    L_0x00cd:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        r6 = r7;
        goto L_0x0069;
    L_0x00d3:
        r13 = move-exception;
    L_0x00d4:
        if (r6 == 0) goto L_0x00d9;
    L_0x00d6:
        r6.release();	 Catch:{ Exception -> 0x00da }
    L_0x00d9:
        throw r13;
    L_0x00da:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x00d9;
    L_0x00df:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        goto L_0x00a3;
    L_0x00e4:
        r13 = move-exception;
        r6 = r7;
        goto L_0x00d4;
    L_0x00e7:
        r3 = move-exception;
        goto L_0x00bf;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.fillVideoAttribute(java.lang.String, org.telegram.tgnet.TLRPC$TL_documentAttributeVideo, org.telegram.messenger.VideoEditedInfo):void");
    }

    private static Bitmap createVideoThumbnail(String filePath, long time) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(time, 1);
            try {
                retriever.release();
            } catch (RuntimeException e) {
            }
        } catch (Exception e2) {
            try {
                retriever.release();
            } catch (RuntimeException e3) {
            }
        } catch (Throwable th) {
            try {
                retriever.release();
            } catch (RuntimeException e4) {
            }
            throw th;
        }
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int max = Math.max(width, height);
        if (((float) max) > 320.0f) {
            float scale = 320.0f / ((float) max);
            Bitmap scaled = Bitmaps.createScaledBitmap(bitmap, Math.round(((float) width) * scale), Math.round(((float) height) * scale), true);
            if (scaled != bitmap) {
                bitmap.recycle();
                bitmap = scaled;
            }
        }
        Bitmap bitmap2 = bitmap;
        return bitmap;
    }

    private static VideoEditedInfo createCompressionSettings(String videoPath) {
        TrackHeaderBox trackHeaderBox = null;
        int originalBitrate = 0;
        int bitrate = 0;
        float videoDuration = 0.0f;
        long videoFramesSize = 0;
        long audioFramesSize = 0;
        int videoFramerate = 25;
        try {
            IsoFile isoFile = new IsoFile(videoPath);
            List<Box> boxes = Path.getPaths((Container) isoFile, "/moov/trak/");
            if (Path.getPath((Container) isoFile, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") == null && BuildVars.LOGS_ENABLED) {
                FileLog.d("video hasn't mp4a atom");
            }
            if (Path.getPath((Container) isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("video hasn't avc1 atom");
                }
                return null;
            }
            for (int b = 0; b < boxes.size(); b++) {
                int a;
                TrackBox trackBox = (TrackBox) ((Box) boxes.get(b));
                long sampleSizes = 0;
                long trackBitrate = 0;
                MediaBox mediaBox = null;
                MediaHeaderBox mediaHeaderBox = null;
                try {
                    mediaBox = trackBox.getMediaBox();
                    mediaHeaderBox = mediaBox.getMediaHeaderBox();
                    for (long j : mediaBox.getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes()) {
                        sampleSizes += j;
                    }
                    videoDuration = ((float) mediaHeaderBox.getDuration()) / ((float) mediaHeaderBox.getTimescale());
                    trackBitrate = (long) ((int) (((float) (8 * sampleSizes)) / videoDuration));
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                TrackHeaderBox headerBox = trackBox.getTrackHeaderBox();
                if (headerBox.getWidth() == 0.0d || headerBox.getHeight() == 0.0d) {
                    audioFramesSize += sampleSizes;
                } else if (trackHeaderBox == null || trackHeaderBox.getWidth() < headerBox.getWidth() || trackHeaderBox.getHeight() < headerBox.getHeight()) {
                    trackHeaderBox = headerBox;
                    bitrate = (int) ((trackBitrate / 100000) * 100000);
                    originalBitrate = bitrate;
                    if (bitrate > 900000) {
                        bitrate = 900000;
                    }
                    videoFramesSize += sampleSizes;
                    if (!(mediaBox == null || mediaHeaderBox == null)) {
                        TimeToSampleBox timeToSampleBox = mediaBox.getMediaInformationBox().getSampleTableBox().getTimeToSampleBox();
                        if (timeToSampleBox != null) {
                            List<TimeToSampleBox.Entry> entries = timeToSampleBox.getEntries();
                            long delta = 0;
                            int size = Math.min(entries.size(), 11);
                            for (a = 1; a < size; a++) {
                                delta += ((TimeToSampleBox.Entry) entries.get(a)).getDelta();
                            }
                            if (delta != 0) {
                                videoFramerate = (int) (((double) mediaHeaderBox.getTimescale()) / ((double) (delta / ((long) (size - 1)))));
                            } else {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                }
            }
            if (trackHeaderBox == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("video hasn't trackHeaderBox atom");
                }
                return null;
            }
            int compressionsCount;
            if (VERSION.SDK_INT < 18) {
                try {
                    MediaCodecInfo codecInfo = MediaController.selectCodec("video/avc");
                    if (codecInfo == null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("no codec info for video/avc");
                        }
                        return null;
                    }
                    String name = codecInfo.getName();
                    if (name.equals("OMX.google.h264.encoder") || name.equals("OMX.ST.VFM.H264Enc") || name.equals("OMX.Exynos.avc.enc") || name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") || name.equals("OMX.MARVELL.VIDEO.H264ENCODER") || name.equals("OMX.k3.video.encoder.avc") || name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("unsupported encoder = " + name);
                        }
                        return null;
                    } else if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("no color format for video/avc");
                        }
                        return null;
                    }
                } catch (Exception e2) {
                    return null;
                }
            }
            videoDuration *= 1000.0f;
            VideoEditedInfo videoEditedInfo = new VideoEditedInfo();
            videoEditedInfo.startTime = -1;
            videoEditedInfo.endTime = -1;
            videoEditedInfo.bitrate = bitrate;
            videoEditedInfo.originalPath = videoPath;
            videoEditedInfo.framerate = videoFramerate;
            videoEditedInfo.estimatedDuration = (long) Math.ceil((double) videoDuration);
            int width = (int) trackHeaderBox.getWidth();
            videoEditedInfo.originalWidth = width;
            videoEditedInfo.resultWidth = width;
            width = (int) trackHeaderBox.getHeight();
            videoEditedInfo.originalHeight = width;
            videoEditedInfo.resultHeight = width;
            Matrix matrix = trackHeaderBox.getMatrix();
            if (matrix.equals(Matrix.ROTATE_90)) {
                videoEditedInfo.rotationValue = 90;
            } else if (matrix.equals(Matrix.ROTATE_180)) {
                videoEditedInfo.rotationValue = 180;
            } else if (matrix.equals(Matrix.ROTATE_270)) {
                videoEditedInfo.rotationValue = 270;
            } else {
                videoEditedInfo.rotationValue = 0;
            }
            int selectedCompression = MessagesController.getGlobalMainSettings().getInt("compress_video2", 1);
            if (videoEditedInfo.originalWidth > 1280 || videoEditedInfo.originalHeight > 1280) {
                compressionsCount = 5;
            } else if (videoEditedInfo.originalWidth > 848 || videoEditedInfo.originalHeight > 848) {
                compressionsCount = 4;
            } else if (videoEditedInfo.originalWidth > 640 || videoEditedInfo.originalHeight > 640) {
                compressionsCount = 3;
            } else if (videoEditedInfo.originalWidth > 480 || videoEditedInfo.originalHeight > 480) {
                compressionsCount = 2;
            } else {
                compressionsCount = 1;
            }
            if (selectedCompression >= compressionsCount) {
                selectedCompression = compressionsCount - 1;
            }
            if (selectedCompression != compressionsCount - 1) {
                float maxSize;
                int targetBitrate;
                float scale;
                switch (selectedCompression) {
                    case 0:
                        maxSize = 432.0f;
                        targetBitrate = 400000;
                        break;
                    case 1:
                        maxSize = 640.0f;
                        targetBitrate = 900000;
                        break;
                    case 2:
                        maxSize = 848.0f;
                        targetBitrate = 1100000;
                        break;
                    default:
                        targetBitrate = 2500000;
                        maxSize = 1280.0f;
                        break;
                }
                if (videoEditedInfo.originalWidth > videoEditedInfo.originalHeight) {
                    scale = maxSize / ((float) videoEditedInfo.originalWidth);
                } else {
                    scale = maxSize / ((float) videoEditedInfo.originalHeight);
                }
                videoEditedInfo.resultWidth = Math.round((((float) videoEditedInfo.originalWidth) * scale) / 2.0f) * 2;
                videoEditedInfo.resultHeight = Math.round((((float) videoEditedInfo.originalHeight) * scale) / 2.0f) * 2;
                if (bitrate != 0) {
                    bitrate = Math.min(targetBitrate, (int) (((float) originalBitrate) / scale));
                    videoFramesSize = (long) ((((float) (bitrate / 8)) * videoDuration) / 1000.0f);
                }
            }
            if (selectedCompression == compressionsCount - 1) {
                videoEditedInfo.resultWidth = videoEditedInfo.originalWidth;
                videoEditedInfo.resultHeight = videoEditedInfo.originalHeight;
                videoEditedInfo.bitrate = originalBitrate;
                videoEditedInfo.estimatedSize = (long) ((int) new File(videoPath).length());
                return videoEditedInfo;
            }
            videoEditedInfo.bitrate = bitrate;
            videoEditedInfo.estimatedSize = (long) ((int) (audioFramesSize + videoFramesSize));
            videoEditedInfo.estimatedSize += (videoEditedInfo.estimatedSize / 32768) * 16;
            return videoEditedInfo;
        } catch (Throwable e3) {
            FileLog.e(e3);
            return null;
        }
    }

    public static void prepareSendingVideo(String videoPath, long estimatedSize, long duration, int width, int height, VideoEditedInfo info, long dialog_id, MessageObject reply_to_msg, CharSequence caption, ArrayList<MessageEntity> entities, int ttl, MessageObject editingMessageObject) {
        if (videoPath != null && videoPath.length() != 0) {
            new Thread(new SendMessagesHelper$$Lambda$21(info, videoPath, dialog_id, duration, ttl, UserConfig.selectedAccount, height, width, estimatedSize, caption, editingMessageObject, reply_to_msg, entities)).start();
        }
    }

    static final /* synthetic */ void lambda$prepareSendingVideo$62$SendMessagesHelper(VideoEditedInfo info, String videoPath, long dialog_id, long duration, int ttl, int currentAccount, int height, int width, long estimatedSize, CharSequence caption, MessageObject editingMessageObject, MessageObject reply_to_msg, ArrayList entities) {
        VideoEditedInfo videoEditedInfo = info != null ? info : createCompressionSettings(videoPath);
        boolean isEncrypted = ((int) dialog_id) == 0;
        boolean isRound = videoEditedInfo != null && videoEditedInfo.roundVideo;
        Bitmap thumb = null;
        String thumbKey = null;
        if (videoEditedInfo == null) {
            if (!(videoPath.endsWith("mp4") || isRound)) {
                prepareSendingDocumentInternal(currentAccount, videoPath, videoPath, null, null, dialog_id, reply_to_msg, caption, entities, editingMessageObject);
                return;
            }
        }
        String path = videoPath;
        String originalPath = videoPath;
        File file = new File(originalPath);
        long startTime = 0;
        originalPath = originalPath + file.length() + "_" + file.lastModified();
        if (videoEditedInfo != null) {
            if (!isRound) {
                originalPath = originalPath + duration + "_" + videoEditedInfo.startTime + "_" + videoEditedInfo.endTime + (videoEditedInfo.muted ? "_m" : "");
                if (videoEditedInfo.resultWidth != videoEditedInfo.originalWidth) {
                    originalPath = originalPath + "_" + videoEditedInfo.resultWidth;
                }
            }
            startTime = videoEditedInfo.startTime >= 0 ? videoEditedInfo.startTime : 0;
        }
        TL_document document = null;
        String parentObject = null;
        if (!isEncrypted && ttl == 0) {
            Object[] sentData = MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, !isEncrypted ? 2 : 5);
            if (sentData != null) {
                document = sentData[0];
                parentObject = sentData[1];
                ensureMediaThumbExists(currentAccount, isEncrypted, document, videoPath, null, startTime);
            }
        }
        if (document == null) {
            TL_documentAttributeVideo attributeVideo;
            thumb = createVideoThumbnail(videoPath, startTime);
            if (thumb == null) {
                thumb = ThumbnailUtils.createVideoThumbnail(videoPath, 1);
            }
            int side = isEncrypted ? 90 : 320;
            PhotoSize size = ImageLoader.scaleAndSaveImage(thumb, (float) side, (float) side, 55, isEncrypted);
            if (!(thumb == null || size == null)) {
                if (!isRound) {
                    thumb = null;
                } else if (isEncrypted) {
                    Utilities.blurBitmap(thumb, 7, VERSION.SDK_INT < 21 ? 0 : 1, thumb.getWidth(), thumb.getHeight(), thumb.getRowBytes());
                    thumbKey = String.format(size.location.volume_id + "_" + size.location.local_id + "@%d_%d_b2", new Object[]{Integer.valueOf((int) (((float) AndroidUtilities.roundMessageSize) / AndroidUtilities.density)), Integer.valueOf((int) (((float) AndroidUtilities.roundMessageSize) / AndroidUtilities.density))});
                } else {
                    Utilities.blurBitmap(thumb, 3, VERSION.SDK_INT < 21 ? 0 : 1, thumb.getWidth(), thumb.getHeight(), thumb.getRowBytes());
                    thumbKey = String.format(size.location.volume_id + "_" + size.location.local_id + "@%d_%d_b", new Object[]{Integer.valueOf((int) (((float) AndroidUtilities.roundMessageSize) / AndroidUtilities.density)), Integer.valueOf((int) (((float) AndroidUtilities.roundMessageSize) / AndroidUtilities.density))});
                }
            }
            document = new TL_document();
            if (size != null) {
                document.thumbs.add(size);
                document.flags |= 1;
            }
            document.file_reference = new byte[0];
            document.mime_type = "video/mp4";
            UserConfig.getInstance(currentAccount).saveConfig(false);
            if (isEncrypted) {
                EncryptedChat encryptedChat = MessagesController.getInstance(currentAccount).getEncryptedChat(Integer.valueOf((int) (dialog_id >> 32)));
                if (encryptedChat != null) {
                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 66) {
                        attributeVideo = new TL_documentAttributeVideo();
                    } else {
                        attributeVideo = new TL_documentAttributeVideo_layer65();
                    }
                } else {
                    return;
                }
            }
            attributeVideo = new TL_documentAttributeVideo();
            attributeVideo.supports_streaming = true;
            attributeVideo.round_message = isRound;
            document.attributes.add(attributeVideo);
            if (videoEditedInfo == null || !videoEditedInfo.needConvert()) {
                if (file.exists()) {
                    document.size = (int) file.length();
                }
                fillVideoAttribute(videoPath, attributeVideo, null);
            } else {
                if (videoEditedInfo.muted) {
                    document.attributes.add(new TL_documentAttributeAnimated());
                    fillVideoAttribute(videoPath, attributeVideo, videoEditedInfo);
                    videoEditedInfo.originalWidth = attributeVideo.w;
                    videoEditedInfo.originalHeight = attributeVideo.h;
                } else {
                    attributeVideo.duration = (int) (duration / 1000);
                }
                if (videoEditedInfo.rotationValue == 90 || videoEditedInfo.rotationValue == 270) {
                    attributeVideo.w = height;
                    attributeVideo.h = width;
                } else {
                    attributeVideo.w = width;
                    attributeVideo.h = height;
                }
                document.size = (int) estimatedSize;
            }
        }
        if (videoEditedInfo != null && videoEditedInfo.needConvert()) {
            file = new File(FileLoader.getDirectory(4), "-2147483648_" + SharedConfig.getLastLocalId() + ".mp4");
            SharedConfig.saveConfig();
            path = file.getAbsolutePath();
        }
        TL_document videoFinal = document;
        String parentFinal = parentObject;
        String originalPathFinal = originalPath;
        String finalPath = path;
        HashMap<String, String> params = new HashMap();
        Bitmap thumbFinal = thumb;
        String thumbKeyFinal = thumbKey;
        String captionFinal = caption != null ? caption.toString() : "";
        if (originalPath != null) {
            params.put("originalPath", originalPath);
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$22(thumbFinal, thumbKeyFinal, editingMessageObject, currentAccount, videoEditedInfo, videoFinal, finalPath, params, parentFinal, dialog_id, reply_to_msg, captionFinal, entities, ttl));
    }

    static final /* synthetic */ void lambda$null$61$SendMessagesHelper(Bitmap thumbFinal, String thumbKeyFinal, MessageObject editingMessageObject, int currentAccount, VideoEditedInfo videoEditedInfo, TL_document videoFinal, String finalPath, HashMap params, String parentFinal, long dialog_id, MessageObject reply_to_msg, String captionFinal, ArrayList entities, int ttl) {
        if (!(thumbFinal == null || thumbKeyFinal == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(thumbFinal), thumbKeyFinal);
        }
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, videoEditedInfo, videoFinal, finalPath, params, false, parentFinal);
        } else {
            getInstance(currentAccount).sendMessage(videoFinal, videoEditedInfo, finalPath, dialog_id, reply_to_msg, captionFinal, entities, null, params, ttl, parentFinal);
        }
    }
}
