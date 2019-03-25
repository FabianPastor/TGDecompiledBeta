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

    public AnimatedFileDrawableStream(Document d, Object p, int a) {
        this.document = d;
        this.parentObject = p;
        this.currentAccount = a;
        this.loadOperation = FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.parentObject, 0);
    }

    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* JADX WARNING: Missing block: B:8:0x000c, code skipped:
            if (r9 != 0) goto L_0x0013;
     */
    /* JADX WARNING: Missing block: B:13:0x0013, code skipped:
            r0 = 0;
     */
    /* JADX WARNING: Missing block: B:14:0x0014, code skipped:
            if (r0 != 0) goto L_0x006b;
     */
    /* JADX WARNING: Missing block: B:16:?, code skipped:
            r0 = r7.loadOperation.getDownloadedLengthFromOffset(r8, r9);
     */
    /* JADX WARNING: Missing block: B:17:0x001c, code skipped:
            if (r0 != 0) goto L_0x0014;
     */
    /* JADX WARNING: Missing block: B:19:0x0024, code skipped:
            if (r7.loadOperation.isPaused() != false) goto L_0x002a;
     */
    /* JADX WARNING: Missing block: B:21:0x0028, code skipped:
            if (r7.lastOffset == r8) goto L_0x0037;
     */
    /* JADX WARNING: Missing block: B:22:0x002a, code skipped:
            org.telegram.messenger.FileLoader.getInstance(r7.currentAccount).loadStreamFile(r7, r7.document, r7.parentObject, r8);
     */
    /* JADX WARNING: Missing block: B:23:0x0037, code skipped:
            r3 = r7.sync;
     */
    /* JADX WARNING: Missing block: B:24:0x0039, code skipped:
            monitor-enter(r3);
     */
    /* JADX WARNING: Missing block: B:27:0x003c, code skipped:
            if (r7.canceled == false) goto L_0x0041;
     */
    /* JADX WARNING: Missing block: B:28:0x003e, code skipped:
            monitor-exit(r3);
     */
    /* JADX WARNING: Missing block: B:30:0x0041, code skipped:
            r7.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Missing block: B:31:0x0049, code skipped:
            monitor-exit(r3);
     */
    /* JADX WARNING: Missing block: B:33:?, code skipped:
            org.telegram.messenger.FileLoader.getInstance(r7.currentAccount).setLoadingVideo(r7.document, false, true);
            r7.waitingForLoad = true;
            r7.countDownLatch.await();
            r7.waitingForLoad = false;
     */
    /* JADX WARNING: Missing block: B:35:0x0063, code skipped:
            r1 = move-exception;
     */
    /* JADX WARNING: Missing block: B:36:0x0064, code skipped:
            org.telegram.messenger.FileLog.e(r1);
     */
    /* JADX WARNING: Missing block: B:42:0x006b, code skipped:
            r7.lastOffset = r8 + r0;
     */
    /* JADX WARNING: Missing block: B:51:?, code skipped:
            return 0;
     */
    /* JADX WARNING: Missing block: B:52:?, code skipped:
            return 0;
     */
    /* JADX WARNING: Missing block: B:53:?, code skipped:
            return r0;
     */
    /* JADX WARNING: Missing block: B:54:?, code skipped:
            return r0;
     */
    public int read(int r8, int r9) {
        /*
        r7 = this;
        r2 = 0;
        r3 = r7.sync;
        monitor-enter(r3);
        r4 = r7.canceled;	 Catch:{ all -> 0x0010 }
        if (r4 == 0) goto L_0x000b;
    L_0x0008:
        monitor-exit(r3);	 Catch:{ all -> 0x0010 }
        r0 = r2;
    L_0x000a:
        return r0;
    L_0x000b:
        monitor-exit(r3);	 Catch:{ all -> 0x0010 }
        if (r9 != 0) goto L_0x0013;
    L_0x000e:
        r0 = r2;
        goto L_0x000a;
    L_0x0010:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0010 }
        throw r2;
    L_0x0013:
        r0 = 0;
    L_0x0014:
        if (r0 != 0) goto L_0x006b;
    L_0x0016:
        r3 = r7.loadOperation;	 Catch:{ Exception -> 0x0063 }
        r0 = r3.getDownloadedLengthFromOffset(r8, r9);	 Catch:{ Exception -> 0x0063 }
        if (r0 != 0) goto L_0x0014;
    L_0x001e:
        r3 = r7.loadOperation;	 Catch:{ Exception -> 0x0063 }
        r3 = r3.isPaused();	 Catch:{ Exception -> 0x0063 }
        if (r3 != 0) goto L_0x002a;
    L_0x0026:
        r3 = r7.lastOffset;	 Catch:{ Exception -> 0x0063 }
        if (r3 == r8) goto L_0x0037;
    L_0x002a:
        r3 = r7.currentAccount;	 Catch:{ Exception -> 0x0063 }
        r3 = org.telegram.messenger.FileLoader.getInstance(r3);	 Catch:{ Exception -> 0x0063 }
        r4 = r7.document;	 Catch:{ Exception -> 0x0063 }
        r5 = r7.parentObject;	 Catch:{ Exception -> 0x0063 }
        r3.loadStreamFile(r7, r4, r5, r8);	 Catch:{ Exception -> 0x0063 }
    L_0x0037:
        r3 = r7.sync;	 Catch:{ Exception -> 0x0063 }
        monitor-enter(r3);	 Catch:{ Exception -> 0x0063 }
        r4 = r7.canceled;	 Catch:{ all -> 0x0068 }
        if (r4 == 0) goto L_0x0041;
    L_0x003e:
        monitor-exit(r3);	 Catch:{ all -> 0x0068 }
        r0 = r2;
        goto L_0x000a;
    L_0x0041:
        r4 = new java.util.concurrent.CountDownLatch;	 Catch:{ all -> 0x0068 }
        r5 = 1;
        r4.<init>(r5);	 Catch:{ all -> 0x0068 }
        r7.countDownLatch = r4;	 Catch:{ all -> 0x0068 }
        monitor-exit(r3);	 Catch:{ all -> 0x0068 }
        r3 = r7.currentAccount;	 Catch:{ Exception -> 0x0063 }
        r3 = org.telegram.messenger.FileLoader.getInstance(r3);	 Catch:{ Exception -> 0x0063 }
        r4 = r7.document;	 Catch:{ Exception -> 0x0063 }
        r5 = 0;
        r6 = 1;
        r3.setLoadingVideo(r4, r5, r6);	 Catch:{ Exception -> 0x0063 }
        r3 = 1;
        r7.waitingForLoad = r3;	 Catch:{ Exception -> 0x0063 }
        r3 = r7.countDownLatch;	 Catch:{ Exception -> 0x0063 }
        r3.await();	 Catch:{ Exception -> 0x0063 }
        r3 = 0;
        r7.waitingForLoad = r3;	 Catch:{ Exception -> 0x0063 }
        goto L_0x0014;
    L_0x0063:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x000a;
    L_0x0068:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0068 }
        throw r2;	 Catch:{ Exception -> 0x0063 }
    L_0x006b:
        r2 = r8 + r0;
        r7.lastOffset = r2;	 Catch:{ Exception -> 0x0063 }
        goto L_0x000a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AnimatedFileDrawableStream.read(int, int):int");
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean removeLoading) {
        synchronized (this.sync) {
            if (this.countDownLatch != null) {
                this.countDownLatch.countDown();
                if (removeLoading && !this.canceled) {
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
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }
}
