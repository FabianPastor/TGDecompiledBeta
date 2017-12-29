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
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.hockeyapp.android.UpdateFragment;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
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
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.DecryptedMessage;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputDocument;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputUser;
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
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaContact;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaExternalDocument;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaGeoPoint;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage_layer45;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker_layer55;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo_layer65;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFile;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputMediaContact;
import org.telegram.tgnet.TLRPC.TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_inputMediaGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputMediaGifExternal;
import org.telegram.tgnet.TLRPC.TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputMediaVenue;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhoto;
import org.telegram.tgnet.TLRPC.TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
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
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
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
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateShortSentMessage;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.TL_userRequest_old2;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
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
    private HashMap<Integer, Message> sendingMessages = new HashMap();
    private HashMap<Integer, MessageObject> unsentMessages = new HashMap();
    private HashMap<String, MessageObject> waitingForCallback = new HashMap();
    private HashMap<String, MessageObject> waitingForLocation = new HashMap();

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

        private class GpsLocationListener implements LocationListener {
            private GpsLocationListener() {
            }

            public void onLocationChanged(Location location) {
                if (location != null && LocationProvider.this.locationQueryCancelRunnable != null) {
                    FileLog.e("found location " + location);
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
                        a = 0;
                        while (a < arr.size()) {
                            message = (DelayedMessage) arr.get(a);
                            ArrayList messages;
                            if (message.type == 4) {
                                while (0 < message.messageObjects.size()) {
                                    MessageObject obj2 = (MessageObject) message.messageObjects.get(a);
                                    if (obj2 == messageObject) {
                                        obj2.videoEditedInfo = null;
                                        obj2.messageOwner.message = "-1";
                                        obj2.messageOwner.media.document.size = (int) finalSize;
                                        messages = new ArrayList();
                                        messages.add(obj2.messageOwner);
                                        MessagesStorage.getInstance(this.currentAccount).putMessages(messages, false, true, false, 0);
                                        break;
                                    }
                                    a++;
                                }
                            } else if (message.obj == messageObject) {
                                message.obj.videoEditedInfo = null;
                                message.obj.messageOwner.message = "-1";
                                message.obj.messageOwner.media.document.size = (int) finalSize;
                                messages = new ArrayList();
                                messages.add(message.obj.messageOwner);
                                MessagesStorage.getInstance(this.currentAccount).putMessages(messages, false, true, false, 0);
                                return;
                            }
                            a++;
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
                            for (int b = 0; b < message.messages.size(); b++) {
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
                    final File cacheFile;
                    if (fileType == 0) {
                        cacheFile = new File(FileLoader.getDirectory(4), Utilities.MD5(path) + "." + ImageLoader.getHttpUrlExtension(path, "file"));
                        Utilities.globalQueue.postRunnable(new Runnable() {
                            public void run() {
                                final TL_photo photo = SendMessagesHelper.getInstance(SendMessagesHelper.this.currentAccount).generatePhotoSizes(cacheFile.toString(), null);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (photo != null) {
                                            messageObject.messageOwner.media.photo = photo;
                                            messageObject.messageOwner.attachPath = cacheFile.toString();
                                            ArrayList messages = new ArrayList();
                                            messages.add(messageObject.messageOwner);
                                            MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(messages, false, true, false, 0);
                                            NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, messageObject.messageOwner);
                                            message.location = ((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location;
                                            message.httpLocation = null;
                                            if (message.type == 4) {
                                                SendMessagesHelper.this.performSendDelayedMessage(message, message.messageObjects.indexOf(messageObject));
                                                return;
                                            } else {
                                                SendMessagesHelper.this.performSendDelayedMessage(message);
                                                return;
                                            }
                                        }
                                        FileLog.e("can't load image " + path + " to file " + cacheFile.toString());
                                        message.markAsError();
                                    }
                                });
                            }
                        });
                    } else if (fileType == 1) {
                        cacheFile = new File(FileLoader.getDirectory(4), Utilities.MD5(path) + ".gif");
                        Utilities.globalQueue.postRunnable(new Runnable() {
                            public void run() {
                                boolean z = true;
                                final Document document = message.obj.getDocument();
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
                                        FileLog.e(e);
                                    }
                                    if (document.thumb == null) {
                                        document.thumb = new TL_photoSizeEmpty();
                                        document.thumb.type = "s";
                                    }
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        message.httpLocation = null;
                                        message.obj.messageOwner.attachPath = cacheFile.toString();
                                        message.location = document.thumb.location;
                                        ArrayList messages = new ArrayList();
                                        messages.add(messageObject.messageOwner);
                                        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(messages, false, true, false, 0);
                                        SendMessagesHelper.this.performSendDelayedMessage(message);
                                        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.updateMessageMedia, message.obj.messageOwner);
                                    }
                                });
                            }
                        });
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

    public void cancelSendingMessage(MessageObject object) {
        ArrayList<String> keysToRemove = new ArrayList();
        boolean enc = false;
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
                        MediaController.getInstance(this.currentAccount).cancelVideoConvert(object);
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
                    MediaController.getInstance(this.currentAccount).cancelVideoConvert(message.obj);
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
        ArrayList<Integer> messages2 = new ArrayList();
        messages2.add(Integer.valueOf(object.getId()));
        MessagesController.getInstance(this.currentAccount).deleteMessages(messages2, null, null, object.messageOwner.to_id.channel_id, false);
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean unsent) {
        if (messageObject.getId() >= 0) {
            return false;
        }
        if (messageObject.messageOwner.action instanceof TL_messageEncryptedAction) {
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
        }
        if (messageObject.messageOwner.action instanceof TL_messageActionScreenshotTaken) {
            sendScreenshotMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) messageObject.getDialogId())), messageObject.messageOwner.reply_to_msg_id, messageObject.messageOwner);
        }
        if (unsent) {
            this.unsentMessages.put(Integer.valueOf(messageObject.getId()), messageObject);
        }
        sendMessage(messageObject);
        return true;
    }

    protected void processSentMessage(int id) {
        int prevSize = this.unsentMessages.size();
        this.unsentMessages.remove(Integer.valueOf(id));
        if (prevSize != 0 && this.unsentMessages.size() == 0) {
            checkUnsentMessages();
        }
    }

    public void processForwardFromMyName(MessageObject messageObject, long did) {
        if (messageObject != null) {
            ArrayList<MessageObject> arrayList;
            if (messageObject.messageOwner.media == null || (messageObject.messageOwner.media instanceof TL_messageMediaEmpty) || (messageObject.messageOwner.media instanceof TL_messageMediaWebPage) || (messageObject.messageOwner.media instanceof TL_messageMediaGame) || (messageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                if (messageObject.messageOwner.message != null) {
                    ArrayList entities;
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
                sendMessage((TL_photo) messageObject.messageOwner.media.photo, null, did, messageObject.replyMessageObject, null, null, messageObject.messageOwner.media.ttl_seconds);
            } else if (messageObject.messageOwner.media.document instanceof TL_document) {
                sendMessage((TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, messageObject.replyMessageObject, null, null, messageObject.messageOwner.media.ttl_seconds);
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
            MessageObject newMsgObj = new MessageObject(this.currentAccount, message, null, false);
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
                                newDocument.thumb.w = document.thumb.w;
                                newDocument.thumb.h = document.thumb.h;
                                newDocument.thumb.type = document.thumb.type;
                                newDocument.thumb.bytes = arr;
                            } catch (Throwable e) {
                                FileLog.e(e);
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
            getInstance(this.currentAccount).sendMessage((TL_document) document, null, null, peer, replyingMessageObject, null, null, 0);
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
            final Peer to_id = MessagesController.getInstance(this.currentAccount).getPeer((int) peer);
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
            HashMap<Long, Long> groupsMap = new HashMap();
            ArrayList<MessageObject> objArr = new ArrayList();
            ArrayList<Message> arr = new ArrayList();
            ArrayList<Long> randomIds = new ArrayList();
            ArrayList<Integer> ids = new ArrayList();
            HashMap<Long, Message> messagesByRandomIds = new HashMap();
            InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id);
            int myId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            boolean toMyself = peer == ((long) myId);
            a = 0;
            while (a < messages.size()) {
                MessageObject msgObj = (MessageObject) messages.get(a);
                if (msgObj.getId() > 0 && !msgObj.isSecretPhoto()) {
                    if (canSendStickers || !(msgObj.isSticker() || msgObj.isGif() || msgObj.isGame())) {
                        if (canSendMedia || !((msgObj.messageOwner.media instanceof TL_messageMediaPhoto) || (msgObj.messageOwner.media instanceof TL_messageMediaDocument))) {
                            MessageFwdHeader messageFwdHeader;
                            boolean groupedIdChanged = false;
                            Message newMsg = new TL_message();
                            if (msgObj.isForwarded()) {
                                newMsg.fwd_from = new TL_messageFwdHeader();
                                newMsg.fwd_from.flags = msgObj.messageOwner.fwd_from.flags;
                                newMsg.fwd_from.from_id = msgObj.messageOwner.fwd_from.from_id;
                                newMsg.fwd_from.date = msgObj.messageOwner.fwd_from.date;
                                newMsg.fwd_from.channel_id = msgObj.messageOwner.fwd_from.channel_id;
                                newMsg.fwd_from.channel_post = msgObj.messageOwner.fwd_from.channel_post;
                                newMsg.fwd_from.post_author = msgObj.messageOwner.fwd_from.post_author;
                                newMsg.flags = 4;
                            } else {
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
                                Long gId = (Long) groupsMap.get(Long.valueOf(msgObj.messageOwner.grouped_id));
                                if (gId == null) {
                                    gId = Long.valueOf(Utilities.random.nextLong());
                                    groupsMap.put(Long.valueOf(msgObj.messageOwner.grouped_id), gId);
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
                            messagesByRandomIds.put(Long.valueOf(newMsg.random_id), newMsg);
                            ids.add(Integer.valueOf(newMsg.fwd_msg_id));
                            newMsg.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                            if (!(inputPeer instanceof TL_inputPeerChannel)) {
                                if ((msgObj.messageOwner.flags & 1024) != 0) {
                                    newMsg.views = msgObj.messageOwner.views;
                                    newMsg.flags |= 1024;
                                }
                                newMsg.unread = true;
                            } else if (isMegagroup) {
                                newMsg.unread = true;
                            } else {
                                newMsg.views = 1;
                                newMsg.flags |= 1024;
                            }
                            newMsg.dialog_id = peer;
                            newMsg.to_id = to_id;
                            if (MessageObject.isVoiceMessage(newMsg) || MessageObject.isRoundVideoMessage(newMsg)) {
                                newMsg.media_unread = true;
                            }
                            if (msgObj.messageOwner.to_id instanceof TL_peerChannel) {
                                newMsg.ttl = -msgObj.messageOwner.to_id.channel_id;
                            }
                            MessageObject messageObject = new MessageObject(this.currentAccount, newMsg, null, true);
                            messageObject.messageOwner.send_state = 1;
                            objArr.add(messageObject);
                            arr.add(newMsg);
                            putToSendingMessages(newMsg);
                            if (BuildVars.DEBUG_VERSION) {
                                FileLog.e("forward message user_id = " + inputPeer.user_id + " chat_id = " + inputPeer.chat_id + " channel_id = " + inputPeer.channel_id + " access_hash = " + inputPeer.access_hash);
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
                            final TL_messages_forwardMessages req = new TL_messages_forwardMessages();
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
                            final ArrayList<Message> newMsgObjArr = arr;
                            final ArrayList<MessageObject> newMsgArr = objArr;
                            final HashMap<Long, Message> messagesByRandomIdsFinal = messagesByRandomIds;
                            final boolean isMegagroupFinal = isMegagroup;
                            final long j = peer;
                            final boolean z2 = toMyself;
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, TL_error error) {
                                    int a;
                                    final Message newMsgObj;
                                    if (error == null) {
                                        Update update;
                                        HashMap<Integer, Long> newMessagesByIds = new HashMap();
                                        Updates updates = (Updates) response;
                                        a = 0;
                                        while (a < updates.updates.size()) {
                                            update = (Update) updates.updates.get(a);
                                            if (update instanceof TL_updateMessageID) {
                                                TL_updateMessageID updateMessageID = (TL_updateMessageID) update;
                                                newMessagesByIds.put(Integer.valueOf(updateMessageID.id), Long.valueOf(updateMessageID.random_id));
                                                updates.updates.remove(a);
                                                a--;
                                            }
                                            a++;
                                        }
                                        Integer value = (Integer) MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(j));
                                        if (value == null) {
                                            value = Integer.valueOf(MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getDialogReadMax(true, j));
                                            MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(j), value);
                                        }
                                        int sentCount = 0;
                                        a = 0;
                                        while (a < updates.updates.size()) {
                                            update = (Update) updates.updates.get(a);
                                            if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateNewChannelMessage)) {
                                                Message message;
                                                updates.updates.remove(a);
                                                a--;
                                                if (update instanceof TL_updateNewMessage) {
                                                    message = ((TL_updateNewMessage) update).message;
                                                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewDifferenceParams(-1, update.pts, -1, update.pts_count);
                                                } else {
                                                    message = ((TL_updateNewChannelMessage) update).message;
                                                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewChannelDifferenceParams(update.pts, update.pts_count, message.to_id.channel_id);
                                                    if (isMegagroupFinal) {
                                                        message.flags |= Integer.MIN_VALUE;
                                                    }
                                                }
                                                message.unread = value.intValue() < message.id;
                                                if (z2) {
                                                    message.out = true;
                                                    message.unread = false;
                                                    message.media_unread = false;
                                                }
                                                Long random_id = (Long) newMessagesByIds.get(Integer.valueOf(message.id));
                                                if (random_id != null) {
                                                    newMsgObj = (Message) messagesByRandomIdsFinal.get(random_id);
                                                    if (newMsgObj != null) {
                                                        int index = newMsgObjArr.indexOf(newMsgObj);
                                                        if (index != -1) {
                                                            MessageObject msgObj = (MessageObject) newMsgArr.get(index);
                                                            newMsgObjArr.remove(index);
                                                            newMsgArr.remove(index);
                                                            final int oldId = newMsgObj.id;
                                                            final ArrayList<Message> sentMessages = new ArrayList();
                                                            sentMessages.add(message);
                                                            newMsgObj.id = message.id;
                                                            sentCount++;
                                                            SendMessagesHelper.this.updateMediaPaths(msgObj, message, null, true);
                                                            MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                                                                public void run() {
                                                                    MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(oldId), newMsgObj.id, 0, false, to_id.channel_id);
                                                                    MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(sentMessages, true, false, false, 0);
                                                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                                                        public void run() {
                                                                            newMsgObj.send_state = 0;
                                                                            DataQuery.getInstance(SendMessagesHelper.this.currentAccount).increasePeerRaiting(j);
                                                                            NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(message.id), message, Long.valueOf(j));
                                                                            SendMessagesHelper.this.processSentMessage(oldId);
                                                                            SendMessagesHelper.this.removeFromSendingMessages(oldId);
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                            a++;
                                        }
                                        if (!updates.updates.isEmpty()) {
                                            MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates(updates, false);
                                        }
                                        StatsController.getInstance(SendMessagesHelper.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, sentCount);
                                    } else {
                                        final TL_error tL_error = error;
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                AlertsCreator.processError(SendMessagesHelper.this.currentAccount, tL_error, null, req, new Object[0]);
                                            }
                                        });
                                    }
                                    for (a = 0; a < newMsgObjArr.size(); a++) {
                                        newMsgObj = (Message) newMsgObjArr.get(a);
                                        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(newMsgObj);
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                newMsgObj.send_state = 2;
                                                NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
                                                SendMessagesHelper.this.processSentMessage(newMsgObj.id);
                                                SendMessagesHelper.this.removeFromSendingMessages(newMsgObj.id);
                                            }
                                        });
                                    }
                                }
                            }, 68);
                            if (a != messages.size() - 1) {
                                objArr = new ArrayList();
                                arr = new ArrayList();
                                randomIds = new ArrayList();
                                ids = new ArrayList();
                                messagesByRandomIds = new HashMap();
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

    public int editMessage(MessageObject messageObject, String message, boolean searchLinks, final BaseFragment fragment, ArrayList<MessageEntity> entities, final Runnable callback) {
        boolean z = false;
        if (fragment == null || fragment.getParentActivity() == null || callback == null) {
            return 0;
        }
        final TL_messages_editMessage req = new TL_messages_editMessage();
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
        return ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        callback.run();
                    }
                });
                if (error == null) {
                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates((Updates) response, false);
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            AlertsCreator.processError(SendMessagesHelper.this.currentAccount, error, fragment, req, new Object[0]);
                        }
                    });
                }
            }
        });
    }

    private void sendLocation(Location location) {
        MessageMedia mediaGeo = new TL_messageMediaGeo();
        mediaGeo.geo = new TL_geoPoint();
        mediaGeo.geo.lat = location.getLatitude();
        mediaGeo.geo._long = location.getLongitude();
        for (Entry<String, MessageObject> entry : this.waitingForLocation.entrySet()) {
            MessageObject messageObject = (MessageObject) entry.getValue();
            getInstance(this.currentAccount).sendMessage(mediaGeo, messageObject.getDialogId(), messageObject, null, null);
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
            final String key = messageObject.getDialogId() + "_" + messageObject.getId() + "_" + Utilities.bytesToHex(button.data) + "_" + type;
            this.waitingForCallback.put(key, messageObject);
            final MessageObject messageObject2 = messageObject;
            final KeyboardButton keyboardButton = button;
            final ChatActivity chatActivity = parentFragment;
            RequestDelegate requestDelegate = new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            SendMessagesHelper.this.waitingForCallback.remove(key);
                            if (cacheFinal && response == null) {
                                SendMessagesHelper.this.sendCallback(false, messageObject2, keyboardButton, chatActivity);
                            } else if (response == null) {
                            } else {
                                if (!(keyboardButton instanceof TL_keyboardButtonBuy)) {
                                    TL_messages_botCallbackAnswer res = response;
                                    if (!(cacheFinal || res.cache_time == 0)) {
                                        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).saveBotCache(key, res);
                                    }
                                    int uid;
                                    User user;
                                    if (res.message != null) {
                                        if (!res.alert) {
                                            uid = messageObject2.messageOwner.from_id;
                                            if (messageObject2.messageOwner.via_bot_id != 0) {
                                                uid = messageObject2.messageOwner.via_bot_id;
                                            }
                                            String name = null;
                                            if (uid > 0) {
                                                user = MessagesController.getInstance(SendMessagesHelper.this.currentAccount).getUser(Integer.valueOf(uid));
                                                if (user != null) {
                                                    name = ContactsController.formatName(user.first_name, user.last_name);
                                                }
                                            } else {
                                                Chat chat = MessagesController.getInstance(SendMessagesHelper.this.currentAccount).getChat(Integer.valueOf(-uid));
                                                if (chat != null) {
                                                    name = chat.title;
                                                }
                                            }
                                            if (name == null) {
                                                name = "bot";
                                            }
                                            chatActivity.showAlert(name, res.message);
                                        } else if (chatActivity.getParentActivity() != null) {
                                            Builder builder = new Builder(chatActivity.getParentActivity());
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                                            builder.setMessage(res.message);
                                            chatActivity.showDialog(builder.create());
                                        }
                                    } else if (res.url != null && chatActivity.getParentActivity() != null) {
                                        uid = messageObject2.messageOwner.from_id;
                                        if (messageObject2.messageOwner.via_bot_id != 0) {
                                            uid = messageObject2.messageOwner.via_bot_id;
                                        }
                                        user = MessagesController.getInstance(SendMessagesHelper.this.currentAccount).getUser(Integer.valueOf(uid));
                                        boolean verified = user != null && user.verified;
                                        if (keyboardButton instanceof TL_keyboardButtonGame) {
                                            TL_game game = messageObject2.messageOwner.media instanceof TL_messageMediaGame ? messageObject2.messageOwner.media.game : null;
                                            if (game != null) {
                                                boolean z;
                                                ChatActivity chatActivity = chatActivity;
                                                MessageObject messageObject = messageObject2;
                                                String str = res.url;
                                                if (verified || !MessagesController.getNotificationsSettings(SendMessagesHelper.this.currentAccount).getBoolean("askgame_" + uid, true)) {
                                                    z = false;
                                                } else {
                                                    z = true;
                                                }
                                                chatActivity.showOpenGameAlert(game, messageObject, str, z, uid);
                                                return;
                                            }
                                            return;
                                        }
                                        chatActivity.showOpenUrlAlert(res.url, false);
                                    }
                                } else if (response instanceof TL_payments_paymentForm) {
                                    TL_payments_paymentForm form = response;
                                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putUsers(form.users, false);
                                    chatActivity.presentFragment(new PaymentFormActivity(form, messageObject2));
                                } else if (response instanceof TL_payments_paymentReceipt) {
                                    chatActivity.presentFragment(new PaymentFormActivity(messageObject2, (TL_payments_paymentReceipt) response));
                                }
                            }
                        }
                    });
                }
            };
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
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                if (error == null) {
                                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates((Updates) response, false);
                                }
                                if (newTaskId != 0) {
                                    MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).removePendingTask(newTaskId);
                                }
                            }
                        });
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e(e);
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, /* anonymous class already generated */);
                }
                newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
            } else {
                newTaskId = taskId;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, /* anonymous class already generated */);
        }
    }

    public void sendMessage(MessageObject retryMessageObject) {
        sendMessage(null, null, null, null, null, null, null, retryMessageObject.getDialogId(), retryMessageObject.messageOwner.attachPath, null, null, true, retryMessageObject, null, retryMessageObject.messageOwner.reply_markup, retryMessageObject.messageOwner.params, 0);
    }

    public void sendMessage(User user, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, user, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params, 0);
    }

    public void sendMessage(TL_document document, VideoEditedInfo videoEditedInfo, String path, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl) {
        sendMessage(null, null, null, videoEditedInfo, null, document, null, peer, path, reply_to_msg, null, true, null, null, replyMarkup, params, ttl);
    }

    public void sendMessage(String message, long peer, MessageObject reply_to_msg, WebPage webPage, boolean searchLinks, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(message, null, null, null, null, null, null, peer, null, reply_to_msg, webPage, searchLinks, null, entities, replyMarkup, params, 0);
    }

    public void sendMessage(MessageMedia location, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, location, null, null, null, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params, 0);
    }

    public void sendMessage(TL_game game, long peer, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, null, null, game, peer, null, null, null, true, null, null, replyMarkup, params, 0);
    }

    public void sendMessage(TL_photo photo, String path, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl) {
        sendMessage(null, null, photo, null, null, null, null, peer, path, reply_to_msg, null, true, null, null, replyMarkup, params, ttl);
    }

    private void sendMessage(String message, MessageMedia location, TL_photo photo, VideoEditedInfo videoEditedInfo, User user, TL_document document, TL_game game, long peer, String path, MessageObject reply_to_msg, WebPage webPage, boolean searchLinks, MessageObject retryMessageObject, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params, int ttl) {
        Throwable e;
        if (peer != 0) {
            Chat chat;
            MessageObject newMsgObj;
            int a;
            DocumentAttribute attribute;
            long groupId;
            DelayedMessage delayedMessage;
            DelayedMessage delayedMessage2;
            DelayedMessage delayedMessage3;
            String originalPath = null;
            if (params != null) {
                if (params.containsKey("originalPath")) {
                    originalPath = (String) params.get("originalPath");
                }
            }
            Message newMsg = null;
            int type = -1;
            int lower_id = (int) peer;
            int high_id = (int) (peer >> 32);
            boolean isChannel = false;
            EncryptedChat encryptedChat = null;
            InputPeer sendToPeer = lower_id != 0 ? MessagesController.getInstance(this.currentAccount).getInputPeer(lower_id) : null;
            ArrayList<InputUser> sendToPeers = null;
            if (lower_id == 0) {
                encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat == null) {
                    if (retryMessageObject != null) {
                        MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(retryMessageObject.messageOwner);
                        retryMessageObject.messageOwner.send_state = 2;
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(retryMessageObject.getId()));
                        processSentMessage(retryMessageObject.getId());
                        return;
                    }
                    return;
                }
            } else if (sendToPeer instanceof TL_inputPeerChannel) {
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(sendToPeer.channel_id));
                isChannel = (chat == null || chat.megagroup) ? false : true;
            }
            if (retryMessageObject != null) {
                try {
                    newMsg = retryMessageObject.messageOwner;
                    if (retryMessageObject.isForwarded()) {
                        type = 4;
                    } else {
                        if (retryMessageObject.type == 0) {
                            if (!(retryMessageObject.messageOwner.media instanceof TL_messageMediaGame)) {
                                message = newMsg.message;
                            }
                            type = 0;
                        } else if (retryMessageObject.type == 4) {
                            location = newMsg.media;
                            type = 1;
                        } else if (retryMessageObject.type == 1) {
                            photo = (TL_photo) newMsg.media.photo;
                            type = 2;
                        } else if (retryMessageObject.type == 3 || retryMessageObject.type == 5 || videoEditedInfo != null) {
                            type = 3;
                            document = (TL_document) newMsg.media.document;
                        } else if (retryMessageObject.type == 12) {
                            User user2 = new TL_userRequest_old2();
                            try {
                                user2.phone = newMsg.media.phone_number;
                                user2.first_name = newMsg.media.first_name;
                                user2.last_name = newMsg.media.last_name;
                                user2.id = newMsg.media.user_id;
                                type = 6;
                                user = user2;
                            } catch (Exception e2) {
                                e = e2;
                                newMsgObj = null;
                                user = user2;
                                FileLog.e(e);
                                MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsg);
                                if (newMsgObj != null) {
                                    newMsgObj.messageOwner.send_state = 2;
                                }
                                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                                processSentMessage(newMsg.id);
                            }
                        } else if (retryMessageObject.type == 8 || retryMessageObject.type == 9 || retryMessageObject.type == 13 || retryMessageObject.type == 14) {
                            document = (TL_document) newMsg.media.document;
                            type = 7;
                        } else if (retryMessageObject.type == 2) {
                            document = (TL_document) newMsg.media.document;
                            type = 8;
                        }
                        if (params != null) {
                            if (params.containsKey("query_id")) {
                                type = 9;
                            }
                        }
                    }
                } catch (Exception e3) {
                    e = e3;
                    newMsgObj = null;
                    FileLog.e(e);
                    MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsg);
                    if (newMsgObj != null) {
                        newMsgObj.messageOwner.send_state = 2;
                    }
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                    processSentMessage(newMsg.id);
                }
            }
            if (message != null) {
                if (encryptedChat != null) {
                    newMsg = new TL_message_secret();
                } else {
                    newMsg = new TL_message();
                }
                if (!(entities == null || entities.isEmpty())) {
                    newMsg.entities = entities;
                }
                if (encryptedChat != null && (webPage instanceof TL_webPagePending)) {
                    if (webPage.url != null) {
                        WebPage newWebPage = new TL_webPageUrlPending();
                        newWebPage.url = webPage.url;
                        webPage = newWebPage;
                    } else {
                        webPage = null;
                    }
                }
                if (webPage == null) {
                    newMsg.media = new TL_messageMediaEmpty();
                } else {
                    newMsg.media = new TL_messageMediaWebPage();
                    newMsg.media.webpage = webPage;
                }
                if (params != null) {
                    if (params.containsKey("query_id")) {
                        type = 9;
                        newMsg.message = message;
                    }
                }
                type = 0;
                newMsg.message = message;
            } else if (location != null) {
                if (encryptedChat != null) {
                    newMsg = new TL_message_secret();
                } else {
                    newMsg = new TL_message();
                }
                newMsg.media = location;
                newMsg.message = TtmlNode.ANONYMOUS_REGION_ID;
                if (params != null) {
                    if (params.containsKey("query_id")) {
                        type = 9;
                    }
                }
                type = 1;
            } else if (photo != null) {
                if (encryptedChat != null) {
                    newMsg = new TL_message_secret();
                } else {
                    newMsg = new TL_message();
                }
                newMsg.media = new TL_messageMediaPhoto();
                r4 = newMsg.media;
                r4.flags |= 3;
                newMsg.media.caption = photo.caption != null ? photo.caption : TtmlNode.ANONYMOUS_REGION_ID;
                if (ttl != 0) {
                    newMsg.media.ttl_seconds = ttl;
                    newMsg.ttl = ttl;
                    r4 = newMsg.media;
                    r4.flags |= 4;
                }
                newMsg.media.photo = photo;
                if (params != null) {
                    if (params.containsKey("query_id")) {
                        type = 9;
                        newMsg.message = "-1";
                        if (path != null && path.length() > 0) {
                            if (path.startsWith("http")) {
                                newMsg.attachPath = path;
                            }
                        }
                        newMsg.attachPath = FileLoader.getPathToAttach(((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location, true).toString();
                    }
                }
                type = 2;
                newMsg.message = "-1";
                if (path.startsWith("http")) {
                    newMsg.attachPath = path;
                }
                newMsg.attachPath = FileLoader.getPathToAttach(((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location, true).toString();
            } else if (game != null) {
                Message newMsg2 = new TL_message();
                try {
                    newMsg2.media = new TL_messageMediaGame();
                    newMsg2.media.caption = TtmlNode.ANONYMOUS_REGION_ID;
                    newMsg2.media.game = game;
                    newMsg2.message = TtmlNode.ANONYMOUS_REGION_ID;
                    if (params != null) {
                        if (params.containsKey("query_id")) {
                            type = 9;
                            newMsg = newMsg2;
                        }
                    }
                    newMsg = newMsg2;
                } catch (Exception e4) {
                    e = e4;
                    newMsgObj = null;
                    newMsg = newMsg2;
                    FileLog.e(e);
                    MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsg);
                    if (newMsgObj != null) {
                        newMsgObj.messageOwner.send_state = 2;
                    }
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                    processSentMessage(newMsg.id);
                }
            } else if (user != null) {
                String str;
                if (encryptedChat != null) {
                    newMsg = new TL_message_secret();
                } else {
                    newMsg = new TL_message();
                }
                newMsg.media = new TL_messageMediaContact();
                newMsg.media.phone_number = user.phone;
                newMsg.media.first_name = user.first_name;
                newMsg.media.last_name = user.last_name;
                newMsg.media.user_id = user.id;
                if (newMsg.media.first_name == null) {
                    r4 = newMsg.media;
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    r4.first_name = str;
                    user.first_name = str;
                }
                if (newMsg.media.last_name == null) {
                    r4 = newMsg.media;
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                    r4.last_name = str;
                    user.last_name = str;
                }
                newMsg.message = TtmlNode.ANONYMOUS_REGION_ID;
                if (params != null) {
                    if (params.containsKey("query_id")) {
                        type = 9;
                    }
                }
                type = 6;
            } else if (document != null) {
                TL_documentAttributeSticker_layer55 attributeSticker;
                String name;
                if (encryptedChat != null) {
                    newMsg = new TL_message_secret();
                } else {
                    newMsg = new TL_message();
                }
                newMsg.media = new TL_messageMediaDocument();
                r4 = newMsg.media;
                r4.flags |= 3;
                if (ttl != 0) {
                    newMsg.media.ttl_seconds = ttl;
                    newMsg.ttl = ttl;
                    r4 = newMsg.media;
                    r4.flags |= 4;
                }
                newMsg.media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                newMsg.media.document = document;
                if (params != null) {
                    if (params.containsKey("query_id")) {
                        type = 9;
                        if (videoEditedInfo == null) {
                            newMsg.message = "-1";
                        } else {
                            newMsg.message = videoEditedInfo.getString();
                        }
                        if (encryptedChat != null || document.dc_id <= 0 || MessageObject.isStickerDocument(document)) {
                            newMsg.attachPath = path;
                        } else {
                            newMsg.attachPath = FileLoader.getPathToAttach(document).toString();
                        }
                        if (encryptedChat != null && MessageObject.isStickerDocument(document)) {
                            a = 0;
                            while (a < document.attributes.size()) {
                                attribute = (DocumentAttribute) document.attributes.get(a);
                                if (attribute instanceof TL_documentAttributeSticker) {
                                    document.attributes.remove(a);
                                    attributeSticker = new TL_documentAttributeSticker_layer55();
                                    document.attributes.add(attributeSticker);
                                    attributeSticker.alt = attribute.alt;
                                    if (attribute.stickerset != null) {
                                        if (attribute.stickerset instanceof TL_inputStickerSetShortName) {
                                            name = attribute.stickerset.short_name;
                                        } else {
                                            name = DataQuery.getInstance(this.currentAccount).getStickerSetName(attribute.stickerset.id);
                                        }
                                        if (TextUtils.isEmpty(name)) {
                                            attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                        } else {
                                            attributeSticker.stickerset = new TL_inputStickerSetShortName();
                                            attributeSticker.stickerset.short_name = name;
                                        }
                                    } else {
                                        attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                    }
                                } else {
                                    a++;
                                }
                            }
                        }
                    }
                }
                if (MessageObject.isVideoDocument(document) || MessageObject.isRoundVideoDocument(document) || videoEditedInfo != null) {
                    type = 3;
                    if (videoEditedInfo == null) {
                        newMsg.message = videoEditedInfo.getString();
                    } else {
                        newMsg.message = "-1";
                    }
                    if (encryptedChat != null) {
                    }
                    newMsg.attachPath = path;
                    a = 0;
                    while (a < document.attributes.size()) {
                        attribute = (DocumentAttribute) document.attributes.get(a);
                        if (attribute instanceof TL_documentAttributeSticker) {
                            a++;
                        } else {
                            document.attributes.remove(a);
                            attributeSticker = new TL_documentAttributeSticker_layer55();
                            document.attributes.add(attributeSticker);
                            attributeSticker.alt = attribute.alt;
                            if (attribute.stickerset != null) {
                                attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                            } else {
                                if (attribute.stickerset instanceof TL_inputStickerSetShortName) {
                                    name = DataQuery.getInstance(this.currentAccount).getStickerSetName(attribute.stickerset.id);
                                } else {
                                    name = attribute.stickerset.short_name;
                                }
                                if (TextUtils.isEmpty(name)) {
                                    attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                } else {
                                    attributeSticker.stickerset = new TL_inputStickerSetShortName();
                                    attributeSticker.stickerset.short_name = name;
                                }
                            }
                        }
                    }
                } else {
                    if (MessageObject.isVoiceDocument(document)) {
                        type = 8;
                    } else {
                        type = 7;
                    }
                    if (videoEditedInfo == null) {
                        newMsg.message = "-1";
                    } else {
                        newMsg.message = videoEditedInfo.getString();
                    }
                    if (encryptedChat != null) {
                    }
                    newMsg.attachPath = path;
                    a = 0;
                    while (a < document.attributes.size()) {
                        attribute = (DocumentAttribute) document.attributes.get(a);
                        if (attribute instanceof TL_documentAttributeSticker) {
                            document.attributes.remove(a);
                            attributeSticker = new TL_documentAttributeSticker_layer55();
                            document.attributes.add(attributeSticker);
                            attributeSticker.alt = attribute.alt;
                            if (attribute.stickerset != null) {
                                if (attribute.stickerset instanceof TL_inputStickerSetShortName) {
                                    name = attribute.stickerset.short_name;
                                } else {
                                    name = DataQuery.getInstance(this.currentAccount).getStickerSetName(attribute.stickerset.id);
                                }
                                if (TextUtils.isEmpty(name)) {
                                    attributeSticker.stickerset = new TL_inputStickerSetShortName();
                                    attributeSticker.stickerset.short_name = name;
                                } else {
                                    attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                }
                            } else {
                                attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                            }
                        } else {
                            a++;
                        }
                    }
                }
            }
            if (newMsg.attachPath == null) {
                newMsg.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            }
            int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
            newMsg.id = newMessageId;
            newMsg.local_id = newMessageId;
            newMsg.out = true;
            if (!isChannel || sendToPeer == null) {
                newMsg.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                newMsg.flags |= 256;
            } else {
                newMsg.from_id = -sendToPeer.channel_id;
            }
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            if (newMsg.random_id == 0) {
                newMsg.random_id = getNextRandomId();
            }
            if (params != null) {
                if (params.containsKey("bot")) {
                    if (encryptedChat != null) {
                        newMsg.via_bot_name = (String) params.get("bot_name");
                        if (newMsg.via_bot_name == null) {
                            newMsg.via_bot_name = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                    } else {
                        newMsg.via_bot_id = Utilities.parseInt((String) params.get("bot")).intValue();
                    }
                    newMsg.flags |= 2048;
                }
            }
            newMsg.params = params;
            if (retryMessageObject == null || !retryMessageObject.resendAsIs) {
                newMsg.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                if (sendToPeer instanceof TL_inputPeerChannel) {
                    if (isChannel) {
                        newMsg.views = 1;
                        newMsg.flags |= 1024;
                    }
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(sendToPeer.channel_id));
                    if (chat != null) {
                        if (chat.megagroup) {
                            newMsg.flags |= Integer.MIN_VALUE;
                            newMsg.unread = true;
                        } else {
                            newMsg.post = true;
                            if (chat.signatures) {
                                newMsg.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                            }
                        }
                    }
                } else {
                    newMsg.unread = true;
                }
            }
            newMsg.flags |= 512;
            newMsg.dialog_id = peer;
            if (reply_to_msg != null) {
                if (encryptedChat == null || reply_to_msg.messageOwner.random_id == 0) {
                    newMsg.flags |= 8;
                } else {
                    newMsg.reply_to_random_id = reply_to_msg.messageOwner.random_id;
                    newMsg.flags |= 8;
                }
                newMsg.reply_to_msg_id = reply_to_msg.getId();
            }
            if (replyMarkup != null && encryptedChat == null) {
                newMsg.flags |= 64;
                newMsg.reply_markup = replyMarkup;
            }
            if (lower_id == 0) {
                newMsg.to_id = new TL_peerUser();
                if (encryptedChat.participant_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    newMsg.to_id.user_id = encryptedChat.admin_id;
                } else {
                    newMsg.to_id.user_id = encryptedChat.participant_id;
                }
                if (ttl != 0) {
                    newMsg.ttl = ttl;
                } else {
                    newMsg.ttl = encryptedChat.ttl;
                }
                if (!(newMsg.ttl == 0 || newMsg.media.document == null)) {
                    int duration;
                    if (MessageObject.isVoiceMessage(newMsg)) {
                        duration = 0;
                        for (a = 0; a < newMsg.media.document.attributes.size(); a++) {
                            attribute = (DocumentAttribute) newMsg.media.document.attributes.get(a);
                            if (attribute instanceof TL_documentAttributeAudio) {
                                duration = attribute.duration;
                                break;
                            }
                        }
                        newMsg.ttl = Math.max(newMsg.ttl, duration + 1);
                    } else if (MessageObject.isVideoMessage(newMsg) || MessageObject.isRoundVideoMessage(newMsg)) {
                        duration = 0;
                        for (a = 0; a < newMsg.media.document.attributes.size(); a++) {
                            attribute = (DocumentAttribute) newMsg.media.document.attributes.get(a);
                            if (attribute instanceof TL_documentAttributeVideo) {
                                duration = attribute.duration;
                                break;
                            }
                        }
                        newMsg.ttl = Math.max(newMsg.ttl, duration + 1);
                    }
                }
            } else if (high_id != 1) {
                newMsg.to_id = MessagesController.getInstance(this.currentAccount).getPeer(lower_id);
                if (lower_id > 0) {
                    User sendToUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_id));
                    if (sendToUser == null) {
                        processSentMessage(newMsg.id);
                        return;
                    } else if (sendToUser.bot) {
                        newMsg.unread = false;
                    }
                }
            } else if (this.currentChatInfo == null) {
                MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsg);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                processSentMessage(newMsg.id);
                return;
            } else {
                ArrayList<InputUser> sendToPeers2 = new ArrayList();
                try {
                    Iterator it = this.currentChatInfo.participants.participants.iterator();
                    while (it.hasNext()) {
                        InputUser peerUser = MessagesController.getInstance(this.currentAccount).getInputUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) it.next()).user_id)));
                        if (peerUser != null) {
                            sendToPeers2.add(peerUser);
                        }
                    }
                    newMsg.to_id = new TL_peerChat();
                    newMsg.to_id.chat_id = lower_id;
                    sendToPeers = sendToPeers2;
                } catch (Exception e5) {
                    e = e5;
                    sendToPeers = sendToPeers2;
                    newMsgObj = null;
                    FileLog.e(e);
                    MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsg);
                    if (newMsgObj != null) {
                        newMsgObj.messageOwner.send_state = 2;
                    }
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                    processSentMessage(newMsg.id);
                }
            }
            if (high_id != 1) {
                if (MessageObject.isVoiceMessage(newMsg) || MessageObject.isRoundVideoMessage(newMsg)) {
                    newMsg.media_unread = true;
                }
            }
            newMsg.send_state = 1;
            newMsgObj = new MessageObject(this.currentAccount, newMsg, null, true);
            try {
                newMsgObj.replyMessageObject = reply_to_msg;
                if (!newMsgObj.isForwarded() && ((newMsgObj.type == 3 || videoEditedInfo != null || newMsgObj.type == 2) && !TextUtils.isEmpty(newMsg.attachPath))) {
                    newMsgObj.attachPathExists = true;
                }
                groupId = 0;
                boolean isFinalGroupMedia = false;
                if (params != null) {
                    String groupIdStr = (String) params.get("groupId");
                    if (groupIdStr != null) {
                        groupId = Utilities.parseLong(groupIdStr).longValue();
                        newMsg.grouped_id = groupId;
                        newMsg.flags |= 131072;
                    }
                    isFinalGroupMedia = params.get("final") != null;
                }
                if (groupId == 0) {
                    ArrayList<MessageObject> objArr = new ArrayList();
                    objArr.add(newMsgObj);
                    ArrayList arr = new ArrayList();
                    arr.add(newMsg);
                    MessagesStorage.getInstance(this.currentAccount).putMessages(arr, false, true, false, 0);
                    MessagesController.getInstance(this.currentAccount).updateInterfaceWithMessages(peer, objArr);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    delayedMessage = null;
                } else {
                    ArrayList<DelayedMessage> arrayList = (ArrayList) this.delayedMessages.get("group_" + groupId);
                    if (arrayList != null) {
                        delayedMessage = (DelayedMessage) arrayList.get(0);
                    } else {
                        delayedMessage = null;
                    }
                    if (delayedMessage == null) {
                        delayedMessage2 = new DelayedMessage(peer);
                        delayedMessage2.type = 4;
                        delayedMessage2.groupId = groupId;
                        delayedMessage2.messageObjects = new ArrayList();
                        delayedMessage2.messages = new ArrayList();
                        delayedMessage2.originalPaths = new ArrayList();
                        delayedMessage2.extraHashMap = new HashMap();
                        delayedMessage2.encryptedChat = encryptedChat;
                    } else {
                        delayedMessage3 = delayedMessage;
                    }
                    if (isFinalGroupMedia) {
                        delayedMessage3.finalGroupMessage = newMsg.id;
                    }
                    delayedMessage = delayedMessage3;
                }
            } catch (Exception e6) {
                e = e6;
                FileLog.e(e);
                MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsg);
                if (newMsgObj != null) {
                    newMsgObj.messageOwner.send_state = 2;
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                processSentMessage(newMsg.id);
            }
            try {
                if (BuildVars.DEBUG_VERSION && sendToPeer != null) {
                    FileLog.e("send message user_id = " + sendToPeer.user_id + " chat_id = " + sendToPeer.chat_id + " channel_id = " + sendToPeer.channel_id + " access_hash = " + sendToPeer.access_hash);
                }
                TL_decryptedMessage reqSend;
                ArrayList<Long> random_ids;
                if (type == 0 || !(type != 9 || message == null || encryptedChat == null)) {
                    if (encryptedChat != null) {
                        if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 73) {
                            reqSend = new TL_decryptedMessage();
                        } else {
                            reqSend = new TL_decryptedMessage_layer45();
                        }
                        reqSend.ttl = newMsg.ttl;
                        if (!(entities == null || entities.isEmpty())) {
                            reqSend.entities = entities;
                            reqSend.flags |= 128;
                        }
                        if (!(reply_to_msg == null || reply_to_msg.messageOwner.random_id == 0)) {
                            reqSend.reply_to_random_id = reply_to_msg.messageOwner.random_id;
                            reqSend.flags |= 8;
                        }
                        if (params != null) {
                            if (params.get("bot_name") != null) {
                                reqSend.via_bot_name = (String) params.get("bot_name");
                                reqSend.flags |= 2048;
                            }
                        }
                        reqSend.random_id = newMsg.random_id;
                        reqSend.message = message;
                        if (webPage == null || webPage.url == null) {
                            reqSend.media = new TL_decryptedMessageMediaEmpty();
                        } else {
                            reqSend.media = new TL_decryptedMessageMediaWebPage();
                            reqSend.media.url = webPage.url;
                            reqSend.flags |= 512;
                        }
                        SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(reqSend, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                        if (retryMessageObject == null) {
                            DataQuery.getInstance(this.currentAccount).cleanDraft(peer, false);
                        }
                        delayedMessage3 = delayedMessage;
                    } else if (sendToPeers != null) {
                        TL_messages_sendBroadcast reqSend2 = new TL_messages_sendBroadcast();
                        random_ids = new ArrayList();
                        for (a = 0; a < sendToPeers.size(); a++) {
                            random_ids.add(Long.valueOf(Utilities.random.nextLong()));
                        }
                        reqSend2.message = message;
                        reqSend2.contacts = sendToPeers;
                        reqSend2.media = new TL_inputMediaEmpty();
                        reqSend2.random_id = random_ids;
                        performSendMessageRequest(reqSend2, newMsgObj, null);
                        delayedMessage3 = delayedMessage;
                    } else {
                        TL_messages_sendMessage reqSend3 = new TL_messages_sendMessage();
                        reqSend3.message = message;
                        reqSend3.clear_draft = retryMessageObject == null;
                        if (newMsg.to_id instanceof TL_peerChannel) {
                            reqSend3.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + peer, false);
                        }
                        reqSend3.peer = sendToPeer;
                        reqSend3.random_id = newMsg.random_id;
                        if (reply_to_msg != null) {
                            reqSend3.flags |= 1;
                            reqSend3.reply_to_msg_id = reply_to_msg.getId();
                        }
                        if (!searchLinks) {
                            reqSend3.no_webpage = true;
                        }
                        if (!(entities == null || entities.isEmpty())) {
                            reqSend3.entities = entities;
                            reqSend3.flags |= 8;
                        }
                        performSendMessageRequest(reqSend3, newMsgObj, null);
                        if (retryMessageObject == null) {
                            DataQuery.getInstance(this.currentAccount).cleanDraft(peer, false);
                        }
                        delayedMessage3 = delayedMessage;
                    }
                } else if ((type < 1 || type > 3) && ((type < 5 || type > 8) && (type != 9 || encryptedChat == null))) {
                    if (type == 4) {
                        TL_messages_forwardMessages reqSend4 = new TL_messages_forwardMessages();
                        reqSend4.to_peer = sendToPeer;
                        reqSend4.with_my_score = retryMessageObject.messageOwner.with_my_score;
                        if (retryMessageObject.messageOwner.ttl != 0) {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-retryMessageObject.messageOwner.ttl));
                            reqSend4.from_peer = new TL_inputPeerChannel();
                            reqSend4.from_peer.channel_id = -retryMessageObject.messageOwner.ttl;
                            if (chat != null) {
                                reqSend4.from_peer.access_hash = chat.access_hash;
                            }
                        } else {
                            reqSend4.from_peer = new TL_inputPeerEmpty();
                        }
                        if (retryMessageObject.messageOwner.to_id instanceof TL_peerChannel) {
                            reqSend4.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + peer, false);
                        }
                        reqSend4.random_id.add(Long.valueOf(newMsg.random_id));
                        if (retryMessageObject.getId() >= 0) {
                            reqSend4.id.add(Integer.valueOf(retryMessageObject.getId()));
                        } else if (retryMessageObject.messageOwner.fwd_msg_id != 0) {
                            reqSend4.id.add(Integer.valueOf(retryMessageObject.messageOwner.fwd_msg_id));
                        } else if (retryMessageObject.messageOwner.fwd_from != null) {
                            reqSend4.id.add(Integer.valueOf(retryMessageObject.messageOwner.fwd_from.channel_post));
                        }
                        performSendMessageRequest(reqSend4, newMsgObj, null);
                        delayedMessage3 = delayedMessage;
                        return;
                    }
                    if (type == 9) {
                        reqSend = new TL_messages_sendInlineBotResult();
                        reqSend.peer = sendToPeer;
                        reqSend.random_id = newMsg.random_id;
                        if (reply_to_msg != null) {
                            reqSend.flags |= 1;
                            reqSend.reply_to_msg_id = reply_to_msg.getId();
                        }
                        if (newMsg.to_id instanceof TL_peerChannel) {
                            reqSend.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + peer, false);
                        }
                        reqSend.query_id = Utilities.parseLong((String) params.get("query_id")).longValue();
                        reqSend.id = (String) params.get(TtmlNode.ATTR_ID);
                        if (retryMessageObject == null) {
                            reqSend.clear_draft = true;
                            DataQuery.getInstance(this.currentAccount).cleanDraft(peer, false);
                        }
                        performSendMessageRequest(reqSend, newMsgObj, null);
                    }
                    delayedMessage3 = delayedMessage;
                } else if (encryptedChat == null) {
                    TLObject reqSend5;
                    InputMedia inputMedia = null;
                    if (type == 1) {
                        if (location instanceof TL_messageMediaVenue) {
                            inputMedia = new TL_inputMediaVenue();
                            inputMedia.address = location.address;
                            inputMedia.title = location.title;
                            inputMedia.provider = location.provider;
                            inputMedia.venue_id = location.venue_id;
                            inputMedia.venue_type = TtmlNode.ANONYMOUS_REGION_ID;
                        } else if (location instanceof TL_messageMediaGeoLive) {
                            inputMedia = new TL_inputMediaGeoLive();
                            inputMedia.period = location.period;
                        } else {
                            inputMedia = new TL_inputMediaGeoPoint();
                        }
                        inputMedia.geo_point = new TL_inputGeoPoint();
                        inputMedia.geo_point.lat = location.geo.lat;
                        inputMedia.geo_point._long = location.geo._long;
                        delayedMessage3 = delayedMessage;
                    } else if (type == 2 || (type == 9 && photo != null)) {
                        if (photo.access_hash == 0) {
                            inputMedia = new TL_inputMediaUploadedPhoto();
                            inputMedia.caption = photo.caption != null ? photo.caption : TtmlNode.ANONYMOUS_REGION_ID;
                            if (ttl != 0) {
                                inputMedia.ttl_seconds = ttl;
                                newMsg.ttl = ttl;
                                inputMedia.flags |= 2;
                            }
                            if (params != null) {
                                String masks = (String) params.get("masks");
                                if (masks != null) {
                                    AbstractSerializedData serializedData = new SerializedData(Utilities.hexToBytes(masks));
                                    int count = serializedData.readInt32(false);
                                    for (a = 0; a < count; a++) {
                                        inputMedia.stickers.add(InputDocument.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                                    }
                                    inputMedia.flags |= 1;
                                }
                            }
                            if (delayedMessage == null) {
                                delayedMessage2 = new DelayedMessage(peer);
                                delayedMessage2.type = 0;
                                delayedMessage2.obj = newMsgObj;
                                delayedMessage2.originalPath = originalPath;
                            } else {
                                delayedMessage3 = delayedMessage;
                            }
                            if (path != null && path.length() > 0) {
                                if (path.startsWith("http")) {
                                    delayedMessage3.httpLocation = path;
                                }
                            }
                            delayedMessage3.location = ((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location;
                        } else {
                            media = new TL_inputMediaPhoto();
                            media.id = new TL_inputPhoto();
                            media.caption = photo.caption != null ? photo.caption : TtmlNode.ANONYMOUS_REGION_ID;
                            media.id.id = photo.id;
                            media.id.access_hash = photo.access_hash;
                            inputMedia = media;
                            delayedMessage3 = delayedMessage;
                        }
                    } else if (type == 3) {
                        if (document.access_hash == 0) {
                            inputMedia = new TL_inputMediaUploadedDocument();
                            inputMedia.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                            inputMedia.mime_type = document.mime_type;
                            inputMedia.attributes = document.attributes;
                            if (!MessageObject.isRoundVideoDocument(document) && (videoEditedInfo == null || !(videoEditedInfo.muted || videoEditedInfo.roundVideo))) {
                                inputMedia.nosound_video = true;
                            }
                            if (ttl != 0) {
                                inputMedia.ttl_seconds = ttl;
                                newMsg.ttl = ttl;
                                inputMedia.flags |= 2;
                            }
                            if (delayedMessage == null) {
                                delayedMessage2 = new DelayedMessage(peer);
                                delayedMessage2.type = 1;
                                delayedMessage2.obj = newMsgObj;
                                delayedMessage2.originalPath = originalPath;
                            } else {
                                delayedMessage3 = delayedMessage;
                            }
                            delayedMessage3.location = document.thumb.location;
                            delayedMessage3.videoEditedInfo = videoEditedInfo;
                        } else {
                            media = new TL_inputMediaDocument();
                            media.id = new TL_inputDocument();
                            media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                            media.id.id = document.id;
                            media.id.access_hash = document.access_hash;
                            inputMedia = media;
                            delayedMessage3 = delayedMessage;
                        }
                    } else if (type == 6) {
                        inputMedia = new TL_inputMediaContact();
                        inputMedia.phone_number = user.phone;
                        inputMedia.first_name = user.first_name;
                        inputMedia.last_name = user.last_name;
                        delayedMessage3 = delayedMessage;
                    } else if (type == 7 || type == 9) {
                        if (document.access_hash == 0) {
                            String str2;
                            if (encryptedChat == null && originalPath != null && originalPath.length() > 0) {
                                if (originalPath.startsWith("http") && params != null) {
                                    inputMedia = new TL_inputMediaGifExternal();
                                    String[] args = ((String) params.get(UpdateFragment.FRAGMENT_URL)).split("\\|");
                                    if (args.length == 2) {
                                        ((TL_inputMediaGifExternal) inputMedia).url = args[0];
                                        inputMedia.q = args[1];
                                    }
                                    delayedMessage3 = delayedMessage;
                                    inputMedia.mime_type = document.mime_type;
                                    inputMedia.attributes = document.attributes;
                                    if (document.caption == null) {
                                        str2 = document.caption;
                                    } else {
                                        str2 = TtmlNode.ANONYMOUS_REGION_ID;
                                    }
                                    inputMedia.caption = str2;
                                }
                            }
                            inputMedia = new TL_inputMediaUploadedDocument();
                            if (ttl != 0) {
                                inputMedia.ttl_seconds = ttl;
                                newMsg.ttl = ttl;
                                inputMedia.flags |= 2;
                            }
                            delayedMessage2 = new DelayedMessage(peer);
                            delayedMessage2.originalPath = originalPath;
                            delayedMessage2.type = 2;
                            delayedMessage2.obj = newMsgObj;
                            delayedMessage2.location = document.thumb.location;
                            inputMedia.mime_type = document.mime_type;
                            inputMedia.attributes = document.attributes;
                            if (document.caption == null) {
                                str2 = TtmlNode.ANONYMOUS_REGION_ID;
                            } else {
                                str2 = document.caption;
                            }
                            inputMedia.caption = str2;
                        } else {
                            media = new TL_inputMediaDocument();
                            media.id = new TL_inputDocument();
                            media.id.id = document.id;
                            media.id.access_hash = document.access_hash;
                            media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                            inputMedia = media;
                            delayedMessage3 = delayedMessage;
                        }
                    } else if (type != 8) {
                        delayedMessage3 = delayedMessage;
                    } else if (document.access_hash == 0) {
                        inputMedia = new TL_inputMediaUploadedDocument();
                        inputMedia.mime_type = document.mime_type;
                        inputMedia.attributes = document.attributes;
                        inputMedia.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                        if (ttl != 0) {
                            inputMedia.ttl_seconds = ttl;
                            newMsg.ttl = ttl;
                            inputMedia.flags |= 2;
                        }
                        delayedMessage2 = new DelayedMessage(peer);
                        delayedMessage2.type = 3;
                        delayedMessage2.obj = newMsgObj;
                    } else {
                        media = new TL_inputMediaDocument();
                        media.id = new TL_inputDocument();
                        media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                        media.id.id = document.id;
                        media.id.access_hash = document.access_hash;
                        inputMedia = media;
                        delayedMessage3 = delayedMessage;
                    }
                    if (sendToPeers != null) {
                        request = new TL_messages_sendBroadcast();
                        random_ids = new ArrayList();
                        for (a = 0; a < sendToPeers.size(); a++) {
                            random_ids.add(Long.valueOf(Utilities.random.nextLong()));
                        }
                        request.contacts = sendToPeers;
                        request.media = inputMedia;
                        request.random_id = random_ids;
                        request.message = TtmlNode.ANONYMOUS_REGION_ID;
                        if (delayedMessage3 != null) {
                            delayedMessage3.sendRequest = request;
                        }
                        reqSend5 = request;
                        if (retryMessageObject == null) {
                            DataQuery.getInstance(this.currentAccount).cleanDraft(peer, false);
                        }
                    } else if (groupId != 0) {
                        if (delayedMessage3.sendRequest != null) {
                            request = (TL_messages_sendMultiMedia) delayedMessage3.sendRequest;
                        } else {
                            request = new TL_messages_sendMultiMedia();
                            request.peer = sendToPeer;
                            if (newMsg.to_id instanceof TL_peerChannel) {
                                request.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + peer, false);
                            }
                            if (reply_to_msg != null) {
                                request.flags |= 1;
                                request.reply_to_msg_id = reply_to_msg.getId();
                            }
                            delayedMessage3.sendRequest = request;
                        }
                        delayedMessage3.messageObjects.add(newMsgObj);
                        delayedMessage3.messages.add(newMsg);
                        delayedMessage3.originalPaths.add(originalPath);
                        TL_inputSingleMedia inputSingleMedia = new TL_inputSingleMedia();
                        inputSingleMedia.random_id = newMsg.random_id;
                        inputSingleMedia.media = inputMedia;
                        request.multi_media.add(inputSingleMedia);
                        reqSend5 = request;
                    } else {
                        request = new TL_messages_sendMedia();
                        request.peer = sendToPeer;
                        if (newMsg.to_id instanceof TL_peerChannel) {
                            request.silent = MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("silent_" + peer, false);
                        }
                        if (reply_to_msg != null) {
                            request.flags |= 1;
                            request.reply_to_msg_id = reply_to_msg.getId();
                        }
                        request.random_id = newMsg.random_id;
                        request.media = inputMedia;
                        if (delayedMessage3 != null) {
                            delayedMessage3.sendRequest = request;
                        }
                        reqSend5 = request;
                    }
                    if (groupId != 0) {
                        performSendDelayedMessage(delayedMessage3);
                    } else if (type == 1) {
                        performSendMessageRequest(reqSend5, newMsgObj, null);
                    } else if (type == 2) {
                        if (photo.access_hash == 0) {
                            performSendDelayedMessage(delayedMessage3);
                        } else {
                            performSendMessageRequest(reqSend5, newMsgObj, null, null, true);
                        }
                    } else if (type == 3) {
                        if (document.access_hash == 0) {
                            performSendDelayedMessage(delayedMessage3);
                        } else {
                            performSendMessageRequest(reqSend5, newMsgObj, null);
                        }
                    } else if (type == 6) {
                        performSendMessageRequest(reqSend5, newMsgObj, null);
                    } else if (type == 7) {
                        if (document.access_hash != 0 || delayedMessage3 == null) {
                            performSendMessageRequest(reqSend5, newMsgObj, originalPath);
                        } else {
                            performSendDelayedMessage(delayedMessage3);
                        }
                    } else if (type != 8) {
                    } else {
                        if (document.access_hash == 0) {
                            performSendDelayedMessage(delayedMessage3);
                        } else {
                            performSendMessageRequest(reqSend5, newMsgObj, null);
                        }
                    }
                } else {
                    DecryptedMessage reqSend6;
                    TL_inputEncryptedFile encryptedFile;
                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) >= 73) {
                        reqSend6 = new TL_decryptedMessage();
                        if (groupId != 0) {
                            reqSend6.grouped_id = groupId;
                            reqSend6.flags |= 131072;
                        }
                    } else {
                        reqSend6 = new TL_decryptedMessage_layer45();
                    }
                    reqSend6.ttl = newMsg.ttl;
                    if (!(entities == null || entities.isEmpty())) {
                        reqSend6.entities = entities;
                        reqSend6.flags |= 128;
                    }
                    if (!(reply_to_msg == null || reply_to_msg.messageOwner.random_id == 0)) {
                        reqSend6.reply_to_random_id = reply_to_msg.messageOwner.random_id;
                        reqSend6.flags |= 8;
                    }
                    reqSend6.flags |= 512;
                    if (params != null) {
                        if (params.get("bot_name") != null) {
                            reqSend6.via_bot_name = (String) params.get("bot_name");
                            reqSend6.flags |= 2048;
                        }
                    }
                    reqSend6.random_id = newMsg.random_id;
                    reqSend6.message = TtmlNode.ANONYMOUS_REGION_ID;
                    if (type == 1) {
                        if (location instanceof TL_messageMediaVenue) {
                            reqSend6.media = new TL_decryptedMessageMediaVenue();
                            reqSend6.media.address = location.address;
                            reqSend6.media.title = location.title;
                            reqSend6.media.provider = location.provider;
                            reqSend6.media.venue_id = location.venue_id;
                        } else {
                            reqSend6.media = new TL_decryptedMessageMediaGeoPoint();
                        }
                        reqSend6.media.lat = location.geo.lat;
                        reqSend6.media._long = location.geo._long;
                        SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(reqSend6, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                        delayedMessage3 = delayedMessage;
                    } else {
                        if (type == 2 || (type == 9 && photo != null)) {
                            PhotoSize small = (PhotoSize) photo.sizes.get(0);
                            PhotoSize big = (PhotoSize) photo.sizes.get(photo.sizes.size() - 1);
                            ImageLoader.fillPhotoSizeWithBytes(small);
                            reqSend6.media = new TL_decryptedMessageMediaPhoto();
                            reqSend6.media.caption = photo.caption != null ? photo.caption : TtmlNode.ANONYMOUS_REGION_ID;
                            if (small.bytes != null) {
                                ((TL_decryptedMessageMediaPhoto) reqSend6.media).thumb = small.bytes;
                            } else {
                                ((TL_decryptedMessageMediaPhoto) reqSend6.media).thumb = new byte[0];
                            }
                            reqSend6.media.thumb_h = small.h;
                            reqSend6.media.thumb_w = small.w;
                            reqSend6.media.w = big.w;
                            reqSend6.media.h = big.h;
                            reqSend6.media.size = big.size;
                            if (big.location.key == null || groupId != 0) {
                                if (delayedMessage == null) {
                                    delayedMessage2 = new DelayedMessage(peer);
                                    delayedMessage2.encryptedChat = encryptedChat;
                                    delayedMessage2.type = 0;
                                    delayedMessage2.originalPath = originalPath;
                                    delayedMessage2.sendEncryptedRequest = reqSend6;
                                    delayedMessage2.obj = newMsgObj;
                                } else {
                                    delayedMessage3 = delayedMessage;
                                }
                                if (!TextUtils.isEmpty(path)) {
                                    if (path.startsWith("http")) {
                                        delayedMessage3.httpLocation = path;
                                        if (groupId == 0) {
                                            performSendDelayedMessage(delayedMessage3);
                                        }
                                    }
                                }
                                delayedMessage3.location = ((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location;
                                if (groupId == 0) {
                                    performSendDelayedMessage(delayedMessage3);
                                }
                            } else {
                                encryptedFile = new TL_inputEncryptedFile();
                                encryptedFile.id = big.location.volume_id;
                                encryptedFile.access_hash = big.location.secret;
                                reqSend6.media.key = big.location.key;
                                reqSend6.media.iv = big.location.iv;
                                SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(reqSend6, newMsgObj.messageOwner, encryptedChat, encryptedFile, null, newMsgObj);
                            }
                        } else if (type == 3) {
                            ImageLoader.fillPhotoSizeWithBytes(document.thumb);
                            if (MessageObject.isNewGifDocument(document) || MessageObject.isRoundVideoDocument(document)) {
                                reqSend6.media = new TL_decryptedMessageMediaDocument();
                                reqSend6.media.attributes = document.attributes;
                                if (document.thumb == null || document.thumb.bytes == null) {
                                    ((TL_decryptedMessageMediaDocument) reqSend6.media).thumb = new byte[0];
                                } else {
                                    ((TL_decryptedMessageMediaDocument) reqSend6.media).thumb = document.thumb.bytes;
                                }
                            } else {
                                reqSend6.media = new TL_decryptedMessageMediaVideo();
                                if (document.thumb == null || document.thumb.bytes == null) {
                                    ((TL_decryptedMessageMediaVideo) reqSend6.media).thumb = new byte[0];
                                } else {
                                    ((TL_decryptedMessageMediaVideo) reqSend6.media).thumb = document.thumb.bytes;
                                }
                            }
                            reqSend6.media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                            reqSend6.media.mime_type = MimeTypes.VIDEO_MP4;
                            reqSend6.media.size = document.size;
                            for (a = 0; a < document.attributes.size(); a++) {
                                attribute = (DocumentAttribute) document.attributes.get(a);
                                if (attribute instanceof TL_documentAttributeVideo) {
                                    reqSend6.media.w = attribute.w;
                                    reqSend6.media.h = attribute.h;
                                    reqSend6.media.duration = attribute.duration;
                                    break;
                                }
                            }
                            reqSend6.media.thumb_h = document.thumb.h;
                            reqSend6.media.thumb_w = document.thumb.w;
                            if (document.key == null || groupId != 0) {
                                if (delayedMessage == null) {
                                    delayedMessage2 = new DelayedMessage(peer);
                                    delayedMessage2.encryptedChat = encryptedChat;
                                    delayedMessage2.type = 1;
                                    delayedMessage2.sendEncryptedRequest = reqSend6;
                                    delayedMessage2.originalPath = originalPath;
                                    delayedMessage2.obj = newMsgObj;
                                } else {
                                    delayedMessage3 = delayedMessage;
                                }
                                delayedMessage3.videoEditedInfo = videoEditedInfo;
                                if (groupId == 0) {
                                    performSendDelayedMessage(delayedMessage3);
                                }
                            } else {
                                encryptedFile = new TL_inputEncryptedFile();
                                encryptedFile.id = document.id;
                                encryptedFile.access_hash = document.access_hash;
                                reqSend6.media.key = document.key;
                                reqSend6.media.iv = document.iv;
                                SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(reqSend6, newMsgObj.messageOwner, encryptedChat, encryptedFile, null, newMsgObj);
                                delayedMessage3 = delayedMessage;
                            }
                        } else if (type == 6) {
                            reqSend6.media = new TL_decryptedMessageMediaContact();
                            reqSend6.media.phone_number = user.phone;
                            reqSend6.media.first_name = user.first_name;
                            reqSend6.media.last_name = user.last_name;
                            reqSend6.media.user_id = user.id;
                            SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(reqSend6, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                            delayedMessage3 = delayedMessage;
                        } else if (type == 7 || (type == 9 && document != null)) {
                            if (MessageObject.isStickerDocument(document)) {
                                reqSend6.media = new TL_decryptedMessageMediaExternalDocument();
                                reqSend6.media.id = document.id;
                                reqSend6.media.date = document.date;
                                reqSend6.media.access_hash = document.access_hash;
                                reqSend6.media.mime_type = document.mime_type;
                                reqSend6.media.size = document.size;
                                reqSend6.media.dc_id = document.dc_id;
                                reqSend6.media.attributes = document.attributes;
                                if (document.thumb == null) {
                                    ((TL_decryptedMessageMediaExternalDocument) reqSend6.media).thumb = new TL_photoSizeEmpty();
                                    ((TL_decryptedMessageMediaExternalDocument) reqSend6.media).thumb.type = "s";
                                } else {
                                    ((TL_decryptedMessageMediaExternalDocument) reqSend6.media).thumb = document.thumb;
                                }
                                SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(reqSend6, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                                delayedMessage3 = delayedMessage;
                            } else {
                                ImageLoader.fillPhotoSizeWithBytes(document.thumb);
                                reqSend6.media = new TL_decryptedMessageMediaDocument();
                                reqSend6.media.attributes = document.attributes;
                                reqSend6.media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                if (document.thumb == null || document.thumb.bytes == null) {
                                    ((TL_decryptedMessageMediaDocument) reqSend6.media).thumb = new byte[0];
                                    reqSend6.media.thumb_h = 0;
                                    reqSend6.media.thumb_w = 0;
                                } else {
                                    ((TL_decryptedMessageMediaDocument) reqSend6.media).thumb = document.thumb.bytes;
                                    reqSend6.media.thumb_h = document.thumb.h;
                                    reqSend6.media.thumb_w = document.thumb.w;
                                }
                                reqSend6.media.size = document.size;
                                reqSend6.media.mime_type = document.mime_type;
                                if (document.key == null) {
                                    delayedMessage2 = new DelayedMessage(peer);
                                    delayedMessage2.originalPath = originalPath;
                                    delayedMessage2.sendEncryptedRequest = reqSend6;
                                    delayedMessage2.type = 2;
                                    delayedMessage2.obj = newMsgObj;
                                    delayedMessage2.encryptedChat = encryptedChat;
                                    if (path != null && path.length() > 0) {
                                        if (path.startsWith("http")) {
                                            delayedMessage2.httpLocation = path;
                                        }
                                    }
                                    performSendDelayedMessage(delayedMessage2);
                                } else {
                                    encryptedFile = new TL_inputEncryptedFile();
                                    encryptedFile.id = document.id;
                                    encryptedFile.access_hash = document.access_hash;
                                    reqSend6.media.key = document.key;
                                    reqSend6.media.iv = document.iv;
                                    SecretChatHelper.getInstance(this.currentAccount).performSendEncryptedRequest(reqSend6, newMsgObj.messageOwner, encryptedChat, encryptedFile, null, newMsgObj);
                                    delayedMessage3 = delayedMessage;
                                }
                            }
                        } else if (type == 8) {
                            delayedMessage2 = new DelayedMessage(peer);
                            delayedMessage2.encryptedChat = encryptedChat;
                            delayedMessage2.sendEncryptedRequest = reqSend6;
                            delayedMessage2.obj = newMsgObj;
                            delayedMessage2.type = 3;
                            reqSend6.media = new TL_decryptedMessageMediaDocument();
                            reqSend6.media.attributes = document.attributes;
                            reqSend6.media.caption = document.caption != null ? document.caption : TtmlNode.ANONYMOUS_REGION_ID;
                            if (document.thumb == null || document.thumb.bytes == null) {
                                ((TL_decryptedMessageMediaDocument) reqSend6.media).thumb = new byte[0];
                                reqSend6.media.thumb_h = 0;
                                reqSend6.media.thumb_w = 0;
                            } else {
                                ((TL_decryptedMessageMediaDocument) reqSend6.media).thumb = document.thumb.bytes;
                                reqSend6.media.thumb_h = document.thumb.h;
                                reqSend6.media.thumb_w = document.thumb.w;
                            }
                            reqSend6.media.mime_type = document.mime_type;
                            reqSend6.media.size = document.size;
                            delayedMessage2.originalPath = originalPath;
                            performSendDelayedMessage(delayedMessage2);
                        }
                        delayedMessage3 = delayedMessage;
                    }
                    if (groupId != 0) {
                        TL_messages_sendEncryptedMultiMedia request;
                        if (delayedMessage3.sendEncryptedRequest != null) {
                            request = (TL_messages_sendEncryptedMultiMedia) delayedMessage3.sendEncryptedRequest;
                        } else {
                            request = new TL_messages_sendEncryptedMultiMedia();
                            delayedMessage3.sendEncryptedRequest = request;
                        }
                        delayedMessage3.messageObjects.add(newMsgObj);
                        delayedMessage3.messages.add(newMsg);
                        delayedMessage3.originalPaths.add(originalPath);
                        delayedMessage3.upload = true;
                        request.messages.add(reqSend6);
                        encryptedFile = new TL_inputEncryptedFile();
                        encryptedFile.id = type == 3 ? 1 : 0;
                        request.files.add(encryptedFile);
                        performSendDelayedMessage(delayedMessage3);
                    }
                    if (retryMessageObject == null) {
                        DataQuery.getInstance(this.currentAccount).cleanDraft(peer, false);
                    }
                }
            } catch (Exception e7) {
                e = e7;
                delayedMessage3 = delayedMessage;
                FileLog.e(e);
                MessagesStorage.getInstance(this.currentAccount).markMessageAsSendError(newMsg);
                if (newMsgObj != null) {
                    newMsgObj.messageOwner.send_state = 2;
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                processSentMessage(newMsg.id);
            }
        }
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
            MediaController.getInstance(this.currentAccount).scheduleVideoConvert(message.obj);
        } else if (message.type == 2) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "gif", this.currentAccount);
            } else if (message.sendRequest != null) {
                if (message.sendRequest instanceof TL_messages_sendMedia) {
                    media = ((TL_messages_sendMedia) message.sendRequest).media;
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
                        MediaController.getInstance(this.currentAccount).scheduleVideoConvert(messageObject);
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

    private void uploadMultiMedia(final DelayedMessage message, final InputMedia inputMedia, InputEncryptedFile inputEncryptedFile, String key) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            InputMedia newInputMedia = null;
                            if (response != null) {
                                MessageMedia messageMedia = response;
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
                                newInputMedia.caption = inputMedia.caption;
                                if (inputMedia.ttl_seconds != 0) {
                                    newInputMedia.ttl_seconds = inputMedia.ttl_seconds;
                                    newInputMedia.flags |= 1;
                                }
                                TL_messages_sendMultiMedia req = message.sendRequest;
                                for (int a = 0; a < req.multi_media.size(); a++) {
                                    if (((TL_inputSingleMedia) req.multi_media.get(a)).media == inputMedia) {
                                        ((TL_inputSingleMedia) req.multi_media.get(a)).media = newInputMedia;
                                        break;
                                    }
                                }
                                SendMessagesHelper.this.sendReadyToSendGroup(message, false, true);
                                return;
                            }
                            message.markAsError();
                        }
                    });
                }
            });
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

    protected void stopVideoService(final String path) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.stopEncodingService, path);
                    }
                });
            }
        });
    }

    protected void putToSendingMessages(Message message) {
        this.sendingMessages.put(Integer.valueOf(message.id), message);
    }

    protected void removeFromSendingMessages(int mid) {
        this.sendingMessages.remove(Integer.valueOf(mid));
    }

    public boolean isSendingMessage(int mid) {
        return this.sendingMessages.containsKey(Integer.valueOf(mid));
    }

    private void performSendMessageRequestMulti(final TL_messages_sendMultiMedia req, final ArrayList<MessageObject> msgObjs, final ArrayList<String> originalPaths) {
        for (int a = 0; a < msgObjs.size(); a++) {
            putToSendingMessages(((MessageObject) msgObjs.get(a)).messageOwner);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject) req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int i;
                        final Message newMsgObj;
                        boolean isSentError = false;
                        if (error == null) {
                            HashMap<Integer, Message> newMessages = new HashMap();
                            HashMap<Long, Integer> newIds = new HashMap();
                            Updates updates = (Updates) response;
                            ArrayList<Update> updatesArr = ((Updates) response).updates;
                            int a = 0;
                            while (a < updatesArr.size()) {
                                Update update = (Update) updatesArr.get(a);
                                if (update instanceof TL_updateMessageID) {
                                    newIds.put(Long.valueOf(update.random_id), Integer.valueOf(((TL_updateMessageID) update).id));
                                    updatesArr.remove(a);
                                    a--;
                                } else if (update instanceof TL_updateNewMessage) {
                                    final TL_updateNewMessage newMessage = (TL_updateNewMessage) update;
                                    newMessages.put(Integer.valueOf(newMessage.message.id), newMessage.message);
                                    Utilities.stageQueue.postRunnable(new Runnable() {
                                        public void run() {
                                            MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
                                        }
                                    });
                                    updatesArr.remove(a);
                                    a--;
                                } else if (update instanceof TL_updateNewChannelMessage) {
                                    final TL_updateNewChannelMessage newMessage2 = (TL_updateNewChannelMessage) update;
                                    newMessages.put(Integer.valueOf(newMessage2.message.id), newMessage2.message);
                                    Utilities.stageQueue.postRunnable(new Runnable() {
                                        public void run() {
                                            MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewChannelDifferenceParams(newMessage2.pts, newMessage2.pts_count, newMessage2.message.to_id.channel_id);
                                        }
                                    });
                                    updatesArr.remove(a);
                                    a--;
                                }
                                a++;
                            }
                            for (i = 0; i < msgObjs.size(); i++) {
                                MessageObject msgObj = (MessageObject) msgObjs.get(i);
                                String originalPath = (String) originalPaths.get(i);
                                newMsgObj = msgObj.messageOwner;
                                final int oldId = newMsgObj.id;
                                ArrayList<Message> sentMessages = new ArrayList();
                                String attachPath = newMsgObj.attachPath;
                                Integer id = (Integer) newIds.get(Long.valueOf(newMsgObj.random_id));
                                if (id == null) {
                                    isSentError = true;
                                    break;
                                }
                                Message message = (Message) newMessages.get(id);
                                if (message == null) {
                                    isSentError = true;
                                    break;
                                }
                                sentMessages.add(message);
                                newMsgObj.id = message.id;
                                if ((newMsgObj.flags & Integer.MIN_VALUE) != 0) {
                                    message.flags |= Integer.MIN_VALUE;
                                }
                                Integer value = (Integer) MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(message.dialog_id));
                                if (value == null) {
                                    value = Integer.valueOf(MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(message.dialog_id), value);
                                }
                                message.unread = value.intValue() < message.id;
                                SendMessagesHelper.this.updateMediaPaths(msgObj, message, originalPath, false);
                                if (null == null) {
                                    StatsController.getInstance(SendMessagesHelper.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, 1);
                                    newMsgObj.send_state = 0;
                                    NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id));
                                    final ArrayList<Message> arrayList = sentMessages;
                                    MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                                        public void run() {
                                            MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(oldId), newMsgObj.id, 0, false, newMsgObj.to_id.channel_id);
                                            MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(arrayList, true, false, false, 0);
                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                public void run() {
                                                    DataQuery.getInstance(SendMessagesHelper.this.currentAccount).increasePeerRaiting(newMsgObj.dialog_id);
                                                    NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newMsgObj.id), newMsgObj, Long.valueOf(newMsgObj.dialog_id));
                                                    SendMessagesHelper.this.processSentMessage(oldId);
                                                    SendMessagesHelper.this.removeFromSendingMessages(oldId);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                            final Updates updates2 = updates;
                            Utilities.stageQueue.postRunnable(new Runnable() {
                                public void run() {
                                    MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates(updates2, false);
                                }
                            });
                        } else {
                            AlertsCreator.processError(SendMessagesHelper.this.currentAccount, error, null, req, new Object[0]);
                            isSentError = true;
                        }
                        if (isSentError) {
                            for (i = 0; i < msgObjs.size(); i++) {
                                newMsgObj = ((MessageObject) msgObjs.get(i)).messageOwner;
                                MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(newMsgObj);
                                newMsgObj.send_state = 2;
                                NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
                                SendMessagesHelper.this.processSentMessage(newMsgObj.id);
                                SendMessagesHelper.this.removeFromSendingMessages(newMsgObj.id);
                            }
                        }
                    }
                });
            }
        }, null, 68);
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
        if (check) {
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
        final Message newMsgObj = msgObj.messageOwner;
        putToSendingMessages(newMsgObj);
        ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
        final TLObject tLObject = req;
        final MessageObject messageObject = msgObj;
        final String str = originalPath;
        RequestDelegate anonymousClass11 = new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        boolean isSentError = false;
                        if (error == null) {
                            int i;
                            int oldId = newMsgObj.id;
                            boolean isBroadcast = tLObject instanceof TL_messages_sendBroadcast;
                            ArrayList<Message> sentMessages = new ArrayList();
                            String attachPath = newMsgObj.attachPath;
                            Message message;
                            if (response instanceof TL_updateShortSentMessage) {
                                TL_updateShortSentMessage res = (TL_updateShortSentMessage) response;
                                message = newMsgObj;
                                Message message2 = newMsgObj;
                                i = res.id;
                                message2.id = i;
                                message.local_id = i;
                                newMsgObj.date = res.date;
                                newMsgObj.entities = res.entities;
                                newMsgObj.out = res.out;
                                if (res.media != null) {
                                    newMsgObj.media = res.media;
                                    message = newMsgObj;
                                    message.flags |= 512;
                                }
                                if ((res.media instanceof TL_messageMediaGame) && !TextUtils.isEmpty(res.message)) {
                                    newMsgObj.message = res.message;
                                }
                                if (!newMsgObj.entities.isEmpty()) {
                                    message = newMsgObj;
                                    message.flags |= 128;
                                }
                                final TL_updateShortSentMessage tL_updateShortSentMessage = res;
                                Utilities.stageQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewDifferenceParams(-1, tL_updateShortSentMessage.pts, tL_updateShortSentMessage.date, tL_updateShortSentMessage.pts_count);
                                    }
                                });
                                sentMessages.add(newMsgObj);
                            } else if (response instanceof Updates) {
                                Updates updates = (Updates) response;
                                ArrayList<Update> updatesArr = ((Updates) response).updates;
                                Message message3 = null;
                                int a = 0;
                                while (a < updatesArr.size()) {
                                    Update update = (Update) updatesArr.get(a);
                                    if (update instanceof TL_updateNewMessage) {
                                        final TL_updateNewMessage newMessage = (TL_updateNewMessage) update;
                                        message3 = newMessage.message;
                                        sentMessages.add(message3);
                                        Utilities.stageQueue.postRunnable(new Runnable() {
                                            public void run() {
                                                MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
                                            }
                                        });
                                        updatesArr.remove(a);
                                        break;
                                    } else if (update instanceof TL_updateNewChannelMessage) {
                                        final TL_updateNewChannelMessage newMessage2 = (TL_updateNewChannelMessage) update;
                                        message3 = newMessage2.message;
                                        sentMessages.add(message3);
                                        if ((newMsgObj.flags & Integer.MIN_VALUE) != 0) {
                                            message = newMessage2.message;
                                            message.flags |= Integer.MIN_VALUE;
                                        }
                                        Utilities.stageQueue.postRunnable(new Runnable() {
                                            public void run() {
                                                MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processNewChannelDifferenceParams(newMessage2.pts, newMessage2.pts_count, newMessage2.message.to_id.channel_id);
                                            }
                                        });
                                        updatesArr.remove(a);
                                    } else {
                                        a++;
                                    }
                                }
                                if (message3 != null) {
                                    Integer value = (Integer) MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.get(Long.valueOf(message3.dialog_id));
                                    if (value == null) {
                                        value = Integer.valueOf(MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getDialogReadMax(message3.out, message3.dialog_id));
                                        MessagesController.getInstance(SendMessagesHelper.this.currentAccount).dialogs_read_outbox_max.put(Long.valueOf(message3.dialog_id), value);
                                    }
                                    message3.unread = value.intValue() < message3.id;
                                    newMsgObj.id = message3.id;
                                    SendMessagesHelper.this.updateMediaPaths(messageObject, message3, str, false);
                                } else {
                                    isSentError = true;
                                }
                                final Updates updates2 = updates;
                                Utilities.stageQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance(SendMessagesHelper.this.currentAccount).processUpdates(updates2, false);
                                    }
                                });
                            }
                            if (MessageObject.isLiveLocationMessage(newMsgObj)) {
                                LocationController.getInstance(SendMessagesHelper.this.currentAccount).addSharingLocation(newMsgObj.dialog_id, newMsgObj.id, newMsgObj.media.period, newMsgObj);
                            }
                            if (!isSentError) {
                                int i2;
                                StatsController.getInstance(SendMessagesHelper.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, 1);
                                newMsgObj.send_state = 0;
                                NotificationCenter instance = NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount);
                                i = NotificationCenter.messageReceivedByServer;
                                Object[] objArr = new Object[4];
                                objArr[0] = Integer.valueOf(oldId);
                                if (isBroadcast) {
                                    i2 = oldId;
                                } else {
                                    i2 = newMsgObj.id;
                                }
                                objArr[1] = Integer.valueOf(i2);
                                objArr[2] = newMsgObj;
                                objArr[3] = Long.valueOf(newMsgObj.dialog_id);
                                instance.postNotificationName(i, objArr);
                                i = oldId;
                                final boolean z = isBroadcast;
                                final ArrayList<Message> arrayList = sentMessages;
                                final String str = attachPath;
                                MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(i), z ? i : newMsgObj.id, 0, false, newMsgObj.to_id.channel_id);
                                        MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(arrayList, true, false, z, 0);
                                        if (z) {
                                            ArrayList currentMessage = new ArrayList();
                                            currentMessage.add(newMsgObj);
                                            MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).putMessages(currentMessage, true, false, false, 0);
                                        }
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                if (z) {
                                                    for (int a = 0; a < arrayList.size(); a++) {
                                                        Message message = (Message) arrayList.get(a);
                                                        ArrayList<MessageObject> arr = new ArrayList();
                                                        MessageObject messageObject = new MessageObject(SendMessagesHelper.this.currentAccount, message, null, false);
                                                        arr.add(messageObject);
                                                        MessagesController.getInstance(SendMessagesHelper.this.currentAccount).updateInterfaceWithMessages(messageObject.getDialogId(), arr, true);
                                                    }
                                                    NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                                }
                                                DataQuery.getInstance(SendMessagesHelper.this.currentAccount).increasePeerRaiting(newMsgObj.dialog_id);
                                                NotificationCenter instance = NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount);
                                                int i = NotificationCenter.messageReceivedByServer;
                                                Object[] objArr = new Object[4];
                                                objArr[0] = Integer.valueOf(i);
                                                objArr[1] = Integer.valueOf(z ? i : newMsgObj.id);
                                                objArr[2] = newMsgObj;
                                                objArr[3] = Long.valueOf(newMsgObj.dialog_id);
                                                instance.postNotificationName(i, objArr);
                                                SendMessagesHelper.this.processSentMessage(i);
                                                SendMessagesHelper.this.removeFromSendingMessages(i);
                                            }
                                        });
                                        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                                            SendMessagesHelper.this.stopVideoService(str);
                                        }
                                    }
                                });
                            }
                        } else {
                            AlertsCreator.processError(SendMessagesHelper.this.currentAccount, error, null, tLObject, new Object[0]);
                            isSentError = true;
                        }
                        if (isSentError) {
                            MessagesStorage.getInstance(SendMessagesHelper.this.currentAccount).markMessageAsSendError(newMsgObj);
                            newMsgObj.send_state = 2;
                            NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
                            SendMessagesHelper.this.processSentMessage(newMsgObj.id);
                            if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                                SendMessagesHelper.this.stopVideoService(newMsgObj.attachPath);
                            }
                            SendMessagesHelper.this.removeFromSendingMessages(newMsgObj.id);
                        }
                    }
                });
            }
        };
        QuickAckDelegate anonymousClass12 = new QuickAckDelegate() {
            public void run() {
                final int msg_id = newMsgObj.id;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        newMsgObj.send_state = 0;
                        NotificationCenter.getInstance(SendMessagesHelper.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(msg_id));
                    }
                });
            }
        };
        if (req instanceof TL_messages_sendMessage) {
            i = 128;
        } else {
            i = 0;
        }
        instance.sendRequest(req, anonymousClass11, anonymousClass12, i | 68);
        if (parentMessage != null) {
            parentMessage.sendDelayedRequests();
        }
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
        final ArrayList<User> arrayList = users;
        final ArrayList<Chat> arrayList2 = chats;
        final ArrayList<EncryptedChat> arrayList3 = encryptedChats;
        final ArrayList<Message> arrayList4 = messages;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putUsers(arrayList, true);
                MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putChats(arrayList2, true);
                MessagesController.getInstance(SendMessagesHelper.this.currentAccount).putEncryptedChats(arrayList3, true);
                for (int a = 0; a < arrayList4.size(); a++) {
                    SendMessagesHelper.this.retrySendMessage(new MessageObject(SendMessagesHelper.this.currentAccount, (Message) arrayList4.get(a), null, false), true);
                }
            }
        });
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

    private static boolean prepareSendingDocumentInternal(int currentAccount, String path, String originalPath, Uri uri, String mime, long dialog_id, MessageObject reply_to_msg, CharSequence caption) {
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
        if (ext.toLowerCase().equals("mp3") || ext.toLowerCase().equals("m4a")) {
            AudioInfo audioInfo = AudioInfo.getAudioInfo(file);
            if (!(audioInfo == null || audioInfo.getDuration() == 0)) {
                attributeAudio = new TL_documentAttributeAudio();
                attributeAudio.duration = (int) (audioInfo.getDuration() / 1000);
                attributeAudio.title = audioInfo.getTitle();
                attributeAudio.performer = audioInfo.getArtist();
                if (attributeAudio.title == null) {
                    attributeAudio.title = TtmlNode.ANONYMOUS_REGION_ID;
                }
                attributeAudio.flags |= 1;
                if (attributeAudio.performer == null) {
                    attributeAudio.performer = TtmlNode.ANONYMOUS_REGION_ID;
                }
                attributeAudio.flags |= 2;
            }
        }
        boolean sendNew = false;
        if (originalPath != null) {
            if (originalPath.endsWith("attheme")) {
                sendNew = true;
            } else if (attributeAudio != null) {
                originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + file.length();
            } else {
                originalPath = originalPath + TtmlNode.ANONYMOUS_REGION_ID + file.length();
            }
        }
        TL_document tL_document = null;
        if (!(sendNew || isEncrypted)) {
            tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, !isEncrypted ? 1 : 4);
            if (!(tL_document != null || path.equals(originalPath) || isEncrypted)) {
                tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(path + file.length(), !isEncrypted ? 1 : 4);
            }
        }
        if (tL_document == null) {
            tL_document = new TL_document();
            tL_document.id = 0;
            tL_document.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
            fileName.file_name = name;
            tL_document.attributes.add(fileName);
            tL_document.size = (int) file.length();
            tL_document.dc_id = 0;
            if (attributeAudio != null) {
                tL_document.attributes.add(attributeAudio);
            }
            if (ext.length() == 0) {
                tL_document.mime_type = "application/octet-stream";
            } else if (ext.toLowerCase().equals("webp")) {
                tL_document.mime_type = "image/webp";
            } else {
                String mimeType = myMime.getMimeTypeFromExtension(ext.toLowerCase());
                if (mimeType != null) {
                    tL_document.mime_type = mimeType;
                } else {
                    tL_document.mime_type = "application/octet-stream";
                }
            }
            if (tL_document.mime_type.equals("image/gif")) {
                try {
                    Bitmap bitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 90.0f, 90.0f, true);
                    if (bitmap != null) {
                        fileName.file_name = "animation.gif";
                        tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, isEncrypted);
                        bitmap.recycle();
                    }
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (tL_document.mime_type.equals("image/webp") && allowSticker) {
                Options bmOptions = new Options();
                try {
                    bmOptions.inJustDecodeBounds = true;
                    RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r");
                    ByteBuffer buffer = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0, (long) path.length());
                    Utilities.loadWebpImage(null, buffer, buffer.limit(), bmOptions, true);
                    randomAccessFile.close();
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
                if (bmOptions.outWidth != 0 && bmOptions.outHeight != 0 && bmOptions.outWidth <= 800 && bmOptions.outHeight <= 800) {
                    TL_documentAttributeSticker attributeSticker = new TL_documentAttributeSticker();
                    attributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
                    attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                    tL_document.attributes.add(attributeSticker);
                    TL_documentAttributeImageSize attributeImageSize = new TL_documentAttributeImageSize();
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
        if (caption != null) {
            tL_document.caption = caption.toString();
        } else {
            tL_document.caption = TtmlNode.ANONYMOUS_REGION_ID;
        }
        final HashMap<String, String> params = new HashMap();
        if (originalPath != null) {
            params.put("originalPath", originalPath);
        }
        final TL_document documentFinal = tL_document;
        final String pathFinal = path;
        final int i = currentAccount;
        final long j = dialog_id;
        final MessageObject messageObject = reply_to_msg;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SendMessagesHelper.getInstance(i).sendMessage(documentFinal, null, pathFinal, j, messageObject, null, params, 0);
            }
        });
        return true;
    }

    public static void prepareSendingDocument(String path, String originalPath, Uri uri, String mine, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent) {
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
            prepareSendingDocuments(paths, originalPaths, uris, mine, dialog_id, reply_to_msg, inputContent);
        }
    }

    public static void prepareSendingAudioDocuments(ArrayList<MessageObject> messageObjects, long dialog_id, MessageObject reply_to_msg) {
        final int currentAccount = UserConfig.selectedAccount;
        final ArrayList<MessageObject> arrayList = messageObjects;
        final long j = dialog_id;
        final MessageObject messageObject = reply_to_msg;
        new Thread(new Runnable() {
            public void run() {
                int size = arrayList.size();
                for (int a = 0; a < size; a++) {
                    final MessageObject messageObject = (MessageObject) arrayList.get(a);
                    String originalPath = messageObject.messageOwner.attachPath;
                    File f = new File(originalPath);
                    boolean isEncrypted = ((int) j) == 0;
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
                        if (MessagesController.getInstance(currentAccount).getEncryptedChat(Integer.valueOf((int) (j >> 32))) == null) {
                            return;
                        }
                    }
                    final HashMap<String, String> params = new HashMap();
                    if (originalPath != null) {
                        params.put("originalPath", originalPath);
                    }
                    final TL_document documentFinal = tL_document;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            SendMessagesHelper.getInstance(currentAccount).sendMessage(documentFinal, null, messageObject.messageOwner.attachPath, j, messageObject, null, params, 0);
                        }
                    });
                }
            }
        }).start();
    }

    public static void prepareSendingDocuments(ArrayList<String> paths, ArrayList<String> originalPaths, ArrayList<Uri> uris, String mime, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent) {
        if (paths != null || originalPaths != null || uris != null) {
            if (paths == null || originalPaths == null || paths.size() == originalPaths.size()) {
                final int currentAccount = UserConfig.selectedAccount;
                final ArrayList<String> arrayList = paths;
                final ArrayList<String> arrayList2 = originalPaths;
                final String str = mime;
                final long j = dialog_id;
                final MessageObject messageObject = reply_to_msg;
                final ArrayList<Uri> arrayList3 = uris;
                final InputContentInfoCompat inputContentInfoCompat = inputContent;
                new Thread(new Runnable() {
                    public void run() {
                        int a;
                        boolean error = false;
                        if (arrayList != null) {
                            for (a = 0; a < arrayList.size(); a++) {
                                if (!SendMessagesHelper.prepareSendingDocumentInternal(currentAccount, (String) arrayList.get(a), (String) arrayList2.get(a), null, str, j, messageObject, null)) {
                                    error = true;
                                }
                            }
                        }
                        if (arrayList3 != null) {
                            for (a = 0; a < arrayList3.size(); a++) {
                                if (!SendMessagesHelper.prepareSendingDocumentInternal(currentAccount, null, null, (Uri) arrayList3.get(a), str, j, messageObject, null)) {
                                    error = true;
                                }
                            }
                        }
                        if (inputContentInfoCompat != null) {
                            inputContentInfoCompat.releasePermission();
                        }
                        if (error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    try {
                                        Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", R.string.UnsupportedAttachment), 0).show();
                                    } catch (Throwable e) {
                                        FileLog.e(e);
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    public static void prepareSendingPhoto(String imageFilePath, Uri imageUri, long dialog_id, MessageObject reply_to_msg, CharSequence caption, ArrayList<InputDocument> stickers, InputContentInfoCompat inputContent, int ttl) {
        SendingMediaInfo info = new SendingMediaInfo();
        info.path = imageFilePath;
        info.uri = imageUri;
        if (caption != null) {
            info.caption = caption.toString();
        }
        info.ttl = ttl;
        if (!(stickers == null || stickers.isEmpty())) {
            info.masks = new ArrayList(stickers);
        }
        ArrayList<SendingMediaInfo> infos = new ArrayList();
        infos.add(info);
        prepareSendingMedia(infos, dialog_id, reply_to_msg, inputContent, false, false);
    }

    public static void prepareSendingBotContextResult(BotInlineResult result, HashMap<String, String> params, long dialog_id, MessageObject reply_to_msg) {
        if (result != null) {
            final int currentAccount = UserConfig.selectedAccount;
            if (result.send_message instanceof TL_botInlineMessageMediaAuto) {
                final BotInlineResult botInlineResult = result;
                final long j = dialog_id;
                final HashMap<String, String> hashMap = params;
                final MessageObject messageObject = reply_to_msg;
                new Thread(new Runnable() {
                    public void run() {
                        String finalPath = null;
                        TL_document document = null;
                        TL_photo photo = null;
                        TL_game game = null;
                        if (!(botInlineResult instanceof TL_botInlineMediaResult)) {
                            if (botInlineResult.content_url != null) {
                                File file = new File(FileLoader.getDirectory(4), Utilities.MD5(botInlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content_url, "file"));
                                if (file.exists()) {
                                    finalPath = file.getAbsolutePath();
                                } else {
                                    finalPath = botInlineResult.content_url;
                                }
                                String str = botInlineResult.type;
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
                                        document.mime_type = botInlineResult.content_type;
                                        document.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                                        TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                                        document.attributes.add(fileName);
                                        str = botInlineResult.type;
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
                                                    FileLog.e(e);
                                                    break;
                                                }
                                                break;
                                            case 1:
                                                audio = new TL_documentAttributeAudio();
                                                audio.duration = botInlineResult.duration;
                                                audio.voice = true;
                                                fileName.file_name = "audio.ogg";
                                                document.attributes.add(audio);
                                                document.thumb = new TL_photoSizeEmpty();
                                                document.thumb.type = "s";
                                                break;
                                            case 2:
                                                audio = new TL_documentAttributeAudio();
                                                audio.duration = botInlineResult.duration;
                                                audio.title = botInlineResult.title;
                                                audio.flags |= 1;
                                                if (botInlineResult.description != null) {
                                                    audio.performer = botInlineResult.description;
                                                    audio.flags |= 2;
                                                }
                                                fileName.file_name = "audio.mp3";
                                                document.attributes.add(audio);
                                                document.thumb = new TL_photoSizeEmpty();
                                                document.thumb.type = "s";
                                                break;
                                            case 3:
                                                int idx = botInlineResult.content_type.indexOf(47);
                                                if (idx == -1) {
                                                    fileName.file_name = "file";
                                                    break;
                                                } else {
                                                    fileName.file_name = "file." + botInlineResult.content_type.substring(idx + 1);
                                                    break;
                                                }
                                            case 4:
                                                fileName.file_name = "video.mp4";
                                                TL_documentAttributeVideo attributeVideo = new TL_documentAttributeVideo();
                                                attributeVideo.w = botInlineResult.w;
                                                attributeVideo.h = botInlineResult.h;
                                                attributeVideo.duration = botInlineResult.duration;
                                                document.attributes.add(attributeVideo);
                                                try {
                                                    bitmap = ImageLoader.loadBitmap(new File(FileLoader.getDirectory(4), Utilities.MD5(botInlineResult.thumb_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.thumb_url, "jpg")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                                    if (bitmap != null) {
                                                        document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                                        bitmap.recycle();
                                                        break;
                                                    }
                                                } catch (Throwable e2) {
                                                    FileLog.e(e2);
                                                    break;
                                                }
                                                break;
                                            case 5:
                                                TL_documentAttributeSticker attributeSticker = new TL_documentAttributeSticker();
                                                attributeSticker.alt = TtmlNode.ANONYMOUS_REGION_ID;
                                                attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                                document.attributes.add(attributeSticker);
                                                TL_documentAttributeImageSize attributeImageSize = new TL_documentAttributeImageSize();
                                                attributeImageSize.w = botInlineResult.w;
                                                attributeImageSize.h = botInlineResult.h;
                                                document.attributes.add(attributeImageSize);
                                                fileName.file_name = "sticker.webp";
                                                try {
                                                    bitmap = ImageLoader.loadBitmap(new File(FileLoader.getDirectory(4), Utilities.MD5(botInlineResult.thumb_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.thumb_url, "webp")).getAbsolutePath(), null, 90.0f, 90.0f, true);
                                                    if (bitmap != null) {
                                                        document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, false);
                                                        bitmap.recycle();
                                                        break;
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
                                        if (document.thumb == null) {
                                            document.thumb = new TL_photoSize();
                                            document.thumb.w = botInlineResult.w;
                                            document.thumb.h = botInlineResult.h;
                                            document.thumb.size = 0;
                                            document.thumb.location = new TL_fileLocationUnavailable();
                                            document.thumb.type = "x";
                                            break;
                                        }
                                        break;
                                    case 6:
                                        if (file.exists()) {
                                            photo = SendMessagesHelper.getInstance(currentAccount).generatePhotoSizes(finalPath, null);
                                        }
                                        if (photo == null) {
                                            photo = new TL_photo();
                                            photo.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                                            TL_photoSize photoSize = new TL_photoSize();
                                            photoSize.w = botInlineResult.w;
                                            photoSize.h = botInlineResult.h;
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
                        } else if (botInlineResult.type.equals("game")) {
                            if (((int) j) != 0) {
                                game = new TL_game();
                                game.title = botInlineResult.title;
                                game.description = botInlineResult.description;
                                game.short_name = botInlineResult.id;
                                game.photo = botInlineResult.photo;
                                if (botInlineResult.document instanceof TL_document) {
                                    game.document = botInlineResult.document;
                                    game.flags |= 1;
                                }
                            } else {
                                return;
                            }
                        } else if (botInlineResult.document != null) {
                            if (botInlineResult.document instanceof TL_document) {
                                document = botInlineResult.document;
                            }
                        } else if (botInlineResult.photo != null && (botInlineResult.photo instanceof TL_photo)) {
                            photo = (TL_photo) botInlineResult.photo;
                        }
                        final String finalPathFinal = finalPath;
                        final TL_document finalDocument = document;
                        final TL_photo finalPhoto = photo;
                        final TL_game finalGame = game;
                        if (!(hashMap == null || botInlineResult.content_url == null)) {
                            hashMap.put("originalPath", botInlineResult.content_url);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (finalDocument != null) {
                                    finalDocument.caption = botInlineResult.send_message.caption;
                                    SendMessagesHelper.getInstance(currentAccount).sendMessage(finalDocument, null, finalPathFinal, j, messageObject, botInlineResult.send_message.reply_markup, hashMap, 0);
                                } else if (finalPhoto != null) {
                                    finalPhoto.caption = botInlineResult.send_message.caption;
                                    SendMessagesHelper.getInstance(currentAccount).sendMessage(finalPhoto, botInlineResult.content_url, j, messageObject, botInlineResult.send_message.reply_markup, hashMap, 0);
                                } else if (finalGame != null) {
                                    SendMessagesHelper.getInstance(currentAccount).sendMessage(finalGame, j, botInlineResult.send_message.reply_markup, hashMap);
                                }
                            }
                        });
                    }
                }).run();
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
                instance.sendMessage(str, dialog_id, reply_to_msg, webPage, z, result.send_message.entities, result.send_message.reply_markup, (HashMap) params);
            } else if (result.send_message instanceof TL_botInlineMessageMediaVenue) {
                MessageMedia venue = new TL_messageMediaVenue();
                venue.geo = result.send_message.geo;
                venue.address = result.send_message.address;
                venue.title = result.send_message.title;
                venue.provider = result.send_message.provider;
                venue.venue_id = result.send_message.venue_id;
                venue.venue_type = TtmlNode.ANONYMOUS_REGION_ID;
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

    public static void prepareSendingText(final String text, final long dialog_id) {
        final int currentAccount = UserConfig.selectedAccount;
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                String textFinal = SendMessagesHelper.getTrimmedString(text);
                                if (textFinal.length() != 0) {
                                    int count = (int) Math.ceil((double) (((float) textFinal.length()) / 4096.0f));
                                    for (int a = 0; a < count; a++) {
                                        SendMessagesHelper.getInstance(currentAccount).sendMessage(textFinal.substring(a * 4096, Math.min((a + 1) * 4096, textFinal.length())), dialog_id, null, null, true, null, null, null);
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public static void prepareSendingMedia(ArrayList<SendingMediaInfo> media, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent, boolean forceDocument, boolean groupPhotos) {
        if (!media.isEmpty()) {
            final int currentAccount = UserConfig.selectedAccount;
            final ArrayList<SendingMediaInfo> arrayList = media;
            final long j = dialog_id;
            final boolean z = forceDocument;
            final boolean z2 = groupPhotos;
            final MessageObject messageObject = reply_to_msg;
            final InputContentInfoCompat inputContentInfoCompat = inputContent;
            mediaSendQueue.postRunnable(new Runnable() {
                /* JADX WARNING: inconsistent code. */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    HashMap<SendingMediaInfo, MediaSendPrepareWorker> workers;
                    int a;
                    final SendingMediaInfo info;
                    String originalPath;
                    String tempPath;
                    TL_photo photo;
                    long beginTime = System.currentTimeMillis();
                    int count = arrayList.size();
                    boolean isEncrypted = ((int) j) == 0;
                    int enryptedLayer = 0;
                    if (isEncrypted) {
                        EncryptedChat encryptedChat = MessagesController.getInstance(currentAccount).getEncryptedChat(Integer.valueOf((int) (j >> 32)));
                        if (encryptedChat != null) {
                            enryptedLayer = AndroidUtilities.getPeerLayerVersion(encryptedChat.layer);
                        }
                    }
                    if ((!isEncrypted || enryptedLayer >= 73) && !z && z2) {
                        workers = new HashMap();
                        for (a = 0; a < count; a++) {
                            info = (SendingMediaInfo) arrayList.get(a);
                            if (info.searchImage == null && !info.isVideo) {
                                originalPath = info.path;
                                tempPath = info.path;
                                if (tempPath == null && info.uri != null) {
                                    tempPath = AndroidUtilities.getPath(info.uri);
                                    originalPath = info.uri.toString();
                                }
                                if (tempPath != null) {
                                    if (!tempPath.endsWith(".gif")) {
                                        if (tempPath.endsWith(".webp")) {
                                        }
                                    }
                                }
                                if (!(tempPath == null && info.uri != null && (MediaController.isGif(info.uri) || MediaController.isWebp(info.uri)))) {
                                    if (tempPath != null) {
                                        File temp = new File(tempPath);
                                        originalPath = originalPath + temp.length() + "_" + temp.lastModified();
                                    } else {
                                        originalPath = null;
                                    }
                                    photo = null;
                                    if (!isEncrypted && info.ttl == 0) {
                                        photo = (TL_photo) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, !isEncrypted ? 0 : 3);
                                        if (photo == null && info.uri != null) {
                                            photo = (TL_photo) MessagesStorage.getInstance(currentAccount).getSentFile(AndroidUtilities.getPath(info.uri), !isEncrypted ? 0 : 3);
                                        }
                                    }
                                    MediaSendPrepareWorker mediaSendPrepareWorker = new MediaSendPrepareWorker();
                                    workers.put(info, mediaSendPrepareWorker);
                                    if (photo != null) {
                                        mediaSendPrepareWorker.photo = photo;
                                    } else {
                                        mediaSendPrepareWorker.sync = new CountDownLatch(1);
                                        final MediaSendPrepareWorker mediaSendPrepareWorker2 = mediaSendPrepareWorker;
                                        SendMessagesHelper.mediaSendThreadPool.execute(new Runnable() {
                                            public void run() {
                                                mediaSendPrepareWorker2.photo = SendMessagesHelper.getInstance(currentAccount).generatePhotoSizes(info.path, info.uri);
                                                mediaSendPrepareWorker2.sync.countDown();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    } else {
                        workers = null;
                    }
                    long groupId = 0;
                    long lastGroupId = 0;
                    ArrayList<String> sendAsDocuments = null;
                    ArrayList<String> sendAsDocumentsOriginal = null;
                    ArrayList<String> sendAsDocumentsCaptions = null;
                    String extension = null;
                    int photosCount = 0;
                    a = 0;
                    while (a < count) {
                        info = (SendingMediaInfo) arrayList.get(a);
                        if (z2 && ((!isEncrypted || enryptedLayer >= 73) && count > 1 && photosCount % 10 == 0)) {
                            groupId = Utilities.random.nextLong();
                            lastGroupId = groupId;
                            photosCount = 0;
                        }
                        final HashMap<String, String> params;
                        TL_document tL_document;
                        File file;
                        String originalPathFinal;
                        final TL_photo photoFinal;
                        if (info.searchImage != null) {
                            if (info.searchImage.type == 1) {
                                File cacheFile;
                                params = new HashMap();
                                tL_document = null;
                                if (info.searchImage.document instanceof TL_document) {
                                    tL_document = (TL_document) info.searchImage.document;
                                    cacheFile = FileLoader.getPathToAttach(tL_document, true);
                                } else {
                                    if (!isEncrypted) {
                                        Document doc = (Document) MessagesStorage.getInstance(currentAccount).getSentFile(info.searchImage.imageUrl, !isEncrypted ? 1 : 4);
                                        if (doc instanceof TL_document) {
                                            tL_document = (TL_document) doc;
                                        }
                                    }
                                    file = new File(FileLoader.getDirectory(4), Utilities.MD5(info.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(info.searchImage.imageUrl, "jpg"));
                                }
                                if (tL_document == null) {
                                    if (info.searchImage.localUrl != null) {
                                        params.put(UpdateFragment.FRAGMENT_URL, info.searchImage.localUrl);
                                    }
                                    File thumbFile = null;
                                    tL_document = new TL_document();
                                    tL_document.id = 0;
                                    tL_document.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                                    TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                                    fileName.file_name = "animation.gif";
                                    tL_document.attributes.add(fileName);
                                    tL_document.size = info.searchImage.size;
                                    tL_document.dc_id = 0;
                                    if (cacheFile.toString().endsWith("mp4")) {
                                        tL_document.mime_type = MimeTypes.VIDEO_MP4;
                                        tL_document.attributes.add(new TL_documentAttributeAnimated());
                                    } else {
                                        tL_document.mime_type = "image/gif";
                                    }
                                    if (cacheFile.exists()) {
                                        thumbFile = cacheFile;
                                    } else {
                                        cacheFile = null;
                                    }
                                    if (thumbFile == null) {
                                        file = new File(FileLoader.getDirectory(4), Utilities.MD5(info.searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(info.searchImage.thumbUrl, "jpg"));
                                        if (!file.exists()) {
                                            thumbFile = null;
                                        }
                                    }
                                    if (thumbFile != null) {
                                        try {
                                            Bitmap bitmap;
                                            if (thumbFile.getAbsolutePath().endsWith("mp4")) {
                                                bitmap = ThumbnailUtils.createVideoThumbnail(thumbFile.getAbsolutePath(), 1);
                                            } else {
                                                bitmap = ImageLoader.loadBitmap(thumbFile.getAbsolutePath(), null, 90.0f, 90.0f, true);
                                            }
                                            if (bitmap != null) {
                                                tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, isEncrypted);
                                                bitmap.recycle();
                                            }
                                        } catch (Throwable e) {
                                            FileLog.e(e);
                                        }
                                    }
                                    if (tL_document.thumb == null) {
                                        tL_document.thumb = new TL_photoSize();
                                        tL_document.thumb.w = info.searchImage.width;
                                        tL_document.thumb.h = info.searchImage.height;
                                        tL_document.thumb.size = 0;
                                        tL_document.thumb.location = new TL_fileLocationUnavailable();
                                        tL_document.thumb.type = "x";
                                    }
                                }
                                tL_document.caption = info.caption;
                                TL_document documentFinal = tL_document;
                                originalPathFinal = info.searchImage.imageUrl;
                                String pathFinal = cacheFile == null ? info.searchImage.imageUrl : cacheFile.toString();
                                if (!(params == null || info.searchImage.imageUrl == null)) {
                                    params.put("originalPath", info.searchImage.imageUrl);
                                }
                                final TL_document tL_document2 = documentFinal;
                                final String str = pathFinal;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        SendMessagesHelper.getInstance(currentAccount).sendMessage(tL_document2, null, str, j, messageObject, null, params, 0);
                                    }
                                });
                            } else {
                                boolean needDownloadHttp = true;
                                photo = null;
                                if (!isEncrypted && info.ttl == 0) {
                                    photo = (TL_photo) MessagesStorage.getInstance(currentAccount).getSentFile(info.searchImage.imageUrl, !isEncrypted ? 0 : 3);
                                }
                                if (photo == null) {
                                    file = new File(FileLoader.getDirectory(4), Utilities.MD5(info.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(info.searchImage.imageUrl, "jpg"));
                                    if (file.exists() && file.length() != 0) {
                                        photo = SendMessagesHelper.getInstance(currentAccount).generatePhotoSizes(file.toString(), null);
                                        if (photo != null) {
                                            needDownloadHttp = false;
                                        }
                                    }
                                    if (photo == null) {
                                        file = new File(FileLoader.getDirectory(4), Utilities.MD5(info.searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(info.searchImage.thumbUrl, "jpg"));
                                        if (file.exists()) {
                                            photo = SendMessagesHelper.getInstance(currentAccount).generatePhotoSizes(file.toString(), null);
                                        }
                                        if (photo == null) {
                                            photo = new TL_photo();
                                            photo.date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
                                            TL_photoSize photoSize = new TL_photoSize();
                                            photoSize.w = info.searchImage.width;
                                            photoSize.h = info.searchImage.height;
                                            photoSize.size = 0;
                                            photoSize.location = new TL_fileLocationUnavailable();
                                            photoSize.type = "x";
                                            photo.sizes.add(photoSize);
                                        }
                                    }
                                }
                                if (photo != null) {
                                    photo.caption = info.caption;
                                    photoFinal = photo;
                                    final boolean needDownloadHttpFinal = needDownloadHttp;
                                    params = new HashMap();
                                    if (info.searchImage.imageUrl != null) {
                                        params.put("originalPath", info.searchImage.imageUrl);
                                    }
                                    if (z2) {
                                        photosCount++;
                                        params.put("groupId", TtmlNode.ANONYMOUS_REGION_ID + groupId);
                                        if (photosCount == 10 || a == count - 1) {
                                            params.put("final", "1");
                                            lastGroupId = 0;
                                        }
                                    }
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            String str;
                                            SendMessagesHelper instance = SendMessagesHelper.getInstance(currentAccount);
                                            TL_photo tL_photo = photoFinal;
                                            if (needDownloadHttpFinal) {
                                                str = info.searchImage.imageUrl;
                                            } else {
                                                str = null;
                                            }
                                            instance.sendMessage(tL_photo, str, j, messageObject, null, params, info.ttl);
                                        }
                                    });
                                }
                            }
                        } else if (info.isVideo) {
                            VideoEditedInfo videoEditedInfo;
                            Bitmap thumb = null;
                            if (z) {
                                videoEditedInfo = null;
                            } else if (info.videoEditedInfo != null) {
                                videoEditedInfo = info.videoEditedInfo;
                            } else {
                                videoEditedInfo = SendMessagesHelper.createCompressionSettings(info.path);
                            }
                            if (z || (videoEditedInfo == null && !info.path.endsWith("mp4"))) {
                                SendMessagesHelper.prepareSendingDocumentInternal(currentAccount, info.path, info.path, null, null, j, messageObject, info.caption);
                            } else {
                                String path = info.path;
                                originalPath = info.path;
                                file = new File(originalPath);
                                long startTime = 0;
                                boolean muted = false;
                                originalPath = originalPath + file.length() + "_" + file.lastModified();
                                if (videoEditedInfo != null) {
                                    muted = videoEditedInfo.muted;
                                    originalPath = originalPath + videoEditedInfo.estimatedDuration + "_" + videoEditedInfo.startTime + "_" + videoEditedInfo.endTime + (videoEditedInfo.muted ? "_m" : TtmlNode.ANONYMOUS_REGION_ID);
                                    if (videoEditedInfo.resultWidth != videoEditedInfo.originalWidth) {
                                        originalPath = originalPath + "_" + videoEditedInfo.resultWidth;
                                    }
                                    startTime = videoEditedInfo.startTime >= 0 ? videoEditedInfo.startTime : 0;
                                }
                                tL_document = null;
                                if (!isEncrypted && info.ttl == 0) {
                                    tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, !isEncrypted ? 2 : 5);
                                }
                                if (tL_document == null) {
                                    TL_documentAttributeVideo attributeVideo;
                                    thumb = SendMessagesHelper.createVideoThumbnail(info.path, startTime);
                                    if (thumb == null) {
                                        thumb = ThumbnailUtils.createVideoThumbnail(info.path, 1);
                                    }
                                    PhotoSize size = ImageLoader.scaleAndSaveImage(thumb, 90.0f, 90.0f, 55, isEncrypted);
                                    if (!(thumb == null || size == null)) {
                                        thumb = null;
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
                                    if (!isEncrypted) {
                                        attributeVideo = new TL_documentAttributeVideo();
                                    } else if (enryptedLayer >= 66) {
                                        attributeVideo = new TL_documentAttributeVideo();
                                    } else {
                                        attributeVideo = new TL_documentAttributeVideo_layer65();
                                    }
                                    tL_document.attributes.add(attributeVideo);
                                    if (videoEditedInfo == null || !videoEditedInfo.needConvert()) {
                                        if (file.exists()) {
                                            tL_document.size = (int) file.length();
                                        }
                                        SendMessagesHelper.fillVideoAttribute(info.path, attributeVideo, null);
                                    } else {
                                        if (videoEditedInfo.muted) {
                                            tL_document.attributes.add(new TL_documentAttributeAnimated());
                                            SendMessagesHelper.fillVideoAttribute(info.path, attributeVideo, videoEditedInfo);
                                            videoEditedInfo.originalWidth = attributeVideo.w;
                                            videoEditedInfo.originalHeight = attributeVideo.h;
                                            attributeVideo.w = videoEditedInfo.resultWidth;
                                            attributeVideo.h = videoEditedInfo.resultHeight;
                                        } else {
                                            attributeVideo.duration = (int) (videoEditedInfo.estimatedDuration / 1000);
                                            if (videoEditedInfo.rotationValue == 90 || videoEditedInfo.rotationValue == 270) {
                                                attributeVideo.w = videoEditedInfo.resultHeight;
                                                attributeVideo.h = videoEditedInfo.resultWidth;
                                            } else {
                                                attributeVideo.w = videoEditedInfo.resultWidth;
                                                attributeVideo.h = videoEditedInfo.resultHeight;
                                            }
                                        }
                                        tL_document.size = (int) videoEditedInfo.estimatedSize;
                                        String fileName2 = "-2147483648_" + SharedConfig.lastLocalId + ".mp4";
                                        SharedConfig.lastLocalId--;
                                        file = new File(FileLoader.getDirectory(4), fileName2);
                                        SharedConfig.saveConfig();
                                        path = file.getAbsolutePath();
                                    }
                                }
                                final TL_document videoFinal = tL_document;
                                originalPathFinal = originalPath;
                                final String finalPath = path;
                                params = new HashMap();
                                final Bitmap thumbFinal = thumb;
                                final String thumbKeyFinal = null;
                                videoFinal.caption = info.caption != null ? info.caption : TtmlNode.ANONYMOUS_REGION_ID;
                                if (originalPath != null) {
                                    params.put("originalPath", originalPath);
                                }
                                if (!muted && z2) {
                                    photosCount++;
                                    params.put("groupId", TtmlNode.ANONYMOUS_REGION_ID + groupId);
                                    if (photosCount == 10 || a == count - 1) {
                                        params.put("final", "1");
                                        lastGroupId = 0;
                                    }
                                }
                                final HashMap<String, String> hashMap = params;
                                final SendingMediaInfo sendingMediaInfo = info;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (!(thumbFinal == null || thumbKeyFinal == null)) {
                                            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(thumbFinal), thumbKeyFinal);
                                        }
                                        SendMessagesHelper.getInstance(currentAccount).sendMessage(videoFinal, videoEditedInfo, finalPath, j, messageObject, null, hashMap, sendingMediaInfo.ttl);
                                    }
                                });
                            }
                        } else {
                            originalPath = info.path;
                            tempPath = info.path;
                            if (tempPath == null && info.uri != null) {
                                tempPath = AndroidUtilities.getPath(info.uri);
                                originalPath = info.uri.toString();
                            }
                            boolean isDocument = false;
                            if (z) {
                                isDocument = true;
                                extension = FileLoader.getFileExtension(new File(tempPath));
                            } else {
                                if (tempPath != null) {
                                    if (!tempPath.endsWith(".gif")) {
                                    }
                                    if (tempPath.endsWith(".gif")) {
                                        extension = "gif";
                                    } else {
                                        extension = "webp";
                                    }
                                    isDocument = true;
                                }
                                if (tempPath == null && info.uri != null) {
                                    if (MediaController.isGif(info.uri)) {
                                        isDocument = true;
                                        originalPath = info.uri.toString();
                                        tempPath = MediaController.copyFileToCache(info.uri, "gif");
                                        extension = "gif";
                                    } else if (MediaController.isWebp(info.uri)) {
                                        isDocument = true;
                                        originalPath = info.uri.toString();
                                        tempPath = MediaController.copyFileToCache(info.uri, "webp");
                                        extension = "webp";
                                    }
                                }
                            }
                            if (isDocument) {
                                if (sendAsDocuments == null) {
                                    sendAsDocuments = new ArrayList();
                                    sendAsDocumentsOriginal = new ArrayList();
                                    sendAsDocumentsCaptions = new ArrayList();
                                }
                                sendAsDocuments.add(tempPath);
                                sendAsDocumentsOriginal.add(originalPath);
                                sendAsDocumentsCaptions.add(info.caption);
                            } else {
                                if (tempPath != null) {
                                    temp = new File(tempPath);
                                    originalPath = originalPath + temp.length() + "_" + temp.lastModified();
                                } else {
                                    originalPath = null;
                                }
                                photo = null;
                                if (workers != null) {
                                    MediaSendPrepareWorker worker = (MediaSendPrepareWorker) workers.get(info);
                                    photo = worker.photo;
                                    if (photo == null) {
                                        try {
                                            worker.sync.await();
                                        } catch (Throwable e2) {
                                            FileLog.e("tmessages", e2);
                                        }
                                        photo = worker.photo;
                                    }
                                } else {
                                    if (!isEncrypted && info.ttl == 0) {
                                        photo = (TL_photo) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, !isEncrypted ? 0 : 3);
                                        if (photo == null && info.uri != null) {
                                            photo = (TL_photo) MessagesStorage.getInstance(currentAccount).getSentFile(AndroidUtilities.getPath(info.uri), !isEncrypted ? 0 : 3);
                                        }
                                    }
                                    if (photo == null) {
                                        photo = SendMessagesHelper.getInstance(currentAccount).generatePhotoSizes(info.path, info.uri);
                                    }
                                }
                                if (photo != null) {
                                    photoFinal = photo;
                                    params = new HashMap();
                                    photo.caption = info.caption;
                                    boolean z = (info.masks == null || info.masks.isEmpty()) ? false : true;
                                    photo.has_stickers = z;
                                    if (z) {
                                        AbstractSerializedData serializedData = new SerializedData((info.masks.size() * 20) + 4);
                                        serializedData.writeInt32(info.masks.size());
                                        for (int b = 0; b < info.masks.size(); b++) {
                                            ((InputDocument) info.masks.get(b)).serializeToStream(serializedData);
                                        }
                                        params.put("masks", Utilities.bytesToHex(serializedData.toByteArray()));
                                    }
                                    if (originalPath != null) {
                                        params.put("originalPath", originalPath);
                                    }
                                    if (z2) {
                                        photosCount++;
                                        params.put("groupId", TtmlNode.ANONYMOUS_REGION_ID + groupId);
                                        if (photosCount == 10 || a == count - 1) {
                                            params.put("final", "1");
                                            lastGroupId = 0;
                                        }
                                    }
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            SendMessagesHelper.getInstance(currentAccount).sendMessage(photoFinal, null, j, messageObject, null, params, info.ttl);
                                        }
                                    });
                                } else {
                                    if (sendAsDocuments == null) {
                                        sendAsDocuments = new ArrayList();
                                        sendAsDocumentsOriginal = new ArrayList();
                                        sendAsDocumentsCaptions = new ArrayList();
                                    }
                                    sendAsDocuments.add(tempPath);
                                    sendAsDocumentsOriginal.add(originalPath);
                                    sendAsDocumentsCaptions.add(info.caption);
                                }
                            }
                        }
                        a++;
                    }
                    if (lastGroupId != 0) {
                        final long j = lastGroupId;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                SendMessagesHelper instance = SendMessagesHelper.getInstance(currentAccount);
                                ArrayList<DelayedMessage> arrayList = (ArrayList) instance.delayedMessages.get("group_" + j);
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
                        });
                    }
                    if (inputContentInfoCompat != null) {
                        inputContentInfoCompat.releasePermission();
                    }
                    if (!(sendAsDocuments == null || sendAsDocuments.isEmpty())) {
                        for (a = 0; a < sendAsDocuments.size(); a++) {
                            SendMessagesHelper.prepareSendingDocumentInternal(currentAccount, (String) sendAsDocuments.get(a), (String) sendAsDocumentsOriginal.get(a), null, extension, j, messageObject, (CharSequence) sendAsDocumentsCaptions.get(a));
                        }
                    }
                    FileLog.d("total send time = " + (System.currentTimeMillis() - beginTime));
                }
            });
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
                        FileLog.e(e2);
                        mediaMetadataRetriever = mediaMetadataRetriever2;
                    }
                }
                mediaMetadataRetriever = mediaMetadataRetriever2;
            } catch (Exception e3) {
                e2 = e3;
                mediaMetadataRetriever = mediaMetadataRetriever2;
                try {
                    FileLog.e(e2);
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Throwable e22) {
                            FileLog.e(e22);
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
                            FileLog.e(e222);
                            return;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Throwable e2222) {
                            FileLog.e(e2222);
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
            FileLog.e(e2222);
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
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
        } catch (Throwable th) {
            try {
                retriever.release();
            } catch (RuntimeException e3) {
            }
        }
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int max = Math.max(width, height);
        if (max > 90) {
            float scale = 90.0f / ((float) max);
            bitmap = Bitmaps.createScaledBitmap(bitmap, Math.round(((float) width) * scale), Math.round(((float) height) * scale), true);
        }
        return bitmap;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static VideoEditedInfo createCompressionSettings(String videoPath) {
        TrackHeaderBox trackHeaderBox = null;
        int originalBitrate = 0;
        int bitrate = 0;
        float videoDuration = 0.0f;
        long videoFramesSize = 0;
        long audioFramesSize = 0;
        IsoFile isoFile = new IsoFile(videoPath);
        List<Box> boxes = Path.getPaths(isoFile, "/moov/trak/");
        if (Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") == null) {
            FileLog.d("video hasn't mp4a atom");
        }
        if (Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null) {
            FileLog.d("video hasn't avc1 atom");
            return null;
        }
        for (int b = 0; b < boxes.size(); b++) {
            TrackBox trackBox = (TrackBox) ((Box) boxes.get(b));
            long sampleSizes = 0;
            long trackBitrate = 0;
            try {
                MediaBox mediaBox = trackBox.getMediaBox();
                MediaHeaderBox mediaHeaderBox = mediaBox.getMediaHeaderBox();
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
            }
        }
        if (trackHeaderBox == null) {
            FileLog.d("video hasn't trackHeaderBox atom");
            return null;
        }
        int compressionsCount;
        if (VERSION.SDK_INT < 18) {
            try {
                MediaCodecInfo codecInfo = MediaController.selectCodec("video/avc");
                if (codecInfo == null) {
                    FileLog.d("no codec info for video/avc");
                    return null;
                }
                String name = codecInfo.getName();
                if (name.equals("OMX.google.h264.encoder") || name.equals("OMX.ST.VFM.H264Enc") || name.equals("OMX.Exynos.avc.enc") || name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") || name.equals("OMX.MARVELL.VIDEO.H264ENCODER") || name.equals("OMX.k3.video.encoder.avc") || name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                    FileLog.d("unsupported encoder = " + name);
                    return null;
                } else if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                    FileLog.d("no color format for video/avc");
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

    public static void prepareSendingVideo(String videoPath, long estimatedSize, long duration, int width, int height, VideoEditedInfo info, long dialog_id, MessageObject reply_to_msg, CharSequence caption, int ttl) {
        if (videoPath != null && videoPath.length() != 0) {
            final int currentAccount = UserConfig.selectedAccount;
            final VideoEditedInfo videoEditedInfo = info;
            final String str = videoPath;
            final long j = dialog_id;
            final long j2 = duration;
            final int i = ttl;
            final int i2 = height;
            final int i3 = width;
            final long j3 = estimatedSize;
            final CharSequence charSequence = caption;
            final MessageObject messageObject = reply_to_msg;
            new Thread(new Runnable() {
                public void run() {
                    final VideoEditedInfo videoEditedInfo = videoEditedInfo != null ? videoEditedInfo : SendMessagesHelper.createCompressionSettings(str);
                    boolean isEncrypted = ((int) j) == 0;
                    boolean isRound = videoEditedInfo != null && videoEditedInfo.roundVideo;
                    Bitmap thumb = null;
                    String thumbKey = null;
                    if (videoEditedInfo != null || str.endsWith("mp4") || isRound) {
                        String path = str;
                        String originalPath = str;
                        File file = new File(originalPath);
                        long startTime = 0;
                        originalPath = originalPath + file.length() + "_" + file.lastModified();
                        if (videoEditedInfo != null) {
                            if (!isRound) {
                                originalPath = originalPath + j2 + "_" + videoEditedInfo.startTime + "_" + videoEditedInfo.endTime + (videoEditedInfo.muted ? "_m" : TtmlNode.ANONYMOUS_REGION_ID);
                                if (videoEditedInfo.resultWidth != videoEditedInfo.originalWidth) {
                                    originalPath = originalPath + "_" + videoEditedInfo.resultWidth;
                                }
                            }
                            startTime = videoEditedInfo.startTime >= 0 ? videoEditedInfo.startTime : 0;
                        }
                        TL_document tL_document = null;
                        if (!isEncrypted && i == 0) {
                            tL_document = (TL_document) MessagesStorage.getInstance(currentAccount).getSentFile(originalPath, !isEncrypted ? 2 : 5);
                        }
                        if (tL_document == null) {
                            TL_documentAttributeVideo attributeVideo;
                            thumb = SendMessagesHelper.createVideoThumbnail(str, startTime);
                            if (thumb == null) {
                                thumb = ThumbnailUtils.createVideoThumbnail(str, 1);
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
                                EncryptedChat encryptedChat = MessagesController.getInstance(currentAccount).getEncryptedChat(Integer.valueOf((int) (j >> 32)));
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
                            attributeVideo.round_message = isRound;
                            tL_document.attributes.add(attributeVideo);
                            if (videoEditedInfo == null || !videoEditedInfo.needConvert()) {
                                if (file.exists()) {
                                    tL_document.size = (int) file.length();
                                }
                                SendMessagesHelper.fillVideoAttribute(str, attributeVideo, null);
                            } else {
                                if (videoEditedInfo.muted) {
                                    tL_document.attributes.add(new TL_documentAttributeAnimated());
                                    SendMessagesHelper.fillVideoAttribute(str, attributeVideo, videoEditedInfo);
                                    videoEditedInfo.originalWidth = attributeVideo.w;
                                    videoEditedInfo.originalHeight = attributeVideo.h;
                                    attributeVideo.w = videoEditedInfo.resultWidth;
                                    attributeVideo.h = videoEditedInfo.resultHeight;
                                } else {
                                    attributeVideo.duration = (int) (j2 / 1000);
                                    if (videoEditedInfo.rotationValue == 90 || videoEditedInfo.rotationValue == 270) {
                                        attributeVideo.w = i2;
                                        attributeVideo.h = i3;
                                    } else {
                                        attributeVideo.w = i3;
                                        attributeVideo.h = i2;
                                    }
                                }
                                tL_document.size = (int) j3;
                                String fileName = "-2147483648_" + SharedConfig.lastLocalId + ".mp4";
                                SharedConfig.lastLocalId--;
                                file = new File(FileLoader.getDirectory(4), fileName);
                                SharedConfig.saveConfig();
                                path = file.getAbsolutePath();
                            }
                        }
                        final TL_document videoFinal = tL_document;
                        String originalPathFinal = originalPath;
                        final String finalPath = path;
                        final HashMap<String, String> params = new HashMap();
                        final Bitmap thumbFinal = thumb;
                        final String thumbKeyFinal = thumbKey;
                        if (charSequence != null) {
                            videoFinal.caption = charSequence.toString();
                        } else {
                            videoFinal.caption = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        if (originalPath != null) {
                            params.put("originalPath", originalPath);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (!(thumbFinal == null || thumbKeyFinal == null)) {
                                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(thumbFinal), thumbKeyFinal);
                                }
                                SendMessagesHelper.getInstance(currentAccount).sendMessage(videoFinal, videoEditedInfo, finalPath, j, messageObject, null, params, i);
                            }
                        });
                        return;
                    }
                    SendMessagesHelper.prepareSendingDocumentInternal(currentAccount, str, str, null, null, j, messageObject, charSequence);
                }
            }).start();
        }
    }
}
