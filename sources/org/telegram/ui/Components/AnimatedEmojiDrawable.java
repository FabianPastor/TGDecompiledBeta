package org.telegram.ui.Components;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
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
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getCustomEmojiDocuments;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;

public class AnimatedEmojiDrawable extends Drawable {
    static int count;
    private static HashMap<Integer, EmojiDocumentFetcher> fetchers;
    private static HashMap<Integer, HashMap<Long, AnimatedEmojiDrawable>> globalEmojiCache;
    private float alpha = 1.0f;
    private boolean attached;
    private int cacheType;
    private TLRPC$Document document;
    private long documentId;
    private ArrayList<AnimatedEmojiSpan.AnimatedEmojiHolder> holders;
    private ImageReceiver imageReceiver;
    public int sizedp;
    private ArrayList<View> views;

    public interface ReceivedDocument {
        void run(TLRPC$Document tLRPC$Document);
    }

    private void drawDebugCacheType(Canvas canvas) {
    }

    private void drawPlaceholder(Canvas canvas, float f, float f2, float f3) {
    }

    public int getOpacity() {
        return -2;
    }

    public void setColorFilter(ColorFilter colorFilter) {
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
            imageReceiver2.setCurrentTime(j);
        }
    }

    public void update(long j) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
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
        if (i == 0) {
            this.sizedp = (int) (((Math.abs(Theme.chat_msgTextPaint.ascent()) + Math.abs(Theme.chat_msgTextPaint.descent())) * 1.15f) / AndroidUtilities.density);
        } else if (i == 1) {
            this.sizedp = (int) (((Math.abs(Theme.chat_msgTextPaintOneEmoji.ascent()) + Math.abs(Theme.chat_msgTextPaintOneEmoji.descent())) * 1.15f) / AndroidUtilities.density);
        } else if (i == 2 || i == 4 || i == 3) {
            this.sizedp = 34;
        } else {
            this.sizedp = 34;
        }
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
        if (i == 0) {
            this.sizedp = (int) (((Math.abs(Theme.chat_msgTextPaint.ascent()) + Math.abs(Theme.chat_msgTextPaint.descent())) * 1.15f) / AndroidUtilities.density);
        } else if (i == 1) {
            this.sizedp = (int) (((Math.abs(Theme.chat_msgTextPaintOneEmoji.ascent()) + Math.abs(Theme.chat_msgTextPaintOneEmoji.descent())) * 1.15f) / AndroidUtilities.density);
        } else if (i == 2 || i == 4 || i == 3) {
            this.sizedp = 34;
        } else {
            this.sizedp = 34;
        }
        initDocument();
    }

    public long getDocumentId() {
        TLRPC$Document tLRPC$Document = this.document;
        return tLRPC$Document != null ? tLRPC$Document.id : this.documentId;
    }

    public TLRPC$Document getDocument() {
        return this.document;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0068, code lost:
        r2 = r0.cacheType;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initDocument() {
        /*
            r23 = this;
            r0 = r23
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            if (r1 == 0) goto L_0x016a
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            if (r1 == 0) goto L_0x000c
            goto L_0x016a
        L_0x000c:
            org.telegram.ui.Components.AnimatedEmojiDrawable$1 r1 = new org.telegram.ui.Components.AnimatedEmojiDrawable$1
            r1.<init>()
            r0.imageReceiver = r1
            int r2 = r0.cacheType
            java.lang.String r3 = "_"
            if (r2 == 0) goto L_0x002d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r4 = r0.cacheType
            r2.append(r4)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.setUniqKeyPrefix(r2)
        L_0x002d:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r2 = r0.sizedp
            r1.append(r2)
            r1.append(r3)
            int r2 = r0.sizedp
            r1.append(r2)
            java.lang.String r2 = "_pcache"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            int r2 = r0.cacheType
            r4 = 1
            if (r2 == 0) goto L_0x0060
            if (r2 == r4) goto L_0x0060
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            java.lang.String r1 = "_compress"
            r2.append(r1)
            java.lang.String r1 = r2.toString()
        L_0x0060:
            int r2 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
            r5 = 3
            r6 = 2
            if (r2 != 0) goto L_0x0070
            int r2 = r0.cacheType
            if (r2 == r6) goto L_0x006e
            if (r2 != r5) goto L_0x0070
        L_0x006e:
            r2 = 1
            goto L_0x0071
        L_0x0070:
            r2 = 0
        L_0x0071:
            org.telegram.tgnet.TLRPC$Document r7 = r0.document
            java.lang.String r7 = r7.mime_type
            java.lang.String r8 = "video/webm"
            boolean r7 = r8.equals(r7)
            r8 = 90
            r9 = 0
            if (r7 == 0) goto L_0x00a6
            org.telegram.tgnet.TLRPC$Document r6 = r0.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r8)
            org.telegram.tgnet.TLRPC$Document r7 = r0.document
            org.telegram.messenger.ImageLocation r7 = org.telegram.messenger.ImageLocation.getForDocument(r7)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r1)
            r8.append(r3)
            java.lang.String r1 = "g"
            r8.append(r1)
            java.lang.String r1 = r8.toString()
            r12 = r1
            r17 = r9
            goto L_0x010f
        L_0x00a6:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            int r10 = r0.cacheType
            if (r10 == 0) goto L_0x00c1
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            int r11 = r0.cacheType
            r10.append(r11)
            r10.append(r3)
            java.lang.String r10 = r10.toString()
            goto L_0x00c3
        L_0x00c1:
            java.lang.String r10 = ""
        L_0x00c3:
            r7.append(r10)
            long r10 = r0.documentId
            r7.append(r10)
            java.lang.String r10 = "@"
            r7.append(r10)
            r7.append(r1)
            java.lang.String r7 = r7.toString()
            int r10 = r0.cacheType
            if (r10 == r6) goto L_0x00e8
            org.telegram.messenger.ImageLoader r6 = org.telegram.messenger.ImageLoader.getInstance()
            boolean r6 = r6.hasLottieMemCache(r7)
            if (r6 != 0) goto L_0x00e6
            goto L_0x00e8
        L_0x00e6:
            r6 = r9
            goto L_0x00fc
        L_0x00e8:
            org.telegram.tgnet.TLRPC$Document r6 = r0.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.thumbs
            r7 = 1045220557(0x3e4ccccd, float:0.2)
            java.lang.String r10 = "windowBackgroundWhiteGrayIcon"
            org.telegram.messenger.SvgHelper$SvgDrawable r6 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r6, (java.lang.String) r10, (float) r7)
            if (r6 == 0) goto L_0x00fc
            r7 = 512(0x200, float:7.175E-43)
            r6.overrideWidthAndHeight(r7, r7)
        L_0x00fc:
            org.telegram.tgnet.TLRPC$Document r7 = r0.document
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r7.thumbs
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
            org.telegram.tgnet.TLRPC$Document r8 = r0.document
            org.telegram.messenger.ImageLocation r8 = org.telegram.messenger.ImageLocation.getForDocument(r8)
            r12 = r1
            r17 = r6
            r6 = r7
            r7 = r8
        L_0x010f:
            if (r2 == 0) goto L_0x0113
            r11 = r9
            goto L_0x0114
        L_0x0113:
            r11 = r7
        L_0x0114:
            org.telegram.messenger.ImageReceiver r10 = r0.imageReceiver
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForDocument((org.telegram.tgnet.TLRPC$PhotoSize) r6, (org.telegram.tgnet.TLRPC$Document) r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r2 = r0.sizedp
            r1.append(r2)
            r1.append(r3)
            int r2 = r0.sizedp
            r1.append(r2)
            java.lang.String r14 = r1.toString()
            r15 = 0
            r16 = 0
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            long r2 = r1.size
            r20 = 0
            r22 = 1
            r18 = r2
            r21 = r1
            r10.setImage(r11, r12, r13, r14, r15, r16, r17, r18, r20, r21, r22)
            int r1 = r0.cacheType
            if (r1 != r5) goto L_0x014e
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r2 = 7
            r1.setLayerNum(r2)
        L_0x014e:
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAspectFit(r4)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAllowStartLottieAnimation(r4)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAllowStartAnimation(r4)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAutoRepeat(r4)
            org.telegram.messenger.ImageReceiver r1 = r0.imageReceiver
            r1.setAllowDecodeSingleFrame(r4)
            r23.updateAttachState()
        L_0x016a:
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

    public void draw(Canvas canvas) {
        drawDebugCacheType(canvas);
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImageCoords(getBounds());
            this.imageReceiver.setAlpha(this.alpha);
            this.imageReceiver.draw(canvas);
        }
        drawPlaceholder(canvas, (float) getBounds().centerX(), (float) getBounds().centerY(), ((float) getBounds().width()) / 2.0f);
    }

    public void draw(Canvas canvas, Rect rect, float f) {
        drawDebugCacheType(canvas);
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
        drawDebugCacheType(canvas);
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

    public void removeView(View view) {
        ArrayList<View> arrayList = this.views;
        if (arrayList != null) {
            arrayList.remove(view);
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
            int r0 = count
            int r0 = r0 + r1
            count = r0
            org.telegram.messenger.ImageReceiver r0 = r3.imageReceiver
            r0.onAttachedToWindow()
            goto L_0x003a
        L_0x0030:
            int r0 = count
            int r0 = r0 - r1
            count = r0
            org.telegram.messenger.ImageReceiver r0 = r3.imageReceiver
            r0.onDetachedFromWindow()
        L_0x003a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedEmojiDrawable.updateAttachState():void");
    }

    public void setAlpha(int i) {
        float f = ((float) i) / 255.0f;
        this.alpha = f;
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setAlpha(f);
        }
    }

    public ImageReceiver getImageReceiver() {
        return this.imageReceiver;
    }
}
