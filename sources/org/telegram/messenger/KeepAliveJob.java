package org.telegram.messenger;

import android.content.Intent;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;

public class KeepAliveJob extends JobIntentService {
    private static volatile CountDownLatch countDownLatch;
    private static volatile boolean startingJob;
    private static final Object sync = new Object();

    /* renamed from: org.telegram.messenger.KeepAliveJob$1 */
    static class C02171 implements Runnable {
        C02171() {
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
    static class C02182 implements Runnable {
        C02182() {
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

    public static void startJob() {
        Utilities.globalQueue.postRunnable(new C02171());
    }

    public static void finishJob() {
        Utilities.globalQueue.postRunnable(new C02182());
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
        synchronized (sync) {
            countDownLatch = null;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("ended keep-alive job");
        }
    }
}
