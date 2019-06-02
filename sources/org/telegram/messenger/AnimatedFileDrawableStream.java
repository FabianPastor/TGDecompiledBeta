package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.Document;

public class AnimatedFileDrawableStream implements FileLoadOperationStream {
    private volatile boolean canceled;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private Document document;
    private int lastOffset;
    private FileLoadOperation loadOperation;
    private Object parentObject;
    private final Object sync = new Object();
    private boolean waitingForLoad;

    public AnimatedFileDrawableStream(Document document, Object obj, int i) {
        this.document = document;
        this.parentObject = obj;
        this.currentAccount = i;
        this.loadOperation = FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.parentObject, 0);
    }

    /* JADX WARNING: Missing block: B:8:0x000b, code skipped:
            if (r7 != 0) goto L_0x000e;
     */
    /* JADX WARNING: Missing block: B:9:0x000d, code skipped:
            return 0;
     */
    /* JADX WARNING: Missing block: B:10:0x000e, code skipped:
            r0 = 0;
     */
    /* JADX WARNING: Missing block: B:11:0x000f, code skipped:
            if (r0 != 0) goto L_0x005c;
     */
    /* JADX WARNING: Missing block: B:13:?, code skipped:
            r0 = r5.loadOperation.getDownloadedLengthFromOffset(r6, r7);
     */
    /* JADX WARNING: Missing block: B:14:0x0017, code skipped:
            if (r0 != 0) goto L_0x000f;
     */
    /* JADX WARNING: Missing block: B:16:0x001f, code skipped:
            if (r5.loadOperation.isPaused() != false) goto L_0x0025;
     */
    /* JADX WARNING: Missing block: B:18:0x0023, code skipped:
            if (r5.lastOffset == r6) goto L_0x0032;
     */
    /* JADX WARNING: Missing block: B:19:0x0025, code skipped:
            org.telegram.messenger.FileLoader.getInstance(r5.currentAccount).loadStreamFile(r5, r5.document, r5.parentObject, r6);
     */
    /* JADX WARNING: Missing block: B:20:0x0032, code skipped:
            r1 = r5.sync;
     */
    /* JADX WARNING: Missing block: B:21:0x0034, code skipped:
            monitor-enter(r1);
     */
    /* JADX WARNING: Missing block: B:24:0x0037, code skipped:
            if (r5.canceled == false) goto L_0x003b;
     */
    /* JADX WARNING: Missing block: B:25:0x0039, code skipped:
            monitor-exit(r1);
     */
    /* JADX WARNING: Missing block: B:26:0x003a, code skipped:
            return 0;
     */
    /* JADX WARNING: Missing block: B:27:0x003b, code skipped:
            r5.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Missing block: B:28:0x0043, code skipped:
            monitor-exit(r1);
     */
    /* JADX WARNING: Missing block: B:30:?, code skipped:
            org.telegram.messenger.FileLoader.getInstance(r5.currentAccount).setLoadingVideo(r5.document, false, true);
            r5.waitingForLoad = true;
            r5.countDownLatch.await();
            r5.waitingForLoad = false;
     */
    /* JADX WARNING: Missing block: B:37:0x005c, code skipped:
            r5.lastOffset = r6 + r0;
     */
    /* JADX WARNING: Missing block: B:38:0x0060, code skipped:
            r6 = move-exception;
     */
    /* JADX WARNING: Missing block: B:39:0x0061, code skipped:
            org.telegram.messenger.FileLog.e(r6);
     */
    /* JADX WARNING: Missing block: B:40:0x0064, code skipped:
            return r0;
     */
    public int read(int r6, int r7) {
        /*
        r5 = this;
        r0 = r5.sync;
        monitor-enter(r0);
        r1 = r5.canceled;	 Catch:{ all -> 0x0065 }
        r2 = 0;
        if (r1 == 0) goto L_0x000a;
    L_0x0008:
        monitor-exit(r0);	 Catch:{ all -> 0x0065 }
        return r2;
    L_0x000a:
        monitor-exit(r0);	 Catch:{ all -> 0x0065 }
        if (r7 != 0) goto L_0x000e;
    L_0x000d:
        return r2;
    L_0x000e:
        r0 = 0;
    L_0x000f:
        if (r0 != 0) goto L_0x005c;
    L_0x0011:
        r1 = r5.loadOperation;	 Catch:{ Exception -> 0x0060 }
        r0 = r1.getDownloadedLengthFromOffset(r6, r7);	 Catch:{ Exception -> 0x0060 }
        if (r0 != 0) goto L_0x000f;
    L_0x0019:
        r1 = r5.loadOperation;	 Catch:{ Exception -> 0x0060 }
        r1 = r1.isPaused();	 Catch:{ Exception -> 0x0060 }
        if (r1 != 0) goto L_0x0025;
    L_0x0021:
        r1 = r5.lastOffset;	 Catch:{ Exception -> 0x0060 }
        if (r1 == r6) goto L_0x0032;
    L_0x0025:
        r1 = r5.currentAccount;	 Catch:{ Exception -> 0x0060 }
        r1 = org.telegram.messenger.FileLoader.getInstance(r1);	 Catch:{ Exception -> 0x0060 }
        r3 = r5.document;	 Catch:{ Exception -> 0x0060 }
        r4 = r5.parentObject;	 Catch:{ Exception -> 0x0060 }
        r1.loadStreamFile(r5, r3, r4, r6);	 Catch:{ Exception -> 0x0060 }
    L_0x0032:
        r1 = r5.sync;	 Catch:{ Exception -> 0x0060 }
        monitor-enter(r1);	 Catch:{ Exception -> 0x0060 }
        r3 = r5.canceled;	 Catch:{ all -> 0x0059 }
        if (r3 == 0) goto L_0x003b;
    L_0x0039:
        monitor-exit(r1);	 Catch:{ all -> 0x0059 }
        return r2;
    L_0x003b:
        r3 = new java.util.concurrent.CountDownLatch;	 Catch:{ all -> 0x0059 }
        r4 = 1;
        r3.<init>(r4);	 Catch:{ all -> 0x0059 }
        r5.countDownLatch = r3;	 Catch:{ all -> 0x0059 }
        monitor-exit(r1);	 Catch:{ all -> 0x0059 }
        r1 = r5.currentAccount;	 Catch:{ Exception -> 0x0060 }
        r1 = org.telegram.messenger.FileLoader.getInstance(r1);	 Catch:{ Exception -> 0x0060 }
        r3 = r5.document;	 Catch:{ Exception -> 0x0060 }
        r1.setLoadingVideo(r3, r2, r4);	 Catch:{ Exception -> 0x0060 }
        r5.waitingForLoad = r4;	 Catch:{ Exception -> 0x0060 }
        r1 = r5.countDownLatch;	 Catch:{ Exception -> 0x0060 }
        r1.await();	 Catch:{ Exception -> 0x0060 }
        r5.waitingForLoad = r2;	 Catch:{ Exception -> 0x0060 }
        goto L_0x000f;
    L_0x0059:
        r6 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0059 }
        throw r6;	 Catch:{ Exception -> 0x0060 }
    L_0x005c:
        r6 = r6 + r0;
        r5.lastOffset = r6;	 Catch:{ Exception -> 0x0060 }
        goto L_0x0064;
    L_0x0060:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
    L_0x0064:
        return r0;
    L_0x0065:
        r6 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0065 }
        goto L_0x0069;
    L_0x0068:
        throw r6;
    L_0x0069:
        goto L_0x0068;
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
                if (z && !this.canceled) {
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

    public Document getDocument() {
        return this.document;
    }

    public Object getParentObject() {
        return this.document;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public boolean isWaitingForLoad() {
        return this.waitingForLoad;
    }

    public void newDataAvailable() {
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
}
