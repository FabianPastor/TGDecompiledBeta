package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaCodecInfo;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
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
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.support.SparseLongArray;
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
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFile;
import org.telegram.tgnet.TLRPC.TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
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
    private LocationProvider locationProvider = new LocationProvider(new C04321());
    private SparseArray<Message> sendingMessages = new SparseArray();
    private SparseArray<MessageObject> unsentMessages = new SparseArray();
    private HashMap<String, Boolean> waitingForCallback = new HashMap();
    private HashMap<String, MessageObject> waitingForLocation = new HashMap();

    /* renamed from: org.telegram.messenger.SendMessagesHelper$1 */
    class C04321 implements LocationProviderDelegate {
        C04321() {
        }

        public void onLocationAcquired(Location location) {
            SendMessagesHelper.this.sendLocation(location);
            SendMessagesHelper.this.waitingForLocation.clear();
        }

        public void onUnableLocationAcquire() {
            HashMap<String, MessageObject> waitingForLocationCopy = new HashMap(SendMessagesHelper.this.waitingForLocation);
            NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, waitingForLocationCopy);
            SendMessagesHelper.this.waitingForLocation.clear();
        }
    }

    protected class DelayedMessage {
        public EncryptedChat encryptedChat;
        public HashMap<Object, Object> extraHashMap;
        public int finalGroupMessage;
        public long groupId;
        public String httpLocation;
        public FileLocation location;
        public ArrayList<MessageObject> messageObjects;
        public ArrayList<Message> messages;
        public MessageObject obj;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public long peer;
        ArrayList<DelayedMessageSendAfterRequest> requests;
        public TLObject sendEncryptedRequest;
        public TLObject sendRequest;
        public int type;
        public boolean upload;
        public VideoEditedInfo videoEditedInfo;

        public DelayedMessage(long peer) {
            this.peer = peer;
        }

        public void addDelayedRequest(TLObject req, MessageObject msgObj, String originalPath) {
            DelayedMessageSendAfterRequest request = new DelayedMessageSendAfterRequest();
            request.request = req;
            request.msgObj = msgObj;
            request.originalPath = originalPath;
            if (this.requests == null) {
                this.requests = new ArrayList();
            }
            this.requests.add(request);
        }

        public void addDelayedRequest(TLObject req, ArrayList<MessageObject> msgObjs, ArrayList<String> originalPaths) {
            DelayedMessageSendAfterRequest request = new DelayedMessageSendAfterRequest();
            request.request = req;
            request.msgObjs = msgObjs;
            request.originalPaths = originalPaths;
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
                        SendMessagesHelper.this.performSendMessageRequestMulti((TL_messages_sendMultiMedia) request.request, request.msgObjs, request.originalPaths);
                    } else {
                        SendMessagesHelper.this.performSendMessageRequest(request.request, request.msgObj, request.originalPath);
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
        public MessageObject msgObj;
        public ArrayList<MessageObject> msgObjs;
        public String originalPath;
        public ArrayList<String> originalPaths;
        public TLObject request;

        protected DelayedMessageSendAfterRequest() {
        }
    }

    public static class LocationProvider {
        private LocationProviderDelegate delegate;
        private GpsLocationListener gpsLocationListener = new GpsLocationListener();
        private Location lastKnownLocation;
        private LocationManager locationManager;
        private Runnable locationQueryCancelRunnable;
        private GpsLocationListener networkLocationListener = new GpsLocationListener();

        public interface LocationProviderDelegate {
            void onLocationAcquired(Location location);

            void onUnableLocationAcquire();
        }

        /* renamed from: org.telegram.messenger.SendMessagesHelper$LocationProvider$1 */
        class C04331 implements Runnable {
            C04331() {
            }

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
        }

        private class GpsLocationListener implements LocationListener {
            private GpsLocationListener() {
            }

            public void onLocationChanged(Location location) {
                if (location != null && LocationProvider.this.locationQueryCancelRunnable != null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m5d("found location " + location);
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
                FileLog.m8e(e);
            }
            try {
                this.locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Throwable e2) {
                FileLog.m8e(e2);
            }
            try {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                if (this.lastKnownLocation == null) {
                    this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
                }
            } catch (Throwable e22) {
                FileLog.m8e(e22);
            }
            if (this.locationQueryCancelRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
            }
            this.locationQueryCancelRunnable = new C04331();
            AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
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
        public volatile TL_photo photo;
        public CountDownLatch sync;

        private MediaSendPrepareWorker() {
        }
    }

    public static class SendingMediaInfo {
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
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FilePreparingStarted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailedLoad);
    }

    public void cleanup() {
        this.delayedMessages.clear();
        this.unsentMessages.clear();
        this.sendingMessages.clear();
        this.waitingForLocation.clear();
        this.waitingForCallback.clear();
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
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath, message, true);
                        } else if (message.type == 1) {
                            if (media.file == null) {
                                media.file = file;
                                if (media.thumb != null || message.location == null) {
                                    performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                                } else {
                                    performSendDelayedMessage(message);
                                }
                            } else {
                                media.thumb = file;
                                media.flags |= 4;
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                            }
                        } else if (message.type == 2) {
                            if (media.file == null) {
                                media.file = file;
                                if (media.thumb != null || message.location == null) {
                                    performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                                } else {
                                    performSendDelayedMessage(message);
                                }
                            } else {
                                media.thumb = file;
                                media.flags |= 4;
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                            }
                        } else if (message.type == 3) {
                            media.file = file;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
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
        } else if (id == NotificationCenter.FilePreparingStarted) {
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
        } else if (id == NotificationCenter.FileNewChunkAvailable) {
            messageObject = (MessageObject) args[0];
            if (messageObject.getId() != 0) {
                finalPath = (String) args[1];
                long availableSize = ((Long) args[2]).longValue();
                long finalSize = ((Long) args[3]).longValue();
                FileLoader.getInstance(this.currentAccount).checkUploadNewDataAvailable(finalPath, ((int) messageObject.getDialogId()) == 0, availableSize, finalSize);
                if (finalSize != 0) {
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
        } else if (id == NotificationCenter.FilePreparingFailed) {
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
        } else if (id == NotificationCenter.httpFileDidLoaded) {
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
        } else if (id == NotificationCenter.FileDidLoaded) {
            path = (String) args[0];
            arr = (ArrayList) this.delayedMessages.get(path);
            if (arr != null) {
                for (a = 0; a < arr.size(); a++) {
                    performSendDelayedMessage((DelayedMessage) arr.get(a));
                }
                this.delayedMessages.remove(path);
            }
        } else if (id == NotificationCenter.httpFileDidFailedLoad || id == NotificationCenter.FileDidFailedLoad) {
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$61(this, generatePhotoSizes(cacheFile.toString(), null), messageObject, cacheFile, message, path));
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
                performSendDelayedMessage(message, message.messageObjects.indexOf(messageObject));
                return;
            } else {
                performSendDelayedMessage(message);
                return;
            }
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m6e("can't load image " + path + " to file " + cacheFile.toString());
        }
        message.markAsError();
    }

    final /* synthetic */ void lambda$didReceivedNotification$4$SendMessagesHelper(DelayedMessage message, File cacheFile, MessageObject messageObject) {
        boolean z = true;
        Document document = message.obj.getDocument();
        if (document.thumb.location instanceof TL_fileLocationUnavailable) {
            try {
                Bitmap bitmap = ImageLoader.loadBitmap(cacheFile.getAbsolutePath(), null, 90.0f, 90.0f, true);
                if (bitmap != null) {
                    if (message.sendEncryptedRequest == null) {
                        z = false;
                    }
                    document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, z);
                    bitmap.recycle();
                }
            } catch (Throwable e) {
                document.thumb = null;
                FileLog.m8e(e);
            }
            if (document.thumb == null) {
                document.thumb = new TL_photoSizeEmpty();
                document.thumb.type = "s";
            }
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$60(this, message, cacheFile, document, messageObject));
    }

    final /* synthetic */ void lambda$null$3$SendMessagesHelper(DelayedMessage message, File cacheFile, Document document, MessageObject messageObject) {
        message.httpLocation = null;
        message.obj.messageOwner.attachPath = cacheFile.toString();
        message.location = document.thumb.location;
        ArrayList messages = new ArrayList();
        messages.add(messageObject.messageOwner);
        MessagesStorage.getInstance(this.currentAccount).putMessages(messages, false, true, false, 0);
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
                int a = 0;
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
        } else {
            MessagesController.getInstance(this.currentAccount).deleteMessages(messageIds, null, null, channelId, false);
        }
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean unsent) {
        if (messageObject.getId() >= 0) {
            if (messageObject.isEditing()) {
                editMessageMedia(messageObject, null, null, null, null, null, true);
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
                sendMessage((TL_photo) messageObject.messageOwner.media.photo, null, did, messageObject.replyMessageObject, messageObject.messageOwner.message, messageObject.messageOwner.entities, null, null, messageObject.messageOwner.media.ttl_seconds);
            } else if (messageObject.messageOwner.media.document instanceof TL_document) {
                sendMessage((TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, messageObject.replyMessageObject, messageObject.messageOwner.message, messageObject.messageOwner.entities, null, null, messageObject.messageOwner.media.ttl_seconds);
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
            TL_messages_sendScreenshotNotification req = new TL_messages_sendScreenshotNotification();
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
            performSendMessageRequest(req, newMsgObj, null);
        }
    }

    public void sendSticker(Document document, long peer, MessageObject replyingMessageObject) {
        if (document != null) {
            if (((int) peer) == 0) {
                if (MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (peer >> 32))) != null) {
                    Document newDocument = new TL_document();
                    newDocument.id = document.id;
                    newDocument.access_hash = document.access_hash;
                    newDocument.date = document.date;
                    newDocument.mime_type = document.mime_type;
                    newDocument.size = document.size;
                    newDocument.dc_id = document.dc_id;
                    newDocument.attributes = new ArrayList(document.attributes);
                    if (newDocument.mime_type == null) {
                        newDocument.mime_type = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    if (document.thumb instanceof TL_photoSize) {
                        File file = FileLoader.getPathToAttach(document.thumb, true);
                        if (file.exists()) {
                            try {
                                int len = (int) file.length();
                                byte[] arr = new byte[((int) file.length())];
                                new RandomAccessFile(file, "r").readFully(arr);
                                newDocument.thumb = new TL_photoCachedSize();
                                newDocument.thumb.location = document.thumb.location;
                                newDocument.thumb.size = document.thumb.size;
                                newDocument.thumb.f34w = document.thumb.f34w;
                                newDocument.thumb.f33h = document.thumb.f33h;
                                newDocument.thumb.type = document.thumb.type;
                                newDocument.thumb.bytes = arr;
                            } catch (Throwable e) {
                                FileLog.m8e(e);
                            }
                        }
                    }
                    if (newDocument.thumb == null) {
                        newDocument.thumb = new TL_photoSizeEmpty();
                        newDocument.thumb.type = "s";
                    }
                    document = newDocument;
                } else {
                    return;
                }
            }
            if (document instanceof TL_document) {
                sendMessage((TL_document) document, null, null, peer, replyingMessageObject, null, null, null, null, 0);
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
            boolean canSendPreview = true;
            if (lower_id <= 0) {
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
                if (ChatObject.isChannel(chat)) {
                    isMegagroup = chat.megagroup;
                    isSignature = chat.signatures;
                    if (chat.banned_rights != null) {
                        canSendStickers = !chat.banned_rights.send_stickers;
                        canSendMedia = !chat.banned_rights.send_media;
                        canSendPreview = !chat.banned_rights.embed_links;
                    }
                }
            } else if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id)) == null) {
                return 0;
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
                                newMsg.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
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
                                FileLog.m5d("forward message user_id = " + inputPeer.user_id + " chat_id = " + inputPeer.chat_id + " channel_id = " + inputPeer.channel_id + " access_hash = " + inputPeer.access_hash);
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
                                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(msgObj.messageOwner.to_id.channel_id));
                                req.from_peer = new TL_inputPeerChannel();
                                req.from_peer.channel_id = msgObj.messageOwner.to_id.channel_id;
                                if (chat != null) {
                                    req.from_peer.access_hash = chat.access_hash;
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
                            sendResult = 2;
                        }
                    } else if (sendResult == 0) {
                        sendResult = 1;
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
                                newMsgObj1.id = message.id;
                                sentCount++;
                                updateMediaPaths(msgObj1, message, null, true);
                                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$56(this, newMsgObj1, oldId, to_id, sentMessages, peer, message));
                            }
                        }
                    }
                }
                a1++;
            }
            if (!updates.updates.isEmpty()) {
                MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            }
            StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, sentCount);
        } else {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$57(this, error, req));
        }
        for (a1 = 0; a1 < newMsgObjArr.size(); a1++) {
            newMsgObj1 = (Message) newMsgObjArr.get(a1);
            MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsgObj1);
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$58(this, newMsgObj1));
        }
    }

    final /* synthetic */ void lambda$null$6$SendMessagesHelper(Message newMsgObj1, int oldId, Peer to_id, ArrayList sentMessages, long peer, Message message) {
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(newMsgObj1.random_id, Integer.valueOf(oldId), newMsgObj1.id, 0, false, to_id.channel_id);
        MessagesStorage.getInstance(this.currentAccount).putMessages(sentMessages, true, false, false, 0);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$59(this, newMsgObj1, peer, oldId, message));
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
        data.writeString(message.message != null ? message.message : TtmlNode.ANONYMOUS_REGION_ID);
        data.writeString(message.attachPath != null ? message.attachPath : TtmlNode.ANONYMOUS_REGION_ID);
        int count = message.entities.size();
        data.writeInt32(count);
        for (int a = 0; a < count; a++) {
            ((MessageEntity) message.entities.get(a)).serializeToStream(data);
        }
    }

    private void editMessageMedia(org.telegram.messenger.MessageObject r37, org.telegram.tgnet.TLRPC.TL_photo r38, org.telegram.messenger.VideoEditedInfo r39, org.telegram.tgnet.TLRPC.TL_document r40, java.lang.String r41, java.util.HashMap<java.lang.String, java.lang.String> r42, boolean r43) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r17_0 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage) in PHI: PHI: (r17_3 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage) = (r17_0 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage), (r17_0 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage), (r17_1 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage), (r17_1 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage), (r17_2 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage), (r17_2 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage) binds: {(r17_0 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage)=B:98:0x02ff, (r17_0 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage)=B:99:?, (r17_1 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage)=B:134:0x0438, (r17_1 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage)=B:135:?, (r17_2 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage)=B:155:0x04f4, (r17_2 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage)=B:156:?}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r36 = this;
        if (r37 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r0 = r37;
        r0 = r0.messageOwner;
        r25 = r0;
        r4 = 0;
        r0 = r37;
        r0.cancelEditing = r4;
        r34 = -1;
        r16 = 0;
        r28 = r37.getDialogId();	 Catch:{ Exception -> 0x0248 }
        if (r43 == 0) goto L_0x01a1;	 Catch:{ Exception -> 0x0248 }
    L_0x0018:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.media;	 Catch:{ Exception -> 0x0248 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x017f;	 Catch:{ Exception -> 0x0248 }
    L_0x0022:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.media;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.photo;	 Catch:{ Exception -> 0x0248 }
        r0 = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;	 Catch:{ Exception -> 0x0248 }
        r38 = r0;	 Catch:{ Exception -> 0x0248 }
        r34 = 2;	 Catch:{ Exception -> 0x0248 }
    L_0x0031:
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0 = r0.params;	 Catch:{ Exception -> 0x0248 }
        r42 = r0;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.message;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.editingMessage = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.entities;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.editingMessageEntities = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0 = r0.attachPath;	 Catch:{ Exception -> 0x0248 }
        r41 = r0;	 Catch:{ Exception -> 0x0248 }
    L_0x004d:
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.attachPath;	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x005a;	 Catch:{ Exception -> 0x0248 }
    L_0x0053:
        r4 = "";	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0.attachPath = r4;	 Catch:{ Exception -> 0x0248 }
    L_0x005a:
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0.local_id = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.type;	 Catch:{ Exception -> 0x0248 }
        r6 = 3;	 Catch:{ Exception -> 0x0248 }
        if (r4 == r6) goto L_0x006f;	 Catch:{ Exception -> 0x0248 }
    L_0x0066:
        if (r39 != 0) goto L_0x006f;	 Catch:{ Exception -> 0x0248 }
    L_0x0068:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.type;	 Catch:{ Exception -> 0x0248 }
        r6 = 2;	 Catch:{ Exception -> 0x0248 }
        if (r4 != r6) goto L_0x007e;	 Catch:{ Exception -> 0x0248 }
    L_0x006f:
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.attachPath;	 Catch:{ Exception -> 0x0248 }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x007e;	 Catch:{ Exception -> 0x0248 }
    L_0x0079:
        r4 = 1;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.attachPathExists = r4;	 Catch:{ Exception -> 0x0248 }
    L_0x007e:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.videoEditedInfo;	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x008c;	 Catch:{ Exception -> 0x0248 }
    L_0x0084:
        if (r39 != 0) goto L_0x008c;	 Catch:{ Exception -> 0x0248 }
    L_0x0086:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0 = r0.videoEditedInfo;	 Catch:{ Exception -> 0x0248 }
        r39 = r0;	 Catch:{ Exception -> 0x0248 }
    L_0x008c:
        if (r43 != 0) goto L_0x00fe;	 Catch:{ Exception -> 0x0248 }
    L_0x008e:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.editingMessage;	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x00b6;	 Catch:{ Exception -> 0x0248 }
    L_0x0094:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.editingMessage;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0.message = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.editingMessageEntities;	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x02ba;	 Catch:{ Exception -> 0x0248 }
    L_0x00a6:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.editingMessageEntities;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0.entities = r4;	 Catch:{ Exception -> 0x0248 }
    L_0x00ae:
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.caption = r4;	 Catch:{ Exception -> 0x0248 }
        r37.generateCaption();	 Catch:{ Exception -> 0x0248 }
    L_0x00b6:
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0248 }
        r5.<init>();	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r5.add(r0);	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0248 }
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);	 Catch:{ Exception -> 0x0248 }
        r6 = 0;	 Catch:{ Exception -> 0x0248 }
        r7 = 1;	 Catch:{ Exception -> 0x0248 }
        r8 = 0;	 Catch:{ Exception -> 0x0248 }
        r9 = 0;	 Catch:{ Exception -> 0x0248 }
        r4.putMessages(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x0248 }
        r4 = -1;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.type = r4;	 Catch:{ Exception -> 0x0248 }
        r37.setType();	 Catch:{ Exception -> 0x0248 }
        r37.createMessageSendInfo();	 Catch:{ Exception -> 0x0248 }
        r14 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0248 }
        r14.<init>();	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r14.add(r0);	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0248 }
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);	 Catch:{ Exception -> 0x0248 }
        r6 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects;	 Catch:{ Exception -> 0x0248 }
        r8 = 2;	 Catch:{ Exception -> 0x0248 }
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0248 }
        r9 = 0;	 Catch:{ Exception -> 0x0248 }
        r10 = java.lang.Long.valueOf(r28);	 Catch:{ Exception -> 0x0248 }
        r8[r9] = r10;	 Catch:{ Exception -> 0x0248 }
        r9 = 1;	 Catch:{ Exception -> 0x0248 }
        r8[r9] = r14;	 Catch:{ Exception -> 0x0248 }
        r4.postNotificationName(r6, r8);	 Catch:{ Exception -> 0x0248 }
    L_0x00fe:
        r26 = 0;	 Catch:{ Exception -> 0x0248 }
        if (r42 == 0) goto L_0x0118;	 Catch:{ Exception -> 0x0248 }
    L_0x0102:
        r4 = "originalPath";	 Catch:{ Exception -> 0x0248 }
        r0 = r42;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x0118;	 Catch:{ Exception -> 0x0248 }
    L_0x010d:
        r4 = "originalPath";	 Catch:{ Exception -> 0x0248 }
        r0 = r42;	 Catch:{ Exception -> 0x0248 }
        r26 = r0.get(r4);	 Catch:{ Exception -> 0x0248 }
        r26 = (java.lang.String) r26;	 Catch:{ Exception -> 0x0248 }
    L_0x0118:
        r4 = 1;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 < r4) goto L_0x0122;	 Catch:{ Exception -> 0x0248 }
    L_0x011d:
        r4 = 3;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 <= r4) goto L_0x012d;	 Catch:{ Exception -> 0x0248 }
    L_0x0122:
        r4 = 5;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 < r4) goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x0127:
        r4 = 8;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 > r4) goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x012d:
        r20 = 0;	 Catch:{ Exception -> 0x0248 }
        r4 = 2;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 != r4) goto L_0x03f3;	 Catch:{ Exception -> 0x0248 }
    L_0x0134:
        r0 = r38;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r10 = 0;	 Catch:{ Exception -> 0x0248 }
        r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x03cd;	 Catch:{ Exception -> 0x0248 }
    L_0x013e:
        r20 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x0248 }
        r20.<init>();	 Catch:{ Exception -> 0x0248 }
        if (r42 == 0) goto L_0x02f1;	 Catch:{ Exception -> 0x0248 }
    L_0x0145:
        r4 = "masks";	 Catch:{ Exception -> 0x0248 }
        r0 = r42;	 Catch:{ Exception -> 0x0248 }
        r22 = r0.get(r4);	 Catch:{ Exception -> 0x0248 }
        r22 = (java.lang.String) r22;	 Catch:{ Exception -> 0x0248 }
        if (r22 == 0) goto L_0x02f1;	 Catch:{ Exception -> 0x0248 }
    L_0x0152:
        r32 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0248 }
        r4 = org.telegram.messenger.Utilities.hexToBytes(r22);	 Catch:{ Exception -> 0x0248 }
        r0 = r32;	 Catch:{ Exception -> 0x0248 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x0248 }
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r32;	 Catch:{ Exception -> 0x0248 }
        r15 = r0.readInt32(r4);	 Catch:{ Exception -> 0x0248 }
        r12 = 0;	 Catch:{ Exception -> 0x0248 }
    L_0x0165:
        if (r12 >= r15) goto L_0x02e4;	 Catch:{ Exception -> 0x0248 }
    L_0x0167:
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.stickers;	 Catch:{ Exception -> 0x0248 }
        r6 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r32;	 Catch:{ Exception -> 0x0248 }
        r6 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0248 }
        r8 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r32;	 Catch:{ Exception -> 0x0248 }
        r6 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r0, r6, r8);	 Catch:{ Exception -> 0x0248 }
        r4.add(r6);	 Catch:{ Exception -> 0x0248 }
        r12 = r12 + 1;	 Catch:{ Exception -> 0x0248 }
        goto L_0x0165;	 Catch:{ Exception -> 0x0248 }
    L_0x017f:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.media;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.document;	 Catch:{ Exception -> 0x0248 }
        r0 = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x0248 }
        r40 = r0;	 Catch:{ Exception -> 0x0248 }
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r40);	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x0194;	 Catch:{ Exception -> 0x0248 }
    L_0x0192:
        if (r39 == 0) goto L_0x019e;	 Catch:{ Exception -> 0x0248 }
    L_0x0194:
        r34 = 3;	 Catch:{ Exception -> 0x0248 }
    L_0x0196:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0 = r0.videoEditedInfo;	 Catch:{ Exception -> 0x0248 }
        r39 = r0;	 Catch:{ Exception -> 0x0248 }
        goto L_0x0031;	 Catch:{ Exception -> 0x0248 }
    L_0x019e:
        r34 = 7;	 Catch:{ Exception -> 0x0248 }
        goto L_0x0196;	 Catch:{ Exception -> 0x0248 }
    L_0x01a1:
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.media;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.previousMedia = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.message;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.previousCaption = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.entities;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.previousCaptionEntities = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.attachPath;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.previousAttachPath = r4;	 Catch:{ Exception -> 0x0248 }
        r33 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0248 }
        r4 = 1;	 Catch:{ Exception -> 0x0248 }
        r0 = r33;	 Catch:{ Exception -> 0x0248 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r25;	 Catch:{ Exception -> 0x0248 }
        r2 = r33;	 Catch:{ Exception -> 0x0248 }
        r0.writePreviousMessageData(r1, r2);	 Catch:{ Exception -> 0x0248 }
        r30 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0248 }
        r4 = r33.length();	 Catch:{ Exception -> 0x0248 }
        r0 = r30;	 Catch:{ Exception -> 0x0248 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r25;	 Catch:{ Exception -> 0x0248 }
        r2 = r30;	 Catch:{ Exception -> 0x0248 }
        r0.writePreviousMessageData(r1, r2);	 Catch:{ Exception -> 0x0248 }
        if (r42 != 0) goto L_0x01ef;	 Catch:{ Exception -> 0x0248 }
    L_0x01e8:
        r27 = new java.util.HashMap;	 Catch:{ Exception -> 0x0248 }
        r27.<init>();	 Catch:{ Exception -> 0x0248 }
        r42 = r27;	 Catch:{ Exception -> 0x0248 }
    L_0x01ef:
        r4 = "prevMedia";	 Catch:{ Exception -> 0x0248 }
        r6 = r30.toByteArray();	 Catch:{ Exception -> 0x0248 }
        r8 = 0;	 Catch:{ Exception -> 0x0248 }
        r6 = android.util.Base64.encodeToString(r6, r8);	 Catch:{ Exception -> 0x0248 }
        r0 = r42;	 Catch:{ Exception -> 0x0248 }
        r0.put(r4, r6);	 Catch:{ Exception -> 0x0248 }
        r30.cleanup();	 Catch:{ Exception -> 0x0248 }
        if (r38 == 0) goto L_0x0279;	 Catch:{ Exception -> 0x0248 }
    L_0x0205:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x0248 }
        r4.<init>();	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0.media = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.media;	 Catch:{ Exception -> 0x0248 }
        r6 = r4.flags;	 Catch:{ Exception -> 0x0248 }
        r6 = r6 | 3;	 Catch:{ Exception -> 0x0248 }
        r4.flags = r6;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.media;	 Catch:{ Exception -> 0x0248 }
        r0 = r38;	 Catch:{ Exception -> 0x0248 }
        r4.photo = r0;	 Catch:{ Exception -> 0x0248 }
        r34 = 2;	 Catch:{ Exception -> 0x0248 }
        if (r41 == 0) goto L_0x0251;	 Catch:{ Exception -> 0x0248 }
    L_0x0224:
        r4 = r41.length();	 Catch:{ Exception -> 0x0248 }
        if (r4 <= 0) goto L_0x0251;	 Catch:{ Exception -> 0x0248 }
    L_0x022a:
        r4 = "http";	 Catch:{ Exception -> 0x0248 }
        r0 = r41;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x0251;	 Catch:{ Exception -> 0x0248 }
    L_0x0235:
        r0 = r41;	 Catch:{ Exception -> 0x0248 }
        r1 = r25;	 Catch:{ Exception -> 0x0248 }
        r1.attachPath = r0;	 Catch:{ Exception -> 0x0248 }
    L_0x023b:
        r0 = r42;	 Catch:{ Exception -> 0x0248 }
        r1 = r25;	 Catch:{ Exception -> 0x0248 }
        r1.params = r0;	 Catch:{ Exception -> 0x0248 }
        r4 = 3;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0.send_state = r4;	 Catch:{ Exception -> 0x0248 }
        goto L_0x004d;
    L_0x0248:
        r18 = move-exception;
    L_0x0249:
        org.telegram.messenger.FileLog.m8e(r18);
        r36.revertEditingMessageObject(r37);
        goto L_0x0002;
    L_0x0251:
        r0 = r38;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0248 }
        r0 = r38;	 Catch:{ Exception -> 0x0248 }
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0248 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0248 }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0248 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r4.location;	 Catch:{ Exception -> 0x0248 }
        r21 = r0;	 Catch:{ Exception -> 0x0248 }
        r4 = 1;	 Catch:{ Exception -> 0x0248 }
        r0 = r21;	 Catch:{ Exception -> 0x0248 }
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r4);	 Catch:{ Exception -> 0x0248 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0.attachPath = r4;	 Catch:{ Exception -> 0x0248 }
        goto L_0x023b;	 Catch:{ Exception -> 0x0248 }
    L_0x0279:
        if (r40 == 0) goto L_0x023b;	 Catch:{ Exception -> 0x0248 }
    L_0x027b:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0248 }
        r4.<init>();	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r0.media = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.media;	 Catch:{ Exception -> 0x0248 }
        r6 = r4.flags;	 Catch:{ Exception -> 0x0248 }
        r6 = r6 | 3;	 Catch:{ Exception -> 0x0248 }
        r4.flags = r6;	 Catch:{ Exception -> 0x0248 }
        r0 = r25;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.media;	 Catch:{ Exception -> 0x0248 }
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r4.document = r0;	 Catch:{ Exception -> 0x0248 }
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r40);	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x029e;	 Catch:{ Exception -> 0x0248 }
    L_0x029c:
        if (r39 == 0) goto L_0x02b7;	 Catch:{ Exception -> 0x0248 }
    L_0x029e:
        r34 = 3;	 Catch:{ Exception -> 0x0248 }
    L_0x02a0:
        if (r39 == 0) goto L_0x02b0;	 Catch:{ Exception -> 0x0248 }
    L_0x02a2:
        r35 = r39.getString();	 Catch:{ Exception -> 0x0248 }
        r4 = "ve";	 Catch:{ Exception -> 0x0248 }
        r0 = r42;	 Catch:{ Exception -> 0x0248 }
        r1 = r35;	 Catch:{ Exception -> 0x0248 }
        r0.put(r4, r1);	 Catch:{ Exception -> 0x0248 }
    L_0x02b0:
        r0 = r41;	 Catch:{ Exception -> 0x0248 }
        r1 = r25;	 Catch:{ Exception -> 0x0248 }
        r1.attachPath = r0;	 Catch:{ Exception -> 0x0248 }
        goto L_0x023b;	 Catch:{ Exception -> 0x0248 }
    L_0x02b7:
        r34 = 7;	 Catch:{ Exception -> 0x0248 }
        goto L_0x02a0;	 Catch:{ Exception -> 0x0248 }
    L_0x02ba:
        r4 = 1;	 Catch:{ Exception -> 0x0248 }
        r0 = new java.lang.CharSequence[r4];	 Catch:{ Exception -> 0x0248 }
        r24 = r0;	 Catch:{ Exception -> 0x0248 }
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r6 = r0.editingMessage;	 Catch:{ Exception -> 0x0248 }
        r24[r4] = r6;	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0248 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0248 }
        r0 = r24;	 Catch:{ Exception -> 0x0248 }
        r19 = r4.getEntities(r0);	 Catch:{ Exception -> 0x0248 }
        if (r19 == 0) goto L_0x00ae;	 Catch:{ Exception -> 0x0248 }
    L_0x02d6:
        r4 = r19.isEmpty();	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x00ae;	 Catch:{ Exception -> 0x0248 }
    L_0x02dc:
        r0 = r19;	 Catch:{ Exception -> 0x0248 }
        r1 = r25;	 Catch:{ Exception -> 0x0248 }
        r1.entities = r0;	 Catch:{ Exception -> 0x0248 }
        goto L_0x00ae;	 Catch:{ Exception -> 0x0248 }
    L_0x02e4:
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0248 }
        r4 = r4 | 1;	 Catch:{ Exception -> 0x0248 }
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0248 }
        r32.cleanup();	 Catch:{ Exception -> 0x0248 }
    L_0x02f1:
        if (r16 != 0) goto L_0x0311;	 Catch:{ Exception -> 0x0248 }
    L_0x02f3:
        r17 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0248 }
        r0 = r17;	 Catch:{ Exception -> 0x0248 }
        r1 = r36;	 Catch:{ Exception -> 0x0248 }
        r2 = r28;	 Catch:{ Exception -> 0x0248 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0248 }
        r4 = 0;
        r0 = r17;	 Catch:{ Exception -> 0x0609 }
        r0.type = r4;	 Catch:{ Exception -> 0x0609 }
        r0 = r37;	 Catch:{ Exception -> 0x0609 }
        r1 = r17;	 Catch:{ Exception -> 0x0609 }
        r1.obj = r0;	 Catch:{ Exception -> 0x0609 }
        r0 = r26;	 Catch:{ Exception -> 0x0609 }
        r1 = r17;	 Catch:{ Exception -> 0x0609 }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0609 }
        r16 = r17;
    L_0x0311:
        if (r41 == 0) goto L_0x03b1;
    L_0x0313:
        r4 = r41.length();	 Catch:{ Exception -> 0x0248 }
        if (r4 <= 0) goto L_0x03b1;	 Catch:{ Exception -> 0x0248 }
    L_0x0319:
        r4 = "http";	 Catch:{ Exception -> 0x0248 }
        r0 = r41;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x03b1;	 Catch:{ Exception -> 0x0248 }
    L_0x0324:
        r0 = r41;	 Catch:{ Exception -> 0x0248 }
        r1 = r16;	 Catch:{ Exception -> 0x0248 }
        r1.httpLocation = r0;	 Catch:{ Exception -> 0x0248 }
    L_0x032a:
        r31 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage;	 Catch:{ Exception -> 0x0248 }
        r31.<init>();	 Catch:{ Exception -> 0x0248 }
        r4 = r37.getId();	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r0.id = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0248 }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x0248 }
        r0 = r28;	 Catch:{ Exception -> 0x0248 }
        r6 = (int) r0;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.getInputPeer(r6);	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r0.peer = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0248 }
        r4 = r4 | 16384;	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r1 = r31;	 Catch:{ Exception -> 0x0248 }
        r1.media = r0;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.editingMessage;	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x0398;	 Catch:{ Exception -> 0x0248 }
    L_0x0360:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.editingMessage;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r0.message = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0248 }
        r4 = r4 | 2048;	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.editingMessageEntities;	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x0538;	 Catch:{ Exception -> 0x0248 }
    L_0x037c:
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.editingMessageEntities;	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r0.entities = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0248 }
        r4 = r4 | 8;	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0248 }
    L_0x038e:
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.editingMessage = r4;	 Catch:{ Exception -> 0x0248 }
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.editingMessageEntities = r4;	 Catch:{ Exception -> 0x0248 }
    L_0x0398:
        if (r16 == 0) goto L_0x03a0;	 Catch:{ Exception -> 0x0248 }
    L_0x039a:
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r1 = r16;	 Catch:{ Exception -> 0x0248 }
        r1.sendRequest = r0;	 Catch:{ Exception -> 0x0248 }
    L_0x03a0:
        r7 = r31;	 Catch:{ Exception -> 0x0248 }
        r4 = 1;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 != r4) goto L_0x056c;	 Catch:{ Exception -> 0x0248 }
    L_0x03a7:
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.performSendMessageRequest(r7, r1, r4);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x03b1:
        r0 = r38;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0248 }
        r0 = r38;	 Catch:{ Exception -> 0x0248 }
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0248 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0248 }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0248 }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.location;	 Catch:{ Exception -> 0x0248 }
        r0 = r16;	 Catch:{ Exception -> 0x0248 }
        r0.location = r4;	 Catch:{ Exception -> 0x0248 }
        goto L_0x032a;	 Catch:{ Exception -> 0x0248 }
    L_0x03cd:
        r23 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x0248 }
        r23.<init>();	 Catch:{ Exception -> 0x0248 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x0248 }
        r4.<init>();	 Catch:{ Exception -> 0x0248 }
        r0 = r23;	 Catch:{ Exception -> 0x0248 }
        r0.id = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r23;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0248 }
        r0 = r38;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0248 }
        r4.id = r8;	 Catch:{ Exception -> 0x0248 }
        r0 = r23;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0248 }
        r0 = r38;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0248 }
        r20 = r23;	 Catch:{ Exception -> 0x0248 }
        goto L_0x032a;	 Catch:{ Exception -> 0x0248 }
    L_0x03f3:
        r4 = 3;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 != r4) goto L_0x0482;	 Catch:{ Exception -> 0x0248 }
    L_0x03f8:
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r10 = 0;	 Catch:{ Exception -> 0x0248 }
        r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x045c;	 Catch:{ Exception -> 0x0248 }
    L_0x0402:
        r20 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0248 }
        r20.<init>();	 Catch:{ Exception -> 0x0248 }
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x0248 }
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r0.mime_type = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0248 }
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r0.attributes = r4;	 Catch:{ Exception -> 0x0248 }
        r4 = r37.isGif();	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x042a;	 Catch:{ Exception -> 0x0248 }
    L_0x041d:
        if (r39 == 0) goto L_0x0425;	 Catch:{ Exception -> 0x0248 }
    L_0x041f:
        r0 = r39;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.muted;	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x042a;	 Catch:{ Exception -> 0x0248 }
    L_0x0425:
        r4 = 1;	 Catch:{ Exception -> 0x0248 }
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r0.nosound_video = r4;	 Catch:{ Exception -> 0x0248 }
    L_0x042a:
        if (r16 != 0) goto L_0x044a;	 Catch:{ Exception -> 0x0248 }
    L_0x042c:
        r17 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0248 }
        r0 = r17;	 Catch:{ Exception -> 0x0248 }
        r1 = r36;	 Catch:{ Exception -> 0x0248 }
        r2 = r28;	 Catch:{ Exception -> 0x0248 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0248 }
        r4 = 1;
        r0 = r17;	 Catch:{ Exception -> 0x0609 }
        r0.type = r4;	 Catch:{ Exception -> 0x0609 }
        r0 = r37;	 Catch:{ Exception -> 0x0609 }
        r1 = r17;	 Catch:{ Exception -> 0x0609 }
        r1.obj = r0;	 Catch:{ Exception -> 0x0609 }
        r0 = r26;	 Catch:{ Exception -> 0x0609 }
        r1 = r17;	 Catch:{ Exception -> 0x0609 }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0609 }
        r16 = r17;
    L_0x044a:
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0248 }
        r4 = r4.location;	 Catch:{ Exception -> 0x0248 }
        r0 = r16;	 Catch:{ Exception -> 0x0248 }
        r0.location = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r39;	 Catch:{ Exception -> 0x0248 }
        r1 = r16;	 Catch:{ Exception -> 0x0248 }
        r1.videoEditedInfo = r0;	 Catch:{ Exception -> 0x0248 }
        goto L_0x032a;	 Catch:{ Exception -> 0x0248 }
    L_0x045c:
        r23 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0248 }
        r23.<init>();	 Catch:{ Exception -> 0x0248 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0248 }
        r4.<init>();	 Catch:{ Exception -> 0x0248 }
        r0 = r23;	 Catch:{ Exception -> 0x0248 }
        r0.id = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r23;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0248 }
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0248 }
        r4.id = r8;	 Catch:{ Exception -> 0x0248 }
        r0 = r23;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0248 }
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0248 }
        r20 = r23;	 Catch:{ Exception -> 0x0248 }
        goto L_0x032a;	 Catch:{ Exception -> 0x0248 }
    L_0x0482:
        r4 = 7;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 != r4) goto L_0x032a;	 Catch:{ Exception -> 0x0248 }
    L_0x0487:
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r10 = 0;	 Catch:{ Exception -> 0x0248 }
        r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x0512;	 Catch:{ Exception -> 0x0248 }
    L_0x0491:
        if (r26 == 0) goto L_0x04e4;	 Catch:{ Exception -> 0x0248 }
    L_0x0493:
        r4 = r26.length();	 Catch:{ Exception -> 0x0248 }
        if (r4 <= 0) goto L_0x04e4;	 Catch:{ Exception -> 0x0248 }
    L_0x0499:
        r4 = "http";	 Catch:{ Exception -> 0x0248 }
        r0 = r26;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0248 }
        if (r4 == 0) goto L_0x04e4;	 Catch:{ Exception -> 0x0248 }
    L_0x04a4:
        if (r42 == 0) goto L_0x04e4;	 Catch:{ Exception -> 0x0248 }
    L_0x04a6:
        r20 = new org.telegram.tgnet.TLRPC$TL_inputMediaGifExternal;	 Catch:{ Exception -> 0x0248 }
        r20.<init>();	 Catch:{ Exception -> 0x0248 }
        r4 = "url";	 Catch:{ Exception -> 0x0248 }
        r0 = r42;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0248 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0248 }
        r6 = "\\|";	 Catch:{ Exception -> 0x0248 }
        r13 = r4.split(r6);	 Catch:{ Exception -> 0x0248 }
        r4 = r13.length;	 Catch:{ Exception -> 0x0248 }
        r6 = 2;	 Catch:{ Exception -> 0x0248 }
        if (r4 != r6) goto L_0x04d2;	 Catch:{ Exception -> 0x0248 }
    L_0x04c1:
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r0 = (org.telegram.tgnet.TLRPC.TL_inputMediaGifExternal) r0;	 Catch:{ Exception -> 0x0248 }
        r4 = r0;	 Catch:{ Exception -> 0x0248 }
        r6 = 0;	 Catch:{ Exception -> 0x0248 }
        r6 = r13[r6];	 Catch:{ Exception -> 0x0248 }
        r4.url = r6;	 Catch:{ Exception -> 0x0248 }
        r4 = 1;	 Catch:{ Exception -> 0x0248 }
        r4 = r13[r4];	 Catch:{ Exception -> 0x0248 }
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r0.f30q = r4;	 Catch:{ Exception -> 0x0248 }
    L_0x04d2:
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x0248 }
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r0.mime_type = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0248 }
        r0 = r20;	 Catch:{ Exception -> 0x0248 }
        r0.attributes = r4;	 Catch:{ Exception -> 0x0248 }
        goto L_0x032a;	 Catch:{ Exception -> 0x0248 }
    L_0x04e4:
        r20 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0248 }
        r20.<init>();	 Catch:{ Exception -> 0x0248 }
        r17 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0248 }
        r0 = r17;	 Catch:{ Exception -> 0x0248 }
        r1 = r36;	 Catch:{ Exception -> 0x0248 }
        r2 = r28;	 Catch:{ Exception -> 0x0248 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0248 }
        r0 = r26;	 Catch:{ Exception -> 0x0609 }
        r1 = r17;	 Catch:{ Exception -> 0x0609 }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0609 }
        r4 = 2;	 Catch:{ Exception -> 0x0609 }
        r0 = r17;	 Catch:{ Exception -> 0x0609 }
        r0.type = r4;	 Catch:{ Exception -> 0x0609 }
        r0 = r37;	 Catch:{ Exception -> 0x0609 }
        r1 = r17;	 Catch:{ Exception -> 0x0609 }
        r1.obj = r0;	 Catch:{ Exception -> 0x0609 }
        r0 = r40;	 Catch:{ Exception -> 0x0609 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0609 }
        r4 = r4.location;	 Catch:{ Exception -> 0x0609 }
        r0 = r17;	 Catch:{ Exception -> 0x0609 }
        r0.location = r4;	 Catch:{ Exception -> 0x0609 }
        r16 = r17;
        goto L_0x04d2;
    L_0x0512:
        r23 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0248 }
        r23.<init>();	 Catch:{ Exception -> 0x0248 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0248 }
        r4.<init>();	 Catch:{ Exception -> 0x0248 }
        r0 = r23;	 Catch:{ Exception -> 0x0248 }
        r0.id = r4;	 Catch:{ Exception -> 0x0248 }
        r0 = r23;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0248 }
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0248 }
        r4.id = r8;	 Catch:{ Exception -> 0x0248 }
        r0 = r23;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0248 }
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0248 }
        r20 = r23;	 Catch:{ Exception -> 0x0248 }
        goto L_0x032a;	 Catch:{ Exception -> 0x0248 }
    L_0x0538:
        r4 = 1;	 Catch:{ Exception -> 0x0248 }
        r0 = new java.lang.CharSequence[r4];	 Catch:{ Exception -> 0x0248 }
        r24 = r0;	 Catch:{ Exception -> 0x0248 }
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r37;	 Catch:{ Exception -> 0x0248 }
        r6 = r0.editingMessage;	 Catch:{ Exception -> 0x0248 }
        r24[r4] = r6;	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0248 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0248 }
        r0 = r24;	 Catch:{ Exception -> 0x0248 }
        r19 = r4.getEntities(r0);	 Catch:{ Exception -> 0x0248 }
        if (r19 == 0) goto L_0x038e;	 Catch:{ Exception -> 0x0248 }
    L_0x0554:
        r4 = r19.isEmpty();	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x038e;	 Catch:{ Exception -> 0x0248 }
    L_0x055a:
        r0 = r19;	 Catch:{ Exception -> 0x0248 }
        r1 = r31;	 Catch:{ Exception -> 0x0248 }
        r1.entities = r0;	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0248 }
        r4 = r4 | 8;	 Catch:{ Exception -> 0x0248 }
        r0 = r31;	 Catch:{ Exception -> 0x0248 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0248 }
        goto L_0x038e;	 Catch:{ Exception -> 0x0248 }
    L_0x056c:
        r4 = 2;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 != r4) goto L_0x0590;	 Catch:{ Exception -> 0x0248 }
    L_0x0571:
        r0 = r38;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r10 = 0;	 Catch:{ Exception -> 0x0248 }
        r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x0584;	 Catch:{ Exception -> 0x0248 }
    L_0x057b:
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r16;	 Catch:{ Exception -> 0x0248 }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x0584:
        r9 = 0;	 Catch:{ Exception -> 0x0248 }
        r10 = 0;	 Catch:{ Exception -> 0x0248 }
        r11 = 1;	 Catch:{ Exception -> 0x0248 }
        r6 = r36;	 Catch:{ Exception -> 0x0248 }
        r8 = r37;	 Catch:{ Exception -> 0x0248 }
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x0590:
        r4 = 3;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 != r4) goto L_0x05b2;	 Catch:{ Exception -> 0x0248 }
    L_0x0595:
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r10 = 0;	 Catch:{ Exception -> 0x0248 }
        r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x05a8;	 Catch:{ Exception -> 0x0248 }
    L_0x059f:
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r16;	 Catch:{ Exception -> 0x0248 }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x05a8:
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.performSendMessageRequest(r7, r1, r4);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x05b2:
        r4 = 6;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 != r4) goto L_0x05c1;	 Catch:{ Exception -> 0x0248 }
    L_0x05b7:
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.performSendMessageRequest(r7, r1, r4);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x05c1:
        r4 = 7;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 != r4) goto L_0x05e6;	 Catch:{ Exception -> 0x0248 }
    L_0x05c6:
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r10 = 0;	 Catch:{ Exception -> 0x0248 }
        r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x05db;	 Catch:{ Exception -> 0x0248 }
    L_0x05d0:
        if (r16 == 0) goto L_0x05db;	 Catch:{ Exception -> 0x0248 }
    L_0x05d2:
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r16;	 Catch:{ Exception -> 0x0248 }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x05db:
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r37;	 Catch:{ Exception -> 0x0248 }
        r2 = r26;	 Catch:{ Exception -> 0x0248 }
        r0.performSendMessageRequest(r7, r1, r2);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x05e6:
        r4 = 8;	 Catch:{ Exception -> 0x0248 }
        r0 = r34;	 Catch:{ Exception -> 0x0248 }
        if (r0 != r4) goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x05ec:
        r0 = r40;	 Catch:{ Exception -> 0x0248 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0248 }
        r10 = 0;	 Catch:{ Exception -> 0x0248 }
        r4 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));	 Catch:{ Exception -> 0x0248 }
        if (r4 != 0) goto L_0x05ff;	 Catch:{ Exception -> 0x0248 }
    L_0x05f6:
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r16;	 Catch:{ Exception -> 0x0248 }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;	 Catch:{ Exception -> 0x0248 }
    L_0x05ff:
        r4 = 0;	 Catch:{ Exception -> 0x0248 }
        r0 = r36;	 Catch:{ Exception -> 0x0248 }
        r1 = r37;	 Catch:{ Exception -> 0x0248 }
        r0.performSendMessageRequest(r7, r1, r4);	 Catch:{ Exception -> 0x0248 }
        goto L_0x0002;
    L_0x0609:
        r18 = move-exception;
        r16 = r17;
        goto L_0x0249;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessageMedia(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean):void");
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
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$55(this, error, fragment, req));
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SendMessagesHelper$$Lambda$53(this, key), 2);
        MessagesController.getInstance(this.currentAccount).markDialogAsRead(dialogId, msgId, msgId, 0, false, 0, true);
    }

    final /* synthetic */ void lambda$null$12$SendMessagesHelper(String key) {
        Boolean bool = (Boolean) this.waitingForCallback.remove(key);
    }

    final /* synthetic */ void lambda$null$13$SendMessagesHelper(String key, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$54(this, key));
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
            RequestDelegate requestDelegate = new SendMessagesHelper$$Lambda$6(this, key, cacheFinal, messageObject, button, parentFragment);
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

    final /* synthetic */ void lambda$sendCallback$16$SendMessagesHelper(String key, boolean cacheFinal, MessageObject messageObject, KeyboardButton button, ChatActivity parentFragment, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$52(this, key, cacheFinal, response, messageObject, button, parentFragment));
    }

    final /* synthetic */ void lambda$null$15$SendMessagesHelper(String key, boolean cacheFinal, TLObject response, MessageObject messageObject, KeyboardButton button, ChatActivity parentFragment) {
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
                    if (!res.alert) {
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
                        parentFragment.showAlert(name, res.message);
                    } else if (parentFragment.getParentActivity() != null) {
                        Builder builder = new Builder(parentFragment.getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", C0431R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0431R.string.OK), null);
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
            request.message = TtmlNode.ANONYMOUS_REGION_ID;
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
                        FileLog.m8e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new SendMessagesHelper$$Lambda$7(this, newTaskId));
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.m8e(e);
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new SendMessagesHelper$$Lambda$7(this, newTaskId));
                }
                newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
            } else {
                newTaskId = taskId;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new SendMessagesHelper$$Lambda$7(this, newTaskId));
        }
    }

    final /* synthetic */ void lambda$sendGame$17$SendMessagesHelper(long newTaskId, TLObject response, TL_error error) {
        if (error == null) {
            MessagesController.getInstance(this.currentAccount).processUpdates((Updates) response, false);
        }
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
    }

    public void sendMessage(MessageObject retryMessageObject) {
        sendMessage(null, null, null, null, null, null, null, null, retryMessageObject.getDialogId(), retryMessageObject.messageOwner.attachPath, null, null, true, retryMessageObject, null, retryMessageObject.messageOwner.reply_markup, retryMessageObject.messageOwner.params, 0);
    }

    public void sendMessage(User user, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, null, user, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params, 0);
    }

    public void sendMessage(TL_document document, VideoEditedInfo videoEditedInfo, String path, long peer, MessageObject reply_to_msg, String caption, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl) {
        sendMessage(null, caption, null, null, videoEditedInfo, null, document, null, peer, path, reply_to_msg, null, true, null, entities, replyMarkup, params, ttl);
    }

    public void sendMessage(String message, long peer, MessageObject reply_to_msg, WebPage webPage, boolean searchLinks, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(message, null, null, null, null, null, null, null, peer, null, reply_to_msg, webPage, searchLinks, null, entities, replyMarkup, params, 0);
    }

    public void sendMessage(MessageMedia location, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, location, null, null, null, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params, 0);
    }

    public void sendMessage(TL_game game, long peer, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, null, null, null, game, peer, null, null, null, true, null, null, replyMarkup, params, 0);
    }

    public void sendMessage(TL_photo photo, String path, long peer, MessageObject reply_to_msg, String caption, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl) {
        sendMessage(null, caption, null, photo, null, null, null, null, peer, path, reply_to_msg, null, true, null, entities, replyMarkup, params, ttl);
    }

    private void sendMessage(java.lang.String r68, java.lang.String r69, org.telegram.tgnet.TLRPC.MessageMedia r70, org.telegram.tgnet.TLRPC.TL_photo r71, org.telegram.messenger.VideoEditedInfo r72, org.telegram.tgnet.TLRPC.User r73, org.telegram.tgnet.TLRPC.TL_document r74, org.telegram.tgnet.TLRPC.TL_game r75, long r76, java.lang.String r78, org.telegram.messenger.MessageObject r79, org.telegram.tgnet.TLRPC.WebPage r80, boolean r81, org.telegram.messenger.MessageObject r82, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r83, org.telegram.tgnet.TLRPC.ReplyMarkup r84, java.util.HashMap<java.lang.String, java.lang.String> r85, int r86) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r28_41 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage) in PHI: PHI: (r28_42 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage) = (r28_41 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage), (r28_43 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage) binds: {(r28_41 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage)=B:409:?, (r28_43 'delayedMessage' org.telegram.messenger.SendMessagesHelper$DelayedMessage)=B:888:0x1aa4}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r67 = this;
        if (r73 == 0) goto L_0x0009;
    L_0x0002:
        r0 = r73;
        r4 = r0.phone;
        if (r4 != 0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r8 = 0;
        r4 = (r76 > r8 ? 1 : (r76 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x0008;
    L_0x000f:
        if (r68 != 0) goto L_0x0016;
    L_0x0011:
        if (r69 != 0) goto L_0x0016;
    L_0x0013:
        r69 = "";
    L_0x0016:
        r51 = 0;
        if (r85 == 0) goto L_0x0030;
    L_0x001a:
        r4 = "originalPath";
        r0 = r85;
        r4 = r0.containsKey(r4);
        if (r4 == 0) goto L_0x0030;
    L_0x0025:
        r4 = "originalPath";
        r0 = r85;
        r51 = r0.get(r4);
        r51 = (java.lang.String) r51;
    L_0x0030:
        r46 = 0;
        r48 = 0;
        r28 = 0;
        r63 = -1;
        r0 = r76;
        r0 = (int) r0;
        r42 = r0;
        r4 = 32;
        r8 = r76 >> r4;
        r0 = (int) r8;
        r35 = r0;
        r38 = 0;
        r16 = 0;
        if (r42 == 0) goto L_0x00ac;
    L_0x004a:
        r0 = r67;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r0 = r42;
        r57 = r4.getInputPeer(r0);
    L_0x0058:
        r58 = 0;
        if (r42 != 0) goto L_0x00af;
    L_0x005c:
        r0 = r67;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r6 = java.lang.Integer.valueOf(r35);
        r16 = r4.getEncryptedChat(r6);
        if (r16 != 0) goto L_0x00d3;
    L_0x006e:
        if (r82 == 0) goto L_0x0008;
    L_0x0070:
        r0 = r67;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r82;
        r6 = r0.messageOwner;
        r4.markMessageAsSendError(r6);
        r0 = r82;
        r4 = r0.messageOwner;
        r6 = 2;
        r4.send_state = r6;
        r0 = r67;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r6 = org.telegram.messenger.NotificationCenter.messageSendError;
        r8 = 1;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r11 = r82.getId();
        r11 = java.lang.Integer.valueOf(r11);
        r8[r9] = r11;
        r4.postNotificationName(r6, r8);
        r4 = r82.getId();
        r0 = r67;
        r0.processSentMessage(r4);
        goto L_0x0008;
    L_0x00ac:
        r57 = 0;
        goto L_0x0058;
    L_0x00af:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r4 == 0) goto L_0x00d3;
    L_0x00b5:
        r0 = r67;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r0 = r57;
        r6 = r0.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r26 = r4.getChat(r6);
        if (r26 == 0) goto L_0x025b;
    L_0x00cb:
        r0 = r26;
        r4 = r0.megagroup;
        if (r4 != 0) goto L_0x025b;
    L_0x00d1:
        r38 = 1;
    L_0x00d3:
        if (r82 == 0) goto L_0x0356;
    L_0x00d5:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x021b }
        r46 = r0;	 Catch:{ Exception -> 0x021b }
        r4 = r82.isForwarded();	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x025f;	 Catch:{ Exception -> 0x021b }
    L_0x00e1:
        r63 = 4;	 Catch:{ Exception -> 0x021b }
    L_0x00e3:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r8 = r0.random_id;	 Catch:{ Exception -> 0x021b }
        r14 = 0;	 Catch:{ Exception -> 0x021b }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x00f5;	 Catch:{ Exception -> 0x021b }
    L_0x00ed:
        r8 = r67.getNextRandomId();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.random_id = r8;	 Catch:{ Exception -> 0x021b }
    L_0x00f5:
        if (r85 == 0) goto L_0x012a;	 Catch:{ Exception -> 0x021b }
    L_0x00f7:
        r4 = "bot";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x012a;	 Catch:{ Exception -> 0x021b }
    L_0x0102:
        if (r16 == 0) goto L_0x074e;	 Catch:{ Exception -> 0x021b }
    L_0x0104:
        r4 = "bot_name";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x021b }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.via_bot_name = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.via_bot_name;	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x0120;	 Catch:{ Exception -> 0x021b }
    L_0x0119:
        r4 = "";	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.via_bot_name = r4;	 Catch:{ Exception -> 0x021b }
    L_0x0120:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.flags;	 Catch:{ Exception -> 0x021b }
        r4 = r4 | 2048;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.flags = r4;	 Catch:{ Exception -> 0x021b }
    L_0x012a:
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.params = r0;	 Catch:{ Exception -> 0x021b }
        if (r82 == 0) goto L_0x0138;	 Catch:{ Exception -> 0x021b }
    L_0x0132:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.resendAsIs;	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x018b;	 Catch:{ Exception -> 0x021b }
    L_0x0138:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r4 = r4.getCurrentTime();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.date = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r57;	 Catch:{ Exception -> 0x021b }
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x0784;	 Catch:{ Exception -> 0x021b }
    L_0x014e:
        if (r38 == 0) goto L_0x015f;	 Catch:{ Exception -> 0x021b }
    L_0x0150:
        r4 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.views = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.flags;	 Catch:{ Exception -> 0x021b }
        r4 = r4 | 1024;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.flags = r4;	 Catch:{ Exception -> 0x021b }
    L_0x015f:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r0 = r57;	 Catch:{ Exception -> 0x021b }
        r6 = r0.channel_id;	 Catch:{ Exception -> 0x021b }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x021b }
        r26 = r4.getChat(r6);	 Catch:{ Exception -> 0x021b }
        if (r26 == 0) goto L_0x018b;	 Catch:{ Exception -> 0x021b }
    L_0x0175:
        r0 = r26;	 Catch:{ Exception -> 0x021b }
        r4 = r0.megagroup;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x0767;	 Catch:{ Exception -> 0x021b }
    L_0x017b:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.flags;	 Catch:{ Exception -> 0x021b }
        r6 = -NUM; // 0xffffffff80000000 float:-0.0 double:NaN;	 Catch:{ Exception -> 0x021b }
        r4 = r4 | r6;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.flags = r4;	 Catch:{ Exception -> 0x021b }
        r4 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.unread = r4;	 Catch:{ Exception -> 0x021b }
    L_0x018b:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.flags;	 Catch:{ Exception -> 0x021b }
        r4 = r4 | 512;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.flags = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r76;	 Catch:{ Exception -> 0x021b }
        r2 = r46;	 Catch:{ Exception -> 0x021b }
        r2.dialog_id = r0;	 Catch:{ Exception -> 0x021b }
        if (r79 == 0) goto L_0x01c7;	 Catch:{ Exception -> 0x021b }
    L_0x019d:
        if (r16 == 0) goto L_0x078b;	 Catch:{ Exception -> 0x021b }
    L_0x019f:
        r0 = r79;	 Catch:{ Exception -> 0x021b }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x021b }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x021b }
        r14 = 0;	 Catch:{ Exception -> 0x021b }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x078b;	 Catch:{ Exception -> 0x021b }
    L_0x01ab:
        r0 = r79;	 Catch:{ Exception -> 0x021b }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x021b }
        r8 = r4.random_id;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.reply_to_random_id = r8;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.flags;	 Catch:{ Exception -> 0x021b }
        r4 = r4 | 8;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.flags = r4;	 Catch:{ Exception -> 0x021b }
    L_0x01bf:
        r4 = r79.getId();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x021b }
    L_0x01c7:
        if (r84 == 0) goto L_0x01db;	 Catch:{ Exception -> 0x021b }
    L_0x01c9:
        if (r16 != 0) goto L_0x01db;	 Catch:{ Exception -> 0x021b }
    L_0x01cb:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.flags;	 Catch:{ Exception -> 0x021b }
        r4 = r4 | 64;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.flags = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r84;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.reply_markup = r0;	 Catch:{ Exception -> 0x021b }
    L_0x01db:
        if (r42 == 0) goto L_0x0993;	 Catch:{ Exception -> 0x021b }
    L_0x01dd:
        r4 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r35;	 Catch:{ Exception -> 0x021b }
        if (r0 != r4) goto L_0x0955;	 Catch:{ Exception -> 0x021b }
    L_0x01e2:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentChatInfo;	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x0797;	 Catch:{ Exception -> 0x021b }
    L_0x01e8:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4.markMessageAsSendError(r0);	 Catch:{ Exception -> 0x021b }
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r6 = org.telegram.messenger.NotificationCenter.messageSendError;	 Catch:{ Exception -> 0x021b }
        r8 = 1;	 Catch:{ Exception -> 0x021b }
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x021b }
        r9 = 0;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r11 = r0.id;	 Catch:{ Exception -> 0x021b }
        r11 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x021b }
        r8[r9] = r11;	 Catch:{ Exception -> 0x021b }
        r4.postNotificationName(r6, r8);	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.id;	 Catch:{ Exception -> 0x021b }
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r0.processSentMessage(r4);	 Catch:{ Exception -> 0x021b }
        goto L_0x0008;
    L_0x021b:
        r31 = move-exception;
        r12 = r48;
    L_0x021e:
        org.telegram.messenger.FileLog.m8e(r31);
        r0 = r67;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r46;
        r4.markMessageAsSendError(r0);
        if (r12 == 0) goto L_0x0235;
    L_0x0230:
        r4 = r12.messageOwner;
        r6 = 2;
        r4.send_state = r6;
    L_0x0235:
        r0 = r67;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r6 = org.telegram.messenger.NotificationCenter.messageSendError;
        r8 = 1;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r46;
        r11 = r0.id;
        r11 = java.lang.Integer.valueOf(r11);
        r8[r9] = r11;
        r4.postNotificationName(r6, r8);
        r0 = r46;
        r4 = r0.id;
        r0 = r67;
        r0.processSentMessage(r4);
        goto L_0x0008;
    L_0x025b:
        r38 = 0;
        goto L_0x00d3;
    L_0x025f:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x0289;	 Catch:{ Exception -> 0x021b }
    L_0x0265:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x021b }
        r4 = r4.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x0282;	 Catch:{ Exception -> 0x021b }
    L_0x026f:
        r63 = 0;	 Catch:{ Exception -> 0x021b }
    L_0x0271:
        if (r85 == 0) goto L_0x00e3;	 Catch:{ Exception -> 0x021b }
    L_0x0273:
        r4 = "query_id";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x00e3;	 Catch:{ Exception -> 0x021b }
    L_0x027e:
        r63 = 9;	 Catch:{ Exception -> 0x021b }
        goto L_0x00e3;	 Catch:{ Exception -> 0x021b }
    L_0x0282:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0 = r0.message;	 Catch:{ Exception -> 0x021b }
        r68 = r0;	 Catch:{ Exception -> 0x021b }
        goto L_0x026f;	 Catch:{ Exception -> 0x021b }
    L_0x0289:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 4;	 Catch:{ Exception -> 0x021b }
        if (r4 != r6) goto L_0x0299;	 Catch:{ Exception -> 0x021b }
    L_0x0290:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0 = r0.media;	 Catch:{ Exception -> 0x021b }
        r70 = r0;	 Catch:{ Exception -> 0x021b }
        r63 = 1;	 Catch:{ Exception -> 0x021b }
        goto L_0x0271;	 Catch:{ Exception -> 0x021b }
    L_0x0299:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 1;	 Catch:{ Exception -> 0x021b }
        if (r4 != r6) goto L_0x02ae;	 Catch:{ Exception -> 0x021b }
    L_0x02a0:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.photo;	 Catch:{ Exception -> 0x021b }
        r0 = r4;	 Catch:{ Exception -> 0x021b }
        r0 = (org.telegram.tgnet.TLRPC.TL_photo) r0;	 Catch:{ Exception -> 0x021b }
        r71 = r0;	 Catch:{ Exception -> 0x021b }
        r63 = 2;	 Catch:{ Exception -> 0x021b }
        goto L_0x0271;	 Catch:{ Exception -> 0x021b }
    L_0x02ae:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 3;	 Catch:{ Exception -> 0x021b }
        if (r4 == r6) goto L_0x02be;	 Catch:{ Exception -> 0x021b }
    L_0x02b5:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 5;	 Catch:{ Exception -> 0x021b }
        if (r4 == r6) goto L_0x02be;	 Catch:{ Exception -> 0x021b }
    L_0x02bc:
        if (r72 == 0) goto L_0x02cc;	 Catch:{ Exception -> 0x021b }
    L_0x02be:
        r63 = 3;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.document;	 Catch:{ Exception -> 0x021b }
        r0 = r4;	 Catch:{ Exception -> 0x021b }
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x021b }
        r74 = r0;	 Catch:{ Exception -> 0x021b }
        goto L_0x0271;	 Catch:{ Exception -> 0x021b }
    L_0x02cc:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 12;	 Catch:{ Exception -> 0x021b }
        if (r4 != r6) goto L_0x0311;	 Catch:{ Exception -> 0x021b }
    L_0x02d4:
        r64 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2;	 Catch:{ Exception -> 0x021b }
        r64.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x1a82 }
        r4 = r0.media;	 Catch:{ Exception -> 0x1a82 }
        r4 = r4.phone_number;	 Catch:{ Exception -> 0x1a82 }
        r0 = r64;	 Catch:{ Exception -> 0x1a82 }
        r0.phone = r4;	 Catch:{ Exception -> 0x1a82 }
        r0 = r46;	 Catch:{ Exception -> 0x1a82 }
        r4 = r0.media;	 Catch:{ Exception -> 0x1a82 }
        r4 = r4.first_name;	 Catch:{ Exception -> 0x1a82 }
        r0 = r64;	 Catch:{ Exception -> 0x1a82 }
        r0.first_name = r4;	 Catch:{ Exception -> 0x1a82 }
        r0 = r46;	 Catch:{ Exception -> 0x1a82 }
        r4 = r0.media;	 Catch:{ Exception -> 0x1a82 }
        r4 = r4.last_name;	 Catch:{ Exception -> 0x1a82 }
        r0 = r64;	 Catch:{ Exception -> 0x1a82 }
        r0.last_name = r4;	 Catch:{ Exception -> 0x1a82 }
        r0 = r46;	 Catch:{ Exception -> 0x1a82 }
        r4 = r0.media;	 Catch:{ Exception -> 0x1a82 }
        r4 = r4.vcard;	 Catch:{ Exception -> 0x1a82 }
        r0 = r64;	 Catch:{ Exception -> 0x1a82 }
        r0.restriction_reason = r4;	 Catch:{ Exception -> 0x1a82 }
        r0 = r46;	 Catch:{ Exception -> 0x1a82 }
        r4 = r0.media;	 Catch:{ Exception -> 0x1a82 }
        r4 = r4.user_id;	 Catch:{ Exception -> 0x1a82 }
        r0 = r64;	 Catch:{ Exception -> 0x1a82 }
        r0.id = r4;	 Catch:{ Exception -> 0x1a82 }
        r63 = 6;
        r73 = r64;
        goto L_0x0271;
    L_0x0311:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 8;	 Catch:{ Exception -> 0x021b }
        if (r4 == r6) goto L_0x0331;	 Catch:{ Exception -> 0x021b }
    L_0x0319:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 9;	 Catch:{ Exception -> 0x021b }
        if (r4 == r6) goto L_0x0331;	 Catch:{ Exception -> 0x021b }
    L_0x0321:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 13;	 Catch:{ Exception -> 0x021b }
        if (r4 == r6) goto L_0x0331;	 Catch:{ Exception -> 0x021b }
    L_0x0329:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 14;	 Catch:{ Exception -> 0x021b }
        if (r4 != r6) goto L_0x0340;	 Catch:{ Exception -> 0x021b }
    L_0x0331:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.document;	 Catch:{ Exception -> 0x021b }
        r0 = r4;	 Catch:{ Exception -> 0x021b }
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x021b }
        r74 = r0;	 Catch:{ Exception -> 0x021b }
        r63 = 7;	 Catch:{ Exception -> 0x021b }
        goto L_0x0271;	 Catch:{ Exception -> 0x021b }
    L_0x0340:
        r0 = r82;	 Catch:{ Exception -> 0x021b }
        r4 = r0.type;	 Catch:{ Exception -> 0x021b }
        r6 = 2;	 Catch:{ Exception -> 0x021b }
        if (r4 != r6) goto L_0x0271;	 Catch:{ Exception -> 0x021b }
    L_0x0347:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.document;	 Catch:{ Exception -> 0x021b }
        r0 = r4;	 Catch:{ Exception -> 0x021b }
        r0 = (org.telegram.tgnet.TLRPC.TL_document) r0;	 Catch:{ Exception -> 0x021b }
        r74 = r0;	 Catch:{ Exception -> 0x021b }
        r63 = 8;	 Catch:{ Exception -> 0x021b }
        goto L_0x0271;	 Catch:{ Exception -> 0x021b }
    L_0x0356:
        if (r68 == 0) goto L_0x0423;	 Catch:{ Exception -> 0x021b }
    L_0x0358:
        if (r16 == 0) goto L_0x03ff;	 Catch:{ Exception -> 0x021b }
    L_0x035a:
        r47 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
    L_0x0361:
        if (r16 == 0) goto L_0x037e;	 Catch:{ Exception -> 0x021b }
    L_0x0363:
        r0 = r80;	 Catch:{ Exception -> 0x021b }
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPagePending;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x037e;	 Catch:{ Exception -> 0x021b }
    L_0x0369:
        r0 = r80;	 Catch:{ Exception -> 0x021b }
        r4 = r0.url;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x0408;	 Catch:{ Exception -> 0x021b }
    L_0x036f:
        r49 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending;	 Catch:{ Exception -> 0x021b }
        r49.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r80;	 Catch:{ Exception -> 0x021b }
        r4 = r0.url;	 Catch:{ Exception -> 0x021b }
        r0 = r49;	 Catch:{ Exception -> 0x021b }
        r0.url = r4;	 Catch:{ Exception -> 0x021b }
        r80 = r49;	 Catch:{ Exception -> 0x021b }
    L_0x037e:
        if (r80 != 0) goto L_0x040c;	 Catch:{ Exception -> 0x021b }
    L_0x0380:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;	 Catch:{ Exception -> 0x021b }
        r4.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.media = r4;	 Catch:{ Exception -> 0x021b }
    L_0x0389:
        if (r85 == 0) goto L_0x041f;	 Catch:{ Exception -> 0x021b }
    L_0x038b:
        r4 = "query_id";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x041f;	 Catch:{ Exception -> 0x021b }
    L_0x0396:
        r63 = 9;	 Catch:{ Exception -> 0x021b }
    L_0x0398:
        r0 = r68;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.message = r0;	 Catch:{ Exception -> 0x021b }
    L_0x039e:
        if (r83 == 0) goto L_0x03b6;	 Catch:{ Exception -> 0x021b }
    L_0x03a0:
        r4 = r83.isEmpty();	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x03b6;	 Catch:{ Exception -> 0x021b }
    L_0x03a6:
        r0 = r83;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.entities = r0;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.flags;	 Catch:{ Exception -> 0x021b }
        r4 = r4 | 128;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.flags = r4;	 Catch:{ Exception -> 0x021b }
    L_0x03b6:
        if (r69 == 0) goto L_0x0723;	 Catch:{ Exception -> 0x021b }
    L_0x03b8:
        r0 = r69;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.message = r0;	 Catch:{ Exception -> 0x021b }
    L_0x03be:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.attachPath;	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x03cb;	 Catch:{ Exception -> 0x021b }
    L_0x03c4:
        r4 = "";	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.attachPath = r4;	 Catch:{ Exception -> 0x021b }
    L_0x03cb:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r4 = r4.getNewMessageId();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.id = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.local_id = r4;	 Catch:{ Exception -> 0x021b }
        r4 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.out = r4;	 Catch:{ Exception -> 0x021b }
        if (r38 == 0) goto L_0x0732;	 Catch:{ Exception -> 0x021b }
    L_0x03e6:
        if (r57 == 0) goto L_0x0732;	 Catch:{ Exception -> 0x021b }
    L_0x03e8:
        r0 = r57;	 Catch:{ Exception -> 0x021b }
        r4 = r0.channel_id;	 Catch:{ Exception -> 0x021b }
        r4 = -r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.from_id = r4;	 Catch:{ Exception -> 0x021b }
    L_0x03f1:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r6 = 0;	 Catch:{ Exception -> 0x021b }
        r4.saveConfig(r6);	 Catch:{ Exception -> 0x021b }
        goto L_0x00e3;	 Catch:{ Exception -> 0x021b }
    L_0x03ff:
        r47 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
        goto L_0x0361;	 Catch:{ Exception -> 0x021b }
    L_0x0408:
        r80 = 0;	 Catch:{ Exception -> 0x021b }
        goto L_0x037e;	 Catch:{ Exception -> 0x021b }
    L_0x040c:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;	 Catch:{ Exception -> 0x021b }
        r4.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.media = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r80;	 Catch:{ Exception -> 0x021b }
        r4.webpage = r0;	 Catch:{ Exception -> 0x021b }
        goto L_0x0389;	 Catch:{ Exception -> 0x021b }
    L_0x041f:
        r63 = 0;	 Catch:{ Exception -> 0x021b }
        goto L_0x0398;	 Catch:{ Exception -> 0x021b }
    L_0x0423:
        if (r70 == 0) goto L_0x0451;	 Catch:{ Exception -> 0x021b }
    L_0x0425:
        if (r16 == 0) goto L_0x0445;	 Catch:{ Exception -> 0x021b }
    L_0x0427:
        r47 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
    L_0x042e:
        r0 = r70;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.media = r0;	 Catch:{ Exception -> 0x021b }
        if (r85 == 0) goto L_0x044d;	 Catch:{ Exception -> 0x021b }
    L_0x0436:
        r4 = "query_id";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x044d;	 Catch:{ Exception -> 0x021b }
    L_0x0441:
        r63 = 9;	 Catch:{ Exception -> 0x021b }
        goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x0445:
        r47 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
        goto L_0x042e;	 Catch:{ Exception -> 0x021b }
    L_0x044d:
        r63 = 1;	 Catch:{ Exception -> 0x021b }
        goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x0451:
        if (r71 == 0) goto L_0x04f7;	 Catch:{ Exception -> 0x021b }
    L_0x0453:
        if (r16 == 0) goto L_0x04c3;	 Catch:{ Exception -> 0x021b }
    L_0x0455:
        r47 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
    L_0x045c:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;	 Catch:{ Exception -> 0x021b }
        r4.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.media = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021b }
        r6 = r6 | 3;	 Catch:{ Exception -> 0x021b }
        r4.flags = r6;	 Catch:{ Exception -> 0x021b }
        if (r83 == 0) goto L_0x0477;	 Catch:{ Exception -> 0x021b }
    L_0x0471:
        r0 = r83;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.entities = r0;	 Catch:{ Exception -> 0x021b }
    L_0x0477:
        if (r86 == 0) goto L_0x0491;	 Catch:{ Exception -> 0x021b }
    L_0x0479:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r86;	 Catch:{ Exception -> 0x021b }
        r4.ttl_seconds = r0;	 Catch:{ Exception -> 0x021b }
        r0 = r86;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.ttl = r0;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021b }
        r6 = r6 | 4;	 Catch:{ Exception -> 0x021b }
        r4.flags = r6;	 Catch:{ Exception -> 0x021b }
    L_0x0491:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r71;	 Catch:{ Exception -> 0x021b }
        r4.photo = r0;	 Catch:{ Exception -> 0x021b }
        if (r85 == 0) goto L_0x04cb;	 Catch:{ Exception -> 0x021b }
    L_0x049b:
        r4 = "query_id";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x04cb;	 Catch:{ Exception -> 0x021b }
    L_0x04a6:
        r63 = 9;	 Catch:{ Exception -> 0x021b }
    L_0x04a8:
        if (r78 == 0) goto L_0x04ce;	 Catch:{ Exception -> 0x021b }
    L_0x04aa:
        r4 = r78.length();	 Catch:{ Exception -> 0x021b }
        if (r4 <= 0) goto L_0x04ce;	 Catch:{ Exception -> 0x021b }
    L_0x04b0:
        r4 = "http";	 Catch:{ Exception -> 0x021b }
        r0 = r78;	 Catch:{ Exception -> 0x021b }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x04ce;	 Catch:{ Exception -> 0x021b }
    L_0x04bb:
        r0 = r78;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.attachPath = r0;	 Catch:{ Exception -> 0x021b }
        goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x04c3:
        r47 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
        goto L_0x045c;	 Catch:{ Exception -> 0x021b }
    L_0x04cb:
        r63 = 2;	 Catch:{ Exception -> 0x021b }
        goto L_0x04a8;	 Catch:{ Exception -> 0x021b }
    L_0x04ce:
        r0 = r71;	 Catch:{ Exception -> 0x021b }
        r4 = r0.sizes;	 Catch:{ Exception -> 0x021b }
        r0 = r71;	 Catch:{ Exception -> 0x021b }
        r6 = r0.sizes;	 Catch:{ Exception -> 0x021b }
        r6 = r6.size();	 Catch:{ Exception -> 0x021b }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x021b }
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x021b }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x021b }
        r0 = r4.location;	 Catch:{ Exception -> 0x021b }
        r41 = r0;	 Catch:{ Exception -> 0x021b }
        r4 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r41;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r4);	 Catch:{ Exception -> 0x021b }
        r4 = r4.toString();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.attachPath = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x04f7:
        if (r75 == 0) goto L_0x0522;	 Catch:{ Exception -> 0x021b }
    L_0x04f9:
        r47 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame;	 Catch:{ Exception -> 0x1a89 }
        r4.<init>();	 Catch:{ Exception -> 0x1a89 }
        r0 = r47;	 Catch:{ Exception -> 0x1a89 }
        r0.media = r4;	 Catch:{ Exception -> 0x1a89 }
        r0 = r47;	 Catch:{ Exception -> 0x1a89 }
        r4 = r0.media;	 Catch:{ Exception -> 0x1a89 }
        r0 = r75;	 Catch:{ Exception -> 0x1a89 }
        r4.game = r0;	 Catch:{ Exception -> 0x1a89 }
        if (r85 == 0) goto L_0x1aac;	 Catch:{ Exception -> 0x1a89 }
    L_0x0511:
        r4 = "query_id";	 Catch:{ Exception -> 0x1a89 }
        r0 = r85;	 Catch:{ Exception -> 0x1a89 }
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x1a89 }
        if (r4 == 0) goto L_0x1aac;
    L_0x051c:
        r63 = 9;
        r46 = r47;
        goto L_0x039e;
    L_0x0522:
        if (r73 == 0) goto L_0x05cd;
    L_0x0524:
        if (r16 == 0) goto L_0x05b6;
    L_0x0526:
        r47 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
    L_0x052d:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact;	 Catch:{ Exception -> 0x021b }
        r4.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.media = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r73;	 Catch:{ Exception -> 0x021b }
        r6 = r0.phone;	 Catch:{ Exception -> 0x021b }
        r4.phone_number = r6;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r73;	 Catch:{ Exception -> 0x021b }
        r6 = r0.first_name;	 Catch:{ Exception -> 0x021b }
        r4.first_name = r6;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r73;	 Catch:{ Exception -> 0x021b }
        r6 = r0.last_name;	 Catch:{ Exception -> 0x021b }
        r4.last_name = r6;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r73;	 Catch:{ Exception -> 0x021b }
        r6 = r0.id;	 Catch:{ Exception -> 0x021b }
        r4.user_id = r6;	 Catch:{ Exception -> 0x021b }
        r0 = r73;	 Catch:{ Exception -> 0x021b }
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x05bf;	 Catch:{ Exception -> 0x021b }
    L_0x0564:
        r0 = r73;	 Catch:{ Exception -> 0x021b }
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x021b }
        r6 = "BEGIN:VCARD";	 Catch:{ Exception -> 0x021b }
        r4 = r4.startsWith(r6);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x05bf;	 Catch:{ Exception -> 0x021b }
    L_0x0571:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r73;	 Catch:{ Exception -> 0x021b }
        r6 = r0.restriction_reason;	 Catch:{ Exception -> 0x021b }
        r4.vcard = r6;	 Catch:{ Exception -> 0x021b }
    L_0x057b:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.first_name;	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x0590;	 Catch:{ Exception -> 0x021b }
    L_0x0583:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r6 = "";	 Catch:{ Exception -> 0x021b }
        r4.first_name = r6;	 Catch:{ Exception -> 0x021b }
        r0 = r73;	 Catch:{ Exception -> 0x021b }
        r0.first_name = r6;	 Catch:{ Exception -> 0x021b }
    L_0x0590:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.last_name;	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x05a5;	 Catch:{ Exception -> 0x021b }
    L_0x0598:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r6 = "";	 Catch:{ Exception -> 0x021b }
        r4.last_name = r6;	 Catch:{ Exception -> 0x021b }
        r0 = r73;	 Catch:{ Exception -> 0x021b }
        r0.last_name = r6;	 Catch:{ Exception -> 0x021b }
    L_0x05a5:
        if (r85 == 0) goto L_0x05c9;	 Catch:{ Exception -> 0x021b }
    L_0x05a7:
        r4 = "query_id";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x05c9;	 Catch:{ Exception -> 0x021b }
    L_0x05b2:
        r63 = 9;	 Catch:{ Exception -> 0x021b }
        goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x05b6:
        r47 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
        goto L_0x052d;	 Catch:{ Exception -> 0x021b }
    L_0x05bf:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r6 = "";	 Catch:{ Exception -> 0x021b }
        r4.vcard = r6;	 Catch:{ Exception -> 0x021b }
        goto L_0x057b;	 Catch:{ Exception -> 0x021b }
    L_0x05c9:
        r63 = 6;	 Catch:{ Exception -> 0x021b }
        goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x05cd:
        if (r74 == 0) goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x05cf:
        if (r16 == 0) goto L_0x06c5;	 Catch:{ Exception -> 0x021b }
    L_0x05d1:
        r47 = new org.telegram.tgnet.TLRPC$TL_message_secret;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
    L_0x05d8:
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x021b }
        r4.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.media = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021b }
        r6 = r6 | 3;	 Catch:{ Exception -> 0x021b }
        r4.flags = r6;	 Catch:{ Exception -> 0x021b }
        if (r86 == 0) goto L_0x0605;	 Catch:{ Exception -> 0x021b }
    L_0x05ed:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r86;	 Catch:{ Exception -> 0x021b }
        r4.ttl_seconds = r0;	 Catch:{ Exception -> 0x021b }
        r0 = r86;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.ttl = r0;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021b }
        r6 = r6 | 4;	 Catch:{ Exception -> 0x021b }
        r4.flags = r6;	 Catch:{ Exception -> 0x021b }
    L_0x0605:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r74;	 Catch:{ Exception -> 0x021b }
        r4.document = r0;	 Catch:{ Exception -> 0x021b }
        if (r85 == 0) goto L_0x06ce;	 Catch:{ Exception -> 0x021b }
    L_0x060f:
        r4 = "query_id";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r4 = r0.containsKey(r4);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x06ce;	 Catch:{ Exception -> 0x021b }
    L_0x061a:
        r63 = 9;	 Catch:{ Exception -> 0x021b }
    L_0x061c:
        if (r72 == 0) goto L_0x0635;	 Catch:{ Exception -> 0x021b }
    L_0x061e:
        r65 = r72.getString();	 Catch:{ Exception -> 0x021b }
        if (r85 != 0) goto L_0x062b;	 Catch:{ Exception -> 0x021b }
    L_0x0624:
        r52 = new java.util.HashMap;	 Catch:{ Exception -> 0x021b }
        r52.<init>();	 Catch:{ Exception -> 0x021b }
        r85 = r52;	 Catch:{ Exception -> 0x021b }
    L_0x062b:
        r4 = "ve";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r1 = r65;	 Catch:{ Exception -> 0x021b }
        r0.put(r4, r1);	 Catch:{ Exception -> 0x021b }
    L_0x0635:
        if (r16 == 0) goto L_0x06ee;	 Catch:{ Exception -> 0x021b }
    L_0x0637:
        r0 = r74;	 Catch:{ Exception -> 0x021b }
        r4 = r0.dc_id;	 Catch:{ Exception -> 0x021b }
        if (r4 <= 0) goto L_0x06ee;	 Catch:{ Exception -> 0x021b }
    L_0x063d:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r74);	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x06ee;	 Catch:{ Exception -> 0x021b }
    L_0x0643:
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r74);	 Catch:{ Exception -> 0x021b }
        r4 = r4.toString();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.attachPath = r4;	 Catch:{ Exception -> 0x021b }
    L_0x064f:
        if (r16 == 0) goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x0651:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r74);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x0657:
        r20 = 0;	 Catch:{ Exception -> 0x021b }
    L_0x0659:
        r0 = r74;	 Catch:{ Exception -> 0x021b }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x021b }
        r4 = r4.size();	 Catch:{ Exception -> 0x021b }
        r0 = r20;	 Catch:{ Exception -> 0x021b }
        if (r0 >= r4) goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x0665:
        r0 = r74;	 Catch:{ Exception -> 0x021b }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x021b }
        r0 = r20;	 Catch:{ Exception -> 0x021b }
        r23 = r4.get(r0);	 Catch:{ Exception -> 0x021b }
        r23 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r23;	 Catch:{ Exception -> 0x021b }
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x071f;	 Catch:{ Exception -> 0x021b }
    L_0x0677:
        r0 = r74;	 Catch:{ Exception -> 0x021b }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x021b }
        r0 = r20;	 Catch:{ Exception -> 0x021b }
        r4.remove(r0);	 Catch:{ Exception -> 0x021b }
        r24 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55;	 Catch:{ Exception -> 0x021b }
        r24.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r74;	 Catch:{ Exception -> 0x021b }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x021b }
        r0 = r24;	 Catch:{ Exception -> 0x021b }
        r4.add(r0);	 Catch:{ Exception -> 0x021b }
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r4 = r0.alt;	 Catch:{ Exception -> 0x021b }
        r0 = r24;	 Catch:{ Exception -> 0x021b }
        r0.alt = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x0714;	 Catch:{ Exception -> 0x021b }
    L_0x069c:
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x021b }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x06f6;	 Catch:{ Exception -> 0x021b }
    L_0x06a4:
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x021b }
        r0 = r4.short_name;	 Catch:{ Exception -> 0x021b }
        r45 = r0;	 Catch:{ Exception -> 0x021b }
    L_0x06ac:
        r4 = android.text.TextUtils.isEmpty(r45);	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x0709;	 Catch:{ Exception -> 0x021b }
    L_0x06b2:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;	 Catch:{ Exception -> 0x021b }
        r4.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r24;	 Catch:{ Exception -> 0x021b }
        r0.stickerset = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r24;	 Catch:{ Exception -> 0x021b }
        r4 = r0.stickerset;	 Catch:{ Exception -> 0x021b }
        r0 = r45;	 Catch:{ Exception -> 0x021b }
        r4.short_name = r0;	 Catch:{ Exception -> 0x021b }
        goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x06c5:
        r47 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x021b }
        r47.<init>();	 Catch:{ Exception -> 0x021b }
        r46 = r47;	 Catch:{ Exception -> 0x021b }
        goto L_0x05d8;	 Catch:{ Exception -> 0x021b }
    L_0x06ce:
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r74);	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x06dc;	 Catch:{ Exception -> 0x021b }
    L_0x06d4:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r74);	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x06dc;	 Catch:{ Exception -> 0x021b }
    L_0x06da:
        if (r72 == 0) goto L_0x06e0;	 Catch:{ Exception -> 0x021b }
    L_0x06dc:
        r63 = 3;	 Catch:{ Exception -> 0x021b }
        goto L_0x061c;	 Catch:{ Exception -> 0x021b }
    L_0x06e0:
        r4 = org.telegram.messenger.MessageObject.isVoiceDocument(r74);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x06ea;	 Catch:{ Exception -> 0x021b }
    L_0x06e6:
        r63 = 8;	 Catch:{ Exception -> 0x021b }
        goto L_0x061c;	 Catch:{ Exception -> 0x021b }
    L_0x06ea:
        r63 = 7;	 Catch:{ Exception -> 0x021b }
        goto L_0x061c;	 Catch:{ Exception -> 0x021b }
    L_0x06ee:
        r0 = r78;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.attachPath = r0;	 Catch:{ Exception -> 0x021b }
        goto L_0x064f;	 Catch:{ Exception -> 0x021b }
    L_0x06f6:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r6 = r0.stickerset;	 Catch:{ Exception -> 0x021b }
        r8 = r6.id;	 Catch:{ Exception -> 0x021b }
        r45 = r4.getStickerSetName(r8);	 Catch:{ Exception -> 0x021b }
        goto L_0x06ac;	 Catch:{ Exception -> 0x021b }
    L_0x0709:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x021b }
        r4.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r24;	 Catch:{ Exception -> 0x021b }
        r0.stickerset = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x0714:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;	 Catch:{ Exception -> 0x021b }
        r4.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r24;	 Catch:{ Exception -> 0x021b }
        r0.stickerset = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x039e;	 Catch:{ Exception -> 0x021b }
    L_0x071f:
        r20 = r20 + 1;	 Catch:{ Exception -> 0x021b }
        goto L_0x0659;	 Catch:{ Exception -> 0x021b }
    L_0x0723:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.message;	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x03be;	 Catch:{ Exception -> 0x021b }
    L_0x0729:
        r4 = "";	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.message = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x03be;	 Catch:{ Exception -> 0x021b }
    L_0x0732:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r4 = r4.getClientUserId();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.from_id = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.flags;	 Catch:{ Exception -> 0x021b }
        r4 = r4 | 256;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.flags = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x03f1;	 Catch:{ Exception -> 0x021b }
    L_0x074e:
        r4 = "bot";	 Catch:{ Exception -> 0x021b }
        r0 = r85;	 Catch:{ Exception -> 0x021b }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x021b }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.Utilities.parseInt(r4);	 Catch:{ Exception -> 0x021b }
        r4 = r4.intValue();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.via_bot_id = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x0120;	 Catch:{ Exception -> 0x021b }
    L_0x0767:
        r4 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.post = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r26;	 Catch:{ Exception -> 0x021b }
        r4 = r0.signatures;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x018b;	 Catch:{ Exception -> 0x021b }
    L_0x0772:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r4 = r4.getClientUserId();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.from_id = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x018b;	 Catch:{ Exception -> 0x021b }
    L_0x0784:
        r4 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.unread = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x018b;	 Catch:{ Exception -> 0x021b }
    L_0x078b:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.flags;	 Catch:{ Exception -> 0x021b }
        r4 = r4 | 8;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.flags = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x01bf;	 Catch:{ Exception -> 0x021b }
    L_0x0797:
        r59 = new java.util.ArrayList;	 Catch:{ Exception -> 0x021b }
        r59.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r67;	 Catch:{ Exception -> 0x07e0 }
        r4 = r0.currentChatInfo;	 Catch:{ Exception -> 0x07e0 }
        r4 = r4.participants;	 Catch:{ Exception -> 0x07e0 }
        r4 = r4.participants;	 Catch:{ Exception -> 0x07e0 }
        r4 = r4.iterator();	 Catch:{ Exception -> 0x07e0 }
    L_0x07a8:
        r6 = r4.hasNext();	 Catch:{ Exception -> 0x07e0 }
        if (r6 == 0) goto L_0x07e7;	 Catch:{ Exception -> 0x07e0 }
    L_0x07ae:
        r53 = r4.next();	 Catch:{ Exception -> 0x07e0 }
        r53 = (org.telegram.tgnet.TLRPC.ChatParticipant) r53;	 Catch:{ Exception -> 0x07e0 }
        r0 = r67;	 Catch:{ Exception -> 0x07e0 }
        r6 = r0.currentAccount;	 Catch:{ Exception -> 0x07e0 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x07e0 }
        r0 = r53;	 Catch:{ Exception -> 0x07e0 }
        r8 = r0.user_id;	 Catch:{ Exception -> 0x07e0 }
        r8 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x07e0 }
        r60 = r6.getUser(r8);	 Catch:{ Exception -> 0x07e0 }
        r0 = r67;	 Catch:{ Exception -> 0x07e0 }
        r6 = r0.currentAccount;	 Catch:{ Exception -> 0x07e0 }
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);	 Catch:{ Exception -> 0x07e0 }
        r0 = r60;	 Catch:{ Exception -> 0x07e0 }
        r54 = r6.getInputUser(r0);	 Catch:{ Exception -> 0x07e0 }
        if (r54 == 0) goto L_0x07a8;	 Catch:{ Exception -> 0x07e0 }
    L_0x07d8:
        r0 = r59;	 Catch:{ Exception -> 0x07e0 }
        r1 = r54;	 Catch:{ Exception -> 0x07e0 }
        r0.add(r1);	 Catch:{ Exception -> 0x07e0 }
        goto L_0x07a8;	 Catch:{ Exception -> 0x07e0 }
    L_0x07e0:
        r31 = move-exception;	 Catch:{ Exception -> 0x07e0 }
        r58 = r59;	 Catch:{ Exception -> 0x07e0 }
        r12 = r48;	 Catch:{ Exception -> 0x07e0 }
        goto L_0x021e;	 Catch:{ Exception -> 0x07e0 }
    L_0x07e7:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;	 Catch:{ Exception -> 0x07e0 }
        r4.<init>();	 Catch:{ Exception -> 0x07e0 }
        r0 = r46;	 Catch:{ Exception -> 0x07e0 }
        r0.to_id = r4;	 Catch:{ Exception -> 0x07e0 }
        r0 = r46;	 Catch:{ Exception -> 0x07e0 }
        r4 = r0.to_id;	 Catch:{ Exception -> 0x07e0 }
        r0 = r42;	 Catch:{ Exception -> 0x07e0 }
        r4.chat_id = r0;	 Catch:{ Exception -> 0x07e0 }
        r58 = r59;
    L_0x07fa:
        r4 = 1;
        r0 = r35;
        if (r0 == r4) goto L_0x0810;
    L_0x07ff:
        r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r46);	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x080b;	 Catch:{ Exception -> 0x021b }
    L_0x0805:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r46);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x0810;	 Catch:{ Exception -> 0x021b }
    L_0x080b:
        r4 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.media_unread = r4;	 Catch:{ Exception -> 0x021b }
    L_0x0810:
        r4 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.send_state = r4;	 Catch:{ Exception -> 0x021b }
        r12 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x021b }
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r6 = 1;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r12.<init>(r4, r0, r6);	 Catch:{ Exception -> 0x021b }
        r0 = r79;	 Catch:{ Exception -> 0x0dfd }
        r12.replyMessageObject = r0;	 Catch:{ Exception -> 0x0dfd }
        r4 = r12.isForwarded();	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x0844;	 Catch:{ Exception -> 0x0dfd }
    L_0x082b:
        r4 = r12.type;	 Catch:{ Exception -> 0x0dfd }
        r6 = 3;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == r6) goto L_0x0837;	 Catch:{ Exception -> 0x0dfd }
    L_0x0830:
        if (r72 != 0) goto L_0x0837;	 Catch:{ Exception -> 0x0dfd }
    L_0x0832:
        r4 = r12.type;	 Catch:{ Exception -> 0x0dfd }
        r6 = 2;	 Catch:{ Exception -> 0x0dfd }
        if (r4 != r6) goto L_0x0844;	 Catch:{ Exception -> 0x0dfd }
    L_0x0837:
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.attachPath;	 Catch:{ Exception -> 0x0dfd }
        r4 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x0844;	 Catch:{ Exception -> 0x0dfd }
    L_0x0841:
        r4 = 1;	 Catch:{ Exception -> 0x0dfd }
        r12.attachPathExists = r4;	 Catch:{ Exception -> 0x0dfd }
    L_0x0844:
        r4 = r12.videoEditedInfo;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x084e;	 Catch:{ Exception -> 0x0dfd }
    L_0x0848:
        if (r72 != 0) goto L_0x084e;	 Catch:{ Exception -> 0x0dfd }
    L_0x084a:
        r0 = r12.videoEditedInfo;	 Catch:{ Exception -> 0x0dfd }
        r72 = r0;	 Catch:{ Exception -> 0x0dfd }
    L_0x084e:
        r32 = 0;	 Catch:{ Exception -> 0x0dfd }
        r39 = 0;	 Catch:{ Exception -> 0x0dfd }
        if (r85 == 0) goto L_0x0887;	 Catch:{ Exception -> 0x0dfd }
    L_0x0854:
        r4 = "groupId";	 Catch:{ Exception -> 0x0dfd }
        r0 = r85;	 Catch:{ Exception -> 0x0dfd }
        r34 = r0.get(r4);	 Catch:{ Exception -> 0x0dfd }
        r34 = (java.lang.String) r34;	 Catch:{ Exception -> 0x0dfd }
        if (r34 == 0) goto L_0x087a;	 Catch:{ Exception -> 0x0dfd }
    L_0x0861:
        r4 = org.telegram.messenger.Utilities.parseLong(r34);	 Catch:{ Exception -> 0x0dfd }
        r32 = r4.longValue();	 Catch:{ Exception -> 0x0dfd }
        r0 = r32;	 Catch:{ Exception -> 0x0dfd }
        r2 = r46;	 Catch:{ Exception -> 0x0dfd }
        r2.grouped_id = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0dfd }
        r6 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4 | r6;	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r0.flags = r4;	 Catch:{ Exception -> 0x0dfd }
    L_0x087a:
        r4 = "final";	 Catch:{ Exception -> 0x0dfd }
        r0 = r85;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x0a9b;	 Catch:{ Exception -> 0x0dfd }
    L_0x0885:
        r39 = 1;	 Catch:{ Exception -> 0x0dfd }
    L_0x0887:
        r8 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x0a9f;	 Catch:{ Exception -> 0x0dfd }
    L_0x088d:
        r50 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0dfd }
        r50.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r50;	 Catch:{ Exception -> 0x0dfd }
        r0.add(r12);	 Catch:{ Exception -> 0x0dfd }
        r5 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0dfd }
        r5.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r5.add(r0);	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0dfd }
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);	 Catch:{ Exception -> 0x0dfd }
        r6 = 0;	 Catch:{ Exception -> 0x0dfd }
        r7 = 1;	 Catch:{ Exception -> 0x0dfd }
        r8 = 0;	 Catch:{ Exception -> 0x0dfd }
        r9 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4.putMessages(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0dfd }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x0dfd }
        r0 = r76;	 Catch:{ Exception -> 0x0dfd }
        r2 = r50;	 Catch:{ Exception -> 0x0dfd }
        r4.updateInterfaceWithMessages(r0, r2);	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0dfd }
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);	 Catch:{ Exception -> 0x0dfd }
        r6 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;	 Catch:{ Exception -> 0x0dfd }
        r8 = 0;	 Catch:{ Exception -> 0x0dfd }
        r8 = new java.lang.Object[r8];	 Catch:{ Exception -> 0x0dfd }
        r4.postNotificationName(r6, r8);	 Catch:{ Exception -> 0x0dfd }
        r29 = r28;
    L_0x08d1:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x091f;	 Catch:{ Exception -> 0x0c82 }
    L_0x08d5:
        if (r57 == 0) goto L_0x091f;	 Catch:{ Exception -> 0x0c82 }
    L_0x08d7:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r6 = "send message user_id = ";	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0c82 }
        r0 = r57;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.user_id;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0c82 }
        r6 = " chat_id = ";	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0c82 }
        r0 = r57;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.chat_id;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0c82 }
        r6 = " channel_id = ";	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0c82 }
        r0 = r57;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.channel_id;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0c82 }
        r6 = " access_hash = ";	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0c82 }
        r0 = r57;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.append(r8);	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0c82 }
        org.telegram.messenger.FileLog.m5d(r4);	 Catch:{ Exception -> 0x0c82 }
    L_0x091f:
        if (r63 == 0) goto L_0x092b;	 Catch:{ Exception -> 0x0c82 }
    L_0x0921:
        r4 = 9;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x0c87;	 Catch:{ Exception -> 0x0c82 }
    L_0x0927:
        if (r68 == 0) goto L_0x0c87;	 Catch:{ Exception -> 0x0c82 }
    L_0x0929:
        if (r16 == 0) goto L_0x0c87;	 Catch:{ Exception -> 0x0c82 }
    L_0x092b:
        if (r16 != 0) goto L_0x0bc8;	 Catch:{ Exception -> 0x0c82 }
    L_0x092d:
        if (r58 == 0) goto L_0x0b3e;	 Catch:{ Exception -> 0x0c82 }
    L_0x092f:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x0c82 }
        r7.<init>();	 Catch:{ Exception -> 0x0c82 }
        r55 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0c82 }
        r55.<init>();	 Catch:{ Exception -> 0x0c82 }
        r20 = 0;	 Catch:{ Exception -> 0x0c82 }
    L_0x093b:
        r4 = r58.size();	 Catch:{ Exception -> 0x0c82 }
        r0 = r20;	 Catch:{ Exception -> 0x0c82 }
        if (r0 >= r4) goto L_0x0b21;	 Catch:{ Exception -> 0x0c82 }
    L_0x0943:
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0c82 }
        r8 = r4.nextLong();	 Catch:{ Exception -> 0x0c82 }
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0c82 }
        r0 = r55;	 Catch:{ Exception -> 0x0c82 }
        r0.add(r4);	 Catch:{ Exception -> 0x0c82 }
        r20 = r20 + 1;
        goto L_0x093b;
    L_0x0955:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r0 = r42;	 Catch:{ Exception -> 0x021b }
        r4 = r4.getPeer(r0);	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.to_id = r4;	 Catch:{ Exception -> 0x021b }
        if (r42 <= 0) goto L_0x07fa;	 Catch:{ Exception -> 0x021b }
    L_0x0969:
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x021b }
        r6 = java.lang.Integer.valueOf(r42);	 Catch:{ Exception -> 0x021b }
        r60 = r4.getUser(r6);	 Catch:{ Exception -> 0x021b }
        if (r60 != 0) goto L_0x0986;	 Catch:{ Exception -> 0x021b }
    L_0x097b:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.id;	 Catch:{ Exception -> 0x021b }
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r0.processSentMessage(r4);	 Catch:{ Exception -> 0x021b }
        goto L_0x0008;	 Catch:{ Exception -> 0x021b }
    L_0x0986:
        r0 = r60;	 Catch:{ Exception -> 0x021b }
        r4 = r0.bot;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x07fa;	 Catch:{ Exception -> 0x021b }
    L_0x098c:
        r4 = 0;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.unread = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x07fa;	 Catch:{ Exception -> 0x021b }
    L_0x0993:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Exception -> 0x021b }
        r4.<init>();	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.to_id = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r16;	 Catch:{ Exception -> 0x021b }
        r4 = r0.participant_id;	 Catch:{ Exception -> 0x021b }
        r0 = r67;	 Catch:{ Exception -> 0x021b }
        r6 = r0.currentAccount;	 Catch:{ Exception -> 0x021b }
        r6 = org.telegram.messenger.UserConfig.getInstance(r6);	 Catch:{ Exception -> 0x021b }
        r6 = r6.getClientUserId();	 Catch:{ Exception -> 0x021b }
        if (r4 != r6) goto L_0x0a14;	 Catch:{ Exception -> 0x021b }
    L_0x09ae:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.to_id;	 Catch:{ Exception -> 0x021b }
        r0 = r16;	 Catch:{ Exception -> 0x021b }
        r6 = r0.admin_id;	 Catch:{ Exception -> 0x021b }
        r4.user_id = r6;	 Catch:{ Exception -> 0x021b }
    L_0x09b8:
        if (r86 == 0) goto L_0x0a1f;	 Catch:{ Exception -> 0x021b }
    L_0x09ba:
        r0 = r86;	 Catch:{ Exception -> 0x021b }
        r1 = r46;	 Catch:{ Exception -> 0x021b }
        r1.ttl = r0;	 Catch:{ Exception -> 0x021b }
    L_0x09c0:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x07fa;	 Catch:{ Exception -> 0x021b }
    L_0x09c6:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.document;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x07fa;	 Catch:{ Exception -> 0x021b }
    L_0x09ce:
        r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r46);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x0a4c;	 Catch:{ Exception -> 0x021b }
    L_0x09d4:
        r30 = 0;	 Catch:{ Exception -> 0x021b }
        r20 = 0;	 Catch:{ Exception -> 0x021b }
    L_0x09d8:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.document;	 Catch:{ Exception -> 0x021b }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x021b }
        r4 = r4.size();	 Catch:{ Exception -> 0x021b }
        r0 = r20;	 Catch:{ Exception -> 0x021b }
        if (r0 >= r4) goto L_0x0a04;	 Catch:{ Exception -> 0x021b }
    L_0x09e8:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.document;	 Catch:{ Exception -> 0x021b }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x021b }
        r0 = r20;	 Catch:{ Exception -> 0x021b }
        r23 = r4.get(r0);	 Catch:{ Exception -> 0x021b }
        r23 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r23;	 Catch:{ Exception -> 0x021b }
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x0a49;	 Catch:{ Exception -> 0x021b }
    L_0x09fe:
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r0 = r0.duration;	 Catch:{ Exception -> 0x021b }
        r30 = r0;	 Catch:{ Exception -> 0x021b }
    L_0x0a04:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021b }
        r6 = r30 + 1;	 Catch:{ Exception -> 0x021b }
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.ttl = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x07fa;	 Catch:{ Exception -> 0x021b }
    L_0x0a14:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.to_id;	 Catch:{ Exception -> 0x021b }
        r0 = r16;	 Catch:{ Exception -> 0x021b }
        r6 = r0.participant_id;	 Catch:{ Exception -> 0x021b }
        r4.user_id = r6;	 Catch:{ Exception -> 0x021b }
        goto L_0x09b8;	 Catch:{ Exception -> 0x021b }
    L_0x0a1f:
        r0 = r16;	 Catch:{ Exception -> 0x021b }
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.ttl = r4;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x09c0;	 Catch:{ Exception -> 0x021b }
    L_0x0a2d:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x09c0;	 Catch:{ Exception -> 0x021b }
    L_0x0a33:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r6 = r0.ttl;	 Catch:{ Exception -> 0x021b }
        r4.ttl_seconds = r6;	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r6 = r4.flags;	 Catch:{ Exception -> 0x021b }
        r6 = r6 | 4;	 Catch:{ Exception -> 0x021b }
        r4.flags = r6;	 Catch:{ Exception -> 0x021b }
        goto L_0x09c0;	 Catch:{ Exception -> 0x021b }
    L_0x0a49:
        r20 = r20 + 1;	 Catch:{ Exception -> 0x021b }
        goto L_0x09d8;	 Catch:{ Exception -> 0x021b }
    L_0x0a4c:
        r4 = org.telegram.messenger.MessageObject.isVideoMessage(r46);	 Catch:{ Exception -> 0x021b }
        if (r4 != 0) goto L_0x0a58;	 Catch:{ Exception -> 0x021b }
    L_0x0a52:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r46);	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x07fa;	 Catch:{ Exception -> 0x021b }
    L_0x0a58:
        r30 = 0;	 Catch:{ Exception -> 0x021b }
        r20 = 0;	 Catch:{ Exception -> 0x021b }
    L_0x0a5c:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.document;	 Catch:{ Exception -> 0x021b }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x021b }
        r4 = r4.size();	 Catch:{ Exception -> 0x021b }
        r0 = r20;	 Catch:{ Exception -> 0x021b }
        if (r0 >= r4) goto L_0x0a88;	 Catch:{ Exception -> 0x021b }
    L_0x0a6c:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.media;	 Catch:{ Exception -> 0x021b }
        r4 = r4.document;	 Catch:{ Exception -> 0x021b }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x021b }
        r0 = r20;	 Catch:{ Exception -> 0x021b }
        r23 = r4.get(r0);	 Catch:{ Exception -> 0x021b }
        r23 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r23;	 Catch:{ Exception -> 0x021b }
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x021b }
        if (r4 == 0) goto L_0x0a98;	 Catch:{ Exception -> 0x021b }
    L_0x0a82:
        r0 = r23;	 Catch:{ Exception -> 0x021b }
        r0 = r0.duration;	 Catch:{ Exception -> 0x021b }
        r30 = r0;	 Catch:{ Exception -> 0x021b }
    L_0x0a88:
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r4 = r0.ttl;	 Catch:{ Exception -> 0x021b }
        r6 = r30 + 1;	 Catch:{ Exception -> 0x021b }
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x021b }
        r0 = r46;	 Catch:{ Exception -> 0x021b }
        r0.ttl = r4;	 Catch:{ Exception -> 0x021b }
        goto L_0x07fa;
    L_0x0a98:
        r20 = r20 + 1;
        goto L_0x0a5c;
    L_0x0a9b:
        r39 = 0;
        goto L_0x0887;
    L_0x0a9f:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0dfd }
        r4.<init>();	 Catch:{ Exception -> 0x0dfd }
        r6 = "group_";	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.append(r6);	 Catch:{ Exception -> 0x0dfd }
        r0 = r32;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.append(r0);	 Catch:{ Exception -> 0x0dfd }
        r40 = r4.toString();	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.delayedMessages;	 Catch:{ Exception -> 0x0dfd }
        r0 = r40;	 Catch:{ Exception -> 0x0dfd }
        r22 = r4.get(r0);	 Catch:{ Exception -> 0x0dfd }
        r22 = (java.util.ArrayList) r22;	 Catch:{ Exception -> 0x0dfd }
        if (r22 == 0) goto L_0x1aa8;	 Catch:{ Exception -> 0x0dfd }
    L_0x0ac3:
        r4 = 0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r22;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0dfd }
        r0 = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r0;	 Catch:{ Exception -> 0x0dfd }
        r28 = r0;	 Catch:{ Exception -> 0x0dfd }
        r29 = r28;
    L_0x0ad1:
        if (r29 != 0) goto L_0x1aa4;
    L_0x0ad3:
        r28 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c82 }
        r0 = r28;	 Catch:{ Exception -> 0x0c82 }
        r1 = r67;	 Catch:{ Exception -> 0x0c82 }
        r2 = r76;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0c82 }
        r4 = 4;
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.type = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r32;	 Catch:{ Exception -> 0x0dfd }
        r2 = r28;	 Catch:{ Exception -> 0x0dfd }
        r2.groupId = r0;	 Catch:{ Exception -> 0x0dfd }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0dfd }
        r4.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.messageObjects = r4;	 Catch:{ Exception -> 0x0dfd }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0dfd }
        r4.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.messages = r4;	 Catch:{ Exception -> 0x0dfd }
        r4 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0dfd }
        r4.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.originalPaths = r4;	 Catch:{ Exception -> 0x0dfd }
        r4 = new java.util.HashMap;	 Catch:{ Exception -> 0x0dfd }
        r4.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.extraHashMap = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r16;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.encryptedChat = r0;	 Catch:{ Exception -> 0x0dfd }
    L_0x0b13:
        if (r39 == 0) goto L_0x0b1d;	 Catch:{ Exception -> 0x0dfd }
    L_0x0b15:
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.id;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.finalGroupMessage = r4;	 Catch:{ Exception -> 0x0dfd }
    L_0x0b1d:
        r29 = r28;
        goto L_0x08d1;
    L_0x0b21:
        r0 = r68;	 Catch:{ Exception -> 0x0c82 }
        r7.message = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r58;	 Catch:{ Exception -> 0x0c82 }
        r7.contacts = r0;	 Catch:{ Exception -> 0x0c82 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaEmpty;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r55;	 Catch:{ Exception -> 0x0c82 }
        r7.random_id = r0;	 Catch:{ Exception -> 0x0c82 }
        r4 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0008;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b3e:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage;	 Catch:{ Exception -> 0x0c82 }
        r7.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r68;	 Catch:{ Exception -> 0x0c82 }
        r7.message = r0;	 Catch:{ Exception -> 0x0c82 }
        if (r82 != 0) goto L_0x0bc6;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b49:
        r4 = 1;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b4a:
        r7.clear_draft = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.to_id;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0b79;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b54:
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0c82 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0c82 }
        r6.<init>();	 Catch:{ Exception -> 0x0c82 }
        r8 = "silent_";	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0c82 }
        r0 = r76;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0c82 }
        r8 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0c82 }
        r7.silent = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b79:
        r0 = r57;	 Catch:{ Exception -> 0x0c82 }
        r7.peer = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0c82 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0b95;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b89:
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 1;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0c82 }
        r7.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b95:
        if (r81 != 0) goto L_0x0b9a;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b97:
        r4 = 1;	 Catch:{ Exception -> 0x0c82 }
        r7.no_webpage = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b9a:
        if (r83 == 0) goto L_0x0bac;	 Catch:{ Exception -> 0x0c82 }
    L_0x0b9c:
        r4 = r83.isEmpty();	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x0bac;	 Catch:{ Exception -> 0x0c82 }
    L_0x0ba2:
        r0 = r83;	 Catch:{ Exception -> 0x0c82 }
        r7.entities = r0;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 8;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0bac:
        r4 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x0c82 }
        if (r82 != 0) goto L_0x0bc2;	 Catch:{ Exception -> 0x0c82 }
    L_0x0bb4:
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r76;	 Catch:{ Exception -> 0x0c82 }
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0c82 }
    L_0x0bc2:
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0008;	 Catch:{ Exception -> 0x0c82 }
    L_0x0bc6:
        r4 = 0;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0b4a;	 Catch:{ Exception -> 0x0c82 }
    L_0x0bc8:
        r0 = r16;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.layer;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x0c82 }
        r6 = 73;	 Catch:{ Exception -> 0x0c82 }
        if (r4 < r6) goto L_0x0c73;	 Catch:{ Exception -> 0x0c82 }
    L_0x0bd4:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x0c82 }
        r7.<init>();	 Catch:{ Exception -> 0x0c82 }
    L_0x0bd9:
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.ttl;	 Catch:{ Exception -> 0x0c82 }
        r7.ttl = r4;	 Catch:{ Exception -> 0x0c82 }
        if (r83 == 0) goto L_0x0bf1;	 Catch:{ Exception -> 0x0c82 }
    L_0x0be1:
        r4 = r83.isEmpty();	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x0bf1;	 Catch:{ Exception -> 0x0c82 }
    L_0x0be7:
        r0 = r83;	 Catch:{ Exception -> 0x0c82 }
        r7.entities = r0;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 128;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0bf1:
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.reply_to_random_id;	 Catch:{ Exception -> 0x0c82 }
        r14 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0c07;	 Catch:{ Exception -> 0x0c82 }
    L_0x0bfb:
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.reply_to_random_id;	 Catch:{ Exception -> 0x0c82 }
        r7.reply_to_random_id = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 8;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c07:
        if (r85 == 0) goto L_0x0c27;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c09:
        r4 = "bot_name";	 Catch:{ Exception -> 0x0c82 }
        r0 = r85;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0c27;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c14:
        r4 = "bot_name";	 Catch:{ Exception -> 0x0c82 }
        r0 = r85;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0c82 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0c82 }
        r7.via_bot_name = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 2048;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c27:
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0c82 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r68;	 Catch:{ Exception -> 0x0c82 }
        r7.message = r0;	 Catch:{ Exception -> 0x0c82 }
        if (r80 == 0) goto L_0x0c7a;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c33:
        r0 = r80;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.url;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0c7a;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c39:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r80;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.url;	 Catch:{ Exception -> 0x0c82 }
        r4.url = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 512;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c4e:
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r10 = 0;	 Catch:{ Exception -> 0x0c82 }
        r11 = 0;	 Catch:{ Exception -> 0x0c82 }
        r9 = r16;	 Catch:{ Exception -> 0x0c82 }
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x0c82 }
        if (r82 != 0) goto L_0x0c6f;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c61:
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r76;	 Catch:{ Exception -> 0x0c82 }
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0c82 }
    L_0x0c6f:
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0008;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c73:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x0c82 }
        r7.<init>();	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0bd9;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c7a:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0c4e;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c82:
        r31 = move-exception;	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x021e;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c87:
        r4 = 1;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 < r4) goto L_0x0c91;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c8c:
        r4 = 3;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 <= r4) goto L_0x0ca4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c91:
        r4 = 5;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 < r4) goto L_0x0c9c;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c96:
        r4 = 8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 <= r4) goto L_0x0ca4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0c9c:
        r4 = 9;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x190b;	 Catch:{ Exception -> 0x0c82 }
    L_0x0ca2:
        if (r16 == 0) goto L_0x190b;	 Catch:{ Exception -> 0x0c82 }
    L_0x0ca4:
        if (r16 != 0) goto L_0x12bf;	 Catch:{ Exception -> 0x0c82 }
    L_0x0ca6:
        r36 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = 1;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x0d44;	 Catch:{ Exception -> 0x0c82 }
    L_0x0cad:
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0d2a;	 Catch:{ Exception -> 0x0c82 }
    L_0x0cb3:
        r36 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue;	 Catch:{ Exception -> 0x0c82 }
        r36.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.address;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.address = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.title;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.title = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.provider;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.provider = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.venue_id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.venue_id = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = "";	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.venue_type = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0cdf:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.geo_point = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.geo_point;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.geo;	 Catch:{ Exception -> 0x0c82 }
        r8 = r6.lat;	 Catch:{ Exception -> 0x0c82 }
        r4.lat = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.geo_point;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.geo;	 Catch:{ Exception -> 0x0c82 }
        r8 = r6._long;	 Catch:{ Exception -> 0x0c82 }
        r4._long = r8;	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;
    L_0x0d02:
        if (r58 == 0) goto L_0x10d0;
    L_0x0d04:
        r56 = new org.telegram.tgnet.TLRPC$TL_messages_sendBroadcast;	 Catch:{ Exception -> 0x0dfd }
        r56.<init>();	 Catch:{ Exception -> 0x0dfd }
        r55 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0dfd }
        r55.<init>();	 Catch:{ Exception -> 0x0dfd }
        r20 = 0;	 Catch:{ Exception -> 0x0dfd }
    L_0x0d10:
        r4 = r58.size();	 Catch:{ Exception -> 0x0dfd }
        r0 = r20;	 Catch:{ Exception -> 0x0dfd }
        if (r0 >= r4) goto L_0x108e;	 Catch:{ Exception -> 0x0dfd }
    L_0x0d18:
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0dfd }
        r8 = r4.nextLong();	 Catch:{ Exception -> 0x0dfd }
        r4 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0dfd }
        r0 = r55;	 Catch:{ Exception -> 0x0dfd }
        r0.add(r4);	 Catch:{ Exception -> 0x0dfd }
        r20 = r20 + 1;
        goto L_0x0d10;
    L_0x0d2a:
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0d3e;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d30:
        r36 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive;	 Catch:{ Exception -> 0x0c82 }
        r36.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.period;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.period = r4;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0cdf;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d3e:
        r36 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint;	 Catch:{ Exception -> 0x0c82 }
        r36.<init>();	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0cdf;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d44:
        r4 = 2;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 == r4) goto L_0x0d51;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d49:
        r4 = 9;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x0e44;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d4f:
        if (r71 == 0) goto L_0x0e44;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d51:
        r0 = r71;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r14 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x0e1c;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d5b:
        r36 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;	 Catch:{ Exception -> 0x0c82 }
        r36.<init>();	 Catch:{ Exception -> 0x0c82 }
        if (r86 == 0) goto L_0x0d78;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d62:
        r0 = r86;	 Catch:{ Exception -> 0x0c82 }
        r1 = r36;	 Catch:{ Exception -> 0x0c82 }
        r1.ttl_seconds = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r86;	 Catch:{ Exception -> 0x0c82 }
        r1 = r46;	 Catch:{ Exception -> 0x0c82 }
        r1.ttl = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 2;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d78:
        if (r85 == 0) goto L_0x0dc6;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d7a:
        r4 = "masks";	 Catch:{ Exception -> 0x0c82 }
        r0 = r85;	 Catch:{ Exception -> 0x0c82 }
        r43 = r0.get(r4);	 Catch:{ Exception -> 0x0c82 }
        r43 = (java.lang.String) r43;	 Catch:{ Exception -> 0x0c82 }
        if (r43 == 0) goto L_0x0dc6;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d87:
        r61 = new org.telegram.tgnet.SerializedData;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.Utilities.hexToBytes(r43);	 Catch:{ Exception -> 0x0c82 }
        r0 = r61;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r4);	 Catch:{ Exception -> 0x0c82 }
        r4 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r61;	 Catch:{ Exception -> 0x0c82 }
        r27 = r0.readInt32(r4);	 Catch:{ Exception -> 0x0c82 }
        r20 = 0;	 Catch:{ Exception -> 0x0c82 }
    L_0x0d9b:
        r0 = r20;	 Catch:{ Exception -> 0x0c82 }
        r1 = r27;	 Catch:{ Exception -> 0x0c82 }
        if (r0 >= r1) goto L_0x0db9;	 Catch:{ Exception -> 0x0c82 }
    L_0x0da1:
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.stickers;	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r61;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.readInt32(r6);	 Catch:{ Exception -> 0x0c82 }
        r8 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r61;	 Catch:{ Exception -> 0x0c82 }
        r6 = org.telegram.tgnet.TLRPC.InputDocument.TLdeserialize(r0, r6, r8);	 Catch:{ Exception -> 0x0c82 }
        r4.add(r6);	 Catch:{ Exception -> 0x0c82 }
        r20 = r20 + 1;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0d9b;	 Catch:{ Exception -> 0x0c82 }
    L_0x0db9:
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 1;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0c82 }
        r61.cleanup();	 Catch:{ Exception -> 0x0c82 }
    L_0x0dc6:
        if (r29 != 0) goto L_0x1aa0;	 Catch:{ Exception -> 0x0c82 }
    L_0x0dc8:
        r28 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c82 }
        r0 = r28;	 Catch:{ Exception -> 0x0c82 }
        r1 = r67;	 Catch:{ Exception -> 0x0c82 }
        r2 = r76;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0c82 }
        r4 = 0;
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.type = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.obj = r12;	 Catch:{ Exception -> 0x0dfd }
        r0 = r51;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0dfd }
    L_0x0de2:
        if (r78 == 0) goto L_0x0e00;	 Catch:{ Exception -> 0x0dfd }
    L_0x0de4:
        r4 = r78.length();	 Catch:{ Exception -> 0x0dfd }
        if (r4 <= 0) goto L_0x0e00;	 Catch:{ Exception -> 0x0dfd }
    L_0x0dea:
        r4 = "http";	 Catch:{ Exception -> 0x0dfd }
        r0 = r78;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x0e00;	 Catch:{ Exception -> 0x0dfd }
    L_0x0df5:
        r0 = r78;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.httpLocation = r0;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0d02;	 Catch:{ Exception -> 0x0dfd }
    L_0x0dfd:
        r31 = move-exception;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x021e;	 Catch:{ Exception -> 0x0dfd }
    L_0x0e00:
        r0 = r71;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0dfd }
        r0 = r71;	 Catch:{ Exception -> 0x0dfd }
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.size();	 Catch:{ Exception -> 0x0dfd }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0dfd }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.location;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.location = r4;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0d02;
    L_0x0e1c:
        r44 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;	 Catch:{ Exception -> 0x0c82 }
        r44.<init>();	 Catch:{ Exception -> 0x0c82 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPhoto;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r0.id = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r71;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r4.id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r71;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0c82 }
        r36 = r44;	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0d02;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e44:
        r4 = 3;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x0eef;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e49:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r14 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x0ec7;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e53:
        r36 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0c82 }
        r36.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.mime_type = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.attributes = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r74);	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x0e81;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e6e:
        if (r72 == 0) goto L_0x0e7c;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e70:
        r0 = r72;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.muted;	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x0e81;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e76:
        r0 = r72;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.roundVideo;	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x0e81;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e7c:
        r4 = 1;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.nosound_video = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e81:
        if (r86 == 0) goto L_0x0e99;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e83:
        r0 = r86;	 Catch:{ Exception -> 0x0c82 }
        r1 = r36;	 Catch:{ Exception -> 0x0c82 }
        r1.ttl_seconds = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r86;	 Catch:{ Exception -> 0x0c82 }
        r1 = r46;	 Catch:{ Exception -> 0x0c82 }
        r1.ttl = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 2;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e99:
        if (r29 != 0) goto L_0x1a9c;	 Catch:{ Exception -> 0x0c82 }
    L_0x0e9b:
        r28 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c82 }
        r0 = r28;	 Catch:{ Exception -> 0x0c82 }
        r1 = r67;	 Catch:{ Exception -> 0x0c82 }
        r2 = r76;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0c82 }
        r4 = 1;
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.type = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.obj = r12;	 Catch:{ Exception -> 0x0dfd }
        r0 = r51;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0dfd }
    L_0x0eb5:
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.location;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.location = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r72;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.videoEditedInfo = r0;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0d02;
    L_0x0ec7:
        r44 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0c82 }
        r44.<init>();	 Catch:{ Exception -> 0x0c82 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r0.id = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r4.id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0c82 }
        r36 = r44;	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0d02;	 Catch:{ Exception -> 0x0c82 }
    L_0x0eef:
        r4 = 6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x0f3b;	 Catch:{ Exception -> 0x0c82 }
    L_0x0ef4:
        r36 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact;	 Catch:{ Exception -> 0x0c82 }
        r36.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.phone;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.phone_number = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.first_name;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.first_name = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.last_name;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.last_name = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0f30;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f17:
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x0c82 }
        r6 = "BEGIN:VCARD";	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.startsWith(r6);	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0f30;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f24:
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.restriction_reason;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.vcard = r4;	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0d02;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f30:
        r4 = "";	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.vcard = r4;	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0d02;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f3b:
        r4 = 7;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 == r4) goto L_0x0f46;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f40:
        r4 = 9;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x1013;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f46:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r14 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x0feb;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f50:
        if (r16 != 0) goto L_0x0fa9;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f52:
        if (r51 == 0) goto L_0x0fa9;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f54:
        r4 = r51.length();	 Catch:{ Exception -> 0x0c82 }
        if (r4 <= 0) goto L_0x0fa9;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f5a:
        r4 = "http";	 Catch:{ Exception -> 0x0c82 }
        r0 = r51;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x0fa9;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f65:
        if (r85 == 0) goto L_0x0fa9;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f67:
        r36 = new org.telegram.tgnet.TLRPC$TL_inputMediaGifExternal;	 Catch:{ Exception -> 0x0c82 }
        r36.<init>();	 Catch:{ Exception -> 0x0c82 }
        r4 = "url";	 Catch:{ Exception -> 0x0c82 }
        r0 = r85;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0c82 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0c82 }
        r6 = "\\|";	 Catch:{ Exception -> 0x0c82 }
        r21 = r4.split(r6);	 Catch:{ Exception -> 0x0c82 }
        r0 = r21;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.length;	 Catch:{ Exception -> 0x0c82 }
        r6 = 2;	 Catch:{ Exception -> 0x0c82 }
        if (r4 != r6) goto L_0x0f95;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f84:
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0 = (org.telegram.tgnet.TLRPC.TL_inputMediaGifExternal) r0;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0;	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r6 = r21[r6];	 Catch:{ Exception -> 0x0c82 }
        r4.url = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = 1;	 Catch:{ Exception -> 0x0c82 }
        r4 = r21[r4];	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.f30q = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0f95:
        r28 = r29;
    L_0x0f97:
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x0dfd }
        r0 = r36;	 Catch:{ Exception -> 0x0dfd }
        r0.mime_type = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0dfd }
        r0 = r36;	 Catch:{ Exception -> 0x0dfd }
        r0.attributes = r4;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0d02;
    L_0x0fa9:
        r36 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0c82 }
        r36.<init>();	 Catch:{ Exception -> 0x0c82 }
        if (r86 == 0) goto L_0x0fc6;	 Catch:{ Exception -> 0x0c82 }
    L_0x0fb0:
        r0 = r86;	 Catch:{ Exception -> 0x0c82 }
        r1 = r36;	 Catch:{ Exception -> 0x0c82 }
        r1.ttl_seconds = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r86;	 Catch:{ Exception -> 0x0c82 }
        r1 = r46;	 Catch:{ Exception -> 0x0c82 }
        r1.ttl = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 2;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x0fc6:
        r28 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c82 }
        r0 = r28;	 Catch:{ Exception -> 0x0c82 }
        r1 = r67;	 Catch:{ Exception -> 0x0c82 }
        r2 = r76;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0c82 }
        r0 = r51;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0dfd }
        r4 = 2;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.type = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.obj = r12;	 Catch:{ Exception -> 0x0dfd }
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.location;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.location = r4;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0f97;
    L_0x0feb:
        r44 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0c82 }
        r44.<init>();	 Catch:{ Exception -> 0x0c82 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r0.id = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r4.id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0c82 }
        r36 = r44;	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0d02;	 Catch:{ Exception -> 0x0c82 }
    L_0x1013:
        r4 = 8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x1a98;	 Catch:{ Exception -> 0x0c82 }
    L_0x1019:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r14 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x1066;	 Catch:{ Exception -> 0x0c82 }
    L_0x1023:
        r36 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;	 Catch:{ Exception -> 0x0c82 }
        r36.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.mime_type;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.mime_type = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.attributes = r4;	 Catch:{ Exception -> 0x0c82 }
        if (r86 == 0) goto L_0x1050;	 Catch:{ Exception -> 0x0c82 }
    L_0x103a:
        r0 = r86;	 Catch:{ Exception -> 0x0c82 }
        r1 = r36;	 Catch:{ Exception -> 0x0c82 }
        r1.ttl_seconds = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r86;	 Catch:{ Exception -> 0x0c82 }
        r1 = r46;	 Catch:{ Exception -> 0x0c82 }
        r1.ttl = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 2;	 Catch:{ Exception -> 0x0c82 }
        r0 = r36;	 Catch:{ Exception -> 0x0c82 }
        r0.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x1050:
        r28 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c82 }
        r0 = r28;	 Catch:{ Exception -> 0x0c82 }
        r1 = r67;	 Catch:{ Exception -> 0x0c82 }
        r2 = r76;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0c82 }
        r4 = 3;
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.type = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.obj = r12;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0d02;
    L_0x1066:
        r44 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument;	 Catch:{ Exception -> 0x0c82 }
        r44.<init>();	 Catch:{ Exception -> 0x0c82 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r0.id = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r4.id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r44;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0c82 }
        r36 = r44;
        r28 = r29;
        goto L_0x0d02;
    L_0x108e:
        r0 = r58;	 Catch:{ Exception -> 0x0dfd }
        r1 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1.contacts = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r36;	 Catch:{ Exception -> 0x0dfd }
        r1 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1.media = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r55;	 Catch:{ Exception -> 0x0dfd }
        r1 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1.random_id = r0;	 Catch:{ Exception -> 0x0dfd }
        r4 = "";	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r0.message = r4;	 Catch:{ Exception -> 0x0dfd }
        if (r28 == 0) goto L_0x10af;	 Catch:{ Exception -> 0x0dfd }
    L_0x10a9:
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.sendRequest = r0;	 Catch:{ Exception -> 0x0dfd }
    L_0x10af:
        r7 = r56;	 Catch:{ Exception -> 0x0dfd }
        if (r82 != 0) goto L_0x10c1;	 Catch:{ Exception -> 0x0dfd }
    L_0x10b3:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0dfd }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0dfd }
        r6 = 0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r76;	 Catch:{ Exception -> 0x0dfd }
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0dfd }
    L_0x10c1:
        r8 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x121e;	 Catch:{ Exception -> 0x0dfd }
    L_0x10c7:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x10d0:
        r8 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x1194;	 Catch:{ Exception -> 0x0dfd }
    L_0x10d6:
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.sendRequest;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x113a;	 Catch:{ Exception -> 0x0dfd }
    L_0x10dc:
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0 = r0.sendRequest;	 Catch:{ Exception -> 0x0dfd }
        r56 = r0;	 Catch:{ Exception -> 0x0dfd }
        r56 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r56;	 Catch:{ Exception -> 0x0dfd }
    L_0x10e4:
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.messageObjects;	 Catch:{ Exception -> 0x0dfd }
        r4.add(r12);	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.messages;	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4.add(r0);	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.originalPaths;	 Catch:{ Exception -> 0x0dfd }
        r0 = r51;	 Catch:{ Exception -> 0x0dfd }
        r4.add(r0);	 Catch:{ Exception -> 0x0dfd }
        r37 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia;	 Catch:{ Exception -> 0x0dfd }
        r37.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0dfd }
        r0 = r37;	 Catch:{ Exception -> 0x0dfd }
        r0.random_id = r8;	 Catch:{ Exception -> 0x0dfd }
        r0 = r36;	 Catch:{ Exception -> 0x0dfd }
        r1 = r37;	 Catch:{ Exception -> 0x0dfd }
        r1.media = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r69;	 Catch:{ Exception -> 0x0dfd }
        r1 = r37;	 Catch:{ Exception -> 0x0dfd }
        r1.message = r0;	 Catch:{ Exception -> 0x0dfd }
        if (r83 == 0) goto L_0x112e;	 Catch:{ Exception -> 0x0dfd }
    L_0x1118:
        r4 = r83.isEmpty();	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x112e;	 Catch:{ Exception -> 0x0dfd }
    L_0x111e:
        r0 = r83;	 Catch:{ Exception -> 0x0dfd }
        r1 = r37;	 Catch:{ Exception -> 0x0dfd }
        r1.entities = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r37;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4 | 1;	 Catch:{ Exception -> 0x0dfd }
        r0 = r37;	 Catch:{ Exception -> 0x0dfd }
        r0.flags = r4;	 Catch:{ Exception -> 0x0dfd }
    L_0x112e:
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.multi_media;	 Catch:{ Exception -> 0x0dfd }
        r0 = r37;	 Catch:{ Exception -> 0x0dfd }
        r4.add(r0);	 Catch:{ Exception -> 0x0dfd }
        r7 = r56;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x10c1;	 Catch:{ Exception -> 0x0dfd }
    L_0x113a:
        r56 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;	 Catch:{ Exception -> 0x0dfd }
        r56.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r57;	 Catch:{ Exception -> 0x0dfd }
        r1 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1.peer = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.to_id;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x1174;	 Catch:{ Exception -> 0x0dfd }
    L_0x114d:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0dfd }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0dfd }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0dfd }
        r6.<init>();	 Catch:{ Exception -> 0x0dfd }
        r8 = "silent_";	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0dfd }
        r0 = r76;	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0dfd }
        r8 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r0.silent = r4;	 Catch:{ Exception -> 0x0dfd }
    L_0x1174:
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x118c;	 Catch:{ Exception -> 0x0dfd }
    L_0x117a:
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4 | 1;	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r0.flags = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r0.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0dfd }
    L_0x118c:
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.sendRequest = r0;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x10e4;	 Catch:{ Exception -> 0x0dfd }
    L_0x1194:
        r56 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia;	 Catch:{ Exception -> 0x0dfd }
        r56.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r57;	 Catch:{ Exception -> 0x0dfd }
        r1 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1.peer = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.to_id;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x11ce;	 Catch:{ Exception -> 0x0dfd }
    L_0x11a7:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0dfd }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0dfd }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0dfd }
        r6.<init>();	 Catch:{ Exception -> 0x0dfd }
        r8 = "silent_";	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0dfd }
        r0 = r76;	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0dfd }
        r8 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r0.silent = r4;	 Catch:{ Exception -> 0x0dfd }
    L_0x11ce:
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x11e6;	 Catch:{ Exception -> 0x0dfd }
    L_0x11d4:
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4 | 1;	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r0.flags = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r0.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0dfd }
    L_0x11e6:
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r0.random_id = r8;	 Catch:{ Exception -> 0x0dfd }
        r0 = r36;	 Catch:{ Exception -> 0x0dfd }
        r1 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1.media = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r69;	 Catch:{ Exception -> 0x0dfd }
        r1 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1.message = r0;	 Catch:{ Exception -> 0x0dfd }
        if (r83 == 0) goto L_0x1212;	 Catch:{ Exception -> 0x0dfd }
    L_0x11fc:
        r4 = r83.isEmpty();	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x1212;	 Catch:{ Exception -> 0x0dfd }
    L_0x1202:
        r0 = r83;	 Catch:{ Exception -> 0x0dfd }
        r1 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1.entities = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.flags;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4 | 8;	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r0.flags = r4;	 Catch:{ Exception -> 0x0dfd }
    L_0x1212:
        if (r28 == 0) goto L_0x121a;	 Catch:{ Exception -> 0x0dfd }
    L_0x1214:
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.sendRequest = r0;	 Catch:{ Exception -> 0x0dfd }
    L_0x121a:
        r7 = r56;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x10c1;	 Catch:{ Exception -> 0x0dfd }
    L_0x121e:
        r4 = 1;	 Catch:{ Exception -> 0x0dfd }
        r0 = r63;	 Catch:{ Exception -> 0x0dfd }
        if (r0 != r4) goto L_0x122b;	 Catch:{ Exception -> 0x0dfd }
    L_0x1223:
        r4 = 0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x122b:
        r4 = 2;	 Catch:{ Exception -> 0x0dfd }
        r0 = r63;	 Catch:{ Exception -> 0x0dfd }
        if (r0 != r4) goto L_0x124e;	 Catch:{ Exception -> 0x0dfd }
    L_0x1230:
        r0 = r71;	 Catch:{ Exception -> 0x0dfd }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0dfd }
        r14 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x1243;	 Catch:{ Exception -> 0x0dfd }
    L_0x123a:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x1243:
        r9 = 0;	 Catch:{ Exception -> 0x0dfd }
        r10 = 0;	 Catch:{ Exception -> 0x0dfd }
        r11 = 1;	 Catch:{ Exception -> 0x0dfd }
        r6 = r67;	 Catch:{ Exception -> 0x0dfd }
        r8 = r12;	 Catch:{ Exception -> 0x0dfd }
        r6.performSendMessageRequest(r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x124e:
        r4 = 3;	 Catch:{ Exception -> 0x0dfd }
        r0 = r63;	 Catch:{ Exception -> 0x0dfd }
        if (r0 != r4) goto L_0x126e;	 Catch:{ Exception -> 0x0dfd }
    L_0x1253:
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0dfd }
        r14 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x1266;	 Catch:{ Exception -> 0x0dfd }
    L_0x125d:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x1266:
        r4 = 0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x126e:
        r4 = 6;	 Catch:{ Exception -> 0x0dfd }
        r0 = r63;	 Catch:{ Exception -> 0x0dfd }
        if (r0 != r4) goto L_0x127b;	 Catch:{ Exception -> 0x0dfd }
    L_0x1273:
        r4 = 0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x127b:
        r4 = 7;	 Catch:{ Exception -> 0x0dfd }
        r0 = r63;	 Catch:{ Exception -> 0x0dfd }
        if (r0 != r4) goto L_0x129e;	 Catch:{ Exception -> 0x0dfd }
    L_0x1280:
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0dfd }
        r14 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x1295;	 Catch:{ Exception -> 0x0dfd }
    L_0x128a:
        if (r28 == 0) goto L_0x1295;	 Catch:{ Exception -> 0x0dfd }
    L_0x128c:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x1295:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r51;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendMessageRequest(r7, r12, r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x129e:
        r4 = 8;	 Catch:{ Exception -> 0x0dfd }
        r0 = r63;	 Catch:{ Exception -> 0x0dfd }
        if (r0 != r4) goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x12a4:
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0dfd }
        r14 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x12b7;	 Catch:{ Exception -> 0x0dfd }
    L_0x12ae:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x12b7:
        r4 = 0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;
    L_0x12bf:
        r0 = r16;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.layer;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);	 Catch:{ Exception -> 0x0c82 }
        r6 = 73;	 Catch:{ Exception -> 0x0c82 }
        if (r4 < r6) goto L_0x1400;	 Catch:{ Exception -> 0x0c82 }
    L_0x12cb:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage;	 Catch:{ Exception -> 0x0c82 }
        r7.<init>();	 Catch:{ Exception -> 0x0c82 }
        r8 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x12e1;	 Catch:{ Exception -> 0x0c82 }
    L_0x12d6:
        r0 = r32;	 Catch:{ Exception -> 0x0c82 }
        r7.grouped_id = r0;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r6 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | r6;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x12e1:
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.ttl;	 Catch:{ Exception -> 0x0c82 }
        r7.ttl = r4;	 Catch:{ Exception -> 0x0c82 }
        if (r83 == 0) goto L_0x12f9;	 Catch:{ Exception -> 0x0c82 }
    L_0x12e9:
        r4 = r83.isEmpty();	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x12f9;	 Catch:{ Exception -> 0x0c82 }
    L_0x12ef:
        r0 = r83;	 Catch:{ Exception -> 0x0c82 }
        r7.entities = r0;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 128;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x12f9:
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.reply_to_random_id;	 Catch:{ Exception -> 0x0c82 }
        r14 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x130f;	 Catch:{ Exception -> 0x0c82 }
    L_0x1303:
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.reply_to_random_id;	 Catch:{ Exception -> 0x0c82 }
        r7.reply_to_random_id = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 8;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x130f:
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 512;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
        if (r85 == 0) goto L_0x1335;	 Catch:{ Exception -> 0x0c82 }
    L_0x1317:
        r4 = "bot_name";	 Catch:{ Exception -> 0x0c82 }
        r0 = r85;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1335;	 Catch:{ Exception -> 0x0c82 }
    L_0x1322:
        r4 = "bot_name";	 Catch:{ Exception -> 0x0c82 }
        r0 = r85;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0c82 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0c82 }
        r7.via_bot_name = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 2048;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x1335:
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0c82 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = "";	 Catch:{ Exception -> 0x0c82 }
        r7.message = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = 1;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x1410;	 Catch:{ Exception -> 0x0c82 }
    L_0x1345:
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1407;	 Catch:{ Exception -> 0x0c82 }
    L_0x134b:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.address;	 Catch:{ Exception -> 0x0c82 }
        r4.address = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.title;	 Catch:{ Exception -> 0x0c82 }
        r4.title = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.provider;	 Catch:{ Exception -> 0x0c82 }
        r4.provider = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.venue_id;	 Catch:{ Exception -> 0x0c82 }
        r4.venue_id = r6;	 Catch:{ Exception -> 0x0c82 }
    L_0x1372:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.geo;	 Catch:{ Exception -> 0x0c82 }
        r8 = r6.lat;	 Catch:{ Exception -> 0x0c82 }
        r4.lat = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r70;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.geo;	 Catch:{ Exception -> 0x0c82 }
        r8 = r6._long;	 Catch:{ Exception -> 0x0c82 }
        r4._long = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r10 = 0;	 Catch:{ Exception -> 0x0c82 }
        r11 = 0;	 Catch:{ Exception -> 0x0c82 }
        r9 = r16;	 Catch:{ Exception -> 0x0c82 }
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;
    L_0x1399:
        r8 = 0;
        r4 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x13ee;
    L_0x139f:
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.sendEncryptedRequest;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x18fa;	 Catch:{ Exception -> 0x0dfd }
    L_0x13a5:
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0 = r0.sendEncryptedRequest;	 Catch:{ Exception -> 0x0dfd }
        r56 = r0;	 Catch:{ Exception -> 0x0dfd }
        r56 = (org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia) r56;	 Catch:{ Exception -> 0x0dfd }
    L_0x13ad:
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.messageObjects;	 Catch:{ Exception -> 0x0dfd }
        r4.add(r12);	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.messages;	 Catch:{ Exception -> 0x0dfd }
        r0 = r46;	 Catch:{ Exception -> 0x0dfd }
        r4.add(r0);	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.originalPaths;	 Catch:{ Exception -> 0x0dfd }
        r0 = r51;	 Catch:{ Exception -> 0x0dfd }
        r4.add(r0);	 Catch:{ Exception -> 0x0dfd }
        r4 = 1;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.upload = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.messages;	 Catch:{ Exception -> 0x0dfd }
        r4.add(r7);	 Catch:{ Exception -> 0x0dfd }
        r10 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0dfd }
        r10.<init>();	 Catch:{ Exception -> 0x0dfd }
        r4 = 3;	 Catch:{ Exception -> 0x0dfd }
        r0 = r63;	 Catch:{ Exception -> 0x0dfd }
        if (r0 != r4) goto L_0x1907;	 Catch:{ Exception -> 0x0dfd }
    L_0x13dc:
        r8 = 1;	 Catch:{ Exception -> 0x0dfd }
    L_0x13de:
        r10.id = r8;	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.files;	 Catch:{ Exception -> 0x0dfd }
        r4.add(r10);	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
    L_0x13ee:
        if (r82 != 0) goto L_0x0008;	 Catch:{ Exception -> 0x0dfd }
    L_0x13f0:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0dfd }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0dfd }
        r6 = 0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r76;	 Catch:{ Exception -> 0x0dfd }
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x0008;
    L_0x1400:
        r7 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45;	 Catch:{ Exception -> 0x0c82 }
        r7.<init>();	 Catch:{ Exception -> 0x0c82 }
        goto L_0x12e1;	 Catch:{ Exception -> 0x0c82 }
    L_0x1407:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1372;	 Catch:{ Exception -> 0x0c82 }
    L_0x1410:
        r4 = 2;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 == r4) goto L_0x141d;	 Catch:{ Exception -> 0x0c82 }
    L_0x1415:
        r4 = 9;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x1541;	 Catch:{ Exception -> 0x0c82 }
    L_0x141b:
        if (r71 == 0) goto L_0x1541;	 Catch:{ Exception -> 0x0c82 }
    L_0x141d:
        r0 = r71;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r62 = r4.get(r6);	 Catch:{ Exception -> 0x0c82 }
        r62 = (org.telegram.tgnet.TLRPC.PhotoSize) r62;	 Catch:{ Exception -> 0x0c82 }
        r0 = r71;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0c82 }
        r0 = r71;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0c82 }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x0c82 }
        r25 = r4.get(r6);	 Catch:{ Exception -> 0x0c82 }
        r25 = (org.telegram.tgnet.TLRPC.PhotoSize) r25;	 Catch:{ Exception -> 0x0c82 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r62);	 Catch:{ Exception -> 0x0c82 }
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r69;	 Catch:{ Exception -> 0x0c82 }
        r4.caption = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r62;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.bytes;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x14de;	 Catch:{ Exception -> 0x0c82 }
    L_0x1452:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r62;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.bytes;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
    L_0x145c:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r62;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.f33h;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r62;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.f34w;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r25;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.f34w;	 Catch:{ Exception -> 0x0c82 }
        r4.f25w = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r25;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.f33h;	 Catch:{ Exception -> 0x0c82 }
        r4.f24h = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r25;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.size;	 Catch:{ Exception -> 0x0c82 }
        r4.size = r6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r25;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.location;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.key;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1492;	 Catch:{ Exception -> 0x0c82 }
    L_0x148c:
        r8 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1504;	 Catch:{ Exception -> 0x0c82 }
    L_0x1492:
        if (r29 != 0) goto L_0x1a94;	 Catch:{ Exception -> 0x0c82 }
    L_0x1494:
        r28 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c82 }
        r0 = r28;	 Catch:{ Exception -> 0x0c82 }
        r1 = r67;	 Catch:{ Exception -> 0x0c82 }
        r2 = r76;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0c82 }
        r0 = r16;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.encryptedChat = r0;	 Catch:{ Exception -> 0x0dfd }
        r4 = 0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.type = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r51;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.obj = r12;	 Catch:{ Exception -> 0x0dfd }
    L_0x14b8:
        r4 = android.text.TextUtils.isEmpty(r78);	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x14e9;	 Catch:{ Exception -> 0x0dfd }
    L_0x14be:
        r4 = "http";	 Catch:{ Exception -> 0x0dfd }
        r0 = r78;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x14e9;	 Catch:{ Exception -> 0x0dfd }
    L_0x14c9:
        r0 = r78;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.httpLocation = r0;	 Catch:{ Exception -> 0x0dfd }
    L_0x14cf:
        r8 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x1399;	 Catch:{ Exception -> 0x0dfd }
    L_0x14d5:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x1399;
    L_0x14de:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto) r4;	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x145c;
    L_0x14e9:
        r0 = r71;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.sizes;	 Catch:{ Exception -> 0x0dfd }
        r0 = r71;	 Catch:{ Exception -> 0x0dfd }
        r6 = r0.sizes;	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.size();	 Catch:{ Exception -> 0x0dfd }
        r6 = r6 + -1;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0dfd }
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.location;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.location = r4;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x14cf;
    L_0x1504:
        r10 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0c82 }
        r10.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r25;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.location;	 Catch:{ Exception -> 0x0c82 }
        r8 = r4.volume_id;	 Catch:{ Exception -> 0x0c82 }
        r10.id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r25;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.location;	 Catch:{ Exception -> 0x0c82 }
        r8 = r4.secret;	 Catch:{ Exception -> 0x0c82 }
        r10.access_hash = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r25;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.location;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.key;	 Catch:{ Exception -> 0x0c82 }
        r4.key = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r25;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.location;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.iv;	 Catch:{ Exception -> 0x0c82 }
        r4.iv = r6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r11 = 0;	 Catch:{ Exception -> 0x0c82 }
        r9 = r16;	 Catch:{ Exception -> 0x0c82 }
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x0c82 }
    L_0x153d:
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1399;	 Catch:{ Exception -> 0x0c82 }
    L_0x1541:
        r4 = 3;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x169c;	 Catch:{ Exception -> 0x0c82 }
    L_0x1546:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r4);	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.MessageObject.isNewGifDocument(r74);	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x1559;	 Catch:{ Exception -> 0x0c82 }
    L_0x1553:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r74);	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1635;	 Catch:{ Exception -> 0x0c82 }
    L_0x1559:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.attributes;	 Catch:{ Exception -> 0x0c82 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x162a;	 Catch:{ Exception -> 0x0c82 }
    L_0x156e:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x162a;	 Catch:{ Exception -> 0x0c82 }
    L_0x1576:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
    L_0x1582:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r69;	 Catch:{ Exception -> 0x0c82 }
        r4.caption = r0;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r6 = "video/mp4";	 Catch:{ Exception -> 0x0c82 }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.size;	 Catch:{ Exception -> 0x0c82 }
        r4.size = r6;	 Catch:{ Exception -> 0x0c82 }
        r20 = 0;	 Catch:{ Exception -> 0x0c82 }
    L_0x1599:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.size();	 Catch:{ Exception -> 0x0c82 }
        r0 = r20;	 Catch:{ Exception -> 0x0c82 }
        if (r0 >= r4) goto L_0x15cf;	 Catch:{ Exception -> 0x0c82 }
    L_0x15a5:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.attributes;	 Catch:{ Exception -> 0x0c82 }
        r0 = r20;	 Catch:{ Exception -> 0x0c82 }
        r23 = r4.get(r0);	 Catch:{ Exception -> 0x0c82 }
        r23 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r23;	 Catch:{ Exception -> 0x0c82 }
        r0 = r23;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1663;	 Catch:{ Exception -> 0x0c82 }
    L_0x15b7:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r23;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.f27w;	 Catch:{ Exception -> 0x0c82 }
        r4.f25w = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r23;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.f26h;	 Catch:{ Exception -> 0x0c82 }
        r4.f24h = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r23;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.duration;	 Catch:{ Exception -> 0x0c82 }
        r4.duration = r6;	 Catch:{ Exception -> 0x0c82 }
    L_0x15cf:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.f33h;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.f34w;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.key;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x15ef;	 Catch:{ Exception -> 0x0c82 }
    L_0x15e9:
        r8 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1667;	 Catch:{ Exception -> 0x0c82 }
    L_0x15ef:
        if (r29 != 0) goto L_0x1a90;	 Catch:{ Exception -> 0x0c82 }
    L_0x15f1:
        r28 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c82 }
        r0 = r28;	 Catch:{ Exception -> 0x0c82 }
        r1 = r67;	 Catch:{ Exception -> 0x0c82 }
        r2 = r76;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0c82 }
        r0 = r16;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.encryptedChat = r0;	 Catch:{ Exception -> 0x0dfd }
        r4 = 1;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.type = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x0dfd }
        r0 = r51;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.obj = r12;	 Catch:{ Exception -> 0x0dfd }
    L_0x1615:
        r0 = r72;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.videoEditedInfo = r0;	 Catch:{ Exception -> 0x0dfd }
        r8 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4 = (r32 > r8 ? 1 : (r32 == r8 ? 0 : -1));	 Catch:{ Exception -> 0x0dfd }
        if (r4 != 0) goto L_0x1399;	 Catch:{ Exception -> 0x0dfd }
    L_0x1621:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x1399;
    L_0x162a:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1582;	 Catch:{ Exception -> 0x0c82 }
    L_0x1635:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1658;	 Catch:{ Exception -> 0x0c82 }
    L_0x1642:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1658;	 Catch:{ Exception -> 0x0c82 }
    L_0x164a:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1582;	 Catch:{ Exception -> 0x0c82 }
    L_0x1658:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo) r4;	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1582;	 Catch:{ Exception -> 0x0c82 }
    L_0x1663:
        r20 = r20 + 1;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1599;	 Catch:{ Exception -> 0x0c82 }
    L_0x1667:
        r10 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0c82 }
        r10.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r10.id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r10.access_hash = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.key;	 Catch:{ Exception -> 0x0c82 }
        r4.key = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.iv;	 Catch:{ Exception -> 0x0c82 }
        r4.iv = r6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r11 = 0;	 Catch:{ Exception -> 0x0c82 }
        r9 = r16;	 Catch:{ Exception -> 0x0c82 }
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1399;	 Catch:{ Exception -> 0x0c82 }
    L_0x169c:
        r4 = 6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x16e0;	 Catch:{ Exception -> 0x0c82 }
    L_0x16a1:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.phone;	 Catch:{ Exception -> 0x0c82 }
        r4.phone_number = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.first_name;	 Catch:{ Exception -> 0x0c82 }
        r4.first_name = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.last_name;	 Catch:{ Exception -> 0x0c82 }
        r4.last_name = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r73;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r4.user_id = r6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r13 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r15 = r12.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r17 = 0;	 Catch:{ Exception -> 0x0c82 }
        r18 = 0;	 Catch:{ Exception -> 0x0c82 }
        r14 = r7;	 Catch:{ Exception -> 0x0c82 }
        r19 = r12;	 Catch:{ Exception -> 0x0c82 }
        r13.performSendEncryptedRequest(r14, r15, r16, r17, r18, r19);	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1399;	 Catch:{ Exception -> 0x0c82 }
    L_0x16e0:
        r4 = 7;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 == r4) goto L_0x16ed;	 Catch:{ Exception -> 0x0c82 }
    L_0x16e5:
        r4 = 9;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x1860;	 Catch:{ Exception -> 0x0c82 }
    L_0x16eb:
        if (r74 == 0) goto L_0x1860;	 Catch:{ Exception -> 0x0c82 }
    L_0x16ed:
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r74);	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1771;	 Catch:{ Exception -> 0x0c82 }
    L_0x16f3:
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r4.id = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.date;	 Catch:{ Exception -> 0x0c82 }
        r4.date = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.mime_type;	 Catch:{ Exception -> 0x0c82 }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.size;	 Catch:{ Exception -> 0x0c82 }
        r4.size = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.dc_id;	 Catch:{ Exception -> 0x0c82 }
        r4.dc_id = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.attributes;	 Catch:{ Exception -> 0x0c82 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x1766;	 Catch:{ Exception -> 0x0c82 }
    L_0x1738:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r4;	 Catch:{ Exception -> 0x0c82 }
        r6 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x0c82 }
        r6.<init>();	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.thumb;	 Catch:{ Exception -> 0x0c82 }
        r6 = "s";	 Catch:{ Exception -> 0x0c82 }
        r4.type = r6;	 Catch:{ Exception -> 0x0c82 }
    L_0x174e:
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r13 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r15 = r12.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r17 = 0;	 Catch:{ Exception -> 0x0c82 }
        r18 = 0;	 Catch:{ Exception -> 0x0c82 }
        r14 = r7;	 Catch:{ Exception -> 0x0c82 }
        r19 = r12;	 Catch:{ Exception -> 0x0c82 }
        r13.performSendEncryptedRequest(r14, r15, r16, r17, r18, r19);	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1399;	 Catch:{ Exception -> 0x0c82 }
    L_0x1766:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument) r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x174e;	 Catch:{ Exception -> 0x0c82 }
    L_0x1771:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r4);	 Catch:{ Exception -> 0x0c82 }
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.media = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.attributes;	 Catch:{ Exception -> 0x0c82 }
        r4.attributes = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r69;	 Catch:{ Exception -> 0x0c82 }
        r4.caption = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1817;	 Catch:{ Exception -> 0x0c82 }
    L_0x1793:
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1817;	 Catch:{ Exception -> 0x0c82 }
    L_0x179b:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.f33h;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.f34w;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0c82 }
    L_0x17bb:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.size;	 Catch:{ Exception -> 0x0c82 }
        r4.size = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.mime_type;	 Catch:{ Exception -> 0x0c82 }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.key;	 Catch:{ Exception -> 0x0c82 }
        if (r4 != 0) goto L_0x182b;	 Catch:{ Exception -> 0x0c82 }
    L_0x17d1:
        r28 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c82 }
        r0 = r28;	 Catch:{ Exception -> 0x0c82 }
        r1 = r67;	 Catch:{ Exception -> 0x0c82 }
        r2 = r76;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0c82 }
        r0 = r51;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x0dfd }
        r4 = 2;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.type = r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.obj = r12;	 Catch:{ Exception -> 0x0dfd }
        r0 = r16;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.encryptedChat = r0;	 Catch:{ Exception -> 0x0dfd }
        if (r78 == 0) goto L_0x180e;	 Catch:{ Exception -> 0x0dfd }
    L_0x17f7:
        r4 = r78.length();	 Catch:{ Exception -> 0x0dfd }
        if (r4 <= 0) goto L_0x180e;	 Catch:{ Exception -> 0x0dfd }
    L_0x17fd:
        r4 = "http";	 Catch:{ Exception -> 0x0dfd }
        r0 = r78;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.startsWith(r4);	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x180e;	 Catch:{ Exception -> 0x0dfd }
    L_0x1808:
        r0 = r78;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.httpLocation = r0;	 Catch:{ Exception -> 0x0dfd }
    L_0x180e:
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x1399;
    L_0x1817:
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0c82 }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x17bb;	 Catch:{ Exception -> 0x0c82 }
    L_0x182b:
        r10 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;	 Catch:{ Exception -> 0x0c82 }
        r10.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.id;	 Catch:{ Exception -> 0x0c82 }
        r10.id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r10.access_hash = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.key;	 Catch:{ Exception -> 0x0c82 }
        r4.key = r6;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.media;	 Catch:{ Exception -> 0x0c82 }
        r0 = r74;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.iv;	 Catch:{ Exception -> 0x0c82 }
        r4.iv = r6;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r6 = org.telegram.messenger.SecretChatHelper.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r8 = r12.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r11 = 0;	 Catch:{ Exception -> 0x0c82 }
        r9 = r16;	 Catch:{ Exception -> 0x0c82 }
        r6.performSendEncryptedRequest(r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x1399;	 Catch:{ Exception -> 0x0c82 }
    L_0x1860:
        r4 = 8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x153d;	 Catch:{ Exception -> 0x0c82 }
    L_0x1866:
        r28 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage;	 Catch:{ Exception -> 0x0c82 }
        r0 = r28;	 Catch:{ Exception -> 0x0c82 }
        r1 = r67;	 Catch:{ Exception -> 0x0c82 }
        r2 = r76;	 Catch:{ Exception -> 0x0c82 }
        r0.<init>(r2);	 Catch:{ Exception -> 0x0c82 }
        r0 = r16;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.encryptedChat = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.sendEncryptedRequest = r7;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.obj = r12;	 Catch:{ Exception -> 0x0dfd }
        r4 = 3;	 Catch:{ Exception -> 0x0dfd }
        r0 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.type = r4;	 Catch:{ Exception -> 0x0dfd }
        r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument;	 Catch:{ Exception -> 0x0dfd }
        r4.<init>();	 Catch:{ Exception -> 0x0dfd }
        r7.media = r4;	 Catch:{ Exception -> 0x0dfd }
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r6 = r0.attributes;	 Catch:{ Exception -> 0x0dfd }
        r4.attributes = r6;	 Catch:{ Exception -> 0x0dfd }
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r0 = r69;	 Catch:{ Exception -> 0x0dfd }
        r4.caption = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x18e6;	 Catch:{ Exception -> 0x0dfd }
    L_0x189f:
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r4 = r0.thumb;	 Catch:{ Exception -> 0x0dfd }
        r4 = r4.bytes;	 Catch:{ Exception -> 0x0dfd }
        if (r4 == 0) goto L_0x18e6;	 Catch:{ Exception -> 0x0dfd }
    L_0x18a7:
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0dfd }
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.bytes;	 Catch:{ Exception -> 0x0dfd }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0dfd }
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.f33h;	 Catch:{ Exception -> 0x0dfd }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0dfd }
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r6 = r0.thumb;	 Catch:{ Exception -> 0x0dfd }
        r6 = r6.f34w;	 Catch:{ Exception -> 0x0dfd }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0dfd }
    L_0x18c7:
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r6 = r0.mime_type;	 Catch:{ Exception -> 0x0dfd }
        r4.mime_type = r6;	 Catch:{ Exception -> 0x0dfd }
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r0 = r74;	 Catch:{ Exception -> 0x0dfd }
        r6 = r0.size;	 Catch:{ Exception -> 0x0dfd }
        r4.size = r6;	 Catch:{ Exception -> 0x0dfd }
        r0 = r51;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.originalPath = r0;	 Catch:{ Exception -> 0x0dfd }
        r0 = r67;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r0.performSendDelayedMessage(r1);	 Catch:{ Exception -> 0x0dfd }
        goto L_0x1399;	 Catch:{ Exception -> 0x0dfd }
    L_0x18e6:
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r4 = (org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument) r4;	 Catch:{ Exception -> 0x0dfd }
        r6 = 0;	 Catch:{ Exception -> 0x0dfd }
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0dfd }
        r4.thumb = r6;	 Catch:{ Exception -> 0x0dfd }
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r6 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4.thumb_h = r6;	 Catch:{ Exception -> 0x0dfd }
        r4 = r7.media;	 Catch:{ Exception -> 0x0dfd }
        r6 = 0;	 Catch:{ Exception -> 0x0dfd }
        r4.thumb_w = r6;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x18c7;	 Catch:{ Exception -> 0x0dfd }
    L_0x18fa:
        r56 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;	 Catch:{ Exception -> 0x0dfd }
        r56.<init>();	 Catch:{ Exception -> 0x0dfd }
        r0 = r56;	 Catch:{ Exception -> 0x0dfd }
        r1 = r28;	 Catch:{ Exception -> 0x0dfd }
        r1.sendEncryptedRequest = r0;	 Catch:{ Exception -> 0x0dfd }
        goto L_0x13ad;
    L_0x1907:
        r8 = 0;
        goto L_0x13de;
    L_0x190b:
        r4 = 4;
        r0 = r63;
        if (r0 != r4) goto L_0x19ef;
    L_0x1910:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;	 Catch:{ Exception -> 0x0c82 }
        r7.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r57;	 Catch:{ Exception -> 0x0c82 }
        r7.to_peer = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r82;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.with_my_score;	 Catch:{ Exception -> 0x0c82 }
        r7.with_my_score = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r82;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.ttl;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x19b5;	 Catch:{ Exception -> 0x0c82 }
    L_0x1929:
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r0 = r82;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x0c82 }
        r6 = -r6;	 Catch:{ Exception -> 0x0c82 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0c82 }
        r26 = r4.getChat(r6);	 Catch:{ Exception -> 0x0c82 }
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.from_peer = r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = r7.from_peer;	 Catch:{ Exception -> 0x0c82 }
        r0 = r82;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.ttl;	 Catch:{ Exception -> 0x0c82 }
        r6 = -r6;	 Catch:{ Exception -> 0x0c82 }
        r4.channel_id = r6;	 Catch:{ Exception -> 0x0c82 }
        if (r26 == 0) goto L_0x195c;	 Catch:{ Exception -> 0x0c82 }
    L_0x1954:
        r4 = r7.from_peer;	 Catch:{ Exception -> 0x0c82 }
        r0 = r26;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.access_hash;	 Catch:{ Exception -> 0x0c82 }
        r4.access_hash = r8;	 Catch:{ Exception -> 0x0c82 }
    L_0x195c:
        r0 = r82;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.to_id;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x198b;	 Catch:{ Exception -> 0x0c82 }
    L_0x1966:
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0c82 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0c82 }
        r6.<init>();	 Catch:{ Exception -> 0x0c82 }
        r8 = "silent_";	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0c82 }
        r0 = r76;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0c82 }
        r8 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0c82 }
        r7.silent = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x198b:
        r4 = r7.random_id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0c82 }
        r6 = java.lang.Long.valueOf(r8);	 Catch:{ Exception -> 0x0c82 }
        r4.add(r6);	 Catch:{ Exception -> 0x0c82 }
        r4 = r82.getId();	 Catch:{ Exception -> 0x0c82 }
        if (r4 < 0) goto L_0x19bd;	 Catch:{ Exception -> 0x0c82 }
    L_0x199e:
        r4 = r7.id;	 Catch:{ Exception -> 0x0c82 }
        r6 = r82.getId();	 Catch:{ Exception -> 0x0c82 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0c82 }
        r4.add(r6);	 Catch:{ Exception -> 0x0c82 }
    L_0x19ab:
        r4 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x0c82 }
        r28 = r29;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x0008;	 Catch:{ Exception -> 0x0c82 }
    L_0x19b5:
        r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;	 Catch:{ Exception -> 0x0c82 }
        r4.<init>();	 Catch:{ Exception -> 0x0c82 }
        r7.from_peer = r4;	 Catch:{ Exception -> 0x0c82 }
        goto L_0x195c;	 Catch:{ Exception -> 0x0c82 }
    L_0x19bd:
        r0 = r82;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.fwd_msg_id;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x19d5;	 Catch:{ Exception -> 0x0c82 }
    L_0x19c5:
        r4 = r7.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r82;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.fwd_msg_id;	 Catch:{ Exception -> 0x0c82 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0c82 }
        r4.add(r6);	 Catch:{ Exception -> 0x0c82 }
        goto L_0x19ab;	 Catch:{ Exception -> 0x0c82 }
    L_0x19d5:
        r0 = r82;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.fwd_from;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x19ab;	 Catch:{ Exception -> 0x0c82 }
    L_0x19dd:
        r4 = r7.id;	 Catch:{ Exception -> 0x0c82 }
        r0 = r82;	 Catch:{ Exception -> 0x0c82 }
        r6 = r0.messageOwner;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.fwd_from;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.channel_post;	 Catch:{ Exception -> 0x0c82 }
        r6 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x0c82 }
        r4.add(r6);	 Catch:{ Exception -> 0x0c82 }
        goto L_0x19ab;	 Catch:{ Exception -> 0x0c82 }
    L_0x19ef:
        r4 = 9;	 Catch:{ Exception -> 0x0c82 }
        r0 = r63;	 Catch:{ Exception -> 0x0c82 }
        if (r0 != r4) goto L_0x1a7e;	 Catch:{ Exception -> 0x0c82 }
    L_0x19f5:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult;	 Catch:{ Exception -> 0x0c82 }
        r7.<init>();	 Catch:{ Exception -> 0x0c82 }
        r0 = r57;	 Catch:{ Exception -> 0x0c82 }
        r7.peer = r0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r8 = r0.random_id;	 Catch:{ Exception -> 0x0c82 }
        r7.random_id = r8;	 Catch:{ Exception -> 0x0c82 }
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1a16;	 Catch:{ Exception -> 0x0c82 }
    L_0x1a0a:
        r4 = r7.flags;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 | 1;	 Catch:{ Exception -> 0x0c82 }
        r7.flags = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.reply_to_msg_id;	 Catch:{ Exception -> 0x0c82 }
        r7.reply_to_msg_id = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x1a16:
        r0 = r46;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.to_id;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerChannel;	 Catch:{ Exception -> 0x0c82 }
        if (r4 == 0) goto L_0x1a43;	 Catch:{ Exception -> 0x0c82 }
    L_0x1a1e:
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4);	 Catch:{ Exception -> 0x0c82 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0c82 }
        r6.<init>();	 Catch:{ Exception -> 0x0c82 }
        r8 = "silent_";	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.append(r8);	 Catch:{ Exception -> 0x0c82 }
        r0 = r76;	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.append(r0);	 Catch:{ Exception -> 0x0c82 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0c82 }
        r8 = 0;	 Catch:{ Exception -> 0x0c82 }
        r4 = r4.getBoolean(r6, r8);	 Catch:{ Exception -> 0x0c82 }
        r7.silent = r4;	 Catch:{ Exception -> 0x0c82 }
    L_0x1a43:
        r4 = "query_id";	 Catch:{ Exception -> 0x0c82 }
        r0 = r85;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0c82 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.Utilities.parseLong(r4);	 Catch:{ Exception -> 0x0c82 }
        r8 = r4.longValue();	 Catch:{ Exception -> 0x0c82 }
        r7.query_id = r8;	 Catch:{ Exception -> 0x0c82 }
        r4 = "id";	 Catch:{ Exception -> 0x0c82 }
        r0 = r85;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.get(r4);	 Catch:{ Exception -> 0x0c82 }
        r4 = (java.lang.String) r4;	 Catch:{ Exception -> 0x0c82 }
        r7.id = r4;	 Catch:{ Exception -> 0x0c82 }
        if (r82 != 0) goto L_0x1a78;	 Catch:{ Exception -> 0x0c82 }
    L_0x1a67:
        r4 = 1;	 Catch:{ Exception -> 0x0c82 }
        r7.clear_draft = r4;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r4 = r0.currentAccount;	 Catch:{ Exception -> 0x0c82 }
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);	 Catch:{ Exception -> 0x0c82 }
        r6 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r76;	 Catch:{ Exception -> 0x0c82 }
        r4.cleanDraft(r0, r6);	 Catch:{ Exception -> 0x0c82 }
    L_0x1a78:
        r4 = 0;	 Catch:{ Exception -> 0x0c82 }
        r0 = r67;	 Catch:{ Exception -> 0x0c82 }
        r0.performSendMessageRequest(r7, r12, r4);	 Catch:{ Exception -> 0x0c82 }
    L_0x1a7e:
        r28 = r29;
        goto L_0x0008;
    L_0x1a82:
        r31 = move-exception;
        r12 = r48;
        r73 = r64;
        goto L_0x021e;
    L_0x1a89:
        r31 = move-exception;
        r12 = r48;
        r46 = r47;
        goto L_0x021e;
    L_0x1a90:
        r28 = r29;
        goto L_0x1615;
    L_0x1a94:
        r28 = r29;
        goto L_0x14b8;
    L_0x1a98:
        r28 = r29;
        goto L_0x0d02;
    L_0x1a9c:
        r28 = r29;
        goto L_0x0eb5;
    L_0x1aa0:
        r28 = r29;
        goto L_0x0de2;
    L_0x1aa4:
        r28 = r29;
        goto L_0x0b13;
    L_0x1aa8:
        r29 = r28;
        goto L_0x0ad1;
    L_0x1aac:
        r46 = r47;
        goto L_0x039e;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, int):void");
    }

    private void performSendDelayedMessage(DelayedMessage message) {
        performSendDelayedMessage(message, -1);
    }

    private void performSendDelayedMessage(DelayedMessage message, int index) {
        String location;
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
                        FileLoader.getInstance(this.currentAccount).loadFile(message.location, "jpg", 0, 0);
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
                            FileLoader.getInstance(this.currentAccount).uploadFile(location, false, false, ConnectionsManager.FileTypeVideo);
                            return;
                        } else {
                            FileLoader.getInstance(this.currentAccount).uploadFile(location, false, false, document.size, ConnectionsManager.FileTypeVideo);
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
                        FileLoader.getInstance(this.currentAccount).uploadFile(location, true, false, ConnectionsManager.FileTypeVideo);
                        return;
                    } else {
                        FileLoader.getInstance(this.currentAccount).uploadFile(location, true, false, document.size, ConnectionsManager.FileTypeVideo);
                        return;
                    }
                }
                putToDelayedMessages(FileLoader.getAttachFileName(document), message);
                FileLoader.getInstance(this.currentAccount).loadFile(document, true, 0);
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
                    instance.uploadFile(location, z, false, ConnectionsManager.FileTypeFile);
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
                    FileLoader.getInstance(this.currentAccount).uploadFile(location, true, false, ConnectionsManager.FileTypeFile);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(document), message);
                FileLoader.getInstance(this.currentAccount).loadFile(document, true, 0);
            }
        } else if (message.type == 3) {
            location = message.obj.messageOwner.attachPath;
            putToDelayedMessages(location, message);
            FileLoader.getInstance(this.currentAccount).uploadFile(location, message.sendRequest == null, true, ConnectionsManager.FileTypeAudio);
        } else if (message.type == 4) {
            boolean add = index < 0;
            if (message.location != null || message.httpLocation != null || message.upload || index >= 0) {
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
                                    FileLoader.getInstance(this.currentAccount).uploadFile(documentLocation, false, false, ConnectionsManager.FileTypeVideo);
                                } else {
                                    FileLoader.getInstance(this.currentAccount).uploadFile(documentLocation, false, false, document.size, ConnectionsManager.FileTypeVideo);
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
                                FileLoader.getInstance(this.currentAccount).uploadFile(documentLocation, true, false, ConnectionsManager.FileTypeVideo);
                            } else {
                                FileLoader.getInstance(this.currentAccount).uploadFile(documentLocation, true, false, document.size, ConnectionsManager.FileTypeVideo);
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
                    location = FileLoader.getDirectory(4) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                    putToDelayedMessages(location, message);
                    message.extraHashMap.put(location, inputMedia);
                    message.extraHashMap.put(messageObject, location);
                    FileLoader.getInstance(this.currentAccount).uploadFile(location, message.sendEncryptedRequest != null, true, 16777216);
                    message.location = null;
                }
                message.upload = false;
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new SendMessagesHelper$$Lambda$8(this, inputMedia, message));
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

    final /* synthetic */ void lambda$uploadMultiMedia$19$SendMessagesHelper(InputMedia inputMedia, DelayedMessage message, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$51(this, response, inputMedia, message));
    }

    final /* synthetic */ void lambda$null$18$SendMessagesHelper(TLObject response, InputMedia inputMedia, DelayedMessage message) {
        InputMedia newInputMedia = null;
        if (response != null) {
            MessageMedia messageMedia = (MessageMedia) response;
            if ((inputMedia instanceof TL_inputMediaUploadedPhoto) && (messageMedia instanceof TL_messageMediaPhoto)) {
                InputMedia inputMediaPhoto = new TL_inputMediaPhoto();
                inputMediaPhoto.id = new TL_inputPhoto();
                inputMediaPhoto.id.id = messageMedia.photo.id;
                inputMediaPhoto.id.access_hash = messageMedia.photo.access_hash;
                newInputMedia = inputMediaPhoto;
            } else if ((inputMedia instanceof TL_inputMediaUploadedDocument) && (messageMedia instanceof TL_messageMediaDocument)) {
                InputMedia inputMediaDocument = new TL_inputMediaDocument();
                inputMediaDocument.id = new TL_inputDocument();
                inputMediaDocument.id.id = messageMedia.document.id;
                inputMediaDocument.id.access_hash = messageMedia.document.access_hash;
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
                        maxDelayedMessage.addDelayedRequest(message.sendRequest, message.messageObjects, message.originalPaths);
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
                performSendMessageRequestMulti((TL_messages_sendMultiMedia) message.sendRequest, message.messageObjects, message.originalPaths);
            } else {
                SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest((TL_messages_sendEncryptedMultiMedia) message.sendEncryptedRequest, message);
            }
            message.sendDelayedRequests();
        } else if (add) {
            putToDelayedMessages(key, message);
        }
    }

    final /* synthetic */ void lambda$null$20$SendMessagesHelper(String path) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, path, Integer.valueOf(this.currentAccount));
    }

    final /* synthetic */ void lambda$stopVideoService$21$SendMessagesHelper(String path) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$50(this, path));
    }

    protected void stopVideoService(String path) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$9(this, path));
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

    private void performSendMessageRequestMulti(TL_messages_sendMultiMedia req, ArrayList<MessageObject> msgObjs, ArrayList<String> originalPaths) {
        for (int a = 0; a < msgObjs.size(); a++) {
            putToSendingMessages(((MessageObject) msgObjs.get(a)).messageOwner);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject) req, new SendMessagesHelper$$Lambda$10(this, msgObjs, originalPaths, req), null, 68);
    }

    final /* synthetic */ void lambda$performSendMessageRequestMulti$28$SendMessagesHelper(ArrayList msgObjs, ArrayList originalPaths, TL_messages_sendMultiMedia req, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$44(this, error, response, msgObjs, originalPaths, req));
    }

    final /* synthetic */ void lambda$null$27$SendMessagesHelper(TL_error error, TLObject response, ArrayList msgObjs, ArrayList originalPaths, TL_messages_sendMultiMedia req) {
        int i;
        Message newMsgObj;
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
                    Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$45(this, newMessage));
                    updatesArr.remove(a);
                    a--;
                } else if (update instanceof TL_updateNewChannelMessage) {
                    TL_updateNewChannelMessage newMessage2 = (TL_updateNewChannelMessage) update;
                    newMessages.put(newMessage2.message.id, newMessage2.message);
                    Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$46(this, newMessage2));
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
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, 1);
                    newMsgObj.send_state = 0;
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), Long.valueOf(grouped_id));
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$47(this, newMsgObj, oldId, sentMessages, grouped_id));
                }
            }
            Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$48(this, updates));
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

    final /* synthetic */ void lambda$null$22$SendMessagesHelper(TL_updateNewMessage newMessage) {
        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
    }

    final /* synthetic */ void lambda$null$23$SendMessagesHelper(TL_updateNewChannelMessage newMessage) {
        MessagesController.getInstance(this.currentAccount).processNewChannelDifferenceParams(newMessage.pts, newMessage.pts_count, newMessage.message.to_id.channel_id);
    }

    final /* synthetic */ void lambda$null$25$SendMessagesHelper(Message newMsgObj, int oldId, ArrayList sentMessages, long grouped_id) {
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(oldId), newMsgObj.id, 0, false, newMsgObj.to_id.channel_id);
        MessagesStorage.getInstance(this.currentAccount).putMessages(sentMessages, true, false, false, 0);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$49(this, newMsgObj, oldId, grouped_id));
    }

    final /* synthetic */ void lambda$null$24$SendMessagesHelper(Message newMsgObj, int oldId, long grouped_id) {
        DataQuery.getInstance(this.currentAccount).increasePeerRaiting(newMsgObj.dialog_id);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id), Long.valueOf(grouped_id));
        processSentMessage(oldId);
        removeFromSendingMessages(oldId);
    }

    final /* synthetic */ void lambda$null$26$SendMessagesHelper(Updates updates) {
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
    }

    private void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath) {
        performSendMessageRequest(req, msgObj, originalPath, null, false);
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

    private void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath, DelayedMessage parentMessage, boolean check) {
        int i;
        if (!(req instanceof TL_messages_editMessage) && check) {
            DelayedMessage maxDelayedMessage = findMaxDelayedMessageForMessageId(msgObj.getId(), msgObj.getDialogId());
            if (maxDelayedMessage != null) {
                maxDelayedMessage.addDelayedRequest(req, msgObj, originalPath);
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
        RequestDelegate sendMessagesHelper$$Lambda$11 = new SendMessagesHelper$$Lambda$11(this, req, newMsgObj, msgObj, originalPath);
        QuickAckDelegate sendMessagesHelper$$Lambda$12 = new SendMessagesHelper$$Lambda$12(this, newMsgObj);
        if (req instanceof TL_messages_sendMessage) {
            i = 128;
        } else {
            i = 0;
        }
        newMsgObj.reqId = instance.sendRequest(req, sendMessagesHelper$$Lambda$11, sendMessagesHelper$$Lambda$12, i | 68);
        if (parentMessage != null) {
            parentMessage.sendDelayedRequests();
        }
    }

    final /* synthetic */ void lambda$performSendMessageRequest$39$SendMessagesHelper(TLObject req, Message newMsgObj, MessageObject msgObj, String originalPath, TLObject response, TL_error error) {
        if (req instanceof TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$34(this, error, newMsgObj, response, msgObj, originalPath, req));
        } else {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$35(this, error, newMsgObj, req, response, msgObj, originalPath));
        }
    }

    final /* synthetic */ void lambda$null$31$SendMessagesHelper(TL_error error, Message newMsgObj, TLObject response, MessageObject msgObj, String originalPath, TLObject req) {
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
            Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$42(this, updates, newMsgObj));
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

    final /* synthetic */ void lambda$null$30$SendMessagesHelper(Updates updates, Message newMsgObj) {
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$43(this, newMsgObj));
    }

    final /* synthetic */ void lambda$null$29$SendMessagesHelper(Message newMsgObj) {
        processSentMessage(newMsgObj.id);
        removeFromSendingMessages(newMsgObj.id);
    }

    final /* synthetic */ void lambda$null$38$SendMessagesHelper(TL_error error, Message newMsgObj, TLObject req, TLObject response, MessageObject msgObj, String originalPath) {
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
                Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$36(this, res));
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
                        Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$37(this, newMessage));
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
                        Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$38(this, newMessage2));
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
                Utilities.stageQueue.postRunnable(new SendMessagesHelper$$Lambda$39(this, updates));
            }
            if (MessageObject.isLiveLocationMessage(newMsgObj)) {
                LocationController.getInstance(this.currentAccount).addSharingLocation(newMsgObj.dialog_id, newMsgObj.id, newMsgObj.media.period, newMsgObj);
            }
            if (!isSentError) {
                StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, 1);
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
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$40(this, newMsgObj, oldId, isBroadcast, sentMessages, attachPath));
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

    final /* synthetic */ void lambda$null$32$SendMessagesHelper(TL_updateShortSentMessage res) {
        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, res.pts, res.date, res.pts_count);
    }

    final /* synthetic */ void lambda$null$33$SendMessagesHelper(TL_updateNewMessage newMessage) {
        MessagesController.getInstance(this.currentAccount).processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
    }

    final /* synthetic */ void lambda$null$34$SendMessagesHelper(TL_updateNewChannelMessage newMessage) {
        MessagesController.getInstance(this.currentAccount).processNewChannelDifferenceParams(newMessage.pts, newMessage.pts_count, newMessage.message.to_id.channel_id);
    }

    final /* synthetic */ void lambda$null$35$SendMessagesHelper(Updates updates) {
        MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
    }

    final /* synthetic */ void lambda$null$37$SendMessagesHelper(Message newMsgObj, int oldId, boolean isBroadcast, ArrayList sentMessages, String attachPath) {
        MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(oldId), isBroadcast ? oldId : newMsgObj.id, 0, false, newMsgObj.to_id.channel_id);
        MessagesStorage.getInstance(this.currentAccount).putMessages(sentMessages, true, false, isBroadcast, 0);
        if (isBroadcast) {
            ArrayList currentMessage = new ArrayList();
            currentMessage.add(newMsgObj);
            MessagesStorage.getInstance(this.currentAccount).putMessages(currentMessage, true, false, false, 0);
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$41(this, isBroadcast, sentMessages, newMsgObj, oldId));
        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
            stopVideoService(attachPath);
        }
    }

    final /* synthetic */ void lambda$null$36$SendMessagesHelper(boolean isBroadcast, ArrayList sentMessages, Message newMsgObj, int oldId) {
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

    final /* synthetic */ void lambda$performSendMessageRequest$41$SendMessagesHelper(Message newMsgObj) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$33(this, newMsgObj, newMsgObj.id));
    }

    final /* synthetic */ void lambda$null$40$SendMessagesHelper(Message newMsgObj, int msg_id) {
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
                    MessagesStorage.getInstance(this.currentAccount).putSentFile(originalPath, sentMessage.media.photo, 0);
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
                                if (size2 == null || size2.location == null || size2.type == null || !((size2.location.volume_id == -2147483648L && size.type.equals(size2.type)) || (size.f34w == size2.f34w && size.f33h == size2.f33h))) {
                                    b++;
                                } else {
                                    fileName = size2.location.volume_id + "_" + size2.location.local_id;
                                    fileName2 = size.location.volume_id + "_" + size.location.local_id;
                                    if (!fileName.equals(fileName2)) {
                                        cacheFile = new File(FileLoader.getDirectory(4), fileName + ".jpg");
                                        if (sentMessage.media.ttl_seconds != 0 || (sentMessage.media.photo.sizes.size() != 1 && size.f34w <= 90 && size.f33h <= 90)) {
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
                if (MessageObject.isVideoMessage(sentMessage)) {
                    if (sentMessage.media.ttl_seconds == 0) {
                        MessagesStorage.getInstance(this.currentAccount).putSentFile(originalPath, sentMessage.media.document, 2);
                    }
                    sentMessage.attachPath = newMsg.attachPath;
                } else if (!(MessageObject.isVoiceMessage(sentMessage) || MessageObject.isRoundVideoMessage(sentMessage) || sentMessage.media.ttl_seconds != 0)) {
                    MessagesStorage.getInstance(this.currentAccount).putSentFile(originalPath, sentMessage.media.document, 1);
                }
                size2 = newMsg.media.document.thumb;
                size = sentMessage.media.document.thumb;
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
                } else if ((size2 != null && (size2.location instanceof TL_fileLocationUnavailable)) || (size2 instanceof TL_photoSizeEmpty)) {
                    newMsg.media.document.thumb = sentMessage.media.document.thumb;
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
                        DataQuery.getInstance(this.currentAccount).addRecentSticker(0, sentMessage.media.document, sentMessage.date, false);
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
                    sentMessage.attachPath = newMsg.attachPath;
                    sentMessage.message = newMsg.message;
                } else if (MessageObject.isVideoMessage(sentMessage)) {
                    newMsgObj.attachPathExists = true;
                } else {
                    newMsgObj.mediaExists = newMsgObj.attachPathExists;
                    newMsgObj.attachPathExists = false;
                    newMsg.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$13(this, users, chats, encryptedChats, messages));
    }

    final /* synthetic */ void lambda$processUnsentMessages$42$SendMessagesHelper(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList messages) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        for (int a = 0; a < messages.size(); a++) {
            retrySendMessage(new MessageObject(this.currentAccount, (Message) messages.get(a), false), true);
        }
    }

    public TL_photo generatePhotoSizes(String path, Uri imageUri) {
        Bitmap bitmap = ImageLoader.loadBitmap(path, imageUri, (float) AndroidUtilities.getPhotoSize(), (float) AndroidUtilities.getPhotoSize(), true);
        if (bitmap == null && AndroidUtilities.getPhotoSize() != 800) {
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
        TL_photo photo = new TL_photo();
        photo.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        photo.sizes = sizes;
        return photo;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean prepareSendingDocumentInternal(int currentAccount, String path, String originalPath, Uri uri, String mime, long dialog_id, MessageObject reply_to_msg, CharSequence caption, ArrayList<MessageEntity> entities, MessageObject editingMessageObject) {
        Throwable e;
        TL_document tL_document;
        TL_documentAttributeFilename fileName;
        Object obj;
        String mimeType;
        Bitmap bitmap;
        Options bmOptions;
        RandomAccessFile randomAccessFile;
        ByteBuffer buffer;
        TL_documentAttributeSticker attributeSticker;
        TL_documentAttributeImageSize attributeImageSize;
        String captionFinal;
        HashMap<String, String> params;
        Throwable th;
        if ((path == null || path.length() == 0) && uri == null) {
            return false;
        }
        if (uri != null && AndroidUtilities.isInternalUri(uri)) {
            return false;
        }
        if (path != null && AndroidUtilities.isInternalUri(Uri.fromFile(new File(path)))) {
            return false;
        }
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        TL_documentAttributeAudio attributeAudio = null;
        String extension = null;
        if (uri != null) {
            boolean hasExt = false;
            if (mime != null) {
                extension = myMime.getExtensionFromMimeType(mime);
            }
            if (extension == null) {
                extension = "txt";
            } else {
                hasExt = true;
            }
            path = MediaController.copyFileToCache(uri, extension);
            if (path == null) {
                return false;
            }
            if (!hasExt) {
                extension = null;
            }
        }
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            return false;
        }
        boolean sendNew;
        boolean isEncrypted = ((int) dialog_id) == 0;
        boolean allowSticker = !isEncrypted;
        String name = file.getName();
        String ext = TtmlNode.ANONYMOUS_REGION_ID;
        if (extension != null) {
            ext = extension;
        } else {
            int idx = path.lastIndexOf(46);
            if (idx != -1) {
                ext = path.substring(idx + 1);
            }
        }
        String extL = ext.toLowerCase();
        String permormer = null;
        String title = null;
        boolean isVoice = false;
        int duration = 0;
        if (!extL.equals("mp3")) {
            if (!extL.equals("m4a")) {
                if (!extL.equals("opus")) {
                    if (!extL.equals("ogg")) {
                    }
                }
                MediaMetadataRetriever mediaMetadataRetriever = null;
                try {
                    MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
                    try {
                        mediaMetadataRetriever2.setDataSource(file.getAbsolutePath());
                        String d = mediaMetadataRetriever2.extractMetadata(9);
                        if (d != null) {
                            duration = (int) Math.ceil((double) (((float) Long.parseLong(d)) / 1000.0f));
                            title = mediaMetadataRetriever2.extractMetadata(7);
                            permormer = mediaMetadataRetriever2.extractMetadata(2);
                        }
                        if (editingMessageObject == null) {
                            if (extL.equals("ogg") && MediaController.isOpusFile(file.getAbsolutePath()) == 1) {
                                isVoice = true;
                            }
                        }
                        if (mediaMetadataRetriever2 != null) {
                            try {
                                mediaMetadataRetriever2.release();
                            } catch (Throwable e2) {
                                FileLog.m8e(e2);
                            }
                        }
                    } catch (Exception e3) {
                        e2 = e3;
                        mediaMetadataRetriever = mediaMetadataRetriever2;
                        try {
                            FileLog.m8e(e2);
                            if (mediaMetadataRetriever != null) {
                                try {
                                    mediaMetadataRetriever.release();
                                } catch (Throwable e22) {
                                    FileLog.m8e(e22);
                                }
                            }
                            if (duration != 0) {
                                attributeAudio = new TL_documentAttributeAudio();
                                attributeAudio.duration = duration;
                                attributeAudio.title = title;
                                attributeAudio.performer = permormer;
                                if (attributeAudio.title == null) {
                                    attributeAudio.title = TtmlNode.ANONYMOUS_REGION_ID;
                                }
                                attributeAudio.flags |= 1;
                                if (attributeAudio.performer == null) {
                                    attributeAudio.performer = TtmlNode.ANONYMOUS_REGION_ID;
                                }
                                attributeAudio.flags |= 2;
                                if (isVoice) {
                                    attributeAudio.voice = true;
                                }
                            }
                            sendNew = false;
                            if (originalPath != null) {
                                if (!originalPath.endsWith("attheme")) {
                                    sendNew = true;
                                } else if (attributeAudio == null) {
                                    originalPath = originalPath + TtmlNode.ANONYMOUS_REGION_ID + file.length();
                                } else {
                                    originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + file.length();
                                }
                            }
                            tL_document = null;
                            tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, isEncrypted ? 4 : 1);
                            tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(path + file.length(), isEncrypted ? 4 : 1);
                            if (tL_document == null) {
                                tL_document = new TL_document();
                                tL_document.id = 0;
                                tL_document.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                                fileName = new TL_documentAttributeFilename();
                                fileName.file_name = name;
                                tL_document.attributes.add(fileName);
                                tL_document.size = (int) file.length();
                                tL_document.dc_id = 0;
                                if (attributeAudio != null) {
                                    tL_document.attributes.add(attributeAudio);
                                }
                                if (ext.length() == 0) {
                                    obj = -1;
                                    switch (extL.hashCode()) {
                                        case 109967:
                                            if (extL.equals("ogg")) {
                                                obj = 2;
                                                break;
                                            }
                                            break;
                                        case 3145576:
                                            if (extL.equals("flac")) {
                                                obj = 3;
                                                break;
                                            }
                                            break;
                                        case 3418175:
                                            if (extL.equals("opus")) {
                                                obj = 1;
                                                break;
                                            }
                                            break;
                                        case 3645340:
                                            if (extL.equals("webp")) {
                                                obj = null;
                                                break;
                                            }
                                            break;
                                    }
                                    switch (obj) {
                                        case null:
                                            tL_document.mime_type = "image/webp";
                                            break;
                                        case 1:
                                            tL_document.mime_type = MimeTypes.AUDIO_OPUS;
                                            break;
                                        case 2:
                                            tL_document.mime_type = "audio/ogg";
                                            break;
                                        case 3:
                                            tL_document.mime_type = MimeTypes.AUDIO_FLAC;
                                            break;
                                        default:
                                            mimeType = myMime.getMimeTypeFromExtension(extL);
                                            if (mimeType == null) {
                                                tL_document.mime_type = mimeType;
                                                break;
                                            }
                                            tL_document.mime_type = "application/octet-stream";
                                            break;
                                    }
                                }
                                tL_document.mime_type = "application/octet-stream";
                                try {
                                    bitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
                                    if (bitmap != null) {
                                        fileName.file_name = "animation.gif";
                                        tL_document.attributes.add(new TL_documentAttributeAnimated());
                                        tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, isEncrypted);
                                        bitmap.recycle();
                                    }
                                } catch (Throwable e222) {
                                    FileLog.m8e(e222);
                                }
                                bmOptions = new Options();
                                try {
                                    bmOptions.inJustDecodeBounds = true;
                                    randomAccessFile = new RandomAccessFile(path, "r");
                                    buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, (long) path.length());
                                    Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                                    randomAccessFile.close();
                                } catch (Throwable e2222) {
                                    FileLog.m8e(e2222);
                                }
                                attributeSticker = new TL_documentAttributeSticker();
                                attributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
                                attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                tL_document.attributes.add(attributeSticker);
                                attributeImageSize = new TL_documentAttributeImageSize();
                                attributeImageSize.w = bmOptions.outWidth;
                                attributeImageSize.h = bmOptions.outHeight;
                                tL_document.attributes.add(attributeImageSize);
                                if (tL_document.thumb == null) {
                                    tL_document.thumb = new TL_photoSizeEmpty();
                                    tL_document.thumb.type = "s";
                                }
                            }
                            captionFinal = caption == null ? TtmlNode.ANONYMOUS_REGION_ID : caption.toString();
                            params = new HashMap();
                            if (originalPath != null) {
                                params.put("originalPath", originalPath);
                            }
                            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$14(editingMessageObject, currentAccount, tL_document, path, params, dialog_id, reply_to_msg, captionFinal, entities));
                            return true;
                        } catch (Throwable th2) {
                            th = th2;
                            if (mediaMetadataRetriever != null) {
                                try {
                                    mediaMetadataRetriever.release();
                                } catch (Throwable e22222) {
                                    FileLog.m8e(e22222);
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        mediaMetadataRetriever = mediaMetadataRetriever2;
                        if (mediaMetadataRetriever != null) {
                            mediaMetadataRetriever.release();
                        }
                        throw th;
                    }
                } catch (Exception e4) {
                    e22222 = e4;
                    FileLog.m8e(e22222);
                    if (mediaMetadataRetriever != null) {
                        mediaMetadataRetriever.release();
                    }
                    if (duration != 0) {
                        attributeAudio = new TL_documentAttributeAudio();
                        attributeAudio.duration = duration;
                        attributeAudio.title = title;
                        attributeAudio.performer = permormer;
                        if (attributeAudio.title == null) {
                            attributeAudio.title = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        attributeAudio.flags |= 1;
                        if (attributeAudio.performer == null) {
                            attributeAudio.performer = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        attributeAudio.flags |= 2;
                        if (isVoice) {
                            attributeAudio.voice = true;
                        }
                    }
                    sendNew = false;
                    if (originalPath != null) {
                        if (!originalPath.endsWith("attheme")) {
                            sendNew = true;
                        } else if (attributeAudio == null) {
                            originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + file.length();
                        } else {
                            originalPath = originalPath + TtmlNode.ANONYMOUS_REGION_ID + file.length();
                        }
                    }
                    tL_document = null;
                    if (isEncrypted) {
                    }
                    tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, isEncrypted ? 4 : 1);
                    if (isEncrypted) {
                    }
                    tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(path + file.length(), isEncrypted ? 4 : 1);
                    if (tL_document == null) {
                        tL_document = new TL_document();
                        tL_document.id = 0;
                        tL_document.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                        fileName = new TL_documentAttributeFilename();
                        fileName.file_name = name;
                        tL_document.attributes.add(fileName);
                        tL_document.size = (int) file.length();
                        tL_document.dc_id = 0;
                        if (attributeAudio != null) {
                            tL_document.attributes.add(attributeAudio);
                        }
                        if (ext.length() == 0) {
                            obj = -1;
                            switch (extL.hashCode()) {
                                case 109967:
                                    if (extL.equals("ogg")) {
                                        obj = 2;
                                        break;
                                    }
                                    break;
                                case 3145576:
                                    if (extL.equals("flac")) {
                                        obj = 3;
                                        break;
                                    }
                                    break;
                                case 3418175:
                                    if (extL.equals("opus")) {
                                        obj = 1;
                                        break;
                                    }
                                    break;
                                case 3645340:
                                    if (extL.equals("webp")) {
                                        obj = null;
                                        break;
                                    }
                                    break;
                            }
                            switch (obj) {
                                case null:
                                    tL_document.mime_type = "image/webp";
                                    break;
                                case 1:
                                    tL_document.mime_type = MimeTypes.AUDIO_OPUS;
                                    break;
                                case 2:
                                    tL_document.mime_type = "audio/ogg";
                                    break;
                                case 3:
                                    tL_document.mime_type = MimeTypes.AUDIO_FLAC;
                                    break;
                                default:
                                    mimeType = myMime.getMimeTypeFromExtension(extL);
                                    if (mimeType == null) {
                                        tL_document.mime_type = "application/octet-stream";
                                        break;
                                    }
                                    tL_document.mime_type = mimeType;
                                    break;
                            }
                        }
                        tL_document.mime_type = "application/octet-stream";
                        bitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
                        if (bitmap != null) {
                            fileName.file_name = "animation.gif";
                            tL_document.attributes.add(new TL_documentAttributeAnimated());
                            tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, isEncrypted);
                            bitmap.recycle();
                        }
                        bmOptions = new Options();
                        bmOptions.inJustDecodeBounds = true;
                        randomAccessFile = new RandomAccessFile(path, "r");
                        buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, (long) path.length());
                        Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                        randomAccessFile.close();
                        attributeSticker = new TL_documentAttributeSticker();
                        attributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
                        attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                        tL_document.attributes.add(attributeSticker);
                        attributeImageSize = new TL_documentAttributeImageSize();
                        attributeImageSize.w = bmOptions.outWidth;
                        attributeImageSize.h = bmOptions.outHeight;
                        tL_document.attributes.add(attributeImageSize);
                        if (tL_document.thumb == null) {
                            tL_document.thumb = new TL_photoSizeEmpty();
                            tL_document.thumb.type = "s";
                        }
                    }
                    if (caption == null) {
                    }
                    params = new HashMap();
                    if (originalPath != null) {
                        params.put("originalPath", originalPath);
                    }
                    AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$14(editingMessageObject, currentAccount, tL_document, path, params, dialog_id, reply_to_msg, captionFinal, entities));
                    return true;
                }
                if (duration != 0) {
                    attributeAudio = new TL_documentAttributeAudio();
                    attributeAudio.duration = duration;
                    attributeAudio.title = title;
                    attributeAudio.performer = permormer;
                    if (attributeAudio.title == null) {
                        attributeAudio.title = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    attributeAudio.flags |= 1;
                    if (attributeAudio.performer == null) {
                        attributeAudio.performer = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    attributeAudio.flags |= 2;
                    if (isVoice) {
                        attributeAudio.voice = true;
                    }
                }
                sendNew = false;
                if (originalPath != null) {
                    if (!originalPath.endsWith("attheme")) {
                        sendNew = true;
                    } else if (attributeAudio == null) {
                        originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + file.length();
                    } else {
                        originalPath = originalPath + TtmlNode.ANONYMOUS_REGION_ID + file.length();
                    }
                }
                tL_document = null;
                if (!(sendNew || isEncrypted)) {
                    if (isEncrypted) {
                    }
                    tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, isEncrypted ? 4 : 1);
                    if (!(tL_document != null || path.equals(originalPath) || isEncrypted)) {
                        if (isEncrypted) {
                        }
                        tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(path + file.length(), isEncrypted ? 4 : 1);
                    }
                }
                if (tL_document == null) {
                    tL_document = new TL_document();
                    tL_document.id = 0;
                    tL_document.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                    fileName = new TL_documentAttributeFilename();
                    fileName.file_name = name;
                    tL_document.attributes.add(fileName);
                    tL_document.size = (int) file.length();
                    tL_document.dc_id = 0;
                    if (attributeAudio != null) {
                        tL_document.attributes.add(attributeAudio);
                    }
                    if (ext.length() == 0) {
                        obj = -1;
                        switch (extL.hashCode()) {
                            case 109967:
                                if (extL.equals("ogg")) {
                                    obj = 2;
                                    break;
                                }
                                break;
                            case 3145576:
                                if (extL.equals("flac")) {
                                    obj = 3;
                                    break;
                                }
                                break;
                            case 3418175:
                                if (extL.equals("opus")) {
                                    obj = 1;
                                    break;
                                }
                                break;
                            case 3645340:
                                if (extL.equals("webp")) {
                                    obj = null;
                                    break;
                                }
                                break;
                        }
                        switch (obj) {
                            case null:
                                tL_document.mime_type = "image/webp";
                                break;
                            case 1:
                                tL_document.mime_type = MimeTypes.AUDIO_OPUS;
                                break;
                            case 2:
                                tL_document.mime_type = "audio/ogg";
                                break;
                            case 3:
                                tL_document.mime_type = MimeTypes.AUDIO_FLAC;
                                break;
                            default:
                                mimeType = myMime.getMimeTypeFromExtension(extL);
                                if (mimeType == null) {
                                    tL_document.mime_type = "application/octet-stream";
                                    break;
                                }
                                tL_document.mime_type = mimeType;
                                break;
                        }
                    }
                    tL_document.mime_type = "application/octet-stream";
                    if (tL_document.mime_type.equals("image/gif") && (editingMessageObject == null || editingMessageObject.getGroupIdForUse() == 0)) {
                        bitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
                        if (bitmap != null) {
                            fileName.file_name = "animation.gif";
                            tL_document.attributes.add(new TL_documentAttributeAnimated());
                            tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, isEncrypted);
                            bitmap.recycle();
                        }
                    }
                    if (tL_document.mime_type.equals("image/webp") && allowSticker && editingMessageObject == null) {
                        bmOptions = new Options();
                        bmOptions.inJustDecodeBounds = true;
                        randomAccessFile = new RandomAccessFile(path, "r");
                        buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, (long) path.length());
                        Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                        randomAccessFile.close();
                        if (bmOptions.outWidth != 0 && bmOptions.outHeight != 0 && bmOptions.outWidth <= 800 && bmOptions.outHeight <= 800) {
                            attributeSticker = new TL_documentAttributeSticker();
                            attributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
                            attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                            tL_document.attributes.add(attributeSticker);
                            attributeImageSize = new TL_documentAttributeImageSize();
                            attributeImageSize.w = bmOptions.outWidth;
                            attributeImageSize.h = bmOptions.outHeight;
                            tL_document.attributes.add(attributeImageSize);
                        }
                    }
                    if (tL_document.thumb == null) {
                        tL_document.thumb = new TL_photoSizeEmpty();
                        tL_document.thumb.type = "s";
                    }
                }
                if (caption == null) {
                }
                params = new HashMap();
                if (originalPath != null) {
                    params.put("originalPath", originalPath);
                }
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$14(editingMessageObject, currentAccount, tL_document, path, params, dialog_id, reply_to_msg, captionFinal, entities));
                return true;
            }
        }
        AudioInfo audioInfo = AudioInfo.getAudioInfo(file);
        if (!(audioInfo == null || audioInfo.getDuration() == 0)) {
            permormer = audioInfo.getArtist();
            title = audioInfo.getTitle();
        }
        if (duration != 0) {
            attributeAudio = new TL_documentAttributeAudio();
            attributeAudio.duration = duration;
            attributeAudio.title = title;
            attributeAudio.performer = permormer;
            if (attributeAudio.title == null) {
                attributeAudio.title = TtmlNode.ANONYMOUS_REGION_ID;
            }
            attributeAudio.flags |= 1;
            if (attributeAudio.performer == null) {
                attributeAudio.performer = TtmlNode.ANONYMOUS_REGION_ID;
            }
            attributeAudio.flags |= 2;
            if (isVoice) {
                attributeAudio.voice = true;
            }
        }
        sendNew = false;
        if (originalPath != null) {
            if (!originalPath.endsWith("attheme")) {
                sendNew = true;
            } else if (attributeAudio == null) {
                originalPath = originalPath + TtmlNode.ANONYMOUS_REGION_ID + file.length();
            } else {
                originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + file.length();
            }
        }
        tL_document = null;
        if (isEncrypted) {
        }
        tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, isEncrypted ? 4 : 1);
        if (isEncrypted) {
        }
        tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(path + file.length(), isEncrypted ? 4 : 1);
        if (tL_document == null) {
            tL_document = new TL_document();
            tL_document.id = 0;
            tL_document.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            fileName = new TL_documentAttributeFilename();
            fileName.file_name = name;
            tL_document.attributes.add(fileName);
            tL_document.size = (int) file.length();
            tL_document.dc_id = 0;
            if (attributeAudio != null) {
                tL_document.attributes.add(attributeAudio);
            }
            if (ext.length() == 0) {
                obj = -1;
                switch (extL.hashCode()) {
                    case 109967:
                        if (extL.equals("ogg")) {
                            obj = 2;
                            break;
                        }
                        break;
                    case 3145576:
                        if (extL.equals("flac")) {
                            obj = 3;
                            break;
                        }
                        break;
                    case 3418175:
                        if (extL.equals("opus")) {
                            obj = 1;
                            break;
                        }
                        break;
                    case 3645340:
                        if (extL.equals("webp")) {
                            obj = null;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case null:
                        tL_document.mime_type = "image/webp";
                        break;
                    case 1:
                        tL_document.mime_type = MimeTypes.AUDIO_OPUS;
                        break;
                    case 2:
                        tL_document.mime_type = "audio/ogg";
                        break;
                    case 3:
                        tL_document.mime_type = MimeTypes.AUDIO_FLAC;
                        break;
                    default:
                        mimeType = myMime.getMimeTypeFromExtension(extL);
                        if (mimeType == null) {
                            tL_document.mime_type = mimeType;
                            break;
                        }
                        tL_document.mime_type = "application/octet-stream";
                        break;
                }
            }
            tL_document.mime_type = "application/octet-stream";
            bitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
            if (bitmap != null) {
                fileName.file_name = "animation.gif";
                tL_document.attributes.add(new TL_documentAttributeAnimated());
                tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, isEncrypted);
                bitmap.recycle();
            }
            bmOptions = new Options();
            bmOptions.inJustDecodeBounds = true;
            randomAccessFile = new RandomAccessFile(path, "r");
            buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, (long) path.length());
            Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
            randomAccessFile.close();
            attributeSticker = new TL_documentAttributeSticker();
            attributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
            attributeSticker.stickerset = new TL_inputStickerSetEmpty();
            tL_document.attributes.add(attributeSticker);
            attributeImageSize = new TL_documentAttributeImageSize();
            attributeImageSize.w = bmOptions.outWidth;
            attributeImageSize.h = bmOptions.outHeight;
            tL_document.attributes.add(attributeImageSize);
            if (tL_document.thumb == null) {
                tL_document.thumb = new TL_photoSizeEmpty();
                tL_document.thumb.type = "s";
            }
        }
        if (caption == null) {
        }
        params = new HashMap();
        if (originalPath != null) {
            params.put("originalPath", originalPath);
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$14(editingMessageObject, currentAccount, tL_document, path, params, dialog_id, reply_to_msg, captionFinal, entities));
        return true;
    }

    static final /* synthetic */ void lambda$prepareSendingDocumentInternal$43$SendMessagesHelper(MessageObject editingMessageObject, int currentAccount, TL_document documentFinal, String pathFinal, HashMap params, long dialog_id, MessageObject reply_to_msg, String captionFinal, ArrayList entities) {
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, null, documentFinal, pathFinal, params, false);
        } else {
            getInstance(currentAccount).sendMessage(documentFinal, null, pathFinal, dialog_id, reply_to_msg, captionFinal, entities, null, params, 0);
        }
    }

    public static void prepareSendingDocument(String path, String originalPath, Uri uri, String mine, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, MessageObject editingMessageObject) {
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
            prepareSendingDocuments(paths, originalPaths, uris, mine, dialog_id, reply_to_msg, inputContent, editingMessageObject);
        }
    }

    public static void prepareSendingAudioDocuments(ArrayList<MessageObject> messageObjects, long dialog_id, MessageObject reply_to_msg, MessageObject editingMessageObject) {
        new Thread(new SendMessagesHelper$$Lambda$15(messageObjects, dialog_id, UserConfig.selectedAccount, editingMessageObject, reply_to_msg)).start();
    }

    static final /* synthetic */ void lambda$prepareSendingAudioDocuments$45$SendMessagesHelper(ArrayList messageObjects, long dialog_id, int currentAccount, MessageObject editingMessageObject, MessageObject reply_to_msg) {
        int size = messageObjects.size();
        for (int a = 0; a < size; a++) {
            MessageObject messageObject = (MessageObject) messageObjects.get(a);
            String originalPath = messageObject.messageOwner.attachPath;
            File f = new File(originalPath);
            boolean isEncrypted = ((int) dialog_id) == 0;
            if (originalPath != null) {
                originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + f.length();
            }
            TL_document tL_document = null;
            if (!isEncrypted) {
                tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, !isEncrypted ? 1 : 4);
            }
            if (tL_document == null) {
                tL_document = messageObject.messageOwner.media.document;
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
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$32(editingMessageObject, currentAccount, tL_document, messageObject, params, dialog_id, reply_to_msg));
        }
    }

    static final /* synthetic */ void lambda$null$44$SendMessagesHelper(MessageObject editingMessageObject, int currentAccount, TL_document documentFinal, MessageObject messageObject, HashMap params, long dialog_id, MessageObject reply_to_msg) {
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, null, documentFinal, messageObject.messageOwner.attachPath, params, false);
            return;
        }
        getInstance(currentAccount).sendMessage(documentFinal, null, messageObject.messageOwner.attachPath, dialog_id, reply_to_msg, null, null, null, params, 0);
    }

    public static void prepareSendingDocuments(ArrayList<String> paths, ArrayList<String> originalPaths, ArrayList<Uri> uris, String mime, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, MessageObject editingMessageObject) {
        if (paths != null || originalPaths != null || uris != null) {
            if (paths == null || originalPaths == null || paths.size() == originalPaths.size()) {
                new Thread(new SendMessagesHelper$$Lambda$16(paths, UserConfig.selectedAccount, originalPaths, mime, dialog_id, reply_to_msg, editingMessageObject, uris, inputContent)).start();
            }
        }
    }

    static final /* synthetic */ void lambda$prepareSendingDocuments$47$SendMessagesHelper(ArrayList paths, int currentAccount, ArrayList originalPaths, String mime, long dialog_id, MessageObject reply_to_msg, MessageObject editingMessageObject, ArrayList uris, InputContentInfoCompat inputContent) {
        int a;
        boolean error = false;
        if (paths != null) {
            for (a = 0; a < paths.size(); a++) {
                if (!prepareSendingDocumentInternal(currentAccount, (String) paths.get(a), (String) originalPaths.get(a), null, mime, dialog_id, reply_to_msg, null, null, editingMessageObject)) {
                    error = true;
                }
            }
        }
        if (uris != null) {
            for (a = 0; a < uris.size(); a++) {
                if (!prepareSendingDocumentInternal(currentAccount, null, null, (Uri) uris.get(a), mime, dialog_id, reply_to_msg, null, null, editingMessageObject)) {
                    error = true;
                }
            }
        }
        if (inputContent != null) {
            inputContent.releasePermission();
        }
        if (error) {
            AndroidUtilities.runOnUIThread(SendMessagesHelper$$Lambda$31.$instance);
        }
    }

    static final /* synthetic */ void lambda$null$46$SendMessagesHelper() {
        try {
            Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", C0431R.string.UnsupportedAttachment), 0).show();
        } catch (Throwable e) {
            FileLog.m8e(e);
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
                new Thread(new SendMessagesHelper$$Lambda$17(result, dialog_id, currentAccount, params, reply_to_msg)).run();
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
                    venue.venue_type = TtmlNode.ANONYMOUS_REGION_ID;
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
                getInstance(currentAccount).sendMessage(user, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            }
        }
    }

    static final /* synthetic */ void lambda$prepareSendingBotContextResult$49$SendMessagesHelper(BotInlineResult result, long dialog_id, int currentAccount, HashMap params, MessageObject reply_to_msg) {
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
                        if (str.equals(MimeTypes.BASE_TYPE_AUDIO)) {
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
                        if (str.equals(MimeTypes.BASE_TYPE_VIDEO)) {
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
                        document = new TL_document();
                        document.id = 0;
                        document.size = 0;
                        document.dc_id = 0;
                        document.mime_type = result.content.mime_type;
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
                                if (str.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                                    obj = 2;
                                    break;
                                }
                                break;
                            case 112202875:
                                if (str.equals(MimeTypes.BASE_TYPE_VIDEO)) {
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
                                    document.mime_type = MimeTypes.VIDEO_MP4;
                                    document.attributes.add(new TL_documentAttributeAnimated());
                                } else {
                                    document.mime_type = "image/gif";
                                }
                                try {
                                    if (finalPath.endsWith("mp4")) {
                                        bitmap = ThumbnailUtils.createVideoThumbnail(finalPath, 1);
                                    } else {
                                        bitmap = ImageLoader.loadBitmap(finalPath, null, 90.0f, 90.0f, true);
                                    }
                                    if (bitmap != null) {
                                        document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                        bitmap.recycle();
                                        break;
                                    }
                                } catch (Throwable e) {
                                    FileLog.m8e(e);
                                    break;
                                }
                                break;
                            case 1:
                                audio = new TL_documentAttributeAudio();
                                audio.duration = MessageObject.getInlineResultDuration(result);
                                audio.voice = true;
                                fileName.file_name = "audio.ogg";
                                document.attributes.add(audio);
                                document.thumb = new TL_photoSizeEmpty();
                                document.thumb.type = "s";
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
                                document.thumb = new TL_photoSizeEmpty();
                                document.thumb.type = "s";
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
                                            document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                            bitmap.recycle();
                                            break;
                                        }
                                    }
                                } catch (Throwable e2) {
                                    FileLog.m8e(e2);
                                    break;
                                }
                                break;
                            case 5:
                                TL_documentAttributeSticker attributeSticker = new TL_documentAttributeSticker();
                                attributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
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
                                            document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                            bitmap.recycle();
                                            break;
                                        }
                                    }
                                } catch (Throwable e22) {
                                    FileLog.m8e(e22);
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
                        if (document.thumb == null) {
                            document.thumb = new TL_photoSize();
                            wh = MessageObject.getInlineResultWidthAndHeight(result);
                            document.thumb.f34w = wh[0];
                            document.thumb.f33h = wh[1];
                            document.thumb.size = 0;
                            document.thumb.location = new TL_fileLocationUnavailable();
                            document.thumb.type = "x";
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
                    default:
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$30(finalDocument, currentAccount, finalPathFinal, dialog_id, reply_to_msg, result, params, finalPhoto, finalGame));
    }

    static final /* synthetic */ void lambda$null$48$SendMessagesHelper(TL_document finalDocument, int currentAccount, String finalPathFinal, long dialog_id, MessageObject reply_to_msg, BotInlineResult result, HashMap params, TL_photo finalPhoto, TL_game finalGame) {
        if (finalDocument != null) {
            getInstance(currentAccount).sendMessage(finalDocument, null, finalPathFinal, dialog_id, reply_to_msg, result.send_message.message, result.send_message.entities, result.send_message.reply_markup, params, 0);
        } else if (finalPhoto != null) {
            getInstance(currentAccount).sendMessage(finalPhoto, result.content != null ? result.content.url : null, dialog_id, reply_to_msg, result.send_message.message, result.send_message.entities, result.send_message.reply_markup, params, 0);
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
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new SendMessagesHelper$$Lambda$18(text, currentAccount, dialog_id));
    }

    static final /* synthetic */ void lambda$null$50$SendMessagesHelper(String text, int currentAccount, long dialog_id) {
        String textFinal = getTrimmedString(text);
        if (textFinal.length() != 0) {
            int count = (int) Math.ceil((double) (((float) textFinal.length()) / 4096.0f));
            for (int a = 0; a < count; a++) {
                getInstance(currentAccount).sendMessage(textFinal.substring(a * 4096, Math.min((a + 1) * 4096, textFinal.length())), dialog_id, null, null, true, null, null, null);
            }
        }
    }

    public static void prepareSendingMedia(ArrayList<SendingMediaInfo> media, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, boolean forceDocument, boolean groupPhotos, MessageObject editingMessageObject) {
        if (!media.isEmpty()) {
            mediaSendQueue.postRunnable(new SendMessagesHelper$$Lambda$19(media, dialog_id, UserConfig.selectedAccount, forceDocument, groupPhotos, editingMessageObject, reply_to_msg, inputContent));
        }
    }

    static final /* synthetic */ void lambda$prepareSendingMedia$59$SendMessagesHelper(java.util.ArrayList r87, long r88, int r90, boolean r91, boolean r92, org.telegram.messenger.MessageObject r93, org.telegram.messenger.MessageObject r94, android.support.v13.view.inputmethod.InputContentInfoCompat r95) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r83_3 'thumbFile' java.io.File) in PHI: PHI: (r83_5 'thumbFile' java.io.File) = (r83_2 'thumbFile' java.io.File), (r83_3 'thumbFile' java.io.File), (r83_4 'thumbFile' java.io.File) binds: {(r83_2 'thumbFile' java.io.File)=B:94:0x0215, (r83_3 'thumbFile' java.io.File)=B:96:0x0254, (r83_4 'thumbFile' java.io.File)=B:97:0x0256}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r42 = java.lang.System.currentTimeMillis();
        r46 = r87.size();
        r0 = r88;
        r4 = (int) r0;
        if (r4 != 0) goto L_0x008b;
    L_0x000d:
        r58 = 1;
    L_0x000f:
        r51 = 0;
        if (r58 == 0) goto L_0x0030;
    L_0x0013:
        r4 = 32;
        r4 = r88 >> r4;
        r0 = (int) r4;
        r56 = r0;
        r4 = org.telegram.messenger.MessagesController.getInstance(r90);
        r5 = java.lang.Integer.valueOf(r56);
        r50 = r4.getEncryptedChat(r5);
        if (r50 == 0) goto L_0x0030;
    L_0x0028:
        r0 = r50;
        r4 = r0.layer;
        r51 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4);
    L_0x0030:
        if (r58 == 0) goto L_0x0038;
    L_0x0032:
        r4 = 73;
        r0 = r51;
        if (r0 < r4) goto L_0x013b;
    L_0x0038:
        if (r91 != 0) goto L_0x013b;
    L_0x003a:
        if (r92 == 0) goto L_0x013b;
    L_0x003c:
        r86 = new java.util.HashMap;
        r86.<init>();
        r14 = 0;
    L_0x0042:
        r0 = r46;
        if (r14 >= r0) goto L_0x013d;
    L_0x0046:
        r0 = r87;
        r13 = r0.get(r14);
        r13 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r13;
        r4 = r13.searchImage;
        if (r4 != 0) goto L_0x0088;
    L_0x0052:
        r4 = r13.isVideo;
        if (r4 != 0) goto L_0x0088;
    L_0x0056:
        r0 = r13.path;
        r66 = r0;
        r0 = r13.path;
        r81 = r0;
        if (r81 != 0) goto L_0x0070;
    L_0x0060:
        r4 = r13.uri;
        if (r4 == 0) goto L_0x0070;
    L_0x0064:
        r4 = r13.uri;
        r81 = org.telegram.messenger.AndroidUtilities.getPath(r4);
        r4 = r13.uri;
        r66 = r4.toString();
    L_0x0070:
        if (r81 == 0) goto L_0x008e;
    L_0x0072:
        r4 = ".gif";
        r0 = r81;
        r4 = r0.endsWith(r4);
        if (r4 != 0) goto L_0x0088;
    L_0x007d:
        r4 = ".webp";
        r0 = r81;
        r4 = r0.endsWith(r4);
        if (r4 == 0) goto L_0x008e;
    L_0x0088:
        r14 = r14 + 1;
        goto L_0x0042;
    L_0x008b:
        r58 = 0;
        goto L_0x000f;
    L_0x008e:
        if (r81 != 0) goto L_0x00a4;
    L_0x0090:
        r4 = r13.uri;
        if (r4 == 0) goto L_0x00a4;
    L_0x0094:
        r4 = r13.uri;
        r4 = org.telegram.messenger.MediaController.isGif(r4);
        if (r4 != 0) goto L_0x0088;
    L_0x009c:
        r4 = r13.uri;
        r4 = org.telegram.messenger.MediaController.isWebp(r4);
        if (r4 != 0) goto L_0x0088;
    L_0x00a4:
        if (r81 == 0) goto L_0x011a;
    L_0x00a6:
        r80 = new java.io.File;
        r80.<init>(r81);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r66;
        r4 = r4.append(r0);
        r10 = r80.length();
        r4 = r4.append(r10);
        r5 = "_";
        r4 = r4.append(r5);
        r10 = r80.lastModified();
        r4 = r4.append(r10);
        r66 = r4.toString();
    L_0x00d1:
        r69 = 0;
        if (r58 != 0) goto L_0x0101;
    L_0x00d5:
        r4 = r13.ttl;
        if (r4 != 0) goto L_0x0101;
    L_0x00d9:
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r90);
        if (r58 != 0) goto L_0x011d;
    L_0x00df:
        r4 = 0;
    L_0x00e0:
        r0 = r66;
        r69 = r5.getSentFile(r0, r4);
        r69 = (org.telegram.tgnet.TLRPC.TL_photo) r69;
        if (r69 != 0) goto L_0x0101;
    L_0x00ea:
        r4 = r13.uri;
        if (r4 == 0) goto L_0x0101;
    L_0x00ee:
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r90);
        r4 = r13.uri;
        r6 = org.telegram.messenger.AndroidUtilities.getPath(r4);
        if (r58 != 0) goto L_0x011f;
    L_0x00fa:
        r4 = 0;
    L_0x00fb:
        r69 = r5.getSentFile(r6, r4);
        r69 = (org.telegram.tgnet.TLRPC.TL_photo) r69;
    L_0x0101:
        r85 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker;
        r4 = 0;
        r0 = r85;
        r0.<init>();
        r0 = r86;
        r1 = r85;
        r0.put(r13, r1);
        if (r69 == 0) goto L_0x0121;
    L_0x0112:
        r0 = r69;
        r1 = r85;
        r1.photo = r0;
        goto L_0x0088;
    L_0x011a:
        r66 = 0;
        goto L_0x00d1;
    L_0x011d:
        r4 = 3;
        goto L_0x00e0;
    L_0x011f:
        r4 = 3;
        goto L_0x00fb;
    L_0x0121:
        r4 = new java.util.concurrent.CountDownLatch;
        r5 = 1;
        r4.<init>(r5);
        r0 = r85;
        r0.sync = r4;
        r4 = mediaSendThreadPool;
        r5 = new org.telegram.messenger.SendMessagesHelper$$Lambda$22;
        r0 = r85;
        r1 = r90;
        r5.<init>(r0, r1, r13);
        r4.execute(r5);
        goto L_0x0088;
    L_0x013b:
        r86 = 0;
    L_0x013d:
        r54 = 0;
        r60 = 0;
        r72 = 0;
        r75 = 0;
        r73 = 0;
        r74 = 0;
        r52 = 0;
        r71 = 0;
        r14 = 0;
    L_0x014e:
        r0 = r46;
        if (r14 >= r0) goto L_0x0a57;
    L_0x0152:
        r0 = r87;
        r13 = r0.get(r14);
        r13 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r13;
        if (r92 == 0) goto L_0x0177;
    L_0x015c:
        if (r58 == 0) goto L_0x0164;
    L_0x015e:
        r4 = 73;
        r0 = r51;
        if (r0 < r4) goto L_0x0177;
    L_0x0164:
        r4 = 1;
        r0 = r46;
        if (r0 <= r4) goto L_0x0177;
    L_0x0169:
        r4 = r71 % 10;
        if (r4 != 0) goto L_0x0177;
    L_0x016d:
        r4 = org.telegram.messenger.Utilities.random;
        r54 = r4.nextLong();
        r60 = r54;
        r71 = 0;
    L_0x0177:
        r4 = r13.searchImage;
        if (r4 == 0) goto L_0x0501;
    L_0x017b:
        r4 = r13.searchImage;
        r4 = r4.type;
        r5 = 1;
        if (r4 != r5) goto L_0x037c;
    L_0x0182:
        r9 = new java.util.HashMap;
        r9.<init>();
        r48 = 0;
        r4 = r13.searchImage;
        r4 = r4.document;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r4 == 0) goto L_0x02f9;
    L_0x0191:
        r4 = r13.searchImage;
        r0 = r4.document;
        r48 = r0;
        r48 = (org.telegram.tgnet.TLRPC.TL_document) r48;
        r4 = 1;
        r0 = r48;
        r45 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r4);
    L_0x01a0:
        if (r48 != 0) goto L_0x02c5;
    L_0x01a2:
        r4 = r13.searchImage;
        r4 = r4.localUrl;
        if (r4 == 0) goto L_0x01b2;
    L_0x01a8:
        r4 = "url";
        r5 = r13.searchImage;
        r5 = r5.localUrl;
        r9.put(r4, r5);
    L_0x01b2:
        r83 = 0;
        r48 = new org.telegram.tgnet.TLRPC$TL_document;
        r48.<init>();
        r4 = 0;
        r0 = r48;
        r0.id = r4;
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r90);
        r4 = r4.getCurrentTime();
        r0 = r48;
        r0.date = r4;
        r53 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;
        r53.<init>();
        r4 = "animation.gif";
        r0 = r53;
        r0.file_name = r4;
        r0 = r48;
        r4 = r0.attributes;
        r0 = r53;
        r4.add(r0);
        r4 = r13.searchImage;
        r4 = r4.size;
        r0 = r48;
        r0.size = r4;
        r4 = 0;
        r0 = r48;
        r0.dc_id = r4;
        r4 = r45.toString();
        r5 = "mp4";
        r4 = r4.endsWith(r5);
        if (r4 == 0) goto L_0x0353;
    L_0x01fa:
        r4 = "video/mp4";
        r0 = r48;
        r0.mime_type = r4;
        r0 = r48;
        r4 = r0.attributes;
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r5.<init>();
        r4.add(r5);
    L_0x020d:
        r4 = r45.exists();
        if (r4 == 0) goto L_0x035c;
    L_0x0213:
        r83 = r45;
    L_0x0215:
        if (r83 != 0) goto L_0x0258;
    L_0x0217:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = r13.searchImage;
        r5 = r5.thumbUrl;
        r5 = org.telegram.messenger.Utilities.MD5(r5);
        r4 = r4.append(r5);
        r5 = ".";
        r4 = r4.append(r5);
        r5 = r13.searchImage;
        r5 = r5.thumbUrl;
        r6 = "jpg";
        r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r6);
        r4 = r4.append(r5);
        r82 = r4.toString();
        r83 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r83;
        r1 = r82;
        r0.<init>(r4, r1);
        r4 = r83.exists();
        if (r4 != 0) goto L_0x0258;
    L_0x0256:
        r83 = 0;
    L_0x0258:
        if (r83 == 0) goto L_0x0287;
    L_0x025a:
        r4 = r83.getAbsolutePath();	 Catch:{ Exception -> 0x0370 }
        r5 = "mp4";	 Catch:{ Exception -> 0x0370 }
        r4 = r4.endsWith(r5);	 Catch:{ Exception -> 0x0370 }
        if (r4 == 0) goto L_0x0360;	 Catch:{ Exception -> 0x0370 }
    L_0x0267:
        r4 = r83.getAbsolutePath();	 Catch:{ Exception -> 0x0370 }
        r5 = 1;	 Catch:{ Exception -> 0x0370 }
        r44 = android.media.ThumbnailUtils.createVideoThumbnail(r4, r5);	 Catch:{ Exception -> 0x0370 }
    L_0x0270:
        if (r44 == 0) goto L_0x0287;	 Catch:{ Exception -> 0x0370 }
    L_0x0272:
        r4 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;	 Catch:{ Exception -> 0x0370 }
        r5 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;	 Catch:{ Exception -> 0x0370 }
        r6 = 55;	 Catch:{ Exception -> 0x0370 }
        r0 = r44;	 Catch:{ Exception -> 0x0370 }
        r1 = r58;	 Catch:{ Exception -> 0x0370 }
        r4 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r4, r5, r6, r1);	 Catch:{ Exception -> 0x0370 }
        r0 = r48;	 Catch:{ Exception -> 0x0370 }
        r0.thumb = r4;	 Catch:{ Exception -> 0x0370 }
        r44.recycle();	 Catch:{ Exception -> 0x0370 }
    L_0x0287:
        r0 = r48;
        r4 = r0.thumb;
        if (r4 != 0) goto L_0x02c5;
    L_0x028d:
        r4 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r4.<init>();
        r0 = r48;
        r0.thumb = r4;
        r0 = r48;
        r4 = r0.thumb;
        r5 = r13.searchImage;
        r5 = r5.width;
        r4.f34w = r5;
        r0 = r48;
        r4 = r0.thumb;
        r5 = r13.searchImage;
        r5 = r5.height;
        r4.f33h = r5;
        r0 = r48;
        r4 = r0.thumb;
        r5 = 0;
        r4.size = r5;
        r0 = r48;
        r4 = r0.thumb;
        r5 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r5.<init>();
        r4.location = r5;
        r0 = r48;
        r4 = r0.thumb;
        r5 = "x";
        r4.type = r5;
    L_0x02c5:
        r7 = r48;
        r4 = r13.searchImage;
        r0 = r4.imageUrl;
        r67 = r0;
        if (r45 != 0) goto L_0x0376;
    L_0x02cf:
        r4 = r13.searchImage;
        r8 = r4.imageUrl;
    L_0x02d3:
        if (r9 == 0) goto L_0x02e5;
    L_0x02d5:
        r4 = r13.searchImage;
        r4 = r4.imageUrl;
        if (r4 == 0) goto L_0x02e5;
    L_0x02db:
        r4 = "originalPath";
        r5 = r13.searchImage;
        r5 = r5.imageUrl;
        r9.put(r4, r5);
    L_0x02e5:
        r4 = new org.telegram.messenger.SendMessagesHelper$$Lambda$23;
        r5 = r93;
        r6 = r90;
        r10 = r88;
        r12 = r94;
        r4.<init>(r5, r6, r7, r8, r9, r10, r12, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x02f5:
        r14 = r14 + 1;
        goto L_0x014e;
    L_0x02f9:
        if (r58 != 0) goto L_0x0316;
    L_0x02fb:
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r90);
        r4 = r13.searchImage;
        r6 = r4.imageUrl;
        if (r58 != 0) goto L_0x0351;
    L_0x0305:
        r4 = 1;
    L_0x0306:
        r47 = r5.getSentFile(r6, r4);
        r47 = (org.telegram.tgnet.TLRPC.Document) r47;
        r0 = r47;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r4 == 0) goto L_0x0316;
    L_0x0312:
        r48 = r47;
        r48 = (org.telegram.tgnet.TLRPC.TL_document) r48;
    L_0x0316:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = r13.searchImage;
        r5 = r5.imageUrl;
        r5 = org.telegram.messenger.Utilities.MD5(r5);
        r4 = r4.append(r5);
        r5 = ".";
        r4 = r4.append(r5);
        r5 = r13.searchImage;
        r5 = r5.imageUrl;
        r6 = "jpg";
        r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r6);
        r4 = r4.append(r5);
        r59 = r4.toString();
        r45 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r45;
        r1 = r59;
        r0.<init>(r4, r1);
        goto L_0x01a0;
    L_0x0351:
        r4 = 4;
        goto L_0x0306;
    L_0x0353:
        r4 = "image/gif";
        r0 = r48;
        r0.mime_type = r4;
        goto L_0x020d;
    L_0x035c:
        r45 = 0;
        goto L_0x0215;
    L_0x0360:
        r4 = r83.getAbsolutePath();	 Catch:{ Exception -> 0x0370 }
        r5 = 0;	 Catch:{ Exception -> 0x0370 }
        r6 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;	 Catch:{ Exception -> 0x0370 }
        r10 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;	 Catch:{ Exception -> 0x0370 }
        r11 = 1;	 Catch:{ Exception -> 0x0370 }
        r44 = org.telegram.messenger.ImageLoader.loadBitmap(r4, r5, r6, r10, r11);	 Catch:{ Exception -> 0x0370 }
        goto L_0x0270;
    L_0x0370:
        r49 = move-exception;
        org.telegram.messenger.FileLog.m8e(r49);
        goto L_0x0287;
    L_0x0376:
        r8 = r45.toString();
        goto L_0x02d3;
    L_0x037c:
        r65 = 1;
        r69 = 0;
        r4 = r13.searchImage;
        r4 = r4.photo;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r4 == 0) goto L_0x04e6;
    L_0x0388:
        r4 = r13.searchImage;
        r0 = r4.photo;
        r69 = r0;
        r69 = (org.telegram.tgnet.TLRPC.TL_photo) r69;
    L_0x0390:
        if (r69 != 0) goto L_0x0480;
    L_0x0392:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = r13.searchImage;
        r5 = r5.imageUrl;
        r5 = org.telegram.messenger.Utilities.MD5(r5);
        r4 = r4.append(r5);
        r5 = ".";
        r4 = r4.append(r5);
        r5 = r13.searchImage;
        r5 = r5.imageUrl;
        r6 = "jpg";
        r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r6);
        r4 = r4.append(r5);
        r59 = r4.toString();
        r45 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r45;
        r1 = r59;
        r0.<init>(r4, r1);
        r4 = r45.exists();
        if (r4 == 0) goto L_0x03ec;
    L_0x03d1:
        r4 = r45.length();
        r10 = 0;
        r4 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r4 == 0) goto L_0x03ec;
    L_0x03db:
        r4 = getInstance(r90);
        r5 = r45.toString();
        r6 = 0;
        r69 = r4.generatePhotoSizes(r5, r6);
        if (r69 == 0) goto L_0x03ec;
    L_0x03ea:
        r65 = 0;
    L_0x03ec:
        if (r69 != 0) goto L_0x0480;
    L_0x03ee:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = r13.searchImage;
        r5 = r5.thumbUrl;
        r5 = org.telegram.messenger.Utilities.MD5(r5);
        r4 = r4.append(r5);
        r5 = ".";
        r4 = r4.append(r5);
        r5 = r13.searchImage;
        r5 = r5.thumbUrl;
        r6 = "jpg";
        r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r6);
        r4 = r4.append(r5);
        r59 = r4.toString();
        r45 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r45;
        r1 = r59;
        r0.<init>(r4, r1);
        r4 = r45.exists();
        if (r4 == 0) goto L_0x043a;
    L_0x042d:
        r4 = getInstance(r90);
        r5 = r45.toString();
        r6 = 0;
        r69 = r4.generatePhotoSizes(r5, r6);
    L_0x043a:
        if (r69 != 0) goto L_0x0480;
    L_0x043c:
        r69 = new org.telegram.tgnet.TLRPC$TL_photo;
        r69.<init>();
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r90);
        r4 = r4.getCurrentTime();
        r0 = r69;
        r0.date = r4;
        r70 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r70.<init>();
        r4 = r13.searchImage;
        r4 = r4.width;
        r0 = r70;
        r0.w = r4;
        r4 = r13.searchImage;
        r4 = r4.height;
        r0 = r70;
        r0.h = r4;
        r4 = 0;
        r0 = r70;
        r0.size = r4;
        r4 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r4.<init>();
        r0 = r70;
        r0.location = r4;
        r4 = "x";
        r0 = r70;
        r0.type = r4;
        r0 = r69;
        r4 = r0.sizes;
        r0 = r70;
        r4.add(r0);
    L_0x0480:
        if (r69 == 0) goto L_0x02f5;
    L_0x0482:
        r18 = r69;
        r19 = r65;
        r9 = new java.util.HashMap;
        r9.<init>();
        r4 = r13.searchImage;
        r4 = r4.imageUrl;
        if (r4 == 0) goto L_0x049b;
    L_0x0491:
        r4 = "originalPath";
        r5 = r13.searchImage;
        r5 = r5.imageUrl;
        r9.put(r4, r5);
    L_0x049b:
        if (r92 == 0) goto L_0x04d0;
    L_0x049d:
        r71 = r71 + 1;
        r4 = "groupId";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "";
        r5 = r5.append(r6);
        r0 = r54;
        r5 = r5.append(r0);
        r5 = r5.toString();
        r9.put(r4, r5);
        r4 = 10;
        r0 = r71;
        if (r0 == r4) goto L_0x04c5;
    L_0x04c1:
        r4 = r46 + -1;
        if (r14 != r4) goto L_0x04d0;
    L_0x04c5:
        r4 = "final";
        r5 = "1";
        r9.put(r4, r5);
        r60 = 0;
    L_0x04d0:
        r15 = new org.telegram.messenger.SendMessagesHelper$$Lambda$24;
        r16 = r93;
        r17 = r90;
        r20 = r13;
        r21 = r9;
        r22 = r88;
        r24 = r94;
        r15.<init>(r16, r17, r18, r19, r20, r21, r22, r24);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r15);
        goto L_0x02f5;
    L_0x04e6:
        if (r58 != 0) goto L_0x0390;
    L_0x04e8:
        r4 = r13.ttl;
        if (r4 != 0) goto L_0x0390;
    L_0x04ec:
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r90);
        r4 = r13.searchImage;
        r6 = r4.imageUrl;
        if (r58 != 0) goto L_0x04ff;
    L_0x04f6:
        r4 = 0;
    L_0x04f7:
        r69 = r5.getSentFile(r6, r4);
        r69 = (org.telegram.tgnet.TLRPC.TL_photo) r69;
        goto L_0x0390;
    L_0x04ff:
        r4 = 3;
        goto L_0x04f7;
    L_0x0501:
        r4 = r13.isVideo;
        if (r4 == 0) goto L_0x07fa;
    L_0x0505:
        r82 = 0;
        r84 = 0;
        if (r91 == 0) goto L_0x0745;
    L_0x050b:
        r26 = 0;
    L_0x050d:
        if (r91 != 0) goto L_0x07d9;
    L_0x050f:
        if (r26 != 0) goto L_0x051c;
    L_0x0511:
        r4 = r13.path;
        r5 = "mp4";
        r4 = r4.endsWith(r5);
        if (r4 == 0) goto L_0x07d9;
    L_0x051c:
        r0 = r13.path;
        r68 = r0;
        r0 = r13.path;
        r66 = r0;
        r80 = new java.io.File;
        r0 = r80;
        r1 = r66;
        r0.<init>(r1);
        r78 = 0;
        r64 = 0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r66;
        r4 = r4.append(r0);
        r10 = r80.length();
        r4 = r4.append(r10);
        r5 = "_";
        r4 = r4.append(r5);
        r10 = r80.lastModified();
        r4 = r4.append(r10);
        r66 = r4.toString();
        if (r26 == 0) goto L_0x05d9;
    L_0x0559:
        r0 = r26;
        r0 = r0.muted;
        r64 = r0;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r66;
        r4 = r4.append(r0);
        r0 = r26;
        r10 = r0.estimatedDuration;
        r4 = r4.append(r10);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r26;
        r10 = r0.startTime;
        r4 = r4.append(r10);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r26;
        r10 = r0.endTime;
        r5 = r4.append(r10);
        r0 = r26;
        r4 = r0.muted;
        if (r4 == 0) goto L_0x0756;
    L_0x0596:
        r4 = "_m";
    L_0x0599:
        r4 = r5.append(r4);
        r66 = r4.toString();
        r0 = r26;
        r4 = r0.resultWidth;
        r0 = r26;
        r5 = r0.originalWidth;
        if (r4 == r5) goto L_0x05c9;
    L_0x05ab:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r66;
        r4 = r4.append(r0);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r26;
        r5 = r0.resultWidth;
        r4 = r4.append(r5);
        r66 = r4.toString();
    L_0x05c9:
        r0 = r26;
        r4 = r0.startTime;
        r10 = 0;
        r4 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1));
        if (r4 < 0) goto L_0x075b;
    L_0x05d3:
        r0 = r26;
        r0 = r0.startTime;
        r78 = r0;
    L_0x05d9:
        r48 = 0;
        if (r58 != 0) goto L_0x05f0;
    L_0x05dd:
        r4 = r13.ttl;
        if (r4 != 0) goto L_0x05f0;
    L_0x05e1:
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r90);
        if (r58 != 0) goto L_0x075f;
    L_0x05e7:
        r4 = 2;
    L_0x05e8:
        r0 = r66;
        r48 = r5.getSentFile(r0, r4);
        r48 = (org.telegram.tgnet.TLRPC.TL_document) r48;
    L_0x05f0:
        if (r48 != 0) goto L_0x06df;
    L_0x05f2:
        r4 = r13.path;
        r0 = r78;
        r82 = createVideoThumbnail(r4, r0);
        if (r82 != 0) goto L_0x0603;
    L_0x05fc:
        r4 = r13.path;
        r5 = 1;
        r82 = android.media.ThumbnailUtils.createVideoThumbnail(r4, r5);
    L_0x0603:
        r4 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r5 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r6 = 55;
        r0 = r82;
        r1 = r58;
        r77 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r4, r5, r6, r1);
        if (r82 == 0) goto L_0x0617;
    L_0x0613:
        if (r77 == 0) goto L_0x0617;
    L_0x0615:
        r82 = 0;
    L_0x0617:
        r48 = new org.telegram.tgnet.TLRPC$TL_document;
        r48.<init>();
        r0 = r77;
        r1 = r48;
        r1.thumb = r0;
        r0 = r48;
        r4 = r0.thumb;
        if (r4 != 0) goto L_0x0762;
    L_0x0628:
        r4 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;
        r4.<init>();
        r0 = r48;
        r0.thumb = r4;
        r0 = r48;
        r4 = r0.thumb;
        r5 = "s";
        r4.type = r5;
    L_0x063a:
        r4 = "video/mp4";
        r0 = r48;
        r0.mime_type = r4;
        r4 = org.telegram.messenger.UserConfig.getInstance(r90);
        r5 = 0;
        r4.saveConfig(r5);
        if (r58 == 0) goto L_0x0774;
    L_0x064b:
        r4 = 66;
        r0 = r51;
        if (r0 < r4) goto L_0x076d;
    L_0x0651:
        r40 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r40.<init>();
    L_0x0656:
        r0 = r48;
        r4 = r0.attributes;
        r0 = r40;
        r4.add(r0);
        if (r26 == 0) goto L_0x07c0;
    L_0x0661:
        r4 = r26.needConvert();
        if (r4 == 0) goto L_0x07c0;
    L_0x0667:
        r0 = r26;
        r4 = r0.muted;
        if (r4 == 0) goto L_0x0780;
    L_0x066d:
        r0 = r48;
        r4 = r0.attributes;
        r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
        r5.<init>();
        r4.add(r5);
        r4 = r13.path;
        r0 = r40;
        r1 = r26;
        fillVideoAttribute(r4, r0, r1);
        r0 = r40;
        r4 = r0.w;
        r0 = r26;
        r0.originalWidth = r4;
        r0 = r40;
        r4 = r0.h;
        r0 = r26;
        r0.originalHeight = r4;
        r0 = r26;
        r4 = r0.resultWidth;
        r0 = r40;
        r0.w = r4;
        r0 = r26;
        r4 = r0.resultHeight;
        r0 = r40;
        r0.h = r4;
    L_0x06a2:
        r0 = r26;
        r4 = r0.estimatedSize;
        r4 = (int) r4;
        r0 = r48;
        r0.size = r4;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "-2147483648_";
        r4 = r4.append(r5);
        r5 = org.telegram.messenger.SharedConfig.getLastLocalId();
        r4 = r4.append(r5);
        r5 = ".mp4";
        r4 = r4.append(r5);
        r53 = r4.toString();
        r45 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r45;
        r1 = r53;
        r0.<init>(r4, r1);
        org.telegram.messenger.SharedConfig.saveConfig();
        r68 = r45.getAbsolutePath();
    L_0x06df:
        r27 = r48;
        r67 = r66;
        r28 = r68;
        r9 = new java.util.HashMap;
        r9.<init>();
        r22 = r82;
        r23 = r84;
        if (r66 == 0) goto L_0x06f8;
    L_0x06f0:
        r4 = "originalPath";
        r0 = r66;
        r9.put(r4, r0);
    L_0x06f8:
        if (r64 != 0) goto L_0x072f;
    L_0x06fa:
        if (r92 == 0) goto L_0x072f;
    L_0x06fc:
        r71 = r71 + 1;
        r4 = "groupId";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "";
        r5 = r5.append(r6);
        r0 = r54;
        r5 = r5.append(r0);
        r5 = r5.toString();
        r9.put(r4, r5);
        r4 = 10;
        r0 = r71;
        if (r0 == r4) goto L_0x0724;
    L_0x0720:
        r4 = r46 + -1;
        if (r14 != r4) goto L_0x072f;
    L_0x0724:
        r4 = "final";
        r5 = "1";
        r9.put(r4, r5);
        r60 = 0;
    L_0x072f:
        r21 = new org.telegram.messenger.SendMessagesHelper$$Lambda$25;
        r24 = r93;
        r25 = r90;
        r29 = r9;
        r30 = r88;
        r32 = r94;
        r33 = r13;
        r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29, r30, r32, r33);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r21);
        goto L_0x02f5;
    L_0x0745:
        r4 = r13.videoEditedInfo;
        if (r4 == 0) goto L_0x074f;
    L_0x0749:
        r0 = r13.videoEditedInfo;
        r26 = r0;
    L_0x074d:
        goto L_0x050d;
    L_0x074f:
        r4 = r13.path;
        r26 = createCompressionSettings(r4);
        goto L_0x074d;
    L_0x0756:
        r4 = "";
        goto L_0x0599;
    L_0x075b:
        r78 = 0;
        goto L_0x05d9;
    L_0x075f:
        r4 = 5;
        goto L_0x05e8;
    L_0x0762:
        r0 = r48;
        r4 = r0.thumb;
        r5 = "s";
        r4.type = r5;
        goto L_0x063a;
    L_0x076d:
        r40 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo_layer65;
        r40.<init>();
        goto L_0x0656;
    L_0x0774:
        r40 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
        r40.<init>();
        r4 = 1;
        r0 = r40;
        r0.supports_streaming = r4;
        goto L_0x0656;
    L_0x0780:
        r0 = r26;
        r4 = r0.estimatedDuration;
        r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r4 = r4 / r10;
        r4 = (int) r4;
        r0 = r40;
        r0.duration = r4;
        r0 = r26;
        r4 = r0.rotationValue;
        r5 = 90;
        if (r4 == r5) goto L_0x079c;
    L_0x0794:
        r0 = r26;
        r4 = r0.rotationValue;
        r5 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        if (r4 != r5) goto L_0x07ae;
    L_0x079c:
        r0 = r26;
        r4 = r0.resultHeight;
        r0 = r40;
        r0.w = r4;
        r0 = r26;
        r4 = r0.resultWidth;
        r0 = r40;
        r0.h = r4;
        goto L_0x06a2;
    L_0x07ae:
        r0 = r26;
        r4 = r0.resultWidth;
        r0 = r40;
        r0.w = r4;
        r0 = r26;
        r4 = r0.resultHeight;
        r0 = r40;
        r0.h = r4;
        goto L_0x06a2;
    L_0x07c0:
        r4 = r80.exists();
        if (r4 == 0) goto L_0x07cf;
    L_0x07c6:
        r4 = r80.length();
        r4 = (int) r4;
        r0 = r48;
        r0.size = r4;
    L_0x07cf:
        r4 = r13.path;
        r5 = 0;
        r0 = r40;
        fillVideoAttribute(r4, r0, r5);
        goto L_0x06df;
    L_0x07d9:
        r0 = r13.path;
        r30 = r0;
        r0 = r13.path;
        r31 = r0;
        r32 = 0;
        r33 = 0;
        r0 = r13.caption;
        r37 = r0;
        r0 = r13.entities;
        r38 = r0;
        r29 = r90;
        r34 = r88;
        r36 = r94;
        r39 = r93;
        prepareSendingDocumentInternal(r29, r30, r31, r32, r33, r34, r36, r37, r38, r39);
        goto L_0x02f5;
    L_0x07fa:
        r0 = r13.path;
        r66 = r0;
        r0 = r13.path;
        r81 = r0;
        if (r81 != 0) goto L_0x0814;
    L_0x0804:
        r4 = r13.uri;
        if (r4 == 0) goto L_0x0814;
    L_0x0808:
        r4 = r13.uri;
        r81 = org.telegram.messenger.AndroidUtilities.getPath(r4);
        r4 = r13.uri;
        r66 = r4.toString();
    L_0x0814:
        r57 = 0;
        if (r91 == 0) goto L_0x085b;
    L_0x0818:
        r57 = 1;
        r4 = new java.io.File;
        r0 = r81;
        r4.<init>(r0);
        r52 = org.telegram.messenger.FileLoader.getFileExtension(r4);
    L_0x0825:
        if (r57 == 0) goto L_0x08ca;
    L_0x0827:
        if (r72 != 0) goto L_0x083d;
    L_0x0829:
        r72 = new java.util.ArrayList;
        r72.<init>();
        r75 = new java.util.ArrayList;
        r75.<init>();
        r73 = new java.util.ArrayList;
        r73.<init>();
        r74 = new java.util.ArrayList;
        r74.<init>();
    L_0x083d:
        r0 = r72;
        r1 = r81;
        r0.add(r1);
        r0 = r75;
        r1 = r66;
        r0.add(r1);
        r4 = r13.caption;
        r0 = r73;
        r0.add(r4);
        r4 = r13.entities;
        r0 = r74;
        r0.add(r4);
        goto L_0x02f5;
    L_0x085b:
        if (r81 == 0) goto L_0x0888;
    L_0x085d:
        r4 = ".gif";
        r0 = r81;
        r4 = r0.endsWith(r4);
        if (r4 != 0) goto L_0x0873;
    L_0x0868:
        r4 = ".webp";
        r0 = r81;
        r4 = r0.endsWith(r4);
        if (r4 == 0) goto L_0x0888;
    L_0x0873:
        r4 = ".gif";
        r0 = r81;
        r4 = r0.endsWith(r4);
        if (r4 == 0) goto L_0x0884;
    L_0x087e:
        r52 = "gif";
    L_0x0881:
        r57 = 1;
        goto L_0x0825;
    L_0x0884:
        r52 = "webp";
        goto L_0x0881;
    L_0x0888:
        if (r81 != 0) goto L_0x0825;
    L_0x088a:
        r4 = r13.uri;
        if (r4 == 0) goto L_0x0825;
    L_0x088e:
        r4 = r13.uri;
        r4 = org.telegram.messenger.MediaController.isGif(r4);
        if (r4 == 0) goto L_0x08ac;
    L_0x0896:
        r57 = 1;
        r4 = r13.uri;
        r66 = r4.toString();
        r4 = r13.uri;
        r5 = "gif";
        r81 = org.telegram.messenger.MediaController.copyFileToCache(r4, r5);
        r52 = "gif";
        goto L_0x0825;
    L_0x08ac:
        r4 = r13.uri;
        r4 = org.telegram.messenger.MediaController.isWebp(r4);
        if (r4 == 0) goto L_0x0825;
    L_0x08b4:
        r57 = 1;
        r4 = r13.uri;
        r66 = r4.toString();
        r4 = r13.uri;
        r5 = "webp";
        r81 = org.telegram.messenger.MediaController.copyFileToCache(r4, r5);
        r52 = "webp";
        goto L_0x0825;
    L_0x08ca:
        if (r81 == 0) goto L_0x096e;
    L_0x08cc:
        r80 = new java.io.File;
        r80.<init>(r81);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r66;
        r4 = r4.append(r0);
        r10 = r80.length();
        r4 = r4.append(r10);
        r5 = "_";
        r4 = r4.append(r5);
        r10 = r80.lastModified();
        r4 = r4.append(r10);
        r66 = r4.toString();
    L_0x08f7:
        r69 = 0;
        if (r86 == 0) goto L_0x0976;
    L_0x08fb:
        r0 = r86;
        r85 = r0.get(r13);
        r85 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r85;
        r0 = r85;
        r0 = r0.photo;
        r69 = r0;
        if (r69 != 0) goto L_0x0918;
    L_0x090b:
        r0 = r85;	 Catch:{ Exception -> 0x0971 }
        r4 = r0.sync;	 Catch:{ Exception -> 0x0971 }
        r4.await();	 Catch:{ Exception -> 0x0971 }
    L_0x0912:
        r0 = r85;
        r0 = r0.photo;
        r69 = r0;
    L_0x0918:
        if (r69 == 0) goto L_0x0a23;
    L_0x091a:
        r18 = r69;
        r9 = new java.util.HashMap;
        r9.<init>();
        r4 = r13.masks;
        if (r4 == 0) goto L_0x09b8;
    L_0x0925:
        r4 = r13.masks;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x09b8;
    L_0x092d:
        r4 = 1;
    L_0x092e:
        r0 = r69;
        r0.has_stickers = r4;
        if (r4 == 0) goto L_0x09cc;
    L_0x0934:
        r76 = new org.telegram.tgnet.SerializedData;
        r4 = r13.masks;
        r4 = r4.size();
        r4 = r4 * 20;
        r4 = r4 + 4;
        r0 = r76;
        r0.<init>(r4);
        r4 = r13.masks;
        r4 = r4.size();
        r0 = r76;
        r0.writeInt32(r4);
        r41 = 0;
    L_0x0952:
        r4 = r13.masks;
        r4 = r4.size();
        r0 = r41;
        if (r0 >= r4) goto L_0x09bb;
    L_0x095c:
        r4 = r13.masks;
        r0 = r41;
        r4 = r4.get(r0);
        r4 = (org.telegram.tgnet.TLRPC.InputDocument) r4;
        r0 = r76;
        r4.serializeToStream(r0);
        r41 = r41 + 1;
        goto L_0x0952;
    L_0x096e:
        r66 = 0;
        goto L_0x08f7;
    L_0x0971:
        r49 = move-exception;
        org.telegram.messenger.FileLog.m8e(r49);
        goto L_0x0912;
    L_0x0976:
        if (r58 != 0) goto L_0x09a4;
    L_0x0978:
        r4 = r13.ttl;
        if (r4 != 0) goto L_0x09a4;
    L_0x097c:
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r90);
        if (r58 != 0) goto L_0x09b4;
    L_0x0982:
        r4 = 0;
    L_0x0983:
        r0 = r66;
        r69 = r5.getSentFile(r0, r4);
        r69 = (org.telegram.tgnet.TLRPC.TL_photo) r69;
        if (r69 != 0) goto L_0x09a4;
    L_0x098d:
        r4 = r13.uri;
        if (r4 == 0) goto L_0x09a4;
    L_0x0991:
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r90);
        r4 = r13.uri;
        r6 = org.telegram.messenger.AndroidUtilities.getPath(r4);
        if (r58 != 0) goto L_0x09b6;
    L_0x099d:
        r4 = 0;
    L_0x099e:
        r69 = r5.getSentFile(r6, r4);
        r69 = (org.telegram.tgnet.TLRPC.TL_photo) r69;
    L_0x09a4:
        if (r69 != 0) goto L_0x0918;
    L_0x09a6:
        r4 = getInstance(r90);
        r5 = r13.path;
        r6 = r13.uri;
        r69 = r4.generatePhotoSizes(r5, r6);
        goto L_0x0918;
    L_0x09b4:
        r4 = 3;
        goto L_0x0983;
    L_0x09b6:
        r4 = 3;
        goto L_0x099e;
    L_0x09b8:
        r4 = 0;
        goto L_0x092e;
    L_0x09bb:
        r4 = "masks";
        r5 = r76.toByteArray();
        r5 = org.telegram.messenger.Utilities.bytesToHex(r5);
        r9.put(r4, r5);
        r76.cleanup();
    L_0x09cc:
        if (r66 == 0) goto L_0x09d6;
    L_0x09ce:
        r4 = "originalPath";
        r0 = r66;
        r9.put(r4, r0);
    L_0x09d6:
        if (r92 == 0) goto L_0x0a0b;
    L_0x09d8:
        r71 = r71 + 1;
        r4 = "groupId";
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "";
        r5 = r5.append(r6);
        r0 = r54;
        r5 = r5.append(r0);
        r5 = r5.toString();
        r9.put(r4, r5);
        r4 = 10;
        r0 = r71;
        if (r0 == r4) goto L_0x0a00;
    L_0x09fc:
        r4 = r46 + -1;
        if (r14 != r4) goto L_0x0a0b;
    L_0x0a00:
        r4 = "final";
        r5 = "1";
        r9.put(r4, r5);
        r60 = 0;
    L_0x0a0b:
        r29 = new org.telegram.messenger.SendMessagesHelper$$Lambda$26;
        r30 = r93;
        r31 = r90;
        r32 = r18;
        r33 = r9;
        r34 = r88;
        r36 = r94;
        r37 = r13;
        r29.<init>(r30, r31, r32, r33, r34, r36, r37);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r29);
        goto L_0x02f5;
    L_0x0a23:
        if (r72 != 0) goto L_0x0a39;
    L_0x0a25:
        r72 = new java.util.ArrayList;
        r72.<init>();
        r75 = new java.util.ArrayList;
        r75.<init>();
        r73 = new java.util.ArrayList;
        r73.<init>();
        r74 = new java.util.ArrayList;
        r74.<init>();
    L_0x0a39:
        r0 = r72;
        r1 = r81;
        r0.add(r1);
        r0 = r75;
        r1 = r66;
        r0.add(r1);
        r4 = r13.caption;
        r0 = r73;
        r0.add(r4);
        r4 = r13.entities;
        r0 = r74;
        r0.add(r4);
        goto L_0x02f5;
    L_0x0a57:
        r4 = 0;
        r4 = (r60 > r4 ? 1 : (r60 == r4 ? 0 : -1));
        if (r4 == 0) goto L_0x0a6b;
    L_0x0a5d:
        r62 = r60;
        r4 = new org.telegram.messenger.SendMessagesHelper$$Lambda$27;
        r0 = r90;
        r1 = r62;
        r4.<init>(r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x0a6b:
        if (r95 == 0) goto L_0x0a70;
    L_0x0a6d:
        r95.releasePermission();
    L_0x0a70:
        if (r72 == 0) goto L_0x0ab1;
    L_0x0a72:
        r4 = r72.isEmpty();
        if (r4 != 0) goto L_0x0ab1;
    L_0x0a78:
        r14 = 0;
    L_0x0a79:
        r4 = r72.size();
        if (r14 >= r4) goto L_0x0ab1;
    L_0x0a7f:
        r0 = r72;
        r30 = r0.get(r14);
        r30 = (java.lang.String) r30;
        r0 = r75;
        r31 = r0.get(r14);
        r31 = (java.lang.String) r31;
        r32 = 0;
        r0 = r73;
        r37 = r0.get(r14);
        r37 = (java.lang.CharSequence) r37;
        r0 = r74;
        r38 = r0.get(r14);
        r38 = (java.util.ArrayList) r38;
        r29 = r90;
        r33 = r52;
        r34 = r88;
        r36 = r94;
        r39 = r93;
        prepareSendingDocumentInternal(r29, r30, r31, r32, r33, r34, r36, r37, r38, r39);
        r14 = r14 + 1;
        goto L_0x0a79;
    L_0x0ab1:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0ad2;
    L_0x0ab5:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "total send time = ";
        r4 = r4.append(r5);
        r10 = java.lang.System.currentTimeMillis();
        r10 = r10 - r42;
        r4 = r4.append(r10);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.m5d(r4);
    L_0x0ad2:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$59$SendMessagesHelper(java.util.ArrayList, long, int, boolean, boolean, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, android.support.v13.view.inputmethod.InputContentInfoCompat):void");
    }

    static final /* synthetic */ void lambda$null$53$SendMessagesHelper(MediaSendPrepareWorker worker, int currentAccount, SendingMediaInfo info) {
        worker.photo = getInstance(currentAccount).generatePhotoSizes(info.path, info.uri);
        worker.sync.countDown();
    }

    static final /* synthetic */ void lambda$null$54$SendMessagesHelper(MessageObject editingMessageObject, int currentAccount, TL_document documentFinal, String pathFinal, HashMap params, long dialog_id, MessageObject reply_to_msg, SendingMediaInfo info) {
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, null, documentFinal, pathFinal, params, false);
            return;
        }
        getInstance(currentAccount).sendMessage(documentFinal, null, pathFinal, dialog_id, reply_to_msg, info.caption, info.entities, null, params, 0);
    }

    static final /* synthetic */ void lambda$null$55$SendMessagesHelper(MessageObject editingMessageObject, int currentAccount, TL_photo photoFinal, boolean needDownloadHttpFinal, SendingMediaInfo info, HashMap params, long dialog_id, MessageObject reply_to_msg) {
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, photoFinal, null, null, needDownloadHttpFinal ? info.searchImage.imageUrl : null, params, false);
        } else {
            getInstance(currentAccount).sendMessage(photoFinal, needDownloadHttpFinal ? info.searchImage.imageUrl : null, dialog_id, reply_to_msg, info.caption, info.entities, null, params, info.ttl);
        }
    }

    static final /* synthetic */ void lambda$null$56$SendMessagesHelper(Bitmap thumbFinal, String thumbKeyFinal, MessageObject editingMessageObject, int currentAccount, VideoEditedInfo videoEditedInfo, TL_document videoFinal, String finalPath, HashMap params, long dialog_id, MessageObject reply_to_msg, SendingMediaInfo info) {
        if (!(thumbFinal == null || thumbKeyFinal == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(thumbFinal), thumbKeyFinal);
        }
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, videoEditedInfo, videoFinal, finalPath, params, false);
            return;
        }
        getInstance(currentAccount).sendMessage(videoFinal, videoEditedInfo, finalPath, dialog_id, reply_to_msg, info.caption, info.entities, null, params, info.ttl);
    }

    static final /* synthetic */ void lambda$null$57$SendMessagesHelper(MessageObject editingMessageObject, int currentAccount, TL_photo photoFinal, HashMap params, long dialog_id, MessageObject reply_to_msg, SendingMediaInfo info) {
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, photoFinal, null, null, null, params, false);
            return;
        }
        getInstance(currentAccount).sendMessage(photoFinal, null, dialog_id, reply_to_msg, info.caption, info.entities, null, params, info.ttl);
    }

    static final /* synthetic */ void lambda$null$58$SendMessagesHelper(int currentAccount, long lastGroupIdFinal) {
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

    private static void fillVideoAttribute(String videoPath, TL_documentAttributeVideo attributeVideo, VideoEditedInfo videoEditedInfo) {
        Throwable e;
        MediaPlayer mp;
        Throwable th;
        boolean infoObtained = false;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
            try {
                mediaMetadataRetriever2.setDataSource(videoPath);
                String width = mediaMetadataRetriever2.extractMetadata(18);
                if (width != null) {
                    attributeVideo.w = Integer.parseInt(width);
                }
                String height = mediaMetadataRetriever2.extractMetadata(19);
                if (height != null) {
                    attributeVideo.h = Integer.parseInt(height);
                }
                String duration = mediaMetadataRetriever2.extractMetadata(9);
                if (duration != null) {
                    attributeVideo.duration = (int) Math.ceil((double) (((float) Long.parseLong(duration)) / 1000.0f));
                }
                if (VERSION.SDK_INT >= 17) {
                    String rotation = mediaMetadataRetriever2.extractMetadata(24);
                    if (rotation != null) {
                        int val = Utilities.parseInt(rotation).intValue();
                        if (videoEditedInfo != null) {
                            videoEditedInfo.rotationValue = val;
                        } else if (val == 90 || val == 270) {
                            int temp = attributeVideo.w;
                            attributeVideo.w = attributeVideo.h;
                            attributeVideo.h = temp;
                        }
                    }
                }
                infoObtained = true;
                if (mediaMetadataRetriever2 != null) {
                    try {
                        mediaMetadataRetriever2.release();
                    } catch (Throwable e2) {
                        FileLog.m8e(e2);
                        mediaMetadataRetriever = mediaMetadataRetriever2;
                    }
                }
                mediaMetadataRetriever = mediaMetadataRetriever2;
            } catch (Exception e3) {
                e2 = e3;
                mediaMetadataRetriever = mediaMetadataRetriever2;
                try {
                    FileLog.m8e(e2);
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Throwable e22) {
                            FileLog.m8e(e22);
                        }
                    }
                    if (infoObtained) {
                        try {
                            mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(videoPath)));
                            if (mp == null) {
                                attributeVideo.duration = (int) Math.ceil((double) (((float) mp.getDuration()) / 1000.0f));
                                attributeVideo.w = mp.getVideoWidth();
                                attributeVideo.h = mp.getVideoHeight();
                                mp.release();
                            }
                        } catch (Throwable e222) {
                            FileLog.m8e(e222);
                            return;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Throwable e2222) {
                            FileLog.m8e(e2222);
                        }
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                mediaMetadataRetriever = mediaMetadataRetriever2;
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
                throw th;
            }
        } catch (Exception e4) {
            e2222 = e4;
            FileLog.m8e(e2222);
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
            if (infoObtained) {
                mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(videoPath)));
                if (mp == null) {
                    attributeVideo.duration = (int) Math.ceil((double) (((float) mp.getDuration()) / 1000.0f));
                    attributeVideo.w = mp.getVideoWidth();
                    attributeVideo.h = mp.getVideoHeight();
                    mp.release();
                }
            }
        }
        if (infoObtained) {
            mp = MediaPlayer.create(ApplicationLoader.applicationContext, Uri.fromFile(new File(videoPath)));
            if (mp == null) {
                attributeVideo.duration = (int) Math.ceil((double) (((float) mp.getDuration()) / 1000.0f));
                attributeVideo.w = mp.getVideoWidth();
                attributeVideo.h = mp.getVideoHeight();
                mp.release();
            }
        }
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
        if (max > 90) {
            float scale = 90.0f / ((float) max);
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
        IsoFile isoFile = new IsoFile(videoPath);
        List<Box> boxes = Path.getPaths((Container) isoFile, "/moov/trak/");
        if (Path.getPath((Container) isoFile, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") == null && BuildVars.LOGS_ENABLED) {
            FileLog.m5d("video hasn't mp4a atom");
        }
        if (Path.getPath((Container) isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m5d("video hasn't avc1 atom");
            }
            return null;
        }
        int b = 0;
        while (b < boxes.size()) {
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
                FileLog.m8e(e);
            }
            try {
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
                b++;
            } catch (Throwable e2) {
                FileLog.m8e(e2);
                return null;
            }
        }
        if (trackHeaderBox == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m5d("video hasn't trackHeaderBox atom");
            }
            return null;
        }
        int compressionsCount;
        if (VERSION.SDK_INT < 18) {
            try {
                MediaCodecInfo codecInfo = MediaController.selectCodec("video/avc");
                if (codecInfo == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m5d("no codec info for video/avc");
                    }
                    return null;
                }
                String name = codecInfo.getName();
                if (name.equals("OMX.google.h264.encoder") || name.equals("OMX.ST.VFM.H264Enc") || name.equals("OMX.Exynos.avc.enc") || name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") || name.equals("OMX.MARVELL.VIDEO.H264ENCODER") || name.equals("OMX.k3.video.encoder.avc") || name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m5d("unsupported encoder = " + name);
                    }
                    return null;
                } else if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m5d("no color format for video/avc");
                    }
                    return null;
                }
            } catch (Exception e3) {
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
    }

    public static void prepareSendingVideo(String videoPath, long estimatedSize, long duration, int width, int height, VideoEditedInfo info, long dialog_id, MessageObject reply_to_msg, CharSequence caption, ArrayList<MessageEntity> entities, int ttl, MessageObject editingMessageObject) {
        if (videoPath != null && videoPath.length() != 0) {
            new Thread(new SendMessagesHelper$$Lambda$20(info, videoPath, dialog_id, duration, ttl, UserConfig.selectedAccount, height, width, estimatedSize, caption, editingMessageObject, reply_to_msg, entities)).start();
        }
    }

    static final /* synthetic */ void lambda$prepareSendingVideo$61$SendMessagesHelper(VideoEditedInfo info, String videoPath, long dialog_id, long duration, int ttl, int currentAccount, int height, int width, long estimatedSize, CharSequence caption, MessageObject editingMessageObject, MessageObject reply_to_msg, ArrayList entities) {
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
                originalPath = originalPath + duration + "_" + videoEditedInfo.startTime + "_" + videoEditedInfo.endTime + (videoEditedInfo.muted ? "_m" : TtmlNode.ANONYMOUS_REGION_ID);
                if (videoEditedInfo.resultWidth != videoEditedInfo.originalWidth) {
                    originalPath = originalPath + "_" + videoEditedInfo.resultWidth;
                }
            }
            startTime = videoEditedInfo.startTime >= 0 ? videoEditedInfo.startTime : 0;
        }
        TL_document tL_document = null;
        if (!isEncrypted && ttl == 0) {
            tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, !isEncrypted ? 2 : 5);
        }
        if (tL_document == null) {
            TL_documentAttributeVideo attributeVideo;
            thumb = createVideoThumbnail(videoPath, startTime);
            if (thumb == null) {
                thumb = ThumbnailUtils.createVideoThumbnail(videoPath, 1);
            }
            PhotoSize size = ImageLoader.scaleAndSaveImage(thumb, 90.0f, 90.0f, 55, isEncrypted);
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
            tL_document = new TL_document();
            tL_document.thumb = size;
            if (tL_document.thumb == null) {
                tL_document.thumb = new TL_photoSizeEmpty();
                tL_document.thumb.type = "s";
            } else {
                tL_document.thumb.type = "s";
            }
            tL_document.mime_type = MimeTypes.VIDEO_MP4;
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
            tL_document.attributes.add(attributeVideo);
            if (videoEditedInfo == null || !videoEditedInfo.needConvert()) {
                if (file.exists()) {
                    tL_document.size = (int) file.length();
                }
                fillVideoAttribute(videoPath, attributeVideo, null);
            } else {
                if (videoEditedInfo.muted) {
                    tL_document.attributes.add(new TL_documentAttributeAnimated());
                    fillVideoAttribute(videoPath, attributeVideo, videoEditedInfo);
                    videoEditedInfo.originalWidth = attributeVideo.w;
                    videoEditedInfo.originalHeight = attributeVideo.h;
                    attributeVideo.w = videoEditedInfo.resultWidth;
                    attributeVideo.h = videoEditedInfo.resultHeight;
                } else {
                    attributeVideo.duration = (int) (duration / 1000);
                    if (videoEditedInfo.rotationValue == 90 || videoEditedInfo.rotationValue == 270) {
                        attributeVideo.w = height;
                        attributeVideo.h = width;
                    } else {
                        attributeVideo.w = width;
                        attributeVideo.h = height;
                    }
                }
                tL_document.size = (int) estimatedSize;
                file = new File(FileLoader.getDirectory(4), "-2147483648_" + SharedConfig.getLastLocalId() + ".mp4");
                SharedConfig.saveConfig();
                path = file.getAbsolutePath();
            }
        }
        TL_document videoFinal = tL_document;
        String originalPathFinal = originalPath;
        String finalPath = path;
        HashMap<String, String> params = new HashMap();
        Bitmap thumbFinal = thumb;
        String thumbKeyFinal = thumbKey;
        String captionFinal = caption != null ? caption.toString() : TtmlNode.ANONYMOUS_REGION_ID;
        if (originalPath != null) {
            params.put("originalPath", originalPath);
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$Lambda$21(thumbFinal, thumbKeyFinal, editingMessageObject, currentAccount, videoEditedInfo, videoFinal, finalPath, params, dialog_id, reply_to_msg, captionFinal, entities, ttl));
    }

    static final /* synthetic */ void lambda$null$60$SendMessagesHelper(Bitmap thumbFinal, String thumbKeyFinal, MessageObject editingMessageObject, int currentAccount, VideoEditedInfo videoEditedInfo, TL_document videoFinal, String finalPath, HashMap params, long dialog_id, MessageObject reply_to_msg, String captionFinal, ArrayList entities, int ttl) {
        if (!(thumbFinal == null || thumbKeyFinal == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(thumbFinal), thumbKeyFinal);
        }
        if (editingMessageObject != null) {
            getInstance(currentAccount).editMessageMedia(editingMessageObject, null, videoEditedInfo, videoFinal, finalPath, params, false);
        } else {
            getInstance(currentAccount).sendMessage(videoFinal, videoEditedInfo, finalPath, dialog_id, reply_to_msg, captionFinal, entities, null, params, ttl);
        }
    }
}
