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

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:46:0x0068 in {6, 9, 18, 19, 26, 31, 36, 38, 40, 41, 45} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public int read(int r6, int r7) {
        /*
        r5 = this;
        r0 = r5.sync;
        monitor-enter(r0);
        r1 = r5.canceled;	 Catch:{ all -> 0x0065 }
        r2 = 0;	 Catch:{ all -> 0x0065 }
        if (r1 == 0) goto L_0x000a;	 Catch:{ all -> 0x0065 }
        monitor-exit(r0);	 Catch:{ all -> 0x0065 }
        return r2;	 Catch:{ all -> 0x0065 }
        monitor-exit(r0);	 Catch:{ all -> 0x0065 }
        if (r7 != 0) goto L_0x000e;
        return r2;
        r0 = 0;
        if (r0 != 0) goto L_0x005c;
        r1 = r5.loadOperation;	 Catch:{ Exception -> 0x0060 }
        r0 = r1.getDownloadedLengthFromOffset(r6, r7);	 Catch:{ Exception -> 0x0060 }
        if (r0 != 0) goto L_0x000f;	 Catch:{ Exception -> 0x0060 }
        r1 = r5.loadOperation;	 Catch:{ Exception -> 0x0060 }
        r1 = r1.isPaused();	 Catch:{ Exception -> 0x0060 }
        if (r1 != 0) goto L_0x0025;	 Catch:{ Exception -> 0x0060 }
        r1 = r5.lastOffset;	 Catch:{ Exception -> 0x0060 }
        if (r1 == r6) goto L_0x0032;	 Catch:{ Exception -> 0x0060 }
        r1 = r5.currentAccount;	 Catch:{ Exception -> 0x0060 }
        r1 = org.telegram.messenger.FileLoader.getInstance(r1);	 Catch:{ Exception -> 0x0060 }
        r3 = r5.document;	 Catch:{ Exception -> 0x0060 }
        r4 = r5.parentObject;	 Catch:{ Exception -> 0x0060 }
        r1.loadStreamFile(r5, r3, r4, r6);	 Catch:{ Exception -> 0x0060 }
        r1 = r5.sync;	 Catch:{ Exception -> 0x0060 }
        monitor-enter(r1);	 Catch:{ Exception -> 0x0060 }
        r3 = r5.canceled;	 Catch:{ all -> 0x0059 }
        if (r3 == 0) goto L_0x003b;	 Catch:{ all -> 0x0059 }
        monitor-exit(r1);	 Catch:{ all -> 0x0059 }
        return r2;	 Catch:{ all -> 0x0059 }
        r3 = new java.util.concurrent.CountDownLatch;	 Catch:{ all -> 0x0059 }
        r4 = 1;	 Catch:{ all -> 0x0059 }
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
        r6 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0059 }
        throw r6;	 Catch:{ Exception -> 0x0060 }
        r6 = r6 + r0;	 Catch:{ Exception -> 0x0060 }
        r5.lastOffset = r6;	 Catch:{ Exception -> 0x0060 }
        goto L_0x0064;
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
        return r0;
        r6 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0065 }
        throw r6;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.AnimatedFileDrawableStream.read(int, int):int");
    }

    public AnimatedFileDrawableStream(Document document, Object obj, int i) {
        this.document = document;
        this.parentObject = obj;
        this.currentAccount = i;
        this.loadOperation = FileLoader.getInstance(this.currentAccount).loadStreamFile(this, this.document, this.parentObject, 0);
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
