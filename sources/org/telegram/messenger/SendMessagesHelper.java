package org.telegram.messenger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
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
import android.os.CancellationSignal;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
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
import org.telegram.tgnet.TLRPC$MessageReplies;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaContact;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaInvoice;
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
import org.telegram.tgnet.TLRPC$TL_inputDocument;
import org.telegram.tgnet.TLRPC$TL_inputEncryptedFile;
import org.telegram.tgnet.TLRPC$TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC$TL_inputMediaGame;
import org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument;
import org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerSelf;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetItem;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_inputUserSelf;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonGame;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth;
import org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC$TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC$TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_messageReplies;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.tgnet.TLRPC$TL_messages_forwardMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_historyImport;
import org.telegram.tgnet.TLRPC$TL_messages_initHistoryImport;
import org.telegram.tgnet.TLRPC$TL_messages_messages;
import org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth;
import org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMessage;
import org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendReaction;
import org.telegram.tgnet.TLRPC$TL_messages_sendScreenshotNotification;
import org.telegram.tgnet.TLRPC$TL_messages_sendVote;
import org.telegram.tgnet.TLRPC$TL_messages_startHistoryImport;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_uploadImportedMedia;
import org.telegram.tgnet.TLRPC$TL_messages_uploadMedia;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoCachedSize;
import org.telegram.tgnet.TLRPC$TL_photoPathSize;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC$TL_photoSizeProgressive;
import org.telegram.tgnet.TLRPC$TL_photoSize_layer127;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_restrictionReason;
import org.telegram.tgnet.TLRPC$TL_stickers_createStickerSet;
import org.telegram.tgnet.TLRPC$TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateEditMessage;
import org.telegram.tgnet.TLRPC$TL_updateMessageID;
import org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC$TL_updateNewMessage;
import org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage;
import org.telegram.tgnet.TLRPC$TL_updateShortSentMessage;
import org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted;
import org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault;
import org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest;
import org.telegram.tgnet.TLRPC$TL_user;
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
    private static final int ERROR_TYPE_FILE_TOO_LARGE = 2;
    private static final int ERROR_TYPE_UNSUPPORTED = 1;
    private static volatile SendMessagesHelper[] Instance = new SendMessagesHelper[4];
    private static DispatchQueue mediaSendQueue = new DispatchQueue("mediaSendQueue");
    private static ThreadPoolExecutor mediaSendThreadPool;
    /* access modifiers changed from: private */
    public HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap<>();
    private SparseArray<TLRPC$Message> editingMessages = new SparseArray<>();
    private HashMap<String, ImportingHistory> importingHistoryFiles = new HashMap<>();
    /* access modifiers changed from: private */
    public LongSparseArray<ImportingHistory> importingHistoryMap = new LongSparseArray<>();
    private HashMap<String, ImportingStickers> importingStickersFiles = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<String, ImportingStickers> importingStickersMap = new HashMap<>();
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
        public boolean forceImage;
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

    public class ImportingHistory {
        public long dialogId;
        public double estimatedUploadSpeed;
        public String historyPath;
        public long importId;
        private long lastUploadSize;
        /* access modifiers changed from: private */
        public long lastUploadTime;
        public ArrayList<Uri> mediaPaths = new ArrayList<>();
        public TLRPC$InputPeer peer;
        public int timeUntilFinish = Integer.MAX_VALUE;
        public long totalSize;
        public ArrayList<String> uploadMedia = new ArrayList<>();
        public int uploadProgress;
        public HashMap<String, Float> uploadProgresses = new HashMap<>();
        public HashSet<String> uploadSet = new HashSet<>();
        public HashMap<String, Long> uploadSize = new HashMap<>();
        public long uploadedSize;

        public ImportingHistory() {
        }

        /* access modifiers changed from: private */
        public void initImport(TLRPC$InputFile tLRPC$InputFile) {
            final TLRPC$TL_messages_initHistoryImport tLRPC$TL_messages_initHistoryImport = new TLRPC$TL_messages_initHistoryImport();
            tLRPC$TL_messages_initHistoryImport.file = tLRPC$InputFile;
            tLRPC$TL_messages_initHistoryImport.media_count = this.mediaPaths.size();
            tLRPC$TL_messages_initHistoryImport.peer = this.peer;
            SendMessagesHelper.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_initHistoryImport, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new SendMessagesHelper$ImportingHistory$1$$ExternalSyntheticLambda0(this, tLObject, tLRPC$TL_messages_initHistoryImport, tLRPC$TL_error));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$0(TLObject tLObject, TLRPC$TL_messages_initHistoryImport tLRPC$TL_messages_initHistoryImport, TLRPC$TL_error tLRPC$TL_error) {
                    if (tLObject instanceof TLRPC$TL_messages_historyImport) {
                        ImportingHistory importingHistory = ImportingHistory.this;
                        importingHistory.importId = ((TLRPC$TL_messages_historyImport) tLObject).id;
                        importingHistory.uploadSet.remove(importingHistory.historyPath);
                        SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId));
                        if (ImportingHistory.this.uploadSet.isEmpty()) {
                            ImportingHistory.this.startImport();
                        }
                        long unused = ImportingHistory.this.lastUploadTime = SystemClock.elapsedRealtime();
                        int size = ImportingHistory.this.uploadMedia.size();
                        for (int i = 0; i < size; i++) {
                            SendMessagesHelper.this.getFileLoader().uploadFile(ImportingHistory.this.uploadMedia.get(i), false, true, 67108864);
                        }
                        return;
                    }
                    SendMessagesHelper.this.importingHistoryMap.remove(ImportingHistory.this.dialogId);
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId), tLRPC$TL_messages_initHistoryImport, tLRPC$TL_error);
                }
            }, 2);
        }

        public long getUploadedCount() {
            return this.uploadedSize;
        }

        public long getTotalCount() {
            return this.totalSize;
        }

        /* access modifiers changed from: private */
        public void onFileFailedToUpload(String str) {
            if (str.equals(this.historyPath)) {
                SendMessagesHelper.this.importingHistoryMap.remove(this.dialogId);
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.code = 400;
                tLRPC$TL_error.text = "IMPORT_UPLOAD_FAILED";
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(this.dialogId), new TLRPC$TL_messages_initHistoryImport(), tLRPC$TL_error);
                return;
            }
            this.uploadSet.remove(str);
        }

        /* access modifiers changed from: private */
        public void addUploadProgress(String str, long j, float f) {
            this.uploadProgresses.put(str, Float.valueOf(f));
            this.uploadSize.put(str, Long.valueOf(j));
            this.uploadedSize = 0;
            for (Map.Entry<String, Long> value : this.uploadSize.entrySet()) {
                this.uploadedSize += ((Long) value.getValue()).longValue();
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (!str.equals(this.historyPath)) {
                long j2 = this.uploadedSize;
                long j3 = this.lastUploadSize;
                if (j2 != j3) {
                    long j4 = this.lastUploadTime;
                    if (elapsedRealtime != j4) {
                        double d = (double) (elapsedRealtime - j4);
                        Double.isNaN(d);
                        double d2 = (double) (j2 - j3);
                        Double.isNaN(d2);
                        double d3 = d2 / (d / 1000.0d);
                        double d4 = this.estimatedUploadSpeed;
                        if (d4 == 0.0d) {
                            this.estimatedUploadSpeed = d3;
                        } else {
                            this.estimatedUploadSpeed = (d3 * 0.01d) + (0.99d * d4);
                        }
                        double d5 = (double) ((this.totalSize - j2) * 1000);
                        double d6 = this.estimatedUploadSpeed;
                        Double.isNaN(d5);
                        this.timeUntilFinish = (int) (d5 / d6);
                        this.lastUploadSize = j2;
                        this.lastUploadTime = elapsedRealtime;
                    }
                }
            }
            int uploadedCount = (int) ((((float) getUploadedCount()) / ((float) getTotalCount())) * 100.0f);
            if (this.uploadProgress != uploadedCount) {
                this.uploadProgress = uploadedCount;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(this.dialogId));
            }
        }

        /* access modifiers changed from: private */
        public void onMediaImport(final String str, long j, TLRPC$InputFile tLRPC$InputFile) {
            addUploadProgress(str, j, 1.0f);
            TLRPC$TL_messages_uploadImportedMedia tLRPC$TL_messages_uploadImportedMedia = new TLRPC$TL_messages_uploadImportedMedia();
            tLRPC$TL_messages_uploadImportedMedia.peer = this.peer;
            tLRPC$TL_messages_uploadImportedMedia.import_id = this.importId;
            tLRPC$TL_messages_uploadImportedMedia.file_name = new File(str).getName();
            MimeTypeMap singleton = MimeTypeMap.getSingleton();
            int lastIndexOf = tLRPC$TL_messages_uploadImportedMedia.file_name.lastIndexOf(46);
            String lowerCase = lastIndexOf != -1 ? tLRPC$TL_messages_uploadImportedMedia.file_name.substring(lastIndexOf + 1).toLowerCase() : "txt";
            String mimeTypeFromExtension = singleton.getMimeTypeFromExtension(lowerCase);
            if (mimeTypeFromExtension == null) {
                if ("opus".equals(lowerCase)) {
                    mimeTypeFromExtension = "audio/opus";
                } else {
                    mimeTypeFromExtension = "webp".equals(lowerCase) ? "image/webp" : "text/plain";
                }
            }
            if (mimeTypeFromExtension.equals("image/jpg") || mimeTypeFromExtension.equals("image/jpeg")) {
                TLRPC$TL_inputMediaUploadedPhoto tLRPC$TL_inputMediaUploadedPhoto = new TLRPC$TL_inputMediaUploadedPhoto();
                tLRPC$TL_inputMediaUploadedPhoto.file = tLRPC$InputFile;
                tLRPC$TL_messages_uploadImportedMedia.media = tLRPC$TL_inputMediaUploadedPhoto;
            } else {
                TLRPC$TL_inputMediaUploadedDocument tLRPC$TL_inputMediaUploadedDocument = new TLRPC$TL_inputMediaUploadedDocument();
                tLRPC$TL_inputMediaUploadedDocument.file = tLRPC$InputFile;
                tLRPC$TL_inputMediaUploadedDocument.mime_type = mimeTypeFromExtension;
                tLRPC$TL_messages_uploadImportedMedia.media = tLRPC$TL_inputMediaUploadedDocument;
            }
            SendMessagesHelper.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_uploadImportedMedia, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new SendMessagesHelper$ImportingHistory$2$$ExternalSyntheticLambda0(this, str));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$0(String str) {
                    ImportingHistory.this.uploadSet.remove(str);
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId));
                    if (ImportingHistory.this.uploadSet.isEmpty()) {
                        ImportingHistory.this.startImport();
                    }
                }
            }, 2);
        }

        /* access modifiers changed from: private */
        public void startImport() {
            final TLRPC$TL_messages_startHistoryImport tLRPC$TL_messages_startHistoryImport = new TLRPC$TL_messages_startHistoryImport();
            tLRPC$TL_messages_startHistoryImport.peer = this.peer;
            tLRPC$TL_messages_startHistoryImport.import_id = this.importId;
            SendMessagesHelper.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_startHistoryImport, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new SendMessagesHelper$ImportingHistory$3$$ExternalSyntheticLambda0(this, tLRPC$TL_error, tLRPC$TL_messages_startHistoryImport));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$0(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_startHistoryImport tLRPC$TL_messages_startHistoryImport) {
                    SendMessagesHelper.this.importingHistoryMap.remove(ImportingHistory.this.dialogId);
                    if (tLRPC$TL_error == null) {
                        SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId));
                        return;
                    }
                    SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(ImportingHistory.this.dialogId), tLRPC$TL_messages_startHistoryImport, tLRPC$TL_error);
                }
            });
        }

        public void setImportProgress(int i) {
            if (i == 100) {
                SendMessagesHelper.this.importingHistoryMap.remove(this.dialogId);
            }
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(this.dialogId));
        }
    }

    public static class ImportingSticker {
        public boolean animated;
        public String emoji;
        public TLRPC$TL_inputStickerSetItem item;
        public String mimeType;
        public String path;
        public boolean validated;

        public void uploadMedia(int i, TLRPC$InputFile tLRPC$InputFile, final Runnable runnable) {
            TLRPC$TL_messages_uploadMedia tLRPC$TL_messages_uploadMedia = new TLRPC$TL_messages_uploadMedia();
            tLRPC$TL_messages_uploadMedia.peer = new TLRPC$TL_inputPeerSelf();
            TLRPC$TL_inputMediaUploadedDocument tLRPC$TL_inputMediaUploadedDocument = new TLRPC$TL_inputMediaUploadedDocument();
            tLRPC$TL_messages_uploadMedia.media = tLRPC$TL_inputMediaUploadedDocument;
            tLRPC$TL_inputMediaUploadedDocument.file = tLRPC$InputFile;
            tLRPC$TL_inputMediaUploadedDocument.mime_type = this.mimeType;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_messages_uploadMedia, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new SendMessagesHelper$ImportingSticker$1$$ExternalSyntheticLambda0(this, tLObject, runnable));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$0(TLObject tLObject, Runnable runnable) {
                    if (tLObject instanceof TLRPC$TL_messageMediaDocument) {
                        ImportingSticker.this.item = new TLRPC$TL_inputStickerSetItem();
                        ImportingSticker.this.item.document = new TLRPC$TL_inputDocument();
                        ImportingSticker importingSticker = ImportingSticker.this;
                        TLRPC$TL_inputStickerSetItem tLRPC$TL_inputStickerSetItem = importingSticker.item;
                        TLRPC$InputDocument tLRPC$InputDocument = tLRPC$TL_inputStickerSetItem.document;
                        TLRPC$Document tLRPC$Document = ((TLRPC$TL_messageMediaDocument) tLObject).document;
                        tLRPC$InputDocument.id = tLRPC$Document.id;
                        tLRPC$InputDocument.access_hash = tLRPC$Document.access_hash;
                        tLRPC$InputDocument.file_reference = tLRPC$Document.file_reference;
                        String str = importingSticker.emoji;
                        if (str == null) {
                            str = "";
                        }
                        tLRPC$TL_inputStickerSetItem.emoji = str;
                        importingSticker.mimeType = tLRPC$Document.mime_type;
                    } else {
                        ImportingSticker importingSticker2 = ImportingSticker.this;
                        if (importingSticker2.animated) {
                            importingSticker2.mimeType = "application/x-bad-tgsticker";
                        }
                    }
                    runnable.run();
                }
            }, 2);
        }
    }

    public class ImportingStickers {
        public double estimatedUploadSpeed;
        private long lastUploadSize;
        private long lastUploadTime;
        public String shortName;
        public String software;
        public int timeUntilFinish = Integer.MAX_VALUE;
        public String title;
        public long totalSize;
        public ArrayList<ImportingSticker> uploadMedia = new ArrayList<>();
        public int uploadProgress;
        public HashMap<String, Float> uploadProgresses = new HashMap<>();
        public HashMap<String, ImportingSticker> uploadSet = new HashMap<>();
        public HashMap<String, Long> uploadSize = new HashMap<>();
        public long uploadedSize;

        public ImportingStickers() {
        }

        /* access modifiers changed from: private */
        public void initImport() {
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
            this.lastUploadTime = SystemClock.elapsedRealtime();
            int size = this.uploadMedia.size();
            for (int i = 0; i < size; i++) {
                SendMessagesHelper.this.getFileLoader().uploadFile(this.uploadMedia.get(i).path, false, true, 67108864);
            }
        }

        public long getUploadedCount() {
            return this.uploadedSize;
        }

        public long getTotalCount() {
            return this.totalSize;
        }

        /* access modifiers changed from: private */
        public void onFileFailedToUpload(String str) {
            ImportingSticker remove = this.uploadSet.remove(str);
            if (remove != null) {
                this.uploadMedia.remove(remove);
            }
        }

        /* access modifiers changed from: private */
        public void addUploadProgress(String str, long j, float f) {
            this.uploadProgresses.put(str, Float.valueOf(f));
            this.uploadSize.put(str, Long.valueOf(j));
            this.uploadedSize = 0;
            for (Map.Entry<String, Long> value : this.uploadSize.entrySet()) {
                this.uploadedSize += ((Long) value.getValue()).longValue();
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j2 = this.uploadedSize;
            long j3 = this.lastUploadSize;
            if (j2 != j3) {
                long j4 = this.lastUploadTime;
                if (elapsedRealtime != j4) {
                    double d = (double) (elapsedRealtime - j4);
                    Double.isNaN(d);
                    double d2 = (double) (j2 - j3);
                    Double.isNaN(d2);
                    double d3 = d2 / (d / 1000.0d);
                    double d4 = this.estimatedUploadSpeed;
                    if (d4 == 0.0d) {
                        this.estimatedUploadSpeed = d3;
                    } else {
                        this.estimatedUploadSpeed = (d3 * 0.01d) + (0.99d * d4);
                    }
                    double d5 = (double) ((this.totalSize - j2) * 1000);
                    double d6 = this.estimatedUploadSpeed;
                    Double.isNaN(d5);
                    this.timeUntilFinish = (int) (d5 / d6);
                    this.lastUploadSize = j2;
                    this.lastUploadTime = elapsedRealtime;
                }
            }
            int uploadedCount = (int) ((((float) getUploadedCount()) / ((float) getTotalCount())) * 100.0f);
            if (this.uploadProgress != uploadedCount) {
                this.uploadProgress = uploadedCount;
                SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
            }
        }

        /* access modifiers changed from: private */
        public void onMediaImport(String str, long j, TLRPC$InputFile tLRPC$InputFile) {
            addUploadProgress(str, j, 1.0f);
            ImportingSticker importingSticker = this.uploadSet.get(str);
            if (importingSticker != null) {
                importingSticker.uploadMedia(SendMessagesHelper.this.currentAccount, tLRPC$InputFile, new SendMessagesHelper$ImportingStickers$$ExternalSyntheticLambda0(this, str));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onMediaImport$0(String str) {
            this.uploadSet.remove(str);
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
            if (this.uploadSet.isEmpty()) {
                startImport();
            }
        }

        /* access modifiers changed from: private */
        public void startImport() {
            final TLRPC$TL_stickers_createStickerSet tLRPC$TL_stickers_createStickerSet = new TLRPC$TL_stickers_createStickerSet();
            tLRPC$TL_stickers_createStickerSet.user_id = new TLRPC$TL_inputUserSelf();
            tLRPC$TL_stickers_createStickerSet.title = this.title;
            tLRPC$TL_stickers_createStickerSet.short_name = this.shortName;
            tLRPC$TL_stickers_createStickerSet.animated = this.uploadMedia.get(0).animated;
            String str = this.software;
            if (str != null) {
                tLRPC$TL_stickers_createStickerSet.software = str;
                tLRPC$TL_stickers_createStickerSet.flags |= 8;
            }
            int size = this.uploadMedia.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_inputStickerSetItem tLRPC$TL_inputStickerSetItem = this.uploadMedia.get(i).item;
                if (tLRPC$TL_inputStickerSetItem != null) {
                    tLRPC$TL_stickers_createStickerSet.stickers.add(tLRPC$TL_inputStickerSetItem);
                }
            }
            SendMessagesHelper.this.getConnectionsManager().sendRequest(tLRPC$TL_stickers_createStickerSet, new RequestDelegate() {
                public void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new SendMessagesHelper$ImportingStickers$1$$ExternalSyntheticLambda0(this, tLRPC$TL_error, tLRPC$TL_stickers_createStickerSet, tLObject));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$run$0(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_stickers_createStickerSet tLRPC$TL_stickers_createStickerSet, TLObject tLObject) {
                    SendMessagesHelper.this.importingStickersMap.remove(ImportingStickers.this.shortName);
                    if (tLRPC$TL_error == null) {
                        SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, ImportingStickers.this.shortName);
                    } else {
                        SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, ImportingStickers.this.shortName, tLRPC$TL_stickers_createStickerSet, tLRPC$TL_error);
                    }
                    if (tLObject instanceof TLRPC$TL_messages_stickerSet) {
                        NotificationCenter notificationCenter = SendMessagesHelper.this.getNotificationCenter();
                        int i = NotificationCenter.stickersImportComplete;
                        if (notificationCenter.hasObservers(i)) {
                            SendMessagesHelper.this.getNotificationCenter().postNotificationName(i, tLObject);
                            return;
                        }
                        SendMessagesHelper.this.getMediaDataController().toggleStickerSet((Context) null, tLObject, 2, (BaseFragment) null, false, false);
                    }
                }
            });
        }

        public void setImportProgress(int i) {
            if (i == 100) {
                SendMessagesHelper.this.importingStickersMap.remove(this.shortName);
            }
            SendMessagesHelper.this.getNotificationCenter().postNotificationName(NotificationCenter.stickersImportProgressChanged, this.shortName);
        }
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
            SendMessagesHelper$LocationProvider$$ExternalSyntheticLambda0 sendMessagesHelper$LocationProvider$$ExternalSyntheticLambda0 = new SendMessagesHelper$LocationProvider$$ExternalSyntheticLambda0(this);
            this.locationQueryCancelRunnable = sendMessagesHelper$LocationProvider$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(sendMessagesHelper$LocationProvider$$ExternalSyntheticLambda0, 5000);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$start$0() {
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
                HashMap access$1200 = SendMessagesHelper.this.delayedMessages;
                access$1200.remove("group_" + this.groupId);
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda22(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploaded);
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploadFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.filePreparingStarted);
        getNotificationCenter().addObserver(this, NotificationCenter.fileNewChunkAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.filePreparingFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
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
        this.importingHistoryFiles.clear();
        this.importingHistoryMap.clear();
        this.importingStickersFiles.clear();
        this.importingStickersMap.clear();
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
        boolean z = true;
        if (i5 == NotificationCenter.fileUploadProgressChanged) {
            String str4 = objArr[0];
            ImportingHistory importingHistory = this.importingHistoryFiles.get(str4);
            if (importingHistory != null) {
                Long l = objArr[1];
                importingHistory.addUploadProgress(str4, l.longValue(), ((float) l.longValue()) / ((float) objArr[2].longValue()));
            }
            ImportingStickers importingStickers = this.importingStickersFiles.get(str4);
            if (importingStickers != null) {
                Long l2 = objArr[1];
                importingStickers.addUploadProgress(str4, l2.longValue(), ((float) l2.longValue()) / ((float) objArr[2].longValue()));
            }
        } else if (i5 == NotificationCenter.fileUploaded) {
            String str5 = objArr[0];
            TLRPC$InputFile tLRPC$InputFile2 = objArr[1];
            TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile3 = objArr[2];
            ImportingHistory importingHistory2 = this.importingHistoryFiles.get(str5);
            if (importingHistory2 != null) {
                if (str5.equals(importingHistory2.historyPath)) {
                    importingHistory2.initImport(tLRPC$InputFile2);
                } else {
                    importingHistory2.onMediaImport(str5, objArr[5].longValue(), tLRPC$InputFile2);
                }
            }
            ImportingStickers importingStickers2 = this.importingStickersFiles.get(str5);
            if (importingStickers2 != null) {
                importingStickers2.onMediaImport(str5, objArr[5].longValue(), tLRPC$InputFile2);
            }
            ArrayList arrayList4 = this.delayedMessages.get(str5);
            if (arrayList4 != null) {
                while (i6 < arrayList4.size()) {
                    DelayedMessage delayedMessage = (DelayedMessage) arrayList4.get(i6);
                    TLObject tLObject2 = delayedMessage.sendRequest;
                    if (tLObject2 instanceof TLRPC$TL_messages_sendMedia) {
                        tLRPC$InputMedia = ((TLRPC$TL_messages_sendMedia) tLObject2).media;
                    } else if (tLObject2 instanceof TLRPC$TL_messages_editMessage) {
                        tLRPC$InputMedia = ((TLRPC$TL_messages_editMessage) tLObject2).media;
                    } else {
                        tLRPC$InputMedia = tLObject2 instanceof TLRPC$TL_messages_sendMultiMedia ? (TLRPC$InputMedia) delayedMessage.extraHashMap.get(str5) : null;
                    }
                    if (tLRPC$InputFile2 == null || tLRPC$InputMedia == null) {
                        arrayList2 = arrayList4;
                        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile4 = tLRPC$InputEncryptedFile3;
                        tLRPC$InputFile = tLRPC$InputFile2;
                        str2 = str5;
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
                                    tLRPC$TL_decryptedMessage.media.size = (long) ((int) objArr[5].longValue());
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
                            str3 = str5;
                            performSendMessageRequest(delayedMessage.sendRequest, delayedMessage.obj, delayedMessage.originalPath, delayedMessage, true, (DelayedMessage) null, delayedMessage.parentObject, (HashMap<String, String>) null, delayedMessage.scheduled);
                        } else {
                            DelayedMessage delayedMessage2 = delayedMessage;
                            arrayList3 = arrayList4;
                            tLRPC$InputEncryptedFile2 = tLRPC$InputEncryptedFile3;
                            i4 = i6;
                            tLRPC$InputFile = tLRPC$InputFile2;
                            str3 = str5;
                            if (i7 != z) {
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
                                            delayedMessage3.performMediaUpload = z;
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
                    str5 = str2;
                    tLRPC$InputFile2 = tLRPC$InputFile;
                    z = true;
                    tLRPC$InputEncryptedFile3 = tLRPC$InputEncryptedFile;
                }
                String str6 = str5;
                if (arrayList4.isEmpty()) {
                    this.delayedMessages.remove(str6);
                }
            }
        } else if (i5 == NotificationCenter.fileUploadFailed) {
            String str7 = objArr[0];
            boolean booleanValue = objArr[1].booleanValue();
            ImportingHistory importingHistory3 = this.importingHistoryFiles.get(str7);
            if (importingHistory3 != null) {
                importingHistory3.onFileFailedToUpload(str7);
            }
            ImportingStickers importingStickers3 = this.importingStickersFiles.get(str7);
            if (importingStickers3 != null) {
                importingStickers3.onFileFailedToUpload(str7);
            }
            ArrayList arrayList5 = this.delayedMessages.get(str7);
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
                    this.delayedMessages.remove(str7);
                }
            }
        } else if (i5 == NotificationCenter.filePreparingStarted) {
            MessageObject messageObject5 = objArr[0];
            if (messageObject5.getId() != 0) {
                String str8 = objArr[1];
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
            if (i5 == NotificationCenter.fileNewChunkAvailable) {
                MessageObject messageObject7 = objArr[0];
                if (messageObject7.getId() != 0) {
                    long longValue = objArr[2].longValue();
                    long longValue2 = objArr[3].longValue();
                    getFileLoader().checkUploadNewDataAvailable(objArr[1], DialogObject.isEncryptedDialog(messageObject7.getDialogId()), longValue, longValue2);
                    if (longValue2 != 0) {
                        stopVideoService(messageObject7.messageOwner.attachPath);
                        ArrayList arrayList7 = this.delayedMessages.get(messageObject7.messageOwner.attachPath);
                        if (arrayList7 != null) {
                            for (int i8 = 0; i8 < arrayList7.size(); i8++) {
                                DelayedMessage delayedMessage8 = (DelayedMessage) arrayList7.get(i8);
                                if (delayedMessage8.type == 4) {
                                    int i9 = 0;
                                    while (true) {
                                        if (i9 >= delayedMessage8.messageObjects.size()) {
                                            break;
                                        }
                                        MessageObject messageObject8 = delayedMessage8.messageObjects.get(i9);
                                        if (messageObject8 == messageObject7) {
                                            delayedMessage8.obj.shouldRemoveVideoEditedInfo = true;
                                            messageObject8.messageOwner.params.remove("ve");
                                            messageObject8.messageOwner.media.document.size = (long) ((int) longValue2);
                                            ArrayList arrayList8 = new ArrayList();
                                            arrayList8.add(messageObject8.messageOwner);
                                            getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList8, false, true, false, 0, messageObject8.scheduled);
                                            break;
                                        }
                                        i9++;
                                    }
                                } else {
                                    MessageObject messageObject9 = delayedMessage8.obj;
                                    if (messageObject9 == messageObject7) {
                                        messageObject9.shouldRemoveVideoEditedInfo = true;
                                        messageObject9.messageOwner.params.remove("ve");
                                        delayedMessage8.obj.messageOwner.media.document.size = (long) ((int) longValue2);
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
                MessageObject messageObject10 = objArr[0];
                if (messageObject10.getId() != 0) {
                    String str9 = objArr[1];
                    stopVideoService(messageObject10.messageOwner.attachPath);
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
                                    } else if (delayedMessage9.messageObjects.get(i11) == messageObject10) {
                                        delayedMessage9.markAsError();
                                        arrayList10.remove(i10);
                                        break;
                                    } else {
                                        i11++;
                                    }
                                }
                            } else if (delayedMessage9.obj == messageObject10) {
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
                    int i12 = 0;
                    while (i12 < arrayList11.size()) {
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
                                messageObject = messageObject6;
                            }
                            messageObject = messageObject2;
                            c = 1;
                        }
                        if (c == 0) {
                            File file = new File(FileLoader.getDirectory(4), Utilities.MD5(str10) + "." + ImageLoader.getHttpUrlExtension(str10, "file"));
                            DispatchQueue dispatchQueue = Utilities.globalQueue;
                            SendMessagesHelper$$ExternalSyntheticLambda25 sendMessagesHelper$$ExternalSyntheticLambda25 = r0;
                            SendMessagesHelper$$ExternalSyntheticLambda25 sendMessagesHelper$$ExternalSyntheticLambda252 = new SendMessagesHelper$$ExternalSyntheticLambda25(this, file, messageObject, delayedMessage10, str10);
                            dispatchQueue.postRunnable(sendMessagesHelper$$ExternalSyntheticLambda25);
                        } else if (c == 1) {
                            Utilities.globalQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda39(this, delayedMessage10, new File(FileLoader.getDirectory(4), Utilities.MD5(str10) + ".gif"), messageObject));
                            i12++;
                            messageObject6 = null;
                        }
                        i12++;
                        messageObject6 = null;
                    }
                    this.delayedMessages.remove(str10);
                }
            } else if (i5 == NotificationCenter.fileLoaded) {
                String str11 = objArr[0];
                ArrayList arrayList12 = this.delayedMessages.get(str11);
                if (arrayList12 != null) {
                    while (i6 < arrayList12.size()) {
                        performSendDelayedMessage((DelayedMessage) arrayList12.get(i6));
                        i6++;
                    }
                    this.delayedMessages.remove(str11);
                }
            } else if ((i5 == NotificationCenter.httpFileDidFailedLoad || i5 == NotificationCenter.fileLoadFailed) && (arrayList = this.delayedMessages.get(str)) != null) {
                while (i6 < arrayList.size()) {
                    ((DelayedMessage) arrayList.get(i6)).markAsError();
                    i6++;
                }
                this.delayedMessages.remove((str = objArr[0]));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$2(File file, MessageObject messageObject, DelayedMessage delayedMessage, String str) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda62(this, generatePhotoSizes(file.toString(), (Uri) null), messageObject, file, delayedMessage, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$1(TLRPC$TL_photo tLRPC$TL_photo, MessageObject messageObject, File file, DelayedMessage delayedMessage, String str) {
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
    public /* synthetic */ void lambda$didReceivedNotification$4(DelayedMessage delayedMessage, File file, MessageObject messageObject) {
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda40(this, delayedMessage, file, document, messageObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$3(DelayedMessage delayedMessage, File file, TLRPC$Document tLRPC$Document, MessageObject messageObject) {
        delayedMessage.httpLocation = null;
        delayedMessage.obj.messageOwner.attachPath = file.toString();
        if (!tLRPC$Document.thumbs.isEmpty()) {
            TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$Document.thumbs.get(0);
            if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize)) {
                delayedMessage.photoSize = tLRPC$PhotoSize;
                delayedMessage.locationParent = tLRPC$Document;
            }
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
            messageObject.resetLayout();
            messageObject.checkLayout();
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

    /* JADX WARNING: Code restructure failed: missing block: B:54:0x019d, code lost:
        r23 = r9;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancelSendingMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r26) {
        /*
            r25 = this;
            r0 = r25
            r1 = r26
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
            r11 = 0
        L_0x0019:
            int r10 = r26.size()
            r12 = 1
            if (r6 >= r10) goto L_0x01b0
            java.lang.Object r8 = r1.get(r6)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            boolean r9 = r8.scheduled
            if (r9 == 0) goto L_0x002b
            r11 = 1
        L_0x002b:
            long r9 = r8.getDialogId()
            int r13 = r8.getId()
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
            r5.add(r13)
            int r13 = r8.getId()
            boolean r14 = r8.scheduled
            org.telegram.tgnet.TLRPC$Message r13 = r0.removeFromSendingMessages(r13, r14)
            if (r13 == 0) goto L_0x004f
            org.telegram.tgnet.ConnectionsManager r14 = r25.getConnectionsManager()
            int r13 = r13.reqId
            r14.cancelRequest(r13, r12)
        L_0x004f:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r13 = r0.delayedMessages
            java.util.Set r13 = r13.entrySet()
            java.util.Iterator r21 = r13.iterator()
        L_0x0059:
            boolean r13 = r21.hasNext()
            if (r13 == 0) goto L_0x01a6
            java.lang.Object r13 = r21.next()
            java.util.Map$Entry r13 = (java.util.Map.Entry) r13
            java.lang.Object r14 = r13.getValue()
            java.util.ArrayList r14 = (java.util.ArrayList) r14
            r15 = 0
        L_0x006c:
            int r4 = r14.size()
            if (r15 >= r4) goto L_0x019b
            java.lang.Object r4 = r14.get(r15)
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r4 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r4
            int r12 = r4.type
            r22 = r7
            r7 = 4
            if (r12 != r7) goto L_0x0157
            r7 = -1
            r12 = 0
            r13 = 0
        L_0x0082:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r14 = r4.messageObjects
            int r14 = r14.size()
            if (r13 >= r14) goto L_0x00aa
            java.util.ArrayList<org.telegram.messenger.MessageObject> r12 = r4.messageObjects
            java.lang.Object r12 = r12.get(r13)
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            int r14 = r12.getId()
            int r15 = r8.getId()
            if (r14 != r15) goto L_0x00a7
            int r7 = r8.getId()
            boolean r14 = r8.scheduled
            r0.removeFromUploadingMessages(r7, r14)
            r7 = r13
            goto L_0x00aa
        L_0x00a7:
            int r13 = r13 + 1
            goto L_0x0082
        L_0x00aa:
            if (r7 < 0) goto L_0x019d
            java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r4.messageObjects
            r13.remove(r7)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r13 = r4.messages
            r13.remove(r7)
            java.util.ArrayList<java.lang.String> r13 = r4.originalPaths
            r13.remove(r7)
            java.util.ArrayList<java.lang.Object> r13 = r4.parentObjects
            boolean r13 = r13.isEmpty()
            if (r13 != 0) goto L_0x00c8
            java.util.ArrayList<java.lang.Object> r13 = r4.parentObjects
            r13.remove(r7)
        L_0x00c8:
            org.telegram.tgnet.TLObject r13 = r4.sendRequest
            if (r13 == 0) goto L_0x00d4
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r13 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r13 = r13.multi_media
            r13.remove(r7)
            goto L_0x00e2
        L_0x00d4:
            org.telegram.tgnet.TLObject r13 = r4.sendEncryptedRequest
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r13 = (org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia) r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r14 = r13.messages
            r14.remove(r7)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r13 = r13.files
            r13.remove(r7)
        L_0x00e2:
            org.telegram.messenger.MediaController r7 = org.telegram.messenger.MediaController.getInstance()
            r7.cancelVideoConvert(r8)
            java.util.HashMap<java.lang.Object, java.lang.Object> r7 = r4.extraHashMap
            java.lang.Object r7 = r7.get(r12)
            java.lang.String r7 = (java.lang.String) r7
            if (r7 == 0) goto L_0x00f6
            r2.add(r7)
        L_0x00f6:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r4.messageObjects
            boolean r7 = r7.isEmpty()
            if (r7 == 0) goto L_0x0103
            r4.sendDelayedRequests()
            goto L_0x019d
        L_0x0103:
            int r7 = r4.finalGroupMessage
            int r12 = r8.getId()
            if (r7 != r12) goto L_0x014b
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r4.messageObjects
            int r12 = r7.size()
            r13 = 1
            int r12 = r12 - r13
            java.lang.Object r7 = r7.get(r12)
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            int r12 = r7.getId()
            r4.finalGroupMessage = r12
            org.telegram.tgnet.TLRPC$Message r12 = r7.messageOwner
            java.util.HashMap<java.lang.String, java.lang.String> r12 = r12.params
            java.lang.String r13 = "final"
            java.lang.String r14 = "1"
            r12.put(r13, r14)
            org.telegram.tgnet.TLRPC$TL_messages_messages r14 = new org.telegram.tgnet.TLRPC$TL_messages_messages
            r14.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r12 = r14.messages
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            r12.add(r7)
            org.telegram.messenger.MessagesStorage r13 = r25.getMessagesStorage()
            r23 = r9
            long r9 = r4.peer
            r17 = -2
            r18 = 0
            r19 = 0
            r15 = r9
            r20 = r11
            r13.putMessages((org.telegram.tgnet.TLRPC$messages_Messages) r14, (long) r15, (int) r17, (int) r18, (boolean) r19, (boolean) r20)
            goto L_0x014d
        L_0x014b:
            r23 = r9
        L_0x014d:
            boolean r7 = r3.contains(r4)
            if (r7 != 0) goto L_0x019f
            r3.add(r4)
            goto L_0x019f
        L_0x0157:
            r23 = r9
            org.telegram.messenger.MessageObject r7 = r4.obj
            int r7 = r7.getId()
            int r9 = r8.getId()
            if (r7 != r9) goto L_0x0192
            int r7 = r8.getId()
            boolean r9 = r8.scheduled
            r0.removeFromUploadingMessages(r7, r9)
            r14.remove(r15)
            r4.sendDelayedRequests()
            org.telegram.messenger.MediaController r7 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r9 = r4.obj
            r7.cancelVideoConvert(r9)
            int r7 = r14.size()
            if (r7 != 0) goto L_0x019f
            java.lang.Object r7 = r13.getKey()
            java.lang.String r7 = (java.lang.String) r7
            r2.add(r7)
            org.telegram.tgnet.TLObject r4 = r4.sendEncryptedRequest
            if (r4 == 0) goto L_0x019f
            r7 = 1
            goto L_0x01a1
        L_0x0192:
            int r15 = r15 + 1
            r7 = r22
            r9 = r23
            r12 = 1
            goto L_0x006c
        L_0x019b:
            r22 = r7
        L_0x019d:
            r23 = r9
        L_0x019f:
            r7 = r22
        L_0x01a1:
            r9 = r23
            r12 = 1
            goto L_0x0059
        L_0x01a6:
            r22 = r7
            r23 = r9
            int r6 = r6 + 1
            r8 = r23
            goto L_0x0019
        L_0x01b0:
            r4 = 0
        L_0x01b1:
            int r6 = r2.size()
            if (r4 >= r6) goto L_0x01df
            java.lang.Object r6 = r2.get(r4)
            java.lang.String r6 = (java.lang.String) r6
            java.lang.String r10 = "http"
            boolean r10 = r6.startsWith(r10)
            if (r10 == 0) goto L_0x01cd
            org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.getInstance()
            r10.cancelLoadHttpFile(r6)
            goto L_0x01d4
        L_0x01cd:
            org.telegram.messenger.FileLoader r10 = r25.getFileLoader()
            r10.cancelFileUpload(r6, r7)
        L_0x01d4:
            r0.stopVideoService(r6)
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r10 = r0.delayedMessages
            r10.remove(r6)
            int r4 = r4 + 1
            goto L_0x01b1
        L_0x01df:
            int r2 = r3.size()
            r4 = 0
        L_0x01e4:
            if (r4 >= r2) goto L_0x01f4
            java.lang.Object r6 = r3.get(r4)
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r6
            r7 = 1
            r10 = 0
            r0.sendReadyToSendGroup(r6, r10, r7)
            int r4 = r4 + 1
            goto L_0x01e4
        L_0x01f4:
            r7 = 1
            r10 = 0
            int r2 = r26.size()
            if (r2 != r7) goto L_0x021c
            java.lang.Object r2 = r1.get(r10)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            boolean r2 = r2.isEditing()
            if (r2 == 0) goto L_0x021c
            java.lang.Object r2 = r1.get(r10)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.previousMedia
            if (r2 == 0) goto L_0x021c
            java.lang.Object r1 = r1.get(r10)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r0.revertEditingMessageObject(r1)
            goto L_0x0226
        L_0x021c:
            org.telegram.messenger.MessagesController r4 = r25.getMessagesController()
            r6 = 0
            r7 = 0
            r10 = 0
            r4.deleteMessages(r5, r6, r7, r8, r10, r11)
        L_0x0226:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.cancelSendingMessage(java.util.ArrayList):void");
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
            TLRPC$EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(messageObject.getDialogId())));
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
            sendScreenshotMessage(getMessagesController().getUser(Long.valueOf(messageObject.getDialogId())), messageObject.getReplyMsgId(), messageObject.messageOwner);
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

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processForwardFromMyName(org.telegram.messenger.MessageObject r18, long r19) {
        /*
            r17 = this;
            r15 = r18
            if (r15 != 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.tgnet.TLRPC$Message r0 = r15.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            r2 = 0
            if (r1 == 0) goto L_0x0114
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            if (r3 != 0) goto L_0x0114
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r3 != 0) goto L_0x0114
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r3 != 0) goto L_0x0114
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r3 != 0) goto L_0x0114
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r19)
            if (r0 == 0) goto L_0x0065
            org.telegram.tgnet.TLRPC$Message r0 = r15.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r0.peer_id
            if (r1 == 0) goto L_0x0065
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$Photo r1 = r0.photo
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r1 != 0) goto L_0x0036
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r0 == 0) goto L_0x0065
        L_0x0036:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "sent_"
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Message r2 = r15.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            long r2 = r2.channel_id
            r1.append(r2)
            java.lang.String r2 = "_"
            r1.append(r2)
            int r2 = r18.getId()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "parentObject"
            r0.put(r2, r1)
            r11 = r0
            goto L_0x0066
        L_0x0065:
            r11 = r2
        L_0x0066:
            org.telegram.tgnet.TLRPC$Message r0 = r15.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r0.media
            org.telegram.tgnet.TLRPC$Photo r1 = r4.photo
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r2 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$TL_photo r1 = (org.telegram.tgnet.TLRPC$TL_photo) r1
            r2 = 0
            org.telegram.messenger.MessageObject r5 = r15.replyMessageObject
            r6 = 0
            java.lang.String r7 = r0.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r0.entities
            r9 = 0
            r12 = 1
            r13 = 0
            int r14 = r4.ttl_seconds
            r0 = r17
            r3 = r19
            r10 = r11
            r11 = r12
            r12 = r13
            r13 = r14
            r14 = r18
            r0.sendMessage(r1, r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
            goto L_0x0193
        L_0x008e:
            org.telegram.tgnet.TLRPC$Document r1 = r4.document
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r2 == 0) goto L_0x00b2
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC$TL_document) r1
            r2 = 0
            java.lang.String r3 = r0.attachPath
            org.telegram.messenger.MessageObject r6 = r15.replyMessageObject
            r7 = 0
            java.lang.String r8 = r0.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r9 = r0.entities
            r10 = 0
            r12 = 1
            r13 = 0
            int r14 = r4.ttl_seconds
            r16 = 0
            r0 = r17
            r4 = r19
            r15 = r18
            r0.sendMessage(r1, r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
            goto L_0x0193
        L_0x00b2:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r0 != 0) goto L_0x0103
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r0 == 0) goto L_0x00bb
            goto L_0x0103
        L_0x00bb:
            java.lang.String r0 = r4.phone_number
            if (r0 == 0) goto L_0x00e8
            org.telegram.tgnet.TLRPC$TL_userContact_old2 r2 = new org.telegram.tgnet.TLRPC$TL_userContact_old2
            r2.<init>()
            org.telegram.tgnet.TLRPC$Message r0 = r15.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r1 = r0.phone_number
            r2.phone = r1
            java.lang.String r1 = r0.first_name
            r2.first_name = r1
            java.lang.String r1 = r0.last_name
            r2.last_name = r1
            long r0 = r0.user_id
            r2.id = r0
            org.telegram.messenger.MessageObject r5 = r15.replyMessageObject
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 1
            r10 = 0
            r1 = r17
            r3 = r19
            r1.sendMessage((org.telegram.tgnet.TLRPC$User) r2, (long) r3, (org.telegram.messenger.MessageObject) r5, (org.telegram.messenger.MessageObject) r6, (org.telegram.tgnet.TLRPC$ReplyMarkup) r7, (java.util.HashMap<java.lang.String, java.lang.String>) r8, (boolean) r9, (int) r10)
            goto L_0x0193
        L_0x00e8:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r19)
            if (r0 != 0) goto L_0x0193
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r2.add(r15)
            r5 = 1
            r6 = 0
            r7 = 1
            r8 = 0
            r1 = r17
            r3 = r19
            r1.sendMessage((java.util.ArrayList<org.telegram.messenger.MessageObject>) r2, (long) r3, (boolean) r5, (boolean) r6, (boolean) r7, (int) r8)
            goto L_0x0193
        L_0x0103:
            org.telegram.messenger.MessageObject r5 = r15.replyMessageObject
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 1
            r10 = 0
            r1 = r17
            r2 = r4
            r3 = r19
            r1.sendMessage((org.telegram.tgnet.TLRPC$MessageMedia) r2, (long) r3, (org.telegram.messenger.MessageObject) r5, (org.telegram.messenger.MessageObject) r6, (org.telegram.tgnet.TLRPC$ReplyMarkup) r7, (java.util.HashMap<java.lang.String, java.lang.String>) r8, (boolean) r9, (int) r10)
            goto L_0x0193
        L_0x0114:
            java.lang.String r3 = r0.message
            if (r3 == 0) goto L_0x017a
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r3 == 0) goto L_0x0120
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r9 = r1
            goto L_0x0121
        L_0x0120:
            r9 = r2
        L_0x0121:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            if (r0 == 0) goto L_0x0163
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0163
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0 = 0
        L_0x0131:
            org.telegram.tgnet.TLRPC$Message r1 = r15.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x0163
            org.telegram.tgnet.TLRPC$Message r1 = r15.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
            java.lang.Object r1 = r1.get(r0)
            org.telegram.tgnet.TLRPC$MessageEntity r1 = (org.telegram.tgnet.TLRPC$MessageEntity) r1
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBold
            if (r3 != 0) goto L_0x015d
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            if (r3 != 0) goto L_0x015d
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPre
            if (r3 != 0) goto L_0x015d
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCode
            if (r3 != 0) goto L_0x015d
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r3 != 0) goto L_0x015d
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler
            if (r3 == 0) goto L_0x0160
        L_0x015d:
            r2.add(r1)
        L_0x0160:
            int r0 = r0 + 1
            goto L_0x0131
        L_0x0163:
            r11 = r2
            org.telegram.tgnet.TLRPC$Message r0 = r15.messageOwner
            java.lang.String r4 = r0.message
            org.telegram.messenger.MessageObject r7 = r15.replyMessageObject
            r8 = 0
            r10 = 1
            r12 = 0
            r13 = 0
            r14 = 1
            r15 = 0
            r16 = 0
            r3 = r17
            r5 = r19
            r3.sendMessage(r4, r5, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
            goto L_0x0193
        L_0x017a:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r19)
            if (r0 == 0) goto L_0x0193
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r2.add(r15)
            r5 = 1
            r6 = 0
            r7 = 1
            r8 = 0
            r1 = r17
            r3 = r19
            r1.sendMessage((java.util.ArrayList<org.telegram.messenger.MessageObject>) r2, (long) r3, (boolean) r5, (boolean) r6, (boolean) r7, (int) r8)
        L_0x0193:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.processForwardFromMyName(org.telegram.messenger.MessageObject, long):void");
    }

    public void sendScreenshotMessage(TLRPC$User tLRPC$User, int i, TLRPC$Message tLRPC$Message) {
        TLRPC$User tLRPC$User2 = tLRPC$User;
        int i2 = i;
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        if (tLRPC$User2 == null || i2 == 0 || tLRPC$User2.id == getUserConfig().getClientUserId()) {
            return;
        }
        TLRPC$TL_messages_sendScreenshotNotification tLRPC$TL_messages_sendScreenshotNotification = new TLRPC$TL_messages_sendScreenshotNotification();
        TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
        tLRPC$TL_messages_sendScreenshotNotification.peer = tLRPC$TL_inputPeerUser;
        tLRPC$TL_inputPeerUser.access_hash = tLRPC$User2.access_hash;
        tLRPC$TL_inputPeerUser.user_id = tLRPC$User2.id;
        if (tLRPC$Message2 != null) {
            tLRPC$TL_messages_sendScreenshotNotification.reply_to_msg_id = i2;
            tLRPC$TL_messages_sendScreenshotNotification.random_id = tLRPC$Message2.random_id;
        } else {
            tLRPC$Message2 = new TLRPC$TL_messageService();
            tLRPC$Message2.random_id = getNextRandomId();
            tLRPC$Message2.dialog_id = tLRPC$User2.id;
            tLRPC$Message2.unread = true;
            tLRPC$Message2.out = true;
            int newMessageId = getUserConfig().getNewMessageId();
            tLRPC$Message2.id = newMessageId;
            tLRPC$Message2.local_id = newMessageId;
            TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
            tLRPC$Message2.from_id = tLRPC$TL_peerUser;
            tLRPC$TL_peerUser.user_id = getUserConfig().getClientUserId();
            int i3 = tLRPC$Message2.flags | 256;
            tLRPC$Message2.flags = i3;
            tLRPC$Message2.flags = i3 | 8;
            TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
            tLRPC$Message2.reply_to = tLRPC$TL_messageReplyHeader;
            tLRPC$TL_messageReplyHeader.reply_to_msg_id = i2;
            TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
            tLRPC$Message2.peer_id = tLRPC$TL_peerUser2;
            tLRPC$TL_peerUser2.user_id = tLRPC$User2.id;
            tLRPC$Message2.date = getConnectionsManager().getCurrentTime();
            tLRPC$Message2.action = new TLRPC$TL_messageActionScreenshotTaken();
            getUserConfig().saveConfig(false);
        }
        tLRPC$TL_messages_sendScreenshotNotification.random_id = tLRPC$Message2.random_id;
        MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message2, false, true);
        messageObject.messageOwner.send_state = 1;
        messageObject.wasJustSent = true;
        ArrayList arrayList = new ArrayList();
        arrayList.add(messageObject);
        getMessagesController().updateInterfaceWithMessages(tLRPC$Message2.dialog_id, arrayList, false);
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(tLRPC$Message2);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList2, false, true, false, 0, false);
        performSendMessageRequest(tLRPC$TL_messages_sendScreenshotNotification, messageObject, (String) null, (DelayedMessage) null, (Object) null, (HashMap<String, String>) null, false);
    }

    public void sendSticker(TLRPC$Document tLRPC$Document, String str, long j, MessageObject messageObject, MessageObject messageObject2, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
        TLRPC$TL_document_layer82 tLRPC$TL_document_layer82;
        HashMap hashMap;
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        if (tLRPC$Document2 != null) {
            if (DialogObject.isEncryptedDialog(j)) {
                if (getMessagesController().getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j))) != null) {
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
                        File pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true);
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
                mediaSendQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda44(this, tLRPC$TL_document_layer82, j, messageObject, messageObject2, z, i, obj, sendAnimationData));
                return;
            }
            if (!TextUtils.isEmpty(str)) {
                hashMap = new HashMap();
                hashMap.put("query", str);
            } else {
                hashMap = null;
            }
            sendMessage((TLRPC$TL_document) tLRPC$TL_document_layer82, (VideoEditedInfo) null, (String) null, j, messageObject, messageObject2, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, obj, sendAnimationData);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSticker$6(TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, Object obj, MessageObject.SendAnimationData sendAnimationData) {
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
        ensureMediaThumbExists(getAccountInstance(), false, tLRPC$Document, file.getAbsolutePath(), (Uri) null, 0);
        strArr[0] = getKeyForPhotoSize(getAccountInstance(), FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 320), bitmapArr, true, true);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda72(this, bitmapArr, strArr, tLRPC$Document, j, messageObject, messageObject2, z, i, obj, sendAnimationData));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSticker$5(Bitmap[] bitmapArr, String[] strArr, TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, Object obj, MessageObject.SendAnimationData sendAnimationData) {
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0], false);
        }
        sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, (String) null, j, messageObject, messageObject2, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj, sendAnimationData);
    }

    /* JADX WARNING: Removed duplicated region for block: B:111:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x04f8  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0502  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x051b  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0575  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0587  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x05a4  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x05cb  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x05cd  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0627  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x062e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x063e  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x0640  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x065b  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x068f  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0695  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0697  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x069f  */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x06db  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0716  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x072e  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x073b  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x073d  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x075d  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0783  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0793  */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x07be  */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x07ea  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x07ec  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x07f9  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x07fb  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x0847  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01f7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r53, long r54, boolean r56, boolean r57, boolean r58, int r59) {
        /*
            r52 = this;
            r14 = r52
            r15 = r53
            r12 = r54
            r11 = r56
            r10 = r57
            r9 = r59
            r8 = 0
            if (r15 == 0) goto L_0x090a
            boolean r0 = r53.isEmpty()
            if (r0 == 0) goto L_0x0017
            goto L_0x090a
        L_0x0017:
            org.telegram.messenger.UserConfig r0 = r52.getUserConfig()
            long r6 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r54)
            if (r0 != 0) goto L_0x08eb
            org.telegram.messenger.MessagesController r0 = r52.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r5 = r0.getPeer(r12)
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r54)
            r16 = 0
            r2 = 1
            if (r0 == 0) goto L_0x0058
            org.telegram.messenger.MessagesController r0 = r52.getMessagesController()
            java.lang.Long r1 = java.lang.Long.valueOf(r54)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x0045
            return r8
        L_0x0045:
            r9 = r5
            r2 = r16
            r5 = 0
            r8 = 0
            r21 = 0
            r22 = 1
            r23 = 0
            r24 = 1
            r25 = 1
            r26 = 1
            goto L_0x00c3
        L_0x0058:
            org.telegram.messenger.MessagesController r0 = r52.getMessagesController()
            long r8 = -r12
            java.lang.Long r1 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x008a
            boolean r1 = r0.signatures
            boolean r3 = r0.megagroup
            r3 = r3 ^ r2
            if (r3 == 0) goto L_0x0086
            boolean r8 = r0.has_link
            if (r8 == 0) goto L_0x0086
            org.telegram.messenger.MessagesController r8 = r52.getMessagesController()
            r9 = r5
            long r4 = r0.id
            org.telegram.tgnet.TLRPC$ChatFull r4 = r8.getChatFull(r4)
            if (r4 == 0) goto L_0x0087
            long r4 = r4.linked_chat_id
            goto L_0x008f
        L_0x0086:
            r9 = r5
        L_0x0087:
            r4 = r16
            goto L_0x008f
        L_0x008a:
            r9 = r5
            r4 = r16
            r1 = 0
            r3 = 0
        L_0x008f:
            if (r0 == 0) goto L_0x009e
            org.telegram.messenger.MessagesController r8 = r52.getMessagesController()
            r21 = r3
            long r2 = r0.id
            java.lang.String r2 = r8.getAdminRank(r2, r6)
            goto L_0x00a1
        L_0x009e:
            r21 = r3
            r2 = 0
        L_0x00a1:
            boolean r3 = org.telegram.messenger.ChatObject.canSendStickers(r0)
            boolean r8 = org.telegram.messenger.ChatObject.canSendMedia(r0)
            boolean r22 = org.telegram.messenger.ChatObject.canSendEmbed(r0)
            boolean r23 = org.telegram.messenger.ChatObject.canSendPolls(r0)
            r24 = r8
            r25 = r22
            r26 = r23
            r8 = r0
            r22 = r3
            r23 = r21
            r21 = r1
            r50 = r4
            r5 = r2
            r2 = r50
        L_0x00c3:
            androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray
            r4.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r27 = new java.util.ArrayList
            r27.<init>()
            java.util.ArrayList r28 = new java.util.ArrayList
            r28.<init>()
            androidx.collection.LongSparseArray r29 = new androidx.collection.LongSparseArray
            r29.<init>()
            r30 = r0
            org.telegram.messenger.MessagesController r0 = r52.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r0 = r0.getInputPeer((long) r12)
            int r31 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r31 != 0) goto L_0x00f2
            r31 = 1
            goto L_0x00f4
        L_0x00f2:
            r31 = 0
        L_0x00f4:
            r33 = r28
            r14 = r29
            r29 = r30
            r32 = 0
            r30 = r0
            r28 = r1
            r0 = 0
            r50 = r27
            r27 = r9
            r9 = r50
        L_0x0107:
            int r1 = r53.size()
            if (r0 >= r1) goto L_0x08e6
            java.lang.Object r1 = r15.get(r0)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r34 = r1.getId()
            if (r34 <= 0) goto L_0x0869
            boolean r34 = r1.needDrawBluredPreview()
            if (r34 == 0) goto L_0x0121
            goto L_0x0869
        L_0x0121:
            boolean r34 = r1.isSticker()
            if (r34 != 0) goto L_0x013f
            boolean r34 = r1.isAnimatedSticker()
            if (r34 != 0) goto L_0x013f
            boolean r34 = r1.isGif()
            if (r34 != 0) goto L_0x013f
            boolean r34 = r1.isGame()
            if (r34 == 0) goto L_0x013a
            goto L_0x013f
        L_0x013a:
            r35 = r0
            r34 = 0
            goto L_0x0143
        L_0x013f:
            r35 = r0
            r34 = 1
        L_0x0143:
            if (r22 != 0) goto L_0x0196
            if (r34 == 0) goto L_0x0196
            if (r32 != 0) goto L_0x0174
            r1 = 8
            boolean r1 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r1)
            if (r1 == 0) goto L_0x0153
            r0 = 4
            goto L_0x0154
        L_0x0153:
            r0 = 1
        L_0x0154:
            r32 = r0
            r41 = r2
            r19 = r4
            r39 = r5
            r43 = r8
            r44 = r28
            r20 = r30
            r3 = r33
            r18 = 1
            r38 = 0
            r33 = r6
            r30 = r14
            r28 = r27
            r14 = r35
            r35 = 0
            goto L_0x08c6
        L_0x0174:
            r41 = r2
            r19 = r4
            r39 = r5
            r43 = r8
            r40 = r9
            r44 = r28
            r37 = r29
            r20 = r30
            r36 = r33
            r18 = 1
            r38 = 0
            r33 = r6
            r30 = r14
            r28 = r27
            r14 = r35
            r35 = 0
            goto L_0x08c0
        L_0x0196:
            if (r24 != 0) goto L_0x01b3
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r15 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r15 != 0) goto L_0x01a4
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L_0x01b3
        L_0x01a4:
            if (r34 != 0) goto L_0x01b3
            if (r32 != 0) goto L_0x0174
            r0 = 7
            boolean r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r0)
            if (r0 == 0) goto L_0x01b1
            r0 = 5
            goto L_0x0154
        L_0x01b1:
            r0 = 2
            goto L_0x0154
        L_0x01b3:
            if (r26 != 0) goto L_0x01cb
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x01cb
            if (r32 != 0) goto L_0x0174
            r0 = 10
            boolean r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r0)
            if (r0 == 0) goto L_0x01c9
            r0 = 6
            goto L_0x0154
        L_0x01c9:
            r0 = 3
            goto L_0x0154
        L_0x01cb:
            org.telegram.tgnet.TLRPC$TL_message r0 = new org.telegram.tgnet.TLRPC$TL_message
            r0.<init>()
            if (r11 != 0) goto L_0x0363
            long r38 = r1.getDialogId()
            int r15 = (r38 > r6 ? 1 : (r38 == r6 ? 0 : -1))
            if (r15 != 0) goto L_0x01ee
            boolean r15 = r1.isFromUser()
            if (r15 == 0) goto L_0x01ee
            org.telegram.tgnet.TLRPC$Message r15 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r15 = r15.from_id
            r34 = r14
            long r14 = r15.user_id
            int r38 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
            if (r38 != 0) goto L_0x01f0
            r14 = 1
            goto L_0x01f1
        L_0x01ee:
            r34 = r14
        L_0x01f0:
            r14 = 0
        L_0x01f1:
            boolean r15 = r1.isForwarded()
            if (r15 == 0) goto L_0x0281
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r14 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r14.<init>()
            r0.fwd_from = r14
            org.telegram.tgnet.TLRPC$Message r15 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r15 = r15.fwd_from
            int r11 = r15.flags
            r20 = 1
            r11 = r11 & 1
            if (r11 == 0) goto L_0x0214
            int r11 = r14.flags
            r11 = r11 | 1
            r14.flags = r11
            org.telegram.tgnet.TLRPC$Peer r11 = r15.from_id
            r14.from_id = r11
        L_0x0214:
            int r11 = r15.flags
            r11 = r11 & 32
            if (r11 == 0) goto L_0x0224
            int r11 = r14.flags
            r11 = r11 | 32
            r14.flags = r11
            java.lang.String r11 = r15.from_name
            r14.from_name = r11
        L_0x0224:
            int r11 = r15.flags
            r37 = 4
            r11 = r11 & 4
            if (r11 == 0) goto L_0x0236
            int r11 = r14.flags
            r11 = r11 | 4
            r14.flags = r11
            int r11 = r15.channel_post
            r14.channel_post = r11
        L_0x0236:
            int r11 = r15.flags
            r36 = 8
            r11 = r11 & 8
            if (r11 == 0) goto L_0x0248
            int r11 = r14.flags
            r11 = r11 | 8
            r14.flags = r11
            java.lang.String r11 = r15.post_author
            r14.post_author = r11
        L_0x0248:
            int r11 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r11 == 0) goto L_0x024e
            if (r23 == 0) goto L_0x0272
        L_0x024e:
            int r11 = r15.flags
            r11 = r11 & 16
            if (r11 == 0) goto L_0x0272
            long r14 = r1.getDialogId()
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r14)
            if (r11 != 0) goto L_0x0272
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r0.fwd_from
            int r14 = r11.flags
            r14 = r14 | 16
            r11.flags = r14
            org.telegram.tgnet.TLRPC$Message r14 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            org.telegram.tgnet.TLRPC$Peer r15 = r14.saved_from_peer
            r11.saved_from_peer = r15
            int r14 = r14.saved_from_msg_id
            r11.saved_from_msg_id = r14
        L_0x0272:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r0.fwd_from
            org.telegram.tgnet.TLRPC$Message r14 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            int r14 = r14.date
            r11.date = r14
            r11 = 4
            r0.flags = r11
            goto L_0x0332
        L_0x0281:
            r11 = 4
            if (r14 != 0) goto L_0x0332
            long r14 = r1.getFromChatId()
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r11 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r11.<init>()
            r0.fwd_from = r11
            r38 = r9
            int r9 = r1.getId()
            r11.channel_post = r9
            org.telegram.tgnet.TLRPC$MessageFwdHeader r9 = r0.fwd_from
            int r11 = r9.flags
            r37 = 4
            r11 = r11 | 4
            r9.flags = r11
            boolean r9 = r1.isFromUser()
            if (r9 == 0) goto L_0x02be
            org.telegram.tgnet.TLRPC$MessageFwdHeader r9 = r0.fwd_from
            org.telegram.tgnet.TLRPC$Message r11 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r11 = r11.from_id
            r9.from_id = r11
            int r11 = r9.flags
            r20 = 1
            r11 = r11 | 1
            r9.flags = r11
            r41 = r2
            r39 = r5
            r40 = r8
            goto L_0x02ee
        L_0x02be:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r9 = r0.fwd_from
            org.telegram.tgnet.TLRPC$TL_peerChannel r11 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r11.<init>()
            r9.from_id = r11
            org.telegram.tgnet.TLRPC$MessageFwdHeader r9 = r0.fwd_from
            org.telegram.tgnet.TLRPC$Peer r11 = r9.from_id
            r39 = r5
            org.telegram.tgnet.TLRPC$Message r5 = r1.messageOwner
            r40 = r8
            org.telegram.tgnet.TLRPC$Peer r8 = r5.peer_id
            r41 = r2
            long r2 = r8.channel_id
            r11.channel_id = r2
            int r2 = r9.flags
            r3 = 1
            r2 = r2 | r3
            r9.flags = r2
            boolean r2 = r5.post
            if (r2 == 0) goto L_0x02ee
            int r2 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x02ee
            org.telegram.tgnet.TLRPC$Peer r2 = r5.from_id
            if (r2 == 0) goto L_0x02ec
            r8 = r2
        L_0x02ec:
            r9.from_id = r8
        L_0x02ee:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.post_author
            if (r2 == 0) goto L_0x02f5
            goto L_0x0328
        L_0x02f5:
            boolean r2 = r1.isOutOwner()
            if (r2 != 0) goto L_0x0328
            int r2 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x0328
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            boolean r2 = r2.post
            if (r2 == 0) goto L_0x0328
            org.telegram.messenger.MessagesController r2 = r52.getMessagesController()
            java.lang.Long r3 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            if (r2 == 0) goto L_0x0328
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r0.fwd_from
            java.lang.String r5 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r5, r2)
            r3.post_author = r2
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r0.fwd_from
            int r3 = r2.flags
            r5 = 8
            r3 = r3 | r5
            r2.flags = r3
        L_0x0328:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            int r2 = r2.date
            r0.date = r2
            r2 = 4
            r0.flags = r2
            goto L_0x033a
        L_0x0332:
            r41 = r2
            r39 = r5
            r40 = r8
            r38 = r9
        L_0x033a:
            int r2 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x036d
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r0.fwd_from
            if (r2 == 0) goto L_0x036d
            int r3 = r2.flags
            r3 = r3 | 16
            r2.flags = r3
            int r3 = r1.getId()
            r2.saved_from_msg_id = r3
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r0.fwd_from
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            r2.saved_from_peer = r3
            long r8 = r3.user_id
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x036d
            long r8 = r1.getDialogId()
            r3.user_id = r8
            goto L_0x036d
        L_0x0363:
            r41 = r2
            r39 = r5
            r40 = r8
            r38 = r9
            r34 = r14
        L_0x036d:
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r0.params = r2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = ""
            r3.append(r5)
            int r8 = r1.getId()
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            java.lang.String r8 = "fwd_id"
            r2.put(r8, r3)
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r0.params
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r5)
            long r8 = r1.getDialogId()
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            java.lang.String r8 = "fwd_peer"
            r2.put(r8, r3)
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r2.restriction_reason
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x03bf
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r2.restriction_reason
            r0.restriction_reason = r2
            int r2 = r0.flags
            r3 = 4194304(0x400000, float:5.877472E-39)
            r2 = r2 | r3
            r0.flags = r2
        L_0x03bf:
            if (r25 != 0) goto L_0x03d1
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r2 == 0) goto L_0x03d1
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r2.<init>()
            r0.media = r2
            goto L_0x03d7
        L_0x03d1:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            r0.media = r2
        L_0x03d7:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r0.media
            if (r2 == 0) goto L_0x03e1
            int r2 = r0.flags
            r2 = r2 | 512(0x200, float:7.175E-43)
            r0.flags = r2
        L_0x03e1:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            long r2 = r2.via_bot_id
            int r8 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r8 == 0) goto L_0x03f1
            r0.via_bot_id = r2
            int r2 = r0.flags
            r2 = r2 | 2048(0x800, float:2.87E-42)
            r0.flags = r2
        L_0x03f1:
            int r2 = (r41 > r16 ? 1 : (r41 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x0410
            org.telegram.tgnet.TLRPC$TL_messageReplies r2 = new org.telegram.tgnet.TLRPC$TL_messageReplies
            r2.<init>()
            r0.replies = r2
            r3 = 1
            r2.comments = r3
            r8 = r41
            r2.channel_id = r8
            int r11 = r2.flags
            r11 = r11 | r3
            r2.flags = r11
            int r2 = r0.flags
            r3 = 8388608(0x800000, float:1.17549435E-38)
            r2 = r2 | r3
            r0.flags = r2
            goto L_0x0412
        L_0x0410:
            r8 = r41
        L_0x0412:
            if (r10 == 0) goto L_0x0418
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r0.media
            if (r2 != 0) goto L_0x041e
        L_0x0418:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            r0.message = r2
        L_0x041e:
            java.lang.String r2 = r0.message
            if (r2 != 0) goto L_0x0424
            r0.message = r5
        L_0x0424:
            int r2 = r1.getId()
            r0.fwd_msg_id = r2
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r3 = r2.attachPath
            r0.attachPath = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r2.entities
            r0.entities = r3
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            if (r2 == 0) goto L_0x04ed
            org.telegram.tgnet.TLRPC$TL_replyInlineMarkup r2 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            r2.<init>()
            r0.reply_markup = r2
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows
            int r2 = r2.size()
            r3 = 0
            r11 = 0
        L_0x044d:
            if (r3 >= r2) goto L_0x04d4
            org.telegram.tgnet.TLRPC$Message r14 = r1.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r14 = r14.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r14 = r14.rows
            java.lang.Object r14 = r14.get(r3)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r14 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r15 = r14.buttons
            int r15 = r15.size()
            r36 = r2
            r2 = 0
            r37 = 0
        L_0x0466:
            r41 = r8
            if (r2 >= r15) goto L_0x04c7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r8 = r14.buttons
            java.lang.Object r8 = r8.get(r2)
            org.telegram.tgnet.TLRPC$KeyboardButton r8 = (org.telegram.tgnet.TLRPC$KeyboardButton) r8
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r43 = r11
            if (r9 != 0) goto L_0x0487
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl
            if (r11 != 0) goto L_0x0487
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline
            if (r11 != 0) goto L_0x0487
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r11 == 0) goto L_0x0485
            goto L_0x0487
        L_0x0485:
            r11 = 1
            goto L_0x04c9
        L_0x0487:
            if (r9 == 0) goto L_0x04a8
            org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r9.<init>()
            int r11 = r8.flags
            r9.flags = r11
            java.lang.String r11 = r8.fwd_text
            if (r11 == 0) goto L_0x049b
            r9.fwd_text = r11
            r9.text = r11
            goto L_0x049f
        L_0x049b:
            java.lang.String r11 = r8.text
            r9.text = r11
        L_0x049f:
            java.lang.String r11 = r8.url
            r9.url = r11
            int r8 = r8.button_id
            r9.button_id = r8
            r8 = r9
        L_0x04a8:
            if (r37 != 0) goto L_0x04b7
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow
            r9.<init>()
            org.telegram.tgnet.TLRPC$ReplyMarkup r11 = r0.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r11 = r11.rows
            r11.add(r9)
            goto L_0x04b9
        L_0x04b7:
            r9 = r37
        L_0x04b9:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r11 = r9.buttons
            r11.add(r8)
            int r2 = r2 + 1
            r37 = r9
            r8 = r41
            r11 = r43
            goto L_0x0466
        L_0x04c7:
            r43 = r11
        L_0x04c9:
            if (r11 == 0) goto L_0x04cc
            goto L_0x04d8
        L_0x04cc:
            int r3 = r3 + 1
            r2 = r36
            r8 = r41
            goto L_0x044d
        L_0x04d4:
            r41 = r8
            r43 = r11
        L_0x04d8:
            if (r11 != 0) goto L_0x04e1
            int r2 = r0.flags
            r2 = r2 | 64
            r0.flags = r2
            goto L_0x04ef
        L_0x04e1:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            r8 = 0
            r2.reply_markup = r8
            int r2 = r0.flags
            r2 = r2 & -65
            r0.flags = r2
            goto L_0x04f0
        L_0x04ed:
            r41 = r8
        L_0x04ef:
            r8 = 0
        L_0x04f0:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r0.entities
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x04fe
            int r2 = r0.flags
            r2 = r2 | 128(0x80, float:1.794E-43)
            r0.flags = r2
        L_0x04fe:
            java.lang.String r2 = r0.attachPath
            if (r2 != 0) goto L_0x0504
            r0.attachPath = r5
        L_0x0504:
            org.telegram.messenger.UserConfig r2 = r52.getUserConfig()
            int r2 = r2.getNewMessageId()
            r0.id = r2
            r0.local_id = r2
            r2 = 1
            r0.out = r2
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            long r2 = r2.grouped_id
            int r5 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r5 == 0) goto L_0x0541
            java.lang.Object r2 = r4.get(r2)
            java.lang.Long r2 = (java.lang.Long) r2
            if (r2 != 0) goto L_0x0534
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random
            long r2 = r2.nextLong()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            long r14 = r3.grouped_id
            r4.put(r14, r2)
        L_0x0534:
            long r2 = r2.longValue()
            r0.grouped_id = r2
            int r2 = r0.flags
            r3 = 131072(0x20000, float:1.83671E-40)
            r2 = r2 | r3
            r0.flags = r2
        L_0x0541:
            r5 = r27
            long r2 = r5.channel_id
            int r9 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r9 == 0) goto L_0x0561
            if (r23 == 0) goto L_0x0561
            if (r21 == 0) goto L_0x0557
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r0.from_id = r2
            r2.user_id = r6
            goto L_0x0559
        L_0x0557:
            r0.from_id = r5
        L_0x0559:
            r2 = 1
            r0.post = r2
            r11 = r39
            r9 = r40
            goto L_0x059e
        L_0x0561:
            r2 = 1
            org.telegram.messenger.MessagesController r3 = r52.getMessagesController()
            long r14 = -r12
            org.telegram.tgnet.TLRPC$ChatFull r3 = r3.getChatFull(r14)
            r9 = r40
            long r14 = org.telegram.messenger.ChatObject.getSendAsPeerId(r9, r3, r2)
            int r2 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x0587
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r0.from_id = r2
            r2.user_id = r6
            int r2 = r0.flags
            r2 = r2 | 256(0x100, float:3.59E-43)
            r0.flags = r2
            r11 = r39
            goto L_0x059e
        L_0x0587:
            org.telegram.messenger.MessagesController r2 = r52.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r2 = r2.getPeer(r14)
            r0.from_id = r2
            r11 = r39
            if (r39 == 0) goto L_0x059e
            r0.post_author = r11
            int r2 = r0.flags
            r3 = 65536(0x10000, float:9.18355E-41)
            r2 = r2 | r3
            r0.flags = r2
        L_0x059e:
            long r2 = r0.random_id
            int r14 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x05aa
            long r2 = r52.getNextRandomId()
            r0.random_id = r2
        L_0x05aa:
            long r2 = r0.random_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r14 = r38
            r14.add(r2)
            long r2 = r0.random_id
            r15 = r34
            r15.put(r2, r0)
            int r2 = r0.fwd_msg_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r3 = r33
            r3.add(r2)
            r2 = r59
            if (r2 == 0) goto L_0x05cd
            r8 = r2
            goto L_0x05d7
        L_0x05cd:
            org.telegram.tgnet.ConnectionsManager r19 = r52.getConnectionsManager()
            int r19 = r19.getCurrentTime()
            r8 = r19
        L_0x05d7:
            r0.date = r8
            r19 = r4
            r8 = r30
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r4 == 0) goto L_0x05f1
            if (r23 == 0) goto L_0x05f1
            r33 = r6
            if (r2 != 0) goto L_0x060a
            r6 = 1
            r0.views = r6
            int r6 = r0.flags
            r6 = r6 | 1024(0x400, float:1.435E-42)
            r0.flags = r6
            goto L_0x060a
        L_0x05f1:
            r33 = r6
            org.telegram.tgnet.TLRPC$Message r6 = r1.messageOwner
            int r7 = r6.flags
            r7 = r7 & 1024(0x400, float:1.435E-42)
            if (r7 == 0) goto L_0x0607
            if (r2 != 0) goto L_0x0607
            int r6 = r6.views
            r0.views = r6
            int r6 = r0.flags
            r6 = r6 | 1024(0x400, float:1.435E-42)
            r0.flags = r6
        L_0x0607:
            r6 = 1
            r0.unread = r6
        L_0x060a:
            r0.dialog_id = r12
            r0.peer_id = r5
            boolean r6 = org.telegram.messenger.MessageObject.isVoiceMessage(r0)
            if (r6 != 0) goto L_0x061d
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r0)
            if (r6 == 0) goto L_0x061b
            goto L_0x061d
        L_0x061b:
            r4 = 1
            goto L_0x0631
        L_0x061d:
            if (r4 == 0) goto L_0x062e
            long r6 = r1.getChannelId()
            int r4 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x062e
            boolean r4 = r1.isContentUnread()
            r0.media_unread = r4
            goto L_0x061b
        L_0x062e:
            r4 = 1
            r0.media_unread = r4
        L_0x0631:
            org.telegram.messenger.MessageObject r6 = new org.telegram.messenger.MessageObject
            r30 = r15
            r15 = r52
            int r7 = r15.currentAccount
            r6.<init>(r7, r0, r4, r4)
            if (r2 == 0) goto L_0x0640
            r7 = 1
            goto L_0x0641
        L_0x0640:
            r7 = 0
        L_0x0641:
            r6.scheduled = r7
            org.telegram.tgnet.TLRPC$Message r7 = r6.messageOwner
            r7.send_state = r4
            r6.wasJustSent = r4
            r4 = r29
            r4.add(r6)
            r7 = r28
            r7.add(r0)
            r28 = r5
            org.telegram.messenger.MessageObject r5 = r1.replyMessageObject
            r40 = r9
            if (r5 == 0) goto L_0x068f
            r5 = 0
        L_0x065c:
            int r9 = r53.size()
            if (r5 >= r9) goto L_0x068a
            r9 = r53
            java.lang.Object r29 = r9.get(r5)
            org.telegram.messenger.MessageObject r29 = (org.telegram.messenger.MessageObject) r29
            r39 = r11
            int r11 = r29.getId()
            org.telegram.messenger.MessageObject r10 = r1.replyMessageObject
            int r10 = r10.getId()
            if (r11 != r10) goto L_0x0683
            org.telegram.tgnet.TLRPC$Message r5 = r6.messageOwner
            org.telegram.messenger.MessageObject r10 = r1.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r11 = r10.messageOwner
            r5.replyMessage = r11
            r6.replyMessageObject = r10
            goto L_0x0693
        L_0x0683:
            int r5 = r5 + 1
            r10 = r57
            r11 = r39
            goto L_0x065c
        L_0x068a:
            r9 = r53
            r39 = r11
            goto L_0x0693
        L_0x068f:
            r39 = r11
            r9 = r53
        L_0x0693:
            if (r2 == 0) goto L_0x0697
            r5 = 1
            goto L_0x0698
        L_0x0697:
            r5 = 0
        L_0x0698:
            r15.putToSendingMessages(r0, r5)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x06d3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "forward message user_id = "
            r0.append(r5)
            long r5 = r8.user_id
            r0.append(r5)
            java.lang.String r5 = " chat_id = "
            r0.append(r5)
            long r5 = r8.chat_id
            r0.append(r5)
            java.lang.String r5 = " channel_id = "
            r0.append(r5)
            long r5 = r8.channel_id
            r0.append(r5)
            java.lang.String r5 = " access_hash = "
            r0.append(r5)
            long r5 = r8.access_hash
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x06d3:
            int r0 = r7.size()
            r5 = 100
            if (r0 == r5) goto L_0x0716
            int r0 = r53.size()
            r5 = 1
            int r0 = r0 - r5
            r6 = r35
            if (r6 == r0) goto L_0x0718
            int r0 = r53.size()
            int r0 = r0 - r5
            if (r6 == r0) goto L_0x0701
            int r0 = r6 + 1
            java.lang.Object r0 = r9.get(r0)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r10 = r0.getDialogId()
            long r35 = r1.getDialogId()
            int r0 = (r10 > r35 ? 1 : (r10 == r35 ? 0 : -1))
            if (r0 == 0) goto L_0x0701
            goto L_0x0718
        L_0x0701:
            r36 = r3
            r37 = r4
            r44 = r7
            r20 = r8
            r43 = r40
            r18 = 1
            r35 = 0
            r38 = 0
            r40 = r14
            r14 = r6
            goto L_0x08c0
        L_0x0716:
            r6 = r35
        L_0x0718:
            org.telegram.messenger.MessagesStorage r43 = r52.getMessagesStorage()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r7)
            r45 = 0
            r46 = 1
            r47 = 0
            r48 = 0
            if (r2 == 0) goto L_0x072e
            r49 = 1
            goto L_0x0730
        L_0x072e:
            r49 = 0
        L_0x0730:
            r44 = r0
            r43.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r44, (boolean) r45, (boolean) r46, (boolean) r47, (int) r48, (boolean) r49)
            org.telegram.messenger.MessagesController r0 = r52.getMessagesController()
            if (r2 == 0) goto L_0x073d
            r5 = 1
            goto L_0x073e
        L_0x073d:
            r5 = 0
        L_0x073e:
            r0.updateInterfaceWithMessages(r12, r4, r5)
            org.telegram.messenger.NotificationCenter r0 = r52.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r10 = 0
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r0.postNotificationName(r5, r11)
            org.telegram.messenger.UserConfig r0 = r52.getUserConfig()
            r0.saveConfig(r10)
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r11 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages
            r11.<init>()
            r11.to_peer = r8
            if (r58 == 0) goto L_0x077e
            int r0 = r15.currentAccount
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r10 = "silent_"
            r5.append(r10)
            r5.append(r12)
            java.lang.String r5 = r5.toString()
            r10 = 0
            boolean r0 = r0.getBoolean(r5, r10)
            if (r0 == 0) goto L_0x077c
            goto L_0x077e
        L_0x077c:
            r0 = 0
            goto L_0x077f
        L_0x077e:
            r0 = 1
        L_0x077f:
            r11.silent = r0
            if (r2 == 0) goto L_0x078b
            r11.schedule_date = r2
            int r0 = r11.flags
            r0 = r0 | 1024(0x400, float:1.435E-42)
            r11.flags = r0
        L_0x078b:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r0 == 0) goto L_0x07be
            org.telegram.messenger.MessagesController r0 = r52.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            r35 = r6
            long r5 = r5.channel_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r5)
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r5 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            r5.<init>()
            r11.from_peer = r5
            org.telegram.tgnet.TLRPC$Message r6 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            r10 = r7
            long r6 = r6.channel_id
            r5.channel_id = r6
            if (r0 == 0) goto L_0x07c8
            long r6 = r0.access_hash
            r5.access_hash = r6
            goto L_0x07c8
        L_0x07be:
            r35 = r6
            r10 = r7
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r0.<init>()
            r11.from_peer = r0
        L_0x07c8:
            r11.random_id = r14
            r11.id = r3
            r7 = r56
            r11.drop_author = r7
            r6 = r57
            r11.drop_media_captions = r6
            int r0 = r53.size()
            r5 = 1
            if (r0 != r5) goto L_0x07ec
            r0 = 0
            java.lang.Object r18 = r9.get(r0)
            r0 = r18
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0.with_my_score
            if (r0 == 0) goto L_0x07ec
            r0 = 1
            goto L_0x07ed
        L_0x07ec:
            r0 = 0
        L_0x07ed:
            r11.with_my_score = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r4)
            r5 = 2147483646(0x7ffffffe, float:NaN)
            if (r2 != r5) goto L_0x07fb
            r5 = 1
            goto L_0x07fc
        L_0x07fb:
            r5 = 0
        L_0x07fc:
            org.telegram.tgnet.ConnectionsManager r13 = r52.getConnectionsManager()
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda79 r12 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda79
            r29 = r0
            r20 = r8
            r8 = r35
            r35 = 0
            r0 = r12
            r36 = r3
            r37 = r4
            r4 = r1
            r1 = r52
            r18 = 1
            r2 = r54
            r27 = r4
            r38 = 0
            r4 = r59
            r6 = r31
            r7 = r30
            r43 = r40
            r40 = r14
            r14 = r8
            r8 = r10
            r44 = r10
            r10 = r9
            r9 = r29
            r15 = r10
            r10 = r27
            r27 = r11
            r11 = r28
            r15 = r12
            r12 = r27
            r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r0 = 68
            r1 = r27
            r13.sendRequest(r1, r15, r0)
            int r0 = r53.size()
            int r0 = r0 + -1
            if (r14 == r0) goto L_0x08c0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            androidx.collection.LongSparseArray r4 = new androidx.collection.LongSparseArray
            r4.<init>()
            r29 = r0
            r44 = r1
            r9 = r2
            r30 = r4
            goto L_0x08c6
        L_0x0869:
            r41 = r2
            r19 = r4
            r39 = r5
            r43 = r8
            r40 = r9
            r44 = r28
            r37 = r29
            r20 = r30
            r36 = r33
            r18 = 1
            r35 = 0
            r38 = 0
            r33 = r6
            r30 = r14
            r28 = r27
            r14 = r0
            int r0 = r1.type
            if (r0 != 0) goto L_0x08c0
            java.lang.CharSequence r0 = r1.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08c0
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x089e
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            r6 = r0
            goto L_0x08a0
        L_0x089e:
            r6 = r38
        L_0x08a0:
            java.lang.CharSequence r0 = r1.messageText
            java.lang.String r2 = r0.toString()
            r4 = 0
            r5 = 0
            if (r6 == 0) goto L_0x08ac
            r7 = 1
            goto L_0x08ad
        L_0x08ac:
            r7 = 0
        L_0x08ad:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r0.entities
            r9 = 0
            r10 = 0
            r13 = 0
            r0 = r52
            r1 = r2
            r2 = r54
            r11 = r58
            r12 = r59
            r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
        L_0x08c0:
            r3 = r36
            r29 = r37
            r9 = r40
        L_0x08c6:
            int r0 = r14 + 1
            r15 = r53
            r12 = r54
            r11 = r56
            r10 = r57
            r4 = r19
            r27 = r28
            r14 = r30
            r6 = r33
            r5 = r39
            r8 = r43
            r28 = r44
            r33 = r3
            r30 = r20
            r2 = r41
            goto L_0x0107
        L_0x08e6:
            r0 = r52
            r8 = r32
            goto L_0x0909
        L_0x08eb:
            r35 = 0
            r8 = 0
        L_0x08ee:
            int r0 = r53.size()
            if (r8 >= r0) goto L_0x0906
            r0 = r53
            java.lang.Object r1 = r0.get(r8)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r0 = r52
            r2 = r54
            r0.processForwardFromMyName(r1, r2)
            int r8 = r8 + 1
            goto L_0x08ee
        L_0x0906:
            r0 = r52
            r8 = 0
        L_0x0909:
            return r8
        L_0x090a:
            r0 = r14
            r35 = 0
            return r35
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, boolean, boolean, int):int");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00fc  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01c2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendMessage$14(long r27, int r29, boolean r30, boolean r31, androidx.collection.LongSparseArray r32, java.util.ArrayList r33, java.util.ArrayList r34, org.telegram.messenger.MessageObject r35, org.telegram.tgnet.TLRPC$Peer r36, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r37, org.telegram.tgnet.TLObject r38, org.telegram.tgnet.TLRPC$TL_error r39) {
        /*
            r26 = this;
            r11 = r26
            r12 = r29
            r13 = r33
            r14 = r34
            r0 = r39
            r15 = 1
            if (r0 != 0) goto L_0x01f6
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
            if (r0 >= r1) goto L_0x01d8
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
            goto L_0x01c9
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
            if (r2 == 0) goto L_0x00b7
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewMessage) r1
            org.telegram.tgnet.TLRPC$Message r2 = r1.message
            org.telegram.messenger.MessagesController r4 = r26.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            r4.processNewDifferenceParams(r0, r10, r0, r1)
            r10 = r2
        L_0x00b4:
            r38 = r3
            goto L_0x00da
        L_0x00b7:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x00c1
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage) r1
            org.telegram.tgnet.TLRPC$Message r1 = r1.message
            r10 = r1
            goto L_0x00b4
        L_0x00c1:
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage) r1
            org.telegram.tgnet.TLRPC$Message r2 = r1.message
            org.telegram.messenger.MessagesController r4 = r26.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            org.telegram.tgnet.TLRPC$Peer r0 = r2.peer_id
            r39 = r2
            r38 = r3
            long r2 = r0.channel_id
            r4.processNewChannelDifferenceParams(r10, r1, r2)
            r10 = r39
        L_0x00da:
            if (r30 == 0) goto L_0x00e6
            int r0 = r10.date
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 == r1) goto L_0x00e6
            r19 = 0
            goto L_0x00e8
        L_0x00e6:
            r19 = r38
        L_0x00e8:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r10)
            if (r19 != 0) goto L_0x00fa
            int r0 = r16.intValue()
            int r1 = r10.id
            if (r0 >= r1) goto L_0x00f7
            r0 = 1
            goto L_0x00f8
        L_0x00f7:
            r0 = 0
        L_0x00f8:
            r10.unread = r0
        L_0x00fa:
            if (r31 == 0) goto L_0x0104
            r10.out = r15
            r4 = 0
            r10.unread = r4
            r10.media_unread = r4
            goto L_0x0105
        L_0x0104:
            r4 = 0
        L_0x0105:
            int r0 = r10.id
            long r0 = r9.get(r0)
            r2 = 0
            int r18 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r18 == 0) goto L_0x01c2
            r3 = r32
            java.lang.Object r0 = r3.get(r0)
            r2 = r0
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC$Message) r2
            if (r2 != 0) goto L_0x011e
            goto L_0x01c2
        L_0x011e:
            int r0 = r13.indexOf(r2)
            r1 = -1
            if (r0 != r1) goto L_0x0127
            goto L_0x01c2
        L_0x0127:
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
            int r0 = r10.flags
            r20 = 33554432(0x2000000, float:9.403955E-38)
            r0 = r0 & r20
            if (r0 == 0) goto L_0x0157
            int r0 = r10.ttl_period
            r4.ttl_period = r0
            int r0 = r4.flags
            r0 = r0 | r20
            r4.flags = r0
        L_0x0157:
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
            if (r12 == 0) goto L_0x0197
            if (r19 != 0) goto L_0x0197
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda23 r8 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda23
            r0 = r8
            r1 = r26
            r2 = r22
            r4 = r15
            r5 = r35
            r6 = r29
            r0.<init>(r1, r2, r3, r4, r5, r6)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8)
            r15 = r7
            r19 = r9
            r13 = 0
            goto L_0x01bd
        L_0x0197:
            org.telegram.messenger.MessagesStorage r0 = r26.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r0.getStorageQueue()
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda52 r6 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda52
            r0 = r6
            r1 = r26
            r2 = r3
            r3 = r36
            r4 = r22
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
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10)
            r12.postRunnable(r14)
        L_0x01bd:
            r0 = r17
            r8 = r21
            goto L_0x01c8
        L_0x01c2:
            r15 = r7
            r19 = r9
            r13 = 0
            r0 = r17
        L_0x01c8:
            r1 = 1
        L_0x01c9:
            int r0 = r0 + r1
            r5 = r27
            r12 = r29
            r13 = r33
            r14 = r34
            r7 = r15
            r9 = r19
            r15 = 1
            goto L_0x0071
        L_0x01d8:
            r15 = r7
            r1 = 1
            r13 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r0 = r15.updates
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01ea
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            r0.processUpdates(r15, r13)
        L_0x01ea:
            org.telegram.messenger.StatsController r0 = r26.getStatsController()
            int r2 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r0.incrementSentItemsCount(r2, r1, r8)
            goto L_0x0202
        L_0x01f6:
            r1 = 1
            r13 = 0
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda59 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda59
            r3 = r37
            r2.<init>(r11, r0, r3)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x0202:
            r10 = 0
        L_0x0203:
            int r0 = r33.size()
            if (r10 >= r0) goto L_0x022c
            r0 = r33
            r2 = 0
            java.lang.Object r3 = r0.get(r10)
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            org.telegram.messenger.MessagesStorage r4 = r26.getMessagesStorage()
            r5 = r29
            if (r5 == 0) goto L_0x021c
            r6 = 1
            goto L_0x021d
        L_0x021c:
            r6 = 0
        L_0x021d:
            r4.markMessageAsSendError(r3, r6)
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda45 r4 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda45
            r4.<init>(r11, r3, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            int r10 = r10 + 1
            r13 = 0
            goto L_0x0203
        L_0x022c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendMessage$14(long, int, boolean, boolean, androidx.collection.LongSparseArray, java.util.ArrayList, java.util.ArrayList, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Peer, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMessage$9(int i, TLRPC$Message tLRPC$Message, ArrayList arrayList, MessageObject messageObject, int i2) {
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(Integer.valueOf(i));
        getMessagesController().deleteMessages(arrayList2, (ArrayList<Long>) null, (TLRPC$EncryptedChat) null, tLRPC$Message.dialog_id, false, true);
        getMessagesStorage().getStorageQueue().postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda34(this, arrayList, messageObject, tLRPC$Message, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMessage$8(ArrayList arrayList, MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, int i2) {
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda37(this, messageObject, tLRPC$Message, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMessage$7(MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, int i2) {
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
    public /* synthetic */ void lambda$sendMessage$11(TLRPC$Message tLRPC$Message, TLRPC$Peer tLRPC$Peer, int i, int i2, ArrayList arrayList, long j, TLRPC$Message tLRPC$Message2, int i3) {
        TLRPC$Message tLRPC$Message3 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message3.random_id, MessageObject.getPeerId(tLRPC$Peer), Integer.valueOf(i), tLRPC$Message3.id, 0, false, i2 != 0 ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, i2 != 0);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda51(this, tLRPC$Message, j, i, tLRPC$Message2, i3, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMessage$10(TLRPC$Message tLRPC$Message, long j, int i, TLRPC$Message tLRPC$Message2, int i2, int i3) {
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
    public /* synthetic */ void lambda$sendMessage$12(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_messages_forwardMessages tLRPC$TL_messages_forwardMessages) {
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, (BaseFragment) null, tLRPC$TL_messages_forwardMessages, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMessage$13(TLRPC$Message tLRPC$Message, int i) {
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

    /* JADX WARNING: type inference failed for: r32v0, types: [java.lang.Object] */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x025d A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0269 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x0271  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0281  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x004e A[SYNTHETIC, Splitter:B:19:0x004e] */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0486 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0492 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x04a4 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x04f0 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x04f5 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x0506 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00a7 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0146 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0163 A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x018a A[Catch:{ Exception -> 0x057d }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void editMessage(org.telegram.messenger.MessageObject r25, org.telegram.tgnet.TLRPC$TL_photo r26, org.telegram.messenger.VideoEditedInfo r27, org.telegram.tgnet.TLRPC$TL_document r28, java.lang.String r29, java.util.HashMap<java.lang.String, java.lang.String> r30, boolean r31, java.lang.Object r32) {
        /*
            r24 = this;
            r11 = r24
            r12 = r25
            r0 = r26
            r1 = r28
            r2 = r29
            java.lang.String r3 = "originalPath"
            java.lang.String r4 = "parentObject"
            if (r12 != 0) goto L_0x0011
            return
        L_0x0011:
            if (r30 != 0) goto L_0x0019
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            goto L_0x001b
        L_0x0019:
            r5 = r30
        L_0x001b:
            org.telegram.tgnet.TLRPC$Message r6 = r12.messageOwner
            r7 = 0
            r12.cancelEditing = r7
            long r8 = r25.getDialogId()     // Catch:{ Exception -> 0x057d }
            boolean r10 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)     // Catch:{ Exception -> 0x057d }
            r13 = 1
            if (r10 == 0) goto L_0x0049
            int r10 = org.telegram.messenger.DialogObject.getEncryptedChatId(r8)     // Catch:{ Exception -> 0x057d }
            org.telegram.messenger.MessagesController r14 = r24.getMessagesController()     // Catch:{ Exception -> 0x057d }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = r14.getEncryptedChat(r10)     // Catch:{ Exception -> 0x057d }
            if (r10 == 0) goto L_0x0047
            int r10 = r10.layer     // Catch:{ Exception -> 0x057d }
            int r10 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r10)     // Catch:{ Exception -> 0x057d }
            r14 = 101(0x65, float:1.42E-43)
            if (r10 >= r14) goto L_0x0049
        L_0x0047:
            r10 = 0
            goto L_0x004a
        L_0x0049:
            r10 = 1
        L_0x004a:
            java.lang.String r14 = "http"
            if (r31 == 0) goto L_0x00a7
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media     // Catch:{ Exception -> 0x057d }
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x057d }
            if (r5 != 0) goto L_0x0084
            if (r2 == 0) goto L_0x0084
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x057d }
            if (r5 == 0) goto L_0x005d
            goto L_0x0084
        L_0x005d:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x057d }
            if (r5 == 0) goto L_0x0069
            org.telegram.tgnet.TLRPC$Photo r0 = r2.photo     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0     // Catch:{ Exception -> 0x057d }
            r5 = r27
            r2 = 2
            goto L_0x0087
        L_0x0069:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x057d }
            if (r5 == 0) goto L_0x0080
            org.telegram.tgnet.TLRPC$Document r1 = r2.document     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC$TL_document) r1     // Catch:{ Exception -> 0x057d }
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1)     // Catch:{ Exception -> 0x057d }
            if (r2 != 0) goto L_0x007c
            if (r27 == 0) goto L_0x007a
            goto L_0x007c
        L_0x007a:
            r2 = 7
            goto L_0x007d
        L_0x007c:
            r2 = 3
        L_0x007d:
            org.telegram.messenger.VideoEditedInfo r5 = r12.videoEditedInfo     // Catch:{ Exception -> 0x057d }
            goto L_0x0087
        L_0x0080:
            r5 = r27
            r2 = -1
            goto L_0x0087
        L_0x0084:
            r5 = r27
            r2 = 1
        L_0x0087:
            java.util.HashMap<java.lang.String, java.lang.String> r15 = r6.params     // Catch:{ Exception -> 0x057d }
            if (r32 != 0) goto L_0x0098
            if (r15 == 0) goto L_0x0098
            boolean r16 = r15.containsKey(r4)     // Catch:{ Exception -> 0x057d }
            if (r16 == 0) goto L_0x0098
            java.lang.Object r4 = r15.get(r4)     // Catch:{ Exception -> 0x057d }
            goto L_0x009a
        L_0x0098:
            r4 = r32
        L_0x009a:
            java.lang.String r7 = r6.message     // Catch:{ Exception -> 0x057d }
            r12.editingMessage = r7     // Catch:{ Exception -> 0x057d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r6.entities     // Catch:{ Exception -> 0x057d }
            r12.editingMessageEntities = r7     // Catch:{ Exception -> 0x057d }
            java.lang.String r7 = r6.attachPath     // Catch:{ Exception -> 0x057d }
            r13 = r4
            goto L_0x015f
        L_0x00a7:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r6.media     // Catch:{ Exception -> 0x057d }
            r12.previousMedia = r4     // Catch:{ Exception -> 0x057d }
            java.lang.String r7 = r6.message     // Catch:{ Exception -> 0x057d }
            r12.previousMessage = r7     // Catch:{ Exception -> 0x057d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r6.entities     // Catch:{ Exception -> 0x057d }
            r12.previousMessageEntities = r7     // Catch:{ Exception -> 0x057d }
            java.lang.String r7 = r6.attachPath     // Catch:{ Exception -> 0x057d }
            r12.previousAttachPath = r7     // Catch:{ Exception -> 0x057d }
            if (r4 != 0) goto L_0x00be
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x057d }
            r4.<init>()     // Catch:{ Exception -> 0x057d }
        L_0x00be:
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x057d }
            r4.<init>((boolean) r13)     // Catch:{ Exception -> 0x057d }
            r11.writePreviousMessageData(r6, r4)     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.SerializedData r7 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x057d }
            int r4 = r4.length()     // Catch:{ Exception -> 0x057d }
            r7.<init>((int) r4)     // Catch:{ Exception -> 0x057d }
            r11.writePreviousMessageData(r6, r7)     // Catch:{ Exception -> 0x057d }
            java.lang.String r4 = "prevMedia"
            byte[] r15 = r7.toByteArray()     // Catch:{ Exception -> 0x057d }
            r13 = 0
            java.lang.String r15 = android.util.Base64.encodeToString(r15, r13)     // Catch:{ Exception -> 0x057d }
            r5.put(r4, r15)     // Catch:{ Exception -> 0x057d }
            r7.cleanup()     // Catch:{ Exception -> 0x057d }
            if (r0 == 0) goto L_0x0127
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x057d }
            r4.<init>()     // Catch:{ Exception -> 0x057d }
            r6.media = r4     // Catch:{ Exception -> 0x057d }
            int r7 = r4.flags     // Catch:{ Exception -> 0x057d }
            r13 = 3
            r7 = r7 | r13
            r4.flags = r7     // Catch:{ Exception -> 0x057d }
            r4.photo = r0     // Catch:{ Exception -> 0x057d }
            if (r2 == 0) goto L_0x0105
            int r4 = r29.length()     // Catch:{ Exception -> 0x057d }
            if (r4 <= 0) goto L_0x0105
            boolean r4 = r2.startsWith(r14)     // Catch:{ Exception -> 0x057d }
            if (r4 == 0) goto L_0x0105
            r6.attachPath = r2     // Catch:{ Exception -> 0x057d }
            goto L_0x0125
        L_0x0105:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r0.sizes     // Catch:{ Exception -> 0x057d }
            int r7 = r4.size()     // Catch:{ Exception -> 0x057d }
            r13 = 1
            int r7 = r7 - r13
            java.lang.Object r4 = r4.get(r7)     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = (org.telegram.tgnet.TLRPC$PhotoSize) r4     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.location     // Catch:{ Exception -> 0x057d }
            int r7 = r11.currentAccount     // Catch:{ Exception -> 0x057d }
            org.telegram.messenger.FileLoader r7 = org.telegram.messenger.FileLoader.getInstance(r7)     // Catch:{ Exception -> 0x057d }
            java.io.File r4 = r7.getPathToAttach(r4, r13)     // Catch:{ Exception -> 0x057d }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x057d }
            r6.attachPath = r4     // Catch:{ Exception -> 0x057d }
        L_0x0125:
            r4 = 2
            goto L_0x0153
        L_0x0127:
            if (r1 == 0) goto L_0x0152
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x057d }
            r4.<init>()     // Catch:{ Exception -> 0x057d }
            r6.media = r4     // Catch:{ Exception -> 0x057d }
            int r7 = r4.flags     // Catch:{ Exception -> 0x057d }
            r13 = 3
            r7 = r7 | r13
            r4.flags = r7     // Catch:{ Exception -> 0x057d }
            r4.document = r1     // Catch:{ Exception -> 0x057d }
            boolean r4 = org.telegram.messenger.MessageObject.isVideoDocument(r28)     // Catch:{ Exception -> 0x057d }
            if (r4 != 0) goto L_0x0143
            if (r27 == 0) goto L_0x0141
            goto L_0x0143
        L_0x0141:
            r4 = 7
            goto L_0x0144
        L_0x0143:
            r4 = 3
        L_0x0144:
            if (r27 == 0) goto L_0x014f
            java.lang.String r7 = r27.getString()     // Catch:{ Exception -> 0x057d }
            java.lang.String r13 = "ve"
            r5.put(r13, r7)     // Catch:{ Exception -> 0x057d }
        L_0x014f:
            r6.attachPath = r2     // Catch:{ Exception -> 0x057d }
            goto L_0x0153
        L_0x0152:
            r4 = 1
        L_0x0153:
            r6.params = r5     // Catch:{ Exception -> 0x057d }
            r7 = 3
            r6.send_state = r7     // Catch:{ Exception -> 0x057d }
            r13 = r32
            r7 = r2
            r2 = r4
            r15 = r5
            r5 = r27
        L_0x015f:
            java.lang.String r4 = r6.attachPath     // Catch:{ Exception -> 0x057d }
            if (r4 != 0) goto L_0x0167
            java.lang.String r4 = ""
            r6.attachPath = r4     // Catch:{ Exception -> 0x057d }
        L_0x0167:
            r4 = 0
            r6.local_id = r4     // Catch:{ Exception -> 0x057d }
            int r4 = r12.type     // Catch:{ Exception -> 0x057d }
            r26 = r1
            r1 = 3
            if (r4 == r1) goto L_0x0176
            if (r5 != 0) goto L_0x0176
            r1 = 2
            if (r4 != r1) goto L_0x0181
        L_0x0176:
            java.lang.String r1 = r6.attachPath     // Catch:{ Exception -> 0x057d }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x057d }
            if (r1 != 0) goto L_0x0181
            r1 = 1
            r12.attachPathExists = r1     // Catch:{ Exception -> 0x057d }
        L_0x0181:
            org.telegram.messenger.VideoEditedInfo r1 = r12.videoEditedInfo     // Catch:{ Exception -> 0x057d }
            if (r1 == 0) goto L_0x0188
            if (r5 != 0) goto L_0x0188
            r5 = r1
        L_0x0188:
            if (r31 != 0) goto L_0x025d
            java.lang.CharSequence r4 = r12.editingMessage     // Catch:{ Exception -> 0x057d }
            if (r4 == 0) goto L_0x01f9
            java.lang.String r1 = r6.message     // Catch:{ Exception -> 0x057d }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x057d }
            r6.message = r4     // Catch:{ Exception -> 0x057d }
            r28 = r5
            r5 = 0
            r12.caption = r5     // Catch:{ Exception -> 0x057d }
            r5 = 1
            if (r2 != r5) goto L_0x01b8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r12.editingMessageEntities     // Catch:{ Exception -> 0x057d }
            if (r5 == 0) goto L_0x01ab
            r6.entities = r5     // Catch:{ Exception -> 0x057d }
            int r1 = r6.flags     // Catch:{ Exception -> 0x057d }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x057d }
            goto L_0x01fb
        L_0x01ab:
            boolean r1 = android.text.TextUtils.equals(r1, r4)     // Catch:{ Exception -> 0x057d }
            if (r1 != 0) goto L_0x01fb
            int r1 = r6.flags     // Catch:{ Exception -> 0x057d }
            r1 = r1 & -129(0xffffffffffffff7f, float:NaN)
            r6.flags = r1     // Catch:{ Exception -> 0x057d }
            goto L_0x01fb
        L_0x01b8:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r12.editingMessageEntities     // Catch:{ Exception -> 0x057d }
            if (r4 == 0) goto L_0x01c5
            r6.entities = r4     // Catch:{ Exception -> 0x057d }
            int r1 = r6.flags     // Catch:{ Exception -> 0x057d }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x057d }
            goto L_0x01f5
        L_0x01c5:
            r4 = 1
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r4]     // Catch:{ Exception -> 0x057d }
            java.lang.CharSequence r4 = r12.editingMessage     // Catch:{ Exception -> 0x057d }
            r16 = 0
            r5[r16] = r4     // Catch:{ Exception -> 0x057d }
            org.telegram.messenger.MediaDataController r4 = r24.getMediaDataController()     // Catch:{ Exception -> 0x057d }
            java.util.ArrayList r4 = r4.getEntities(r5, r10)     // Catch:{ Exception -> 0x057d }
            if (r4 == 0) goto L_0x01e7
            boolean r5 = r4.isEmpty()     // Catch:{ Exception -> 0x057d }
            if (r5 != 0) goto L_0x01e7
            r6.entities = r4     // Catch:{ Exception -> 0x057d }
            int r1 = r6.flags     // Catch:{ Exception -> 0x057d }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x057d }
            goto L_0x01f5
        L_0x01e7:
            java.lang.String r4 = r6.message     // Catch:{ Exception -> 0x057d }
            boolean r1 = android.text.TextUtils.equals(r1, r4)     // Catch:{ Exception -> 0x057d }
            if (r1 != 0) goto L_0x01f5
            int r1 = r6.flags     // Catch:{ Exception -> 0x057d }
            r1 = r1 & -129(0xffffffffffffff7f, float:NaN)
            r6.flags = r1     // Catch:{ Exception -> 0x057d }
        L_0x01f5:
            r25.generateCaption()     // Catch:{ Exception -> 0x057d }
            goto L_0x01fb
        L_0x01f9:
            r28 = r5
        L_0x01fb:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x057d }
            r1.<init>()     // Catch:{ Exception -> 0x057d }
            r1.add(r6)     // Catch:{ Exception -> 0x057d }
            org.telegram.messenger.MessagesStorage r17 = r24.getMessagesStorage()     // Catch:{ Exception -> 0x057d }
            r19 = 0
            r20 = 1
            r21 = 0
            r22 = 0
            boolean r4 = r12.scheduled     // Catch:{ Exception -> 0x057d }
            r18 = r1
            r23 = r4
            r17.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r18, (boolean) r19, (boolean) r20, (boolean) r21, (int) r22, (boolean) r23)     // Catch:{ Exception -> 0x057d }
            r1 = -1
            r12.type = r1     // Catch:{ Exception -> 0x057d }
            r25.setType()     // Catch:{ Exception -> 0x057d }
            r1 = 1
            if (r2 != r1) goto L_0x0238
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media     // Catch:{ Exception -> 0x057d }
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x057d }
            if (r4 != 0) goto L_0x0235
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x057d }
            if (r1 == 0) goto L_0x022e
            goto L_0x0235
        L_0x022e:
            r25.resetLayout()     // Catch:{ Exception -> 0x057d }
            r25.checkLayout()     // Catch:{ Exception -> 0x057d }
            goto L_0x0238
        L_0x0235:
            r25.generateCaption()     // Catch:{ Exception -> 0x057d }
        L_0x0238:
            r25.createMessageSendInfo()     // Catch:{ Exception -> 0x057d }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x057d }
            r1.<init>()     // Catch:{ Exception -> 0x057d }
            r1.add(r12)     // Catch:{ Exception -> 0x057d }
            org.telegram.messenger.NotificationCenter r4 = r24.getNotificationCenter()     // Catch:{ Exception -> 0x057d }
            int r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects     // Catch:{ Exception -> 0x057d }
            r17 = r10
            r6 = 2
            java.lang.Object[] r10 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x057d }
            java.lang.Long r6 = java.lang.Long.valueOf(r8)     // Catch:{ Exception -> 0x057d }
            r16 = 0
            r10[r16] = r6     // Catch:{ Exception -> 0x057d }
            r6 = 1
            r10[r6] = r1     // Catch:{ Exception -> 0x057d }
            r4.postNotificationName(r5, r10)     // Catch:{ Exception -> 0x057d }
            goto L_0x0261
        L_0x025d:
            r28 = r5
            r17 = r10
        L_0x0261:
            if (r15 == 0) goto L_0x0271
            boolean r1 = r15.containsKey(r3)     // Catch:{ Exception -> 0x057d }
            if (r1 == 0) goto L_0x0271
            java.lang.Object r1 = r15.get(r3)     // Catch:{ Exception -> 0x057d }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x057d }
            r4 = r1
            goto L_0x0272
        L_0x0271:
            r4 = 0
        L_0x0272:
            r1 = 8
            r3 = 1
            if (r2 < r3) goto L_0x027a
            r5 = 3
            if (r2 <= r5) goto L_0x027f
        L_0x027a:
            r5 = 5
            if (r2 < r5) goto L_0x0584
            if (r2 > r1) goto L_0x0584
        L_0x027f:
            if (r2 != r3) goto L_0x0285
            r32 = r15
            goto L_0x046c
        L_0x0285:
            java.lang.String r3 = "masks"
            r10 = 2
            if (r2 != r10) goto L_0x0335
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r10 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x057d }
            r10.<init>()     // Catch:{ Exception -> 0x057d }
            if (r15 == 0) goto L_0x02cf
            java.lang.Object r3 = r15.get(r3)     // Catch:{ Exception -> 0x057d }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x057d }
            if (r3 == 0) goto L_0x02cf
            org.telegram.tgnet.SerializedData r1 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x057d }
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x057d }
            r1.<init>((byte[]) r3)     // Catch:{ Exception -> 0x057d }
            r3 = 0
            int r5 = r1.readInt32(r3)     // Catch:{ Exception -> 0x057d }
            r6 = 0
        L_0x02a8:
            if (r6 >= r5) goto L_0x02c3
            r26 = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r10.stickers     // Catch:{ Exception -> 0x057d }
            r32 = r15
            int r15 = r1.readInt32(r3)     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$InputDocument r15 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r1, r15, r3)     // Catch:{ Exception -> 0x057d }
            r5.add(r15)     // Catch:{ Exception -> 0x057d }
            int r6 = r6 + 1
            r5 = r26
            r15 = r32
            r3 = 0
            goto L_0x02a8
        L_0x02c3:
            r32 = r15
            int r3 = r10.flags     // Catch:{ Exception -> 0x057d }
            r5 = 1
            r3 = r3 | r5
            r10.flags = r3     // Catch:{ Exception -> 0x057d }
            r1.cleanup()     // Catch:{ Exception -> 0x057d }
            goto L_0x02d1
        L_0x02cf:
            r32 = r15
        L_0x02d1:
            long r5 = r0.access_hash     // Catch:{ Exception -> 0x057d }
            r18 = 0
            int r1 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1))
            if (r1 != 0) goto L_0x02dc
            r5 = r10
            r1 = 1
            goto L_0x02fd
        L_0x02dc:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x057d }
            r1.<init>()     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r3 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x057d }
            r3.<init>()     // Catch:{ Exception -> 0x057d }
            r1.id = r3     // Catch:{ Exception -> 0x057d }
            long r5 = r0.id     // Catch:{ Exception -> 0x057d }
            r3.id = r5     // Catch:{ Exception -> 0x057d }
            long r5 = r0.access_hash     // Catch:{ Exception -> 0x057d }
            r3.access_hash = r5     // Catch:{ Exception -> 0x057d }
            byte[] r5 = r0.file_reference     // Catch:{ Exception -> 0x057d }
            r3.file_reference = r5     // Catch:{ Exception -> 0x057d }
            if (r5 != 0) goto L_0x02fb
            r5 = 0
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x057d }
            r3.file_reference = r6     // Catch:{ Exception -> 0x057d }
        L_0x02fb:
            r5 = r1
            r1 = 0
        L_0x02fd:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x057d }
            r3.<init>(r8)     // Catch:{ Exception -> 0x057d }
            r6 = 0
            r3.type = r6     // Catch:{ Exception -> 0x057d }
            r3.obj = r12     // Catch:{ Exception -> 0x057d }
            r3.originalPath = r4     // Catch:{ Exception -> 0x057d }
            r3.parentObject = r13     // Catch:{ Exception -> 0x057d }
            r3.inputUploadMedia = r10     // Catch:{ Exception -> 0x057d }
            r3.performMediaUpload = r1     // Catch:{ Exception -> 0x057d }
            if (r7 == 0) goto L_0x0320
            int r6 = r7.length()     // Catch:{ Exception -> 0x057d }
            if (r6 <= 0) goto L_0x0320
            boolean r6 = r7.startsWith(r14)     // Catch:{ Exception -> 0x057d }
            if (r6 == 0) goto L_0x0320
            r3.httpLocation = r7     // Catch:{ Exception -> 0x057d }
            goto L_0x0332
        L_0x0320:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes     // Catch:{ Exception -> 0x057d }
            int r7 = r6.size()     // Catch:{ Exception -> 0x057d }
            r10 = 1
            int r7 = r7 - r10
            java.lang.Object r6 = r6.get(r7)     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC$PhotoSize) r6     // Catch:{ Exception -> 0x057d }
            r3.photoSize = r6     // Catch:{ Exception -> 0x057d }
            r3.locationParent = r0     // Catch:{ Exception -> 0x057d }
        L_0x0332:
            r7 = r3
            goto L_0x046f
        L_0x0335:
            r32 = r15
            r0 = 3
            if (r2 != r0) goto L_0x0400
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x057d }
            r0.<init>()     // Catch:{ Exception -> 0x057d }
            r15 = r32
            if (r32 == 0) goto L_0x0376
            java.lang.Object r1 = r15.get(r3)     // Catch:{ Exception -> 0x057d }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x057d }
            if (r1 == 0) goto L_0x0376
            org.telegram.tgnet.SerializedData r3 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x057d }
            byte[] r1 = org.telegram.messenger.Utilities.hexToBytes(r1)     // Catch:{ Exception -> 0x057d }
            r3.<init>((byte[]) r1)     // Catch:{ Exception -> 0x057d }
            r1 = 0
            int r5 = r3.readInt32(r1)     // Catch:{ Exception -> 0x057d }
            r6 = 0
        L_0x035a:
            if (r6 >= r5) goto L_0x036d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r7 = r0.stickers     // Catch:{ Exception -> 0x057d }
            int r10 = r3.readInt32(r1)     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$InputDocument r10 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r3, r10, r1)     // Catch:{ Exception -> 0x057d }
            r7.add(r10)     // Catch:{ Exception -> 0x057d }
            int r6 = r6 + 1
            r1 = 0
            goto L_0x035a
        L_0x036d:
            int r1 = r0.flags     // Catch:{ Exception -> 0x057d }
            r5 = 1
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x057d }
            r3.cleanup()     // Catch:{ Exception -> 0x057d }
        L_0x0376:
            r1 = r26
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x057d }
            r0.mime_type = r3     // Catch:{ Exception -> 0x057d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x057d }
            r0.attributes = r3     // Catch:{ Exception -> 0x057d }
            boolean r3 = r25.isGif()     // Catch:{ Exception -> 0x057d }
            if (r3 != 0) goto L_0x039e
            if (r28 == 0) goto L_0x038f
            r5 = r28
            boolean r3 = r5.muted     // Catch:{ Exception -> 0x057d }
            if (r3 != 0) goto L_0x03a0
            goto L_0x0391
        L_0x038f:
            r5 = r28
        L_0x0391:
            r3 = 1
            r0.nosound_video = r3     // Catch:{ Exception -> 0x057d }
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x057d }
            if (r3 == 0) goto L_0x03a0
            java.lang.String r3 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x057d }
            goto L_0x03a0
        L_0x039e:
            r5 = r28
        L_0x03a0:
            long r6 = r1.access_hash     // Catch:{ Exception -> 0x057d }
            r18 = 0
            int r3 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1))
            if (r3 != 0) goto L_0x03ad
            r3 = r0
            r32 = r15
            r6 = 1
            goto L_0x03cf
        L_0x03ad:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x057d }
            r3.<init>()     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$TL_inputDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x057d }
            r6.<init>()     // Catch:{ Exception -> 0x057d }
            r3.id = r6     // Catch:{ Exception -> 0x057d }
            r32 = r15
            long r14 = r1.id     // Catch:{ Exception -> 0x057d }
            r6.id = r14     // Catch:{ Exception -> 0x057d }
            long r14 = r1.access_hash     // Catch:{ Exception -> 0x057d }
            r6.access_hash = r14     // Catch:{ Exception -> 0x057d }
            byte[] r7 = r1.file_reference     // Catch:{ Exception -> 0x057d }
            r6.file_reference = r7     // Catch:{ Exception -> 0x057d }
            if (r7 != 0) goto L_0x03ce
            r7 = 0
            byte[] r10 = new byte[r7]     // Catch:{ Exception -> 0x057d }
            r6.file_reference = r10     // Catch:{ Exception -> 0x057d }
        L_0x03ce:
            r6 = 0
        L_0x03cf:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r7 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x057d }
            r7.<init>(r8)     // Catch:{ Exception -> 0x057d }
            r10 = 1
            r7.type = r10     // Catch:{ Exception -> 0x057d }
            r7.obj = r12     // Catch:{ Exception -> 0x057d }
            r7.originalPath = r4     // Catch:{ Exception -> 0x057d }
            r7.parentObject = r13     // Catch:{ Exception -> 0x057d }
            r7.inputUploadMedia = r0     // Catch:{ Exception -> 0x057d }
            r7.performMediaUpload = r6     // Catch:{ Exception -> 0x057d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x057d }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x057d }
            if (r0 != 0) goto L_0x03fa
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x057d }
            r10 = 0
            java.lang.Object r0 = r0.get(r10)     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0     // Catch:{ Exception -> 0x057d }
            boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x057d }
            if (r10 != 0) goto L_0x03fa
            r7.photoSize = r0     // Catch:{ Exception -> 0x057d }
            r7.locationParent = r1     // Catch:{ Exception -> 0x057d }
        L_0x03fa:
            r7.videoEditedInfo = r5     // Catch:{ Exception -> 0x057d }
            r5 = r3
            r1 = r6
            goto L_0x046f
        L_0x0400:
            r1 = r26
            r0 = 7
            if (r2 != r0) goto L_0x046c
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x057d }
            r0.<init>()     // Catch:{ Exception -> 0x057d }
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x057d }
            r0.mime_type = r3     // Catch:{ Exception -> 0x057d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x057d }
            r0.attributes = r3     // Catch:{ Exception -> 0x057d }
            long r5 = r1.access_hash     // Catch:{ Exception -> 0x057d }
            r14 = 0
            int r3 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r3 != 0) goto L_0x041d
            r5 = r0
            r3 = 1
            goto L_0x043e
        L_0x041d:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x057d }
            r3.<init>()     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$TL_inputDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x057d }
            r5.<init>()     // Catch:{ Exception -> 0x057d }
            r3.id = r5     // Catch:{ Exception -> 0x057d }
            long r6 = r1.id     // Catch:{ Exception -> 0x057d }
            r5.id = r6     // Catch:{ Exception -> 0x057d }
            long r6 = r1.access_hash     // Catch:{ Exception -> 0x057d }
            r5.access_hash = r6     // Catch:{ Exception -> 0x057d }
            byte[] r6 = r1.file_reference     // Catch:{ Exception -> 0x057d }
            r5.file_reference = r6     // Catch:{ Exception -> 0x057d }
            if (r6 != 0) goto L_0x043c
            r6 = 0
            byte[] r7 = new byte[r6]     // Catch:{ Exception -> 0x057d }
            r5.file_reference = r7     // Catch:{ Exception -> 0x057d }
        L_0x043c:
            r5 = r3
            r3 = 0
        L_0x043e:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x057d }
            r6.<init>(r8)     // Catch:{ Exception -> 0x057d }
            r6.originalPath = r4     // Catch:{ Exception -> 0x057d }
            r7 = 2
            r6.type = r7     // Catch:{ Exception -> 0x057d }
            r6.obj = r12     // Catch:{ Exception -> 0x057d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x057d }
            boolean r7 = r7.isEmpty()     // Catch:{ Exception -> 0x057d }
            if (r7 != 0) goto L_0x0463
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x057d }
            r10 = 0
            java.lang.Object r7 = r7.get(r10)     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC$PhotoSize) r7     // Catch:{ Exception -> 0x057d }
            boolean r10 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x057d }
            if (r10 != 0) goto L_0x0463
            r6.photoSize = r7     // Catch:{ Exception -> 0x057d }
            r6.locationParent = r1     // Catch:{ Exception -> 0x057d }
        L_0x0463:
            r6.parentObject = r13     // Catch:{ Exception -> 0x057d }
            r6.inputUploadMedia = r0     // Catch:{ Exception -> 0x057d }
            r6.performMediaUpload = r3     // Catch:{ Exception -> 0x057d }
            r1 = r3
            r7 = r6
            goto L_0x046f
        L_0x046c:
            r1 = 0
            r5 = 0
            r7 = 0
        L_0x046f:
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r0 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage     // Catch:{ Exception -> 0x057d }
            r0.<init>()     // Catch:{ Exception -> 0x057d }
            int r3 = r25.getId()     // Catch:{ Exception -> 0x057d }
            r0.id = r3     // Catch:{ Exception -> 0x057d }
            org.telegram.messenger.MessagesController r3 = r24.getMessagesController()     // Catch:{ Exception -> 0x057d }
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((long) r8)     // Catch:{ Exception -> 0x057d }
            r0.peer = r3     // Catch:{ Exception -> 0x057d }
            if (r5 == 0) goto L_0x048e
            int r3 = r0.flags     // Catch:{ Exception -> 0x057d }
            r3 = r3 | 16384(0x4000, float:2.2959E-41)
            r0.flags = r3     // Catch:{ Exception -> 0x057d }
            r0.media = r5     // Catch:{ Exception -> 0x057d }
        L_0x048e:
            boolean r3 = r12.scheduled     // Catch:{ Exception -> 0x057d }
            if (r3 == 0) goto L_0x04a0
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner     // Catch:{ Exception -> 0x057d }
            int r3 = r3.date     // Catch:{ Exception -> 0x057d }
            r0.schedule_date = r3     // Catch:{ Exception -> 0x057d }
            int r3 = r0.flags     // Catch:{ Exception -> 0x057d }
            r5 = 32768(0x8000, float:4.5918E-41)
            r3 = r3 | r5
            r0.flags = r3     // Catch:{ Exception -> 0x057d }
        L_0x04a0:
            java.lang.CharSequence r3 = r12.editingMessage     // Catch:{ Exception -> 0x057d }
            if (r3 == 0) goto L_0x04ee
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x057d }
            r0.message = r3     // Catch:{ Exception -> 0x057d }
            int r3 = r0.flags     // Catch:{ Exception -> 0x057d }
            r3 = r3 | 2048(0x800, float:2.87E-42)
            r0.flags = r3     // Catch:{ Exception -> 0x057d }
            boolean r5 = r12.editingMessageSearchWebPage     // Catch:{ Exception -> 0x057d }
            if (r5 != 0) goto L_0x04b6
            r5 = 1
            goto L_0x04b7
        L_0x04b6:
            r5 = 0
        L_0x04b7:
            r0.no_webpage = r5     // Catch:{ Exception -> 0x057d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r12.editingMessageEntities     // Catch:{ Exception -> 0x057d }
            if (r5 == 0) goto L_0x04c6
            r0.entities = r5     // Catch:{ Exception -> 0x057d }
            r5 = 8
            r3 = r3 | r5
            r0.flags = r3     // Catch:{ Exception -> 0x057d }
        L_0x04c4:
            r3 = 0
            goto L_0x04ea
        L_0x04c6:
            r3 = 1
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r3]     // Catch:{ Exception -> 0x057d }
            java.lang.CharSequence r3 = r12.editingMessage     // Catch:{ Exception -> 0x057d }
            r6 = 0
            r5[r6] = r3     // Catch:{ Exception -> 0x057d }
            org.telegram.messenger.MediaDataController r3 = r24.getMediaDataController()     // Catch:{ Exception -> 0x057d }
            r6 = r17
            java.util.ArrayList r3 = r3.getEntities(r5, r6)     // Catch:{ Exception -> 0x057d }
            if (r3 == 0) goto L_0x04c4
            boolean r5 = r3.isEmpty()     // Catch:{ Exception -> 0x057d }
            if (r5 != 0) goto L_0x04c4
            r0.entities = r3     // Catch:{ Exception -> 0x057d }
            int r3 = r0.flags     // Catch:{ Exception -> 0x057d }
            r5 = 8
            r3 = r3 | r5
            r0.flags = r3     // Catch:{ Exception -> 0x057d }
            goto L_0x04c4
        L_0x04ea:
            r12.editingMessage = r3     // Catch:{ Exception -> 0x057d }
            r12.editingMessageEntities = r3     // Catch:{ Exception -> 0x057d }
        L_0x04ee:
            if (r7 == 0) goto L_0x04f2
            r7.sendRequest = r0     // Catch:{ Exception -> 0x057d }
        L_0x04f2:
            r3 = 1
            if (r2 != r3) goto L_0x0506
            r4 = 0
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x057d }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x0506:
            r3 = 2
            if (r2 != r3) goto L_0x0521
            if (r1 == 0) goto L_0x0510
            r11.performSendDelayedMessage(r7)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x0510:
            r5 = 0
            r6 = 1
            boolean r10 = r12.scheduled     // Catch:{ Exception -> 0x057d }
            r1 = r24
            r2 = r0
            r3 = r25
            r8 = r13
            r9 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x0521:
            r3 = 3
            if (r2 != r3) goto L_0x053a
            if (r1 == 0) goto L_0x052b
            r11.performSendDelayedMessage(r7)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x052b:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x057d }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x053a:
            r3 = 6
            if (r2 != r3) goto L_0x054c
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x057d }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x054c:
            r3 = 7
            if (r2 != r3) goto L_0x0564
            if (r1 == 0) goto L_0x0555
            r11.performSendDelayedMessage(r7)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x0555:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x057d }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x0564:
            r3 = 8
            if (r2 != r3) goto L_0x0584
            if (r1 == 0) goto L_0x056e
            r11.performSendDelayedMessage(r7)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x056e:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x057d }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x057d }
            goto L_0x0584
        L_0x057d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r24.revertEditingMessageObject(r25)
        L_0x0584:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessage(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, BaseFragment baseFragment, ArrayList<TLRPC$MessageEntity> arrayList, int i) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return 0;
        }
        TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage = new TLRPC$TL_messages_editMessage();
        tLRPC$TL_messages_editMessage.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
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
        return getConnectionsManager().sendRequest(tLRPC$TL_messages_editMessage, new SendMessagesHelper$$ExternalSyntheticLambda88(this, baseFragment, tLRPC$TL_messages_editMessage));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$editMessage$16(BaseFragment baseFragment, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        } else {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda60(this, tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editMessage));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$editMessage$15(TLRPC$TL_error tLRPC$TL_error, BaseFragment baseFragment, TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage) {
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda24(this, j, i, bArr));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendNotificationCallback$19(long j, int i, byte[] bArr) {
        TLRPC$Chat chatSync;
        TLRPC$User userSync;
        long j2 = j;
        int i2 = i;
        byte[] bArr2 = bArr;
        String str = j + "_" + i + "_" + Utilities.bytesToHex(bArr) + "_" + 0;
        this.waitingForCallback.put(str, Boolean.TRUE);
        if (!DialogObject.isUserDialog(j)) {
            long j3 = -j2;
            if (getMessagesController().getChat(Long.valueOf(j3)) == null && (chatSync = getMessagesStorage().getChatSync(j3)) != null) {
                getMessagesController().putChat(chatSync, true);
            }
        } else if (getMessagesController().getUser(Long.valueOf(j)) == null && (userSync = getMessagesStorage().getUserSync(j)) != null) {
            getMessagesController().putUser(userSync, true);
        }
        TLRPC$TL_messages_getBotCallbackAnswer tLRPC$TL_messages_getBotCallbackAnswer = new TLRPC$TL_messages_getBotCallbackAnswer();
        tLRPC$TL_messages_getBotCallbackAnswer.peer = getMessagesController().getInputPeer(j);
        tLRPC$TL_messages_getBotCallbackAnswer.msg_id = i2;
        tLRPC$TL_messages_getBotCallbackAnswer.game = false;
        if (bArr2 != null) {
            tLRPC$TL_messages_getBotCallbackAnswer.flags |= 1;
            tLRPC$TL_messages_getBotCallbackAnswer.data = bArr2;
        }
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getBotCallbackAnswer, new SendMessagesHelper$$ExternalSyntheticLambda81(this, str), 2);
        getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, 0, true, 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendNotificationCallback$17(String str) {
        this.waitingForCallback.remove(str);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendNotificationCallback$18(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda27(this, str));
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
        tLRPC$TL_messages_sendVote.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
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
        return getConnectionsManager().sendRequest(tLRPC$TL_messages_sendVote, new SendMessagesHelper$$ExternalSyntheticLambda84(this, messageObject, str, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendVote$21(MessageObject messageObject, String str, Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            this.voteSendTime.put(messageObject.getPollId(), 0L);
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            this.voteSendTime.put(messageObject.getPollId(), Long.valueOf(SystemClock.elapsedRealtime()));
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda29(this, str, runnable));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendVote$20(String str, Runnable runnable) {
        this.waitingForVote.remove(str);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: protected */
    public long getVoteSendTime(long j) {
        return this.voteSendTime.get(j, 0L).longValue();
    }

    public void sendReaction(MessageObject messageObject, CharSequence charSequence, boolean z, ChatActivity chatActivity, Runnable runnable) {
        if (messageObject != null && chatActivity != null) {
            TLRPC$TL_messages_sendReaction tLRPC$TL_messages_sendReaction = new TLRPC$TL_messages_sendReaction();
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (!tLRPC$Message.isThreadMessage || tLRPC$Message.fwd_from == null) {
                tLRPC$TL_messages_sendReaction.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
                tLRPC$TL_messages_sendReaction.msg_id = messageObject.getId();
            } else {
                tLRPC$TL_messages_sendReaction.peer = getMessagesController().getInputPeer(messageObject.getFromChatId());
                tLRPC$TL_messages_sendReaction.msg_id = messageObject.messageOwner.fwd_from.saved_from_msg_id;
            }
            if (charSequence != null) {
                tLRPC$TL_messages_sendReaction.reaction = charSequence.toString();
                tLRPC$TL_messages_sendReaction.flags |= 1;
            }
            if (z) {
                tLRPC$TL_messages_sendReaction.flags |= 2;
                tLRPC$TL_messages_sendReaction.big = true;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_sendReaction, new SendMessagesHelper$$ExternalSyntheticLambda80(this, runnable));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendReaction$22(Runnable runnable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
            }
        }
    }

    public void requestUrlAuth(String str, ChatActivity chatActivity, boolean z) {
        TLRPC$TL_messages_requestUrlAuth tLRPC$TL_messages_requestUrlAuth = new TLRPC$TL_messages_requestUrlAuth();
        tLRPC$TL_messages_requestUrlAuth.url = str;
        tLRPC$TL_messages_requestUrlAuth.flags |= 4;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_requestUrlAuth, new SendMessagesHelper$$ExternalSyntheticLambda90(chatActivity, tLRPC$TL_messages_requestUrlAuth, str, z), 2);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$requestUrlAuth$23(ChatActivity chatActivity, TLRPC$TL_messages_requestUrlAuth tLRPC$TL_messages_requestUrlAuth, String str, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject == null) {
            AlertsCreator.showOpenUrlAlert(chatActivity, str, false, z);
        } else if (tLObject instanceof TLRPC$TL_urlAuthResultRequest) {
            chatActivity.showRequestUrlAlert((TLRPC$TL_urlAuthResultRequest) tLObject, tLRPC$TL_messages_requestUrlAuth, str, z);
        } else if (tLObject instanceof TLRPC$TL_urlAuthResultAccepted) {
            AlertsCreator.showOpenUrlAlert(chatActivity, ((TLRPC$TL_urlAuthResultAccepted) tLObject).url, false, false);
        } else if (tLObject instanceof TLRPC$TL_urlAuthResultDefault) {
            AlertsCreator.showOpenUrlAlert(chatActivity, str, false, z);
        }
    }

    public void sendCallback(boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity) {
        lambda$sendCallback$24(z, messageObject, tLRPC$KeyboardButton, (TLRPC$InputCheckPasswordSRP) null, (TwoStepVerificationActivity) null, chatActivity);
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0088  */
    /* renamed from: sendCallback */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$sendCallback$24(boolean r19, org.telegram.messenger.MessageObject r20, org.telegram.tgnet.TLRPC$KeyboardButton r21, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP r22, org.telegram.ui.TwoStepVerificationActivity r23, org.telegram.ui.ChatActivity r24) {
        /*
            r18 = this;
            r0 = r20
            r12 = r21
            if (r0 == 0) goto L_0x01ba
            if (r12 == 0) goto L_0x01ba
            if (r24 != 0) goto L_0x000c
            goto L_0x01ba
        L_0x000c:
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r14 = 1
            r11 = 2
            if (r13 == 0) goto L_0x0016
            r1 = 3
        L_0x0013:
            r16 = 0
            goto L_0x0027
        L_0x0016:
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            if (r1 == 0) goto L_0x001c
            r1 = 1
            goto L_0x0013
        L_0x001c:
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r1 == 0) goto L_0x0024
            r16 = r19
            r1 = 2
            goto L_0x0027
        L_0x0024:
            r16 = r19
            r1 = 0
        L_0x0027:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            long r3 = r20.getDialogId()
            r2.append(r3)
            java.lang.String r3 = "_"
            r2.append(r3)
            int r4 = r20.getId()
            r2.append(r4)
            r2.append(r3)
            byte[] r4 = r12.data
            java.lang.String r4 = org.telegram.messenger.Utilities.bytesToHex(r4)
            r2.append(r4)
            r2.append(r3)
            r2.append(r1)
            java.lang.String r10 = r2.toString()
            r9 = r18
            java.util.HashMap<java.lang.String, java.lang.Boolean> r1 = r9.waitingForCallback
            java.lang.Boolean r2 = java.lang.Boolean.TRUE
            r1.put(r10, r2)
            org.telegram.tgnet.TLObject[] r8 = new org.telegram.tgnet.TLObject[r14]
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda82 r7 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda82
            r1 = r7
            r2 = r18
            r3 = r10
            r4 = r16
            r5 = r20
            r6 = r21
            r14 = r7
            r7 = r24
            r17 = r8
            r8 = r23
            r9 = r17
            r15 = r10
            r10 = r22
            r11 = r19
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            if (r16 == 0) goto L_0x0088
            org.telegram.messenger.MessagesStorage r0 = r18.getMessagesStorage()
            r0.getBotCache(r15, r14)
            goto L_0x01ba
        L_0x0088:
            if (r13 == 0) goto L_0x00b9
            org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth r1 = new org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth
            r1.<init>()
            org.telegram.messenger.MessagesController r2 = r18.getMessagesController()
            long r3 = r20.getDialogId()
            org.telegram.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer((long) r3)
            r1.peer = r2
            int r0 = r20.getId()
            r1.msg_id = r0
            int r0 = r12.button_id
            r1.button_id = r0
            int r0 = r1.flags
            r2 = 2
            r0 = r0 | r2
            r1.flags = r0
            r0 = 0
            r17[r0] = r1
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r1, r14, r2)
            goto L_0x01ba
        L_0x00b9:
            r2 = 2
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r1 == 0) goto L_0x0172
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            int r1 = r1.flags
            r1 = r1 & 4
            if (r1 != 0) goto L_0x014f
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm r1 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm
            r1.<init>()
            org.telegram.tgnet.TLRPC$TL_inputInvoiceMessage r3 = new org.telegram.tgnet.TLRPC$TL_inputInvoiceMessage
            r3.<init>()
            int r4 = r20.getId()
            r3.msg_id = r4
            org.telegram.messenger.MessagesController r4 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            org.telegram.tgnet.TLRPC$InputPeer r0 = r4.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r0)
            r3.peer = r0
            r1.invoice = r3
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0143 }
            r0.<init>()     // Catch:{ Exception -> 0x0143 }
            java.lang.String r3 = "bg_color"
            java.lang.String r4 = "windowBackgroundWhite"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x0143 }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x0143 }
            java.lang.String r3 = "text_color"
            java.lang.String r4 = "windowBackgroundWhiteBlackText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x0143 }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x0143 }
            java.lang.String r3 = "hint_color"
            java.lang.String r4 = "windowBackgroundWhiteHintText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x0143 }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x0143 }
            java.lang.String r3 = "link_color"
            java.lang.String r4 = "windowBackgroundWhiteLinkText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x0143 }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x0143 }
            java.lang.String r3 = "button_color"
            java.lang.String r4 = "featuredStickers_addButton"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x0143 }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x0143 }
            java.lang.String r3 = "button_text_color"
            java.lang.String r4 = "featuredStickers_buttonText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x0143 }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x0143 }
            org.telegram.tgnet.TLRPC$TL_dataJSON r3 = new org.telegram.tgnet.TLRPC$TL_dataJSON     // Catch:{ Exception -> 0x0143 }
            r3.<init>()     // Catch:{ Exception -> 0x0143 }
            r1.theme_params = r3     // Catch:{ Exception -> 0x0143 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0143 }
            r3.data = r0     // Catch:{ Exception -> 0x0143 }
            int r0 = r1.flags     // Catch:{ Exception -> 0x0143 }
            r3 = 1
            r0 = r0 | r3
            r1.flags = r0     // Catch:{ Exception -> 0x0143 }
            goto L_0x0147
        L_0x0143:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0147:
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r1, r14, r2)
            goto L_0x01ba
        L_0x014f:
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentReceipt r1 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentReceipt
            r1.<init>()
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            int r3 = r3.receipt_msg_id
            r1.msg_id = r3
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            org.telegram.tgnet.TLRPC$InputPeer r0 = r3.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r0)
            r1.peer = r0
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r1, r14, r2)
            goto L_0x01ba
        L_0x0172:
            org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer r1 = new org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer
            r1.<init>()
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            long r4 = r20.getDialogId()
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((long) r4)
            r1.peer = r3
            int r0 = r20.getId()
            r1.msg_id = r0
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            r1.game = r0
            boolean r0 = r12.requires_password
            if (r0 == 0) goto L_0x01a7
            if (r22 == 0) goto L_0x0198
            r0 = r22
            goto L_0x019d
        L_0x0198:
            org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty
            r0.<init>()
        L_0x019d:
            r1.password = r0
            r1.password = r0
            int r0 = r1.flags
            r0 = r0 | 4
            r1.flags = r0
        L_0x01a7:
            byte[] r0 = r12.data
            if (r0 == 0) goto L_0x01b3
            int r3 = r1.flags
            r4 = 1
            r3 = r3 | r4
            r1.flags = r3
            r1.data = r0
        L_0x01b3:
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r1, r14, r2)
        L_0x01ba:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendCallback$24(boolean, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP, org.telegram.ui.TwoStepVerificationActivity, org.telegram.ui.ChatActivity):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendCallback$30(String str, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, TwoStepVerificationActivity twoStepVerificationActivity, TLObject[] tLObjectArr, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, boolean z2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda31(this, str, z, tLObject, messageObject, tLRPC$KeyboardButton, chatActivity, twoStepVerificationActivity, tLObjectArr, tLRPC$TL_error, tLRPC$InputCheckPasswordSRP, z2));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0168, code lost:
        if (org.telegram.messenger.MessagesController.getNotificationsSettings(r7.currentAccount).getBoolean("askgame_" + r12, true) != false) goto L_0x016c;
     */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendCallback$29(java.lang.String r28, boolean r29, org.telegram.tgnet.TLObject r30, org.telegram.messenger.MessageObject r31, org.telegram.tgnet.TLRPC$KeyboardButton r32, org.telegram.ui.ChatActivity r33, org.telegram.ui.TwoStepVerificationActivity r34, org.telegram.tgnet.TLObject[] r35, org.telegram.tgnet.TLRPC$TL_error r36, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP r37, boolean r38) {
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
            goto L_0x049d
        L_0x001d:
            r8 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r9 = "OK"
            r10 = 0
            r11 = 1
            if (r1 == 0) goto L_0x0184
            if (r34 == 0) goto L_0x002e
            r34.needHideProgress()
            r34.finishFragment()
        L_0x002e:
            long r12 = r31.getFromChatId()
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
            long r14 = r2.via_bot_id
            r16 = 0
            int r2 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x003d
            r12 = r14
        L_0x003d:
            int r2 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x0058
            org.telegram.messenger.MessagesController r2 = r27.getMessagesController()
            java.lang.Long r14 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r14)
            if (r2 == 0) goto L_0x006a
            java.lang.String r14 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r14, r2)
            goto L_0x006b
        L_0x0058:
            org.telegram.messenger.MessagesController r2 = r27.getMessagesController()
            long r14 = -r12
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r14)
            if (r2 == 0) goto L_0x006a
            java.lang.String r2 = r2.title
            goto L_0x006b
        L_0x006a:
            r2 = r10
        L_0x006b:
            if (r2 != 0) goto L_0x006f
            java.lang.String r2 = "bot"
        L_0x006f:
            boolean r14 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            if (r14 == 0) goto L_0x00a1
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest
            if (r0 == 0) goto L_0x0085
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest r0 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest) r0
            r1 = r35[r3]
            org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth r1 = (org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth) r1
            java.lang.String r2 = r5.url
            r6.showRequestUrlAlert(r0, r1, r2, r3)
            goto L_0x049d
        L_0x0085:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted
            if (r0 == 0) goto L_0x0093
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted r0 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted) r0
            java.lang.String r0 = r0.url
            org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r6, r0, r3, r3)
            goto L_0x049d
        L_0x0093:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault
            if (r0 == 0) goto L_0x049d
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault r0 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault) r0
            java.lang.String r0 = r5.url
            org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r6, r0, r3, r11)
            goto L_0x049d
        L_0x00a1:
            boolean r14 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r14 == 0) goto L_0x00cf
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_payments_paymentForm
            if (r0 == 0) goto L_0x00bf
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = (org.telegram.tgnet.TLRPC$TL_payments_paymentForm) r0
            org.telegram.messenger.MessagesController r1 = r27.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r0.users
            r1.putUsers(r2, r3)
            org.telegram.ui.PaymentFormActivity r1 = new org.telegram.ui.PaymentFormActivity
            r1.<init>((org.telegram.tgnet.TLRPC$TL_payments_paymentForm) r0, (org.telegram.messenger.MessageObject) r4, (org.telegram.ui.ActionBar.BaseFragment) r6)
            r6.presentFragment(r1)
            goto L_0x049d
        L_0x00bf:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt
            if (r0 == 0) goto L_0x049d
            org.telegram.ui.PaymentFormActivity r0 = new org.telegram.ui.PaymentFormActivity
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r1 = (org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt) r1
            r0.<init>(r1)
            r6.presentFragment(r0)
            goto L_0x049d
        L_0x00cf:
            org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer r1 = (org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer) r1
            if (r29 != 0) goto L_0x00e2
            int r14 = r1.cache_time
            if (r14 == 0) goto L_0x00e2
            boolean r14 = r5.requires_password
            if (r14 != 0) goto L_0x00e2
            org.telegram.messenger.MessagesStorage r14 = r27.getMessagesStorage()
            r14.saveBotCache(r0, r1)
        L_0x00e2:
            java.lang.String r0 = r1.message
            if (r0 == 0) goto L_0x0117
            boolean r3 = r1.alert
            if (r3 == 0) goto L_0x0112
            android.app.Activity r0 = r33.getParentActivity()
            if (r0 != 0) goto L_0x00f1
            return
        L_0x00f1:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r33.getParentActivity()
            r0.<init>((android.content.Context) r3)
            r0.setTitle(r2)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r0.setPositiveButton(r2, r10)
            java.lang.String r1 = r1.message
            r0.setMessage(r1)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r6.showDialog(r0)
            goto L_0x049d
        L_0x0112:
            r6.showAlert(r2, r0)
            goto L_0x049d
        L_0x0117:
            java.lang.String r0 = r1.url
            if (r0 == 0) goto L_0x049d
            android.app.Activity r0 = r33.getParentActivity()
            if (r0 != 0) goto L_0x0122
            return
        L_0x0122:
            org.telegram.messenger.MessagesController r0 = r27.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 == 0) goto L_0x0136
            boolean r0 = r0.verified
            if (r0 == 0) goto L_0x0136
            r0 = 1
            goto L_0x0137
        L_0x0136:
            r0 = 0
        L_0x0137:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            if (r2 == 0) goto L_0x017d
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r5 == 0) goto L_0x0146
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            r10 = r2
        L_0x0146:
            if (r10 != 0) goto L_0x0149
            return
        L_0x0149:
            java.lang.String r1 = r1.url
            if (r0 != 0) goto L_0x016b
            int r0 = r7.currentAccount
            android.content.SharedPreferences r0 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "askgame_"
            r2.append(r5)
            r2.append(r12)
            java.lang.String r2 = r2.toString()
            boolean r0 = r0.getBoolean(r2, r11)
            if (r0 == 0) goto L_0x016b
            goto L_0x016c
        L_0x016b:
            r11 = 0
        L_0x016c:
            r32 = r33
            r33 = r10
            r34 = r31
            r35 = r1
            r36 = r11
            r37 = r12
            r32.showOpenGameAlert(r33, r34, r35, r36, r37)
            goto L_0x049d
        L_0x017d:
            java.lang.String r0 = r1.url
            org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r6, r0, r3, r3)
            goto L_0x049d
        L_0x0184:
            if (r2 == 0) goto L_0x049d
            android.app.Activity r0 = r33.getParentActivity()
            if (r0 != 0) goto L_0x018d
            return
        L_0x018d:
            java.lang.String r0 = r2.text
            java.lang.String r1 = "PASSWORD_HASH_INVALID"
            boolean r0 = r1.equals(r0)
            r12 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r13 = "Cancel"
            if (r0 == 0) goto L_0x01f1
            if (r37 != 0) goto L_0x049d
            org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r33.getParentActivity()
            r8.<init>((android.content.Context) r0)
            r0 = 2131624721(0x7f0e0311, float:1.887663E38)
            java.lang.String r1 = "BotOwnershipTransfer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r8.setTitle(r0)
            r0 = 2131624724(0x7f0e0314, float:1.8876636E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = "BotOwnershipTransferReadyAlertText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setMessage(r0)
            r0 = 2131624723(0x7f0e0313, float:1.8876634E38)
            java.lang.String r1 = "BotOwnershipTransferChangeOwner"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda0 r11 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda0
            r0 = r11
            r1 = r27
            r2 = r38
            r3 = r31
            r4 = r32
            r5 = r33
            r0.<init>(r1, r2, r3, r4, r5)
            r8.setPositiveButton(r9, r11)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r8.setNegativeButton(r0, r10)
            org.telegram.ui.ActionBar.AlertDialog r0 = r8.create()
            r6.showDialog(r0)
            goto L_0x049d
        L_0x01f1:
            java.lang.String r0 = r2.text
            java.lang.String r1 = "PASSWORD_MISSING"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0248
            java.lang.String r0 = r2.text
            java.lang.String r14 = "PASSWORD_TOO_FRESH_"
            boolean r0 = r0.startsWith(r14)
            if (r0 != 0) goto L_0x0248
            java.lang.String r0 = r2.text
            java.lang.String r14 = "SESSION_TOO_FRESH_"
            boolean r0 = r0.startsWith(r14)
            if (r0 == 0) goto L_0x0210
            goto L_0x0248
        L_0x0210:
            java.lang.String r0 = r2.text
            java.lang.String r1 = "SRP_ID_INVALID"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x023e
            org.telegram.tgnet.TLRPC$TL_account_getPassword r8 = new org.telegram.tgnet.TLRPC$TL_account_getPassword
            r8.<init>()
            int r0 = r7.currentAccount
            org.telegram.tgnet.ConnectionsManager r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda89 r10 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda89
            r0 = r10
            r1 = r27
            r2 = r34
            r3 = r38
            r4 = r31
            r5 = r32
            r6 = r33
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r0 = 8
            r9.sendRequest(r8, r10, r0)
            goto L_0x049d
        L_0x023e:
            if (r34 == 0) goto L_0x049d
            r34.needHideProgress()
            r34.finishFragment()
            goto L_0x049d
        L_0x0248:
            if (r34 == 0) goto L_0x024d
            r34.needHideProgress()
        L_0x024d:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r33.getParentActivity()
            r0.<init>((android.content.Context) r4)
            r4 = 2131625563(0x7f0e065b, float:1.8878337E38)
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
            if (r16 == 0) goto L_0x02a4
            r16 = 5
            goto L_0x02a6
        L_0x02a4:
            r16 = 3
        L_0x02a6:
            r10 = r16 | 48
            r5.setGravity(r10)
            r10 = 2131624722(0x7f0e0312, float:1.8876632E38)
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
            r8 = 2131165573(0x7var_, float:1.7945367E38)
            r12.setImageResource(r8)
            boolean r16 = org.telegram.messenger.LocaleController.isRTL
            r18 = 1093664768(0x41300000, float:11.0)
            if (r16 == 0) goto L_0x0301
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8 = r16
            goto L_0x0302
        L_0x0301:
            r8 = 0
        L_0x0302:
            r16 = 1091567616(0x41100000, float:9.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r19 = org.telegram.messenger.LocaleController.isRTL
            if (r19 == 0) goto L_0x030e
            r11 = 0
            goto L_0x0314
        L_0x030e:
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11 = r19
        L_0x0314:
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
            if (r10 == 0) goto L_0x033f
            r10 = 5
            goto L_0x0340
        L_0x033f:
            r10 = 3
        L_0x0340:
            r10 = r10 | 48
            r8.setGravity(r10)
            r10 = 2131625560(0x7f0e0658, float:1.8878331E38)
            java.lang.String r11 = "EditAdminTransferAlertText1"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.replaceTags(r10)
            r8.setText(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x036b
            r10 = -1
            r11 = -2
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r8, r15)
            r8 = 5
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r8)
            r5.addView(r12, r15)
            goto L_0x037b
        L_0x036b:
            r10 = -1
            r11 = -2
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
            r5.addView(r12, r15)
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r8, r12)
        L_0x037b:
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
            r10 = 2131165573(0x7var_, float:1.7945367E38)
            r8.setImageResource(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x03b2
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            goto L_0x03b3
        L_0x03b2:
            r10 = 0
        L_0x03b3:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x03bd
            r12 = 0
            goto L_0x03c1
        L_0x03bd:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r18)
        L_0x03c1:
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
            if (r10 == 0) goto L_0x03ee
            r10 = 5
            goto L_0x03ef
        L_0x03ee:
            r10 = 3
        L_0x03ef:
            r10 = r10 | 48
            r3.setGravity(r10)
            r10 = 2131625561(0x7f0e0659, float:1.8878333E38)
            java.lang.String r11 = "EditAdminTransferAlertText2"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.replaceTags(r10)
            r3.setText(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x041a
            r10 = -1
            r11 = -2
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r3, r10)
            r12 = 5
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r12)
            r5.addView(r8, r3)
            goto L_0x042b
        L_0x041a:
            r10 = -1
            r11 = -2
            r12 = 5
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
            r5.addView(r8, r15)
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r3, r8)
        L_0x042b:
            java.lang.String r2 = r2.text
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0450
            r1 = 2131625568(0x7f0e0660, float:1.8878348E38)
            java.lang.String r2 = "EditAdminTransferSetPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda1
            r2.<init>(r6)
            r0.setPositiveButton(r1, r2)
            r1 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r13, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            goto L_0x0496
        L_0x0450:
            android.widget.TextView r1 = new android.widget.TextView
            android.app.Activity r2 = r33.getParentActivity()
            r1.<init>(r2)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r1.setTextColor(r2)
            r2 = 1098907648(0x41800000, float:16.0)
            r3 = 1
            r1.setTextSize(r3, r2)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x046c
            r17 = 5
        L_0x046c:
            r2 = r17 | 48
            r1.setGravity(r2)
            r2 = 2131625562(0x7f0e065a, float:1.8878335E38)
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
            r1 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
        L_0x0496:
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r6.showDialog(r0)
        L_0x049d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendCallback$29(java.lang.String, boolean, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.ui.ChatActivity, org.telegram.ui.TwoStepVerificationActivity, org.telegram.tgnet.TLObject[], org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendCallback$25(boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
        twoStepVerificationActivity.setDelegate(new SendMessagesHelper$$ExternalSyntheticLambda91(this, z, messageObject, tLRPC$KeyboardButton, twoStepVerificationActivity, chatActivity));
        chatActivity.presentFragment(twoStepVerificationActivity);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendCallback$28(TwoStepVerificationActivity twoStepVerificationActivity, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda57(this, tLRPC$TL_error, tLObject, twoStepVerificationActivity, z, messageObject, tLRPC$KeyboardButton, chatActivity));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendCallback$27(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, TwoStepVerificationActivity twoStepVerificationActivity, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            twoStepVerificationActivity.setCurrentPasswordInfo((byte[]) null, tLRPC$TL_account_password);
            TwoStepVerificationActivity.initPasswordNewAlgo(tLRPC$TL_account_password);
            lambda$sendCallback$24(z, messageObject, tLRPC$KeyboardButton, twoStepVerificationActivity.getNewSrpPassword(), twoStepVerificationActivity, chatActivity);
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
            long sendAsPeerId = ChatObject.getSendAsPeerId(getMessagesController().getChat(Long.valueOf(tLRPC$InputPeer.chat_id)), getMessagesController().getChatFull(tLRPC$InputPeer.chat_id));
            if (sendAsPeerId != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                tLRPC$TL_messages_sendMedia.send_as = getMessagesController().getInputPeer(sendAsPeerId);
            }
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
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMedia, new SendMessagesHelper$$ExternalSyntheticLambda78(this, j2));
                }
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMedia, new SendMessagesHelper$$ExternalSyntheticLambda78(this, j2));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendGame$31(long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, (TLRPC$TL_messageMediaInvoice) null, dialogId, tLRPC$Message.attachPath, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, messageObject, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$Message.reply_markup, tLRPC$Message.params, !tLRPC$Message.silent, messageObject2.scheduled ? tLRPC$Message.date : 0, 0, (Object) null, (MessageObject.SendAnimationData) null);
    }

    public void sendMessage(TLRPC$User tLRPC$User, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$User, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, (TLRPC$TL_messageMediaInvoice) null, j, (String) null, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null, (MessageObject.SendAnimationData) null);
    }

    public void sendMessage(TLRPC$TL_messageMediaInvoice tLRPC$TL_messageMediaInvoice, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, tLRPC$TL_messageMediaInvoice, j, (String) null, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null, (MessageObject.SendAnimationData) null);
    }

    public void sendMessage(TLRPC$TL_document tLRPC$TL_document, VideoEditedInfo videoEditedInfo, String str, long j, MessageObject messageObject, MessageObject messageObject2, String str2, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj, MessageObject.SendAnimationData sendAnimationData) {
        String str3 = str2;
        sendMessage((String) null, str3, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, videoEditedInfo, (TLRPC$User) null, tLRPC$TL_document, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, (TLRPC$TL_messageMediaInvoice) null, j, str, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, arrayList, tLRPC$ReplyMarkup, hashMap, z, i, i2, obj, sendAnimationData);
    }

    public void sendMessage(String str, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$WebPage tLRPC$WebPage, boolean z, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z2, int i, MessageObject.SendAnimationData sendAnimationData) {
        sendMessage(str, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, (TLRPC$TL_messageMediaInvoice) null, j, (String) null, messageObject, messageObject2, tLRPC$WebPage, z, (MessageObject) null, arrayList, tLRPC$ReplyMarkup, hashMap, z2, i, 0, (Object) null, sendAnimationData);
    }

    public void sendMessage(TLRPC$MessageMedia tLRPC$MessageMedia, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, tLRPC$MessageMedia, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, (TLRPC$TL_messageMediaInvoice) null, j, (String) null, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null, (MessageObject.SendAnimationData) null);
    }

    public void sendMessage(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, tLRPC$TL_messageMediaPoll, (TLRPC$TL_messageMediaInvoice) null, j, (String) null, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null, (MessageObject.SendAnimationData) null);
    }

    public void sendMessage(TLRPC$TL_game tLRPC$TL_game, long j, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i) {
        sendMessage((String) null, (String) null, (TLRPC$MessageMedia) null, (TLRPC$TL_photo) null, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, tLRPC$TL_game, (TLRPC$TL_messageMediaPoll) null, (TLRPC$TL_messageMediaInvoice) null, j, (String) null, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (MessageObject) null, (ArrayList<TLRPC$MessageEntity>) null, tLRPC$ReplyMarkup, hashMap, z, i, 0, (Object) null, (MessageObject.SendAnimationData) null);
    }

    public void sendMessage(TLRPC$TL_photo tLRPC$TL_photo, String str, long j, MessageObject messageObject, MessageObject messageObject2, String str2, ArrayList<TLRPC$MessageEntity> arrayList, TLRPC$ReplyMarkup tLRPC$ReplyMarkup, HashMap<String, String> hashMap, boolean z, int i, int i2, Object obj) {
        String str3 = str2;
        sendMessage((String) null, str3, (TLRPC$MessageMedia) null, tLRPC$TL_photo, (VideoEditedInfo) null, (TLRPC$User) null, (TLRPC$TL_document) null, (TLRPC$TL_game) null, (TLRPC$TL_messageMediaPoll) null, (TLRPC$TL_messageMediaInvoice) null, j, str, messageObject, messageObject2, (TLRPC$WebPage) null, true, (MessageObject) null, arrayList, tLRPC$ReplyMarkup, hashMap, z, i, i2, obj, (MessageObject.SendAnimationData) null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v3, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v4, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v5, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v7, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v8, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v40, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v79, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v40, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v43, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v47, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v103, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v49, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v50, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v107, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v25, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v113, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v114, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v119, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v121, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v129, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v79, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v80, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v132, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v133, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v135, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v57, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v125, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v4, resolved type: org.telegram.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v61, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v257, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v61, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v62, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v259, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v260, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v262, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v157, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v158, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v159, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v160, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v161, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v265, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v67, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v8, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v71, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v9, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v267, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v13, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v268, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r36v14, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v72, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v200, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v201, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v202, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v204, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v205, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v206, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v207, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v208, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v209, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v281, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v211, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v212, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v213, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v216, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v283, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v83, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v284, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v285, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v84, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v235, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v87, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v249, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v253, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v89, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v190, resolved type: org.telegram.tgnet.TLRPC$TL_userRequest_old2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v256, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v261, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v206, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v207, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v208, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v278, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v279, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v280, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v281, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v282, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v283, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v284, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v266, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v268, resolved type: char} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v106, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v107, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX WARNING: type inference failed for: r18v1 */
    /* JADX WARNING: type inference failed for: r5v36, types: [org.telegram.tgnet.TLRPC$MessageMedia] */
    /* JADX WARNING: type inference failed for: r13v21 */
    /* JADX WARNING: type inference failed for: r13v29 */
    /* JADX WARNING: type inference failed for: r14v26 */
    /* JADX WARNING: type inference failed for: r14v29 */
    /* JADX WARNING: type inference failed for: r18v32 */
    /* JADX WARNING: type inference failed for: r18v39 */
    /* JADX WARNING: type inference failed for: r18v41 */
    /* JADX WARNING: type inference failed for: r18v43 */
    /* JADX WARNING: type inference failed for: r18v45 */
    /* JADX WARNING: type inference failed for: r14v92 */
    /* JADX WARNING: type inference failed for: r14v93 */
    /* JADX WARNING: type inference failed for: r14v95 */
    /* JADX WARNING: type inference failed for: r14v97 */
    /* JADX WARNING: type inference failed for: r2v265 */
    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0e67: MOVE  (r5v136 org.telegram.tgnet.TLRPC$TL_photo) = (r49v0 org.telegram.tgnet.TLRPC$TL_photo)
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
    /* JADX WARNING: Removed duplicated region for block: B:1012:0x1237  */
    /* JADX WARNING: Removed duplicated region for block: B:1159:0x1521  */
    /* JADX WARNING: Removed duplicated region for block: B:1218:0x168f A[Catch:{ Exception -> 0x1ad6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1224:0x16bb A[Catch:{ Exception -> 0x1ad6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1229:0x16f8 A[Catch:{ Exception -> 0x1ad6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1237:0x1726 A[Catch:{ Exception -> 0x1ad6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1238:0x1728 A[Catch:{ Exception -> 0x1ad6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1240:0x172d A[Catch:{ Exception -> 0x1ad6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1243:0x173b A[Catch:{ Exception -> 0x1ad6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02be A[Catch:{ Exception -> 0x032a }] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02c3 A[Catch:{ Exception -> 0x032a }] */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x02cb  */
    /* JADX WARNING: Removed duplicated region for block: B:1333:0x19a1  */
    /* JADX WARNING: Removed duplicated region for block: B:1337:0x19ab  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02cd  */
    /* JADX WARNING: Removed duplicated region for block: B:1344:0x19d7 A[SYNTHETIC, Splitter:B:1344:0x19d7] */
    /* JADX WARNING: Removed duplicated region for block: B:1346:0x1a0a A[Catch:{ Exception -> 0x1211 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1355:0x1a36 A[Catch:{ Exception -> 0x1211 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1356:0x1a38 A[Catch:{ Exception -> 0x1211 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1373:0x1a69 A[SYNTHETIC, Splitter:B:1373:0x1a69] */
    /* JADX WARNING: Removed duplicated region for block: B:1377:0x1a70 A[SYNTHETIC, Splitter:B:1377:0x1a70] */
    /* JADX WARNING: Removed duplicated region for block: B:1395:0x1ab7  */
    /* JADX WARNING: Removed duplicated region for block: B:1398:0x1abd A[SYNTHETIC, Splitter:B:1398:0x1abd] */
    /* JADX WARNING: Removed duplicated region for block: B:1410:0x1aeb A[Catch:{ Exception -> 0x1c0c }] */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0336  */
    /* JADX WARNING: Removed duplicated region for block: B:1441:0x1b71 A[Catch:{ Exception -> 0x1c0c }] */
    /* JADX WARNING: Removed duplicated region for block: B:1485:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1486:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1489:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1503:0x16ad A[EDGE_INSN: B:1503:0x16ad->B:1222:0x16ad ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x03d0 A[Catch:{ Exception -> 0x037d }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x03d8 A[Catch:{ Exception -> 0x037d }] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x03e9 A[Catch:{ Exception -> 0x037d }] */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x03ec A[Catch:{ Exception -> 0x037d }] */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x04a5 A[Catch:{ Exception -> 0x050d }] */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x04b9  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x066d A[SYNTHETIC, Splitter:B:359:0x066d] */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0690 A[Catch:{ Exception -> 0x0619 }] */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x06a1 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x06a5 A[Catch:{ Exception -> 0x0762 }] */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0779 A[SYNTHETIC, Splitter:B:418:0x0779] */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0791 A[Catch:{ Exception -> 0x0788 }] */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0794 A[Catch:{ Exception -> 0x0788 }] */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x079e A[Catch:{ Exception -> 0x0788 }] */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x07b1 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0788 }] */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x07d1 A[Catch:{ Exception -> 0x0788 }] */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x07d8 A[Catch:{ Exception -> 0x0788 }] */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0811  */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x083d A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x084a A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0854  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x011c A[SYNTHETIC, Splitter:B:46:0x011c] */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x088f  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0892  */
    /* JADX WARNING: Removed duplicated region for block: B:502:0x08f8 A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x0941 A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x094f A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x097b A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:534:0x09ab A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x09e8 A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x0a18 A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0135 A[Catch:{ Exception -> 0x032a }] */
    /* JADX WARNING: Removed duplicated region for block: B:581:0x0a65 A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0a6e A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x0a9b A[Catch:{ Exception -> 0x0836 }] */
    /* JADX WARNING: Removed duplicated region for block: B:600:0x0ae1  */
    /* JADX WARNING: Removed duplicated region for block: B:601:0x0ae3  */
    /* JADX WARNING: Removed duplicated region for block: B:605:0x0aec A[SYNTHETIC, Splitter:B:605:0x0aec] */
    /* JADX WARNING: Removed duplicated region for block: B:619:0x0b08  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0b18 A[SYNTHETIC, Splitter:B:627:0x0b18] */
    /* JADX WARNING: Removed duplicated region for block: B:648:0x0b73  */
    /* JADX WARNING: Removed duplicated region for block: B:671:0x0bc7  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0CLASSNAME A[Catch:{ Exception -> 0x0b9c }] */
    /* JADX WARNING: Removed duplicated region for block: B:679:0x0CLASSNAME A[Catch:{ Exception -> 0x0b9c }] */
    /* JADX WARNING: Removed duplicated region for block: B:720:0x0cf0 A[Catch:{ Exception -> 0x0b9c }] */
    /* JADX WARNING: Removed duplicated region for block: B:723:0x0d09 A[Catch:{ Exception -> 0x0b9c }] */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x0d17 A[Catch:{ Exception -> 0x0b9c }] */
    /* JADX WARNING: Removed duplicated region for block: B:732:0x0d3a A[Catch:{ Exception -> 0x0b9c }] */
    /* JADX WARNING: Removed duplicated region for block: B:733:0x0d3c A[Catch:{ Exception -> 0x0b9c }] */
    /* JADX WARNING: Removed duplicated region for block: B:765:0x0deb A[Catch:{ Exception -> 0x0b9c }] */
    /* JADX WARNING: Removed duplicated region for block: B:811:0x0eb2 A[Catch:{ Exception -> 0x0b9c }] */
    /* JADX WARNING: Removed duplicated region for block: B:813:0x0ec2  */
    /* JADX WARNING: Removed duplicated region for block: B:829:0x0f1a A[Catch:{ Exception -> 0x0f9a }] */
    /* JADX WARNING: Removed duplicated region for block: B:830:0x0f1d A[Catch:{ Exception -> 0x0f9a }] */
    /* JADX WARNING: Removed duplicated region for block: B:839:0x0var_ A[Catch:{ Exception -> 0x0f9a }] */
    /* JADX WARNING: Removed duplicated region for block: B:847:0x0var_ A[Catch:{ Exception -> 0x1000 }] */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x0var_ A[Catch:{ Exception -> 0x1000 }] */
    /* JADX WARNING: Removed duplicated region for block: B:927:0x10fe A[Catch:{ Exception -> 0x1154 }] */
    /* JADX WARNING: Removed duplicated region for block: B:977:0x11ab A[SYNTHETIC, Splitter:B:977:0x11ab] */
    /* JADX WARNING: Removed duplicated region for block: B:982:0x11b8 A[SYNTHETIC, Splitter:B:982:0x11b8] */
    /* JADX WARNING: Removed duplicated region for block: B:995:0x11f5 A[Catch:{ Exception -> 0x1211 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:194:0x0411=Splitter:B:194:0x0411, B:205:0x0439=Splitter:B:205:0x0439} */
    private void sendMessage(java.lang.String r56, java.lang.String r57, org.telegram.tgnet.TLRPC$MessageMedia r58, org.telegram.tgnet.TLRPC$TL_photo r59, org.telegram.messenger.VideoEditedInfo r60, org.telegram.tgnet.TLRPC$User r61, org.telegram.tgnet.TLRPC$TL_document r62, org.telegram.tgnet.TLRPC$TL_game r63, org.telegram.tgnet.TLRPC$TL_messageMediaPoll r64, org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r65, long r66, java.lang.String r68, org.telegram.messenger.MessageObject r69, org.telegram.messenger.MessageObject r70, org.telegram.tgnet.TLRPC$WebPage r71, boolean r72, org.telegram.messenger.MessageObject r73, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r74, org.telegram.tgnet.TLRPC$ReplyMarkup r75, java.util.HashMap<java.lang.String, java.lang.String> r76, boolean r77, int r78, int r79, java.lang.Object r80, org.telegram.messenger.MessageObject.SendAnimationData r81) {
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
            r10 = r66
            r12 = r68
            r13 = r69
            r14 = r70
            r15 = r71
            r14 = r73
            r13 = r74
            r6 = r76
            r9 = r78
            r7 = r79
            if (r5 == 0) goto L_0x002d
            java.lang.String r12 = r5.phone
            if (r12 != 0) goto L_0x002d
            return
        L_0x002d:
            r16 = 0
            int r12 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r12 != 0) goto L_0x0034
            return
        L_0x0034:
            java.lang.String r12 = ""
            if (r2 != 0) goto L_0x003d
            if (r57 != 0) goto L_0x003d
            r18 = r12
            goto L_0x003f
        L_0x003d:
            r18 = r57
        L_0x003f:
            if (r6 == 0) goto L_0x0052
            java.lang.String r5 = "originalPath"
            boolean r5 = r6.containsKey(r5)
            if (r5 == 0) goto L_0x0052
            java.lang.String r5 = "originalPath"
            java.lang.Object r5 = r6.get(r5)
            java.lang.String r5 = (java.lang.String) r5
            goto L_0x0053
        L_0x0052:
            r5 = 0
        L_0x0053:
            r19 = -1
            boolean r20 = org.telegram.messenger.DialogObject.isEncryptedDialog(r66)
            if (r20 != 0) goto L_0x0066
            r20 = r5
            org.telegram.messenger.MessagesController r5 = r55.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer((long) r10)
            goto L_0x0069
        L_0x0066:
            r20 = r5
            r5 = 0
        L_0x0069:
            org.telegram.messenger.UserConfig r21 = r55.getUserConfig()
            long r3 = r21.getClientUserId()
            boolean r21 = org.telegram.messenger.DialogObject.isEncryptedDialog(r66)
            r22 = r3
            if (r21 == 0) goto L_0x00c1
            org.telegram.messenger.MessagesController r3 = r55.getMessagesController()
            int r25 = org.telegram.messenger.DialogObject.getEncryptedChatId(r66)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r25)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
            if (r3 != 0) goto L_0x00bc
            if (r14 == 0) goto L_0x00bb
            org.telegram.messenger.MessagesStorage r2 = r55.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            boolean r4 = r14.scheduled
            r2.markMessageAsSendError(r3, r4)
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            r3 = 2
            r2.send_state = r3
            org.telegram.messenger.NotificationCenter r2 = r55.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.messageSendError
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r5 = r73.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 0
            r4[r6] = r5
            r2.postNotificationName(r3, r4)
            int r2 = r73.getId()
            r1.processSentMessage(r2)
        L_0x00bb:
            return
        L_0x00bc:
            r8 = r3
            r29 = r16
            r3 = 0
            goto L_0x010c
        L_0x00c1:
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r3 == 0) goto L_0x0108
            org.telegram.messenger.MessagesController r3 = r55.getMessagesController()
            long r7 = r5.channel_id
            java.lang.Long r4 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            org.telegram.messenger.MessagesController r4 = r55.getMessagesController()
            long r7 = r3.id
            org.telegram.tgnet.TLRPC$ChatFull r4 = r4.getChatFull(r7)
            boolean r7 = r3.megagroup
            if (r7 != 0) goto L_0x00e3
            r7 = 1
            goto L_0x00e4
        L_0x00e3:
            r7 = 0
        L_0x00e4:
            if (r7 == 0) goto L_0x00f3
            boolean r8 = r3.has_link
            if (r8 == 0) goto L_0x00f3
            if (r4 == 0) goto L_0x00f3
            r25 = r7
            long r7 = r4.linked_chat_id
            r27 = r7
            goto L_0x00f7
        L_0x00f3:
            r25 = r7
            r27 = r16
        L_0x00f7:
            org.telegram.messenger.MessagesController r7 = r55.getMessagesController()
            r8 = 1
            long r3 = org.telegram.messenger.ChatObject.getSendAsPeerId(r3, r4, r8)
            org.telegram.tgnet.TLRPC$Peer r3 = r7.getPeer(r3)
            r29 = r27
            r8 = 0
            goto L_0x010e
        L_0x0108:
            r29 = r16
            r3 = 0
            r8 = 0
        L_0x010c:
            r25 = 0
        L_0x010e:
            java.lang.String r7 = "fwd_id"
            java.lang.String r4 = "http"
            r27 = r3
            java.lang.String r3 = "parentObject"
            r31 = r5
            java.lang.String r5 = "query_id"
            if (r14 == 0) goto L_0x0336
            org.telegram.tgnet.TLRPC$Message r1 = r14.messageOwner     // Catch:{ Exception -> 0x0331 }
            if (r80 != 0) goto L_0x012d
            if (r6 == 0) goto L_0x012d
            boolean r27 = r6.containsKey(r3)     // Catch:{ Exception -> 0x032a }
            if (r27 == 0) goto L_0x012d
            java.lang.Object r27 = r6.get(r3)     // Catch:{ Exception -> 0x032a }
            goto L_0x012f
        L_0x012d:
            r27 = r80
        L_0x012f:
            boolean r33 = r73.isForwarded()     // Catch:{ Exception -> 0x032a }
            if (r33 != 0) goto L_0x02f6
            if (r6 == 0) goto L_0x013f
            boolean r33 = r6.containsKey(r7)     // Catch:{ Exception -> 0x032a }
            if (r33 == 0) goto L_0x013f
            goto L_0x02f6
        L_0x013f:
            boolean r33 = r73.isDice()     // Catch:{ Exception -> 0x032a }
            if (r33 == 0) goto L_0x0161
            java.lang.String r2 = r73.getDiceEmoji()     // Catch:{ Exception -> 0x032a }
            r13 = r64
            r33 = r3
            r35 = r4
            r34 = r7
            r18 = r12
            r36 = r18
            r19 = 11
            r3 = r58
            r4 = r59
            r7 = r61
        L_0x015d:
            r12 = r62
            goto L_0x02b6
        L_0x0161:
            r33 = r3
            int r3 = r14.type     // Catch:{ Exception -> 0x032a }
            if (r3 == 0) goto L_0x0299
            boolean r3 = r73.isAnimatedEmoji()     // Catch:{ Exception -> 0x032a }
            if (r3 == 0) goto L_0x016f
            goto L_0x0299
        L_0x016f:
            int r3 = r14.type     // Catch:{ Exception -> 0x032a }
            r34 = r7
            r7 = 4
            if (r3 != r7) goto L_0x0185
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x032a }
            r7 = r61
            r13 = r64
            r35 = r4
            r36 = r12
            r19 = 1
            r4 = r59
            goto L_0x015d
        L_0x0185:
            r7 = 1
            if (r3 != r7) goto L_0x01a7
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$Message r7 = r14.messageOwner     // Catch:{ Exception -> 0x032a }
            java.lang.String r7 = r7.message     // Catch:{ Exception -> 0x032a }
            if (r7 == 0) goto L_0x0196
            r18 = r7
        L_0x0196:
            r7 = r61
            r13 = r64
            r35 = r4
            r36 = r12
            r19 = 2
            r12 = r62
            r4 = r3
        L_0x01a3:
            r3 = r58
            goto L_0x02b6
        L_0x01a7:
            r7 = 3
            if (r3 == r7) goto L_0x0270
            r7 = 5
            if (r3 == r7) goto L_0x0270
            org.telegram.messenger.VideoEditedInfo r7 = r14.videoEditedInfo     // Catch:{ Exception -> 0x032a }
            if (r7 == 0) goto L_0x01b3
            goto L_0x0270
        L_0x01b3:
            r7 = 12
            if (r3 != r7) goto L_0x01f2
            org.telegram.tgnet.TLRPC$TL_userRequest_old2 r3 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2     // Catch:{ Exception -> 0x032a }
            r3.<init>()     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r1.media     // Catch:{ Exception -> 0x032a }
            r35 = r4
            java.lang.String r4 = r7.phone_number     // Catch:{ Exception -> 0x032a }
            r3.phone = r4     // Catch:{ Exception -> 0x032a }
            java.lang.String r4 = r7.first_name     // Catch:{ Exception -> 0x032a }
            r3.first_name = r4     // Catch:{ Exception -> 0x032a }
            java.lang.String r4 = r7.last_name     // Catch:{ Exception -> 0x032a }
            r3.last_name = r4     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r4 = new org.telegram.tgnet.TLRPC$TL_restrictionReason     // Catch:{ Exception -> 0x032a }
            r4.<init>()     // Catch:{ Exception -> 0x032a }
            r4.platform = r12     // Catch:{ Exception -> 0x032a }
            r4.reason = r12     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r1.media     // Catch:{ Exception -> 0x032a }
            java.lang.String r7 = r7.vcard     // Catch:{ Exception -> 0x032a }
            r4.text = r7     // Catch:{ Exception -> 0x032a }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r7 = r3.restriction_reason     // Catch:{ Exception -> 0x032a }
            r7.add(r4)     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media     // Catch:{ Exception -> 0x032a }
            r36 = r12
            long r12 = r4.user_id     // Catch:{ Exception -> 0x032a }
            r3.id = r12     // Catch:{ Exception -> 0x032a }
            r4 = r59
            r12 = r62
            r13 = r64
            r7 = r3
            r19 = 6
            goto L_0x01a3
        L_0x01f2:
            r35 = r4
            r36 = r12
            r4 = 8
            if (r3 == r4) goto L_0x024f
            r4 = 9
            if (r3 == r4) goto L_0x024f
            r4 = 13
            if (r3 == r4) goto L_0x024f
            r4 = 14
            if (r3 == r4) goto L_0x024f
            r4 = 15
            if (r3 != r4) goto L_0x020b
            goto L_0x024f
        L_0x020b:
            r4 = 2
            if (r3 != r4) goto L_0x0230
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x032a }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x032a }
            if (r4 == 0) goto L_0x0225
            r7 = r61
            r13 = r64
            r12 = r3
            r18 = r4
            r19 = 8
            goto L_0x0289
        L_0x0225:
            r4 = r59
            r7 = r61
            r13 = r64
            r12 = r3
            r19 = 8
            goto L_0x01a3
        L_0x0230:
            r4 = 17
            if (r3 != r4) goto L_0x0243
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3     // Catch:{ Exception -> 0x032a }
            r4 = r59
            r7 = r61
            r12 = r62
            r13 = r3
            r19 = 10
            goto L_0x01a3
        L_0x0243:
            r3 = r58
            r4 = r59
            r7 = r61
            r12 = r62
            r13 = r64
            goto L_0x02b6
        L_0x024f:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x032a }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x032a }
            if (r4 == 0) goto L_0x0265
            r7 = r61
            r13 = r64
            r12 = r3
            r18 = r4
            r19 = 7
            goto L_0x0289
        L_0x0265:
            r4 = r59
            r7 = r61
            r13 = r64
            r12 = r3
            r19 = 7
            goto L_0x01a3
        L_0x0270:
            r35 = r4
            r36 = r12
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x032a }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x032a }
            if (r4 == 0) goto L_0x028e
            r7 = r61
            r13 = r64
            r12 = r3
            r18 = r4
            r19 = 3
        L_0x0289:
            r3 = r58
            r4 = r59
            goto L_0x02b6
        L_0x028e:
            r4 = r59
            r7 = r61
            r13 = r64
            r12 = r3
            r19 = 3
            goto L_0x01a3
        L_0x0299:
            r35 = r4
            r34 = r7
            r36 = r12
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x032a }
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media     // Catch:{ Exception -> 0x032a }
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x032a }
            if (r3 == 0) goto L_0x02a8
            goto L_0x02aa
        L_0x02a8:
            java.lang.String r2 = r1.message     // Catch:{ Exception -> 0x032a }
        L_0x02aa:
            r3 = r58
            r4 = r59
            r7 = r61
            r12 = r62
            r13 = r64
            r19 = 0
        L_0x02b6:
            if (r6 == 0) goto L_0x02c3
            boolean r37 = r6.containsKey(r5)     // Catch:{ Exception -> 0x032a }
            if (r37 == 0) goto L_0x02c3
            r56 = r2
            r19 = 9
            goto L_0x02c5
        L_0x02c3:
            r56 = r2
        L_0x02c5:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x032a }
            int r2 = r2.ttl_seconds     // Catch:{ Exception -> 0x032a }
            if (r2 <= 0) goto L_0x02cd
            r14 = r2
            goto L_0x02cf
        L_0x02cd:
            r14 = r79
        L_0x02cf:
            r58 = r4
            r32 = r5
            r9 = r6
            r38 = r12
            r39 = r13
            r40 = r15
            r42 = r18
            r41 = r19
            r2 = r27
            r12 = r36
            r19 = 0
            r15 = r55
            r4 = r74
            r5 = r77
            r13 = r1
            r18 = r3
            r3 = r7
            r6 = r22
            r1 = r31
            r22 = 0
            goto L_0x080f
        L_0x02f6:
            r33 = r3
            r35 = r4
            r34 = r7
            r36 = r12
            r3 = r61
            r38 = r62
            r39 = r64
            r4 = r74
            r14 = r79
            r13 = r1
            r56 = r2
            r32 = r5
            r9 = r6
            r40 = r15
            r42 = r18
            r6 = r22
            r2 = r27
            r1 = r31
            r12 = r36
            r19 = 0
            r22 = 0
            r41 = 4
            r15 = r55
            r18 = r58
            r58 = r59
            r5 = r77
            goto L_0x080f
        L_0x032a:
            r0 = move-exception
            r13 = r1
            r5 = r9
        L_0x032d:
            r2 = 0
        L_0x032e:
            r1 = r0
            goto L_0x1CLASSNAME
        L_0x0331:
            r0 = move-exception
            r1 = r0
            r5 = r9
            goto L_0x1CLASSNAME
        L_0x0336:
            r33 = r3
            r35 = r4
            r34 = r7
            r36 = r12
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r66)     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r1 == 0) goto L_0x0356
            org.telegram.messenger.MessagesController r1 = r55.getMessagesController()     // Catch:{ Exception -> 0x0331 }
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x0331 }
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)     // Catch:{ Exception -> 0x0331 }
            boolean r1 = org.telegram.messenger.ChatObject.canSendStickers(r1)     // Catch:{ Exception -> 0x0331 }
            goto L_0x0357
        L_0x0356:
            r1 = 1
        L_0x0357:
            if (r2 == 0) goto L_0x03fc
            if (r8 == 0) goto L_0x0361
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0331 }
            r3.<init>()     // Catch:{ Exception -> 0x0331 }
            goto L_0x0366
        L_0x0361:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0331 }
            r3.<init>()     // Catch:{ Exception -> 0x0331 }
        L_0x0366:
            if (r8 == 0) goto L_0x0383
            boolean r4 = r15 instanceof org.telegram.tgnet.TLRPC$TL_webPagePending     // Catch:{ Exception -> 0x037d }
            if (r4 == 0) goto L_0x0383
            java.lang.String r4 = r15.url     // Catch:{ Exception -> 0x037d }
            if (r4 == 0) goto L_0x037b
            org.telegram.tgnet.TLRPC$TL_webPageUrlPending r4 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending     // Catch:{ Exception -> 0x037d }
            r4.<init>()     // Catch:{ Exception -> 0x037d }
            java.lang.String r7 = r15.url     // Catch:{ Exception -> 0x037d }
            r4.url = r7     // Catch:{ Exception -> 0x037d }
            r15 = r4
            goto L_0x0383
        L_0x037b:
            r15 = 0
            goto L_0x0383
        L_0x037d:
            r0 = move-exception
            r1 = r0
            r13 = r3
            r5 = r9
            goto L_0x083a
        L_0x0383:
            if (r1 == 0) goto L_0x03ca
            int r1 = r56.length()     // Catch:{ Exception -> 0x037d }
            r4 = 30
            if (r1 >= r4) goto L_0x03ca
            if (r15 != 0) goto L_0x03ca
            r4 = r74
            if (r4 == 0) goto L_0x0399
            boolean r1 = r74.isEmpty()     // Catch:{ Exception -> 0x037d }
            if (r1 == 0) goto L_0x03cc
        L_0x0399:
            org.telegram.messenger.MessagesController r1 = r55.getMessagesController()     // Catch:{ Exception -> 0x037d }
            java.util.HashSet<java.lang.String> r1 = r1.diceEmojies     // Catch:{ Exception -> 0x037d }
            java.lang.String r7 = "️"
            r12 = r36
            java.lang.String r7 = r2.replace(r7, r12)     // Catch:{ Exception -> 0x037d }
            boolean r1 = r1.contains(r7)     // Catch:{ Exception -> 0x037d }
            if (r1 == 0) goto L_0x03ce
            if (r8 != 0) goto L_0x03ce
            if (r9 != 0) goto L_0x03ce
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaDice     // Catch:{ Exception -> 0x037d }
            r1.<init>()     // Catch:{ Exception -> 0x037d }
            r1.emoticon = r2     // Catch:{ Exception -> 0x037d }
            r7 = -1
            r1.value = r7     // Catch:{ Exception -> 0x037d }
            r3.media = r1     // Catch:{ Exception -> 0x037d }
            r2 = 0
            r14 = 0
            r19 = 11
            r9 = r68
            r13 = r79
            r32 = r5
            r7 = r12
            r1 = r15
            goto L_0x0421
        L_0x03ca:
            r4 = r74
        L_0x03cc:
            r12 = r36
        L_0x03ce:
            if (r15 != 0) goto L_0x03d8
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x037d }
            r1.<init>()     // Catch:{ Exception -> 0x037d }
            r3.media = r1     // Catch:{ Exception -> 0x037d }
            goto L_0x03e1
        L_0x03d8:
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x037d }
            r1.<init>()     // Catch:{ Exception -> 0x037d }
            r3.media = r1     // Catch:{ Exception -> 0x037d }
            r1.webpage = r15     // Catch:{ Exception -> 0x037d }
        L_0x03e1:
            if (r6 == 0) goto L_0x03ec
            boolean r1 = r6.containsKey(r5)     // Catch:{ Exception -> 0x037d }
            if (r1 == 0) goto L_0x03ec
            r19 = 9
            goto L_0x03ee
        L_0x03ec:
            r19 = 0
        L_0x03ee:
            r3.message = r2     // Catch:{ Exception -> 0x037d }
            r2 = 0
            r14 = 0
            r9 = r68
            r13 = r79
            r32 = r5
            r1 = r15
            r7 = r18
            goto L_0x0421
        L_0x03fc:
            r3 = r64
            r4 = r74
            r12 = r36
            if (r3 == 0) goto L_0x0426
            if (r8 == 0) goto L_0x040c
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0331 }
            r1.<init>()     // Catch:{ Exception -> 0x0331 }
            goto L_0x0411
        L_0x040c:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0331 }
            r1.<init>()     // Catch:{ Exception -> 0x0331 }
        L_0x0411:
            r1.media = r3     // Catch:{ Exception -> 0x032a }
            r2 = 0
            r9 = r68
            r13 = r79
            r3 = r1
            r32 = r5
            r1 = r15
            r7 = r18
            r14 = 0
            r19 = 10
        L_0x0421:
            r5 = 5
        L_0x0422:
            r15 = r55
            goto L_0x0777
        L_0x0426:
            r7 = r58
            r2 = r22
            if (r7 == 0) goto L_0x0459
            if (r8 == 0) goto L_0x0434
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0331 }
            r1.<init>()     // Catch:{ Exception -> 0x0331 }
            goto L_0x0439
        L_0x0434:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0331 }
            r1.<init>()     // Catch:{ Exception -> 0x0331 }
        L_0x0439:
            r1.media = r7     // Catch:{ Exception -> 0x032a }
            if (r6 == 0) goto L_0x0447
            boolean r13 = r6.containsKey(r5)     // Catch:{ Exception -> 0x032a }
            if (r13 == 0) goto L_0x0447
            r14 = 0
            r19 = 9
            goto L_0x044a
        L_0x0447:
            r14 = 0
            r19 = 1
        L_0x044a:
            r9 = r68
            r13 = r79
            r22 = r2
            r32 = r5
            r7 = r18
            r2 = 0
            r5 = 5
            r3 = r1
            r1 = r15
            goto L_0x0422
        L_0x0459:
            r13 = r59
            if (r13 == 0) goto L_0x04ec
            if (r8 == 0) goto L_0x0465
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0331 }
            r1.<init>()     // Catch:{ Exception -> 0x0331 }
            goto L_0x046a
        L_0x0465:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x04e8 }
            r1.<init>()     // Catch:{ Exception -> 0x04e8 }
        L_0x046a:
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x04e4 }
            r7.<init>()     // Catch:{ Exception -> 0x04e4 }
            r1.media = r7     // Catch:{ Exception -> 0x04e4 }
            int r9 = r7.flags     // Catch:{ Exception -> 0x04e4 }
            r19 = 3
            r9 = r9 | 3
            r7.flags = r9     // Catch:{ Exception -> 0x04e4 }
            if (r4 == 0) goto L_0x047d
            r1.entities = r4     // Catch:{ Exception -> 0x050d }
        L_0x047d:
            r15 = r79
            if (r15 == 0) goto L_0x048b
            r7.ttl_seconds = r15     // Catch:{ Exception -> 0x050d }
            r1.ttl = r15     // Catch:{ Exception -> 0x050d }
            r22 = 4
            r9 = r9 | 4
            r7.flags = r9     // Catch:{ Exception -> 0x050d }
        L_0x048b:
            r7.photo = r13     // Catch:{ Exception -> 0x04e4 }
            if (r6 == 0) goto L_0x049a
            boolean r7 = r6.containsKey(r5)     // Catch:{ Exception -> 0x050d }
            if (r7 == 0) goto L_0x049a
            r9 = r68
            r7 = 9
            goto L_0x049d
        L_0x049a:
            r9 = r68
            r7 = 2
        L_0x049d:
            if (r9 == 0) goto L_0x04b9
            int r22 = r68.length()     // Catch:{ Exception -> 0x050d }
            if (r22 <= 0) goto L_0x04b9
            r63 = r7
            r7 = r35
            boolean r22 = r9.startsWith(r7)     // Catch:{ Exception -> 0x050d }
            if (r22 == 0) goto L_0x04b6
            r1.attachPath = r9     // Catch:{ Exception -> 0x050d }
            r15 = r55
            r35 = r7
            goto L_0x04df
        L_0x04b6:
            r35 = r7
            goto L_0x04bb
        L_0x04b9:
            r63 = r7
        L_0x04bb:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r13.sizes     // Catch:{ Exception -> 0x04e4 }
            int r22 = r7.size()     // Catch:{ Exception -> 0x04e4 }
            r13 = 1
            int r15 = r22 + -1
            java.lang.Object r7 = r7.get(r15)     // Catch:{ Exception -> 0x04e4 }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC$PhotoSize) r7     // Catch:{ Exception -> 0x04e4 }
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.location     // Catch:{ Exception -> 0x04e4 }
            r15 = r55
            int r13 = r15.currentAccount     // Catch:{ Exception -> 0x050d }
            org.telegram.messenger.FileLoader r13 = org.telegram.messenger.FileLoader.getInstance(r13)     // Catch:{ Exception -> 0x050d }
            r14 = 1
            java.io.File r7 = r13.getPathToAttach(r7, r14)     // Catch:{ Exception -> 0x050d }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x050d }
            r1.attachPath = r7     // Catch:{ Exception -> 0x050d }
        L_0x04df:
            r19 = r63
            r13 = r79
            goto L_0x0539
        L_0x04e4:
            r0 = move-exception
            r15 = r55
            goto L_0x050e
        L_0x04e8:
            r0 = move-exception
            r15 = r55
            goto L_0x0514
        L_0x04ec:
            r15 = r55
            r7 = r63
            r9 = r68
            r13 = r79
            if (r7 == 0) goto L_0x0518
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0513 }
            r1.<init>()     // Catch:{ Exception -> 0x0513 }
            org.telegram.tgnet.TLRPC$TL_messageMediaGame r14 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x050d }
            r14.<init>()     // Catch:{ Exception -> 0x050d }
            r1.media = r14     // Catch:{ Exception -> 0x050d }
            r14.game = r7     // Catch:{ Exception -> 0x050d }
            if (r6 == 0) goto L_0x0539
            boolean r7 = r6.containsKey(r5)     // Catch:{ Exception -> 0x050d }
            if (r7 == 0) goto L_0x0539
            goto L_0x052d
        L_0x050d:
            r0 = move-exception
        L_0x050e:
            r5 = r78
            r13 = r1
            goto L_0x032d
        L_0x0513:
            r0 = move-exception
        L_0x0514:
            r5 = r78
            goto L_0x1CLASSNAME
        L_0x0518:
            r7 = r65
            r14 = r78
            if (r7 == 0) goto L_0x0551
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x054c }
            r1.<init>()     // Catch:{ Exception -> 0x054c }
            r1.media = r7     // Catch:{ Exception -> 0x0547 }
            if (r6 == 0) goto L_0x0539
            boolean r7 = r6.containsKey(r5)     // Catch:{ Exception -> 0x0547 }
            if (r7 == 0) goto L_0x0539
        L_0x052d:
            r22 = r2
            r32 = r5
            r7 = r18
            r2 = 0
            r5 = 5
            r14 = 0
            r19 = 9
            goto L_0x0542
        L_0x0539:
            r22 = r2
            r32 = r5
            r7 = r18
            r2 = 0
            r5 = 5
            r14 = 0
        L_0x0542:
            r3 = r1
        L_0x0543:
            r1 = r71
            goto L_0x0777
        L_0x0547:
            r0 = move-exception
            r13 = r1
            r5 = r14
            goto L_0x032d
        L_0x054c:
            r0 = move-exception
            r1 = r0
            r5 = r14
            goto L_0x1CLASSNAME
        L_0x0551:
            r7 = r61
            if (r7 == 0) goto L_0x05dc
            if (r8 == 0) goto L_0x055d
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x054c }
            r1.<init>()     // Catch:{ Exception -> 0x054c }
            goto L_0x0562
        L_0x055d:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0513 }
            r1.<init>()     // Catch:{ Exception -> 0x0513 }
        L_0x0562:
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r14 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact     // Catch:{ Exception -> 0x050d }
            r14.<init>()     // Catch:{ Exception -> 0x050d }
            r1.media = r14     // Catch:{ Exception -> 0x050d }
            r22 = r2
            java.lang.String r2 = r7.phone     // Catch:{ Exception -> 0x050d }
            r14.phone_number = r2     // Catch:{ Exception -> 0x050d }
            java.lang.String r2 = r7.first_name     // Catch:{ Exception -> 0x050d }
            r14.first_name = r2     // Catch:{ Exception -> 0x050d }
            java.lang.String r2 = r7.last_name     // Catch:{ Exception -> 0x050d }
            r14.last_name = r2     // Catch:{ Exception -> 0x050d }
            long r2 = r7.id     // Catch:{ Exception -> 0x050d }
            r14.user_id = r2     // Catch:{ Exception -> 0x050d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r7.restriction_reason     // Catch:{ Exception -> 0x050d }
            boolean r2 = r2.isEmpty()     // Catch:{ Exception -> 0x050d }
            if (r2 != 0) goto L_0x05a6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r7.restriction_reason     // Catch:{ Exception -> 0x050d }
            r3 = 0
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x050d }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r2 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r2     // Catch:{ Exception -> 0x050d }
            java.lang.String r2 = r2.text     // Catch:{ Exception -> 0x050d }
            java.lang.String r3 = "BEGIN:VCARD"
            boolean r2 = r2.startsWith(r3)     // Catch:{ Exception -> 0x050d }
            if (r2 == 0) goto L_0x05a6
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x050d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r3 = r7.restriction_reason     // Catch:{ Exception -> 0x050d }
            r14 = 0
            java.lang.Object r3 = r3.get(r14)     // Catch:{ Exception -> 0x050d }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r3 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r3     // Catch:{ Exception -> 0x050d }
            java.lang.String r3 = r3.text     // Catch:{ Exception -> 0x050d }
            r2.vcard = r3     // Catch:{ Exception -> 0x050d }
            goto L_0x05aa
        L_0x05a6:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x050d }
            r2.vcard = r12     // Catch:{ Exception -> 0x050d }
        L_0x05aa:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x050d }
            java.lang.String r3 = r2.first_name     // Catch:{ Exception -> 0x050d }
            if (r3 != 0) goto L_0x05b4
            r2.first_name = r12     // Catch:{ Exception -> 0x050d }
            r7.first_name = r12     // Catch:{ Exception -> 0x050d }
        L_0x05b4:
            java.lang.String r3 = r2.last_name     // Catch:{ Exception -> 0x050d }
            if (r3 != 0) goto L_0x05bc
            r2.last_name = r12     // Catch:{ Exception -> 0x050d }
            r7.last_name = r12     // Catch:{ Exception -> 0x050d }
        L_0x05bc:
            if (r6 == 0) goto L_0x05d0
            boolean r2 = r6.containsKey(r5)     // Catch:{ Exception -> 0x050d }
            if (r2 == 0) goto L_0x05d0
            r3 = r1
            r32 = r5
            r7 = r18
            r2 = 0
            r5 = 5
            r14 = 0
            r19 = 9
            goto L_0x0543
        L_0x05d0:
            r3 = r1
            r32 = r5
            r7 = r18
            r2 = 0
            r5 = 5
            r14 = 0
            r19 = 6
            goto L_0x0543
        L_0x05dc:
            r22 = r2
            r3 = r6
            r2 = r62
            if (r2 == 0) goto L_0x076c
            if (r8 == 0) goto L_0x05eb
            org.telegram.tgnet.TLRPC$TL_message_secret r6 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0513 }
            r6.<init>()     // Catch:{ Exception -> 0x0513 }
            goto L_0x05f0
        L_0x05eb:
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0513 }
            r6.<init>()     // Catch:{ Exception -> 0x0513 }
        L_0x05f0:
            boolean r14 = org.telegram.messenger.DialogObject.isChatDialog(r66)     // Catch:{ Exception -> 0x0762 }
            if (r14 == 0) goto L_0x0620
            if (r1 != 0) goto L_0x0620
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r2.attributes     // Catch:{ Exception -> 0x0619 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0619 }
            r14 = 0
        L_0x05ff:
            if (r14 >= r1) goto L_0x0620
            r63 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r2.attributes     // Catch:{ Exception -> 0x0619 }
            java.lang.Object r1 = r1.get(r14)     // Catch:{ Exception -> 0x0619 }
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x0619 }
            if (r1 == 0) goto L_0x0614
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r2.attributes     // Catch:{ Exception -> 0x0619 }
            r1.remove(r14)     // Catch:{ Exception -> 0x0619 }
            r1 = 1
            goto L_0x0621
        L_0x0614:
            int r14 = r14 + 1
            r1 = r63
            goto L_0x05ff
        L_0x0619:
            r0 = move-exception
            r5 = r78
            r1 = r0
            r13 = r6
            goto L_0x083a
        L_0x0620:
            r1 = 0
        L_0x0621:
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r14 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0762 }
            r14.<init>()     // Catch:{ Exception -> 0x0762 }
            r6.media = r14     // Catch:{ Exception -> 0x0762 }
            r63 = r1
            int r1 = r14.flags     // Catch:{ Exception -> 0x0762 }
            r7 = 3
            r1 = r1 | r7
            r14.flags = r1     // Catch:{ Exception -> 0x0762 }
            if (r13 == 0) goto L_0x063c
            r14.ttl_seconds = r13     // Catch:{ Exception -> 0x0619 }
            r6.ttl = r13     // Catch:{ Exception -> 0x0619 }
            r19 = 4
            r1 = r1 | 4
            r14.flags = r1     // Catch:{ Exception -> 0x0619 }
        L_0x063c:
            r14.document = r2     // Catch:{ Exception -> 0x0762 }
            if (r3 == 0) goto L_0x0649
            boolean r1 = r3.containsKey(r5)     // Catch:{ Exception -> 0x0619 }
            if (r1 == 0) goto L_0x0649
            r19 = 9
            goto L_0x066b
        L_0x0649:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoSticker(r62)     // Catch:{ Exception -> 0x0762 }
            if (r1 != 0) goto L_0x0660
            boolean r1 = org.telegram.messenger.MessageObject.isVideoDocument(r62)     // Catch:{ Exception -> 0x0619 }
            if (r1 != 0) goto L_0x065d
            boolean r1 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r62)     // Catch:{ Exception -> 0x0619 }
            if (r1 != 0) goto L_0x065d
            if (r60 == 0) goto L_0x0660
        L_0x065d:
            r19 = 3
            goto L_0x066b
        L_0x0660:
            boolean r1 = org.telegram.messenger.MessageObject.isVoiceDocument(r62)     // Catch:{ Exception -> 0x0762 }
            if (r1 == 0) goto L_0x0669
            r19 = 8
            goto L_0x066b
        L_0x0669:
            r19 = 7
        L_0x066b:
            if (r60 == 0) goto L_0x067d
            java.lang.String r1 = r60.getString()     // Catch:{ Exception -> 0x0619 }
            if (r3 != 0) goto L_0x0678
            java.util.HashMap r3 = new java.util.HashMap     // Catch:{ Exception -> 0x0619 }
            r3.<init>()     // Catch:{ Exception -> 0x0619 }
        L_0x0678:
            java.lang.String r14 = "ve"
            r3.put(r14, r1)     // Catch:{ Exception -> 0x0619 }
        L_0x067d:
            if (r8 == 0) goto L_0x06a1
            int r1 = r2.dc_id     // Catch:{ Exception -> 0x0619 }
            if (r1 <= 0) goto L_0x06a1
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r62)     // Catch:{ Exception -> 0x0619 }
            if (r1 != 0) goto L_0x06a1
            r1 = 1
            boolean r14 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r2, r1)     // Catch:{ Exception -> 0x0619 }
            if (r14 != 0) goto L_0x06a1
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0619 }
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x0619 }
            java.io.File r1 = r1.getPathToAttach(r2)     // Catch:{ Exception -> 0x0619 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0619 }
            r6.attachPath = r1     // Catch:{ Exception -> 0x0619 }
            goto L_0x06a3
        L_0x06a1:
            r6.attachPath = r9     // Catch:{ Exception -> 0x0762 }
        L_0x06a3:
            if (r8 == 0) goto L_0x0752
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r62)     // Catch:{ Exception -> 0x0762 }
            if (r1 != 0) goto L_0x06b2
            r1 = 1
            boolean r14 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r2, r1)     // Catch:{ Exception -> 0x0619 }
            if (r14 == 0) goto L_0x0752
        L_0x06b2:
            r1 = 0
        L_0x06b3:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r14 = r2.attributes     // Catch:{ Exception -> 0x0762 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0762 }
            if (r1 >= r14) goto L_0x0752
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r14 = r2.attributes     // Catch:{ Exception -> 0x0762 }
            java.lang.Object r14 = r14.get(r1)     // Catch:{ Exception -> 0x0762 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r14 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r14     // Catch:{ Exception -> 0x0762 }
            boolean r7 = r14 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker     // Catch:{ Exception -> 0x0762 }
            if (r7 == 0) goto L_0x0744
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r2.attributes     // Catch:{ Exception -> 0x0762 }
            r7.remove(r1)     // Catch:{ Exception -> 0x0762 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55 r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55     // Catch:{ Exception -> 0x0762 }
            r1.<init>()     // Catch:{ Exception -> 0x0762 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r2.attributes     // Catch:{ Exception -> 0x0762 }
            r7.add(r1)     // Catch:{ Exception -> 0x0762 }
            java.lang.String r7 = r14.alt     // Catch:{ Exception -> 0x0762 }
            r1.alt = r7     // Catch:{ Exception -> 0x0762 }
            org.telegram.tgnet.TLRPC$InputStickerSet r7 = r14.stickerset     // Catch:{ Exception -> 0x0762 }
            if (r7 == 0) goto L_0x0735
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x0762 }
            if (r2 == 0) goto L_0x06e9
            java.lang.String r2 = r7.short_name     // Catch:{ Exception -> 0x0619 }
            r32 = r5
            r36 = r6
            goto L_0x06f9
        L_0x06e9:
            org.telegram.messenger.MediaDataController r2 = r55.getMediaDataController()     // Catch:{ Exception -> 0x0762 }
            org.telegram.tgnet.TLRPC$InputStickerSet r7 = r14.stickerset     // Catch:{ Exception -> 0x0762 }
            r32 = r5
            r36 = r6
            long r5 = r7.id     // Catch:{ Exception -> 0x0742 }
            java.lang.String r2 = r2.getStickerSetName(r5)     // Catch:{ Exception -> 0x0742 }
        L_0x06f9:
            boolean r5 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0742 }
            if (r5 != 0) goto L_0x070b
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r5 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x0742 }
            r5.<init>()     // Catch:{ Exception -> 0x0742 }
            r1.stickerset = r5     // Catch:{ Exception -> 0x0742 }
            r5.short_name = r2     // Catch:{ Exception -> 0x0742 }
            r2 = 0
            r5 = 5
            goto L_0x072b
        L_0x070b:
            org.telegram.tgnet.TLRPC$InputStickerSet r2 = r14.stickerset     // Catch:{ Exception -> 0x0742 }
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetID     // Catch:{ Exception -> 0x0742 }
            if (r2 == 0) goto L_0x0722
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0742 }
            r2.<init>(r10)     // Catch:{ Exception -> 0x0742 }
            r2.encryptedChat = r8     // Catch:{ Exception -> 0x0742 }
            r2.locationParent = r1     // Catch:{ Exception -> 0x0742 }
            r5 = 5
            r2.type = r5     // Catch:{ Exception -> 0x0742 }
            org.telegram.tgnet.TLRPC$InputStickerSet r6 = r14.stickerset     // Catch:{ Exception -> 0x0742 }
            r2.parentObject = r6     // Catch:{ Exception -> 0x0742 }
            goto L_0x0724
        L_0x0722:
            r5 = 5
            r2 = 0
        L_0x0724:
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r6 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0742 }
            r6.<init>()     // Catch:{ Exception -> 0x0742 }
            r1.stickerset = r6     // Catch:{ Exception -> 0x0742 }
        L_0x072b:
            r14 = r63
            r1 = r71
            r6 = r3
            r7 = r18
            r3 = r36
            goto L_0x0777
        L_0x0735:
            r32 = r5
            r36 = r6
            r5 = 5
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0742 }
            r2.<init>()     // Catch:{ Exception -> 0x0742 }
            r1.stickerset = r2     // Catch:{ Exception -> 0x0742 }
            goto L_0x0757
        L_0x0742:
            r0 = move-exception
            goto L_0x0765
        L_0x0744:
            r32 = r5
            r36 = r6
            r5 = 5
            int r1 = r1 + 1
            r2 = r62
            r5 = r32
            r7 = 3
            goto L_0x06b3
        L_0x0752:
            r32 = r5
            r36 = r6
            r5 = 5
        L_0x0757:
            r14 = r63
            r1 = r71
            r6 = r3
            r7 = r18
            r3 = r36
            r2 = 0
            goto L_0x0777
        L_0x0762:
            r0 = move-exception
            r36 = r6
        L_0x0765:
            r5 = r78
            r1 = r0
            r13 = r36
            goto L_0x083a
        L_0x076c:
            r32 = r5
            r5 = 5
            r1 = r71
            r6 = r3
            r7 = r18
            r2 = 0
            r3 = 0
            r14 = 0
        L_0x0777:
            if (r4 == 0) goto L_0x078f
            boolean r18 = r74.isEmpty()     // Catch:{ Exception -> 0x0788 }
            if (r18 != 0) goto L_0x078f
            r3.entities = r4     // Catch:{ Exception -> 0x0788 }
            int r5 = r3.flags     // Catch:{ Exception -> 0x0788 }
            r5 = r5 | 128(0x80, float:1.794E-43)
            r3.flags = r5     // Catch:{ Exception -> 0x0788 }
            goto L_0x078f
        L_0x0788:
            r0 = move-exception
            r5 = r78
            r1 = r0
            r13 = r3
            goto L_0x083a
        L_0x078f:
            if (r7 == 0) goto L_0x0794
            r3.message = r7     // Catch:{ Exception -> 0x0788 }
            goto L_0x079a
        L_0x0794:
            java.lang.String r5 = r3.message     // Catch:{ Exception -> 0x0788 }
            if (r5 != 0) goto L_0x079a
            r3.message = r12     // Catch:{ Exception -> 0x0788 }
        L_0x079a:
            java.lang.String r5 = r3.attachPath     // Catch:{ Exception -> 0x0788 }
            if (r5 != 0) goto L_0x07a0
            r3.attachPath = r12     // Catch:{ Exception -> 0x0788 }
        L_0x07a0:
            org.telegram.messenger.UserConfig r5 = r55.getUserConfig()     // Catch:{ Exception -> 0x0788 }
            int r5 = r5.getNewMessageId()     // Catch:{ Exception -> 0x0788 }
            r3.id = r5     // Catch:{ Exception -> 0x0788 }
            r3.local_id = r5     // Catch:{ Exception -> 0x0788 }
            r5 = 1
            r3.out = r5     // Catch:{ Exception -> 0x0788 }
            if (r25 == 0) goto L_0x07c7
            if (r31 == 0) goto L_0x07c7
            org.telegram.tgnet.TLRPC$TL_peerChannel r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ Exception -> 0x0788 }
            r5.<init>()     // Catch:{ Exception -> 0x0788 }
            r3.from_id = r5     // Catch:{ Exception -> 0x0788 }
            r63 = r1
            r65 = r6
            r71 = r7
            r1 = r31
            long r6 = r1.channel_id     // Catch:{ Exception -> 0x0788 }
            r5.channel_id = r6     // Catch:{ Exception -> 0x0788 }
            goto L_0x07d5
        L_0x07c7:
            r63 = r1
            r65 = r6
            r71 = r7
            r1 = r31
            if (r27 == 0) goto L_0x07d8
            r5 = r27
            r3.from_id = r5     // Catch:{ Exception -> 0x0788 }
        L_0x07d5:
            r6 = r22
            goto L_0x07e9
        L_0x07d8:
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x0788 }
            r5.<init>()     // Catch:{ Exception -> 0x0788 }
            r3.from_id = r5     // Catch:{ Exception -> 0x0788 }
            r6 = r22
            r5.user_id = r6     // Catch:{ Exception -> 0x0788 }
            int r5 = r3.flags     // Catch:{ Exception -> 0x0788 }
            r5 = r5 | 256(0x100, float:3.59E-43)
            r3.flags = r5     // Catch:{ Exception -> 0x0788 }
        L_0x07e9:
            org.telegram.messenger.UserConfig r5 = r55.getUserConfig()     // Catch:{ Exception -> 0x0788 }
            r76 = r2
            r2 = 0
            r5.saveConfig(r2)     // Catch:{ Exception -> 0x0788 }
            r18 = r58
            r58 = r59
            r38 = r62
            r40 = r63
            r39 = r64
            r9 = r65
            r42 = r71
            r5 = r77
            r2 = r80
            r22 = r14
            r41 = r19
            r19 = r76
            r14 = r13
            r13 = r3
            r3 = r61
        L_0x080f:
            if (r5 == 0) goto L_0x083d
            r59 = r2
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0836 }
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)     // Catch:{ Exception -> 0x0836 }
            r61 = r3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0836 }
            r3.<init>()     // Catch:{ Exception -> 0x0836 }
            java.lang.String r4 = "silent_"
            r3.append(r4)     // Catch:{ Exception -> 0x0836 }
            r3.append(r10)     // Catch:{ Exception -> 0x0836 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0836 }
            r4 = 0
            boolean r2 = r2.getBoolean(r3, r4)     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x0834
            goto L_0x0841
        L_0x0834:
            r2 = 0
            goto L_0x0842
        L_0x0836:
            r0 = move-exception
            r5 = r78
        L_0x0839:
            r1 = r0
        L_0x083a:
            r2 = 0
            goto L_0x1CLASSNAME
        L_0x083d:
            r59 = r2
            r61 = r3
        L_0x0841:
            r2 = 1
        L_0x0842:
            r13.silent = r2     // Catch:{ Exception -> 0x0836 }
            long r2 = r13.random_id     // Catch:{ Exception -> 0x0836 }
            int r4 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x0850
            long r2 = r55.getNextRandomId()     // Catch:{ Exception -> 0x0836 }
            r13.random_id = r2     // Catch:{ Exception -> 0x0836 }
        L_0x0850:
            java.lang.String r4 = "bot_name"
            if (r9 == 0) goto L_0x0884
            java.lang.String r2 = "bot"
            boolean r2 = r9.containsKey(r2)     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x0884
            if (r8 == 0) goto L_0x086b
            java.lang.Object r2 = r9.get(r4)     // Catch:{ Exception -> 0x0836 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0836 }
            r13.via_bot_name = r2     // Catch:{ Exception -> 0x0836 }
            if (r2 != 0) goto L_0x087e
            r13.via_bot_name = r12     // Catch:{ Exception -> 0x0836 }
            goto L_0x087e
        L_0x086b:
            java.lang.String r2 = "bot"
            java.lang.Object r2 = r9.get(r2)     // Catch:{ Exception -> 0x0836 }
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2     // Catch:{ Exception -> 0x0836 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)     // Catch:{ Exception -> 0x0836 }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x0836 }
            long r2 = (long) r2     // Catch:{ Exception -> 0x0836 }
            r13.via_bot_id = r2     // Catch:{ Exception -> 0x0836 }
        L_0x087e:
            int r2 = r13.flags     // Catch:{ Exception -> 0x0836 }
            r2 = r2 | 2048(0x800, float:2.87E-42)
            r13.flags = r2     // Catch:{ Exception -> 0x0836 }
        L_0x0884:
            r13.params = r9     // Catch:{ Exception -> 0x0836 }
            r2 = r73
            if (r2 == 0) goto L_0x0892
            boolean r3 = r2.resendAsIs     // Catch:{ Exception -> 0x0836 }
            if (r3 != 0) goto L_0x088f
            goto L_0x0892
        L_0x088f:
            r62 = r4
            goto L_0x08ea
        L_0x0892:
            r3 = r78
            if (r3 == 0) goto L_0x0898
            r2 = r3
            goto L_0x08a2
        L_0x0898:
            org.telegram.tgnet.ConnectionsManager r23 = r55.getConnectionsManager()     // Catch:{ Exception -> 0x1c1e }
            int r23 = r23.getCurrentTime()     // Catch:{ Exception -> 0x1c1e }
            r2 = r23
        L_0x08a2:
            r13.date = r2     // Catch:{ Exception -> 0x1c1e }
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x1c1e }
            if (r2 == 0) goto L_0x08e5
            if (r3 != 0) goto L_0x08ba
            if (r25 == 0) goto L_0x08ba
            r2 = 1
            r13.views = r2     // Catch:{ Exception -> 0x08b6 }
            int r2 = r13.flags     // Catch:{ Exception -> 0x08b6 }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r13.flags = r2     // Catch:{ Exception -> 0x08b6 }
            goto L_0x08ba
        L_0x08b6:
            r0 = move-exception
            r1 = r0
            r5 = r3
            goto L_0x083a
        L_0x08ba:
            org.telegram.messenger.MessagesController r2 = r55.getMessagesController()     // Catch:{ Exception -> 0x0836 }
            r62 = r4
            long r3 = r1.channel_id     // Catch:{ Exception -> 0x0836 }
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x08ea
            boolean r3 = r2.megagroup     // Catch:{ Exception -> 0x0836 }
            if (r3 == 0) goto L_0x08d4
            r3 = 1
            r13.unread = r3     // Catch:{ Exception -> 0x0836 }
            goto L_0x08ea
        L_0x08d4:
            r3 = 1
            r13.post = r3     // Catch:{ Exception -> 0x0836 }
            boolean r2 = r2.signatures     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x08ea
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x0836 }
            r2.<init>()     // Catch:{ Exception -> 0x0836 }
            r13.from_id = r2     // Catch:{ Exception -> 0x0836 }
            r2.user_id = r6     // Catch:{ Exception -> 0x0836 }
            goto L_0x08ea
        L_0x08e5:
            r62 = r4
            r2 = 1
            r13.unread = r2     // Catch:{ Exception -> 0x0836 }
        L_0x08ea:
            int r2 = r13.flags     // Catch:{ Exception -> 0x0836 }
            r2 = r2 | 512(0x200, float:7.175E-43)
            r13.flags = r2     // Catch:{ Exception -> 0x0836 }
            r13.dialog_id = r10     // Catch:{ Exception -> 0x0836 }
            r4 = r69
            r3 = r74
            if (r4 == 0) goto L_0x0941
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = new org.telegram.tgnet.TLRPC$TL_messageReplyHeader     // Catch:{ Exception -> 0x0836 }
            r2.<init>()     // Catch:{ Exception -> 0x0836 }
            r13.reply_to = r2     // Catch:{ Exception -> 0x0836 }
            if (r8 == 0) goto L_0x0917
            r31 = r1
            org.telegram.tgnet.TLRPC$Message r1 = r4.messageOwner     // Catch:{ Exception -> 0x0836 }
            r36 = r6
            long r5 = r1.random_id     // Catch:{ Exception -> 0x0836 }
            int r1 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x091b
            r2.reply_to_random_id = r5     // Catch:{ Exception -> 0x0836 }
            int r1 = r13.flags     // Catch:{ Exception -> 0x0836 }
            r5 = 8
            r1 = r1 | r5
            r13.flags = r1     // Catch:{ Exception -> 0x0836 }
            goto L_0x0922
        L_0x0917:
            r31 = r1
            r36 = r6
        L_0x091b:
            int r1 = r13.flags     // Catch:{ Exception -> 0x0836 }
            r5 = 8
            r1 = r1 | r5
            r13.flags = r1     // Catch:{ Exception -> 0x0836 }
        L_0x0922:
            int r1 = r69.getId()     // Catch:{ Exception -> 0x0836 }
            r2.reply_to_msg_id = r1     // Catch:{ Exception -> 0x0836 }
            r1 = r70
            r7 = r73
            if (r1 == 0) goto L_0x0949
            if (r1 == r4) goto L_0x0949
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r13.reply_to     // Catch:{ Exception -> 0x0836 }
            int r5 = r70.getId()     // Catch:{ Exception -> 0x0836 }
            r2.reply_to_top_id = r5     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r13.reply_to     // Catch:{ Exception -> 0x0836 }
            int r5 = r2.flags     // Catch:{ Exception -> 0x0836 }
            r6 = 2
            r5 = r5 | r6
            r2.flags = r5     // Catch:{ Exception -> 0x0836 }
            goto L_0x0949
        L_0x0941:
            r31 = r1
            r36 = r6
            r1 = r70
            r7 = r73
        L_0x0949:
            r6 = r29
            int r2 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x0967
            org.telegram.tgnet.TLRPC$TL_messageReplies r2 = new org.telegram.tgnet.TLRPC$TL_messageReplies     // Catch:{ Exception -> 0x0836 }
            r2.<init>()     // Catch:{ Exception -> 0x0836 }
            r13.replies = r2     // Catch:{ Exception -> 0x0836 }
            r5 = 1
            r2.comments = r5     // Catch:{ Exception -> 0x0836 }
            r2.channel_id = r6     // Catch:{ Exception -> 0x0836 }
            int r6 = r2.flags     // Catch:{ Exception -> 0x0836 }
            r6 = r6 | r5
            r2.flags = r6     // Catch:{ Exception -> 0x0836 }
            int r2 = r13.flags     // Catch:{ Exception -> 0x0836 }
            r5 = 8388608(0x800000, float:1.17549435E-38)
            r2 = r2 | r5
            r13.flags = r2     // Catch:{ Exception -> 0x0836 }
        L_0x0967:
            r2 = r75
            if (r2 == 0) goto L_0x0975
            if (r8 != 0) goto L_0x0975
            int r5 = r13.flags     // Catch:{ Exception -> 0x0836 }
            r5 = r5 | 64
            r13.flags = r5     // Catch:{ Exception -> 0x0836 }
            r13.reply_markup = r2     // Catch:{ Exception -> 0x0836 }
        L_0x0975:
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r66)     // Catch:{ Exception -> 0x0836 }
            if (r2 != 0) goto L_0x09ab
            org.telegram.messenger.MessagesController r2 = r55.getMessagesController()     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$Peer r2 = r2.getPeer(r10)     // Catch:{ Exception -> 0x0836 }
            r13.peer_id = r2     // Catch:{ Exception -> 0x0836 }
            boolean r2 = org.telegram.messenger.DialogObject.isUserDialog(r66)     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x09a8
            org.telegram.messenger.MessagesController r2 = r55.getMessagesController()     // Catch:{ Exception -> 0x0836 }
            java.lang.Long r5 = java.lang.Long.valueOf(r66)     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r5)     // Catch:{ Exception -> 0x0836 }
            if (r2 != 0) goto L_0x099f
            int r1 = r13.id     // Catch:{ Exception -> 0x0836 }
            r15.processSentMessage(r1)     // Catch:{ Exception -> 0x0836 }
            return
        L_0x099f:
            boolean r2 = r2.bot     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x09a7
            r5 = 0
            r13.unread = r5     // Catch:{ Exception -> 0x0836 }
            goto L_0x09a8
        L_0x09a7:
            r5 = 0
        L_0x09a8:
            r7 = 4
            goto L_0x0a52
        L_0x09ab:
            r5 = 0
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x0836 }
            r2.<init>()     // Catch:{ Exception -> 0x0836 }
            r13.peer_id = r2     // Catch:{ Exception -> 0x0836 }
            long r6 = r8.participant_id     // Catch:{ Exception -> 0x0836 }
            int r23 = (r6 > r36 ? 1 : (r6 == r36 ? 0 : -1))
            if (r23 != 0) goto L_0x09be
            long r6 = r8.admin_id     // Catch:{ Exception -> 0x0836 }
            r2.user_id = r6     // Catch:{ Exception -> 0x0836 }
            goto L_0x09c0
        L_0x09be:
            r2.user_id = r6     // Catch:{ Exception -> 0x0836 }
        L_0x09c0:
            if (r14 == 0) goto L_0x09c6
            r13.ttl = r14     // Catch:{ Exception -> 0x0836 }
        L_0x09c4:
            r7 = 4
            goto L_0x09d8
        L_0x09c6:
            int r2 = r8.ttl     // Catch:{ Exception -> 0x0836 }
            r13.ttl = r2     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x09c4
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r13.media     // Catch:{ Exception -> 0x0836 }
            if (r6 == 0) goto L_0x09c4
            r6.ttl_seconds = r2     // Catch:{ Exception -> 0x0836 }
            int r2 = r6.flags     // Catch:{ Exception -> 0x0836 }
            r7 = 4
            r2 = r2 | r7
            r6.flags = r2     // Catch:{ Exception -> 0x0836 }
        L_0x09d8:
            int r2 = r13.ttl     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x0a52
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r13.media     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x0a52
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r13)     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x0a18
            r6 = 0
        L_0x09e9:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r13.media     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x0836 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes     // Catch:{ Exception -> 0x0836 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0836 }
            if (r6 >= r2) goto L_0x0a0c
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r13.media     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x0836 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes     // Catch:{ Exception -> 0x0836 }
            java.lang.Object r2 = r2.get(r6)     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2     // Catch:{ Exception -> 0x0836 }
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ Exception -> 0x0836 }
            if (r5 == 0) goto L_0x0a08
            int r6 = r2.duration     // Catch:{ Exception -> 0x0836 }
            goto L_0x0a0d
        L_0x0a08:
            int r6 = r6 + 1
            r5 = 0
            goto L_0x09e9
        L_0x0a0c:
            r6 = 0
        L_0x0a0d:
            int r2 = r13.ttl     // Catch:{ Exception -> 0x0836 }
            r5 = 1
            int r6 = r6 + r5
            int r2 = java.lang.Math.max(r2, r6)     // Catch:{ Exception -> 0x0836 }
            r13.ttl = r2     // Catch:{ Exception -> 0x0836 }
            goto L_0x0a52
        L_0x0a18:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r13)     // Catch:{ Exception -> 0x0836 }
            if (r2 != 0) goto L_0x0a24
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r13)     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x0a52
        L_0x0a24:
            r6 = 0
        L_0x0a25:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r13.media     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x0836 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes     // Catch:{ Exception -> 0x0836 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0836 }
            if (r6 >= r2) goto L_0x0a47
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r13.media     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x0836 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes     // Catch:{ Exception -> 0x0836 }
            java.lang.Object r2 = r2.get(r6)     // Catch:{ Exception -> 0x0836 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2     // Catch:{ Exception -> 0x0836 }
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x0836 }
            if (r5 == 0) goto L_0x0a44
            int r6 = r2.duration     // Catch:{ Exception -> 0x0836 }
            goto L_0x0a48
        L_0x0a44:
            int r6 = r6 + 1
            goto L_0x0a25
        L_0x0a47:
            r6 = 0
        L_0x0a48:
            int r2 = r13.ttl     // Catch:{ Exception -> 0x0836 }
            r5 = 1
            int r6 = r6 + r5
            int r2 = java.lang.Math.max(r2, r6)     // Catch:{ Exception -> 0x0836 }
            r13.ttl = r2     // Catch:{ Exception -> 0x0836 }
        L_0x0a52:
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r13)     // Catch:{ Exception -> 0x0836 }
            if (r2 != 0) goto L_0x0a5e
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r13)     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x0a61
        L_0x0a5e:
            r2 = 1
            r13.media_unread = r2     // Catch:{ Exception -> 0x0836 }
        L_0x0a61:
            org.telegram.tgnet.TLRPC$Peer r2 = r13.from_id     // Catch:{ Exception -> 0x0836 }
            if (r2 != 0) goto L_0x0a69
            org.telegram.tgnet.TLRPC$Peer r2 = r13.peer_id     // Catch:{ Exception -> 0x0836 }
            r13.from_id = r2     // Catch:{ Exception -> 0x0836 }
        L_0x0a69:
            r5 = 1
            r13.send_state = r5     // Catch:{ Exception -> 0x0836 }
            if (r9 == 0) goto L_0x0a9b
            java.lang.String r2 = "groupId"
            java.lang.Object r2 = r9.get(r2)     // Catch:{ Exception -> 0x0836 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x0a8b
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r2)     // Catch:{ Exception -> 0x0836 }
            long r5 = r2.longValue()     // Catch:{ Exception -> 0x0836 }
            r13.grouped_id = r5     // Catch:{ Exception -> 0x0836 }
            int r2 = r13.flags     // Catch:{ Exception -> 0x0836 }
            r23 = 131072(0x20000, float:1.83671E-40)
            r2 = r2 | r23
            r13.flags = r2     // Catch:{ Exception -> 0x0836 }
            goto L_0x0a8d
        L_0x0a8b:
            r5 = r16
        L_0x0a8d:
            java.lang.String r2 = "final"
            java.lang.Object r2 = r9.get(r2)     // Catch:{ Exception -> 0x0836 }
            if (r2 == 0) goto L_0x0a97
            r2 = 1
            goto L_0x0a98
        L_0x0a97:
            r2 = 0
        L_0x0a98:
            r23 = r2
            goto L_0x0a9f
        L_0x0a9b:
            r5 = r16
            r23 = 0
        L_0x0a9f:
            org.telegram.messenger.MessageObject r2 = new org.telegram.messenger.MessageObject     // Catch:{ Exception -> 0x0836 }
            int r7 = r15.currentAccount     // Catch:{ Exception -> 0x0836 }
            r25 = 1
            r27 = 1
            r63 = r59
            r71 = r2
            r43 = r61
            r44 = r33
            r3 = r7
            r45 = r62
            r7 = r35
            r4 = r13
            r49 = r58
            r47 = r5
            r50 = r20
            r46 = r31
            r51 = r32
            r6 = r77
            r5 = r69
            r58 = r14
            r14 = r6
            r6 = r25
            r52 = r7
            r36 = r12
            r1 = 3
            r12 = r73
            r7 = r27
            r2.<init>((int) r3, (org.telegram.tgnet.TLRPC$Message) r4, (org.telegram.messenger.MessageObject) r5, (boolean) r6, (boolean) r7)     // Catch:{ Exception -> 0x0836 }
            r3 = r71
            r2 = r81
            r3.sendAnimationData = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            r2 = 1
            r3.wasJustSent = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            r4 = r78
            if (r4 == 0) goto L_0x0ae3
            r5 = 1
            goto L_0x0ae4
        L_0x0ae3:
            r5 = 0
        L_0x0ae4:
            r3.scheduled = r5     // Catch:{ Exception -> 0x1CLASSNAME }
            boolean r5 = r3.isForwarded()     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r5 != 0) goto L_0x0b08
            int r5 = r3.type     // Catch:{ Exception -> 0x0b02 }
            if (r5 == r1) goto L_0x0af6
            if (r60 != 0) goto L_0x0af6
            r6 = 2
            if (r5 != r6) goto L_0x0b09
            goto L_0x0af7
        L_0x0af6:
            r6 = 2
        L_0x0af7:
            java.lang.String r5 = r13.attachPath     // Catch:{ Exception -> 0x0b02 }
            boolean r5 = android.text.TextUtils.isEmpty(r5)     // Catch:{ Exception -> 0x0b02 }
            if (r5 != 0) goto L_0x0b09
            r3.attachPathExists = r2     // Catch:{ Exception -> 0x0b02 }
            goto L_0x0b09
        L_0x0b02:
            r0 = move-exception
            r1 = r0
            r2 = r3
        L_0x0b05:
            r5 = r4
            goto L_0x1CLASSNAME
        L_0x0b08:
            r6 = 2
        L_0x0b09:
            org.telegram.messenger.VideoEditedInfo r5 = r3.videoEditedInfo     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r5 == 0) goto L_0x0b10
            if (r60 != 0) goto L_0x0b10
            goto L_0x0b12
        L_0x0b10:
            r5 = r60
        L_0x0b12:
            r6 = r47
            int r20 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r20 != 0) goto L_0x0b73
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0b6c }
            r1.<init>()     // Catch:{ Exception -> 0x0b6c }
            r1.add(r3)     // Catch:{ Exception -> 0x0b6c }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ Exception -> 0x0b6c }
            r2.<init>()     // Catch:{ Exception -> 0x0b6c }
            r2.add(r13)     // Catch:{ Exception -> 0x0b6c }
            r57 = r5
            int r5 = r15.currentAccount     // Catch:{ Exception -> 0x0b6c }
            org.telegram.messenger.MessagesStorage r23 = org.telegram.messenger.MessagesStorage.getInstance(r5)     // Catch:{ Exception -> 0x0b6c }
            r25 = 0
            r26 = 1
            r27 = 0
            r28 = 0
            if (r4 == 0) goto L_0x0b3d
            r29 = 1
            goto L_0x0b3f
        L_0x0b3d:
            r29 = 0
        L_0x0b3f:
            r24 = r2
            r23.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r24, (boolean) r25, (boolean) r26, (boolean) r27, (int) r28, (boolean) r29)     // Catch:{ Exception -> 0x0b6c }
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0b6c }
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)     // Catch:{ Exception -> 0x0b6c }
            if (r4 == 0) goto L_0x0b4e
            r5 = 1
            goto L_0x0b4f
        L_0x0b4e:
            r5 = 0
        L_0x0b4f:
            r2.updateInterfaceWithMessages(r10, r1, r5)     // Catch:{ Exception -> 0x0b6c }
            if (r4 != 0) goto L_0x0b65
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0b6c }
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)     // Catch:{ Exception -> 0x0b6c }
            int r2 = org.telegram.messenger.NotificationCenter.dialogsNeedReload     // Catch:{ Exception -> 0x0b6c }
            r71 = r3
            r5 = 0
            java.lang.Object[] r3 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0b9c }
            r1.postNotificationName(r2, r3)     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0b68
        L_0x0b65:
            r71 = r3
            r5 = 0
        L_0x0b68:
            r1 = r19
            r2 = 0
            goto L_0x0bc3
        L_0x0b6c:
            r0 = move-exception
            r71 = r3
        L_0x0b6f:
            r2 = r71
        L_0x0b71:
            r1 = r0
            goto L_0x0b05
        L_0x0b73:
            r71 = r3
            r57 = r5
            r5 = 0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1c0f }
            r1.<init>()     // Catch:{ Exception -> 0x1c0f }
            java.lang.String r2 = "group_"
            r1.append(r2)     // Catch:{ Exception -> 0x1c0f }
            r1.append(r6)     // Catch:{ Exception -> 0x1c0f }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x1c0f }
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r2 = r15.delayedMessages     // Catch:{ Exception -> 0x1c0f }
            java.lang.Object r1 = r2.get(r1)     // Catch:{ Exception -> 0x1c0f }
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch:{ Exception -> 0x1c0f }
            if (r1 == 0) goto L_0x0b9e
            java.lang.Object r1 = r1.get(r5)     // Catch:{ Exception -> 0x0b9c }
            r19 = r1
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r19 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r19     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0b9e
        L_0x0b9c:
            r0 = move-exception
            goto L_0x0b6f
        L_0x0b9e:
            if (r19 != 0) goto L_0x0bb2
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0b9c }
            r1.<init>(r10)     // Catch:{ Exception -> 0x0b9c }
            r1.initForGroup(r6)     // Catch:{ Exception -> 0x0b9c }
            r1.encryptedChat = r8     // Catch:{ Exception -> 0x0b9c }
            if (r4 == 0) goto L_0x0bae
            r3 = 1
            goto L_0x0baf
        L_0x0bae:
            r3 = 0
        L_0x0baf:
            r1.scheduled = r3     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0bb4
        L_0x0bb2:
            r1 = r19
        L_0x0bb4:
            r1.performMediaUpload = r5     // Catch:{ Exception -> 0x1c0f }
            r2 = 0
            r1.photoSize = r2     // Catch:{ Exception -> 0x1c0f }
            r1.videoEditedInfo = r2     // Catch:{ Exception -> 0x1c0f }
            r1.httpLocation = r2     // Catch:{ Exception -> 0x1c0f }
            if (r23 == 0) goto L_0x0bc3
            int r3 = r13.id     // Catch:{ Exception -> 0x0b9c }
            r1.finalGroupMessage = r3     // Catch:{ Exception -> 0x0b9c }
        L_0x0bc3:
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1c0f }
            if (r3 == 0) goto L_0x0CLASSNAME
            r3 = r46
            if (r3 == 0) goto L_0x0c2e
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b9c }
            r2.<init>()     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r5 = "send message user_id = "
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            r47 = r6
            long r5 = r3.user_id     // Catch:{ Exception -> 0x0b9c }
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r5 = " chat_id = "
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            long r5 = r3.chat_id     // Catch:{ Exception -> 0x0b9c }
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r5 = " channel_id = "
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            long r5 = r3.channel_id     // Catch:{ Exception -> 0x0b9c }
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r5 = " access_hash = "
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            long r5 = r3.access_hash     // Catch:{ Exception -> 0x0b9c }
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r5 = " notify = "
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            r2.append(r14)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r5 = " silent = "
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            int r5 = r15.currentAccount     // Catch:{ Exception -> 0x0b9c }
            android.content.SharedPreferences r5 = org.telegram.messenger.MessagesController.getNotificationsSettings(r5)     // Catch:{ Exception -> 0x0b9c }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b9c }
            r6.<init>()     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r7 = "silent_"
            r6.append(r7)     // Catch:{ Exception -> 0x0b9c }
            r6.append(r10)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0b9c }
            r7 = 0
            boolean r5 = r5.getBoolean(r6, r7)     // Catch:{ Exception -> 0x0b9c }
            r2.append(r5)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b9c }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0CLASSNAME
        L_0x0c2e:
            r47 = r6
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r47 = r6
            r3 = r46
        L_0x0CLASSNAME:
            r2 = r41
            if (r2 == 0) goto L_0x1adb
            r5 = 9
            if (r2 != r5) goto L_0x0CLASSNAME
            if (r56 == 0) goto L_0x0CLASSNAME
            if (r8 == 0) goto L_0x0CLASSNAME
            goto L_0x1adb
        L_0x0CLASSNAME:
            r5 = 1
            if (r2 < r5) goto L_0x0CLASSNAME
            r5 = 3
            if (r2 <= r5) goto L_0x0c4a
            goto L_0x0CLASSNAME
        L_0x0c4a:
            r5 = 5
        L_0x0c4b:
            r6 = 10
            r7 = 11
        L_0x0c4f:
            r14 = 4
            goto L_0x0de9
        L_0x0CLASSNAME:
            r5 = 5
            if (r2 < r5) goto L_0x0CLASSNAME
            r6 = 8
            if (r2 <= r6) goto L_0x0c4b
        L_0x0CLASSNAME:
            r6 = 9
            if (r2 != r6) goto L_0x0c5f
            if (r8 != 0) goto L_0x0c4b
        L_0x0c5f:
            r6 = 10
            r7 = 11
            if (r2 == r6) goto L_0x0c4f
            if (r2 != r7) goto L_0x0CLASSNAME
            goto L_0x0c4f
        L_0x0CLASSNAME:
            r14 = 4
            if (r2 != r14) goto L_0x0d52
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r1 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages     // Catch:{ Exception -> 0x0b9c }
            r1.<init>()     // Catch:{ Exception -> 0x0b9c }
            r1.to_peer = r3     // Catch:{ Exception -> 0x0b9c }
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner     // Catch:{ Exception -> 0x0b9c }
            boolean r2 = r2.with_my_score     // Catch:{ Exception -> 0x0b9c }
            r1.with_my_score = r2     // Catch:{ Exception -> 0x0b9c }
            if (r9 == 0) goto L_0x0ce3
            r2 = r34
            boolean r3 = r9.containsKey(r2)     // Catch:{ Exception -> 0x0b9c }
            if (r3 == 0) goto L_0x0ce3
            java.lang.Object r2 = r9.get(r2)     // Catch:{ Exception -> 0x0b9c }
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2     // Catch:{ Exception -> 0x0b9c }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2)     // Catch:{ Exception -> 0x0b9c }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x0b9c }
            r3 = 1
            r1.drop_author = r3     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r3 = "fwd_peer"
            java.lang.Object r3 = r9.get(r3)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0b9c }
            java.lang.Long r3 = org.telegram.messenger.Utilities.parseLong(r3)     // Catch:{ Exception -> 0x0b9c }
            long r5 = r3.longValue()     // Catch:{ Exception -> 0x0b9c }
            int r3 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r3 >= 0) goto L_0x0cd2
            org.telegram.messenger.MessagesController r3 = r55.getMessagesController()     // Catch:{ Exception -> 0x0b9c }
            long r5 = -r5
            java.lang.Long r5 = java.lang.Long.valueOf(r5)     // Catch:{ Exception -> 0x0b9c }
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r5)     // Catch:{ Exception -> 0x0b9c }
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r3)     // Catch:{ Exception -> 0x0b9c }
            if (r5 == 0) goto L_0x0cca
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r5 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x0b9c }
            r5.<init>()     // Catch:{ Exception -> 0x0b9c }
            r1.from_peer = r5     // Catch:{ Exception -> 0x0b9c }
            long r6 = r3.id     // Catch:{ Exception -> 0x0b9c }
            r5.channel_id = r6     // Catch:{ Exception -> 0x0b9c }
            long r6 = r3.access_hash     // Catch:{ Exception -> 0x0b9c }
            r5.access_hash = r6     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0cd9
        L_0x0cca:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r3 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0b9c }
            r3.<init>()     // Catch:{ Exception -> 0x0b9c }
            r1.from_peer = r3     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0cd9
        L_0x0cd2:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r3 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0b9c }
            r3.<init>()     // Catch:{ Exception -> 0x0b9c }
            r1.from_peer = r3     // Catch:{ Exception -> 0x0b9c }
        L_0x0cd9:
            java.util.ArrayList<java.lang.Integer> r3 = r1.id     // Catch:{ Exception -> 0x0b9c }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x0b9c }
            r3.add(r2)     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0cea
        L_0x0ce3:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0b9c }
            r2.<init>()     // Catch:{ Exception -> 0x0b9c }
            r1.from_peer = r2     // Catch:{ Exception -> 0x0b9c }
        L_0x0cea:
            boolean r2 = r13.silent     // Catch:{ Exception -> 0x0b9c }
            r1.silent = r2     // Catch:{ Exception -> 0x0b9c }
            if (r4 == 0) goto L_0x0cf8
            r1.schedule_date = r4     // Catch:{ Exception -> 0x0b9c }
            int r2 = r1.flags     // Catch:{ Exception -> 0x0b9c }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r1.flags = r2     // Catch:{ Exception -> 0x0b9c }
        L_0x0cf8:
            java.util.ArrayList<java.lang.Long> r2 = r1.random_id     // Catch:{ Exception -> 0x0b9c }
            long r5 = r13.random_id     // Catch:{ Exception -> 0x0b9c }
            java.lang.Long r3 = java.lang.Long.valueOf(r5)     // Catch:{ Exception -> 0x0b9c }
            r2.add(r3)     // Catch:{ Exception -> 0x0b9c }
            int r2 = r73.getId()     // Catch:{ Exception -> 0x0b9c }
            if (r2 < 0) goto L_0x0d17
            java.util.ArrayList<java.lang.Integer> r2 = r1.id     // Catch:{ Exception -> 0x0b9c }
            int r3 = r73.getId()     // Catch:{ Exception -> 0x0b9c }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x0b9c }
            r2.add(r3)     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0d36
        L_0x0d17:
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner     // Catch:{ Exception -> 0x0b9c }
            int r3 = r2.fwd_msg_id     // Catch:{ Exception -> 0x0b9c }
            if (r3 == 0) goto L_0x0d27
            java.util.ArrayList<java.lang.Integer> r2 = r1.id     // Catch:{ Exception -> 0x0b9c }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x0b9c }
            r2.add(r3)     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0d36
        L_0x0d27:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from     // Catch:{ Exception -> 0x0b9c }
            if (r2 == 0) goto L_0x0d36
            java.util.ArrayList<java.lang.Integer> r3 = r1.id     // Catch:{ Exception -> 0x0b9c }
            int r2 = r2.channel_post     // Catch:{ Exception -> 0x0b9c }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x0b9c }
            r3.add(r2)     // Catch:{ Exception -> 0x0b9c }
        L_0x0d36:
            r2 = 0
            r3 = 0
            if (r4 == 0) goto L_0x0d3c
            r5 = 1
            goto L_0x0d3d
        L_0x0d3c:
            r5 = 0
        L_0x0d3d:
            r56 = r55
            r57 = r1
            r58 = r71
            r59 = r2
            r60 = r3
            r61 = r63
            r62 = r9
            r63 = r5
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x0b9c }
            goto L_0x1CLASSNAME
        L_0x0d52:
            r1 = 9
            if (r2 != r1) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult     // Catch:{ Exception -> 0x0b9c }
            r1.<init>()     // Catch:{ Exception -> 0x0b9c }
            r1.peer = r3     // Catch:{ Exception -> 0x0b9c }
            long r2 = r13.random_id     // Catch:{ Exception -> 0x0b9c }
            r1.random_id = r2     // Catch:{ Exception -> 0x0b9c }
            org.telegram.tgnet.TLRPC$Peer r2 = r13.from_id     // Catch:{ Exception -> 0x0b9c }
            if (r2 == 0) goto L_0x0d71
            org.telegram.messenger.MessagesController r2 = r55.getMessagesController()     // Catch:{ Exception -> 0x0b9c }
            org.telegram.tgnet.TLRPC$Peer r3 = r13.from_id     // Catch:{ Exception -> 0x0b9c }
            org.telegram.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r3)     // Catch:{ Exception -> 0x0b9c }
            r1.send_as = r2     // Catch:{ Exception -> 0x0b9c }
        L_0x0d71:
            java.lang.String r2 = "bot"
            boolean r2 = r9.containsKey(r2)     // Catch:{ Exception -> 0x0b9c }
            if (r2 != 0) goto L_0x0d7b
            r3 = 1
            goto L_0x0d7c
        L_0x0d7b:
            r3 = 0
        L_0x0d7c:
            r1.hide_via = r3     // Catch:{ Exception -> 0x0b9c }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r13.reply_to     // Catch:{ Exception -> 0x0b9c }
            if (r2 == 0) goto L_0x0d8e
            int r2 = r2.reply_to_msg_id     // Catch:{ Exception -> 0x0b9c }
            if (r2 == 0) goto L_0x0d8e
            int r3 = r1.flags     // Catch:{ Exception -> 0x0b9c }
            r5 = 1
            r3 = r3 | r5
            r1.flags = r3     // Catch:{ Exception -> 0x0b9c }
            r1.reply_to_msg_id = r2     // Catch:{ Exception -> 0x0b9c }
        L_0x0d8e:
            boolean r2 = r13.silent     // Catch:{ Exception -> 0x0b9c }
            r1.silent = r2     // Catch:{ Exception -> 0x0b9c }
            if (r4 == 0) goto L_0x0d9c
            r1.schedule_date = r4     // Catch:{ Exception -> 0x0b9c }
            int r2 = r1.flags     // Catch:{ Exception -> 0x0b9c }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r1.flags = r2     // Catch:{ Exception -> 0x0b9c }
        L_0x0d9c:
            r2 = r51
            java.lang.Object r2 = r9.get(r2)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0b9c }
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r2)     // Catch:{ Exception -> 0x0b9c }
            long r2 = r2.longValue()     // Catch:{ Exception -> 0x0b9c }
            r1.query_id = r2     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r2 = "id"
            java.lang.Object r2 = r9.get(r2)     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0b9c }
            r1.id = r2     // Catch:{ Exception -> 0x0b9c }
            if (r12 != 0) goto L_0x0dcd
            r2 = 1
            r1.clear_draft = r2     // Catch:{ Exception -> 0x0b9c }
            org.telegram.messenger.MediaDataController r2 = r55.getMediaDataController()     // Catch:{ Exception -> 0x0b9c }
            if (r70 == 0) goto L_0x0dc8
            int r3 = r70.getId()     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0dc9
        L_0x0dc8:
            r3 = 0
        L_0x0dc9:
            r5 = 0
            r2.cleanDraft(r10, r3, r5)     // Catch:{ Exception -> 0x0b9c }
        L_0x0dcd:
            r2 = 0
            r3 = 0
            if (r4 == 0) goto L_0x0dd3
            r5 = 1
            goto L_0x0dd4
        L_0x0dd3:
            r5 = 0
        L_0x0dd4:
            r56 = r55
            r57 = r1
            r58 = r71
            r59 = r2
            r60 = r3
            r61 = r63
            r62 = r9
            r63 = r5
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x0b9c }
            goto L_0x1CLASSNAME
        L_0x0de9:
            if (r8 != 0) goto L_0x1521
            r5 = 1
            if (r2 != r5) goto L_0x0e5e
            r5 = r18
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x0b9c }
            if (r8 == 0) goto L_0x0e0e
            org.telegram.tgnet.TLRPC$TL_inputMediaVenue r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue     // Catch:{ Exception -> 0x0b9c }
            r8.<init>()     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r10 = r5.address     // Catch:{ Exception -> 0x0b9c }
            r8.address = r10     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r10 = r5.title     // Catch:{ Exception -> 0x0b9c }
            r8.title = r10     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r10 = r5.provider     // Catch:{ Exception -> 0x0b9c }
            r8.provider = r10     // Catch:{ Exception -> 0x0b9c }
            java.lang.String r10 = r5.venue_id     // Catch:{ Exception -> 0x0b9c }
            r8.venue_id = r10     // Catch:{ Exception -> 0x0b9c }
            r14 = r36
            r8.venue_type = r14     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0e3d
        L_0x0e0e:
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive     // Catch:{ Exception -> 0x0b9c }
            if (r8 == 0) goto L_0x0e38
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive     // Catch:{ Exception -> 0x0b9c }
            r8.<init>()     // Catch:{ Exception -> 0x0b9c }
            int r10 = r5.period     // Catch:{ Exception -> 0x0b9c }
            r8.period = r10     // Catch:{ Exception -> 0x0b9c }
            int r10 = r8.flags     // Catch:{ Exception -> 0x0b9c }
            r11 = 2
            r10 = r10 | r11
            r8.flags = r10     // Catch:{ Exception -> 0x0b9c }
            int r11 = r5.heading     // Catch:{ Exception -> 0x0b9c }
            if (r11 == 0) goto L_0x0e2a
            r8.heading = r11     // Catch:{ Exception -> 0x0b9c }
            r10 = r10 | r14
            r8.flags = r10     // Catch:{ Exception -> 0x0b9c }
        L_0x0e2a:
            int r10 = r5.proximity_notification_radius     // Catch:{ Exception -> 0x0b9c }
            if (r10 == 0) goto L_0x0e3d
            r8.proximity_notification_radius = r10     // Catch:{ Exception -> 0x0b9c }
            int r10 = r8.flags     // Catch:{ Exception -> 0x0b9c }
            r11 = 8
            r10 = r10 | r11
            r8.flags = r10     // Catch:{ Exception -> 0x0b9c }
            goto L_0x0e3d
        L_0x0e38:
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint     // Catch:{ Exception -> 0x0b9c }
            r8.<init>()     // Catch:{ Exception -> 0x0b9c }
        L_0x0e3d:
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r10 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint     // Catch:{ Exception -> 0x0b9c }
            r10.<init>()     // Catch:{ Exception -> 0x0b9c }
            r8.geo_point = r10     // Catch:{ Exception -> 0x0b9c }
            org.telegram.tgnet.TLRPC$GeoPoint r5 = r5.geo     // Catch:{ Exception -> 0x0b9c }
            double r11 = r5.lat     // Catch:{ Exception -> 0x0b9c }
            r10.lat = r11     // Catch:{ Exception -> 0x0b9c }
            double r11 = r5._long     // Catch:{ Exception -> 0x0b9c }
            r10._long = r11     // Catch:{ Exception -> 0x0b9c }
            r57 = r2
            r46 = r3
            r5 = r8
            r64 = r9
            r6 = r50
            r3 = 0
            r2 = r71
            r8 = r4
            r4 = r13
            goto L_0x1331
        L_0x0e5e:
            r14 = r36
            r5 = 2
            if (r2 == r5) goto L_0x1253
            r5 = 9
            if (r2 != r5) goto L_0x0e76
            r5 = r49
            if (r5 == 0) goto L_0x0e76
            r7 = r58
            r57 = r2
            r46 = r3
            r8 = r4
            r18 = r13
            goto L_0x125e
        L_0x0e76:
            java.lang.String r5 = "query"
            r8 = 3
            if (r2 != r8) goto L_0x0fa5
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0f9e }
            r8.<init>()     // Catch:{ Exception -> 0x0f9e }
            r12 = r38
            java.lang.String r14 = r12.mime_type     // Catch:{ Exception -> 0x0f9e }
            r8.mime_type = r14     // Catch:{ Exception -> 0x0f9e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r14 = r12.attributes     // Catch:{ Exception -> 0x0f9e }
            r8.attributes = r14     // Catch:{ Exception -> 0x0f9e }
            if (r22 != 0) goto L_0x0ea2
            boolean r14 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r12)     // Catch:{ Exception -> 0x0b9c }
            if (r14 != 0) goto L_0x0e9f
            if (r57 == 0) goto L_0x0ea2
            r14 = r57
            boolean r7 = r14.muted     // Catch:{ Exception -> 0x0b9c }
            if (r7 != 0) goto L_0x0eb0
            boolean r7 = r14.roundVideo     // Catch:{ Exception -> 0x0b9c }
            if (r7 != 0) goto L_0x0eb0
            goto L_0x0ea4
        L_0x0e9f:
            r14 = r57
            goto L_0x0eb0
        L_0x0ea2:
            r14 = r57
        L_0x0ea4:
            r7 = 1
            r8.nosound_video = r7     // Catch:{ Exception -> 0x0f9e }
            boolean r7 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0f9e }
            if (r7 == 0) goto L_0x0eb0
            java.lang.String r7 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r7)     // Catch:{ Exception -> 0x0b9c }
        L_0x0eb0:
            if (r58 == 0) goto L_0x0ec0
            r7 = r58
            r8.ttl_seconds = r7     // Catch:{ Exception -> 0x0b9c }
            r13.ttl = r7     // Catch:{ Exception -> 0x0b9c }
            int r7 = r8.flags     // Catch:{ Exception -> 0x0b9c }
            r18 = 2
            r7 = r7 | 2
            r8.flags = r7     // Catch:{ Exception -> 0x0b9c }
        L_0x0ec0:
            if (r9 == 0) goto L_0x0f0e
            java.lang.String r7 = "masks"
            java.lang.Object r7 = r9.get(r7)     // Catch:{ Exception -> 0x0var_ }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ Exception -> 0x0var_ }
            if (r7 == 0) goto L_0x0f0e
            org.telegram.tgnet.SerializedData r6 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0var_ }
            byte[] r7 = org.telegram.messenger.Utilities.hexToBytes(r7)     // Catch:{ Exception -> 0x0var_ }
            r6.<init>((byte[]) r7)     // Catch:{ Exception -> 0x0var_ }
            r46 = r3
            r7 = 0
            int r3 = r6.readInt32(r7)     // Catch:{ Exception -> 0x0var_ }
        L_0x0edc:
            if (r7 >= r3) goto L_0x0efb
            r56 = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r3 = r8.stickers     // Catch:{ Exception -> 0x0var_ }
            r57 = r2
            r18 = r13
            r13 = 0
            int r2 = r6.readInt32(r13)     // Catch:{ Exception -> 0x0f9a }
            org.telegram.tgnet.TLRPC$InputDocument r2 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r6, r2, r13)     // Catch:{ Exception -> 0x0f9a }
            r3.add(r2)     // Catch:{ Exception -> 0x0f9a }
            int r7 = r7 + 1
            r3 = r56
            r2 = r57
            r13 = r18
            goto L_0x0edc
        L_0x0efb:
            r57 = r2
            r18 = r13
            int r2 = r8.flags     // Catch:{ Exception -> 0x0f9a }
            r3 = 1
            r2 = r2 | r3
            r8.flags = r2     // Catch:{ Exception -> 0x0f9a }
            r6.cleanup()     // Catch:{ Exception -> 0x0f9a }
            goto L_0x0var_
        L_0x0var_:
            r0 = move-exception
            r18 = r13
            goto L_0x0b6f
        L_0x0f0e:
            r57 = r2
            r46 = r3
            r18 = r13
        L_0x0var_:
            long r2 = r12.access_hash     // Catch:{ Exception -> 0x0f9a }
            int r6 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r6 != 0) goto L_0x0f1d
            r5 = r8
            r3 = 1
            goto L_0x0var_
        L_0x0f1d:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0f9a }
            r2.<init>()     // Catch:{ Exception -> 0x0f9a }
            org.telegram.tgnet.TLRPC$TL_inputDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0f9a }
            r3.<init>()     // Catch:{ Exception -> 0x0f9a }
            r2.id = r3     // Catch:{ Exception -> 0x0f9a }
            long r6 = r12.id     // Catch:{ Exception -> 0x0f9a }
            r3.id = r6     // Catch:{ Exception -> 0x0f9a }
            long r6 = r12.access_hash     // Catch:{ Exception -> 0x0f9a }
            r3.access_hash = r6     // Catch:{ Exception -> 0x0f9a }
            byte[] r6 = r12.file_reference     // Catch:{ Exception -> 0x0f9a }
            r3.file_reference = r6     // Catch:{ Exception -> 0x0f9a }
            if (r6 != 0) goto L_0x0f3c
            r6 = 0
            byte[] r7 = new byte[r6]     // Catch:{ Exception -> 0x0f9a }
            r3.file_reference = r7     // Catch:{ Exception -> 0x0f9a }
        L_0x0f3c:
            if (r9 == 0) goto L_0x0var_
            boolean r3 = r9.containsKey(r5)     // Catch:{ Exception -> 0x0f9a }
            if (r3 == 0) goto L_0x0var_
            java.lang.Object r3 = r9.get(r5)     // Catch:{ Exception -> 0x0f9a }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0f9a }
            r2.query = r3     // Catch:{ Exception -> 0x0f9a }
            int r3 = r2.flags     // Catch:{ Exception -> 0x0f9a }
            r5 = 2
            r3 = r3 | r5
            r2.flags = r3     // Catch:{ Exception -> 0x0f9a }
        L_0x0var_:
            r5 = r2
            r3 = 0
        L_0x0var_:
            if (r1 != 0) goto L_0x0var_
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0f9a }
            r1.<init>(r10)     // Catch:{ Exception -> 0x0f9a }
            r2 = 1
            r1.type = r2     // Catch:{ Exception -> 0x0f9a }
            r2 = r71
            r1.obj = r2     // Catch:{ Exception -> 0x1000 }
            r6 = r50
            r1.originalPath = r6     // Catch:{ Exception -> 0x1000 }
            r13 = r63
            r1.parentObject = r13     // Catch:{ Exception -> 0x1000 }
            if (r4 == 0) goto L_0x0f6e
            r7 = 1
            goto L_0x0f6f
        L_0x0f6e:
            r7 = 0
        L_0x0f6f:
            r1.scheduled = r7     // Catch:{ Exception -> 0x1000 }
            goto L_0x0var_
        L_0x0var_:
            r13 = r63
            r2 = r71
            r6 = r50
        L_0x0var_:
            r1.inputUploadMedia = r8     // Catch:{ Exception -> 0x1000 }
            r1.performMediaUpload = r3     // Catch:{ Exception -> 0x1000 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r12.thumbs     // Catch:{ Exception -> 0x1000 }
            boolean r7 = r7.isEmpty()     // Catch:{ Exception -> 0x1000 }
            if (r7 != 0) goto L_0x0var_
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r12.thumbs     // Catch:{ Exception -> 0x1000 }
            r8 = 0
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x1000 }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC$PhotoSize) r7     // Catch:{ Exception -> 0x1000 }
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x1000 }
            if (r8 != 0) goto L_0x0var_
            r1.photoSize = r7     // Catch:{ Exception -> 0x1000 }
            r1.locationParent = r12     // Catch:{ Exception -> 0x1000 }
        L_0x0var_:
            r1.videoEditedInfo = r14     // Catch:{ Exception -> 0x1000 }
            r8 = r4
            goto L_0x123c
        L_0x0f9a:
            r0 = move-exception
            r2 = r71
            goto L_0x1001
        L_0x0f9e:
            r0 = move-exception
            r2 = r71
            r18 = r13
            goto L_0x0b71
        L_0x0fa5:
            r7 = r58
            r46 = r3
            r18 = r13
            r12 = r38
            r6 = r50
            r8 = 6
            r13 = r63
            r3 = r2
            r2 = r71
            if (r3 != r8) goto L_0x1007
            org.telegram.tgnet.TLRPC$TL_inputMediaContact r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact     // Catch:{ Exception -> 0x1000 }
            r5.<init>()     // Catch:{ Exception -> 0x1000 }
            r7 = r43
            java.lang.String r10 = r7.phone     // Catch:{ Exception -> 0x1000 }
            r5.phone_number = r10     // Catch:{ Exception -> 0x1000 }
            java.lang.String r10 = r7.first_name     // Catch:{ Exception -> 0x1000 }
            r5.first_name = r10     // Catch:{ Exception -> 0x1000 }
            java.lang.String r10 = r7.last_name     // Catch:{ Exception -> 0x1000 }
            r5.last_name = r10     // Catch:{ Exception -> 0x1000 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r10 = r7.restriction_reason     // Catch:{ Exception -> 0x1000 }
            boolean r10 = r10.isEmpty()     // Catch:{ Exception -> 0x1000 }
            if (r10 != 0) goto L_0x0ff3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r10 = r7.restriction_reason     // Catch:{ Exception -> 0x1000 }
            r11 = 0
            java.lang.Object r10 = r10.get(r11)     // Catch:{ Exception -> 0x1000 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r10 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r10     // Catch:{ Exception -> 0x1000 }
            java.lang.String r10 = r10.text     // Catch:{ Exception -> 0x1000 }
            java.lang.String r11 = "BEGIN:VCARD"
            boolean r10 = r10.startsWith(r11)     // Catch:{ Exception -> 0x1000 }
            if (r10 == 0) goto L_0x0ff3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r7 = r7.restriction_reason     // Catch:{ Exception -> 0x1000 }
            r10 = 0
            java.lang.Object r7 = r7.get(r10)     // Catch:{ Exception -> 0x1000 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r7 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r7     // Catch:{ Exception -> 0x1000 }
            java.lang.String r7 = r7.text     // Catch:{ Exception -> 0x1000 }
            r5.vcard = r7     // Catch:{ Exception -> 0x1000 }
            goto L_0x0ff5
        L_0x0ff3:
            r5.vcard = r14     // Catch:{ Exception -> 0x1000 }
        L_0x0ff5:
            r57 = r3
            r8 = r4
            r64 = r9
            r63 = r13
            r4 = r18
            goto L_0x112d
        L_0x1000:
            r0 = move-exception
        L_0x1001:
            r1 = r0
            r5 = r4
        L_0x1003:
            r13 = r18
            goto L_0x1CLASSNAME
        L_0x1007:
            r14 = 7
            if (r3 == r14) goto L_0x113b
            r14 = 9
            if (r3 != r14) goto L_0x1010
            goto L_0x113b
        L_0x1010:
            r14 = 8
            if (r3 != r14) goto L_0x10a9
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x10a2 }
            r1.<init>()     // Catch:{ Exception -> 0x10a2 }
            java.lang.String r14 = r12.mime_type     // Catch:{ Exception -> 0x10a2 }
            r1.mime_type = r14     // Catch:{ Exception -> 0x10a2 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r14 = r12.attributes     // Catch:{ Exception -> 0x10a2 }
            r1.attributes = r14     // Catch:{ Exception -> 0x10a2 }
            if (r7 == 0) goto L_0x1032
            r1.ttl_seconds = r7     // Catch:{ Exception -> 0x10a2 }
            r14 = r18
            r14.ttl = r7     // Catch:{ Exception -> 0x10a0 }
            int r7 = r1.flags     // Catch:{ Exception -> 0x10a0 }
            r18 = 2
            r7 = r7 | 2
            r1.flags = r7     // Catch:{ Exception -> 0x10a0 }
            goto L_0x1034
        L_0x1032:
            r14 = r18
        L_0x1034:
            r57 = r9
            long r8 = r12.access_hash     // Catch:{ Exception -> 0x10a0 }
            int r7 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r7 != 0) goto L_0x1042
            r5 = r1
            r9 = r3
            r4 = 1
            r3 = r57
            goto L_0x107c
        L_0x1042:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r7 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x10a0 }
            r7.<init>()     // Catch:{ Exception -> 0x10a0 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r8 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x10a0 }
            r8.<init>()     // Catch:{ Exception -> 0x10a0 }
            r7.id = r8     // Catch:{ Exception -> 0x10a0 }
            r9 = r3
            long r3 = r12.id     // Catch:{ Exception -> 0x11b3 }
            r8.id = r3     // Catch:{ Exception -> 0x11b3 }
            long r3 = r12.access_hash     // Catch:{ Exception -> 0x11b3 }
            r8.access_hash = r3     // Catch:{ Exception -> 0x11b3 }
            byte[] r3 = r12.file_reference     // Catch:{ Exception -> 0x11b3 }
            r8.file_reference = r3     // Catch:{ Exception -> 0x11b3 }
            if (r3 != 0) goto L_0x1062
            r3 = 0
            byte[] r4 = new byte[r3]     // Catch:{ Exception -> 0x11b3 }
            r8.file_reference = r4     // Catch:{ Exception -> 0x11b3 }
        L_0x1062:
            r3 = r57
            if (r57 == 0) goto L_0x107a
            boolean r4 = r3.containsKey(r5)     // Catch:{ Exception -> 0x11b3 }
            if (r4 == 0) goto L_0x107a
            java.lang.Object r4 = r3.get(r5)     // Catch:{ Exception -> 0x11b3 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x11b3 }
            r7.query = r4     // Catch:{ Exception -> 0x11b3 }
            int r4 = r7.flags     // Catch:{ Exception -> 0x11b3 }
            r5 = 2
            r4 = r4 | r5
            r7.flags = r4     // Catch:{ Exception -> 0x11b3 }
        L_0x107a:
            r5 = r7
            r4 = 0
        L_0x107c:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r7 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x11b3 }
            r7.<init>(r10)     // Catch:{ Exception -> 0x11b3 }
            r8 = 3
            r7.type = r8     // Catch:{ Exception -> 0x11b3 }
            r7.obj = r2     // Catch:{ Exception -> 0x11b3 }
            r7.parentObject = r13     // Catch:{ Exception -> 0x11b3 }
            r7.inputUploadMedia = r1     // Catch:{ Exception -> 0x11b3 }
            r7.performMediaUpload = r4     // Catch:{ Exception -> 0x11b3 }
            r8 = r78
            if (r8 == 0) goto L_0x1092
            r1 = 1
            goto L_0x1093
        L_0x1092:
            r1 = 0
        L_0x1093:
            r7.scheduled = r1     // Catch:{ Exception -> 0x1154 }
            r64 = r3
            r3 = r4
            r1 = r7
            r57 = r9
            r63 = r13
            r4 = r14
            goto L_0x1331
        L_0x10a0:
            r0 = move-exception
            goto L_0x10a5
        L_0x10a2:
            r0 = move-exception
            r14 = r18
        L_0x10a5:
            r1 = r0
            r5 = r4
            goto L_0x1157
        L_0x10a9:
            r8 = r4
            r14 = r18
            r5 = 10
            r4 = r3
            r3 = r9
            if (r4 != r5) goto L_0x1117
            org.telegram.tgnet.TLRPC$TL_inputMediaPoll r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll     // Catch:{ Exception -> 0x1154 }
            r5.<init>()     // Catch:{ Exception -> 0x1154 }
            r7 = r39
            org.telegram.tgnet.TLRPC$Poll r9 = r7.poll     // Catch:{ Exception -> 0x1154 }
            r5.poll = r9     // Catch:{ Exception -> 0x1154 }
            if (r3 == 0) goto L_0x10f8
            java.lang.String r9 = "answers"
            boolean r9 = r3.containsKey(r9)     // Catch:{ Exception -> 0x1154 }
            if (r9 == 0) goto L_0x10f8
            java.lang.String r9 = "answers"
            java.lang.Object r9 = r3.get(r9)     // Catch:{ Exception -> 0x1154 }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ Exception -> 0x1154 }
            byte[] r9 = org.telegram.messenger.Utilities.hexToBytes(r9)     // Catch:{ Exception -> 0x1154 }
            int r10 = r9.length     // Catch:{ Exception -> 0x1154 }
            if (r10 <= 0) goto L_0x10f8
            r10 = 0
        L_0x10d7:
            int r11 = r9.length     // Catch:{ Exception -> 0x1154 }
            if (r10 >= r11) goto L_0x10ef
            java.util.ArrayList<byte[]> r11 = r5.correct_answers     // Catch:{ Exception -> 0x1154 }
            r57 = r3
            r12 = 1
            byte[] r3 = new byte[r12]     // Catch:{ Exception -> 0x1154 }
            byte r12 = r9[r10]     // Catch:{ Exception -> 0x1154 }
            r18 = 0
            r3[r18] = r12     // Catch:{ Exception -> 0x1154 }
            r11.add(r3)     // Catch:{ Exception -> 0x1154 }
            int r10 = r10 + 1
            r3 = r57
            goto L_0x10d7
        L_0x10ef:
            r57 = r3
            int r3 = r5.flags     // Catch:{ Exception -> 0x1154 }
            r9 = 1
            r3 = r3 | r9
            r5.flags = r3     // Catch:{ Exception -> 0x1154 }
            goto L_0x10fa
        L_0x10f8:
            r57 = r3
        L_0x10fa:
            org.telegram.tgnet.TLRPC$PollResults r3 = r7.results     // Catch:{ Exception -> 0x1154 }
            if (r3 == 0) goto L_0x1126
            java.lang.String r3 = r3.solution     // Catch:{ Exception -> 0x1154 }
            boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x1154 }
            if (r3 != 0) goto L_0x1126
            org.telegram.tgnet.TLRPC$PollResults r3 = r7.results     // Catch:{ Exception -> 0x1154 }
            java.lang.String r7 = r3.solution     // Catch:{ Exception -> 0x1154 }
            r5.solution = r7     // Catch:{ Exception -> 0x1154 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r3.solution_entities     // Catch:{ Exception -> 0x1154 }
            r5.solution_entities = r3     // Catch:{ Exception -> 0x1154 }
            int r3 = r5.flags     // Catch:{ Exception -> 0x1154 }
            r7 = 2
            r3 = r3 | r7
            r5.flags = r3     // Catch:{ Exception -> 0x1154 }
            goto L_0x1126
        L_0x1117:
            r57 = r3
            r3 = 11
            if (r4 != r3) goto L_0x1130
            org.telegram.tgnet.TLRPC$TL_inputMediaDice r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDice     // Catch:{ Exception -> 0x1154 }
            r5.<init>()     // Catch:{ Exception -> 0x1154 }
            r3 = r56
            r5.emoticon = r3     // Catch:{ Exception -> 0x1154 }
        L_0x1126:
            r64 = r57
            r57 = r4
            r63 = r13
            r4 = r14
        L_0x112d:
            r3 = 0
            goto L_0x1331
        L_0x1130:
            r64 = r57
            r57 = r4
            r63 = r13
            r4 = r14
            r3 = 0
            r5 = 0
            goto L_0x1331
        L_0x113b:
            r8 = r4
            r57 = r9
            r14 = r18
            r4 = r3
            r9 = r57
            if (r6 != 0) goto L_0x115a
            r3 = r68
            if (r3 != 0) goto L_0x115a
            r57 = r4
            long r3 = r12.access_hash     // Catch:{ Exception -> 0x1154 }
            int r18 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x1152
            goto L_0x115c
        L_0x1152:
            r3 = 0
            goto L_0x11a5
        L_0x1154:
            r0 = move-exception
            r1 = r0
            r5 = r8
        L_0x1157:
            r13 = r14
            goto L_0x1CLASSNAME
        L_0x115a:
            r57 = r4
        L_0x115c:
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x124c }
            r3.<init>()     // Catch:{ Exception -> 0x124c }
            if (r7 == 0) goto L_0x116d
            r3.ttl_seconds = r7     // Catch:{ Exception -> 0x1154 }
            r14.ttl = r7     // Catch:{ Exception -> 0x1154 }
            int r4 = r3.flags     // Catch:{ Exception -> 0x1154 }
            r7 = 2
            r4 = r4 | r7
            r3.flags = r4     // Catch:{ Exception -> 0x1154 }
        L_0x116d:
            if (r22 != 0) goto L_0x118b
            boolean r4 = android.text.TextUtils.isEmpty(r68)     // Catch:{ Exception -> 0x1154 }
            if (r4 != 0) goto L_0x118e
            java.lang.String r4 = r68.toLowerCase()     // Catch:{ Exception -> 0x1154 }
            java.lang.String r7 = "mp4"
            boolean r4 = r4.endsWith(r7)     // Catch:{ Exception -> 0x1154 }
            if (r4 == 0) goto L_0x118e
            if (r9 == 0) goto L_0x118b
            java.lang.String r4 = "forceDocument"
            boolean r4 = r9.containsKey(r4)     // Catch:{ Exception -> 0x1154 }
            if (r4 == 0) goto L_0x118e
        L_0x118b:
            r4 = 1
            r3.nosound_video = r4     // Catch:{ Exception -> 0x124c }
        L_0x118e:
            if (r9 == 0) goto L_0x119a
            java.lang.String r4 = "forceDocument"
            boolean r4 = r9.containsKey(r4)     // Catch:{ Exception -> 0x1154 }
            if (r4 == 0) goto L_0x119a
            r4 = 1
            goto L_0x119b
        L_0x119a:
            r4 = 0
        L_0x119b:
            r3.force_file = r4     // Catch:{ Exception -> 0x124c }
            java.lang.String r4 = r12.mime_type     // Catch:{ Exception -> 0x124c }
            r3.mime_type = r4     // Catch:{ Exception -> 0x124c }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r12.attributes     // Catch:{ Exception -> 0x124c }
            r3.attributes = r4     // Catch:{ Exception -> 0x124c }
        L_0x11a5:
            long r7 = r12.access_hash     // Catch:{ Exception -> 0x1244 }
            int r4 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x11b8
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x11b3 }
            r5 = r3
            r63 = r13
            r18 = r14
            goto L_0x11f3
        L_0x11b3:
            r0 = move-exception
            r5 = r78
            r1 = r0
            goto L_0x1157
        L_0x11b8:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x1244 }
            r4.<init>()     // Catch:{ Exception -> 0x1244 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r7 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x1244 }
            r7.<init>()     // Catch:{ Exception -> 0x1244 }
            r4.id = r7     // Catch:{ Exception -> 0x1244 }
            r63 = r13
            r18 = r14
            long r13 = r12.id     // Catch:{ Exception -> 0x1211 }
            r7.id = r13     // Catch:{ Exception -> 0x1211 }
            long r13 = r12.access_hash     // Catch:{ Exception -> 0x1211 }
            r7.access_hash = r13     // Catch:{ Exception -> 0x1211 }
            byte[] r8 = r12.file_reference     // Catch:{ Exception -> 0x1211 }
            r7.file_reference = r8     // Catch:{ Exception -> 0x1211 }
            if (r8 != 0) goto L_0x11db
            r8 = 0
            byte[] r13 = new byte[r8]     // Catch:{ Exception -> 0x1211 }
            r7.file_reference = r13     // Catch:{ Exception -> 0x1211 }
        L_0x11db:
            if (r9 == 0) goto L_0x11f1
            boolean r7 = r9.containsKey(r5)     // Catch:{ Exception -> 0x1211 }
            if (r7 == 0) goto L_0x11f1
            java.lang.Object r5 = r9.get(r5)     // Catch:{ Exception -> 0x1211 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x1211 }
            r4.query = r5     // Catch:{ Exception -> 0x1211 }
            int r5 = r4.flags     // Catch:{ Exception -> 0x1211 }
            r7 = 2
            r5 = r5 | r7
            r4.flags = r5     // Catch:{ Exception -> 0x1211 }
        L_0x11f1:
            r5 = r4
            r4 = 0
        L_0x11f3:
            if (r3 == 0) goto L_0x1237
            if (r1 != 0) goto L_0x1213
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1211 }
            r1.<init>(r10)     // Catch:{ Exception -> 0x1211 }
            r7 = 2
            r1.type = r7     // Catch:{ Exception -> 0x1211 }
            r1.obj = r2     // Catch:{ Exception -> 0x1211 }
            r1.originalPath = r6     // Catch:{ Exception -> 0x1211 }
            r13 = r63
            r1.parentObject = r13     // Catch:{ Exception -> 0x1211 }
            r8 = r78
            if (r8 == 0) goto L_0x120d
            r7 = 1
            goto L_0x120e
        L_0x120d:
            r7 = 0
        L_0x120e:
            r1.scheduled = r7     // Catch:{ Exception -> 0x1235 }
            goto L_0x1217
        L_0x1211:
            r0 = move-exception
            goto L_0x1247
        L_0x1213:
            r13 = r63
            r8 = r78
        L_0x1217:
            r1.inputUploadMedia = r3     // Catch:{ Exception -> 0x1235 }
            r1.performMediaUpload = r4     // Catch:{ Exception -> 0x1235 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r12.thumbs     // Catch:{ Exception -> 0x1235 }
            boolean r3 = r3.isEmpty()     // Catch:{ Exception -> 0x1235 }
            if (r3 != 0) goto L_0x123b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r12.thumbs     // Catch:{ Exception -> 0x1235 }
            r7 = 0
            java.lang.Object r3 = r3.get(r7)     // Catch:{ Exception -> 0x1235 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x1235 }
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x1235 }
            if (r7 != 0) goto L_0x123b
            r1.photoSize = r3     // Catch:{ Exception -> 0x1235 }
            r1.locationParent = r12     // Catch:{ Exception -> 0x1235 }
            goto L_0x123b
        L_0x1235:
            r0 = move-exception
            goto L_0x124f
        L_0x1237:
            r13 = r63
            r8 = r78
        L_0x123b:
            r3 = r4
        L_0x123c:
            r64 = r9
            r63 = r13
            r4 = r18
            goto L_0x1331
        L_0x1244:
            r0 = move-exception
            r18 = r14
        L_0x1247:
            r5 = r78
            r1 = r0
            goto L_0x1003
        L_0x124c:
            r0 = move-exception
            r18 = r14
        L_0x124f:
            r1 = r0
            r5 = r8
            goto L_0x1003
        L_0x1253:
            r7 = r58
            r57 = r2
            r46 = r3
            r8 = r4
            r18 = r13
            r5 = r49
        L_0x125e:
            r6 = r50
            r13 = r63
            r2 = r71
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x1519 }
            r3.<init>()     // Catch:{ Exception -> 0x1519 }
            if (r7 == 0) goto L_0x1278
            r3.ttl_seconds = r7     // Catch:{ Exception -> 0x1519 }
            r4 = r18
            r4.ttl = r7     // Catch:{ Exception -> 0x1517 }
            int r7 = r3.flags     // Catch:{ Exception -> 0x1517 }
            r12 = 2
            r7 = r7 | r12
            r3.flags = r7     // Catch:{ Exception -> 0x1517 }
            goto L_0x127a
        L_0x1278:
            r4 = r18
        L_0x127a:
            if (r9 == 0) goto L_0x12c1
            java.lang.String r7 = "masks"
            java.lang.Object r7 = r9.get(r7)     // Catch:{ Exception -> 0x1517 }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ Exception -> 0x1517 }
            if (r7 == 0) goto L_0x12c1
            org.telegram.tgnet.SerializedData r12 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x1517 }
            byte[] r7 = org.telegram.messenger.Utilities.hexToBytes(r7)     // Catch:{ Exception -> 0x1517 }
            r12.<init>((byte[]) r7)     // Catch:{ Exception -> 0x1517 }
            r7 = 0
            int r14 = r12.readInt32(r7)     // Catch:{ Exception -> 0x1517 }
        L_0x1294:
            if (r7 >= r14) goto L_0x12b3
            r56 = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r14 = r3.stickers     // Catch:{ Exception -> 0x1517 }
            r64 = r9
            r63 = r13
            r9 = 0
            int r13 = r12.readInt32(r9)     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$InputDocument r13 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r12, r13, r9)     // Catch:{ Exception -> 0x1517 }
            r14.add(r13)     // Catch:{ Exception -> 0x1517 }
            int r7 = r7 + 1
            r14 = r56
            r13 = r63
            r9 = r64
            goto L_0x1294
        L_0x12b3:
            r64 = r9
            r63 = r13
            int r7 = r3.flags     // Catch:{ Exception -> 0x1517 }
            r9 = 1
            r7 = r7 | r9
            r3.flags = r7     // Catch:{ Exception -> 0x1517 }
            r12.cleanup()     // Catch:{ Exception -> 0x1517 }
            goto L_0x12c5
        L_0x12c1:
            r64 = r9
            r63 = r13
        L_0x12c5:
            long r12 = r5.access_hash     // Catch:{ Exception -> 0x1517 }
            int r7 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r7 != 0) goto L_0x12ce
            r9 = r3
            r7 = 1
            goto L_0x12ef
        L_0x12ce:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r7 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x1517 }
            r7.<init>()     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r9 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x1517 }
            r9.<init>()     // Catch:{ Exception -> 0x1517 }
            r7.id = r9     // Catch:{ Exception -> 0x1517 }
            long r12 = r5.id     // Catch:{ Exception -> 0x1517 }
            r9.id = r12     // Catch:{ Exception -> 0x1517 }
            long r12 = r5.access_hash     // Catch:{ Exception -> 0x1517 }
            r9.access_hash = r12     // Catch:{ Exception -> 0x1517 }
            byte[] r12 = r5.file_reference     // Catch:{ Exception -> 0x1517 }
            r9.file_reference = r12     // Catch:{ Exception -> 0x1517 }
            if (r12 != 0) goto L_0x12ed
            r12 = 0
            byte[] r13 = new byte[r12]     // Catch:{ Exception -> 0x1517 }
            r9.file_reference = r13     // Catch:{ Exception -> 0x1517 }
        L_0x12ed:
            r9 = r7
            r7 = 0
        L_0x12ef:
            if (r1 != 0) goto L_0x1304
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1517 }
            r1.<init>(r10)     // Catch:{ Exception -> 0x1517 }
            r10 = 0
            r1.type = r10     // Catch:{ Exception -> 0x1517 }
            r1.obj = r2     // Catch:{ Exception -> 0x1517 }
            r1.originalPath = r6     // Catch:{ Exception -> 0x1517 }
            if (r8 == 0) goto L_0x1301
            r10 = 1
            goto L_0x1302
        L_0x1301:
            r10 = 0
        L_0x1302:
            r1.scheduled = r10     // Catch:{ Exception -> 0x1517 }
        L_0x1304:
            r1.inputUploadMedia = r3     // Catch:{ Exception -> 0x1517 }
            r1.performMediaUpload = r7     // Catch:{ Exception -> 0x1517 }
            r3 = r68
            if (r3 == 0) goto L_0x131d
            int r10 = r68.length()     // Catch:{ Exception -> 0x1517 }
            if (r10 <= 0) goto L_0x131d
            r13 = r52
            boolean r10 = r3.startsWith(r13)     // Catch:{ Exception -> 0x1517 }
            if (r10 == 0) goto L_0x131d
            r1.httpLocation = r3     // Catch:{ Exception -> 0x1517 }
            goto L_0x132f
        L_0x131d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r5.sizes     // Catch:{ Exception -> 0x1517 }
            int r10 = r3.size()     // Catch:{ Exception -> 0x1517 }
            r11 = 1
            int r10 = r10 - r11
            java.lang.Object r3 = r3.get(r10)     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x1517 }
            r1.photoSize = r3     // Catch:{ Exception -> 0x1517 }
            r1.locationParent = r5     // Catch:{ Exception -> 0x1517 }
        L_0x132f:
            r3 = r7
            r5 = r9
        L_0x1331:
            int r7 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r7 == 0) goto L_0x13be
            org.telegram.tgnet.TLObject r7 = r1.sendRequest     // Catch:{ Exception -> 0x1517 }
            if (r7 == 0) goto L_0x133c
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r7 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r7     // Catch:{ Exception -> 0x1517 }
            goto L_0x1365
        L_0x133c:
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia     // Catch:{ Exception -> 0x1517 }
            r7.<init>()     // Catch:{ Exception -> 0x1517 }
            r9 = r46
            r7.peer = r9     // Catch:{ Exception -> 0x1517 }
            boolean r9 = r4.silent     // Catch:{ Exception -> 0x1517 }
            r7.silent = r9     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r9 = r4.reply_to     // Catch:{ Exception -> 0x1517 }
            if (r9 == 0) goto L_0x1359
            int r9 = r9.reply_to_msg_id     // Catch:{ Exception -> 0x1517 }
            if (r9 == 0) goto L_0x1359
            int r10 = r7.flags     // Catch:{ Exception -> 0x1517 }
            r11 = 1
            r10 = r10 | r11
            r7.flags = r10     // Catch:{ Exception -> 0x1517 }
            r7.reply_to_msg_id = r9     // Catch:{ Exception -> 0x1517 }
        L_0x1359:
            if (r8 == 0) goto L_0x1363
            r7.schedule_date = r8     // Catch:{ Exception -> 0x1517 }
            int r9 = r7.flags     // Catch:{ Exception -> 0x1517 }
            r9 = r9 | 1024(0x400, float:1.435E-42)
            r7.flags = r9     // Catch:{ Exception -> 0x1517 }
        L_0x1363:
            r1.sendRequest = r7     // Catch:{ Exception -> 0x1517 }
        L_0x1365:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r9 = r1.messageObjects     // Catch:{ Exception -> 0x1517 }
            r9.add(r2)     // Catch:{ Exception -> 0x1517 }
            java.util.ArrayList<java.lang.Object> r9 = r1.parentObjects     // Catch:{ Exception -> 0x1517 }
            r10 = r63
            r9.add(r10)     // Catch:{ Exception -> 0x1517 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r1.locations     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$PhotoSize r11 = r1.photoSize     // Catch:{ Exception -> 0x1517 }
            r9.add(r11)     // Catch:{ Exception -> 0x1517 }
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo> r9 = r1.videoEditedInfos     // Catch:{ Exception -> 0x1517 }
            org.telegram.messenger.VideoEditedInfo r11 = r1.videoEditedInfo     // Catch:{ Exception -> 0x1517 }
            r9.add(r11)     // Catch:{ Exception -> 0x1517 }
            java.util.ArrayList<java.lang.String> r9 = r1.httpLocations     // Catch:{ Exception -> 0x1517 }
            java.lang.String r11 = r1.httpLocation     // Catch:{ Exception -> 0x1517 }
            r9.add(r11)     // Catch:{ Exception -> 0x1517 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputMedia> r9 = r1.inputMedias     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$InputMedia r11 = r1.inputUploadMedia     // Catch:{ Exception -> 0x1517 }
            r9.add(r11)     // Catch:{ Exception -> 0x1517 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r9 = r1.messages     // Catch:{ Exception -> 0x1517 }
            r9.add(r4)     // Catch:{ Exception -> 0x1517 }
            java.util.ArrayList<java.lang.String> r9 = r1.originalPaths     // Catch:{ Exception -> 0x1517 }
            r9.add(r6)     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r9 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia     // Catch:{ Exception -> 0x1517 }
            r9.<init>()     // Catch:{ Exception -> 0x1517 }
            long r11 = r4.random_id     // Catch:{ Exception -> 0x1517 }
            r9.random_id = r11     // Catch:{ Exception -> 0x1517 }
            r9.media = r5     // Catch:{ Exception -> 0x1517 }
            r11 = r42
            r9.message = r11     // Catch:{ Exception -> 0x1517 }
            r12 = r74
            if (r12 == 0) goto L_0x13b8
            boolean r5 = r74.isEmpty()     // Catch:{ Exception -> 0x1517 }
            if (r5 != 0) goto L_0x13b8
            r9.entities = r12     // Catch:{ Exception -> 0x1517 }
            int r5 = r9.flags     // Catch:{ Exception -> 0x1517 }
            r11 = 1
            r5 = r5 | r11
            r9.flags = r5     // Catch:{ Exception -> 0x1517 }
        L_0x13b8:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r5 = r7.multi_media     // Catch:{ Exception -> 0x1517 }
            r5.add(r9)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1418
        L_0x13be:
            r10 = r63
            r12 = r74
            r11 = r42
            r9 = r46
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r7 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia     // Catch:{ Exception -> 0x1517 }
            r7.<init>()     // Catch:{ Exception -> 0x1517 }
            r7.peer = r9     // Catch:{ Exception -> 0x1517 }
            boolean r9 = r4.silent     // Catch:{ Exception -> 0x1517 }
            r7.silent = r9     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r9 = r4.reply_to     // Catch:{ Exception -> 0x1517 }
            if (r9 == 0) goto L_0x13e1
            int r9 = r9.reply_to_msg_id     // Catch:{ Exception -> 0x1517 }
            if (r9 == 0) goto L_0x13e1
            int r13 = r7.flags     // Catch:{ Exception -> 0x1517 }
            r14 = 1
            r13 = r13 | r14
            r7.flags = r13     // Catch:{ Exception -> 0x1517 }
            r7.reply_to_msg_id = r9     // Catch:{ Exception -> 0x1517 }
        L_0x13e1:
            long r13 = r4.random_id     // Catch:{ Exception -> 0x1517 }
            r7.random_id = r13     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$Peer r9 = r4.from_id     // Catch:{ Exception -> 0x1517 }
            if (r9 == 0) goto L_0x13f5
            org.telegram.messenger.MessagesController r9 = r55.getMessagesController()     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$Peer r13 = r4.from_id     // Catch:{ Exception -> 0x1517 }
            org.telegram.tgnet.TLRPC$InputPeer r9 = r9.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r13)     // Catch:{ Exception -> 0x1517 }
            r7.send_as = r9     // Catch:{ Exception -> 0x1517 }
        L_0x13f5:
            r7.media = r5     // Catch:{ Exception -> 0x1517 }
            r7.message = r11     // Catch:{ Exception -> 0x1517 }
            if (r12 == 0) goto L_0x140a
            boolean r5 = r74.isEmpty()     // Catch:{ Exception -> 0x1517 }
            if (r5 != 0) goto L_0x140a
            r7.entities = r12     // Catch:{ Exception -> 0x1517 }
            int r5 = r7.flags     // Catch:{ Exception -> 0x1517 }
            r9 = 8
            r5 = r5 | r9
            r7.flags = r5     // Catch:{ Exception -> 0x1517 }
        L_0x140a:
            if (r8 == 0) goto L_0x1414
            r7.schedule_date = r8     // Catch:{ Exception -> 0x1517 }
            int r5 = r7.flags     // Catch:{ Exception -> 0x1517 }
            r5 = r5 | 1024(0x400, float:1.435E-42)
            r7.flags = r5     // Catch:{ Exception -> 0x1517 }
        L_0x1414:
            if (r1 == 0) goto L_0x1418
            r1.sendRequest = r7     // Catch:{ Exception -> 0x1517 }
        L_0x1418:
            int r5 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r5 == 0) goto L_0x1421
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x1421:
            r9 = r57
            r5 = 1
            if (r9 != r5) goto L_0x1441
            r3 = 0
            if (r8 == 0) goto L_0x142b
            r5 = 1
            goto L_0x142c
        L_0x142b:
            r5 = 0
        L_0x142c:
            r56 = r55
            r57 = r7
            r58 = r2
            r59 = r3
            r60 = r1
            r61 = r10
            r62 = r64
            r63 = r5
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x1441:
            r5 = 2
            if (r9 != r5) goto L_0x1469
            if (r3 == 0) goto L_0x144b
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x144b:
            r3 = 0
            r5 = 1
            if (r8 == 0) goto L_0x1451
            r9 = 1
            goto L_0x1452
        L_0x1451:
            r9 = 0
        L_0x1452:
            r56 = r55
            r57 = r7
            r58 = r2
            r59 = r6
            r60 = r3
            r61 = r5
            r62 = r1
            r63 = r10
            r65 = r9
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63, r64, r65)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x1469:
            r5 = 3
            if (r9 != r5) goto L_0x148d
            if (r3 == 0) goto L_0x1473
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x1473:
            if (r8 == 0) goto L_0x1477
            r3 = 1
            goto L_0x1478
        L_0x1477:
            r3 = 0
        L_0x1478:
            r56 = r55
            r57 = r7
            r58 = r2
            r59 = r6
            r60 = r1
            r61 = r10
            r62 = r64
            r63 = r3
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x148d:
            r5 = 6
            if (r9 != r5) goto L_0x14aa
            if (r8 == 0) goto L_0x1494
            r3 = 1
            goto L_0x1495
        L_0x1494:
            r3 = 0
        L_0x1495:
            r56 = r55
            r57 = r7
            r58 = r2
            r59 = r6
            r60 = r1
            r61 = r10
            r62 = r64
            r63 = r3
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x14aa:
            r5 = 7
            if (r9 != r5) goto L_0x14d0
            if (r3 == 0) goto L_0x14b6
            if (r1 == 0) goto L_0x14b6
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x14b6:
            if (r8 == 0) goto L_0x14ba
            r3 = 1
            goto L_0x14bb
        L_0x14ba:
            r3 = 0
        L_0x14bb:
            r56 = r55
            r57 = r7
            r58 = r2
            r59 = r6
            r60 = r1
            r61 = r10
            r62 = r64
            r63 = r3
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x14d0:
            r5 = 8
            if (r9 != r5) goto L_0x14f5
            if (r3 == 0) goto L_0x14db
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x14db:
            if (r8 == 0) goto L_0x14df
            r3 = 1
            goto L_0x14e0
        L_0x14df:
            r3 = 0
        L_0x14e0:
            r56 = r55
            r57 = r7
            r58 = r2
            r59 = r6
            r60 = r1
            r61 = r10
            r62 = r64
            r63 = r3
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x14f5:
            r3 = 10
            if (r9 == r3) goto L_0x14fd
            r3 = 11
            if (r9 != r3) goto L_0x1CLASSNAME
        L_0x14fd:
            if (r8 == 0) goto L_0x1501
            r3 = 1
            goto L_0x1502
        L_0x1501:
            r3 = 0
        L_0x1502:
            r56 = r55
            r57 = r7
            r58 = r2
            r59 = r6
            r60 = r1
            r61 = r10
            r62 = r64
            r63 = r3
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x1517 }
            goto L_0x1CLASSNAME
        L_0x1517:
            r0 = move-exception
            goto L_0x151c
        L_0x1519:
            r0 = move-exception
            r4 = r18
        L_0x151c:
            r1 = r0
            r13 = r4
            r5 = r8
            goto L_0x1CLASSNAME
        L_0x1521:
            r53 = r57
            r54 = r63
            r4 = r74
            r64 = r9
            r5 = r18
            r14 = r36
            r12 = r38
            r3 = r42
            r7 = r49
            r35 = r52
            r9 = r2
            r2 = r71
            int r6 = r8.layer     // Catch:{ Exception -> 0x1ad6 }
            int r6 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r6)     // Catch:{ Exception -> 0x1ad6 }
            r10 = 73
            if (r6 < r10) goto L_0x155f
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1ad6 }
            r6.<init>()     // Catch:{ Exception -> 0x1ad6 }
            int r10 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r10 == 0) goto L_0x155a
            r10 = r47
            r6.grouped_id = r10     // Catch:{ Exception -> 0x1ad6 }
            r56 = r1
            int r1 = r6.flags     // Catch:{ Exception -> 0x1ad6 }
            r18 = 131072(0x20000, float:1.83671E-40)
            r1 = r1 | r18
            r6.flags = r1     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x1568
        L_0x155a:
            r56 = r1
            r10 = r47
            goto L_0x1568
        L_0x155f:
            r56 = r1
            r10 = r47
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x1ad6 }
            r6.<init>()     // Catch:{ Exception -> 0x1ad6 }
        L_0x1568:
            int r1 = r13.ttl     // Catch:{ Exception -> 0x1ad6 }
            r6.ttl = r1     // Catch:{ Exception -> 0x1ad6 }
            if (r4 == 0) goto L_0x157c
            boolean r1 = r74.isEmpty()     // Catch:{ Exception -> 0x1ad6 }
            if (r1 != 0) goto L_0x157c
            r6.entities = r4     // Catch:{ Exception -> 0x1ad6 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x1ad6 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x1ad6 }
        L_0x157c:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r1 = r13.reply_to     // Catch:{ Exception -> 0x1ad6 }
            r47 = r10
            if (r1 == 0) goto L_0x1591
            long r10 = r1.reply_to_random_id     // Catch:{ Exception -> 0x1ad6 }
            int r1 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1591
            r6.reply_to_random_id = r10     // Catch:{ Exception -> 0x1ad6 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x1ad6 }
            r4 = 8
            r1 = r1 | r4
            r6.flags = r1     // Catch:{ Exception -> 0x1ad6 }
        L_0x1591:
            boolean r1 = r13.silent     // Catch:{ Exception -> 0x1ad6 }
            r6.silent = r1     // Catch:{ Exception -> 0x1ad6 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x1ad6 }
            r1 = r1 | 512(0x200, float:7.175E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x1ad6 }
            r1 = r64
            if (r64 == 0) goto L_0x15b5
            r10 = r45
            java.lang.Object r4 = r1.get(r10)     // Catch:{ Exception -> 0x1ad6 }
            if (r4 == 0) goto L_0x15b5
            java.lang.Object r4 = r1.get(r10)     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x1ad6 }
            r6.via_bot_name = r4     // Catch:{ Exception -> 0x1ad6 }
            int r4 = r6.flags     // Catch:{ Exception -> 0x1ad6 }
            r4 = r4 | 2048(0x800, float:2.87E-42)
            r6.flags = r4     // Catch:{ Exception -> 0x1ad6 }
        L_0x15b5:
            long r10 = r13.random_id     // Catch:{ Exception -> 0x1ad6 }
            r6.random_id = r10     // Catch:{ Exception -> 0x1ad6 }
            r6.message = r14     // Catch:{ Exception -> 0x1ad6 }
            r4 = 1
            if (r9 != r4) goto L_0x160e
            boolean r1 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x1ad6 }
            if (r1 == 0) goto L_0x15da
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue     // Catch:{ Exception -> 0x1ad6 }
            r1.<init>()     // Catch:{ Exception -> 0x1ad6 }
            r6.media = r1     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r3 = r5.address     // Catch:{ Exception -> 0x1ad6 }
            r1.address = r3     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r3 = r5.title     // Catch:{ Exception -> 0x1ad6 }
            r1.title = r3     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r3 = r5.provider     // Catch:{ Exception -> 0x1ad6 }
            r1.provider = r3     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r3 = r5.venue_id     // Catch:{ Exception -> 0x1ad6 }
            r1.venue_id = r3     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x15e1
        L_0x15da:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint     // Catch:{ Exception -> 0x1ad6 }
            r1.<init>()     // Catch:{ Exception -> 0x1ad6 }
            r6.media = r1     // Catch:{ Exception -> 0x1ad6 }
        L_0x15e1:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$GeoPoint r3 = r5.geo     // Catch:{ Exception -> 0x1ad6 }
            double r4 = r3.lat     // Catch:{ Exception -> 0x1ad6 }
            r1.lat = r4     // Catch:{ Exception -> 0x1ad6 }
            double r3 = r3._long     // Catch:{ Exception -> 0x1ad6 }
            r1._long = r3     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.messenger.SecretChatHelper r1 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner     // Catch:{ Exception -> 0x1ad6 }
            r4 = 0
            r5 = 0
            r58 = r1
            r59 = r6
            r60 = r3
            r61 = r8
            r62 = r4
            r63 = r5
            r64 = r2
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1ad6 }
            r11 = r66
            r57 = r9
            r5 = r50
            goto L_0x17f5
        L_0x160e:
            r4 = 2
            if (r9 == r4) goto L_0x1961
            r4 = 9
            if (r9 != r4) goto L_0x162a
            if (r7 == 0) goto L_0x162a
            r11 = r66
            r14 = r3
            r57 = r9
            r18 = r13
            r10 = r35
            r4 = r44
            r5 = r50
            r13 = r54
            r9 = r56
            goto L_0x1974
        L_0x162a:
            r4 = 3
            if (r9 != r4) goto L_0x1742
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r12.thumbs     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r15.getThumbForSecretChat(r4)     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r4)     // Catch:{ Exception -> 0x1ad6 }
            boolean r5 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r12)     // Catch:{ Exception -> 0x1ad6 }
            if (r5 != 0) goto L_0x165d
            boolean r5 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r12)     // Catch:{ Exception -> 0x1ad6 }
            if (r5 == 0) goto L_0x1643
            goto L_0x165d
        L_0x1643:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo     // Catch:{ Exception -> 0x1ad6 }
            r5.<init>()     // Catch:{ Exception -> 0x1ad6 }
            r6.media = r5     // Catch:{ Exception -> 0x1ad6 }
            if (r4 == 0) goto L_0x1655
            byte[] r7 = r4.bytes     // Catch:{ Exception -> 0x1ad6 }
            if (r7 == 0) goto L_0x1655
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r5     // Catch:{ Exception -> 0x1ad6 }
            r5.thumb = r7     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x167a
        L_0x1655:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r5     // Catch:{ Exception -> 0x1ad6 }
            r7 = 0
            byte[] r10 = new byte[r7]     // Catch:{ Exception -> 0x1ad6 }
            r5.thumb = r10     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x167a
        L_0x165d:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1ad6 }
            r5.<init>()     // Catch:{ Exception -> 0x1ad6 }
            r6.media = r5     // Catch:{ Exception -> 0x1ad6 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r12.attributes     // Catch:{ Exception -> 0x1ad6 }
            r5.attributes = r7     // Catch:{ Exception -> 0x1ad6 }
            if (r4 == 0) goto L_0x1673
            byte[] r7 = r4.bytes     // Catch:{ Exception -> 0x1ad6 }
            if (r7 == 0) goto L_0x1673
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1ad6 }
            r5.thumb = r7     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x167a
        L_0x1673:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1ad6 }
            r7 = 0
            byte[] r10 = new byte[r7]     // Catch:{ Exception -> 0x1ad6 }
            r5.thumb = r10     // Catch:{ Exception -> 0x1ad6 }
        L_0x167a:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            r5.caption = r3     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r3 = "video/mp4"
            r5.mime_type = r3     // Catch:{ Exception -> 0x1ad6 }
            long r10 = r12.size     // Catch:{ Exception -> 0x1ad6 }
            r5.size = r10     // Catch:{ Exception -> 0x1ad6 }
            r3 = 0
        L_0x1687:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r12.attributes     // Catch:{ Exception -> 0x1ad6 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x1ad6 }
            if (r3 >= r5) goto L_0x16ad
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r12.attributes     // Catch:{ Exception -> 0x1ad6 }
            java.lang.Object r5 = r5.get(r3)     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r5     // Catch:{ Exception -> 0x1ad6 }
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x1ad6 }
            if (r7 == 0) goto L_0x16aa
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            int r7 = r5.w     // Catch:{ Exception -> 0x1ad6 }
            r3.w = r7     // Catch:{ Exception -> 0x1ad6 }
            int r7 = r5.h     // Catch:{ Exception -> 0x1ad6 }
            r3.h = r7     // Catch:{ Exception -> 0x1ad6 }
            int r5 = r5.duration     // Catch:{ Exception -> 0x1ad6 }
            r3.duration = r5     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x16ad
        L_0x16aa:
            int r3 = r3 + 1
            goto L_0x1687
        L_0x16ad:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            int r5 = r4.h     // Catch:{ Exception -> 0x1ad6 }
            r3.thumb_h = r5     // Catch:{ Exception -> 0x1ad6 }
            int r4 = r4.w     // Catch:{ Exception -> 0x1ad6 }
            r3.thumb_w = r4     // Catch:{ Exception -> 0x1ad6 }
            byte[] r3 = r12.key     // Catch:{ Exception -> 0x1ad6 }
            if (r3 == 0) goto L_0x16f6
            int r3 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x16c0
            goto L_0x16f6
        L_0x16c0:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1ad6 }
            r1.<init>()     // Catch:{ Exception -> 0x1ad6 }
            long r3 = r12.id     // Catch:{ Exception -> 0x1ad6 }
            r1.id = r3     // Catch:{ Exception -> 0x1ad6 }
            long r3 = r12.access_hash     // Catch:{ Exception -> 0x1ad6 }
            r1.access_hash = r3     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            byte[] r4 = r12.key     // Catch:{ Exception -> 0x1ad6 }
            r3.key = r4     // Catch:{ Exception -> 0x1ad6 }
            byte[] r4 = r12.iv     // Catch:{ Exception -> 0x1ad6 }
            r3.iv = r4     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.messenger.SecretChatHelper r3 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner     // Catch:{ Exception -> 0x1ad6 }
            r5 = 0
            r58 = r3
            r59 = r6
            r60 = r4
            r61 = r8
            r62 = r1
            r63 = r5
            r64 = r2
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1ad6 }
            r1 = r56
            r11 = r66
            r5 = r50
            goto L_0x173e
        L_0x16f6:
            if (r56 != 0) goto L_0x172d
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1ad6 }
            r11 = r66
            r3.<init>(r11)     // Catch:{ Exception -> 0x1ad6 }
            r3.encryptedChat = r8     // Catch:{ Exception -> 0x1ad6 }
            r4 = 1
            r3.type = r4     // Catch:{ Exception -> 0x1ad6 }
            r3.sendEncryptedRequest = r6     // Catch:{ Exception -> 0x1ad6 }
            r5 = r50
            r3.originalPath = r5     // Catch:{ Exception -> 0x1ad6 }
            r3.obj = r2     // Catch:{ Exception -> 0x1ad6 }
            if (r1 == 0) goto L_0x171d
            r4 = r44
            boolean r7 = r1.containsKey(r4)     // Catch:{ Exception -> 0x1ad6 }
            if (r7 == 0) goto L_0x171d
            java.lang.Object r1 = r1.get(r4)     // Catch:{ Exception -> 0x1ad6 }
            r3.parentObject = r1     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x1721
        L_0x171d:
            r14 = r54
            r3.parentObject = r14     // Catch:{ Exception -> 0x1ad6 }
        L_0x1721:
            r1 = 1
            r3.performMediaUpload = r1     // Catch:{ Exception -> 0x1ad6 }
            if (r78 == 0) goto L_0x1728
            r1 = 1
            goto L_0x1729
        L_0x1728:
            r1 = 0
        L_0x1729:
            r3.scheduled = r1     // Catch:{ Exception -> 0x1ad6 }
            r1 = r3
            goto L_0x1733
        L_0x172d:
            r11 = r66
            r5 = r50
            r1 = r56
        L_0x1733:
            r3 = r53
            r1.videoEditedInfo = r3     // Catch:{ Exception -> 0x1ad6 }
            int r3 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x173e
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1ad6 }
        L_0x173e:
            r57 = r9
            goto L_0x1882
        L_0x1742:
            r7 = r12
            r4 = r44
            r5 = r50
            r14 = r54
            r10 = 6
            r11 = r66
            if (r9 != r10) goto L_0x1782
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact     // Catch:{ Exception -> 0x1ad6 }
            r1.<init>()     // Catch:{ Exception -> 0x1ad6 }
            r6.media = r1     // Catch:{ Exception -> 0x1ad6 }
            r7 = r43
            java.lang.String r3 = r7.phone     // Catch:{ Exception -> 0x1ad6 }
            r1.phone_number = r3     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r3 = r7.first_name     // Catch:{ Exception -> 0x1ad6 }
            r1.first_name = r3     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r3 = r7.last_name     // Catch:{ Exception -> 0x1ad6 }
            r1.last_name = r3     // Catch:{ Exception -> 0x1ad6 }
            long r3 = r7.id     // Catch:{ Exception -> 0x1ad6 }
            r1.user_id = r3     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.messenger.SecretChatHelper r1 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner     // Catch:{ Exception -> 0x1ad6 }
            r4 = 0
            r7 = 0
            r58 = r1
            r59 = r6
            r60 = r3
            r61 = r8
            r62 = r4
            r63 = r7
            r64 = r2
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x17f3
        L_0x1782:
            r10 = 7
            if (r9 == r10) goto L_0x17f9
            r10 = 9
            if (r9 != r10) goto L_0x178c
            if (r7 == 0) goto L_0x178c
            goto L_0x17f9
        L_0x178c:
            r1 = 8
            if (r9 != r1) goto L_0x17f3
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1ad6 }
            r1.<init>(r11)     // Catch:{ Exception -> 0x1ad6 }
            r1.encryptedChat = r8     // Catch:{ Exception -> 0x1ad6 }
            r1.sendEncryptedRequest = r6     // Catch:{ Exception -> 0x1ad6 }
            r1.obj = r2     // Catch:{ Exception -> 0x1ad6 }
            r4 = 3
            r1.type = r4     // Catch:{ Exception -> 0x1ad6 }
            r1.parentObject = r14     // Catch:{ Exception -> 0x1ad6 }
            r4 = 1
            r1.performMediaUpload = r4     // Catch:{ Exception -> 0x1ad6 }
            if (r78 == 0) goto L_0x17a7
            r4 = 1
            goto L_0x17a8
        L_0x17a7:
            r4 = 0
        L_0x17a8:
            r1.scheduled = r4     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1ad6 }
            r4.<init>()     // Catch:{ Exception -> 0x1ad6 }
            r6.media = r4     // Catch:{ Exception -> 0x1ad6 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r7.attributes     // Catch:{ Exception -> 0x1ad6 }
            r4.attributes = r8     // Catch:{ Exception -> 0x1ad6 }
            r4.caption = r3     // Catch:{ Exception -> 0x1ad6 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r7.thumbs     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r15.getThumbForSecretChat(r3)     // Catch:{ Exception -> 0x1ad6 }
            if (r3 == 0) goto L_0x17d4
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            r8 = r4
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r8 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r8     // Catch:{ Exception -> 0x1ad6 }
            byte[] r10 = r3.bytes     // Catch:{ Exception -> 0x1ad6 }
            r8.thumb = r10     // Catch:{ Exception -> 0x1ad6 }
            int r8 = r3.h     // Catch:{ Exception -> 0x1ad6 }
            r4.thumb_h = r8     // Catch:{ Exception -> 0x1ad6 }
            int r3 = r3.w     // Catch:{ Exception -> 0x1ad6 }
            r4.thumb_w = r3     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x17e2
        L_0x17d4:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            r4 = r3
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r4 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r4     // Catch:{ Exception -> 0x1ad6 }
            r8 = 0
            byte[] r10 = new byte[r8]     // Catch:{ Exception -> 0x1ad6 }
            r4.thumb = r10     // Catch:{ Exception -> 0x1ad6 }
            r3.thumb_h = r8     // Catch:{ Exception -> 0x1ad6 }
            r3.thumb_w = r8     // Catch:{ Exception -> 0x1ad6 }
        L_0x17e2:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r4 = r7.mime_type     // Catch:{ Exception -> 0x1ad6 }
            r3.mime_type = r4     // Catch:{ Exception -> 0x1ad6 }
            long r7 = r7.size     // Catch:{ Exception -> 0x1ad6 }
            r3.size = r7     // Catch:{ Exception -> 0x1ad6 }
            r1.originalPath = r5     // Catch:{ Exception -> 0x1ad6 }
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x173e
        L_0x17f3:
            r57 = r9
        L_0x17f5:
            r9 = r56
            goto L_0x1881
        L_0x17f9:
            r57 = r9
            long r9 = r7.access_hash     // Catch:{ Exception -> 0x195c }
            int r18 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r18 == 0) goto L_0x1886
            boolean r9 = org.telegram.messenger.MessageObject.isStickerDocument(r7)     // Catch:{ Exception -> 0x1ad6 }
            if (r9 != 0) goto L_0x180e
            r9 = 1
            boolean r10 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r7, r9)     // Catch:{ Exception -> 0x1ad6 }
            if (r10 == 0) goto L_0x1886
        L_0x180e:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument     // Catch:{ Exception -> 0x1ad6 }
            r1.<init>()     // Catch:{ Exception -> 0x1ad6 }
            r6.media = r1     // Catch:{ Exception -> 0x1ad6 }
            long r3 = r7.id     // Catch:{ Exception -> 0x1ad6 }
            r1.id = r3     // Catch:{ Exception -> 0x1ad6 }
            int r3 = r7.date     // Catch:{ Exception -> 0x1ad6 }
            r1.date = r3     // Catch:{ Exception -> 0x1ad6 }
            long r3 = r7.access_hash     // Catch:{ Exception -> 0x1ad6 }
            r1.access_hash = r3     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r3 = r7.mime_type     // Catch:{ Exception -> 0x1ad6 }
            r1.mime_type = r3     // Catch:{ Exception -> 0x1ad6 }
            long r3 = r7.size     // Catch:{ Exception -> 0x1ad6 }
            r1.size = r3     // Catch:{ Exception -> 0x1ad6 }
            int r3 = r7.dc_id     // Catch:{ Exception -> 0x1ad6 }
            r1.dc_id = r3     // Catch:{ Exception -> 0x1ad6 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r7.attributes     // Catch:{ Exception -> 0x1ad6 }
            r1.attributes = r3     // Catch:{ Exception -> 0x1ad6 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r7.thumbs     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r15.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x1ad6 }
            if (r1 == 0) goto L_0x1840
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r3 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r3     // Catch:{ Exception -> 0x1ad6 }
            r3.thumb = r1     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x1855
        L_0x1840:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r3 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty     // Catch:{ Exception -> 0x1ad6 }
            r3.<init>()     // Catch:{ Exception -> 0x1ad6 }
            r1.thumb = r3     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r6.media     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r1 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r1     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r1.thumb     // Catch:{ Exception -> 0x1ad6 }
            java.lang.String r3 = "s"
            r1.type = r3     // Catch:{ Exception -> 0x1ad6 }
        L_0x1855:
            if (r56 == 0) goto L_0x1866
            r9 = r56
            int r1 = r9.type     // Catch:{ Exception -> 0x1ad6 }
            r3 = 5
            if (r1 != r3) goto L_0x1868
            r9.sendEncryptedRequest = r6     // Catch:{ Exception -> 0x1ad6 }
            r9.obj = r2     // Catch:{ Exception -> 0x1ad6 }
            r15.performSendDelayedMessage(r9)     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x1881
        L_0x1866:
            r9 = r56
        L_0x1868:
            org.telegram.messenger.SecretChatHelper r1 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner     // Catch:{ Exception -> 0x1ad6 }
            r4 = 0
            r7 = 0
            r58 = r1
            r59 = r6
            r60 = r3
            r61 = r8
            r62 = r4
            r63 = r7
            r64 = r2
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1ad6 }
        L_0x1881:
            r1 = r9
        L_0x1882:
            r18 = r13
            goto L_0x1a6c
        L_0x1886:
            r9 = r56
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x195c }
            r10.<init>()     // Catch:{ Exception -> 0x195c }
            r6.media = r10     // Catch:{ Exception -> 0x195c }
            r18 = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r7.attributes     // Catch:{ Exception -> 0x1211 }
            r10.attributes = r13     // Catch:{ Exception -> 0x1211 }
            r10.caption = r3     // Catch:{ Exception -> 0x1211 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r7.thumbs     // Catch:{ Exception -> 0x1211 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r15.getThumbForSecretChat(r3)     // Catch:{ Exception -> 0x1211 }
            if (r3 == 0) goto L_0x18b6
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x1211 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r10 = r6.media     // Catch:{ Exception -> 0x1211 }
            r13 = r10
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r13 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r13     // Catch:{ Exception -> 0x1211 }
            r63 = r14
            byte[] r14 = r3.bytes     // Catch:{ Exception -> 0x1211 }
            r13.thumb = r14     // Catch:{ Exception -> 0x1211 }
            int r13 = r3.h     // Catch:{ Exception -> 0x1211 }
            r10.thumb_h = r13     // Catch:{ Exception -> 0x1211 }
            int r3 = r3.w     // Catch:{ Exception -> 0x1211 }
            r10.thumb_w = r3     // Catch:{ Exception -> 0x1211 }
            goto L_0x18c6
        L_0x18b6:
            r63 = r14
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x1211 }
            r10 = r3
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r10 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r10     // Catch:{ Exception -> 0x1211 }
            r13 = 0
            byte[] r14 = new byte[r13]     // Catch:{ Exception -> 0x1211 }
            r10.thumb = r14     // Catch:{ Exception -> 0x1211 }
            r3.thumb_h = r13     // Catch:{ Exception -> 0x1211 }
            r3.thumb_w = r13     // Catch:{ Exception -> 0x1211 }
        L_0x18c6:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x1211 }
            long r13 = r7.size     // Catch:{ Exception -> 0x1211 }
            r3.size = r13     // Catch:{ Exception -> 0x1211 }
            java.lang.String r10 = r7.mime_type     // Catch:{ Exception -> 0x1211 }
            r3.mime_type = r10     // Catch:{ Exception -> 0x1211 }
            byte[] r3 = r7.key     // Catch:{ Exception -> 0x1211 }
            if (r3 == 0) goto L_0x190b
            int r3 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x18d9
            goto L_0x190b
        L_0x18d9:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1211 }
            r1.<init>()     // Catch:{ Exception -> 0x1211 }
            long r3 = r7.id     // Catch:{ Exception -> 0x1211 }
            r1.id = r3     // Catch:{ Exception -> 0x1211 }
            long r3 = r7.access_hash     // Catch:{ Exception -> 0x1211 }
            r1.access_hash = r3     // Catch:{ Exception -> 0x1211 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r6.media     // Catch:{ Exception -> 0x1211 }
            byte[] r4 = r7.key     // Catch:{ Exception -> 0x1211 }
            r3.key = r4     // Catch:{ Exception -> 0x1211 }
            byte[] r4 = r7.iv     // Catch:{ Exception -> 0x1211 }
            r3.iv = r4     // Catch:{ Exception -> 0x1211 }
            org.telegram.messenger.SecretChatHelper r3 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1211 }
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner     // Catch:{ Exception -> 0x1211 }
            r7 = 0
            r58 = r3
            r59 = r6
            r60 = r4
            r61 = r8
            r62 = r1
            r63 = r7
            r64 = r2
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1211 }
        L_0x1908:
            r1 = r9
            goto L_0x1a6c
        L_0x190b:
            if (r9 != 0) goto L_0x193e
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1211 }
            r3.<init>(r11)     // Catch:{ Exception -> 0x1211 }
            r3.encryptedChat = r8     // Catch:{ Exception -> 0x1211 }
            r7 = 2
            r3.type = r7     // Catch:{ Exception -> 0x1211 }
            r3.sendEncryptedRequest = r6     // Catch:{ Exception -> 0x1211 }
            r3.originalPath = r5     // Catch:{ Exception -> 0x1211 }
            r3.obj = r2     // Catch:{ Exception -> 0x1211 }
            if (r1 == 0) goto L_0x192c
            boolean r7 = r1.containsKey(r4)     // Catch:{ Exception -> 0x1211 }
            if (r7 == 0) goto L_0x192c
            java.lang.Object r1 = r1.get(r4)     // Catch:{ Exception -> 0x1211 }
            r3.parentObject = r1     // Catch:{ Exception -> 0x1211 }
            goto L_0x1930
        L_0x192c:
            r13 = r63
            r3.parentObject = r13     // Catch:{ Exception -> 0x1211 }
        L_0x1930:
            r1 = 1
            r3.performMediaUpload = r1     // Catch:{ Exception -> 0x1211 }
            if (r78 == 0) goto L_0x1937
            r1 = 1
            goto L_0x1938
        L_0x1937:
            r1 = 0
        L_0x1938:
            r3.scheduled = r1     // Catch:{ Exception -> 0x1211 }
            r1 = r3
            r3 = r68
            goto L_0x1941
        L_0x193e:
            r3 = r68
            r1 = r9
        L_0x1941:
            if (r3 == 0) goto L_0x1953
            int r4 = r68.length()     // Catch:{ Exception -> 0x1211 }
            if (r4 <= 0) goto L_0x1953
            r10 = r35
            boolean r4 = r3.startsWith(r10)     // Catch:{ Exception -> 0x1211 }
            if (r4 == 0) goto L_0x1953
            r1.httpLocation = r3     // Catch:{ Exception -> 0x1211 }
        L_0x1953:
            int r3 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x1a6c
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1211 }
            goto L_0x1a6c
        L_0x195c:
            r0 = move-exception
            r18 = r13
            goto L_0x1ad7
        L_0x1961:
            r11 = r66
            r14 = r3
            r57 = r9
            r18 = r13
            r10 = r35
            r4 = r44
            r5 = r50
            r13 = r54
            r9 = r56
            r3 = r68
        L_0x1974:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r7.sizes     // Catch:{ Exception -> 0x1acf }
            r35 = r10
            r10 = 0
            java.lang.Object r3 = r3.get(r10)     // Catch:{ Exception -> 0x1acf }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x1acf }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r7.sizes     // Catch:{ Exception -> 0x1acf }
            int r19 = r10.size()     // Catch:{ Exception -> 0x1acf }
            r49 = r7
            r20 = 1
            int r7 = r19 + -1
            java.lang.Object r7 = r10.get(r7)     // Catch:{ Exception -> 0x1acf }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC$PhotoSize) r7     // Catch:{ Exception -> 0x1acf }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x1acf }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r10 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto     // Catch:{ Exception -> 0x1acf }
            r10.<init>()     // Catch:{ Exception -> 0x1acf }
            r6.media = r10     // Catch:{ Exception -> 0x1acf }
            r10.caption = r14     // Catch:{ Exception -> 0x1acf }
            byte[] r14 = r3.bytes     // Catch:{ Exception -> 0x1acf }
            if (r14 == 0) goto L_0x19ab
            r63 = r13
            r13 = r10
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r13 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r13     // Catch:{ Exception -> 0x1211 }
            r13.thumb = r14     // Catch:{ Exception -> 0x1211 }
            r44 = r4
            goto L_0x19b7
        L_0x19ab:
            r63 = r13
            r13 = r10
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r13 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r13     // Catch:{ Exception -> 0x1acf }
            r44 = r4
            r14 = 0
            byte[] r4 = new byte[r14]     // Catch:{ Exception -> 0x1acf }
            r13.thumb = r4     // Catch:{ Exception -> 0x1acf }
        L_0x19b7:
            int r4 = r3.h     // Catch:{ Exception -> 0x1acf }
            r10.thumb_h = r4     // Catch:{ Exception -> 0x1acf }
            int r3 = r3.w     // Catch:{ Exception -> 0x1acf }
            r10.thumb_w = r3     // Catch:{ Exception -> 0x1acf }
            int r3 = r7.w     // Catch:{ Exception -> 0x1acf }
            r10.w = r3     // Catch:{ Exception -> 0x1acf }
            int r3 = r7.h     // Catch:{ Exception -> 0x1acf }
            r10.h = r3     // Catch:{ Exception -> 0x1acf }
            int r3 = r7.size     // Catch:{ Exception -> 0x1acf }
            long r3 = (long) r3     // Catch:{ Exception -> 0x1acf }
            r10.size = r3     // Catch:{ Exception -> 0x1acf }
            org.telegram.tgnet.TLRPC$FileLocation r3 = r7.location     // Catch:{ Exception -> 0x1acf }
            byte[] r3 = r3.key     // Catch:{ Exception -> 0x1acf }
            if (r3 == 0) goto L_0x1a0a
            int r3 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x19d7
            goto L_0x1a0a
        L_0x19d7:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1211 }
            r1.<init>()     // Catch:{ Exception -> 0x1211 }
            org.telegram.tgnet.TLRPC$FileLocation r3 = r7.location     // Catch:{ Exception -> 0x1211 }
            long r13 = r3.volume_id     // Catch:{ Exception -> 0x1211 }
            r1.id = r13     // Catch:{ Exception -> 0x1211 }
            long r13 = r3.secret     // Catch:{ Exception -> 0x1211 }
            r1.access_hash = r13     // Catch:{ Exception -> 0x1211 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r6.media     // Catch:{ Exception -> 0x1211 }
            byte[] r7 = r3.key     // Catch:{ Exception -> 0x1211 }
            r4.key = r7     // Catch:{ Exception -> 0x1211 }
            byte[] r3 = r3.iv     // Catch:{ Exception -> 0x1211 }
            r4.iv = r3     // Catch:{ Exception -> 0x1211 }
            org.telegram.messenger.SecretChatHelper r3 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1211 }
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner     // Catch:{ Exception -> 0x1211 }
            r7 = 0
            r58 = r3
            r59 = r6
            r60 = r4
            r61 = r8
            r62 = r1
            r63 = r7
            r64 = r2
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1211 }
            goto L_0x1908
        L_0x1a0a:
            if (r9 != 0) goto L_0x1a3d
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1211 }
            r3.<init>(r11)     // Catch:{ Exception -> 0x1211 }
            r3.encryptedChat = r8     // Catch:{ Exception -> 0x1211 }
            r4 = 0
            r3.type = r4     // Catch:{ Exception -> 0x1211 }
            r3.originalPath = r5     // Catch:{ Exception -> 0x1211 }
            r3.sendEncryptedRequest = r6     // Catch:{ Exception -> 0x1211 }
            r3.obj = r2     // Catch:{ Exception -> 0x1211 }
            if (r1 == 0) goto L_0x1a2d
            r4 = r44
            boolean r7 = r1.containsKey(r4)     // Catch:{ Exception -> 0x1211 }
            if (r7 == 0) goto L_0x1a2d
            java.lang.Object r1 = r1.get(r4)     // Catch:{ Exception -> 0x1211 }
            r3.parentObject = r1     // Catch:{ Exception -> 0x1211 }
            goto L_0x1a31
        L_0x1a2d:
            r7 = r63
            r3.parentObject = r7     // Catch:{ Exception -> 0x1211 }
        L_0x1a31:
            r1 = 1
            r3.performMediaUpload = r1     // Catch:{ Exception -> 0x1211 }
            if (r78 == 0) goto L_0x1a38
            r1 = 1
            goto L_0x1a39
        L_0x1a38:
            r1 = 0
        L_0x1a39:
            r3.scheduled = r1     // Catch:{ Exception -> 0x1211 }
            r1 = r3
            goto L_0x1a3e
        L_0x1a3d:
            r1 = r9
        L_0x1a3e:
            boolean r3 = android.text.TextUtils.isEmpty(r68)     // Catch:{ Exception -> 0x1acf }
            if (r3 != 0) goto L_0x1a51
            r3 = r68
            r4 = r35
            boolean r4 = r3.startsWith(r4)     // Catch:{ Exception -> 0x1211 }
            if (r4 == 0) goto L_0x1a51
            r1.httpLocation = r3     // Catch:{ Exception -> 0x1211 }
            goto L_0x1a65
        L_0x1a51:
            r4 = r49
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r4.sizes     // Catch:{ Exception -> 0x1acf }
            int r7 = r3.size()     // Catch:{ Exception -> 0x1acf }
            r8 = 1
            int r7 = r7 - r8
            java.lang.Object r3 = r3.get(r7)     // Catch:{ Exception -> 0x1acf }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x1acf }
            r1.photoSize = r3     // Catch:{ Exception -> 0x1acf }
            r1.locationParent = r4     // Catch:{ Exception -> 0x1acf }
        L_0x1a65:
            int r3 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x1a6c
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1211 }
        L_0x1a6c:
            int r3 = (r47 > r16 ? 1 : (r47 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x1ab7
            org.telegram.tgnet.TLObject r3 = r1.sendEncryptedRequest     // Catch:{ Exception -> 0x1ab3 }
            if (r3 == 0) goto L_0x1a77
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r3 = (org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia) r3     // Catch:{ Exception -> 0x1211 }
            goto L_0x1a7e
        L_0x1a77:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia     // Catch:{ Exception -> 0x1ab3 }
            r3.<init>()     // Catch:{ Exception -> 0x1ab3 }
            r1.sendEncryptedRequest = r3     // Catch:{ Exception -> 0x1ab3 }
        L_0x1a7e:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r1.messageObjects     // Catch:{ Exception -> 0x1ab3 }
            r4.add(r2)     // Catch:{ Exception -> 0x1ab3 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r4 = r1.messages     // Catch:{ Exception -> 0x1ab3 }
            r13 = r18
            r4.add(r13)     // Catch:{ Exception -> 0x1ad6 }
            java.util.ArrayList<java.lang.String> r4 = r1.originalPaths     // Catch:{ Exception -> 0x1ad6 }
            r4.add(r5)     // Catch:{ Exception -> 0x1ad6 }
            r4 = 1
            r1.performMediaUpload = r4     // Catch:{ Exception -> 0x1ad6 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r4 = r3.messages     // Catch:{ Exception -> 0x1ad6 }
            r4.add(r6)     // Catch:{ Exception -> 0x1ad6 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r4 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1ad6 }
            r4.<init>()     // Catch:{ Exception -> 0x1ad6 }
            r5 = r57
            r6 = 3
            if (r5 == r6) goto L_0x1aa4
            r6 = 7
            if (r5 != r6) goto L_0x1aa6
        L_0x1aa4:
            r16 = 1
        L_0x1aa6:
            r5 = r16
            r4.id = r5     // Catch:{ Exception -> 0x1ad6 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r3 = r3.files     // Catch:{ Exception -> 0x1ad6 }
            r3.add(r4)     // Catch:{ Exception -> 0x1ad6 }
            r15.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1ad6 }
            goto L_0x1ab9
        L_0x1ab3:
            r0 = move-exception
            r13 = r18
            goto L_0x1ad7
        L_0x1ab7:
            r13 = r18
        L_0x1ab9:
            r5 = r78
            if (r73 != 0) goto L_0x1CLASSNAME
            org.telegram.messenger.MediaDataController r1 = r55.getMediaDataController()     // Catch:{ Exception -> 0x1c0c }
            if (r70 == 0) goto L_0x1ac8
            int r3 = r70.getId()     // Catch:{ Exception -> 0x1c0c }
            goto L_0x1ac9
        L_0x1ac8:
            r3 = 0
        L_0x1ac9:
            r4 = 0
            r1.cleanDraft(r11, r3, r4)     // Catch:{ Exception -> 0x1c0c }
            goto L_0x1CLASSNAME
        L_0x1acf:
            r0 = move-exception
            r5 = r78
            r13 = r18
            goto L_0x032e
        L_0x1ad6:
            r0 = move-exception
        L_0x1ad7:
            r5 = r78
            goto L_0x032e
        L_0x1adb:
            r7 = r63
            r2 = r71
            r5 = r4
            r1 = r9
            r11 = r10
            r10 = r45
            r4 = r74
            r9 = r3
            r3 = r56
            if (r8 != 0) goto L_0x1b71
            org.telegram.tgnet.TLRPC$TL_messages_sendMessage r6 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage     // Catch:{ Exception -> 0x1c0c }
            r6.<init>()     // Catch:{ Exception -> 0x1c0c }
            r6.message = r3     // Catch:{ Exception -> 0x1c0c }
            if (r73 != 0) goto L_0x1af6
            r3 = 1
            goto L_0x1af7
        L_0x1af6:
            r3 = 0
        L_0x1af7:
            r6.clear_draft = r3     // Catch:{ Exception -> 0x1c0c }
            boolean r3 = r13.silent     // Catch:{ Exception -> 0x1c0c }
            r6.silent = r3     // Catch:{ Exception -> 0x1c0c }
            r6.peer = r9     // Catch:{ Exception -> 0x1c0c }
            long r8 = r13.random_id     // Catch:{ Exception -> 0x1c0c }
            r6.random_id = r8     // Catch:{ Exception -> 0x1c0c }
            org.telegram.tgnet.TLRPC$Peer r3 = r13.from_id     // Catch:{ Exception -> 0x1c0c }
            if (r3 == 0) goto L_0x1b13
            org.telegram.messenger.MessagesController r3 = r55.getMessagesController()     // Catch:{ Exception -> 0x1c0c }
            org.telegram.tgnet.TLRPC$Peer r8 = r13.from_id     // Catch:{ Exception -> 0x1c0c }
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r8)     // Catch:{ Exception -> 0x1c0c }
            r6.send_as = r3     // Catch:{ Exception -> 0x1c0c }
        L_0x1b13:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r13.reply_to     // Catch:{ Exception -> 0x1c0c }
            if (r3 == 0) goto L_0x1b23
            int r3 = r3.reply_to_msg_id     // Catch:{ Exception -> 0x1c0c }
            if (r3 == 0) goto L_0x1b23
            int r8 = r6.flags     // Catch:{ Exception -> 0x1c0c }
            r9 = 1
            r8 = r8 | r9
            r6.flags = r8     // Catch:{ Exception -> 0x1c0c }
            r6.reply_to_msg_id = r3     // Catch:{ Exception -> 0x1c0c }
        L_0x1b23:
            if (r72 != 0) goto L_0x1b28
            r3 = 1
            r6.no_webpage = r3     // Catch:{ Exception -> 0x1c0c }
        L_0x1b28:
            if (r4 == 0) goto L_0x1b39
            boolean r3 = r74.isEmpty()     // Catch:{ Exception -> 0x1c0c }
            if (r3 != 0) goto L_0x1b39
            r6.entities = r4     // Catch:{ Exception -> 0x1c0c }
            int r3 = r6.flags     // Catch:{ Exception -> 0x1c0c }
            r4 = 8
            r3 = r3 | r4
            r6.flags = r3     // Catch:{ Exception -> 0x1c0c }
        L_0x1b39:
            if (r5 == 0) goto L_0x1b43
            r6.schedule_date = r5     // Catch:{ Exception -> 0x1c0c }
            int r3 = r6.flags     // Catch:{ Exception -> 0x1c0c }
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r6.flags = r3     // Catch:{ Exception -> 0x1c0c }
        L_0x1b43:
            r3 = 0
            r4 = 0
            if (r5 == 0) goto L_0x1b49
            r8 = 1
            goto L_0x1b4a
        L_0x1b49:
            r8 = 0
        L_0x1b4a:
            r56 = r55
            r57 = r6
            r58 = r2
            r59 = r3
            r60 = r4
            r61 = r7
            r62 = r1
            r63 = r8
            r56.performSendMessageRequest(r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x1c0c }
            if (r73 != 0) goto L_0x1CLASSNAME
            org.telegram.messenger.MediaDataController r1 = r55.getMediaDataController()     // Catch:{ Exception -> 0x1c0c }
            if (r70 == 0) goto L_0x1b6a
            int r3 = r70.getId()     // Catch:{ Exception -> 0x1c0c }
            goto L_0x1b6b
        L_0x1b6a:
            r3 = 0
        L_0x1b6b:
            r4 = 0
            r1.cleanDraft(r11, r3, r4)     // Catch:{ Exception -> 0x1c0c }
            goto L_0x1CLASSNAME
        L_0x1b71:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1c0c }
            r6.<init>()     // Catch:{ Exception -> 0x1c0c }
            int r7 = r13.ttl     // Catch:{ Exception -> 0x1c0c }
            r6.ttl = r7     // Catch:{ Exception -> 0x1c0c }
            if (r4 == 0) goto L_0x1b8a
            boolean r7 = r74.isEmpty()     // Catch:{ Exception -> 0x1c0c }
            if (r7 != 0) goto L_0x1b8a
            r6.entities = r4     // Catch:{ Exception -> 0x1c0c }
            int r4 = r6.flags     // Catch:{ Exception -> 0x1c0c }
            r4 = r4 | 128(0x80, float:1.794E-43)
            r6.flags = r4     // Catch:{ Exception -> 0x1c0c }
        L_0x1b8a:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = r13.reply_to     // Catch:{ Exception -> 0x1c0c }
            if (r4 == 0) goto L_0x1b9d
            long r14 = r4.reply_to_random_id     // Catch:{ Exception -> 0x1c0c }
            int r4 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x1b9d
            r6.reply_to_random_id = r14     // Catch:{ Exception -> 0x1c0c }
            int r4 = r6.flags     // Catch:{ Exception -> 0x1c0c }
            r7 = 8
            r4 = r4 | r7
            r6.flags = r4     // Catch:{ Exception -> 0x1c0c }
        L_0x1b9d:
            if (r1 == 0) goto L_0x1bb3
            java.lang.Object r4 = r1.get(r10)     // Catch:{ Exception -> 0x1c0c }
            if (r4 == 0) goto L_0x1bb3
            java.lang.Object r1 = r1.get(r10)     // Catch:{ Exception -> 0x1c0c }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x1c0c }
            r6.via_bot_name = r1     // Catch:{ Exception -> 0x1c0c }
            int r1 = r6.flags     // Catch:{ Exception -> 0x1c0c }
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r6.flags = r1     // Catch:{ Exception -> 0x1c0c }
        L_0x1bb3:
            boolean r1 = r13.silent     // Catch:{ Exception -> 0x1c0c }
            r6.silent = r1     // Catch:{ Exception -> 0x1c0c }
            long r9 = r13.random_id     // Catch:{ Exception -> 0x1c0c }
            r6.random_id = r9     // Catch:{ Exception -> 0x1c0c }
            r6.message = r3     // Catch:{ Exception -> 0x1c0c }
            r15 = r40
            if (r15 == 0) goto L_0x1bd7
            java.lang.String r1 = r15.url     // Catch:{ Exception -> 0x1c0c }
            if (r1 == 0) goto L_0x1bd7
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage     // Catch:{ Exception -> 0x1c0c }
            r1.<init>()     // Catch:{ Exception -> 0x1c0c }
            r6.media = r1     // Catch:{ Exception -> 0x1c0c }
            java.lang.String r3 = r15.url     // Catch:{ Exception -> 0x1c0c }
            r1.url = r3     // Catch:{ Exception -> 0x1c0c }
            int r1 = r6.flags     // Catch:{ Exception -> 0x1c0c }
            r1 = r1 | 512(0x200, float:7.175E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x1c0c }
            goto L_0x1bde
        L_0x1bd7:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty     // Catch:{ Exception -> 0x1c0c }
            r1.<init>()     // Catch:{ Exception -> 0x1c0c }
            r6.media = r1     // Catch:{ Exception -> 0x1c0c }
        L_0x1bde:
            org.telegram.messenger.SecretChatHelper r1 = r55.getSecretChatHelper()     // Catch:{ Exception -> 0x1c0c }
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner     // Catch:{ Exception -> 0x1c0c }
            r4 = 0
            r7 = 0
            r58 = r1
            r59 = r6
            r60 = r3
            r61 = r8
            r62 = r4
            r63 = r7
            r64 = r2
            r58.performSendEncryptedRequest(r59, r60, r61, r62, r63, r64)     // Catch:{ Exception -> 0x1c0c }
            if (r73 != 0) goto L_0x1CLASSNAME
            org.telegram.messenger.MediaDataController r1 = r55.getMediaDataController()     // Catch:{ Exception -> 0x1c0c }
            if (r70 == 0) goto L_0x1CLASSNAME
            int r3 = r70.getId()     // Catch:{ Exception -> 0x1c0c }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r3 = 0
        L_0x1CLASSNAME:
            r4 = 0
            r1.cleanDraft(r11, r3, r4)     // Catch:{ Exception -> 0x1c0c }
        L_0x1CLASSNAME:
            r2 = r55
            goto L_0x1CLASSNAME
        L_0x1c0c:
            r0 = move-exception
            goto L_0x032e
        L_0x1c0f:
            r0 = move-exception
            r2 = r71
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            r2 = r3
        L_0x1CLASSNAME:
            r5 = r4
            goto L_0x032e
        L_0x1CLASSNAME:
            r0 = move-exception
            r5 = r78
            r2 = r3
            goto L_0x032e
        L_0x1c1e:
            r0 = move-exception
            r5 = r3
            goto L_0x0839
        L_0x1CLASSNAME:
            r0 = move-exception
            r5 = r9
        L_0x1CLASSNAME:
            r1 = r0
        L_0x1CLASSNAME:
            r2 = 0
            r13 = 0
        L_0x1CLASSNAME:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            org.telegram.messenger.MessagesStorage r1 = r55.getMessagesStorage()
            if (r5 == 0) goto L_0x1CLASSNAME
            r3 = 1
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r3 = 0
        L_0x1CLASSNAME:
            r1.markMessageAsSendError(r13, r3)
            if (r2 == 0) goto L_0x1c3d
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            r2 = 2
            r1.send_state = r2
        L_0x1c3d:
            org.telegram.messenger.NotificationCenter r1 = r55.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messageSendError
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r4 = r13.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r5 = 0
            r3[r5] = r4
            r1.postNotificationName(r2, r3)
            int r1 = r13.id
            r2 = r55
            r2.processSentMessage(r1)
        L_0x1CLASSNAME:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.lang.String, java.lang.String, org.telegram.tgnet.TLRPC$MessageMedia, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$TL_document, org.telegram.tgnet.TLRPC$TL_game, org.telegram.tgnet.TLRPC$TL_messageMediaPoll, org.telegram.tgnet.TLRPC$TL_messageMediaInvoice, long, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$WebPage, boolean, org.telegram.messenger.MessageObject, java.util.ArrayList, org.telegram.tgnet.TLRPC$ReplyMarkup, java.util.HashMap, boolean, int, int, java.lang.Object, org.telegram.messenger.MessageObject$SendAnimationData):void");
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
                    TLRPC$TL_photoSize_layer127 tLRPC$TL_photoSize_layer127 = new TLRPC$TL_photoSize_layer127();
                    tLRPC$TL_photoSize_layer127.type = tLRPC$PhotoSize.type;
                    tLRPC$TL_photoSize_layer127.w = tLRPC$PhotoSize.w;
                    tLRPC$TL_photoSize_layer127.h = tLRPC$PhotoSize.h;
                    tLRPC$TL_photoSize_layer127.size = tLRPC$PhotoSize.size;
                    byte[] bArr = tLRPC$PhotoSize.bytes;
                    tLRPC$TL_photoSize_layer127.bytes = bArr;
                    if (bArr == null) {
                        tLRPC$TL_photoSize_layer127.bytes = new byte[0];
                    }
                    TLRPC$TL_fileLocation_layer82 tLRPC$TL_fileLocation_layer82 = new TLRPC$TL_fileLocation_layer82();
                    tLRPC$TL_photoSize_layer127.location = tLRPC$TL_fileLocation_layer82;
                    TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize.location;
                    tLRPC$TL_fileLocation_layer82.dc_id = tLRPC$FileLocation.dc_id;
                    tLRPC$TL_fileLocation_layer82.volume_id = tLRPC$FileLocation.volume_id;
                    tLRPC$TL_fileLocation_layer82.local_id = tLRPC$FileLocation.local_id;
                    tLRPC$TL_fileLocation_layer82.secret = tLRPC$FileLocation.secret;
                    return tLRPC$TL_photoSize_layer127;
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
        TLRPC$PhotoSize tLRPC$PhotoSize;
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
                String file = FileLoader.getInstance(this.currentAccount).getPathToAttach(delayedMessage2.photoSize).toString();
                putToDelayedMessages(file, delayedMessage2);
                getFileLoader().uploadFile(file, false, true, 16777216);
                putToUploadingMessages(delayedMessage2.obj);
            } else {
                String file2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(delayedMessage2.photoSize).toString();
                if (!(delayedMessage2.sendEncryptedRequest == null || delayedMessage2.photoSize.location.dc_id == 0)) {
                    File file3 = new File(file2);
                    if (!file3.exists()) {
                        file2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(delayedMessage2.photoSize, true).toString();
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
                        tLRPC$DecryptedMessageMedia.size = (long) ((int) videoEditedInfo2.estimatedSize);
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
                            getFileLoader().uploadFile(str3, false, false, document.size, 33554432, false);
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
                        getFileLoader().uploadFile(str5, true, false, document2.size, 33554432, false);
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
                } else if (tLRPC$InputMedia.thumb == null && (tLRPC$PhotoSize = delayedMessage2.photoSize) != null && !(tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize)) {
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
                        TLRPC$PhotoSize tLRPC$PhotoSize2 = delayedMessage2.photoSize;
                        if (!(tLRPC$PhotoSize2 == null || tLRPC$PhotoSize2.location == null)) {
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
                                TLRPC$PhotoSize tLRPC$PhotoSize3 = delayedMessage2.photoSize;
                                if (!(tLRPC$PhotoSize3 == null || tLRPC$PhotoSize3.location == null)) {
                                    delayedMessage2.extraHashMap.put(str13 + "_t", delayedMessage2.photoSize);
                                }
                                VideoEditedInfo videoEditedInfo5 = messageObject8.videoEditedInfo;
                                if (videoEditedInfo5 == null || !videoEditedInfo5.needConvert()) {
                                    getFileLoader().uploadFile(str13, false, false, 33554432);
                                } else {
                                    getFileLoader().uploadFile(str13, false, false, document6.size, 33554432, false);
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
                            TLRPC$PhotoSize tLRPC$PhotoSize4 = delayedMessage2.photoSize;
                            if (!(tLRPC$PhotoSize4 == null || tLRPC$PhotoSize4.location == null)) {
                                delayedMessage2.extraHashMap.put(str13 + "_t", delayedMessage2.photoSize);
                            }
                            VideoEditedInfo videoEditedInfo6 = messageObject10.videoEditedInfo;
                            if (videoEditedInfo6 == null || !videoEditedInfo6.needConvert()) {
                                getFileLoader().uploadFile(str13, true, false, 33554432);
                            } else {
                                getFileLoader().uploadFile(str13, true, false, document6.size, 33554432, false);
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
                        String file4 = FileLoader.getInstance(this.currentAccount).getPathToAttach(delayedMessage2.photoSize).toString();
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
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new SendMessagesHelper$$ExternalSyntheticLambda85(this, delayedMessage2, str16));
            putToDelayedMessages(str16, delayedMessage2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendDelayedMessage$33(DelayedMessage delayedMessage, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda42(this, tLObject, delayedMessage, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendDelayedMessage$32(TLObject tLObject, DelayedMessage delayedMessage, String str) {
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
                    getNotificationCenter().postNotificationName(NotificationCenter.fileUploadProgressChanged, str, -1L, -1L, Boolean.FALSE);
                    break;
                } else {
                    i++;
                }
            }
            TLRPC$TL_messages_uploadMedia tLRPC$TL_messages_uploadMedia = new TLRPC$TL_messages_uploadMedia();
            tLRPC$TL_messages_uploadMedia.media = tLRPC$InputMedia;
            tLRPC$TL_messages_uploadMedia.peer = ((TLRPC$TL_messages_sendMultiMedia) delayedMessage.sendRequest).peer;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_uploadMedia, new SendMessagesHelper$$ExternalSyntheticLambda87(this, tLRPC$InputMedia, delayedMessage));
        } else if (tLRPC$InputEncryptedFile != null) {
            TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
            int i2 = 0;
            while (true) {
                if (i2 >= tLRPC$TL_messages_sendEncryptedMultiMedia.files.size()) {
                    break;
                } else if (tLRPC$TL_messages_sendEncryptedMultiMedia.files.get(i2) == tLRPC$InputEncryptedFile) {
                    putToSendingMessages(delayedMessage.messages.get(i2), delayedMessage.scheduled);
                    getNotificationCenter().postNotificationName(NotificationCenter.fileUploadProgressChanged, str, -1L, -1L, Boolean.FALSE);
                    break;
                } else {
                    i2++;
                }
            }
            sendReadyToSendGroup(delayedMessage, false, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$uploadMultiMedia$35(TLRPC$InputMedia tLRPC$InputMedia, DelayedMessage delayedMessage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda43(this, tLObject, tLRPC$InputMedia, delayedMessage));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0097  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$uploadMultiMedia$34(org.telegram.tgnet.TLObject r6, org.telegram.tgnet.TLRPC$InputMedia r7, org.telegram.messenger.SendMessagesHelper.DelayedMessage r8) {
        /*
            r5 = this;
            if (r6 == 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$MessageMedia r6 = (org.telegram.tgnet.TLRPC$MessageMedia) r6
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto
            if (r0 == 0) goto L_0x0030
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0030
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
            boolean r6 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r6 == 0) goto L_0x005d
            java.lang.String r6 = "set uploaded photo"
            org.telegram.messenger.FileLog.d(r6)
            goto L_0x005d
        L_0x0030:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument
            if (r0 == 0) goto L_0x005c
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L_0x005c
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
            boolean r6 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r6 == 0) goto L_0x005d
            java.lang.String r6 = "set uploaded document"
            org.telegram.messenger.FileLog.d(r6)
            goto L_0x005d
        L_0x005c:
            r0 = 0
        L_0x005d:
            if (r0 == 0) goto L_0x0097
            int r6 = r7.ttl_seconds
            r1 = 1
            if (r6 == 0) goto L_0x006b
            r0.ttl_seconds = r6
            int r6 = r0.flags
            r6 = r6 | r1
            r0.flags = r6
        L_0x006b:
            org.telegram.tgnet.TLObject r6 = r8.sendRequest
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r6 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r6
            r2 = 0
            r3 = 0
        L_0x0071:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r6.multi_media
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0093
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r6.multi_media
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r4 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r4
            org.telegram.tgnet.TLRPC$InputMedia r4 = r4.media
            if (r4 != r7) goto L_0x0090
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r6 = r6.multi_media
            java.lang.Object r6 = r6.get(r3)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r6 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r6
            r6.media = r0
            goto L_0x0093
        L_0x0090:
            int r3 = r3 + 1
            goto L_0x0071
        L_0x0093:
            r5.sendReadyToSendGroup(r8, r2, r1)
            goto L_0x009a
        L_0x0097:
            r8.markAsError()
        L_0x009a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$uploadMultiMedia$34(org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$InputMedia, org.telegram.messenger.SendMessagesHelper$DelayedMessage):void");
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
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("add message");
                }
            }
            TLObject tLObject = delayedMessage.sendRequest;
            if (tLObject instanceof TLRPC$TL_messages_sendMultiMedia) {
                TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = (TLRPC$TL_messages_sendMultiMedia) tLObject;
                while (i2 < tLRPC$TL_messages_sendMultiMedia.multi_media.size()) {
                    TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_messages_sendMultiMedia.multi_media.get(i2).media;
                    if (!(tLRPC$InputMedia instanceof TLRPC$TL_inputMediaUploadedPhoto) && !(tLRPC$InputMedia instanceof TLRPC$TL_inputMediaUploadedDocument)) {
                        i2++;
                    } else if (BuildVars.DEBUG_VERSION) {
                        FileLog.d("multi media not ready");
                        return;
                    } else {
                        return;
                    }
                }
                if (z2 && (findMaxDelayedMessageForMessageId = findMaxDelayedMessageForMessageId(delayedMessage.finalGroupMessage, delayedMessage.peer)) != null) {
                    findMaxDelayedMessageForMessageId.addDelayedRequest(delayedMessage.sendRequest, delayedMessage.messageObjects, delayedMessage.originalPaths, delayedMessage.parentObjects, delayedMessage, delayedMessage.scheduled);
                    ArrayList<DelayedMessageSendAfterRequest> arrayList2 = delayedMessage.requests;
                    if (arrayList2 != null) {
                        findMaxDelayedMessageForMessageId.requests.addAll(arrayList2);
                    }
                    if (BuildVars.DEBUG_VERSION) {
                        FileLog.d("has maxDelayedMessage, delay");
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
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("final message not added, add");
            }
            putToDelayedMessages(str, delayedMessage);
        } else if (BuildVars.DEBUG_VERSION) {
            FileLog.d("final message not added");
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$stopVideoService$36(String str) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopEncodingService, str, Integer.valueOf(this.currentAccount));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$stopVideoService$37(String str) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda28(this, str));
    }

    public void stopVideoService(String str) {
        getMessagesStorage().getStorageQueue().postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda26(this, str));
    }

    /* access modifiers changed from: protected */
    public void putToSendingMessages(TLRPC$Message tLRPC$Message, boolean z) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda54(this, tLRPC$Message, z));
        } else {
            putToSendingMessages(tLRPC$Message, z, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$putToSendingMessages$38(TLRPC$Message tLRPC$Message, boolean z) {
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
        SendMessagesHelper$$ExternalSyntheticLambda83 sendMessagesHelper$$ExternalSyntheticLambda83 = new SendMessagesHelper$$ExternalSyntheticLambda83(this, arrayList3, tLRPC$TL_messages_sendMultiMedia, arrayList, arrayList2, delayedMessage, z);
        TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia2 = tLRPC$TL_messages_sendMultiMedia;
        connectionsManager.sendRequest((TLObject) tLRPC$TL_messages_sendMultiMedia, (RequestDelegate) sendMessagesHelper$$ExternalSyntheticLambda83, (QuickAckDelegate) null, 68);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequestMulti$46(ArrayList arrayList, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, ArrayList arrayList2, ArrayList arrayList3, DelayedMessage delayedMessage, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda61(this, tLRPC$TL_messages_sendMultiMedia, delayedMessage, arrayList2, z));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda56(this, tLRPC$TL_error, tLObject, arrayList2, arrayList3, z, tLRPC$TL_messages_sendMultiMedia));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequestMulti$39(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, DelayedMessage delayedMessage, ArrayList arrayList, boolean z) {
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
    public /* synthetic */ void lambda$performSendMessageRequestMulti$45(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, ArrayList arrayList, ArrayList arrayList2, boolean z, TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia) {
        boolean z2;
        TLRPC$Updates tLRPC$Updates;
        boolean z3;
        TLRPC$Message tLRPC$Message;
        TLRPC$Updates tLRPC$Updates2;
        int i;
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        ArrayList arrayList3 = arrayList;
        boolean z4 = z;
        if (tLRPC$TL_error2 == null) {
            SparseArray sparseArray = new SparseArray();
            LongSparseArray longSparseArray = new LongSparseArray();
            TLRPC$Updates tLRPC$Updates3 = (TLRPC$Updates) tLObject;
            ArrayList<TLRPC$Update> arrayList4 = tLRPC$Updates3.updates;
            LongSparseArray longSparseArray2 = null;
            int i2 = 0;
            while (i2 < arrayList4.size()) {
                TLRPC$Update tLRPC$Update = arrayList4.get(i2);
                if (tLRPC$Update instanceof TLRPC$TL_updateMessageID) {
                    TLRPC$TL_updateMessageID tLRPC$TL_updateMessageID = (TLRPC$TL_updateMessageID) tLRPC$Update;
                    longSparseArray.put(tLRPC$TL_updateMessageID.random_id, Integer.valueOf(tLRPC$TL_updateMessageID.id));
                    arrayList4.remove(i2);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewMessage) {
                    TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage = (TLRPC$TL_updateNewMessage) tLRPC$Update;
                    TLRPC$Message tLRPC$Message2 = tLRPC$TL_updateNewMessage.message;
                    sparseArray.put(tLRPC$Message2.id, tLRPC$Message2);
                    Utilities.stageQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda66(this, tLRPC$TL_updateNewMessage));
                    arrayList4.remove(i2);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewChannelMessage) {
                    TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage = (TLRPC$TL_updateNewChannelMessage) tLRPC$Update;
                    TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(MessagesController.getUpdateChannelId(tLRPC$TL_updateNewChannelMessage)));
                    if (!((chat != null && !chat.megagroup) || (tLRPC$TL_messageReplyHeader = tLRPC$TL_updateNewChannelMessage.message.reply_to) == null || (tLRPC$TL_messageReplyHeader.reply_to_top_id == 0 && tLRPC$TL_messageReplyHeader.reply_to_msg_id == 0))) {
                        if (longSparseArray2 == null) {
                            longSparseArray2 = new LongSparseArray();
                        }
                        long dialogId = MessageObject.getDialogId(tLRPC$TL_updateNewChannelMessage.message);
                        SparseArray sparseArray2 = (SparseArray) longSparseArray2.get(dialogId);
                        if (sparseArray2 == null) {
                            sparseArray2 = new SparseArray();
                            longSparseArray2.put(dialogId, sparseArray2);
                        }
                        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader2 = tLRPC$TL_updateNewChannelMessage.message.reply_to;
                        int i3 = tLRPC$TL_messageReplyHeader2.reply_to_top_id;
                        if (i3 == 0) {
                            i3 = tLRPC$TL_messageReplyHeader2.reply_to_msg_id;
                        }
                        TLRPC$MessageReplies tLRPC$MessageReplies = (TLRPC$MessageReplies) sparseArray2.get(i3);
                        if (tLRPC$MessageReplies == null) {
                            tLRPC$MessageReplies = new TLRPC$TL_messageReplies();
                            sparseArray2.put(i3, tLRPC$MessageReplies);
                        }
                        TLRPC$Peer tLRPC$Peer = tLRPC$TL_updateNewChannelMessage.message.from_id;
                        if (tLRPC$Peer != null) {
                            tLRPC$MessageReplies.recent_repliers.add(0, tLRPC$Peer);
                        }
                        tLRPC$MessageReplies.replies++;
                    }
                    TLRPC$Message tLRPC$Message3 = tLRPC$TL_updateNewChannelMessage.message;
                    sparseArray.put(tLRPC$Message3.id, tLRPC$Message3);
                    Utilities.stageQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda63(this, tLRPC$TL_updateNewChannelMessage));
                    arrayList4.remove(i2);
                } else if (tLRPC$Update instanceof TLRPC$TL_updateNewScheduledMessage) {
                    TLRPC$Message tLRPC$Message4 = ((TLRPC$TL_updateNewScheduledMessage) tLRPC$Update).message;
                    sparseArray.put(tLRPC$Message4.id, tLRPC$Message4);
                    arrayList4.remove(i2);
                } else {
                    i2++;
                }
                i2--;
                i2++;
            }
            if (longSparseArray2 != null) {
                getMessagesStorage().putChannelViews((LongSparseArray<SparseIntArray>) null, (LongSparseArray<SparseIntArray>) null, longSparseArray2, true);
                getNotificationCenter().postNotificationName(NotificationCenter.didUpdateMessagesViews, null, null, longSparseArray2, Boolean.TRUE);
            }
            int i4 = 0;
            while (true) {
                if (i4 >= arrayList.size()) {
                    tLRPC$Updates = tLRPC$Updates3;
                    z3 = false;
                    break;
                }
                MessageObject messageObject = (MessageObject) arrayList3.get(i4);
                String str = (String) arrayList2.get(i4);
                TLRPC$Message tLRPC$Message5 = messageObject.messageOwner;
                int i5 = tLRPC$Message5.id;
                ArrayList arrayList5 = new ArrayList();
                Integer num = (Integer) longSparseArray.get(tLRPC$Message5.random_id);
                if (num == null || (tLRPC$Message = (TLRPC$Message) sparseArray.get(num.intValue())) == null) {
                    tLRPC$Updates = tLRPC$Updates3;
                    z3 = true;
                } else {
                    MessageObject.getDialogId(tLRPC$Message);
                    arrayList5.add(tLRPC$Message);
                    if ((tLRPC$Message.flags & 33554432) != 0) {
                        TLRPC$Message tLRPC$Message6 = messageObject.messageOwner;
                        tLRPC$Message6.ttl_period = tLRPC$Message.ttl_period;
                        tLRPC$Message6.flags = 33554432 | tLRPC$Message6.flags;
                    }
                    ArrayList arrayList6 = arrayList5;
                    int i6 = i5;
                    updateMediaPaths(messageObject, tLRPC$Message, tLRPC$Message.id, str, false);
                    int mediaExistanceFlags = messageObject.getMediaExistanceFlags();
                    TLRPC$Message tLRPC$Message7 = tLRPC$Message5;
                    tLRPC$Message7.id = tLRPC$Message.id;
                    long j = tLRPC$Message.grouped_id;
                    if (!z4) {
                        tLRPC$Updates2 = tLRPC$Updates3;
                        i = i4;
                        Integer num2 = getMessagesController().dialogs_read_outbox_max.get(Long.valueOf(tLRPC$Message.dialog_id));
                        if (num2 == null) {
                            num2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(tLRPC$Message.out, tLRPC$Message.dialog_id));
                            getMessagesController().dialogs_read_outbox_max.put(Long.valueOf(tLRPC$Message.dialog_id), num2);
                        }
                        tLRPC$Message.unread = num2.intValue() < tLRPC$Message.id;
                    } else {
                        tLRPC$Updates2 = tLRPC$Updates3;
                        i = i4;
                    }
                    getStatsController().incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 1, 1);
                    tLRPC$Message7.send_state = 0;
                    int i7 = i;
                    SparseArray sparseArray3 = sparseArray;
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i6), Integer.valueOf(tLRPC$Message7.id), tLRPC$Message7, Long.valueOf(tLRPC$Message7.dialog_id), Long.valueOf(j), Integer.valueOf(mediaExistanceFlags), Boolean.valueOf(z));
                    TLRPC$Updates tLRPC$Updates4 = tLRPC$Updates2;
                    DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                    SparseArray sparseArray4 = sparseArray3;
                    SendMessagesHelper$$ExternalSyntheticLambda50 sendMessagesHelper$$ExternalSyntheticLambda50 = r0;
                    SendMessagesHelper$$ExternalSyntheticLambda50 sendMessagesHelper$$ExternalSyntheticLambda502 = new SendMessagesHelper$$ExternalSyntheticLambda50(this, tLRPC$Message7, i6, z, arrayList6, j, mediaExistanceFlags);
                    storageQueue.postRunnable(sendMessagesHelper$$ExternalSyntheticLambda50);
                    i4 = i7 + 1;
                    sparseArray = sparseArray4;
                    tLRPC$Updates3 = tLRPC$Updates4;
                    longSparseArray = longSparseArray;
                }
            }
            tLRPC$Updates = tLRPC$Updates3;
            z3 = true;
            Utilities.stageQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda68(this, tLRPC$Updates));
            z2 = z3;
        } else {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error2, (BaseFragment) null, tLRPC$TL_messages_sendMultiMedia, new Object[0]);
            z2 = true;
        }
        if (z2) {
            for (int i8 = 0; i8 < arrayList.size(); i8++) {
                TLRPC$Message tLRPC$Message8 = ((MessageObject) arrayList3.get(i8)).messageOwner;
                getMessagesStorage().markMessageAsSendError(tLRPC$Message8, z4);
                tLRPC$Message8.send_state = 2;
                getNotificationCenter().postNotificationName(NotificationCenter.messageSendError, Integer.valueOf(tLRPC$Message8.id));
                processSentMessage(tLRPC$Message8.id);
                removeFromSendingMessages(tLRPC$Message8.id, z4);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequestMulti$40(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequestMulti$41(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.peer_id.channel_id);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequestMulti$43(TLRPC$Message tLRPC$Message, int i, boolean z, ArrayList arrayList, long j, int i2) {
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, MessageObject.getPeerId(tLRPC$Message2.peer_id), Integer.valueOf(i), tLRPC$Message2.id, 0, false, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda48(this, tLRPC$Message, i, j, i2, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequestMulti$42(TLRPC$Message tLRPC$Message, int i, long j, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), Long.valueOf(j), Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequestMulti$44(TLRPC$Updates tLRPC$Updates) {
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
            SendMessagesHelper$$ExternalSyntheticLambda86 sendMessagesHelper$$ExternalSyntheticLambda86 = r0;
            ConnectionsManager connectionsManager = getConnectionsManager();
            SendMessagesHelper$$ExternalSyntheticLambda86 sendMessagesHelper$$ExternalSyntheticLambda862 = new SendMessagesHelper$$ExternalSyntheticLambda86(this, tLObject, obj, messageObject, str, delayedMessage, z, delayedMessage2, z3, tLRPC$Message);
            tLRPC$Message.reqId = connectionsManager.sendRequest(tLObject2, (RequestDelegate) sendMessagesHelper$$ExternalSyntheticLambda86, (QuickAckDelegate) new SendMessagesHelper$$ExternalSyntheticLambda77(this, tLRPC$Message), (tLObject2 instanceof TLRPC$TL_messages_sendMessage ? 128 : 0) | 68);
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
    public /* synthetic */ void lambda$performSendMessageRequest$60(TLObject tLObject, Object obj, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, boolean z2, TLRPC$Message tLRPC$Message, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        TLObject tLObject3 = tLObject;
        Object obj2 = obj;
        TLRPC$TL_error tLRPC$TL_error2 = tLRPC$TL_error;
        if (tLRPC$TL_error2 != null && (((tLObject3 instanceof TLRPC$TL_messages_sendMedia) || (tLObject3 instanceof TLRPC$TL_messages_editMessage)) && FileRefController.isFileRefError(tLRPC$TL_error2.text))) {
            if (obj2 != null) {
                getFileRefController().requestReference(obj, tLObject3, messageObject, str, delayedMessage, Boolean.valueOf(z), delayedMessage2, Boolean.valueOf(z2));
                return;
            } else if (delayedMessage2 != null) {
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda55(this, tLRPC$Message, z2, tLObject, delayedMessage2));
                return;
            }
        }
        if (tLObject3 instanceof TLRPC$TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda58(this, tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, z2, tLObject));
        } else {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda71(this, z2, tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, tLObject));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$47(TLRPC$Message tLRPC$Message, boolean z, TLObject tLObject, DelayedMessage delayedMessage) {
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
    public /* synthetic */ void lambda$performSendMessageRequest$50(TLRPC$TL_error tLRPC$TL_error, TLRPC$Message tLRPC$Message, TLObject tLObject, MessageObject messageObject, String str, boolean z, TLObject tLObject2) {
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
            Utilities.stageQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda70(this, tLRPC$Updates, tLRPC$Message, z));
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
    public /* synthetic */ void lambda$performSendMessageRequest$49(TLRPC$Updates tLRPC$Updates, TLRPC$Message tLRPC$Message, boolean z) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda53(this, tLRPC$Message, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$48(TLRPC$Message tLRPC$Message, boolean z) {
        processSentMessage(tLRPC$Message.id);
        removeFromSendingMessages(tLRPC$Message.id, z);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0116, code lost:
        r12 = r3;
        r2 = null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x029f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$performSendMessageRequest$59(boolean r28, org.telegram.tgnet.TLRPC$TL_error r29, org.telegram.tgnet.TLRPC$Message r30, org.telegram.tgnet.TLObject r31, org.telegram.messenger.MessageObject r32, java.lang.String r33, org.telegram.tgnet.TLObject r34) {
        /*
            r27 = this;
            r8 = r27
            r9 = r28
            r0 = r29
            r10 = r30
            r1 = r31
            if (r0 != 0) goto L_0x034c
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
            r16 = 33554432(0x2000000, float:9.403955E-38)
            if (r4 == 0) goto L_0x00e7
            r4 = r1
            org.telegram.tgnet.TLRPC$TL_updateShortSentMessage r4 = (org.telegram.tgnet.TLRPC$TL_updateShortSentMessage) r4
            r2 = 0
            int r3 = r4.id
            r17 = 0
            r18 = 0
            r0 = r27
            r1 = r32
            r15 = r4
            r4 = r17
            r11 = 4
            r5 = r18
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
            int r1 = r15.flags
            r1 = r1 & r16
            if (r1 == 0) goto L_0x0063
            int r1 = r15.ttl_period
            r10.ttl_period = r1
            int r1 = r10.flags
            r1 = r1 | r16
            r10.flags = r1
        L_0x0063:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r15.media
            if (r1 == 0) goto L_0x0072
            r10.media = r1
            int r1 = r10.flags
            r1 = r1 | 512(0x200, float:7.175E-43)
            r10.flags = r1
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r30)
        L_0x0072:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r15.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 != 0) goto L_0x007c
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r1 == 0) goto L_0x0088
        L_0x007c:
            java.lang.String r1 = r15.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0088
            java.lang.String r1 = r15.message
            r10.message = r1
        L_0x0088:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r10.entities
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0096
            int r1 = r10.flags
            r1 = r1 | 128(0x80, float:1.794E-43)
            r10.flags = r1
        L_0x0096:
            org.telegram.messenger.MessagesController r1 = r27.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            long r2 = r10.dialog_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x00c9
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
        L_0x00c9:
            int r1 = r1.intValue()
            int r2 = r10.id
            if (r1 >= r2) goto L_0x00d3
            r1 = 1
            goto L_0x00d4
        L_0x00d3:
            r1 = 0
        L_0x00d4:
            r10.unread = r1
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda67 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda67
            r2.<init>(r8, r15)
            r1.postRunnable(r2)
            r7.add(r10)
            r12 = r0
            r13 = 0
            goto L_0x027f
        L_0x00e7:
            r11 = 4
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$Updates
            if (r4 == 0) goto L_0x027d
            r15 = r1
            org.telegram.tgnet.TLRPC$Updates r15 = (org.telegram.tgnet.TLRPC$Updates) r15
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r15.updates
            r4 = 0
        L_0x00f2:
            int r5 = r1.size()
            if (r4 >= r5) goto L_0x01ba
            java.lang.Object r5 = r1.get(r4)
            org.telegram.tgnet.TLRPC$Update r5 = (org.telegram.tgnet.TLRPC$Update) r5
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewMessage
            if (r3 == 0) goto L_0x011a
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewMessage) r5
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            org.telegram.messenger.DispatchQueue r11 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda65 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda65
            r2.<init>(r8, r5)
            r11.postRunnable(r2)
            r1.remove(r4)
        L_0x0116:
            r12 = r3
            r2 = 0
            goto L_0x01bc
        L_0x011a:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage
            if (r2 == 0) goto L_0x01a2
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage) r5
            long r2 = org.telegram.messenger.MessagesController.getUpdateChannelId(r5)
            org.telegram.messenger.MessagesController r11 = r27.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r11.getChat(r2)
            if (r2 == 0) goto L_0x0136
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x018d
        L_0x0136:
            org.telegram.tgnet.TLRPC$Message r2 = r5.message
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r2.reply_to
            if (r2 == 0) goto L_0x018d
            int r3 = r2.reply_to_top_id
            if (r3 != 0) goto L_0x0144
            int r2 = r2.reply_to_msg_id
            if (r2 == 0) goto L_0x018d
        L_0x0144:
            androidx.collection.LongSparseArray r2 = new androidx.collection.LongSparseArray
            r2.<init>()
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            long r12 = org.telegram.messenger.MessageObject.getDialogId(r3)
            java.lang.Object r3 = r2.get(r12)
            android.util.SparseArray r3 = (android.util.SparseArray) r3
            if (r3 != 0) goto L_0x015f
            android.util.SparseArray r3 = new android.util.SparseArray
            r3.<init>()
            r2.put(r12, r3)
        L_0x015f:
            org.telegram.tgnet.TLRPC$Message r12 = r5.message
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r12 = r12.reply_to
            int r13 = r12.reply_to_top_id
            if (r13 == 0) goto L_0x0168
            goto L_0x016a
        L_0x0168:
            int r13 = r12.reply_to_msg_id
        L_0x016a:
            java.lang.Object r12 = r3.get(r13)
            org.telegram.tgnet.TLRPC$MessageReplies r12 = (org.telegram.tgnet.TLRPC$MessageReplies) r12
            if (r12 != 0) goto L_0x017a
            org.telegram.tgnet.TLRPC$TL_messageReplies r12 = new org.telegram.tgnet.TLRPC$TL_messageReplies
            r12.<init>()
            r3.put(r13, r12)
        L_0x017a:
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id
            if (r3 == 0) goto L_0x0186
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r13 = r12.recent_repliers
            r11 = 0
            r13.add(r11, r3)
        L_0x0186:
            int r3 = r12.replies
            r11 = 1
            int r3 = r3 + r11
            r12.replies = r3
            goto L_0x018e
        L_0x018d:
            r2 = 0
        L_0x018e:
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            org.telegram.messenger.DispatchQueue r12 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda64 r13 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda64
            r13.<init>(r8, r5)
            r12.postRunnable(r13)
            r1.remove(r4)
            r12 = r3
            goto L_0x01bc
        L_0x01a2:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x01b2
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage) r5
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            r1.remove(r4)
            goto L_0x0116
        L_0x01b2:
            int r4 = r4 + 1
            r3 = 2147483646(0x7ffffffe, float:NaN)
            r11 = 4
            goto L_0x00f2
        L_0x01ba:
            r2 = 0
            r12 = 0
        L_0x01bc:
            if (r2 == 0) goto L_0x01e0
            org.telegram.messenger.MessagesStorage r1 = r27.getMessagesStorage()
            r3 = 0
            r4 = 1
            r1.putChannelViews(r3, r3, r2, r4)
            org.telegram.messenger.NotificationCenter r1 = r27.getNotificationCenter()
            int r5 = org.telegram.messenger.NotificationCenter.didUpdateMessagesViews
            r11 = 4
            java.lang.Object[] r13 = new java.lang.Object[r11]
            r11 = 0
            r13[r11] = r3
            r13[r4] = r3
            r3 = 2
            r13[r3] = r2
            java.lang.Boolean r2 = java.lang.Boolean.TRUE
            r3 = 3
            r13[r3] = r2
            r1.postNotificationName(r5, r13)
        L_0x01e0:
            if (r12 == 0) goto L_0x026d
            org.telegram.messenger.MessageObject.getDialogId(r12)
            if (r0 == 0) goto L_0x01f0
            int r0 = r12.date
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 == r1) goto L_0x01f0
            r13 = 0
            goto L_0x01f1
        L_0x01f0:
            r13 = r9
        L_0x01f1:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r12)
            if (r13 != 0) goto L_0x0236
            org.telegram.messenger.MessagesController r0 = r27.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r0.dialogs_read_outbox_max
            long r1 = r12.dialog_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0229
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
        L_0x0229:
            int r0 = r0.intValue()
            int r1 = r12.id
            if (r0 >= r1) goto L_0x0233
            r0 = 1
            goto L_0x0234
        L_0x0233:
            r0 = 0
        L_0x0234:
            r12.unread = r0
        L_0x0236:
            r5 = r32
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.lang.String r1 = r12.post_author
            r0.post_author = r1
            int r1 = r12.flags
            r1 = r1 & r16
            if (r1 == 0) goto L_0x024e
            int r1 = r12.ttl_period
            r0.ttl_period = r1
            int r1 = r0.flags
            r1 = r1 | r16
            r0.flags = r1
        L_0x024e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r12.entities
            r0.entities = r1
            int r3 = r12.id
            r16 = 0
            r0 = r27
            r1 = r32
            r2 = r12
            r4 = r33
            r5 = r16
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r0 = r32.getMediaExistanceFlags()
            int r1 = r12.id
            r10.id = r1
            r1 = r0
            r0 = 0
            goto L_0x0270
        L_0x026d:
            r13 = r9
            r0 = 1
            r1 = 0
        L_0x0270:
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda69 r3 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda69
            r3.<init>(r8, r15)
            r2.postRunnable(r3)
            r15 = r0
            r12 = r1
            goto L_0x0280
        L_0x027d:
            r13 = r9
            r12 = 0
        L_0x027f:
            r15 = 0
        L_0x0280:
            boolean r0 = org.telegram.messenger.MessageObject.isLiveLocationMessage(r30)
            r1 = 0
            if (r0 == 0) goto L_0x029d
            long r3 = r10.via_bot_id
            int r0 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x029d
            java.lang.String r0 = r10.via_bot_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x029d
            org.telegram.messenger.LocationController r0 = r27.getLocationController()
            r0.addSharingLocation(r10)
        L_0x029d:
            if (r15 != 0) goto L_0x0358
            org.telegram.messenger.StatsController r0 = r27.getStatsController()
            int r3 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r4 = 1
            r0.incrementSentItemsCount(r3, r4, r4)
            r0 = 0
            r10.send_state = r0
            if (r9 == 0) goto L_0x02f0
            if (r13 != 0) goto L_0x02f0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            r0.add(r1)
            org.telegram.messenger.MessagesController r19 = r27.getMessagesController()
            r21 = 0
            r22 = 0
            long r1 = r10.dialog_id
            r25 = 0
            r26 = 1
            r20 = r0
            r23 = r1
            r19.deleteMessages(r20, r21, r22, r23, r25, r26)
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r12 = r0.getStorageQueue()
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda35 r13 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda35
            r0 = r13
            r1 = r27
            r2 = r7
            r3 = r32
            r4 = r30
            r5 = r6
            r6 = r28
            r7 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r12.postRunnable(r13)
            goto L_0x0358
        L_0x02f0:
            org.telegram.messenger.NotificationCenter r0 = r27.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            r4 = 7
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r6)
            r13 = 0
            r4[r13] = r5
            int r5 = r10.id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r11 = 1
            r4[r11] = r5
            r5 = 2
            r4[r5] = r10
            r31 = r12
            long r11 = r10.dialog_id
            java.lang.Long r5 = java.lang.Long.valueOf(r11)
            r11 = 3
            r4[r11] = r5
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r2 = 4
            r4[r2] = r1
            r1 = 5
            java.lang.Integer r2 = java.lang.Integer.valueOf(r31)
            r4[r1] = r2
            r1 = 6
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r28)
            r4[r1] = r2
            r0.postNotificationName(r3, r4)
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r11 = r0.getStorageQueue()
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda49 r12 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda49
            r0 = r12
            r1 = r27
            r2 = r30
            r3 = r6
            r4 = r28
            r5 = r7
            r6 = r31
            r7 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r11.postRunnable(r12)
            goto L_0x0358
        L_0x034c:
            int r1 = r8.currentAccount
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r34
            r4 = 0
            org.telegram.ui.Components.AlertsCreator.processError(r1, r0, r4, r2, r3)
            r15 = 1
        L_0x0358:
            if (r15 == 0) goto L_0x039a
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
            if (r0 != 0) goto L_0x0390
            boolean r0 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r30)
            if (r0 != 0) goto L_0x0390
            boolean r0 = org.telegram.messenger.MessageObject.isNewGifMessage(r30)
            if (r0 == 0) goto L_0x0395
        L_0x0390:
            java.lang.String r0 = r10.attachPath
            r8.stopVideoService(r0)
        L_0x0395:
            int r0 = r10.id
            r8.removeFromSendingMessages(r0, r9)
        L_0x039a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$performSendMessageRequest$59(boolean, org.telegram.tgnet.TLRPC$TL_error, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLObject, org.telegram.messenger.MessageObject, java.lang.String, org.telegram.tgnet.TLObject):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$51(TLRPC$TL_updateShortSentMessage tLRPC$TL_updateShortSentMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateShortSentMessage.pts, tLRPC$TL_updateShortSentMessage.date, tLRPC$TL_updateShortSentMessage.pts_count);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$52(TLRPC$TL_updateNewMessage tLRPC$TL_updateNewMessage) {
        getMessagesController().processNewDifferenceParams(-1, tLRPC$TL_updateNewMessage.pts, -1, tLRPC$TL_updateNewMessage.pts_count);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$53(TLRPC$TL_updateNewChannelMessage tLRPC$TL_updateNewChannelMessage) {
        getMessagesController().processNewChannelDifferenceParams(tLRPC$TL_updateNewChannelMessage.pts, tLRPC$TL_updateNewChannelMessage.pts_count, tLRPC$TL_updateNewChannelMessage.message.peer_id.channel_id);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$54(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().processUpdates(tLRPC$Updates, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$56(ArrayList arrayList, MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, boolean z, String str) {
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda38(this, messageObject, tLRPC$Message, i, z));
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(str);
            return;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$55(MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true, true));
        getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$58(TLRPC$Message tLRPC$Message, int i, boolean z, ArrayList arrayList, int i2, String str) {
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, MessageObject.getPeerId(tLRPC$Message2.peer_id), Integer.valueOf(i), tLRPC$Message2.id, 0, false, z ? 1 : 0);
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, z);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda47(this, tLRPC$Message, i, i2, z));
        if (MessageObject.isVideoMessage(tLRPC$Message) || MessageObject.isRoundVideoMessage(tLRPC$Message) || MessageObject.isNewGifMessage(tLRPC$Message)) {
            stopVideoService(str);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$57(TLRPC$Message tLRPC$Message, int i, int i2, boolean z) {
        getMediaDataController().increasePeerRaiting(tLRPC$Message.dialog_id);
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i), Integer.valueOf(tLRPC$Message.id), tLRPC$Message, Long.valueOf(tLRPC$Message.dialog_id), 0L, Integer.valueOf(i2), Boolean.valueOf(z));
        processSentMessage(i);
        removeFromSendingMessages(i, z);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$62(TLRPC$Message tLRPC$Message) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda46(this, tLRPC$Message, tLRPC$Message.id));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$61(TLRPC$Message tLRPC$Message, int i) {
        tLRPC$Message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(i));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:119:0x021f, code lost:
        r7 = r3.location.volume_id + "_" + r3.location.local_id;
        r11 = r9.location.volume_id + "_" + r9.location.local_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0257, code lost:
        if (r7.equals(r11) == false) goto L_0x025a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x025a, code lost:
        r12 = new java.io.File(org.telegram.messenger.FileLoader.getDirectory(4), r7 + ".jpg");
        r4 = r8.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0277, code lost:
        if (r4.ttl_seconds != 0) goto L_0x0298;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x0281, code lost:
        if (r4.photo.sizes.size() == r15) goto L_0x028d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x0287, code lost:
        if (r9.w > 90) goto L_0x028d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x028b, code lost:
        if (r9.h <= 90) goto L_0x0298;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x028d, code lost:
        r4 = org.telegram.messenger.FileLoader.getInstance(r0.currentAccount).getPathToAttach(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0298, code lost:
        r4 = new java.io.File(org.telegram.messenger.FileLoader.getDirectory(4), r11 + ".jpg");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x02b1, code lost:
        r12.renameTo(r4);
        org.telegram.messenger.ImageLoader.getInstance().replaceImageInCache(r7, r11, org.telegram.messenger.ImageLocation.getForPhoto(r9, r8.media.photo), r2);
        r3.location = r9.location;
        r3.size = r9.size;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x02cb, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004e, code lost:
        r6 = (r6 = r8.media).photo;
     */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x05e9  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0653  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0109  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMediaPaths(org.telegram.messenger.MessageObject r20, org.telegram.tgnet.TLRPC$Message r21, int r22, java.lang.String r23, boolean r24) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            r8 = r21
            r9 = r23
            r2 = r24
            org.telegram.tgnet.TLRPC$Message r10 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            java.lang.String r5 = "_"
            if (r3 == 0) goto L_0x013a
            boolean r3 = r20.isLiveLocation()
            if (r3 == 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r8.media
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r10.media
            int r3 = r3.period
            r6.period = r3
            goto L_0x00d3
        L_0x0026:
            boolean r3 = r20.isDice()
            if (r3 == 0) goto L_0x003a
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaDice) r3
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r8.media
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r6 = (org.telegram.tgnet.TLRPC$TL_messageMediaDice) r6
            int r6 = r6.value
            r3.value = r6
            goto L_0x00d3
        L_0x003a:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Photo r6 = r3.photo
            r7 = 40
            if (r6 == 0) goto L_0x0060
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r6.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r7)
            if (r8 == 0) goto L_0x0059
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r8.media
            if (r6 == 0) goto L_0x0059
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            if (r6 == 0) goto L_0x0059
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7)
            goto L_0x005a
        L_0x0059:
            r6 = r3
        L_0x005a:
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r10.media
            org.telegram.tgnet.TLRPC$Photo r7 = r7.photo
            goto L_0x00d6
        L_0x0060:
            org.telegram.tgnet.TLRPC$Document r6 = r3.document
            if (r6 == 0) goto L_0x0081
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r6.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r7)
            if (r8 == 0) goto L_0x007b
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r8.media
            if (r6 == 0) goto L_0x007b
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            if (r6 == 0) goto L_0x007b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7)
            goto L_0x007c
        L_0x007b:
            r6 = r3
        L_0x007c:
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r10.media
            org.telegram.tgnet.TLRPC$Document r7 = r7.document
            goto L_0x00d6
        L_0x0081:
            org.telegram.tgnet.TLRPC$WebPage r3 = r3.webpage
            if (r3 == 0) goto L_0x00d3
            org.telegram.tgnet.TLRPC$Photo r6 = r3.photo
            if (r6 == 0) goto L_0x00ac
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r6.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r7)
            if (r8 == 0) goto L_0x00a4
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r8.media
            if (r6 == 0) goto L_0x00a4
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            if (r6 == 0) goto L_0x00a4
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            if (r6 == 0) goto L_0x00a4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.sizes
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7)
            goto L_0x00a5
        L_0x00a4:
            r6 = r3
        L_0x00a5:
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r10.media
            org.telegram.tgnet.TLRPC$WebPage r7 = r7.webpage
            org.telegram.tgnet.TLRPC$Photo r7 = r7.photo
            goto L_0x00d6
        L_0x00ac:
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            if (r3 == 0) goto L_0x00d3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r7)
            if (r8 == 0) goto L_0x00cb
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r8.media
            if (r6 == 0) goto L_0x00cb
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            if (r6 == 0) goto L_0x00cb
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            if (r6 == 0) goto L_0x00cb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7)
            goto L_0x00cc
        L_0x00cb:
            r6 = r3
        L_0x00cc:
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r10.media
            org.telegram.tgnet.TLRPC$WebPage r7 = r7.webpage
            org.telegram.tgnet.TLRPC$Document r7 = r7.document
            goto L_0x00d6
        L_0x00d3:
            r3 = 0
            r6 = 0
            r7 = 0
        L_0x00d6:
            boolean r11 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r11 == 0) goto L_0x013b
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r3 == 0) goto L_0x013b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r11 = "stripped"
            r3.append(r11)
            java.lang.String r12 = org.telegram.messenger.FileRefController.getKeyForParentObject(r20)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            if (r8 == 0) goto L_0x0109
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r11)
            java.lang.String r11 = org.telegram.messenger.FileRefController.getKeyForParentObject(r21)
            r12.append(r11)
            java.lang.String r11 = r12.toString()
            goto L_0x012e
        L_0x0109:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "strippedmessage"
            r11.append(r12)
            r12 = r22
            r11.append(r12)
            r11.append(r5)
            long r12 = r20.getChannelId()
            r11.append(r12)
            r11.append(r5)
            boolean r12 = r1.scheduled
            r11.append(r12)
            java.lang.String r11 = r11.toString()
        L_0x012e:
            org.telegram.messenger.ImageLoader r12 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForObject(r6, r7)
            r12.replaceImageInCache(r3, r11, r7, r2)
            goto L_0x013b
        L_0x013a:
            r6 = 0
        L_0x013b:
            if (r8 != 0) goto L_0x013e
            return
        L_0x013e:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r8.media
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            java.lang.String r13 = "sent_"
            java.lang.String r14 = ".jpg"
            r4 = 0
            r15 = 1
            if (r7 == 0) goto L_0x035d
            org.telegram.tgnet.TLRPC$Photo r7 = r3.photo
            if (r7 == 0) goto L_0x035d
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r10.media
            boolean r11 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r11 == 0) goto L_0x035d
            org.telegram.tgnet.TLRPC$Photo r7 = r7.photo
            if (r7 == 0) goto L_0x035d
            int r3 = r3.ttl_seconds
            if (r3 != 0) goto L_0x0186
            boolean r1 = r1.scheduled
            if (r1 != 0) goto L_0x0186
            org.telegram.messenger.MessagesStorage r1 = r19.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r8.media
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r13)
            org.telegram.tgnet.TLRPC$Peer r11 = r8.peer_id
            long r11 = r11.channel_id
            r7.append(r11)
            r7.append(r5)
            int r11 = r8.id
            r7.append(r11)
            java.lang.String r7 = r7.toString()
            r1.putSentFile(r9, r3, r4, r7)
        L_0x0186:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r10.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            int r1 = r1.size()
            if (r1 != r15) goto L_0x01b2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r10.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            java.lang.Object r1 = r1.get(r4)
            org.telegram.tgnet.TLRPC$PhotoSize r1 = (org.telegram.tgnet.TLRPC$PhotoSize) r1
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.location
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r1 == 0) goto L_0x01b2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r10.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r8.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            r1.sizes = r2
            goto L_0x033f
        L_0x01b2:
            r1 = 0
        L_0x01b3:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.sizes
            int r3 = r3.size()
            if (r1 >= r3) goto L_0x033f
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.sizes
            java.lang.Object r3 = r3.get(r1)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3
            if (r3 == 0) goto L_0x0337
            org.telegram.tgnet.TLRPC$FileLocation r7 = r3.location
            if (r7 == 0) goto L_0x0337
            java.lang.String r7 = r3.type
            if (r7 != 0) goto L_0x01d7
            goto L_0x0337
        L_0x01d7:
            r7 = 0
        L_0x01d8:
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r8.media
            org.telegram.tgnet.TLRPC$Photo r9 = r9.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.sizes
            int r9 = r9.size()
            if (r7 >= r9) goto L_0x02d3
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r8.media
            org.telegram.tgnet.TLRPC$Photo r9 = r9.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.sizes
            java.lang.Object r9 = r9.get(r7)
            org.telegram.tgnet.TLRPC$PhotoSize r9 = (org.telegram.tgnet.TLRPC$PhotoSize) r9
            if (r9 == 0) goto L_0x02cd
            org.telegram.tgnet.TLRPC$FileLocation r11 = r9.location
            if (r11 == 0) goto L_0x02cd
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r11 != 0) goto L_0x02cd
            java.lang.String r11 = r9.type
            if (r11 != 0) goto L_0x0200
            goto L_0x02cd
        L_0x0200:
            org.telegram.tgnet.TLRPC$FileLocation r12 = r3.location
            long r12 = r12.volume_id
            r16 = -2147483648(0xfffffffvar_, double:NaN)
            int r18 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x0213
            java.lang.String r12 = r3.type
            boolean r11 = r11.equals(r12)
            if (r11 != 0) goto L_0x021f
        L_0x0213:
            int r11 = r9.w
            int r12 = r3.w
            if (r11 != r12) goto L_0x02cd
            int r11 = r9.h
            int r12 = r3.h
            if (r11 != r12) goto L_0x02cd
        L_0x021f:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r11 = r3.location
            long r11 = r11.volume_id
            r7.append(r11)
            r7.append(r5)
            org.telegram.tgnet.TLRPC$FileLocation r11 = r3.location
            int r11 = r11.local_id
            r7.append(r11)
            java.lang.String r7 = r7.toString()
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r12 = r9.location
            long r12 = r12.volume_id
            r11.append(r12)
            r11.append(r5)
            org.telegram.tgnet.TLRPC$FileLocation r12 = r9.location
            int r12 = r12.local_id
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            boolean r12 = r7.equals(r11)
            if (r12 == 0) goto L_0x025a
            goto L_0x02cb
        L_0x025a:
            java.io.File r12 = new java.io.File
            r13 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r7)
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r12.<init>(r4, r13)
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r8.media
            int r13 = r4.ttl_seconds
            if (r13 != 0) goto L_0x0298
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.sizes
            int r4 = r4.size()
            if (r4 == r15) goto L_0x028d
            int r4 = r9.w
            r13 = 90
            if (r4 > r13) goto L_0x028d
            int r4 = r9.h
            if (r4 <= r13) goto L_0x0298
        L_0x028d:
            int r4 = r0.currentAccount
            org.telegram.messenger.FileLoader r4 = org.telegram.messenger.FileLoader.getInstance(r4)
            java.io.File r4 = r4.getPathToAttach(r9)
            goto L_0x02b1
        L_0x0298:
            java.io.File r4 = new java.io.File
            r13 = 4
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r11)
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r4.<init>(r15, r13)
        L_0x02b1:
            r12.renameTo(r4)
            org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r8.media
            org.telegram.tgnet.TLRPC$Photo r12 = r12.photo
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r9, (org.telegram.tgnet.TLRPC$Photo) r12)
            r4.replaceImageInCache(r7, r11, r12, r2)
            org.telegram.tgnet.TLRPC$FileLocation r4 = r9.location
            r3.location = r4
            int r4 = r9.size
            r3.size = r4
        L_0x02cb:
            r4 = 1
            goto L_0x02d4
        L_0x02cd:
            int r7 = r7 + 1
            r4 = 0
            r15 = 1
            goto L_0x01d8
        L_0x02d3:
            r4 = 0
        L_0x02d4:
            if (r4 != 0) goto L_0x0337
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r7 = r3.location
            long r11 = r7.volume_id
            r4.append(r11)
            r4.append(r5)
            org.telegram.tgnet.TLRPC$FileLocation r7 = r3.location
            int r7 = r7.local_id
            r4.append(r7)
            java.lang.String r4 = r4.toString()
            java.io.File r7 = new java.io.File
            r9 = 4
            java.io.File r11 = org.telegram.messenger.FileLoader.getDirectory(r9)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r4)
            r9.append(r14)
            java.lang.String r9 = r9.toString()
            r7.<init>(r11, r9)
            r7.delete()
            java.lang.String r3 = r3.type
            java.lang.String r7 = "s"
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x0337
            if (r6 == 0) goto L_0x0337
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.sizes
            r3.set(r1, r6)
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r8.media
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r6, (org.telegram.tgnet.TLRPC$Photo) r3)
            org.telegram.messenger.ImageLoader r7 = org.telegram.messenger.ImageLoader.getInstance()
            r11 = 0
            r12 = 0
            java.lang.String r9 = r3.getKey(r8, r11, r12)
            r7.replaceImageInCache(r4, r9, r3, r2)
            goto L_0x0339
        L_0x0337:
            r11 = 0
            r12 = 0
        L_0x0339:
            int r1 = r1 + 1
            r4 = 0
            r15 = 1
            goto L_0x01b3
        L_0x033f:
            java.lang.String r1 = r8.message
            r10.message = r1
            java.lang.String r1 = r10.attachPath
            r8.attachPath = r1
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r10.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r8.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            long r3 = r2.id
            r1.id = r3
            int r3 = r2.dc_id
            r1.dc_id = r3
            long r2 = r2.access_hash
            r1.access_hash = r2
            goto L_0x06b1
        L_0x035d:
            r11 = 0
            r12 = 0
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r4 == 0) goto L_0x065c
            org.telegram.tgnet.TLRPC$Document r4 = r3.document
            if (r4 == 0) goto L_0x065c
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r10.media
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x065c
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            if (r4 == 0) goto L_0x065c
            int r3 = r3.ttl_seconds
            if (r3 != 0) goto L_0x0412
            org.telegram.messenger.VideoEditedInfo r3 = r1.videoEditedInfo
            if (r3 == 0) goto L_0x038b
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r4 = r3.mediaEntities
            if (r4 != 0) goto L_0x0412
            java.lang.String r3 = r3.paintPath
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 == 0) goto L_0x0412
            org.telegram.messenger.VideoEditedInfo r3 = r1.videoEditedInfo
            org.telegram.messenger.MediaController$CropState r3 = r3.cropState
            if (r3 != 0) goto L_0x0412
        L_0x038b:
            boolean r3 = org.telegram.messenger.MessageObject.isVideoMessage(r21)
            if (r3 != 0) goto L_0x0397
            boolean r4 = org.telegram.messenger.MessageObject.isGifMessage(r21)
            if (r4 == 0) goto L_0x03db
        L_0x0397:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r8.media
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            boolean r4 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r4)
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r10.media
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            boolean r6 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r6)
            if (r4 != r6) goto L_0x03db
            boolean r4 = r1.scheduled
            if (r4 != 0) goto L_0x03d4
            org.telegram.messenger.MessagesStorage r4 = r19.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r8.media
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            r7 = 2
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r13)
            org.telegram.tgnet.TLRPC$Peer r13 = r8.peer_id
            long r11 = r13.channel_id
            r15.append(r11)
            r15.append(r5)
            int r11 = r8.id
            r15.append(r11)
            java.lang.String r11 = r15.toString()
            r4.putSentFile(r9, r6, r7, r11)
        L_0x03d4:
            if (r3 == 0) goto L_0x0412
            java.lang.String r3 = r10.attachPath
            r8.attachPath = r3
            goto L_0x0412
        L_0x03db:
            boolean r3 = org.telegram.messenger.MessageObject.isVoiceMessage(r21)
            if (r3 != 0) goto L_0x0412
            boolean r3 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r21)
            if (r3 != 0) goto L_0x0412
            boolean r3 = r1.scheduled
            if (r3 != 0) goto L_0x0412
            org.telegram.messenger.MessagesStorage r3 = r19.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r8.media
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r13)
            org.telegram.tgnet.TLRPC$Peer r7 = r8.peer_id
            long r11 = r7.channel_id
            r6.append(r11)
            r6.append(r5)
            int r7 = r8.id
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r7 = 1
            r3.putSentFile(r9, r4, r7, r6)
        L_0x0412:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            r4 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4)
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r8.media
            org.telegram.tgnet.TLRPC$Document r6 = r6.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r4)
            if (r3 == 0) goto L_0x04cb
            org.telegram.tgnet.TLRPC$FileLocation r6 = r3.location
            if (r6 == 0) goto L_0x04cb
            long r6 = r6.volume_id
            r11 = -2147483648(0xfffffffvar_, double:NaN)
            int r13 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r13 != 0) goto L_0x04cb
            if (r4 == 0) goto L_0x04cb
            org.telegram.tgnet.TLRPC$FileLocation r6 = r4.location
            if (r6 == 0) goto L_0x04cb
            boolean r6 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r6 != 0) goto L_0x04cb
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r6 != 0) goto L_0x04cb
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r7 = r3.location
            long r11 = r7.volume_id
            r6.append(r11)
            r6.append(r5)
            org.telegram.tgnet.TLRPC$FileLocation r7 = r3.location
            int r7 = r7.local_id
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r11 = r4.location
            long r11 = r11.volume_id
            r7.append(r11)
            r7.append(r5)
            org.telegram.tgnet.TLRPC$FileLocation r5 = r4.location
            int r5 = r5.local_id
            r7.append(r5)
            java.lang.String r5 = r7.toString()
            boolean r7 = r6.equals(r5)
            if (r7 != 0) goto L_0x04f4
            java.io.File r7 = new java.io.File
            r11 = 4
            java.io.File r12 = org.telegram.messenger.FileLoader.getDirectory(r11)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r6)
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r7.<init>(r12, r13)
            java.io.File r12 = new java.io.File
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r11)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r5)
            r11.append(r14)
            java.lang.String r11 = r11.toString()
            r12.<init>(r13, r11)
            r7.renameTo(r12)
            org.telegram.messenger.ImageLoader r7 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r8.media
            org.telegram.tgnet.TLRPC$Document r11 = r11.document
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r4, (org.telegram.tgnet.TLRPC$Document) r11)
            r7.replaceImageInCache(r6, r5, r11, r2)
            org.telegram.tgnet.TLRPC$FileLocation r2 = r4.location
            r3.location = r2
            int r2 = r4.size
            r3.size = r2
            goto L_0x04f4
        L_0x04cb:
            if (r4 == 0) goto L_0x04dc
            if (r3 == 0) goto L_0x04dc
            boolean r2 = org.telegram.messenger.MessageObject.isStickerMessage(r21)
            if (r2 == 0) goto L_0x04dc
            org.telegram.tgnet.TLRPC$FileLocation r2 = r3.location
            if (r2 == 0) goto L_0x04dc
            r4.location = r2
            goto L_0x04f4
        L_0x04dc:
            if (r3 == 0) goto L_0x04e8
            org.telegram.tgnet.TLRPC$FileLocation r2 = r3.location
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r2 != 0) goto L_0x04e8
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r2 == 0) goto L_0x04f4
        L_0x04e8:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r10.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r8.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r3.thumbs
            r2.thumbs = r3
        L_0x04f4:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r10.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r8.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            int r4 = r3.dc_id
            r2.dc_id = r4
            long r4 = r3.id
            r2.id = r4
            long r3 = r3.access_hash
            r2.access_hash = r3
            r2 = 0
        L_0x0509:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x052b
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r4 == 0) goto L_0x0528
            byte[] r4 = r3.waveform
            goto L_0x052c
        L_0x0528:
            int r2 = r2 + 1
            goto L_0x0509
        L_0x052b:
            r4 = 0
        L_0x052c:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r10.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r8.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            r2.attributes = r3
            if (r4 == 0) goto L_0x0562
            r2 = 0
        L_0x053b:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0562
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r3.attributes
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$DocumentAttribute r3 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r3
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r5 == 0) goto L_0x055f
            r3.waveform = r4
            int r5 = r3.flags
            r6 = 4
            r5 = r5 | r6
            r3.flags = r5
        L_0x055f:
            int r2 = r2 + 1
            goto L_0x053b
        L_0x0562:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r10.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r8.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            long r4 = r3.size
            r2.size = r4
            java.lang.String r3 = r3.mime_type
            r2.mime_type = r3
            int r2 = r8.flags
            r3 = 4
            r2 = r2 & r3
            if (r2 != 0) goto L_0x05d5
            boolean r2 = org.telegram.messenger.MessageObject.isOut(r21)
            if (r2 == 0) goto L_0x05d5
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r8.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            boolean r2 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r2)
            if (r2 == 0) goto L_0x05ad
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r8.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            boolean r2 = org.telegram.messenger.MessageObject.isDocumentHasAttachedStickers(r2)
            if (r2 == 0) goto L_0x0599
            org.telegram.messenger.MessagesController r2 = r19.getMessagesController()
            boolean r2 = r2.saveGifsWithStickers
            goto L_0x059a
        L_0x0599:
            r2 = 1
        L_0x059a:
            if (r2 == 0) goto L_0x05ab
            org.telegram.messenger.MediaDataController r2 = r19.getMediaDataController()
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r8.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            int r4 = r8.date
            r5 = 1
            r2.addRecentGif(r3, r4, r5)
            goto L_0x05d5
        L_0x05ab:
            r5 = 1
            goto L_0x05d5
        L_0x05ad:
            r5 = 1
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r8.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            boolean r2 = org.telegram.messenger.MessageObject.isStickerDocument(r2)
            if (r2 != 0) goto L_0x05c2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r8.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            boolean r2 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r2, r5)
            if (r2 == 0) goto L_0x05d5
        L_0x05c2:
            org.telegram.messenger.MediaDataController r2 = r19.getMediaDataController()
            r3 = 0
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r8.media
            org.telegram.tgnet.TLRPC$Document r5 = r4.document
            int r6 = r8.date
            r7 = 0
            r11 = 0
            r4 = r21
            r2.addRecentSticker(r3, r4, r5, r6, r7)
            goto L_0x05d6
        L_0x05d5:
            r11 = 0
        L_0x05d6:
            java.lang.String r2 = r10.attachPath
            if (r2 == 0) goto L_0x0653
            r3 = 4
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r3)
            java.lang.String r3 = r3.getAbsolutePath()
            boolean r2 = r2.startsWith(r3)
            if (r2 == 0) goto L_0x0653
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r10.attachPath
            r2.<init>(r3)
            int r3 = r0.currentAccount
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r8.media
            org.telegram.tgnet.TLRPC$Document r5 = r4.document
            int r4 = r4.ttl_seconds
            if (r4 == 0) goto L_0x0600
            r4 = 1
            goto L_0x0601
        L_0x0600:
            r4 = 0
        L_0x0601:
            java.io.File r3 = r3.getPathToAttach(r5, r4)
            boolean r4 = r2.renameTo(r3)
            if (r4 != 0) goto L_0x0624
            boolean r2 = r2.exists()
            if (r2 == 0) goto L_0x0616
            java.lang.String r2 = r10.attachPath
            r8.attachPath = r2
            goto L_0x0618
        L_0x0616:
            r1.attachPathExists = r11
        L_0x0618:
            boolean r2 = r3.exists()
            r1.mediaExists = r2
            java.lang.String r1 = r10.message
            r8.message = r1
            goto L_0x06b1
        L_0x0624:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r21)
            if (r2 == 0) goto L_0x062f
            r2 = 1
            r1.attachPathExists = r2
            goto L_0x06b1
        L_0x062f:
            boolean r2 = r1.attachPathExists
            r1.mediaExists = r2
            r1.attachPathExists = r11
            java.lang.String r1 = ""
            r10.attachPath = r1
            if (r9 == 0) goto L_0x06b1
            java.lang.String r1 = "http"
            boolean r1 = r9.startsWith(r1)
            if (r1 == 0) goto L_0x06b1
            org.telegram.messenger.MessagesStorage r1 = r19.getMessagesStorage()
            java.lang.String r2 = r3.toString()
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r10.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            r1.addRecentLocalFile(r9, r2, r3)
            goto L_0x06b1
        L_0x0653:
            java.lang.String r1 = r10.attachPath
            r8.attachPath = r1
            java.lang.String r1 = r10.message
            r8.message = r1
            goto L_0x06b1
        L_0x065c:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r1 == 0) goto L_0x0669
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r10.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r1 == 0) goto L_0x0669
            r10.media = r3
            goto L_0x06b1
        L_0x0669:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0670
            r10.media = r3
            goto L_0x06b1
        L_0x0670:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r1 == 0) goto L_0x0683
            org.telegram.tgnet.TLRPC$GeoPoint r1 = r3.geo
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r10.media
            org.telegram.tgnet.TLRPC$GeoPoint r2 = r2.geo
            double r3 = r2.lat
            r1.lat = r3
            double r2 = r2._long
            r1._long = r2
            goto L_0x06b1
        L_0x0683:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 != 0) goto L_0x0693
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r1 == 0) goto L_0x068c
            goto L_0x0693
        L_0x068c:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r1 == 0) goto L_0x06b1
            r10.media = r3
            goto L_0x06b1
        L_0x0693:
            r10.media = r3
            java.lang.String r1 = r8.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x06a5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r8.entities
            r10.entities = r1
            java.lang.String r1 = r8.message
            r10.message = r1
        L_0x06a5:
            org.telegram.tgnet.TLRPC$ReplyMarkup r1 = r8.reply_markup
            if (r1 == 0) goto L_0x06b1
            r10.reply_markup = r1
            int r1 = r10.flags
            r1 = r1 | 64
            r10.flags = r1
        L_0x06b1:
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda33(this, arrayList3, arrayList4, arrayList5, arrayList, arrayList2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processUnsentMessages$63(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5) {
        HashMap<String, String> hashMap;
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        int size = arrayList4.size();
        for (int i = 0; i < size; i++) {
            MessageObject messageObject = new MessageObject(this.currentAccount, (TLRPC$Message) arrayList4.get(i), false, true);
            long groupId = messageObject.getGroupId();
            if (groupId != 0 && (hashMap = messageObject.messageOwner.params) != null && !hashMap.containsKey("final") && (i == size - 1 || ((TLRPC$Message) arrayList4.get(i + 1)).grouped_id != groupId)) {
                messageObject.messageOwner.params.put("final", "1");
            }
            retrySendMessage(messageObject, true);
        }
        if (arrayList5 != null) {
            for (int i2 = 0; i2 < arrayList5.size(); i2++) {
                MessageObject messageObject2 = new MessageObject(this.currentAccount, (TLRPC$Message) arrayList5.get(i2), false, true);
                messageObject2.scheduled = true;
                retrySendMessage(messageObject2, true);
            }
        }
    }

    public ImportingStickers getImportingStickers(String str) {
        return this.importingStickersMap.get(str);
    }

    public ImportingHistory getImportingHistory(long j) {
        return this.importingHistoryMap.get(j);
    }

    public boolean isImportingStickers() {
        return this.importingStickersMap.size() != 0;
    }

    public boolean isImportingHistory() {
        return this.importingHistoryMap.size() != 0;
    }

    public void prepareImportHistory(long j, Uri uri, ArrayList<Uri> arrayList, MessagesStorage.LongCallback longCallback) {
        if (this.importingHistoryMap.get(j) != null) {
            longCallback.run(0);
            return;
        }
        if (DialogObject.isChatDialog(j)) {
            long j2 = -j;
            TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(j2));
            if (chat != null && !chat.megagroup) {
                getMessagesController().convertToMegaGroup((Context) null, j2, (BaseFragment) null, new SendMessagesHelper$$ExternalSyntheticLambda76(this, uri, arrayList, longCallback));
                return;
            }
        }
        new Thread(new SendMessagesHelper$$ExternalSyntheticLambda32(this, arrayList, j, uri, longCallback)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareImportHistory$64(Uri uri, ArrayList arrayList, MessagesStorage.LongCallback longCallback, long j) {
        if (j != 0) {
            prepareImportHistory(-j, uri, arrayList, longCallback);
            return;
        }
        longCallback.run(0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareImportHistory$69(ArrayList arrayList, long j, Uri uri, MessagesStorage.LongCallback longCallback) {
        Uri uri2;
        long j2 = j;
        MessagesStorage.LongCallback longCallback2 = longCallback;
        ArrayList arrayList2 = arrayList != null ? arrayList : new ArrayList();
        ImportingHistory importingHistory = new ImportingHistory();
        importingHistory.mediaPaths = arrayList2;
        importingHistory.dialogId = j2;
        importingHistory.peer = getMessagesController().getInputPeer(j2);
        HashMap hashMap = new HashMap();
        int size = arrayList2.size();
        for (int i = 0; i < size + 1; i++) {
            if (i == 0) {
                uri2 = uri;
            } else {
                uri2 = (Uri) arrayList2.get(i - 1);
            }
            if (uri2 != null && !AndroidUtilities.isInternalUri(uri2)) {
                String copyFileToCache = MediaController.copyFileToCache(uri2, "txt");
                if (copyFileToCache == null) {
                    continue;
                } else {
                    File file = new File(copyFileToCache);
                    if (file.exists()) {
                        long length = file.length();
                        if (length != 0) {
                            importingHistory.totalSize += length;
                            if (i != 0) {
                                importingHistory.uploadMedia.add(copyFileToCache);
                            } else if (length > 33554432) {
                                file.delete();
                                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda19(longCallback2));
                                return;
                            } else {
                                importingHistory.historyPath = copyFileToCache;
                            }
                            importingHistory.uploadSet.add(copyFileToCache);
                            hashMap.put(copyFileToCache, importingHistory);
                        }
                    }
                    if (i == 0) {
                        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda18(longCallback2));
                        return;
                    }
                }
            } else if (i == 0) {
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda17(longCallback2));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda36(this, hashMap, j, importingHistory, longCallback));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareImportHistory$67(MessagesStorage.LongCallback longCallback) {
        Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("ImportFileTooLarge", NUM), 0).show();
        longCallback.run(0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareImportHistory$68(HashMap hashMap, long j, ImportingHistory importingHistory, MessagesStorage.LongCallback longCallback) {
        this.importingHistoryFiles.putAll(hashMap);
        this.importingHistoryMap.put(j, importingHistory);
        getFileLoader().uploadFile(importingHistory.historyPath, false, true, 0, 67108864, true);
        getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(j));
        longCallback.run(j);
        try {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, ImportingService.class));
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void prepareImportStickers(String str, String str2, String str3, ArrayList<ImportingSticker> arrayList, MessagesStorage.StringCallback stringCallback) {
        if (this.importingStickersMap.get(str2) != null) {
            stringCallback.run((String) null);
        } else {
            new Thread(new SendMessagesHelper$$ExternalSyntheticLambda30(this, str, str2, str3, arrayList, stringCallback)).start();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareImportStickers$72(String str, String str2, String str3, ArrayList arrayList, MessagesStorage.StringCallback stringCallback) {
        ImportingStickers importingStickers = new ImportingStickers();
        importingStickers.title = str;
        importingStickers.shortName = str2;
        importingStickers.software = str3;
        HashMap hashMap = new HashMap();
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            ImportingSticker importingSticker = (ImportingSticker) arrayList.get(i);
            File file = new File(importingSticker.path);
            if (file.exists()) {
                long length = file.length();
                if (length != 0) {
                    importingStickers.totalSize += length;
                    importingStickers.uploadMedia.add(importingSticker);
                    importingStickers.uploadSet.put(importingSticker.path, importingSticker);
                    hashMap.put(importingSticker.path, importingStickers);
                }
            }
            if (i == 0) {
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda20(stringCallback));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda41(this, importingStickers, hashMap, str2, stringCallback));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareImportStickers$71(ImportingStickers importingStickers, HashMap hashMap, String str, MessagesStorage.StringCallback stringCallback) {
        if (importingStickers.uploadMedia.get(0).item != null) {
            importingStickers.startImport();
        } else {
            this.importingStickersFiles.putAll(hashMap);
            this.importingStickersMap.put(str, importingStickers);
            importingStickers.initImport();
            getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, str);
            stringCallback.run(str);
        }
        try {
            ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, ImportingService.class));
        } catch (Throwable th) {
            FileLog.e(th);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v34, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v39, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0058, code lost:
        if (r3 == false) goto L_0x005c;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x0175 A[SYNTHETIC, Splitter:B:109:0x0175] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0189 A[SYNTHETIC, Splitter:B:117:0x0189] */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x01e5  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x01e8  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0226  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x022c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x03f6 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x046d  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0477  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x047d  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0487  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x0499  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x04a5  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x04fe  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x0503 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r32, java.lang.String r33, java.lang.String r34, android.net.Uri r35, java.lang.String r36, long r37, org.telegram.messenger.MessageObject r39, org.telegram.messenger.MessageObject r40, java.lang.CharSequence r41, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r42, org.telegram.messenger.MessageObject r43, long[] r44, boolean r45, boolean r46, boolean r47, int r48, java.lang.Integer[] r49) {
        /*
            r8 = r32
            r0 = r33
            r1 = r34
            r2 = r35
            r3 = r36
            r9 = 1
            if (r0 == 0) goto L_0x0013
            int r4 = r33.length()
            if (r4 != 0) goto L_0x0016
        L_0x0013:
            if (r2 != 0) goto L_0x0016
            return r9
        L_0x0016:
            if (r2 == 0) goto L_0x001f
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r35)
            if (r4 == 0) goto L_0x001f
            return r9
        L_0x001f:
            if (r0 == 0) goto L_0x0031
            java.io.File r4 = new java.io.File
            r4.<init>(r0)
            android.net.Uri r4 = android.net.Uri.fromFile(r4)
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r4)
            if (r4 == 0) goto L_0x0031
            return r9
        L_0x0031:
            android.webkit.MimeTypeMap r10 = android.webkit.MimeTypeMap.getSingleton()
            r11 = 2
            if (r2 == 0) goto L_0x005b
            if (r0 != 0) goto L_0x005b
            boolean r0 = checkFileSize(r8, r2)
            if (r0 == 0) goto L_0x0041
            return r11
        L_0x0041:
            if (r3 == 0) goto L_0x0048
            java.lang.String r0 = r10.getExtensionFromMimeType(r3)
            goto L_0x0049
        L_0x0048:
            r0 = 0
        L_0x0049:
            if (r0 != 0) goto L_0x004f
            java.lang.String r0 = "txt"
            r3 = 0
            goto L_0x0050
        L_0x004f:
            r3 = 1
        L_0x0050:
            java.lang.String r2 = org.telegram.messenger.MediaController.copyFileToCache(r2, r0)
            if (r2 != 0) goto L_0x0057
            return r9
        L_0x0057:
            r13 = r2
            if (r3 != 0) goto L_0x005d
            goto L_0x005c
        L_0x005b:
            r13 = r0
        L_0x005c:
            r0 = 0
        L_0x005d:
            java.io.File r14 = new java.io.File
            r14.<init>(r13)
            boolean r2 = r14.exists()
            if (r2 == 0) goto L_0x0567
            long r2 = r14.length()
            r6 = 0
            int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r4 != 0) goto L_0x0074
            goto L_0x0567
        L_0x0074:
            int r2 = r32.getCurrentAccount()
            long r3 = r14.length()
            boolean r2 = org.telegram.messenger.FileLoader.checkUploadFileSize(r2, r3)
            if (r2 != 0) goto L_0x0083
            return r11
        L_0x0083:
            boolean r5 = org.telegram.messenger.DialogObject.isEncryptedDialog(r37)
            java.lang.String r4 = r14.getName()
            r3 = -1
            java.lang.String r2 = ""
            if (r0 == 0) goto L_0x0093
        L_0x0090:
            r16 = r0
            goto L_0x00a3
        L_0x0093:
            r0 = 46
            int r0 = r13.lastIndexOf(r0)
            if (r0 == r3) goto L_0x00a1
            int r0 = r0 + r9
            java.lang.String r0 = r13.substring(r0)
            goto L_0x0090
        L_0x00a1:
            r16 = r2
        L_0x00a3:
            java.lang.String r12 = r16.toLowerCase()
            java.lang.String r15 = "mp3"
            boolean r0 = r12.equals(r15)
            java.lang.String r6 = "flac"
            java.lang.String r7 = "opus"
            java.lang.String r9 = "m4a"
            java.lang.String r11 = "ogg"
            if (r0 != 0) goto L_0x0193
            boolean r0 = r12.equals(r9)
            if (r0 == 0) goto L_0x00bf
            goto L_0x0193
        L_0x00bf:
            boolean r0 = r12.equals(r7)
            if (r0 != 0) goto L_0x00df
            boolean r0 = r12.equals(r11)
            if (r0 != 0) goto L_0x00df
            boolean r0 = r12.equals(r6)
            if (r0 == 0) goto L_0x00d2
            goto L_0x00df
        L_0x00d2:
            r21 = r4
            r19 = r6
            r20 = r7
            r0 = 0
            r3 = 0
            r4 = 0
        L_0x00db:
            r6 = 0
            goto L_0x01bf
        L_0x00df:
            android.media.MediaMetadataRetriever r3 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0164, all -> 0x0160 }
            r3.<init>()     // Catch:{ Exception -> 0x0164, all -> 0x0160 }
            java.lang.String r0 = r14.getAbsolutePath()     // Catch:{ Exception -> 0x0158 }
            r3.setDataSource(r0)     // Catch:{ Exception -> 0x0158 }
            r0 = 9
            java.lang.String r0 = r3.extractMetadata(r0)     // Catch:{ Exception -> 0x0158 }
            if (r0 == 0) goto L_0x011e
            r19 = r6
            r20 = r7
            long r6 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x011a }
            float r0 = (float) r6     // Catch:{ Exception -> 0x011a }
            r6 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r6
            double r6 = (double) r0     // Catch:{ Exception -> 0x011a }
            double r6 = java.lang.Math.ceil(r6)     // Catch:{ Exception -> 0x011a }
            int r6 = (int) r6
            r0 = 7
            java.lang.String r7 = r3.extractMetadata(r0)     // Catch:{ Exception -> 0x0116 }
            r21 = r4
            r4 = 2
            java.lang.String r0 = r3.extractMetadata(r4)     // Catch:{ Exception -> 0x0113 }
            r4 = r0
            goto L_0x0127
        L_0x0113:
            r0 = move-exception
            goto L_0x016e
        L_0x0116:
            r0 = move-exception
            r21 = r4
            goto L_0x016d
        L_0x011a:
            r0 = move-exception
            r21 = r4
            goto L_0x016c
        L_0x011e:
            r21 = r4
            r19 = r6
            r20 = r7
            r4 = 0
            r6 = 0
            r7 = 0
        L_0x0127:
            if (r43 != 0) goto L_0x0142
            boolean r0 = r12.equals(r11)     // Catch:{ Exception -> 0x013e }
            if (r0 == 0) goto L_0x0142
            java.lang.String r0 = r14.getAbsolutePath()     // Catch:{ Exception -> 0x013e }
            int r0 = org.telegram.messenger.MediaController.isOpusFile(r0)     // Catch:{ Exception -> 0x013e }
            r22 = r4
            r4 = 1
            if (r0 != r4) goto L_0x0144
            r4 = 1
            goto L_0x0145
        L_0x013e:
            r0 = move-exception
            r22 = r4
            goto L_0x0170
        L_0x0142:
            r22 = r4
        L_0x0144:
            r4 = 0
        L_0x0145:
            r3.release()     // Catch:{ Exception -> 0x0149 }
            goto L_0x014e
        L_0x0149:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x014e:
            r0 = r7
            r3 = r22
            r22 = r4
            r4 = r6
            r6 = 0
            goto L_0x01c1
        L_0x0158:
            r0 = move-exception
            r21 = r4
            r19 = r6
            r20 = r7
            goto L_0x016c
        L_0x0160:
            r0 = move-exception
            r1 = r0
            r12 = 0
            goto L_0x0187
        L_0x0164:
            r0 = move-exception
            r21 = r4
            r19 = r6
            r20 = r7
            r3 = 0
        L_0x016c:
            r6 = 0
        L_0x016d:
            r7 = 0
        L_0x016e:
            r22 = 0
        L_0x0170:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0184 }
            if (r3 == 0) goto L_0x017e
            r3.release()     // Catch:{ Exception -> 0x0179 }
            goto L_0x017e
        L_0x0179:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x017e:
            r4 = r6
            r0 = r7
            r3 = r22
            goto L_0x00db
        L_0x0184:
            r0 = move-exception
            r1 = r0
            r12 = r3
        L_0x0187:
            if (r12 == 0) goto L_0x0192
            r12.release()     // Catch:{ Exception -> 0x018d }
            goto L_0x0192
        L_0x018d:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0192:
            throw r1
        L_0x0193:
            r21 = r4
            r19 = r6
            r20 = r7
            org.telegram.messenger.audioinfo.AudioInfo r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r14)
            if (r0 == 0) goto L_0x01b7
            long r3 = r0.getDuration()
            r6 = 0
            int r22 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r22 == 0) goto L_0x01b9
            java.lang.String r22 = r0.getArtist()
            java.lang.String r0 = r0.getTitle()
            r23 = 1000(0x3e8, double:4.94E-321)
            long r3 = r3 / r23
            int r4 = (int) r3
            goto L_0x01bd
        L_0x01b7:
            r6 = 0
        L_0x01b9:
            r0 = 0
            r4 = 0
            r22 = 0
        L_0x01bd:
            r3 = r22
        L_0x01bf:
            r22 = 0
        L_0x01c1:
            if (r4 == 0) goto L_0x01e5
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r6.<init>()
            r6.duration = r4
            r6.title = r0
            r6.performer = r3
            if (r0 != 0) goto L_0x01d2
            r6.title = r2
        L_0x01d2:
            int r0 = r6.flags
            r4 = 1
            r0 = r0 | r4
            r6.flags = r0
            if (r3 != 0) goto L_0x01dc
            r6.performer = r2
        L_0x01dc:
            r3 = 2
            r0 = r0 | r3
            r6.flags = r0
            if (r22 == 0) goto L_0x01e6
            r6.voice = r4
            goto L_0x01e6
        L_0x01e5:
            r6 = 0
        L_0x01e6:
            if (r1 == 0) goto L_0x0226
            java.lang.String r0 = "attheme"
            boolean r0 = r1.endsWith(r0)
            if (r0 == 0) goto L_0x01f3
            r7 = r1
            r0 = 1
            goto L_0x0228
        L_0x01f3:
            if (r6 == 0) goto L_0x020e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            java.lang.String r1 = "audio"
            r0.append(r1)
            long r3 = r14.length()
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            goto L_0x0224
        L_0x020e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r0.append(r2)
            long r3 = r14.length()
            r0.append(r3)
            java.lang.String r0 = r0.toString()
        L_0x0224:
            r7 = r0
            goto L_0x0227
        L_0x0226:
            r7 = r1
        L_0x0227:
            r0 = 0
        L_0x0228:
            r22 = 4
            if (r0 != 0) goto L_0x02ca
            if (r5 != 0) goto L_0x02ca
            org.telegram.messenger.MessagesStorage r0 = r32.getMessagesStorage()
            if (r5 != 0) goto L_0x0236
            r1 = 1
            goto L_0x0237
        L_0x0236:
            r1 = 4
        L_0x0237:
            java.lang.Object[] r0 = r0.getSentFile(r7, r1)
            if (r0 == 0) goto L_0x024f
            r1 = 0
            r3 = r0[r1]
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r3 == 0) goto L_0x024f
            r3 = r0[r1]
            r1 = r3
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC$TL_document) r1
            r3 = 1
            r0 = r0[r3]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x0251
        L_0x024f:
            r0 = 0
            r1 = 0
        L_0x0251:
            if (r1 != 0) goto L_0x0294
            boolean r3 = r13.equals(r7)
            if (r3 != 0) goto L_0x0294
            if (r5 != 0) goto L_0x0294
            org.telegram.messenger.MessagesStorage r3 = r32.getMessagesStorage()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r13)
            r23 = r0
            r34 = r1
            long r0 = r14.length()
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            if (r5 != 0) goto L_0x027a
            r1 = 1
            goto L_0x027b
        L_0x027a:
            r1 = 4
        L_0x027b:
            java.lang.Object[] r0 = r3.getSentFile(r0, r1)
            if (r0 == 0) goto L_0x0298
            r1 = 0
            r3 = r0[r1]
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r3 == 0) goto L_0x0298
            r3 = r0[r1]
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3
            r1 = 1
            r0 = r0[r1]
            java.lang.String r0 = (java.lang.String) r0
            r23 = r3
            goto L_0x029c
        L_0x0294:
            r23 = r0
            r34 = r1
        L_0x0298:
            r0 = r23
            r23 = r34
        L_0x029c:
            r24 = 0
            r25 = 0
            r1 = r32
            r4 = r2
            r2 = r5
            r27 = -1
            r3 = r23
            r29 = r4
            r28 = r21
            r4 = r13
            r8 = r5
            r5 = r24
            r30 = r7
            r33 = r9
            r21 = r13
            r24 = r19
            r13 = r6
            r19 = r8
            r8 = 0
            r31 = r20
            r20 = r10
            r10 = r31
            r6 = r25
            ensureMediaThumbExists(r1, r2, r3, r4, r5, r6)
            r7 = r0
            goto L_0x02e6
        L_0x02ca:
            r29 = r2
            r30 = r7
            r33 = r9
            r24 = r19
            r28 = r21
            r8 = 0
            r27 = -1
            r19 = r5
            r21 = r13
            r13 = r6
            r31 = r20
            r20 = r10
            r10 = r31
            r7 = 0
            r23 = 0
        L_0x02e6:
            java.lang.String r1 = "image/webp"
            if (r23 != 0) goto L_0x046d
            org.telegram.tgnet.TLRPC$TL_document r2 = new org.telegram.tgnet.TLRPC$TL_document
            r2.<init>()
            r2.id = r8
            org.telegram.tgnet.ConnectionsManager r0 = r32.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r2.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            r3 = r28
            r0.file_name = r3
            r3 = 0
            byte[] r4 = new byte[r3]
            r2.file_reference = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r2.attributes
            r4.add(r0)
            long r4 = r14.length()
            r2.size = r4
            r2.dc_id = r3
            if (r13 == 0) goto L_0x031d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r2.attributes
            r3.add(r13)
        L_0x031d:
            int r3 = r16.length()
            java.lang.String r4 = "application/octet-stream"
            if (r3 == 0) goto L_0x0396
            int r3 = r12.hashCode()
            switch(r3) {
                case 106458: goto L_0x035f;
                case 108272: goto L_0x0356;
                case 109967: goto L_0x034d;
                case 3145576: goto L_0x0342;
                case 3418175: goto L_0x0339;
                case 3645340: goto L_0x032e;
                default: goto L_0x032c;
            }
        L_0x032c:
            r3 = -1
            goto L_0x0369
        L_0x032e:
            java.lang.String r3 = "webp"
            boolean r3 = r12.equals(r3)
            if (r3 != 0) goto L_0x0337
            goto L_0x032c
        L_0x0337:
            r3 = 5
            goto L_0x0369
        L_0x0339:
            boolean r3 = r12.equals(r10)
            if (r3 != 0) goto L_0x0340
            goto L_0x032c
        L_0x0340:
            r3 = 4
            goto L_0x0369
        L_0x0342:
            r3 = r24
            boolean r3 = r12.equals(r3)
            if (r3 != 0) goto L_0x034b
            goto L_0x032c
        L_0x034b:
            r3 = 3
            goto L_0x0369
        L_0x034d:
            boolean r3 = r12.equals(r11)
            if (r3 != 0) goto L_0x0354
            goto L_0x032c
        L_0x0354:
            r3 = 2
            goto L_0x0369
        L_0x0356:
            boolean r3 = r12.equals(r15)
            if (r3 != 0) goto L_0x035d
            goto L_0x032c
        L_0x035d:
            r3 = 1
            goto L_0x0369
        L_0x035f:
            r3 = r33
            boolean r3 = r12.equals(r3)
            if (r3 != 0) goto L_0x0368
            goto L_0x032c
        L_0x0368:
            r3 = 0
        L_0x0369:
            switch(r3) {
                case 0: goto L_0x0391;
                case 1: goto L_0x038c;
                case 2: goto L_0x0387;
                case 3: goto L_0x0382;
                case 4: goto L_0x037d;
                case 5: goto L_0x037a;
                default: goto L_0x036c;
            }
        L_0x036c:
            r3 = r20
            java.lang.String r3 = r3.getMimeTypeFromExtension(r12)
            if (r3 == 0) goto L_0x0377
            r2.mime_type = r3
            goto L_0x0398
        L_0x0377:
            r2.mime_type = r4
            goto L_0x0398
        L_0x037a:
            r2.mime_type = r1
            goto L_0x0398
        L_0x037d:
            java.lang.String r3 = "audio/opus"
            r2.mime_type = r3
            goto L_0x0398
        L_0x0382:
            java.lang.String r3 = "audio/flac"
            r2.mime_type = r3
            goto L_0x0398
        L_0x0387:
            java.lang.String r3 = "audio/ogg"
            r2.mime_type = r3
            goto L_0x0398
        L_0x038c:
            java.lang.String r3 = "audio/mpeg"
            r2.mime_type = r3
            goto L_0x0398
        L_0x0391:
            java.lang.String r3 = "audio/m4a"
            r2.mime_type = r3
            goto L_0x0398
        L_0x0396:
            r2.mime_type = r4
        L_0x0398:
            if (r46 != 0) goto L_0x03ec
            java.lang.String r3 = r2.mime_type
            java.lang.String r4 = "image/gif"
            boolean r3 = r3.equals(r4)
            if (r3 == 0) goto L_0x03ec
            if (r43 == 0) goto L_0x03ae
            long r3 = r43.getGroupIdForUse()
            int r5 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x03ec
        L_0x03ae:
            java.lang.String r3 = r14.getAbsolutePath()     // Catch:{ Exception -> 0x03e5 }
            r4 = 1119092736(0x42b40000, float:90.0)
            r5 = 0
            r6 = 1
            android.graphics.Bitmap r3 = org.telegram.messenger.ImageLoader.loadBitmap(r3, r5, r4, r4, r6)     // Catch:{ Exception -> 0x03e5 }
            if (r3 == 0) goto L_0x03ec
            java.lang.String r5 = "animation.gif"
            r0.file_name = r5     // Catch:{ Exception -> 0x03e5 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r2.attributes     // Catch:{ Exception -> 0x03e5 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x03e5 }
            r5.<init>()     // Catch:{ Exception -> 0x03e5 }
            r0.add(r5)     // Catch:{ Exception -> 0x03e5 }
            r0 = 55
            r5 = r19
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r3, r4, r4, r0, r5)     // Catch:{ Exception -> 0x03e3 }
            if (r0 == 0) goto L_0x03df
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r2.thumbs     // Catch:{ Exception -> 0x03e3 }
            r4.add(r0)     // Catch:{ Exception -> 0x03e3 }
            int r0 = r2.flags     // Catch:{ Exception -> 0x03e3 }
            r4 = 1
            r0 = r0 | r4
            r2.flags = r0     // Catch:{ Exception -> 0x03e3 }
        L_0x03df:
            r3.recycle()     // Catch:{ Exception -> 0x03e3 }
            goto L_0x03ee
        L_0x03e3:
            r0 = move-exception
            goto L_0x03e8
        L_0x03e5:
            r0 = move-exception
            r5 = r19
        L_0x03e8:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x03ee
        L_0x03ec:
            r5 = r19
        L_0x03ee:
            java.lang.String r0 = r2.mime_type
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0467
            if (r43 != 0) goto L_0x0467
            android.graphics.BitmapFactory$Options r3 = new android.graphics.BitmapFactory$Options
            r3.<init>()
            r4 = 1
            r3.inJustDecodeBounds = r4     // Catch:{ Exception -> 0x042b }
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x042b }
            java.lang.String r4 = "r"
            r6 = r21
            r0.<init>(r6, r4)     // Catch:{ Exception -> 0x0429 }
            java.nio.channels.FileChannel r19 = r0.getChannel()     // Catch:{ Exception -> 0x0429 }
            java.nio.channels.FileChannel$MapMode r20 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Exception -> 0x0429 }
            r21 = 0
            int r4 = r6.length()     // Catch:{ Exception -> 0x0429 }
            long r8 = (long) r4     // Catch:{ Exception -> 0x0429 }
            r23 = r8
            java.nio.MappedByteBuffer r4 = r19.map(r20, r21, r23)     // Catch:{ Exception -> 0x0429 }
            int r8 = r4.limit()     // Catch:{ Exception -> 0x0429 }
            r9 = 0
            r10 = 1
            org.telegram.messenger.Utilities.loadWebpImage(r9, r4, r8, r3, r10)     // Catch:{ Exception -> 0x0429 }
            r0.close()     // Catch:{ Exception -> 0x0429 }
            goto L_0x0431
        L_0x0429:
            r0 = move-exception
            goto L_0x042e
        L_0x042b:
            r0 = move-exception
            r6 = r21
        L_0x042e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0431:
            int r0 = r3.outWidth
            if (r0 == 0) goto L_0x0469
            int r4 = r3.outHeight
            if (r4 == 0) goto L_0x0469
            r8 = 800(0x320, float:1.121E-42)
            if (r0 > r8) goto L_0x0469
            if (r4 > r8) goto L_0x0469
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            r4 = r29
            r0.alt = r4
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r8 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r8.<init>()
            r0.stickerset = r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r2.attributes
            r8.add(r0)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r0.<init>()
            int r8 = r3.outWidth
            r0.w = r8
            int r3 = r3.outHeight
            r0.h = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r2.attributes
            r3.add(r0)
            goto L_0x046b
        L_0x0467:
            r6 = r21
        L_0x0469:
            r4 = r29
        L_0x046b:
            r0 = r2
            goto L_0x0475
        L_0x046d:
            r5 = r19
            r6 = r21
            r4 = r29
            r0 = r23
        L_0x0475:
            if (r41 == 0) goto L_0x047d
            java.lang.String r2 = r41.toString()
            r12 = r2
            goto L_0x047e
        L_0x047d:
            r12 = r4
        L_0x047e:
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            r2 = r30
            if (r2 == 0) goto L_0x048c
            java.lang.String r3 = "originalPath"
            r8.put(r3, r2)
        L_0x048c:
            java.lang.String r2 = "1"
            if (r46 == 0) goto L_0x0497
            if (r13 != 0) goto L_0x0497
            java.lang.String r3 = "forceDocument"
            r8.put(r3, r2)
        L_0x0497:
            if (r7 == 0) goto L_0x049e
            java.lang.String r3 = "parentObject"
            r8.put(r3, r7)
        L_0x049e:
            r3 = 0
            java.lang.Integer r9 = java.lang.Integer.valueOf(r3)
            if (r49 == 0) goto L_0x04fe
            r9 = r49[r3]
            java.lang.String r10 = r0.mime_type
            if (r10 == 0) goto L_0x04bf
            java.lang.String r10 = r10.toLowerCase()
            boolean r1 = r10.startsWith(r1)
            if (r1 == 0) goto L_0x04bf
            java.lang.Integer r1 = java.lang.Integer.valueOf(r27)
            r49[r3] = r1
            r15 = 0
            r18 = 1
            goto L_0x0501
        L_0x04bf:
            java.lang.String r1 = r0.mime_type
            if (r1 == 0) goto L_0x04dd
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r3 = "image/"
            boolean r1 = r1.startsWith(r3)
            if (r1 != 0) goto L_0x04e3
            java.lang.String r1 = r0.mime_type
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r3 = "video/mp4"
            boolean r1 = r1.startsWith(r3)
            if (r1 != 0) goto L_0x04e3
        L_0x04dd:
            boolean r1 = org.telegram.messenger.MessageObject.canPreviewDocument(r0)
            if (r1 == 0) goto L_0x04ec
        L_0x04e3:
            r1 = 1
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r15 = 0
            r49[r15] = r1
            goto L_0x04ff
        L_0x04ec:
            r15 = 0
            if (r13 == 0) goto L_0x04f7
            r1 = 2
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r49[r15] = r1
            goto L_0x04ff
        L_0x04f7:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r15)
            r49[r15] = r1
            goto L_0x04ff
        L_0x04fe:
            r15 = 0
        L_0x04ff:
            r18 = 0
        L_0x0501:
            if (r5 != 0) goto L_0x0543
            if (r44 == 0) goto L_0x0543
            if (r49 == 0) goto L_0x051f
            if (r9 == 0) goto L_0x051f
            r1 = r49[r15]
            if (r9 == r1) goto L_0x051f
            r9 = r44[r15]
            r3 = r32
            r14 = r48
            finishGroup(r3, r9, r14)
            java.security.SecureRandom r1 = org.telegram.messenger.Utilities.random
            long r9 = r1.nextLong()
            r44[r15] = r9
            goto L_0x0523
        L_0x051f:
            r3 = r32
            r14 = r48
        L_0x0523:
            if (r18 != 0) goto L_0x0547
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            r4 = r44[r15]
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            java.lang.String r4 = "groupId"
            r8.put(r4, r1)
            if (r45 == 0) goto L_0x0547
            java.lang.String r1 = "final"
            r8.put(r1, r2)
            goto L_0x0547
        L_0x0543:
            r3 = r32
            r14 = r48
        L_0x0547:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda13 r16 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda13
            r1 = r16
            r2 = r43
            r3 = r32
            r4 = r0
            r5 = r6
            r6 = r8
            r8 = r37
            r10 = r39
            r11 = r40
            r13 = r42
            r14 = r47
            r17 = 0
            r15 = r48
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r10, r11, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r16)
            return r17
        L_0x0567:
            r1 = 1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, long[], boolean, boolean, boolean, int, java.lang.Integer[]):int");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingDocumentInternal$73(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, String str3, ArrayList arrayList, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, str, hashMap, false, str2);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject2, messageObject3, str3, arrayList, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str2, (MessageObject.SendAnimationData) null);
        }
    }

    private static boolean checkFileSize(AccountInstance accountInstance, Uri uri) {
        long j = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                AssetFileDescriptor openAssetFileDescriptor = ApplicationLoader.applicationContext.getContentResolver().openAssetFileDescriptor(uri, "r", (CancellationSignal) null);
                if (openAssetFileDescriptor != null) {
                    long length = openAssetFileDescriptor.getLength();
                }
                Cursor query = ApplicationLoader.applicationContext.getContentResolver().query(uri, new String[]{"_size"}, (String) null, (String[]) null, (String) null);
                int columnIndex = query.getColumnIndex("_size");
                query.moveToFirst();
                j = query.getLong(columnIndex);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return !FileLoader.checkUploadFileSize(accountInstance.getCurrentAccount(), j);
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
        new Thread(new SendMessagesHelper$$ExternalSyntheticLambda10(arrayList, j, accountInstance, str, messageObject3, messageObject, messageObject2, z, i)).start();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v3, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v3, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00b7  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00e2 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingAudioDocuments$75(java.util.ArrayList r22, long r23, org.telegram.messenger.AccountInstance r25, java.lang.String r26, org.telegram.messenger.MessageObject r27, org.telegram.messenger.MessageObject r28, org.telegram.messenger.MessageObject r29, boolean r30, int r31) {
        /*
            int r0 = r22.size()
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
        L_0x0009:
            if (r4 >= r0) goto L_0x00fd
            r6 = r22
            java.lang.Object r7 = r6.get(r4)
            r12 = r7
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            org.telegram.tgnet.TLRPC$Message r7 = r12.messageOwner
            java.lang.String r7 = r7.attachPath
            java.io.File r8 = new java.io.File
            r8.<init>(r7)
            boolean r9 = org.telegram.messenger.DialogObject.isEncryptedDialog(r23)
            r10 = 1
            if (r9 != 0) goto L_0x0031
            if (r0 <= r10) goto L_0x0031
            int r11 = r5 % 10
            if (r11 != 0) goto L_0x0031
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random
            long r2 = r2.nextLong()
            r5 = 0
        L_0x0031:
            if (r7 == 0) goto L_0x004b
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r7)
            java.lang.String r7 = "audio"
            r11.append(r7)
            long r7 = r8.length()
            r11.append(r7)
            java.lang.String r7 = r11.toString()
        L_0x004b:
            r8 = 0
            if (r9 != 0) goto L_0x007d
            org.telegram.messenger.MessagesStorage r11 = r25.getMessagesStorage()
            if (r9 != 0) goto L_0x0056
            r13 = 1
            goto L_0x0057
        L_0x0056:
            r13 = 4
        L_0x0057:
            java.lang.Object[] r11 = r11.getSentFile(r7, r13)
            if (r11 == 0) goto L_0x007d
            r13 = r11[r1]
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r13 == 0) goto L_0x007d
            r13 = r11[r1]
            r20 = r13
            org.telegram.tgnet.TLRPC$TL_document r20 = (org.telegram.tgnet.TLRPC$TL_document) r20
            r11 = r11[r10]
            java.lang.String r11 = (java.lang.String) r11
            r17 = 0
            r18 = 0
            r13 = r25
            r14 = r9
            r15 = r20
            r16 = r7
            ensureMediaThumbExists(r13, r14, r15, r16, r17, r18)
            r14 = r11
            goto L_0x0080
        L_0x007d:
            r14 = r8
            r20 = r14
        L_0x0080:
            if (r20 != 0) goto L_0x008b
            org.telegram.tgnet.TLRPC$Message r11 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            org.telegram.tgnet.TLRPC$Document r11 = r11.document
            org.telegram.tgnet.TLRPC$TL_document r11 = (org.telegram.tgnet.TLRPC$TL_document) r11
            goto L_0x008d
        L_0x008b:
            r11 = r20
        L_0x008d:
            if (r9 == 0) goto L_0x00a2
            int r9 = org.telegram.messenger.DialogObject.getEncryptedChatId(r23)
            org.telegram.messenger.MessagesController r13 = r25.getMessagesController()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r13.getEncryptedChat(r9)
            if (r9 != 0) goto L_0x00a2
            return
        L_0x00a2:
            if (r4 != 0) goto L_0x00a7
            r19 = r26
            goto L_0x00a9
        L_0x00a7:
            r19 = r8
        L_0x00a9:
            java.util.HashMap r13 = new java.util.HashMap
            r13.<init>()
            if (r7 == 0) goto L_0x00b5
            java.lang.String r8 = "originalPath"
            r13.put(r8, r7)
        L_0x00b5:
            if (r14 == 0) goto L_0x00bc
            java.lang.String r7 = "parentObject"
            r13.put(r7, r14)
        L_0x00bc:
            int r5 = r5 + r10
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = ""
            r7.append(r8)
            r7.append(r2)
            java.lang.String r7 = r7.toString()
            java.lang.String r8 = "groupId"
            r13.put(r8, r7)
            r7 = 10
            if (r5 == r7) goto L_0x00db
            int r7 = r0 + -1
            if (r4 != r7) goto L_0x00e2
        L_0x00db:
            java.lang.String r7 = "final"
            java.lang.String r8 = "1"
            r13.put(r7, r8)
        L_0x00e2:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda15 r7 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda15
            r8 = r7
            r9 = r27
            r10 = r25
            r15 = r23
            r17 = r28
            r18 = r29
            r20 = r30
            r21 = r31
            r8.<init>(r9, r10, r11, r12, r13, r14, r15, r17, r18, r19, r20, r21)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)
            int r4 = r4 + 1
            goto L_0x0009
        L_0x00fd:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingAudioDocuments$75(java.util.ArrayList, long, org.telegram.messenger.AccountInstance, java.lang.String, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingAudioDocuments$74(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, MessageObject messageObject2, HashMap hashMap, String str, long j, MessageObject messageObject3, MessageObject messageObject4, String str2, boolean z, int i) {
        MessageObject messageObject5 = messageObject2;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, messageObject5.messageOwner.attachPath, hashMap, false, str);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, messageObject5.messageOwner.attachPath, j, messageObject3, messageObject4, str2, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str, (MessageObject.SendAnimationData) null);
    }

    private static void finishGroup(AccountInstance accountInstance, long j, int i) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda12(accountInstance, j, i));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$finishGroup$76(AccountInstance accountInstance, long j, int i) {
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
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                SendMessagesHelper$$ExternalSyntheticLambda3 sendMessagesHelper$$ExternalSyntheticLambda3 = r0;
                SendMessagesHelper$$ExternalSyntheticLambda3 sendMessagesHelper$$ExternalSyntheticLambda32 = new SendMessagesHelper$$ExternalSyntheticLambda3(j, arrayList, str, accountInstance, i, arrayList2, str2, messageObject, messageObject2, messageObject3, inputContentInfoCompat, z, arrayList3);
                dispatchQueue.postRunnable(sendMessagesHelper$$ExternalSyntheticLambda3);
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingDocuments$77(long j, ArrayList arrayList, String str, AccountInstance accountInstance, int i, ArrayList arrayList2, String str2, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, InputContentInfoCompat inputContentInfoCompat, boolean z, ArrayList arrayList3) {
        long[] jArr;
        Integer[] numArr;
        ArrayList arrayList4;
        int i2;
        ArrayList arrayList5 = arrayList;
        AccountInstance accountInstance2 = accountInstance;
        int i3 = i;
        ArrayList arrayList6 = arrayList3;
        int i4 = 1;
        long[] jArr2 = new long[1];
        Integer[] numArr2 = new Integer[1];
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(j);
        int i5 = 10;
        if (arrayList5 != null) {
            int size = arrayList.size();
            i2 = 0;
            int i6 = 0;
            int i7 = 0;
            while (i7 < size) {
                String str3 = i7 == 0 ? str : null;
                if (!isEncryptedDialog && size > i4 && i6 % 10 == 0) {
                    if (jArr2[0] != 0) {
                        finishGroup(accountInstance2, jArr2[0], i3);
                    }
                    jArr2[0] = Utilities.random.nextLong();
                    i6 = 0;
                }
                int i8 = i6 + 1;
                long j2 = jArr2[0];
                int i9 = i7;
                int i10 = i8;
                int i11 = size;
                Integer[] numArr3 = numArr2;
                long[] jArr3 = jArr2;
                i2 = prepareSendingDocumentInternal(accountInstance, (String) arrayList5.get(i7), (String) arrayList2.get(i7), (Uri) null, str2, j, messageObject, messageObject2, str3, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, jArr3, i8 == i5 || i7 == size + -1, inputContentInfoCompat == null, z, i, numArr3);
                i6 = (j2 != jArr3[0] || jArr3[0] == -1) ? 1 : i10;
                i7 = i9 + 1;
                accountInstance2 = accountInstance;
                i3 = i;
                ArrayList arrayList7 = arrayList3;
                size = i11;
                numArr2 = numArr3;
                jArr2 = jArr3;
                i5 = 10;
                i4 = 1;
            }
            numArr = numArr2;
            jArr = jArr2;
            arrayList4 = arrayList3;
        } else {
            numArr = numArr2;
            jArr = jArr2;
            arrayList4 = arrayList3;
            i2 = 0;
        }
        if (arrayList4 != null) {
            jArr[0] = 0;
            int size2 = arrayList3.size();
            int i12 = 0;
            int i13 = 0;
            while (i13 < arrayList3.size()) {
                String str4 = (i13 == 0 && (arrayList5 == null || arrayList.size() == 0)) ? str : null;
                if (isEncryptedDialog) {
                    AccountInstance accountInstance3 = accountInstance;
                    int i14 = i;
                } else if (size2 <= 1 || i12 % 10 != 0) {
                    AccountInstance accountInstance4 = accountInstance;
                    int i15 = i;
                } else {
                    if (jArr[0] != 0) {
                        finishGroup(accountInstance, jArr[0], i);
                    } else {
                        AccountInstance accountInstance5 = accountInstance;
                        int i16 = i;
                    }
                    jArr[0] = Utilities.random.nextLong();
                    i12 = 0;
                }
                int i17 = i12 + 1;
                long j3 = jArr[0];
                int i18 = i17;
                int i19 = i13;
                int i20 = size2;
                i2 = prepareSendingDocumentInternal(accountInstance, (String) null, (String) null, (Uri) arrayList4.get(i13), str2, j, messageObject, messageObject2, str4, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, jArr, i17 == 10 || i13 == size2 + -1, inputContentInfoCompat == null, z, i, numArr);
                i12 = (j3 != jArr[0] || jArr[0] == -1) ? 1 : i18;
                i13 = i19 + 1;
                arrayList4 = arrayList3;
                size2 = i20;
            }
        }
        if (inputContentInfoCompat != null) {
            inputContentInfoCompat.releasePermission();
        }
        handleError(i2, accountInstance);
    }

    private static void handleError(int i, AccountInstance accountInstance) {
        if (i != 0) {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda2(i, accountInstance));
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$handleError$78(int i, AccountInstance accountInstance) {
        if (i == 1) {
            try {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.getString("UnsupportedAttachment", NUM));
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (i == 2) {
            NotificationCenter.getInstance(accountInstance.getCurrentAccount()).postNotificationName(NotificationCenter.currentUserShowLimitReachedDialog, 6);
        }
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, Uri uri, long j, MessageObject messageObject, MessageObject messageObject2, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, ArrayList<TLRPC$InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject3, boolean z, int i2) {
        prepareSendingPhoto(accountInstance, str, (String) null, uri, j, messageObject, messageObject2, charSequence, arrayList, arrayList2, inputContentInfoCompat, i, messageObject3, (VideoEditedInfo) null, z, i2, false);
    }

    public static void prepareSendingPhoto(AccountInstance accountInstance, String str, String str2, Uri uri, long j, MessageObject messageObject, MessageObject messageObject2, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, ArrayList<TLRPC$InputDocument> arrayList2, InputContentInfoCompat inputContentInfoCompat, int i, MessageObject messageObject3, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
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
        prepareSendingMedia(accountInstance, arrayList4, j, messageObject, messageObject2, inputContentInfoCompat, z2, false, messageObject3, z, i2);
    }

    public static void prepareSendingBotContextResult(AccountInstance accountInstance, TLRPC$BotInlineResult tLRPC$BotInlineResult, HashMap<String, String> hashMap, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i) {
        TLRPC$BotInlineResult tLRPC$BotInlineResult2 = tLRPC$BotInlineResult;
        if (tLRPC$BotInlineResult2 != null) {
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult2.send_message;
            if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaAuto) {
                new Thread(new SendMessagesHelper$$ExternalSyntheticLambda4(j, tLRPC$BotInlineResult, accountInstance, hashMap, messageObject, messageObject2, z, i)).run();
            } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageText) {
                TLRPC$TL_webPagePending tLRPC$TL_webPagePending = null;
                if (DialogObject.isEncryptedDialog(j)) {
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
                sendMessagesHelper.sendMessage(tLRPC$BotInlineMessage2.message, j, messageObject, messageObject2, tLRPC$TL_webPagePending2, !tLRPC$BotInlineMessage2.no_webpage, tLRPC$BotInlineMessage2.entities, tLRPC$BotInlineMessage2.reply_markup, hashMap, z, i, (MessageObject.SendAnimationData) null);
            } else if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaVenue) {
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
            } else if ((tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaInvoice) && !DialogObject.isEncryptedDialog(j)) {
                TLRPC$TL_botInlineMessageMediaInvoice tLRPC$TL_botInlineMessageMediaInvoice = (TLRPC$TL_botInlineMessageMediaInvoice) tLRPC$BotInlineResult2.send_message;
                TLRPC$TL_messageMediaInvoice tLRPC$TL_messageMediaInvoice = new TLRPC$TL_messageMediaInvoice();
                tLRPC$TL_messageMediaInvoice.shipping_address_requested = tLRPC$TL_botInlineMessageMediaInvoice.shipping_address_requested;
                tLRPC$TL_messageMediaInvoice.test = tLRPC$TL_botInlineMessageMediaInvoice.test;
                tLRPC$TL_messageMediaInvoice.title = tLRPC$TL_botInlineMessageMediaInvoice.title;
                tLRPC$TL_messageMediaInvoice.description = tLRPC$TL_botInlineMessageMediaInvoice.description;
                TLRPC$WebDocument tLRPC$WebDocument = tLRPC$TL_botInlineMessageMediaInvoice.photo;
                if (tLRPC$WebDocument != null) {
                    tLRPC$TL_messageMediaInvoice.photo = tLRPC$WebDocument;
                    tLRPC$TL_messageMediaInvoice.flags |= 1;
                }
                tLRPC$TL_messageMediaInvoice.currency = tLRPC$TL_botInlineMessageMediaInvoice.currency;
                tLRPC$TL_messageMediaInvoice.total_amount = tLRPC$TL_botInlineMessageMediaInvoice.total_amount;
                tLRPC$TL_messageMediaInvoice.start_param = "";
                accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_messageMediaInvoice, j, messageObject, messageObject2, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0284, code lost:
        if (r0 == null) goto L_0x0291;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x0286, code lost:
        r4.thumbs.add(r0);
        r4.flags |= 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x0291, code lost:
        r5.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x0296, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x0297, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x029c, code lost:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio();
        r0.duration = org.telegram.messenger.MessageObject.getInlineResultDuration(r22);
        r0.title = r11.title;
        r1 = r0.flags | 1;
        r0.flags = r1;
        r5 = r11.description;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x02b3, code lost:
        if (r5 == null) goto L_0x02bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x02b5, code lost:
        r0.performer = r5;
        r0.flags = r1 | 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x02bb, code lost:
        r8.file_name = "audio.mp3";
        r4.attributes.add(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x02c6, code lost:
        r0 = r11.content.mime_type.lastIndexOf(47);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x02d1, code lost:
        if (r0 == -1) goto L_0x02f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x02d3, code lost:
        r8.file_name = "file." + r11.content.mime_type.substring(r0 + 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x02f2, code lost:
        r8.file_name = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x02f6, code lost:
        r8.file_name = "animation.gif";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x0302, code lost:
        if (r7.endsWith("mp4") == false) goto L_0x0311;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x0304, code lost:
        r4.mime_type = "video/mp4";
        r4.attributes.add(new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x0311, code lost:
        r4.mime_type = "image/gif";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x0315, code lost:
        if (r2 == false) goto L_0x031a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x0317, code lost:
        r6 = 90;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x031a, code lost:
        r6 = 320;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0320, code lost:
        if (r7.endsWith("mp4") == false) goto L_0x0388;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0322, code lost:
        r14 = createVideoThumbnail(r7, 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x0327, code lost:
        if (r14 != null) goto L_0x038e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x0329, code lost:
        r1 = r11.thumb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x032d, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_webDocument) == false) goto L_0x038e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x0335, code lost:
        if ("video/mp4".equals(r1.mime_type) == false) goto L_0x038e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x0337, code lost:
        r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r11.thumb.url, (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0343, code lost:
        if (android.text.TextUtils.isEmpty(r1) == false) goto L_0x034e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0345, code lost:
        r1 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r11.thumb.mime_type);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x034e, code lost:
        r1 = "." + r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x035d, code lost:
        r14 = createVideoThumbnail(new java.io.File(org.telegram.messenger.FileLoader.getDirectory(4), org.telegram.messenger.Utilities.MD5(r11.thumb.url) + r1).getAbsolutePath(), 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0388, code lost:
        r1 = (float) r6;
        r14 = org.telegram.messenger.ImageLoader.loadBitmap(r7, (android.net.Uri) null, r1, r1, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x038e, code lost:
        if (r14 == null) goto L_0x043c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x0390, code lost:
        r1 = (float) r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x0393, code lost:
        if (r6 <= 90) goto L_0x0397;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0395, code lost:
        r0 = 80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x0397, code lost:
        r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r14, r1, r1, r0, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x039c, code lost:
        if (r0 == null) goto L_0x03a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x039e, code lost:
        r4.thumbs.add(r0);
        r4.flags |= 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x03a9, code lost:
        r14.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x03ae, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x03af, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x03b4, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker();
        r6.alt = "";
        r6.stickerset = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty();
        r4.attributes.add(r6);
        r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize();
        r13 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22);
        r6.w = r13[0];
        r6.h = r13[1];
        r4.attributes.add(r6);
        r8.file_name = "sticker.webp";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x03e7, code lost:
        if (r11.thumb == null) goto L_0x043c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x03e9, code lost:
        r5 = org.telegram.messenger.ImageLoader.loadBitmap(new java.io.File(org.telegram.messenger.FileLoader.getDirectory(4), org.telegram.messenger.Utilities.MD5(r11.thumb.url) + "." + org.telegram.messenger.ImageLoader.getHttpUrlExtension(r11.thumb.url, "webp")).getAbsolutePath(), (android.net.Uri) null, 90.0f, 90.0f, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0420, code lost:
        if (r5 == null) goto L_0x043c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0422, code lost:
        r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r5, 90.0f, 90.0f, 55, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x0427, code lost:
        if (r0 == null) goto L_0x0434;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x0429, code lost:
        r4.thumbs.add(r0);
        r4.flags |= 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x0434, code lost:
        r5.recycle();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0438, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0439, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x01b7, code lost:
        r9 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x01b9, code lost:
        r14 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x01fc, code lost:
        r0 = 55;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0200, code lost:
        switch(r14) {
            case 0: goto L_0x03b4;
            case 1: goto L_0x02f6;
            case 2: goto L_0x02c6;
            case 3: goto L_0x029c;
            case 4: goto L_0x021e;
            case 5: goto L_0x0205;
            default: goto L_0x0203;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0205, code lost:
        r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio();
        r0.duration = org.telegram.messenger.MessageObject.getInlineResultDuration(r22);
        r0.voice = true;
        r8.file_name = "audio.ogg";
        r4.attributes.add(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x021e, code lost:
        r8.file_name = "video.mp4";
        r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo();
        r13 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22);
        r6.w = r13[0];
        r6.h = r13[1];
        r6.duration = org.telegram.messenger.MessageObject.getInlineResultDuration(r22);
        r6.supports_streaming = true;
        r4.attributes.add(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0244, code lost:
        if (r11.thumb == null) goto L_0x043c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0246, code lost:
        r5 = org.telegram.messenger.ImageLoader.loadBitmap(new java.io.File(org.telegram.messenger.FileLoader.getDirectory(4), org.telegram.messenger.Utilities.MD5(r11.thumb.url) + "." + org.telegram.messenger.ImageLoader.getHttpUrlExtension(r11.thumb.url, "jpg")).getAbsolutePath(), (android.net.Uri) null, 90.0f, 90.0f, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x027d, code lost:
        if (r5 == null) goto L_0x043c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x027f, code lost:
        r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r5, 90.0f, 90.0f, 55, false);
     */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x049d  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04dc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingBotContextResult$80(long r20, org.telegram.tgnet.TLRPC$BotInlineResult r22, org.telegram.messenger.AccountInstance r23, java.util.HashMap r24, org.telegram.messenger.MessageObject r25, org.telegram.messenger.MessageObject r26, boolean r27, int r28) {
        /*
            r11 = r22
            r12 = r24
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r20)
            java.lang.String r0 = r11.type
            java.lang.String r1 = "game"
            boolean r0 = r1.equals(r0)
            r3 = 0
            r8 = 0
            r9 = 1
            if (r0 == 0) goto L_0x004a
            if (r2 == 0) goto L_0x0018
            return
        L_0x0018:
            org.telegram.tgnet.TLRPC$TL_game r0 = new org.telegram.tgnet.TLRPC$TL_game
            r0.<init>()
            java.lang.String r4 = r11.title
            r0.title = r4
            java.lang.String r4 = r11.description
            r0.description = r4
            java.lang.String r4 = r11.id
            r0.short_name = r4
            org.telegram.tgnet.TLRPC$Photo r4 = r11.photo
            r0.photo = r4
            if (r4 != 0) goto L_0x0036
            org.telegram.tgnet.TLRPC$TL_photoEmpty r4 = new org.telegram.tgnet.TLRPC$TL_photoEmpty
            r4.<init>()
            r0.photo = r4
        L_0x0036:
            org.telegram.tgnet.TLRPC$Document r4 = r11.document
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r5 == 0) goto L_0x0043
            r0.document = r4
            int r4 = r0.flags
            r4 = r4 | r9
            r0.flags = r4
        L_0x0043:
            r16 = r0
            r0 = r3
            r8 = r0
            r15 = r8
            goto L_0x0485
        L_0x004a:
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMediaResult
            if (r0 == 0) goto L_0x006c
            org.telegram.tgnet.TLRPC$Document r0 = r11.document
            if (r0 == 0) goto L_0x005b
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r4 == 0) goto L_0x0480
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            r8 = r3
            goto L_0x0482
        L_0x005b:
            org.telegram.tgnet.TLRPC$Photo r0 = r11.photo
            if (r0 == 0) goto L_0x0480
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r4 == 0) goto L_0x0480
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            r15 = r0
            r0 = r3
            r8 = r0
            r16 = r8
            goto L_0x0485
        L_0x006c:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            if (r0 == 0) goto L_0x0480
            java.lang.String r0 = r0.url
            java.lang.String r0 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r0, r3)
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            java.lang.String r5 = "."
            if (r4 == 0) goto L_0x0087
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            java.lang.String r0 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r0)
            goto L_0x0096
        L_0x0087:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            r4.append(r0)
            java.lang.String r0 = r4.toString()
        L_0x0096:
            java.io.File r4 = new java.io.File
            r6 = 4
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r6)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            org.telegram.tgnet.TLRPC$WebDocument r13 = r11.content
            java.lang.String r13 = r13.url
            java.lang.String r13 = org.telegram.messenger.Utilities.MD5(r13)
            r10.append(r13)
            r10.append(r0)
            java.lang.String r0 = r10.toString()
            r4.<init>(r7, r0)
            boolean r0 = r4.exists()
            if (r0 == 0) goto L_0x00c2
            java.lang.String r0 = r4.getAbsolutePath()
            goto L_0x00c6
        L_0x00c2:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.url
        L_0x00c6:
            r7 = r0
            java.lang.String r0 = r11.type
            r0.hashCode()
            int r10 = r0.hashCode()
            java.lang.String r13 = "voice"
            java.lang.String r15 = "video"
            r16 = 3
            java.lang.String r14 = "audio"
            java.lang.String r1 = "gif"
            java.lang.String r6 = "sticker"
            r18 = 2
            java.lang.String r9 = "file"
            switch(r10) {
                case -1890252483: goto L_0x011d;
                case 102340: goto L_0x0114;
                case 3143036: goto L_0x010b;
                case 93166550: goto L_0x0102;
                case 106642994: goto L_0x00f7;
                case 112202875: goto L_0x00ee;
                case 112386354: goto L_0x00e5;
                default: goto L_0x00e3;
            }
        L_0x00e3:
            r0 = -1
            goto L_0x0125
        L_0x00e5:
            boolean r0 = r0.equals(r13)
            if (r0 != 0) goto L_0x00ec
            goto L_0x00e3
        L_0x00ec:
            r0 = 6
            goto L_0x0125
        L_0x00ee:
            boolean r0 = r0.equals(r15)
            if (r0 != 0) goto L_0x00f5
            goto L_0x00e3
        L_0x00f5:
            r0 = 5
            goto L_0x0125
        L_0x00f7:
            java.lang.String r10 = "photo"
            boolean r0 = r0.equals(r10)
            if (r0 != 0) goto L_0x0100
            goto L_0x00e3
        L_0x0100:
            r0 = 4
            goto L_0x0125
        L_0x0102:
            boolean r0 = r0.equals(r14)
            if (r0 != 0) goto L_0x0109
            goto L_0x00e3
        L_0x0109:
            r0 = 3
            goto L_0x0125
        L_0x010b:
            boolean r0 = r0.equals(r9)
            if (r0 != 0) goto L_0x0112
            goto L_0x00e3
        L_0x0112:
            r0 = 2
            goto L_0x0125
        L_0x0114:
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x011b
            goto L_0x00e3
        L_0x011b:
            r0 = 1
            goto L_0x0125
        L_0x011d:
            boolean r0 = r0.equals(r6)
            if (r0 != 0) goto L_0x0124
            goto L_0x00e3
        L_0x0124:
            r0 = 0
        L_0x0125:
            java.lang.String r10 = "x"
            switch(r0) {
                case 0: goto L_0x017d;
                case 1: goto L_0x017d;
                case 2: goto L_0x017d;
                case 3: goto L_0x017d;
                case 4: goto L_0x0131;
                case 5: goto L_0x017d;
                case 6: goto L_0x017d;
                default: goto L_0x012a;
            }
        L_0x012a:
            r0 = r3
            r15 = r0
            r16 = r15
        L_0x012e:
            r8 = r7
            goto L_0x0485
        L_0x0131:
            boolean r0 = r4.exists()
            if (r0 == 0) goto L_0x0140
            org.telegram.messenger.SendMessagesHelper r0 = r23.getSendMessagesHelper()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r7, r3)
            goto L_0x0141
        L_0x0140:
            r0 = r3
        L_0x0141:
            if (r0 != 0) goto L_0x0178
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r1 = r23.getConnectionsManager()
            int r1 = r1.getCurrentTime()
            r0.date = r1
            byte[] r1 = new byte[r8]
            r0.file_reference = r1
            org.telegram.tgnet.TLRPC$TL_photoSize r1 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r1.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r5 = r4[r8]
            r1.w = r5
            r5 = 1
            r4 = r4[r5]
            r1.h = r4
            r1.size = r5
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r4 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r4.<init>()
            r1.location = r4
            r1.type = r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r0.sizes
            r4.add(r1)
        L_0x0178:
            r15 = r0
            r0 = r3
            r16 = r0
            goto L_0x012e
        L_0x017d:
            org.telegram.tgnet.TLRPC$TL_document r4 = new org.telegram.tgnet.TLRPC$TL_document
            r4.<init>()
            r19 = r9
            r8 = 0
            r4.id = r8
            r4.size = r8
            r8 = 0
            r4.dc_id = r8
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r4.mime_type = r0
            byte[] r0 = new byte[r8]
            r4.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r23.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r4.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r8.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r4.attributes
            r0.add(r8)
            java.lang.String r0 = r11.type
            r0.hashCode()
            int r9 = r0.hashCode()
            switch(r9) {
                case -1890252483: goto L_0x01f2;
                case 102340: goto L_0x01e7;
                case 3143036: goto L_0x01dc;
                case 93166550: goto L_0x01d1;
                case 112202875: goto L_0x01c6;
                case 112386354: goto L_0x01bb;
                default: goto L_0x01b7;
            }
        L_0x01b7:
            r9 = r19
        L_0x01b9:
            r14 = -1
            goto L_0x01fc
        L_0x01bb:
            boolean r0 = r0.equals(r13)
            if (r0 != 0) goto L_0x01c2
            goto L_0x01b7
        L_0x01c2:
            r9 = r19
            r14 = 5
            goto L_0x01fc
        L_0x01c6:
            boolean r0 = r0.equals(r15)
            if (r0 != 0) goto L_0x01cd
            goto L_0x01b7
        L_0x01cd:
            r9 = r19
            r14 = 4
            goto L_0x01fc
        L_0x01d1:
            boolean r0 = r0.equals(r14)
            if (r0 != 0) goto L_0x01d8
            goto L_0x01b7
        L_0x01d8:
            r9 = r19
            r14 = 3
            goto L_0x01fc
        L_0x01dc:
            r9 = r19
            boolean r0 = r0.equals(r9)
            if (r0 != 0) goto L_0x01e5
            goto L_0x01fa
        L_0x01e5:
            r14 = 2
            goto L_0x01fc
        L_0x01e7:
            r9 = r19
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x01f0
            goto L_0x01fa
        L_0x01f0:
            r14 = 1
            goto L_0x01fc
        L_0x01f2:
            r9 = r19
            boolean r0 = r0.equals(r6)
            if (r0 != 0) goto L_0x01fb
        L_0x01fa:
            goto L_0x01b9
        L_0x01fb:
            r14 = 0
        L_0x01fc:
            r0 = 55
            r1 = 1119092736(0x42b40000, float:90.0)
            switch(r14) {
                case 0: goto L_0x03b4;
                case 1: goto L_0x02f6;
                case 2: goto L_0x02c6;
                case 3: goto L_0x029c;
                case 4: goto L_0x021e;
                case 5: goto L_0x0205;
                default: goto L_0x0203;
            }
        L_0x0203:
            goto L_0x043c
        L_0x0205:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r1
            r1 = 1
            r0.voice = r1
            java.lang.String r1 = "audio.ogg"
            r8.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r4.attributes
            r1.add(r0)
            goto L_0x043c
        L_0x021e:
            java.lang.String r6 = "video.mp4"
            r8.file_name = r6
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r6.<init>()
            int[] r13 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r14 = 0
            r15 = r13[r14]
            r6.w = r15
            r14 = 1
            r13 = r13[r14]
            r6.h = r13
            int r13 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r6.duration = r13
            r6.supports_streaming = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r4.attributes
            r13.add(r6)
            org.telegram.tgnet.TLRPC$WebDocument r6 = r11.thumb     // Catch:{ all -> 0x0296 }
            if (r6 == 0) goto L_0x043c
            java.io.File r6 = new java.io.File     // Catch:{ all -> 0x0296 }
            r13 = 4
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r13)     // Catch:{ all -> 0x0296 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0296 }
            r14.<init>()     // Catch:{ all -> 0x0296 }
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.thumb     // Catch:{ all -> 0x0296 }
            java.lang.String r15 = r15.url     // Catch:{ all -> 0x0296 }
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)     // Catch:{ all -> 0x0296 }
            r14.append(r15)     // Catch:{ all -> 0x0296 }
            r14.append(r5)     // Catch:{ all -> 0x0296 }
            org.telegram.tgnet.TLRPC$WebDocument r5 = r11.thumb     // Catch:{ all -> 0x0296 }
            java.lang.String r5 = r5.url     // Catch:{ all -> 0x0296 }
            java.lang.String r15 = "jpg"
            java.lang.String r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r15)     // Catch:{ all -> 0x0296 }
            r14.append(r5)     // Catch:{ all -> 0x0296 }
            java.lang.String r5 = r14.toString()     // Catch:{ all -> 0x0296 }
            r6.<init>(r13, r5)     // Catch:{ all -> 0x0296 }
            java.lang.String r5 = r6.getAbsolutePath()     // Catch:{ all -> 0x0296 }
            r6 = 1
            android.graphics.Bitmap r5 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r3, r1, r1, r6)     // Catch:{ all -> 0x0296 }
            if (r5 == 0) goto L_0x043c
            r6 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r5, r1, r1, r0, r6)     // Catch:{ all -> 0x0296 }
            if (r0 == 0) goto L_0x0291
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs     // Catch:{ all -> 0x0296 }
            r1.add(r0)     // Catch:{ all -> 0x0296 }
            int r0 = r4.flags     // Catch:{ all -> 0x0296 }
            r1 = 1
            r0 = r0 | r1
            r4.flags = r0     // Catch:{ all -> 0x0296 }
        L_0x0291:
            r5.recycle()     // Catch:{ all -> 0x0296 }
            goto L_0x043c
        L_0x0296:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x043c
        L_0x029c:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r22)
            r0.duration = r1
            java.lang.String r1 = r11.title
            r0.title = r1
            int r1 = r0.flags
            r5 = 1
            r1 = r1 | r5
            r0.flags = r1
            java.lang.String r5 = r11.description
            if (r5 == 0) goto L_0x02bb
            r0.performer = r5
            r1 = r1 | 2
            r0.flags = r1
        L_0x02bb:
            java.lang.String r1 = "audio.mp3"
            r8.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r4.attributes
            r1.add(r0)
            goto L_0x043c
        L_0x02c6:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r1 = 47
            int r0 = r0.lastIndexOf(r1)
            r1 = -1
            if (r0 == r1) goto L_0x02f2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "file."
            r1.append(r5)
            org.telegram.tgnet.TLRPC$WebDocument r5 = r11.content
            java.lang.String r5 = r5.mime_type
            r6 = 1
            int r0 = r0 + r6
            java.lang.String r0 = r5.substring(r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r8.file_name = r0
            goto L_0x043c
        L_0x02f2:
            r8.file_name = r9
            goto L_0x043c
        L_0x02f6:
            java.lang.String r1 = "animation.gif"
            r8.file_name = r1
            java.lang.String r1 = "mp4"
            boolean r6 = r7.endsWith(r1)
            java.lang.String r13 = "video/mp4"
            if (r6 == 0) goto L_0x0311
            r4.mime_type = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r14 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r14.<init>()
            r6.add(r14)
            goto L_0x0315
        L_0x0311:
            java.lang.String r6 = "image/gif"
            r4.mime_type = r6
        L_0x0315:
            if (r2 == 0) goto L_0x031a
            r6 = 90
            goto L_0x031c
        L_0x031a:
            r6 = 320(0x140, float:4.48E-43)
        L_0x031c:
            boolean r1 = r7.endsWith(r1)     // Catch:{ all -> 0x03ae }
            if (r1 == 0) goto L_0x0388
            r1 = 1
            android.graphics.Bitmap r14 = createVideoThumbnail(r7, r1)     // Catch:{ all -> 0x03ae }
            if (r14 != 0) goto L_0x038e
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.thumb     // Catch:{ all -> 0x03ae }
            boolean r15 = r1 instanceof org.telegram.tgnet.TLRPC$TL_webDocument     // Catch:{ all -> 0x03ae }
            if (r15 == 0) goto L_0x038e
            java.lang.String r1 = r1.mime_type     // Catch:{ all -> 0x03ae }
            boolean r1 = r13.equals(r1)     // Catch:{ all -> 0x03ae }
            if (r1 == 0) goto L_0x038e
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.thumb     // Catch:{ all -> 0x03ae }
            java.lang.String r1 = r1.url     // Catch:{ all -> 0x03ae }
            java.lang.String r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r3)     // Catch:{ all -> 0x03ae }
            boolean r13 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03ae }
            if (r13 == 0) goto L_0x034e
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.thumb     // Catch:{ all -> 0x03ae }
            java.lang.String r1 = r1.mime_type     // Catch:{ all -> 0x03ae }
            java.lang.String r1 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r1)     // Catch:{ all -> 0x03ae }
            goto L_0x035d
        L_0x034e:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ae }
            r13.<init>()     // Catch:{ all -> 0x03ae }
            r13.append(r5)     // Catch:{ all -> 0x03ae }
            r13.append(r1)     // Catch:{ all -> 0x03ae }
            java.lang.String r1 = r13.toString()     // Catch:{ all -> 0x03ae }
        L_0x035d:
            java.io.File r5 = new java.io.File     // Catch:{ all -> 0x03ae }
            r13 = 4
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r13)     // Catch:{ all -> 0x03ae }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ae }
            r14.<init>()     // Catch:{ all -> 0x03ae }
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.thumb     // Catch:{ all -> 0x03ae }
            java.lang.String r15 = r15.url     // Catch:{ all -> 0x03ae }
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)     // Catch:{ all -> 0x03ae }
            r14.append(r15)     // Catch:{ all -> 0x03ae }
            r14.append(r1)     // Catch:{ all -> 0x03ae }
            java.lang.String r1 = r14.toString()     // Catch:{ all -> 0x03ae }
            r5.<init>(r13, r1)     // Catch:{ all -> 0x03ae }
            java.lang.String r1 = r5.getAbsolutePath()     // Catch:{ all -> 0x03ae }
            r5 = 1
            android.graphics.Bitmap r14 = createVideoThumbnail(r1, r5)     // Catch:{ all -> 0x03ae }
            goto L_0x038e
        L_0x0388:
            float r1 = (float) r6     // Catch:{ all -> 0x03ae }
            r5 = 1
            android.graphics.Bitmap r14 = org.telegram.messenger.ImageLoader.loadBitmap(r7, r3, r1, r1, r5)     // Catch:{ all -> 0x03ae }
        L_0x038e:
            if (r14 == 0) goto L_0x043c
            float r1 = (float) r6     // Catch:{ all -> 0x03ae }
            r5 = 90
            if (r6 <= r5) goto L_0x0397
            r0 = 80
        L_0x0397:
            r5 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r14, r1, r1, r0, r5)     // Catch:{ all -> 0x03ae }
            if (r0 == 0) goto L_0x03a9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs     // Catch:{ all -> 0x03ae }
            r1.add(r0)     // Catch:{ all -> 0x03ae }
            int r0 = r4.flags     // Catch:{ all -> 0x03ae }
            r1 = 1
            r0 = r0 | r1
            r4.flags = r0     // Catch:{ all -> 0x03ae }
        L_0x03a9:
            r14.recycle()     // Catch:{ all -> 0x03ae }
            goto L_0x043c
        L_0x03ae:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x043c
        L_0x03b4:
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r6.<init>()
            java.lang.String r13 = ""
            r6.alt = r13
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r13 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r13.<init>()
            r6.stickerset = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r4.attributes
            r13.add(r6)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r6.<init>()
            int[] r13 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r14 = 0
            r15 = r13[r14]
            r6.w = r15
            r14 = 1
            r13 = r13[r14]
            r6.h = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r4.attributes
            r13.add(r6)
            java.lang.String r6 = "sticker.webp"
            r8.file_name = r6
            org.telegram.tgnet.TLRPC$WebDocument r6 = r11.thumb     // Catch:{ all -> 0x0438 }
            if (r6 == 0) goto L_0x043c
            java.io.File r6 = new java.io.File     // Catch:{ all -> 0x0438 }
            r13 = 4
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r13)     // Catch:{ all -> 0x0438 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x0438 }
            r14.<init>()     // Catch:{ all -> 0x0438 }
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.thumb     // Catch:{ all -> 0x0438 }
            java.lang.String r15 = r15.url     // Catch:{ all -> 0x0438 }
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)     // Catch:{ all -> 0x0438 }
            r14.append(r15)     // Catch:{ all -> 0x0438 }
            r14.append(r5)     // Catch:{ all -> 0x0438 }
            org.telegram.tgnet.TLRPC$WebDocument r5 = r11.thumb     // Catch:{ all -> 0x0438 }
            java.lang.String r5 = r5.url     // Catch:{ all -> 0x0438 }
            java.lang.String r15 = "webp"
            java.lang.String r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r15)     // Catch:{ all -> 0x0438 }
            r14.append(r5)     // Catch:{ all -> 0x0438 }
            java.lang.String r5 = r14.toString()     // Catch:{ all -> 0x0438 }
            r6.<init>(r13, r5)     // Catch:{ all -> 0x0438 }
            java.lang.String r5 = r6.getAbsolutePath()     // Catch:{ all -> 0x0438 }
            r6 = 1
            android.graphics.Bitmap r5 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r3, r1, r1, r6)     // Catch:{ all -> 0x0438 }
            if (r5 == 0) goto L_0x043c
            r6 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r5, r1, r1, r0, r6)     // Catch:{ all -> 0x0438 }
            if (r0 == 0) goto L_0x0434
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs     // Catch:{ all -> 0x0438 }
            r1.add(r0)     // Catch:{ all -> 0x0438 }
            int r0 = r4.flags     // Catch:{ all -> 0x0438 }
            r1 = 1
            r0 = r0 | r1
            r4.flags = r0     // Catch:{ all -> 0x0438 }
        L_0x0434:
            r5.recycle()     // Catch:{ all -> 0x0438 }
            goto L_0x043c
        L_0x0438:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x043c:
            java.lang.String r0 = r8.file_name
            if (r0 != 0) goto L_0x0442
            r8.file_name = r9
        L_0x0442:
            java.lang.String r0 = r4.mime_type
            if (r0 != 0) goto L_0x044a
            java.lang.String r0 = "application/octet-stream"
            r4.mime_type = r0
        L_0x044a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r4.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x047a
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            int[] r1 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r22)
            r5 = 0
            r6 = r1[r5]
            r0.w = r6
            r6 = 1
            r1 = r1[r6]
            r0.h = r1
            r0.size = r5
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r1.<init>()
            r0.location = r1
            r0.type = r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs
            r1.add(r0)
            int r0 = r4.flags
            r0 = r0 | r6
            r4.flags = r0
        L_0x047a:
            r15 = r3
            r16 = r15
            r0 = r4
            goto L_0x012e
        L_0x0480:
            r0 = r3
            r8 = r0
        L_0x0482:
            r15 = r8
            r16 = r15
        L_0x0485:
            if (r12 == 0) goto L_0x0492
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.content
            if (r1 == 0) goto L_0x0492
            java.lang.String r1 = r1.url
            java.lang.String r3 = "originalPath"
            r12.put(r3, r1)
        L_0x0492:
            r1 = 1
            android.graphics.Bitmap[] r9 = new android.graphics.Bitmap[r1]
            java.lang.String[] r10 = new java.lang.String[r1]
            boolean r1 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r0)
            if (r1 == 0) goto L_0x04dc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r0.thumbs
            r3 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r13 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r1, r3)
            int r1 = r23.getCurrentAccount()
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)
            java.io.File r1 = r1.getPathToAttach(r0)
            boolean r3 = r1.exists()
            if (r3 != 0) goto L_0x04c4
            int r1 = r23.getCurrentAccount()
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)
            r3 = 1
            java.io.File r1 = r1.getPathToAttach(r0, r3)
        L_0x04c4:
            java.lang.String r4 = r1.getAbsolutePath()
            r5 = 0
            r6 = 0
            r1 = r23
            r3 = r0
            ensureMediaThumbExists(r1, r2, r3, r4, r5, r6)
            r5 = r23
            r1 = 1
            java.lang.String r1 = getKeyForPhotoSize(r5, r13, r9, r1, r1)
            r2 = 0
            r10[r2] = r1
            goto L_0x04de
        L_0x04dc:
            r5 = r23
        L_0x04de:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda74 r17 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda74
            r1 = r17
            r2 = r0
            r3 = r9
            r4 = r10
            r5 = r23
            r6 = r8
            r7 = r20
            r9 = r25
            r10 = r26
            r11 = r22
            r12 = r24
            r13 = r27
            r14 = r28
            r1.<init>(r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14, r15, r16)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r17)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingBotContextResult$80(long, org.telegram.tgnet.TLRPC$BotInlineResult, org.telegram.messenger.AccountInstance, java.util.HashMap, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingBotContextResult$79(TLRPC$TL_document tLRPC$TL_document, Bitmap[] bitmapArr, String[] strArr, AccountInstance accountInstance, String str, long j, MessageObject messageObject, MessageObject messageObject2, TLRPC$BotInlineResult tLRPC$BotInlineResult, HashMap hashMap, boolean z, int i, TLRPC$TL_photo tLRPC$TL_photo, TLRPC$TL_game tLRPC$TL_game) {
        TLRPC$BotInlineResult tLRPC$BotInlineResult2 = tLRPC$BotInlineResult;
        if (tLRPC$TL_document != null) {
            if (!(bitmapArr[0] == null || strArr[0] == null)) {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0], false);
            }
            SendMessagesHelper sendMessagesHelper = accountInstance.getSendMessagesHelper();
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage = tLRPC$BotInlineResult2.send_message;
            sendMessagesHelper.sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject, messageObject2, tLRPC$BotInlineMessage.message, tLRPC$BotInlineMessage.entities, tLRPC$BotInlineMessage.reply_markup, hashMap, z, i, 0, tLRPC$BotInlineResult, (MessageObject.SendAnimationData) null);
        } else if (tLRPC$TL_photo != null) {
            SendMessagesHelper sendMessagesHelper2 = accountInstance.getSendMessagesHelper();
            TLRPC$WebDocument tLRPC$WebDocument = tLRPC$BotInlineResult2.content;
            String str2 = tLRPC$WebDocument != null ? tLRPC$WebDocument.url : null;
            TLRPC$BotInlineMessage tLRPC$BotInlineMessage2 = tLRPC$BotInlineResult2.send_message;
            sendMessagesHelper2.sendMessage(tLRPC$TL_photo, str2, j, messageObject, messageObject2, tLRPC$BotInlineMessage2.message, tLRPC$BotInlineMessage2.entities, tLRPC$BotInlineMessage2.reply_markup, hashMap, z, i, 0, tLRPC$BotInlineResult);
        } else if (tLRPC$TL_game != null) {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_game, j, tLRPC$BotInlineResult2.send_message.reply_markup, (HashMap<String, String>) hashMap, z, i);
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
        accountInstance.getMessagesStorage().getStorageQueue().postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda7(str, accountInstance, j, z, i));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingText$81(String str, AccountInstance accountInstance, long j, boolean z, int i) {
        String trimmedString = getTrimmedString(str);
        if (trimmedString.length() != 0) {
            int ceil = (int) Math.ceil((double) (((float) trimmedString.length()) / 4096.0f));
            int i2 = 0;
            while (i2 < ceil) {
                int i3 = i2 * 4096;
                i2++;
                accountInstance.getSendMessagesHelper().sendMessage(trimmedString.substring(i3, Math.min(i2 * 4096, trimmedString.length())), j, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, (MessageObject.SendAnimationData) null);
            }
        }
    }

    public static void ensureMediaThumbExists(AccountInstance accountInstance, boolean z, TLObject tLObject, String str, Uri uri, long j) {
        TLRPC$PhotoSize scaleAndSaveImage;
        TLRPC$PhotoSize scaleAndSaveImage2;
        TLObject tLObject2 = tLObject;
        String str2 = str;
        Uri uri2 = uri;
        if (tLObject2 instanceof TLRPC$TL_photo) {
            TLRPC$TL_photo tLRPC$TL_photo = (TLRPC$TL_photo) tLObject2;
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_photo.sizes, 90);
            boolean exists = ((closestPhotoSizeWithSize instanceof TLRPC$TL_photoStrippedSize) || (closestPhotoSizeWithSize instanceof TLRPC$TL_photoPathSize)) ? true : FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(closestPhotoSizeWithSize, true).exists();
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(tLRPC$TL_photo.sizes, AndroidUtilities.getPhotoSize());
            boolean exists2 = FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(closestPhotoSizeWithSize2, false).exists();
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
                if (!(closestPhotoSizeWithSize3 instanceof TLRPC$TL_photoStrippedSize) && !(closestPhotoSizeWithSize3 instanceof TLRPC$TL_photoPathSize) && !FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(closestPhotoSizeWithSize3, true).exists()) {
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

    public static String getKeyForPhotoSize(AccountInstance accountInstance, TLRPC$PhotoSize tLRPC$PhotoSize, Bitmap[] bitmapArr, boolean z, boolean z2) {
        if (tLRPC$PhotoSize == null || tLRPC$PhotoSize.location == null) {
            return null;
        }
        Point messageSize = ChatMessageCell.getMessageSize(tLRPC$PhotoSize.w, tLRPC$PhotoSize.h);
        if (bitmapArr != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                File pathToAttach = FileLoader.getInstance(accountInstance.getCurrentAccount()).getPathToAttach(tLRPC$PhotoSize, z2);
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x004c */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean shouldSendWebPAsSticker(java.lang.String r10, android.net.Uri r11) {
        /*
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options
            r0.<init>()
            r1 = 1
            r0.inJustDecodeBounds = r1
            r2 = 0
            if (r10 == 0) goto L_0x0033
            java.io.RandomAccessFile r11 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x002e }
            java.lang.String r3 = "r"
            r11.<init>(r10, r3)     // Catch:{ Exception -> 0x002e }
            java.nio.channels.FileChannel r4 = r11.getChannel()     // Catch:{ Exception -> 0x002e }
            java.nio.channels.FileChannel$MapMode r5 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Exception -> 0x002e }
            r6 = 0
            int r10 = r10.length()     // Catch:{ Exception -> 0x002e }
            long r8 = (long) r10     // Catch:{ Exception -> 0x002e }
            java.nio.MappedByteBuffer r10 = r4.map(r5, r6, r8)     // Catch:{ Exception -> 0x002e }
            int r3 = r10.limit()     // Catch:{ Exception -> 0x002e }
            org.telegram.messenger.Utilities.loadWebpImage(r2, r10, r3, r0, r1)     // Catch:{ Exception -> 0x002e }
            r11.close()     // Catch:{ Exception -> 0x002e }
            goto L_0x004e
        L_0x002e:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
            goto L_0x004e
        L_0x0033:
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x004d }
            android.content.ContentResolver r10 = r10.getContentResolver()     // Catch:{ Exception -> 0x004d }
            java.io.InputStream r10 = r10.openInputStream(r11)     // Catch:{ Exception -> 0x004d }
            android.graphics.BitmapFactory.decodeStream(r10, r2, r0)     // Catch:{ all -> 0x0046 }
            if (r10 == 0) goto L_0x004e
            r10.close()     // Catch:{ Exception -> 0x004d }
            goto L_0x004e
        L_0x0046:
            r11 = move-exception
            if (r10 == 0) goto L_0x004c
            r10.close()     // Catch:{ all -> 0x004c }
        L_0x004c:
            throw r11     // Catch:{ Exception -> 0x004d }
        L_0x004d:
        L_0x004e:
            int r10 = r0.outWidth
            r11 = 800(0x320, float:1.121E-42)
            if (r10 >= r11) goto L_0x0059
            int r10 = r0.outHeight
            if (r10 >= r11) goto L_0x0059
            goto L_0x005a
        L_0x0059:
            r1 = 0
        L_0x005a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.shouldSendWebPAsSticker(java.lang.String, android.net.Uri):boolean");
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
            mediaSendQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda11(arrayList, j, z, z4, accountInstance, messageObject3, messageObject, messageObject2, z3, i, inputContentInfoCompat));
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v102, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v166, resolved type: java.lang.Object[]} */
    /* JADX WARNING: type inference failed for: r8v0 */
    /* JADX WARNING: type inference failed for: r6v27 */
    /* JADX WARNING: type inference failed for: r6v28 */
    /* JADX WARNING: type inference failed for: r8v32, types: [boolean] */
    /* JADX WARNING: type inference failed for: r8v33 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005d, code lost:
        if (r4 != false) goto L_0x0061;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x062c, code lost:
        if (r5 != null) goto L_0x060f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x066d, code lost:
        if (r3.endsWith(r8) != false) goto L_0x068e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x08fe, code lost:
        if (r11 == (r15 - 1)) goto L_0x0903;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0cc6, code lost:
        if (r5 == (r11 - 1)) goto L_0x0ccd;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r6v19, types: [int, boolean] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:234:0x060f */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02db A[Catch:{ Exception -> 0x02cc }] */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02e6 A[Catch:{ Exception -> 0x02cc }] */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02f3 A[Catch:{ Exception -> 0x0312 }] */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0320  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x0367  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x0627 A[SYNTHETIC, Splitter:B:247:0x0627] */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x06bf  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x071a  */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x07b3  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x08e0  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x0909  */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0b2e  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00ac  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:527:0x0c3d  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0c4f  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:542:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x0c9d  */
    /* JADX WARNING: Removed duplicated region for block: B:562:0x0d14 A[LOOP:4: B:560:0x0d0c->B:562:0x0d14, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:595:0x0e6a  */
    /* JADX WARNING: Removed duplicated region for block: B:596:0x0e6d  */
    /* JADX WARNING: Removed duplicated region for block: B:611:0x0c4c A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x014d  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0166  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingMedia$89(java.util.ArrayList r65, long r66, boolean r68, boolean r69, org.telegram.messenger.AccountInstance r70, org.telegram.messenger.MessageObject r71, org.telegram.messenger.MessageObject r72, org.telegram.messenger.MessageObject r73, boolean r74, int r75, androidx.core.view.inputmethod.InputContentInfoCompat r76) {
        /*
            r1 = r65
            r15 = r70
            long r19 = java.lang.System.currentTimeMillis()
            int r14 = r65.size()
            boolean r13 = org.telegram.messenger.DialogObject.isEncryptedDialog(r66)
            java.lang.String r12 = ".webp"
            java.lang.String r11 = ".gif"
            r21 = 3
            java.lang.String r9 = "_"
            r10 = 0
            r8 = 1
            if (r68 != 0) goto L_0x0189
            if (r69 == 0) goto L_0x0189
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r6 = 0
        L_0x0024:
            if (r6 >= r14) goto L_0x0184
            java.lang.Object r2 = r1.get(r6)
            r5 = r2
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r5 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r5
            org.telegram.messenger.MediaController$SearchImage r2 = r5.searchImage
            if (r2 != 0) goto L_0x0178
            boolean r2 = r5.isVideo
            if (r2 != 0) goto L_0x0178
            org.telegram.messenger.VideoEditedInfo r2 = r5.videoEditedInfo
            if (r2 != 0) goto L_0x0178
            java.lang.String r2 = r5.path
            if (r2 != 0) goto L_0x004c
            android.net.Uri r3 = r5.uri
            if (r3 == 0) goto L_0x004c
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.getPath(r3)
            android.net.Uri r3 = r5.uri
            java.lang.String r3 = r3.toString()
            goto L_0x004d
        L_0x004c:
            r3 = r2
        L_0x004d:
            if (r2 == 0) goto L_0x0074
            int r4 = r5.ttl
            if (r4 > 0) goto L_0x0074
            boolean r4 = r2.endsWith(r11)
            if (r4 != 0) goto L_0x0060
            boolean r4 = r2.endsWith(r12)
            if (r4 == 0) goto L_0x0075
            goto L_0x0061
        L_0x0060:
            r4 = 0
        L_0x0061:
            int r7 = r65.size()
            if (r7 > r8) goto L_0x0071
            if (r4 == 0) goto L_0x0178
            boolean r4 = shouldSendWebPAsSticker(r2, r10)
            if (r4 == 0) goto L_0x0071
            goto L_0x0178
        L_0x0071:
            r5.forceImage = r8
            goto L_0x00aa
        L_0x0074:
            r4 = 0
        L_0x0075:
            java.lang.String r7 = r5.path
            android.net.Uri r10 = r5.uri
            boolean r7 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r7, r10)
            if (r7 == 0) goto L_0x0081
            goto L_0x0178
        L_0x0081:
            if (r2 != 0) goto L_0x00aa
            android.net.Uri r7 = r5.uri
            if (r7 == 0) goto L_0x00aa
            boolean r7 = org.telegram.messenger.MediaController.isGif(r7)
            if (r7 != 0) goto L_0x0095
            android.net.Uri r4 = r5.uri
            boolean r4 = org.telegram.messenger.MediaController.isWebp(r4)
            if (r4 == 0) goto L_0x00aa
        L_0x0095:
            int r7 = r65.size()
            if (r7 > r8) goto L_0x00a8
            if (r4 == 0) goto L_0x0178
            android.net.Uri r4 = r5.uri
            r7 = 0
            boolean r4 = shouldSendWebPAsSticker(r7, r4)
            if (r4 == 0) goto L_0x00a8
            goto L_0x0178
        L_0x00a8:
            r5.forceImage = r8
        L_0x00aa:
            if (r2 == 0) goto L_0x00d0
            java.io.File r4 = new java.io.File
            r4.<init>(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r10 = r6
            long r6 = r4.length()
            r2.append(r6)
            r2.append(r9)
            long r3 = r4.lastModified()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            goto L_0x00d2
        L_0x00d0:
            r10 = r6
            r2 = 0
        L_0x00d2:
            if (r13 != 0) goto L_0x014d
            int r3 = r5.ttl
            if (r3 != 0) goto L_0x014d
            org.telegram.messenger.MessagesStorage r3 = r70.getMessagesStorage()
            if (r13 != 0) goto L_0x00e0
            r4 = 0
            goto L_0x00e1
        L_0x00e0:
            r4 = 3
        L_0x00e1:
            java.lang.Object[] r2 = r3.getSentFile(r2, r4)
            if (r2 == 0) goto L_0x00f8
            r3 = 0
            r4 = r2[r3]
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r4 == 0) goto L_0x00f8
            r4 = r2[r3]
            r3 = r4
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3
            r2 = r2[r8]
            java.lang.String r2 = (java.lang.String) r2
            goto L_0x00fa
        L_0x00f8:
            r2 = 0
            r3 = 0
        L_0x00fa:
            if (r3 != 0) goto L_0x0129
            android.net.Uri r4 = r5.uri
            if (r4 == 0) goto L_0x0129
            org.telegram.messenger.MessagesStorage r4 = r70.getMessagesStorage()
            android.net.Uri r6 = r5.uri
            java.lang.String r6 = org.telegram.messenger.AndroidUtilities.getPath(r6)
            if (r13 != 0) goto L_0x010e
            r7 = 0
            goto L_0x010f
        L_0x010e:
            r7 = 3
        L_0x010f:
            java.lang.Object[] r4 = r4.getSentFile(r6, r7)
            if (r4 == 0) goto L_0x0129
            r7 = 0
            r6 = r4[r7]
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r6 == 0) goto L_0x012a
            r2 = r4[r7]
            org.telegram.tgnet.TLRPC$TL_photo r2 = (org.telegram.tgnet.TLRPC$TL_photo) r2
            r3 = r4[r8]
            java.lang.String r3 = (java.lang.String) r3
            r16 = r2
            r18 = r3
            goto L_0x012e
        L_0x0129:
            r7 = 0
        L_0x012a:
            r18 = r2
            r16 = r3
        L_0x012e:
            java.lang.String r6 = r5.path
            android.net.Uri r4 = r5.uri
            r22 = 0
            r2 = r70
            r3 = r13
            r24 = r4
            r4 = r16
            r25 = r5
            r5 = r6
            r6 = r24
            r24 = r12
            r12 = 1
            r7 = r22
            ensureMediaThumbExists(r2, r3, r4, r5, r6, r7)
            r7 = r16
            r2 = r18
            goto L_0x0154
        L_0x014d:
            r25 = r5
            r24 = r12
            r12 = 1
            r2 = 0
            r7 = 0
        L_0x0154:
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r3 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker
            r4 = 0
            r3.<init>()
            r4 = r25
            r0.put(r4, r3)
            if (r7 == 0) goto L_0x0166
            r3.parentObject = r2
            r3.photo = r7
            goto L_0x017c
        L_0x0166:
            java.util.concurrent.CountDownLatch r2 = new java.util.concurrent.CountDownLatch
            r2.<init>(r12)
            r3.sync = r2
            java.util.concurrent.ThreadPoolExecutor r2 = mediaSendThreadPool
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda21 r5 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda21
            r5.<init>(r3, r15, r4, r13)
            r2.execute(r5)
            goto L_0x017c
        L_0x0178:
            r10 = r6
            r24 = r12
            r12 = 1
        L_0x017c:
            int r6 = r10 + 1
            r12 = r24
            r8 = 1
            r10 = 0
            goto L_0x0024
        L_0x0184:
            r24 = r12
            r12 = 1
            r10 = r0
            goto L_0x018d
        L_0x0189:
            r24 = r12
            r12 = 1
            r10 = 0
        L_0x018d:
            r0 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r22 = 0
            r27 = 0
            r28 = 0
            r30 = 0
        L_0x019b:
            if (r6 >= r14) goto L_0x0de5
            java.lang.Object r8 = r1.get(r6)
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r8 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r8
            if (r69 == 0) goto L_0x01b6
            if (r14 <= r12) goto L_0x01b6
            int r16 = r0 % 10
            if (r16 != 0) goto L_0x01b6
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r28 = r0.nextLong()
            r34 = r28
            r16 = 0
            goto L_0x01ba
        L_0x01b6:
            r16 = r0
            r34 = r30
        L_0x01ba:
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            r18 = r6
            java.lang.String r6 = "video/mp4"
            java.lang.String r7 = "1"
            java.lang.String r12 = "final"
            java.lang.String r1 = "groupId"
            r30 = r2
            java.lang.String r2 = "mp4"
            r31 = r9
            java.lang.String r9 = "originalPath"
            r36 = r10
            java.lang.String r10 = ""
            r38 = r3
            java.lang.String r3 = "jpg"
            r39 = r4
            java.lang.String r4 = "."
            r40 = 4
            if (r0 == 0) goto L_0x0557
            r41 = r5
            org.telegram.messenger.VideoEditedInfo r5 = r8.videoEditedInfo
            if (r5 != 0) goto L_0x053f
            int r5 = r0.type
            r42 = r11
            r11 = 1
            if (r5 != r11) goto L_0x03c2
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r1 == 0) goto L_0x0207
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            int r1 = r70.getCurrentAccount()
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)
            java.io.File r1 = r1.getPathToAttach(r0, r11)
            goto L_0x0233
        L_0x0207:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r1 = r8.searchImage
            java.lang.String r1 = r1.imageUrl
            java.lang.String r1 = org.telegram.messenger.Utilities.MD5(r1)
            r0.append(r1)
            r0.append(r4)
            org.telegram.messenger.MediaController$SearchImage r1 = r8.searchImage
            java.lang.String r1 = r1.imageUrl
            java.lang.String r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r3)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.io.File r1 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r1.<init>(r5, r0)
            r0 = 0
        L_0x0233:
            if (r0 != 0) goto L_0x0350
            org.telegram.tgnet.TLRPC$TL_document r5 = new org.telegram.tgnet.TLRPC$TL_document
            r5.<init>()
            r10 = 0
            r5.id = r10
            r12 = 0
            byte[] r0 = new byte[r12]
            r5.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r70.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r5.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            java.lang.String r12 = "animation.gif"
            r0.file_name = r12
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r5.attributes
            r12.add(r0)
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            int r0 = r0.size
            long r10 = (long) r0
            r5.size = r10
            r10 = 0
            r5.dc_id = r10
            if (r68 != 0) goto L_0x027e
            java.lang.String r0 = r1.toString()
            boolean r0 = r0.endsWith(r2)
            if (r0 == 0) goto L_0x027e
            r5.mime_type = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r5.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r6.<init>()
            r0.add(r6)
            goto L_0x0282
        L_0x027e:
            java.lang.String r0 = "image/gif"
            r5.mime_type = r0
        L_0x0282:
            boolean r0 = r1.exists()
            if (r0 == 0) goto L_0x028a
            r6 = r1
            goto L_0x028c
        L_0x028a:
            r1 = 0
            r6 = 0
        L_0x028c:
            if (r1 != 0) goto L_0x02c0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r1 = r8.searchImage
            java.lang.String r1 = r1.thumbUrl
            java.lang.String r1 = org.telegram.messenger.Utilities.MD5(r1)
            r0.append(r1)
            r0.append(r4)
            org.telegram.messenger.MediaController$SearchImage r1 = r8.searchImage
            java.lang.String r1 = r1.thumbUrl
            java.lang.String r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r3)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.io.File r1 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r1.<init>(r3, r0)
            boolean r0 = r1.exists()
            if (r0 != 0) goto L_0x02c0
            r1 = 0
        L_0x02c0:
            if (r1 == 0) goto L_0x0317
            if (r13 != 0) goto L_0x02cf
            int r0 = r8.ttl     // Catch:{ Exception -> 0x02cc }
            if (r0 == 0) goto L_0x02c9
            goto L_0x02cf
        L_0x02c9:
            r0 = 320(0x140, float:4.48E-43)
            goto L_0x02d1
        L_0x02cc:
            r0 = move-exception
            r10 = 0
            goto L_0x0313
        L_0x02cf:
            r0 = 90
        L_0x02d1:
            java.lang.String r3 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x02cc }
            boolean r2 = r3.endsWith(r2)     // Catch:{ Exception -> 0x02cc }
            if (r2 == 0) goto L_0x02e6
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x02cc }
            r2 = 1
            android.graphics.Bitmap r1 = createVideoThumbnail(r1, r2)     // Catch:{ Exception -> 0x02cc }
            r10 = 0
            goto L_0x02f1
        L_0x02e6:
            r2 = 1
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x02cc }
            float r3 = (float) r0
            r10 = 0
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r10, r3, r3, r2)     // Catch:{ Exception -> 0x0312 }
        L_0x02f1:
            if (r1 == 0) goto L_0x0318
            float r2 = (float) r0     // Catch:{ Exception -> 0x0312 }
            r3 = 90
            if (r0 <= r3) goto L_0x02fb
            r0 = 80
            goto L_0x02fd
        L_0x02fb:
            r0 = 55
        L_0x02fd:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r2, r2, r0, r13)     // Catch:{ Exception -> 0x0312 }
            if (r0 == 0) goto L_0x030e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r5.thumbs     // Catch:{ Exception -> 0x0312 }
            r2.add(r0)     // Catch:{ Exception -> 0x0312 }
            int r0 = r5.flags     // Catch:{ Exception -> 0x0312 }
            r2 = 1
            r0 = r0 | r2
            r5.flags = r0     // Catch:{ Exception -> 0x0312 }
        L_0x030e:
            r1.recycle()     // Catch:{ Exception -> 0x0312 }
            goto L_0x0318
        L_0x0312:
            r0 = move-exception
        L_0x0313:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0318
        L_0x0317:
            r10 = 0
        L_0x0318:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r5.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x034b
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r1 = r8.searchImage
            int r2 = r1.width
            r0.w = r2
            int r1 = r1.height
            r0.h = r1
            r12 = 0
            r0.size = r12
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r1.<init>()
            r0.location = r1
            java.lang.String r1 = "x"
            r0.type = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r5.thumbs
            r1.add(r0)
            int r0 = r5.flags
            r17 = 1
            r0 = r0 | 1
            r5.flags = r0
            goto L_0x034e
        L_0x034b:
            r12 = 0
            r17 = 1
        L_0x034e:
            r1 = r6
            goto L_0x0355
        L_0x0350:
            r10 = 0
            r12 = 0
            r17 = 1
            r5 = r0
        L_0x0355:
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r1 != 0) goto L_0x035c
            goto L_0x0360
        L_0x035c:
            java.lang.String r0 = r1.toString()
        L_0x0360:
            r6 = r0
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r0 == 0) goto L_0x036a
            r7.put(r9, r0)
        L_0x036a:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda14 r0 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda14
            r1 = 0
            r11 = r30
            r2 = r0
            r9 = r38
            r3 = r71
            r43 = r39
            r4 = r70
            r44 = r41
            r45 = r18
            r25 = 0
            r18 = r8
            r8 = r1
            r46 = r9
            r1 = r10
            r48 = r31
            r47 = r36
            r9 = r66
            r49 = r11
            r50 = r42
            r11 = r72
            r51 = r24
            r12 = r73
            r52 = r13
            r13 = r18
            r53 = r14
            r14 = r74
            r15 = r75
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            r0 = r16
            r37 = r34
            r4 = r43
            r5 = r44
            r31 = r45
            r3 = r46
            r33 = r47
            r41 = r48
            r2 = r49
            r23 = r50
            r30 = r51
            r34 = r52
            r32 = 0
            r35 = r1
            goto L_0x053b
        L_0x03c2:
            r52 = r13
            r53 = r14
            r45 = r18
            r51 = r24
            r49 = r30
            r48 = r31
            r47 = r36
            r46 = r38
            r43 = r39
            r44 = r41
            r50 = r42
            r15 = 0
            r25 = 0
            r18 = r8
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r2 == 0) goto L_0x03e6
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            goto L_0x03e7
        L_0x03e6:
            r0 = r15
        L_0x03e7:
            if (r0 != 0) goto L_0x04b0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r14 = r18
            org.telegram.messenger.MediaController$SearchImage r5 = r14.searchImage
            java.lang.String r5 = r5.imageUrl
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)
            r2.append(r5)
            r2.append(r4)
            org.telegram.messenger.MediaController$SearchImage r5 = r14.searchImage
            java.lang.String r5 = r5.imageUrl
            java.lang.String r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r3)
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r5.<init>(r6, r2)
            boolean r2 = r5.exists()
            if (r2 == 0) goto L_0x0434
            long r17 = r5.length()
            int r2 = (r17 > r25 ? 1 : (r17 == r25 ? 0 : -1))
            if (r2 == 0) goto L_0x0434
            org.telegram.messenger.SendMessagesHelper r0 = r70.getSendMessagesHelper()
            java.lang.String r2 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r15)
            if (r0 == 0) goto L_0x0434
            r2 = 0
            goto L_0x0435
        L_0x0434:
            r2 = 1
        L_0x0435:
            if (r0 != 0) goto L_0x04ac
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            org.telegram.messenger.MediaController$SearchImage r6 = r14.searchImage
            java.lang.String r6 = r6.thumbUrl
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r6)
            r5.append(r6)
            r5.append(r4)
            org.telegram.messenger.MediaController$SearchImage r4 = r14.searchImage
            java.lang.String r4 = r4.thumbUrl
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r3)
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            java.io.File r4 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r4.<init>(r5, r3)
            boolean r3 = r4.exists()
            if (r3 == 0) goto L_0x0474
            org.telegram.messenger.SendMessagesHelper r0 = r70.getSendMessagesHelper()
            java.lang.String r3 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r3, r15)
        L_0x0474:
            if (r0 != 0) goto L_0x04ac
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r3 = r70.getConnectionsManager()
            int r3 = r3.getCurrentTime()
            r0.date = r3
            r13 = 0
            byte[] r3 = new byte[r13]
            r0.file_reference = r3
            org.telegram.tgnet.TLRPC$TL_photoSize r3 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r3.<init>()
            org.telegram.messenger.MediaController$SearchImage r4 = r14.searchImage
            int r5 = r4.width
            r3.w = r5
            int r4 = r4.height
            r3.h = r4
            r3.size = r13
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r4 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r4.<init>()
            r3.location = r4
            java.lang.String r4 = "x"
            r3.type = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r0.sizes
            r4.add(r3)
            goto L_0x04ad
        L_0x04ac:
            r13 = 0
        L_0x04ad:
            r5 = r0
            r6 = r2
            goto L_0x04b5
        L_0x04b0:
            r14 = r18
            r13 = 0
            r5 = r0
            r6 = 1
        L_0x04b5:
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            org.telegram.messenger.MediaController$SearchImage r0 = r14.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r0 == 0) goto L_0x04c3
            r8.put(r9, r0)
        L_0x04c3:
            if (r69 == 0) goto L_0x04f7
            int r0 = r16 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r10)
            r10 = r34
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            r8.put(r1, r2)
            r1 = 10
            if (r0 == r1) goto L_0x04eb
            r1 = r53
            int r2 = r1 + -1
            r9 = r45
            if (r9 != r2) goto L_0x04e8
            goto L_0x04ef
        L_0x04e8:
            r16 = r0
            goto L_0x04fd
        L_0x04eb:
            r9 = r45
            r1 = r53
        L_0x04ef:
            r8.put(r12, r7)
            r16 = r0
            r28 = r25
            goto L_0x04fd
        L_0x04f7:
            r10 = r34
            r9 = r45
            r1 = r53
        L_0x04fd:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda16 r0 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda16
            r12 = 0
            r2 = r0
            r3 = r71
            r4 = r70
            r7 = r14
            r14 = r9
            r9 = r12
            r54 = r10
            r10 = r66
            r12 = r72
            r13 = r73
            r56 = r14
            r14 = r74
            r53 = r1
            r1 = r15
            r15 = r75
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            r35 = r1
            r0 = r16
            r4 = r43
            r5 = r44
            r3 = r46
            r33 = r47
            r41 = r48
            r2 = r49
            r23 = r50
            r30 = r51
            r34 = r52
            r37 = r54
            r31 = r56
            r32 = 0
        L_0x053b:
            r1 = r70
            goto L_0x0dcf
        L_0x053f:
            r50 = r11
            r52 = r13
            r15 = r14
            r56 = r18
            r51 = r24
            r49 = r30
            r48 = r31
            r54 = r34
            r47 = r36
            r46 = r38
            r43 = r39
            r44 = r41
            goto L_0x056e
        L_0x0557:
            r44 = r5
            r50 = r11
            r52 = r13
            r15 = r14
            r56 = r18
            r51 = r24
            r49 = r30
            r48 = r31
            r54 = r34
            r47 = r36
            r46 = r38
            r43 = r39
        L_0x056e:
            r25 = 0
            r11 = r1
            r14 = r8
            r1 = 0
            r8 = 10
            boolean r0 = r14.isVideo
            if (r0 != 0) goto L_0x09a2
            org.telegram.messenger.VideoEditedInfo r0 = r14.videoEditedInfo
            if (r0 == 0) goto L_0x057f
            goto L_0x09a2
        L_0x057f:
            java.lang.String r0 = r14.path
            if (r0 != 0) goto L_0x05a9
            android.net.Uri r2 = r14.uri
            if (r2 == 0) goto L_0x05a9
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 30
            if (r0 < r3) goto L_0x059b
            java.lang.String r0 = r2.getScheme()
            java.lang.String r2 = "content"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x059b
            r0 = r1
            goto L_0x05a1
        L_0x059b:
            android.net.Uri r0 = r14.uri
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r0)
        L_0x05a1:
            android.net.Uri r2 = r14.uri
            java.lang.String r2 = r2.toString()
            r3 = r0
            goto L_0x05ab
        L_0x05a9:
            r2 = r0
            r3 = r2
        L_0x05ab:
            if (r76 == 0) goto L_0x063e
            android.net.Uri r0 = r14.uri
            if (r0 == 0) goto L_0x063e
            android.content.ClipDescription r0 = r76.getDescription()
            java.lang.String r4 = "image/png"
            boolean r0 = r0.hasMimeType(r4)
            if (r0 == 0) goto L_0x063e
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x061d }
            r0.<init>()     // Catch:{ all -> 0x061d }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x061d }
            android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x061d }
            android.net.Uri r5 = r14.uri     // Catch:{ all -> 0x061d }
            java.io.InputStream r4 = r4.openInputStream(r5)     // Catch:{ all -> 0x061d }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r4, r1, r0)     // Catch:{ all -> 0x0619 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0619 }
            r5.<init>()     // Catch:{ all -> 0x0619 }
            java.lang.String r6 = "-2147483648_"
            r5.append(r6)     // Catch:{ all -> 0x0619 }
            int r6 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ all -> 0x0619 }
            r5.append(r6)     // Catch:{ all -> 0x0619 }
            r13 = r51
            r5.append(r13)     // Catch:{ all -> 0x0617 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0617 }
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r40)     // Catch:{ all -> 0x0617 }
            java.io.File r8 = new java.io.File     // Catch:{ all -> 0x0617 }
            r8.<init>(r6, r5)     // Catch:{ all -> 0x0617 }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ all -> 0x0617 }
            r5.<init>(r8)     // Catch:{ all -> 0x0617 }
            android.graphics.Bitmap$CompressFormat r6 = android.graphics.Bitmap.CompressFormat.WEBP     // Catch:{ all -> 0x0615 }
            r1 = 100
            r0.compress(r6, r1, r5)     // Catch:{ all -> 0x0615 }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ all -> 0x0615 }
            android.net.Uri r0 = android.net.Uri.fromFile(r8)     // Catch:{ all -> 0x0615 }
            r14.uri = r0     // Catch:{ all -> 0x0615 }
            if (r4 == 0) goto L_0x060f
            r4.close()     // Catch:{ Exception -> 0x060f }
        L_0x060f:
            r5.close()     // Catch:{ Exception -> 0x0613 }
            goto L_0x0640
        L_0x0613:
            goto L_0x0640
        L_0x0615:
            r0 = move-exception
            goto L_0x0622
        L_0x0617:
            r0 = move-exception
            goto L_0x0621
        L_0x0619:
            r0 = move-exception
            r13 = r51
            goto L_0x0621
        L_0x061d:
            r0 = move-exception
            r13 = r51
            r4 = 0
        L_0x0621:
            r5 = 0
        L_0x0622:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x062f }
            if (r4 == 0) goto L_0x062c
            r4.close()     // Catch:{ Exception -> 0x062b }
            goto L_0x062c
        L_0x062b:
        L_0x062c:
            if (r5 == 0) goto L_0x0640
            goto L_0x060f
        L_0x062f:
            r0 = move-exception
            r1 = r0
            if (r4 == 0) goto L_0x0638
            r4.close()     // Catch:{ Exception -> 0x0637 }
            goto L_0x0638
        L_0x0637:
        L_0x0638:
            if (r5 == 0) goto L_0x063d
            r5.close()     // Catch:{ Exception -> 0x063d }
        L_0x063d:
            throw r1
        L_0x063e:
            r13 = r51
        L_0x0640:
            java.lang.String r0 = "webp"
            java.lang.String r1 = "gif"
            if (r68 != 0) goto L_0x06ab
            java.lang.String r4 = r14.path
            android.net.Uri r5 = r14.uri
            boolean r4 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r4, r5)
            if (r4 == 0) goto L_0x0651
            goto L_0x06ab
        L_0x0651:
            boolean r4 = r14.forceImage
            if (r4 != 0) goto L_0x0670
            if (r3 == 0) goto L_0x0670
            r8 = r50
            boolean r4 = r3.endsWith(r8)
            if (r4 != 0) goto L_0x0665
            boolean r4 = r3.endsWith(r13)
            if (r4 == 0) goto L_0x0672
        L_0x0665:
            int r4 = r14.ttl
            if (r4 > 0) goto L_0x0672
            boolean r4 = r3.endsWith(r8)
            if (r4 == 0) goto L_0x06a5
            goto L_0x068e
        L_0x0670:
            r8 = r50
        L_0x0672:
            boolean r4 = r14.forceImage
            if (r4 != 0) goto L_0x06a8
            if (r3 != 0) goto L_0x06a8
            android.net.Uri r4 = r14.uri
            if (r4 == 0) goto L_0x06a8
            boolean r4 = org.telegram.messenger.MediaController.isGif(r4)
            if (r4 == 0) goto L_0x0691
            android.net.Uri r0 = r14.uri
            java.lang.String r2 = r0.toString()
            android.net.Uri r0 = r14.uri
            java.lang.String r3 = org.telegram.messenger.MediaController.copyFileToCache(r0, r1)
        L_0x068e:
            r22 = r1
            goto L_0x06bb
        L_0x0691:
            android.net.Uri r1 = r14.uri
            boolean r1 = org.telegram.messenger.MediaController.isWebp(r1)
            if (r1 == 0) goto L_0x06a8
            android.net.Uri r1 = r14.uri
            java.lang.String r2 = r1.toString()
            android.net.Uri r1 = r14.uri
            java.lang.String r3 = org.telegram.messenger.MediaController.copyFileToCache(r1, r0)
        L_0x06a5:
            r22 = r0
            goto L_0x06bb
        L_0x06a8:
            r1 = r3
            r0 = 0
            goto L_0x06bd
        L_0x06ab:
            r8 = r50
            if (r3 == 0) goto L_0x06b9
            java.io.File r0 = new java.io.File
            r0.<init>(r3)
            java.lang.String r0 = org.telegram.messenger.FileLoader.getFileExtension(r0)
            goto L_0x06a5
        L_0x06b9:
            r22 = r10
        L_0x06bb:
            r1 = r3
            r0 = 1
        L_0x06bd:
            if (r0 == 0) goto L_0x071a
            r6 = r44
            if (r6 != 0) goto L_0x06df
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r27 = new java.util.ArrayList
            r27.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r6 = r27
            goto L_0x06e8
        L_0x06df:
            r5 = r6
            r6 = r27
            r4 = r43
            r3 = r46
            r0 = r49
        L_0x06e8:
            r5.add(r1)
            r4.add(r2)
            android.net.Uri r1 = r14.uri
            r3.add(r1)
            java.lang.String r1 = r14.caption
            r0.add(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r14.entities
            r6.add(r1)
            r1 = r70
            r2 = r0
            r27 = r6
            r23 = r8
            r30 = r13
            r53 = r15
            r0 = r16
            r33 = r47
            r41 = r48
            r34 = r52
            r37 = r54
            r31 = r56
        L_0x0714:
            r32 = 0
            r35 = 0
            goto L_0x0dcf
        L_0x071a:
            r6 = r44
            if (r1 == 0) goto L_0x0746
            java.io.File r0 = new java.io.File
            r0.<init>(r1)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            long r4 = r0.length()
            r3.append(r4)
            r5 = r48
            r3.append(r5)
            long r4 = r0.lastModified()
            r3.append(r4)
            java.lang.String r0 = r3.toString()
            r5 = r0
            r4 = r47
            goto L_0x0749
        L_0x0746:
            r4 = r47
            r5 = 0
        L_0x0749:
            if (r4 == 0) goto L_0x077c
            java.lang.Object r0 = r4.get(r14)
            r2 = r0
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
            if (r0 != 0) goto L_0x0766
            java.util.concurrent.CountDownLatch r0 = r2.sync     // Catch:{ Exception -> 0x075e }
            r0.await()     // Catch:{ Exception -> 0x075e }
            goto L_0x0762
        L_0x075e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0762:
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
        L_0x0766:
            r32 = r1
            r33 = r4
            r57 = r5
            r44 = r6
            r23 = r8
            r51 = r13
            r58 = r48
            r8 = r52
            r1 = 1
            r6 = r3
            r13 = r7
            r7 = r0
            goto L_0x0848
        L_0x077c:
            r3 = r52
            if (r3 != 0) goto L_0x080d
            int r0 = r14.ttl
            if (r0 != 0) goto L_0x080d
            org.telegram.messenger.MessagesStorage r0 = r70.getMessagesStorage()
            if (r3 != 0) goto L_0x078c
            r2 = 0
            goto L_0x078d
        L_0x078c:
            r2 = 3
        L_0x078d:
            java.lang.Object[] r0 = r0.getSentFile(r5, r2)
            r47 = r4
            if (r0 == 0) goto L_0x07ab
            r2 = 0
            r4 = r0[r2]
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r4 == 0) goto L_0x07a8
            r4 = r0[r2]
            org.telegram.tgnet.TLRPC$TL_photo r4 = (org.telegram.tgnet.TLRPC$TL_photo) r4
            r17 = r7
            r7 = 1
            r0 = r0[r7]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x07b1
        L_0x07a8:
            r17 = r7
            goto L_0x07ae
        L_0x07ab:
            r17 = r7
            r2 = 0
        L_0x07ae:
            r7 = 1
            r0 = 0
            r4 = 0
        L_0x07b1:
            if (r4 != 0) goto L_0x07de
            android.net.Uri r7 = r14.uri
            if (r7 == 0) goto L_0x07e1
            org.telegram.messenger.MessagesStorage r7 = r70.getMessagesStorage()
            android.net.Uri r2 = r14.uri
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.getPath(r2)
            r18 = r0
            if (r3 != 0) goto L_0x07c7
            r0 = 0
            goto L_0x07c8
        L_0x07c7:
            r0 = 3
        L_0x07c8:
            java.lang.Object[] r0 = r7.getSentFile(r2, r0)
            r2 = 0
            if (r0 == 0) goto L_0x07e3
            r7 = r0[r2]
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r7 == 0) goto L_0x07e3
            r4 = r0[r2]
            org.telegram.tgnet.TLRPC$TL_photo r4 = (org.telegram.tgnet.TLRPC$TL_photo) r4
            r7 = 1
            r0 = r0[r7]
            java.lang.String r0 = (java.lang.String) r0
        L_0x07de:
            r18 = r0
            goto L_0x07e4
        L_0x07e1:
            r18 = r0
        L_0x07e3:
            r7 = 1
        L_0x07e4:
            r0 = r4
            java.lang.String r4 = r14.path
            android.net.Uri r7 = r14.uri
            r30 = 0
            r2 = r70
            r52 = r3
            r32 = r4
            r33 = r47
            r4 = r0
            r57 = r5
            r58 = r48
            r5 = r32
            r32 = r1
            r1 = r6
            r6 = r7
            r44 = r1
            r23 = r8
            r51 = r13
            r13 = r17
            r1 = 1
            r7 = r30
            ensureMediaThumbExists(r2, r3, r4, r5, r6, r7)
            goto L_0x0822
        L_0x080d:
            r32 = r1
            r52 = r3
            r33 = r4
            r57 = r5
            r44 = r6
            r23 = r8
            r51 = r13
            r58 = r48
            r1 = 1
            r13 = r7
            r0 = 0
            r18 = 0
        L_0x0822:
            if (r0 != 0) goto L_0x0843
            org.telegram.messenger.SendMessagesHelper r0 = r70.getSendMessagesHelper()
            java.lang.String r2 = r14.path
            android.net.Uri r3 = r14.uri
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r3)
            r8 = r52
            if (r8 == 0) goto L_0x0845
            boolean r2 = r14.canDeleteAfter
            if (r2 == 0) goto L_0x0845
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r14.path
            r2.<init>(r3)
            r2.delete()
            goto L_0x0845
        L_0x0843:
            r8 = r52
        L_0x0845:
            r7 = r0
            r6 = r18
        L_0x0848:
            if (r7 == 0) goto L_0x0942
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r1]
            java.lang.String[] r4 = new java.lang.String[r1]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r0 = r14.masks
            if (r0 == 0) goto L_0x085f
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x085f
            r0 = 1
            goto L_0x0860
        L_0x085f:
            r0 = 0
        L_0x0860:
            r7.has_stickers = r0
            if (r0 == 0) goto L_0x08a3
            org.telegram.tgnet.SerializedData r0 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r14.masks
            int r2 = r2.size()
            int r2 = r2 * 20
            int r2 = r2 + 4
            r0.<init>((int) r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r14.masks
            int r2 = r2.size()
            r0.writeInt32(r2)
            r2 = 0
        L_0x087d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r14.masks
            int r1 = r1.size()
            if (r2 >= r1) goto L_0x0893
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r14.masks
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$InputDocument r1 = (org.telegram.tgnet.TLRPC$InputDocument) r1
            r1.serializeToStream(r0)
            int r2 = r2 + 1
            goto L_0x087d
        L_0x0893:
            byte[] r1 = r0.toByteArray()
            java.lang.String r1 = org.telegram.messenger.Utilities.bytesToHex(r1)
            java.lang.String r2 = "masks"
            r5.put(r2, r1)
            r0.cleanup()
        L_0x08a3:
            r1 = r57
            if (r1 == 0) goto L_0x08aa
            r5.put(r9, r1)
        L_0x08aa:
            if (r6 == 0) goto L_0x08b1
            java.lang.String r0 = "parentObject"
            r5.put(r0, r6)
        L_0x08b1:
            if (r69 == 0) goto L_0x08bf
            int r0 = r65.size()     // Catch:{ Exception -> 0x08d7 }
            r1 = 1
            if (r0 != r1) goto L_0x08bb
            goto L_0x08bf
        L_0x08bb:
            r1 = r70
            r9 = 0
            goto L_0x08de
        L_0x08bf:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r7.sizes     // Catch:{ Exception -> 0x08d7 }
            int r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize()     // Catch:{ Exception -> 0x08d7 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r1)     // Catch:{ Exception -> 0x08d7 }
            if (r0 == 0) goto L_0x08bb
            r1 = r70
            r9 = 0
            java.lang.String r0 = getKeyForPhotoSize(r1, r0, r3, r9, r9)     // Catch:{ Exception -> 0x08d5 }
            r4[r9] = r0     // Catch:{ Exception -> 0x08d5 }
            goto L_0x08de
        L_0x08d5:
            r0 = move-exception
            goto L_0x08db
        L_0x08d7:
            r0 = move-exception
            r1 = r70
            r9 = 0
        L_0x08db:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x08de:
            if (r69 == 0) goto L_0x0909
            int r0 = r16 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r10)
            r9 = r54
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r5.put(r11, r2)
            r2 = 10
            if (r0 == r2) goto L_0x0901
            int r2 = r15 + -1
            r11 = r56
            if (r11 != r2) goto L_0x090f
            goto L_0x0903
        L_0x0901:
            r11 = r56
        L_0x0903:
            r5.put(r12, r13)
            r28 = r25
            goto L_0x090f
        L_0x0909:
            r9 = r54
            r11 = r56
            r0 = r16
        L_0x090f:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda75 r17 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda75
            r2 = r17
            r12 = r5
            r5 = r71
            r18 = r6
            r6 = r70
            r13 = r8
            r8 = r12
            r59 = r9
            r12 = 0
            r9 = r18
            r61 = r11
            r10 = r66
            r12 = r72
            r62 = r13
            r30 = r51
            r13 = r73
            r63 = r15
            r15 = r74
            r16 = r75
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r17)
            r4 = r43
            r5 = r44
            r3 = r46
            r2 = r49
            goto L_0x0996
        L_0x0942:
            r62 = r8
            r63 = r15
            r30 = r51
            r59 = r54
            r61 = r56
            r1 = r57
            r15 = r70
            if (r44 != 0) goto L_0x0970
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r27 = new java.util.ArrayList
            r27.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0 = r27
            r6 = r32
            goto L_0x097c
        L_0x0970:
            r0 = r27
            r6 = r32
            r4 = r43
            r5 = r44
            r3 = r46
            r2 = r49
        L_0x097c:
            r5.add(r6)
            r4.add(r1)
            android.net.Uri r1 = r14.uri
            r3.add(r1)
            java.lang.String r1 = r14.caption
            r2.add(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r14.entities
            r0.add(r1)
            r27 = r0
            r1 = r15
            r0 = r16
        L_0x0996:
            r41 = r58
            r37 = r59
            r31 = r61
            r34 = r62
            r53 = r63
            goto L_0x0714
        L_0x09a2:
            r13 = r7
            r63 = r15
            r33 = r47
            r58 = r48
            r23 = r50
            r30 = r51
            r62 = r52
            r59 = r54
            r61 = r56
            r15 = r70
            if (r68 == 0) goto L_0x09b9
            r0 = 0
            goto L_0x09c4
        L_0x09b9:
            org.telegram.messenger.VideoEditedInfo r0 = r14.videoEditedInfo
            if (r0 == 0) goto L_0x09be
            goto L_0x09c4
        L_0x09be:
            java.lang.String r0 = r14.path
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r0)
        L_0x09c4:
            if (r68 != 0) goto L_0x0d74
            if (r0 != 0) goto L_0x09d0
            java.lang.String r1 = r14.path
            boolean r1 = r1.endsWith(r2)
            if (r1 == 0) goto L_0x0d74
        L_0x09d0:
            java.lang.String r1 = r14.path
            if (r1 != 0) goto L_0x0a27
            org.telegram.messenger.MediaController$SearchImage r1 = r14.searchImage
            if (r1 == 0) goto L_0x0a27
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r1 == 0) goto L_0x09f6
            int r1 = r70.getCurrentAccount()
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)
            org.telegram.messenger.MediaController$SearchImage r2 = r14.searchImage
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            r3 = 1
            java.io.File r1 = r1.getPathToAttach(r2, r3)
            java.lang.String r1 = r1.getAbsolutePath()
            r14.path = r1
            goto L_0x0a27
        L_0x09f6:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r14.searchImage
            java.lang.String r2 = r2.imageUrl
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r1.append(r2)
            r1.append(r4)
            org.telegram.messenger.MediaController$SearchImage r2 = r14.searchImage
            java.lang.String r2 = r2.imageUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r2, r3)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.io.File r2 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r2.<init>(r3, r1)
            java.lang.String r1 = r2.getAbsolutePath()
            r14.path = r1
        L_0x0a27:
            java.lang.String r1 = r14.path
            java.io.File r7 = new java.io.File
            r7.<init>(r1)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            long r3 = r7.length()
            r2.append(r3)
            r8 = r58
            r2.append(r8)
            long r3 = r7.lastModified()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            if (r0 == 0) goto L_0x0aa9
            boolean r3 = r0.muted
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r18 = r1
            long r1 = r0.estimatedDuration
            r4.append(r1)
            r4.append(r8)
            long r1 = r0.startTime
            r4.append(r1)
            r4.append(r8)
            long r1 = r0.endTime
            r4.append(r1)
            boolean r1 = r0.muted
            if (r1 == 0) goto L_0x0a77
            java.lang.String r1 = "_m"
            goto L_0x0a78
        L_0x0a77:
            r1 = r10
        L_0x0a78:
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            int r2 = r0.resultWidth
            int r4 = r0.originalWidth
            if (r2 == r4) goto L_0x0a99
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            r2.append(r8)
            int r1 = r0.resultWidth
            r2.append(r1)
            java.lang.String r1 = r2.toString()
        L_0x0a99:
            r2 = r1
            long r4 = r0.startTime
            int r1 = (r4 > r25 ? 1 : (r4 == r25 ? 0 : -1))
            if (r1 < 0) goto L_0x0aa1
            goto L_0x0aa3
        L_0x0aa1:
            r4 = r25
        L_0x0aa3:
            r1 = r2
            r31 = r3
            r3 = r62
            goto L_0x0ab2
        L_0x0aa9:
            r18 = r1
            r1 = r2
            r4 = r25
            r3 = r62
            r31 = 0
        L_0x0ab2:
            if (r3 != 0) goto L_0x0b1e
            int r2 = r14.ttl
            if (r2 != 0) goto L_0x0b1e
            if (r0 == 0) goto L_0x0aca
            org.telegram.messenger.MediaController$SavedFilterState r2 = r0.filterState
            if (r2 != 0) goto L_0x0b1e
            java.lang.String r2 = r0.paintPath
            if (r2 != 0) goto L_0x0b1e
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r2 = r0.mediaEntities
            if (r2 != 0) goto L_0x0b1e
            org.telegram.messenger.MediaController$CropState r2 = r0.cropState
            if (r2 != 0) goto L_0x0b1e
        L_0x0aca:
            org.telegram.messenger.MessagesStorage r2 = r70.getMessagesStorage()
            if (r3 != 0) goto L_0x0ad6
            r32 = 2
            r52 = r3
            r3 = 2
            goto L_0x0adb
        L_0x0ad6:
            r32 = 5
            r52 = r3
            r3 = 5
        L_0x0adb:
            java.lang.Object[] r2 = r2.getSentFile(r1, r3)
            r34 = r4
            if (r2 == 0) goto L_0x0b15
            r3 = 0
            r4 = r2[r3]
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r4 == 0) goto L_0x0b15
            r4 = r2[r3]
            r32 = r4
            org.telegram.tgnet.TLRPC$TL_document r32 = (org.telegram.tgnet.TLRPC$TL_document) r32
            r4 = 1
            r2 = r2[r4]
            r36 = r2
            java.lang.String r36 = (java.lang.String) r36
            java.lang.String r5 = r14.path
            r38 = 0
            r2 = r70
            r4 = r52
            r3 = r4
            r64 = r4
            r4 = r32
            r39 = r12
            r12 = r6
            r6 = r38
            r38 = r7
            r41 = r8
            r7 = r34
            ensureMediaThumbExists(r2, r3, r4, r5, r6, r7)
            r8 = r36
            goto L_0x0b2c
        L_0x0b15:
            r38 = r7
            r41 = r8
            r39 = r12
            r64 = r52
            goto L_0x0b28
        L_0x0b1e:
            r64 = r3
            r34 = r4
            r38 = r7
            r41 = r8
            r39 = r12
        L_0x0b28:
            r12 = r6
            r8 = 0
            r32 = 0
        L_0x0b2c:
            if (r32 != 0) goto L_0x0CLASSNAME
            java.lang.String r2 = r14.thumbPath
            if (r2 == 0) goto L_0x0b37
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2)
            goto L_0x0b38
        L_0x0b37:
            r2 = 0
        L_0x0b38:
            if (r2 != 0) goto L_0x0b4b
            java.lang.String r2 = r14.path
            r4 = r34
            android.graphics.Bitmap r2 = createVideoThumbnailAtTime(r2, r4)
            if (r2 != 0) goto L_0x0b4b
            java.lang.String r2 = r14.path
            r3 = 1
            android.graphics.Bitmap r2 = createVideoThumbnail(r2, r3)
        L_0x0b4b:
            r7 = r64
            if (r2 == 0) goto L_0x0b7b
            if (r7 != 0) goto L_0x0b63
            int r3 = r14.ttl
            if (r3 == 0) goto L_0x0b56
            goto L_0x0b63
        L_0x0b56:
            int r3 = r2.getWidth()
            int r4 = r2.getHeight()
            int r3 = java.lang.Math.max(r3, r4)
            goto L_0x0b65
        L_0x0b63:
            r3 = 90
        L_0x0b65:
            float r4 = (float) r3
            r5 = 90
            if (r3 <= r5) goto L_0x0b6d
            r3 = 80
            goto L_0x0b6f
        L_0x0b6d:
            r3 = 55
        L_0x0b6f:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r4, r4, r3, r7)
            r4 = 0
            r5 = 1
            r6 = 0
            java.lang.String r32 = getKeyForPhotoSize(r15, r3, r4, r5, r6)
            goto L_0x0b80
        L_0x0b7b:
            r5 = 1
            r6 = 0
            r3 = 0
            r32 = 0
        L_0x0b80:
            org.telegram.tgnet.TLRPC$TL_document r4 = new org.telegram.tgnet.TLRPC$TL_document
            r4.<init>()
            byte[] r5 = new byte[r6]
            r4.file_reference = r5
            if (r3 == 0) goto L_0x0b96
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r4.thumbs
            r5.add(r3)
            int r3 = r4.flags
            r5 = 1
            r3 = r3 | r5
            r4.flags = r3
        L_0x0b96:
            r4.mime_type = r12
            org.telegram.messenger.UserConfig r3 = r70.getUserConfig()
            r3.saveConfig(r6)
            if (r7 == 0) goto L_0x0ba7
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r3.<init>()
            goto L_0x0baf
        L_0x0ba7:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r3.<init>()
            r5 = 1
            r3.supports_streaming = r5
        L_0x0baf:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r4.attributes
            r5.add(r3)
            if (r0 == 0) goto L_0x0c0c
            boolean r5 = r0.needConvert()
            if (r5 != 0) goto L_0x0bc0
            boolean r5 = r14.isVideo
            if (r5 != 0) goto L_0x0c0c
        L_0x0bc0:
            boolean r5 = r14.isVideo
            if (r5 == 0) goto L_0x0bd8
            boolean r5 = r0.muted
            if (r5 == 0) goto L_0x0bd8
            java.lang.String r5 = r14.path
            fillVideoAttribute(r5, r3, r0)
            int r5 = r3.w
            r0.originalWidth = r5
            int r5 = r3.h
            r0.originalHeight = r5
            r52 = r7
            goto L_0x0be3
        L_0x0bd8:
            r52 = r7
            long r6 = r0.estimatedDuration
            r34 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 / r34
            int r5 = (int) r6
            r3.duration = r5
        L_0x0be3:
            int r5 = r0.rotationValue
            org.telegram.messenger.MediaController$CropState r6 = r0.cropState
            if (r6 == 0) goto L_0x0bee
            int r7 = r6.transformWidth
            int r6 = r6.transformHeight
            goto L_0x0bf2
        L_0x0bee:
            int r7 = r0.resultWidth
            int r6 = r0.resultHeight
        L_0x0bf2:
            r12 = 90
            if (r5 == r12) goto L_0x0CLASSNAME
            r12 = 270(0x10e, float:3.78E-43)
            if (r5 != r12) goto L_0x0bfb
            goto L_0x0CLASSNAME
        L_0x0bfb:
            r3.w = r7
            r3.h = r6
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r3.w = r6
            r3.h = r7
        L_0x0CLASSNAME:
            long r5 = r0.estimatedSize
            int r3 = (int) r5
            long r5 = (long) r3
            r4.size = r5
            r12 = 0
            goto L_0x0CLASSNAME
        L_0x0c0c:
            r52 = r7
            boolean r5 = r38.exists()
            if (r5 == 0) goto L_0x0c1c
            long r5 = r38.length()
            int r6 = (int) r5
            long r5 = (long) r6
            r4.size = r5
        L_0x0c1c:
            java.lang.String r5 = r14.path
            r12 = 0
            fillVideoAttribute(r5, r3, r12)
        L_0x0CLASSNAME:
            r3 = r2
            r7 = r4
            r4 = r32
            goto L_0x0c2e
        L_0x0CLASSNAME:
            r52 = r64
            r12 = 0
            r3 = r12
            r4 = r3
            r7 = r32
        L_0x0c2e:
            if (r0 == 0) goto L_0x0CLASSNAME
            boolean r2 = r0.muted
            if (r2 == 0) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r7.attributes
            int r2 = r2.size()
            r5 = 0
        L_0x0c3b:
            if (r5 >= r2) goto L_0x0c4c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r7.attributes
            java.lang.Object r6 = r6.get(r5)
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            if (r6 == 0) goto L_0x0CLASSNAME
            r2 = 1
            goto L_0x0c4d
        L_0x0CLASSNAME:
            int r5 = r5 + 1
            goto L_0x0c3b
        L_0x0c4c:
            r2 = 0
        L_0x0c4d:
            if (r2 != 0) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r7.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r5.<init>()
            r2.add(r5)
        L_0x0CLASSNAME:
            if (r0 == 0) goto L_0x0CLASSNAME
            boolean r2 = r0.needConvert()
            if (r2 != 0) goto L_0x0CLASSNAME
            boolean r2 = r14.isVideo
            if (r2 != 0) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "-2147483648_"
            r2.append(r5)
            int r5 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r2.append(r5)
            java.lang.String r5 = ".mp4"
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r5.<init>(r6, r2)
            org.telegram.messenger.SharedConfig.saveConfig()
            java.lang.String r2 = r5.getAbsolutePath()
            r18 = r2
        L_0x0CLASSNAME:
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            if (r1 == 0) goto L_0x0c9b
            r6.put(r9, r1)
        L_0x0c9b:
            if (r8 == 0) goto L_0x0ca2
            java.lang.String r1 = "parentObject"
            r6.put(r1, r8)
        L_0x0ca2:
            if (r31 != 0) goto L_0x0cd5
            if (r69 == 0) goto L_0x0cd5
            int r1 = r16 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r10)
            r9 = r59
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r6.put(r11, r2)
            r2 = 10
            if (r1 == r2) goto L_0x0cc9
            r11 = r63
            int r2 = r11 + -1
            r5 = r61
            if (r5 != r2) goto L_0x0cdd
            goto L_0x0ccd
        L_0x0cc9:
            r5 = r61
            r11 = r63
        L_0x0ccd:
            r2 = r39
            r6.put(r2, r13)
            r28 = r25
            goto L_0x0cdd
        L_0x0cd5:
            r9 = r59
            r5 = r61
            r11 = r63
            r1 = r16
        L_0x0cdd:
            if (r52 != 0) goto L_0x0d32
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r14.masks
            if (r2 == 0) goto L_0x0d32
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0d32
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r7.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers r13 = new org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers
            r13.<init>()
            r2.add(r13)
            org.telegram.tgnet.SerializedData r2 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r13 = r14.masks
            int r13 = r13.size()
            int r13 = r13 * 20
            int r13 = r13 + 4
            r2.<init>((int) r13)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r13 = r14.masks
            int r13 = r13.size()
            r2.writeInt32(r13)
            r13 = 0
        L_0x0d0c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r12 = r14.masks
            int r12 = r12.size()
            if (r13 >= r12) goto L_0x0d22
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r12 = r14.masks
            java.lang.Object r12 = r12.get(r13)
            org.telegram.tgnet.TLRPC$InputDocument r12 = (org.telegram.tgnet.TLRPC$InputDocument) r12
            r12.serializeToStream(r2)
            int r13 = r13 + 1
            goto L_0x0d0c
        L_0x0d22:
            byte[] r12 = r2.toByteArray()
            java.lang.String r12 = org.telegram.messenger.Utilities.bytesToHex(r12)
            java.lang.String r13 = "masks"
            r6.put(r13, r12)
            r2.cleanup()
        L_0x0d32:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda6 r24 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda6
            r2 = r24
            r31 = r5
            r5 = r71
            r12 = r6
            r32 = 0
            r6 = r70
            r13 = r7
            r34 = r52
            r7 = r0
            r36 = r8
            r8 = r13
            r37 = r9
            r9 = r18
            r10 = r12
            r12 = r11
            r11 = r36
            r0 = r1
            r1 = r12
            r35 = 0
            r12 = r66
            r18 = r14
            r14 = r72
            r53 = r1
            r1 = r15
            r15 = r73
            r16 = r18
            r17 = r74
            r18 = r75
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r24)
            r16 = r0
            r4 = r43
            r5 = r44
            r3 = r46
            r2 = r49
            goto L_0x0dcd
        L_0x0d74:
            r18 = r14
            r1 = r15
            r41 = r58
            r37 = r59
            r31 = r61
            r34 = r62
            r53 = r63
            r32 = 0
            r35 = 0
            if (r44 != 0) goto L_0x0da6
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r27 = new java.util.ArrayList
            r27.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r5 = r0
            r8 = r18
            r0 = r27
            goto L_0x0db2
        L_0x0da6:
            r8 = r18
            r0 = r27
            r4 = r43
            r5 = r44
            r3 = r46
            r2 = r49
        L_0x0db2:
            java.lang.String r6 = r8.path
            r5.add(r6)
            java.lang.String r6 = r8.path
            r4.add(r6)
            android.net.Uri r6 = r8.uri
            r3.add(r6)
            java.lang.String r6 = r8.caption
            r2.add(r6)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r8.entities
            r0.add(r6)
            r27 = r0
        L_0x0dcd:
            r0 = r16
        L_0x0dcf:
            int r6 = r31 + 1
            r15 = r1
            r11 = r23
            r24 = r30
            r10 = r33
            r13 = r34
            r30 = r37
            r9 = r41
            r14 = r53
            r12 = 1
            r1 = r65
            goto L_0x019b
        L_0x0de5:
            r49 = r2
            r46 = r3
            r43 = r4
            r44 = r5
            r34 = r13
            r53 = r14
            r1 = r15
            r7 = r28
            r25 = 0
            r32 = 0
            int r2 = (r7 > r25 ? 1 : (r7 == r25 ? 0 : -1))
            r15 = r75
            if (r2 == 0) goto L_0x0e01
            finishGroup(r1, r7, r15)
        L_0x0e01:
            if (r76 == 0) goto L_0x0e06
            r76.releasePermission()
        L_0x0e06:
            if (r44 == 0) goto L_0x0ec8
            boolean r2 = r44.isEmpty()
            if (r2 != 0) goto L_0x0ec8
            r2 = 1
            long[] r14 = new long[r2]
            int r13 = r44.size()
            r7 = r0
            r0 = 0
        L_0x0e17:
            if (r0 >= r13) goto L_0x0ec8
            if (r68 == 0) goto L_0x0e30
            if (r34 != 0) goto L_0x0e30
            r3 = r53
            if (r3 <= r2) goto L_0x0e32
            int r2 = r7 % 10
            if (r2 != 0) goto L_0x0e32
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random
            long r4 = r2.nextLong()
            r14[r32] = r4
            r2 = 1
            r7 = 0
            goto L_0x0e33
        L_0x0e30:
            r3 = r53
        L_0x0e32:
            r2 = 1
        L_0x0e33:
            int r12 = r7 + 1
            r5 = r44
            java.lang.Object r4 = r5.get(r0)
            java.lang.String r4 = (java.lang.String) r4
            r11 = r43
            java.lang.Object r6 = r11.get(r0)
            java.lang.String r6 = (java.lang.String) r6
            r10 = r46
            java.lang.Object r7 = r10.get(r0)
            android.net.Uri r7 = (android.net.Uri) r7
            r9 = r49
            java.lang.Object r8 = r9.get(r0)
            r16 = r8
            java.lang.CharSequence r16 = (java.lang.CharSequence) r16
            r8 = r27
            java.lang.Object r18 = r8.get(r0)
            r21 = r18
            java.util.ArrayList r21 = (java.util.ArrayList) r21
            r2 = 10
            if (r12 == r2) goto L_0x0e6d
            int r2 = r13 + -1
            if (r0 != r2) goto L_0x0e6a
            goto L_0x0e6d
        L_0x0e6a:
            r23 = 0
            goto L_0x0e6f
        L_0x0e6d:
            r23 = 1
        L_0x0e6f:
            r18 = 0
            r24 = r3
            r25 = r5
            r26 = 10
            r27 = 1
            r5 = r1
            r1 = r70
            r2 = r4
            r3 = r6
            r4 = r7
            r6 = r5
            r5 = r22
            r6 = r66
            r28 = r8
            r8 = r72
            r29 = r9
            r9 = r73
            r30 = r10
            r10 = r16
            r31 = r11
            r11 = r21
            r21 = r12
            r12 = r71
            r33 = r13
            r13 = r14
            r35 = r14
            r14 = r23
            r15 = r68
            r16 = r74
            r17 = r75
            int r1 = prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            r2 = r70
            handleError(r1, r2)
            int r0 = r0 + 1
            r15 = r75
            r1 = r2
            r7 = r21
            r53 = r24
            r44 = r25
            r27 = r28
            r49 = r29
            r46 = r30
            r43 = r31
            r13 = r33
            r14 = r35
            r2 = 1
            goto L_0x0e17
        L_0x0ec8:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0ee6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "total send time = "
            r0.append(r1)
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r19
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0ee6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingMedia$89(java.util.ArrayList, long, boolean, boolean, org.telegram.messenger.AccountInstance, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, boolean, int, androidx.core.view.inputmethod.InputContentInfoCompat):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingMedia$84(MediaSendPrepareWorker mediaSendPrepareWorker, AccountInstance accountInstance, SendingMediaInfo sendingMediaInfo, boolean z) {
        mediaSendPrepareWorker.photo = accountInstance.getSendMessagesHelper().generatePhotoSizes(sendingMediaInfo.path, sendingMediaInfo.uri);
        if (z && sendingMediaInfo.canDeleteAfter) {
            new File(sendingMediaInfo.path).delete();
        }
        mediaSendPrepareWorker.sync.countDown();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingMedia$85(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, str, hashMap, false, str2);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str2, (MessageObject.SendAnimationData) null);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingMedia$86(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, boolean z, SendingMediaInfo sendingMediaInfo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, boolean z2, int i) {
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingMedia$87(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        Bitmap bitmap2 = bitmap;
        String str4 = str;
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmap2 == null || str4 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str4, false);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
            return;
        }
        accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, messageObject3, sendingMediaInfo2.caption, sendingMediaInfo2.entities, (TLRPC$ReplyMarkup) null, hashMap, z, i, sendingMediaInfo2.ttl, str3, (MessageObject.SendAnimationData) null);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingMedia$88(Bitmap[] bitmapArr, String[] strArr, MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_photo tLRPC$TL_photo, HashMap hashMap, String str, long j, MessageObject messageObject2, MessageObject messageObject3, SendingMediaInfo sendingMediaInfo, boolean z, int i) {
        SendingMediaInfo sendingMediaInfo2 = sendingMediaInfo;
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0], false);
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
            java.lang.Integer r1 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r1)     // Catch:{ Exception -> 0x0075, all -> 0x0072 }
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

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r3.release();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:18:0x0056 */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x005a A[ExcHandler: all (r0v1 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:10:0x0046] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap createVideoThumbnailAtTime(java.lang.String r19, long r20, int[] r22, boolean r23) {
        /*
            r0 = r19
            r1 = r20
            r3 = r22
            r4 = r23
            if (r4 == 0) goto L_0x0040
            org.telegram.ui.Components.AnimatedFileDrawable r15 = new org.telegram.ui.Components.AnimatedFileDrawable
            java.io.File r6 = new java.io.File
            r6.<init>(r0)
            r7 = 1
            r8 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 1
            r5 = r15
            r18 = r15
            r15 = r16
            r16 = r17
            r5.<init>(r6, r7, r8, r10, r11, r12, r13, r15, r16)
            r5 = r18
            android.graphics.Bitmap r4 = r5.getFrameAtTime(r1, r4)
            r6 = 0
            if (r3 == 0) goto L_0x0036
            int r7 = r5.getOrientation()
            r3[r6] = r7
        L_0x0036:
            r5.recycle()
            if (r4 != 0) goto L_0x005f
            android.graphics.Bitmap r0 = createVideoThumbnailAtTime(r0, r1, r3, r6)
            return r0
        L_0x0040:
            android.media.MediaMetadataRetriever r3 = new android.media.MediaMetadataRetriever
            r3.<init>()
            r4 = 0
            r3.setDataSource(r0)     // Catch:{ Exception -> 0x0056, all -> 0x005a }
            r0 = 1
            android.graphics.Bitmap r0 = r3.getFrameAtTime(r1, r0)     // Catch:{ Exception -> 0x0056, all -> 0x005a }
            if (r0 != 0) goto L_0x0055
            r4 = 3
            android.graphics.Bitmap r0 = r3.getFrameAtTime(r1, r4)     // Catch:{ Exception -> 0x0055, all -> 0x005a }
        L_0x0055:
            r4 = r0
        L_0x0056:
            r3.release()     // Catch:{ RuntimeException -> 0x005f }
            goto L_0x005f
        L_0x005a:
            r0 = move-exception
            r3.release()     // Catch:{ RuntimeException -> 0x005e }
        L_0x005e:
            throw r0
        L_0x005f:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.createVideoThumbnailAtTime(java.lang.String, long, int[], boolean):android.graphics.Bitmap");
    }

    private static VideoEditedInfo createCompressionSettings(String str) {
        int[] iArr = new int[11];
        AnimatedFileDrawable.getVideoInfo(str, iArr);
        boolean z = false;
        if (iArr[0] == 0) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("video hasn't avc1 atom");
            }
            return null;
        }
        int videoBitrate = MediaController.getVideoBitrate(str);
        if (videoBitrate == -1) {
            videoBitrate = iArr[3];
        }
        int i = 4;
        float f = (float) iArr[4];
        int i2 = iArr[6];
        long j = (long) iArr[5];
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
            i = max > 854.0f ? 3 : max > 640.0f ? 2 : 1;
        }
        int round = Math.round(((float) DownloadController.getInstance(UserConfig.selectedAccount).getMaxVideoBitrate()) / (100.0f / ((float) i)));
        if (round > i) {
            round = i;
        }
        if (round != i || Math.max(videoEditedInfo.originalWidth, videoEditedInfo.originalHeight) > 1280) {
            if (round == 1) {
                f2 = 432.0f;
            } else if (round != 2) {
                f2 = round != 3 ? 1280.0f : 848.0f;
            }
            int i6 = videoEditedInfo.originalWidth;
            int i7 = videoEditedInfo.originalHeight;
            float f3 = f2 / (i6 > i7 ? (float) i6 : (float) i7);
            videoEditedInfo.resultWidth = Math.round((((float) i6) * f3) / 2.0f) * 2;
            videoEditedInfo.resultHeight = Math.round((((float) videoEditedInfo.originalHeight) * f3) / 2.0f) * 2;
            z = true;
        }
        int makeVideoBitrate = MediaController.makeVideoBitrate(videoEditedInfo.originalHeight, videoEditedInfo.originalWidth, videoBitrate, videoEditedInfo.resultHeight, videoEditedInfo.resultWidth);
        if (!z) {
            videoEditedInfo.resultWidth = videoEditedInfo.originalWidth;
            videoEditedInfo.resultHeight = videoEditedInfo.originalHeight;
            videoEditedInfo.bitrate = makeVideoBitrate;
        } else {
            videoEditedInfo.bitrate = makeVideoBitrate;
        }
        long j2 = (long) ((int) (((float) j) + (((f / 1000.0f) * ((float) makeVideoBitrate)) / 8.0f)));
        videoEditedInfo.estimatedSize = j2;
        if (j2 == 0) {
            videoEditedInfo.estimatedSize = 1;
        }
        return videoEditedInfo;
    }

    public static void prepareSendingVideo(AccountInstance accountInstance, String str, VideoEditedInfo videoEditedInfo, long j, MessageObject messageObject, MessageObject messageObject2, CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, int i, MessageObject messageObject3, boolean z, int i2, boolean z2) {
        if (str != null && str.length() != 0) {
            SendMessagesHelper$$ExternalSyntheticLambda73 sendMessagesHelper$$ExternalSyntheticLambda73 = r0;
            SendMessagesHelper$$ExternalSyntheticLambda73 sendMessagesHelper$$ExternalSyntheticLambda732 = new SendMessagesHelper$$ExternalSyntheticLambda73(videoEditedInfo, str, j, i, accountInstance, charSequence, messageObject3, messageObject, messageObject2, arrayList, z, i2, z2);
            new Thread(sendMessagesHelper$$ExternalSyntheticLambda73).start();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0295  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02f6  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x0335  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x033b  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0340  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0347  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x024a  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x027f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingVideo$91(org.telegram.messenger.VideoEditedInfo r29, java.lang.String r30, long r31, int r33, org.telegram.messenger.AccountInstance r34, java.lang.CharSequence r35, org.telegram.messenger.MessageObject r36, org.telegram.messenger.MessageObject r37, org.telegram.messenger.MessageObject r38, java.util.ArrayList r39, boolean r40, int r41, boolean r42) {
        /*
            r7 = r30
            if (r29 == 0) goto L_0x0007
            r8 = r29
            goto L_0x000c
        L_0x0007:
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r30)
            r8 = r0
        L_0x000c:
            boolean r9 = org.telegram.messenger.DialogObject.isEncryptedDialog(r31)
            r10 = 0
            r11 = 1
            if (r8 == 0) goto L_0x001a
            boolean r0 = r8.roundVideo
            if (r0 == 0) goto L_0x001a
            r12 = 1
            goto L_0x001b
        L_0x001a:
            r12 = 0
        L_0x001b:
            if (r8 != 0) goto L_0x004b
            java.lang.String r0 = "mp4"
            boolean r0 = r7.endsWith(r0)
            if (r0 != 0) goto L_0x004b
            if (r12 == 0) goto L_0x0028
            goto L_0x004b
        L_0x0028:
            r3 = 0
            r4 = 0
            r12 = 0
            r13 = 0
            r17 = 0
            r0 = r34
            r1 = r30
            r2 = r30
            r5 = r31
            r7 = r37
            r8 = r38
            r9 = r35
            r10 = r39
            r11 = r36
            r14 = r42
            r15 = r40
            r16 = r41
            prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
            goto L_0x036f
        L_0x004b:
            java.io.File r13 = new java.io.File
            r13.<init>(r7)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            long r1 = r13.length()
            r0.append(r1)
            java.lang.String r14 = "_"
            r0.append(r14)
            long r1 = r13.lastModified()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r15 = ""
            r1 = 0
            if (r8 == 0) goto L_0x00c4
            if (r12 != 0) goto L_0x00bd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            long r4 = r8.estimatedDuration
            r3.append(r4)
            r3.append(r14)
            long r4 = r8.startTime
            r3.append(r4)
            r3.append(r14)
            long r4 = r8.endTime
            r3.append(r4)
            boolean r0 = r8.muted
            if (r0 == 0) goto L_0x009b
            java.lang.String r0 = "_m"
            goto L_0x009c
        L_0x009b:
            r0 = r15
        L_0x009c:
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            int r3 = r8.resultWidth
            int r4 = r8.originalWidth
            if (r3 == r4) goto L_0x00bd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r14)
            int r0 = r8.resultWidth
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x00bd:
            long r3 = r8.startTime
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 < 0) goto L_0x00c4
            r1 = r3
        L_0x00c4:
            r5 = r0
            r3 = r1
            r6 = 2
            r2 = 0
            if (r9 != 0) goto L_0x0117
            if (r33 != 0) goto L_0x0117
            if (r8 == 0) goto L_0x00de
            org.telegram.messenger.MediaController$SavedFilterState r0 = r8.filterState
            if (r0 != 0) goto L_0x0117
            java.lang.String r0 = r8.paintPath
            if (r0 != 0) goto L_0x0117
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r8.mediaEntities
            if (r0 != 0) goto L_0x0117
            org.telegram.messenger.MediaController$CropState r0 = r8.cropState
            if (r0 != 0) goto L_0x0117
        L_0x00de:
            org.telegram.messenger.MessagesStorage r0 = r34.getMessagesStorage()
            if (r9 != 0) goto L_0x00e6
            r1 = 2
            goto L_0x00e7
        L_0x00e6:
            r1 = 5
        L_0x00e7:
            java.lang.Object[] r0 = r0.getSentFile(r5, r1)
            if (r0 == 0) goto L_0x0117
            r1 = r0[r10]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r1 == 0) goto L_0x0117
            r1 = r0[r10]
            r16 = r1
            org.telegram.tgnet.TLRPC$TL_document r16 = (org.telegram.tgnet.TLRPC$TL_document) r16
            r0 = r0[r11]
            r17 = r0
            java.lang.String r17 = (java.lang.String) r17
            r18 = 0
            r0 = r34
            r1 = r9
            r2 = r16
            r20 = r3
            r3 = r30
            r4 = r18
            r22 = r5
            r10 = 2
            r5 = r20
            ensureMediaThumbExists(r0, r1, r2, r3, r4, r5)
            r6 = r17
            goto L_0x011e
        L_0x0117:
            r20 = r3
            r22 = r5
            r10 = 2
            r2 = 0
            r6 = 0
        L_0x011e:
            if (r2 != 0) goto L_0x02f6
            r0 = r20
            android.graphics.Bitmap r0 = createVideoThumbnailAtTime(r7, r0)
            if (r0 != 0) goto L_0x012c
            android.graphics.Bitmap r0 = createVideoThumbnail(r7, r11)
        L_0x012c:
            r2 = r0
            r0 = 90
            if (r9 != 0) goto L_0x0137
            if (r33 == 0) goto L_0x0134
            goto L_0x0137
        L_0x0134:
            r1 = 320(0x140, float:4.48E-43)
            goto L_0x0139
        L_0x0137:
            r1 = 90
        L_0x0139:
            float r3 = (float) r1
            if (r1 <= r0) goto L_0x013f
            r1 = 80
            goto L_0x0141
        L_0x013f:
            r1 = 55
        L_0x0141:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r1, r9)
            if (r2 == 0) goto L_0x0242
            if (r1 == 0) goto L_0x0242
            if (r12 == 0) goto L_0x0241
            r3 = 21
            if (r9 == 0) goto L_0x01e4
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createScaledBitmap(r2, r0, r0, r11)
            r24 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x015c
            r25 = 0
            goto L_0x015e
        L_0x015c:
            r25 = 1
        L_0x015e:
            int r26 = r2.getWidth()
            int r27 = r2.getHeight()
            int r28 = r2.getRowBytes()
            r23 = r2
            org.telegram.messenger.Utilities.blurBitmap(r23, r24, r25, r26, r27, r28)
            r24 = 7
            if (r4 >= r3) goto L_0x0176
            r25 = 0
            goto L_0x0178
        L_0x0176:
            r25 = 1
        L_0x0178:
            int r26 = r2.getWidth()
            int r27 = r2.getHeight()
            int r28 = r2.getRowBytes()
            r23 = r2
            org.telegram.messenger.Utilities.blurBitmap(r23, r24, r25, r26, r27, r28)
            r24 = 7
            if (r4 >= r3) goto L_0x0190
            r25 = 0
            goto L_0x0192
        L_0x0190:
            r25 = 1
        L_0x0192:
            int r26 = r2.getWidth()
            int r27 = r2.getHeight()
            int r28 = r2.getRowBytes()
            r23 = r2
            org.telegram.messenger.Utilities.blurBitmap(r23, r24, r25, r26, r27, r28)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            long r4 = r4.volume_id
            r3.append(r4)
            r3.append(r14)
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            int r4 = r4.local_id
            r3.append(r4)
            java.lang.String r4 = "@%d_%d_b2"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object[] r4 = new java.lang.Object[r10]
            int r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r5 = (float) r5
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 / r10
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r10 = 0
            r4[r10] = r5
            int r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r5 = (float) r5
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 / r10
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r11] = r5
            java.lang.String r3 = java.lang.String.format(r3, r4)
            goto L_0x0243
        L_0x01e4:
            r24 = 3
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x01ed
            r25 = 0
            goto L_0x01ef
        L_0x01ed:
            r25 = 1
        L_0x01ef:
            int r26 = r2.getWidth()
            int r27 = r2.getHeight()
            int r28 = r2.getRowBytes()
            r23 = r2
            org.telegram.messenger.Utilities.blurBitmap(r23, r24, r25, r26, r27, r28)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            long r4 = r4.volume_id
            r3.append(r4)
            r3.append(r14)
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            int r4 = r4.local_id
            r3.append(r4)
            java.lang.String r4 = "@%d_%d_b"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object[] r4 = new java.lang.Object[r10]
            int r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r5 = (float) r5
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 / r10
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r10 = 0
            r4[r10] = r5
            int r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r5 = (float) r5
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r5 = r5 / r10
            int r5 = (int) r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r4[r11] = r5
            java.lang.String r3 = java.lang.String.format(r3, r4)
            goto L_0x0243
        L_0x0241:
            r2 = 0
        L_0x0242:
            r3 = 0
        L_0x0243:
            org.telegram.tgnet.TLRPC$TL_document r4 = new org.telegram.tgnet.TLRPC$TL_document
            r4.<init>()
            if (r1 == 0) goto L_0x0254
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r4.thumbs
            r5.add(r1)
            int r1 = r4.flags
            r1 = r1 | r11
            r4.flags = r1
        L_0x0254:
            r1 = 0
            byte[] r5 = new byte[r1]
            r4.file_reference = r5
            java.lang.String r5 = "video/mp4"
            r4.mime_type = r5
            org.telegram.messenger.UserConfig r5 = r34.getUserConfig()
            r5.saveConfig(r1)
            if (r9 == 0) goto L_0x027f
            int r1 = org.telegram.messenger.DialogObject.getEncryptedChatId(r31)
            org.telegram.messenger.MessagesController r5 = r34.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r5.getEncryptedChat(r1)
            if (r1 != 0) goto L_0x0279
            return
        L_0x0279:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            goto L_0x0286
        L_0x027f:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            r1.supports_streaming = r11
        L_0x0286:
            r1.round_message = r12
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r4.attributes
            r5.add(r1)
            if (r8 == 0) goto L_0x02e0
            boolean r5 = r8.needConvert()
            if (r5 == 0) goto L_0x02e0
            boolean r5 = r8.muted
            if (r5 == 0) goto L_0x02af
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r4.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r9 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r9.<init>()
            r5.add(r9)
            fillVideoAttribute(r7, r1, r8)
            int r5 = r1.w
            r8.originalWidth = r5
            int r5 = r1.h
            r8.originalHeight = r5
            goto L_0x02b7
        L_0x02af:
            long r9 = r8.estimatedDuration
            r11 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 / r11
            int r5 = (int) r9
            r1.duration = r5
        L_0x02b7:
            int r5 = r8.rotationValue
            org.telegram.messenger.MediaController$CropState r9 = r8.cropState
            if (r9 == 0) goto L_0x02c5
            int r10 = r9.transformWidth
            int r11 = r9.transformHeight
            int r9 = r9.transformRotation
            int r5 = r5 + r9
            goto L_0x02c9
        L_0x02c5:
            int r10 = r8.resultWidth
            int r11 = r8.resultHeight
        L_0x02c9:
            if (r5 == r0) goto L_0x02d5
            r0 = 270(0x10e, float:3.78E-43)
            if (r5 != r0) goto L_0x02d0
            goto L_0x02d5
        L_0x02d0:
            r1.w = r10
            r1.h = r11
            goto L_0x02d9
        L_0x02d5:
            r1.w = r11
            r1.h = r10
        L_0x02d9:
            long r0 = r8.estimatedSize
            int r1 = (int) r0
            long r0 = (long) r1
            r4.size = r0
            goto L_0x02f2
        L_0x02e0:
            boolean r0 = r13.exists()
            if (r0 == 0) goto L_0x02ee
            long r9 = r13.length()
            int r0 = (int) r9
            long r9 = (long) r0
            r4.size = r9
        L_0x02ee:
            r0 = 0
            fillVideoAttribute(r7, r1, r0)
        L_0x02f2:
            r1 = r2
            r2 = r3
            r9 = r4
            goto L_0x02fa
        L_0x02f6:
            r0 = 0
            r1 = r0
            r9 = r2
            r2 = r1
        L_0x02fa:
            if (r8 == 0) goto L_0x032e
            boolean r0 = r8.needConvert()
            if (r0 == 0) goto L_0x032e
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
            r7 = r0
        L_0x032e:
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            if (r35 == 0) goto L_0x033b
            java.lang.String r0 = r35.toString()
            r14 = r0
            goto L_0x033c
        L_0x033b:
            r14 = r15
        L_0x033c:
            r0 = r22
            if (r0 == 0) goto L_0x0345
            java.lang.String r3 = "originalPath"
            r10.put(r3, r0)
        L_0x0345:
            if (r6 == 0) goto L_0x034c
            java.lang.String r0 = "parentObject"
            r10.put(r0, r6)
        L_0x034c:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda5 r19 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda5
            r0 = r19
            r3 = r36
            r4 = r34
            r5 = r8
            r17 = r6
            r6 = r9
            r8 = r10
            r9 = r17
            r10 = r31
            r12 = r37
            r13 = r38
            r15 = r39
            r16 = r40
            r17 = r41
            r18 = r33
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r19)
        L_0x036f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$91(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, int, org.telegram.messenger.AccountInstance, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, int, boolean):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingVideo$90(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, MessageObject messageObject3, String str4, ArrayList arrayList, boolean z, int i, int i2) {
        Bitmap bitmap2 = bitmap;
        String str5 = str;
        if (!(bitmap2 == null || str5 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str5, false);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, messageObject3, str4, arrayList, (TLRPC$ReplyMarkup) null, hashMap, z, i, i2, str3, (MessageObject.SendAnimationData) null);
        }
    }
}
