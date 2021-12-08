package org.telegram.messenger;

import android.content.Intent;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;

public class KeepAliveJob extends JobIntentService {
    private static volatile CountDownLatch countDownLatch;
    private static Runnable finishJobByTimeoutRunnable = KeepAliveJob$$ExternalSyntheticLambda0.INSTANCE;
    private static volatile boolean startingJob;
    private static final Object sync = new Object();

    public static void startJob() {
        Utilities.globalQueue.postRunnable(KeepAliveJob$$ExternalSyntheticLambda1.INSTANCE);
    }

    static /* synthetic */ void lambda$startJob$0() {
        if (!startingJob && countDownLatch == null) {
            try {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("starting keep-alive job");
                }
                synchronized (sync) {
                    startingJob = true;
                }
                enqueueWork(ApplicationLoader.applicationContext, KeepAliveJob.class, 1000, new Intent());
            } catch (Exception e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public static void finishJobInternal() {
        synchronized (sync) {
            if (countDownLatch != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("finish keep-alive job");
                }
                countDownLatch.countDown();
            }
            if (startingJob) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("finish queued keep-alive job");
                }
                startingJob = false;
            }
        }
    }

    public static void finishJob() {
        Utilities.globalQueue.postRunnable(KeepAliveJob$$ExternalSyntheticLambda0.INSTANCE);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x001c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0016, code lost:
        org.telegram.messenger.FileLog.d("started keep-alive job");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001c, code lost:
        org.telegram.messenger.Utilities.globalQueue.postRunnable(finishJobByTimeoutRunnable, 60000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        countDownLatch.await();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onHandleWork(android.content.Intent r5) {
        /*
            r4 = this;
            java.lang.Object r0 = sync
            monitor-enter(r0)
            boolean r1 = startingJob     // Catch:{ all -> 0x0048 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            return
        L_0x0009:
            java.util.concurrent.CountDownLatch r1 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x0048 }
            r2 = 1
            r1.<init>(r2)     // Catch:{ all -> 0x0048 }
            countDownLatch = r1     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x001c
            java.lang.String r0 = "started keep-alive job"
            org.telegram.messenger.FileLog.d(r0)
        L_0x001c:
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.globalQueue
            java.lang.Runnable r1 = finishJobByTimeoutRunnable
            r2 = 60000(0xea60, double:2.9644E-319)
            r0.postRunnable(r1, r2)
            java.util.concurrent.CountDownLatch r0 = countDownLatch     // Catch:{ all -> 0x002c }
            r0.await()     // Catch:{ all -> 0x002c }
            goto L_0x002d
        L_0x002c:
            r0 = move-exception
        L_0x002d:
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.globalQueue
            java.lang.Runnable r1 = finishJobByTimeoutRunnable
            r0.cancelRunnable(r1)
            java.lang.Object r1 = sync
            monitor-enter(r1)
            r0 = 0
            countDownLatch = r0     // Catch:{ all -> 0x0045 }
            monitor-exit(r1)     // Catch:{ all -> 0x0045 }
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0044
            java.lang.String r0 = "ended keep-alive job"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0044:
            return
        L_0x0045:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0045 }
            throw r0
        L_0x0048:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.KeepAliveJob.onHandleWork(android.content.Intent):void");
    }
}
