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
    private long lastOffset;
    private final FileLoadOperation loadOperation;
    private ImageLocation location;
    private Object parentObject;
    private boolean preview;
    private final Object sync = new Object();
    private boolean waitingForLoad;

    public AnimatedFileDrawableStream(TLRPC$Document tLRPC$Document, ImageLocation imageLocation, Object obj, int i, boolean z) {
        this.document = tLRPC$Document;
        this.location = imageLocation;
        this.parentObject = obj;
        this.currentAccount = i;
        this.preview = z;
        this.loadOperation = FileLoader.getInstance(i).loadStreamFile(this, this.document, this.location, this.parentObject, 0, this.preview);
    }

    public boolean isFinishedLoadingFile() {
        return this.finishedLoadingFile;
    }

    public String getFinishedFilePath() {
        return this.finishedFilePath;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
        r12 = 0;
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0019, code lost:
        if (r1 != r12) goto L_0x00af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r6 = (long) r0;
        r3 = r9.loadOperation.getDownloadedLengthFromOffset(r6, (long) r10);
        r14 = r3[0];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0028, code lost:
        if (r9.finishedLoadingFile != false) goto L_0x003e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002e, code lost:
        if (r3[1] == r12) goto L_0x003e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0030, code lost:
        r9.finishedLoadingFile = true;
        r9.finishedFilePath = r9.loadOperation.getCacheFileFinal().getAbsolutePath();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0040, code lost:
        if (r14 != r12) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0048, code lost:
        if (r9.loadOperation.isPaused() != false) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004e, code lost:
        if (r9.lastOffset != r6) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0052, code lost:
        if (r9.preview == false) goto L_0x0055;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0055, code lost:
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0057, code lost:
        r12 = true;
        org.telegram.messenger.FileLoader.getInstance(r9.currentAccount).loadStreamFile(r17, r9.document, r9.location, r9.parentObject, r6, r9.preview);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006f, code lost:
        r1 = r9.sync;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0071, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0074, code lost:
        if (r9.canceled == false) goto L_0x0083;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0076, code lost:
        org.telegram.messenger.FileLoader.getInstance(r9.currentAccount).cancelLoadFile(r9.document);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0081, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0082, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0083, code lost:
        r9.countDownLatch = new java.util.concurrent.CountDownLatch(r12 ? 1 : 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x008a, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x008d, code lost:
        if (r9.preview != false) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x008f, code lost:
        org.telegram.messenger.FileLoader.getInstance(r9.currentAccount).setLoadingVideo(r9.document, false, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x009a, code lost:
        r9.waitingForLoad = r12;
        r9.countDownLatch.await();
        r9.waitingForLoad = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00a7, code lost:
        r1 = r14;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00ac, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00ad, code lost:
        r1 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        r9.lastOffset = ((long) r0) + r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00b4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0011, code lost:
        if (r10 != 0) goto L_0x0014;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(int r18, int r19) {
        /*
            r17 = this;
            r9 = r17
            r0 = r18
            r10 = r19
            java.lang.Object r1 = r9.sync
            monitor-enter(r1)
            boolean r2 = r9.canceled     // Catch:{ all -> 0x00ba }
            r11 = 0
            if (r2 == 0) goto L_0x0010
            monitor-exit(r1)     // Catch:{ all -> 0x00ba }
            return r11
        L_0x0010:
            monitor-exit(r1)     // Catch:{ all -> 0x00ba }
            if (r10 != 0) goto L_0x0014
            return r11
        L_0x0014:
            r12 = 0
            r1 = r12
        L_0x0017:
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 != 0) goto L_0x00af
            org.telegram.messenger.FileLoadOperation r3 = r9.loadOperation     // Catch:{ Exception -> 0x00b4 }
            long r6 = (long) r0     // Catch:{ Exception -> 0x00b4 }
            long r4 = (long) r10     // Catch:{ Exception -> 0x00b4 }
            long[] r3 = r3.getDownloadedLengthFromOffset(r6, r4)     // Catch:{ Exception -> 0x00b4 }
            r14 = r3[r11]     // Catch:{ Exception -> 0x00b4 }
            boolean r1 = r9.finishedLoadingFile     // Catch:{ Exception -> 0x00ac }
            r8 = 1
            if (r1 != 0) goto L_0x003e
            r1 = r3[r8]     // Catch:{ Exception -> 0x00ac }
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 == 0) goto L_0x003e
            r9.finishedLoadingFile = r8     // Catch:{ Exception -> 0x00ac }
            org.telegram.messenger.FileLoadOperation r1 = r9.loadOperation     // Catch:{ Exception -> 0x00ac }
            java.io.File r1 = r1.getCacheFileFinal()     // Catch:{ Exception -> 0x00ac }
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x00ac }
            r9.finishedFilePath = r1     // Catch:{ Exception -> 0x00ac }
        L_0x003e:
            int r1 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1))
            if (r1 != 0) goto L_0x00a7
            org.telegram.messenger.FileLoadOperation r1 = r9.loadOperation     // Catch:{ Exception -> 0x00ac }
            boolean r1 = r1.isPaused()     // Catch:{ Exception -> 0x00ac }
            if (r1 != 0) goto L_0x0057
            long r1 = r9.lastOffset     // Catch:{ Exception -> 0x00ac }
            int r3 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r3 != 0) goto L_0x0057
            boolean r1 = r9.preview     // Catch:{ Exception -> 0x00ac }
            if (r1 == 0) goto L_0x0055
            goto L_0x0057
        L_0x0055:
            r12 = 1
            goto L_0x006f
        L_0x0057:
            int r1 = r9.currentAccount     // Catch:{ Exception -> 0x00ac }
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x00ac }
            org.telegram.tgnet.TLRPC$Document r3 = r9.document     // Catch:{ Exception -> 0x00ac }
            org.telegram.messenger.ImageLocation r4 = r9.location     // Catch:{ Exception -> 0x00ac }
            java.lang.Object r5 = r9.parentObject     // Catch:{ Exception -> 0x00ac }
            boolean r2 = r9.preview     // Catch:{ Exception -> 0x00ac }
            r16 = r2
            r2 = r17
            r12 = 1
            r8 = r16
            r1.loadStreamFile(r2, r3, r4, r5, r6, r8)     // Catch:{ Exception -> 0x00ac }
        L_0x006f:
            java.lang.Object r1 = r9.sync     // Catch:{ Exception -> 0x00ac }
            monitor-enter(r1)     // Catch:{ Exception -> 0x00ac }
            boolean r2 = r9.canceled     // Catch:{ all -> 0x00a4 }
            if (r2 == 0) goto L_0x0083
            int r0 = r9.currentAccount     // Catch:{ all -> 0x00a4 }
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)     // Catch:{ all -> 0x00a4 }
            org.telegram.tgnet.TLRPC$Document r2 = r9.document     // Catch:{ all -> 0x00a4 }
            r0.cancelLoadFile((org.telegram.tgnet.TLRPC$Document) r2)     // Catch:{ all -> 0x00a4 }
            monitor-exit(r1)     // Catch:{ all -> 0x00a4 }
            return r11
        L_0x0083:
            java.util.concurrent.CountDownLatch r2 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x00a4 }
            r2.<init>(r12)     // Catch:{ all -> 0x00a4 }
            r9.countDownLatch = r2     // Catch:{ all -> 0x00a4 }
            monitor-exit(r1)     // Catch:{ all -> 0x00a4 }
            boolean r1 = r9.preview     // Catch:{ Exception -> 0x00ac }
            if (r1 != 0) goto L_0x009a
            int r1 = r9.currentAccount     // Catch:{ Exception -> 0x00ac }
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x00ac }
            org.telegram.tgnet.TLRPC$Document r2 = r9.document     // Catch:{ Exception -> 0x00ac }
            r1.setLoadingVideo(r2, r11, r12)     // Catch:{ Exception -> 0x00ac }
        L_0x009a:
            r9.waitingForLoad = r12     // Catch:{ Exception -> 0x00ac }
            java.util.concurrent.CountDownLatch r1 = r9.countDownLatch     // Catch:{ Exception -> 0x00ac }
            r1.await()     // Catch:{ Exception -> 0x00ac }
            r9.waitingForLoad = r11     // Catch:{ Exception -> 0x00ac }
            goto L_0x00a7
        L_0x00a4:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00a4 }
            throw r0     // Catch:{ Exception -> 0x00ac }
        L_0x00a7:
            r1 = r14
            r12 = 0
            goto L_0x0017
        L_0x00ac:
            r0 = move-exception
            r1 = r14
            goto L_0x00b5
        L_0x00af:
            long r3 = (long) r0
            long r3 = r3 + r1
            r9.lastOffset = r3     // Catch:{ Exception -> 0x00b4 }
            goto L_0x00b8
        L_0x00b4:
            r0 = move-exception
        L_0x00b5:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r11)
        L_0x00b8:
            int r0 = (int) r1
            return r0
        L_0x00ba:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00ba }
            goto L_0x00be
        L_0x00bd:
            throw r0
        L_0x00be:
            goto L_0x00bd
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AnimatedFileDrawableStream.read(int, int):int");
    }

    public void cancel() {
        cancel(true);
    }

    public void cancel(boolean z) {
        synchronized (this.sync) {
            CountDownLatch countDownLatch2 = this.countDownLatch;
            if (countDownLatch2 != null) {
                countDownLatch2.countDown();
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
