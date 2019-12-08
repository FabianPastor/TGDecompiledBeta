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
            if (r0 != 0) goto L_0x0068;
     */
    /* JADX WARNING: Missing block: B:13:?, code skipped:
            r0 = r9.loadOperation.getDownloadedLengthFromOffset(r10, r11);
     */
    /* JADX WARNING: Missing block: B:14:0x0017, code skipped:
            if (r0 != 0) goto L_0x000f;
     */
    /* JADX WARNING: Missing block: B:16:0x001f, code skipped:
            if (r9.loadOperation.isPaused() != false) goto L_0x0029;
     */
    /* JADX WARNING: Missing block: B:18:0x0023, code skipped:
            if (r9.lastOffset != r10) goto L_0x0029;
     */
    /* JADX WARNING: Missing block: B:20:0x0027, code skipped:
            if (r9.preview == false) goto L_0x003a;
     */
    /* JADX WARNING: Missing block: B:21:0x0029, code skipped:
            org.telegram.messenger.FileLoader.getInstance(r9.currentAccount).loadStreamFile(r9, r9.document, r9.parentObject, r10, r9.preview);
     */
    /* JADX WARNING: Missing block: B:22:0x003a, code skipped:
            r1 = r9.sync;
     */
    /* JADX WARNING: Missing block: B:23:0x003c, code skipped:
            monitor-enter(r1);
     */
    /* JADX WARNING: Missing block: B:26:0x003f, code skipped:
            if (r9.canceled == false) goto L_0x0043;
     */
    /* JADX WARNING: Missing block: B:27:0x0041, code skipped:
            monitor-exit(r1);
     */
    /* JADX WARNING: Missing block: B:28:0x0042, code skipped:
            return 0;
     */
    /* JADX WARNING: Missing block: B:29:0x0043, code skipped:
            r9.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Missing block: B:30:0x004b, code skipped:
            monitor-exit(r1);
     */
    /* JADX WARNING: Missing block: B:33:0x004e, code skipped:
            if (r9.preview != false) goto L_0x005b;
     */
    /* JADX WARNING: Missing block: B:34:0x0050, code skipped:
            org.telegram.messenger.FileLoader.getInstance(r9.currentAccount).setLoadingVideo(r9.document, false, true);
     */
    /* JADX WARNING: Missing block: B:35:0x005b, code skipped:
            r9.waitingForLoad = true;
            r9.countDownLatch.await();
            r9.waitingForLoad = false;
     */
    /* JADX WARNING: Missing block: B:42:0x0068, code skipped:
            r9.lastOffset = r10 + r0;
     */
    /* JADX WARNING: Missing block: B:43:0x006c, code skipped:
            r10 = move-exception;
     */
    /* JADX WARNING: Missing block: B:44:0x006d, code skipped:
            org.telegram.messenger.FileLog.e(r10);
     */
    /* JADX WARNING: Missing block: B:45:0x0070, code skipped:
            return r0;
     */
    public int read(int r10, int r11) {
        /*
        r9 = this;
        r0 = r9.sync;
        monitor-enter(r0);
        r1 = r9.canceled;	 Catch:{ all -> 0x0071 }
        r2 = 0;
        if (r1 == 0) goto L_0x000a;
    L_0x0008:
        monitor-exit(r0);	 Catch:{ all -> 0x0071 }
        return r2;
    L_0x000a:
        monitor-exit(r0);	 Catch:{ all -> 0x0071 }
        if (r11 != 0) goto L_0x000e;
    L_0x000d:
        return r2;
    L_0x000e:
        r0 = 0;
    L_0x000f:
        if (r0 != 0) goto L_0x0068;
    L_0x0011:
        r1 = r9.loadOperation;	 Catch:{ Exception -> 0x006c }
        r0 = r1.getDownloadedLengthFromOffset(r10, r11);	 Catch:{ Exception -> 0x006c }
        if (r0 != 0) goto L_0x000f;
    L_0x0019:
        r1 = r9.loadOperation;	 Catch:{ Exception -> 0x006c }
        r1 = r1.isPaused();	 Catch:{ Exception -> 0x006c }
        if (r1 != 0) goto L_0x0029;
    L_0x0021:
        r1 = r9.lastOffset;	 Catch:{ Exception -> 0x006c }
        if (r1 != r10) goto L_0x0029;
    L_0x0025:
        r1 = r9.preview;	 Catch:{ Exception -> 0x006c }
        if (r1 == 0) goto L_0x003a;
    L_0x0029:
        r1 = r9.currentAccount;	 Catch:{ Exception -> 0x006c }
        r3 = org.telegram.messenger.FileLoader.getInstance(r1);	 Catch:{ Exception -> 0x006c }
        r5 = r9.document;	 Catch:{ Exception -> 0x006c }
        r6 = r9.parentObject;	 Catch:{ Exception -> 0x006c }
        r8 = r9.preview;	 Catch:{ Exception -> 0x006c }
        r4 = r9;
        r7 = r10;
        r3.loadStreamFile(r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x006c }
    L_0x003a:
        r1 = r9.sync;	 Catch:{ Exception -> 0x006c }
        monitor-enter(r1);	 Catch:{ Exception -> 0x006c }
        r3 = r9.canceled;	 Catch:{ all -> 0x0065 }
        if (r3 == 0) goto L_0x0043;
    L_0x0041:
        monitor-exit(r1);	 Catch:{ all -> 0x0065 }
        return r2;
    L_0x0043:
        r3 = new java.util.concurrent.CountDownLatch;	 Catch:{ all -> 0x0065 }
        r4 = 1;
        r3.<init>(r4);	 Catch:{ all -> 0x0065 }
        r9.countDownLatch = r3;	 Catch:{ all -> 0x0065 }
        monitor-exit(r1);	 Catch:{ all -> 0x0065 }
        r1 = r9.preview;	 Catch:{ Exception -> 0x006c }
        if (r1 != 0) goto L_0x005b;
    L_0x0050:
        r1 = r9.currentAccount;	 Catch:{ Exception -> 0x006c }
        r1 = org.telegram.messenger.FileLoader.getInstance(r1);	 Catch:{ Exception -> 0x006c }
        r3 = r9.document;	 Catch:{ Exception -> 0x006c }
        r1.setLoadingVideo(r3, r2, r4);	 Catch:{ Exception -> 0x006c }
    L_0x005b:
        r9.waitingForLoad = r4;	 Catch:{ Exception -> 0x006c }
        r1 = r9.countDownLatch;	 Catch:{ Exception -> 0x006c }
        r1.await();	 Catch:{ Exception -> 0x006c }
        r9.waitingForLoad = r2;	 Catch:{ Exception -> 0x006c }
        goto L_0x000f;
    L_0x0065:
        r10 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0065 }
        throw r10;	 Catch:{ Exception -> 0x006c }
    L_0x0068:
        r10 = r10 + r0;
        r9.lastOffset = r10;	 Catch:{ Exception -> 0x006c }
        goto L_0x0070;
    L_0x006c:
        r10 = move-exception;
        org.telegram.messenger.FileLog.e(r10);
    L_0x0070:
        return r0;
    L_0x0071:
        r10 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0071 }
        goto L_0x0075;
    L_0x0074:
        throw r10;
    L_0x0075:
        goto L_0x0074;
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
                if (!(!z || this.canceled || this.preview)) {
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
