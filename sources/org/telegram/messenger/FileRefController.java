package org.telegram.messenger;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class FileRefController extends BaseController {
    private static volatile FileRefController[] Instance = new FileRefController[3];
    private ArrayList<Waiter> favStickersWaiter = new ArrayList<>();
    private long lastCleanupTime = SystemClock.elapsedRealtime();
    private HashMap<String, ArrayList<Requester>> locationRequester = new HashMap<>();
    private HashMap<TLRPC.TL_messages_sendMultiMedia, Object[]> multiMediaCache = new HashMap<>();
    private HashMap<String, ArrayList<Requester>> parentRequester = new HashMap<>();
    private ArrayList<Waiter> recentStickersWaiter = new ArrayList<>();
    private HashMap<String, CachedResult> responseCache = new HashMap<>();
    private ArrayList<Waiter> savedGifsWaiters = new ArrayList<>();
    private ArrayList<Waiter> wallpaperWaiters = new ArrayList<>();

    private static class Requester {
        /* access modifiers changed from: private */
        public Object[] args;
        /* access modifiers changed from: private */
        public boolean completed;
        /* access modifiers changed from: private */
        public TLRPC.InputFileLocation location;
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

        public Waiter(String loc, String parent) {
            this.locationKey = loc;
            this.parentKey = parent;
        }
    }

    public static FileRefController getInstance(int num) {
        FileRefController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FileRefController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    FileRefController[] fileRefControllerArr = Instance;
                    FileRefController fileRefController = new FileRefController(num);
                    localInstance = fileRefController;
                    fileRefControllerArr[num] = fileRefController;
                }
            }
        }
        return localInstance;
    }

    public FileRefController(int instance) {
        super(instance);
    }

    public static String getKeyForParentObject(Object parentObject) {
        if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            long channelId = messageObject.getChannelId();
            return "message" + messageObject.getRealId() + "_" + channelId + "_" + messageObject.scheduled;
        } else if (parentObject instanceof TLRPC.Message) {
            TLRPC.Message message = (TLRPC.Message) parentObject;
            long channelId2 = message.peer_id != null ? message.peer_id.channel_id : 0;
            return "message" + message.id + "_" + channelId2 + "_" + message.from_scheduled;
        } else if (parentObject instanceof TLRPC.WebPage) {
            return "webpage" + ((TLRPC.WebPage) parentObject).id;
        } else if (parentObject instanceof TLRPC.User) {
            return "user" + ((TLRPC.User) parentObject).id;
        } else if (parentObject instanceof TLRPC.Chat) {
            return "chat" + ((TLRPC.Chat) parentObject).id;
        } else if (parentObject instanceof String) {
            return "str" + ((String) parentObject);
        } else if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
            return "set" + ((TLRPC.TL_messages_stickerSet) parentObject).set.id;
        } else if (parentObject instanceof TLRPC.StickerSetCovered) {
            return "set" + ((TLRPC.StickerSetCovered) parentObject).set.id;
        } else if (parentObject instanceof TLRPC.InputStickerSet) {
            return "set" + ((TLRPC.InputStickerSet) parentObject).id;
        } else if (parentObject instanceof TLRPC.TL_wallPaper) {
            return "wallpaper" + ((TLRPC.TL_wallPaper) parentObject).id;
        } else if (parentObject instanceof TLRPC.TL_theme) {
            return "theme" + ((TLRPC.TL_theme) parentObject).id;
        } else if (parentObject == null) {
            return null;
        } else {
            return "" + parentObject;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:119:0x03ca  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x03e1  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x032a  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x032e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestReference(java.lang.Object r18, java.lang.Object... r19) {
        /*
            r17 = this;
            r6 = r17
            r0 = r18
            r7 = r19
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r2 = 0
            if (r1 == 0) goto L_0x0029
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "start loading request reference for parent = "
            r1.append(r3)
            r1.append(r0)
            java.lang.String r3 = " args = "
            r1.append(r3)
            r3 = r7[r2]
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            org.telegram.messenger.FileLog.d(r1)
        L_0x0029:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputSingleMedia
            r3 = 2
            java.lang.String r4 = "photo_"
            java.lang.String r5 = "file_"
            if (r1 == 0) goto L_0x0092
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r1 = (org.telegram.tgnet.TLRPC.TL_inputSingleMedia) r1
            org.telegram.tgnet.TLRPC$InputMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaDocument
            if (r8 == 0) goto L_0x0061
            org.telegram.tgnet.TLRPC$InputMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r4 = (org.telegram.tgnet.TLRPC.TL_inputMediaDocument) r4
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r4.id
            long r9 = r5.id
            r8.append(r9)
            java.lang.String r5 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r9 = r4.id
            long r9 = r9.id
            r8.id = r9
            goto L_0x008a
        L_0x0061:
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaPhoto
            if (r5 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r5 = (org.telegram.tgnet.TLRPC.TL_inputMediaPhoto) r5
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r5.id
            long r9 = r4.id
            r8.append(r9)
            java.lang.String r4 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r9 = r5.id
            long r9 = r9.id
            r8.id = r9
            r5 = r4
        L_0x008a:
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x008e:
            r6.sendErrorToObject(r7, r2)
            return
        L_0x0092:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia
            if (r1 == 0) goto L_0x00ca
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia r1 = (org.telegram.tgnet.TLRPC.TL_messages_sendMultiMedia) r1
            r4 = r0
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            java.util.HashMap<org.telegram.tgnet.TLRPC$TL_messages_sendMultiMedia, java.lang.Object[]> r5 = r6.multiMediaCache
            r5.put(r1, r7)
            r5 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r8 = r1.multi_media
            int r8 = r8.size()
        L_0x00ab:
            if (r5 >= r8) goto L_0x00c9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_inputSingleMedia> r9 = r1.multi_media
            java.lang.Object r9 = r9.get(r5)
            org.telegram.tgnet.TLRPC$TL_inputSingleMedia r9 = (org.telegram.tgnet.TLRPC.TL_inputSingleMedia) r9
            java.lang.Object r0 = r4.get(r5)
            if (r0 != 0) goto L_0x00bc
            goto L_0x00c6
        L_0x00bc:
            java.lang.Object[] r10 = new java.lang.Object[r3]
            r10[r2] = r9
            r11 = 1
            r10[r11] = r1
            r6.requestReference(r0, r10)
        L_0x00c6:
            int r5 = r5 + 1
            goto L_0x00ab
        L_0x00c9:
            return
        L_0x00ca:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_sendMedia
            if (r1 == 0) goto L_0x012e
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_sendMedia r1 = (org.telegram.tgnet.TLRPC.TL_messages_sendMedia) r1
            org.telegram.tgnet.TLRPC$InputMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaDocument
            if (r8 == 0) goto L_0x00fd
            org.telegram.tgnet.TLRPC$InputMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r4 = (org.telegram.tgnet.TLRPC.TL_inputMediaDocument) r4
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r4.id
            long r9 = r5.id
            r8.append(r9)
            java.lang.String r5 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r9 = r4.id
            long r9 = r9.id
            r8.id = r9
            goto L_0x0126
        L_0x00fd:
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaPhoto
            if (r5 == 0) goto L_0x012a
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r5 = (org.telegram.tgnet.TLRPC.TL_inputMediaPhoto) r5
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r5.id
            long r9 = r4.id
            r8.append(r9)
            java.lang.String r4 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r9 = r5.id
            long r9 = r9.id
            r8.id = r9
            r5 = r4
        L_0x0126:
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x012a:
            r6.sendErrorToObject(r7, r2)
            return
        L_0x012e:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_editMessage
            if (r1 == 0) goto L_0x0192
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_editMessage r1 = (org.telegram.tgnet.TLRPC.TL_messages_editMessage) r1
            org.telegram.tgnet.TLRPC$InputMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaDocument
            if (r8 == 0) goto L_0x0161
            org.telegram.tgnet.TLRPC$InputMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaDocument r4 = (org.telegram.tgnet.TLRPC.TL_inputMediaDocument) r4
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r4.id
            long r9 = r5.id
            r8.append(r9)
            java.lang.String r5 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r9 = r4.id
            long r9 = r9.id
            r8.id = r9
            goto L_0x018a
        L_0x0161:
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputMediaPhoto
            if (r5 == 0) goto L_0x018e
            org.telegram.tgnet.TLRPC$InputMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputMediaPhoto r5 = (org.telegram.tgnet.TLRPC.TL_inputMediaPhoto) r5
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r5.id
            long r9 = r4.id
            r8.append(r9)
            java.lang.String r4 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r9 = r5.id
            long r9 = r9.id
            r8.id = r9
            r5 = r4
        L_0x018a:
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x018e:
            r6.sendErrorToObject(r7, r2)
            return
        L_0x0192:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_saveGif
            if (r1 == 0) goto L_0x01bf
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_saveGif r1 = (org.telegram.tgnet.TLRPC.TL_messages_saveGif) r1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r1.id
            long r8 = r5.id
            r4.append(r8)
            java.lang.String r5 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r4 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r4.<init>()
            r8 = r4
            org.telegram.tgnet.TLRPC$InputDocument r4 = r1.id
            long r9 = r4.id
            r8.id = r9
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x01bf:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker
            if (r1 == 0) goto L_0x01ec
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_saveRecentSticker r1 = (org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker) r1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r1.id
            long r8 = r5.id
            r4.append(r8)
            java.lang.String r5 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r4 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r4.<init>()
            r8 = r4
            org.telegram.tgnet.TLRPC$InputDocument r4 = r1.id
            long r9 = r4.id
            r8.id = r9
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x01ec:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_faveSticker
            if (r1 == 0) goto L_0x0219
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_faveSticker r1 = (org.telegram.tgnet.TLRPC.TL_messages_faveSticker) r1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r1.id
            long r8 = r5.id
            r4.append(r8)
            java.lang.String r5 = r4.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r4 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r4.<init>()
            r8 = r4
            org.telegram.tgnet.TLRPC$InputDocument r4 = r1.id
            long r9 = r4.id
            r8.id = r9
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x0219:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers
            if (r1 == 0) goto L_0x027d
            r1 = r7[r2]
            org.telegram.tgnet.TLRPC$TL_messages_getAttachedStickers r1 = (org.telegram.tgnet.TLRPC.TL_messages_getAttachedStickers) r1
            org.telegram.tgnet.TLRPC$InputStickeredMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_inputStickeredMediaDocument
            if (r8 == 0) goto L_0x024c
            org.telegram.tgnet.TLRPC$InputStickeredMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputStickeredMediaDocument r4 = (org.telegram.tgnet.TLRPC.TL_inputStickeredMediaDocument) r4
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r5)
            org.telegram.tgnet.TLRPC$InputDocument r5 = r4.id
            long r9 = r5.id
            r8.append(r9)
            java.lang.String r5 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputDocument r9 = r4.id
            long r9 = r9.id
            r8.id = r9
            goto L_0x0275
        L_0x024c:
            org.telegram.tgnet.TLRPC$InputStickeredMedia r5 = r1.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputStickeredMediaPhoto
            if (r5 == 0) goto L_0x0279
            org.telegram.tgnet.TLRPC$InputStickeredMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$TL_inputStickeredMediaPhoto r5 = (org.telegram.tgnet.TLRPC.TL_inputStickeredMediaPhoto) r5
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r4)
            org.telegram.tgnet.TLRPC$InputPhoto r4 = r5.id
            long r9 = r4.id
            r8.append(r9)
            java.lang.String r4 = r8.toString()
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputPhoto r9 = r5.id
            long r9 = r9.id
            r8.id = r9
            r5 = r4
        L_0x0275:
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x0279:
            r6.sendErrorToObject(r7, r2)
            return
        L_0x027d:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputFileLocation
            if (r1 == 0) goto L_0x02a8
            r1 = r7[r2]
            r8 = r1
            org.telegram.tgnet.TLRPC$TL_inputFileLocation r8 = (org.telegram.tgnet.TLRPC.TL_inputFileLocation) r8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "loc_"
            r1.append(r4)
            int r4 = r8.local_id
            r1.append(r4)
            java.lang.String r4 = "_"
            r1.append(r4)
            long r4 = r8.volume_id
            r1.append(r4)
            java.lang.String r5 = r1.toString()
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x02a8:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation
            if (r1 == 0) goto L_0x02c7
            r1 = r7[r2]
            r8 = r1
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = (org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation) r8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            long r4 = r8.id
            r1.append(r4)
            java.lang.String r5 = r1.toString()
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x02c7:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputPhotoFileLocation
            if (r1 == 0) goto L_0x02e6
            r1 = r7[r2]
            r8 = r1
            org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation r8 = (org.telegram.tgnet.TLRPC.TL_inputPhotoFileLocation) r8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            long r4 = r8.id
            r1.append(r4)
            java.lang.String r5 = r1.toString()
            r9 = r8
            r8 = r5
            goto L_0x0306
        L_0x02e6:
            r1 = r7[r2]
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation
            if (r1 == 0) goto L_0x0402
            r1 = r7[r2]
            r8 = r1
            org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r8 = (org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation) r8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "avatar_"
            r1.append(r4)
            long r4 = r8.id
            r1.append(r4)
            java.lang.String r5 = r1.toString()
            r9 = r8
            r8 = r5
        L_0x0306:
            boolean r1 = r0 instanceof org.telegram.messenger.MessageObject
            if (r1 == 0) goto L_0x0323
            r1 = r0
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            int r4 = r1.getRealId()
            if (r4 >= 0) goto L_0x0323
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            if (r4 == 0) goto L_0x0323
            org.telegram.tgnet.TLRPC$Message r4 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r4.webpage
            r10 = r0
            goto L_0x0324
        L_0x0323:
            r10 = r0
        L_0x0324:
            java.lang.String r11 = getKeyForParentObject(r10)
            if (r11 != 0) goto L_0x032e
            r6.sendErrorToObject(r7, r2)
            return
        L_0x032e:
            org.telegram.messenger.FileRefController$Requester r0 = new org.telegram.messenger.FileRefController$Requester
            r1 = 0
            r0.<init>()
            r12 = r0
            java.lang.Object[] unused = r12.args = r7
            org.telegram.tgnet.TLRPC.InputFileLocation unused = r12.location = r9
            java.lang.String unused = r12.locationKey = r8
            r0 = 0
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r1 = r6.locationRequester
            java.lang.Object r1 = r1.get(r8)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 != 0) goto L_0x0356
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r1 = r2
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r6.locationRequester
            r2.put(r8, r1)
            int r0 = r0 + 1
        L_0x0356:
            r1.add(r12)
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r6.parentRequester
            java.lang.Object r2 = r2.get(r11)
            r1 = r2
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 != 0) goto L_0x0374
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r1 = r2
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r2 = r6.parentRequester
            r2.put(r11, r1)
            int r0 = r0 + 1
            r13 = r0
            r14 = r1
            goto L_0x0376
        L_0x0374:
            r13 = r0
            r14 = r1
        L_0x0376:
            r14.add(r12)
            if (r13 == r3) goto L_0x037c
            return
        L_0x037c:
            r0 = r8
            boolean r1 = r10 instanceof java.lang.String
            if (r1 == 0) goto L_0x03c0
            r1 = r10
            java.lang.String r1 = (java.lang.String) r1
            java.lang.String r2 = "wallpaper"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x0390
            java.lang.String r0 = "wallpaper"
            r15 = r0
            goto L_0x03c1
        L_0x0390:
            java.lang.String r2 = "gif"
            boolean r2 = r1.startsWith(r2)
            if (r2 == 0) goto L_0x039c
            java.lang.String r0 = "gif"
            r15 = r0
            goto L_0x03c1
        L_0x039c:
            java.lang.String r2 = "recent"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x03a8
            java.lang.String r0 = "recent"
            r15 = r0
            goto L_0x03c1
        L_0x03a8:
            java.lang.String r2 = "fav"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x03b4
            java.lang.String r0 = "fav"
            r15 = r0
            goto L_0x03c1
        L_0x03b4:
            java.lang.String r2 = "update"
            boolean r2 = r2.equals(r1)
            if (r2 == 0) goto L_0x03c0
            java.lang.String r0 = "update"
            r15 = r0
            goto L_0x03c1
        L_0x03c0:
            r15 = r0
        L_0x03c1:
            r17.cleanupCache()
            org.telegram.messenger.FileRefController$CachedResult r16 = r6.getCachedResponse(r15)
            if (r16 == 0) goto L_0x03e1
            org.telegram.tgnet.TLObject r3 = r16.response
            r4 = 0
            r5 = 1
            r0 = r17
            r1 = r8
            r2 = r11
            boolean r0 = r0.onRequestComplete(r1, r2, r3, r4, r5)
            if (r0 != 0) goto L_0x03e0
            java.util.HashMap<java.lang.String, org.telegram.messenger.FileRefController$CachedResult> r0 = r6.responseCache
            r0.remove(r8)
            goto L_0x03fe
        L_0x03e0:
            return
        L_0x03e1:
            org.telegram.messenger.FileRefController$CachedResult r16 = r6.getCachedResponse(r11)
            if (r16 == 0) goto L_0x03fe
            org.telegram.tgnet.TLObject r3 = r16.response
            r4 = 0
            r5 = 1
            r0 = r17
            r1 = r8
            r2 = r11
            boolean r0 = r0.onRequestComplete(r1, r2, r3, r4, r5)
            if (r0 != 0) goto L_0x03fd
            java.util.HashMap<java.lang.String, org.telegram.messenger.FileRefController$CachedResult> r0 = r6.responseCache
            r0.remove(r11)
            goto L_0x03fe
        L_0x03fd:
            return
        L_0x03fe:
            r6.requestReferenceFromServer(r10, r8, r11, r7)
            return
        L_0x0402:
            r6.sendErrorToObject(r7, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.requestReference(java.lang.Object, java.lang.Object[]):void");
    }

    private void broadcastWaitersData(ArrayList<Waiter> waiters, TLObject response) {
        int a = 0;
        int N = waiters.size();
        while (a < N) {
            Waiter waiter = waiters.get(a);
            onRequestComplete(waiter.locationKey, waiter.parentKey, response, a == N + -1, false);
            a++;
        }
        waiters.clear();
    }

    private void requestReferenceFromServer(Object parentObject, String locationKey, String parentKey, Object[] args) {
        if (parentObject instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) parentObject;
            long channelId = messageObject.getChannelId();
            if (messageObject.scheduled) {
                TLRPC.TL_messages_getScheduledMessages req = new TLRPC.TL_messages_getScheduledMessages();
                req.peer = getMessagesController().getInputPeer(messageObject.getDialogId());
                req.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req, new FileRefController$$ExternalSyntheticLambda5(this, locationKey, parentKey));
            } else if (channelId != 0) {
                TLRPC.TL_channels_getMessages req2 = new TLRPC.TL_channels_getMessages();
                req2.channel = getMessagesController().getInputChannel(channelId);
                req2.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req2, new FileRefController$$ExternalSyntheticLambda6(this, locationKey, parentKey));
            } else {
                TLRPC.TL_messages_getMessages req3 = new TLRPC.TL_messages_getMessages();
                req3.id.add(Integer.valueOf(messageObject.getRealId()));
                getConnectionsManager().sendRequest(req3, new FileRefController$$ExternalSyntheticLambda15(this, locationKey, parentKey));
            }
        } else if (parentObject instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper wallPaper = (TLRPC.TL_wallPaper) parentObject;
            TLRPC.TL_account_getWallPaper req4 = new TLRPC.TL_account_getWallPaper();
            TLRPC.TL_inputWallPaper inputWallPaper = new TLRPC.TL_inputWallPaper();
            inputWallPaper.id = wallPaper.id;
            inputWallPaper.access_hash = wallPaper.access_hash;
            req4.wallpaper = inputWallPaper;
            getConnectionsManager().sendRequest(req4, new FileRefController$$ExternalSyntheticLambda17(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme theme = (TLRPC.TL_theme) parentObject;
            TLRPC.TL_account_getTheme req5 = new TLRPC.TL_account_getTheme();
            TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
            inputTheme.id = theme.id;
            inputTheme.access_hash = theme.access_hash;
            req5.theme = inputTheme;
            req5.format = "android";
            getConnectionsManager().sendRequest(req5, new FileRefController$$ExternalSyntheticLambda18(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.WebPage) {
            TLRPC.TL_messages_getWebPage req6 = new TLRPC.TL_messages_getWebPage();
            req6.url = ((TLRPC.WebPage) parentObject).url;
            req6.hash = 0;
            getConnectionsManager().sendRequest(req6, new FileRefController$$ExternalSyntheticLambda19(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.User) {
            TLRPC.TL_users_getUsers req7 = new TLRPC.TL_users_getUsers();
            req7.id.add(getMessagesController().getInputUser((TLRPC.User) parentObject));
            getConnectionsManager().sendRequest(req7, new FileRefController$$ExternalSyntheticLambda20(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) parentObject;
            if (chat instanceof TLRPC.TL_chat) {
                TLRPC.TL_messages_getChats req8 = new TLRPC.TL_messages_getChats();
                req8.id.add(Long.valueOf(chat.id));
                getConnectionsManager().sendRequest(req8, new FileRefController$$ExternalSyntheticLambda21(this, locationKey, parentKey));
            } else if (chat instanceof TLRPC.TL_channel) {
                TLRPC.TL_channels_getChannels req9 = new TLRPC.TL_channels_getChannels();
                req9.id.add(MessagesController.getInputChannel(chat));
                getConnectionsManager().sendRequest(req9, new FileRefController$$ExternalSyntheticLambda23(this, locationKey, parentKey));
            }
        } else if (parentObject instanceof String) {
            String string = (String) parentObject;
            if ("wallpaper".equals(string)) {
                if (this.wallpaperWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_account_getWallPapers(), new FileRefController$$ExternalSyntheticLambda4(this));
                }
                this.wallpaperWaiters.add(new Waiter(locationKey, parentKey));
            } else if (string.startsWith("gif")) {
                if (this.savedGifsWaiters.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_messages_getSavedGifs(), new FileRefController$$ExternalSyntheticLambda1(this));
                }
                this.savedGifsWaiters.add(new Waiter(locationKey, parentKey));
            } else if ("recent".equals(string)) {
                if (this.recentStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_messages_getRecentStickers(), new FileRefController$$ExternalSyntheticLambda2(this));
                }
                this.recentStickersWaiter.add(new Waiter(locationKey, parentKey));
            } else if ("fav".equals(string)) {
                if (this.favStickersWaiter.isEmpty()) {
                    getConnectionsManager().sendRequest(new TLRPC.TL_messages_getFavedStickers(), new FileRefController$$ExternalSyntheticLambda3(this));
                }
                this.favStickersWaiter.add(new Waiter(locationKey, parentKey));
            } else if ("update".equals(string)) {
                TLRPC.TL_help_getAppUpdate req10 = new TLRPC.TL_help_getAppUpdate();
                try {
                    req10.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
                } catch (Exception e) {
                }
                if (req10.source == null) {
                    req10.source = "";
                }
                getConnectionsManager().sendRequest(req10, new FileRefController$$ExternalSyntheticLambda7(this, locationKey, parentKey));
            } else if (string.startsWith("avatar_")) {
                long id = Utilities.parseLong(string).longValue();
                if (id > 0) {
                    TLRPC.TL_photos_getUserPhotos req11 = new TLRPC.TL_photos_getUserPhotos();
                    req11.limit = 80;
                    req11.offset = 0;
                    req11.max_id = 0;
                    req11.user_id = getMessagesController().getInputUser(id);
                    getConnectionsManager().sendRequest(req11, new FileRefController$$ExternalSyntheticLambda8(this, locationKey, parentKey));
                    return;
                }
                TLRPC.TL_messages_search req12 = new TLRPC.TL_messages_search();
                req12.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
                req12.limit = 80;
                req12.offset_id = 0;
                req12.q = "";
                req12.peer = getMessagesController().getInputPeer(id);
                getConnectionsManager().sendRequest(req12, new FileRefController$$ExternalSyntheticLambda9(this, locationKey, parentKey));
            } else if (string.startsWith("sent_")) {
                String[] params = string.split("_");
                if (params.length == 3) {
                    long channelId2 = Utilities.parseLong(params[1]).longValue();
                    if (channelId2 != 0) {
                        TLRPC.TL_channels_getMessages req13 = new TLRPC.TL_channels_getMessages();
                        req13.channel = getMessagesController().getInputChannel(channelId2);
                        req13.id.add(Utilities.parseInt(params[2]));
                        getConnectionsManager().sendRequest(req13, new FileRefController$$ExternalSyntheticLambda10(this, locationKey, parentKey));
                        return;
                    }
                    TLRPC.TL_messages_getMessages req14 = new TLRPC.TL_messages_getMessages();
                    req14.id.add(Utilities.parseInt(params[2]));
                    getConnectionsManager().sendRequest(req14, new FileRefController$$ExternalSyntheticLambda12(this, locationKey, parentKey));
                    return;
                }
                sendErrorToObject(args, 0);
            } else {
                sendErrorToObject(args, 0);
            }
        } else if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet stickerSet = (TLRPC.TL_messages_stickerSet) parentObject;
            TLRPC.TL_messages_getStickerSet req15 = new TLRPC.TL_messages_getStickerSet();
            req15.stickerset = new TLRPC.TL_inputStickerSetID();
            req15.stickerset.id = stickerSet.set.id;
            req15.stickerset.access_hash = stickerSet.set.access_hash;
            getConnectionsManager().sendRequest(req15, new FileRefController$$ExternalSyntheticLambda13(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.StickerSetCovered) {
            TLRPC.StickerSetCovered stickerSet2 = (TLRPC.StickerSetCovered) parentObject;
            TLRPC.TL_messages_getStickerSet req16 = new TLRPC.TL_messages_getStickerSet();
            req16.stickerset = new TLRPC.TL_inputStickerSetID();
            req16.stickerset.id = stickerSet2.set.id;
            req16.stickerset.access_hash = stickerSet2.set.access_hash;
            getConnectionsManager().sendRequest(req16, new FileRefController$$ExternalSyntheticLambda14(this, locationKey, parentKey));
        } else if (parentObject instanceof TLRPC.InputStickerSet) {
            TLRPC.TL_messages_getStickerSet req17 = new TLRPC.TL_messages_getStickerSet();
            req17.stickerset = (TLRPC.InputStickerSet) parentObject;
            getConnectionsManager().sendRequest(req17, new FileRefController$$ExternalSyntheticLambda16(this, locationKey, parentKey));
        } else {
            sendErrorToObject(args, 0);
        }
    }

    /* renamed from: lambda$requestReferenceFromServer$0$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m574xedaf0ec8(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$1$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m575xvar_e7(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$2$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m586xfCLASSNAME(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$3$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m588x3deae25(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$4$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m589xb43e344(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$5$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m590x12a91863(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$6$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m591x1a0e4d82(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$7$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m592x217382a1(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$8$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m593x28d8b7c0(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$9$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m594x303decdf(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.wallpaperWaiters, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$10$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m576x3c3ad4fd(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.savedGifsWaiters, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$11$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m577x43a00a1c(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.recentStickersWaiter, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$12$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m578x4b053f3b(TLObject response, TLRPC.TL_error error) {
        broadcastWaitersData(this.favStickersWaiter, response);
    }

    /* renamed from: lambda$requestReferenceFromServer$13$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m579x526a745a(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$14$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m580x59cfa979(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$15$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m581x6134de98(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$16$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m582x689a13b7(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$17$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m583x6ffvar_d6(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, false, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$18$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m584x77647df5(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$19$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m585x7ec9b314(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    /* renamed from: lambda$requestReferenceFromServer$20$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m587x217CLASSNAMEbe(String locationKey, String parentKey, TLObject response, TLRPC.TL_error error) {
        onRequestComplete(locationKey, parentKey, response, true, false);
    }

    private boolean isSameReference(byte[] oldRef, byte[] newRef) {
        return Arrays.equals(oldRef, newRef);
    }

    private boolean onUpdateObjectReference(Requester requester, byte[] file_reference, TLRPC.InputFileLocation locationReplacement, boolean fromCache) {
        if (BuildVars.DEBUG_VERSION) {
            FileLog.d("fileref updated for " + requester.args[0] + " " + requester.locationKey);
        }
        if (requester.args[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.TL_messages_sendMultiMedia multiMedia = (TLRPC.TL_messages_sendMultiMedia) requester.args[1];
            Object[] objects = this.multiMediaCache.get(multiMedia);
            if (objects == null) {
                return true;
            }
            TLRPC.TL_inputSingleMedia req = (TLRPC.TL_inputSingleMedia) requester.args[0];
            if (req.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument = (TLRPC.TL_inputMediaDocument) req.media;
                if (fromCache && isSameReference(mediaDocument.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument.id.file_reference = file_reference;
            } else if (req.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto = (TLRPC.TL_inputMediaPhoto) req.media;
                if (fromCache && isSameReference(mediaPhoto.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto.id.file_reference = file_reference;
            }
            int index = multiMedia.multi_media.indexOf(req);
            if (index < 0) {
                return true;
            }
            ArrayList<Object> parentObjects = (ArrayList) objects[3];
            parentObjects.set(index, (Object) null);
            boolean done = true;
            for (int a = 0; a < parentObjects.size(); a++) {
                if (parentObjects.get(a) != null) {
                    done = false;
                }
            }
            if (done) {
                this.multiMediaCache.remove(multiMedia);
                AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda28(this, multiMedia, objects));
            }
        } else if (requester.args[0] instanceof TLRPC.TL_messages_sendMedia) {
            TLRPC.TL_messages_sendMedia req2 = (TLRPC.TL_messages_sendMedia) requester.args[0];
            if (req2.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument2 = (TLRPC.TL_inputMediaDocument) req2.media;
                if (fromCache && isSameReference(mediaDocument2.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument2.id.file_reference = file_reference;
            } else if (req2.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto2 = (TLRPC.TL_inputMediaPhoto) req2.media;
                if (fromCache && isSameReference(mediaPhoto2.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto2.id.file_reference = file_reference;
            }
            AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda0(this, requester));
        } else if (requester.args[0] instanceof TLRPC.TL_messages_editMessage) {
            TLRPC.TL_messages_editMessage req3 = (TLRPC.TL_messages_editMessage) requester.args[0];
            if (req3.media instanceof TLRPC.TL_inputMediaDocument) {
                TLRPC.TL_inputMediaDocument mediaDocument3 = (TLRPC.TL_inputMediaDocument) req3.media;
                if (fromCache && isSameReference(mediaDocument3.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument3.id.file_reference = file_reference;
            } else if (req3.media instanceof TLRPC.TL_inputMediaPhoto) {
                TLRPC.TL_inputMediaPhoto mediaPhoto3 = (TLRPC.TL_inputMediaPhoto) req3.media;
                if (fromCache && isSameReference(mediaPhoto3.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto3.id.file_reference = file_reference;
            }
            AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda11(this, requester));
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif req4 = (TLRPC.TL_messages_saveGif) requester.args[0];
            if (fromCache && isSameReference(req4.id.file_reference, file_reference)) {
                return false;
            }
            req4.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req4, FileRefController$$ExternalSyntheticLambda24.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker req5 = (TLRPC.TL_messages_saveRecentSticker) requester.args[0];
            if (fromCache && isSameReference(req5.id.file_reference, file_reference)) {
                return false;
            }
            req5.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req5, FileRefController$$ExternalSyntheticLambda25.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker req6 = (TLRPC.TL_messages_faveSticker) requester.args[0];
            if (fromCache && isSameReference(req6.id.file_reference, file_reference)) {
                return false;
            }
            req6.id.file_reference = file_reference;
            getConnectionsManager().sendRequest(req6, FileRefController$$ExternalSyntheticLambda26.INSTANCE);
        } else if (requester.args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            TLRPC.TL_messages_getAttachedStickers req7 = (TLRPC.TL_messages_getAttachedStickers) requester.args[0];
            if (req7.media instanceof TLRPC.TL_inputStickeredMediaDocument) {
                TLRPC.TL_inputStickeredMediaDocument mediaDocument4 = (TLRPC.TL_inputStickeredMediaDocument) req7.media;
                if (fromCache && isSameReference(mediaDocument4.id.file_reference, file_reference)) {
                    return false;
                }
                mediaDocument4.id.file_reference = file_reference;
            } else if (req7.media instanceof TLRPC.TL_inputStickeredMediaPhoto) {
                TLRPC.TL_inputStickeredMediaPhoto mediaPhoto4 = (TLRPC.TL_inputStickeredMediaPhoto) req7.media;
                if (fromCache && isSameReference(mediaPhoto4.id.file_reference, file_reference)) {
                    return false;
                }
                mediaPhoto4.id.file_reference = file_reference;
            }
            getConnectionsManager().sendRequest(req7, (RequestDelegate) requester.args[1]);
        } else if (requester.args[1] instanceof FileLoadOperation) {
            FileLoadOperation fileLoadOperation = (FileLoadOperation) requester.args[1];
            if (locationReplacement != null) {
                if (fromCache && isSameReference(fileLoadOperation.location.file_reference, locationReplacement.file_reference)) {
                    return false;
                }
                fileLoadOperation.location = locationReplacement;
            } else if (fromCache && isSameReference(requester.location.file_reference, file_reference)) {
                return false;
            } else {
                requester.location.file_reference = file_reference;
            }
            fileLoadOperation.requestingReference = false;
            fileLoadOperation.startDownloadRequest();
        }
        return true;
    }

    /* renamed from: lambda$onUpdateObjectReference$21$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m571xcb28ae7e(TLRPC.TL_messages_sendMultiMedia multiMedia, Object[] objects) {
        TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = multiMedia;
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, objects[1], objects[2], (ArrayList<Object>) null, objects[4], objects[5].booleanValue());
    }

    /* renamed from: lambda$onUpdateObjectReference$22$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m572xd28de39d(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, (HashMap<String, String>) null, ((Boolean) requester.args[6]).booleanValue());
    }

    /* renamed from: lambda$onUpdateObjectReference$23$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m573xd9var_bc(Requester requester) {
        getSendMessagesHelper().performSendMessageRequest((TLObject) requester.args[0], (MessageObject) requester.args[1], (String) requester.args[2], (SendMessagesHelper.DelayedMessage) requester.args[3], ((Boolean) requester.args[4]).booleanValue(), (SendMessagesHelper.DelayedMessage) requester.args[5], (Object) null, (HashMap<String, String>) null, ((Boolean) requester.args[6]).booleanValue());
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$24(TLObject response, TLRPC.TL_error error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$25(TLObject response, TLRPC.TL_error error) {
    }

    static /* synthetic */ void lambda$onUpdateObjectReference$26(TLObject response, TLRPC.TL_error error) {
    }

    private void sendErrorToObject(Object[] args, int reason) {
        if (args[0] instanceof TLRPC.TL_inputSingleMedia) {
            TLRPC.TL_messages_sendMultiMedia req = args[1];
            Object[] objects = this.multiMediaCache.get(req);
            if (objects != null) {
                this.multiMediaCache.remove(req);
                AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda29(this, req, objects));
            }
        } else if ((args[0] instanceof TLRPC.TL_messages_sendMedia) || (args[0] instanceof TLRPC.TL_messages_editMessage)) {
            AndroidUtilities.runOnUIThread(new FileRefController$$ExternalSyntheticLambda32(this, args));
        } else if (args[0] instanceof TLRPC.TL_messages_saveGif) {
            TLRPC.TL_messages_saveGif tL_messages_saveGif = args[0];
        } else if (args[0] instanceof TLRPC.TL_messages_saveRecentSticker) {
            TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = args[0];
        } else if (args[0] instanceof TLRPC.TL_messages_faveSticker) {
            TLRPC.TL_messages_faveSticker tL_messages_faveSticker = args[0];
        } else if (args[0] instanceof TLRPC.TL_messages_getAttachedStickers) {
            getConnectionsManager().sendRequest(args[0], args[1]);
        } else if (reason == 0) {
            TLRPC.TL_error error = new TLRPC.TL_error();
            error.text = "not found parent object to request reference";
            error.code = 400;
            if (args[1] instanceof FileLoadOperation) {
                FileLoadOperation fileLoadOperation = args[1];
                fileLoadOperation.requestingReference = false;
                fileLoadOperation.processRequestResult(args[2], error);
            }
        } else if (reason == 1 && (args[1] instanceof FileLoadOperation)) {
            FileLoadOperation fileLoadOperation2 = args[1];
            fileLoadOperation2.requestingReference = false;
            fileLoadOperation2.onFail(false, 0);
        }
    }

    /* renamed from: lambda$sendErrorToObject$27$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m595xf2fa116e(TLRPC.TL_messages_sendMultiMedia req, Object[] objects) {
        TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia = req;
        getSendMessagesHelper().performSendMessageRequestMulti(tL_messages_sendMultiMedia, objects[1], objects[2], (ArrayList<Object>) null, objects[4], objects[5].booleanValue());
    }

    /* renamed from: lambda$sendErrorToObject$28$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m596xfa5var_d(Object[] args) {
        getSendMessagesHelper().performSendMessageRequest(args[0], args[1], args[2], args[3], args[4].booleanValue(), args[5], (Object) null, (HashMap<String, String>) null, args[6].booleanValue());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v5, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v6, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v36, resolved type: boolean} */
    /* JADX WARNING: type inference failed for: r12v0 */
    /* JADX WARNING: type inference failed for: r12v37 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r12v1, types: [int, boolean] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0164 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0182 A[LOOP:2: B:53:0x00d8->B:80:0x0182, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean onRequestComplete(java.lang.String r29, java.lang.String r30, org.telegram.tgnet.TLObject r31, boolean r32, boolean r33) {
        /*
            r28 = this;
            r6 = r28
            r7 = r29
            r8 = r30
            r9 = r31
            r0 = 0
            r1 = r30
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_account_wallPapers
            if (r2 == 0) goto L_0x0013
            java.lang.String r1 = "wallpaper"
            r10 = r1
            goto L_0x002c
        L_0x0013:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_savedGifs
            if (r2 == 0) goto L_0x001b
            java.lang.String r1 = "gif"
            r10 = r1
            goto L_0x002c
        L_0x001b:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_recentStickers
            if (r2 == 0) goto L_0x0023
            java.lang.String r1 = "recent"
            r10 = r1
            goto L_0x002c
        L_0x0023:
            boolean r2 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_favedStickers
            if (r2 == 0) goto L_0x002b
            java.lang.String r1 = "fav"
            r10 = r1
            goto L_0x002c
        L_0x002b:
            r10 = r1
        L_0x002c:
            r12 = 1
            if (r8 == 0) goto L_0x007f
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r1 = r6.parentRequester
            java.lang.Object r1 = r1.get(r8)
            r13 = r1
            java.util.ArrayList r13 = (java.util.ArrayList) r13
            if (r13 == 0) goto L_0x007f
            r1 = 0
            int r14 = r13.size()
            r15 = r0
            r5 = r1
        L_0x0041:
            if (r5 >= r14) goto L_0x0072
            java.lang.Object r0 = r13.get(r5)
            r16 = r0
            org.telegram.messenger.FileRefController$Requester r16 = (org.telegram.messenger.FileRefController.Requester) r16
            boolean r0 = r16.completed
            if (r0 == 0) goto L_0x0054
            r17 = r5
            goto L_0x006f
        L_0x0054:
            java.lang.String r1 = r16.locationKey
            r2 = 0
            if (r32 == 0) goto L_0x005f
            if (r15 != 0) goto L_0x005f
            r4 = 1
            goto L_0x0060
        L_0x005f:
            r4 = 0
        L_0x0060:
            r0 = r28
            r3 = r31
            r17 = r5
            r5 = r33
            boolean r0 = r0.onRequestComplete(r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x006f
            r15 = 1
        L_0x006f:
            int r5 = r17 + 1
            goto L_0x0041
        L_0x0072:
            r17 = r5
            if (r15 == 0) goto L_0x0079
            r6.putReponseToCache(r10, r9)
        L_0x0079:
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r0 = r6.parentRequester
            r0.remove(r8)
            r0 = r15
        L_0x007f:
            r1 = 0
            r2 = 0
            r3 = 0
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r4 = r6.locationRequester
            java.lang.Object r4 = r4.get(r7)
            java.util.ArrayList r4 = (java.util.ArrayList) r4
            if (r4 != 0) goto L_0x008d
            return r0
        L_0x008d:
            r5 = r29
            r10 = 0
            int r13 = r4.size()
        L_0x0094:
            if (r10 >= r13) goto L_0x04c1
            java.lang.Object r14 = r4.get(r10)
            org.telegram.messenger.FileRefController$Requester r14 = (org.telegram.messenger.FileRefController.Requester) r14
            boolean r15 = r14.completed
            if (r15 == 0) goto L_0x00ab
            r19 = r4
            r21 = r13
            r8 = 0
            r4 = r33
            goto L_0x04b7
        L_0x00ab:
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputFileLocation
            if (r15 != 0) goto L_0x00bb
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation
            if (r15 == 0) goto L_0x00bf
        L_0x00bb:
            org.telegram.tgnet.TLRPC$InputFileLocation[] r2 = new org.telegram.tgnet.TLRPC.InputFileLocation[r12]
            boolean[] r3 = new boolean[r12]
        L_0x00bf:
            boolean unused = r14.completed = r12
            boolean r15 = r9 instanceof org.telegram.tgnet.TLRPC.messages_Messages
            if (r15 == 0) goto L_0x01c2
            r15 = r9
            org.telegram.tgnet.TLRPC$messages_Messages r15 = (org.telegram.tgnet.TLRPC.messages_Messages) r15
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r12 = r15.messages
            boolean r12 = r12.isEmpty()
            if (r12 != 0) goto L_0x01bb
            r12 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r11 = r15.messages
            int r11 = r11.size()
        L_0x00d8:
            if (r12 >= r11) goto L_0x0192
            r18 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r1 = r15.messages
            java.lang.Object r1 = r1.get(r12)
            org.telegram.tgnet.TLRPC$Message r1 = (org.telegram.tgnet.TLRPC.Message) r1
            r19 = r4
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            if (r4 == 0) goto L_0x014d
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            if (r4 == 0) goto L_0x00fd
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0162
        L_0x00fd:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            if (r4 == 0) goto L_0x0127
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            org.telegram.tgnet.TLRPC$Document r4 = r4.document
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r4 != 0) goto L_0x0124
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r1.media
            org.telegram.tgnet.TLRPC$TL_game r8 = r8.game
            org.telegram.tgnet.TLRPC$Photo r8 = r8.photo
            r18 = r4
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Photo) r8, (org.telegram.tgnet.TLRPC.InputFileLocation) r4, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0162
        L_0x0124:
            r18 = r4
            goto L_0x0162
        L_0x0127:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            if (r4 == 0) goto L_0x013a
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Photo) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0162
        L_0x013a:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            if (r4 == 0) goto L_0x0160
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.WebPage) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0162
        L_0x014d:
            org.telegram.tgnet.TLRPC$MessageAction r4 = r1.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r4 == 0) goto L_0x0160
            org.telegram.tgnet.TLRPC$MessageAction r4 = r1.action
            org.telegram.tgnet.TLRPC$Photo r4 = r4.photo
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r4 = r6.getFileReference((org.telegram.tgnet.TLRPC.Photo) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            goto L_0x0162
        L_0x0160:
            r4 = r18
        L_0x0162:
            if (r4 == 0) goto L_0x0182
            if (r32 == 0) goto L_0x0179
            org.telegram.messenger.MessagesStorage r8 = r28.getMessagesStorage()
            r18 = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r4 = r15.users
            r20 = r11
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r11 = r15.chats
            r21 = r13
            r13 = 0
            r8.replaceMessageIfExists(r1, r4, r11, r13)
            goto L_0x017f
        L_0x0179:
            r18 = r4
            r20 = r11
            r21 = r13
        L_0x017f:
            r1 = r18
            goto L_0x019a
        L_0x0182:
            r18 = r4
            r20 = r11
            r21 = r13
            int r12 = r12 + 1
            r8 = r30
            r1 = r18
            r4 = r19
            goto L_0x00d8
        L_0x0192:
            r18 = r1
            r19 = r4
            r20 = r11
            r21 = r13
        L_0x019a:
            if (r1 != 0) goto L_0x01bf
            org.telegram.messenger.MessagesStorage r4 = r28.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Message> r8 = r15.messages
            r11 = 0
            java.lang.Object r8 = r8.get(r11)
            org.telegram.tgnet.TLRPC$Message r8 = (org.telegram.tgnet.TLRPC.Message) r8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$User> r11 = r15.users
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat> r12 = r15.chats
            r13 = 1
            r4.replaceMessageIfExists(r8, r11, r12, r13)
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r4 == 0) goto L_0x01bf
            java.lang.String r4 = "file ref not found in messages, replacing message"
            org.telegram.messenger.FileLog.d(r4)
            goto L_0x01bf
        L_0x01bb:
            r19 = r4
            r21 = r13
        L_0x01bf:
            r4 = 0
            goto L_0x0495
        L_0x01c2:
            r19 = r4
            r21 = r13
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_help_appUpdate
            if (r4 == 0) goto L_0x01e6
            r4 = r9
            org.telegram.tgnet.TLRPC$TL_help_appUpdate r4 = (org.telegram.tgnet.TLRPC.TL_help_appUpdate) r4
            org.telegram.tgnet.TLRPC$Document r8 = r4.document
            org.telegram.tgnet.TLRPC$InputFileLocation r11 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r8, (org.telegram.tgnet.TLRPC.InputFileLocation) r11, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 != 0) goto L_0x01e3
            org.telegram.tgnet.TLRPC$Document r8 = r4.sticker
            org.telegram.tgnet.TLRPC$InputFileLocation r11 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r8, (org.telegram.tgnet.TLRPC.InputFileLocation) r11, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
        L_0x01e3:
            r4 = 0
            goto L_0x0495
        L_0x01e6:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.WebPage
            if (r4 == 0) goto L_0x01f8
            r4 = r9
            org.telegram.tgnet.TLRPC$WebPage r4 = (org.telegram.tgnet.TLRPC.WebPage) r4
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.WebPage) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r8, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            r4 = 0
            goto L_0x0495
        L_0x01f8:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_account_wallPapers
            if (r4 == 0) goto L_0x0231
            r4 = r9
            org.telegram.tgnet.TLRPC$TL_account_wallPapers r4 = (org.telegram.tgnet.TLRPC.TL_account_wallPapers) r4
            r8 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r11 = r4.wallpapers
            int r11 = r11.size()
        L_0x0206:
            if (r8 >= r11) goto L_0x0220
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r12 = r4.wallpapers
            java.lang.Object r12 = r12.get(r8)
            org.telegram.tgnet.TLRPC$WallPaper r12 = (org.telegram.tgnet.TLRPC.WallPaper) r12
            org.telegram.tgnet.TLRPC$Document r12 = r12.document
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x021d
            goto L_0x0220
        L_0x021d:
            int r8 = r8 + 1
            goto L_0x0206
        L_0x0220:
            if (r1 == 0) goto L_0x022e
            if (r32 == 0) goto L_0x022e
            org.telegram.messenger.MessagesStorage r8 = r28.getMessagesStorage()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$WallPaper> r11 = r4.wallpapers
            r12 = 1
            r8.putWallpapers(r11, r12)
        L_0x022e:
            r4 = 0
            goto L_0x0495
        L_0x0231:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_wallPaper
            if (r4 == 0) goto L_0x0259
            r4 = r9
            org.telegram.tgnet.TLRPC$TL_wallPaper r4 = (org.telegram.tgnet.TLRPC.TL_wallPaper) r4
            org.telegram.tgnet.TLRPC$Document r8 = r4.document
            org.telegram.tgnet.TLRPC$InputFileLocation r11 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r8, (org.telegram.tgnet.TLRPC.InputFileLocation) r11, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0256
            if (r32 == 0) goto L_0x0256
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            r8.add(r4)
            org.telegram.messenger.MessagesStorage r11 = r28.getMessagesStorage()
            r12 = 0
            r11.putWallpapers(r8, r12)
        L_0x0256:
            r4 = 0
            goto L_0x0495
        L_0x0259:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_theme
            if (r4 == 0) goto L_0x0279
            r4 = r9
            org.telegram.tgnet.TLRPC$TL_theme r4 = (org.telegram.tgnet.TLRPC.TL_theme) r4
            org.telegram.tgnet.TLRPC$Document r8 = r4.document
            org.telegram.tgnet.TLRPC$InputFileLocation r11 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r8, (org.telegram.tgnet.TLRPC.InputFileLocation) r11, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0276
            if (r32 == 0) goto L_0x0276
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda33 r8 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda33
            r8.<init>(r4)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8)
        L_0x0276:
            r4 = 0
            goto L_0x0495
        L_0x0279:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.Vector
            if (r4 == 0) goto L_0x031f
            r4 = r9
            org.telegram.tgnet.TLRPC$Vector r4 = (org.telegram.tgnet.TLRPC.Vector) r4
            java.util.ArrayList<java.lang.Object> r8 = r4.objects
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x031a
            r8 = 0
            java.util.ArrayList<java.lang.Object> r11 = r4.objects
            int r11 = r11.size()
        L_0x028f:
            if (r8 >= r11) goto L_0x0315
            java.util.ArrayList<java.lang.Object> r12 = r4.objects
            java.lang.Object r12 = r12.get(r8)
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC.User
            if (r13 == 0) goto L_0x02d3
            r13 = r12
            org.telegram.tgnet.TLRPC$User r13 = (org.telegram.tgnet.TLRPC.User) r13
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.User) r13, (org.telegram.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r32 == 0) goto L_0x02ca
            if (r1 == 0) goto L_0x02ca
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            r15.add(r13)
            r18 = r1
            org.telegram.messenger.MessagesStorage r1 = r28.getMessagesStorage()
            r20 = r4
            r22 = r11
            r4 = 0
            r11 = 1
            r1.putUsersAndChats(r15, r4, r11, r11)
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda31 r1 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda31
            r1.<init>(r6, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x02d0
        L_0x02ca:
            r18 = r1
            r20 = r4
            r22 = r11
        L_0x02d0:
            r1 = r18
            goto L_0x030a
        L_0x02d3:
            r20 = r4
            r22 = r11
            boolean r4 = r12 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r4 == 0) goto L_0x030a
            r4 = r12
            org.telegram.tgnet.TLRPC$Chat r4 = (org.telegram.tgnet.TLRPC.Chat) r4
            org.telegram.tgnet.TLRPC$InputFileLocation r11 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Chat) r4, (org.telegram.tgnet.TLRPC.InputFileLocation) r11, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r32 == 0) goto L_0x0306
            if (r1 == 0) goto L_0x0306
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r11.add(r4)
            org.telegram.messenger.MessagesStorage r13 = r28.getMessagesStorage()
            r18 = r1
            r1 = 1
            r15 = 0
            r13.putUsersAndChats(r15, r11, r1, r1)
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda22 r1 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda22
            r1.<init>(r6, r4)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x0308
        L_0x0306:
            r18 = r1
        L_0x0308:
            r1 = r18
        L_0x030a:
            if (r1 == 0) goto L_0x030d
            goto L_0x031c
        L_0x030d:
            int r8 = r8 + 1
            r4 = r20
            r11 = r22
            goto L_0x028f
        L_0x0315:
            r20 = r4
            r22 = r11
            goto L_0x031c
        L_0x031a:
            r20 = r4
        L_0x031c:
            r4 = 0
            goto L_0x0495
        L_0x031f:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_chats
            if (r4 == 0) goto L_0x0384
            r4 = r9
            org.telegram.tgnet.TLRPC$TL_messages_chats r4 = (org.telegram.tgnet.TLRPC.TL_messages_chats) r4
            java.util.ArrayList r8 = r4.chats
            boolean r8 = r8.isEmpty()
            if (r8 != 0) goto L_0x037f
            r8 = 0
            java.util.ArrayList r11 = r4.chats
            int r11 = r11.size()
        L_0x0335:
            if (r8 >= r11) goto L_0x037b
            java.util.ArrayList r12 = r4.chats
            java.lang.Object r12 = r12.get(r8)
            org.telegram.tgnet.TLRPC$Chat r12 = (org.telegram.tgnet.TLRPC.Chat) r12
            org.telegram.tgnet.TLRPC$InputFileLocation r13 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Chat) r12, (org.telegram.tgnet.TLRPC.InputFileLocation) r13, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0371
            if (r32 == 0) goto L_0x0369
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            r13.add(r12)
            org.telegram.messenger.MessagesStorage r15 = r28.getMessagesStorage()
            r17 = r1
            r18 = r4
            r1 = 1
            r4 = 0
            r15.putUsersAndChats(r4, r13, r1, r1)
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda27 r1 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda27
            r1.<init>(r6, r12)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            goto L_0x036e
        L_0x0369:
            r17 = r1
            r18 = r4
            r4 = 0
        L_0x036e:
            r1 = r17
            goto L_0x0382
        L_0x0371:
            r17 = r1
            r18 = r4
            r4 = 0
            int r8 = r8 + 1
            r4 = r18
            goto L_0x0335
        L_0x037b:
            r18 = r4
            r4 = 0
            goto L_0x0382
        L_0x037f:
            r18 = r4
            r4 = 0
        L_0x0382:
            goto L_0x0495
        L_0x0384:
            r4 = 0
            boolean r8 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_savedGifs
            if (r8 == 0) goto L_0x03c2
            r8 = r9
            org.telegram.tgnet.TLRPC$TL_messages_savedGifs r8 = (org.telegram.tgnet.TLRPC.TL_messages_savedGifs) r8
            r11 = 0
            java.util.ArrayList r12 = r8.gifs
            int r12 = r12.size()
        L_0x0393:
            if (r11 >= r12) goto L_0x03ab
            java.util.ArrayList r13 = r8.gifs
            java.lang.Object r13 = r13.get(r11)
            org.telegram.tgnet.TLRPC$Document r13 = (org.telegram.tgnet.TLRPC.Document) r13
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r13, (org.telegram.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x03a8
            goto L_0x03ab
        L_0x03a8:
            int r11 = r11 + 1
            goto L_0x0393
        L_0x03ab:
            if (r32 == 0) goto L_0x03c0
            org.telegram.messenger.MediaDataController r22 = r28.getMediaDataController()
            r23 = 0
            java.util.ArrayList r11 = r8.gifs
            r25 = 1
            r26 = 0
            r27 = 1
            r24 = r11
            r22.processLoadedRecentDocuments(r23, r24, r25, r26, r27)
        L_0x03c0:
            goto L_0x0495
        L_0x03c2:
            boolean r8 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_stickerSet
            if (r8 == 0) goto L_0x03f6
            r8 = r9
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC.TL_messages_stickerSet) r8
            if (r1 != 0) goto L_0x03ea
            r11 = 0
            java.util.ArrayList r12 = r8.documents
            int r12 = r12.size()
        L_0x03d2:
            if (r11 >= r12) goto L_0x03ea
            java.util.ArrayList r13 = r8.documents
            java.lang.Object r13 = r13.get(r11)
            org.telegram.tgnet.TLRPC$Document r13 = (org.telegram.tgnet.TLRPC.Document) r13
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r13, (org.telegram.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x03e7
            goto L_0x03ea
        L_0x03e7:
            int r11 = r11 + 1
            goto L_0x03d2
        L_0x03ea:
            if (r32 == 0) goto L_0x03f4
            org.telegram.messenger.FileRefController$$ExternalSyntheticLambda30 r11 = new org.telegram.messenger.FileRefController$$ExternalSyntheticLambda30
            r11.<init>(r6, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r11)
        L_0x03f4:
            goto L_0x0495
        L_0x03f6:
            boolean r8 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_recentStickers
            if (r8 == 0) goto L_0x0432
            r8 = r9
            org.telegram.tgnet.TLRPC$TL_messages_recentStickers r8 = (org.telegram.tgnet.TLRPC.TL_messages_recentStickers) r8
            r11 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r12 = r8.stickers
            int r12 = r12.size()
        L_0x0404:
            if (r11 >= r12) goto L_0x041c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r13 = r8.stickers
            java.lang.Object r13 = r13.get(r11)
            org.telegram.tgnet.TLRPC$Document r13 = (org.telegram.tgnet.TLRPC.Document) r13
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r13, (org.telegram.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0419
            goto L_0x041c
        L_0x0419:
            int r11 = r11 + 1
            goto L_0x0404
        L_0x041c:
            if (r32 == 0) goto L_0x0431
            org.telegram.messenger.MediaDataController r22 = r28.getMediaDataController()
            r23 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r11 = r8.stickers
            r25 = 0
            r26 = 0
            r27 = 1
            r24 = r11
            r22.processLoadedRecentDocuments(r23, r24, r25, r26, r27)
        L_0x0431:
            goto L_0x0495
        L_0x0432:
            boolean r8 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messages_favedStickers
            if (r8 == 0) goto L_0x046e
            r8 = r9
            org.telegram.tgnet.TLRPC$TL_messages_favedStickers r8 = (org.telegram.tgnet.TLRPC.TL_messages_favedStickers) r8
            r11 = 0
            java.util.ArrayList r12 = r8.stickers
            int r12 = r12.size()
        L_0x0440:
            if (r11 >= r12) goto L_0x0458
            java.util.ArrayList r13 = r8.stickers
            java.lang.Object r13 = r13.get(r11)
            org.telegram.tgnet.TLRPC$Document r13 = (org.telegram.tgnet.TLRPC.Document) r13
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Document) r13, (org.telegram.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0455
            goto L_0x0458
        L_0x0455:
            int r11 = r11 + 1
            goto L_0x0440
        L_0x0458:
            if (r32 == 0) goto L_0x0494
            org.telegram.messenger.MediaDataController r22 = r28.getMediaDataController()
            r23 = 2
            java.util.ArrayList r11 = r8.stickers
            r25 = 0
            r26 = 0
            r27 = 1
            r24 = r11
            r22.processLoadedRecentDocuments(r23, r24, r25, r26, r27)
            goto L_0x0494
        L_0x046e:
            boolean r8 = r9 instanceof org.telegram.tgnet.TLRPC.photos_Photos
            if (r8 == 0) goto L_0x0494
            r8 = r9
            org.telegram.tgnet.TLRPC$photos_Photos r8 = (org.telegram.tgnet.TLRPC.photos_Photos) r8
            r11 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r12 = r8.photos
            int r12 = r12.size()
        L_0x047c:
            if (r11 >= r12) goto L_0x0495
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Photo> r13 = r8.photos
            java.lang.Object r13 = r13.get(r11)
            org.telegram.tgnet.TLRPC$Photo r13 = (org.telegram.tgnet.TLRPC.Photo) r13
            org.telegram.tgnet.TLRPC$InputFileLocation r15 = r14.location
            byte[] r1 = r6.getFileReference((org.telegram.tgnet.TLRPC.Photo) r13, (org.telegram.tgnet.TLRPC.InputFileLocation) r15, (boolean[]) r3, (org.telegram.tgnet.TLRPC.InputFileLocation[]) r2)
            if (r1 == 0) goto L_0x0491
            goto L_0x0495
        L_0x0491:
            int r11 = r11 + 1
            goto L_0x047c
        L_0x0494:
        L_0x0495:
            if (r1 == 0) goto L_0x04ac
            if (r2 == 0) goto L_0x049d
            r8 = 0
            r12 = r2[r8]
            goto L_0x049f
        L_0x049d:
            r8 = 0
            r12 = r4
        L_0x049f:
            r4 = r33
            boolean r11 = r6.onUpdateObjectReference(r14, r1, r12, r4)
            if (r11 == 0) goto L_0x04aa
            r0 = 1
            r12 = 1
            goto L_0x04b7
        L_0x04aa:
            r12 = 1
            goto L_0x04b7
        L_0x04ac:
            r4 = r33
            r8 = 0
            java.lang.Object[] r11 = r14.args
            r12 = 1
            r6.sendErrorToObject(r11, r12)
        L_0x04b7:
            int r10 = r10 + 1
            r8 = r30
            r4 = r19
            r13 = r21
            goto L_0x0094
        L_0x04c1:
            r19 = r4
            r21 = r13
            r4 = r33
            java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.FileRefController$Requester>> r8 = r6.locationRequester
            r8.remove(r7)
            if (r0 == 0) goto L_0x04d1
            r6.putReponseToCache(r5, r9)
        L_0x04d1:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.onRequestComplete(java.lang.String, java.lang.String, org.telegram.tgnet.TLObject, boolean, boolean):boolean");
    }

    /* renamed from: lambda$onRequestComplete$30$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m567x819cd9c5(TLRPC.User user) {
        getMessagesController().putUser(user, false);
    }

    /* renamed from: lambda$onRequestComplete$31$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m568x89020ee4(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    /* renamed from: lambda$onRequestComplete$32$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m569x90674403(TLRPC.Chat chat) {
        getMessagesController().putChat(chat, false);
    }

    /* renamed from: lambda$onRequestComplete$33$org-telegram-messenger-FileRefController  reason: not valid java name */
    public /* synthetic */ void m570x97cCLASSNAME(TLRPC.TL_messages_stickerSet stickerSet) {
        getMediaDataController().replaceStickerSet(stickerSet);
    }

    private void cleanupCache() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastCleanupTime) >= 600000) {
            this.lastCleanupTime = SystemClock.elapsedRealtime();
            ArrayList<String> keysToDelete = null;
            for (Map.Entry<String, CachedResult> entry : this.responseCache.entrySet()) {
                if (Math.abs(SystemClock.elapsedRealtime() - entry.getValue().firstQueryTime) >= 600000) {
                    if (keysToDelete == null) {
                        keysToDelete = new ArrayList<>();
                    }
                    keysToDelete.add(entry.getKey());
                }
            }
            if (keysToDelete != null) {
                int size = keysToDelete.size();
                for (int a = 0; a < size; a++) {
                    this.responseCache.remove(keysToDelete.get(a));
                }
            }
        }
    }

    private CachedResult getCachedResponse(String key) {
        CachedResult cachedResult = this.responseCache.get(key);
        if (cachedResult == null || Math.abs(SystemClock.elapsedRealtime() - cachedResult.firstQueryTime) < 600000) {
            return cachedResult;
        }
        this.responseCache.remove(key);
        return null;
    }

    private void putReponseToCache(String key, TLObject response) {
        CachedResult cachedResult = this.responseCache.get(key);
        if (cachedResult == null) {
            cachedResult = new CachedResult();
            TLObject unused = cachedResult.response = response;
            long unused2 = cachedResult.firstQueryTime = SystemClock.uptimeMillis();
            this.responseCache.put(key, cachedResult);
        }
        long unused3 = cachedResult.lastQueryTime = SystemClock.uptimeMillis();
    }

    private byte[] getFileReference(TLRPC.Document document, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (document == null || location == null) {
            return null;
        }
        if (!(location instanceof TLRPC.TL_inputDocumentFileLocation)) {
            int a = 0;
            int size = document.thumbs.size();
            while (a < size) {
                TLRPC.PhotoSize photoSize = document.thumbs.get(a);
                byte[] result = getFileReference(photoSize, location, needReplacement);
                if (needReplacement != null && needReplacement[0]) {
                    replacement[0] = new TLRPC.TL_inputDocumentFileLocation();
                    replacement[0].id = document.id;
                    replacement[0].volume_id = location.volume_id;
                    replacement[0].local_id = location.local_id;
                    replacement[0].access_hash = document.access_hash;
                    replacement[0].file_reference = document.file_reference;
                    replacement[0].thumb_size = photoSize.type;
                    return document.file_reference;
                } else if (result != null) {
                    return result;
                } else {
                    a++;
                }
            }
        } else if (document.id == location.id) {
            return document.file_reference;
        }
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChannel} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerUser} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v10, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: org.telegram.tgnet.TLRPC$TL_inputPeerChat} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getPeerReferenceReplacement(org.telegram.tgnet.TLRPC.User r6, org.telegram.tgnet.TLRPC.Chat r7, boolean r8, org.telegram.tgnet.TLRPC.InputFileLocation r9, org.telegram.tgnet.TLRPC.InputFileLocation[] r10, boolean[] r11) {
        /*
            r5 = this;
            r0 = 0
            if (r11 == 0) goto L_0x005c
            boolean r1 = r11[r0]
            if (r1 == 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation
            r1.<init>()
            long r2 = r9.volume_id
            r1.id = r2
            long r2 = r9.volume_id
            r1.volume_id = r2
            int r2 = r9.local_id
            r1.local_id = r2
            r1.big = r8
            if (r6 == 0) goto L_0x0031
            org.telegram.tgnet.TLRPC$TL_inputPeerUser r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerUser
            r2.<init>()
            long r3 = r6.id
            r2.user_id = r3
            long r3 = r6.access_hash
            r2.access_hash = r3
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r6.photo
            long r3 = r3.photo_id
            r1.photo_id = r3
            goto L_0x0056
        L_0x0031:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r2 == 0) goto L_0x0046
            org.telegram.tgnet.TLRPC$TL_inputPeerChannel r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            r2.<init>()
            long r3 = r7.id
            r2.channel_id = r3
            long r3 = r7.access_hash
            r2.access_hash = r3
            goto L_0x0050
        L_0x0046:
            org.telegram.tgnet.TLRPC$TL_inputPeerChat r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerChat
            r2.<init>()
            long r3 = r7.id
            r2.chat_id = r3
            r3 = r2
        L_0x0050:
            org.telegram.tgnet.TLRPC$ChatPhoto r3 = r7.photo
            long r3 = r3.photo_id
            r1.photo_id = r3
        L_0x0056:
            r1.peer = r2
            r10[r0] = r1
            r0 = 1
            return r0
        L_0x005c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileRefController.getPeerReferenceReplacement(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, boolean, org.telegram.tgnet.TLRPC$InputFileLocation, org.telegram.tgnet.TLRPC$InputFileLocation[], boolean[]):boolean");
    }

    private byte[] getFileReference(TLRPC.User user, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (user == null || user.photo == null || !(location instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        byte[] result = getFileReference(user.photo.photo_small, location, needReplacement);
        if (getPeerReferenceReplacement(user, (TLRPC.Chat) null, false, location, replacement, needReplacement)) {
            return new byte[0];
        }
        if (result == null) {
            result = getFileReference(user.photo.photo_big, location, needReplacement);
            if (getPeerReferenceReplacement(user, (TLRPC.Chat) null, true, location, replacement, needReplacement)) {
                return new byte[0];
            }
        }
        return result;
    }

    private byte[] getFileReference(TLRPC.Chat chat, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (chat == null || chat.photo == null || (!(location instanceof TLRPC.TL_inputFileLocation) && !(location instanceof TLRPC.TL_inputPeerPhotoFileLocation))) {
            return null;
        }
        if (location instanceof TLRPC.TL_inputPeerPhotoFileLocation) {
            needReplacement[0] = true;
            if (getPeerReferenceReplacement((TLRPC.User) null, chat, false, location, replacement, needReplacement)) {
                return new byte[0];
            }
            return null;
        }
        byte[] result = getFileReference(chat.photo.photo_small, location, needReplacement);
        if (getPeerReferenceReplacement((TLRPC.User) null, chat, false, location, replacement, needReplacement)) {
            return new byte[0];
        }
        if (result == null) {
            result = getFileReference(chat.photo.photo_big, location, needReplacement);
            if (getPeerReferenceReplacement((TLRPC.User) null, chat, true, location, replacement, needReplacement)) {
                return new byte[0];
            }
        }
        return result;
    }

    private byte[] getFileReference(TLRPC.Photo photo, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        if (photo == null) {
            return null;
        }
        if (!(location instanceof TLRPC.TL_inputPhotoFileLocation)) {
            if (location instanceof TLRPC.TL_inputFileLocation) {
                int a = 0;
                int size = photo.sizes.size();
                while (a < size) {
                    TLRPC.PhotoSize photoSize = photo.sizes.get(a);
                    byte[] result = getFileReference(photoSize, location, needReplacement);
                    if (needReplacement != null && needReplacement[0]) {
                        replacement[0] = new TLRPC.TL_inputPhotoFileLocation();
                        replacement[0].id = photo.id;
                        replacement[0].volume_id = location.volume_id;
                        replacement[0].local_id = location.local_id;
                        replacement[0].access_hash = photo.access_hash;
                        replacement[0].file_reference = photo.file_reference;
                        replacement[0].thumb_size = photoSize.type;
                        return photo.file_reference;
                    } else if (result != null) {
                        return result;
                    } else {
                        a++;
                    }
                }
            }
            return null;
        } else if (photo.id == location.id) {
            return photo.file_reference;
        } else {
            return null;
        }
    }

    private byte[] getFileReference(TLRPC.PhotoSize photoSize, TLRPC.InputFileLocation location, boolean[] needReplacement) {
        if (photoSize == null || !(location instanceof TLRPC.TL_inputFileLocation)) {
            return null;
        }
        return getFileReference(photoSize.location, location, needReplacement);
    }

    private byte[] getFileReference(TLRPC.FileLocation fileLocation, TLRPC.InputFileLocation location, boolean[] needReplacement) {
        if (fileLocation == null || !(location instanceof TLRPC.TL_inputFileLocation) || fileLocation.local_id != location.local_id || fileLocation.volume_id != location.volume_id) {
            return null;
        }
        if (fileLocation.file_reference == null && needReplacement != null) {
            needReplacement[0] = true;
        }
        return fileLocation.file_reference;
    }

    private byte[] getFileReference(TLRPC.WebPage webpage, TLRPC.InputFileLocation location, boolean[] needReplacement, TLRPC.InputFileLocation[] replacement) {
        byte[] result = getFileReference(webpage.document, location, needReplacement, replacement);
        if (result != null) {
            return result;
        }
        byte[] result2 = getFileReference(webpage.photo, location, needReplacement, replacement);
        if (result2 != null) {
            return result2;
        }
        if (!webpage.attributes.isEmpty()) {
            int size1 = webpage.attributes.size();
            for (int a = 0; a < size1; a++) {
                TLRPC.TL_webPageAttributeTheme attribute = webpage.attributes.get(a);
                int size2 = attribute.documents.size();
                for (int b = 0; b < size2; b++) {
                    byte[] result3 = getFileReference(attribute.documents.get(b), location, needReplacement, replacement);
                    if (result3 != null) {
                        return result3;
                    }
                }
            }
        }
        if (webpage.cached_page == null) {
            return null;
        }
        int size22 = webpage.cached_page.documents.size();
        for (int b2 = 0; b2 < size22; b2++) {
            byte[] result4 = getFileReference(webpage.cached_page.documents.get(b2), location, needReplacement, replacement);
            if (result4 != null) {
                return result4;
            }
        }
        int size23 = webpage.cached_page.photos.size();
        for (int b3 = 0; b3 < size23; b3++) {
            byte[] result5 = getFileReference(webpage.cached_page.photos.get(b3), location, needReplacement, replacement);
            if (result5 != null) {
                return result5;
            }
        }
        return null;
    }

    public static boolean isFileRefError(String error) {
        return "FILEREF_EXPIRED".equals(error) || "FILE_REFERENCE_EXPIRED".equals(error) || "FILE_REFERENCE_EMPTY".equals(error) || (error != null && error.startsWith("FILE_REFERENCE_"));
    }
}
