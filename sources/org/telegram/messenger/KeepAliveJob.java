package org.telegram.messenger;

import android.content.Intent;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;

public class KeepAliveJob extends JobIntentService {
    private static volatile CountDownLatch countDownLatch;
    private static Runnable finishJobByTimeoutRunnable = new C02993();
    private static volatile boolean startingJob;
    private static final Object sync = new Object();

    /* renamed from: org.telegram.messenger.KeepAliveJob$1 */
    static class C02971 implements Runnable {
        C02971() {
        }

        public void run() {
            if (!KeepAliveJob.startingJob && KeepAliveJob.countDownLatch == null) {
                try {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m5d("starting keep-alive job");
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
    static class C02982 implements Runnable {
        C02982() {
        }

        public void run() {
            KeepAliveJob.finishJobInternal();
        }
    }

    /* renamed from: org.telegram.messenger.KeepAliveJob$3 */
    static class C02993 implements Runnable {
        C02993() {
        }

        public void run() {
            KeepAliveJob.finishJobInternal();
        }
    }

    public static void startJob() {
        Utilities.globalQueue.postRunnable(new C02971());
    }

    private static void finishJobInternal() {
        synchronized (sync) {
            if (countDownLatch != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m5d("finish keep-alive job");
                }
                countDownLatch.countDown();
            }
            if (startingJob) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m5d("finish queued keep-alive job");
                }
                startingJob = false;
            }
        }
    }

    public static void finishJob() {
        Utilities.globalQueue.postRunnable(new C02982());
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
            FileLog.m5d("ended keep-alive job");
        }
    }
}
