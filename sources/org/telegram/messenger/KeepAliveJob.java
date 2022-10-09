package org.telegram.messenger;

import android.content.Intent;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;
/* loaded from: classes.dex */
public class KeepAliveJob extends JobIntentService {
    private static volatile CountDownLatch countDownLatch;
    private static volatile boolean startingJob;
    private static final Object sync = new Object();
    private static Runnable finishJobByTimeoutRunnable = KeepAliveJob$$ExternalSyntheticLambda0.INSTANCE;

    public static void startJob() {
        Utilities.globalQueue.postRunnable(KeepAliveJob$$ExternalSyntheticLambda1.INSTANCE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$startJob$0() {
        if (startingJob || countDownLatch != null) {
            return;
        }
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("starting keep-alive job");
            }
            synchronized (sync) {
                startingJob = true;
            }
            JobIntentService.enqueueWork(ApplicationLoader.applicationContext, KeepAliveJob.class, 1000, new Intent());
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    @Override // org.telegram.messenger.support.JobIntentService
    protected void onHandleWork(Intent intent) {
        synchronized (sync) {
            if (!startingJob) {
                return;
            }
            countDownLatch = new CountDownLatch(1);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("started keep-alive job");
            }
            Utilities.globalQueue.postRunnable(finishJobByTimeoutRunnable, 60000L);
            try {
                countDownLatch.await();
            } catch (Throwable unused) {
            }
            Utilities.globalQueue.cancelRunnable(finishJobByTimeoutRunnable);
            synchronized (sync) {
                countDownLatch = null;
            }
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.d("ended keep-alive job");
        }
    }
}
