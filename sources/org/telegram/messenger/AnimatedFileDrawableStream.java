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
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0019, code lost:
        if (r1 != 0) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r3 = r8.loadOperation.getDownloadedLengthFromOffset(r0, (long) r9);
        r13 = r3[0];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0027, code lost:
        if (r8.finishedLoadingFile != false) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002d, code lost:
        if (r3[1] == 0) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002f, code lost:
        r8.finishedLoadingFile = true;
        r8.finishedFilePath = r8.loadOperation.getCacheFileFinal().getAbsolutePath();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003f, code lost:
        if (r13 != 0) goto L_0x00a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0047, code lost:
        if (r8.loadOperation.isPaused() != false) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004e, code lost:
        if (r8.lastOffset != ((long) r0)) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0052, code lost:
        if (r8.preview == false) goto L_0x0069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0054, code lost:
        org.telegram.messenger.FileLoader.getInstance(r8.currentAccount).loadStreamFile(r16, r8.document, r8.location, r8.parentObject, r17, r8.preview);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0069, code lost:
        r1 = r8.sync;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006b, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x006e, code lost:
        if (r8.canceled == false) goto L_0x007d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0070, code lost:
        org.telegram.messenger.FileLoader.getInstance(r8.currentAccount).cancelLoadFile(r8.document);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x007b, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007c, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007d, code lost:
        r8.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0084, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0087, code lost:
        if (r8.preview != false) goto L_0x0094;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0089, code lost:
        org.telegram.messenger.FileLoader.getInstance(r8.currentAccount).setLoadingVideo(r8.document, false, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0094, code lost:
        r8.waitingForLoad = true;
        r8.countDownLatch.await();
        r8.waitingForLoad = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00a1, code lost:
        r1 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00a4, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00a5, code lost:
        r1 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
        r8.lastOffset = ((long) r0) + r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00ac, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0011, code lost:
        if (r9 != 0) goto L_0x0014;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(int r17, int r18) {
        /*
            r16 = this;
            r8 = r16
            r0 = r17
            r9 = r18
            java.lang.Object r1 = r8.sync
            monitor-enter(r1)
            boolean r2 = r8.canceled     // Catch:{ all -> 0x00b2 }
            r10 = 0
            if (r2 == 0) goto L_0x0010
            monitor-exit(r1)     // Catch:{ all -> 0x00b2 }
            return r10
        L_0x0010:
            monitor-exit(r1)     // Catch:{ all -> 0x00b2 }
            if (r9 != 0) goto L_0x0014
            return r10
        L_0x0014:
            r11 = 0
            r1 = r11
        L_0x0017:
            int r3 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r3 != 0) goto L_0x00a7
            org.telegram.messenger.FileLoadOperation r3 = r8.loadOperation     // Catch:{ Exception -> 0x00ac }
            long r4 = (long) r9     // Catch:{ Exception -> 0x00ac }
            long[] r3 = r3.getDownloadedLengthFromOffset(r0, r4)     // Catch:{ Exception -> 0x00ac }
            r13 = r3[r10]     // Catch:{ Exception -> 0x00ac }
            boolean r1 = r8.finishedLoadingFile     // Catch:{ Exception -> 0x00a4 }
            r15 = 1
            if (r1 != 0) goto L_0x003d
            r1 = r3[r15]     // Catch:{ Exception -> 0x00a4 }
            int r3 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r3 == 0) goto L_0x003d
            r8.finishedLoadingFile = r15     // Catch:{ Exception -> 0x00a4 }
            org.telegram.messenger.FileLoadOperation r1 = r8.loadOperation     // Catch:{ Exception -> 0x00a4 }
            java.io.File r1 = r1.getCacheFileFinal()     // Catch:{ Exception -> 0x00a4 }
            java.lang.String r1 = r1.getAbsolutePath()     // Catch:{ Exception -> 0x00a4 }
            r8.finishedFilePath = r1     // Catch:{ Exception -> 0x00a4 }
        L_0x003d:
            int r1 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x00a1
            org.telegram.messenger.FileLoadOperation r1 = r8.loadOperation     // Catch:{ Exception -> 0x00a4 }
            boolean r1 = r1.isPaused()     // Catch:{ Exception -> 0x00a4 }
            if (r1 != 0) goto L_0x0054
            long r1 = r8.lastOffset     // Catch:{ Exception -> 0x00a4 }
            long r3 = (long) r0     // Catch:{ Exception -> 0x00a4 }
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x0054
            boolean r1 = r8.preview     // Catch:{ Exception -> 0x00a4 }
            if (r1 == 0) goto L_0x0069
        L_0x0054:
            int r1 = r8.currentAccount     // Catch:{ Exception -> 0x00a4 }
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x00a4 }
            org.telegram.tgnet.TLRPC$Document r3 = r8.document     // Catch:{ Exception -> 0x00a4 }
            org.telegram.messenger.ImageLocation r4 = r8.location     // Catch:{ Exception -> 0x00a4 }
            java.lang.Object r5 = r8.parentObject     // Catch:{ Exception -> 0x00a4 }
            boolean r7 = r8.preview     // Catch:{ Exception -> 0x00a4 }
            r2 = r16
            r6 = r17
            r1.loadStreamFile(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x00a4 }
        L_0x0069:
            java.lang.Object r1 = r8.sync     // Catch:{ Exception -> 0x00a4 }
            monitor-enter(r1)     // Catch:{ Exception -> 0x00a4 }
            boolean r2 = r8.canceled     // Catch:{ all -> 0x009e }
            if (r2 == 0) goto L_0x007d
            int r0 = r8.currentAccount     // Catch:{ all -> 0x009e }
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)     // Catch:{ all -> 0x009e }
            org.telegram.tgnet.TLRPC$Document r2 = r8.document     // Catch:{ all -> 0x009e }
            r0.cancelLoadFile((org.telegram.tgnet.TLRPC$Document) r2)     // Catch:{ all -> 0x009e }
            monitor-exit(r1)     // Catch:{ all -> 0x009e }
            return r10
        L_0x007d:
            java.util.concurrent.CountDownLatch r2 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x009e }
            r2.<init>(r15)     // Catch:{ all -> 0x009e }
            r8.countDownLatch = r2     // Catch:{ all -> 0x009e }
            monitor-exit(r1)     // Catch:{ all -> 0x009e }
            boolean r1 = r8.preview     // Catch:{ Exception -> 0x00a4 }
            if (r1 != 0) goto L_0x0094
            int r1 = r8.currentAccount     // Catch:{ Exception -> 0x00a4 }
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r1)     // Catch:{ Exception -> 0x00a4 }
            org.telegram.tgnet.TLRPC$Document r2 = r8.document     // Catch:{ Exception -> 0x00a4 }
            r1.setLoadingVideo(r2, r10, r15)     // Catch:{ Exception -> 0x00a4 }
        L_0x0094:
            r8.waitingForLoad = r15     // Catch:{ Exception -> 0x00a4 }
            java.util.concurrent.CountDownLatch r1 = r8.countDownLatch     // Catch:{ Exception -> 0x00a4 }
            r1.await()     // Catch:{ Exception -> 0x00a4 }
            r8.waitingForLoad = r10     // Catch:{ Exception -> 0x00a4 }
            goto L_0x00a1
        L_0x009e:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x009e }
            throw r0     // Catch:{ Exception -> 0x00a4 }
        L_0x00a1:
            r1 = r13
            goto L_0x0017
        L_0x00a4:
            r0 = move-exception
            r1 = r13
            goto L_0x00ad
        L_0x00a7:
            long r3 = (long) r0
            long r3 = r3 + r1
            r8.lastOffset = r3     // Catch:{ Exception -> 0x00ac }
            goto L_0x00b0
        L_0x00ac:
            r0 = move-exception
        L_0x00ad:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r10)
        L_0x00b0:
            int r0 = (int) r1
            return r0
        L_0x00b2:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00b2 }
            goto L_0x00b6
        L_0x00b5:
            throw r0
        L_0x00b6:
            goto L_0x00b5
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
