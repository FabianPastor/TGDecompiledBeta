package org.telegram.messenger;

import android.content.Intent;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;

public class KeepAliveJob extends JobIntentService {
    private static volatile CountDownLatch countDownLatch;
    private static Runnable finishJobByTimeoutRunnable = new Runnable() {
        public void run() {
            KeepAliveJob.finishJobInternal();
        }
    };
    private static volatile boolean startingJob;
    private static final Object sync = new Object();

    public static void startJob() {
        Utilities.globalQueue.postRunnable(new Runnable() {
            public void run() {
                if (!KeepAliveJob.startingJob && KeepAliveJob.countDownLatch == null) {
                    try {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("starting keep-alive job");
                        }
                        synchronized (KeepAliveJob.sync) {
                            KeepAliveJob.startingJob = true;
                        }
                        JobIntentService.enqueueWork(ApplicationLoader.applicationContext, KeepAliveJob.class, 1000, new Intent());
                    } catch (Exception unused) {
                    }
                }
            }
        });
    }

    private static void finishJobInternal() {
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

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Missing block: B:10:0x0014, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x001b;
     */
    /* JADX WARNING: Missing block: B:11:0x0016, code skipped:
            org.telegram.messenger.FileLog.d("started keep-alive job");
     */
    /* JADX WARNING: Missing block: B:12:0x001b, code skipped:
            org.telegram.messenger.Utilities.globalQueue.postRunnable(finishJobByTimeoutRunnable, 60000);
     */
    /* JADX WARNING: Missing block: B:14:?, code skipped:
            countDownLatch.await();
     */
    public void onHandleWork(android.content.Intent r4) {
        /*
        r3 = this;
        r4 = sync;
        monitor-enter(r4);
        r0 = startingJob;	 Catch:{ all -> 0x0045 }
        if (r0 != 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r4);	 Catch:{ all -> 0x0045 }
        return;
    L_0x0009:
        r0 = new java.util.concurrent.CountDownLatch;	 Catch:{ all -> 0x0045 }
        r1 = 1;
        r0.<init>(r1);	 Catch:{ all -> 0x0045 }
        countDownLatch = r0;	 Catch:{ all -> 0x0045 }
        monitor-exit(r4);	 Catch:{ all -> 0x0045 }
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x001b;
    L_0x0016:
        r4 = "started keep-alive job";
        org.telegram.messenger.FileLog.d(r4);
    L_0x001b:
        r4 = org.telegram.messenger.Utilities.globalQueue;
        r0 = finishJobByTimeoutRunnable;
        r1 = 60000; // 0xea60 float:8.4078E-41 double:2.9644E-319;
        r4.postRunnable(r0, r1);
        r4 = countDownLatch;	 Catch:{ Throwable -> 0x002a }
        r4.await();	 Catch:{ Throwable -> 0x002a }
    L_0x002a:
        r4 = org.telegram.messenger.Utilities.globalQueue;
        r0 = finishJobByTimeoutRunnable;
        r4.cancelRunnable(r0);
        r0 = sync;
        monitor-enter(r0);
        r4 = 0;
        countDownLatch = r4;	 Catch:{ all -> 0x0042 }
        monitor-exit(r0);	 Catch:{ all -> 0x0042 }
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0041;
    L_0x003c:
        r4 = "ended keep-alive job";
        org.telegram.messenger.FileLog.d(r4);
    L_0x0041:
        return;
    L_0x0042:
        r4 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x0042 }
        throw r4;
    L_0x0045:
        r0 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x0045 }
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.KeepAliveJob.onHandleWork(android.content.Intent):void");
    }
}
