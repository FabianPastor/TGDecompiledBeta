package org.telegram.messenger;

import android.content.Intent;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;

public class KeepAliveJob extends JobIntentService {
    private static volatile CountDownLatch countDownLatch;
    private static Runnable finishJobByTimeoutRunnable = new CLASSNAME();
    private static volatile boolean startingJob;
    private static final Object sync = new Object();

    /* renamed from: org.telegram.messenger.KeepAliveJob$1 */
    static class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            if (!KeepAliveJob.startingJob && KeepAliveJob.countDownLatch == null) {
                try {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m10d("starting keep-alive job");
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
    static class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            KeepAliveJob.finishJobInternal();
        }
    }

    /* renamed from: org.telegram.messenger.KeepAliveJob$3 */
    static class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            KeepAliveJob.finishJobInternal();
        }
    }

    public static void startJob() {
        Utilities.globalQueue.postRunnable(new CLASSNAME());
    }

    private static void finishJobInternal() {
        synchronized (sync) {
            if (countDownLatch != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("finish keep-alive job");
                }
                countDownLatch.countDown();
            }
            if (startingJob) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("finish queued keep-alive job");
                }
                startingJob = false;
            }
        }
    }

    public static void finishJob() {
        Utilities.globalQueue.postRunnable(new CLASSNAME());
    }

    /* JADX WARNING: Missing block: B:9:0x0014, code:
            if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x001c;
     */
    /* JADX WARNING: Missing block: B:10:0x0016, code:
            org.telegram.messenger.FileLog.m10d("started keep-alive job");
     */
    /* JADX WARNING: Missing block: B:11:0x001c, code:
            org.telegram.messenger.Utilities.globalQueue.postRunnable(finishJobByTimeoutRunnable, com.google.android.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
     */
    /* JADX WARNING: Missing block: B:13:?, code:
            countDownLatch.await();
     */
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
            FileLog.m10d("ended keep-alive job");
        }
    }
}
