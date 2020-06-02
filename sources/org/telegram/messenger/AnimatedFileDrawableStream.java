package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC$Document;

public class AnimatedFileDrawableStream implements FileLoadOperationStream {
    private volatile boolean canceled;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private TLRPC$Document document;
    private String finishedFilePath;
    private boolean finishedLoadingFile;
    private boolean ignored;
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

    public boolean isFinishedLoadingFile() {
        return this.finishedLoadingFile;
    }

    public String getFinishedFilePath() {
        return this.finishedFilePath;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000e, code lost:
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x000f, code lost:
        if (r0 != 0) goto L_0x0080;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r1 = r11.loadOperation.getDownloadedLengthFromOffset(r12, r13);
        r0 = r1[0];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001c, code lost:
        if (r11.finishedLoadingFile != false) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0020, code lost:
        if (r1[1] == 0) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0022, code lost:
        r11.finishedLoadingFile = true;
        r11.finishedFilePath = r11.loadOperation.getCacheFileFinal().getAbsolutePath();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0030, code lost:
        if (r0 != 0) goto L_0x000f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0038, code lost:
        if (r11.loadOperation.isPaused() != false) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003c, code lost:
        if (r11.lastOffset != r12) goto L_0x0042;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0040, code lost:
        if (r11.preview == false) goto L_0x0053;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0042, code lost:
        org.telegram.messenger.FileLoader.getInstance(r11.currentAccount).loadStreamFile(r11, r11.document, r11.parentObject, r12, r11.preview);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0053, code lost:
        r1 = r11.sync;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0055, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0058, code lost:
        if (r11.canceled == false) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x005a, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x005b, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x005c, code lost:
        r11.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0063, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0066, code lost:
        if (r11.preview != false) goto L_0x0073;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0068, code lost:
        org.telegram.messenger.FileLoader.getInstance(r11.currentAccount).setLoadingVideo(r11.document, false, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0073, code lost:
        r11.waitingForLoad = true;
        r11.countDownLatch.await();
        r11.waitingForLoad = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0080, code lost:
        r11.lastOffset = r12 + r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0084, code lost:
        r12 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0085, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0088, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000b, code lost:
        if (r13 != 0) goto L_0x000e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000d, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(int r12, int r13) {
        /*
            r11 = this;
            java.lang.Object r0 = r11.sync
            monitor-enter(r0)
            boolean r1 = r11.canceled     // Catch:{ all -> 0x0089 }
            r2 = 0
            if (r1 == 0) goto L_0x000a
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            return r2
        L_0x000a:
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            if (r13 != 0) goto L_0x000e
            return r2
        L_0x000e:
            r0 = 0
        L_0x000f:
            if (r0 != 0) goto L_0x0080
            org.telegram.messenger.FileLoadOperation r1 = r11.loadOperation     // Catch:{ Exception -> 0x0084 }
            int[] r1 = r1.getDownloadedLengthFromOffset(r12, r13)     // Catch:{ Exception -> 0x0084 }
            r0 = r1[r2]     // Catch:{ Exception -> 0x0084 }
            boolean r3 = r11.finishedLoadingFile     // Catch:{ Exception -> 0x0084 }
            r4 = 1
            if (r3 != 0) goto L_0x0030
            r1 = r1[r4]     // Catch:{ Exception -> 0x0084 }
            if (r1 == 0) goto L_0x0030
            r11.finishedLoadingFile = r4     // Catch:{ Exception -> 0x0084 }
            org.telegram.messenger.FileLoadOperation r1 = r11.loadOperation     // Catch:{ Exception -> 0x0084 }
            java.io.File r1 = r1.getCacheFileFinal()     // Catch:{ Exception -> 0x0084 }
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x0084 }
            r11.finishedFilePath = r1     // Catch:{ Exception -> 0x0084 }
        L_0x0030:
            if (r0 != 0) goto L_0x000f
            org.telegram.messenger.FileLoadOperation r1 = r11.loadOperation     // Catch:{ Exception -> 0x0084 }
            boolean r1 = r1.isPaused()     // Catch:{ Exception -> 0x0084 }
            if (r1 != 0) goto L_0x0042
            int r1 = r11.lastOffset     // Catch:{ Exception -> 0x0084 }
            if (r1 != r12) goto L_0x0042
            boolean r1 = r11.preview     // Catch:{ Exception -> 0x0084 }
            if (r1 == 0) goto L_0x0053
        L_0x0042:
            int r1 = r11.currentAccount     // Catch:{ Exception -> 0x0084 }
            org.telegram.messenger.FileLoader r5 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x0084 }
            org.telegram.tgnet.TLRPC$Document r7 = r11.document     // Catch:{ Exception -> 0x0084 }
            java.lang.Object r8 = r11.parentObject     // Catch:{ Exception -> 0x0084 }
            boolean r10 = r11.preview     // Catch:{ Exception -> 0x0084 }
            r6 = r11
            r9 = r12
            r5.loadStreamFile(r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0084 }
        L_0x0053:
            java.lang.Object r1 = r11.sync     // Catch:{ Exception -> 0x0084 }
            monitor-enter(r1)     // Catch:{ Exception -> 0x0084 }
            boolean r3 = r11.canceled     // Catch:{ all -> 0x007d }
            if (r3 == 0) goto L_0x005c
            monitor-exit(r1)     // Catch:{ all -> 0x007d }
            return r2
        L_0x005c:
            java.util.concurrent.CountDownLatch r3 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x007d }
            r3.<init>(r4)     // Catch:{ all -> 0x007d }
            r11.countDownLatch = r3     // Catch:{ all -> 0x007d }
            monitor-exit(r1)     // Catch:{ all -> 0x007d }
            boolean r1 = r11.preview     // Catch:{ Exception -> 0x0084 }
            if (r1 != 0) goto L_0x0073
            int r1 = r11.currentAccount     // Catch:{ Exception -> 0x0084 }
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x0084 }
            org.telegram.tgnet.TLRPC$Document r3 = r11.document     // Catch:{ Exception -> 0x0084 }
            r1.setLoadingVideo(r3, r2, r4)     // Catch:{ Exception -> 0x0084 }
        L_0x0073:
            r11.waitingForLoad = r4     // Catch:{ Exception -> 0x0084 }
            java.util.concurrent.CountDownLatch r1 = r11.countDownLatch     // Catch:{ Exception -> 0x0084 }
            r1.await()     // Catch:{ Exception -> 0x0084 }
            r11.waitingForLoad = r2     // Catch:{ Exception -> 0x0084 }
            goto L_0x000f
        L_0x007d:
            r12 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x007d }
            throw r12     // Catch:{ Exception -> 0x0084 }
        L_0x0080:
            int r12 = r12 + r0
            r11.lastOffset = r12     // Catch:{ Exception -> 0x0084 }
            goto L_0x0088
        L_0x0084:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
        L_0x0088:
            return r0
        L_0x0089:
            r12 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            goto L_0x008d
        L_0x008c:
            throw r12
        L_0x008d:
            goto L_0x008c
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
