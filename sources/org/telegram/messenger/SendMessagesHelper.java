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
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
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
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
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
import org.telegram.tgnet.TLRPC$TL_messageEntityBold;
import org.telegram.tgnet.TLRPC$TL_messageEntityCode;
import org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC$TL_messageEntityPre;
import org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC$TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaGame;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
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
import org.telegram.tgnet.TLRPC$TL_userContact_old2;
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
                    String str9 = objArr[1];
                    long longValue = objArr[2].longValue();
                    long longValue2 = objArr[3].longValue();
                    getFileLoader().checkUploadNewDataAvailable(str9, ((int) messageObject7.getDialogId()) == 0, longValue, longValue2);
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
                    String str10 = objArr[1];
                    stopVideoService(messageObject10.messageOwner.attachPath);
                    ArrayList arrayList10 = this.delayedMessages.get(str10);
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
                            this.delayedMessages.remove(str10);
                        }
                    }
                }
            } else if (i5 == NotificationCenter.httpFileDidLoad) {
                String str11 = objArr[0];
                ArrayList arrayList11 = this.delayedMessages.get(str11);
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
                                messageObject2 = (MessageObject) delayedMessage10.extraHashMap.get(str11);
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
                            File file = new File(FileLoader.getDirectory(4), Utilities.MD5(str11) + "." + ImageLoader.getHttpUrlExtension(str11, "file"));
                            DispatchQueue dispatchQueue = Utilities.globalQueue;
                            SendMessagesHelper$$ExternalSyntheticLambda24 sendMessagesHelper$$ExternalSyntheticLambda24 = r0;
                            SendMessagesHelper$$ExternalSyntheticLambda24 sendMessagesHelper$$ExternalSyntheticLambda242 = new SendMessagesHelper$$ExternalSyntheticLambda24(this, file, messageObject, delayedMessage10, str11);
                            dispatchQueue.postRunnable(sendMessagesHelper$$ExternalSyntheticLambda24);
                        } else if (c == 1) {
                            Utilities.globalQueue.postRunnable(new SendMessagesHelper$$ExternalSyntheticLambda38(this, delayedMessage10, new File(FileLoader.getDirectory(4), Utilities.MD5(str11) + ".gif"), messageObject));
                            i12++;
                            messageObject6 = null;
                        }
                        i12++;
                        messageObject6 = null;
                    }
                    this.delayedMessages.remove(str11);
                }
            } else if (i5 == NotificationCenter.fileLoaded) {
                String str12 = objArr[0];
                ArrayList arrayList12 = this.delayedMessages.get(str12);
                if (arrayList12 != null) {
                    while (i6 < arrayList12.size()) {
                        performSendDelayedMessage((DelayedMessage) arrayList12.get(i6));
                        i6++;
                    }
                    this.delayedMessages.remove(str12);
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

    public void cancelSendingMessage(ArrayList<MessageObject> arrayList) {
        boolean z;
        long j;
        ArrayList<MessageObject> arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        long j2 = 0;
        int i = 0;
        boolean z2 = false;
        int i2 = 0;
        boolean z3 = false;
        while (i < arrayList.size()) {
            MessageObject messageObject = arrayList2.get(i);
            if (messageObject.scheduled) {
                j2 = messageObject.getDialogId();
                z3 = true;
            }
            arrayList5.add(Integer.valueOf(messageObject.getId()));
            int i3 = messageObject.messageOwner.peer_id.channel_id;
            TLRPC$Message removeFromSendingMessages = removeFromSendingMessages(messageObject.getId(), messageObject.scheduled);
            if (removeFromSendingMessages != null) {
                getConnectionsManager().cancelRequest(removeFromSendingMessages.reqId, true);
            }
            for (Map.Entry next : this.delayedMessages.entrySet()) {
                ArrayList arrayList6 = (ArrayList) next.getValue();
                int i4 = 0;
                while (true) {
                    if (i4 >= arrayList6.size()) {
                        z = z2;
                        j = j2;
                        break;
                    }
                    DelayedMessage delayedMessage = (DelayedMessage) arrayList6.get(i4);
                    z = z2;
                    j = j2;
                    if (delayedMessage.type == 4) {
                        int i5 = -1;
                        MessageObject messageObject2 = null;
                        int i6 = 0;
                        while (true) {
                            if (i6 >= delayedMessage.messageObjects.size()) {
                                break;
                            }
                            messageObject2 = delayedMessage.messageObjects.get(i6);
                            if (messageObject2.getId() == messageObject.getId()) {
                                removeFromUploadingMessages(messageObject.getId(), messageObject.scheduled);
                                i5 = i6;
                                break;
                            }
                            i6++;
                        }
                        if (i5 >= 0) {
                            delayedMessage.messageObjects.remove(i5);
                            delayedMessage.messages.remove(i5);
                            delayedMessage.originalPaths.remove(i5);
                            if (!delayedMessage.parentObjects.isEmpty()) {
                                delayedMessage.parentObjects.remove(i5);
                            }
                            TLObject tLObject = delayedMessage.sendRequest;
                            if (tLObject != null) {
                                ((TLRPC$TL_messages_sendMultiMedia) tLObject).multi_media.remove(i5);
                            } else {
                                TLRPC$TL_messages_sendEncryptedMultiMedia tLRPC$TL_messages_sendEncryptedMultiMedia = (TLRPC$TL_messages_sendEncryptedMultiMedia) delayedMessage.sendEncryptedRequest;
                                tLRPC$TL_messages_sendEncryptedMultiMedia.messages.remove(i5);
                                tLRPC$TL_messages_sendEncryptedMultiMedia.files.remove(i5);
                            }
                            MediaController.getInstance().cancelVideoConvert(messageObject);
                            String str = (String) delayedMessage.extraHashMap.get(messageObject2);
                            if (str != null) {
                                arrayList3.add(str);
                            }
                            if (delayedMessage.messageObjects.isEmpty()) {
                                delayedMessage.sendDelayedRequests();
                            } else {
                                if (delayedMessage.finalGroupMessage == messageObject.getId()) {
                                    ArrayList<MessageObject> arrayList7 = delayedMessage.messageObjects;
                                    MessageObject messageObject3 = arrayList7.get(arrayList7.size() - 1);
                                    delayedMessage.finalGroupMessage = messageObject3.getId();
                                    messageObject3.messageOwner.params.put("final", "1");
                                    TLRPC$TL_messages_messages tLRPC$TL_messages_messages = new TLRPC$TL_messages_messages();
                                    tLRPC$TL_messages_messages.messages.add(messageObject3.messageOwner);
                                    getMessagesStorage().putMessages((TLRPC$messages_Messages) tLRPC$TL_messages_messages, delayedMessage.peer, -2, 0, false, z3);
                                }
                                if (!arrayList4.contains(delayedMessage)) {
                                    arrayList4.add(delayedMessage);
                                }
                            }
                        }
                    } else if (delayedMessage.obj.getId() == messageObject.getId()) {
                        removeFromUploadingMessages(messageObject.getId(), messageObject.scheduled);
                        arrayList6.remove(i4);
                        delayedMessage.sendDelayedRequests();
                        MediaController.getInstance().cancelVideoConvert(delayedMessage.obj);
                        if (arrayList6.size() == 0) {
                            arrayList3.add((String) next.getKey());
                            if (delayedMessage.sendEncryptedRequest != null) {
                                z2 = true;
                            }
                        }
                    } else {
                        i4++;
                        z2 = z;
                        j2 = j;
                    }
                }
                z2 = z;
                j2 = j;
            }
            boolean z4 = z2;
            long j3 = j2;
            i++;
            i2 = i3;
        }
        for (int i7 = 0; i7 < arrayList3.size(); i7++) {
            String str2 = (String) arrayList3.get(i7);
            if (str2.startsWith("http")) {
                ImageLoader.getInstance().cancelLoadHttpFile(str2);
            } else {
                getFileLoader().cancelFileUpload(str2, z2);
            }
            stopVideoService(str2);
            this.delayedMessages.remove(str2);
        }
        int size = arrayList4.size();
        for (int i8 = 0; i8 < size; i8++) {
            sendReadyToSendGroup((DelayedMessage) arrayList4.get(i8), false, true);
        }
        if (arrayList.size() != 1 || !arrayList2.get(0).isEditing() || arrayList2.get(0).previousMedia == null) {
            getMessagesController().deleteMessages(arrayList5, (ArrayList<Long>) null, (TLRPC$EncryptedChat) null, j2, i2, false, z3);
        } else {
            revertEditingMessageObject(arrayList2.get(0));
        }
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
            TLRPC$EncryptedChat encryptedChat = getMessagesController().getEncryptedChat(Integer.valueOf((int) (messageObject.getDialogId() >> 32)));
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
            sendScreenshotMessage(getMessagesController().getUser(Integer.valueOf((int) messageObject.getDialogId())), messageObject.getReplyMsgId(), messageObject.messageOwner);
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

    public void processForwardFromMyName(MessageObject messageObject, long j) {
        ArrayList arrayList;
        HashMap hashMap;
        MessageObject messageObject2 = messageObject;
        long j2 = j;
        if (messageObject2 != null) {
            TLRPC$Message tLRPC$Message = messageObject2.messageOwner;
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (tLRPC$MessageMedia != null && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaEmpty) && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaGame) && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice)) {
                int i = (int) j2;
                if (i != 0 || tLRPC$Message.peer_id == null || (!(tLRPC$MessageMedia.photo instanceof TLRPC$TL_photo) && !(tLRPC$MessageMedia.document instanceof TLRPC$TL_document))) {
                    hashMap = null;
                } else {
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put("parentObject", "sent_" + messageObject2.messageOwner.peer_id.channel_id + "_" + messageObject.getId());
                    hashMap = hashMap2;
                }
                TLRPC$Message tLRPC$Message2 = messageObject2.messageOwner;
                TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message2.media;
                TLRPC$Photo tLRPC$Photo = tLRPC$MessageMedia2.photo;
                if (tLRPC$Photo instanceof TLRPC$TL_photo) {
                    sendMessage((TLRPC$TL_photo) tLRPC$Photo, (String) null, j, messageObject2.replyMessageObject, (MessageObject) null, tLRPC$Message2.message, tLRPC$Message2.entities, (TLRPC$ReplyMarkup) null, hashMap, true, 0, tLRPC$MessageMedia2.ttl_seconds, messageObject);
                    return;
                }
                TLRPC$Document tLRPC$Document = tLRPC$MessageMedia2.document;
                if (tLRPC$Document instanceof TLRPC$TL_document) {
                    sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, tLRPC$Message2.attachPath, j, messageObject2.replyMessageObject, (MessageObject) null, tLRPC$Message2.message, tLRPC$Message2.entities, (TLRPC$ReplyMarkup) null, hashMap, true, 0, tLRPC$MessageMedia2.ttl_seconds, messageObject, (MessageObject.SendAnimationData) null);
                } else if ((tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaVenue) || (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaGeo)) {
                    sendMessage(tLRPC$MessageMedia2, j, messageObject2.replyMessageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (tLRPC$MessageMedia2.phone_number != null) {
                    TLRPC$TL_userContact_old2 tLRPC$TL_userContact_old2 = new TLRPC$TL_userContact_old2();
                    TLRPC$MessageMedia tLRPC$MessageMedia3 = messageObject2.messageOwner.media;
                    tLRPC$TL_userContact_old2.phone = tLRPC$MessageMedia3.phone_number;
                    tLRPC$TL_userContact_old2.first_name = tLRPC$MessageMedia3.first_name;
                    tLRPC$TL_userContact_old2.last_name = tLRPC$MessageMedia3.last_name;
                    tLRPC$TL_userContact_old2.id = tLRPC$MessageMedia3.user_id;
                    sendMessage((TLRPC$User) tLRPC$TL_userContact_old2, j, messageObject2.replyMessageObject, (MessageObject) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                } else if (i != 0) {
                    ArrayList arrayList2 = new ArrayList();
                    arrayList2.add(messageObject2);
                    sendMessage(arrayList2, j, true, true, 0);
                }
            } else if (tLRPC$Message.message != null) {
                TLRPC$WebPage tLRPC$WebPage = tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage ? tLRPC$MessageMedia.webpage : null;
                ArrayList<TLRPC$MessageEntity> arrayList3 = tLRPC$Message.entities;
                if (arrayList3 == null || arrayList3.isEmpty()) {
                    arrayList = null;
                } else {
                    ArrayList arrayList4 = new ArrayList();
                    for (int i2 = 0; i2 < messageObject2.messageOwner.entities.size(); i2++) {
                        TLRPC$MessageEntity tLRPC$MessageEntity = messageObject2.messageOwner.entities.get(i2);
                        if ((tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityBold) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityItalic) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityPre) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCode) || (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityTextUrl)) {
                            arrayList4.add(tLRPC$MessageEntity);
                        }
                    }
                    arrayList = arrayList4;
                }
                sendMessage(messageObject2.messageOwner.message, j, messageObject2.replyMessageObject, (MessageObject) null, tLRPC$WebPage, true, arrayList, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
            } else if (((int) j2) != 0) {
                ArrayList arrayList5 = new ArrayList();
                arrayList5.add(messageObject2);
                sendMessage(arrayList5, j, true, true, 0);
            }
        }
    }

    public void sendScreenshotMessage(TLRPC$User tLRPC$User, int i, TLRPC$Message tLRPC$Message) {
        if (tLRPC$User != null && i != 0 && tLRPC$User.id != getUserConfig().getClientUserId()) {
            TLRPC$TL_messages_sendScreenshotNotification tLRPC$TL_messages_sendScreenshotNotification = new TLRPC$TL_messages_sendScreenshotNotification();
            TLRPC$TL_inputPeerUser tLRPC$TL_inputPeerUser = new TLRPC$TL_inputPeerUser();
            tLRPC$TL_messages_sendScreenshotNotification.peer = tLRPC$TL_inputPeerUser;
            tLRPC$TL_inputPeerUser.access_hash = tLRPC$User.access_hash;
            tLRPC$TL_inputPeerUser.user_id = tLRPC$User.id;
            if (tLRPC$Message != null) {
                tLRPC$TL_messages_sendScreenshotNotification.reply_to_msg_id = i;
                tLRPC$TL_messages_sendScreenshotNotification.random_id = tLRPC$Message.random_id;
            } else {
                tLRPC$Message = new TLRPC$TL_messageService();
                tLRPC$Message.random_id = getNextRandomId();
                tLRPC$Message.dialog_id = (long) tLRPC$User.id;
                tLRPC$Message.unread = true;
                tLRPC$Message.out = true;
                int newMessageId = getUserConfig().getNewMessageId();
                tLRPC$Message.id = newMessageId;
                tLRPC$Message.local_id = newMessageId;
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$Message.from_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = getUserConfig().getClientUserId();
                int i2 = tLRPC$Message.flags | 256;
                tLRPC$Message.flags = i2;
                tLRPC$Message.flags = i2 | 8;
                TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = new TLRPC$TL_messageReplyHeader();
                tLRPC$Message.reply_to = tLRPC$TL_messageReplyHeader;
                tLRPC$TL_messageReplyHeader.reply_to_msg_id = i;
                TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
                tLRPC$Message.peer_id = tLRPC$TL_peerUser2;
                tLRPC$TL_peerUser2.user_id = tLRPC$User.id;
                tLRPC$Message.date = getConnectionsManager().getCurrentTime();
                tLRPC$Message.action = new TLRPC$TL_messageActionScreenshotTaken();
                getUserConfig().saveConfig(false);
            }
            tLRPC$TL_messages_sendScreenshotNotification.random_id = tLRPC$Message.random_id;
            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message, false, true);
            messageObject.messageOwner.send_state = 1;
            messageObject.wasJustSent = true;
            ArrayList arrayList = new ArrayList();
            arrayList.add(messageObject);
            getMessagesController().updateInterfaceWithMessages(tLRPC$Message.dialog_id, arrayList, false);
            getNotificationCenter().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(tLRPC$Message);
            getMessagesStorage().putMessages((ArrayList<TLRPC$Message>) arrayList2, false, true, false, 0, false);
            performSendMessageRequest(tLRPC$TL_messages_sendScreenshotNotification, messageObject, (String) null, (DelayedMessage) null, (Object) null, (HashMap<String, String>) null, false);
        }
    }

    public void sendSticker(TLRPC$Document tLRPC$Document, String str, long j, MessageObject messageObject, MessageObject messageObject2, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
        TLRPC$TL_document_layer82 tLRPC$TL_document_layer82;
        HashMap hashMap;
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        long j2 = j;
        if (tLRPC$Document2 != null) {
            if (((int) j2) == 0) {
                if (getMessagesController().getEncryptedChat(Integer.valueOf((int) (j2 >> 32))) != null) {
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
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
        }
        sendMessage((TLRPC$TL_document) tLRPC$Document, (VideoEditedInfo) null, (String) null, j, messageObject, messageObject2, (String) null, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, z, i, 0, obj, sendAnimationData);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:265:0x067a, code lost:
        if (r9.get(r15 + 1).getDialogId() != r14.getDialogId()) goto L_0x069d;
     */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0330  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x04b4  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x04be  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x04d9  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x0521  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0531  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x054a  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0573  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0575  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x05ca  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x05d1  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x05dc  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x05f3  */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x05f5  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x060a  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x060c  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x0614  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x064c  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x065f  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x069b  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x06b0  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x06b3  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x06c0  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x06c2  */
    /* JADX WARNING: Removed duplicated region for block: B:279:0x06e6  */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x070c  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x071c  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0745  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x076b  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x076d  */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x077a  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x077d  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x07cf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendMessage(java.util.ArrayList<org.telegram.messenger.MessageObject> r48, long r49, boolean r51, boolean r52, int r53) {
        /*
            r47 = this;
            r14 = r47
            r15 = r48
            r12 = r49
            r11 = r51
            r10 = r53
            r9 = 0
            if (r15 == 0) goto L_0x0892
            boolean r0 = r48.isEmpty()
            if (r0 == 0) goto L_0x0015
            goto L_0x0892
        L_0x0015:
            int r0 = (int) r12
            org.telegram.messenger.UserConfig r1 = r47.getUserConfig()
            int r8 = r1.getClientUserId()
            if (r0 == 0) goto L_0x0873
            org.telegram.messenger.MessagesController r1 = r47.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r7 = r1.getPeer(r0)
            r5 = 1
            if (r0 <= 0) goto L_0x004a
            org.telegram.messenger.MessagesController r1 = r47.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            if (r1 != 0) goto L_0x003a
            return r9
        L_0x003a:
            r4 = 0
            r6 = 0
            r9 = 0
            r18 = 0
            r19 = 0
            r20 = 1
            r21 = 1
            r22 = 1
            r23 = 1
            goto L_0x00aa
        L_0x004a:
            org.telegram.messenger.MessagesController r1 = r47.getMessagesController()
            int r2 = -r0
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0077
            boolean r2 = r1.signatures
            boolean r3 = r1.megagroup
            r3 = r3 ^ r5
            if (r3 == 0) goto L_0x0079
            boolean r4 = r1.has_link
            if (r4 == 0) goto L_0x0079
            org.telegram.messenger.MessagesController r4 = r47.getMessagesController()
            int r9 = r1.id
            org.telegram.tgnet.TLRPC$ChatFull r4 = r4.getChatFull(r9)
            if (r4 == 0) goto L_0x0079
            int r4 = r4.linked_chat_id
            goto L_0x007a
        L_0x0077:
            r2 = 0
            r3 = 0
        L_0x0079:
            r4 = 0
        L_0x007a:
            if (r1 == 0) goto L_0x0087
            org.telegram.messenger.MessagesController r9 = r47.getMessagesController()
            int r6 = r1.id
            java.lang.String r6 = r9.getAdminRank(r6, r8)
            goto L_0x0088
        L_0x0087:
            r6 = 0
        L_0x0088:
            boolean r9 = org.telegram.messenger.ChatObject.canSendStickers(r1)
            boolean r18 = org.telegram.messenger.ChatObject.canSendMedia(r1)
            boolean r19 = org.telegram.messenger.ChatObject.canSendEmbed(r1)
            boolean r20 = org.telegram.messenger.ChatObject.canSendPolls(r1)
            r21 = r18
            r22 = r19
            r23 = r20
            r18 = r2
            r19 = r3
            r20 = r9
            r9 = r1
            r46 = r6
            r6 = r4
            r4 = r46
        L_0x00aa:
            android.util.LongSparseArray r2 = new android.util.LongSparseArray
            r2.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r24 = new java.util.ArrayList
            r24.<init>()
            java.util.ArrayList r25 = new java.util.ArrayList
            r25.<init>()
            android.util.LongSparseArray r26 = new android.util.LongSparseArray
            r26.<init>()
            org.telegram.messenger.MessagesController r5 = r47.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r5 = r5.getInputPeer((int) r0)
            r28 = r1
            long r0 = (long) r8
            int r29 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r29 != 0) goto L_0x00da
            r29 = 1
            goto L_0x00dc
        L_0x00da:
            r29 = 0
        L_0x00dc:
            r30 = r3
            r3 = r24
            r24 = r28
            r10 = 0
            r28 = r25
            r25 = r5
            r5 = r26
            r26 = 0
        L_0x00eb:
            int r14 = r48.size()
            if (r10 >= r14) goto L_0x086e
            java.lang.Object r14 = r15.get(r10)
            org.telegram.messenger.MessageObject r14 = (org.telegram.messenger.MessageObject) r14
            int r31 = r14.getId()
            if (r31 <= 0) goto L_0x07f0
            boolean r31 = r14.needDrawBluredPreview()
            if (r31 == 0) goto L_0x0105
            goto L_0x07f0
        L_0x0105:
            r31 = r10
            if (r20 != 0) goto L_0x0172
            boolean r33 = r14.isSticker()
            if (r33 != 0) goto L_0x0121
            boolean r33 = r14.isAnimatedSticker()
            if (r33 != 0) goto L_0x0121
            boolean r33 = r14.isGif()
            if (r33 != 0) goto L_0x0121
            boolean r33 = r14.isGame()
            if (r33 == 0) goto L_0x0172
        L_0x0121:
            if (r26 != 0) goto L_0x014e
            r14 = 8
            boolean r14 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r9, r14)
            if (r14 == 0) goto L_0x012d
            r10 = 4
            goto L_0x012e
        L_0x012d:
            r10 = 1
        L_0x012e:
            r36 = r4
            r17 = r6
            r38 = r8
            r37 = r9
            r26 = r10
            r16 = r25
            r39 = r28
            r15 = r31
            r25 = 0
            r27 = 0
            r41 = 1
            r31 = r0
            r28 = r2
            r1 = r30
            r30 = r7
            goto L_0x0850
        L_0x014e:
            r35 = r3
            r36 = r4
            r17 = r6
            r38 = r8
            r37 = r9
            r40 = r24
            r16 = r25
            r39 = r28
            r34 = r30
            r15 = r31
            r25 = 0
            r27 = 0
            r41 = 1
            r31 = r0
            r28 = r2
            r24 = r5
            r30 = r7
            goto L_0x0848
        L_0x0172:
            if (r21 != 0) goto L_0x018d
            org.telegram.tgnet.TLRPC$Message r10 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            boolean r15 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r15 != 0) goto L_0x0180
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r10 == 0) goto L_0x018d
        L_0x0180:
            if (r26 != 0) goto L_0x014e
            r10 = 7
            boolean r10 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r9, r10)
            if (r10 == 0) goto L_0x018b
            r10 = 5
            goto L_0x012e
        L_0x018b:
            r10 = 2
            goto L_0x012e
        L_0x018d:
            if (r23 != 0) goto L_0x01a5
            org.telegram.tgnet.TLRPC$Message r10 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r10 == 0) goto L_0x01a5
            if (r26 != 0) goto L_0x014e
            r10 = 10
            boolean r10 = org.telegram.messenger.ChatObject.isActionBannedByDefault(r9, r10)
            if (r10 == 0) goto L_0x01a3
            r10 = 6
            goto L_0x012e
        L_0x01a3:
            r10 = 3
            goto L_0x012e
        L_0x01a5:
            org.telegram.tgnet.TLRPC$TL_message r10 = new org.telegram.tgnet.TLRPC$TL_message
            r10.<init>()
            java.lang.String r15 = ""
            if (r11 != 0) goto L_0x033d
            long r34 = r14.getDialogId()
            int r36 = (r34 > r0 ? 1 : (r34 == r0 ? 0 : -1))
            if (r36 != 0) goto L_0x01c6
            boolean r34 = r14.isFromUser()
            if (r34 == 0) goto L_0x01c6
            org.telegram.tgnet.TLRPC$Message r11 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r11 = r11.from_id
            int r11 = r11.user_id
            if (r11 != r8) goto L_0x01c6
            r11 = 1
            goto L_0x01c7
        L_0x01c6:
            r11 = 0
        L_0x01c7:
            boolean r34 = r14.isForwarded()
            if (r34 == 0) goto L_0x025b
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r11 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r11.<init>()
            r10.fwd_from = r11
            r34 = r5
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r5 = r5.fwd_from
            r35 = r3
            int r3 = r5.flags
            r27 = 1
            r3 = r3 & 1
            if (r3 == 0) goto L_0x01ee
            int r3 = r11.flags
            r3 = r3 | 1
            r11.flags = r3
            org.telegram.tgnet.TLRPC$Peer r3 = r5.from_id
            r11.from_id = r3
        L_0x01ee:
            int r3 = r5.flags
            r3 = r3 & 32
            if (r3 == 0) goto L_0x01fe
            int r3 = r11.flags
            r3 = r3 | 32
            r11.flags = r3
            java.lang.String r3 = r5.from_name
            r11.from_name = r3
        L_0x01fe:
            int r3 = r5.flags
            r33 = 4
            r3 = r3 & 4
            if (r3 == 0) goto L_0x0210
            int r3 = r11.flags
            r3 = r3 | 4
            r11.flags = r3
            int r3 = r5.channel_post
            r11.channel_post = r3
        L_0x0210:
            int r3 = r5.flags
            r32 = 8
            r3 = r3 & 8
            if (r3 == 0) goto L_0x0222
            int r3 = r11.flags
            r3 = r3 | 8
            r11.flags = r3
            java.lang.String r3 = r5.post_author
            r11.post_author = r3
        L_0x0222:
            int r3 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r3 == 0) goto L_0x0228
            if (r19 == 0) goto L_0x024c
        L_0x0228:
            int r3 = r5.flags
            r3 = r3 & 16
            if (r3 == 0) goto L_0x024c
            long r36 = r14.getDialogId()
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((long) r36)
            if (r3 != 0) goto L_0x024c
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r10.fwd_from
            int r5 = r3.flags
            r5 = r5 | 16
            r3.flags = r5
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r5 = r5.fwd_from
            org.telegram.tgnet.TLRPC$Peer r11 = r5.saved_from_peer
            r3.saved_from_peer = r11
            int r5 = r5.saved_from_msg_id
            r3.saved_from_msg_id = r5
        L_0x024c:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r10.fwd_from
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r5 = r5.fwd_from
            int r5 = r5.date
            r3.date = r5
            r3 = 4
            r10.flags = r3
            goto L_0x030a
        L_0x025b:
            r35 = r3
            r34 = r5
            r3 = 4
            if (r11 != 0) goto L_0x030a
            int r5 = r14.getFromChatId()
            org.telegram.tgnet.TLRPC$TL_messageFwdHeader r11 = new org.telegram.tgnet.TLRPC$TL_messageFwdHeader
            r11.<init>()
            r10.fwd_from = r11
            int r3 = r14.getId()
            r11.channel_post = r3
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r10.fwd_from
            int r11 = r3.flags
            r33 = 4
            r11 = r11 | 4
            r3.flags = r11
            boolean r3 = r14.isFromUser()
            if (r3 == 0) goto L_0x029a
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r10.fwd_from
            org.telegram.tgnet.TLRPC$Message r11 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r11 = r11.from_id
            r3.from_id = r11
            int r11 = r3.flags
            r27 = 1
            r11 = r11 | 1
            r3.flags = r11
            r36 = r4
            r38 = r7
            r37 = r9
            goto L_0x02c8
        L_0x029a:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r10.fwd_from
            org.telegram.tgnet.TLRPC$TL_peerChannel r11 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r11.<init>()
            r3.from_id = r11
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r10.fwd_from
            org.telegram.tgnet.TLRPC$Peer r11 = r3.from_id
            r36 = r4
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner
            r37 = r9
            org.telegram.tgnet.TLRPC$Peer r9 = r4.peer_id
            r38 = r7
            int r7 = r9.channel_id
            r11.channel_id = r7
            int r7 = r3.flags
            r11 = 1
            r7 = r7 | r11
            r3.flags = r7
            boolean r7 = r4.post
            if (r7 == 0) goto L_0x02c8
            if (r5 <= 0) goto L_0x02c8
            org.telegram.tgnet.TLRPC$Peer r4 = r4.from_id
            if (r4 == 0) goto L_0x02c6
            r9 = r4
        L_0x02c6:
            r3.from_id = r9
        L_0x02c8:
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            java.lang.String r3 = r3.post_author
            if (r3 == 0) goto L_0x02cf
            goto L_0x0300
        L_0x02cf:
            boolean r3 = r14.isOutOwner()
            if (r3 != 0) goto L_0x0300
            if (r5 <= 0) goto L_0x0300
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            boolean r3 = r3.post
            if (r3 == 0) goto L_0x0300
            org.telegram.messenger.MessagesController r3 = r47.getMessagesController()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0300
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r10.fwd_from
            java.lang.String r5 = r3.first_name
            java.lang.String r3 = r3.last_name
            java.lang.String r3 = org.telegram.messenger.ContactsController.formatName(r5, r3)
            r4.post_author = r3
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r10.fwd_from
            int r4 = r3.flags
            r5 = 8
            r4 = r4 | r5
            r3.flags = r4
        L_0x0300:
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            int r3 = r3.date
            r10.date = r3
            r3 = 4
            r10.flags = r3
            goto L_0x0310
        L_0x030a:
            r36 = r4
            r38 = r7
            r37 = r9
        L_0x0310:
            int r3 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r3 != 0) goto L_0x033a
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r10.fwd_from
            if (r3 == 0) goto L_0x033a
            int r4 = r3.flags
            r4 = r4 | 16
            r3.flags = r4
            int r4 = r14.getId()
            r3.saved_from_msg_id = r4
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r10.fwd_from
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            r3.saved_from_peer = r4
            int r3 = r4.user_id
            if (r3 != r8) goto L_0x033a
            r32 = r0
            long r0 = r14.getDialogId()
            int r1 = (int) r0
            r4.user_id = r1
            goto L_0x0382
        L_0x033a:
            r32 = r0
            goto L_0x0382
        L_0x033d:
            r32 = r0
            r35 = r3
            r36 = r4
            r34 = r5
            r38 = r7
            r37 = r9
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r10.params = r0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r15)
            int r3 = r14.getId()
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            java.lang.String r3 = "fwd_id"
            r0.put(r3, r1)
            java.util.HashMap<java.lang.String, java.lang.String> r0 = r10.params
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r15)
            long r3 = r14.getDialogId()
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            java.lang.String r3 = "fwd_peer"
            r0.put(r3, r1)
        L_0x0382:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0399
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r0.restriction_reason
            r10.restriction_reason = r0
            int r0 = r10.flags
            r1 = 4194304(0x400000, float:5.877472E-39)
            r0 = r0 | r1
            r10.flags = r0
        L_0x0399:
            if (r22 != 0) goto L_0x03ab
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r0 == 0) goto L_0x03ab
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r0 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r0.<init>()
            r10.media = r0
            goto L_0x03b1
        L_0x03ab:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            r10.media = r0
        L_0x03b1:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r10.media
            if (r0 == 0) goto L_0x03bb
            int r0 = r10.flags
            r0 = r0 | 512(0x200, float:7.175E-43)
            r10.flags = r0
        L_0x03bb:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            int r0 = r0.via_bot_id
            if (r0 == 0) goto L_0x03c9
            r10.via_bot_id = r0
            int r0 = r10.flags
            r0 = r0 | 2048(0x800, float:2.87E-42)
            r10.flags = r0
        L_0x03c9:
            if (r6 == 0) goto L_0x03e3
            org.telegram.tgnet.TLRPC$TL_messageReplies r0 = new org.telegram.tgnet.TLRPC$TL_messageReplies
            r0.<init>()
            r10.replies = r0
            r1 = 1
            r0.comments = r1
            r0.channel_id = r6
            int r3 = r0.flags
            r3 = r3 | r1
            r0.flags = r3
            int r0 = r10.flags
            r1 = 8388608(0x800000, float:1.17549435E-38)
            r0 = r0 | r1
            r10.flags = r0
        L_0x03e3:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            java.lang.String r0 = r0.message
            r10.message = r0
            if (r0 != 0) goto L_0x03ed
            r10.message = r15
        L_0x03ed:
            int r0 = r14.getId()
            r10.fwd_msg_id = r0
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            java.lang.String r1 = r0.attachPath
            r10.attachPath = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r0.entities
            r10.entities = r1
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            if (r0 == 0) goto L_0x04ab
            org.telegram.tgnet.TLRPC$TL_replyInlineMarkup r0 = new org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
            r0.<init>()
            r10.reply_markup = r0
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r0 = r0.size()
            r1 = 0
            r3 = 0
        L_0x0416:
            if (r1 >= r0) goto L_0x0494
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r4 = r4.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r4 = r4.rows
            java.lang.Object r4 = r4.get(r1)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r4 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r5 = r4.buttons
            int r5 = r5.size()
            r7 = 0
            r9 = 0
        L_0x042c:
            if (r7 >= r5) goto L_0x0488
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r11 = r4.buttons
            java.lang.Object r11 = r11.get(r7)
            org.telegram.tgnet.TLRPC$KeyboardButton r11 = (org.telegram.tgnet.TLRPC$KeyboardButton) r11
            r39 = r0
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r40 = r3
            if (r0 != 0) goto L_0x044d
            boolean r3 = r11 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrl
            if (r3 != 0) goto L_0x044d
            boolean r3 = r11 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonSwitchInline
            if (r3 != 0) goto L_0x044d
            boolean r3 = r11 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r3 == 0) goto L_0x044b
            goto L_0x044d
        L_0x044b:
            r3 = 1
            goto L_0x048c
        L_0x044d:
            if (r0 == 0) goto L_0x046e
            org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth r0 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            r0.<init>()
            int r3 = r11.flags
            r0.flags = r3
            java.lang.String r3 = r11.fwd_text
            if (r3 == 0) goto L_0x0461
            r0.fwd_text = r3
            r0.text = r3
            goto L_0x0465
        L_0x0461:
            java.lang.String r3 = r11.text
            r0.text = r3
        L_0x0465:
            java.lang.String r3 = r11.url
            r0.url = r3
            int r3 = r11.button_id
            r0.button_id = r3
            r11 = r0
        L_0x046e:
            if (r9 != 0) goto L_0x047c
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r9 = new org.telegram.tgnet.TLRPC$TL_keyboardButtonRow
            r9.<init>()
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r10.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            r0.add(r9)
        L_0x047c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r0 = r9.buttons
            r0.add(r11)
            int r7 = r7 + 1
            r0 = r39
            r3 = r40
            goto L_0x042c
        L_0x0488:
            r39 = r0
            r40 = r3
        L_0x048c:
            if (r3 == 0) goto L_0x048f
            goto L_0x0496
        L_0x048f:
            int r1 = r1 + 1
            r0 = r39
            goto L_0x0416
        L_0x0494:
            r40 = r3
        L_0x0496:
            if (r3 != 0) goto L_0x049f
            int r0 = r10.flags
            r0 = r0 | 64
            r10.flags = r0
            goto L_0x04ab
        L_0x049f:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            r7 = 0
            r0.reply_markup = r7
            int r0 = r10.flags
            r0 = r0 & -65
            r10.flags = r0
            goto L_0x04ac
        L_0x04ab:
            r7 = 0
        L_0x04ac:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r10.entities
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x04ba
            int r0 = r10.flags
            r0 = r0 | 128(0x80, float:1.794E-43)
            r10.flags = r0
        L_0x04ba:
            java.lang.String r0 = r10.attachPath
            if (r0 != 0) goto L_0x04c0
            r10.attachPath = r15
        L_0x04c0:
            org.telegram.messenger.UserConfig r0 = r47.getUserConfig()
            int r0 = r0.getNewMessageId()
            r10.id = r0
            r10.local_id = r0
            r0 = 1
            r10.out = r0
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            long r0 = r0.grouped_id
            r3 = 0
            int r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x04ff
            java.lang.Object r0 = r2.get(r0)
            java.lang.Long r0 = (java.lang.Long) r0
            if (r0 != 0) goto L_0x04f2
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r0 = r0.nextLong()
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Message r1 = r14.messageOwner
            long r3 = r1.grouped_id
            r2.put(r3, r0)
        L_0x04f2:
            long r0 = r0.longValue()
            r10.grouped_id = r0
            int r0 = r10.flags
            r1 = 131072(0x20000, float:1.83671E-40)
            r0 = r0 | r1
            r10.flags = r0
        L_0x04ff:
            r9 = r38
            int r0 = r9.channel_id
            if (r0 == 0) goto L_0x051b
            if (r19 == 0) goto L_0x051b
            if (r18 == 0) goto L_0x0513
            org.telegram.tgnet.TLRPC$TL_peerUser r0 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r0.<init>()
            r10.from_id = r0
            r0.user_id = r8
            goto L_0x0515
        L_0x0513:
            r10.from_id = r9
        L_0x0515:
            r0 = 1
            r10.post = r0
            r4 = r36
            goto L_0x0542
        L_0x051b:
            boolean r0 = org.telegram.messenger.ChatObject.shouldSendAnonymously(r37)
            if (r0 == 0) goto L_0x0531
            r10.from_id = r9
            r4 = r36
            if (r36 == 0) goto L_0x0542
            r10.post_author = r4
            int r0 = r10.flags
            r1 = 65536(0x10000, float:9.18355E-41)
            r0 = r0 | r1
            r10.flags = r0
            goto L_0x0542
        L_0x0531:
            r4 = r36
            org.telegram.tgnet.TLRPC$TL_peerUser r0 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r0.<init>()
            r10.from_id = r0
            r0.user_id = r8
            int r0 = r10.flags
            r0 = r0 | 256(0x100, float:3.59E-43)
            r10.flags = r0
        L_0x0542:
            long r0 = r10.random_id
            r38 = 0
            int r3 = (r0 > r38 ? 1 : (r0 == r38 ? 0 : -1))
            if (r3 != 0) goto L_0x0550
            long r0 = r47.getNextRandomId()
            r10.random_id = r0
        L_0x0550:
            long r0 = r10.random_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            r3 = r35
            r3.add(r0)
            long r0 = r10.random_id
            r5 = r34
            r5.put(r0, r10)
            int r0 = r10.fwd_msg_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r1 = r28
            r1.add(r0)
            r11 = r53
            r15 = r31
            if (r11 == 0) goto L_0x0575
            r0 = r11
            goto L_0x057d
        L_0x0575:
            org.telegram.tgnet.ConnectionsManager r0 = r47.getConnectionsManager()
            int r0 = r0.getCurrentTime()
        L_0x057d:
            r10.date = r0
            r0 = r25
            boolean r7 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r7 == 0) goto L_0x0597
            if (r19 == 0) goto L_0x0597
            r25 = r1
            if (r11 != 0) goto L_0x0594
            r1 = 1
            r10.views = r1
            int r1 = r10.flags
            r1 = r1 | 1024(0x400, float:1.435E-42)
            r10.flags = r1
        L_0x0594:
            r28 = r2
            goto L_0x05b2
        L_0x0597:
            r25 = r1
            org.telegram.tgnet.TLRPC$Message r1 = r14.messageOwner
            r28 = r2
            int r2 = r1.flags
            r2 = r2 & 1024(0x400, float:1.435E-42)
            if (r2 == 0) goto L_0x05af
            if (r11 != 0) goto L_0x05af
            int r1 = r1.views
            r10.views = r1
            int r1 = r10.flags
            r1 = r1 | 1024(0x400, float:1.435E-42)
            r10.flags = r1
        L_0x05af:
            r1 = 1
            r10.unread = r1
        L_0x05b2:
            r10.dialog_id = r12
            r10.peer_id = r9
            boolean r1 = org.telegram.messenger.MessageObject.isVoiceMessage(r10)
            if (r1 != 0) goto L_0x05c2
            boolean r1 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r10)
            if (r1 == 0) goto L_0x05d4
        L_0x05c2:
            if (r7 == 0) goto L_0x05d1
            int r1 = r14.getChannelId()
            if (r1 == 0) goto L_0x05d1
            boolean r1 = r14.isContentUnread()
            r10.media_unread = r1
            goto L_0x05d4
        L_0x05d1:
            r1 = 1
            r10.media_unread = r1
        L_0x05d4:
            org.telegram.tgnet.TLRPC$Message r1 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r2 == 0) goto L_0x05e1
            int r1 = r1.channel_id
            int r1 = -r1
            r10.ttl = r1
        L_0x05e1:
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject
            r7 = r47
            r36 = r4
            r2 = r25
            int r4 = r7.currentAccount
            r34 = r5
            r5 = 1
            r1.<init>(r4, r10, r5, r5)
            if (r11 == 0) goto L_0x05f5
            r4 = 1
            goto L_0x05f6
        L_0x05f5:
            r4 = 0
        L_0x05f6:
            r1.scheduled = r4
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            r4.send_state = r5
            r1.wasJustSent = r5
            r4 = r24
            r4.add(r1)
            r5 = r30
            r5.add(r10)
            if (r11 == 0) goto L_0x060c
            r1 = 1
            goto L_0x060d
        L_0x060c:
            r1 = 0
        L_0x060d:
            r7.putToSendingMessages(r10, r1)
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x064c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r10 = "forward message user_id = "
            r1.append(r10)
            int r10 = r0.user_id
            r1.append(r10)
            java.lang.String r10 = " chat_id = "
            r1.append(r10)
            int r10 = r0.chat_id
            r1.append(r10)
            java.lang.String r10 = " channel_id = "
            r1.append(r10)
            int r10 = r0.channel_id
            r1.append(r10)
            java.lang.String r10 = " access_hash = "
            r1.append(r10)
            r10 = r8
            r38 = r9
            long r8 = r0.access_hash
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.FileLog.d(r1)
            goto L_0x064f
        L_0x064c:
            r10 = r8
            r38 = r9
        L_0x064f:
            int r1 = r5.size()
            r8 = 100
            if (r1 == r8) goto L_0x069b
            int r1 = r48.size()
            r8 = 1
            int r1 = r1 - r8
            if (r15 == r1) goto L_0x069b
            int r1 = r48.size()
            int r1 = r1 - r8
            if (r15 == r1) goto L_0x067d
            int r1 = r15 + 1
            r9 = r48
            java.lang.Object r1 = r9.get(r1)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            long r24 = r1.getDialogId()
            long r30 = r14.getDialogId()
            int r1 = (r24 > r30 ? 1 : (r24 == r30 ? 0 : -1))
            if (r1 == 0) goto L_0x067f
            goto L_0x069d
        L_0x067d:
            r9 = r48
        L_0x067f:
            r16 = r0
            r39 = r2
            r35 = r3
            r40 = r4
            r17 = r6
            r31 = r32
            r24 = r34
            r30 = r38
            r25 = 0
            r27 = 0
            r41 = 1
            r34 = r5
            r38 = r10
            goto L_0x0848
        L_0x069b:
            r9 = r48
        L_0x069d:
            org.telegram.messenger.MessagesStorage r39 = r47.getMessagesStorage()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>(r5)
            r41 = 0
            r42 = 1
            r43 = 0
            r44 = 0
            if (r11 == 0) goto L_0x06b3
            r45 = 1
            goto L_0x06b5
        L_0x06b3:
            r45 = 0
        L_0x06b5:
            r40 = r1
            r39.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r40, (boolean) r41, (boolean) r42, (boolean) r43, (int) r44, (boolean) r45)
            org.telegram.messenger.MessagesController r1 = r47.getMessagesController()
            if (r11 == 0) goto L_0x06c2
            r8 = 1
            goto L_0x06c3
        L_0x06c2:
            r8 = 0
        L_0x06c3:
            r1.updateInterfaceWithMessages(r12, r4, r8)
            org.telegram.messenger.NotificationCenter r1 = r47.getNotificationCenter()
            int r8 = org.telegram.messenger.NotificationCenter.dialogsNeedReload
            r24 = r5
            r25 = r6
            r5 = 0
            java.lang.Object[] r6 = new java.lang.Object[r5]
            r1.postNotificationName(r8, r6)
            org.telegram.messenger.UserConfig r1 = r47.getUserConfig()
            r1.saveConfig(r5)
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r8 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages
            r8.<init>()
            r8.to_peer = r0
            if (r52 == 0) goto L_0x0707
            int r1 = r7.currentAccount
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getNotificationsSettings(r1)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "silent_"
            r5.append(r6)
            r5.append(r12)
            java.lang.String r5 = r5.toString()
            r6 = 0
            boolean r1 = r1.getBoolean(r5, r6)
            if (r1 == 0) goto L_0x0705
            goto L_0x0707
        L_0x0705:
            r1 = 0
            goto L_0x0708
        L_0x0707:
            r1 = 1
        L_0x0708:
            r8.silent = r1
            if (r11 == 0) goto L_0x0714
            r8.schedule_date = r11
            int r1 = r8.flags
            r1 = r1 | 1024(0x400, float:1.435E-42)
            r8.flags = r1
        L_0x0714:
            org.telegram.tgnet.TLRPC$Message r1 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r1 == 0) goto L_0x0745
            org.telegram.messenger.MessagesController r1 = r47.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            int r5 = r5.channel_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r5)
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r5 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            r5.<init>()
            r8.from_peer = r5
            org.telegram.tgnet.TLRPC$Message r6 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            int r6 = r6.channel_id
            r5.channel_id = r6
            r6 = r0
            if (r1 == 0) goto L_0x074d
            long r0 = r1.access_hash
            r5.access_hash = r0
            goto L_0x074d
        L_0x0745:
            r6 = r0
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r0.<init>()
            r8.from_peer = r0
        L_0x074d:
            r8.random_id = r3
            r8.id = r2
            r5 = r51
            r8.drop_author = r5
            int r0 = r48.size()
            r1 = 1
            if (r0 != r1) goto L_0x076d
            r0 = 0
            java.lang.Object r16 = r9.get(r0)
            r0 = r16
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0.with_my_score
            if (r0 == 0) goto L_0x076d
            r0 = 1
            goto L_0x076e
        L_0x076d:
            r0 = 0
        L_0x076e:
            r8.with_my_score = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>(r4)
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r11 != r1) goto L_0x077d
            r30 = 1
            goto L_0x077f
        L_0x077d:
            r30 = 0
        L_0x077f:
            org.telegram.tgnet.ConnectionsManager r1 = r47.getConnectionsManager()
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda80 r13 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda80
            r12 = r0
            r31 = r32
            r27 = 0
            r0 = r13
            r33 = r15
            r16 = 1
            r15 = r1
            r1 = r47
            r39 = r2
            r35 = r3
            r40 = r4
            r2 = r49
            r4 = r53
            r16 = r6
            r41 = 1
            r46 = r34
            r34 = r24
            r24 = r46
            r5 = r30
            r17 = r25
            r25 = 0
            r6 = r29
            r30 = r38
            r7 = r24
            r38 = r10
            r10 = r8
            r8 = r34
            r9 = r12
            r12 = r10
            r10 = r14
            r11 = r30
            r14 = r12
            r0.<init>(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r0 = 68
            r15.sendRequest(r14, r13, r0)
            int r0 = r48.size()
            int r0 = r0 + -1
            r15 = r33
            if (r15 == r0) goto L_0x0848
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            android.util.LongSparseArray r4 = new android.util.LongSparseArray
            r4.<init>()
            r24 = r0
            r39 = r3
            r5 = r4
            r3 = r2
            goto L_0x0850
        L_0x07f0:
            r31 = r0
            r35 = r3
            r36 = r4
            r17 = r6
            r38 = r8
            r37 = r9
            r15 = r10
            r40 = r24
            r16 = r25
            r39 = r28
            r34 = r30
            r25 = 0
            r27 = 0
            r41 = 1
            r28 = r2
            r24 = r5
            r30 = r7
            int r0 = r14.type
            if (r0 != 0) goto L_0x0848
            java.lang.CharSequence r0 = r14.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0848
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0827
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            r6 = r0
            goto L_0x0829
        L_0x0827:
            r6 = r25
        L_0x0829:
            java.lang.CharSequence r0 = r14.messageText
            java.lang.String r1 = r0.toString()
            r4 = 0
            r5 = 0
            if (r6 == 0) goto L_0x0835
            r7 = 1
            goto L_0x0836
        L_0x0835:
            r7 = 0
        L_0x0836:
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r0.entities
            r9 = 0
            r10 = 0
            r13 = 0
            r0 = r47
            r2 = r49
            r11 = r52
            r12 = r53
            r0.sendMessage(r1, r2, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
        L_0x0848:
            r5 = r24
            r1 = r34
            r3 = r35
            r24 = r40
        L_0x0850:
            int r10 = r15 + 1
            r15 = r48
            r12 = r49
            r11 = r51
            r25 = r16
            r6 = r17
            r2 = r28
            r7 = r30
            r4 = r36
            r9 = r37
            r8 = r38
            r28 = r39
            r30 = r1
            r0 = r31
            goto L_0x00eb
        L_0x086e:
            r2 = r47
            r9 = r26
            goto L_0x0891
        L_0x0873:
            r27 = 0
            r9 = 0
        L_0x0876:
            int r0 = r48.size()
            if (r9 >= r0) goto L_0x088e
            r0 = r48
            java.lang.Object r1 = r0.get(r9)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            r2 = r47
            r3 = r49
            r2.processForwardFromMyName(r1, r3)
            int r9 = r9 + 1
            goto L_0x0876
        L_0x088e:
            r2 = r47
            r9 = 0
        L_0x0891:
            return r9
        L_0x0892:
            r2 = r14
            r27 = 0
            return r27
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.sendMessage(java.util.ArrayList, long, boolean, boolean, int):int");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00df  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0120  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x01bb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$sendMessage$14(long r27, int r29, boolean r30, boolean r31, android.util.LongSparseArray r32, java.util.ArrayList r33, java.util.ArrayList r34, org.telegram.messenger.MessageObject r35, org.telegram.tgnet.TLRPC$Peer r36, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r37, org.telegram.tgnet.TLObject r38, org.telegram.tgnet.TLRPC$TL_error r39) {
        /*
            r26 = this;
            r11 = r26
            r12 = r29
            r13 = r33
            r14 = r34
            r0 = r39
            r15 = 1
            if (r0 != 0) goto L_0x01ef
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
            if (r0 >= r1) goto L_0x01d1
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
            goto L_0x01c2
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
            if (r2 == 0) goto L_0x00b5
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewMessage) r1
            org.telegram.tgnet.TLRPC$Message r2 = r1.message
            org.telegram.messenger.MessagesController r4 = r26.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            r4.processNewDifferenceParams(r0, r10, r0, r1)
        L_0x00b3:
            r10 = r2
            goto L_0x00d3
        L_0x00b5:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x00bf
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage) r1
            org.telegram.tgnet.TLRPC$Message r1 = r1.message
            r10 = r1
            goto L_0x00d3
        L_0x00bf:
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r1 = (org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage) r1
            org.telegram.tgnet.TLRPC$Message r2 = r1.message
            org.telegram.messenger.MessagesController r4 = r26.getMessagesController()
            int r10 = r1.pts
            int r1 = r1.pts_count
            org.telegram.tgnet.TLRPC$Peer r0 = r2.peer_id
            int r0 = r0.channel_id
            r4.processNewChannelDifferenceParams(r10, r1, r0)
            goto L_0x00b3
        L_0x00d3:
            if (r30 == 0) goto L_0x00df
            int r0 = r10.date
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 == r1) goto L_0x00df
            r19 = 0
            goto L_0x00e1
        L_0x00df:
            r19 = r3
        L_0x00e1:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r10)
            if (r19 != 0) goto L_0x00f3
            int r0 = r16.intValue()
            int r1 = r10.id
            if (r0 >= r1) goto L_0x00f0
            r0 = 1
            goto L_0x00f1
        L_0x00f0:
            r0 = 0
        L_0x00f1:
            r10.unread = r0
        L_0x00f3:
            if (r31 == 0) goto L_0x00fd
            r10.out = r15
            r4 = 0
            r10.unread = r4
            r10.media_unread = r4
            goto L_0x00fe
        L_0x00fd:
            r4 = 0
        L_0x00fe:
            int r0 = r10.id
            long r0 = r9.get(r0)
            r2 = 0
            int r18 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r18 == 0) goto L_0x01bb
            r3 = r32
            java.lang.Object r0 = r3.get(r0)
            r2 = r0
            org.telegram.tgnet.TLRPC$Message r2 = (org.telegram.tgnet.TLRPC$Message) r2
            if (r2 != 0) goto L_0x0117
            goto L_0x01bb
        L_0x0117:
            int r0 = r13.indexOf(r2)
            r1 = -1
            if (r0 != r1) goto L_0x0120
            goto L_0x01bb
        L_0x0120:
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
            if (r0 == 0) goto L_0x0150
            int r0 = r10.ttl_period
            r4.ttl_period = r0
            int r0 = r4.flags
            r0 = r0 | r20
            r4.flags = r0
        L_0x0150:
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
            if (r12 == 0) goto L_0x0190
            if (r19 != 0) goto L_0x0190
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
            goto L_0x01b6
        L_0x0190:
            org.telegram.messenger.MessagesStorage r0 = r26.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r8 = r0.getStorageQueue()
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda48 r6 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda48
            r0 = r6
            r1 = r26
            r2 = r3
            r3 = r22
            r4 = r36
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
        L_0x01b6:
            r0 = r17
            r8 = r21
            goto L_0x01c1
        L_0x01bb:
            r15 = r7
            r19 = r9
            r13 = 0
            r0 = r17
        L_0x01c1:
            r1 = 1
        L_0x01c2:
            int r0 = r0 + r1
            r5 = r27
            r12 = r29
            r13 = r33
            r14 = r34
            r7 = r15
            r9 = r19
            r15 = 1
            goto L_0x0071
        L_0x01d1:
            r15 = r7
            r1 = 1
            r13 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r0 = r15.updates
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x01e3
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            r0.processUpdates(r15, r13)
        L_0x01e3:
            org.telegram.messenger.StatsController r0 = r26.getStatsController()
            int r2 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r0.incrementSentItemsCount(r2, r1, r8)
            goto L_0x01fb
        L_0x01ef:
            r1 = 1
            r13 = 0
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda58 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda58
            r3 = r37
            r2.<init>(r11, r0, r3)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
        L_0x01fb:
            r10 = 0
        L_0x01fc:
            int r0 = r33.size()
            if (r10 >= r0) goto L_0x0225
            r0 = r33
            r2 = 0
            java.lang.Object r3 = r0.get(r10)
            org.telegram.tgnet.TLRPC$Message r3 = (org.telegram.tgnet.TLRPC$Message) r3
            org.telegram.messenger.MessagesStorage r4 = r26.getMessagesStorage()
            r5 = r29
            if (r5 == 0) goto L_0x0215
            r6 = 1
            goto L_0x0216
        L_0x0215:
            r6 = 0
        L_0x0216:
            r4.markMessageAsSendError(r3, r6)
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda44 r4 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda44
            r4.<init>(r11, r3, r5)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r4)
            int r10 = r10 + 1
            r13 = 0
            goto L_0x01fc
        L_0x0225:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendMessage$14(long, int, boolean, boolean, android.util.LongSparseArray, java.util.ArrayList, java.util.ArrayList, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$Peer, org.telegram.tgnet.TLRPC$TL_messages_forwardMessages, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendMessage$9(int i, TLRPC$Message tLRPC$Message, ArrayList arrayList, MessageObject messageObject, int i2) {
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(Integer.valueOf(i));
        getMessagesController().deleteMessages(arrayList2, (ArrayList<Long>) null, (TLRPC$EncryptedChat) null, tLRPC$Message.dialog_id, tLRPC$Message.peer_id.channel_id, false, true);
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
    public /* synthetic */ void lambda$sendMessage$11(TLRPC$Message tLRPC$Message, int i, TLRPC$Peer tLRPC$Peer, int i2, ArrayList arrayList, long j, TLRPC$Message tLRPC$Message2, int i3) {
        TLRPC$Message tLRPC$Message3 = tLRPC$Message;
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message3.random_id, Long.valueOf((long) i), tLRPC$Message3.id, 0, false, tLRPC$Peer.channel_id, i2 != 0 ? 1 : 0);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v20, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v22, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v15, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v13, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v27, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v23, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v15, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v16, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v35, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v39, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v41, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.util.HashMap<java.lang.String, java.lang.String>} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v9, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v43, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v44, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0259 A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x0265 A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x026d  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x027f  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x004b A[SYNTHETIC, Splitter:B:19:0x004b] */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x0482 A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x048e A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x04a0 A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x04ec A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x04f1 A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0500 A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00a6 A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0142 A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x015f A[Catch:{ Exception -> 0x0575 }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0186 A[Catch:{ Exception -> 0x0575 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void editMessage(org.telegram.messenger.MessageObject r27, org.telegram.tgnet.TLRPC$TL_photo r28, org.telegram.messenger.VideoEditedInfo r29, org.telegram.tgnet.TLRPC$TL_document r30, java.lang.String r31, java.util.HashMap<java.lang.String, java.lang.String> r32, boolean r33, java.lang.Object r34) {
        /*
            r26 = this;
            r11 = r26
            r12 = r27
            r0 = r28
            r1 = r30
            r2 = r31
            java.lang.String r3 = "originalPath"
            java.lang.String r4 = "parentObject"
            if (r12 != 0) goto L_0x0011
            return
        L_0x0011:
            if (r32 != 0) goto L_0x0019
            java.util.HashMap r5 = new java.util.HashMap
            r5.<init>()
            goto L_0x001b
        L_0x0019:
            r5 = r32
        L_0x001b:
            org.telegram.tgnet.TLRPC$Message r6 = r12.messageOwner
            r7 = 0
            r12.cancelEditing = r7
            long r8 = r27.getDialogId()     // Catch:{ Exception -> 0x0575 }
            int r10 = (int) r8     // Catch:{ Exception -> 0x0575 }
            if (r10 != 0) goto L_0x0046
            r14 = 32
            long r14 = r8 >> r14
            int r15 = (int) r14     // Catch:{ Exception -> 0x0575 }
            org.telegram.messenger.MessagesController r14 = r26.getMessagesController()     // Catch:{ Exception -> 0x0575 }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$EncryptedChat r14 = r14.getEncryptedChat(r15)     // Catch:{ Exception -> 0x0575 }
            if (r14 == 0) goto L_0x0044
            int r14 = r14.layer     // Catch:{ Exception -> 0x0575 }
            int r14 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r14)     // Catch:{ Exception -> 0x0575 }
            r15 = 101(0x65, float:1.42E-43)
            if (r14 >= r15) goto L_0x0046
        L_0x0044:
            r14 = 0
            goto L_0x0047
        L_0x0046:
            r14 = 1
        L_0x0047:
            java.lang.String r15 = "http"
            if (r33 == 0) goto L_0x00a6
            org.telegram.tgnet.TLRPC$Message r2 = r12.messageOwner     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media     // Catch:{ Exception -> 0x0575 }
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x0575 }
            if (r5 != 0) goto L_0x0081
            if (r2 == 0) goto L_0x0081
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x0575 }
            if (r5 == 0) goto L_0x005a
            goto L_0x0081
        L_0x005a:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0575 }
            if (r5 == 0) goto L_0x0066
            org.telegram.tgnet.TLRPC$Photo r0 = r2.photo     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0     // Catch:{ Exception -> 0x0575 }
            r5 = r29
            r2 = 2
            goto L_0x0084
        L_0x0066:
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0575 }
            if (r5 == 0) goto L_0x007d
            org.telegram.tgnet.TLRPC$Document r1 = r2.document     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$TL_document r1 = (org.telegram.tgnet.TLRPC$TL_document) r1     // Catch:{ Exception -> 0x0575 }
            boolean r2 = org.telegram.messenger.MessageObject.isVideoDocument(r1)     // Catch:{ Exception -> 0x0575 }
            if (r2 != 0) goto L_0x0079
            if (r29 == 0) goto L_0x0077
            goto L_0x0079
        L_0x0077:
            r2 = 7
            goto L_0x007a
        L_0x0079:
            r2 = 3
        L_0x007a:
            org.telegram.messenger.VideoEditedInfo r5 = r12.videoEditedInfo     // Catch:{ Exception -> 0x0575 }
            goto L_0x0084
        L_0x007d:
            r5 = r29
            r2 = -1
            goto L_0x0084
        L_0x0081:
            r5 = r29
            r2 = 1
        L_0x0084:
            java.util.HashMap<java.lang.String, java.lang.String> r7 = r6.params     // Catch:{ Exception -> 0x0575 }
            if (r34 != 0) goto L_0x0095
            if (r7 == 0) goto L_0x0095
            boolean r17 = r7.containsKey(r4)     // Catch:{ Exception -> 0x0575 }
            if (r17 == 0) goto L_0x0095
            java.lang.Object r4 = r7.get(r4)     // Catch:{ Exception -> 0x0575 }
            goto L_0x0097
        L_0x0095:
            r4 = r34
        L_0x0097:
            java.lang.String r13 = r6.message     // Catch:{ Exception -> 0x0575 }
            r12.editingMessage = r13     // Catch:{ Exception -> 0x0575 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r13 = r6.entities     // Catch:{ Exception -> 0x0575 }
            r12.editingMessageEntities = r13     // Catch:{ Exception -> 0x0575 }
            java.lang.String r13 = r6.attachPath     // Catch:{ Exception -> 0x0575 }
            r18 = r10
            r10 = r4
            goto L_0x015b
        L_0x00a6:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r6.media     // Catch:{ Exception -> 0x0575 }
            r12.previousMedia = r4     // Catch:{ Exception -> 0x0575 }
            java.lang.String r7 = r6.message     // Catch:{ Exception -> 0x0575 }
            r12.previousMessage = r7     // Catch:{ Exception -> 0x0575 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r7 = r6.entities     // Catch:{ Exception -> 0x0575 }
            r12.previousMessageEntities = r7     // Catch:{ Exception -> 0x0575 }
            java.lang.String r7 = r6.attachPath     // Catch:{ Exception -> 0x0575 }
            r12.previousAttachPath = r7     // Catch:{ Exception -> 0x0575 }
            if (r4 != 0) goto L_0x00bd
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x0575 }
            r4.<init>()     // Catch:{ Exception -> 0x0575 }
        L_0x00bd:
            org.telegram.tgnet.SerializedData r4 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0575 }
            r7 = 1
            r4.<init>((boolean) r7)     // Catch:{ Exception -> 0x0575 }
            r11.writePreviousMessageData(r6, r4)     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.SerializedData r7 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0575 }
            int r4 = r4.length()     // Catch:{ Exception -> 0x0575 }
            r7.<init>((int) r4)     // Catch:{ Exception -> 0x0575 }
            r11.writePreviousMessageData(r6, r7)     // Catch:{ Exception -> 0x0575 }
            java.lang.String r4 = "prevMedia"
            byte[] r13 = r7.toByteArray()     // Catch:{ Exception -> 0x0575 }
            r18 = r10
            r10 = 0
            java.lang.String r13 = android.util.Base64.encodeToString(r13, r10)     // Catch:{ Exception -> 0x0575 }
            r5.put(r4, r13)     // Catch:{ Exception -> 0x0575 }
            r7.cleanup()     // Catch:{ Exception -> 0x0575 }
            if (r0 == 0) goto L_0x0123
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0575 }
            r4.<init>()     // Catch:{ Exception -> 0x0575 }
            r6.media = r4     // Catch:{ Exception -> 0x0575 }
            int r7 = r4.flags     // Catch:{ Exception -> 0x0575 }
            r10 = 3
            r7 = r7 | r10
            r4.flags = r7     // Catch:{ Exception -> 0x0575 }
            r4.photo = r0     // Catch:{ Exception -> 0x0575 }
            if (r2 == 0) goto L_0x0107
            int r4 = r31.length()     // Catch:{ Exception -> 0x0575 }
            if (r4 <= 0) goto L_0x0107
            boolean r4 = r2.startsWith(r15)     // Catch:{ Exception -> 0x0575 }
            if (r4 == 0) goto L_0x0107
            r6.attachPath = r2     // Catch:{ Exception -> 0x0575 }
            goto L_0x0121
        L_0x0107:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r0.sizes     // Catch:{ Exception -> 0x0575 }
            int r7 = r4.size()     // Catch:{ Exception -> 0x0575 }
            r10 = 1
            int r7 = r7 - r10
            java.lang.Object r4 = r4.get(r7)     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$PhotoSize r4 = (org.telegram.tgnet.TLRPC$PhotoSize) r4     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.location     // Catch:{ Exception -> 0x0575 }
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r4, r10)     // Catch:{ Exception -> 0x0575 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0575 }
            r6.attachPath = r4     // Catch:{ Exception -> 0x0575 }
        L_0x0121:
            r4 = 2
            goto L_0x014f
        L_0x0123:
            if (r1 == 0) goto L_0x014e
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0575 }
            r4.<init>()     // Catch:{ Exception -> 0x0575 }
            r6.media = r4     // Catch:{ Exception -> 0x0575 }
            int r7 = r4.flags     // Catch:{ Exception -> 0x0575 }
            r10 = 3
            r7 = r7 | r10
            r4.flags = r7     // Catch:{ Exception -> 0x0575 }
            r4.document = r1     // Catch:{ Exception -> 0x0575 }
            boolean r4 = org.telegram.messenger.MessageObject.isVideoDocument(r30)     // Catch:{ Exception -> 0x0575 }
            if (r4 != 0) goto L_0x013f
            if (r29 == 0) goto L_0x013d
            goto L_0x013f
        L_0x013d:
            r4 = 7
            goto L_0x0140
        L_0x013f:
            r4 = 3
        L_0x0140:
            if (r29 == 0) goto L_0x014b
            java.lang.String r7 = r29.getString()     // Catch:{ Exception -> 0x0575 }
            java.lang.String r10 = "ve"
            r5.put(r10, r7)     // Catch:{ Exception -> 0x0575 }
        L_0x014b:
            r6.attachPath = r2     // Catch:{ Exception -> 0x0575 }
            goto L_0x014f
        L_0x014e:
            r4 = 1
        L_0x014f:
            r6.params = r5     // Catch:{ Exception -> 0x0575 }
            r7 = 3
            r6.send_state = r7     // Catch:{ Exception -> 0x0575 }
            r10 = r34
            r13 = r2
            r2 = r4
            r7 = r5
            r5 = r29
        L_0x015b:
            java.lang.String r4 = r6.attachPath     // Catch:{ Exception -> 0x0575 }
            if (r4 != 0) goto L_0x0163
            java.lang.String r4 = ""
            r6.attachPath = r4     // Catch:{ Exception -> 0x0575 }
        L_0x0163:
            r4 = 0
            r6.local_id = r4     // Catch:{ Exception -> 0x0575 }
            int r4 = r12.type     // Catch:{ Exception -> 0x0575 }
            r28 = r1
            r1 = 3
            if (r4 == r1) goto L_0x0172
            if (r5 != 0) goto L_0x0172
            r1 = 2
            if (r4 != r1) goto L_0x017d
        L_0x0172:
            java.lang.String r1 = r6.attachPath     // Catch:{ Exception -> 0x0575 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ Exception -> 0x0575 }
            if (r1 != 0) goto L_0x017d
            r1 = 1
            r12.attachPathExists = r1     // Catch:{ Exception -> 0x0575 }
        L_0x017d:
            org.telegram.messenger.VideoEditedInfo r1 = r12.videoEditedInfo     // Catch:{ Exception -> 0x0575 }
            if (r1 == 0) goto L_0x0184
            if (r5 != 0) goto L_0x0184
            r5 = r1
        L_0x0184:
            if (r33 != 0) goto L_0x0259
            java.lang.CharSequence r4 = r12.editingMessage     // Catch:{ Exception -> 0x0575 }
            if (r4 == 0) goto L_0x01f5
            java.lang.String r1 = r6.message     // Catch:{ Exception -> 0x0575 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0575 }
            r6.message = r4     // Catch:{ Exception -> 0x0575 }
            r30 = r5
            r5 = 0
            r12.caption = r5     // Catch:{ Exception -> 0x0575 }
            r5 = 1
            if (r2 != r5) goto L_0x01b4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r12.editingMessageEntities     // Catch:{ Exception -> 0x0575 }
            if (r5 == 0) goto L_0x01a7
            r6.entities = r5     // Catch:{ Exception -> 0x0575 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x0575 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x0575 }
            goto L_0x01f7
        L_0x01a7:
            boolean r1 = android.text.TextUtils.equals(r1, r4)     // Catch:{ Exception -> 0x0575 }
            if (r1 != 0) goto L_0x01f7
            int r1 = r6.flags     // Catch:{ Exception -> 0x0575 }
            r1 = r1 & -129(0xffffffffffffff7f, float:NaN)
            r6.flags = r1     // Catch:{ Exception -> 0x0575 }
            goto L_0x01f7
        L_0x01b4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r12.editingMessageEntities     // Catch:{ Exception -> 0x0575 }
            if (r4 == 0) goto L_0x01c1
            r6.entities = r4     // Catch:{ Exception -> 0x0575 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x0575 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x0575 }
            goto L_0x01f1
        L_0x01c1:
            r4 = 1
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r4]     // Catch:{ Exception -> 0x0575 }
            java.lang.CharSequence r4 = r12.editingMessage     // Catch:{ Exception -> 0x0575 }
            r19 = 0
            r5[r19] = r4     // Catch:{ Exception -> 0x0575 }
            org.telegram.messenger.MediaDataController r4 = r26.getMediaDataController()     // Catch:{ Exception -> 0x0575 }
            java.util.ArrayList r4 = r4.getEntities(r5, r14)     // Catch:{ Exception -> 0x0575 }
            if (r4 == 0) goto L_0x01e3
            boolean r5 = r4.isEmpty()     // Catch:{ Exception -> 0x0575 }
            if (r5 != 0) goto L_0x01e3
            r6.entities = r4     // Catch:{ Exception -> 0x0575 }
            int r1 = r6.flags     // Catch:{ Exception -> 0x0575 }
            r1 = r1 | 128(0x80, float:1.794E-43)
            r6.flags = r1     // Catch:{ Exception -> 0x0575 }
            goto L_0x01f1
        L_0x01e3:
            java.lang.String r4 = r6.message     // Catch:{ Exception -> 0x0575 }
            boolean r1 = android.text.TextUtils.equals(r1, r4)     // Catch:{ Exception -> 0x0575 }
            if (r1 != 0) goto L_0x01f1
            int r1 = r6.flags     // Catch:{ Exception -> 0x0575 }
            r1 = r1 & -129(0xffffffffffffff7f, float:NaN)
            r6.flags = r1     // Catch:{ Exception -> 0x0575 }
        L_0x01f1:
            r27.generateCaption()     // Catch:{ Exception -> 0x0575 }
            goto L_0x01f7
        L_0x01f5:
            r30 = r5
        L_0x01f7:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0575 }
            r1.<init>()     // Catch:{ Exception -> 0x0575 }
            r1.add(r6)     // Catch:{ Exception -> 0x0575 }
            org.telegram.messenger.MessagesStorage r19 = r26.getMessagesStorage()     // Catch:{ Exception -> 0x0575 }
            r21 = 0
            r22 = 1
            r23 = 0
            r24 = 0
            boolean r4 = r12.scheduled     // Catch:{ Exception -> 0x0575 }
            r20 = r1
            r25 = r4
            r19.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r20, (boolean) r21, (boolean) r22, (boolean) r23, (int) r24, (boolean) r25)     // Catch:{ Exception -> 0x0575 }
            r1 = -1
            r12.type = r1     // Catch:{ Exception -> 0x0575 }
            r27.setType()     // Catch:{ Exception -> 0x0575 }
            r1 = 1
            if (r2 != r1) goto L_0x0234
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media     // Catch:{ Exception -> 0x0575 }
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0575 }
            if (r4 != 0) goto L_0x0231
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0575 }
            if (r1 == 0) goto L_0x022a
            goto L_0x0231
        L_0x022a:
            r27.resetLayout()     // Catch:{ Exception -> 0x0575 }
            r27.checkLayout()     // Catch:{ Exception -> 0x0575 }
            goto L_0x0234
        L_0x0231:
            r27.generateCaption()     // Catch:{ Exception -> 0x0575 }
        L_0x0234:
            r27.createMessageSendInfo()     // Catch:{ Exception -> 0x0575 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x0575 }
            r1.<init>()     // Catch:{ Exception -> 0x0575 }
            r1.add(r12)     // Catch:{ Exception -> 0x0575 }
            org.telegram.messenger.NotificationCenter r4 = r26.getNotificationCenter()     // Catch:{ Exception -> 0x0575 }
            int r5 = org.telegram.messenger.NotificationCenter.replaceMessagesObjects     // Catch:{ Exception -> 0x0575 }
            r16 = r14
            r6 = 2
            java.lang.Object[] r14 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0575 }
            java.lang.Long r6 = java.lang.Long.valueOf(r8)     // Catch:{ Exception -> 0x0575 }
            r19 = 0
            r14[r19] = r6     // Catch:{ Exception -> 0x0575 }
            r6 = 1
            r14[r6] = r1     // Catch:{ Exception -> 0x0575 }
            r4.postNotificationName(r5, r14)     // Catch:{ Exception -> 0x0575 }
            goto L_0x025d
        L_0x0259:
            r30 = r5
            r16 = r14
        L_0x025d:
            if (r7 == 0) goto L_0x026d
            boolean r1 = r7.containsKey(r3)     // Catch:{ Exception -> 0x0575 }
            if (r1 == 0) goto L_0x026d
            java.lang.Object r1 = r7.get(r3)     // Catch:{ Exception -> 0x0575 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0575 }
            r4 = r1
            goto L_0x026e
        L_0x026d:
            r4 = 0
        L_0x026e:
            r1 = 8
            r3 = 1
            if (r2 < r3) goto L_0x0276
            r5 = 3
            if (r2 <= r5) goto L_0x027b
        L_0x0276:
            r5 = 5
            if (r2 < r5) goto L_0x057c
            if (r2 > r1) goto L_0x057c
        L_0x027b:
            if (r2 != r3) goto L_0x027f
            goto L_0x0466
        L_0x027f:
            java.lang.String r3 = "masks"
            r14 = 2
            if (r2 != r14) goto L_0x0332
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r14 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x0575 }
            r14.<init>()     // Catch:{ Exception -> 0x0575 }
            if (r7 == 0) goto L_0x02c9
            java.lang.Object r3 = r7.get(r3)     // Catch:{ Exception -> 0x0575 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0575 }
            if (r3 == 0) goto L_0x02c9
            org.telegram.tgnet.SerializedData r1 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0575 }
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x0575 }
            r1.<init>((byte[]) r3)     // Catch:{ Exception -> 0x0575 }
            r3 = 0
            int r5 = r1.readInt32(r3)     // Catch:{ Exception -> 0x0575 }
            r6 = 0
        L_0x02a2:
            if (r6 >= r5) goto L_0x02bd
            r28 = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r5 = r14.stickers     // Catch:{ Exception -> 0x0575 }
            r19 = r7
            int r7 = r1.readInt32(r3)     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$InputDocument r7 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r1, r7, r3)     // Catch:{ Exception -> 0x0575 }
            r5.add(r7)     // Catch:{ Exception -> 0x0575 }
            int r6 = r6 + 1
            r5 = r28
            r7 = r19
            r3 = 0
            goto L_0x02a2
        L_0x02bd:
            r19 = r7
            int r3 = r14.flags     // Catch:{ Exception -> 0x0575 }
            r5 = 1
            r3 = r3 | r5
            r14.flags = r3     // Catch:{ Exception -> 0x0575 }
            r1.cleanup()     // Catch:{ Exception -> 0x0575 }
            goto L_0x02cb
        L_0x02c9:
            r19 = r7
        L_0x02cb:
            long r5 = r0.access_hash     // Catch:{ Exception -> 0x0575 }
            r20 = 0
            int r1 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1))
            if (r1 != 0) goto L_0x02d6
            r5 = r14
            r1 = 1
            goto L_0x02f7
        L_0x02d6:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x0575 }
            r1.<init>()     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r3 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x0575 }
            r3.<init>()     // Catch:{ Exception -> 0x0575 }
            r1.id = r3     // Catch:{ Exception -> 0x0575 }
            long r5 = r0.id     // Catch:{ Exception -> 0x0575 }
            r3.id = r5     // Catch:{ Exception -> 0x0575 }
            long r5 = r0.access_hash     // Catch:{ Exception -> 0x0575 }
            r3.access_hash = r5     // Catch:{ Exception -> 0x0575 }
            byte[] r5 = r0.file_reference     // Catch:{ Exception -> 0x0575 }
            r3.file_reference = r5     // Catch:{ Exception -> 0x0575 }
            if (r5 != 0) goto L_0x02f5
            r5 = 0
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x0575 }
            r3.file_reference = r6     // Catch:{ Exception -> 0x0575 }
        L_0x02f5:
            r5 = r1
            r1 = 0
        L_0x02f7:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0575 }
            r3.<init>(r8)     // Catch:{ Exception -> 0x0575 }
            r6 = 0
            r3.type = r6     // Catch:{ Exception -> 0x0575 }
            r3.obj = r12     // Catch:{ Exception -> 0x0575 }
            r3.originalPath = r4     // Catch:{ Exception -> 0x0575 }
            r3.parentObject = r10     // Catch:{ Exception -> 0x0575 }
            r3.inputUploadMedia = r14     // Catch:{ Exception -> 0x0575 }
            r3.performMediaUpload = r1     // Catch:{ Exception -> 0x0575 }
            if (r13 == 0) goto L_0x031a
            int r6 = r13.length()     // Catch:{ Exception -> 0x0575 }
            if (r6 <= 0) goto L_0x031a
            boolean r6 = r13.startsWith(r15)     // Catch:{ Exception -> 0x0575 }
            if (r6 == 0) goto L_0x031a
            r3.httpLocation = r13     // Catch:{ Exception -> 0x0575 }
            goto L_0x032c
        L_0x031a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.sizes     // Catch:{ Exception -> 0x0575 }
            int r7 = r6.size()     // Catch:{ Exception -> 0x0575 }
            r8 = 1
            int r7 = r7 - r8
            java.lang.Object r6 = r6.get(r7)     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$PhotoSize r6 = (org.telegram.tgnet.TLRPC$PhotoSize) r6     // Catch:{ Exception -> 0x0575 }
            r3.photoSize = r6     // Catch:{ Exception -> 0x0575 }
            r3.locationParent = r0     // Catch:{ Exception -> 0x0575 }
        L_0x032c:
            r13 = r3
            r7 = r19
            r3 = r1
            goto L_0x0469
        L_0x0332:
            r19 = r7
            r0 = 3
            if (r2 != r0) goto L_0x03f9
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0575 }
            r0.<init>()     // Catch:{ Exception -> 0x0575 }
            r7 = r19
            if (r19 == 0) goto L_0x0373
            java.lang.Object r1 = r7.get(r3)     // Catch:{ Exception -> 0x0575 }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ Exception -> 0x0575 }
            if (r1 == 0) goto L_0x0373
            org.telegram.tgnet.SerializedData r3 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0575 }
            byte[] r1 = org.telegram.messenger.Utilities.hexToBytes(r1)     // Catch:{ Exception -> 0x0575 }
            r3.<init>((byte[]) r1)     // Catch:{ Exception -> 0x0575 }
            r1 = 0
            int r5 = r3.readInt32(r1)     // Catch:{ Exception -> 0x0575 }
            r6 = 0
        L_0x0357:
            if (r6 >= r5) goto L_0x036a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r13 = r0.stickers     // Catch:{ Exception -> 0x0575 }
            int r14 = r3.readInt32(r1)     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$InputDocument r14 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r3, r14, r1)     // Catch:{ Exception -> 0x0575 }
            r13.add(r14)     // Catch:{ Exception -> 0x0575 }
            int r6 = r6 + 1
            r1 = 0
            goto L_0x0357
        L_0x036a:
            int r1 = r0.flags     // Catch:{ Exception -> 0x0575 }
            r5 = 1
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0575 }
            r3.cleanup()     // Catch:{ Exception -> 0x0575 }
        L_0x0373:
            r1 = r28
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x0575 }
            r0.mime_type = r3     // Catch:{ Exception -> 0x0575 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0575 }
            r0.attributes = r3     // Catch:{ Exception -> 0x0575 }
            boolean r3 = r27.isGif()     // Catch:{ Exception -> 0x0575 }
            if (r3 != 0) goto L_0x039b
            if (r30 == 0) goto L_0x038c
            r5 = r30
            boolean r3 = r5.muted     // Catch:{ Exception -> 0x0575 }
            if (r3 != 0) goto L_0x039d
            goto L_0x038e
        L_0x038c:
            r5 = r30
        L_0x038e:
            r3 = 1
            r0.nosound_video = r3     // Catch:{ Exception -> 0x0575 }
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0575 }
            if (r3 == 0) goto L_0x039d
            java.lang.String r3 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x0575 }
            goto L_0x039d
        L_0x039b:
            r5 = r30
        L_0x039d:
            long r13 = r1.access_hash     // Catch:{ Exception -> 0x0575 }
            r19 = 0
            int r3 = (r13 > r19 ? 1 : (r13 == r19 ? 0 : -1))
            if (r3 != 0) goto L_0x03a8
            r6 = r0
            r3 = 1
            goto L_0x03c9
        L_0x03a8:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0575 }
            r3.<init>()     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0575 }
            r6.<init>()     // Catch:{ Exception -> 0x0575 }
            r3.id = r6     // Catch:{ Exception -> 0x0575 }
            long r13 = r1.id     // Catch:{ Exception -> 0x0575 }
            r6.id = r13     // Catch:{ Exception -> 0x0575 }
            long r13 = r1.access_hash     // Catch:{ Exception -> 0x0575 }
            r6.access_hash = r13     // Catch:{ Exception -> 0x0575 }
            byte[] r13 = r1.file_reference     // Catch:{ Exception -> 0x0575 }
            r6.file_reference = r13     // Catch:{ Exception -> 0x0575 }
            if (r13 != 0) goto L_0x03c7
            r13 = 0
            byte[] r14 = new byte[r13]     // Catch:{ Exception -> 0x0575 }
            r6.file_reference = r14     // Catch:{ Exception -> 0x0575 }
        L_0x03c7:
            r6 = r3
            r3 = 0
        L_0x03c9:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r13 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0575 }
            r13.<init>(r8)     // Catch:{ Exception -> 0x0575 }
            r8 = 1
            r13.type = r8     // Catch:{ Exception -> 0x0575 }
            r13.obj = r12     // Catch:{ Exception -> 0x0575 }
            r13.originalPath = r4     // Catch:{ Exception -> 0x0575 }
            r13.parentObject = r10     // Catch:{ Exception -> 0x0575 }
            r13.inputUploadMedia = r0     // Catch:{ Exception -> 0x0575 }
            r13.performMediaUpload = r3     // Catch:{ Exception -> 0x0575 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0575 }
            boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0575 }
            if (r0 != 0) goto L_0x03f4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r1.thumbs     // Catch:{ Exception -> 0x0575 }
            r8 = 0
            java.lang.Object r0 = r0.get(r8)     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0     // Catch:{ Exception -> 0x0575 }
            boolean r8 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x0575 }
            if (r8 != 0) goto L_0x03f4
            r13.photoSize = r0     // Catch:{ Exception -> 0x0575 }
            r13.locationParent = r1     // Catch:{ Exception -> 0x0575 }
        L_0x03f4:
            r13.videoEditedInfo = r5     // Catch:{ Exception -> 0x0575 }
            r5 = r6
            goto L_0x0469
        L_0x03f9:
            r1 = r28
            r7 = r19
            r0 = 7
            if (r2 != r0) goto L_0x0466
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r0 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0575 }
            r0.<init>()     // Catch:{ Exception -> 0x0575 }
            java.lang.String r3 = r1.mime_type     // Catch:{ Exception -> 0x0575 }
            r0.mime_type = r3     // Catch:{ Exception -> 0x0575 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r1.attributes     // Catch:{ Exception -> 0x0575 }
            r0.attributes = r3     // Catch:{ Exception -> 0x0575 }
            long r5 = r1.access_hash     // Catch:{ Exception -> 0x0575 }
            r13 = 0
            int r3 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r3 != 0) goto L_0x0418
            r5 = r0
            r3 = 1
            goto L_0x0439
        L_0x0418:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x0575 }
            r3.<init>()     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x0575 }
            r5.<init>()     // Catch:{ Exception -> 0x0575 }
            r3.id = r5     // Catch:{ Exception -> 0x0575 }
            long r13 = r1.id     // Catch:{ Exception -> 0x0575 }
            r5.id = r13     // Catch:{ Exception -> 0x0575 }
            long r13 = r1.access_hash     // Catch:{ Exception -> 0x0575 }
            r5.access_hash = r13     // Catch:{ Exception -> 0x0575 }
            byte[] r6 = r1.file_reference     // Catch:{ Exception -> 0x0575 }
            r5.file_reference = r6     // Catch:{ Exception -> 0x0575 }
            if (r6 != 0) goto L_0x0437
            r6 = 0
            byte[] r13 = new byte[r6]     // Catch:{ Exception -> 0x0575 }
            r5.file_reference = r13     // Catch:{ Exception -> 0x0575 }
        L_0x0437:
            r5 = r3
            r3 = 0
        L_0x0439:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r6 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0575 }
            r6.<init>(r8)     // Catch:{ Exception -> 0x0575 }
            r6.originalPath = r4     // Catch:{ Exception -> 0x0575 }
            r8 = 2
            r6.type = r8     // Catch:{ Exception -> 0x0575 }
            r6.obj = r12     // Catch:{ Exception -> 0x0575 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r1.thumbs     // Catch:{ Exception -> 0x0575 }
            boolean r8 = r8.isEmpty()     // Catch:{ Exception -> 0x0575 }
            if (r8 != 0) goto L_0x045e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r1.thumbs     // Catch:{ Exception -> 0x0575 }
            r9 = 0
            java.lang.Object r8 = r8.get(r9)     // Catch:{ Exception -> 0x0575 }
            org.telegram.tgnet.TLRPC$PhotoSize r8 = (org.telegram.tgnet.TLRPC$PhotoSize) r8     // Catch:{ Exception -> 0x0575 }
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x0575 }
            if (r9 != 0) goto L_0x045e
            r6.photoSize = r8     // Catch:{ Exception -> 0x0575 }
            r6.locationParent = r1     // Catch:{ Exception -> 0x0575 }
        L_0x045e:
            r6.parentObject = r10     // Catch:{ Exception -> 0x0575 }
            r6.inputUploadMedia = r0     // Catch:{ Exception -> 0x0575 }
            r6.performMediaUpload = r3     // Catch:{ Exception -> 0x0575 }
            r13 = r6
            goto L_0x0469
        L_0x0466:
            r3 = 0
            r5 = 0
            r13 = 0
        L_0x0469:
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r0 = new org.telegram.tgnet.TLRPC$TL_messages_editMessage     // Catch:{ Exception -> 0x0575 }
            r0.<init>()     // Catch:{ Exception -> 0x0575 }
            int r1 = r27.getId()     // Catch:{ Exception -> 0x0575 }
            r0.id = r1     // Catch:{ Exception -> 0x0575 }
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()     // Catch:{ Exception -> 0x0575 }
            r6 = r18
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer((int) r6)     // Catch:{ Exception -> 0x0575 }
            r0.peer = r1     // Catch:{ Exception -> 0x0575 }
            if (r5 == 0) goto L_0x048a
            int r1 = r0.flags     // Catch:{ Exception -> 0x0575 }
            r1 = r1 | 16384(0x4000, float:2.2959E-41)
            r0.flags = r1     // Catch:{ Exception -> 0x0575 }
            r0.media = r5     // Catch:{ Exception -> 0x0575 }
        L_0x048a:
            boolean r1 = r12.scheduled     // Catch:{ Exception -> 0x0575 }
            if (r1 == 0) goto L_0x049c
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner     // Catch:{ Exception -> 0x0575 }
            int r1 = r1.date     // Catch:{ Exception -> 0x0575 }
            r0.schedule_date = r1     // Catch:{ Exception -> 0x0575 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0575 }
            r5 = 32768(0x8000, float:4.5918E-41)
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0575 }
        L_0x049c:
            java.lang.CharSequence r1 = r12.editingMessage     // Catch:{ Exception -> 0x0575 }
            if (r1 == 0) goto L_0x04ea
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0575 }
            r0.message = r1     // Catch:{ Exception -> 0x0575 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0575 }
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r0.flags = r1     // Catch:{ Exception -> 0x0575 }
            boolean r5 = r12.editingMessageSearchWebPage     // Catch:{ Exception -> 0x0575 }
            if (r5 != 0) goto L_0x04b2
            r5 = 1
            goto L_0x04b3
        L_0x04b2:
            r5 = 0
        L_0x04b3:
            r0.no_webpage = r5     // Catch:{ Exception -> 0x0575 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r12.editingMessageEntities     // Catch:{ Exception -> 0x0575 }
            if (r5 == 0) goto L_0x04c2
            r0.entities = r5     // Catch:{ Exception -> 0x0575 }
            r5 = 8
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0575 }
        L_0x04c0:
            r1 = 0
            goto L_0x04e6
        L_0x04c2:
            r1 = 1
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r1]     // Catch:{ Exception -> 0x0575 }
            java.lang.CharSequence r1 = r12.editingMessage     // Catch:{ Exception -> 0x0575 }
            r6 = 0
            r5[r6] = r1     // Catch:{ Exception -> 0x0575 }
            org.telegram.messenger.MediaDataController r1 = r26.getMediaDataController()     // Catch:{ Exception -> 0x0575 }
            r6 = r16
            java.util.ArrayList r1 = r1.getEntities(r5, r6)     // Catch:{ Exception -> 0x0575 }
            if (r1 == 0) goto L_0x04c0
            boolean r5 = r1.isEmpty()     // Catch:{ Exception -> 0x0575 }
            if (r5 != 0) goto L_0x04c0
            r0.entities = r1     // Catch:{ Exception -> 0x0575 }
            int r1 = r0.flags     // Catch:{ Exception -> 0x0575 }
            r5 = 8
            r1 = r1 | r5
            r0.flags = r1     // Catch:{ Exception -> 0x0575 }
            goto L_0x04c0
        L_0x04e6:
            r12.editingMessage = r1     // Catch:{ Exception -> 0x0575 }
            r12.editingMessageEntities = r1     // Catch:{ Exception -> 0x0575 }
        L_0x04ea:
            if (r13 == 0) goto L_0x04ee
            r13.sendRequest = r0     // Catch:{ Exception -> 0x0575 }
        L_0x04ee:
            r1 = 1
            if (r2 != r1) goto L_0x0500
            r4 = 0
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0575 }
            r1 = r26
            r2 = r0
            r3 = r27
            r5 = r13
            r6 = r10
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x0500:
            r1 = 2
            if (r2 != r1) goto L_0x051c
            if (r3 == 0) goto L_0x050a
            r11.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x050a:
            r5 = 0
            r6 = 1
            boolean r14 = r12.scheduled     // Catch:{ Exception -> 0x0575 }
            r1 = r26
            r2 = r0
            r3 = r27
            r9 = r7
            r7 = r13
            r8 = r10
            r10 = r14
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x051c:
            r9 = r7
            r1 = 3
            if (r2 != r1) goto L_0x0535
            if (r3 == 0) goto L_0x0527
            r11.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x0527:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0575 }
            r1 = r26
            r2 = r0
            r3 = r27
            r5 = r13
            r6 = r10
            r7 = r9
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x0535:
            r1 = 6
            if (r2 != r1) goto L_0x0546
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0575 }
            r1 = r26
            r2 = r0
            r3 = r27
            r5 = r13
            r6 = r10
            r7 = r9
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x0546:
            r1 = 7
            if (r2 != r1) goto L_0x055d
            if (r3 == 0) goto L_0x054f
            r11.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x054f:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0575 }
            r1 = r26
            r2 = r0
            r3 = r27
            r5 = r13
            r6 = r10
            r7 = r9
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x055d:
            r1 = 8
            if (r2 != r1) goto L_0x057c
            if (r3 == 0) goto L_0x0567
            r11.performSendDelayedMessage(r13)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x0567:
            boolean r8 = r12.scheduled     // Catch:{ Exception -> 0x0575 }
            r1 = r26
            r2 = r0
            r3 = r27
            r5 = r13
            r6 = r10
            r7 = r9
            r1.performSendMessageRequest(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057c
        L_0x0575:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r26.revertEditingMessageObject(r27)
        L_0x057c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.editMessage(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$TL_photo, org.telegram.messenger.VideoEditedInfo, org.telegram.tgnet.TLRPC$TL_document, java.lang.String, java.util.HashMap, boolean, java.lang.Object):void");
    }

    public int editMessage(MessageObject messageObject, String str, boolean z, BaseFragment baseFragment, ArrayList<TLRPC$MessageEntity> arrayList, int i) {
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return 0;
        }
        TLRPC$TL_messages_editMessage tLRPC$TL_messages_editMessage = new TLRPC$TL_messages_editMessage();
        tLRPC$TL_messages_editMessage.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
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
        int i2 = i;
        byte[] bArr2 = bArr;
        int i3 = (int) j;
        String str = j + "_" + i2 + "_" + Utilities.bytesToHex(bArr) + "_" + 0;
        this.waitingForCallback.put(str, Boolean.TRUE);
        if (i3 <= 0) {
            int i4 = -i3;
            if (getMessagesController().getChat(Integer.valueOf(i4)) == null && (chatSync = getMessagesStorage().getChatSync(i4)) != null) {
                getMessagesController().putChat(chatSync, true);
            }
        } else if (getMessagesController().getUser(Integer.valueOf(i3)) == null && (userSync = getMessagesStorage().getUserSync(i3)) != null) {
            getMessagesController().putUser(userSync, true);
        }
        TLRPC$TL_messages_getBotCallbackAnswer tLRPC$TL_messages_getBotCallbackAnswer = new TLRPC$TL_messages_getBotCallbackAnswer();
        tLRPC$TL_messages_getBotCallbackAnswer.peer = getMessagesController().getInputPeer(i3);
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
        tLRPC$TL_messages_sendVote.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
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

    public void sendReaction(MessageObject messageObject, CharSequence charSequence, ChatActivity chatActivity) {
        if (messageObject != null && chatActivity != null) {
            TLRPC$TL_messages_sendReaction tLRPC$TL_messages_sendReaction = new TLRPC$TL_messages_sendReaction();
            tLRPC$TL_messages_sendReaction.peer = getMessagesController().getInputPeer((int) messageObject.getDialogId());
            tLRPC$TL_messages_sendReaction.msg_id = messageObject.getId();
            if (charSequence != null) {
                tLRPC$TL_messages_sendReaction.reaction = charSequence.toString();
                tLRPC$TL_messages_sendReaction.flags |= 1;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_sendReaction, new SendMessagesHelper$$ExternalSyntheticLambda78(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendReaction$22(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
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
            if (r0 == 0) goto L_0x01b5
            if (r12 == 0) goto L_0x01b5
            if (r24 != 0) goto L_0x000c
            goto L_0x01b5
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
            goto L_0x01b5
        L_0x0088:
            if (r13 == 0) goto L_0x00ba
            org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth r1 = new org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth
            r1.<init>()
            org.telegram.messenger.MessagesController r2 = r18.getMessagesController()
            long r3 = r20.getDialogId()
            int r4 = (int) r3
            org.telegram.tgnet.TLRPC$InputPeer r2 = r2.getInputPeer((int) r4)
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
            goto L_0x01b5
        L_0x00ba:
            r2 = 2
            boolean r1 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r1 == 0) goto L_0x016c
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            int r1 = r1.flags
            r1 = r1 & 4
            if (r1 != 0) goto L_0x0149
            org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm r1 = new org.telegram.tgnet.TLRPC$TL_payments_getPaymentForm
            r1.<init>()
            int r3 = r20.getId()
            r1.msg_id = r3
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            org.telegram.tgnet.TLRPC$InputPeer r0 = r3.getInputPeer((org.telegram.tgnet.TLRPC$Peer) r0)
            r1.peer = r0
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x013d }
            r0.<init>()     // Catch:{ Exception -> 0x013d }
            java.lang.String r3 = "bg_color"
            java.lang.String r4 = "windowBackgroundWhite"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013d }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013d }
            java.lang.String r3 = "text_color"
            java.lang.String r4 = "windowBackgroundWhiteBlackText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013d }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013d }
            java.lang.String r3 = "hint_color"
            java.lang.String r4 = "windowBackgroundWhiteHintText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013d }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013d }
            java.lang.String r3 = "link_color"
            java.lang.String r4 = "windowBackgroundWhiteLinkText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013d }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013d }
            java.lang.String r3 = "button_color"
            java.lang.String r4 = "featuredStickers_addButton"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013d }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013d }
            java.lang.String r3 = "button_text_color"
            java.lang.String r4 = "featuredStickers_buttonText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)     // Catch:{ Exception -> 0x013d }
            r0.put(r3, r4)     // Catch:{ Exception -> 0x013d }
            org.telegram.tgnet.TLRPC$TL_dataJSON r3 = new org.telegram.tgnet.TLRPC$TL_dataJSON     // Catch:{ Exception -> 0x013d }
            r3.<init>()     // Catch:{ Exception -> 0x013d }
            r1.theme_params = r3     // Catch:{ Exception -> 0x013d }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x013d }
            r3.data = r0     // Catch:{ Exception -> 0x013d }
            int r0 = r1.flags     // Catch:{ Exception -> 0x013d }
            r3 = 1
            r0 = r0 | r3
            r1.flags = r0     // Catch:{ Exception -> 0x013d }
            goto L_0x0141
        L_0x013d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0141:
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r1, r14, r2)
            goto L_0x01b5
        L_0x0149:
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
            goto L_0x01b5
        L_0x016c:
            org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer r1 = new org.telegram.tgnet.TLRPC$TL_messages_getBotCallbackAnswer
            r1.<init>()
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            long r4 = r20.getDialogId()
            int r5 = (int) r4
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((int) r5)
            r1.peer = r3
            int r0 = r20.getId()
            r1.msg_id = r0
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            r1.game = r0
            boolean r0 = r12.requires_password
            if (r0 == 0) goto L_0x01a2
            if (r22 == 0) goto L_0x0193
            r0 = r22
            goto L_0x0198
        L_0x0193:
            org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty r0 = new org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty
            r0.<init>()
        L_0x0198:
            r1.password = r0
            r1.password = r0
            int r0 = r1.flags
            r0 = r0 | 4
            r1.flags = r0
        L_0x01a2:
            byte[] r0 = r12.data
            if (r0 == 0) goto L_0x01ae
            int r3 = r1.flags
            r4 = 1
            r3 = r3 | r4
            r1.flags = r3
            r1.data = r0
        L_0x01ae:
            org.telegram.tgnet.ConnectionsManager r0 = r18.getConnectionsManager()
            r0.sendRequest(r1, r14, r2)
        L_0x01b5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$sendCallback$24(boolean, org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$KeyboardButton, org.telegram.tgnet.TLRPC$InputCheckPasswordSRP, org.telegram.ui.TwoStepVerificationActivity, org.telegram.ui.ChatActivity):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendCallback$30(String str, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity, TwoStepVerificationActivity twoStepVerificationActivity, TLObject[] tLObjectArr, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, boolean z2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda30(this, str, z, tLObject, messageObject, tLRPC$KeyboardButton, chatActivity, twoStepVerificationActivity, tLObjectArr, tLRPC$TL_error, tLRPC$InputCheckPasswordSRP, z2));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0170, code lost:
        if (org.telegram.messenger.MessagesController.getNotificationsSettings(r7.currentAccount).getBoolean("askgame_" + r0, true) != false) goto L_0x0174;
     */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00e4  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x010c  */
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
            goto L_0x04a5
        L_0x001d:
            r8 = 2131626591(0x7f0e0a5f, float:1.8880423E38)
            java.lang.String r9 = "OK"
            r10 = 0
            r11 = 1
            if (r1 == 0) goto L_0x018c
            if (r34 == 0) goto L_0x002e
            r34.needHideProgress()
            r34.finishFragment()
        L_0x002e:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonUrlAuth
            if (r2 == 0) goto L_0x0060
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest
            if (r0 == 0) goto L_0x0044
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest r0 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultRequest) r0
            r1 = r35[r3]
            org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth r1 = (org.telegram.tgnet.TLRPC$TL_messages_requestUrlAuth) r1
            java.lang.String r2 = r5.url
            r6.showRequestUrlAlert(r0, r1, r2, r3)
            goto L_0x04a5
        L_0x0044:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted
            if (r0 == 0) goto L_0x0052
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted r0 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultAccepted) r0
            java.lang.String r0 = r0.url
            org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r6, r0, r3, r3)
            goto L_0x04a5
        L_0x0052:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault
            if (r0 == 0) goto L_0x04a5
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault r0 = (org.telegram.tgnet.TLRPC$TL_urlAuthResultDefault) r0
            java.lang.String r0 = r5.url
            org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r6, r0, r3, r11)
            goto L_0x04a5
        L_0x0060:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy
            if (r2 == 0) goto L_0x008e
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_payments_paymentForm
            if (r0 == 0) goto L_0x007e
            r0 = r1
            org.telegram.tgnet.TLRPC$TL_payments_paymentForm r0 = (org.telegram.tgnet.TLRPC$TL_payments_paymentForm) r0
            org.telegram.messenger.MessagesController r1 = r27.getMessagesController()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r0.users
            r1.putUsers(r2, r3)
            org.telegram.ui.PaymentFormActivity r1 = new org.telegram.ui.PaymentFormActivity
            r1.<init>(r0, r4, r6)
            r6.presentFragment(r1)
            goto L_0x04a5
        L_0x007e:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt
            if (r0 == 0) goto L_0x04a5
            org.telegram.ui.PaymentFormActivity r0 = new org.telegram.ui.PaymentFormActivity
            org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt r1 = (org.telegram.tgnet.TLRPC$TL_payments_paymentReceipt) r1
            r0.<init>(r1)
            r6.presentFragment(r0)
            goto L_0x04a5
        L_0x008e:
            org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer r1 = (org.telegram.tgnet.TLRPC$TL_messages_botCallbackAnswer) r1
            if (r29 != 0) goto L_0x00a1
            int r2 = r1.cache_time
            if (r2 == 0) goto L_0x00a1
            boolean r2 = r5.requires_password
            if (r2 != 0) goto L_0x00a1
            org.telegram.messenger.MessagesStorage r2 = r27.getMessagesStorage()
            r2.saveBotCache(r0, r1)
        L_0x00a1:
            java.lang.String r0 = r1.message
            if (r0 == 0) goto L_0x0113
            int r0 = r31.getFromChatId()
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
            int r2 = r2.via_bot_id
            if (r2 == 0) goto L_0x00b0
            r0 = r2
        L_0x00b0:
            if (r0 <= 0) goto L_0x00c9
            org.telegram.messenger.MessagesController r2 = r27.getMessagesController()
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            if (r0 == 0) goto L_0x00db
            java.lang.String r2 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)
            goto L_0x00dc
        L_0x00c9:
            org.telegram.messenger.MessagesController r2 = r27.getMessagesController()
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r2.getChat(r0)
            if (r0 == 0) goto L_0x00db
            java.lang.String r0 = r0.title
            goto L_0x00dc
        L_0x00db:
            r0 = r10
        L_0x00dc:
            if (r0 != 0) goto L_0x00e0
            java.lang.String r0 = "bot"
        L_0x00e0:
            boolean r2 = r1.alert
            if (r2 == 0) goto L_0x010c
            android.app.Activity r2 = r33.getParentActivity()
            if (r2 != 0) goto L_0x00eb
            return
        L_0x00eb:
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r33.getParentActivity()
            r2.<init>((android.content.Context) r3)
            r2.setTitle(r0)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r2.setPositiveButton(r0, r10)
            java.lang.String r0 = r1.message
            r2.setMessage(r0)
            org.telegram.ui.ActionBar.AlertDialog r0 = r2.create()
            r6.showDialog(r0)
            goto L_0x04a5
        L_0x010c:
            java.lang.String r1 = r1.message
            r6.showAlert(r0, r1)
            goto L_0x04a5
        L_0x0113:
            java.lang.String r0 = r1.url
            if (r0 == 0) goto L_0x04a5
            android.app.Activity r0 = r33.getParentActivity()
            if (r0 != 0) goto L_0x011e
            return
        L_0x011e:
            int r0 = r31.getFromChatId()
            org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
            int r2 = r2.via_bot_id
            if (r2 == 0) goto L_0x0129
            r0 = r2
        L_0x0129:
            org.telegram.messenger.MessagesController r2 = r27.getMessagesController()
            java.lang.Integer r8 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r8)
            if (r2 == 0) goto L_0x013d
            boolean r2 = r2.verified
            if (r2 == 0) goto L_0x013d
            r2 = 1
            goto L_0x013e
        L_0x013d:
            r2 = 0
        L_0x013e:
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonGame
            if (r5 == 0) goto L_0x0185
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x014d
            org.telegram.tgnet.TLRPC$TL_game r5 = r5.game
            goto L_0x014e
        L_0x014d:
            r5 = r10
        L_0x014e:
            if (r5 != 0) goto L_0x0151
            return
        L_0x0151:
            java.lang.String r1 = r1.url
            if (r2 != 0) goto L_0x0173
            int r2 = r7.currentAccount
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "askgame_"
            r8.append(r9)
            r8.append(r0)
            java.lang.String r8 = r8.toString()
            boolean r2 = r2.getBoolean(r8, r11)
            if (r2 == 0) goto L_0x0173
            goto L_0x0174
        L_0x0173:
            r11 = 0
        L_0x0174:
            r32 = r33
            r33 = r5
            r34 = r31
            r35 = r1
            r36 = r11
            r37 = r0
            r32.showOpenGameAlert(r33, r34, r35, r36, r37)
            goto L_0x04a5
        L_0x0185:
            java.lang.String r0 = r1.url
            org.telegram.ui.Components.AlertsCreator.showOpenUrlAlert(r6, r0, r3, r3)
            goto L_0x04a5
        L_0x018c:
            if (r2 == 0) goto L_0x04a5
            android.app.Activity r0 = r33.getParentActivity()
            if (r0 != 0) goto L_0x0195
            return
        L_0x0195:
            java.lang.String r0 = r2.text
            java.lang.String r1 = "PASSWORD_HASH_INVALID"
            boolean r0 = r1.equals(r0)
            r12 = 2131624660(0x7f0e02d4, float:1.8876506E38)
            java.lang.String r13 = "Cancel"
            if (r0 == 0) goto L_0x01f9
            if (r37 != 0) goto L_0x04a5
            org.telegram.ui.ActionBar.AlertDialog$Builder r8 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r0 = r33.getParentActivity()
            r8.<init>((android.content.Context) r0)
            r0 = 2131624593(0x7f0e0291, float:1.887637E38)
            java.lang.String r1 = "BotOwnershipTransfer"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r8.setTitle(r0)
            r0 = 2131624596(0x7f0e0294, float:1.8876376E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = "BotOwnershipTransferReadyAlertText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r8.setMessage(r0)
            r0 = 2131624595(0x7f0e0293, float:1.8876374E38)
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
            goto L_0x04a5
        L_0x01f9:
            java.lang.String r0 = r2.text
            java.lang.String r1 = "PASSWORD_MISSING"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0250
            java.lang.String r0 = r2.text
            java.lang.String r14 = "PASSWORD_TOO_FRESH_"
            boolean r0 = r0.startsWith(r14)
            if (r0 != 0) goto L_0x0250
            java.lang.String r0 = r2.text
            java.lang.String r14 = "SESSION_TOO_FRESH_"
            boolean r0 = r0.startsWith(r14)
            if (r0 == 0) goto L_0x0218
            goto L_0x0250
        L_0x0218:
            java.lang.String r0 = r2.text
            java.lang.String r1 = "SRP_ID_INVALID"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0246
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
            goto L_0x04a5
        L_0x0246:
            if (r34 == 0) goto L_0x04a5
            r34.needHideProgress()
            r34.finishFragment()
            goto L_0x04a5
        L_0x0250:
            if (r34 == 0) goto L_0x0255
            r34.needHideProgress()
        L_0x0255:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r4 = r33.getParentActivity()
            r0.<init>((android.content.Context) r4)
            r4 = 2131625283(0x7f0e0543, float:1.887777E38)
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
            if (r16 == 0) goto L_0x02ac
            r16 = 5
            goto L_0x02ae
        L_0x02ac:
            r16 = 3
        L_0x02ae:
            r10 = r16 | 48
            r5.setGravity(r10)
            r10 = 2131624594(0x7f0e0292, float:1.8876372E38)
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
            r8 = 2131165581(0x7var_d, float:1.7945383E38)
            r12.setImageResource(r8)
            boolean r16 = org.telegram.messenger.LocaleController.isRTL
            r18 = 1093664768(0x41300000, float:11.0)
            if (r16 == 0) goto L_0x0309
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r8 = r16
            goto L_0x030a
        L_0x0309:
            r8 = 0
        L_0x030a:
            r16 = 1091567616(0x41100000, float:9.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r19 = org.telegram.messenger.LocaleController.isRTL
            if (r19 == 0) goto L_0x0316
            r11 = 0
            goto L_0x031c
        L_0x0316:
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r11 = r19
        L_0x031c:
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
            if (r10 == 0) goto L_0x0347
            r10 = 5
            goto L_0x0348
        L_0x0347:
            r10 = 3
        L_0x0348:
            r10 = r10 | 48
            r8.setGravity(r10)
            r10 = 2131625280(0x7f0e0540, float:1.8877763E38)
            java.lang.String r11 = "EditAdminTransferAlertText1"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.replaceTags(r10)
            r8.setText(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0373
            r10 = -1
            r11 = -2
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r8, r15)
            r8 = 5
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r8)
            r5.addView(r12, r15)
            goto L_0x0383
        L_0x0373:
            r10 = -1
            r11 = -2
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
            r5.addView(r12, r15)
            android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r8, r12)
        L_0x0383:
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
            r10 = 2131165581(0x7var_d, float:1.7945383E38)
            r8.setImageResource(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x03ba
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r18)
            goto L_0x03bb
        L_0x03ba:
            r10 = 0
        L_0x03bb:
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r16)
            boolean r12 = org.telegram.messenger.LocaleController.isRTL
            if (r12 == 0) goto L_0x03c5
            r12 = 0
            goto L_0x03c9
        L_0x03c5:
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r18)
        L_0x03c9:
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
            if (r10 == 0) goto L_0x03f6
            r10 = 5
            goto L_0x03f7
        L_0x03f6:
            r10 = 3
        L_0x03f7:
            r10 = r10 | 48
            r3.setGravity(r10)
            r10 = 2131625281(0x7f0e0541, float:1.8877766E38)
            java.lang.String r11 = "EditAdminTransferAlertText2"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            android.text.SpannableStringBuilder r10 = org.telegram.messenger.AndroidUtilities.replaceTags(r10)
            r3.setText(r10)
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 == 0) goto L_0x0422
            r10 = -1
            r11 = -2
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r3, r10)
            r12 = 5
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r12)
            r5.addView(r8, r3)
            goto L_0x0433
        L_0x0422:
            r10 = -1
            r11 = -2
            r12 = 5
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r11)
            r5.addView(r8, r15)
            android.widget.LinearLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r11)
            r5.addView(r3, r8)
        L_0x0433:
            java.lang.String r2 = r2.text
            boolean r1 = r1.equals(r2)
            if (r1 == 0) goto L_0x0458
            r1 = 2131625288(0x7f0e0548, float:1.887778E38)
            java.lang.String r2 = "EditAdminTransferSetPassword"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda1
            r2.<init>(r6)
            r0.setPositiveButton(r1, r2)
            r1 = 2131624660(0x7f0e02d4, float:1.8876506E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r13, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
            goto L_0x049e
        L_0x0458:
            android.widget.TextView r1 = new android.widget.TextView
            android.app.Activity r2 = r33.getParentActivity()
            r1.<init>(r2)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r1.setTextColor(r2)
            r2 = 1098907648(0x41800000, float:16.0)
            r3 = 1
            r1.setTextSize(r3, r2)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x0474
            r17 = 5
        L_0x0474:
            r2 = r17 | 48
            r1.setGravity(r2)
            r2 = 2131625282(0x7f0e0542, float:1.8877768E38)
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
            r1 = 2131626591(0x7f0e0a5f, float:1.8880423E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r2 = 0
            r0.setNegativeButton(r1, r2)
        L_0x049e:
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r6.showDialog(r0)
        L_0x04a5:
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
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMedia, new SendMessagesHelper$$ExternalSyntheticLambda79(this, j2));
                }
                j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_sendMedia, new SendMessagesHelper$$ExternalSyntheticLambda79(this, j2));
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v1, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: org.telegram.tgnet.TLRPC$MessageMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v5, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v7, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v6, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v16, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v7, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v20, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v3, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v24, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v52, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v4, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v26, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v59, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v6, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v71, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v7, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v85, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v129, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v28, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v138, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v139, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v30, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v33, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v34, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v35, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v174, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v45, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v35, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v69, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v181, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v153, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v183, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v72, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v18, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v159, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v74, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r54v18, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v190, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v193, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v168, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v47, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v76, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v169, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v78, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v79, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v80, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v83, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v175, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v176, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v178, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v73, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v44, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v25, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v45, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v27, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v182, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v46, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v28, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v51, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v97, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v184, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v29, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v52, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v186, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v31, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v53, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v187, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v32, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v54, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v33, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v34, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v36, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v188, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v190, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v76, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v193, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v64, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v38, resolved type: org.telegram.messenger.SendMessagesHelper$DelayedMessage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v67, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v203, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v145, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v148, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v213, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v217, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v81, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v229, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v230, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v82, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v208, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v84, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v231, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v20, resolved type: org.telegram.messenger.MessageObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v42, resolved type: org.telegram.tgnet.TLRPC$MessageMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v232, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v85, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v210, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v233, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v86, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v211, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v212, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v213, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v214, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v215, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r23v11, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v91, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v234, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v218, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v96, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v103, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v251, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v252, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v236, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v105, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v254, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v255, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v256, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v257, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v258, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v259, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v260, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v261, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v262, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v263, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v264, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v265, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v266, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v267, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v150, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v268, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v269, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v278, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v237, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v112, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v113, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v238, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v239, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v114, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v79, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v115, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v240, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v61, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v54, resolved type: org.telegram.tgnet.TLRPC$MessageMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v289, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v29, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v56, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v30, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v57, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r61v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v190, resolved type: org.telegram.tgnet.TLRPC$TL_userRequest_old2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v120, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v7, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v247, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v37, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v159, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v330, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v331, resolved type: org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v332, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v251, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaContact} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v163, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v164, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v166, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v167, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v168, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v169, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v170, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v171, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v65, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v172, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v173, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v174, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v175, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v176, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v177, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v178, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v179, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v252, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v253, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v254, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v255, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v256, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v257, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaDice} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v77, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v78, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v258, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v39, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v41, resolved type: org.telegram.messenger.SendMessagesHelper} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v118, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v259, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v119, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v120, resolved type: byte[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v84, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v260, resolved type: org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v88, resolved type: org.telegram.tgnet.TLRPC$Message} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v90, resolved type: org.telegram.tgnet.TLRPC$MessageMedia} */
    /* JADX WARNING: type inference failed for: r18v30 */
    /* JADX WARNING: type inference failed for: r18v75 */
    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0e70: MOVE  (r7v93 org.telegram.tgnet.TLRPC$TL_photo) = (r44v0 org.telegram.tgnet.TLRPC$TL_photo)
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
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:1123:0x142b A[Catch:{ Exception -> 0x153f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1125:0x1435 A[Catch:{ Exception -> 0x153f }] */
    /* JADX WARNING: Removed duplicated region for block: B:1194:0x154a  */
    /* JADX WARNING: Removed duplicated region for block: B:1268:0x16b3 A[Catch:{ Exception -> 0x1586 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1274:0x16e1 A[Catch:{ Exception -> 0x1586 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1279:0x171e A[Catch:{ Exception -> 0x1586 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1287:0x174c A[Catch:{ Exception -> 0x1586 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1288:0x174e A[Catch:{ Exception -> 0x1586 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1290:0x1752 A[Catch:{ Exception -> 0x1586 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1293:0x1760 A[Catch:{ Exception -> 0x1586 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1356:0x1977 A[Catch:{ Exception -> 0x17af }] */
    /* JADX WARNING: Removed duplicated region for block: B:1357:0x1979 A[Catch:{ Exception -> 0x17af }] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0298 A[Catch:{ Exception -> 0x0319 }] */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x029d A[Catch:{ Exception -> 0x0319 }] */
    /* JADX WARNING: Removed duplicated region for block: B:1396:0x1a76 A[Catch:{ Exception -> 0x17af }] */
    /* JADX WARNING: Removed duplicated region for block: B:1397:0x1a78 A[Catch:{ Exception -> 0x17af }] */
    /* JADX WARNING: Removed duplicated region for block: B:1413:0x1aa7 A[SYNTHETIC, Splitter:B:1413:0x1aa7] */
    /* JADX WARNING: Removed duplicated region for block: B:1417:0x1aae A[SYNTHETIC, Splitter:B:1417:0x1aae] */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02a9  */
    /* JADX WARNING: Removed duplicated region for block: B:1437:0x1afc  */
    /* JADX WARNING: Removed duplicated region for block: B:1439:0x1b00 A[SYNTHETIC, Splitter:B:1439:0x1b00] */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x02c8  */
    /* JADX WARNING: Removed duplicated region for block: B:1458:0x1b34 A[SYNTHETIC, Splitter:B:1458:0x1b34] */
    /* JADX WARNING: Removed duplicated region for block: B:1487:0x1baa A[Catch:{ Exception -> 0x1CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:1529:0x1c6a  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x032b  */
    /* JADX WARNING: Removed duplicated region for block: B:1530:0x1c6c  */
    /* JADX WARNING: Removed duplicated region for block: B:1533:0x1CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1546:0x16d1 A[EDGE_INSN: B:1546:0x16d1->B:1272:0x16d1 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:1564:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x03ba A[Catch:{ Exception -> 0x0369 }] */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x03c2 A[Catch:{ Exception -> 0x0369 }] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x03d3 A[Catch:{ Exception -> 0x0369 }] */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x03d6 A[Catch:{ Exception -> 0x0369 }] */
    /* JADX WARNING: Removed duplicated region for block: B:341:0x0635 A[Catch:{ Exception -> 0x05e5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x0658 A[Catch:{ Exception -> 0x05e5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:353:0x0663 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x0667 A[Catch:{ Exception -> 0x0771 }] */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x07a9 A[Catch:{ Exception -> 0x07a2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x07ac A[SYNTHETIC, Splitter:B:413:0x07ac] */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x07b6 A[SYNTHETIC, Splitter:B:421:0x07b6] */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x07e1 A[Catch:{ Exception -> 0x07a2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x07ec A[SYNTHETIC, Splitter:B:432:0x07ec] */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0820  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x084b  */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x0858 A[SYNTHETIC, Splitter:B:450:0x0858] */
    /* JADX WARNING: Removed duplicated region for block: B:454:0x0862  */
    /* JADX WARNING: Removed duplicated region for block: B:467:0x0895 A[SYNTHETIC, Splitter:B:467:0x0895] */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x089b  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x089d A[SYNTHETIC, Splitter:B:472:0x089d] */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x08ab A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:489:0x08e1  */
    /* JADX WARNING: Removed duplicated region for block: B:495:0x08f2 A[SYNTHETIC, Splitter:B:495:0x08f2] */
    /* JADX WARNING: Removed duplicated region for block: B:507:0x0935 A[Catch:{ Exception -> 0x0845 }] */
    /* JADX WARNING: Removed duplicated region for block: B:509:0x093b A[Catch:{ Exception -> 0x0845 }] */
    /* JADX WARNING: Removed duplicated region for block: B:515:0x0965 A[Catch:{ Exception -> 0x0845 }] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0116 A[SYNTHETIC, Splitter:B:51:0x0116] */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x0994 A[SYNTHETIC, Splitter:B:525:0x0994] */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x09c6 A[SYNTHETIC, Splitter:B:548:0x09c6] */
    /* JADX WARNING: Removed duplicated region for block: B:585:0x0a53 A[SYNTHETIC, Splitter:B:585:0x0a53] */
    /* JADX WARNING: Removed duplicated region for block: B:591:0x0a5c  */
    /* JADX WARNING: Removed duplicated region for block: B:602:0x0a88  */
    /* JADX WARNING: Removed duplicated region for block: B:612:0x0aca A[Catch:{ Exception -> 0x1CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:613:0x0acc A[Catch:{ Exception -> 0x1CLASSNAME }] */
    /* JADX WARNING: Removed duplicated region for block: B:616:0x0ad5 A[SYNTHETIC, Splitter:B:616:0x0ad5] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x012f A[Catch:{ Exception -> 0x0319 }] */
    /* JADX WARNING: Removed duplicated region for block: B:635:0x0b00 A[SYNTHETIC, Splitter:B:635:0x0b00] */
    /* JADX WARNING: Removed duplicated region for block: B:653:0x0b52  */
    /* JADX WARNING: Removed duplicated region for block: B:676:0x0ba2  */
    /* JADX WARNING: Removed duplicated region for block: B:681:0x0c0e A[Catch:{ Exception -> 0x0b4e }] */
    /* JADX WARNING: Removed duplicated region for block: B:683:0x0CLASSNAME A[Catch:{ Exception -> 0x0b4e }] */
    /* JADX WARNING: Removed duplicated region for block: B:728:0x0cfe A[Catch:{ Exception -> 0x0b4e }] */
    /* JADX WARNING: Removed duplicated region for block: B:731:0x0d17 A[Catch:{ Exception -> 0x0b4e }] */
    /* JADX WARNING: Removed duplicated region for block: B:732:0x0d25 A[Catch:{ Exception -> 0x0b4e }] */
    /* JADX WARNING: Removed duplicated region for block: B:740:0x0d48 A[Catch:{ Exception -> 0x0b4e }] */
    /* JADX WARNING: Removed duplicated region for block: B:741:0x0d4a A[Catch:{ Exception -> 0x0b4e }] */
    /* JADX WARNING: Removed duplicated region for block: B:771:0x0def A[Catch:{ Exception -> 0x0b4e }] */
    /* JADX WARNING: Removed duplicated region for block: B:815:0x0eb2 A[Catch:{ Exception -> 0x0b4e }] */
    /* JADX WARNING: Removed duplicated region for block: B:817:0x0ec2  */
    /* JADX WARNING: Removed duplicated region for block: B:831:0x0var_ A[Catch:{ Exception -> 0x1004 }] */
    /* JADX WARNING: Removed duplicated region for block: B:832:0x0f1c A[Catch:{ Exception -> 0x1004 }] */
    /* JADX WARNING: Removed duplicated region for block: B:841:0x0var_ A[Catch:{ Exception -> 0x1004 }] */
    /* JADX WARNING: Removed duplicated region for block: B:846:0x0f6f A[Catch:{ Exception -> 0x1004 }] */
    /* JADX WARNING: Removed duplicated region for block: B:849:0x0f7f A[Catch:{ Exception -> 0x1004 }] */
    /* JADX WARNING: Removed duplicated region for block: B:984:0x11b0  */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:278:0x0535=Splitter:B:278:0x0535, B:214:0x042d=Splitter:B:214:0x042d, B:200:0x03fe=Splitter:B:200:0x03fe, B:228:0x046e=Splitter:B:228:0x046e} */
    private void sendMessage(java.lang.String r54, java.lang.String r55, org.telegram.tgnet.TLRPC$MessageMedia r56, org.telegram.tgnet.TLRPC$TL_photo r57, org.telegram.messenger.VideoEditedInfo r58, org.telegram.tgnet.TLRPC$User r59, org.telegram.tgnet.TLRPC$TL_document r60, org.telegram.tgnet.TLRPC$TL_game r61, org.telegram.tgnet.TLRPC$TL_messageMediaPoll r62, org.telegram.tgnet.TLRPC$TL_messageMediaInvoice r63, long r64, java.lang.String r66, org.telegram.messenger.MessageObject r67, org.telegram.messenger.MessageObject r68, org.telegram.tgnet.TLRPC$WebPage r69, boolean r70, org.telegram.messenger.MessageObject r71, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r72, org.telegram.tgnet.TLRPC$ReplyMarkup r73, java.util.HashMap<java.lang.String, java.lang.String> r74, boolean r75, int r76, int r77, java.lang.Object r78, org.telegram.messenger.MessageObject.SendAnimationData r79) {
        /*
            r53 = this;
            r1 = r53
            r2 = r54
            r3 = r56
            r4 = r57
            r5 = r59
            r6 = r60
            r7 = r61
            r8 = r62
            r9 = r63
            r10 = r64
            r12 = r66
            r13 = r67
            r14 = r68
            r15 = r69
            r14 = r71
            r13 = r72
            r6 = r74
            r9 = r76
            r7 = r77
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
            if (r55 != 0) goto L_0x003d
            r18 = r12
            goto L_0x003f
        L_0x003d:
            r18 = r55
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
            r20 = r5
            int r5 = (int) r10
            r21 = 32
            long r3 = r10 >> r21
            int r4 = (int) r3
            if (r5 == 0) goto L_0x0068
            org.telegram.messenger.MessagesController r3 = r53.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r3 = r3.getInputPeer((int) r5)
            goto L_0x0069
        L_0x0068:
            r3 = 0
        L_0x0069:
            org.telegram.messenger.UserConfig r21 = r53.getUserConfig()
            int r10 = r21.getClientUserId()
            if (r5 != 0) goto L_0x00b7
            org.telegram.messenger.MessagesController r11 = r53.getMessagesController()
            r23 = r10
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = r11.getEncryptedChat(r10)
            if (r10 != 0) goto L_0x00b4
            if (r14 == 0) goto L_0x00b3
            org.telegram.messenger.MessagesStorage r2 = r53.getMessagesStorage()
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner
            boolean r4 = r14.scheduled
            r2.markMessageAsSendError(r3, r4)
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner
            r3 = 2
            r2.send_state = r3
            org.telegram.messenger.NotificationCenter r2 = r53.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.messageSendError
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            int r5 = r71.getId()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r6 = 0
            r4[r6] = r5
            r2.postNotificationName(r3, r4)
            int r2 = r71.getId()
            r1.processSentMessage(r2)
        L_0x00b3:
            return
        L_0x00b4:
            r25 = r4
            goto L_0x0101
        L_0x00b7:
            r23 = r10
            boolean r10 = r3 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r10 == 0) goto L_0x00fe
            org.telegram.messenger.MessagesController r10 = r53.getMessagesController()
            int r11 = r3.channel_id
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r11)
            if (r10 == 0) goto L_0x00d3
            boolean r11 = r10.megagroup
            if (r11 != 0) goto L_0x00d3
            r11 = 1
            goto L_0x00d4
        L_0x00d3:
            r11 = 0
        L_0x00d4:
            if (r11 == 0) goto L_0x00f0
            r24 = r11
            boolean r11 = r10.has_link
            if (r11 == 0) goto L_0x00ed
            org.telegram.messenger.MessagesController r11 = r53.getMessagesController()
            r25 = r4
            int r4 = r10.id
            org.telegram.tgnet.TLRPC$ChatFull r4 = r11.getChatFull(r4)
            if (r4 == 0) goto L_0x00f4
            int r4 = r4.linked_chat_id
            goto L_0x00f5
        L_0x00ed:
            r25 = r4
            goto L_0x00f4
        L_0x00f0:
            r25 = r4
            r24 = r11
        L_0x00f4:
            r4 = 0
        L_0x00f5:
            boolean r10 = org.telegram.messenger.ChatObject.shouldSendAnonymously(r10)
            r26 = r4
            r11 = r10
            r10 = 0
            goto L_0x0106
        L_0x00fe:
            r25 = r4
            r10 = 0
        L_0x0101:
            r11 = 0
            r24 = 0
            r26 = 0
        L_0x0106:
            java.lang.String r4 = "fwd_id"
            r27 = r11
            java.lang.String r11 = "http"
            r28 = r3
            java.lang.String r3 = "parentObject"
            java.lang.String r1 = "query_id"
            r31 = r11
            if (r14 == 0) goto L_0x032b
            org.telegram.tgnet.TLRPC$Message r11 = r14.messageOwner     // Catch:{ Exception -> 0x0322 }
            if (r78 != 0) goto L_0x0127
            if (r6 == 0) goto L_0x0127
            boolean r27 = r6.containsKey(r3)     // Catch:{ Exception -> 0x0319 }
            if (r27 == 0) goto L_0x0127
            java.lang.Object r27 = r6.get(r3)     // Catch:{ Exception -> 0x0319 }
            goto L_0x0129
        L_0x0127:
            r27 = r78
        L_0x0129:
            boolean r32 = r71.isForwarded()     // Catch:{ Exception -> 0x0319 }
            if (r32 != 0) goto L_0x02eb
            if (r6 == 0) goto L_0x0139
            boolean r32 = r6.containsKey(r4)     // Catch:{ Exception -> 0x0319 }
            if (r32 == 0) goto L_0x0139
            goto L_0x02eb
        L_0x0139:
            boolean r32 = r71.isDice()     // Catch:{ Exception -> 0x0319 }
            if (r32 == 0) goto L_0x0157
            java.lang.String r2 = r71.getDiceEmoji()     // Catch:{ Exception -> 0x0319 }
            r7 = r59
            r32 = r3
            r33 = r4
            r18 = r8
            r19 = r12
            r34 = 11
            r3 = r56
            r4 = r57
        L_0x0153:
            r8 = r60
            goto L_0x0290
        L_0x0157:
            r32 = r3
            int r3 = r14.type     // Catch:{ Exception -> 0x0319 }
            if (r3 == 0) goto L_0x0275
            boolean r3 = r71.isAnimatedEmoji()     // Catch:{ Exception -> 0x0319 }
            if (r3 == 0) goto L_0x0165
            goto L_0x0275
        L_0x0165:
            int r3 = r14.type     // Catch:{ Exception -> 0x0319 }
            r33 = r4
            r4 = 4
            if (r3 != r4) goto L_0x0178
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r11.media     // Catch:{ Exception -> 0x0319 }
            r4 = r57
            r7 = r59
            r19 = r18
            r34 = 1
            goto L_0x028c
        L_0x0178:
            r4 = 1
            if (r3 != r4) goto L_0x0194
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r11.media     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$Photo r3 = r3.photo     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$TL_photo r3 = (org.telegram.tgnet.TLRPC$TL_photo) r3     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0319 }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x0319 }
            if (r4 == 0) goto L_0x0189
            r18 = r4
        L_0x0189:
            r7 = r59
            r4 = r3
            r19 = r18
            r34 = 2
        L_0x0190:
            r3 = r56
            goto L_0x028c
        L_0x0194:
            r4 = 3
            if (r3 == r4) goto L_0x0250
            r4 = 5
            if (r3 == r4) goto L_0x0250
            org.telegram.messenger.VideoEditedInfo r4 = r14.videoEditedInfo     // Catch:{ Exception -> 0x0319 }
            if (r4 == 0) goto L_0x01a0
            goto L_0x0250
        L_0x01a0:
            r4 = 12
            if (r3 != r4) goto L_0x01d9
            org.telegram.tgnet.TLRPC$TL_userRequest_old2 r3 = new org.telegram.tgnet.TLRPC$TL_userRequest_old2     // Catch:{ Exception -> 0x0319 }
            r3.<init>()     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r11.media     // Catch:{ Exception -> 0x0319 }
            java.lang.String r7 = r4.phone_number     // Catch:{ Exception -> 0x0319 }
            r3.phone = r7     // Catch:{ Exception -> 0x0319 }
            java.lang.String r7 = r4.first_name     // Catch:{ Exception -> 0x0319 }
            r3.first_name = r7     // Catch:{ Exception -> 0x0319 }
            java.lang.String r4 = r4.last_name     // Catch:{ Exception -> 0x0319 }
            r3.last_name = r4     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r4 = new org.telegram.tgnet.TLRPC$TL_restrictionReason     // Catch:{ Exception -> 0x0319 }
            r4.<init>()     // Catch:{ Exception -> 0x0319 }
            r4.platform = r12     // Catch:{ Exception -> 0x0319 }
            r4.reason = r12     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r11.media     // Catch:{ Exception -> 0x0319 }
            java.lang.String r7 = r7.vcard     // Catch:{ Exception -> 0x0319 }
            r4.text = r7     // Catch:{ Exception -> 0x0319 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r7 = r3.restriction_reason     // Catch:{ Exception -> 0x0319 }
            r7.add(r4)     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r11.media     // Catch:{ Exception -> 0x0319 }
            int r4 = r4.user_id     // Catch:{ Exception -> 0x0319 }
            r3.id = r4     // Catch:{ Exception -> 0x0319 }
            r4 = r57
            r7 = r3
            r19 = r18
            r34 = 6
            goto L_0x0190
        L_0x01d9:
            r4 = 8
            if (r3 == r4) goto L_0x0232
            r4 = 9
            if (r3 == r4) goto L_0x0232
            r4 = 13
            if (r3 == r4) goto L_0x0232
            r4 = 14
            if (r3 == r4) goto L_0x0232
            r4 = 15
            if (r3 != r4) goto L_0x01ee
            goto L_0x0232
        L_0x01ee:
            r4 = 2
            if (r3 != r4) goto L_0x0211
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r11.media     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0319 }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x0319 }
            if (r4 == 0) goto L_0x0207
            r7 = r59
            r19 = r4
            r18 = r8
            r34 = 8
            goto L_0x0264
        L_0x0207:
            r4 = r57
            r7 = r59
            r19 = r18
            r34 = 8
            goto L_0x026f
        L_0x0211:
            r4 = 17
            if (r3 != r4) goto L_0x0226
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r11.media     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3     // Catch:{ Exception -> 0x0319 }
            r4 = r57
            r7 = r59
            r8 = r60
            r19 = r18
            r34 = 10
            r18 = r3
            goto L_0x0272
        L_0x0226:
            r3 = r56
            r4 = r57
            r7 = r59
            r19 = r18
            r34 = -1
            goto L_0x028c
        L_0x0232:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r11.media     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0319 }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x0319 }
            if (r4 == 0) goto L_0x0247
            r7 = r59
            r19 = r4
            r18 = r8
            r34 = 7
            goto L_0x0264
        L_0x0247:
            r4 = r57
            r7 = r59
            r19 = r18
            r34 = 7
            goto L_0x026f
        L_0x0250:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r11.media     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$TL_document r3 = (org.telegram.tgnet.TLRPC$TL_document) r3     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0319 }
            java.lang.String r4 = r4.message     // Catch:{ Exception -> 0x0319 }
            if (r4 == 0) goto L_0x0267
            r7 = r59
            r19 = r4
            r18 = r8
            r34 = 3
        L_0x0264:
            r4 = r57
            goto L_0x0271
        L_0x0267:
            r4 = r57
            r7 = r59
            r19 = r18
            r34 = 3
        L_0x026f:
            r18 = r8
        L_0x0271:
            r8 = r3
        L_0x0272:
            r3 = r56
            goto L_0x0290
        L_0x0275:
            r33 = r4
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x0319 }
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media     // Catch:{ Exception -> 0x0319 }
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x0319 }
            if (r3 == 0) goto L_0x0280
            goto L_0x0282
        L_0x0280:
            java.lang.String r2 = r11.message     // Catch:{ Exception -> 0x0319 }
        L_0x0282:
            r3 = r56
            r4 = r57
            r7 = r59
            r19 = r18
            r34 = 0
        L_0x028c:
            r18 = r8
            goto L_0x0153
        L_0x0290:
            if (r6 == 0) goto L_0x029d
            boolean r35 = r6.containsKey(r1)     // Catch:{ Exception -> 0x0319 }
            if (r35 == 0) goto L_0x029d
            r54 = r2
            r34 = 9
            goto L_0x029f
        L_0x029d:
            r54 = r2
        L_0x029f:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r11.media     // Catch:{ Exception -> 0x0319 }
            int r2 = r2.ttl_seconds     // Catch:{ Exception -> 0x0319 }
            r36 = r1
            r56 = r4
            if (r2 <= 0) goto L_0x02c8
            r37 = r8
            r13 = r11
            r39 = r15
            r38 = r18
            r40 = r19
            r1 = r23
            r61 = r27
            r63 = r34
            r19 = 0
            r23 = 0
            r4 = r53
            r15 = r2
            r18 = r3
            r11 = r6
            r2 = r7
            r6 = r28
            r7 = r64
            goto L_0x02e7
        L_0x02c8:
            r2 = r7
            r37 = r8
            r13 = r11
            r39 = r15
            r38 = r18
            r40 = r19
            r1 = r23
            r61 = r27
            r63 = r34
            r19 = 0
            r23 = 0
            r4 = r53
            r7 = r64
            r15 = r77
            r18 = r3
            r11 = r6
            r6 = r28
        L_0x02e7:
            r3 = r75
            goto L_0x081e
        L_0x02eb:
            r32 = r3
            r33 = r4
            r4 = r53
            r37 = r60
            r3 = r75
            r36 = r1
            r54 = r2
            r38 = r8
            r13 = r11
            r39 = r15
            r40 = r18
            r1 = r23
            r61 = r27
            r63 = 4
            r19 = 0
            r23 = 0
            r18 = r56
            r56 = r57
            r2 = r59
            r7 = r64
            r15 = r77
            r11 = r6
            r6 = r28
            goto L_0x081e
        L_0x0319:
            r0 = move-exception
            r4 = r53
            r3 = r0
            r1 = r9
        L_0x031e:
            r5 = r11
        L_0x031f:
            r2 = 0
            goto L_0x1CLASSNAME
        L_0x0322:
            r0 = move-exception
            r4 = r53
            r3 = r0
            r1 = r9
            r2 = 0
            r5 = 0
            goto L_0x1CLASSNAME
        L_0x032b:
            r32 = r3
            r33 = r4
            if (r5 >= 0) goto L_0x0343
            org.telegram.messenger.MessagesController r3 = r53.getMessagesController()     // Catch:{ Exception -> 0x0322 }
            int r4 = -r5
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0322 }
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)     // Catch:{ Exception -> 0x0322 }
            boolean r3 = org.telegram.messenger.ChatObject.canSendStickers(r3)     // Catch:{ Exception -> 0x0322 }
            goto L_0x0344
        L_0x0343:
            r3 = 1
        L_0x0344:
            if (r2 == 0) goto L_0x03ef
            if (r10 == 0) goto L_0x034e
            org.telegram.tgnet.TLRPC$TL_message_secret r4 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0322 }
            r4.<init>()     // Catch:{ Exception -> 0x0322 }
            goto L_0x0353
        L_0x034e:
            org.telegram.tgnet.TLRPC$TL_message r4 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0322 }
            r4.<init>()     // Catch:{ Exception -> 0x0322 }
        L_0x0353:
            if (r10 == 0) goto L_0x0372
            boolean r7 = r15 instanceof org.telegram.tgnet.TLRPC$TL_webPagePending     // Catch:{ Exception -> 0x0369 }
            if (r7 == 0) goto L_0x0372
            java.lang.String r7 = r15.url     // Catch:{ Exception -> 0x0369 }
            if (r7 == 0) goto L_0x0367
            org.telegram.tgnet.TLRPC$TL_webPageUrlPending r7 = new org.telegram.tgnet.TLRPC$TL_webPageUrlPending     // Catch:{ Exception -> 0x0369 }
            r7.<init>()     // Catch:{ Exception -> 0x0369 }
            java.lang.String r11 = r15.url     // Catch:{ Exception -> 0x0369 }
            r7.url = r11     // Catch:{ Exception -> 0x0369 }
            goto L_0x0373
        L_0x0367:
            r7 = 0
            goto L_0x0373
        L_0x0369:
            r0 = move-exception
            r3 = r0
            r5 = r4
            r1 = r9
            r2 = 0
            r4 = r53
            goto L_0x1CLASSNAME
        L_0x0372:
            r7 = r15
        L_0x0373:
            if (r3 == 0) goto L_0x03b8
            int r3 = r54.length()     // Catch:{ Exception -> 0x0369 }
            r11 = 30
            if (r3 >= r11) goto L_0x03b8
            if (r7 != 0) goto L_0x03b8
            if (r13 == 0) goto L_0x0387
            boolean r3 = r72.isEmpty()     // Catch:{ Exception -> 0x0369 }
            if (r3 == 0) goto L_0x03b8
        L_0x0387:
            org.telegram.messenger.MessagesController r3 = r53.getMessagesController()     // Catch:{ Exception -> 0x0369 }
            java.util.HashSet<java.lang.String> r3 = r3.diceEmojies     // Catch:{ Exception -> 0x0369 }
            java.lang.String r11 = "️"
            java.lang.String r11 = r2.replace(r11, r12)     // Catch:{ Exception -> 0x0369 }
            boolean r3 = r3.contains(r11)     // Catch:{ Exception -> 0x0369 }
            if (r3 == 0) goto L_0x03b8
            if (r10 != 0) goto L_0x03b8
            if (r9 != 0) goto L_0x03b8
            org.telegram.tgnet.TLRPC$TL_messageMediaDice r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaDice     // Catch:{ Exception -> 0x0369 }
            r3.<init>()     // Catch:{ Exception -> 0x0369 }
            r3.emoticon = r2     // Catch:{ Exception -> 0x0369 }
            r11 = -1
            r3.value = r11     // Catch:{ Exception -> 0x0369 }
            r4.media = r3     // Catch:{ Exception -> 0x0369 }
            r11 = r66
            r36 = r1
            r3 = r4
            r2 = r6
            r15 = r7
            r6 = r12
            r1 = r23
            r18 = 0
            r19 = 11
            goto L_0x03e7
        L_0x03b8:
            if (r7 != 0) goto L_0x03c2
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ Exception -> 0x0369 }
            r3.<init>()     // Catch:{ Exception -> 0x0369 }
            r4.media = r3     // Catch:{ Exception -> 0x0369 }
            goto L_0x03cb
        L_0x03c2:
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage     // Catch:{ Exception -> 0x0369 }
            r3.<init>()     // Catch:{ Exception -> 0x0369 }
            r4.media = r3     // Catch:{ Exception -> 0x0369 }
            r3.webpage = r7     // Catch:{ Exception -> 0x0369 }
        L_0x03cb:
            if (r6 == 0) goto L_0x03d6
            boolean r3 = r6.containsKey(r1)     // Catch:{ Exception -> 0x0369 }
            if (r3 == 0) goto L_0x03d6
            r19 = 9
            goto L_0x03d8
        L_0x03d6:
            r19 = 0
        L_0x03d8:
            r4.message = r2     // Catch:{ Exception -> 0x0369 }
            r11 = r66
            r36 = r1
            r3 = r4
            r2 = r6
            r15 = r7
            r6 = r18
            r1 = r23
            r18 = 0
        L_0x03e7:
            r23 = 0
            r4 = r53
            r7 = r64
            goto L_0x078f
        L_0x03ef:
            if (r8 == 0) goto L_0x041c
            if (r10 == 0) goto L_0x03f9
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0322 }
            r3.<init>()     // Catch:{ Exception -> 0x0322 }
            goto L_0x03fe
        L_0x03f9:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0322 }
            r3.<init>()     // Catch:{ Exception -> 0x0322 }
        L_0x03fe:
            r3.media = r8     // Catch:{ Exception -> 0x0413 }
            r4 = r53
            r7 = r64
            r11 = r66
            r36 = r1
            r2 = r6
            r6 = r18
            r1 = r23
            r18 = 0
            r19 = 10
            goto L_0x078d
        L_0x0413:
            r0 = move-exception
            r4 = r53
        L_0x0416:
            r5 = r3
            r1 = r9
        L_0x0418:
            r2 = 0
        L_0x0419:
            r3 = r0
            goto L_0x1CLASSNAME
        L_0x041c:
            r4 = r56
            if (r4 == 0) goto L_0x045d
            if (r10 == 0) goto L_0x0428
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0322 }
            r3.<init>()     // Catch:{ Exception -> 0x0322 }
            goto L_0x042d
        L_0x0428:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0322 }
            r3.<init>()     // Catch:{ Exception -> 0x0322 }
        L_0x042d:
            r3.media = r4     // Catch:{ Exception -> 0x0413 }
            if (r6 == 0) goto L_0x044a
            boolean r7 = r6.containsKey(r1)     // Catch:{ Exception -> 0x0413 }
            if (r7 == 0) goto L_0x044a
            r4 = r53
            r7 = r64
            r11 = r66
        L_0x043d:
            r36 = r1
            r2 = r6
            r6 = r18
            r1 = r23
            r18 = 0
            r19 = 9
            goto L_0x078d
        L_0x044a:
            r4 = r53
            r7 = r64
            r11 = r66
            r36 = r1
            r2 = r6
            r6 = r18
            r1 = r23
            r18 = 0
            r19 = 1
            goto L_0x078d
        L_0x045d:
            r7 = r57
            if (r7 == 0) goto L_0x04d5
            if (r10 == 0) goto L_0x0469
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0322 }
            r3.<init>()     // Catch:{ Exception -> 0x0322 }
            goto L_0x046e
        L_0x0469:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0322 }
            r3.<init>()     // Catch:{ Exception -> 0x0322 }
        L_0x046e:
            org.telegram.tgnet.TLRPC$TL_messageMediaPhoto r11 = new org.telegram.tgnet.TLRPC$TL_messageMediaPhoto     // Catch:{ Exception -> 0x0413 }
            r11.<init>()     // Catch:{ Exception -> 0x0413 }
            r3.media = r11     // Catch:{ Exception -> 0x0413 }
            int r2 = r11.flags     // Catch:{ Exception -> 0x0413 }
            r19 = 3
            r2 = r2 | 3
            r11.flags = r2     // Catch:{ Exception -> 0x0413 }
            if (r13 == 0) goto L_0x0481
            r3.entities = r13     // Catch:{ Exception -> 0x0413 }
        L_0x0481:
            r4 = r77
            if (r4 == 0) goto L_0x048f
            r11.ttl_seconds = r4     // Catch:{ Exception -> 0x0413 }
            r3.ttl = r4     // Catch:{ Exception -> 0x0413 }
            r19 = 4
            r2 = r2 | 4
            r11.flags = r2     // Catch:{ Exception -> 0x0413 }
        L_0x048f:
            r11.photo = r7     // Catch:{ Exception -> 0x0413 }
            if (r6 == 0) goto L_0x049e
            boolean r2 = r6.containsKey(r1)     // Catch:{ Exception -> 0x0413 }
            if (r2 == 0) goto L_0x049e
            r11 = r66
            r19 = 9
            goto L_0x04a2
        L_0x049e:
            r11 = r66
            r19 = 2
        L_0x04a2:
            if (r11 == 0) goto L_0x04b9
            int r2 = r66.length()     // Catch:{ Exception -> 0x0413 }
            if (r2 <= 0) goto L_0x04b9
            r2 = r31
            boolean r31 = r11.startsWith(r2)     // Catch:{ Exception -> 0x0413 }
            if (r31 == 0) goto L_0x04b7
            r3.attachPath = r11     // Catch:{ Exception -> 0x0413 }
            r31 = r2
            goto L_0x050d
        L_0x04b7:
            r31 = r2
        L_0x04b9:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r7.sizes     // Catch:{ Exception -> 0x0413 }
            int r34 = r2.size()     // Catch:{ Exception -> 0x0413 }
            r4 = 1
            int r7 = r34 + -1
            java.lang.Object r2 = r2.get(r7)     // Catch:{ Exception -> 0x0413 }
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2     // Catch:{ Exception -> 0x0413 }
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.location     // Catch:{ Exception -> 0x0413 }
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r4)     // Catch:{ Exception -> 0x0413 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0413 }
            r3.attachPath = r2     // Catch:{ Exception -> 0x0413 }
            goto L_0x050d
        L_0x04d5:
            r2 = r61
            r11 = r66
            r4 = r77
            if (r2 == 0) goto L_0x04f4
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0322 }
            r3.<init>()     // Catch:{ Exception -> 0x0322 }
            org.telegram.tgnet.TLRPC$TL_messageMediaGame r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaGame     // Catch:{ Exception -> 0x0413 }
            r7.<init>()     // Catch:{ Exception -> 0x0413 }
            r3.media = r7     // Catch:{ Exception -> 0x0413 }
            r7.game = r2     // Catch:{ Exception -> 0x0413 }
            if (r6 == 0) goto L_0x050d
            boolean r2 = r6.containsKey(r1)     // Catch:{ Exception -> 0x0413 }
            if (r2 == 0) goto L_0x050d
            goto L_0x0507
        L_0x04f4:
            r2 = r63
            if (r2 == 0) goto L_0x051a
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0322 }
            r3.<init>()     // Catch:{ Exception -> 0x0322 }
            r3.media = r2     // Catch:{ Exception -> 0x0413 }
            if (r6 == 0) goto L_0x050d
            boolean r2 = r6.containsKey(r1)     // Catch:{ Exception -> 0x0413 }
            if (r2 == 0) goto L_0x050d
        L_0x0507:
            r4 = r53
            r7 = r64
            goto L_0x043d
        L_0x050d:
            r4 = r53
            r7 = r64
            r36 = r1
            r2 = r6
            r6 = r18
            r1 = r23
            goto L_0x078b
        L_0x051a:
            r2 = r59
            r7 = 0
            if (r2 == 0) goto L_0x05b0
            if (r10 == 0) goto L_0x0530
            org.telegram.tgnet.TLRPC$TL_message_secret r3 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0527 }
            r3.<init>()     // Catch:{ Exception -> 0x0527 }
            goto L_0x0535
        L_0x0527:
            r0 = move-exception
            r4 = r53
            r3 = r0
            r2 = r7
            r5 = r2
            r1 = r9
            goto L_0x1CLASSNAME
        L_0x0530:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0322 }
            r3.<init>()     // Catch:{ Exception -> 0x0322 }
        L_0x0535:
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaContact     // Catch:{ Exception -> 0x0413 }
            r7.<init>()     // Catch:{ Exception -> 0x0413 }
            r3.media = r7     // Catch:{ Exception -> 0x0413 }
            java.lang.String r8 = r2.phone     // Catch:{ Exception -> 0x0413 }
            r7.phone_number = r8     // Catch:{ Exception -> 0x0413 }
            java.lang.String r8 = r2.first_name     // Catch:{ Exception -> 0x0413 }
            r7.first_name = r8     // Catch:{ Exception -> 0x0413 }
            java.lang.String r8 = r2.last_name     // Catch:{ Exception -> 0x0413 }
            r7.last_name = r8     // Catch:{ Exception -> 0x0413 }
            int r8 = r2.id     // Catch:{ Exception -> 0x0413 }
            r7.user_id = r8     // Catch:{ Exception -> 0x0413 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r7 = r2.restriction_reason     // Catch:{ Exception -> 0x0413 }
            boolean r7 = r7.isEmpty()     // Catch:{ Exception -> 0x0413 }
            if (r7 != 0) goto L_0x0577
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r7 = r2.restriction_reason     // Catch:{ Exception -> 0x0413 }
            r8 = 0
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0413 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r7 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r7     // Catch:{ Exception -> 0x0413 }
            java.lang.String r7 = r7.text     // Catch:{ Exception -> 0x0413 }
            java.lang.String r8 = "BEGIN:VCARD"
            boolean r7 = r7.startsWith(r8)     // Catch:{ Exception -> 0x0413 }
            if (r7 == 0) goto L_0x0577
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r3.media     // Catch:{ Exception -> 0x0413 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r8 = r2.restriction_reason     // Catch:{ Exception -> 0x0413 }
            r15 = 0
            java.lang.Object r8 = r8.get(r15)     // Catch:{ Exception -> 0x0413 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r8 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r8     // Catch:{ Exception -> 0x0413 }
            java.lang.String r8 = r8.text     // Catch:{ Exception -> 0x0413 }
            r7.vcard = r8     // Catch:{ Exception -> 0x0413 }
            goto L_0x057b
        L_0x0577:
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r3.media     // Catch:{ Exception -> 0x0413 }
            r7.vcard = r12     // Catch:{ Exception -> 0x0413 }
        L_0x057b:
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r3.media     // Catch:{ Exception -> 0x0413 }
            java.lang.String r8 = r7.first_name     // Catch:{ Exception -> 0x0413 }
            if (r8 != 0) goto L_0x0585
            r7.first_name = r12     // Catch:{ Exception -> 0x0413 }
            r2.first_name = r12     // Catch:{ Exception -> 0x0413 }
        L_0x0585:
            java.lang.String r8 = r7.last_name     // Catch:{ Exception -> 0x0413 }
            if (r8 != 0) goto L_0x058d
            r7.last_name = r12     // Catch:{ Exception -> 0x0413 }
            r2.last_name = r12     // Catch:{ Exception -> 0x0413 }
        L_0x058d:
            if (r6 == 0) goto L_0x059d
            boolean r7 = r6.containsKey(r1)     // Catch:{ Exception -> 0x0413 }
            if (r7 == 0) goto L_0x059d
            r4 = r53
            r7 = r64
            r15 = r69
            goto L_0x043d
        L_0x059d:
            r4 = r53
            r7 = r64
            r15 = r69
            r36 = r1
            r2 = r6
            r6 = r18
            r1 = r23
            r18 = 0
            r19 = 6
            goto L_0x078d
        L_0x05b0:
            r7 = r6
            r6 = r60
            if (r6 == 0) goto L_0x077c
            if (r10 == 0) goto L_0x05bd
            org.telegram.tgnet.TLRPC$TL_message_secret r8 = new org.telegram.tgnet.TLRPC$TL_message_secret     // Catch:{ Exception -> 0x0322 }
            r8.<init>()     // Catch:{ Exception -> 0x0322 }
            goto L_0x05c2
        L_0x05bd:
            org.telegram.tgnet.TLRPC$TL_message r8 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ Exception -> 0x0322 }
            r8.<init>()     // Catch:{ Exception -> 0x0322 }
        L_0x05c2:
            if (r5 >= 0) goto L_0x05ed
            if (r3 != 0) goto L_0x05ed
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r6.attributes     // Catch:{ Exception -> 0x05e5 }
            int r3 = r3.size()     // Catch:{ Exception -> 0x05e5 }
            r15 = 0
        L_0x05cd:
            if (r15 >= r3) goto L_0x05ed
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r6.attributes     // Catch:{ Exception -> 0x05e5 }
            java.lang.Object r2 = r2.get(r15)     // Catch:{ Exception -> 0x05e5 }
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x05e5 }
            if (r2 == 0) goto L_0x05e0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r6.attributes     // Catch:{ Exception -> 0x05e5 }
            r2.remove(r15)     // Catch:{ Exception -> 0x05e5 }
            r2 = 1
            goto L_0x05ee
        L_0x05e0:
            int r15 = r15 + 1
            r2 = r59
            goto L_0x05cd
        L_0x05e5:
            r0 = move-exception
            r4 = r53
            r3 = r0
            r5 = r8
            r1 = r9
            goto L_0x031f
        L_0x05ed:
            r2 = 0
        L_0x05ee:
            org.telegram.tgnet.TLRPC$TL_messageMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument     // Catch:{ Exception -> 0x0771 }
            r3.<init>()     // Catch:{ Exception -> 0x0771 }
            r8.media = r3     // Catch:{ Exception -> 0x0771 }
            int r15 = r3.flags     // Catch:{ Exception -> 0x0771 }
            r19 = 3
            r15 = r15 | 3
            r3.flags = r15     // Catch:{ Exception -> 0x0771 }
            if (r4 == 0) goto L_0x0609
            r3.ttl_seconds = r4     // Catch:{ Exception -> 0x05e5 }
            r8.ttl = r4     // Catch:{ Exception -> 0x05e5 }
            r19 = 4
            r15 = r15 | 4
            r3.flags = r15     // Catch:{ Exception -> 0x05e5 }
        L_0x0609:
            r3.document = r6     // Catch:{ Exception -> 0x0771 }
            if (r7 == 0) goto L_0x0616
            boolean r3 = r7.containsKey(r1)     // Catch:{ Exception -> 0x05e5 }
            if (r3 == 0) goto L_0x0616
            r19 = 9
            goto L_0x0633
        L_0x0616:
            boolean r3 = org.telegram.messenger.MessageObject.isVideoDocument(r60)     // Catch:{ Exception -> 0x0771 }
            if (r3 != 0) goto L_0x0631
            boolean r3 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r60)     // Catch:{ Exception -> 0x05e5 }
            if (r3 != 0) goto L_0x0631
            if (r58 == 0) goto L_0x0625
            goto L_0x0631
        L_0x0625:
            boolean r3 = org.telegram.messenger.MessageObject.isVoiceDocument(r60)     // Catch:{ Exception -> 0x05e5 }
            if (r3 == 0) goto L_0x062e
            r19 = 8
            goto L_0x0633
        L_0x062e:
            r19 = 7
            goto L_0x0633
        L_0x0631:
            r19 = 3
        L_0x0633:
            if (r58 == 0) goto L_0x0645
            java.lang.String r3 = r58.getString()     // Catch:{ Exception -> 0x05e5 }
            if (r7 != 0) goto L_0x0640
            java.util.HashMap r7 = new java.util.HashMap     // Catch:{ Exception -> 0x05e5 }
            r7.<init>()     // Catch:{ Exception -> 0x05e5 }
        L_0x0640:
            java.lang.String r15 = "ve"
            r7.put(r15, r3)     // Catch:{ Exception -> 0x05e5 }
        L_0x0645:
            if (r10 == 0) goto L_0x0663
            int r3 = r6.dc_id     // Catch:{ Exception -> 0x05e5 }
            if (r3 <= 0) goto L_0x0663
            boolean r3 = org.telegram.messenger.MessageObject.isStickerDocument(r60)     // Catch:{ Exception -> 0x05e5 }
            if (r3 != 0) goto L_0x0663
            r3 = 1
            boolean r15 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r6, r3)     // Catch:{ Exception -> 0x05e5 }
            if (r15 != 0) goto L_0x0663
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r60)     // Catch:{ Exception -> 0x05e5 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x05e5 }
            r8.attachPath = r3     // Catch:{ Exception -> 0x05e5 }
            goto L_0x0665
        L_0x0663:
            r8.attachPath = r11     // Catch:{ Exception -> 0x0771 }
        L_0x0665:
            if (r10 == 0) goto L_0x0755
            boolean r3 = org.telegram.messenger.MessageObject.isStickerDocument(r60)     // Catch:{ Exception -> 0x0771 }
            if (r3 != 0) goto L_0x0674
            r3 = 1
            boolean r15 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r6, r3)     // Catch:{ Exception -> 0x05e5 }
            if (r15 == 0) goto L_0x0755
        L_0x0674:
            r3 = 0
        L_0x0675:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r15 = r6.attributes     // Catch:{ Exception -> 0x0771 }
            int r15 = r15.size()     // Catch:{ Exception -> 0x0771 }
            if (r3 >= r15) goto L_0x0755
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r15 = r6.attributes     // Catch:{ Exception -> 0x0771 }
            java.lang.Object r15 = r15.get(r3)     // Catch:{ Exception -> 0x0771 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r15 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r15     // Catch:{ Exception -> 0x0771 }
            r34 = r1
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeSticker     // Catch:{ Exception -> 0x0771 }
            if (r1 == 0) goto L_0x0736
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r6.attributes     // Catch:{ Exception -> 0x0771 }
            r1.remove(r3)     // Catch:{ Exception -> 0x0771 }
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55 r1 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker_layer55     // Catch:{ Exception -> 0x0771 }
            r1.<init>()     // Catch:{ Exception -> 0x0771 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r6.attributes     // Catch:{ Exception -> 0x0771 }
            r3.add(r1)     // Catch:{ Exception -> 0x0771 }
            java.lang.String r3 = r15.alt     // Catch:{ Exception -> 0x0771 }
            r1.alt = r3     // Catch:{ Exception -> 0x0771 }
            org.telegram.tgnet.TLRPC$InputStickerSet r3 = r15.stickerset     // Catch:{ Exception -> 0x0771 }
            if (r3 == 0) goto L_0x071c
            r61 = r2
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x0771 }
            if (r2 == 0) goto L_0x06ab
            java.lang.String r2 = r3.short_name     // Catch:{ Exception -> 0x05e5 }
            goto L_0x06b7
        L_0x06ab:
            org.telegram.messenger.MediaDataController r2 = r53.getMediaDataController()     // Catch:{ Exception -> 0x0771 }
            org.telegram.tgnet.TLRPC$InputStickerSet r3 = r15.stickerset     // Catch:{ Exception -> 0x0771 }
            long r3 = r3.id     // Catch:{ Exception -> 0x0771 }
            java.lang.String r2 = r2.getStickerSetName(r3)     // Catch:{ Exception -> 0x0771 }
        L_0x06b7:
            boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0771 }
            if (r3 != 0) goto L_0x06d5
            org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName r3 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName     // Catch:{ Exception -> 0x05e5 }
            r3.<init>()     // Catch:{ Exception -> 0x05e5 }
            r1.stickerset = r3     // Catch:{ Exception -> 0x05e5 }
            r3.short_name = r2     // Catch:{ Exception -> 0x05e5 }
            r2 = 0
            r6 = 5
            r4 = r53
            r63 = r7
            r3 = r23
            r36 = r34
            r23 = r8
            r7 = r64
            goto L_0x070d
        L_0x06d5:
            org.telegram.tgnet.TLRPC$InputStickerSet r2 = r15.stickerset     // Catch:{ Exception -> 0x0771 }
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetID     // Catch:{ Exception -> 0x0771 }
            if (r2 == 0) goto L_0x06f8
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r2 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0771 }
            r4 = r53
            r63 = r7
            r3 = r23
            r36 = r34
            r23 = r8
            r7 = r64
            r2.<init>(r7)     // Catch:{ Exception -> 0x0734 }
            r2.encryptedChat = r10     // Catch:{ Exception -> 0x0734 }
            r2.locationParent = r1     // Catch:{ Exception -> 0x0734 }
            r6 = 5
            r2.type = r6     // Catch:{ Exception -> 0x0734 }
            org.telegram.tgnet.TLRPC$InputStickerSet r15 = r15.stickerset     // Catch:{ Exception -> 0x0734 }
            r2.parentObject = r15     // Catch:{ Exception -> 0x0734 }
            goto L_0x0706
        L_0x06f8:
            r4 = r53
            r63 = r7
            r3 = r23
            r36 = r34
            r6 = 5
            r23 = r8
            r7 = r64
            r2 = 0
        L_0x0706:
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r15 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0734 }
            r15.<init>()     // Catch:{ Exception -> 0x0734 }
            r1.stickerset = r15     // Catch:{ Exception -> 0x0734 }
        L_0x070d:
            r15 = r69
            r1 = r3
            r6 = r18
            r3 = r23
            r23 = r61
            r18 = r2
            r2 = r63
            goto L_0x078f
        L_0x071c:
            r4 = r53
            r61 = r2
            r63 = r7
            r3 = r23
            r36 = r34
            r6 = 5
            r23 = r8
            r7 = r64
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty     // Catch:{ Exception -> 0x0734 }
            r2.<init>()     // Catch:{ Exception -> 0x0734 }
            r1.stickerset = r2     // Catch:{ Exception -> 0x0734 }
            r1 = r3
            goto L_0x0764
        L_0x0734:
            r0 = move-exception
            goto L_0x0776
        L_0x0736:
            r4 = r53
            r61 = r2
            r63 = r7
            r1 = r23
            r36 = r34
            r6 = 5
            r23 = r8
            r7 = r64
            int r3 = r3 + 1
            r6 = r60
            r7 = r63
            r4 = r77
            r8 = r23
            r23 = r1
            r1 = r36
            goto L_0x0675
        L_0x0755:
            r6 = 5
            r4 = r53
            r36 = r1
            r61 = r2
            r63 = r7
            r1 = r23
            r23 = r8
            r7 = r64
        L_0x0764:
            r2 = r63
            r15 = r69
            r6 = r18
            r3 = r23
            r18 = 0
            r23 = r61
            goto L_0x078f
        L_0x0771:
            r0 = move-exception
            r23 = r8
            r4 = r53
        L_0x0776:
            r3 = r0
            r1 = r9
            r5 = r23
            goto L_0x031f
        L_0x077c:
            r4 = r53
            r7 = r64
            r36 = r1
            r1 = r23
            r15 = r69
            r2 = r74
            r6 = r18
            r3 = 0
        L_0x078b:
            r18 = 0
        L_0x078d:
            r23 = 0
        L_0x078f:
            if (r13 == 0) goto L_0x07a5
            boolean r34 = r72.isEmpty()     // Catch:{ Exception -> 0x07a2 }
            if (r34 != 0) goto L_0x07a5
            r3.entities = r13     // Catch:{ Exception -> 0x07a2 }
            r61 = r2
            int r2 = r3.flags     // Catch:{ Exception -> 0x07a2 }
            r2 = r2 | 128(0x80, float:1.794E-43)
            r3.flags = r2     // Catch:{ Exception -> 0x07a2 }
            goto L_0x07a7
        L_0x07a2:
            r0 = move-exception
            goto L_0x0416
        L_0x07a5:
            r61 = r2
        L_0x07a7:
            if (r6 == 0) goto L_0x07ac
            r3.message = r6     // Catch:{ Exception -> 0x07a2 }
            goto L_0x07b2
        L_0x07ac:
            java.lang.String r2 = r3.message     // Catch:{ Exception -> 0x1c5c }
            if (r2 != 0) goto L_0x07b2
            r3.message = r12     // Catch:{ Exception -> 0x07a2 }
        L_0x07b2:
            java.lang.String r2 = r3.attachPath     // Catch:{ Exception -> 0x1c5c }
            if (r2 != 0) goto L_0x07b8
            r3.attachPath = r12     // Catch:{ Exception -> 0x07a2 }
        L_0x07b8:
            org.telegram.messenger.UserConfig r2 = r53.getUserConfig()     // Catch:{ Exception -> 0x1c5c }
            int r2 = r2.getNewMessageId()     // Catch:{ Exception -> 0x1c5c }
            r3.id = r2     // Catch:{ Exception -> 0x1c5c }
            r3.local_id = r2     // Catch:{ Exception -> 0x1c5c }
            r2 = 1
            r3.out = r2     // Catch:{ Exception -> 0x1c5c }
            if (r24 == 0) goto L_0x07db
            if (r28 == 0) goto L_0x07db
            org.telegram.tgnet.TLRPC$TL_peerChannel r2 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ Exception -> 0x07a2 }
            r2.<init>()     // Catch:{ Exception -> 0x07a2 }
            r3.from_id = r2     // Catch:{ Exception -> 0x07a2 }
            r63 = r6
            r6 = r28
            int r13 = r6.channel_id     // Catch:{ Exception -> 0x07a2 }
            r2.channel_id = r13     // Catch:{ Exception -> 0x07a2 }
            goto L_0x07fb
        L_0x07db:
            r63 = r6
            r6 = r28
            if (r27 == 0) goto L_0x07ec
            org.telegram.messenger.MessagesController r2 = r53.getMessagesController()     // Catch:{ Exception -> 0x07a2 }
            org.telegram.tgnet.TLRPC$Peer r2 = r2.getPeer(r5)     // Catch:{ Exception -> 0x07a2 }
            r3.from_id = r2     // Catch:{ Exception -> 0x07a2 }
            goto L_0x07fb
        L_0x07ec:
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x1c5c }
            r2.<init>()     // Catch:{ Exception -> 0x1c5c }
            r3.from_id = r2     // Catch:{ Exception -> 0x1c5c }
            r2.user_id = r1     // Catch:{ Exception -> 0x1c5c }
            int r2 = r3.flags     // Catch:{ Exception -> 0x1c5c }
            r2 = r2 | 256(0x100, float:3.59E-43)
            r3.flags = r2     // Catch:{ Exception -> 0x1c5c }
        L_0x07fb:
            org.telegram.messenger.UserConfig r2 = r53.getUserConfig()     // Catch:{ Exception -> 0x1c5c }
            r13 = 0
            r2.saveConfig(r13)     // Catch:{ Exception -> 0x1c5c }
            r2 = r59
            r37 = r60
            r11 = r61
            r38 = r62
            r40 = r63
            r61 = r78
            r13 = r3
            r39 = r15
            r63 = r19
            r3 = r75
            r15 = r77
            r19 = r18
            r18 = r56
            r56 = r57
        L_0x081e:
            if (r3 == 0) goto L_0x084b
            r57 = r2
            int r2 = r4.currentAccount     // Catch:{ Exception -> 0x0845 }
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2)     // Catch:{ Exception -> 0x0845 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0845 }
            r3.<init>()     // Catch:{ Exception -> 0x0845 }
            r59 = r15
            java.lang.String r15 = "silent_"
            r3.append(r15)     // Catch:{ Exception -> 0x0845 }
            r3.append(r7)     // Catch:{ Exception -> 0x0845 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0845 }
            r15 = 0
            boolean r2 = r2.getBoolean(r3, r15)     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x0843
            goto L_0x084f
        L_0x0843:
            r2 = 0
            goto L_0x0850
        L_0x0845:
            r0 = move-exception
            r3 = r0
            r1 = r9
            r5 = r13
            goto L_0x031f
        L_0x084b:
            r57 = r2
            r59 = r15
        L_0x084f:
            r2 = 1
        L_0x0850:
            r13.silent = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            long r2 = r13.random_id     // Catch:{ Exception -> 0x1CLASSNAME }
            int r15 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r15 != 0) goto L_0x085e
            long r2 = r53.getNextRandomId()     // Catch:{ Exception -> 0x0845 }
            r13.random_id = r2     // Catch:{ Exception -> 0x0845 }
        L_0x085e:
            java.lang.String r15 = "bot_name"
            if (r11 == 0) goto L_0x0891
            java.lang.String r2 = "bot"
            boolean r2 = r11.containsKey(r2)     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x0891
            if (r10 == 0) goto L_0x0879
            java.lang.Object r2 = r11.get(r15)     // Catch:{ Exception -> 0x0845 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0845 }
            r13.via_bot_name = r2     // Catch:{ Exception -> 0x0845 }
            if (r2 != 0) goto L_0x088b
            r13.via_bot_name = r12     // Catch:{ Exception -> 0x0845 }
            goto L_0x088b
        L_0x0879:
            java.lang.String r2 = "bot"
            java.lang.Object r2 = r11.get(r2)     // Catch:{ Exception -> 0x0845 }
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2     // Catch:{ Exception -> 0x0845 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ Exception -> 0x0845 }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x0845 }
            r13.via_bot_id = r2     // Catch:{ Exception -> 0x0845 }
        L_0x088b:
            int r2 = r13.flags     // Catch:{ Exception -> 0x0845 }
            r2 = r2 | 2048(0x800, float:2.87E-42)
            r13.flags = r2     // Catch:{ Exception -> 0x0845 }
        L_0x0891:
            r13.params = r11     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r14 == 0) goto L_0x0899
            boolean r2 = r14.resendAsIs     // Catch:{ Exception -> 0x0845 }
            if (r2 != 0) goto L_0x08e4
        L_0x0899:
            if (r9 == 0) goto L_0x089d
            r2 = r9
            goto L_0x08a5
        L_0x089d:
            org.telegram.tgnet.ConnectionsManager r2 = r53.getConnectionsManager()     // Catch:{ Exception -> 0x1CLASSNAME }
            int r2 = r2.getCurrentTime()     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x08a5:
            r13.date = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            boolean r2 = r6 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r2 == 0) goto L_0x08e1
            if (r9 != 0) goto L_0x08b8
            if (r24 == 0) goto L_0x08b8
            r2 = 1
            r13.views = r2     // Catch:{ Exception -> 0x0845 }
            int r2 = r13.flags     // Catch:{ Exception -> 0x0845 }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r13.flags = r2     // Catch:{ Exception -> 0x0845 }
        L_0x08b8:
            org.telegram.messenger.MessagesController r2 = r53.getMessagesController()     // Catch:{ Exception -> 0x0845 }
            int r3 = r6.channel_id     // Catch:{ Exception -> 0x0845 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x08e4
            boolean r3 = r2.megagroup     // Catch:{ Exception -> 0x0845 }
            if (r3 == 0) goto L_0x08d0
            r3 = 1
            r13.unread = r3     // Catch:{ Exception -> 0x0845 }
            goto L_0x08e4
        L_0x08d0:
            r3 = 1
            r13.post = r3     // Catch:{ Exception -> 0x0845 }
            boolean r2 = r2.signatures     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x08e4
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x0845 }
            r2.<init>()     // Catch:{ Exception -> 0x0845 }
            r13.from_id = r2     // Catch:{ Exception -> 0x0845 }
            r2.user_id = r1     // Catch:{ Exception -> 0x0845 }
            goto L_0x08e4
        L_0x08e1:
            r2 = 1
            r13.unread = r2     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x08e4:
            int r2 = r13.flags     // Catch:{ Exception -> 0x1CLASSNAME }
            r2 = r2 | 512(0x200, float:7.175E-43)
            r13.flags = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            r13.dialog_id = r7     // Catch:{ Exception -> 0x1CLASSNAME }
            r3 = r67
            r2 = r72
            if (r3 == 0) goto L_0x0935
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = new org.telegram.tgnet.TLRPC$TL_messageReplyHeader     // Catch:{ Exception -> 0x0845 }
            r2.<init>()     // Catch:{ Exception -> 0x0845 }
            r13.reply_to = r2     // Catch:{ Exception -> 0x0845 }
            if (r10 == 0) goto L_0x090f
            r28 = r6
            org.telegram.tgnet.TLRPC$Message r6 = r3.messageOwner     // Catch:{ Exception -> 0x0845 }
            long r6 = r6.random_id     // Catch:{ Exception -> 0x0845 }
            int r8 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r8 == 0) goto L_0x0911
            r2.reply_to_random_id = r6     // Catch:{ Exception -> 0x0845 }
            int r6 = r13.flags     // Catch:{ Exception -> 0x0845 }
            r7 = 8
            r6 = r6 | r7
            r13.flags = r6     // Catch:{ Exception -> 0x0845 }
            goto L_0x0918
        L_0x090f:
            r28 = r6
        L_0x0911:
            int r6 = r13.flags     // Catch:{ Exception -> 0x0845 }
            r7 = 8
            r6 = r6 | r7
            r13.flags = r6     // Catch:{ Exception -> 0x0845 }
        L_0x0918:
            int r6 = r67.getId()     // Catch:{ Exception -> 0x0845 }
            r2.reply_to_msg_id = r6     // Catch:{ Exception -> 0x0845 }
            r8 = r68
            if (r8 == 0) goto L_0x0939
            if (r8 == r3) goto L_0x0939
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r13.reply_to     // Catch:{ Exception -> 0x0845 }
            int r6 = r68.getId()     // Catch:{ Exception -> 0x0845 }
            r2.reply_to_top_id = r6     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r13.reply_to     // Catch:{ Exception -> 0x0845 }
            int r6 = r2.flags     // Catch:{ Exception -> 0x0845 }
            r7 = 2
            r6 = r6 | r7
            r2.flags = r6     // Catch:{ Exception -> 0x0845 }
            goto L_0x0939
        L_0x0935:
            r8 = r68
            r28 = r6
        L_0x0939:
            if (r26 == 0) goto L_0x0955
            org.telegram.tgnet.TLRPC$TL_messageReplies r2 = new org.telegram.tgnet.TLRPC$TL_messageReplies     // Catch:{ Exception -> 0x0845 }
            r2.<init>()     // Catch:{ Exception -> 0x0845 }
            r13.replies = r2     // Catch:{ Exception -> 0x0845 }
            r6 = 1
            r2.comments = r6     // Catch:{ Exception -> 0x0845 }
            r7 = r26
            r2.channel_id = r7     // Catch:{ Exception -> 0x0845 }
            int r7 = r2.flags     // Catch:{ Exception -> 0x0845 }
            r7 = r7 | r6
            r2.flags = r7     // Catch:{ Exception -> 0x0845 }
            int r2 = r13.flags     // Catch:{ Exception -> 0x0845 }
            r6 = 8388608(0x800000, float:1.17549435E-38)
            r2 = r2 | r6
            r13.flags = r2     // Catch:{ Exception -> 0x0845 }
        L_0x0955:
            r2 = r73
            if (r2 == 0) goto L_0x0963
            if (r10 != 0) goto L_0x0963
            int r6 = r13.flags     // Catch:{ Exception -> 0x0845 }
            r6 = r6 | 64
            r13.flags = r6     // Catch:{ Exception -> 0x0845 }
            r13.reply_markup = r2     // Catch:{ Exception -> 0x0845 }
        L_0x0963:
            if (r5 == 0) goto L_0x0994
            org.telegram.messenger.MessagesController r1 = r53.getMessagesController()     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$Peer r1 = r1.getPeer(r5)     // Catch:{ Exception -> 0x0845 }
            r13.peer_id = r1     // Catch:{ Exception -> 0x0845 }
            if (r5 <= 0) goto L_0x098c
            org.telegram.messenger.MessagesController r1 = r53.getMessagesController()     // Catch:{ Exception -> 0x0845 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)     // Catch:{ Exception -> 0x0845 }
            if (r1 != 0) goto L_0x0985
            int r1 = r13.id     // Catch:{ Exception -> 0x0845 }
            r4.processSentMessage(r1)     // Catch:{ Exception -> 0x0845 }
            return
        L_0x0985:
            boolean r1 = r1.bot     // Catch:{ Exception -> 0x0845 }
            if (r1 == 0) goto L_0x098c
            r1 = 0
            r13.unread = r1     // Catch:{ Exception -> 0x0845 }
        L_0x098c:
            r1 = r59
            r2 = r25
            r5 = 1
            r6 = 4
            goto L_0x0a3e
        L_0x0994:
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ Exception -> 0x1CLASSNAME }
            r2.<init>()     // Catch:{ Exception -> 0x1CLASSNAME }
            r13.peer_id = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            int r5 = r10.participant_id     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r5 != r1) goto L_0x09a4
            int r1 = r10.admin_id     // Catch:{ Exception -> 0x0845 }
            r2.user_id = r1     // Catch:{ Exception -> 0x0845 }
            goto L_0x09a6
        L_0x09a4:
            r2.user_id = r5     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x09a6:
            if (r59 == 0) goto L_0x09ae
            r1 = r59
            r13.ttl = r1     // Catch:{ Exception -> 0x0845 }
        L_0x09ac:
            r6 = 4
            goto L_0x09c2
        L_0x09ae:
            r1 = r59
            int r2 = r10.ttl     // Catch:{ Exception -> 0x1CLASSNAME }
            r13.ttl = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r2 == 0) goto L_0x09ac
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r13.media     // Catch:{ Exception -> 0x0845 }
            if (r5 == 0) goto L_0x09ac
            r5.ttl_seconds = r2     // Catch:{ Exception -> 0x0845 }
            int r2 = r5.flags     // Catch:{ Exception -> 0x0845 }
            r6 = 4
            r2 = r2 | r6
            r5.flags = r2     // Catch:{ Exception -> 0x0845 }
        L_0x09c2:
            int r2 = r13.ttl     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r2 == 0) goto L_0x0a3b
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r13.media     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x0a3b
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r13)     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x0a01
            r2 = 0
        L_0x09d3:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r13.media     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0845 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0845 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0845 }
            if (r2 >= r5) goto L_0x09f5
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r13.media     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0845 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0845 }
            java.lang.Object r5 = r5.get(r2)     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r5     // Catch:{ Exception -> 0x0845 }
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio     // Catch:{ Exception -> 0x0845 }
            if (r7 == 0) goto L_0x09f2
            int r2 = r5.duration     // Catch:{ Exception -> 0x0845 }
            goto L_0x09f6
        L_0x09f2:
            int r2 = r2 + 1
            goto L_0x09d3
        L_0x09f5:
            r2 = 0
        L_0x09f6:
            int r5 = r13.ttl     // Catch:{ Exception -> 0x0845 }
            r7 = 1
            int r2 = r2 + r7
            int r2 = java.lang.Math.max(r5, r2)     // Catch:{ Exception -> 0x0845 }
            r13.ttl = r2     // Catch:{ Exception -> 0x0845 }
            goto L_0x0a3b
        L_0x0a01:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r13)     // Catch:{ Exception -> 0x0845 }
            if (r2 != 0) goto L_0x0a0d
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r13)     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x0a3b
        L_0x0a0d:
            r2 = 0
        L_0x0a0e:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r13.media     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0845 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0845 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0845 }
            if (r2 >= r5) goto L_0x0a30
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r13.media     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ Exception -> 0x0845 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r5.attributes     // Catch:{ Exception -> 0x0845 }
            java.lang.Object r5 = r5.get(r2)     // Catch:{ Exception -> 0x0845 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r5     // Catch:{ Exception -> 0x0845 }
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x0845 }
            if (r7 == 0) goto L_0x0a2d
            int r2 = r5.duration     // Catch:{ Exception -> 0x0845 }
            goto L_0x0a31
        L_0x0a2d:
            int r2 = r2 + 1
            goto L_0x0a0e
        L_0x0a30:
            r2 = 0
        L_0x0a31:
            int r5 = r13.ttl     // Catch:{ Exception -> 0x0845 }
            r7 = 1
            int r2 = r2 + r7
            int r2 = java.lang.Math.max(r5, r2)     // Catch:{ Exception -> 0x0845 }
            r13.ttl = r2     // Catch:{ Exception -> 0x0845 }
        L_0x0a3b:
            r2 = r25
            r5 = 1
        L_0x0a3e:
            if (r2 == r5) goto L_0x0a4f
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r13)     // Catch:{ Exception -> 0x0845 }
            if (r2 != 0) goto L_0x0a4c
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r13)     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x0a4f
        L_0x0a4c:
            r2 = 1
            r13.media_unread = r2     // Catch:{ Exception -> 0x0845 }
        L_0x0a4f:
            org.telegram.tgnet.TLRPC$Peer r2 = r13.from_id     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r2 != 0) goto L_0x0a57
            org.telegram.tgnet.TLRPC$Peer r2 = r13.peer_id     // Catch:{ Exception -> 0x0845 }
            r13.from_id = r2     // Catch:{ Exception -> 0x0845 }
        L_0x0a57:
            r2 = 1
            r13.send_state = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r11 == 0) goto L_0x0a88
            java.lang.String r2 = "groupId"
            java.lang.Object r2 = r11.get(r2)     // Catch:{ Exception -> 0x0845 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x0a78
            java.lang.Long r2 = org.telegram.messenger.Utilities.parseLong(r2)     // Catch:{ Exception -> 0x0845 }
            long r6 = r2.longValue()     // Catch:{ Exception -> 0x0845 }
            r13.grouped_id = r6     // Catch:{ Exception -> 0x0845 }
            int r2 = r13.flags     // Catch:{ Exception -> 0x0845 }
            r5 = 131072(0x20000, float:1.83671E-40)
            r2 = r2 | r5
            r13.flags = r2     // Catch:{ Exception -> 0x0845 }
            goto L_0x0a7a
        L_0x0a78:
            r6 = r16
        L_0x0a7a:
            java.lang.String r2 = "final"
            java.lang.Object r2 = r11.get(r2)     // Catch:{ Exception -> 0x0845 }
            if (r2 == 0) goto L_0x0a84
            r2 = 1
            goto L_0x0a85
        L_0x0a84:
            r2 = 0
        L_0x0a85:
            r24 = r2
            goto L_0x0a8c
        L_0x0a88:
            r6 = r16
            r24 = 0
        L_0x0a8c:
            org.telegram.messenger.MessageObject r5 = new org.telegram.messenger.MessageObject     // Catch:{ Exception -> 0x1CLASSNAME }
            int r2 = r4.currentAccount     // Catch:{ Exception -> 0x1CLASSNAME }
            r25 = 1
            r26 = 1
            r41 = r57
            r27 = r2
            r42 = r31
            r2 = r5
            r44 = r56
            r43 = r28
            r45 = r32
            r3 = r27
            r46 = r33
            r4 = r13
            r47 = r5
            r48 = r20
            r5 = r67
            r60 = r11
            r20 = r12
            r11 = r6
            r6 = r25
            r59 = r1
            r56 = r15
            r1 = 0
            r14 = r64
            r7 = r26
            r2.<init>((int) r3, (org.telegram.tgnet.TLRPC$Message) r4, (org.telegram.messenger.MessageObject) r5, (boolean) r6, (boolean) r7)     // Catch:{ Exception -> 0x1CLASSNAME }
            r2 = r79
            r3 = r47
            r3.sendAnimationData = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            r2 = 1
            r3.wasJustSent = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r9 == 0) goto L_0x0acc
            r2 = 1
            goto L_0x0acd
        L_0x0acc:
            r2 = 0
        L_0x0acd:
            r3.scheduled = r2     // Catch:{ Exception -> 0x1CLASSNAME }
            boolean r2 = r3.isForwarded()     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r2 != 0) goto L_0x0af3
            int r2 = r3.type     // Catch:{ Exception -> 0x0aeb }
            r4 = 3
            if (r2 == r4) goto L_0x0adf
            if (r58 != 0) goto L_0x0adf
            r4 = 2
            if (r2 != r4) goto L_0x0af3
        L_0x0adf:
            java.lang.String r2 = r13.attachPath     // Catch:{ Exception -> 0x0aeb }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0aeb }
            if (r2 != 0) goto L_0x0af3
            r2 = 1
            r3.attachPathExists = r2     // Catch:{ Exception -> 0x0aeb }
            goto L_0x0af3
        L_0x0aeb:
            r0 = move-exception
            r4 = r53
            r2 = r3
        L_0x0aef:
            r1 = r9
            r5 = r13
            goto L_0x0419
        L_0x0af3:
            org.telegram.messenger.VideoEditedInfo r2 = r3.videoEditedInfo     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r2 == 0) goto L_0x0afa
            if (r58 != 0) goto L_0x0afa
            goto L_0x0afc
        L_0x0afa:
            r2 = r58
        L_0x0afc:
            int r4 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x0b52
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x0aeb }
            r4.<init>()     // Catch:{ Exception -> 0x0aeb }
            r4.add(r3)     // Catch:{ Exception -> 0x0aeb }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x0aeb }
            r5.<init>()     // Catch:{ Exception -> 0x0aeb }
            r5.add(r13)     // Catch:{ Exception -> 0x0aeb }
            r6 = r53
            int r7 = r6.currentAccount     // Catch:{ Exception -> 0x0b4e }
            org.telegram.messenger.MessagesStorage r24 = org.telegram.messenger.MessagesStorage.getInstance(r7)     // Catch:{ Exception -> 0x0b4e }
            r26 = 0
            r27 = 1
            r28 = 0
            r29 = 0
            if (r9 == 0) goto L_0x0b25
            r30 = 1
            goto L_0x0b27
        L_0x0b25:
            r30 = 0
        L_0x0b27:
            r25 = r5
            r24.putMessages((java.util.ArrayList<org.telegram.tgnet.TLRPC$Message>) r25, (boolean) r26, (boolean) r27, (boolean) r28, (int) r29, (boolean) r30)     // Catch:{ Exception -> 0x0b4e }
            int r5 = r6.currentAccount     // Catch:{ Exception -> 0x0b4e }
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)     // Catch:{ Exception -> 0x0b4e }
            if (r9 == 0) goto L_0x0b36
            r7 = 1
            goto L_0x0b37
        L_0x0b36:
            r7 = 0
        L_0x0b37:
            r5.updateInterfaceWithMessages(r14, r4, r7)     // Catch:{ Exception -> 0x0b4e }
            if (r9 != 0) goto L_0x0b4a
            int r4 = r6.currentAccount     // Catch:{ Exception -> 0x0b4e }
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getInstance(r4)     // Catch:{ Exception -> 0x0b4e }
            int r5 = org.telegram.messenger.NotificationCenter.dialogsNeedReload     // Catch:{ Exception -> 0x0b4e }
            r7 = 0
            java.lang.Object[] r1 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0b4e }
            r4.postNotificationName(r5, r1)     // Catch:{ Exception -> 0x0b4e }
        L_0x0b4a:
            r1 = r19
            r4 = 0
            goto L_0x0b9e
        L_0x0b4e:
            r0 = move-exception
            r2 = r3
            r4 = r6
            goto L_0x0aef
        L_0x0b52:
            r6 = r53
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x1CLASSNAME }
            r1.<init>()     // Catch:{ Exception -> 0x1CLASSNAME }
            java.lang.String r4 = "group_"
            r1.append(r4)     // Catch:{ Exception -> 0x1CLASSNAME }
            r1.append(r11)     // Catch:{ Exception -> 0x1CLASSNAME }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x1CLASSNAME }
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.SendMessagesHelper$DelayedMessage>> r4 = r6.delayedMessages     // Catch:{ Exception -> 0x1CLASSNAME }
            java.lang.Object r1 = r4.get(r1)     // Catch:{ Exception -> 0x1CLASSNAME }
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r1 == 0) goto L_0x0b78
            r4 = 0
            java.lang.Object r1 = r1.get(r4)     // Catch:{ Exception -> 0x0b4e }
            r19 = r1
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r19 = (org.telegram.messenger.SendMessagesHelper.DelayedMessage) r19     // Catch:{ Exception -> 0x0b4e }
        L_0x0b78:
            if (r19 != 0) goto L_0x0b8c
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x0b4e }
            r1.<init>(r14)     // Catch:{ Exception -> 0x0b4e }
            r1.initForGroup(r11)     // Catch:{ Exception -> 0x0b4e }
            r1.encryptedChat = r10     // Catch:{ Exception -> 0x0b4e }
            if (r9 == 0) goto L_0x0b88
            r4 = 1
            goto L_0x0b89
        L_0x0b88:
            r4 = 0
        L_0x0b89:
            r1.scheduled = r4     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0b8e
        L_0x0b8c:
            r1 = r19
        L_0x0b8e:
            r4 = 0
            r1.performMediaUpload = r4     // Catch:{ Exception -> 0x1CLASSNAME }
            r4 = 0
            r1.photoSize = r4     // Catch:{ Exception -> 0x1CLASSNAME }
            r1.videoEditedInfo = r4     // Catch:{ Exception -> 0x1CLASSNAME }
            r1.httpLocation = r4     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r24 == 0) goto L_0x0b9e
            int r5 = r13.id     // Catch:{ Exception -> 0x0b4e }
            r1.finalGroupMessage = r5     // Catch:{ Exception -> 0x0b4e }
        L_0x0b9e:
            boolean r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r5 == 0) goto L_0x0c0e
            r5 = r43
            if (r5 == 0) goto L_0x0c0b
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b4e }
            r7.<init>()     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r4 = "send message user_id = "
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            int r4 = r5.user_id     // Catch:{ Exception -> 0x0b4e }
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r4 = " chat_id = "
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            int r4 = r5.chat_id     // Catch:{ Exception -> 0x0b4e }
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r4 = " channel_id = "
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            int r4 = r5.channel_id     // Catch:{ Exception -> 0x0b4e }
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r4 = " access_hash = "
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            r73 = r11
            long r11 = r5.access_hash     // Catch:{ Exception -> 0x0b4e }
            r7.append(r11)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r4 = " notify = "
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            r4 = r75
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r4 = " silent = "
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            int r4 = r6.currentAccount     // Catch:{ Exception -> 0x0b4e }
            android.content.SharedPreferences r4 = org.telegram.messenger.MessagesController.getNotificationsSettings(r4)     // Catch:{ Exception -> 0x0b4e }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b4e }
            r11.<init>()     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r12 = "silent_"
            r11.append(r12)     // Catch:{ Exception -> 0x0b4e }
            r11.append(r14)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0b4e }
            r12 = 0
            boolean r4 = r4.getBoolean(r11, r12)     // Catch:{ Exception -> 0x0b4e }
            r7.append(r4)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r4 = r7.toString()     // Catch:{ Exception -> 0x0b4e }
            org.telegram.messenger.FileLog.d(r4)     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0CLASSNAME
        L_0x0c0b:
            r73 = r11
            goto L_0x0CLASSNAME
        L_0x0c0e:
            r73 = r11
            r5 = r43
        L_0x0CLASSNAME:
            if (r63 == 0) goto L_0x1b22
            r4 = r63
            r7 = 9
            if (r4 != r7) goto L_0x0CLASSNAME
            if (r54 == 0) goto L_0x0CLASSNAME
            if (r10 == 0) goto L_0x0CLASSNAME
            goto L_0x1b22
        L_0x0CLASSNAME:
            r7 = 1
            if (r4 < r7) goto L_0x0c2f
            r7 = 3
            if (r4 <= r7) goto L_0x0CLASSNAME
            goto L_0x0c2f
        L_0x0CLASSNAME:
            r11 = r14
            r7 = 4
            r15 = r60
            r14 = r71
            goto L_0x0ded
        L_0x0c2f:
            r7 = 5
            if (r4 < r7) goto L_0x0CLASSNAME
            r11 = 8
            if (r4 <= r11) goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r11 = 9
            if (r4 != r11) goto L_0x0c3c
            if (r10 != 0) goto L_0x0CLASSNAME
        L_0x0c3c:
            r11 = 10
            if (r4 == r11) goto L_0x0CLASSNAME
            r12 = 11
            if (r4 != r12) goto L_0x0CLASSNAME
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r7 = 4
            if (r4 != r7) goto L_0x0d60
            org.telegram.tgnet.TLRPC$TL_messages_forwardMessages r1 = new org.telegram.tgnet.TLRPC$TL_messages_forwardMessages     // Catch:{ Exception -> 0x0b4e }
            r1.<init>()     // Catch:{ Exception -> 0x0b4e }
            r1.to_peer = r5     // Catch:{ Exception -> 0x0b4e }
            r14 = r71
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner     // Catch:{ Exception -> 0x0b4e }
            boolean r2 = r2.with_my_score     // Catch:{ Exception -> 0x0b4e }
            r1.with_my_score = r2     // Catch:{ Exception -> 0x0b4e }
            r15 = r60
            if (r60 == 0) goto L_0x0cc5
            r2 = r46
            boolean r4 = r15.containsKey(r2)     // Catch:{ Exception -> 0x0b4e }
            if (r4 == 0) goto L_0x0cc5
            java.lang.Object r2 = r15.get(r2)     // Catch:{ Exception -> 0x0b4e }
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2     // Catch:{ Exception -> 0x0b4e }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ Exception -> 0x0b4e }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x0b4e }
            r4 = 1
            r1.drop_author = r4     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r4 = "fwd_peer"
            java.lang.Object r4 = r15.get(r4)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x0b4e }
            java.lang.Long r4 = org.telegram.messenger.Utilities.parseLong(r4)     // Catch:{ Exception -> 0x0b4e }
            long r4 = r4.longValue()     // Catch:{ Exception -> 0x0b4e }
            int r7 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r7 >= 0) goto L_0x0cb4
            org.telegram.messenger.MessagesController r7 = r53.getMessagesController()     // Catch:{ Exception -> 0x0b4e }
            long r4 = -r4
            int r5 = (int) r4     // Catch:{ Exception -> 0x0b4e }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0b4e }
            org.telegram.tgnet.TLRPC$Chat r4 = r7.getChat(r4)     // Catch:{ Exception -> 0x0b4e }
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r4)     // Catch:{ Exception -> 0x0b4e }
            if (r5 == 0) goto L_0x0cac
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r5 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x0b4e }
            r5.<init>()     // Catch:{ Exception -> 0x0b4e }
            r1.from_peer = r5     // Catch:{ Exception -> 0x0b4e }
            int r7 = r4.id     // Catch:{ Exception -> 0x0b4e }
            r5.channel_id = r7     // Catch:{ Exception -> 0x0b4e }
            long r7 = r4.access_hash     // Catch:{ Exception -> 0x0b4e }
            r5.access_hash = r7     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0cbb
        L_0x0cac:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0b4e }
            r4.<init>()     // Catch:{ Exception -> 0x0b4e }
            r1.from_peer = r4     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0cbb
        L_0x0cb4:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0b4e }
            r4.<init>()     // Catch:{ Exception -> 0x0b4e }
            r1.from_peer = r4     // Catch:{ Exception -> 0x0b4e }
        L_0x0cbb:
            java.util.ArrayList<java.lang.Integer> r4 = r1.id     // Catch:{ Exception -> 0x0b4e }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x0b4e }
            r4.add(r2)     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0cf8
        L_0x0cc5:
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner     // Catch:{ Exception -> 0x0b4e }
            int r2 = r2.ttl     // Catch:{ Exception -> 0x0b4e }
            if (r2 == 0) goto L_0x0cf1
            org.telegram.messenger.MessagesController r2 = r53.getMessagesController()     // Catch:{ Exception -> 0x0b4e }
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0b4e }
            int r4 = r4.ttl     // Catch:{ Exception -> 0x0b4e }
            int r4 = -r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0b4e }
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r4)     // Catch:{ Exception -> 0x0b4e }
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel     // Catch:{ Exception -> 0x0b4e }
            r4.<init>()     // Catch:{ Exception -> 0x0b4e }
            r1.from_peer = r4     // Catch:{ Exception -> 0x0b4e }
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0b4e }
            int r5 = r5.ttl     // Catch:{ Exception -> 0x0b4e }
            int r5 = -r5
            r4.channel_id = r5     // Catch:{ Exception -> 0x0b4e }
            if (r2 == 0) goto L_0x0cf8
            long r7 = r2.access_hash     // Catch:{ Exception -> 0x0b4e }
            r4.access_hash = r7     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0cf8
        L_0x0cf1:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty     // Catch:{ Exception -> 0x0b4e }
            r2.<init>()     // Catch:{ Exception -> 0x0b4e }
            r1.from_peer = r2     // Catch:{ Exception -> 0x0b4e }
        L_0x0cf8:
            boolean r2 = r13.silent     // Catch:{ Exception -> 0x0b4e }
            r1.silent = r2     // Catch:{ Exception -> 0x0b4e }
            if (r9 == 0) goto L_0x0d06
            r1.schedule_date = r9     // Catch:{ Exception -> 0x0b4e }
            int r2 = r1.flags     // Catch:{ Exception -> 0x0b4e }
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r1.flags = r2     // Catch:{ Exception -> 0x0b4e }
        L_0x0d06:
            java.util.ArrayList<java.lang.Long> r2 = r1.random_id     // Catch:{ Exception -> 0x0b4e }
            long r4 = r13.random_id     // Catch:{ Exception -> 0x0b4e }
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x0b4e }
            r2.add(r4)     // Catch:{ Exception -> 0x0b4e }
            int r2 = r71.getId()     // Catch:{ Exception -> 0x0b4e }
            if (r2 < 0) goto L_0x0d25
            java.util.ArrayList<java.lang.Integer> r2 = r1.id     // Catch:{ Exception -> 0x0b4e }
            int r4 = r71.getId()     // Catch:{ Exception -> 0x0b4e }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0b4e }
            r2.add(r4)     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0d44
        L_0x0d25:
            org.telegram.tgnet.TLRPC$Message r2 = r14.messageOwner     // Catch:{ Exception -> 0x0b4e }
            int r4 = r2.fwd_msg_id     // Catch:{ Exception -> 0x0b4e }
            if (r4 == 0) goto L_0x0d35
            java.util.ArrayList<java.lang.Integer> r2 = r1.id     // Catch:{ Exception -> 0x0b4e }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ Exception -> 0x0b4e }
            r2.add(r4)     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0d44
        L_0x0d35:
            org.telegram.tgnet.TLRPC$MessageFwdHeader r2 = r2.fwd_from     // Catch:{ Exception -> 0x0b4e }
            if (r2 == 0) goto L_0x0d44
            java.util.ArrayList<java.lang.Integer> r4 = r1.id     // Catch:{ Exception -> 0x0b4e }
            int r2 = r2.channel_post     // Catch:{ Exception -> 0x0b4e }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ Exception -> 0x0b4e }
            r4.add(r2)     // Catch:{ Exception -> 0x0b4e }
        L_0x0d44:
            r2 = 0
            r4 = 0
            if (r9 == 0) goto L_0x0d4a
            r5 = 1
            goto L_0x0d4b
        L_0x0d4a:
            r5 = 0
        L_0x0d4b:
            r54 = r53
            r55 = r1
            r56 = r3
            r57 = r2
            r58 = r4
            r59 = r61
            r60 = r15
            r61 = r5
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0dea
        L_0x0d60:
            r1 = r14
            r7 = 9
            r15 = r60
            r14 = r71
            if (r4 != r7) goto L_0x0dea
            org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult r4 = new org.telegram.tgnet.TLRPC$TL_messages_sendInlineBotResult     // Catch:{ Exception -> 0x0b4e }
            r4.<init>()     // Catch:{ Exception -> 0x0b4e }
            r4.peer = r5     // Catch:{ Exception -> 0x0b4e }
            long r10 = r13.random_id     // Catch:{ Exception -> 0x0b4e }
            r4.random_id = r10     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r5 = "bot"
            boolean r5 = r15.containsKey(r5)     // Catch:{ Exception -> 0x0b4e }
            if (r5 != 0) goto L_0x0d7e
            r5 = 1
            goto L_0x0d7f
        L_0x0d7e:
            r5 = 0
        L_0x0d7f:
            r4.hide_via = r5     // Catch:{ Exception -> 0x0b4e }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r5 = r13.reply_to     // Catch:{ Exception -> 0x0b4e }
            if (r5 == 0) goto L_0x0d91
            int r5 = r5.reply_to_msg_id     // Catch:{ Exception -> 0x0b4e }
            if (r5 == 0) goto L_0x0d91
            int r7 = r4.flags     // Catch:{ Exception -> 0x0b4e }
            r10 = 1
            r7 = r7 | r10
            r4.flags = r7     // Catch:{ Exception -> 0x0b4e }
            r4.reply_to_msg_id = r5     // Catch:{ Exception -> 0x0b4e }
        L_0x0d91:
            boolean r5 = r13.silent     // Catch:{ Exception -> 0x0b4e }
            r4.silent = r5     // Catch:{ Exception -> 0x0b4e }
            if (r9 == 0) goto L_0x0d9f
            r4.schedule_date = r9     // Catch:{ Exception -> 0x0b4e }
            int r5 = r4.flags     // Catch:{ Exception -> 0x0b4e }
            r5 = r5 | 1024(0x400, float:1.435E-42)
            r4.flags = r5     // Catch:{ Exception -> 0x0b4e }
        L_0x0d9f:
            r5 = r36
            java.lang.Object r5 = r15.get(r5)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x0b4e }
            java.lang.Long r5 = org.telegram.messenger.Utilities.parseLong(r5)     // Catch:{ Exception -> 0x0b4e }
            long r10 = r5.longValue()     // Catch:{ Exception -> 0x0b4e }
            r4.query_id = r10     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r5 = "id"
            java.lang.Object r5 = r15.get(r5)     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x0b4e }
            r4.id = r5     // Catch:{ Exception -> 0x0b4e }
            if (r14 != 0) goto L_0x0dd0
            r5 = 1
            r4.clear_draft = r5     // Catch:{ Exception -> 0x0b4e }
            org.telegram.messenger.MediaDataController r5 = r53.getMediaDataController()     // Catch:{ Exception -> 0x0b4e }
            if (r8 == 0) goto L_0x0dcb
            int r7 = r68.getId()     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0dcc
        L_0x0dcb:
            r7 = 0
        L_0x0dcc:
            r8 = 0
            r5.cleanDraft(r1, r7, r8)     // Catch:{ Exception -> 0x0b4e }
        L_0x0dd0:
            r1 = 0
            r2 = 0
            if (r9 == 0) goto L_0x0dd6
            r5 = 1
            goto L_0x0dd7
        L_0x0dd6:
            r5 = 0
        L_0x0dd7:
            r54 = r53
            r55 = r4
            r56 = r3
            r57 = r1
            r58 = r2
            r59 = r61
            r60 = r15
            r61 = r5
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x0b4e }
        L_0x0dea:
            r4 = r6
            goto L_0x1CLASSNAME
        L_0x0ded:
            if (r10 != 0) goto L_0x154a
            r7 = 1
            if (r4 != r7) goto L_0x0e67
            r7 = r18
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x0b4e }
            if (r2 == 0) goto L_0x0e12
            org.telegram.tgnet.TLRPC$TL_inputMediaVenue r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaVenue     // Catch:{ Exception -> 0x0b4e }
            r2.<init>()     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r8 = r7.address     // Catch:{ Exception -> 0x0b4e }
            r2.address = r8     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r8 = r7.title     // Catch:{ Exception -> 0x0b4e }
            r2.title = r8     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r8 = r7.provider     // Catch:{ Exception -> 0x0b4e }
            r2.provider = r8     // Catch:{ Exception -> 0x0b4e }
            java.lang.String r8 = r7.venue_id     // Catch:{ Exception -> 0x0b4e }
            r2.venue_id = r8     // Catch:{ Exception -> 0x0b4e }
            r8 = r20
            r2.venue_type = r8     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0e42
        L_0x0e12:
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive     // Catch:{ Exception -> 0x0b4e }
            if (r2 == 0) goto L_0x0e3d
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoLive     // Catch:{ Exception -> 0x0b4e }
            r2.<init>()     // Catch:{ Exception -> 0x0b4e }
            int r8 = r7.period     // Catch:{ Exception -> 0x0b4e }
            r2.period = r8     // Catch:{ Exception -> 0x0b4e }
            int r8 = r2.flags     // Catch:{ Exception -> 0x0b4e }
            r10 = 2
            r8 = r8 | r10
            r2.flags = r8     // Catch:{ Exception -> 0x0b4e }
            int r10 = r7.heading     // Catch:{ Exception -> 0x0b4e }
            if (r10 == 0) goto L_0x0e2f
            r2.heading = r10     // Catch:{ Exception -> 0x0b4e }
            r10 = 4
            r8 = r8 | r10
            r2.flags = r8     // Catch:{ Exception -> 0x0b4e }
        L_0x0e2f:
            int r8 = r7.proximity_notification_radius     // Catch:{ Exception -> 0x0b4e }
            if (r8 == 0) goto L_0x0e42
            r2.proximity_notification_radius = r8     // Catch:{ Exception -> 0x0b4e }
            int r8 = r2.flags     // Catch:{ Exception -> 0x0b4e }
            r10 = 8
            r8 = r8 | r10
            r2.flags = r8     // Catch:{ Exception -> 0x0b4e }
            goto L_0x0e42
        L_0x0e3d:
            org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaGeoPoint     // Catch:{ Exception -> 0x0b4e }
            r2.<init>()     // Catch:{ Exception -> 0x0b4e }
        L_0x0e42:
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r8 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint     // Catch:{ Exception -> 0x0b4e }
            r8.<init>()     // Catch:{ Exception -> 0x0b4e }
            r2.geo_point = r8     // Catch:{ Exception -> 0x0b4e }
            org.telegram.tgnet.TLRPC$GeoPoint r7 = r7.geo     // Catch:{ Exception -> 0x0b4e }
            double r10 = r7.lat     // Catch:{ Exception -> 0x0b4e }
            r8.lat = r10     // Catch:{ Exception -> 0x0b4e }
            double r10 = r7._long     // Catch:{ Exception -> 0x0b4e }
            r8._long = r10     // Catch:{ Exception -> 0x0b4e }
            r18 = r61
            r10 = r73
            r19 = r4
            r43 = r5
            r8 = r9
            r62 = r15
            r4 = r48
            r7 = 0
            r5 = r2
            r2 = r3
            r9 = r6
            r6 = r13
            goto L_0x1353
        L_0x0e67:
            r8 = r20
            r7 = 2
            if (r4 == r7) goto L_0x1272
            r7 = 9
            if (r4 != r7) goto L_0x0e7e
            r7 = r44
            if (r7 == 0) goto L_0x0e7e
            r14 = r59
            r2 = r3
            r19 = r4
            r43 = r5
            r8 = r9
            goto L_0x127c
        L_0x0e7e:
            java.lang.String r7 = "query"
            r10 = 3
            if (r4 != r10) goto L_0x0fa7
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r8 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x0f9d }
            r8.<init>()     // Catch:{ Exception -> 0x0f9d }
            r10 = r37
            java.lang.String r14 = r10.mime_type     // Catch:{ Exception -> 0x0f9d }
            r8.mime_type = r14     // Catch:{ Exception -> 0x0f9d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r14 = r10.attributes     // Catch:{ Exception -> 0x0f9d }
            r8.attributes = r14     // Catch:{ Exception -> 0x0f9d }
            if (r23 != 0) goto L_0x0ea4
            boolean r14 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r10)     // Catch:{ Exception -> 0x0b4e }
            if (r14 != 0) goto L_0x0eb0
            if (r2 == 0) goto L_0x0ea4
            boolean r14 = r2.muted     // Catch:{ Exception -> 0x0b4e }
            if (r14 != 0) goto L_0x0eb0
            boolean r14 = r2.roundVideo     // Catch:{ Exception -> 0x0b4e }
            if (r14 != 0) goto L_0x0eb0
        L_0x0ea4:
            r14 = 1
            r8.nosound_video = r14     // Catch:{ Exception -> 0x0f9d }
            boolean r14 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0f9d }
            if (r14 == 0) goto L_0x0eb0
            java.lang.String r14 = "nosound_video = true"
            org.telegram.messenger.FileLog.d(r14)     // Catch:{ Exception -> 0x0b4e }
        L_0x0eb0:
            if (r59 == 0) goto L_0x0ec0
            r14 = r59
            r8.ttl_seconds = r14     // Catch:{ Exception -> 0x0b4e }
            r13.ttl = r14     // Catch:{ Exception -> 0x0b4e }
            int r14 = r8.flags     // Catch:{ Exception -> 0x0b4e }
            r18 = 2
            r14 = r14 | 2
            r8.flags = r14     // Catch:{ Exception -> 0x0b4e }
        L_0x0ec0:
            if (r15 == 0) goto L_0x0f0b
            java.lang.String r14 = "masks"
            java.lang.Object r14 = r15.get(r14)     // Catch:{ Exception -> 0x0f9d }
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ Exception -> 0x0f9d }
            if (r14 == 0) goto L_0x0f0b
            r43 = r5
            org.telegram.tgnet.SerializedData r5 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x0f9d }
            byte[] r14 = org.telegram.messenger.Utilities.hexToBytes(r14)     // Catch:{ Exception -> 0x0f9d }
            r5.<init>((byte[]) r14)     // Catch:{ Exception -> 0x0f9d }
            r18 = r13
            r14 = 0
            int r13 = r5.readInt32(r14)     // Catch:{ Exception -> 0x1004 }
        L_0x0ede:
            if (r14 >= r13) goto L_0x0efd
            r54 = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r13 = r8.stickers     // Catch:{ Exception -> 0x1004 }
            r57 = r2
            r19 = r4
            r4 = 0
            int r2 = r5.readInt32(r4)     // Catch:{ Exception -> 0x1004 }
            org.telegram.tgnet.TLRPC$InputDocument r2 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r5, r2, r4)     // Catch:{ Exception -> 0x1004 }
            r13.add(r2)     // Catch:{ Exception -> 0x1004 }
            int r14 = r14 + 1
            r13 = r54
            r2 = r57
            r4 = r19
            goto L_0x0ede
        L_0x0efd:
            r57 = r2
            r19 = r4
            int r2 = r8.flags     // Catch:{ Exception -> 0x1004 }
            r4 = 1
            r2 = r2 | r4
            r8.flags = r2     // Catch:{ Exception -> 0x1004 }
            r5.cleanup()     // Catch:{ Exception -> 0x1004 }
            goto L_0x0var_
        L_0x0f0b:
            r57 = r2
            r19 = r4
            r43 = r5
            r18 = r13
        L_0x0var_:
            long r4 = r10.access_hash     // Catch:{ Exception -> 0x1004 }
            int r2 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r2 != 0) goto L_0x0f1c
            r5 = r8
            r2 = 1
            goto L_0x0var_
        L_0x0f1c:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r2 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x1004 }
            r2.<init>()     // Catch:{ Exception -> 0x1004 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r4 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x1004 }
            r4.<init>()     // Catch:{ Exception -> 0x1004 }
            r2.id = r4     // Catch:{ Exception -> 0x1004 }
            long r13 = r10.id     // Catch:{ Exception -> 0x1004 }
            r4.id = r13     // Catch:{ Exception -> 0x1004 }
            long r13 = r10.access_hash     // Catch:{ Exception -> 0x1004 }
            r4.access_hash = r13     // Catch:{ Exception -> 0x1004 }
            byte[] r5 = r10.file_reference     // Catch:{ Exception -> 0x1004 }
            r4.file_reference = r5     // Catch:{ Exception -> 0x1004 }
            if (r5 != 0) goto L_0x0f3b
            r5 = 0
            byte[] r13 = new byte[r5]     // Catch:{ Exception -> 0x1004 }
            r4.file_reference = r13     // Catch:{ Exception -> 0x1004 }
        L_0x0f3b:
            if (r15 == 0) goto L_0x0var_
            boolean r4 = r15.containsKey(r7)     // Catch:{ Exception -> 0x1004 }
            if (r4 == 0) goto L_0x0var_
            java.lang.Object r4 = r15.get(r7)     // Catch:{ Exception -> 0x1004 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Exception -> 0x1004 }
            r2.query = r4     // Catch:{ Exception -> 0x1004 }
            int r4 = r2.flags     // Catch:{ Exception -> 0x1004 }
            r5 = 2
            r4 = r4 | r5
            r2.flags = r4     // Catch:{ Exception -> 0x1004 }
        L_0x0var_:
            r5 = r2
            r2 = 0
        L_0x0var_:
            if (r1 != 0) goto L_0x0f6f
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1004 }
            r1.<init>(r11)     // Catch:{ Exception -> 0x1004 }
            r4 = 1
            r1.type = r4     // Catch:{ Exception -> 0x1004 }
            r1.obj = r3     // Catch:{ Exception -> 0x1004 }
            r4 = r48
            r1.originalPath = r4     // Catch:{ Exception -> 0x1004 }
            r13 = r61
            r1.parentObject = r13     // Catch:{ Exception -> 0x1004 }
            if (r9 == 0) goto L_0x0f6b
            r7 = 1
            goto L_0x0f6c
        L_0x0f6b:
            r7 = 0
        L_0x0f6c:
            r1.scheduled = r7     // Catch:{ Exception -> 0x1004 }
            goto L_0x0var_
        L_0x0f6f:
            r13 = r61
            r4 = r48
        L_0x0var_:
            r1.inputUploadMedia = r8     // Catch:{ Exception -> 0x1004 }
            r1.performMediaUpload = r2     // Catch:{ Exception -> 0x1004 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r10.thumbs     // Catch:{ Exception -> 0x1004 }
            boolean r7 = r7.isEmpty()     // Catch:{ Exception -> 0x1004 }
            if (r7 != 0) goto L_0x0var_
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r10.thumbs     // Catch:{ Exception -> 0x1004 }
            r8 = 0
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x1004 }
            org.telegram.tgnet.TLRPC$PhotoSize r7 = (org.telegram.tgnet.TLRPC$PhotoSize) r7     // Catch:{ Exception -> 0x1004 }
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x1004 }
            if (r8 != 0) goto L_0x0var_
            r1.photoSize = r7     // Catch:{ Exception -> 0x1004 }
            r1.locationParent = r10     // Catch:{ Exception -> 0x1004 }
        L_0x0var_:
            r7 = r57
            r1.videoEditedInfo = r7     // Catch:{ Exception -> 0x1004 }
            r10 = r73
            r7 = r2
            r2 = r3
            r8 = r9
            r62 = r15
            goto L_0x0fff
        L_0x0f9d:
            r0 = move-exception
            r18 = r13
        L_0x0fa0:
            r2 = r3
            r4 = r6
        L_0x0fa2:
            r1 = r9
        L_0x0fa3:
            r5 = r18
            goto L_0x0419
        L_0x0fa7:
            r14 = r59
            r2 = r4
            r43 = r5
            r18 = r13
            r10 = r37
            r4 = r48
            r5 = 6
            r13 = r61
            if (r2 != r5) goto L_0x1006
            org.telegram.tgnet.TLRPC$TL_inputMediaContact r7 = new org.telegram.tgnet.TLRPC$TL_inputMediaContact     // Catch:{ Exception -> 0x1004 }
            r7.<init>()     // Catch:{ Exception -> 0x1004 }
            r10 = r41
            java.lang.String r11 = r10.phone     // Catch:{ Exception -> 0x1004 }
            r7.phone_number = r11     // Catch:{ Exception -> 0x1004 }
            java.lang.String r11 = r10.first_name     // Catch:{ Exception -> 0x1004 }
            r7.first_name = r11     // Catch:{ Exception -> 0x1004 }
            java.lang.String r11 = r10.last_name     // Catch:{ Exception -> 0x1004 }
            r7.last_name = r11     // Catch:{ Exception -> 0x1004 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r11 = r10.restriction_reason     // Catch:{ Exception -> 0x1004 }
            boolean r11 = r11.isEmpty()     // Catch:{ Exception -> 0x1004 }
            if (r11 != 0) goto L_0x0ff3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r11 = r10.restriction_reason     // Catch:{ Exception -> 0x1004 }
            r12 = 0
            java.lang.Object r11 = r11.get(r12)     // Catch:{ Exception -> 0x1004 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r11 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r11     // Catch:{ Exception -> 0x1004 }
            java.lang.String r11 = r11.text     // Catch:{ Exception -> 0x1004 }
            java.lang.String r12 = "BEGIN:VCARD"
            boolean r11 = r11.startsWith(r12)     // Catch:{ Exception -> 0x1004 }
            if (r11 == 0) goto L_0x0ff3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r8 = r10.restriction_reason     // Catch:{ Exception -> 0x1004 }
            r10 = 0
            java.lang.Object r8 = r8.get(r10)     // Catch:{ Exception -> 0x1004 }
            org.telegram.tgnet.TLRPC$TL_restrictionReason r8 = (org.telegram.tgnet.TLRPC$TL_restrictionReason) r8     // Catch:{ Exception -> 0x1004 }
            java.lang.String r8 = r8.text     // Catch:{ Exception -> 0x1004 }
            r7.vcard = r8     // Catch:{ Exception -> 0x1004 }
            goto L_0x0ff5
        L_0x0ff3:
            r7.vcard = r8     // Catch:{ Exception -> 0x1004 }
        L_0x0ff5:
            r10 = r73
            r19 = r2
            r2 = r3
            r5 = r7
            r8 = r9
            r62 = r15
            r7 = 0
        L_0x0fff:
            r9 = r6
        L_0x1000:
            r6 = r18
            goto L_0x1141
        L_0x1004:
            r0 = move-exception
            goto L_0x0fa0
        L_0x1006:
            r8 = 7
            if (r2 == r8) goto L_0x1145
            r8 = 9
            if (r2 != r8) goto L_0x100f
            goto L_0x1145
        L_0x100f:
            r8 = 8
            if (r2 != r8) goto L_0x10bd
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r1 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x1004 }
            r1.<init>()     // Catch:{ Exception -> 0x1004 }
            java.lang.String r8 = r10.mime_type     // Catch:{ Exception -> 0x1004 }
            r1.mime_type = r8     // Catch:{ Exception -> 0x1004 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r10.attributes     // Catch:{ Exception -> 0x1004 }
            r1.attributes = r8     // Catch:{ Exception -> 0x1004 }
            if (r14 == 0) goto L_0x103c
            r1.ttl_seconds = r14     // Catch:{ Exception -> 0x1033 }
            r8 = r18
            r8.ttl = r14     // Catch:{ Exception -> 0x1031 }
            int r14 = r1.flags     // Catch:{ Exception -> 0x1031 }
            r18 = 2
            r14 = r14 | 2
            r1.flags = r14     // Catch:{ Exception -> 0x1031 }
            goto L_0x103e
        L_0x1031:
            r0 = move-exception
            goto L_0x1036
        L_0x1033:
            r0 = move-exception
            r8 = r18
        L_0x1036:
            r2 = r3
            r4 = r6
            r5 = r8
            r1 = r9
            goto L_0x0419
        L_0x103c:
            r8 = r18
        L_0x103e:
            long r5 = r10.access_hash     // Catch:{ Exception -> 0x10b5 }
            int r14 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x1049
            r5 = r1
            r18 = r8
            r6 = 1
            goto L_0x1081
        L_0x1049:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x10b5 }
            r5.<init>()     // Catch:{ Exception -> 0x10b5 }
            org.telegram.tgnet.TLRPC$TL_inputDocument r6 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x10b5 }
            r6.<init>()     // Catch:{ Exception -> 0x10b5 }
            r5.id = r6     // Catch:{ Exception -> 0x10b5 }
            r18 = r8
            long r8 = r10.id     // Catch:{ Exception -> 0x10ad }
            r6.id = r8     // Catch:{ Exception -> 0x10ad }
            long r8 = r10.access_hash     // Catch:{ Exception -> 0x10ad }
            r6.access_hash = r8     // Catch:{ Exception -> 0x10ad }
            byte[] r8 = r10.file_reference     // Catch:{ Exception -> 0x10ad }
            r6.file_reference = r8     // Catch:{ Exception -> 0x10ad }
            if (r8 != 0) goto L_0x106a
            r8 = 0
            byte[] r9 = new byte[r8]     // Catch:{ Exception -> 0x10ad }
            r6.file_reference = r9     // Catch:{ Exception -> 0x10ad }
        L_0x106a:
            if (r15 == 0) goto L_0x1080
            boolean r6 = r15.containsKey(r7)     // Catch:{ Exception -> 0x10ad }
            if (r6 == 0) goto L_0x1080
            java.lang.Object r6 = r15.get(r7)     // Catch:{ Exception -> 0x10ad }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ Exception -> 0x10ad }
            r5.query = r6     // Catch:{ Exception -> 0x10ad }
            int r6 = r5.flags     // Catch:{ Exception -> 0x10ad }
            r7 = 2
            r6 = r6 | r7
            r5.flags = r6     // Catch:{ Exception -> 0x10ad }
        L_0x1080:
            r6 = 0
        L_0x1081:
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r7 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x10ad }
            r9 = r53
            r7.<init>(r11)     // Catch:{ Exception -> 0x10a7 }
            r8 = 3
            r7.type = r8     // Catch:{ Exception -> 0x10a7 }
            r7.obj = r3     // Catch:{ Exception -> 0x10a7 }
            r7.parentObject = r13     // Catch:{ Exception -> 0x10a7 }
            r7.inputUploadMedia = r1     // Catch:{ Exception -> 0x10a7 }
            r7.performMediaUpload = r6     // Catch:{ Exception -> 0x10a7 }
            r8 = r76
            if (r8 == 0) goto L_0x1099
            r1 = 1
            goto L_0x109a
        L_0x1099:
            r1 = 0
        L_0x109a:
            r7.scheduled = r1     // Catch:{ Exception -> 0x115c }
            r10 = r73
            r19 = r2
            r2 = r3
            r1 = r7
            r62 = r15
            r7 = r6
            goto L_0x1000
        L_0x10a7:
            r0 = move-exception
            r1 = r76
            r2 = r3
            goto L_0x115f
        L_0x10ad:
            r0 = move-exception
            r4 = r53
            r1 = r76
            r2 = r3
            goto L_0x0fa3
        L_0x10b5:
            r0 = move-exception
            r18 = r8
            r4 = r53
            r2 = r3
            goto L_0x0fa2
        L_0x10bd:
            r8 = r9
            r5 = 10
            r9 = r6
            if (r2 != r5) goto L_0x111f
            org.telegram.tgnet.TLRPC$TL_inputMediaPoll r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaPoll     // Catch:{ Exception -> 0x115c }
            r5.<init>()     // Catch:{ Exception -> 0x115c }
            r6 = r38
            org.telegram.tgnet.TLRPC$Poll r7 = r6.poll     // Catch:{ Exception -> 0x115c }
            r5.poll = r7     // Catch:{ Exception -> 0x115c }
            if (r15 == 0) goto L_0x1102
            java.lang.String r7 = "answers"
            boolean r7 = r15.containsKey(r7)     // Catch:{ Exception -> 0x115c }
            if (r7 == 0) goto L_0x1102
            java.lang.String r7 = "answers"
            java.lang.Object r7 = r15.get(r7)     // Catch:{ Exception -> 0x115c }
            java.lang.String r7 = (java.lang.String) r7     // Catch:{ Exception -> 0x115c }
            byte[] r7 = org.telegram.messenger.Utilities.hexToBytes(r7)     // Catch:{ Exception -> 0x115c }
            int r10 = r7.length     // Catch:{ Exception -> 0x115c }
            if (r10 <= 0) goto L_0x1102
            r10 = 0
        L_0x10e8:
            int r11 = r7.length     // Catch:{ Exception -> 0x115c }
            if (r10 >= r11) goto L_0x10fc
            java.util.ArrayList<byte[]> r11 = r5.correct_answers     // Catch:{ Exception -> 0x115c }
            r12 = 1
            byte[] r14 = new byte[r12]     // Catch:{ Exception -> 0x115c }
            byte r12 = r7[r10]     // Catch:{ Exception -> 0x115c }
            r19 = 0
            r14[r19] = r12     // Catch:{ Exception -> 0x115c }
            r11.add(r14)     // Catch:{ Exception -> 0x115c }
            int r10 = r10 + 1
            goto L_0x10e8
        L_0x10fc:
            int r7 = r5.flags     // Catch:{ Exception -> 0x115c }
            r10 = 1
            r7 = r7 | r10
            r5.flags = r7     // Catch:{ Exception -> 0x115c }
        L_0x1102:
            org.telegram.tgnet.TLRPC$PollResults r7 = r6.results     // Catch:{ Exception -> 0x115c }
            if (r7 == 0) goto L_0x112c
            java.lang.String r7 = r7.solution     // Catch:{ Exception -> 0x115c }
            boolean r7 = android.text.TextUtils.isEmpty(r7)     // Catch:{ Exception -> 0x115c }
            if (r7 != 0) goto L_0x112c
            org.telegram.tgnet.TLRPC$PollResults r6 = r6.results     // Catch:{ Exception -> 0x115c }
            java.lang.String r7 = r6.solution     // Catch:{ Exception -> 0x115c }
            r5.solution = r7     // Catch:{ Exception -> 0x115c }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r6.solution_entities     // Catch:{ Exception -> 0x115c }
            r5.solution_entities = r6     // Catch:{ Exception -> 0x115c }
            int r6 = r5.flags     // Catch:{ Exception -> 0x115c }
            r7 = 2
            r6 = r6 | r7
            r5.flags = r6     // Catch:{ Exception -> 0x115c }
            goto L_0x112c
        L_0x111f:
            r5 = 11
            if (r2 != r5) goto L_0x1136
            org.telegram.tgnet.TLRPC$TL_inputMediaDice r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaDice     // Catch:{ Exception -> 0x115c }
            r5.<init>()     // Catch:{ Exception -> 0x115c }
            r6 = r54
            r5.emoticon = r6     // Catch:{ Exception -> 0x115c }
        L_0x112c:
            r10 = r73
            r19 = r2
            r2 = r3
            r62 = r15
            r6 = r18
            goto L_0x1140
        L_0x1136:
            r10 = r73
            r19 = r2
            r2 = r3
            r62 = r15
            r6 = r18
            r5 = 0
        L_0x1140:
            r7 = 0
        L_0x1141:
            r18 = r13
            goto L_0x1353
        L_0x1145:
            r8 = r9
            r9 = r6
            if (r4 != 0) goto L_0x1162
            r5 = r66
            if (r5 != 0) goto L_0x1162
            long r5 = r10.access_hash     // Catch:{ Exception -> 0x115c }
            int r19 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r19 != 0) goto L_0x1154
            goto L_0x1162
        L_0x1154:
            r19 = r2
            r2 = r13
            r6 = r18
            r5 = 0
            goto L_0x11c6
        L_0x115c:
            r0 = move-exception
            r2 = r3
            r1 = r8
        L_0x115f:
            r4 = r9
            goto L_0x0fa3
        L_0x1162:
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x126d }
            r5.<init>()     // Catch:{ Exception -> 0x126d }
            if (r14 == 0) goto L_0x1181
            r5.ttl_seconds = r14     // Catch:{ Exception -> 0x1178 }
            r6 = r18
            r6.ttl = r14     // Catch:{ Exception -> 0x11a7 }
            int r14 = r5.flags     // Catch:{ Exception -> 0x11a7 }
            r18 = 2
            r14 = r14 | 2
            r5.flags = r14     // Catch:{ Exception -> 0x11a7 }
            goto L_0x1183
        L_0x1178:
            r0 = move-exception
            r6 = r18
        L_0x117b:
            r2 = r3
            r5 = r6
            r1 = r8
            r4 = r9
            goto L_0x0419
        L_0x1181:
            r6 = r18
        L_0x1183:
            if (r23 != 0) goto L_0x11a9
            boolean r14 = android.text.TextUtils.isEmpty(r66)     // Catch:{ Exception -> 0x11a7 }
            if (r14 != 0) goto L_0x11a4
            java.lang.String r14 = r66.toLowerCase()     // Catch:{ Exception -> 0x11a7 }
            r19 = r2
            java.lang.String r2 = "mp4"
            boolean r2 = r14.endsWith(r2)     // Catch:{ Exception -> 0x11a7 }
            if (r2 == 0) goto L_0x11ae
            if (r15 == 0) goto L_0x11ab
            java.lang.String r2 = "forceDocument"
            boolean r2 = r15.containsKey(r2)     // Catch:{ Exception -> 0x11a7 }
            if (r2 == 0) goto L_0x11ae
            goto L_0x11ab
        L_0x11a4:
            r19 = r2
            goto L_0x11ae
        L_0x11a7:
            r0 = move-exception
            goto L_0x117b
        L_0x11a9:
            r19 = r2
        L_0x11ab:
            r2 = 1
            r5.nosound_video = r2     // Catch:{ Exception -> 0x126a }
        L_0x11ae:
            if (r15 == 0) goto L_0x11ba
            java.lang.String r2 = "forceDocument"
            boolean r2 = r15.containsKey(r2)     // Catch:{ Exception -> 0x11a7 }
            if (r2 == 0) goto L_0x11ba
            r2 = 1
            goto L_0x11bb
        L_0x11ba:
            r2 = 0
        L_0x11bb:
            r5.force_file = r2     // Catch:{ Exception -> 0x126a }
            java.lang.String r2 = r10.mime_type     // Catch:{ Exception -> 0x126a }
            r5.mime_type = r2     // Catch:{ Exception -> 0x126a }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r10.attributes     // Catch:{ Exception -> 0x126a }
            r5.attributes = r2     // Catch:{ Exception -> 0x126a }
            r2 = r13
        L_0x11c6:
            long r13 = r10.access_hash     // Catch:{ Exception -> 0x126a }
            int r18 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r18 != 0) goto L_0x11d4
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaUploadedDocument     // Catch:{ Exception -> 0x11a7 }
            r54 = r2
            r47 = r3
            r13 = r5
            goto L_0x1218
        L_0x11d4:
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r13 = new org.telegram.tgnet.TLRPC$TL_inputMediaDocument     // Catch:{ Exception -> 0x126a }
            r13.<init>()     // Catch:{ Exception -> 0x126a }
            org.telegram.tgnet.TLRPC$TL_inputDocument r14 = new org.telegram.tgnet.TLRPC$TL_inputDocument     // Catch:{ Exception -> 0x126a }
            r14.<init>()     // Catch:{ Exception -> 0x126a }
            r13.id = r14     // Catch:{ Exception -> 0x126a }
            r54 = r2
            r47 = r3
            long r2 = r10.id     // Catch:{ Exception -> 0x1236 }
            r14.id = r2     // Catch:{ Exception -> 0x1236 }
            long r2 = r10.access_hash     // Catch:{ Exception -> 0x1236 }
            r14.access_hash = r2     // Catch:{ Exception -> 0x1236 }
            byte[] r2 = r10.file_reference     // Catch:{ Exception -> 0x1236 }
            r14.file_reference = r2     // Catch:{ Exception -> 0x1236 }
            if (r2 != 0) goto L_0x1201
            r2 = 0
            byte[] r3 = new byte[r2]     // Catch:{ Exception -> 0x11f8 }
            r14.file_reference = r3     // Catch:{ Exception -> 0x11f8 }
            goto L_0x1201
        L_0x11f8:
            r0 = move-exception
            r3 = r0
            r5 = r6
            r1 = r8
            r4 = r9
            r2 = r47
            goto L_0x1CLASSNAME
        L_0x1201:
            if (r15 == 0) goto L_0x1217
            boolean r2 = r15.containsKey(r7)     // Catch:{ Exception -> 0x11f8 }
            if (r2 == 0) goto L_0x1217
            java.lang.Object r2 = r15.get(r7)     // Catch:{ Exception -> 0x11f8 }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ Exception -> 0x11f8 }
            r13.query = r2     // Catch:{ Exception -> 0x11f8 }
            int r2 = r13.flags     // Catch:{ Exception -> 0x11f8 }
            r3 = 2
            r2 = r2 | r3
            r13.flags = r2     // Catch:{ Exception -> 0x11f8 }
        L_0x1217:
            r7 = 0
        L_0x1218:
            if (r5 == 0) goto L_0x125d
            if (r1 != 0) goto L_0x123b
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1236 }
            r1.<init>(r11)     // Catch:{ Exception -> 0x1236 }
            r2 = 2
            r1.type = r2     // Catch:{ Exception -> 0x1236 }
            r2 = r47
            r1.obj = r2     // Catch:{ Exception -> 0x1294 }
            r1.originalPath = r4     // Catch:{ Exception -> 0x1294 }
            r3 = r54
            r1.parentObject = r3     // Catch:{ Exception -> 0x1294 }
            if (r8 == 0) goto L_0x1232
            r11 = 1
            goto L_0x1233
        L_0x1232:
            r11 = 0
        L_0x1233:
            r1.scheduled = r11     // Catch:{ Exception -> 0x1294 }
            goto L_0x123f
        L_0x1236:
            r0 = move-exception
            r2 = r47
            goto L_0x1295
        L_0x123b:
            r3 = r54
            r2 = r47
        L_0x123f:
            r1.inputUploadMedia = r5     // Catch:{ Exception -> 0x1294 }
            r1.performMediaUpload = r7     // Catch:{ Exception -> 0x1294 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r10.thumbs     // Catch:{ Exception -> 0x1294 }
            boolean r5 = r5.isEmpty()     // Catch:{ Exception -> 0x1294 }
            if (r5 != 0) goto L_0x1261
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r10.thumbs     // Catch:{ Exception -> 0x1294 }
            r11 = 0
            java.lang.Object r5 = r5.get(r11)     // Catch:{ Exception -> 0x1294 }
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5     // Catch:{ Exception -> 0x1294 }
            boolean r11 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize     // Catch:{ Exception -> 0x1294 }
            if (r11 != 0) goto L_0x1261
            r1.photoSize = r5     // Catch:{ Exception -> 0x1294 }
            r1.locationParent = r10     // Catch:{ Exception -> 0x1294 }
            goto L_0x1261
        L_0x125d:
            r3 = r54
            r2 = r47
        L_0x1261:
            r10 = r73
            r18 = r3
            r5 = r13
            r62 = r15
            goto L_0x1353
        L_0x126a:
            r0 = move-exception
            r2 = r3
            goto L_0x1295
        L_0x126d:
            r0 = move-exception
            r2 = r3
            r6 = r18
            goto L_0x1295
        L_0x1272:
            r14 = r59
            r2 = r3
            r19 = r4
            r43 = r5
            r8 = r9
            r7 = r44
        L_0x127c:
            r4 = r48
            r3 = r61
            r9 = r6
            r6 = r13
            org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto r5 = new org.telegram.tgnet.TLRPC$TL_inputMediaUploadedPhoto     // Catch:{ Exception -> 0x1543 }
            r5.<init>()     // Catch:{ Exception -> 0x1543 }
            if (r14 == 0) goto L_0x129b
            r5.ttl_seconds = r14     // Catch:{ Exception -> 0x1294 }
            r6.ttl = r14     // Catch:{ Exception -> 0x1294 }
            int r10 = r5.flags     // Catch:{ Exception -> 0x1294 }
            r13 = 2
            r10 = r10 | r13
            r5.flags = r10     // Catch:{ Exception -> 0x1294 }
            goto L_0x129b
        L_0x1294:
            r0 = move-exception
        L_0x1295:
            r3 = r0
            r5 = r6
            r1 = r8
            r4 = r9
            goto L_0x1CLASSNAME
        L_0x129b:
            if (r15 == 0) goto L_0x12e2
            java.lang.String r10 = "masks"
            java.lang.Object r10 = r15.get(r10)     // Catch:{ Exception -> 0x1294 }
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ Exception -> 0x1294 }
            if (r10 == 0) goto L_0x12e2
            org.telegram.tgnet.SerializedData r13 = new org.telegram.tgnet.SerializedData     // Catch:{ Exception -> 0x1294 }
            byte[] r10 = org.telegram.messenger.Utilities.hexToBytes(r10)     // Catch:{ Exception -> 0x1294 }
            r13.<init>((byte[]) r10)     // Catch:{ Exception -> 0x1294 }
            r10 = 0
            int r14 = r13.readInt32(r10)     // Catch:{ Exception -> 0x1294 }
        L_0x12b5:
            if (r10 >= r14) goto L_0x12d4
            r54 = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r14 = r5.stickers     // Catch:{ Exception -> 0x1294 }
            r18 = r3
            r62 = r15
            r15 = 0
            int r3 = r13.readInt32(r15)     // Catch:{ Exception -> 0x1294 }
            org.telegram.tgnet.TLRPC$InputDocument r3 = org.telegram.tgnet.TLRPC$InputDocument.TLdeserialize(r13, r3, r15)     // Catch:{ Exception -> 0x1294 }
            r14.add(r3)     // Catch:{ Exception -> 0x1294 }
            int r10 = r10 + 1
            r14 = r54
            r15 = r62
            r3 = r18
            goto L_0x12b5
        L_0x12d4:
            r18 = r3
            r62 = r15
            int r3 = r5.flags     // Catch:{ Exception -> 0x1294 }
            r10 = 1
            r3 = r3 | r10
            r5.flags = r3     // Catch:{ Exception -> 0x1294 }
            r13.cleanup()     // Catch:{ Exception -> 0x1294 }
            goto L_0x12e6
        L_0x12e2:
            r18 = r3
            r62 = r15
        L_0x12e6:
            long r13 = r7.access_hash     // Catch:{ Exception -> 0x1543 }
            int r3 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x12ef
            r3 = r5
            r10 = 1
            goto L_0x130f
        L_0x12ef:
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r3 = new org.telegram.tgnet.TLRPC$TL_inputMediaPhoto     // Catch:{ Exception -> 0x1543 }
            r3.<init>()     // Catch:{ Exception -> 0x1543 }
            org.telegram.tgnet.TLRPC$TL_inputPhoto r10 = new org.telegram.tgnet.TLRPC$TL_inputPhoto     // Catch:{ Exception -> 0x1543 }
            r10.<init>()     // Catch:{ Exception -> 0x1543 }
            r3.id = r10     // Catch:{ Exception -> 0x1543 }
            long r13 = r7.id     // Catch:{ Exception -> 0x1543 }
            r10.id = r13     // Catch:{ Exception -> 0x1543 }
            long r13 = r7.access_hash     // Catch:{ Exception -> 0x1543 }
            r10.access_hash = r13     // Catch:{ Exception -> 0x1543 }
            byte[] r13 = r7.file_reference     // Catch:{ Exception -> 0x1543 }
            r10.file_reference = r13     // Catch:{ Exception -> 0x1543 }
            if (r13 != 0) goto L_0x130e
            r13 = 0
            byte[] r14 = new byte[r13]     // Catch:{ Exception -> 0x1294 }
            r10.file_reference = r14     // Catch:{ Exception -> 0x1294 }
        L_0x130e:
            r10 = 0
        L_0x130f:
            if (r1 != 0) goto L_0x1324
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r1 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1294 }
            r1.<init>(r11)     // Catch:{ Exception -> 0x1294 }
            r11 = 0
            r1.type = r11     // Catch:{ Exception -> 0x1294 }
            r1.obj = r2     // Catch:{ Exception -> 0x1294 }
            r1.originalPath = r4     // Catch:{ Exception -> 0x1294 }
            if (r8 == 0) goto L_0x1321
            r11 = 1
            goto L_0x1322
        L_0x1321:
            r11 = 0
        L_0x1322:
            r1.scheduled = r11     // Catch:{ Exception -> 0x1294 }
        L_0x1324:
            r1.inputUploadMedia = r5     // Catch:{ Exception -> 0x1543 }
            r1.performMediaUpload = r10     // Catch:{ Exception -> 0x1543 }
            r5 = r66
            if (r5 == 0) goto L_0x133d
            int r11 = r66.length()     // Catch:{ Exception -> 0x1294 }
            if (r11 <= 0) goto L_0x133d
            r13 = r42
            boolean r11 = r5.startsWith(r13)     // Catch:{ Exception -> 0x1294 }
            if (r11 == 0) goto L_0x133d
            r1.httpLocation = r5     // Catch:{ Exception -> 0x1294 }
            goto L_0x134f
        L_0x133d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r7.sizes     // Catch:{ Exception -> 0x1543 }
            int r11 = r5.size()     // Catch:{ Exception -> 0x1543 }
            r12 = 1
            int r11 = r11 - r12
            java.lang.Object r5 = r5.get(r11)     // Catch:{ Exception -> 0x1543 }
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5     // Catch:{ Exception -> 0x1543 }
            r1.photoSize = r5     // Catch:{ Exception -> 0x1543 }
            r1.locationParent = r7     // Catch:{ Exception -> 0x1543 }
        L_0x134f:
            r5 = r3
            r7 = r10
            r10 = r73
        L_0x1353:
            int r3 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x13e4
            org.telegram.tgnet.TLObject r3 = r1.sendRequest     // Catch:{ Exception -> 0x1294 }
            if (r3 == 0) goto L_0x135e
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r3 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r3     // Catch:{ Exception -> 0x1294 }
            goto L_0x1387
        L_0x135e:
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia     // Catch:{ Exception -> 0x1294 }
            r3.<init>()     // Catch:{ Exception -> 0x1294 }
            r13 = r43
            r3.peer = r13     // Catch:{ Exception -> 0x1294 }
            boolean r12 = r6.silent     // Catch:{ Exception -> 0x1294 }
            r3.silent = r12     // Catch:{ Exception -> 0x1294 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r12 = r6.reply_to     // Catch:{ Exception -> 0x1294 }
            if (r12 == 0) goto L_0x137b
            int r12 = r12.reply_to_msg_id     // Catch:{ Exception -> 0x1294 }
            if (r12 == 0) goto L_0x137b
            int r13 = r3.flags     // Catch:{ Exception -> 0x1294 }
            r14 = 1
            r13 = r13 | r14
            r3.flags = r13     // Catch:{ Exception -> 0x1294 }
            r3.reply_to_msg_id = r12     // Catch:{ Exception -> 0x1294 }
        L_0x137b:
            if (r8 == 0) goto L_0x1385
            r3.schedule_date = r8     // Catch:{ Exception -> 0x1294 }
            int r12 = r3.flags     // Catch:{ Exception -> 0x1294 }
            r12 = r12 | 1024(0x400, float:1.435E-42)
            r3.flags = r12     // Catch:{ Exception -> 0x1294 }
        L_0x1385:
            r1.sendRequest = r3     // Catch:{ Exception -> 0x1294 }
        L_0x1387:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r12 = r1.messageObjects     // Catch:{ Exception -> 0x1294 }
            r12.add(r2)     // Catch:{ Exception -> 0x1294 }
            java.util.ArrayList<java.lang.Object> r12 = r1.parentObjects     // Catch:{ Exception -> 0x1294 }
            r15 = r18
            r12.add(r15)     // Catch:{ Exception -> 0x1294 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r1.locations     // Catch:{ Exception -> 0x1294 }
            org.telegram.tgnet.TLRPC$PhotoSize r13 = r1.photoSize     // Catch:{ Exception -> 0x1294 }
            r12.add(r13)     // Catch:{ Exception -> 0x1294 }
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo> r12 = r1.videoEditedInfos     // Catch:{ Exception -> 0x1294 }
            org.telegram.messenger.VideoEditedInfo r13 = r1.videoEditedInfo     // Catch:{ Exception -> 0x1294 }
            r12.add(r13)     // Catch:{ Exception -> 0x1294 }
            java.util.ArrayList<java.lang.String> r12 = r1.httpLocations     // Catch:{ Exception -> 0x1294 }
            java.lang.String r13 = r1.httpLocation     // Catch:{ Exception -> 0x1294 }
            r12.add(r13)     // Catch:{ Exception -> 0x1294 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputMedia> r12 = r1.inputMedias     // Catch:{ Exception -> 0x1294 }
            org.telegram.tgnet.TLRPC$InputMedia r13 = r1.inputUploadMedia     // Catch:{ Exception -> 0x1294 }
            r12.add(r13)     // Catch:{ Exception -> 0x1294 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r12 = r1.messages     // Catch:{ Exception -> 0x1294 }
            r12.add(r6)     // Catch:{ Exception -> 0x1294 }
            java.util.ArrayList<java.lang.String> r12 = r1.originalPaths     // Catch:{ Exception -> 0x1294 }
            r12.add(r4)     // Catch:{ Exception -> 0x1294 }
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r12 = new org.telegram.tgnet.TLRPC$TL_inputSingleMedia     // Catch:{ Exception -> 0x1294 }
            r12.<init>()     // Catch:{ Exception -> 0x1294 }
            long r13 = r6.random_id     // Catch:{ Exception -> 0x1294 }
            r12.random_id = r13     // Catch:{ Exception -> 0x1294 }
            r12.media = r5     // Catch:{ Exception -> 0x1294 }
            r14 = r40
            r12.message = r14     // Catch:{ Exception -> 0x1294 }
            r5 = r72
            if (r5 == 0) goto L_0x13da
            boolean r13 = r72.isEmpty()     // Catch:{ Exception -> 0x1294 }
            if (r13 != 0) goto L_0x13da
            r12.entities = r5     // Catch:{ Exception -> 0x1294 }
            int r5 = r12.flags     // Catch:{ Exception -> 0x1294 }
            r13 = 1
            r5 = r5 | r13
            r12.flags = r5     // Catch:{ Exception -> 0x1294 }
        L_0x13da:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r5 = r3.multi_media     // Catch:{ Exception -> 0x1294 }
            r5.add(r12)     // Catch:{ Exception -> 0x1294 }
            r12 = r3
            r48 = r4
            r3 = r10
            goto L_0x1439
        L_0x13e4:
            r3 = r72
            r15 = r18
            r14 = r40
            r13 = r43
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r12 = new org.telegram.tgnet.TLRPC$TL_messages_sendMedia     // Catch:{ Exception -> 0x1543 }
            r12.<init>()     // Catch:{ Exception -> 0x1543 }
            r12.peer = r13     // Catch:{ Exception -> 0x1543 }
            boolean r13 = r6.silent     // Catch:{ Exception -> 0x1543 }
            r12.silent = r13     // Catch:{ Exception -> 0x1543 }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r13 = r6.reply_to     // Catch:{ Exception -> 0x1543 }
            if (r13 == 0) goto L_0x140c
            int r13 = r13.reply_to_msg_id     // Catch:{ Exception -> 0x1294 }
            if (r13 == 0) goto L_0x140c
            r48 = r4
            int r4 = r12.flags     // Catch:{ Exception -> 0x1294 }
            r18 = 1
            r4 = r4 | 1
            r12.flags = r4     // Catch:{ Exception -> 0x1294 }
            r12.reply_to_msg_id = r13     // Catch:{ Exception -> 0x1294 }
            goto L_0x140e
        L_0x140c:
            r48 = r4
        L_0x140e:
            r73 = r10
            long r9 = r6.random_id     // Catch:{ Exception -> 0x153f }
            r12.random_id = r9     // Catch:{ Exception -> 0x153f }
            r12.media = r5     // Catch:{ Exception -> 0x153f }
            r12.message = r14     // Catch:{ Exception -> 0x153f }
            if (r3 == 0) goto L_0x1429
            boolean r4 = r72.isEmpty()     // Catch:{ Exception -> 0x153f }
            if (r4 != 0) goto L_0x1429
            r12.entities = r3     // Catch:{ Exception -> 0x153f }
            int r3 = r12.flags     // Catch:{ Exception -> 0x153f }
            r4 = 8
            r3 = r3 | r4
            r12.flags = r3     // Catch:{ Exception -> 0x153f }
        L_0x1429:
            if (r8 == 0) goto L_0x1433
            r12.schedule_date = r8     // Catch:{ Exception -> 0x153f }
            int r3 = r12.flags     // Catch:{ Exception -> 0x153f }
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r12.flags = r3     // Catch:{ Exception -> 0x153f }
        L_0x1433:
            if (r1 == 0) goto L_0x1437
            r1.sendRequest = r12     // Catch:{ Exception -> 0x153f }
        L_0x1437:
            r3 = r73
        L_0x1439:
            int r5 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r5 == 0) goto L_0x1447
            r4 = r53
            r4.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x1444:
            r0 = move-exception
            goto L_0x1545
        L_0x1447:
            r3 = 1
            r4 = r53
            r9 = r19
            if (r9 != r3) goto L_0x1469
            r3 = 0
            if (r8 == 0) goto L_0x1453
            r5 = 1
            goto L_0x1454
        L_0x1453:
            r5 = 0
        L_0x1454:
            r54 = r53
            r55 = r12
            r56 = r2
            r57 = r3
            r58 = r1
            r59 = r15
            r60 = r62
            r61 = r5
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x1469:
            r3 = 2
            if (r9 != r3) goto L_0x1491
            if (r7 == 0) goto L_0x1473
            r4.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x1473:
            r3 = 0
            r5 = 1
            if (r8 == 0) goto L_0x1479
            r7 = 1
            goto L_0x147a
        L_0x1479:
            r7 = 0
        L_0x147a:
            r54 = r53
            r55 = r12
            r56 = r2
            r57 = r48
            r58 = r3
            r59 = r5
            r60 = r1
            r61 = r15
            r63 = r7
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61, r62, r63)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x1491:
            r3 = 3
            if (r9 != r3) goto L_0x14b5
            if (r7 == 0) goto L_0x149b
            r4.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x149b:
            if (r8 == 0) goto L_0x149f
            r3 = 1
            goto L_0x14a0
        L_0x149f:
            r3 = 0
        L_0x14a0:
            r54 = r53
            r55 = r12
            r56 = r2
            r57 = r48
            r58 = r1
            r59 = r15
            r60 = r62
            r61 = r3
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x14b5:
            r3 = 6
            if (r9 != r3) goto L_0x14d2
            if (r8 == 0) goto L_0x14bc
            r3 = 1
            goto L_0x14bd
        L_0x14bc:
            r3 = 0
        L_0x14bd:
            r54 = r53
            r55 = r12
            r56 = r2
            r57 = r48
            r58 = r1
            r59 = r15
            r60 = r62
            r61 = r3
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x14d2:
            r3 = 7
            if (r9 != r3) goto L_0x14f8
            if (r7 == 0) goto L_0x14de
            if (r1 == 0) goto L_0x14de
            r4.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x14de:
            if (r8 == 0) goto L_0x14e2
            r3 = 1
            goto L_0x14e3
        L_0x14e2:
            r3 = 0
        L_0x14e3:
            r54 = r53
            r55 = r12
            r56 = r2
            r57 = r48
            r58 = r1
            r59 = r15
            r60 = r62
            r61 = r3
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x14f8:
            r3 = 8
            if (r9 != r3) goto L_0x151d
            if (r7 == 0) goto L_0x1503
            r4.performSendDelayedMessage(r1)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x1503:
            if (r8 == 0) goto L_0x1507
            r3 = 1
            goto L_0x1508
        L_0x1507:
            r3 = 0
        L_0x1508:
            r54 = r53
            r55 = r12
            r56 = r2
            r57 = r48
            r58 = r1
            r59 = r15
            r60 = r62
            r61 = r3
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x151d:
            r3 = 10
            if (r9 == r3) goto L_0x1525
            r3 = 11
            if (r9 != r3) goto L_0x1CLASSNAME
        L_0x1525:
            if (r8 == 0) goto L_0x1529
            r3 = 1
            goto L_0x152a
        L_0x1529:
            r3 = 0
        L_0x152a:
            r54 = r53
            r55 = r12
            r56 = r2
            r57 = r48
            r58 = r1
            r59 = r15
            r60 = r62
            r61 = r3
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x1444 }
            goto L_0x1CLASSNAME
        L_0x153f:
            r0 = move-exception
            r4 = r53
            goto L_0x1545
        L_0x1543:
            r0 = move-exception
            r4 = r9
        L_0x1545:
            r3 = r0
            r5 = r6
            r1 = r8
            goto L_0x1CLASSNAME
        L_0x154a:
            r49 = r2
            r2 = r3
            r9 = r4
            r4 = r6
            r8 = r13
            r62 = r15
            r7 = r18
            r11 = r20
            r15 = r37
            r5 = r40
            r50 = r41
            r31 = r42
            r6 = r44
            r18 = r61
            r3 = r72
            r13 = r73
            int r12 = r10.layer     // Catch:{ Exception -> 0x1b1c }
            int r12 = org.telegram.messenger.AndroidUtilities.getPeerLayerVersion(r12)     // Catch:{ Exception -> 0x1b1c }
            r54 = r1
            r1 = 73
            if (r12 < r1) goto L_0x158d
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1586 }
            r1.<init>()     // Catch:{ Exception -> 0x1586 }
            int r12 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r12 == 0) goto L_0x1592
            r1.grouped_id = r13     // Catch:{ Exception -> 0x1586 }
            int r12 = r1.flags     // Catch:{ Exception -> 0x1586 }
            r19 = 131072(0x20000, float:1.83671E-40)
            r12 = r12 | r19
            r1.flags = r12     // Catch:{ Exception -> 0x1586 }
            goto L_0x1592
        L_0x1586:
            r0 = move-exception
            r1 = r76
            r3 = r0
            r5 = r8
            goto L_0x1CLASSNAME
        L_0x158d:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45 r1 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage_layer45     // Catch:{ Exception -> 0x1b1c }
            r1.<init>()     // Catch:{ Exception -> 0x1b1c }
        L_0x1592:
            int r12 = r8.ttl     // Catch:{ Exception -> 0x1b1c }
            r1.ttl = r12     // Catch:{ Exception -> 0x1b1c }
            if (r3 == 0) goto L_0x15a6
            boolean r12 = r72.isEmpty()     // Catch:{ Exception -> 0x1586 }
            if (r12 != 0) goto L_0x15a6
            r1.entities = r3     // Catch:{ Exception -> 0x1586 }
            int r3 = r1.flags     // Catch:{ Exception -> 0x1586 }
            r3 = r3 | 128(0x80, float:1.794E-43)
            r1.flags = r3     // Catch:{ Exception -> 0x1586 }
        L_0x15a6:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r8.reply_to     // Catch:{ Exception -> 0x1b1c }
            r73 = r13
            if (r3 == 0) goto L_0x15bb
            long r12 = r3.reply_to_random_id     // Catch:{ Exception -> 0x1586 }
            int r3 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x15bb
            r1.reply_to_random_id = r12     // Catch:{ Exception -> 0x1586 }
            int r3 = r1.flags     // Catch:{ Exception -> 0x1586 }
            r12 = 8
            r3 = r3 | r12
            r1.flags = r3     // Catch:{ Exception -> 0x1586 }
        L_0x15bb:
            boolean r3 = r8.silent     // Catch:{ Exception -> 0x1b1c }
            r1.silent = r3     // Catch:{ Exception -> 0x1b1c }
            int r3 = r1.flags     // Catch:{ Exception -> 0x1b1c }
            r3 = r3 | 512(0x200, float:7.175E-43)
            r1.flags = r3     // Catch:{ Exception -> 0x1b1c }
            if (r62 == 0) goto L_0x15e0
            r14 = r56
            r12 = r62
            java.lang.Object r3 = r12.get(r14)     // Catch:{ Exception -> 0x1586 }
            if (r3 == 0) goto L_0x15e2
            java.lang.Object r3 = r12.get(r14)     // Catch:{ Exception -> 0x1586 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x1586 }
            r1.via_bot_name = r3     // Catch:{ Exception -> 0x1586 }
            int r3 = r1.flags     // Catch:{ Exception -> 0x1586 }
            r3 = r3 | 2048(0x800, float:2.87E-42)
            r1.flags = r3     // Catch:{ Exception -> 0x1586 }
            goto L_0x15e2
        L_0x15e0:
            r12 = r62
        L_0x15e2:
            long r13 = r8.random_id     // Catch:{ Exception -> 0x1b1c }
            r1.random_id = r13     // Catch:{ Exception -> 0x1b1c }
            r1.message = r11     // Catch:{ Exception -> 0x1b1c }
            r3 = 1
            if (r9 != r3) goto L_0x1641
            boolean r3 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue     // Catch:{ Exception -> 0x1586 }
            if (r3 == 0) goto L_0x1607
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVenue     // Catch:{ Exception -> 0x1586 }
            r3.<init>()     // Catch:{ Exception -> 0x1586 }
            r1.media = r3     // Catch:{ Exception -> 0x1586 }
            java.lang.String r5 = r7.address     // Catch:{ Exception -> 0x1586 }
            r3.address = r5     // Catch:{ Exception -> 0x1586 }
            java.lang.String r5 = r7.title     // Catch:{ Exception -> 0x1586 }
            r3.title = r5     // Catch:{ Exception -> 0x1586 }
            java.lang.String r5 = r7.provider     // Catch:{ Exception -> 0x1586 }
            r3.provider = r5     // Catch:{ Exception -> 0x1586 }
            java.lang.String r5 = r7.venue_id     // Catch:{ Exception -> 0x1586 }
            r3.venue_id = r5     // Catch:{ Exception -> 0x1586 }
            goto L_0x160e
        L_0x1607:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaGeoPoint     // Catch:{ Exception -> 0x1586 }
            r3.<init>()     // Catch:{ Exception -> 0x1586 }
            r1.media = r3     // Catch:{ Exception -> 0x1586 }
        L_0x160e:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r1.media     // Catch:{ Exception -> 0x1586 }
            org.telegram.tgnet.TLRPC$GeoPoint r5 = r7.geo     // Catch:{ Exception -> 0x1586 }
            double r6 = r5.lat     // Catch:{ Exception -> 0x1586 }
            r3.lat = r6     // Catch:{ Exception -> 0x1586 }
            double r5 = r5._long     // Catch:{ Exception -> 0x1586 }
            r3._long = r5     // Catch:{ Exception -> 0x1586 }
            org.telegram.messenger.SecretChatHelper r3 = r53.getSecretChatHelper()     // Catch:{ Exception -> 0x1586 }
            org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner     // Catch:{ Exception -> 0x1586 }
            r6 = 0
            r7 = 0
            r56 = r3
            r57 = r1
            r58 = r5
            r59 = r10
            r60 = r6
            r61 = r7
            r62 = r2
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x1586 }
            r13 = r64
            r6 = r73
            r18 = r8
            r19 = r9
            r11 = r48
            r8 = r54
            goto L_0x18bb
        L_0x1641:
            r3 = 2
            if (r9 == r3) goto L_0x199c
            r3 = 9
            if (r9 != r3) goto L_0x164e
            if (r6 == 0) goto L_0x164e
            r13 = r64
            goto L_0x19a0
        L_0x164e:
            r3 = 3
            if (r9 != r3) goto L_0x176c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r15.thumbs     // Catch:{ Exception -> 0x1586 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r4.getThumbForSecretChat(r3)     // Catch:{ Exception -> 0x1586 }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x1586 }
            boolean r6 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r15)     // Catch:{ Exception -> 0x1586 }
            if (r6 != 0) goto L_0x1681
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r15)     // Catch:{ Exception -> 0x1586 }
            if (r6 == 0) goto L_0x1667
            goto L_0x1681
        L_0x1667:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo     // Catch:{ Exception -> 0x1586 }
            r6.<init>()     // Catch:{ Exception -> 0x1586 }
            r1.media = r6     // Catch:{ Exception -> 0x1586 }
            if (r3 == 0) goto L_0x1679
            byte[] r7 = r3.bytes     // Catch:{ Exception -> 0x1586 }
            if (r7 == 0) goto L_0x1679
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r6     // Catch:{ Exception -> 0x1586 }
            r6.thumb = r7     // Catch:{ Exception -> 0x1586 }
            goto L_0x169e
        L_0x1679:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo) r6     // Catch:{ Exception -> 0x1586 }
            r7 = 0
            byte[] r11 = new byte[r7]     // Catch:{ Exception -> 0x1586 }
            r6.thumb = r11     // Catch:{ Exception -> 0x1586 }
            goto L_0x169e
        L_0x1681:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x1586 }
            r6.<init>()     // Catch:{ Exception -> 0x1586 }
            r1.media = r6     // Catch:{ Exception -> 0x1586 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r7 = r15.attributes     // Catch:{ Exception -> 0x1586 }
            r6.attributes = r7     // Catch:{ Exception -> 0x1586 }
            if (r3 == 0) goto L_0x1697
            byte[] r7 = r3.bytes     // Catch:{ Exception -> 0x1586 }
            if (r7 == 0) goto L_0x1697
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r6     // Catch:{ Exception -> 0x1586 }
            r6.thumb = r7     // Catch:{ Exception -> 0x1586 }
            goto L_0x169e
        L_0x1697:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r6 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r6     // Catch:{ Exception -> 0x1586 }
            r7 = 0
            byte[] r11 = new byte[r7]     // Catch:{ Exception -> 0x1586 }
            r6.thumb = r11     // Catch:{ Exception -> 0x1586 }
        L_0x169e:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r1.media     // Catch:{ Exception -> 0x1586 }
            r6.caption = r5     // Catch:{ Exception -> 0x1586 }
            java.lang.String r5 = "video/mp4"
            r6.mime_type = r5     // Catch:{ Exception -> 0x1586 }
            int r5 = r15.size     // Catch:{ Exception -> 0x1586 }
            r6.size = r5     // Catch:{ Exception -> 0x1586 }
            r5 = 0
        L_0x16ab:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r15.attributes     // Catch:{ Exception -> 0x1586 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x1586 }
            if (r5 >= r6) goto L_0x16d1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r15.attributes     // Catch:{ Exception -> 0x1586 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x1586 }
            org.telegram.tgnet.TLRPC$DocumentAttribute r6 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r6     // Catch:{ Exception -> 0x1586 }
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x1586 }
            if (r7 == 0) goto L_0x16ce
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r1.media     // Catch:{ Exception -> 0x1586 }
            int r7 = r6.w     // Catch:{ Exception -> 0x1586 }
            r5.w = r7     // Catch:{ Exception -> 0x1586 }
            int r7 = r6.h     // Catch:{ Exception -> 0x1586 }
            r5.h = r7     // Catch:{ Exception -> 0x1586 }
            int r6 = r6.duration     // Catch:{ Exception -> 0x1586 }
            r5.duration = r6     // Catch:{ Exception -> 0x1586 }
            goto L_0x16d1
        L_0x16ce:
            int r5 = r5 + 1
            goto L_0x16ab
        L_0x16d1:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r1.media     // Catch:{ Exception -> 0x1586 }
            int r6 = r3.h     // Catch:{ Exception -> 0x1586 }
            r5.thumb_h = r6     // Catch:{ Exception -> 0x1586 }
            int r3 = r3.w     // Catch:{ Exception -> 0x1586 }
            r5.thumb_w = r3     // Catch:{ Exception -> 0x1586 }
            byte[] r3 = r15.key     // Catch:{ Exception -> 0x1586 }
            r6 = r73
            if (r3 == 0) goto L_0x171c
            int r3 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x16e6
            goto L_0x171c
        L_0x16e6:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1586 }
            r3.<init>()     // Catch:{ Exception -> 0x1586 }
            long r11 = r15.id     // Catch:{ Exception -> 0x1586 }
            r3.id = r11     // Catch:{ Exception -> 0x1586 }
            long r11 = r15.access_hash     // Catch:{ Exception -> 0x1586 }
            r3.access_hash = r11     // Catch:{ Exception -> 0x1586 }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r1.media     // Catch:{ Exception -> 0x1586 }
            byte[] r11 = r15.key     // Catch:{ Exception -> 0x1586 }
            r5.key = r11     // Catch:{ Exception -> 0x1586 }
            byte[] r11 = r15.iv     // Catch:{ Exception -> 0x1586 }
            r5.iv = r11     // Catch:{ Exception -> 0x1586 }
            org.telegram.messenger.SecretChatHelper r5 = r53.getSecretChatHelper()     // Catch:{ Exception -> 0x1586 }
            org.telegram.tgnet.TLRPC$Message r11 = r2.messageOwner     // Catch:{ Exception -> 0x1586 }
            r12 = 0
            r56 = r5
            r57 = r1
            r58 = r11
            r59 = r10
            r60 = r3
            r61 = r12
            r62 = r2
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x1586 }
            r3 = r54
            r13 = r64
            r11 = r48
            goto L_0x1763
        L_0x171c:
            if (r54 != 0) goto L_0x1752
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x1586 }
            r13 = r64
            r3.<init>(r13)     // Catch:{ Exception -> 0x1586 }
            r3.encryptedChat = r10     // Catch:{ Exception -> 0x1586 }
            r5 = 1
            r3.type = r5     // Catch:{ Exception -> 0x1586 }
            r3.sendEncryptedRequest = r1     // Catch:{ Exception -> 0x1586 }
            r11 = r48
            r3.originalPath = r11     // Catch:{ Exception -> 0x1586 }
            r3.obj = r2     // Catch:{ Exception -> 0x1586 }
            if (r12 == 0) goto L_0x1743
            r5 = r45
            boolean r10 = r12.containsKey(r5)     // Catch:{ Exception -> 0x1586 }
            if (r10 == 0) goto L_0x1743
            java.lang.Object r5 = r12.get(r5)     // Catch:{ Exception -> 0x1586 }
            r3.parentObject = r5     // Catch:{ Exception -> 0x1586 }
            goto L_0x1747
        L_0x1743:
            r5 = r18
            r3.parentObject = r5     // Catch:{ Exception -> 0x1586 }
        L_0x1747:
            r5 = 1
            r3.performMediaUpload = r5     // Catch:{ Exception -> 0x1586 }
            if (r76 == 0) goto L_0x174e
            r5 = 1
            goto L_0x174f
        L_0x174e:
            r5 = 0
        L_0x174f:
            r3.scheduled = r5     // Catch:{ Exception -> 0x1586 }
            goto L_0x1758
        L_0x1752:
            r13 = r64
            r11 = r48
            r3 = r54
        L_0x1758:
            r5 = r49
            r3.videoEditedInfo = r5     // Catch:{ Exception -> 0x1586 }
            int r5 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r5 != 0) goto L_0x1763
            r4.performSendDelayedMessage(r3)     // Catch:{ Exception -> 0x1586 }
        L_0x1763:
            r23 = r6
            r18 = r8
            r19 = r9
            r8 = r3
            goto L_0x1aaa
        L_0x176c:
            r13 = r64
            r6 = r73
            r3 = r18
            r51 = r45
            r11 = r48
            r18 = r8
            r8 = 6
            if (r9 != r8) goto L_0x17b7
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaContact     // Catch:{ Exception -> 0x17af }
            r3.<init>()     // Catch:{ Exception -> 0x17af }
            r1.media = r3     // Catch:{ Exception -> 0x17af }
            r5 = r50
            java.lang.String r8 = r5.phone     // Catch:{ Exception -> 0x17af }
            r3.phone_number = r8     // Catch:{ Exception -> 0x17af }
            java.lang.String r8 = r5.first_name     // Catch:{ Exception -> 0x17af }
            r3.first_name = r8     // Catch:{ Exception -> 0x17af }
            java.lang.String r8 = r5.last_name     // Catch:{ Exception -> 0x17af }
            r3.last_name = r8     // Catch:{ Exception -> 0x17af }
            int r5 = r5.id     // Catch:{ Exception -> 0x17af }
            r3.user_id = r5     // Catch:{ Exception -> 0x17af }
            org.telegram.messenger.SecretChatHelper r3 = r53.getSecretChatHelper()     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner     // Catch:{ Exception -> 0x17af }
            r8 = 0
            r12 = 0
            r56 = r3
            r57 = r1
            r58 = r5
            r59 = r10
            r60 = r8
            r61 = r12
            r62 = r2
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x17af }
            goto L_0x182d
        L_0x17af:
            r0 = move-exception
            r1 = r76
            r3 = r0
            r5 = r18
            goto L_0x1CLASSNAME
        L_0x17b7:
            r8 = 7
            if (r9 == r8) goto L_0x1833
            r8 = 9
            if (r9 != r8) goto L_0x17c2
            if (r15 == 0) goto L_0x17c2
            goto L_0x1833
        L_0x17c2:
            r8 = 8
            if (r9 != r8) goto L_0x182d
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r8 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x17af }
            r8.<init>(r13)     // Catch:{ Exception -> 0x17af }
            r8.encryptedChat = r10     // Catch:{ Exception -> 0x17af }
            r8.sendEncryptedRequest = r1     // Catch:{ Exception -> 0x17af }
            r8.obj = r2     // Catch:{ Exception -> 0x17af }
            r10 = 3
            r8.type = r10     // Catch:{ Exception -> 0x17af }
            r8.parentObject = r3     // Catch:{ Exception -> 0x17af }
            r3 = 1
            r8.performMediaUpload = r3     // Catch:{ Exception -> 0x17af }
            if (r76 == 0) goto L_0x17dd
            r3 = 1
            goto L_0x17de
        L_0x17dd:
            r3 = 0
        L_0x17de:
            r8.scheduled = r3     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x17af }
            r3.<init>()     // Catch:{ Exception -> 0x17af }
            r1.media = r3     // Catch:{ Exception -> 0x17af }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r10 = r15.attributes     // Catch:{ Exception -> 0x17af }
            r3.attributes = r10     // Catch:{ Exception -> 0x17af }
            r3.caption = r5     // Catch:{ Exception -> 0x17af }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r15.thumbs     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r4.getThumbForSecretChat(r3)     // Catch:{ Exception -> 0x17af }
            if (r3 == 0) goto L_0x180a
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r1.media     // Catch:{ Exception -> 0x17af }
            r10 = r5
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r10 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r10     // Catch:{ Exception -> 0x17af }
            byte[] r12 = r3.bytes     // Catch:{ Exception -> 0x17af }
            r10.thumb = r12     // Catch:{ Exception -> 0x17af }
            int r10 = r3.h     // Catch:{ Exception -> 0x17af }
            r5.thumb_h = r10     // Catch:{ Exception -> 0x17af }
            int r3 = r3.w     // Catch:{ Exception -> 0x17af }
            r5.thumb_w = r3     // Catch:{ Exception -> 0x17af }
            goto L_0x1818
        L_0x180a:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r1.media     // Catch:{ Exception -> 0x17af }
            r5 = r3
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x17af }
            r10 = 0
            byte[] r12 = new byte[r10]     // Catch:{ Exception -> 0x17af }
            r5.thumb = r12     // Catch:{ Exception -> 0x17af }
            r3.thumb_h = r10     // Catch:{ Exception -> 0x17af }
            r3.thumb_w = r10     // Catch:{ Exception -> 0x17af }
        L_0x1818:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r1.media     // Catch:{ Exception -> 0x17af }
            java.lang.String r5 = r15.mime_type     // Catch:{ Exception -> 0x17af }
            r3.mime_type = r5     // Catch:{ Exception -> 0x17af }
            int r5 = r15.size     // Catch:{ Exception -> 0x17af }
            r3.size = r5     // Catch:{ Exception -> 0x17af }
            r8.originalPath = r11     // Catch:{ Exception -> 0x17af }
            r4.performSendDelayedMessage(r8)     // Catch:{ Exception -> 0x17af }
            r23 = r6
            r19 = r9
            goto L_0x1aaa
        L_0x182d:
            r8 = r54
            r19 = r9
            goto L_0x18bb
        L_0x1833:
            r19 = r9
            long r8 = r15.access_hash     // Catch:{ Exception -> 0x17af }
            int r20 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r20 == 0) goto L_0x18bf
            boolean r8 = org.telegram.messenger.MessageObject.isStickerDocument(r15)     // Catch:{ Exception -> 0x17af }
            if (r8 != 0) goto L_0x1848
            r8 = 1
            boolean r9 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r15, r8)     // Catch:{ Exception -> 0x17af }
            if (r9 == 0) goto L_0x18bf
        L_0x1848:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument     // Catch:{ Exception -> 0x17af }
            r3.<init>()     // Catch:{ Exception -> 0x17af }
            r1.media = r3     // Catch:{ Exception -> 0x17af }
            long r8 = r15.id     // Catch:{ Exception -> 0x17af }
            r3.id = r8     // Catch:{ Exception -> 0x17af }
            int r5 = r15.date     // Catch:{ Exception -> 0x17af }
            r3.date = r5     // Catch:{ Exception -> 0x17af }
            long r8 = r15.access_hash     // Catch:{ Exception -> 0x17af }
            r3.access_hash = r8     // Catch:{ Exception -> 0x17af }
            java.lang.String r5 = r15.mime_type     // Catch:{ Exception -> 0x17af }
            r3.mime_type = r5     // Catch:{ Exception -> 0x17af }
            int r5 = r15.size     // Catch:{ Exception -> 0x17af }
            r3.size = r5     // Catch:{ Exception -> 0x17af }
            int r5 = r15.dc_id     // Catch:{ Exception -> 0x17af }
            r3.dc_id = r5     // Catch:{ Exception -> 0x17af }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r15.attributes     // Catch:{ Exception -> 0x17af }
            r3.attributes = r5     // Catch:{ Exception -> 0x17af }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r15.thumbs     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r4.getThumbForSecretChat(r3)     // Catch:{ Exception -> 0x17af }
            if (r3 == 0) goto L_0x187a
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r1.media     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r5     // Catch:{ Exception -> 0x17af }
            r5.thumb = r3     // Catch:{ Exception -> 0x17af }
            goto L_0x188f
        L_0x187a:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r1.media     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r3 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r3     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$TL_photoSizeEmpty r5 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty     // Catch:{ Exception -> 0x17af }
            r5.<init>()     // Catch:{ Exception -> 0x17af }
            r3.thumb = r5     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r1.media     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument r3 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaExternalDocument) r3     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r3.thumb     // Catch:{ Exception -> 0x17af }
            java.lang.String r5 = "s"
            r3.type = r5     // Catch:{ Exception -> 0x17af }
        L_0x188f:
            if (r54 == 0) goto L_0x18a0
            r8 = r54
            int r3 = r8.type     // Catch:{ Exception -> 0x17af }
            r5 = 5
            if (r3 != r5) goto L_0x18a2
            r8.sendEncryptedRequest = r1     // Catch:{ Exception -> 0x17af }
            r8.obj = r2     // Catch:{ Exception -> 0x17af }
            r4.performSendDelayedMessage(r8)     // Catch:{ Exception -> 0x17af }
            goto L_0x18bb
        L_0x18a0:
            r8 = r54
        L_0x18a2:
            org.telegram.messenger.SecretChatHelper r3 = r53.getSecretChatHelper()     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner     // Catch:{ Exception -> 0x17af }
            r9 = 0
            r12 = 0
            r56 = r3
            r57 = r1
            r58 = r5
            r59 = r10
            r60 = r9
            r61 = r12
            r62 = r2
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x17af }
        L_0x18bb:
            r23 = r6
            goto L_0x1aaa
        L_0x18bf:
            r8 = r54
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r9 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument     // Catch:{ Exception -> 0x17af }
            r9.<init>()     // Catch:{ Exception -> 0x17af }
            r1.media = r9     // Catch:{ Exception -> 0x17af }
            r54 = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r3 = r15.attributes     // Catch:{ Exception -> 0x17af }
            r9.attributes = r3     // Catch:{ Exception -> 0x17af }
            r9.caption = r5     // Catch:{ Exception -> 0x17af }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r15.thumbs     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r4.getThumbForSecretChat(r3)     // Catch:{ Exception -> 0x17af }
            if (r3 == 0) goto L_0x18ef
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r1.media     // Catch:{ Exception -> 0x17af }
            r9 = r5
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r9 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r9     // Catch:{ Exception -> 0x17af }
            r60 = r12
            byte[] r12 = r3.bytes     // Catch:{ Exception -> 0x17af }
            r9.thumb = r12     // Catch:{ Exception -> 0x17af }
            int r9 = r3.h     // Catch:{ Exception -> 0x17af }
            r5.thumb_h = r9     // Catch:{ Exception -> 0x17af }
            int r3 = r3.w     // Catch:{ Exception -> 0x17af }
            r5.thumb_w = r3     // Catch:{ Exception -> 0x17af }
            goto L_0x18ff
        L_0x18ef:
            r60 = r12
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r1.media     // Catch:{ Exception -> 0x17af }
            r5 = r3
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument r5 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument) r5     // Catch:{ Exception -> 0x17af }
            r9 = 0
            byte[] r12 = new byte[r9]     // Catch:{ Exception -> 0x17af }
            r5.thumb = r12     // Catch:{ Exception -> 0x17af }
            r3.thumb_h = r9     // Catch:{ Exception -> 0x17af }
            r3.thumb_w = r9     // Catch:{ Exception -> 0x17af }
        L_0x18ff:
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r3 = r1.media     // Catch:{ Exception -> 0x17af }
            int r5 = r15.size     // Catch:{ Exception -> 0x17af }
            r3.size = r5     // Catch:{ Exception -> 0x17af }
            java.lang.String r5 = r15.mime_type     // Catch:{ Exception -> 0x17af }
            r3.mime_type = r5     // Catch:{ Exception -> 0x17af }
            byte[] r3 = r15.key     // Catch:{ Exception -> 0x17af }
            if (r3 == 0) goto L_0x1947
            int r3 = (r6 > r16 ? 1 : (r6 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x1912
            goto L_0x1947
        L_0x1912:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x17af }
            r3.<init>()     // Catch:{ Exception -> 0x17af }
            r73 = r6
            long r5 = r15.id     // Catch:{ Exception -> 0x17af }
            r3.id = r5     // Catch:{ Exception -> 0x17af }
            long r5 = r15.access_hash     // Catch:{ Exception -> 0x17af }
            r3.access_hash = r5     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r5 = r1.media     // Catch:{ Exception -> 0x17af }
            byte[] r6 = r15.key     // Catch:{ Exception -> 0x17af }
            r5.key = r6     // Catch:{ Exception -> 0x17af }
            byte[] r6 = r15.iv     // Catch:{ Exception -> 0x17af }
            r5.iv = r6     // Catch:{ Exception -> 0x17af }
            org.telegram.messenger.SecretChatHelper r5 = r53.getSecretChatHelper()     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner     // Catch:{ Exception -> 0x17af }
            r7 = 0
            r56 = r5
            r57 = r1
            r58 = r6
            r59 = r10
            r60 = r3
            r61 = r7
            r62 = r2
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x17af }
            r23 = r73
            goto L_0x1aaa
        L_0x1947:
            r73 = r6
            if (r8 != 0) goto L_0x197d
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x17af }
            r3.<init>(r13)     // Catch:{ Exception -> 0x17af }
            r3.encryptedChat = r10     // Catch:{ Exception -> 0x17af }
            r5 = 2
            r3.type = r5     // Catch:{ Exception -> 0x17af }
            r3.sendEncryptedRequest = r1     // Catch:{ Exception -> 0x17af }
            r3.originalPath = r11     // Catch:{ Exception -> 0x17af }
            r3.obj = r2     // Catch:{ Exception -> 0x17af }
            if (r60 == 0) goto L_0x196e
            r5 = r60
            r6 = r51
            boolean r7 = r5.containsKey(r6)     // Catch:{ Exception -> 0x17af }
            if (r7 == 0) goto L_0x196e
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x17af }
            r3.parentObject = r5     // Catch:{ Exception -> 0x17af }
            goto L_0x1972
        L_0x196e:
            r7 = r54
            r3.parentObject = r7     // Catch:{ Exception -> 0x17af }
        L_0x1972:
            r5 = 1
            r3.performMediaUpload = r5     // Catch:{ Exception -> 0x17af }
            if (r76 == 0) goto L_0x1979
            r5 = 1
            goto L_0x197a
        L_0x1979:
            r5 = 0
        L_0x197a:
            r3.scheduled = r5     // Catch:{ Exception -> 0x17af }
            r8 = r3
        L_0x197d:
            r3 = r66
            if (r3 == 0) goto L_0x1991
            int r5 = r66.length()     // Catch:{ Exception -> 0x17af }
            if (r5 <= 0) goto L_0x1991
            r9 = r31
            boolean r5 = r3.startsWith(r9)     // Catch:{ Exception -> 0x17af }
            if (r5 == 0) goto L_0x1991
            r8.httpLocation = r3     // Catch:{ Exception -> 0x17af }
        L_0x1991:
            r23 = r73
            int r3 = (r23 > r16 ? 1 : (r23 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x1aaa
            r4.performSendDelayedMessage(r8)     // Catch:{ Exception -> 0x17af }
            goto L_0x1aaa
        L_0x199c:
            r13 = r64
            r3 = r66
        L_0x19a0:
            r23 = r73
            r15 = r6
            r19 = r9
            r7 = r18
            r9 = r31
            r6 = r45
            r11 = r48
            r18 = r8
            r8 = r54
            r52 = r12
            r12 = r5
            r5 = r52
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r15.sizes     // Catch:{ Exception -> 0x1b18 }
            r31 = r9
            r9 = 0
            java.lang.Object r3 = r3.get(r9)     // Catch:{ Exception -> 0x1b18 }
            org.telegram.tgnet.TLRPC$PhotoSize r3 = (org.telegram.tgnet.TLRPC$PhotoSize) r3     // Catch:{ Exception -> 0x1b18 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r15.sizes     // Catch:{ Exception -> 0x1b18 }
            int r20 = r9.size()     // Catch:{ Exception -> 0x1b18 }
            r44 = r15
            r22 = 1
            int r15 = r20 + -1
            java.lang.Object r9 = r9.get(r15)     // Catch:{ Exception -> 0x1b18 }
            org.telegram.tgnet.TLRPC$PhotoSize r9 = (org.telegram.tgnet.TLRPC$PhotoSize) r9     // Catch:{ Exception -> 0x1b18 }
            org.telegram.messenger.ImageLoader.fillPhotoSizeWithBytes(r3)     // Catch:{ Exception -> 0x1b18 }
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r15 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto     // Catch:{ Exception -> 0x1b18 }
            r15.<init>()     // Catch:{ Exception -> 0x1b18 }
            r1.media = r15     // Catch:{ Exception -> 0x1b18 }
            r15.caption = r12     // Catch:{ Exception -> 0x1b18 }
            byte[] r12 = r3.bytes     // Catch:{ Exception -> 0x1b18 }
            if (r12 == 0) goto L_0x19ed
            r54 = r7
            r7 = r15
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r7 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r7     // Catch:{ Exception -> 0x17af }
            r7.thumb = r12     // Catch:{ Exception -> 0x17af }
            r45 = r6
            goto L_0x19f9
        L_0x19ed:
            r54 = r7
            r7 = r15
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto r7 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto) r7     // Catch:{ Exception -> 0x1b18 }
            r45 = r6
            r12 = 0
            byte[] r6 = new byte[r12]     // Catch:{ Exception -> 0x1b18 }
            r7.thumb = r6     // Catch:{ Exception -> 0x1b18 }
        L_0x19f9:
            int r6 = r3.h     // Catch:{ Exception -> 0x1b18 }
            r15.thumb_h = r6     // Catch:{ Exception -> 0x1b18 }
            int r3 = r3.w     // Catch:{ Exception -> 0x1b18 }
            r15.thumb_w = r3     // Catch:{ Exception -> 0x1b18 }
            int r3 = r9.w     // Catch:{ Exception -> 0x1b18 }
            r15.w = r3     // Catch:{ Exception -> 0x1b18 }
            int r3 = r9.h     // Catch:{ Exception -> 0x1b18 }
            r15.h = r3     // Catch:{ Exception -> 0x1b18 }
            int r3 = r9.size     // Catch:{ Exception -> 0x1b18 }
            r15.size = r3     // Catch:{ Exception -> 0x1b18 }
            org.telegram.tgnet.TLRPC$FileLocation r3 = r9.location     // Catch:{ Exception -> 0x1b18 }
            byte[] r3 = r3.key     // Catch:{ Exception -> 0x1b18 }
            if (r3 == 0) goto L_0x1a4a
            int r3 = (r23 > r16 ? 1 : (r23 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x1a18
            goto L_0x1a4a
        L_0x1a18:
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x17af }
            r3.<init>()     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$FileLocation r5 = r9.location     // Catch:{ Exception -> 0x17af }
            long r6 = r5.volume_id     // Catch:{ Exception -> 0x17af }
            r3.id = r6     // Catch:{ Exception -> 0x17af }
            long r6 = r5.secret     // Catch:{ Exception -> 0x17af }
            r3.access_hash = r6     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$DecryptedMessageMedia r6 = r1.media     // Catch:{ Exception -> 0x17af }
            byte[] r7 = r5.key     // Catch:{ Exception -> 0x17af }
            r6.key = r7     // Catch:{ Exception -> 0x17af }
            byte[] r5 = r5.iv     // Catch:{ Exception -> 0x17af }
            r6.iv = r5     // Catch:{ Exception -> 0x17af }
            org.telegram.messenger.SecretChatHelper r5 = r53.getSecretChatHelper()     // Catch:{ Exception -> 0x17af }
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner     // Catch:{ Exception -> 0x17af }
            r7 = 0
            r56 = r5
            r57 = r1
            r58 = r6
            r59 = r10
            r60 = r3
            r61 = r7
            r62 = r2
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x17af }
            goto L_0x1aaa
        L_0x1a4a:
            if (r8 != 0) goto L_0x1a7c
            org.telegram.messenger.SendMessagesHelper$DelayedMessage r3 = new org.telegram.messenger.SendMessagesHelper$DelayedMessage     // Catch:{ Exception -> 0x17af }
            r3.<init>(r13)     // Catch:{ Exception -> 0x17af }
            r3.encryptedChat = r10     // Catch:{ Exception -> 0x17af }
            r6 = 0
            r3.type = r6     // Catch:{ Exception -> 0x17af }
            r3.originalPath = r11     // Catch:{ Exception -> 0x17af }
            r3.sendEncryptedRequest = r1     // Catch:{ Exception -> 0x17af }
            r3.obj = r2     // Catch:{ Exception -> 0x17af }
            if (r5 == 0) goto L_0x1a6d
            r6 = r45
            boolean r7 = r5.containsKey(r6)     // Catch:{ Exception -> 0x17af }
            if (r7 == 0) goto L_0x1a6d
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x17af }
            r3.parentObject = r5     // Catch:{ Exception -> 0x17af }
            goto L_0x1a71
        L_0x1a6d:
            r5 = r54
            r3.parentObject = r5     // Catch:{ Exception -> 0x17af }
        L_0x1a71:
            r5 = 1
            r3.performMediaUpload = r5     // Catch:{ Exception -> 0x17af }
            if (r76 == 0) goto L_0x1a78
            r5 = 1
            goto L_0x1a79
        L_0x1a78:
            r5 = 0
        L_0x1a79:
            r3.scheduled = r5     // Catch:{ Exception -> 0x17af }
            r8 = r3
        L_0x1a7c:
            boolean r3 = android.text.TextUtils.isEmpty(r66)     // Catch:{ Exception -> 0x1b18 }
            if (r3 != 0) goto L_0x1a8f
            r3 = r66
            r5 = r31
            boolean r5 = r3.startsWith(r5)     // Catch:{ Exception -> 0x17af }
            if (r5 == 0) goto L_0x1a8f
            r8.httpLocation = r3     // Catch:{ Exception -> 0x17af }
            goto L_0x1aa3
        L_0x1a8f:
            r3 = r44
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r3.sizes     // Catch:{ Exception -> 0x1b18 }
            int r6 = r5.size()     // Catch:{ Exception -> 0x1b18 }
            r7 = 1
            int r6 = r6 - r7
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x1b18 }
            org.telegram.tgnet.TLRPC$PhotoSize r5 = (org.telegram.tgnet.TLRPC$PhotoSize) r5     // Catch:{ Exception -> 0x1b18 }
            r8.photoSize = r5     // Catch:{ Exception -> 0x1b18 }
            r8.locationParent = r3     // Catch:{ Exception -> 0x1b18 }
        L_0x1aa3:
            int r3 = (r23 > r16 ? 1 : (r23 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x1aaa
            r4.performSendDelayedMessage(r8)     // Catch:{ Exception -> 0x17af }
        L_0x1aaa:
            int r3 = (r23 > r16 ? 1 : (r23 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x1afc
            org.telegram.tgnet.TLObject r3 = r8.sendEncryptedRequest     // Catch:{ Exception -> 0x1af3 }
            if (r3 == 0) goto L_0x1ab5
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r3 = (org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia) r3     // Catch:{ Exception -> 0x17af }
            goto L_0x1abc
        L_0x1ab5:
            org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia r3 = new org.telegram.tgnet.TLRPC$TL_messages_sendEncryptedMultiMedia     // Catch:{ Exception -> 0x1af3 }
            r3.<init>()     // Catch:{ Exception -> 0x1af3 }
            r8.sendEncryptedRequest = r3     // Catch:{ Exception -> 0x1af3 }
        L_0x1abc:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r8.messageObjects     // Catch:{ Exception -> 0x1af3 }
            r5.add(r2)     // Catch:{ Exception -> 0x1af3 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r5 = r8.messages     // Catch:{ Exception -> 0x1af3 }
            r6 = r18
            r5.add(r6)     // Catch:{ Exception -> 0x1af1 }
            java.util.ArrayList<java.lang.String> r5 = r8.originalPaths     // Catch:{ Exception -> 0x1af1 }
            r5.add(r11)     // Catch:{ Exception -> 0x1af1 }
            r5 = 1
            r8.performMediaUpload = r5     // Catch:{ Exception -> 0x1af1 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_decryptedMessage> r5 = r3.messages     // Catch:{ Exception -> 0x1af1 }
            r5.add(r1)     // Catch:{ Exception -> 0x1af1 }
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFile r1 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFile     // Catch:{ Exception -> 0x1af1 }
            r1.<init>()     // Catch:{ Exception -> 0x1af1 }
            r5 = r19
            r7 = 3
            if (r5 == r7) goto L_0x1ae2
            r7 = 7
            if (r5 != r7) goto L_0x1ae4
        L_0x1ae2:
            r16 = 1
        L_0x1ae4:
            r9 = r16
            r1.id = r9     // Catch:{ Exception -> 0x1af1 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputEncryptedFile> r3 = r3.files     // Catch:{ Exception -> 0x1af1 }
            r3.add(r1)     // Catch:{ Exception -> 0x1af1 }
            r4.performSendDelayedMessage(r8)     // Catch:{ Exception -> 0x1af1 }
            goto L_0x1afe
        L_0x1af1:
            r0 = move-exception
            goto L_0x1af6
        L_0x1af3:
            r0 = move-exception
            r6 = r18
        L_0x1af6:
            r1 = r76
            r3 = r0
            r5 = r6
            goto L_0x1CLASSNAME
        L_0x1afc:
            r6 = r18
        L_0x1afe:
            if (r71 != 0) goto L_0x1CLASSNAME
            org.telegram.messenger.MediaDataController r1 = r53.getMediaDataController()     // Catch:{ Exception -> 0x1b15 }
            r11 = r6
            if (r68 == 0) goto L_0x1b0e
            int r3 = r68.getId()     // Catch:{ Exception -> 0x1b0c }
            goto L_0x1b0f
        L_0x1b0c:
            r0 = move-exception
            goto L_0x1b1e
        L_0x1b0e:
            r3 = 0
        L_0x1b0f:
            r5 = 0
            r1.cleanDraft(r13, r3, r5)     // Catch:{ Exception -> 0x1b0c }
            goto L_0x1CLASSNAME
        L_0x1b15:
            r0 = move-exception
            r11 = r6
            goto L_0x1b1e
        L_0x1b18:
            r0 = move-exception
            r11 = r18
            goto L_0x1b1e
        L_0x1b1c:
            r0 = move-exception
            r11 = r8
        L_0x1b1e:
            r1 = r76
            goto L_0x1c4f
        L_0x1b22:
            r27 = r61
            r2 = r3
            r4 = r6
            r1 = r9
            r11 = r13
            r7 = r14
            r6 = r54
            r14 = r56
            r3 = r72
            r13 = r5
            r5 = r60
            if (r10 != 0) goto L_0x1baa
            org.telegram.tgnet.TLRPC$TL_messages_sendMessage r9 = new org.telegram.tgnet.TLRPC$TL_messages_sendMessage     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.<init>()     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.message = r6     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r71 != 0) goto L_0x1b3f
            r6 = 1
            goto L_0x1b40
        L_0x1b3f:
            r6 = 0
        L_0x1b40:
            r9.clear_draft = r6     // Catch:{ Exception -> 0x1CLASSNAME }
            boolean r6 = r11.silent     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.silent = r6     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.peer = r13     // Catch:{ Exception -> 0x1CLASSNAME }
            long r12 = r11.random_id     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.random_id = r12     // Catch:{ Exception -> 0x1CLASSNAME }
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r6 = r11.reply_to     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r6 == 0) goto L_0x1b5c
            int r6 = r6.reply_to_msg_id     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r6 == 0) goto L_0x1b5c
            int r10 = r9.flags     // Catch:{ Exception -> 0x1CLASSNAME }
            r12 = 1
            r10 = r10 | r12
            r9.flags = r10     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.reply_to_msg_id = r6     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1b5c:
            if (r70 != 0) goto L_0x1b61
            r6 = 1
            r9.no_webpage = r6     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1b61:
            if (r3 == 0) goto L_0x1b72
            boolean r6 = r72.isEmpty()     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r6 != 0) goto L_0x1b72
            r9.entities = r3     // Catch:{ Exception -> 0x1CLASSNAME }
            int r3 = r9.flags     // Catch:{ Exception -> 0x1CLASSNAME }
            r6 = 8
            r3 = r3 | r6
            r9.flags = r3     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1b72:
            if (r1 == 0) goto L_0x1b7c
            r9.schedule_date = r1     // Catch:{ Exception -> 0x1CLASSNAME }
            int r3 = r9.flags     // Catch:{ Exception -> 0x1CLASSNAME }
            r3 = r3 | 1024(0x400, float:1.435E-42)
            r9.flags = r3     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1b7c:
            r3 = 0
            r6 = 0
            if (r1 == 0) goto L_0x1b82
            r10 = 1
            goto L_0x1b83
        L_0x1b82:
            r10 = 0
        L_0x1b83:
            r54 = r53
            r55 = r9
            r56 = r2
            r57 = r3
            r58 = r6
            r59 = r27
            r60 = r5
            r61 = r10
            r54.performSendMessageRequest(r55, r56, r57, r58, r59, r60, r61)     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r71 != 0) goto L_0x1CLASSNAME
            org.telegram.messenger.MediaDataController r3 = r53.getMediaDataController()     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r68 == 0) goto L_0x1ba3
            int r5 = r68.getId()     // Catch:{ Exception -> 0x1CLASSNAME }
            goto L_0x1ba4
        L_0x1ba3:
            r5 = 0
        L_0x1ba4:
            r6 = 0
            r3.cleanDraft(r7, r5, r6)     // Catch:{ Exception -> 0x1CLASSNAME }
            goto L_0x1CLASSNAME
        L_0x1baa:
            org.telegram.tgnet.TLRPC$TL_decryptedMessage r9 = new org.telegram.tgnet.TLRPC$TL_decryptedMessage     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.<init>()     // Catch:{ Exception -> 0x1CLASSNAME }
            int r12 = r11.ttl     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.ttl = r12     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r3 == 0) goto L_0x1bc3
            boolean r12 = r72.isEmpty()     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r12 != 0) goto L_0x1bc3
            r9.entities = r3     // Catch:{ Exception -> 0x1CLASSNAME }
            int r3 = r9.flags     // Catch:{ Exception -> 0x1CLASSNAME }
            r3 = r3 | 128(0x80, float:1.794E-43)
            r9.flags = r3     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1bc3:
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r11.reply_to     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r3 == 0) goto L_0x1bd6
            long r12 = r3.reply_to_random_id     // Catch:{ Exception -> 0x1CLASSNAME }
            int r3 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x1bd6
            r9.reply_to_random_id = r12     // Catch:{ Exception -> 0x1CLASSNAME }
            int r3 = r9.flags     // Catch:{ Exception -> 0x1CLASSNAME }
            r12 = 8
            r3 = r3 | r12
            r9.flags = r3     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1bd6:
            if (r5 == 0) goto L_0x1bec
            java.lang.Object r3 = r5.get(r14)     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r3 == 0) goto L_0x1bec
            java.lang.Object r3 = r5.get(r14)     // Catch:{ Exception -> 0x1CLASSNAME }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.via_bot_name = r3     // Catch:{ Exception -> 0x1CLASSNAME }
            int r3 = r9.flags     // Catch:{ Exception -> 0x1CLASSNAME }
            r3 = r3 | 2048(0x800, float:2.87E-42)
            r9.flags = r3     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1bec:
            boolean r3 = r11.silent     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.silent = r3     // Catch:{ Exception -> 0x1CLASSNAME }
            long r12 = r11.random_id     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.random_id = r12     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.message = r6     // Catch:{ Exception -> 0x1CLASSNAME }
            r15 = r39
            if (r15 == 0) goto L_0x1CLASSNAME
            java.lang.String r3 = r15.url     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r3 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaWebPage     // Catch:{ Exception -> 0x1CLASSNAME }
            r3.<init>()     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.media = r3     // Catch:{ Exception -> 0x1CLASSNAME }
            java.lang.String r5 = r15.url     // Catch:{ Exception -> 0x1CLASSNAME }
            r3.url = r5     // Catch:{ Exception -> 0x1CLASSNAME }
            int r3 = r9.flags     // Catch:{ Exception -> 0x1CLASSNAME }
            r3 = r3 | 512(0x200, float:7.175E-43)
            r9.flags = r3     // Catch:{ Exception -> 0x1CLASSNAME }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty r3 = new org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaEmpty     // Catch:{ Exception -> 0x1CLASSNAME }
            r3.<init>()     // Catch:{ Exception -> 0x1CLASSNAME }
            r9.media = r3     // Catch:{ Exception -> 0x1CLASSNAME }
        L_0x1CLASSNAME:
            org.telegram.messenger.SecretChatHelper r3 = r53.getSecretChatHelper()     // Catch:{ Exception -> 0x1CLASSNAME }
            org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner     // Catch:{ Exception -> 0x1CLASSNAME }
            r6 = 0
            r12 = 0
            r56 = r3
            r57 = r9
            r58 = r5
            r59 = r10
            r60 = r6
            r61 = r12
            r62 = r2
            r56.performSendEncryptedRequest(r57, r58, r59, r60, r61, r62)     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r71 != 0) goto L_0x1CLASSNAME
            org.telegram.messenger.MediaDataController r3 = r53.getMediaDataController()     // Catch:{ Exception -> 0x1CLASSNAME }
            if (r68 == 0) goto L_0x1c3d
            int r5 = r68.getId()     // Catch:{ Exception -> 0x1CLASSNAME }
            goto L_0x1c3e
        L_0x1c3d:
            r5 = 0
        L_0x1c3e:
            r6 = 0
            r3.cleanDraft(r7, r5, r6)     // Catch:{ Exception -> 0x1CLASSNAME }
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            goto L_0x1c4f
        L_0x1CLASSNAME:
            r0 = move-exception
            r2 = r3
            r4 = r6
            goto L_0x1c4d
        L_0x1CLASSNAME:
            r0 = move-exception
            r4 = r53
            r2 = r3
        L_0x1c4d:
            r1 = r9
            r11 = r13
        L_0x1c4f:
            r3 = r0
            r5 = r11
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
            r4 = r53
            goto L_0x1CLASSNAME
        L_0x1CLASSNAME:
            r0 = move-exception
        L_0x1CLASSNAME:
            r1 = r9
            r11 = r13
            r3 = r0
            goto L_0x031e
        L_0x1c5c:
            r0 = move-exception
            r1 = r9
            r5 = r3
            goto L_0x0418
        L_0x1CLASSNAME:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            org.telegram.messenger.MessagesStorage r3 = r53.getMessagesStorage()
            if (r1 == 0) goto L_0x1c6c
            r1 = 1
            goto L_0x1c6d
        L_0x1c6c:
            r1 = 0
        L_0x1c6d:
            r3.markMessageAsSendError(r5, r1)
            if (r2 == 0) goto L_0x1CLASSNAME
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            r2 = 2
            r1.send_state = r2
        L_0x1CLASSNAME:
            org.telegram.messenger.NotificationCenter r1 = r53.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.messageSendError
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            int r6 = r5.id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r7 = 0
            r3[r7] = r6
            r1.postNotificationName(r2, r3)
            int r1 = r5.id
            r4.processSentMessage(r1)
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
            SparseArray sparseArray2 = null;
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
                    TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(MessagesController.getUpdateChannelId(tLRPC$TL_updateNewChannelMessage)));
                    if (!((chat != null && !chat.megagroup) || (tLRPC$TL_messageReplyHeader = tLRPC$TL_updateNewChannelMessage.message.reply_to) == null || (tLRPC$TL_messageReplyHeader.reply_to_top_id == 0 && tLRPC$TL_messageReplyHeader.reply_to_msg_id == 0))) {
                        if (sparseArray2 == null) {
                            sparseArray2 = new SparseArray();
                        }
                        int dialogId = (int) MessageObject.getDialogId(tLRPC$TL_updateNewChannelMessage.message);
                        SparseArray sparseArray3 = (SparseArray) sparseArray2.get(dialogId);
                        if (sparseArray3 == null) {
                            sparseArray3 = new SparseArray();
                            sparseArray2.put(dialogId, sparseArray3);
                        }
                        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader2 = tLRPC$TL_updateNewChannelMessage.message.reply_to;
                        int i3 = tLRPC$TL_messageReplyHeader2.reply_to_top_id;
                        if (i3 == 0) {
                            i3 = tLRPC$TL_messageReplyHeader2.reply_to_msg_id;
                        }
                        TLRPC$TL_messageReplies tLRPC$TL_messageReplies = (TLRPC$TL_messageReplies) sparseArray3.get(i3);
                        if (tLRPC$TL_messageReplies == null) {
                            tLRPC$TL_messageReplies = new TLRPC$TL_messageReplies();
                            sparseArray3.put(i3, tLRPC$TL_messageReplies);
                        }
                        TLRPC$Peer tLRPC$Peer = tLRPC$TL_updateNewChannelMessage.message.from_id;
                        if (tLRPC$Peer != null) {
                            tLRPC$TL_messageReplies.recent_repliers.add(0, tLRPC$Peer);
                        }
                        tLRPC$TL_messageReplies.replies++;
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
            if (sparseArray2 != null) {
                getMessagesStorage().putChannelViews((SparseArray<SparseIntArray>) null, (SparseArray<SparseIntArray>) null, sparseArray2, true, true);
                getNotificationCenter().postNotificationName(NotificationCenter.didUpdateMessagesViews, null, null, sparseArray2, Boolean.TRUE);
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
                    SparseArray sparseArray4 = sparseArray;
                    getNotificationCenter().postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(i6), Integer.valueOf(tLRPC$Message7.id), tLRPC$Message7, Long.valueOf(tLRPC$Message7.dialog_id), Long.valueOf(j), Integer.valueOf(mediaExistanceFlags), Boolean.valueOf(z));
                    TLRPC$Updates tLRPC$Updates4 = tLRPC$Updates2;
                    DispatchQueue storageQueue = getMessagesStorage().getStorageQueue();
                    SparseArray sparseArray5 = sparseArray4;
                    SendMessagesHelper$$ExternalSyntheticLambda50 sendMessagesHelper$$ExternalSyntheticLambda50 = r0;
                    SendMessagesHelper$$ExternalSyntheticLambda50 sendMessagesHelper$$ExternalSyntheticLambda502 = new SendMessagesHelper$$ExternalSyntheticLambda50(this, tLRPC$Message7, i6, z, arrayList6, j, mediaExistanceFlags);
                    storageQueue.postRunnable(sendMessagesHelper$$ExternalSyntheticLambda50);
                    i4 = i7 + 1;
                    sparseArray = sparseArray5;
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
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, Long.valueOf((long) i), tLRPC$Message2.id, 0, false, tLRPC$Message2.peer_id.channel_id, z ? 1 : 0);
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
    /* JADX WARNING: Removed duplicated region for block: B:108:0x02a7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$performSendMessageRequest$59(boolean r29, org.telegram.tgnet.TLRPC$TL_error r30, org.telegram.tgnet.TLRPC$Message r31, org.telegram.tgnet.TLObject r32, org.telegram.messenger.MessageObject r33, java.lang.String r34, org.telegram.tgnet.TLObject r35) {
        /*
            r28 = this;
            r8 = r28
            r9 = r29
            r0 = r30
            r10 = r31
            r1 = r32
            if (r0 != 0) goto L_0x035d
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
            r0 = r28
            r1 = r33
            r15 = r4
            r4 = r17
            r11 = 4
            r5 = r18
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r0 = r33.getMediaExistanceFlags()
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
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r31)
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
            org.telegram.messenger.MessagesController r1 = r28.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            long r2 = r10.dialog_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            if (r1 != 0) goto L_0x00c9
            org.telegram.messenger.MessagesStorage r1 = r28.getMessagesStorage()
            boolean r2 = r10.out
            long r3 = r10.dialog_id
            int r1 = r1.getDialogReadMax(r2, r3)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.messenger.MessagesController r2 = r28.getMessagesController()
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
            goto L_0x028b
        L_0x00e7:
            r11 = 4
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$Updates
            if (r4 == 0) goto L_0x0289
            r15 = r1
            org.telegram.tgnet.TLRPC$Updates r15 = (org.telegram.tgnet.TLRPC$Updates) r15
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r1 = r15.updates
            r4 = 0
        L_0x00f2:
            int r5 = r1.size()
            if (r4 >= r5) goto L_0x01bc
            java.lang.Object r5 = r1.get(r4)
            org.telegram.tgnet.TLRPC$Update r5 = (org.telegram.tgnet.TLRPC$Update) r5
            boolean r3 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewMessage
            if (r3 == 0) goto L_0x011a
            org.telegram.tgnet.TLRPC$TL_updateNewMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewMessage) r5
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda64 r11 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda64
            r11.<init>(r8, r5)
            r2.postRunnable(r11)
            r1.remove(r4)
        L_0x0116:
            r12 = r3
            r2 = 0
            goto L_0x01be
        L_0x011a:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage
            if (r2 == 0) goto L_0x01a4
            org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewChannelMessage) r5
            int r2 = org.telegram.messenger.MessagesController.getUpdateChannelId(r5)
            org.telegram.messenger.MessagesController r3 = r28.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r3.getChat(r2)
            if (r2 == 0) goto L_0x0136
            boolean r2 = r2.megagroup
            if (r2 == 0) goto L_0x018f
        L_0x0136:
            org.telegram.tgnet.TLRPC$Message r2 = r5.message
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r2 = r2.reply_to
            if (r2 == 0) goto L_0x018f
            int r3 = r2.reply_to_top_id
            if (r3 != 0) goto L_0x0144
            int r2 = r2.reply_to_msg_id
            if (r2 == 0) goto L_0x018f
        L_0x0144:
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            long r12 = org.telegram.messenger.MessageObject.getDialogId(r3)
            int r3 = (int) r12
            java.lang.Object r12 = r2.get(r3)
            android.util.SparseArray r12 = (android.util.SparseArray) r12
            if (r12 != 0) goto L_0x0160
            android.util.SparseArray r12 = new android.util.SparseArray
            r12.<init>()
            r2.put(r3, r12)
        L_0x0160:
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r3 = r3.reply_to
            int r13 = r3.reply_to_top_id
            if (r13 == 0) goto L_0x0169
            goto L_0x016b
        L_0x0169:
            int r13 = r3.reply_to_msg_id
        L_0x016b:
            java.lang.Object r3 = r12.get(r13)
            org.telegram.tgnet.TLRPC$TL_messageReplies r3 = (org.telegram.tgnet.TLRPC$TL_messageReplies) r3
            if (r3 != 0) goto L_0x017b
            org.telegram.tgnet.TLRPC$TL_messageReplies r3 = new org.telegram.tgnet.TLRPC$TL_messageReplies
            r3.<init>()
            r12.put(r13, r3)
        L_0x017b:
            org.telegram.tgnet.TLRPC$Message r12 = r5.message
            org.telegram.tgnet.TLRPC$Peer r12 = r12.from_id
            if (r12 == 0) goto L_0x0187
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Peer> r13 = r3.recent_repliers
            r11 = 0
            r13.add(r11, r12)
        L_0x0187:
            int r11 = r3.replies
            r12 = 1
            int r13 = r11 + 1
            r3.replies = r13
            goto L_0x0190
        L_0x018f:
            r2 = 0
        L_0x0190:
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            org.telegram.messenger.DispatchQueue r12 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda63 r13 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda63
            r13.<init>(r8, r5)
            r12.postRunnable(r13)
            r1.remove(r4)
            r12 = r3
            goto L_0x01be
        L_0x01a4:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage
            if (r2 == 0) goto L_0x01b4
            org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage r5 = (org.telegram.tgnet.TLRPC$TL_updateNewScheduledMessage) r5
            org.telegram.tgnet.TLRPC$Message r3 = r5.message
            r7.add(r3)
            r1.remove(r4)
            goto L_0x0116
        L_0x01b4:
            int r4 = r4 + 1
            r3 = 2147483646(0x7ffffffe, float:NaN)
            r11 = 4
            goto L_0x00f2
        L_0x01bc:
            r2 = 0
            r12 = 0
        L_0x01be:
            if (r2 == 0) goto L_0x01ec
            org.telegram.messenger.MessagesStorage r19 = r28.getMessagesStorage()
            r20 = 0
            r21 = 0
            r23 = 1
            r24 = 1
            r22 = r2
            r19.putChannelViews(r20, r21, r22, r23, r24)
            org.telegram.messenger.NotificationCenter r1 = r28.getNotificationCenter()
            int r3 = org.telegram.messenger.NotificationCenter.didUpdateMessagesViews
            r4 = 4
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r4 = 0
            r13 = 0
            r5[r13] = r4
            r11 = 1
            r5[r11] = r4
            r4 = 2
            r5[r4] = r2
            java.lang.Boolean r2 = java.lang.Boolean.TRUE
            r4 = 3
            r5[r4] = r2
            r1.postNotificationName(r3, r5)
        L_0x01ec:
            if (r12 == 0) goto L_0x0279
            org.telegram.messenger.MessageObject.getDialogId(r12)
            if (r0 == 0) goto L_0x01fc
            int r0 = r12.date
            r1 = 2147483646(0x7ffffffe, float:NaN)
            if (r0 == r1) goto L_0x01fc
            r13 = 0
            goto L_0x01fd
        L_0x01fc:
            r13 = r9
        L_0x01fd:
            org.telegram.messenger.ImageLoader.saveMessageThumbs(r12)
            if (r13 != 0) goto L_0x0242
            org.telegram.messenger.MessagesController r0 = r28.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r0 = r0.dialogs_read_outbox_max
            long r1 = r12.dialog_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            java.lang.Object r0 = r0.get(r1)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0235
            org.telegram.messenger.MessagesStorage r0 = r28.getMessagesStorage()
            boolean r1 = r12.out
            long r2 = r12.dialog_id
            int r0 = r0.getDialogReadMax(r1, r2)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.messenger.MessagesController r1 = r28.getMessagesController()
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.dialogs_read_outbox_max
            long r2 = r12.dialog_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r1.put(r2, r0)
        L_0x0235:
            int r0 = r0.intValue()
            int r1 = r12.id
            if (r0 >= r1) goto L_0x023f
            r0 = 1
            goto L_0x0240
        L_0x023f:
            r0 = 0
        L_0x0240:
            r12.unread = r0
        L_0x0242:
            r5 = r33
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.lang.String r1 = r12.post_author
            r0.post_author = r1
            int r1 = r12.flags
            r1 = r1 & r16
            if (r1 == 0) goto L_0x025a
            int r1 = r12.ttl_period
            r0.ttl_period = r1
            int r1 = r0.flags
            r1 = r1 | r16
            r0.flags = r1
        L_0x025a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r12.entities
            r0.entities = r1
            int r3 = r12.id
            r16 = 0
            r0 = r28
            r1 = r33
            r2 = r12
            r4 = r34
            r5 = r16
            r0.updateMediaPaths(r1, r2, r3, r4, r5)
            int r0 = r33.getMediaExistanceFlags()
            int r1 = r12.id
            r10.id = r1
            r1 = r0
            r0 = 0
            goto L_0x027c
        L_0x0279:
            r13 = r9
            r0 = 1
            r1 = 0
        L_0x027c:
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda68 r3 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda68
            r3.<init>(r8, r15)
            r2.postRunnable(r3)
            r15 = r0
            r12 = r1
            goto L_0x028c
        L_0x0289:
            r13 = r9
            r12 = 0
        L_0x028b:
            r15 = 0
        L_0x028c:
            boolean r0 = org.telegram.messenger.MessageObject.isLiveLocationMessage(r31)
            if (r0 == 0) goto L_0x02a5
            int r0 = r10.via_bot_id
            if (r0 != 0) goto L_0x02a5
            java.lang.String r0 = r10.via_bot_name
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x02a5
            org.telegram.messenger.LocationController r0 = r28.getLocationController()
            r0.addSharingLocation(r10)
        L_0x02a5:
            if (r15 != 0) goto L_0x0369
            org.telegram.messenger.StatsController r0 = r28.getStatsController()
            int r1 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType()
            r2 = 1
            r0.incrementSentItemsCount(r1, r2, r2)
            r0 = 0
            r10.send_state = r0
            if (r9 == 0) goto L_0x02ff
            if (r13 != 0) goto L_0x02ff
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            r0.add(r1)
            org.telegram.messenger.MessagesController r19 = r28.getMessagesController()
            r21 = 0
            r22 = 0
            long r1 = r10.dialog_id
            org.telegram.tgnet.TLRPC$Peer r3 = r10.peer_id
            int r3 = r3.channel_id
            r26 = 0
            r27 = 1
            r20 = r0
            r23 = r1
            r25 = r3
            r19.deleteMessages(r20, r21, r22, r23, r25, r26, r27)
            org.telegram.messenger.MessagesStorage r0 = r28.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r12 = r0.getStorageQueue()
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda34 r13 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda34
            r0 = r13
            r1 = r28
            r2 = r7
            r3 = r33
            r4 = r31
            r5 = r6
            r6 = r29
            r7 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r12.postRunnable(r13)
            goto L_0x0369
        L_0x02ff:
            org.telegram.messenger.NotificationCenter r0 = r28.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.messageReceivedByServer
            r2 = 7
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r6)
            r4 = 0
            r2[r4] = r3
            int r3 = r10.id
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 1
            r2[r4] = r3
            r3 = 2
            r2[r3] = r10
            long r3 = r10.dialog_id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r4 = 3
            r2[r4] = r3
            r3 = 0
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r4 = 4
            r2[r4] = r3
            r3 = 5
            java.lang.Integer r4 = java.lang.Integer.valueOf(r12)
            r2[r3] = r4
            r3 = 6
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r29)
            r2[r3] = r4
            r0.postNotificationName(r1, r2)
            org.telegram.messenger.MessagesStorage r0 = r28.getMessagesStorage()
            org.telegram.messenger.DispatchQueue r13 = r0.getStorageQueue()
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda49 r5 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda49
            r0 = r5
            r1 = r28
            r2 = r31
            r3 = r6
            r4 = r29
            r6 = r5
            r5 = r7
            r7 = r6
            r6 = r12
            r12 = r7
            r7 = r14
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r13.postRunnable(r12)
            goto L_0x0369
        L_0x035d:
            int r1 = r8.currentAccount
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r35
            r4 = 0
            org.telegram.ui.Components.AlertsCreator.processError(r1, r0, r4, r2, r3)
            r15 = 1
        L_0x0369:
            if (r15 == 0) goto L_0x03ab
            org.telegram.messenger.MessagesStorage r0 = r28.getMessagesStorage()
            r0.markMessageAsSendError(r10, r9)
            r0 = 2
            r10.send_state = r0
            org.telegram.messenger.NotificationCenter r0 = r28.getNotificationCenter()
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
            boolean r0 = org.telegram.messenger.MessageObject.isVideoMessage(r31)
            if (r0 != 0) goto L_0x03a1
            boolean r0 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r31)
            if (r0 != 0) goto L_0x03a1
            boolean r0 = org.telegram.messenger.MessageObject.isNewGifMessage(r31)
            if (r0 == 0) goto L_0x03a6
        L_0x03a1:
            java.lang.String r0 = r10.attachPath
            r8.stopVideoService(r0)
        L_0x03a6:
            int r0 = r10.id
            r8.removeFromSendingMessages(r0, r9)
        L_0x03ab:
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
        getMessagesStorage().updateMessageStateAndId(tLRPC$Message2.random_id, Long.valueOf((long) i), tLRPC$Message2.id, 0, false, tLRPC$Message2.peer_id.channel_id, z ? 1 : 0);
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

    /* JADX WARNING: Code restructure failed: missing block: B:119:0x021f, code lost:
        r3 = new java.lang.StringBuilder();
        r3.append(r2.location.volume_id);
        r4 = r16;
        r3.append(r4);
        r3.append(r2.location.local_id);
        r3 = r3.toString();
        r6 = r8.location.volume_id + r4 + r8.location.local_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0259, code lost:
        if (r3.equals(r6) == false) goto L_0x025c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x025c, code lost:
        r10 = new java.io.File(org.telegram.messenger.FileLoader.getDirectory(4), r3 + ".jpg");
        r11 = r7.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0279, code lost:
        if (r11.ttl_seconds != 0) goto L_0x0295;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x0284, code lost:
        if (r11.photo.sizes.size() == 1) goto L_0x0290;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x028a, code lost:
        if (r8.w > 90) goto L_0x0290;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x028e, code lost:
        if (r8.h <= 90) goto L_0x0295;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x0290, code lost:
        r11 = org.telegram.messenger.FileLoader.getPathToAttach(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0295, code lost:
        r11 = new java.io.File(org.telegram.messenger.FileLoader.getDirectory(4), r6 + ".jpg");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x02ae, code lost:
        r10.renameTo(r11);
        org.telegram.messenger.ImageLoader.getInstance().replaceImageInCache(r3, r6, org.telegram.messenger.ImageLocation.getForPhoto(r8, r7.media.photo), r1);
        r2.location = r8.location;
        r2.size = r8.size;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x02c8, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004c, code lost:
        r5 = (r5 = r7.media).photo;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x05bd, code lost:
        if (org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r7.media.document, true) != false) goto L_0x05c1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x05e8  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x064b  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0107  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMediaPaths(org.telegram.messenger.MessageObject r18, org.telegram.tgnet.TLRPC$Message r19, int r20, java.lang.String r21, boolean r22) {
        /*
            r17 = this;
            r0 = r18
            r7 = r19
            r8 = r21
            r1 = r22
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            java.lang.String r4 = "_"
            if (r2 == 0) goto L_0x0138
            boolean r2 = r18.isLiveLocation()
            if (r2 == 0) goto L_0x0024
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0024
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            int r2 = r2.period
            r5.period = r2
            goto L_0x00d1
        L_0x0024:
            boolean r2 = r18.isDice()
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
            java.lang.String r11 = org.telegram.messenger.FileRefController.getKeyForParentObject(r18)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            if (r7 == 0) goto L_0x0107
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            java.lang.String r10 = org.telegram.messenger.FileRefController.getKeyForParentObject(r19)
            r11.append(r10)
            java.lang.String r10 = r11.toString()
            goto L_0x012c
        L_0x0107:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "strippedmessage"
            r10.append(r11)
            r11 = r20
            r10.append(r11)
            r10.append(r4)
            int r11 = r18.getChannelId()
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
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            java.lang.String r12 = "sent_"
            java.lang.String r13 = ".jpg"
            r15 = 0
            r3 = 1
            if (r6 == 0) goto L_0x035e
            org.telegram.tgnet.TLRPC$Photo r6 = r2.photo
            if (r6 == 0) goto L_0x035e
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r9.media
            boolean r14 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r14 == 0) goto L_0x035e
            org.telegram.tgnet.TLRPC$Photo r6 = r6.photo
            if (r6 == 0) goto L_0x035e
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x0187
            boolean r0 = r0.scheduled
            if (r0 != 0) goto L_0x0187
            org.telegram.messenger.MessagesStorage r0 = r17.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r12)
            org.telegram.tgnet.TLRPC$Peer r12 = r7.peer_id
            int r12 = r12.channel_id
            r6.append(r12)
            r6.append(r4)
            int r12 = r7.id
            r6.append(r12)
            java.lang.String r6 = r6.toString()
            r0.putSentFile(r8, r2, r15, r6)
        L_0x0187:
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            int r0 = r0.size()
            if (r0 != r3) goto L_0x01b3
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.sizes
            java.lang.Object r0 = r0.get(r15)
            org.telegram.tgnet.TLRPC$PhotoSize r0 = (org.telegram.tgnet.TLRPC$PhotoSize) r0
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.location
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r0 == 0) goto L_0x01b3
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Photo r1 = r1.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r1.sizes
            r0.sizes = r1
            goto L_0x0340
        L_0x01b3:
            r0 = 0
        L_0x01b4:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            int r2 = r2.size()
            if (r0 >= r2) goto L_0x0340
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.sizes
            java.lang.Object r2 = r2.get(r0)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = (org.telegram.tgnet.TLRPC$PhotoSize) r2
            if (r2 == 0) goto L_0x0337
            org.telegram.tgnet.TLRPC$FileLocation r6 = r2.location
            if (r6 == 0) goto L_0x0337
            java.lang.String r6 = r2.type
            if (r6 != 0) goto L_0x01d8
            goto L_0x0337
        L_0x01d8:
            r6 = 0
        L_0x01d9:
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r7.media
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            int r8 = r8.size()
            if (r6 >= r8) goto L_0x02d4
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r7.media
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.sizes
            java.lang.Object r8 = r8.get(r6)
            org.telegram.tgnet.TLRPC$PhotoSize r8 = (org.telegram.tgnet.TLRPC$PhotoSize) r8
            if (r8 == 0) goto L_0x02cc
            org.telegram.tgnet.TLRPC$FileLocation r12 = r8.location
            if (r12 == 0) goto L_0x02cc
            boolean r12 = r8 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r12 != 0) goto L_0x02cc
            java.lang.String r12 = r8.type
            if (r12 != 0) goto L_0x0201
            goto L_0x02cc
        L_0x0201:
            org.telegram.tgnet.TLRPC$FileLocation r14 = r2.location
            r16 = r4
            long r3 = r14.volume_id
            int r14 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r14 != 0) goto L_0x0213
            java.lang.String r3 = r2.type
            boolean r3 = r12.equals(r3)
            if (r3 != 0) goto L_0x021f
        L_0x0213:
            int r3 = r8.w
            int r4 = r2.w
            if (r3 != r4) goto L_0x02ca
            int r3 = r8.h
            int r4 = r2.h
            if (r3 != r4) goto L_0x02ca
        L_0x021f:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r4 = r2.location
            long r10 = r4.volume_id
            r3.append(r10)
            r4 = r16
            r3.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r6 = r2.location
            int r6 = r6.local_id
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r10 = r8.location
            long r10 = r10.volume_id
            r6.append(r10)
            r6.append(r4)
            org.telegram.tgnet.TLRPC$FileLocation r10 = r8.location
            int r10 = r10.local_id
            r6.append(r10)
            java.lang.String r6 = r6.toString()
            boolean r10 = r3.equals(r6)
            if (r10 == 0) goto L_0x025c
            goto L_0x02c8
        L_0x025c:
            java.io.File r10 = new java.io.File
            r11 = 4
            java.io.File r12 = org.telegram.messenger.FileLoader.getDirectory(r11)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r3)
            r11.append(r13)
            java.lang.String r11 = r11.toString()
            r10.<init>(r12, r11)
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r7.media
            int r12 = r11.ttl_seconds
            if (r12 != 0) goto L_0x0295
            org.telegram.tgnet.TLRPC$Photo r11 = r11.photo
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r11 = r11.sizes
            int r11 = r11.size()
            r12 = 1
            if (r11 == r12) goto L_0x0290
            int r11 = r8.w
            r12 = 90
            if (r11 > r12) goto L_0x0290
            int r11 = r8.h
            if (r11 <= r12) goto L_0x0295
        L_0x0290:
            java.io.File r11 = org.telegram.messenger.FileLoader.getPathToAttach(r8)
            goto L_0x02ae
        L_0x0295:
            java.io.File r11 = new java.io.File
            r12 = 4
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r12)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r6)
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r11.<init>(r14, r12)
        L_0x02ae:
            r10.renameTo(r11)
            org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.getInstance()
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r7.media
            org.telegram.tgnet.TLRPC$Photo r11 = r11.photo
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForPhoto((org.telegram.tgnet.TLRPC$PhotoSize) r8, (org.telegram.tgnet.TLRPC$Photo) r11)
            r10.replaceImageInCache(r3, r6, r11, r1)
            org.telegram.tgnet.TLRPC$FileLocation r3 = r8.location
            r2.location = r3
            int r3 = r8.size
            r2.size = r3
        L_0x02c8:
            r3 = 1
            goto L_0x02d5
        L_0x02ca:
            r4 = r16
        L_0x02cc:
            int r6 = r6 + 1
            r3 = 1
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            goto L_0x01d9
        L_0x02d4:
            r3 = 0
        L_0x02d5:
            if (r3 != 0) goto L_0x0337
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
            if (r2 == 0) goto L_0x0337
            if (r5 == 0) goto L_0x0337
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
            goto L_0x0338
        L_0x0337:
            r10 = 0
        L_0x0338:
            int r0 = r0 + 1
            r3 = 1
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            goto L_0x01b4
        L_0x0340:
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
            goto L_0x06a9
        L_0x035e:
            r10 = 0
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r3 == 0) goto L_0x0654
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            if (r3 == 0) goto L_0x0654
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r9.media
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x0654
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            if (r3 == 0) goto L_0x0654
            int r2 = r2.ttl_seconds
            if (r2 != 0) goto L_0x0412
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            if (r2 == 0) goto L_0x038b
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r3 = r2.mediaEntities
            if (r3 != 0) goto L_0x0412
            java.lang.String r2 = r2.paintPath
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 == 0) goto L_0x0412
            org.telegram.messenger.VideoEditedInfo r2 = r0.videoEditedInfo
            org.telegram.messenger.MediaController$CropState r2 = r2.cropState
            if (r2 != 0) goto L_0x0412
        L_0x038b:
            boolean r2 = org.telegram.messenger.MessageObject.isVideoMessage(r19)
            if (r2 != 0) goto L_0x0397
            boolean r3 = org.telegram.messenger.MessageObject.isGifMessage(r19)
            if (r3 == 0) goto L_0x03db
        L_0x0397:
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r9.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r5)
            if (r3 != r5) goto L_0x03db
            boolean r3 = r0.scheduled
            if (r3 != 0) goto L_0x03d4
            org.telegram.messenger.MessagesStorage r3 = r17.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            r6 = 2
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r12)
            org.telegram.tgnet.TLRPC$Peer r12 = r7.peer_id
            int r12 = r12.channel_id
            r11.append(r12)
            r11.append(r4)
            int r12 = r7.id
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            r3.putSentFile(r8, r5, r6, r11)
        L_0x03d4:
            if (r2 == 0) goto L_0x0412
            java.lang.String r2 = r9.attachPath
            r7.attachPath = r2
            goto L_0x0412
        L_0x03db:
            boolean r2 = org.telegram.messenger.MessageObject.isVoiceMessage(r19)
            if (r2 != 0) goto L_0x0412
            boolean r2 = org.telegram.messenger.MessageObject.isRoundVideoMessage(r19)
            if (r2 != 0) goto L_0x0412
            boolean r2 = r0.scheduled
            if (r2 != 0) goto L_0x0412
            org.telegram.messenger.MessagesStorage r2 = r17.getMessagesStorage()
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r3.document
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r12)
            org.telegram.tgnet.TLRPC$Peer r6 = r7.peer_id
            int r6 = r6.channel_id
            r5.append(r6)
            r5.append(r4)
            int r6 = r7.id
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 1
            r2.putSentFile(r8, r3, r6, r5)
        L_0x0412:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r3 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3)
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r7.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r3)
            if (r2 == 0) goto L_0x04cb
            org.telegram.tgnet.TLRPC$FileLocation r5 = r2.location
            if (r5 == 0) goto L_0x04cb
            long r5 = r5.volume_id
            r11 = -2147483648(0xfffffffvar_, double:NaN)
            int r14 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1))
            if (r14 != 0) goto L_0x04cb
            if (r3 == 0) goto L_0x04cb
            org.telegram.tgnet.TLRPC$FileLocation r5 = r3.location
            if (r5 == 0) goto L_0x04cb
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x04cb
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r5 != 0) goto L_0x04cb
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
            if (r6 != 0) goto L_0x04f4
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
            goto L_0x04f4
        L_0x04cb:
            if (r3 == 0) goto L_0x04dc
            if (r2 == 0) goto L_0x04dc
            boolean r1 = org.telegram.messenger.MessageObject.isStickerMessage(r19)
            if (r1 == 0) goto L_0x04dc
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            if (r1 == 0) goto L_0x04dc
            r3.location = r1
            goto L_0x04f4
        L_0x04dc:
            if (r2 == 0) goto L_0x04e8
            org.telegram.tgnet.TLRPC$FileLocation r1 = r2.location
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            if (r1 != 0) goto L_0x04e8
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoSizeEmpty
            if (r1 == 0) goto L_0x04f4
        L_0x04e8:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r2.thumbs
            r1.thumbs = r2
        L_0x04f4:
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
        L_0x0509:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x052b
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r3 == 0) goto L_0x0528
            byte[] r3 = r2.waveform
            goto L_0x052c
        L_0x0528:
            int r1 = r1 + 1
            goto L_0x0509
        L_0x052b:
            r3 = r10
        L_0x052c:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            r1.attributes = r2
            if (r3 == 0) goto L_0x0562
            r1 = 0
        L_0x053b:
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x0562
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r2.attributes
            java.lang.Object r2 = r2.get(r1)
            org.telegram.tgnet.TLRPC$DocumentAttribute r2 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r2
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            if (r4 == 0) goto L_0x055f
            r2.waveform = r3
            int r4 = r2.flags
            r5 = 4
            r4 = r4 | r5
            r2.flags = r4
        L_0x055f:
            int r1 = r1 + 1
            goto L_0x053b
        L_0x0562:
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
            if (r1 != 0) goto L_0x05d4
            boolean r1 = org.telegram.messenger.MessageObject.isOut(r19)
            if (r1 == 0) goto L_0x05d4
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r1)
            if (r1 == 0) goto L_0x05aa
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isDocumentHasAttachedStickers(r1)
            if (r1 == 0) goto L_0x0599
            org.telegram.messenger.MessagesController r1 = r17.getMessagesController()
            boolean r12 = r1.saveGifsWithStickers
            goto L_0x059a
        L_0x0599:
            r12 = 1
        L_0x059a:
            if (r12 == 0) goto L_0x05d4
            org.telegram.messenger.MediaDataController r1 = r17.getMediaDataController()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            int r3 = r7.date
            r1.addRecentGif(r2, r3)
            goto L_0x05d4
        L_0x05aa:
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = org.telegram.messenger.MessageObject.isStickerDocument(r1)
            if (r1 != 0) goto L_0x05c0
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r7.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            r3 = 1
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r3)
            if (r1 == 0) goto L_0x05d4
            goto L_0x05c1
        L_0x05c0:
            r3 = 1
        L_0x05c1:
            org.telegram.messenger.MediaDataController r1 = r17.getMediaDataController()
            r2 = 0
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r7.media
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            int r5 = r7.date
            r6 = 0
            r10 = 1
            r3 = r19
            r1.addRecentSticker(r2, r3, r4, r5, r6)
            goto L_0x05d5
        L_0x05d4:
            r10 = 1
        L_0x05d5:
            java.lang.String r1 = r9.attachPath
            if (r1 == 0) goto L_0x064b
            r2 = 4
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r2)
            java.lang.String r2 = r2.getAbsolutePath()
            boolean r1 = r1.startsWith(r2)
            if (r1 == 0) goto L_0x064b
            java.io.File r1 = new java.io.File
            java.lang.String r2 = r9.attachPath
            r1.<init>(r2)
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$Document r3 = r2.document
            int r2 = r2.ttl_seconds
            if (r2 == 0) goto L_0x05f9
            r2 = 1
            goto L_0x05fa
        L_0x05f9:
            r2 = 0
        L_0x05fa:
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r2)
            boolean r3 = r1.renameTo(r2)
            if (r3 != 0) goto L_0x061d
            boolean r1 = r1.exists()
            if (r1 == 0) goto L_0x060f
            java.lang.String r1 = r9.attachPath
            r7.attachPath = r1
            goto L_0x0611
        L_0x060f:
            r0.attachPathExists = r15
        L_0x0611:
            boolean r1 = r2.exists()
            r0.mediaExists = r1
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x06a9
        L_0x061d:
            boolean r1 = org.telegram.messenger.MessageObject.isVideoMessage(r19)
            if (r1 == 0) goto L_0x0627
            r0.attachPathExists = r10
            goto L_0x06a9
        L_0x0627:
            boolean r1 = r0.attachPathExists
            r0.mediaExists = r1
            r0.attachPathExists = r15
            java.lang.String r0 = ""
            r9.attachPath = r0
            if (r8 == 0) goto L_0x06a9
            java.lang.String r0 = "http"
            boolean r0 = r8.startsWith(r0)
            if (r0 == 0) goto L_0x06a9
            org.telegram.messenger.MessagesStorage r0 = r17.getMessagesStorage()
            java.lang.String r1 = r2.toString()
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r9.media
            org.telegram.tgnet.TLRPC$Document r2 = r2.document
            r0.addRecentLocalFile(r8, r1, r2)
            goto L_0x06a9
        L_0x064b:
            java.lang.String r0 = r9.attachPath
            r7.attachPath = r0
            java.lang.String r0 = r9.message
            r7.message = r0
            goto L_0x06a9
        L_0x0654:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r0 == 0) goto L_0x0661
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r9.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r0 == 0) goto L_0x0661
            r9.media = r2
            goto L_0x06a9
        L_0x0661:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r0 == 0) goto L_0x0668
            r9.media = r2
            goto L_0x06a9
        L_0x0668:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r0 == 0) goto L_0x067b
            org.telegram.tgnet.TLRPC$GeoPoint r0 = r2.geo
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r9.media
            org.telegram.tgnet.TLRPC$GeoPoint r1 = r1.geo
            double r2 = r1.lat
            r0.lat = r2
            double r1 = r1._long
            r0._long = r1
            goto L_0x06a9
        L_0x067b:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 != 0) goto L_0x068b
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r0 == 0) goto L_0x0684
            goto L_0x068b
        L_0x0684:
            boolean r0 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x06a9
            r9.media = r2
            goto L_0x06a9
        L_0x068b:
            r9.media = r2
            java.lang.String r0 = r7.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x069d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r7.entities
            r9.entities = r0
            java.lang.String r0 = r7.message
            r9.message = r0
        L_0x069d:
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r7.reply_markup
            if (r0 == 0) goto L_0x06a9
            r9.reply_markup = r0
            int r0 = r9.flags
            r0 = r0 | 64
            r9.flags = r0
        L_0x06a9:
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

    public void prepareImportHistory(long j, Uri uri, ArrayList<Uri> arrayList, MessagesStorage.IntCallback intCallback) {
        if (this.importingHistoryMap.get(j) != null) {
            intCallback.run(0);
            return;
        }
        int i = (int) j;
        if (i < 0) {
            int i2 = -i;
            TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(i2));
            if (chat != null && !chat.megagroup) {
                getMessagesController().convertToMegaGroup((Context) null, i2, (BaseFragment) null, new SendMessagesHelper$$ExternalSyntheticLambda76(this, uri, arrayList, intCallback));
                return;
            }
        }
        new Thread(new SendMessagesHelper$$ExternalSyntheticLambda31(this, arrayList, j, uri, intCallback)).start();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareImportHistory$64(Uri uri, ArrayList arrayList, MessagesStorage.IntCallback intCallback, int i) {
        if (i != 0) {
            prepareImportHistory((long) (-i), uri, arrayList, intCallback);
            return;
        }
        intCallback.run(0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareImportHistory$69(ArrayList arrayList, long j, Uri uri, MessagesStorage.IntCallback intCallback) {
        Uri uri2;
        long j2 = j;
        MessagesStorage.IntCallback intCallback2 = intCallback;
        ArrayList arrayList2 = arrayList != null ? arrayList : new ArrayList();
        ImportingHistory importingHistory = new ImportingHistory();
        importingHistory.mediaPaths = arrayList2;
        importingHistory.dialogId = j2;
        importingHistory.peer = getMessagesController().getInputPeer((int) j2);
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
                                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda17(intCallback2));
                                return;
                            } else {
                                importingHistory.historyPath = copyFileToCache;
                            }
                            importingHistory.uploadSet.add(copyFileToCache);
                            hashMap.put(copyFileToCache, importingHistory);
                        }
                    }
                    if (i == 0) {
                        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda18(intCallback2));
                        return;
                    }
                }
            } else if (i == 0) {
                AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda16(intCallback2));
                return;
            }
        }
        AndroidUtilities.runOnUIThread(new SendMessagesHelper$$ExternalSyntheticLambda35(this, hashMap, j, importingHistory, intCallback));
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareImportHistory$67(MessagesStorage.IntCallback intCallback) {
        Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("ImportFileTooLarge", NUM), 0).show();
        intCallback.run(0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareImportHistory$68(HashMap hashMap, long j, ImportingHistory importingHistory, MessagesStorage.IntCallback intCallback) {
        this.importingHistoryFiles.putAll(hashMap);
        this.importingHistoryMap.put(j, importingHistory);
        getFileLoader().uploadFile(importingHistory.historyPath, false, true, 0, 67108864, true);
        getNotificationCenter().postNotificationName(NotificationCenter.historyImportProgressChanged, Long.valueOf(j));
        intCallback.run((int) j);
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
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x02fb, code lost:
        r1 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0344, code lost:
        switch(r1) {
            case 0: goto L_0x036c;
            case 1: goto L_0x0367;
            case 2: goto L_0x0362;
            case 3: goto L_0x035d;
            case 4: goto L_0x0358;
            case 5: goto L_0x0355;
            default: goto L_0x0347;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0347, code lost:
        r1 = r19.getMimeTypeFromExtension(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x034d, code lost:
        if (r1 == null) goto L_0x0352;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x034f, code lost:
        r10.mime_type = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0352, code lost:
        r10.mime_type = "application/octet-stream";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0355, code lost:
        r10.mime_type = "image/webp";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x0358, code lost:
        r10.mime_type = "audio/opus";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x035d, code lost:
        r10.mime_type = "audio/flac";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x0362, code lost:
        r10.mime_type = "audio/ogg";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x0367, code lost:
        r10.mime_type = "audio/mpeg";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x036c, code lost:
        r10.mime_type = "audio/m4a";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0053, code lost:
        if (r3 == false) goto L_0x0057;
     */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0152 A[SYNTHETIC, Splitter:B:106:0x0152] */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0167 A[SYNTHETIC, Splitter:B:114:0x0167] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0171  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x01a0  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x01c6  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0205 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x02b6  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x043a  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x043f  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0445  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x044d  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0461  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0468  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x04c2  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x04c9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00b1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean prepareSendingDocumentInternal(org.telegram.messenger.AccountInstance r34, java.lang.String r35, java.lang.String r36, android.net.Uri r37, java.lang.String r38, long r39, org.telegram.messenger.MessageObject r41, org.telegram.messenger.MessageObject r42, java.lang.CharSequence r43, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r44, org.telegram.messenger.MessageObject r45, long[] r46, boolean r47, boolean r48, boolean r49, int r50, java.lang.Integer[] r51) {
        /*
            r0 = r35
            r1 = r36
            r2 = r37
            r3 = r38
            r4 = 0
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)
            if (r0 == 0) goto L_0x0015
            int r6 = r35.length()
            if (r6 != 0) goto L_0x0018
        L_0x0015:
            if (r2 != 0) goto L_0x0018
            return r4
        L_0x0018:
            if (r2 == 0) goto L_0x0021
            boolean r6 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r37)
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
            if (r3 == 0) goto L_0x052b
            long r8 = r2.length()
            r12 = 0
            int r3 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r3 != 0) goto L_0x006f
            goto L_0x052b
        L_0x006f:
            r10 = r39
            int r3 = (int) r10
            if (r3 != 0) goto L_0x0076
            r3 = 1
            goto L_0x0077
        L_0x0076:
            r3 = 0
        L_0x0077:
            java.lang.String r9 = r2.getName()
            r8 = -1
            java.lang.String r7 = ""
            if (r0 == 0) goto L_0x0083
        L_0x0080:
            r16 = r0
            goto L_0x0093
        L_0x0083:
            r0 = 46
            int r0 = r14.lastIndexOf(r0)
            if (r0 == r8) goto L_0x0091
            int r0 = r0 + r15
            java.lang.String r0 = r14.substring(r0)
            goto L_0x0080
        L_0x0091:
            r16 = r7
        L_0x0093:
            java.lang.String r11 = r16.toLowerCase()
            java.lang.String r10 = "mp3"
            boolean r0 = r11.equals(r10)
            java.lang.String r4 = "flac"
            java.lang.String r12 = "opus"
            java.lang.String r13 = "m4a"
            java.lang.String r15 = "ogg"
            r18 = r5
            if (r0 != 0) goto L_0x0171
            boolean r0 = r11.equals(r13)
            if (r0 == 0) goto L_0x00b1
            goto L_0x0171
        L_0x00b1:
            boolean r0 = r11.equals(r12)
            if (r0 != 0) goto L_0x00ce
            boolean r0 = r11.equals(r15)
            if (r0 != 0) goto L_0x00ce
            boolean r0 = r11.equals(r4)
            if (r0 == 0) goto L_0x00c4
            goto L_0x00ce
        L_0x00c4:
            r19 = r6
            r0 = 0
            r5 = 0
            r6 = 0
            r8 = 0
        L_0x00ca:
            r20 = 0
            goto L_0x019c
        L_0x00ce:
            android.media.MediaMetadataRetriever r8 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0144, all -> 0x0140 }
            r8.<init>()     // Catch:{ Exception -> 0x0144, all -> 0x0140 }
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x013b }
            r8.setDataSource(r0)     // Catch:{ Exception -> 0x013b }
            r0 = 9
            java.lang.String r0 = r8.extractMetadata(r0)     // Catch:{ Exception -> 0x013b }
            if (r0 == 0) goto L_0x0109
            r19 = r6
            long r5 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x0107 }
            float r0 = (float) r5     // Catch:{ Exception -> 0x0107 }
            r5 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r5
            double r5 = (double) r0     // Catch:{ Exception -> 0x0107 }
            double r5 = java.lang.Math.ceil(r5)     // Catch:{ Exception -> 0x0107 }
            int r5 = (int) r5
            r0 = 7
            java.lang.String r6 = r8.extractMetadata(r0)     // Catch:{ Exception -> 0x0102 }
            r20 = r5
            r5 = 2
            java.lang.String r0 = r8.extractMetadata(r5)     // Catch:{ Exception -> 0x0100 }
            r5 = r0
            goto L_0x010f
        L_0x0100:
            r0 = move-exception
            goto L_0x014b
        L_0x0102:
            r0 = move-exception
            r20 = r5
            r6 = 0
            goto L_0x014b
        L_0x0107:
            r0 = move-exception
            goto L_0x013e
        L_0x0109:
            r19 = r6
            r5 = 0
            r6 = 0
            r20 = 0
        L_0x010f:
            if (r45 != 0) goto L_0x012a
            boolean r0 = r11.equals(r15)     // Catch:{ Exception -> 0x0126 }
            if (r0 == 0) goto L_0x012a
            java.lang.String r0 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x0126 }
            int r0 = org.telegram.messenger.MediaController.isOpusFile(r0)     // Catch:{ Exception -> 0x0126 }
            r21 = r5
            r5 = 1
            if (r0 != r5) goto L_0x012c
            r5 = 1
            goto L_0x012d
        L_0x0126:
            r0 = move-exception
            r21 = r5
            goto L_0x014d
        L_0x012a:
            r21 = r5
        L_0x012c:
            r5 = 0
        L_0x012d:
            r8.release()     // Catch:{ Exception -> 0x0131 }
            goto L_0x0136
        L_0x0131:
            r0 = move-exception
            r8 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0136:
            r0 = r20
            r8 = r21
            goto L_0x00ca
        L_0x013b:
            r0 = move-exception
            r19 = r6
        L_0x013e:
            r6 = 0
            goto L_0x0149
        L_0x0140:
            r0 = move-exception
            r1 = r0
            r7 = 0
            goto L_0x0165
        L_0x0144:
            r0 = move-exception
            r19 = r6
            r6 = 0
            r8 = 0
        L_0x0149:
            r20 = 0
        L_0x014b:
            r21 = 0
        L_0x014d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0162 }
            if (r8 == 0) goto L_0x015b
            r8.release()     // Catch:{ Exception -> 0x0156 }
            goto L_0x015b
        L_0x0156:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
        L_0x015b:
            r0 = r20
            r8 = r21
            r5 = 0
            goto L_0x00ca
        L_0x0162:
            r0 = move-exception
            r1 = r0
            r7 = r8
        L_0x0165:
            if (r7 == 0) goto L_0x0170
            r7.release()     // Catch:{ Exception -> 0x016b }
            goto L_0x0170
        L_0x016b:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0170:
            throw r1
        L_0x0171:
            r19 = r6
            org.telegram.messenger.audioinfo.AudioInfo r0 = org.telegram.messenger.audioinfo.AudioInfo.getAudioInfo(r2)
            if (r0 == 0) goto L_0x0191
            long r5 = r0.getDuration()
            r20 = 0
            int r8 = (r5 > r20 ? 1 : (r5 == r20 ? 0 : -1))
            if (r8 == 0) goto L_0x0193
            java.lang.String r8 = r0.getArtist()
            java.lang.String r0 = r0.getTitle()
            r22 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r22
            int r6 = (int) r5
            goto L_0x0196
        L_0x0191:
            r20 = 0
        L_0x0193:
            r0 = 0
            r6 = 0
            r8 = 0
        L_0x0196:
            r5 = 0
            r32 = r6
            r6 = r0
            r0 = r32
        L_0x019c:
            r37 = r9
            if (r0 == 0) goto L_0x01c3
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r9 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r9.<init>()
            r9.duration = r0
            r9.title = r6
            r9.performer = r8
            if (r6 != 0) goto L_0x01af
            r9.title = r7
        L_0x01af:
            int r0 = r9.flags
            r6 = 1
            r0 = r0 | r6
            r9.flags = r0
            if (r8 != 0) goto L_0x01b9
            r9.performer = r7
        L_0x01b9:
            r8 = 2
            r0 = r0 | r8
            r9.flags = r0
            if (r5 == 0) goto L_0x01c1
            r9.voice = r6
        L_0x01c1:
            r5 = r9
            goto L_0x01c4
        L_0x01c3:
            r5 = 0
        L_0x01c4:
            if (r1 == 0) goto L_0x0202
            java.lang.String r0 = "attheme"
            boolean r0 = r1.endsWith(r0)
            if (r0 == 0) goto L_0x01d0
            r0 = 1
            goto L_0x0203
        L_0x01d0:
            if (r5 == 0) goto L_0x01eb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            java.lang.String r1 = "audio"
            r0.append(r1)
            long r8 = r2.length()
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            goto L_0x0201
        L_0x01eb:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            r0.append(r7)
            long r8 = r2.length()
            r0.append(r8)
            java.lang.String r0 = r0.toString()
        L_0x0201:
            r1 = r0
        L_0x0202:
            r0 = 0
        L_0x0203:
            if (r0 != 0) goto L_0x029c
            if (r3 != 0) goto L_0x029c
            org.telegram.messenger.MessagesStorage r0 = r34.getMessagesStorage()
            if (r3 != 0) goto L_0x020f
            r8 = 1
            goto L_0x0210
        L_0x020f:
            r8 = 4
        L_0x0210:
            java.lang.Object[] r0 = r0.getSentFile(r1, r8)
            if (r0 == 0) goto L_0x0228
            r8 = 0
            r9 = r0[r8]
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r9 == 0) goto L_0x0228
            r9 = r0[r8]
            r8 = r9
            org.telegram.tgnet.TLRPC$TL_document r8 = (org.telegram.tgnet.TLRPC$TL_document) r8
            r9 = 1
            r0 = r0[r9]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x022a
        L_0x0228:
            r0 = 0
            r8 = 0
        L_0x022a:
            if (r8 != 0) goto L_0x0270
            boolean r9 = r14.equals(r1)
            if (r9 != 0) goto L_0x0270
            if (r3 != 0) goto L_0x0270
            org.telegram.messenger.MessagesStorage r9 = r34.getMessagesStorage()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r14)
            r22 = r10
            r38 = r11
            long r10 = r2.length()
            r6.append(r10)
            java.lang.String r6 = r6.toString()
            if (r3 != 0) goto L_0x0253
            r10 = 1
            goto L_0x0254
        L_0x0253:
            r10 = 4
        L_0x0254:
            java.lang.Object[] r6 = r9.getSentFile(r6, r10)
            if (r6 == 0) goto L_0x0274
            r9 = 0
            r10 = r6[r9]
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r10 == 0) goto L_0x0274
            r0 = r6[r9]
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            r8 = 1
            r6 = r6[r8]
            java.lang.String r6 = (java.lang.String) r6
            r32 = r6
            r6 = r0
            r0 = r32
            goto L_0x0275
        L_0x0270:
            r22 = r10
            r38 = r11
        L_0x0274:
            r6 = r8
        L_0x0275:
            r11 = 0
            r23 = 0
            r25 = -1
            r8 = r3
            r10 = r37
            r9 = r6
            r26 = r10
            r27 = r22
            r10 = r14
            r35 = r38
            r37 = r6
            r6 = r12
            r38 = r15
            r32 = r20
            r20 = r1
            r1 = r13
            r21 = r14
            r14 = r32
            r12 = r23
            ensureMediaThumbExists(r8, r9, r10, r11, r12)
            r8 = r0
            r0 = r37
            goto L_0x02b2
        L_0x029c:
            r26 = r37
            r27 = r10
            r35 = r11
            r6 = r12
            r38 = r15
            r25 = -1
            r32 = r20
            r20 = r1
            r1 = r13
            r21 = r14
            r14 = r32
            r0 = 0
            r8 = 0
        L_0x02b2:
            java.lang.String r9 = "image/webp"
            if (r0 != 0) goto L_0x043a
            org.telegram.tgnet.TLRPC$TL_document r10 = new org.telegram.tgnet.TLRPC$TL_document
            r10.<init>()
            r10.id = r14
            org.telegram.tgnet.ConnectionsManager r0 = r34.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r10.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            r11 = r26
            r0.file_name = r11
            r11 = 0
            byte[] r12 = new byte[r11]
            r10.file_reference = r12
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r12 = r10.attributes
            r12.add(r0)
            long r12 = r2.length()
            int r13 = (int) r12
            r10.size = r13
            r10.dc_id = r11
            if (r5 == 0) goto L_0x02ea
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r11 = r10.attributes
            r11.add(r5)
        L_0x02ea:
            int r11 = r16.length()
            java.lang.String r12 = "application/octet-stream"
            if (r11 == 0) goto L_0x0371
            int r11 = r35.hashCode()
            switch(r11) {
                case 106458: goto L_0x033a;
                case 108272: goto L_0x032d;
                case 109967: goto L_0x0320;
                case 3145576: goto L_0x0315;
                case 3418175: goto L_0x030a;
                case 3645340: goto L_0x02fd;
                default: goto L_0x02f9;
            }
        L_0x02f9:
            r11 = r35
        L_0x02fb:
            r1 = -1
            goto L_0x0344
        L_0x02fd:
            java.lang.String r1 = "webp"
            r11 = r35
            boolean r1 = r11.equals(r1)
            if (r1 != 0) goto L_0x0308
            goto L_0x0342
        L_0x0308:
            r1 = 5
            goto L_0x0344
        L_0x030a:
            r11 = r35
            boolean r1 = r11.equals(r6)
            if (r1 != 0) goto L_0x0313
            goto L_0x0342
        L_0x0313:
            r1 = 4
            goto L_0x0344
        L_0x0315:
            r11 = r35
            boolean r1 = r11.equals(r4)
            if (r1 != 0) goto L_0x031e
            goto L_0x0342
        L_0x031e:
            r1 = 3
            goto L_0x0344
        L_0x0320:
            r11 = r35
            r1 = r38
            boolean r1 = r11.equals(r1)
            if (r1 != 0) goto L_0x032b
            goto L_0x0342
        L_0x032b:
            r1 = 2
            goto L_0x0344
        L_0x032d:
            r11 = r35
            r1 = r27
            boolean r1 = r11.equals(r1)
            if (r1 != 0) goto L_0x0338
            goto L_0x0342
        L_0x0338:
            r1 = 1
            goto L_0x0344
        L_0x033a:
            r11 = r35
            boolean r1 = r11.equals(r1)
            if (r1 != 0) goto L_0x0343
        L_0x0342:
            goto L_0x02fb
        L_0x0343:
            r1 = 0
        L_0x0344:
            switch(r1) {
                case 0: goto L_0x036c;
                case 1: goto L_0x0367;
                case 2: goto L_0x0362;
                case 3: goto L_0x035d;
                case 4: goto L_0x0358;
                case 5: goto L_0x0355;
                default: goto L_0x0347;
            }
        L_0x0347:
            r1 = r19
            java.lang.String r1 = r1.getMimeTypeFromExtension(r11)
            if (r1 == 0) goto L_0x0352
            r10.mime_type = r1
            goto L_0x0373
        L_0x0352:
            r10.mime_type = r12
            goto L_0x0373
        L_0x0355:
            r10.mime_type = r9
            goto L_0x0373
        L_0x0358:
            java.lang.String r1 = "audio/opus"
            r10.mime_type = r1
            goto L_0x0373
        L_0x035d:
            java.lang.String r1 = "audio/flac"
            r10.mime_type = r1
            goto L_0x0373
        L_0x0362:
            java.lang.String r1 = "audio/ogg"
            r10.mime_type = r1
            goto L_0x0373
        L_0x0367:
            java.lang.String r1 = "audio/mpeg"
            r10.mime_type = r1
            goto L_0x0373
        L_0x036c:
            java.lang.String r1 = "audio/m4a"
            r10.mime_type = r1
            goto L_0x0373
        L_0x0371:
            r10.mime_type = r12
        L_0x0373:
            if (r48 != 0) goto L_0x03c0
            java.lang.String r1 = r10.mime_type
            java.lang.String r4 = "image/gif"
            boolean r1 = r1.equals(r4)
            if (r1 == 0) goto L_0x03c0
            if (r45 == 0) goto L_0x0389
            long r11 = r45.getGroupIdForUse()
            int r1 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
            if (r1 != 0) goto L_0x03c0
        L_0x0389:
            java.lang.String r1 = r2.getAbsolutePath()     // Catch:{ Exception -> 0x03bc }
            r2 = 1119092736(0x42b40000, float:90.0)
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r1 = org.telegram.messenger.ImageLoader.loadBitmap(r1, r4, r2, r2, r6)     // Catch:{ Exception -> 0x03bc }
            if (r1 == 0) goto L_0x03c0
            java.lang.String r4 = "animation.gif"
            r0.file_name = r4     // Catch:{ Exception -> 0x03bc }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r10.attributes     // Catch:{ Exception -> 0x03bc }
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r4 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated     // Catch:{ Exception -> 0x03bc }
            r4.<init>()     // Catch:{ Exception -> 0x03bc }
            r0.add(r4)     // Catch:{ Exception -> 0x03bc }
            r0 = 55
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r2, r2, r0, r3)     // Catch:{ Exception -> 0x03bc }
            if (r0 == 0) goto L_0x03b8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r2 = r10.thumbs     // Catch:{ Exception -> 0x03bc }
            r2.add(r0)     // Catch:{ Exception -> 0x03bc }
            int r0 = r10.flags     // Catch:{ Exception -> 0x03bc }
            r2 = 1
            r0 = r0 | r2
            r10.flags = r0     // Catch:{ Exception -> 0x03bc }
        L_0x03b8:
            r1.recycle()     // Catch:{ Exception -> 0x03bc }
            goto L_0x03c0
        L_0x03bc:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03c0:
            java.lang.String r0 = r10.mime_type
            boolean r0 = r0.equals(r9)
            if (r0 == 0) goto L_0x0437
            if (r45 != 0) goto L_0x0437
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options
            r1.<init>()
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ Exception -> 0x03fd }
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x03fd }
            java.lang.String r2 = "r"
            r4 = r21
            r0.<init>(r4, r2)     // Catch:{ Exception -> 0x03fb }
            java.nio.channels.FileChannel r26 = r0.getChannel()     // Catch:{ Exception -> 0x03fb }
            java.nio.channels.FileChannel$MapMode r27 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ Exception -> 0x03fb }
            r28 = 0
            int r2 = r4.length()     // Catch:{ Exception -> 0x03fb }
            long r11 = (long) r2     // Catch:{ Exception -> 0x03fb }
            r30 = r11
            java.nio.MappedByteBuffer r2 = r26.map(r27, r28, r30)     // Catch:{ Exception -> 0x03fb }
            int r6 = r2.limit()     // Catch:{ Exception -> 0x03fb }
            r11 = 0
            r12 = 1
            org.telegram.messenger.Utilities.loadWebpImage(r11, r2, r6, r1, r12)     // Catch:{ Exception -> 0x03fb }
            r0.close()     // Catch:{ Exception -> 0x03fb }
            goto L_0x0403
        L_0x03fb:
            r0 = move-exception
            goto L_0x0400
        L_0x03fd:
            r0 = move-exception
            r4 = r21
        L_0x0400:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0403:
            int r0 = r1.outWidth
            if (r0 == 0) goto L_0x043d
            int r2 = r1.outHeight
            if (r2 == 0) goto L_0x043d
            r6 = 800(0x320, float:1.121E-42)
            if (r0 > r6) goto L_0x043d
            if (r2 > r6) goto L_0x043d
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r0.<init>()
            r0.alt = r7
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r2 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r2.<init>()
            r0.stickerset = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r10.attributes
            r2.add(r0)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r0.<init>()
            int r2 = r1.outWidth
            r0.w = r2
            int r1 = r1.outHeight
            r0.h = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r10.attributes
            r1.add(r0)
            goto L_0x043d
        L_0x0437:
            r4 = r21
            goto L_0x043d
        L_0x043a:
            r4 = r21
            r10 = r0
        L_0x043d:
            if (r43 == 0) goto L_0x0445
            java.lang.String r0 = r43.toString()
            r11 = r0
            goto L_0x0446
        L_0x0445:
            r11 = r7
        L_0x0446:
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            if (r20 == 0) goto L_0x0454
            java.lang.String r0 = "originalPath"
            r1 = r20
            r6.put(r0, r1)
        L_0x0454:
            java.lang.String r0 = "1"
            if (r48 == 0) goto L_0x045f
            if (r5 != 0) goto L_0x045f
            java.lang.String r1 = "forceDocument"
            r6.put(r1, r0)
        L_0x045f:
            if (r8 == 0) goto L_0x0466
            java.lang.String r1 = "parentObject"
            r6.put(r1, r8)
        L_0x0466:
            if (r51 == 0) goto L_0x04c2
            r1 = 0
            r2 = r51[r1]
            java.lang.String r12 = r10.mime_type
            if (r12 == 0) goto L_0x0484
            java.lang.String r12 = r12.toLowerCase()
            boolean r9 = r12.startsWith(r9)
            if (r9 == 0) goto L_0x0484
            java.lang.Integer r5 = java.lang.Integer.valueOf(r25)
            r51[r1] = r5
            r5 = r2
            r1 = 1
            r17 = 0
            goto L_0x04c7
        L_0x0484:
            java.lang.String r1 = r10.mime_type
            if (r1 == 0) goto L_0x04a2
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r9 = "image/"
            boolean r1 = r1.startsWith(r9)
            if (r1 != 0) goto L_0x04a8
            java.lang.String r1 = r10.mime_type
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r9 = "video/mp4"
            boolean r1 = r1.startsWith(r9)
            if (r1 != 0) goto L_0x04a8
        L_0x04a2:
            boolean r1 = org.telegram.messenger.MessageObject.canPreviewDocument(r10)
            if (r1 == 0) goto L_0x04b2
        L_0x04a8:
            r1 = 1
            java.lang.Integer r5 = java.lang.Integer.valueOf(r1)
            r17 = 0
            r51[r17] = r5
            goto L_0x04c0
        L_0x04b2:
            r17 = 0
            if (r5 == 0) goto L_0x04be
            r1 = 2
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r51[r17] = r1
            goto L_0x04c0
        L_0x04be:
            r51[r17] = r18
        L_0x04c0:
            r5 = r2
            goto L_0x04c6
        L_0x04c2:
            r17 = 0
            r5 = r18
        L_0x04c6:
            r1 = 0
        L_0x04c7:
            if (r3 != 0) goto L_0x0509
            if (r46 == 0) goto L_0x0509
            if (r51 == 0) goto L_0x04e5
            if (r5 == 0) goto L_0x04e5
            r2 = r51[r17]
            if (r5 == r2) goto L_0x04e5
            r2 = r46[r17]
            r5 = r34
            r14 = r50
            finishGroup(r5, r2, r14)
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random
            long r2 = r2.nextLong()
            r46[r17] = r2
            goto L_0x04e9
        L_0x04e5:
            r5 = r34
            r14 = r50
        L_0x04e9:
            if (r1 != 0) goto L_0x050d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            r2 = r46[r17]
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "groupId"
            r6.put(r2, r1)
            if (r47 == 0) goto L_0x050d
            java.lang.String r1 = "final"
            r6.put(r1, r0)
            goto L_0x050d
        L_0x0509:
            r5 = r34
            r14 = r50
        L_0x050d:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda12 r15 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda12
            r0 = r15
            r1 = r45
            r2 = r34
            r3 = r10
            r5 = r6
            r6 = r8
            r7 = r39
            r9 = r41
            r10 = r42
            r12 = r44
            r13 = r49
            r14 = r50
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13, r14)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r15)
            r1 = 1
            return r1
        L_0x052b:
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00bd  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00ea A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingAudioDocuments$75(java.util.ArrayList r24, long r25, org.telegram.messenger.AccountInstance r27, java.lang.String r28, org.telegram.messenger.MessageObject r29, org.telegram.messenger.MessageObject r30, org.telegram.messenger.MessageObject r31, boolean r32, int r33) {
        /*
            r14 = r25
            int r13 = r24.size()
            r16 = 0
            r0 = 0
            r2 = 0
            r12 = 0
        L_0x000c:
            if (r12 >= r13) goto L_0x0119
            r11 = r24
            java.lang.Object r3 = r11.get(r12)
            r4 = r3
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            java.lang.String r3 = r3.attachPath
            java.io.File r5 = new java.io.File
            r5.<init>(r3)
            int r6 = (int) r14
            r7 = 1
            if (r6 != 0) goto L_0x0026
            r6 = 1
            goto L_0x0027
        L_0x0026:
            r6 = 0
        L_0x0027:
            if (r6 != 0) goto L_0x0038
            if (r13 <= r7) goto L_0x0038
            int r8 = r2 % 10
            if (r8 != 0) goto L_0x0038
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r0 = r0.nextLong()
            r9 = r0
            r2 = 0
            goto L_0x0039
        L_0x0038:
            r9 = r0
        L_0x0039:
            if (r3 == 0) goto L_0x0053
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            java.lang.String r1 = "audio"
            r0.append(r1)
            long r7 = r5.length()
            r0.append(r7)
            java.lang.String r3 = r0.toString()
        L_0x0053:
            if (r6 != 0) goto L_0x0086
            org.telegram.messenger.MessagesStorage r5 = r27.getMessagesStorage()
            if (r6 != 0) goto L_0x005d
            r7 = 1
            goto L_0x005e
        L_0x005d:
            r7 = 4
        L_0x005e:
            java.lang.Object[] r5 = r5.getSentFile(r3, r7)
            if (r5 == 0) goto L_0x0086
            r7 = r5[r16]
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r7 == 0) goto L_0x0086
            r7 = r5[r16]
            org.telegram.tgnet.TLRPC$TL_document r7 = (org.telegram.tgnet.TLRPC$TL_document) r7
            r1 = 1
            r5 = r5[r1]
            java.lang.String r5 = (java.lang.String) r5
            r20 = 0
            r21 = 0
            r17 = r6
            r18 = r7
            r19 = r3
            ensureMediaThumbExists(r17, r18, r19, r20, r21)
            r23 = r7
            r7 = r5
            r5 = r23
            goto L_0x0088
        L_0x0086:
            r5 = 0
            r7 = 0
        L_0x0088:
            if (r5 != 0) goto L_0x0092
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            org.telegram.tgnet.TLRPC$TL_document r5 = (org.telegram.tgnet.TLRPC$TL_document) r5
        L_0x0092:
            if (r6 == 0) goto L_0x00a8
            r6 = 32
            long r0 = r14 >> r6
            int r1 = (int) r0
            org.telegram.messenger.MessagesController r0 = r27.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r1)
            if (r0 != 0) goto L_0x00a8
            return
        L_0x00a8:
            if (r12 != 0) goto L_0x00ad
            r18 = r28
            goto L_0x00af
        L_0x00ad:
            r18 = 0
        L_0x00af:
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            if (r3 == 0) goto L_0x00bb
            java.lang.String r0 = "originalPath"
            r6.put(r0, r3)
        L_0x00bb:
            if (r7 == 0) goto L_0x00c2
            java.lang.String r0 = "parentObject"
            r6.put(r0, r7)
        L_0x00c2:
            r0 = 1
            int r8 = r2 + 1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = ""
            r0.append(r1)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "groupId"
            r6.put(r1, r0)
            r0 = 10
            if (r8 == r0) goto L_0x00e3
            int r0 = r13 + -1
            if (r12 != r0) goto L_0x00ea
        L_0x00e3:
            java.lang.String r0 = "final"
            java.lang.String r1 = "1"
            r6.put(r0, r1)
        L_0x00ea:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda14 r17 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda14
            r0 = r17
            r1 = r29
            r2 = r27
            r3 = r5
            r5 = r6
            r6 = r7
            r19 = r8
            r7 = r25
            r20 = r9
            r9 = r30
            r10 = r31
            r11 = r18
            r18 = r12
            r12 = r32
            r22 = r13
            r13 = r33
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r17)
            int r12 = r18 + 1
            r2 = r19
            r0 = r20
            r13 = r22
            goto L_0x000c
        L_0x0119:
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
        boolean z3 = ((int) j) == 0;
        int i4 = 10;
        if (arrayList5 != null) {
            int size = arrayList.size();
            int i5 = 0;
            int i6 = 0;
            z2 = false;
            while (i6 < size) {
                String str3 = i6 == 0 ? str : null;
                if (!z3 && size > i3 && i5 % 10 == 0) {
                    if (jArr2[0] != 0) {
                        finishGroup(accountInstance2, jArr2[0], i2);
                    }
                    jArr2[0] = Utilities.random.nextLong();
                    i5 = 0;
                }
                int i7 = i5 + 1;
                long j2 = jArr2[0];
                int i8 = i7;
                int i9 = i6;
                int i10 = size;
                Integer[] numArr3 = numArr2;
                long[] jArr3 = jArr2;
                if (!prepareSendingDocumentInternal(accountInstance, (String) arrayList5.get(i6), (String) arrayList2.get(i6), (Uri) null, str2, j, messageObject, messageObject2, str3, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, jArr3, i7 == i4 || i6 == size + -1, inputContentInfoCompat == null, z, i, numArr3)) {
                    z2 = true;
                }
                i5 = (j2 != jArr3[0] || jArr3[0] == -1) ? 1 : i8;
                i6 = i9 + 1;
                long j3 = j;
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
                if (z3) {
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
                long j4 = jArr[0];
                int i17 = i16;
                int i18 = i12;
                int i19 = size2;
                if (!prepareSendingDocumentInternal(accountInstance, (String) null, (String) null, (Uri) arrayList4.get(i12), str2, j, messageObject, messageObject2, str4, (ArrayList<TLRPC$MessageEntity>) null, messageObject3, jArr, i16 == 10 || i12 == size2 + -1, inputContentInfoCompat == null, z, i, numArr)) {
                    z2 = true;
                }
                i11 = (j4 != jArr[0] || jArr[0] == -1) ? 1 : i17;
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
                if (((int) j) == 0) {
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
            } else {
                long j2 = j;
                if (tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaVenue) {
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
                } else if ((tLRPC$BotInlineMessage instanceof TLRPC$TL_botInlineMessageMediaInvoice) && !DialogObject.isSecretDialogId(j)) {
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
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v0, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r16v0, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v2, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v63, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v64, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v65, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v66, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v68, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v69, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v70, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v95, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v35, resolved type: org.telegram.tgnet.TLRPC$TL_document} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v97, resolved type: org.telegram.tgnet.TLRPC$TL_photo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v98, resolved type: org.telegram.tgnet.TLRPC$TL_game} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v38, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r0v99 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01be, code lost:
        if (r0.equals("voice") == false) goto L_0x01b7;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x043d  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0443  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x044f  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x049b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingBotContextResult$80(long r21, org.telegram.tgnet.TLRPC$BotInlineResult r23, org.telegram.messenger.AccountInstance r24, java.util.HashMap r25, org.telegram.messenger.MessageObject r26, org.telegram.messenger.MessageObject r27, boolean r28, int r29) {
        /*
            r11 = r23
            r12 = r25
            r7 = r21
            int r0 = (int) r7
            r2 = 1
            if (r0 != 0) goto L_0x000c
            r13 = 1
            goto L_0x000d
        L_0x000c:
            r13 = 0
        L_0x000d:
            java.lang.String r3 = r11.type
            java.lang.String r4 = "game"
            boolean r3 = r4.equals(r3)
            r5 = 0
            if (r3 == 0) goto L_0x004d
            if (r0 != 0) goto L_0x001b
            return
        L_0x001b:
            org.telegram.tgnet.TLRPC$TL_game r0 = new org.telegram.tgnet.TLRPC$TL_game
            r0.<init>()
            java.lang.String r3 = r11.title
            r0.title = r3
            java.lang.String r3 = r11.description
            r0.description = r3
            java.lang.String r3 = r11.id
            r0.short_name = r3
            org.telegram.tgnet.TLRPC$Photo r3 = r11.photo
            r0.photo = r3
            if (r3 != 0) goto L_0x0039
            org.telegram.tgnet.TLRPC$TL_photoEmpty r3 = new org.telegram.tgnet.TLRPC$TL_photoEmpty
            r3.<init>()
            r0.photo = r3
        L_0x0039:
            org.telegram.tgnet.TLRPC$Document r3 = r11.document
            boolean r6 = r3 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r6 == 0) goto L_0x0046
            r0.document = r3
            int r3 = r0.flags
            r3 = r3 | r2
            r0.flags = r3
        L_0x0046:
            r20 = r0
            r0 = r5
            r2 = r0
            r6 = r2
            goto L_0x0483
        L_0x004d:
            boolean r0 = r11 instanceof org.telegram.tgnet.TLRPC$TL_botInlineMediaResult
            if (r0 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$Document r0 = r11.document
            if (r0 == 0) goto L_0x0060
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r3 == 0) goto L_0x047d
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            r2 = r0
            r0 = r5
            r6 = r0
            goto L_0x0481
        L_0x0060:
            org.telegram.tgnet.TLRPC$Photo r0 = r11.photo
            if (r0 == 0) goto L_0x047d
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r3 == 0) goto L_0x047d
            org.telegram.tgnet.TLRPC$TL_photo r0 = (org.telegram.tgnet.TLRPC$TL_photo) r0
            r2 = r5
            goto L_0x0480
        L_0x006d:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            if (r0 == 0) goto L_0x047d
            java.lang.String r0 = r0.url
            java.lang.String r0 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r0, r5)
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            java.lang.String r6 = "."
            if (r3 == 0) goto L_0x0088
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            java.lang.String r0 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r0)
            goto L_0x0097
        L_0x0088:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x0097:
            java.io.File r3 = new java.io.File
            r9 = 4
            java.io.File r10 = org.telegram.messenger.FileLoader.getDirectory(r9)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.content
            java.lang.String r15 = r15.url
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)
            r14.append(r15)
            r14.append(r0)
            java.lang.String r0 = r14.toString()
            r3.<init>(r10, r0)
            boolean r0 = r3.exists()
            if (r0 == 0) goto L_0x00c3
            java.lang.String r0 = r3.getAbsolutePath()
            goto L_0x00c7
        L_0x00c3:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.url
        L_0x00c7:
            r10 = r0
            java.lang.String r0 = r11.type
            r0.hashCode()
            int r14 = r0.hashCode()
            java.lang.String r15 = "voice"
            r16 = 5
            java.lang.String r4 = "video"
            r18 = 3
            java.lang.String r9 = "audio"
            java.lang.String r2 = "gif"
            java.lang.String r1 = "sticker"
            r19 = 2
            java.lang.String r5 = "file"
            switch(r14) {
                case -1890252483: goto L_0x0120;
                case 102340: goto L_0x0117;
                case 3143036: goto L_0x010e;
                case 93166550: goto L_0x0105;
                case 106642994: goto L_0x00fa;
                case 112202875: goto L_0x00f1;
                case 112386354: goto L_0x00e8;
                default: goto L_0x00e6;
            }
        L_0x00e6:
            r0 = -1
            goto L_0x0128
        L_0x00e8:
            boolean r0 = r0.equals(r15)
            if (r0 != 0) goto L_0x00ef
            goto L_0x00e6
        L_0x00ef:
            r0 = 6
            goto L_0x0128
        L_0x00f1:
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x00f8
            goto L_0x00e6
        L_0x00f8:
            r0 = 5
            goto L_0x0128
        L_0x00fa:
            java.lang.String r14 = "photo"
            boolean r0 = r0.equals(r14)
            if (r0 != 0) goto L_0x0103
            goto L_0x00e6
        L_0x0103:
            r0 = 4
            goto L_0x0128
        L_0x0105:
            boolean r0 = r0.equals(r9)
            if (r0 != 0) goto L_0x010c
            goto L_0x00e6
        L_0x010c:
            r0 = 3
            goto L_0x0128
        L_0x010e:
            boolean r0 = r0.equals(r5)
            if (r0 != 0) goto L_0x0115
            goto L_0x00e6
        L_0x0115:
            r0 = 2
            goto L_0x0128
        L_0x0117:
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x011e
            goto L_0x00e6
        L_0x011e:
            r0 = 1
            goto L_0x0128
        L_0x0120:
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x0127
            goto L_0x00e6
        L_0x0127:
            r0 = 0
        L_0x0128:
            java.lang.String r14 = "x"
            switch(r0) {
                case 0: goto L_0x017f;
                case 1: goto L_0x017f;
                case 2: goto L_0x017f;
                case 3: goto L_0x017f;
                case 4: goto L_0x0134;
                case 5: goto L_0x017f;
                case 6: goto L_0x017f;
                default: goto L_0x012d;
            }
        L_0x012d:
            r6 = r10
            r0 = 0
        L_0x012f:
            r2 = 0
            r20 = 0
            goto L_0x0483
        L_0x0134:
            boolean r0 = r3.exists()
            if (r0 == 0) goto L_0x0144
            org.telegram.messenger.SendMessagesHelper r0 = r24.getSendMessagesHelper()
            r1 = 0
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r10, r1)
            goto L_0x0145
        L_0x0144:
            r0 = 0
        L_0x0145:
            if (r0 != 0) goto L_0x017d
            org.telegram.tgnet.TLRPC$TL_photo r0 = new org.telegram.tgnet.TLRPC$TL_photo
            r0.<init>()
            org.telegram.tgnet.ConnectionsManager r1 = r24.getConnectionsManager()
            int r1 = r1.getCurrentTime()
            r0.date = r1
            r1 = 0
            byte[] r2 = new byte[r1]
            r0.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize r2 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r2.<init>()
            int[] r3 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r4 = r3[r1]
            r2.w = r4
            r1 = 1
            r3 = r3[r1]
            r2.h = r3
            r2.size = r1
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r1.<init>()
            r2.location = r1
            r2.type = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r0.sizes
            r1.add(r2)
        L_0x017d:
            r6 = r10
            goto L_0x012f
        L_0x017f:
            org.telegram.tgnet.TLRPC$TL_document r3 = new org.telegram.tgnet.TLRPC$TL_document
            r3.<init>()
            r7 = 0
            r3.id = r7
            r7 = 0
            r3.size = r7
            r3.dc_id = r7
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r3.mime_type = r0
            byte[] r0 = new byte[r7]
            r3.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r24.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r3.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r7.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r3.attributes
            r0.add(r7)
            java.lang.String r0 = r11.type
            r0.hashCode()
            int r8 = r0.hashCode()
            switch(r8) {
                case -1890252483: goto L_0x01e9;
                case 102340: goto L_0x01df;
                case 3143036: goto L_0x01d5;
                case 93166550: goto L_0x01cb;
                case 112202875: goto L_0x01c1;
                case 112386354: goto L_0x01ba;
                default: goto L_0x01b7;
            }
        L_0x01b7:
            r16 = -1
            goto L_0x01f2
        L_0x01ba:
            boolean r0 = r0.equals(r15)
            if (r0 != 0) goto L_0x01f2
            goto L_0x01b7
        L_0x01c1:
            boolean r0 = r0.equals(r4)
            if (r0 != 0) goto L_0x01c8
            goto L_0x01b7
        L_0x01c8:
            r16 = 4
            goto L_0x01f2
        L_0x01cb:
            boolean r0 = r0.equals(r9)
            if (r0 != 0) goto L_0x01d2
            goto L_0x01b7
        L_0x01d2:
            r16 = 3
            goto L_0x01f2
        L_0x01d5:
            boolean r0 = r0.equals(r5)
            if (r0 != 0) goto L_0x01dc
            goto L_0x01b7
        L_0x01dc:
            r16 = 2
            goto L_0x01f2
        L_0x01df:
            boolean r0 = r0.equals(r2)
            if (r0 != 0) goto L_0x01e6
            goto L_0x01b7
        L_0x01e6:
            r16 = 1
            goto L_0x01f2
        L_0x01e9:
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x01f0
            goto L_0x01b7
        L_0x01f0:
            r16 = 0
        L_0x01f2:
            r0 = 55
            r1 = 1119092736(0x42b40000, float:90.0)
            switch(r16) {
                case 0: goto L_0x03ad;
                case 1: goto L_0x02ed;
                case 2: goto L_0x02bd;
                case 3: goto L_0x0293;
                case 4: goto L_0x0214;
                case 5: goto L_0x01fc;
                default: goto L_0x01f9;
            }
        L_0x01f9:
            r4 = 0
            goto L_0x0439
        L_0x01fc:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r23)
            r0.duration = r1
            r1 = 1
            r0.voice = r1
            java.lang.String r1 = "audio.ogg"
            r7.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r3.attributes
            r1.add(r0)
            goto L_0x01f9
        L_0x0214:
            java.lang.String r2 = "video.mp4"
            r7.file_name = r2
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r2.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r8 = 0
            r9 = r4[r8]
            r2.w = r9
            r8 = 1
            r4 = r4[r8]
            r2.h = r4
            int r4 = org.telegram.messenger.MessageObject.getInlineResultDuration(r23)
            r2.duration = r4
            r2.supports_streaming = r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r2)
            org.telegram.tgnet.TLRPC$WebDocument r2 = r11.thumb     // Catch:{ all -> 0x028d }
            if (r2 == 0) goto L_0x01f9
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x028d }
            r4 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ all -> 0x028d }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x028d }
            r8.<init>()     // Catch:{ all -> 0x028d }
            org.telegram.tgnet.TLRPC$WebDocument r9 = r11.thumb     // Catch:{ all -> 0x028d }
            java.lang.String r9 = r9.url     // Catch:{ all -> 0x028d }
            java.lang.String r9 = org.telegram.messenger.Utilities.MD5(r9)     // Catch:{ all -> 0x028d }
            r8.append(r9)     // Catch:{ all -> 0x028d }
            r8.append(r6)     // Catch:{ all -> 0x028d }
            org.telegram.tgnet.TLRPC$WebDocument r6 = r11.thumb     // Catch:{ all -> 0x028d }
            java.lang.String r6 = r6.url     // Catch:{ all -> 0x028d }
            java.lang.String r9 = "jpg"
            java.lang.String r6 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r9)     // Catch:{ all -> 0x028d }
            r8.append(r6)     // Catch:{ all -> 0x028d }
            java.lang.String r6 = r8.toString()     // Catch:{ all -> 0x028d }
            r2.<init>(r4, r6)     // Catch:{ all -> 0x028d }
            java.lang.String r2 = r2.getAbsolutePath()     // Catch:{ all -> 0x028d }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r2 = org.telegram.messenger.ImageLoader.loadBitmap(r2, r4, r1, r1, r6)     // Catch:{ all -> 0x028d }
            if (r2 == 0) goto L_0x01f9
            r4 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r1, r1, r0, r4)     // Catch:{ all -> 0x028d }
            if (r0 == 0) goto L_0x0288
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs     // Catch:{ all -> 0x028d }
            r1.add(r0)     // Catch:{ all -> 0x028d }
            int r0 = r3.flags     // Catch:{ all -> 0x028d }
            r1 = 1
            r0 = r0 | r1
            r3.flags = r0     // Catch:{ all -> 0x028d }
        L_0x0288:
            r2.recycle()     // Catch:{ all -> 0x028d }
            goto L_0x01f9
        L_0x028d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01f9
        L_0x0293:
            org.telegram.tgnet.TLRPC$TL_documentAttributeAudio r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r0.<init>()
            int r1 = org.telegram.messenger.MessageObject.getInlineResultDuration(r23)
            r0.duration = r1
            java.lang.String r1 = r11.title
            r0.title = r1
            int r1 = r0.flags
            r2 = 1
            r1 = r1 | r2
            r0.flags = r1
            java.lang.String r2 = r11.description
            if (r2 == 0) goto L_0x02b2
            r0.performer = r2
            r1 = r1 | 2
            r0.flags = r1
        L_0x02b2:
            java.lang.String r1 = "audio.mp3"
            r7.file_name = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r3.attributes
            r1.add(r0)
            goto L_0x01f9
        L_0x02bd:
            org.telegram.tgnet.TLRPC$WebDocument r0 = r11.content
            java.lang.String r0 = r0.mime_type
            r1 = 47
            int r0 = r0.lastIndexOf(r1)
            r1 = -1
            if (r0 == r1) goto L_0x02e9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "file."
            r1.append(r2)
            org.telegram.tgnet.TLRPC$WebDocument r2 = r11.content
            java.lang.String r2 = r2.mime_type
            r4 = 1
            int r0 = r0 + r4
            java.lang.String r0 = r2.substring(r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r7.file_name = r0
            goto L_0x01f9
        L_0x02e9:
            r7.file_name = r5
            goto L_0x01f9
        L_0x02ed:
            java.lang.String r1 = "animation.gif"
            r7.file_name = r1
            java.lang.String r1 = "mp4"
            boolean r2 = r10.endsWith(r1)
            java.lang.String r4 = "video/mp4"
            if (r2 == 0) goto L_0x0308
            r3.mime_type = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r3.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r8.<init>()
            r2.add(r8)
            goto L_0x030c
        L_0x0308:
            java.lang.String r2 = "image/gif"
            r3.mime_type = r2
        L_0x030c:
            r2 = 90
            if (r13 == 0) goto L_0x0313
            r8 = 90
            goto L_0x0315
        L_0x0313:
            r8 = 320(0x140, float:4.48E-43)
        L_0x0315:
            boolean r1 = r10.endsWith(r1)     // Catch:{ all -> 0x03a7 }
            if (r1 == 0) goto L_0x0382
            r1 = 1
            android.graphics.Bitmap r9 = createVideoThumbnail(r10, r1)     // Catch:{ all -> 0x03a7 }
            if (r9 != 0) goto L_0x0389
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.thumb     // Catch:{ all -> 0x03a7 }
            boolean r15 = r1 instanceof org.telegram.tgnet.TLRPC$TL_webDocument     // Catch:{ all -> 0x03a7 }
            if (r15 == 0) goto L_0x0389
            java.lang.String r1 = r1.mime_type     // Catch:{ all -> 0x03a7 }
            boolean r1 = r4.equals(r1)     // Catch:{ all -> 0x03a7 }
            if (r1 == 0) goto L_0x0389
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.thumb     // Catch:{ all -> 0x03a7 }
            java.lang.String r1 = r1.url     // Catch:{ all -> 0x03a7 }
            r4 = 0
            java.lang.String r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r4)     // Catch:{ all -> 0x03a7 }
            boolean r4 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x03a7 }
            if (r4 == 0) goto L_0x0348
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.thumb     // Catch:{ all -> 0x03a7 }
            java.lang.String r1 = r1.mime_type     // Catch:{ all -> 0x03a7 }
            java.lang.String r1 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r1)     // Catch:{ all -> 0x03a7 }
            goto L_0x0357
        L_0x0348:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a7 }
            r4.<init>()     // Catch:{ all -> 0x03a7 }
            r4.append(r6)     // Catch:{ all -> 0x03a7 }
            r4.append(r1)     // Catch:{ all -> 0x03a7 }
            java.lang.String r1 = r4.toString()     // Catch:{ all -> 0x03a7 }
        L_0x0357:
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x03a7 }
            r6 = 4
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r6)     // Catch:{ all -> 0x03a7 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x03a7 }
            r9.<init>()     // Catch:{ all -> 0x03a7 }
            org.telegram.tgnet.TLRPC$WebDocument r15 = r11.thumb     // Catch:{ all -> 0x03a7 }
            java.lang.String r15 = r15.url     // Catch:{ all -> 0x03a7 }
            java.lang.String r15 = org.telegram.messenger.Utilities.MD5(r15)     // Catch:{ all -> 0x03a7 }
            r9.append(r15)     // Catch:{ all -> 0x03a7 }
            r9.append(r1)     // Catch:{ all -> 0x03a7 }
            java.lang.String r1 = r9.toString()     // Catch:{ all -> 0x03a7 }
            r4.<init>(r6, r1)     // Catch:{ all -> 0x03a7 }
            java.lang.String r1 = r4.getAbsolutePath()     // Catch:{ all -> 0x03a7 }
            r4 = 1
            android.graphics.Bitmap r9 = createVideoThumbnail(r1, r4)     // Catch:{ all -> 0x03a7 }
            goto L_0x0389
        L_0x0382:
            float r1 = (float) r8     // Catch:{ all -> 0x03a7 }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r9 = org.telegram.messenger.ImageLoader.loadBitmap(r10, r4, r1, r1, r6)     // Catch:{ all -> 0x03a7 }
        L_0x0389:
            if (r9 == 0) goto L_0x01f9
            float r1 = (float) r8     // Catch:{ all -> 0x03a7 }
            if (r8 <= r2) goto L_0x0390
            r0 = 80
        L_0x0390:
            r2 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r9, r1, r1, r0, r2)     // Catch:{ all -> 0x03a7 }
            if (r0 == 0) goto L_0x03a2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs     // Catch:{ all -> 0x03a7 }
            r1.add(r0)     // Catch:{ all -> 0x03a7 }
            int r0 = r3.flags     // Catch:{ all -> 0x03a7 }
            r1 = 1
            r0 = r0 | r1
            r3.flags = r0     // Catch:{ all -> 0x03a7 }
        L_0x03a2:
            r9.recycle()     // Catch:{ all -> 0x03a7 }
            goto L_0x01f9
        L_0x03a7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x01f9
        L_0x03ad:
            org.telegram.tgnet.TLRPC$TL_documentAttributeSticker r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeSticker
            r2.<init>()
            java.lang.String r4 = ""
            r2.alt = r4
            org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty r4 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            r4.<init>()
            r2.stickerset = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r2)
            org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize
            r2.<init>()
            int[] r4 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r8 = 0
            r9 = r4[r8]
            r2.w = r9
            r8 = 1
            r4 = r4[r8]
            r2.h = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r2)
            java.lang.String r2 = "sticker.webp"
            r7.file_name = r2
            org.telegram.tgnet.TLRPC$WebDocument r2 = r11.thumb     // Catch:{ all -> 0x0434 }
            if (r2 == 0) goto L_0x01f9
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x0434 }
            r4 = 4
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch:{ all -> 0x0434 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0434 }
            r8.<init>()     // Catch:{ all -> 0x0434 }
            org.telegram.tgnet.TLRPC$WebDocument r9 = r11.thumb     // Catch:{ all -> 0x0434 }
            java.lang.String r9 = r9.url     // Catch:{ all -> 0x0434 }
            java.lang.String r9 = org.telegram.messenger.Utilities.MD5(r9)     // Catch:{ all -> 0x0434 }
            r8.append(r9)     // Catch:{ all -> 0x0434 }
            r8.append(r6)     // Catch:{ all -> 0x0434 }
            org.telegram.tgnet.TLRPC$WebDocument r6 = r11.thumb     // Catch:{ all -> 0x0434 }
            java.lang.String r6 = r6.url     // Catch:{ all -> 0x0434 }
            java.lang.String r9 = "webp"
            java.lang.String r6 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r6, r9)     // Catch:{ all -> 0x0434 }
            r8.append(r6)     // Catch:{ all -> 0x0434 }
            java.lang.String r6 = r8.toString()     // Catch:{ all -> 0x0434 }
            r2.<init>(r4, r6)     // Catch:{ all -> 0x0434 }
            java.lang.String r2 = r2.getAbsolutePath()     // Catch:{ all -> 0x0434 }
            r4 = 0
            r6 = 1
            android.graphics.Bitmap r2 = org.telegram.messenger.ImageLoader.loadBitmap(r2, r4, r1, r1, r6)     // Catch:{ all -> 0x0432 }
            if (r2 == 0) goto L_0x0439
            r6 = 0
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r1, r1, r0, r6)     // Catch:{ all -> 0x0432 }
            if (r0 == 0) goto L_0x042e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs     // Catch:{ all -> 0x0432 }
            r1.add(r0)     // Catch:{ all -> 0x0432 }
            int r0 = r3.flags     // Catch:{ all -> 0x0432 }
            r1 = 1
            r0 = r0 | r1
            r3.flags = r0     // Catch:{ all -> 0x0432 }
        L_0x042e:
            r2.recycle()     // Catch:{ all -> 0x0432 }
            goto L_0x0439
        L_0x0432:
            r0 = move-exception
            goto L_0x0436
        L_0x0434:
            r0 = move-exception
            r4 = 0
        L_0x0436:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0439:
            java.lang.String r0 = r7.file_name
            if (r0 != 0) goto L_0x043f
            r7.file_name = r5
        L_0x043f:
            java.lang.String r0 = r3.mime_type
            if (r0 != 0) goto L_0x0447
            java.lang.String r0 = "application/octet-stream"
            r3.mime_type = r0
        L_0x0447:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r3.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0477
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            int[] r1 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r23)
            r2 = 0
            r5 = r1[r2]
            r0.w = r5
            r5 = 1
            r1 = r1[r5]
            r0.h = r1
            r0.size = r2
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r1 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r1.<init>()
            r0.location = r1
            r0.type = r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r1 = r3.thumbs
            r1.add(r0)
            int r0 = r3.flags
            r0 = r0 | r5
            r3.flags = r0
        L_0x0477:
            r2 = r3
            r0 = r4
            r20 = r0
            r6 = r10
            goto L_0x0483
        L_0x047d:
            r4 = r5
            r0 = r4
            r2 = r0
        L_0x0480:
            r6 = r2
        L_0x0481:
            r20 = r6
        L_0x0483:
            if (r12 == 0) goto L_0x0490
            org.telegram.tgnet.TLRPC$WebDocument r1 = r11.content
            if (r1 == 0) goto L_0x0490
            java.lang.String r1 = r1.url
            java.lang.String r3 = "originalPath"
            r12.put(r3, r1)
        L_0x0490:
            r1 = 1
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r1]
            java.lang.String[] r4 = new java.lang.String[r1]
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r2)
            if (r5 == 0) goto L_0x04c5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r2.thumbs
            r7 = 320(0x140, float:4.48E-43)
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r7)
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r2)
            boolean r8 = r7.exists()
            if (r8 != 0) goto L_0x04b1
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r1)
        L_0x04b1:
            java.lang.String r15 = r7.getAbsolutePath()
            r16 = 0
            r17 = 0
            r14 = r2
            ensureMediaThumbExists(r13, r14, r15, r16, r17)
            r1 = 1
            java.lang.String r1 = getKeyForPhotoSize(r5, r3, r1, r1)
            r5 = 0
            r4[r5] = r1
        L_0x04c5:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda73 r17 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda73
            r1 = r17
            r5 = r24
            r7 = r21
            r9 = r26
            r10 = r27
            r11 = r23
            r12 = r25
            r13 = r28
            r14 = r29
            r15 = r0
            r16 = r20
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
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
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
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_game, j, tLRPC$BotInlineResult2.send_message.reply_markup, hashMap, z, i);
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
    private static boolean shouldSendWebPAsSticker(java.lang.String r10, android.net.Uri r11) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v1, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v0, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v1, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v2, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v3, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v4, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v5, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v79, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v6, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v7, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r22v8, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: org.telegram.messenger.SendMessagesHelper$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: org.telegram.messenger.SendMessagesHelper$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v29, resolved type: org.telegram.messenger.SendMessagesHelper$1} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v30, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v161, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v162, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v163, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: android.net.Uri} */
    /* JADX WARNING: type inference failed for: r2v67 */
    /* JADX WARNING: type inference failed for: r3v27 */
    /* JADX WARNING: type inference failed for: r0v78, types: [java.lang.String] */
    /* JADX WARNING: type inference failed for: r2v169 */
    /* JADX WARNING: type inference failed for: r4v115 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x060c, code lost:
        if (r4 != null) goto L_0x05ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x064d, code lost:
        if (r5.endsWith(r12) != false) goto L_0x066e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0060, code lost:
        if (r9 != false) goto L_0x0064;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:233:0x05ef */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x02e1 A[Catch:{ Exception -> 0x02d2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x02ec A[Catch:{ Exception -> 0x02d2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x02f9 A[Catch:{ Exception -> 0x0318 }] */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0326  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0351  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0363  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x036d  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0439  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x0607 A[SYNTHETIC, Splitter:B:246:0x0607] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x069f  */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x06f4  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x078c  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x08b2  */
    /* JADX WARNING: Removed duplicated region for block: B:400:0x08e0  */
    /* JADX WARNING: Removed duplicated region for block: B:467:0x0b03  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0bfc  */
    /* JADX WARNING: Removed duplicated region for block: B:529:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:535:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:544:0x0c6d  */
    /* JADX WARNING: Removed duplicated region for block: B:546:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0CLASSNAME A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:565:0x0cec A[LOOP:4: B:563:0x0ce4->B:565:0x0cec, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:598:0x0e38  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x0e3b  */
    /* JADX WARNING: Removed duplicated region for block: B:615:0x0CLASSNAME A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x014a  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0163  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingMedia$89(java.util.ArrayList r67, long r68, boolean r70, boolean r71, org.telegram.messenger.AccountInstance r72, org.telegram.messenger.MessageObject r73, org.telegram.messenger.MessageObject r74, org.telegram.messenger.MessageObject r75, boolean r76, int r77, androidx.core.view.inputmethod.InputContentInfoCompat r78) {
        /*
            r1 = r67
            r15 = r72
            long r19 = java.lang.System.currentTimeMillis()
            int r14 = r67.size()
            r12 = r68
            int r0 = (int) r12
            if (r0 != 0) goto L_0x0013
            r10 = 1
            goto L_0x0014
        L_0x0013:
            r10 = 0
        L_0x0014:
            java.lang.String r8 = ".webp"
            java.lang.String r6 = ".gif"
            r21 = 3
            java.lang.String r7 = "_"
            r5 = 0
            if (r70 != 0) goto L_0x0193
            if (r71 == 0) goto L_0x0193
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r4 = 0
        L_0x0027:
            if (r4 >= r14) goto L_0x018a
            java.lang.Object r2 = r1.get(r4)
            r3 = r2
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r3 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r3
            org.telegram.messenger.MediaController$SearchImage r2 = r3.searchImage
            if (r2 != 0) goto L_0x0176
            boolean r2 = r3.isVideo
            if (r2 != 0) goto L_0x0176
            org.telegram.messenger.VideoEditedInfo r2 = r3.videoEditedInfo
            if (r2 != 0) goto L_0x0176
            java.lang.String r2 = r3.path
            if (r2 != 0) goto L_0x004f
            android.net.Uri r11 = r3.uri
            if (r11 == 0) goto L_0x004f
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.getPath(r11)
            android.net.Uri r11 = r3.uri
            java.lang.String r11 = r11.toString()
            goto L_0x0050
        L_0x004f:
            r11 = r2
        L_0x0050:
            if (r2 == 0) goto L_0x0074
            int r9 = r3.ttl
            if (r9 > 0) goto L_0x0074
            boolean r9 = r2.endsWith(r6)
            if (r9 != 0) goto L_0x0063
            boolean r9 = r2.endsWith(r8)
            if (r9 == 0) goto L_0x0075
            goto L_0x0064
        L_0x0063:
            r9 = 0
        L_0x0064:
            if (r9 == 0) goto L_0x0176
            boolean r9 = shouldSendWebPAsSticker(r2, r5)
            if (r9 == 0) goto L_0x006e
            goto L_0x0176
        L_0x006e:
            r9 = 1
            r3.forceImage = r9
            r22 = r4
            goto L_0x00b0
        L_0x0074:
            r9 = 0
        L_0x0075:
            java.lang.String r5 = r3.path
            r22 = r4
            android.net.Uri r4 = r3.uri
            boolean r4 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r5, r4)
            if (r4 == 0) goto L_0x008a
        L_0x0081:
            r27 = r6
            r28 = r7
            r26 = r8
            r8 = 0
            goto L_0x017f
        L_0x008a:
            if (r2 != 0) goto L_0x00af
            android.net.Uri r4 = r3.uri
            if (r4 == 0) goto L_0x00af
            boolean r4 = org.telegram.messenger.MediaController.isGif(r4)
            if (r4 != 0) goto L_0x009e
            android.net.Uri r4 = r3.uri
            boolean r9 = org.telegram.messenger.MediaController.isWebp(r4)
            if (r9 == 0) goto L_0x00af
        L_0x009e:
            if (r9 == 0) goto L_0x0081
            android.net.Uri r4 = r3.uri
            r5 = 0
            boolean r4 = shouldSendWebPAsSticker(r5, r4)
            if (r4 == 0) goto L_0x00ab
            goto L_0x0178
        L_0x00ab:
            r4 = 1
            r3.forceImage = r4
            goto L_0x00b0
        L_0x00af:
            r5 = 0
        L_0x00b0:
            if (r2 == 0) goto L_0x00d6
            java.io.File r4 = new java.io.File
            r4.<init>(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            r9 = r6
            long r5 = r4.length()
            r2.append(r5)
            r2.append(r7)
            long r4 = r4.lastModified()
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            goto L_0x00d8
        L_0x00d6:
            r9 = r6
            r2 = 0
        L_0x00d8:
            if (r10 != 0) goto L_0x014a
            int r4 = r3.ttl
            if (r4 != 0) goto L_0x014a
            org.telegram.messenger.MessagesStorage r4 = r72.getMessagesStorage()
            if (r10 != 0) goto L_0x00e6
            r5 = 0
            goto L_0x00e7
        L_0x00e6:
            r5 = 3
        L_0x00e7:
            java.lang.Object[] r2 = r4.getSentFile(r2, r5)
            if (r2 == 0) goto L_0x00fe
            r4 = 0
            r5 = r2[r4]
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r5 == 0) goto L_0x00fe
            r5 = r2[r4]
            org.telegram.tgnet.TLRPC$TL_photo r5 = (org.telegram.tgnet.TLRPC$TL_photo) r5
            r4 = 1
            r2 = r2[r4]
            java.lang.String r2 = (java.lang.String) r2
            goto L_0x0100
        L_0x00fe:
            r2 = 0
            r5 = 0
        L_0x0100:
            if (r5 != 0) goto L_0x012d
            android.net.Uri r4 = r3.uri
            if (r4 == 0) goto L_0x012d
            org.telegram.messenger.MessagesStorage r4 = r72.getMessagesStorage()
            android.net.Uri r6 = r3.uri
            java.lang.String r6 = org.telegram.messenger.AndroidUtilities.getPath(r6)
            if (r10 != 0) goto L_0x0114
            r11 = 0
            goto L_0x0115
        L_0x0114:
            r11 = 3
        L_0x0115:
            java.lang.Object[] r4 = r4.getSentFile(r6, r11)
            if (r4 == 0) goto L_0x012d
            r6 = 0
            r11 = r4[r6]
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r11 == 0) goto L_0x012d
            r2 = r4[r6]
            r5 = r2
            org.telegram.tgnet.TLRPC$TL_photo r5 = (org.telegram.tgnet.TLRPC$TL_photo) r5
            r2 = 1
            r4 = r4[r2]
            r2 = r4
            java.lang.String r2 = (java.lang.String) r2
        L_0x012d:
            r11 = r2
            r23 = r5
            java.lang.String r4 = r3.path
            android.net.Uri r5 = r3.uri
            r24 = 0
            r2 = r10
            r6 = r3
            r3 = r23
            r26 = r8
            r8 = 0
            r28 = r7
            r27 = r9
            r9 = r6
            r6 = r24
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r5 = r23
            goto L_0x0154
        L_0x014a:
            r28 = r7
            r26 = r8
            r27 = r9
            r8 = 0
            r9 = r3
            r5 = r8
            r11 = r5
        L_0x0154:
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = new org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker
            r2.<init>()
            r0.put(r9, r2)
            if (r5 == 0) goto L_0x0163
            r2.parentObject = r11
            r2.photo = r5
            goto L_0x017f
        L_0x0163:
            java.util.concurrent.CountDownLatch r3 = new java.util.concurrent.CountDownLatch
            r4 = 1
            r3.<init>(r4)
            r2.sync = r3
            java.util.concurrent.ThreadPoolExecutor r3 = mediaSendThreadPool
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda20 r4 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda20
            r4.<init>(r2, r15, r9, r10)
            r3.execute(r4)
            goto L_0x017f
        L_0x0176:
            r22 = r4
        L_0x0178:
            r27 = r6
            r28 = r7
            r26 = r8
            r8 = r5
        L_0x017f:
            int r4 = r22 + 1
            r5 = r8
            r8 = r26
            r6 = r27
            r7 = r28
            goto L_0x0027
        L_0x018a:
            r27 = r6
            r28 = r7
            r26 = r8
            r8 = r5
            r11 = r0
            goto L_0x019b
        L_0x0193:
            r27 = r6
            r28 = r7
            r26 = r8
            r8 = r5
            r11 = r8
        L_0x019b:
            r2 = r8
            r3 = r2
            r4 = r3
            r5 = r4
            r22 = r5
            r29 = r22
            r0 = 0
            r9 = 0
            r23 = 0
            r30 = 0
        L_0x01a9:
            if (r9 >= r14) goto L_0x0db7
            java.lang.Object r25 = r1.get(r9)
            r8 = r25
            org.telegram.messenger.SendMessagesHelper$SendingMediaInfo r8 = (org.telegram.messenger.SendMessagesHelper.SendingMediaInfo) r8
            if (r71 == 0) goto L_0x01c7
            r6 = 1
            if (r14 <= r6) goto L_0x01c7
            int r6 = r0 % 10
            if (r6 != 0) goto L_0x01c7
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random
            long r30 = r0.nextLong()
            r6 = r30
            r23 = 0
            goto L_0x01cb
        L_0x01c7:
            r6 = r23
            r23 = r0
        L_0x01cb:
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            r24 = r5
            java.lang.String r5 = "video/mp4"
            r35 = r6
            java.lang.String r6 = "1"
            java.lang.String r7 = "final"
            java.lang.String r1 = "groupId"
            r25 = r2
            java.lang.String r2 = "mp4"
            r37 = r9
            java.lang.String r9 = "originalPath"
            r38 = r11
            java.lang.String r11 = ""
            r39 = r3
            java.lang.String r3 = "jpg"
            r40 = r4
            java.lang.String r4 = "."
            r41 = 4
            if (r0 == 0) goto L_0x053d
            org.telegram.messenger.VideoEditedInfo r12 = r8.videoEditedInfo
            if (r12 != 0) goto L_0x053d
            int r12 = r0.type
            r13 = 1
            if (r12 != r13) goto L_0x03c7
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r1 == 0) goto L_0x020e
            org.telegram.tgnet.TLRPC$TL_document r0 = (org.telegram.tgnet.TLRPC$TL_document) r0
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r13)
            goto L_0x023a
        L_0x020e:
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
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r1.<init>(r6, r0)
            r0 = 0
        L_0x023a:
            if (r0 != 0) goto L_0x0355
            org.telegram.tgnet.TLRPC$TL_document r6 = new org.telegram.tgnet.TLRPC$TL_document
            r6.<init>()
            r11 = 0
            r6.id = r11
            r13 = 0
            byte[] r0 = new byte[r13]
            r6.file_reference = r0
            org.telegram.tgnet.ConnectionsManager r0 = r72.getConnectionsManager()
            int r0 = r0.getCurrentTime()
            r6.date = r0
            org.telegram.tgnet.TLRPC$TL_documentAttributeFilename r0 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename
            r0.<init>()
            java.lang.String r13 = "animation.gif"
            r0.file_name = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r6.attributes
            r13.add(r0)
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            int r0 = r0.size
            r6.size = r0
            r13 = 0
            r6.dc_id = r13
            if (r70 != 0) goto L_0x0284
            java.lang.String r0 = r1.toString()
            boolean r0 = r0.endsWith(r2)
            if (r0 == 0) goto L_0x0284
            r6.mime_type = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r0 = r6.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r5.<init>()
            r0.add(r5)
            goto L_0x0288
        L_0x0284:
            java.lang.String r0 = "image/gif"
            r6.mime_type = r0
        L_0x0288:
            boolean r0 = r1.exists()
            if (r0 == 0) goto L_0x0290
            r5 = r1
            goto L_0x0292
        L_0x0290:
            r1 = 0
            r5 = 0
        L_0x0292:
            if (r5 != 0) goto L_0x02c6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r5 = r8.searchImage
            java.lang.String r5 = r5.thumbUrl
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)
            r0.append(r5)
            r0.append(r4)
            org.telegram.messenger.MediaController$SearchImage r4 = r8.searchImage
            java.lang.String r4 = r4.thumbUrl
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r3)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.io.File r5 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r5.<init>(r3, r0)
            boolean r0 = r5.exists()
            if (r0 != 0) goto L_0x02c6
            r5 = 0
        L_0x02c6:
            if (r5 == 0) goto L_0x031d
            if (r10 != 0) goto L_0x02d5
            int r0 = r8.ttl     // Catch:{ Exception -> 0x02d2 }
            if (r0 == 0) goto L_0x02cf
            goto L_0x02d5
        L_0x02cf:
            r0 = 320(0x140, float:4.48E-43)
            goto L_0x02d7
        L_0x02d2:
            r0 = move-exception
            r13 = 0
            goto L_0x0319
        L_0x02d5:
            r0 = 90
        L_0x02d7:
            java.lang.String r3 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x02d2 }
            boolean r2 = r3.endsWith(r2)     // Catch:{ Exception -> 0x02d2 }
            if (r2 == 0) goto L_0x02ec
            java.lang.String r2 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x02d2 }
            r3 = 1
            android.graphics.Bitmap r2 = createVideoThumbnail(r2, r3)     // Catch:{ Exception -> 0x02d2 }
            r13 = 0
            goto L_0x02f7
        L_0x02ec:
            r3 = 1
            java.lang.String r2 = r5.getAbsolutePath()     // Catch:{ Exception -> 0x02d2 }
            float r4 = (float) r0
            r13 = 0
            android.graphics.Bitmap r2 = org.telegram.messenger.ImageLoader.loadBitmap(r2, r13, r4, r4, r3)     // Catch:{ Exception -> 0x0318 }
        L_0x02f7:
            if (r2 == 0) goto L_0x031e
            float r3 = (float) r0     // Catch:{ Exception -> 0x0318 }
            r4 = 90
            if (r0 <= r4) goto L_0x0301
            r0 = 80
            goto L_0x0303
        L_0x0301:
            r0 = 55
        L_0x0303:
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r2, r3, r3, r0, r10)     // Catch:{ Exception -> 0x0318 }
            if (r0 == 0) goto L_0x0314
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r3 = r6.thumbs     // Catch:{ Exception -> 0x0318 }
            r3.add(r0)     // Catch:{ Exception -> 0x0318 }
            int r0 = r6.flags     // Catch:{ Exception -> 0x0318 }
            r3 = 1
            r0 = r0 | r3
            r6.flags = r0     // Catch:{ Exception -> 0x0318 }
        L_0x0314:
            r2.recycle()     // Catch:{ Exception -> 0x0318 }
            goto L_0x031e
        L_0x0318:
            r0 = move-exception
        L_0x0319:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x031e
        L_0x031d:
            r13 = 0
        L_0x031e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r6.thumbs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0351
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r8.searchImage
            int r3 = r2.width
            r0.w = r3
            int r2 = r2.height
            r0.h = r2
            r5 = 0
            r0.size = r5
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
            goto L_0x035c
        L_0x0351:
            r5 = 0
            r16 = 1
            goto L_0x035c
        L_0x0355:
            r5 = 0
            r11 = 0
            r13 = 0
            r16 = 1
            r6 = r0
        L_0x035c:
            org.telegram.messenger.MediaController$SearchImage r0 = r8.searchImage
            java.lang.String r0 = r0.imageUrl
            if (r1 != 0) goto L_0x0363
            goto L_0x0367
        L_0x0363:
            java.lang.String r0 = r1.toString()
        L_0x0367:
            org.telegram.messenger.MediaController$SearchImage r1 = r8.searchImage
            java.lang.String r1 = r1.imageUrl
            if (r1 == 0) goto L_0x0370
            r7.put(r9, r1)
        L_0x0370:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda13 r1 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda13
            r9 = 0
            r4 = r25
            r2 = r1
            r42 = r39
            r3 = r73
            r44 = r4
            r43 = r40
            r4 = r72
            r45 = r24
            r17 = 0
            r5 = r6
            r24 = r11
            r11 = r35
            r6 = r0
            r13 = r8
            r46 = r26
            r8 = r9
            r48 = r10
            r49 = r37
            r9 = r68
            r50 = r11
            r52 = r38
            r12 = 0
            r11 = r74
            r12 = r75
            r53 = r14
            r14 = r76
            r15 = r77
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            r0 = r23
            r26 = r27
            r35 = r28
            r3 = r42
            r4 = r43
            r2 = r44
            r5 = r45
            r27 = r46
            r28 = r48
            r32 = r49
            r37 = r50
            r33 = r52
            r65 = r53
        L_0x03c3:
            r34 = 0
            goto L_0x0d9d
        L_0x03c7:
            r13 = r8
            r48 = r10
            r53 = r14
            r45 = r24
            r44 = r25
            r46 = r26
            r50 = r35
            r49 = r37
            r52 = r38
            r42 = r39
            r43 = r40
            r24 = 0
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r2 == 0) goto L_0x03e8
            r5 = r0
            org.telegram.tgnet.TLRPC$TL_photo r5 = (org.telegram.tgnet.TLRPC$TL_photo) r5
            goto L_0x03e9
        L_0x03e8:
            r5 = 0
        L_0x03e9:
            if (r5 != 0) goto L_0x04b1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r13.searchImage
            java.lang.String r2 = r2.imageUrl
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r0.append(r2)
            r0.append(r4)
            org.telegram.messenger.MediaController$SearchImage r2 = r13.searchImage
            java.lang.String r2 = r2.imageUrl
            java.lang.String r2 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r2, r3)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.io.File r2 = new java.io.File
            java.io.File r8 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r2.<init>(r8, r0)
            boolean r0 = r2.exists()
            if (r0 == 0) goto L_0x0435
            long r14 = r2.length()
            int r0 = (r14 > r24 ? 1 : (r14 == r24 ? 0 : -1))
            if (r0 == 0) goto L_0x0435
            org.telegram.messenger.SendMessagesHelper r0 = r72.getSendMessagesHelper()
            java.lang.String r2 = r2.toString()
            r15 = 0
            org.telegram.tgnet.TLRPC$TL_photo r5 = r0.generatePhotoSizes(r2, r15)
            if (r5 == 0) goto L_0x0436
            r0 = 0
            goto L_0x0437
        L_0x0435:
            r15 = 0
        L_0x0436:
            r0 = 1
        L_0x0437:
            if (r5 != 0) goto L_0x04af
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.messenger.MediaController$SearchImage r8 = r13.searchImage
            java.lang.String r8 = r8.thumbUrl
            java.lang.String r8 = org.telegram.messenger.Utilities.MD5(r8)
            r2.append(r8)
            r2.append(r4)
            org.telegram.messenger.MediaController$SearchImage r4 = r13.searchImage
            java.lang.String r4 = r4.thumbUrl
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r3)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r3.<init>(r4, r2)
            boolean r2 = r3.exists()
            if (r2 == 0) goto L_0x0476
            org.telegram.messenger.SendMessagesHelper r2 = r72.getSendMessagesHelper()
            java.lang.String r3 = r3.toString()
            org.telegram.tgnet.TLRPC$TL_photo r5 = r2.generatePhotoSizes(r3, r15)
        L_0x0476:
            if (r5 != 0) goto L_0x04af
            org.telegram.tgnet.TLRPC$TL_photo r2 = new org.telegram.tgnet.TLRPC$TL_photo
            r2.<init>()
            org.telegram.tgnet.ConnectionsManager r3 = r72.getConnectionsManager()
            int r3 = r3.getCurrentTime()
            r2.date = r3
            r14 = 0
            byte[] r3 = new byte[r14]
            r2.file_reference = r3
            org.telegram.tgnet.TLRPC$TL_photoSize r3 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r3.<init>()
            org.telegram.messenger.MediaController$SearchImage r4 = r13.searchImage
            int r5 = r4.width
            r3.w = r5
            int r4 = r4.height
            r3.h = r4
            r3.size = r14
            org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable r4 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable
            r4.<init>()
            r3.location = r4
            java.lang.String r4 = "x"
            r3.type = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r2.sizes
            r4.add(r3)
            r5 = r2
            goto L_0x04b4
        L_0x04af:
            r14 = 0
            goto L_0x04b4
        L_0x04b1:
            r14 = 0
            r15 = 0
            r0 = 1
        L_0x04b4:
            java.util.HashMap r8 = new java.util.HashMap
            r8.<init>()
            org.telegram.messenger.MediaController$SearchImage r2 = r13.searchImage
            java.lang.String r2 = r2.imageUrl
            if (r2 == 0) goto L_0x04c2
            r8.put(r9, r2)
        L_0x04c2:
            if (r71 == 0) goto L_0x04f6
            int r2 = r23 + 1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r11)
            r10 = r50
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            r8.put(r1, r3)
            r1 = 10
            if (r2 == r1) goto L_0x04ea
            r1 = r53
            int r3 = r1 + -1
            r12 = r49
            if (r12 != r3) goto L_0x04e7
            goto L_0x04ee
        L_0x04e7:
            r23 = r2
            goto L_0x04fc
        L_0x04ea:
            r12 = r49
            r1 = r53
        L_0x04ee:
            r8.put(r7, r6)
            r23 = r2
            r30 = r24
            goto L_0x04fc
        L_0x04f6:
            r12 = r49
            r10 = r50
            r1 = r53
        L_0x04fc:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda15 r16 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda15
            r9 = 0
            r2 = r16
            r3 = r73
            r4 = r72
            r6 = r0
            r7 = r13
            r54 = r10
            r10 = r68
            r13 = r12
            r12 = r74
            r56 = r13
            r13 = r75
            r14 = r76
            r53 = r1
            r1 = r15
            r15 = r77
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r16)
            r34 = r1
            r0 = r23
            r26 = r27
            r35 = r28
            r3 = r42
            r4 = r43
            r2 = r44
            r5 = r45
            r27 = r46
            r28 = r48
            r33 = r52
            r65 = r53
            r37 = r54
            r32 = r56
            goto L_0x0d9d
        L_0x053d:
            r13 = r8
            r48 = r10
            r15 = r14
            r45 = r24
            r44 = r25
            r46 = r26
            r54 = r35
            r56 = r37
            r52 = r38
            r42 = r39
            r43 = r40
            r24 = 0
            r8 = r1
            r1 = 0
            boolean r0 = r13.isVideo
            if (r0 != 0) goto L_0x0984
            org.telegram.messenger.VideoEditedInfo r0 = r13.videoEditedInfo
            if (r0 == 0) goto L_0x055f
            goto L_0x0984
        L_0x055f:
            java.lang.String r0 = r13.path
            if (r0 != 0) goto L_0x0589
            android.net.Uri r2 = r13.uri
            if (r2 == 0) goto L_0x0589
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 30
            if (r0 < r3) goto L_0x057b
            java.lang.String r0 = r2.getScheme()
            java.lang.String r2 = "content"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x057b
            r5 = r1
            goto L_0x0581
        L_0x057b:
            android.net.Uri r0 = r13.uri
            java.lang.String r5 = org.telegram.messenger.AndroidUtilities.getPath(r0)
        L_0x0581:
            android.net.Uri r0 = r13.uri
            java.lang.String r0 = r0.toString()
            r2 = r0
            goto L_0x058b
        L_0x0589:
            r2 = r0
            r5 = r2
        L_0x058b:
            if (r78 == 0) goto L_0x061e
            android.net.Uri r0 = r13.uri
            if (r0 == 0) goto L_0x061e
            android.content.ClipDescription r0 = r78.getDescription()
            java.lang.String r3 = "image/png"
            boolean r0 = r0.hasMimeType(r3)
            if (r0 == 0) goto L_0x061e
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x05fd }
            r0.<init>()     // Catch:{ all -> 0x05fd }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x05fd }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x05fd }
            android.net.Uri r4 = r13.uri     // Catch:{ all -> 0x05fd }
            java.io.InputStream r3 = r3.openInputStream(r4)     // Catch:{ all -> 0x05fd }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r3, r1, r0)     // Catch:{ all -> 0x05f9 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x05f9 }
            r4.<init>()     // Catch:{ all -> 0x05f9 }
            java.lang.String r10 = "-2147483648_"
            r4.append(r10)     // Catch:{ all -> 0x05f9 }
            int r10 = org.telegram.messenger.SharedConfig.getLastLocalId()     // Catch:{ all -> 0x05f9 }
            r4.append(r10)     // Catch:{ all -> 0x05f9 }
            r14 = r46
            r4.append(r14)     // Catch:{ all -> 0x05f7 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x05f7 }
            java.io.File r10 = org.telegram.messenger.FileLoader.getDirectory(r41)     // Catch:{ all -> 0x05f7 }
            java.io.File r12 = new java.io.File     // Catch:{ all -> 0x05f7 }
            r12.<init>(r10, r4)     // Catch:{ all -> 0x05f7 }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ all -> 0x05f7 }
            r4.<init>(r12)     // Catch:{ all -> 0x05f7 }
            android.graphics.Bitmap$CompressFormat r10 = android.graphics.Bitmap.CompressFormat.WEBP     // Catch:{ all -> 0x05f5 }
            r1 = 100
            r0.compress(r10, r1, r4)     // Catch:{ all -> 0x05f5 }
            org.telegram.messenger.SharedConfig.saveConfig()     // Catch:{ all -> 0x05f5 }
            android.net.Uri r0 = android.net.Uri.fromFile(r12)     // Catch:{ all -> 0x05f5 }
            r13.uri = r0     // Catch:{ all -> 0x05f5 }
            if (r3 == 0) goto L_0x05ef
            r3.close()     // Catch:{ Exception -> 0x05ef }
        L_0x05ef:
            r4.close()     // Catch:{ Exception -> 0x05f3 }
            goto L_0x0620
        L_0x05f3:
            goto L_0x0620
        L_0x05f5:
            r0 = move-exception
            goto L_0x0602
        L_0x05f7:
            r0 = move-exception
            goto L_0x0601
        L_0x05f9:
            r0 = move-exception
            r14 = r46
            goto L_0x0601
        L_0x05fd:
            r0 = move-exception
            r14 = r46
            r3 = 0
        L_0x0601:
            r4 = 0
        L_0x0602:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x060f }
            if (r3 == 0) goto L_0x060c
            r3.close()     // Catch:{ Exception -> 0x060b }
            goto L_0x060c
        L_0x060b:
        L_0x060c:
            if (r4 == 0) goto L_0x0620
            goto L_0x05ef
        L_0x060f:
            r0 = move-exception
            r1 = r0
            if (r3 == 0) goto L_0x0618
            r3.close()     // Catch:{ Exception -> 0x0617 }
            goto L_0x0618
        L_0x0617:
        L_0x0618:
            if (r4 == 0) goto L_0x061d
            r4.close()     // Catch:{ Exception -> 0x061d }
        L_0x061d:
            throw r1
        L_0x061e:
            r14 = r46
        L_0x0620:
            java.lang.String r0 = "webp"
            java.lang.String r1 = "gif"
            if (r70 != 0) goto L_0x068b
            java.lang.String r3 = r13.path
            android.net.Uri r4 = r13.uri
            boolean r3 = org.telegram.messenger.ImageLoader.shouldSendImageAsDocument(r3, r4)
            if (r3 == 0) goto L_0x0631
            goto L_0x068b
        L_0x0631:
            boolean r3 = r13.forceImage
            if (r3 != 0) goto L_0x0650
            if (r5 == 0) goto L_0x0650
            r12 = r27
            boolean r3 = r5.endsWith(r12)
            if (r3 != 0) goto L_0x0645
            boolean r3 = r5.endsWith(r14)
            if (r3 == 0) goto L_0x0652
        L_0x0645:
            int r3 = r13.ttl
            if (r3 > 0) goto L_0x0652
            boolean r3 = r5.endsWith(r12)
            if (r3 == 0) goto L_0x0685
            goto L_0x066e
        L_0x0650:
            r12 = r27
        L_0x0652:
            boolean r3 = r13.forceImage
            if (r3 != 0) goto L_0x0688
            if (r5 != 0) goto L_0x0688
            android.net.Uri r3 = r13.uri
            if (r3 == 0) goto L_0x0688
            boolean r3 = org.telegram.messenger.MediaController.isGif(r3)
            if (r3 == 0) goto L_0x0671
            android.net.Uri r0 = r13.uri
            java.lang.String r2 = r0.toString()
            android.net.Uri r0 = r13.uri
            java.lang.String r5 = org.telegram.messenger.MediaController.copyFileToCache(r0, r1)
        L_0x066e:
            r22 = r1
            goto L_0x069b
        L_0x0671:
            android.net.Uri r1 = r13.uri
            boolean r1 = org.telegram.messenger.MediaController.isWebp(r1)
            if (r1 == 0) goto L_0x0688
            android.net.Uri r1 = r13.uri
            java.lang.String r2 = r1.toString()
            android.net.Uri r1 = r13.uri
            java.lang.String r5 = org.telegram.messenger.MediaController.copyFileToCache(r1, r0)
        L_0x0685:
            r22 = r0
            goto L_0x069b
        L_0x0688:
            r1 = r5
            r0 = 0
            goto L_0x069d
        L_0x068b:
            r12 = r27
            if (r5 == 0) goto L_0x0699
            java.io.File r0 = new java.io.File
            r0.<init>(r5)
            java.lang.String r0 = org.telegram.messenger.FileLoader.getFileExtension(r0)
            goto L_0x0685
        L_0x0699:
            r22 = r11
        L_0x069b:
            r1 = r5
            r0 = 1
        L_0x069d:
            if (r0 == 0) goto L_0x06f4
            r10 = r45
            if (r10 != 0) goto L_0x06bf
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r29 = new java.util.ArrayList
            r29.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r6 = r29
            goto L_0x06c8
        L_0x06bf:
            r5 = r10
            r6 = r29
            r3 = r42
            r4 = r43
            r0 = r44
        L_0x06c8:
            r5.add(r1)
            r4.add(r2)
            android.net.Uri r1 = r13.uri
            r3.add(r1)
            java.lang.String r1 = r13.caption
            r0.add(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r13.entities
            r6.add(r1)
            r2 = r0
            r29 = r6
            r26 = r12
            r27 = r14
            r65 = r15
            r0 = r23
            r35 = r28
            r28 = r48
            r33 = r52
            r37 = r54
            r32 = r56
            goto L_0x03c3
        L_0x06f4:
            r10 = r45
            if (r1 == 0) goto L_0x071f
            java.io.File r0 = new java.io.File
            r0.<init>(r1)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            long r4 = r0.length()
            r3.append(r4)
            r5 = r28
            r3.append(r5)
            long r4 = r0.lastModified()
            r3.append(r4)
            java.lang.String r5 = r3.toString()
            r4 = r52
            goto L_0x0722
        L_0x071f:
            r4 = r52
            r5 = 0
        L_0x0722:
            if (r4 == 0) goto L_0x0755
            java.lang.Object r0 = r4.get(r13)
            r2 = r0
            org.telegram.messenger.SendMessagesHelper$MediaSendPrepareWorker r2 = (org.telegram.messenger.SendMessagesHelper.MediaSendPrepareWorker) r2
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
            if (r0 != 0) goto L_0x073f
            java.util.concurrent.CountDownLatch r0 = r2.sync     // Catch:{ Exception -> 0x0737 }
            r0.await()     // Catch:{ Exception -> 0x0737 }
            goto L_0x073b
        L_0x0737:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x073b:
            org.telegram.tgnet.TLRPC$TL_photo r0 = r2.photo
            java.lang.String r3 = r2.parentObject
        L_0x073f:
            r16 = r1
            r33 = r4
            r45 = r10
            r17 = r12
            r46 = r14
            r58 = r28
            r1 = 1
            r10 = r5
            r12 = r6
            r14 = r7
            r7 = r48
            r6 = r0
            r5 = r3
            goto L_0x081d
        L_0x0755:
            r3 = r48
            if (r3 != 0) goto L_0x07e1
            int r0 = r13.ttl
            if (r0 != 0) goto L_0x07e1
            org.telegram.messenger.MessagesStorage r0 = r72.getMessagesStorage()
            if (r3 != 0) goto L_0x0765
            r2 = 0
            goto L_0x0766
        L_0x0765:
            r2 = 3
        L_0x0766:
            java.lang.Object[] r0 = r0.getSentFile(r5, r2)
            r52 = r4
            if (r0 == 0) goto L_0x0784
            r2 = 0
            r4 = r0[r2]
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r4 == 0) goto L_0x0781
            r4 = r0[r2]
            org.telegram.tgnet.TLRPC$TL_photo r4 = (org.telegram.tgnet.TLRPC$TL_photo) r4
            r16 = r6
            r6 = 1
            r0 = r0[r6]
            java.lang.String r0 = (java.lang.String) r0
            goto L_0x078a
        L_0x0781:
            r16 = r6
            goto L_0x0787
        L_0x0784:
            r16 = r6
            r2 = 0
        L_0x0787:
            r6 = 1
            r0 = 0
            r4 = 0
        L_0x078a:
            if (r4 != 0) goto L_0x07b7
            android.net.Uri r6 = r13.uri
            if (r6 == 0) goto L_0x07ba
            org.telegram.messenger.MessagesStorage r6 = r72.getMessagesStorage()
            android.net.Uri r2 = r13.uri
            java.lang.String r2 = org.telegram.messenger.AndroidUtilities.getPath(r2)
            r18 = r0
            if (r3 != 0) goto L_0x07a0
            r0 = 0
            goto L_0x07a1
        L_0x07a0:
            r0 = 3
        L_0x07a1:
            java.lang.Object[] r0 = r6.getSentFile(r2, r0)
            r2 = 0
            if (r0 == 0) goto L_0x07bc
            r6 = r0[r2]
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r6 == 0) goto L_0x07bc
            r4 = r0[r2]
            org.telegram.tgnet.TLRPC$TL_photo r4 = (org.telegram.tgnet.TLRPC$TL_photo) r4
            r6 = 1
            r0 = r0[r6]
            java.lang.String r0 = (java.lang.String) r0
        L_0x07b7:
            r18 = r0
            goto L_0x07bd
        L_0x07ba:
            r18 = r0
        L_0x07bc:
            r6 = 1
        L_0x07bd:
            r0 = r4
            java.lang.String r4 = r13.path
            android.net.Uri r6 = r13.uri
            r26 = 0
            r2 = r3
            r57 = r3
            r3 = r0
            r33 = r52
            r45 = r10
            r58 = r28
            r10 = r5
            r5 = r6
            r17 = r12
            r46 = r14
            r12 = r16
            r16 = r1
            r14 = r7
            r1 = 1
            r6 = r26
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r5 = r0
            goto L_0x07f6
        L_0x07e1:
            r16 = r1
            r57 = r3
            r33 = r4
            r45 = r10
            r17 = r12
            r46 = r14
            r58 = r28
            r1 = 1
            r10 = r5
            r12 = r6
            r14 = r7
            r5 = 0
            r18 = 0
        L_0x07f6:
            if (r5 != 0) goto L_0x0818
            org.telegram.messenger.SendMessagesHelper r0 = r72.getSendMessagesHelper()
            java.lang.String r2 = r13.path
            android.net.Uri r3 = r13.uri
            org.telegram.tgnet.TLRPC$TL_photo r0 = r0.generatePhotoSizes(r2, r3)
            r7 = r57
            if (r7 == 0) goto L_0x0816
            boolean r2 = r13.canDeleteAfter
            if (r2 == 0) goto L_0x0816
            java.io.File r2 = new java.io.File
            java.lang.String r3 = r13.path
            r2.<init>(r3)
            r2.delete()
        L_0x0816:
            r6 = r0
            goto L_0x081b
        L_0x0818:
            r7 = r57
            r6 = r5
        L_0x081b:
            r5 = r18
        L_0x081d:
            if (r6 == 0) goto L_0x0922
            java.util.HashMap r4 = new java.util.HashMap
            r4.<init>()
            android.graphics.Bitmap[] r3 = new android.graphics.Bitmap[r1]
            java.lang.String[] r2 = new java.lang.String[r1]
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r0 = r13.masks
            if (r0 == 0) goto L_0x0834
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0834
            r0 = 1
            goto L_0x0835
        L_0x0834:
            r0 = 0
        L_0x0835:
            r6.has_stickers = r0
            if (r0 == 0) goto L_0x087b
            org.telegram.tgnet.SerializedData r0 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r13.masks
            int r1 = r1.size()
            int r1 = r1 * 20
            int r1 = r1 + 4
            r0.<init>((int) r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r13.masks
            int r1 = r1.size()
            r0.writeInt32(r1)
            r48 = r7
            r1 = 0
        L_0x0854:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r7 = r13.masks
            int r7 = r7.size()
            if (r1 >= r7) goto L_0x086a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r7 = r13.masks
            java.lang.Object r7 = r7.get(r1)
            org.telegram.tgnet.TLRPC$InputDocument r7 = (org.telegram.tgnet.TLRPC$InputDocument) r7
            r7.serializeToStream(r0)
            int r1 = r1 + 1
            goto L_0x0854
        L_0x086a:
            byte[] r1 = r0.toByteArray()
            java.lang.String r1 = org.telegram.messenger.Utilities.bytesToHex(r1)
            java.lang.String r7 = "masks"
            r4.put(r7, r1)
            r0.cleanup()
            goto L_0x087d
        L_0x087b:
            r48 = r7
        L_0x087d:
            if (r10 == 0) goto L_0x0882
            r4.put(r9, r10)
        L_0x0882:
            if (r5 == 0) goto L_0x0889
            java.lang.String r0 = "parentObject"
            r4.put(r0, r5)
        L_0x0889:
            if (r71 == 0) goto L_0x0895
            int r0 = r67.size()     // Catch:{ Exception -> 0x08ab }
            r1 = 1
            if (r0 != r1) goto L_0x0893
            goto L_0x0895
        L_0x0893:
            r1 = 0
            goto L_0x08b0
        L_0x0895:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r6.sizes     // Catch:{ Exception -> 0x08ab }
            int r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize()     // Catch:{ Exception -> 0x08ab }
            org.telegram.tgnet.TLRPC$PhotoSize r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r1)     // Catch:{ Exception -> 0x08ab }
            if (r0 == 0) goto L_0x0893
            r1 = 0
            java.lang.String r0 = getKeyForPhotoSize(r0, r3, r1, r1)     // Catch:{ Exception -> 0x08a9 }
            r2[r1] = r0     // Catch:{ Exception -> 0x08a9 }
            goto L_0x08b0
        L_0x08a9:
            r0 = move-exception
            goto L_0x08ad
        L_0x08ab:
            r0 = move-exception
            r1 = 0
        L_0x08ad:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x08b0:
            if (r71 == 0) goto L_0x08e0
            int r0 = r23 + 1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r11)
            r10 = r54
            r7.append(r10)
            java.lang.String r7 = r7.toString()
            r4.put(r8, r7)
            r7 = 10
            if (r0 == r7) goto L_0x08d6
            int r7 = r15 + -1
            r9 = r56
            if (r9 != r7) goto L_0x08d3
            goto L_0x08d8
        L_0x08d3:
            r23 = r0
            goto L_0x08e4
        L_0x08d6:
            r9 = r56
        L_0x08d8:
            r4.put(r14, r12)
            r23 = r0
            r30 = r24
            goto L_0x08e4
        L_0x08e0:
            r10 = r54
            r9 = r56
        L_0x08e4:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda74 r0 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda74
            r7 = r2
            r2 = r0
            r8 = r4
            r4 = r7
            r18 = r5
            r5 = r73
            r7 = r6
            r6 = r72
            r14 = r48
            r12 = r9
            r9 = r18
            r59 = r10
            r1 = r45
            r10 = r68
            r61 = r12
            r26 = r17
            r12 = r74
            r17 = r13
            r13 = r75
            r62 = r14
            r27 = r46
            r14 = r17
            r63 = r15
            r15 = r76
            r16 = r77
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            r5 = r1
            r0 = r23
            r3 = r42
            r4 = r43
            r2 = r44
            goto L_0x0978
        L_0x0922:
            r62 = r7
            r63 = r15
            r26 = r17
            r1 = r45
            r27 = r46
            r59 = r54
            r61 = r56
            r17 = r13
            if (r1 != 0) goto L_0x0952
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r29 = new java.util.ArrayList
            r29.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r1 = r16
            r0 = r29
            goto L_0x095d
        L_0x0952:
            r5 = r1
            r1 = r16
            r0 = r29
            r3 = r42
            r4 = r43
            r2 = r44
        L_0x095d:
            r5.add(r1)
            r4.add(r10)
            r15 = r17
            android.net.Uri r1 = r15.uri
            r3.add(r1)
            java.lang.String r1 = r15.caption
            r2.add(r1)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r15.entities
            r0.add(r1)
            r29 = r0
            r0 = r23
        L_0x0978:
            r35 = r58
            r37 = r59
            r32 = r61
            r28 = r62
            r65 = r63
            goto L_0x03c3
        L_0x0984:
            r12 = r6
            r14 = r7
            r63 = r15
            r26 = r27
            r58 = r28
            r1 = r45
            r27 = r46
            r62 = r48
            r33 = r52
            r59 = r54
            r61 = r56
            r15 = r13
            if (r70 == 0) goto L_0x099d
            r0 = 0
            goto L_0x09a8
        L_0x099d:
            org.telegram.messenger.VideoEditedInfo r0 = r15.videoEditedInfo
            if (r0 == 0) goto L_0x09a2
            goto L_0x09a8
        L_0x09a2:
            java.lang.String r0 = r15.path
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r0)
        L_0x09a8:
            if (r70 != 0) goto L_0x0d43
            if (r0 != 0) goto L_0x09b4
            java.lang.String r6 = r15.path
            boolean r2 = r6.endsWith(r2)
            if (r2 == 0) goto L_0x0d43
        L_0x09b4:
            java.lang.String r2 = r15.path
            if (r2 != 0) goto L_0x09ff
            org.telegram.messenger.MediaController$SearchImage r2 = r15.searchImage
            if (r2 == 0) goto L_0x09ff
            org.telegram.tgnet.TLRPC$Photo r2 = r2.photo
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photo
            if (r6 == 0) goto L_0x09ce
            r6 = 1
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToAttach(r2, r6)
            java.lang.String r2 = r2.getAbsolutePath()
            r15.path = r2
            goto L_0x09ff
        L_0x09ce:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.messenger.MediaController$SearchImage r6 = r15.searchImage
            java.lang.String r6 = r6.imageUrl
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r6)
            r2.append(r6)
            r2.append(r4)
            org.telegram.messenger.MediaController$SearchImage r4 = r15.searchImage
            java.lang.String r4 = r4.imageUrl
            java.lang.String r3 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r4, r3)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r3.<init>(r4, r2)
            java.lang.String r2 = r3.getAbsolutePath()
            r15.path = r2
        L_0x09ff:
            java.lang.String r10 = r15.path
            java.io.File r13 = new java.io.File
            r13.<init>(r10)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r10)
            long r3 = r13.length()
            r2.append(r3)
            r6 = r58
            r2.append(r6)
            long r3 = r13.lastModified()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            if (r0 == 0) goto L_0x0a7f
            boolean r3 = r0.muted
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            r7 = r3
            long r2 = r0.estimatedDuration
            r4.append(r2)
            r4.append(r6)
            long r2 = r0.startTime
            r4.append(r2)
            r4.append(r6)
            long r2 = r0.endTime
            r4.append(r2)
            boolean r2 = r0.muted
            if (r2 == 0) goto L_0x0a4e
            java.lang.String r2 = "_m"
            goto L_0x0a4f
        L_0x0a4e:
            r2 = r11
        L_0x0a4f:
            r4.append(r2)
            java.lang.String r2 = r4.toString()
            int r3 = r0.resultWidth
            int r4 = r0.originalWidth
            if (r3 == r4) goto L_0x0a70
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            r3.append(r6)
            int r2 = r0.resultWidth
            r3.append(r2)
            java.lang.String r2 = r3.toString()
        L_0x0a70:
            long r3 = r0.startTime
            int r16 = (r3 > r24 ? 1 : (r3 == r24 ? 0 : -1))
            if (r16 < 0) goto L_0x0a77
            goto L_0x0a79
        L_0x0a77:
            r3 = r24
        L_0x0a79:
            r16 = r7
            r7 = r2
            r2 = r62
            goto L_0x0a86
        L_0x0a7f:
            r7 = r2
            r3 = r24
            r2 = r62
            r16 = 0
        L_0x0a86:
            if (r2 != 0) goto L_0x0af3
            r17 = r3
            int r3 = r15.ttl
            if (r3 != 0) goto L_0x0aee
            if (r0 == 0) goto L_0x0aa0
            org.telegram.messenger.MediaController$SavedFilterState r3 = r0.filterState
            if (r3 != 0) goto L_0x0aee
            java.lang.String r3 = r0.paintPath
            if (r3 != 0) goto L_0x0aee
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r3 = r0.mediaEntities
            if (r3 != 0) goto L_0x0aee
            org.telegram.messenger.MediaController$CropState r3 = r0.cropState
            if (r3 != 0) goto L_0x0aee
        L_0x0aa0:
            org.telegram.messenger.MessagesStorage r3 = r72.getMessagesStorage()
            if (r2 != 0) goto L_0x0aa8
            r4 = 2
            goto L_0x0aa9
        L_0x0aa8:
            r4 = 5
        L_0x0aa9:
            java.lang.Object[] r3 = r3.getSentFile(r7, r4)
            if (r3 == 0) goto L_0x0aee
            r48 = r2
            r4 = 0
            r2 = r3[r4]
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r2 == 0) goto L_0x0ae4
            r2 = r3[r4]
            r28 = r2
            org.telegram.tgnet.TLRPC$TL_document r28 = (org.telegram.tgnet.TLRPC$TL_document) r28
            r2 = 1
            r3 = r3[r2]
            r34 = r3
            java.lang.String r34 = (java.lang.String) r34
            java.lang.String r4 = r15.path
            r35 = 0
            r3 = r48
            r2 = r3
            r64 = r3
            r3 = r28
            r36 = r10
            r10 = r5
            r5 = r35
            r45 = r1
            r35 = r6
            r1 = r7
            r6 = r17
            ensureMediaThumbExists(r2, r3, r4, r5, r6)
            r5 = r28
            r7 = r34
            goto L_0x0b01
        L_0x0ae4:
            r45 = r1
            r35 = r6
            r1 = r7
            r36 = r10
            r64 = r48
            goto L_0x0afe
        L_0x0aee:
            r45 = r1
            r64 = r2
            goto L_0x0af9
        L_0x0af3:
            r45 = r1
            r64 = r2
            r17 = r3
        L_0x0af9:
            r35 = r6
            r1 = r7
            r36 = r10
        L_0x0afe:
            r10 = r5
            r5 = 0
            r7 = 0
        L_0x0b01:
            if (r5 != 0) goto L_0x0bfc
            java.lang.String r2 = r15.thumbPath
            if (r2 == 0) goto L_0x0b0c
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeFile(r2)
            goto L_0x0b0d
        L_0x0b0c:
            r5 = 0
        L_0x0b0d:
            if (r5 != 0) goto L_0x0b20
            java.lang.String r2 = r15.path
            r3 = r17
            android.graphics.Bitmap r5 = createVideoThumbnailAtTime(r2, r3)
            if (r5 != 0) goto L_0x0b20
            java.lang.String r2 = r15.path
            r3 = 1
            android.graphics.Bitmap r5 = createVideoThumbnail(r2, r3)
        L_0x0b20:
            if (r5 == 0) goto L_0x0b52
            r6 = r64
            if (r6 != 0) goto L_0x0b38
            int r2 = r15.ttl
            if (r2 == 0) goto L_0x0b2b
            goto L_0x0b38
        L_0x0b2b:
            int r2 = r5.getWidth()
            int r3 = r5.getHeight()
            int r2 = java.lang.Math.max(r2, r3)
            goto L_0x0b3a
        L_0x0b38:
            r2 = 90
        L_0x0b3a:
            float r3 = (float) r2
            r4 = 90
            if (r2 <= r4) goto L_0x0b42
            r2 = 80
            goto L_0x0b44
        L_0x0b42:
            r2 = 55
        L_0x0b44:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r5, r3, r3, r2, r6)
            r17 = r5
            r3 = 0
            r4 = 0
            r5 = 1
            java.lang.String r18 = getKeyForPhotoSize(r2, r3, r5, r4)
            goto L_0x0b5b
        L_0x0b52:
            r17 = r5
            r6 = r64
            r4 = 0
            r5 = 1
            r2 = 0
            r18 = 0
        L_0x0b5b:
            org.telegram.tgnet.TLRPC$TL_document r3 = new org.telegram.tgnet.TLRPC$TL_document
            r3.<init>()
            byte[] r5 = new byte[r4]
            r3.file_reference = r5
            if (r2 == 0) goto L_0x0b71
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r3.thumbs
            r4.add(r2)
            int r2 = r3.flags
            r4 = 1
            r2 = r2 | r4
            r3.flags = r2
        L_0x0b71:
            r3.mime_type = r10
            org.telegram.messenger.UserConfig r2 = r72.getUserConfig()
            r4 = 0
            r2.saveConfig(r4)
            if (r6 == 0) goto L_0x0b83
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r2.<init>()
            goto L_0x0b8b
        L_0x0b83:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r2.<init>()
            r4 = 1
            r2.supports_streaming = r4
        L_0x0b8b:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r2)
            if (r0 == 0) goto L_0x0be3
            boolean r4 = r0.needConvert()
            if (r4 != 0) goto L_0x0b9c
            boolean r4 = r15.isVideo
            if (r4 != 0) goto L_0x0be3
        L_0x0b9c:
            boolean r4 = r15.isVideo
            if (r4 == 0) goto L_0x0bb2
            boolean r4 = r0.muted
            if (r4 == 0) goto L_0x0bb2
            java.lang.String r4 = r15.path
            fillVideoAttribute(r4, r2, r0)
            int r4 = r2.w
            r0.originalWidth = r4
            int r4 = r2.h
            r0.originalHeight = r4
            goto L_0x0bbb
        L_0x0bb2:
            long r4 = r0.estimatedDuration
            r37 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 / r37
            int r5 = (int) r4
            r2.duration = r5
        L_0x0bbb:
            int r4 = r0.rotationValue
            org.telegram.messenger.MediaController$CropState r5 = r0.cropState
            if (r5 == 0) goto L_0x0bc6
            int r10 = r5.transformWidth
            int r5 = r5.transformHeight
            goto L_0x0bca
        L_0x0bc6:
            int r10 = r0.resultWidth
            int r5 = r0.resultHeight
        L_0x0bca:
            r13 = 90
            if (r4 == r13) goto L_0x0bd8
            r13 = 270(0x10e, float:3.78E-43)
            if (r4 != r13) goto L_0x0bd3
            goto L_0x0bd8
        L_0x0bd3:
            r2.w = r10
            r2.h = r5
            goto L_0x0bdc
        L_0x0bd8:
            r2.w = r5
            r2.h = r10
        L_0x0bdc:
            long r4 = r0.estimatedSize
            int r2 = (int) r4
            r3.size = r2
            r13 = 0
            goto L_0x0bf6
        L_0x0be3:
            boolean r4 = r13.exists()
            if (r4 == 0) goto L_0x0bf0
            long r4 = r13.length()
            int r5 = (int) r4
            r3.size = r5
        L_0x0bf0:
            java.lang.String r4 = r15.path
            r13 = 0
            fillVideoAttribute(r4, r2, r13)
        L_0x0bf6:
            r10 = r3
            r3 = r17
            r4 = r18
            goto L_0x0CLASSNAME
        L_0x0bfc:
            r6 = r64
            r13 = 0
            r10 = r5
            r3 = r13
            r4 = r3
        L_0x0CLASSNAME:
            if (r0 == 0) goto L_0x0c2e
            boolean r2 = r0.muted
            if (r2 == 0) goto L_0x0c2e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r10.attributes
            int r2 = r2.size()
            r5 = 0
        L_0x0c0f:
            if (r5 >= r2) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r10.attributes
            java.lang.Object r13 = r13.get(r5)
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            if (r13 == 0) goto L_0x0c1d
            r2 = 1
            goto L_0x0CLASSNAME
        L_0x0c1d:
            int r5 = r5 + 1
            r13 = 0
            goto L_0x0c0f
        L_0x0CLASSNAME:
            r2 = 0
        L_0x0CLASSNAME:
            if (r2 != 0) goto L_0x0c2e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r2 = r10.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r5 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r5.<init>()
            r2.add(r5)
        L_0x0c2e:
            if (r0 == 0) goto L_0x0CLASSNAME
            boolean r2 = r0.needConvert()
            if (r2 != 0) goto L_0x0c3a
            boolean r2 = r15.isVideo
            if (r2 != 0) goto L_0x0CLASSNAME
        L_0x0c3a:
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
            java.io.File r13 = org.telegram.messenger.FileLoader.getDirectory(r41)
            r5.<init>(r13, r2)
            org.telegram.messenger.SharedConfig.saveConfig()
            java.lang.String r2 = r5.getAbsolutePath()
            r36 = r2
        L_0x0CLASSNAME:
            java.util.HashMap r13 = new java.util.HashMap
            r13.<init>()
            if (r1 == 0) goto L_0x0CLASSNAME
            r13.put(r9, r1)
        L_0x0CLASSNAME:
            if (r7 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "parentObject"
            r13.put(r1, r7)
        L_0x0CLASSNAME:
            if (r16 != 0) goto L_0x0cae
            if (r71 == 0) goto L_0x0cae
            int r1 = r23 + 1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            r9 = r4
            r4 = r59
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r13.put(r8, r2)
            r2 = 10
            if (r1 == r2) goto L_0x0ca2
            r11 = r63
            int r2 = r11 + -1
            r8 = r61
            if (r8 != r2) goto L_0x0c9f
            goto L_0x0ca6
        L_0x0c9f:
            r23 = r1
            goto L_0x0cb5
        L_0x0ca2:
            r8 = r61
            r11 = r63
        L_0x0ca6:
            r13.put(r14, r12)
            r23 = r1
            r30 = r24
            goto L_0x0cb5
        L_0x0cae:
            r9 = r4
            r4 = r59
            r8 = r61
            r11 = r63
        L_0x0cb5:
            if (r6 != 0) goto L_0x0d0a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r1 = r15.masks
            if (r1 == 0) goto L_0x0d0a
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0d0a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r1 = r10.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers
            r2.<init>()
            r1.add(r2)
            org.telegram.tgnet.SerializedData r1 = new org.telegram.tgnet.SerializedData
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r15.masks
            int r2 = r2.size()
            int r2 = r2 * 20
            int r2 = r2 + 4
            r1.<init>((int) r2)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r2 = r15.masks
            int r2 = r2.size()
            r1.writeInt32(r2)
            r2 = 0
        L_0x0ce4:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r12 = r15.masks
            int r12 = r12.size()
            if (r2 >= r12) goto L_0x0cfa
            java.util.ArrayList<org.telegram.tgnet.TLRPC$InputDocument> r12 = r15.masks
            java.lang.Object r12 = r12.get(r2)
            org.telegram.tgnet.TLRPC$InputDocument r12 = (org.telegram.tgnet.TLRPC$InputDocument) r12
            r12.serializeToStream(r1)
            int r2 = r2 + 1
            goto L_0x0ce4
        L_0x0cfa:
            byte[] r2 = r1.toByteArray()
            java.lang.String r2 = org.telegram.messenger.Utilities.bytesToHex(r2)
            java.lang.String r12 = "masks"
            r13.put(r12, r2)
            r1.cleanup()
        L_0x0d0a:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda5 r1 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda5
            r2 = r1
            r37 = r4
            r4 = r9
            r5 = r73
            r28 = r6
            r6 = r72
            r47 = r7
            r7 = r0
            r32 = r8
            r8 = r10
            r9 = r36
            r10 = r13
            r14 = r11
            r11 = r47
            r34 = 0
            r12 = r68
            r65 = r14
            r14 = r74
            r17 = r15
            r15 = r75
            r16 = r17
            r17 = r76
            r18 = r77
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            r3 = r42
            r4 = r43
            r2 = r44
            r5 = r45
            goto L_0x0d9b
        L_0x0d43:
            r45 = r1
            r17 = r15
            r35 = r58
            r37 = r59
            r32 = r61
            r28 = r62
            r65 = r63
            r34 = 0
            if (r45 != 0) goto L_0x0d74
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r29 = new java.util.ArrayList
            r29.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            r5 = r0
            r1 = r17
            r0 = r29
            goto L_0x0d80
        L_0x0d74:
            r1 = r17
            r0 = r29
            r3 = r42
            r4 = r43
            r2 = r44
            r5 = r45
        L_0x0d80:
            java.lang.String r6 = r1.path
            r5.add(r6)
            java.lang.String r6 = r1.path
            r4.add(r6)
            android.net.Uri r6 = r1.uri
            r3.add(r6)
            java.lang.String r6 = r1.caption
            r2.add(r6)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
            r0.add(r1)
            r29 = r0
        L_0x0d9b:
            r0 = r23
        L_0x0d9d:
            int r9 = r32 + 1
            r1 = r67
            r12 = r68
            r15 = r72
            r10 = r28
            r11 = r33
            r28 = r35
            r23 = r37
            r14 = r65
            r66 = r27
            r27 = r26
            r26 = r66
            goto L_0x01a9
        L_0x0db7:
            r44 = r2
            r42 = r3
            r43 = r4
            r45 = r5
            r28 = r10
            r65 = r14
            r6 = r30
            r24 = 0
            int r1 = (r6 > r24 ? 1 : (r6 == r24 ? 0 : -1))
            r15 = r72
            r14 = r77
            if (r1 == 0) goto L_0x0dd2
            finishGroup(r15, r6, r14)
        L_0x0dd2:
            if (r78 == 0) goto L_0x0dd7
            r78.releasePermission()
        L_0x0dd7:
            if (r45 == 0) goto L_0x0e8b
            boolean r1 = r45.isEmpty()
            if (r1 != 0) goto L_0x0e8b
            r1 = 1
            long[] r13 = new long[r1]
            int r12 = r45.size()
            r11 = r0
            r0 = 0
        L_0x0de8:
            if (r0 >= r12) goto L_0x0e8b
            if (r70 == 0) goto L_0x0e02
            if (r28 != 0) goto L_0x0e02
            r10 = r65
            if (r10 <= r1) goto L_0x0e04
            int r1 = r11 % 10
            if (r1 != 0) goto L_0x0e04
            java.security.SecureRandom r1 = org.telegram.messenger.Utilities.random
            long r1 = r1.nextLong()
            r3 = 0
            r13[r3] = r1
            r1 = 1
            r11 = 0
            goto L_0x0e06
        L_0x0e02:
            r10 = r65
        L_0x0e04:
            r3 = 0
            r1 = 1
        L_0x0e06:
            int r11 = r11 + r1
            r5 = r45
            java.lang.Object r2 = r5.get(r0)
            java.lang.String r2 = (java.lang.String) r2
            r9 = r43
            java.lang.Object r4 = r9.get(r0)
            java.lang.String r4 = (java.lang.String) r4
            r8 = r42
            java.lang.Object r6 = r8.get(r0)
            android.net.Uri r6 = (android.net.Uri) r6
            r7 = r44
            java.lang.Object r16 = r7.get(r0)
            java.lang.CharSequence r16 = (java.lang.CharSequence) r16
            r15 = r29
            java.lang.Object r17 = r15.get(r0)
            java.util.ArrayList r17 = (java.util.ArrayList) r17
            r8 = 10
            if (r11 == r8) goto L_0x0e3b
            int r1 = r12 + -1
            if (r0 != r1) goto L_0x0e38
            goto L_0x0e3b
        L_0x0e38:
            r21 = 0
            goto L_0x0e3d
        L_0x0e3b:
            r21 = 1
        L_0x0e3d:
            r18 = 0
            r23 = r5
            r24 = 0
            r25 = 1
            r1 = r72
            r3 = r4
            r4 = r6
            r5 = r22
            r26 = r7
            r6 = r68
            r27 = r42
            r29 = 10
            r8 = r74
            r30 = r9
            r9 = r75
            r31 = r10
            r10 = r16
            r32 = r11
            r11 = r17
            r33 = r12
            r12 = r73
            r34 = r13
            r14 = r21
            r21 = r15
            r15 = r70
            r16 = r76
            r17 = r77
            prepareSendingDocumentInternal(r1, r2, r3, r4, r5, r6, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            int r0 = r0 + 1
            r15 = r72
            r14 = r77
            r29 = r21
            r45 = r23
            r44 = r26
            r43 = r30
            r65 = r31
            r11 = r32
            r12 = r33
            r1 = 1
            goto L_0x0de8
        L_0x0e8b:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0ea9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "total send time = "
            r0.append(r1)
            long r1 = java.lang.System.currentTimeMillis()
            long r1 = r1 - r19
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0ea9:
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
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str4);
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
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmapArr[0]), strArr[0]);
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
        String str2 = str;
        int[] iArr = new int[11];
        AnimatedFileDrawable.getVideoInfo(str2, iArr);
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
        long j = (long) iArr[6];
        long j2 = (long) iArr[5];
        int i2 = iArr[7];
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
        videoEditedInfo.originalPath = str2;
        videoEditedInfo.framerate = i2;
        videoEditedInfo.estimatedDuration = (long) Math.ceil((double) f);
        int i3 = iArr[1];
        videoEditedInfo.originalWidth = i3;
        videoEditedInfo.resultWidth = i3;
        int i4 = iArr[2];
        videoEditedInfo.originalHeight = i4;
        videoEditedInfo.resultHeight = i4;
        videoEditedInfo.rotationValue = iArr[8];
        videoEditedInfo.originalDuration = (long) (f * 1000.0f);
        float max = (float) Math.max(i3, i4);
        if (max <= 1280.0f) {
            i = max > 854.0f ? 3 : max > 640.0f ? 2 : 1;
        }
        int round = Math.round(((float) DownloadController.getInstance(UserConfig.selectedAccount).getMaxVideoBitrate()) / (100.0f / ((float) i))) - 1;
        if (round >= i) {
            round = i - 1;
        }
        int i5 = i - 1;
        if (round != i5) {
            float f2 = round != 1 ? round != 2 ? round != 3 ? 1280.0f : 848.0f : 640.0f : 432.0f;
            int i6 = videoEditedInfo.originalWidth;
            int i7 = videoEditedInfo.originalHeight;
            float f3 = f2 / (i6 > i7 ? (float) i6 : (float) i7);
            videoEditedInfo.resultWidth = Math.round((((float) i6) * f3) / 2.0f) * 2;
            videoEditedInfo.resultHeight = Math.round((((float) videoEditedInfo.originalHeight) * f3) / 2.0f) * 2;
        }
        int makeVideoBitrate = MediaController.makeVideoBitrate(videoEditedInfo.originalHeight, videoEditedInfo.originalWidth, videoBitrate, videoEditedInfo.resultHeight, videoEditedInfo.resultWidth);
        if (round == i5) {
            videoEditedInfo.resultWidth = videoEditedInfo.originalWidth;
            videoEditedInfo.resultHeight = videoEditedInfo.originalHeight;
            videoEditedInfo.bitrate = makeVideoBitrate;
            videoEditedInfo.estimatedSize = (long) ((int) (((float) j2) + (((f / 1000.0f) * ((float) makeVideoBitrate)) / 8.0f)));
        } else {
            videoEditedInfo.bitrate = makeVideoBitrate;
            long j3 = (long) ((int) (j2 + j));
            videoEditedInfo.estimatedSize = j3;
            videoEditedInfo.estimatedSize = j3 + ((j3 / 32768) * 16);
        }
        if (videoEditedInfo.estimatedSize == 0) {
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
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0301  */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x030d  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x033a  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x0342  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0348  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x034e  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0355  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0124  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$prepareSendingVideo$91(org.telegram.messenger.VideoEditedInfo r29, java.lang.String r30, long r31, int r33, org.telegram.messenger.AccountInstance r34, java.lang.CharSequence r35, org.telegram.messenger.MessageObject r36, org.telegram.messenger.MessageObject r37, org.telegram.messenger.MessageObject r38, java.util.ArrayList r39, boolean r40, int r41, boolean r42) {
        /*
            r6 = r30
            r10 = r31
            if (r29 == 0) goto L_0x0009
            r7 = r29
            goto L_0x000e
        L_0x0009:
            org.telegram.messenger.VideoEditedInfo r0 = createCompressionSettings(r30)
            r7 = r0
        L_0x000e:
            int r0 = (int) r10
            r8 = 0
            r9 = 1
            if (r0 != 0) goto L_0x0015
            r12 = 1
            goto L_0x0016
        L_0x0015:
            r12 = 0
        L_0x0016:
            if (r7 == 0) goto L_0x001e
            boolean r0 = r7.roundVideo
            if (r0 == 0) goto L_0x001e
            r13 = 1
            goto L_0x001f
        L_0x001e:
            r13 = 0
        L_0x001f:
            if (r7 != 0) goto L_0x004f
            java.lang.String r0 = "mp4"
            boolean r0 = r6.endsWith(r0)
            if (r0 != 0) goto L_0x004f
            if (r13 == 0) goto L_0x002c
            goto L_0x004f
        L_0x002c:
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
            goto L_0x037e
        L_0x004f:
            java.io.File r14 = new java.io.File
            r14.<init>(r6)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r6)
            long r1 = r14.length()
            r0.append(r1)
            java.lang.String r15 = "_"
            r0.append(r15)
            long r1 = r14.lastModified()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r16 = ""
            r1 = 0
            if (r7 == 0) goto L_0x00c9
            if (r13 != 0) goto L_0x00c2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            long r4 = r7.estimatedDuration
            r3.append(r4)
            r3.append(r15)
            long r4 = r7.startTime
            r3.append(r4)
            r3.append(r15)
            long r4 = r7.endTime
            r3.append(r4)
            boolean r0 = r7.muted
            if (r0 == 0) goto L_0x009f
            java.lang.String r0 = "_m"
            goto L_0x00a1
        L_0x009f:
            r0 = r16
        L_0x00a1:
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            int r3 = r7.resultWidth
            int r4 = r7.originalWidth
            if (r3 == r4) goto L_0x00c2
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r15)
            int r0 = r7.resultWidth
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x00c2:
            long r3 = r7.startTime
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 < 0) goto L_0x00c9
            r1 = r3
        L_0x00c9:
            r4 = r0
            r2 = r1
            r5 = 2
            if (r12 != 0) goto L_0x011b
            if (r33 != 0) goto L_0x011b
            if (r7 == 0) goto L_0x00e2
            org.telegram.messenger.MediaController$SavedFilterState r0 = r7.filterState
            if (r0 != 0) goto L_0x011b
            java.lang.String r0 = r7.paintPath
            if (r0 != 0) goto L_0x011b
            java.util.ArrayList<org.telegram.messenger.VideoEditedInfo$MediaEntity> r0 = r7.mediaEntities
            if (r0 != 0) goto L_0x011b
            org.telegram.messenger.MediaController$CropState r0 = r7.cropState
            if (r0 != 0) goto L_0x011b
        L_0x00e2:
            org.telegram.messenger.MessagesStorage r0 = r34.getMessagesStorage()
            if (r12 != 0) goto L_0x00ea
            r1 = 2
            goto L_0x00ed
        L_0x00ea:
            r17 = 5
            r1 = 5
        L_0x00ed:
            java.lang.Object[] r0 = r0.getSentFile(r4, r1)
            if (r0 == 0) goto L_0x011b
            r1 = r0[r8]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_document
            if (r1 == 0) goto L_0x011b
            r1 = r0[r8]
            r17 = r1
            org.telegram.tgnet.TLRPC$TL_document r17 = (org.telegram.tgnet.TLRPC$TL_document) r17
            r0 = r0[r9]
            r18 = r0
            java.lang.String r18 = (java.lang.String) r18
            r19 = 0
            r0 = r12
            r1 = r17
            r20 = r2
            r2 = r30
            r3 = r19
            r22 = r4
            r8 = 2
            r4 = r20
            ensureMediaThumbExists(r0, r1, r2, r3, r4)
            r5 = r18
            goto L_0x0122
        L_0x011b:
            r20 = r2
            r22 = r4
            r8 = 2
            r1 = 0
            r5 = 0
        L_0x0122:
            if (r1 != 0) goto L_0x0301
            r2 = r20
            android.graphics.Bitmap r0 = createVideoThumbnailAtTime(r6, r2)
            if (r0 != 0) goto L_0x0130
            android.graphics.Bitmap r0 = createVideoThumbnail(r6, r9)
        L_0x0130:
            r1 = r0
            r0 = 90
            if (r12 != 0) goto L_0x013b
            if (r33 == 0) goto L_0x0138
            goto L_0x013b
        L_0x0138:
            r2 = 320(0x140, float:4.48E-43)
            goto L_0x013d
        L_0x013b:
            r2 = 90
        L_0x013d:
            float r3 = (float) r2
            if (r2 <= r0) goto L_0x0143
            r2 = 80
            goto L_0x0145
        L_0x0143:
            r2 = 55
        L_0x0145:
            org.telegram.tgnet.TLRPC$PhotoSize r2 = org.telegram.messenger.ImageLoader.scaleAndSaveImage(r1, r3, r3, r2, r12)
            if (r1 == 0) goto L_0x024d
            if (r2 == 0) goto L_0x024d
            if (r13 == 0) goto L_0x024a
            r3 = 21
            if (r12 == 0) goto L_0x01ed
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createScaledBitmap(r1, r0, r0, r9)
            r24 = 7
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 >= r3) goto L_0x0160
            r25 = 0
            goto L_0x0162
        L_0x0160:
            r25 = 1
        L_0x0162:
            int r26 = r1.getWidth()
            int r27 = r1.getHeight()
            int r28 = r1.getRowBytes()
            r23 = r1
            org.telegram.messenger.Utilities.blurBitmap(r23, r24, r25, r26, r27, r28)
            r24 = 7
            if (r4 >= r3) goto L_0x017a
            r25 = 0
            goto L_0x017c
        L_0x017a:
            r25 = 1
        L_0x017c:
            int r26 = r1.getWidth()
            int r27 = r1.getHeight()
            int r28 = r1.getRowBytes()
            r23 = r1
            org.telegram.messenger.Utilities.blurBitmap(r23, r24, r25, r26, r27, r28)
            r24 = 7
            if (r4 >= r3) goto L_0x0194
            r25 = 0
            goto L_0x0196
        L_0x0194:
            r25 = 1
        L_0x0196:
            int r26 = r1.getWidth()
            int r27 = r1.getHeight()
            int r28 = r1.getRowBytes()
            r23 = r1
            org.telegram.messenger.Utilities.blurBitmap(r23, r24, r25, r26, r27, r28)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r4 = r2.location
            r17 = r1
            long r0 = r4.volume_id
            r3.append(r0)
            r3.append(r15)
            org.telegram.tgnet.TLRPC$FileLocation r0 = r2.location
            int r0 = r0.local_id
            r3.append(r0)
            java.lang.String r0 = "@%d_%d_b2"
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            java.lang.Object[] r1 = new java.lang.Object[r8]
            int r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r3 = (float) r3
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r4
            int r3 = (int) r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r4 = 0
            r1[r4] = r3
            int r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r3 = (float) r3
            float r4 = org.telegram.messenger.AndroidUtilities.density
            float r3 = r3 / r4
            int r3 = (int) r3
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r1[r9] = r3
            java.lang.String r1 = java.lang.String.format(r0, r1)
            r0 = r1
            r1 = r17
            goto L_0x024e
        L_0x01ed:
            r24 = 3
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 >= r3) goto L_0x01f6
            r25 = 0
            goto L_0x01f8
        L_0x01f6:
            r25 = 1
        L_0x01f8:
            int r26 = r1.getWidth()
            int r27 = r1.getHeight()
            int r28 = r1.getRowBytes()
            r23 = r1
            org.telegram.messenger.Utilities.blurBitmap(r23, r24, r25, r26, r27, r28)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            long r3 = r3.volume_id
            r0.append(r3)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$FileLocation r3 = r2.location
            int r3 = r3.local_id
            r0.append(r3)
            java.lang.String r3 = "@%d_%d_b"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r3 = new java.lang.Object[r8]
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r4 = (float) r4
            float r8 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r8
            int r4 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r8 = 0
            r3[r8] = r4
            int r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            float r4 = (float) r4
            float r8 = org.telegram.messenger.AndroidUtilities.density
            float r4 = r4 / r8
            int r4 = (int) r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3[r9] = r4
            java.lang.String r0 = java.lang.String.format(r0, r3)
            goto L_0x024e
        L_0x024a:
            r0 = 0
            r1 = 0
            goto L_0x024e
        L_0x024d:
            r0 = 0
        L_0x024e:
            org.telegram.tgnet.TLRPC$TL_document r3 = new org.telegram.tgnet.TLRPC$TL_document
            r3.<init>()
            if (r2 == 0) goto L_0x025f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r3.thumbs
            r4.add(r2)
            int r2 = r3.flags
            r2 = r2 | r9
            r3.flags = r2
        L_0x025f:
            r2 = 0
            byte[] r4 = new byte[r2]
            r3.file_reference = r4
            java.lang.String r4 = "video/mp4"
            r3.mime_type = r4
            org.telegram.messenger.UserConfig r4 = r34.getUserConfig()
            r4.saveConfig(r2)
            if (r12 == 0) goto L_0x028b
            r2 = 32
            long r8 = r10 >> r2
            int r2 = (int) r8
            org.telegram.messenger.MessagesController r4 = r34.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r4.getEncryptedChat(r2)
            if (r2 != 0) goto L_0x0285
            return
        L_0x0285:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r2.<init>()
            goto L_0x0292
        L_0x028b:
            org.telegram.tgnet.TLRPC$TL_documentAttributeVideo r2 = new org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            r2.<init>()
            r2.supports_streaming = r9
        L_0x0292:
            r2.round_message = r13
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            r4.add(r2)
            if (r7 == 0) goto L_0x02ed
            boolean r4 = r7.needConvert()
            if (r4 == 0) goto L_0x02ed
            boolean r4 = r7.muted
            if (r4 == 0) goto L_0x02bb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r3.attributes
            org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated r8 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated
            r8.<init>()
            r4.add(r8)
            fillVideoAttribute(r6, r2, r7)
            int r4 = r2.w
            r7.originalWidth = r4
            int r4 = r2.h
            r7.originalHeight = r4
            goto L_0x02c3
        L_0x02bb:
            long r8 = r7.estimatedDuration
            r12 = 1000(0x3e8, double:4.94E-321)
            long r8 = r8 / r12
            int r4 = (int) r8
            r2.duration = r4
        L_0x02c3:
            int r4 = r7.rotationValue
            org.telegram.messenger.MediaController$CropState r8 = r7.cropState
            if (r8 == 0) goto L_0x02d1
            int r9 = r8.transformWidth
            int r12 = r8.transformHeight
            int r8 = r8.transformRotation
            int r4 = r4 + r8
            goto L_0x02d5
        L_0x02d1:
            int r9 = r7.resultWidth
            int r12 = r7.resultHeight
        L_0x02d5:
            r8 = 90
            if (r4 == r8) goto L_0x02e3
            r8 = 270(0x10e, float:3.78E-43)
            if (r4 != r8) goto L_0x02de
            goto L_0x02e3
        L_0x02de:
            r2.w = r9
            r2.h = r12
            goto L_0x02e7
        L_0x02e3:
            r2.w = r12
            r2.h = r9
        L_0x02e7:
            long r8 = r7.estimatedSize
            int r2 = (int) r8
            r3.size = r2
            goto L_0x02fe
        L_0x02ed:
            boolean r4 = r14.exists()
            if (r4 == 0) goto L_0x02fa
            long r8 = r14.length()
            int r4 = (int) r8
            r3.size = r4
        L_0x02fa:
            r4 = 0
            fillVideoAttribute(r6, r2, r4)
        L_0x02fe:
            r2 = r0
            r8 = r3
            goto L_0x0305
        L_0x0301:
            r4 = 0
            r8 = r1
            r1 = r4
            r2 = r1
        L_0x0305:
            if (r7 == 0) goto L_0x033a
            boolean r0 = r7.needConvert()
            if (r0 == 0) goto L_0x033a
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
            goto L_0x033b
        L_0x033a:
            r9 = r6
        L_0x033b:
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
            if (r35 == 0) goto L_0x0348
            java.lang.String r0 = r35.toString()
            r14 = r0
            goto L_0x034a
        L_0x0348:
            r14 = r16
        L_0x034a:
            r0 = r22
            if (r0 == 0) goto L_0x0353
            java.lang.String r3 = "originalPath"
            r12.put(r3, r0)
        L_0x0353:
            if (r5 == 0) goto L_0x035a
            java.lang.String r0 = "parentObject"
            r12.put(r0, r5)
        L_0x035a:
            org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda4 r19 = new org.telegram.messenger.SendMessagesHelper$$ExternalSyntheticLambda4
            r0 = r19
            r3 = r36
            r4 = r34
            r18 = r5
            r5 = r7
            r6 = r8
            r7 = r9
            r8 = r12
            r9 = r18
            r10 = r31
            r12 = r37
            r13 = r38
            r15 = r39
            r16 = r40
            r17 = r41
            r18 = r33
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13, r14, r15, r16, r17, r18)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r19)
        L_0x037e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SendMessagesHelper.lambda$prepareSendingVideo$91(org.telegram.messenger.VideoEditedInfo, java.lang.String, long, int, org.telegram.messenger.AccountInstance, java.lang.CharSequence, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, int, boolean):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$prepareSendingVideo$90(Bitmap bitmap, String str, MessageObject messageObject, AccountInstance accountInstance, VideoEditedInfo videoEditedInfo, TLRPC$TL_document tLRPC$TL_document, String str2, HashMap hashMap, String str3, long j, MessageObject messageObject2, MessageObject messageObject3, String str4, ArrayList arrayList, boolean z, int i, int i2) {
        Bitmap bitmap2 = bitmap;
        String str5 = str;
        if (!(bitmap2 == null || str5 == null)) {
            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(bitmap2), str5);
        }
        if (messageObject != null) {
            accountInstance.getSendMessagesHelper().editMessage(messageObject, (TLRPC$TL_photo) null, videoEditedInfo, tLRPC$TL_document, str2, hashMap, false, str3);
        } else {
            accountInstance.getSendMessagesHelper().sendMessage(tLRPC$TL_document, videoEditedInfo, str2, j, messageObject2, messageObject3, str4, arrayList, (TLRPC$ReplyMarkup) null, hashMap, z, i, i2, str3, (MessageObject.SendAnimationData) null);
        }
    }
}
