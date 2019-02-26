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

    public AnimatedFileDrawableStream(Document d, Object p, int a) {
        this.document = d;
        this.parentObject = p;
        this.currentAccount = a;
        this.loadOperation = FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.parentObject, 0);
    }

    /* JADX WARNING: Missing block: B:8:0x000c, code:
            if (r9 != 0) goto L_0x0013;
     */
    /* JADX WARNING: Missing block: B:13:0x0013, code:
            r0 = 0;
     */
    /* JADX WARNING: Missing block: B:14:0x0014, code:
            if (r0 != 0) goto L_0x0065;
     */
    /* JADX WARNING: Missing block: B:16:?, code:
            r0 = r7.loadOperation.getDownloadedLengthFromOffset(r8, r9);
     */
    /* JADX WARNING: Missing block: B:17:0x001c, code:
            if (r0 != 0) goto L_0x0014;
     */
    /* JADX WARNING: Missing block: B:19:0x0024, code:
            if (r7.loadOperation.isPaused() != false) goto L_0x002a;
     */
    /* JADX WARNING: Missing block: B:21:0x0028, code:
            if (r7.lastOffset == r8) goto L_0x0037;
     */
    /* JADX WARNING: Missing block: B:22:0x002a, code:
            org.telegram.messenger.FileLoader.getInstance(r7.currentAccount).loadStreamFile(r7, r7.document, r7.parentObject, r8);
     */
    /* JADX WARNING: Missing block: B:23:0x0037, code:
            r3 = r7.sync;
     */
    /* JADX WARNING: Missing block: B:24:0x0039, code:
            monitor-enter(r3);
     */
    /* JADX WARNING: Missing block: B:27:0x003c, code:
            if (r7.canceled == false) goto L_0x0041;
     */
    /* JADX WARNING: Missing block: B:28:0x003e, code:
            monitor-exit(r3);
     */
    /* JADX WARNING: Missing block: B:30:0x0041, code:
            r7.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Missing block: B:31:0x0049, code:
            monitor-exit(r3);
     */
    /* JADX WARNING: Missing block: B:33:?, code:
            org.telegram.messenger.FileLoader.getInstance(r7.currentAccount).setLoadingVideo(r7.document, false, true);
            r7.countDownLatch.await();
     */
    /* JADX WARNING: Missing block: B:35:0x005d, code:
            r1 = move-exception;
     */
    /* JADX WARNING: Missing block: B:36:0x005e, code:
            org.telegram.messenger.FileLog.e(r1);
     */
    /* JADX WARNING: Missing block: B:42:0x0065, code:
            r7.lastOffset = r8 + r0;
     */
    /* JADX WARNING: Missing block: B:52:?, code:
            return 0;
     */
    /* JADX WARNING: Missing block: B:53:?, code:
            return 0;
     */
    /* JADX WARNING: Missing block: B:54:?, code:
            return r0;
     */
    /* JADX WARNING: Missing block: B:55:?, code:
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
        if (r0 != 0) goto L_0x0065;
    L_0x0016:
        r3 = r7.loadOperation;	 Catch:{ Exception -> 0x005d }
        r0 = r3.getDownloadedLengthFromOffset(r8, r9);	 Catch:{ Exception -> 0x005d }
        if (r0 != 0) goto L_0x0014;
    L_0x001e:
        r3 = r7.loadOperation;	 Catch:{ Exception -> 0x005d }
        r3 = r3.isPaused();	 Catch:{ Exception -> 0x005d }
        if (r3 != 0) goto L_0x002a;
    L_0x0026:
        r3 = r7.lastOffset;	 Catch:{ Exception -> 0x005d }
        if (r3 == r8) goto L_0x0037;
    L_0x002a:
        r3 = r7.currentAccount;	 Catch:{ Exception -> 0x005d }
        r3 = org.telegram.messenger.FileLoader.getInstance(r3);	 Catch:{ Exception -> 0x005d }
        r4 = r7.document;	 Catch:{ Exception -> 0x005d }
        r5 = r7.parentObject;	 Catch:{ Exception -> 0x005d }
        r3.loadStreamFile(r7, r4, r5, r8);	 Catch:{ Exception -> 0x005d }
    L_0x0037:
        r3 = r7.sync;	 Catch:{ Exception -> 0x005d }
        monitor-enter(r3);	 Catch:{ Exception -> 0x005d }
        r4 = r7.canceled;	 Catch:{ all -> 0x0062 }
        if (r4 == 0) goto L_0x0041;
    L_0x003e:
        monitor-exit(r3);	 Catch:{ all -> 0x0062 }
        r0 = r2;
        goto L_0x000a;
    L_0x0041:
        r4 = new java.util.concurrent.CountDownLatch;	 Catch:{ all -> 0x0062 }
        r5 = 1;
        r4.<init>(r5);	 Catch:{ all -> 0x0062 }
        r7.countDownLatch = r4;	 Catch:{ all -> 0x0062 }
        monitor-exit(r3);	 Catch:{ all -> 0x0062 }
        r3 = r7.currentAccount;	 Catch:{ Exception -> 0x005d }
        r3 = org.telegram.messenger.FileLoader.getInstance(r3);	 Catch:{ Exception -> 0x005d }
        r4 = r7.document;	 Catch:{ Exception -> 0x005d }
        r5 = 0;
        r6 = 1;
        r3.setLoadingVideo(r4, r5, r6);	 Catch:{ Exception -> 0x005d }
        r3 = r7.countDownLatch;	 Catch:{ Exception -> 0x005d }
        r3.await();	 Catch:{ Exception -> 0x005d }
        goto L_0x0014;
    L_0x005d:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x000a;
    L_0x0062:
        r2 = move-exception;
        monitor-exit(r3);	 Catch:{ all -> 0x0062 }
        throw r2;	 Catch:{ Exception -> 0x005d }
    L_0x0065:
        r2 = r8 + r0;
        r7.lastOffset = r2;	 Catch:{ Exception -> 0x005d }
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

    public void newDataAvailable() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }
}
