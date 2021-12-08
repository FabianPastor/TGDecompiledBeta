package org.telegram.messenger;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputFileLocation;
import org.telegram.tgnet.TLRPC$InputMedia;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$InputStickeredMedia;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$Page;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_account_getTheme;
import org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
import org.telegram.tgnet.TLRPC$TL_account_getWallPapers;
import org.telegram.tgnet.TLRPC$TL_channel;
import org.telegram.tgnet.TLRPC$TL_channels_getChannels;
import org.telegram.tgnet.TLRPC$TL_channels_getMessages;
import org.telegram.tgnet.TLRPC$TL_chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_help_getAppUpdate;
import org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputMediaDocument;
import org.telegram.tgnet.TLRPC$TL_inputMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputSingleMedia;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument;
import org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_inputTheme;
import org.telegram.tgnet.TLRPC$TL_inputWallPaper;
import org.telegram.tgnet.TLRPC$TL_messages_editMessage;
import org.telegram.tgnet.TLRPC$TL_messages_faveSticker;
import org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getChats;
import org.telegram.tgnet.TLRPC$TL_messages_getFavedStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getRecentStickers;
import org.telegram.tgnet.TLRPC$TL_messages_getSavedGifs;
import org.telegram.tgnet.TLRPC$TL_messages_getScheduledMessages;
import org.telegram.tgnet.TLRPC$TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_getWebPage;
import org.telegram.tgnet.TLRPC$TL_messages_saveGif;
import org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_messages_sendMedia;
import org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_users_getUsers;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$TL_webPageAttributeTheme;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$WebPage;

public class FileRefController extends BaseController {
    private static volatile FileRefController[] Instance = new FileRefController[3];
    private ArrayList<Waiter> favStickersWaiter = new ArrayList<>();
    private long lastCleanupTime = SystemClock.elapsedRealtime();
    private HashMap<String, ArrayList<Requester>> locationRequester = new HashMap<>();
    private HashMap<TLRPC$TL_messages_sendMultiMedia, Object[]> multiMediaCache = new HashMap<>();
    private HashMap<String, ArrayList<Requester>> parentRequester = new HashMap<>();
    private ArrayList<Waiter> recentStickersWaiter = new ArrayList<>();
    private HashMap<String, CachedResult> responseCache = new HashMap<>();
    private ArrayList<Waiter> savedGifsWaiters = new ArrayList<>();
    private ArrayList<Waiter> wallpaperWaiters = new ArrayList<>();

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onUpdateObjectReference$24(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onUpdateObjectReference$25(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$onUpdateObjectReference$26(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    private static class Requester {
        /* access modifiers changed from: private */
        public Object[] args;
        /* access modifiers changed from: private */
        public boolean completed;
        /* access modifiers changed from: private */
        public TLRPC$InputFileLocation location;
        /* access modifiers changed from: private */
        public String locationKey;

        private Requester() {
        }
    }

    private static class CachedResult {
        /* access modifiers changed from: private */
        public long firstQueryTime;
        /* access modifiers changed from: private */
        public long lastQueryTime;
        /* access modifiers changed from: private */
        public TLObject response;

        private CachedResult() {
        }
    }

    private static class Waiter {
        /* access modifiers changed from: private */
        public String locationKey;
        /* access modifiers changed from: private */
        public String parentKey;

        public Waiter(String str, String str2) {
            this.locationKey = str;
            this.parentKey = str2;
        }
    }

    public static FileRefController getInstance(int i) {
        FileRefController fileRefController = Instance[i];
        if (fileRefController == null) {
            synchronized (FileRefController.class) {
                fileRefController = Instance[i];
                if (fileRefController == null) {
                    FileRefController[] fileRefControllerArr = Instance;
                    FileRefController fileRefController2 = new FileRefController(i);
                    fileRefControllerArr[i] = fileRefController2;
                    fileRefController = fileRefController2;
                }
            }
        }
        return fileRefController;
    }

    public FileRefController(int i) {
        super(i);
    }

    public static String getKeyForParentObject(Object obj) {
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            long channelId = messageObject.getChannelId();
            return "message" + messageObject.getRealId() + "_" + channelId + "_" + messageObject.scheduled;
        } else if (obj instanceof TLRPC$Message) {
            TLRPC$Message tLRPC$Message = (TLRPC$Message) obj;
            TLRPC$Peer tLRPC$Peer = tLRPC$Message.peer_id;
            long j = tLRPC$Peer != null ? tLRPC$Peer.channel_id : 0;
            return "message" + tLRPC$Message.id + "_" + j + "_" + tLRPC$Message.from_scheduled;
        } else if (obj instanceof TLRPC$WebPage) {
            return "webpage" + ((TLRPC$WebPage) obj).id;
        } else if (obj instanceof TLRPC$User) {
            return "user" + ((TLRPC$User) obj).id;
        } else if (obj instanceof TLRPC$Chat) {
            return "chat" + ((TLRPC$Chat) obj).id;
        } else if (obj instanceof String) {
            return "str" + ((String) obj);
        } else if (obj instanceof TLRPC$TL_messages_stickerSet) {
            return "set" + ((TLRPC$TL_messages_stickerSet) obj).set.id;
        } else if (obj instanceof TLRPC$StickerSetCovered) {
            return "set" + ((TLRPC$StickerSetCovered) obj).set.id;
        } else if (obj instanceof TLRPC$InputStickerSet) {
            return "set" + ((TLRPC$InputStickerSet) obj).id;
        } else if (obj instanceof TLRPC$TL_wallPaper) {
            return "wallpaper" + ((TLRPC$TL_wallPaper) obj).id;
        } else if (obj instanceof TLRPC$TL_theme) {
            return "theme" + ((TLRPC$TL_theme) obj).id;
        } else if (obj == null) {
            return null;
        } else {
            return "" + obj;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:108:0x0366, code lost:
        if (r2.equals(r1) != false) goto L_0x036a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0373  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0389  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestReference(java.lang.Object r13, java.lang.Object... r14) {
        /*
            r12 = this;
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r1 = 0
            if (r0 == 0) goto L_0x0023
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "start loading request reference for parent = "
            r0.append(r2)
            r0.append(r13)
            java.lang.String r2 = " args = "
            r0.append(r2)
            r2 = r14[r1]
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0023:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputSingleMedia
            r2 = 2
            r3 = 1
            java.lang.String r4 = "photo_"
            java.lang.String r5 = "file_"
            if (r0 == 0) goto L_0x0085
            r0 = r14[r1]
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r0 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r0
            org.telegram.tgnet.TLRPC$InputMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaDocument
            if (r6 == 0) goto L_0x005b
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaDocument) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x005b:
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaPhoto
            if (r5 == 0) goto L_0x0081
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaPhoto) r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r0.id
            long r6 = r4.id
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x0081:
            r12.sendErrorToObject(r14, r1)
            return
        L_0x0085:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia
            if (r0 == 0) goto L_0x00bb
            r0 = r14[r1]
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r0 = (org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia) r0
            java.util.ArrayList r13 = (java.util.ArrayList) r13
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia, java.lang.Object[]> r4 = r12.multiMediaCache
            r4.put(r0, r14)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r14 = r0.multi_media
            int r14 = r14.size()
            r4 = 0
        L_0x009d:
            if (r4 >= r14) goto L_0x00ba
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r5 = r0.multi_media
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r5 = (org.telegram.tgnet.TLRPC$TL_inputSingleMedia) r5
            java.lang.Object r6 = r13.get(r4)
            if (r6 != 0) goto L_0x00ae
            goto L_0x00b7
        L_0x00ae:
            java.lang.Object[] r7 = new java.lang.Object[r2]
            r7[r1] = r5
            r7[r3] = r0
            r12.requestReference(r6, r7)
        L_0x00b7:
            int r4 = r4 + 1
            goto L_0x009d
        L_0x00ba:
            return
        L_0x00bb:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_sendMedia
            if (r0 == 0) goto L_0x0117
            r0 = r14[r1]
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r0 = (org.telegram.tgnet.TLRPC$TL_messages_sendMedia) r0
            org.telegram.tgnet.TLRPC$InputMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaDocument
            if (r6 == 0) goto L_0x00ed
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaDocument) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x00ed:
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaPhoto
            if (r5 == 0) goto L_0x0113
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaPhoto) r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r0.id
            long r6 = r4.id
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x0113:
            r12.sendErrorToObject(r14, r1)
            return
        L_0x0117:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_editMessage
            if (r0 == 0) goto L_0x0173
            r0 = r14[r1]
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r0 = (org.telegram.tgnet.TLRPC$TL_messages_editMessage) r0
            org.telegram.tgnet.TLRPC$InputMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaDocument
            if (r6 == 0) goto L_0x0149
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaDocument) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x0149:
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputMediaPhoto
            if (r5 == 0) goto L_0x016f
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r0 = (org.telegram.tgnet.TLRPC$TL_inputMediaPhoto) r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r0.id
            long r6 = r4.id
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x016f:
            r12.sendErrorToObject(r14, r1)
            return
        L_0x0173:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_saveGif
            if (r0 == 0) goto L_0x019d
            r0 = r14[r1]
            org.telegram.tgnet.TLRPC$TL_messages_saveGif r0 = (org.telegram.tgnet.TLRPC$TL_messages_saveGif) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x019d:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker
            if (r0 == 0) goto L_0x01c7
            r0 = r14[r1]
            org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker r0 = (org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x01c7:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_faveSticker
            if (r0 == 0) goto L_0x01f1
            r0 = r14[r1]
            org.telegram.tgnet.TLRPC$TL_messages_faveSticker r0 = (org.telegram.tgnet.TLRPC$TL_messages_faveSticker) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x01f1:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers
            if (r0 == 0) goto L_0x024d
            r0 = r14[r1]
            org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers r0 = (org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers) r0
            org.telegram.tgnet.TLRPC$InputStickeredMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument
            if (r6 == 0) goto L_0x0223
            org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument r0 = (org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r0.id
            long r5 = r5.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x0223:
            boolean r5 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto
            if (r5 == 0) goto L_0x0249
            org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto r0 = (org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto) r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r0.id
            long r6 = r4.id
            r5.append(r6)
            java.lang.String r4 = r5.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r0 = r0.id
            long r6 = r0.id
            r5.id = r6
            goto L_0x02ce
        L_0x0249:
            r12.sendErrorToObject(r14, r1)
            return
        L_0x024d:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputFileLocation
            if (r0 == 0) goto L_0x0276
            r0 = r14[r1]
            r5 = r0
            org.telegram.tgnet.TLRPC$TL_inputFileLocation r5 = (org.telegram.tgnet.TLRPC$TL_inputFileLocation) r5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "loc_"
            r0.append(r4)
            int r4 = r5.local_id
            r0.append(r4)
            java.lang.String r4 = "_"
            r0.append(r4)
            long r6 = r5.volume_id
            r0.append(r6)
            java.lang.String r4 = r0.toString()
            goto L_0x02ce
        L_0x0276:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            if (r0 == 0) goto L_0x0293
            r0 = r14[r1]
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r0 = (org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            long r5 = r0.id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r5 = r0
            goto L_0x02ce
        L_0x0293:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            if (r0 == 0) goto L_0x02b0
            r0 = r14[r1]
            r5 = r0
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r5 = (org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation) r5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r4)
            long r6 = r5.id
            r0.append(r6)
            java.lang.String r4 = r0.toString()
            goto L_0x02ce
        L_0x02b0:
            r0 = r14[r1]
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation
            if (r0 == 0) goto L_0x03a9
            r0 = r14[r1]
            r5 = r0
            org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r5 = (org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation) r5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "avatar_"
            r0.append(r4)
            long r6 = r5.id
            r0.append(r6)
            java.lang.String r4 = r0.toString()
        L_0x02ce:
            boolean r0 = r13 instanceof org.telegram.messenger.MessageObject
            if (r0 == 0) goto L_0x02e4
            r0 = r13
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r6 = r0.getRealId()
            if (r6 >= 0) goto L_0x02e4
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x02e4
            r13 = r0
        L_0x02e4:
            java.lang.String r0 = getKeyForParentObject(r13)
            if (r0 != 0) goto L_0x02ee
            r12.sendErrorToObject(r14, r1)
            return
        L_0x02ee:
            org.telegram.messenger.FileRefController$Requester r6 = new org.telegram.messenger.FileRefController$Requester
            r7 = 0
            r6.<init>()
            java.lang.Object[] unused = r6.args = r14
            org.telegram.tgnet.TLRPC$InputFileLocation unused = r6.location = r5
            java.lang.String unused = r6.locationKey = r4
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r5 = r12.locationRequester
            java.lang.Object r5 = r5.get(r4)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x0312
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r1 = r12.locationRequester
            r1.put(r4, r5)
            r1 = 1
        L_0x0312:
            r5.add(r6)
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r3 = r12.parentRequester
            java.lang.Object r3 = r3.get(r0)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            if (r3 != 0) goto L_0x032b
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r5 = r12.parentRequester
            r5.put(r0, r3)
            int r1 = r1 + 1
        L_0x032b:
            r3.add(r6)
            if (r1 == r2) goto L_0x0331
            return
        L_0x0331:
            boolean r1 = r13 instanceof java.lang.String
            java.lang.String r2 = "update"
            java.lang.String r3 = "fav"
            java.lang.String r5 = "recent"
            java.lang.String r6 = "gif"
            java.lang.String r7 = "wallpaper"
            if (r1 == 0) goto L_0x0369
            r1 = r13
            java.lang.String r1 = (java.lang.String) r1
            boolean r8 = r7.equals(r1)
            if (r8 == 0) goto L_0x034a
            r2 = r7
            goto L_0x036a
        L_0x034a:
            boolean r7 = r1.startsWith(r6)
            if (r7 == 0) goto L_0x0352
            r2 = r6
            goto L_0x036a
        L_0x0352:
            boolean r6 = r5.equals(r1)
            if (r6 == 0) goto L_0x035a
            r2 = r5
            goto L_0x036a
        L_0x035a:
            boolean r5 = r3.equals(r1)
            if (r5 == 0) goto L_0x0362
            r2 = r3
            goto L_0x036a
        L_0x0362:
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L_0x0369
            goto L_0x036a
        L_0x0369:
            r2 = r4
        L_0x036a:
            r12.cleanupCache()
            org.telegram.messenger.FileRefController$CachedResult r1 = r12.getCachedResponse(r2)
            if (r1 == 0) goto L_0x0389
            org.telegram.tgnet.TLObject r9 = r1.response
            r10 = 0
            r11 = 1
            r6 = r12
            r7 = r4
            r8 = r0
            boolean r1 = r6.onRequestComplete(r7, r8, r9, r10, r11)
            if (r1 != 0) goto L_0x0388
            java.util.HashMap<java.lang.String, org.telegram.messenger.FileRefController$CachedResult> r1 = r12.responseCache
            r1.remove(r4)
            goto L_0x03a5
        L_0x0388:
            return
        L_0x0389:
            org.telegram.messenger.FileRefController$CachedResult r1 = r12.getCachedResponse(r0)
            if (r1 == 0) goto L_0x03a5
            org.telegram.tgnet.TLObject r9 = r1.response
            r10 = 0
            r11 = 1
            r6 = r12
            r7 = r4
            r8 = r0
            boolean r1 = r6.onRequestComplete(r7, r8, r9, r10, r11)
            if (r1 != 0) goto L_0x03a4
            java.util.HashMap<java.lang.String, org.telegram.messenger.FileRefController$CachedResult> r1 = r12.responseCache
            r1.remove(r0)
            goto L_0x03a5
        L_0x03a4:
            return
        L_0x03a5:
            r12.requestReferenceFromServer(r13, r4, r0, r14)
            return
        L_0x03a9:
            r12.sendErrorToObject(r14, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.requestReference(java.lang.Object, java.lang.Object[]):void");
    }

    private void broadcastWaitersData(ArrayList<Waiter> arrayList, TLObject tLObject) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Waiter waiter = arrayList.get(i);
            onRequestComplete(waiter.locationKey, waiter.parentKey, tLObject, i == size + -1, false);
            i++;
        }
        arrayList.clear();
    }

    private void requestReferenceFromServer(Object obj, String str, String str2, Object[] objArr) {
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            long channelId = messageObject.getChannelId();
            if (messageObject.scheduled) {
                TLRPC$TL_messages_getScheduledMessages tLRPC$TL_messages_getScheduledMessages = new TLRPC$TL_messages_getScheduledMessages();
                tLRPC$TL_messages_getScheduledMessages.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
                tLRPC$TL_messages_getScheduledMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getScheduledMessages, new FileRefController$$ExternalSyntheticLambda22(this, str, str2));
            } else if (channelId != 0) {
                TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
                tLRPC$TL_channels_getMessages.channel = getMessagesController().getInputChannel(channelId);
                tLRPC$TL_channels_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tLRPC$TL_channels_getMessages, new FileRefController$$ExternalSyntheticLambda16(this, str, str2));
            } else {
                TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages = new TLRPC$TL_messages_getMessages();
                tLRPC$TL_messages_getMessages.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages, new FileRefController$$ExternalSyntheticLambda17(this, str, str2));
            }
        } else if (obj instanceof TLRPC$TL_wallPaper) {
            TLRPC$TL_wallPaper tLRPC$TL_wallPaper = (TLRPC$TL_wallPaper) obj;
            TLRPC$TL_account_getWallPaper tLRPC$TL_account_getWallPaper = new TLRPC$TL_account_getWallPaper();
            TLRPC$TL_inputWallPaper tLRPC$TL_inputWallPaper = new TLRPC$TL_inputWallPaper();
            tLRPC$TL_inputWallPaper.id = tLRPC$TL_wallPaper.id;
            tLRPC$TL_inputWallPaper.access_hash = tLRPC$TL_wallPaper.access_hash;
            tLRPC$TL_account_getWallPaper.wallpaper = tLRPC$TL_inputWallPaper;
            getConnectionsManager().sendRequest(tLRPC$TL_account_getWallPaper, new FileRefController$$ExternalSyntheticLambda27(this, str, str2));
        } else if (obj instanceof TLRPC$TL_theme) {
            TLRPC$TL_theme tLRPC$TL_theme = (TLRPC$TL_theme) obj;
            TLRPC$TL_account_getTheme tLRPC$TL_account_getTheme = new TLRPC$TL_account_getTheme();
            TLRPC$TL_inputTheme tLRPC$TL_inputTheme = new TLRPC$TL_inputTheme();
            tLRPC$TL_inputTheme.id = tLRPC$TL_theme.id;
            tLRPC$TL_inputTheme.access_hash = tLRPC$TL_theme.access_hash;
            tLRPC$TL_account_getTheme.theme = tLRPC$TL_inputTheme;
            tLRPC$TL_account_getTheme.format = "android";
            getConnectionsManager().sendRequest(tLRPC$TL_account_getTheme, new FileRefController$$ExternalSyntheticLambda30(this, str, str2));
        } else if (obj instanceof TLRPC$WebPage) {
            TLRPC$TL_messages_getWebPage tLRPC$TL_messages_getWebPage = new TLRPC$TL_messages_getWebPage();
            tLRPC$TL_messages_getWebPage.url = ((TLRPC$WebPage) obj).url;
            tLRPC$TL_messages_getWebPage.hash = 0;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getWebPage, new FileRefController$$ExternalSyntheticLambda29(this, str, str2));
        } else if (obj instanceof TLRPC$User) {
            TLRPC$TL_users_getUsers tLRPC$TL_users_getUsers = new TLRPC$TL_users_getUsers();
            tLRPC$TL_users_getUsers.id.add(getMessagesController().getInputUser((TLRPC$User) obj));
            getConnectionsManager().sendRequest(tLRPC$TL_users_getUsers, new FileRefController$$ExternalSyntheticLambda28(this, str, str2));
        } else if (obj instanceof TLRPC$Chat) {
            TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) obj;
            if (tLRPC$Chat instanceof TLRPC$TL_chat) {
                TLRPC$TL_messages_getChats tLRPC$TL_messages_getChats = new TLRPC$TL_messages_getChats();
                tLRPC$TL_messages_getChats.id.add(Long.valueOf(tLRPC$Chat.id));
                getConnectionsManager().sendRequest(tLRPC$TL_messages_getChats, new FileRefController$$ExternalSyntheticLambda21(this, str, str2));
            } else if (tLRPC$Chat instanceof TLRPC$TL_channel) {
                TLRPC$TL_channels_getChannels tLRPC$TL_channels_getChannels = new TLRPC$TL_channels_getChannels();
                tLRPC$TL_channels_getChannels.id.add(MessagesController.getInputChannel(tLRPC$Chat));
                getConnectionsManager().sendRequest(tLRPC$TL_channels_getChannels, new FileRefController$$ExternalSyntheticLambda15(this, str, str2));
            }
        } else if (obj instanceof String) {
            String str3 = (String) obj;
            if ("wallpaper".equals(str3)) {
                if (this.wallpaperWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC$TL_account_getWallPapers(), new FileRefController$$ExternalSyntheticLambda10(this));
                }
                this.wallpaperWaiters.add(new Waiter(str, str2));
            } else if (str3.startsWith("gif")) {
                if (this.savedGifsWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC$TL_messages_getSavedGifs(), new FileRefController$$ExternalSyntheticLambda11(this));
                }
                this.savedGifsWaiters.add(new Waiter(str, str2));
            } else if ("recent".equals(str3)) {
                if (this.recentStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC$TL_messages_getRecentStickers(), new FileRefController$$ExternalSyntheticLambda12(this));
                }
                this.recentStickersWaiter.add(new Waiter(str, str2));
            } else if ("fav".equals(str3)) {
                if (this.favStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC$TL_messages_getFavedStickers(), new FileRefController$$ExternalSyntheticLambda13(this));
                }
                this.favStickersWaiter.add(new Waiter(str, str2));
            } else if ("update".equals(str3)) {
                TLRPC$TL_help_getAppUpdate tLRPC$TL_help_getAppUpdate = new TLRPC$TL_help_getAppUpdate();
                try {
                    tLRPC$TL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
                } catch (Exception unused) {
                }
                if (tLRPC$TL_help_getAppUpdate.source == null) {
                    tLRPC$TL_help_getAppUpdate.source = "";
                }
                getConnectionsManager().sendRequest(tLRPC$TL_help_getAppUpdate, new FileRefController$$ExternalSyntheticLambda23(this, str, str2));
            } else if (str3.startsWith("avatar_")) {
                long longValue = Utilities.parseLong(str3).longValue();
                if (longValue > 0) {
                    TLRPC$TL_photos_getUserPhotos tLRPC$TL_photos_getUserPhotos = new TLRPC$TL_photos_getUserPhotos();
                    tLRPC$TL_photos_getUserPhotos.limit = 80;
                    tLRPC$TL_photos_getUserPhotos.offset = 0;
                    tLRPC$TL_photos_getUserPhotos.max_id = 0;
                    tLRPC$TL_photos_getUserPhotos.user_id = getMessagesController().getInputUser(longValue);
                    getConnectionsManager().sendRequest(tLRPC$TL_photos_getUserPhotos, new FileRefController$$ExternalSyntheticLambda20(this, str, str2));
                    return;
                }
                TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
                tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterChatPhotos();
                tLRPC$TL_messages_search.limit = 80;
                tLRPC$TL_messages_search.offset_id = 0;
                tLRPC$TL_messages_search.q = "";
                tLRPC$TL_messages_search.peer = getMessagesController().getInputPeer(longValue);
                getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new FileRefController$$ExternalSyntheticLambda25(this, str, str2));
            } else if (str3.startsWith("sent_")) {
                String[] split = str3.split("_");
                if (split.length == 3) {
                    long longValue2 = Utilities.parseLong(split[1]).longValue();
                    if (longValue2 != 0) {
                        TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages2 = new TLRPC$TL_channels_getMessages();
                        tLRPC$TL_channels_getMessages2.channel = getMessagesController().getInputChannel(longValue2);
                        tLRPC$TL_channels_getMessages2.id.add(Utilities.parseInt(split[2]));
                        getConnectionsManager().sendRequest(tLRPC$TL_channels_getMessages2, new FileRefController$$ExternalSyntheticLambda14(this, str, str2));
                        return;
                    }
                    TLRPC$TL_messages_getMessages tLRPC$TL_messages_getMessages2 = new TLRPC$TL_messages_getMessages();
                    tLRPC$TL_messages_getMessages2.id.add(Utilities.parseInt(split[2]));
                    getConnectionsManager().sendRequest(tLRPC$TL_messages_getMessages2, new FileRefController$$ExternalSyntheticLambda24(this, str, str2));
                    return;
                }
                sendErrorToObject(objArr, 0);
            } else {
                sendErrorToObject(objArr, 0);
            }
        } else if (obj instanceof TLRPC$TL_messages_stickerSet) {
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet = new TLRPC$TL_messages_getStickerSet();
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_messages_getStickerSet.stickerset = tLRPC$TL_inputStickerSetID;
            TLRPC$StickerSet tLRPC$StickerSet = ((TLRPC$TL_messages_stickerSet) obj).set;
            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet, new FileRefController$$ExternalSyntheticLambda18(this, str, str2));
        } else if (obj instanceof TLRPC$StickerSetCovered) {
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet2 = new TLRPC$TL_messages_getStickerSet();
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID2 = new TLRPC$TL_inputStickerSetID();
            tLRPC$TL_messages_getStickerSet2.stickerset = tLRPC$TL_inputStickerSetID2;
            TLRPC$StickerSet tLRPC$StickerSet2 = ((TLRPC$StickerSetCovered) obj).set;
            tLRPC$TL_inputStickerSetID2.id = tLRPC$StickerSet2.id;
            tLRPC$TL_inputStickerSetID2.access_hash = tLRPC$StickerSet2.access_hash;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet2, new FileRefController$$ExternalSyntheticLambda19(this, str, str2));
        } else if (obj instanceof TLRPC$InputStickerSet) {
            TLRPC$TL_messages_getStickerSet tLRPC$TL_messages_getStickerSet3 = new TLRPC$TL_messages_getStickerSet();
            tLRPC$TL_messages_getStickerSet3.stickerset = (TLRPC$InputStickerSet) obj;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getStickerSet3, new FileRefController$$ExternalSyntheticLambda26(this, str, str2));
        } else {
            sendErrorToObject(objArr, 0);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$0(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$1(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$2(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$3(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$4(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$5(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$6(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$7(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$8(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        broadcastWaitersData(this.wallpaperWaiters, tLObject);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        broadcastWaitersData(this.savedGifsWaiters, tLObject);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$11(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        broadcastWaitersData(this.recentStickersWaiter, tLObject);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$12(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        broadcastWaitersData(this.favStickersWaiter, tLObject);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$13(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$14(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$15(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$16(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$17(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, false, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$18(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$19(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestReferenceFromServer$20(String str, String str2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        onRequestComplete(str, str2, tLObject, true, false);
    }

    private boolean isSameReference(byte[] bArr, byte[] bArr2) {
        return Arrays.equals(bArr, bArr2);
    }

    private boolean onUpdateObjectReference(Requester requester, byte[] bArr, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean z) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("fileref updated for " + requester.args[0] + " " + requester.locationKey);
        }
        if (requester.args[0] instanceof TLRPC$TL_inputSingleMedia) {
            TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = (TLRPC$TL_messages_sendMultiMedia) requester.args[1];
            Object[] objArr = this.multiMediaCache.get(tLRPC$TL_messages_sendMultiMedia);
            if (objArr == null) {
                return true;
            }
            TLRPC$TL_inputSingleMedia tLRPC$TL_inputSingleMedia = (TLRPC$TL_inputSingleMedia) requester.args[0];
            TLRPC$InputMedia tLRPC$InputMedia = tLRPC$TL_inputSingleMedia.media;
            if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaDocument) {
                TLRPC$TL_inputMediaDocument tLRPC$TL_inputMediaDocument = (TLRPC$TL_inputMediaDocument) tLRPC$InputMedia;
                if (z && isSameReference(tLRPC$TL_inputMediaDocument.id.file_reference, bArr)) {
                    return false;
                }
                tLRPC$TL_inputMediaDocument.id.file_reference = bArr;
            } else if (tLRPC$InputMedia instanceof TLRPC$TL_inputMediaPhoto) {
                TLRPC$TL_inputMediaPhoto tLRPC$TL_inputMediaPhoto = (TLRPC$TL_inputMediaPhoto) tLRPC$InputMedia;
                if (z && isSameReference(tLRPC$TL_inputMediaPhoto.id.file_reference, bArr)) {
                    return false;
                }
                tLRPC$TL_inputMediaPhoto.id.file_reference = bArr;
            }
            int indexOf = tLRPC$TL_messages_sendMultiMedia.multi_media.indexOf(tLRPC$TL_inputSingleMedia);
            if (indexOf < 0) {
                return true;
            }
            ArrayList arrayList = (ArrayList) objArr[3];
            arrayList.set(indexOf, (Object) null);
            boolean z2 = true;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) != null) {
                    z2 = false;
                }
            }
            if (z2) {
                this.multiMediaCache.remove(tLRPC$TL_messages_sendMultiMedia);
                AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda5(this, tLRPC$TL_messages_sendMultiMedia, objArr));
            }
        } else if (requester.args[0] instanceof TLRPC$TL_messages_sendMedia) {
            TLRPC$InputMedia tLRPC$InputMedia2 = ((TLRPC$TL_messages_sendMedia) requester.args[0]).media;
            if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaDocument) {
                TLRPC$TL_inputMediaDocument tLRPC$TL_inputMediaDocument2 = (TLRPC$TL_inputMediaDocument) tLRPC$InputMedia2;
                if (z && isSameReference(tLRPC$TL_inputMediaDocument2.id.file_reference, bArr)) {
                    return false;
                }
                tLRPC$TL_inputMediaDocument2.id.file_reference = bArr;
            } else if (tLRPC$InputMedia2 instanceof TLRPC$TL_inputMediaPhoto) {
                TLRPC$TL_inputMediaPhoto tLRPC$TL_inputMediaPhoto2 = (TLRPC$TL_inputMediaPhoto) tLRPC$InputMedia2;
                if (z && isSameReference(tLRPC$TL_inputMediaPhoto2.id.file_reference, bArr)) {
                    return false;
                }
                tLRPC$TL_inputMediaPhoto2.id.file_reference = bArr;
            }
            AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda0(this, requester));
        } else if (requester.args[0] instanceof TLRPC$TL_messages_editMessage) {
            TLRPC$InputMedia tLRPC$InputMedia3 = ((TLRPC$TL_messages_editMessage) requester.args[0]).media;
            if (tLRPC$InputMedia3 instanceof TLRPC$TL_inputMediaDocument) {
                TLRPC$TL_inputMediaDocument tLRPC$TL_inputMediaDocument3 = (TLRPC$TL_inputMediaDocument) tLRPC$InputMedia3;
                if (z && isSameReference(tLRPC$TL_inputMediaDocument3.id.file_reference, bArr)) {
                    return false;
                }
                tLRPC$TL_inputMediaDocument3.id.file_reference = bArr;
            } else if (tLRPC$InputMedia3 instanceof TLRPC$TL_inputMediaPhoto) {
                TLRPC$TL_inputMediaPhoto tLRPC$TL_inputMediaPhoto3 = (TLRPC$TL_inputMediaPhoto) tLRPC$InputMedia3;
                if (z && isSameReference(tLRPC$TL_inputMediaPhoto3.id.file_reference, bArr)) {
                    return false;
                }
                tLRPC$TL_inputMediaPhoto3.id.file_reference = bArr;
            }
            AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda1(this, requester));
        } else if (requester.args[0] instanceof TLRPC$TL_messages_saveGif) {
            TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif = (TLRPC$TL_messages_saveGif) requester.args[0];
            if (z && isSameReference(tLRPC$TL_messages_saveGif.id.file_reference, bArr)) {
                return false;
            }
            tLRPC$TL_messages_saveGif.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_saveGif, FileRefController$$ExternalSyntheticLambda31.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC$TL_messages_saveRecentSticker) {
            TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker = (TLRPC$TL_messages_saveRecentSticker) requester.args[0];
            if (z && isSameReference(tLRPC$TL_messages_saveRecentSticker.id.file_reference, bArr)) {
                return false;
            }
            tLRPC$TL_messages_saveRecentSticker.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_saveRecentSticker, FileRefController$$ExternalSyntheticLambda33.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC$TL_messages_faveSticker) {
            TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker = (TLRPC$TL_messages_faveSticker) requester.args[0];
            if (z && isSameReference(tLRPC$TL_messages_faveSticker.id.file_reference, bArr)) {
                return false;
            }
            tLRPC$TL_messages_faveSticker.id.file_reference = bArr;
            getConnectionsManager().sendRequest(tLRPC$TL_messages_faveSticker, FileRefController$$ExternalSyntheticLambda32.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC$TL_messages_getAttachedStickers) {
            TLRPC$TL_messages_getAttachedStickers tLRPC$TL_messages_getAttachedStickers = (TLRPC$TL_messages_getAttachedStickers) requester.args[0];
            TLRPC$InputStickeredMedia tLRPC$InputStickeredMedia = tLRPC$TL_messages_getAttachedStickers.media;
            if (tLRPC$InputStickeredMedia instanceof TLRPC$TL_inputStickeredMediaDocument) {
                TLRPC$TL_inputStickeredMediaDocument tLRPC$TL_inputStickeredMediaDocument = (TLRPC$TL_inputStickeredMediaDocument) tLRPC$InputStickeredMedia;
                if (z && isSameReference(tLRPC$TL_inputStickeredMediaDocument.id.file_reference, bArr)) {
                    return false;
                }
                tLRPC$TL_inputStickeredMediaDocument.id.file_reference = bArr;
            } else if (tLRPC$InputStickeredMedia instanceof TLRPC$TL_inputStickeredMediaPhoto) {
                TLRPC$TL_inputStickeredMediaPhoto tLRPC$TL_inputStickeredMediaPhoto = (TLRPC$TL_inputStickeredMediaPhoto) tLRPC$InputStickeredMedia;
                if (z && isSameReference(tLRPC$TL_inputStickeredMediaPhoto.id.file_reference, bArr)) {
                    return false;
                }
                tLRPC$TL_inputStickeredMediaPhoto.id.file_reference = bArr;
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getAttachedStickers, (RequestDelegate) requester.args[1]);
        } else if (requester.args[1] instanceof FileLoadOperation) {
            FileLoadOperation fileLoadOperation = (FileLoadOperation) requester.args[1];
            if (tLRPC$InputFileLocation != null) {
                if (z && isSameReference(fileLoadOperation.location.file_reference, tLRPC$InputFileLocation.file_reference)) {
                    return false;
                }
                fileLoadOperation.location = tLRPC$InputFileLocation;
            } else if (z && isSameReference(requester.location.file_reference, bArr)) {
                return false;
            } else {
                requester.location.file_reference = bArr;
            }
            fileLoadOperation.requestingReference = false;
            fileLoadOperation.startDownloadRequest();
        }
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onUpdateObjectReference$21(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tLRPC$TL_messages_sendMultiMedia, objArr[1], objArr[2], (ArrayList<Object>) null, objArr[4], objArr[5].booleanValue());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onUpdateObjectReference$22(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, (HashMap<String, String>) null, ((Boolean) requester.args[6]).booleanValue());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onUpdateObjectReference$23(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, (HashMap<String, String>) null, ((Boolean) requester.args[6]).booleanValue());
    }

    private void sendErrorToObject(Object[] objArr, int i) {
        if (objArr[0] instanceof TLRPC$TL_inputSingleMedia) {
            TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia = objArr[1];
            Object[] objArr2 = this.multiMediaCache.get(tLRPC$TL_messages_sendMultiMedia);
            if (objArr2 != null) {
                this.multiMediaCache.remove(tLRPC$TL_messages_sendMultiMedia);
                AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda4(this, tLRPC$TL_messages_sendMultiMedia, objArr2));
            }
        } else if ((objArr[0] instanceof TLRPC$TL_messages_sendMedia) || (objArr[0] instanceof TLRPC$TL_messages_editMessage)) {
            AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda8(this, objArr));
        } else if (objArr[0] instanceof TLRPC$TL_messages_saveGif) {
            TLRPC$TL_messages_saveGif tLRPC$TL_messages_saveGif = objArr[0];
        } else if (objArr[0] instanceof TLRPC$TL_messages_saveRecentSticker) {
            TLRPC$TL_messages_saveRecentSticker tLRPC$TL_messages_saveRecentSticker = objArr[0];
        } else if (objArr[0] instanceof TLRPC$TL_messages_faveSticker) {
            TLRPC$TL_messages_faveSticker tLRPC$TL_messages_faveSticker = objArr[0];
        } else if (objArr[0] instanceof TLRPC$TL_messages_getAttachedStickers) {
            getConnectionsManager().sendRequest(objArr[0], objArr[1]);
        } else if (i == 0) {
            TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
            tLRPC$TL_error.text = "not found parent object to request reference";
            tLRPC$TL_error.code = 400;
            if (objArr[1] instanceof FileLoadOperation) {
                FileLoadOperation fileLoadOperation = objArr[1];
                fileLoadOperation.requestingReference = false;
                fileLoadOperation.processRequestResult(objArr[2], tLRPC$TL_error);
            }
        } else if (i == 1 && (objArr[1] instanceof FileLoadOperation)) {
            FileLoadOperation fileLoadOperation2 = objArr[1];
            fileLoadOperation2.requestingReference = false;
            fileLoadOperation2.onFail(false, 0);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendErrorToObject$27(TLRPC$TL_messages_sendMultiMedia tLRPC$TL_messages_sendMultiMedia, Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequestMulti(tLRPC$TL_messages_sendMultiMedia, objArr[1], objArr[2], (ArrayList<Object>) null, objArr[4], objArr[5].booleanValue());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendErrorToObject$28(Object[] objArr) {
        getSendMessagesHelper().performSendMessageRequest(objArr[0], objArr[1], objArr[2], objArr[3], objArr[4].booleanValue(), objArr[5], (Object) null, (HashMap<String, String>) null, objArr[6].booleanValue());
    }

    /* JADX WARNING: type inference failed for: r13v0 */
    /* JADX WARNING: type inference failed for: r13v2 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r13v1, types: [int, boolean] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0034  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x041a  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x042f  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x013c A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0082 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x014d A[LOOP:2: B:54:0x00d1->B:79:0x014d, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean onRequestComplete(java.lang.String r28, java.lang.String r29, org.telegram.tgnet.TLObject r30, boolean r31, boolean r32) {
        /*
            r27 = this;
            r6 = r27
            r7 = r28
            r8 = r29
            r9 = r30
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$TL_account_wallPapers
            if (r10 == 0) goto L_0x0010
            java.lang.String r0 = "wallpaper"
        L_0x000e:
            r11 = r0
            goto L_0x0026
        L_0x0010:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messages_savedGifs
            if (r0 == 0) goto L_0x0017
            java.lang.String r0 = "gif"
            goto L_0x000e
        L_0x0017:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messages_recentStickers
            if (r0 == 0) goto L_0x001e
            java.lang.String r0 = "recent"
            goto L_0x000e
        L_0x001e:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messages_favedStickers
            if (r0 == 0) goto L_0x0025
            java.lang.String r0 = "fav"
            goto L_0x000e
        L_0x0025:
            r11 = r8
        L_0x0026:
            r13 = 1
            if (r8 == 0) goto L_0x0076
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r0 = r6.parentRequester
            java.lang.Object r0 = r0.get(r8)
            r14 = r0
            java.util.ArrayList r14 = (java.util.ArrayList) r14
            if (r14 == 0) goto L_0x0076
            int r15 = r14.size()
            r5 = 0
            r16 = 0
        L_0x003b:
            if (r5 >= r15) goto L_0x006b
            java.lang.Object r0 = r14.get(r5)
            org.telegram.messenger.FileRefController$Requester r0 = (org.telegram.messenger.FileRefController.Requester) r0
            boolean r1 = r0.completed
            if (r1 == 0) goto L_0x004c
            r17 = r5
            goto L_0x0068
        L_0x004c:
            java.lang.String r1 = r0.locationKey
            r2 = 0
            if (r31 == 0) goto L_0x0057
            if (r16 != 0) goto L_0x0057
            r4 = 1
            goto L_0x0058
        L_0x0057:
            r4 = 0
        L_0x0058:
            r0 = r27
            r3 = r30
            r17 = r5
            r5 = r32
            boolean r0 = r0.onRequestComplete(r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x0068
            r16 = 1
        L_0x0068:
            int r5 = r17 + 1
            goto L_0x003b
        L_0x006b:
            if (r16 == 0) goto L_0x0070
            r6.putReponseToCache(r11, r9)
        L_0x0070:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r0 = r6.parentRequester
            r0.remove(r8)
            goto L_0x0078
        L_0x0076:
            r16 = 0
        L_0x0078:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r0 = r6.locationRequester
            java.lang.Object r0 = r0.get(r7)
            java.util.ArrayList r0 = (java.util.ArrayList) r0
            if (r0 != 0) goto L_0x0083
            return r16
        L_0x0083:
            int r1 = r0.size()
            r3 = 0
            r4 = 0
            r5 = 0
            r8 = 0
        L_0x008b:
            if (r3 >= r1) goto L_0x0443
            java.lang.Object r11 = r0.get(r3)
            org.telegram.messenger.FileRefController$Requester r11 = (org.telegram.messenger.FileRefController.Requester) r11
            boolean r14 = r11.completed
            if (r14 == 0) goto L_0x00a4
            r2 = r32
            r18 = r0
            r20 = r1
            r0 = 0
            r11 = 1
            r12 = 0
            goto L_0x043a
        L_0x00a4:
            org.telegram.tgnet.TLRPC$InputFileLocation r14 = r11.location
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_inputFileLocation
            if (r14 != 0) goto L_0x00b4
            org.telegram.tgnet.TLRPC$InputFileLocation r14 = r11.location
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation
            if (r14 == 0) goto L_0x00b8
        L_0x00b4:
            org.telegram.tgnet.TLRPC$InputFileLocation[] r5 = new org.telegram.tgnet.TLRPC$InputFileLocation[r13]
            boolean[] r4 = new boolean[r13]
        L_0x00b8:
            boolean unused = r11.completed = r13
            boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC$messages_Messages
            if (r14 == 0) goto L_0x017f
            r14 = r9
            org.telegram.tgnet.TLRPC$messages_Messages r14 = (org.telegram.tgnet.TLRPC$messages_Messages) r14
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            boolean r15 = r15.isEmpty()
            if (r15 != 0) goto L_0x0178
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r15 = r14.messages
            int r15 = r15.size()
            r2 = 0
        L_0x00d1:
            if (r2 >= r15) goto L_0x0155
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r13 = r14.messages
            java.lang.Object r13 = r13.get(r2)
            org.telegram.tgnet.TLRPC$Message r13 = (org.telegram.tgnet.TLRPC$Message) r13
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r13.media
            if (r12 == 0) goto L_0x0127
            r18 = r0
            org.telegram.tgnet.TLRPC$Document r0 = r12.document
            if (r0 == 0) goto L_0x00ee
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r0 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r0, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            goto L_0x0139
        L_0x00ee:
            org.telegram.tgnet.TLRPC$TL_game r0 = r12.game
            if (r0 == 0) goto L_0x010d
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r0 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r0, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r0 != 0) goto L_0x0139
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r13.media
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r0 = r6.getFileReference((org.telegram.tgnet.TLRPC$Photo) r0, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            goto L_0x0139
        L_0x010d:
            org.telegram.tgnet.TLRPC$Photo r0 = r12.photo
            if (r0 == 0) goto L_0x011a
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r0 = r6.getFileReference((org.telegram.tgnet.TLRPC$Photo) r0, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            goto L_0x0139
        L_0x011a:
            org.telegram.tgnet.TLRPC$WebPage r0 = r12.webpage
            if (r0 == 0) goto L_0x013a
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r0 = r6.getFileReference((org.telegram.tgnet.TLRPC$WebPage) r0, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            goto L_0x0139
        L_0x0127:
            r18 = r0
            org.telegram.tgnet.TLRPC$MessageAction r0 = r13.action
            boolean r12 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r12 == 0) goto L_0x013a
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r0 = r6.getFileReference((org.telegram.tgnet.TLRPC$Photo) r0, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
        L_0x0139:
            r8 = r0
        L_0x013a:
            if (r8 == 0) goto L_0x014d
            if (r31 == 0) goto L_0x014b
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r2 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r14.chats
            r15 = 0
            r0.replaceMessageIfExists(r13, r2, r12, r15)
            goto L_0x0157
        L_0x014b:
            r15 = 0
            goto L_0x0157
        L_0x014d:
            r0 = 0
            int r2 = r2 + 1
            r0 = r18
            r13 = 1
            goto L_0x00d1
        L_0x0155:
            r18 = r0
        L_0x0157:
            r0 = 0
            if (r8 != 0) goto L_0x017a
            org.telegram.messenger.MessagesStorage r2 = r27.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r12 = r14.messages
            java.lang.Object r12 = r12.get(r0)
            org.telegram.tgnet.TLRPC$Message r12 = (org.telegram.tgnet.TLRPC$Message) r12
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r0 = r14.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r13 = r14.chats
            r14 = 1
            r2.replaceMessageIfExists(r12, r0, r13, r14)
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x017a
            java.lang.String r0 = "file ref not found in messages, replacing message"
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x017a
        L_0x0178:
            r18 = r0
        L_0x017a:
            r20 = r1
        L_0x017c:
            r12 = 0
            goto L_0x0418
        L_0x017f:
            r18 = r0
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_help_appUpdate
            if (r0 == 0) goto L_0x01a2
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r0 = (org.telegram.tgnet.TLRPC$TL_help_appUpdate) r0
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r2 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r2 != 0) goto L_0x019e
            org.telegram.tgnet.TLRPC$Document r0 = r0.sticker
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r11.location
            byte[] r2 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r0, (org.telegram.tgnet.TLRPC$InputFileLocation) r2, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
        L_0x019e:
            r20 = r1
            r8 = r2
            goto L_0x017c
        L_0x01a2:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$WebPage
            if (r0 == 0) goto L_0x01b3
            r0 = r9
            org.telegram.tgnet.TLRPC$WebPage r0 = (org.telegram.tgnet.TLRPC$WebPage) r0
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r11.location
            byte[] r0 = r6.getFileReference((org.telegram.tgnet.TLRPC$WebPage) r0, (org.telegram.tgnet.TLRPC$InputFileLocation) r2, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            r8 = r0
            goto L_0x017a
        L_0x01b3:
            if (r10 == 0) goto L_0x01e8
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_account_wallPapers r0 = (org.telegram.tgnet.TLRPC$TL_account_wallPapers) r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r2 = r0.wallpapers
            int r2 = r2.size()
            r12 = 0
        L_0x01bf:
            if (r12 >= r2) goto L_0x01d9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r8 = r0.wallpapers
            java.lang.Object r8 = r8.get(r12)
            org.telegram.tgnet.TLRPC$WallPaper r8 = (org.telegram.tgnet.TLRPC$WallPaper) r8
            org.telegram.tgnet.TLRPC$Document r8 = r8.document
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r11.location
            byte[] r8 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r8, (org.telegram.tgnet.TLRPC$InputFileLocation) r13, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r8 == 0) goto L_0x01d6
            goto L_0x01d9
        L_0x01d6:
            int r12 = r12 + 1
            goto L_0x01bf
        L_0x01d9:
            if (r8 == 0) goto L_0x017a
            if (r31 == 0) goto L_0x017a
            org.telegram.messenger.MessagesStorage r2 = r27.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r0 = r0.wallpapers
            r12 = 1
            r2.putWallpapers(r0, r12)
            goto L_0x017a
        L_0x01e8:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_wallPaper
            if (r0 == 0) goto L_0x020e
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_wallPaper r0 = (org.telegram.tgnet.TLRPC$TL_wallPaper) r0
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r2 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r2 == 0) goto L_0x019e
            if (r31 == 0) goto L_0x019e
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r8.add(r0)
            org.telegram.messenger.MessagesStorage r0 = r27.getMessagesStorage()
            r12 = 0
            r0.putWallpapers(r8, r12)
            goto L_0x019e
        L_0x020e:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_theme
            if (r0 == 0) goto L_0x022d
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_theme r0 = (org.telegram.tgnet.TLRPC$TL_theme) r0
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r2 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r2 == 0) goto L_0x019e
            if (r31 == 0) goto L_0x019e
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda9 r8 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda9
            r8.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8)
            goto L_0x019e
        L_0x022d:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$Vector
            if (r0 == 0) goto L_0x02bb
            r0 = r9
            org.telegram.tgnet.TLRPC$Vector r0 = (org.telegram.tgnet.TLRPC$Vector) r0
            java.util.ArrayList<java.lang.Object> r2 = r0.objects
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x017a
            java.util.ArrayList<java.lang.Object> r2 = r0.objects
            int r2 = r2.size()
            r15 = 0
        L_0x0243:
            if (r15 >= r2) goto L_0x017a
            java.util.ArrayList<java.lang.Object> r12 = r0.objects
            java.lang.Object r12 = r12.get(r15)
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC$User
            if (r13 == 0) goto L_0x0280
            org.telegram.tgnet.TLRPC$User r12 = (org.telegram.tgnet.TLRPC$User) r12
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r8 = r6.getFileReference((org.telegram.tgnet.TLRPC$User) r12, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r31 == 0) goto L_0x027b
            if (r8 == 0) goto L_0x027b
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            r13.add(r12)
            org.telegram.messenger.MessagesStorage r14 = r27.getMessagesStorage()
            r19 = r0
            r20 = r1
            r0 = 0
            r1 = 1
            r14.putUsersAndChats(r13, r0, r1, r1)
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda7 r0 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda7
            r0.<init>(r6, r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x02b0
        L_0x027b:
            r19 = r0
            r20 = r1
            goto L_0x02b0
        L_0x0280:
            r19 = r0
            r20 = r1
            boolean r0 = r12 instanceof org.telegram.tgnet.TLRPC$Chat
            if (r0 == 0) goto L_0x02b0
            org.telegram.tgnet.TLRPC$Chat r12 = (org.telegram.tgnet.TLRPC$Chat) r12
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r11.location
            byte[] r0 = r6.getFileReference((org.telegram.tgnet.TLRPC$Chat) r12, (org.telegram.tgnet.TLRPC$InputFileLocation) r0, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r31 == 0) goto L_0x02af
            if (r0 == 0) goto L_0x02af
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r1.add(r12)
            org.telegram.messenger.MessagesStorage r8 = r27.getMessagesStorage()
            r13 = 0
            r14 = 1
            r8.putUsersAndChats(r13, r1, r14, r14)
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda2 r1 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda2
            r1.<init>(r6, r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x02af:
            r8 = r0
        L_0x02b0:
            if (r8 == 0) goto L_0x02b4
            goto L_0x017c
        L_0x02b4:
            int r15 = r15 + 1
            r0 = r19
            r1 = r20
            goto L_0x0243
        L_0x02bb:
            r20 = r1
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messages_chats
            if (r0 == 0) goto L_0x0308
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_messages_chats r0 = (org.telegram.tgnet.TLRPC$TL_messages_chats) r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r0.chats
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x017c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r1 = r0.chats
            int r1 = r1.size()
            r15 = 0
        L_0x02d3:
            if (r15 >= r1) goto L_0x017c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r2 = r0.chats
            java.lang.Object r2 = r2.get(r15)
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC$Chat) r2
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r8 = r6.getFileReference((org.telegram.tgnet.TLRPC$Chat) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r8 == 0) goto L_0x0304
            if (r31 == 0) goto L_0x017c
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r0.add(r2)
            org.telegram.messenger.MessagesStorage r1 = r27.getMessagesStorage()
            r12 = 0
            r13 = 1
            r1.putUsersAndChats(r12, r0, r13, r13)
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda3 r0 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda3
            r0.<init>(r6, r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            goto L_0x0418
        L_0x0304:
            r12 = 0
            int r15 = r15 + 1
            goto L_0x02d3
        L_0x0308:
            r12 = 0
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messages_savedGifs
            if (r0 == 0) goto L_0x0346
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_messages_savedGifs r0 = (org.telegram.tgnet.TLRPC$TL_messages_savedGifs) r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r0.gifs
            int r1 = r1.size()
            r15 = 0
        L_0x0317:
            if (r15 >= r1) goto L_0x032f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r0.gifs
            java.lang.Object r2 = r2.get(r15)
            org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC$Document) r2
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r8 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r8 == 0) goto L_0x032c
            goto L_0x032f
        L_0x032c:
            int r15 = r15 + 1
            goto L_0x0317
        L_0x032f:
            if (r31 == 0) goto L_0x0418
            org.telegram.messenger.MediaDataController r21 = r27.getMediaDataController()
            r22 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.gifs
            r24 = 1
            r25 = 0
            r26 = 1
            r23 = r0
            r21.processLoadedRecentDocuments(r22, r23, r24, r25, r26)
            goto L_0x0418
        L_0x0346:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messages_stickerSet
            if (r0 == 0) goto L_0x037a
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r0 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r0
            if (r8 != 0) goto L_0x036e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r0.documents
            int r1 = r1.size()
            r15 = 0
        L_0x0356:
            if (r15 >= r1) goto L_0x036e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r0.documents
            java.lang.Object r2 = r2.get(r15)
            org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC$Document) r2
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r8 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r8 == 0) goto L_0x036b
            goto L_0x036e
        L_0x036b:
            int r15 = r15 + 1
            goto L_0x0356
        L_0x036e:
            if (r31 == 0) goto L_0x0418
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda6 r1 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda6
            r1.<init>(r6, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x0418
        L_0x037a:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messages_recentStickers
            if (r0 == 0) goto L_0x03b6
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_messages_recentStickers r0 = (org.telegram.tgnet.TLRPC$TL_messages_recentStickers) r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r0.stickers
            int r1 = r1.size()
            r15 = 0
        L_0x0388:
            if (r15 >= r1) goto L_0x03a0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r0.stickers
            java.lang.Object r2 = r2.get(r15)
            org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC$Document) r2
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r8 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r8 == 0) goto L_0x039d
            goto L_0x03a0
        L_0x039d:
            int r15 = r15 + 1
            goto L_0x0388
        L_0x03a0:
            if (r31 == 0) goto L_0x0418
            org.telegram.messenger.MediaDataController r21 = r27.getMediaDataController()
            r22 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.stickers
            r24 = 0
            r25 = 0
            r26 = 1
            r23 = r0
            r21.processLoadedRecentDocuments(r22, r23, r24, r25, r26)
            goto L_0x0418
        L_0x03b6:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messages_favedStickers
            if (r0 == 0) goto L_0x03f2
            r0 = r9
            org.telegram.tgnet.TLRPC$TL_messages_favedStickers r0 = (org.telegram.tgnet.TLRPC$TL_messages_favedStickers) r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r1 = r0.stickers
            int r1 = r1.size()
            r15 = 0
        L_0x03c4:
            if (r15 >= r1) goto L_0x03dc
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r0.stickers
            java.lang.Object r2 = r2.get(r15)
            org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC$Document) r2
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r8 = r6.getFileReference((org.telegram.tgnet.TLRPC$Document) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r8 == 0) goto L_0x03d9
            goto L_0x03dc
        L_0x03d9:
            int r15 = r15 + 1
            goto L_0x03c4
        L_0x03dc:
            if (r31 == 0) goto L_0x0418
            org.telegram.messenger.MediaDataController r21 = r27.getMediaDataController()
            r22 = 2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r0 = r0.stickers
            r24 = 0
            r25 = 0
            r26 = 1
            r23 = r0
            r21.processLoadedRecentDocuments(r22, r23, r24, r25, r26)
            goto L_0x0418
        L_0x03f2:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$photos_Photos
            if (r0 == 0) goto L_0x0418
            r0 = r9
            org.telegram.tgnet.TLRPC$photos_Photos r0 = (org.telegram.tgnet.TLRPC$photos_Photos) r0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r1 = r0.photos
            int r1 = r1.size()
            r15 = 0
        L_0x0400:
            if (r15 >= r1) goto L_0x0418
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r2 = r0.photos
            java.lang.Object r2 = r2.get(r15)
            org.telegram.tgnet.TLRPC$Photo r2 = (org.telegram.tgnet.TLRPC$Photo) r2
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r11.location
            byte[] r8 = r6.getFileReference((org.telegram.tgnet.TLRPC$Photo) r2, (org.telegram.tgnet.TLRPC$InputFileLocation) r8, (boolean[]) r4, (org.telegram.tgnet.TLRPC$InputFileLocation[]) r5)
            if (r8 == 0) goto L_0x0415
            goto L_0x0418
        L_0x0415:
            int r15 = r15 + 1
            goto L_0x0400
        L_0x0418:
            if (r8 == 0) goto L_0x042f
            r0 = 0
            if (r5 == 0) goto L_0x0422
            r1 = r5[r0]
            r2 = r32
            goto L_0x0425
        L_0x0422:
            r2 = r32
            r1 = r12
        L_0x0425:
            boolean r1 = r6.onUpdateObjectReference(r11, r8, r1, r2)
            r11 = 1
            if (r1 == 0) goto L_0x043a
            r16 = 1
            goto L_0x043a
        L_0x042f:
            r2 = r32
            r0 = 0
            java.lang.Object[] r1 = r11.args
            r11 = 1
            r6.sendErrorToObject(r1, r11)
        L_0x043a:
            int r3 = r3 + 1
            r0 = r18
            r1 = r20
            r13 = 1
            goto L_0x008b
        L_0x0443:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r0 = r6.locationRequester
            r0.remove(r7)
            if (r16 == 0) goto L_0x044d
            r6.putReponseToCache(r7, r9)
        L_0x044d:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.onRequestComplete(java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestComplete$30(TLRPC$User tLRPC$User) {
        getMessagesController().putUser(tLRPC$User, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestComplete$31(TLRPC$Chat tLRPC$Chat) {
        getMessagesController().putChat(tLRPC$Chat, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestComplete$32(TLRPC$Chat tLRPC$Chat) {
        getMessagesController().putChat(tLRPC$Chat, false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onRequestComplete$33(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        getMediaDataController().replaceStickerSet(tLRPC$TL_messages_stickerSet);
    }

    private void cleanupCache() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastCleanupTime) >= 600000) {
            this.lastCleanupTime = SystemClock.elapsedRealtime();
            ArrayList arrayList = null;
            for (Map.Entry next : this.responseCache.entrySet()) {
                if (Math.abs(SystemClock.elapsedRealtime() - ((CachedResult) next.getValue()).firstQueryTime) >= 600000) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add((String) next.getKey());
                }
            }
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    this.responseCache.remove(arrayList.get(i));
                }
            }
        }
    }

    private CachedResult getCachedResponse(String str) {
        CachedResult cachedResult = this.responseCache.get(str);
        if (cachedResult == null || Math.abs(SystemClock.elapsedRealtime() - cachedResult.firstQueryTime) < 600000) {
            return cachedResult;
        }
        this.responseCache.remove(str);
        return null;
    }

    private void putReponseToCache(String str, TLObject tLObject) {
        CachedResult cachedResult = this.responseCache.get(str);
        if (cachedResult == null) {
            cachedResult = new CachedResult();
            TLObject unused = cachedResult.response = tLObject;
            long unused2 = cachedResult.firstQueryTime = SystemClock.uptimeMillis();
            this.responseCache.put(str, cachedResult);
        }
        long unused3 = cachedResult.lastQueryTime = SystemClock.uptimeMillis();
    }

    private byte[] getFileReference(TLRPC$Document tLRPC$Document, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        if (!(tLRPC$Document == null || tLRPC$InputFileLocation == null)) {
            if (!(tLRPC$InputFileLocation instanceof TLRPC$TL_inputDocumentFileLocation)) {
                int size = tLRPC$Document.thumbs.size();
                int i = 0;
                while (i < size) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$Document.thumbs.get(i);
                    byte[] fileReference = getFileReference(tLRPC$PhotoSize, tLRPC$InputFileLocation, zArr);
                    if (zArr != null && zArr[0]) {
                        tLRPC$InputFileLocationArr[0] = new TLRPC$TL_inputDocumentFileLocation();
                        tLRPC$InputFileLocationArr[0].id = tLRPC$Document.id;
                        tLRPC$InputFileLocationArr[0].volume_id = tLRPC$InputFileLocation.volume_id;
                        tLRPC$InputFileLocationArr[0].local_id = tLRPC$InputFileLocation.local_id;
                        tLRPC$InputFileLocationArr[0].access_hash = tLRPC$Document.access_hash;
                        TLRPC$InputFileLocation tLRPC$InputFileLocation2 = tLRPC$InputFileLocationArr[0];
                        byte[] bArr = tLRPC$Document.file_reference;
                        tLRPC$InputFileLocation2.file_reference = bArr;
                        tLRPC$InputFileLocationArr[0].thumb_size = tLRPC$PhotoSize.type;
                        return bArr;
                    } else if (fileReference != null) {
                        return fileReference;
                    } else {
                        i++;
                    }
                }
            } else if (tLRPC$Document.id == tLRPC$InputFileLocation.id) {
                return tLRPC$Document.file_reference;
            }
        }
        return null;
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [org.telegram.tgnet.TLRPC$InputFileLocation[]] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getPeerReferenceReplacement(org.telegram.tgnet.TLRPC$User r4, org.telegram.tgnet.TLRPC$Chat r5, boolean r6, org.telegram.tgnet.TLRPC$InputFileLocation r7, org.telegram.tgnet.TLRPC$InputFileLocation[] r8, boolean[] r9) {
        /*
            r3 = this;
            r0 = 0
            if (r9 == 0) goto L_0x0058
            boolean r9 = r9[r0]
            if (r9 == 0) goto L_0x0058
            org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r9 = new org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation
            r9.<init>()
            long r1 = r7.volume_id
            r9.id = r1
            r9.volume_id = r1
            int r7 = r7.local_id
            r9.local_id = r7
            r9.big = r6
            if (r4 == 0) goto L_0x002e
            org.telegram.tgnet.TLRPC$TL_inputPeerUser r5 = new org.telegram.tgnet.TLRPC$TL_inputPeerUser
            r5.<init>()
            long r6 = r4.id
            r5.user_id = r6
            long r6 = r4.access_hash
            r5.access_hash = r6
            org.telegram.tgnet.TLRPC$UserProfilePhoto r4 = r4.photo
            long r6 = r4.photo_id
            r9.photo_id = r6
            goto L_0x0052
        L_0x002e:
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r4 == 0) goto L_0x0042
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            r4.<init>()
            long r6 = r5.id
            r4.channel_id = r6
            long r6 = r5.access_hash
            r4.access_hash = r6
            goto L_0x004b
        L_0x0042:
            org.telegram.tgnet.TLRPC$TL_inputPeerChat r4 = new org.telegram.tgnet.TLRPC$TL_inputPeerChat
            r4.<init>()
            long r6 = r5.id
            r4.chat_id = r6
        L_0x004b:
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r5.photo
            long r5 = r5.photo_id
            r9.photo_id = r5
            r5 = r4
        L_0x0052:
            r9.peer = r5
            r8[r0] = r9
            r4 = 1
            return r4
        L_0x0058:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.getPeerReferenceReplacement(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean, org.telegram.tgnet.TLRPC$InputFileLocation, org.telegram.tgnet.TLRPC$InputFileLocation[], boolean[]):boolean");
    }

    private byte[] getFileReference(TLRPC$User tLRPC$User, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto;
        if (tLRPC$User == null || (tLRPC$UserProfilePhoto = tLRPC$User.photo) == null || !(tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation)) {
            return null;
        }
        byte[] fileReference = getFileReference(tLRPC$UserProfilePhoto.photo_small, tLRPC$InputFileLocation, zArr);
        if (getPeerReferenceReplacement(tLRPC$User, (TLRPC$Chat) null, false, tLRPC$InputFileLocation, tLRPC$InputFileLocationArr, zArr)) {
            return new byte[0];
        }
        if (fileReference == null) {
            fileReference = getFileReference(tLRPC$User.photo.photo_big, tLRPC$InputFileLocation, zArr);
            if (getPeerReferenceReplacement(tLRPC$User, (TLRPC$Chat) null, true, tLRPC$InputFileLocation, tLRPC$InputFileLocationArr, zArr)) {
                return new byte[0];
            }
        }
        return fileReference;
    }

    private byte[] getFileReference(TLRPC$Chat tLRPC$Chat, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        TLRPC$ChatPhoto tLRPC$ChatPhoto;
        byte[] bArr = null;
        if (!(tLRPC$Chat == null || (tLRPC$ChatPhoto = tLRPC$Chat.photo) == null || (!(tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation) && !(tLRPC$InputFileLocation instanceof TLRPC$TL_inputPeerPhotoFileLocation)))) {
            if (tLRPC$InputFileLocation instanceof TLRPC$TL_inputPeerPhotoFileLocation) {
                zArr[0] = true;
                if (getPeerReferenceReplacement((TLRPC$User) null, tLRPC$Chat, false, tLRPC$InputFileLocation, tLRPC$InputFileLocationArr, zArr)) {
                    return new byte[0];
                }
                return null;
            }
            bArr = getFileReference(tLRPC$ChatPhoto.photo_small, tLRPC$InputFileLocation, zArr);
            if (getPeerReferenceReplacement((TLRPC$User) null, tLRPC$Chat, false, tLRPC$InputFileLocation, tLRPC$InputFileLocationArr, zArr)) {
                return new byte[0];
            }
            if (bArr == null) {
                bArr = getFileReference(tLRPC$Chat.photo.photo_big, tLRPC$InputFileLocation, zArr);
                if (getPeerReferenceReplacement((TLRPC$User) null, tLRPC$Chat, true, tLRPC$InputFileLocation, tLRPC$InputFileLocationArr, zArr)) {
                    return new byte[0];
                }
            }
        }
        return bArr;
    }

    private byte[] getFileReference(TLRPC$Photo tLRPC$Photo, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        if (tLRPC$Photo == null) {
            return null;
        }
        if (!(tLRPC$InputFileLocation instanceof TLRPC$TL_inputPhotoFileLocation)) {
            if (tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation) {
                int size = tLRPC$Photo.sizes.size();
                int i = 0;
                while (i < size) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$Photo.sizes.get(i);
                    byte[] fileReference = getFileReference(tLRPC$PhotoSize, tLRPC$InputFileLocation, zArr);
                    if (zArr != null && zArr[0]) {
                        tLRPC$InputFileLocationArr[0] = new TLRPC$TL_inputPhotoFileLocation();
                        tLRPC$InputFileLocationArr[0].id = tLRPC$Photo.id;
                        tLRPC$InputFileLocationArr[0].volume_id = tLRPC$InputFileLocation.volume_id;
                        tLRPC$InputFileLocationArr[0].local_id = tLRPC$InputFileLocation.local_id;
                        tLRPC$InputFileLocationArr[0].access_hash = tLRPC$Photo.access_hash;
                        TLRPC$InputFileLocation tLRPC$InputFileLocation2 = tLRPC$InputFileLocationArr[0];
                        byte[] bArr = tLRPC$Photo.file_reference;
                        tLRPC$InputFileLocation2.file_reference = bArr;
                        tLRPC$InputFileLocationArr[0].thumb_size = tLRPC$PhotoSize.type;
                        return bArr;
                    } else if (fileReference != null) {
                        return fileReference;
                    } else {
                        i++;
                    }
                }
            }
            return null;
        } else if (tLRPC$Photo.id == tLRPC$InputFileLocation.id) {
            return tLRPC$Photo.file_reference;
        } else {
            return null;
        }
    }

    private byte[] getFileReference(TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr) {
        if (tLRPC$PhotoSize == null || !(tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation)) {
            return null;
        }
        return getFileReference(tLRPC$PhotoSize.location, tLRPC$InputFileLocation, zArr);
    }

    private byte[] getFileReference(TLRPC$FileLocation tLRPC$FileLocation, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr) {
        if (tLRPC$FileLocation == null || !(tLRPC$InputFileLocation instanceof TLRPC$TL_inputFileLocation) || tLRPC$FileLocation.local_id != tLRPC$InputFileLocation.local_id || tLRPC$FileLocation.volume_id != tLRPC$InputFileLocation.volume_id) {
            return null;
        }
        byte[] bArr = tLRPC$FileLocation.file_reference;
        if (bArr == null && zArr != null) {
            zArr[0] = true;
        }
        return bArr;
    }

    private byte[] getFileReference(TLRPC$WebPage tLRPC$WebPage, TLRPC$InputFileLocation tLRPC$InputFileLocation, boolean[] zArr, TLRPC$InputFileLocation[] tLRPC$InputFileLocationArr) {
        byte[] fileReference = getFileReference(tLRPC$WebPage.document, tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
        if (fileReference != null) {
            return fileReference;
        }
        byte[] fileReference2 = getFileReference(tLRPC$WebPage.photo, tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
        if (fileReference2 != null) {
            return fileReference2;
        }
        if (!tLRPC$WebPage.attributes.isEmpty()) {
            int size = tLRPC$WebPage.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_webPageAttributeTheme tLRPC$TL_webPageAttributeTheme = tLRPC$WebPage.attributes.get(i);
                int size2 = tLRPC$TL_webPageAttributeTheme.documents.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    byte[] fileReference3 = getFileReference(tLRPC$TL_webPageAttributeTheme.documents.get(i2), tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
                    if (fileReference3 != null) {
                        return fileReference3;
                    }
                }
            }
        }
        TLRPC$Page tLRPC$Page = tLRPC$WebPage.cached_page;
        if (tLRPC$Page == null) {
            return null;
        }
        int size3 = tLRPC$Page.documents.size();
        for (int i3 = 0; i3 < size3; i3++) {
            byte[] fileReference4 = getFileReference(tLRPC$WebPage.cached_page.documents.get(i3), tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
            if (fileReference4 != null) {
                return fileReference4;
            }
        }
        int size4 = tLRPC$WebPage.cached_page.photos.size();
        for (int i4 = 0; i4 < size4; i4++) {
            byte[] fileReference5 = getFileReference(tLRPC$WebPage.cached_page.photos.get(i4), tLRPC$InputFileLocation, zArr, tLRPC$InputFileLocationArr);
            if (fileReference5 != null) {
                return fileReference5;
            }
        }
        return null;
    }

    public static boolean isFileRefError(String str) {
        return "FILEREF_EXPIRED".equals(str) || "FILE_REFERENCE_EXPIRED".equals(str) || "FILE_REFERENCE_EMPTY".equals(str) || (str != null && str.startsWith("FILE_REFERENCE_"));
    }
}
