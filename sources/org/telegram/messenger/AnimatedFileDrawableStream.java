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
    private long lastOffset;
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

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001b, code lost:
        if (r1 != 0) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r12 = r8.loadOperation.getDownloadedLengthFromOffset(r9, (long) r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0027, code lost:
        r13 = r12[0];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002b, code lost:
        if (r8.finishedLoadingFile != false) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0031, code lost:
        if (r12[1] == 0) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0033, code lost:
        r8.finishedLoadingFile = true;
        r8.finishedFilePath = r8.loadOperation.getCacheFileFinal().getAbsolutePath();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0043, code lost:
        if (r13 != 0) goto L_0x009a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004b, code lost:
        if (r8.loadOperation.isPaused() != false) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0052, code lost:
        if (r8.lastOffset != ((long) r9)) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0056, code lost:
        if (r8.preview == false) goto L_0x006d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0058, code lost:
        org.telegram.messenger.FileLoader.getInstance(r8.currentAccount).loadStreamFile(r16, r8.document, r8.location, r8.parentObject, r17, r8.preview);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x006d, code lost:
        r1 = r8.sync;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x006f, code lost:
        monitor-enter(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0072, code lost:
        if (r8.canceled == false) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0074, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0075, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0076, code lost:
        r8.countDownLatch = new java.util.concurrent.CountDownLatch(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x007d, code lost:
        monitor-exit(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0080, code lost:
        if (r8.preview != false) goto L_0x008d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0082, code lost:
        org.telegram.messenger.FileLoader.getInstance(r8.currentAccount).setLoadingVideo(r8.document, false, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x008d, code lost:
        r8.waitingForLoad = true;
        r8.countDownLatch.await();
        r8.waitingForLoad = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x009a, code lost:
        r1 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x009d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x009e, code lost:
        r1 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
        r8.lastOffset = ((long) r9) + r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00a5, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0011, code lost:
        if (r10 != 0) goto L_0x0014;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        return 0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(int r17, int r18) {
        /*
            r16 = this;
            r8 = r16
            r9 = r17
            r10 = r18
            java.lang.Object r1 = r8.sync
            monitor-enter(r1)
            boolean r0 = r8.canceled     // Catch:{ all -> 0x00ab }
            r11 = 0
            if (r0 == 0) goto L_0x0010
            monitor-exit(r1)     // Catch:{ all -> 0x00ab }
            return r11
        L_0x0010:
            monitor-exit(r1)     // Catch:{ all -> 0x00ab }
            if (r10 != 0) goto L_0x0014
            return r11
        L_0x0014:
            r0 = 0
            r1 = r0
        L_0x0017:
            r3 = 0
            int r0 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x00a0
            org.telegram.messenger.FileLoadOperation r0 = r8.loadOperation     // Catch:{ Exception -> 0x00a5 }
            long r5 = (long) r10     // Catch:{ Exception -> 0x00a5 }
            long[] r0 = r0.getDownloadedLengthFromOffset(r9, r5)     // Catch:{ Exception -> 0x00a5 }
            r12 = r0
            r5 = r12[r11]     // Catch:{ Exception -> 0x00a5 }
            r13 = r5
            boolean r0 = r8.finishedLoadingFile     // Catch:{ Exception -> 0x009d }
            r15 = 1
            if (r0 != 0) goto L_0x0041
            r0 = r12[r15]     // Catch:{ Exception -> 0x009d }
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0041
            r8.finishedLoadingFile = r15     // Catch:{ Exception -> 0x009d }
            org.telegram.messenger.FileLoadOperation r0 = r8.loadOperation     // Catch:{ Exception -> 0x009d }
            java.io.File r0 = r0.getCacheFileFinal()     // Catch:{ Exception -> 0x009d }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ Exception -> 0x009d }
            r8.finishedFilePath = r0     // Catch:{ Exception -> 0x009d }
        L_0x0041:
            int r0 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x009a
            org.telegram.messenger.FileLoadOperation r0 = r8.loadOperation     // Catch:{ Exception -> 0x009d }
            boolean r0 = r0.isPaused()     // Catch:{ Exception -> 0x009d }
            if (r0 != 0) goto L_0x0058
            long r0 = r8.lastOffset     // Catch:{ Exception -> 0x009d }
            long r2 = (long) r9     // Catch:{ Exception -> 0x009d }
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0058
            boolean r0 = r8.preview     // Catch:{ Exception -> 0x009d }
            if (r0 == 0) goto L_0x006d
        L_0x0058:
            int r0 = r8.currentAccount     // Catch:{ Exception -> 0x009d }
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r0)     // Catch:{ Exception -> 0x009d }
            org.telegram.tgnet.TLRPC$Document r3 = r8.document     // Catch:{ Exception -> 0x009d }
            org.telegram.messenger.ImageLocation r4 = r8.location     // Catch:{ Exception -> 0x009d }
            java.lang.Object r5 = r8.parentObject     // Catch:{ Exception -> 0x009d }
            boolean r7 = r8.preview     // Catch:{ Exception -> 0x009d }
            r2 = r16
            r6 = r17
            r1.loadStreamFile(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x009d }
        L_0x006d:
            java.lang.Object r1 = r8.sync     // Catch:{ Exception -> 0x009d }
            monitor-enter(r1)     // Catch:{ Exception -> 0x009d }
            boolean r0 = r8.canceled     // Catch:{ all -> 0x0097 }
            if (r0 == 0) goto L_0x0076
            monitor-exit(r1)     // Catch:{ all -> 0x0097 }
            return r11
        L_0x0076:
            java.util.concurrent.CountDownLatch r0 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x0097 }
            r0.<init>(r15)     // Catch:{ all -> 0x0097 }
            r8.countDownLatch = r0     // Catch:{ all -> 0x0097 }
            monitor-exit(r1)     // Catch:{ all -> 0x0097 }
            boolean r0 = r8.preview     // Catch:{ Exception -> 0x009d }
            if (r0 != 0) goto L_0x008d
            int r0 = r8.currentAccount     // Catch:{ Exception -> 0x009d }
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)     // Catch:{ Exception -> 0x009d }
            org.telegram.tgnet.TLRPC$Document r1 = r8.document     // Catch:{ Exception -> 0x009d }
            r0.setLoadingVideo(r1, r11, r15)     // Catch:{ Exception -> 0x009d }
        L_0x008d:
            r8.waitingForLoad = r15     // Catch:{ Exception -> 0x009d }
            java.util.concurrent.CountDownLatch r0 = r8.countDownLatch     // Catch:{ Exception -> 0x009d }
            r0.await()     // Catch:{ Exception -> 0x009d }
            r8.waitingForLoad = r11     // Catch:{ Exception -> 0x009d }
            goto L_0x009a
        L_0x0097:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0097 }
            throw r0     // Catch:{ Exception -> 0x009d }
        L_0x009a:
            r1 = r13
            goto L_0x0017
        L_0x009d:
            r0 = move-exception
            r1 = r13
            goto L_0x00a6
        L_0x00a0:
            long r3 = (long) r9
            long r3 = r3 + r1
            r8.lastOffset = r3     // Catch:{ Exception -> 0x00a5 }
            goto L_0x00a9
        L_0x00a5:
            r0 = move-exception
        L_0x00a6:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r11)
        L_0x00a9:
            int r0 = (int) r1
            return r0
        L_0x00ab:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00ab }
            goto L_0x00af
        L_0x00ae:
            throw r0
        L_0x00af:
            goto L_0x00ae
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
