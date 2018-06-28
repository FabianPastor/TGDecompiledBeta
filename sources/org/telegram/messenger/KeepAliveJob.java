package org.telegram.messenger;

import android.content.Intent;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;

public class KeepAliveJob extends JobIntentService {
    private static volatile CountDownLatch countDownLatch;
    private static Runnable finishJobByTimeoutRunnable = new C02593();
    private static volatile boolean startingJob;
    private static final Object sync = new Object();

    /* renamed from: org.telegram.messenger.KeepAliveJob$1 */
    static class C02571 implements Runnable {
        C02571() {
        }

        public void run() {
            if (!KeepAliveJob.startingJob && KeepAliveJob.countDownLatch == null) {
                try {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("starting keep-alive job");
                    }
                    synchronized (KeepAliveJob.sync) {
                        KeepAliveJob.startingJob = true;
                    }
                    JobIntentService.enqueueWork(ApplicationLoader.applicationContext, KeepAliveJob.class, 1000, new Intent());
                } catch (Exception e) {
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.KeepAliveJob$2 */
    static class C02582 implements Runnable {
        C02582() {
        }

        public void run() {
            synchronized (KeepAliveJob.sync) {
                if (KeepAliveJob.countDownLatch != null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("finish keep-alive job");
                    }
                    KeepAliveJob.countDownLatch.countDown();
                }
                if (KeepAliveJob.startingJob) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("finish queued keep-alive job");
                    }
                    KeepAliveJob.startingJob = false;
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.KeepAliveJob$3 */
    static class C02593 implements Runnable {
        C02593() {
        }

        public void run() {
            KeepAliveJob.finishJob();
        }
    }

    public static void startJob() {
        Utilities.globalQueue.postRunnable(new C02571());
    }

    public static void finishJob() {
        Utilities.globalQueue.postRunnable(new C02582());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onHandleWork(Intent intent) {
        synchronized (sync) {
            if (startingJob) {
                countDownLatch = new CountDownLatch(1);
            } else {
                return;
            }
        }
        Utilities.globalQueue.cancelRunnable(finishJobByTimeoutRunnable);
        synchronized (sync) {
            countDownLatch = null;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("ended keep-alive job");
        }
    }
}
