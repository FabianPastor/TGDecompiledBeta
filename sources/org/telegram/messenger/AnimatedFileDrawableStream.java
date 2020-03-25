package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC$Document;

public class AnimatedFileDrawableStream implements FileLoadOperationStream {
    private volatile boolean canceled;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private TLRPC$Document document;
    private int lastOffset;
    private FileLoadOperation loadOperation;
    private Object parentObject;
    private boolean preview;
    private final Object sync = new Object();
    private boolean waitingForLoad;

    public AnimatedFileDrawableStream(TLRPC$Document tLRPC$Document, Object obj, int i, boolean z) {
        this.document = tLRPC$Document;
        this.parentObject = obj;
        this.currentAccount = i;
        this.preview = z;
        this.loadOperation = FileLoader.getInstance(i).loadStreamFile(this, this.document, this.parentObject, 0, this.preview);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000e, code lost:
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x000f, code lost:
        if (r0 != 0) goto L_0x0068;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r0 = r9.loadOperation.getDownloadedLengthFromOffset(r10, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0017, code lost:
        if (r0 != 0) goto L_0x000f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x001f, code lost:
        if (r9.loadOperation.isPaused() != false) goto L_0x0029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0023, code lost:
        if (r9.lastOffset != r10) goto L_0x0029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0027, code lost:
        if (r9.preview == false) goto L_0x003a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0029, code lost:
        org.telegram.messenger.FileLoader.getInstance(r9.currentAccount).loadStreamFile(r9, r9.document, r9.parentObject, r10, r9.preview);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003a, code lost:
        r1 = r9.sync;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003c, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x003f, code lost:
        if (r9.canceled == false) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0041, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0042, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0043, code lost:
        r9.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x004b, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x004e, code lost:
        if (r9.preview != false) goto L_0x005b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0050, code lost:
        org.telegram.messenger.FileLoader.getInstance(r9.currentAccount).setLoadingVideo(r9.document, false, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x005b, code lost:
        r9.waitingForLoad = true;
        r9.countDownLatch.await();
        r9.waitingForLoad = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0068, code lost:
        r9.lastOffset = r10 + r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x006c, code lost:
        r10 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x006d, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0070, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000b, code lost:
        if (r11 != 0) goto L_0x000e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000d, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(int r10, int r11) {
        /*
            r9 = this;
            java.lang.Object r0 = r9.sync
            monitor-enter(r0)
            boolean r1 = r9.canceled     // Catch:{ all -> 0x0071 }
            r2 = 0
            if (r1 == 0) goto L_0x000a
            monitor-exit(r0)     // Catch:{ all -> 0x0071 }
            return r2
        L_0x000a:
            monitor-exit(r0)     // Catch:{ all -> 0x0071 }
            if (r11 != 0) goto L_0x000e
            return r2
        L_0x000e:
            r0 = 0
        L_0x000f:
            if (r0 != 0) goto L_0x0068
            org.telegram.messenger.FileLoadOperation r1 = r9.loadOperation     // Catch:{ Exception -> 0x006c }
            int r0 = r1.getDownloadedLengthFromOffset(r10, r11)     // Catch:{ Exception -> 0x006c }
            if (r0 != 0) goto L_0x000f
            org.telegram.messenger.FileLoadOperation r1 = r9.loadOperation     // Catch:{ Exception -> 0x006c }
            boolean r1 = r1.isPaused()     // Catch:{ Exception -> 0x006c }
            if (r1 != 0) goto L_0x0029
            int r1 = r9.lastOffset     // Catch:{ Exception -> 0x006c }
            if (r1 != r10) goto L_0x0029
            boolean r1 = r9.preview     // Catch:{ Exception -> 0x006c }
            if (r1 == 0) goto L_0x003a
        L_0x0029:
            int r1 = r9.currentAccount     // Catch:{ Exception -> 0x006c }
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x006c }
            org.telegram.tgnet.TLRPC$Document r5 = r9.document     // Catch:{ Exception -> 0x006c }
            java.lang.Object r6 = r9.parentObject     // Catch:{ Exception -> 0x006c }
            boolean r8 = r9.preview     // Catch:{ Exception -> 0x006c }
            r4 = r9
            r7 = r10
            r3.loadStreamFile(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x006c }
        L_0x003a:
            java.lang.Object r1 = r9.sync     // Catch:{ Exception -> 0x006c }
            monitor-enter(r1)     // Catch:{ Exception -> 0x006c }
            boolean r3 = r9.canceled     // Catch:{ all -> 0x0065 }
            if (r3 == 0) goto L_0x0043
            monitor-exit(r1)     // Catch:{ all -> 0x0065 }
            return r2
        L_0x0043:
            java.util.concurrent.CountDownLatch r3 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x0065 }
            r4 = 1
            r3.<init>(r4)     // Catch:{ all -> 0x0065 }
            r9.countDownLatch = r3     // Catch:{ all -> 0x0065 }
            monitor-exit(r1)     // Catch:{ all -> 0x0065 }
            boolean r1 = r9.preview     // Catch:{ Exception -> 0x006c }
            if (r1 != 0) goto L_0x005b
            int r1 = r9.currentAccount     // Catch:{ Exception -> 0x006c }
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x006c }
            org.telegram.tgnet.TLRPC$Document r3 = r9.document     // Catch:{ Exception -> 0x006c }
            r1.setLoadingVideo(r3, r2, r4)     // Catch:{ Exception -> 0x006c }
        L_0x005b:
            r9.waitingForLoad = r4     // Catch:{ Exception -> 0x006c }
            java.util.concurrent.CountDownLatch r1 = r9.countDownLatch     // Catch:{ Exception -> 0x006c }
            r1.await()     // Catch:{ Exception -> 0x006c }
            r9.waitingForLoad = r2     // Catch:{ Exception -> 0x006c }
            goto L_0x000f
        L_0x0065:
            r10 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0065 }
            throw r10     // Catch:{ Exception -> 0x006c }
        L_0x0068:
            int r10 = r10 + r0
            r9.lastOffset = r10     // Catch:{ Exception -> 0x006c }
            goto L_0x0070
        L_0x006c:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x0070:
            return r0
        L_0x0071:
            r10 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0071 }
            goto L_0x0075
        L_0x0074:
            throw r10
        L_0x0075:
            goto L_0x0074
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AnimatedFileDrawableStream.read(int, int):int");
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean z) {
        synchronized (this.sync) {
            if (this.countDownLatch != null) {
                this.countDownLatch.countDown();
                if (z && !this.canceled && !this.preview) {
                    FileLoader.getInstance(this.currentAccount).removeLoadingVideo(this.document, false, true);
                }
            }
            this.canceled = true;
        }
    }

    public void reset() {
        synchronized (this.sync) {
            this.canceled = false;
        }
    }

    public TLRPC$Document getDocument() {
        return this.document;
    }

    public Object getParentObject() {
        return this.document;
    }

    public boolean isPreview() {
        return this.preview;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public boolean isWaitingForLoad() {
        return this.waitingForLoad;
    }

    public void newDataAvailable() {
        CountDownLatch countDownLatch2 = this.countDownLatch;
        if (countDownLatch2 != null) {
            countDownLatch2.countDown();
        }
    }
}
