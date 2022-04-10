package org.telegram.messenger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.io.File;
import java.io.FileInputStream;
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
    private static volatile SendMessagesHelper[] Instance = new SendMessagesHelper[3];
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda21(this));
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
                                            messageObject8.messageOwner.media.document.size = (int) longValue2;
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
                                i10++;
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
                            SendMessagesHelper$$ExternalSyntheticLambda24 sendMessagesHelper$$ExternalSyntheticLambda24 = r0;
                            SendMessagesHelper$$ExternalSyntheticLambda24 sendMessagesHelper$$ExternalSyntheticLambda242 = new SendMessagesHelper$$ExternalSyntheticLambda24(this, file, messageObject, delayedMessage10, str10);
                            dispatchQueue.postRunnable(sendMessagesHelper$$ExternalSyntheticLambda24);
                        } else if (c == 1) {
                            Utilities.globalQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda38(this, delayedMessage10, new File(FileLoader.getDirectory(4), Utilities.MD5(str10) + ".gif"), messageObject));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda61(this, generatePhotoSizes(file.toString(), (Uri) null), messageObject, file, delayedMessage, str));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda39(this, delayedMessage, file, document, messageObject));
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
                mediaSendQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda43(this, tLRPC$TL_document_layer82, j, messageObject, messageObject2, z, i, obj, sendAnimationData));
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
        ensureMediaThumbExists(false, tLRPC$Document, file.getAbsolutePath(), (Uri) null, 0);
        strArr[0] = getKeyForPhotoSize(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 320), bitmapArr, true, true);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda71(this, bitmapArr, strArr, tLRPC$Document, j, messageObject, messageObject2, z, i, obj, sendAnimationData));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendSticker$5(Bitmap[] bitmapArr, String[] strArr, TLRPC$Document tLRPC$Document, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, Object obj, MessageObject.SendAnimationData sendAnimationData) {
        if (!(bitmapArr[0] == null || strArr[0] == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0], false);
        }
        sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, (String) null, j, messageObject, messageObject2, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj, sendAnimationData);
    }

    /* JADX WARNING: Removed duplicated region for block: B:108:0x0275  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0350  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x04ec  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x04f6  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x050f  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0569  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x057b  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0598  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x05bf  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x05c1  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x061b  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x0622  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0632  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0634  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x064f  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x067d  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0683  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0685  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x068d  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x06c9  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0704  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x0719  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x071c  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x0729  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x072b  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x074b  */
    /* JADX WARNING: Removed duplicated region for block: B:302:0x0771  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0781  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x07ac  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x07d8  */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x07da  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x07e7  */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x07e9  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x0835  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x01eb  */
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
            if (r15 == 0) goto L_0x08f7
            boolean r0 = r53.isEmpty()
            if (r0 == 0) goto L_0x0017
            goto L_0x08f7
        L_0x0017:
            org.telegram.messenger.UserConfig r0 = r52.getUserConfig()
            long r6 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r54)
            if (r0 != 0) goto L_0x08d8
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
            if (r0 >= r1) goto L_0x08d3
            java.lang.Object r1 = r15.get(r0)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r34 = r1.getId()
            if (r34 <= 0) goto L_0x0858
            boolean r34 = r1.needDrawBluredPreview()
            if (r34 == 0) goto L_0x0121
            goto L_0x0858
        L_0x0121:
            r34 = r0
            if (r22 != 0) goto L_0x018c
            boolean r36 = r1.isSticker()
            if (r36 != 0) goto L_0x013d
            boolean r36 = r1.isAnimatedSticker()
            if (r36 != 0) goto L_0x013d
            boolean r36 = r1.isGif()
            if (r36 != 0) goto L_0x013d
            boolean r36 = r1.isGame()
            if (r36 == 0) goto L_0x018c
        L_0x013d:
            if (r32 != 0) goto L_0x016a
            r1 = 8
            boolean r1 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r1)
            if (r1 == 0) goto L_0x0149
            r0 = 4
            goto L_0x014a
        L_0x0149:
            r0 = 1
        L_0x014a:
            r32 = r0
            r41 = r2
            r19 = r4
            r39 = r5
            r35 = r6
            r43 = r8
            r44 = r28
            r20 = r30
            r18 = 1
            r38 = 0
            r30 = r14
            r28 = r27
            r14 = r34
            r34 = r33
            r33 = 0
            goto L_0x08b3
        L_0x016a:
            r41 = r2
            r19 = r4
            r39 = r5
            r35 = r6
            r43 = r8
            r40 = r9
            r44 = r28
            r37 = r29
            r20 = r30
            r18 = 1
            r38 = 0
            r30 = r14
            r28 = r27
            r14 = r34
            r34 = r33
            r33 = 0
            goto L_0x08af
        L_0x018c:
            if (r24 != 0) goto L_0x01a7
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r15 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r15 != 0) goto L_0x019a
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L_0x01a7
        L_0x019a:
            if (r32 != 0) goto L_0x016a
            r0 = 7
            boolean r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r0)
            if (r0 == 0) goto L_0x01a5
            r0 = 5
            goto L_0x014a
        L_0x01a5:
            r0 = 2
            goto L_0x014a
        L_0x01a7:
            if (r26 != 0) goto L_0x01bf
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x01bf
            if (r32 != 0) goto L_0x016a
            r0 = 10
            boolean r0 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r8, r0)
            if (r0 == 0) goto L_0x01bd
            r0 = 6
            goto L_0x014a
        L_0x01bd:
            r0 = 3
            goto L_0x014a
        L_0x01bf:
            org.telegram.tgnet.TLRPC$TL_message r0 = new org.telegram.tgnet.TLRPC$TL_message
            r0.<init>()
            if (r11 != 0) goto L_0x0357
            long r37 = r1.getDialogId()
            int r15 = (r37 > r6 ? 1 : (r37 == r6 ? 0 : -1))
            if (r15 != 0) goto L_0x01e2
            boolean r15 = r1.isFromUser()
            if (r15 == 0) goto L_0x01e2
            org.telegram.tgnet.TLRPC$Message r15 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r15 = r15.from_id
            r37 = r14
            long r14 = r15.user_id
            int r38 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
            if (r38 != 0) goto L_0x01e4
            r14 = 1
            goto L_0x01e5
        L_0x01e2:
            r37 = r14
        L_0x01e4:
            r14 = 0
        L_0x01e5:
            boolean r15 = r1.isForwarded()
            if (r15 == 0) goto L_0x0275
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r14 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r14.<init>()
            r0.fwd_from = r14
            org.telegram.tgnet.TLRPC$Message r15 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r15 = r15.fwd_from
            int r11 = r15.flags
            r20 = 1
            r11 = r11 & 1
            if (r11 == 0) goto L_0x0208
            int r11 = r14.flags
            r11 = r11 | 1
            r14.flags = r11
            org.telegram.tgnet.TLRPC$Peer r11 = r15.from_id
            r14.from_id = r11
        L_0x0208:
            int r11 = r15.flags
            r11 = r11 & 32
            if (r11 == 0) goto L_0x0218
            int r11 = r14.flags
            r11 = r11 | 32
            r14.flags = r11
            java.lang.String r11 = r15.from_name
            r14.from_name = r11
        L_0x0218:
            int r11 = r15.flags
            r36 = 4
            r11 = r11 & 4
            if (r11 == 0) goto L_0x022a
            int r11 = r14.flags
            r11 = r11 | 4
            r14.flags = r11
            int r11 = r15.channel_post
            r14.channel_post = r11
        L_0x022a:
            int r11 = r15.flags
            r35 = 8
            r11 = r11 & 8
            if (r11 == 0) goto L_0x023c
            int r11 = r14.flags
            r11 = r11 | 8
            r14.flags = r11
            java.lang.String r11 = r15.post_author
            r14.post_author = r11
        L_0x023c:
            int r11 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r11 == 0) goto L_0x0242
            if (r23 == 0) goto L_0x0266
        L_0x0242:
            int r11 = r15.flags
            r11 = r11 & 16
            if (r11 == 0) goto L_0x0266
            long r14 = r1.getDialogId()
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r14)
            if (r11 != 0) goto L_0x0266
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
        L_0x0266:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r11 = r0.fwd_from
            org.telegram.tgnet.TLRPC$Message r14 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            int r14 = r14.date
            r11.date = r14
            r11 = 4
            r0.flags = r11
            goto L_0x0326
        L_0x0275:
            r11 = 4
            if (r14 != 0) goto L_0x0326
            long r14 = r1.getFromChatId()
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r11 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r11.<init>()
            r0.fwd_from = r11
            r38 = r9
            int r9 = r1.getId()
            r11.channel_post = r9
            org.telegram.tgnet.TLRPC$MessageFwdHeader r9 = r0.fwd_from
            int r11 = r9.flags
            r36 = 4
            r11 = r11 | 4
            r9.flags = r11
            boolean r9 = r1.isFromUser()
            if (r9 == 0) goto L_0x02b2
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
            goto L_0x02e2
        L_0x02b2:
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
            if (r2 == 0) goto L_0x02e2
            int r2 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x02e2
            org.telegram.tgnet.TLRPC$Peer r2 = r5.from_id
            if (r2 == 0) goto L_0x02e0
            r8 = r2
        L_0x02e0:
            r9.from_id = r8
        L_0x02e2:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.post_author
            if (r2 == 0) goto L_0x02e9
            goto L_0x031c
        L_0x02e9:
            boolean r2 = r1.isOutOwner()
            if (r2 != 0) goto L_0x031c
            int r2 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r2 <= 0) goto L_0x031c
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            boolean r2 = r2.post
            if (r2 == 0) goto L_0x031c
            org.telegram.messenger.MessagesController r2 = r52.getMessagesController()
            java.lang.Long r3 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
            if (r2 == 0) goto L_0x031c
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
        L_0x031c:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            int r2 = r2.date
            r0.date = r2
            r2 = 4
            r0.flags = r2
            goto L_0x032e
        L_0x0326:
            r41 = r2
            r39 = r5
            r40 = r8
            r38 = r9
        L_0x032e:
            int r2 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x0361
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r0.fwd_from
            if (r2 == 0) goto L_0x0361
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
            if (r2 != 0) goto L_0x0361
            long r8 = r1.getDialogId()
            r3.user_id = r8
            goto L_0x0361
        L_0x0357:
            r41 = r2
            r39 = r5
            r40 = r8
            r38 = r9
            r37 = r14
        L_0x0361:
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
            if (r2 != 0) goto L_0x03b3
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r2.restriction_reason
            r0.restriction_reason = r2
            int r2 = r0.flags
            r3 = 4194304(0x400000, float:5.877472E-39)
            r2 = r2 | r3
            r0.flags = r2
        L_0x03b3:
            if (r25 != 0) goto L_0x03c5
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r2 == 0) goto L_0x03c5
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r2.<init>()
            r0.media = r2
            goto L_0x03cb
        L_0x03c5:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            r0.media = r2
        L_0x03cb:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r0.media
            if (r2 == 0) goto L_0x03d5
            int r2 = r0.flags
            r2 = r2 | 512(0x200, float:7.175E-43)
            r0.flags = r2
        L_0x03d5:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            long r2 = r2.via_bot_id
            int r8 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r8 == 0) goto L_0x03e5
            r0.via_bot_id = r2
            int r2 = r0.flags
            r2 = r2 | 2048(0x800, float:2.87E-42)
            r0.flags = r2
        L_0x03e5:
            int r2 = (r41 > r16 ? 1 : (r41 == r16 ? 0 : -1))
            if (r2 == 0) goto L_0x0404
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
            goto L_0x0406
        L_0x0404:
            r8 = r41
        L_0x0406:
            if (r10 == 0) goto L_0x040c
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r0.media
            if (r2 != 0) goto L_0x0412
        L_0x040c:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r2 = r2.message
            r0.message = r2
        L_0x0412:
            java.lang.String r2 = r0.message
            if (r2 != 0) goto L_0x0418
            r0.message = r5
        L_0x0418:
            int r2 = r1.getId()
            r0.fwd_msg_id = r2
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.lang.String r3 = r2.attachPath
            r0.attachPath = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r2.entities
            r0.entities = r3
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            if (r2 == 0) goto L_0x04e1
            org.telegram.tgnet.TLRPC$TL_replyInlineMarkup r2 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            r2.<init>()
            r0.reply_markup = r2
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows
            int r2 = r2.size()
            r3 = 0
            r11 = 0
        L_0x0441:
            if (r3 >= r2) goto L_0x04c8
            org.telegram.tgnet.TLRPC$Message r14 = r1.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r14 = r14.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r14 = r14.rows
            java.lang.Object r14 = r14.get(r3)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r14 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r15 = r14.buttons
            int r15 = r15.size()
            r35 = r2
            r2 = 0
            r36 = 0
        L_0x045a:
            r41 = r8
            if (r2 >= r15) goto L_0x04bb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r8 = r14.buttons
            java.lang.Object r8 = r8.get(r2)
            org.telegram.tgnet.TLRPC$KeyboardButton r8 = (org.telegram.tgnet.TLRPC$KeyboardButton) r8
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r43 = r11
            if (r9 != 0) goto L_0x047b
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl
            if (r11 != 0) goto L_0x047b
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline
            if (r11 != 0) goto L_0x047b
            boolean r11 = r8 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r11 == 0) goto L_0x0479
            goto L_0x047b
        L_0x0479:
            r11 = 1
            goto L_0x04bd
        L_0x047b:
            if (r9 == 0) goto L_0x049c
            org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r9.<init>()
            int r11 = r8.flags
            r9.flags = r11
            java.lang.String r11 = r8.fwd_text
            if (r11 == 0) goto L_0x048f
            r9.fwd_text = r11
            r9.text = r11
            goto L_0x0493
        L_0x048f:
            java.lang.String r11 = r8.text
            r9.text = r11
        L_0x0493:
            java.lang.String r11 = r8.url
            r9.url = r11
            int r8 = r8.button_id
            r9.button_id = r8
            r8 = r9
        L_0x049c:
            if (r36 != 0) goto L_0x04ab
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow
            r9.<init>()
            org.telegram.tgnet.TLRPC$ReplyMarkup r11 = r0.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r11 = r11.rows
            r11.add(r9)
            goto L_0x04ad
        L_0x04ab:
            r9 = r36
        L_0x04ad:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r11 = r9.buttons
            r11.add(r8)
            int r2 = r2 + 1
            r36 = r9
            r8 = r41
            r11 = r43
            goto L_0x045a
        L_0x04bb:
            r43 = r11
        L_0x04bd:
            if (r11 == 0) goto L_0x04c0
            goto L_0x04cc
        L_0x04c0:
            int r3 = r3 + 1
            r2 = r35
            r8 = r41
            goto L_0x0441
        L_0x04c8:
            r41 = r8
            r43 = r11
        L_0x04cc:
            if (r11 != 0) goto L_0x04d5
            int r2 = r0.flags
            r2 = r2 | 64
            r0.flags = r2
            goto L_0x04e3
        L_0x04d5:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            r8 = 0
            r2.reply_markup = r8
            int r2 = r0.flags
            r2 = r2 & -65
            r0.flags = r2
            goto L_0x04e4
        L_0x04e1:
            r41 = r8
        L_0x04e3:
            r8 = 0
        L_0x04e4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r0.entities
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x04f2
            int r2 = r0.flags
            r2 = r2 | 128(0x80, float:1.794E-43)
            r0.flags = r2
        L_0x04f2:
            java.lang.String r2 = r0.attachPath
            if (r2 != 0) goto L_0x04f8
            r0.attachPath = r5
        L_0x04f8:
            org.telegram.messenger.UserConfig r2 = r52.getUserConfig()
            int r2 = r2.getNewMessageId()
            r0.id = r2
            r0.local_id = r2
            r2 = 1
            r0.out = r2
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            long r2 = r2.grouped_id
            int r5 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r5 == 0) goto L_0x0535
            java.lang.Object r2 = r4.get(r2)
            java.lang.Long r2 = (java.lang.Long) r2
            if (r2 != 0) goto L_0x0528
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random
            long r2 = r2.nextLong()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            long r14 = r3.grouped_id
            r4.put(r14, r2)
        L_0x0528:
            long r2 = r2.longValue()
            r0.grouped_id = r2
            int r2 = r0.flags
            r3 = 131072(0x20000, float:1.83671E-40)
            r2 = r2 | r3
            r0.flags = r2
        L_0x0535:
            r5 = r27
            long r2 = r5.channel_id
            int r9 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r9 == 0) goto L_0x0555
            if (r23 == 0) goto L_0x0555
            if (r21 == 0) goto L_0x054b
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r0.from_id = r2
            r2.user_id = r6
            goto L_0x054d
        L_0x054b:
            r0.from_id = r5
        L_0x054d:
            r2 = 1
            r0.post = r2
            r11 = r39
            r9 = r40
            goto L_0x0592
        L_0x0555:
            r2 = 1
            org.telegram.messenger.MessagesController r3 = r52.getMessagesController()
            long r14 = -r12
            org.telegram.tgnet.TLRPC$ChatFull r3 = r3.getChatFull(r14)
            r9 = r40
            long r14 = org.telegram.messenger.ChatObject.getSendAsPeerId(r9, r3, r2)
            int r2 = (r14 > r6 ? 1 : (r14 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x057b
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r0.from_id = r2
            r2.user_id = r6
            int r2 = r0.flags
            r2 = r2 | 256(0x100, float:3.59E-43)
            r0.flags = r2
            r11 = r39
            goto L_0x0592
        L_0x057b:
            org.telegram.messenger.MessagesController r2 = r52.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r2 = r2.getPeer(r14)
            r0.from_id = r2
            r11 = r39
            if (r39 == 0) goto L_0x0592
            r0.post_author = r11
            int r2 = r0.flags
            r3 = 65536(0x10000, float:9.18355E-41)
            r2 = r2 | r3
            r0.flags = r2
        L_0x0592:
            long r2 = r0.random_id
            int r14 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x059e
            long r2 = r52.getNextRandomId()
            r0.random_id = r2
        L_0x059e:
            long r2 = r0.random_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r14 = r38
            r14.add(r2)
            long r2 = r0.random_id
            r15 = r37
            r15.put(r2, r0)
            int r2 = r0.fwd_msg_id
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r3 = r33
            r3.add(r2)
            r2 = r59
            if (r2 == 0) goto L_0x05c1
            r8 = r2
            goto L_0x05cb
        L_0x05c1:
            org.telegram.tgnet.ConnectionsManager r19 = r52.getConnectionsManager()
            int r19 = r19.getCurrentTime()
            r8 = r19
        L_0x05cb:
            r0.date = r8
            r19 = r4
            r8 = r30
            boolean r4 = r8 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r4 == 0) goto L_0x05e5
            if (r23 == 0) goto L_0x05e5
            r35 = r6
            if (r2 != 0) goto L_0x05fe
            r6 = 1
            r0.views = r6
            int r6 = r0.flags
            r6 = r6 | 1024(0x400, float:1.435E-42)
            r0.flags = r6
            goto L_0x05fe
        L_0x05e5:
            r35 = r6
            org.telegram.tgnet.TLRPC$Message r6 = r1.messageOwner
            int r7 = r6.flags
            r7 = r7 & 1024(0x400, float:1.435E-42)
            if (r7 == 0) goto L_0x05fb
            if (r2 != 0) goto L_0x05fb
            int r6 = r6.views
            r0.views = r6
            int r6 = r0.flags
            r6 = r6 | 1024(0x400, float:1.435E-42)
            r0.flags = r6
        L_0x05fb:
            r6 = 1
            r0.unread = r6
        L_0x05fe:
            r0.dialog_id = r12
            r0.peer_id = r5
            boolean r6 = org.telegram.messenger.MessageObject.isVoiceMessage(r0)
            if (r6 != 0) goto L_0x0611
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r0)
            if (r6 == 0) goto L_0x060f
            goto L_0x0611
        L_0x060f:
            r4 = 1
            goto L_0x0625
        L_0x0611:
            if (r4 == 0) goto L_0x0622
            long r6 = r1.getChannelId()
            int r4 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x0622
            boolean r4 = r1.isContentUnread()
            r0.media_unread = r4
            goto L_0x060f
        L_0x0622:
            r4 = 1
            r0.media_unread = r4
        L_0x0625:
            org.telegram.messenger.MessageObject r6 = new org.telegram.messenger.MessageObject
            r30 = r15
            r15 = r52
            int r7 = r15.currentAccount
            r6.<init>(r7, r0, r4, r4)
            if (r2 == 0) goto L_0x0634
            r7 = 1
            goto L_0x0635
        L_0x0634:
            r7 = 0
        L_0x0635:
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
            if (r5 == 0) goto L_0x067d
            r5 = 0
        L_0x0650:
            int r9 = r53.size()
            if (r5 >= r9) goto L_0x0678
            r9 = r53
            java.lang.Object r29 = r9.get(r5)
            org.telegram.messenger.MessageObject r29 = (org.telegram.messenger.MessageObject) r29
            r39 = r11
            int r11 = r29.getId()
            org.telegram.messenger.MessageObject r10 = r1.replyMessageObject
            int r10 = r10.getId()
            if (r11 != r10) goto L_0x0671
            org.telegram.messenger.MessageObject r5 = r1.replyMessageObject
            r6.replyMessageObject = r5
            goto L_0x0681
        L_0x0671:
            int r5 = r5 + 1
            r10 = r57
            r11 = r39
            goto L_0x0650
        L_0x0678:
            r9 = r53
            r39 = r11
            goto L_0x0681
        L_0x067d:
            r39 = r11
            r9 = r53
        L_0x0681:
            if (r2 == 0) goto L_0x0685
            r5 = 1
            goto L_0x0686
        L_0x0685:
            r5 = 0
        L_0x0686:
            r15.putToSendingMessages(r0, r5)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x06c1
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
        L_0x06c1:
            int r0 = r7.size()
            r5 = 100
            if (r0 == r5) goto L_0x0704
            int r0 = r53.size()
            r5 = 1
            int r0 = r0 - r5
            r6 = r34
            if (r6 == r0) goto L_0x0706
            int r0 = r53.size()
            int r0 = r0 - r5
            if (r6 == r0) goto L_0x06ef
            int r0 = r6 + 1
            java.lang.Object r0 = r9.get(r0)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r10 = r0.getDialogId()
            long r33 = r1.getDialogId()
            int r0 = (r10 > r33 ? 1 : (r10 == r33 ? 0 : -1))
            if (r0 == 0) goto L_0x06ef
            goto L_0x0706
        L_0x06ef:
            r34 = r3
            r37 = r4
            r44 = r7
            r20 = r8
            r43 = r40
            r18 = 1
            r33 = 0
            r38 = 0
            r40 = r14
            r14 = r6
            goto L_0x08af
        L_0x0704:
            r6 = r34
        L_0x0706:
            org.telegram.messenger.MessagesStorage r43 = r52.getMessagesStorage()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r7)
            r45 = 0
            r46 = 1
            r47 = 0
            r48 = 0
            if (r2 == 0) goto L_0x071c
            r49 = 1
            goto L_0x071e
        L_0x071c:
            r49 = 0
        L_0x071e:
            r44 = r0
            r43.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r44, (boolean) r45, (boolean) r46, (boolean) r47, (int) r48, (boolean) r49)
            org.telegram.messenger.MessagesController r0 = r52.getMessagesController()
            if (r2 == 0) goto L_0x072b
            r5 = 1
            goto L_0x072c
        L_0x072b:
            r5 = 0
        L_0x072c:
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
            if (r58 == 0) goto L_0x076c
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
            if (r0 == 0) goto L_0x076a
            goto L_0x076c
        L_0x076a:
            r0 = 0
            goto L_0x076d
        L_0x076c:
            r0 = 1
        L_0x076d:
            r11.silent = r0
            if (r2 == 0) goto L_0x0779
            r11.schedule_date = r2
            int r0 = r11.flags
            r0 = r0 | 1024(0x400, float:1.435E-42)
            r11.flags = r0
        L_0x0779:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r0 == 0) goto L_0x07ac
            org.telegram.messenger.MessagesController r0 = r52.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            r34 = r6
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
            if (r0 == 0) goto L_0x07b6
            long r6 = r0.access_hash
            r5.access_hash = r6
            goto L_0x07b6
        L_0x07ac:
            r34 = r6
            r10 = r7
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r0.<init>()
            r11.from_peer = r0
        L_0x07b6:
            r11.random_id = r14
            r11.id = r3
            r7 = r56
            r11.drop_author = r7
            r6 = r57
            r11.drop_media_captions = r6
            int r0 = r53.size()
            r5 = 1
            if (r0 != r5) goto L_0x07da
            r0 = 0
            java.lang.Object r18 = r9.get(r0)
            r0 = r18
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0.with_my_score
            if (r0 == 0) goto L_0x07da
            r0 = 1
            goto L_0x07db
        L_0x07da:
            r0 = 0
        L_0x07db:
            r11.with_my_score = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r4)
            r5 = 2147483646(0x7ffffffe, float:NaN)
            if (r2 != r5) goto L_0x07e9
            r5 = 1
            goto L_0x07ea
        L_0x07e9:
            r5 = 0
        L_0x07ea:
            org.telegram.tgnet.ConnectionsManager r13 = r52.getConnectionsManager()
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda79 r12 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda79
            r29 = r0
            r20 = r8
            r8 = r34
            r33 = 0
            r0 = r12
            r34 = r3
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
            if (r14 == r0) goto L_0x08af
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
            r34 = r3
            r30 = r4
            goto L_0x08b3
        L_0x0858:
            r41 = r2
            r19 = r4
            r39 = r5
            r35 = r6
            r43 = r8
            r40 = r9
            r44 = r28
            r37 = r29
            r20 = r30
            r34 = r33
            r18 = 1
            r33 = 0
            r38 = 0
            r30 = r14
            r28 = r27
            r14 = r0
            int r0 = r1.type
            if (r0 != 0) goto L_0x08af
            java.lang.CharSequence r0 = r1.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08af
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x088d
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            r6 = r0
            goto L_0x088f
        L_0x088d:
            r6 = r38
        L_0x088f:
            java.lang.CharSequence r0 = r1.messageText
            java.lang.String r2 = r0.toString()
            r4 = 0
            r5 = 0
            if (r6 == 0) goto L_0x089b
            r7 = 1
            goto L_0x089c
        L_0x089b:
            r7 = 0
        L_0x089c:
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
        L_0x08af:
            r29 = r37
            r9 = r40
        L_0x08b3:
            int r0 = r14 + 1
            r15 = r53
            r12 = r54
            r11 = r56
            r10 = r57
            r4 = r19
            r27 = r28
            r14 = r30
            r33 = r34
            r6 = r35
            r5 = r39
            r2 = r41
            r8 = r43
            r28 = r44
            r30 = r20
            goto L_0x0107
        L_0x08d3:
            r0 = r52
            r8 = r32
            goto L_0x08f6
        L_0x08d8:
            r33 = 0
            r8 = 0
        L_0x08db:
            int r0 = r53.size()
            if (r8 >= r0) goto L_0x08f3
            r0 = r53
            java.lang.Object r1 = r0.get(r8)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r0 = r52
            r2 = r54
            r0.processForwardFromMyName(r1, r2)
            int r8 = r8 + 1
            goto L_0x08db
        L_0x08f3:
            r0 = r52
            r8 = 0
        L_0x08f6:
            return r8
        L_0x08f7:
            r0 = r14
            r33 = 0
            return r33
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda22 r8 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda22
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda51 r6 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda51
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda58 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda58
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda44 r4 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda44
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
        getMessagesStorage().getStorageQueue().postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda33(this, arrayList, messageObject, tLRPC$Message, i, i2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMessage$8(ArrayList arrayList, MessageObject messageObject, TLRPC$Message tLRPC$Message, int i, int i2) {
        getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList, true, false, false, 0, false);
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda36(this, messageObject, tLRPC$Message, i, i2));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda50(this, tLRPC$Message, j, i, tLRPC$Message2, i3, i2));
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
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0257 A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0263 A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x026b  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x027b  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x027f  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x004e A[SYNTHETIC, Splitter:B:19:0x004e] */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0480 A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x048c A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x049e A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x04ea A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x04ef A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x0500 A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00a7 A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0140 A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x015d A[Catch:{ Exception -> 0x0577 }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0184 A[Catch:{ Exception -> 0x0577 }] */
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
            long r8 = r25.getDialogId()     // Catch:{ Exception -> 0x0577 }
            boolean r10 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)     // Catch:{ Exception -> 0x0577 }
            r13 = 1
            if (r10 == 0) goto L_0x0049
            int r10 = org.telegram.messenger.DialogObject.getEncryptedChatId(r8)     // Catch:{ Exception -> 0x0577 }
            org.telegram.messenger.MessagesController r14 = r24.getMessagesController()     // Catch:{ Exception -> 0x0577 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = r14.getEncryptedChat(r10)     // Catch:{ Exception -> 0x0577 }
            if (r10 == 0) goto L_0x0047
            int r10 = r10.layer     // Catch:{ Exception -> 0x0577 }
            int r10 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r10)     // Catch:{ Exception -> 0x0577 }
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
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media     // Catch:{ Exception -> 0x0577 }
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x0577 }
            if (r5 != 0) goto L_0x0084
            if (r2 == 0) goto L_0x0084
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x0577 }
            if (r5 == 0) goto L_0x005d
            goto L_0x0084
        L_0x005d:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0577 }
            if (r5 == 0) goto L_0x0069
            org.telegram.tgnet.TLRPC$Photo r0 = r2.photo     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0     // Catch:{ Exception -> 0x0577 }
            r5 = r27
            r2 = 2
            goto L_0x0087
        L_0x0069:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0577 }
            if (r5 == 0) goto L_0x0080
            org.telegram.tgnet.TLRPC$Document r1 = r2.document     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC$TL_document) r1     // Catch:{ Exception -> 0x0577 }
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1)     // Catch:{ Exception -> 0x0577 }
            if (r2 != 0) goto L_0x007c
            if (r27 == 0) goto L_0x007a
            goto L_0x007c
        L_0x007a:
            r2 = 7
            goto L_0x007d
        L_0x007c:
            r2 = 3
        L_0x007d:
            org.telegram.messenger.VideoEditedInfo r5 = r12.videoEditedInfo     // Catch:{ Exception -> 0x0577 }
            goto L_0x0087
        L_0x0080:
            r5 = r27
            r2 = -1
            goto L_0x0087
        L_0x0084:
            r5 = r27
            r2 = 1
        L_0x0087:
            java.util.HashMap<java.lang.String, java.lang.String> r15 = r6.params     // Catch:{ Exception -> 0x0577 }
            if (r32 != 0) goto L_0x0098
            if (r15 == 0) goto L_0x0098
            boolean r16 = r15.containsKey(r4)     // Catch:{ Exception -> 0x0577 }
            if (r16 == 0) goto L_0x0098
            java.lang.Object r4 = r15.get(r4)     // Catch:{ Exception -> 0x0577 }
            goto L_0x009a
        L_0x0098:
            r4 = r32
        L_0x009a:
            java.lang.String r7 = r6.message     // Catch:{ Exception -> 0x0577 }
            r12.editingMessage = r7     // Catch:{ Exception -> 0x0577 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r6.entities     // Catch:{ Exception -> 0x0577 }
            r12.editingMessageEntities = r7     // Catch:{ Exception -> 0x0577 }
            java.lang.String r7 = r6.attachPath     // Catch:{ Exception -> 0x0577 }
            r13 = r4
            goto L_0x0159
        L_0x00a7:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r6.media     // Catch:{ Exception -> 0x0577 }
            r12.previousMedia = r4     // Catch:{ Exception -> 0x0577 }
            java.lang.String r7 = r6.message     // Catch:{ Exception -> 0x0577 }
            r12.previousMessage = r7     // Catch:{ Exception -> 0x0577 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r6.entities     // Catch:{ Exception -> 0x0577 }
            r12.previousMessageEntities = r7     // Catch:{ Exception -> 0x0577 }
            java.lang.String r7 = r6.attachPath     // Catch:{ Exception -> 0x0577 }
            r12.previousAttachPath = r7     // Catch:{ Exception -> 0x0577 }
            if (r4 != 0) goto L_0x00be
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x0577 }
            r4.<init>()     // Catch:{ Exception -> 0x0577 }
        L_0x00be:
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0577 }
            r4.<init>((boolean) r13)     // Catch:{ Exception -> 0x0577 }
            r11.writePreviousMessageData(r6, r4)     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.SerializedData r7 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0577 }
            int r4 = r4.length()     // Catch:{ Exception -> 0x0577 }
            r7.<init>((int) r4)     // Catch:{ Exception -> 0x0577 }
            r11.writePreviousMessageData(r6, r7)     // Catch:{ Exception -> 0x0577 }
            java.lang.String r4 = "prevMedia"
            byte[] r15 = r7.toByteArray()     // Catch:{ Exception -> 0x0577 }
            r13 = 0
            java.lang.String r15 = android.util.Base64.encodeToString(r15, r13)     // Catch:{ Exception -> 0x0577 }
            r5.put(r4, r15)     // Catch:{ Exception -> 0x0577 }
            r7.cleanup()     // Catch:{ Exception -> 0x0577 }
            if (r0 == 0) goto L_0x0121
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0577 }
            r4.<init>()     // Catch:{ Exception -> 0x0577 }
            r6.media = r4     // Catch:{ Exception -> 0x0577 }
            int r7 = r4.flags     // Catch:{ Exception -> 0x0577 }
            r13 = 3
            r7 = r7 | r13
            r4.flags = r7     // Catch:{ Exception -> 0x0577 }
            r4.photo = r0     // Catch:{ Exception -> 0x0577 }
            if (r2 == 0) goto L_0x0105
            int r4 = r29.length()     // Catch:{ Exception -> 0x0577 }
            if (r4 <= 0) goto L_0x0105
            boolean r4 = r2.startsWith(r14)     // Catch:{ Exception -> 0x0577 }
            if (r4 == 0) goto L_0x0105
            r6.attachPath = r2     // Catch:{ Exception -> 0x0577 }
            goto L_0x011f
        L_0x0105:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r0.sizes     // Catch:{ Exception -> 0x0577 }
            int r7 = r4.size()     // Catch:{ Exception -> 0x0577 }
            r13 = 1
            int r7 = r7 - r13
            java.lang.Object r4 = r4.get(r7)     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = (org.telegram.tgnet.TLRPC$PhotoSize) r4     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.location     // Catch:{ Exception -> 0x0577 }
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r4, r13)     // Catch:{ Exception -> 0x0577 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0577 }
            r6.attachPath = r4     // Catch:{ Exception -> 0x0577 }
        L_0x011f:
            r4 = 2
            goto L_0x014d
        L_0x0121:
            if (r1 == 0) goto L_0x014c
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0577 }
            r4.<init>()     // Catch:{ Exception -> 0x0577 }
            r6.media = r4     // Catch:{ Exception -> 0x0577 }
            int r7 = r4.flags     // Catch:{ Exception -> 0x0577 }
            r13 = 3
            r7 = r7 | r13
            r4.flags = r7     // Catch:{ Exception -> 0x0577 }
            r4.document = r1     // Catch:{ Exception -> 0x0577 }
            boolean r4 = org.telegram.messenger.MessageObject.isVideoDocument(r28)     // Catch:{ Exception -> 0x0577 }
            if (r4 != 0) goto L_0x013d
            if (r27 == 0) goto L_0x013b
            goto L_0x013d
        L_0x013b:
            r4 = 7
            goto L_0x013e
        L_0x013d:
            r4 = 3
        L_0x013e:
            if (r27 == 0) goto L_0x0149
            java.lang.String r7 = r27.getString()     // Catch:{ Exception -> 0x0577 }
            java.lang.String r13 = "ve"
            r5.put(r13, r7)     // Catch:{ Exception -> 0x0577 }
        L_0x0149:
            r6.attachPath = r2     // Catch:{ Exception -> 0x0577 }
            goto L_0x014d
        L_0x014c:
            r4 = 1
        L_0x014d:
            r6.params = r5     // Catch:{ Exception -> 0x0577 }
            r7 = 3
            r6.send_state = r7     // Catch:{ Exception -> 0x0577 }
            r13 = r32
            r7 = r2
            r2 = r4
            r15 = r5
            r5 = r27
        L_0x0159:
            java.lang.String r4 = r6.attachPath     // Catch:{ Exception -> 0x0577 }
            if (r4 != 0) goto L_0x0161
            java.lang.String r4 = ""
            r6.attachPath = r4     // Catch:{ Exception -> 0x0577 }
        L_0x0161:
            r4 = 0
            r6.local_id = r4     // Catch:{ Exception -> 0x0577 }
            int r4 = r12.type     // Catch:{ Exception -> 0x0577 }
            r26 = r1
            r1 = 3
            if (r4 == r1) goto L_0x0170
            if (r5 != 0) goto L_0x0170
            r1 = 2
            if (r4 != r1) goto L_0x017b
        L_0x0170:
            java.lang.String r1 = r6.attachPath     // Catch:{ Exception -> 0x0577 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0577 }
            if (r1 != 0) goto L_0x017b
            r1 = 1
            r12.attachPathExists = r1     // Catch:{ Exception -> 0x0577 }
        L_0x017b:
            org.telegram.messenger.VideoEditedInfo r1 = r12.videoEditedInfo     // Catch:{ Exception -> 0x0577 }
            if (r1 == 0) goto L_0x0182
            if (r5 != 0) goto L_0x0182
            r5 = r1
        L_0x0182:
            if (r31 != 0) goto L_0x0257
            java.lang.CharSequence r4 = r12.editingMessage     // Catch:{ Exception -> 0x0577 }
            if (r4 == 0) goto L_0x01f3
            java.lang.String r1 = r6.message     // Catch:{ Exception -> 0x0577 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0577 }
            r6.message = r4     // Catch:{ Exception -> 0x0577 }
            r28 = r5
            r5 = 0
            r12.caption = r5     // Catch:{ Exception -> 0x0577 }
            r5 = 1
            if (r2 != r5) goto L_0x01b2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r12.editingMessageEntities     // Catch:{ Exception -> 0x0577 }
            if (r5 == 0) goto L_0x01a5
            r6.entities = r5     // Catch:{ Exception -> 0x0577 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x0577 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x0577 }
            goto L_0x01f5
        L_0x01a5:
            boolean r1 = android.text.TextUtils.equals(r1, r4)     // Catch:{ Exception -> 0x0577 }
            if (r1 != 0) goto L_0x01f5
            int r1 = r6.flags     // Catch:{ Exception -> 0x0577 }
            r1 = r1 & -129(0xffffffffffffff7f, float:NaN)
            r6.flags = r1     // Catch:{ Exception -> 0x0577 }
            goto L_0x01f5
        L_0x01b2:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r12.editingMessageEntities     // Catch:{ Exception -> 0x0577 }
            if (r4 == 0) goto L_0x01bf
            r6.entities = r4     // Catch:{ Exception -> 0x0577 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x0577 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x0577 }
            goto L_0x01ef
        L_0x01bf:
            r4 = 1
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r4]     // Catch:{ Exception -> 0x0577 }
            java.lang.CharSequence r4 = r12.editingMessage     // Catch:{ Exception -> 0x0577 }
            r16 = 0
            r5[r16] = r4     // Catch:{ Exception -> 0x0577 }
            org.telegram.messenger.MediaDataController r4 = r24.getMediaDataController()     // Catch:{ Exception -> 0x0577 }
            java.util.ArrayList r4 = r4.getEntities(r5, r10)     // Catch:{ Exception -> 0x0577 }
            if (r4 == 0) goto L_0x01e1
            boolean r5 = r4.isEmpty()     // Catch:{ Exception -> 0x0577 }
            if (r5 != 0) goto L_0x01e1
            r6.entities = r4     // Catch:{ Exception -> 0x0577 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x0577 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x0577 }
            goto L_0x01ef
        L_0x01e1:
            java.lang.String r4 = r6.message     // Catch:{ Exception -> 0x0577 }
            boolean r1 = android.text.TextUtils.equals(r1, r4)     // Catch:{ Exception -> 0x0577 }
            if (r1 != 0) goto L_0x01ef
            int r1 = r6.flags     // Catch:{ Exception -> 0x0577 }
            r1 = r1 & -129(0xffffffffffffff7f, float:NaN)
            r6.flags = r1     // Catch:{ Exception -> 0x0577 }
        L_0x01ef:
            r25.generateCaption()     // Catch:{ Exception -> 0x0577 }
            goto L_0x01f5
        L_0x01f3:
            r28 = r5
        L_0x01f5:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0577 }
            r1.<init>()     // Catch:{ Exception -> 0x0577 }
            r1.add(r6)     // Catch:{ Exception -> 0x0577 }
            org.telegram.messenger.MessagesStorage r17 = r24.getMessagesStorage()     // Catch:{ Exception -> 0x0577 }
            r19 = 0
            r20 = 1
            r21 = 0
            r22 = 0
            boolean r4 = r12.scheduled     // Catch:{ Exception -> 0x0577 }
            r18 = r1
            r23 = r4
            r17.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r18, (boolean) r19, (boolean) r20, (boolean) r21, (int) r22, (boolean) r23)     // Catch:{ Exception -> 0x0577 }
            r1 = -1
            r12.type = r1     // Catch:{ Exception -> 0x0577 }
            r25.setType()     // Catch:{ Exception -> 0x0577 }
            r1 = 1
            if (r2 != r1) goto L_0x0232
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media     // Catch:{ Exception -> 0x0577 }
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0577 }
            if (r4 != 0) goto L_0x022f
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0577 }
            if (r1 == 0) goto L_0x0228
            goto L_0x022f
        L_0x0228:
            r25.resetLayout()     // Catch:{ Exception -> 0x0577 }
            r25.checkLayout()     // Catch:{ Exception -> 0x0577 }
            goto L_0x0232
        L_0x022f:
            r25.generateCaption()     // Catch:{ Exception -> 0x0577 }
        L_0x0232:
            r25.createMessageSendInfo()     // Catch:{ Exception -> 0x0577 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0577 }
            r1.<init>()     // Catch:{ Exception -> 0x0577 }
            r1.add(r12)     // Catch:{ Exception -> 0x0577 }
            org.telegram.messenger.NotificationCenter r4 = r24.getNotificationCenter()     // Catch:{ Exception -> 0x0577 }
            int r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects     // Catch:{ Exception -> 0x0577 }
            r17 = r10
            r6 = 2
            java.lang.Object[] r10 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0577 }
            java.lang.Long r6 = java.lang.Long.valueOf(r8)     // Catch:{ Exception -> 0x0577 }
            r16 = 0
            r10[r16] = r6     // Catch:{ Exception -> 0x0577 }
            r6 = 1
            r10[r6] = r1     // Catch:{ Exception -> 0x0577 }
            r4.postNotificationName(r5, r10)     // Catch:{ Exception -> 0x0577 }
            goto L_0x025b
        L_0x0257:
            r28 = r5
            r17 = r10
        L_0x025b:
            if (r15 == 0) goto L_0x026b
            boolean r1 = r15.containsKey(r3)     // Catch:{ Exception -> 0x0577 }
            if (r1 == 0) goto L_0x026b
            java.lang.Object r1 = r15.get(r3)     // Catch:{ Exception -> 0x0577 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0577 }
            r4 = r1
            goto L_0x026c
        L_0x026b:
            r4 = 0
        L_0x026c:
            r1 = 8
            r3 = 1
            if (r2 < r3) goto L_0x0274
            r5 = 3
            if (r2 <= r5) goto L_0x0279
        L_0x0274:
            r5 = 5
            if (r2 < r5) goto L_0x057e
            if (r2 > r1) goto L_0x057e
        L_0x0279:
            if (r2 != r3) goto L_0x027f
            r32 = r15
            goto L_0x0466
        L_0x027f:
            java.lang.String r3 = "masks"
            r10 = 2
            if (r2 != r10) goto L_0x032f
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r10 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x0577 }
            r10.<init>()     // Catch:{ Exception -> 0x0577 }
            if (r15 == 0) goto L_0x02c9
            java.lang.Object r3 = r15.get(r3)     // Catch:{ Exception -> 0x0577 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0577 }
            if (r3 == 0) goto L_0x02c9
            org.telegram.tgnet.SerializedData r1 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0577 }
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x0577 }
            r1.<init>((byte[]) r3)     // Catch:{ Exception -> 0x0577 }
            r3 = 0
            int r5 = r1.readInt32(r3)     // Catch:{ Exception -> 0x0577 }
            r6 = 0
        L_0x02a2:
            if (r6 >= r5) goto L_0x02bd
            r26 = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r10.stickers     // Catch:{ Exception -> 0x0577 }
            r32 = r15
            int r15 = r1.readInt32(r3)     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$InputDocument r15 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r1, r15, r3)     // Catch:{ Exception -> 0x0577 }
            r5.add(r15)     // Catch:{ Exception -> 0x0577 }
            int r6 = r6 + 1
            r5 = r26
            r15 = r32
            r3 = 0
            goto L_0x02a2
        L_0x02bd:
            r32 = r15
            int r3 = r10.flags     // Catch:{ Exception -> 0x0577 }
            r5 = 1
            r3 = r3 | r5
            r10.flags = r3     // Catch:{ Exception -> 0x0577 }
            r1.cleanup()     // Catch:{ Exception -> 0x0577 }
            goto L_0x02cb
        L_0x02c9:
            r32 = r15
        L_0x02cb:
            long r5 = r0.access_hash     // Catch:{ Exception -> 0x0577 }
            r18 = 0
            int r1 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1))
            if (r1 != 0) goto L_0x02d6
            r5 = r10
            r1 = 1
            goto L_0x02f7
        L_0x02d6:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x0577 }
            r1.<init>()     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r3 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x0577 }
            r3.<init>()     // Catch:{ Exception -> 0x0577 }
            r1.id = r3     // Catch:{ Exception -> 0x0577 }
            long r5 = r0.id     // Catch:{ Exception -> 0x0577 }
            r3.id = r5     // Catch:{ Exception -> 0x0577 }
            long r5 = r0.access_hash     // Catch:{ Exception -> 0x0577 }
            r3.access_hash = r5     // Catch:{ Exception -> 0x0577 }
            byte[] r5 = r0.file_reference     // Catch:{ Exception -> 0x0577 }
            r3.file_reference = r5     // Catch:{ Exception -> 0x0577 }
            if (r5 != 0) goto L_0x02f5
            r5 = 0
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x0577 }
            r3.file_reference = r6     // Catch:{ Exception -> 0x0577 }
        L_0x02f5:
            r5 = r1
            r1 = 0
        L_0x02f7:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0577 }
            r3.<init>(r8)     // Catch:{ Exception -> 0x0577 }
            r6 = 0
            r3.type = r6     // Catch:{ Exception -> 0x0577 }
            r3.obj = r12     // Catch:{ Exception -> 0x0577 }
            r3.originalPath = r4     // Catch:{ Exception -> 0x0577 }
            r3.parentObject = r13     // Catch:{ Exception -> 0x0577 }
            r3.inputUploadMedia = r10     // Catch:{ Exception -> 0x0577 }
            r3.performMediaUpload = r1     // Catch:{ Exception -> 0x0577 }
            if (r7 == 0) goto L_0x031a
            int r6 = r7.length()     // Catch:{ Exception -> 0x0577 }
            if (r6 <= 0) goto L_0x031a
            boolean r6 = r7.startsWith(r14)     // Catch:{ Exception -> 0x0577 }
            if (r6 == 0) goto L_0x031a
            r3.httpLocation = r7     // Catch:{ Exception -> 0x0577 }
            goto L_0x032c
        L_0x031a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes     // Catch:{ Exception -> 0x0577 }
            int r7 = r6.size()     // Catch:{ Exception -> 0x0577 }
            r10 = 1
            int r7 = r7 - r10
            java.lang.Object r6 = r6.get(r7)     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC$PhotoSize) r6     // Catch:{ Exception -> 0x0577 }
            r3.photoSize = r6     // Catch:{ Exception -> 0x0577 }
            r3.locationParent = r0     // Catch:{ Exception -> 0x0577 }
        L_0x032c:
            r7 = r3
            goto L_0x0469
        L_0x032f:
            r32 = r15
            r0 = 3
            if (r2 != r0) goto L_0x03fa
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0577 }
            r0.<init>()     // Catch:{ Exception -> 0x0577 }
            r15 = r32
            if (r32 == 0) goto L_0x0370
            java.lang.Object r1 = r15.get(r3)     // Catch:{ Exception -> 0x0577 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0577 }
            if (r1 == 0) goto L_0x0370
            org.telegram.tgnet.SerializedData r3 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0577 }
            byte[] r1 = org.telegram.messenger.Utilities.hexToBytes(r1)     // Catch:{ Exception -> 0x0577 }
            r3.<init>((byte[]) r1)     // Catch:{ Exception -> 0x0577 }
            r1 = 0
            int r5 = r3.readInt32(r1)     // Catch:{ Exception -> 0x0577 }
            r6 = 0
        L_0x0354:
            if (r6 >= r5) goto L_0x0367
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r7 = r0.stickers     // Catch:{ Exception -> 0x0577 }
            int r10 = r3.readInt32(r1)     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$InputDocument r10 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r3, r10, r1)     // Catch:{ Exception -> 0x0577 }
            r7.add(r10)     // Catch:{ Exception -> 0x0577 }
            int r6 = r6 + 1
            r1 = 0
            goto L_0x0354
        L_0x0367:
            int r1 = r0.flags     // Catch:{ Exception -> 0x0577 }
            r5 = 1
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0577 }
            r3.cleanup()     // Catch:{ Exception -> 0x0577 }
        L_0x0370:
            r1 = r26
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x0577 }
            r0.mime_type = r3     // Catch:{ Exception -> 0x0577 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0577 }
            r0.attributes = r3     // Catch:{ Exception -> 0x0577 }
            boolean r3 = r25.isGif()     // Catch:{ Exception -> 0x0577 }
            if (r3 != 0) goto L_0x0398
            if (r28 == 0) goto L_0x0389
            r5 = r28
            boolean r3 = r5.muted     // Catch:{ Exception -> 0x0577 }
            if (r3 != 0) goto L_0x039a
            goto L_0x038b
        L_0x0389:
            r5 = r28
        L_0x038b:
            r3 = 1
            r0.nosound_video = r3     // Catch:{ Exception -> 0x0577 }
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0577 }
            if (r3 == 0) goto L_0x039a
            java.lang.String r3 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x0577 }
            goto L_0x039a
        L_0x0398:
            r5 = r28
        L_0x039a:
            long r6 = r1.access_hash     // Catch:{ Exception -> 0x0577 }
            r18 = 0
            int r3 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1))
            if (r3 != 0) goto L_0x03a7
            r3 = r0
            r32 = r15
            r6 = 1
            goto L_0x03c9
        L_0x03a7:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0577 }
            r3.<init>()     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0577 }
            r6.<init>()     // Catch:{ Exception -> 0x0577 }
            r3.id = r6     // Catch:{ Exception -> 0x0577 }
            r32 = r15
            long r14 = r1.id     // Catch:{ Exception -> 0x0577 }
            r6.id = r14     // Catch:{ Exception -> 0x0577 }
            long r14 = r1.access_hash     // Catch:{ Exception -> 0x0577 }
            r6.access_hash = r14     // Catch:{ Exception -> 0x0577 }
            byte[] r7 = r1.file_reference     // Catch:{ Exception -> 0x0577 }
            r6.file_reference = r7     // Catch:{ Exception -> 0x0577 }
            if (r7 != 0) goto L_0x03c8
            r7 = 0
            byte[] r10 = new byte[r7]     // Catch:{ Exception -> 0x0577 }
            r6.file_reference = r10     // Catch:{ Exception -> 0x0577 }
        L_0x03c8:
            r6 = 0
        L_0x03c9:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r7 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0577 }
            r7.<init>(r8)     // Catch:{ Exception -> 0x0577 }
            r10 = 1
            r7.type = r10     // Catch:{ Exception -> 0x0577 }
            r7.obj = r12     // Catch:{ Exception -> 0x0577 }
            r7.originalPath = r4     // Catch:{ Exception -> 0x0577 }
            r7.parentObject = r13     // Catch:{ Exception -> 0x0577 }
            r7.inputUploadMedia = r0     // Catch:{ Exception -> 0x0577 }
            r7.performMediaUpload = r6     // Catch:{ Exception -> 0x0577 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0577 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0577 }
            if (r0 != 0) goto L_0x03f4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0577 }
            r10 = 0
            java.lang.Object r0 = r0.get(r10)     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0     // Catch:{ Exception -> 0x0577 }
            boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x0577 }
            if (r10 != 0) goto L_0x03f4
            r7.photoSize = r0     // Catch:{ Exception -> 0x0577 }
            r7.locationParent = r1     // Catch:{ Exception -> 0x0577 }
        L_0x03f4:
            r7.videoEditedInfo = r5     // Catch:{ Exception -> 0x0577 }
            r5 = r3
            r1 = r6
            goto L_0x0469
        L_0x03fa:
            r1 = r26
            r0 = 7
            if (r2 != r0) goto L_0x0466
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0577 }
            r0.<init>()     // Catch:{ Exception -> 0x0577 }
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x0577 }
            r0.mime_type = r3     // Catch:{ Exception -> 0x0577 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0577 }
            r0.attributes = r3     // Catch:{ Exception -> 0x0577 }
            long r5 = r1.access_hash     // Catch:{ Exception -> 0x0577 }
            r14 = 0
            int r3 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r3 != 0) goto L_0x0417
            r5 = r0
            r3 = 1
            goto L_0x0438
        L_0x0417:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0577 }
            r3.<init>()     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0577 }
            r5.<init>()     // Catch:{ Exception -> 0x0577 }
            r3.id = r5     // Catch:{ Exception -> 0x0577 }
            long r6 = r1.id     // Catch:{ Exception -> 0x0577 }
            r5.id = r6     // Catch:{ Exception -> 0x0577 }
            long r6 = r1.access_hash     // Catch:{ Exception -> 0x0577 }
            r5.access_hash = r6     // Catch:{ Exception -> 0x0577 }
            byte[] r6 = r1.file_reference     // Catch:{ Exception -> 0x0577 }
            r5.file_reference = r6     // Catch:{ Exception -> 0x0577 }
            if (r6 != 0) goto L_0x0436
            r6 = 0
            byte[] r7 = new byte[r6]     // Catch:{ Exception -> 0x0577 }
            r5.file_reference = r7     // Catch:{ Exception -> 0x0577 }
        L_0x0436:
            r5 = r3
            r3 = 0
        L_0x0438:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0577 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x0577 }
            r6.originalPath = r4     // Catch:{ Exception -> 0x0577 }
            r7 = 2
            r6.type = r7     // Catch:{ Exception -> 0x0577 }
            r6.obj = r12     // Catch:{ Exception -> 0x0577 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x0577 }
            boolean r7 = r7.isEmpty()     // Catch:{ Exception -> 0x0577 }
            if (r7 != 0) goto L_0x045d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r1.thumbs     // Catch:{ Exception -> 0x0577 }
            r10 = 0
            java.lang.Object r7 = r7.get(r10)     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC$PhotoSize) r7     // Catch:{ Exception -> 0x0577 }
            boolean r10 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x0577 }
            if (r10 != 0) goto L_0x045d
            r6.photoSize = r7     // Catch:{ Exception -> 0x0577 }
            r6.locationParent = r1     // Catch:{ Exception -> 0x0577 }
        L_0x045d:
            r6.parentObject = r13     // Catch:{ Exception -> 0x0577 }
            r6.inputUploadMedia = r0     // Catch:{ Exception -> 0x0577 }
            r6.performMediaUpload = r3     // Catch:{ Exception -> 0x0577 }
            r1 = r3
            r7 = r6
            goto L_0x0469
        L_0x0466:
            r1 = 0
            r5 = 0
            r7 = 0
        L_0x0469:
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r0 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage     // Catch:{ Exception -> 0x0577 }
            r0.<init>()     // Catch:{ Exception -> 0x0577 }
            int r3 = r25.getId()     // Catch:{ Exception -> 0x0577 }
            r0.id = r3     // Catch:{ Exception -> 0x0577 }
            org.telegram.messenger.MessagesController r3 = r24.getMessagesController()     // Catch:{ Exception -> 0x0577 }
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((long) r8)     // Catch:{ Exception -> 0x0577 }
            r0.peer = r3     // Catch:{ Exception -> 0x0577 }
            if (r5 == 0) goto L_0x0488
            int r3 = r0.flags     // Catch:{ Exception -> 0x0577 }
            r3 = r3 | 16384(0x4000, float:2.2959E-41)
            r0.flags = r3     // Catch:{ Exception -> 0x0577 }
            r0.media = r5     // Catch:{ Exception -> 0x0577 }
        L_0x0488:
            boolean r3 = r12.scheduled     // Catch:{ Exception -> 0x0577 }
            if (r3 == 0) goto L_0x049a
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner     // Catch:{ Exception -> 0x0577 }
            int r3 = r3.date     // Catch:{ Exception -> 0x0577 }
            r0.schedule_date = r3     // Catch:{ Exception -> 0x0577 }
            int r3 = r0.flags     // Catch:{ Exception -> 0x0577 }
            r5 = 32768(0x8000, float:4.5918E-41)
            r3 = r3 | r5
            r0.flags = r3     // Catch:{ Exception -> 0x0577 }
        L_0x049a:
            java.lang.CharSequence r3 = r12.editingMessage     // Catch:{ Exception -> 0x0577 }
            if (r3 == 0) goto L_0x04e8
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0577 }
            r0.message = r3     // Catch:{ Exception -> 0x0577 }
            int r3 = r0.flags     // Catch:{ Exception -> 0x0577 }
            r3 = r3 | 2048(0x800, float:2.87E-42)
            r0.flags = r3     // Catch:{ Exception -> 0x0577 }
            boolean r5 = r12.editingMessageSearchWebPage     // Catch:{ Exception -> 0x0577 }
            if (r5 != 0) goto L_0x04b0
            r5 = 1
            goto L_0x04b1
        L_0x04b0:
            r5 = 0
        L_0x04b1:
            r0.no_webpage = r5     // Catch:{ Exception -> 0x0577 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r12.editingMessageEntities     // Catch:{ Exception -> 0x0577 }
            if (r5 == 0) goto L_0x04c0
            r0.entities = r5     // Catch:{ Exception -> 0x0577 }
            r5 = 8
            r3 = r3 | r5
            r0.flags = r3     // Catch:{ Exception -> 0x0577 }
        L_0x04be:
            r3 = 0
            goto L_0x04e4
        L_0x04c0:
            r3 = 1
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r3]     // Catch:{ Exception -> 0x0577 }
            java.lang.CharSequence r3 = r12.editingMessage     // Catch:{ Exception -> 0x0577 }
            r6 = 0
            r5[r6] = r3     // Catch:{ Exception -> 0x0577 }
            org.telegram.messenger.MediaDataController r3 = r24.getMediaDataController()     // Catch:{ Exception -> 0x0577 }
            r6 = r17
            java.util.ArrayList r3 = r3.getEntities(r5, r6)     // Catch:{ Exception -> 0x0577 }
            if (r3 == 0) goto L_0x04be
            boolean r5 = r3.isEmpty()     // Catch:{ Exception -> 0x0577 }
            if (r5 != 0) goto L_0x04be
            r0.entities = r3     // Catch:{ Exception -> 0x0577 }
            int r3 = r0.flags     // Catch:{ Exception -> 0x0577 }
            r5 = 8
            r3 = r3 | r5
            r0.flags = r3     // Catch:{ Exception -> 0x0577 }
            goto L_0x04be
        L_0x04e4:
            r12.editingMessage = r3     // Catch:{ Exception -> 0x0577 }
            r12.editingMessageEntities = r3     // Catch:{ Exception -> 0x0577 }
        L_0x04e8:
            if (r7 == 0) goto L_0x04ec
            r7.sendRequest = r0     // Catch:{ Exception -> 0x0577 }
        L_0x04ec:
            r3 = 1
            if (r2 != r3) goto L_0x0500
            r4 = 0
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0577 }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x0500:
            r3 = 2
            if (r2 != r3) goto L_0x051b
            if (r1 == 0) goto L_0x050a
            r11.performSendDelayedMessage(r7)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x050a:
            r5 = 0
            r6 = 1
            boolean r10 = r12.scheduled     // Catch:{ Exception -> 0x0577 }
            r1 = r24
            r2 = r0
            r3 = r25
            r8 = r13
            r9 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x051b:
            r3 = 3
            if (r2 != r3) goto L_0x0534
            if (r1 == 0) goto L_0x0525
            r11.performSendDelayedMessage(r7)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x0525:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0577 }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x0534:
            r3 = 6
            if (r2 != r3) goto L_0x0546
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0577 }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x0546:
            r3 = 7
            if (r2 != r3) goto L_0x055e
            if (r1 == 0) goto L_0x054f
            r11.performSendDelayedMessage(r7)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x054f:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0577 }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x055e:
            r3 = 8
            if (r2 != r3) goto L_0x057e
            if (r1 == 0) goto L_0x0568
            r11.performSendDelayedMessage(r7)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x0568:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0577 }
            r1 = r24
            r2 = r0
            r3 = r25
            r5 = r7
            r6 = r13
            r7 = r32
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0577 }
            goto L_0x057e
        L_0x0577:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r24.revertEditingMessageObject(r25)
        L_0x057e:
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
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda59(this, tLRPC$TL_error, baseFragment, tLRPC$TL_messages_editMessage));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda23(this, j, i, bArr));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda26(this, str));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda28(this, str, runnable));
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
            if (r0 == 0) goto L_0x01b3
            if (r12 == 0) goto L_0x01b3
            if (r24 != 0) goto L_0x000c
            goto L_0x01b3
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
            goto L_0x01b3
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
            goto L_0x01b3
        L_0x00b9:
            r2 = 2
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r1 == 0) goto L_0x016b
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            int r1 = r1.flags
            r1 = r1 & 4
            if (r1 != 0) goto L_0x0148
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm r1 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm
            r1.<init>()
            int r3 = r20.getId()
            r1.msg_id = r3
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            org.telegram.tgnet.TLRPC$InputPeer r0 = r3.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r0)
            r1.peer = r0
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x013c }
            r0.<init>()     // Catch:{ Exception -> 0x013c }
            java.lang.String r3 = "bg_color"
            java.lang.String r4 = "windowBackgroundWhite"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013c }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013c }
            java.lang.String r3 = "text_color"
            java.lang.String r4 = "windowBackgroundWhiteBlackText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013c }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013c }
            java.lang.String r3 = "hint_color"
            java.lang.String r4 = "windowBackgroundWhiteHintText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013c }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013c }
            java.lang.String r3 = "link_color"
            java.lang.String r4 = "windowBackgroundWhiteLinkText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013c }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013c }
            java.lang.String r3 = "button_color"
            java.lang.String r4 = "featuredStickers_addButton"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013c }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013c }
            java.lang.String r3 = "button_text_color"
            java.lang.String r4 = "featuredStickers_buttonText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013c }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013c }
            org.telegram.tgnet.TLRPC$TL_dataJSON r3 = new org.telegram.tgnet.TLRPC$TL_dataJSON     // Catch:{ Exception -> 0x013c }
            r3.<init>()     // Catch:{ Exception -> 0x013c }
            r1.theme_params = r3     // Catch:{ Exception -> 0x013c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x013c }
            r3.data = r0     // Catch:{ Exception -> 0x013c }
            int r0 = r1.flags     // Catch:{ Exception -> 0x013c }
            r3 = 1
            r0 = r0 | r3
            r1.flags = r0     // Catch:{ Exception -> 0x013c }
            goto L_0x0140
        L_0x013c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0140:
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r1, r14, r2)
            goto L_0x01b3
        L_0x0148:
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
            goto L_0x01b3
        L_0x016b:
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
            if (r0 == 0) goto L_0x01a0
            if (r22 == 0) goto L_0x0191
            r0 = r22
            goto L_0x0196
        L_0x0191:
            org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty
            r0.<init>()
        L_0x0196:
            r1.password = r0
            r1.password = r0
            int r0 = r1.flags
            r0 = r0 | 4
            r1.flags = r0
        L_0x01a0:
            byte[] r0 = r12.data
            if (r0 == 0) goto L_0x01ac
            int r3 = r1.flags
            r4 = 1
            r3 = r3 | r4
            r1.flags = r3
            r1.data = r0
        L_0x01ac:
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r1, r14, r2)
        L_0x01b3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendCallback$24(boolean, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP, org.telegram.ui.TwoStepVerificationActivity, org.telegram.ui.ChatActivity):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendCallback$30(String str, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, TwoStepVerificationActivity twoStepVerificationActivity, TLObject[] tLObjectArr, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, boolean z2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda30(this, str, z, tLObject, messageObject, tLRPC$KeyboardButton, chatActivity, twoStepVerificationActivity, tLObjectArr, tLRPC$TL_error, tLRPC$InputCheckPasswordSRP, z2));
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
            r8 = 2131626907(0x7f0e0b9b, float:1.8881063E38)
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
            r1.<init>(r0, r4, r6)
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
            r12 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r13 = "Cancel"
            if (r0 == 0) goto L_0x01f1
            if (r37 != 0) goto L_0x049d
            org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r33.getParentActivity()
            r8.<init>((android.content.Context) r0)
            r0 = 2131624655(0x7f0e02cf, float:1.8876496E38)
            java.lang.String r1 = "BotOwnershipTransfer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r8.setTitle(r0)
            r0 = 2131624658(0x7f0e02d2, float:1.8876502E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = "BotOwnershipTransferReadyAlertText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setMessage(r0)
            r0 = 2131624657(0x7f0e02d1, float:1.88765E38)
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
            r4 = 2131625457(0x7f0e05f1, float:1.8878122E38)
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
            r10 = 2131624656(0x7f0e02d0, float:1.8876498E38)
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
            r8 = 2131165610(0x7var_aa, float:1.7945442E38)
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
            r10 = 2131625454(0x7f0e05ee, float:1.8878116E38)
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
            r10 = 2131165610(0x7var_aa, float:1.7945442E38)
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
            r10 = 2131625455(0x7f0e05ef, float:1.8878118E38)
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
            r1 = 2131625462(0x7f0e05f6, float:1.8878133E38)
            java.lang.String r2 = "EditAdminTransferSetPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda1
            r2.<init>(r6)
            r0.setPositiveButton(r1, r2)
            r1 = 2131624750(0x7f0e032e, float:1.8876689E38)
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
            r2 = 2131625456(0x7f0e05f0, float:1.887812E38)
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
            r1 = 2131626907(0x7f0e0b9b, float:1.8881063E38)
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda56(this, tLRPC$TL_error, tLObject, twoStepVerificationActivity, z, messageObject, tLRPC$KeyboardButton, chatActivity));
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v4, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: org.telegram.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v9, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v8, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r68v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v19, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v13, resolved type: org.telegram.tgnet.TLRPC$MessageMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v20, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v21, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: org.telegram.tgnet.TLRPC$MessageMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v22, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v23, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v16, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v24, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v25, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v6, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v26, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v17, resolved type: org.telegram.tgnet.TLRPC$MessageMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v18, resolved type: org.telegram.tgnet.TLRPC$MessageMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v7, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v50, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v27, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v28, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v8, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v29, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v33, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v34, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v35, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v36, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v37, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v38, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v39, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v9, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v40, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v42, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v43, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v10, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v45, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v101, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v46, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v11, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v102, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v103, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v106, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v47, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v110, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v48, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v79, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v12, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v49, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v14, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v50, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v15, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v51, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v16, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v17, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v52, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v76, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v77, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v78, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v86, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v87, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v89, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v146, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v21, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v22, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v20, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v21, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v22, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v46, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v60, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v47, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v48, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v49, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v50, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v51, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v23, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v24, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v61, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v26, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v98, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v27, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v104, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v63, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v107, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v64, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v29, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v65, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v32, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v110, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v115, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v118, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v33, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v34, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v68, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v69, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v73, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v89, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v93, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v94, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r63v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v95, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v96, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v98, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v99, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v100, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v210, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v102, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v99, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v145, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v108, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v109, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v183, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v147, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v148, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v149, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v165, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v114, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v185, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v7, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v115, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v166, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r37v8, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v168, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v172, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v120, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v180, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v131, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v175, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v124, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v177, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v178, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v142, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v180, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v143, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v182, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v186, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v187, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v188, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v135, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v199, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v150, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v151, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v152, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v200, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v153, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v201, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v202, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v154, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v266, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v267, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v143, resolved type: org.telegram.tgnet.TLRPC$TL_userRequest_old2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v65, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v66, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v67, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v68, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v69, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v71, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v216, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v217, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v160, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v162, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v163, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v164, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v165, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v166, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v214, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v215, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v216, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v217, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v218, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v73, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v190, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v193, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX WARNING: type inference failed for: r7v45, types: [java.lang.Object, java.lang.String] */
    /* JADX WARNING: type inference failed for: r27v2 */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x052d, code lost:
        if (r6.containsKey(r5) != false) goto L_0x0554;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0552, code lost:
        if (r6.containsKey(r5) != false) goto L_0x0554;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x06ce, code lost:
        if (org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r2, true) != false) goto L_0x06d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x06e7, code lost:
        r2.attributes.remove(r1);
        r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55();
        r2.attributes.add(r1);
        r1.alt = r15.alt;
        r3 = r15.stickerset;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x06fc, code lost:
        if (r3 == null) goto L_0x0765;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x0700, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName) == false) goto L_0x0709;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x0702, code lost:
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:?, code lost:
        r2 = r3.short_name;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x0704, code lost:
        r33 = r5;
        r37 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x0709, code lost:
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:?, code lost:
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x070f, code lost:
        r33 = r5;
        r37 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x0713, code lost:
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:?, code lost:
        r2 = getMediaDataController().getStickerSetName(r15.stickerset.id);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x071d, code lost:
        if (android.text.TextUtils.isEmpty(r2) != false) goto L_0x072e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x071f, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName();
        r1.stickerset = r3;
        r3.short_name = r2;
        r2 = null;
        r6 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0732, code lost:
        if ((r15.stickerset instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetID) == false) goto L_0x0748;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0736, code lost:
        r6 = r59;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0739, code lost:
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:?, code lost:
        r2 = new org.telegram.messenger.SendMessagesHelper.DelayedMessage(r6, r10);
        r2.encryptedChat = r8;
        r2.locationParent = r1;
        r2.type = 5;
        r2.parentObject = r15.stickerset;
        r6 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0748, code lost:
        r6 = r59;
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x074d, code lost:
        r1.stickerset = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0754, code lost:
        r3 = r69;
        r15 = r75;
        r19 = r14;
        r1 = r18;
        r18 = r67;
        r14 = r2;
        r2 = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:400:0x0763, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0765, code lost:
        r33 = r5;
        r37 = r6;
        r6 = r59;
        r1.stickerset = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0775, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0776, code lost:
        r14 = r82;
        r1 = r0;
        r8 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:604:0x0aaa, code lost:
        if (org.telegram.messenger.MessageObject.isRoundVideoMessage(r9) != false) goto L_0x0aac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x0ee9, code lost:
        if (r4.roundVideo == false) goto L_0x0eef;
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1010:0x1200 A[Catch:{ Exception -> 0x12a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1011:0x1206 A[Catch:{ Exception -> 0x12a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1023:0x1241 A[Catch:{ Exception -> 0x1261 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x1290  */
    /* JADX WARNING: Removed duplicated region for block: B:1093:0x1392 A[Catch:{ Exception -> 0x1587 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1110:0x141d A[Catch:{ Exception -> 0x1587 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1131:0x1482  */
    /* JADX WARNING: Removed duplicated region for block: B:1134:0x1489 A[Catch:{ Exception -> 0x1587 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1195:0x1590  */
    /* JADX WARNING: Removed duplicated region for block: B:1269:0x16f6 A[Catch:{ Exception -> 0x15c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1275:0x1722 A[Catch:{ Exception -> 0x15c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1280:0x1763 A[Catch:{ Exception -> 0x15c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1289:0x1795  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02be A[Catch:{ Exception -> 0x0347 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1290:0x1797  */
    /* JADX WARNING: Removed duplicated region for block: B:1293:0x179b A[Catch:{ Exception -> 0x17b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1296:0x17ad A[Catch:{ Exception -> 0x17b9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02c3 A[Catch:{ Exception -> 0x0347 }] */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02d2  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02f4  */
    /* JADX WARNING: Removed duplicated region for block: B:1363:0x19d4 A[Catch:{ Exception -> 0x1809 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1364:0x19d6 A[Catch:{ Exception -> 0x1809 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1405:0x1ad9 A[Catch:{ Exception -> 0x12a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1406:0x1adb A[Catch:{ Exception -> 0x12a4 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1423:0x1b0f A[SYNTHETIC, Splitter:B:1423:0x1b0f] */
    /* JADX WARNING: Removed duplicated region for block: B:1428:0x1b17 A[SYNTHETIC, Splitter:B:1428:0x1b17] */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0357  */
    /* JADX WARNING: Removed duplicated region for block: B:1447:0x1b5f A[Catch:{ Exception -> 0x1cb8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1449:0x1b63 A[Catch:{ Exception -> 0x1cb8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1461:0x1b96 A[Catch:{ Exception -> 0x1cb8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1492:0x1c1d A[Catch:{ Exception -> 0x1cb8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1545:0x1cf3  */
    /* JADX WARNING: Removed duplicated region for block: B:1546:0x1cf5  */
    /* JADX WARNING: Removed duplicated region for block: B:1549:0x1cfb  */
    /* JADX WARNING: Removed duplicated region for block: B:1563:0x1714 A[EDGE_INSN: B:1563:0x1714->B:1273:0x1714 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:1581:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x03f8 A[Catch:{ Exception -> 0x039e }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0400 A[Catch:{ Exception -> 0x039e }] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0411 A[Catch:{ Exception -> 0x039e }] */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0414 A[Catch:{ Exception -> 0x039e }] */
    /* JADX WARNING: Removed duplicated region for block: B:322:0x0653 A[SYNTHETIC, Splitter:B:322:0x0653] */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x0661 A[SYNTHETIC, Splitter:B:327:0x0661] */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0670 A[SYNTHETIC, Splitter:B:334:0x0670] */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x0686  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0689  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x068c A[SYNTHETIC, Splitter:B:347:0x068c] */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x069f A[Catch:{ Exception -> 0x0638 }] */
    /* JADX WARNING: Removed duplicated region for block: B:360:0x06b4 A[Catch:{ Exception -> 0x0638 }] */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x06bf A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:364:0x06c3 A[Catch:{ Exception -> 0x07a9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x07c4 A[SYNTHETIC, Splitter:B:415:0x07c4] */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x07dd A[Catch:{ Exception -> 0x07d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x07e0 A[SYNTHETIC, Splitter:B:423:0x07e0] */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x07ea A[SYNTHETIC, Splitter:B:431:0x07ea] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x07fd A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0819 A[Catch:{ Exception -> 0x07d3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0820 A[SYNTHETIC, Splitter:B:443:0x0820] */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x085f  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x088e  */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x089d A[SYNTHETIC, Splitter:B:465:0x089d] */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x08a7  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x011c A[SYNTHETIC, Splitter:B:46:0x011c] */
    /* JADX WARNING: Removed duplicated region for block: B:483:0x08dd A[SYNTHETIC, Splitter:B:483:0x08dd] */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x08ed  */
    /* JADX WARNING: Removed duplicated region for block: B:490:0x08ef A[SYNTHETIC, Splitter:B:490:0x08ef] */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x08fd A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:508:0x093a  */
    /* JADX WARNING: Removed duplicated region for block: B:514:0x094f A[SYNTHETIC, Splitter:B:514:0x094f] */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x0993 A[Catch:{ Exception -> 0x0886 }] */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x099e A[Catch:{ Exception -> 0x0886 }] */
    /* JADX WARNING: Removed duplicated region for block: B:537:0x09ca A[SYNTHETIC, Splitter:B:537:0x09ca] */
    /* JADX WARNING: Removed duplicated region for block: B:549:0x09fa  */
    /* JADX WARNING: Removed duplicated region for block: B:572:0x0a2b A[SYNTHETIC, Splitter:B:572:0x0a2b] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0135 A[Catch:{ Exception -> 0x0347 }] */
    /* JADX WARNING: Removed duplicated region for block: B:602:0x0aa6 A[SYNTHETIC, Splitter:B:602:0x0aa6] */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0ab3 A[SYNTHETIC, Splitter:B:610:0x0ab3] */
    /* JADX WARNING: Removed duplicated region for block: B:616:0x0abc  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0ae9  */
    /* JADX WARNING: Removed duplicated region for block: B:638:0x0b26  */
    /* JADX WARNING: Removed duplicated region for block: B:639:0x0b28  */
    /* JADX WARNING: Removed duplicated region for block: B:643:0x0b31 A[SYNTHETIC, Splitter:B:643:0x0b31] */
    /* JADX WARNING: Removed duplicated region for block: B:656:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:664:0x0b5d A[SYNTHETIC, Splitter:B:664:0x0b5d] */
    /* JADX WARNING: Removed duplicated region for block: B:678:0x0baa  */
    /* JADX WARNING: Removed duplicated region for block: B:699:0x0bf6  */
    /* JADX WARNING: Removed duplicated region for block: B:708:0x0CLASSNAME A[Catch:{ Exception -> 0x0c5e }] */
    /* JADX WARNING: Removed duplicated region for block: B:710:0x0c6d A[Catch:{ Exception -> 0x0c5e }] */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x0d30 A[Catch:{ Exception -> 0x0c5e }] */
    /* JADX WARNING: Removed duplicated region for block: B:754:0x0d49 A[Catch:{ Exception -> 0x0c5e }] */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x0d57 A[Catch:{ Exception -> 0x0c5e }] */
    /* JADX WARNING: Removed duplicated region for block: B:763:0x0d7a A[Catch:{ Exception -> 0x0c5e }] */
    /* JADX WARNING: Removed duplicated region for block: B:764:0x0d7c A[Catch:{ Exception -> 0x0c5e }] */
    /* JADX WARNING: Removed duplicated region for block: B:804:0x0e48  */
    /* JADX WARNING: Removed duplicated region for block: B:850:0x0efe A[Catch:{ Exception -> 0x0eb5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:852:0x0f0c  */
    /* JADX WARNING: Removed duplicated region for block: B:866:0x0var_ A[Catch:{ Exception -> 0x1058 }] */
    /* JADX WARNING: Removed duplicated region for block: B:867:0x0var_ A[Catch:{ Exception -> 0x1058 }] */
    /* JADX WARNING: Removed duplicated region for block: B:876:0x0fa4 A[Catch:{ Exception -> 0x1058 }] */
    /* JADX WARNING: Removed duplicated region for block: B:881:0x0fbe A[Catch:{ Exception -> 0x1058 }] */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x0fd0 A[Catch:{ Exception -> 0x1058 }] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:205:0x0468=Splitter:B:205:0x0468, B:194:0x043d=Splitter:B:194:0x043d} */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:219:0x04a3=Splitter:B:219:0x04a3, B:279:0x0589=Splitter:B:279:0x0589} */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendMessage(java.lang.String r60, java.lang.String r61, org.telegram.tgnet.TLRPC$MessageMedia r62, org.telegram.tgnet.TLRPC$TL_photo r63, org.telegram.messenger.VideoEditedInfo r64, org.telegram.tgnet.TLRPC$User r65, org.telegram.tgnet.TLRPC$TL_document r66, org.telegram.tgnet.TLRPC$TL_game r67, org.telegram.tgnet.TLRPC$TL_messageMediaPoll r68, org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r69, long r70, java.lang.String r72, org.telegram.messenger.MessageObject r73, org.telegram.messenger.MessageObject r74, org.telegram.tgnet.TLRPC$WebPage r75, boolean r76, org.telegram.messenger.MessageObject r77, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r78, org.telegram.tgnet.TLRPC$ReplyMarkup r79, java.util.HashMap<java.lang.String, java.lang.String> r80, boolean r81, int r82, int r83, java.lang.Object r84, org.telegram.messenger.MessageObject.SendAnimationData r85) {
        /*
            r59 = this;
            r1 = r59
            r2 = r60
            r3 = r62
            r4 = r63
            r5 = r65
            r6 = r66
            r7 = r67
            r8 = r68
            r9 = r69
            r10 = r70
            r12 = r72
            r13 = r73
            r14 = r74
            r15 = r75
            r14 = r77
            r13 = r78
            r6 = r80
            r9 = r82
            r7 = r83
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
            if (r61 != 0) goto L_0x003d
            r18 = r12
            goto L_0x003f
        L_0x003d:
            r18 = r61
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
            boolean r20 = org.telegram.messenger.DialogObject.isEncryptedDialog(r70)
            if (r20 != 0) goto L_0x0066
            r20 = r5
            org.telegram.messenger.MessagesController r5 = r59.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer((long) r10)
            goto L_0x0069
        L_0x0066:
            r20 = r5
            r5 = 0
        L_0x0069:
            org.telegram.messenger.UserConfig r21 = r59.getUserConfig()
            long r3 = r21.getClientUserId()
            boolean r21 = org.telegram.messenger.DialogObject.isEncryptedDialog(r70)
            r22 = r3
            if (r21 == 0) goto L_0x00c1
            org.telegram.messenger.MessagesController r3 = r59.getMessagesController()
            int r25 = org.telegram.messenger.DialogObject.getEncryptedChatId(r70)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r25)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r4)
            if (r3 != 0) goto L_0x00bc
            if (r14 == 0) goto L_0x00bb
            org.telegram.messenger.MessagesStorage r2 = r59.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            boolean r4 = r14.scheduled
            r2.markMessageAsSendError(r3, r4)
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            r3 = 2
            r2.send_state = r3
            org.telegram.messenger.NotificationCenter r2 = r59.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.messageSendError
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r5 = r77.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 0
            r4[r6] = r5
            r2.postNotificationName(r3, r4)
            int r2 = r77.getId()
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
            org.telegram.messenger.MessagesController r3 = r59.getMessagesController()
            long r7 = r5.channel_id
            java.lang.Long r4 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            org.telegram.messenger.MessagesController r4 = r59.getMessagesController()
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
            org.telegram.messenger.MessagesController r7 = r59.getMessagesController()
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
            r32 = r5
            java.lang.String r5 = "query_id"
            if (r14 == 0) goto L_0x0357
            org.telegram.tgnet.TLRPC$Message r1 = r14.messageOwner     // Catch:{ Exception -> 0x0350 }
            if (r84 != 0) goto L_0x012d
            if (r6 == 0) goto L_0x012d
            boolean r27 = r6.containsKey(r3)     // Catch:{ Exception -> 0x0347 }
            if (r27 == 0) goto L_0x012d
            java.lang.Object r27 = r6.get(r3)     // Catch:{ Exception -> 0x0347 }
            goto L_0x012f
        L_0x012d:
            r27 = r84
        L_0x012f:
            boolean r34 = r77.isForwarded()     // Catch:{ Exception -> 0x0347 }
            if (r34 != 0) goto L_0x0317
            if (r6 == 0) goto L_0x013f
            boolean r34 = r6.containsKey(r7)     // Catch:{ Exception -> 0x0347 }
            if (r34 == 0) goto L_0x013f
            goto L_0x0317
        L_0x013f:
            boolean r34 = r77.isDice()     // Catch:{ Exception -> 0x0347 }
            if (r34 == 0) goto L_0x0161
            java.lang.String r2 = r77.getDiceEmoji()     // Catch:{ Exception -> 0x0347 }
            r13 = r68
            r34 = r3
            r36 = r4
            r35 = r7
            r18 = r12
            r37 = r18
            r19 = 11
            r3 = r62
            r4 = r63
            r7 = r65
        L_0x015d:
            r12 = r66
            goto L_0x02b6
        L_0x0161:
            r34 = r3
            int r3 = r14.type     // Catch:{ Exception -> 0x0347 }
            if (r3 == 0) goto L_0x0299
            boolean r3 = r77.isAnimatedEmoji()     // Catch:{ Exception -> 0x0347 }
            if (r3 == 0) goto L_0x016f
            goto L_0x0299
        L_0x016f:
            int r3 = r14.type     // Catch:{ Exception -> 0x0347 }
            r35 = r7
            r7 = 4
            if (r3 != r7) goto L_0x0185
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x0347 }
            r7 = r65
            r13 = r68
            r36 = r4
            r37 = r12
            r19 = 1
            r4 = r63
            goto L_0x015d
        L_0x0185:
            r7 = 1
            if (r3 != r7) goto L_0x01a7
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$Message r7 = r14.messageOwner     // Catch:{ Exception -> 0x0347 }
            java.lang.String r7 = r7.message     // Catch:{ Exception -> 0x0347 }
            if (r7 == 0) goto L_0x0196
            r18 = r7
        L_0x0196:
            r7 = r65
            r13 = r68
            r36 = r4
            r37 = r12
            r19 = 2
            r12 = r66
            r4 = r3
        L_0x01a3:
            r3 = r62
            goto L_0x02b6
        L_0x01a7:
            r7 = 3
            if (r3 == r7) goto L_0x0270
            r7 = 5
            if (r3 == r7) goto L_0x0270
            org.telegram.messenger.VideoEditedInfo r7 = r14.videoEditedInfo     // Catch:{ Exception -> 0x0347 }
            if (r7 == 0) goto L_0x01b3
            goto L_0x0270
        L_0x01b3:
            r7 = 12
            if (r3 != r7) goto L_0x01f2
            org.telegram.tgnet.TLRPC$TL_userRequest_old2 r3 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2     // Catch:{ Exception -> 0x0347 }
            r3.<init>()     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r1.media     // Catch:{ Exception -> 0x0347 }
            r36 = r4
            java.lang.String r4 = r7.phone_number     // Catch:{ Exception -> 0x0347 }
            r3.phone = r4     // Catch:{ Exception -> 0x0347 }
            java.lang.String r4 = r7.first_name     // Catch:{ Exception -> 0x0347 }
            r3.first_name = r4     // Catch:{ Exception -> 0x0347 }
            java.lang.String r4 = r7.last_name     // Catch:{ Exception -> 0x0347 }
            r3.last_name = r4     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r4 = new org.telegram.tgnet.TLRPC$TL_restrictionReason     // Catch:{ Exception -> 0x0347 }
            r4.<init>()     // Catch:{ Exception -> 0x0347 }
            r4.platform = r12     // Catch:{ Exception -> 0x0347 }
            r4.reason = r12     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r1.media     // Catch:{ Exception -> 0x0347 }
            java.lang.String r7 = r7.vcard     // Catch:{ Exception -> 0x0347 }
            r4.text = r7     // Catch:{ Exception -> 0x0347 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r7 = r3.restriction_reason     // Catch:{ Exception -> 0x0347 }
            r7.add(r4)     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media     // Catch:{ Exception -> 0x0347 }
            r37 = r12
            long r12 = r4.user_id     // Catch:{ Exception -> 0x0347 }
            r3.id = r12     // Catch:{ Exception -> 0x0347 }
            r4 = r63
            r12 = r66
            r13 = r68
            r7 = r3
            r19 = 6
            goto L_0x01a3
        L_0x01f2:
            r36 = r4
            r37 = r12
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
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0347 }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x0347 }
            if (r4 == 0) goto L_0x0225
            r7 = r65
            r13 = r68
            r12 = r3
            r18 = r4
            r19 = 8
            goto L_0x0289
        L_0x0225:
            r4 = r63
            r7 = r65
            r13 = r68
            r12 = r3
            r19 = 8
            goto L_0x01a3
        L_0x0230:
            r4 = 17
            if (r3 != r4) goto L_0x0243
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3     // Catch:{ Exception -> 0x0347 }
            r4 = r63
            r7 = r65
            r12 = r66
            r13 = r3
            r19 = 10
            goto L_0x01a3
        L_0x0243:
            r3 = r62
            r4 = r63
            r7 = r65
            r12 = r66
            r13 = r68
            goto L_0x02b6
        L_0x024f:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0347 }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x0347 }
            if (r4 == 0) goto L_0x0265
            r7 = r65
            r13 = r68
            r12 = r3
            r18 = r4
            r19 = 7
            goto L_0x0289
        L_0x0265:
            r4 = r63
            r7 = r65
            r13 = r68
            r12 = r3
            r19 = 7
            goto L_0x01a3
        L_0x0270:
            r36 = r4
            r37 = r12
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0347 }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x0347 }
            if (r4 == 0) goto L_0x028e
            r7 = r65
            r13 = r68
            r12 = r3
            r18 = r4
            r19 = 3
        L_0x0289:
            r3 = r62
            r4 = r63
            goto L_0x02b6
        L_0x028e:
            r4 = r63
            r7 = r65
            r13 = r68
            r12 = r3
            r19 = 3
            goto L_0x01a3
        L_0x0299:
            r36 = r4
            r35 = r7
            r37 = r12
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x0347 }
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media     // Catch:{ Exception -> 0x0347 }
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x0347 }
            if (r3 == 0) goto L_0x02a8
            goto L_0x02aa
        L_0x02a8:
            java.lang.String r2 = r1.message     // Catch:{ Exception -> 0x0347 }
        L_0x02aa:
            r3 = r62
            r4 = r63
            r7 = r65
            r12 = r66
            r13 = r68
            r19 = 0
        L_0x02b6:
            if (r6 == 0) goto L_0x02c3
            boolean r38 = r6.containsKey(r5)     // Catch:{ Exception -> 0x0347 }
            if (r38 == 0) goto L_0x02c3
            r60 = r2
            r19 = 9
            goto L_0x02c5
        L_0x02c3:
            r60 = r2
        L_0x02c5:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0347 }
            int r2 = r2.ttl_seconds     // Catch:{ Exception -> 0x0347 }
            r14 = r81
            r9 = r1
            r62 = r4
            r33 = r5
            if (r2 <= 0) goto L_0x02f4
            r39 = r12
            r40 = r13
            r41 = r15
            r42 = r18
            r15 = r19
            r4 = r22
            r1 = r32
            r12 = r37
            r18 = 0
            r19 = 0
            r13 = r60
            r22 = r3
            r60 = r7
            r3 = r27
            r7 = r2
            r2 = r6
            r6 = r59
            goto L_0x085d
        L_0x02f4:
            r2 = r6
            r39 = r12
            r40 = r13
            r41 = r15
            r42 = r18
            r15 = r19
            r4 = r22
            r1 = r32
            r12 = r37
            r18 = 0
            r19 = 0
            r6 = r59
            r13 = r60
            r22 = r3
            r60 = r7
            r3 = r27
            r7 = r83
            goto L_0x085d
        L_0x0317:
            r34 = r3
            r36 = r4
            r35 = r7
            r37 = r12
            r60 = r65
            r39 = r66
            r40 = r68
            r14 = r81
            r7 = r83
            r9 = r1
            r13 = r2
            r33 = r5
            r2 = r6
            r41 = r15
            r42 = r18
            r4 = r22
            r3 = r27
            r1 = r32
            r12 = r37
            r15 = 4
            r18 = 0
            r19 = 0
            r6 = r59
            r22 = r62
            r62 = r63
            goto L_0x085d
        L_0x0347:
            r0 = move-exception
            r8 = r59
            r5 = r1
            r14 = r9
        L_0x034c:
            r15 = 0
        L_0x034d:
            r1 = r0
            goto L_0x1cea
        L_0x0350:
            r0 = move-exception
            r8 = r59
            r1 = r0
            r14 = r9
            goto L_0x1ce8
        L_0x0357:
            r34 = r3
            r36 = r4
            r35 = r7
            r37 = r12
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r70)     // Catch:{ Exception -> 0x1ce3 }
            if (r1 == 0) goto L_0x0377
            org.telegram.messenger.MessagesController r1 = r59.getMessagesController()     // Catch:{ Exception -> 0x0350 }
            long r3 = -r10
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x0350 }
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)     // Catch:{ Exception -> 0x0350 }
            boolean r1 = org.telegram.messenger.ChatObject.canSendStickers(r1)     // Catch:{ Exception -> 0x0350 }
            goto L_0x0378
        L_0x0377:
            r1 = 1
        L_0x0378:
            if (r2 == 0) goto L_0x0428
            if (r8 == 0) goto L_0x0382
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0350 }
            r3.<init>()     // Catch:{ Exception -> 0x0350 }
            goto L_0x0387
        L_0x0382:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0350 }
            r3.<init>()     // Catch:{ Exception -> 0x0350 }
        L_0x0387:
            if (r8 == 0) goto L_0x03a6
            boolean r4 = r15 instanceof org.telegram.tgnet.TLRPC$TL_webPagePending     // Catch:{ Exception -> 0x039e }
            if (r4 == 0) goto L_0x03a6
            java.lang.String r4 = r15.url     // Catch:{ Exception -> 0x039e }
            if (r4 == 0) goto L_0x039c
            org.telegram.tgnet.TLRPC$TL_webPageUrlPending r4 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending     // Catch:{ Exception -> 0x039e }
            r4.<init>()     // Catch:{ Exception -> 0x039e }
            java.lang.String r7 = r15.url     // Catch:{ Exception -> 0x039e }
            r4.url = r7     // Catch:{ Exception -> 0x039e }
            r15 = r4
            goto L_0x03a6
        L_0x039c:
            r15 = 0
            goto L_0x03a6
        L_0x039e:
            r0 = move-exception
            r8 = r59
            r1 = r0
            r5 = r3
            r14 = r9
            goto L_0x1ce9
        L_0x03a6:
            if (r1 == 0) goto L_0x03f2
            int r1 = r60.length()     // Catch:{ Exception -> 0x039e }
            r4 = 30
            if (r1 >= r4) goto L_0x03f2
            if (r15 != 0) goto L_0x03f2
            r4 = r78
            if (r4 == 0) goto L_0x03bc
            boolean r1 = r78.isEmpty()     // Catch:{ Exception -> 0x039e }
            if (r1 == 0) goto L_0x03f4
        L_0x03bc:
            org.telegram.messenger.MessagesController r1 = r59.getMessagesController()     // Catch:{ Exception -> 0x039e }
            java.util.HashSet<java.lang.String> r1 = r1.diceEmojies     // Catch:{ Exception -> 0x039e }
            java.lang.String r7 = "️"
            r12 = r37
            java.lang.String r7 = r2.replace(r7, r12)     // Catch:{ Exception -> 0x039e }
            boolean r1 = r1.contains(r7)     // Catch:{ Exception -> 0x039e }
            if (r1 == 0) goto L_0x03f6
            if (r8 != 0) goto L_0x03f6
            if (r9 != 0) goto L_0x03f6
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaDice     // Catch:{ Exception -> 0x039e }
            r1.<init>()     // Catch:{ Exception -> 0x039e }
            r1.emoticon = r2     // Catch:{ Exception -> 0x039e }
            r7 = -1
            r1.value = r7     // Catch:{ Exception -> 0x039e }
            r3.media = r1     // Catch:{ Exception -> 0x039e }
            r7 = r65
            r9 = r72
            r13 = r83
            r2 = r3
            r33 = r5
            r3 = r6
            r1 = r12
            r14 = 0
            r18 = 0
            r19 = 11
            goto L_0x0450
        L_0x03f2:
            r4 = r78
        L_0x03f4:
            r12 = r37
        L_0x03f6:
            if (r15 != 0) goto L_0x0400
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x039e }
            r1.<init>()     // Catch:{ Exception -> 0x039e }
            r3.media = r1     // Catch:{ Exception -> 0x039e }
            goto L_0x0409
        L_0x0400:
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x039e }
            r1.<init>()     // Catch:{ Exception -> 0x039e }
            r3.media = r1     // Catch:{ Exception -> 0x039e }
            r1.webpage = r15     // Catch:{ Exception -> 0x039e }
        L_0x0409:
            if (r6 == 0) goto L_0x0414
            boolean r1 = r6.containsKey(r5)     // Catch:{ Exception -> 0x039e }
            if (r1 == 0) goto L_0x0414
            r19 = 9
            goto L_0x0416
        L_0x0414:
            r19 = 0
        L_0x0416:
            r3.message = r2     // Catch:{ Exception -> 0x039e }
            r7 = r65
            r9 = r72
            r13 = r83
            r2 = r3
            r33 = r5
            r3 = r6
            r1 = r18
            r14 = 0
            r18 = 0
            goto L_0x0450
        L_0x0428:
            r3 = r68
            r4 = r78
            r12 = r37
            if (r3 == 0) goto L_0x0455
            if (r8 == 0) goto L_0x0438
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0350 }
            r1.<init>()     // Catch:{ Exception -> 0x0350 }
            goto L_0x043d
        L_0x0438:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0350 }
            r1.<init>()     // Catch:{ Exception -> 0x0350 }
        L_0x043d:
            r1.media = r3     // Catch:{ Exception -> 0x0347 }
            r7 = r65
            r9 = r72
            r13 = r83
            r2 = r1
            r33 = r5
            r3 = r6
            r1 = r18
            r14 = 0
            r18 = 0
            r19 = 10
        L_0x0450:
            r5 = 3
        L_0x0451:
            r6 = r59
            goto L_0x07c2
        L_0x0455:
            r7 = r62
            r2 = r22
            if (r7 == 0) goto L_0x0492
            if (r8 == 0) goto L_0x0463
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0350 }
            r1.<init>()     // Catch:{ Exception -> 0x0350 }
            goto L_0x0468
        L_0x0463:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0350 }
            r1.<init>()     // Catch:{ Exception -> 0x0350 }
        L_0x0468:
            r1.media = r7     // Catch:{ Exception -> 0x0347 }
            if (r6 == 0) goto L_0x0482
            boolean r13 = r6.containsKey(r5)     // Catch:{ Exception -> 0x0347 }
            if (r13 == 0) goto L_0x0482
            r7 = r65
            r9 = r72
            r13 = r83
            r22 = r2
            r33 = r5
            r3 = r6
            r14 = 0
            r19 = 9
            goto L_0x0562
        L_0x0482:
            r7 = r65
            r9 = r72
            r13 = r83
            r22 = r2
            r33 = r5
            r3 = r6
            r14 = 0
            r19 = 1
            goto L_0x0562
        L_0x0492:
            r13 = r63
            if (r13 == 0) goto L_0x0511
            if (r8 == 0) goto L_0x049e
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0350 }
            r1.<init>()     // Catch:{ Exception -> 0x0350 }
            goto L_0x04a3
        L_0x049e:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0538 }
            r1.<init>()     // Catch:{ Exception -> 0x0538 }
        L_0x04a3:
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0530 }
            r7.<init>()     // Catch:{ Exception -> 0x0530 }
            r1.media = r7     // Catch:{ Exception -> 0x0530 }
            int r9 = r7.flags     // Catch:{ Exception -> 0x0530 }
            r19 = 3
            r9 = r9 | 3
            r7.flags = r9     // Catch:{ Exception -> 0x0530 }
            if (r4 == 0) goto L_0x04b6
            r1.entities = r4     // Catch:{ Exception -> 0x0530 }
        L_0x04b6:
            r15 = r83
            if (r15 == 0) goto L_0x04c4
            r7.ttl_seconds = r15     // Catch:{ Exception -> 0x0530 }
            r1.ttl = r15     // Catch:{ Exception -> 0x0530 }
            r19 = 4
            r9 = r9 | 4
            r7.flags = r9     // Catch:{ Exception -> 0x0530 }
        L_0x04c4:
            r7.photo = r13     // Catch:{ Exception -> 0x0530 }
            if (r6 == 0) goto L_0x04d3
            boolean r7 = r6.containsKey(r5)     // Catch:{ Exception -> 0x0530 }
            if (r7 == 0) goto L_0x04d3
            r9 = r72
            r19 = 9
            goto L_0x04d7
        L_0x04d3:
            r9 = r72
            r19 = 2
        L_0x04d7:
            if (r9 == 0) goto L_0x04ee
            int r7 = r72.length()     // Catch:{ Exception -> 0x0530 }
            if (r7 <= 0) goto L_0x04ee
            r7 = r36
            boolean r22 = r9.startsWith(r7)     // Catch:{ Exception -> 0x0530 }
            if (r22 == 0) goto L_0x04ec
            r1.attachPath = r9     // Catch:{ Exception -> 0x0530 }
            r36 = r7
            goto L_0x0509
        L_0x04ec:
            r36 = r7
        L_0x04ee:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r13.sizes     // Catch:{ Exception -> 0x0530 }
            int r22 = r7.size()     // Catch:{ Exception -> 0x0530 }
            r13 = 1
            int r15 = r22 + -1
            java.lang.Object r7 = r7.get(r15)     // Catch:{ Exception -> 0x0530 }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC$PhotoSize) r7     // Catch:{ Exception -> 0x0530 }
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.location     // Catch:{ Exception -> 0x0530 }
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r13)     // Catch:{ Exception -> 0x0530 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0530 }
            r1.attachPath = r7     // Catch:{ Exception -> 0x0530 }
        L_0x0509:
            r14 = 0
            r7 = r65
            r15 = r75
            r13 = r83
            goto L_0x055d
        L_0x0511:
            r7 = r67
            r9 = r72
            r13 = r83
            if (r7 == 0) goto L_0x053f
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0538 }
            r1.<init>()     // Catch:{ Exception -> 0x0538 }
            org.telegram.tgnet.TLRPC$TL_messageMediaGame r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x0530 }
            r15.<init>()     // Catch:{ Exception -> 0x0530 }
            r1.media = r15     // Catch:{ Exception -> 0x0530 }
            r15.game = r7     // Catch:{ Exception -> 0x0530 }
            if (r6 == 0) goto L_0x0558
            boolean r7 = r6.containsKey(r5)     // Catch:{ Exception -> 0x0530 }
            if (r7 == 0) goto L_0x0558
            goto L_0x0554
        L_0x0530:
            r0 = move-exception
            r8 = r59
            r14 = r82
            r5 = r1
            goto L_0x034c
        L_0x0538:
            r0 = move-exception
            r8 = r59
            r14 = r82
            goto L_0x1ce7
        L_0x053f:
            r7 = r69
            r15 = r82
            if (r7 == 0) goto L_0x0578
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0571 }
            r1.<init>()     // Catch:{ Exception -> 0x0571 }
            r1.media = r7     // Catch:{ Exception -> 0x056a }
            if (r6 == 0) goto L_0x0558
            boolean r7 = r6.containsKey(r5)     // Catch:{ Exception -> 0x056a }
            if (r7 == 0) goto L_0x0558
        L_0x0554:
            r14 = 0
            r19 = 9
            goto L_0x0559
        L_0x0558:
            r14 = 0
        L_0x0559:
            r7 = r65
            r15 = r75
        L_0x055d:
            r22 = r2
            r33 = r5
            r3 = r6
        L_0x0562:
            r5 = 3
            r6 = r59
            r2 = r1
            r1 = r18
            goto L_0x07c0
        L_0x056a:
            r0 = move-exception
            r8 = r59
            r5 = r1
            r14 = r15
            goto L_0x034c
        L_0x0571:
            r0 = move-exception
            r8 = r59
            r1 = r0
            r14 = r15
            goto L_0x1ce8
        L_0x0578:
            r7 = r65
            if (r7 == 0) goto L_0x05ff
            if (r8 == 0) goto L_0x0584
            org.telegram.tgnet.TLRPC$TL_message_secret r1 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0571 }
            r1.<init>()     // Catch:{ Exception -> 0x0571 }
            goto L_0x0589
        L_0x0584:
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0538 }
            r1.<init>()     // Catch:{ Exception -> 0x0538 }
        L_0x0589:
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r15 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact     // Catch:{ Exception -> 0x0530 }
            r15.<init>()     // Catch:{ Exception -> 0x0530 }
            r1.media = r15     // Catch:{ Exception -> 0x0530 }
            java.lang.String r14 = r7.phone     // Catch:{ Exception -> 0x0530 }
            r15.phone_number = r14     // Catch:{ Exception -> 0x0530 }
            java.lang.String r14 = r7.first_name     // Catch:{ Exception -> 0x0530 }
            r15.first_name = r14     // Catch:{ Exception -> 0x0530 }
            java.lang.String r14 = r7.last_name     // Catch:{ Exception -> 0x0530 }
            r15.last_name = r14     // Catch:{ Exception -> 0x0530 }
            r22 = r2
            long r2 = r7.id     // Catch:{ Exception -> 0x0530 }
            r15.user_id = r2     // Catch:{ Exception -> 0x0530 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r7.restriction_reason     // Catch:{ Exception -> 0x0530 }
            boolean r2 = r2.isEmpty()     // Catch:{ Exception -> 0x0530 }
            if (r2 != 0) goto L_0x05cd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r2 = r7.restriction_reason     // Catch:{ Exception -> 0x0530 }
            r3 = 0
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x0530 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r2 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r2     // Catch:{ Exception -> 0x0530 }
            java.lang.String r2 = r2.text     // Catch:{ Exception -> 0x0530 }
            java.lang.String r3 = "BEGIN:VCARD"
            boolean r2 = r2.startsWith(r3)     // Catch:{ Exception -> 0x0530 }
            if (r2 == 0) goto L_0x05cd
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0530 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r3 = r7.restriction_reason     // Catch:{ Exception -> 0x0530 }
            r14 = 0
            java.lang.Object r3 = r3.get(r14)     // Catch:{ Exception -> 0x0530 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r3 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r3     // Catch:{ Exception -> 0x0530 }
            java.lang.String r3 = r3.text     // Catch:{ Exception -> 0x0530 }
            r2.vcard = r3     // Catch:{ Exception -> 0x0530 }
            goto L_0x05d1
        L_0x05cd:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0530 }
            r2.vcard = r12     // Catch:{ Exception -> 0x0530 }
        L_0x05d1:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media     // Catch:{ Exception -> 0x0530 }
            java.lang.String r3 = r2.first_name     // Catch:{ Exception -> 0x0530 }
            if (r3 != 0) goto L_0x05db
            r2.first_name = r12     // Catch:{ Exception -> 0x0530 }
            r7.first_name = r12     // Catch:{ Exception -> 0x0530 }
        L_0x05db:
            java.lang.String r3 = r2.last_name     // Catch:{ Exception -> 0x0530 }
            if (r3 != 0) goto L_0x05e3
            r2.last_name = r12     // Catch:{ Exception -> 0x0530 }
            r7.last_name = r12     // Catch:{ Exception -> 0x0530 }
        L_0x05e3:
            if (r6 == 0) goto L_0x05ef
            boolean r2 = r6.containsKey(r5)     // Catch:{ Exception -> 0x0530 }
            if (r2 == 0) goto L_0x05ef
            r14 = 0
            r19 = 9
            goto L_0x05f2
        L_0x05ef:
            r14 = 0
            r19 = 6
        L_0x05f2:
            r15 = r75
            r2 = r1
            r33 = r5
            r3 = r6
            r1 = r18
            r5 = 3
            r18 = 0
            goto L_0x0451
        L_0x05ff:
            r22 = r2
            r3 = r6
            r2 = r66
            if (r2 == 0) goto L_0x07b5
            if (r8 == 0) goto L_0x060e
            org.telegram.tgnet.TLRPC$TL_message_secret r6 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0538 }
            r6.<init>()     // Catch:{ Exception -> 0x0538 }
            goto L_0x0613
        L_0x060e:
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0538 }
            r6.<init>()     // Catch:{ Exception -> 0x0538 }
        L_0x0613:
            boolean r14 = org.telegram.messenger.DialogObject.isChatDialog(r70)     // Catch:{ Exception -> 0x07a9 }
            if (r14 == 0) goto L_0x0641
            if (r1 != 0) goto L_0x0641
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r2.attributes     // Catch:{ Exception -> 0x0638 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0638 }
            r14 = 0
        L_0x0622:
            if (r14 >= r1) goto L_0x0641
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r15 = r2.attributes     // Catch:{ Exception -> 0x0638 }
            java.lang.Object r15 = r15.get(r14)     // Catch:{ Exception -> 0x0638 }
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x0638 }
            if (r15 == 0) goto L_0x0635
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r2.attributes     // Catch:{ Exception -> 0x0638 }
            r1.remove(r14)     // Catch:{ Exception -> 0x0638 }
            r1 = 1
            goto L_0x0642
        L_0x0635:
            int r14 = r14 + 1
            goto L_0x0622
        L_0x0638:
            r0 = move-exception
            r8 = r59
            r14 = r82
            r1 = r0
            r5 = r6
            goto L_0x1ce9
        L_0x0641:
            r1 = 0
        L_0x0642:
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r14 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x07a9 }
            r14.<init>()     // Catch:{ Exception -> 0x07a9 }
            r6.media = r14     // Catch:{ Exception -> 0x07a9 }
            int r15 = r14.flags     // Catch:{ Exception -> 0x07a9 }
            r19 = 3
            r15 = r15 | 3
            r14.flags = r15     // Catch:{ Exception -> 0x07a9 }
            if (r13 == 0) goto L_0x065d
            r14.ttl_seconds = r13     // Catch:{ Exception -> 0x0638 }
            r6.ttl = r13     // Catch:{ Exception -> 0x0638 }
            r31 = 4
            r15 = r15 | 4
            r14.flags = r15     // Catch:{ Exception -> 0x0638 }
        L_0x065d:
            r14.document = r2     // Catch:{ Exception -> 0x07a9 }
            if (r3 == 0) goto L_0x066a
            boolean r14 = r3.containsKey(r5)     // Catch:{ Exception -> 0x0638 }
            if (r14 == 0) goto L_0x066a
            r14 = 9
            goto L_0x068a
        L_0x066a:
            boolean r14 = org.telegram.messenger.MessageObject.isVideoSticker(r66)     // Catch:{ Exception -> 0x07a9 }
            if (r14 != 0) goto L_0x0680
            boolean r14 = org.telegram.messenger.MessageObject.isVideoDocument(r66)     // Catch:{ Exception -> 0x0638 }
            if (r14 != 0) goto L_0x067e
            boolean r14 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r66)     // Catch:{ Exception -> 0x0638 }
            if (r14 != 0) goto L_0x067e
            if (r64 == 0) goto L_0x0680
        L_0x067e:
            r14 = 3
            goto L_0x068a
        L_0x0680:
            boolean r14 = org.telegram.messenger.MessageObject.isVoiceDocument(r66)     // Catch:{ Exception -> 0x07a9 }
            if (r14 == 0) goto L_0x0689
            r14 = 8
            goto L_0x068a
        L_0x0689:
            r14 = 7
        L_0x068a:
            if (r64 == 0) goto L_0x069f
            java.lang.String r15 = r64.getString()     // Catch:{ Exception -> 0x0638 }
            if (r3 != 0) goto L_0x0697
            java.util.HashMap r3 = new java.util.HashMap     // Catch:{ Exception -> 0x0638 }
            r3.<init>()     // Catch:{ Exception -> 0x0638 }
        L_0x0697:
            r67 = r1
            java.lang.String r1 = "ve"
            r3.put(r1, r15)     // Catch:{ Exception -> 0x0638 }
            goto L_0x06a1
        L_0x069f:
            r67 = r1
        L_0x06a1:
            if (r8 == 0) goto L_0x06bf
            int r1 = r2.dc_id     // Catch:{ Exception -> 0x0638 }
            if (r1 <= 0) goto L_0x06bf
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r66)     // Catch:{ Exception -> 0x0638 }
            if (r1 != 0) goto L_0x06bf
            r1 = 1
            boolean r15 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r2, r1)     // Catch:{ Exception -> 0x0638 }
            if (r15 != 0) goto L_0x06bf
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r66)     // Catch:{ Exception -> 0x0638 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0638 }
            r6.attachPath = r1     // Catch:{ Exception -> 0x0638 }
            goto L_0x06c1
        L_0x06bf:
            r6.attachPath = r9     // Catch:{ Exception -> 0x07a9 }
        L_0x06c1:
            if (r8 == 0) goto L_0x0791
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r66)     // Catch:{ Exception -> 0x07a9 }
            if (r1 != 0) goto L_0x06d0
            r1 = 1
            boolean r15 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r2, r1)     // Catch:{ Exception -> 0x0638 }
            if (r15 == 0) goto L_0x0791
        L_0x06d0:
            r1 = 0
        L_0x06d1:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r15 = r2.attributes     // Catch:{ Exception -> 0x07a9 }
            int r15 = r15.size()     // Catch:{ Exception -> 0x07a9 }
            if (r1 >= r15) goto L_0x0791
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r15 = r2.attributes     // Catch:{ Exception -> 0x07a9 }
            java.lang.Object r15 = r15.get(r1)     // Catch:{ Exception -> 0x07a9 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r15 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r15     // Catch:{ Exception -> 0x07a9 }
            r69 = r3
            boolean r3 = r15 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker     // Catch:{ Exception -> 0x07a9 }
            if (r3 == 0) goto L_0x077b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r2.attributes     // Catch:{ Exception -> 0x07a9 }
            r3.remove(r1)     // Catch:{ Exception -> 0x07a9 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55 r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55     // Catch:{ Exception -> 0x07a9 }
            r1.<init>()     // Catch:{ Exception -> 0x07a9 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r2.attributes     // Catch:{ Exception -> 0x07a9 }
            r3.add(r1)     // Catch:{ Exception -> 0x07a9 }
            java.lang.String r3 = r15.alt     // Catch:{ Exception -> 0x07a9 }
            r1.alt = r3     // Catch:{ Exception -> 0x07a9 }
            org.telegram.tgnet.TLRPC$InputStickerSet r3 = r15.stickerset     // Catch:{ Exception -> 0x07a9 }
            if (r3 == 0) goto L_0x0765
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x07a9 }
            if (r2 == 0) goto L_0x0709
            java.lang.String r2 = r3.short_name     // Catch:{ Exception -> 0x0638 }
            r33 = r5
            r37 = r6
            goto L_0x0719
        L_0x0709:
            org.telegram.messenger.MediaDataController r2 = r59.getMediaDataController()     // Catch:{ Exception -> 0x07a9 }
            org.telegram.tgnet.TLRPC$InputStickerSet r3 = r15.stickerset     // Catch:{ Exception -> 0x07a9 }
            r33 = r5
            r37 = r6
            long r5 = r3.id     // Catch:{ Exception -> 0x0763 }
            java.lang.String r2 = r2.getStickerSetName(r5)     // Catch:{ Exception -> 0x0763 }
        L_0x0719:
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0763 }
            if (r3 != 0) goto L_0x072e
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x0763 }
            r3.<init>()     // Catch:{ Exception -> 0x0763 }
            r1.stickerset = r3     // Catch:{ Exception -> 0x0763 }
            r3.short_name = r2     // Catch:{ Exception -> 0x0763 }
            r2 = 0
            r3 = 5
            r5 = 3
            r6 = r59
            goto L_0x0754
        L_0x072e:
            org.telegram.tgnet.TLRPC$InputStickerSet r2 = r15.stickerset     // Catch:{ Exception -> 0x0763 }
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetID     // Catch:{ Exception -> 0x0763 }
            if (r2 == 0) goto L_0x0748
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0763 }
            r5 = 3
            r6 = r59
            r2.<init>(r10)     // Catch:{ Exception -> 0x0775 }
            r2.encryptedChat = r8     // Catch:{ Exception -> 0x0775 }
            r2.locationParent = r1     // Catch:{ Exception -> 0x0775 }
            r3 = 5
            r2.type = r3     // Catch:{ Exception -> 0x0775 }
            org.telegram.tgnet.TLRPC$InputStickerSet r15 = r15.stickerset     // Catch:{ Exception -> 0x0775 }
            r2.parentObject = r15     // Catch:{ Exception -> 0x0775 }
            goto L_0x074d
        L_0x0748:
            r3 = 5
            r5 = 3
            r6 = r59
            r2 = 0
        L_0x074d:
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r15 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0775 }
            r15.<init>()     // Catch:{ Exception -> 0x0775 }
            r1.stickerset = r15     // Catch:{ Exception -> 0x0775 }
        L_0x0754:
            r3 = r69
            r15 = r75
            r19 = r14
            r1 = r18
            r18 = r67
            r14 = r2
            r2 = r37
            goto L_0x07c2
        L_0x0763:
            r0 = move-exception
            goto L_0x07ac
        L_0x0765:
            r3 = 5
            r33 = r5
            r37 = r6
            r5 = 3
            r6 = r59
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0775 }
            r2.<init>()     // Catch:{ Exception -> 0x0775 }
            r1.stickerset = r2     // Catch:{ Exception -> 0x0775 }
            goto L_0x079b
        L_0x0775:
            r0 = move-exception
            r14 = r82
            r1 = r0
            r8 = r6
            goto L_0x07b1
        L_0x077b:
            r3 = 5
            r33 = r5
            r37 = r6
            r5 = 3
            r6 = r59
            int r1 = r1 + 1
            r2 = r66
            r3 = r69
            r5 = r33
            r6 = r37
            r19 = 3
            goto L_0x06d1
        L_0x0791:
            r69 = r3
            r33 = r5
            r37 = r6
            r3 = 5
            r5 = 3
            r6 = r59
        L_0x079b:
            r3 = r69
            r15 = r75
            r19 = r14
            r1 = r18
            r2 = r37
            r14 = 0
            r18 = r67
            goto L_0x07c2
        L_0x07a9:
            r0 = move-exception
            r37 = r6
        L_0x07ac:
            r8 = r59
            r14 = r82
            r1 = r0
        L_0x07b1:
            r5 = r37
            goto L_0x1ce9
        L_0x07b5:
            r6 = r59
            r33 = r5
            r5 = 3
            r15 = r75
            r1 = r18
            r2 = 0
            r14 = 0
        L_0x07c0:
            r18 = 0
        L_0x07c2:
            if (r4 == 0) goto L_0x07db
            boolean r37 = r78.isEmpty()     // Catch:{ Exception -> 0x07d3 }
            if (r37 != 0) goto L_0x07db
            r2.entities = r4     // Catch:{ Exception -> 0x07d3 }
            int r5 = r2.flags     // Catch:{ Exception -> 0x07d3 }
            r5 = r5 | 128(0x80, float:1.794E-43)
            r2.flags = r5     // Catch:{ Exception -> 0x07d3 }
            goto L_0x07db
        L_0x07d3:
            r0 = move-exception
            r14 = r82
            r1 = r0
            r5 = r2
            r8 = r6
            goto L_0x1ce9
        L_0x07db:
            if (r1 == 0) goto L_0x07e0
            r2.message = r1     // Catch:{ Exception -> 0x07d3 }
            goto L_0x07e6
        L_0x07e0:
            java.lang.String r5 = r2.message     // Catch:{ Exception -> 0x1cd9 }
            if (r5 != 0) goto L_0x07e6
            r2.message = r12     // Catch:{ Exception -> 0x07d3 }
        L_0x07e6:
            java.lang.String r5 = r2.attachPath     // Catch:{ Exception -> 0x1cd9 }
            if (r5 != 0) goto L_0x07ec
            r2.attachPath = r12     // Catch:{ Exception -> 0x07d3 }
        L_0x07ec:
            org.telegram.messenger.UserConfig r5 = r59.getUserConfig()     // Catch:{ Exception -> 0x1cd9 }
            int r5 = r5.getNewMessageId()     // Catch:{ Exception -> 0x1cd9 }
            r2.id = r5     // Catch:{ Exception -> 0x1cd9 }
            r2.local_id = r5     // Catch:{ Exception -> 0x1cd9 }
            r5 = 1
            r2.out = r5     // Catch:{ Exception -> 0x1cd9 }
            if (r25 == 0) goto L_0x0811
            if (r32 == 0) goto L_0x0811
            org.telegram.tgnet.TLRPC$TL_peerChannel r5 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ Exception -> 0x07d3 }
            r5.<init>()     // Catch:{ Exception -> 0x07d3 }
            r2.from_id = r5     // Catch:{ Exception -> 0x07d3 }
            r69 = r1
            r75 = r3
            r1 = r32
            long r3 = r1.channel_id     // Catch:{ Exception -> 0x07d3 }
            r5.channel_id = r3     // Catch:{ Exception -> 0x07d3 }
            goto L_0x081d
        L_0x0811:
            r69 = r1
            r75 = r3
            r1 = r32
            if (r27 == 0) goto L_0x0820
            r3 = r27
            r2.from_id = r3     // Catch:{ Exception -> 0x07d3 }
        L_0x081d:
            r4 = r22
            goto L_0x0831
        L_0x0820:
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x1cd9 }
            r3.<init>()     // Catch:{ Exception -> 0x1cd9 }
            r2.from_id = r3     // Catch:{ Exception -> 0x1cd9 }
            r4 = r22
            r3.user_id = r4     // Catch:{ Exception -> 0x1cd9 }
            int r3 = r2.flags     // Catch:{ Exception -> 0x1cd9 }
            r3 = r3 | 256(0x100, float:3.59E-43)
            r2.flags = r3     // Catch:{ Exception -> 0x1cd9 }
        L_0x0831:
            org.telegram.messenger.UserConfig r3 = r59.getUserConfig()     // Catch:{ Exception -> 0x1cd9 }
            r22 = r2
            r2 = 0
            r3.saveConfig(r2)     // Catch:{ Exception -> 0x1cd5 }
            r39 = r66
            r40 = r68
            r42 = r69
            r2 = r75
            r3 = r84
            r41 = r15
            r15 = r19
            r9 = r22
            r22 = r62
            r62 = r63
            r19 = r18
            r18 = r14
            r14 = r81
            r58 = r13
            r13 = r60
            r60 = r7
            r7 = r58
        L_0x085d:
            if (r14 == 0) goto L_0x088e
            r63 = r3
            int r3 = r6.currentAccount     // Catch:{ Exception -> 0x0886 }
            android.content.SharedPreferences r3 = org.telegram.messenger.MessagesController.getNotificationsSettings(r3)     // Catch:{ Exception -> 0x0886 }
            r65 = r13
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0886 }
            r13.<init>()     // Catch:{ Exception -> 0x0886 }
            r69 = r15
            java.lang.String r15 = "silent_"
            r13.append(r15)     // Catch:{ Exception -> 0x0886 }
            r13.append(r10)     // Catch:{ Exception -> 0x0886 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0886 }
            r15 = 0
            boolean r3 = r3.getBoolean(r13, r15)     // Catch:{ Exception -> 0x0886 }
            if (r3 == 0) goto L_0x0884
            goto L_0x0894
        L_0x0884:
            r3 = 0
            goto L_0x0895
        L_0x0886:
            r0 = move-exception
            r14 = r82
        L_0x0889:
            r1 = r0
            r8 = r6
            r5 = r9
            goto L_0x1ce9
        L_0x088e:
            r63 = r3
            r65 = r13
            r69 = r15
        L_0x0894:
            r3 = 1
        L_0x0895:
            r9.silent = r3     // Catch:{ Exception -> 0x1ccd }
            long r13 = r9.random_id     // Catch:{ Exception -> 0x1ccd }
            int r3 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x08a3
            long r13 = r59.getNextRandomId()     // Catch:{ Exception -> 0x0886 }
            r9.random_id = r13     // Catch:{ Exception -> 0x0886 }
        L_0x08a3:
            java.lang.String r13 = "bot_name"
            if (r2 == 0) goto L_0x08d7
            java.lang.String r3 = "bot"
            boolean r3 = r2.containsKey(r3)     // Catch:{ Exception -> 0x0886 }
            if (r3 == 0) goto L_0x08d7
            if (r8 == 0) goto L_0x08be
            java.lang.Object r3 = r2.get(r13)     // Catch:{ Exception -> 0x0886 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0886 }
            r9.via_bot_name = r3     // Catch:{ Exception -> 0x0886 }
            if (r3 != 0) goto L_0x08d1
            r9.via_bot_name = r12     // Catch:{ Exception -> 0x0886 }
            goto L_0x08d1
        L_0x08be:
            java.lang.String r3 = "bot"
            java.lang.Object r3 = r2.get(r3)     // Catch:{ Exception -> 0x0886 }
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3     // Catch:{ Exception -> 0x0886 }
            java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x0886 }
            int r3 = r3.intValue()     // Catch:{ Exception -> 0x0886 }
            long r14 = (long) r3     // Catch:{ Exception -> 0x0886 }
            r9.via_bot_id = r14     // Catch:{ Exception -> 0x0886 }
        L_0x08d1:
            int r3 = r9.flags     // Catch:{ Exception -> 0x0886 }
            r3 = r3 | 2048(0x800, float:2.87E-42)
            r9.flags = r3     // Catch:{ Exception -> 0x0886 }
        L_0x08d7:
            r9.params = r2     // Catch:{ Exception -> 0x1ccd }
            r3 = r77
            if (r3 == 0) goto L_0x08e9
            boolean r14 = r3.resendAsIs     // Catch:{ Exception -> 0x0886 }
            if (r14 != 0) goto L_0x08e2
            goto L_0x08e9
        L_0x08e2:
            r14 = r82
            r37 = r12
            r66 = r13
            goto L_0x0941
        L_0x08e9:
            r14 = r82
            if (r14 == 0) goto L_0x08ef
            r15 = r14
            goto L_0x08f7
        L_0x08ef:
            org.telegram.tgnet.ConnectionsManager r15 = r59.getConnectionsManager()     // Catch:{ Exception -> 0x1ccb }
            int r15 = r15.getCurrentTime()     // Catch:{ Exception -> 0x1ccb }
        L_0x08f7:
            r9.date = r15     // Catch:{ Exception -> 0x1ccb }
            boolean r15 = r1 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x1ccb }
            if (r15 == 0) goto L_0x093a
            if (r14 != 0) goto L_0x090a
            if (r25 == 0) goto L_0x090a
            r15 = 1
            r9.views = r15     // Catch:{ Exception -> 0x0937 }
            int r15 = r9.flags     // Catch:{ Exception -> 0x0937 }
            r15 = r15 | 1024(0x400, float:1.435E-42)
            r9.flags = r15     // Catch:{ Exception -> 0x0937 }
        L_0x090a:
            org.telegram.messenger.MessagesController r15 = r59.getMessagesController()     // Catch:{ Exception -> 0x0937 }
            r37 = r12
            r66 = r13
            long r12 = r1.channel_id     // Catch:{ Exception -> 0x0937 }
            java.lang.Long r12 = java.lang.Long.valueOf(r12)     // Catch:{ Exception -> 0x0937 }
            org.telegram.tgnet.TLRPC$Chat r12 = r15.getChat(r12)     // Catch:{ Exception -> 0x0937 }
            if (r12 == 0) goto L_0x0941
            boolean r13 = r12.megagroup     // Catch:{ Exception -> 0x0937 }
            if (r13 == 0) goto L_0x0926
            r13 = 1
            r9.unread = r13     // Catch:{ Exception -> 0x0937 }
            goto L_0x0941
        L_0x0926:
            r13 = 1
            r9.post = r13     // Catch:{ Exception -> 0x0937 }
            boolean r12 = r12.signatures     // Catch:{ Exception -> 0x0937 }
            if (r12 == 0) goto L_0x0941
            org.telegram.tgnet.TLRPC$TL_peerUser r12 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x0937 }
            r12.<init>()     // Catch:{ Exception -> 0x0937 }
            r9.from_id = r12     // Catch:{ Exception -> 0x0937 }
            r12.user_id = r4     // Catch:{ Exception -> 0x0937 }
            goto L_0x0941
        L_0x0937:
            r0 = move-exception
            goto L_0x0889
        L_0x093a:
            r37 = r12
            r66 = r13
            r12 = 1
            r9.unread = r12     // Catch:{ Exception -> 0x1ccb }
        L_0x0941:
            int r12 = r9.flags     // Catch:{ Exception -> 0x1ccb }
            r12 = r12 | 512(0x200, float:7.175E-43)
            r9.flags = r12     // Catch:{ Exception -> 0x1ccb }
            r9.dialog_id = r10     // Catch:{ Exception -> 0x1ccb }
            r12 = r73
            r13 = r78
            if (r12 == 0) goto L_0x0993
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r15 = new org.telegram.tgnet.TLRPC$TL_messageReplyHeader     // Catch:{ Exception -> 0x0886 }
            r15.<init>()     // Catch:{ Exception -> 0x0886 }
            r9.reply_to = r15     // Catch:{ Exception -> 0x0886 }
            if (r8 == 0) goto L_0x096c
            r32 = r1
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner     // Catch:{ Exception -> 0x0886 }
            long r13 = r1.random_id     // Catch:{ Exception -> 0x0886 }
            int r1 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x096e
            r15.reply_to_random_id = r13     // Catch:{ Exception -> 0x0886 }
            int r1 = r9.flags     // Catch:{ Exception -> 0x0886 }
            r13 = 8
            r1 = r1 | r13
            r9.flags = r1     // Catch:{ Exception -> 0x0886 }
            goto L_0x0975
        L_0x096c:
            r32 = r1
        L_0x096e:
            int r1 = r9.flags     // Catch:{ Exception -> 0x0886 }
            r13 = 8
            r1 = r1 | r13
            r9.flags = r1     // Catch:{ Exception -> 0x0886 }
        L_0x0975:
            int r1 = r73.getId()     // Catch:{ Exception -> 0x0886 }
            r15.reply_to_msg_id = r1     // Catch:{ Exception -> 0x0886 }
            r1 = r74
            r13 = r3
            if (r1 == 0) goto L_0x0998
            if (r1 == r12) goto L_0x0998
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r9.reply_to     // Catch:{ Exception -> 0x0886 }
            int r14 = r74.getId()     // Catch:{ Exception -> 0x0886 }
            r3.reply_to_top_id = r14     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r9.reply_to     // Catch:{ Exception -> 0x0886 }
            int r14 = r3.flags     // Catch:{ Exception -> 0x0886 }
            r15 = 2
            r14 = r14 | r15
            r3.flags = r14     // Catch:{ Exception -> 0x0886 }
            goto L_0x0998
        L_0x0993:
            r32 = r1
            r13 = r3
            r1 = r74
        L_0x0998:
            r14 = r29
            int r3 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x09b6
            org.telegram.tgnet.TLRPC$TL_messageReplies r3 = new org.telegram.tgnet.TLRPC$TL_messageReplies     // Catch:{ Exception -> 0x0886 }
            r3.<init>()     // Catch:{ Exception -> 0x0886 }
            r9.replies = r3     // Catch:{ Exception -> 0x0886 }
            r12 = 1
            r3.comments = r12     // Catch:{ Exception -> 0x0886 }
            r3.channel_id = r14     // Catch:{ Exception -> 0x0886 }
            int r14 = r3.flags     // Catch:{ Exception -> 0x0886 }
            r14 = r14 | r12
            r3.flags = r14     // Catch:{ Exception -> 0x0886 }
            int r3 = r9.flags     // Catch:{ Exception -> 0x0886 }
            r12 = 8388608(0x800000, float:1.17549435E-38)
            r3 = r3 | r12
            r9.flags = r3     // Catch:{ Exception -> 0x0886 }
        L_0x09b6:
            r3 = r79
            if (r3 == 0) goto L_0x09c4
            if (r8 != 0) goto L_0x09c4
            int r12 = r9.flags     // Catch:{ Exception -> 0x0886 }
            r12 = r12 | 64
            r9.flags = r12     // Catch:{ Exception -> 0x0886 }
            r9.reply_markup = r3     // Catch:{ Exception -> 0x0886 }
        L_0x09c4:
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r70)     // Catch:{ Exception -> 0x1ccd }
            if (r3 != 0) goto L_0x09fa
            org.telegram.messenger.MessagesController r3 = r59.getMessagesController()     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$Peer r3 = r3.getPeer(r10)     // Catch:{ Exception -> 0x0886 }
            r9.peer_id = r3     // Catch:{ Exception -> 0x0886 }
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r70)     // Catch:{ Exception -> 0x0886 }
            if (r3 == 0) goto L_0x09f6
            org.telegram.messenger.MessagesController r3 = r59.getMessagesController()     // Catch:{ Exception -> 0x0886 }
            java.lang.Long r4 = java.lang.Long.valueOf(r70)     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)     // Catch:{ Exception -> 0x0886 }
            if (r3 != 0) goto L_0x09ee
            int r1 = r9.id     // Catch:{ Exception -> 0x0886 }
            r6.processSentMessage(r1)     // Catch:{ Exception -> 0x0886 }
            return
        L_0x09ee:
            boolean r3 = r3.bot     // Catch:{ Exception -> 0x0886 }
            if (r3 == 0) goto L_0x09f6
            r3 = 0
            r9.unread = r3     // Catch:{ Exception -> 0x0886 }
            goto L_0x09f7
        L_0x09f6:
            r3 = 0
        L_0x09f7:
            r12 = 4
            goto L_0x0aa0
        L_0x09fa:
            r3 = 0
            org.telegram.tgnet.TLRPC$TL_peerUser r12 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x1ccd }
            r12.<init>()     // Catch:{ Exception -> 0x1ccd }
            r9.peer_id = r12     // Catch:{ Exception -> 0x1ccd }
            long r14 = r8.participant_id     // Catch:{ Exception -> 0x1ccd }
            int r23 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
            if (r23 != 0) goto L_0x0a0d
            long r4 = r8.admin_id     // Catch:{ Exception -> 0x0886 }
            r12.user_id = r4     // Catch:{ Exception -> 0x0886 }
            goto L_0x0a0f
        L_0x0a0d:
            r12.user_id = r14     // Catch:{ Exception -> 0x1ccd }
        L_0x0a0f:
            if (r7 == 0) goto L_0x0a15
            r9.ttl = r7     // Catch:{ Exception -> 0x0886 }
        L_0x0a13:
            r12 = 4
            goto L_0x0a27
        L_0x0a15:
            int r4 = r8.ttl     // Catch:{ Exception -> 0x1ccd }
            r9.ttl = r4     // Catch:{ Exception -> 0x1ccd }
            if (r4 == 0) goto L_0x0a13
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media     // Catch:{ Exception -> 0x0886 }
            if (r5 == 0) goto L_0x0a13
            r5.ttl_seconds = r4     // Catch:{ Exception -> 0x0886 }
            int r4 = r5.flags     // Catch:{ Exception -> 0x0886 }
            r12 = 4
            r4 = r4 | r12
            r5.flags = r4     // Catch:{ Exception -> 0x0886 }
        L_0x0a27:
            int r4 = r9.ttl     // Catch:{ Exception -> 0x1ccd }
            if (r4 == 0) goto L_0x0aa0
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r9.media     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ Exception -> 0x0886 }
            if (r4 == 0) goto L_0x0aa0
            boolean r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r9)     // Catch:{ Exception -> 0x0886 }
            if (r4 == 0) goto L_0x0a66
            r4 = 0
        L_0x0a38:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0886 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0886 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0886 }
            if (r4 >= r5) goto L_0x0a5a
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0886 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0886 }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r5     // Catch:{ Exception -> 0x0886 }
            boolean r14 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ Exception -> 0x0886 }
            if (r14 == 0) goto L_0x0a57
            int r4 = r5.duration     // Catch:{ Exception -> 0x0886 }
            goto L_0x0a5b
        L_0x0a57:
            int r4 = r4 + 1
            goto L_0x0a38
        L_0x0a5a:
            r4 = 0
        L_0x0a5b:
            int r5 = r9.ttl     // Catch:{ Exception -> 0x0886 }
            r14 = 1
            int r4 = r4 + r14
            int r4 = java.lang.Math.max(r5, r4)     // Catch:{ Exception -> 0x0886 }
            r9.ttl = r4     // Catch:{ Exception -> 0x0886 }
            goto L_0x0aa0
        L_0x0a66:
            boolean r4 = org.telegram.messenger.MessageObject.isVideoMessage(r9)     // Catch:{ Exception -> 0x0886 }
            if (r4 != 0) goto L_0x0a72
            boolean r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r9)     // Catch:{ Exception -> 0x0886 }
            if (r4 == 0) goto L_0x0aa0
        L_0x0a72:
            r4 = 0
        L_0x0a73:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0886 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0886 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0886 }
            if (r4 >= r5) goto L_0x0a95
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0886 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0886 }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ Exception -> 0x0886 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r5     // Catch:{ Exception -> 0x0886 }
            boolean r14 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x0886 }
            if (r14 == 0) goto L_0x0a92
            int r4 = r5.duration     // Catch:{ Exception -> 0x0886 }
            goto L_0x0a96
        L_0x0a92:
            int r4 = r4 + 1
            goto L_0x0a73
        L_0x0a95:
            r4 = 0
        L_0x0a96:
            int r5 = r9.ttl     // Catch:{ Exception -> 0x0886 }
            r14 = 1
            int r4 = r4 + r14
            int r4 = java.lang.Math.max(r5, r4)     // Catch:{ Exception -> 0x0886 }
            r9.ttl = r4     // Catch:{ Exception -> 0x0886 }
        L_0x0aa0:
            boolean r4 = org.telegram.messenger.MessageObject.isVoiceMessage(r9)     // Catch:{ Exception -> 0x1ccd }
            if (r4 != 0) goto L_0x0aac
            boolean r4 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r9)     // Catch:{ Exception -> 0x0886 }
            if (r4 == 0) goto L_0x0aaf
        L_0x0aac:
            r4 = 1
            r9.media_unread = r4     // Catch:{ Exception -> 0x1ccd }
        L_0x0aaf:
            org.telegram.tgnet.TLRPC$Peer r4 = r9.from_id     // Catch:{ Exception -> 0x1ccd }
            if (r4 != 0) goto L_0x0ab7
            org.telegram.tgnet.TLRPC$Peer r4 = r9.peer_id     // Catch:{ Exception -> 0x0886 }
            r9.from_id = r4     // Catch:{ Exception -> 0x0886 }
        L_0x0ab7:
            r4 = 1
            r9.send_state = r4     // Catch:{ Exception -> 0x1ccd }
            if (r2 == 0) goto L_0x0ae9
            java.lang.String r5 = "groupId"
            java.lang.Object r5 = r2.get(r5)     // Catch:{ Exception -> 0x0886 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x0886 }
            if (r5 == 0) goto L_0x0ad9
            java.lang.Long r5 = org.telegram.messenger.Utilities.parseLong(r5)     // Catch:{ Exception -> 0x0886 }
            long r14 = r5.longValue()     // Catch:{ Exception -> 0x0886 }
            r9.grouped_id = r14     // Catch:{ Exception -> 0x0886 }
            int r5 = r9.flags     // Catch:{ Exception -> 0x0886 }
            r21 = 131072(0x20000, float:1.83671E-40)
            r5 = r5 | r21
            r9.flags = r5     // Catch:{ Exception -> 0x0886 }
            goto L_0x0adb
        L_0x0ad9:
            r14 = r16
        L_0x0adb:
            java.lang.String r5 = "final"
            java.lang.Object r5 = r2.get(r5)     // Catch:{ Exception -> 0x0886 }
            if (r5 == 0) goto L_0x0ae5
            r5 = 1
            goto L_0x0ae6
        L_0x0ae5:
            r5 = 0
        L_0x0ae6:
            r12 = r14
            r14 = r5
            goto L_0x0aec
        L_0x0ae9:
            r12 = r16
            r14 = 0
        L_0x0aec:
            org.telegram.messenger.MessageObject r15 = new org.telegram.messenger.MessageObject     // Catch:{ Exception -> 0x1ccd }
            int r5 = r6.currentAccount     // Catch:{ Exception -> 0x1ccd }
            r21 = 1
            r23 = 1
            r43 = r2
            r2 = r15
            r68 = r63
            r44 = r34
            r4 = 0
            r3 = r5
            r5 = r36
            r4 = r9
            r47 = r5
            r46 = r20
            r45 = r32
            r48 = r33
            r5 = r73
            r1 = r6
            r6 = r21
            r50 = r60
            r49 = r7
            r60 = r14
            r51 = r35
            r14 = 0
            r7 = r23
            r2.<init>((int) r3, (org.telegram.tgnet.TLRPC$Message) r4, (org.telegram.messenger.MessageObject) r5, (boolean) r6, (boolean) r7)     // Catch:{ Exception -> 0x1cc6 }
            r2 = r85
            r15.sendAnimationData = r2     // Catch:{ Exception -> 0x1cbe }
            r2 = 1
            r15.wasJustSent = r2     // Catch:{ Exception -> 0x1cbe }
            r3 = r82
            if (r3 == 0) goto L_0x0b28
            r4 = 1
            goto L_0x0b29
        L_0x0b28:
            r4 = 0
        L_0x0b29:
            r15.scheduled = r4     // Catch:{ Exception -> 0x1cba }
            boolean r4 = r15.isForwarded()     // Catch:{ Exception -> 0x1cba }
            if (r4 != 0) goto L_0x0b4e
            int r4 = r15.type     // Catch:{ Exception -> 0x0b48 }
            r5 = 3
            if (r4 == r5) goto L_0x0b3c
            if (r64 != 0) goto L_0x0b3c
            r6 = 2
            if (r4 != r6) goto L_0x0b50
            goto L_0x0b3d
        L_0x0b3c:
            r6 = 2
        L_0x0b3d:
            java.lang.String r4 = r9.attachPath     // Catch:{ Exception -> 0x0b48 }
            boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0b48 }
            if (r4 != 0) goto L_0x0b50
            r15.attachPathExists = r2     // Catch:{ Exception -> 0x0b48 }
            goto L_0x0b50
        L_0x0b48:
            r0 = move-exception
            r8 = r1
            r14 = r3
            r5 = r9
            goto L_0x034d
        L_0x0b4e:
            r5 = 3
            r6 = 2
        L_0x0b50:
            org.telegram.messenger.VideoEditedInfo r4 = r15.videoEditedInfo     // Catch:{ Exception -> 0x1cba }
            if (r4 == 0) goto L_0x0b57
            if (r64 != 0) goto L_0x0b57
            goto L_0x0b59
        L_0x0b57:
            r4 = r64
        L_0x0b59:
            int r7 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r7 != 0) goto L_0x0baa
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x0b48 }
            r7.<init>()     // Catch:{ Exception -> 0x0b48 }
            r7.add(r15)     // Catch:{ Exception -> 0x0b48 }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x0b48 }
            r6.<init>()     // Catch:{ Exception -> 0x0b48 }
            r6.add(r9)     // Catch:{ Exception -> 0x0b48 }
            int r5 = r1.currentAccount     // Catch:{ Exception -> 0x0b48 }
            org.telegram.messenger.MessagesStorage r23 = org.telegram.messenger.MessagesStorage.getInstance(r5)     // Catch:{ Exception -> 0x0b48 }
            r25 = 0
            r26 = 1
            r27 = 0
            r28 = 0
            if (r3 == 0) goto L_0x0b80
            r29 = 1
            goto L_0x0b82
        L_0x0b80:
            r29 = 0
        L_0x0b82:
            r24 = r6
            r23.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r24, (boolean) r25, (boolean) r26, (boolean) r27, (int) r28, (boolean) r29)     // Catch:{ Exception -> 0x0b48 }
            int r5 = r1.currentAccount     // Catch:{ Exception -> 0x0b48 }
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)     // Catch:{ Exception -> 0x0b48 }
            if (r3 == 0) goto L_0x0b91
            r6 = 1
            goto L_0x0b92
        L_0x0b91:
            r6 = 0
        L_0x0b92:
            r5.updateInterfaceWithMessages(r10, r7, r6)     // Catch:{ Exception -> 0x0b48 }
            if (r3 != 0) goto L_0x0ba6
            int r5 = r1.currentAccount     // Catch:{ Exception -> 0x0b48 }
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)     // Catch:{ Exception -> 0x0b48 }
            int r6 = org.telegram.messenger.NotificationCenter.dialogsNeedReload     // Catch:{ Exception -> 0x0b48 }
            r7 = 0
            java.lang.Object[] r2 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0b48 }
            r5.postNotificationName(r6, r2)     // Catch:{ Exception -> 0x0b48 }
            goto L_0x0ba7
        L_0x0ba6:
            r7 = 0
        L_0x0ba7:
            r2 = r18
            goto L_0x0bf2
        L_0x0baa:
            r7 = 0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1cba }
            r2.<init>()     // Catch:{ Exception -> 0x1cba }
            java.lang.String r5 = "group_"
            r2.append(r5)     // Catch:{ Exception -> 0x1cba }
            r2.append(r12)     // Catch:{ Exception -> 0x1cba }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x1cba }
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r5 = r1.delayedMessages     // Catch:{ Exception -> 0x1cba }
            java.lang.Object r2 = r5.get(r2)     // Catch:{ Exception -> 0x1cba }
            java.util.ArrayList r2 = (java.util.ArrayList) r2     // Catch:{ Exception -> 0x1cba }
            if (r2 == 0) goto L_0x0bce
            java.lang.Object r2 = r2.get(r7)     // Catch:{ Exception -> 0x0b48 }
            r18 = r2
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r18 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r18     // Catch:{ Exception -> 0x0b48 }
        L_0x0bce:
            if (r18 != 0) goto L_0x0be2
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0b48 }
            r2.<init>(r10)     // Catch:{ Exception -> 0x0b48 }
            r2.initForGroup(r12)     // Catch:{ Exception -> 0x0b48 }
            r2.encryptedChat = r8     // Catch:{ Exception -> 0x0b48 }
            if (r3 == 0) goto L_0x0bde
            r5 = 1
            goto L_0x0bdf
        L_0x0bde:
            r5 = 0
        L_0x0bdf:
            r2.scheduled = r5     // Catch:{ Exception -> 0x0b48 }
            goto L_0x0be4
        L_0x0be2:
            r2 = r18
        L_0x0be4:
            r2.performMediaUpload = r7     // Catch:{ Exception -> 0x1cba }
            r2.photoSize = r14     // Catch:{ Exception -> 0x1cba }
            r2.videoEditedInfo = r14     // Catch:{ Exception -> 0x1cba }
            r2.httpLocation = r14     // Catch:{ Exception -> 0x1cba }
            if (r60 == 0) goto L_0x0bf2
            int r5 = r9.id     // Catch:{ Exception -> 0x0b48 }
            r2.finalGroupMessage = r5     // Catch:{ Exception -> 0x0b48 }
        L_0x0bf2:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1cba }
            if (r5 == 0) goto L_0x0CLASSNAME
            r5 = r45
            if (r5 == 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c5e }
            r6.<init>()     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r14 = "send message user_id = "
            r6.append(r14)     // Catch:{ Exception -> 0x0c5e }
            r14 = r8
            long r7 = r5.user_id     // Catch:{ Exception -> 0x0c5e }
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r7 = " chat_id = "
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            long r7 = r5.chat_id     // Catch:{ Exception -> 0x0c5e }
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r7 = " channel_id = "
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            long r7 = r5.channel_id     // Catch:{ Exception -> 0x0c5e }
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r7 = " access_hash = "
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            long r7 = r5.access_hash     // Catch:{ Exception -> 0x0c5e }
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r7 = " notify = "
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            r7 = r81
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r7 = " silent = "
            r6.append(r7)     // Catch:{ Exception -> 0x0c5e }
            int r7 = r1.currentAccount     // Catch:{ Exception -> 0x0c5e }
            android.content.SharedPreferences r7 = org.telegram.messenger.MessagesController.getNotificationsSettings(r7)     // Catch:{ Exception -> 0x0c5e }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c5e }
            r8.<init>()     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r1 = "silent_"
            r8.append(r1)     // Catch:{ Exception -> 0x0c5e }
            r8.append(r10)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r1 = r8.toString()     // Catch:{ Exception -> 0x0c5e }
            r8 = 0
            boolean r1 = r7.getBoolean(r1, r8)     // Catch:{ Exception -> 0x0c5e }
            r6.append(r1)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r1 = r6.toString()     // Catch:{ Exception -> 0x0c5e }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0c5e }
            goto L_0x0c6b
        L_0x0c5e:
            r0 = move-exception
            r8 = r59
        L_0x0CLASSNAME:
            r1 = r0
            r14 = r3
        L_0x0CLASSNAME:
            r5 = r9
            goto L_0x1cea
        L_0x0CLASSNAME:
            r14 = r8
            goto L_0x0c6b
        L_0x0CLASSNAME:
            r14 = r8
            r5 = r45
        L_0x0c6b:
            if (r69 == 0) goto L_0x1b83
            r1 = r69
            r6 = 9
            if (r1 != r6) goto L_0x0CLASSNAME
            if (r65 == 0) goto L_0x0CLASSNAME
            if (r14 == 0) goto L_0x0CLASSNAME
            goto L_0x1b83
        L_0x0CLASSNAME:
            r6 = 1
            if (r1 < r6) goto L_0x0c8d
            r6 = 3
            if (r1 <= r6) goto L_0x0CLASSNAME
            goto L_0x0c8d
        L_0x0CLASSNAME:
            r8 = r59
            r52 = r12
            r13 = r43
            r6 = 4
            r7 = 10
        L_0x0CLASSNAME:
            r12 = r77
            goto L_0x0e46
        L_0x0c8d:
            r6 = 5
            if (r1 < r6) goto L_0x0CLASSNAME
            r7 = 8
            if (r1 <= r7) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r7 = 9
            if (r1 != r7) goto L_0x0c9a
            if (r14 != 0) goto L_0x0CLASSNAME
        L_0x0c9a:
            r7 = 10
            if (r1 == r7) goto L_0x0e3d
            r8 = 11
            if (r1 != r8) goto L_0x0ca4
            goto L_0x0e3d
        L_0x0ca4:
            r6 = 4
            if (r1 != r6) goto L_0x0d92
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r1 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages     // Catch:{ Exception -> 0x0c5e }
            r1.<init>()     // Catch:{ Exception -> 0x0c5e }
            r1.to_peer = r5     // Catch:{ Exception -> 0x0c5e }
            r12 = r77
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner     // Catch:{ Exception -> 0x0c5e }
            boolean r2 = r2.with_my_score     // Catch:{ Exception -> 0x0c5e }
            r1.with_my_score = r2     // Catch:{ Exception -> 0x0c5e }
            r13 = r43
            if (r13 == 0) goto L_0x0d23
            r2 = r51
            boolean r4 = r13.containsKey(r2)     // Catch:{ Exception -> 0x0c5e }
            if (r4 == 0) goto L_0x0d23
            java.lang.Object r2 = r13.get(r2)     // Catch:{ Exception -> 0x0c5e }
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2     // Catch:{ Exception -> 0x0c5e }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ Exception -> 0x0c5e }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x0c5e }
            r4 = 1
            r1.drop_author = r4     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r4 = "fwd_peer"
            java.lang.Object r4 = r13.get(r4)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0c5e }
            java.lang.Long r4 = org.telegram.messenger.Utilities.parseLong(r4)     // Catch:{ Exception -> 0x0c5e }
            long r4 = r4.longValue()     // Catch:{ Exception -> 0x0c5e }
            int r6 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r6 >= 0) goto L_0x0d12
            org.telegram.messenger.MessagesController r6 = r59.getMessagesController()     // Catch:{ Exception -> 0x0c5e }
            long r4 = -r4
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x0c5e }
            org.telegram.tgnet.TLRPC$Chat r4 = r6.getChat(r4)     // Catch:{ Exception -> 0x0c5e }
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r4)     // Catch:{ Exception -> 0x0c5e }
            if (r5 == 0) goto L_0x0d0a
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r5 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x0c5e }
            r5.<init>()     // Catch:{ Exception -> 0x0c5e }
            r1.from_peer = r5     // Catch:{ Exception -> 0x0c5e }
            long r6 = r4.id     // Catch:{ Exception -> 0x0c5e }
            r5.channel_id = r6     // Catch:{ Exception -> 0x0c5e }
            long r6 = r4.access_hash     // Catch:{ Exception -> 0x0c5e }
            r5.access_hash = r6     // Catch:{ Exception -> 0x0c5e }
            goto L_0x0d19
        L_0x0d0a:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0c5e }
            r4.<init>()     // Catch:{ Exception -> 0x0c5e }
            r1.from_peer = r4     // Catch:{ Exception -> 0x0c5e }
            goto L_0x0d19
        L_0x0d12:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0c5e }
            r4.<init>()     // Catch:{ Exception -> 0x0c5e }
            r1.from_peer = r4     // Catch:{ Exception -> 0x0c5e }
        L_0x0d19:
            java.util.ArrayList<java.lang.Integer> r4 = r1.id     // Catch:{ Exception -> 0x0c5e }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x0c5e }
            r4.add(r2)     // Catch:{ Exception -> 0x0c5e }
            goto L_0x0d2a
        L_0x0d23:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0c5e }
            r2.<init>()     // Catch:{ Exception -> 0x0c5e }
            r1.from_peer = r2     // Catch:{ Exception -> 0x0c5e }
        L_0x0d2a:
            boolean r2 = r9.silent     // Catch:{ Exception -> 0x0c5e }
            r1.silent = r2     // Catch:{ Exception -> 0x0c5e }
            if (r3 == 0) goto L_0x0d38
            r1.schedule_date = r3     // Catch:{ Exception -> 0x0c5e }
            int r2 = r1.flags     // Catch:{ Exception -> 0x0c5e }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r1.flags = r2     // Catch:{ Exception -> 0x0c5e }
        L_0x0d38:
            java.util.ArrayList<java.lang.Long> r2 = r1.random_id     // Catch:{ Exception -> 0x0c5e }
            long r4 = r9.random_id     // Catch:{ Exception -> 0x0c5e }
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x0c5e }
            r2.add(r4)     // Catch:{ Exception -> 0x0c5e }
            int r2 = r77.getId()     // Catch:{ Exception -> 0x0c5e }
            if (r2 < 0) goto L_0x0d57
            java.util.ArrayList<java.lang.Integer> r2 = r1.id     // Catch:{ Exception -> 0x0c5e }
            int r4 = r77.getId()     // Catch:{ Exception -> 0x0c5e }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0c5e }
            r2.add(r4)     // Catch:{ Exception -> 0x0c5e }
            goto L_0x0d76
        L_0x0d57:
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner     // Catch:{ Exception -> 0x0c5e }
            int r4 = r2.fwd_msg_id     // Catch:{ Exception -> 0x0c5e }
            if (r4 == 0) goto L_0x0d67
            java.util.ArrayList<java.lang.Integer> r2 = r1.id     // Catch:{ Exception -> 0x0c5e }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0c5e }
            r2.add(r4)     // Catch:{ Exception -> 0x0c5e }
            goto L_0x0d76
        L_0x0d67:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from     // Catch:{ Exception -> 0x0c5e }
            if (r2 == 0) goto L_0x0d76
            java.util.ArrayList<java.lang.Integer> r4 = r1.id     // Catch:{ Exception -> 0x0c5e }
            int r2 = r2.channel_post     // Catch:{ Exception -> 0x0c5e }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x0c5e }
            r4.add(r2)     // Catch:{ Exception -> 0x0c5e }
        L_0x0d76:
            r2 = 0
            r4 = 0
            if (r3 == 0) goto L_0x0d7c
            r5 = 1
            goto L_0x0d7d
        L_0x0d7c:
            r5 = 0
        L_0x0d7d:
            r60 = r59
            r61 = r1
            r62 = r15
            r63 = r2
            r64 = r4
            r65 = r68
            r66 = r13
            r67 = r5
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67)     // Catch:{ Exception -> 0x0c5e }
            goto L_0x0e39
        L_0x0d92:
            r12 = r77
            r13 = r43
            r2 = 9
            if (r1 != r2) goto L_0x0e39
            org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult     // Catch:{ Exception -> 0x0c5e }
            r1.<init>()     // Catch:{ Exception -> 0x0c5e }
            r1.peer = r5     // Catch:{ Exception -> 0x0c5e }
            long r4 = r9.random_id     // Catch:{ Exception -> 0x0c5e }
            r1.random_id = r4     // Catch:{ Exception -> 0x0c5e }
            org.telegram.tgnet.TLRPC$Peer r2 = r9.from_id     // Catch:{ Exception -> 0x0c5e }
            if (r2 == 0) goto L_0x0db5
            org.telegram.messenger.MessagesController r2 = r59.getMessagesController()     // Catch:{ Exception -> 0x0c5e }
            org.telegram.tgnet.TLRPC$Peer r4 = r9.from_id     // Catch:{ Exception -> 0x0c5e }
            org.telegram.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r4)     // Catch:{ Exception -> 0x0c5e }
            r1.send_as = r2     // Catch:{ Exception -> 0x0c5e }
        L_0x0db5:
            java.lang.String r2 = "bot"
            boolean r2 = r13.containsKey(r2)     // Catch:{ Exception -> 0x0c5e }
            if (r2 != 0) goto L_0x0dbf
            r2 = 1
            goto L_0x0dc0
        L_0x0dbf:
            r2 = 0
        L_0x0dc0:
            r1.hide_via = r2     // Catch:{ Exception -> 0x0c5e }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r9.reply_to     // Catch:{ Exception -> 0x0c5e }
            if (r2 == 0) goto L_0x0dd2
            int r2 = r2.reply_to_msg_id     // Catch:{ Exception -> 0x0c5e }
            if (r2 == 0) goto L_0x0dd2
            int r4 = r1.flags     // Catch:{ Exception -> 0x0c5e }
            r5 = 1
            r4 = r4 | r5
            r1.flags = r4     // Catch:{ Exception -> 0x0c5e }
            r1.reply_to_msg_id = r2     // Catch:{ Exception -> 0x0c5e }
        L_0x0dd2:
            boolean r2 = r9.silent     // Catch:{ Exception -> 0x0c5e }
            r1.silent = r2     // Catch:{ Exception -> 0x0c5e }
            if (r3 == 0) goto L_0x0de0
            r1.schedule_date = r3     // Catch:{ Exception -> 0x0c5e }
            int r2 = r1.flags     // Catch:{ Exception -> 0x0c5e }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r1.flags = r2     // Catch:{ Exception -> 0x0c5e }
        L_0x0de0:
            r2 = r48
            java.lang.Object r2 = r13.get(r2)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0c5e }
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r2)     // Catch:{ Exception -> 0x0c5e }
            long r4 = r2.longValue()     // Catch:{ Exception -> 0x0c5e }
            r1.query_id = r4     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r2 = "id"
            java.lang.Object r2 = r13.get(r2)     // Catch:{ Exception -> 0x0c5e }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0c5e }
            r1.id = r2     // Catch:{ Exception -> 0x0c5e }
            if (r12 != 0) goto L_0x0e14
            r2 = 1
            r1.clear_draft = r2     // Catch:{ Exception -> 0x0c5e }
            org.telegram.messenger.MediaDataController r2 = r59.getMediaDataController()     // Catch:{ Exception -> 0x0c5e }
            r4 = r59
            if (r74 == 0) goto L_0x0e0e
            int r5 = r74.getId()     // Catch:{ Exception -> 0x0e33 }
            goto L_0x0e0f
        L_0x0e0e:
            r5 = 0
        L_0x0e0f:
            r6 = 0
            r2.cleanDraft(r10, r5, r6)     // Catch:{ Exception -> 0x0e33 }
            goto L_0x0e16
        L_0x0e14:
            r4 = r59
        L_0x0e16:
            r2 = 0
            r5 = 0
            if (r3 == 0) goto L_0x0e1c
            r6 = 1
            goto L_0x0e1d
        L_0x0e1c:
            r6 = 0
        L_0x0e1d:
            r60 = r59
            r61 = r1
            r62 = r15
            r63 = r2
            r64 = r5
            r65 = r68
            r66 = r13
            r67 = r6
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67)     // Catch:{ Exception -> 0x0e33 }
            r8 = r4
            goto L_0x1d1a
        L_0x0e33:
            r0 = move-exception
            r1 = r0
            r14 = r3
            r8 = r4
            goto L_0x0CLASSNAME
        L_0x0e39:
            r8 = r59
            goto L_0x1d1a
        L_0x0e3d:
            r8 = r59
            r52 = r12
            r13 = r43
            r6 = 4
            goto L_0x0CLASSNAME
        L_0x0e46:
            if (r14 != 0) goto L_0x1590
            r7 = 1
            if (r1 != r7) goto L_0x0eb8
            r7 = r22
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x0eb5 }
            if (r4 == 0) goto L_0x0e6b
            org.telegram.tgnet.TLRPC$TL_inputMediaVenue r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue     // Catch:{ Exception -> 0x0eb5 }
            r4.<init>()     // Catch:{ Exception -> 0x0eb5 }
            java.lang.String r6 = r7.address     // Catch:{ Exception -> 0x0eb5 }
            r4.address = r6     // Catch:{ Exception -> 0x0eb5 }
            java.lang.String r6 = r7.title     // Catch:{ Exception -> 0x0eb5 }
            r4.title = r6     // Catch:{ Exception -> 0x0eb5 }
            java.lang.String r6 = r7.provider     // Catch:{ Exception -> 0x0eb5 }
            r4.provider = r6     // Catch:{ Exception -> 0x0eb5 }
            java.lang.String r6 = r7.venue_id     // Catch:{ Exception -> 0x0eb5 }
            r4.venue_id = r6     // Catch:{ Exception -> 0x0eb5 }
            r6 = r37
            r4.venue_type = r6     // Catch:{ Exception -> 0x0eb5 }
            goto L_0x0e9a
        L_0x0e6b:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive     // Catch:{ Exception -> 0x0eb5 }
            if (r4 == 0) goto L_0x0e95
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive     // Catch:{ Exception -> 0x0eb5 }
            r4.<init>()     // Catch:{ Exception -> 0x0eb5 }
            int r10 = r7.period     // Catch:{ Exception -> 0x0eb5 }
            r4.period = r10     // Catch:{ Exception -> 0x0eb5 }
            int r10 = r4.flags     // Catch:{ Exception -> 0x0eb5 }
            r11 = 2
            r10 = r10 | r11
            r4.flags = r10     // Catch:{ Exception -> 0x0eb5 }
            int r11 = r7.heading     // Catch:{ Exception -> 0x0eb5 }
            if (r11 == 0) goto L_0x0e87
            r4.heading = r11     // Catch:{ Exception -> 0x0eb5 }
            r6 = r6 | r10
            r4.flags = r6     // Catch:{ Exception -> 0x0eb5 }
        L_0x0e87:
            int r6 = r7.proximity_notification_radius     // Catch:{ Exception -> 0x0eb5 }
            if (r6 == 0) goto L_0x0e9a
            r4.proximity_notification_radius = r6     // Catch:{ Exception -> 0x0eb5 }
            int r6 = r4.flags     // Catch:{ Exception -> 0x0eb5 }
            r10 = 8
            r6 = r6 | r10
            r4.flags = r6     // Catch:{ Exception -> 0x0eb5 }
            goto L_0x0e9a
        L_0x0e95:
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint     // Catch:{ Exception -> 0x0eb5 }
            r4.<init>()     // Catch:{ Exception -> 0x0eb5 }
        L_0x0e9a:
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r6 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint     // Catch:{ Exception -> 0x0eb5 }
            r6.<init>()     // Catch:{ Exception -> 0x0eb5 }
            r4.geo_point = r6     // Catch:{ Exception -> 0x0eb5 }
            org.telegram.tgnet.TLRPC$GeoPoint r7 = r7.geo     // Catch:{ Exception -> 0x0eb5 }
            double r10 = r7.lat     // Catch:{ Exception -> 0x0eb5 }
            r6.lat = r10     // Catch:{ Exception -> 0x0eb5 }
            double r10 = r7._long     // Catch:{ Exception -> 0x0eb5 }
            r6._long = r10     // Catch:{ Exception -> 0x0eb5 }
            r69 = r1
            r7 = r3
            r45 = r5
            r5 = r9
            r43 = r13
            goto L_0x117a
        L_0x0eb5:
            r0 = move-exception
            goto L_0x0CLASSNAME
        L_0x0eb8:
            r6 = r37
            r7 = 2
            if (r1 == r7) goto L_0x12ae
            r7 = 9
            if (r1 != r7) goto L_0x0ec5
            if (r62 == 0) goto L_0x0ec5
            goto L_0x12ae
        L_0x0ec5:
            java.lang.String r7 = "query"
            r12 = 3
            if (r1 != r12) goto L_0x0ffc
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0ff3 }
            r6.<init>()     // Catch:{ Exception -> 0x0ff3 }
            r14 = r39
            java.lang.String r12 = r14.mime_type     // Catch:{ Exception -> 0x0ff3 }
            r6.mime_type = r12     // Catch:{ Exception -> 0x0ff3 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r14.attributes     // Catch:{ Exception -> 0x0ff3 }
            r6.attributes = r12     // Catch:{ Exception -> 0x0ff3 }
            if (r19 != 0) goto L_0x0eef
            boolean r12 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r14)     // Catch:{ Exception -> 0x0eb5 }
            if (r12 != 0) goto L_0x0eec
            if (r4 == 0) goto L_0x0eef
            boolean r12 = r4.muted     // Catch:{ Exception -> 0x0eb5 }
            if (r12 != 0) goto L_0x0eec
            boolean r12 = r4.roundVideo     // Catch:{ Exception -> 0x0eb5 }
            if (r12 != 0) goto L_0x0eec
            goto L_0x0eef
        L_0x0eec:
            r12 = r49
            goto L_0x0efc
        L_0x0eef:
            r12 = 1
            r6.nosound_video = r12     // Catch:{ Exception -> 0x0ff3 }
            boolean r12 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0ff3 }
            if (r12 == 0) goto L_0x0eec
            java.lang.String r12 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r12)     // Catch:{ Exception -> 0x0eb5 }
            goto L_0x0eec
        L_0x0efc:
            if (r12 == 0) goto L_0x0f0a
            r6.ttl_seconds = r12     // Catch:{ Exception -> 0x0eb5 }
            r9.ttl = r12     // Catch:{ Exception -> 0x0eb5 }
            int r12 = r6.flags     // Catch:{ Exception -> 0x0eb5 }
            r18 = 2
            r12 = r12 | 2
            r6.flags = r12     // Catch:{ Exception -> 0x0eb5 }
        L_0x0f0a:
            if (r13 == 0) goto L_0x0var_
            java.lang.String r12 = "masks"
            java.lang.Object r12 = r13.get(r12)     // Catch:{ Exception -> 0x0ff3 }
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ Exception -> 0x0ff3 }
            if (r12 == 0) goto L_0x0var_
            r45 = r5
            org.telegram.tgnet.SerializedData r5 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0ff3 }
            byte[] r12 = org.telegram.messenger.Utilities.hexToBytes(r12)     // Catch:{ Exception -> 0x0ff3 }
            r5.<init>((byte[]) r12)     // Catch:{ Exception -> 0x0ff3 }
            r18 = r9
            r12 = 0
            int r9 = r5.readInt32(r12)     // Catch:{ Exception -> 0x1058 }
        L_0x0var_:
            if (r12 >= r9) goto L_0x0var_
            r60 = r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r9 = r6.stickers     // Catch:{ Exception -> 0x1058 }
            r69 = r1
            r63 = r4
            r1 = 0
            int r4 = r5.readInt32(r1)     // Catch:{ Exception -> 0x1058 }
            org.telegram.tgnet.TLRPC$InputDocument r4 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r5, r4, r1)     // Catch:{ Exception -> 0x1058 }
            r9.add(r4)     // Catch:{ Exception -> 0x1058 }
            int r12 = r12 + 1
            r9 = r60
            r4 = r63
            r1 = r69
            goto L_0x0var_
        L_0x0var_:
            r69 = r1
            r63 = r4
            int r1 = r6.flags     // Catch:{ Exception -> 0x1058 }
            r4 = 1
            r1 = r1 | r4
            r6.flags = r1     // Catch:{ Exception -> 0x1058 }
            r5.cleanup()     // Catch:{ Exception -> 0x1058 }
            goto L_0x0f5d
        L_0x0var_:
            r69 = r1
            r63 = r4
            r45 = r5
            r18 = r9
        L_0x0f5d:
            long r4 = r14.access_hash     // Catch:{ Exception -> 0x1058 }
            int r1 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r1 != 0) goto L_0x0var_
            r60 = r6
            r5 = r60
            r1 = 1
            goto L_0x0fa2
        L_0x0var_:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x1058 }
            r1.<init>()     // Catch:{ Exception -> 0x1058 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x1058 }
            r4.<init>()     // Catch:{ Exception -> 0x1058 }
            r1.id = r4     // Catch:{ Exception -> 0x1058 }
            r60 = r6
            long r5 = r14.id     // Catch:{ Exception -> 0x1058 }
            r4.id = r5     // Catch:{ Exception -> 0x1058 }
            long r5 = r14.access_hash     // Catch:{ Exception -> 0x1058 }
            r4.access_hash = r5     // Catch:{ Exception -> 0x1058 }
            byte[] r5 = r14.file_reference     // Catch:{ Exception -> 0x1058 }
            r4.file_reference = r5     // Catch:{ Exception -> 0x1058 }
            if (r5 != 0) goto L_0x0f8a
            r5 = 0
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x1058 }
            r4.file_reference = r6     // Catch:{ Exception -> 0x1058 }
        L_0x0f8a:
            if (r13 == 0) goto L_0x0fa0
            boolean r4 = r13.containsKey(r7)     // Catch:{ Exception -> 0x1058 }
            if (r4 == 0) goto L_0x0fa0
            java.lang.Object r4 = r13.get(r7)     // Catch:{ Exception -> 0x1058 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x1058 }
            r1.query = r4     // Catch:{ Exception -> 0x1058 }
            int r4 = r1.flags     // Catch:{ Exception -> 0x1058 }
            r5 = 2
            r4 = r4 | r5
            r1.flags = r4     // Catch:{ Exception -> 0x1058 }
        L_0x0fa0:
            r5 = r1
            r1 = 0
        L_0x0fa2:
            if (r2 != 0) goto L_0x0fbe
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1058 }
            r2.<init>(r10)     // Catch:{ Exception -> 0x1058 }
            r4 = 1
            r2.type = r4     // Catch:{ Exception -> 0x1058 }
            r2.obj = r15     // Catch:{ Exception -> 0x1058 }
            r4 = r46
            r2.originalPath = r4     // Catch:{ Exception -> 0x1058 }
            r9 = r68
            r2.parentObject = r9     // Catch:{ Exception -> 0x1058 }
            if (r3 == 0) goto L_0x0fba
            r6 = 1
            goto L_0x0fbb
        L_0x0fba:
            r6 = 0
        L_0x0fbb:
            r2.scheduled = r6     // Catch:{ Exception -> 0x1058 }
            goto L_0x0fc2
        L_0x0fbe:
            r9 = r68
            r4 = r46
        L_0x0fc2:
            r6 = r60
            r2.inputUploadMedia = r6     // Catch:{ Exception -> 0x1058 }
            r2.performMediaUpload = r1     // Catch:{ Exception -> 0x1058 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r14.thumbs     // Catch:{ Exception -> 0x1058 }
            boolean r6 = r6.isEmpty()     // Catch:{ Exception -> 0x1058 }
            if (r6 != 0) goto L_0x0fe1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r14.thumbs     // Catch:{ Exception -> 0x1058 }
            r7 = 0
            java.lang.Object r6 = r6.get(r7)     // Catch:{ Exception -> 0x1058 }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC$PhotoSize) r6     // Catch:{ Exception -> 0x1058 }
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x1058 }
            if (r7 != 0) goto L_0x0fe1
            r2.photoSize = r6     // Catch:{ Exception -> 0x1058 }
            r2.locationParent = r14     // Catch:{ Exception -> 0x1058 }
        L_0x0fe1:
            r6 = r63
            r2.videoEditedInfo = r6     // Catch:{ Exception -> 0x1058 }
            r7 = r3
            r68 = r9
            r43 = r13
            r10 = r52
            r3 = r1
            r9 = r4
            r4 = r5
            r5 = r18
            goto L_0x138e
        L_0x0ff3:
            r0 = move-exception
            r18 = r9
        L_0x0ff6:
            r1 = r0
            r14 = r3
        L_0x0ff8:
            r5 = r18
            goto L_0x1cea
        L_0x0ffc:
            r45 = r5
            r18 = r9
            r14 = r39
            r4 = r46
            r12 = r49
            r5 = 6
            r9 = r68
            if (r1 != r5) goto L_0x105a
            org.telegram.tgnet.TLRPC$TL_inputMediaContact r7 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact     // Catch:{ Exception -> 0x1058 }
            r7.<init>()     // Catch:{ Exception -> 0x1058 }
            r10 = r50
            java.lang.String r11 = r10.phone     // Catch:{ Exception -> 0x1058 }
            r7.phone_number = r11     // Catch:{ Exception -> 0x1058 }
            java.lang.String r11 = r10.first_name     // Catch:{ Exception -> 0x1058 }
            r7.first_name = r11     // Catch:{ Exception -> 0x1058 }
            java.lang.String r11 = r10.last_name     // Catch:{ Exception -> 0x1058 }
            r7.last_name = r11     // Catch:{ Exception -> 0x1058 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r11 = r10.restriction_reason     // Catch:{ Exception -> 0x1058 }
            boolean r11 = r11.isEmpty()     // Catch:{ Exception -> 0x1058 }
            if (r11 != 0) goto L_0x1047
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r11 = r10.restriction_reason     // Catch:{ Exception -> 0x1058 }
            r12 = 0
            java.lang.Object r11 = r11.get(r12)     // Catch:{ Exception -> 0x1058 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r11 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r11     // Catch:{ Exception -> 0x1058 }
            java.lang.String r11 = r11.text     // Catch:{ Exception -> 0x1058 }
            java.lang.String r12 = "BEGIN:VCARD"
            boolean r11 = r11.startsWith(r12)     // Catch:{ Exception -> 0x1058 }
            if (r11 == 0) goto L_0x1047
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r6 = r10.restriction_reason     // Catch:{ Exception -> 0x1058 }
            r10 = 0
            java.lang.Object r6 = r6.get(r10)     // Catch:{ Exception -> 0x1058 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r6 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r6     // Catch:{ Exception -> 0x1058 }
            java.lang.String r6 = r6.text     // Catch:{ Exception -> 0x1058 }
            r7.vcard = r6     // Catch:{ Exception -> 0x1058 }
            goto L_0x1049
        L_0x1047:
            r7.vcard = r6     // Catch:{ Exception -> 0x1058 }
        L_0x1049:
            r69 = r1
            r68 = r9
            r43 = r13
            r5 = r18
            r10 = r52
            r9 = r4
            r4 = r7
            r7 = r3
            goto L_0x117e
        L_0x1058:
            r0 = move-exception
            goto L_0x0ff6
        L_0x105a:
            r6 = 7
            if (r1 == r6) goto L_0x1192
            r6 = 9
            if (r1 != r6) goto L_0x1063
            goto L_0x1192
        L_0x1063:
            r6 = 8
            if (r1 != r6) goto L_0x10fc
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x1058 }
            r2.<init>()     // Catch:{ Exception -> 0x1058 }
            java.lang.String r6 = r14.mime_type     // Catch:{ Exception -> 0x1058 }
            r2.mime_type = r6     // Catch:{ Exception -> 0x1058 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r14.attributes     // Catch:{ Exception -> 0x1058 }
            r2.attributes = r6     // Catch:{ Exception -> 0x1058 }
            if (r12 == 0) goto L_0x1091
            r2.ttl_seconds = r12     // Catch:{ Exception -> 0x1089 }
            r6 = r18
            r6.ttl = r12     // Catch:{ Exception -> 0x1087 }
            int r12 = r2.flags     // Catch:{ Exception -> 0x1087 }
            r18 = 2
            r12 = r12 | 2
            r2.flags = r12     // Catch:{ Exception -> 0x1087 }
            r18 = r6
            goto L_0x1091
        L_0x1087:
            r0 = move-exception
            goto L_0x108c
        L_0x1089:
            r0 = move-exception
            r6 = r18
        L_0x108c:
            r1 = r0
            r14 = r3
            r5 = r6
            goto L_0x1cea
        L_0x1091:
            long r5 = r14.access_hash     // Catch:{ Exception -> 0x1058 }
            int r12 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r12 != 0) goto L_0x109c
            r5 = r2
            r46 = r4
            r3 = 1
            goto L_0x10d4
        L_0x109c:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x1058 }
            r5.<init>()     // Catch:{ Exception -> 0x1058 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x1058 }
            r6.<init>()     // Catch:{ Exception -> 0x1058 }
            r5.id = r6     // Catch:{ Exception -> 0x1058 }
            r46 = r4
            long r3 = r14.id     // Catch:{ Exception -> 0x12a4 }
            r6.id = r3     // Catch:{ Exception -> 0x12a4 }
            long r3 = r14.access_hash     // Catch:{ Exception -> 0x12a4 }
            r6.access_hash = r3     // Catch:{ Exception -> 0x12a4 }
            byte[] r3 = r14.file_reference     // Catch:{ Exception -> 0x12a4 }
            r6.file_reference = r3     // Catch:{ Exception -> 0x12a4 }
            if (r3 != 0) goto L_0x10bd
            r3 = 0
            byte[] r4 = new byte[r3]     // Catch:{ Exception -> 0x12a4 }
            r6.file_reference = r4     // Catch:{ Exception -> 0x12a4 }
        L_0x10bd:
            if (r13 == 0) goto L_0x10d3
            boolean r3 = r13.containsKey(r7)     // Catch:{ Exception -> 0x12a4 }
            if (r3 == 0) goto L_0x10d3
            java.lang.Object r3 = r13.get(r7)     // Catch:{ Exception -> 0x12a4 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x12a4 }
            r5.query = r3     // Catch:{ Exception -> 0x12a4 }
            int r3 = r5.flags     // Catch:{ Exception -> 0x12a4 }
            r4 = 2
            r3 = r3 | r4
            r5.flags = r3     // Catch:{ Exception -> 0x12a4 }
        L_0x10d3:
            r3 = 0
        L_0x10d4:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r4 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x12a4 }
            r4.<init>(r10)     // Catch:{ Exception -> 0x12a4 }
            r6 = 3
            r4.type = r6     // Catch:{ Exception -> 0x12a4 }
            r4.obj = r15     // Catch:{ Exception -> 0x12a4 }
            r4.parentObject = r9     // Catch:{ Exception -> 0x12a4 }
            r4.inputUploadMedia = r2     // Catch:{ Exception -> 0x12a4 }
            r4.performMediaUpload = r3     // Catch:{ Exception -> 0x12a4 }
            r6 = r82
            if (r6 == 0) goto L_0x10ea
            r2 = 1
            goto L_0x10eb
        L_0x10ea:
            r2 = 0
        L_0x10eb:
            r4.scheduled = r2     // Catch:{ Exception -> 0x115e }
            r69 = r1
            r2 = r4
            r4 = r5
            r7 = r6
            r68 = r9
            r43 = r13
            r5 = r18
            r9 = r46
            goto L_0x12a0
        L_0x10fc:
            r6 = r3
            r46 = r4
            r3 = 10
            if (r1 != r3) goto L_0x1163
            org.telegram.tgnet.TLRPC$TL_inputMediaPoll r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll     // Catch:{ Exception -> 0x115e }
            r5.<init>()     // Catch:{ Exception -> 0x115e }
            r3 = r40
            org.telegram.tgnet.TLRPC$Poll r4 = r3.poll     // Catch:{ Exception -> 0x115e }
            r5.poll = r4     // Catch:{ Exception -> 0x115e }
            if (r13 == 0) goto L_0x1141
            java.lang.String r4 = "answers"
            boolean r4 = r13.containsKey(r4)     // Catch:{ Exception -> 0x115e }
            if (r4 == 0) goto L_0x1141
            java.lang.String r4 = "answers"
            java.lang.Object r4 = r13.get(r4)     // Catch:{ Exception -> 0x115e }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x115e }
            byte[] r4 = org.telegram.messenger.Utilities.hexToBytes(r4)     // Catch:{ Exception -> 0x115e }
            int r7 = r4.length     // Catch:{ Exception -> 0x115e }
            if (r7 <= 0) goto L_0x1141
            r7 = 0
        L_0x1128:
            int r10 = r4.length     // Catch:{ Exception -> 0x115e }
            if (r7 >= r10) goto L_0x113b
            java.util.ArrayList<byte[]> r10 = r5.correct_answers     // Catch:{ Exception -> 0x115e }
            r11 = 1
            byte[] r12 = new byte[r11]     // Catch:{ Exception -> 0x115e }
            byte r11 = r4[r7]     // Catch:{ Exception -> 0x115e }
            r14 = 0
            r12[r14] = r11     // Catch:{ Exception -> 0x115e }
            r10.add(r12)     // Catch:{ Exception -> 0x115e }
            int r7 = r7 + 1
            goto L_0x1128
        L_0x113b:
            int r4 = r5.flags     // Catch:{ Exception -> 0x115e }
            r7 = 1
            r4 = r4 | r7
            r5.flags = r4     // Catch:{ Exception -> 0x115e }
        L_0x1141:
            org.telegram.tgnet.TLRPC$PollResults r4 = r3.results     // Catch:{ Exception -> 0x115e }
            if (r4 == 0) goto L_0x1170
            java.lang.String r4 = r4.solution     // Catch:{ Exception -> 0x115e }
            boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x115e }
            if (r4 != 0) goto L_0x1170
            org.telegram.tgnet.TLRPC$PollResults r3 = r3.results     // Catch:{ Exception -> 0x115e }
            java.lang.String r4 = r3.solution     // Catch:{ Exception -> 0x115e }
            r5.solution = r4     // Catch:{ Exception -> 0x115e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r3.solution_entities     // Catch:{ Exception -> 0x115e }
            r5.solution_entities = r3     // Catch:{ Exception -> 0x115e }
            int r3 = r5.flags     // Catch:{ Exception -> 0x115e }
            r4 = 2
            r3 = r3 | r4
            r5.flags = r3     // Catch:{ Exception -> 0x115e }
            goto L_0x1170
        L_0x115e:
            r0 = move-exception
            r1 = r0
            r14 = r6
            goto L_0x0ff8
        L_0x1163:
            r3 = 11
            if (r1 != r3) goto L_0x1181
            org.telegram.tgnet.TLRPC$TL_inputMediaDice r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDice     // Catch:{ Exception -> 0x115e }
            r5.<init>()     // Catch:{ Exception -> 0x115e }
            r3 = r65
            r5.emoticon = r3     // Catch:{ Exception -> 0x115e }
        L_0x1170:
            r69 = r1
            r4 = r5
            r7 = r6
            r68 = r9
            r43 = r13
            r5 = r18
        L_0x117a:
            r9 = r46
            r10 = r52
        L_0x117e:
            r3 = 0
            goto L_0x138e
        L_0x1181:
            r69 = r1
            r7 = r6
            r68 = r9
            r43 = r13
            r5 = r18
            r9 = r46
            r10 = r52
            r3 = 0
            r4 = 0
            goto L_0x138e
        L_0x1192:
            r6 = r3
            r46 = r4
            r3 = r72
            r4 = r18
            if (r46 != 0) goto L_0x11af
            if (r3 != 0) goto L_0x11af
            long r5 = r14.access_hash     // Catch:{ Exception -> 0x11a8 }
            int r18 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x11a4
            goto L_0x11af
        L_0x11a4:
            r18 = r4
            r5 = 0
            goto L_0x11fa
        L_0x11a8:
            r0 = move-exception
            r14 = r82
            r1 = r0
            r5 = r4
            goto L_0x1cea
        L_0x11af:
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x12a6 }
            r5.<init>()     // Catch:{ Exception -> 0x12a6 }
            if (r12 == 0) goto L_0x11c0
            r5.ttl_seconds = r12     // Catch:{ Exception -> 0x11a8 }
            r4.ttl = r12     // Catch:{ Exception -> 0x11a8 }
            int r6 = r5.flags     // Catch:{ Exception -> 0x11a8 }
            r12 = 2
            r6 = r6 | r12
            r5.flags = r6     // Catch:{ Exception -> 0x11a8 }
        L_0x11c0:
            if (r19 != 0) goto L_0x11de
            boolean r6 = android.text.TextUtils.isEmpty(r72)     // Catch:{ Exception -> 0x11a8 }
            if (r6 != 0) goto L_0x11e1
            java.lang.String r3 = r72.toLowerCase()     // Catch:{ Exception -> 0x11a8 }
            java.lang.String r6 = "mp4"
            boolean r3 = r3.endsWith(r6)     // Catch:{ Exception -> 0x11a8 }
            if (r3 == 0) goto L_0x11e1
            if (r13 == 0) goto L_0x11de
            java.lang.String r3 = "forceDocument"
            boolean r3 = r13.containsKey(r3)     // Catch:{ Exception -> 0x11a8 }
            if (r3 == 0) goto L_0x11e1
        L_0x11de:
            r3 = 1
            r5.nosound_video = r3     // Catch:{ Exception -> 0x12a6 }
        L_0x11e1:
            if (r13 == 0) goto L_0x11ed
            java.lang.String r3 = "forceDocument"
            boolean r3 = r13.containsKey(r3)     // Catch:{ Exception -> 0x11a8 }
            if (r3 == 0) goto L_0x11ed
            r3 = 1
            goto L_0x11ee
        L_0x11ed:
            r3 = 0
        L_0x11ee:
            r5.force_file = r3     // Catch:{ Exception -> 0x12a6 }
            java.lang.String r3 = r14.mime_type     // Catch:{ Exception -> 0x12a6 }
            r5.mime_type = r3     // Catch:{ Exception -> 0x12a6 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r14.attributes     // Catch:{ Exception -> 0x12a6 }
            r5.attributes = r3     // Catch:{ Exception -> 0x12a6 }
            r18 = r4
        L_0x11fa:
            long r3 = r14.access_hash     // Catch:{ Exception -> 0x12a4 }
            int r6 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r6 != 0) goto L_0x1206
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x12a4 }
            r4 = r5
            r68 = r9
            goto L_0x123f
        L_0x1206:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x12a4 }
            r3.<init>()     // Catch:{ Exception -> 0x12a4 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x12a4 }
            r4.<init>()     // Catch:{ Exception -> 0x12a4 }
            r3.id = r4     // Catch:{ Exception -> 0x12a4 }
            r68 = r9
            long r8 = r14.id     // Catch:{ Exception -> 0x1261 }
            r4.id = r8     // Catch:{ Exception -> 0x1261 }
            long r8 = r14.access_hash     // Catch:{ Exception -> 0x1261 }
            r4.access_hash = r8     // Catch:{ Exception -> 0x1261 }
            byte[] r6 = r14.file_reference     // Catch:{ Exception -> 0x1261 }
            r4.file_reference = r6     // Catch:{ Exception -> 0x1261 }
            if (r6 != 0) goto L_0x1227
            r6 = 0
            byte[] r8 = new byte[r6]     // Catch:{ Exception -> 0x1261 }
            r4.file_reference = r8     // Catch:{ Exception -> 0x1261 }
        L_0x1227:
            if (r13 == 0) goto L_0x123d
            boolean r4 = r13.containsKey(r7)     // Catch:{ Exception -> 0x1261 }
            if (r4 == 0) goto L_0x123d
            java.lang.Object r4 = r13.get(r7)     // Catch:{ Exception -> 0x1261 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x1261 }
            r3.query = r4     // Catch:{ Exception -> 0x1261 }
            int r4 = r3.flags     // Catch:{ Exception -> 0x1261 }
            r6 = 2
            r4 = r4 | r6
            r3.flags = r4     // Catch:{ Exception -> 0x1261 }
        L_0x123d:
            r4 = r3
            r3 = 0
        L_0x123f:
            if (r5 == 0) goto L_0x1290
            if (r2 != 0) goto L_0x1265
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1261 }
            r8 = r59
            r2.<init>(r10)     // Catch:{ Exception -> 0x12a4 }
            r6 = 2
            r2.type = r6     // Catch:{ Exception -> 0x12a4 }
            r2.obj = r15     // Catch:{ Exception -> 0x12a4 }
            r9 = r46
            r2.originalPath = r9     // Catch:{ Exception -> 0x12a4 }
            r6 = r68
            r2.parentObject = r6     // Catch:{ Exception -> 0x12a4 }
            r7 = r82
            if (r7 == 0) goto L_0x125d
            r10 = 1
            goto L_0x125e
        L_0x125d:
            r10 = 0
        L_0x125e:
            r2.scheduled = r10     // Catch:{ Exception -> 0x128b }
            goto L_0x126d
        L_0x1261:
            r0 = move-exception
            r8 = r59
            goto L_0x12a9
        L_0x1265:
            r8 = r59
            r6 = r68
            r7 = r82
            r9 = r46
        L_0x126d:
            r2.inputUploadMedia = r5     // Catch:{ Exception -> 0x128b }
            r2.performMediaUpload = r3     // Catch:{ Exception -> 0x128b }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r14.thumbs     // Catch:{ Exception -> 0x128b }
            boolean r5 = r5.isEmpty()     // Catch:{ Exception -> 0x128b }
            if (r5 != 0) goto L_0x1298
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r14.thumbs     // Catch:{ Exception -> 0x128b }
            r10 = 0
            java.lang.Object r5 = r5.get(r10)     // Catch:{ Exception -> 0x128b }
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5     // Catch:{ Exception -> 0x128b }
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x128b }
            if (r10 != 0) goto L_0x1298
            r2.photoSize = r5     // Catch:{ Exception -> 0x128b }
            r2.locationParent = r14     // Catch:{ Exception -> 0x128b }
            goto L_0x1298
        L_0x128b:
            r0 = move-exception
            r1 = r0
            r14 = r7
            goto L_0x0ff8
        L_0x1290:
            r8 = r59
            r6 = r68
            r7 = r82
            r9 = r46
        L_0x1298:
            r69 = r1
            r68 = r6
            r43 = r13
            r5 = r18
        L_0x12a0:
            r10 = r52
            goto L_0x138e
        L_0x12a4:
            r0 = move-exception
            goto L_0x12a9
        L_0x12a6:
            r0 = move-exception
            r18 = r4
        L_0x12a9:
            r14 = r82
        L_0x12ab:
            r1 = r0
            goto L_0x0ff8
        L_0x12ae:
            r6 = r68
            r7 = r3
            r45 = r5
            r18 = r9
            r9 = r46
            r12 = r49
            r3 = r72
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r4 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x1589 }
            r4.<init>()     // Catch:{ Exception -> 0x1589 }
            if (r12 == 0) goto L_0x12cf
            r4.ttl_seconds = r12     // Catch:{ Exception -> 0x1589 }
            r5 = r18
            r5.ttl = r12     // Catch:{ Exception -> 0x1587 }
            int r12 = r4.flags     // Catch:{ Exception -> 0x1587 }
            r14 = 2
            r12 = r12 | r14
            r4.flags = r12     // Catch:{ Exception -> 0x1587 }
            goto L_0x12d1
        L_0x12cf:
            r5 = r18
        L_0x12d1:
            if (r13 == 0) goto L_0x131a
            java.lang.String r12 = "masks"
            java.lang.Object r12 = r13.get(r12)     // Catch:{ Exception -> 0x1587 }
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ Exception -> 0x1587 }
            if (r12 == 0) goto L_0x131a
            org.telegram.tgnet.SerializedData r14 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x1587 }
            byte[] r12 = org.telegram.messenger.Utilities.hexToBytes(r12)     // Catch:{ Exception -> 0x1587 }
            r14.<init>((byte[]) r12)     // Catch:{ Exception -> 0x1587 }
            r43 = r13
            r12 = 0
            int r13 = r14.readInt32(r12)     // Catch:{ Exception -> 0x1587 }
        L_0x12ed:
            if (r12 >= r13) goto L_0x130c
            r60 = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r13 = r4.stickers     // Catch:{ Exception -> 0x1587 }
            r69 = r1
            r68 = r6
            r1 = 0
            int r6 = r14.readInt32(r1)     // Catch:{ Exception -> 0x1587 }
            org.telegram.tgnet.TLRPC$InputDocument r6 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r14, r6, r1)     // Catch:{ Exception -> 0x1587 }
            r13.add(r6)     // Catch:{ Exception -> 0x1587 }
            int r12 = r12 + 1
            r13 = r60
            r6 = r68
            r1 = r69
            goto L_0x12ed
        L_0x130c:
            r69 = r1
            r68 = r6
            int r1 = r4.flags     // Catch:{ Exception -> 0x1587 }
            r6 = 1
            r1 = r1 | r6
            r4.flags = r1     // Catch:{ Exception -> 0x1587 }
            r14.cleanup()     // Catch:{ Exception -> 0x1587 }
            goto L_0x1320
        L_0x131a:
            r69 = r1
            r68 = r6
            r43 = r13
        L_0x1320:
            r1 = r62
            long r12 = r1.access_hash     // Catch:{ Exception -> 0x1587 }
            int r6 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r6 != 0) goto L_0x132b
            r12 = r4
            r6 = 1
            goto L_0x134c
        L_0x132b:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r6 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x1587 }
            r6.<init>()     // Catch:{ Exception -> 0x1587 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r12 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x1587 }
            r12.<init>()     // Catch:{ Exception -> 0x1587 }
            r6.id = r12     // Catch:{ Exception -> 0x1587 }
            long r13 = r1.id     // Catch:{ Exception -> 0x1587 }
            r12.id = r13     // Catch:{ Exception -> 0x1587 }
            long r13 = r1.access_hash     // Catch:{ Exception -> 0x1587 }
            r12.access_hash = r13     // Catch:{ Exception -> 0x1587 }
            byte[] r13 = r1.file_reference     // Catch:{ Exception -> 0x1587 }
            r12.file_reference = r13     // Catch:{ Exception -> 0x1587 }
            if (r13 != 0) goto L_0x134a
            r13 = 0
            byte[] r14 = new byte[r13]     // Catch:{ Exception -> 0x1587 }
            r12.file_reference = r14     // Catch:{ Exception -> 0x1587 }
        L_0x134a:
            r12 = r6
            r6 = 0
        L_0x134c:
            if (r2 != 0) goto L_0x1361
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1587 }
            r2.<init>(r10)     // Catch:{ Exception -> 0x1587 }
            r10 = 0
            r2.type = r10     // Catch:{ Exception -> 0x1587 }
            r2.obj = r15     // Catch:{ Exception -> 0x1587 }
            r2.originalPath = r9     // Catch:{ Exception -> 0x1587 }
            if (r7 == 0) goto L_0x135e
            r10 = 1
            goto L_0x135f
        L_0x135e:
            r10 = 0
        L_0x135f:
            r2.scheduled = r10     // Catch:{ Exception -> 0x1587 }
        L_0x1361:
            r2.inputUploadMedia = r4     // Catch:{ Exception -> 0x1587 }
            r2.performMediaUpload = r6     // Catch:{ Exception -> 0x1587 }
            if (r3 == 0) goto L_0x1378
            int r4 = r72.length()     // Catch:{ Exception -> 0x1587 }
            if (r4 <= 0) goto L_0x1378
            r4 = r47
            boolean r4 = r3.startsWith(r4)     // Catch:{ Exception -> 0x1587 }
            if (r4 == 0) goto L_0x1378
            r2.httpLocation = r3     // Catch:{ Exception -> 0x1587 }
            goto L_0x138a
        L_0x1378:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r1.sizes     // Catch:{ Exception -> 0x1587 }
            int r4 = r3.size()     // Catch:{ Exception -> 0x1587 }
            r10 = 1
            int r4 = r4 - r10
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x1587 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x1587 }
            r2.photoSize = r3     // Catch:{ Exception -> 0x1587 }
            r2.locationParent = r1     // Catch:{ Exception -> 0x1587 }
        L_0x138a:
            r3 = r6
            r4 = r12
            goto L_0x12a0
        L_0x138e:
            int r1 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x141d
            org.telegram.tgnet.TLObject r1 = r2.sendRequest     // Catch:{ Exception -> 0x1587 }
            if (r1 == 0) goto L_0x1399
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r1     // Catch:{ Exception -> 0x1587 }
            goto L_0x13c2
        L_0x1399:
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia     // Catch:{ Exception -> 0x1587 }
            r1.<init>()     // Catch:{ Exception -> 0x1587 }
            r6 = r45
            r1.peer = r6     // Catch:{ Exception -> 0x1587 }
            boolean r6 = r5.silent     // Catch:{ Exception -> 0x1587 }
            r1.silent = r6     // Catch:{ Exception -> 0x1587 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r6 = r5.reply_to     // Catch:{ Exception -> 0x1587 }
            if (r6 == 0) goto L_0x13b6
            int r6 = r6.reply_to_msg_id     // Catch:{ Exception -> 0x1587 }
            if (r6 == 0) goto L_0x13b6
            int r12 = r1.flags     // Catch:{ Exception -> 0x1587 }
            r13 = 1
            r12 = r12 | r13
            r1.flags = r12     // Catch:{ Exception -> 0x1587 }
            r1.reply_to_msg_id = r6     // Catch:{ Exception -> 0x1587 }
        L_0x13b6:
            if (r7 == 0) goto L_0x13c0
            r1.schedule_date = r7     // Catch:{ Exception -> 0x1587 }
            int r6 = r1.flags     // Catch:{ Exception -> 0x1587 }
            r6 = r6 | 1024(0x400, float:1.435E-42)
            r1.flags = r6     // Catch:{ Exception -> 0x1587 }
        L_0x13c0:
            r2.sendRequest = r1     // Catch:{ Exception -> 0x1587 }
        L_0x13c2:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r6 = r2.messageObjects     // Catch:{ Exception -> 0x1587 }
            r6.add(r15)     // Catch:{ Exception -> 0x1587 }
            java.util.ArrayList<java.lang.Object> r6 = r2.parentObjects     // Catch:{ Exception -> 0x1587 }
            r12 = r68
            r6.add(r12)     // Catch:{ Exception -> 0x1587 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r2.locations     // Catch:{ Exception -> 0x1587 }
            org.telegram.tgnet.TLRPC$PhotoSize r13 = r2.photoSize     // Catch:{ Exception -> 0x1587 }
            r6.add(r13)     // Catch:{ Exception -> 0x1587 }
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo> r6 = r2.videoEditedInfos     // Catch:{ Exception -> 0x1587 }
            org.telegram.messenger.VideoEditedInfo r13 = r2.videoEditedInfo     // Catch:{ Exception -> 0x1587 }
            r6.add(r13)     // Catch:{ Exception -> 0x1587 }
            java.util.ArrayList<java.lang.String> r6 = r2.httpLocations     // Catch:{ Exception -> 0x1587 }
            java.lang.String r13 = r2.httpLocation     // Catch:{ Exception -> 0x1587 }
            r6.add(r13)     // Catch:{ Exception -> 0x1587 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputMedia> r6 = r2.inputMedias     // Catch:{ Exception -> 0x1587 }
            org.telegram.tgnet.TLRPC$InputMedia r13 = r2.inputUploadMedia     // Catch:{ Exception -> 0x1587 }
            r6.add(r13)     // Catch:{ Exception -> 0x1587 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r6 = r2.messages     // Catch:{ Exception -> 0x1587 }
            r6.add(r5)     // Catch:{ Exception -> 0x1587 }
            java.util.ArrayList<java.lang.String> r6 = r2.originalPaths     // Catch:{ Exception -> 0x1587 }
            r6.add(r9)     // Catch:{ Exception -> 0x1587 }
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r6 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia     // Catch:{ Exception -> 0x1587 }
            r6.<init>()     // Catch:{ Exception -> 0x1587 }
            long r13 = r5.random_id     // Catch:{ Exception -> 0x1587 }
            r6.random_id = r13     // Catch:{ Exception -> 0x1587 }
            r6.media = r4     // Catch:{ Exception -> 0x1587 }
            r13 = r42
            r6.message = r13     // Catch:{ Exception -> 0x1587 }
            r14 = r78
            if (r14 == 0) goto L_0x1415
            boolean r4 = r78.isEmpty()     // Catch:{ Exception -> 0x1587 }
            if (r4 != 0) goto L_0x1415
            r6.entities = r14     // Catch:{ Exception -> 0x1587 }
            int r4 = r6.flags     // Catch:{ Exception -> 0x1587 }
            r13 = 1
            r4 = r4 | r13
            r6.flags = r4     // Catch:{ Exception -> 0x1587 }
        L_0x1415:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r4 = r1.multi_media     // Catch:{ Exception -> 0x1587 }
            r4.add(r6)     // Catch:{ Exception -> 0x1587 }
            r46 = r9
            goto L_0x147e
        L_0x141d:
            r12 = r68
            r14 = r78
            r13 = r42
            r6 = r45
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia     // Catch:{ Exception -> 0x1587 }
            r1.<init>()     // Catch:{ Exception -> 0x1587 }
            r1.peer = r6     // Catch:{ Exception -> 0x1587 }
            boolean r6 = r5.silent     // Catch:{ Exception -> 0x1587 }
            r1.silent = r6     // Catch:{ Exception -> 0x1587 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r6 = r5.reply_to     // Catch:{ Exception -> 0x1587 }
            if (r6 == 0) goto L_0x1445
            int r6 = r6.reply_to_msg_id     // Catch:{ Exception -> 0x1587 }
            if (r6 == 0) goto L_0x1445
            r46 = r9
            int r9 = r1.flags     // Catch:{ Exception -> 0x1587 }
            r18 = 1
            r9 = r9 | 1
            r1.flags = r9     // Catch:{ Exception -> 0x1587 }
            r1.reply_to_msg_id = r6     // Catch:{ Exception -> 0x1587 }
            goto L_0x1447
        L_0x1445:
            r46 = r9
        L_0x1447:
            long r8 = r5.random_id     // Catch:{ Exception -> 0x1583 }
            r1.random_id = r8     // Catch:{ Exception -> 0x1583 }
            org.telegram.tgnet.TLRPC$Peer r6 = r5.from_id     // Catch:{ Exception -> 0x1583 }
            if (r6 == 0) goto L_0x145b
            org.telegram.messenger.MessagesController r6 = r59.getMessagesController()     // Catch:{ Exception -> 0x1583 }
            org.telegram.tgnet.TLRPC$Peer r8 = r5.from_id     // Catch:{ Exception -> 0x1583 }
            org.telegram.tgnet.TLRPC$InputPeer r6 = r6.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r8)     // Catch:{ Exception -> 0x1583 }
            r1.send_as = r6     // Catch:{ Exception -> 0x1583 }
        L_0x145b:
            r1.media = r4     // Catch:{ Exception -> 0x1583 }
            r1.message = r13     // Catch:{ Exception -> 0x1583 }
            if (r14 == 0) goto L_0x1470
            boolean r4 = r78.isEmpty()     // Catch:{ Exception -> 0x1583 }
            if (r4 != 0) goto L_0x1470
            r1.entities = r14     // Catch:{ Exception -> 0x1583 }
            int r4 = r1.flags     // Catch:{ Exception -> 0x1583 }
            r6 = 8
            r4 = r4 | r6
            r1.flags = r4     // Catch:{ Exception -> 0x1583 }
        L_0x1470:
            if (r7 == 0) goto L_0x147a
            r1.schedule_date = r7     // Catch:{ Exception -> 0x1583 }
            int r4 = r1.flags     // Catch:{ Exception -> 0x1583 }
            r4 = r4 | 1024(0x400, float:1.435E-42)
            r1.flags = r4     // Catch:{ Exception -> 0x1583 }
        L_0x147a:
            if (r2 == 0) goto L_0x147e
            r2.sendRequest = r1     // Catch:{ Exception -> 0x1583 }
        L_0x147e:
            int r4 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x1489
            r8 = r59
            r8.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x1489:
            r4 = 1
            r8 = r59
            r9 = r69
            if (r9 != r4) goto L_0x14ab
            r3 = 0
            if (r7 == 0) goto L_0x1495
            r4 = 1
            goto L_0x1496
        L_0x1495:
            r4 = 0
        L_0x1496:
            r60 = r59
            r61 = r1
            r62 = r15
            r63 = r3
            r64 = r2
            r65 = r12
            r66 = r43
            r67 = r4
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x14ab:
            r4 = 2
            if (r9 != r4) goto L_0x14d5
            if (r3 == 0) goto L_0x14b5
            r8.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x14b5:
            r3 = 0
            r4 = 1
            if (r7 == 0) goto L_0x14bb
            r6 = 1
            goto L_0x14bc
        L_0x14bb:
            r6 = 0
        L_0x14bc:
            r60 = r59
            r61 = r1
            r62 = r15
            r63 = r46
            r64 = r3
            r65 = r4
            r66 = r2
            r67 = r12
            r68 = r43
            r69 = r6
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67, r68, r69)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x14d5:
            r4 = 3
            if (r9 != r4) goto L_0x14f9
            if (r3 == 0) goto L_0x14df
            r8.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x14df:
            if (r7 == 0) goto L_0x14e3
            r3 = 1
            goto L_0x14e4
        L_0x14e3:
            r3 = 0
        L_0x14e4:
            r60 = r59
            r61 = r1
            r62 = r15
            r63 = r46
            r64 = r2
            r65 = r12
            r66 = r43
            r67 = r3
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x14f9:
            r4 = 6
            if (r9 != r4) goto L_0x1516
            if (r7 == 0) goto L_0x1500
            r3 = 1
            goto L_0x1501
        L_0x1500:
            r3 = 0
        L_0x1501:
            r60 = r59
            r61 = r1
            r62 = r15
            r63 = r46
            r64 = r2
            r65 = r12
            r66 = r43
            r67 = r3
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x1516:
            r4 = 7
            if (r9 != r4) goto L_0x153c
            if (r3 == 0) goto L_0x1522
            if (r2 == 0) goto L_0x1522
            r8.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x1522:
            if (r7 == 0) goto L_0x1526
            r3 = 1
            goto L_0x1527
        L_0x1526:
            r3 = 0
        L_0x1527:
            r60 = r59
            r61 = r1
            r62 = r15
            r63 = r46
            r64 = r2
            r65 = r12
            r66 = r43
            r67 = r3
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x153c:
            r4 = 8
            if (r9 != r4) goto L_0x1561
            if (r3 == 0) goto L_0x1547
            r8.performSendDelayedMessage(r2)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x1547:
            if (r7 == 0) goto L_0x154b
            r3 = 1
            goto L_0x154c
        L_0x154b:
            r3 = 0
        L_0x154c:
            r60 = r59
            r61 = r1
            r62 = r15
            r63 = r46
            r64 = r2
            r65 = r12
            r66 = r43
            r67 = r3
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x1561:
            r3 = 10
            if (r9 == r3) goto L_0x1569
            r3 = 11
            if (r9 != r3) goto L_0x1d1a
        L_0x1569:
            if (r7 == 0) goto L_0x156d
            r3 = 1
            goto L_0x156e
        L_0x156d:
            r3 = 0
        L_0x156e:
            r60 = r59
            r61 = r1
            r62 = r15
            r63 = r46
            r64 = r2
            r65 = r12
            r66 = r43
            r67 = r3
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67)     // Catch:{ Exception -> 0x1587 }
            goto L_0x1d1a
        L_0x1583:
            r0 = move-exception
            r8 = r59
            goto L_0x158c
        L_0x1587:
            r0 = move-exception
            goto L_0x158c
        L_0x1589:
            r0 = move-exception
            r5 = r18
        L_0x158c:
            r1 = r0
            r14 = r7
            goto L_0x1cea
        L_0x1590:
            r12 = r78
            r54 = r4
            r5 = r9
            r43 = r13
            r3 = r14
            r7 = r22
            r6 = r37
            r14 = r39
            r13 = r42
            r36 = r47
            r55 = r50
            r10 = r52
            r9 = r1
            r1 = r62
            int r4 = r3.layer     // Catch:{ Exception -> 0x1b7d }
            int r4 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r4)     // Catch:{ Exception -> 0x1b7d }
            r60 = r2
            r2 = 73
            if (r4 < r2) goto L_0x15ce
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x15c9 }
            r2.<init>()     // Catch:{ Exception -> 0x15c9 }
            int r4 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x15d3
            r2.grouped_id = r10     // Catch:{ Exception -> 0x15c9 }
            int r4 = r2.flags     // Catch:{ Exception -> 0x15c9 }
            r18 = 131072(0x20000, float:1.83671E-40)
            r4 = r4 | r18
            r2.flags = r4     // Catch:{ Exception -> 0x15c9 }
            goto L_0x15d3
        L_0x15c9:
            r0 = move-exception
            r14 = r82
            goto L_0x034d
        L_0x15ce:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x1b7d }
            r2.<init>()     // Catch:{ Exception -> 0x1b7d }
        L_0x15d3:
            int r4 = r5.ttl     // Catch:{ Exception -> 0x1b7d }
            r2.ttl = r4     // Catch:{ Exception -> 0x1b7d }
            if (r12 == 0) goto L_0x15e7
            boolean r4 = r78.isEmpty()     // Catch:{ Exception -> 0x15c9 }
            if (r4 != 0) goto L_0x15e7
            r2.entities = r12     // Catch:{ Exception -> 0x15c9 }
            int r4 = r2.flags     // Catch:{ Exception -> 0x15c9 }
            r4 = r4 | 128(0x80, float:1.794E-43)
            r2.flags = r4     // Catch:{ Exception -> 0x15c9 }
        L_0x15e7:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r4 = r5.reply_to     // Catch:{ Exception -> 0x1b7d }
            r52 = r10
            if (r4 == 0) goto L_0x15fc
            long r10 = r4.reply_to_random_id     // Catch:{ Exception -> 0x15c9 }
            int r4 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r4 == 0) goto L_0x15fc
            r2.reply_to_random_id = r10     // Catch:{ Exception -> 0x15c9 }
            int r4 = r2.flags     // Catch:{ Exception -> 0x15c9 }
            r10 = 8
            r4 = r4 | r10
            r2.flags = r4     // Catch:{ Exception -> 0x15c9 }
        L_0x15fc:
            boolean r4 = r5.silent     // Catch:{ Exception -> 0x1b7d }
            r2.silent = r4     // Catch:{ Exception -> 0x1b7d }
            int r4 = r2.flags     // Catch:{ Exception -> 0x1b7d }
            r4 = r4 | 512(0x200, float:7.175E-43)
            r2.flags = r4     // Catch:{ Exception -> 0x1b7d }
            if (r43 == 0) goto L_0x1621
            r10 = r66
            r4 = r43
            java.lang.Object r11 = r4.get(r10)     // Catch:{ Exception -> 0x15c9 }
            if (r11 == 0) goto L_0x1623
            java.lang.Object r10 = r4.get(r10)     // Catch:{ Exception -> 0x15c9 }
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ Exception -> 0x15c9 }
            r2.via_bot_name = r10     // Catch:{ Exception -> 0x15c9 }
            int r10 = r2.flags     // Catch:{ Exception -> 0x15c9 }
            r10 = r10 | 2048(0x800, float:2.87E-42)
            r2.flags = r10     // Catch:{ Exception -> 0x15c9 }
            goto L_0x1623
        L_0x1621:
            r4 = r43
        L_0x1623:
            long r10 = r5.random_id     // Catch:{ Exception -> 0x1b7d }
            r2.random_id = r10     // Catch:{ Exception -> 0x1b7d }
            r2.message = r6     // Catch:{ Exception -> 0x1b7d }
            r6 = 1
            if (r9 != r6) goto L_0x1682
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x15c9 }
            if (r1 == 0) goto L_0x1648
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue     // Catch:{ Exception -> 0x15c9 }
            r1.<init>()     // Catch:{ Exception -> 0x15c9 }
            r2.media = r1     // Catch:{ Exception -> 0x15c9 }
            java.lang.String r4 = r7.address     // Catch:{ Exception -> 0x15c9 }
            r1.address = r4     // Catch:{ Exception -> 0x15c9 }
            java.lang.String r4 = r7.title     // Catch:{ Exception -> 0x15c9 }
            r1.title = r4     // Catch:{ Exception -> 0x15c9 }
            java.lang.String r4 = r7.provider     // Catch:{ Exception -> 0x15c9 }
            r1.provider = r4     // Catch:{ Exception -> 0x15c9 }
            java.lang.String r4 = r7.venue_id     // Catch:{ Exception -> 0x15c9 }
            r1.venue_id = r4     // Catch:{ Exception -> 0x15c9 }
            goto L_0x164f
        L_0x1648:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint     // Catch:{ Exception -> 0x15c9 }
            r1.<init>()     // Catch:{ Exception -> 0x15c9 }
            r2.media = r1     // Catch:{ Exception -> 0x15c9 }
        L_0x164f:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r1 = r2.media     // Catch:{ Exception -> 0x15c9 }
            org.telegram.tgnet.TLRPC$GeoPoint r4 = r7.geo     // Catch:{ Exception -> 0x15c9 }
            double r6 = r4.lat     // Catch:{ Exception -> 0x15c9 }
            r1.lat = r6     // Catch:{ Exception -> 0x15c9 }
            double r6 = r4._long     // Catch:{ Exception -> 0x15c9 }
            r1._long = r6     // Catch:{ Exception -> 0x15c9 }
            org.telegram.messenger.SecretChatHelper r1 = r59.getSecretChatHelper()     // Catch:{ Exception -> 0x15c9 }
            org.telegram.tgnet.TLRPC$Message r4 = r15.messageOwner     // Catch:{ Exception -> 0x15c9 }
            r6 = 0
            r7 = 0
            r62 = r1
            r63 = r2
            r64 = r4
            r65 = r3
            r66 = r6
            r67 = r7
            r68 = r15
            r62.performSendEncryptedRequest(r63, r64, r65, r66, r67, r68)     // Catch:{ Exception -> 0x15c9 }
            r4 = r60
            r11 = r70
            r1 = r82
            r18 = r5
            r69 = r9
            r6 = r52
            goto L_0x191e
        L_0x1682:
            r6 = 2
            if (r9 == r6) goto L_0x19fa
            r6 = 9
            if (r9 != r6) goto L_0x1691
            if (r1 == 0) goto L_0x1691
            r14 = r68
            r11 = r70
            goto L_0x1a00
        L_0x1691:
            r1 = 3
            if (r9 != r1) goto L_0x17be
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r14.thumbs     // Catch:{ Exception -> 0x15c9 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r8.getThumbForSecretChat(r1)     // Catch:{ Exception -> 0x15c9 }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r1)     // Catch:{ Exception -> 0x15c9 }
            boolean r6 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r14)     // Catch:{ Exception -> 0x15c9 }
            if (r6 != 0) goto L_0x16c4
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r14)     // Catch:{ Exception -> 0x15c9 }
            if (r6 == 0) goto L_0x16aa
            goto L_0x16c4
        L_0x16aa:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo     // Catch:{ Exception -> 0x15c9 }
            r6.<init>()     // Catch:{ Exception -> 0x15c9 }
            r2.media = r6     // Catch:{ Exception -> 0x15c9 }
            if (r1 == 0) goto L_0x16bc
            byte[] r7 = r1.bytes     // Catch:{ Exception -> 0x15c9 }
            if (r7 == 0) goto L_0x16bc
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r6     // Catch:{ Exception -> 0x15c9 }
            r6.thumb = r7     // Catch:{ Exception -> 0x15c9 }
            goto L_0x16e1
        L_0x16bc:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r6     // Catch:{ Exception -> 0x15c9 }
            r7 = 0
            byte[] r10 = new byte[r7]     // Catch:{ Exception -> 0x15c9 }
            r6.thumb = r10     // Catch:{ Exception -> 0x15c9 }
            goto L_0x16e1
        L_0x16c4:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x15c9 }
            r6.<init>()     // Catch:{ Exception -> 0x15c9 }
            r2.media = r6     // Catch:{ Exception -> 0x15c9 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r14.attributes     // Catch:{ Exception -> 0x15c9 }
            r6.attributes = r7     // Catch:{ Exception -> 0x15c9 }
            if (r1 == 0) goto L_0x16da
            byte[] r7 = r1.bytes     // Catch:{ Exception -> 0x15c9 }
            if (r7 == 0) goto L_0x16da
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r6     // Catch:{ Exception -> 0x15c9 }
            r6.thumb = r7     // Catch:{ Exception -> 0x15c9 }
            goto L_0x16e1
        L_0x16da:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r6     // Catch:{ Exception -> 0x15c9 }
            r7 = 0
            byte[] r10 = new byte[r7]     // Catch:{ Exception -> 0x15c9 }
            r6.thumb = r10     // Catch:{ Exception -> 0x15c9 }
        L_0x16e1:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r2.media     // Catch:{ Exception -> 0x15c9 }
            r6.caption = r13     // Catch:{ Exception -> 0x15c9 }
            java.lang.String r7 = "video/mp4"
            r6.mime_type = r7     // Catch:{ Exception -> 0x15c9 }
            int r7 = r14.size     // Catch:{ Exception -> 0x15c9 }
            r6.size = r7     // Catch:{ Exception -> 0x15c9 }
            r6 = 0
        L_0x16ee:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r14.attributes     // Catch:{ Exception -> 0x15c9 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x15c9 }
            if (r6 >= r7) goto L_0x1714
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r14.attributes     // Catch:{ Exception -> 0x15c9 }
            java.lang.Object r7 = r7.get(r6)     // Catch:{ Exception -> 0x15c9 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r7 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r7     // Catch:{ Exception -> 0x15c9 }
            boolean r10 = r7 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x15c9 }
            if (r10 == 0) goto L_0x1711
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r2.media     // Catch:{ Exception -> 0x15c9 }
            int r10 = r7.w     // Catch:{ Exception -> 0x15c9 }
            r6.w = r10     // Catch:{ Exception -> 0x15c9 }
            int r10 = r7.h     // Catch:{ Exception -> 0x15c9 }
            r6.h = r10     // Catch:{ Exception -> 0x15c9 }
            int r7 = r7.duration     // Catch:{ Exception -> 0x15c9 }
            r6.duration = r7     // Catch:{ Exception -> 0x15c9 }
            goto L_0x1714
        L_0x1711:
            int r6 = r6 + 1
            goto L_0x16ee
        L_0x1714:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r2.media     // Catch:{ Exception -> 0x15c9 }
            int r7 = r1.h     // Catch:{ Exception -> 0x15c9 }
            r6.thumb_h = r7     // Catch:{ Exception -> 0x15c9 }
            int r1 = r1.w     // Catch:{ Exception -> 0x15c9 }
            r6.thumb_w = r1     // Catch:{ Exception -> 0x15c9 }
            byte[] r1 = r14.key     // Catch:{ Exception -> 0x15c9 }
            if (r1 == 0) goto L_0x1761
            int r1 = (r52 > r16 ? 1 : (r52 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1727
            goto L_0x1761
        L_0x1727:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x15c9 }
            r1.<init>()     // Catch:{ Exception -> 0x15c9 }
            long r6 = r14.id     // Catch:{ Exception -> 0x15c9 }
            r1.id = r6     // Catch:{ Exception -> 0x15c9 }
            long r6 = r14.access_hash     // Catch:{ Exception -> 0x15c9 }
            r1.access_hash = r6     // Catch:{ Exception -> 0x15c9 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r2.media     // Catch:{ Exception -> 0x15c9 }
            byte[] r6 = r14.key     // Catch:{ Exception -> 0x15c9 }
            r4.key = r6     // Catch:{ Exception -> 0x15c9 }
            byte[] r6 = r14.iv     // Catch:{ Exception -> 0x15c9 }
            r4.iv = r6     // Catch:{ Exception -> 0x15c9 }
            org.telegram.messenger.SecretChatHelper r4 = r59.getSecretChatHelper()     // Catch:{ Exception -> 0x15c9 }
            org.telegram.tgnet.TLRPC$Message r6 = r15.messageOwner     // Catch:{ Exception -> 0x15c9 }
            r7 = 0
            r62 = r4
            r63 = r2
            r64 = r6
            r65 = r3
            r66 = r1
            r67 = r7
            r68 = r15
            r62.performSendEncryptedRequest(r63, r64, r65, r66, r67, r68)     // Catch:{ Exception -> 0x15c9 }
            r1 = r60
            r11 = r70
            r3 = r82
            r10 = r46
            r6 = r52
            goto L_0x17b0
        L_0x1761:
            if (r60 != 0) goto L_0x179b
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x15c9 }
            r11 = r70
            r6 = r52
            r1.<init>(r11)     // Catch:{ Exception -> 0x15c9 }
            r1.encryptedChat = r3     // Catch:{ Exception -> 0x15c9 }
            r3 = 1
            r1.type = r3     // Catch:{ Exception -> 0x15c9 }
            r1.sendEncryptedRequest = r2     // Catch:{ Exception -> 0x15c9 }
            r10 = r46
            r1.originalPath = r10     // Catch:{ Exception -> 0x15c9 }
            r1.obj = r15     // Catch:{ Exception -> 0x15c9 }
            if (r4 == 0) goto L_0x178a
            r3 = r44
            boolean r13 = r4.containsKey(r3)     // Catch:{ Exception -> 0x15c9 }
            if (r13 == 0) goto L_0x178a
            java.lang.Object r3 = r4.get(r3)     // Catch:{ Exception -> 0x15c9 }
            r1.parentObject = r3     // Catch:{ Exception -> 0x15c9 }
            goto L_0x178e
        L_0x178a:
            r3 = r68
            r1.parentObject = r3     // Catch:{ Exception -> 0x15c9 }
        L_0x178e:
            r3 = 1
            r1.performMediaUpload = r3     // Catch:{ Exception -> 0x15c9 }
            r3 = r82
            if (r3 == 0) goto L_0x1797
            r4 = 1
            goto L_0x1798
        L_0x1797:
            r4 = 0
        L_0x1798:
            r1.scheduled = r4     // Catch:{ Exception -> 0x17b9 }
            goto L_0x17a5
        L_0x179b:
            r11 = r70
            r3 = r82
            r10 = r46
            r6 = r52
            r1 = r60
        L_0x17a5:
            r4 = r54
            r1.videoEditedInfo = r4     // Catch:{ Exception -> 0x17b9 }
            int r4 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x17b0
            r8.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x17b9 }
        L_0x17b0:
            r4 = r1
            r14 = r3
            r18 = r5
            r69 = r9
        L_0x17b6:
            r5 = r10
            goto L_0x1b13
        L_0x17b9:
            r0 = move-exception
            r1 = r0
            r14 = r3
            goto L_0x1cea
        L_0x17be:
            r11 = r70
            r1 = r82
            r43 = r4
            r18 = r5
            r56 = r44
            r10 = r46
            r6 = r52
            r4 = 6
            r5 = r68
            if (r9 != r4) goto L_0x180f
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact     // Catch:{ Exception -> 0x1809 }
            r4.<init>()     // Catch:{ Exception -> 0x1809 }
            r2.media = r4     // Catch:{ Exception -> 0x1809 }
            r5 = r55
            java.lang.String r13 = r5.phone     // Catch:{ Exception -> 0x1809 }
            r4.phone_number = r13     // Catch:{ Exception -> 0x1809 }
            java.lang.String r13 = r5.first_name     // Catch:{ Exception -> 0x1809 }
            r4.first_name = r13     // Catch:{ Exception -> 0x1809 }
            java.lang.String r13 = r5.last_name     // Catch:{ Exception -> 0x1809 }
            r4.last_name = r13     // Catch:{ Exception -> 0x1809 }
            long r13 = r5.id     // Catch:{ Exception -> 0x1809 }
            r4.user_id = r13     // Catch:{ Exception -> 0x1809 }
            org.telegram.messenger.SecretChatHelper r4 = r59.getSecretChatHelper()     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$Message r5 = r15.messageOwner     // Catch:{ Exception -> 0x1809 }
            r13 = 0
            r14 = 0
            r62 = r4
            r63 = r2
            r64 = r5
            r65 = r3
            r66 = r13
            r67 = r14
            r68 = r15
            r62.performSendEncryptedRequest(r63, r64, r65, r66, r67, r68)     // Catch:{ Exception -> 0x1809 }
            r4 = r60
            r69 = r9
            goto L_0x188e
        L_0x1809:
            r0 = move-exception
            r14 = r1
            r5 = r18
            goto L_0x034d
        L_0x180f:
            r4 = 7
            if (r9 == r4) goto L_0x1892
            r4 = 9
            if (r9 != r4) goto L_0x181e
            if (r14 == 0) goto L_0x181e
            r68 = r5
            r69 = r9
            goto L_0x1896
        L_0x181e:
            r4 = 8
            if (r9 != r4) goto L_0x188a
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r4 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1809 }
            r4.<init>(r11)     // Catch:{ Exception -> 0x1809 }
            r4.encryptedChat = r3     // Catch:{ Exception -> 0x1809 }
            r4.sendEncryptedRequest = r2     // Catch:{ Exception -> 0x1809 }
            r4.obj = r15     // Catch:{ Exception -> 0x1809 }
            r3 = 3
            r4.type = r3     // Catch:{ Exception -> 0x1809 }
            r4.parentObject = r5     // Catch:{ Exception -> 0x1809 }
            r3 = 1
            r4.performMediaUpload = r3     // Catch:{ Exception -> 0x1809 }
            if (r1 == 0) goto L_0x1839
            r3 = 1
            goto L_0x183a
        L_0x1839:
            r3 = 0
        L_0x183a:
            r4.scheduled = r3     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1809 }
            r3.<init>()     // Catch:{ Exception -> 0x1809 }
            r2.media = r3     // Catch:{ Exception -> 0x1809 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r14.attributes     // Catch:{ Exception -> 0x1809 }
            r3.attributes = r5     // Catch:{ Exception -> 0x1809 }
            r3.caption = r13     // Catch:{ Exception -> 0x1809 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r14.thumbs     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r8.getThumbForSecretChat(r3)     // Catch:{ Exception -> 0x1809 }
            if (r3 == 0) goto L_0x1868
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r2.media     // Catch:{ Exception -> 0x1809 }
            r13 = r5
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r13 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r13     // Catch:{ Exception -> 0x1809 }
            r69 = r9
            byte[] r9 = r3.bytes     // Catch:{ Exception -> 0x1809 }
            r13.thumb = r9     // Catch:{ Exception -> 0x1809 }
            int r9 = r3.h     // Catch:{ Exception -> 0x1809 }
            r5.thumb_h = r9     // Catch:{ Exception -> 0x1809 }
            int r3 = r3.w     // Catch:{ Exception -> 0x1809 }
            r5.thumb_w = r3     // Catch:{ Exception -> 0x1809 }
            goto L_0x1878
        L_0x1868:
            r69 = r9
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r2.media     // Catch:{ Exception -> 0x1809 }
            r5 = r3
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x1809 }
            r9 = 0
            byte[] r13 = new byte[r9]     // Catch:{ Exception -> 0x1809 }
            r5.thumb = r13     // Catch:{ Exception -> 0x1809 }
            r3.thumb_h = r9     // Catch:{ Exception -> 0x1809 }
            r3.thumb_w = r9     // Catch:{ Exception -> 0x1809 }
        L_0x1878:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r2.media     // Catch:{ Exception -> 0x1809 }
            java.lang.String r5 = r14.mime_type     // Catch:{ Exception -> 0x1809 }
            r3.mime_type = r5     // Catch:{ Exception -> 0x1809 }
            int r5 = r14.size     // Catch:{ Exception -> 0x1809 }
            r3.size = r5     // Catch:{ Exception -> 0x1809 }
            r4.originalPath = r10     // Catch:{ Exception -> 0x1809 }
            r8.performSendDelayedMessage(r4)     // Catch:{ Exception -> 0x1809 }
            r14 = r1
            goto L_0x17b6
        L_0x188a:
            r69 = r9
            r4 = r60
        L_0x188e:
            r46 = r10
            goto L_0x191e
        L_0x1892:
            r69 = r9
            r68 = r5
        L_0x1896:
            long r4 = r14.access_hash     // Catch:{ Exception -> 0x1809 }
            int r9 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r9 == 0) goto L_0x1923
            boolean r4 = org.telegram.messenger.MessageObject.isStickerDocument(r14)     // Catch:{ Exception -> 0x1809 }
            if (r4 != 0) goto L_0x18a9
            r4 = 1
            boolean r5 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r14, r4)     // Catch:{ Exception -> 0x1809 }
            if (r5 == 0) goto L_0x1923
        L_0x18a9:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r4 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument     // Catch:{ Exception -> 0x1809 }
            r4.<init>()     // Catch:{ Exception -> 0x1809 }
            r2.media = r4     // Catch:{ Exception -> 0x1809 }
            r46 = r10
            long r9 = r14.id     // Catch:{ Exception -> 0x1809 }
            r4.id = r9     // Catch:{ Exception -> 0x1809 }
            int r5 = r14.date     // Catch:{ Exception -> 0x1809 }
            r4.date = r5     // Catch:{ Exception -> 0x1809 }
            long r9 = r14.access_hash     // Catch:{ Exception -> 0x1809 }
            r4.access_hash = r9     // Catch:{ Exception -> 0x1809 }
            java.lang.String r5 = r14.mime_type     // Catch:{ Exception -> 0x1809 }
            r4.mime_type = r5     // Catch:{ Exception -> 0x1809 }
            int r5 = r14.size     // Catch:{ Exception -> 0x1809 }
            r4.size = r5     // Catch:{ Exception -> 0x1809 }
            int r5 = r14.dc_id     // Catch:{ Exception -> 0x1809 }
            r4.dc_id = r5     // Catch:{ Exception -> 0x1809 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r14.attributes     // Catch:{ Exception -> 0x1809 }
            r4.attributes = r5     // Catch:{ Exception -> 0x1809 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r14.thumbs     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r8.getThumbForSecretChat(r4)     // Catch:{ Exception -> 0x1809 }
            if (r4 == 0) goto L_0x18dd
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r2.media     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r5     // Catch:{ Exception -> 0x1809 }
            r5.thumb = r4     // Catch:{ Exception -> 0x1809 }
            goto L_0x18f2
        L_0x18dd:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r2.media     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r4 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r4     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r5 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty     // Catch:{ Exception -> 0x1809 }
            r5.<init>()     // Catch:{ Exception -> 0x1809 }
            r4.thumb = r5     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r4 = r2.media     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r4 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r4     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = r4.thumb     // Catch:{ Exception -> 0x1809 }
            java.lang.String r5 = "s"
            r4.type = r5     // Catch:{ Exception -> 0x1809 }
        L_0x18f2:
            if (r60 == 0) goto L_0x1903
            r4 = r60
            int r5 = r4.type     // Catch:{ Exception -> 0x1809 }
            r9 = 5
            if (r5 != r9) goto L_0x1905
            r4.sendEncryptedRequest = r2     // Catch:{ Exception -> 0x1809 }
            r4.obj = r15     // Catch:{ Exception -> 0x1809 }
            r8.performSendDelayedMessage(r4)     // Catch:{ Exception -> 0x1809 }
            goto L_0x191e
        L_0x1903:
            r4 = r60
        L_0x1905:
            org.telegram.messenger.SecretChatHelper r5 = r59.getSecretChatHelper()     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$Message r9 = r15.messageOwner     // Catch:{ Exception -> 0x1809 }
            r10 = 0
            r13 = 0
            r62 = r5
            r63 = r2
            r64 = r9
            r65 = r3
            r66 = r10
            r67 = r13
            r68 = r15
            r62.performSendEncryptedRequest(r63, r64, r65, r66, r67, r68)     // Catch:{ Exception -> 0x1809 }
        L_0x191e:
            r14 = r1
            r5 = r46
            goto L_0x1b13
        L_0x1923:
            r4 = r60
            r46 = r10
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1809 }
            r5.<init>()     // Catch:{ Exception -> 0x1809 }
            r2.media = r5     // Catch:{ Exception -> 0x1809 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r9 = r14.attributes     // Catch:{ Exception -> 0x1809 }
            r5.attributes = r9     // Catch:{ Exception -> 0x1809 }
            r5.caption = r13     // Catch:{ Exception -> 0x1809 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r14.thumbs     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r8.getThumbForSecretChat(r5)     // Catch:{ Exception -> 0x1809 }
            if (r5 == 0) goto L_0x1951
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r5)     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r9 = r2.media     // Catch:{ Exception -> 0x1809 }
            r10 = r9
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r10 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r10     // Catch:{ Exception -> 0x1809 }
            byte[] r13 = r5.bytes     // Catch:{ Exception -> 0x1809 }
            r10.thumb = r13     // Catch:{ Exception -> 0x1809 }
            int r10 = r5.h     // Catch:{ Exception -> 0x1809 }
            r9.thumb_h = r10     // Catch:{ Exception -> 0x1809 }
            int r5 = r5.w     // Catch:{ Exception -> 0x1809 }
            r9.thumb_w = r5     // Catch:{ Exception -> 0x1809 }
            goto L_0x195f
        L_0x1951:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r2.media     // Catch:{ Exception -> 0x1809 }
            r9 = r5
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r9 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r9     // Catch:{ Exception -> 0x1809 }
            r10 = 0
            byte[] r13 = new byte[r10]     // Catch:{ Exception -> 0x1809 }
            r9.thumb = r13     // Catch:{ Exception -> 0x1809 }
            r5.thumb_h = r10     // Catch:{ Exception -> 0x1809 }
            r5.thumb_w = r10     // Catch:{ Exception -> 0x1809 }
        L_0x195f:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r2.media     // Catch:{ Exception -> 0x1809 }
            int r9 = r14.size     // Catch:{ Exception -> 0x1809 }
            r5.size = r9     // Catch:{ Exception -> 0x1809 }
            java.lang.String r9 = r14.mime_type     // Catch:{ Exception -> 0x1809 }
            r5.mime_type = r9     // Catch:{ Exception -> 0x1809 }
            byte[] r5 = r14.key     // Catch:{ Exception -> 0x1809 }
            if (r5 == 0) goto L_0x19a4
            int r5 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r5 == 0) goto L_0x1972
            goto L_0x19a4
        L_0x1972:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r5 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1809 }
            r5.<init>()     // Catch:{ Exception -> 0x1809 }
            long r9 = r14.id     // Catch:{ Exception -> 0x1809 }
            r5.id = r9     // Catch:{ Exception -> 0x1809 }
            long r9 = r14.access_hash     // Catch:{ Exception -> 0x1809 }
            r5.access_hash = r9     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r9 = r2.media     // Catch:{ Exception -> 0x1809 }
            byte[] r10 = r14.key     // Catch:{ Exception -> 0x1809 }
            r9.key = r10     // Catch:{ Exception -> 0x1809 }
            byte[] r10 = r14.iv     // Catch:{ Exception -> 0x1809 }
            r9.iv = r10     // Catch:{ Exception -> 0x1809 }
            org.telegram.messenger.SecretChatHelper r9 = r59.getSecretChatHelper()     // Catch:{ Exception -> 0x1809 }
            org.telegram.tgnet.TLRPC$Message r10 = r15.messageOwner     // Catch:{ Exception -> 0x1809 }
            r13 = 0
            r62 = r9
            r63 = r2
            r64 = r10
            r65 = r3
            r66 = r5
            r67 = r13
            r68 = r15
            r62.performSendEncryptedRequest(r63, r64, r65, r66, r67, r68)     // Catch:{ Exception -> 0x1809 }
            r5 = r46
            goto L_0x19f7
        L_0x19a4:
            if (r4 != 0) goto L_0x19da
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r4 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1809 }
            r4.<init>(r11)     // Catch:{ Exception -> 0x1809 }
            r4.encryptedChat = r3     // Catch:{ Exception -> 0x1809 }
            r3 = 2
            r4.type = r3     // Catch:{ Exception -> 0x1809 }
            r4.sendEncryptedRequest = r2     // Catch:{ Exception -> 0x1809 }
            r5 = r46
            r4.originalPath = r5     // Catch:{ Exception -> 0x1809 }
            r4.obj = r15     // Catch:{ Exception -> 0x1809 }
            if (r43 == 0) goto L_0x19cb
            r9 = r43
            r3 = r56
            boolean r10 = r9.containsKey(r3)     // Catch:{ Exception -> 0x1809 }
            if (r10 == 0) goto L_0x19cb
            java.lang.Object r3 = r9.get(r3)     // Catch:{ Exception -> 0x1809 }
            r4.parentObject = r3     // Catch:{ Exception -> 0x1809 }
            goto L_0x19cf
        L_0x19cb:
            r14 = r68
            r4.parentObject = r14     // Catch:{ Exception -> 0x1809 }
        L_0x19cf:
            r3 = 1
            r4.performMediaUpload = r3     // Catch:{ Exception -> 0x1809 }
            if (r1 == 0) goto L_0x19d6
            r3 = 1
            goto L_0x19d7
        L_0x19d6:
            r3 = 0
        L_0x19d7:
            r4.scheduled = r3     // Catch:{ Exception -> 0x1809 }
            goto L_0x19dc
        L_0x19da:
            r5 = r46
        L_0x19dc:
            r10 = r72
            if (r10 == 0) goto L_0x19f0
            int r3 = r72.length()     // Catch:{ Exception -> 0x1809 }
            if (r3 <= 0) goto L_0x19f0
            r3 = r36
            boolean r3 = r10.startsWith(r3)     // Catch:{ Exception -> 0x1809 }
            if (r3 == 0) goto L_0x19f0
            r4.httpLocation = r10     // Catch:{ Exception -> 0x1809 }
        L_0x19f0:
            int r3 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x19f7
            r8.performSendDelayedMessage(r4)     // Catch:{ Exception -> 0x1809 }
        L_0x19f7:
            r14 = r1
            goto L_0x1b13
        L_0x19fa:
            r14 = r68
            r11 = r70
            r10 = r82
        L_0x1a00:
            r18 = r5
            r69 = r9
            r57 = r36
            r5 = r46
            r6 = r52
            r9 = r4
            r4 = r60
            r60 = r3
            r3 = r44
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r1.sizes     // Catch:{ Exception -> 0x1b76 }
            r68 = r14
            r14 = 0
            java.lang.Object r10 = r10.get(r14)     // Catch:{ Exception -> 0x1b76 }
            org.telegram.tgnet.TLRPC$PhotoSize r10 = (org.telegram.tgnet.TLRPC$PhotoSize) r10     // Catch:{ Exception -> 0x1b76 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r1.sizes     // Catch:{ Exception -> 0x1b76 }
            int r19 = r14.size()     // Catch:{ Exception -> 0x1b76 }
            r21 = r1
            r20 = 1
            int r1 = r19 + -1
            java.lang.Object r1 = r14.get(r1)     // Catch:{ Exception -> 0x1b76 }
            org.telegram.tgnet.TLRPC$PhotoSize r1 = (org.telegram.tgnet.TLRPC$PhotoSize) r1     // Catch:{ Exception -> 0x1b76 }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r10)     // Catch:{ Exception -> 0x1b76 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r14 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto     // Catch:{ Exception -> 0x1b76 }
            r14.<init>()     // Catch:{ Exception -> 0x1b76 }
            r2.media = r14     // Catch:{ Exception -> 0x1b76 }
            r14.caption = r13     // Catch:{ Exception -> 0x1b76 }
            byte[] r13 = r10.bytes     // Catch:{ Exception -> 0x1b76 }
            if (r13 == 0) goto L_0x1a48
            r44 = r3
            r3 = r14
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r3 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r3     // Catch:{ Exception -> 0x12a4 }
            r3.thumb = r13     // Catch:{ Exception -> 0x12a4 }
            r43 = r9
            goto L_0x1a54
        L_0x1a48:
            r44 = r3
            r3 = r14
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r3 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r3     // Catch:{ Exception -> 0x1b76 }
            r43 = r9
            r13 = 0
            byte[] r9 = new byte[r13]     // Catch:{ Exception -> 0x1b76 }
            r3.thumb = r9     // Catch:{ Exception -> 0x1b76 }
        L_0x1a54:
            int r3 = r10.h     // Catch:{ Exception -> 0x1b76 }
            r14.thumb_h = r3     // Catch:{ Exception -> 0x1b76 }
            int r3 = r10.w     // Catch:{ Exception -> 0x1b76 }
            r14.thumb_w = r3     // Catch:{ Exception -> 0x1b76 }
            int r3 = r1.w     // Catch:{ Exception -> 0x1b76 }
            r14.w = r3     // Catch:{ Exception -> 0x1b76 }
            int r3 = r1.h     // Catch:{ Exception -> 0x1b76 }
            r14.h = r3     // Catch:{ Exception -> 0x1b76 }
            int r3 = r1.size     // Catch:{ Exception -> 0x1b76 }
            r14.size = r3     // Catch:{ Exception -> 0x1b76 }
            org.telegram.tgnet.TLRPC$FileLocation r3 = r1.location     // Catch:{ Exception -> 0x1b76 }
            byte[] r3 = r3.key     // Catch:{ Exception -> 0x1b76 }
            if (r3 == 0) goto L_0x1aa9
            int r3 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x1a73
            goto L_0x1aa9
        L_0x1a73:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x12a4 }
            r3.<init>()     // Catch:{ Exception -> 0x12a4 }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.location     // Catch:{ Exception -> 0x12a4 }
            long r9 = r1.volume_id     // Catch:{ Exception -> 0x12a4 }
            r3.id = r9     // Catch:{ Exception -> 0x12a4 }
            long r9 = r1.secret     // Catch:{ Exception -> 0x12a4 }
            r3.access_hash = r9     // Catch:{ Exception -> 0x12a4 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r9 = r2.media     // Catch:{ Exception -> 0x12a4 }
            byte[] r10 = r1.key     // Catch:{ Exception -> 0x12a4 }
            r9.key = r10     // Catch:{ Exception -> 0x12a4 }
            byte[] r1 = r1.iv     // Catch:{ Exception -> 0x12a4 }
            r9.iv = r1     // Catch:{ Exception -> 0x12a4 }
            org.telegram.messenger.SecretChatHelper r1 = r59.getSecretChatHelper()     // Catch:{ Exception -> 0x12a4 }
            org.telegram.tgnet.TLRPC$Message r9 = r15.messageOwner     // Catch:{ Exception -> 0x12a4 }
            r10 = 0
            r62 = r1
            r63 = r2
            r64 = r9
            r65 = r60
            r66 = r3
            r67 = r10
            r68 = r15
            r62.performSendEncryptedRequest(r63, r64, r65, r66, r67, r68)     // Catch:{ Exception -> 0x12a4 }
            r14 = r82
            r1 = r4
            goto L_0x1b12
        L_0x1aa9:
            if (r4 != 0) goto L_0x1adf
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x12a4 }
            r1.<init>(r11)     // Catch:{ Exception -> 0x12a4 }
            r4 = r60
            r1.encryptedChat = r4     // Catch:{ Exception -> 0x12a4 }
            r3 = 0
            r1.type = r3     // Catch:{ Exception -> 0x12a4 }
            r1.originalPath = r5     // Catch:{ Exception -> 0x12a4 }
            r1.sendEncryptedRequest = r2     // Catch:{ Exception -> 0x12a4 }
            r1.obj = r15     // Catch:{ Exception -> 0x12a4 }
            if (r43 == 0) goto L_0x1ad0
            r9 = r43
            r3 = r44
            boolean r4 = r9.containsKey(r3)     // Catch:{ Exception -> 0x12a4 }
            if (r4 == 0) goto L_0x1ad0
            java.lang.Object r3 = r9.get(r3)     // Catch:{ Exception -> 0x12a4 }
            r1.parentObject = r3     // Catch:{ Exception -> 0x12a4 }
            goto L_0x1ad4
        L_0x1ad0:
            r13 = r68
            r1.parentObject = r13     // Catch:{ Exception -> 0x12a4 }
        L_0x1ad4:
            r3 = 1
            r1.performMediaUpload = r3     // Catch:{ Exception -> 0x12a4 }
            if (r82 == 0) goto L_0x1adb
            r3 = 1
            goto L_0x1adc
        L_0x1adb:
            r3 = 0
        L_0x1adc:
            r1.scheduled = r3     // Catch:{ Exception -> 0x12a4 }
            goto L_0x1ae0
        L_0x1adf:
            r1 = r4
        L_0x1ae0:
            boolean r3 = android.text.TextUtils.isEmpty(r72)     // Catch:{ Exception -> 0x1b76 }
            if (r3 != 0) goto L_0x1af5
            r3 = r72
            r14 = r82
            r4 = r57
            boolean r4 = r3.startsWith(r4)     // Catch:{ Exception -> 0x1b1e }
            if (r4 == 0) goto L_0x1af7
            r1.httpLocation = r3     // Catch:{ Exception -> 0x1b1e }
            goto L_0x1b0b
        L_0x1af5:
            r14 = r82
        L_0x1af7:
            r4 = r21
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r4.sizes     // Catch:{ Exception -> 0x1b5d }
            int r9 = r3.size()     // Catch:{ Exception -> 0x1b5d }
            r10 = 1
            int r9 = r9 - r10
            java.lang.Object r3 = r3.get(r9)     // Catch:{ Exception -> 0x1b5d }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x1b5d }
            r1.photoSize = r3     // Catch:{ Exception -> 0x1b5d }
            r1.locationParent = r4     // Catch:{ Exception -> 0x1b5d }
        L_0x1b0b:
            int r3 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x1b12
            r8.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1b1e }
        L_0x1b12:
            r4 = r1
        L_0x1b13:
            int r1 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1b5f
            org.telegram.tgnet.TLObject r1 = r4.sendEncryptedRequest     // Catch:{ Exception -> 0x1b5d }
            if (r1 == 0) goto L_0x1b21
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r1 = (org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia) r1     // Catch:{ Exception -> 0x1b1e }
            goto L_0x1b28
        L_0x1b1e:
            r0 = move-exception
            goto L_0x12ab
        L_0x1b21:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r1 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia     // Catch:{ Exception -> 0x1b5d }
            r1.<init>()     // Catch:{ Exception -> 0x1b5d }
            r4.sendEncryptedRequest = r1     // Catch:{ Exception -> 0x1b5d }
        L_0x1b28:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r4.messageObjects     // Catch:{ Exception -> 0x1b5d }
            r3.add(r15)     // Catch:{ Exception -> 0x1b5d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r3 = r4.messages     // Catch:{ Exception -> 0x1b5d }
            r7 = r18
            r3.add(r7)     // Catch:{ Exception -> 0x1cb8 }
            java.util.ArrayList<java.lang.String> r3 = r4.originalPaths     // Catch:{ Exception -> 0x1cb8 }
            r3.add(r5)     // Catch:{ Exception -> 0x1cb8 }
            r3 = 1
            r4.performMediaUpload = r3     // Catch:{ Exception -> 0x1cb8 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r3 = r1.messages     // Catch:{ Exception -> 0x1cb8 }
            r3.add(r2)     // Catch:{ Exception -> 0x1cb8 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1cb8 }
            r2.<init>()     // Catch:{ Exception -> 0x1cb8 }
            r3 = r69
            r5 = 3
            if (r3 == r5) goto L_0x1b4e
            r5 = 7
            if (r3 != r5) goto L_0x1b50
        L_0x1b4e:
            r16 = 1
        L_0x1b50:
            r5 = r16
            r2.id = r5     // Catch:{ Exception -> 0x1cb8 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r1 = r1.files     // Catch:{ Exception -> 0x1cb8 }
            r1.add(r2)     // Catch:{ Exception -> 0x1cb8 }
            r8.performSendDelayedMessage(r4)     // Catch:{ Exception -> 0x1cb8 }
            goto L_0x1b61
        L_0x1b5d:
            r0 = move-exception
            goto L_0x1b79
        L_0x1b5f:
            r7 = r18
        L_0x1b61:
            if (r77 != 0) goto L_0x1d1a
            org.telegram.messenger.MediaDataController r1 = r59.getMediaDataController()     // Catch:{ Exception -> 0x1cb8 }
            if (r74 == 0) goto L_0x1b6f
            int r3 = r74.getId()     // Catch:{ Exception -> 0x1cb8 }
            r2 = 0
            goto L_0x1b71
        L_0x1b6f:
            r2 = 0
            r3 = 0
        L_0x1b71:
            r1.cleanDraft(r11, r3, r2)     // Catch:{ Exception -> 0x1cb8 }
            goto L_0x1d1a
        L_0x1b76:
            r0 = move-exception
            r14 = r82
        L_0x1b79:
            r7 = r18
            goto L_0x1cc3
        L_0x1b7d:
            r0 = move-exception
            r14 = r82
            r7 = r5
            goto L_0x034d
        L_0x1b83:
            r8 = r59
            r13 = r68
            r1 = r78
            r6 = r5
            r7 = r9
            r11 = r10
            r4 = r14
            r9 = r43
            r10 = r66
            r14 = r3
            r3 = r65
            if (r4 != 0) goto L_0x1c1d
            org.telegram.tgnet.TLRPC$TL_messages_sendMessage r2 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage     // Catch:{ Exception -> 0x1cb8 }
            r2.<init>()     // Catch:{ Exception -> 0x1cb8 }
            r2.message = r3     // Catch:{ Exception -> 0x1cb8 }
            if (r77 != 0) goto L_0x1ba1
            r3 = 1
            goto L_0x1ba2
        L_0x1ba1:
            r3 = 0
        L_0x1ba2:
            r2.clear_draft = r3     // Catch:{ Exception -> 0x1cb8 }
            boolean r3 = r7.silent     // Catch:{ Exception -> 0x1cb8 }
            r2.silent = r3     // Catch:{ Exception -> 0x1cb8 }
            r2.peer = r6     // Catch:{ Exception -> 0x1cb8 }
            long r3 = r7.random_id     // Catch:{ Exception -> 0x1cb8 }
            r2.random_id = r3     // Catch:{ Exception -> 0x1cb8 }
            org.telegram.tgnet.TLRPC$Peer r3 = r7.from_id     // Catch:{ Exception -> 0x1cb8 }
            if (r3 == 0) goto L_0x1bbe
            org.telegram.messenger.MessagesController r3 = r59.getMessagesController()     // Catch:{ Exception -> 0x1cb8 }
            org.telegram.tgnet.TLRPC$Peer r4 = r7.from_id     // Catch:{ Exception -> 0x1cb8 }
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r4)     // Catch:{ Exception -> 0x1cb8 }
            r2.send_as = r3     // Catch:{ Exception -> 0x1cb8 }
        L_0x1bbe:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r7.reply_to     // Catch:{ Exception -> 0x1cb8 }
            if (r3 == 0) goto L_0x1bce
            int r3 = r3.reply_to_msg_id     // Catch:{ Exception -> 0x1cb8 }
            if (r3 == 0) goto L_0x1bce
            int r4 = r2.flags     // Catch:{ Exception -> 0x1cb8 }
            r5 = 1
            r4 = r4 | r5
            r2.flags = r4     // Catch:{ Exception -> 0x1cb8 }
            r2.reply_to_msg_id = r3     // Catch:{ Exception -> 0x1cb8 }
        L_0x1bce:
            if (r76 != 0) goto L_0x1bd3
            r3 = 1
            r2.no_webpage = r3     // Catch:{ Exception -> 0x1cb8 }
        L_0x1bd3:
            if (r1 == 0) goto L_0x1be4
            boolean r3 = r78.isEmpty()     // Catch:{ Exception -> 0x1cb8 }
            if (r3 != 0) goto L_0x1be4
            r2.entities = r1     // Catch:{ Exception -> 0x1cb8 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x1cb8 }
            r3 = 8
            r1 = r1 | r3
            r2.flags = r1     // Catch:{ Exception -> 0x1cb8 }
        L_0x1be4:
            if (r14 == 0) goto L_0x1bee
            r2.schedule_date = r14     // Catch:{ Exception -> 0x1cb8 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x1cb8 }
            r1 = r1 | 1024(0x400, float:1.435E-42)
            r2.flags = r1     // Catch:{ Exception -> 0x1cb8 }
        L_0x1bee:
            r1 = 0
            r3 = 0
            if (r14 == 0) goto L_0x1bf4
            r4 = 1
            goto L_0x1bf5
        L_0x1bf4:
            r4 = 0
        L_0x1bf5:
            r60 = r59
            r61 = r2
            r62 = r15
            r63 = r1
            r64 = r3
            r65 = r13
            r66 = r9
            r67 = r4
            r60.performSendMessageRequest(r61, r62, r63, r64, r65, r66, r67)     // Catch:{ Exception -> 0x1cb8 }
            if (r77 != 0) goto L_0x1d1a
            org.telegram.messenger.MediaDataController r1 = r59.getMediaDataController()     // Catch:{ Exception -> 0x1cb8 }
            if (r74 == 0) goto L_0x1CLASSNAME
            int r3 = r74.getId()     // Catch:{ Exception -> 0x1cb8 }
            r2 = 0
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r2 = 0
            r3 = 0
        L_0x1CLASSNAME:
            r1.cleanDraft(r11, r3, r2)     // Catch:{ Exception -> 0x1cb8 }
            goto L_0x1d1a
        L_0x1c1d:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r2 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1cb8 }
            r2.<init>()     // Catch:{ Exception -> 0x1cb8 }
            int r5 = r7.ttl     // Catch:{ Exception -> 0x1cb8 }
            r2.ttl = r5     // Catch:{ Exception -> 0x1cb8 }
            if (r1 == 0) goto L_0x1CLASSNAME
            boolean r5 = r78.isEmpty()     // Catch:{ Exception -> 0x1cb8 }
            if (r5 != 0) goto L_0x1CLASSNAME
            r2.entities = r1     // Catch:{ Exception -> 0x1cb8 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x1cb8 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r2.flags = r1     // Catch:{ Exception -> 0x1cb8 }
        L_0x1CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r1 = r7.reply_to     // Catch:{ Exception -> 0x1cb8 }
            if (r1 == 0) goto L_0x1CLASSNAME
            long r5 = r1.reply_to_random_id     // Catch:{ Exception -> 0x1cb8 }
            int r1 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r1 == 0) goto L_0x1CLASSNAME
            r2.reply_to_random_id = r5     // Catch:{ Exception -> 0x1cb8 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x1cb8 }
            r5 = 8
            r1 = r1 | r5
            r2.flags = r1     // Catch:{ Exception -> 0x1cb8 }
        L_0x1CLASSNAME:
            if (r9 == 0) goto L_0x1c5f
            java.lang.Object r1 = r9.get(r10)     // Catch:{ Exception -> 0x1cb8 }
            if (r1 == 0) goto L_0x1c5f
            java.lang.Object r1 = r9.get(r10)     // Catch:{ Exception -> 0x1cb8 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x1cb8 }
            r2.via_bot_name = r1     // Catch:{ Exception -> 0x1cb8 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x1cb8 }
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r2.flags = r1     // Catch:{ Exception -> 0x1cb8 }
        L_0x1c5f:
            boolean r1 = r7.silent     // Catch:{ Exception -> 0x1cb8 }
            r2.silent = r1     // Catch:{ Exception -> 0x1cb8 }
            long r5 = r7.random_id     // Catch:{ Exception -> 0x1cb8 }
            r2.random_id = r5     // Catch:{ Exception -> 0x1cb8 }
            r2.message = r3     // Catch:{ Exception -> 0x1cb8 }
            r1 = r41
            if (r1 == 0) goto L_0x1CLASSNAME
            java.lang.String r3 = r1.url     // Catch:{ Exception -> 0x1cb8 }
            if (r3 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage     // Catch:{ Exception -> 0x1cb8 }
            r3.<init>()     // Catch:{ Exception -> 0x1cb8 }
            r2.media = r3     // Catch:{ Exception -> 0x1cb8 }
            java.lang.String r1 = r1.url     // Catch:{ Exception -> 0x1cb8 }
            r3.url = r1     // Catch:{ Exception -> 0x1cb8 }
            int r1 = r2.flags     // Catch:{ Exception -> 0x1cb8 }
            r1 = r1 | 512(0x200, float:7.175E-43)
            r2.flags = r1     // Catch:{ Exception -> 0x1cb8 }
            goto L_0x1c8a
        L_0x1CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty     // Catch:{ Exception -> 0x1cb8 }
            r1.<init>()     // Catch:{ Exception -> 0x1cb8 }
            r2.media = r1     // Catch:{ Exception -> 0x1cb8 }
        L_0x1c8a:
            org.telegram.messenger.SecretChatHelper r1 = r59.getSecretChatHelper()     // Catch:{ Exception -> 0x1cb8 }
            org.telegram.tgnet.TLRPC$Message r3 = r15.messageOwner     // Catch:{ Exception -> 0x1cb8 }
            r5 = 0
            r6 = 0
            r62 = r1
            r63 = r2
            r64 = r3
            r65 = r4
            r66 = r5
            r67 = r6
            r68 = r15
            r62.performSendEncryptedRequest(r63, r64, r65, r66, r67, r68)     // Catch:{ Exception -> 0x1cb8 }
            if (r77 != 0) goto L_0x1d1a
            org.telegram.messenger.MediaDataController r1 = r59.getMediaDataController()     // Catch:{ Exception -> 0x1cb8 }
            if (r74 == 0) goto L_0x1cb1
            int r3 = r74.getId()     // Catch:{ Exception -> 0x1cb8 }
            r2 = 0
            goto L_0x1cb3
        L_0x1cb1:
            r2 = 0
            r3 = 0
        L_0x1cb3:
            r1.cleanDraft(r11, r3, r2)     // Catch:{ Exception -> 0x1cb8 }
            goto L_0x1d1a
        L_0x1cb8:
            r0 = move-exception
            goto L_0x1cc3
        L_0x1cba:
            r0 = move-exception
            r8 = r1
            r14 = r3
            goto L_0x1cc2
        L_0x1cbe:
            r0 = move-exception
            r14 = r82
            r8 = r1
        L_0x1cc2:
            r7 = r9
        L_0x1cc3:
            r1 = r0
            r5 = r7
            goto L_0x1cea
        L_0x1cc6:
            r0 = move-exception
            r14 = r82
            r8 = r1
            goto L_0x1cd1
        L_0x1ccb:
            r0 = move-exception
            goto L_0x1cd0
        L_0x1ccd:
            r0 = move-exception
            r14 = r82
        L_0x1cd0:
            r8 = r6
        L_0x1cd1:
            r7 = r9
            r1 = r0
            r5 = r7
            goto L_0x1ce9
        L_0x1cd5:
            r0 = move-exception
            r14 = r82
            goto L_0x1cde
        L_0x1cd9:
            r0 = move-exception
            r14 = r82
            r22 = r2
        L_0x1cde:
            r8 = r6
            r1 = r0
            r5 = r22
            goto L_0x1ce9
        L_0x1ce3:
            r0 = move-exception
            r8 = r59
            r14 = r9
        L_0x1ce7:
            r1 = r0
        L_0x1ce8:
            r5 = 0
        L_0x1ce9:
            r15 = 0
        L_0x1cea:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            org.telegram.messenger.MessagesStorage r1 = r59.getMessagesStorage()
            if (r14 == 0) goto L_0x1cf5
            r3 = 1
            goto L_0x1cf6
        L_0x1cf5:
            r3 = 0
        L_0x1cf6:
            r1.markMessageAsSendError(r5, r3)
            if (r15 == 0) goto L_0x1d00
            org.telegram.tgnet.TLRPC$Message r1 = r15.messageOwner
            r2 = 2
            r1.send_state = r2
        L_0x1d00:
            org.telegram.messenger.NotificationCenter r1 = r59.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messageSendError
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r4 = r5.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r6 = 0
            r3[r6] = r4
            r1.postNotificationName(r2, r3)
            int r1 = r5.id
            r8.processSentMessage(r1)
        L_0x1d1a:
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
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new SendMessagesHelper$$ExternalSyntheticLambda85(this, delayedMessage2, str16));
            putToDelayedMessages(str16, delayedMessage2);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendDelayedMessage$33(DelayedMessage delayedMessage, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda41(this, tLObject, delayedMessage, str));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda42(this, tLObject, tLRPC$InputMedia, delayedMessage));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda27(this, str));
    }

    public void stopVideoService(String str) {
        getMessagesStorage().getStorageQueue().postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda25(this, str));
    }

    /* access modifiers changed from: protected */
    public void putToSendingMessages(TLRPC$Message tLRPC$Message, boolean z) {
        if (Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda53(this, tLRPC$Message, z));
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
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda60(this, tLRPC$TL_messages_sendMultiMedia, delayedMessage, arrayList2, z));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda55(this, tLRPC$TL_error, tLObject, arrayList2, arrayList3, z, tLRPC$TL_messages_sendMultiMedia));
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
                    Utilities.stageQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda65(this, tLRPC$TL_updateNewMessage));
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
                    Utilities.stageQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda62(this, tLRPC$TL_updateNewChannelMessage));
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
                    SendMessagesHelper$$ExternalSyntheticLambda49 sendMessagesHelper$$ExternalSyntheticLambda49 = r0;
                    SendMessagesHelper$$ExternalSyntheticLambda49 sendMessagesHelper$$ExternalSyntheticLambda492 = new SendMessagesHelper$$ExternalSyntheticLambda49(this, tLRPC$Message7, i6, z, arrayList6, j, mediaExistanceFlags);
                    storageQueue.postRunnable(sendMessagesHelper$$ExternalSyntheticLambda49);
                    i4 = i7 + 1;
                    sparseArray = sparseArray4;
                    tLRPC$Updates3 = tLRPC$Updates4;
                    longSparseArray = longSparseArray;
                }
            }
            tLRPC$Updates = tLRPC$Updates3;
            z3 = true;
            Utilities.stageQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda67(this, tLRPC$Updates));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda47(this, tLRPC$Message, i, j, i2, z));
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
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda54(this, tLRPC$Message, z2, tLObject, delayedMessage2));
                return;
            }
        }
        if (tLObject3 instanceof TLRPC$TL_messages_editMessage) {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda57(this, tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, z2, tLObject));
        } else {
            AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda70(this, z2, tLRPC$TL_error, tLRPC$Message, tLObject2, messageObject, str, tLObject));
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
            Utilities.stageQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda69(this, tLRPC$Updates, tLRPC$Message, z));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda52(this, tLRPC$Message, z));
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda66 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda66
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda64 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda64
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda63 r13 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda63
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda68 r3 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda68
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda34 r13 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda34
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
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda48 r12 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda48
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda37(this, messageObject, tLRPC$Message, i, z));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda46(this, tLRPC$Message, i, i2, z));
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda45(this, tLRPC$Message, tLRPC$Message.id));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performSendMessageRequest$61(TLRPC$Message tLRPC$Message, int i) {
        tLRPC$Message.send_state = 0;
        getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByAck, Integer.valueOf(i));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:119:0x021d, code lost:
        r6 = r2.location.volume_id + "_" + r2.location.local_id;
        r10 = r8.location.volume_id + "_" + r8.location.local_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0255, code lost:
        if (r6.equals(r10) == false) goto L_0x0258;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0258, code lost:
        r11 = new java.io.File(org.telegram.messenger.FileLoader.getDirectory(4), r6 + ".jpg");
        r12 = r7.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0275, code lost:
        if (r12.ttl_seconds != 0) goto L_0x0290;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x027f, code lost:
        if (r12.photo.sizes.size() == r3) goto L_0x028b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x0285, code lost:
        if (r8.w > 90) goto L_0x028b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x0289, code lost:
        if (r8.h <= 90) goto L_0x0290;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x028b, code lost:
        r12 = org.telegram.messenger.FileLoader.getPathToAttach(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0290, code lost:
        r12 = new java.io.File(org.telegram.messenger.FileLoader.getDirectory(4), r10 + ".jpg");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x02a9, code lost:
        r11.renameTo(r12);
        org.telegram.messenger.ImageLoader.getInstance().replaceImageInCache(r6, r10, org.telegram.messenger.ImageLocation.getForPhoto(r8, r7.media.photo), r1);
        r2.location = r8.location;
        r2.size = r8.size;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x02c3, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004c, code lost:
        r5 = (r5 = r7.media).photo;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x05b2, code lost:
        if (org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r7.media.document, true) != false) goto L_0x05b6;
     */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x05dd  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0640  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0107  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMediaPaths(org.telegram.messenger.MessageObject r19, org.telegram.tgnet.TLRPC$Message r20, int r21, java.lang.String r22, boolean r23) {
        /*
            r18 = this;
            r0 = r19
            r7 = r20
            r8 = r22
            r1 = r23
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            java.lang.String r4 = "_"
            if (r2 == 0) goto L_0x0138
            boolean r2 = r19.isLiveLocation()
            if (r2 == 0) goto L_0x0024
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0024
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            int r2 = r2.period
            r5.period = r2
            goto L_0x00d1
        L_0x0024:
            boolean r2 = r19.isDice()
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
            if (r10 == 0) goto L_0x0139
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r2 == 0) goto L_0x0139
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r10 = "stripped"
            r2.append(r10)
            java.lang.String r11 = org.telegram.messenger.FileRefController.getKeyForParentObject(r19)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            if (r7 == 0) goto L_0x0107
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            java.lang.String r10 = org.telegram.messenger.FileRefController.getKeyForParentObject(r20)
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            goto L_0x012c
        L_0x0107:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "strippedmessage"
            r10.append(r11)
            r11 = r21
            r10.append(r11)
            r10.append(r4)
            long r11 = r19.getChannelId()
            r10.append(r11)
            r10.append(r4)
            boolean r11 = r0.scheduled
            r10.append(r11)
            java.lang.String r10 = r10.toString()
        L_0x012c:
            org.telegram.messenger.ImageLoader r11 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForObject(r5, r6)
            r11.replaceImageInCache(r2, r10, r6, r1)
            goto L_0x0139
        L_0x0138:
            r5 = 0
        L_0x0139:
            if (r7 != 0) goto L_0x013c
            return
        L_0x013c:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            java.lang.String r12 = "sent_"
            java.lang.String r13 = ".jpg"
            r15 = 0
            r3 = 1
            if (r6 == 0) goto L_0x0351
            org.telegram.tgnet.TLRPC$Photo r6 = r2.photo
            if (r6 == 0) goto L_0x0351
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            boolean r14 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r14 == 0) goto L_0x0351
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            if (r6 == 0) goto L_0x0351
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x0184
            boolean r0 = r0.scheduled
            if (r0 != 0) goto L_0x0184
            org.telegram.messenger.MessagesStorage r0 = r18.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r12)
            org.telegram.tgnet.TLRPC$Peer r12 = r7.peer_id
            long r10 = r12.channel_id
            r6.append(r10)
            r6.append(r4)
            int r10 = r7.id
            r6.append(r10)
            java.lang.String r6 = r6.toString()
            r0.putSentFile(r8, r2, r15, r6)
        L_0x0184:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            int r0 = r0.size()
            if (r0 != r3) goto L_0x01b0
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            java.lang.Object r0 = r0.get(r15)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.location
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r0 == 0) goto L_0x01b0
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            r0.sizes = r1
            goto L_0x0333
        L_0x01b0:
            r0 = 0
        L_0x01b1:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x0333
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2
            if (r2 == 0) goto L_0x032d
            org.telegram.tgnet.TLRPC$FileLocation r6 = r2.location
            if (r6 == 0) goto L_0x032d
            java.lang.String r6 = r2.type
            if (r6 != 0) goto L_0x01d5
            goto L_0x032d
        L_0x01d5:
            r6 = 0
        L_0x01d6:
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r7.media
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            int r8 = r8.size()
            if (r6 >= r8) goto L_0x02ca
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r7.media
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            java.lang.Object r8 = r8.get(r6)
            org.telegram.tgnet.TLRPC$PhotoSize r8 = (org.telegram.tgnet.TLRPC$PhotoSize) r8
            if (r8 == 0) goto L_0x02c5
            org.telegram.tgnet.TLRPC$FileLocation r10 = r8.location
            if (r10 == 0) goto L_0x02c5
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r10 != 0) goto L_0x02c5
            java.lang.String r10 = r8.type
            if (r10 != 0) goto L_0x01fe
            goto L_0x02c5
        L_0x01fe:
            org.telegram.tgnet.TLRPC$FileLocation r11 = r2.location
            long r11 = r11.volume_id
            r16 = -2147483648(0xfffffffvar_, double:NaN)
            int r14 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x0211
            java.lang.String r11 = r2.type
            boolean r10 = r10.equals(r11)
            if (r10 != 0) goto L_0x021d
        L_0x0211:
            int r10 = r8.w
            int r11 = r2.w
            if (r10 != r11) goto L_0x02c5
            int r10 = r8.h
            int r11 = r2.h
            if (r10 != r11) goto L_0x02c5
        L_0x021d:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r10 = r2.location
            long r10 = r10.volume_id
            r6.append(r10)
            r6.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r10 = r2.location
            int r10 = r10.local_id
            r6.append(r10)
            java.lang.String r6 = r6.toString()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r11 = r8.location
            long r11 = r11.volume_id
            r10.append(r11)
            r10.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r11 = r8.location
            int r11 = r11.local_id
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            boolean r11 = r6.equals(r10)
            if (r11 == 0) goto L_0x0258
            goto L_0x02c3
        L_0x0258:
            java.io.File r11 = new java.io.File
            r12 = 4
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r12)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r6)
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r11.<init>(r14, r12)
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r7.media
            int r14 = r12.ttl_seconds
            if (r14 != 0) goto L_0x0290
            org.telegram.tgnet.TLRPC$Photo r12 = r12.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r12.sizes
            int r12 = r12.size()
            if (r12 == r3) goto L_0x028b
            int r12 = r8.w
            r14 = 90
            if (r12 > r14) goto L_0x028b
            int r12 = r8.h
            if (r12 <= r14) goto L_0x0290
        L_0x028b:
            java.io.File r12 = org.telegram.messenger.FileLoader.getPathToAttach(r8)
            goto L_0x02a9
        L_0x0290:
            java.io.File r12 = new java.io.File
            r14 = 4
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r14)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r10)
            r14.append(r13)
            java.lang.String r14 = r14.toString()
            r12.<init>(r3, r14)
        L_0x02a9:
            r11.renameTo(r12)
            org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r7.media
            org.telegram.tgnet.TLRPC$Photo r11 = r11.photo
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r8, (org.telegram.tgnet.TLRPC$Photo) r11)
            r3.replaceImageInCache(r6, r10, r11, r1)
            org.telegram.tgnet.TLRPC$FileLocation r3 = r8.location
            r2.location = r3
            int r3 = r8.size
            r2.size = r3
        L_0x02c3:
            r3 = 1
            goto L_0x02cb
        L_0x02c5:
            int r6 = r6 + 1
            r3 = 1
            goto L_0x01d6
        L_0x02ca:
            r3 = 0
        L_0x02cb:
            if (r3 != 0) goto L_0x032d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r6 = r2.location
            long r10 = r6.volume_id
            r3.append(r10)
            r3.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r6 = r2.location
            int r6 = r6.local_id
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            java.io.File r6 = new java.io.File
            r8 = 4
            java.io.File r10 = org.telegram.messenger.FileLoader.getDirectory(r8)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r3)
            r8.append(r13)
            java.lang.String r8 = r8.toString()
            r6.<init>(r10, r8)
            r6.delete()
            java.lang.String r2 = r2.type
            java.lang.String r6 = "s"
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L_0x032d
            if (r5 == 0) goto L_0x032d
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            r2.set(r0, r5)
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r5, (org.telegram.tgnet.TLRPC$Photo) r2)
            org.telegram.messenger.ImageLoader r6 = org.telegram.messenger.ImageLoader.getInstance()
            r10 = 0
            java.lang.String r8 = r2.getKey(r7, r10, r15)
            r6.replaceImageInCache(r3, r8, r2, r1)
            goto L_0x032e
        L_0x032d:
            r10 = 0
        L_0x032e:
            int r0 = r0 + 1
            r3 = 1
            goto L_0x01b1
        L_0x0333:
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
            goto L_0x069e
        L_0x0351:
            r10 = 0
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r3 == 0) goto L_0x0649
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            if (r3 == 0) goto L_0x0649
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r9.media
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x0649
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            if (r3 == 0) goto L_0x0649
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x0407
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            if (r2 == 0) goto L_0x037e
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r3 = r2.mediaEntities
            if (r3 != 0) goto L_0x0407
            java.lang.String r2 = r2.paintPath
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x0407
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            org.telegram.messenger.MediaController$CropState r2 = r2.cropState
            if (r2 != 0) goto L_0x0407
        L_0x037e:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r20)
            if (r2 != 0) goto L_0x038a
            boolean r3 = org.telegram.messenger.MessageObject.isGifMessage(r20)
            if (r3 == 0) goto L_0x03d0
        L_0x038a:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r5)
            if (r3 != r5) goto L_0x03d0
            boolean r3 = r0.scheduled
            if (r3 != 0) goto L_0x03c9
            org.telegram.messenger.MessagesStorage r3 = r18.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r12)
            org.telegram.tgnet.TLRPC$Peer r12 = r7.peer_id
            long r6 = r12.channel_id
            r11.append(r6)
            r11.append(r4)
            r7 = r20
            int r6 = r7.id
            r11.append(r6)
            java.lang.String r6 = r11.toString()
            r11 = 2
            r3.putSentFile(r8, r5, r11, r6)
        L_0x03c9:
            if (r2 == 0) goto L_0x0407
            java.lang.String r2 = r9.attachPath
            r7.attachPath = r2
            goto L_0x0407
        L_0x03d0:
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r20)
            if (r2 != 0) goto L_0x0407
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r20)
            if (r2 != 0) goto L_0x0407
            boolean r2 = r0.scheduled
            if (r2 != 0) goto L_0x0407
            org.telegram.messenger.MessagesStorage r2 = r18.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r12)
            org.telegram.tgnet.TLRPC$Peer r6 = r7.peer_id
            long r11 = r6.channel_id
            r5.append(r11)
            r5.append(r4)
            int r6 = r7.id
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 1
            r2.putSentFile(r8, r3, r6, r5)
        L_0x0407:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r3 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r3)
            if (r2 == 0) goto L_0x04c0
            org.telegram.tgnet.TLRPC$FileLocation r5 = r2.location
            if (r5 == 0) goto L_0x04c0
            long r5 = r5.volume_id
            r11 = -2147483648(0xfffffffvar_, double:NaN)
            int r14 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r14 != 0) goto L_0x04c0
            if (r3 == 0) goto L_0x04c0
            org.telegram.tgnet.TLRPC$FileLocation r5 = r3.location
            if (r5 == 0) goto L_0x04c0
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x04c0
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x04c0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r6 = r2.location
            long r11 = r6.volume_id
            r5.append(r11)
            r5.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r6 = r2.location
            int r6 = r6.local_id
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r11 = r3.location
            long r11 = r11.volume_id
            r6.append(r11)
            r6.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r4 = r3.location
            int r4 = r4.local_id
            r6.append(r4)
            java.lang.String r4 = r6.toString()
            boolean r6 = r5.equals(r4)
            if (r6 != 0) goto L_0x04e9
            java.io.File r6 = new java.io.File
            r11 = 4
            java.io.File r12 = org.telegram.messenger.FileLoader.getDirectory(r11)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r5)
            r14.append(r13)
            java.lang.String r14 = r14.toString()
            r6.<init>(r12, r14)
            java.io.File r12 = new java.io.File
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r11)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r4)
            r11.append(r13)
            java.lang.String r11 = r11.toString()
            r12.<init>(r14, r11)
            r6.renameTo(r12)
            org.telegram.messenger.ImageLoader r6 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r7.media
            org.telegram.tgnet.TLRPC$Document r11 = r11.document
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r3, (org.telegram.tgnet.TLRPC$Document) r11)
            r6.replaceImageInCache(r5, r4, r11, r1)
            org.telegram.tgnet.TLRPC$FileLocation r1 = r3.location
            r2.location = r1
            int r1 = r3.size
            r2.size = r1
            goto L_0x04e9
        L_0x04c0:
            if (r3 == 0) goto L_0x04d1
            if (r2 == 0) goto L_0x04d1
            boolean r1 = org.telegram.messenger.MessageObject.isStickerMessage(r20)
            if (r1 == 0) goto L_0x04d1
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            if (r1 == 0) goto L_0x04d1
            r3.location = r1
            goto L_0x04e9
        L_0x04d1:
            if (r2 == 0) goto L_0x04dd
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r1 != 0) goto L_0x04dd
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r1 == 0) goto L_0x04e9
        L_0x04dd:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r1.thumbs = r2
        L_0x04e9:
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
        L_0x04fe:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x0520
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r3 == 0) goto L_0x051d
            byte[] r3 = r2.waveform
            goto L_0x0521
        L_0x051d:
            int r1 = r1 + 1
            goto L_0x04fe
        L_0x0520:
            r3 = r10
        L_0x0521:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            r1.attributes = r2
            if (r3 == 0) goto L_0x0557
            r1 = 0
        L_0x0530:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x0557
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r4 == 0) goto L_0x0554
            r2.waveform = r3
            int r4 = r2.flags
            r5 = 4
            r4 = r4 | r5
            r2.flags = r4
        L_0x0554:
            int r1 = r1 + 1
            goto L_0x0530
        L_0x0557:
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
            if (r1 != 0) goto L_0x05c9
            boolean r1 = org.telegram.messenger.MessageObject.isOut(r20)
            if (r1 == 0) goto L_0x05c9
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r1)
            if (r1 == 0) goto L_0x059f
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isDocumentHasAttachedStickers(r1)
            if (r1 == 0) goto L_0x058e
            org.telegram.messenger.MessagesController r1 = r18.getMessagesController()
            boolean r6 = r1.saveGifsWithStickers
            goto L_0x058f
        L_0x058e:
            r6 = 1
        L_0x058f:
            if (r6 == 0) goto L_0x05c9
            org.telegram.messenger.MediaDataController r1 = r18.getMediaDataController()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            int r3 = r7.date
            r1.addRecentGif(r2, r3)
            goto L_0x05c9
        L_0x059f:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r1)
            if (r1 != 0) goto L_0x05b5
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            r3 = 1
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r3)
            if (r1 == 0) goto L_0x05c9
            goto L_0x05b6
        L_0x05b5:
            r3 = 1
        L_0x05b6:
            org.telegram.messenger.MediaDataController r1 = r18.getMediaDataController()
            r2 = 0
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r7.media
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            int r5 = r7.date
            r6 = 0
            r10 = 1
            r3 = r20
            r1.addRecentSticker(r2, r3, r4, r5, r6)
            goto L_0x05ca
        L_0x05c9:
            r10 = 1
        L_0x05ca:
            java.lang.String r1 = r9.attachPath
            if (r1 == 0) goto L_0x0640
            r2 = 4
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r2)
            java.lang.String r2 = r2.getAbsolutePath()
            boolean r1 = r1.startsWith(r2)
            if (r1 == 0) goto L_0x0640
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r9.attachPath
            r1.<init>(r2)
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            int r2 = r2.ttl_seconds
            if (r2 == 0) goto L_0x05ee
            r2 = 1
            goto L_0x05ef
        L_0x05ee:
            r2 = 0
        L_0x05ef:
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r2)
            boolean r3 = r1.renameTo(r2)
            if (r3 != 0) goto L_0x0612
            boolean r1 = r1.exists()
            if (r1 == 0) goto L_0x0604
            java.lang.String r1 = r9.attachPath
            r7.attachPath = r1
            goto L_0x0606
        L_0x0604:
            r0.attachPathExists = r15
        L_0x0606:
            boolean r1 = r2.exists()
            r0.mediaExists = r1
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x069e
        L_0x0612:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoMessage(r20)
            if (r1 == 0) goto L_0x061c
            r0.attachPathExists = r10
            goto L_0x069e
        L_0x061c:
            boolean r1 = r0.attachPathExists
            r0.mediaExists = r1
            r0.attachPathExists = r15
            java.lang.String r0 = ""
            r9.attachPath = r0
            if (r8 == 0) goto L_0x069e
            java.lang.String r0 = "http"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x069e
            org.telegram.messenger.MessagesStorage r0 = r18.getMessagesStorage()
            java.lang.String r1 = r2.toString()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            r0.addRecentLocalFile(r8, r1, r2)
            goto L_0x069e
        L_0x0640:
            java.lang.String r0 = r9.attachPath
            r7.attachPath = r0
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x069e
        L_0x0649:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r0 == 0) goto L_0x0656
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r0 == 0) goto L_0x0656
            r9.media = r2
            goto L_0x069e
        L_0x0656:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r0 == 0) goto L_0x065d
            r9.media = r2
            goto L_0x069e
        L_0x065d:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r0 == 0) goto L_0x0670
            org.telegram.tgnet.TLRPC$GeoPoint r0 = r2.geo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$GeoPoint r1 = r1.geo
            double r2 = r1.lat
            r0.lat = r2
            double r1 = r1._long
            r0._long = r1
            goto L_0x069e
        L_0x0670:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 != 0) goto L_0x0680
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r0 == 0) goto L_0x0679
            goto L_0x0680
        L_0x0679:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x069e
            r9.media = r2
            goto L_0x069e
        L_0x0680:
            r9.media = r2
            java.lang.String r0 = r7.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0692
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r7.entities
            r9.entities = r0
            java.lang.String r0 = r7.message
            r9.message = r0
        L_0x0692:
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r7.reply_markup
            if (r0 == 0) goto L_0x069e
            r9.reply_markup = r0
            int r0 = r9.flags
            r0 = r0 | 64
            r9.flags = r0
        L_0x069e:
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda32(this, arrayList3, arrayList4, arrayList5, arrayList, arrayList2));
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
        new Thread(new SendMessagesHelper$$ExternalSyntheticLambda31(this, arrayList, j, uri, longCallback)).start();
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
                                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda18(longCallback2));
                                return;
                            } else {
                                importingHistory.historyPath = copyFileToCache;
                            }
                            importingHistory.uploadSet.add(copyFileToCache);
                            hashMap.put(copyFileToCache, importingHistory);
                        }
                    }
                    if (i == 0) {
                        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda17(longCallback2));
                        return;
                    }
                }
            } else if (i == 0) {
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda16(longCallback2));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda35(this, hashMap, j, importingHistory, longCallback));
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
            new Thread(new SendMessagesHelper$$ExternalSyntheticLambda29(this, str, str2, str3, arrayList, stringCallback)).start();
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
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda19(stringCallback));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda40(this, importingStickers, hashMap, str2, stringCallback));
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

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:181:0x0310, code lost:
        r1 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0357, code lost:
        switch(r1) {
            case 0: goto L_0x037f;
            case 1: goto L_0x037a;
            case 2: goto L_0x0375;
            case 3: goto L_0x0370;
            case 4: goto L_0x036b;
            case 5: goto L_0x0368;
            default: goto L_0x035a;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x035a, code lost:
        r1 = r22.getMimeTypeFromExtension(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0360, code lost:
        if (r1 == null) goto L_0x0365;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x0362, code lost:
        r10.mime_type = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0365, code lost:
        r10.mime_type = "application/octet-stream";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0368, code lost:
        r10.mime_type = "image/webp";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x036b, code lost:
        r10.mime_type = "audio/opus";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0370, code lost:
        r10.mime_type = "audio/flac";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0375, code lost:
        r10.mime_type = "audio/ogg";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x037a, code lost:
        r10.mime_type = "audio/mpeg";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x037f, code lost:
        r10.mime_type = "audio/m4a";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0053, code lost:
        if (r3 == false) goto L_0x0057;
     */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0164 A[SYNTHETIC, Splitter:B:107:0x0164] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x016e  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x01c1  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0205 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x02cd  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x03e4 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x045a  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0464  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x046a  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x0472  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0486  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x048d  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x04ec A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0152 A[SYNTHETIC, Splitter:B:99:0x0152] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r39, java.lang.String r40, java.lang.String r41, android.net.Uri r42, java.lang.String r43, long r44, org.telegram.messenger.MessageObject r46, org.telegram.messenger.MessageObject r47, java.lang.CharSequence r48, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r49, org.telegram.messenger.MessageObject r50, long[] r51, boolean r52, boolean r53, boolean r54, int r55, java.lang.Integer[] r56) {
        /*
            r0 = r40
            r1 = r41
            r2 = r42
            r3 = r43
            r4 = 0
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)
            if (r0 == 0) goto L_0x0015
            int r6 = r40.length()
            if (r6 != 0) goto L_0x0018
        L_0x0015:
            if (r2 != 0) goto L_0x0018
            return r4
        L_0x0018:
            if (r2 == 0) goto L_0x0021
            boolean r6 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r42)
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
            if (r2 == 0) goto L_0x0056
            if (r0 != 0) goto L_0x0056
            if (r3 == 0) goto L_0x0043
            java.lang.String r0 = r6.getExtensionFromMimeType(r3)
            goto L_0x0044
        L_0x0043:
            r0 = 0
        L_0x0044:
            if (r0 != 0) goto L_0x004a
            java.lang.String r0 = "txt"
            r3 = 0
            goto L_0x004b
        L_0x004a:
            r3 = 1
        L_0x004b:
            java.lang.String r2 = org.telegram.messenger.MediaController.copyFileToCache(r2, r0)
            if (r2 != 0) goto L_0x0052
            return r4
        L_0x0052:
            r14 = r2
            if (r3 != 0) goto L_0x0058
            goto L_0x0057
        L_0x0056:
            r14 = r0
        L_0x0057:
            r0 = 0
        L_0x0058:
            java.io.File r2 = new java.io.File
            r2.<init>(r14)
            boolean r3 = r2.exists()
            if (r3 == 0) goto L_0x054d
            long r8 = r2.length()
            r12 = 0
            int r3 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r3 != 0) goto L_0x006f
            goto L_0x054d
        L_0x006f:
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r44)
            java.lang.String r11 = r2.getName()
            r10 = -1
            java.lang.String r9 = ""
            if (r0 == 0) goto L_0x007f
        L_0x007c:
            r16 = r0
            goto L_0x008f
        L_0x007f:
            r0 = 46
            int r0 = r14.lastIndexOf(r0)
            if (r0 == r10) goto L_0x008d
            int r0 = r0 + r15
            java.lang.String r0 = r14.substring(r0)
            goto L_0x007c
        L_0x008d:
            r16 = r9
        L_0x008f:
            java.lang.String r8 = r16.toLowerCase()
            java.lang.String r7 = "mp3"
            boolean r0 = r8.equals(r7)
            r40 = r11
            java.lang.String r11 = "flac"
            java.lang.String r4 = "opus"
            java.lang.String r12 = "m4a"
            java.lang.String r13 = "ogg"
            r15 = 2
            if (r0 != 0) goto L_0x016e
            boolean r0 = r8.equals(r12)
            if (r0 == 0) goto L_0x00ae
            goto L_0x016e
        L_0x00ae:
            boolean r0 = r8.equals(r4)
            if (r0 != 0) goto L_0x00cd
            boolean r0 = r8.equals(r13)
            if (r0 != 0) goto L_0x00cd
            boolean r0 = r8.equals(r11)
            if (r0 == 0) goto L_0x00c1
            goto L_0x00cd
        L_0x00c1:
            r19 = r11
            r20 = r12
            r0 = 0
            r11 = 0
            r12 = 0
        L_0x00c8:
            r15 = 0
        L_0x00c9:
            r22 = 0
            goto L_0x019c
        L_0x00cd:
            android.media.MediaMetadataRetriever r10 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0143, all -> 0x013f }
            r10.<init>()     // Catch:{ Exception -> 0x0143, all -> 0x013f }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x0139 }
            r10.setDataSource(r0)     // Catch:{ Exception -> 0x0139 }
            r0 = 9
            java.lang.String r0 = r10.extractMetadata(r0)     // Catch:{ Exception -> 0x0139 }
            if (r0 == 0) goto L_0x010d
            r19 = r11
            r20 = r12
            long r11 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x010b }
            float r0 = (float) r11     // Catch:{ Exception -> 0x010b }
            r11 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r11
            double r11 = (double) r0     // Catch:{ Exception -> 0x010b }
            double r11 = java.lang.Math.ceil(r11)     // Catch:{ Exception -> 0x010b }
            int r11 = (int) r11
            r0 = 7
            java.lang.String r12 = r10.extractMetadata(r0)     // Catch:{ Exception -> 0x0105 }
            java.lang.String r0 = r10.extractMetadata(r15)     // Catch:{ Exception -> 0x0100 }
            r21 = r11
            r11 = r0
            goto L_0x0115
        L_0x0100:
            r0 = move-exception
            r21 = r11
            r11 = 0
            goto L_0x014d
        L_0x0105:
            r0 = move-exception
            r21 = r11
            r11 = 0
            r12 = 0
            goto L_0x014d
        L_0x010b:
            r0 = move-exception
            goto L_0x0149
        L_0x010d:
            r19 = r11
            r20 = r12
            r11 = 0
            r12 = 0
            r21 = 0
        L_0x0115:
            if (r50 != 0) goto L_0x012c
            boolean r0 = r8.equals(r13)     // Catch:{ Exception -> 0x012a }
            if (r0 == 0) goto L_0x012c
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x012a }
            int r0 = org.telegram.messenger.MediaController.isOpusFile(r0)     // Catch:{ Exception -> 0x012a }
            r15 = 1
            if (r0 != r15) goto L_0x012c
            r15 = 1
            goto L_0x012d
        L_0x012a:
            r0 = move-exception
            goto L_0x014d
        L_0x012c:
            r15 = 0
        L_0x012d:
            r10.release()     // Catch:{ Exception -> 0x0131 }
            goto L_0x0136
        L_0x0131:
            r0 = move-exception
            r10 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x0136:
            r0 = r21
            goto L_0x00c9
        L_0x0139:
            r0 = move-exception
            r19 = r11
            r20 = r12
            goto L_0x0149
        L_0x013f:
            r0 = move-exception
            r1 = r0
            r7 = 0
            goto L_0x0162
        L_0x0143:
            r0 = move-exception
            r19 = r11
            r20 = r12
            r10 = 0
        L_0x0149:
            r11 = 0
            r12 = 0
            r21 = 0
        L_0x014d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x015f }
            if (r10 == 0) goto L_0x015b
            r10.release()     // Catch:{ Exception -> 0x0156 }
            goto L_0x015b
        L_0x0156:
            r0 = move-exception
            r10 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x015b:
            r0 = r21
            goto L_0x00c8
        L_0x015f:
            r0 = move-exception
            r1 = r0
            r7 = r10
        L_0x0162:
            if (r7 == 0) goto L_0x016d
            r7.release()     // Catch:{ Exception -> 0x0168 }
            goto L_0x016d
        L_0x0168:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x016d:
            throw r1
        L_0x016e:
            r19 = r11
            r20 = r12
            org.telegram.messenger.audioinfo.AudioInfo r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r2)
            if (r0 == 0) goto L_0x0190
            long r10 = r0.getDuration()
            r22 = 0
            int r12 = (r10 > r22 ? 1 : (r10 == r22 ? 0 : -1))
            if (r12 == 0) goto L_0x0192
            java.lang.String r12 = r0.getArtist()
            java.lang.String r0 = r0.getTitle()
            r24 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 / r24
            int r11 = (int) r10
            goto L_0x0195
        L_0x0190:
            r22 = 0
        L_0x0192:
            r0 = 0
            r11 = 0
            r12 = 0
        L_0x0195:
            r15 = 0
            r35 = r12
            r12 = r0
            r0 = r11
            r11 = r35
        L_0x019c:
            if (r0 == 0) goto L_0x01c1
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r10 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r10.<init>()
            r10.duration = r0
            r10.title = r12
            r10.performer = r11
            if (r12 != 0) goto L_0x01ad
            r10.title = r9
        L_0x01ad:
            int r0 = r10.flags
            r12 = 1
            r0 = r0 | r12
            r10.flags = r0
            if (r11 != 0) goto L_0x01b7
            r10.performer = r9
        L_0x01b7:
            r11 = 2
            r0 = r0 | r11
            r10.flags = r0
            if (r15 == 0) goto L_0x01bf
            r10.voice = r12
        L_0x01bf:
            r15 = r10
            goto L_0x01c2
        L_0x01c1:
            r15 = 0
        L_0x01c2:
            if (r1 == 0) goto L_0x0200
            java.lang.String r0 = "attheme"
            boolean r0 = r1.endsWith(r0)
            if (r0 == 0) goto L_0x01ce
            r0 = 1
            goto L_0x0201
        L_0x01ce:
            if (r15 == 0) goto L_0x01e9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            java.lang.String r1 = "audio"
            r0.append(r1)
            long r10 = r2.length()
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            goto L_0x01ff
        L_0x01e9:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r0.append(r9)
            long r10 = r2.length()
            r0.append(r10)
            java.lang.String r0 = r0.toString()
        L_0x01ff:
            r1 = r0
        L_0x0200:
            r0 = 0
        L_0x0201:
            r21 = 4
            if (r0 != 0) goto L_0x02a5
            if (r3 != 0) goto L_0x02a5
            org.telegram.messenger.MessagesStorage r0 = r39.getMessagesStorage()
            if (r3 != 0) goto L_0x020f
            r10 = 1
            goto L_0x0210
        L_0x020f:
            r10 = 4
        L_0x0210:
            java.lang.Object[] r0 = r0.getSentFile(r1, r10)
            if (r0 == 0) goto L_0x0228
            r10 = 0
            r11 = r0[r10]
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r11 == 0) goto L_0x0228
            r11 = r0[r10]
            r10 = r11
            org.telegram.tgnet.TLRPC$TL_document r10 = (org.telegram.tgnet.TLRPC$TL_document) r10
            r11 = 1
            r0 = r0[r11]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x022a
        L_0x0228:
            r0 = 0
            r10 = 0
        L_0x022a:
            if (r10 != 0) goto L_0x026e
            boolean r11 = r14.equals(r1)
            if (r11 != 0) goto L_0x026e
            if (r3 != 0) goto L_0x026e
            org.telegram.messenger.MessagesStorage r11 = r39.getMessagesStorage()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r14)
            r43 = r8
            r42 = r9
            long r8 = r2.length()
            r12.append(r8)
            java.lang.String r8 = r12.toString()
            if (r3 != 0) goto L_0x0253
            r9 = 1
            goto L_0x0254
        L_0x0253:
            r9 = 4
        L_0x0254:
            java.lang.Object[] r8 = r11.getSentFile(r8, r9)
            if (r8 == 0) goto L_0x0272
            r9 = 0
            r11 = r8[r9]
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r11 == 0) goto L_0x0272
            r0 = r8[r9]
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            r9 = 1
            r8 = r8[r9]
            java.lang.String r8 = (java.lang.String) r8
            r24 = r0
            r0 = r8
            goto L_0x0274
        L_0x026e:
            r43 = r8
            r42 = r9
        L_0x0272:
            r24 = r10
        L_0x0274:
            r11 = 0
            r25 = 0
            r12 = r43
            r8 = r3
            r10 = r42
            r9 = r24
            r27 = r10
            r18 = -1
            r10 = r14
            r28 = r5
            r5 = r40
            r35 = r19
            r19 = r1
            r1 = r35
            r40 = r3
            r42 = r7
            r3 = r13
            r35 = r22
            r22 = r6
            r6 = r35
            r37 = r20
            r20 = r14
            r14 = r37
            r12 = r25
            ensureMediaThumbExists(r8, r9, r10, r11, r12)
            r8 = r0
            goto L_0x02c9
        L_0x02a5:
            r28 = r5
            r42 = r7
            r43 = r8
            r27 = r9
            r18 = -1
            r5 = r40
            r40 = r3
            r3 = r13
            r35 = r19
            r19 = r1
            r1 = r35
            r36 = r22
            r22 = r6
            r6 = r36
            r38 = r20
            r20 = r14
            r14 = r38
            r8 = 0
            r24 = 0
        L_0x02c9:
            java.lang.String r9 = "image/webp"
            if (r24 != 0) goto L_0x045a
            org.telegram.tgnet.TLRPC$TL_document r10 = new org.telegram.tgnet.TLRPC$TL_document
            r10.<init>()
            r10.id = r6
            org.telegram.tgnet.ConnectionsManager r0 = r39.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r10.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            r0.file_name = r5
            r5 = 0
            byte[] r11 = new byte[r5]
            r10.file_reference = r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r11 = r10.attributes
            r11.add(r0)
            long r11 = r2.length()
            int r12 = (int) r11
            r10.size = r12
            r10.dc_id = r5
            if (r15 == 0) goto L_0x02ff
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r10.attributes
            r5.add(r15)
        L_0x02ff:
            int r5 = r16.length()
            java.lang.String r11 = "application/octet-stream"
            if (r5 == 0) goto L_0x0384
            int r5 = r43.hashCode()
            switch(r5) {
                case 106458: goto L_0x034d;
                case 108272: goto L_0x0340;
                case 109967: goto L_0x0335;
                case 3145576: goto L_0x032a;
                case 3418175: goto L_0x031f;
                case 3645340: goto L_0x0312;
                default: goto L_0x030e;
            }
        L_0x030e:
            r5 = r43
        L_0x0310:
            r1 = -1
            goto L_0x0357
        L_0x0312:
            java.lang.String r1 = "webp"
            r5 = r43
            boolean r1 = r5.equals(r1)
            if (r1 != 0) goto L_0x031d
            goto L_0x0355
        L_0x031d:
            r1 = 5
            goto L_0x0357
        L_0x031f:
            r5 = r43
            boolean r1 = r5.equals(r4)
            if (r1 != 0) goto L_0x0328
            goto L_0x0355
        L_0x0328:
            r1 = 4
            goto L_0x0357
        L_0x032a:
            r5 = r43
            boolean r1 = r5.equals(r1)
            if (r1 != 0) goto L_0x0333
            goto L_0x0355
        L_0x0333:
            r1 = 3
            goto L_0x0357
        L_0x0335:
            r5 = r43
            boolean r1 = r5.equals(r3)
            if (r1 != 0) goto L_0x033e
            goto L_0x0355
        L_0x033e:
            r1 = 2
            goto L_0x0357
        L_0x0340:
            r1 = r42
            r5 = r43
            boolean r1 = r5.equals(r1)
            if (r1 != 0) goto L_0x034b
            goto L_0x0355
        L_0x034b:
            r1 = 1
            goto L_0x0357
        L_0x034d:
            r5 = r43
            boolean r1 = r5.equals(r14)
            if (r1 != 0) goto L_0x0356
        L_0x0355:
            goto L_0x0310
        L_0x0356:
            r1 = 0
        L_0x0357:
            switch(r1) {
                case 0: goto L_0x037f;
                case 1: goto L_0x037a;
                case 2: goto L_0x0375;
                case 3: goto L_0x0370;
                case 4: goto L_0x036b;
                case 5: goto L_0x0368;
                default: goto L_0x035a;
            }
        L_0x035a:
            r1 = r22
            java.lang.String r1 = r1.getMimeTypeFromExtension(r5)
            if (r1 == 0) goto L_0x0365
            r10.mime_type = r1
            goto L_0x0386
        L_0x0365:
            r10.mime_type = r11
            goto L_0x0386
        L_0x0368:
            r10.mime_type = r9
            goto L_0x0386
        L_0x036b:
            java.lang.String r1 = "audio/opus"
            r10.mime_type = r1
            goto L_0x0386
        L_0x0370:
            java.lang.String r1 = "audio/flac"
            r10.mime_type = r1
            goto L_0x0386
        L_0x0375:
            java.lang.String r1 = "audio/ogg"
            r10.mime_type = r1
            goto L_0x0386
        L_0x037a:
            java.lang.String r1 = "audio/mpeg"
            r10.mime_type = r1
            goto L_0x0386
        L_0x037f:
            java.lang.String r1 = "audio/m4a"
            r10.mime_type = r1
            goto L_0x0386
        L_0x0384:
            r10.mime_type = r11
        L_0x0386:
            if (r53 != 0) goto L_0x03da
            java.lang.String r1 = r10.mime_type
            java.lang.String r3 = "image/gif"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x03da
            if (r50 == 0) goto L_0x039c
            long r3 = r50.getGroupIdForUse()
            int r1 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x03da
        L_0x039c:
            java.lang.String r1 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x03d3 }
            r2 = 1119092736(0x42b40000, float:90.0)
            r3 = 0
            r4 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r3, r2, r2, r4)     // Catch:{ Exception -> 0x03d3 }
            if (r1 == 0) goto L_0x03da
            java.lang.String r3 = "animation.gif"
            r0.file_name = r3     // Catch:{ Exception -> 0x03d3 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r10.attributes     // Catch:{ Exception -> 0x03d3 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x03d3 }
            r3.<init>()     // Catch:{ Exception -> 0x03d3 }
            r0.add(r3)     // Catch:{ Exception -> 0x03d3 }
            r0 = 55
            r3 = r40
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r2, r2, r0, r3)     // Catch:{ Exception -> 0x03d1 }
            if (r0 == 0) goto L_0x03cd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r10.thumbs     // Catch:{ Exception -> 0x03d1 }
            r2.add(r0)     // Catch:{ Exception -> 0x03d1 }
            int r0 = r10.flags     // Catch:{ Exception -> 0x03d1 }
            r2 = 1
            r0 = r0 | r2
            r10.flags = r0     // Catch:{ Exception -> 0x03d1 }
        L_0x03cd:
            r1.recycle()     // Catch:{ Exception -> 0x03d1 }
            goto L_0x03dc
        L_0x03d1:
            r0 = move-exception
            goto L_0x03d6
        L_0x03d3:
            r0 = move-exception
            r3 = r40
        L_0x03d6:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x03dc
        L_0x03da:
            r3 = r40
        L_0x03dc:
            java.lang.String r0 = r10.mime_type
            boolean r0 = r0.equals(r9)
            if (r0 == 0) goto L_0x0455
            if (r50 != 0) goto L_0x0455
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options
            r1.<init>()
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ Exception -> 0x0419 }
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0419 }
            java.lang.String r2 = "r"
            r4 = r20
            r0.<init>(r4, r2)     // Catch:{ Exception -> 0x0417 }
            java.nio.channels.FileChannel r29 = r0.getChannel()     // Catch:{ Exception -> 0x0417 }
            java.nio.channels.FileChannel$MapMode r30 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Exception -> 0x0417 }
            r31 = 0
            int r2 = r4.length()     // Catch:{ Exception -> 0x0417 }
            long r5 = (long) r2     // Catch:{ Exception -> 0x0417 }
            r33 = r5
            java.nio.MappedByteBuffer r2 = r29.map(r30, r31, r33)     // Catch:{ Exception -> 0x0417 }
            int r5 = r2.limit()     // Catch:{ Exception -> 0x0417 }
            r6 = 0
            r7 = 1
            org.telegram.messenger.Utilities.loadWebpImage(r6, r2, r5, r1, r7)     // Catch:{ Exception -> 0x0417 }
            r0.close()     // Catch:{ Exception -> 0x0417 }
            goto L_0x041f
        L_0x0417:
            r0 = move-exception
            goto L_0x041c
        L_0x0419:
            r0 = move-exception
            r4 = r20
        L_0x041c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x041f:
            int r0 = r1.outWidth
            if (r0 == 0) goto L_0x0457
            int r2 = r1.outHeight
            if (r2 == 0) goto L_0x0457
            r5 = 800(0x320, float:1.121E-42)
            if (r0 > r5) goto L_0x0457
            if (r2 > r5) goto L_0x0457
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            r2 = r27
            r0.alt = r2
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r5 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r5.<init>()
            r0.stickerset = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r10.attributes
            r5.add(r0)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r0.<init>()
            int r5 = r1.outWidth
            r0.w = r5
            int r1 = r1.outHeight
            r0.h = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r10.attributes
            r1.add(r0)
            goto L_0x0462
        L_0x0455:
            r4 = r20
        L_0x0457:
            r2 = r27
            goto L_0x0462
        L_0x045a:
            r3 = r40
            r4 = r20
            r2 = r27
            r10 = r24
        L_0x0462:
            if (r48 == 0) goto L_0x046a
            java.lang.String r0 = r48.toString()
            r11 = r0
            goto L_0x046b
        L_0x046a:
            r11 = r2
        L_0x046b:
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            if (r19 == 0) goto L_0x0479
            java.lang.String r0 = "originalPath"
            r1 = r19
            r5.put(r0, r1)
        L_0x0479:
            java.lang.String r0 = "1"
            if (r53 == 0) goto L_0x0484
            if (r15 != 0) goto L_0x0484
            java.lang.String r1 = "forceDocument"
            r5.put(r1, r0)
        L_0x0484:
            if (r8 == 0) goto L_0x048b
            java.lang.String r1 = "parentObject"
            r5.put(r1, r8)
        L_0x048b:
            if (r56 == 0) goto L_0x04e5
            r1 = 0
            r6 = r56[r1]
            java.lang.String r7 = r10.mime_type
            if (r7 == 0) goto L_0x04a8
            java.lang.String r7 = r7.toLowerCase()
            boolean r7 = r7.startsWith(r9)
            if (r7 == 0) goto L_0x04a8
            java.lang.Integer r7 = java.lang.Integer.valueOf(r18)
            r56[r1] = r7
            r1 = 1
            r17 = 0
            goto L_0x04ea
        L_0x04a8:
            java.lang.String r1 = r10.mime_type
            if (r1 == 0) goto L_0x04c6
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r7 = "image/"
            boolean r1 = r1.startsWith(r7)
            if (r1 != 0) goto L_0x04cc
            java.lang.String r1 = r10.mime_type
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r7 = "video/mp4"
            boolean r1 = r1.startsWith(r7)
            if (r1 != 0) goto L_0x04cc
        L_0x04c6:
            boolean r1 = org.telegram.messenger.MessageObject.canPreviewDocument(r10)
            if (r1 == 0) goto L_0x04d6
        L_0x04cc:
            r1 = 1
            java.lang.Integer r7 = java.lang.Integer.valueOf(r1)
            r17 = 0
            r56[r17] = r7
            goto L_0x04e9
        L_0x04d6:
            r17 = 0
            if (r15 == 0) goto L_0x04e2
            r1 = 2
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r56[r17] = r1
            goto L_0x04e9
        L_0x04e2:
            r56[r17] = r28
            goto L_0x04e9
        L_0x04e5:
            r17 = 0
            r6 = r28
        L_0x04e9:
            r1 = 0
        L_0x04ea:
            if (r3 != 0) goto L_0x052c
            if (r51 == 0) goto L_0x052c
            if (r56 == 0) goto L_0x0508
            if (r6 == 0) goto L_0x0508
            r3 = r56[r17]
            if (r6 == r3) goto L_0x0508
            r6 = r51[r17]
            r3 = r39
            r14 = r55
            finishGroup(r3, r6, r14)
            java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random
            long r6 = r6.nextLong()
            r51[r17] = r6
            goto L_0x050c
        L_0x0508:
            r3 = r39
            r14 = r55
        L_0x050c:
            if (r1 != 0) goto L_0x0530
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r2)
            r6 = r51[r17]
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "groupId"
            r5.put(r2, r1)
            if (r52 == 0) goto L_0x0530
            java.lang.String r1 = "final"
            r5.put(r1, r0)
            goto L_0x0530
        L_0x052c:
            r3 = r39
            r14 = r55
        L_0x0530:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda12 r15 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda12
            r0 = r15
            r1 = r50
            r2 = r39
            r3 = r10
            r6 = r8
            r7 = r44
            r9 = r46
            r10 = r47
            r12 = r49
            r13 = r54
            r14 = r55
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r15)
            r1 = 1
            return r1
        L_0x054d:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance, java.lang.String, java.lang.String, android.net.Uri, java.lang.String, long, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.lang.CharSequence, java.util.ArrayList, org.telegram.messenger.MessageObject, long[], boolean, boolean, boolean, int, java.lang.Integer[]):boolean");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingDocumentInternal$73(MessageObject messageObject, AccountInstance accountInstance, TLRPC$TL_document tLRPC$TL_document, String str, HashMap hashMap, String str2, long j, MessageObject messageObject2, MessageObject messageObject3, String str3, ArrayList arrayList, boolean z, int i) {
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, (VideoEditedInfo) null, tLRPC$TL_document, str, hashMap, false, str2);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, (VideoEditedInfo) null, str, j, messageObject2, messageObject3, str3, arrayList, (TLRPC$ReplyMarkup) null, hashMap, z, i, 0, str2, (MessageObject.SendAnimationData) null);
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
        new Thread(new SendMessagesHelper$$ExternalSyntheticLambda9(arrayList, j, accountInstance, str, messageObject3, messageObject, messageObject2, z, i)).start();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v3, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v5, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v8, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008c  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a1  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00df A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingAudioDocuments$75(java.util.ArrayList r22, long r23, org.telegram.messenger.AccountInstance r25, java.lang.String r26, org.telegram.messenger.MessageObject r27, org.telegram.messenger.MessageObject r28, org.telegram.messenger.MessageObject r29, boolean r30, int r31) {
        /*
            int r0 = r22.size()
            r1 = 0
            r2 = 0
            r4 = 0
            r5 = 0
        L_0x0009:
            if (r4 >= r0) goto L_0x00fa
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
            if (r9 != 0) goto L_0x007a
            org.telegram.messenger.MessagesStorage r11 = r25.getMessagesStorage()
            if (r9 != 0) goto L_0x0056
            r13 = 1
            goto L_0x0057
        L_0x0056:
            r13 = 4
        L_0x0057:
            java.lang.Object[] r11 = r11.getSentFile(r7, r13)
            if (r11 == 0) goto L_0x007a
            r13 = r11[r1]
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r13 == 0) goto L_0x007a
            r13 = r11[r1]
            r19 = r13
            org.telegram.tgnet.TLRPC$TL_document r19 = (org.telegram.tgnet.TLRPC$TL_document) r19
            r11 = r11[r10]
            java.lang.String r11 = (java.lang.String) r11
            r16 = 0
            r17 = 0
            r13 = r9
            r14 = r19
            r15 = r7
            ensureMediaThumbExists(r13, r14, r15, r16, r17)
            r14 = r11
            goto L_0x007d
        L_0x007a:
            r14 = r8
            r19 = r14
        L_0x007d:
            if (r19 != 0) goto L_0x0088
            org.telegram.tgnet.TLRPC$Message r11 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            org.telegram.tgnet.TLRPC$Document r11 = r11.document
            org.telegram.tgnet.TLRPC$TL_document r11 = (org.telegram.tgnet.TLRPC$TL_document) r11
            goto L_0x008a
        L_0x0088:
            r11 = r19
        L_0x008a:
            if (r9 == 0) goto L_0x009f
            int r9 = org.telegram.messenger.DialogObject.getEncryptedChatId(r23)
            org.telegram.messenger.MessagesController r13 = r25.getMessagesController()
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$EncryptedChat r9 = r13.getEncryptedChat(r9)
            if (r9 != 0) goto L_0x009f
            return
        L_0x009f:
            if (r4 != 0) goto L_0x00a4
            r19 = r26
            goto L_0x00a6
        L_0x00a4:
            r19 = r8
        L_0x00a6:
            java.util.HashMap r13 = new java.util.HashMap
            r13.<init>()
            if (r7 == 0) goto L_0x00b2
            java.lang.String r8 = "originalPath"
            r13.put(r8, r7)
        L_0x00b2:
            if (r14 == 0) goto L_0x00b9
            java.lang.String r7 = "parentObject"
            r13.put(r7, r14)
        L_0x00b9:
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
            if (r5 == r7) goto L_0x00d8
            int r7 = r0 + -1
            if (r4 != r7) goto L_0x00df
        L_0x00d8:
            java.lang.String r7 = "final"
            java.lang.String r8 = "1"
            r13.put(r7, r8)
        L_0x00df:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda14 r7 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda14
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
        L_0x00fa:
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
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda11(accountInstance, j, i));
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
                SendMessagesHelper$$ExternalSyntheticLambda2 sendMessagesHelper$$ExternalSyntheticLambda2 = r0;
                SendMessagesHelper$$ExternalSyntheticLambda2 sendMessagesHelper$$ExternalSyntheticLambda22 = new SendMessagesHelper$$ExternalSyntheticLambda2(j, arrayList, str, accountInstance, i, arrayList2, str2, messageObject, messageObject2, messageObject3, inputContentInfoCompat, z, arrayList3);
                new Thread(sendMessagesHelper$$ExternalSyntheticLambda2).start();
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingDocuments$78(long j, ArrayList arrayList, String str, AccountInstance accountInstance, int i, ArrayList arrayList2, String str2, MessageObject messageObject, MessageObject messageObject2, MessageObject messageObject3, InputContentInfoCompat inputContentInfoCompat, boolean z, ArrayList arrayList3) {
        long[] jArr;
        Integer[] numArr;
        boolean z2;
        ArrayList arrayList4;
        ArrayList arrayList5 = arrayList;
        AccountInstance accountInstance2 = accountInstance;
        int i2 = i;
        ArrayList arrayList6 = arrayList3;
        int i3 = 1;
        long[] jArr2 = new long[1];
        Integer[] numArr2 = new Integer[1];
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(j);
        int i4 = 10;
        if (arrayList5 != null) {
            int size = arrayList.size();
            int i5 = 0;
            int i6 = 0;
            z2 = false;
            while (i6 < size) {
                String str3 = i6 == 0 ? str : null;
                if (!isEncryptedDialog && size > i3 && i5 % 10 == 0) {
                    if (jArr2[0] != 0) {
                        finishGroup(accountInstance2, jArr2[0], i2);
                    }
                    jArr2[0] = Utilities.random.nextLong();
                    i5 = 0;
                }
                int i7 = i5 + 1;
                long j2 = jArr2[0];
                int i8 = i6;
                int i9 = i7;
                int i10 = size;
                Integer[] numArr3 = numArr2;
                long[] jArr3 = jArr2;
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList5.get(i6), (String) arrayList2.get(i6), (Uri) null, str2, j, messageObject, messageObject2, str3, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, jArr3, i7 == i4 || i6 == size + -1, inputContentInfoCompat == null, z, i, numArr3)) {
                    z2 = true;
                }
                i5 = (j2 != jArr3[0] || jArr3[0] == -1) ? 1 : i9;
                i6 = i8 + 1;
                accountInstance2 = accountInstance;
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
                String str4 = (i12 == 0 && (arrayList5 == null || arrayList.size() == 0)) ? str : null;
                if (isEncryptedDialog) {
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
                long j3 = jArr[0];
                int i17 = i16;
                int i18 = i12;
                int i19 = size2;
                if (!prepareSendingDocumentInternal(accountInstance, (String) null, (String) null, (Uri) arrayList4.get(i12), str2, j, messageObject, messageObject2, str4, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, jArr, i16 == 10 || i12 == size2 + -1, inputContentInfoCompat == null, z, i, numArr)) {
                    z2 = true;
                }
                i11 = (j3 != jArr[0] || jArr[0] == -1) ? 1 : i17;
                i12 = i18 + 1;
                arrayList4 = arrayList3;
                size2 = i19;
            }
        }
        if (inputContentInfoCompat != null) {
            inputContentInfoCompat.releasePermission();
        }
        if (z2) {
            AndroidUtilities.runOnUIThread(SendMessagesHelper$$ExternalSyntheticLambda75.INSTANCE);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingDocuments$77() {
        try {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.getString("UnsupportedAttachment", NUM));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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
                new Thread(new SendMessagesHelper$$ExternalSyntheticLambda3(j, tLRPC$BotInlineResult, accountInstance, hashMap, messageObject, messageObject2, z, i)).run();
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v4, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v19, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX WARNING: type inference failed for: r15v20 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingBotContextResult$80(long r21, org.telegram.tgnet.TLRPC$BotInlineResult r23, org.telegram.messenger.AccountInstance r24, java.util.HashMap r25, org.telegram.messenger.MessageObject r26, org.telegram.messenger.MessageObject r27, boolean r28, int r29) {
        /*
            r11 = r23
            r12 = r25
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r21)
            java.lang.String r0 = r11.type
            java.lang.String r2 = "game"
            boolean r0 = r2.equals(r0)
            r3 = 0
            r7 = 0
            r8 = 1
            if (r0 == 0) goto L_0x004c
            if (r1 == 0) goto L_0x0018
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
            r4 = r4 | r8
            r0.flags = r4
        L_0x0043:
            r16 = r0
            r19 = r1
            r0 = r3
            r9 = r0
            r15 = r9
            goto L_0x047e
        L_0x004c:
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMediaResult
            if (r0 == 0) goto L_0x0072
            org.telegram.tgnet.TLRPC$Document r0 = r11.document
            if (r0 == 0) goto L_0x005f
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r4 == 0) goto L_0x0477
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            r19 = r1
            r9 = r3
            goto L_0x047b
        L_0x005f:
            org.telegram.tgnet.TLRPC$Photo r0 = r11.photo
            if (r0 == 0) goto L_0x0477
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r4 == 0) goto L_0x0477
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            r15 = r0
            r19 = r1
            r0 = r3
            r9 = r0
            r16 = r9
            goto L_0x047e
        L_0x0072:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            if (r0 == 0) goto L_0x0477
            java.lang.String r0 = r0.url
            java.lang.String r0 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r0, r3)
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            java.lang.String r5 = "."
            if (r4 == 0) goto L_0x008d
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            java.lang.String r0 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r0)
            goto L_0x009c
        L_0x008d:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            r4.append(r0)
            java.lang.String r0 = r4.toString()
        L_0x009c:
            java.io.File r4 = new java.io.File
            r6 = 4
            java.io.File r9 = org.telegram.messenger.FileLoader.getDirectory(r6)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            org.telegram.tgnet.TLRPC$WebDocument r13 = r11.content
            java.lang.String r13 = r13.url
            java.lang.String r13 = org.telegram.messenger.Utilities.MD5(r13)
            r10.append(r13)
            r10.append(r0)
            java.lang.String r0 = r10.toString()
            r4.<init>(r9, r0)
            boolean r0 = r4.exists()
            if (r0 == 0) goto L_0x00c8
            java.lang.String r0 = r4.getAbsolutePath()
            goto L_0x00cc
        L_0x00c8:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.url
        L_0x00cc:
            r9 = r0
            java.lang.String r0 = r11.type
            r0.hashCode()
            int r10 = r0.hashCode()
            java.lang.String r13 = "voice"
            java.lang.String r15 = "video"
            r16 = 3
            java.lang.String r14 = "audio"
            java.lang.String r2 = "gif"
            java.lang.String r6 = "sticker"
            r18 = 2
            java.lang.String r8 = "file"
            switch(r10) {
                case -1890252483: goto L_0x0123;
                case 102340: goto L_0x011a;
                case 3143036: goto L_0x0111;
                case 93166550: goto L_0x0108;
                case 106642994: goto L_0x00fd;
                case 112202875: goto L_0x00f4;
                case 112386354: goto L_0x00eb;
                default: goto L_0x00e9;
            }
        L_0x00e9:
            r0 = -1
            goto L_0x012b
        L_0x00eb:
            boolean r0 = r0.equals(r13)
            if (r0 != 0) goto L_0x00f2
            goto L_0x00e9
        L_0x00f2:
            r0 = 6
            goto L_0x012b
        L_0x00f4:
            boolean r0 = r0.equals(r15)
            if (r0 != 0) goto L_0x00fb
            goto L_0x00e9
        L_0x00fb:
            r0 = 5
            goto L_0x012b
        L_0x00fd:
            java.lang.String r10 = "photo"
            boolean r0 = r0.equals(r10)
            if (r0 != 0) goto L_0x0106
            goto L_0x00e9
        L_0x0106:
            r0 = 4
            goto L_0x012b
        L_0x0108:
            boolean r0 = r0.equals(r14)
            if (r0 != 0) goto L_0x010f
            goto L_0x00e9
        L_0x010f:
            r0 = 3
            goto L_0x012b
        L_0x0111:
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L_0x0118
            goto L_0x00e9
        L_0x0118:
            r0 = 2
            goto L_0x012b
        L_0x011a:
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x0121
            goto L_0x00e9
        L_0x0121:
            r0 = 1
            goto L_0x012b
        L_0x0123:
            boolean r0 = r0.equals(r6)
            if (r0 != 0) goto L_0x012a
            goto L_0x00e9
        L_0x012a:
            r0 = 0
        L_0x012b:
            java.lang.String r10 = "x"
            switch(r0) {
                case 0: goto L_0x0185;
                case 1: goto L_0x0185;
                case 2: goto L_0x0185;
                case 3: goto L_0x0185;
                case 4: goto L_0x0136;
                case 5: goto L_0x0185;
                case 6: goto L_0x0185;
                default: goto L_0x0130;
            }
        L_0x0130:
            r19 = r1
            r0 = r3
            r15 = r0
            goto L_0x047c
        L_0x0136:
            boolean r0 = r4.exists()
            if (r0 == 0) goto L_0x0145
            org.telegram.messenger.SendMessagesHelper r0 = r24.getSendMessagesHelper()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r9, r3)
            goto L_0x0146
        L_0x0145:
            r0 = r3
        L_0x0146:
            if (r0 != 0) goto L_0x017d
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r2 = r24.getConnectionsManager()
            int r2 = r2.getCurrentTime()
            r0.date = r2
            byte[] r2 = new byte[r7]
            r0.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize r2 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r2.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r5 = r4[r7]
            r2.w = r5
            r5 = 1
            r4 = r4[r5]
            r2.h = r4
            r2.size = r5
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r4 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r4.<init>()
            r2.location = r4
            r2.type = r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r0.sizes
            r4.add(r2)
        L_0x017d:
            r15 = r0
            r19 = r1
            r0 = r3
            r16 = r0
            goto L_0x047e
        L_0x0185:
            org.telegram.tgnet.TLRPC$TL_document r4 = new org.telegram.tgnet.TLRPC$TL_document
            r4.<init>()
            r19 = r1
            r0 = 0
            r4.id = r0
            r4.size = r7
            r4.dc_id = r7
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r4.mime_type = r0
            byte[] r0 = new byte[r7]
            r4.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r24.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r4.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r1.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r4.attributes
            r0.add(r1)
            java.lang.String r0 = r11.type
            r0.hashCode()
            int r20 = r0.hashCode()
            switch(r20) {
                case -1890252483: goto L_0x01ed;
                case 102340: goto L_0x01e4;
                case 3143036: goto L_0x01db;
                case 93166550: goto L_0x01d2;
                case 112202875: goto L_0x01c9;
                case 112386354: goto L_0x01c0;
                default: goto L_0x01be;
            }
        L_0x01be:
            r14 = -1
            goto L_0x01f5
        L_0x01c0:
            boolean r0 = r0.equals(r13)
            if (r0 != 0) goto L_0x01c7
            goto L_0x01be
        L_0x01c7:
            r14 = 5
            goto L_0x01f5
        L_0x01c9:
            boolean r0 = r0.equals(r15)
            if (r0 != 0) goto L_0x01d0
            goto L_0x01be
        L_0x01d0:
            r14 = 4
            goto L_0x01f5
        L_0x01d2:
            boolean r0 = r0.equals(r14)
            if (r0 != 0) goto L_0x01d9
            goto L_0x01be
        L_0x01d9:
            r14 = 3
            goto L_0x01f5
        L_0x01db:
            boolean r0 = r0.equals(r8)
            if (r0 != 0) goto L_0x01e2
            goto L_0x01be
        L_0x01e2:
            r14 = 2
            goto L_0x01f5
        L_0x01e4:
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x01eb
            goto L_0x01be
        L_0x01eb:
            r14 = 1
            goto L_0x01f5
        L_0x01ed:
            boolean r0 = r0.equals(r6)
            if (r0 != 0) goto L_0x01f4
            goto L_0x01be
        L_0x01f4:
            r14 = 0
        L_0x01f5:
            r0 = 55
            r2 = 1119092736(0x42b40000, float:90.0)
            switch(r14) {
                case 0: goto L_0x03ad;
                case 1: goto L_0x02ed;
                case 2: goto L_0x02bd;
                case 3: goto L_0x0293;
                case 4: goto L_0x0217;
                case 5: goto L_0x01fe;
                default: goto L_0x01fc;
            }
        L_0x01fc:
            goto L_0x0435
        L_0x01fe:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r2 = org.telegram.messenger.MessageObject.getInlineResultDuration(r23)
            r0.duration = r2
            r2 = 1
            r0.voice = r2
            java.lang.String r2 = "audio.ogg"
            r1.file_name = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r4.attributes
            r2.add(r0)
            goto L_0x0435
        L_0x0217:
            java.lang.String r6 = "video.mp4"
            r1.file_name = r6
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r6.<init>()
            int[] r13 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r14 = r13[r7]
            r6.w = r14
            r14 = 1
            r13 = r13[r14]
            r6.h = r13
            int r13 = org.telegram.messenger.MessageObject.getInlineResultDuration(r23)
            r6.duration = r13
            r6.supports_streaming = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r4.attributes
            r13.add(r6)
            org.telegram.tgnet.TLRPC$WebDocument r6 = r11.thumb     // Catch:{ all -> 0x028d }
            if (r6 == 0) goto L_0x0435
            java.io.File r6 = new java.io.File     // Catch:{ all -> 0x028d }
            r13 = 4
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r13)     // Catch:{ all -> 0x028d }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x028d }
            r14.<init>()     // Catch:{ all -> 0x028d }
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.thumb     // Catch:{ all -> 0x028d }
            java.lang.String r15 = r15.url     // Catch:{ all -> 0x028d }
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)     // Catch:{ all -> 0x028d }
            r14.append(r15)     // Catch:{ all -> 0x028d }
            r14.append(r5)     // Catch:{ all -> 0x028d }
            org.telegram.tgnet.TLRPC$WebDocument r5 = r11.thumb     // Catch:{ all -> 0x028d }
            java.lang.String r5 = r5.url     // Catch:{ all -> 0x028d }
            java.lang.String r15 = "jpg"
            java.lang.String r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r15)     // Catch:{ all -> 0x028d }
            r14.append(r5)     // Catch:{ all -> 0x028d }
            java.lang.String r5 = r14.toString()     // Catch:{ all -> 0x028d }
            r6.<init>(r13, r5)     // Catch:{ all -> 0x028d }
            java.lang.String r5 = r6.getAbsolutePath()     // Catch:{ all -> 0x028d }
            r6 = 1
            android.graphics.Bitmap r5 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r3, r2, r2, r6)     // Catch:{ all -> 0x028d }
            if (r5 == 0) goto L_0x0435
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r5, r2, r2, r0, r7)     // Catch:{ all -> 0x028d }
            if (r0 == 0) goto L_0x0288
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r4.thumbs     // Catch:{ all -> 0x028d }
            r2.add(r0)     // Catch:{ all -> 0x028d }
            int r0 = r4.flags     // Catch:{ all -> 0x028d }
            r2 = 1
            r0 = r0 | r2
            r4.flags = r0     // Catch:{ all -> 0x028d }
        L_0x0288:
            r5.recycle()     // Catch:{ all -> 0x028d }
            goto L_0x0435
        L_0x028d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0435
        L_0x0293:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r2 = org.telegram.messenger.MessageObject.getInlineResultDuration(r23)
            r0.duration = r2
            java.lang.String r2 = r11.title
            r0.title = r2
            int r2 = r0.flags
            r5 = 1
            r2 = r2 | r5
            r0.flags = r2
            java.lang.String r5 = r11.description
            if (r5 == 0) goto L_0x02b2
            r0.performer = r5
            r2 = r2 | 2
            r0.flags = r2
        L_0x02b2:
            java.lang.String r2 = "audio.mp3"
            r1.file_name = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r4.attributes
            r2.add(r0)
            goto L_0x0435
        L_0x02bd:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r2 = 47
            int r0 = r0.lastIndexOf(r2)
            r2 = -1
            if (r0 == r2) goto L_0x02e9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "file."
            r2.append(r5)
            org.telegram.tgnet.TLRPC$WebDocument r5 = r11.content
            java.lang.String r5 = r5.mime_type
            r6 = 1
            int r0 = r0 + r6
            java.lang.String r0 = r5.substring(r0)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1.file_name = r0
            goto L_0x0435
        L_0x02e9:
            r1.file_name = r8
            goto L_0x0435
        L_0x02ed:
            java.lang.String r2 = "animation.gif"
            r1.file_name = r2
            java.lang.String r2 = "mp4"
            boolean r6 = r9.endsWith(r2)
            java.lang.String r13 = "video/mp4"
            if (r6 == 0) goto L_0x0308
            r4.mime_type = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r14 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r14.<init>()
            r6.add(r14)
            goto L_0x030c
        L_0x0308:
            java.lang.String r6 = "image/gif"
            r4.mime_type = r6
        L_0x030c:
            r6 = 90
            if (r19 == 0) goto L_0x0313
            r14 = 90
            goto L_0x0315
        L_0x0313:
            r14 = 320(0x140, float:4.48E-43)
        L_0x0315:
            boolean r2 = r9.endsWith(r2)     // Catch:{ all -> 0x03a7 }
            if (r2 == 0) goto L_0x0381
            r2 = 1
            android.graphics.Bitmap r15 = createVideoThumbnail(r9, r2)     // Catch:{ all -> 0x03a7 }
            if (r15 != 0) goto L_0x0387
            org.telegram.tgnet.TLRPC$WebDocument r2 = r11.thumb     // Catch:{ all -> 0x03a7 }
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_webDocument     // Catch:{ all -> 0x03a7 }
            if (r0 == 0) goto L_0x0387
            java.lang.String r0 = r2.mime_type     // Catch:{ all -> 0x03a7 }
            boolean r0 = r13.equals(r0)     // Catch:{ all -> 0x03a7 }
            if (r0 == 0) goto L_0x0387
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.thumb     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = r0.url     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r0, r3)     // Catch:{ all -> 0x03a7 }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x03a7 }
            if (r2 == 0) goto L_0x0347
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.thumb     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = r0.mime_type     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r0)     // Catch:{ all -> 0x03a7 }
            goto L_0x0356
        L_0x0347:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a7 }
            r2.<init>()     // Catch:{ all -> 0x03a7 }
            r2.append(r5)     // Catch:{ all -> 0x03a7 }
            r2.append(r0)     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = r2.toString()     // Catch:{ all -> 0x03a7 }
        L_0x0356:
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x03a7 }
            r5 = 4
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r5)     // Catch:{ all -> 0x03a7 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a7 }
            r13.<init>()     // Catch:{ all -> 0x03a7 }
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.thumb     // Catch:{ all -> 0x03a7 }
            java.lang.String r15 = r15.url     // Catch:{ all -> 0x03a7 }
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)     // Catch:{ all -> 0x03a7 }
            r13.append(r15)     // Catch:{ all -> 0x03a7 }
            r13.append(r0)     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = r13.toString()     // Catch:{ all -> 0x03a7 }
            r2.<init>(r5, r0)     // Catch:{ all -> 0x03a7 }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ all -> 0x03a7 }
            r2 = 1
            android.graphics.Bitmap r15 = createVideoThumbnail(r0, r2)     // Catch:{ all -> 0x03a7 }
            goto L_0x0387
        L_0x0381:
            float r0 = (float) r14     // Catch:{ all -> 0x03a7 }
            r2 = 1
            android.graphics.Bitmap r15 = org.telegram.messenger.ImageLoader.loadBitmap(r9, r3, r0, r0, r2)     // Catch:{ all -> 0x03a7 }
        L_0x0387:
            if (r15 == 0) goto L_0x0435
            float r0 = (float) r14     // Catch:{ all -> 0x03a7 }
            if (r14 <= r6) goto L_0x038f
            r2 = 80
            goto L_0x0391
        L_0x038f:
            r2 = 55
        L_0x0391:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r15, r0, r0, r2, r7)     // Catch:{ all -> 0x03a7 }
            if (r0 == 0) goto L_0x03a2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r4.thumbs     // Catch:{ all -> 0x03a7 }
            r2.add(r0)     // Catch:{ all -> 0x03a7 }
            int r0 = r4.flags     // Catch:{ all -> 0x03a7 }
            r2 = 1
            r0 = r0 | r2
            r4.flags = r0     // Catch:{ all -> 0x03a7 }
        L_0x03a2:
            r15.recycle()     // Catch:{ all -> 0x03a7 }
            goto L_0x0435
        L_0x03a7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0435
        L_0x03ad:
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            java.lang.String r6 = ""
            r0.alt = r6
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r6 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r6.<init>()
            r0.stickerset = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes
            r6.add(r0)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r0.<init>()
            int[] r6 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r13 = r6[r7]
            r0.w = r13
            r13 = 1
            r6 = r6[r13]
            r0.h = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r4.attributes
            r6.add(r0)
            java.lang.String r0 = "sticker.webp"
            r1.file_name = r0
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.thumb     // Catch:{ all -> 0x0431 }
            if (r0 == 0) goto L_0x0435
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0431 }
            r6 = 4
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r6)     // Catch:{ all -> 0x0431 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x0431 }
            r13.<init>()     // Catch:{ all -> 0x0431 }
            org.telegram.tgnet.TLRPC$WebDocument r14 = r11.thumb     // Catch:{ all -> 0x0431 }
            java.lang.String r14 = r14.url     // Catch:{ all -> 0x0431 }
            java.lang.String r14 = org.telegram.messenger.Utilities.MD5(r14)     // Catch:{ all -> 0x0431 }
            r13.append(r14)     // Catch:{ all -> 0x0431 }
            r13.append(r5)     // Catch:{ all -> 0x0431 }
            org.telegram.tgnet.TLRPC$WebDocument r5 = r11.thumb     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = r5.url     // Catch:{ all -> 0x0431 }
            java.lang.String r14 = "webp"
            java.lang.String r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r14)     // Catch:{ all -> 0x0431 }
            r13.append(r5)     // Catch:{ all -> 0x0431 }
            java.lang.String r5 = r13.toString()     // Catch:{ all -> 0x0431 }
            r0.<init>(r6, r5)     // Catch:{ all -> 0x0431 }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x0431 }
            r5 = 1
            android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r3, r2, r2, r5)     // Catch:{ all -> 0x0431 }
            if (r0 == 0) goto L_0x0435
            r5 = 55
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r0, r2, r2, r5, r7)     // Catch:{ all -> 0x0431 }
            if (r2 == 0) goto L_0x042d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r4.thumbs     // Catch:{ all -> 0x0431 }
            r5.add(r2)     // Catch:{ all -> 0x0431 }
            int r2 = r4.flags     // Catch:{ all -> 0x0431 }
            r5 = 1
            r2 = r2 | r5
            r4.flags = r2     // Catch:{ all -> 0x0431 }
        L_0x042d:
            r0.recycle()     // Catch:{ all -> 0x0431 }
            goto L_0x0435
        L_0x0431:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0435:
            java.lang.String r0 = r1.file_name
            if (r0 != 0) goto L_0x043b
            r1.file_name = r8
        L_0x043b:
            java.lang.String r0 = r4.mime_type
            if (r0 != 0) goto L_0x0443
            java.lang.String r0 = "application/octet-stream"
            r4.mime_type = r0
        L_0x0443:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r4.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0472
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            int[] r1 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r2 = r1[r7]
            r0.w = r2
            r2 = 1
            r1 = r1[r2]
            r0.h = r1
            r0.size = r7
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r1.<init>()
            r0.location = r1
            r0.type = r10
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r4.thumbs
            r1.add(r0)
            int r0 = r4.flags
            r0 = r0 | r2
            r4.flags = r0
        L_0x0472:
            r15 = r3
            r16 = r15
            r0 = r4
            goto L_0x047e
        L_0x0477:
            r19 = r1
            r0 = r3
            r9 = r0
        L_0x047b:
            r15 = r9
        L_0x047c:
            r16 = r15
        L_0x047e:
            if (r12 == 0) goto L_0x048b
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.content
            if (r1 == 0) goto L_0x048b
            java.lang.String r1 = r1.url
            java.lang.String r2 = "originalPath"
            r12.put(r2, r1)
        L_0x048b:
            r1 = 1
            android.graphics.Bitmap[] r8 = new android.graphics.Bitmap[r1]
            java.lang.String[] r10 = new java.lang.String[r1]
            boolean r2 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r0)
            if (r2 == 0) goto L_0x04c0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r0.thumbs
            r3 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r13 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r0)
            boolean r3 = r2.exists()
            if (r3 != 0) goto L_0x04ac
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r1)
        L_0x04ac:
            java.lang.String r3 = r2.getAbsolutePath()
            r4 = 0
            r5 = 0
            r1 = r19
            r2 = r0
            ensureMediaThumbExists(r1, r2, r3, r4, r5)
            r1 = 1
            java.lang.String r1 = getKeyForPhotoSize(r13, r8, r1, r1)
            r10[r7] = r1
        L_0x04c0:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda73 r17 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda73
            r1 = r17
            r2 = r0
            r3 = r8
            r4 = r10
            r5 = r24
            r6 = r9
            r7 = r21
            r9 = r26
            r10 = r27
            r11 = r23
            r12 = r25
            r13 = r28
            r14 = r29
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
        accountInstance.getMessagesStorage().getStorageQueue().postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda6(str, accountInstance, j, z, i));
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
            mediaSendQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda10(arrayList, j, z, z4, accountInstance, messageObject3, messageObject, messageObject2, z3, i, inputContentInfoCompat));
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v89, resolved type: org.telegram.tgnet.TLRPC$Photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v106, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v152, resolved type: java.lang.Object[]} */
    /* JADX WARNING: type inference failed for: r6v0 */
    /* JADX WARNING: type inference failed for: r6v39, types: [boolean] */
    /* JADX WARNING: type inference failed for: r6v40 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:221|222|(8:223|224|225|226|227|228|229|(2:231|232))|233|234) */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005d, code lost:
        if (r4 != false) goto L_0x0061;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0621, code lost:
        if (r5 != null) goto L_0x0604;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0662, code lost:
        if (r3.endsWith(r13) != false) goto L_0x0683;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:233:0x0604 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02cb A[Catch:{ Exception -> 0x02bc }] */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02d6 A[Catch:{ Exception -> 0x02bc }] */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02e3 A[Catch:{ Exception -> 0x0302 }] */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x0310  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x033b  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x034f  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x035a  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x061c A[SYNTHETIC, Splitter:B:246:0x061c] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x06b4  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x070b  */
    /* JADX WARNING: Removed duplicated region for block: B:390:0x08ce  */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x0904  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0b33  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00ac  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:526:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:532:0x0c5b  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0ca4  */
    /* JADX WARNING: Removed duplicated region for block: B:543:0x0ca9  */
    /* JADX WARNING: Removed duplicated region for block: B:545:0x0cb0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0d11 A[LOOP:4: B:558:0x0d09->B:560:0x0d11, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:593:0x0e58  */
    /* JADX WARNING: Removed duplicated region for block: B:594:0x0e5b  */
    /* JADX WARNING: Removed duplicated region for block: B:610:0x0CLASSNAME A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0158  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x015d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingMedia$89(java.util.ArrayList r68, long r69, boolean r71, boolean r72, org.telegram.messenger.AccountInstance r73, org.telegram.messenger.MessageObject r74, org.telegram.messenger.MessageObject r75, org.telegram.messenger.MessageObject r76, boolean r77, int r78, androidx.core.view.inputmethod.InputContentInfoCompat r79) {
        /*
            r1 = r68
            r15 = r73
            long r19 = java.lang.System.currentTimeMillis()
            int r14 = r68.size()
            boolean r13 = org.telegram.messenger.DialogObject.isEncryptedDialog(r69)
            java.lang.String r12 = ".webp"
            java.lang.String r11 = ".gif"
            r21 = 3
            java.lang.String r9 = "_"
            r10 = 0
            r6 = 1
            if (r71 != 0) goto L_0x0180
            if (r72 == 0) goto L_0x0180
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r7 = 0
        L_0x0024:
            if (r7 >= r14) goto L_0x017b
            java.lang.Object r2 = r1.get(r7)
            r5 = r2
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r5 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r5
            org.telegram.messenger.MediaController$SearchImage r2 = r5.searchImage
            if (r2 != 0) goto L_0x016f
            boolean r2 = r5.isVideo
            if (r2 != 0) goto L_0x016f
            org.telegram.messenger.VideoEditedInfo r2 = r5.videoEditedInfo
            if (r2 != 0) goto L_0x016f
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
            int r8 = r68.size()
            if (r8 > r6) goto L_0x0071
            if (r4 == 0) goto L_0x016f
            boolean r4 = shouldSendWebPAsSticker(r2, r10)
            if (r4 == 0) goto L_0x0071
            goto L_0x016f
        L_0x0071:
            r5.forceImage = r6
            goto L_0x00aa
        L_0x0074:
            r4 = 0
        L_0x0075:
            java.lang.String r8 = r5.path
            android.net.Uri r10 = r5.uri
            boolean r8 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r8, r10)
            if (r8 == 0) goto L_0x0081
            goto L_0x016f
        L_0x0081:
            if (r2 != 0) goto L_0x00aa
            android.net.Uri r8 = r5.uri
            if (r8 == 0) goto L_0x00aa
            boolean r8 = org.telegram.messenger.MediaController.isGif(r8)
            if (r8 != 0) goto L_0x0095
            android.net.Uri r4 = r5.uri
            boolean r4 = org.telegram.messenger.MediaController.isWebp(r4)
            if (r4 == 0) goto L_0x00aa
        L_0x0095:
            int r8 = r68.size()
            if (r8 > r6) goto L_0x00a8
            if (r4 == 0) goto L_0x016f
            android.net.Uri r4 = r5.uri
            r8 = 0
            boolean r4 = shouldSendWebPAsSticker(r8, r4)
            if (r4 == 0) goto L_0x00a8
            goto L_0x016f
        L_0x00a8:
            r5.forceImage = r6
        L_0x00aa:
            if (r2 == 0) goto L_0x00d0
            java.io.File r4 = new java.io.File
            r4.<init>(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r10 = r7
            long r6 = r4.length()
            r2.append(r6)
            r2.append(r9)
            long r3 = r4.lastModified()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            goto L_0x00d2
        L_0x00d0:
            r10 = r7
            r2 = 0
        L_0x00d2:
            if (r13 != 0) goto L_0x0147
            int r3 = r5.ttl
            if (r3 != 0) goto L_0x0147
            org.telegram.messenger.MessagesStorage r3 = r73.getMessagesStorage()
            if (r13 != 0) goto L_0x00e0
            r4 = 0
            goto L_0x00e1
        L_0x00e0:
            r4 = 3
        L_0x00e1:
            java.lang.Object[] r2 = r3.getSentFile(r2, r4)
            if (r2 == 0) goto L_0x00f9
            r3 = 0
            r4 = r2[r3]
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r4 == 0) goto L_0x00f9
            r4 = r2[r3]
            r3 = r4
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3
            r4 = 1
            r2 = r2[r4]
            java.lang.String r2 = (java.lang.String) r2
            goto L_0x00fb
        L_0x00f9:
            r2 = 0
            r3 = 0
        L_0x00fb:
            if (r3 != 0) goto L_0x012a
            android.net.Uri r4 = r5.uri
            if (r4 == 0) goto L_0x012a
            org.telegram.messenger.MessagesStorage r4 = r73.getMessagesStorage()
            android.net.Uri r6 = r5.uri
            java.lang.String r6 = org.telegram.messenger.AndroidUtilities.getPath(r6)
            if (r13 != 0) goto L_0x010f
            r7 = 0
            goto L_0x0110
        L_0x010f:
            r7 = 3
        L_0x0110:
            java.lang.Object[] r4 = r4.getSentFile(r6, r7)
            if (r4 == 0) goto L_0x012a
            r6 = 0
            r7 = r4[r6]
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r7 == 0) goto L_0x012a
            r2 = r4[r6]
            org.telegram.tgnet.TLRPC$TL_photo r2 = (org.telegram.tgnet.TLRPC$TL_photo) r2
            r6 = 1
            r3 = r4[r6]
            java.lang.String r3 = (java.lang.String) r3
            r8 = r2
            r18 = r3
            goto L_0x012e
        L_0x012a:
            r6 = 1
            r18 = r2
            r8 = r3
        L_0x012e:
            java.lang.String r4 = r5.path
            android.net.Uri r7 = r5.uri
            r22 = 0
            r2 = r13
            r3 = r8
            r24 = r8
            r8 = r5
            r5 = r7
            r25 = r12
            r12 = 1
            r6 = r22
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r3 = r18
            r2 = r24
            goto L_0x014d
        L_0x0147:
            r8 = r5
            r25 = r12
            r12 = 1
            r2 = 0
            r3 = 0
        L_0x014d:
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r4 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker
            r5 = 0
            r4.<init>()
            r0.put(r8, r4)
            if (r2 == 0) goto L_0x015d
            r4.parentObject = r3
            r4.photo = r2
            goto L_0x0173
        L_0x015d:
            java.util.concurrent.CountDownLatch r2 = new java.util.concurrent.CountDownLatch
            r2.<init>(r12)
            r4.sync = r2
            java.util.concurrent.ThreadPoolExecutor r2 = mediaSendThreadPool
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda20 r3 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda20
            r3.<init>(r4, r15, r8, r13)
            r2.execute(r3)
            goto L_0x0173
        L_0x016f:
            r10 = r7
            r25 = r12
            r12 = 1
        L_0x0173:
            int r7 = r10 + 1
            r12 = r25
            r6 = 1
            r10 = 0
            goto L_0x0024
        L_0x017b:
            r25 = r12
            r12 = 1
            r10 = r0
            goto L_0x0184
        L_0x0180:
            r25 = r12
            r12 = 1
            r10 = 0
        L_0x0184:
            r0 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r22 = 0
            r23 = 0
            r26 = 0
            r27 = 0
        L_0x0192:
            if (r6 >= r14) goto L_0x0dd9
            java.lang.Object r8 = r1.get(r6)
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r8 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r8
            if (r72 == 0) goto L_0x01ad
            if (r14 <= r12) goto L_0x01ad
            int r18 = r0 % 10
            if (r18 != 0) goto L_0x01ad
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r27 = r0.nextLong()
            r31 = r27
            r18 = 0
            goto L_0x01b1
        L_0x01ad:
            r18 = r0
            r31 = r23
        L_0x01b1:
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            r23 = r5
            java.lang.String r5 = "video/mp4"
            r24 = r6
            java.lang.String r6 = "1"
            java.lang.String r7 = "final"
            java.lang.String r12 = "groupId"
            java.lang.String r1 = "mp4"
            r35 = r9
            java.lang.String r9 = "originalPath"
            r36 = r10
            java.lang.String r10 = ""
            r38 = r2
            java.lang.String r2 = "jpg"
            r39 = r3
            java.lang.String r3 = "."
            r40 = 4
            if (r0 == 0) goto L_0x054a
            r41 = r4
            org.telegram.messenger.VideoEditedInfo r4 = r8.videoEditedInfo
            if (r4 != 0) goto L_0x052f
            int r4 = r0.type
            r42 = r11
            r11 = 1
            if (r4 != r11) goto L_0x03b2
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r4 == 0) goto L_0x01f6
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r11)
            goto L_0x0222
        L_0x01f6:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r4 = r8.searchImage
            java.lang.String r4 = r4.imageUrl
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)
            r0.append(r4)
            r0.append(r3)
            org.telegram.messenger.MediaController$SearchImage r4 = r8.searchImage
            java.lang.String r4 = r4.imageUrl
            java.lang.String r4 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r2)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.io.File r4 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r4.<init>(r6, r0)
            r0 = 0
        L_0x0222:
            if (r0 != 0) goto L_0x0341
            org.telegram.tgnet.TLRPC$TL_document r6 = new org.telegram.tgnet.TLRPC$TL_document
            r6.<init>()
            r10 = 0
            r6.id = r10
            r12 = 0
            byte[] r0 = new byte[r12]
            r6.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r73.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r6.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            java.lang.String r12 = "animation.gif"
            r0.file_name = r12
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r6.attributes
            r12.add(r0)
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            int r0 = r0.size
            r6.size = r0
            r12 = 0
            r6.dc_id = r12
            if (r71 != 0) goto L_0x026c
            java.lang.String r0 = r4.toString()
            boolean r0 = r0.endsWith(r1)
            if (r0 == 0) goto L_0x026c
            r6.mime_type = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r6.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r5.<init>()
            r0.add(r5)
            goto L_0x0270
        L_0x026c:
            java.lang.String r0 = "image/gif"
            r6.mime_type = r0
        L_0x0270:
            boolean r0 = r4.exists()
            if (r0 == 0) goto L_0x0278
            r5 = r4
            goto L_0x027a
        L_0x0278:
            r4 = 0
            r5 = 0
        L_0x027a:
            if (r4 != 0) goto L_0x02b0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r4 = r8.searchImage
            java.lang.String r4 = r4.thumbUrl
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)
            r0.append(r4)
            r0.append(r3)
            org.telegram.messenger.MediaController$SearchImage r3 = r8.searchImage
            java.lang.String r3 = r3.thumbUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r3, r2)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r2 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r2.<init>(r3, r0)
            boolean r0 = r2.exists()
            if (r0 != 0) goto L_0x02af
            r4 = 0
            goto L_0x02b0
        L_0x02af:
            r4 = r2
        L_0x02b0:
            if (r4 == 0) goto L_0x0307
            if (r13 != 0) goto L_0x02bf
            int r0 = r8.ttl     // Catch:{ Exception -> 0x02bc }
            if (r0 == 0) goto L_0x02b9
            goto L_0x02bf
        L_0x02b9:
            r0 = 320(0x140, float:4.48E-43)
            goto L_0x02c1
        L_0x02bc:
            r0 = move-exception
            r12 = 0
            goto L_0x0303
        L_0x02bf:
            r0 = 90
        L_0x02c1:
            java.lang.String r2 = r4.getAbsolutePath()     // Catch:{ Exception -> 0x02bc }
            boolean r1 = r2.endsWith(r1)     // Catch:{ Exception -> 0x02bc }
            if (r1 == 0) goto L_0x02d6
            java.lang.String r1 = r4.getAbsolutePath()     // Catch:{ Exception -> 0x02bc }
            r2 = 1
            android.graphics.Bitmap r1 = createVideoThumbnail(r1, r2)     // Catch:{ Exception -> 0x02bc }
            r12 = 0
            goto L_0x02e1
        L_0x02d6:
            r2 = 1
            java.lang.String r1 = r4.getAbsolutePath()     // Catch:{ Exception -> 0x02bc }
            float r3 = (float) r0
            r12 = 0
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r12, r3, r3, r2)     // Catch:{ Exception -> 0x0302 }
        L_0x02e1:
            if (r1 == 0) goto L_0x0308
            float r2 = (float) r0     // Catch:{ Exception -> 0x0302 }
            r3 = 90
            if (r0 <= r3) goto L_0x02eb
            r0 = 80
            goto L_0x02ed
        L_0x02eb:
            r0 = 55
        L_0x02ed:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r2, r2, r0, r13)     // Catch:{ Exception -> 0x0302 }
            if (r0 == 0) goto L_0x02fe
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r6.thumbs     // Catch:{ Exception -> 0x0302 }
            r2.add(r0)     // Catch:{ Exception -> 0x0302 }
            int r0 = r6.flags     // Catch:{ Exception -> 0x0302 }
            r2 = 1
            r0 = r0 | r2
            r6.flags = r0     // Catch:{ Exception -> 0x0302 }
        L_0x02fe:
            r1.recycle()     // Catch:{ Exception -> 0x0302 }
            goto L_0x0308
        L_0x0302:
            r0 = move-exception
        L_0x0303:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0308
        L_0x0307:
            r12 = 0
        L_0x0308:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r6.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x033b
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r1 = r8.searchImage
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
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r6.thumbs
            r2.add(r0)
            int r0 = r6.flags
            r16 = 1
            r0 = r0 | 1
            r6.flags = r0
            goto L_0x033e
        L_0x033b:
            r1 = 0
            r16 = 1
        L_0x033e:
            r4 = r5
            r5 = r6
            goto L_0x0348
        L_0x0341:
            r1 = 0
            r10 = 0
            r12 = 0
            r16 = 1
            r5 = r0
        L_0x0348:
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r4 != 0) goto L_0x034f
            goto L_0x0353
        L_0x034f:
            java.lang.String r0 = r4.toString()
        L_0x0353:
            r6 = r0
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r0 == 0) goto L_0x035d
            r7.put(r9, r0)
        L_0x035d:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda13 r0 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda13
            r9 = 0
            r4 = r38
            r2 = r0
            r43 = r39
            r3 = r74
            r45 = r4
            r44 = r41
            r4 = r73
            r46 = r23
            r47 = r24
            r23 = r10
            r11 = r8
            r8 = r9
            r49 = r35
            r48 = r36
            r9 = r69
            r17 = r11
            r50 = r42
            r11 = r75
            r1 = r12
            r51 = r25
            r12 = r76
            r52 = r13
            r13 = r17
            r53 = r14
            r14 = r77
            r15 = r78
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            r35 = r1
            r0 = r18
            r36 = r31
            r3 = r43
            r4 = r44
            r2 = r45
            r5 = r46
            r33 = r47
            r32 = r48
            r38 = r49
            r31 = r50
            r29 = r51
            r34 = r52
            goto L_0x0dc2
        L_0x03b2:
            r17 = r8
            r52 = r13
            r53 = r14
            r46 = r23
            r47 = r24
            r51 = r25
            r49 = r35
            r48 = r36
            r45 = r38
            r43 = r39
            r44 = r41
            r50 = r42
            r1 = 0
            r23 = 0
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r4 == 0) goto L_0x03d6
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            goto L_0x03d7
        L_0x03d6:
            r0 = r1
        L_0x03d7:
            if (r0 != 0) goto L_0x049f
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r15 = r17
            org.telegram.messenger.MediaController$SearchImage r5 = r15.searchImage
            java.lang.String r5 = r5.imageUrl
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)
            r4.append(r5)
            r4.append(r3)
            org.telegram.messenger.MediaController$SearchImage r5 = r15.searchImage
            java.lang.String r5 = r5.imageUrl
            java.lang.String r5 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r5, r2)
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.io.File r5 = new java.io.File
            java.io.File r8 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r5.<init>(r8, r4)
            boolean r4 = r5.exists()
            if (r4 == 0) goto L_0x0424
            long r13 = r5.length()
            int r4 = (r13 > r23 ? 1 : (r13 == r23 ? 0 : -1))
            if (r4 == 0) goto L_0x0424
            org.telegram.messenger.SendMessagesHelper r0 = r73.getSendMessagesHelper()
            java.lang.String r4 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r4, r1)
            if (r0 == 0) goto L_0x0424
            r8 = 0
            goto L_0x0425
        L_0x0424:
            r8 = 1
        L_0x0425:
            if (r0 != 0) goto L_0x049c
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            org.telegram.messenger.MediaController$SearchImage r5 = r15.searchImage
            java.lang.String r5 = r5.thumbUrl
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)
            r4.append(r5)
            r4.append(r3)
            org.telegram.messenger.MediaController$SearchImage r3 = r15.searchImage
            java.lang.String r3 = r3.thumbUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r3, r2)
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r3.<init>(r4, r2)
            boolean r2 = r3.exists()
            if (r2 == 0) goto L_0x0464
            org.telegram.messenger.SendMessagesHelper r0 = r73.getSendMessagesHelper()
            java.lang.String r2 = r3.toString()
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r1)
        L_0x0464:
            if (r0 != 0) goto L_0x049c
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r2 = r73.getConnectionsManager()
            int r2 = r2.getCurrentTime()
            r0.date = r2
            r14 = 0
            byte[] r2 = new byte[r14]
            r0.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize r2 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r2.<init>()
            org.telegram.messenger.MediaController$SearchImage r3 = r15.searchImage
            int r4 = r3.width
            r2.w = r4
            int r3 = r3.height
            r2.h = r3
            r2.size = r14
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r3 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r3.<init>()
            r2.location = r3
            java.lang.String r3 = "x"
            r2.type = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r0.sizes
            r3.add(r2)
            goto L_0x049d
        L_0x049c:
            r14 = 0
        L_0x049d:
            r5 = r0
            goto L_0x04a4
        L_0x049f:
            r15 = r17
            r14 = 0
            r5 = r0
            r8 = 1
        L_0x04a4:
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r15.searchImage
            java.lang.String r2 = r2.imageUrl
            if (r2 == 0) goto L_0x04b2
            r0.put(r9, r2)
        L_0x04b2:
            if (r72 == 0) goto L_0x04e6
            int r2 = r18 + 1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            r10 = r31
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            r0.put(r12, r3)
            r13 = 10
            if (r2 == r13) goto L_0x04da
            r13 = r53
            int r3 = r13 + -1
            r12 = r47
            if (r12 != r3) goto L_0x04d7
            goto L_0x04de
        L_0x04d7:
            r18 = r2
            goto L_0x04ec
        L_0x04da:
            r12 = r47
            r13 = r53
        L_0x04de:
            r0.put(r7, r6)
            r18 = r2
            r27 = r23
            goto L_0x04ec
        L_0x04e6:
            r10 = r31
            r12 = r47
            r13 = r53
        L_0x04ec:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda15 r16 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda15
            r9 = 0
            r2 = r16
            r3 = r74
            r4 = r73
            r6 = r8
            r7 = r15
            r8 = r0
            r54 = r10
            r10 = r69
            r15 = r12
            r12 = r75
            r56 = r13
            r13 = r76
            r17 = 0
            r14 = r77
            r57 = r15
            r15 = r78
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r16)
            r35 = r1
            r0 = r18
            r3 = r43
            r4 = r44
            r2 = r45
            r5 = r46
            r32 = r48
            r38 = r49
            r31 = r50
            r29 = r51
            r34 = r52
            r36 = r54
            r53 = r56
            r33 = r57
            goto L_0x0dc2
        L_0x052f:
            r0 = r1
            r15 = r8
            r50 = r11
            r52 = r13
            r56 = r14
            r46 = r23
            r57 = r24
            r51 = r25
            r54 = r31
            r49 = r35
            r48 = r36
            r45 = r38
            r43 = r39
            r44 = r41
            goto L_0x0564
        L_0x054a:
            r0 = r1
            r44 = r4
            r15 = r8
            r50 = r11
            r52 = r13
            r56 = r14
            r46 = r23
            r57 = r24
            r51 = r25
            r54 = r31
            r49 = r35
            r48 = r36
            r45 = r38
            r43 = r39
        L_0x0564:
            r1 = 0
            r13 = 10
            r14 = 0
            r23 = 0
            boolean r4 = r15.isVideo
            if (r4 != 0) goto L_0x09b4
            org.telegram.messenger.VideoEditedInfo r4 = r15.videoEditedInfo
            if (r4 == 0) goto L_0x0574
            goto L_0x09b4
        L_0x0574:
            java.lang.String r0 = r15.path
            if (r0 != 0) goto L_0x059e
            android.net.Uri r2 = r15.uri
            if (r2 == 0) goto L_0x059e
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 30
            if (r0 < r3) goto L_0x0590
            java.lang.String r0 = r2.getScheme()
            java.lang.String r2 = "content"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x0590
            r0 = r1
            goto L_0x0596
        L_0x0590:
            android.net.Uri r0 = r15.uri
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r0)
        L_0x0596:
            android.net.Uri r2 = r15.uri
            java.lang.String r2 = r2.toString()
            r3 = r0
            goto L_0x05a0
        L_0x059e:
            r2 = r0
            r3 = r2
        L_0x05a0:
            if (r79 == 0) goto L_0x0633
            android.net.Uri r0 = r15.uri
            if (r0 == 0) goto L_0x0633
            android.content.ClipDescription r0 = r79.getDescription()
            java.lang.String r4 = "image/png"
            boolean r0 = r0.hasMimeType(r4)
            if (r0 == 0) goto L_0x0633
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0612 }
            r0.<init>()     // Catch:{ all -> 0x0612 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0612 }
            android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x0612 }
            android.net.Uri r5 = r15.uri     // Catch:{ all -> 0x0612 }
            java.io.InputStream r4 = r4.openInputStream(r5)     // Catch:{ all -> 0x0612 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r4, r1, r0)     // Catch:{ all -> 0x060e }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x060e }
            r5.<init>()     // Catch:{ all -> 0x060e }
            java.lang.String r8 = "-2147483648_"
            r5.append(r8)     // Catch:{ all -> 0x060e }
            int r8 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ all -> 0x060e }
            r5.append(r8)     // Catch:{ all -> 0x060e }
            r11 = r51
            r5.append(r11)     // Catch:{ all -> 0x060c }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x060c }
            java.io.File r8 = org.telegram.messenger.FileLoader.getDirectory(r40)     // Catch:{ all -> 0x060c }
            java.io.File r13 = new java.io.File     // Catch:{ all -> 0x060c }
            r13.<init>(r8, r5)     // Catch:{ all -> 0x060c }
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ all -> 0x060c }
            r5.<init>(r13)     // Catch:{ all -> 0x060c }
            android.graphics.Bitmap$CompressFormat r8 = android.graphics.Bitmap.CompressFormat.WEBP     // Catch:{ all -> 0x060a }
            r1 = 100
            r0.compress(r8, r1, r5)     // Catch:{ all -> 0x060a }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ all -> 0x060a }
            android.net.Uri r0 = android.net.Uri.fromFile(r13)     // Catch:{ all -> 0x060a }
            r15.uri = r0     // Catch:{ all -> 0x060a }
            if (r4 == 0) goto L_0x0604
            r4.close()     // Catch:{ Exception -> 0x0604 }
        L_0x0604:
            r5.close()     // Catch:{ Exception -> 0x0608 }
            goto L_0x0635
        L_0x0608:
            goto L_0x0635
        L_0x060a:
            r0 = move-exception
            goto L_0x0617
        L_0x060c:
            r0 = move-exception
            goto L_0x0616
        L_0x060e:
            r0 = move-exception
            r11 = r51
            goto L_0x0616
        L_0x0612:
            r0 = move-exception
            r11 = r51
            r4 = 0
        L_0x0616:
            r5 = 0
        L_0x0617:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0624 }
            if (r4 == 0) goto L_0x0621
            r4.close()     // Catch:{ Exception -> 0x0620 }
            goto L_0x0621
        L_0x0620:
        L_0x0621:
            if (r5 == 0) goto L_0x0635
            goto L_0x0604
        L_0x0624:
            r0 = move-exception
            r1 = r0
            if (r4 == 0) goto L_0x062d
            r4.close()     // Catch:{ Exception -> 0x062c }
            goto L_0x062d
        L_0x062c:
        L_0x062d:
            if (r5 == 0) goto L_0x0632
            r5.close()     // Catch:{ Exception -> 0x0632 }
        L_0x0632:
            throw r1
        L_0x0633:
            r11 = r51
        L_0x0635:
            java.lang.String r0 = "webp"
            java.lang.String r1 = "gif"
            if (r71 != 0) goto L_0x06a0
            java.lang.String r4 = r15.path
            android.net.Uri r5 = r15.uri
            boolean r4 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r4, r5)
            if (r4 == 0) goto L_0x0646
            goto L_0x06a0
        L_0x0646:
            boolean r4 = r15.forceImage
            if (r4 != 0) goto L_0x0665
            if (r3 == 0) goto L_0x0665
            r13 = r50
            boolean r4 = r3.endsWith(r13)
            if (r4 != 0) goto L_0x065a
            boolean r4 = r3.endsWith(r11)
            if (r4 == 0) goto L_0x0667
        L_0x065a:
            int r4 = r15.ttl
            if (r4 > 0) goto L_0x0667
            boolean r4 = r3.endsWith(r13)
            if (r4 == 0) goto L_0x069a
            goto L_0x0683
        L_0x0665:
            r13 = r50
        L_0x0667:
            boolean r4 = r15.forceImage
            if (r4 != 0) goto L_0x069d
            if (r3 != 0) goto L_0x069d
            android.net.Uri r4 = r15.uri
            if (r4 == 0) goto L_0x069d
            boolean r4 = org.telegram.messenger.MediaController.isGif(r4)
            if (r4 == 0) goto L_0x0686
            android.net.Uri r0 = r15.uri
            java.lang.String r2 = r0.toString()
            android.net.Uri r0 = r15.uri
            java.lang.String r3 = org.telegram.messenger.MediaController.copyFileToCache(r0, r1)
        L_0x0683:
            r22 = r1
            goto L_0x06b0
        L_0x0686:
            android.net.Uri r1 = r15.uri
            boolean r1 = org.telegram.messenger.MediaController.isWebp(r1)
            if (r1 == 0) goto L_0x069d
            android.net.Uri r1 = r15.uri
            java.lang.String r2 = r1.toString()
            android.net.Uri r1 = r15.uri
            java.lang.String r3 = org.telegram.messenger.MediaController.copyFileToCache(r1, r0)
        L_0x069a:
            r22 = r0
            goto L_0x06b0
        L_0x069d:
            r1 = r3
            r8 = 0
            goto L_0x06b2
        L_0x06a0:
            r13 = r50
            if (r3 == 0) goto L_0x06ae
            java.io.File r0 = new java.io.File
            r0.<init>(r3)
            java.lang.String r0 = org.telegram.messenger.FileLoader.getFileExtension(r0)
            goto L_0x069a
        L_0x06ae:
            r22 = r10
        L_0x06b0:
            r1 = r3
            r8 = 1
        L_0x06b2:
            if (r8 == 0) goto L_0x070b
            r8 = r46
            if (r8 != 0) goto L_0x06d4
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r26 = new java.util.ArrayList
            r26.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r6 = r26
            goto L_0x06dd
        L_0x06d4:
            r5 = r8
            r6 = r26
            r3 = r43
            r4 = r44
            r0 = r45
        L_0x06dd:
            r5.add(r1)
            r4.add(r2)
            android.net.Uri r1 = r15.uri
            r3.add(r1)
            java.lang.String r1 = r15.caption
            r0.add(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r15.entities
            r6.add(r1)
            r2 = r0
            r26 = r6
            r29 = r11
            r31 = r13
            r0 = r18
            r32 = r48
            r38 = r49
            r34 = r52
            r36 = r54
            r53 = r56
            r33 = r57
        L_0x0707:
            r35 = 0
            goto L_0x0dc2
        L_0x070b:
            r8 = r46
            if (r1 == 0) goto L_0x0739
            java.io.File r0 = new java.io.File
            r0.<init>(r1)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            long r4 = r0.length()
            r3.append(r4)
            r5 = r49
            r3.append(r5)
            r17 = r15
            long r14 = r0.lastModified()
            r3.append(r14)
            java.lang.String r0 = r3.toString()
            r14 = r0
            r15 = r48
            goto L_0x0740
        L_0x0739:
            r17 = r15
            r5 = r49
            r15 = r48
            r14 = 0
        L_0x0740:
            if (r15 == 0) goto L_0x076f
            r4 = r17
            java.lang.Object r0 = r15.get(r4)
            r2 = r0
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
            if (r0 != 0) goto L_0x075f
            java.util.concurrent.CountDownLatch r0 = r2.sync     // Catch:{ Exception -> 0x0757 }
            r0.await()     // Catch:{ Exception -> 0x0757 }
            goto L_0x075b
        L_0x0757:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x075b:
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
        L_0x075f:
            r30 = r1
            r60 = r5
            r61 = r6
            r62 = r7
            r6 = r52
            r1 = 1
            r5 = r0
            r7 = r4
            r4 = r3
            goto L_0x0837
        L_0x076f:
            r4 = r17
            r3 = r52
            if (r3 != 0) goto L_0x07fb
            int r0 = r4.ttl
            if (r0 != 0) goto L_0x07fb
            org.telegram.messenger.MessagesStorage r0 = r73.getMessagesStorage()
            if (r3 != 0) goto L_0x0781
            r2 = 0
            goto L_0x0782
        L_0x0781:
            r2 = 3
        L_0x0782:
            java.lang.Object[] r0 = r0.getSentFile(r14, r2)
            r49 = r5
            if (r0 == 0) goto L_0x079c
            r2 = 0
            r5 = r0[r2]
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r5 == 0) goto L_0x079c
            r5 = r0[r2]
            r2 = r5
            org.telegram.tgnet.TLRPC$TL_photo r2 = (org.telegram.tgnet.TLRPC$TL_photo) r2
            r5 = 1
            r0 = r0[r5]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x079f
        L_0x079c:
            r5 = 1
            r0 = 0
            r2 = 0
        L_0x079f:
            if (r2 != 0) goto L_0x07d2
            android.net.Uri r5 = r4.uri
            if (r5 == 0) goto L_0x07d2
            org.telegram.messenger.MessagesStorage r5 = r73.getMessagesStorage()
            r17 = r0
            android.net.Uri r0 = r4.uri
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r0)
            r29 = r2
            if (r3 != 0) goto L_0x07b7
            r2 = 0
            goto L_0x07b8
        L_0x07b7:
            r2 = 3
        L_0x07b8:
            java.lang.Object[] r0 = r5.getSentFile(r0, r2)
            if (r0 == 0) goto L_0x07d6
            r2 = 0
            r5 = r0[r2]
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r5 == 0) goto L_0x07d6
            r5 = r0[r2]
            org.telegram.tgnet.TLRPC$TL_photo r5 = (org.telegram.tgnet.TLRPC$TL_photo) r5
            r30 = 1
            r0 = r0[r30]
            java.lang.String r0 = (java.lang.String) r0
            r29 = r5
            goto L_0x07da
        L_0x07d2:
            r17 = r0
            r29 = r2
        L_0x07d6:
            r30 = 1
            r0 = r17
        L_0x07da:
            java.lang.String r5 = r4.path
            android.net.Uri r2 = r4.uri
            r31 = 0
            r17 = r2
            r2 = r3
            r58 = r3
            r3 = r29
            r59 = r4
            r4 = r5
            r30 = r1
            r60 = r49
            r1 = 1
            r5 = r17
            r61 = r6
            r62 = r7
            r6 = r31
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            goto L_0x080b
        L_0x07fb:
            r30 = r1
            r58 = r3
            r59 = r4
            r60 = r5
            r61 = r6
            r62 = r7
            r1 = 1
            r0 = 0
            r29 = 0
        L_0x080b:
            if (r29 != 0) goto L_0x0830
            org.telegram.messenger.SendMessagesHelper r2 = r73.getSendMessagesHelper()
            r7 = r59
            java.lang.String r3 = r7.path
            android.net.Uri r4 = r7.uri
            org.telegram.tgnet.TLRPC$TL_photo r2 = r2.generatePhotoSizes(r3, r4)
            r6 = r58
            if (r6 == 0) goto L_0x082d
            boolean r3 = r7.canDeleteAfter
            if (r3 == 0) goto L_0x082d
            java.io.File r3 = new java.io.File
            java.lang.String r4 = r7.path
            r3.<init>(r4)
            r3.delete()
        L_0x082d:
            r4 = r0
            r5 = r2
            goto L_0x0837
        L_0x0830:
            r6 = r58
            r7 = r59
            r4 = r0
            r5 = r29
        L_0x0837:
            if (r5 == 0) goto L_0x0952
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            android.graphics.Bitmap[] r2 = new android.graphics.Bitmap[r1]
            r52 = r6
            java.lang.String[] r6 = new java.lang.String[r1]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r0 = r7.masks
            if (r0 == 0) goto L_0x0850
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0850
            r0 = 1
            goto L_0x0851
        L_0x0850:
            r0 = 0
        L_0x0851:
            r5.has_stickers = r0
            if (r0 == 0) goto L_0x0897
            org.telegram.tgnet.SerializedData r0 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r7.masks
            int r1 = r1.size()
            int r1 = r1 * 20
            int r1 = r1 + 4
            r0.<init>((int) r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r7.masks
            int r1 = r1.size()
            r0.writeInt32(r1)
            r46 = r8
            r1 = 0
        L_0x0870:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r8 = r7.masks
            int r8 = r8.size()
            if (r1 >= r8) goto L_0x0886
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r8 = r7.masks
            java.lang.Object r8 = r8.get(r1)
            org.telegram.tgnet.TLRPC$InputDocument r8 = (org.telegram.tgnet.TLRPC$InputDocument) r8
            r8.serializeToStream(r0)
            int r1 = r1 + 1
            goto L_0x0870
        L_0x0886:
            byte[] r1 = r0.toByteArray()
            java.lang.String r1 = org.telegram.messenger.Utilities.bytesToHex(r1)
            java.lang.String r8 = "masks"
            r3.put(r8, r1)
            r0.cleanup()
            goto L_0x0899
        L_0x0897:
            r46 = r8
        L_0x0899:
            if (r14 == 0) goto L_0x089e
            r3.put(r9, r14)
        L_0x089e:
            if (r4 == 0) goto L_0x08a5
            java.lang.String r0 = "parentObject"
            r3.put(r0, r4)
        L_0x08a5:
            if (r72 == 0) goto L_0x08b1
            int r0 = r68.size()     // Catch:{ Exception -> 0x08c7 }
            r1 = 1
            if (r0 != r1) goto L_0x08af
            goto L_0x08b1
        L_0x08af:
            r1 = 0
            goto L_0x08cc
        L_0x08b1:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r5.sizes     // Catch:{ Exception -> 0x08c7 }
            int r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize()     // Catch:{ Exception -> 0x08c7 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r1)     // Catch:{ Exception -> 0x08c7 }
            if (r0 == 0) goto L_0x08af
            r1 = 0
            java.lang.String r0 = getKeyForPhotoSize(r0, r2, r1, r1)     // Catch:{ Exception -> 0x08c5 }
            r6[r1] = r0     // Catch:{ Exception -> 0x08c5 }
            goto L_0x08cc
        L_0x08c5:
            r0 = move-exception
            goto L_0x08c9
        L_0x08c7:
            r0 = move-exception
            r1 = 0
        L_0x08c9:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x08cc:
            if (r72 == 0) goto L_0x0904
            int r0 = r18 + 1
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r10)
            r9 = r54
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r3.put(r12, r8)
            r14 = 10
            if (r0 == r14) goto L_0x08f4
            r14 = r56
            int r8 = r14 + -1
            r12 = r57
            if (r12 != r8) goto L_0x08f1
            goto L_0x08f8
        L_0x08f1:
            r18 = r0
            goto L_0x090a
        L_0x08f4:
            r14 = r56
            r12 = r57
        L_0x08f8:
            r8 = r61
            r1 = r62
            r3.put(r1, r8)
            r18 = r0
            r27 = r23
            goto L_0x090a
        L_0x0904:
            r9 = r54
            r14 = r56
            r12 = r57
        L_0x090a:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda74 r0 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda74
            r1 = r2
            r2 = r0
            r8 = r3
            r3 = r1
            r1 = r4
            r4 = r6
            r29 = r5
            r5 = r74
            r63 = r52
            r6 = r73
            r17 = r7
            r7 = r29
            r64 = r46
            r65 = r9
            r9 = r1
            r29 = r11
            r10 = r69
            r1 = r12
            r12 = r75
            r31 = r13
            r13 = r76
            r47 = r1
            r53 = r14
            r1 = 0
            r14 = r17
            r32 = r15
            r15 = r77
            r16 = r78
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            r0 = r18
            r3 = r43
            r4 = r44
            r2 = r45
            r33 = r47
            r38 = r60
            r34 = r63
            r5 = r64
            goto L_0x09b0
        L_0x0952:
            r63 = r6
            r17 = r7
            r29 = r11
            r31 = r13
            r32 = r15
            r65 = r54
            r53 = r56
            r47 = r57
            r1 = 0
            r15 = r8
            if (r15 != 0) goto L_0x0984
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r26 = new java.util.ArrayList
            r26.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r0 = r26
            r6 = r30
            goto L_0x098f
        L_0x0984:
            r5 = r15
            r0 = r26
            r6 = r30
            r3 = r43
            r4 = r44
            r2 = r45
        L_0x098f:
            r5.add(r6)
            r4.add(r14)
            r13 = r17
            android.net.Uri r6 = r13.uri
            r3.add(r6)
            java.lang.String r6 = r13.caption
            r2.add(r6)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r13.entities
            r0.add(r6)
            r26 = r0
            r0 = r18
            r33 = r47
            r38 = r60
            r34 = r63
        L_0x09b0:
            r36 = r65
            goto L_0x0707
        L_0x09b4:
            r8 = r6
            r6 = r7
            r13 = r15
            r15 = r46
            r32 = r48
            r60 = r49
            r31 = r50
            r29 = r51
            r63 = r52
            r65 = r54
            r53 = r56
            r11 = r57
            r1 = 0
            r14 = 10
            if (r71 == 0) goto L_0x09d0
            r7 = 0
            goto L_0x09dc
        L_0x09d0:
            org.telegram.messenger.VideoEditedInfo r4 = r13.videoEditedInfo
            if (r4 == 0) goto L_0x09d5
            goto L_0x09db
        L_0x09d5:
            java.lang.String r4 = r13.path
            org.telegram.messenger.VideoEditedInfo r4 = createCompressionSettings(r4)
        L_0x09db:
            r7 = r4
        L_0x09dc:
            if (r71 != 0) goto L_0x0d6c
            if (r7 != 0) goto L_0x09e8
            java.lang.String r4 = r13.path
            boolean r0 = r4.endsWith(r0)
            if (r0 == 0) goto L_0x0d6c
        L_0x09e8:
            java.lang.String r0 = r13.path
            if (r0 != 0) goto L_0x0a33
            org.telegram.messenger.MediaController$SearchImage r0 = r13.searchImage
            if (r0 == 0) goto L_0x0a33
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r4 == 0) goto L_0x0a02
            r4 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r4)
            java.lang.String r0 = r0.getAbsolutePath()
            r13.path = r0
            goto L_0x0a33
        L_0x0a02:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r4 = r13.searchImage
            java.lang.String r4 = r4.imageUrl
            java.lang.String r4 = org.telegram.messenger.Utilities.MD5(r4)
            r0.append(r4)
            r0.append(r3)
            org.telegram.messenger.MediaController$SearchImage r3 = r13.searchImage
            java.lang.String r3 = r3.imageUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r3, r2)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r2 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r2.<init>(r3, r0)
            java.lang.String r0 = r2.getAbsolutePath()
            r13.path = r0
        L_0x0a33:
            java.lang.String r0 = r13.path
            java.io.File r4 = new java.io.File
            r4.<init>(r0)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            r46 = r15
            long r14 = r4.length()
            r2.append(r14)
            r15 = r60
            r2.append(r15)
            r14 = r5
            r62 = r6
            long r5 = r4.lastModified()
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            if (r7 == 0) goto L_0x0ab8
            boolean r3 = r7.muted
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            long r1 = r7.estimatedDuration
            r5.append(r1)
            r5.append(r15)
            long r1 = r7.startTime
            r5.append(r1)
            r5.append(r15)
            long r1 = r7.endTime
            r5.append(r1)
            boolean r1 = r7.muted
            if (r1 == 0) goto L_0x0a86
            java.lang.String r1 = "_m"
            goto L_0x0a87
        L_0x0a86:
            r1 = r10
        L_0x0a87:
            r5.append(r1)
            java.lang.String r1 = r5.toString()
            int r2 = r7.resultWidth
            int r5 = r7.originalWidth
            if (r2 == r5) goto L_0x0aa8
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            r2.append(r15)
            int r1 = r7.resultWidth
            r2.append(r1)
            java.lang.String r1 = r2.toString()
        L_0x0aa8:
            r2 = r1
            long r5 = r7.startTime
            int r1 = (r5 > r23 ? 1 : (r5 == r23 ? 0 : -1))
            if (r1 < 0) goto L_0x0ab0
            goto L_0x0ab2
        L_0x0ab0:
            r5 = r23
        L_0x0ab2:
            r1 = r2
            r16 = r3
            r3 = r63
            goto L_0x0abf
        L_0x0ab8:
            r1 = r2
            r5 = r23
            r3 = r63
            r16 = 0
        L_0x0abf:
            if (r3 != 0) goto L_0x0b21
            int r2 = r13.ttl
            if (r2 != 0) goto L_0x0b21
            if (r7 == 0) goto L_0x0ad7
            org.telegram.messenger.MediaController$SavedFilterState r2 = r7.filterState
            if (r2 != 0) goto L_0x0b21
            java.lang.String r2 = r7.paintPath
            if (r2 != 0) goto L_0x0b21
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r2 = r7.mediaEntities
            if (r2 != 0) goto L_0x0b21
            org.telegram.messenger.MediaController$CropState r2 = r7.cropState
            if (r2 != 0) goto L_0x0b21
        L_0x0ad7:
            org.telegram.messenger.MessagesStorage r2 = r73.getMessagesStorage()
            if (r3 != 0) goto L_0x0ae3
            r17 = 2
            r33 = r0
            r0 = 2
            goto L_0x0ae8
        L_0x0ae3:
            r17 = 5
            r33 = r0
            r0 = 5
        L_0x0ae8:
            java.lang.Object[] r0 = r2.getSentFile(r1, r0)
            if (r0 == 0) goto L_0x0b23
            r17 = r4
            r2 = 0
            r4 = r0[r2]
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r4 == 0) goto L_0x0b1e
            r4 = r0[r2]
            r34 = r4
            org.telegram.tgnet.TLRPC$TL_document r34 = (org.telegram.tgnet.TLRPC$TL_document) r34
            r2 = 1
            r0 = r0[r2]
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r4 = r13.path
            r35 = 0
            r2 = r3
            r67 = r3
            r3 = r34
            r38 = r5
            r5 = r35
            r35 = r0
            r0 = r7
            r49 = r15
            r15 = r62
            r6 = r38
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r7 = r35
            goto L_0x0b31
        L_0x0b1e:
            r67 = r3
            goto L_0x0b27
        L_0x0b21:
            r33 = r0
        L_0x0b23:
            r67 = r3
            r17 = r4
        L_0x0b27:
            r38 = r5
            r0 = r7
            r49 = r15
            r15 = r62
            r7 = 0
            r34 = 0
        L_0x0b31:
            if (r34 != 0) goto L_0x0CLASSNAME
            java.lang.String r2 = r13.thumbPath
            if (r2 == 0) goto L_0x0b3c
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2)
            goto L_0x0b3d
        L_0x0b3c:
            r2 = 0
        L_0x0b3d:
            if (r2 != 0) goto L_0x0b50
            java.lang.String r2 = r13.path
            r5 = r38
            android.graphics.Bitmap r2 = createVideoThumbnailAtTime(r2, r5)
            if (r2 != 0) goto L_0x0b50
            java.lang.String r2 = r13.path
            r3 = 1
            android.graphics.Bitmap r2 = createVideoThumbnail(r2, r3)
        L_0x0b50:
            if (r2 == 0) goto L_0x0b84
            r6 = r67
            if (r6 != 0) goto L_0x0b68
            int r3 = r13.ttl
            if (r3 == 0) goto L_0x0b5b
            goto L_0x0b68
        L_0x0b5b:
            int r3 = r2.getWidth()
            int r4 = r2.getHeight()
            int r3 = java.lang.Math.max(r3, r4)
            goto L_0x0b6a
        L_0x0b68:
            r3 = 90
        L_0x0b6a:
            float r4 = (float) r3
            r5 = 90
            if (r3 <= r5) goto L_0x0b72
            r3 = 80
            goto L_0x0b74
        L_0x0b72:
            r3 = 55
        L_0x0b74:
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r4, r4, r3, r6)
            r34 = r2
            r2 = 1
            r4 = 0
            r5 = 0
            java.lang.String r30 = getKeyForPhotoSize(r3, r4, r2, r5)
            r4 = r30
            goto L_0x0b8b
        L_0x0b84:
            r34 = r2
            r6 = r67
            r5 = 0
            r3 = 0
            r4 = 0
        L_0x0b8b:
            org.telegram.tgnet.TLRPC$TL_document r2 = new org.telegram.tgnet.TLRPC$TL_document
            r2.<init>()
            r35 = r4
            byte[] r4 = new byte[r5]
            r2.file_reference = r4
            if (r3 == 0) goto L_0x0ba3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r2.thumbs
            r4.add(r3)
            int r3 = r2.flags
            r4 = 1
            r3 = r3 | r4
            r2.flags = r3
        L_0x0ba3:
            r2.mime_type = r14
            org.telegram.messenger.UserConfig r3 = r73.getUserConfig()
            r4 = 0
            r3.saveConfig(r4)
            if (r6 == 0) goto L_0x0bb5
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r3.<init>()
            goto L_0x0bbd
        L_0x0bb5:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r3 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r3.<init>()
            r4 = 1
            r3.supports_streaming = r4
        L_0x0bbd:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r2.attributes
            r4.add(r3)
            if (r0 == 0) goto L_0x0CLASSNAME
            boolean r4 = r0.needConvert()
            if (r4 != 0) goto L_0x0bce
            boolean r4 = r13.isVideo
            if (r4 != 0) goto L_0x0CLASSNAME
        L_0x0bce:
            boolean r4 = r13.isVideo
            if (r4 == 0) goto L_0x0be4
            boolean r4 = r0.muted
            if (r4 == 0) goto L_0x0be4
            java.lang.String r4 = r13.path
            fillVideoAttribute(r4, r3, r0)
            int r4 = r3.w
            r0.originalWidth = r4
            int r4 = r3.h
            r0.originalHeight = r4
            goto L_0x0bed
        L_0x0be4:
            long r4 = r0.estimatedDuration
            r38 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 / r38
            int r5 = (int) r4
            r3.duration = r5
        L_0x0bed:
            int r4 = r0.rotationValue
            org.telegram.messenger.MediaController$CropState r5 = r0.cropState
            if (r5 == 0) goto L_0x0bf8
            int r14 = r5.transformWidth
            int r5 = r5.transformHeight
            goto L_0x0bfc
        L_0x0bf8:
            int r14 = r0.resultWidth
            int r5 = r0.resultHeight
        L_0x0bfc:
            r52 = r6
            r6 = 90
            if (r4 == r6) goto L_0x0c0c
            r6 = 270(0x10e, float:3.78E-43)
            if (r4 != r6) goto L_0x0CLASSNAME
            goto L_0x0c0c
        L_0x0CLASSNAME:
            r3.w = r14
            r3.h = r5
            goto L_0x0CLASSNAME
        L_0x0c0c:
            r3.w = r5
            r3.h = r14
        L_0x0CLASSNAME:
            long r3 = r0.estimatedSize
            int r4 = (int) r3
            r2.size = r4
            r14 = 0
            goto L_0x0c2c
        L_0x0CLASSNAME:
            r52 = r6
            boolean r4 = r17.exists()
            if (r4 == 0) goto L_0x0CLASSNAME
            long r4 = r17.length()
            int r5 = (int) r4
            r2.size = r5
        L_0x0CLASSNAME:
            java.lang.String r4 = r13.path
            r14 = 0
            fillVideoAttribute(r4, r3, r14)
        L_0x0c2c:
            r6 = r2
            r3 = r34
            r4 = r35
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r52 = r67
            r14 = 0
            r3 = r14
            r4 = r3
            r6 = r34
        L_0x0CLASSNAME:
            if (r0 == 0) goto L_0x0CLASSNAME
            boolean r2 = r0.muted
            if (r2 == 0) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r6.attributes
            int r2 = r2.size()
            r5 = 0
        L_0x0CLASSNAME:
            if (r5 >= r2) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r14 = r6.attributes
            java.lang.Object r14 = r14.get(r5)
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            if (r14 == 0) goto L_0x0CLASSNAME
            r2 = 1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            int r5 = r5 + 1
            r14 = 0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r2 = 0
        L_0x0CLASSNAME:
            if (r2 != 0) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r6.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r5.<init>()
            r2.add(r5)
        L_0x0CLASSNAME:
            if (r0 == 0) goto L_0x0c9d
            boolean r2 = r0.needConvert()
            if (r2 != 0) goto L_0x0CLASSNAME
            boolean r2 = r13.isVideo
            if (r2 != 0) goto L_0x0c9d
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
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r40)
            r5.<init>(r14, r2)
            org.telegram.messenger.SharedConfig.saveConfig()
            java.lang.String r2 = r5.getAbsolutePath()
            r33 = r2
        L_0x0c9d:
            java.util.HashMap r14 = new java.util.HashMap
            r14.<init>()
            if (r1 == 0) goto L_0x0ca7
            r14.put(r9, r1)
        L_0x0ca7:
            if (r7 == 0) goto L_0x0cae
            java.lang.String r1 = "parentObject"
            r14.put(r1, r7)
        L_0x0cae:
            if (r16 != 0) goto L_0x0cd6
            if (r72 == 0) goto L_0x0cd6
            int r1 = r18 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r10)
            r9 = r65
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r14.put(r12, r2)
            r2 = 10
            if (r1 == r2) goto L_0x0cd0
            int r2 = r53 + -1
            if (r11 != r2) goto L_0x0cda
        L_0x0cd0:
            r14.put(r15, r8)
            r27 = r23
            goto L_0x0cda
        L_0x0cd6:
            r9 = r65
            r1 = r18
        L_0x0cda:
            if (r52 != 0) goto L_0x0d2f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r13.masks
            if (r2 == 0) goto L_0x0d2f
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0d2f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r6.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers
            r5.<init>()
            r2.add(r5)
            org.telegram.tgnet.SerializedData r2 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r13.masks
            int r5 = r5.size()
            int r5 = r5 * 20
            int r5 = r5 + 4
            r2.<init>((int) r5)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r13.masks
            int r5 = r5.size()
            r2.writeInt32(r5)
            r8 = 0
        L_0x0d09:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r13.masks
            int r5 = r5.size()
            if (r8 >= r5) goto L_0x0d1f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r13.masks
            java.lang.Object r5 = r5.get(r8)
            org.telegram.tgnet.TLRPC$InputDocument r5 = (org.telegram.tgnet.TLRPC$InputDocument) r5
            r5.serializeToStream(r2)
            int r8 = r8 + 1
            goto L_0x0d09
        L_0x0d1f:
            byte[] r5 = r2.toByteArray()
            java.lang.String r5 = org.telegram.messenger.Utilities.bytesToHex(r5)
            java.lang.String r8 = "masks"
            r14.put(r8, r5)
            r2.cleanup()
        L_0x0d2f:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda5 r25 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda5
            r2 = r25
            r5 = r74
            r8 = r6
            r34 = r52
            r6 = r73
            r35 = r7
            r7 = r0
            r36 = r9
            r9 = r33
            r10 = r14
            r33 = r11
            r11 = r35
            r15 = r13
            r12 = r69
            r35 = 0
            r14 = r75
            r0 = r1
            r17 = r15
            r1 = r46
            r38 = r49
            r15 = r76
            r16 = r17
            r17 = r77
            r18 = r78
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r25)
            r18 = r0
            r5 = r1
            r3 = r43
            r4 = r44
            r2 = r45
            goto L_0x0dc0
        L_0x0d6c:
            r33 = r11
            r17 = r13
            r1 = r15
            r38 = r60
            r34 = r63
            r36 = r65
            r35 = 0
            if (r1 != 0) goto L_0x0d9a
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r26 = new java.util.ArrayList
            r26.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r5 = r0
            r8 = r17
            r0 = r26
            goto L_0x0da5
        L_0x0d9a:
            r5 = r1
            r8 = r17
            r0 = r26
            r3 = r43
            r4 = r44
            r2 = r45
        L_0x0da5:
            java.lang.String r1 = r8.path
            r5.add(r1)
            java.lang.String r1 = r8.path
            r4.add(r1)
            android.net.Uri r1 = r8.uri
            r3.add(r1)
            java.lang.String r1 = r8.caption
            r2.add(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r8.entities
            r0.add(r1)
            r26 = r0
        L_0x0dc0:
            r0 = r18
        L_0x0dc2:
            int r6 = r33 + 1
            r1 = r68
            r15 = r73
            r25 = r29
            r11 = r31
            r10 = r32
            r13 = r34
            r23 = r36
            r9 = r38
            r14 = r53
            r12 = 1
            goto L_0x0192
        L_0x0dd9:
            r45 = r2
            r43 = r3
            r44 = r4
            r1 = r5
            r34 = r13
            r53 = r14
            r7 = r27
            r23 = 0
            int r2 = (r7 > r23 ? 1 : (r7 == r23 ? 0 : -1))
            r15 = r73
            r14 = r78
            if (r2 == 0) goto L_0x0df3
            finishGroup(r15, r7, r14)
        L_0x0df3:
            if (r79 == 0) goto L_0x0df8
            r79.releasePermission()
        L_0x0df8:
            if (r1 == 0) goto L_0x0eae
            boolean r2 = r1.isEmpty()
            if (r2 != 0) goto L_0x0eae
            r2 = 1
            long[] r13 = new long[r2]
            int r12 = r1.size()
            r8 = r0
            r0 = 0
        L_0x0e09:
            if (r0 >= r12) goto L_0x0eae
            if (r71 == 0) goto L_0x0e23
            if (r34 != 0) goto L_0x0e23
            r3 = r53
            if (r3 <= r2) goto L_0x0e25
            int r2 = r8 % 10
            if (r2 != 0) goto L_0x0e25
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random
            long r4 = r2.nextLong()
            r2 = 0
            r13[r2] = r4
            r4 = 1
            r8 = 0
            goto L_0x0e27
        L_0x0e23:
            r3 = r53
        L_0x0e25:
            r2 = 0
            r4 = 1
        L_0x0e27:
            int r11 = r8 + 1
            java.lang.Object r5 = r1.get(r0)
            java.lang.String r5 = (java.lang.String) r5
            r10 = r44
            java.lang.Object r6 = r10.get(r0)
            java.lang.String r6 = (java.lang.String) r6
            r9 = r43
            java.lang.Object r7 = r9.get(r0)
            android.net.Uri r7 = (android.net.Uri) r7
            r8 = r45
            java.lang.Object r16 = r8.get(r0)
            java.lang.CharSequence r16 = (java.lang.CharSequence) r16
            r15 = r26
            java.lang.Object r17 = r15.get(r0)
            java.util.ArrayList r17 = (java.util.ArrayList) r17
            r14 = 10
            if (r11 == r14) goto L_0x0e5b
            int r2 = r12 + -1
            if (r0 != r2) goto L_0x0e58
            goto L_0x0e5b
        L_0x0e58:
            r21 = 0
            goto L_0x0e5d
        L_0x0e5b:
            r21 = 1
        L_0x0e5d:
            r18 = 0
            r24 = r1
            r23 = r3
            r25 = 0
            r26 = 1
            r1 = r73
            r2 = r5
            r3 = r6
            r4 = r7
            r5 = r22
            r6 = r69
            r27 = r8
            r8 = r75
            r28 = r9
            r9 = r76
            r29 = r10
            r10 = r16
            r30 = r11
            r11 = r17
            r31 = r12
            r12 = r74
            r32 = r13
            r33 = 10
            r14 = r21
            r21 = r15
            r15 = r71
            r16 = r77
            r17 = r78
            prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            int r0 = r0 + 1
            r15 = r73
            r14 = r78
            r26 = r21
            r53 = r23
            r1 = r24
            r45 = r27
            r43 = r28
            r44 = r29
            r8 = r30
            r12 = r31
            r2 = 1
            goto L_0x0e09
        L_0x0eae:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0ecc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "total send time = "
            r0.append(r1)
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r19
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0ecc:
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
            SendMessagesHelper$$ExternalSyntheticLambda72 sendMessagesHelper$$ExternalSyntheticLambda72 = r0;
            SendMessagesHelper$$ExternalSyntheticLambda72 sendMessagesHelper$$ExternalSyntheticLambda722 = new SendMessagesHelper$$ExternalSyntheticLambda72(videoEditedInfo, str, j, i, accountInstance, charSequence, messageObject3, messageObject, messageObject2, arrayList, z, i2, z2);
            new Thread(sendMessagesHelper$$ExternalSyntheticLambda72).start();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x029e  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x02e8  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02fd  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0336  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x033e  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x0345  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x034c  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x024f  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x026c  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x0285  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingVideo$91(org.telegram.messenger.VideoEditedInfo r26, java.lang.String r27, long r28, int r30, org.telegram.messenger.AccountInstance r31, java.lang.CharSequence r32, org.telegram.messenger.MessageObject r33, org.telegram.messenger.MessageObject r34, org.telegram.messenger.MessageObject r35, java.util.ArrayList r36, boolean r37, int r38, boolean r39) {
        /*
            r6 = r27
            if (r26 == 0) goto L_0x0007
            r7 = r26
            goto L_0x000c
        L_0x0007:
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r27)
            r7 = r0
        L_0x000c:
            boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r28)
            r9 = 0
            r10 = 1
            if (r7 == 0) goto L_0x001a
            boolean r0 = r7.roundVideo
            if (r0 == 0) goto L_0x001a
            r11 = 1
            goto L_0x001b
        L_0x001a:
            r11 = 0
        L_0x001b:
            if (r7 != 0) goto L_0x004b
            java.lang.String r0 = "mp4"
            boolean r0 = r6.endsWith(r0)
            if (r0 != 0) goto L_0x004b
            if (r11 == 0) goto L_0x0028
            goto L_0x004b
        L_0x0028:
            r3 = 0
            r4 = 0
            r12 = 0
            r13 = 0
            r17 = 0
            r0 = r31
            r1 = r27
            r2 = r27
            r5 = r28
            r7 = r34
            r8 = r35
            r9 = r32
            r10 = r36
            r11 = r33
            r14 = r39
            r15 = r37
            r16 = r38
            prepareSendingDocumentInternal(r0, r1, r2, r3, r4, r5, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
            goto L_0x0375
        L_0x004b:
            java.io.File r12 = new java.io.File
            r12.<init>(r6)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r6)
            long r1 = r12.length()
            r0.append(r1)
            java.lang.String r13 = "_"
            r0.append(r13)
            long r1 = r12.lastModified()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r14 = ""
            r1 = 0
            if (r7 == 0) goto L_0x00c4
            if (r11 != 0) goto L_0x00bd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            long r4 = r7.estimatedDuration
            r3.append(r4)
            r3.append(r13)
            long r4 = r7.startTime
            r3.append(r4)
            r3.append(r13)
            long r4 = r7.endTime
            r3.append(r4)
            boolean r0 = r7.muted
            if (r0 == 0) goto L_0x009b
            java.lang.String r0 = "_m"
            goto L_0x009c
        L_0x009b:
            r0 = r14
        L_0x009c:
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            int r3 = r7.resultWidth
            int r4 = r7.originalWidth
            if (r3 == r4) goto L_0x00bd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r13)
            int r0 = r7.resultWidth
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x00bd:
            long r3 = r7.startTime
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 < 0) goto L_0x00c4
            r1 = r3
        L_0x00c4:
            r15 = r0
            r4 = r1
            r3 = 2
            r2 = 0
            if (r8 != 0) goto L_0x0113
            if (r30 != 0) goto L_0x0113
            if (r7 == 0) goto L_0x00de
            org.telegram.messenger.MediaController$SavedFilterState r0 = r7.filterState
            if (r0 != 0) goto L_0x0113
            java.lang.String r0 = r7.paintPath
            if (r0 != 0) goto L_0x0113
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r7.mediaEntities
            if (r0 != 0) goto L_0x0113
            org.telegram.messenger.MediaController$CropState r0 = r7.cropState
            if (r0 != 0) goto L_0x0113
        L_0x00de:
            org.telegram.messenger.MessagesStorage r0 = r31.getMessagesStorage()
            if (r8 != 0) goto L_0x00e6
            r1 = 2
            goto L_0x00e7
        L_0x00e6:
            r1 = 5
        L_0x00e7:
            java.lang.Object[] r0 = r0.getSentFile(r15, r1)
            if (r0 == 0) goto L_0x0113
            r1 = r0[r9]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r1 == 0) goto L_0x0113
            r1 = r0[r9]
            r16 = r1
            org.telegram.tgnet.TLRPC$TL_document r16 = (org.telegram.tgnet.TLRPC$TL_document) r16
            r0 = r0[r10]
            r17 = r0
            java.lang.String r17 = (java.lang.String) r17
            r18 = 0
            r0 = r8
            r1 = r16
            r2 = r27
            r9 = 2
            r3 = r18
            r20 = r4
            ensureMediaThumbExists(r0, r1, r2, r3, r4)
            r2 = r16
            r5 = r17
            goto L_0x0118
        L_0x0113:
            r20 = r4
            r9 = 2
            r2 = 0
            r5 = 0
        L_0x0118:
            if (r2 != 0) goto L_0x02fd
            r0 = r20
            android.graphics.Bitmap r0 = createVideoThumbnailAtTime(r6, r0)
            if (r0 != 0) goto L_0x0126
            android.graphics.Bitmap r0 = createVideoThumbnail(r6, r10)
        L_0x0126:
            r2 = r0
            r0 = 90
            if (r8 != 0) goto L_0x0131
            if (r30 == 0) goto L_0x012e
            goto L_0x0131
        L_0x012e:
            r1 = 320(0x140, float:4.48E-43)
            goto L_0x0133
        L_0x0131:
            r1 = 90
        L_0x0133:
            float r3 = (float) r1
            if (r1 <= r0) goto L_0x0139
            r1 = 80
            goto L_0x013b
        L_0x0139:
            r1 = 55
        L_0x013b:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r1, r8)
            if (r2 == 0) goto L_0x0245
            if (r1 == 0) goto L_0x0245
            if (r11 == 0) goto L_0x0241
            r3 = 21
            if (r8 == 0) goto L_0x01e1
            android.graphics.Bitmap r2 = android.graphics.Bitmap.createScaledBitmap(r2, r0, r0, r10)
            r21 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x0156
            r22 = 0
            goto L_0x0158
        L_0x0156:
            r22 = 1
        L_0x0158:
            int r23 = r2.getWidth()
            int r24 = r2.getHeight()
            int r25 = r2.getRowBytes()
            r20 = r2
            org.telegram.messenger.Utilities.blurBitmap(r20, r21, r22, r23, r24, r25)
            r21 = 7
            if (r4 >= r3) goto L_0x0170
            r22 = 0
            goto L_0x0172
        L_0x0170:
            r22 = 1
        L_0x0172:
            int r23 = r2.getWidth()
            int r24 = r2.getHeight()
            int r25 = r2.getRowBytes()
            r20 = r2
            org.telegram.messenger.Utilities.blurBitmap(r20, r21, r22, r23, r24, r25)
            r21 = 7
            if (r4 >= r3) goto L_0x018a
            r22 = 0
            goto L_0x018c
        L_0x018a:
            r22 = 1
        L_0x018c:
            int r23 = r2.getWidth()
            int r24 = r2.getHeight()
            int r25 = r2.getRowBytes()
            r20 = r2
            org.telegram.messenger.Utilities.blurBitmap(r20, r21, r22, r23, r24, r25)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            r17 = r11
            long r10 = r4.volume_id
            r3.append(r10)
            r3.append(r13)
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            int r4 = r4.local_id
            r3.append(r4)
            java.lang.String r4 = "@%d_%d_b2"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object[] r4 = new java.lang.Object[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r9 = (float) r9
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r9 = r9 / r10
            int r9 = (int) r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r10 = 0
            r4[r10] = r9
            int r9 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r9 = (float) r9
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r9 = r9 / r10
            int r9 = (int) r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r10 = 1
            r4[r10] = r9
            java.lang.String r3 = java.lang.String.format(r3, r4)
            goto L_0x0248
        L_0x01e1:
            r17 = r11
            r21 = 3
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x01ec
            r22 = 0
            goto L_0x01ee
        L_0x01ec:
            r22 = 1
        L_0x01ee:
            int r23 = r2.getWidth()
            int r24 = r2.getHeight()
            int r25 = r2.getRowBytes()
            r20 = r2
            org.telegram.messenger.Utilities.blurBitmap(r20, r21, r22, r23, r24, r25)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            long r10 = r4.volume_id
            r3.append(r10)
            r3.append(r13)
            org.telegram.tgnet.TLRPC$FileLocation r4 = r1.location
            int r4 = r4.local_id
            r3.append(r4)
            java.lang.String r4 = "@%d_%d_b"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object[] r4 = new java.lang.Object[r9]
            int r9 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r9 = (float) r9
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r9 = r9 / r10
            int r9 = (int) r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r10 = 0
            r4[r10] = r9
            int r9 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r9 = (float) r9
            float r10 = org.telegram.messenger.AndroidUtilities.density
            float r9 = r9 / r10
            int r9 = (int) r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r10 = 1
            r4[r10] = r9
            java.lang.String r3 = java.lang.String.format(r3, r4)
            goto L_0x0248
        L_0x0241:
            r17 = r11
            r2 = 0
            goto L_0x0247
        L_0x0245:
            r17 = r11
        L_0x0247:
            r3 = 0
        L_0x0248:
            org.telegram.tgnet.TLRPC$TL_document r4 = new org.telegram.tgnet.TLRPC$TL_document
            r4.<init>()
            if (r1 == 0) goto L_0x025a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r4.thumbs
            r9.add(r1)
            int r1 = r4.flags
            r9 = 1
            r1 = r1 | r9
            r4.flags = r1
        L_0x025a:
            r1 = 0
            byte[] r9 = new byte[r1]
            r4.file_reference = r9
            java.lang.String r9 = "video/mp4"
            r4.mime_type = r9
            org.telegram.messenger.UserConfig r9 = r31.getUserConfig()
            r9.saveConfig(r1)
            if (r8 == 0) goto L_0x0285
            int r1 = org.telegram.messenger.DialogObject.getEncryptedChatId(r28)
            org.telegram.messenger.MessagesController r8 = r31.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r8.getEncryptedChat(r1)
            if (r1 != 0) goto L_0x027f
            return
        L_0x027f:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            goto L_0x028d
        L_0x0285:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r1.<init>()
            r8 = 1
            r1.supports_streaming = r8
        L_0x028d:
            r9 = r17
            r1.round_message = r9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r4.attributes
            r8.add(r1)
            if (r7 == 0) goto L_0x02e8
            boolean r8 = r7.needConvert()
            if (r8 == 0) goto L_0x02e8
            boolean r8 = r7.muted
            if (r8 == 0) goto L_0x02b8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r4.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r9 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r9.<init>()
            r8.add(r9)
            fillVideoAttribute(r6, r1, r7)
            int r8 = r1.w
            r7.originalWidth = r8
            int r8 = r1.h
            r7.originalHeight = r8
            goto L_0x02c0
        L_0x02b8:
            long r8 = r7.estimatedDuration
            r10 = 1000(0x3e8, double:4.94E-321)
            long r8 = r8 / r10
            int r9 = (int) r8
            r1.duration = r9
        L_0x02c0:
            int r8 = r7.rotationValue
            org.telegram.messenger.MediaController$CropState r9 = r7.cropState
            if (r9 == 0) goto L_0x02ce
            int r10 = r9.transformWidth
            int r11 = r9.transformHeight
            int r9 = r9.transformRotation
            int r8 = r8 + r9
            goto L_0x02d2
        L_0x02ce:
            int r10 = r7.resultWidth
            int r11 = r7.resultHeight
        L_0x02d2:
            if (r8 == r0) goto L_0x02de
            r0 = 270(0x10e, float:3.78E-43)
            if (r8 != r0) goto L_0x02d9
            goto L_0x02de
        L_0x02d9:
            r1.w = r10
            r1.h = r11
            goto L_0x02e2
        L_0x02de:
            r1.w = r11
            r1.h = r10
        L_0x02e2:
            long r0 = r7.estimatedSize
            int r1 = (int) r0
            r4.size = r1
            goto L_0x02f9
        L_0x02e8:
            boolean r0 = r12.exists()
            if (r0 == 0) goto L_0x02f5
            long r8 = r12.length()
            int r0 = (int) r8
            r4.size = r0
        L_0x02f5:
            r0 = 0
            fillVideoAttribute(r6, r1, r0)
        L_0x02f9:
            r1 = r2
            r2 = r3
            r8 = r4
            goto L_0x0301
        L_0x02fd:
            r0 = 0
            r1 = r0
            r8 = r2
            r2 = r1
        L_0x0301:
            if (r7 == 0) goto L_0x0336
            boolean r0 = r7.needConvert()
            if (r0 == 0) goto L_0x0336
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
            r9 = r0
            goto L_0x0337
        L_0x0336:
            r9 = r6
        L_0x0337:
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            if (r32 == 0) goto L_0x0343
            java.lang.String r0 = r32.toString()
            r14 = r0
        L_0x0343:
            if (r15 == 0) goto L_0x034a
            java.lang.String r0 = "originalPath"
            r10.put(r0, r15)
        L_0x034a:
            if (r5 == 0) goto L_0x0351
            java.lang.String r0 = "parentObject"
            r10.put(r0, r5)
        L_0x0351:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda4 r19 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda4
            r0 = r19
            r3 = r33
            r4 = r31
            r17 = r5
            r5 = r7
            r6 = r8
            r7 = r9
            r8 = r10
            r9 = r17
            r10 = r28
            r12 = r34
            r13 = r35
            r15 = r36
            r16 = r37
            r17 = r38
            r18 = r30
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r19)
        L_0x0375:
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
