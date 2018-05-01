package org.telegram.messenger;

import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;

public class KeepAliveJob extends JobIntentService {
    private static volatile CountDownLatch countDownLatch;
    private static Runnable finishJobByTimeoutRunnable = new C02213();
    private static volatile boolean startingJob;
    private static final Object sync = new Object();

    /* renamed from: org.telegram.messenger.KeepAliveJob$1 */
    static class C02191 implements Runnable {
        C02191() {
        }

        public void run() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r4 = this;
            r0 = org.telegram.messenger.KeepAliveJob.startingJob;
            if (r0 != 0) goto L_0x0033;
        L_0x0006:
            r0 = org.telegram.messenger.KeepAliveJob.countDownLatch;
            if (r0 == 0) goto L_0x000d;
        L_0x000c:
            goto L_0x0033;
        L_0x000d:
            r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0032 }
            if (r0 == 0) goto L_0x0016;	 Catch:{ Exception -> 0x0032 }
        L_0x0011:
            r0 = "starting keep-alive job";	 Catch:{ Exception -> 0x0032 }
            org.telegram.messenger.FileLog.m0d(r0);	 Catch:{ Exception -> 0x0032 }
        L_0x0016:
            r0 = org.telegram.messenger.KeepAliveJob.sync;	 Catch:{ Exception -> 0x0032 }
            monitor-enter(r0);	 Catch:{ Exception -> 0x0032 }
            r1 = 1;
            org.telegram.messenger.KeepAliveJob.startingJob = r1;	 Catch:{ all -> 0x002f }
            monitor-exit(r0);	 Catch:{ all -> 0x002f }
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0032 }
            r1 = org.telegram.messenger.KeepAliveJob.class;	 Catch:{ Exception -> 0x0032 }
            r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Exception -> 0x0032 }
            r3 = new android.content.Intent;	 Catch:{ Exception -> 0x0032 }
            r3.<init>();	 Catch:{ Exception -> 0x0032 }
            org.telegram.messenger.support.JobIntentService.enqueueWork(r0, r1, r2, r3);	 Catch:{ Exception -> 0x0032 }
            goto L_0x0032;
        L_0x002f:
            r1 = move-exception;
            monitor-exit(r0);	 Catch:{ all -> 0x002f }
            throw r1;	 Catch:{ Exception -> 0x0032 }
        L_0x0032:
            return;
        L_0x0033:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.KeepAliveJob.1.run():void");
        }
    }

    /* renamed from: org.telegram.messenger.KeepAliveJob$2 */
    static class C02202 implements Runnable {
        C02202() {
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
    static class C02213 implements Runnable {
        C02213() {
        }

        public void run() {
            KeepAliveJob.finishJob();
        }
    }

    public static void startJob() {
        Utilities.globalQueue.postRunnable(new C02191());
    }

    public static void finishJob() {
        Utilities.globalQueue.postRunnable(new C02202());
    }

    protected void onHandleWork(android.content.Intent r4) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r3 = this;
        r4 = sync;
        monitor-enter(r4);
        r0 = startingJob;	 Catch:{ all -> 0x0045 }
        if (r0 != 0) goto L_0x0009;	 Catch:{ all -> 0x0045 }
    L_0x0007:
        monitor-exit(r4);	 Catch:{ all -> 0x0045 }
        return;	 Catch:{ all -> 0x0045 }
    L_0x0009:
        r0 = new java.util.concurrent.CountDownLatch;	 Catch:{ all -> 0x0045 }
        r1 = 1;	 Catch:{ all -> 0x0045 }
        r0.<init>(r1);	 Catch:{ all -> 0x0045 }
        countDownLatch = r0;	 Catch:{ all -> 0x0045 }
        monitor-exit(r4);	 Catch:{ all -> 0x0045 }
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x001b;
    L_0x0016:
        r4 = "started keep-alive job";
        org.telegram.messenger.FileLog.m0d(r4);
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
        org.telegram.messenger.FileLog.m0d(r4);
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
