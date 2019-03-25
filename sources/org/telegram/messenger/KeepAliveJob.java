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
                    } catch (Exception e) {
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
    /* JADX WARNING: Missing block: B:9:0x0014, code skipped:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x001c;
     */
    /* JADX WARNING: Missing block: B:10:0x0016, code skipped:
            org.telegram.messenger.FileLog.d("started keep-alive job");
     */
    /* JADX WARNING: Missing block: B:11:0x001c, code skipped:
            org.telegram.messenger.Utilities.globalQueue.postRunnable(finishJobByTimeoutRunnable, 60000);
     */
    /* JADX WARNING: Missing block: B:13:?, code skipped:
            countDownLatch.await();
     */
    public void onHandleWork(android.content.Intent r5) {
        /*
        r4 = this;
        r1 = sync;
        monitor-enter(r1);
        r0 = startingJob;	 Catch:{ all -> 0x0044 }
        if (r0 != 0) goto L_0x0009;
    L_0x0007:
        monitor-exit(r1);	 Catch:{ all -> 0x0044 }
    L_0x0008:
        return;
    L_0x0009:
        r0 = new java.util.concurrent.CountDownLatch;	 Catch:{ all -> 0x0044 }
        r2 = 1;
        r0.<init>(r2);	 Catch:{ all -> 0x0044 }
        countDownLatch = r0;	 Catch:{ all -> 0x0044 }
        monitor-exit(r1);	 Catch:{ all -> 0x0044 }
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x001c;
    L_0x0016:
        r0 = "started keep-alive job";
        org.telegram.messenger.FileLog.d(r0);
    L_0x001c:
        r0 = org.telegram.messenger.Utilities.globalQueue;
        r1 = finishJobByTimeoutRunnable;
        r2 = 60000; // 0xea60 float:8.4078E-41 double:2.9644E-319;
        r0.postRunnable(r1, r2);
        r0 = countDownLatch;	 Catch:{ Throwable -> 0x004a }
        r0.await();	 Catch:{ Throwable -> 0x004a }
    L_0x002b:
        r0 = org.telegram.messenger.Utilities.globalQueue;
        r1 = finishJobByTimeoutRunnable;
        r0.cancelRunnable(r1);
        r1 = sync;
        monitor-enter(r1);
        r0 = 0;
        countDownLatch = r0;	 Catch:{ all -> 0x0047 }
        monitor-exit(r1);	 Catch:{ all -> 0x0047 }
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x0008;
    L_0x003d:
        r0 = "ended keep-alive job";
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x0008;
    L_0x0044:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0044 }
        throw r0;
    L_0x0047:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0047 }
        throw r0;
    L_0x004a:
        r0 = move-exception;
        goto L_0x002b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.KeepAliveJob.onHandleWork(android.content.Intent):void");
    }
}
