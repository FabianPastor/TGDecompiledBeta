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
    private boolean preview;
    private final Object sync = new Object();
    private boolean waitingForLoad;

    public AnimatedFileDrawableStream(Document document, Object obj, int i, boolean z) {
        this.document = document;
        this.parentObject = obj;
        this.currentAccount = i;
        this.preview = z;
        this.loadOperation = FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.parentObject, 0, this.preview);
    }

    /* JADX WARNING: Missing block: B:8:0x000b, code skipped:
            if (r11 != 0) goto L_0x000e;
     */
    /* JADX WARNING: Missing block: B:9:0x000d, code skipped:
            return 0;
     */
    /* JADX WARNING: Missing block: B:10:0x000e, code skipped:
            r0 = 0;
     */
    /* JADX WARNING: Missing block: B:11:0x000f, code skipped:
            if (r0 != 0) goto L_0x0060;
     */
    /* JADX WARNING: Missing block: B:13:?, code skipped:
            r0 = r9.loadOperation.getDownloadedLengthFromOffset(r10, r11);
     */
    /* JADX WARNING: Missing block: B:14:0x0017, code skipped:
            if (r0 != 0) goto L_0x000f;
     */
    /* JADX WARNING: Missing block: B:16:0x001f, code skipped:
            if (r9.loadOperation.isPaused() != false) goto L_0x0025;
     */
    /* JADX WARNING: Missing block: B:18:0x0023, code skipped:
            if (r9.lastOffset == r10) goto L_0x0036;
     */
    /* JADX WARNING: Missing block: B:19:0x0025, code skipped:
            org.telegram.messenger.FileLoader.getInstance(r9.currentAccount).loadStreamFile(r9, r9.document, r9.parentObject, r10, r9.preview);
     */
    /* JADX WARNING: Missing block: B:20:0x0036, code skipped:
            r1 = r9.sync;
     */
    /* JADX WARNING: Missing block: B:21:0x0038, code skipped:
            monitor-enter(r1);
     */
    /* JADX WARNING: Missing block: B:24:0x003b, code skipped:
            if (r9.canceled == false) goto L_0x003f;
     */
    /* JADX WARNING: Missing block: B:25:0x003d, code skipped:
            monitor-exit(r1);
     */
    /* JADX WARNING: Missing block: B:26:0x003e, code skipped:
            return 0;
     */
    /* JADX WARNING: Missing block: B:27:0x003f, code skipped:
            r9.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Missing block: B:28:0x0047, code skipped:
            monitor-exit(r1);
     */
    /* JADX WARNING: Missing block: B:30:?, code skipped:
            org.telegram.messenger.FileLoader.getInstance(r9.currentAccount).setLoadingVideo(r9.document, false, true);
            r9.waitingForLoad = true;
            r9.countDownLatch.await();
            r9.waitingForLoad = false;
     */
    /* JADX WARNING: Missing block: B:37:0x0060, code skipped:
            r9.lastOffset = r10 + r0;
     */
    /* JADX WARNING: Missing block: B:38:0x0064, code skipped:
            r10 = move-exception;
     */
    /* JADX WARNING: Missing block: B:39:0x0065, code skipped:
            org.telegram.messenger.FileLog.e(r10);
     */
    /* JADX WARNING: Missing block: B:40:0x0068, code skipped:
            return r0;
     */
    public int read(int r10, int r11) {
        /*
        r9 = this;
        r0 = r9.sync;
        monitor-enter(r0);
        r1 = r9.canceled;	 Catch:{ all -> 0x0069 }
        r2 = 0;
        if (r1 == 0) goto L_0x000a;
    L_0x0008:
        monitor-exit(r0);	 Catch:{ all -> 0x0069 }
        return r2;
    L_0x000a:
        monitor-exit(r0);	 Catch:{ all -> 0x0069 }
        if (r11 != 0) goto L_0x000e;
    L_0x000d:
        return r2;
    L_0x000e:
        r0 = 0;
    L_0x000f:
        if (r0 != 0) goto L_0x0060;
    L_0x0011:
        r1 = r9.loadOperation;	 Catch:{ Exception -> 0x0064 }
        r0 = r1.getDownloadedLengthFromOffset(r10, r11);	 Catch:{ Exception -> 0x0064 }
        if (r0 != 0) goto L_0x000f;
    L_0x0019:
        r1 = r9.loadOperation;	 Catch:{ Exception -> 0x0064 }
        r1 = r1.isPaused();	 Catch:{ Exception -> 0x0064 }
        if (r1 != 0) goto L_0x0025;
    L_0x0021:
        r1 = r9.lastOffset;	 Catch:{ Exception -> 0x0064 }
        if (r1 == r10) goto L_0x0036;
    L_0x0025:
        r1 = r9.currentAccount;	 Catch:{ Exception -> 0x0064 }
        r3 = org.telegram.messenger.FileLoader.getInstance(r1);	 Catch:{ Exception -> 0x0064 }
        r5 = r9.document;	 Catch:{ Exception -> 0x0064 }
        r6 = r9.parentObject;	 Catch:{ Exception -> 0x0064 }
        r8 = r9.preview;	 Catch:{ Exception -> 0x0064 }
        r4 = r9;
        r7 = r10;
        r3.loadStreamFile(r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x0064 }
    L_0x0036:
        r1 = r9.sync;	 Catch:{ Exception -> 0x0064 }
        monitor-enter(r1);	 Catch:{ Exception -> 0x0064 }
        r3 = r9.canceled;	 Catch:{ all -> 0x005d }
        if (r3 == 0) goto L_0x003f;
    L_0x003d:
        monitor-exit(r1);	 Catch:{ all -> 0x005d }
        return r2;
    L_0x003f:
        r3 = new java.util.concurrent.CountDownLatch;	 Catch:{ all -> 0x005d }
        r4 = 1;
        r3.<init>(r4);	 Catch:{ all -> 0x005d }
        r9.countDownLatch = r3;	 Catch:{ all -> 0x005d }
        monitor-exit(r1);	 Catch:{ all -> 0x005d }
        r1 = r9.currentAccount;	 Catch:{ Exception -> 0x0064 }
        r1 = org.telegram.messenger.FileLoader.getInstance(r1);	 Catch:{ Exception -> 0x0064 }
        r3 = r9.document;	 Catch:{ Exception -> 0x0064 }
        r1.setLoadingVideo(r3, r2, r4);	 Catch:{ Exception -> 0x0064 }
        r9.waitingForLoad = r4;	 Catch:{ Exception -> 0x0064 }
        r1 = r9.countDownLatch;	 Catch:{ Exception -> 0x0064 }
        r1.await();	 Catch:{ Exception -> 0x0064 }
        r9.waitingForLoad = r2;	 Catch:{ Exception -> 0x0064 }
        goto L_0x000f;
    L_0x005d:
        r10 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x005d }
        throw r10;	 Catch:{ Exception -> 0x0064 }
    L_0x0060:
        r10 = r10 + r0;
        r9.lastOffset = r10;	 Catch:{ Exception -> 0x0064 }
        goto L_0x0068;
    L_0x0064:
        r10 = move-exception;
        org.telegram.messenger.FileLog.e(r10);
    L_0x0068:
        return r0;
    L_0x0069:
        r10 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0069 }
        goto L_0x006d;
    L_0x006c:
        throw r10;
    L_0x006d:
        goto L_0x006c;
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
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
}
