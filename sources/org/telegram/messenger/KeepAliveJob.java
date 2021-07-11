package org.telegram.messenger;

import android.content.Intent;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;

public class KeepAliveJob extends JobIntentService {
    /* access modifiers changed from: private */
    public static volatile CountDownLatch countDownLatch;
    private static Runnable finishJobByTimeoutRunnable = new Runnable() {
        public void run() {
            KeepAliveJob.finishJobInternal();
        }
    };
    /* access modifiers changed from: private */
    public static volatile boolean startingJob;
    /* access modifiers changed from: private */
    public static final Object sync = new Object();

    public static void startJob() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public void run() {
                if (!KeepAliveJob.startingJob && KeepAliveJob.countDownLatch == null) {
                    try {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("starting keep-alive job");
                        }
                        synchronized (KeepAliveJob.sync) {
                            boolean unused = KeepAliveJob.startingJob = true;
                        }
                        JobIntentService.enqueueWork(ApplicationLoader.applicationContext, KeepAliveJob.class, 1000, new Intent());
                    } catch (Exception unused2) {
                    }
                }
            }
        });
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
        Utilities.globalQueue.postRunnable(new Runnable() {
            public void run() {
                KeepAliveJob.finishJobInternal();
            }
        });
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
    public void onHandleWork(android.content.Intent r4) {
        /*
            r3 = this;
            java.lang.Object r4 = sync
            monitor-enter(r4)
            boolean r0 = startingJob     // Catch:{ all -> 0x0046 }
            if (r0 != 0) goto L_0x0009
            monitor-exit(r4)     // Catch:{ all -> 0x0046 }
            return
        L_0x0009:
            java.util.concurrent.CountDownLatch r0 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x0046 }
            r1 = 1
            r0.<init>(r1)     // Catch:{ all -> 0x0046 }
            countDownLatch = r0     // Catch:{ all -> 0x0046 }
            monitor-exit(r4)     // Catch:{ all -> 0x0046 }
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r4 == 0) goto L_0x001c
            java.lang.String r4 = "started keep-alive job"
            org.telegram.messenger.FileLog.d(r4)
        L_0x001c:
            org.telegram.messenger.DispatchQueue r4 = org.telegram.messenger.Utilities.globalQueue
            java.lang.Runnable r0 = finishJobByTimeoutRunnable
            r1 = 60000(0xea60, double:2.9644E-319)
            r4.postRunnable(r0, r1)
            java.util.concurrent.CountDownLatch r4 = countDownLatch     // Catch:{ all -> 0x002b }
            r4.await()     // Catch:{ all -> 0x002b }
        L_0x002b:
            org.telegram.messenger.DispatchQueue r4 = org.telegram.messenger.Utilities.globalQueue
            java.lang.Runnable r0 = finishJobByTimeoutRunnable
            r4.cancelRunnable(r0)
            java.lang.Object r0 = sync
            monitor-enter(r0)
            r4 = 0
            countDownLatch = r4     // Catch:{ all -> 0x0043 }
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r4 == 0) goto L_0x0042
            java.lang.String r4 = "ended keep-alive job"
            org.telegram.messenger.FileLog.d(r4)
        L_0x0042:
            return
        L_0x0043:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0043 }
            throw r4
        L_0x0046:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x0046 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.KeepAliveJob.onHandleWork(android.content.Intent):void");
    }
}
