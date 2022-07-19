package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getCustomEmojiDocuments;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;

public class AnimatedEmojiDrawable extends Drawable {
    static int count = 0;
    private static HashMap<Integer, EmojiDocumentFetcher> fetchers = null;
    private static HashMap<Integer, HashMap<Long, AnimatedEmojiDrawable>> globalEmojiCache = null;
    private static Paint placeholderPaint = null;
    public static int sizedp = 30;
    private float alpha = 1.0f;
    private boolean attached;
    private int cacheType;
    private TLRPC$Document document;
    private long documentId;
    private ArrayList<AnimatedEmojiSpan.AnimatedEmojiHolder> holders;
    private ImageReceiver imageReceiver;
    private ArrayList<View> views;

    interface ReceivedDocument {
        void run(TLRPC$Document tLRPC$Document);
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
        private ArrayList<Long> toFetchDocuments;

        public EmojiDocumentFetcher(int i) {
            this.currentAccount = i;
        }

        public void fetchDocument(long j, ReceivedDocument receivedDocument) {
            TLRPC$Document tLRPC$Document;
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
                    this.toFetchDocuments = new ArrayList<>();
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
            this.fetchRunnable = null;
            loadFromDatabase(new ArrayList(this.toFetchDocuments));
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
                ArrayList arrayList3 = new ArrayList(arrayList);
                while (queryFinalized.next()) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    try {
                        TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(true), true);
                        if (!(TLdeserialize == null || TLdeserialize.id == 0)) {
                            arrayList2.add(TLdeserialize);
                            arrayList3.remove(Long.valueOf(TLdeserialize.id));
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    if (byteBufferValue != null) {
                        byteBufferValue.reuse();
                    }
                }
                AndroidUtilities.runOnUIThread(new AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda3(this, arrayList2, arrayList3));
                queryFinalized.dispose();
            } catch (SQLiteException e2) {
                FileLog.e((Throwable) e2);
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFromDatabase$1(ArrayList arrayList, ArrayList arrayList2) {
            processDocuments(arrayList);
            if (!arrayList2.isEmpty()) {
                loadFromServer(arrayList2);
            }
        }

        private void loadFromServer(ArrayList<Long> arrayList) {
            TLRPC$TL_messages_getCustomEmojiDocuments tLRPC$TL_messages_getCustomEmojiDocuments = new TLRPC$TL_messages_getCustomEmojiDocuments();
            tLRPC$TL_messages_getCustomEmojiDocuments.document_id = arrayList;
            this.toFetchDocuments.clear();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getCustomEmojiDocuments, new AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda5(this, tLRPC$TL_messages_getCustomEmojiDocuments));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFromServer$4(TLRPC$TL_messages_getCustomEmojiDocuments tLRPC$TL_messages_getCustomEmojiDocuments, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda4(this, tLObject, tLRPC$TL_messages_getCustomEmojiDocuments));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadFromServer$3(TLObject tLObject, TLRPC$TL_messages_getCustomEmojiDocuments tLRPC$TL_messages_getCustomEmojiDocuments) {
            if (tLObject instanceof TLRPC$Vector) {
                ArrayList<Object> arrayList = ((TLRPC$Vector) tLObject).objects;
                putToStorage(arrayList);
                processDocuments(arrayList);
                if (tLRPC$TL_messages_getCustomEmojiDocuments.document_id.size() > arrayList.size()) {
                    for (Long longValue : this.loadingDocuments.keySet()) {
                        fetchDocument(longValue.longValue(), (ReceivedDocument) null);
                    }
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
        this.cacheType = i;
        if (i == 0) {
            TextPaint textPaint = Theme.chat_msgTextPaint;
            sizedp = (int) (((Math.abs(textPaint.ascent()) + Math.abs(textPaint.descent())) * 1.15f) / AndroidUtilities.density);
        } else if (i == 1 || i == 3 || i == 2) {
            sizedp = 34;
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
        this.cacheType = i;
        this.document = tLRPC$Document;
        AndroidUtilities.runOnUIThread(new AnimatedEmojiDrawable$$ExternalSyntheticLambda0(this));
    }

    public long getDocumentId() {
        return this.documentId;
    }

    /* access modifiers changed from: private */
    public void initDocument() {
        String str;
        if (this.document != null && this.imageReceiver == null) {
            AnonymousClass1 r1 = new ImageReceiver() {
                public void invalidate() {
                    super.invalidate();
                    AnimatedEmojiDrawable.this.invalidate();
                }

                /* access modifiers changed from: protected */
                public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                    AnimatedEmojiDrawable.this.invalidate();
                    return super.setImageBitmapByKey(drawable, str, i, z, i2);
                }
            };
            this.imageReceiver = r1;
            if (this.cacheType != 0) {
                r1.setUniqKeyPrefix(this.cacheType + "_");
            }
            if ("video/webm".equals(this.document.mime_type)) {
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.document.thumbs, 90);
                this.imageReceiver.setParentView(new View(ApplicationLoader.applicationContext) {
                    public void invalidate() {
                        AnimatedEmojiDrawable.this.invalidate();
                    }
                });
                ImageLocation forDocument = ImageLocation.getForDocument(closestPhotoSizeWithSize, this.document);
                TLRPC$Document tLRPC$Document = this.document;
                this.imageReceiver.setImage(ImageLocation.getForDocument(this.document), sizedp + "_" + sizedp + "_pcache_" + "g", forDocument, (String) null, (Drawable) null, tLRPC$Document.size, (String) null, tLRPC$Document, 1);
            } else {
                SvgHelper.SvgDrawable svgDrawable = null;
                StringBuilder sb = new StringBuilder();
                if (this.cacheType != 0) {
                    str = this.cacheType + "_";
                } else {
                    str = "";
                }
                sb.append(str);
                sb.append(this.documentId);
                sb.append("@");
                sb.append(sizedp);
                sb.append("_");
                sb.append(sizedp);
                sb.append("_pcache");
                if (!ImageLoader.getInstance().hasLottieMemCache(sb.toString()) && (svgDrawable = DocumentObject.getSvgThumb(this.document.thumbs, "windowBackgroundWhiteGrayIcon", 0.2f)) != null) {
                    svgDrawable.overrideWidthAndHeight(512, 512);
                }
                SvgHelper.SvgDrawable svgDrawable2 = svgDrawable;
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(this.document.thumbs, 90);
                this.imageReceiver.setImage(ImageLocation.getForDocument(this.document), sizedp + "_" + sizedp + "_pcache", ImageLocation.getForDocument(closestPhotoSizeWithSize2, this.document), (String) null, (ImageLocation) null, (String) null, svgDrawable2, 0, (String) null, this.document, 1);
            }
            if (this.cacheType == 2) {
                this.imageReceiver.setLayerNum(7);
            }
            this.imageReceiver.setAspectFit(true);
            this.imageReceiver.setAllowStartLottieAnimation(true);
            this.imageReceiver.setAllowStartAnimation(true);
            this.imageReceiver.setAutoRepeat(1);
            this.imageReceiver.setAllowDecodeSingleFrame(true);
            updateAttachState();
        }
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
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImageCoords(getBounds());
            this.imageReceiver.draw(canvas);
            return;
        }
        if (placeholderPaint == null) {
            Paint paint = new Paint(1);
            placeholderPaint = paint;
            paint.setColor(Theme.isCurrentThemeDark() ? NUM : NUM);
        }
        int alpha2 = placeholderPaint.getAlpha();
        placeholderPaint.setAlpha((int) (((float) alpha2) * this.alpha));
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(getBounds());
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.width() * 0.4f, placeholderPaint);
        placeholderPaint.setAlpha(alpha2);
    }

    public void draw(Canvas canvas, Rect rect, float f) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.setImageCoords(rect);
            this.imageReceiver.setAlpha(f);
            this.imageReceiver.draw(canvas);
            return;
        }
        if (placeholderPaint == null) {
            Paint paint = new Paint(1);
            placeholderPaint = paint;
            paint.setColor(Theme.isCurrentThemeDark() ? NUM : NUM);
        }
        int alpha2 = placeholderPaint.getAlpha();
        placeholderPaint.setAlpha((int) (((float) alpha2) * f));
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(rect);
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.width() * 0.4f, placeholderPaint);
        placeholderPaint.setAlpha(alpha2);
    }

    public void draw(Canvas canvas, ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder) {
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            imageReceiver2.draw(canvas, backgroundThreadDrawHolder);
            return;
        }
        if (placeholderPaint == null) {
            Paint paint = new Paint(1);
            placeholderPaint = paint;
            paint.setColor(Theme.isCurrentThemeDark() ? NUM : NUM);
        }
        int alpha2 = placeholderPaint.getAlpha();
        placeholderPaint.setAlpha((int) (((float) alpha2) * this.alpha));
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(getBounds());
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.width() * 0.4f, placeholderPaint);
        placeholderPaint.setAlpha(alpha2);
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
        ImageReceiver imageReceiver2 = this.imageReceiver;
        if (imageReceiver2 != null) {
            float f = ((float) i) / 255.0f;
            this.alpha = f;
            imageReceiver2.setAlpha(f);
        }
    }

    public ImageReceiver getImageReceiver() {
        return this.imageReceiver;
    }
}
