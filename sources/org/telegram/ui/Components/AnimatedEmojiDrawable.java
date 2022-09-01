package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmojiDefaultStatuses;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_messages_getCustomEmojiDocuments;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;

public class AnimatedEmojiDrawable extends Drawable {
    private static HashMap<Integer, EmojiDocumentFetcher> fetchers;
    private static HashMap<Integer, HashMap<Long, AnimatedEmojiDrawable>> globalEmojiCache;
    private float alpha = 1.0f;
    private boolean attached;
    /* access modifiers changed from: private */
    public int cacheType;
    private ColorFilter colorFilterToSet;
    int count;
    private TLRPC$Document document;
    private long documentId;
    private ArrayList<AnimatedEmojiSpan.AnimatedEmojiHolder> holders;
    private ImageReceiver imageReceiver;
    public int sizedp;
    private ArrayList<View> views;

    public interface ReceivedDocument {
        void run(TLRPC$Document tLRPC$Document);
    }

    private void drawPlaceholder(Canvas canvas, float f, float f2, float f3) {
    }

    public int getOpacity() {
        return -2;
    }

    public static AnimatedEmojiDrawable make(int i, int i2, long j) {
        if (globalEmojiCache == null) {
            globalEmojiCache = new HashMap<>();
        }
        int hashCode = Arrays.hashCode(new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        HashMap hashMap = globalEmojiCache.get(Integer.valueOf(hashCode));
        if (hashMap == null) {
            HashMap<Integer, HashMap<Long, AnimatedEmojiDrawable>> hashMap2 = globalEmojiCache;
            Integer valueOf = Integer.valueOf(hashCode);
            HashMap hashMap3 = new HashMap();
            hashMap2.put(valueOf, hashMap3);
            hashMap = hashMap3;
        }
        AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) hashMap.get(Long.valueOf(j));
        if (animatedEmojiDrawable != null) {
            return animatedEmojiDrawable;
        }
        Long valueOf2 = Long.valueOf(j);
        AnimatedEmojiDrawable animatedEmojiDrawable2 = new AnimatedEmojiDrawable(i2, i, j);
        hashMap.put(valueOf2, animatedEmojiDrawable2);
        return animatedEmojiDrawable2;
    }

    public static AnimatedEmojiDrawable make(int i, int i2, TLRPC$Document tLRPC$Document) {
        if (globalEmojiCache == null) {
            globalEmojiCache = new HashMap<>();
        }
        int hashCode = Arrays.hashCode(new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        HashMap hashMap = globalEmojiCache.get(Integer.valueOf(hashCode));
        if (hashMap == null) {
            HashMap<Integer, HashMap<Long, AnimatedEmojiDrawable>> hashMap2 = globalEmojiCache;
            Integer valueOf = Integer.valueOf(hashCode);
            HashMap hashMap3 = new HashMap();
            hashMap2.put(valueOf, hashMap3);
            hashMap = hashMap3;
        }
        AnimatedEmojiDrawable animatedEmojiDrawable = (AnimatedEmojiDrawable) hashMap.get(Long.valueOf(tLRPC$Document.id));
        if (animatedEmojiDrawable != null) {
            return animatedEmojiDrawable;
        }
        Long valueOf2 = Long.valueOf(tLRPC$Document.id);
        AnimatedEmojiDrawable animatedEmojiDrawable2 = new AnimatedEmojiDrawable(i2, i, tLRPC$Document);
        hashMap.put(valueOf2, animatedEmojiDrawable2);
        return animatedEmojiDrawable2;
    }

    public static int getCacheTypeForEnterView() {
        return SharedConfig.getDevicePerformanceClass() == 0 ? 0 : 2;
    }

    public void setTime(long j) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            if (this.cacheType == 8) {
                j = 0;
            }
            imageReceiver2.setCurrentTime(j);
        }
    }

    public void update(long j) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            if (this.cacheType == 8) {
                j = 0;
            }
            if (imageReceiver2.getLottieAnimation() != null) {
                this.imageReceiver.getLottieAnimation().updateCurrentFrame(j, true);
            }
            if (this.imageReceiver.getAnimation() != null) {
                this.imageReceiver.getAnimation().updateCurrentFrame(j, true);
            }
        }
    }

    public static EmojiDocumentFetcher getDocumentFetcher(int i) {
        if (fetchers == null) {
            fetchers = new HashMap<>();
        }
        EmojiDocumentFetcher emojiDocumentFetcher = fetchers.get(Integer.valueOf(i));
        if (emojiDocumentFetcher != null) {
            return emojiDocumentFetcher;
        }
        HashMap<Integer, EmojiDocumentFetcher> hashMap = fetchers;
        Integer valueOf = Integer.valueOf(i);
        EmojiDocumentFetcher emojiDocumentFetcher2 = new EmojiDocumentFetcher(i);
        hashMap.put(valueOf, emojiDocumentFetcher2);
        return emojiDocumentFetcher2;
    }

    public static class EmojiDocumentFetcher {
        private final int currentAccount;
        /* access modifiers changed from: private */
        public HashMap<Long, TLRPC$Document> emojiDocumentsCache;
        private Runnable fetchRunnable;
        private HashMap<Long, ArrayList<ReceivedDocument>> loadingDocuments;
        private HashSet<Long> toFetchDocuments;

        public EmojiDocumentFetcher(int i) {
            this.currentAccount = i;
        }

        public void fetchDocument(long j, ReceivedDocument receivedDocument) {
            TLRPC$Document tLRPC$Document;
            checkThread();
            HashMap<Long, TLRPC$Document> hashMap = this.emojiDocumentsCache;
            if (hashMap == null || (tLRPC$Document = hashMap.get(Long.valueOf(j))) == null) {
                if (receivedDocument != null) {
                    if (this.loadingDocuments == null) {
                        this.loadingDocuments = new HashMap<>();
                    }
                    ArrayList arrayList = this.loadingDocuments.get(Long.valueOf(j));
                    if (arrayList != null) {
                        arrayList.add(receivedDocument);
                        return;
                    }
                    ArrayList arrayList2 = new ArrayList(1);
                    arrayList2.add(receivedDocument);
                    this.loadingDocuments.put(Long.valueOf(j), arrayList2);
                }
                if (this.toFetchDocuments == null) {
                    this.toFetchDocuments = new HashSet<>();
                }
                this.toFetchDocuments.add(Long.valueOf(j));
                if (this.fetchRunnable == null) {
                    AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda0 animatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda0 = new AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda0(this);
                    this.fetchRunnable = animatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda0;
                    AndroidUtilities.runOnUIThread(animatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda0);
                    return;
                }
                return;
            }
            receivedDocument.run(tLRPC$Document);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$fetchDocument$0() {
            ArrayList arrayList = new ArrayList(this.toFetchDocuments);
            this.toFetchDocuments.clear();
            loadFromDatabase(arrayList);
            this.fetchRunnable = null;
        }

        private void checkThread() {
            if (BuildVars.DEBUG_VERSION && Thread.currentThread() != Looper.getMainLooper().getThread()) {
                throw new IllegalStateException("Wrong thread");
            }
        }

        private void loadFromDatabase(ArrayList<Long> arrayList) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda1(this, arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFromDatabase$2(ArrayList arrayList) {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
            try {
                String join = TextUtils.join(",", arrayList);
                SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data FROM animated_emoji WHERE document_id IN (%s)", new Object[]{join}), new Object[0]);
                ArrayList arrayList2 = new ArrayList();
                HashSet hashSet = new HashSet(arrayList);
                while (queryFinalized.next()) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    try {
                        TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(true), true);
                        if (!(TLdeserialize == null || TLdeserialize.id == 0)) {
                            arrayList2.add(TLdeserialize);
                            hashSet.remove(Long.valueOf(TLdeserialize.id));
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    if (byteBufferValue != null) {
                        byteBufferValue.reuse();
                    }
                }
                AndroidUtilities.runOnUIThread(new AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda3(this, arrayList2, hashSet));
                queryFinalized.dispose();
            } catch (SQLiteException e2) {
                FileLog.e((Throwable) e2);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFromDatabase$1(ArrayList arrayList, HashSet hashSet) {
            processDocuments(arrayList);
            if (!hashSet.isEmpty()) {
                loadFromServer(new ArrayList(hashSet));
            }
        }

        private void loadFromServer(ArrayList<Long> arrayList) {
            TLRPC$TL_messages_getCustomEmojiDocuments tLRPC$TL_messages_getCustomEmojiDocuments = new TLRPC$TL_messages_getCustomEmojiDocuments();
            tLRPC$TL_messages_getCustomEmojiDocuments.document_id = arrayList;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getCustomEmojiDocuments, new AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda5(this, arrayList));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFromServer$4(ArrayList arrayList, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda4(this, arrayList, tLObject));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFromServer$3(ArrayList arrayList, TLObject tLObject) {
            HashSet hashSet = new HashSet(arrayList);
            if (tLObject instanceof TLRPC$Vector) {
                ArrayList<Object> arrayList2 = ((TLRPC$Vector) tLObject).objects;
                putToStorage(arrayList2);
                processDocuments(arrayList2);
                for (int i = 0; i < arrayList2.size(); i++) {
                    if (arrayList2.get(i) instanceof TLRPC$Document) {
                        hashSet.remove(Long.valueOf(((TLRPC$Document) arrayList2.get(i)).id));
                    }
                }
                if (!hashSet.isEmpty()) {
                    loadFromServer(new ArrayList(hashSet));
                }
            }
        }

        private void putToStorage(ArrayList<Object> arrayList) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda2(this, arrayList));
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x004d A[Catch:{ SQLiteException -> 0x0057 }] */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x0050 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$putToStorage$5(java.util.ArrayList r7) {
            /*
                r6 = this;
                int r0 = r6.currentAccount
                org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
                org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()
                java.lang.String r1 = "REPLACE INTO animated_emoji VALUES(?, ?)"
                org.telegram.SQLite.SQLitePreparedStatement r0 = r0.executeFast(r1)     // Catch:{ SQLiteException -> 0x0057 }
                r1 = 0
            L_0x0011:
                int r2 = r7.size()     // Catch:{ SQLiteException -> 0x0057 }
                if (r1 >= r2) goto L_0x0053
                java.lang.Object r2 = r7.get(r1)     // Catch:{ SQLiteException -> 0x0057 }
                boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$Document     // Catch:{ SQLiteException -> 0x0057 }
                if (r2 == 0) goto L_0x0050
                java.lang.Object r2 = r7.get(r1)     // Catch:{ SQLiteException -> 0x0057 }
                org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC$Document) r2     // Catch:{ SQLiteException -> 0x0057 }
                r3 = 0
                org.telegram.tgnet.NativeByteBuffer r4 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0046 }
                int r5 = r2.getObjectSize()     // Catch:{ Exception -> 0x0046 }
                r4.<init>((int) r5)     // Catch:{ Exception -> 0x0046 }
                r2.serializeToStream(r4)     // Catch:{ Exception -> 0x0043 }
                r0.requery()     // Catch:{ Exception -> 0x0043 }
                long r2 = r2.id     // Catch:{ Exception -> 0x0043 }
                r5 = 1
                r0.bindLong(r5, r2)     // Catch:{ Exception -> 0x0043 }
                r2 = 2
                r0.bindByteBuffer((int) r2, (org.telegram.tgnet.NativeByteBuffer) r4)     // Catch:{ Exception -> 0x0043 }
                r0.step()     // Catch:{ Exception -> 0x0043 }
                goto L_0x004b
            L_0x0043:
                r2 = move-exception
                r3 = r4
                goto L_0x0047
            L_0x0046:
                r2 = move-exception
            L_0x0047:
                r2.printStackTrace()     // Catch:{ SQLiteException -> 0x0057 }
                r4 = r3
            L_0x004b:
                if (r4 == 0) goto L_0x0050
                r4.reuse()     // Catch:{ SQLiteException -> 0x0057 }
            L_0x0050:
                int r1 = r1 + 1
                goto L_0x0011
            L_0x0053:
                r0.dispose()     // Catch:{ SQLiteException -> 0x0057 }
                goto L_0x005b
            L_0x0057:
                r7 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
            L_0x005b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedEmojiDrawable.EmojiDocumentFetcher.lambda$putToStorage$5(java.util.ArrayList):void");
        }

        public void processDocuments(ArrayList<?> arrayList) {
            ArrayList remove;
            checkThread();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) instanceof TLRPC$Document) {
                    TLRPC$Document tLRPC$Document = (TLRPC$Document) arrayList.get(i);
                    if (this.emojiDocumentsCache == null) {
                        this.emojiDocumentsCache = new HashMap<>();
                    }
                    this.emojiDocumentsCache.put(Long.valueOf(tLRPC$Document.id), tLRPC$Document);
                    HashMap<Long, ArrayList<ReceivedDocument>> hashMap = this.loadingDocuments;
                    if (!(hashMap == null || (remove = hashMap.remove(Long.valueOf(tLRPC$Document.id))) == null)) {
                        for (int i2 = 0; i2 < remove.size(); i2++) {
                            ((ReceivedDocument) remove.get(i2)).run(tLRPC$Document);
                        }
                        remove.clear();
                    }
                }
            }
        }
    }

    public static TLRPC$Document findDocument(int i, long j) {
        EmojiDocumentFetcher documentFetcher = getDocumentFetcher(i);
        if (documentFetcher == null || documentFetcher.emojiDocumentsCache == null) {
            return null;
        }
        return (TLRPC$Document) documentFetcher.emojiDocumentsCache.get(Long.valueOf(j));
    }

    public AnimatedEmojiDrawable(int i, int i2, long j) {
        new AnimatedFloat(1.0f, (Runnable) new AnimatedEmojiDrawable$$ExternalSyntheticLambda0(this), 0, 150, (TimeInterpolator) new LinearInterpolator());
        this.cacheType = i;
        updateSize();
        this.documentId = j;
        getDocumentFetcher(i2).fetchDocument(j, new AnimatedEmojiDrawable$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(TLRPC$Document tLRPC$Document) {
        this.document = tLRPC$Document;
        initDocument();
    }

    public AnimatedEmojiDrawable(int i, int i2, TLRPC$Document tLRPC$Document) {
        new AnimatedFloat(1.0f, (Runnable) new AnimatedEmojiDrawable$$ExternalSyntheticLambda0(this), 0, 150, (TimeInterpolator) new LinearInterpolator());
        this.cacheType = i;
        this.document = tLRPC$Document;
        updateSize();
        initDocument();
    }

    private void updateSize() {
        int i = this.cacheType;
        if (i == 0) {
            this.sizedp = (int) (((Math.abs(Theme.chat_msgTextPaint.ascent()) + Math.abs(Theme.chat_msgTextPaint.descent())) * 1.15f) / AndroidUtilities.density);
        } else if (i == 1 || i == 4) {
            this.sizedp = (int) (((Math.abs(Theme.chat_msgTextPaintEmoji[2].ascent()) + Math.abs(Theme.chat_msgTextPaintEmoji[2].descent())) * 1.15f) / AndroidUtilities.density);
        } else if (i == 8) {
            this.sizedp = (int) (((Math.abs(Theme.chat_msgTextPaintEmoji[0].ascent()) + Math.abs(Theme.chat_msgTextPaintEmoji[0].descent())) * 1.15f) / AndroidUtilities.density);
        } else {
            this.sizedp = 34;
        }
    }

    public long getDocumentId() {
        TLRPC$Document tLRPC$Document = this.document;
        return tLRPC$Document != null ? tLRPC$Document.id : this.documentId;
    }

    public TLRPC$Document getDocument() {
        return this.document;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x004f, code lost:
        r1 = r0.cacheType;
     */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x018c  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01ac  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01e8  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0206  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0216  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initDocument() {
        /*
            r30 = this;
            r0 = r30
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            if (r1 == 0) goto L_0x0231
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            if (r1 == 0) goto L_0x000c
            goto L_0x0231
        L_0x000c:
            org.telegram.ui.Components.AnimatedEmojiDrawable$1 r1 = new org.telegram.ui.Components.AnimatedEmojiDrawable$1
            r1.<init>()
            r0.imageReceiver = r1
            android.graphics.ColorFilter r1 = r0.colorFilterToSet
            if (r1 == 0) goto L_0x0024
            boolean r1 = r30.canOverrideColor()
            if (r1 == 0) goto L_0x0024
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            android.graphics.ColorFilter r2 = r0.colorFilterToSet
            r1.setColorFilter(r2)
        L_0x0024:
            int r1 = r0.cacheType
            java.lang.String r2 = "_"
            if (r1 == 0) goto L_0x0040
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = r0.cacheType
            r3.append(r4)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            r1.setUniqKeyPrefix(r3)
        L_0x0040:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r3 = 1
            r1.setVideoThumbIsSame(r3)
            int r1 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
            r4 = 5
            r5 = 3
            r6 = 2
            if (r1 != 0) goto L_0x0059
            int r1 = r0.cacheType
            if (r1 == r6) goto L_0x0057
            if (r1 == r5) goto L_0x0057
            if (r1 != r4) goto L_0x0059
        L_0x0057:
            r1 = 1
            goto L_0x005a
        L_0x0059:
            r1 = 0
        L_0x005a:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            int r9 = r0.sizedp
            r8.append(r9)
            r8.append(r2)
            int r9 = r0.sizedp
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            int r9 = r0.cacheType
            r10 = 8
            if (r9 == r10) goto L_0x008f
            if (r9 != r3) goto L_0x007e
            int r9 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
            if (r9 >= r6) goto L_0x008f
        L_0x007e:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            java.lang.String r8 = "_pcache"
            r9.append(r8)
            java.lang.String r8 = r9.toString()
        L_0x008f:
            int r9 = r0.cacheType
            if (r9 == 0) goto L_0x00a6
            if (r9 == r3) goto L_0x00a6
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            java.lang.String r8 = "_compress"
            r9.append(r8)
            java.lang.String r8 = r9.toString()
        L_0x00a6:
            int r9 = r0.cacheType
            if (r9 != r10) goto L_0x00bb
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            java.lang.String r8 = "firstframe"
            r9.append(r8)
            java.lang.String r8 = r9.toString()
        L_0x00bb:
            org.telegram.tgnet.TLRPC$Document r9 = r0.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r9.thumbs
            r11 = 90
            org.telegram.tgnet.TLRPC$PhotoSize r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r11)
            org.telegram.tgnet.TLRPC$Document r11 = r0.document
            java.lang.String r11 = r11.mime_type
            java.lang.String r12 = "video/webm"
            boolean r11 = r12.equals(r11)
            r12 = 1045220557(0x3e4ccccd, float:0.2)
            java.lang.String r13 = "windowBackgroundWhiteGrayIcon"
            if (r11 == 0) goto L_0x00fc
            org.telegram.tgnet.TLRPC$Document r11 = r0.document
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForDocument(r11)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r8)
            r15.append(r2)
            java.lang.String r8 = "g"
            r15.append(r8)
            java.lang.String r8 = r15.toString()
            org.telegram.tgnet.TLRPC$Document r15 = r0.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r15 = r15.thumbs
            org.telegram.messenger.SvgHelper$SvgDrawable r12 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r15, (java.lang.String) r13, (float) r12)
        L_0x00f8:
            r24 = r12
            goto L_0x0181
        L_0x00fc:
            org.telegram.tgnet.TLRPC$Document r11 = r0.document
            java.lang.String r11 = r11.mime_type
            java.lang.String r15 = "application/x-tgsticker"
            boolean r11 = r15.equals(r11)
            r15 = 512(0x200, float:7.175E-43)
            if (r11 == 0) goto L_0x0169
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            int r14 = r0.cacheType
            if (r14 == 0) goto L_0x0125
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            int r7 = r0.cacheType
            r14.append(r7)
            r14.append(r2)
            java.lang.String r7 = r14.toString()
            goto L_0x0127
        L_0x0125:
            java.lang.String r7 = ""
        L_0x0127:
            r11.append(r7)
            long r4 = r0.documentId
            r11.append(r4)
            java.lang.String r4 = "@"
            r11.append(r4)
            r11.append(r8)
            java.lang.String r4 = r11.toString()
            int r5 = r0.cacheType
            if (r5 == r6) goto L_0x014c
            org.telegram.messenger.ImageLoader r5 = org.telegram.messenger.ImageLoader.getInstance()
            boolean r4 = r5.hasLottieMemCache(r4)
            if (r4 != 0) goto L_0x014a
            goto L_0x014c
        L_0x014a:
            r12 = 0
            goto L_0x0162
        L_0x014c:
            org.telegram.tgnet.TLRPC$Document r4 = r0.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.thumbs
            org.telegram.messenger.SvgHelper$SvgDrawable r4 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r4, (java.lang.String) r13, (float) r12)
            if (r4 == 0) goto L_0x0161
            org.telegram.tgnet.TLRPC$Document r5 = r0.document
            boolean r5 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r5, r3)
            if (r5 == 0) goto L_0x0161
            r4.overrideWidthAndHeight(r15, r15)
        L_0x0161:
            r12 = r4
        L_0x0162:
            org.telegram.tgnet.TLRPC$Document r4 = r0.document
            org.telegram.messenger.ImageLocation r11 = org.telegram.messenger.ImageLocation.getForDocument(r4)
            goto L_0x00f8
        L_0x0169:
            org.telegram.tgnet.TLRPC$Document r4 = r0.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r4 = r4.thumbs
            org.telegram.messenger.SvgHelper$SvgDrawable r12 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r4, (java.lang.String) r13, (float) r12)
            if (r12 == 0) goto L_0x017e
            org.telegram.tgnet.TLRPC$Document r4 = r0.document
            boolean r4 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r4, r3)
            if (r4 == 0) goto L_0x017e
            r12.overrideWidthAndHeight(r15, r15)
        L_0x017e:
            r24 = r12
            r11 = 0
        L_0x0181:
            if (r1 == 0) goto L_0x0186
            r16 = 0
            goto L_0x0188
        L_0x0186:
            r16 = r11
        L_0x0188:
            int r1 = r0.cacheType
            if (r1 != r10) goto L_0x01ac
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r18 = 0
            r19 = 0
            r22 = 0
            r23 = 0
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            long r4 = r2.size
            r27 = 0
            r29 = 1
            r17 = r1
            r20 = r16
            r21 = r8
            r25 = r4
            r28 = r2
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25, r27, r28, r29)
            goto L_0x01e3
        L_0x01ac:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            org.telegram.tgnet.TLRPC$Document r4 = r0.document
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r9, (org.telegram.tgnet.TLRPC$Document) r4)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = r0.sizedp
            r4.append(r5)
            r4.append(r2)
            int r2 = r0.sizedp
            r4.append(r2)
            java.lang.String r21 = r4.toString()
            r22 = 0
            r23 = 0
            org.telegram.tgnet.TLRPC$Document r2 = r0.document
            long r4 = r2.size
            r27 = 0
            r29 = 1
            r17 = r1
            r18 = r16
            r19 = r8
            r25 = r4
            r28 = r2
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25, r27, r28, r29)
        L_0x01e3:
            int r1 = r0.cacheType
            r2 = 7
            if (r1 != r2) goto L_0x01ed
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAutoRepeatCount(r6)
        L_0x01ed:
            int r1 = r0.cacheType
            r4 = 3
            if (r1 == r4) goto L_0x01f8
            r4 = 5
            if (r1 == r4) goto L_0x01f8
            r4 = 4
            if (r1 != r4) goto L_0x01fd
        L_0x01f8:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setLayerNum(r2)
        L_0x01fd:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAspectFit(r3)
            int r1 = r0.cacheType
            if (r1 == r10) goto L_0x0216
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAllowStartLottieAnimation(r3)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAllowStartAnimation(r3)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAutoRepeat(r3)
            goto L_0x0226
        L_0x0216:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r2 = 0
            r1.setAllowStartAnimation(r2)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAllowStartLottieAnimation(r2)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAutoRepeat(r2)
        L_0x0226:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAllowDecodeSingleFrame(r3)
            r30.updateAttachState()
            r30.invalidate()
        L_0x0231:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedEmojiDrawable.initDocument():void");
    }

    /* access modifiers changed from: package-private */
    public void invalidate() {
        if (this.views != null) {
            for (int i = 0; i < this.views.size(); i++) {
                View view = this.views.get(i);
                if (view != null) {
                    view.invalidate();
                }
            }
        }
        if (this.holders != null) {
            for (int i2 = 0; i2 < this.holders.size(); i2++) {
                AnimatedEmojiSpan.AnimatedEmojiHolder animatedEmojiHolder = this.holders.get(i2);
                if (animatedEmojiHolder != null) {
                    animatedEmojiHolder.invalidate();
                }
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AnimatedEmojiDrawable{");
        TLRPC$Document tLRPC$Document = this.document;
        sb.append(tLRPC$Document == null ? "null" : MessageObject.findAnimatedEmojiEmoticon(tLRPC$Document, (String) null));
        sb.append("}");
        return sb.toString();
    }

    public void draw(Canvas canvas) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImageCoords(getBounds());
            this.imageReceiver.setAlpha(this.alpha);
            this.imageReceiver.draw(canvas);
        }
        drawPlaceholder(canvas, (float) getBounds().centerX(), (float) getBounds().centerY(), ((float) getBounds().width()) / 2.0f);
    }

    public void draw(Canvas canvas, Rect rect, float f) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImageCoords(rect);
            this.imageReceiver.setAlpha(f);
            this.imageReceiver.draw(canvas);
        }
        if (rect != null) {
            drawPlaceholder(canvas, (float) rect.centerX(), (float) rect.centerY(), ((float) rect.width()) / 2.0f);
        }
    }

    public void draw(Canvas canvas, ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setAlpha(this.alpha);
            this.imageReceiver.draw(canvas, backgroundThreadDrawHolder);
        }
        if (backgroundThreadDrawHolder != null) {
            float f = backgroundThreadDrawHolder.imageX;
            float f2 = backgroundThreadDrawHolder.imageW;
            drawPlaceholder(canvas, f + (f2 / 2.0f), backgroundThreadDrawHolder.imageY + (backgroundThreadDrawHolder.imageH / 2.0f), f2 / 2.0f);
        }
    }

    public void addView(View view) {
        if (this.views == null) {
            this.views = new ArrayList<>(10);
        }
        if (!this.views.contains(view)) {
            this.views.add(view);
        }
        updateAttachState();
    }

    public void addView(AnimatedEmojiSpan.AnimatedEmojiHolder animatedEmojiHolder) {
        if (this.holders == null) {
            this.holders = new ArrayList<>(10);
        }
        if (!this.holders.contains(animatedEmojiHolder)) {
            this.holders.add(animatedEmojiHolder);
        }
        updateAttachState();
    }

    public void removeView(AnimatedEmojiSpan.AnimatedEmojiHolder animatedEmojiHolder) {
        ArrayList<AnimatedEmojiSpan.AnimatedEmojiHolder> arrayList = this.holders;
        if (arrayList != null) {
            arrayList.remove(animatedEmojiHolder);
        }
        updateAttachState();
    }

    public void removeView(Drawable.Callback callback) {
        ArrayList<View> arrayList = this.views;
        if (arrayList != null) {
            arrayList.remove(callback);
        }
        updateAttachState();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0010, code lost:
        r0 = r3.holders;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAttachState() {
        /*
            r3 = this;
            org.telegram.messenger.ImageReceiver r0 = r3.imageReceiver
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            java.util.ArrayList<android.view.View> r0 = r3.views
            r1 = 1
            if (r0 == 0) goto L_0x0010
            int r0 = r0.size()
            if (r0 > 0) goto L_0x001a
        L_0x0010:
            java.util.ArrayList<org.telegram.ui.Components.AnimatedEmojiSpan$AnimatedEmojiHolder> r0 = r3.holders
            if (r0 == 0) goto L_0x001c
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x001c
        L_0x001a:
            r0 = 1
            goto L_0x001d
        L_0x001c:
            r0 = 0
        L_0x001d:
            boolean r2 = r3.attached
            if (r0 == r2) goto L_0x003a
            r3.attached = r0
            if (r0 == 0) goto L_0x0030
            int r0 = r3.count
            int r0 = r0 + r1
            r3.count = r0
            org.telegram.messenger.ImageReceiver r0 = r3.imageReceiver
            r0.onAttachedToWindow()
            goto L_0x003a
        L_0x0030:
            int r0 = r3.count
            int r0 = r0 - r1
            r3.count = r0
            org.telegram.messenger.ImageReceiver r0 = r3.imageReceiver
            r0.onDetachedFromWindow()
        L_0x003a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedEmojiDrawable.updateAttachState():void");
    }

    public boolean canOverrideColor() {
        TLRPC$Document tLRPC$Document = this.document;
        if (tLRPC$Document == null) {
            return false;
        }
        TLRPC$InputStickerSet inputStickerSet = MessageObject.getInputStickerSet(tLRPC$Document);
        if ((inputStickerSet instanceof TLRPC$TL_inputStickerSetEmojiDefaultStatuses) || ((inputStickerSet instanceof TLRPC$TL_inputStickerSetID) && inputStickerSet.id == 773947703670341676L)) {
            return true;
        }
        return false;
    }

    public int getAlpha() {
        return (int) (this.alpha * 255.0f);
    }

    public void setAlpha(int i) {
        float f = ((float) i) / 255.0f;
        this.alpha = f;
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setAlpha(f);
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        if (this.imageReceiver == null) {
            this.colorFilterToSet = colorFilter;
        } else if (canOverrideColor()) {
            this.imageReceiver.setColorFilter(colorFilter);
        }
    }

    public ImageReceiver getImageReceiver() {
        return this.imageReceiver;
    }

    public static class WrapSizeDrawable extends Drawable {
        private int alpha = 255;
        private Drawable drawable;
        int height;
        int width;

        public WrapSizeDrawable(Drawable drawable2, int i, int i2) {
            this.drawable = drawable2;
            this.width = i;
            this.height = i2;
        }

        public Drawable getDrawable() {
            return this.drawable;
        }

        public void draw(Canvas canvas) {
            Drawable drawable2 = this.drawable;
            if (drawable2 != null) {
                drawable2.setBounds(getBounds());
                this.drawable.setAlpha(this.alpha);
                this.drawable.draw(canvas);
            }
        }

        public int getIntrinsicWidth() {
            return this.width;
        }

        public int getIntrinsicHeight() {
            return this.height;
        }

        public void setAlpha(int i) {
            this.alpha = i;
            Drawable drawable2 = this.drawable;
            if (drawable2 != null) {
                drawable2.setAlpha(i);
            }
        }

        public void setColorFilter(ColorFilter colorFilter) {
            Drawable drawable2 = this.drawable;
            if (drawable2 != null) {
                drawable2.setColorFilter(colorFilter);
            }
        }

        public int getOpacity() {
            Drawable drawable2 = this.drawable;
            if (drawable2 != null) {
                return drawable2.getOpacity();
            }
            return -2;
        }
    }

    public static class SwapAnimatedEmojiDrawable extends Drawable {
        private int alpha;
        private int cacheType;
        public boolean center;
        private AnimatedFloat changeProgress;
        private ColorFilter colorFilter;
        private Drawable[] drawables;
        private Integer lastColor;
        private OvershootInterpolator overshootInterpolator;
        private View parentView;
        private int size;

        public int getOpacity() {
            return -2;
        }

        public void setColorFilter(ColorFilter colorFilter2) {
        }

        public SwapAnimatedEmojiDrawable(View view, int i) {
            this(view, i, 7);
        }

        public SwapAnimatedEmojiDrawable(View view, int i, int i2) {
            this.center = false;
            this.overshootInterpolator = new OvershootInterpolator(2.0f);
            AnimatedFloat animatedFloat = new AnimatedFloat((View) null, 300, (TimeInterpolator) CubicBezierInterpolator.EASE_OUT);
            this.changeProgress = animatedFloat;
            this.drawables = new Drawable[2];
            this.alpha = 255;
            this.parentView = view;
            animatedFloat.setParent(view);
            this.size = i;
            this.cacheType = i2;
        }

        public void setParentView(View view) {
            removeParentView(this.parentView);
            this.parentView = view;
            addParentView(view);
            this.changeProgress.setParent(view);
            this.parentView = view;
        }

        public void addParentView(View view) {
            Drawable[] drawableArr = this.drawables;
            if (drawableArr[0] instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawableArr[0]).addView(view);
            }
            Drawable[] drawableArr2 = this.drawables;
            if (drawableArr2[1] instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawableArr2[1]).addView(view);
            }
        }

        public void removeParentView(View view) {
            Drawable[] drawableArr = this.drawables;
            if (drawableArr[0] instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawableArr[0]).removeView((Drawable.Callback) view);
            }
            Drawable[] drawableArr2 = this.drawables;
            if (drawableArr2[1] instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawableArr2[1]).removeView((Drawable.Callback) view);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
            r0 = (org.telegram.ui.Components.AnimatedEmojiDrawable) getDrawable();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void play() {
            /*
                r3 = this;
                android.graphics.drawable.Drawable r0 = r3.getDrawable()
                boolean r0 = r0 instanceof org.telegram.ui.Components.AnimatedEmojiDrawable
                if (r0 == 0) goto L_0x0022
                android.graphics.drawable.Drawable r0 = r3.getDrawable()
                org.telegram.ui.Components.AnimatedEmojiDrawable r0 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r0
                org.telegram.messenger.ImageReceiver r1 = r0.getImageReceiver()
                if (r1 == 0) goto L_0x0022
                int r0 = r0.cacheType
                r2 = 7
                if (r0 != r2) goto L_0x001f
                r0 = 2
                r1.setAutoRepeatCount(r0)
            L_0x001f:
                r1.startAnimation()
            L_0x0022:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable.play():void");
        }

        public void setColor(Integer num) {
            Integer num2 = this.lastColor;
            if (num2 != null || num != null) {
                if (num2 == null || num == null || num2 != num) {
                    this.lastColor = num;
                    this.colorFilter = num != null ? new PorterDuffColorFilter(num.intValue(), PorterDuff.Mode.MULTIPLY) : null;
                }
            }
        }

        public Integer getColor() {
            return this.lastColor;
        }

        public void draw(Canvas canvas) {
            float f = this.changeProgress.set(1.0f);
            Rect bounds = getBounds();
            Drawable[] drawableArr = this.drawables;
            if (drawableArr[1] != null && f < 1.0f) {
                drawableArr[1].setAlpha((int) (((float) this.alpha) * (1.0f - f)));
                Drawable[] drawableArr2 = this.drawables;
                if (drawableArr2[1] instanceof AnimatedEmojiDrawable) {
                    drawableArr2[1].setBounds(bounds);
                } else if (this.center) {
                    drawableArr2[1].setBounds(bounds.centerX() - (this.drawables[1].getIntrinsicWidth() / 2), bounds.centerY() - (this.drawables[1].getIntrinsicHeight() / 2), bounds.centerX() + (this.drawables[1].getIntrinsicWidth() / 2), bounds.centerY() + (this.drawables[1].getIntrinsicHeight() / 2));
                } else {
                    drawableArr2[1].setBounds(bounds.left, bounds.centerY() - (this.drawables[1].getIntrinsicHeight() / 2), bounds.left + this.drawables[1].getIntrinsicWidth(), bounds.centerY() + (this.drawables[1].getIntrinsicHeight() / 2));
                }
                this.drawables[1].setColorFilter(this.colorFilter);
                this.drawables[1].draw(canvas);
                this.drawables[1].setColorFilter((ColorFilter) null);
            }
            if (this.drawables[0] != null) {
                canvas.save();
                if (this.drawables[0] instanceof AnimatedEmojiDrawable) {
                    if (f < 1.0f) {
                        float interpolation = this.overshootInterpolator.getInterpolation(f);
                        canvas.scale(interpolation, interpolation, (float) bounds.centerX(), (float) bounds.centerY());
                    }
                    this.drawables[0].setBounds(bounds);
                } else if (this.center) {
                    if (f < 1.0f) {
                        float interpolation2 = this.overshootInterpolator.getInterpolation(f);
                        canvas.scale(interpolation2, interpolation2, (float) bounds.centerX(), (float) bounds.centerY());
                    }
                    this.drawables[0].setBounds(bounds.centerX() - (this.drawables[0].getIntrinsicWidth() / 2), bounds.centerY() - (this.drawables[0].getIntrinsicHeight() / 2), bounds.centerX() + (this.drawables[0].getIntrinsicWidth() / 2), bounds.centerY() + (this.drawables[0].getIntrinsicHeight() / 2));
                } else {
                    if (f < 1.0f) {
                        float interpolation3 = this.overshootInterpolator.getInterpolation(f);
                        canvas.scale(interpolation3, interpolation3, ((float) bounds.left) + (((float) this.drawables[0].getIntrinsicWidth()) / 2.0f), (float) bounds.centerY());
                    }
                    this.drawables[0].setBounds(bounds.left, bounds.centerY() - (this.drawables[0].getIntrinsicHeight() / 2), bounds.left + this.drawables[0].getIntrinsicWidth(), bounds.centerY() + (this.drawables[0].getIntrinsicHeight() / 2));
                }
                this.drawables[0].setAlpha(this.alpha);
                this.drawables[0].setColorFilter(this.colorFilter);
                this.drawables[0].draw(canvas);
                this.drawables[0].setColorFilter((ColorFilter) null);
                canvas.restore();
            }
        }

        public Drawable getDrawable() {
            return this.drawables[0];
        }

        public void set(long j, boolean z) {
            set(j, this.cacheType, z);
        }

        public void set(long j, int i, boolean z) {
            Drawable[] drawableArr = this.drawables;
            if (!(drawableArr[0] instanceof AnimatedEmojiDrawable) || ((AnimatedEmojiDrawable) drawableArr[0]).getDocumentId() != j) {
                if (z) {
                    this.changeProgress.set(0.0f, true);
                    Drawable[] drawableArr2 = this.drawables;
                    if (drawableArr2[1] != null) {
                        if (drawableArr2[1] instanceof AnimatedEmojiDrawable) {
                            ((AnimatedEmojiDrawable) drawableArr2[1]).removeView((Drawable.Callback) this.parentView);
                        }
                        this.drawables[1] = null;
                    }
                    Drawable[] drawableArr3 = this.drawables;
                    drawableArr3[1] = drawableArr3[0];
                    drawableArr3[0] = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i, j);
                    ((AnimatedEmojiDrawable) this.drawables[0]).addView(this.parentView);
                } else {
                    this.changeProgress.set(1.0f, true);
                    detach();
                    this.drawables[0] = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i, j);
                    ((AnimatedEmojiDrawable) this.drawables[0]).addView(this.parentView);
                }
                this.lastColor = -1;
                this.colorFilter = null;
                play();
                View view = this.parentView;
                if (view != null) {
                    view.invalidate();
                }
            }
        }

        public void set(TLRPC$Document tLRPC$Document, boolean z) {
            set(tLRPC$Document, this.cacheType, z);
        }

        public void set(TLRPC$Document tLRPC$Document, int i, boolean z) {
            Drawable[] drawableArr = this.drawables;
            if (!(drawableArr[0] instanceof AnimatedEmojiDrawable) || tLRPC$Document == null || ((AnimatedEmojiDrawable) drawableArr[0]).getDocumentId() != tLRPC$Document.id) {
                if (z) {
                    this.changeProgress.set(0.0f, true);
                    Drawable[] drawableArr2 = this.drawables;
                    if (drawableArr2[1] != null) {
                        if (drawableArr2[1] instanceof AnimatedEmojiDrawable) {
                            ((AnimatedEmojiDrawable) drawableArr2[1]).removeView((Drawable.Callback) this.parentView);
                        }
                        this.drawables[1] = null;
                    }
                    Drawable[] drawableArr3 = this.drawables;
                    drawableArr3[1] = drawableArr3[0];
                    if (tLRPC$Document != null) {
                        drawableArr3[0] = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i, tLRPC$Document);
                        ((AnimatedEmojiDrawable) this.drawables[0]).addView(this.parentView);
                    } else {
                        drawableArr3[0] = null;
                    }
                } else {
                    this.changeProgress.set(1.0f, true);
                    detach();
                    if (tLRPC$Document != null) {
                        this.drawables[0] = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i, tLRPC$Document);
                        ((AnimatedEmojiDrawable) this.drawables[0]).addView(this.parentView);
                    } else {
                        this.drawables[0] = null;
                    }
                }
                this.lastColor = -1;
                this.colorFilter = null;
                play();
                View view = this.parentView;
                if (view != null) {
                    view.invalidate();
                }
            }
        }

        public void set(Drawable drawable, boolean z) {
            if (this.drawables[0] != drawable) {
                if (z) {
                    this.changeProgress.set(0.0f, true);
                    Drawable[] drawableArr = this.drawables;
                    if (drawableArr[1] != null) {
                        if (drawableArr[1] instanceof AnimatedEmojiDrawable) {
                            ((AnimatedEmojiDrawable) drawableArr[1]).removeView((Drawable.Callback) this.parentView);
                        }
                        this.drawables[1] = null;
                    }
                    Drawable[] drawableArr2 = this.drawables;
                    drawableArr2[1] = drawableArr2[0];
                    drawableArr2[0] = drawable;
                } else {
                    this.changeProgress.set(1.0f, true);
                    detach();
                    this.drawables[0] = drawable;
                }
                this.lastColor = -1;
                this.colorFilter = null;
                play();
                View view = this.parentView;
                if (view != null) {
                    view.invalidate();
                }
            }
        }

        public void detach() {
            Drawable[] drawableArr = this.drawables;
            if (drawableArr[0] instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawableArr[0]).removeView((Drawable.Callback) this.parentView);
            }
            Drawable[] drawableArr2 = this.drawables;
            if (drawableArr2[1] instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawableArr2[1]).removeView((Drawable.Callback) this.parentView);
            }
        }

        public void attach() {
            Drawable[] drawableArr = this.drawables;
            if (drawableArr[0] instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawableArr[0]).addView(this.parentView);
            }
            Drawable[] drawableArr2 = this.drawables;
            if (drawableArr2[1] instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawableArr2[1]).addView(this.parentView);
            }
        }

        public int getIntrinsicWidth() {
            return this.size;
        }

        public int getIntrinsicHeight() {
            return this.size;
        }

        public void setAlpha(int i) {
            this.alpha = i;
        }
    }
}
