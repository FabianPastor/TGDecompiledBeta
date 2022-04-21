package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC;

public class AnimatedFileDrawableStream implements FileLoadOperationStream {
    private volatile boolean canceled;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private TLRPC.Document document;
    private String finishedFilePath;
    private boolean finishedLoadingFile;
    private boolean ignored;
    private int lastOffset;
    private FileLoadOperation loadOperation;
    private ImageLocation location;
    private Object parentObject;
    private boolean preview;
    private final Object sync = new Object();
    private boolean waitingForLoad;

    public AnimatedFileDrawableStream(TLRPC.Document d, ImageLocation l, Object p, int a, boolean prev) {
        this.document = d;
        this.location = l;
        this.parentObject = p;
        this.currentAccount = a;
        this.preview = prev;
        this.loadOperation = FileLoader.getInstance(a).loadStreamFile(this, this.document, this.location, this.parentObject, 0, this.preview);
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
        if (r0 != 0) goto L_0x0084;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r1 = r12.loadOperation.getDownloadedLengthFromOffset(r13, r14);
        r0 = r1[0];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x001d, code lost:
        if (r12.finishedLoadingFile != false) goto L_0x0031;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0021, code lost:
        if (r1[1] == 0) goto L_0x0031;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0023, code lost:
        r12.finishedLoadingFile = true;
        r12.finishedFilePath = r12.loadOperation.getCacheFileFinal().getAbsolutePath();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0031, code lost:
        if (r0 != 0) goto L_0x0083;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0039, code lost:
        if (r12.loadOperation.isPaused() != false) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003d, code lost:
        if (r12.lastOffset != r13) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0041, code lost:
        if (r12.preview == false) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0043, code lost:
        org.telegram.messenger.FileLoader.getInstance(r12.currentAccount).loadStreamFile(r12, r12.document, r12.location, r12.parentObject, r13, r12.preview);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0056, code lost:
        r3 = r12.sync;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0058, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x005b, code lost:
        if (r12.canceled == false) goto L_0x005f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x005d, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x005e, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x005f, code lost:
        r12.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0066, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0069, code lost:
        if (r12.preview != false) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x006b, code lost:
        org.telegram.messenger.FileLoader.getInstance(r12.currentAccount).setLoadingVideo(r12.document, false, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0076, code lost:
        r12.waitingForLoad = true;
        r12.countDownLatch.await();
        r12.waitingForLoad = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0084, code lost:
        r12.lastOffset = r13 + r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x0089, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x008a, code lost:
        org.telegram.messenger.FileLog.e((java.lang.Throwable) r1, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x008d, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x000b, code lost:
        if (r14 != 0) goto L_0x000e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000d, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(int r13, int r14) {
        /*
            r12 = this;
            java.lang.Object r0 = r12.sync
            monitor-enter(r0)
            boolean r1 = r12.canceled     // Catch:{ all -> 0x008e }
            r2 = 0
            if (r1 == 0) goto L_0x000a
            monitor-exit(r0)     // Catch:{ all -> 0x008e }
            return r2
        L_0x000a:
            monitor-exit(r0)     // Catch:{ all -> 0x008e }
            if (r14 != 0) goto L_0x000e
            return r2
        L_0x000e:
            r0 = 0
        L_0x000f:
            if (r0 != 0) goto L_0x0084
            org.telegram.messenger.FileLoadOperation r1 = r12.loadOperation     // Catch:{ Exception -> 0x0089 }
            int[] r1 = r1.getDownloadedLengthFromOffset(r13, r14)     // Catch:{ Exception -> 0x0089 }
            r3 = r1[r2]     // Catch:{ Exception -> 0x0089 }
            r0 = r3
            boolean r3 = r12.finishedLoadingFile     // Catch:{ Exception -> 0x0089 }
            r4 = 1
            if (r3 != 0) goto L_0x0031
            r3 = r1[r4]     // Catch:{ Exception -> 0x0089 }
            if (r3 == 0) goto L_0x0031
            r12.finishedLoadingFile = r4     // Catch:{ Exception -> 0x0089 }
            org.telegram.messenger.FileLoadOperation r3 = r12.loadOperation     // Catch:{ Exception -> 0x0089 }
            java.io.File r3 = r3.getCacheFileFinal()     // Catch:{ Exception -> 0x0089 }
            java.lang.String r3 = r3.getAbsolutePath()     // Catch:{ Exception -> 0x0089 }
            r12.finishedFilePath = r3     // Catch:{ Exception -> 0x0089 }
        L_0x0031:
            if (r0 != 0) goto L_0x0083
            org.telegram.messenger.FileLoadOperation r3 = r12.loadOperation     // Catch:{ Exception -> 0x0089 }
            boolean r3 = r3.isPaused()     // Catch:{ Exception -> 0x0089 }
            if (r3 != 0) goto L_0x0043
            int r3 = r12.lastOffset     // Catch:{ Exception -> 0x0089 }
            if (r3 != r13) goto L_0x0043
            boolean r3 = r12.preview     // Catch:{ Exception -> 0x0089 }
            if (r3 == 0) goto L_0x0056
        L_0x0043:
            int r3 = r12.currentAccount     // Catch:{ Exception -> 0x0089 }
            org.telegram.messenger.FileLoader r5 = org.telegram.messenger.FileLoader.getInstance(r3)     // Catch:{ Exception -> 0x0089 }
            org.telegram.tgnet.TLRPC$Document r7 = r12.document     // Catch:{ Exception -> 0x0089 }
            org.telegram.messenger.ImageLocation r8 = r12.location     // Catch:{ Exception -> 0x0089 }
            java.lang.Object r9 = r12.parentObject     // Catch:{ Exception -> 0x0089 }
            boolean r11 = r12.preview     // Catch:{ Exception -> 0x0089 }
            r6 = r12
            r10 = r13
            r5.loadStreamFile(r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0089 }
        L_0x0056:
            java.lang.Object r3 = r12.sync     // Catch:{ Exception -> 0x0089 }
            monitor-enter(r3)     // Catch:{ Exception -> 0x0089 }
            boolean r5 = r12.canceled     // Catch:{ all -> 0x0080 }
            if (r5 == 0) goto L_0x005f
            monitor-exit(r3)     // Catch:{ all -> 0x0080 }
            return r2
        L_0x005f:
            java.util.concurrent.CountDownLatch r5 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x0080 }
            r5.<init>(r4)     // Catch:{ all -> 0x0080 }
            r12.countDownLatch = r5     // Catch:{ all -> 0x0080 }
            monitor-exit(r3)     // Catch:{ all -> 0x0080 }
            boolean r3 = r12.preview     // Catch:{ Exception -> 0x0089 }
            if (r3 != 0) goto L_0x0076
            int r3 = r12.currentAccount     // Catch:{ Exception -> 0x0089 }
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r3)     // Catch:{ Exception -> 0x0089 }
            org.telegram.tgnet.TLRPC$Document r5 = r12.document     // Catch:{ Exception -> 0x0089 }
            r3.setLoadingVideo(r5, r2, r4)     // Catch:{ Exception -> 0x0089 }
        L_0x0076:
            r12.waitingForLoad = r4     // Catch:{ Exception -> 0x0089 }
            java.util.concurrent.CountDownLatch r3 = r12.countDownLatch     // Catch:{ Exception -> 0x0089 }
            r3.await()     // Catch:{ Exception -> 0x0089 }
            r12.waitingForLoad = r2     // Catch:{ Exception -> 0x0089 }
            goto L_0x0083
        L_0x0080:
            r4 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0080 }
            throw r4     // Catch:{ Exception -> 0x0089 }
        L_0x0083:
            goto L_0x000f
        L_0x0084:
            int r1 = r13 + r0
            r12.lastOffset = r1     // Catch:{ Exception -> 0x0089 }
            goto L_0x008d
        L_0x0089:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1, (boolean) r2)
        L_0x008d:
            return r0
        L_0x008e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x008e }
            goto L_0x0092
        L_0x0091:
            throw r1
        L_0x0092:
            goto L_0x0091
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AnimatedFileDrawableStream.read(int, int):int");
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean removeLoading) {
        synchronized (this.sync) {
            CountDownLatch countDownLatch2 = this.countDownLatch;
            if (countDownLatch2 != null) {
                countDownLatch2.countDown();
                if (removeLoading && !this.canceled && !this.preview) {
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

    public TLRPC.Document getDocument() {
        return this.document;
    }

    public ImageLocation getLocation() {
        return this.location;
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
