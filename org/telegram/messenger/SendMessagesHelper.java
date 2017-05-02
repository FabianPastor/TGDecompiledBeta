package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.query.DraftQuery;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.query.StickersQuery;
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
import org.telegram.tgnet.TLRPC.TL_fileLocation;
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
import org.telegram.tgnet.TLRPC.TL_inputMediaGeoPoint;
import org.telegram.tgnet.TLRPC.TL_inputMediaGifExternal;
import org.telegram.tgnet.TLRPC.TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputMediaUploadedThumbDocument;
import org.telegram.tgnet.TLRPC.TL_inputMediaVenue;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPhoto;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC.TL_message;
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
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.telegram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
import org.telegram.tgnet.TLRPC.TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC.TL_messages_sendMessage;
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
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.PaymentFormActivity;

public class SendMessagesHelper implements NotificationCenterDelegate {
    private static volatile SendMessagesHelper Instance = null;
    private ChatFull currentChatInfo = null;
    private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap();
    private LocationProvider locationProvider = new LocationProvider(new LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            SendMessagesHelper.this.sendLocation(location);
            SendMessagesHelper.this.waitingForLocation.clear();
        }

        public void onUnableLocationAcquire() {
            HashMap<String, MessageObject> waitingForLocationCopy = new HashMap(SendMessagesHelper.this.waitingForLocation);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, waitingForLocationCopy);
            SendMessagesHelper.this.waitingForLocation.clear();
        }
    });
    private HashMap<Integer, Message> sendingMessages = new HashMap();
    private HashMap<Integer, MessageObject> unsentMessages = new HashMap();
    private HashMap<String, MessageObject> waitingForCallback = new HashMap();
    private HashMap<String, MessageObject> waitingForLocation = new HashMap();

    protected class DelayedMessage {
        public TL_document documentLocation;
        public EncryptedChat encryptedChat;
        public String httpLocation;
        public FileLocation location;
        public MessageObject obj;
        public String originalPath;
        public TL_decryptedMessage sendEncryptedRequest;
        public TLObject sendRequest;
        public int type;
        public VideoEditedInfo videoEditedInfo;

        protected DelayedMessage() {
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
                this.locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService(Param.LOCATION);
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

    public static SendMessagesHelper getInstance() {
        SendMessagesHelper localInstance = Instance;
        if (localInstance == null) {
            synchronized (SendMessagesHelper.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        SendMessagesHelper localInstance2 = new SendMessagesHelper();
                        try {
                            Instance = localInstance2;
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

    public SendMessagesHelper() {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
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

    public void didReceivedNotification(int id, Object... args) {
        String location;
        ArrayList<DelayedMessage> arr;
        int a;
        DelayedMessage message;
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
                    }
                    if (file != null && media != null) {
                        if (message.type == 0) {
                            media.file = file;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
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
                                performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                            }
                        } else if (message.type == 3) {
                            media.file = file;
                            performSendMessageRequest(message.sendRequest, message.obj, message.originalPath);
                        }
                        arr.remove(a);
                        a--;
                    } else if (!(encryptedFile == null || message.sendEncryptedRequest == null)) {
                        if ((message.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaVideo) || (message.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaPhoto) || (message.sendEncryptedRequest.media instanceof TL_decryptedMessageMediaDocument)) {
                            message.sendEncryptedRequest.media.size = (int) ((Long) args[5]).longValue();
                        }
                        message.sendEncryptedRequest.media.key = (byte[]) args[3];
                        message.sendEncryptedRequest.media.iv = (byte[]) args[4];
                        SecretChatHelper.getInstance().performSendEncryptedRequest(message.sendEncryptedRequest, message.obj.messageOwner, message.encryptedChat, encryptedFile, message.originalPath, message.obj);
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
                        MessagesStorage.getInstance().markMessageAsSendError(obj.obj.messageOwner);
                        obj.obj.messageOwner.send_state = 2;
                        arr.remove(a);
                        a--;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(obj.obj.getId()));
                        processSentMessage(obj.obj.getId());
                    }
                    a++;
                }
                if (arr.isEmpty()) {
                    this.delayedMessages.remove(location);
                }
            }
        } else if (id == NotificationCenter.FilePreparingStarted) {
            messageObject = args[0];
            if (messageObject.getId() != 0) {
                finalPath = args[1];
                arr = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                if (arr != null) {
                    for (a = 0; a < arr.size(); a++) {
                        message = (DelayedMessage) arr.get(a);
                        if (message.obj == messageObject) {
                            message.videoEditedInfo = null;
                            performSendDelayedMessage(message);
                            arr.remove(a);
                            break;
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
                long finalSize = ((Long) args[2]).longValue();
                FileLoader.getInstance().checkUploadNewDataAvailable(finalPath, ((int) messageObject.getDialogId()) == 0, finalSize);
                if (finalSize != 0) {
                    arr = (ArrayList) this.delayedMessages.get(messageObject.messageOwner.attachPath);
                    if (arr != null) {
                        for (a = 0; a < arr.size(); a++) {
                            message = (DelayedMessage) arr.get(a);
                            if (message.obj == messageObject) {
                                message.obj.videoEditedInfo = null;
                                message.obj.messageOwner.message = "-1";
                                message.obj.messageOwner.media.document.size = (int) finalSize;
                                ArrayList messages = new ArrayList();
                                messages.add(message.obj.messageOwner);
                                MessagesStorage.getInstance().putMessages(messages, false, true, false, 0);
                                break;
                            }
                        }
                        if (arr.isEmpty()) {
                            this.delayedMessages.remove(messageObject.messageOwner.attachPath);
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
                        if (message.obj == messageObject) {
                            MessagesStorage.getInstance().markMessageAsSendError(message.obj.messageOwner);
                            message.obj.messageOwner.send_state = 2;
                            arr.remove(a);
                            a--;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.obj.getId()));
                            processSentMessage(message.obj.getId());
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
                    if (message.type == 0) {
                        final File file2 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(message.httpLocation) + "." + ImageLoader.getHttpUrlExtension(message.httpLocation, "file"));
                        final DelayedMessage delayedMessage = message;
                        Utilities.globalQueue.postRunnable(new Runnable() {
                            public void run() {
                                final TL_photo photo = SendMessagesHelper.getInstance().generatePhotoSizes(file2.toString(), null);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        if (photo != null) {
                                            delayedMessage.httpLocation = null;
                                            delayedMessage.obj.messageOwner.media.photo = photo;
                                            delayedMessage.obj.messageOwner.attachPath = file2.toString();
                                            delayedMessage.location = ((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location;
                                            ArrayList messages = new ArrayList();
                                            messages.add(delayedMessage.obj.messageOwner);
                                            MessagesStorage.getInstance().putMessages(messages, false, true, false, 0);
                                            SendMessagesHelper.this.performSendDelayedMessage(delayedMessage);
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, delayedMessage.obj);
                                            return;
                                        }
                                        FileLog.e("can't load image " + delayedMessage.httpLocation + " to file " + file2.toString());
                                        MessagesStorage.getInstance().markMessageAsSendError(delayedMessage.obj.messageOwner);
                                        delayedMessage.obj.messageOwner.send_state = 2;
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(delayedMessage.obj.getId()));
                                        SendMessagesHelper.this.processSentMessage(delayedMessage.obj.getId());
                                    }
                                });
                            }
                        });
                    } else if (message.type == 2) {
                        final DelayedMessage delayedMessage2 = message;
                        final File file3 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(message.httpLocation) + ".gif");
                        Utilities.globalQueue.postRunnable(new Runnable() {
                            public void run() {
                                boolean z = true;
                                if (delayedMessage2.documentLocation.thumb.location instanceof TL_fileLocationUnavailable) {
                                    try {
                                        Bitmap bitmap = ImageLoader.loadBitmap(file3.getAbsolutePath(), null, 90.0f, 90.0f, true);
                                        if (bitmap != null) {
                                            TL_document tL_document = delayedMessage2.documentLocation;
                                            if (delayedMessage2.sendEncryptedRequest == null) {
                                                z = false;
                                            }
                                            tL_document.thumb = ImageLoader.scaleAndSaveImage(bitmap, 90.0f, 90.0f, 55, z);
                                            bitmap.recycle();
                                        }
                                    } catch (Throwable e) {
                                        delayedMessage2.documentLocation.thumb = null;
                                        FileLog.e(e);
                                    }
                                    if (delayedMessage2.documentLocation.thumb == null) {
                                        delayedMessage2.documentLocation.thumb = new TL_photoSizeEmpty();
                                        delayedMessage2.documentLocation.thumb.type = "s";
                                    }
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        delayedMessage2.httpLocation = null;
                                        delayedMessage2.obj.messageOwner.attachPath = file3.toString();
                                        delayedMessage2.location = delayedMessage2.documentLocation.thumb.location;
                                        ArrayList messages = new ArrayList();
                                        messages.add(delayedMessage2.obj.messageOwner);
                                        MessagesStorage.getInstance().putMessages(messages, false, true, false, 0);
                                        SendMessagesHelper.this.performSendDelayedMessage(delayedMessage2);
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, delayedMessage2.obj);
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
                Iterator it = arr.iterator();
                while (it.hasNext()) {
                    message = (DelayedMessage) it.next();
                    MessagesStorage.getInstance().markMessageAsSendError(message.obj.messageOwner);
                    message.obj.messageOwner.send_state = 2;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(message.obj.getId()));
                    processSentMessage(message.obj.getId());
                }
                this.delayedMessages.remove(path);
            }
        }
    }

    public void cancelSendingMessage(MessageObject object) {
        String keyToRemvoe = null;
        boolean enc = false;
        for (Entry<String, ArrayList<DelayedMessage>> entry : this.delayedMessages.entrySet()) {
            ArrayList<DelayedMessage> messages = (ArrayList) entry.getValue();
            int a = 0;
            while (a < messages.size()) {
                DelayedMessage message = (DelayedMessage) messages.get(a);
                if (message.obj.getId() == object.getId()) {
                    messages.remove(a);
                    MediaController.getInstance().cancelVideoConvert(message.obj);
                    if (messages.size() == 0) {
                        keyToRemvoe = (String) entry.getKey();
                        if (message.sendEncryptedRequest != null) {
                            enc = true;
                        }
                    }
                } else {
                    a++;
                }
            }
        }
        if (keyToRemvoe != null) {
            if (keyToRemvoe.startsWith("http")) {
                ImageLoader.getInstance().cancelLoadHttpFile(keyToRemvoe);
            } else {
                FileLoader.getInstance().cancelUploadFile(keyToRemvoe, enc);
            }
            stopVideoService(keyToRemvoe);
        }
        ArrayList<Integer> messages2 = new ArrayList();
        messages2.add(Integer.valueOf(object.getId()));
        MessagesController.getInstance().deleteMessages(messages2, null, null, object.messageOwner.to_id.channel_id, false);
    }

    public boolean retrySendMessage(MessageObject messageObject, boolean unsent) {
        if (messageObject.getId() >= 0) {
            return false;
        }
        if (messageObject.messageOwner.action instanceof TL_messageEncryptedAction) {
            EncryptedChat encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
            if (encryptedChat == null) {
                MessagesStorage.getInstance().markMessageAsSendError(messageObject.messageOwner);
                messageObject.messageOwner.send_state = 2;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(messageObject.getId()));
                processSentMessage(messageObject.getId());
                return false;
            }
            if (messageObject.messageOwner.random_id == 0) {
                messageObject.messageOwner.random_id = getNextRandomId();
            }
            if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                SecretChatHelper.getInstance().sendTTLMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionDeleteMessages) {
                SecretChatHelper.getInstance().sendMessagesDeleteMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionFlushHistory) {
                SecretChatHelper.getInstance().sendClearHistoryMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNotifyLayer) {
                SecretChatHelper.getInstance().sendNotifyLayerMessage(encryptedChat, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionReadMessages) {
                SecretChatHelper.getInstance().sendMessagesReadMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                SecretChatHelper.getInstance().sendScreenshotMessage(encryptedChat, null, messageObject.messageOwner);
            } else if (!((messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionTyping) || (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionResend))) {
                if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionCommitKey) {
                    SecretChatHelper.getInstance().sendCommitKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAbortKey) {
                    SecretChatHelper.getInstance().sendAbortKeyMessage(encryptedChat, messageObject.messageOwner, 0);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionRequestKey) {
                    SecretChatHelper.getInstance().sendRequestKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionAcceptKey) {
                    SecretChatHelper.getInstance().sendAcceptKeyMessage(encryptedChat, messageObject.messageOwner);
                } else if (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionNoop) {
                    SecretChatHelper.getInstance().sendNoopMessage(encryptedChat, messageObject.messageOwner);
                }
            }
            return true;
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
                sendMessage((TL_photo) messageObject.messageOwner.media.photo, null, did, messageObject.replyMessageObject, null, null);
            } else if (messageObject.messageOwner.media.document instanceof TL_document) {
                sendMessage((TL_document) messageObject.messageOwner.media.document, null, messageObject.messageOwner.attachPath, did, messageObject.replyMessageObject, null, null);
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

    public void sendSticker(Document document, long peer, MessageObject replyingMessageObject) {
        if (document != null) {
            if (((int) peer) == 0) {
                if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (peer >> 32))) != null) {
                    Document newDocument = new TL_document();
                    newDocument.id = document.id;
                    newDocument.access_hash = document.access_hash;
                    newDocument.date = document.date;
                    newDocument.mime_type = document.mime_type;
                    newDocument.size = document.size;
                    newDocument.dc_id = document.dc_id;
                    newDocument.attributes = new ArrayList(document.attributes);
                    if (newDocument.mime_type == null) {
                        newDocument.mime_type = "";
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
            getInstance().sendMessage((TL_document) document, null, null, peer, replyingMessageObject, null, null);
        }
    }

    public void sendMessage(ArrayList<MessageObject> messages, long peer) {
        if (messages != null && !messages.isEmpty()) {
            int lower_id = (int) peer;
            int a;
            if (lower_id != 0) {
                Chat chat;
                final Peer to_id = MessagesController.getPeer((int) peer);
                boolean isMegagroup = false;
                boolean isSignature = false;
                if (lower_id <= 0) {
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                    if (ChatObject.isChannel(chat)) {
                        isMegagroup = chat.megagroup;
                        isSignature = chat.signatures;
                    }
                } else if (MessagesController.getInstance().getUser(Integer.valueOf(lower_id)) == null) {
                    return;
                }
                ArrayList<MessageObject> objArr = new ArrayList();
                ArrayList<Message> arr = new ArrayList();
                ArrayList<Long> randomIds = new ArrayList();
                ArrayList<Integer> ids = new ArrayList();
                HashMap<Long, Message> messagesByRandomIds = new HashMap();
                InputPeer inputPeer = MessagesController.getInputPeer(lower_id);
                int myId = UserConfig.getClientUserId();
                boolean toMyself = peer == ((long) myId);
                a = 0;
                while (a < messages.size()) {
                    MessageObject msgObj = (MessageObject) messages.get(a);
                    if (msgObj.getId() > 0) {
                        Message newMsg = new TL_message();
                        if (msgObj.isForwarded()) {
                            newMsg.fwd_from = msgObj.messageOwner.fwd_from;
                        } else if (msgObj.getDialogId() != ((long) myId)) {
                            newMsg.fwd_from = new TL_messageFwdHeader();
                            TL_messageFwdHeader tL_messageFwdHeader;
                            if (msgObj.isFromUser()) {
                                newMsg.fwd_from.from_id = msgObj.messageOwner.from_id;
                                tL_messageFwdHeader = newMsg.fwd_from;
                                tL_messageFwdHeader.flags |= 1;
                            } else {
                                newMsg.fwd_from.channel_id = msgObj.messageOwner.to_id.channel_id;
                                tL_messageFwdHeader = newMsg.fwd_from;
                                tL_messageFwdHeader.flags |= 2;
                                if (msgObj.messageOwner.post) {
                                    newMsg.fwd_from.channel_post = msgObj.getId();
                                    tL_messageFwdHeader = newMsg.fwd_from;
                                    tL_messageFwdHeader.flags |= 4;
                                    if (msgObj.messageOwner.from_id > 0) {
                                        newMsg.fwd_from.from_id = msgObj.messageOwner.from_id;
                                        tL_messageFwdHeader = newMsg.fwd_from;
                                        tL_messageFwdHeader.flags |= 1;
                                    }
                                }
                            }
                            newMsg.date = msgObj.messageOwner.date;
                        }
                        newMsg.media = msgObj.messageOwner.media;
                        newMsg.flags = 4;
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
                        int newMessageId = UserConfig.getNewMessageId();
                        newMsg.id = newMessageId;
                        newMsg.local_id = newMessageId;
                        newMsg.out = true;
                        if (to_id.channel_id == 0 || isMegagroup) {
                            newMsg.from_id = UserConfig.getClientUserId();
                            newMsg.flags |= 256;
                        } else {
                            newMsg.from_id = isSignature ? UserConfig.getClientUserId() : -to_id.channel_id;
                            newMsg.post = true;
                        }
                        if (newMsg.random_id == 0) {
                            newMsg.random_id = getNextRandomId();
                        }
                        randomIds.add(Long.valueOf(newMsg.random_id));
                        messagesByRandomIds.put(Long.valueOf(newMsg.random_id), newMsg);
                        ids.add(Integer.valueOf(newMsg.fwd_msg_id));
                        newMsg.date = ConnectionsManager.getInstance().getCurrentTime();
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
                        if ((MessageObject.isVoiceMessage(newMsg) || MessageObject.isRoundVideoMessage(newMsg)) && newMsg.to_id.channel_id == 0) {
                            newMsg.media_unread = true;
                        }
                        if (msgObj.messageOwner.to_id instanceof TL_peerChannel) {
                            newMsg.ttl = -msgObj.messageOwner.to_id.channel_id;
                        }
                        MessageObject messageObject = new MessageObject(newMsg, null, true);
                        messageObject.messageOwner.send_state = 1;
                        objArr.add(messageObject);
                        arr.add(newMsg);
                        putToSendingMessages(newMsg);
                        if (BuildVars.DEBUG_VERSION) {
                            FileLog.e("forward message user_id = " + inputPeer.user_id + " chat_id = " + inputPeer.chat_id + " channel_id = " + inputPeer.channel_id + " access_hash = " + inputPeer.access_hash);
                        }
                        if (!(arr.size() == 100 || a == messages.size() - 1)) {
                            if (a != messages.size() - 1) {
                                if (((MessageObject) messages.get(a + 1)).getDialogId() == msgObj.getDialogId()) {
                                }
                            }
                        }
                        MessagesStorage.getInstance().putMessages(new ArrayList(arr), false, true, false, 0);
                        MessagesController.getInstance().updateInterfaceWithMessages(peer, objArr);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        UserConfig.saveConfig(false);
                        final TL_messages_forwardMessages req = new TL_messages_forwardMessages();
                        req.to_peer = inputPeer;
                        if (req.to_peer instanceof TL_inputPeerChannel) {
                            req.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + peer, false);
                        }
                        if (msgObj.messageOwner.to_id instanceof TL_peerChannel) {
                            chat = MessagesController.getInstance().getChat(Integer.valueOf(msgObj.messageOwner.to_id.channel_id));
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
                        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
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
                                    Integer value = (Integer) MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(j));
                                    if (value == null) {
                                        value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, j));
                                        MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(j), value);
                                    }
                                    int sentCount = 0;
                                    for (a = 0; a < updates.updates.size(); a++) {
                                        update = (Update) updates.updates.get(a);
                                        if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateNewChannelMessage)) {
                                            Message message;
                                            if (update instanceof TL_updateNewMessage) {
                                                message = ((TL_updateNewMessage) update).message;
                                                MessagesController.getInstance().processNewDifferenceParams(-1, update.pts, -1, update.pts_count);
                                            } else {
                                                message = ((TL_updateNewChannelMessage) update).message;
                                                MessagesController.getInstance().processNewChannelDifferenceParams(update.pts, update.pts_count, message.to_id.channel_id);
                                                if (isMegagroupFinal) {
                                                    message.flags |= Integer.MIN_VALUE;
                                                }
                                            }
                                            message.unread = value.intValue() < message.id;
                                            if (z2) {
                                                message.out = true;
                                                message.unread = false;
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
                                                        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                                                            public void run() {
                                                                MessagesStorage.getInstance().updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(oldId), newMsgObj.id, 0, false, to_id.channel_id);
                                                                MessagesStorage.getInstance().putMessages(sentMessages, true, false, false, 0);
                                                                AndroidUtilities.runOnUIThread(new Runnable() {
                                                                    public void run() {
                                                                        newMsgObj.send_state = 0;
                                                                        SearchQuery.increasePeerRaiting(j);
                                                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(message.id), message, Long.valueOf(j));
                                                                        SendMessagesHelper.this.processSentMessage(oldId);
                                                                        SendMessagesHelper.this.removeFromSendingMessages(oldId);
                                                                    }
                                                                });
                                                                if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                                                                    SendMessagesHelper.this.stopVideoService(newMsgObj.attachPath);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, sentCount);
                                } else {
                                    final TL_error tL_error = error;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            AlertsCreator.processError(tL_error, null, req, new Object[0]);
                                        }
                                    });
                                }
                                for (a = 0; a < newMsgObjArr.size(); a++) {
                                    newMsgObj = (Message) newMsgObjArr.get(a);
                                    MessagesStorage.getInstance().markMessageAsSendError(newMsgObj);
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            newMsgObj.send_state = 2;
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
                                            SendMessagesHelper.this.processSentMessage(newMsgObj.id);
                                            if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                                                SendMessagesHelper.this.stopVideoService(newMsgObj.attachPath);
                                            }
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
                    }
                    a++;
                }
                return;
            }
            for (a = 0; a < messages.size(); a++) {
                processForwardFromMyName((MessageObject) messages.get(a), peer);
            }
        }
    }

    public int editMessage(MessageObject messageObject, String message, boolean searchLinks, final BaseFragment fragment, ArrayList<MessageEntity> entities, final Runnable callback) {
        boolean z = false;
        if (fragment == null || fragment.getParentActivity() == null || callback == null) {
            return 0;
        }
        final TL_messages_editMessage req = new TL_messages_editMessage();
        req.peer = MessagesController.getInputPeer((int) messageObject.getDialogId());
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
        return ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        callback.run();
                    }
                });
                if (error == null) {
                    MessagesController.getInstance().processUpdates((Updates) response, false);
                } else {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            AlertsCreator.processError(error, fragment, req, new Object[0]);
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
            getInstance().sendMessage(mediaGeo, messageObject.getDialogId(), messageObject, null, null);
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
                                        MessagesStorage.getInstance().saveBotCache(key, res);
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
                                                user = MessagesController.getInstance().getUser(Integer.valueOf(uid));
                                                if (user != null) {
                                                    name = ContactsController.formatName(user.first_name, user.last_name);
                                                }
                                            } else {
                                                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-uid));
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
                                        user = MessagesController.getInstance().getUser(Integer.valueOf(uid));
                                        boolean verified = user != null && user.verified;
                                        if (keyboardButton instanceof TL_keyboardButtonGame) {
                                            TL_game game = messageObject2.messageOwner.media instanceof TL_messageMediaGame ? messageObject2.messageOwner.media.game : null;
                                            if (game != null) {
                                                boolean z;
                                                ChatActivity chatActivity = chatActivity;
                                                MessageObject messageObject = messageObject2;
                                                String str = res.url;
                                                if (verified || !ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("askgame_" + uid, true)) {
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
                                    MessagesController.getInstance().putUsers(form.users, false);
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
                MessagesStorage.getInstance().getBotCache(key, requestDelegate);
            } else if (!(button instanceof TL_keyboardButtonBuy)) {
                TL_messages_getBotCallbackAnswer req = new TL_messages_getBotCallbackAnswer();
                req.peer = MessagesController.getInputPeer((int) messageObject.getDialogId());
                req.msg_id = messageObject.getId();
                req.game = button instanceof TL_keyboardButtonGame;
                if (button.data != null) {
                    req.flags |= 1;
                    req.data = button.data;
                }
                ConnectionsManager.getInstance().sendRequest(req, requestDelegate, 2);
            } else if ((messageObject.messageOwner.media.flags & 4) == 0) {
                TL_payments_getPaymentForm req2 = new TL_payments_getPaymentForm();
                req2.msg_id = messageObject.getId();
                ConnectionsManager.getInstance().sendRequest(req2, requestDelegate, 2);
            } else {
                TL_payments_getPaymentReceipt req3 = new TL_payments_getPaymentReceipt();
                req3.msg_id = messageObject.messageOwner.media.receipt_msg_id;
                ConnectionsManager.getInstance().sendRequest(req3, requestDelegate, 2);
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
                request.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + peer.channel_id, false);
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
                        newTaskId = MessagesStorage.getInstance().createPendingTask(data);
                        ConnectionsManager.getInstance().sendRequest(request, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                if (error == null) {
                                    MessagesController.getInstance().processUpdates((Updates) response, false);
                                }
                                if (newTaskId != 0) {
                                    MessagesStorage.getInstance().removePendingTask(newTaskId);
                                }
                            }
                        });
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e(e);
                    newTaskId = MessagesStorage.getInstance().createPendingTask(data);
                    ConnectionsManager.getInstance().sendRequest(request, /* anonymous class already generated */);
                }
                newTaskId = MessagesStorage.getInstance().createPendingTask(data);
            } else {
                newTaskId = taskId;
            }
            ConnectionsManager.getInstance().sendRequest(request, /* anonymous class already generated */);
        }
    }

    public void sendMessage(MessageObject retryMessageObject) {
        sendMessage(null, null, null, null, null, null, null, retryMessageObject.getDialogId(), retryMessageObject.messageOwner.attachPath, null, null, true, retryMessageObject, null, retryMessageObject.messageOwner.reply_markup, retryMessageObject.messageOwner.params);
    }

    public void sendMessage(User user, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, user, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params);
    }

    public void sendMessage(TL_document document, VideoEditedInfo videoEditedInfo, String path, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, videoEditedInfo, null, document, null, peer, path, reply_to_msg, null, true, null, null, replyMarkup, params);
    }

    public void sendMessage(String message, long peer, MessageObject reply_to_msg, WebPage webPage, boolean searchLinks, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(message, null, null, null, null, null, null, peer, null, reply_to_msg, webPage, searchLinks, null, entities, replyMarkup, params);
    }

    public void sendMessage(MessageMedia location, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, location, null, null, null, null, null, peer, null, reply_to_msg, null, true, null, null, replyMarkup, params);
    }

    public void sendMessage(TL_game game, long peer, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, null, null, null, null, game, peer, null, null, null, true, null, null, replyMarkup, params);
    }

    public void sendMessage(TL_photo photo, String path, long peer, MessageObject reply_to_msg, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        sendMessage(null, null, photo, null, null, null, null, peer, path, reply_to_msg, null, true, null, null, replyMarkup, params);
    }

    private void sendMessage(String message, MessageMedia location, TL_photo photo, VideoEditedInfo videoEditedInfo, User user, TL_document document, TL_game game, long peer, String path, MessageObject reply_to_msg, WebPage webPage, boolean searchLinks, MessageObject retryMessageObject, ArrayList<MessageEntity> entities, ReplyMarkup replyMarkup, HashMap<String, String> params) {
        Throwable e;
        MessageObject newMsgObj;
        if (peer != 0) {
            Chat chat;
            int a;
            DocumentAttribute attribute;
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
            InputPeer sendToPeer = lower_id != 0 ? MessagesController.getInputPeer(lower_id) : null;
            ArrayList<InputUser> sendToPeers = null;
            if (lower_id == 0) {
                encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat == null) {
                    if (retryMessageObject != null) {
                        MessagesStorage.getInstance().markMessageAsSendError(retryMessageObject.messageOwner);
                        retryMessageObject.messageOwner.send_state = 2;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(retryMessageObject.getId()));
                        processSentMessage(retryMessageObject.getId());
                        return;
                    }
                    return;
                }
            } else if (sendToPeer instanceof TL_inputPeerChannel) {
                chat = MessagesController.getInstance().getChat(Integer.valueOf(sendToPeer.channel_id));
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
                                MessagesStorage.getInstance().markMessageAsSendError(newMsg);
                                if (newMsgObj != null) {
                                    newMsgObj.messageOwner.send_state = 2;
                                }
                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
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
                    MessagesStorage.getInstance().markMessageAsSendError(newMsg);
                    if (newMsgObj != null) {
                        newMsgObj.messageOwner.send_state = 2;
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
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
                newMsg.message = "";
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
                newMsg.media.caption = photo.caption != null ? photo.caption : "";
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
                    newMsg2.media.caption = "";
                    newMsg2.media.game = game;
                    newMsg2.message = "";
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
                    MessagesStorage.getInstance().markMessageAsSendError(newMsg);
                    if (newMsgObj != null) {
                        newMsgObj.messageOwner.send_state = 2;
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
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
                    str = "";
                    newMsg.media.first_name = str;
                    user.first_name = str;
                }
                if (newMsg.media.last_name == null) {
                    str = "";
                    newMsg.media.last_name = str;
                    user.last_name = str;
                }
                newMsg.message = "";
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
                newMsg.media.caption = document.caption != null ? document.caption : "";
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
                                            name = StickersQuery.getStickerSetName(attribute.stickerset.id);
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
                                    name = StickersQuery.getStickerSetName(attribute.stickerset.id);
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
                                    name = StickersQuery.getStickerSetName(attribute.stickerset.id);
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
                newMsg.attachPath = "";
            }
            int newMessageId = UserConfig.getNewMessageId();
            newMsg.id = newMessageId;
            newMsg.local_id = newMessageId;
            newMsg.out = true;
            if (!isChannel || sendToPeer == null) {
                newMsg.from_id = UserConfig.getClientUserId();
                newMsg.flags |= 256;
            } else {
                newMsg.from_id = -sendToPeer.channel_id;
            }
            UserConfig.saveConfig(false);
            if (newMsg.random_id == 0) {
                newMsg.random_id = getNextRandomId();
            }
            if (params != null) {
                if (params.containsKey("bot")) {
                    if (encryptedChat != null) {
                        newMsg.via_bot_name = (String) params.get("bot_name");
                        if (newMsg.via_bot_name == null) {
                            newMsg.via_bot_name = "";
                        }
                    } else {
                        newMsg.via_bot_id = Utilities.parseInt((String) params.get("bot")).intValue();
                    }
                    newMsg.flags |= 2048;
                }
            }
            newMsg.params = params;
            if (retryMessageObject == null || !retryMessageObject.resendAsIs) {
                newMsg.date = ConnectionsManager.getInstance().getCurrentTime();
                if (sendToPeer instanceof TL_inputPeerChannel) {
                    if (isChannel) {
                        newMsg.views = 1;
                        newMsg.flags |= 1024;
                    }
                    chat = MessagesController.getInstance().getChat(Integer.valueOf(sendToPeer.channel_id));
                    if (chat != null) {
                        if (chat.megagroup) {
                            newMsg.flags |= Integer.MIN_VALUE;
                            newMsg.unread = true;
                        } else {
                            newMsg.post = true;
                            if (chat.signatures) {
                                newMsg.from_id = UserConfig.getClientUserId();
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
                if (encryptedChat.participant_id == UserConfig.getClientUserId()) {
                    newMsg.to_id.user_id = encryptedChat.admin_id;
                } else {
                    newMsg.to_id.user_id = encryptedChat.participant_id;
                }
                newMsg.ttl = encryptedChat.ttl;
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
                        newMsg.ttl = Math.max(encryptedChat.ttl, duration + 1);
                    } else if (MessageObject.isVideoMessage(newMsg) || MessageObject.isRoundVideoMessage(newMsg)) {
                        duration = 0;
                        for (a = 0; a < newMsg.media.document.attributes.size(); a++) {
                            attribute = (DocumentAttribute) newMsg.media.document.attributes.get(a);
                            if (attribute instanceof TL_documentAttributeVideo) {
                                duration = attribute.duration;
                                break;
                            }
                        }
                        newMsg.ttl = Math.max(encryptedChat.ttl, duration + 1);
                    }
                }
            } else if (high_id != 1) {
                newMsg.to_id = MessagesController.getPeer(lower_id);
                if (lower_id > 0) {
                    User sendToUser = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
                    if (sendToUser == null) {
                        processSentMessage(newMsg.id);
                        return;
                    } else if (sendToUser.bot) {
                        newMsg.unread = false;
                    }
                }
            } else if (this.currentChatInfo == null) {
                MessagesStorage.getInstance().markMessageAsSendError(newMsg);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                processSentMessage(newMsg.id);
                return;
            } else {
                ArrayList<InputUser> sendToPeers2 = new ArrayList();
                try {
                    Iterator it = this.currentChatInfo.participants.participants.iterator();
                    while (it.hasNext()) {
                        InputUser peerUser = MessagesController.getInputUser(MessagesController.getInstance().getUser(Integer.valueOf(((ChatParticipant) it.next()).user_id)));
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
                    MessagesStorage.getInstance().markMessageAsSendError(newMsg);
                    if (newMsgObj != null) {
                        newMsgObj.messageOwner.send_state = 2;
                    }
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                    processSentMessage(newMsg.id);
                }
            }
            if (high_id != 1) {
                if ((MessageObject.isVoiceMessage(newMsg) || MessageObject.isRoundVideoMessage(newMsg)) && newMsg.to_id.channel_id == 0) {
                    newMsg.media_unread = true;
                }
            }
            newMsg.send_state = 1;
            newMsgObj = new MessageObject(newMsg, null, true);
            try {
                newMsgObj.replyMessageObject = reply_to_msg;
                if (!newMsgObj.isForwarded() && ((newMsgObj.type == 3 || videoEditedInfo != null || newMsgObj.type == 2) && !TextUtils.isEmpty(newMsg.attachPath))) {
                    newMsgObj.attachPathExists = true;
                }
                ArrayList<MessageObject> objArr = new ArrayList();
                objArr.add(newMsgObj);
                ArrayList arr = new ArrayList();
                arr.add(newMsg);
                MessagesStorage.getInstance().putMessages(arr, false, true, false, 0);
                MessagesController.getInstance().updateInterfaceWithMessages(peer, objArr);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                if (BuildVars.DEBUG_VERSION && sendToPeer != null) {
                    FileLog.e("send message user_id = " + sendToPeer.user_id + " chat_id = " + sendToPeer.chat_id + " channel_id = " + sendToPeer.channel_id + " access_hash = " + sendToPeer.access_hash);
                }
                ArrayList<Long> random_ids;
                if (type == 0 || !(type != 9 || message == null || encryptedChat == null)) {
                    if (encryptedChat != null) {
                        TL_decryptedMessage reqSend = new TL_decryptedMessage();
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
                        SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                        if (retryMessageObject == null) {
                            DraftQuery.cleanDraft(peer, false);
                        }
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
                    } else {
                        TL_messages_sendMessage reqSend3 = new TL_messages_sendMessage();
                        reqSend3.message = message;
                        reqSend3.clear_draft = retryMessageObject == null;
                        if (newMsg.to_id instanceof TL_peerChannel) {
                            reqSend3.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + peer, false);
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
                            DraftQuery.cleanDraft(peer, false);
                        }
                    }
                } else if ((type < 1 || type > 3) && ((type < 5 || type > 8) && (type != 9 || encryptedChat == null))) {
                    if (type == 4) {
                        TL_messages_forwardMessages reqSend4 = new TL_messages_forwardMessages();
                        reqSend4.to_peer = sendToPeer;
                        reqSend4.with_my_score = retryMessageObject.messageOwner.with_my_score;
                        if (retryMessageObject.messageOwner.ttl != 0) {
                            chat = MessagesController.getInstance().getChat(Integer.valueOf(-retryMessageObject.messageOwner.ttl));
                            reqSend4.from_peer = new TL_inputPeerChannel();
                            reqSend4.from_peer.channel_id = -retryMessageObject.messageOwner.ttl;
                            if (chat != null) {
                                reqSend4.from_peer.access_hash = chat.access_hash;
                            }
                        } else {
                            reqSend4.from_peer = new TL_inputPeerEmpty();
                        }
                        if (retryMessageObject.messageOwner.to_id instanceof TL_peerChannel) {
                            reqSend4.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + peer, false);
                        }
                        reqSend4.random_id.add(Long.valueOf(newMsg.random_id));
                        if (retryMessageObject.getId() >= 0) {
                            reqSend4.id.add(Integer.valueOf(retryMessageObject.getId()));
                        } else {
                            reqSend4.id.add(Integer.valueOf(retryMessageObject.messageOwner.fwd_msg_id));
                        }
                        performSendMessageRequest(reqSend4, newMsgObj, null);
                    } else if (type == 9) {
                        TL_messages_sendInlineBotResult reqSend5 = new TL_messages_sendInlineBotResult();
                        reqSend5.peer = sendToPeer;
                        reqSend5.random_id = newMsg.random_id;
                        if (reply_to_msg != null) {
                            reqSend5.flags |= 1;
                            reqSend5.reply_to_msg_id = reply_to_msg.getId();
                        }
                        if (newMsg.to_id instanceof TL_peerChannel) {
                            reqSend5.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + peer, false);
                        }
                        reqSend5.query_id = Utilities.parseLong((String) params.get("query_id")).longValue();
                        reqSend5.id = (String) params.get(TtmlNode.ATTR_ID);
                        if (retryMessageObject == null) {
                            reqSend5.clear_draft = true;
                            DraftQuery.cleanDraft(peer, false);
                        }
                        performSendMessageRequest(reqSend5, newMsgObj, null);
                    }
                } else if (encryptedChat == null) {
                    TLObject reqSend6;
                    InputMedia inputMedia = null;
                    DelayedMessage delayedMessage = null;
                    if (type == 1) {
                        if (location instanceof TL_messageMediaVenue) {
                            inputMedia = new TL_inputMediaVenue();
                            inputMedia.address = location.address;
                            inputMedia.title = location.title;
                            inputMedia.provider = location.provider;
                            inputMedia.venue_id = location.venue_id;
                        } else {
                            inputMedia = new TL_inputMediaGeoPoint();
                        }
                        inputMedia.geo_point = new TL_inputGeoPoint();
                        inputMedia.geo_point.lat = location.geo.lat;
                        inputMedia.geo_point._long = location.geo._long;
                    } else if (type == 2 || (type == 9 && photo != null)) {
                        if (photo.access_hash == 0) {
                            inputMedia = new TL_inputMediaUploadedPhoto();
                            inputMedia.caption = photo.caption != null ? photo.caption : "";
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
                            r0 = new DelayedMessage();
                            r0.originalPath = originalPath;
                            r0.type = 0;
                            r0.obj = newMsgObj;
                            if (path != null && path.length() > 0) {
                                if (path.startsWith("http")) {
                                    r0.httpLocation = path;
                                }
                            }
                            r0.location = ((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location;
                        } else {
                            media = new TL_inputMediaPhoto();
                            media.id = new TL_inputPhoto();
                            media.caption = photo.caption != null ? photo.caption : "";
                            media.id.id = photo.id;
                            media.id.access_hash = photo.access_hash;
                            inputMedia = media;
                        }
                    } else if (type == 3) {
                        if (document.access_hash == 0) {
                            if (document.thumb.location != null) {
                                inputMedia = new TL_inputMediaUploadedThumbDocument();
                            } else {
                                inputMedia = new TL_inputMediaUploadedDocument();
                            }
                            inputMedia.caption = document.caption != null ? document.caption : "";
                            inputMedia.mime_type = document.mime_type;
                            inputMedia.attributes = document.attributes;
                            r0 = new DelayedMessage();
                            r0.originalPath = originalPath;
                            r0.type = 1;
                            r0.obj = newMsgObj;
                            r0.location = document.thumb.location;
                            r0.documentLocation = document;
                            r0.videoEditedInfo = videoEditedInfo;
                        } else {
                            media = new TL_inputMediaDocument();
                            media.id = new TL_inputDocument();
                            media.caption = document.caption != null ? document.caption : "";
                            media.id.id = document.id;
                            media.id.access_hash = document.access_hash;
                            inputMedia = media;
                        }
                    } else if (type == 6) {
                        inputMedia = new TL_inputMediaContact();
                        inputMedia.phone_number = user.phone;
                        inputMedia.first_name = user.first_name;
                        inputMedia.last_name = user.last_name;
                    } else if (type == 7 || type == 9) {
                        if (document.access_hash == 0) {
                            String str2;
                            if (encryptedChat == null && originalPath != null && originalPath.length() > 0) {
                                if (originalPath.startsWith("http") && params != null) {
                                    inputMedia = new TL_inputMediaGifExternal();
                                    String[] args = ((String) params.get("url")).split("\\|");
                                    if (args.length == 2) {
                                        ((TL_inputMediaGifExternal) inputMedia).url = args[0];
                                        inputMedia.q = args[1];
                                    }
                                    inputMedia.mime_type = document.mime_type;
                                    inputMedia.attributes = document.attributes;
                                    if (document.caption == null) {
                                        str2 = document.caption;
                                    } else {
                                        str2 = "";
                                    }
                                    inputMedia.caption = str2;
                                }
                            }
                            if (document.thumb.location == null || !(document.thumb.location instanceof TL_fileLocation)) {
                                inputMedia = new TL_inputMediaUploadedDocument();
                            } else {
                                inputMedia = new TL_inputMediaUploadedThumbDocument();
                            }
                            r0 = new DelayedMessage();
                            r0.originalPath = originalPath;
                            r0.type = 2;
                            r0.obj = newMsgObj;
                            r0.documentLocation = document;
                            r0.location = document.thumb.location;
                            inputMedia.mime_type = document.mime_type;
                            inputMedia.attributes = document.attributes;
                            if (document.caption == null) {
                                str2 = "";
                            } else {
                                str2 = document.caption;
                            }
                            inputMedia.caption = str2;
                        } else {
                            media = new TL_inputMediaDocument();
                            media.id = new TL_inputDocument();
                            media.id.id = document.id;
                            media.id.access_hash = document.access_hash;
                            media.caption = document.caption != null ? document.caption : "";
                            inputMedia = media;
                        }
                    } else if (type == 8) {
                        if (document.access_hash == 0) {
                            inputMedia = new TL_inputMediaUploadedDocument();
                            inputMedia.mime_type = document.mime_type;
                            inputMedia.attributes = document.attributes;
                            inputMedia.caption = document.caption != null ? document.caption : "";
                            r0 = new DelayedMessage();
                            r0.type = 3;
                            r0.obj = newMsgObj;
                            r0.documentLocation = document;
                        } else {
                            media = new TL_inputMediaDocument();
                            media.id = new TL_inputDocument();
                            media.caption = document.caption != null ? document.caption : "";
                            media.id.id = document.id;
                            media.id.access_hash = document.access_hash;
                            inputMedia = media;
                        }
                    }
                    TLObject request;
                    if (sendToPeers != null) {
                        request = new TL_messages_sendBroadcast();
                        random_ids = new ArrayList();
                        for (a = 0; a < sendToPeers.size(); a++) {
                            random_ids.add(Long.valueOf(Utilities.random.nextLong()));
                        }
                        request.contacts = sendToPeers;
                        request.media = inputMedia;
                        request.random_id = random_ids;
                        request.message = "";
                        if (delayedMessage != null) {
                            delayedMessage.sendRequest = request;
                        }
                        reqSend6 = request;
                        if (retryMessageObject == null) {
                            DraftQuery.cleanDraft(peer, false);
                        }
                    } else {
                        request = new TL_messages_sendMedia();
                        request.peer = sendToPeer;
                        if (newMsg.to_id instanceof TL_peerChannel) {
                            request.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + peer, false);
                        }
                        request.random_id = newMsg.random_id;
                        request.media = inputMedia;
                        if (reply_to_msg != null) {
                            request.flags |= 1;
                            request.reply_to_msg_id = reply_to_msg.getId();
                        }
                        if (delayedMessage != null) {
                            delayedMessage.sendRequest = request;
                        }
                        reqSend6 = request;
                    }
                    if (type == 1) {
                        performSendMessageRequest(reqSend6, newMsgObj, null);
                    } else if (type == 2) {
                        if (photo.access_hash == 0) {
                            performSendDelayedMessage(delayedMessage);
                        } else {
                            performSendMessageRequest(reqSend6, newMsgObj, null);
                        }
                    } else if (type == 3) {
                        if (document.access_hash == 0) {
                            performSendDelayedMessage(delayedMessage);
                        } else {
                            performSendMessageRequest(reqSend6, newMsgObj, null);
                        }
                    } else if (type == 6) {
                        performSendMessageRequest(reqSend6, newMsgObj, null);
                    } else if (type == 7) {
                        if (document.access_hash != 0 || delayedMessage == null) {
                            performSendMessageRequest(reqSend6, newMsgObj, originalPath);
                        } else {
                            performSendDelayedMessage(delayedMessage);
                        }
                    } else if (type != 8) {
                    } else {
                        if (document.access_hash == 0) {
                            performSendDelayedMessage(delayedMessage);
                        } else {
                            performSendMessageRequest(reqSend6, newMsgObj, null);
                        }
                    }
                } else {
                    DecryptedMessage reqSend7 = new TL_decryptedMessage();
                    reqSend7.ttl = newMsg.ttl;
                    if (!(entities == null || entities.isEmpty())) {
                        reqSend7.entities = entities;
                        reqSend7.flags |= 128;
                    }
                    if (!(reply_to_msg == null || reply_to_msg.messageOwner.random_id == 0)) {
                        reqSend7.reply_to_random_id = reply_to_msg.messageOwner.random_id;
                        reqSend7.flags |= 8;
                    }
                    reqSend7.flags |= 512;
                    if (params != null) {
                        if (params.get("bot_name") != null) {
                            reqSend7.via_bot_name = (String) params.get("bot_name");
                            reqSend7.flags |= 2048;
                        }
                    }
                    reqSend7.random_id = newMsg.random_id;
                    reqSend7.message = "";
                    if (type == 1) {
                        if (location instanceof TL_messageMediaVenue) {
                            reqSend7.media = new TL_decryptedMessageMediaVenue();
                            reqSend7.media.address = location.address;
                            reqSend7.media.title = location.title;
                            reqSend7.media.provider = location.provider;
                            reqSend7.media.venue_id = location.venue_id;
                        } else {
                            reqSend7.media = new TL_decryptedMessageMediaGeoPoint();
                        }
                        reqSend7.media.lat = location.geo.lat;
                        reqSend7.media._long = location.geo._long;
                        SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend7, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                    } else if (type == 2 || (type == 9 && photo != null)) {
                        PhotoSize small = (PhotoSize) photo.sizes.get(0);
                        PhotoSize big = (PhotoSize) photo.sizes.get(photo.sizes.size() - 1);
                        ImageLoader.fillPhotoSizeWithBytes(small);
                        reqSend7.media = new TL_decryptedMessageMediaPhoto();
                        reqSend7.media.caption = photo.caption != null ? photo.caption : "";
                        if (small.bytes != null) {
                            ((TL_decryptedMessageMediaPhoto) reqSend7.media).thumb = small.bytes;
                        } else {
                            ((TL_decryptedMessageMediaPhoto) reqSend7.media).thumb = new byte[0];
                        }
                        reqSend7.media.thumb_h = small.h;
                        reqSend7.media.thumb_w = small.w;
                        reqSend7.media.w = big.w;
                        reqSend7.media.h = big.h;
                        reqSend7.media.size = big.size;
                        if (big.location.key == null) {
                            r0 = new DelayedMessage();
                            r0.originalPath = originalPath;
                            r0.sendEncryptedRequest = reqSend7;
                            r0.type = 0;
                            r0.obj = newMsgObj;
                            r0.encryptedChat = encryptedChat;
                            if (path != null && path.length() > 0) {
                                if (path.startsWith("http")) {
                                    r0.httpLocation = path;
                                    performSendDelayedMessage(r0);
                                }
                            }
                            r0.location = ((PhotoSize) photo.sizes.get(photo.sizes.size() - 1)).location;
                            performSendDelayedMessage(r0);
                        } else {
                            encryptedFile = new TL_inputEncryptedFile();
                            encryptedFile.id = big.location.volume_id;
                            encryptedFile.access_hash = big.location.secret;
                            reqSend7.media.key = big.location.key;
                            reqSend7.media.iv = big.location.iv;
                            SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend7, newMsgObj.messageOwner, encryptedChat, encryptedFile, null, newMsgObj);
                        }
                    } else if (type == 3) {
                        ImageLoader.fillPhotoSizeWithBytes(document.thumb);
                        if (MessageObject.isNewGifDocument(document) || MessageObject.isRoundVideoDocument(document)) {
                            reqSend7.media = new TL_decryptedMessageMediaDocument();
                            reqSend7.media.attributes = document.attributes;
                            if (document.thumb == null || document.thumb.bytes == null) {
                                ((TL_decryptedMessageMediaDocument) reqSend7.media).thumb = new byte[0];
                            } else {
                                ((TL_decryptedMessageMediaDocument) reqSend7.media).thumb = document.thumb.bytes;
                            }
                        } else {
                            reqSend7.media = new TL_decryptedMessageMediaVideo();
                            if (document.thumb == null || document.thumb.bytes == null) {
                                ((TL_decryptedMessageMediaVideo) reqSend7.media).thumb = new byte[0];
                            } else {
                                ((TL_decryptedMessageMediaVideo) reqSend7.media).thumb = document.thumb.bytes;
                            }
                        }
                        reqSend7.media.caption = document.caption != null ? document.caption : "";
                        reqSend7.media.mime_type = MimeTypes.VIDEO_MP4;
                        reqSend7.media.size = document.size;
                        for (a = 0; a < document.attributes.size(); a++) {
                            attribute = (DocumentAttribute) document.attributes.get(a);
                            if (attribute instanceof TL_documentAttributeVideo) {
                                reqSend7.media.w = attribute.w;
                                reqSend7.media.h = attribute.h;
                                reqSend7.media.duration = attribute.duration;
                                break;
                            }
                        }
                        reqSend7.media.thumb_h = document.thumb.h;
                        reqSend7.media.thumb_w = document.thumb.w;
                        if (document.key == null) {
                            r0 = new DelayedMessage();
                            r0.originalPath = originalPath;
                            r0.sendEncryptedRequest = reqSend7;
                            r0.type = 1;
                            r0.obj = newMsgObj;
                            r0.encryptedChat = encryptedChat;
                            r0.documentLocation = document;
                            r0.videoEditedInfo = videoEditedInfo;
                            performSendDelayedMessage(r0);
                        } else {
                            encryptedFile = new TL_inputEncryptedFile();
                            encryptedFile.id = document.id;
                            encryptedFile.access_hash = document.access_hash;
                            reqSend7.media.key = document.key;
                            reqSend7.media.iv = document.iv;
                            SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend7, newMsgObj.messageOwner, encryptedChat, encryptedFile, null, newMsgObj);
                        }
                    } else if (type == 6) {
                        reqSend7.media = new TL_decryptedMessageMediaContact();
                        reqSend7.media.phone_number = user.phone;
                        reqSend7.media.first_name = user.first_name;
                        reqSend7.media.last_name = user.last_name;
                        reqSend7.media.user_id = user.id;
                        SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend7, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                    } else if (type == 7 || (type == 9 && document != null)) {
                        if (MessageObject.isStickerDocument(document)) {
                            reqSend7.media = new TL_decryptedMessageMediaExternalDocument();
                            reqSend7.media.id = document.id;
                            reqSend7.media.date = document.date;
                            reqSend7.media.access_hash = document.access_hash;
                            reqSend7.media.mime_type = document.mime_type;
                            reqSend7.media.size = document.size;
                            reqSend7.media.dc_id = document.dc_id;
                            reqSend7.media.attributes = document.attributes;
                            if (document.thumb == null) {
                                ((TL_decryptedMessageMediaExternalDocument) reqSend7.media).thumb = new TL_photoSizeEmpty();
                                ((TL_decryptedMessageMediaExternalDocument) reqSend7.media).thumb.type = "s";
                            } else {
                                ((TL_decryptedMessageMediaExternalDocument) reqSend7.media).thumb = document.thumb;
                            }
                            SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend7, newMsgObj.messageOwner, encryptedChat, null, null, newMsgObj);
                        } else {
                            ImageLoader.fillPhotoSizeWithBytes(document.thumb);
                            reqSend7.media = new TL_decryptedMessageMediaDocument();
                            reqSend7.media.attributes = document.attributes;
                            reqSend7.media.caption = document.caption != null ? document.caption : "";
                            if (document.thumb == null || document.thumb.bytes == null) {
                                ((TL_decryptedMessageMediaDocument) reqSend7.media).thumb = new byte[0];
                                reqSend7.media.thumb_h = 0;
                                reqSend7.media.thumb_w = 0;
                            } else {
                                ((TL_decryptedMessageMediaDocument) reqSend7.media).thumb = document.thumb.bytes;
                                reqSend7.media.thumb_h = document.thumb.h;
                                reqSend7.media.thumb_w = document.thumb.w;
                            }
                            reqSend7.media.size = document.size;
                            reqSend7.media.mime_type = document.mime_type;
                            if (document.key == null) {
                                r0 = new DelayedMessage();
                                r0.originalPath = originalPath;
                                r0.sendEncryptedRequest = reqSend7;
                                r0.type = 2;
                                r0.obj = newMsgObj;
                                r0.encryptedChat = encryptedChat;
                                if (path != null && path.length() > 0) {
                                    if (path.startsWith("http")) {
                                        r0.httpLocation = path;
                                    }
                                }
                                r0.documentLocation = document;
                                performSendDelayedMessage(r0);
                            } else {
                                encryptedFile = new TL_inputEncryptedFile();
                                encryptedFile.id = document.id;
                                encryptedFile.access_hash = document.access_hash;
                                reqSend7.media.key = document.key;
                                reqSend7.media.iv = document.iv;
                                SecretChatHelper.getInstance().performSendEncryptedRequest(reqSend7, newMsgObj.messageOwner, encryptedChat, encryptedFile, null, newMsgObj);
                            }
                        }
                    } else if (type == 8) {
                        r0 = new DelayedMessage();
                        r0.encryptedChat = encryptedChat;
                        r0.sendEncryptedRequest = reqSend7;
                        r0.obj = newMsgObj;
                        r0.documentLocation = document;
                        r0.type = 3;
                        reqSend7.media = new TL_decryptedMessageMediaDocument();
                        reqSend7.media.attributes = document.attributes;
                        reqSend7.media.caption = document.caption != null ? document.caption : "";
                        if (document.thumb == null || document.thumb.bytes == null) {
                            ((TL_decryptedMessageMediaDocument) reqSend7.media).thumb = new byte[0];
                            reqSend7.media.thumb_h = 0;
                            reqSend7.media.thumb_w = 0;
                        } else {
                            ((TL_decryptedMessageMediaDocument) reqSend7.media).thumb = document.thumb.bytes;
                            reqSend7.media.thumb_h = document.thumb.h;
                            reqSend7.media.thumb_w = document.thumb.w;
                        }
                        reqSend7.media.mime_type = document.mime_type;
                        reqSend7.media.size = document.size;
                        r0.originalPath = originalPath;
                        performSendDelayedMessage(r0);
                    }
                    if (retryMessageObject == null) {
                        DraftQuery.cleanDraft(peer, false);
                    }
                }
            } catch (Exception e6) {
                e = e6;
                FileLog.e(e);
                MessagesStorage.getInstance().markMessageAsSendError(newMsg);
                if (newMsgObj != null) {
                    newMsgObj.messageOwner.send_state = 2;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsg.id));
                processSentMessage(newMsg.id);
            }
        }
    }

    private void performSendDelayedMessage(DelayedMessage message) {
        boolean z = true;
        boolean z2 = false;
        String location;
        if (message.type == 0) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "file");
            } else if (message.sendRequest != null) {
                location = FileLoader.getPathToAttach(message.location).toString();
                putToDelayedMessages(location, message);
                FileLoader.getInstance().uploadFile(location, false, true, 16777216);
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
                        FileLoader.getInstance().loadFile(message.location, "jpg", 0, false);
                        return;
                    }
                }
                putToDelayedMessages(location, message);
                FileLoader.getInstance().uploadFile(location, true, true, 16777216);
            }
        } else if (message.type == 1) {
            if (message.videoEditedInfo != null) {
                location = message.obj.messageOwner.attachPath;
                if (location == null) {
                    location = FileLoader.getInstance().getDirectory(4) + "/" + message.documentLocation.id + ".mp4";
                }
                putToDelayedMessages(location, message);
                MediaController.getInstance().scheduleVideoConvert(message.obj);
            } else if (message.sendRequest != null) {
                if (message.sendRequest instanceof TL_messages_sendMedia) {
                    media = ((TL_messages_sendMedia) message.sendRequest).media;
                } else {
                    media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                }
                if (media.file == null) {
                    location = message.obj.messageOwner.attachPath;
                    if (location == null) {
                        location = FileLoader.getInstance().getDirectory(4) + "/" + message.documentLocation.id + ".mp4";
                    }
                    putToDelayedMessages(location, message);
                    if (message.obj.videoEditedInfo != null) {
                        FileLoader.getInstance().uploadFile(location, false, false, message.documentLocation.size, ConnectionsManager.FileTypeVideo);
                        return;
                    }
                    FileLoader.getInstance().uploadFile(location, false, false, ConnectionsManager.FileTypeVideo);
                    return;
                }
                location = FileLoader.getInstance().getDirectory(4) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                putToDelayedMessages(location, message);
                FileLoader.getInstance().uploadFile(location, false, true, 16777216);
            } else {
                location = message.obj.messageOwner.attachPath;
                if (location == null) {
                    location = FileLoader.getInstance().getDirectory(4) + "/" + message.documentLocation.id + ".mp4";
                }
                if (message.sendEncryptedRequest == null || message.documentLocation.dc_id == 0 || new File(location).exists()) {
                    putToDelayedMessages(location, message);
                    if (message.obj.videoEditedInfo != null) {
                        FileLoader.getInstance().uploadFile(location, true, false, message.documentLocation.size, ConnectionsManager.FileTypeVideo);
                        return;
                    }
                    FileLoader.getInstance().uploadFile(location, true, false, ConnectionsManager.FileTypeVideo);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(message.documentLocation), message);
                FileLoader.getInstance().loadFile(message.documentLocation, true, false);
            }
        } else if (message.type == 2) {
            if (message.httpLocation != null) {
                putToDelayedMessages(message.httpLocation, message);
                ImageLoader.getInstance().loadHttpFile(message.httpLocation, "gif");
            } else if (message.sendRequest != null) {
                if (message.sendRequest instanceof TL_messages_sendMedia) {
                    media = ((TL_messages_sendMedia) message.sendRequest).media;
                } else {
                    media = ((TL_messages_sendBroadcast) message.sendRequest).media;
                }
                if (media.file == null) {
                    location = message.obj.messageOwner.attachPath;
                    putToDelayedMessages(location, message);
                    r0 = FileLoader.getInstance();
                    if (message.sendRequest != null) {
                        z = false;
                    }
                    r0.uploadFile(location, z, false, ConnectionsManager.FileTypeFile);
                } else if (media.thumb == null && message.location != null) {
                    location = FileLoader.getInstance().getDirectory(4) + "/" + message.location.volume_id + "_" + message.location.local_id + ".jpg";
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance().uploadFile(location, false, true, 16777216);
                }
            } else {
                location = message.obj.messageOwner.attachPath;
                if (message.sendEncryptedRequest == null || message.documentLocation.dc_id == 0 || new File(location).exists()) {
                    putToDelayedMessages(location, message);
                    FileLoader.getInstance().uploadFile(location, true, false, ConnectionsManager.FileTypeFile);
                    return;
                }
                putToDelayedMessages(FileLoader.getAttachFileName(message.documentLocation), message);
                FileLoader.getInstance().loadFile(message.documentLocation, true, false);
            }
        } else if (message.type == 3) {
            location = message.obj.messageOwner.attachPath;
            putToDelayedMessages(location, message);
            r0 = FileLoader.getInstance();
            if (message.sendRequest == null) {
                z2 = true;
            }
            r0.uploadFile(location, z2, true, ConnectionsManager.FileTypeAudio);
        }
    }

    protected void stopVideoService(final String path) {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.stopEncodingService, path);
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

    private void performSendMessageRequest(TLObject req, MessageObject msgObj, String originalPath) {
        int i;
        final Message newMsgObj = msgObj.messageOwner;
        putToSendingMessages(newMsgObj);
        ConnectionsManager instance = ConnectionsManager.getInstance();
        final TLObject tLObject = req;
        final MessageObject messageObject = msgObj;
        final String str = originalPath;
        RequestDelegate anonymousClass9 = new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        boolean isSentError = false;
                        if (error == null) {
                            int i;
                            final int oldId = newMsgObj.id;
                            final boolean isBroadcast = tLObject instanceof TL_messages_sendBroadcast;
                            final ArrayList<Message> sentMessages = new ArrayList();
                            final String attachPath = newMsgObj.attachPath;
                            Message message;
                            if (response instanceof TL_updateShortSentMessage) {
                                final TL_updateShortSentMessage res = response;
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
                                Utilities.stageQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance().processNewDifferenceParams(-1, res.pts, res.date, res.pts_count);
                                    }
                                });
                                sentMessages.add(newMsgObj);
                            } else if (response instanceof Updates) {
                                final Updates updates = response;
                                ArrayList<Update> updatesArr = ((Updates) response).updates;
                                Message message3 = null;
                                int a = 0;
                                while (a < updatesArr.size()) {
                                    Update update = (Update) updatesArr.get(a);
                                    if (update instanceof TL_updateNewMessage) {
                                        final TL_updateNewMessage newMessage = (TL_updateNewMessage) update;
                                        message3 = newMessage.message;
                                        sentMessages.add(message3);
                                        newMsgObj.id = newMessage.message.id;
                                        Utilities.stageQueue.postRunnable(new Runnable() {
                                            public void run() {
                                                MessagesController.getInstance().processNewDifferenceParams(-1, newMessage.pts, -1, newMessage.pts_count);
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
                                                MessagesController.getInstance().processNewChannelDifferenceParams(newMessage2.pts, newMessage2.pts_count, newMessage2.message.to_id.channel_id);
                                            }
                                        });
                                        updatesArr.remove(a);
                                    } else {
                                        a++;
                                    }
                                }
                                if (message3 != null) {
                                    Integer value = (Integer) MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(message3.dialog_id));
                                    if (value == null) {
                                        value = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(message3.out, message3.dialog_id));
                                        MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(message3.dialog_id), value);
                                    }
                                    message3.unread = value.intValue() < message3.id;
                                    newMsgObj.id = message3.id;
                                    SendMessagesHelper.this.updateMediaPaths(messageObject, message3, str, false);
                                } else {
                                    isSentError = true;
                                }
                                Utilities.stageQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesController.getInstance().processUpdates(updates, false);
                                    }
                                });
                            }
                            if (!isSentError) {
                                int i2;
                                StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, 1);
                                newMsgObj.send_state = 0;
                                NotificationCenter instance = NotificationCenter.getInstance();
                                i = NotificationCenter.messageReceivedByServer;
                                Integer[] numArr = new Object[4];
                                numArr[0] = Integer.valueOf(oldId);
                                if (isBroadcast) {
                                    i2 = oldId;
                                } else {
                                    i2 = newMsgObj.id;
                                }
                                numArr[1] = Integer.valueOf(i2);
                                numArr[2] = newMsgObj;
                                numArr[3] = Long.valueOf(newMsgObj.dialog_id);
                                instance.postNotificationName(i, numArr);
                                MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
                                    public void run() {
                                        MessagesStorage.getInstance().updateMessageStateAndId(newMsgObj.random_id, Integer.valueOf(oldId), isBroadcast ? oldId : newMsgObj.id, 0, false, newMsgObj.to_id.channel_id);
                                        MessagesStorage.getInstance().putMessages(sentMessages, true, false, isBroadcast, 0);
                                        if (isBroadcast) {
                                            ArrayList currentMessage = new ArrayList();
                                            currentMessage.add(newMsgObj);
                                            MessagesStorage.getInstance().putMessages(currentMessage, true, false, false, 0);
                                        }
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                if (isBroadcast) {
                                                    for (int a = 0; a < sentMessages.size(); a++) {
                                                        Message message = (Message) sentMessages.get(a);
                                                        ArrayList<MessageObject> arr = new ArrayList();
                                                        MessageObject messageObject = new MessageObject(message, null, false);
                                                        arr.add(messageObject);
                                                        MessagesController.getInstance().updateInterfaceWithMessages(messageObject.getDialogId(), arr, true);
                                                    }
                                                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                                }
                                                SearchQuery.increasePeerRaiting(newMsgObj.dialog_id);
                                                NotificationCenter instance = NotificationCenter.getInstance();
                                                int i = NotificationCenter.messageReceivedByServer;
                                                Object[] objArr = new Object[4];
                                                objArr[0] = Integer.valueOf(oldId);
                                                objArr[1] = Integer.valueOf(isBroadcast ? oldId : newMsgObj.id);
                                                objArr[2] = newMsgObj;
                                                objArr[3] = Long.valueOf(newMsgObj.dialog_id);
                                                instance.postNotificationName(i, objArr);
                                                SendMessagesHelper.this.processSentMessage(oldId);
                                                SendMessagesHelper.this.removeFromSendingMessages(oldId);
                                            }
                                        });
                                        if (MessageObject.isVideoMessage(newMsgObj) || MessageObject.isRoundVideoMessage(newMsgObj) || MessageObject.isNewGifMessage(newMsgObj)) {
                                            SendMessagesHelper.this.stopVideoService(attachPath);
                                        }
                                    }
                                });
                            }
                        } else {
                            AlertsCreator.processError(error, null, tLObject, new Object[0]);
                            isSentError = true;
                        }
                        if (isSentError) {
                            MessagesStorage.getInstance().markMessageAsSendError(newMsgObj);
                            newMsgObj.send_state = 2;
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(newMsgObj.id));
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
        QuickAckDelegate anonymousClass10 = new QuickAckDelegate() {
            public void run() {
                final int msg_id = newMsgObj.id;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        newMsgObj.send_state = 0;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(msg_id));
                    }
                });
            }
        };
        if (req instanceof TL_messages_sendMessage) {
            i = 128;
        } else {
            i = 0;
        }
        instance.sendRequest(req, anonymousClass9, anonymousClass10, i | 68);
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
                MessagesStorage.getInstance().putSentFile(originalPath, sentMessage.media.photo, 0);
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
                                        cacheFile = new File(FileLoader.getInstance().getDirectory(4), fileName + ".jpg");
                                        if (sentMessage.media.photo.sizes.size() == 1 || size.w > 90 || size.h > 90) {
                                            cacheFile2 = FileLoader.getPathToAttach(size);
                                        } else {
                                            cacheFile2 = new File(FileLoader.getInstance().getDirectory(4), fileName2 + ".jpg");
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
                    MessagesStorage.getInstance().putSentFile(originalPath, sentMessage.media.document, 2);
                    sentMessage.attachPath = newMsg.attachPath;
                } else if (!(MessageObject.isVoiceMessage(sentMessage) || MessageObject.isRoundVideoMessage(sentMessage))) {
                    MessagesStorage.getInstance().putSentFile(originalPath, sentMessage.media.document, 1);
                }
                size2 = newMsg.media.document.thumb;
                size = sentMessage.media.document.thumb;
                if (size2 != null && size2.location != null && size2.location.volume_id == -2147483648L && size != null && size.location != null && !(size instanceof TL_photoSizeEmpty) && !(size2 instanceof TL_photoSizeEmpty)) {
                    fileName = size2.location.volume_id + "_" + size2.location.local_id;
                    fileName2 = size.location.volume_id + "_" + size.location.local_id;
                    if (!fileName.equals(fileName2)) {
                        new File(FileLoader.getInstance().getDirectory(4), fileName + ".jpg").renameTo(new File(FileLoader.getInstance().getDirectory(4), fileName2 + ".jpg"));
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
                        StickersQuery.addRecentGif(sentMessage.media.document, sentMessage.date);
                    } else if (MessageObject.isStickerDocument(sentMessage.media.document)) {
                        StickersQuery.addRecentSticker(0, sentMessage.media.document, sentMessage.date);
                    }
                }
                if (newMsg.attachPath == null || !newMsg.attachPath.startsWith(FileLoader.getInstance().getDirectory(4).getAbsolutePath())) {
                    sentMessage.attachPath = newMsg.attachPath;
                    sentMessage.message = newMsg.message;
                    return;
                }
                cacheFile = new File(newMsg.attachPath);
                cacheFile2 = FileLoader.getPathToAttach(sentMessage.media.document);
                if (!cacheFile.renameTo(cacheFile2)) {
                    sentMessage.attachPath = newMsg.attachPath;
                    sentMessage.message = newMsg.message;
                } else if (MessageObject.isVideoMessage(sentMessage)) {
                    newMsgObj.attachPathExists = true;
                } else {
                    newMsgObj.mediaExists = newMsgObj.attachPathExists;
                    newMsgObj.attachPathExists = false;
                    newMsg.attachPath = "";
                    if (originalPath != null) {
                        if (originalPath.startsWith("http")) {
                            MessagesStorage.getInstance().addRecentLocalFile(originalPath, cacheFile2.toString(), newMsg.media.document);
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
        MessagesStorage.getInstance().getUnsentMessages(1000);
    }

    protected void processUnsentMessages(ArrayList<Message> messages, ArrayList<User> users, ArrayList<Chat> chats, ArrayList<EncryptedChat> encryptedChats) {
        final ArrayList<User> arrayList = users;
        final ArrayList<Chat> arrayList2 = chats;
        final ArrayList<EncryptedChat> arrayList3 = encryptedChats;
        final ArrayList<Message> arrayList4 = messages;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.getInstance().putUsers(arrayList, true);
                MessagesController.getInstance().putChats(arrayList2, true);
                MessagesController.getInstance().putEncryptedChats(arrayList3, true);
                for (int a = 0; a < arrayList4.size(); a++) {
                    SendMessagesHelper.this.retrySendMessage(new MessageObject((Message) arrayList4.get(a), null, false), true);
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
        UserConfig.saveConfig(false);
        TL_photo photo = new TL_photo();
        photo.date = ConnectionsManager.getInstance().getCurrentTime();
        photo.sizes = sizes;
        return photo;
    }

    private static boolean prepareSendingDocumentInternal(String path, String originalPath, Uri uri, String mime, long dialog_id, MessageObject reply_to_msg, String caption) {
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
            if (mime != null) {
                extension = myMime.getExtensionFromMimeType(mime);
            }
            if (extension == null) {
                extension = "txt";
            }
            path = MediaController.copyFileToCache(uri, extension);
            if (path == null) {
                return false;
            }
        }
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            return false;
        }
        boolean isEncrypted = ((int) dialog_id) == 0;
        boolean allowSticker = !isEncrypted;
        String name = file.getName();
        String ext = "";
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
                    attributeAudio.title = "";
                }
                attributeAudio.flags |= 1;
                if (attributeAudio.performer == null) {
                    attributeAudio.performer = "";
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
                originalPath = originalPath + "" + file.length();
            }
        }
        TL_document tL_document = null;
        if (!(sendNew || isEncrypted)) {
            tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(originalPath, !isEncrypted ? 1 : 4);
            if (!(tL_document != null || path.equals(originalPath) || isEncrypted)) {
                tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(path + file.length(), !isEncrypted ? 1 : 4);
            }
        }
        if (tL_document == null) {
            tL_document = new TL_document();
            tL_document.id = 0;
            tL_document.date = ConnectionsManager.getInstance().getCurrentTime();
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
                    attributeSticker.alt = "";
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
        tL_document.caption = caption;
        final HashMap<String, String> params = new HashMap();
        if (originalPath != null) {
            params.put("originalPath", originalPath);
        }
        final TL_document documentFinal = tL_document;
        final String pathFinal = path;
        final long j = dialog_id;
        final MessageObject messageObject = reply_to_msg;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SendMessagesHelper.getInstance().sendMessage(documentFinal, null, pathFinal, j, messageObject, null, params);
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

    public static void prepareSendingAudioDocuments(final ArrayList<MessageObject> messageObjects, final long dialog_id, final MessageObject reply_to_msg) {
        new Thread(new Runnable() {
            public void run() {
                int size = messageObjects.size();
                for (int a = 0; a < size; a++) {
                    final MessageObject messageObject = (MessageObject) messageObjects.get(a);
                    String originalPath = messageObject.messageOwner.attachPath;
                    File f = new File(originalPath);
                    boolean isEncrypted = ((int) dialog_id) == 0;
                    if (originalPath != null) {
                        originalPath = originalPath + MimeTypes.BASE_TYPE_AUDIO + f.length();
                    }
                    TL_document tL_document = null;
                    if (!isEncrypted) {
                        tL_document = (TL_document) MessagesStorage.getInstance().getSentFile(originalPath, !isEncrypted ? 1 : 4);
                    }
                    if (tL_document == null) {
                        tL_document = messageObject.messageOwner.media.document;
                    }
                    if (isEncrypted) {
                        if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (dialog_id >> 32))) == null) {
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
                            SendMessagesHelper.getInstance().sendMessage(documentFinal, null, messageObject.messageOwner.attachPath, dialog_id, reply_to_msg, null, params);
                        }
                    });
                }
            }
        }).start();
    }

    public static void prepareSendingDocuments(ArrayList<String> paths, ArrayList<String> originalPaths, ArrayList<Uri> uris, String mime, long dialog_id, MessageObject reply_to_msg, InputContentInfoCompat inputContent) {
        if (paths != null || originalPaths != null || uris != null) {
            if (paths == null || originalPaths == null || paths.size() == originalPaths.size()) {
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
                                if (!SendMessagesHelper.prepareSendingDocumentInternal((String) arrayList.get(a), (String) arrayList2.get(a), null, str, j, messageObject, null)) {
                                    error = true;
                                }
                            }
                        }
                        if (arrayList3 != null) {
                            for (a = 0; a < arrayList3.size(); a++) {
                                if (!SendMessagesHelper.prepareSendingDocumentInternal(null, null, (Uri) arrayList3.get(a), str, j, messageObject, null)) {
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

    public static void prepareSendingPhoto(String imageFilePath, Uri imageUri, long dialog_id, MessageObject reply_to_msg, CharSequence caption, ArrayList<InputDocument> stickers, InputContentInfoCompat inputContent) {
        ArrayList<String> paths = null;
        ArrayList<Uri> uris = null;
        ArrayList<String> captions = null;
        ArrayList<ArrayList<InputDocument>> masks = null;
        if (!(imageFilePath == null || imageFilePath.length() == 0)) {
            paths = new ArrayList();
            paths.add(imageFilePath);
        }
        if (imageUri != null) {
            uris = new ArrayList();
            uris.add(imageUri);
        }
        if (caption != null) {
            captions = new ArrayList();
            captions.add(caption.toString());
        }
        if (!(stickers == null || stickers.isEmpty())) {
            masks = new ArrayList();
            masks.add(new ArrayList(stickers));
        }
        prepareSendingPhotos(paths, uris, dialog_id, reply_to_msg, captions, masks, inputContent);
    }

    public static void prepareSendingBotContextResult(BotInlineResult result, HashMap<String, String> params, long dialog_id, MessageObject reply_to_msg) {
        if (result != null) {
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
                                File file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(botInlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content_url, "file"));
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
                                        document.date = ConnectionsManager.getInstance().getCurrentTime();
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
                                                    bitmap = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(botInlineResult.thumb_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.thumb_url, "jpg")).getAbsolutePath(), null, 90.0f, 90.0f, true);
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
                                                attributeSticker.alt = "";
                                                attributeSticker.stickerset = new TL_inputStickerSetEmpty();
                                                document.attributes.add(attributeSticker);
                                                TL_documentAttributeImageSize attributeImageSize = new TL_documentAttributeImageSize();
                                                attributeImageSize.w = botInlineResult.w;
                                                attributeImageSize.h = botInlineResult.h;
                                                document.attributes.add(attributeImageSize);
                                                fileName.file_name = "sticker.webp";
                                                try {
                                                    bitmap = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(botInlineResult.thumb_url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.thumb_url, "webp")).getAbsolutePath(), null, 90.0f, 90.0f, true);
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
                                            photo = SendMessagesHelper.getInstance().generatePhotoSizes(finalPath, null);
                                        }
                                        if (photo == null) {
                                            photo = new TL_photo();
                                            photo.date = ConnectionsManager.getInstance().getCurrentTime();
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
                                    SendMessagesHelper.getInstance().sendMessage(finalDocument, null, finalPathFinal, j, messageObject, botInlineResult.send_message.reply_markup, hashMap);
                                } else if (finalPhoto != null) {
                                    finalPhoto.caption = botInlineResult.send_message.caption;
                                    SendMessagesHelper.getInstance().sendMessage(finalPhoto, botInlineResult.content_url, j, messageObject, botInlineResult.send_message.reply_markup, hashMap);
                                } else if (finalGame != null) {
                                    SendMessagesHelper.getInstance().sendMessage(finalGame, j, botInlineResult.send_message.reply_markup, hashMap);
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
                SendMessagesHelper instance = getInstance();
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
                getInstance().sendMessage(venue, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            } else if (result.send_message instanceof TL_botInlineMessageMediaGeo) {
                MessageMedia location = new TL_messageMediaGeo();
                location.geo = result.send_message.geo;
                getInstance().sendMessage(location, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            } else if (result.send_message instanceof TL_botInlineMessageMediaContact) {
                User user = new TL_user();
                user.phone = result.send_message.phone_number;
                user.first_name = result.send_message.first_name;
                user.last_name = result.send_message.last_name;
                getInstance().sendMessage(user, dialog_id, reply_to_msg, result.send_message.reply_markup, (HashMap) params);
            }
        }
    }

    public static void prepareSendingPhotosSearch(final ArrayList<SearchImage> photos, final long dialog_id, final MessageObject reply_to_msg) {
        if (photos != null && !photos.isEmpty()) {
            new Thread(new Runnable() {
                public void run() {
                    boolean isEncrypted = ((int) dialog_id) == 0;
                    for (int a = 0; a < photos.size(); a++) {
                        final SearchImage searchImage = (SearchImage) photos.get(a);
                        final HashMap<String, String> params;
                        File cacheFile;
                        if (searchImage.type == 1) {
                            params = new HashMap();
                            TL_document tL_document = null;
                            if (searchImage.document instanceof TL_document) {
                                tL_document = searchImage.document;
                                cacheFile = FileLoader.getPathToAttach(tL_document, true);
                            } else {
                                if (!isEncrypted) {
                                    Document doc = (Document) MessagesStorage.getInstance().getSentFile(searchImage.imageUrl, !isEncrypted ? 1 : 4);
                                    if (doc instanceof TL_document) {
                                        tL_document = (TL_document) doc;
                                    }
                                }
                                cacheFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg"));
                            }
                            if (tL_document == null) {
                                if (searchImage.localUrl != null) {
                                    params.put("url", searchImage.localUrl);
                                }
                                File thumbFile = null;
                                tL_document = new TL_document();
                                tL_document.id = 0;
                                tL_document.date = ConnectionsManager.getInstance().getCurrentTime();
                                TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                                fileName.file_name = "animation.gif";
                                tL_document.attributes.add(fileName);
                                tL_document.size = searchImage.size;
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
                                    File file = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.thumbUrl, "jpg"));
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
                                    tL_document.thumb.w = searchImage.width;
                                    tL_document.thumb.h = searchImage.height;
                                    tL_document.thumb.size = 0;
                                    tL_document.thumb.location = new TL_fileLocationUnavailable();
                                    tL_document.thumb.type = "x";
                                }
                            }
                            if (searchImage.caption != null) {
                                tL_document.caption = searchImage.caption.toString();
                            }
                            final TL_document documentFinal = tL_document;
                            String originalPathFinal = searchImage.imageUrl;
                            String pathFinal = cacheFile == null ? searchImage.imageUrl : cacheFile.toString();
                            if (!(params == null || searchImage.imageUrl == null)) {
                                params.put("originalPath", searchImage.imageUrl);
                            }
                            final String str = pathFinal;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    SendMessagesHelper.getInstance().sendMessage(documentFinal, null, str, dialog_id, reply_to_msg, null, params);
                                }
                            });
                        } else {
                            boolean needDownloadHttp = true;
                            TL_photo tL_photo = null;
                            if (!isEncrypted) {
                                tL_photo = (TL_photo) MessagesStorage.getInstance().getSentFile(searchImage.imageUrl, !isEncrypted ? 0 : 3);
                            }
                            if (tL_photo == null) {
                                cacheFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.imageUrl, "jpg"));
                                if (cacheFile.exists() && cacheFile.length() != 0) {
                                    tL_photo = SendMessagesHelper.getInstance().generatePhotoSizes(cacheFile.toString(), null);
                                    if (tL_photo != null) {
                                        needDownloadHttp = false;
                                    }
                                }
                                if (tL_photo == null) {
                                    cacheFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(searchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(searchImage.thumbUrl, "jpg"));
                                    if (cacheFile.exists()) {
                                        tL_photo = SendMessagesHelper.getInstance().generatePhotoSizes(cacheFile.toString(), null);
                                    }
                                    if (tL_photo == null) {
                                        tL_photo = new TL_photo();
                                        tL_photo.date = ConnectionsManager.getInstance().getCurrentTime();
                                        TL_photoSize photoSize = new TL_photoSize();
                                        photoSize.w = searchImage.width;
                                        photoSize.h = searchImage.height;
                                        photoSize.size = 0;
                                        photoSize.location = new TL_fileLocationUnavailable();
                                        photoSize.type = "x";
                                        tL_photo.sizes.add(photoSize);
                                    }
                                }
                            }
                            if (tL_photo != null) {
                                if (searchImage.caption != null) {
                                    tL_photo.caption = searchImage.caption.toString();
                                }
                                final TL_photo photoFinal = tL_photo;
                                final boolean needDownloadHttpFinal = needDownloadHttp;
                                params = new HashMap();
                                if (searchImage.imageUrl != null) {
                                    params.put("originalPath", searchImage.imageUrl);
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        String str;
                                        SendMessagesHelper instance = SendMessagesHelper.getInstance();
                                        TL_photo tL_photo = photoFinal;
                                        if (needDownloadHttpFinal) {
                                            str = searchImage.imageUrl;
                                        } else {
                                            str = null;
                                        }
                                        instance.sendMessage(tL_photo, str, dialog_id, reply_to_msg, null, params);
                                    }
                                });
                            }
                        }
                    }
                }
            }).start();
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
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                Utilities.stageQueue.postRunnable(new Runnable() {
                    public void run() {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                String textFinal = SendMessagesHelper.getTrimmedString(text);
                                if (textFinal.length() != 0) {
                                    int count = (int) Math.ceil((double) (((float) textFinal.length()) / 4096.0f));
                                    for (int a = 0; a < count; a++) {
                                        SendMessagesHelper.getInstance().sendMessage(textFinal.substring(a * 4096, Math.min((a + 1) * 4096, textFinal.length())), dialog_id, null, null, true, null, null, null);
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public static void prepareSendingPhotos(ArrayList<String> paths, ArrayList<Uri> uris, long dialog_id, MessageObject reply_to_msg, ArrayList<String> captions, ArrayList<ArrayList<InputDocument>> masks, InputContentInfoCompat inputContent) {
        if (paths != null || uris != null) {
            if (paths != null && paths.isEmpty()) {
                return;
            }
            if (uris == null || !uris.isEmpty()) {
                final ArrayList<String> pathsCopy = new ArrayList();
                final ArrayList<Uri> urisCopy = new ArrayList();
                if (paths != null) {
                    pathsCopy.addAll(paths);
                }
                if (uris != null) {
                    urisCopy.addAll(uris);
                }
                final long j = dialog_id;
                final ArrayList<String> arrayList = captions;
                final ArrayList<ArrayList<InputDocument>> arrayList2 = masks;
                final MessageObject messageObject = reply_to_msg;
                final InputContentInfoCompat inputContentInfoCompat = inputContent;
                new Thread(new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        int a;
                        boolean isEncrypted = ((int) j) == 0;
                        ArrayList<String> sendAsDocuments = null;
                        ArrayList<String> sendAsDocumentsOriginal = null;
                        ArrayList<String> sendAsDocumentsCaptions = null;
                        int count = !pathsCopy.isEmpty() ? pathsCopy.size() : urisCopy.size();
                        String path = null;
                        Uri uri = null;
                        String extension = null;
                        for (a = 0; a < count; a++) {
                            TL_photo photo;
                            HashMap<String, String> params;
                            ArrayList<InputDocument> arrayList;
                            AbstractSerializedData serializedData;
                            int b;
                            Object obj;
                            if (!pathsCopy.isEmpty()) {
                                path = (String) pathsCopy.get(a);
                            } else if (!urisCopy.isEmpty()) {
                                uri = (Uri) urisCopy.get(a);
                            }
                            String originalPath = path;
                            String tempPath = path;
                            if (tempPath == null && uri != null) {
                                tempPath = AndroidUtilities.getPath(uri);
                                originalPath = uri.toString();
                            }
                            boolean isDocument = false;
                            if (tempPath != null) {
                                if (!tempPath.endsWith(".gif")) {
                                }
                                if (tempPath.endsWith(".gif")) {
                                    extension = "gif";
                                } else {
                                    extension = "webp";
                                }
                                isDocument = true;
                                if (isDocument) {
                                    if (tempPath == null) {
                                        File temp = new File(tempPath);
                                        originalPath = originalPath + temp.length() + "_" + temp.lastModified();
                                    } else {
                                        originalPath = null;
                                    }
                                    photo = null;
                                    if (!isEncrypted) {
                                        photo = (TL_photo) MessagesStorage.getInstance().getSentFile(originalPath, isEncrypted ? 0 : 3);
                                        if (photo == null && uri != null) {
                                            photo = (TL_photo) MessagesStorage.getInstance().getSentFile(AndroidUtilities.getPath(uri), isEncrypted ? 0 : 3);
                                        }
                                    }
                                    if (photo == null) {
                                        photo = SendMessagesHelper.getInstance().generatePhotoSizes(path, uri);
                                    }
                                    if (photo == null) {
                                        TL_photo photoFinal = photo;
                                        params = new HashMap();
                                        if (arrayList != null) {
                                            photo.caption = (String) arrayList.get(a);
                                        }
                                        if (arrayList2 != null) {
                                            boolean z;
                                            arrayList = (ArrayList) arrayList2.get(a);
                                            z = arrayList == null && !arrayList.isEmpty();
                                            photo.has_stickers = z;
                                            if (z) {
                                                serializedData = new SerializedData((arrayList.size() * 20) + 4);
                                                serializedData.writeInt32(arrayList.size());
                                                for (b = 0; b < arrayList.size(); b++) {
                                                    ((InputDocument) arrayList.get(b)).serializeToStream(serializedData);
                                                }
                                                params.put("masks", Utilities.bytesToHex(serializedData.toByteArray()));
                                            }
                                        }
                                        if (originalPath != null) {
                                            params.put("originalPath", originalPath);
                                        }
                                        final TL_photo tL_photo = photoFinal;
                                        final HashMap<String, String> hashMap = params;
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                SendMessagesHelper.getInstance().sendMessage(tL_photo, null, j, messageObject, null, hashMap);
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
                                        sendAsDocumentsCaptions.add(arrayList == null ? (String) arrayList.get(a) : null);
                                    }
                                } else {
                                    if (sendAsDocuments == null) {
                                        sendAsDocuments = new ArrayList();
                                        sendAsDocumentsOriginal = new ArrayList();
                                        sendAsDocumentsCaptions = new ArrayList();
                                    }
                                    sendAsDocuments.add(tempPath);
                                    sendAsDocumentsOriginal.add(originalPath);
                                    if (arrayList == null) {
                                        obj = (String) arrayList.get(a);
                                    } else {
                                        obj = null;
                                    }
                                    sendAsDocumentsCaptions.add(obj);
                                }
                            }
                            if (tempPath == null && uri != null) {
                                if (MediaController.isGif(uri)) {
                                    isDocument = true;
                                    originalPath = uri.toString();
                                    tempPath = MediaController.copyFileToCache(uri, "gif");
                                    extension = "gif";
                                } else if (MediaController.isWebp(uri)) {
                                    isDocument = true;
                                    originalPath = uri.toString();
                                    tempPath = MediaController.copyFileToCache(uri, "webp");
                                    extension = "webp";
                                }
                            }
                            if (isDocument) {
                                if (tempPath == null) {
                                    originalPath = null;
                                } else {
                                    File temp2 = new File(tempPath);
                                    originalPath = originalPath + temp2.length() + "_" + temp2.lastModified();
                                }
                                photo = null;
                                if (isEncrypted) {
                                    if (isEncrypted) {
                                    }
                                    photo = (TL_photo) MessagesStorage.getInstance().getSentFile(originalPath, isEncrypted ? 0 : 3);
                                    if (isEncrypted) {
                                    }
                                    photo = (TL_photo) MessagesStorage.getInstance().getSentFile(AndroidUtilities.getPath(uri), isEncrypted ? 0 : 3);
                                }
                                if (photo == null) {
                                    photo = SendMessagesHelper.getInstance().generatePhotoSizes(path, uri);
                                }
                                if (photo == null) {
                                    if (sendAsDocuments == null) {
                                        sendAsDocuments = new ArrayList();
                                        sendAsDocumentsOriginal = new ArrayList();
                                        sendAsDocumentsCaptions = new ArrayList();
                                    }
                                    sendAsDocuments.add(tempPath);
                                    sendAsDocumentsOriginal.add(originalPath);
                                    if (arrayList == null) {
                                    }
                                    sendAsDocumentsCaptions.add(arrayList == null ? (String) arrayList.get(a) : null);
                                } else {
                                    TL_photo photoFinal2 = photo;
                                    params = new HashMap();
                                    if (arrayList != null) {
                                        photo.caption = (String) arrayList.get(a);
                                    }
                                    if (arrayList2 != null) {
                                        arrayList = (ArrayList) arrayList2.get(a);
                                        if (arrayList == null) {
                                        }
                                        photo.has_stickers = z;
                                        if (z) {
                                            serializedData = new SerializedData((arrayList.size() * 20) + 4);
                                            serializedData.writeInt32(arrayList.size());
                                            for (b = 0; b < arrayList.size(); b++) {
                                                ((InputDocument) arrayList.get(b)).serializeToStream(serializedData);
                                            }
                                            params.put("masks", Utilities.bytesToHex(serializedData.toByteArray()));
                                        }
                                    }
                                    if (originalPath != null) {
                                        params.put("originalPath", originalPath);
                                    }
                                    final TL_photo tL_photo2 = photoFinal2;
                                    final HashMap<String, String> hashMap2 = params;
                                    AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                                }
                            } else {
                                if (sendAsDocuments == null) {
                                    sendAsDocuments = new ArrayList();
                                    sendAsDocumentsOriginal = new ArrayList();
                                    sendAsDocumentsCaptions = new ArrayList();
                                }
                                sendAsDocuments.add(tempPath);
                                sendAsDocumentsOriginal.add(originalPath);
                                if (arrayList == null) {
                                    obj = null;
                                } else {
                                    obj = (String) arrayList.get(a);
                                }
                                sendAsDocumentsCaptions.add(obj);
                            }
                        }
                        if (inputContentInfoCompat != null) {
                            inputContentInfoCompat.releasePermission();
                        }
                        if (sendAsDocuments != null && !sendAsDocuments.isEmpty()) {
                            for (a = 0; a < sendAsDocuments.size(); a++) {
                                SendMessagesHelper.prepareSendingDocumentInternal((String) sendAsDocuments.get(a), (String) sendAsDocumentsOriginal.get(a), null, extension, j, messageObject, (String) sendAsDocumentsCaptions.get(a));
                            }
                        }
                    }
                }).start();
            }
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

    public static void prepareSendingVideo(String videoPath, long estimatedSize, long duration, int width, int height, VideoEditedInfo videoEditedInfo, long dialog_id, MessageObject reply_to_msg, String caption) {
        if (videoPath != null && videoPath.length() != 0) {
            final long j = dialog_id;
            final VideoEditedInfo videoEditedInfo2 = videoEditedInfo;
            final String str = videoPath;
            final long j2 = duration;
            final int i = height;
            final int i2 = width;
            final long j3 = estimatedSize;
            final String str2 = caption;
            final MessageObject messageObject = reply_to_msg;
            new Thread(new Runnable() {
                public void run() {
                    boolean isEncrypted = ((int) j) == 0;
                    boolean isRound = videoEditedInfo2 != null && videoEditedInfo2.roundVideo;
                    final VideoEditedInfo editedInfo = !isRound ? videoEditedInfo2 : null;
                    if (editedInfo != null || str.endsWith("mp4") || isRound) {
                        String path = str;
                        String originalPath = str;
                        File file = new File(originalPath);
                        originalPath = originalPath + file.length() + "_" + file.lastModified();
                        if (editedInfo != null) {
                            originalPath = originalPath + j2 + "_" + editedInfo.startTime + "_" + editedInfo.endTime;
                            if (editedInfo.resultWidth == editedInfo.originalWidth) {
                                originalPath = originalPath + "_" + editedInfo.resultWidth;
                            }
                        }
                        TL_document document = null;
                        PhotoSize size;
                        EncryptedChat encryptedChat;
                        TL_documentAttributeVideo attributeVideo;
                        String fileName;
                        final TL_document videoFinal;
                        String originalPathFinal;
                        final String finalPath;
                        final HashMap<String, String> params;
                        if (isEncrypted) {
                            if (null == null) {
                                size = ImageLoader.scaleAndSaveImage(ThumbnailUtils.createVideoThumbnail(str, 1), 90.0f, 90.0f, 55, isEncrypted);
                                document = new TL_document();
                                document.thumb = size;
                                if (document.thumb != null) {
                                    document.thumb.type = "s";
                                } else {
                                    document.thumb = new TL_photoSizeEmpty();
                                    document.thumb.type = "s";
                                }
                                document.mime_type = MimeTypes.VIDEO_MP4;
                                UserConfig.saveConfig(false);
                                if (isEncrypted) {
                                    encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (j >> 32)));
                                    if (encryptedChat != null) {
                                        return;
                                    }
                                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) < 66) {
                                        attributeVideo = new TL_documentAttributeVideo_layer65();
                                    } else {
                                        attributeVideo = new TL_documentAttributeVideo();
                                    }
                                } else {
                                    attributeVideo = new TL_documentAttributeVideo();
                                }
                                attributeVideo.round_message = isRound;
                                document.attributes.add(attributeVideo);
                                if (editedInfo == null) {
                                    if (file.exists()) {
                                        document.size = (int) file.length();
                                    }
                                    SendMessagesHelper.fillVideoAttribute(str, attributeVideo, null);
                                } else {
                                    if (editedInfo.muted) {
                                        document.attributes.add(new TL_documentAttributeAnimated());
                                        SendMessagesHelper.fillVideoAttribute(str, attributeVideo, editedInfo);
                                        editedInfo.originalWidth = attributeVideo.w;
                                        editedInfo.originalHeight = attributeVideo.h;
                                        attributeVideo.w = editedInfo.resultWidth;
                                        attributeVideo.h = editedInfo.resultHeight;
                                    } else {
                                        attributeVideo.duration = (int) (j2 / 1000);
                                        if (editedInfo.rotationValue != 90) {
                                        }
                                        attributeVideo.w = i;
                                        attributeVideo.h = i2;
                                    }
                                    document.size = (int) j3;
                                    fileName = "-2147483648_" + UserConfig.lastLocalId + ".mp4";
                                    UserConfig.lastLocalId--;
                                    file = new File(FileLoader.getInstance().getDirectory(4), fileName);
                                    UserConfig.saveConfig(false);
                                    path = file.getAbsolutePath();
                                }
                            }
                            videoFinal = document;
                            originalPathFinal = originalPath;
                            finalPath = path;
                            params = new HashMap();
                            videoFinal.caption = str2;
                            if (originalPath != null) {
                                params.put("originalPath", originalPath);
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    SendMessagesHelper.getInstance().sendMessage(videoFinal, editedInfo, finalPath, j, messageObject, null, params);
                                }
                            });
                            return;
                        }
                        if (null == null) {
                            size = ImageLoader.scaleAndSaveImage(ThumbnailUtils.createVideoThumbnail(str, 1), 90.0f, 90.0f, 55, isEncrypted);
                            document = new TL_document();
                            document.thumb = size;
                            if (document.thumb != null) {
                                document.thumb = new TL_photoSizeEmpty();
                                document.thumb.type = "s";
                            } else {
                                document.thumb.type = "s";
                            }
                            document.mime_type = MimeTypes.VIDEO_MP4;
                            UserConfig.saveConfig(false);
                            if (isEncrypted) {
                                encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int) (j >> 32)));
                                if (encryptedChat != null) {
                                    if (AndroidUtilities.getPeerLayerVersion(encryptedChat.layer) < 66) {
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
                            document.attributes.add(attributeVideo);
                            if (editedInfo == null) {
                                if (editedInfo.muted) {
                                    document.attributes.add(new TL_documentAttributeAnimated());
                                    SendMessagesHelper.fillVideoAttribute(str, attributeVideo, editedInfo);
                                    editedInfo.originalWidth = attributeVideo.w;
                                    editedInfo.originalHeight = attributeVideo.h;
                                    attributeVideo.w = editedInfo.resultWidth;
                                    attributeVideo.h = editedInfo.resultHeight;
                                } else {
                                    attributeVideo.duration = (int) (j2 / 1000);
                                    if (editedInfo.rotationValue != 90 || editedInfo.rotationValue == 270) {
                                        attributeVideo.w = i;
                                        attributeVideo.h = i2;
                                    } else {
                                        attributeVideo.w = i2;
                                        attributeVideo.h = i;
                                    }
                                }
                                document.size = (int) j3;
                                fileName = "-2147483648_" + UserConfig.lastLocalId + ".mp4";
                                UserConfig.lastLocalId--;
                                file = new File(FileLoader.getInstance().getDirectory(4), fileName);
                                UserConfig.saveConfig(false);
                                path = file.getAbsolutePath();
                            } else {
                                if (file.exists()) {
                                    document.size = (int) file.length();
                                }
                                SendMessagesHelper.fillVideoAttribute(str, attributeVideo, null);
                            }
                        }
                        videoFinal = document;
                        originalPathFinal = originalPath;
                        finalPath = path;
                        params = new HashMap();
                        videoFinal.caption = str2;
                        if (originalPath != null) {
                            params.put("originalPath", originalPath);
                        }
                        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
                        return;
                    }
                    SendMessagesHelper.prepareSendingDocumentInternal(str, str, null, null, j, messageObject, str2);
                }
            }).start();
        }
    }
}
